package com.nyc.hhs.batch.impl;

import java.util.Arrays;
import java.util.Date;
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
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.nyc.hhs.batch.IBatchQueue;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.cache.ICacheManager;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.NotificationBean;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.XMLUtil;
import com.sun.mail.smtp.SMTPTransport;

/**
 * This batch will get the alert and email notification details from
 * NOTIFICATION and GROUP_NOTIFICATION tables. It will process the list and send
 * mail to users fetched from NOTIFICATION table. For Alert notifications, it
 * will fetch the userIds corresponding to group Id and org Id and insert
 * details in USER_NOTIFICATION.
 * 
 */

public class NotificationBatch implements IBatchQueue
{
	private static final LogInfo LOG_OBJECT = new LogInfo(NotificationBatch.class);

	/**
	 * This method provides the implementation of getQueue method of interface
	 * IBatchQueue
	 * 
	 * @param a map object containing parameters
	 * @return a list of notification bean
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public List<NotificationBean> getQueue(Map aoMParameters)
	{
		return null;
	}

	/**
	 * This method provides the implementation of overridden method of interface
	 * and will execute notification batch by calling notification service
	 * 
	 * @param a list of Queue
	 */
	@Override
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void executeQueue(List aoLQueue)
	{
		try
		{
			ICacheManager loCacheManager = BaseCacheManagerWeb.getInstance();
			Object loCacheObject = XMLUtil.getDomObj(this.getClass().getResourceAsStream(
					"/" + ApplicationConstants.TRANSACTION_CONFIG));
			loCacheManager.putCacheObject(ApplicationConstants.TRANSACTION_ELEMENT, loCacheObject);

			Channel loChannelObj = new Channel();
			/**
			 * This transaction will get Group Notification details. Will get
			 * all the groupIds from the List and fetch userIds for ORgId and
			 * GropuID. Then it will update User Notification with userIDs and
			 * alert Notification details
			 */
			/*[Start] R7.4.0 QC9134 Approved Payment Rejected by FMS  */
			TransactionManager.executeTransaction(loChannelObj, "getGroupNotificationsList");
            /*[End] R7.4.0 QC9134 Approved Payment Rejected by FMS  */

			List<NotificationBean> loGroupDetailList = (List<NotificationBean>) loChannelObj
					.getData("groupNotificationList");

			for (NotificationBean loNotificationBean : loGroupDetailList)
			{
				loChannelObj.setData("groupNotificationBean", loNotificationBean);
				TransactionManager.executeTransaction(loChannelObj, "insertNotifications");
			}

		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error occurred while running email Notification batch:", aoExp);
		}
	}

	/**
	 * This method will send mail to all the users as specified in input
	 * 
	 * @param aoTo a string array of user ids
	 * @param asSubject a string value of email subject
	 * @param asMessage a string value of email body
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public static boolean sendMail(List<HashMap<String, String>> aoEmailList, HashMap<String, String> aoEmailMap,
			String[] aoTo, String asSubject, String asMessage, String asProviderName) throws ApplicationException
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
			InternetAddress loToAddress = null;
			int liCounter = 0;
			// To get the array of addresses
			while (liCounter < aoTo.length)
			{
				if (null != aoTo[liCounter] && !("".equalsIgnoreCase(aoTo[liCounter])))
				{
					//AddressException try catch added.
					try{
					loToAddress = new InternetAddress(aoTo[liCounter]);
					}catch(AddressException inetEx){
						LOG_OBJECT.Error("Address Exception at [" + aoTo[liCounter] + "] Invalid email address!");
						continue;
					}
					loMessage.addRecipient(Message.RecipientType.TO, loToAddress);
				}
				liCounter++;
			}
			StringBuffer loSbMessage = new StringBuffer();
			if (null != asProviderName && !"".equals(asProviderName))
			{
				loSbMessage.append(asProviderName);
				loSbMessage.append("<br/><br/>");
			}
			loMessage.setSubject(asSubject);
			// Add html content
			// Specify the cid of the image to include in the email
			mailHtmlAppend(asMessage, lsServerName, lsServerPort, lsContextPath, lsAppProtocol, loSbMessage);
			Multipart loMultiPart = new MimeMultipart();
			MimeBodyPart loHtmlPart = new MimeBodyPart();
			loHtmlPart.setContent(loSbMessage.toString(), "text/html; charset=utf-8");
			loMultiPart.addBodyPart(loHtmlPart);
			DataSource loFds = new FileDataSource(System.getProperty("user.dir") + lsLogo);
			MimeBodyPart loImagePart = new MimeBodyPart();
			loImagePart.setDataHandler(new DataHandler(loFds));
			// assign a cid to the image
			loImagePart.setHeader("Content-ID", "<nyc-logo-image>");
			loMultiPart.addBodyPart(loImagePart);
			loMessage.setContent(loMultiPart);
			// Send the message
			SMTPTransport loTransport = (SMTPTransport) loSession.getTransport("smtp");
			loTransport.connect(lsHost, lsUserName, lsPassword);
			loTransport.sendMessage(loMessage, loMessage.getAllRecipients());
			LOG_OBJECT.Debug("Mail Details: Subject == " + asSubject + "Send to == " + Arrays.toString(aoTo)
					+ new Date());
			loTransport.close();
			lbMailSentFlag = true;
		}
		catch (MessagingException aoExp)
		{
			LOG_OBJECT.Error("Error occurred while send mails in Email Notification Batch notificatio id :: "
					+ aoEmailMap.get("notificationId"), aoExp);
			aoEmailList.remove(aoEmailMap);
			lbMailSentFlag = false;
		}
		return lbMailSentFlag;
	}

	/**
	 * This method append mail's html part
	 * 
	 * @param asMessage
	 * @param lsServerName
	 * @param lsServerPort
	 * @param lsContextPath
	 * @param lsAppProtocol
	 * @param loSbMessage
	 */
	private static void mailHtmlAppend(String asMessage, String lsServerName, String lsServerPort,
			String lsContextPath, String lsAppProtocol, StringBuffer loSbMessage)
	{
		String lsHtml = "<html><body>";
		loSbMessage.append(lsHtml);
		loSbMessage.append(asMessage);
		loSbMessage.append("<br/><br/><p>Thank you</p><br/><p>HHS Accelerator Team</p><br/><a href=\"");
		loSbMessage.append(lsAppProtocol);
		loSbMessage.append("://");
		loSbMessage.append(lsServerName);
		loSbMessage.append(':');
		loSbMessage.append(lsServerPort);
		loSbMessage.append('/');
		loSbMessage.append(lsContextPath);
		loSbMessage.append(ApplicationConstants.PORTAL_URL);
		loSbMessage.append(ApplicationConstants.LOGIN_PAGE_LINK);
		loSbMessage
				.append("\"><img src='cid:nyc-logo-image'></a><p>HHS Accelerator is the City of New York's Web-based document storage and procurement system to simplify and speed the contract process for client and community services.</p></body></html>");
	}
	

    public static void main(String[] args) {
             (new NotificationBatch()).executeQueue(null);
    }
}
