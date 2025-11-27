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
 * This ContractBudgetModificationReviewTaskHandler will implement all the
 * abstract methods of MainTaskHandler and hence implement Task related
 * functionalities of BudgetModificationReviewTask
 * 
 */
public class BudgetModificationTaskHandler extends MainTaskHandler
{

	/**
	 * This method decide the execution flow on click of Finish Task button with
	 * task status Approved for Contract budget modification Review Task
	 * <ul>
	 * <li>Set Audit details in channel by calling 'addAuditDataToChannel'
	 * Utility method</li>
	 * <li>Execute Transaction to Contract budget modification Review Task</li>
	 * </ul>
	 * 
	 * @param aoBudgetModificationTaskDetailsBean TaskDetailsBean object
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Map taskApprove(TaskDetailsBean aoBudgetModificationTaskDetailsBean) throws ApplicationException
	{
		Map loTaskParamMap = new HashMap();
		Map loHmWFReqProps = new HashMap();
		Map loHmWFComponentForBudgetModification = new HashMap();
		loTaskParamMap.put(HHSConstants.TASK_ERROR, HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED);
		loTaskParamMap.put(HHSConstants.PAGE_ERROR, HHSConstants.EMPTY_STRING);
		String lsCurrentLevel = aoBudgetModificationTaskDetailsBean.getLevel();
		String lsTotalLevel = aoBudgetModificationTaskDetailsBean.getTotalLevel();
		boolean lbFinalFinish = false;
		Channel loChannelForBudgetModification = new Channel();
		if (lsCurrentLevel.equalsIgnoreCase(lsTotalLevel))
		{
			lbFinalFinish = true;
		}
		loChannelForBudgetModification.setData(HHSConstants.LO_HM_WF_REQ_PROPS, loHmWFReqProps);
		loChannelForBudgetModification.setData(HHSConstants.AO_FILENET_SESSION,
				aoBudgetModificationTaskDetailsBean.getP8UserSession());
		loChannelForBudgetModification.setData(HHSConstants.AO_TASK_DETAILS_BEAN, aoBudgetModificationTaskDetailsBean);
		loChannelForBudgetModification.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
		loChannelForBudgetModification.setData(HHSConstants.LB_FINAL_FINISH, lbFinalFinish);
		loChannelForBudgetModification.setData(HHSConstants.AO_BUDGET_STATUS, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_BUDGET_APPROVED));
		aoBudgetModificationTaskDetailsBean.setEntityStatus(ApplicationConstants.STATUS_APPROVED);
		HHSUtil.setAuditOnFinancialFinishTask(aoBudgetModificationTaskDetailsBean, loChannelForBudgetModification);

		if (lbFinalFinish)
		{
			loHmWFComponentForBudgetModification.put(HHSConstants.COMPONENT_ACTION,
					HHSConstants.MERGE_BUDGET_MOD_COMPONENT_ACTION);
			loHmWFComponentForBudgetModification.put(HHSConstants.VALUES,
					CommonUtil.convertBeanToString(aoBudgetModificationTaskDetailsBean));
			loHmWFComponentForBudgetModification.put(HHSConstants.CONTRACT_BUDGET_STATUS, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_BUDGET_APPROVED));
			loChannelForBudgetModification.setData(HHSConstants.LO_HM_WF_PROPERTIES,
					loHmWFComponentForBudgetModification);
			loChannelForBudgetModification.setData(HHSConstants.WORK_FLOW_NAME, HHSConstants.WF_FINANCIAL_UTILITY);
			//added in R7 to reset the show info flag
			loChannelForBudgetModification.setData(HHSConstants.BUDGET_ID, aoBudgetModificationTaskDetailsBean.getBudgetId());
			//R7 end
			HHSTransactionManager.executeTransaction(loChannelForBudgetModification,
					HHSConstants.MERGE_BUDGET_MODIFICATION_REVIEW_TASK);
		}
		loChannelForBudgetModification.setData(HHSConstants.AO_TASK_DETAILS_BEAN, aoBudgetModificationTaskDetailsBean);
		// Start R5 : set EntityId and EntityName for AutoSave
		CommonUtil.setChannelForAutoSaveData(loChannelForBudgetModification,
				aoBudgetModificationTaskDetailsBean.getWorkFlowId(), HHSR5Constants.TASKS);
		// End R5 : set EntityId and EntityName for AutoSave
		HHSTransactionManager.executeTransaction(loChannelForBudgetModification,
				HHSConstants.FINISH_BUDGET_MODIFICATION_REVIEW_TASK);
		loTaskParamMap.put(HHSConstants.PAGE_ERROR, HHSConstants.EMPTY_STRING);
		loTaskParamMap.put(HHSConstants.TASK_ERROR, HHSConstants.EMPTY_STRING);
		return loTaskParamMap;
	}

	/**
	 * This method decide the execution flow on click of Finish Task button with
	 * task status Return For Revision for Contract budget modification Review
	 * Task
	 * <ul>
	 * <li>Set Audit details in channel by calling 'addAuditDataToChannel'
	 * Utility method</li>
	 * <li>Execute Transaction to Return For Revision Contract budget
	 * modification review Task</li>
	 * </ul>
	 * 
	 * @param aoTaskDetailsBeanForBudgetModification TaskDetailsBean object
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Map taskReturn(TaskDetailsBean aoTaskDetailsBeanForBudgetModification) throws ApplicationException
	{
		Map loTaskParamMap = new HashMap();
		loTaskParamMap.put(HHSConstants.TASK_ERROR, HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED);
		loTaskParamMap.put(HHSConstants.PAGE_ERROR, HHSConstants.EMPTY_STRING);
		Channel loChannelForBudgetModification = new Channel();
		boolean lbFinalFinish = false;
		String lsCurrentLevel = aoTaskDetailsBeanForBudgetModification.getLevel();
		if (lsCurrentLevel.equalsIgnoreCase(HHSConstants.ONE))
		{
			lbFinalFinish = true;
		}
		loChannelForBudgetModification.setData(HHSConstants.AO_FILENET_SESSION,
				aoTaskDetailsBeanForBudgetModification.getP8UserSession());
		loChannelForBudgetModification.setData(HHSConstants.AO_TASK_DETAILS_BEAN,
				aoTaskDetailsBeanForBudgetModification);
		loChannelForBudgetModification.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
		loChannelForBudgetModification.setData(HHSConstants.LB_FINAL_FINISH, lbFinalFinish);
		loChannelForBudgetModification.setData(HHSConstants.AO_BUDGET_STATUS, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.BUDGET_RETURNED_FOR_REVISION));
		HHSUtil.setAuditOnFinancialFinishTask(aoTaskDetailsBeanForBudgetModification, loChannelForBudgetModification);
		// Start R5 : set EntityId and EntityName for AutoSave
		CommonUtil.setChannelForAutoSaveData(loChannelForBudgetModification,
				aoTaskDetailsBeanForBudgetModification.getWorkFlowId(), HHSR5Constants.TASKS);
		// End R5 : set EntityId and EntityName for AutoSave
		HHSTransactionManager.executeTransaction(loChannelForBudgetModification,
				HHSConstants.FINISH_BUDGET_MOD_RETURNED_REVIEW_TASK);

		loTaskParamMap.put(HHSConstants.PAGE_ERROR, HHSConstants.EMPTY_STRING);
		loTaskParamMap.put(HHSConstants.TASK_ERROR, HHSConstants.EMPTY_STRING);
		return loTaskParamMap;
	}

}
