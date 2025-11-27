package com.nyc.hhs.controllers.util;

import java.util.HashMap;
import java.util.Map;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.util.PropertyLoader;

/**
 * <p>
 * This util class will be used to for ContractBudgetController. All decision
 * making or control flow is executed here.
 * </p>
 * 
 */

public class ContractBudgetControllerUtils
{

	/**
	 * Constant for Logging
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(ContractBudgetControllerUtils.class);

	/**
	 * <ul>
	 * To fetch the jsp Path
	 * </ul>
	 * Modified for Release 3.4.0, #5681
	 * @param asPrintRender Parameter to set the jsp path for CONTRACT BUDGET
	 *            LANDING PRINT
	 * @param asActionReqParam Parameter to set the jsp path for CONTRACT BUDGET
	 *            REVIEW TASK
	 * @return lsJspPath Jsp path returned on the basis of parameter sent.
	 * @throws ApplicationException Application Exception thrown.
	 */
	public static String getlsJspPath(String asPrintRender, String asActionReqParam) throws ApplicationException
	{
		String lsJspPath;
		try
		{
			if (null != asPrintRender && asPrintRender.equalsIgnoreCase(HHSConstants.PRINTER_VIEW))
			{
				lsJspPath = HHSConstants.JSP_CONTRACTBUDGET_CONTRACT_BUDGET_LANDING_PRINT;
			}
			//Added check for Release 3.4.0, #5681 - Starts
			else if(null != asPrintRender && asPrintRender.equalsIgnoreCase(HHSConstants.PRINTER_VIEW_BUDGET))
			{
				lsJspPath = HHSConstants.JSP_CONTRACT_BUDGET_PRINT;
			}
			//Added check for Release 3.4.0, #5681 - Ends
			//Added for R6: return Payment review task
			else if(null != asActionReqParam && asActionReqParam.equalsIgnoreCase(HHSConstants.TASK_RETURN_PAYMENT_REVIEW))
			{
				lsJspPath = HHSConstants.JSP_RETURN_PAYMENT_REVIEW_TASK;
			}
			//Added for R6: return Payment review task end
			else if (null != asActionReqParam && asActionReqParam.equalsIgnoreCase(HHSConstants.TASK_BUDGET_REVIEW))
			{
				lsJspPath = HHSConstants.JSP_CONTRACTBUDGET_CONTRACT_BUDGET_REVIEW_TASK;
			}
			else
			{
				lsJspPath = HHSConstants.JSP_CONTRACTBUDGET_CONTRACT_BUDGET_LANDING;
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error Occured While setting "
					+ "jsp path for CONTRACT BUDGET LANDING PRINT", aoEx);
			LOG_OBJECT.Error("Error Occured While setting " + "jsp path for CONTRACT BUDGET LANDING PRINT", loAppEx);
			throw loAppEx;
		}
		return lsJspPath;
	}

