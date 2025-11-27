package com.nyc.hhs.taskhandlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.constants.TransactionConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.NotificationDataBean;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This PaymentReviewTaskHandler will implement all the abstract methods of
 * MainTaskHandler and hence implement Task related functionalities of
 * PaymentReviewTask
 * 
 */
public class PaymentReviewTaskHandler extends MainTaskHandler
{

	private static final LogInfo LOG_OBJECT = new LogInfo(AdvancePaymentReviewTaskHandler.class);

	/**
	 * This method decide the execution flow on click of Finish Task button with
	 * task status Approved for Payment Review Task
	 * <ul>
	 * <li>Set Audit details in channel by calling 'addAuditDataToChannel'
	 * Utility method</li>
	 * <li>Execute Transaction to Invoice Review Task</li>
	 * </ul>
	 * 
	 * @param aoTaskDetailsBean TaskDetailsBean object
	 * @return Map loTaskParamMap with task details
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Map taskApprove(TaskDetailsBean aoTaskDetailsBean) throws ApplicationException
	{
		Map loTaskParamMap = new HashMap();
		HashMap loHmWFReqProps = new HashMap();
		String lsErrorMsg = HHSConstants.EMPTY_STRING;
		Map<String, Object> loErrorMap = null;
		boolean lbValidationError = true;
		loTaskParamMap.put(HHSConstants.TASK_ERROR, HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED);
		loTaskParamMap.put(HHSConstants.PAGE_ERROR, lsErrorMsg);
		String lsCurrentLevel = aoTaskDetailsBean.getLevel();
		String lsTotalLevel = aoTaskDetailsBean.getTotalLevel();
		boolean lbFinalFinish = false;
		Channel loChannel = new Channel();
		if (lsCurrentLevel.equalsIgnoreCase(lsTotalLevel))
		{
			lbFinalFinish = true;
		}
		loChannel.setData(HHSConstants.LO_HM_WF_REQ_PROPS, loHmWFReqProps);
		loChannel.setData(HHSConstants.AO_FILENET_SESSION, aoTaskDetailsBean.getP8UserSession());
		loChannel.setData(HHSConstants.AO_TASK_DETAILS_BEAN, aoTaskDetailsBean);
		loChannel.setData(HHSConstants.REVIEWER_LEVEL_KEY, lsCurrentLevel);
		loChannel.setData(HHSConstants.REVIEWER_LEVEL_KEY, lsCurrentLevel);
		// First validation
		// Validation removed from Level 1 .. now this validation is done
		// irrespective of user level .. release 3.8.0
		/*
		 * if (HHSConstants.ONE.equalsIgnoreCase(lsCurrentLevel)) {
		 */
		// Review level Validation
		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.VALIDATE_LEVEL_ONE_PAYMENT_FINISH_TASK);
		loErrorMap = (HashMap<String, Object>) loChannel.getData(HHSConstants.AO_ERROR_MAP);
		int liErrorCode = (Integer) loErrorMap.get(HHSConstants.ERROR_CODE);
		if (liErrorCode != HHSConstants.INT_ZERO)
		{
			lbValidationError = false;
			lsErrorMsg = (String) loErrorMap.get(HHSConstants.CLC_ERROR_MSG);
		}
		// }
		if (lbValidationError)
		{
			loChannel.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
			loChannel.setData(HHSConstants.LB_FINAL_FINISH, lbFinalFinish);
			loChannel.setData(HHSConstants.AO_BUDGET_STATUS, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PAYMENT_APPROVED));
			HHSUtil.setAuditOnFinancialFinishTask(aoTaskDetailsBean, loChannel);
			// Start R5 : set EntityId and EntityName for AutoSave
			CommonUtil.setChannelForAutoSaveData(loChannel, aoTaskDetailsBean.getWorkFlowId(), HHSR5Constants.TASKS);
			// End R5 : set EntityId and EntityName for AutoSave
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FINISH_PAYMENT_REVIEW_TASK);
		}
		loTaskParamMap.put(HHSConstants.PAGE_ERROR, lsErrorMsg);
		loTaskParamMap.put(HHSConstants.TASK_ERROR, HHSConstants.EMPTY_STRING);
		return loTaskParamMap;
	}

	/**
	 * This method modified as a part of release 3.1.0 for enhancement 6023.
	 * <ul>
	 * <li>Code added to reject payment review task</li>
	 * </ul>
	 * 
	 * This method decide the execution flow on click of Finish Task button with
	 * task status Return For Revision for Payment Review Task
	 * <ul>
	 * <li>This method is updated in R4</li>
	 * <li>Set Audit details in channel by calling 'addAuditDataToChannel'
	 * Utility method</li>
	 * <li>Execute Transaction to Return For Revision Invoice review Task by
	 * excecuting 'finishPaymentReturnReviewTask'</li>
	 * </ul>
	 * 
	 * @param aoTaskDetailsBean TaskDetailsBean object
	 * @return Map loTaskParamMap with task details
	 * @throws ApplicationException If an Application Exception occurs
	 * 
	 * 
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Map taskReturn(TaskDetailsBean aoTaskDetailsBean) throws ApplicationException
	{
		Map loTaskParamMap = new HashMap();
		loTaskParamMap.put(HHSConstants.TASK_ERROR, HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED);
		loTaskParamMap.put(HHSConstants.PAGE_ERROR, HHSConstants.EMPTY_STRING);
		Channel loChannel = new Channel();
		boolean lbFinalFinish = false;
		boolean lbNotificationFlag = false;
		String lsCurrentLevel = aoTaskDetailsBean.getLevel();
		HHSUtil.setAuditOnFinancialFinishTask(aoTaskDetailsBean, loChannel);
		if (null != lsCurrentLevel && lsCurrentLevel.equalsIgnoreCase(HHSConstants.ONE))
		{

			lbFinalFinish = true;
			// Copy internal comments to show in Invoice Review task
			List<HhsAuditBean> loAuditList = null;
			loAuditList = (List<HhsAuditBean>) loChannel.getData(HHSConstants.LO_AUDIT_LIST);
			if (null != loAuditList && null != aoTaskDetailsBean.getInternalComment()
					&& !(HHSConstants.EMPTY_STRING.equalsIgnoreCase(aoTaskDetailsBean.getInternalComment())))
			{

				loAuditList.add(HHSUtil.addAuditDataToChannel(HHSConstants.AUDIT_TASK_INTERNAL_COMMENTS,
						HHSConstants.AUDIT_TASK_INTERNAL_COMMENTS, aoTaskDetailsBean.getInternalComment(),
						HHSConstants.TASK_INVOICE_REVIEW, aoTaskDetailsBean.getEntityId(),
						aoTaskDetailsBean.getUserId(), HHSConstants.AGENCY_AUDIT));
			}

		}
		if (null != lsCurrentLevel && lsCurrentLevel.equalsIgnoreCase(HHSConstants.TWO))
		{
			// Added in R7-To Fix Defect #7211(delete payment Accounting lines
			// while doing 'Return for Revision' apart from level 1)
			if (aoTaskDetailsBean.getInvoiceId() != null || aoTaskDetailsBean.getBudgetAdvanceId() != null)
			{
				String lsInvoiceId = aoTaskDetailsBean.getInvoiceId();
				String lsBudgetAdvanceId = aoTaskDetailsBean.getBudgetAdvanceId();
				HashMap<String, String> loPaymentDetailMap = new HashMap<String, String>();
				List<String> loPendingApprovedPaymentIdList = null;
				loPaymentDetailMap.put(HHSConstants.INVOICE_ID, lsInvoiceId);
				loPaymentDetailMap.put(HHSConstants.BUDGET_ADVANCE_ID, lsBudgetAdvanceId);
				loChannel.setData(HHSConstants.PAYMENT_DETAIL_MAP, loPaymentDetailMap);
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_PAYMENTS_NOT_LEVEL1);
				loPendingApprovedPaymentIdList = (List<String>) loChannel
						.getData(HHSConstants.PENDING_APPROVED_PAYMENT_LIST);

				loChannel.setData(HHSConstants.PENDING_APPROVED_PAYMENT_IDS_LIST, loPendingApprovedPaymentIdList);
				HHSTransactionManager.executeTransaction(loChannel,
						HHSConstants.UPDATE_ACCOUNTING_LINES_AS_PER_FMS_FEED);
			}
		}

		loChannel.setData(HHSConstants.AO_FILENET_SESSION, aoTaskDetailsBean.getP8UserSession());
		loChannel.setData(HHSConstants.AO_TASK_DETAILS_BEAN, aoTaskDetailsBean);
		loChannel.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
		loChannel.setData(HHSConstants.LB_FINAL_FINISH, lbFinalFinish);

		// code added to reject payment review task .. release 3.1.0 for
		// enhancement 6023.
		if (null != aoTaskDetailsBean.getTaskSource() && aoTaskDetailsBean.getTaskSource().equals(HHSConstants.BATCH))
		{
			loChannel.setData(HHSConstants.AO_BUDGET_STATUS,
					PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.INVOICE_REJECTED));
			if (lbFinalFinish)
			{
				lbNotificationFlag = true;
			}
		}
		else
		{
			loChannel.setData(HHSConstants.AO_BUDGET_STATUS, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_INVOICE_PENDING_APPROVAL));
		}
		HashMap lopaymnentHashMap = getNotificationMapForPaymentRejected(aoTaskDetailsBean);
		loChannel.setData(HHSConstants.LO_HM_NOTIFY_PARAM, lopaymnentHashMap);
		loChannel.setData("lbSendNotificationFlag", lbNotificationFlag);
		// Start R5 : set EntityId and EntityName for AutoSave
		CommonUtil.setChannelForAutoSaveData(loChannel, aoTaskDetailsBean.getWorkFlowId(), HHSR5Constants.TASKS);
		// End R5 : set EntityId and EntityName for AutoSave
		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FINISH_PAYMENT_RETURN_REVIEW_TASK);

		loTaskParamMap.put(HHSConstants.PAGE_ERROR, HHSConstants.EMPTY_STRING);
		loTaskParamMap.put(HHSConstants.TASK_ERROR, HHSConstants.EMPTY_STRING);
		return loTaskParamMap;
	}

	/**
	 * This method added as a part of release 3.1.0 for enhancement 6023. This
	 * method returns notification map for NT400 notification when payment
	 * review task is rejected by agency
	 * <ul>
	 * <li>Notification map prepared for NT400 notification</li>
	 * </ul>
	 * 
	 * @param aoTaskDetailsBean TaskDetailsBean object
	 * @return HashMap loNotificationMapForPaymentRejected with notification
	 *         details
	 * @throws ApplicationException If an Application Exception occurs
	 * 
	 * 
	 */
	private HashMap<String, Object> getNotificationMapForPaymentRejected(TaskDetailsBean aoTaskDetailsBean)
			throws ApplicationException
	{
		HashMap<String, Object> loNotificationMapForPaymentRejected = new HashMap<String, Object>();
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		try
		{

			String lsUserId = aoTaskDetailsBean.getUserId();
			NotificationDataBean loNotificationDataBean = new NotificationDataBean();
			String lsCityUrl = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
					HHSConstants.PROP_CITY_URL);
			List<String> loNotificationAlertList = new ArrayList<String>();
			loNotificationAlertList.add(HHSConstants.NT400);

			HashMap<String, String> loLinkMap = new HashMap<String, String>();
			StringBuilder lsBfApplicationUrl = new StringBuilder();
			lsBfApplicationUrl.append(lsCityUrl).append(HHSConstants.PAYMENT_REJECTION_AGENCY_URL);
			loLinkMap.put(HHSConstants.LINK, lsBfApplicationUrl.toString());
			loNotificationDataBean.setLinkMap(loLinkMap);
			loNotificationDataBean.setAgencyLinkMap(loLinkMap);
			List<String> loAgencyIdList = new ArrayList<String>();
			loAgencyIdList.add(aoTaskDetailsBean.getAgencyId());
			loNotificationDataBean.setAgencyList(loAgencyIdList);
			loNotificationMapForPaymentRejected.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
			loNotificationMapForPaymentRejected.put(HHSConstants.NT400, loNotificationDataBean);
			loRequestMap.put(HHSConstants.VOUCHER_ID, HHSConstants.EMPTY_STRING);
			loRequestMap.put(HHSConstants.AGENCY_COMMENTS_DATA, aoTaskDetailsBean.getInternalComment());
			loRequestMap.put(HHSConstants.AGENCY_USER_ID, aoTaskDetailsBean.getUserId());
			loNotificationMapForPaymentRejected.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
			loNotificationMapForPaymentRejected.put(ApplicationConstants.ENTITY_ID, aoTaskDetailsBean.getInvoiceId());
			loNotificationMapForPaymentRejected.put(ApplicationConstants.ENTITY_TYPE, HHSConstants.INVOICE_PAYMENT);
			loNotificationMapForPaymentRejected.put(HHSConstants.CREATED_BY_USER_ID, lsUserId);
			loNotificationMapForPaymentRejected.put(HHSConstants.MODIFIED_BY, lsUserId);
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occured while setting notification map for rejected payments", aoEx);
			throw new ApplicationException(
					"Error was occurred while get notfication for getNotificationMapForProposalTask", aoEx);
		}
		return loNotificationMapForPaymentRejected;
	}

}
