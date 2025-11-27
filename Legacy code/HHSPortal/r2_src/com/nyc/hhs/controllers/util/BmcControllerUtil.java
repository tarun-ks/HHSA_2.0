/**
 * This class has utility function for bmc controller
 */
package com.nyc.hhs.controllers.util;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletSession;
import javax.portlet.ResourceRequest;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.ContractBudgetBean;
import com.nyc.hhs.model.ProcurementCOF;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.util.HHSUtil;

/**
 * This utility class is having operations for business logic and transaction
 * for BmcController class.
 * 
 */
public class BmcControllerUtil
{

	/**
	 * Constant for Logging
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(BmcControllerUtil.class);

	/**
	 * 
	 * This method perform actions on database based on operation performed <li>
	 * The transaction used: fetchConfigurableYearBudgetAmount</li>
	 * @param aoChannelObj - Channel object
	 * @return lsBudgetAmount
	 * @throws ApplicationException object
	 */
	public static String fetchNewFYBudgetAmount(Channel aoChannelObj) throws ApplicationException
	{
		String lsBudgetAmount = null;
		try
		{
			HHSTransactionManager.executeTransaction(aoChannelObj, HHSConstants.TRN_CHK_IF_BUDGET_EXISTS);
			String lsBudgetId = (String) aoChannelObj.getData(HHSConstants.RSLT_BUDGET_ID);

			if (lsBudgetId != null && !lsBudgetId.isEmpty())
			{
				aoChannelObj.setData(HHSConstants.BUDGET_ID_KEY, lsBudgetId);
				HHSTransactionManager.executeTransaction(aoChannelObj, HHSConstants.TRN_CONFIG_SUB_BUDGET_AMOUNT);
				lsBudgetAmount = (((String) aoChannelObj.getData(HHSConstants.RSLT_FISCAL_YEAR_AMOUNT) == null) ? HHSConstants.STRING_ZERO
						: (String) aoChannelObj.getData(HHSConstants.RSLT_FISCAL_YEAR_AMOUNT));
			}
			else
			{
				lsBudgetAmount = HHSConstants.STRING_ZERO;
			}
		}
		// handling Application Exception if occur while performing actions on
		// database
		catch (ApplicationException aoAppEx)
		{

			LOG_OBJECT.Error("ApplicationException Occured while performing actions on database", aoAppEx);
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while performing actions on database", aoEx);
		}

		return lsBudgetAmount;

	}

