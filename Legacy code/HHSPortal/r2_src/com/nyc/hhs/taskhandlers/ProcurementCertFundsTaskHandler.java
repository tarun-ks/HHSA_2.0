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

/**
 * This BudgetModificationTaskHandler will implement all the abstract methods of
 * MainTaskHandler and hence implement Task related functionalities of
 * BudgetModificationTask
 * 
 */
public class ProcurementCertFundsTaskHandler extends MainTaskHandler
{

	/**
	 * This method decide the execution flow on click of Finish Task button with
	 * task status Approved for Procurement Certification of Fund Task
	 * <ul>
	 * <li>Set Audit details in channel by calling 'addAuditDataToChannel'
	 * Utility method</li>
	 * <li>Execute Transaction to Approve Procurement Certification of Fund Task
	 * </li>
	 * </ul>
	 * @param aoTaskDetailsBean TaskDetailsBean object
	 * @return loTaskParamMap - returns any error
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Map taskApprove(TaskDetailsBean aoTaskDetailsBean) throws ApplicationException
	{
		Map loTaskParamMap = new HashMap();
		String lsCurrentLevel = aoTaskDetailsBean.getLevel();
		String lsTotalLevel = aoTaskDetailsBean.getTotalLevel();
		Channel loChannel = new Channel();
		int liCurrentLevel = 0;
		Map loHmWFReqProps = new HashMap();
		Boolean loFinalFinish = false;

		if (lsCurrentLevel.equalsIgnoreCase(lsTotalLevel))
		{
			loFinalFinish = true;
		}

		if (null != lsCurrentLevel)
		{
			liCurrentLevel = Integer.valueOf(lsCurrentLevel);
		}

		loHmWFReqProps.put(HHSConstants.CURR_LEVEL, liCurrentLevel + HHSConstants.INT_ONE);
		loChannel.setData(HHSConstants.AO_FILENET_SESSION, aoTaskDetailsBean.getP8UserSession());
		loChannel.setData(HHSConstants.AO_TASK_DETAILS_BEAN, aoTaskDetailsBean);
		loChannel.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, Boolean.TRUE);
		loChannel.setData(HHSConstants.LO_HM_WF_REQ_PROPS, loHmWFReqProps);
		loChannel.setData(HHSConstants.LB_FINAL_FINISH, loFinalFinish);
		HHSUtil.setAuditOnFinancialFinishTask(aoTaskDetailsBean, loChannel);
		// Start R5 : set EntityId and EntityName for AutoSave
		CommonUtil.setChannelForAutoSaveData(loChannel, aoTaskDetailsBean.getWorkFlowId(), HHSR5Constants.TASKS);
		// End R5 : set EntityId and EntityName for AutoSave
		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.PROCUREMENT_TASK_OF_FUNDS);
		loTaskParamMap.put(HHSConstants.PAGE_ERROR, HHSConstants.EMPTY_STRING);
		loTaskParamMap.put(HHSConstants.TASK_ERROR, HHSConstants.EMPTY_STRING);
		return loTaskParamMap;
	}

	/**
	 * This method decide the execution flow on click of Finish Task button with
	 * task status Return For Revision for Procurement Certification of Fund
	 * Task
	 * <ul>
	 * <li>Set Audit details in channel by calling 'addAuditDataToChannel'
	 * Utility method</li>
	 * <li>Execute Transaction to Return For Revision Procurement Certification
	 * of Fund Task</li>
	 * </ul>
	 * @param aoTaskDetailsBean TaskDetailsBean object
	 * @return loTaskParamMap - returns any error
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public Map taskReturn(TaskDetailsBean aoTaskDetailsBean) throws ApplicationException
	{
		Map loTaskParamMap = new HashMap();
		String lsCurrentLevel = aoTaskDetailsBean.getLevel();
		String lsTotalLevel = aoTaskDetailsBean.getTotalLevel();
		int liCurrentLevel = 0;
		Boolean loFinalFinish = false;
		Map loHmWFReqProps = new HashMap();
		Channel loChannel = new Channel();

		if (lsCurrentLevel.equalsIgnoreCase(lsTotalLevel))
		{
			loFinalFinish = true;
		}

		if (null != lsCurrentLevel)
		{
			liCurrentLevel = Integer.valueOf(lsCurrentLevel);
		}

		loHmWFReqProps.put(HHSConstants.CURR_LEVEL, liCurrentLevel + HHSConstants.INT_ONE);
		loChannel.setData(HHSConstants.AO_FILENET_SESSION, aoTaskDetailsBean.getP8UserSession());
		loChannel.setData(HHSConstants.AO_TASK_DETAILS_BEAN, aoTaskDetailsBean);
		loChannel.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, Boolean.TRUE);
		loChannel.setData(HHSConstants.LO_HM_WF_REQ_PROPS, loHmWFReqProps);
		loChannel.setData(HHSConstants.LB_FINAL_FINISH, loFinalFinish);
		HHSUtil.setAuditOnFinancialFinishTask(aoTaskDetailsBean, loChannel);

		HHSUtil.setAuditOnFinancialFinishTask(aoTaskDetailsBean, loChannel);
		// Start R5 : set EntityId and EntityName for AutoSave
		CommonUtil.setChannelForAutoSaveData(loChannel, aoTaskDetailsBean.getWorkFlowId(), HHSR5Constants.TASKS);
		// End R5 : set EntityId and EntityName for AutoSave
		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.PROCUREMENT_TASK_OF_FUNDS);
		loTaskParamMap.put(HHSConstants.PAGE_ERROR, HHSConstants.EMPTY_STRING);
		loTaskParamMap.put(HHSConstants.TASK_ERROR, HHSConstants.EMPTY_STRING);
		return loTaskParamMap;
	}

}