	/**
	 * This method write to validate the budgetStatus
	 * <ul>
	 * <li>1. If Budget status is Canceled then add Error Message</li>
	 * <li>2. If Budget status is Closed then add Error Message</li>
	 * <li>3. If Budget status is Suspended add Error Message</li>
	 * <li>4. Other wise add Success Message</li>
	 * </ul>
	 * @param asbudgetID Budget id passed to be validated.
	 * @return loMap Map returned with the error message set.
	 * @throws ApplicationException Application Exception thrown.
	 */
	public static Map<String, String> validateBudgetStatus(String asbudgetID) throws ApplicationException
	{
		Map<String, String> loMap = null;
		try
		{
			loMap = new HashMap<String, String>();

			String lsBudgetStatus = fetchCurrentBudgetStatus(asbudgetID);

			if (lsBudgetStatus.equalsIgnoreCase(fetchErrorMsg(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_BUDGET_CANCELLED)))
			{
				loMap.put(HHSConstants.ERROR_MESSAGE,
						fetchErrorMsg(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.CB_CANCELLED));
			}
			else if (lsBudgetStatus.equalsIgnoreCase(fetchErrorMsg(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_BUDGET_CLOSED)))
			{
				loMap.put(HHSConstants.ERROR_MESSAGE,
						fetchErrorMsg(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.CB_CLOSED));
			}
			else if (lsBudgetStatus.equalsIgnoreCase(fetchErrorMsg(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_BUDGET_SUSPENDED)))
			{
				loMap.put(HHSConstants.ERROR_MESSAGE,
						fetchErrorMsg(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.CB_SUSPENDED));
			}
			else
			{
				// successMessage
				loMap.put(HHSConstants.SUCCESS_MESSAGE,
						fetchErrorMsg(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.CB_SAVED));
			}
		}
		// handling Application Exception if error occur while validating the
		// budgetStatus
		catch (ApplicationException aoAppEx)
		{

			LOG_OBJECT.Error("ApplicationException Occured while validating the budgetStatus", aoAppEx);
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while validating the budgetStatus", aoEx);
			//Made changes for enhancement id 6000 release 3.8.0.
			throw new ApplicationException(HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED, aoEx);
		}

		return loMap;

	}
	
	
	/**This method is used to validate that there are required number
	 * <ul>
	 * <li>Created this method for release 3.6.0 Enhancement id 6484.</li>
	 * <li>Check is for fiscal year on or after 2016.</li>
	 * </ul>
	 * of sub budget site added for a particular budget.
	 * @param asbudgetID budget id as input.
	 * @param loMap Map as input.
	 * @param asFiscalYearId fiscal year id as input.
	 * @return Map Map as output with error message set in it.
	 * @throws ApplicationException Application Exception thrown.
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> validateSubBudgetSite(String asbudgetID, Map<String, String> loMap,String asFiscalYearId) throws ApplicationException
	{
		try
		{
				HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb
				.getInstance().getCacheObject(ApplicationConstants.APPLICATION_SETTING);
				if(asFiscalYearId!=null && loApplicationSettingMap.get(HHSConstants.FISCAL_YEAR_ID_ADD_SITE_KEY)!=null)
				{
					Integer liFiscalYearIdAddSite = Integer.parseInt(loApplicationSettingMap.get(HHSConstants.FISCAL_YEAR_ID_ADD_SITE_KEY));
					Integer liFiscalYearId = Integer.parseInt(asFiscalYearId);
					//Only for budget on or after 2016
					if(liFiscalYearIdAddSite!=null && liFiscalYearId!=null && liFiscalYearId >= liFiscalYearIdAddSite)
					{
						Channel loChannel = new Channel();
						loChannel.setData(HHSConstants.BUDGET_ID, asbudgetID);
			
						HHSTransactionManager.executeTransaction(loChannel, HHSConstants.SUB_BUDGET_SITE_COUNT);
			
						Integer loSubBudgetSiteCount = (Integer) loChannel.getData(HHSConstants.SUB_BUDGET_SITE_COUNT);
						if (loSubBudgetSiteCount!=null && loSubBudgetSiteCount > 0)
						{
							loMap.put(HHSConstants.ERROR_MESSAGE,
									fetchErrorMsg(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.SUB_BUDGET_SITE_REQUIRED));
							loMap.remove(HHSConstants.SUCCESS_MESSAGE);
						}
					}
				}
		}
		// handling Application Exception if error occur while validating the
		// sub budget site
		catch (ApplicationException aoAppEx)
		{

			LOG_OBJECT.Error("ApplicationException Occured while validating the sub budget site", aoAppEx);
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while validating the sub budget site", aoEx);
		}

		return loMap;

	}

	/**
	 * This method is used to fetch the Error Message
	 * 
	 * @param aoProperty Property attribute from where constant has to be
	 *            fetched.
	 * @param aoConstants Constant to be fetched.
	 * @return lsErrorMsg Error message set on the basis of property and
	 *         constant set.
	 * @throws ApplicationException Application Exception thrown.
	 */
	public static String fetchErrorMsg(String aoProperty, String aoConstants) throws ApplicationException
	{
		String lsErrorMsg = HHSConstants.EMPTY_STRING;
		try
		{
			lsErrorMsg = PropertyLoader.getProperty(aoProperty, aoConstants);
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error Occured While fetching the Error Message",
					aoEx);
			LOG_OBJECT.Error("Error Occured While fetching the Error Message", loAppEx);
			throw loAppEx;
		}
		return lsErrorMsg;
	}

	/**
	 * This method is used to fetch the current Budget Status
	 * 
	 * @param asbudgetID Budget Id on the basis of which budget status is
	 *            fetched.
	 * @return lsBudgetStatus fetched status on the basis of Budget Id.
	 * @throws ApplicationException Application Exception thrown.
	 */
	public static String fetchCurrentBudgetStatus(String asbudgetID) throws ApplicationException
	{
		String lsBudgetStatus = null;
		try
		{
			Channel loChannel = new Channel();
			loChannel.setData(HHSConstants.BUDGET_ID, asbudgetID);

			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_CURRENT_CB_STATUS);

			lsBudgetStatus = (String) loChannel.getData(HHSConstants.BUDGET_STATUS);
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error Occured While fetching the current Budget Status:-", aoEx);
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While fetching the current Budget Status:-" + aoEx);
			throw loAppEx;
		}
		return lsBudgetStatus;
	}

	/**
	 * This method is used to fetch the current Budget Status
	 * 
	 * @param asBudgetStatusId Status Id to be validated from status properties.
	 * @return Returns true or false on the basis of budget StatusId sent.
	 * @throws ApplicationException Application Exception thrown.
	 */
	public static String getbudgetStatus(String asBudgetStatusId) throws ApplicationException
	{
		try
		{
			if ((asBudgetStatusId.equalsIgnoreCase(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.BUDGET_RETURNED_FOR_REVISION)))
					|| asBudgetStatusId.equalsIgnoreCase(PropertyLoader.getProperty(
							HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.BUDGET_PENDING_SUBMISSION)))
			{
				return HHSConstants.FALSE;
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While fetching the current Budget Status", aoEx);
			LOG_OBJECT.Error("Error Occured While fetching the current Budget Status", loAppEx);
			throw loAppEx;
		}
		return HHSConstants.TRUE;
	}

	/**
	 * This method write to validate the budgetStatus for suspended
	 * <ul>
	 * <li>1. If Budget status is Suspended add Error Message</li>
	 * <li>2. Other wise add Success Message</li>
	 * </ul>
	 * @param asbudgetID Budget id passed to be validated.
	 * @return loMap Map returned with the error message set.
	 * @throws ApplicationException Application Exception thrown.
	 */
	public static Map<String, String> validateBudgetStatusForSuspended(String asbudgetID) throws ApplicationException
	{
		Map<String, String> loMap = null;
		try
		{
			loMap = new HashMap<String, String>();

			String lsBudgetStatus = fetchCurrentBudgetStatus(asbudgetID);

			if (lsBudgetStatus.equalsIgnoreCase(fetchErrorMsg(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_BUDGET_SUSPENDED)))
			{
				loMap.put(HHSConstants.ERROR_MESSAGE,
						fetchErrorMsg(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.CB_SUSPENDED));
			}
			else
			{
				// successMessage
				loMap.put(HHSConstants.SUCCESS_MESSAGE,
						fetchErrorMsg(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.CB_SAVED));
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While validating the budgetStatus for suspended", aoEx);
			LOG_OBJECT.Error("Error Occured While validating the budgetStatus for suspended", loAppEx);
			throw loAppEx;
		}
		return loMap;
	}
	/**
	 * This method is used to fetch the current Budget Status
	 * 
	 * @param asbudgetID Budget Id on the basis of which budget status is
	 *            fetched.
	 * @return lsBudgetStatus fetched status on the basis of Budget Id.
	 * @throws ApplicationException Application Exception thrown.
	 */
	public static String fetchCurrentReturnedPaymentStatus(String asReturnedPaymentID) throws ApplicationException
	{
		String lsReturnedPaymentStatus = null;
		try
		{
			Channel loChannel = new Channel();
			loChannel.setData(HHSConstants.RETURN_PAYMENT_DETAIL_ID, asReturnedPaymentID);

			HHSTransactionManager.executeTransaction(loChannel, "fetchCurrentReturnedPaymentStatus",HHSR5Constants.TRANSACTION_ELEMENT_R5);

			lsReturnedPaymentStatus = (String) loChannel.getData(HHSConstants.RETURN_STATUS);
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error Occured While fetching the current Budget Status:-", aoEx);
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While fetching the current Budget Status:-" + aoEx);
			throw loAppEx;
		}
		return lsReturnedPaymentStatus;
	}
}
