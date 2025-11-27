package com.nyc.hhs.taskhandlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSP8Constants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.constants.TransactionConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.NotificationDataBean;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This ContractCertFundsTaskHandler will implement all the abstract methods of
 * MainTaskHandler and hence implement Task related functionalities of Contract
 * Certification of Funds Task
 * 
 */
public class ContractCertFundsTaskHandler extends MainTaskHandler
{

	private static final LogInfo LOG_OBJECT = new LogInfo(ContractCertFundsTaskHandler.class);

	/**
	 * This method is modified for Release 3.6.0 for enhancement request #6496.
	 * This method is modified for Release 3.12.0 defect #6585. This method
	 * decide the execution flow on click of Finish Task button with task status
	 * Approved for Contract Certification of Funds Task
	 * <ul>
	 * <li>If the task is not assigned to the Final Reviewer, then the task is
	 * sent to the next reviewer level queue</li>
	 * <li>If the task is assigned to the Final Reviewer execute
	 * finishContractCOFTask transaction which will do following actions: 1.
	 * Close Contract Certification of Funds Task 2. Generate Contract
	 * Certification of Funds document and send to FileNet 3. If the Contract is
	 * part of an open Procurement (Contract Source = ‘R2 Contract’) Check to
	 * see if the Cert of Funds task has been approved for all other contracts
	 * awarded from the procurement. If yes, generate S247 - ‘Configure Award
	 * Documents Task’ If not, no further actions are required If the Contract
	 * is NOT associated with an open Procurement (Contract Source = ‘APT’)
	 * Update the Contract Status to ‘Pending Registration’</li>
	 * </ul>
	 * 
	 * @param aoTaskDetailsBean TaskDetailsBean object
	 * @return loTaskParamMap Map
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Map taskApprove(TaskDetailsBean aoTaskDetailsBean) throws ApplicationException
	{
		Map loTaskParamMap = new HashMap();
		String lsCurrentLevel = aoTaskDetailsBean.getLevel();
		String lsTotalLevel = aoTaskDetailsBean.getTotalLevel();
		Boolean lbFinalFinish = false;
		// Contract source check added for Release 3.12.0 defect #6585 -start
		Boolean lbProcessNotification = false;
		// Contract source check added for Release 3.12.0 defect #6585 -end
		Channel loChannel = new Channel();
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		try
		{
			if (lsCurrentLevel.equalsIgnoreCase(lsTotalLevel))
			{
				loChannel.setData(HHSConstants.CONTRACT_ID_KEY, aoTaskDetailsBean.getContractId());
				/*
				 * Commenting code Release 5 changes done for Award Negotiation
				 * // Contract source check added for Release 3.12.0 defect
				 * #6585 // -start
				 * HHSTransactionManager.executeTransaction(loChannel,
				 * HHSConstants.CS_FETCH_CONTRACT_SOURCE_ID); String
				 * lsContractSource = (String)
				 * loChannel.getData(HHSConstants.AS_CONTRACT_SOURCE); if
				 * (lsContractSource.equalsIgnoreCase(HHSConstants.TWO)) {
				 * lbProcessNotification = true; } // Contract source check
				 * added for Release 3.12.0 defect #6585 // -end
				 */
				lbProcessNotification = true;
				lbFinalFinish = true;
				loChannel.setData(HHSConstants.LS_WOB_NUM, aoTaskDetailsBean.getContractConfWob());
			}
			loChannel.setData(HHSConstants.LB_FINAL_FINISH, lbFinalFinish);
			loChannel.setData(HHSConstants.AO_FILENET_SESSION, aoTaskDetailsBean.getP8UserSession());
			loChannel.setData(ApplicationConstants.TASK_DETAILS_BEAN_KEY, aoTaskDetailsBean);
			loChannel.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
			HHSUtil.setAuditOnFinancialFinishTask(aoTaskDetailsBean, loChannel);
			loNotificationMap = getNotificationMapForContractCertTask(aoTaskDetailsBean);
			loChannel.setData(HHSConstants.LO_NOTIFICATION_MAP, loNotificationMap);
			// Contract source check added for Release 3.12.0 defect #6585
			// -start
			loChannel.setData("abProcessNotification", lbProcessNotification);
			// Contract source check added for Release 3.12.0 defect #6585 -end
			// Start R5 : set EntityId and EntityName for AutoSave
			CommonUtil.setChannelForAutoSaveData(loChannel, aoTaskDetailsBean.getWorkFlowId(), HHSR5Constants.TASKS);
			// End R5 : set EntityId and EntityName for AutoSave
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.CTH_FINISH_CONTRACT_COF_TASK);

			loTaskParamMap.put(HHSConstants.PAGE_ERROR, HHSConstants.EMPTY_STRING);
			loTaskParamMap.put(HHSConstants.TASK_ERROR, HHSConstants.EMPTY_STRING);
		}
		catch (ApplicationException aoExp)
		{
			aoExp.addContextData(
					"ApplicationException occured while executing taskApprove for ContractCertFundsTaskHandler", aoExp);
			throw aoExp;
		}
		catch (Exception aoExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while executing taskApprove for ContractCertFundsTaskHandler ", aoExp);
			loAppEx.addContextData(
					"ApplicationException occured while executing taskApprove for ContractCertFundsTaskHandler", aoExp);
			throw loAppEx;
		}

		return loTaskParamMap;
	}

	/**
	 * This method decide the execution flow on click of Finish Task button with
	 * task status Return For Revision for Contract Certification of Funds Task
	 * <ul>
	 * <li>If the task is assigned to a Level 2 Reviewer, call
	 * returnContractCOFTaskWithLevelTwo transaction which will launch Workflow
	 * 302 – Contract Configuration and <Contract Configuration Task Status> is
	 * set as ‘Returned for Revisions’. Close the <Contract Certification of
	 * Funds Task> and Update the Contract Status to ‘Pending Configuration’</li>
	 * <li>If the task is assigned to a Level 3 Reviewer or higher, call
	 * returnContractCOFTaskWithLevelThree transaction which will return the
	 * task to the previous reviewer level queue and set <Contract Certification
	 * of Funds Task Status> as ‘Returned for Revision’</li>
	 * <li>Updated in R7 for Cost Center, Added 'deleteServicesDetails' service on return of
	 * Contract COF Task: Transaction returnContractCOFTaskWithLevelThree updated</li>
	 * <li>Updated in R7 to update is_published flag to zero on return of COF</li>
	 * </ul>
	 * 
	 * @param aoTaskDetailsBean TaskDetailsBean object
	 * @return loTaskParamMap Map
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings("unchecked")
	public Map taskReturn(TaskDetailsBean aoTaskDetailsBean) throws ApplicationException
	{
		Map loTaskParamMap = new HashMap();
		Channel loChannel = new Channel();
		Boolean lbFinalFinish = false;

		try
		{
			String lsCurrentLevel = aoTaskDetailsBean.getLevel();
			HHSUtil.setAuditOnFinancialFinishTask(aoTaskDetailsBean, loChannel);
			loChannel.setData(HHSConstants.AO_FILENET_SESSION, aoTaskDetailsBean.getP8UserSession());
			loChannel.setData(ApplicationConstants.TASK_DETAILS_BEAN_KEY, aoTaskDetailsBean);
			loChannel.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
			loChannel.setData(HHSConstants.CTH_LO_WOB_NUM, aoTaskDetailsBean.getContractConfWob());
			if (lsCurrentLevel.equalsIgnoreCase(HHSConstants.TWO))
			{
				lbFinalFinish = true;
				// Copy internal comments & Task assignment entry to show in
				// Amendment Config task
				List<HhsAuditBean> loAuditList = null;
				loAuditList = (List<HhsAuditBean>) loChannel.getData(HHSConstants.LO_AUDIT_LIST);
				if (null != loAuditList && null != aoTaskDetailsBean.getInternalComment()
						&& !(HHSConstants.EMPTY_STRING.equalsIgnoreCase(aoTaskDetailsBean.getInternalComment())))
				{

					loAuditList.add(HHSUtil.addAuditDataToChannel(HHSConstants.AUDIT_TASK_INTERNAL_COMMENTS,
							HHSConstants.AUDIT_TASK_INTERNAL_COMMENTS, aoTaskDetailsBean.getInternalComment(),
							HHSConstants.TASK_CONTRACT_CONFIGURATION, aoTaskDetailsBean.getEntityId(),
							aoTaskDetailsBean.getUserId(), HHSConstants.AGENCY_AUDIT));
				}
				if (null != loAuditList)
				{
					loAuditList.add(HHSUtil.addAuditDataToChannel(P8Constants.EVENT_NAME_ASSIGN,
							P8Constants.EVENT_NAME_ASSIGN, HHSConstants.PROPERTY_TASK_CREATION_DATA,
							HHSConstants.TASK_CONTRACT_CONFIGURATION, aoTaskDetailsBean.getEntityId(),
							aoTaskDetailsBean.getUserId(), HHSConstants.AGENCY_AUDIT));
				}
			}
			// Start: Added in R7 to update is_published flag to zero on return of COF
			loChannel.setData(HHSConstants.AS_CONTRACT_ID, aoTaskDetailsBean.getContractId());
			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.FETCH_BUDGET_FISCAL_YEAR,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			String lsFYYear = (String) loChannel.getData(HHSConstants.CBL_MESSAGE);
			HHSUtil.setPublishEntryType(loChannel, aoTaskDetailsBean.getContractId(), HHSConstants.ZERO, HHSConstants.ONE,
			aoTaskDetailsBean.getUserId(), lsFYYear);
			// End: Added in R7
			loChannel.setData(HHSConstants.LO_HM_WF_REQ_PROPS, loTaskParamMap);
			loChannel.setData(HHSConstants.LB_FLAG, lbFinalFinish);
			// Start R5 : set EntityId and EntityName for AutoSave
			CommonUtil.setChannelForAutoSaveData(loChannel, aoTaskDetailsBean.getWorkFlowId(), HHSR5Constants.TASKS);
			// End R5 : set EntityId and EntityName for AutoSave
			HHSTransactionManager.executeTransaction(loChannel,
					HHSConstants.CTH_RETURN_CONTRACT_COF_TASK_WITH_LEVEL_THREE);

		}
		catch (ApplicationException aoExp)
		{
			aoExp.addContextData(
					"ApplicationException occured while executing taskReturn for ContractCertFundsTaskHandler", aoExp);
			throw aoExp;
		}
		catch (Exception aoExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while executing taskReturn for ContractCertFundsTaskHandler ", aoExp);
			loAppEx.addContextData(
					"ApplicationException occured while executing taskReturn for ContractCertFundsTaskHandler", aoExp);
			throw loAppEx;
		}

		loTaskParamMap.put(HHSConstants.PAGE_ERROR, HHSConstants.EMPTY_STRING);
		loTaskParamMap.put(HHSConstants.TASK_ERROR, HHSConstants.EMPTY_STRING);
		return loTaskParamMap;
	}

	/**
	 * This method is added for Release 3.6.0 for enhancement request #6496. The
	 * method sets notification parameter map for Notification/Alert
	 * NT403/AL403.
	 * @param aoTaskDetailsBean TaskDetailsBean object
	 * @return
	 * @throws ApplicationException
	 */
	private HashMap<String, Object> getNotificationMapForContractCertTask(TaskDetailsBean aoTaskDetailsBean)
			throws ApplicationException
	{
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		try
		{
			Channel loChannel = new Channel();
			NotificationDataBean loNotificationDataBean = new NotificationDataBean();
			List<String> loNotificationAlertList = new ArrayList<String>();
			loNotificationAlertList.add(HHSConstants.NT_403);
			loNotificationAlertList.add(HHSConstants.AL_403);
			loChannel.setData(HHSConstants.CONTRACT_ID_KEY, aoTaskDetailsBean.getContractId());
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_BUD_DETAILS_NT403);
			HashMap loBudgetDetailMap = (HashMap) loChannel.getData(HHSConstants.BUDGET_DETAILS_MAP);
			HashMap<String, String> loLinkMap = new HashMap<String, String>();
			StringBuffer lsBfApplicationUrl = new StringBuffer();
			lsBfApplicationUrl = getNotificationLink();
			lsBfApplicationUrl.append(HHSConstants.CONTRACT_BUDGET_REVISIONS_URL);
			lsBfApplicationUrl.append(HHSConstants.CONTRACT_ID_URL);
			lsBfApplicationUrl.append(aoTaskDetailsBean.getContractId());
			lsBfApplicationUrl.append(HHSConstants.BUDGET_ID_URL);
			lsBfApplicationUrl.append(String.valueOf(loBudgetDetailMap.get(HHSConstants.BUDGET_ID_HASH_KEY)));
			lsBfApplicationUrl.append(HHSConstants.FISCAL_YEAR_ID_URL);
			lsBfApplicationUrl.append(String.valueOf(loBudgetDetailMap.get(HHSConstants.FISCAL_YEAR_ID_CAPS)));
			loLinkMap.put(HHSConstants.LINK, lsBfApplicationUrl.toString());
			List<String> loProviderIdList = new ArrayList<String>();
			loProviderIdList.add(aoTaskDetailsBean.getOrganizationId());
			loNotificationDataBean.setProviderList(loProviderIdList);
			loNotificationDataBean.setLinkMap(loLinkMap);
			loNotificationMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
			loNotificationMap.put(HHSConstants.NT_403, loNotificationDataBean);
			loNotificationMap.put(HHSConstants.AL_403, loNotificationDataBean);
			loRequestMap.put(HHSConstants.PROC_TITLE, aoTaskDetailsBean.getProcurementTitle());
			loRequestMap.put(HHSConstants.AGENCY_NAME_COLUMN,
					String.valueOf(loBudgetDetailMap.get(HHSConstants.AGENCY_NAME_COLUMN)));
			loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
			loNotificationMap.put(ApplicationConstants.ENTITY_ID,
					String.valueOf(loBudgetDetailMap.get(HHSConstants.BUDGET_ID_HASH_KEY)));
			loNotificationMap.put(ApplicationConstants.ENTITY_TYPE, HHSConstants.BUDGETLIST_BUDGET);
			loNotificationMap.put(HHSConstants.CREATED_BY_USER_ID, aoTaskDetailsBean.getUserId());
			loNotificationMap.put(HHSConstants.MODIFIED_BY, aoTaskDetailsBean.getUserId());
		}
		catch (ApplicationException aoExp)
		{
			aoExp.addContextData("ApplicationException occured while setting notification map", aoExp);
			throw aoExp;
		}
		catch (Exception aoExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while setting notification map ", aoExp);
			loAppEx.addContextData("ApplicationException occured while setting notification map", aoExp);
			throw loAppEx;
		}
		return loNotificationMap;
	}

	/**
	 * This method is added for Release 3.6.0 for enhancement request #6496.
	 * This method is used to get Notifications link.
	 * @return
	 * @throws ApplicationException
	 */
	private StringBuffer getNotificationLink() throws ApplicationException
	{

		StringBuffer loNotificationLINK = new StringBuffer();
		String lsServerName = PropertyLoader.getProperty(HHSP8Constants.PROPERTY_FILE,
				HHSP8Constants.SERVER_NAME_FOR_PROVIDER_BATCH);
		String lsServerPort = PropertyLoader.getProperty(HHSP8Constants.PROPERTY_FILE,
				HHSP8Constants.SERVER_PORT_FOR_PROVIDER_BATCH);
		String lsContextPath = PropertyLoader.getProperty(HHSP8Constants.PROPERTY_FILE,
				HHSP8Constants.CONTEXT_PATH_FOR_PROVIDER_BATCH);
		String lsAppProtocol = PropertyLoader.getProperty(HHSP8Constants.PROPERTY_FILE,
				HHSP8Constants.SERVER_PROTOCOL_FOR_PROVIDER_BATCH);
		loNotificationLINK.append(lsAppProtocol);
		loNotificationLINK.append("://");
		loNotificationLINK.append(lsServerName);
		loNotificationLINK.append(":");
		loNotificationLINK.append(lsServerPort);
		loNotificationLINK.append("/");
		loNotificationLINK.append(lsContextPath);

		return loNotificationLINK;
	}
}
