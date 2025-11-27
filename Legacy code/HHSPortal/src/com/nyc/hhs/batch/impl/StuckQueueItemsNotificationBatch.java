package com.nyc.hhs.batch.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.nyc.hhs.batch.IBatchQueue;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.QueueItemsDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;
import com.sun.mail.smtp.SMTPTransport;

public class StuckQueueItemsNotificationBatch implements IBatchQueue
{
	private static final LogInfo LOG_OBJECT = new LogInfo(StuckQueueItemsNotificationBatch.class);

	/**
	 * This method is the entry point for the execution of the batch. it will
	 * start execution of the batch. Changes done for enhancement 6508 for
	 * Release 3.6.0
	 */
	@SuppressWarnings("unchecked")
	public void executeQueue(List aoLQueue) throws ApplicationException
	{
		List<QueueItemsDetailsBean> loTimeConfigMap = null;
		List<QueueItemsDetailsBean> loStuckItemsDetails = new ArrayList<QueueItemsDetailsBean>();
		QueueItemsDetailsBean loQueueItemsDetailsBean = null;
		try
		{
			loTimeConfigMap = getTimeConfigForQueueItems();
			P8UserSession loUserSession = setP8SessionVariables();
			for (Iterator loConfigItr = loTimeConfigMap.iterator(); loConfigItr.hasNext();)
			{
				loQueueItemsDetailsBean = (QueueItemsDetailsBean) loConfigItr.next();
				loQueueItemsDetailsBean = getQueueWorkItems(loUserSession,
						HHSConstants.QUEUE_NAME_MAP.get(loQueueItemsDetailsBean.getQueueType() + "_NAME"),
						loQueueItemsDetailsBean.getStuckSince(), loQueueItemsDetailsBean);
				if (loQueueItemsDetailsBean.getStuckItemDetails().size() > 0)
				{
					loQueueItemsDetailsBean.setQueueType(loQueueItemsDetailsBean.getQueueType());
					loStuckItemsDetails.add(loQueueItemsDetailsBean);
				}

			}
			// Start || Changes done for enhancement 6508 for Release 3.6.0
			Channel loChannel = new Channel();
			Map<String, String> lsInputParam = new HashMap<String, String>();
			lsInputParam.put(HHSConstants.COMPONENT_NAME, HHSConstants.MNTNC_QUEUE_SENDFLAG);
			lsInputParam.put(HHSConstants.CONTENT_NAME, HHSConstants.MNTNC_QUEUE_SENDFLAG);
			loChannel.setData(HHSConstants.INPUT_PARAM_MAP, lsInputParam);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.GET_APP_SETTINGS_MAIN_QUEUE);
			String losettingsVal = (String) loChannel.getData(HHSConstants.SETTINGS_VAL);

			if (!loStuckItemsDetails.isEmpty())
			{
				if (!sendMail(loStuckItemsDetails))
				{
					LOG_OBJECT.Debug("**************** Problem Occured while sending Email ******************");
				}
			}
			else if (null != losettingsVal && losettingsVal.equalsIgnoreCase(HHSConstants.TRUE))
			{
				if (!sendMail(loStuckItemsDetails))
				{
					LOG_OBJECT.Debug("**************** Problem Occured while sending Email ******************");
				}
			}
			// End || Changes done for enhancement 6508 for Release 3.6.0
			else
			{
				LOG_OBJECT.Debug("**************** No issue found in Component & Maintenance Queue******************");
			}
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in StuckQueueItemsNitificationBatch.executeQueue()", aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception in StuckQueueItemsNitificationBatch.executeQueue()", aoExp);
			throw new ApplicationException("Exception in StuckQueueItemsNitificationBatch.executeQueue()", aoExp);
		}
	}

	/**
	 * This method is used to fetch the work items present in the mentioned
	 * Queue since the time period mentioned in the argument
	 * 
	 * @param aoUserSession P8UserSession Bean Object
	 * @param asQueueName Queue name
	 * @param aiStuckTime Time since the items stucked in the queue
	 * @param aoQueueItemsDetailsBean Stuck Queue items details
	 * @return Details of the work items present in the queue
	 * @throws ApplicationException
	 */
	private QueueItemsDetailsBean getQueueWorkItems(P8UserSession aoUserSession, String asQueueName, int aiStuckTime,
			QueueItemsDetailsBean aoQueueItemsDetailsBean) throws ApplicationException
	{

		// int liQueueItemCount = 0;
		HashMap<String, Integer> loHmWorkItemsDetails = new HashMap<String, Integer>();
		Channel loChannelObj = new Channel();
		try
		{
			// Call the data-provider class
			loChannelObj.setData(HHSConstants.QUEUE_NAME, asQueueName);
			loChannelObj.setData(HHSConstants.STUCK_TIME_PERIOD, aiStuckTime);
			loChannelObj.setData(HHSConstants.AO_FILENET_SESSION, aoUserSession);
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.GET_WORKITEMS_FROM_QUEUE);
			loHmWorkItemsDetails = (HashMap) loChannelObj.getData(HHSConstants.NUMBER_OF_WORKITEMS_IN_QUEUE);
			if (loHmWorkItemsDetails.size() > 0)
			{
				aoQueueItemsDetailsBean.setQueueName(asQueueName);
				aoQueueItemsDetailsBean.setStuckItemDetails(loHmWorkItemsDetails);
				aoQueueItemsDetailsBean.setStuckSince(aiStuckTime);
			}
		}
		/**
		 * catch the application exception thrown from the service layer log it
		 * into the console
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while fetching QueWorkItem from Queue" + asQueueName, aoAppEx);
			throw aoAppEx;
		}
		/**
		 * catch any exception thrown from the code and wrap it into application
		 * exception and propagate forward
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetching QueWorkItem from Queue" + asQueueName, aoExp);
			throw new ApplicationException("Error while fetching QueWorkItem from Queue" + asQueueName, aoExp);
		}
		return aoQueueItemsDetailsBean;
	}

	/**
	 * This method will read the HHS property file and set the required
	 * parameters to establish connection with PE server
	 * 
	 * @return P8UserSession P8UserSession bean object
	 * @throws Exception
	 */
	public static P8UserSession setP8SessionVariables() throws Exception
	{
		P8UserSession loUserSession = new P8UserSession();
		loUserSession.setContentEngineUri(PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
				"FILENET_URI"));
		loUserSession.setObjectStoreName(PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
				"OBJECT_STORE_NAME"));
		loUserSession.setIsolatedRegionName(PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
				"CONNECTION_POINT_NAME"));
		loUserSession.setIsolatedRegionNumber(PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
				"CONNECTION_POINT_NUMBER"));
		loUserSession.setUserId(HHSUtil.decryptASEString(PropertyLoader.getProperty(
				HHSConstants.HHS_SERVICE_PROPERTIES_PATH, "CE_USER_ID")));
		loUserSession.setPassword(HHSUtil.decryptASEString(PropertyLoader.getProperty(
				HHSConstants.HHS_SERVICE_PROPERTIES_PATH, "CE_PASSWORD")));
		return loUserSession;
	}

	/**
	 * This method will fetch the delay configuration to check the queue items
	 * 
	 * @return list of queue details configured in Database
	 * @throws ApplicationException
	 */
	private List<QueueItemsDetailsBean> getTimeConfigForQueueItems() throws ApplicationException
	{
		List<QueueItemsDetailsBean> loTimeConfigMap = new ArrayList<QueueItemsDetailsBean>();
		Channel loChannelObj = new Channel();
		try
		{
			// Call the data-provider class
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.FETCH_QUEUE_ITEM_DELAY_CONFIG);
			loTimeConfigMap = (List<QueueItemsDetailsBean>) loChannelObj.getData(HHSConstants.QUEUE_ITEM_DELAY_CONFIG);
		}
		/**
		 * catch the application exception thrown from the service layer log it
		 * into the console
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured while fetching Queue Item Delay Configuration from DB", aoAppEx);
			throw aoAppEx;
		}
		/**
		 * catch any exception thrown from the code and wrap it into application
		 * exception and propagate forward
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error occured while fetching Queue Item Delay Configuration from DB", aoExp);
			throw new ApplicationException("Error occured while fetching Queue Item Delay Configuration from DB", aoExp);
		}
		return loTimeConfigMap;
	}

	/**
	 * This method will read the email configuration file and sent the email
	 * notification regarding the stuck items in the Queue Changes done for
	 * enhancement 6508 for Release 3.6.0
	 * @param aoStuckItemsDetailsBeans list of stuck items details
	 * @return boolean true if email sent success
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	private static boolean sendMail(List<QueueItemsDetailsBean> aoStuckItemsDetailsBeans) throws ApplicationException
	{
		String lsEnvironment = System.getProperty("hhs.env");
		boolean lbMailSentFlag = false;
		Properties loProps = System.getProperties();
		List<String> loToAddressList = new ArrayList<String>();
		try
		{
			String lsFrom = PropertyLoader.getProperty(HHSConstants.HHS_EMAIL_PROPERTIES, "FROM");
			String lsHost = PropertyLoader.getProperty(HHSConstants.HHS_EMAIL_PROPERTIES, "SMTP_HOST_NAME");
			String lsSmtpPort = PropertyLoader.getProperty(HHSConstants.HHS_EMAIL_PROPERTIES, "SMTP_PORT");
			String lsUserName = PropertyLoader.getProperty(HHSConstants.HHS_EMAIL_PROPERTIES, "USER_NAME");
			String lsPassword = PropertyLoader.getProperty(HHSConstants.HHS_EMAIL_PROPERTIES, "PASSWORD");
			String lsSubject = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
					"STUCK_QUEUE_ITEMS_SUBJECT");
			String lsMessage = "";
			/*
			 * String lsMessage =
			 * PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH
			 * , "STUCK_QUEUE_ITEMS_MESSAGE");
			 */
			// String toAddresses =
			// PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
			// "STUCK_QUEUE_ITEMS_TO_ADDRESS");
			// Start || Changes done for enhancement 6508 for Release 3.6.0
			Channel loChannel = new Channel();
			Map<String, String> lsInputParam = new HashMap<String, String>();
			lsInputParam.put(HHSConstants.COMPONENT_NAME, HHSConstants.MNTNC_QUEUE_EMAILIDS);
			lsInputParam.put(HHSConstants.CONTENT_NAME, HHSConstants.MNTNC_QUEUE_EMAILIDS);
			loChannel.setData(HHSConstants.INPUT_PARAM_MAP, lsInputParam);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.GET_APP_SETTINGS_MAIN_QUEUE);
			String toAddresses = (String) loChannel.getData(HHSConstants.SETTINGS_VAL);
			// End || Changes done for enhancement 6508 for Release 3.6.0
			String lsFormat = "&#09";
			loProps.put("mail.smtp.host", lsHost);
			loProps.put("mail.smtp.user", lsFrom);
			loProps.put("mail.smtp.port", lsSmtpPort);
			loProps.put("mail.smtp.auth", "true");
			if (lsEnvironment.indexOf("_") > 0)
			{
				String lsEnvArray[] = lsEnvironment.split("_");
				lsEnvironment = lsEnvArray[0];
				if (lsEnvironment.indexOf("-") > 0)
				{
					String lsEnvNameArray[] = lsEnvironment.split("-");
					lsEnvironment = lsEnvNameArray[0];
				}
			}
			Session loSession = Session.getDefaultInstance(loProps, null);
			// Create message
			MimeMessage loMessage = new MimeMessage(loSession);
			// add address
			loMessage.setFrom(new InternetAddress(lsFrom));
			InternetAddress loToAddress = null;
			if (null != toAddresses && toAddresses.contains(";"))
			{
				String toAddrs[] = toAddresses.split(";");
				loToAddressList = Arrays.asList(toAddrs);
			}
			else
			{
				loToAddressList.add(toAddresses);
			}

			for (Iterator<String> loAddressItr = loToAddressList.iterator(); loAddressItr.hasNext();)
			{
				loToAddress = new InternetAddress(loAddressItr.next());
				loMessage.addRecipient(Message.RecipientType.TO, loToAddress);
			}
			LOG_OBJECT.Debug("To Address List==" + loToAddress);
			lsSubject = lsSubject + " in " + lsEnvironment + " !!!";
			loMessage.setSubject(lsSubject);
			StringBuffer loSbMessage = new StringBuffer();

			if (null != aoStuckItemsDetailsBeans && !aoStuckItemsDetailsBeans.isEmpty())
			{

				loSbMessage.append("<html><body>").append(lsMessage);
				for (Iterator<QueueItemsDetailsBean> loQueueItemItr = aoStuckItemsDetailsBeans.iterator(); loQueueItemItr
						.hasNext();)
				{
					QueueItemsDetailsBean queueItemsDetailsBean = loQueueItemItr.next();
					if (queueItemsDetailsBean.getQueueType().contains(HHSConstants.COMPONENT_QUEUE))
					{
						if (queueItemsDetailsBean.getStuckItemDetails().size() > 0)
						{

							HashMap<String, Integer> loDetails = queueItemsDetailsBean.getStuckItemDetails();
							loSbMessage
									.append("<br/><br/><p> Please find the details of stuck tasks in Component queue:</p> ");
							loSbMessage.append("<table><tr><td><b><u>TaskType</b></u>").append(lsFormat)
									.append("</td><td><b><u>Task Count</u></b></td></tr>");
							for (Entry<String, Integer> loEntry : loDetails.entrySet())
							{

								loSbMessage.append("<tr><td>").append(loEntry.getKey()).append("</td>")
										.append(lsFormat).append("<td>").append(loEntry.getValue())
										.append("</td></tr>");
							}
							loSbMessage.append("</table>");
						}
						else
						{
							loSbMessage.append("<br/><br/><p> There are no stuck items in Component queue.</p> ");
						}
					}
					if (queueItemsDetailsBean.getQueueType().contains(HHSConstants.MAINTENANCE_QUEUE))
					{
						if (queueItemsDetailsBean.getStuckItemDetails().size() > 0)
						{

							HashMap<String, Integer> loDetails = queueItemsDetailsBean.getStuckItemDetails();
							loSbMessage
									.append("<br/><br/><p> Please find the details of stuck tasks in Maintenance queue:</p> ");
							loSbMessage.append("<table><tr><td><b><u>TaskType</b></u>").append(lsFormat)
									.append("</td><td><b><u>Task Count</u></b></td></tr>");
							for (Entry<String, Integer> loEntry : loDetails.entrySet())
							{

								loSbMessage.append("<tr><td>").append(loEntry.getKey()).append("</td>")
										.append(lsFormat).append("<td>").append(loEntry.getValue())
										.append("</td></tr>");
							}
							loSbMessage.append("</table>");
						}
						else
						{
							loSbMessage.append("<br/><br/><p> There are no stuck items in Maintenance queue</p> ");
						}
					}
				}
			}
			else
			{
				loSbMessage.append("<br/><br/><p> There are no stuck items in Maintenance and Component queue.</p> ");
			}
			loSbMessage.append("<br/><br/><p>Thank you</p><br/>HHS Support Team");
			loSbMessage.append("</body></html>");
			LOG_OBJECT.Debug("Message Prepared ********* " + loSbMessage);
			Multipart loMultiPart = new MimeMultipart();
			MimeBodyPart loHtmlPart = new MimeBodyPart();
			loHtmlPart.setContent(loSbMessage.toString(), "text/html; charset=utf-8");
			loMultiPart.addBodyPart(loHtmlPart);
			loMessage.setContent(loMultiPart);
			SMTPTransport loTransport = (SMTPTransport) loSession.getTransport("smtp");
			loTransport.connect(lsHost, lsUserName, lsPassword);
			loTransport.sendMessage(loMessage, loMessage.getAllRecipients());
			loTransport.close();
			lbMailSentFlag = true;
		}
		catch (MessagingException aoExp)
		{
			lbMailSentFlag = false;
			LOG_OBJECT.Error("Error while sending Notification Mail for Queue items count", aoExp);
			throw new ApplicationException("Error while sending Notification Mail for Queue items count", aoExp);
		}
		catch (Exception aoExp)
		{
			lbMailSentFlag = false;
			LOG_OBJECT.Error("Error while sending Notification Mail for Queue items count", aoExp);
			throw new ApplicationException("Error while sending Notification Mail for Queue items count", aoExp);
		}
		return lbMailSentFlag;
	}

	/**
	 * This Method will return null List
	 * aoMParameters Map
	 */
	@Override
	public List getQueue(Map aoMParameters)
	{
		return null;
	}

}
