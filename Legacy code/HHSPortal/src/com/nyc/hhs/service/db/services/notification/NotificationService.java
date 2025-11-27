package com.nyc.hhs.service.db.services.notification;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;

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

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSP8Constants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.constants.TransactionConstants;
import com.nyc.hhs.daomanager.service.ServiceState;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.BudgetList;
import com.nyc.hhs.model.BulkNotificationList;
import com.nyc.hhs.model.NotificationAlertMasterBean;
import com.nyc.hhs.model.NotificationBean;
import com.nyc.hhs.model.NotificationDataBean;
import com.nyc.hhs.model.NotificationParamBean;
import com.nyc.hhs.model.NotificationURLBean;
import com.nyc.hhs.model.ReturnedPayment;
import com.nyc.hhs.model.UserEmailIdBean;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.FileNetOperationsUtils;
import com.nyc.hhs.util.PropertyLoader;
import com.sun.mail.smtp.SMTPTransport;

/**
 * This class implements the functionality of sending mails and alerts to the
 * designated users. This class updated in r6 for return payment.
 * 
 */
public class NotificationService extends ServiceState
{
	private static final LogInfo LOG_OBJECT = new LogInfo(NotificationService.class);

	/**
	 * Method to process the subject and message Includes reading template from
	 * xml and replacing with key value data
	 * 
	 * @param aoNotifyParam a map containing notification information
	 * @return a boolean value for notification processed status
	 * @throws ApplicationException If an application exception occurred
	 */
	public Boolean processNotification(SqlSession aoMyBatisSession, HashMap<String, Object> aoHMNotifyParam)
			throws ApplicationException
	{
		return processNotification(aoMyBatisSession, aoHMNotifyParam, true);
	}

	/**
	 * Method to process the subject and message Includes reading template from
	 * xml and replacing with key value data
	 * 
	 * @param aoNotifyParam a map containing notification information
	 * @return a boolean value for notification processed status
	 * @throws ApplicationException If an application exception occurred
	 */
	public Boolean processSMNotification(SqlSession aoMyBatisSession, HashMap<String, Object> aoHMNotifyParam)
			throws ApplicationException
	{
		HashMap<String, Object> loNotificationMap = (HashMap<String, Object>) aoHMNotifyParam
				.get(ApplicationConstants.LO_HM_NOTIFY_PARAM);
		return processNotification(aoMyBatisSession, loNotificationMap, true);
	}

	/**
	 * @param aoMyBatisSession SQL mybatis session
	 * @param aoHMNotifyParam Notification Parma Map
	 * @param asCount
	 * @return true if data is properly inserted into Notification Table
	 * @throws ApplicationException
	 */
	public Boolean processNotificationForBatch(SqlSession aoMyBatisSession, HashMap<String, Object> aoHMNotifyParam,
			String asCount) throws ApplicationException
	{		
		if (null != asCount && Integer.valueOf(asCount) == 0)
		{
			return processNotification(aoMyBatisSession, aoHMNotifyParam, true);
		}
		else if (null != asCount && Integer.valueOf(asCount) > 0){
			return processNotification(aoMyBatisSession, aoHMNotifyParam, true);
		}
		else
		{
			return false;
		}
		
	}

	/**
	 * Updated Method in R4. This method is used to update the notification
	 * status to over ride for the procurement user is publishing
	 * @param aoMyBatisSession sql session
	 * @param asProcurementId procurement id
	 * @return boolean if updated success fully
	 * @throws ApplicationException if any exception occurs
	 */
	public Boolean updateNotificationStatus(SqlSession aoMyBatisSession, String asProcurementId, Boolean aoSuccess)
			throws ApplicationException
	{
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		Boolean loUpdatedSuccess = Boolean.FALSE;
		try
		{
			List<String> notificationAlertList = new ArrayList<String>();
			notificationAlertList.add(HHSConstants.AL208);
			notificationAlertList.add(HHSConstants.AL207);
			notificationAlertList.add(HHSConstants.NT218);
			notificationAlertList.add(HHSConstants.AL206);
			notificationAlertList.add(HHSConstants.NT215);
			notificationAlertList.add(HHSConstants.NT211);
			notificationAlertList.add(HHSConstants.NT212);
			notificationAlertList.add(HHSConstants.NT215);
			notificationAlertList.add(HHSConstants.NT216);
			notificationAlertList.add(HHSConstants.NT217);
			notificationAlertList.add(HHSConstants.NT205);
			notificationAlertList.add(HHSConstants.AL210);
			notificationAlertList.add(HHSConstants.AL209);
			notificationAlertList.add(HHSConstants.NT218);

			if (aoSuccess)
			{
				loNotificationMap.put(HHSConstants.NOTIFICATION_LIST_KEY, notificationAlertList);
				loNotificationMap.put(HHSConstants.ENTITY_TYPE, HHSConstants.PROCUREMENT);
				loNotificationMap.put(HHSConstants.ENTITY_ID, asProcurementId);
				DAOUtil.masterDAO(aoMyBatisSession, loNotificationMap,
						ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, "updateNotificationStatus",
						HHSConstants.JAVA_UTIL_HASH_MAP);
				loUpdatedSuccess = Boolean.TRUE;
			}
		}
		catch (ApplicationException aoExp)
		{
			setMoState("Error occurred while updating Alert/Notification Statyus for Procument ID:" + asProcurementId);
			throw aoExp;
		}
		return loUpdatedSuccess;
	}

	/**
	 * Added Method in R4. This method will fetch the data from
	 * Notification_Alert_Master table for the particular notification alert ID
	 * @param aoMyBatisSession SQL Session
	 * @param asNotificationAlertId Event ID for which the details are to be
	 *            fetched
	 * @return the List of type NotificationAlertMasterBean corresponding to the
	 *         NotificationAlertId
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public List<NotificationAlertMasterBean> getNotificationAlertMasterDetails(SqlSession aoMyBatisSession,
			String asNotificationAlertId) throws ApplicationException
	{
		List<NotificationAlertMasterBean> loNotificationAlertMasterBean = null;
		try
		{
			HashMap loOrgMap = new HashMap();
			if (null != asNotificationAlertId && !asNotificationAlertId.isEmpty())
			{
				loOrgMap.put("notificationAlertId", asNotificationAlertId);
			}
			loNotificationAlertMasterBean = (List<NotificationAlertMasterBean>) DAOUtil.masterDAO(aoMyBatisSession,
					loOrgMap, ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER,
					HHSConstants.GET_NOTI_ALERT_MASTER_DETAILS, HHSConstants.JAVA_UTIL_HASH_MAP);
		}
		catch (ApplicationException aoExp)
		{

			throw aoExp;
		}
		return loNotificationAlertMasterBean;
	}

	/**
	 * It will process the Notification Map and will insert the data in
	 * GROUP_NOTIFICTION Table
	 * <ul>
	 * <li>Update Method in R4</li>
	 * <li>Will iterate the Event Id List</li>
	 * <li>Then generate the list of NotificationBean based on organization id
	 * and alert id</li>
	 * <li>Then call the function <b>insertIntoGroupNotificationTable</b> to
	 * insert the data into Group_Notification table</li>
	 * <li>Insert the generated list into the Group_Notification table by
	 * executing query <b>insertIntoGroupNotificationTable</b></li>
	 * </ul>
	 * @param aoMyBatisSession Sql session
	 * @param aoHMNotifyParam - Map object - Contains the List of event Ids, and
	 *            the map corresponding to each event id and a request map that
	 *            contains the parameters to be replaced
	 * @param aoValidateStatus
	 * @return returns true if the Notification is added successfully in the
	 *         Group_Notificaiton Table otherwise false
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public Boolean processNotification(SqlSession aoMyBatisSession, HashMap<String, Object> aoHMNotifyParam,
			Boolean aoValidateStatus) throws ApplicationException
	{
		LOG_OBJECT.Debug("In process of EmailNotification.");
		Boolean loNotificationStatus = Boolean.FALSE;
		String lsEntityType = null;
		String lsEntityId = null;
		String lsEventId = null;

		if (aoValidateStatus)
		{
			try
			{
				if (null != aoHMNotifyParam && !aoHMNotifyParam.isEmpty())
				{
					List<NotificationBean> loFinalNotificationList = new ArrayList<NotificationBean>();

					List<String> loNotificationList = (List<String>) aoHMNotifyParam
							.get(HHSConstants.NOTIFICATION_ALERT_ID);
					Iterator<String> loNotificationIterator = loNotificationList.iterator();

					HashMap<String, String> loRequestMap = (HashMap<String, String>) aoHMNotifyParam
							.get(TransactionConstants.REQUEST_MAP_PARAMETER_NAME);

					lsEntityType = (String) aoHMNotifyParam.get(ApplicationConstants.ENTITY_TYPE);
					lsEntityId = (String) aoHMNotifyParam.get(ApplicationConstants.ENTITY_ID);
					String lsCreatedBy = (String) aoHMNotifyParam.get(HHSConstants.CREATED_BY_USER_ID);
					String lsModifiedBy = (String) aoHMNotifyParam.get(HHSConstants.MODIFIED_BY);

					if (lsCreatedBy == null)
					{
						lsCreatedBy = HHSConstants.DEFAULT;
					}
					if (lsModifiedBy == null)
					{
						lsModifiedBy = HHSConstants.DEFAULT;
					}
					if (lsEntityType == null)
					{
						lsEntityType = HHSConstants.DEFAULT;
					}
					if (lsEntityId == null)
					{
						lsEntityId = HHSConstants.DEFAULT;
					}
					while (loNotificationIterator.hasNext())
					{

						lsEventId = (String) loNotificationIterator.next();

						List<NotificationAlertMasterBean> loNotificationAlertMasterBeanList = getNotificationAlertMasterDetails(
								aoMyBatisSession, lsEventId);
						NotificationAlertMasterBean loNotificationAlertMasterBean = null;
						HashSet<String> loOrgTypeSet = new HashSet<String>();
						if (null != loNotificationAlertMasterBeanList && !loNotificationAlertMasterBeanList.isEmpty())
						{
							loNotificationAlertMasterBean = loNotificationAlertMasterBeanList.get(0);

						}
						// R 8.4.2
						// Application Exception handling is breaking legacy code - commented out for now
						//else{							
						//	throw new ApplicationException("No email template found for lsEventId:" +lsEventId +",lsEntityType:" +lsEntityType);
						//}
						String lsMsgSubject = "";
						if (null != loNotificationAlertMasterBean && null != loNotificationAlertMasterBean.getSubject())
						{
							lsMsgSubject = loNotificationAlertMasterBean.getSubject();
						}
						lsMsgSubject = ReplaceParams.replaceWithParams(lsMsgSubject, loRequestMap);
						String lsMsgBody = "";
						if (null != loNotificationAlertMasterBean
								&& null != loNotificationAlertMasterBean.getMessageBody())
						{
							lsMsgBody = loNotificationAlertMasterBean.getMessageBody();
						}
						lsMsgBody = ReplaceParams.replaceWithParams(lsMsgBody, loRequestMap);

						String lsModuleName = "";
						if (null != loNotificationAlertMasterBean
								&& null != loNotificationAlertMasterBean.getModuleName())
						{
							lsModuleName = loNotificationAlertMasterBean.getModuleName();
						}
						String lsNotificationSent = HHSConstants.FALSE;
						String lsOrgType = "";
						List<String> loOrgnizationList = null;
						NotificationDataBean loNotificationDataBean = (NotificationDataBean) aoHMNotifyParam
								.get(lsEventId);
						if (null != loNotificationAlertMasterBeanList)
						{
							for (NotificationAlertMasterBean loNotifAlertMasterBean : loNotificationAlertMasterBeanList)
							{
								if (!loOrgTypeSet.contains(loNotifAlertMasterBean.getOrgType()))
								{
									loOrgTypeSet.add(loNotifAlertMasterBean.getOrgType());
									lsOrgType = loNotifAlertMasterBean.getOrgType();

									if (lsOrgType.equalsIgnoreCase(HHSConstants.PROVIDER))
									{
										loOrgnizationList = loNotificationDataBean.getProviderList();
										if (null != loOrgnizationList)
										{
											for (Iterator<String> loProviderItr = loOrgnizationList.iterator(); loProviderItr
													.hasNext();)
											{
												String lsProvider = loProviderItr.next();
												loFinalNotificationList = generateGroupNotificationBean(
														loFinalNotificationList, lsMsgSubject, lsMsgBody, lsProvider,
														lsEntityType, lsEntityId, lsModuleName, lsEventId,
														lsNotificationSent, lsOrgType, lsCreatedBy, lsEventId,
														loNotificationDataBean.getLinkMap(),
														loNotificationDataBean.getAdditionalParameterMap(),
														lsModifiedBy);
											}
										}
									}

									else if (lsOrgType.equalsIgnoreCase(HHSConstants.AGENCY))
									{
										loOrgnizationList = loNotificationDataBean.getAgencyList();
										if (null != loOrgnizationList)
										{
											for (Iterator<String> loAgencyItr = loOrgnizationList.iterator(); loAgencyItr
													.hasNext();)
											{
												String lsAgencyId = loAgencyItr.next();
												loFinalNotificationList = generateGroupNotificationBean(
														loFinalNotificationList, lsMsgSubject, lsMsgBody, lsAgencyId,
														lsEntityType, lsEntityId, lsModuleName, lsEventId,
														lsNotificationSent, lsOrgType, lsCreatedBy, lsEventId,
														loNotificationDataBean.getAgencyLinkMap(),
														loNotificationDataBean.getAdditionalParameterMap(),
														lsModifiedBy);
											}
										}
									}
									else if (lsOrgType.equalsIgnoreCase(HHSConstants.ACCELERATOR))
									{
										loFinalNotificationList = generateGroupNotificationBean(
												loFinalNotificationList, lsMsgSubject, lsMsgBody,
												HHSConstants.CITY_ORG, lsEntityType, lsEntityId, lsModuleName,
												lsEventId, lsNotificationSent, lsOrgType, lsCreatedBy, lsEventId,
												loNotificationDataBean.getLinkMap(),
												loNotificationDataBean.getAdditionalParameterMap(), lsModifiedBy);
									}
								}
							}
						}
					}
					if (null != loFinalNotificationList && !loFinalNotificationList.isEmpty())
					{
						insertIntoGroupNotificationTable(aoMyBatisSession, loFinalNotificationList, loRequestMap);
						setMoState("Transaction Success:Notification Data inserted successfully for EventType:"
								+ lsEventId);
						loNotificationStatus = Boolean.TRUE;
					}
				}
			}
			catch (ApplicationException aoExp)
			{
				setMoState("Transaction Failure:Error occurred while inserting notification data for Event ID:"
						+ lsEventId);
				throw aoExp;
			}
			catch (Exception loEx)
			{
				setMoState("Transaction Failure:Error occurred while inserting notification data for Event ID:"
						+ aoHMNotifyParam.get(TransactionConstants.EVENT_TYPE_PARAMETER_NAME));
				throw new ApplicationException("Error while fetching Procurement details for user Type:" + lsEventId,
						loEx);
			}
		}

		return loNotificationStatus;
	}

	/**
	 * Added for R4 It will generate the Group Notification Bean list to insert
	 * it to GROUP_NOTIFICATION Table
	 * @param aoNotificationList List of type NotificationBean
	 * @param asMsgSubject subject of the mail
	 * @param asMsgBody body of the mail
	 * @param asProviderId contains the organization id
	 * @param asEntityType contains the Entity Type for which the Notification
	 *            will be sent
	 * @param asEntityId contains the Entity ID for which the Notification will
	 *            be sent
	 * @param asModuleName contains the module name for which the Notification
	 *            will be sent
	 * @param asNotificationAlertId contains the Event ID of the Notification
	 * @param asNotificationSent to check whether the notification is sent or
	 *            not
	 * @param asOrgType contains the organization
	 *            type(provider,agency,accelerator)
	 * @param asCreatedBy user ID of the user who generated the notification
	 * @param asEventId Notification Alert Id
	 * @param aoLinkMap Map containing the URL's to be replaced
	 * @param aoParamMap map containing the Strings to be replaced except URL's
	 * @param asModifiedBy user ID of the user who modified the notification
	 * @return the list of type NotificationBean
	 */
	private List<NotificationBean> generateGroupNotificationBean(List<NotificationBean> aoNotificationList,
			String asMsgSubject, String asMsgBody, String asProviderId, String asEntityType, String asEntityId,
			String asModuleName, String asNotificationAlertId, String asNotificationSent, String asOrgType,
			String asCreatedBy, String asEventId, HashMap<String, String> aoLinkMap,
			HashMap<String, String> aoParamMap, String asModifiedBy)
	{

		if (null == aoNotificationList)
		{
			aoNotificationList = new ArrayList<NotificationBean>();
		}

		NotificationBean loNotificationBean = new NotificationBean();
		loNotificationBean.setGroupName(null);
		loNotificationBean.setSubject(asMsgSubject);
		loNotificationBean.setMessageBody(asMsgBody);
		loNotificationBean.setProviderId(asProviderId);
		loNotificationBean.setNotificationAlertId(asNotificationAlertId);
		loNotificationBean.setNotificationSent(asNotificationSent);
		loNotificationBean.setEntityId(asEntityId);
		loNotificationBean.setEntityType(asEntityType);
		loNotificationBean.setModuleName(asModuleName);
		loNotificationBean.setOrgType(asOrgType);
		loNotificationBean.setCreatedBy(asCreatedBy);
		loNotificationBean.setEventId(asEventId);
		loNotificationBean.setLinkMap(aoLinkMap);
		loNotificationBean.setParamMap(aoParamMap);
		loNotificationBean.setModifiedBy(asModifiedBy);
		aoNotificationList.add(loNotificationBean);

		return aoNotificationList;

	}

