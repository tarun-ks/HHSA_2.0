package com.nyc.hhs.taskhandlers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.ContractBudgetBean;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.HHSUtil;

/**
 * This NewFYConfigurationTaskHandler will implement all the abstract methods of
 * MainTaskHandler and hence implement Task related functionalities of
 * NewFYConfigurationTask
 */
public class NewFYConfigurationTaskHandler extends MainTaskHandler
{

	/**
	 * Log object to record all logs
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(NewFYConfigurationTaskHandler.class);

	/**
	 * This method approves the New FY Configuration flow on click of Finish
	 * Task button with task status Approved for NewFY Configuration Task
	 * <ul>
	 * <li>Execute Transaction <b> getContractEndDate </b></li>
	 * </ul>
	 * <br>
	 * Method updated in R4.
	 * @param aoTaskDetailsBean TaskDetailsBean object
	 * @return loTaskParamMap - returns any error
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Override
	public Map taskApprove(TaskDetailsBean aoTaskDetailsBean) throws ApplicationException
	{
		Map loTaskParamMap = new HashMap();
		try
		{
			boolean lbFinalFinish = false;
			Channel loChannel = new Channel();
			HashMap loErrorCheckRule = new HashMap();
			String lsContractEndDate = null;
			Calendar loBudgetStartDate = Calendar.getInstance();
			Calendar loBudgetEndDate = Calendar.getInstance();
			String lsContractId = aoTaskDetailsBean.getContractId();
			String lsConfigurableFiscalYear = aoTaskDetailsBean.getNewFYId();
			loChannel.setData(HHSConstants.AO_FILENET_SESSION, aoTaskDetailsBean.getP8UserSession());
			loChannel.setData(HHSConstants.AO_TASK_DETAILS_BEAN, aoTaskDetailsBean);
			loChannel.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, Boolean.TRUE);
			loChannel.setData(HHSConstants.LB_FINAL_FINISH, lbFinalFinish);
			// start release 3.14.0
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.NEW_FY_FINISH_VALIDATE);
			loErrorCheckRule = (HashMap) loChannel.getData(HHSConstants.LO_ERROR_CHECK_RULE);
			if (((String) loErrorCheckRule.get(HHSConstants.CLC_ERROR_CHECK)).equalsIgnoreCase(HHSConstants.ERROR_FLAG))
			{
				loTaskParamMap.put(HHSConstants.PAGE_ERROR, loErrorCheckRule.get(HHSConstants.CLC_ERROR_MSG));
				return loTaskParamMap;
			}
			// end release 3.14.0
			// Start: Added in R7 For Cost Center
			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.VALIDATE_SERVICES_OPTED_FOR_NEW_FY,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			String loMessage = (String) loChannel.getData(HHSConstants.CBL_MESSAGE);
			if (loMessage != null)
			{
				loTaskParamMap.put(HHSConstants.PAGE_ERROR, loMessage);
				return loTaskParamMap;
			}
			// End: Added in R7 For Cost Center
			HHSUtil.setAuditOnFinancialFinishTask(aoTaskDetailsBean, loChannel);
			loChannel.setData(HHSConstants.CONTRACT_ID_KEY, lsContractId);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.TRN_GET_CONTRACT_END_DATE);
			lsContractEndDate = (String) loChannel.getData(HHSConstants.ARG_CONTRACT_END_ID);

			ContractBudgetBean loContractBudgetBean = new ContractBudgetBean();
			loContractBudgetBean.setContractId(lsContractId);
			loContractBudgetBean.setBudgetfiscalYear(lsConfigurableFiscalYear);
			loContractBudgetBean.setStatusId(HHSConstants.BUDGET_PENDING_SUBMISSION_STATUS_ID);
			loContractBudgetBean.setModifiedByUserId(aoTaskDetailsBean.getUserId());
			loContractBudgetBean.setOrganizationId(aoTaskDetailsBean.getOrganizationId());
			loBudgetStartDate.set((Integer.parseInt(lsConfigurableFiscalYear) - HHSConstants.INT_ONE),
					HHSConstants.INT_FISCAL_YEAR_START_MONTH, HHSConstants.INT_FISCAL_YEAR_START_DAY_OF_MONTH);
			loBudgetEndDate.set(Integer.parseInt(lsConfigurableFiscalYear), HHSConstants.INT_FISCAL_YEAR_END_MONTH,
					HHSConstants.INT_FISCAL_YEAR_END_DAY_OF_MONTH);

			SimpleDateFormat loDateFormat = new SimpleDateFormat(HHSConstants.NFCTH_DATE_FORMAT);
			loContractBudgetBean.setBudgetEndDate(getContractBudgetEndDate(loDateFormat, lsContractEndDate,
					loBudgetEndDate));
			loContractBudgetBean.setBudgetStartDate(loDateFormat.format(loBudgetStartDate.getTime()));
			loChannel.setData(HHSConstants.AO_CONTRACT_BUDGET_BEAN, loContractBudgetBean);

			// add default entries budget level
			Map loHmWFReqProps = new HashMap();
			loHmWFReqProps.put(HHSConstants.COMPONENT_ACTION, HHSConstants.INSERT_LINE_ITEMS_CONF_COMPONENT_ACTION);

			loChannel.setData(HHSConstants.WORK_FLOW_NAME, HHSConstants.WF_FINANCIAL_UTILITY);
			loChannel.setData(HHSConstants.LO_HM_WF_REQ_PROPS, loHmWFReqProps);
			loChannel.setData(ApplicationConstants.TASK_DETAILS_BEAN_KEY, aoTaskDetailsBean);
			aoTaskDetailsBean.setStartFiscalYear(lsConfigurableFiscalYear);
			loHmWFReqProps.put(HHSConstants.VALUES, CommonUtil.convertBeanToString(aoTaskDetailsBean));
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.INSERT_LINE_ITEMS_CONF_COMPONENT_ACTION);

			// add copy of line item with 0 values ( of previous year sub
			// budgets)
			loHmWFReqProps.put(HHSConstants.COMPONENT_ACTION,
					HHSConstants.CBY_INSERT_OLD_SUB_BUDGET_LINE_ITEMS_ZERO_COPY);
			loChannel.setData(HHSConstants.LO_HM_WF_REQ_PROPS, loHmWFReqProps);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.INSERT_LINE_ITEMS_CONF_COMPONENT_ACTION);
			// R4 Publish EntryType for contractBudget
			HHSUtil.setPublishEntryType(loChannel, aoTaskDetailsBean.getContractId(), HHSConstants.ONE,
					HHSConstants.ONE, aoTaskDetailsBean.getUserId(), lsConfigurableFiscalYear);
			// Start R5 : set EntityId and EntityName for AutoSave
			CommonUtil.setChannelForAutoSaveData(loChannel, aoTaskDetailsBean.getWorkFlowId(), HHSR5Constants.TASKS);
			// End R5 : set EntityId and EntityName for AutoSave
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.TRN_NEW_FY_CONFIG_FINISH_TASK);
			loTaskParamMap.put(HHSConstants.PAGE_ERROR, HHSConstants.EMPTY_STRING);
			loTaskParamMap.put(HHSConstants.TASK_ERROR, HHSConstants.EMPTY_STRING);
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured in NewFYConfigurationTaskHandler :: taskApprove method");
			throw aoAppEx;
		}
		return loTaskParamMap;
	}

	/**
	 * This method returns the earliest date of fiscal year end date and
	 * contract budget end date.
	 * 
	 * @param aoDateFormat : the defined date format to be used for parsing
	 * @param asContractEndDate : the contract end date
	 * @param asBudgetEndDate : the budget end date
	 * @return lsContractEndDate : the final end date to be updated in contract
	 *         budget
	 * @throws ApplicationException : exception object
	 */
	private String getContractBudgetEndDate(SimpleDateFormat aoDateFormat, String asContractEndDate,
			Calendar asBudgetEndDate) throws ApplicationException
	{
		String lsContractEndDate = null;
		try
		{
			Date loFormattedContractEndDate = aoDateFormat.parse(asContractEndDate);
			lsContractEndDate = (asBudgetEndDate.getTime().before(loFormattedContractEndDate)) ? aoDateFormat
					.format(asBudgetEndDate.getTime()) : aoDateFormat.format(loFormattedContractEndDate);
		}
		catch (ParseException aoParseEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occurred in NewFYConfigurationTaskHandler: method getContractBudgetEndDate", aoParseEx);
			LOG_OBJECT.Error(HHSConstants.NFCTH_DATE_PARSE_ERROR_MSG + aoDateFormat.toString(), loAppEx);
			throw loAppEx;
		}
		return lsContractEndDate;
	}

