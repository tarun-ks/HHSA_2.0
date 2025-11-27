package com.nyc.hhs.daomanager.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.RenderRequest;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.TransactionConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.ContactUsBean;
import com.nyc.hhs.model.NotificationDataBean;
import com.nyc.hhs.model.RegisterNycIdBean;
import com.nyc.hhs.model.Task;
import com.nyc.hhs.model.WorkflowDetails;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.DateUtil;

/**
 * ContactUsService: This class selects, inserts and update the contact us
 *                   information in the Contact_Us_Details in the database
 */

public class ContactUsService extends ServiceState
{
	private static final LogInfo LOG_OBJECT = new LogInfo(ContactUsService.class);

	/**
	 * This method inserts the contact us information in database.
	 * 
	 * @param loSeqNo
	 *            a sequence generated as when a new question for a topic is
	 *            added
	 * @param aoContactUsBean
	 * @param aoMybatisSession
	 * @return true or false depending whether the question is correctly
	 *         inserted in the database.
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "static-access" })
	public boolean saveAcceleratorApplicationContact(Integer aoSeqNo, ContactUsBean aoContactUsBean, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		boolean lbInsertStatus = true;
		try
		{
			aoContactUsBean.setMsSequenceID(aoSeqNo);
			DAOUtil.masterDAO(aoMybatisSession, aoContactUsBean, ApplicationConstants.MAPPER_CLASS_CONTACT_US, "insertContactUsInformation",
					"com.nyc.hhs.model.ContactUsBean");

		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("ContactUsBean", CommonUtil.convertBeanToString(aoContactUsBean));
			LOG_OBJECT.Error("Exception occured while getting data from getTopicsList in ContactUsService ", loAppEx);
			setMoState("Transaction Failed:: ContactUsService: getTopicsList method -" +
					" failed to insert record for contact_us_details with sequenceid="
					+ aoSeqNo + " \n ");
			throw loAppEx;
		}
		setMoState("Transaction Success:: ContactUsService: saveAcceleratorApplicationContact method - " +
				"insert record for contact_us_details with sequenceid="
				+ aoSeqNo + " \n ");
		return lbInsertStatus;
	}
	/** Start Rel 6.3.0 QC 8693 */
	/**
	 * 
	 * This method will send an email with details from ContactUs Task
	 * @param aoContactUsBean
	 * @param aoMybatisSession
	 * @return boolean status of email sent
	 * @throws ApplicationException
	 */
	public boolean sendContactUsEmail(ContactUsBean aoContactUsBean, SqlSession aoMybatisSession) throws ApplicationException
	{
		boolean emailSentStatus = true;
		
		Map notificationMap = new HashMap();
		
		try
		{
			sendNT414ContactUsEmail(notificationMap, aoContactUsBean);
					
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("ContactUsBean", CommonUtil.convertBeanToString(aoContactUsBean));
			LOG_OBJECT.Error("Exception occured while getting data from getTopicsList in ContactUsService ", loAppEx);
			setMoState("Transaction Failed:: ContactUsService: sendContactUsEmail for staffId - " + aoContactUsBean.getMsCreationUser() );

			throw loAppEx;
		}
		setMoState("Transaction Success:: ContactUsService: sendContactUsEmail for staffId - " + aoContactUsBean.getMsCreationUser() );
		
		return emailSentStatus;
	}
	
	
	/** This method gets StaffDetails & then calls the notification framework to send NT414 ContactUs Email */
	private void sendNT414ContactUsEmail(Map notificationMap, ContactUsBean contactUsBean) throws ApplicationException
	{
		String providerId = contactUsBean.getMsOrganisationId();
		String staffId = contactUsBean.getMsCreationUser();
		
		List<String> loNotificationAlertList = new ArrayList<String>();
		loNotificationAlertList.add("NT414");
		notificationMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
		NotificationDataBean loNotificationDataBean = new NotificationDataBean();
		List<String> loProviderList = new ArrayList<String>();
		loProviderList.add(providerId);
		loNotificationDataBean.setProviderList(loProviderList);
		notificationMap.put("NT414", loNotificationDataBean);
		
		Channel loChannel = new Channel();
		LOG_OBJECT.Debug("Entered ContactUsService.sendNT414ContactUsEmail() with parameters::" + providerId + "," + staffId);
		loChannel.setData(HHSConstants.USER_ID, staffId);
		loChannel.setData(ApplicationConstants.ORGID, providerId);
		TransactionManager.executeTransaction(loChannel, "providerdetails_db");
		List<WorkflowDetails> loProviderDetails = (List<WorkflowDetails>) loChannel.getData("resultProviderDetails");
		HashMap<Object, String> loParamMap = new HashMap<Object, String>();

		if (loProviderDetails.iterator().hasNext())
		{
			WorkflowDetails loWFDetails = loProviderDetails.iterator().next();
			
			loParamMap.put(HHSConstants.ORGANIZATION_LEGAL_NAME, loWFDetails.getMsProvidername());
			loParamMap.put(HHSConstants.EMAIL, loWFDetails.getMsEmailAddress());
			loParamMap.put(HHSConstants.USER, loWFDetails.getMsSubmittedBy());

			String lsPhoneNoToFormat = loWFDetails.getMsPhone();
			if (lsPhoneNoToFormat != null && lsPhoneNoToFormat.length() == P8Constants.PHONE_NO_LENGTH)
			{
				String lsPhoneAfterformat = lsPhoneNoToFormat.substring(0, 3) + "-" + lsPhoneNoToFormat.substring(3, 6)
						+ "-" + lsPhoneNoToFormat.substring(6, 10);
				loParamMap.put(HHSConstants.PHONE, lsPhoneAfterformat);
			}
			else if (lsPhoneNoToFormat == null)
			{
				loParamMap.put(HHSConstants.PHONE, "");
			}
			else
			{
				loParamMap.put(HHSConstants.PHONE, lsPhoneNoToFormat);
			}

			loParamMap.put(HHSConstants.PROVIDER_STATUS_KEY, loWFDetails.getMsProviderSatus());
			
			loParamMap.put(HHSConstants.DATE_CREATED, DateUtil.getCurrentDateWithTimeStamp());
			
			loParamMap.put(HHSConstants.PROVIDER_COMMENTS, contactUsBean.getMsQuestion());
			
			loParamMap.put(HHSConstants.TOPIC, contactUsBean.getMsTopic());
			
			loParamMap.put(HHSConstants.CONTACT_MEDIUM, contactUsBean.getMsContactMedium());
		}
		
		notificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loParamMap);
		loChannel.setData("loHmNotifyParam", notificationMap);
		LOG_OBJECT.Debug("Send NT414 ContactUs Email with notificationMap: " + notificationMap);
		TransactionManager.executeTransaction(loChannel, "insertNotificationDetail");
		
