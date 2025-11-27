package com.nyc.hhs.contractsbatch.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.constants.TransactionConstants;
import com.nyc.hhs.daomanager.service.ContractBudgetModificationService;
import com.nyc.hhs.daomanager.service.FinancialsService;
import com.nyc.hhs.daomanager.service.HhsAuditService;
import com.nyc.hhs.daomanager.service.ServiceState;
import com.nyc.hhs.daomanager.service.TaskService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.ContractBean;
import com.nyc.hhs.model.ContractBudgetBean;
import com.nyc.hhs.model.ContractFinancialBean;
import com.nyc.hhs.model.ContractList;
import com.nyc.hhs.model.EPinDetailBean;
import com.nyc.hhs.model.FiscalDate;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.NotificationDataBean;
import com.nyc.hhs.model.SiteDetailsBean;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.service.db.services.notification.NotificationService;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This service class will be used to get/set all data for Batch Processing for
 * Contract/Amendment List from database using mappings and queries defined in
 * ContractMapper.xml. Insertion or update of data can also be performed.
 * 
 */
public class ContractsBatchService extends ServiceState
{
	private static final LogInfo LOG_OBJECT = new LogInfo(ContractsBatchService.class);

	/**
	 * This method is for fetching the Contracts list for Batch Process
	 * 
	 * <ul>
	 * <li>Fetches the List of Contracts with their status set as 'ETL
	 * Registered'(which are to be batch processed)</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Mybatis Session
	 * @param aoHMArgs a HashMap type object
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public List<ContractBean> fetchContractsForBatchProcess(SqlSession aoMybatisSession, HashMap aoHMArgs)
			throws ApplicationException
	{
System.out.println("====ContractBatchService============[fetchContractsForBatchProcess:Start]  :: " );		
		List<ContractBean> loContractBeanList = null;
		HashMap loHMArgs = null;
		try
		{
			// Fetch the Contracts with status set as 'ETL Registered'
			loHMArgs = (HashMap) aoHMArgs.get(HHSConstants.AO_HM_ARGS);
			System.out.println("======ContractBatchService==========Contract Status to select  :: " + loHMArgs);		
			loContractBeanList = (List<ContractBean>) DAOUtil.masterDAO(aoMybatisSession, loHMArgs,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_CONTRACTS_FOR_BATCH_PROCESS,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			setMoState("Base Contracts for Batch Process fetched successfully.\n");
			
		}
		catch (ApplicationException aoExp)
		{
			// Handle the ApplicationException type Exception and set moState
			// and context data
			setMoState("Error occured while Batch Process in fetchContractsForBatchProcess() method\n");
			aoExp.addContextData("Exception occured while Batch Process in fetchContractsForBatchProcess() method",
					aoExp);
			LOG_OBJECT.Error("Error occured while Batch Process in fetchContractsForBatchProcess() method", aoExp);
			throw aoExp;
		}
		catch (Exception aoExp)
		{
			// Handle the Exception type exception and set moState
			// and context data
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while Batch Process in fetchContractsForBatchProcess() method", aoExp);
			setMoState("Error occured while Batch Process in fetchContractsForBatchProcess() method\n");
			loAppEx.addContextData("Exception occured while Batch Process in fetchContractsForBatchProcess() method",
					loAppEx);
			LOG_OBJECT.Error("Error occured while Batch Process in fetchContractsForBatchProcess() method", loAppEx);
			throw loAppEx;
		}
		return loContractBeanList;
	}

	/**
	 * This method is for fetching the Contracts list for Batch Process
	 * 
	 * <ul>
	 * <li>Fetches the List of Contract Amendments with their status set as ETL
	 * Registered(which are to be batch processed)</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Mybatis Session
	 * @param aoHMArgs a HashMap type object
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public List<ContractBean> fetchAmendmentContractsForBatchProcess(SqlSession aoMybatisSession, HashMap aoHMArgs)
			throws ApplicationException
	{
		List<ContractBean> loContractBeanList = null;
		HashMap loHMArgs = null;
		/*
		 * [Start] R7.12.0 QC9314
		 * Set all Amendments status 'Registered' which have been merged through 'Mark as Registered'.   
		*/
				Integer loBudgetCnt = (Integer) DAOUtil.masterDAO(aoMybatisSession, null,
						HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.SET_BUDGET_FROM_AMEND_STATUS_AS_ACTIVE, null);

				Integer loAmdCnt = (Integer) DAOUtil.masterDAO(aoMybatisSession, null,
						HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.SET_AMENDMENT_STATUS_AS_REGISTERED, null);
				
				setMoState( "Totoal " + loAmdCnt + " Amendment(s) has been set as Registered. \n " + 
							loBudgetCnt + " Base Budget(s) from Out Year Amendment set as Active." );
		/*
		 * [End] R7.12.0 QC9314   
		*/

		try
		{
			// Fetch the Contract Amendments with status set as 'ETL Registered'
			loHMArgs = (HashMap) aoHMArgs.get(HHSConstants.AO_HM_ARGS);
			loContractBeanList = (List<ContractBean>) DAOUtil.masterDAO(aoMybatisSession, loHMArgs,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSConstants.FETCH_AMENDMENT_CONTRACTS_FOR_BATCH_PROCESS, HHSConstants.JAVA_UTIL_HASH_MAP);
			setMoState("Amendment Contracts for Batch Process fetched successfully.\n");
			System.out.println("======ContractBatchService==========fetchAmendmentContractsForBatchProcess   ");		
		}
		catch (ApplicationException aoExp)
		{
			// Handle the ApplicationException type Exception and set moState
			// and context data
			setMoState("Error occured while Batch Process in fetchAmendmentContractsForBatchProcess() method\n");
			aoExp.addContextData(
					"Exception occured while Batch Process in fetchAmendmentContractsForBatchProcess() method", aoExp);
			LOG_OBJECT.Error("Error occured while Batch Process in fetchAmendmentContractsForBatchProcess() method",
					aoExp);
			throw aoExp;
		}
		catch (Exception aoExp)
		{
			// Handle the Exception type exception and set moState
			// and context data
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while Batch Process in fetchAmendmentContractsForBatchProcess() method", aoExp);
			setMoState("Error occured while Batch Process in fetchAmendmentContractsForBatchProcess() method\n");
			loAppEx.addContextData(
					"Exception occured while Batch Process in fetchAmendmentContractsForBatchProcess() method", loAppEx);
			LOG_OBJECT.Error("Error occured while Batch Process in fetchAmendmentContractsForBatchProcess() method",
					loAppEx);
			throw loAppEx;
		}

