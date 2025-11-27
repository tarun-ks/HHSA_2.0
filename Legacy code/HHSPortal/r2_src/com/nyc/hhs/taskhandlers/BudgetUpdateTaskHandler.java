package com.nyc.hhs.taskhandlers;

import java.util.HashMap;
import java.util.Map;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This BudgetUpdateTaskHandler will implement all the abstract methods of
 * MainTaskHandler and hence implement Task related functionalities of
 * BudgetUpdateReviewTask
 * 
 */
public class BudgetUpdateTaskHandler extends MainTaskHandler
{

	/**
	 * This method decide the execution flow on click of Finish Task button with
	 * task status Approved for budget update Review Task
	 * <ul>
	 * <li>Set Audit details in channel by calling 'addAuditDataToChannel'
	 * Utility method</li>
	 * <li>Execute Transaction to Contract budget update Review Task</li>
	 * </ul>
	 * <br>
	 * Method updated in R4.
	 * 
	 * @param aoBudgetUpdateTaskDetailsBean TaskDetailsBean object
	 * @throws ApplicationException If an Application Exception occurs
	 * @return Map loTaskParamMap with task details
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Map taskApprove(TaskDetailsBean aoBudgetUpdateTaskDetailsBean) throws ApplicationException
	{
		Map loTaskParamMapForBudgetUpdate = new HashMap();
		Map loHmWFReqProps = new HashMap();
		Map loHmWFComponentForBudgetUpdate = new HashMap();
		loTaskParamMapForBudgetUpdate.put(HHSConstants.TASK_ERROR, HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED);
		loTaskParamMapForBudgetUpdate.put(HHSConstants.PAGE_ERROR, HHSConstants.EMPTY_STRING);
		String lsCurrentLevel = aoBudgetUpdateTaskDetailsBean.getLevel();
		String lsTotalLevel = aoBudgetUpdateTaskDetailsBean.getTotalLevel();
		boolean lbFinalFinish = false;
		Channel loChannelForBudgetUpdate = new Channel();
		if (lsCurrentLevel.equalsIgnoreCase(lsTotalLevel))
		{
			lbFinalFinish = true;
		}
		loChannelForBudgetUpdate.setData(HHSConstants.LO_HM_WF_REQ_PROPS, loHmWFReqProps);
		loChannelForBudgetUpdate.setData(HHSConstants.AO_FILENET_SESSION,
				aoBudgetUpdateTaskDetailsBean.getP8UserSession());
		loChannelForBudgetUpdate.setData(HHSConstants.AO_TASK_DETAILS_BEAN, aoBudgetUpdateTaskDetailsBean);
		loChannelForBudgetUpdate.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
		loChannelForBudgetUpdate.setData(HHSConstants.LB_FINAL_FINISH, lbFinalFinish);
		loChannelForBudgetUpdate.setData(HHSConstants.AO_BUDGET_STATUS, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_BUDGET_APPROVED));
		aoBudgetUpdateTaskDetailsBean.setEntityStatus(ApplicationConstants.STATUS_APPROVED);
		HHSUtil.setAuditOnFinancialFinishTask(aoBudgetUpdateTaskDetailsBean, loChannelForBudgetUpdate);
		if (lbFinalFinish)
		{
			loHmWFComponentForBudgetUpdate.put(HHSConstants.COMPONENT_ACTION,
					HHSConstants.MERGE_BUDGET_UPDATE_COMPONENT_ACTION);
			loHmWFComponentForBudgetUpdate.put(HHSConstants.VALUES,
					CommonUtil.convertBeanToString(aoBudgetUpdateTaskDetailsBean));
			loHmWFComponentForBudgetUpdate.put(HHSConstants.CONTRACT_BUDGET_STATUS, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_BUDGET_APPROVED));
			loChannelForBudgetUpdate.setData(HHSConstants.LO_HM_WF_PROPERTIES, loHmWFComponentForBudgetUpdate);
			loChannelForBudgetUpdate.setData(HHSConstants.WORK_FLOW_NAME, HHSConstants.WF_FINANCIAL_UTILITY);
			HHSTransactionManager.executeTransaction(loChannelForBudgetUpdate,
					HHSConstants.MERGE_BUDGET_UPDATE_REVIEW_TASK);
		}
		// R4 start merge update budget with base active budget
		HashMap loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.CONTRACT_ID, aoBudgetUpdateTaskDetailsBean.getContractId());
		loHashMap.put(HHSConstants.BUDGET_ID, aoBudgetUpdateTaskDetailsBean.getBudgetId());
		loHashMap.put(HHSConstants.CREATED_BY_USER_ID, aoBudgetUpdateTaskDetailsBean.getUserId());
		loChannelForBudgetUpdate.setData(HHSConstants.AO_HASH_MAP, loHashMap);
		// R4 end merge update budget with base active budget
		loChannelForBudgetUpdate.setData(HHSConstants.LB_FINAL_FINISH, lbFinalFinish);
		loChannelForBudgetUpdate.setData(HHSConstants.AO_TASK_DETAILS_BEAN, aoBudgetUpdateTaskDetailsBean);
		// Start R5 : set EntityId and EntityName for AutoSave
		CommonUtil.setChannelForAutoSaveData(loChannelForBudgetUpdate, aoBudgetUpdateTaskDetailsBean.getWorkFlowId(),
				HHSR5Constants.TASKS);
		// End R5 : set EntityId and EntityName for AutoSave
		HHSTransactionManager.executeTransaction(loChannelForBudgetUpdate,
				HHSConstants.FINISH_BUDGET_UPDATE_REVIEW_TASK);
		loTaskParamMapForBudgetUpdate.put(HHSConstants.PAGE_ERROR, HHSConstants.EMPTY_STRING);
		loTaskParamMapForBudgetUpdate.put(HHSConstants.TASK_ERROR, HHSConstants.EMPTY_STRING);
		return loTaskParamMapForBudgetUpdate;
	}

	/**
	 * This method decide the execution flow on click of Finish Task button with
	 * task status Return For Revision for Contract budget update Review Task
	 * <ul>
	 * <li>Set Audit details in channel by calling 'addAuditDataToChannel'
	 * Utility method</li>
	 * <li>Execute Transaction to Return For Revision Contract budget update
	 * review Task</li>
	 * </ul>
	 * 
	 * @param aoTaskDetailsBeanForBudgetUpdate TaskDetailsBean object
	 * @throws ApplicationException If an Application Exception occurs
	 * @return Map loTaskParamMap with task details
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Map taskReturn(TaskDetailsBean aoTaskDetailsBeanForBudgetUpdate) throws ApplicationException
	{
		Map loTaskParamMapForBudgetUpdate = new HashMap();
		loTaskParamMapForBudgetUpdate.put(HHSConstants.TASK_ERROR, HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED);
		loTaskParamMapForBudgetUpdate.put(HHSConstants.PAGE_ERROR, HHSConstants.EMPTY_STRING);
		Channel loChannelForBudgetUpdate = new Channel();
		boolean lbFinalFinish = false;
		String lsCurrentLevel = aoTaskDetailsBeanForBudgetUpdate.getLevel();
		if (lsCurrentLevel.equalsIgnoreCase(HHSConstants.ONE))
		{
			lbFinalFinish = true;
		}
		loChannelForBudgetUpdate.setData(HHSConstants.AO_FILENET_SESSION,
				aoTaskDetailsBeanForBudgetUpdate.getP8UserSession());
		loChannelForBudgetUpdate.setData(HHSConstants.AO_TASK_DETAILS_BEAN, aoTaskDetailsBeanForBudgetUpdate);
		loChannelForBudgetUpdate.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
		loChannelForBudgetUpdate.setData(HHSConstants.LB_FINAL_FINISH, lbFinalFinish);
		loChannelForBudgetUpdate.setData(HHSConstants.AO_BUDGET_STATUS, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.BUDGET_RETURNED_FOR_REVISION));
		HHSUtil.setAuditOnFinancialFinishTask(aoTaskDetailsBeanForBudgetUpdate, loChannelForBudgetUpdate);
		// Start R5 : set EntityId and EntityName for AutoSave
		CommonUtil.setChannelForAutoSaveData(loChannelForBudgetUpdate,
				aoTaskDetailsBeanForBudgetUpdate.getWorkFlowId(), HHSR5Constants.TASKS);
		// End R5 : set EntityId and EntityName for AutoSave
		HHSTransactionManager.executeTransaction(loChannelForBudgetUpdate,
				HHSConstants.FINISH_BUDGET_UPDATE_RETURNED_TASK);

		loTaskParamMapForBudgetUpdate.put(HHSConstants.PAGE_ERROR, HHSConstants.EMPTY_STRING);
		loTaskParamMapForBudgetUpdate.put(HHSConstants.TASK_ERROR, HHSConstants.EMPTY_STRING);
		return loTaskParamMapForBudgetUpdate;
	}

}