	/**
	 * This method is added for Release 3.12.0 #6602 This method deletes the
	 * "Configure New FY" task on click of Cancel Task button
	 * @param aoTaskDetailsBean TaskDetailsBean object
	 * @return loTaskParamMap - returns any error
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	@Override
	public Map taskReturn(TaskDetailsBean aoTaskDetailsBean) throws ApplicationException
	{
		Map loTaskParamMap = new HashMap();
		Channel loChannel = new Channel();
		HashMap loHmDocReqProps = new HashMap();
		String lsContractId = aoTaskDetailsBean.getContractId();
		String lsWorkflowID = aoTaskDetailsBean.getWorkFlowId();
		String lsUserId = aoTaskDetailsBean.getUserId();
		String lsConfigurableFiscalYear = aoTaskDetailsBean.getNewFYId();
		HHSUtil.addAuditDataToChannel(loChannel, HHSConstants.CANCEL_CONFIGURE_NEW_FY_FOR_AUDIT,
				HHSConstants.CANCEL_CONFIGURE_NEW_FY_FOR_AUDIT, HHSConstants.CANCEL_CONFIGURE_NEW_FY_TEXT + " "
						+ lsConfigurableFiscalYear, HHSConstants.TASK_NEW_FY_CONFIGURATION, lsContractId,
				aoTaskDetailsBean.getUserId(), HHSConstants.AGENCY_AUDIT, HHSConstants.AUDIT_BEAN);
		ContractBudgetBean loContractBudgetBean = new ContractBudgetBean();
		loContractBudgetBean.setContractId(lsContractId);
		loContractBudgetBean.setBudgetfiscalYear(lsConfigurableFiscalYear);
		loContractBudgetBean.setModifiedByUserId(lsUserId);
		loHmDocReqProps.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, false);
		loChannel.setData(HHSConstants.LO_HM_DOC_REQ_PROPS, loHmDocReqProps);
		loChannel.setData(HHSConstants.AO_FILENET_SESSION, aoTaskDetailsBean.getP8UserSession());
		loChannel.setData(HHSConstants.AS_WORKFLOW_ID, lsWorkflowID);
		loChannel.setData(HHSConstants.AO_FINAL_FINISH, true);
		loChannel.setData(HHSConstants.ERROR_CHECK_RULE, true);
		loChannel.setData(HHSConstants.AO_CONTRACT_BUDGET_BEAN, loContractBudgetBean);
		// Start R5 : set EntityId and EntityName for AutoSave
		CommonUtil.setChannelForAutoSaveData(loChannel, aoTaskDetailsBean.getWorkFlowId(), HHSR5Constants.TASKS);
		// End R5 : set EntityId and EntityName for AutoSave
		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.CANCEL_CONFIGURE_NEW_FY);
		loTaskParamMap.put(HHSConstants.PAGE_ERROR, HHSConstants.EMPTY_STRING);
		loTaskParamMap.put(HHSConstants.TASK_ERROR, HHSConstants.EMPTY_STRING);
		return loTaskParamMap;
	}

}
