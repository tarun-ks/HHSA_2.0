package com.nyc.hhs.batch.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.jdom.Document;

import com.nyc.hhs.batch.IBatchQueue;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.cache.ICacheManager;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.ProposalReportBean;
import com.nyc.hhs.service.db.services.notification.ProposalReportOnDueDateService;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.XMLUtil;
import com.sun.mail.smtp.SMTPTransport;

/**
 * This batch id to create Proposal report by Agency at due date and provide on
 * the email.
 * 
 */

public class ProposalReportOnDueDateBatch implements IBatchQueue
{
	protected static final LogInfo LOG_OBJECT = new LogInfo(ProposalReportOnDueDateBatch.class);

	protected static final String REPORT_HEADER_ID = "5602_REPORT_HEADER";
	protected static final String REPORT_FILEPATH = "REPORT_FILEPATH";
	protected static final String REPORT_FILE_NAME = "5602_REPORT_FILE_NAME";
	protected static final String REPORT_SUBJECT = "5602_REPORT_SUBJECT";
	protected static final String REPORT_SUBJECT_TITLE_LOCATION = "__PROCUREMENT_TITLE__";
	protected static final String REPORT_FILE_NAME_LOCATION = "__AGENCY_ID__";
	protected static final String REPORT_BODY_MESSAGE = "5602_REPORT_EMAIL_BODY_MESSAGE";
	protected static final String REPORT_PROPERTY_FILE = "com.nyc.hhs.properties.reportheader";

	/**
	 * This method provides the implementation of getQueue method of interface
	 * IBatchQueue
	 * 
	 * @param a map object containing parameters
	 * @return a list of notification bean
	 */
	@Override
	public List getQueue(Map aoMParameters)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * This method provides the implementation of overridden method of interface
	 * and will execute notification batch by calling notification service
	 * 
	 * @param a list of Queue
	 */
	@Override
	public void executeQueue(List aoLQueue)
	{
		try
		{
			ICacheManager loCacheManager = BaseCacheManagerWeb.getInstance();
			Document loCacheObject = XMLUtil.getDomObj(this.getClass().getResourceAsStream(
					ApplicationConstants.BATCH_TRANSACTION_CONFIG));

			loCacheManager.putCacheObject(ApplicationConstants.TRANSACTION_ELEMENT, loCacheObject);

			Channel loChannelObj = new Channel();
			/**
			 * This transaction will get Notification details. Will get all the
			 * groupIds from the List and fetch userIds for ORgId and GropuID.
			 * Then it will update User Notification with userIDs and alert
			 * Notification details
			 */
			TransactionManager.executeTransaction(loChannelObj,
					ProposalReportOnDueDateService.PROPOSAL_DUE_DATE_REPORT_METHOD);

			@SuppressWarnings("unchecked")
			List<ProposalReportBean> loOnDetailList = (List<ProposalReportBean>) loChannelObj
					.getData(ProposalReportOnDueDateService.PROPOSAL_DUE_DATE_REPORT_RESULT);

			// TransactionManager.executeTransaction(loChannelObj,
			// ProposalReportOnDueDateService.METHOD_REPORT_RECIPIENTS_LIST);
			@SuppressWarnings("unchecked")
			List<String> loRecipientsList = (List<String>) loChannelObj
					.getData(ProposalReportOnDueDateService.RESULT_REPORT_RECIPIENTS_LIST);

			String loEmailBodyMessage = PropertyLoader.getProperty(REPORT_PROPERTY_FILE, REPORT_BODY_MESSAGE);

			if (loOnDetailList != null && !loOnDetailList.isEmpty())
			{
				Map<String, List<ProposalReportBean>> loMap = reorganizeReportByProcurement(loOnDetailList);
				for (String key : loMap.keySet())
				{
					sendEmailWithReport(loMap.get(key), loRecipientsList, loEmailBodyMessage);

					// to give mail server a break
					Thread.sleep(1000);
				}
			}
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error occurred while running email Notification batch:", aoExp);
		}
		catch (InterruptedException aoExp)
		{
			LOG_OBJECT.Error("Error occurred while running email Notification batch:", aoExp);
		}

	}

	/**
	 * This method will instantiate ProposalReportOnDueDateBatch
	 * This method will execute proposal Batch
	 * @param args
	 */
	public static void main(String[] args)
	{
		(new ProposalReportOnDueDateBatch()).executeQueue(null);
	}

