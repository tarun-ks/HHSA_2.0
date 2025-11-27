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
 * This BudgetModificationTaskHandler will implement all the abstract methods of
 * MainTaskHandler and hence implement Task related functionalities of
 * BudgetModificationTask
 * 
 */
public class ContractConfigurationTaskHandler extends MainTaskHandler
{
	private static final LogInfo LOG_OBJECT = new LogInfo(ContractConfigurationTaskHandler.class);

	/**
	 * This method is modified for Release 3.6.0 for enhancement request #6496.
	 * This method decide the execution flow on click of Finish Task button with
	 * task status Approved for Contract configuration Task
	 * <ul>
	 * <li>Set Audit details in channel by calling 'addAuditDataToChannel'
	 * Utility method</li>
	 * <li>Execute Transaction insertLineItemsForContractConfigTask to Contract
	 * configuration Task</li>
	 * <li>Updated in R7 for Cost Center</li>
	 * </ul>
	 * <br>
	 * Method updated in R4.
	 * @param aoTaskDetailsBean TaskDetailsBean object
	 * @return loTaskParamMap Map
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Map taskApprove(TaskDetailsBean aoTaskDetailsBean) throws ApplicationException
	{
		Map loTaskParamMap = new HashMap();
		Map loHmWFReqProps = new HashMap();
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		boolean lbFinalFinish = true;
		Channel loChannel = new Channel();
		if (aoTaskDetailsBean.getLaunchCOF())
		{
			lbFinalFinish = false;
		}
		loHmWFReqProps.put(HHSConstants.COMPONENT_ACTION, HHSConstants.INSERT_LINE_ITEMS_CONF_COMPONENT_ACTION);
		loHmWFReqProps.put(HHSConstants.VALUES, CommonUtil.convertBeanToString(aoTaskDetailsBean));
		loChannel.setData(HHSConstants.WORK_FLOW_NAME, HHSConstants.WF_FINANCIAL_UTILITY);
		loChannel.setData(HHSConstants.LO_HM_WF_REQ_PROPS, loHmWFReqProps);
		loChannel.setData(HHSConstants.AO_FILENET_SESSION, aoTaskDetailsBean.getP8UserSession());
		loChannel.setData(ApplicationConstants.TASK_DETAILS_BEAN_KEY, aoTaskDetailsBean);
		loChannel.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
		loChannel.setData(HHSConstants.LB_FINAL_FINISH, lbFinalFinish);
		// Start: Added in R7 For Cost Center
		HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.VALIDATE_SERVICES_OPTED,
				HHSR5Constants.TRANSACTION_ELEMENT_R5);
		String loMessage = (String) loChannel.getData(HHSConstants.CBL_MESSAGE);
		if (loMessage != null)
		{
			loTaskParamMap.put(HHSConstants.PAGE_ERROR, loMessage);
			return loTaskParamMap;
		}
		// End: Added in R7 For Cost Center
		HHSUtil.setAuditOnFinancialFinishTask(aoTaskDetailsBean, loChannel);

		List<HhsAuditBean> loAuditList = null;
		loAuditList = (List<HhsAuditBean>) loChannel.getData(HHSConstants.LO_AUDIT_LIST);
		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.INSERT_LINE_ITEMS_CONF_COMPONENT_ACTION);
		if (!(aoTaskDetailsBean.getLinkedWobNum().isEmpty() || aoTaskDetailsBean.getLinkedWobNum() == null))
		{
			loAuditList.add(HHSUtil.addAuditDataToChannel(P8Constants.EVENT_NAME_ASSIGN, P8Constants.EVENT_NAME_ASSIGN,
					HHSConstants.TASK_ASSIGNED_TO + HHSConstants.COLON + HHSConstants.SPACE
							+ HHSConstants.UNASSIGNED_LEVEL2, HHSConstants.TASK_CONTRACT_COF,
					aoTaskDetailsBean.getEntityId(), aoTaskDetailsBean.getUserId(), HHSConstants.AGENCY_AUDIT));
		}
		// R4 Publish EntryTypeId's for contractBudget
		HHSUtil.setPublishEntryType(loChannel, aoTaskDetailsBean.getContractId(), HHSConstants.ONE, HHSConstants.ONE,
				aoTaskDetailsBean.getUserId(), aoTaskDetailsBean.getStartFiscalYear());
		loNotificationMap = getNotificationMapForContractConfigTask(aoTaskDetailsBean);
		loChannel.setData(HHSConstants.LO_NOTIFICATION_MAP, loNotificationMap);
		// Start R5 : set EntityId and EntityName for AutoSave
		CommonUtil.setChannelForAutoSaveData(loChannel, aoTaskDetailsBean.getWorkFlowId(), HHSR5Constants.TASKS);
		// End R5 : set EntityId and EntityName for AutoSave
		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.CCT_FINISH_CONTRACT_CONFIG_TASK);
		loTaskParamMap.put(HHSConstants.PAGE_ERROR, HHSConstants.EMPTY_STRING);
		loTaskParamMap.put(HHSConstants.TASK_ERROR, HHSConstants.EMPTY_STRING);
		return loTaskParamMap;
	}

	/**
	 * This method decide the execution flow on click of Finish Task button with
	 * task status Return For Revision for Contract configuration Task
	 * <ul>
	 * <li>Set Audit details in channel by calling 'addAuditDataToChannel'
	 * Utility method</li>
	 * <li>Execute Transaction to Return For Revision Contract configuration
	 * Task</li>
	 * </ul>
	 * 
	 * @param aoTaskDetailsBean TaskDetailsBean object
	 * @return loTaskParamMap Map
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Map taskReturn(TaskDetailsBean aoTaskDetailsBean) throws ApplicationException
	{
		Map loTaskParamMap = new HashMap();

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
	private HashMap<String, Object> getNotificationMapForContractConfigTask(TaskDetailsBean aoTaskDetailsBean)
			throws ApplicationException
	{
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		try
		{
			Channel loChannel = new Channel();
			NotificationDataBean loNotificationDataBean = new NotificationDataBean();
			String lsCityUrl = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
					HHSConstants.PROP_CITY_URL);
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
