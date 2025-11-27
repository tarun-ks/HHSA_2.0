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
 * This ContractBudgetReviewTaskHandler will implement all the abstract methods
 * of MainTaskHandler and hence implement Task related functionalities of
 * BudgetReviewTask
 * 
 */
public class InvoiceReviewTaskHandler extends MainTaskHandler
{

	/**
	 * This method modified as a part of enhancement 6576 release 3.10.0
	 * 
	 * This method decide the execution flow on click of Finish Task button with
	 * task status Approved for Invoice Review Task
	 * <ul>
	 * <li>Set Audit details in channel by calling 'addAuditDataToChannel'
	 * Utility method</li>
	 * <li>Execute Transaction to Invoice Review Task</li>
	 * <li>Two service method are added for agency interface module.It update
	 * remaining and YTD invoiced amount in line items table on task approval.</li>
	 * </ul>
	 * 
	 * @param aoTaskDetailsBeanForInvoiceReview TaskDetailsBean object
	 * @return Map loTaskParamMap with task details
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Map taskApprove(TaskDetailsBean aoTaskDetailsBeanForInvoiceReview) throws ApplicationException
	{
		Map loTaskParamMapForInvoiceReview = new HashMap();
		HashMap loHmWFReqProps = new HashMap();
		Map<String, Object> loErrorMap = null;
		String lsErrorMsg = HHSConstants.EMPTY_STRING;
		boolean lbRevewLevelError = false;
		loTaskParamMapForInvoiceReview.put(HHSConstants.TASK_ERROR, HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED);
		loTaskParamMapForInvoiceReview.put(HHSConstants.PAGE_ERROR, lsErrorMsg);
		String lsCurrentLevel = aoTaskDetailsBeanForInvoiceReview.getLevel();
		String lsTotalLevel = aoTaskDetailsBeanForInvoiceReview.getTotalLevel();
		boolean lbFinalFinish = false;
		Channel loChannelForInvoiceReview = new Channel();
		if (lsCurrentLevel.equalsIgnoreCase(lsTotalLevel))
		{
			lbFinalFinish = true;
		}
		loChannelForInvoiceReview.setData(HHSConstants.LO_HM_WF_REQ_PROPS, loHmWFReqProps);
		loChannelForInvoiceReview.setData(HHSConstants.AO_FILENET_SESSION,
				aoTaskDetailsBeanForInvoiceReview.getP8UserSession());
		loChannelForInvoiceReview.setData(HHSConstants.AO_TASK_DETAILS_BEAN, aoTaskDetailsBeanForInvoiceReview);
		loChannelForInvoiceReview
				.setData(HHSConstants.INVOICE_ID_KEY, aoTaskDetailsBeanForInvoiceReview.getInvoiceId());
		loChannelForInvoiceReview.setData(HHSConstants.CONTRACT_ID_KEY,
				aoTaskDetailsBeanForInvoiceReview.getContractId());
		loChannelForInvoiceReview.setData(HHSConstants.REVIEWER_LEVEL_KEY, lsCurrentLevel);
		// changes done as a part of release 3.10.0 enhancement 6576 - start
		loChannelForInvoiceReview.setData(HHSConstants.BUDGET_ID, aoTaskDetailsBeanForInvoiceReview.getBudgetId());
		// changes done as a part of release 3.10.0 enhancement 6576 - end
		// First validation
		HHSTransactionManager.executeTransaction(loChannelForInvoiceReview,
				HHSConstants.FINISH_INVOICING_REVIEW_TASK_ERROR_CHECK);
		loErrorMap = (HashMap<String, Object>) loChannelForInvoiceReview.getData(HHSConstants.AO_ERROR_MAP);
		String lsErrorCode = (String) loErrorMap.get(HHSConstants.ERROR_CODE);
		lsErrorMsg = (String) loErrorMap.get(HHSConstants.CLC_ERROR_MSG);
		if (lsErrorCode == HHSConstants.ONE)
		{
			if (lbFinalFinish)
			{
				loChannelForInvoiceReview.setData(HHSConstants.PROPERTY_PE_AGENCY_ID,
						aoTaskDetailsBeanForInvoiceReview.getAgencyId());
				loChannelForInvoiceReview.setData(HHSConstants.REVIEW_PROC_ID, HHSConstants.PAYMENT_REVIEW_ID);
				// Review level Validation
				HHSTransactionManager.executeTransaction(loChannelForInvoiceReview, HHSConstants.FETCH_REVIEW_LEVEL_CB);
				Integer loReviewLevel = (Integer) loChannelForInvoiceReview.getData(HHSConstants.REVIEW_LEVEL);
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
				loChannelForInvoiceReview.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
				loChannelForInvoiceReview.setData(HHSConstants.LB_FINAL_FINISH, lbFinalFinish);
				loChannelForInvoiceReview.setData(HHSConstants.AO_BUDGET_STATUS, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_INVOICE_APPROVED));
				aoTaskDetailsBeanForInvoiceReview.setEntityStatus(ApplicationConstants.STATUS_APPROVED);
				HHSUtil.setAuditOnFinancialFinishTask(aoTaskDetailsBeanForInvoiceReview, loChannelForInvoiceReview);
				// changes for agency outbound interafce 6644
				loChannelForInvoiceReview.setData(HHSConstants.AS_USER_ID,
						aoTaskDetailsBeanForInvoiceReview.getUserId());
				loChannelForInvoiceReview.setData(HHSConstants.AO_TASK_DETAILS_BEAN, aoTaskDetailsBeanForInvoiceReview);
				// Start R5 : set EntityId and EntityName for AutoSave
				CommonUtil.setChannelForAutoSaveData(loChannelForInvoiceReview,
						aoTaskDetailsBeanForInvoiceReview.getWorkFlowId(), HHSR5Constants.TASKS);
				// End R5 : set EntityId and EntityName for AutoSave
				HHSTransactionManager.executeTransaction(loChannelForInvoiceReview,
						HHSConstants.FINISH_INVOICING_REVIEW_TASK);
			}
		}
		loTaskParamMapForInvoiceReview.put(HHSConstants.PAGE_ERROR, lsErrorMsg);
		loTaskParamMapForInvoiceReview.put(HHSConstants.TASK_ERROR, HHSConstants.EMPTY_STRING);
		return loTaskParamMapForInvoiceReview;
	}

	/**
	 * This method updated for Release 3.12.0 enhancement 6644 This method
	 * decide the execution flow on click of Finish Task button with task status
	 * Return For Revision for Contract budget Review Task
	 * <ul>
	 * <li>Set Audit details in channel by calling 'addAuditDataToChannel'
	 * Utility method</li>
	 * <li>Execute Transaction to Return For Revision Invoice review Task</li>
	 * <li>Two service method are added for agency interface module.It update
	 * remaining and YTD invoiced amount in line items table when task is
	 * returned.</li>
	 * <li>This method was update in R4.</li>
	 * </ul>
	 * 
	 * @param aoTaskDetailsBeanForInvoiceReview TaskDetailsBean object
	 * @return Map loTaskParamMap with task details
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Map taskReturn(TaskDetailsBean aoTaskDetailsBeanForInvoiceReview) throws ApplicationException
	{
		Map loTaskParamMapForInvoiceReview = new HashMap();
		loTaskParamMapForInvoiceReview.put(HHSConstants.TASK_ERROR, HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED);
		loTaskParamMapForInvoiceReview.put(HHSConstants.PAGE_ERROR, HHSConstants.EMPTY_STRING);
		Channel loChannelForInvoiceReview = new Channel();
		boolean lbFinalFinish = false;
		String lsCurrentLevel = aoTaskDetailsBeanForInvoiceReview.getLevel();
		if (lsCurrentLevel.equalsIgnoreCase(HHSConstants.ONE))
		{
			lbFinalFinish = true;
		}
		loChannelForInvoiceReview.setData(HHSConstants.AO_FILENET_SESSION,
				aoTaskDetailsBeanForInvoiceReview.getP8UserSession());
		loChannelForInvoiceReview.setData(HHSConstants.AO_TASK_DETAILS_BEAN, aoTaskDetailsBeanForInvoiceReview);
		loChannelForInvoiceReview.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
		loChannelForInvoiceReview.setData(HHSConstants.LB_FINAL_FINISH, lbFinalFinish);
		loChannelForInvoiceReview.setData(HHSConstants.AO_BUDGET_STATUS, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_INVOICE_RETURNED_FOR_REVISION));
		loChannelForInvoiceReview
				.setData(HHSConstants.INVOICE_ID_KEY, aoTaskDetailsBeanForInvoiceReview.getInvoiceId());
		// Release 3.12.0 enhancement 6644
		loChannelForInvoiceReview.setData(HHSConstants.AS_USER_ID, aoTaskDetailsBeanForInvoiceReview.getUserId());

		HHSUtil.setAuditOnFinancialFinishTask(aoTaskDetailsBeanForInvoiceReview, loChannelForInvoiceReview);

		// changes for agency outbound interafce 6644
		loChannelForInvoiceReview.setData(HHSConstants.AS_USER_ID, aoTaskDetailsBeanForInvoiceReview.getUserId());
		// Start R5 : set EntityId and EntityName for AutoSave
		CommonUtil.setChannelForAutoSaveData(loChannelForInvoiceReview,
				aoTaskDetailsBeanForInvoiceReview.getWorkFlowId(), HHSR5Constants.TASKS);
		// End R5 : set EntityId and EntityName for AutoSave
		HHSTransactionManager.executeTransaction(loChannelForInvoiceReview, HHSConstants.FINISH_INVOICING_REVIEW_TASK);

		loTaskParamMapForInvoiceReview.put(HHSConstants.PAGE_ERROR, HHSConstants.EMPTY_STRING);
		loTaskParamMapForInvoiceReview.put(HHSConstants.TASK_ERROR, HHSConstants.EMPTY_STRING);
		return loTaskParamMapForInvoiceReview;
	}

}
