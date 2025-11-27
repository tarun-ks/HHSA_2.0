package com.nyc.hhs.daomanager.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.TransactionConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.AssignmentsSummaryBean;
import com.nyc.hhs.model.BudgetDetails;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.ContractList;
import com.nyc.hhs.model.PaymentBean;
import com.nyc.hhs.model.PaymentChartOfAllocation;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This service class will get the method calls from controller through
 * transaction layer. Execute queries by calling mapper and return query output
 * back to controller. If any error exists, wrap the exception into Application
 * Exception and throw it to controller.
 */
public class PaymentModuleService extends ServiceState
{

	/**
	 * LogInfo object for Logging
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(PaymentModuleService.class);

	/**
	 * <ul>
	 * <li>This service class is invoked through fetchContractInfoForPayment
	 * transaction id for Advance Payment Request screen</li>
	 * <li>This method fetchContractInfoForPayment will get the contract
	 * Information on the basis of contractId, budgetId</li>
	 * <li>This query used: fetchContractInfoForPayment</li>
	 * </ul>
	 * @param aoMybatisSession Session Object
	 * @param aoHashMap HashMap containing ContractId and BudgetId
	 * @return loContractList
	 * @throws ApplicationException - ApplicationException object
	 * 
	 */
	public ContractList fetchContractInfoForPayment(SqlSession aoMybatisSession, HashMap<String, String> aoHashMap)
			throws ApplicationException
	{
		ContractList loContractList = null;
		try
		{
			loContractList = (ContractList) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER, HHSConstants.PM_FETCH_CONTRACT_INFO_PAYMENT,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			loContractList.setContractAgencyName(HHSUtil.getAgencyName(loContractList.getContractAgencyName()));
		}
		// Application Exception handled here
		catch (ApplicationException aoAppEx)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			// And setting the transaction state
			aoAppEx.addContextData(HHSConstants.CLC_CONTRACT_ID, aoHashMap);
			LOG_OBJECT.Error("Exception occured while retrieveing Contract Information in AdvancePaymentService ",
					aoAppEx);
			setMoState("Transaction Failed:: InvoiceService: fetchContractInfoForPayment method - failed to fetch"
					+ aoHashMap + " \n");
			throw aoAppEx;
		}
		catch (Exception aoExp)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			// And setting the transaction state
			LOG_OBJECT.Error("Exception occured while retrieveing Contract Information in AdvancePaymentService ",
					aoExp);
			setMoState("Transaction Failed:: InvoiceService: fetchContractInfoForPayment method - failed to fetch"
					+ aoHashMap + " \n");
			throw new ApplicationException("Error occured while retrieving Contract Summary for Advance Payment", aoExp);
		}

		return loContractList;
	}

	/**
	 * <li>This service class is invoked through fetchFyBudgetSummaryForPayment
	 * transaction id for Contract budget screen</li> <li>
	 * This method fetchFyBudgetSummaryForPayment will get the Fiscal Year
	 * contract Information on the basis of contractId</li> <li>This query used:
	 * fetchFyBudgetSummary</li> <li>This query used: getInvoiceAmount</li> <li>
	 * This query used: fetchInvFyBudgetActualPaid</li>
	 * 
	 * 
	 * @param aoMybatisSession Mybatis Session Object.
	 * @param aoHashMap HashMap set.
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 * @return loFyBudget - BudgetDetails obj
	 */
	public BudgetDetails fetchFyBudgetSummaryForPayment(SqlSession aoMybatisSession, HashMap<String, String> aoHashMap)
			throws ApplicationException
	{
		BudgetDetails loFyBudget = null;

		try
		{
			loFyBudget = (BudgetDetails) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER, HHSConstants.FETCH_FY_BUDGET_SUMMARY,
					HHSConstants.JAVA_UTIL_HASH_MAP);

			BigDecimal loInvoiceAmount = null;

			loInvoiceAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER, HHSConstants.CBY_GET_INVOICE_AMOUNT,
					HHSConstants.JAVA_UTIL_HASH_MAP);

			loFyBudget.setInvoicedAmount((loInvoiceAmount));

			BigDecimal loActPaidAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession,
					aoHashMap.get(HHSConstants.BUDGET_ID_WORKFLOW), HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER,
					HHSConstants.INV_FETCH_FY_BUDGET_ACTUAL_PAID, HHSConstants.JAVA_LANG_STRING);

			loFyBudget.setYtdActualPaid(loActPaidAmount);
			loFyBudget.setRemainingAmount(loFyBudget.getApprovedBudget().subtract(loInvoiceAmount));
			loFyBudget.setCashBalance(loFyBudget.getApprovedBudget().subtract(loActPaidAmount));

		}
		// Application Exception handled here
		catch (ApplicationException loAppEx)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			// And setting the transaction state
			loAppEx.addContextData(HHSConstants.CONTRACT_ID_WORKFLOW, aoHashMap);
			LOG_OBJECT.Error(
					"Exception occured while retrieveing Fiscal Year Contract Information in PaymentModuleService ",
					loAppEx);
			setMoState("Transaction Failed:: PaymentModuleService: fetchFyBudgetSummaryForPayment method - failed to fetch"
					+ aoHashMap + " \n");
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			// And setting the transaction state
			LOG_OBJECT.Error(
					"Exception occured while retrieveing Fiscal Year Contract Information in PaymentModuleService ",
					loAppEx);
			setMoState("Transaction Failed:: PaymentModuleService: fetchFyBudgetSummaryForPayment method - failed to fetch"
					+ aoHashMap + " \n");
			throw new ApplicationException(
					"Error occured while retrieving Fiscal Year Contract Summary in PaymentModuleService", loAppEx);
		}
		return loFyBudget;
	}

	/**
	 * This method is triggered to get the information in session.
	 * <ul>
	 * <li>A hashmap is passed to get the data.</li>
	 * <li>This query used: getCbGridDataForSession</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Mybatis Session Object.
	 * @param aoHashMap HashMap set.
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 * @return loCBGridBean
	 */
	@SuppressWarnings("rawtypes")
	public CBGridBean getCbGridDataForSession(SqlSession aoMybatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		CBGridBean loCBGridBean = null;
		try
		{
			loCBGridBean = (CBGridBean) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER, HHSConstants.GET_CB_GRID_DATA_FOR_SESSION,
					HHSConstants.JAVA_UTIL_HASH_MAP);
		}
		// Application Exception handled here
		catch (ApplicationException loAppEx)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			// And setting the transaction state
			loAppEx.addContextData(HHSConstants.BUDGET_ID, aoHashMap);
			LOG_OBJECT.Error(
					"Exception occured while retrieveing CbGridDataForSession Information in PaymentModuleService ",
					loAppEx);
			setMoState("Transaction Failed:: InvoiceService: getCbGridDataForSession method - failed to fetch"
					+ aoHashMap + " \n");
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			// And setting the transaction state
			LOG_OBJECT.Error("Exception occured while retrieveing CbGridDataForSession in PaymentModuleService ",
					loAppEx);
			setMoState("Transaction Failed:: InvoiceService: getCbGridDataForSession method - failed to fetch"
					+ aoHashMap + " \n");
			throw new ApplicationException("Error occured while insertRate", loAppEx);
		}

		return loCBGridBean;
	}

	/**
	 * <p>
	 * This method is triggered to get the Assignment Grid information.
	 * <ul>
	 * <li>A bean is passed, CBGridBean</li>
	 * <li>This query used: contractInvAssignmentSummary</li>
	 * <li>This query used: fetchAdvanceAssignmentAmount</li>
	 * This method was updated in R4
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatisSession :Session Object
	 * @param aoCBGridBeanObj : CBGridBean Object
	 * @throws ApplicationException : Application Exception
	 * @return loAssignmentsList List of AssignmentsSummaryBean
	 * @throws ApplicationException : ApplicationException Object
	 */

	@SuppressWarnings("unchecked")
	public List<AssignmentsSummaryBean> fetchAssignmentSummary(SqlSession aoMybatisSession, CBGridBean aoCBGridBeanObj)
			throws ApplicationException
	{
		List<AssignmentsSummaryBean> loAssignmentsList = null;
		List<AssignmentsSummaryBean> loAdvanceAssignmentDetails = null;

		try
		{
			// Fetch first two columns of Assignment grid - Assignee name and
			// YTD_Assignment Amount
			loAssignmentsList = (List<AssignmentsSummaryBean>) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.CI_FETCH_ASSIGNMENT_SUMMARY,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			// Fetch Advance_AssignmentAmount
			loAdvanceAssignmentDetails = (List<AssignmentsSummaryBean>) DAOUtil.masterDAO(aoMybatisSession,
					aoCBGridBeanObj, HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER,
					HHSConstants.PM_FETCH_ADVANCE_ASSIGNMET_AMOUNT, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			// Merge Advance_Assignment Amount to loAssignmentsList
			mergeAssignmentDetails(loAssignmentsList, loAdvanceAssignmentDetails);
		}
		// Application Exception thrown by DAO layer are caught and logged here
		// and then thrown
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("aoCBGridBeanObj", CommonUtil.convertBeanToString(aoCBGridBeanObj));
			LOG_OBJECT.Error("Exception occured  fetch at PaymentModuleService: fetchAssignmentSummary() ", loAppEx);
			setMoState("PaymentModuleService: fetchAssignmentSummary() failed to fetch Assignment for BudgetAdvacne:"
					+ aoCBGridBeanObj.getBudgetAdvanceId() + " \n");
			throw loAppEx;
		}
		// Exception thrown by DAO layer are caught and logged here and then
		// thrown
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while edit in PaymentModuleService ", loAppEx);
			setMoState("Transaction Failed:: PaymentModuleService: fetchAssignmentSummary method - failed to fetch Assignment"
					+ aoCBGridBeanObj.getBudgetAdvanceId() + " \n");
			throw new ApplicationException("Exception occured while fetching Assignment from PaymentModuleService ",
					loAppEx);
		}

		return loAssignmentsList;
	}

	/**
	 * <p>
	 * This method is used to concatenate Assignment Amount in aoInputBeanList
	 * and other Assignment Details in aoResultBeanList
	 * </p>
	 * 
	 * @param aoResultBeanList List of AssignmentsSummaryBean having
	 *            OperationSupport details
	 * @param aoInputBeanList List of AssignmentsSummaryBean having Assignment
	 *            Amount
	 * 
	 */
	private void mergeAssignmentDetails(List<AssignmentsSummaryBean> aoResultBeanList,
			List<AssignmentsSummaryBean> aoInputBeanList)
	{

		// Get Invoiced Amount and put in a Map with respective
		// operationSupportID as Key
		Map<String, AssignmentsSummaryBean> loDetailMap = new HashMap<String, AssignmentsSummaryBean>();

		for (AssignmentsSummaryBean loInputIterate : aoInputBeanList)
		{
			loDetailMap.put(loInputIterate.getId(), loInputIterate);
		}
		//
		for (AssignmentsSummaryBean loResultIterate : aoResultBeanList)
		{
			// If loDetailMap has entry for
			// loResultIterate.id +"_"+invoiceDetailId
			if (loDetailMap.containsKey(loResultIterate.getId()))
			{

				loResultIterate.setAssignmentAmount(loDetailMap.get(loResultIterate.getId()).getAssignmentAmount());
			}
			else
			{
				loResultIterate.setAssignmentAmount(String.valueOf(HHSConstants.INT_ZERO));
			}
		}

	}

	/**
	 * This is the updateRate() method for updating Rate details for the
	 * particular contract Budget.
	 * 
	 * <ul>
	 * <li>Provider is able to Edit the Rate line items that have previously
	 * been added.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Session
	 * @param aoAssignmentsSummaryBean AssignmentSummaryBean
	 * @return Boolean Status of Update operation
	 * @throws ApplicationException : Application Exception
	 */
	public Boolean editAssignmentSummary(SqlSession aoMybatisSession, AssignmentsSummaryBean aoAssignmentsSummaryBean)
			throws ApplicationException
	{
		HashMap<String, Object> loQueryParam = new HashMap<String, Object>();
		Boolean loFlag = Boolean.FALSE;
		try
		{
			loQueryParam.put(HHSConstants.ID, aoAssignmentsSummaryBean.getId());
			loQueryParam.put(HHSConstants.BUDGET_ADVANCE_ID, aoAssignmentsSummaryBean.getBudgetAdvanceId());
			loQueryParam.put(HHSConstants.CONTRACT_BUDGET_ID, aoAssignmentsSummaryBean.getContractBudgetID());
			loQueryParam.put(HHSConstants.IS_LINE_ITEM_INVOICE_AMT, aoAssignmentsSummaryBean.getAssignmentAmount());
			loQueryParam.put(HHSConstants.MODIFIED_BY_AGENCY, aoAssignmentsSummaryBean.getModifyByAgency());
			loQueryParam.put(HHSConstants.MODIFIED_BY_PROVIDER, aoAssignmentsSummaryBean.getModifyByProvider());

			insertUpdateAssignmentDetail(aoMybatisSession, loQueryParam);
			loFlag = Boolean.TRUE;

		}
		// Application Exception thrown by DAO layer are caught and logged here
		// and then thrown
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("ID : ", aoAssignmentsSummaryBean.getId());
			LOG_OBJECT.Error("Exception occured  edit at PaymentModuleService: editAssignmentSummary() ", loAppEx);
			setMoState("PaymentModuleService: editAssignmentSummary failed to edit at id:"
					+ aoAssignmentsSummaryBean.getId() + " \n");
			throw loAppEx;
		}
		// Exception thrown by DAO layer are caught and logged here and then
		// thrown
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while edit in PaymentModuleService ", loAppEx);
			setMoState("Transaction Failed:: PaymentModuleService: editAssignmentSummary method - failed to edit"
					+ aoAssignmentsSummaryBean.getId() + " \n");
			throw new ApplicationException("Exception occured while edit in PaymentModuleService ", loAppEx);
		}
		return loFlag;
	}

	/**
	 * This method is used to delete Assignment Details from contract invoice
	 * Screen <li>This query used: fetchCountPaymentAssignment</li> <li>This
	 * query used: fetchCountInvoiceDetails</li> <li>This query used:
	 * delAdvanceAssignment</li> <li>This query used: delInvoiceDetails</li> <li>
	 * This query used: delAssignment</li>
	 * 
	 * @param aoMybatisSession Session Object
	 * @param aoAssignmentsSummaryBean : Assignment Summary
	 * @return Boolean Status of Delete Assignment
	 * @throws ApplicationException : Application Exception
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Boolean delAssignment(SqlSession aoMybatisSession, AssignmentsSummaryBean aoAssignmentsSummaryBean)
			throws ApplicationException
	{
		boolean lbError = false;
		Boolean loDelStatus = true;
		Integer loFetchCount = HHSConstants.INT_ZERO;
		Integer loFetchInvoiceCount = HHSConstants.INT_ZERO;
		HashMap loHashMapForAssignment = new HashMap<String, String>();
		// Split id attribute as it is formed as
		// assignmentId_budgetAdvanceId_advanceAssignmentId
		loHashMapForAssignment.put(HHSConstants.ASSIGNMENT_ID, aoAssignmentsSummaryBean.getId());
		loHashMapForAssignment.put(HHSConstants.BUDGET_ADVANCE_ID, aoAssignmentsSummaryBean.getBudgetAdvanceId());
		loHashMapForAssignment.put(HHSConstants.CONTRACT_BUDGET_ID, aoAssignmentsSummaryBean.getContractBudgetID());

		try
		{

			// Check Assignments from S400_Advance Payment Request Screen
			loFetchCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loHashMapForAssignment,
					HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER, HHSConstants.FETCH_COUNT_PAYMENT_ASSIGNMENT,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			// Set Parameters for fetchCountInvoiceDetails
			loHashMapForAssignment.put(HHSConstants.INVOICE_ENTRY_TYPE_ID, HHSConstants.ASSIGNMENT_ENTRY_TYPE_ID);
			loHashMapForAssignment.put(HHSConstants.INVOICE_LINE_ITEM_ID, aoAssignmentsSummaryBean.getId());
			loHashMapForAssignment.put(HHSConstants.STATUS, HHSConstants.APPROVED);
			// Check Assignments from S329_Invoice Screen
			loFetchInvoiceCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loHashMapForAssignment,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_COUNT_INVOICE_DETAILS,
					HHSConstants.JAVA_UTIL_HASH_MAP);

			if ((loFetchCount + loFetchInvoiceCount) == HHSConstants.INT_ZERO)
			{
				// Delete Advance_Assignment
				DAOUtil.masterDAO(aoMybatisSession, aoAssignmentsSummaryBean,
						HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER, HHSConstants.PM_DELETE_ADVANCE_ASSIGNMENT,
						HHSConstants.INPUT_PARAM_CLASS_ASSIGNMENT_BEAN);
				// Delete from Invoice_Details if Assignee has un-approved
				// assignments
				DAOUtil.masterDAO(aoMybatisSession, loHashMapForAssignment, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
						HHSConstants.DEL_INVOICE_DETAILS, HHSConstants.JAVA_UTIL_HASH_MAP);
				// Delete Assignee
				DAOUtil.masterDAO(aoMybatisSession, aoAssignmentsSummaryBean, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
						HHSConstants.CI_DEL_ASSIGNMENT, HHSConstants.INPUT_PARAM_CLASS_ASSIGNMENT_BEAN);
			}
			else
			{
				// To display Error message on Page
				lbError = true;
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.MSG_KEY_INVOICE_ASSIGNMENT_DELETE_CHECK));
			}
		}
		// Application Exception thrown by DAO layer are caught and logged here
		// and then thrown
		catch (ApplicationException loAppEx)
		{
			if (lbError)
			{
				loAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, loAppEx.toString());
			}
			loAppEx.addContextData("ID : ", aoAssignmentsSummaryBean.getId());
			LOG_OBJECT.Error("Exception occured  delete at PaymentModuleService: delAssignment() ", loAppEx);
			setMoState("PaymentModuleService: delAssignment() failed to delete at id:"
					+ aoAssignmentsSummaryBean.getId() + " \n");
			throw loAppEx;
		}
		// Exception thrown by DAO layer are caught and logged here and then
		// thrown
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while delete in PaymentModuleService ", loAppEx);
			setMoState("Transaction Failed:: InvoiceService: delAssignment method - failed to delete"
					+ aoAssignmentsSummaryBean.getId() + " \n");
			throw new ApplicationException("Exception occured while delete in PaymentModuleService ", loAppEx);
		}
		return loDelStatus;
	}

	/**
	 * This method insert/update Assignment amount information for Assignee. <li>
	 * This query used: updatePaymentAssignmentDetails</li> <li>This query used:
	 * insertPaymentAssignmentDetails</li>
	 * @param aoMybatiSession Session
	 * @param aoQueryParam - Hashmap
	 * @throws ApplicationException - Application Exception
	 */
	@SuppressWarnings("rawtypes")
	private void insertUpdateAssignmentDetail(SqlSession aoMybatiSession, HashMap aoQueryParam)
			throws ApplicationException
	{

		int liUpdateCount = HHSConstants.INT_ZERO;
		try
		{
			// update the invoice detail if it has entry for that line item
			// otherwise insert row for the line item
			liUpdateCount = (Integer) DAOUtil.masterDAO(aoMybatiSession, aoQueryParam,
					HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER, HHSConstants.PM_UPDATE_PAYMENT_ASSIGNMET_DETAILS,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			if (liUpdateCount < HHSConstants.INT_ONE)
			{
				DAOUtil.masterDAO(aoMybatiSession, aoQueryParam, HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER,
						HHSConstants.PM_INSERT_PAYMENT_ASSIGNMET_DETAILS, HHSConstants.JAVA_UTIL_HASH_MAP);
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoAppExp)
		{
			// Set the state, added context data and added error log if any
			// application exception occurs
			setMoState("Transaction Failed:: PaymentModuleService: insertUpdateAssignmentDetail method - failed to update Advance Assignment"
					+ aoQueryParam.get(HHSConstants.ID) + " \n");
			aoAppExp.addContextData("Exception occured while updating Advance Assignment ", aoAppExp);
			LOG_OBJECT.Error("Exception occured in PaymentModuleService: insertUpdateAssignmentDetail method:: ",
					aoAppExp);
			throw aoAppExp;
		}
	}

	/**
	 * This method modified as a part of release 3.1.0 for enhancement 6023
	 * <ul>
	 * <li>set advance status for interface review (payment review)task</li>
	 * </ul>
	 * 
	 * This method is used to set budget status on final Approve and returned
	 * for revision in case of advance Request review Task.
	 * <ul>
	 * <li>IUpdate the status of the advance</li>
	 * <li>This query used: setAdvanceStatusForReviewTask</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession Mybatis Session Object.
	 * @param aoFinalFinish FinalFinish parameter.
	 * @param aoTaskDetailsBean TaskDetailsBean parameter.
	 * @param asBudgetStatus BudgetStatus parameter.
	 * @throws Exception an exception object
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void setAdvanceStatusForReviewTask(SqlSession aoMyBatisSession, Boolean aoFinalFinish,
			TaskDetailsBean aoTaskDetailsBean, String asBudgetStatus) throws ApplicationException
	{
		try
		{
			if (aoFinalFinish)
			{
				Map loHashMap = new HashMap<String, String>();
				loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, aoTaskDetailsBean.getContractId());
				loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, aoTaskDetailsBean.getBudgetId());
				loHashMap.put(HHSConstants.BUDGET_ADVANCE_ID, aoTaskDetailsBean.getBudgetAdvanceId());
				loHashMap.put(HHSConstants.STATUS_ID, asBudgetStatus);
				loHashMap.put(HHSConstants.MODIFY_BY, aoTaskDetailsBean.getUserId());
				// code added to set advance status for interface review
				// (payment review)task
				if (null != aoTaskDetailsBean.getTaskSource()
						&& aoTaskDetailsBean.getTaskSource().equals(HHSConstants.BATCH))
				{
					DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER,
							HHSConstants.SET_ADVANCE_STATUS_FOR_INTERFACE_REVIEW_TASK, HHSConstants.JAVA_UTIL_MAP);
				}
				else
				{
					DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER,
							HHSConstants.SET_ADVANCE_STATUS_REVIEW_TASK, HHSConstants.JAVA_UTIL_MAP);
				}

				// changes for agency outbound interafce
				DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
						HHSConstants.UPDATE_MODIFIED_DATE_ASSIGNMENT_IN_ADVANCE_REVIEW, HHSConstants.JAVA_UTIL_MAP);
				// changes for agency outbound interafce
				setMoState("Transaction Success:: PaymentModuleService:setAdvanceStatusForReviewTask"
						+ " method - success to update record " + " \n");
			}

		}
		// catch any application exception thrown from the code due to UPDATE
		// statement and throw it
		// forward
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("ApplicationException occured while executing query setAdvanceStatusForReviewTask ",
					loAppEx);
			setMoState("Transaction Failed:: PaymentModuleService:setAdvanceStatusForReviewTask"
					+ " method - failed to update record " + " \n");
			loAppEx.addContextData("BudgetId passed: ", aoTaskDetailsBean.getBudgetId());
			throw loAppEx;
		}
		// catch any null exception thrown from the code due to UPDATE
		// statement and throw it
		// forward
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while executing query in setAdvanceStatusForReviewTask ", loAppEx);
			setMoState("Transaction Failed:: PaymentModuleService:setAdvanceStatusForReviewTask method"
					+ " - failed to update record " + " \n");
			throw new ApplicationException("Exception occured while executing query in setAdvanceStatusForReviewTask",
					loAppEx);
		}
	}

	/**
	 * This method is used to delete budget advance record . for revision in
	 * case of advance Request review Task.
	 * <ul>
	 * <li>IUpdate the status of the advance</li>
	 * <li>This query used: deleteBudgetAdvance</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession Mybatis Session Object.
	 * @param aoFinalFinish FinalFinish parameter.
	 * @param aoTaskDetailsBean TaskDetailsBean parameter.
	 * @param asBudgetStatus BudgetStatus parameter.
	 * @throws Exception an exception object
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void deleteBudgetAdvance(SqlSession aoMyBatisSession, Boolean aoFinalFinish,
			TaskDetailsBean aoTaskDetailsBean, String asBudgetStatus) throws Exception
	{
		try
		{
			if (aoFinalFinish)
			{
				Map loHashMap = new HashMap<String, String>();
				loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, aoTaskDetailsBean.getContractId());
				loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, aoTaskDetailsBean.getBudgetId());
				loHashMap.put(HHSConstants.BUDGET_ADVANCE_ID, aoTaskDetailsBean.getBudgetAdvanceId());
				loHashMap.put(HHSConstants.STATUS_ID, asBudgetStatus);
				loHashMap.put(HHSConstants.MODIFY_BY, aoTaskDetailsBean.getUserId());
				DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER,
						HHSConstants.DELETE_BUDGET_ADVANCE, HHSConstants.JAVA_UTIL_MAP);
				setMoState("Transaction Success:: PaymentModuleService:deleteBudgetAdvance"
						+ " method - success to update record " + " \n");
			}

		}
		// catch any application exception thrown from the code due to UPDATE
		// statement and throw it
		// forward
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("ApplicationException occured while executing query deleteBudgetAdvance ", loAppEx);
			setMoState("Transaction Failed:: PaymentModuleService:DELETE_BUDGET_ADVANCE"
					+ " method - failed to update record " + " \n");
			loAppEx.addContextData("BudgetId passed: ", aoTaskDetailsBean.getBudgetId());
			throw loAppEx;
		}
		// catch any null exception thrown from the code due to UPDATE
		// statement and throw it
		// forward
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while executing query in deleteBudgetAdvance", loAppEx);
			setMoState("Transaction Failed:: PaymentModuleService:deleteBudgetAdvance method"
					+ " - failed to update record " + " \n");
			loAppEx = new ApplicationException("Exception occured while executing query in deleteBudgetAdvance",
					loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * This method is used to set payment record status for approval of payment
	 * review Task.
	 * <ul>
	 * <li>IUpdate the status of the advance</li>
	 * <li>This query used: setPeriod</li>
	 * <li>This query used: setPaymentStatus</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession Mybatis Session Object.
	 * @param aoFinalFinish FinalFinish parameter.
	 * @param aoTaskDetailsBean TaskDetailsBean parameter.
	 * @param asBudgetStatus BudgetStatus parameter.
	 * @throws Exception an exception object
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void setPaymentStatus(SqlSession aoMyBatisSession, Boolean aoFinalFinish, TaskDetailsBean aoTaskDetailsBean,
			String asBudgetStatus) throws Exception
	{
		try
		{
			Map loHashMap = new HashMap<String, String>();
			loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, aoTaskDetailsBean.getContractId());
			loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, aoTaskDetailsBean.getBudgetId());
			loHashMap.put(HHSConstants.INVOICE_ID, aoTaskDetailsBean.getInvoiceId());
			loHashMap.put(HHSConstants.BUDGET_ADVANCE_ID, aoTaskDetailsBean.getBudgetAdvanceId());
			loHashMap.put(HHSConstants.STATUS_ID, asBudgetStatus);
			loHashMap.put(HHSConstants.MODIFY_BY, aoTaskDetailsBean.getUserId());
			loHashMap.put(HHSConstants.PERIOD, aoTaskDetailsBean.getPeriod());
			if (HHSConstants.ONE.equalsIgnoreCase(aoTaskDetailsBean.getLevel()))
			{
				DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER,
						HHSConstants.SET_PERIOD, HHSConstants.JAVA_UTIL_MAP);
				setMoState("Transaction Success:: PaymentModuleService:setPaymentStatus: setPeriod query"
						+ " method - success to update record " + " \n");
			}
			if (aoFinalFinish)
			{
				LOG_OBJECT.Debug("before fetching paymentAmount");
				// START || Changes for enhancement 6487 for Release 3.12.0
				List<Map<Object, Object>> lsPaymentAmountList = (List<Map<Object, Object>>) DAOUtil.masterDAO(
						aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER,
						HHSConstants.FETCH_PAYMENT_AMOUNT, HHSConstants.JAVA_UTIL_MAP);
				Iterator iterator = lsPaymentAmountList.iterator();
				while (iterator.hasNext())
				{
					Map<Object, Object> loPaymentInfo = (Map<Object, Object>) iterator.next();
					String lsPaymentAmount = loPaymentInfo.get(HHSConstants.PAYMENT_AMOUNT).toString();
					String lsPaymentId = loPaymentInfo.get(HHSConstants.PAYMENT_ID_COL).toString();
					LOG_OBJECT.Debug("lsPaymentAmount " + lsPaymentAmount + " lsPaymentId " + lsPaymentId);
					loHashMap.put(HHSConstants.PAYMENT_ID, lsPaymentId);
					loHashMap.put(HHSConstants.PAYMENT_VALUE, lsPaymentAmount);
					if (null != lsPaymentAmount && lsPaymentAmount.equalsIgnoreCase(HHSConstants.ZERO))
					{
						loHashMap.put(HHSConstants.STATUS_ID, PropertyLoader.getProperty(
								HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PAYMENT_DISBURSED));
					}
					else
					{
						loHashMap.put(HHSConstants.STATUS_ID, asBudgetStatus);
					}
					// END || Changes for enhancement 6487 for Release 3.12.0
					DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER,
							HHSConstants.SET_PAYMENT_STATUS, HHSConstants.JAVA_UTIL_MAP);
				}
				setMoState("Transaction Success:: PaymentModuleService:setPaymentStatus"
						+ " method - success to update record " + " \n");
			}

		}
		// catch any application exception thrown from the code due to UPDATE
		// statement and throw it
		// forward
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("ApplicationException occured while executing query setPaymentStatus ", loAppEx);
			setMoState("Transaction Failed:: PaymentModuleService:setPaymentStatus "
					+ " method - failed to update record " + " \n");
			loAppEx.addContextData("BudgetId passed: ", aoTaskDetailsBean.getBudgetId());
			throw loAppEx;
		}
		// catch any exception thrown other than application Exception
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while executing query in setPaymentStatus ", loAppEx);
			setMoState("Transaction Failed:: PaymentModuleService:setPaymentStatus method"
					+ " - failed to update record " + " \n");
			loAppEx = new ApplicationException("Exception occured while executing query in setPaymentStatus", loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * This method modified as a part of release 3.1.0 for enhancement 6023.
	 * <ul>
	 * <li>code added to set invoice status against rejected payment</li>
	 * </ul>
	 * 
	 * This method is used to set Invoice status for return for revision of
	 * payment review Task.
	 * <ul>
	 * <li>Update the status of the advance</li>
	 * <li>This query used: setInvoiceStatus</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession Mybatis Session Object.
	 * @param aoFinalFinish FinalFinish parameter.
	 * @param aoTaskDetailsBean TaskDetailsBean parameter.
	 * @param asBudgetStatus BudgetStatus parameter.
	 * @throws Exception an exception object
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void setInvoiceStatus(SqlSession aoMyBatisSession, Boolean aoFinalFinish, TaskDetailsBean aoTaskDetailsBean,
			String asBudgetStatus) throws ApplicationException
	{
		try
		{
			if (aoFinalFinish)
			{
				Map loHashMap = new HashMap<String, String>();
				loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, aoTaskDetailsBean.getContractId());
				loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, aoTaskDetailsBean.getBudgetId());
				loHashMap.put(HHSConstants.INVOICE_ID, aoTaskDetailsBean.getInvoiceId());
				loHashMap.put(HHSConstants.STATUS_ID, asBudgetStatus);
				loHashMap.put(HHSConstants.MODIFY_BY, aoTaskDetailsBean.getUserId());
				// code added to set invoice status against rejected payment
				if (null != aoTaskDetailsBean.getTaskSource()
						&& aoTaskDetailsBean.getTaskSource().equals(HHSConstants.BATCH))
				{
					DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER,
							"setInterfaceInvoiceStatus", HHSConstants.JAVA_UTIL_MAP);
				}
				else
				{
					DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER,
							HHSConstants.SET_INVOICE_STATUS, HHSConstants.JAVA_UTIL_MAP);
				}
				setMoState("Transaction Success:: PaymentModuleService:setInvoiceStatus"
						+ " method - success to update record " + " \n");
			}

		}
		// catch any application exception thrown from the code due to UPDATE
		// statement and throw it
		// forward
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("ApplicationException occured while executing query setInvoiceStatus ", loAppEx);
			setMoState("Transaction Failed:: PaymentModuleService:setInvoiceStatus"
					+ " method - failed to update record " + " \n");
			loAppEx.addContextData("BudgetId passed: ", aoTaskDetailsBean.getBudgetId());
			throw loAppEx;
		}
		// catch any null exception thrown from the code due to UPDATE
		// statement and throw it
		// forward
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while executing query in setInvoiceStatus ", loAppEx);
			setMoState("Transaction Failed:: PaymentModuleService:setInvoiceStatus method"
					+ " - failed to update record " + " \n");
			throw new ApplicationException("Exception occured while executing query in setInvoiceStatus", loAppEx);
		}
	}

	/**
	 * This method modified as a part of release 3.1.0 for enhancement 6023.
	 * <ul>
	 * <li>code added fetch voucher numbers required to send notification NT400</li>
	 * </ul>
	 * 
	 * This method is used to delete payment records associated with Invoice for
	 * return for revision of payment review Task.
	 * <ul>
	 * <li>IUpdate the status of the advance</li>
	 * <li>This query used: deletePaymentRecords</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession Mybatis Session Object.
	 * @param aoFinalFinish FinalFinish parameter.
	 * @param aoTaskDetailsBean TaskDetailsBean parameter.
	 * @param asBudgetStatus BudgetStatus parameter.
	 * @throws Exception an exception object
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public HashMap deletePaymentRecords(SqlSession aoMyBatisSession, Boolean aoFinalFinish,
			TaskDetailsBean aoTaskDetailsBean, String asBudgetStatus, HashMap<String, Object> aoHMNotifyParam)
			throws ApplicationException
	{
		try
		{
			if (aoFinalFinish)
			{
				Map loHashMap = new HashMap<String, String>();
				Map loAgencyDetailsMap = new HashMap<String, String>();
				HashMap<String, String> loRequestMap = new HashMap<String, String>();
				loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, aoTaskDetailsBean.getContractId());
				loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, aoTaskDetailsBean.getBudgetId());
				loHashMap.put(HHSConstants.INVOICE_ID, aoTaskDetailsBean.getInvoiceId());
				loHashMap.put(HHSConstants.BUDGET_ADVANCE_ID, aoTaskDetailsBean.getBudgetAdvanceId());
				loHashMap.put(HHSConstants.STATUS_ID, asBudgetStatus);
				loHashMap.put(HHSConstants.MODIFY_BY, aoTaskDetailsBean.getUserId());
				loHashMap.put(HHSConstants.AS_AGENCY_USER_ID, aoTaskDetailsBean.getUserId());
				// code added fetch voucher numbers required to send
				// notification NT400
				if (null != aoTaskDetailsBean.getTaskSource()
						&& aoTaskDetailsBean.getTaskSource().equals(HHSConstants.BATCH))
				{
					String lsVouchers = (String) DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
							HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER,
							HHSConstants.FETCH_COMMA_SEPARATED_VOUCHER_LIST, HHSConstants.JAVA_UTIL_MAP);
					loRequestMap = (HashMap) aoHMNotifyParam.get(TransactionConstants.REQUEST_MAP_PARAMETER_NAME);
					loRequestMap.put(HHSConstants.VOUCHER_ID, lsVouchers);
					loAgencyDetailsMap = (Map) DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
							HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER, HHSConstants.FETCH_AGENCY_DETAILS,
							HHSConstants.JAVA_UTIL_MAP);
					loRequestMap = (HashMap) aoHMNotifyParam.get(TransactionConstants.REQUEST_MAP_PARAMETER_NAME);
					loRequestMap.put(HHSConstants.AGENCY_USER_NAME,
							(String) loAgencyDetailsMap.get(HHSConstants.AGENCY_USER_NAME));
					loRequestMap.put(HHSConstants.AGENCY_USER_EMAIL_ID,
							(String) loAgencyDetailsMap.get(HHSConstants.AGENCY_USER_EMAIL_ID));
					aoHMNotifyParam.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
					DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER,
							HHSConstants.DELETE_INTERFACE_PAYMENT_RECORDS, HHSConstants.JAVA_UTIL_MAP);
				}
				else
				{
					DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER,
							HHSConstants.DELETE_PAYMENT_RECORDS, HHSConstants.JAVA_UTIL_MAP);
				}

				setMoState("Transaction Success:: PaymentModuleService:deletePaymentRecords"
						+ " method - success to update record " + " \n");
			}

		}
		// catch any application exception thrown from the code due to UPDATE
		// statement and throw it
		// forward
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("ApplicationException occured while executing query deletePaymentRecords ", loAppEx);
			setMoState("Transaction Failed:: PaymentModuleService:deletePaymentRecords"
					+ " method - failed to update record " + " \n");
			loAppEx.addContextData("BudgetId passed: ", aoTaskDetailsBean.getBudgetId());
			throw loAppEx;
		}
		// catch any null exception thrown from the code due to UPDATE
		// statement and throw it
		// forward
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while executing query in deletePaymentRecords ", loAppEx);
			setMoState("Transaction Failed:: PaymentModuleService:deletePaymentRecords method"
					+ " - failed to update record " + " \n");
			throw new ApplicationException("Exception occured while executing query in deletePaymentRecords", loAppEx);
		}
		return aoHMNotifyParam;
	}

	/**
	 * <ul>
	 * <li>This service class is invoked through getPaymentHeaderDetails
	 * transaction id for Payment Detail screen</li>
	 * <li>This query used: getPaymentHeaderDetails</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - SqlSession Object
	 * @param aoHashMap - HashMap
	 * @return loPaymentBean - PaymentBean
	 * @throws ApplicationException - ApplicationException object
	 */
	public PaymentBean fetchPaymentHeaderDetails(SqlSession aoMybatisSession, HashMap<String, String> aoHashMap)
			throws ApplicationException
	{
		PaymentBean loPaymentBean = null;

		try
		{
			// Fetches data for payment details
			loPaymentBean = (PaymentBean) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER, HHSConstants.PAYMENT_HEADER_DETAILS,
					HHSConstants.JAVA_UTIL_HASH_MAP);

		}// Exception occur if database is down or getting null in required
			// parameter
		catch (ApplicationException aoAppExp)
		{
			setMoState("Error while fetching Payment details for payment id : " + aoHashMap.get("paymentId"));
			aoAppExp.addContextData(
					"Error while fetching Payment details for payment id : " + aoHashMap.get("paymentId"), aoAppExp);
			LOG_OBJECT.Error("Error while fetching Payment details for payment id : ", aoAppExp);
			throw aoAppExp;
		}
		return loPaymentBean;
	}

	/**
	 * <ul>
	 * <li>This method fetchPaymentLineDetails will get the Payment Line Details
	 * on the basis of invoiceId and paymentId</li>
	 * <li>This query used: fetchPaymentFyBudgetSummary</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - SqlSession Object
	 * @param aoHashMap - HashMap
	 * @return loPaymentBudgetDetails - BudgetDetails
	 * @throws ApplicationException - ApplicationException object
	 */
	public BudgetDetails fetchPaymentLineDetails(SqlSession aoMybatisSession, HashMap<String, String> aoHashMap)
			throws ApplicationException
	{
		BudgetDetails loPaymentBudgetDetails = null;

		try
		{
			// Getting FY budget amount and YTD invoice amount
			loPaymentBudgetDetails = (BudgetDetails) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER, HHSConstants.PAYMENT_BUDGET_SUMMARY,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			if (loPaymentBudgetDetails == null)
			{
				return new BudgetDetails();
			}
			else
			{
				// Getting actual paid amount
				BigDecimal loActPaidAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
						HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER, HHSConstants.PAYMENT_ACTUAL_PAID,
						HHSConstants.JAVA_UTIL_HASH_MAP);

				loPaymentBudgetDetails.setYtdActualPaid(loActPaidAmount);
				loPaymentBudgetDetails.setRemainingAmount(loPaymentBudgetDetails.getApprovedBudget().subtract(
						loPaymentBudgetDetails.getYtdInvoicedAmount()));
				loPaymentBudgetDetails.setCashBalance(loPaymentBudgetDetails.getApprovedBudget().subtract(
						loActPaidAmount));

				// Getting total invoice amount
				BigDecimal loTotalInvoiceAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
						HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER, HHSConstants.TOTAL_INVOICE_AMOUNT,
						HHSConstants.JAVA_UTIL_HASH_MAP);

				loPaymentBudgetDetails.setInvoicedAmount(loTotalInvoiceAmount);

				// Getting total payment amount
				BigDecimal loTotalPaymentAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
						HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER, HHSConstants.TOTAL_PAYMENT_AMOUNT,
						HHSConstants.JAVA_UTIL_HASH_MAP);

				loPaymentBudgetDetails.setPaymentAmount(loTotalPaymentAmount);
			}

		}// Exception occur if database is down or getting null in required
			// parameter
		catch (ApplicationException aoAppExp)
		{
			setMoState("Error while fetching Payment line details for payment id : " + aoHashMap.get("paymentId"));
			aoAppExp.addContextData(
					"Error while fetching Payment line details for payment id : " + aoHashMap.get("paymentId"),
					aoAppExp);
			LOG_OBJECT.Error("Error while fetching Payment line details ", aoAppExp);
			throw aoAppExp;
		}

		return loPaymentBudgetDetails;
	}

	/**
	 * <ul>
	 * <li>This method fetchPaymentVoucherList will get the Payment Line Details
	 * on the basis of paymentId</li>
	 * <li>This query used: getPaymentVoucherNum</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - SqlSession Object
	 * @param aoHashMap - HashMap
	 * @return loPaymentVoucher - List<PaymentBean>
	 * @throws ApplicationException - ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public List<PaymentBean> fetchPaymentVoucherList(SqlSession aoMybatisSession, HashMap<String, String> aoHashMap)
			throws ApplicationException
	{
		List<PaymentBean> loPaymentVoucher = null;

		try
		{
			// Getting payment voucher list
			loPaymentVoucher = (List<PaymentBean>) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER, HHSConstants.PAYMENT_VOUCHER_DETAIL,
					HHSConstants.JAVA_UTIL_HASH_MAP);

		}// Exception occur if database is down or getting null in required
			// parameter
		catch (ApplicationException aoAppExp)
		{
			setMoState("Error while fetching Payment voucher details for payment id : " + aoHashMap.get("paymentId"));
			aoAppExp.addContextData(
					"Error while fetching Payment voucher details for payment id : " + aoHashMap.get("paymentId"),
					aoAppExp);
			LOG_OBJECT.Error("Error while fetching Payment voucher details ", aoAppExp);
			throw aoAppExp;
		}

		return loPaymentVoucher;
	}

	/**
	 * <ul>
	 * <li>This service class is invoked through getPaymentReviewHeaderDetails
	 * transaction id for Payment Review Task Detail screen</li>
	 * <li>This query used: getPaymentReviewHeaderDetails</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - SqlSession Object
	 * @param aoHashMap - HashMap
	 * @return loPaymentBean - PaymentBean
	 * @throws ApplicationException - ApplicationException object
	 */
	public PaymentBean fetchPaymentReviewHeaderDetails(SqlSession aoMybatisSession, HashMap<String, String> aoHashMap)
			throws ApplicationException
	{
		PaymentBean loPaymentBean = null;

		try
		{
			// Getting data to display on Payment review task detail screen
			loPaymentBean = (PaymentBean) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER, HHSConstants.PAYMENT_REVIEW_HEADER_DETAILS,
					HHSConstants.JAVA_UTIL_HASH_MAP);

		}// Exception occur if database is down or getting null in required
			// parameter
		catch (ApplicationException aoAppExp)
		{
			setMoState("Error while fetching Payment review header details for invoice id : "
					+ aoHashMap.get("invoiceId"));
			aoAppExp.addContextData(
					"Error while fetching Payment review header details for invoice id : " + aoHashMap.get("invoiceId"),
					aoAppExp);
			LOG_OBJECT.Error("Error while fetching Payment review header details ", aoAppExp);
			throw aoAppExp;
		}

		return loPaymentBean;
	}

	/**
	 * This method validates the Total CoA Payment amount entered by the user
	 * with the Total Payment Amount for the given Voucher Id.
	 * 
	 * It returns a map with error_code and error_msg if validation fails. <li>
	 * This query used: getPaymentVoucherList</li> <li>This query used:
	 * getCoASumAmount</li> <li>This query used: getTotalPaymentAmount</li>
	 * 
	 * @param aoMyBatisSession : MyBatis Session
	 * @param aoTaskDetailsBean : taskDetailsBean object containing the required
	 *            information for processing
	 * @return loValidationResultMap: Map object
	 * @throws ApplicationException : applicationException object
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Map validateCoATotalPayment(SqlSession aoMybatisSession, TaskDetailsBean aoTaskDetailsBean)
			throws ApplicationException
	{
		Map loValidationResultMap = new HashMap<String, Object>();
		int liCounter = 0;
		BigDecimal loCoAPaymentSum = null;
		BigDecimal loTotalPaymentAmount = null;
		String lsPaymentVoucherId = null;
		String lsPaymentVoucherNum = null;
		StringBuffer loInvalidPaymentVoucherString = new StringBuffer(HHSConstants.EMPTY_STRING);

		loValidationResultMap.put(HHSConstants.ERROR_CODE, HHSConstants.INT_ZERO);
		loValidationResultMap.put(HHSConstants.CLC_ERROR_MSG, HHSConstants.EMPTY_STRING);

		List<PaymentBean> loPaymentVoucherList = (List<PaymentBean>) DAOUtil.masterDAO(aoMybatisSession,
				aoTaskDetailsBean, HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER,
				HHSConstants.GET_PAYMENT_VOUCHER_LIST, HHSConstants.CS_TASK_DETAILS_BEAN);

		for (Iterator loIterator = loPaymentVoucherList.iterator(); loIterator.hasNext();)
		{
			PaymentBean loPaymentBean = (PaymentBean) loIterator.next();
			lsPaymentVoucherId = (String) loPaymentBean.getPaymentId();
			lsPaymentVoucherNum = (String) loPaymentBean.getPaymentVoucherNo();
			loCoAPaymentSum = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, lsPaymentVoucherId,
					HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER, HHSConstants.GET_COA_SUM_AMOUNT,
					HHSConstants.JAVA_LANG_STRING);

			loTotalPaymentAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, lsPaymentVoucherId,
					HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER, HHSConstants.GET_TOTAL_PAYMENT_AMOUNT,
					HHSConstants.JAVA_LANG_STRING);

			// creates the voucher id string
			if (loCoAPaymentSum.compareTo(loTotalPaymentAmount) != 0)
			{
				loInvalidPaymentVoucherString.append((liCounter == 0 ? lsPaymentVoucherNum : HHSConstants.COMMA_SPACE
						+ lsPaymentVoucherNum));
				liCounter++;
			}
		}

		// lsInvalidPaymentVoucherString contains some Voucher Ids for which
		// validation has failed
		String lsBatchInProgressFlag = (String) DAOUtil.masterDAO(aoMybatisSession, lsPaymentVoucherId,
				HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER, HHSConstants.FETCH_BATCH_IN_PROGRESS_FLAG,
				HHSConstants.JAVA_LANG_STRING);
		if (null != lsBatchInProgressFlag && lsBatchInProgressFlag.equalsIgnoreCase(HHSConstants.ONE))
		{
			loValidationResultMap.put(HHSConstants.ERROR_CODE, HHSConstants.INT_ONE);
			loValidationResultMap.put(HHSConstants.CLC_ERROR_MSG, PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.BATCH_IN_PROGRESS_ERROR));
		}
		else if (!loInvalidPaymentVoucherString.toString().equalsIgnoreCase(HHSConstants.EMPTY_STRING))
		{
			loValidationResultMap.put(HHSConstants.ERROR_CODE, HHSConstants.INT_ONE);
			loValidationResultMap.put(
					HHSConstants.CLC_ERROR_MSG,
					PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSConstants.COA_SUM_NOT_EQUAL_TOTAL_PAYMENT_AMOUNT)
							+ HHSConstants.SPACE
							+ loInvalidPaymentVoucherString.toString() + HHSConstants.DOT);
		}
		return loValidationResultMap;
	}

	/**
	 * <p>
	 * This method fetch unallocated funds details from DB
	 * <ul>
	 * <li>Call fetchUnallocatedFunds query set sub budget id as where clause</li>
	 * <li>If the user is coming for the first time than a blank value is insert
	 * </li>
	 * <li>against that record, to ease the update.
	 * <li>This query used: paymentCOFFetch</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatisSession Sql session object
	 * @param aoCBGridBeanObj CBGridBean passed with attributes set in session.
	 * @return List<PaymentChartOfAllocation> loPaymentChartOfAllocationList
	 * @throws ApplicationException Application exception if error occur.
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public List fetchpaymentCOF(SqlSession aoMybatisSession, CBGridBean aoCBGridBeanObj) throws ApplicationException
	{
		HashMap<String, Object> aoHashMap = new HashMap<String, Object>();
		List<PaymentChartOfAllocation> loPaymentChartOfAllocationList = null;
		// here subBudgt id is used as payment Id
		aoHashMap.put(HHSConstants.PAYMENT_ID, aoCBGridBeanObj.getSubBudgetID());
		aoHashMap.put(HHSConstants.CONTRACT_ID, aoCBGridBeanObj.getContractID());
		try
		{
			//Added in R7-To Fix Defect #7211(fetching previous date hour(4PM))
			HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb
					.getInstance().getCacheObject(ApplicationConstants.APPLICATION_SETTING);
			aoHashMap.put(HHSConstants.PREV_DATE_HOUR,
					loApplicationSettingMap.get(HHSConstants.PREV_DATE_HOUR_KEY));

			// fetching the details for paymentCOF.
			loPaymentChartOfAllocationList = (List<PaymentChartOfAllocation>) DAOUtil.masterDAO(aoMybatisSession,
					aoHashMap, HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER, HHSConstants.PAYMENT_COF_FETCH,
					HHSConstants.JAVA_UTIL_HASH_MAP);
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppExp)
		{
			// setting the transaction state for exception and setting the
			// context
			setMoState("error occured while fetching paymentCOF for business type id "
					+ aoHashMap.get(HHSConstants.PAYMENT_ID));
			loAppExp.addContextData("Exception occured while fetching paymentCOF ", loAppExp);
			LOG_OBJECT.Error("error occured while fetching paymentCOF ", loAppExp);
			throw loAppExp;
		}
		return loPaymentChartOfAllocationList;
	}

	/**
	 * <p>
	 * This method update Unallocated fund details in DB
	 * <ul>
	 * <li>1.Get all unallocated funds details in UnallocatedFunds Bean</li>
	 * <li>2.Call updateUnallocatedFunds query</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatisSession Sql session object
	 * @param aoPaymentChartOfAllocation PaymentChartOfAllocation Bean Object
	 * @return lbUpdateStatus boolean
	 * @throws ApplicationException Application exception if error occur.
	 */

	public boolean updatepaymentCOF(SqlSession aoMybatisSession, PaymentChartOfAllocation aoPaymentChartOfAllocation)
			throws ApplicationException
	{
		boolean lbUpdateStatus = false;
		boolean lbError = false;

		try
		{
			String lsId = aoPaymentChartOfAllocation.getId();
			String[] loStartDateArray = lsId.split(HHSConstants.HYPHEN);
			aoPaymentChartOfAllocation.setId(loStartDateArray[0]);
			aoPaymentChartOfAllocation.setCommodityLineCode(loStartDateArray[1]);
			aoPaymentChartOfAllocation.setActgLn(loStartDateArray[2]);
			aoPaymentChartOfAllocation.setPaymentId(aoPaymentChartOfAllocation.getSubBudgetID());
			
			//Added in R7-To Fix Defect #7211(fetching previous date hour(4PM))
			HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb
					.getInstance().getCacheObject(ApplicationConstants.APPLICATION_SETTING);
			String loprevDateHour = (String) loApplicationSettingMap.get(HHSConstants.PREV_DATE_HOUR_KEY);
			aoPaymentChartOfAllocation.setPrevDateHour(loprevDateHour);
			
			BigDecimal loRemainingAmt = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, aoPaymentChartOfAllocation,
					HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER, HHSConstants.PAYMENT_COF_REAMINING_AMOUNT_FETCH,
					HHSConstants.PAYMENT_CHART_OF_ALLOCATION);

			if ((new BigDecimal(aoPaymentChartOfAllocation.getPaymentAmount())).compareTo(loRemainingAmt) > 0)
			{
				lbError = true;
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.PAYMENT_REMAINING_AMOUNT_CHECK));
			}
			else
			{
				DAOUtil.masterDAO(aoMybatisSession, aoPaymentChartOfAllocation,
						HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER, HHSConstants.PAYMENT_COF_EDIT,
						HHSConstants.PAYMENT_CHART_OF_ALLOCATION);
				lbUpdateStatus = true;
			}
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppExp)
		{
			// setting the transaction state for exception and setting the
			// context
			if (lbError)
			{
				loAppExp.addContextData(HHSConstants.GRID_ERROR_MESSAGE, loAppExp.toString());
			}
			setMoState("error occured while updating paymentCOF for business type id "
					+ aoPaymentChartOfAllocation.getPaymentId());
			loAppExp.addContextData("Exception occured while  updating  paymentCOF ", loAppExp);
			LOG_OBJECT.Error("error occured while  updating  paymentCOF ", loAppExp);
			throw loAppExp;
		}
		return lbUpdateStatus;
	}

	/**
	 * <ul>
	 * <li>This service class is invoked through getPaymentHeaderDetails
	 * transaction id for Payment Detail screen this method fetch the data for
	 * Advance payment header</li>
	 * <li>This query used: getAdvancePaymentHeaderDetails</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - SqlSession Object
	 * @param aoHashMap - HashMap
	 * @return loPaymentBean - PaymentBean
	 * @throws ApplicationException - ApplicationException object
	 */
	public PaymentBean fetchAdvancePaymentHeaderDetails(SqlSession aoMybatisSession, HashMap<String, String> aoHashMap)
			throws ApplicationException
	{
		PaymentBean loPaymentBean = null;

		try
		{
			// Fetching advance payment details
			loPaymentBean = (PaymentBean) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER, HHSConstants.ADVANCE_PAYMENT_HEADER_DETAILS,
					HHSConstants.JAVA_UTIL_HASH_MAP);

		}// Exception occur if database is down or getting null in required
			// parameter
		catch (ApplicationException aoAppExp)
		{
			setMoState("Error while fetching Advance Payment header details for payment id : "
					+ aoHashMap.get("paymentId"));
			aoAppExp.addContextData("Error while fetching  Advance Payment header details for payment id : "
					+ aoHashMap.get("paymentId"), aoAppExp);
			LOG_OBJECT.Error("Error while fetching  Advance Payment header details ", aoAppExp);
			throw aoAppExp;
		}
		return loPaymentBean;
	}

	/**
	 * <ul>
	 * <li>This method fetchAdvancePaymentLineDetails will get the Payment Line
	 * Details on the basis of invoiceId and paymentId Following steps are
	 * involved 1. Fetching FY budget amount and YTD invoice amount 2.Getting
	 * payment actual paid amount 3.Getting total advance amount 4.Getting total
	 * payment amount</li>
	 * <li>This query used: fetchPaymentFyBudgetSummary</li>
	 * <li>This query used: fetchPaymentFyBudgetActualPaid</li>
	 * <li>This query used: fetchTotalAdvanceAmount</li>
	 * <li>This query used: fetchTotalPaymentAmount</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - SqlSession Object
	 * @param aoHashMap - HashMap
	 * @return loPaymentBudgetDetails - BudgetDetails bean
	 * @throws ApplicationException - ApplicationException object
	 */
	public BudgetDetails fetchAdvancePaymentLineDetails(SqlSession aoMybatisSession, HashMap<String, String> aoHashMap)
			throws ApplicationException
	{
		BudgetDetails loPaymentBudgetDetails = null;

		try
		{
			// Fetching FY budget amount and YTD invoice amount
			loPaymentBudgetDetails = (BudgetDetails) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER, HHSConstants.PAYMENT_BUDGET_SUMMARY,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			if (loPaymentBudgetDetails == null)
			{
				return new BudgetDetails();
			}
			else
			{
				// Getting payment actual paid amount
				BigDecimal loActPaidAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
						HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER, HHSConstants.PAYMENT_ACTUAL_PAID,
						HHSConstants.JAVA_UTIL_HASH_MAP);

				loPaymentBudgetDetails.setYtdActualPaid(loActPaidAmount);
				loPaymentBudgetDetails.setRemainingAmount(loPaymentBudgetDetails.getApprovedBudget().subtract(
						loPaymentBudgetDetails.getYtdInvoicedAmount()));
				loPaymentBudgetDetails.setCashBalance(loPaymentBudgetDetails.getApprovedBudget().subtract(
						loActPaidAmount));

				// Getting total advance amount
				BigDecimal loTotalAdvanceAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
						HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER, HHSConstants.TOTAL_ADVANCE_AMOUNT,
						HHSConstants.JAVA_UTIL_HASH_MAP);

				loPaymentBudgetDetails.setAdvanceAmount(loTotalAdvanceAmount);

				// Getting total payment amount
				BigDecimal loTotalPaymentAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
						HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER, HHSConstants.TOTAL_PAYMENT_AMOUNT,
						HHSConstants.JAVA_UTIL_HASH_MAP);

				loPaymentBudgetDetails.setPaymentAmount(loTotalPaymentAmount);
			}

		}// Exception occur if database is down or getting null in required
			// parameter
		catch (ApplicationException aoAppExp)
		{
			setMoState("Error while fetching Advance Payment line details for payment id : "
					+ aoHashMap.get(HHSConstants.PAYMENT_ID_WORKFLOW));
			aoAppExp.addContextData(
					"Error while fetching Advance Payment line details for payment id : "
							+ aoHashMap.get(HHSConstants.PAYMENT_ID_WORKFLOW), aoAppExp);
			LOG_OBJECT.Error("Error while fetching Advance Payment line details ", aoAppExp);
			throw aoAppExp;
		}
		return loPaymentBudgetDetails;
	}

	/**
	 * <ul>
	 * <li>This method fetches list of Payment voucher number for advance
	 * payment review task Details</li>
	 * <li>This query used: getAdvancePaymentVoucherList</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - SqlSession Object
	 * @param aoHashMap - HashMap
	 * @return loPaymentVoucher - List<PaymentBean> list of PaymentBean
	 * @throws ApplicationException - ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public List<PaymentBean> fetchAdvancePaymentVoucherList(SqlSession aoMybatisSession,
			HashMap<String, String> aoHashMap) throws ApplicationException
	{
		List<PaymentBean> loPaymentVoucher = null;

		try
		{
			// Fetching advance payment voucher list
			loPaymentVoucher = (List<PaymentBean>) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER, HHSConstants.ADVANCE_PAYMENT_VOUCHER_DETAIL,
					HHSConstants.JAVA_UTIL_HASH_MAP);

		}// Exception occur if database is down or getting null in required
			// parameter
		catch (ApplicationException aoAppExp)
		{
			setMoState("Error while fetching Advance Payment voucher details for budget_advanced_id : "
					+ aoHashMap.get("budgetAdvanceId"));
			aoAppExp.addContextData("Error while fetching Advance Payment voucher details for budget_advanced_id : "
					+ aoHashMap.get("budgetAdvanceId"), aoAppExp);
			LOG_OBJECT.Error("Error while fetching Advance Payment voucher details for budget_advanced_id : "
					+ aoHashMap.get("budgetAdvanceId") + " Exception : " + aoAppExp);
			throw aoAppExp;
		}

		return loPaymentVoucher;
	}

	/**
	 * <ul>
	 * <li>This service class is invoked through
	 * fetchAdvancePaymentReviewDetails transaction id for Payment Review Task
	 * Detail screen</li>
	 * <li>This query used: getAdvancePaymentReviewHeaderDetails</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - SqlSession Object
	 * @param aoHashMap - HashMap object
	 * @return loPaymentBean - PaymentBean object
	 * @throws ApplicationException - ApplicationException object
	 */
	public PaymentBean fetchAdvancePaymentReviewDetails(SqlSession aoMybatisSession, HashMap<String, String> aoHashMap)
			throws ApplicationException
	{
		PaymentBean loPaymentBean = null;

		try
		{
			// Fetching advance payment details for budget advance id
			loPaymentBean = (PaymentBean) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER,
					HHSConstants.ADVANCE_PAYMENT_REVIEW_HEADER_DETAILS, HHSConstants.JAVA_UTIL_HASH_MAP);

		}// Exception occur if database is down or getting null in required
			// parameter
		catch (ApplicationException aoAppExp)
		{
			setMoState("Error while fetching Advance Payment Review header details for budget_advanced_id : "
					+ aoHashMap.get("budgetAdvanceId"));
			aoAppExp.addContextData(
					"Error while fetching Advance Payment Review header details for budget_advanced_id : "
							+ aoHashMap.get("budgetAdvanceId"), aoAppExp);
			LOG_OBJECT.Error("Error while fetching Advance Payment Review header details ", aoAppExp);
			throw aoAppExp;
		}

		return loPaymentBean;
	}

	/**
	 * This method validates that Total Assigned amount should not be greater
	 * than Budget Advance Amount
	 * 
	 * It returns a map with error_code and error_msg if validation fails.
	 * 
	 * <li>This query used: fetchAdvanceAndAssignment</li>
	 * 
	 * @param aoMyBatisSession : MyBatis Session
	 * @param aoTaskDetailsBean : taskDetailsBean object containing the required
	 *            information for processing
	 * @return loValidationResultMap : returns appropriate errorMessage and
	 *         errorCode
	 * @throws ApplicationException : applicationException object
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Map validateLevelOneAdvanceRequestFinishTask(SqlSession aoMybatisSession, TaskDetailsBean aoTaskDetailsBean)
			throws ApplicationException
	{
		Map loValidationResultMap = new HashMap<String, Object>();
		BigDecimal loAssignmentAmount = null;
		BigDecimal loAdvanceAmount = null;

		loValidationResultMap.put(HHSConstants.ERROR_CODE, HHSConstants.INT_ZERO);
		loValidationResultMap.put(HHSConstants.CLC_ERROR_MSG, HHSConstants.EMPTY_STRING);

		Map loAmountMap = (Map) DAOUtil.masterDAO(aoMybatisSession, aoTaskDetailsBean,
				HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER, HHSConstants.FETCH_ADVANCE_AND_ASSIGNMENT,
				HHSConstants.CS_TASK_DETAILS_BEAN);
		loAssignmentAmount = (BigDecimal) loAmountMap.get(HHSConstants.ASSIGNMENT_AMOUNT);
		loAdvanceAmount = (BigDecimal) loAmountMap.get(HHSConstants.ADVANCE_AMOUNT);
		if (loAssignmentAmount.compareTo(loAdvanceAmount) > 0)
		{
			loValidationResultMap.put(HHSConstants.ERROR_CODE, HHSConstants.INT_ONE);
			loValidationResultMap.put(HHSConstants.CLC_ERROR_MSG, PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.MSG_INVALID_ASSIGNMENT_AMOUNT));
		}
		return loValidationResultMap;
	}

	/**
	 * This method returns the advance payment review details.
	 * 
	 * <li>This query used: fetchAdvanceDescForTaskHeader</li>
	 * @return
	 * @throws ApplicationException
	 */
	public String fetchAdvanceDesc(SqlSession aoMybatisSession, TaskDetailsBean aoTaskDetailsBean)
			throws ApplicationException
	{
		String lsAdvanceDesc = null;

		try
		{
			lsAdvanceDesc = (String) DAOUtil.masterDAO(aoMybatisSession, aoTaskDetailsBean.getBudgetAdvanceId(),
					HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER, HHSConstants.PAY_FETCH_ADVANCE_DESC,
					HHSConstants.JAVA_LANG_STRING);

		}
		catch (ApplicationException aoAppExp)
		{
			setMoState("Error while fetching Advance Payment Review header details for budget_advanced_id : "
					+ aoTaskDetailsBean.getBudgetAdvanceId());
			aoAppExp.addContextData(
					"Error while fetching Advance Payment Review header details for budget_advanced_id : "
							+ aoTaskDetailsBean.getBudgetAdvanceId(), aoAppExp);
			LOG_OBJECT.Error("Error while fetching Advance Payment Review header details ", aoAppExp);
			throw aoAppExp;
		}
		return lsAdvanceDesc;

	}

}
