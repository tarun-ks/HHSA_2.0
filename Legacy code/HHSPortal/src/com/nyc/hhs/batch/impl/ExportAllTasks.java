package com.nyc.hhs.batch.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.batch.IBatchQueue;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSP8Constants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.constants.TransactionConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.AgencyTaskBean;
import com.nyc.hhs.model.NotificationDataBean;
import com.nyc.hhs.service.filenetmanager.p8services.P8HelperServices;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.PropertyLoader;

public class ExportAllTasks extends P8HelperServices implements IBatchQueue
{
	private static final LogInfo LOG_OBJECT = new LogInfo(ExportAllTasks.class);


	
	/**
	 * Blank implementation of getQueue method of interface
	 * 
	 * @param aoMParameters HashMap of Parameter
	 * @return null
	 */
	@SuppressWarnings("rawtypes")
	public List getQueue(Map aoMParameters)
	{
		return null;
	}
	/**
	 * This method is used to execute Queue
	 * <ul>
	 * <li>
	 * setData in channel object</li>
	 * <li>Transaction called is <b>getExportTaskList<b></li>
	 * </ul>
	 * @param aoLQueue List
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public void executeQueue(List aoLQueue) throws ApplicationException
	{
		SqlSession loFilenetPEDBSession = null;
		HashMap loExportDetail=null;
		List<String> loTaskTypeList =null;
		String lsUserRole=null;
		String lsUserOrgType=null;
		String lsUserOrg=null;
		String lsAgencyId=null;
		String lsExportRequestId=null;
		String lsDateFormat = null;
		List<HashMap> loHMExportList = new ArrayList<HashMap>();
		Channel loChannel = new Channel();
		try
		{
			P8UserSession loFilenetSession = filenetConnection.setP8SessionVariables();
			loFilenetPEDBSession = HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory().openSession();
			loFilenetSession.setFilenetPEDBSession(loFilenetPEDBSession);
			
			loChannel.setData("lsStatusId", "183");

			HHSTransactionManager.executeTransaction(loChannel, "getExportTaskList",
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			loHMExportList = (List<HashMap>) loChannel.getData("loExportTaskList");
			
		    /*[Start] R8.7.0 QC9555 Performance Turning
			 * 1. no open DB connection at the same time.
			 * 2. fetch/load target data only from Invoice data and user data(Provider and City user) 
			 * */
			System.out.println("---------------[Export all Task]: starts   "   );
			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.FETCH_INVOICE_USER_LIST_TASK_EXPORT,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			Map<String, List<AgencyTaskBean>> loMap = (HashMap<String, List<AgencyTaskBean>>) loChannel.getData(HHSR5Constants.PARAM_LIST_FOR_TASK_EXPORT);
			List<AgencyTaskBean> loUserNameLst = loMap.get(HHSP8Constants.PARAM_USER_NAME_LIST);
			List<AgencyTaskBean> loInvSrtEndDateLst  = loMap.get(HHSP8Constants.PARAM_INVOICE_SRT_END_DATE_LIST);

		    HashMap <String, AgencyTaskBean> loUserMap = convertUsrList2Map( loUserNameLst);
		    HashMap <String, AgencyTaskBean> loInvMap = convertInvList2Map( loInvSrtEndDateLst );
			System.out.println("---------------[Export all Task]: User List:" + loUserNameLst.size() + "  && Invoice Srt&End date: " + loInvSrtEndDateLst.size() );
			
			/*
			 * [End] R8.7.0 QC9555 Performance Turning  
			 * */
			
