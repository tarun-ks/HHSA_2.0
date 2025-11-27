package com.nyc.hhs.contractsbatch.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.ListUtils;
import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.batch.IBatchQueue;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.ContractBean;
import com.nyc.hhs.model.ContractList;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8services.P8HelperServices;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This class will be used to perform all major actions in R3 batch process for
 * Registering the Base, Renew and Amendment type Contracts
 * 
 */
public class ContractsStatusUpdateBatch extends P8HelperServices implements IBatchQueue
{

	private static final LogInfo LOG_OBJECT = new LogInfo(ContractsStatusUpdateBatch.class);

	private Channel moChannelObj;
	private Boolean moBatchExecutedSuccessfuly = Boolean.TRUE;

	/**
	 * Blank implementation of getQueue method of interface
	 * 
	 * @param aoMParameters HashMap of Parameter
	 * @return null
	 */
	@SuppressWarnings("rawtypes")
	public List<ContractsStatusUpdateBatch> getQueue(Map aoMParameters)
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
			// call the function for updating contracts status
			updateContractsStatus();

		}
		// Catch all ApplicationExceptions and throw to the caller after
		// logging error
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in ContractsStatusUpdateBatch.executeQueue()", aoAppEx);
			throw aoAppEx;
		}
	} // end function executeQueue

	/**
	 * This method will call all the other methods for executing the batch
	 * operations and provides the required supporting environment for following
	 * calls
	 * 
	 * @throws ApplicationException
	 */
	private void updateContractsStatus() throws ApplicationException
	{
		SqlSession loFilenetPEDBSession = null;
		try
		{
			// Log the event of entering batch process
			LOG_OBJECT.Debug("Entered updateContractsStatus()");

			// Collect and set supporting things for DB Queries and File-net
			moChannelObj = new Channel();

			// Get Filenet session
			P8UserSession loFilenetSession = filenetConnection.setP8SessionVariables();
			loFilenetPEDBSession = HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory().openSession();
			loFilenetSession.setFilenetPEDBSession(loFilenetPEDBSession);

			moChannelObj.setData(HHSConstants.AO_FILENET_SESSION, loFilenetSession);
			moChannelObj.setData(HHSConstants.LB_AUTH_STATUS_FLAG, true);

			// Process Base Contracts
			processBaseContracts();
		
			//3.12.0 enhancement 6601
System.out.println("================[updateFlagAmendmentRegisteredInFMS]  :: Start");
			updateFlagAmendmentRegisteredInFMS();
System.out.println("================[markAmendmentETLRegistredWhichAreRegisteredInFMS]  :: Start");
			markAmendmentETLRegistredWhichAreRegisteredInFMS();

System.out.println("================[processAmendmentContracts]  :: Start");
			// Process Amendment Contracts
			processAmendmentContracts();
System.out.println("================[All Contract Merge Batch  ]  :: Done");

			if (moBatchExecutedSuccessfuly)
			{
				LOG_OBJECT.Debug("All Contracts have been processed successfully.\n");
			}
			else
			{
				LOG_OBJECT
						.Debug("Some Contracts could not be processed successfully and thrown Exception. See Log file for more details.\n");
			}

			// Log the event of exiting batch process
			LOG_OBJECT.Debug("Exited updateContractsStatus()");
		}
		// Catch all ApplicationExceptions and throw to the caller after
		// logging error
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in updateContractsStatus()", aoAppEx);
			throw aoAppEx;
		}
		finally
		{
			if (null != loFilenetPEDBSession)
			{
				loFilenetPEDBSession.close();
			}

		}
	}

	/**
	 * This method is for fetching the Contracts list for Batch Process
	 * 
	 * <ul>
	 * <li>Fetches the List of Contracts with their status is set to be ETL
	 * Registered(which are to be batch processed)</li>
	 * <li>Categorize them and calls their respective category method for
	 * further batch processing</li>
	 * </ul>
	 * 
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private void processBaseContracts() throws ApplicationException
	{
		List<ContractBean> loBaseContractsList = null;
		List<ContractBean> loBaseContractsWithNoDiscList = new ArrayList<ContractBean>();
		List<ContractBean> loBaseContractsWithDiscList = new ArrayList<ContractBean>();
		HashMap loHMArgs = new HashMap();
		LOG_OBJECT.Debug("===processBaseContracts===");
		try
		{
			// Fetch The contracts which have set their Status as 'ETL
			// Registered'
			loHMArgs.put(HHSConstants.STATUS_ID, PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.CONTRAT_STATUS_FOR_BATCH_PROCESS));
			LOG_OBJECT.Debug("===processBaseContracts with Staus 118 ===");
			moChannelObj.setData(HHSConstants.AO_HM_ARGS, loHMArgs);
			HHSTransactionManager.executeTransactionComponent(moChannelObj,
					HHSConstants.FETCH_CONTRACTS_FOR_BATCH_PROCESS);
			loBaseContractsList = (List<ContractBean>) moChannelObj.getData(HHSConstants.CONTRACT_BEAN_LIST_ARG);

			// Check the list fetch is not empty - if empty, just skip this
			// process
			if (null != loBaseContractsList && !loBaseContractsList.isEmpty())
			{
				// Categorize the Contracts in categories 1. with no
				// discrepancies and 2. with discrepancies
				filterBaseContractsList(loBaseContractsList, loBaseContractsWithNoDiscList, loBaseContractsWithDiscList);

				// Process the list of contract with no discrepancies is not
				// empty
				processBaseContractsWithNoDisc(loBaseContractsWithNoDiscList);

				// Process the list of contract with discrepancies is not empty
				processBaseContractsWithDisc(loBaseContractsWithDiscList);
			}
		}
		catch (ApplicationException aoAppExp)
		{
			// Handle the ApplicationException type Exception and set context
			// data
			aoAppExp.addContextData("Exception occured while Batch Process in processBaseContracts() method", aoAppExp);
			LOG_OBJECT.Error("Error occured while Batch Process in processBaseContracts() method", aoAppExp);
			throw aoAppExp;
		}
		catch (Exception aoExp)
		{
			// Handle the Exception type exception and set context data
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while Batch Process in processBaseContracts() method", aoExp);
			loAppEx.addContextData("Exception occured while Batch Process in processBaseContracts() method", loAppEx);
			LOG_OBJECT.Error("Error occured while Batch Process in processBaseContracts() method", loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * This method is for fetching the Contracts list for Batch Process
	 * 
	 * <ul>
	 * <li>Fetches the List of Contract Amendments with their status is set to
	 * be ETL Registered(which are to be batch processed)</li>
	 * <li>Categorize them and calls their respective category method for
	 * further batch processing</li>
	 * </ul>
	 * 
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private void processAmendmentContracts() throws ApplicationException
	{
		List<ContractBean> loAmendmentContractsList = null;

		List<ContractBean> loPositiveAmendmentContractsWithNoDiscList = new ArrayList<ContractBean>();
		List<ContractBean> loPositiveAmendmentContractsWithDiscList = new ArrayList<ContractBean>();

		List<ContractBean> loNegativeAmendmentContractsWithNoDiscList = new ArrayList<ContractBean>();
		List<ContractBean> loNegativeAmendmentContractsWithDiscList = new ArrayList<ContractBean>();

		Channel loChannelObj = null;
		HashMap loHMArgs = new HashMap();

		try
		{
			loChannelObj = new Channel();

			// Fetch The Amendment Contracts with their Base Amendment Details
			loHMArgs.put(HHSConstants.STATUS_ID, PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.CONTRAT_STATUS_FOR_BATCH_PROCESS));
			loChannelObj.setData(HHSConstants.AO_HM_ARGS, loHMArgs);

System.out.println("================[processAmendmentContracts  ]  :: status " + loHMArgs.get(HHSConstants.STATUS_ID));
			HHSTransactionManager.executeTransactionComponent(loChannelObj,
					HHSConstants.FETCH_AMENDMENT_CONTRACTS_FOR_BATCH_PROCESS);
			loAmendmentContractsList = (List) loChannelObj.getData(HHSConstants.CONTRACT_BEAN_LIST_ARG);
			
			if( loAmendmentContractsList == null ){
System.out.println("================[FETCH_AMENDMENT_CONTRACTS_FOR_BATCH_PROCESS  ]  :: amdment cnt  0"   );
			} else {
System.out.println("================[FETCH_AMENDMENT_CONTRACTS_FOR_BATCH_PROCESS  ]  :: amdment cnt " + loAmendmentContractsList.size()   );
			}

			// Check if no record found - just skip this process in such case
			if (null != loAmendmentContractsList && !loAmendmentContractsList.isEmpty())
			{
				// Categorize the contracts on basis of their type and amount
				filterAmendmentContractsList(loAmendmentContractsList, loPositiveAmendmentContractsWithNoDiscList,
						loPositiveAmendmentContractsWithDiscList, loNegativeAmendmentContractsWithNoDiscList,
						loNegativeAmendmentContractsWithDiscList);

				//Start: R7 for defect 8705- This transaction will update the End base budget date.
				loChannelObj.setData(HHSConstants.ALL_CONTRACT_LIST,loAmendmentContractsList);
				HHSTransactionManager.executeTransactionComponent(loChannelObj,
						HHSConstants.UPDATE_AMEND_CONTRACTS_BUDGET_STATUS_REQUEST_PARTIALMERGE);
				
				//End: R7 for defect 8705
				
				// Process Positive Amendment Contracts with No Discrepancies
				processPositiveAmendmentContractsWithNoDisc(loPositiveAmendmentContractsWithNoDiscList);

				// process Positive Amendment Contracts with Discrepancies
				processPositiveAmendmentContractsWithDisc(loPositiveAmendmentContractsWithDiscList);

				// process Negative Amendment Contracts with No Discrepancies
				processNegativeAmendmentContractsWithNoDisc(loNegativeAmendmentContractsWithNoDiscList);

				// process Negative Amendment Contracts with Discrepancies
				processNegativeAmendmentContractsWithDisc(loNegativeAmendmentContractsWithDiscList);
				
				//Start: Changes done in R7 for defect 8705
				/* [Start] R8.10.0 QC9399    */
				for (Iterator iterator = loAmendmentContractsList.iterator(); iterator.hasNext();)
				{
					ContractBean contractBean = (ContractBean) iterator.next();
					moChannelObj.setData("asContractId",contractBean.getContractId());
/*		   			if( contractBean.getContractEndDate().after(contractBean.getParentContractEndDate() )    ){
	    			    System.out.println(" ------------AMD update Budget End date");
*/						HHSTransactionManager.executeTransactionComponent(moChannelObj,
								HHSConstants.UPDATE_BUDGET_END_DATE_WITH_AMEND_END_DATE);
/*	                } else {
	                    System.out.println(" ------------AMD Does NOT update Budget End date");
	                }
*/
				}
				/* [End] R8.10.0 QC9399    */
				//End: R7 for defect 8705
			
			}
		}
		// Catch all Exceptions and throw in the form of ApplicationException -
		// Exceptions may occur while fetching the records and while
		// categorizing them in respective categories
		catch (ApplicationException aoAppExp)
		{
			// Handle the ApplicationException type Exception and set context
			// data
			aoAppExp.addContextData("Exception occured while Batch Process in processAmendmentContracts() method",
					aoAppExp);
			LOG_OBJECT.Error("Error occured while Batch Process in processAmendmentContracts() method", aoAppExp);
			throw aoAppExp;
		}
		catch (Exception aoExp)
		{
			// Handle the Exception type exception and set context data in newly
			// created ApplicationException
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while Batch Process in processAmendmentContracts() method", aoExp);
			loAppEx.addContextData("Exception occured while Batch Process in processAmendmentContracts() method",
					loAppEx);
			LOG_OBJECT.Error("Error occured while Batch Process in processAmendmentContracts() method", loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * This method is for filtering/categorizing Contracts list for Batch
	 * Process on the basis of discrepancy flag
	 * 
	 * @param aoBaseContractsList - List<ContractBean> object
	 * @param aoBaseContractsWithNoDiscList - List<ContractBean> object
	 * @param aoBaseContractsWithDiscList - List<ContractBean> object
	 * @throws ApplicationException
	 */
	private void filterBaseContractsList(List<ContractBean> aoBaseContractsList,
			List<ContractBean> aoBaseContractsWithNoDiscList, List<ContractBean> aoBaseContractsWithDiscList)
			throws ApplicationException
	{
		ContractBean loContractBean = null;
		try
		{
			Iterator<ContractBean> loIteratorSett = aoBaseContractsList.iterator();
			while (loIteratorSett.hasNext())
			{
				loContractBean = loIteratorSett.next();
				if (loContractBean.getDiscrepancyFlag().equals(HHSConstants.STRING_ZERO))
				{
					aoBaseContractsWithNoDiscList.add(loContractBean);
				}
				else
				{
					aoBaseContractsWithDiscList.add(loContractBean);
				}
			}
		}
		catch (Exception aoExp)
		{
			// Handle the Exception type exception and set context data
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while Batch Process in filterBaseContractsList() method", aoExp);
			loAppEx.addContextData("Exception occured while Batch Process in filterBaseContractsList() method", loAppEx);
			LOG_OBJECT.Error("Error occured while Batch Process in filterBaseContractsList() method", loAppEx);
			throw loAppEx;
		}
	}

	
	/**
	 * Release 3.12.0 Enhancement 6601.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	private void updateFlagAmendmentRegisteredInFMS()
	{
		
		try 
		{
			HashMap asHMReqdProp= new HashMap<String,String>();
			asHMReqdProp.put(HHSConstants.USER_ID, HHSConstants.SYSTEM_USER);
			moChannelObj.setData(HHSConstants.AO_HASH_MAP,asHMReqdProp);
System.out.println("================[updateFlagAmendmentRegisteredInFMS:getAmendmentRegisterdInFms]  :: getAmendmentRegisterdInFms");

			HHSTransactionManager.executeTransactionComponent(moChannelObj,	HHSConstants.GET_AMENDMENT_REGISTERED_IN_FMS);
			 List<ContractList> loAmendmentContractList =  (List<ContractList>) moChannelObj.getData(HHSConstants.LS_AMENDMENT_CONTRACT_LIST);

			 for( ContractList con  : loAmendmentContractList){
				 System.out.println("================[updateFlagAmendmentRegisteredInFMS:getAmendmentRegisterdInFms]  :: contract Id:"+ con.getContractId());
			 }
			 
			 //R7 Defect 8644: Part 3
			 /**
			  * This variable loRequestPartialMergeList will get the list of all the contracts which are requested for the partial merge 
			  * by the city user.
			  * This variable loAddListOfAMAndRP will get the list of all the contracts including the list available in loRequestPartialMergeList
			  * and loAmendmentContractList.
			  */
			 List<ContractList> loRequestPartialMergeList= (List<ContractList>) moChannelObj.getData("lsPartialMergeContractList");

			 for( ContractList con  : loRequestPartialMergeList){
				 System.out.println("================[updateFlagAmendmentRegisteredInFMS:lsPartialMergeContractList]  :: contract Id:"+ con.getContractId());
			 }
			 List<ContractList> loAddListOfAMAndRP=ListUtils.union(loAmendmentContractList, loRequestPartialMergeList);
			 //the below if condition part should not execute if the ePin is generated and make the IS_FMS_Registered=1

for( ContractList con  : loRequestPartialMergeList){
	System.out.println("================[updateFlagAmendmentRegisteredInFMS:Union Amendment for Merge ]  :: contract Id:"+ con.getContractId());
}

			 
			 if(loAddListOfAMAndRP!=null && !loAddListOfAMAndRP.isEmpty())
			 {
				 for (ContractList loContractBean : loAddListOfAMAndRP) {
					 try 
						{
System.out.println("================[updateFlagAmendmentRegisteredInFMS:UPDATE_AND_PARTIAL_MERGE_AMENDMENT_REGISTERED_IN_FMS ]  :: contract Id:"+ loContractBean.getContractId());						 
						 
					 asHMReqdProp.put(HHSConstants.CONTRACT_ID, loContractBean.getContractId());
					 moChannelObj.setData(HHSConstants.LS_AMENDMENT_CONTRACT_ID,loContractBean.getContractId());
					 HHSTransactionManager.executeTransactionComponent(moChannelObj,
								HHSConstants.UPDATE_AND_PARTIAL_MERGE_AMENDMENT_REGISTERED_IN_FMS);
						}
					 catch (ApplicationException aoAppExp)
						{
							// Log the Exception thrown and skip to next COntract
							LOG_OBJECT
									.Error("Error occured while Batch Process in updateAndPartialMergeAmendmentRegisteredInFMS()" + " : Failure Reason - " + aoAppExp);
						}
				 }
				 
				//Start: Update base budget end date in release 7 for defect 8705
				 for (Iterator iterator = loAmendmentContractList.iterator(); iterator.hasNext();)
					{
					 ContractList contractBean = (ContractList) iterator.next();
System.out.println("================[updateFlagAmendmentRegisteredInFMS:UPDATE_BUDGET_END_DATE_WITH_AMEND_END_DATE]  :: contract Id:"+ contractBean.getContractId());						 
					 
						moChannelObj.setData("asContractId",contractBean.getContractId());
						HHSTransactionManager.executeTransactionComponent(moChannelObj,
									HHSConstants.UPDATE_BUDGET_END_DATE_WITH_AMEND_END_DATE);
					}
				 //End
				
			 }
		} 
		catch (ApplicationException aoAppExp)
		{
			// Log the Exception thrown and skip to next COntract
			LOG_OBJECT
					.Error("Error occured while Batch Process in updateFlagAmendmentRegisteredInFMS()" + " : Failure Reason - " + aoAppExp);
		}
	}
	
	/**
	 * Release 3.12.0 Enhancement 6601.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void markAmendmentETLRegistredWhichAreRegisteredInFMS()
	{
		
		try 
		{
			HashMap asHMReqdProp= new HashMap<String,String>();
			asHMReqdProp.put(HHSConstants.USER_ID, HHSConstants.SYSTEM_USER);
			moChannelObj.setData(HHSConstants.AO_HASH_MAP,asHMReqdProp);
			HHSTransactionManager.executeTransactionComponent(moChannelObj,
					HHSConstants.MARK_AMENDMENT_ETL_REGISTERED_WHICH_ARE_REGISTERED_IN_FMS);
			System.out.println("=====ContractStatusUpdateBatch :: markAmendmentETLRegistredWhichAreRegisteredInFMS======");
		} 
		catch (ApplicationException aoAppExp)
		{
			// Log the Exception thrown and skip to next COntract
			LOG_OBJECT
					.Error("Error occured while Batch Process in markAmendmentETLRegistredWhichAreRegisteredInFMS()" + " : Failure Reason - " + aoAppExp);
		}
	}
	
	//R7 Start: Defect 8644 part 3
	/**
	 * This method will mark the requested amendment as ETL registered.
	 * It will update the flag REQUEST_PARTIAL_MERGE=1.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void markAmendmentETLRegistredWithPartialMergeRequest()
	{
		
		try 
		{
			HashMap asHMReqdProp= new HashMap<String,String>();
			asHMReqdProp.put(HHSConstants.USER_ID, HHSConstants.SYSTEM_USER);
			moChannelObj.setData(HHSConstants.AO_HASH_MAP,asHMReqdProp);
			HHSTransactionManager.executeTransactionComponent(moChannelObj,
					HHSConstants.MARK_AMENDMENT_ETL_REGISTERED_WITH_PARTIAL_MERGE_REQUEST);
			System.out.println("=====ContractStatusUpdateBatch :: markAmendmentETLRegistredWithPartialMergeRequest======");
		} 
		catch (ApplicationException aoAppExp)
		{
			// Log the Exception thrown and skip to next Contract
			LOG_OBJECT
					.Error("Error occured while Batch Process in markAmendmentETLRegistredWithPartialMergeRequest()" + " : Failure Reason - " + aoAppExp);
		}
	}
	//R7 End: Defect 8644 part 3
	
	/**
	 * This method is for filtering/categorizing Contract Amendments list for
	 * Batch Process on the basis of discrepancy flag
	 * 
	 * @param aoAmendmentContractsList - List<ContractBean> object
	 * @param aoPositiveAmendmentContractsWithNoDiscList - List<ContractBean>
	 *            object
	 * @param aoPositiveAmendmentContractsWithDiscList - List<ContractBean>
	 *            object
	 * @param aoNegativeAmendmentContractsWithNoDiscList - List<ContractBean>
	 *            object
	 * @param aoNegativeAmendmentContractsWithDiscList - List<ContractBean>
	 *            object
	 * @throws ApplicationException
	 */
	private void filterAmendmentContractsList(List<ContractBean> aoAmendmentContractsList,
			List<ContractBean> aoPositiveAmendmentContractsWithNoDiscList,
			List<ContractBean> aoPositiveAmendmentContractsWithDiscList,
			List<ContractBean> aoNegativeAmendmentContractsWithNoDiscList,
			List<ContractBean> aoNegativeAmendmentContractsWithDiscList) throws ApplicationException
	{
		ContractBean loContractBean = null;
		try
		{
			Iterator<ContractBean> loIteratorSett = aoAmendmentContractsList.iterator();
			while (loIteratorSett.hasNext())
			{
				loContractBean = loIteratorSett.next();

				// Check if the contract amount is positive or zero - yields to
				// be positive amendment else negative

				boolean lbIsPositiveAmendment = new BigDecimal(loContractBean.getContractAmount())
						.compareTo(BigDecimal.ZERO) >= HHSConstants.INT_ZERO ? true : false;

				// check if discrepancy flag is set or not
				// setting discrepancy flag explicitly to false since discrepancy need not be checked for running the batch.
				boolean lbIsWithDiscrepancy = false;

				if (lbIsWithDiscrepancy && lbIsPositiveAmendment)
				{
					aoPositiveAmendmentContractsWithDiscList.add(loContractBean);
				}
				else if (!lbIsWithDiscrepancy && lbIsPositiveAmendment)
				{
					aoPositiveAmendmentContractsWithNoDiscList.add(loContractBean);
				}
				else if (lbIsWithDiscrepancy && !lbIsPositiveAmendment)
				{
					aoNegativeAmendmentContractsWithDiscList.add(loContractBean);
				}
				else
				{
					aoNegativeAmendmentContractsWithNoDiscList.add(loContractBean);
				}
			}
		}
		catch (Exception aoExp)
		{
			// Handle the Exception type exception and set context data
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while Batch Process in filterAmendmentContractsList() method", aoExp);
			loAppEx.addContextData("Exception occured while Batch Process in filterAmendmentContractsList() method",
					loAppEx);
			LOG_OBJECT.Error("Error occured while Batch Process in filterAmendmentContractsList() method", loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * This method is for processing batch for Contracts list with No
	 * discrepancy
	 * <ul>
	 * <li>Runs Batch process in loop for the contracts in list - marks them as
	 * Registered</li>
	 * </ul>
	 * 
	 * @param aoBaseContractsWithNoDiscList - List<ContractBean> object
	 */
	private void processBaseContractsWithNoDisc(List<ContractBean> aoBaseContractsWithNoDiscList)
	{
		// Check the list of contract with no discrepancies is not empty
		// - if empty, just skip this process for these
		if (null != aoBaseContractsWithNoDiscList && !aoBaseContractsWithNoDiscList.isEmpty())
		{
			ContractBean loContractBean = null;
			Iterator<ContractBean> loIteratorSett = aoBaseContractsWithNoDiscList.iterator();

			// Iterate through the ContractBean list obtained
			while (loIteratorSett.hasNext())
			{
				loContractBean = loIteratorSett.next();
				try
				{
					LOG_OBJECT.Debug("\nProcessing Contract- ContractId:" + loContractBean.getContractId()
							+ ";Contract Title:'" + loContractBean.getContractTitle() + "'.\n");

					moChannelObj.setData(HHSConstants.AO_CONTRACT_BEAN, loContractBean);
					// Start Release 3.8.0 Enhancement 6481 
					HashMap loHmDocReqProps = new HashMap();
					loHmDocReqProps.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, false);
					moChannelObj.setData(HHSConstants.LO_HM_DOC_REQ_PROPS, loHmDocReqProps);
					moChannelObj.setData(HHSConstants.CONTRACT_ID, loContractBean.getContractId());
					moChannelObj.setData(HHSConstants.ERROR_CHECK_RULE, true);
					HHSUtil.addAuditDataToChannel(moChannelObj, HHSConstants.CONTRACT_DELETION,
							HHSConstants.CONTRACT_DELETION, HHSConstants.CONTRACT + " " + loContractBean.getContractId() + " "
									+ HHSConstants.DELETED, HHSConstants.CONTRACT, loContractBean.getContractId(), "system",
							HHSConstants.ACCELERATOR_AUDIT, HHSConstants.AUDIT_BEAN);
					HHSTransactionManager.executeTransactionComponent(moChannelObj,
							HHSConstants.RUN_BATCH_BASE_CONTRACT_WITH_NO_DISC);
					// End Release 3.8.0 Enhancement 6481 
					LOG_OBJECT.Debug("\nContract- ContractId:" + loContractBean.getContractId() + ";Contract Title:'"
							+ loContractBean.getContractTitle() + "' processed successfully.\n");
				}
				catch (ApplicationException aoAppExp)
				{
					// Log the Exception thrown and skip to next COntract
					LOG_OBJECT
							.Error("Error occured while Batch Process in processBaseContractsWithNoDisc() method for ContractId - "
									+ loContractBean.getContractId()
									+ " : Contract Title - "
									+ loContractBean.getContractTitle() + " : Failure Reason - " + aoAppExp);

					// Update the flag for batch failure for a contract
					moBatchExecutedSuccessfuly = Boolean.FALSE;
				}
			}
		}
	}

	/**
	 * This method is for processing batch for Contracts list with discrepancy
	 * <ul>
	 * <li>Checks if Batch run needed for this contract</li>
	 * <li>Runs Batch process for this if needed else skips it</li>
	 * <li>Marks the Contract as Registered and removes the discrepancy</li>
	 * </ul>
	 * 
	 * @param aoBaseContractsWithDiscList - List<ContractBean> object *
	 */
	private void processBaseContractsWithDisc(List<ContractBean> aoBaseContractsWithDiscList)
	{
		// Check the list of contract with discrepancies is not empty
		// - if empty, just skip this process for these
		if (null != aoBaseContractsWithDiscList && !aoBaseContractsWithDiscList.isEmpty())
		{
			ContractBean loContractBean = null;
			Iterator<ContractBean> loIteratorSett = aoBaseContractsWithDiscList.iterator();

			// Iterate through the ContractBean list obtained
			while (loIteratorSett.hasNext())
			{
				loContractBean = loIteratorSett.next();
				try
				{
					LOG_OBJECT.Debug("\nProcessing Contract- ContractId:" + loContractBean.getContractId()
							+ ";Contract Title:'" + loContractBean.getContractTitle() + "'.\n");
					System.out.println("\n **************ContractStatusUpdateBatch :: processBaseContractsWithDisc :: ContractId: " + loContractBean.getContractId());
					
					updateBeanWithRefinedData(loContractBean);
					moChannelObj.setData(HHSConstants.AO_CONTRACT_BEAN, loContractBean);

					// Start Release 3.8.0 Enhancement 6481 
					HashMap loHmDocReqProps = new HashMap();
					loHmDocReqProps.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, false);
					moChannelObj.setData(HHSConstants.LO_HM_DOC_REQ_PROPS, loHmDocReqProps);
					moChannelObj.setData(HHSConstants.CONTRACT_ID, loContractBean.getContractId());
					moChannelObj.setData(HHSConstants.ERROR_CHECK_RULE, true);
					HHSUtil.addAuditDataToChannel(moChannelObj, HHSConstants.CONTRACT_DELETION,
							HHSConstants.CONTRACT_DELETION, HHSConstants.CONTRACT + " " + loContractBean.getContractId() + " "
									+ HHSConstants.DELETED, HHSConstants.CONTRACT, loContractBean.getContractId(), "system",
							HHSConstants.ACCELERATOR_AUDIT, HHSConstants.AUDIT_BEAN);
					// End Release 3.8.0 Enhancement 6481 
					// Check if the Contract needs to run Batch process on it or
					// not (no need to run batch if contract value remains same
					// and the start and end FY are also same in HHS DB and in
					// FMS)
					System.out.println("\n **************ContractStatusUpdateBatch :: processBaseContractsWithDisc :: loContractBean.isBatchRunNeeded() " + loContractBean.isBatchRunNeeded());
					if (loContractBean.isBatchRunNeeded())
					{
						System.out.println("\n **************ContractStatusUpdateBatch :: processBaseContractsWithDisc :: RUN_BATCH_BASE_CONTRACT_WITH_DISC");					  
						HHSTransactionManager.executeTransactionComponent(moChannelObj,
								HHSConstants.RUN_BATCH_BASE_CONTRACT_WITH_DISC);
					}
					else
					{
						// Remove the discrepancies and mark the Contract as
						// 'Registered'
						System.out.println("\n **************ContractStatusUpdateBatch :: processBaseContractsWithDisc :: RUN_BATCH_BASE_CONTRACT_WITH_DISC_NO_BASE_STR_CHANGE");
						HHSTransactionManager.executeTransactionComponent(moChannelObj,
								HHSConstants.RUN_BATCH_BASE_CONTRACT_WITH_DISC_NO_BASE_STR_CHANGE);
					}

					LOG_OBJECT.Debug("\nContract- ContractId:" + loContractBean.getContractId() + ";Contract Title:'"
							+ loContractBean.getContractTitle() + "' processed successfully.\n");
				}
				catch (ApplicationException aoAppExp)
				{
					// Log the Exception thrown and skip to next Contract
					LOG_OBJECT
							.Error("Error occured while Batch Process in processBaseContractsWithDisc() method for ContractId - "
									+ loContractBean.getContractId()
									+ " : Contract Title - "
									+ loContractBean.getContractTitle() + " : Failure Reason - " + aoAppExp);

					// Update the flag for batch failure for a contract
					moBatchExecutedSuccessfuly = Boolean.FALSE;
				}
			}
		}
	}

	/**
	 * <p>
	 * This method is for processing batch for Positive Contract Amendments list
	 * with No discrepancy
	 * <ul>
	 * <li>Runs Batch process for this Amendment</li>
	 * <li>Marks the Contract as Registered and removes the discrepancy</li>
	 * <li>Two service method are added for agency interface module.It update
	 * remaining and YTD invoiced amount in line items table</li>
	 * </ul>
	 * Method Updated in R4
	 * </p>
	 * @param aoPositiveAmendmentContractsWithNoDiscList - List<ContractBean>
	 *            object
	 */
	private void processPositiveAmendmentContractsWithNoDisc(
			List<ContractBean> aoPositiveAmendmentContractsWithNoDiscList)
	{
		// Process Positive Amendment Contracts with No Discrepancies
		// for Batch if the list is not null
		if (null != aoPositiveAmendmentContractsWithNoDiscList && !aoPositiveAmendmentContractsWithNoDiscList.isEmpty())
		{
			ContractBean loContractBean = null;
			Iterator<ContractBean> loIteratorSettAmendContracts = aoPositiveAmendmentContractsWithNoDiscList.iterator();

			// Iterate through the ContractBean list obtained
			while (loIteratorSettAmendContracts.hasNext())
			{
				loContractBean = loIteratorSettAmendContracts.next();
				try
				{
					LOG_OBJECT.Debug("\nProcessing Contract- ContractId:" + loContractBean.getContractId()
							+ ";Contract Title:'" + loContractBean.getContractTitle() + "'.\n");
System.out.println("\n-------------------    " +
		"[processPositiveAmendmentContractsWithNoDisc]Processing Contract- ContractId:" + loContractBean.getContractId() + ";Contract Title:'" + loContractBean.getContractTitle() + 
		"'.\n   [Contract End date]" +loContractBean.getParentContractEndDate().toString() );

					moChannelObj.setData(HHSConstants.AO_CONTRACT_BEAN, loContractBean);
					// R4 Change: channel variable added for executing service
					// updateYtdAndRemainingAmountInLineItems and
					// updateYtdAndRemainingAmountInBudgetAndSubBudget
					moChannelObj.setData(HHSConstants.LB_FINAL_FINISH, true);
					//changes for agency outbound interafce 6644
					moChannelObj.setData(HHSConstants.AS_USER_ID, HHSConstants.SYSTEM_USER);
					HHSTransactionManager.executeTransactionComponent(moChannelObj,
							HHSConstants.RUN_BATCH_POSITIVE_AMENDMENT_WITH_NO_DISC);

					LOG_OBJECT.Debug("\nContract- ContractId:" + loContractBean.getContractId() + ";Contract Title:'"
							+ loContractBean.getContractTitle() + "' processed successfully.\n");
System.out.println("\n ---------------------------" +
		"[processPositiveAmendmentContractsWithNoDisc]END of Processing Contract- ContractId:" + loContractBean.getContractId() + ";Contract Title:'" + loContractBean.getContractTitle() 
		+ "'.\n   [Contract End date]"+loContractBean.getParentContractEndDate().toString());
				}
				catch (ApplicationException aoAppExp)
				{
					// Log the Exception thrown and skip to next COntract
					LOG_OBJECT
							.Error("Error occured while Batch Process in processPositiveAmendmentContractsWithNoDisc() method for ContractId - "
									+ loContractBean.getContractId()
									+ " : Contract Title - "
									+ loContractBean.getContractTitle() + " : Failure Reason - " + aoAppExp);

					// Update the flag for batch failure for a contract
					moBatchExecutedSuccessfuly = Boolean.FALSE;
				}
			}
		}
	}

	/**
	 * <p>
	 * This method is for processing batch for Positive Contract Amendments list
	 * with discrepancy
	 * <ul>
	 * <li>Checks if Batch run needed for this contract</li>
	 * <li>Runs Batch process for this if needed else skips it</li>
	 * <li>Marks the Contract as Registered and removes the discrepancy</li>
	 * <li>Two service method are added for agency interface module.It update
	 * remaining and YTD invoiced amount in line items table</li>
	 * </ul>
	 * Method Updated in R4
	 * </p>
	 * @param aoPositiveAmendmentContractsWithDiscList - List<ContractBean>
	 *            object
	 */
	private void processPositiveAmendmentContractsWithDisc(List<ContractBean> aoPositiveAmendmentContractsWithDiscList)
	{
		// process Positive Amendment Contracts with Discrepancies for
		// Batch if the list is not null
		if (null != aoPositiveAmendmentContractsWithDiscList && !aoPositiveAmendmentContractsWithDiscList.isEmpty())
		{
			ContractBean loContractBean = null;
			Iterator<ContractBean> loIteratorSett = aoPositiveAmendmentContractsWithDiscList.iterator();

			// Iterate through the ContractBean list obtained
			while (loIteratorSett.hasNext())
			{
				loContractBean = loIteratorSett.next();
				try
				{
					LOG_OBJECT.Debug("\nProcessing Contract- ContractId:" + loContractBean.getContractId()
							+ ";Contract Title:'" + loContractBean.getContractTitle() + "'.\n");

					updateBeanWithRefinedData(loContractBean);
					moChannelObj.setData(HHSConstants.AO_CONTRACT_BEAN, loContractBean);
					// R4 Change: channel variable added for executing service
					// updateYtdAndRemainingAmountInLineItems and
					// updateYtdAndRemainingAmountInBudgetAndSubBudget
					moChannelObj.setData(HHSConstants.LB_FINAL_FINISH, true);
					//changes for agency outbound interafce 6644
					moChannelObj.setData(HHSConstants.AS_USER_ID, HHSConstants.SYSTEM_USER);
					if (loContractBean.isBatchRunNeeded())
					{
						HHSTransactionManager.executeTransactionComponent(moChannelObj,
								HHSConstants.RUN_BATCH_POSITIVE_AMENDMENT_WITH_DISC);
					}
					else
					{
						// Remove discrepancy and set the status as Registered
						HHSTransactionManager.executeTransactionComponent(moChannelObj,
								HHSConstants.RUN_BATCH_POSITIVE_AMENDMENT_WITH_DISC_NO_BASE_STR_CHANGE);
					}

					LOG_OBJECT.Debug("\nContract- ContractId:" + loContractBean.getContractId() + ";Contract Title:'"
							+ loContractBean.getContractTitle() + "' processed successfully.\n");
				}
				catch (ApplicationException aoAppExp)
				{
					// Log the Exception thrown and skip to next COntract
					LOG_OBJECT
							.Error("Error occured while Batch Process in processPositiveAmendmentContractsWithDisc() method for ContractId - "
									+ loContractBean.getContractId()
									+ " : Contract Title - "
									+ loContractBean.getContractTitle() + " : Failure Reason - " + aoAppExp);

					// Update the flag for batch failure for a contract
					moBatchExecutedSuccessfuly = Boolean.FALSE;
				}
			}
		}
	}

	/**
	 * This method is for processing batch for Negative Contract Amendments list
	 * with No discrepancy
	 * <ul>
	 * <li>Runs Batch process for this Amendment</li>
	 * <li>Marks the Contract as Registered and removes the discrepancy</li>
	 * </ul>
	 * 
	 * @param aoNegativeAmendmentContractsWithNoDiscList - List<ContractBean>
	 *            object
	 */
	private void processNegativeAmendmentContractsWithNoDisc(
			List<ContractBean> aoNegativeAmendmentContractsWithNoDiscList)
	{
		// process Negative Amendment Contracts with No Discrepancies
		// for
		// Batch if the list is not null
		System.out.println("====ContractStatusUpdateBatch :: processNegativeAmendmentContractsWithNoDisc==");
		if (null != aoNegativeAmendmentContractsWithNoDiscList && !aoNegativeAmendmentContractsWithNoDiscList.isEmpty())
		{
			ContractBean loContractBean = null;
			Iterator<ContractBean> loIteratorSettAmendContracts = aoNegativeAmendmentContractsWithNoDiscList.iterator();

			// Iterate through the ContractBean list obtained
			while (loIteratorSettAmendContracts.hasNext())
			{
				loContractBean = loIteratorSettAmendContracts.next();
				try
				{
					LOG_OBJECT.Debug("\nProcessing Contract- ContractId:" + loContractBean.getContractId()
							+ ";Contract Title:'" + loContractBean.getContractTitle() + "'.\n");

					System.out.println("\n------------------ContractStatusUpdateBatch-    " +
							"[processNegativeAmendmentContractsWithNoDisc] RUN_BATCH_NEGATIVE_AMENDMENT_WITH_NO_DISC Processing Contract- ContractId:" + loContractBean.getContractId() + ";Contract Title:'" + loContractBean.getContractTitle() + "'.\n");

					moChannelObj.setData(HHSConstants.AO_CONTRACT_BEAN, loContractBean);
					HHSTransactionManager.executeTransactionComponent(moChannelObj,
							HHSConstants.RUN_BATCH_NEGATIVE_AMENDMENT_WITH_NO_DISC);

					LOG_OBJECT.Debug("\nContract- ContractId:" + loContractBean.getContractId() + ";Contract Title:'"
							+ loContractBean.getContractTitle() + "' processed successfully.\n");
					System.out.println("\n------------------ContractStatusUpdateBatch-    " +
							"[processNegativeAmendmentContractsWithNoDisc]Processing Contract- ContractId:" + loContractBean.getContractId() + ";Contract Title:'" + loContractBean.getContractTitle() + "' processed successfully.\n");
				}
				catch (ApplicationException aoAppExp)
				{
					// Log the Exception thrown and skip to next COntract
					LOG_OBJECT
							.Error("Error occured while Batch Process in processNegativeAmendmentContractsWithNoDisc() method for ContractId - "
									+ loContractBean.getContractId()
									+ " : Contract Title - "
									+ loContractBean.getContractTitle() + " : Failure Reason - " + aoAppExp);

					// Update the flag for batch failure for a contract
					moBatchExecutedSuccessfuly = Boolean.FALSE;
				}
			}
		}
	}

	/**
	 * This method is for processing batch for Negative Contract Amendments list
	 * with discrepancy
	 * <ul>
	 * <li>Checks if Batch run needed for this contract</li>
	 * <li>Runs Batch process for this if needed else skips it</li>
	 * <li>Marks the Contract as Registered and removes the discrepancy</li>
	 * </ul>
	 * 
	 * @param aoNegativeAmendmentContractsWithDiscList - List<ContractBean>
	 *            object
	 */
	private void processNegativeAmendmentContractsWithDisc(List<ContractBean> aoNegativeAmendmentContractsWithDiscList)
	{
		// process Negative Amendment Contracts with Discrepancies for
		// Batch if the list is not null
		if (null != aoNegativeAmendmentContractsWithDiscList && !aoNegativeAmendmentContractsWithDiscList.isEmpty())
		{
			ContractBean loContractBean = null;
			Iterator<ContractBean> loIteratorSett = aoNegativeAmendmentContractsWithDiscList.iterator();

			// Iterate through the ContractBean list obtained
			while (loIteratorSett.hasNext())
			{
				loContractBean = loIteratorSett.next();
				try
				{
					LOG_OBJECT.Debug("\nProcessing Contract- ContractId:" + loContractBean.getContractId()
							+ ";Contract Title:'" + loContractBean.getContractTitle() + "'.\n");

					updateBeanWithRefinedData(loContractBean);
					moChannelObj.setData(HHSConstants.AO_CONTRACT_BEAN, loContractBean);

					// Check if Batch Run is needed or not
					if (loContractBean.isBatchRunNeeded())
					{
						HHSTransactionManager.executeTransactionComponent(moChannelObj,
								HHSConstants.RUN_BATCH_NEGATIVE_AMENDMENT_WITH_DISC);
					}
					else
					{
						// Remove discrepancies and Mark the Amendment as
						// Registered
						HHSTransactionManager.executeTransactionComponent(moChannelObj,
								HHSConstants.RUN_BATCH_NEGATIVE_AMENDMENT_WITH_DISC_NO_BASE_STR_CHANGE);
					}

					LOG_OBJECT.Debug("\nContract- ContractId:" + loContractBean.getContractId() + ";Contract Title:'"
							+ loContractBean.getContractTitle() + "' processed successfully.\n");
				}
				catch (ApplicationException aoAppExp)
				{
					// Log the Exception thrown and skip to next COntract
					LOG_OBJECT
							.Error("Error occured while Batch Process in processNegativeAmendmentContractsWithDisc() method for ContractId - "
									+ loContractBean.getContractId()
									+ " : Contract Title - "
									+ loContractBean.getContractTitle() + " : Failure Reason - " + aoAppExp);

					// Update the flag for batch failure for a contract
					moBatchExecutedSuccessfuly = Boolean.FALSE;
				}
			}
		}
	}

	/**
	 * This method is for updating the Contract Bean (Contract Data - Base and
	 * Amendment) with calculative fields
	 * <ul>
	 * <li>Updates Discrepancy Status in the Bean</li>
	 * <li>Updates Fiscal Years - startFiscalYear and endFiscalYear(for HHS and
	 * FMS)</li>
	 * <li>Updates if Batch run is needed or not</li>
	 * <li>Updates which kind of discrepancy exists in the contract/amendment if
	 * it exists</li>
	 * </ul>
	 * 
	 * @param aoContractBean - ContractBean object
	 * @throws ApplicationException
	 */
	private void updateBeanWithRefinedData(ContractBean aoContractBean) throws ApplicationException
	{
		System.out.println("\n **************ContractStatusUpdateBatch :: updateBeanWithRefinedData :: bean :: "+aoContractBean);
		HashMap<String, Integer> loContractFYDetails = null;
		try
		{
			BigDecimal loContractAmount = new BigDecimal(aoContractBean.getContractAmount());
			BigDecimal loFmsContractAmount = new BigDecimal(aoContractBean.getFmsContractAmount());
			Date loContractStartDate = DateUtil.getSqlDate(aoContractBean.getContractStartDate());
			Date loContractEndDate = DateUtil.getSqlDate(aoContractBean.getContractEndDate());
			Date loFmsContractStartDate = DateUtil.getSqlDate(aoContractBean.getFmsContractStartDate());
			Date loFmsContractEndDate = DateUtil.getSqlDate(aoContractBean.getFmsContractEndDate());
			HashMap<String, Integer> loFmsContractFYDetails = null;

			// Get calculated Fiscal Years for Contrast as per start date and
			// end date
			loContractFYDetails = HHSUtil.getFirstAndLastFYOfContract(loContractStartDate, loContractEndDate);
			Integer loContractStartFiscalYear = loContractFYDetails.get(HHSConstants.START_FISCAL_YEAR);
			Integer loContractEndFiscalYear = loContractFYDetails.get(HHSConstants.CONTRACT_END_FY);

			// Get calculated Fiscal Years for Contrast as per start date and
			// end date (as per FMS)
			loFmsContractFYDetails = HHSUtil.getFirstAndLastFYOfContract(loFmsContractStartDate, loFmsContractEndDate);
			Integer loFmsContractStartFiscalYear = loFmsContractFYDetails.get(HHSConstants.START_FISCAL_YEAR);
			Integer loFmsContractEndFiscalYear = loFmsContractFYDetails.get(HHSConstants.CONTRACT_END_FY);

			// Update the discrepancy in Contract Amount (defaults to false)
			if (!loContractAmount.equals(loFmsContractAmount))
			{
				aoContractBean.setDiscrepancyInContractAmount(HHSConstants.BOOLEAN_TRUE);
			}

			// Update the discrepancy in contract Start Date (defaults to false)
			if (loContractStartDate.compareTo(loFmsContractStartDate) != HHSConstants.INT_ZERO)
			{
				aoContractBean.setDiscrepancyInStartDate(HHSConstants.BOOLEAN_TRUE);
			}

			// Update the discrepancy in contract End Date (defaults to false)
			if (loContractEndDate.compareTo(loFmsContractEndDate) != HHSConstants.INT_ZERO)
			{
				aoContractBean.setDiscrepancyInEndDate(HHSConstants.BOOLEAN_TRUE);
			}

			// Set the fiscal years (Contract's Start, End and Contract Start
			// and End Fiscal Years as per FMS)
			aoContractBean.setContractStartFiscalYear(loContractStartFiscalYear);
			aoContractBean.setContractEndFiscalYear(loContractEndFiscalYear);
			aoContractBean.setFmsContractStartFiscalYear(loFmsContractStartFiscalYear);
			aoContractBean.setFmsContractEndFiscalYear(loFmsContractEndFiscalYear);

			// Set Total Fiscal Years as per Start and End Dates of Contract
			aoContractBean.setFmsTotalFiscalYearsCount(HHSConstants.INT_ONE + loFmsContractEndFiscalYear
					- loFmsContractStartFiscalYear);

			// Set Total Fiscal Years as per Start and End Dates of Contract (as
			// per FMS)
			aoContractBean.setTotalFiscalYearsCount(HHSConstants.INT_ONE + loContractEndFiscalYear
					- loContractStartFiscalYear);
			if (!aoContractBean.isDiscrepancyInContractAmount()
					&& aoContractBean.getContractStartFiscalYear().equals(
							aoContractBean.getFmsContractStartFiscalYear())
					&& aoContractBean.getContractEndFiscalYear().equals(aoContractBean.getFmsContractEndFiscalYear()))
			{
				aoContractBean.setBatchRunNeeded(HHSConstants.BOOLEAN_FALSE);
			}
			// In case of negative amendment - update the FMS finalized Contract
			// Amount (to be updated in it's Parent Base contract)
			if (aoContractBean.isDiscrepancyInContractAmount()
					&& (loContractAmount.compareTo(BigDecimal.ZERO) < HHSConstants.INT_ZERO))
			{
				BigDecimal loParentContractAmount = new BigDecimal(aoContractBean.getParentContractAmount());
				BigDecimal loFmsContractAmountAfterMerge = loParentContractAmount.subtract(loContractAmount).add(
						loFmsContractAmount);

				aoContractBean.setFmsContractAmountAfterMerge(loFmsContractAmountAfterMerge.toString());
			}
			
			System.out.println("\n **************ContractStatusUpdateBatch :: updateBeanWithRefinedData :: bean :: "+aoContractBean);
		}
		catch (ApplicationException aoAppExp)
		{
			// Handle the ApplicationException type Exception and set context
			// data
			aoAppExp.addContextData("Exception occured while Batch Process in updateBeanWithRefinedData() method",
					aoAppExp);
			LOG_OBJECT.Error("Error occured while Batch Process in updateBeanWithRefinedData() method", aoAppExp);
			throw aoAppExp;
		}
		catch (Exception aoExp)
		{
			// Handle the Exception type exception and set context data
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while Batch Process in updateBeanWithRefinedData() method", aoExp);
			loAppEx.addContextData("Exception occured while Batch Process in updateBeanWithRefinedData() method",
					loAppEx);
			LOG_OBJECT.Error("Error occured while Batch Process in updateBeanWithRefinedData() method", loAppEx);
			throw loAppEx;
		}
	}
}