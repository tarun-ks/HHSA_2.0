package com.nyc.hhs.contractsbatch.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.batch.IBatchQueue;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.controllers.util.BmcControllerUtil;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.ContractBean;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8services.P8HelperServices;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This batch will be responsible for processing the inbound contract from APT
 * interface and launching the cof workflow for these contracts
 * 
 */
public class ContractsConfigureBatch extends P8HelperServices implements IBatchQueue
{

	private static final LogInfo LOG_OBJECT = new LogInfo(ContractsConfigureBatch.class);
	

	/**
	 * Blank implementation of getQueue method of interface
	 * 
	 * @param aoMParameters HashMap of Parameter
	 * @return null
	 */
	@SuppressWarnings("rawtypes")
	public List<ContractsConfigureBatch> getQueue(Map aoMParameters)
	{
		return null;
	}

	/**
	 * Implementation of the executeQueue method . This method will call all the
	 * other methods for executing the batch operations
	 * 
	 * @param aoLQueue List of Queue
	 * @throws ApplicationException 
	 */
	@SuppressWarnings("rawtypes")
	public void executeQueue(List aoLQueue) throws ApplicationException
	{
		try
		{
			// Collect and set supporting things for DB Queries and File-net
			Channel loChannelObj = new Channel();

			loChannelObj.setData(HHSConstants.LB_AUTH_STATUS_FLAG, true);
			SqlSession loFilenetPEDBSession = null;
			// Get Filenet session
			P8UserSession loFilenetSession = filenetConnection.setP8SessionVariables();
			loFilenetPEDBSession = HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory().openSession();
			loFilenetSession.setFilenetPEDBSession(loFilenetPEDBSession);
			loChannelObj.setData(HHSConstants.AO_FILENET_SESSION, loFilenetSession);

			// Calls the method to update the status of contract from etl to cof
			// pending
			processInboundAptContracts(loChannelObj);
			processInboundAptAmendments(loChannelObj);

		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in ContractsConfigureBatch.executeQueue()", aoAppEx);
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception in ContractsConfigureBatch.executeQueue()", aoEx);
		}
	} // end function executeQueue