			if(null!=loHMExportList)
			{
			Iterator loItr = loHMExportList.iterator();

			while (loItr.hasNext())
			{
				loExportDetail=(HashMap)loItr.next();
				 lsUserRole=(String)loExportDetail.get("USER_ROLE");
				 lsUserOrgType=(String)loExportDetail.get("USER_ORG_TYPE");
				 lsUserOrg=(String)loExportDetail.get("ORG_TYPE");
				 lsExportRequestId = loExportDetail.get("EXPORT_REQUEST_ID").toString();
				 loExportDetail.get("CREATED_DATE").toString();
				 DateFormat dateFormat2 = new SimpleDateFormat("MMddyyyy-hhmmss");
				 SimpleDateFormat loDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
				 lsDateFormat= dateFormat2.format(loDateFormat.parse((loExportDetail.get("CREATED_DATE").toString())));
				 
				 lsAgencyId=lsUserOrgType;
				loTaskTypeList = new ArrayList<String>();
				if (null != lsUserRole
						&& !lsUserRole.isEmpty()
						&& lsUserOrg.equalsIgnoreCase(HHSR5Constants.USER_AGENCY)
						&& (lsUserRole.equalsIgnoreCase(HHSR5Constants.ACCO_STAFF_ROLE)
								|| lsUserRole.equalsIgnoreCase(HHSR5Constants.ACCO_ADMIN_STAFF_ROLE) || lsUserRole
									.equalsIgnoreCase(HHSR5Constants.ACCO_MANAGER_ROLE)))
				{
					getTaskTypesForAcco(loTaskTypeList);
				}
				else if (null != lsUserRole
						&& !lsUserRole.isEmpty()
						&& lsUserOrg.equalsIgnoreCase(HHSR5Constants.USER_AGENCY)
						&& (lsUserRole.equalsIgnoreCase(HHSR5Constants.PROGRAM_STAFF_ROLE)
								|| lsUserRole.equalsIgnoreCase(HHSR5Constants.PROGRAM_ADMIN_STAFF_ROLE)
								|| lsUserRole.equalsIgnoreCase(HHSR5Constants.PROGRAM_MANAGER_ROLE)
								|| lsUserRole.equalsIgnoreCase(HHSR5Constants.FINANCE_STAFF_ROLE)
								|| lsUserRole.equalsIgnoreCase(HHSR5Constants.FINANCE_ADMIN_STAFF_ROLE) || lsUserRole
									.equalsIgnoreCase(HHSR5Constants.FINANCE_MANAGER_ROLE)
								|| lsUserRole.equalsIgnoreCase(HHSR5Constants.CFO_ROLE)))
				{
					getTaskTypesForFinance(loTaskTypeList);
				}
				else
				{
					if (lsUserOrg.equalsIgnoreCase(HHSR5Constants.USER_CITY))
					{
						lsAgencyId=HHSConstants.USER_CITY;
						getTaskTypesForCity(loTaskTypeList);
					}
					else
					{
						getTaskTypesForAcco(loTaskTypeList);
					}

				}
				//exportTaskList(loFilenetSession,loTaskTypeList, lsAgencyId,lsExportRequestId,lsDateFormat, loUserNameLst, loInvSrtEndDateLst  );
				exportTaskList(loFilenetSession,loTaskTypeList, lsAgencyId,lsExportRequestId,lsDateFormat, loUserMap, loInvMap  );

			}
			}
		}
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.printStackTrace();
			LOG_OBJECT.Error("Exception in  executeQueue::" + aoAppEx.getMessage());
			throw aoAppEx;
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (null != loFilenetPEDBSession)
			{
				loFilenetPEDBSession.close();
			}
		}
	}
	/**
	 * This method is used to export Task List
	 * <ul>
	 * <li>
	 * Set loAgencyTaskBean, asAgencyId,  in aoAgencyTaskBean</li>
	 * <li>Transaction called is <b>fetchAgencyTaskListExport<b></li>
	 * </ul>
	 * @param P8UserSession aoUserSession
	 * @param aoTaskTypeList List<String>
	 * @param asAgencyId String
	 * @param asExportRequestId String
	 * @param asDateFormat String
	 */
