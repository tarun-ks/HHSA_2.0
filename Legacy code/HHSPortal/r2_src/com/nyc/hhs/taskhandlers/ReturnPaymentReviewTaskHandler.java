package com.nyc.hhs.taskhandlers;

import java.util.HashMap;
import java.util.Map;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This ReturnPaymentReviewTaskHandler will implement all the abstract methods
 * of MainTaskHandler and hence implement Task related functionalities of Returned
 * Payment Task
 * 
 */
public class ReturnPaymentReviewTaskHandler extends MainTaskHandler
{

	private static final LogInfo LOG_OBJECT = new LogInfo(ReturnPaymentReviewTaskHandler.class);

	/**
	 * This method decide the execution flow on click of Finish Task button with
	 * task status Approved for Returned Payment Task
	 * <ul>
	 * <li>Set Audit details in channel by calling
	 * 'setAuditOnFinancialFinishTask' Utility method</li>
	 * <li>Execute Transaction to Return Review Task by executing
	 * 'finishReturnPaymentReviewTask'</li>
	 * </ul>
	 * 
	 * @param aoTaskDetailsBeanForReturnPaymentRequest TaskDetailsBean object
	 * @return Map loTaskParamMap with task details
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Map taskApprove(TaskDetailsBean aoTaskDetailsBeanForReturnPaymentRequest) throws ApplicationException
	{
		Map loTaskParamMapForReturnPaymentRequest = new HashMap();
		HashMap loHmWFReqProps = new HashMap();
		String lsErrorMsg = HHSConstants.EMPTY_STRING;
		boolean lbRevewLevelError = false;
		try
		{
			loTaskParamMapForReturnPaymentRequest.put(HHSConstants.TASK_ERROR,
					HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED);
			loTaskParamMapForReturnPaymentRequest.put(HHSConstants.PAGE_ERROR, lsErrorMsg);
			String lsCurrentLevel = aoTaskDetailsBeanForReturnPaymentRequest.getLevel();
			String lsTotalLevel = aoTaskDetailsBeanForReturnPaymentRequest.getTotalLevel();
			aoTaskDetailsBeanForReturnPaymentRequest.setEntityName(HHSR5Constants.RETURNED_PAYMENT_STRING);
			boolean lbFinalFinish = false;
			Channel loChannelForReturnPaymentRequest = new Channel();
			if (lsCurrentLevel.equalsIgnoreCase(lsTotalLevel))
			{
				lbFinalFinish = true;
			}
			loChannelForReturnPaymentRequest.setData(HHSConstants.LO_HM_WF_REQ_PROPS, loHmWFReqProps);
			loChannelForReturnPaymentRequest.setData(HHSConstants.AO_FILENET_SESSION,
					aoTaskDetailsBeanForReturnPaymentRequest.getP8UserSession());
			loChannelForReturnPaymentRequest.setData(HHSConstants.AO_TASK_DETAILS_BEAN,
					aoTaskDetailsBeanForReturnPaymentRequest);
			loChannelForReturnPaymentRequest.setData(HHSConstants.REVIEWER_LEVEL_KEY, lsCurrentLevel);
			// First validation
			if (lbFinalFinish)
			{
				loChannelForReturnPaymentRequest.setData(HHSConstants.PROPERTY_PE_AGENCY_ID,
						aoTaskDetailsBeanForReturnPaymentRequest.getAgencyId());
				loChannelForReturnPaymentRequest.setData(HHSConstants.REVIEW_PROC_ID,
						HHSConstants.ADVANCE_PAYMENT_REVIEW_ID);
				// Revew level Validation
				HHSTransactionManager.executeTransaction(loChannelForReturnPaymentRequest,
						HHSConstants.FETCH_REVIEW_LEVEL_CB);
				Integer loReviewLevel = (Integer) loChannelForReturnPaymentRequest.getData(HHSConstants.REVIEW_LEVEL);
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
				loChannelForReturnPaymentRequest.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
				loChannelForReturnPaymentRequest.setData(HHSConstants.LB_FINAL_FINISH, lbFinalFinish);
				loChannelForReturnPaymentRequest.setData(HHSConstants.AO_BUDGET_STATUS, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSR5Constants.STATUS_RET_PAY_APPROVED));
				loChannelForReturnPaymentRequest.setData(HHSConstants.RETURN_PAYMENT_DETAIL_ID,
						aoTaskDetailsBeanForReturnPaymentRequest.getReturnPaymentDetailId());
				HHSUtil.setAuditOnFinancialFinishTask(aoTaskDetailsBeanForReturnPaymentRequest,
						loChannelForReturnPaymentRequest);
				if (lbFinalFinish)
				{
					HHSUtil.setAuditForApprovedStatus(aoTaskDetailsBeanForReturnPaymentRequest, loChannelForReturnPaymentRequest);
				}
				CommonUtil.setChannelForAutoSaveData(loChannelForReturnPaymentRequest,
						aoTaskDetailsBeanForReturnPaymentRequest.getWorkFlowId(), HHSR5Constants.TASKS);
				HHSTransactionManager.executeTransaction(loChannelForReturnPaymentRequest,
						HHSR5Constants.TASK_FINISH_RETURNED_PAYMENT_REVIEW, HHSR5Constants.TRANSACTION_ELEMENT_R5);
			}
		}
		catch (ApplicationException aoExp)
		{
			aoExp.addContextData(
					"ApplicationException occured while executing task approve for Returned Payment Task", aoExp);
			LOG_OBJECT.Error("ApplicationException occured while executing task approve for Returned Payment Task", aoExp);
			throw aoExp;
		}
		catch (Exception aoExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while executing task approve for Returned Payment Task ", aoExp);
			loAppEx.addContextData(
					"Exception occured while executing task approve for Returned Payment Task", aoExp);
			LOG_OBJECT.Error("Exception occured while executing task approve for Returned Payment Task", aoExp);
			throw loAppEx;
		}
		loTaskParamMapForReturnPaymentRequest.put(HHSConstants.PAGE_ERROR, lsErrorMsg);
		loTaskParamMapForReturnPaymentRequest.put(HHSConstants.TASK_ERROR, HHSConstants.EMPTY_STRING);
		return loTaskParamMapForReturnPaymentRequest;
	}

	/**
	 * This method decide the execution flow on click of Finish Task button with
	 * task status Return For Revision for Returned Payment Task
	 * <ul>
	 * <li>This method is updated in R4</li>
	 * <li>Set Audit details in channel by calling
	 * 'setAuditOnFinancialFinishTask' Utility method</li>
	 * <li>Execute Transaction to Return For Revision Invoice review Task by
	 * executing 'finishReturnPaymentReturnReviewTask'</li>
	 * </ul>
	 * 
	 * @param aoTaskReturnDetailsBeanForReturnPayment TaskDetailsBean object
	 * @return Map loTaskParamMapForReturnPaymentRequest with task details
	 * @throws ApplicationException If an Application Exception occurs
	 * 
	 * 
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Map taskReturn(TaskDetailsBean aoTaskReturnDetailsBeanForReturnPayment) throws ApplicationException
	{
		Map loTaskParamMapForReturnPaymentRequest = new HashMap();
		try
		{
			loTaskParamMapForReturnPaymentRequest.put(HHSConstants.TASK_ERROR, HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED);
			loTaskParamMapForReturnPaymentRequest.put(HHSConstants.PAGE_ERROR, HHSConstants.EMPTY_STRING);
			Channel loChannelForReturnPaymentRequest = new Channel();
			boolean lbFinalFinish = false;
			String lsCurrentLevel = aoTaskReturnDetailsBeanForReturnPayment.getLevel();
			aoTaskReturnDetailsBeanForReturnPayment.setEntityName(HHSR5Constants.RETURNED_PAYMENT_STRING);
			loChannelForReturnPaymentRequest.setData(HHSConstants.AO_FILENET_SESSION,
					aoTaskReturnDetailsBeanForReturnPayment.getP8UserSession());
			loChannelForReturnPaymentRequest.setData(HHSConstants.AO_TASK_DETAILS_BEAN,
					aoTaskReturnDetailsBeanForReturnPayment);
			loChannelForReturnPaymentRequest.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
			loChannelForReturnPaymentRequest.setData(HHSConstants.AO_BUDGET_STATUS, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSR5Constants.STATUS_RET_PAY_CANCELLED));
			HHSUtil.setAuditOnFinancialFinishTask(aoTaskReturnDetailsBeanForReturnPayment, loChannelForReturnPaymentRequest);
			if (lsCurrentLevel.equalsIgnoreCase(HHSConstants.ONE))
			{
				lbFinalFinish = true;
				HHSUtil.setAuditForCancelStatus(aoTaskReturnDetailsBeanForReturnPayment,loChannelForReturnPaymentRequest);
			}
			loChannelForReturnPaymentRequest.setData(HHSConstants.LB_FINAL_FINISH, lbFinalFinish);
			CommonUtil.setChannelForAutoSaveData(loChannelForReturnPaymentRequest,
					aoTaskReturnDetailsBeanForReturnPayment.getWorkFlowId(), HHSR5Constants.TASKS);
			HHSTransactionManager.executeTransaction(loChannelForReturnPaymentRequest,
					HHSR5Constants.TRANSACTION_FOR_RETURN_TASK_PAYMENT_REVIEW, HHSR5Constants.TRANSACTION_ELEMENT_R5);
		}
		catch (ApplicationException aoExp)
		{
			aoExp.addContextData(
					"ApplicationException occured while executing task return for Returned Payment Task", aoExp);
			LOG_OBJECT.Error("Exception occured while executing task return for Returned Payment Task", aoExp);
			throw aoExp;
		}
		catch (Exception aoExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while executing task return for Returned Payment Task ", aoExp);
			loAppEx.addContextData(
					"Exception occured while executing task return for Returned Payment Task", aoExp);
			LOG_OBJECT.Error("Exception occured while executing task return for Returned Payment Task", aoExp);
			throw loAppEx;
		}
		loTaskParamMapForReturnPaymentRequest.put(HHSConstants.PAGE_ERROR, HHSConstants.EMPTY_STRING);
		loTaskParamMapForReturnPaymentRequest.put(HHSConstants.TASK_ERROR, HHSConstants.EMPTY_STRING);
		return loTaskParamMapForReturnPaymentRequest;
	}
}
