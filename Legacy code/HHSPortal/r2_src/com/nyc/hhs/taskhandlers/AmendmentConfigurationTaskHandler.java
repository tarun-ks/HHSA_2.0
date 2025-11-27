package com.nyc.hhs.taskhandlers;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.ProcurementCOF;
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
public class AmendmentConfigurationTaskHandler extends MainTaskHandler
{

	/**
	 * <p>
	 * This method decide the execution flow on click of Finish Task button with
	 * task status Approved for Contract configuration Task
	 * <ul>
	 * <li>Set Audit details in channel by calling 'addAuditDataToChannel'
	 * Utility method</li>
	 * <li>Execute Transaction to Contract configuration Task id <b>
	 * validateContractConfigUpdateAmount</b></li>
	 * <li>set the channel details</li>
	 * </ul>
	 * Updated Method in R4
	 * </p>
	 * 
	 * @param aoTaskDetailsBean TaskDetailsBean object
	 * @return loTaskParamMap Map
	 * @throws ApplicationException If an Application Exception occurs
	 * 
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Map taskApprove(TaskDetailsBean aoTaskDetailsBean) throws ApplicationException
	{
		Map loTaskParamMap = new HashMap();
		Map loHmWFReqProps = new HashMap();
		Channel loChannel = new Channel();
		String lsContractId = aoTaskDetailsBean.getBaseContractId();
		

		loChannel.setData(HHSConstants.LB_FLAG, true);
		loChannel.setData(HHSConstants.AO_FILENET_SESSION, aoTaskDetailsBean.getP8UserSession());
		loChannel.setData(ApplicationConstants.TASK_DETAILS_BEAN_KEY, aoTaskDetailsBean);
		loChannel.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
		HHSUtil.setAuditOnFinancialFinishTask(aoTaskDetailsBean, loChannel);
		loChannel.setData(HHSConstants.CONTRACT_ID_KEY, lsContractId);
		loChannel.setData(HHSConstants.AMENDED_CONTRACT_ID, aoTaskDetailsBean.getContractId());
		loChannel.setData(HHSConstants.CONTRACT_TYPE_ID_KEY, HHSConstants.TWO);
		loChannel.setData(HHSConstants.AS_BUDGET_TYPE_ID, HHSConstants.ONE);
		// release 3.12.0 enhancement 6601
		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.CS_VALIDATE_CONTRACT_CONFIG_AMEND_AMOUNT);
		String loMessage = (String) loChannel.getData(HHSConstants.CBL_MESSAGE);

		if (loMessage != null)
		{
			loTaskParamMap.put(HHSConstants.PAGE_ERROR, loMessage);
			return loTaskParamMap;
		}
		Channel loChannel1 = new Channel();
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.CONTRACT_ID, lsContractId);
		loHashMap.put(HHSConstants.CONTRACT_TYPE_ID, HHSConstants.TWO);
		loHashMap.put(HHSConstants.AMEND_CONTRACT_ID, aoTaskDetailsBean.getContractId());
		loChannel1.setData(HHSConstants.AO_HASH_MAP, loHashMap);
		HHSTransactionManager.executeTransaction(loChannel1,
				HHSConstants.BMC_FETCH_CONTRACT_AMENDMENT_CONFIGURATION_DETAILS);
		ProcurementCOF loPprocureCofAmendment = (ProcurementCOF) loChannel1
				.getData(HHSConstants.BMC_PROCUREMENT_CON_DETAILS);
		
		//Start R8.4.0 qc_8537 $0 Amendments do not appear in Amendment CoF task list after configuration
		if (loPprocureCofAmendment.getAmendmentValue() != null && new BigDecimal(loPprocureCofAmendment.getAmendmentValue()).compareTo(BigDecimal.ZERO) == HHSConstants.INT_ZERO){
			loHmWFReqProps.put(HHSConstants.PROPERTY_PE_LAUNCH_COF, true);
		}		
		else{//existing logic
			if (loPprocureCofAmendment.getAmendmentValue().equalsIgnoreCase(HHSConstants.STRING_ZERO))
			{
				loHmWFReqProps.put(HHSConstants.PROPERTY_PE_LAUNCH_COF, false);
			}
			else
			{
				loHmWFReqProps.put(HHSConstants.PROPERTY_PE_LAUNCH_COF, true);
			}
		}
		//End R8.4.0 qc_8537 $0 Amendments do not appear in Amendment CoF task list after configuration


		/*[Start] R8.9.0  QC9213 Amendment Missing Default Line Items - After Returned for Revision    */
/*		List<HhsAuditBean> loAuditList = null;
		loAuditList = (List<HhsAuditBean>) loChannel.getData(HHSConstants.LO_AUDIT_LIST);
		if (aoTaskDetailsBean.getLinkedWobNum().isEmpty() || aoTaskDetailsBean.getLinkedWobNum() == null)
		{
*/
		    loHmWFReqProps.put(HHSConstants.COMPONENT_ACTION, HHSConstants.INSERT_LINE_ITEMS_CONF_COMPONENT_ACTION);