	/**
	 * This method will fetch all the contracts with status ETL Registered(192)
	 * from Contract table
	 * @param aoChannelObj : Channel object
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private void processInboundAptContracts(Channel aoChannelObj) throws ApplicationException
	{

		// Log the event of entering batch process
		try
		{
			LOG_OBJECT.Info("Entered processInboundAptContracts()");

			HashMap loHMArgs = new HashMap();
			List<ContractBean> loContractsList = null;
			// Fetch The contracts which have set their Status as 'ETL
			// Registered'
			loHMArgs.put(HHSConstants.STATUS_ID, PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.CONTRAT_STATUS_FOR_INBOUND_APT_BATCH_PROCESS));
			aoChannelObj.setData(HHSConstants.AO_HM_ARGS, loHMArgs);
			HHSTransactionManager.executeTransaction(aoChannelObj, HHSConstants.FETCH_CONTRACTS_FOR_BATCH_PROCESS);
			loContractsList = (List<ContractBean>) aoChannelObj.getData(HHSConstants.CONTRACT_BEAN_LIST_ARG);
			// This method will launch cof workflows and update the status of
			// contracts and amendments to pending cof
			processAndLaunchContractCof(loContractsList, aoChannelObj);
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in ContractsConfigureBatch.processInboundAptContracts() for aoChannelObj:"
					+ aoChannelObj, aoAppEx);
			throw aoAppEx;
		}
	}

	/**
	 * This method will fetch all the contract amendments with status ETL
	 * Registered(192)
	 * @param aoChannelObj : Channel object
	 * 
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked" })
	private void processInboundAptAmendments(Channel aoChannelObj) throws ApplicationException
	{
		try
		{
			// Log the event of entering batch process
			LOG_OBJECT.Info("Entered processInboundAptAmendments()");
	
			Map loHMArgs = new HashMap();
			List<ContractBean> loContractsList = null;
			// Fetch The contracts which have set their Status as 'ETL
			// Registered'
			loHMArgs.put(HHSConstants.STATUS_ID, PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.CONTRAT_STATUS_FOR_INBOUND_APT_BATCH_PROCESS));
			aoChannelObj.setData(HHSConstants.AO_HM_ARGS, loHMArgs);
			HHSTransactionManager
					.executeTransaction(aoChannelObj, HHSConstants.FETCH_AMENDMENT_CONTRACTS_FOR_BATCH_PROCESS);
			loContractsList = (List<ContractBean>) aoChannelObj.getData(HHSConstants.CONTRACT_BEAN_LIST_ARG);
			processAndLaunchAmendmentCof(loContractsList, aoChannelObj);
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in ContractsConfigureBatch.processInboundAptAmendments() for aoChannelObj:"
					+ aoChannelObj, aoAppEx);
			throw aoAppEx;
		}

	}

	/**
	 * This method is used to insert line items for all the active fiscal years
	 * and then launch amendment configuration of funds work flow. Putting catch
	 * inside for loop to not break the loop if exception for one contract
	 * 
	 * @param loChannelObj
	 * @param loContractsForAmendments : List of contract amendments with status
	 *            ETL registered(192)
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private void processAndLaunchAmendmentCof(List<ContractBean> aoContractsForAmendments, Channel aoChannelObj) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered processAndLaunchAmendmentCof() with parameters:"+aoContractsForAmendments);
		for (ContractBean loAmendmentBean : aoContractsForAmendments)
		{
			try
			{
				P8UserSession loFilenetSession = filenetConnection.setP8SessionVariables();
				Map loHmWFReqProps = new HashMap();
				String lsContractId = loAmendmentBean.getParentContractId();
				TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
				loTaskDetailsBean.setContractId(loAmendmentBean.getContractId());
				loTaskDetailsBean.setStartFiscalYear(loAmendmentBean.getBudgetStartYear());
				loTaskDetailsBean.setBaseContractId(lsContractId);
				//R6: Updated the task name from constants file
				loTaskDetailsBean.setTaskName(HHSConstants.TASK_AMENDMENT_CONFIGURATION);
				loTaskDetailsBean.setUserId(HHSConstants.SYSTEM_USER);
				loTaskDetailsBean.setP8UserSession(loFilenetSession);

				/*
				 * Getting the active approved fiscal years by calling
				 * fechActiveApprovedFiscalYears transaction
				 */
				Channel loChannelObj = new Channel();
				loChannelObj.setData(HHSConstants.AMENDED_CONTRACT_ID, loTaskDetailsBean.getContractId());
				HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.FETCH_ACTIVE_APPROVED_FISCAL_YEARS);
				List<String> loFiscalYrList = (List) loChannelObj.getData(HHSConstants.FISCAL_YEAR_LIST);
				
				loChannelObj.setData(HHSConstants.CONTRACT_ID_KEY, lsContractId);
				loChannelObj.setData(HHSConstants.AMENDED_CONTRACT_ID, loAmendmentBean.getContractId());
				loChannelObj.setData(HHSConstants.CONTRACT_TYPE_ID_KEY, loAmendmentBean.getContractTypeId());

				Integer loNextNewFy = BmcControllerUtil.getNextNewFYBudgetYear(loChannelObj);
				
				/*
				 * Get calculated Fiscal Years for Contrast as per start date
				 * and end date
				 */
				Date loContractStartDate = DateUtil.getSqlDate(loAmendmentBean.getContractStartDate());
				Date loContractEndDate = DateUtil.getSqlDate(loAmendmentBean.getContractEndDate());
				HashMap<String, Integer> loContractFYDetails = null;

				loContractFYDetails = HHSUtil.getFirstAndLastFYOfContract(loContractStartDate, loContractEndDate);
				Integer liFmsContractEndFiscalYear = loContractFYDetails.get(HHSConstants.CONTRACT_END_FY);
				
				/*
				 * If the amendment end year is more than or equal to the out of year fiscal year, then adding this year 
				 * into the year array to process budgets
				 */
				if(liFmsContractEndFiscalYear >= loNextNewFy)
				{
					loFiscalYrList.add(loNextNewFy.toString());
				}

				
				/*
				 * Invoking the utility work flow for each Fiscal year
				 */
				Channel loUtilityWFChannelObj = new Channel();
				loUtilityWFChannelObj.setData(HHSConstants.AO_FILENET_SESSION, loFilenetSession);
				for (String loFiscalYr : loFiscalYrList)
				{
					/*
					 * If the amendment has a fiscal year one more than the last configured fiscal year
					 * then we will insert budget, sub-budget and budget-customization records 
					 * for the base contract as well
					 */
					if(loFiscalYr.equalsIgnoreCase(loNextNewFy.toString()))
					{
						loChannelObj.setData(HHSConstants.FISCAL_YEAR_ID_KEY, loFiscalYr);
						CBGridBean loCBGridBean = new CBGridBean();
						loCBGridBean.setContractID(lsContractId);
						loCBGridBean.setModifiedByUserId(HHSConstants.SYSTEM_USER);
						loCBGridBean.setModifyByAgency(HHSConstants.SYSTEM_USER);
						loChannelObj.setData(HHSConstants.CB_GRID_BEAN_OBJ, loCBGridBean);
						HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.GET_NEXT_NEW_FY_BUDGET_DETAILS_FOR_BATCH);
					}
					
					loHmWFReqProps.put(HHSConstants.COMPONENT_ACTION,
							HHSConstants.INSERT_LINE_ITEMS_CONF_COMPONENT_ACTION);
					loUtilityWFChannelObj.setData(HHSConstants.WORK_FLOW_NAME, HHSConstants.WF_FINANCIAL_UTILITY);
					loTaskDetailsBean.setStartFiscalYear(loFiscalYr);
					loHmWFReqProps.put(HHSConstants.VALUES, CommonUtil.convertBeanToString(loTaskDetailsBean));
					loUtilityWFChannelObj.setData(HHSConstants.LO_HM_WF_REQ_PROPS, loHmWFReqProps);
					HHSTransactionManager.executeTransaction(loUtilityWFChannelObj,
							HHSConstants.INSERT_LINE_ITEMS_CONF_COMPONENT_ACTION);
				}

				/*
				 * Now we launch the transaction which first populates the WF
				 * properties then calls file net to launch COF work flows and
				 * then updates the status of contract to 60
				 */
				HashMap loHMWFRequiredProps = new HashMap();
				loHMWFRequiredProps.put(HHSConstants.CONTRACT_ID_WORKFLOW, loAmendmentBean.getContractId());
				//R6: Updated from constants file
				loHMWFRequiredProps.put(HHSConstants.SUBMITTED_BY, HHSConstants.SYSTEM_INTERFACE);
				loHMWFRequiredProps.put(HHSConstants.PROPERTY_PE_LAUNCH_COF, true);
				loHMWFRequiredProps.put(HHSConstants.WORKFLOW_NAME, HHSConstants.WF_AMENDMENT_CONFIGURATION);
				loHMWFRequiredProps.put(HHSConstants.TASK_STATUS, HHSConstants.STR_BUDGET_APPROVED);
				aoChannelObj.setData(HHSConstants.AO_HMWF_REQUIRED_PROPS, loHMWFRequiredProps);
				aoChannelObj.setData(HHSConstants.AO_CONTRACT_BEAN, loAmendmentBean);
				HHSTransactionManager.executeTransaction(aoChannelObj,
						HHSConstants.PROCESS_INBOUND_AMENDMENTS_FOR_BATCH);
			}
			catch (ApplicationException aoAppEx)
			{
				LOG_OBJECT.Error("Exception in ContractsConfigureBatch.processAndLaunchAmendmentCof() for contractId:"
						+ loAmendmentBean.getContractId(), aoAppEx);
				throw aoAppEx;
			}
		}

	}

	/**
	 * This method is used to insert line items at the end of contract
	 * configuration task and then launch contract configuration of funds work
	 * flow Putting catch inside for loop to not break the loop if exception for
	 * one contract
	 * @param loChannelObj
	 * @param loContractsForAmendments : List of contract amendments with status
	 *            ETL registered(192)
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private void processAndLaunchContractCof(List<ContractBean> aoContractsForBase, Channel aoChannelObj)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered processAndLaunchAmendmentCof() with parameters:"+aoContractsForBase);
		for (ContractBean loContractBean : aoContractsForBase)
		{
			try
			{
				Map loHmWFReqProps = new HashMap();
				// First we are calling utility work flow
//				Channel loUtilityWFChannelObj = new Channel();
				TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
				loTaskDetailsBean.setContractId(loContractBean.getContractId());
				loTaskDetailsBean.setTaskName(HHSConstants.TASK_CONTRACT_CONFIGURATION);
				loTaskDetailsBean.setUserId(HHSConstants.SYSTEM_USER);
				// loTaskDetailsBean.setStartFiscalYear(liFmsContractStartFiscalYear.toString());
				loTaskDetailsBean.setStartFiscalYear(loContractBean.getBudgetStartYear());
				loHmWFReqProps.put(HHSConstants.COMPONENT_ACTION, HHSConstants.INSERT_LINE_ITEMS_CONF_COMPONENT_ACTION);
				loHmWFReqProps.put(HHSConstants.VALUES, CommonUtil.convertBeanToString(loTaskDetailsBean));
				aoChannelObj.setData(HHSConstants.WORK_FLOW_NAME, HHSConstants.WF_FINANCIAL_UTILITY);
				aoChannelObj.setData(HHSConstants.LO_HM_WF_REQ_PROPS, loHmWFReqProps);
				HHSTransactionManager.executeTransaction(aoChannelObj,
						HHSConstants.INSERT_LINE_ITEMS_CONF_COMPONENT_ACTION);

				/*
				 * Step2: We put the Required WF properties for financials work
				 * flow
				 */
				HashMap loHMWFRequiredProps = new HashMap();
				loHMWFRequiredProps.put(HHSConstants.CONTRACT_ID_WORKFLOW, loContractBean.getContractId());
				loHMWFRequiredProps.put(HHSConstants.SUBMITTED_BY, HHSConstants.SYSTEM_USER);
				loHMWFRequiredProps.put(HHSConstants.PROPERTY_PE_SUBMITTED_BY, HHSConstants.SYSTEM_USER);
				loHMWFRequiredProps.put(HHSConstants.PROPERTY_PE_LAUNCH_ORG_TYPE, HHSConstants.CITY);
				loHMWFRequiredProps.put(HHSConstants.PROPERTY_PE_LAUNCH_COF, true);
				loHMWFRequiredProps.put(HHSConstants.WORKFLOW_NAME, HHSConstants.WF_CONTRACT_CONFIGURATION);
				aoChannelObj.setData(HHSConstants.AO_HMWF_REQUIRED_PROPS, loHMWFRequiredProps);

				/*
				 * Step3: We launch the transaction which first populates the WF
				 * properties then calls file net to launch CC and COF work flows
				 * and then updates the status of contract to 60 - pending cof
				 */
				aoChannelObj.setData(HHSConstants.AO_CONTRACT_BEAN, loContractBean);
				HHSTransactionManager
						.executeTransaction(aoChannelObj, HHSConstants.PROCESS_INBOUND_CONTRACTS_FOR_BATCH);
			}
			catch (ApplicationException aoAppEx)
			{
				LOG_OBJECT.Error("Exception in ContractsConfigureBatch.processAndLaunchAmendmentCof() for contractId:"
						+ loContractBean.getContractId(), aoAppEx);
				throw aoAppEx;
			}
		}
	}

}