		LOG_OBJECT.Debug("Exit ContactUsService.sendNT414ContactUsEmail()");
	}

/** End Rel 6.3.0 QC 8693 */	
	
	
//	/** This method actually calls the notification framework to send NT414 ContactUs Email */
//	private void sendNT414ContactUsEmail(Map notificationMap, Channel loChannelObj) throws ApplicationException
//	{
//		
//		List<String> loNotificationAlertList = new ArrayList<String>();
//		loNotificationAlertList.add("NT414");
//		notificationMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
//
////		NotificationDataBean loNotificationDataBean = new NotificationDataBean();
////		HashMap<String, String> loLinkMap = new HashMap<String, String>();
////		loNotificationDataBean.setLinkMap(loLinkMap);
////		loNotificationDataBean.setAgencyLinkMap(loLinkMap);
//
//		notificationMap.put(ApplicationConstants.ENTITY_TYPE, "new_user_email");
//		notificationMap.put(ApplicationConstants.ENTITY_ID, lsEmailAddress);
//		notificationMap.put(HHSConstants.CREATED_BY_USER_ID, HHSConstants.SYSTEM_USER);
//		notificationMap.put(HHSConstants.MODIFIED_BY, HHSConstants.SYSTEM_USER);
////		notificationMap.put("NT034", loNotificationDataBean);
//		loChannelObj.setData("loHmNotifyParam", notificationMap);
//		LOG_OBJECT.Debug("Send NT414 ContactUs Email with notificationMap: " + notificationMap);
//		TransactionManager.executeTransaction(loChannelObj, "insertNotificationDetail");
//		LOG_OBJECT.Debug("notification sent ");
//	}

	/**
	/**
	 * This method retrieves topic list from the topic drop down
	 * 
	 * @param aoMybatisSession
	 * @return list of the topic to be displayed for the user to select.
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "static-access" })
	public List getTopicsList(SqlSession aoMybatisSession) throws ApplicationException
	{
		List loTopicList;
		try
		{

			loTopicList = (List) DAOUtil.masterDAO(aoMybatisSession, null, ApplicationConstants.MAPPER_CLASS_CONTACT_US, "getTopicList", null);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while getting data from getTopicsList in ContactUsService ", loAppEx);
			setMoState("Transaction Failed:: ContactUsService: getTopicsList method - failed to get all records from TOPIC \n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: ContactUsService: getTopicsList method - retrieved all records from TOPIC \n ");
		return loTopicList;
	}

	/**
	 * This method retrieves the Contact Us information
	 * 
	 * @param aoMybatisSession
	 * @param aoContactus
	 *            bean is passed containing the form data.
	 * @return a list of the all records from contact_us_details with contact us
	 *         id
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "static-access" })
	public List getContactUsIdFromDAO(SqlSession aoMybatisSession, ContactUsBean aoContactus) throws ApplicationException
	{
		List loContactList;
		try
		{

			loContactList = (List) DAOUtil.masterDAO(aoMybatisSession, aoContactus.getMsSequenceID(), ApplicationConstants.MAPPER_CLASS_CONTACT_US,
					"getContactIDList", "java.lang.Integer");
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("ContactUsBean", CommonUtil.convertBeanToString(aoContactus));
			LOG_OBJECT.Error("Exception occured while getting data from getContactUsIdFromDAO in ContactUsService ", loAppEx);
			setMoState("Transaction Failed:: ContactUsService: getContactUsIdFromDAO method - failed to retrieve all records" +
					" from contact_us_details with contactusid="
					+ aoContactus.getMsSequenceID() + " and topic id is present in TOPIC and contact_us_details \n ");
			throw loAppEx;
		}
		setMoState("Transaction Success:: ContactUsService: getContactUsIdFromDAO method - retrieved all records from " +
				"contact_us_details with contactusid="
				+ aoContactus.getMsSequenceID() + " and topic id is present in TOPIC and contact_us_details \n ");
		return loContactList;
	}

	/**
	 * This method returns the sequence ID for the insertion in database.
	 * 
	 * @param aoMybatisSession
	 * @return the next sequence generated in the database when a new record is
	 *         inserted.
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "static-access" })
	public Integer getSequenceFromTable(SqlSession aoMybatisSession) throws ApplicationException
	{
		Integer lbSequence;
		try
		{
			lbSequence = (Integer) DAOUtil.masterDAO(aoMybatisSession, null, ApplicationConstants.MAPPER_CLASS_CONTACT_US, "getSequenceFromTable",
					null);

		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while getting data from getSequenceFromTable in ContactUsService ", loAppEx);
			setMoState("Transaction Failed:: ContactUsService: getSequenceFromTable method " +
					"- failed retrieve the next value SEQ_CONTACT_US_TOPIC_ID.nextval from dual \n ");
			throw loAppEx;
		}
		setMoState("Transaction Success:: ContactUsService: getSequenceFromTable method -" +
				" retrieved the next value SEQ_CONTACT_US_TOPIC_ID.nextval from dual \n ");
		return lbSequence;
	}
}