	/**
	 * Added for R4. This method insert the data into GROUP_NOTIFICATION Table
	 * and links in NOTIFICATION_ALERT_URL Table
	 * <ul>
	 * <li>Added Method in R4</li>
	 * <li>It will iterate each group notification bean list</li>
	 * <li>First it will make an entry into GROUP_NOTIFICATION_TABLE by
	 * executing query <b>insertIntoGroupNotificationTable</b></li>
	 * <li>It will check whether the link map contains LINKS</li>
	 * <li>If it contains link it will make an entry into
	 * Notification_Alert_URL_Table by executing query
	 * <b>insertIntoNotificationAlertUrl</b></li>
	 * <li>After that It will replace the links in the body and will update the
	 * Group Notification Table by executing query
	 * <b>updateGroupNotificationTable</b></li>
	 * <li>After that it check for the additional params. if the additional
	 * params are there then they are inserted into NOTIFICATION_PARAM_VALUE
	 * table with query<b>insertInNotificationParams</b></li>
	 * </ul>
	 * @param aoMyBatisSession Sql Session
	 * @param aoNotificationList Notification Bean List to insert in
	 *            Notification Table
	 * @param aoRequestMap Contains the links that need to be replaced in the
	 *            email body
	 * @return returns true if the Notification is added successfully in the
	 *         Group_Notificaiton Table otherwise false
	 * @throws ApplicationException
	 */
	public boolean insertIntoGroupNotificationTable(SqlSession aoMyBatisSession,
			List<NotificationBean> aoNotificationList, HashMap<String, String> aoRequestMap)
			throws ApplicationException
	{
		boolean lbStatus = false;
		// Start R.9.2 QC 9651 disable PQL Notifications and Alerts
		List<String> nalist = new ArrayList<String>();
		nalist.add(ApplicationConstants.NT003);
		nalist.add(ApplicationConstants.NT006);
		nalist.add(ApplicationConstants.NT007);
		nalist.add(ApplicationConstants.NT009);
		nalist.add(ApplicationConstants.NT010);
		nalist.add(ApplicationConstants.NT022);
		nalist.add(ApplicationConstants.NT023); 
		nalist.add(ApplicationConstants.NT024); 
		nalist.add(ApplicationConstants.NT025); 
		nalist.add(ApplicationConstants.NT026); 
		nalist.add(ApplicationConstants.NT027); 
		nalist.add(ApplicationConstants.NT028); 
		nalist.add(ApplicationConstants.NT029); 
		nalist.add(ApplicationConstants.NT030); 
		nalist.add(ApplicationConstants.NT035);
		nalist.add(ApplicationConstants.AL003);
		nalist.add(ApplicationConstants.AL005);
		nalist.add(ApplicationConstants.AL008);
		nalist.add(ApplicationConstants.AL010);
		nalist.add(ApplicationConstants.AL011);
		nalist.add(ApplicationConstants.AL020); 
		nalist.add(ApplicationConstants.AL021); 
		nalist.add(ApplicationConstants.AL022); 
		nalist.add(ApplicationConstants.AL023);
		nalist.add(ApplicationConstants.AL024); 
		nalist.add(ApplicationConstants.AL025); 
		nalist.add(ApplicationConstants.AL026);
		nalist.add(ApplicationConstants.AL027);
		nalist.add(ApplicationConstants.AL029); 
		// End R.9.2 QC 9651 disable PQL Notifications and Alerts
		
		try
		{
			if (null != aoNotificationList)
			{
				Iterator<NotificationBean> loIter = aoNotificationList.iterator();
				HashMap<String, Object> loLinkParamMap = new HashMap<String, Object>();
				while (loIter.hasNext())
				{
					NotificationBean loNotificationBean = loIter.next();
					// Start R.9.2 QC 9651 disable PQL Notifications and Alerts
					if (!nalist.contains(loNotificationBean.getEventId()))
					{
					// End R.9.2 QC 9651 disable PQL Notifications and Alerts					
						int liGroupNotificationId = getNextGroupNotificationId(aoMyBatisSession);
						loNotificationBean.setGroupNotificationId(liGroupNotificationId);
						DAOUtil.masterDAO(aoMyBatisSession, loNotificationBean,
								ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER,
								HHSConstants.INS_INTO_GROUP_NOTI_TABLE, HHSConstants.NOTIFICATION_BEAN);
	
						HashMap<String, Object> loUrlParamMap = new HashMap<String, Object>();
						loUrlParamMap.put(HHSConstants.GROUP_NOTIFICATION_ID, liGroupNotificationId);
						String lsEventId = loNotificationBean.getEventId();
	
						// This will replace the links in the message body
						if (loNotificationBean.getLinkMap() != null)
						{
							Iterator<String> loIterator = loNotificationBean.getLinkMap().keySet().iterator();
	
							while (loIterator.hasNext())
							{
								String lsKey = loIterator.next();
								if (null != lsKey)
								{
	
									if (lsKey.toLowerCase().contains(HHSConstants.LINK.toLowerCase()))
									{
										String lsValue = loNotificationBean.getLinkMap().get(lsKey);
										String lsUrlOrderNumber = HHSConstants.ONE;
										if (!lsKey.toLowerCase().contains(HHSConstants.USER_PROVIDER)
												&& !lsKey.toLowerCase().contains(HHSConstants.AGENCY.toLowerCase()))
										{
	
											if (!lsKey.equalsIgnoreCase(HHSConstants.LINK))
											{
												lsUrlOrderNumber = Character.toString(lsKey.charAt(lsKey.length() - 1));
											}
										}
										loUrlParamMap.put(HHSConstants.URL, lsValue);
										loUrlParamMap.put(HHSConstants.URL_ORDER, lsUrlOrderNumber);
										loUrlParamMap.put(HHSConstants.CREATED_BY, loNotificationBean.getCreatedBy());
										loUrlParamMap.put(HHSConstants.MODIFIED_BY, loNotificationBean.getCreatedBy());
										loUrlParamMap.put(HHSConstants.GROUP_NOTIFICATION_ID, liGroupNotificationId);
										int liNextNotificationAlertUrlId = getNextNotificationAlertUrlId(aoMyBatisSession);
										loUrlParamMap.put(HHSConstants.GROUP_NOTIFICATION_URL_ID,
												liNextNotificationAlertUrlId);
										DAOUtil.masterDAO(aoMyBatisSession, loUrlParamMap,
												ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER,
												HHSConstants.INS_INTO_NOTI_ALERT_URL, HHSConstants.JAVA_UTIL_HASH_MAP);
										String lsURLToReplace = "";
										// For particular Events the attribute
										// _pageLabel in the URL is changed
	
										if (lsEventId.equalsIgnoreCase(HHSConstants.NT039)
												|| lsEventId.equalsIgnoreCase(HHSConstants.NT037)
												|| lsEventId.equalsIgnoreCase(HHSConstants.NT038)
												|| lsEventId.equalsIgnoreCase(HHSConstants.NT034)
												|| lsEventId.equalsIgnoreCase(HHSConstants.NT020))
										{
											// In case of provider URL that contains
											// "?"
											if (lsValue.contains("?"))
											{
												lsURLToReplace = lsValue.substring(0, lsValue.indexOf('?') + 1)
														+ "_pageLabel=xyz&notificationIdFromEmail="
														+ liNextNotificationAlertUrlId;
											}
											// In case of city URL that doesn't
											// contain "?"
											else
											{
												// added lsValue in the url.it was
												// missed earlier.
												// incident for NT032 and NT033
												lsURLToReplace = lsValue + "?" + "_pageLabel=xyz&notificationIdFromEmail="
														+ liNextNotificationAlertUrlId;
											}
										}
										else
										{
											if (lsValue.contains("?"))
											{
												lsURLToReplace = lsValue.substring(0, lsValue.indexOf('?') + 1)
														+ "_pageLabel=abc&notificationIdFromEmail="
														+ liNextNotificationAlertUrlId;
											}
											else
											{
												// Start QC9611 R 9.1 Update in-system notifications and alerts for CHAR 500  Redirect to PASSPort
												
												lsURLToReplace = lsValue;
												// add if condition for else
												if(!lsValue.equalsIgnoreCase(ApplicationConstants.PASSPORT_SHARED_DOCUMENT_ALERT_NOTIFICATION_LINK))
												{
												// End QC9611 R 9.1 Update in-system notifications and alerts for CHAR 500  Redirect to PASSPort
													
												// added lsValue in the url.it was
												// missed earlier.
												// incident for NT032 and NT033
												lsURLToReplace = lsValue + "?" + "_pageLabel=abc&notificationIdFromEmail="
														+ liNextNotificationAlertUrlId;
												} // End QC9611 R 9.1  - end of if condition
											}
										}
										// Updated in Release 3.1.0, Notification
										// Link Creation Replacing '&' as '&amp;'
										loLinkParamMap.put(lsKey, lsURLToReplace.replaceAll("&not", "&amp;not"));
									}
								}
							}
						}
	
						String lsBody = ReplaceParams
								.replaceWithParams(loNotificationBean.getMessageBody(), loLinkParamMap);
						HashMap<String, Object> aoMsgBodyMap = new HashMap<String, Object>();
						aoMsgBodyMap.put(HHSConstants.MSG_BODY, lsBody);
						aoMsgBodyMap.put(HHSConstants.GROUP_NOTIFICATION_ID, liGroupNotificationId);
						DAOUtil.masterDAO(aoMyBatisSession, aoMsgBodyMap,
								ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER,
								HHSConstants.UPDATE_GROUP_NOTI_TABLE, HHSConstants.JAVA_UTIL_HASH_MAP);
	
						// Check for some additional params that needed to be
						// inserted into Notification_param_values table
						if (loNotificationBean.getParamMap() != null)
						{
	
							Iterator<String> loIterator = loNotificationBean.getParamMap().keySet().iterator();
							while (loIterator.hasNext())
							{
								String lsKey = loIterator.next();
								if (null != lsKey)
								{
									String lsValue = loNotificationBean.getParamMap().get(lsKey);
									NotificationParamBean loNotificationParamBean = new NotificationParamBean();
									loNotificationParamBean.setGroupNotificationId(liGroupNotificationId + "");
									loNotificationParamBean.setParamName(lsKey);
									loNotificationParamBean.setParamValue(lsValue);
									DAOUtil.masterDAO(aoMyBatisSession, loNotificationParamBean,
											ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER,
											HHSConstants.INS_INTO_NOTI_PARAM_TABLE, HHSConstants.NOTI_PARAM_BEAN);
								}
							}
						}
	
					}
				// Start QC 9651 R 9.2 	disable PQL Notifications and Alerts		
				} 
				// End QC 9651 R 9.2 	disable PQL Notifications and Alerts							
			}
			lbStatus = true;
			setMoState("Transaction Success: Notification Details successfully inserted");
		}
		catch (ApplicationException aoExp)
		{
			setMoState("Transaction failure: Error occurred while inserting details for notification table");
			throw aoExp;
		}
		catch (Exception loEx)
		{
			setMoState("Transaction failure: Error occurred while inserting details for notification table");
			throw new ApplicationException(
					"Transaction failure: Error occurred while inserting details for notification table", loEx);
		}
		return lbStatus;
	}