	/**
	 * This method will reorganize list from DB put into a Map of list by Agency
	 * 
	 * @param aoTo a string array of user ids
	 * @param asSubject a string value of email subject
	 * @param asMessage a string value of email body
	 * @throws ApplicationException If an Application Exception occurs
	 */
	protected Map<String, List<ProposalReportBean>> reorganizeReportByAgency(List<ProposalReportBean> aoProposalList)
	{

		Map<String, List<ProposalReportBean>> propoRep = new HashMap<String, List<ProposalReportBean>>();

		for (ProposalReportBean repVo : aoProposalList)
		{
			if (propoRep.containsKey(repVo.getAgencyId()))
			{
				propoRep.get(repVo.getAgencyId()).add(repVo);
			}
			else
			{
				propoRep.put(repVo.getAgencyId(), new ArrayList<ProposalReportBean>());
				propoRep.get(repVo.getAgencyId()).add(repVo);
			}
		}

		return propoRep;
	}

	/**
	 * This method will reorganize list from DB put into a Map of list by
	 * Procurement
	 * 
	 * @param aoTo a string array of user ids
	 * @param asSubject a string value of email subject
	 * @param asMessage a string value of email body
	 * @throws ApplicationException If an Application Exception occurs
	 */
	protected Map<String, List<ProposalReportBean>> reorganizeReportByProcurement(
			List<ProposalReportBean> aoProposalList)
	{

		Map<String, List<ProposalReportBean>> propoRep = new HashMap<String, List<ProposalReportBean>>();

		for (ProposalReportBean repVo : aoProposalList)
		{
			if (propoRep.containsKey(repVo.getProcurementTitle()))
			{
				propoRep.get(repVo.getProcurementTitle()).add(repVo);
			}
			else
			{
				propoRep.put(repVo.getProcurementTitle(), new ArrayList<ProposalReportBean>());
				propoRep.get(repVo.getProcurementTitle()).add(repVo);
			}
		}

		return propoRep;
	}

	/**
	 * This method will send mail to all the users as specified in input after
	 * attaching report.
	 * 
	 * 
	 * @param aoTo a string array of user ids
	 * @param asSubject a string value of email subject
	 * @param asMessage a string value of email body
	 * @throws ApplicationException If an Application Exception occurs
	 */
	protected static void sendEmailWithReport(List<ProposalReportBean> aoLst, List<String> aoReciList,
			String loEmailBodyMessage) throws ApplicationException
	{
		if (aoLst == null || aoLst.isEmpty())
			return;

		String loEmailSubject = PropertyLoader.getProperty(REPORT_PROPERTY_FILE, REPORT_SUBJECT).replace(
				REPORT_SUBJECT_TITLE_LOCATION, aoLst.get(0).getProcurementTitle());

		sendMail(aoLst, aoReciList, loEmailSubject, loEmailBodyMessage);
	}