		return loContractBeanList;
	}

	/**
	 * This method is for closing Tasks for Batch Process for a contract (for
	 * parent contract if the contract id amendment contract)
	 * 
	 * <ul>
	 * <li>Closes Contract Budget Modification Review Task</li>
	 * <li>Closes Contract Budget Update Review Task</li>
	 * <li>Closes Contract Configuration Update Task</li>
	 * <li>Closes New Fiscal Year Configuration Task</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Mybatis Session
	 * @param aoHMArgs a HashMap type object
	 * @return loTerminationFlag - Boolean type object
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public Boolean closeAppropriateTasks(SqlSession aoMybatisSession, HashMap aoHMArgs) throws ApplicationException
	{
		Boolean loTerminationFlag = Boolean.FALSE;
		String lsContractId = null;
		String lsUpdateTypeContractId = null;
		TaskService loTaskService = new TaskService();
		HashMap loUserHashMap = new HashMap();
		Map loContractInfo = null;

		try
		{
			ContractBean loContractBean = (ContractBean) aoHMArgs.get(HHSConstants.AO_CONTRACT_BEAN);
			P8UserSession loFilenetSession = (P8UserSession) aoHMArgs.get(HHSConstants.AO_FILENET_SESSION);

			setMoState("Closing/Cancelling all appropriate Tasks for Contract-" + loContractBean.getContractId() + "-'"
					+ loContractBean.getContractTitle() + "'.\n");

			// Check if it is for Contract of Type Amendment
			lsContractId = getBaseContractIdOfContract(loContractBean);

			// Close/Cancel appropriate tasks

			setMoState("Closing/Cancelling 'Contract Budget Modification Review' Tasks for Contract-"
					+ loContractBean.getContractId() + "-'" + loContractBean.getContractTitle() + "'.\n");
			closeContractBudgetModificationReviewTask(loFilenetSession, lsContractId);

			setMoState("Closing/Cancelling 'Contract Budget Update Review' Tasks for Contract-"
					+ loContractBean.getContractId() + "-'" + loContractBean.getContractTitle() + "'.\n");
			loContractInfo = (HashMap) DAOUtil.masterDAO(aoMybatisSession, lsContractId,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.BMC_FETCH_UPDATE_CONTRACT_ID,
					HHSConstants.JAVA_LANG_STRING);
			if (loContractInfo != null && !loContractInfo.isEmpty()
					&& loContractInfo.get(HHSConstants.CONTRACT_ID_UNDERSCORE) != null)
			{
				lsUpdateTypeContractId = String.valueOf(loContractInfo.get(HHSConstants.CONTRACT_ID_UNDERSCORE));
				closeContractBudgetUpdateReviewTask(loFilenetSession, lsUpdateTypeContractId);
			}

			setMoState("Closing/Cancelling 'Contract Configuration Update' Tasks for Contract-"
					+ loContractBean.getContractId() + "-'" + loContractBean.getContractTitle() + "'.\n");
			closeContractConfigurationUpdateTask(loFilenetSession, lsContractId);

			setMoState("Closing/Cancelling 'New Fiscal Year Configuration' Task for Contract-"
					+ loContractBean.getContractId() + "-'" + loContractBean.getContractTitle() + "'.\n");
			closeNewFiscalYearConfigurationTask(loFilenetSession, lsContractId);

			// Cleanup the Unnecessary Budgets and Contracts against Task
			// cancellations
			loUserHashMap.put(HHSConstants.MODIFY_BY, HHSConstants.SYSTEM_USER);
			loUserHashMap.put(HHSConstants.PROPERTY_PE_CONTRACT_ID, lsContractId);
			loTaskService.deleteNotNeededBudgetAndContract(aoMybatisSession, loUserHashMap, Boolean.TRUE);

			loTerminationFlag = Boolean.TRUE;
		}
		catch (ApplicationException aoExp)
		{
			// Handle the ApplicationException type Exception and set moState
			// and context data
			setMoState("Error occured while Batch Process in closeAppropriateTasks() method\n");
			aoExp.addContextData("Application Exception occured while Batch Process in closeAppropriateTasks() method",
					aoExp);
			LOG_OBJECT.Error("Error occured while Batch Process in closeAppropriateTasks() method", aoExp);
			throw aoExp;
		}
		catch (Exception aoExp)
		{
			// Handle the Exception type exception and set moState
			// and context data
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while Batch Process in closeAppropriateTasks() method", aoExp);
			setMoState("Error occured while Batch Process in closeAppropriateTasks() method\n");
			loAppEx.addContextData("Exception occured while Batch Process in closeAppropriateTasks() method", loAppEx);
			LOG_OBJECT.Error("Error occured while Batch Process in closeAppropriateTasks() method", loAppEx);
			throw loAppEx;
		}

		return loTerminationFlag;
	}

	/**
	 * This method is for closing Contract Budget Modification Review Task for
	 * Batch Process for a contract
	 * 
	 * <ul>
	 * <li>Closes Contract Budget Modification Review Task</li>
	 * </ul>
	 * 
	 * @param aoFilenetSession - P8UserSession Session
	 * @param asContractId - String type object
	 * @return loTerminationFlag - Boolean type object
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private Boolean closeContractBudgetModificationReviewTask(P8UserSession aoFilenetSession, String asContractId)
			throws ApplicationException
	{
		TaskService loTaskService = null;
		HashMap loWorkflowProperties = null;
		loTaskService = new TaskService();
		loWorkflowProperties = new HashMap();

		loWorkflowProperties.put(HHSConstants.PROPERTY_PE_CONTRACT_ID, asContractId);
		loWorkflowProperties.put(HHSConstants.PROPERTY_PE_TASK_TYPE, HHSConstants.TASK_BUDGET_MODIFICATION);
		Boolean loTerminationFlag = loTaskService.closeAllOpenTask(aoFilenetSession, loWorkflowProperties);
		return loTerminationFlag;
	}

	/**
	 * This method is for closing Task for Batch Process for a contract
	 * 
	 * <ul>
	 * <li>Closes Contract Budget Update Review Task</li>
	 * </ul>
	 * 
	 * @param aoFilenetSession a P8UserSession Session
	 * @param asContractId a String type object
	 * @return loTerminationFlag - Boolean type object
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private Boolean closeContractBudgetUpdateReviewTask(P8UserSession aoFilenetSession, String asContractId)
			throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		HashMap loWorkflowProperties = new HashMap();

		loWorkflowProperties.put(HHSConstants.PROPERTY_PE_CONTRACT_ID, asContractId);
		loWorkflowProperties.put(HHSConstants.PROPERTY_PE_TASK_TYPE, HHSConstants.TASK_BUDGET_UPDATE);
		Boolean loTerminationFlag = loTaskService.closeAllOpenTask(aoFilenetSession, loWorkflowProperties);
		return loTerminationFlag;
	}

	/**
	 * This method is for closing Contract Configuration Update Task for Batch
	 * Process for a contract
	 * 
	 * <ul>
	 * <li>Closes Contract Configuration Update Task</li>
	 * </ul>
	 * 
	 * @param aoFilenetSession - P8UserSession Session
	 * @param asContractId - String type object
	 * @return loTerminationFlag - Boolean type object
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private Boolean closeContractConfigurationUpdateTask(P8UserSession aoFilenetSession, String asContractId)
			throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		HashMap loWorkflowProperties = new HashMap();

		loWorkflowProperties.put(HHSConstants.PROPERTY_PE_CONTRACT_ID, asContractId);
		loWorkflowProperties.put(HHSConstants.PROPERTY_PE_TASK_TYPE, HHSConstants.TASK_CONTRACT_UPDATE);
		Boolean loTerminationFlag = loTaskService.closeAllOpenTask(aoFilenetSession, loWorkflowProperties);
		return loTerminationFlag;
	}

	/**
	 * This method is for closing New Fiscal Year Configuration Task for Batch
	 * Process for a contract
	 * 
	 * <ul>
	 * <li>Closes New Fiscal Year Configuration Task</li>
	 * </ul>
	 * 
	 * @param aoFilenetSession - P8UserSession Session
	 * @param asContractId - String type object
	 * @return loTerminationFlag - Boolean type object
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private Boolean closeNewFiscalYearConfigurationTask(P8UserSession aoFilenetSession, String asContractId)
			throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		HashMap loWorkflowProperties = new HashMap();

		loWorkflowProperties.put(HHSConstants.PROPERTY_PE_CONTRACT_ID, asContractId);
		loWorkflowProperties.put(HHSConstants.PROPERTY_PE_TASK_TYPE, HHSConstants.TASK_NEW_FY_CONFIGURATION);
		Boolean loTerminationFlag = loTaskService.closeAllOpenTask(aoFilenetSession, loWorkflowProperties);
		return loTerminationFlag;
	}

	/**
	 * This method is for Updating the status of 'Budget Modification' and
	 * 'Budget Update' to Canceled in Batch Process (of it's own if it is of
	 * Base Contract type or its Parent Base Contract if it is an Amendment Type
	 * Contract)
	 * <ul>
	 * <li>Updates status of Budget Modification to canceled</li>
	 * <li>Updates status of Budget Update to canceled</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - Mybatis Session
	 * @param aoHMArgs - HashMap type object
	 * @return loStatusUpdateResult - Boolean type object
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public Boolean updateBudgetModificationAndUpdateStatus(SqlSession aoMybatisSession, HashMap aoHMArgs)
			throws ApplicationException
	{
		Boolean loStatusUpdateResult = Boolean.FALSE;
		ContractBean loContractBean = (ContractBean) aoHMArgs.get(HHSConstants.AO_CONTRACT_BEAN);
		ContractBean loContractBeanObj = new ContractBean();

		try
		{
			setMoState("Updating Status of Modification/Update Budgets of Contract-" + loContractBean.getContractId()
					+ "-'" + loContractBean.getContractTitle() + "'.\n");

			loContractBeanObj.setContractId(getBaseContractIdOfContract(loContractBean));

			// Update the Status (to Canceled) of Budget (Modification/Update)of
			// Contracts which have DISCREPANCY_FLAG set
			DAOUtil.masterDAO(aoMybatisSession, loContractBeanObj, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSConstants.BATCH_UPDATE_BUDGET_MODIFICATION_AND_UPDATE_STATUS, HHSConstants.CONTRACT_BEAN_PATH);

			loStatusUpdateResult = Boolean.TRUE;
		}
		catch (ApplicationException aoExp)
		{
			// Handle the ApplicationException type Exception and set moState
			// and context data
			setMoState("Error occured while Batch Process in updateBudgetModificationAndUpdateStatus() method\n");
			aoExp.addContextData(
					"Exception occured while Batch Process in updateBudgetModificationAndUpdateStatus() method", aoExp);
			LOG_OBJECT.Error("Error occured while Batch Process in updateBudgetModificationAndUpdateStatus() method",
					aoExp);
			throw aoExp;
		}
		catch (Exception aoExp)
		{
			// Handle the Exception type exception and set moState
			// and context data
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while Batch Process in updateBudgetModificationAndUpdateStatus() method", aoExp);
			setMoState("Error occured while Batch Process in updateBudgetModificationAndUpdateStatus() method\n");
			loAppEx.addContextData(
					"Exception occured while Batch Process in updateBudgetModificationAndUpdateStatus() method",
					loAppEx);
			LOG_OBJECT.Error("Error occured while Batch Process in updateBudgetModificationAndUpdateStatus() method",
					loAppEx);
			throw loAppEx;
		}

		return loStatusUpdateResult;
	}

	/**
	 * This method resets the discrepancy in a contract
	 * 
	 * <ul>
	 * <li>Updates Contract DISCREPACNY_FLAG to '0'</li> *
	 * </ul>
	 * 
	 * @param aoMybatisSession Mybatis Session
	 * @param aoHMArgs - HashMap type object
	 * @return loContractDiscRemovalResult - Boolean type object
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes" })
	public Boolean resetDiscrepancyStatus(SqlSession aoMybatisSession, HashMap aoHMArgs) throws ApplicationException
	{
		Boolean loContractDiscResetResult = Boolean.FALSE;
		ContractBean loContractBean = (ContractBean) aoHMArgs.get(HHSConstants.AO_CONTRACT_BEAN);
		try
		{
			setMoState("Resetting Discrepancy Flag of Contract:" + loContractBean.getContractId() + "-'"
					+ loContractBean.getContractTitle() + "'.\n");

			DAOUtil.masterDAO(aoMybatisSession, loContractBean, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSConstants.RESET_DISCREPANCY_STATUS, HHSConstants.CONTRACT_BEAN_PATH);
			loContractDiscResetResult = Boolean.TRUE;
		}
		catch (ApplicationException aoExp)
		{
			// Handle the ApplicationException type Exception and set moState
			// and context data
			setMoState("Error occured while Batch Process in resetDiscrepancyStatus() method.\n");
			aoExp.addContextData("Exception occured while Batch Process in resetDiscrepancyStatus() method.", aoExp);
			LOG_OBJECT.Error("Error occured while Batch Process in resetDiscrepancyStatus() method.", aoExp);
			throw aoExp;
		}

		return loContractDiscResetResult;
	}

	/**
	 * This method removes the discrepancies in a Contract/Amendment (Updates
	 * own records in Contract type Contract and in Parent record if it is of
	 * Amendment Type)
	 * 
	 * <li>Updates Contract Start Date to FMS Contract Start Date if Discrepancy
	 * is in Contract Start Date</li> <li>Updates Contract End Date to FMS
	 * Contract End Date if Discrepancy is in Contract End Date</li> </ul>
	 * 
	 * @param aoMybatisSession Mybatis Session
	 * @param aoHMArgs - HashMap type object
	 * @return loContractDiscRemovalResult - Boolean type object
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes" })
	public Boolean removeContractDiscrepancies(SqlSession aoMybatisSession, HashMap aoHMArgs)
			throws ApplicationException
	{
		Boolean loContractDiscResetResult = Boolean.FALSE;
		ContractBean loContractBean = (ContractBean) aoHMArgs.get(HHSConstants.AO_CONTRACT_BEAN);
		try
		{
			setMoState("Removing Discrepancies of Contract/Amendment:" + loContractBean.getContractId() + "-'"
					+ loContractBean.getContractTitle() + "'.\n");

			DAOUtil.masterDAO(aoMybatisSession, loContractBean, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSConstants.REMOVE_CONTRACT_DISCREPANCIES, HHSConstants.CONTRACT_BEAN_PATH);
			loContractDiscResetResult = Boolean.TRUE;
		}
		catch (ApplicationException aoExp)
		{
			// Handle the ApplicationException type Exception and set moState
			// and context data
			setMoState("Error occured while Batch Process in removeContractDiscrepancies() method.\n");
			aoExp.addContextData("Exception occured while Batch Process in removeContractDiscrepancies() method.",
					aoExp);
			LOG_OBJECT.Error("Error occured while Batch Process in removeContractDiscrepancies() method.", aoExp);
			throw aoExp;
		}

		return loContractDiscResetResult;
	}

	/**
	 * This method Sets the Status of a Contract to 'Registered'
	 * 
	 * @param aoMybatisSession Mybatis Session
	 * @param aoHMArgs - HashMap type object
	 * @return loStatusResult - Boolean type object
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public Boolean setContractStatusAsRegistered(SqlSession aoMybatisSession, HashMap aoHMArgs)
			throws ApplicationException
	{
		Boolean loStatusResult = Boolean.FALSE;
		ContractBean loContractBean = (ContractBean) aoHMArgs.get(HHSConstants.AO_CONTRACT_BEAN);

		try
		{
			setMoState("Registering Contract: " + loContractBean.getContractId() + "-"
					+ loContractBean.getContractTitle() + ".\n");

			// Update the Contract Status to Registered)
			DAOUtil.masterDAO(aoMybatisSession, loContractBean, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSConstants.SET_CONTRACT_STATUS_AS_REGISTERED, HHSConstants.CONTRACT_BEAN_PATH);

			// If Amendment Type Contract is being registered
			if (loContractBean.getContractTypeId().equals(HHSConstants.CONTRACT_AMENDMENT_TYPE_ID))
			{
				// Send Notification for Registration of the Contract Amendment
				sendNotificationForAmendmentRegistration(aoMybatisSession, loContractBean);
			}
			else
			{
				// Mark the Underlying Approved Budgets as Active (For Base or
				// Renew type contracts)
				markApprovedBudgetsAsActive(aoMybatisSession, loContractBean);

				// Update the underlying Budgets EXT_CT_NUMBER as of it's
				// Contract
				updateBudgetsExtCtNumber(aoMybatisSession, loContractBean);
			}

			loStatusResult = Boolean.TRUE;
		}
		catch (ApplicationException aoExp)
		{
			// Handle the ApplicationException type Exception and set moState
			// and context data
			setMoState("Error occured while setting Contract Status as Registered for Batch Process in setContractStatusAsRegistered() method.\n");
			aoExp.addContextData(
					"Exception occured while setting Contract Status as Registered for Batch Process in setContractStatusAsRegistered() method.",
					aoExp);
			LOG_OBJECT
					.Error("Error occured while setting Contract Status as Registered for Batch Process in setContractStatusAsRegistered() method."
							+ aoExp);
			throw aoExp;
		}
		catch (Exception aoExp)
		{
			// Handle the Exception type exception and set moState
			// and context data
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while setting Contract Status as Registered for Batch Process in setContractStatusAsRegistered() method.",
					aoExp);
			setMoState("Error occured while setting Contract Status as Registered for Batch Process in setContractStatusAsRegistered() method.\n");
			loAppEx.addContextData(
					"Exception occured while setting Contract Status as Registered for Batch Process in setContractStatusAsRegistered() method.",
					loAppEx);
			LOG_OBJECT
					.Error("Error occured while setting Contract Status as Registered for Batch Process in setContractStatusAsRegistered() method."
							+ loAppEx);
			throw loAppEx;
		}

		return loStatusResult;
	}

	/**
	 * Updated in R7
     * This method Merges the line items (Budgets, sub- budgets and respective
	 * line items) the Status of a Contract into it's Base Contract (For
	 * Positive type Amendment)
	 * 
	 * <ul>
	 * <li>Merged the line items under a contract (Budgets under Contract)</li>
	 * <li>Sets the Status of the being merged Budgte as 'Approved'</li>
	 * <li>Modified this method for release 3.6.0 Enhancement id 6263</li>
	 * </ul>
	 * @param aoMybatisSession Mybatis Session
	 * @param aoHMArgs - HashMap type object
	 * @return loStatusResult - Boolean type object
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public Boolean mergeAmendmentInBaseContract(SqlSession aoMybatisSession, HashMap aoHMArgs)
			throws ApplicationException
	{   System.out.println(" ========ContractBatchService :: [mergeAmendmentInBaseContract] =========== ");
		Boolean loStatusResult = Boolean.FALSE;
		ContractBudgetBean loContractBudgetBean = null;

		try
		{
			ContractBean loContractBean = (ContractBean) aoHMArgs.get(HHSConstants.AO_CONTRACT_BEAN);
			P8UserSession loFilenetSession = (P8UserSession) aoHMArgs.get(HHSConstants.AO_FILENET_SESSION);

			setMoState("Merging Amendment Contract: '" + loContractBean.getContractId() + " : "
					+ loContractBean.getContractTitle() + "' into it's Base Contract.\n");

/* [Start] R8.10.0 QC9399    */

            FiscalDate  loAmdEndFy = new FiscalDate( loContractBean.getContractEndDate()  );
            FiscalDate  loBaseEndFy = new FiscalDate( loContractBean.getParentContractEndDate() );

            System.out.println("\n ------------ContractBatchService ["+ loContractBean.getContractId() +"]:: Merge term \n  [base Contract End date] " 
            + loBaseEndFy.toString() + "   [AMD End date] " + loAmdEndFy.toString()  );
			
            // For Zero value amendment - no merging needed Dates in Base
            // Contract
            if (new BigDecimal(loContractBean.getContractAmount()).compareTo(BigDecimal.ZERO) == HHSConstants.INT_ZERO)
            {
                System.out.println(" ------------Zero dollar AMD update contract End date");
                loStatusResult = Boolean.TRUE;
                  DAOUtil.masterDAO(aoMybatisSession, loContractBean, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
                        HHSConstants.MERGE_AMENDMENT_DATES_IN_BASE_CONTRACT, HHSConstants.CONTRACT_BEAN_PATH);
                return loStatusResult;
            }else {
    			if( loContractBean.getContractEndDate().after(loContractBean.getParentContractEndDate() )    ){
    			    System.out.println(" ------------non-Zero dollar AMD update contract End date");
    	            DAOUtil.masterDAO(aoMybatisSession, loContractBean, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
    	                    HHSConstants.MERGE_AMENDMENT_DATES_IN_BASE_CONTRACT, HHSConstants.CONTRACT_BEAN_PATH);
                } else {
                    System.out.println(" ------------non-Zero dollar AMD Does NOT update contract End date");
                }
            }

			// For Zero value amendment - no merging needed Dates in Base
			// Contract
