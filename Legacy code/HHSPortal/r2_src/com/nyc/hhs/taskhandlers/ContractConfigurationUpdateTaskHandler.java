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
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.HHSUtil;

/**
 * This BudgetModificationTaskHandler will implement all the abstract methods of
 * MainTaskHandler and hence implement Task related functionalities of
 * BudgetModificationTask
 * 
 */
public class ContractConfigurationUpdateTaskHandler extends MainTaskHandler
{

	/**
	 * This method decide the execution flow on click of Finish Task button with
	 * task status Approved for Contract configuration Task
	 * <ul>
	 * <li>Set Audit details in channel by calling 'addAuditDataToChannel'
	 * Utility method</li>
	 * <li>Execute Transaction to Contract configuration Task</li>
	 * </ul>
	 * 
	 * @param aoTaskDetailsBean TaskDetailsBean object
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Map taskApprove(TaskDetailsBean aoTaskDetailsBean) throws ApplicationException
	{
		Map loTaskParamMap = new HashMap();
		Map loHmWFReqProps = new HashMap();
		Map loHmWFReqPropsForAutoApproval = new HashMap<String,Object>();
		boolean lbFinalFinish = false;
		Channel loChannel = new Channel();
		String lsContractId = aoTaskDetailsBean.getContractId();
		String lsUpdateContractId = null;
		
System.out.println("======taskApprove==========      :: Start "   );

		loChannel.setData(HHSConstants.LO_HM_WF_REQ_PROPS, loHmWFReqProps);
		loChannel.setData(HHSConstants.AO_FILENET_SESSION, aoTaskDetailsBean.getP8UserSession());
		loChannel.setData(HHSConstants.AO_TASK_DETAILS_BEAN, aoTaskDetailsBean);
		loChannel.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
		loChannel.setData(HHSConstants.LB_FINAL_FINISH, lbFinalFinish);
		loChannel.setData(HHSConstants.CONTRACT_ID_KEY, lsContractId);
		loChannel.setData(HHSConstants.CONTRACT_TYPE_ID_KEY, HHSConstants.FOUR);
		loChannel.setData(HHSConstants.AS_BUDGET_TYPE_ID, HHSConstants.FOUR);

		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.CS_VALIDATE_CONTRACT_CONFIG_UPDATE_AMOUNT);
		String loMessage = (String) loChannel.getData(HHSConstants.CBL_MESSAGE);

		if (loMessage != null)
		{
			loTaskParamMap.put(HHSConstants.PAGE_ERROR, loMessage);
			return loTaskParamMap;
		}
		// add default entries budget level
		if (null == aoTaskDetailsBean.getLinkedWobNum() || aoTaskDetailsBean.getLinkedWobNum().isEmpty())
		{
			loHmWFReqProps.put(HHSConstants.COMPONENT_ACTION, HHSConstants.INSERT_LINE_ITEMS_CONF_COMPONENT_ACTION);
			loChannel.setData(HHSConstants.WORK_FLOW_NAME, HHSConstants.WF_FINANCIAL_UTILITY);
			loChannel.setData(HHSConstants.LO_HM_WF_REQ_PROPS, loHmWFReqProps);

			// fetch update contract id
			
			Channel loChannelObj = new Channel();
			loChannelObj.setData("asBaseContractId", lsContractId);
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.BMC_FETCH_UPDATE_CONTRACT_ID);
			lsUpdateContractId = (String) loChannelObj.getData("asUpdateContractId");
			// end

			aoTaskDetailsBean.setContractId(lsUpdateContractId);
			loChannel.setData(ApplicationConstants.TASK_DETAILS_BEAN_KEY, aoTaskDetailsBean);
			List<String> loFiscalYrList = (List<String>) loChannel.getData("aoReturnedFYIList");

			for (String loFiscalYr : loFiscalYrList)
			{
				aoTaskDetailsBean.setStartFiscalYear(loFiscalYr);
				loHmWFReqProps.put(HHSConstants.VALUES, CommonUtil.convertBeanToString(aoTaskDetailsBean));
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.INSERT_LINE_ITEMS_CONF_COMPONENT_ACTION);
			}

		}
		aoTaskDetailsBean.setContractId(lsContractId);
		HHSUtil.setAuditOnFinancialFinishTask(aoTaskDetailsBean, loChannel);
		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.CS_FINISH_CONTRACT_CONFIG_UPDATE_ALL_TASK);
		
		//Added for R7 - Auto Update - launching Utility workflow to check auto update condition
		Channel loChannelObjForAutoApproval = new Channel();
		loHmWFReqPropsForAutoApproval.put(HHSConstants.COMPONENT_ACTION, HHSR5Constants.CHECK_AUTO_APPROVAL_FOR_ASSIGNMENT);
		loChannelObjForAutoApproval.setData(HHSConstants.AO_FILENET_SESSION, aoTaskDetailsBean.getP8UserSession());
		loHmWFReqPropsForAutoApproval.put(HHSConstants.PROPERTY_PE_CONTRACT_ID,lsUpdateContractId );
		loHmWFReqPropsForAutoApproval.put(HHSConstants.VALUES, CommonUtil.convertBeanToString(aoTaskDetailsBean));
		loChannelObjForAutoApproval.setData(HHSConstants.WORK_FLOW_NAME, HHSConstants.WF_FINANCIAL_UTILITY);
		loChannelObjForAutoApproval.setData(HHSConstants.LO_HM_WF_REQ_PROPS, loHmWFReqPropsForAutoApproval);
		HHSTransactionManager.executeTransaction(loChannelObjForAutoApproval, HHSConstants.INSERT_LINE_ITEMS_CONF_COMPONENT_ACTION);
		//End R7
		loTaskParamMap.put(HHSConstants.PAGE_ERROR, HHSConstants.EMPTY_STRING);
		loTaskParamMap.put(HHSConstants.TASK_ERROR, HHSConstants.EMPTY_STRING);

System.out.println("======taskApprove==========  taskApprove  :: End "   );

		return loTaskParamMap;
	}

	/**
	 * This method is updated for Release 3.8.0 #6483
	 * This method decide the execution flow on click of Finish Task button with
	 * task status Cancel for Contract configuration Task
	 * <ul>
	 * <li>Set Audit details in channel by calling 'addAuditDataToChannel'
	 * Utility method</li>
	 * <li>Execute Transaction to Return For Revision Contract configuration
	 * Task</li>
	 * </ul>
	 * 
	 * @param aoTaskDetailsBean TaskDetailsBean object
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Map taskReturn(TaskDetailsBean aoTaskDetailsBean) throws ApplicationException
	{
		Map loTaskParamMap = new HashMap();
		HashMap loHmDocReqProps = new HashMap();
		Channel loChannel = new Channel();
		String lsContractId = aoTaskDetailsBean.getContractId();
		String lsWorkflowID = aoTaskDetailsBean.getWorkFlowId();
		
		// fetch update contract id
		String lsUpdateContractId = null;
		Channel loChannelObj = new Channel();
		loChannelObj.setData(HHSConstants.AS_BASE_CONTRACT_ID, lsContractId);
		HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.BMC_FETCH_UPDATE_CONTRACT_ID);
		lsUpdateContractId = (String) loChannelObj.getData(HHSConstants.AS_UPDATE_CONTRACT_ID);
		
		HHSUtil.addAuditDataToChannel(loChannel, HHSConstants.CANCEL_CONTRACT_UPDATE,
				HHSConstants.CANCEL_CONTRACT_UPDATE, "Contract Update Cancelled for " + " " + lsContractId, HHSConstants.CONTRACT, lsContractId, aoTaskDetailsBean.getUserId(),
				HHSConstants.AGENCY_AUDIT, HHSConstants.AUDIT_BEAN);
		
		loHmDocReqProps.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, false);
		loChannel.setData(HHSConstants.LO_HM_DOC_REQ_PROPS, loHmDocReqProps);
		loChannel.setData(HHSConstants.AO_FILENET_SESSION, aoTaskDetailsBean.getP8UserSession());
		loChannel.setData(HHSConstants.CONTRACT_ID_KEY, lsUpdateContractId);
		loChannel.setData(HHSConstants.AS_WORKFLOW_ID, lsWorkflowID);
		loChannel.setData(HHSConstants.AO_FINAL_FINISH, true);
		loChannel.setData(HHSConstants.ERROR_CHECK_RULE, true);
		
		
		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.CANCEL_CONTRACT_CONF_UPDATE);
		loTaskParamMap.put(HHSConstants.PAGE_ERROR, HHSConstants.EMPTY_STRING);
		loTaskParamMap.put(HHSConstants.TASK_ERROR, HHSConstants.EMPTY_STRING);
		return loTaskParamMap;
	}

}