	/**
	 * Added for R4. This method fetches the Next Group_Notification_Id from the
	 * sequence SEQ_GROUP_NOTIFICATION_ID
	 * @param aoMyBatisSession Sql session
	 * @return returns the groupNotificationId of the new row to be inserted
	 * @throws ApplicationException
	 */
	private int getNextGroupNotificationId(SqlSession aoMyBatisSession) throws ApplicationException
	{
		int liGroupNotificationId = 0;
		try
		{
			liGroupNotificationId = (Integer) DAOUtil.masterDAO(aoMyBatisSession, null,
					ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, HHSConstants.GET_NEXT_GROUP_NOTI_ID, null);
			setMoState("Transaction Success: Max Notification Id fetcehd successfully");
		}
		catch (ApplicationException aoExp)
		{
			setMoState("Transaction Failure: Error Occurred while fetching next notification Id");
			throw aoExp;
		}
		catch (Exception loEx)
		{
			setMoState("Transaction Failure: Error Occurred while fetching next notification Id");
			throw new ApplicationException("Transaction Failure: Error Occurred while fetching next notification Id",
					loEx);
		}
		return liGroupNotificationId;
	}

	/**
	 * Added for R4. This method fetches the Next Group_Notification_Alert_Id
	 * from the sequence SEQ_GROUP_NOTIFICATION_Alert_ID
	 * 
	 * @param aoMyBatisSession Sql session
	 * @return returns the liGroupNotificationUrlId of the new row to be
	 *         inserted
	 * @throws ApplicationException
	 */
	private int getNextNotificationAlertUrlId(SqlSession aoMyBatisSession) throws ApplicationException
	{
		int liGroupNotificationUrlId = 0;
		try
		{
			liGroupNotificationUrlId = (Integer) DAOUtil.masterDAO(aoMyBatisSession, null,
					ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, HHSConstants.GET_NEXT_NOTI_ALERT_URL_ID,
					null);
			setMoState("Transaction Success: Next Notification Alert URL Id fetcehd successfully");
		}
		catch (ApplicationException aoExp)
		{
			setMoState("Transaction Failure: Error Occurred while fetching next notification Alert URL Id");
			throw aoExp;
		}
		catch (Exception loEx)
		{
			setMoState("Transaction Failure: Error Occurred while fetching next notification Alert URL Id");
			throw new ApplicationException(
					"Transaction Failure: Error Occurred while fetching next notification Alert URL Id", loEx);
		}
		return liGroupNotificationUrlId;
	}

	
	
	
	/**
	 * Added for R4. This method will fetch all the rows from
	 * GROUP_NOTIFICATION_TABLE where NOTIFICATION_SENT is false
	 * @param aoMyBatisSession Sql session
	 * @return return the list of type NotificationBean
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked" })
	public List<NotificationBean> getUnsentGroupNotificationsList(SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		List<NotificationBean> loGroupDetailList = null;
		try
		{
			loGroupDetailList = (List<NotificationBean>) DAOUtil.masterDAO(aoMyBatisSession, null,
					ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, HHSConstants.GET_UNSENT_GROUP_NOTI_LIST,
					null);
			setMoState("Transaction Success: Group Notification Data fetched successfully");
		}
		catch (ApplicationException aoAppEx)
		{
			setMoState("Transaction Failure: Error Occurred while fetching group notification Data");
			throw aoAppEx;
		}
		catch (Exception loEx)
		{
			setMoState("Transaction Failure: Error Occurred while fetching group notification Data");
			throw new ApplicationException(
					"Transaction Failure: Error Occurred while fetching group notification Data", loEx);
		}
		return loGroupDetailList;
	}

	/****
	 * Added for R7.4.0. Fetch all voucher #s into one email and send the email to agency When approved payment rejected by FMS 
	 * Then Set NOTIFICATION_SENT true which prevent NT318 from being sent again.
     * @param aoMyBatisSession Sql session
     * @return return the list of type NotificationBean
     * @throws ApplicationException
	 */
	   @SuppressWarnings({ "unchecked" })
       public boolean getUnsentNT318List(SqlSession aoMyBatisSession)
               throws ApplicationException
       {
		// Start QC9630 R 9.1 - Optimize NT318 Notifications
       	   
           int limit = 60000;
           List<NotificationBean> descriptionList = null;
           HashMap<String, Object> loAgencyDescMap = new HashMap<String, Object>();
 
           try
           {
               
        	   descriptionList = (List<NotificationBean>) DAOUtil.masterDAO(aoMyBatisSession, null,
       					ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, HHSConstants.FETCH_NT318_DESCRIPTION, null);
        	   LOG_OBJECT.Info("=====Description list retreived======::  "+descriptionList.size() );
        	   
        	   StringBuilder sb = new StringBuilder();
        	   String agency = "";
        	   String description = "";
        	  
        	   if(descriptionList!=null && !descriptionList.isEmpty())
        	   {	   
        	   	   for (NotificationBean loNotificationBean : descriptionList)
		   		   {   
        	   		    
        	   		    if (agency.isEmpty())
        	   		    {	
        	   		    	agency = loNotificationBean.getProviderId();
        	   		    	
        	   		    }
        	   		    
        	   		    if (!agency.equalsIgnoreCase(loNotificationBean.getProviderId())  ) // or description exceed the limit
        	   		    {
        	   		    	// insert into Group Notification table
        	   		    	description = sb.toString();
        	   		    	LOG_OBJECT.Info("1=====Agency ::: "+agency);
        	   		    	LOG_OBJECT.Info("1=====Description length::: "+description.length());
        	   		    	
        	   		    	loAgencyDescMap.put("agency", agency);
        	   		    	loAgencyDescMap.put("description", description);
        	   		    	
        	   		    	DAOUtil.masterDAO(aoMyBatisSession, loAgencyDescMap,
        	                        ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, HHSConstants.REWRITE_NT318_MSG, HHSConstants.JAVA_UTIL_HASH_MAP);
        	                       	   		    	
        	   		    	// reset values
        	   		    	agency = loNotificationBean.getProviderId();
        	   		    	sb.setLength(0);
        	   		    }
        	   		    
                        if(sb!=null && sb.length() > 0)
                        {	
                        	String sbstring = sb.toString().replace(",",  "<br/>");
                         	int sbln = sbstring.length();
                        	String currentDesc = loNotificationBean.getMessageBody().replace(",",  "<br/>"); 
                           	int descln = currentDesc.length();
                           	int total = descln + sbln;
                        	
                        	if(total >= limit)
                        	{
                        		//insert into Group Notification table
                        		description = sb.toString();
            	   		    	LOG_OBJECT.Info("Limit Agency ::: "+agency);
            	   		    	LOG_OBJECT.Info("Limit Description length::: "+description.length());
            	   		    	//LOG_OBJECT.Info("2=====Description ::: "+description);
            	   		    	           	   		    	
            	   		    	loAgencyDescMap.put("agency", agency);
            	   		    	loAgencyDescMap.put("description", description);
            	   		    	
            	   		    	
            	   		    	DAOUtil.masterDAO(aoMyBatisSession, loAgencyDescMap,
            	                        ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, HHSConstants.REWRITE_NT318_MSG, HHSConstants.JAVA_UTIL_HASH_MAP);
            	                    	   		    	
            	   		    	// reset value
            	   		    	sb.setLength(0);
                        	}
                        	
                        }
                        //LOG_OBJECT.Info("=====loNotificationBean.getMessageBody() ::: "+ loNotificationBean.getMessageBody());
        	   		    sb.append(loNotificationBean.getMessageBody());
				   	   	sb.append(",");
				  			
		   		   }
        	   	   //process last 
        	   	   //insert into Group Notification table
        	   	   	description = sb.toString();
        	   	   	description = description.replace(",", "<br/>");
	   		    	LOG_OBJECT.Info("====last Agency ::: "+agency);
	   		    	LOG_OBJECT.Info("====last=Description length::: "+description.length());
	   		    	//LOG_OBJECT.Info("====last Description ::: "+description);
	   		    	
	   		    	loAgencyDescMap.put("agency", agency);
	   		    	loAgencyDescMap.put("description", description);
	   		    	
	   		    	DAOUtil.masterDAO(aoMyBatisSession, loAgencyDescMap,
	                        ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, HHSConstants.REWRITE_NT318_MSG, HHSConstants.JAVA_UTIL_HASH_MAP);
        	   
	   		    	DAOUtil.masterDAO(aoMyBatisSession, null,
	                        ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, HHSConstants.UPDATE_NOTIFICATION_STATUS_NT318, null);
        	   }
       		   	   
        	   
        	   //DAOUtil.masterDAO(aoMyBatisSession, null,
               //        ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, HHSConstants.REWRITE_NT318_MSG, null);
               setMoState("Transaction Success: Group Notification Data NT318 rewrote notification successfully");
               
            // End QC9630 R 9.0.1 - Optimize NT318 Notifications
           }    
           catch (ApplicationException aoAppEx)
           {
               setMoState("Transaction Failure: Error Occurred while rewriting NT318 notification ");
               throw aoAppEx;
           }
           catch (Exception loEx)
           {
               setMoState("Transaction Failure: Error Occurred while rewriting NT318 notification ");
               throw new ApplicationException(
                       "Transaction Failure: Error Occurred while fetching group notification Data", loEx);
           }
           return true;
       }