/*			if (new BigDecimal(loContractBean.getContractAmount()).compareTo(BigDecimal.ZERO) == HHSConstants.INT_ZERO)
			{
				loStatusResult = Boolean.TRUE;
	              DAOUtil.masterDAO(aoMybatisSession, loContractBean, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
	                    HHSConstants.MERGE_AMENDMENT_DATES_IN_BASE_CONTRACT, HHSConstants.CONTRACT_BEAN_PATH);
				return loStatusResult;
			}
*/
            /* [End]  R8.10.0 QC9399  */

			HashMap loHMArgs = new HashMap();
			loHMArgs.put(HHSConstants.CONTRACT_ID, loContractBean.getContractId());
			List<ContractBudgetBean> loAffectedBudgets = (List<ContractBudgetBean>) DAOUtil.masterDAO(aoMybatisSession,
					loHMArgs, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_CONTRACT_BUDGETS,
					HHSConstants.JAVA_UTIL_HASH_MAP);

			if( loAffectedBudgets != null ) 
				System.out.println("\n ------------ContractBatchService :: [mergeAmendmentInBaseContract] loAffectedBudgets.size() : " +  loAffectedBudgets.size() + "'.\n");
			else
				System.out.println("\n ------------ContractBatchService :: [mergeAmendmentInBaseContract] no budget \n");

			TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
			loTaskDetailsBean.setContractId(loContractBean.getContractId());
			loTaskDetailsBean.setTaskName(HHSConstants.AUDIT_CONTRACT_BUDGET_AMENDMENT);
			loTaskDetailsBean.setUserId(HHSConstants.SYSTEM_USER);
			loTaskDetailsBean.setP8UserSession(loFilenetSession);
			// Initialize the count with zero for merging the contract - it
			// merges the contract only for first approved budget
			Integer loApprovedBudgetCount = HHSConstants.INT_ZERO;

			ContractBudgetModificationService loCBModificationService = new ContractBudgetModificationService();
			Iterator<ContractBudgetBean> loIteratorSett = loAffectedBudgets.iterator();

			String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_BUDGET_APPROVED);

			while (loIteratorSett.hasNext())
			{
				System.out.println("\n ------------ContractBatchService :: [mergeAmendmentInBaseContract] In while loop \n");
				loContractBudgetBean = loIteratorSett.next();
				loTaskDetailsBean.setBudgetId(loContractBudgetBean.getBudgetId());
				// added in R7: changes made for updation of PI indirect rate 
				String lsBudgetStatusId=loContractBudgetBean.getStatusId();
				if (null != lsBudgetStatusId && lsBudgetStatusId.equals(lsBudgetStatus))
				{
					LOG_OBJECT.Info("updating PI rate for Amendment having Budget Id: "
							+ loContractBudgetBean.getBudgetId() + " Budget Status Id: " + lsBudgetStatusId);
					
					System.out.println("\n ------------ContractBatchService :: updating PI rate for Amendment having Budget Id: "
							+ loContractBudgetBean.getBudgetId() + " Budget Status Id: " + lsBudgetStatusId);
					loTaskDetailsBean.setTaskStatus(loContractBudgetBean.getStatusId());
					ArrayList<SiteDetailsBean> loSubBudgetDetails = (ArrayList<SiteDetailsBean>) DAOUtil.masterDAO(
							aoMybatisSession, loContractBudgetBean.getBudgetId(),
							HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSR5Constants.FETCH_SUB_BUDGET_LIST_DETAILS_FOR_APPROVED_TASK,
							HHSConstants.JAVA_LANG_STRING);
					Boolean lsPIUpdateStatus = loCBModificationService.updatePIPercentforSubBudget(aoMybatisSession,
							loTaskDetailsBean, loSubBudgetDetails);
					setMoState("Updated PI Indirect Rate:" + lsPIUpdateStatus);
					
				}

				// End R7: changes for PI indirect rate updation
				System.out.println("\n ------------ContractBatchService :: mergeBudgetLineItemsForAmendment having Budget Id: "
						+ loTaskDetailsBean.getBudgetId() + " loApprovedBudgetCount: " + loApprovedBudgetCount);
				loCBModificationService.mergeBudgetLineItemsForAmendment(aoMybatisSession, loTaskDetailsBean,
						lsBudgetStatus, loApprovedBudgetCount);
				
				// increment it so that it should not attempt to merge the
				// contract more than once
				loApprovedBudgetCount++;
System.out.println("\n ------------ContractBatchService :: [mergeAmendmentInBaseContract] In while loop END \n");
			}
			// Release 3.6.0 Enhancement id 6263
			HashMap loHashMap = new HashMap();
			loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, loTaskDetailsBean.getContractId());
			// //changes for agency outbound interafce 6644
			loHashMap.put(HHSConstants.MODIFY_BY, HHSConstants.SYSTEM_USER);
			if (loApprovedBudgetCount == HHSConstants.INT_ZERO)
			{
System.out.println("\n ------------ContractBatchService ::[mergeAmendmentInBaseContract] into mergeBudgetLineItemsAmendmentNoBudgetEffected  \n");

				//update for PI indirect rate
				loCBModificationService.mergeBudgetLineItemsAmendmentNoBudgetEffected(aoMybatisSession, loHashMap,
						true, true);
			}

			setMoState("Merging Amendment Contract: '" + loContractBean.getContractId() + " : "
					+ loContractBean.getContractTitle() + "' into it's Base Contract finished successfully.\n");
            fetchAMDBaseDate(  aoMybatisSession, loContractBean.getContractId()); 
			loStatusResult = Boolean.TRUE;
		}
		catch (ApplicationException aoAppExp)
		{
			// Handle the ApplicationException type exception and set moState
			// and context data
			setMoState("Error occured while Batch Process in mergeAmendmentInBaseContract() method.\n");
			aoAppExp.addContextData("Exception occured while Batch Process in mergeAmendmentInBaseContract() method.",
					aoAppExp);
			LOG_OBJECT.Error("Error occured while Batch Process in mergeAmendmentInBaseContract() method.", aoAppExp);
			throw aoAppExp;
		}
		catch (Exception aoExp)
		{
			// Handle the Exception type exception and set moState
			// and context data
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while Batch Process in mergeAmendmentInBaseContract() method.", aoExp);
			setMoState("Error occured while Batch Process in mergeAmendmentInBaseContract() method.\n");
			loAppEx.addContextData("Exception occured while Batch Process in mergeAmendmentInBaseContract() method.",
					loAppEx);
			LOG_OBJECT.Error("Error occured while Batch Process in mergeAmendmentInBaseContract() method.", loAppEx);
			throw loAppEx;
		}
		return loStatusResult;
	}

	/**
	 * This method sets the required properties (in a HashMap) required for
	 * launching the Work Flow
	 * 
	 * @param aoMybatisSession Mybatis Session
	 * @param aoHMArgs - HashMap type object
	 * @return loHMWFRequiredProps - HashMap type object
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public HashMap setWFPropForContConfUpdateTask(SqlSession aoMybatisSession, HashMap aoHMArgs)
			throws ApplicationException
	{
		System.out.println("\n **************ContractBatchService :: setWFPropForContConfUpdateTask");
		HashMap loHMWFRequiredProps = new HashMap();
		String lsBaseContractId = null;
		ContractBean loContractBean = (ContractBean) aoHMArgs.get(HHSConstants.AO_CONTRACT_BEAN);
		try
		{
			setMoState("Setting Workflow Properties for launching 'Contract Configuration Update' task for Contract:"
					+ loContractBean.getContractId() + "-'" + loContractBean.getContractTitle() + "'.\n");

			// Set contract id of the Contract in arguments list (if COntract
			// type is Amendment then it's parent id is to be set)
			lsBaseContractId = getBaseContractIdOfContract(loContractBean);
			createContractUpdateRecordAndFYs(aoMybatisSession, loContractBean);

			// Set the required properties in HashMap for launching Contract
			// Configuration Task
			loHMWFRequiredProps.put(HHSConstants.CONTRACT_ID_WORKFLOW, lsBaseContractId);
			loHMWFRequiredProps.put(HHSConstants.SUBMITTED_BY, HHSConstants.SYSTEM_USER);
			loHMWFRequiredProps.put(HHSConstants.WORKFLOW_NAME, HHSConstants.WF_CONTRACT_CONFIGURATION_UPDATE);
		}
		catch (Exception aoExp)
		{
			ApplicationException loAppExp = new ApplicationException(
					"Exception occured while Batch Process in setWFPropForContConfUpdateTask() method", aoExp);
			// Handle the ApplicationException type Exception and set moState
			// and context data
			setMoState("Error occured while setting WF properties for Batch Process in setWFPropForContConfUpdateTask() method.\n");
			LOG_OBJECT
					.Error("Error occured while setting WF properties for Batch Process in setWFPropForContConfUpdateTask() method."
							+ aoExp);
			throw loAppExp;
		}

		return loHMWFRequiredProps;
	}

	/**
	 * This method sets the required properties (in a HashMap) required for
	 * launching the Work Flow and also make entries in the Contract and
	 * Contract Financials table for Update Contract Configuration task launch
	 * and to work upon
	 * 
	 * @param aoMybatisSession Mybatis Session
	 * @param aoContractBean - ContractBean type object
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public void createContractUpdateRecordAndFYs(SqlSession aoMybatisSession, ContractBean aoContractBean)
			throws ApplicationException
	{
		System.out.println("\n **************ContractBatchService :: createContractUpdateRecordAndFYs :: bean :: "+aoContractBean);
		HashMap loHMArgsFetchContDetails = new HashMap();
		HashMap loHMArgsFetchContFinancials = new HashMap();

		try
		{
			setMoState("Making a new (Update type)Contract entry for 'Contract Configuration Update Task' for :"
					+ aoContractBean.getContractId() + "-'" + aoContractBean.getContractTitle() + "'.\n");
			String lsBaseContractId = getBaseContractIdOfContract(aoContractBean);
			String lsContractId = aoContractBean.getContractId();
			loHMArgsFetchContDetails.put(HHSConstants.CONTRACT_ID, lsBaseContractId);
			// Get basic information of the Base contract to copy into Update
			// type Contract (to be inserted)
			EPinDetailBean loBaseContractDetails = (EPinDetailBean) DAOUtil.masterDAO(aoMybatisSession,
					loHMArgsFetchContDetails, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSConstants.FETCH_BASE_CONTRACT_DETAILS_FOR_UPDATE, HHSConstants.JAVA_UTIL_HASH_MAP);
			if (aoContractBean.getContractTypeId().equals(HHSConstants.CONTRACT_AMENDMENT_TYPE_ID))
			{
				// If Contract being processed is of Amendment type - update
				// it's parent Contract amount with additional Contract Amount
				// difference in FMS and Original

				BigDecimal loFinalContractAmount = new BigDecimal(aoContractBean.getParentContractAmount()).add(
						new BigDecimal(aoContractBean.getFmsContractAmount())).subtract(
						(new BigDecimal(aoContractBean.getContractAmount())));

				loBaseContractDetails.setContractValue(loFinalContractAmount.toString());
			}
			else
			{
				// For Contract of types Base and Renew just update it with FMS
				loBaseContractDetails.setContractValue(aoContractBean.getFmsContractAmount());
			}

			loBaseContractDetails.setParentContractId(lsBaseContractId);
			loBaseContractDetails.setContractStartDate(aoContractBean.getFmsContractStartDate());
			loBaseContractDetails.setContractEndDate(aoContractBean.getFmsContractEndDate());

			// Get newly inserted Contract's ContractId
			Integer loUpdateContractId = (Integer) DAOUtil.masterDAO(aoMybatisSession, null,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.GET_CONTRACT_SEQ_FROM_TABLE, null);
			loBaseContractDetails.setContractId(loUpdateContractId.toString());
			// Insert new record in DB for Update and get the new ContractId
			DAOUtil.masterDAO(aoMybatisSession, loBaseContractDetails, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSConstants.ADD_NEW_CONTRACT_FOR_UPDATE, HHSConstants.COM_NYC_HHS_MODEL_E_PIN_DETAIL_BEAN);
			setMoState("Making DB Entries in CONTRACT_FINANCIALS table for proposed FYs for Contract: "
					+ aoContractBean.getContractId() + "-'" + aoContractBean.getContractTitle() + "'.\n");
			fetchAndInsertContractFinancialDetails(aoMybatisSession, aoContractBean, loHMArgsFetchContFinancials,
					lsContractId, loUpdateContractId);
		}
		catch (ApplicationException aoAppExp)
		{
			// Handle the ApplicationException type exception and set moState
			// and context data
			setMoState("Error occured while Batch Process in createContractUpdateRecordAndFYs() method.\n");
			aoAppExp.addContextData(
					"Exception occured while Batch Process in createContractUpdateRecordAndFYs() method.", aoAppExp);
			LOG_OBJECT.Error("Error occured while Batch Process in createContractUpdateRecordAndFYs() method.",
					aoAppExp);
			throw aoAppExp;
		}
		catch (Exception aoExp)
		{
			ApplicationException loAppExp = new ApplicationException(
					"Exception occured while Batch Process in createContractUpdateRecordAndFYs() method", aoExp);
			// Handle the ApplicationException type Exception and set moState
			// and context data
			setMoState("Error occured while setting WF properties for Batch Process in createContractUpdateRecordAndFYs() method.\n");
			LOG_OBJECT
					.Error("Error occured while setting WF properties for Batch Process in createContractUpdateRecordAndFYs() method."
							+ aoExp);
			throw loAppExp;
		}
	}

	/**
	 * This method is used to fetch and insert contract financial details <li>
	 * The query used:fetchedContractFinancialDetails</li> <li>The query
	 * insertFetchedContractFinancialDetails</li>
	 * @param aoMybatisSession
	 * @param aoContractBean
	 * @param aoHMArgsFetchContFinancials
	 * @param aoContractId
	 * @param aoUpdateContractId
	 * @throws ApplicationException
	 */
	private void fetchAndInsertContractFinancialDetails(SqlSession aoMybatisSession, ContractBean aoContractBean,
			HashMap aoHMArgsFetchContFinancials, String aoContractId, Integer aoUpdateContractId)
			throws ApplicationException
	{
		Integer loContractStartFiscalYear = aoContractBean.getContractStartFiscalYear();
		Integer loFmsContractStartFiscalYear = aoContractBean.getFmsContractStartFiscalYear();
		Integer loFmsContractEndFiscalYear = aoContractBean.getFmsContractEndFiscalYear();

		aoHMArgsFetchContFinancials.put(HHSConstants.CONTRACT_ID, aoContractId);
		aoHMArgsFetchContFinancials.put(HHSConstants.FISCAL_YEAR, loContractStartFiscalYear.toString());

		// Fetch the list of Contract Financial entries for any year
		// (Start Year as of now) which is to be added again for the new
		// FY entry with changes in the FY
		// Defect fix 6398 in release 3.1.0
		List<ContractFinancialBean> loContractFinancialBeanList = (List<ContractFinancialBean>) DAOUtil.masterDAO(
				aoMybatisSession, aoHMArgsFetchContFinancials, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
				HHSConstants.CS_FETCH_CONTRACT_FINANCIAL_DETAILS_BATCH, HHSConstants.JAVA_UTIL_HASH_MAP);

		if (null != loContractFinancialBeanList && !loContractFinancialBeanList.isEmpty())
		{
			// Add the entries equal to the entries already made for
			// current Start FY (may it be taken for any fiscal year - it
			// would be same)
			Iterator<ContractFinancialBean> loIter = loContractFinancialBeanList.iterator();
			while (loIter.hasNext())
			{
				ContractFinancialBean loContractFinancialBean = loIter.next();
				loContractFinancialBean.setContractTypeId(HHSConstants.CONTRACT_UPDATE_TYPE_ID);
				// Set newly inserted ContractId
				loContractFinancialBean.setContractId(aoUpdateContractId.toString());
				loContractFinancialBean.setAmmount(HHSConstants.STRING_ZERO);
				// Defect fix 6398 in release 3.1.0
				DAOUtil.masterDAO(aoMybatisSession, loContractFinancialBean, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
						HHSConstants.CS_INSERT_FETCH_CONTRACT_FINANCIAL_DETAILS_BATCH,
						HHSConstants.CS_CONTRACT_FINANCIAL_BEAN);

			}
		}
	}

	/**
	 * This method fetched the Base Contract id of a contracts - Parent
	 * ContractId if contract is of type amendment else the contract id of the
	 * contract itself
	 * 
	 * @param aoContractBean - ContractBean type object
	 * @return lsBaseContractId - String type object
	 * @throws ApplicationException
	 */
	private String getBaseContractIdOfContract(ContractBean aoContractBean) throws ApplicationException
	{
		String lsBaseContractId = null;
		try
		{
			// Set contract id of the Contract in arguments list (if COntract
			// type is Amendment then it's parent id is to be set)
			if (aoContractBean.getContractTypeId().equals(HHSConstants.CONTRACT_BASE_TYPE_ID)
					|| aoContractBean.getContractTypeId().equals(HHSConstants.CONTRACT_RENEWAL_TYPE_ID))
			{
				lsBaseContractId = aoContractBean.getContractId();
			}
			else
			{
				lsBaseContractId = aoContractBean.getParentContractId();
			}
		}
		catch (Exception aoExp)
		{
			ApplicationException loAppExp = new ApplicationException(
					"Exception occured while Batch Process in getBaseContractIdOfContract() method", aoExp);
			// Handle the ApplicationException type Exception and set moState
			// and context data
			setMoState("Error occured while setting WF properties for Batch Process in getBaseContractIdOfContract() method.\n");
			LOG_OBJECT
					.Error("Error occured while setting WF properties for Batch Process in getBaseContractIdOfContract() method."
							+ aoExp);
			throw loAppExp;
		}
		return lsBaseContractId;

	}

	/**
	 * This method will update the CT# of the underlying Budgets as of it's own
	 * while Registering a Contract
	 * 
	 * @param aoMybatisSession Mybatis Session
	 * @param aoContractBean - ContractBean type object
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private void updateBudgetsExtCtNumber(SqlSession aoMybatisSession, ContractBean aoContractBean)
			throws ApplicationException
	{
		HashMap loHMArgs = new HashMap();

		try
		{
			setMoState("Updating CT# of Budgets of Contract:" + aoContractBean.getContractId() + "-'"
					+ aoContractBean.getContractTitle() + "'.\n");

			// Update the CT_CT_NUMBER of underlying Budgets
			loHMArgs.put(HHSConstants.CONTRACT_ID, aoContractBean.getContractId());
			loHMArgs.put(HHSConstants.EXT_CT_NUMBER, aoContractBean.getExtCtNumber());
			DAOUtil.masterDAO(aoMybatisSession, loHMArgs, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSConstants.UPDATE_BUDGETS_EXT_CT_NUMBER, HHSConstants.JAVA_UTIL_HASH_MAP);
		}
		catch (ApplicationException aoAppExp)
		{
			// Handle the ApplicationException type exception and set moState
			// and context data
			setMoState("Error occured while Batch Process in updateBudgetsExtCtNumber() method.\n");
			aoAppExp.addContextData("Exception occured while Batch Process in updateBudgetsExtCtNumber() method.",
					aoAppExp);
			LOG_OBJECT.Error("Error occured while Batch Process in updateBudgetsExtCtNumber() method.", aoAppExp);
			throw aoAppExp;
		}
		catch (Exception aoExp)
		{
			// Handle the Exception type exception and set moState
			// and context data
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while Batch Process in updateBudgetsExtCtNumber() method.", aoExp);
			setMoState("Error occured while Batch Process in updateBudgetsExtCtNumber() method.\n");
			loAppEx.addContextData("Exception occured while Batch Process in updateBudgetsExtCtNumber() method.",
					loAppEx);
			LOG_OBJECT.Error("Error occured while Batch Process in updateBudgetsExtCtNumber() method.", loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * This method will mark the underlying Budgets as 'Active' while
	 * Registering a Contract
	 * 
	 * @param aoMybatisSession Mybatis Session
	 * @param aoContractBean - ContractBean type object
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private void markApprovedBudgetsAsActive(SqlSession aoMybatisSession, ContractBean aoContractBean)
			throws ApplicationException
	{
		HashMap loHMArgs = new HashMap();
		List<ContractBudgetBean> loContractBudgetBeanList = null;

		try
		{
			// Get the Active Budgets of the Contract (Parent Base COntract if
			// it is Amendment Contract)
			loHMArgs.put(HHSConstants.CONTRACT_ID, getBaseContractIdOfContract(aoContractBean));
			loContractBudgetBeanList = (List<ContractBudgetBean>) DAOUtil.masterDAO(aoMybatisSession, loHMArgs,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.GET_APPROVED_BUDGETS_OF_CONTRACT,
					HHSConstants.JAVA_UTIL_HASH_MAP);

			setMoState("Fetching list of Approved Budgets of Contract:" + aoContractBean.getContractId() + "-'"
					+ aoContractBean.getContractTitle() + "'.\n");

			// Mark the Approved Budgets as Active
			// changes for agency outbound interafce 6641
			loHMArgs.put(HHSConstants.MODIFY_BY, HHSConstants.SYSTEM_USER);
			DAOUtil.masterDAO(aoMybatisSession, loHMArgs, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSConstants.MARK_APPROVED_BUDGETS_AS_ACTIVE, HHSConstants.JAVA_UTIL_HASH_MAP);

			// Insert audit records for Budgets tasks history
			insertAuditRecForBudgetActivation(aoMybatisSession, aoContractBean, loContractBudgetBeanList);

			setMoState("Marking Approved Budgets of Contract: " + aoContractBean.getContractId() + "-'"
					+ aoContractBean.getContractTitle() + "' as Active.\n");

			// Send Notifications for Budgets Activation (for each approved
			// budget - fetched before setting their status as Active)
			sendNotificationsForBudgetActivations(aoMybatisSession, aoContractBean, loContractBudgetBeanList);

		}
		catch (ApplicationException aoAppExp)
		{
			// Handle the ApplicationException type exception and set moState
			// and context data
			setMoState("Error occured while Batch Process in markApprovedBudgetsAsActive() method.\n");
			aoAppExp.addContextData("Exception occured while Batch Process in markApprovedBudgetsAsActive() method.",
					aoAppExp);
			LOG_OBJECT.Error("Error occured while Batch Process in markApprovedBudgetsAsActive() method.", aoAppExp);
			throw aoAppExp;
		}
		catch (Exception aoExp)
		{
			// Handle the Exception type exception and set moState
			// and context data
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while Batch Process in markApprovedBudgetsAsActive() method.", aoExp);
			setMoState("Error occured while Batch Process in markApprovedBudgetsAsActive() method.\n");
			loAppEx.addContextData("Exception occured while Batch Process in markApprovedBudgetsAsActive() method.",
					loAppEx);
			LOG_OBJECT.Error("Error occured while Batch Process in markApprovedBudgetsAsActive() method.", loAppEx);
			throw loAppEx;
		}

	}

	/**
	 * This method will insert the Audit records in the DB for Budgets getting
	 * Activated
	 * 
	 * @param aoMybatisSession Mybatis Session
	 * @param aoContractBean - ContractBean type object *
	 * @param aoContractBudgetBeanList - ContractBudgetBean (List) type object
	 * @throws ApplicationException
	 */
	private void insertAuditRecForBudgetActivation(SqlSession aoMybatisSession, ContractBean aoContractBean,
			List<ContractBudgetBean> aoContractBudgetBeanList) throws ApplicationException
	{
		try
		{
			// Check if the Approved Budgets list is empty
			if (null != aoContractBudgetBeanList && !aoContractBudgetBeanList.isEmpty())
			{
				ContractBudgetBean loContractBudgetBean = null;
				HhsAuditBean loHhsAuditBean = null;
				HhsAuditService loHHSAuditService = new HhsAuditService();

				Iterator<ContractBudgetBean> loIteratorSett = aoContractBudgetBeanList.iterator();
				while (loIteratorSett.hasNext())
				{
					loContractBudgetBean = loIteratorSett.next();

					setMoState("Inserting Audit Record for Budget Activation for (BudgetId-"
							+ loContractBudgetBean.getBudgetId() + " : FY-"
							+ loContractBudgetBean.getBudgetfiscalYear() + " of Contract-"
							+ aoContractBean.getContractId() + "-'" + aoContractBean.getContractTitle() + "'.\n");

					loHhsAuditBean = new HhsAuditBean();
					String lsStatusChange = HHSConstants.STATUS_CHANGED_FROM;

					StringBuilder loStatusChange = new StringBuilder();
					loStatusChange.append(lsStatusChange);
					loStatusChange.append(HHSConstants.SPACE);
					loStatusChange.append(HHSConstants.STR);
					loStatusChange.append(HHSConstants.STR_BUDGET_APPROVED);
					loStatusChange.append(HHSConstants.STR);
					loStatusChange.append(HHSConstants._TO_);
					loStatusChange.append(HHSConstants.STR);
					loStatusChange.append(HHSConstants.BUDGET_ACTIVE);
					loStatusChange.append(HHSConstants.STR);

					loHhsAuditBean = HHSUtil.addAuditDataToChannel(HHSConstants.STATUS_CHANGE,
							HHSConstants.STATUS_CHANGE, loStatusChange.toString(), HHSConstants.BUDGET_TYPE3,
							loContractBudgetBean.getBudgetId(), HHSConstants.SYSTEM_USER, HHSConstants.AGENCY_AUDIT);

					loHhsAuditBean.setEntityId(loContractBudgetBean.getBudgetId());
					loHhsAuditBean.setEntityType(HHSConstants.BUDGET_TYPE3);
					loHHSAuditService.hhsauditInsert(aoMybatisSession, loHhsAuditBean, Boolean.TRUE);
				}
			}
		}
		catch (ApplicationException aoAppExp)
		{
			// Handle the ApplicationException type exception and set moState
			// and context data
			setMoState("Error occured while Batch Process (inserting Audit records for budget activation) in insertAuditRecForBudgetActivation() method.\n");
			aoAppExp.addContextData(
					"Exception occured while Batch Process (inserting Audit records for budget activation) in insertAuditRecForBudgetActivation() method.",
					aoAppExp);
			LOG_OBJECT
					.Error("Error occured while Batch Process (inserting Audit records for budget activation) in insertAuditRecForBudgetActivation() method."
							+ aoAppExp);
			throw aoAppExp;
		}
		catch (Exception aoExp)
		{
			// Handle the Exception type exception and set moState
			// and context data
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while Batch Process (inserting Audit records for budget activation) in insertAuditRecForBudgetActivation() method.",
					aoExp);
			setMoState("Error occured while Batch Process (inserting Audit records for budget activation) in insertAuditRecForBudgetActivation() method.\n");
			loAppEx.addContextData(
					"Exception occured while Batch Process (inserting Audit records for budget activation) in insertAuditRecForBudgetActivation() method.",
					loAppEx);
			LOG_OBJECT
					.Error("Error occured while Batch Process (inserting Audit records for budget activation) in insertAuditRecForBudgetActivation() method."
							+ loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * This method will send the Notifications to the Provider (Level 2) for
	 * their Contract's Budgets getting Activated
	 * 
	 * <ul>
	 * <li>Traverses the List of Approved Budgets</li>
	 * <li>Send Alerts and Emails to the Providers (L2)</li>
	 * </ul>
	 * @param aoMybatisSession Mybatis Session
	 * @param aoContractBean - ContractBean type object *
	 * @param aoContractBudgetBeanList - ContractBudgetBean (List) type object
	 * @throws ApplicationException
	 */
	private void sendNotificationsForBudgetActivations(SqlSession aoMybatisSession, ContractBean aoContractBean,
			List<ContractBudgetBean> aoContractBudgetBeanList) throws ApplicationException
	{
		try
		{
			// Check if the Approved Budgets list is empty
			if (null != aoContractBudgetBeanList && !aoContractBudgetBeanList.isEmpty())
			{
				// Get the factors to prepare the URL for the Budget
				String lsServerName = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
						HHSConstants.SERVER_NAME_FOR_PROVIDER_BATCH);
				String lsServerPort = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
						HHSConstants.SERVER_PORT_FOR_PROVIDER_BATCH);
				String lsContextPath = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
						HHSConstants.CONTEXT_PATH_FOR_PROVIDER_BATCH);
				String lsAppProtocol = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
						HHSConstants.SERVER_PROTOCOL_FOR_PROVIDER_BATCH);

				sendNotificationAlertAndEmail(aoMybatisSession, aoContractBean, aoContractBudgetBeanList, lsServerName,
						lsServerPort, lsContextPath, lsAppProtocol);
			}
		}
		catch (ApplicationException aoAppExp)
		{
			// Handle the ApplicationException type exception and set moState
			// and context data
			setMoState("Error occured while Batch Process (sending notification for budget activation) in sendNotificationsForBudgetActivations() method.\n");
			aoAppExp.addContextData(
					"Exception occured while Batch Process (sending notification for budget activation) in sendNotificationsForBudgetActivations() method.",
					aoAppExp);
			LOG_OBJECT
					.Error("Error occured while Batch Process (sennding notification for budget activation) in sendNotificationsForBudgetActivations() method."
							+ aoAppExp);
			throw aoAppExp;
		}
		catch (Exception aoExp)
		{
			// Handle the Exception type exception and set moState
			// and context data
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while Batch Process (sending notification for budget activation) in sendNotificationsForBudgetActivations() method.",
					aoExp);
			setMoState("Error occured while Batch Process (sending notification for budget activation) in sendNotificationsForBudgetActivations() method.\n");
			loAppEx.addContextData(
					"Exception occured while Batch Process (sending notification for budget activation) in sendNotificationsForBudgetActivations() method.",
					loAppEx);
			LOG_OBJECT
					.Error("Error occured while Batch Process (sending notification for budget activation) in sendNotificationsForBudgetActivations() method."
							+ loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * This method is used to send alert and notification to providers.
	 * @param aoMybatisSession
	 * @param aoContractBean
	 * @param aoContractBudgetBeanList
	 * @param aoServerName
	 * @param aoServerPort
	 * @param aoContextPath
	 * @param asAppProtocol
	 * @throws ApplicationException
	 */
	private void sendNotificationAlertAndEmail(SqlSession aoMybatisSession, ContractBean aoContractBean,
			List<ContractBudgetBean> aoContractBudgetBeanList, String aoServerName, String aoServerPort,
			String aoContextPath, String asAppProtocol) throws ApplicationException
	{
		NotificationService loNotificationService = new NotificationService();
		HashMap<String, Object> loHMNotifyParam = null;
		ContractBudgetBean loContractBudgetBean = null;
		List<String> loAlertList = null;
		HashMap<String, Object> loRequestMap = null;
		StringBuffer loBfApplicationUrl = null;
		List<String> loProvidersList = new ArrayList<String>();

		// Send the Notifications (Alert and Email) to the designated
		// Providers
		Iterator<ContractBudgetBean> loIteratorSett = aoContractBudgetBeanList.iterator();
		while (loIteratorSett.hasNext())
		{
			loContractBudgetBean = loIteratorSett.next();
			loHMNotifyParam = new HashMap<String, Object>();
			loAlertList = new ArrayList<String>();
			loRequestMap = new HashMap<String, Object>();
			HashMap<String, String> loLinkMap = new HashMap<String, String>();
			loBfApplicationUrl = null;
			// Prepare the Link to the Contract Budget
			loBfApplicationUrl = new StringBuffer();
			loBfApplicationUrl.append(asAppProtocol);
			loBfApplicationUrl.append("://");
			loBfApplicationUrl.append(aoServerName);
			loBfApplicationUrl.append(":");
			loBfApplicationUrl.append(aoServerPort);
			loBfApplicationUrl.append("/");
			loBfApplicationUrl.append(aoContextPath);
			loBfApplicationUrl.append(HHSConstants.CONTRACT_BUDGET_ACTIVE_URL);
			loBfApplicationUrl.append("&contractId=");
			loBfApplicationUrl.append(aoContractBean.getContractId());
			loBfApplicationUrl.append("&budgetId=");
			loBfApplicationUrl.append(loContractBudgetBean.getBudgetId());
			loBfApplicationUrl.append("&fiscalYearId=");
			loBfApplicationUrl.append(loContractBudgetBean.getBudgetfiscalYear());

			// Add the Alert and Email Notifications
			loAlertList.add(HHSConstants.AL302);
			loAlertList.add(HHSConstants.NT302);

			loRequestMap.put(HHSConstants.NT_PROCUREMENT_TITLE, aoContractBean.getContractTitle());
			loRequestMap.put(HHSConstants.NT_CT, aoContractBean.getExtCtNumber());
			loLinkMap.put(HHSConstants.LINK, loBfApplicationUrl.toString());

			// Get the list of designated L2 Providers to whom the
			// notifications need to be sent
			loProvidersList.add(aoContractBean.getOrganizationId());
			NotificationDataBean loNotificationDataBean = new NotificationDataBean();

			loNotificationDataBean.setProviderList(loProvidersList);
			loNotificationDataBean.setAgencyLinkMap(loLinkMap);
			loNotificationDataBean.setLinkMap(loLinkMap);
			loHMNotifyParam.put(HHSConstants.AL302, loNotificationDataBean);
			loHMNotifyParam.put(HHSConstants.NT302, loNotificationDataBean);
			loHMNotifyParam.put(ApplicationConstants.ENTITY_ID, aoContractBean.getContractId());
			loHMNotifyParam.put(ApplicationConstants.ENTITY_TYPE, HHSConstants.CONTRACT);
			loHMNotifyParam.put(HHSConstants.CREATED_BY_USER_ID, HHSConstants.SYSTEM_USER);
			loHMNotifyParam.put(HHSConstants.MODIFIED_BY, HHSConstants.SYSTEM_USER);
			loHMNotifyParam.put(HHSConstants.NOTIFICATION_ALERT_ID, loAlertList);
			loHMNotifyParam.put(TransactionConstants.EVENT_ID_PARAMETER_NAME, loAlertList);
			loHMNotifyParam.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
			loHMNotifyParam.put(TransactionConstants.PROVIDER_ID, loProvidersList);

			setMoState("Sending Notifications for Activation of Budget(BudgetId-" + loContractBudgetBean.getBudgetId()
					+ " : FY-" + loContractBudgetBean.getBudgetfiscalYear() + " of Contract-"
					+ aoContractBean.getContractId() + "-'" + aoContractBean.getContractTitle() + "'.\n");

			loNotificationService.processNotification(aoMybatisSession, loHMNotifyParam);
		}
	}

	/**
	 * This method will send the Notifications to the Provider (Level 2) for
	 * their Contract's Budgets getting Activated
	 * 
	 * <ul>
	 * <li>Traverses the List of Approved Budgets</li>
	 * <li>Send Alerts and Emails to the Providers (L2)</li>
	 * </ul>
	 * @param aoMybatisSession Mybatis Session
	 * @param aoContractBean - ContractBean type object
	 * @throws ApplicationException
	 */
	private void sendNotificationForAmendmentRegistration(SqlSession aoMybatisSession, ContractBean aoContractBean)
			throws ApplicationException
	{
		List<String> loAgencyUserList = new ArrayList<String>();

		try
		{
			setMoState("Sending Notification for Registration of Amendment:" + aoContractBean.getContractId() + "-'"
					+ aoContractBean.getContractTitle() + "'.\n");

			String lsCityUrl = PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE,
					ApplicationConstants.PROPERTY_CITY_URL);

			NotificationService loNotificationService = new NotificationService();
			HashMap<String, Object> loHMNotifyParam = new HashMap<String, Object>();
			List<String> loAlertList = new ArrayList<String>();
			HashMap<String, Object> loRequestMap = new HashMap<String, Object>();
			StringBuffer loBfApplicationUrl = new StringBuffer(256);

			NotificationDataBean loNotificationDataBean = new NotificationDataBean();
			HashMap<String, String> loLinkMap = new HashMap<String, String>();

			// Prepare the Link to the Contract Budget
			loBfApplicationUrl.append(lsCityUrl);
			loBfApplicationUrl.append(HHSConstants.CONTRACT_LIST_URL);

			// Add the Alert and Email Notifications
			loAlertList.add(HHSConstants.AL320);
			loHMNotifyParam.put(HHSConstants.NOTIFICATION_ALERT_ID, loAlertList);
			loRequestMap.put(HHSConstants.NT_PROCUREMENT_TITLE, aoContractBean.getContractTitle());
			loRequestMap.put(HHSConstants.NT_CT, aoContractBean.getParentExtCtNumber());
			loRequestMap.put(HHSConstants.AWARD_E_PIN, aoContractBean.getAwardEpin());
			loLinkMap.put(HHSConstants.LINK, loBfApplicationUrl.toString());
			loNotificationDataBean.setLinkMap(loLinkMap);
			loNotificationDataBean.setAgencyLinkMap(loLinkMap);
			loNotificationDataBean.setAgencyList(loAgencyUserList);
			loHMNotifyParam.put(HHSConstants.AL320, loNotificationDataBean);
			loHMNotifyParam.put(HHSConstants.CREATED_BY_USER_ID, HHSConstants.SYSTEM_USER);
			loHMNotifyParam.put(HHSConstants.MODIFIED_BY, HHSConstants.SYSTEM_USER);
			// Get the list of designated L2 Providers to whom the
			// notifications need to be sent
			loAgencyUserList.add(aoContractBean.getAgencyId());

			loHMNotifyParam.put(TransactionConstants.EVENT_ID_PARAMETER_NAME, loAlertList);
			loHMNotifyParam.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
			loHMNotifyParam.put(TransactionConstants.AGENCY_ID, loAgencyUserList);
			loHMNotifyParam.put(ApplicationConstants.ENTITY_ID, aoContractBean.getContractId());
			loHMNotifyParam.put(ApplicationConstants.ENTITY_TYPE, HHSConstants.CONTRACT_AMENDMENT);

			loNotificationService.processNotification(aoMybatisSession, loHMNotifyParam);
		}
		catch (ApplicationException aoAppExp)
		{
			// Handle the ApplicationException type exception and set moState
			// and context data
			setMoState("Error occured while Batch Process (sending notification for budget activation) in sendNotificationForAmendmentRegistration() method.\n");
			aoAppExp.addContextData(
					"Exception occured while Batch Process (sending notification for budget activation) in sendNotificationForAmendmentRegistration() method.",
					aoAppExp);
			LOG_OBJECT
					.Error("Error occured while Batch Process (sennding notification for budget activation) in sendNotificationForAmendmentRegistration() method."
							+ aoAppExp);
			throw aoAppExp;
		}
		catch (Exception aoExp)
		{
			// Handle the Exception type exception and set moState
			// and context data
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while Batch Process (sending notification for budget activation) in sendNotificationForAmendmentRegistration() method.",
					aoExp);
			setMoState("Error occured while Batch Process (sending notification for budget activation) in sendNotificationForAmendmentRegistration() method.\n");
			loAppEx.addContextData(
					"Exception occured while Batch Process (sending notification for budget activation) in sendNotificationForAmendmentRegistration() method.",
					loAppEx);
			LOG_OBJECT
					.Error("Error occured while Batch Process (sending notification for budget activation) in sendNotificationForAmendmentRegistration() method."
							+ loAppEx);
			throw loAppEx;
		}
	}

	/* Start R4 Invoice Batch */
	/**
	 * This method will fetch Data from Invoice table which is processed from
	 * ETL
	 * 
	 * <ul>
	 * <li>get information from invoice table by calling Service :
	 * fetchApproveInvoiceFromETL</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession object
	 * @param aoHashMap - HashMap type object
	 * @return loInvoiceDetails - List of HashMap type object
	 * @throws ApplicationException if any exception occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public List<HashMap> fetchApproveInvoiceFromETL(SqlSession aoMybatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		List<HashMap> loInvoiceDetails = null;
		try
		{
			loInvoiceDetails = (List<HashMap>) DAOUtil.masterDAO(aoMybatisSession, null,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_APPROVAL_INVOICE_FROM_ETL, null);
			if (null == loInvoiceDetails)
			{
				return new ArrayList<HashMap>();
			}
		}
		catch (ApplicationException aoExp)
		{
			// Handle the ApplicationException type Exception and set moState
			// and context data
			setMoState("Error occured while Batch Process in fetchApproveInvoiceFromETL() method\n");
			aoExp.addContextData("Exception occured while Batch Process in fetchApproveInvoiceFromETL() method", aoExp);
			LOG_OBJECT.Error("Error occured while Batch Process in fetchApproveInvoiceFromETL() method", aoExp);
			throw aoExp;
		}
		catch (Exception aoExp)
		{
			// Handle the Exception type exception and set moState
			// and context data
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while Batch Process in fetchApproveInvoiceFromETL() method", aoExp);
			setMoState("Error occured while Batch Process in fetchApproveInvoiceFromETL() method\n");
			loAppEx.addContextData("Exception occured while Batch Process in fetchApproveInvoiceFromETL() method",
					loAppEx);
			LOG_OBJECT.Error("Error occured while Batch Process in fetchApproveInvoiceFromETL() method", loAppEx);
			throw loAppEx;
		}

		return loInvoiceDetails;
	}

	/**
	 * This method will update Invoice table once batch is done for a particular
	 * row
	 * 
	 * <ul>
	 * <li>update invoice table calling Service : updateInvoiceIdForBatch</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession object
	 * @param aoHashMap - HashMap object
	 * @return loStatus - Boolean type object
	 * @throws ApplicationException if any exception occurs
	 */
	@SuppressWarnings(
	{ "rawtypes" })
	public Boolean updateInvoiceIdForBatch(SqlSession aoMybatisSession, HashMap aoHashMap) throws ApplicationException
	{
		Boolean loStatus = Boolean.FALSE;
		try
		{
			DAOUtil.masterDAO(aoMybatisSession, aoHashMap, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
					HHSConstants.UPDATE_INVOICE_FOR_BATCH, HHSConstants.JAVA_UTIL_HASH_MAP);
			loStatus = Boolean.TRUE;
		}
		catch (ApplicationException aoExp)
		{
			// Handle the ApplicationException type Exception and set moState
			// and context data
			setMoState("Error occured while Batch Process in updateInvoiceIdForBatch() method\n");
			aoExp.addContextData("Exception occured while Batch Process in updateInvoiceIdForBatch() method", aoExp);
			LOG_OBJECT.Error("Error occured while Batch Process in updateInvoiceIdForBatch() method", aoExp);
			throw aoExp;
		}
		catch (Exception aoExp)
		{
			// Handle the Exception type exception and set moState
			// and context data
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while Batch Process in updateInvoiceIdForBatch() method", aoExp);
			setMoState("Error occured while Batch Process in updateInvoiceIdForBatch() method\n");
			loAppEx.addContextData("Exception occured while Batch Process in updateInvoiceIdForBatch() method", loAppEx);
			LOG_OBJECT.Error("Error occured while Batch Process in updateInvoiceIdForBatch() method", loAppEx);
			throw loAppEx;
		}

		return loStatus;
	}

	/**
	 * This method will fetch Data from DB which is processed from ETL
	 * 
	 * <ul>
	 * <li>get information from budget, contract, budget_advance table calling
	 * Service : fetchBudgetAdvanceFromETL</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession object
	 * @return loInvoiceDetails - List of HashMap type object
	 * @throws ApplicationException if any exception occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public List<HashMap> fetchBudgetAdvanceFromETL(SqlSession aoMybatisSession) throws ApplicationException
	{
		List<HashMap> loBudgetAdvanceDetails = null;
		try
		{
			loBudgetAdvanceDetails = (List<HashMap>) DAOUtil.masterDAO(aoMybatisSession, null,
					HHSConstants.MAPPER_CLASS_BUDGET_MAPPER, HHSConstants.FETCH_BUDGET_ADVANCE_FROM_ETL, null);
			if (null == loBudgetAdvanceDetails)
			{
				return new ArrayList<HashMap>();
			}
		}
		catch (ApplicationException aoExp)
		{
			// Handle the ApplicationException type Exception and set moState
			// and context data
			setMoState("Error occured while Batch Process in fetchBudgetAdvanceFromETL() method\n");
			aoExp.addContextData("Exception occured while Batch Process in fetchBudgetAdvanceFromETL() method", aoExp);
			LOG_OBJECT.Error("Error occured while Batch Process in fetchBudgetAdvanceFromETL() method", aoExp);
			throw aoExp;
		}
		catch (Exception aoExp)
		{
			// Handle the Exception type exception and set moState
			// and context data
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while Batch Process in fetchBudgetAdvanceFromETL() method", aoExp);
			setMoState("Error occured while Batch Process in fetchBudgetAdvanceFromETL() method\n");
			loAppEx.addContextData("Exception occured while Batch Process in fetchBudgetAdvanceFromETL() method",
					loAppEx);
			LOG_OBJECT.Error("Error occured while Batch Process in fetchBudgetAdvanceFromETL() method", loAppEx);
			throw loAppEx;
		}

		return loBudgetAdvanceDetails;
	}

	/**
	 * This method will update Invoice table once batch is done for a particular
	 * row
	 * 
	 * <ul>
	 * <li>update budget_advance table calling Service :
	 * updateBudgetAdvanceIdForBatch</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession object
	 * @param aoHMWFRequiredProps HashMap object
	 * @return loStatus - Boolean type object
	 * @throws ApplicationException if any exception occurs
	 */
	@SuppressWarnings(
	{ "rawtypes" })
	public Boolean updateBudgetAdvanceIdForBatch(SqlSession aoMybatisSession, HashMap aoHMWFRequiredProps)
			throws ApplicationException
	{
		Boolean loStatus = Boolean.TRUE;
		try
		{
			DAOUtil.masterDAO(aoMybatisSession, aoHMWFRequiredProps, HHSConstants.MAPPER_CLASS_BUDGET_MAPPER,
					HHSConstants.UPDATE_BUDGET_ADVANCE_ID_FOR_BATCH, HHSConstants.JAVA_UTIL_HASH_MAP);
		}
		catch (ApplicationException aoExp)
		{
			// Handle the ApplicationException type Exception and set moState
			// and context data
			setMoState("Error occured while Batch Process in updateBudgetAdvanceIdForBatch() method\n");
			aoExp.addContextData("Exception occured while Batch Process in updateBudgetAdvanceIdForBatch() method",
					aoExp);
			LOG_OBJECT.Error("Error occured while Batch Process in updateBudgetAdvanceIdForBatch() method", aoExp);
			throw aoExp;
		}
		catch (Exception aoExp)
		{
			// Handle the Exception type exception and set moState
			// and context data
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while Batch Process in updateBudgetAdvanceIdForBatch() method", aoExp);
			setMoState("Error occured while Batch Process in updateBudgetAdvanceIdForBatch() method\n");
			loAppEx.addContextData("Exception occured while Batch Process in updateBudgetAdvanceIdForBatch() method",
					loAppEx);
			LOG_OBJECT.Error("Error occured while Batch Process in updateBudgetAdvanceIdForBatch() method", loAppEx);
			throw loAppEx;
		}

		return loStatus;
	}

	/**
	 * Release 3.12.0 Enhancement 6601
	 * @param aoMybatisSession
	 * @return
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public List<ContractList> getAmendmentRegisterdInFms(SqlSession aoMybatisSession, HashMap asHMReqdProp)
			throws ApplicationException
	{
		List<ContractList> loList = new ArrayList<ContractList>();

		try
		{
			loList = (List<ContractList>) DAOUtil.masterDAO(aoMybatisSession, asHMReqdProp,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.GET_AMENDMENT_REGISTERED_IN_FMS,
					HHSConstants.JAVA_UTIL_HASH_MAP);
		}
		catch (ApplicationException aoExp)
		{
			// Handle the ApplicationException type Exception and set moState
			// and context data
			setMoState("Error occured while Batch Process in getAmendmentRegisterdInFms() method\n");
			aoExp.addContextData("Exception occured while Batch Process in getAmendmentRegisterdInFms() method", aoExp);
			LOG_OBJECT.Error("Error occured while Batch Process in getAmendmentRegisterdInFms() method", aoExp);
			throw aoExp;
		}
		return loList;
	}

	/**
	 * Updated in R7 for Cost Center to copy out-year configured services in base
	 * @param aoMybatisSession
	 * @param asHMReqdProp
	 * @param loList
	 * @throws ApplicationException
	 * @throws ParseException 
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked", "unused" })
	public Boolean updateAndPartialMergeAmendmentRegisteredInFMS(P8UserSession aoFilenetSession,
			SqlSession aoMybatisSession, HashMap asHMReqdProp, String asContractId) throws ApplicationException, ParseException
	{
		Boolean loStatus = false;
		try
		{
			System.out.println(" ====ContractsBatchService :: updateAndPartialMergeAmendmentRegisteredInFMS ");
			TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();

			loTaskDetailsBean.setTaskName(HHSConstants.AUDIT_CONTRACT_BUDGET_AMENDMENT);
			loTaskDetailsBean.setUserId(HHSConstants.SYSTEM_USER);
			loTaskDetailsBean.setContractId(asContractId);
			List<ContractBudgetBean> loAffectedBudgets = (List<ContractBudgetBean>) DAOUtil.masterDAO(aoMybatisSession,
					asHMReqdProp, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSConstants.FETCH_AMENDMENT_CONTRACT_BUDGETS, HHSConstants.JAVA_UTIL_HASH_MAP);
			
			System.out.println(" ====ContractsBatchService :: loAffectedBudgets :: "+loAffectedBudgets);
			
			List<String> loAmendmentBudgetIds = new ArrayList<String>();
			for (ContractBudgetBean loContractBudgetBean : loAffectedBudgets)
			{
				loAmendmentBudgetIds.add(loContractBudgetBean.getBudgetfiscalYear());
			}
			System.out.println(" ====ContractsBatchService :: loAmendmentBudgetIds :: "+loAmendmentBudgetIds);
			ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
			System.out.println(" ====ContractsBatchService invoke :: loContractBudgetModificationService.mergeOnlyConfigurationForAmendment ");
			loContractBudgetModificationService.mergeOnlyConfigurationForAmendment(aoMybatisSession, loTaskDetailsBean,
					loAmendmentBudgetIds);
			
			// start release 3.14.0

			// Fetch Parent ContractId
			Map loParentHashMap = new HashMap();
			loParentHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, asContractId);
			String lsParentContractId = (String) DAOUtil.masterDAO(aoMybatisSession, loParentHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.FETCH_PARENT_CONTRACT_ID, HHSConstants.JAVA_UTIL_MAP);
			ContractBudgetBean loContractBudgetBean = new ContractBudgetBean();
			loContractBudgetBean.setContractId(lsParentContractId);
			loContractBudgetBean.setAmendmentContractId(asContractId);
			
           
			// Start QC 9388 R 8.4 Update the last active FiscalYeaer Budget with proper End date
			ContractBudgetBean lastBasedApprovedFYBudgetBean = null;
			lastBasedApprovedFYBudgetBean = (ContractBudgetBean) DAOUtil.masterDAO(aoMybatisSession, lsParentContractId,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_LAST_BASE_APPROVED_FY,
					HHSConstants.JAVA_LANG_STRING);
			//@TO DO
			
			if(lastBasedApprovedFYBudgetBean!=null)
			{
				ContractBean baseContractBean = null;
				baseContractBean = (ContractBean) DAOUtil.masterDAO(aoMybatisSession, lsParentContractId,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_BASE_CONTRACT_START_END_DATE,
					HHSConstants.JAVA_LANG_STRING);
				
				// String -> Date
				Date dateBaseBudgetEnd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(lastBasedApprovedFYBudgetBean.getBudgetEndDate());
				System.out.println("=======dateBaseBudgetEnd :: " +dateBaseBudgetEnd);
				// Date -> String
				String stringBaseBudgetEndDate = new SimpleDateFormat("MM/dd/yyyy").format(dateBaseBudgetEnd);
				System.out.println("=======string BaseBudgetEnd :: " +stringBaseBudgetEndDate);
				
			 	// Get calculated Fiscal Years for Contrast as per start date and end date
			 	Date dateContractStart = DateUtil.getSqlDate(baseContractBean.getContractStartDate());
				System.out.println("===============dateContractStart :: " + dateContractStart);
				Date dateContractEnd = DateUtil.getSqlDate(baseContractBean.getContractEndDate());
				System.out.println("========Contract End Date :: " + dateContractEnd);
				
				HashMap<String, Integer> loContractFYDetails = null;
				loContractFYDetails = HHSUtil.getFirstAndLastFYOfContract(dateContractStart, dateContractEnd);
				Integer loContractEndFiscalYear = loContractFYDetails.get(HHSConstants.CONTRACT_END_FY);
				Integer loBudgetFiscalYear = Integer.valueOf(lastBasedApprovedFYBudgetBean.getBudgetfiscalYear());
				System.out.println("==========CONTRACT_END_FY :: " + loContractEndFiscalYear);
				System.out.println("==========BUDGET_FY :: " + loBudgetFiscalYear);
				System.out.println("=======string BaseBudgetEndDate :: " +stringBaseBudgetEndDate);
				//Date dateBudgetEnd = DateUtil.getSqlDate(lastBasedApprovedFYBudgetBean.getBudgetEndDate()); 
				Date dateBudgetEnd = DateUtil.getSqlDate(stringBaseBudgetEndDate);
				System.out.println("====== Budget End  Date :: " + dateBudgetEnd);
				
				Map loHashMap = new HashMap();
				loHashMap.put(HHSConstants.RSLT_BUDGET_ID, lastBasedApprovedFYBudgetBean.getBudgetId());
				loHashMap.put(HHSConstants.BASE_LAST_MODIFIED_BY, HHSConstants.SYSTEM_USER);
				
				
				String budgetEndDateCalculated = HHSConstants.FISCAL_YEAR_END_DATE + lastBasedApprovedFYBudgetBean.getBudgetfiscalYear();
				System.out.println("==========budgetEndDateCalculated :: " + budgetEndDateCalculated);
				
				if( loBudgetFiscalYear < loContractEndFiscalYear)
				{
					
					if(!budgetEndDateCalculated.equalsIgnoreCase(stringBaseBudgetEndDate))
					{	
						Calendar loBudgetEndDate = Calendar.getInstance();
						SimpleDateFormat losdf = new SimpleDateFormat("yyyy-MM-dd");
						Date ldBudgetEndDate=null;
						
						String[] lsBudgetEndDateArr = budgetEndDateCalculated.split(HHSConstants.FORWARD_SLASH);
						loBudgetEndDate.set(Calendar.MONTH,Integer.parseInt(lsBudgetEndDateArr[0])-1);
						loBudgetEndDate.set(Calendar.DATE,Integer.parseInt(lsBudgetEndDateArr[1]));
						loBudgetEndDate.set(Calendar.YEAR,Integer.parseInt(lsBudgetEndDateArr[2]));
						loBudgetEndDate.set(Calendar.HOUR_OF_DAY,0);
					    loBudgetEndDate.set(Calendar.MINUTE,0);
					    loBudgetEndDate.set(Calendar.SECOND,0);
					    loBudgetEndDate.set(Calendar.MILLISECOND,0);
						ldBudgetEndDate=losdf.parse(losdf.format(loBudgetEndDate.getTime()));
						
						// update base budget end date with calculated budget end date
						// don't be confused by name of transaction
						loHashMap.put(HHSConstants.BASE_CONTRACT_BUDGET_END_DATE, ldBudgetEndDate);
						DAOUtil.masterDAO(aoMybatisSession, loHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
								HHSConstants.BUDGET_END_DATE_UPDATED_IN_AMENDMENT, HHSConstants.JAVA_UTIL_HASH_MAP);
						}
				}
				else if(loBudgetFiscalYear == loContractEndFiscalYear && 
					   (dateBudgetEnd.after(dateContractEnd) || dateBudgetEnd.before(dateContractEnd)) )
				{
					// update base budget end date with contract end date
					// don't be confused by name of transaction
					loHashMap.put(HHSConstants.BASE_CONTRACT_BUDGET_END_DATE, baseContractBean.getContractEndDate());
					DAOUtil.masterDAO(aoMybatisSession, loHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
							HHSConstants.BUDGET_END_DATE_UPDATED_IN_AMENDMENT, HHSConstants.JAVA_UTIL_HASH_MAP);
				}
			} 
						
			// End QC 9388 R 8.4 Update the last active FiscalYeaer Budget with proper End date
			
			
			Integer liLastConfiguredFY = (Integer) DAOUtil.masterDAO(aoMybatisSession, lsParentContractId,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_LAST_FY_CONFIGURED_AND_NEXT_NEW_FY,
					HHSConstants.JAVA_LANG_STRING);
			liLastConfiguredFY = liLastConfiguredFY + 1;
			loContractBudgetBean.setBudgetfiscalYear(liLastConfiguredFY.toString());
			List<ContractBudgetBean> loBudgetAmendmentRegisteredInFMS = (List<ContractBudgetBean>) DAOUtil.masterDAO(
					aoMybatisSession, loContractBudgetBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.BUDGET_AMENDMENT_REGISTERED_FMS, HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
			if (loBudgetAmendmentRegisteredInFMS != null && !loBudgetAmendmentRegisteredInFMS.isEmpty())
			{
				for (ContractBudgetBean loBudgetAmendmentRegisteredInFMSBean : loBudgetAmendmentRegisteredInFMS)
				{
					Map loHashMap = new HashMap();
					loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW,
							loBudgetAmendmentRegisteredInFMSBean.getContractId());
					loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, loBudgetAmendmentRegisteredInFMSBean.getBudgetId());
					loHashMap.put(HHSConstants.MODIFY_BY, loContractBudgetBean.getModifiedByUserId());
					loHashMap.put(HHSConstants.FISCAL_YEAR_ID_KEY, loContractBudgetBean.getBudgetfiscalYear());
					loHashMap.put(HHSConstants.PARENT_CONTRACT_ID, loContractBudgetBean.getContractId());

					// Update Amount for Base Sub_Budget using Amendment Amount
					DAOUtil.masterDAO(aoMybatisSession, loHashMap,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
							HHSConstants.CBM_UPDATE_BASE_SUBBUDGET_AMOUNT, HHSConstants.JAVA_UTIL_MAP);
					// Update Amount for Base Budget using Amendment Amount
					DAOUtil.masterDAO(aoMybatisSession, loHashMap,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
							HHSConstants.CBM_UPDATE_BASE_BUDGET_AMOUNT, HHSConstants.JAVA_UTIL_MAP);

					// Logical deletion of all Modification Sub_Budget
					DAOUtil.masterDAO(aoMybatisSession, loHashMap,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
							HHSConstants.CBM_DELETE_SUBBUDGET_REPLICA, HHSConstants.JAVA_UTIL_MAP);

					// Logical Deletion of Budget
					DAOUtil.masterDAO(aoMybatisSession, loHashMap,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
							HHSConstants.CBM_DELETE_BUDGET_REPLICA, HHSConstants.JAVA_UTIL_MAP);

					// merge customization
					FinancialsService loFinancialsService = new FinancialsService();
					HashMap loHMArgs = new HashMap();
					ContractBean loContractBean = new ContractBean();
					loContractBean.setContractId(loBudgetAmendmentRegisteredInFMSBean.getContractId());
					loContractBean.setParentContractId(loContractBudgetBean.getContractId());
					loContractBean.setAmendAffectedBudgetId(loBudgetAmendmentRegisteredInFMSBean.getBudgetId());
					loHMArgs.put(HHSConstants.AO_CONTRACT_BEAN, loContractBean);
					loFinancialsService.mergeBaseBudCustomization(aoMybatisSession, loHMArgs);

					loBudgetAmendmentRegisteredInFMSBean.setModifiedByUserId(HHSConstants.SYSTEM_USER);
					DAOUtil.masterDAO(aoMybatisSession, loBudgetAmendmentRegisteredInFMSBean,
							HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.UPDATE_BUDGET_CUSTOMIZATION_ON_NEW_FY_BUDGET,
							HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
					// Start:Added in R7 for Cost Center to copy out-year configured services in base
					HashMap loMap = new HashMap();
					loMap.put(HHSConstants.BUDGET_ID_WORKFLOW, loBudgetAmendmentRegisteredInFMSBean.getBudgetId());
					loMap.put(HHSConstants.USER_ID, HHSConstants.SYSTEM_USER);
					DAOUtil.masterDAO(aoMybatisSession, loMap,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
							HHSR5Constants.CBY_INSERT_SERVICES_TO_BASE_CONFIGURATION, HHSConstants.JAVA_UTIL_MAP);
					LOG_OBJECT.Info("Finished copying out-year configured services in base");
					// End:Added in R7 for Cost Center to copy out-year configured services in base
				}
			}

			DAOUtil.masterDAO(aoMybatisSession, loContractBudgetBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.CS_UPDATE_AMENDMENT_REGISTERED_IN_FMS_BUDGET_STATUS_NEXT_NEW_FY,
					HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
			

			// end release 3.14.0
			// Fetch Parent ContractId
			/*
			 * //start release 3.14.0
			 * asHMReqdProp.put(HHSConstants.CONTRACT_ID_WORKFLOW,
			 * asContractId); String lsParentContractId = (String)
			 * DAOUtil.masterDAO(aoMybatisSession, asHMReqdProp,
			 * HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
			 * HHSConstants.FETCH_PARENT_CONTRACT_ID,
			 * HHSConstants.JAVA_UTIL_MAP);
			 * asHMReqdProp.put(HHSConstants.PARENT_CONTRACT_ID,
			 * lsParentContractId);
			 * asHMReqdProp.put(HHSConstants.LO_AMENDMENT_BUDGET_LIST,
			 * loAmendmentBudgetIds); List<ContractBudgetBean>
			 * loMismatchContractFinancialsAndBudgetAmount =
			 * (List<ContractBudgetBean>) DAOUtil.masterDAO(aoMybatisSession,
			 * asHMReqdProp, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
			 * HHSConstants.MISMATCH_CONTRACT_FINANCIALS_AND_BUDGET_AMOUNT,
			 * HHSConstants.JAVA_UTIL_HASH_MAP);
			 * if(loMismatchContractFinancialsAndBudgetAmount!=null &&
			 * !loMismatchContractFinancialsAndBudgetAmount.isEmpty()) { for
			 * (ContractBudgetBean loContractBudgetBean :
			 * loMismatchContractFinancialsAndBudgetAmount) { // Set the
			 * required properties in HashMap for launching Contract //
			 * Configuration Task
			 * 
			 * NotificationService loNotificationService = new
			 * NotificationService();
			 * loNotificationService.processNotification(aoMybatisSession,
			 * getNotificationMapForNT413(lsParentContractId,
			 * HHSConstants.SYSTEM_USER,aoMybatisSession,loContractBudgetBean));
			 * } }//end release 3.14.0
			 */
			DAOUtil.masterDAO(aoMybatisSession, asHMReqdProp, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSConstants.UPDATE_FLAG_AMENDMENT_REGISTERED_IN_FMS, HHSConstants.JAVA_UTIL_HASH_MAP);
			loStatus = true;
			
			// Start: Added in R7 for defect 8644
            DAOUtil.masterDAO(aoMybatisSession, loTaskDetailsBean.getContractId(),
                        HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSR5Constants.UPDATE_PARTIAL_MERGE_ACTION,
                        HHSConstants.JAVA_LANG_STRING);
            // End: Added in R7 for Defect 8644
            
            
		}
		catch (ApplicationException aoExp)
		{
			// Handle the ApplicationException type Exception and set moState
			// and context data
			setMoState("Error occured while Batch Process in updateAndPartialMergeAmendmentRegisteredInFMS() method\n");
			aoExp.addContextData(
					"Exception occured while Batch Process in updateAndPartialMergeAmendmentRegisteredInFMS() method",
					aoExp);
			LOG_OBJECT.Error(
					"Error occured while Batch Process in updateAndPartialMergeAmendmentRegisteredInFMS() method",
					aoExp);
			throw aoExp;
		}
		return loStatus;
	}

	/**
	 * Release 3.12.0 Enhancement 6601
	 * @param aoMybatisSession
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Boolean markAmendmentETLRegistredWhichAreRegisteredInFMS(SqlSession aoMybatisSession, HashMap asHMReqdProp)
			throws ApplicationException
	{
		
		System.out.println("=======[markAmendmentETLRegistredWhichAreRegisteredInFMS]: set AMD status 118 ");
		Boolean loStatus = false;
		try
		{
			DAOUtil.masterDAO(aoMybatisSession, asHMReqdProp, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSConstants.MARK_AMENDMENT_ETL_REGISTERED_WHICH_ARE_REGISTERED_IN_FMS,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			loStatus = true;
		}
		catch (ApplicationException aoExp)
		{
			// Handle the ApplicationException type Exception and set moState
			// and context data
			setMoState("Error occured while Batch Process in markAmendmentETLRegistredWhichAreRegisteredInFMS() method\n");
			aoExp.addContextData(
					"Exception occured while Batch Process in markAmendmentETLRegistredWhichAreRegisteredInFMS() method",
					aoExp);
			LOG_OBJECT.Error(
					"Error occured while Batch Process in markAmendmentETLRegistredWhichAreRegisteredInFMS() method",
					aoExp);
			throw aoExp;
		}
		System.out.println("=======[markAmendmentETLRegistredWhichAreRegisteredInFMS]: set AMD status 118 "+ loStatus);
		
		return loStatus;
	}
	
    //Start R7: defect 8644 part 3
	/**
     * This method is called to update the IS_AMENDMENT_REGISTERED_IN_FMS as 1 in the contract table when 
	 * REQUEST_PARTIAL_MERGE is 2, status_id is 118 and contract_type_id is 2.
	 * @param aoMybatisSession
	 * @param asHMReqdProp
	 * @return
	 * @throws ApplicationException
	 */
	
	@SuppressWarnings("rawtypes")
	public Boolean markAmendmentETLRegistredWithPartialMergeRequest(SqlSession aoMybatisSession, HashMap asHMReqdProp)
			throws ApplicationException
	{
		System.out.println("=======[markAmendmentETLRegistredWithPartialMergeRequest]: set AMD status 118 " +
"    update contract set IS_AMENDMENT_REGISTERED_IN_FMS =1 where REQUEST_PARTIAL_MERGE = 2 and status_id = 118 and contract_type_id = 2");

		Boolean loStatus = false;
		try
		{
			DAOUtil.masterDAO(aoMybatisSession, asHMReqdProp, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSConstants.MARK_AMENDMENT_ETL_REGISTERED_WITH_PARTIAL_MERGE_REQUEST,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			loStatus = true;
		}
		catch (ApplicationException aoExp)
		{
			// Handle the ApplicationException type Exception and set moState
			// and context data
			setMoState("Error occured while Batch Process in markAmendmentETLRegistredWithPartialMergeRequest() method\n");
			aoExp.addContextData(
					"Exception occured while Batch Process in markAmendmentETLRegistredWithPartialMergeRequest() method",
					aoExp);
			LOG_OBJECT.Error(
					"Error occured while Batch Process in markAmendmentETLRegistredWithPartialMergeRequest() method",
					aoExp);
			throw aoExp;
		}
		
System.out.println("=======[markAmendmentETLRegistredWithPartialMergeRequest]: set AMD status 118 " +loStatus );

		return loStatus;
	}
	//End R7: defect 8644 part 3

	/**
	 * This method generates notification map for Accept Proposal task depending
	 * upon input alert list
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Create a local object Notification map</li>
	 * <li>Create the Notification Alert List that is added to the Notification
	 * Map</li>
	 * <li>Creates the request param map</li>
	 * <li>Create NotificationDataBean type object and set the provider list,
	 * agency list, linkMap and agencyLinkMap attributes</li>
	 * <li>Then add the request map, created by, modified by, Entity ID, Entity
	 * Type,NotificationDataBean to the Notification map</li>
	 * <li>Set dynamic parameters to be replaced in request map according to the
	 * value of the request score amendment boolean flag</li>
	 * <li>Set alert list in map and return modified notification map</li>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest object
	 * @param aoAlertList a list of notification ID to be sent
	 * @return a notification hashmap
	 * @throws ApplicationException In case exception occurs
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private HashMap<String, Object> getNotificationMapForNT413(String asParentContractId, String lsUserId,
			SqlSession aoMybatisSession, ContractBudgetBean loContractBudgetBean) throws ApplicationException
	{
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		try
		{
			HashMap<String, String> loRequestMap = new HashMap<String, String>();
			List<String> loNotificationAlertList = new ArrayList<String>();
			loNotificationAlertList.add(HHSConstants.NT413);
			loNotificationMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
			HashMap aoHMArgs = new HashMap();
			aoHMArgs.put(HHSConstants.FISCAL_YEAR_ID, loContractBudgetBean.getBudgetfiscalYear());
			aoHMArgs.put(HHSConstants.PARENT_CONTRACT_ID, asParentContractId);
			String lsMergedValue = (String) DAOUtil.masterDAO(aoMybatisSession, aoHMArgs,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.GET_CONTRACT__FINANCIALS_INFORMATION,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			HashMap lsContractInfo = (HashMap) DAOUtil.masterDAO(aoMybatisSession, aoHMArgs,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.GET_CONTRACT_INFORMATION,
					HHSConstants.JAVA_UTIL_HASH_MAP);

			loRequestMap.put(HHSConstants.NT_PROCUREMENT_TITLE, (String) lsContractInfo.get("CONTRACT_TITLE"));
			loRequestMap.put(HHSConstants.NT_CT, (String) lsContractInfo.get("EXT_CT_NUMBER"));
			loRequestMap.put(HHSConstants.FISCAL_YEAR_ID_NT, loContractBudgetBean.getBudgetfiscalYear());
			loRequestMap.put(HHSConstants.FISCAL_YEAR_BUDGET, loContractBudgetBean.getTotalbudgetAmount());
			loRequestMap.put(HHSConstants.FISCAL_YEAR_MERGED_VALUE, lsMergedValue);
			NotificationDataBean loNotificationDataBean = new NotificationDataBean();
			loNotificationMap.put(HHSConstants.NT413, loNotificationDataBean);
			loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
			loNotificationMap.put(ApplicationConstants.ENTITY_ID, asParentContractId);
			loNotificationMap.put(ApplicationConstants.ENTITY_TYPE, HHSConstants.PROPOSAL);
			loNotificationMap.put(HHSConstants.CREATED_BY_USER_ID, lsUserId);
			loNotificationMap.put(HHSConstants.MODIFIED_BY, lsUserId);
		}
		// handling Exception thrown by the application
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error was occurred while get notfication", aoEx);
			throw new ApplicationException("Error was occurred while get notfication", aoEx);
		}
		return loNotificationMap;
	}

	// Added for Release 6 Apt Interface
	/**
	 * This method is used for updating the status of contract to 60(pending
	 * COF) after COF task is launched
	 * @param aoMybatisSession : SqlSession object
	 * @param aoHMArgs : HashMap containing values of contract required for
	 *            updating contract status
	 * @return lbStatusResult : Boolean value which is true if status is updated
	 *         successfully
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public Boolean setContractStatusAsRegisteredForCOF(SqlSession aoMybatisSession, HashMap aoHMArgs)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered into setContractStatusAsRegisteredForCOF() with paramters::" + aoHMArgs);
		Boolean lbStatusResult = Boolean.FALSE;

		ContractBean loContractBean = (ContractBean) aoHMArgs.get(HHSConstants.AO_CONTRACT_BEAN);

		try
		{
			setMoState("Registering Contract: " + loContractBean.getContractId() + "-"
					+ loContractBean.getContractTitle() + ".\n");
			loContractBean.setStatusId(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_CONTRACT_PENDING_COF));
			// Update the Contract Status to Registered)
			DAOUtil.masterDAO(aoMybatisSession, loContractBean, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSConstants.SET_CONTRACT_STATUS_AS_REGISTERED_FOR_COF, HHSConstants.CONTRACT_BEAN_PATH);
			lbStatusResult = Boolean.TRUE;
			setMoState("Contract Status sucessfully updated to Pending COF for ContractId : "+ loContractBean.getContractId());
		}
		catch (ApplicationException aoExp)
		{
			// Handle the ApplicationException type Exception and set moState
			// and context data
			setMoState("Error occured while setting Contract Status as Registered for Batch Process in setContractStatusAsRegisteredForCOF() method.\n");
			aoExp.addContextData(
					"Exception occured while setting Contract Status as Registered for Batch Process in setContractStatusAsRegisteredForCOF() method.",
					aoExp);
			LOG_OBJECT
					.Error("Error occured while setting Contract Status as Registered for Batch Process in setContractStatusAsRegisteredForCOF() method."
							+ aoExp);
			throw aoExp;
		}
		catch (Exception aoExp)
		{
			// Handle the Exception type exception and set moState
			// and context data
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while setting Contract Status as Registered for Batch Process in setContractStatusAsRegisteredForCOF() method.",
					aoExp);
			setMoState("Error occured while setting Contract Status as Registered for Batch Process in setContractStatusAsRegisteredForCOF() method.\n");
			loAppEx.addContextData(
					"Exception occured while setting Contract Status as Registered for Batch Process in setContractStatusAsRegisteredForCOF() method.",
					loAppEx);
			LOG_OBJECT
					.Error("Error occured while setting Contract Status as Registered for Batch Process in setContractStatusAsRegisteredForCOF() method."
							+ loAppEx);
			throw loAppEx;
		}

		return lbStatusResult;
	}

	/**
	 * This method is used for updating the status of contract amendment to
	 * 60(pending COF) after COF task is launched
	 * @param aoMybatisSession : SqlSession object
	 * @param aoHMArgs : HashMap containing values of contract required for
	 *            updating contract status
	 * @return lbStatusResult : Boolean value which is true if status is updated
	 *         successfully
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public Boolean setContractStatusAsRegisteredForAmendmentCOF(SqlSession aoMybatisSession, ContractBean aoContractBean)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered into setContractStatusAsRegisteredForAmendmentCOF() with paramters::" + aoContractBean);
		Boolean loStatusResult = Boolean.FALSE;
		try
		{
			setMoState("Registering Contract: " + aoContractBean.getContractId() + "-"
					+ aoContractBean.getContractTitle() + ".\n");
			aoContractBean.setStatusId(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_CONTRACT_PENDING_COF));
			// Update the Contract Status to Registered)
			DAOUtil.masterDAO(aoMybatisSession, aoContractBean, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSConstants.SET_CONTRACT_STATUS_AS_REGISTERED_FOR_AMENDMENT_COF, HHSConstants.CONTRACT_BEAN_PATH);
			loStatusResult = Boolean.TRUE;
		}
		catch (ApplicationException aoExp)
		{
			// Handle the ApplicationException type Exception and set moState
			// and context data
			setMoState("Error occured while setting Contract Status as Registered for Batch Process in setContractStatusAsRegisteredForAmendmentCOF() method.\n");
			aoExp.addContextData(
					"Exception occured while setting Contract Status as Registered for Batch Process in setContractStatusAsRegisteredForAmendmentCOF() method.",
					aoExp);
			LOG_OBJECT
					.Error("Error occured while setting Contract Status as Registered for Batch Process in setContractStatusAsRegisteredForAmendmentCOF() method."
							+ aoExp);
			throw aoExp;
		}
		catch (Exception aoExp)
		{
			// Handle the Exception type exception and set moState
			// and context data
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while setting Contract Status as Registered for Batch Process in setContractStatusAsRegisteredForAmendmentCOF() method.",
					aoExp);
			setMoState("Error occured while setting Contract Status as Registered for Batch Process in setContractStatusAsRegisteredForAmendmentCOF() method.\n");
			loAppEx.addContextData(
					"Exception occured while setting Contract Status as Registered for Batch Process in setContractStatusAsRegisteredForAmendmentCOF() method.",
					loAppEx);
			LOG_OBJECT
					.Error("Error occured while setting Contract Status as Registered for Batch Process in setContractStatusAsRegisteredForAmendmentCOF() method."
							+ loAppEx);
			throw loAppEx;
		}

		return loStatusResult;
	}
	// Release 6 Apt Interface changes end
	
	
	//R7 defect 8644 changes start
	/**
	 * getPartialMergeRequestList method added to get the list of all the contract ids which are requested for the partial merge.
	 * @param aoMybatisSession
	 * @param asHMReqdProp
	 * @return
	 * @throws ApplicationException
	 */
	
	@SuppressWarnings(
			{ "unchecked", "rawtypes" })
			public List<ContractList> getPartialMergeRequestList(SqlSession aoMybatisSession, HashMap asHMReqdProp)
					throws ApplicationException
			{
				List<ContractList> loList = new ArrayList<ContractList>();

				try
				{
					loList = (List<ContractList>) DAOUtil.masterDAO(aoMybatisSession, asHMReqdProp,
							HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.GET_PARTIAL_MERGE_REQUEST_LIST,
							HHSConstants.JAVA_UTIL_HASH_MAP);
				}
				catch (ApplicationException aoExp)
				{
					// Handle the ApplicationException type Exception and set moState
					// and context data
					setMoState("Error occurred while Batch Process in getPartialMergeRequestList() method\n");
					aoExp.addContextData("Exception occurred while Batch Process in getPartialMergeRequestList() method", aoExp);
					LOG_OBJECT.Error("Error occurred while Batch Process in getPartialMergeRequestList() method", aoExp);
					throw aoExp;
				}
				return loList;
			}
	
	//Start R7: defect 8644 part 3
	/**
	 * This method updateBudgetStatus() added to update the base budget status as active in case to Mark as Registered.
	 * In the case of Mark as Registered the base budget will not change to Active status so this method will update the
	 * status of those budget into Active status. 
	 * @param aoMybatisSession
	 * @param aoContractBeanList
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public Boolean updateBudgetStatus(SqlSession aoMybatisSession,List<ContractBean> aoContractBeanList)
			throws ApplicationException{
		LOG_OBJECT.Info("Executing the method updateBudgetEndDate() to update the Budget End Date");
		String lsContractId=null;
		String lsBudgetId=null;
		Boolean lbBEDate=false;
		HhsAuditService loHhsAuditService = new HhsAuditService();
		try
		{
			for (Iterator liContractItr = aoContractBeanList.iterator(); liContractItr.hasNext();)
			{
				LOG_OBJECT.Info("Executing the for loop of method updateBudgetEndDate()");
				String lsRequestPartialMerge=null;
				ContractBean loContractBean = (ContractBean) liContractItr.next();
				lsContractId=loContractBean.getContractId();
				lsRequestPartialMerge=requestPartialMergeStatusMethod(aoMybatisSession,lsContractId);
				if(Integer.parseInt(lsRequestPartialMerge)>0){
					List<String> loBudgetList= new ArrayList<String>();
					loBudgetList = (ArrayList<String>) DAOUtil.masterDAO(aoMybatisSession, lsContractId,
							HHSConstants.MAPPER_CLASS_BUDGET_MAPPER, HHSConstants.FETCH_BUDGET_LIST,
							HHSConstants.JAVA_LANG_STRING);
					LOG_OBJECT.Info("Executing the if condition of method updateBudgetEndDate(), the condition is true");
					
					//Below concept added for the audit.
					
					for (Iterator liBudgetItr = loBudgetList.iterator(); liBudgetItr.hasNext();)
					{
						List<HhsAuditBean> loAuditList = new ArrayList<HhsAuditBean>();
						LOG_OBJECT.Info("Executing the for loop of method updateBudgetEndDate() for updating the budget status from Approved to Active");
						lsBudgetId=(String) liBudgetItr.next();
						DAOUtil.masterDAO(aoMybatisSession, lsBudgetId, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
								HHSR5Constants.BASE_CONTRACT_BUDGET_STATUS, HHSConstants.JAVA_LANG_STRING);
						LOG_OBJECT.Info("Executing the for loop of method updateBudgetEndDate() for auditing");
						
					loAuditList.add(HHSUtil.addAuditDataToChannel(HHSConstants.STATUS_CHANGE, HHSConstants.STATUS_CHANGE,
							ApplicationConstants.STATUS_CHANGED_FROM + HHSConstants.SPACE + HHSConstants.STR
									+ HHSConstants.STR_BUDGET_APPROVED + HHSConstants.STR + HHSConstants.TO
									+ HHSConstants.STR + HHSConstants.BUDGET_ACTIVE + HHSConstants.STR, HHSConstants.BUDGET_TYPE3,
									lsBudgetId, HHSConstants.SYSTEM_USER, HHSConstants.AGENCY_AUDIT));
					loHhsAuditService.hhsMultiAuditInsert(aoMybatisSession, loAuditList, true);
					}
				//End
				}
			}
		}
		catch (ApplicationException loExp)
		{
			loExp.addContextData(HHSConstants.CONTRACT_ID_WORKFLOW, lsContractId);
			LOG_OBJECT.Error("Exception occured in FinancialsService: updateBudgetStatus method:: ", loExp);
			setMoState("ApplicationException while executing updateBudgetStatus method ");
			throw loExp;
		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Exception occured in FinancialsService: updateBudgetStatus method:: ", loEx);
			setMoState("Exception while executing updateBudgetStatus method ");
			throw new ApplicationException("Exception while executing updateBudgetStatus method", loEx);
		}
		return lbBEDate;
	}

	
	
	/**
	 * requestPartialMergeStatusMethod method added to get the status of the contract id's which are requested for the partial merge.
	 * @param aoMyBatisSession
	 * @param asContractId
	 * @return lsrequestPartialMergeStatus
	 * @throws ApplicationException
	 */
	public String requestPartialMergeStatusMethod(SqlSession aoMyBatisSession, String asContractId)
			throws ApplicationException
	{
		String lsrequestPartialMergeStatus = HHSConstants.EMPTY_STRING;
		LOG_OBJECT.Info("Executing the method requestPartialMergeStatusMethod()");

		try
		{
			lsrequestPartialMergeStatus = (String) DAOUtil.masterDAO(aoMyBatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.REQUEST_PARTIAL_MERGE_STATUS,
					HHSConstants.JAVA_LANG_STRING);
			LOG_OBJECT.Info(" The status of the contract received.", true);
			if(Integer.parseInt(lsrequestPartialMergeStatus)==0){
			lsrequestPartialMergeStatus = (String) DAOUtil.masterDAO(aoMyBatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_REQUEST_PARTIAL_MERGE_VALUE,
					HHSConstants.JAVA_LANG_STRING);
			}
			LOG_OBJECT.Info(" The status of the contract received.", true);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoAppExp)
		{
			setMoState("Error while fetching contract status for contract id : " + asContractId);
			LOG_OBJECT.Error("Error while fetching contract status with Exception : ", aoAppExp);
			throw aoAppExp;
		}

		return lsrequestPartialMergeStatus;

	}
	// R7 defect 8644 changes end.
	
	//Start R7: changes done for defect 8705
		/**
		 * This method will called at the time of execution of the batch and will update
		 * the Budget end date, if the contract end date is changed in the Amendment.
		 * Updated in Defect 9000
		 * @param aoMybatisSession
		 * @param aoContractBeanList
		 * @return
		 * @throws ApplicationException
		 */
		@SuppressWarnings("unchecked")
		public Boolean updateBaseBudgetEndDate(SqlSession aoMybatisSession,String asContractId)
				throws ApplicationException{
			LOG_OBJECT.Info("Executing the method updateBudgetEndDate() to update the Budget End Date");
			HashMap loHMArgs = new HashMap();
			List<HashMap<String,String>> loBaseBudgetDetailList=null;
			String lsAmendContractEndDate = null;
			Boolean lbBEDate=false;
			String lsBudgetId=null;
			String lsFiscalYearId=null;
			String lsBudgetEndDateFromDB=null;
			Calendar loBudgetEndDate = Calendar.getInstance();
			Calendar loamendContractcal=Calendar.getInstance();
			SimpleDateFormat losdf = new SimpleDateFormat("yyyy-MM-dd");
			Date ldBudgetEndDate=null;
			Date ldAmendContractEndDate=null;
			try
			{
					loHMArgs.put(HHSConstants.BASE_LAST_MODIFIED_BY, HHSConstants.SYSTEM_USER);
					loHMArgs.put(HHSConstants.CONTRACT_ID_KEY,asContractId);
					
					loBaseBudgetDetailList = (List<HashMap<String,String>>)DAOUtil.masterDAO(
								aoMybatisSession, loHMArgs, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
								HHSConstants.FETCH_BUDGET_DETAILS, HHSConstants.JAVA_UTIL_HASH_MAP);
						
					if(loBaseBudgetDetailList !=null)
					{
						for(Iterator liBudgetItr = loBaseBudgetDetailList.iterator(); liBudgetItr.hasNext();)
						{
							HashMap<String,String> loBudgetDetailMap=(HashMap<String,String>) liBudgetItr.next();
							lsBudgetId=String.valueOf(loBudgetDetailMap.get(HHSConstants.BUDGET_ID_HASH_KEY));
							lsFiscalYearId=String.valueOf(loBudgetDetailMap.get(HHSConstants.FISCAL_YEAR_ID_CAPS));
							//get budget end date
							lsBudgetEndDateFromDB=(String)loBudgetDetailMap.get(HHSConstants.BUDGET_END_DATE_CAPS);
								if(null != lsBudgetEndDateFromDB && lsBudgetEndDateFromDB.contains(HHSConstants.FORWARD_SLASH))
								{
									String[] lsBudgetEndDateArr = lsBudgetEndDateFromDB.split(HHSConstants.FORWARD_SLASH);
									loBudgetEndDate.set(Calendar.MONTH,Integer.parseInt(lsBudgetEndDateArr[0])-1);
									loBudgetEndDate.set(Calendar.DATE,Integer.parseInt(lsBudgetEndDateArr[1]));
									loBudgetEndDate.set(Calendar.YEAR,Integer.parseInt(lsBudgetEndDateArr[2]));
									loBudgetEndDate.set(Calendar.HOUR_OF_DAY,0);
								    loBudgetEndDate.set(Calendar.MINUTE,0);
								    loBudgetEndDate.set(Calendar.SECOND,0);
								    loBudgetEndDate.set(Calendar.MILLISECOND,0);
									ldBudgetEndDate=losdf.parse(losdf.format(loBudgetEndDate.getTime()));
								}
							//get proposed contract end date
							lsAmendContractEndDate = (String)loBudgetDetailMap.get(HHSConstants.AMEND_CONTRACT_END_DATE_CAPS);
							if(null != lsAmendContractEndDate && lsAmendContractEndDate.contains(HHSConstants.FORWARD_SLASH))
							{
								String[] lsAmendmentEndDateArr = lsAmendContractEndDate.split(HHSConstants.FORWARD_SLASH);
								loamendContractcal.set(Calendar.MONTH,Integer.parseInt(lsAmendmentEndDateArr[0])-1);
								loamendContractcal.set(Calendar.DATE,Integer.parseInt(lsAmendmentEndDateArr[1]));
								loamendContractcal.set(Calendar.YEAR,Integer.parseInt(lsAmendmentEndDateArr[2]));
								loamendContractcal.set(Calendar.HOUR_OF_DAY,0);
								loamendContractcal.set(Calendar.MINUTE,0);
								loamendContractcal.set(Calendar.SECOND,0);
								loamendContractcal.set(Calendar.MILLISECOND,0);
								ldAmendContractEndDate=losdf.parse(losdf.format(loamendContractcal.getTime()));
								
							}
							Calendar loCurrentFyEndDate=Calendar.getInstance();
							loCurrentFyEndDate.set(Calendar.MONTH,HHSConstants.INT_FISCAL_YEAR_END_MONTH);
							loCurrentFyEndDate.set(Calendar.DATE,HHSConstants.INT_FISCAL_YEAR_END_DAY_OF_MONTH);
							loCurrentFyEndDate.set(Calendar.YEAR,Integer.valueOf(lsFiscalYearId));
							loCurrentFyEndDate.set(Calendar.HOUR_OF_DAY,0);
							loCurrentFyEndDate.set(Calendar.MINUTE,0);
							loCurrentFyEndDate.set(Calendar.SECOND,0);
							loCurrentFyEndDate.set(Calendar.MILLISECOND,0);
							Date ldCurrentFYContractEndDate=losdf.parse(losdf.format(loCurrentFyEndDate.getTime()));
							
							loHMArgs.put(HHSConstants.RSLT_BUDGET_ID, lsBudgetId);
							LOG_OBJECT.Debug("Budget EndDate:" + ldBudgetEndDate + " Current FY end date:"+ ldCurrentFYContractEndDate
									+" Proposed Contract End Date:" +ldAmendContractEndDate );
							if(ldAmendContractEndDate.compareTo(ldCurrentFYContractEndDate)>0)
							{
								//This condition will check whether the budget_end_date is not equals to 30th June
								if(ldBudgetEndDate.compareTo(ldCurrentFYContractEndDate)!=0)
								{
									loHMArgs.put(HHSConstants.BASE_CONTRACT_BUDGET_END_DATE, loCurrentFyEndDate.getTime());
									DAOUtil.masterDAO(aoMybatisSession, loHMArgs, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
											HHSConstants.BUDGET_END_DATE_UPDATED_IN_AMENDMENT, HHSConstants.JAVA_UTIL_HASH_MAP);
								}
							}
							else
							{
								loCurrentFyEndDate.set(Calendar.HOUR_OF_DAY,0);
								loHMArgs.put(HHSConstants.BASE_CONTRACT_BUDGET_END_DATE, loamendContractcal.getTime());
								DAOUtil.masterDAO(aoMybatisSession, loHMArgs, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
										HHSConstants.BUDGET_END_DATE_UPDATED_IN_AMENDMENT, HHSConstants.JAVA_UTIL_HASH_MAP);
							}
						}
					}
			}
			catch (ApplicationException loExp)
			{
				loExp.addContextData(HHSConstants.CONTRACT_ID_WORKFLOW, loHMArgs.get(HHSConstants.CONTRACT_ID_WORKFLOW));
				LOG_OBJECT.Error("Exception occured in FinancialsService: updateBudgetEndDate method:: ", loExp);
				setMoState("ApplicationException while executing updateBudgetEndDate method ");
				throw loExp;
			}
			catch (Exception loEx)
			{
				LOG_OBJECT.Error("Exception occured in FinancialsService: updateBudgetEndDate method:: ", loEx);
				setMoState("Exception while executing updateBudgetEndDate method ");
				throw new ApplicationException("Exception while executing updateBudgetEndDate method", loEx);
			}
			return lbBEDate;
		}
		//End R7
		/* [Start] R8.10.0 QC9399    */
		@SuppressWarnings("unchecked")
		public ContractBean fetchAMDBaseDate(SqlSession aoMybatisSession, String aoContractId) throws ApplicationException{
			HashMap <String,String> aoMap = new HashMap<String,String>() ;
			aoMap.put(HHSConstants.CONTRACT_ID1, aoContractId);
			System.out.println(" TRACE------------ [aoContractId]"+ aoContractId );
			List<ContractBean> loContractBeanList = (List<ContractBean>) DAOUtil.masterDAO(aoMybatisSession, aoMap, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSConstants.FETCH_AMD_BASE_DATE, HHSConstants.JAVA_UTIL_HASH_MAP);

			if(loContractBeanList != null && loContractBeanList.size() > 0 ){
				System.out.println(" ------------ [contract End date]"+ loContractBeanList.get(0).getParentContractEndDate().toString() 
						+ "[AMD End date]" + loContractBeanList.get(0).getContractEndDate().toString());
			    return   loContractBeanList.get(0);
			} else {
				System.out.println(" ------------ [contract End date]  NULL  [AMD End date]  NULL" );
				return   null;
			}
		}
		/* [End] R8.10.0 QC9399    */
		
}