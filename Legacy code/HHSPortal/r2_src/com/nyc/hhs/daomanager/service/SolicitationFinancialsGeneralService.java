package com.nyc.hhs.daomanager.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.AuthenticationBean;
import com.nyc.hhs.model.FinancialSummaryBean;
import com.nyc.hhs.model.MasterStatusBean;
import com.nyc.hhs.model.ProcurementSummaryBean;
import com.nyc.hhs.model.QueueItemsDetailsBean;
import com.nyc.hhs.service.webservices.AccountRequestLoginStub;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This service class will get the method calls from controller through
 * transaction layer. Execute queries by calling mapper and return query output
 * back to controller. If any error exists, wrap the exception into Application
 * Exception and throw it to controller.
 */
public class SolicitationFinancialsGeneralService extends ServiceState
{
	/**
	 * This is a log object used to log any exception into log file.
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(SolicitationFinancialsGeneralService.class);

	/**
	 * This method is used to fetch Procurement Counts for accelerator and
	 * agency home page
	 * 
	 * <ul>
	 * <li>Execute select query with Id "fetchProcurementPortletCount" from
	 * common mapper</li>
	 * <li>Fetch count of RFPs scheduled to be released within 10 days</li>
	 * <li>Fetch count of RFPs scheduled to be released within 60 days</li>
	 * <li>Fetch count of RFPs in released status</li>
	 * <li>Fetch count of RFPs with proposal due dates within 10 days</li>
	 * <li>Fetch count of RFPs with proposals received</li>
	 * <li>Fetch count of RFPs with evaluations complete</li>
	 * <li>Fetch count of RFPs with selections made and submitted to accelerator
	 * </li>
	 * <li>Return bean object with all procurement counts</li>
	 * 
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession object
	 * @param asUserOrg a string value of user org
	 * @return a procurement summary bean object
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public ProcurementSummaryBean fetchProcurementCountForAccHomePage(SqlSession aoMybatisSession, String asUserOrg)
			throws ApplicationException
	{
		ProcurementSummaryBean loProcurementSummaryBean = null;
		try
		{
			HashMap<String, String> loProcCountMap = new HashMap<String, String>();
			loProcCountMap.put(HHSConstants.USER_ORG, asUserOrg);
			loProcurementSummaryBean = (ProcurementSummaryBean) DAOUtil.masterDAO(aoMybatisSession, loProcCountMap,
					HHSConstants.MAPPER_CLASS_COMMON_MAPPER, HHSConstants.FETCH_ACC_PROCUREMENT_PORTLET_COUNT,
					HHSConstants.JAVA_UTIL_HASH_MAP);

			setMoState("Procurement Homepage Counts fetched successfully");
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			LOG_OBJECT
					.Error("Exception occured while fetching Procurement HomePage Counts in SolicitationFinancialsGeneralService ",
							loExp);
			setMoState("Error occurred while fetching Procurement Homepage Counts");
			throw loExp;
		}
		return loProcurementSummaryBean;
	}

	/**
	 * This method will get the procurement count and financial counts for
	 * provider home page
	 * <ul>
	 * <li>Execute Select query with ID <b>fetchProcurementCountForProv</b></li>
	 * <li>Return bean object will all procurement and financial count</li>
	 * </ul>
	 * @param aoMybatisSession SQL Session Object
	 * @param asOrgId a string value of organization ID
	 * @return Procurement summary bean
	 * @throws ApplicationException throws application Exception
	 */
	public ProcurementSummaryBean fetchProcurementCountForProvHomePage(SqlSession aoMybatisSession, String asOrgId)
			throws ApplicationException
	{
		ProcurementSummaryBean loProcurementSummaryBean = null;
		try
		{
			Map<String, String> loDataMap = new HashMap<String, String>();
			loDataMap.put(HHSConstants.ORG_ID, asOrgId);
			loDataMap.put(HHSConstants.STATUS_PROCUREMENT_PLANNED, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_PLANNED));
			loDataMap.put(HHSConstants.STATUS_PROVIDER_ELIGIBLE_TO_PROPOSE, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROVIDER_ELIGIBLE_TO_PROPOSE));
			loDataMap.put(HHSConstants.STATUS_PROCUREMENT_RELEASED, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_RELEASED));
			loDataMap.put(HHSConstants.STATUS_PROVIDER_DRAFT, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROVIDER_DRAFT));
			loDataMap.put(HHSConstants.STATUS_PROVIDER_SUBMITTED_PROPOSAL, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROVIDER_SUBMITTED_PROPOSAL));
			loDataMap.put(HHSConstants.STATUS_PROVIDER_SELECTED, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROVIDER_SELECTED));
			loDataMap.put(HHSConstants.STATUS_PROCUREMENT_CLOSED, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_CLOSED));
			loDataMap.put(HHSConstants.STATUS_PROCUREMENT_CANCELLED, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_CANCELLED));
			// Start : R5 Added
			loDataMap.put(HHSConstants.STATUS_PROPOSAL_RETURNED_FOR_REVISION, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_RETURNED_FOR_REVISION));
			// End : R5 Added
			loProcurementSummaryBean = (ProcurementSummaryBean) DAOUtil.masterDAO(aoMybatisSession, loDataMap,
					HHSConstants.MAPPER_CLASS_COMMON_MAPPER, HHSConstants.FETCH_PROCUREMENT_COUNT_PROV_HOME_PAGE,
					HHSConstants.JAVA_UTIL_MAP);

			setMoState("Procurement Homepage Counts fetched successfully");
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			LOG_OBJECT
					.Error("Exception occured while fetching Procurement HomePage Counts in SolicitationFinancialsGeneralService ",
							loExp);
			setMoState("Error occurred while fetching Procurement Homepage Counts");
			throw loExp;
		}
		return loProcurementSummaryBean;
	}

	/**
	 * This method will fetch financials home page count for accelerator users
	 * 
	 * <ul>
	 * <li>Execute select query with id "fetchAccFinancialsPortletCount" from
	 * common mapper</li>
	 * <li>Fetch count of Contracts Pending Configuration</li>
	 * <li>Fetch count of Contracts Pending Certification of Funds</li>
	 * <li>Fetch count of Contracts Pending Registration</li>
	 * <li>Fetch count of Budgets and Amendment Budgets pending Approval</li>
	 * <li>Fetch count of Budget Modifications Pending Approval</li>
	 * <li>Fetch count of Invoices Pending Approval</li>
	 * <li>Fetch count of Payments Pending Approval</li>
	 * <li>Fetch count of Payments with FMS error</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Mybatis Session
	 * @param asUserOrg a string value of user org
	 * @return a financial bean object
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public FinancialSummaryBean fetchAccFinancialsPortletCount(SqlSession aoMybatisSession, String asUserOrg)
			throws ApplicationException
	{
		FinancialSummaryBean loFinancialSummaryBean = null;
		try
		{
			HashMap<String, String> loFincCountMap = new HashMap<String, String>();
			loFincCountMap.put(HHSConstants.USER_ORG, asUserOrg);
			loFincCountMap.put(HHSConstants.STATUS_CONTRACT_PENDING_CONFIGURATION, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_CONTRACT_PENDING_CONFIGURATION));
			loFincCountMap.put(HHSConstants.STATUS_CONTRACT_PENDING_COF, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_CONTRACT_PENDING_COF));
			loFincCountMap.put(HHSConstants.STATUS_CONTRACT_PENDING_REGISTARTION, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_CONTRACT_PENDING_REGISTARTION));
			loFincCountMap.put(HHSConstants.STATUS_BUDGET_PENDING_APPROVAL, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_BUDGET_PENDING_APPROVAL));
			loFincCountMap.put(HHSConstants.STATUS_PAYMENT_PENDING_FMS_ACTION, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PAYMENT_PENDING_FMS_ACTION));
			loFincCountMap.put(HHSConstants.STATUS_INVOICE_PENDING_APPROVAL, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_INVOICE_PENDING_APPROVAL));
			loFincCountMap.put(HHSConstants.STATUS_PAYMENT_PENDING_APPROVAL, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PAYMENT_PENDING_APPROVAL));
			loFincCountMap.put(HHSConstants.BUDGET_TYPE_CONTRACT_BUDGET, HHSConstants.BUDGET_TYPE3);
			loFincCountMap.put(HHSConstants.BUDGET_TYPE_BUDGET_AMENDMENT, HHSConstants.BUDGET_TYPE1);
			loFincCountMap.put(HHSConstants.BUDGET_TYPE_BUDGET_MODIFICATION, HHSConstants.BUDGET_TYPE2);
			loFincCountMap.put(HHSConstants.BUDGET_TYPE_BUDGET_UPDATE, HHSConstants.BUDGET_TYPE4);
			loFinancialSummaryBean = (FinancialSummaryBean) DAOUtil.masterDAO(aoMybatisSession, loFincCountMap,
					HHSConstants.MAPPER_CLASS_COMMON_MAPPER, HHSConstants.FETCH_ACC_FINANCIALS_PORTLET_COUNT,
					HHSConstants.JAVA_UTIL_HASH_MAP);

			setMoState("Financials Homepage Counts fetched successfully");
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			LOG_OBJECT
					.Error("Exception occured while fetching Procurement HomePage Counts in SolicitationFinancialsGeneralService ",
							loExp);
			setMoState("Error occurred while fetching Financials Homepage Counts");
			throw loExp;
		}
		return loFinancialSummaryBean;
	}

	/**
	 * This method will fetch financial home page count for Provider users
	 * <ul>
	 * <li>Execute select query with id <b>"fetchProviderFinancialCount"</b>
	 * from common mapper</li>
	 * <li>Fetch count of Contracts Pending Registration</li>
	 * <li>Fetch count of Active Budgets</li>
	 * <li>Fetch count of Budgets Pending Submission</li>
	 * <li>Fetch count of Budgets Pending Approval</li>
	 * <li>Fetch count of Budgets Returned For Revision</li>
	 * <li>Fetch count of Modification Pending Submission</li>
	 * <li>Fetch count of Modification Pending Approval</li>
	 * <li>Fetch count of Modification Returned for Revision</li>
	 * <li>Fetch count of Invoices Pending Submission</li>
	 * <li>Fetch count of Invoice Pending Approval</li>
	 * <li>Fetch count of Invoice Returned for Revision</li>
	 * </ul>
	 * @param aoMybatisSession Valid Sql Session
	 * @param asOrgId asOrgId
	 * @return loFinancialSummaryBean financial Summary bean
	 * @throws ApplicationException If any Exception occurred
	 */
	public FinancialSummaryBean fetchProviderFinancialCount(SqlSession aoMybatisSession, String asOrgId)
			throws ApplicationException
	{
		FinancialSummaryBean loFinancialSummaryBean = null;
		try
		{
			Map<String, String> loDataMap = new HashMap<String, String>();
			loDataMap.put(HHSConstants.ORG_ID, asOrgId);
			loDataMap.put(HHSConstants.STATUS_CONTRACT_PENDING_REGISTARTION, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_CONTRACT_PENDING_REGISTARTION));
			loDataMap.put(HHSConstants.STATUS_BUDGET_ACTIVE, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_BUDGET_ACTIVE));
			loDataMap.put(HHSConstants.BUDGET_PENDING_SUBMISSION, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.BUDGET_PENDING_SUBMISSION));
			loDataMap.put(HHSConstants.STATUS_BUDGET_PENDING_APPROVAL, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_BUDGET_PENDING_APPROVAL));
			loDataMap.put(HHSConstants.STATUS_INVOICE_PENDING_APPROVAL, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_INVOICE_PENDING_APPROVAL));
			loDataMap.put(HHSConstants.STATUS_INVOICE_PENDING_SUBMISSION, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_INVOICE_PENDING_SUBMISSION));
			loDataMap.put(HHSConstants.BUDGET_RETURNED_FOR_REVISION, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.BUDGET_RETURNED_FOR_REVISION));
			loDataMap.put(HHSConstants.STATUS_INVOICE_RETURNED_FOR_REVISION, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_INVOICE_RETURNED_FOR_REVISION));
			loDataMap.put(HHSConstants.BUDGET_AMEND_TYPE, HHSConstants.ONE);
			loDataMap.put(HHSConstants.CONTRACT_BUDGET_TYPE, HHSConstants.TWO);
			loDataMap.put(HHSConstants.BUDGET_MOD_TYPE, HHSConstants.THREE);
			loDataMap.put(HHSConstants.BUDGET_UPD_TYPE, HHSConstants.FOUR);
			loFinancialSummaryBean = (FinancialSummaryBean) DAOUtil.masterDAO(aoMybatisSession, loDataMap,
					HHSConstants.MAPPER_CLASS_COMMON_MAPPER, HHSConstants.FETCH_PROVIDER_FINANCIAL_COUNT,
					HHSConstants.JAVA_UTIL_MAP);

			setMoState("Financials Homepage Counts fetched successfully for Provider: " + asOrgId);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured while fetching Financials Homepage Counts for  Provider: " + asOrgId,
					aoAppEx);
			setMoState("Error occurred while fetching Financials Homepage Counts for Provider: " + asOrgId);
			throw aoAppEx;
		}
		return loFinancialSummaryBean;
	}

	/**
	 * This method gets Master Status
	 * @param aoMyBatisSession aoMyBatisSession
	 * @return loListMasterStatusBean
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public List<MasterStatusBean> getMasterStatus(SqlSession aoMyBatisSession) throws ApplicationException
	{
		List<MasterStatusBean> loListMasterStatusBean = null;
		try
		{
			loListMasterStatusBean = (ArrayList<MasterStatusBean>) DAOUtil.masterDAO(aoMyBatisSession, null,
					HHSConstants.MAPPER_CLASS_COMMON_MAPPER, HHSConstants.GET_MASTER_STATUS, null);
		}
		catch (ApplicationException aoAppEx)
		{
			throw aoAppEx;
		}
		return loListMasterStatusBean;
	}

	/***
	 * 
	 * @param aoMybatisSession aoMybatisSession
	 * @param aiProcurementId aiProcurementId
	 * @param asScreenName Screen name
	 * @return loHashMap HashMap
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public HashMap<Object, Object> getProcurementChangeControlWidget(SqlSession aoMybatisSession,
			Integer aiProcurementId, String asScreenName) throws ApplicationException
	{
		boolean lbHasScreen = false;
		HashMap<Object, Object> loHashMap = null;
		try
		{
			Map<String, Object> loDataMap = new HashMap<String, Object>();
			loDataMap.put(HHSConstants.AI_PROC_ID, aiProcurementId);
			if (asScreenName != null)
			{
				String[] loTableNames = HHSConstants.CHANGE_CONTROL_SETTING.get(asScreenName);
				if (loTableNames != null)
				{
					loDataMap.put(HHSConstants.MAIN_TABLE, loTableNames[0]);
					loDataMap.put(HHSConstants.ADDENDUM_TABLE, loTableNames[1]);
					loDataMap.put(HHSConstants.MODIFIED_DATE, loTableNames[2]);
					loDataMap.put(HHSConstants.MODIFIED_BY, loTableNames[3]);
					lbHasScreen = true;
				}
			}
			if (lbHasScreen)
			{
				loHashMap = (HashMap<Object, Object>) DAOUtil.masterDAO(aoMybatisSession, loDataMap,
						HHSConstants.MAPPER_CLASS_COMMON_MAPPER,
						HHSConstants.GET_PROCUREMENT_CHANGE_CONTROL_WIDGET_DETAILED, HHSConstants.JAVA_UTIL_MAP);
			}
			else
			{
				loHashMap = (HashMap<Object, Object>) DAOUtil.masterDAO(aoMybatisSession, aiProcurementId,
						HHSConstants.MAPPER_CLASS_COMMON_MAPPER, HHSConstants.GET_PROCUREMENT_CHANGE_CONTROL_WIDGET,
						HHSConstants.INTEGER_CLASS_PATH);
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			throw aoAppEx;
		}
		return loHashMap;
	}

	/**
	 * This method is used for authenticating login user
	 * 
	 * <ul>
	 * <li>1.Get the required info for authentication i.e username,password</li>
	 * <li>2.Create instance of AccountRequestLoginStub</li>
	 * <li>3.Call authenticateLoginUser passing username,password as parameter</li>
	 * <li>4.Get the authentication Status Flag .User is valid if status flag is
	 * true</li>
	 * </ul>
	 * 
	 * @param aoAuthBean AuthenticationBean
	 * @return boolean will provide flag whether user is authenticated or not
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public boolean authenticateLoginUser(AuthenticationBean aoAuthBean) throws ApplicationException
	{
		boolean lbAuthStatusFlag = false;
		try
		{
			AccountRequestLoginStub loAcctReqLoginStb = new AccountRequestLoginStub();
			lbAuthStatusFlag = loAcctReqLoginStb.authenticateLoginUser(aoAuthBean.getUserName(),
					aoAuthBean.getPassword());
			setMoState("User " + aoAuthBean.getUserName() + "successfully authenticated");
		}
		catch (ApplicationException aoAppEx)
		{
			setMoState("Transaction Failed:: SolicitationFinancialsGeneralService:authenticateLoginUser method - while authenticating login details for user "
					+ aoAuthBean.getUserName() + "\n ");
			throw aoAppEx;
		}
		return lbAuthStatusFlag;
	}

	/**
	 * This method will get the list of All the Epins from the Epin Refrence
	 * Table
	 * <ul>
	 * <li>Get the SQL session from the Parameter</li>
	 * <li>Execute the query <b>fetchEpinList</b> of Procurement Mapper</li>
	 * </ul>
	 * @param aoMybatisSession Valid SQL Session
	 * @param asQueryId asQueryId
	 * @param asDataToSearch data to be searched
	 * @return loEpinList List of All E-pins
	 * @throws ApplicationException when any error Occured
	 */
	@SuppressWarnings("unchecked")
	public List<String> fetchEpinList(SqlSession aoMybatisSession, String asQueryId, String asDataToSearch)
			throws ApplicationException
	{
		List<String> loEpinList = null;
		try
		{
			// check whether the query string is null or not
			if (null != asQueryId)
			{
				asDataToSearch = HHSConstants.PERCENT + asDataToSearch.toLowerCase() + HHSConstants.PERCENT;
				loEpinList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, asDataToSearch,
						HHSConstants.MAPPER_CLASS_COMMON_MAPPER, asQueryId, HHSConstants.JAVA_LANG_STRING);
			}
			else
			{
				throw new ApplicationException("query id can not be null to fetch E-PIN List");
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT
					.Error("Exception occured while fetchin epinlist in fetchEpinList method in SolicitationFinancialsGeneralService ",
							aoAppEx);
			setMoState("Transaction Failed:: SolicitationFinancialsGeneralService:fetchEpinList method - failed to get the Epin List with queryId: \n"
					+ asQueryId);
			throw aoAppEx;
		}
		return loEpinList;
	}

	/**
	 * This method will get the list of All the Epins from the Epin Refrence
	 * Table
	 * <ul>
	 * <li>Get the SQL session from the Parameter</li>
	 * <li>Execute the query <b>fetchEpinList</b> of Procurement Mapper</li>
	 * </ul>
	 * @param aoMybatisSession Valid SQL Session
	 * @param asQueryId Query Id
	 * @param asDataToSearch Data To Search
	 * @return loEpinList List of All E-pins
	 * 
	 * @throws ApplicationException when any error Occured
	 */
	public List<String> fetchContractNoList(SqlSession aoMybatisSession, String asQueryId, String asDataToSearch)
			throws ApplicationException
	{
		List<String> loContractNoList = null;
		try
		{
			asDataToSearch = HHSConstants.PERCENT + asDataToSearch.toLowerCase() + HHSConstants.PERCENT;
			loContractNoList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, asDataToSearch,
					HHSConstants.MAPPER_CLASS_COMMON_MAPPER, asQueryId, HHSConstants.JAVA_LANG_STRING);
		}
		catch (ApplicationException aoAppEx)
		{
			setMoState("Transaction Failed:: SolicitationFinancialsGeneralService:fetchEpinList method - failed to get the Epin List \n");
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			setMoState("Transaction Failed:: SolicitationFinancialsGeneralService:fetchEpinList method - failed to get the Epin List \n");
			throw new ApplicationException("Error occured while getting Epin in fetchEpinList", aoAppEx);
		}

		return loContractNoList;
	}

	/**
	 * This method updates the last modified date and user in the database.
	 * <ul>
	 * <li>1. Retrieve the required information i.e. user name</li>
	 * <li>2. Execute query <b>updateLastModifiedDetails</b> to update the last
	 * modified by field in the database with the login user name and last
	 * modified date with the current date</li>
	 * <li>This query used: updateLastModifiedDetails</li>
	 * <li>This method was updated in R4</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession - mybatis SQL session
	 * @param aoLastModifiedHashMap - HashMap containing last modified details
	 * @param abSuccessStatus - boolean value of success status
	 * @return - boolean value of update status
	 * @throws ApplicationException If an Application Exception occurs
	 */

	public Boolean updateLastModifiedDetails(SqlSession aoMyBatisSession, Map aoLastModifiedHashMap,
			Boolean abSuccessStatus) throws ApplicationException
	{
		Boolean lbUpdateStatus = false;
		HashMap loHmReqExceProp = new HashMap();
		LOG_OBJECT.Info("Entered into loading services details::" + loHmReqExceProp.toString());

		if (abSuccessStatus)
		{
			try
			{
				DAOUtil.masterDAO(aoMyBatisSession, aoLastModifiedHashMap, HHSConstants.MAPPER_CLASS_COMMON_MAPPER,
						HHSConstants.UPDATE_LAST_MODIFIED_DETAILS, HHSConstants.JAVA_UTIL_MAP);
				setMoState("Last Modified details inserted successfully");
				lbUpdateStatus = true;
				setMoState("Last Modified details updated successfully for procurement Id"
						+ aoLastModifiedHashMap.get(HHSConstants.PROCUREMENT_ID));
			}

			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handles over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */

			catch (ApplicationException loExp)
			{
				loExp.setContextData(aoLastModifiedHashMap);
				loExp.setContextData(loHmReqExceProp);
				LOG_OBJECT.Error("Error while inserting Last Modified details in procurement table :", loExp);
				throw loExp;
			}
			catch (Exception loEx)
			{
				ApplicationException loExp = new ApplicationException(
						"Error while inserting Last Modified details in procurement table:"
								+ aoLastModifiedHashMap.get(HHSConstants.PROCUREMENT_ID), loEx);
				throw loExp;
			}
		}
		return lbUpdateStatus;
	}

	/**
	 * This method fetches the provider widget details i.e. procurement status
	 * and provider status corresponding to the procurement Id.
	 * <ul>
	 * <li>1. Retrieve procurement Is and provider Id</li>
	 * <li>2. Create a HashMap<String, String> and populate it with procurement
	 * Id, provider Id, and procurement and provider status strings</li>
	 * <li>3. Execute query <b>getProviderWidgetDetils</b> to fetch the required
	 * details and return the output</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProcurementId - string representation of Procurement Id
	 * @param asProviderId - string representation of Provider Id
	 * @return loProviderMap - HashMap populated with provider widget details
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public Map<Object, Object> getProviderWidgetDetils(SqlSession aoMybatisSession, String asProcurementId,
			String asProviderId) throws ApplicationException
	{
		Map<Object, Object> loProviderMap = null;
		Map<String, String> loProviderDetails = new HashMap<String, String>();
		try
		{
			if (null != asProviderId)
			{
				loProviderDetails.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
				loProviderDetails.put(HHSConstants.PROVIDER_ID_KEY, asProviderId);
				loProviderDetails.put(HHSConstants.STATUS_PROCUREMENT_PLANNED, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_PLANNED));
				loProviderDetails.put(HHSConstants.STATUS_PROVIDER_SERVICE_APP_REQUIRED, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROVIDER_SERVICE_APP_REQUIRED));
				loProviderDetails.put(HHSConstants.STATUS_PROCUREMENT_RELEASED, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_RELEASED));
				loProviderDetails.put(HHSConstants.STATUS_PROVIDER_NOT_APPLICABLE, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROVIDER_NOT_APPLICABLE));

				loProviderMap = (Map<Object, Object>) DAOUtil.masterDAO(aoMybatisSession, loProviderDetails,
						HHSConstants.MAPPER_CLASS_COMMON_MAPPER, HHSConstants.GET_PROVIDER_WIDGET_DETILS,
						HHSConstants.JAVA_UTIL_MAP);
				setMoState("Provider Widget Details fetched successfully for Provider Id:" + asProviderId);
			}
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while fetching Provider widget details");
			throw loExp;
		}
		return loProviderMap;
	}

	/**
	 * This method will get get the count of the rows exists in database for the
	 * specific document id
	 * <ul>
	 * <li>Get the SQL session from the Parameter</li>
	 * <li>Execute the query <b>checkDocumentExistsInAnyTable</b> of common
	 * mapper</li>
	 * </ul>
	 * @param aoMybatisSession Valid SQL Session
	 * @param asDocumentId asDocumentId
	 * @return loEpinList List of All E-pins
	 * @throws ApplicationException when any error Occured
	 */
	public Boolean checkDocumentExistsInAnyTable(SqlSession aoMybatisSession, String asDocumentId)
			throws ApplicationException
	{
		Boolean lbDocumentExists = Boolean.FALSE;
		Integer liNoRowsExists = HHSConstants.INT_ZERO;
		try
		{
			liNoRowsExists = (Integer) DAOUtil.masterDAO(aoMybatisSession, asDocumentId,
					HHSConstants.MAPPER_CLASS_COMMON_MAPPER, HHSConstants.CHECK_DOCUMENT_EXISTS_IN_ANY_TABLE,
					HHSConstants.JAVA_LANG_STRING);
			if (liNoRowsExists > 1)
			{
				lbDocumentExists = Boolean.TRUE;
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT
					.Error("Transaction Failed:: SolicitationFinancialsGeneralService:checkDocumentExistsInAnyTable method - failed to get document exists count",
							aoAppEx);
			setMoState("Transaction Failed:: SolicitationFinancialsGeneralService:checkDocumentExistsInAnyTable method - failed to get document exists count \n");
			throw aoAppEx;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (Exception aoAppEx)
		{
			LOG_OBJECT
					.Error("Transaction Failed:: SolicitationFinancialsGeneralService:checkDocumentExistsInAnyTable method - failed to get document exists count",
							aoAppEx);
			setMoState("Transaction Failed:: SolicitationFinancialsGeneralService:checkDocumentExistsInAnyTable method - failed to get document exists count \n");
			throw new ApplicationException("Error occured while getting document count from database", aoAppEx);
		}

		return lbDocumentExists;
	}

	/**
	 * This method will get get the count of the rows exists in database for the
	 * specific document id
	 * <ul>
	 * <li>Get the SQL session from the Parameter</li>
	 * <li>Execute the query <b>checkDocumentExistsInAnyTable</b> of common
	 * mapper</li>
	 * </ul>
	 * @param aoMybatisSession Valid SQL Session
	 * @param aoDocumentIds aoDocumentIds
	 * @return loEpinList List of All E-pins
	 * @throws ApplicationException when any error Occured
	 */
	public List<String> checkDocumentsExistsInAnyTable(SqlSession aoMybatisSession, List<String> aoDocumentIds)
			throws ApplicationException
	{
		Integer liNoRowsExists = HHSConstants.INT_ZERO;
		List<String> loDocumentLists = new ArrayList<String>();
		try
		{
			// checking if document Id list is not null and its size is greater
			// than zero
			if (aoDocumentIds != null && !aoDocumentIds.isEmpty())
			{
				// iterating document Id list
				for (Iterator loIterator = aoDocumentIds.iterator(); loIterator.hasNext();)
				{
					String lsDocumentId = (String) loIterator.next();
					liNoRowsExists = (Integer) DAOUtil.masterDAO(aoMybatisSession, lsDocumentId,
							HHSConstants.MAPPER_CLASS_COMMON_MAPPER, HHSConstants.CHECK_DOCUMENT_EXISTS_IN_ANY_TABLE,
							HHSConstants.JAVA_LANG_STRING);
					// checking if count of no. of rows is 1
					if (liNoRowsExists == 1)
					{
						loDocumentLists.add(lsDocumentId);
					}

				}
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			setMoState("Transaction Failed:: SolicitationFinancialsGeneralService:checkDocumentExistsInAnyTable method - failed to get document exists count \n");
			throw aoAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception aoAppEx)
		{
			setMoState("Transaction Failed:: SolicitationFinancialsGeneralService:checkDocumentExistsInAnyTable method - failed to get document exists count \n");
			throw new ApplicationException("Error occured while getting document count from database", aoAppEx);
		}

		return loDocumentLists;
	}

	/**
	 * This method removes the locked screens of a user on login if the screen
	 * was locked 1 day before login and was not accidentally removed
	 * <ul>
	 * <li>1. Execute query "removeLockedUserById" to remove locks corresponding
	 * to logger in user id if locks are older than one day</li>
	 * <li>2. return true flag in case query executes successfully</li>
	 * </ul>
	 * @param aoMybatisSession - my batis session object
	 * @param asUserId - user id of current logged in user
	 * @return - flag depecting if data has been successfully deleted
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Boolean removeLockedUserById(SqlSession aoMybatisSession, String asUserId) throws ApplicationException
	{
		Boolean loIsDataDeleted = false;
		try
		{
			DAOUtil.masterDAO(aoMybatisSession, asUserId, HHSConstants.MAPPER_CLASS_COMMON_MAPPER,
					HHSConstants.REMOVE_LOCKED_USER_BY_ID, HHSConstants.JAVA_LANG_STRING);
			loIsDataDeleted = true;
		}
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData(HHSConstants.LO_SERVICE_DATA, asUserId);
			setMoState("Transaction Failed:: SolicitationFinancialsGeneralService:removeLockedUserById method\n");
			throw aoAppEx;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured while removing lock for user by id",
					aoEx);
			loAppEx.addContextData(HHSConstants.LO_SERVICE_DATA, asUserId);
			setMoState("Transaction Failed:: SolicitationFinancialsGeneralService:removeLockedUserById method\n");
			throw loAppEx;
		}
		return loIsDataDeleted;
	}

	/**
	 * This method removes all the locks corresponding to session id
	 * <ul>
	 * <li>1. Execute query "removeLockedUser" to remove locks corresponding to
	 * session id</li>
	 * <li>2. return true flag in case query executes successfully</li>
	 * <li>The query used: removeLockedUser</li>
	 * </ul>
	 * @param aoMybatisSession - my batis session object
	 * @param asSessionId - current users session id
	 * @return - flag depecting if data has been successfully deleted
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Boolean removeLockedUser(SqlSession aoMybatisSession, String asSessionId) throws ApplicationException
	{
		Boolean loIsDataDeleted = false;
		try
		{
			DAOUtil.masterDAO(aoMybatisSession, asSessionId, HHSConstants.MAPPER_CLASS_COMMON_MAPPER,
					HHSConstants.REMOVE_LOCKED_USER, HHSConstants.JAVA_LANG_STRING);
			loIsDataDeleted = true;
		}
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData(HHSConstants.LO_SERVICE_DATA, asSessionId);
			setMoState("Transaction Failed:: SolicitationFinancialsGeneralService:removeLockedUser method\n");
			throw aoAppEx;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error occured while removing lock for user by session id", aoEx);
			loAppEx.addContextData(HHSConstants.LO_SERVICE_DATA, asSessionId);
			setMoState("Transaction Failed:: SolicitationFinancialsGeneralService:removeLockedUser method\n");
			throw loAppEx;
		}
		return loIsDataDeleted;
	}

	/**
	 * This method gets the locking details for a lock id, if any
	 * <ul>
	 * <li>1. Set the data in map</li>
	 * <li>2. Execute query "checkLockFlagExist" and get data corresponding to
	 * locking id(if any)</li>
	 * </ul>
	 * @param aoMybatisSession - my batis session object
	 * @param asLockId - lock id to be checked
	 * @param asSessionId - current users session id
	 * @param asUserName - user name of current user
	 * @return - map corresponding to lock id
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> checkLockFlagExist(SqlSession aoMybatisSession, String asLockId, String asSessionId,
			String asUserName) throws ApplicationException
	{
		Map<String, String> loOutput = null;
		Map<String, String> loDataMap = new HashMap<String, String>();
		try
		{
			loDataMap.put(HHSConstants.LOCK_ID, asLockId);
			loDataMap.put(HHSConstants.SESSION_ID, asSessionId);
			loDataMap.put(HHSConstants.KEY_SESSION_USER_NAME, asUserName);
			loOutput = (Map<String, String>) DAOUtil.masterDAO(aoMybatisSession, loDataMap,
					HHSConstants.MAPPER_CLASS_COMMON_MAPPER, HHSConstants.CHECK_LOCK_FLAG_EXIST,
					HHSConstants.JAVA_UTIL_MAP);
		}
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData(HHSConstants.LO_SERVICE_DATA, loDataMap);
			setMoState("Transaction Failed:: SolicitationFinancialsGeneralService:checkLockFlagExist method\n");
			throw aoAppEx;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error occured while getting data corresponding to lock id", aoEx);
			loAppEx.addContextData(HHSConstants.LO_SERVICE_DATA, loDataMap);
			setMoState("Transaction Failed:: SolicitationFinancialsGeneralService:checkLockFlagExist method\n");
			throw loAppEx;
		}
		return loOutput;
	}

	/**
	 * This method checks if a lock already exist for a locking id, if not add
	 * the lock
	 * <ul>
	 * <li>1. Check if lock exist for locking id</li>
	 * <li>2. In case no lock exist, remove any previous lock taken by current
	 * user and add a new lock using "addLock" query id</li>
	 * </ul>
	 * @param aoMybatisSession - my batis session object
	 * @param asLockId - lock id to be checked
	 * @param asSessionId - current users session id
	 * @param asUserName - user name of current user
	 * @param asUserId- user id of current user
	 * @param aoLockData - lock data if any for current lock id
	 * @return - flag depecting that a new lock has been added
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Boolean addLock(SqlSession aoMybatisSession, String asLockId, String asSessionId, String asUserName,
			String asUserId, Map<String, String> aoLockData) throws ApplicationException
	{
		Boolean loIsLockAdded = false;
		Map<String, String> loDataMap = new HashMap<String, String>();
		try
		{
			loDataMap.put(HHSConstants.LOCK_ID, asLockId);
			loDataMap.put(HHSConstants.SESSION_ID, asSessionId);
			loDataMap.put(HHSConstants.KEY_SESSION_USER_NAME, asUserName);
			loDataMap.put(HHSConstants.AS_USER_ID, asUserId);
			if (aoLockData == null || aoLockData.get(HHSConstants.USER_SESSION_ID) == null)
			{
				removeLockedUser(aoMybatisSession, asSessionId);
				DAOUtil.masterDAO(aoMybatisSession, loDataMap, HHSConstants.MAPPER_CLASS_COMMON_MAPPER,
						HHSConstants.ADD_LOCK, HHSConstants.JAVA_UTIL_MAP);
				loIsLockAdded = true;
			}
		}
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData(HHSConstants.LO_SERVICE_DATA, loDataMap);
			setMoState("Transaction Failed:: SolicitationFinancialsGeneralService:addLock method\n");
			throw aoAppEx;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured while adding lock for user", aoEx);
			loAppEx.addContextData(HHSConstants.LO_SERVICE_DATA, loDataMap);
			setMoState("Transaction Failed:: SolicitationFinancialsGeneralService:addLock method\n");
			throw loAppEx;
		}
		return loIsLockAdded;
	}

	/**
	 * This method is used to fetch procuring agencies list from database <li>
	 * This query used: fetchProcuringAgenciesFromDB</li>
	 * @param aoMybatisSession Mybatis Session
	 * @return loProcuringAgencyMap List
	 * @throws ApplicationException
	 */
	public List<Map<String, String>> getProcuringAgencyFromDB(SqlSession aoMybatisSession) throws ApplicationException
	{
		List<Map<String, String>> loProcuringAgencyMap = null;
		try
		{
			loProcuringAgencyMap = (List<Map<String, String>>) DAOUtil.masterDAO(aoMybatisSession, null,
					HHSConstants.MAPPER_CLASS_COMMON_MAPPER, HHSConstants.FETCH_PROCURING_AGENCIES, null);

		}
		catch (ApplicationException aoAppEx)
		{
			setMoState("Transaction Failed:: fetching procuring agencies from DB");
			throw aoAppEx;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured while adding lock for user", aoEx);
			setMoState("Transaction Failed:: fetching procuring agencies from DB");
			throw loAppEx;
		}
		return loProcuringAgencyMap;
	}

	/**
	 * This method is used to fetch queue item delay configuration agencies list
	 * from database <li>
	 * This query used: getQueueItemDelayConfig</li> <li>This method is added in
	 * build no 3.6.0 for Enhancement Number 6508</li>
	 * @param aoMybatisSession Mybatis Session
	 * @return loProcuringAgencyMap List
	 * @throws ApplicationException
	 */
	public List<QueueItemsDetailsBean> getQueueItemDelayConfig(SqlSession aoMybatisSession) throws ApplicationException
	{
		List<QueueItemsDetailsBean> loTimeConfigMap = new ArrayList<QueueItemsDetailsBean>();
		try
		{
			loTimeConfigMap = (List<QueueItemsDetailsBean>) DAOUtil.masterDAO(aoMybatisSession, null,
					HHSConstants.MAPPER_CLASS_COMMON_MAPPER, HHSConstants.FETCH_QUEUE_ITEM_DELAY_CONFIG, null);

		}
		catch (ApplicationException aoAppEx)
		{
			setMoState("Transaction Failed:: fetching Queue Item Delay Configuration from DB");
			throw aoAppEx;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error occured while fetching Queue Item Delay Configuration from DB", aoEx);
			setMoState("Transaction Failed:: fetching Queue Item Delay Configuration from DB");
			throw loAppEx;
		}
		return loTimeConfigMap;
	}
}