			loChannel.setData(HHSConstants.WORK_FLOW_NAME, HHSConstants.WF_FINANCIAL_UTILITY);
			loChannel.setData(HHSConstants.LO_HM_WF_REQ_PROPS, loHmWFReqProps);
			loChannel.setData(HHSConstants.AO_FILENET_SESSION, aoTaskDetailsBean.getP8UserSession());
			loChannel.setData(ApplicationConstants.TASK_DETAILS_BEAN_KEY, aoTaskDetailsBean);
			Channel loChannelObj = new Channel();
			loChannelObj.setData(HHSConstants.AMENDED_CONTRACT_ID, aoTaskDetailsBean.getContractId());
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.FETCH_ACTIVE_APPROVED_FISCAL_YEARS);
			List<String> loFiscalYrList = (List) loChannelObj.getData(HHSConstants.FISCAL_YEAR_LIST);

			for (String loFiscalYr : loFiscalYrList)
			{
				aoTaskDetailsBean.setStartFiscalYear(loFiscalYr);
				loHmWFReqProps.put(HHSConstants.VALUES, CommonUtil.convertBeanToString(aoTaskDetailsBean));
				HHSTransactionManager.executeTransaction(loChannel,
						HHSConstants.INSERT_LINE_ITEMS_CONF_COMPONENT_ACTION);
			}
/*		}
		else
		{
			loAuditList.add(HHSUtil.addAuditDataToChannel(P8Constants.EVENT_NAME_ASSIGN, P8Constants.EVENT_NAME_ASSIGN,
					HHSConstants.TASK_ASSIGNED_TO + HHSConstants.COLON + HHSConstants.SPACE
							+ HHSConstants.UNASSIGNED_LEVEL2, HHSConstants.TASK_AMENDMENT_COF,
					aoTaskDetailsBean.getEntityId(), aoTaskDetailsBean.getUserId(), HHSConstants.AGENCY_AUDIT));
		}
*/
		/*[End] R8.9.0 QC9213 Amendment Missing Default Line Items - After Returned for Revision  */


		loChannel.setData(HHSConstants.LO_HM_WF_REQ_PROPS, loHmWFReqProps);
		loChannel.setData(HHSConstants.AS_STATUS_ID, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_CONTRACT_PENDING_COF));
		// Start R5 : set EntityId and EntityName for AutoSave
		CommonUtil.setChannelForAutoSaveData(loChannel, aoTaskDetailsBean.getWorkFlowId(), HHSR5Constants.TASKS);
		// End R5 : set EntityId and EntityName for AutoSave
		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FINISH_AMENDMENT_CONF_TASK);
		loTaskParamMap.put(HHSConstants.PAGE_ERROR, HHSConstants.EMPTY_STRING);
		loTaskParamMap.put(HHSConstants.TASK_ERROR, HHSConstants.EMPTY_STRING);
		return loTaskParamMap;
	}

	/**
	 * This method decide the execution flow on click of Finish Task button with
	 * task status Return For Revision for Contract configuration Task
	 * <ul>
	 * <li>set the Map details</li>
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

}
