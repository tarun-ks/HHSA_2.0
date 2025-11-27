package com.nyc.hhs.taskhandlers;

import java.util.HashMap;
import java.util.Map;

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
 * This AdvanceRequestReviewTaskHandler will implement all the abstract methods
 * of MainTaskHandler and hence implement Task related functionalities of
 * AdvanceRequestReviewTask
 * 
 */
public class AdvanceRequestReviewTaskHandler extends MainTaskHandler
{

	/**
	 * This method decide the execution flow on click of Finish Task button with
	 * task status Approved for Invoice Review Task
	 * <ul>
	 * <li>Set Audit details in channel by calling 'addAuditDataToChannel'
	 * Utility method</li>
	 * <li>Execute Transaction to Invoice Review Task</li>
	 * </ul>
	 * 
	 * @param aoTaskDetailsBeanForAdvanceRequest TaskDetailsBean object
	 * @return Map loTaskParamMap with task details
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Map taskApprove(TaskDetailsBean aoTaskDetailsBeanForAdvanceRequest) throws ApplicationException
	{
		Map loTaskParamMapForAdvanceRequest = new HashMap();
		HashMap loHmWFReqProps = new HashMap();
		String lsErrorMsg = HHSConstants.EMPTY_STRING;
		Map<String, Object> loErrorMap = null;
		boolean lbRevewLevelError = false;
		loTaskParamMapForAdvanceRequest.put(HHSConstants.TASK_ERROR, HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED);
		loTaskParamMapForAdvanceRequest.put(HHSConstants.PAGE_ERROR, lsErrorMsg);
		String lsCurrentLevel = aoTaskDetailsBeanForAdvanceRequest.getLevel();
		String lsTotalLevel = aoTaskDetailsBeanForAdvanceRequest.getTotalLevel();
		boolean lbFinalFinish = false;
		Channel loChannelForAdvanceRequest = new Channel();
		if (lsCurrentLevel.equalsIgnoreCase(lsTotalLevel))
		{
			lbFinalFinish = true;
		}
		loChannelForAdvanceRequest.setData(HHSConstants.LO_HM_WF_REQ_PROPS, loHmWFReqProps);
		loChannelForAdvanceRequest.setData(HHSConstants.AO_FILENET_SESSION,
				aoTaskDetailsBeanForAdvanceRequest.getP8UserSession());
		loChannelForAdvanceRequest.setData(HHSConstants.AO_TASK_DETAILS_BEAN, aoTaskDetailsBeanForAdvanceRequest);
		loChannelForAdvanceRequest.setData(HHSConstants.REVIEWER_LEVEL_KEY, lsCurrentLevel);
		if (HHSConstants.ONE.equalsIgnoreCase(lsCurrentLevel))
		{
			HHSTransactionManager.executeTransaction(loChannelForAdvanceRequest,
					HHSConstants.VALIDATE_LEVEL_ONE_ADVANCE_REQUEST_FINISH_TASK);
			loErrorMap = (HashMap<String, Object>) loChannelForAdvanceRequest.getData(HHSConstants.AO_ERROR_MAP);
			int liErrorCode = (Integer) loErrorMap.get(HHSConstants.ERROR_CODE);
			if (liErrorCode != HHSConstants.INT_ZERO)
			{
				lbRevewLevelError = true;
				lsErrorMsg = (String) loErrorMap.get(HHSConstants.CLC_ERROR_MSG);
			}
		}
		// First validation
		if (lbFinalFinish)
		{
			loChannelForAdvanceRequest.setData(HHSConstants.PROPERTY_PE_AGENCY_ID,
					aoTaskDetailsBeanForAdvanceRequest.getAgencyId());
			loChannelForAdvanceRequest.setData(HHSConstants.REVIEW_PROC_ID, HHSConstants.ADVANCE_PAYMENT_REVIEW_ID);
			// Revew level Validation
			HHSTransactionManager.executeTransaction(loChannelForAdvanceRequest, HHSConstants.FETCH_REVIEW_LEVEL_CB);
			Integer loReviewLevel = (Integer) loChannelForAdvanceRequest.getData(HHSConstants.REVIEW_LEVEL);
			// If review level is not set
			if (loReviewLevel == HHSConstants.INT_ZERO)
			{
				lbRevewLevelError = true;
				lsErrorMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.CB_REVIEW_LEVEL_ERROR);
			}
		}
		if (!lbRevewLevelError)
		{
			loChannelForAdvanceRequest.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
			loChannelForAdvanceRequest.setData(HHSConstants.LB_FINAL_FINISH, lbFinalFinish);
			loChannelForAdvanceRequest.setData(HHSConstants.AO_BUDGET_STATUS,
					PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.ADVANCE_APPROVED));
			HHSUtil.setAuditOnFinancialFinishTask(aoTaskDetailsBeanForAdvanceRequest, loChannelForAdvanceRequest);
			// Start R5 : set EntityId and EntityName for AutoSave
			CommonUtil.setChannelForAutoSaveData(loChannelForAdvanceRequest,
					aoTaskDetailsBeanForAdvanceRequest.getWorkFlowId(), HHSR5Constants.TASKS);
			// End R5 : set EntityId and EntityName for AutoSave
			HHSTransactionManager.executeTransaction(loChannelForAdvanceRequest,
					HHSConstants.FINISH_ADVANCE_REQUEST_REVIEW_TASK);
		}
		loTaskParamMapForAdvanceRequest.put(HHSConstants.PAGE_ERROR, lsErrorMsg);
		loTaskParamMapForAdvanceRequest.put(HHSConstants.TASK_ERROR, HHSConstants.EMPTY_STRING);
		return loTaskParamMapForAdvanceRequest;
	}

	/**
	 * This method decide the execution flow on click of Finish Task button with
	 * task status Return For Revision for Contract budget Review Task
	 * <ul>
	 * <li>Set Audit details in channel by calling 'addAuditDataToChannel'
	 * Utility method</li>
	 * <li>Execute Transaction to Return For Revision Invoice review Task</li>
	 * </ul>
	 * 
	 * @param aoTaskDetailsBeanForAdvanceRequest TaskDetailsBean object
	 * @return Map loTaskParamMap with task details
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Map taskReturn(TaskDetailsBean aoTaskDetailsBeanForAdvanceRequest) throws ApplicationException
	{
		Map loTaskParamMapForAdvanceRequest = new HashMap();
		loTaskParamMapForAdvanceRequest.put(HHSConstants.TASK_ERROR, HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED);
		loTaskParamMapForAdvanceRequest.put(HHSConstants.PAGE_ERROR, HHSConstants.EMPTY_STRING);
		Channel loChannelForAdvancerequest = new Channel();
		boolean lbFinalFinish = false;
		String lsCurrentLevel = aoTaskDetailsBeanForAdvanceRequest.getLevel();
		if (lsCurrentLevel.equalsIgnoreCase(HHSConstants.ONE))
		{
			lbFinalFinish = true;
		}
		loChannelForAdvancerequest.setData(HHSConstants.AO_FILENET_SESSION,
				aoTaskDetailsBeanForAdvanceRequest.getP8UserSession());
		loChannelForAdvancerequest.setData(HHSConstants.AO_TASK_DETAILS_BEAN, aoTaskDetailsBeanForAdvanceRequest);
		loChannelForAdvancerequest.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
		loChannelForAdvancerequest.setData(HHSConstants.LB_FINAL_FINISH, lbFinalFinish);
		// Not using the budget status.
		loChannelForAdvancerequest.setData(HHSConstants.AO_BUDGET_STATUS, "0");

		HHSUtil.setAuditOnFinancialFinishTask(aoTaskDetailsBeanForAdvanceRequest, loChannelForAdvancerequest);
		// Start R5 : set EntityId and EntityName for AutoSave
		CommonUtil.setChannelForAutoSaveData(loChannelForAdvancerequest,
				aoTaskDetailsBeanForAdvanceRequest.getWorkFlowId(), HHSR5Constants.TASKS);
		// End R5 : set EntityId and EntityName for AutoSave
		HHSTransactionManager.executeTransaction(loChannelForAdvancerequest,
				HHSConstants.FINISH_ADVANCE_REQUEST_RETURN_REVIEW_TASK);

		loTaskParamMapForAdvanceRequest.put(HHSConstants.PAGE_ERROR, HHSConstants.EMPTY_STRING);
		loTaskParamMapForAdvanceRequest.put(HHSConstants.TASK_ERROR, HHSConstants.EMPTY_STRING);
		return loTaskParamMapForAdvanceRequest;
	}

}