/*	public void exportTaskList(P8UserSession aoUserSession, List<String> aoTaskTypeList, 
			String asAgencyId,String asExportRequestId,String asDateFormat ,  List<AgencyTaskBean> aoUserNameLst  , List<AgencyTaskBean> aoInvSrtEndDateLst )
*/
	public void exportTaskList(P8UserSession aoUserSession, List<String> aoTaskTypeList, 
		String asAgencyId,String asExportRequestId,String asDateFormat ,  HashMap <String, AgencyTaskBean> loUserMap  , HashMap <String, AgencyTaskBean> loInvMap )	{
		try
		{
			String lsFileName=null;
			String lsFilePath=null;
			String lsSubFileName=asAgencyId;
			List<String> loNotificationAlertList = new ArrayList<String>();
			HashMap<String, Object> loHmNotifyParam = new HashMap<String, Object>();
			Channel loChannel = new Channel();
			List<AgencyTaskBean> loAgencyTaskBeanList;
			StringBuffer loHeader = new StringBuffer();
			AgencyTaskBean aoAgencyTaskBean = new AgencyTaskBean();
			HashMap<String, Object> loFilterProp = new HashMap<String, Object>();
			Iterator loHeaderListItr = HHSR5Constants.HEADER_FOR_TASK_LIST_EXPORT_ACCO.iterator();
			if(asAgencyId.equalsIgnoreCase(HHSConstants.USER_CITY))
			{
				lsSubFileName="HHS";
			}
			
			while (loHeaderListItr.hasNext())
			{
				loHeader.append(loHeaderListItr.next());
				if (loHeaderListItr.hasNext())
				{
					loHeader.append(HHSConstants.COMMA);
				}
			}
			loFilterProp.put(HHSR5Constants.PROPERTY_PE_AGENCY_ID, asAgencyId);
			loFilterProp.put(HHSR5Constants.PROPERTY_PE_TASK_TYPE, aoTaskTypeList);
			aoAgencyTaskBean.setAgencyId(asAgencyId);
			aoAgencyTaskBean.setFilterProp(loFilterProp);
			aoAgencyTaskBean.setOrderBy("\"LaunchDate\" DESC, lower(\"TaskType\") ASC");
			aoAgencyTaskBean.setTaskNameList(aoTaskTypeList);
			loChannel.setData(HHSConstants.AO_FILENET_SESSION, aoUserSession);
			loChannel.setData(HHSConstants.AGENCY_TASK_BEAN, aoAgencyTaskBean);
			
			/*[Start] R8.7.0 QC9555*/ 
			loChannel.setData(HHSP8Constants.PARAM_USER_NAME_LIST, loUserMap);
			loChannel.setData(HHSP8Constants.PARAM_INVOICE_SRT_END_DATE_LIST, loInvMap);

/*			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.FETCH_AGENCY_TASK_LIST_EXPORT,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);*/

			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.FETCH_AGENCY_TASK_LIST_FOR_EXPORT,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			/*[End]   R8.7.0 QC9555 */ 

			loAgencyTaskBeanList = (List<AgencyTaskBean>) loChannel.getData(HHSConstants.AGENCY_TASK_LIST);
			Iterator<AgencyTaskBean> loListItr = loAgencyTaskBeanList.iterator();
			StringBuffer loStringBuff = new StringBuffer();
			while (loListItr.hasNext())
			{
				AgencyTaskBean loBean = (AgencyTaskBean) loListItr.next();
				loStringBuff.append(loBean.toStringForExport());
				if (loListItr.hasNext())
				{
					loStringBuff.append(HHSR5Constants.LINE_SEPRATOR);
				}
			}
			loHeader.append(HHSR5Constants.LINE_SEPRATOR);
			loHeader.append(loStringBuff);
			lsFileName=HHSR5Constants.TASK_EXPORT_FILE_NAME + HHSR5Constants.UNDERSCORE
			+ lsSubFileName + HHSR5Constants.UNDERSCORE + asDateFormat
			+ HHSR5Constants.CSV_CONSTANT;
			
			File loTempDir = new File(PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
					"TASK_EXPORT_FILE_PATH")+ File.separator + asExportRequestId);
			if (!loTempDir.exists())
			{
				loTempDir.mkdirs();
			}
			lsFilePath = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
					"TASK_EXPORT_FILE_PATH")+ File.separator + asExportRequestId + File.separator + lsFileName;
			
			 writeTaskExportToFile(lsFilePath,loHeader);
			 
				// For sending notification

				 loNotificationAlertList.add(HHSR5Constants.NT234);
				loHmNotifyParam.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
				NotificationDataBean loNotificationAL234 = new NotificationDataBean();
				HashMap<String, String> loRequestMap = new HashMap<String, String>();
				HashMap<String, String> loAgencyLinkMap = new HashMap<String, String>();
				String lsAppProtocol = PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE,
						HHSConstants.PROP_CITY_URL);
				StringBuffer loSbMessage = new StringBuffer();
					loSbMessage.append("<a href=");
					loSbMessage.append(lsAppProtocol).append("/ExportAllBatch");
					loSbMessage.append("?requestId=");
					loSbMessage.append(asExportRequestId).append("&lsSubFileName=").append(lsSubFileName);
					loSbMessage.append(">").append(lsFileName);
					loSbMessage.append("</a>");
					loSbMessage.append("</BR>");
			
				StringBuffer loSbMessageUrl = new StringBuffer();
				String lsServerName = PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE,
						HHSConstants.PROP_CITY_URL);
				loSbMessageUrl.append("<a href=").append(lsServerName)
						.append(ApplicationConstants.PORTAL_URL);
				loSbMessageUrl.append("\">HHS Accelerator</a>");
				loRequestMap.put("ACCELERATORLINK", loSbMessageUrl.toString());
				loRequestMap.put("DOWNLOADLINK", loSbMessage.toString());
				loNotificationAL234.setAgencyLinkMap(loAgencyLinkMap);
				loHmNotifyParam.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
				loHmNotifyParam.put(ApplicationConstants.ENTITY_ID, asExportRequestId);
				loHmNotifyParam.put(ApplicationConstants.ENTITY_TYPE, "");
				loHmNotifyParam.put(HHSConstants.CREATED_BY_USER_ID, "system");
				loHmNotifyParam.put(HHSConstants.MODIFIED_BY, "system");
				loHmNotifyParam.put(HHSR5Constants.AL234, loNotificationAL234);
				loHmNotifyParam.put(HHSR5Constants.NT234, loNotificationAL234);
				loChannel.setData(HHSConstants.LO_HM_NOTIFY_PARAM, loHmNotifyParam);
				TransactionManager.executeTransaction(loChannel, "sendFileDownloadNotification",
						HHSR5Constants.TRANSACTION_ELEMENT_R5);

				Boolean lbStatus = (Boolean) loChannel.getData("insertStatus");
				if (lbStatus)
				{
					Channel loChannelObj = new Channel();
					loChannelObj.setData("ReqId", asExportRequestId);
					TransactionManager.executeTransaction(loChannelObj, "updateExportSuccessFlag",
							HHSR5Constants.TRANSACTION_ELEMENT_R5);
			}
			 
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Application Exception occurred in exportTaskList:", aoExp);
		}
		catch (final Exception aoExp)
		{
			LOG_OBJECT.Error("Application Exception occurred in exportTaskList:", aoExp);
		}
	}
	/**
	 * This method is used to write Task Export To File
	 * @param asFilename String
	 * @param aoExportData StringBuffer
	 * @throws IOException If an Application Exception occurs
	 */
    private void writeTaskExportToFile(String asFilename, StringBuffer aoExportData) throws  IOException {
        BufferedWriter loBufferedWriter = new BufferedWriter(new FileWriter(asFilename));
        loBufferedWriter.write(aoExportData.toString());
        loBufferedWriter.flush();
        loBufferedWriter.close();
    }

	/**
	 * @param aoTaskTypeList
	 */
	private void getTaskTypesForCity(List<String> aoTaskTypeList)
	{
		aoTaskTypeList.add("Approve Award");
		aoTaskTypeList.add("Approve Award Amount");
		aoTaskTypeList.add("Approve PSR");
		aoTaskTypeList.add("Business Review Application");
		//aoTaskTypeList.add("Contact Us"); // QC 9587 R 8.10.0Remove Contact Us task
		aoTaskTypeList.add("New Filing");
		aoTaskTypeList.add("Organization Legal Name - Update Request");
		aoTaskTypeList.add("Provider Account Request");
		aoTaskTypeList.add("Service Application");
		aoTaskTypeList.add("Withdrawal Request");
		aoTaskTypeList.add("Withdrawal Request - Business Review Application");
		aoTaskTypeList.add("Withdrawal Request - Service Application");
	}

	/**
	 * @param aoTaskTypeList
	 */
	@SuppressWarnings("rawtypes")
	private void getTaskTypesForFinance(List<String> aoTaskTypeList)
	{
		//Changes made in R6 for FindBug
		Iterator loItr = HHSConstants.FINANCIAL_TASK_PROCESS_ID_MAP.entrySet().iterator();
		 while(loItr.hasNext())
		 {
			 Map.Entry loEntry = (Map.Entry)loItr.next();
			 String loTaskName = (String) loEntry.getKey();
              if(null != loTaskName && !loTaskName.isEmpty())
              {
           	   aoTaskTypeList.add(loTaskName);
              }
		 }
		
	}

	/**
	 * @param aoTaskTypeList
	 */
	private void getTaskTypesForAcco(List<String> aoTaskTypeList)
	{
		aoTaskTypeList.add("Accept Proposal");
		aoTaskTypeList.add("Evaluate Proposal");
		aoTaskTypeList.add("Review Scores");
		aoTaskTypeList.add("Configure Award Documents");
		aoTaskTypeList.add("Complete PSR");
		aoTaskTypeList.add("Finalize Award Amount");
		getTaskTypesForFinance(aoTaskTypeList);
	}
	/**
	 * This method is used to get Task Types For Cfo
	 * @param aoTaskTypeList List<String>
	 */
	private void getTaskTypesForCfo(List<String> aoTaskTypeList)
	{
		aoTaskTypeList.add("Evaluate Proposal");
		aoTaskTypeList.add("Complete PSR");
		aoTaskTypeList.add("Finalize Award Amount");
		getTaskTypesForFinance(aoTaskTypeList);	
	}

	
	
    /*[Start] R8.7.0 QC9555 Performance Turning */
    private HashMap <String, AgencyTaskBean> convertUsrList2Map(List<AgencyTaskBean> aoUserNameLst){
		HashMap <String, AgencyTaskBean> loUserst = new HashMap <String, AgencyTaskBean> ();

		for( AgencyTaskBean loUsrbean :   aoUserNameLst ) {
			if( !loUserst.containsKey(loUsrbean.getSubmittedById()) ){
				loUserst.put(loUsrbean.getSubmittedById(), loUsrbean) ;
			}
		}

		return  loUserst;
	}

    private HashMap <String, AgencyTaskBean> convertInvList2Map(List<AgencyTaskBean> aoInvDateLst){
		HashMap <String, AgencyTaskBean> loInvLst = new HashMap <String, AgencyTaskBean> ();

		for( AgencyTaskBean loInvbean :   aoInvDateLst ) {
			if( !loInvLst.containsKey(loInvbean.getInvoiceNumber()    ) ){
				loInvLst.put(loInvbean.getInvoiceNumber(), loInvbean) ;
			}
		}

		return  loInvLst;
	}
    /*[End] R8.7.0 QC9555 Performance Turning */


}

