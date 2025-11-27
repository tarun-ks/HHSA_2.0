package com.nyc.hhs.taskhandlers;

import java.util.HashMap;
import java.util.Map;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.daomanager.service.FinancialsService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This ContractBudgetReviewTaskHandler will implement all the abstract methods
 * of MainTaskHandler and hence implement Task related functionalities of
 * BudgetReviewTask
 * 
 */
public class ContractBudgetReviewTaskHandler extends MainTaskHandler
{
	private static final LogInfo LOG_OBJECT = new LogInfo(FinancialsService.class);

	/**
	 * This method updated for Release 3.12.0 enhancement 6644 This method
	 * decide the execution flow on click of Finish Task button with task status
	 * Approved for Contract budget Review Task
	 * <ul>
	 * <li>Set Audit details in channel by calling 'addAuditDataToChannel'
	 * Utility method</li>
	 * <li>Execute Transaction to Contract budget Review Task</li>
	 * <li>This transaction has service to insert entry in pdf table so its pdf
	 * can be generated and saved in filenet through batch.</li>
	 * <li>Two service method are added for agency interface module.It update
	 * remaining and YTD invoiced amount in line items table on task approval.</li>
	 * <li>This method was updated in R4.</li>
	 * </ul>
	 * 
	 * @param aoTaskDetailsBeanForBudgetReview TaskDetailsBean object
	 * @return Map loTaskParamMap with task details
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Map taskApprove(TaskDetailsBean aoTaskDetailsBeanForBudgetReview) throws ApplicationException
	{
		Map loTaskParamMapForBudgetReview = new HashMap();
		Map loHmWFReqProps = new HashMap();
		Map loHmWFComponentForBudgetReview = new HashMap();
		loTaskParamMapForBudgetReview.put(HHSConstants.TASK_ERROR, HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED);
		loTaskParamMapForBudgetReview.put(HHSConstants.PAGE_ERROR, HHSConstants.EMPTY_STRING);
		String lsCurrentLevel = aoTaskDetailsBeanForBudgetReview.getLevel();
		String lsTotalLevel = aoTaskDetailsBeanForBudgetReview.getTotalLevel();
		boolean lbFinalFinish = false;
		Channel loChannelForBudgetReview = new Channel();
		if (lsCurrentLevel.equalsIgnoreCase(lsTotalLevel))
		{
			lbFinalFinish = true;
		}
		loChannelForBudgetReview.setData(HHSConstants.LO_HM_WF_REQ_PROPS, loHmWFReqProps);
		loChannelForBudgetReview.setData(HHSConstants.AO_FILENET_SESSION,
				aoTaskDetailsBeanForBudgetReview.getP8UserSession());
		loChannelForBudgetReview.setData(HHSConstants.AO_TASK_DETAILS_BEAN, aoTaskDetailsBeanForBudgetReview);
		loChannelForBudgetReview.setData(HHSConstants.BUDGET_ID_WORKFLOW,
				aoTaskDetailsBeanForBudgetReview.getBudgetId());
		loChannelForBudgetReview.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
		loChannelForBudgetReview.setData(HHSConstants.LB_FINAL_FINISH, lbFinalFinish);
		loChannelForBudgetReview
				.setData(HHSConstants.CONTRACT_ID_KEY, aoTaskDetailsBeanForBudgetReview.getContractId());
		loChannelForBudgetReview.setData(HHSConstants.AO_BUDGET_STATUS, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_BUDGET_APPROVED));

		// changes for agency outbound interafce 6644
		loChannelForBudgetReview.setData(HHSConstants.AS_USER_ID, HHSConstants.SYSTEM_USER);
		aoTaskDetailsBeanForBudgetReview.setEntityStatus(ApplicationConstants.STATUS_APPROVED);
		if (lbFinalFinish)
		{

			// fetch contract status
			HHSTransactionManager.executeTransaction(loChannelForBudgetReview, HHSConstants.IS_FETCH_CONTRACT_STATUS);
			String lsContractStatus = (String) loChannelForBudgetReview.getData(HHSConstants.AS_CONTRACT_STATUS);
			loHmWFComponentForBudgetReview.put(HHSConstants.COMPONENT_ACTION,
					HHSConstants.INSERT_REPLICA_BUDGET_COMPONENT_ACTION);
			loHmWFComponentForBudgetReview.put(HHSConstants.VALUES,
					CommonUtil.convertBeanToString(aoTaskDetailsBeanForBudgetReview));
			loHmWFComponentForBudgetReview.put(HHSConstants.CONTRACT_BUDGET_STATUS, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_BUDGET_APPROVED));
			//Start R7: for defect 8644
			//fetch REQUEST_PARTIAL_MERGE status in contract table.
			HHSTransactionManager.executeTransaction(loChannelForBudgetReview, HHSConstants.FETCH_REQUEST_PARTIAL_MERGE_STATUS);
			String lsPartialMergeStatus = (String) loChannelForBudgetReview.getData(HHSConstants.REQUEST_PARTIAL_MERGE_STATUS);

			if(Integer.parseInt(lsPartialMergeStatus)>0){
				LOG_OBJECT.Info("Contract requested for partial merge so the Base contract budget will be in the Approved status instead of active");
			}
			//End
			else if (HHSConstants.CONTRACT_REGISTERED_STATUS_ID.equals(lsContractStatus))
			{
				loHmWFComponentForBudgetReview.put(HHSConstants.CONTRACT_BUDGET_STATUS, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_BUDGET_ACTIVE));
				aoTaskDetailsBeanForBudgetReview.setEntityStatus(HHSConstants.BUDGET_ACTIVE);
			}
			loChannelForBudgetReview.setData(HHSConstants.LO_HM_WF_PROPERTIES, loHmWFComponentForBudgetReview);
			loChannelForBudgetReview.setData(HHSConstants.WORK_FLOW_NAME, HHSConstants.WF_FINANCIAL_UTILITY);
			loChannelForBudgetReview.setData(HHSConstants.AO_TASK_DETAILS_BEAN, aoTaskDetailsBeanForBudgetReview);
			HHSTransactionManager.executeTransaction(loChannelForBudgetReview,
					HHSConstants.CREATE_REPLICA_BUDGET_REVIEW_TASK);
		}
		HHSUtil.setAuditOnFinancialFinishTask(aoTaskDetailsBeanForBudgetReview, loChannelForBudgetReview);
		// Start R5 : set EntityId and EntityName for AutoSave
		CommonUtil.setChannelForAutoSaveData(loChannelForBudgetReview,
				aoTaskDetailsBeanForBudgetReview.getWorkFlowId(), HHSR5Constants.TASKS);
		// End R5 : set EntityId and EntityName for AutoSave
		HHSTransactionManager.executeTransaction(loChannelForBudgetReview,
				HHSConstants.FINISH_CONTRACT_BUDGET_REVIEW_TASK);
		loTaskParamMapForBudgetReview.put(HHSConstants.PAGE_ERROR, HHSConstants.EMPTY_STRING);
		loTaskParamMapForBudgetReview.put(HHSConstants.TASK_ERROR, HHSConstants.EMPTY_STRING);
		return loTaskParamMapForBudgetReview;
	}

	/**
	 * This method decide the execution flow on click of Finish Task button with
	 * task status Return For Revision for Contract budget Review Task
	 * <ul>
	 * <li>Set Audit details in channel by calling 'addAuditDataToChannel'
	 * Utility method</li>
	 * <li>Execute Transaction to Return For Revision Contract budget review
	 * Task</li>
	 * </ul>
	 * 
	 * @param aoTaskDetailsBeanForBudgetReview TaskDetailsBean object
	 * @return Map loTaskParamMap with task details
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Map taskReturn(TaskDetailsBean aoTaskDetailsBeanForBudgetReview) throws ApplicationException
	{
		Map loTaskParamMapForBudgetReview = new HashMap();
		loTaskParamMapForBudgetReview.put(HHSConstants.TASK_ERROR, HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED);
		loTaskParamMapForBudgetReview.put(HHSConstants.PAGE_ERROR, HHSConstants.EMPTY_STRING);
		Channel loChannelForBudgetReview = new Channel();
		boolean lbFinalFinish = false;
		String lsCurrentLevel = aoTaskDetailsBeanForBudgetReview.getLevel();
		if (lsCurrentLevel.equalsIgnoreCase(HHSConstants.ONE))
		{
			lbFinalFinish = true;
		}
		loChannelForBudgetReview.setData(HHSConstants.AO_FILENET_SESSION,
				aoTaskDetailsBeanForBudgetReview.getP8UserSession());
		loChannelForBudgetReview.setData(HHSConstants.AO_TASK_DETAILS_BEAN, aoTaskDetailsBeanForBudgetReview);
		loChannelForBudgetReview.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
		loChannelForBudgetReview.setData(HHSConstants.LB_FINAL_FINISH, lbFinalFinish);
		loChannelForBudgetReview.setData(HHSConstants.AO_BUDGET_STATUS, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.BUDGET_RETURNED_FOR_REVISION));
		HHSUtil.setAuditOnFinancialFinishTask(aoTaskDetailsBeanForBudgetReview, loChannelForBudgetReview);
		// Start R5 : set EntityId and EntityName for AutoSave
		CommonUtil.setChannelForAutoSaveData(loChannelForBudgetReview,
				aoTaskDetailsBeanForBudgetReview.getWorkFlowId(), HHSR5Constants.TASKS);
		// End R5 : set EntityId and EntityName for AutoSave
		HHSTransactionManager.executeTransaction(loChannelForBudgetReview,
				HHSConstants.FINISH_BUDGET_RETURN_REVIEW_TASK);

		loTaskParamMapForBudgetReview.put(HHSConstants.PAGE_ERROR, HHSConstants.EMPTY_STRING);
		loTaskParamMapForBudgetReview.put(HHSConstants.TASK_ERROR, HHSConstants.EMPTY_STRING);
		return loTaskParamMapForBudgetReview;
	}

}