	/**
	 * This method perform actions on database based on operation performed on
	 * Subgrid
	 * <ul>
	 * <li>If add operation perform on the grid insert row into database</li>
	 * <li>If delete operation perform on the grid delete row from database</li>
	 * <li>If edit operation perform on the grid update row into database</li>
	 * </ul>
	 * @param asOperation - Operation type
	 * @param asTransactionName - Transaction name
	 * @param aoChannelObj - Channel object
	 * @param aoBeanObj - ContractBudgetBean object
	 * @return lbTransactionStatus - Boolean
	 * @throws ApplicationException object
	 */
	public static Boolean executeGridTransactionForBudgetConfig(String asOperation, String asTransactionName,
			Channel aoChannelObj, ContractBudgetBean aoBeanObj) throws ApplicationException
	{
		Boolean lbTransactionStatus = false;
		try
		{
			if (asOperation != null && HHSConstants.OPERATION_ADD.equalsIgnoreCase(asOperation))
			{
				aoChannelObj.setData(HHSConstants.CB_GRID_BEAN_OBJ, aoBeanObj);
				HHSTransactionManager.executeTransaction(aoChannelObj, asTransactionName);
				lbTransactionStatus = true;
			}
			else if (asOperation != null && HHSConstants.OPERATION_EDIT.equalsIgnoreCase(asOperation))
			{
				aoChannelObj.setData(HHSConstants.CB_GRID_BEAN_OBJ, aoBeanObj);
				HHSTransactionManager.executeTransaction(aoChannelObj, asTransactionName);
				lbTransactionStatus = true;
			}
			else if (asOperation != null && HHSConstants.OPERATION_DELETE.equalsIgnoreCase(asOperation))
			{

				aoChannelObj.setData(HHSConstants.CB_GRID_BEAN_OBJ, aoBeanObj);
				HHSTransactionManager.executeTransaction(aoChannelObj, asTransactionName);
				lbTransactionStatus = true;
			}
		}
		// handling Application Exception if occur while performing actions on
		// database based
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT
					.Error("ApplicationException Occured while performing actions on database based on operation performed on subgrid"
							+ aoAppEx);
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT
					.Error("Exception Occured while performing actions on database based on operation performed on subgrid"
							+ aoEx);
		}
		return lbTransactionStatus;
	}

	/**
	 * This method perform actions on database based on operation performed on
	 * Subgrid
	 * <ul>
	 * <li>If add operation perform on the grid insert row into database</li>
	 * <li>If delete operation perform on the grid delete row from database</li>
	 * <li>If edit operation perform on the grid update row into database</li>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * @param asOperation - Operation type
	 * @param asTransactionName - Transaction Name
	 * @param aoChannelObj - Channel object
	 * @param aoBeanObj - ContractBudgetBean object
	 * @return lbTransactionStatus - Boolean
	 * @throws ApplicationException object
	 */
	public static Boolean executeGridTransactionForBudgetConfigUpdateTask(String asOperation, String asTransactionName,
			Channel aoChannelObj, ContractBudgetBean aoBeanObj) throws ApplicationException
	{
		Boolean lbTransactionStatus = false;
		try
		{
			if (asOperation != null && HHSConstants.OPERATION_ADD.equalsIgnoreCase(asOperation))
			{
				if (validateNewSubBuget(aoBeanObj))
				{
					aoChannelObj.setData(HHSConstants.CB_GRID_BEAN_OBJ, aoBeanObj);
					HHSTransactionManager.executeTransaction(aoChannelObj, asTransactionName);
					lbTransactionStatus = true;
				}
			}
			else if (asOperation != null && HHSConstants.OPERATION_DELETE.equalsIgnoreCase(asOperation))
			{

				aoChannelObj.setData(HHSConstants.CB_GRID_BEAN_OBJ, aoBeanObj);
				HHSTransactionManager.executeTransaction(aoChannelObj, asTransactionName);
				lbTransactionStatus = true;
			}
			else if (asOperation != null && HHSConstants.OPERATION_EDIT.equalsIgnoreCase(asOperation)
					&& validateNewSubBuget(aoBeanObj))
			{
				aoChannelObj.setData(HHSConstants.CB_GRID_BEAN_OBJ, aoBeanObj);
				HHSTransactionManager.executeTransaction(aoChannelObj, asTransactionName);
				lbTransactionStatus = true;
			}
		}
		// handling Application Exception if error occured while performing
		// actions on database based on operation performed on Subgrid
		catch (ApplicationException aoAppEx)
		{

			LOG_OBJECT
					.Error("ApplicationException Occured while performing actions on database based on operation performed on Subgrid"
							+ aoAppEx);
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT
					.Error("Exception Occured while performing actions on database based on operation performed on Subgrid"
							+ aoEx);
		}
		return lbTransactionStatus;
	}

	/**
	 * This method perform actions on database based on operation performed
	 * @param aoBeanObj - ContractBudgetBean object
	 * @return lbValid
	 * @throws ApplicationException object
	 */
	private static Boolean validateNewSubBuget(ContractBudgetBean aoBeanObj) throws ApplicationException
	{
		Boolean lbValid = true;
		try
		{
			if (aoBeanObj != null)
			{
				String lsBudgetName = aoBeanObj.getSubbudgetName();
				if (lsBudgetName == null || HHSConstants.EMPTY_STRING.equals(lsBudgetName))
				{
					lbValid = false;
				}

			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured while performing actions on database based on operation performed", aoEx);
			LOG_OBJECT
					.Error("Error Occured while performing actions on database based on operation performed", loAppEx);
			throw loAppEx;
		}
		return lbValid;
	}

	/**
	 * This method perform actions on database based on operation performed <li>
	 * The transaction used: fetchBudgetDetailsByFYAndContractId</li>
	 * @param aoChannelObj - Channel object
	 * @return loReturnedBudgetDetails - List
	 * @throws ApplicationException object
	 */
	@SuppressWarnings("rawtypes")
	public static List getBudgetDetails(Channel aoChannelObj) throws ApplicationException
	{
		List loReturnedBudgetDetails = null;
		try
		{
			HHSTransactionManager.executeTransaction(aoChannelObj, HHSConstants.CS_FETCH_BUDGET_DETAILS);
			loReturnedBudgetDetails = (List) aoChannelObj.getData(HHSConstants.AO_RETURNED_GRID_LIST);
		}
		// handling Application Exception if error occured while performing
		// actions on database based on operation performed
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT
					.Error("ApplicationException Occured while performing actions on database based on operation performed"
							+ aoAppEx);
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while performing actions on database based on operation performed",
					aoEx);
		}
		return loReturnedBudgetDetails;
	}

	/**
	 * This method perform actions on database based on operation performed <li>
	 * The transaction used: fetchBudgetDetailsActiveOrApproved</li>
	 * @param aoChannelObj - Channel object
	 * @return loReturnedBudgetDetails - List
	 * @throws ApplicationException object
	 */
	@SuppressWarnings("rawtypes")
	public static List<ContractBudgetBean> getActiveApprovedBudgetDetails(Channel aoChannelObj)
			throws ApplicationException
	{
		List<ContractBudgetBean> loReturnedBudgetDetails = new ArrayList();
		try
		{
			HHSTransactionManager
					.executeTransaction(aoChannelObj, HHSConstants.CS_FETCH_ACTIVE_APPROVED_BUDGET_DETAILS);
			loReturnedBudgetDetails = (List<ContractBudgetBean>) aoChannelObj
					.getData(HHSConstants.AO_RETURNED_GRID_LIST);
		}
		// handling Application Exception if error occured while performing
		// actions on database based on operation performed
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT
					.Error("ApplicationException Occured while performing actions on database based on operation performed"
							+ aoAppEx);
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while performing actions on database based on operation performed",
					aoEx);
		}
		return loReturnedBudgetDetails;
	}

	/**Release 3.14.0
	 * This method perform actions on database based on operation performed <li>
	 * The transaction used: fetchBudgetDetailsActiveOrApproved</li>
	 * @param aoChannelObj - Channel object
	 * @return loReturnedBudgetDetails - List
	 * @throws ApplicationException object
	 */
	@SuppressWarnings("rawtypes")
	public static List<ContractBudgetBean> getNextNewFYBudgetDetails(Channel aoChannelObj)
			throws ApplicationException
	{
		List<ContractBudgetBean> loReturnedBudgetDetails = new ArrayList();
		try
		{
			HHSTransactionManager
					.executeTransaction(aoChannelObj, HHSConstants.CS_FETCH_NEXT_NEW_FY_BUDGET_DETAILS);
			loReturnedBudgetDetails = (List<ContractBudgetBean>) aoChannelObj
					.getData(HHSConstants.AO_RETURNED_GRID_LIST);
		}
		// handling Application Exception if error occured while performing
		// actions on database based on operation performed
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT
					.Error("ApplicationException Occured while performing actions on database based on operation performed"
							+ aoAppEx);
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while performing actions on database based on operation performed",
					aoEx);
		}
		return loReturnedBudgetDetails;
	}

	/**Release 3.14.0
	 * This method perform actions on database based on operation performed <li>
	 * The transaction used: fetchBudgetDetailsActiveOrApproved</li>
	 * @param aoChannelObj - Channel object
	 * @return loReturnedBudgetDetails - List
	 * @throws ApplicationException object
	 */
	@SuppressWarnings("rawtypes")
	public static Integer getNextNewFYBudgetYear(Channel aoChannelObj)
			throws ApplicationException
	{
		Integer loNextNewFy = 0;
		try
		{
			HHSTransactionManager
					.executeTransaction(aoChannelObj, HHSConstants.CS_FETCH_LAST_CONFIGURED_FY_BUDGET_YEAR);
			loNextNewFy = (Integer) aoChannelObj
					.getData(HHSConstants.CS_FETCH_NEXT_NEW_FY);
			loNextNewFy = loNextNewFy+1;
		}
		// handling Application Exception if error occured while performing
		// actions on database based on operation performed
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT
					.Error("ApplicationException Occured while performing actions on database based on operation performed"
							+ aoAppEx);
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while performing actions on database based on operation performed",
					aoEx);
		}
		return loNextNewFy;
	}
	/**
	 * This method perform actions on database based on operation performed <li>
	 * The transaction used: fetchAmendmentBudgetDetails</li>
	 * @param aoChannelObj - Channel object
	 * @return loReturnedBudgetDetails - List
	 * @throws ApplicationException object
	 */
	@SuppressWarnings("rawtypes")
	public static List getAmendmentBudgetDetails(Channel aoChannelObj) throws ApplicationException
	{
		List loReturnedBudgetDetails = null;
		try
		{
			HHSTransactionManager.executeTransaction(aoChannelObj, HHSConstants.CS_FETCH_AMENDMENT_BUDGET_DETAILS);
			loReturnedBudgetDetails = (List) aoChannelObj.getData(HHSConstants.AO_RETURNED_GRID_LIST);
		}
		// handling Application Exception if error occured while performing
		// actions on database based on operation performed
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT
					.Error("ApplicationException Occured while performing actions on database based on operation performed"
							+ aoAppEx);
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while performing actions on database based on operation performed",
					aoEx);
		}
		return loReturnedBudgetDetails;
	}

	/**
	 * This method perform actions on database based on operation performed <li>
	 * The transaction used: fetchFYAndContractId</li>
	 * @param aoChannelObj - Channel object
	 * @return loFYIList List
	 * @throws ApplicationException object
	 */
	@SuppressWarnings("rawtypes")
	public static List getFYList(Channel aoChannelObj) throws ApplicationException
	{
		List loFYIList = null;
		try
		{
			HHSTransactionManager.executeTransaction(aoChannelObj, HHSConstants.CS_FETCH_FY_AND_CONTRACT_ID);
			loFYIList = (List) aoChannelObj.getData(HHSConstants.BMC_RETURNED_FYI_LIST_PARAM);
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured while performing actions on database based on operation performed", aoEx);
			LOG_OBJECT
					.Error("Error Occured while performing actions on database based on operation performed", loAppEx);
			throw loAppEx;
		}
		return loFYIList;
	}

	/**
	 * This method perform actions on database based on operation performed <li>
	 * The transaction used: fetchFYPlannedAmount</li>
	 * @param aoChannelObj - Channel object
	 * @param lsAmendment - Amendment
	 * @return lsFYBudgetPlannedAmount String
	 * @throws ApplicationException object
	 */
	public static String getFYBudgetPlannedAmount(Channel aoChannelObj, String lsAmendment) throws ApplicationException
	{
		String lsFYBudgetPlannedAmount = null;
		try
		{
			if (null != lsAmendment && lsAmendment.equalsIgnoreCase(HHSConstants.TRUE))
			{
				HHSTransactionManager.executeTransaction(aoChannelObj,
						HHSConstants.CS_FETCH_AMENDMENT_FY_PLANNED_AMOUNT);
				lsFYBudgetPlannedAmount = (String) aoChannelObj.getData(HHSConstants.BMC_FY_PLANNED_AMOUNT);
			}
			else
			{
				HHSTransactionManager.executeTransaction(aoChannelObj, HHSConstants.CS_FETCH_FY_PLANNED_AMOUNT);
				lsFYBudgetPlannedAmount = (String) aoChannelObj.getData(HHSConstants.BMC_FY_PLANNED_AMOUNT);
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured while performing actions on database based on operation performed", aoEx);
			LOG_OBJECT
					.Error("Error Occured while performing actions on database based on operation performed", loAppEx);
			throw loAppEx;
		}
		return lsFYBudgetPlannedAmount;
	}

	/**
	 * This method perform actions on database based on operation performed on
	 * Sub-grid
	 * <ul>
	 * <li>If add operation perform on the grid insert row into database</li>
	 * <li>If delete operation perform on the grid delete row from database</li>
	 * <li>If edit operation perform on the grid update row into database</li>
	 * <li>The transaction used: fetchFYPlannedAmount</li>
	 * </ul>
	 * @param asOperation - Operation type
	 * @param asTransactionName - Transaction Name
	 * @param aoChannelObj - Channel object
	 * @param aoBeanObj - ContractBudgetBean object
	 * @param asAmendment Amendment
	 * @return lbTransactionStatus - Boolean
	 * @throws ApplicationException object
	 */
	public static Boolean executeGridTransactionForBudgetConfigUpdate(String asOperation, String asTransactionName,
			Channel aoChannelObj, ContractBudgetBean aoBeanObj, String asAmendment) throws ApplicationException
	{
		Boolean loTransactionStatus = false;
		if (asOperation != null && HHSConstants.OPERATION_ADD.equalsIgnoreCase(asOperation))
		{
			aoChannelObj.setData(HHSConstants.CB_GRID_BEAN_OBJ, aoBeanObj);
			HHSTransactionManager.executeTransaction(aoChannelObj, asTransactionName);
			loTransactionStatus = true;

		}
		else if (asOperation != null && HHSConstants.OPERATION_EDIT.equalsIgnoreCase(asOperation))
		{

			aoChannelObj.setData(HHSConstants.CB_GRID_BEAN_OBJ, aoBeanObj);
			aoChannelObj.setData(HHSConstants.AS_AMENDMENT, asAmendment);
			HHSTransactionManager.executeTransaction(aoChannelObj, asTransactionName);
			loTransactionStatus = true;

		}
		else if (asOperation != null && HHSConstants.OPERATION_DELETE.equalsIgnoreCase(asOperation))
		{

			aoChannelObj.setData(HHSConstants.CB_GRID_BEAN_OBJ, aoBeanObj);
			HHSTransactionManager.executeTransaction(aoChannelObj, asTransactionName);
			loTransactionStatus = true;
		}

		return loTransactionStatus;
	}

	/**
	 * This method perform actions on database based on operation performed <li>
	 * The transaction used: fetchPlannedAmtForUpdatedContractId</li>
	 * @param aoChannelObj - Channel object
	 * @return lsFYBudgetPlannedAmount String
	 * @throws ApplicationException object
	 */
	public static String getFYBudgetPlannedForUpdatedContractId(Channel aoChannelObj) throws ApplicationException
	{
		String lsFYBudgetPlannedAmount = null;
		try
		{
			HHSTransactionManager
					.executeTransaction(aoChannelObj, HHSConstants.BMC_FETCH_FY_PLANNED_AMOUNT_FOR_UPDATED);
			lsFYBudgetPlannedAmount = (String) aoChannelObj.getData(HHSConstants.BMC_FY_PLANNED_AMOUNT);
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While performing actions on database based on operation performed", aoEx);
			LOG_OBJECT
					.Error("Error Occured While performing actions on database based on operation performed", loAppEx);
			throw loAppEx;
		}
		return lsFYBudgetPlannedAmount;
	}

	/**
	 * This method perform actions on database based on operation performed <li>
	 * The transaction used: checkBudgetDetails</li>
	 * @param aoChannelObj - Channel object
	 * @return lsupdatedBudgetId String
	 * @throws ApplicationException object
	 */
	public static String checkBudgetDetails(Channel aoChannelObj) throws ApplicationException
	{
		String lsUpdatedBudgetId = null;
		try
		{
			HHSTransactionManager.executeTransaction(aoChannelObj, HHSConstants.CS_CHECK_BUDGET_DETAILS);
			lsUpdatedBudgetId = (String) aoChannelObj.getData(HHSConstants.UPDATED_BUDGET_ID);
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While performing actions on database based on operation performed", aoEx);
			LOG_OBJECT
					.Error("Error Occured While performing actions on database based on operation performed", loAppEx);
			throw loAppEx;
		}
		return lsUpdatedBudgetId;
	}

	/**
	 * This method is used to add new budget
	 * <ul>
	 * <li>The transaction used: insertNewBudgetDetails</li>
	 * </ul>
	 * @param aoResourceRequest - ResourceRequest object
	 * @param aoContractBudgetBean - ContractBudgetBean object
	 * @param aoChannelObj - Channel object
	 * @param aoCBGridBean - CBGridBean object
	 * @param aoPprocureCof - PprocureCof object
	 * @param aiConfigurationFiscalYear - int type value
	 * @param asFYBudgetPlannedAmount - String type object
	 * @throws ApplicationException - ApplicationException object
	 */
	public static void addNewBudget(ResourceRequest aoResourceRequest, ContractBudgetBean aoContractBudgetBean,
			Channel aoChannelObj, CBGridBean aoCBGridBean, ProcurementCOF aoPprocureCof,
			Integer aiConfigurationFiscalYear, String asFYBudgetPlannedAmount) throws ApplicationException
	{
		try
		{
			aoContractBudgetBean.setBudgetfiscalYear(aiConfigurationFiscalYear.toString());
			aoContractBudgetBean.setBudgetStartDate(HHSUtil.getNewBudgetStartDate(aoPprocureCof.getContractStartDate(),
					aiConfigurationFiscalYear.toString()));
			aoContractBudgetBean.setBudgetEndDate(HHSUtil.getNewBudgetEndDate(aoPprocureCof.getContractEndDate(),
					aiConfigurationFiscalYear.toString()));
			aoContractBudgetBean.setBudgetTypeId(Integer.parseInt(HHSConstants.TWO));
			aoContractBudgetBean.setContractId(aoCBGridBean.getContractID());
			String lsUserId = String.valueOf(aoResourceRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE));
			aoContractBudgetBean.setCreatedByUserId(lsUserId);
			aoContractBudgetBean.setModifiedByUserId(lsUserId);
			aoContractBudgetBean.setTotalbudgetAmount(asFYBudgetPlannedAmount);
			aoContractBudgetBean.setPlannedAmount(asFYBudgetPlannedAmount);
			aoContractBudgetBean.setStatusId(HHSConstants.BUDGET_PENDING_CONFIGURATION_STATUS_ID);
			aoChannelObj.setData(HHSConstants.AO_CONTRACT_BUDGET_BEAN, aoContractBudgetBean);
			HHSTransactionManager.executeTransaction(aoChannelObj, HHSConstants.BMC_INSERT_NEW_BUDGET_DETAILS);
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error Occured While fetching input parameters",
					aoEx);
			LOG_OBJECT.Error("Error Occured While fetching input parameters", loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * This method is used to add new amendment budget.
	 * <ul>
	 * <li>This method was updated in R4</li>
	 * <li>The transaction used:insertNewAmendmentBudgetDetails</li>
	 * </ul>
	 * @param aoResourceRequest - ResourceRequest object
	 * @param aoContractBudgetBean - ContractBudgetBean object
	 * @param aoChannelObj - Channel object
	 * @param aoCBGridBean - CBGridBean object
	 * @param aoPprocureCof - PprocureCof object
	 * @param aiConfigurationFiscalYear - int type value
	 * @param asFYBudgetPlannedAmount - String type object
	 * @throws ApplicationException - ApplicationException object
	 */
	public static void addNewAmendmentBudget(ResourceRequest aoResourceRequest,
			ContractBudgetBean aoContractBudgetBean, Channel aoChannelObj, CBGridBean aoCBGridBean,
			ProcurementCOF aoPprocureCof, Integer aiConfigurationFiscalYear, String asFYBudgetPlannedAmount)
			throws ApplicationException
	{
		try
		{
			aoContractBudgetBean.setBudgetfiscalYear(aiConfigurationFiscalYear.toString());
			aoContractBudgetBean.setBudgetStartDate(HHSUtil.getNewBudgetStartDate(aoPprocureCof.getContractStartDate(),
					aoContractBudgetBean.getBudgetfiscalYear()));
			aoContractBudgetBean.setBudgetEndDate(HHSUtil.getNewBudgetEndDate(aoPprocureCof.getContractEndDate(),
					aoContractBudgetBean.getBudgetfiscalYear()));
			aoContractBudgetBean.setBudgetTypeId(Integer.parseInt(HHSConstants.ONE));
			aoContractBudgetBean.setContractId(aoCBGridBean.getContractID());
			aoContractBudgetBean.setAmendmentContractId(aoCBGridBean.getAmendmentContractID());
			aoContractBudgetBean.setContractTypeId(HHSConstants.TWO);
			String lsUserId = String.valueOf(aoResourceRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE));
			aoContractBudgetBean.setCreatedByUserId(lsUserId);
			aoContractBudgetBean.setModifiedByUserId(lsUserId);
			aoContractBudgetBean.setTotalbudgetAmount(asFYBudgetPlannedAmount);
			aoContractBudgetBean.setPlannedAmount(asFYBudgetPlannedAmount);
			aoContractBudgetBean.setStatusId(HHSConstants.BUDGET_PENDING_CONFIGURATION_STATUS_ID);
			aoChannelObj.setData(HHSConstants.AO_CONTRACT_BUDGET_BEAN, aoContractBudgetBean);
			HHSTransactionManager
					.executeTransaction(aoChannelObj, HHSConstants.BMC_INSERT_NEW_AMENDMENT_BUDGET_DETAILS);
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error Occured While fetching input parameters",
					aoEx);
			LOG_OBJECT.Error("Error Occured While fetching input parameters", loAppEx);
			throw loAppEx;
		}
	}
	
	/**
	 * This method is added for Release 3.8.0 #6483
	 * This method sets the discrepancy flag to true in taskdetails bean if there is discrepency in base budget when contract is being registered.
	 * @param aoTaskDetailsBean TaskDetailBean object
	 * @param asContractId String containing contract id
	 * @throws ApplicationException ApplicationException object
	 */
	public static void fetchDiscrepencyDetailsForUpdateTask(TaskDetailsBean aoTaskDetailsBean, String asContractId) throws ApplicationException
	{
		try
		{
			Channel loChannel = new Channel();
			loChannel.setData(HHSConstants.AO_TASK_DETAILS_BEAN, aoTaskDetailsBean);
			loChannel.setData(HHSConstants.CONTRACT_ID_KEY, asContractId);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_DISCREPENCY_DETAILS_UPDATES_TASK);
			String lsContractId = (String) loChannel.getData(HHSConstants.AS_SELECTED_CONTRACT_ID);
			if (null != lsContractId)
			{
				aoTaskDetailsBean.setDiscFlagForUpdate(true);
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error Occured While fetching discrepency details", aoEx);
			LOG_OBJECT.Error("Error Occured While fetching discrepency details", loAppEx);
			throw loAppEx;
		}
	}
}