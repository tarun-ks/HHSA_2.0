package com.nyc.hhs.taskhandlers;

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
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This AmendmentCOFTaskHandler will implement all the abstract methods of
 * MainTaskHandler and hence implement Task related functionalities of
 * AmendmentCOFTask
 * 
 */
public class AmendmentCOFTaskHandler extends MainTaskHandler
{

	/**
	 * This method decide the execution flow on click of Finish Task button with
	 * task status Approved for Contract configuration Task
	 * <ul>
	 * <li>Set Audit details in channel by calling 'addAuditDataToChannel'
	 * Utility method</li>
	 * <li>Execute Transaction id <b> finishAmendmentCofTask</b></li>
	 * <li>Updated in R7 for Defect 8700</li>
	 * <li>Updated in R7 for Cost Center -
	 * Added transaction insertServicesForAmendment for inserting amended services data 
	 * on finish of Amendment COF task</li>
	 * </ul>
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
		Channel loChannel = new Channel();
		boolean lbFinalFinish = false;
		String lsCurrentLevel = aoTaskDetailsBean.getLevel();
		String lsTotalLevel = aoTaskDetailsBean.getTotalLevel();
		if (lsCurrentLevel.equalsIgnoreCase(lsTotalLevel))
		{
			lbFinalFinish = true;
		}

		loChannel.setData(HHSConstants.AO_FILENET_SESSION, aoTaskDetailsBean.getP8UserSession());
		loChannel.setData(ApplicationConstants.TASK_DETAILS_BEAN_KEY, aoTaskDetailsBean);
		loChannel.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
		loChannel.setData(HHSConstants.LB_FINAL_FINISH, lbFinalFinish);
		HHSUtil.setAuditOnFinancialFinishTask(aoTaskDetailsBean, loChannel);
		// Start: Added in R7 for Cost Center
		Channel loChannelObj = new Channel();
		loChannelObj.setData(HHSConstants.AMENDED_CONTRACT_ID, aoTaskDetailsBean.getContractId());
		HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.FETCH_ACTIVE_APPROVED_FISCAL_YEARS);
		List<String> loFiscalYrList = (List) loChannelObj.getData(HHSConstants.FISCAL_YEAR_LIST);
		for (String loFiscalYr : loFiscalYrList)
		{
			aoTaskDetailsBean.setStartFiscalYear(loFiscalYr);
			loChannelObj.setData(HHSConstants.AO_TASK_DETAILS_BEAN, aoTaskDetailsBean);
			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.INSERT_SERVICES_FOR_AMENDMENT,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
		}
		// End: Added in R7 for Cost Center
		// Start changes for enhancement id 6263 release 3.6.0
		HashMap loHashMap = new HashMap();
		loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, aoTaskDetailsBean.getContractId());
		//Start: Added in R7 for Defect 8700
		loHashMap.put(HHSConstants.MODIFY_BY, aoTaskDetailsBean.getUserId());
		//End: Added in R7 for Defect 8700
		loChannel.setData(HHSConstants.AO_HASH_MAP, loHashMap);
		// End
		// Start R5 : set EntityId and EntityName for AutoSave
		CommonUtil.setChannelForAutoSaveData(loChannel, aoTaskDetailsBean.getWorkFlowId(), HHSR5Constants.TASKS);
		// End R5 : set EntityId and EntityName for AutoSave
		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FINISH_AMENDMENT_COF_TASK);
		loTaskParamMap.put(HHSConstants.PAGE_ERROR, HHSConstants.EMPTY_STRING);
		loTaskParamMap.put(HHSConstants.TASK_ERROR, HHSConstants.EMPTY_STRING);
		return loTaskParamMap;
	}

	/**
	 * <p>
	 * This method decide the execution flow on click of Finish Task button with
	 * task status Return For Revision for Amendment Certification of Funds Task
	 * <ul>
	 * <li>Set Audit details in channel by calling 'addAuditDataToChannel'
	 * Utility method</li>
	 * <li>Execute Transaction to Return For Revision Contract configuration
	 * Task <b> returnAmendmentCOFTask</b></li>
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
	public Map taskReturn(TaskDetailsBean aoTaskDetailsBean) throws ApplicationException
	{
		Map loTaskParamMap = new HashMap();
		Channel loChannel = new Channel();
		Boolean lbFinalFinish = false;

		try
		{
			String lsCurrentLevel = aoTaskDetailsBean.getLevel();
			HashMap loErrorCheckRule = new HashMap();
			HHSUtil.setAuditOnFinancialFinishTask(aoTaskDetailsBean, loChannel);
			loChannel.setData(HHSConstants.AO_FILENET_SESSION, aoTaskDetailsBean.getP8UserSession());
			loChannel.setData(ApplicationConstants.TASK_DETAILS_BEAN_KEY, aoTaskDetailsBean);
			loChannel.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
			if (lsCurrentLevel.equalsIgnoreCase(HHSConstants.TWO))
			{
				// start release 3.14.0
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.RETURN_AMENDMENT_COF_VALIDATE);
				loErrorCheckRule = (HashMap) loChannel.getData(HHSConstants.LO_ERROR_CHECK_RULE);
				if (((String) loErrorCheckRule.get(HHSConstants.CLC_ERROR_CHECK))
						.equalsIgnoreCase(HHSConstants.ERROR_FLAG))
				{
					loTaskParamMap.put(HHSConstants.PAGE_ERROR, loErrorCheckRule.get(HHSConstants.CLC_ERROR_MSG));
					return loTaskParamMap;
				}
				// end release 3.14.0

				lbFinalFinish = true;
				// Copy internal comments to show in Amendment Config task
				List<HhsAuditBean> loAuditList = null;
				loAuditList = (List<HhsAuditBean>) loChannel.getData(HHSConstants.LO_AUDIT_LIST);
				if (null != loAuditList && null != aoTaskDetailsBean.getInternalComment()
						&& !(HHSConstants.EMPTY_STRING.equalsIgnoreCase(aoTaskDetailsBean.getInternalComment())))
				{

					loAuditList.add(HHSUtil.addAuditDataToChannel(HHSConstants.AUDIT_TASK_INTERNAL_COMMENTS,
							HHSConstants.AUDIT_TASK_INTERNAL_COMMENTS, aoTaskDetailsBean.getInternalComment(),
							HHSConstants.TASK_AMENDMENT_CONFIGURATION, aoTaskDetailsBean.getEntityId(),
							aoTaskDetailsBean.getUserId(), HHSConstants.AGENCY_AUDIT));
				}
				if (null != loAuditList)
				{
					loAuditList.add(HHSUtil.addAuditDataToChannel(P8Constants.EVENT_NAME_ASSIGN,
							P8Constants.EVENT_NAME_ASSIGN, HHSConstants.PROPERTY_TASK_CREATION_DATA,
							HHSConstants.TASK_AMENDMENT_CONFIGURATION, aoTaskDetailsBean.getEntityId(),
							aoTaskDetailsBean.getUserId(), HHSConstants.AGENCY_AUDIT));
				}
				loChannel.setData(HHSConstants.AS_STATUS_ID, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_CONTRACT_PENDING_CONFIGURATION));
			}
			loChannel.setData(HHSConstants.LO_HM_WF_REQ_PROPS, loTaskParamMap);
			loChannel.setData(HHSConstants.LB_FLAG, lbFinalFinish);
			// Start R5 : set EntityId and EntityName for AutoSave
			CommonUtil.setChannelForAutoSaveData(loChannel, aoTaskDetailsBean.getWorkFlowId(), HHSR5Constants.TASKS);
			// End R5 : set EntityId and EntityName for AutoSave
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.RETURN_AMENDMENT_COF);

		}
		catch (ApplicationException aoExp)
		{
			aoExp.addContextData("ApplicationException occured while executing taskReturn for AmendmentCOFTaskHandler",
					aoExp);
			throw aoExp;
		}
		catch (Exception aoExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while executing taskReturn for AmendmentCOFTaskHandler ", aoExp);
			loAppEx.addContextData(
					"ApplicationException occured while executing taskReturn for AmendmentCOFTaskHandler", aoExp);
			throw loAppEx;
		}

		loTaskParamMap.put(HHSConstants.PAGE_ERROR, HHSConstants.EMPTY_STRING);
		loTaskParamMap.put(HHSConstants.TASK_ERROR, HHSConstants.EMPTY_STRING);
		return loTaskParamMap;
	}

}