	/**
	 * It will process the group notification bean and will send either Alert or
	 * Mail depending upon Notification Type
	 * <ul>
	 * <li>Added Method in R4</li>
	 * <li>First it will fetch the master detail map containing all the rows
	 * from Notification_Alert_Master Table corresponding to that particular
	 * Event Id</li>
	 * <li>Then it will check for the prerprocessing</li>
	 * <li>If the preprocessing is required then appropriate preprocessing class
	 * is used to fetch required results</li>
	 * <li>If the preprocessing is not required then the user email is fetched
	 * by executing query <b>getUserEmailIds</b></li>
	 * </ul>
	 * <li>But in case of some special Notifications query
	 * <b>getUserEmailIds012</b> is executed</li>
	 * @param aoMyBatisSession Sql session
	 * @param aoGroupNotificationBean The Notification Bean whose entry will be
	 *            made in Notification Table or User_Notificaiton Table
	 *            depending upon its type
	 * @return true if the data is properly inserted into Notification Table/
	 *         User_Notificaiton table else returns false
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public boolean insertAllNotifications(SqlSession aoMyBatisSession, NotificationBean aoGroupNotificationBean)
			throws ApplicationException
	{
		boolean lbStatus = true;
		try
		{

			HashMap<String, Object> loUserGroupMap = new HashMap<String, Object>();
			HashMap<String, String> loUserIdMap = new HashMap<String, String>();
			List<NotificationAlertMasterBean> loNotificationAlertMasterBeanList = getNotificationAlertMasterDetails(
					aoMyBatisSession, null);
			
			Map<String, List<NotificationAlertMasterBean>> loNotiAlertMasterDetailsMap = getNotificationAlertMasterDetailsMap(loNotificationAlertMasterBeanList);

			loUserIdMap.clear();
			String lsEventId = aoGroupNotificationBean.getNotificationAlertId();
			loNotificationAlertMasterBeanList = loNotiAlertMasterDetailsMap.get(lsEventId);

			List<String> loUserLevels = new ArrayList<String>();
			List<String> loPermissionType = new ArrayList<String>();
			boolean lbIsPrePRocessingRequired = false;
			String lsClassName = null;
			String lsAlertType = null;
			

			for (NotificationAlertMasterBean loNotificationAlertMasterBean : loNotificationAlertMasterBeanList)
			{
				if (loNotificationAlertMasterBean.getUserLevel() != null)
				{
					loUserLevels.add(loNotificationAlertMasterBean.getUserLevel());
				}
				if (loNotificationAlertMasterBean.getPermissionType() != null)
				{
					loPermissionType.add(loNotificationAlertMasterBean.getPermissionType());
				}
				if (!lbIsPrePRocessingRequired
						&& loNotificationAlertMasterBean.getPreProcessingRequired().equalsIgnoreCase(HHSConstants.ONE))
				{
					lbIsPrePRocessingRequired = true;
					lsClassName = loNotificationAlertMasterBean.getPreProcessingClass();
				}
				if (lsAlertType == null)
				{
					lsAlertType = loNotificationAlertMasterBean.getAlertType();
				}
				
			}
			// Release 5 changes done for contract restriction
			if (Arrays.asList(HHSR5Constants.CONTRACT_RESTRICTED_NOTIFICATION_BUDGET).contains(
					aoGroupNotificationBean.getNotificationAlertId()))
			{
				loUserGroupMap.put(HHSR5Constants.CONTRACT_RESTRICTION_NOTIFICATION, "Budget");
			}
			if (Arrays.asList(HHSR5Constants.CONTRACT_RESTRICTED_NOTIFICATION_INVOICE).contains(
					aoGroupNotificationBean.getNotificationAlertId()))
			{
				loUserGroupMap.put(HHSR5Constants.CONTRACT_RESTRICTION_NOTIFICATION, "Invoice");
			}

			loUserGroupMap.put(HHSConstants.ENTITY_ID, aoGroupNotificationBean.getEntityId());
			loUserGroupMap.put(HHSConstants.ORGID, aoGroupNotificationBean.getProviderId());
			if (loUserLevels.size() > 0)
			{
				loUserGroupMap.put(HHSConstants.USER_LEVELS, loUserLevels);
			}
			if (loPermissionType.size() > 0)
			{
				loUserGroupMap.put(HHSConstants.PERMISSION_TYPE, loPermissionType);
			}
			List<UserEmailIdBean> loUserEmailList = null;
			
			//System.out.println("@@@@@ EventId ::: " + lsEventId + "       ");
			if (lbIsPrePRocessingRequired && lsClassName != null)
			{
				Object loObj = Class.forName(lsClassName).newInstance();
				PreProcessingNotificationAlert loPreProcessing = (PreProcessingNotificationAlert) loObj;
				loUserEmailList = loPreProcessing.getAllUsersForNotificationAlert(aoMyBatisSession,
						aoGroupNotificationBean, loNotificationAlertMasterBeanList, loUserGroupMap);
				LOG_OBJECT.Debug("Notification user list fetched from pre processing class ::: " + loUserEmailList);
			}
			else
			{
				// fix done for NT019 .. HD02030160 - start
				if (lsEventId.equalsIgnoreCase(HHSConstants.NT011) || lsEventId.equalsIgnoreCase(HHSConstants.NT012))
				{
					loUserEmailList = (ArrayList<UserEmailIdBean>) DAOUtil.masterDAO(aoMyBatisSession, loUserGroupMap,
							ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER,
							HHSConstants.GET_USER_EMAIL_IDS_NT012, HHSConstants.JAVA_UTIL_HASH_MAP);
				}
				else if (lsEventId.equalsIgnoreCase(HHSConstants.NT019)
						|| lsEventId.equalsIgnoreCase(HHSConstants.AL019))
				{
					loUserEmailList = (ArrayList<UserEmailIdBean>) DAOUtil.masterDAO(aoMyBatisSession, loUserGroupMap,
							ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER,
							HHSConstants.GET_USER_EMAIL_IDS_NT019, HHSConstants.JAVA_UTIL_HASH_MAP);
				}
				/*[Start] R7.4.0 QC9134 */
				else if ( lsEventId.equalsIgnoreCase(HHSConstants.NT318) ){
                    loUserEmailList = (ArrayList<UserEmailIdBean>) DAOUtil.masterDAO(aoMyBatisSession, loUserGroupMap,
                            ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER,
                            HHSConstants.GET_USER_EMAIL_IDS_NT318, HHSConstants.JAVA_UTIL_HASH_MAP);
                   // System.out.println("Notification Bean::: "+HHSConstants.NT318+" == " + loUserGroupMap.get(HHSConstants.ORGID) );

				} 
                /*[End] R7.4.0 QC9134 */
				else 
				{
					loUserEmailList = (ArrayList<UserEmailIdBean>) DAOUtil.masterDAO(aoMyBatisSession, loUserGroupMap,
							ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, HHSConstants.GET_USER_EMAIL_IDS,
							HHSConstants.JAVA_UTIL_HASH_MAP);
				}

			}


			if (null != loUserEmailList && !loUserEmailList.isEmpty())
			{
				// Start || Changes done for 5978 for Release 3.11.0
				String lsOrgName = "";
				for (Iterator iterator = loUserEmailList.iterator(); iterator.hasNext();)
				{
					UserEmailIdBean userEmailIdBean = (UserEmailIdBean) iterator.next();
					if (!("true".equalsIgnoreCase(userEmailIdBean.getBccFlag())))
					{
						lsOrgName = userEmailIdBean.getOrgLegalName();
						break;
					}
				}
				// End || Changes done for 5978 for Reelase 3.11.0
				// incident regarding Agency abbreviation instead of Accelerator
				// and Agency Name in emails
				if (lsOrgName != null && lsOrgName.equalsIgnoreCase(HHSConstants.CITY))
				{
					lsOrgName = HHSConstants.ACCELERATOR;
				}

	            //System.out.println("@@@@@ lsOrgName ::: " + lsOrgName + "       ");
				aoGroupNotificationBean.setProviderName(lsOrgName);
				Boolean loMailSent = false;

				// call to send the mail
				if (lsAlertType.equalsIgnoreCase(HHSConstants.EMAIL))
				{
					loMailSent = sendMail(loUserEmailList, aoGroupNotificationBean.getSubject(),
						aoGroupNotificationBean.getMessageBody(), aoGroupNotificationBean.getProviderName());
				}
				for (UserEmailIdBean loUserEmailIdBean : loUserEmailList)
				{
					if (!loUserIdMap.containsKey(loUserEmailIdBean.getStaffId())
							&& (null == loUserEmailIdBean.getSkipUserInNotification() || "false"
									.equals(loUserEmailIdBean.getSkipUserInNotification())))
					{
						loUserIdMap.put(loUserEmailIdBean.getStaffId(), loUserEmailIdBean.getUserEmailId());

						aoGroupNotificationBean.setCreatedBy(HHSConstants.SYSTEM_USER);
						aoGroupNotificationBean.setModifiedBy(HHSConstants.SYSTEM_USER);
						aoGroupNotificationBean.setUserId(loUserEmailIdBean.getStaffId());
						aoGroupNotificationBean.setGroupName(loUserEmailIdBean.getUserLevel());
						aoGroupNotificationBean.setAlertType(lsAlertType);
						aoGroupNotificationBean.setProviderId(loUserEmailIdBean.getUserOrgId());

						if (lsAlertType.equalsIgnoreCase(HHSConstants.ALERT))
						{
							aoGroupNotificationBean.setReadNotification("N");
							aoGroupNotificationBean.setDeleteNotification("N");
							DAOUtil.masterDAO(aoMyBatisSession, aoGroupNotificationBean,
									ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER,
									HHSConstants.INS_INTO_USER_NOTI_TABLE, HHSConstants.NOTIFICATION_BEAN);
						}
						else if (lsAlertType.equalsIgnoreCase(HHSConstants.EMAIL))
						{
							if (loMailSent)
							{
								DAOUtil.masterDAO(aoMyBatisSession, aoGroupNotificationBean,
										ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER,
										HHSConstants.INS_INTO_NOTI_TABLE, HHSConstants.NOTIFICATION_BEAN);
							}
						}
					}
				}

				HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
				loNotificationMap.put(HHSConstants.GROUP_NOTIFICATION_ID,
						aoGroupNotificationBean.getGroupNotificationId());
				if (lsAlertType.equalsIgnoreCase(HHSConstants.ALERT)
						|| (lsAlertType.equalsIgnoreCase(HHSConstants.EMAIL) && loMailSent))
				{
					DAOUtil.masterDAO(aoMyBatisSession, loNotificationMap,
							ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER,
							HHSConstants.UPDATE_GROUP_NOTI_STATUS, HHSConstants.JAVA_UTIL_HASH_MAP);
				}
			}

			setMoState("Transaction Success: Notification Details successfully inserted");

		}
		catch (ApplicationException aoExp)
		{
			lbStatus = false;
			setMoState("Transaction failure: Error occurred while inserting details in notification table");
			LOG_OBJECT.Error("Error occurred while running email Notification batch:", aoExp);
		}

		catch (Exception loExp)
		{
			lbStatus = false;
			setMoState("Transaction failure: Error occurred while inserting details in notification table");
			LOG_OBJECT.Error("Error occurred while running email Notification batch:", loExp);
		}
		return lbStatus;
	}

	/**
	 * Added Method in R4.This Method will create the map which contains the
	 * List of type NotificationAlertMasterBean corresponding to the Event Type
	 * or Notification Alert ID
	 * @param aoNotificationAlertMasterBeanList
	 * @return the map which contains the List of type
	 *         NotificationAlertMasterBean
	 */
	private Map<String, List<NotificationAlertMasterBean>> getNotificationAlertMasterDetailsMap(
			List<NotificationAlertMasterBean> aoNotificationAlertMasterBeanList)
	{
		List<NotificationAlertMasterBean> loNotificationAlertMasterBeanList = new ArrayList<NotificationAlertMasterBean>();
		Map<String, List<NotificationAlertMasterBean>> aoNotiAlertMasterDetailsMap = new HashMap<String, List<NotificationAlertMasterBean>>();
		for (NotificationAlertMasterBean loNotificationAlertMasterBean : aoNotificationAlertMasterBeanList)
		{
			if (aoNotiAlertMasterDetailsMap.containsKey(loNotificationAlertMasterBean.getNotificationALertId()))
			{
				loNotificationAlertMasterBeanList = new ArrayList<NotificationAlertMasterBean>();
				loNotificationAlertMasterBeanList = aoNotiAlertMasterDetailsMap.get(loNotificationAlertMasterBean
						.getNotificationALertId());
				loNotificationAlertMasterBeanList.add(loNotificationAlertMasterBean);
				aoNotiAlertMasterDetailsMap.put(loNotificationAlertMasterBean.getNotificationALertId(),
						loNotificationAlertMasterBeanList);
			}
			else
			{
				loNotificationAlertMasterBeanList = new ArrayList<NotificationAlertMasterBean>();
				loNotificationAlertMasterBeanList.add(loNotificationAlertMasterBean);
				aoNotiAlertMasterDetailsMap.put(loNotificationAlertMasterBean.getNotificationALertId(),
						loNotificationAlertMasterBeanList);
			}
		}

		return aoNotiAlertMasterDetailsMap;
	}

	/**
	 * <ul>
	 * <li>Added Method in R4</li>
	 * <li>
	 * This method will send the email to the users</li>
	 * <li>First it will read the server name, from . host, username and
	 * password form the property file</li>
	 * <li>Then it will check for the provider name. If available then it will
	 * add it to me body</li>
	 * <li>Then it will iterate the user list add them as recipient</li>
	 * <li>Then it will create the message body and will send the mail</li>
	 * </ul>
	 * @param aoUserEmailList The list of users who will receive the mail
	 * @param asSubject Subject of the mail
	 * @param asMessage message of the mail
	 * @param asProviderName Organization name if required
	 * @return true if the send is sent successfully otherwise returns false
	 * @throws ApplicationException
	 */
	private boolean sendMail(List<UserEmailIdBean> aoUserEmailList, String asSubject, String asMessage,
			String asProviderName) throws ApplicationException
	{
		HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb.getInstance()
				.getCacheObject(ApplicationConstants.APPLICATION_SETTING);
		if (null != loApplicationSettingMap && null != loApplicationSettingMap.get("AllowNotificationEmail_Flag")
				&& !loApplicationSettingMap.get("AllowNotificationEmail_Flag").isEmpty()
				&& loApplicationSettingMap.get("AllowNotificationEmail_Flag").equalsIgnoreCase("false"))
		{
			return true;
		}
		boolean lbMailSentFlag = false;
		String lsFrom = PropertyLoader.getProperty("com.nyc.hhs.properties.email", "FROM");
		String lsHost = PropertyLoader.getProperty("com.nyc.hhs.properties.email", "SMTP_HOST_NAME");
		String lsSmtpPort = PropertyLoader.getProperty("com.nyc.hhs.properties.email", "SMTP_PORT");
		String lsUserName = PropertyLoader.getProperty("com.nyc.hhs.properties.email", "USER_NAME");
		String lsPassword = PropertyLoader.getProperty("com.nyc.hhs.properties.email", "PASSWORD");
		String lsBCCAddress = PropertyLoader.getProperty("com.nyc.hhs.properties.email", "TO_BCC_ADDRESS");
		String lsIncludeBCC = PropertyLoader.getProperty("com.nyc.hhs.properties.email", "INCLUDE_BCC");
		String lsServerName = PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE,
				HHSConstants.SERVER_NAME_FOR_PROVIDER_BATCH);
		String lsServerPort = PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE,
				HHSConstants.SERVER_PORT_FOR_PROVIDER_BATCH);
		String lsContextPath = PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE,
				HHSConstants.CONTEXT_PATH_FOR_PROVIDER_BATCH);
		String lsAppProtocol = PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE,
				HHSConstants.SERVER_PROTOCOL_FOR_PROVIDER_BATCH);
		String lsLogo = PropertyLoader.getProperty("com.nyc.hhs.properties.email", "NYC_LOGO");
		// Added in R6 for Recoup Amount Table
		String lsFYBudgetTableImage = PropertyLoader.getProperty("com.nyc.hhs.properties.email",
				HHSR5Constants.FISCAL_YEAR_BUDGET_INFO_TABLE);
		// R6 End
		Properties loProps = System.getProperties();
		loProps.put("mail.smtp.host", lsHost);
		loProps.put("mail.smtp.user", lsFrom);
		loProps.put("mail.smtp.port", lsSmtpPort);
		loProps.put("mail.smtp.auth", "true");
		loProps.put("mail.smtp.ehlo","false");
		try
		{
			Session loSession = Session.getDefaultInstance(loProps, null);
			// Create message
			MimeMessage loMessage = new MimeMessage(loSession);
			// add address
			loMessage.setFrom(new InternetAddress(lsFrom));
			if (null != lsBCCAddress && !lsBCCAddress.isEmpty())
			{
				loMessage.addRecipients(Message.RecipientType.BCC, lsBCCAddress);
			}

			// START || Changes for enhancement 5978 for Release 3.11.0
			LOG_OBJECT.Debug("lsIncludeBCC-------------" + lsIncludeBCC);
			InternetAddress loAddress = null;
			// To get the array of addresses
			for (UserEmailIdBean loUserEmailIdBean : aoUserEmailList)
			{
				if (loUserEmailIdBean != null)
				{
					LOG_OBJECT.Debug("aoUserEmailIdBean.getBccFlag()-------------" + loUserEmailIdBean.getBccFlag());
					// AddressException try catch added.
					try
					{
						loAddress = new InternetAddress(loUserEmailIdBean.getUserEmailId());
					}
					catch (AddressException loInetEx)
					{
						LOG_OBJECT.Error("Address Exception at [" + loUserEmailIdBean.getUserEmailId()
								+ "] Invalid email address!");
						continue;
					}
					if (null == loUserEmailIdBean.getBccFlag()
							|| (null != loUserEmailIdBean.getBccFlag() && loUserEmailIdBean.getBccFlag()
									.equalsIgnoreCase(HHSConstants.FALSE)))
					{
						LOG_OBJECT.Debug("aoUserEmailIdBean.getBccFlag()---false----------");
						loMessage.addRecipient(Message.RecipientType.TO, loAddress);
					}
					else
					{
						LOG_OBJECT.Debug("aoUserEmailIdBean.getBccFlag()---true----------");
						if ("yes".equalsIgnoreCase(lsIncludeBCC))
						{
							loMessage.addRecipient(Message.RecipientType.BCC, loAddress);
						}
					}
				}
			}
			// END || Changes for enhancement 5978 for Release 3.11.0

			StringBuffer loSbMessage = new StringBuffer();
			if (null != asProviderName && !"".equals(asProviderName))
			{
				loSbMessage.append(asProviderName).append("<br/><br/>");
			}
			loMessage.setSubject(asSubject);
			// Add html content
			// Specify the cid of the image to include in the email
			String lsHtml = "<html><body>";
			loSbMessage.append(lsHtml).append(asMessage).append("<br/><br/><p>Thank you</p>")
					.append("<p>HHS Accelerator Team</p>");
			loSbMessage.append("<a href=\"").append(lsAppProtocol).append("://").append(lsServerName).append(":")
					.append(lsServerPort).append("/").append(lsContextPath).append(ApplicationConstants.PORTAL_URL)
					.append(ApplicationConstants.LOGIN_PAGE_LINK);
			loSbMessage.append("\"><img src='cid:nyc-logo-image'></a>");
			/*
			 * loSbMessage .append(
			 * "<p>HHS Accelerator is the City of New York's Web-based document storage and procurement system to simplify and speed the contract process for client and community services.</p>"
			 * );
			 */
			loSbMessage.append("</body></html>");
			Multipart loMultiPart = new MimeMultipart();
			MimeBodyPart loHtmlPart = new MimeBodyPart();
			loHtmlPart.setContent(loSbMessage.toString(), "text/html; charset=utf-8");
			loMultiPart.addBodyPart(loHtmlPart);
			//[Start] R8.4.1 QC_9506 Bulk Upload Template rejecting EPINS
			try{
				String logoFileName=System.getProperty("user.dir") + lsLogo;
				File logoFile = new File (logoFileName);
				if(logoFile.exists()){
					DataSource loFds = new FileDataSource(logoFileName);
					MimeBodyPart loImagePart = new MimeBodyPart();
					loImagePart.setDataHandler(new DataHandler(loFds));
					// assign a cid to the image
					loImagePart.setHeader("Content-ID", "<nyc-logo-image>");
					loMultiPart.addBodyPart(loImagePart);
				}else{
					//not finding logo image should not stop sending email
					LOG_OBJECT.Error("Error finding logo image in Notification Service, filename:" +logoFileName);
				}
			}catch (Exception e){
				//not finding logo image should not stop sending email
				LOG_OBJECT.Error("Error while adding logo image in Notification Service", e);
			}
			// r6 Start: Added For Notification NT323,NT322
			if (asMessage.contains("Recoup-Table-image"))
			{
				try{
					String recoupTableImageFileName=System.getProperty("user.dir") + lsFYBudgetTableImage;
					File recoupTableImageFile = new File (recoupTableImageFileName);
					if(recoupTableImageFile.exists()){
						DataSource loFds1 = new FileDataSource(recoupTableImageFileName);
						MimeBodyPart loTableImagePart = new MimeBodyPart();
						loTableImagePart.setDataHandler(new DataHandler(loFds1));
						// assign a cid to the image
						loTableImagePart.setHeader("Content-ID", "<Recoup-Table-image>");
						loMultiPart.addBodyPart(loTableImagePart);
					}else{
						//not finding recoup table image should not stop sending email
						LOG_OBJECT.Error("Error finding recoup table imag in Notification Service, filename:" +recoupTableImageFile);
					}
				}catch (Exception e){
					//not finding recoup table image should not stop sending email
					LOG_OBJECT.Error("Error while adding recoup table image in Notification Service", e);
				}
			}
			//[End] R8.4.1 QC_9506 Bulk Upload Template rejecting EPINS
			
			// r6 End
			loMessage.setContent(loMultiPart);
			// Send the message
			SMTPTransport loTransport = (SMTPTransport) loSession.getTransport("smtp");
			loTransport.connect(lsHost, lsUserName, lsPassword);
			loTransport.sendMessage(loMessage, loMessage.getAllRecipients());
			LOG_OBJECT.Debug("Mail Details: Subject == " + asSubject + new java.util.Date());
			loTransport.close();
			lbMailSentFlag = true;
		}
		catch (MessagingException loExp)
		{
			lbMailSentFlag = false;
			LOG_OBJECT.Error("Error while sending Notification Mail in Notification Service", loExp);
		}
		catch (Exception loEx)
		{
			lbMailSentFlag = false;
			LOG_OBJECT.Error("Error while sending Notification Mail in Notification Service", loEx);
		}
		return lbMailSentFlag;
	}

	/**
	 * <ul>
	 * <li>Added Method in R4</li>
	 * <li>This method fetches the URL from from the Notification_alert_url
	 * table by executing the query<b>getUrlNotificationDetails</b></li>
	 * </ul>
	 * @param aoMyBatisSession Sql Session
	 * @param asNotificationUrlId URL ID corresponding to each row in
	 *            notification_alert_url Table
	 * @return the bean of the NotificationURLBean corresponding to parameter
	 *         <b>notificationIdFromEmail</b>in the URL
	 * @throws ApplicationException
	 */
	public NotificationURLBean getUrlNotificationDetails(SqlSession aoMyBatisSession, String asNotificationUrlId)
			throws ApplicationException
	{
		NotificationURLBean loNotificationURLBean = null;
		try
		{
			loNotificationURLBean = (NotificationURLBean) DAOUtil.masterDAO(aoMyBatisSession, asNotificationUrlId,
					ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, HHSConstants.GET_URL_NOTI_DETAILS,
					HHSConstants.JAVA_LANG_STRING);
			setMoState("Transaction Success: Group Notification Details fetched successfully");
		}
		catch (ApplicationException aoAppEx)
		{
			setMoState("Transaction Failure: Error Occurred while fetching notification URL details");
			throw aoAppEx;
		}
		catch (Exception loEx)
		{
			setMoState("Transaction Failure: Error Occurred while fetching notification URL details");
			throw new ApplicationException(
					"Transaction Failure: Error Occurred while fetching notification URL details", loEx);
		}
		return loNotificationURLBean;
	}

	/**
	 * Method is added in R6 for Return Payment. This method will fetch the
	 * group_notification_id of last notification inserted in group
	 * notification_table
	 * @param aoMyBatisSession
	 * @param abNotifyFlag- to check whether user needs to notified or not
	 * @return maximum notification Id
	 * @throws ApplicationException
	 */
	public String getMaxGroupNotificationId(SqlSession aoMyBatisSession, Boolean aoNotifyFlag)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered into getMaxGroupNotificationId method with parameters::" + aoNotifyFlag);
		String lsMaxUserId = null;
		if (aoNotifyFlag)
		{
			try
			{
				lsMaxUserId = (String) DAOUtil.masterDAO(aoMyBatisSession, null,
						ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER,
						HHSR5Constants.FETCH_MAX_GROUP_NOTIFICATION_ID, null);
			}
			catch (ApplicationException loEx)
			{
				LOG_OBJECT.Error("Application Exception in fetching Data", loEx);
				throw loEx;
			}
			catch (Exception loExp)
			{
				LOG_OBJECT.Error("Exception in fetching Data", loExp);
				throw new ApplicationException("Transaction Failure: Exception in fetching Data", loExp);
			}
		}
		return lsMaxUserId;

	}

	/**
	 * Method added in R6 for Return Payment. This method will update the status
	 * of new bulk_notification in payment_notification_mapping table and then
	 * insert into RequestNotificationMapping Table
	 * @param aoMyBatisSession
	 * @param asUserId- requesting user Id
	 * @param asMaxId- Maximum notification Id in group Notification table
	 * @param asRole- Selected by user from Notification Screen
	 * @return insert status
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Boolean insertNotificationRequestedDetails(SqlSession aoMyBatisSession, String asUserId, String asMaxId,
			String asRole, Boolean aoProcessStatus, Boolean aoNotifyFlag) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered into insertNotificationRequestedDetails with Parameters::" + asRole + ","
				+ aoProcessStatus + "," + asMaxId + "," + aoNotifyFlag + "," + asUserId);
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		List<ReturnedPayment> loNotificationIdList = new ArrayList<ReturnedPayment>();
		loHashMap.put(HHSConstants.CREATED_BY, asUserId);
		// Key name updated from maxcount to lastInsertedGroupNotificationId
		loHashMap.put(HHSR5Constants.LAST_INSERTED_GROUP_NOTIF_ID, asMaxId);
		String lsNotificationSessionId = null;
		Boolean loInsertStatus = Boolean.FALSE;
		if (aoNotifyFlag)
		{
			try
			{
				if (aoProcessStatus && asMaxId != null)
				{
					loNotificationIdList = (ArrayList<ReturnedPayment>) DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
							ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER,
							HHSR5Constants.FETCH_UPDATED_NOTIFICATION_ID, HHSConstants.JAVA_UTIL_HASH_MAP);
					if (loNotificationIdList != null && loNotificationIdList.size() > 0)
					{

						lsNotificationSessionId = (String) DAOUtil.masterDAO(aoMyBatisSession, null,
								ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER,
								HHSR5Constants.FETCH_NOTIFICATION_SEQUENCE_MAPPING_ID, null);
						loHashMap.put(HHSR5Constants.NOTIFICATION_SESSION_ID, lsNotificationSessionId);
						loHashMap.put(HHSConstants.STATUS_COLUMN, HHSConstants.ZERO);
						for (ReturnedPayment loGroupNotificationDetail : loNotificationIdList)
						{
							loHashMap.put(HHSConstants.GROUP_NOTIFICATION_ID,
									loGroupNotificationDetail.getGroupNotificationId());
							loHashMap.put(HHSR5Constants.BUDGET_ID_WORKFLOW, loGroupNotificationDetail.getBudgetId());
							loHashMap.put(HHSConstants.ROLE_ATT, asRole);
							loInsertStatus = (Boolean) DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
									ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER,
									HHSR5Constants.INSERT_INTO_REQUEST_NOTIFICATION_MAPPING,
									HHSConstants.JAVA_UTIL_HASH_MAP);
							LOG_OBJECT.Info("Exiting from insertNotificationRequestedDetails with status::",
									loInsertStatus);
						}
					}
				}
			}
			catch (ApplicationException loEx)
			{
				LOG_OBJECT.Error("Application Exception in Inserting details for send notification", loEx);
				throw loEx;
			}
			catch (Exception loExp)
			{
				LOG_OBJECT.Error("Exception in Inserting details for send notification", loExp);
				throw new ApplicationException("Transaction Failure: Exception in Inserting details for send notification",
						loExp);
			}
		}
		return loInsertStatus;

	}

	/**
	 * Method added in R6 for Return Payment. This method will fetch the details
	 * from Export_payment_notification for which file is not download.
	 * Notification will be in 'Not Started' state. This details will be used by
	 * R6 batch to process notifications and download file and accordingly
	 * update the status in Export_payment_notification table
	 * @param aoMyBatisSession
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings(
	{ "unchecked" })
	public List<BudgetList> getInputListForExportNotifications(SqlSession aoMyBatisSession) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered into getInputListForExportNotifications Method::");
		List<BudgetList> loBudgetLists = null;
		String lsNotificationPendingStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSR5Constants.EXPORT_NOTIFICATIONS_NOT_STARTED);
		try
		{
			loBudgetLists = (List<BudgetList>) DAOUtil.masterDAO(aoMyBatisSession, lsNotificationPendingStatus,
					ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER,
					HHSR5Constants.GET_BULK_NOTIFICATION_INPUT_DATA, HHSConstants.JAVA_LANG_STRING);
		}
		catch (ApplicationException loEx)
		{
			LOG_OBJECT.Error("Application Exception in fetching CSV Data", loEx);
			throw loEx;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception in fetching CSV Data", loExp);
			throw new ApplicationException("Transaction Failure: Exception in fetching CSV Data", loExp);
		}

		return loBudgetLists;

	}

	/**
	 * This method is added in R6 for return Payment. This method will populate
	 * the data in csv file for bulk export task.
	 * @param aoMyBatisSession
	 * @param aoBulkNotificationDataList List of Budget
	 * @param asDirectoryId Request Id
	 * @param asOrgName Organisation Name
	 * @return Map containing filename and request download Status
	 * @throws Exception
	 */
	@SuppressWarnings(
	{ "rawtypes"})
	public Map downloadBulkExportFile(List<BulkNotificationList> aoBulkNotificationDataList, String asDirectoryId,
			String asOrgName) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered into downloadBulkExportFile with parameters Id::" + asOrgName + ",requestId" + asDirectoryId);
		String lsStatus = null;
		Map<String, String> loStatusMap = new HashMap<String, String>();

		// get Export File Data
		try
		{
			if (null != aoBulkNotificationDataList && !aoBulkNotificationDataList.isEmpty())
			{
				loStatusMap = processBulkNotificationList(
						aoBulkNotificationDataList, loStatusMap, asDirectoryId, asOrgName);
			}
			else
			{
				lsStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSR5Constants.EXPORT_NOTIFICATIONS_EMPTY);
				loStatusMap.put(HHSR5Constants.DOWNLOAD_STATUS, lsStatus);
			}
		}
		catch (ApplicationException loEx)
		{
			LOG_OBJECT.Error("Application Exception in creating CSV File", loEx);
			throw loEx;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception in creating CSV File", loExp);
			throw new ApplicationException("Transaction Failure: Exception in creating CSV File", loExp);
		}
		return loStatusMap;
	}

	/**
	 * This method is added in R6 as a part of Returned Payment Notification Export.
	 * This method will create the CSV file and return the map containing file name and 
	 * timestamp of file creation. 
	 * @param aoBulkNotificationDataList
	 * @param loStatusMap
	 * @param asDirectoryId
	 * @param asOrgName
	 * @return
	 * @throws ApplicationException
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	private Map<String, String> processBulkNotificationList(
			List<BulkNotificationList> aoBulkNotificationDataList, Map<String, String> loStatusMap,
			String asDirectoryId, String asOrgName) throws ApplicationException, IOException
	{
		Map<String, BulkNotificationList> loFinalOutputMap = new HashMap<String, BulkNotificationList>();
		StringBuffer loHeader = new StringBuffer();
		StringBuffer loStringBuff = new StringBuffer();
		DateFormat loDateFormat = new SimpleDateFormat("MMddyyyy-hhmmss");
		String lsDateFormat = loDateFormat.format(new Date());
		Iterator loHeaderListItr = HHSR5Constants.HEADER_FOR_BULK_NOTIFICATION.iterator();
		// File Header
		while (loHeaderListItr.hasNext())
		{
			loHeader.append(loHeaderListItr.next());
			if (loHeaderListItr.hasNext())
			{
				loHeader.append(HHSConstants.COMMA);
			}
		}
		// File data
		loFinalOutputMap = getUniqueProviderListforNotification(aoBulkNotificationDataList, loFinalOutputMap);
		for (Map.Entry<String, BulkNotificationList> loOutputMap : loFinalOutputMap.entrySet())
		{
			String lsCityNum = loOutputMap.getValue().getCtNumber();
			if (lsCityNum == null || lsCityNum.isEmpty())
			{
				loOutputMap.getValue().setCtNumber(HHSR5Constants.NOT_REGISTERED);
			}
			loStringBuff.append(loOutputMap.getValue().toString());
			loStringBuff.append(HHSR5Constants.LINE_SEPRATOR);
		}
		loHeader.append(HHSR5Constants.LINE_SEPRATOR);
		loHeader.append(loStringBuff);
		String loFilePath = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
				HHSConstants.BULK_UPLOAD_ABSOLUTE_PATH);
		File loTempDir = new File(loFilePath);
		if (!loTempDir.exists())
		{
			loTempDir.mkdirs();
		}
		String lsFileName = asDirectoryId + HHSR5Constants.DELIMETER_SIGN + HHSR5Constants.NOTIFICATION_EXPORT
				+ HHSR5Constants.UNDERSCORE + asOrgName + HHSR5Constants.UNDERSCORE + lsDateFormat
				+ HHSR5Constants.CSV_CONSTANT;
		writeTaskExportToFile(loFilePath + lsFileName , loHeader);
		loStatusMap.put(HHSR5Constants.DOWNLOAD_STATUS, PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSR5Constants.EXPORT_NOTIFICATIONS_FINISHED));
		loStatusMap.put(HHSR5Constants.FILE_NAME, lsFileName);
		return loStatusMap;
	}
	
	/**
	 * This method is added in R6 as a part of Return Payment Notif Export.
	 * This method will merge the CSV data on the basis of contractId, groupNotificationId,
	 * notificationDate and organizationId of provider.
	 * @param aoBulkNotificationDataList
	 * @param loFinalOutputMap
	 * @return the semicolon separated provider email id.
	 */
	private Map<String, BulkNotificationList> getUniqueProviderListforNotification(List<BulkNotificationList> aoBulkNotificationDataList,
			Map<String, BulkNotificationList> loFinalOutputMap)
	{
		for (BulkNotificationList loBulkNotification : aoBulkNotificationDataList)
		{
			//Start: defect 8604 groupNotificationId check added
			if (null == (loFinalOutputMap.get(loBulkNotification.getContractId()
					+ loBulkNotification.getNotificationSent() + loBulkNotification.getProviderOrgId()+loBulkNotification.getGroupNotificationId())))
			{
				loFinalOutputMap.put(loBulkNotification.getContractId() + loBulkNotification.getNotificationSent()
						+ loBulkNotification.getProviderOrgId()+loBulkNotification.getGroupNotificationId(), loBulkNotification);
			}
			else
			{
				BulkNotificationList loExistingBean = loFinalOutputMap.get(loBulkNotification.getContractId()
						+ loBulkNotification.getNotificationSent() + loBulkNotification.getProviderOrgId()+loBulkNotification.getGroupNotificationId());
				//End: defect 8604
				loExistingBean.setProviderName(loExistingBean.getProviderName()
						+ HHSR5Constants.DELIMETER_SEMICOLON_SPACE + loBulkNotification.getProviderName());
				loFinalOutputMap.put(loBulkNotification.getContractId() + loBulkNotification.getNotificationSent()
						+ loBulkNotification.getProviderOrgId(), loExistingBean);
			}
		}
		return loFinalOutputMap;
	}
	/**
	 * Method added in R6 for return Payment. This method is used to write
	 * notifications in File.
	 * 
	 * @param asFilename String
	 * @param aoExportData StringBuffer
	 * @throws IOException If an Application Exception occurs
	 */
	private void writeTaskExportToFile(String asFilename, StringBuffer aoExportData) throws IOException
	{
		BufferedWriter loBufferedWriter = new BufferedWriter(new FileWriter(asFilename));
		loBufferedWriter.write(aoExportData.toString());
		loBufferedWriter.flush();
		loBufferedWriter.close();
	}

	/**
	 * This method added in R6 for Return Payment. This method is added to
	 * process notifications for send bulk notification screen
	 * @param aoMyBatisSession
	 * @param aoNotificationBeanList contains the budget details
	 * @param asUserId
	 * @param asProgramName
	 * @param asFiscalYear
	 * @param aoHashMap contains the notification Id and Alert Id
	 * @param asNotificationId
	 * @param asUserOrg
	 * @param aoNotifyFlag
	 * @return Process notification status as Boolean
	 * @throws Exception
	 */
	@SuppressWarnings(
	{ "unchecked", "unused" })
	public Boolean processBulkNotification(SqlSession aoMyBatisSession, List<BudgetList> aoNotificationBeanList,
			String asUserId, String asProgramName, String asFiscalYear, HashMap<String, Object> aoHashMap,
			String asNotificationId, String asUserOrg, Boolean aoNotifyFlag) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered into processBulkNotification with parameters::" + asUserId + "," + asNotificationId
				+ "," + asProgramName + "," + asFiscalYear + "," + aoHashMap + "," + asUserOrg + "," + aoNotifyFlag);
		Boolean loProcessStatus = Boolean.FALSE;
		if (aoNotifyFlag)
		{
			String lsActionSelected = (String) aoHashMap.get(HHSR5Constants.ACTION_SELECTED);
			try
			{
				if (aoNotificationBeanList != null && !aoNotificationBeanList.isEmpty())
				{
					Integer liSize = aoNotificationBeanList.size();
					NotificationDataBean loNotificationAL322 = new NotificationDataBean();
					List<String> loOrgId = new ArrayList<String>();
					for (BudgetList loBudgetListBean : aoNotificationBeanList)
					{
						if (!loOrgId.isEmpty())
						{
							loOrgId.clear();
						}
						aoHashMap.put(ApplicationConstants.ENTITY_ID, loBudgetListBean.getBudgetId());
						loOrgId.add(loBudgetListBean.getOrgId());
						loNotificationAL322.setProviderList(loOrgId);
						updateRequestMap(aoHashMap, loBudgetListBean, asProgramName, asFiscalYear, asNotificationId, asUserOrg, aoNotificationBeanList);
						//Start:Added for notification link snapshot
						HashMap<String, String> loLinkMap = new HashMap<String, String>();
						StringBuffer loSbMessageUrl = createBulkNotificationMessageURL(loBudgetListBean);
						String lsServerName = PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE,
								HHSConstants.PROP_CITY_URL);
						StringBuffer loSbMessageUrlAgencySetting = new StringBuffer(lsServerName);
						loSbMessageUrlAgencySetting.append(HHSConstants.AGENCY_SETTING_CITY);
						loSbMessageUrlAgencySetting.append(HHSR5Constants.AGENCY_SETTING_TAB_URL);
						loSbMessageUrlAgencySetting.append(HHSR5Constants.BULK_NOTIFICATIONS);
						loLinkMap.put(HHSConstants.LINK,loSbMessageUrl.toString());
						loLinkMap.put(HHSConstants.LINK1,loSbMessageUrlAgencySetting.toString());
						
						loNotificationAL322.setLinkMap(loLinkMap);
						//End:Added for notification link snapshot
						// Start:user specific check added for notifications
						decideNotificationAndAlertID(asNotificationId, aoHashMap, loNotificationAL322);
						// End:
						loProcessStatus = processNotification(aoMyBatisSession, aoHashMap, true);
					}
					if (loProcessStatus
							&& (null != lsActionSelected && lsActionSelected
									.equalsIgnoreCase(HHSConstants.SEND_NOTFICATION)))
					{
						LOG_OBJECT.Info("Notification processed successfully for bulk notification request::");
						List<String> loNotificationList = (List<String>) aoHashMap
								.get(HHSConstants.NOTIFICATION_ALERT_ID);
						loNotificationList.clear();
						loNotificationList.add(HHSR5Constants.NT325);
						aoHashMap.put(HHSR5Constants.NT325, loNotificationAL322);
						processNotification(aoMyBatisSession, aoHashMap, true);
					}
				}
				else if (null != lsActionSelected && lsActionSelected.equalsIgnoreCase(HHSConstants.SEND_NOTFICATION))
				{
					LOG_OBJECT.Info("Entered into processBulkNotification For NT325 No Budget::");
					NotificationBean loNotificationBean = new NotificationBean();
					loNotificationBean.setCreatedBy(asUserId);
					loNotificationBean.setOrgType(asUserOrg);
					loNotificationBean.setTotalCount(HHSConstants.ZERO);
					loNotificationBean.setNotificationCount(HHSConstants.ZERO);
					loNotificationBean.setEntityId(asFiscalYear);
					loNotificationBean.setGroupName(asProgramName);
					loProcessStatus = processBulkAcknowledgement(aoMyBatisSession, loNotificationBean, true);
					LOG_OBJECT.Info("Exiting from processBulkNotification For NT325 No Budget with status::",
							loProcessStatus);
				}
			}
			catch (ApplicationException loExp)
			{
				LOG_OBJECT.Error("Application Exception in Processing notifications", loExp);
				throw loExp;
			}

			catch (Exception loEx)
			{
				LOG_OBJECT.Error("Exception in Processing notifications", loEx);
				throw new ApplicationException("Transaction Failure: Exception in Processing notifications", loEx);
			}
		}
		return loProcessStatus;
	}
	
	/**
	 * The method is added in Release 6.
	 * It will update the map required to send notification.
	 * @param aoHashMap required map
	 * @param aoBudgetListBean
	 * @param asProgramName
	 * @param asFiscalYear
	 * @param asNotificationId
	 * @param asUserOrg
	 * @param aoNotificationBeanList
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	private void updateRequestMap(HashMap<String, Object> aoHashMap, BudgetList aoBudgetListBean, String asProgramName, String asFiscalYear,
			String asNotificationId, String asUserOrg, List<BudgetList> aoNotificationBeanList) throws ApplicationException
	{
		HashMap<String, String> loRequestMap = (HashMap<String, String>) aoHashMap
				.get(TransactionConstants.REQUEST_MAP_PARAMETER_NAME);
		TreeSet<String> loTreeSet = FileNetOperationsUtils.getNYCAgencyListFromDB();
		Integer liSize = aoNotificationBeanList.size();
		String lsAgencyUser = HHSConstants.EMPTY_STRING;
		String lsCtNum = aoBudgetListBean.getCtId();
		if (null == lsCtNum || lsCtNum.isEmpty())
		{
			aoBudgetListBean.setCtId(HHSR5Constants.NOT_REGISTERED);
		}
		loRequestMap.put(HHSConstants.NT_CT, aoBudgetListBean.getCtId());
		loRequestMap.put(HHSR5Constants.BUDGET_ID_HASH_KEY, aoBudgetListBean.getBudgetId());
		String lsAmount = aoBudgetListBean.getUnRecoupAmount();
		// added for defect 8544
		if (asNotificationId.contains(HHSR5Constants.NT321)
				&& null != aoHashMap.get(HHSR5Constants.REMITTANCE_AMOUNT)
				&& !((String) aoHashMap.get(HHSR5Constants.REMITTANCE_AMOUNT))
						.equals(HHSConstants.EMPTY_STRING))
		{
			lsAmount = (String) aoHashMap.get(HHSR5Constants.REMITTANCE_AMOUNT);
		}
		else
		{
			lsAmount = HHSConstants.ZERO;
		}
		// defect 8544 End
		loRequestMap.put(HHSConstants.AMOUNT, lsAmount);
		loRequestMap.put(HHSR5Constants.FISCAL_YEAR_CAPS, asFiscalYear);
		loRequestMap.put(HHSConstants.BUDGET_CONTRACT_TITLE, aoBudgetListBean.getContractTitle());
		loRequestMap.put(HHSConstants.ORGANIZATION_LEGAL_NAME, aoBudgetListBean.getProviderName());
		loRequestMap.put(HHSConstants.PROGRAM_NAME, asProgramName);
		loRequestMap.put(HHSConstants.AGENCYID, asUserOrg);
		loRequestMap.put(HHSR5Constants.TOTAL_BUDGET, liSize.toString());
		// naming convention
		Iterator<String> loItrAgencyList = loTreeSet.iterator();
		while (loItrAgencyList.hasNext())
		{
			String loUserOrg = loItrAgencyList.next();
			if (loUserOrg.split(HHSConstants.DELIMETER_SIGN)[0].trim().equalsIgnoreCase(asUserOrg))
			{
				lsAgencyUser = loUserOrg.split(HHSConstants.HYPHEN)[1];
				break;
			}
		}
		loRequestMap.put(HHSConstants.USER_ORG, lsAgencyUser.trim());
		//StringBuffer loSbMessageUrl = createBulkNotificationMessageURL(aoBudgetListBean);
		// url for NT325
		String lsServerName = PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE,
				HHSConstants.PROP_CITY_URL);
		/*String loSbMessageUrlAgencySetting = lsServerName + HHSConstants.AGENCY_SETTING_CITY +
				HHSR5Constants.AGENCY_SETTING_TAB_URL + HHSR5Constants.BULK_NOTIFICATIONS;*/
		// url for contract budget
		//loRequestMap.put(HHSConstants.LINK1, loSbMessageUrlAgencySetting);
		//loRequestMap.put(HHSConstants.LINK, loSbMessageUrl.toString());
		loRequestMap.put(HHSR5Constants.RETURNED_PAYMENT_DESCRIPTION,
				(String) aoHashMap.get(HHSR5Constants.RETURNED_PAYMENT_DESCRIPTION));
	}
	
	/**
	 * The method is added in Release 6.
	 * It will create url for sending bulk notification.
	 * @param aoBudgetListBean
	 * @return url
	 * @throws ApplicationException
	 */
	private StringBuffer createBulkNotificationMessageURL(BudgetList aoBudgetListBean) throws ApplicationException
	{
		StringBuffer loSbMessageUrl = new StringBuffer();
		String lsServerNameProvider = PropertyLoader.getProperty(HHSP8Constants.PROPERTY_FILE,
				HHSP8Constants.SERVER_NAME_FOR_PROVIDER_BATCH);
		String lsServerPort = PropertyLoader.getProperty(HHSP8Constants.PROPERTY_FILE,
				HHSP8Constants.SERVER_PORT_FOR_PROVIDER_BATCH);
		String lsContextPath = PropertyLoader.getProperty(HHSP8Constants.PROPERTY_FILE,
				HHSP8Constants.CONTEXT_PATH_FOR_PROVIDER_BATCH);
		String lsAppProtocol = PropertyLoader.getProperty(HHSP8Constants.PROPERTY_FILE,
				HHSP8Constants.SERVER_PROTOCOL_FOR_PROVIDER_BATCH);
		loSbMessageUrl.append(lsAppProtocol);
		loSbMessageUrl.append("://");
		loSbMessageUrl.append(lsServerNameProvider);
		loSbMessageUrl.append(":");
		loSbMessageUrl.append(lsServerPort);
		loSbMessageUrl.append("/");
		loSbMessageUrl.append(lsContextPath);
		loSbMessageUrl
				// .append(lsServerName)
				.append(ApplicationConstants.PORTAL_URL)
				.append(HHSConstants.PAGE_LABEL_PORTLET_URL + HHSConstants.CONTRACT_ID_URL
						+ aoBudgetListBean.getContractId())
				.append(HHSConstants.BUDGET_ID_URL + aoBudgetListBean.getBudgetId())
				.append(HHSConstants.FISCAL_YEAR_ID_URL + aoBudgetListBean.getFiscalYear())
				.append(HHSConstants.BUDGET_TYPE_URL + HHSConstants.BUDGET_TYPE3)
				.append(HHSR5Constants.FINANCIAL_BUDGET_TAB);
		return loSbMessageUrl;
	}

	/**
	 * The method is added in Release 6.
	 * It will decide the notification id and alert id to be sent.
	 * @param asNotificationId
	 * @param aoHashMap
	 * @param aoNotificationData
	 * @return
	 */
	private HashMap<String, Object> decideNotificationAndAlertID(String asNotificationId,
			HashMap<String, Object> aoHashMap, NotificationDataBean aoNotificationData)
	{
		if (asNotificationId.equalsIgnoreCase(HHSR5Constants.NT322))
		{
			aoHashMap.put(HHSR5Constants.NT322, aoNotificationData);
			aoHashMap.put(HHSR5Constants.AL322, aoNotificationData);
		}
		else if (HHSR5Constants.NT321.equalsIgnoreCase(asNotificationId))
		{
			aoHashMap.put(HHSR5Constants.NT321, aoNotificationData);
			aoHashMap.put(HHSR5Constants.AL321, aoNotificationData);
		}
		else if (HHSR5Constants.NT321_EDL2.equalsIgnoreCase(asNotificationId))
		{
			aoHashMap.put(HHSR5Constants.NT321_EDL2, aoNotificationData);
			aoHashMap.put(HHSR5Constants.AL321_EDL2, aoNotificationData);
		}
		else if (HHSR5Constants.NT321_L2.equalsIgnoreCase(asNotificationId))
		{
			aoHashMap.put(HHSR5Constants.NT321_L2, aoNotificationData);
			aoHashMap.put(HHSR5Constants.AL321_L2, aoNotificationData);
		}
		else if (HHSR5Constants.NT323.equalsIgnoreCase(asNotificationId))
		{
			aoHashMap.put(HHSR5Constants.NT323, aoNotificationData);
			aoHashMap.put(HHSR5Constants.AL323, aoNotificationData);
		}
		else if (HHSR5Constants.NT323_L2.equalsIgnoreCase(asNotificationId))
		{
			aoHashMap.put(HHSR5Constants.NT323_L2, aoNotificationData);
			aoHashMap.put(HHSR5Constants.AL323_L2, aoNotificationData);
		}
		else if (HHSR5Constants.NT323_ED.equalsIgnoreCase(asNotificationId))
		{
			aoHashMap.put(HHSR5Constants.NT323_ED, aoNotificationData);
			aoHashMap.put(HHSR5Constants.AL323_ED, aoNotificationData);
		}
		return aoHashMap;
	}
	
	/**
	 * This method is added in R6 For Return Payment to process Notification
	 * NT325. This method will process the notifications for 0 budget criteria.
	 * @param aoMyBatisSession
	 * @param aoNotificationBean contains Notification details
	 * @param aoNotificationListStatus
	 * @return process notification status
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private Boolean processBulkAcknowledgement(SqlSession aoMyBatisSession, NotificationBean aoNotificationBean,
			Boolean aoNotificationListStatus) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered into processBulkAcknowledgement with parameters::" + aoNotificationBean.toString()
				+ "," + aoNotificationListStatus);
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		HashMap<String, String> loLinkMap = new HashMap<String, String>();
		boolean lbStatus = Boolean.FALSE;
		List<String> loNotificationAlertList = new ArrayList<String>();
		loNotificationAlertList.add(HHSR5Constants.NT325);
		loNotificationMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		NotificationDataBean loNotificationNL325 = new NotificationDataBean();
		List<String> loOrgId = new ArrayList<String>();
		String lsUserId;
		try
		{
			TreeSet<String> loTreeSet = new TreeSet<String>();
			loTreeSet = FileNetOperationsUtils.getNYCAgencyListFromDB();
			StringBuffer loSbMessageUrlAgencySetting = new StringBuffer();
			String lsAgencyUser = HHSConstants.EMPTY_STRING;
			lsUserId = aoNotificationBean.getCreatedBy();
			loOrgId.add(aoNotificationBean.getCreatedBy());
			loNotificationNL325.setProviderList(loOrgId);
			// Set Input param
			
			loNotificationMap.put(HHSConstants.CREATED_BY_USER_ID, lsUserId);
			loNotificationMap.put(HHSConstants.MODIFIED_BY, lsUserId);
			loNotificationMap.put(ApplicationConstants.ENTITY_ID, aoNotificationBean.getEntityId());
			loNotificationMap.put(ApplicationConstants.ENTITY_TYPE, HHSConstants.BUDGET);
			String lsServerName = PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE,
					HHSConstants.PROP_CITY_URL);
			loSbMessageUrlAgencySetting.append(lsServerName).append(HHSConstants.AGENCY_SETTING_CITY)
					.append(HHSR5Constants.AGENCY_SETTING_TAB_URL + HHSR5Constants.BULK_NOTIFICATIONS);
			// Get Agency Name
			String lsOrgName = aoNotificationBean.getOrgType();
			Iterator<String> loItrAgencyList = loTreeSet.iterator();
			while (loItrAgencyList.hasNext())
			{
				String loUserOrg = loItrAgencyList.next();
				if (loUserOrg.split(HHSConstants.DELIMETER_SIGN)[0].trim().equalsIgnoreCase(lsOrgName))
				{
					lsAgencyUser = loUserOrg.split(HHSConstants.HYPHEN)[1];
					break;
				}
			}
			Integer loNotificationCount = Integer.parseInt(aoNotificationBean.getNotificationCount());
			// Request Map
			loRequestMap.put(HHSConstants.PROGRAM_NAME, aoNotificationBean.getGroupName());
			loRequestMap.put(HHSR5Constants.FISCAL_YEAR_CAPS, aoNotificationBean.getEntityId());
			//loRequestMap.put(HHSConstants.LINK1, loSbMessageUrlAgencySetting.toString());
			//Start:Added for notification link snapshot
			loLinkMap.put(HHSConstants.LINK1, loSbMessageUrlAgencySetting.toString());
			loNotificationNL325.setLinkMap(loLinkMap);
			//End:Added for notification link snapshot
			loNotificationMap.put(HHSR5Constants.NT325, loNotificationNL325);
			loRequestMap.put(HHSR5Constants.TOTAL_BUDGET, aoNotificationBean.getTotalCount());
			loRequestMap.put(HHSConstants.USER_ORG, lsAgencyUser.trim());
			loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
			if (loNotificationCount != null && loNotificationCount == 0)
			{
				lbStatus = processNotification(aoMyBatisSession, loNotificationMap, true);
				LOG_OBJECT.Info("Exiting from processBulkAcknowledgement with status::", lbStatus);
			}
		}
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("Application Exception in Processing notifications", loExp);
			throw loExp;
		}

		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception in Processing notifications", loExp);
			throw new ApplicationException("Transaction Failure: Exception in Processing notifications", loExp);
		}
		return lbStatus;
	}
	
	//** [Start] QC 8963 R 8.10 AL311 provider notification alert missing agency name
		/**
		 * Added in Release 8.10.0
		 * The method will return Agency name
		 * @param aoMyBatisSession
		 * @param aoBudgetId
		 * @return agencyName
		 * @throws ApplicationException
		 */
		public String fetchAgencyNameFromBudget(SqlSession aoMyBatisSession, String aoEntityId) throws ApplicationException
		{
			LOG_OBJECT.Debug("Entered NotificationService.fetchAgencyNameFromBudget ::: "+aoEntityId);
			String agencyName = HHSConstants.EMPTY_STRING;
			try
			{
				agencyName = (String) DAOUtil.masterDAO(aoMyBatisSession, aoEntityId,
						ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, HHSConstants.FETCH_AGENCY_NAME_FROM_BUDGET,
						HHSConstants.JAVA_LANG_STRING);
			}
			catch (ApplicationException aoAppex)
			{
				setMoState("Error while NotificationService.fetchAgencyNameFromBudget()");
				throw aoAppex;
			}
			LOG_OBJECT.Debug("Exited NotificationService.fetchAgencyNameFromBudget()::: "+agencyName);
			
			return agencyName;
		}
		
		//** [End] QC 8963 R 8.10 AL311 provider notification alert missing agency name
		
}