	/**
	 * This method will send mail to all the users as specified in input
	 * 
	 * @param aoTo a string array of user ids
	 * @param asSubject a string value of email subject
	 * @param asMessage a string value of email body
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public static boolean sendMail(List<ProposalReportBean> aoLst, List<String> aoReciList, String asSubject,
			String asMessage) throws ApplicationException
	{
		boolean lbMailSentFlag = false;
		String lsFrom = PropertyLoader.getProperty("com.nyc.hhs.properties.email", "FROM");
		String lsHost = PropertyLoader.getProperty("com.nyc.hhs.properties.email", "SMTP_HOST_NAME");
		String lsSmtpPort = PropertyLoader.getProperty("com.nyc.hhs.properties.email", "SMTP_PORT");
		String lsUserName = PropertyLoader.getProperty("com.nyc.hhs.properties.email", "USER_NAME");
		String lsPassword = PropertyLoader.getProperty("com.nyc.hhs.properties.email", "PASSWORD");
		String lsBCCAddress = PropertyLoader.getProperty("com.nyc.hhs.properties.email", "TO_BCC_ADDRESS");
		String lsIncludeBCC = PropertyLoader.getProperty("com.nyc.hhs.properties.email", "INCLUDE_BCC");
		String lsServerName = PropertyLoader.getProperty("com.nyc.hhs.properties.hhsservices",
				"SERVER_NAME_FOR_PROVIDER_BATCH");
		String lsServerPort = PropertyLoader.getProperty("com.nyc.hhs.properties.hhsservices",
				"SERVER_PORT_FOR_PROVIDER_BATCH");
		String lsContextPath = PropertyLoader.getProperty("com.nyc.hhs.properties.hhsservices",
				"CONTEXT_PATH_FOR_PROVIDER_BATCH");
		String lsAppProtocol = PropertyLoader.getProperty("com.nyc.hhs.properties.hhsservices",
				"SERVER_PROTOCOL_FOR_PROVIDER_BATCH");
		String lsLogo = PropertyLoader.getProperty("com.nyc.hhs.properties.email", "NYC_LOGO");
		Properties loProps = System.getProperties();
		loProps.put("mail.smtp.host", lsHost);
		loProps.put("mail.smtp.user", lsFrom);
		loProps.put("mail.smtp.port", lsSmtpPort);
		loProps.put("mail.smtp.auth", "true");
		try
		{
			Session loSession = Session.getDefaultInstance(loProps, null);
			// Create message
			MimeMessage loMessage = new MimeMessage(loSession);
			// add address
			loMessage.setFrom(new InternetAddress(lsFrom));
			if ("yes".equalsIgnoreCase(lsIncludeBCC))
			{
				loMessage.addRecipients(Message.RecipientType.BCC, lsBCCAddress);
			}
			// To get the array of addresses
			for (String rec : aoReciList)
			{
				loMessage.addRecipients(Message.RecipientType.TO, rec);
			}

			loMessage.setSubject(asSubject);
			// Add html content
			// Specify the cid of the image to include in the email
			// mailHtmlAppend(asMessage, lsServerName, lsServerPort,
			// lsContextPath, lsAppProtocol, loSbMessage);
			Multipart loMultiPart = new MimeMultipart();
			MimeBodyPart loHtmlPart = new MimeBodyPart();
			loHtmlPart.setContent(asMessage, "text/html; charset=utf-8");
			loMultiPart.addBodyPart(loHtmlPart);

			// Attach report into email
			MimeBodyPart loReportPart = new MimeBodyPart();
			File f = saveFile(aoLst);
			DataSource source = new FileDataSource(f.getAbsolutePath());
			loReportPart.setDataHandler(new DataHandler(source));
			loReportPart.setFileName(f.getName());

			loMultiPart.addBodyPart(loReportPart);

			loMessage.setContent(loMultiPart);
			// Send the message
			SMTPTransport loTransport = (SMTPTransport) loSession.getTransport("smtp");
			loTransport.connect(lsHost, lsUserName, lsPassword);
			loTransport.sendMessage(loMessage, loMessage.getAllRecipients());
			loTransport.close();
			lbMailSentFlag = true;

			f.delete();
		}
		catch (MessagingException aoExp)
		{
			LOG_OBJECT.Error("Error occurred while send mails in Email Notification Batch notificatio id :: "
					+ lsBCCAddress, aoExp);
			lbMailSentFlag = false;
		}
		return lbMailSentFlag;
	}
/**
 * This method will execute for saving report for attachment
 * @param aoLst
 * @return
 * @throws ApplicationException
 */
	protected static File saveFile(List<ProposalReportBean> aoLst) throws ApplicationException
	{
		String fileFolder = PropertyLoader.getProperty("com.nyc.hhs.properties.hhsservices", REPORT_FILEPATH);
		String fileName = getFileName().replaceAll(REPORT_FILE_NAME_LOCATION, aoLst.get(0).getAgencyId());
		String loReportHeader = PropertyLoader.getProperty("com.nyc.hhs.properties.reportheader", REPORT_HEADER_ID);
		File f = new File(fileFolder, fileName);
		if (f.exists())
		{
			f.delete();
		}
		try
		{
			f.createNewFile();
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(f));

			// Writing Header
			bWriter.write(loReportHeader);
			bWriter.newLine();
			for (ProposalReportBean vo : aoLst)
			{
				bWriter.write(vo.toCommarDelimertedString());
				bWriter.newLine();
			}
			bWriter.flush();
			bWriter.close();
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occurred while saving report for attachment ", aoEx);
			throw new ApplicationException("Error occurred while saving report for attachment ", aoEx);
		}
		return f;
	}
/**
 * This method will get the filename
 * Filename: 5602_REPORT_FILE_NAME.csv
 * @return
 * @throws ApplicationException
 */
	protected static String getFileName() throws ApplicationException
	{
		String reportName = PropertyLoader.getProperty("com.nyc.hhs.properties.reportheader", REPORT_FILE_NAME);

		Calendar loCalendar = Calendar.getInstance();

		return reportName + DateUtil.getDateMMddYYYYHHMMSSFormat(loCalendar.getTime()) + ".csv";
	}

}
