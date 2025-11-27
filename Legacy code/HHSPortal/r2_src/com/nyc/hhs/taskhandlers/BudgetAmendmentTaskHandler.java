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
 * This BudgetAmendmentTaskHandler will implement all the abstract methods of
 * MainTaskHandler and hence implement Task related functionalities of
 * BudgetAmendmentReviewTask
 * 
 */
public class BudgetAmendmentTaskHandler extends MainTaskHandler
{

	/**
	 * This method decide the execution flow on click of Finish Task button with
	 * task status Approved for Contract budget Amendment Review Task
	 * <ul>
	 * <li>Set Audit details in channel by calling 'addAuditDataToChannel'
	 * Utility method</li>
	 * <li>Execute Transaction to Contract budget Amendment Review Task</li>
	 * <li>Transaction id mergeBudgetForAmendmentReviewTask</li>
	 * </ul>
	 * Updated Method in R4
	 * @param aoTaskDetailsBeanBudgetAmendment TaskDetailsBean object
	 * @return Map loTaskParamMap with task details
	 * @throws ApplicationException If an Application Exception occurs
	 * 
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Map taskApprove(TaskDetailsBean aoTaskDetailsBeanBudgetAmendment) throws ApplicationException
	{
		Map loTaskParamMapForBudgetAmend = new HashMap();
		Map loHmWFReqProps = new HashMap();
		Map loHmWFComponentForBudgetAmend = new HashMap();
		loTaskParamMapForBudgetAmend.put(HHSConstants.TASK_ERROR, HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED);
		loTaskParamMapForBudgetAmend.put(HHSConstants.PAGE_ERROR, HHSConstants.EMPTY_STRING);
		String lsCurrentLevel = aoTaskDetailsBeanBudgetAmendment.getLevel();
		String lsTotalLevel = aoTaskDetailsBeanBudgetAmendment.getTotalLevel();
		boolean lbFinalFinish = false;
		Channel loChannelForBudgetAmend = new Channel();
		if (lsCurrentLevel.equalsIgnoreCase(lsTotalLevel))
		{
			lbFinalFinish = true;
		}
		loChannelForBudgetAmend.setData(HHSConstants.LO_HM_WF_REQ_PROPS, loHmWFReqProps);
		loChannelForBudgetAmend.setData(HHSConstants.AO_FILENET_SESSION,
				aoTaskDetailsBeanBudgetAmendment.getP8UserSession());
		loChannelForBudgetAmend.setData(HHSConstants.AO_TASK_DETAILS_BEAN, aoTaskDetailsBeanBudgetAmendment);
		loChannelForBudgetAmend.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
		loChannelForBudgetAmend.setData(HHSConstants.LB_FINAL_FINISH, lbFinalFinish);
		loChannelForBudgetAmend.setData(HHSConstants.AO_BUDGET_STATUS, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_BUDGET_APPROVED));
		aoTaskDetailsBeanBudgetAmendment.setEntityStatus(ApplicationConstants.STATUS_APPROVED);
		HHSUtil.setAuditOnFinancialFinishTask(aoTaskDetailsBeanBudgetAmendment, loChannelForBudgetAmend);
		// Executing transaction which will generate XML and then will store in
		// FileNet
		HHSTransactionManager.executeTransaction(loChannelForBudgetAmend, HHSConstants.GENERATE_AMENDMENT_BUDGET_DATA);

		if (HHSConstants.NEGATIVE.equalsIgnoreCase(aoTaskDetailsBeanBudgetAmendment.getAmendmentType()))
		{
			// Get count of Approved Budgets for the contract
			HHSTransactionManager.executeTransaction(loChannelForBudgetAmend,
					HHSConstants.FETCH_COUNT_BUDGET_AMENDMENT_APPROVED);
			Integer loCount = (Integer) loChannelForBudgetAmend.getData(HHSConstants.AI_ROW_COUNT);

			loHmWFComponentForBudgetAmend.put(HHSConstants.COMPONENT_ACTION,
					HHSConstants.MERGE_BUDGET_AMENDMENT_COMPONENT_ACTION);
			loHmWFComponentForBudgetAmend.put(HHSConstants.VALUES,
					CommonUtil.convertBeanToString(aoTaskDetailsBeanBudgetAmendment));
			loHmWFComponentForBudgetAmend.put(HHSConstants.APPROVED_BUDGET_COUNT, loCount);
			loHmWFComponentForBudgetAmend.put(HHSConstants.CONTRACT_BUDGET_STATUS, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_BUDGET_APPROVED));
			loChannelForBudgetAmend.setData(HHSConstants.LO_HM_WF_PROPERTIES, loHmWFComponentForBudgetAmend);
			loChannelForBudgetAmend.setData(HHSConstants.WORK_FLOW_NAME, HHSConstants.WF_FINANCIAL_UTILITY);

			// This transaction need to be re-factored to Component
			if (lbFinalFinish)
			{
				HHSTransactionManager.executeTransaction(loChannelForBudgetAmend,
						HHSConstants.MERGE_BUDGET_AMENDMENT_REVIEW_TASK);
			}
		}
		loChannelForBudgetAmend.setData(HHSConstants.AO_TASK_DETAILS_BEAN, aoTaskDetailsBeanBudgetAmendment);
		// Start R5 : set EntityId and EntityName for AutoSave
		CommonUtil.setChannelForAutoSaveData(loChannelForBudgetAmend, aoTaskDetailsBeanBudgetAmendment.getWorkFlowId(),
				HHSR5Constants.TASKS);
		// End R5 : set EntityId and EntityName for AutoSave
		HHSTransactionManager.executeTransaction(loChannelForBudgetAmend,
				HHSConstants.FINISH_BUDGET_AMENDMENT_REVIEW_TASK);

		loTaskParamMapForBudgetAmend.put(HHSConstants.PAGE_ERROR, HHSConstants.EMPTY_STRING);
		loTaskParamMapForBudgetAmend.put(HHSConstants.TASK_ERROR, HHSConstants.EMPTY_STRING);
		return loTaskParamMapForBudgetAmend;
	}

	/**
	 * This method decide the execution flow on click of Finish Task button with
	 * task status Return For Revision for Contract budget Amendment Review Task
	 * <ul>
	 * <li>Set Audit details in channel by calling 'addAuditDataToChannel'
	 * Utility method</li>
	 * <li>Execute Transaction to Return For Revision Contract budget Amendment
	 * review Task</li>
	 * </ul>
	 * 
	 * @param aoTaskDetailsBeanForBudgetAmend TaskDetailsBean object
	 * @return loTaskParamMap with task details
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Map taskReturn(TaskDetailsBean aoTaskDetailsBeanForBudgetAmend) throws ApplicationException
	{
		Map loTaskParamMapForBudgetUpdate = new HashMap();
		loTaskParamMapForBudgetUpdate.put(HHSConstants.TASK_ERROR, HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED);
		loTaskParamMapForBudgetUpdate.put(HHSConstants.PAGE_ERROR, HHSConstants.EMPTY_STRING);
		Channel loChannelForBudgetAmend = new Channel();
		boolean lbFinalFinish = false;
		String lsCurrentLevel = aoTaskDetailsBeanForBudgetAmend.getLevel();
		if (lsCurrentLevel.equalsIgnoreCase(HHSConstants.ONE))
		{
			lbFinalFinish = true;
		}
		loChannelForBudgetAmend.setData(HHSConstants.AO_FILENET_SESSION,
				aoTaskDetailsBeanForBudgetAmend.getP8UserSession());
		loChannelForBudgetAmend.setData(HHSConstants.AO_TASK_DETAILS_BEAN, aoTaskDetailsBeanForBudgetAmend);
		loChannelForBudgetAmend.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
		loChannelForBudgetAmend.setData(HHSConstants.LB_FINAL_FINISH, lbFinalFinish);
		loChannelForBudgetAmend.setData(HHSConstants.AO_BUDGET_STATUS, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.BUDGET_RETURNED_FOR_REVISION));
		HHSUtil.setAuditOnFinancialFinishTask(aoTaskDetailsBeanForBudgetAmend, loChannelForBudgetAmend);
		// Start R5 : set EntityId and EntityName for AutoSave
		CommonUtil.setChannelForAutoSaveData(loChannelForBudgetAmend, aoTaskDetailsBeanForBudgetAmend.getWorkFlowId(),
				HHSR5Constants.TASKS);
		// End R5 : set EntityId and EntityName for AutoSave

		HHSTransactionManager.executeTransaction(loChannelForBudgetAmend,
				HHSConstants.FINISH_BUDGET_AMENDMENT_RETURNED_TASK);

		loTaskParamMapForBudgetUpdate.put(HHSConstants.PAGE_ERROR, HHSConstants.EMPTY_STRING);
		loTaskParamMapForBudgetUpdate.put(HHSConstants.TASK_ERROR, HHSConstants.EMPTY_STRING);
		return loTaskParamMapForBudgetUpdate;
	}

}
