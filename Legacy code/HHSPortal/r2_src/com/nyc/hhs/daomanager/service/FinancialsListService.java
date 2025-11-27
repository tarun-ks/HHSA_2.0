package com.nyc.hhs.daomanager.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.component.operations.WorkflowOperations;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSP8Constants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.constants.TransactionConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.model.ContractFinancialBean;
import com.nyc.hhs.model.ContractList;
import com.nyc.hhs.model.EPinDetailBean;
import com.nyc.hhs.model.FinancialWFBean;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.PaymentSortAndFilter;
import com.nyc.hhs.model.ProgramNameInfo;
import com.nyc.hhs.rule.Rule;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8SecurityOperations;
import com.nyc.hhs.service.filenetmanager.p8services.P8ProcessServiceForSolicitationFinancials;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;

import filenet.vw.api.VWSession;

/**
 * This service class will be used to get all data for Contract List page from
 * database using mappings and queries defined in ContractMapper.xml. Insertion
 * or updation of data can also be performed.
 * <p>
 * This class is updated in Release 7
 * </p>
 */
public class FinancialsListService extends ServiceState
{

	private static final LogInfo LOG_OBJECT = new LogInfo(FinancialsListService.class);

	/**
	 * This method is used to fetch all the contracts based on user
	 * type(Accelerator/Agency/Provider). Data will be fetched on basis of
	 * filter values as received in ContractListFilter as
	 * amendContractErrorCheckRule n input.
	 * 
	 * <ul>
	 * <li>1.If user type is Provider, then we will call the
	 * fetchContractListProvider query to get the required contracts list based
	 * on selected filter and sorting criteria and list of contract nos which
	 * are under amendment and has not been registered yet</li>
	 * <li>2.If user type is Agency, then we will call the
	 * fetchContractListAgency query to get the required contracts list based on
	 * selected filter and sorting criteria and list of contract nos which are
	 * under amendment and has not been registered yet</li>
	 * <li>3.If user type is Accelerator, then we will call the
	 * fetchContractListAccelerator query to get the required contracts list
	 * based on selected filter and sorting criteria and list of contract nos
	 * which are under amendment and has not been registered yet</li>
	 * <li>4. If any contract has the same contract no as amendment then
	 * pendingAMendment will be set true for it.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession mybatis SQL session
	 * @param aoContractFilterBean object of ContractList
	 * @param asUserType string containing user type
	 * @return loContractList a list of contracts
	 */
	@SuppressWarnings("unchecked")
	public List<ContractList> fetchContractListSummary(SqlSession aoMybatisSession, ContractList aoContractFilterBean,
			String asUserType) throws ApplicationException
	{
		List<ContractList> loContractList = null;
		List<String> loAmendmentContractNos = null;
		List<String> loRenewalContractNos = null;
		List<String> loContractForUpdate = null;
		List<String> loContractForRestriction = null;
		String lsStatus = null;
		Integer count = null;
		try
		{
			if (null != aoContractFilterBean.getProvider())
			{
				StringBuffer loProviderNameSb = new StringBuffer(HHSConstants.PERCENT).append(
						aoContractFilterBean.getProvider()).append(HHSConstants.PERCENT);
				aoContractFilterBean.setProvider(loProviderNameSb.toString());
			}
			if (aoContractFilterBean.getContractValueFrom() != null)
			{
				aoContractFilterBean.setContractValueFrom(aoContractFilterBean.getContractValueFrom().replaceAll(
						HHSConstants.COMMA, HHSConstants.EMPTY_STRING));
			}
			if (aoContractFilterBean.getContractValueTo() != null)
			{
				aoContractFilterBean.setContractValueTo(aoContractFilterBean.getContractValueTo().replaceAll(
						HHSConstants.COMMA, HHSConstants.EMPTY_STRING));
			}
			if (aoContractFilterBean.getContractStatusList() != null)
			{
				for (String ascontractStatus : aoContractFilterBean.getContractStatusList())
				{
					if (ascontractStatus.equalsIgnoreCase(HHSConstants.STATUS_PENDING_REG)
							//Begin R6.3 QC5690	
							//||	ascontractStatus.equalsIgnoreCase(HHSConstants.STATUS_PENDING_NOTIFICATION)
							//End R6.3 QC5690
							)
					{
						lsStatus = HHSConstants.STATUS_ETL_REG;
					}
				}
				if (null != lsStatus && !lsStatus.isEmpty())
				{
					aoContractFilterBean.getContractStatusList().add(lsStatus);
				}
				if (asUserType.equals(ApplicationConstants.PROVIDER_ORG))
				{
					loContractList = (List<ContractList>) DAOUtil.masterDAO(aoMybatisSession, aoContractFilterBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_CONTRACT_LIST_PROVIDER,
							HHSConstants.COM_NYC_HHS_MODEL_CONTRACT_LIST);
					loAmendmentContractNos = (List<String>) DAOUtil.masterDAO(aoMybatisSession, aoContractFilterBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_AMENDMENT_LIST_PROVIDER,
							HHSConstants.COM_NYC_HHS_MODEL_CONTRACT_LIST);
					// Release 5 Contract Restriction
					loContractForRestriction = (List<String>) DAOUtil.masterDAO(aoMybatisSession, aoContractFilterBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSR5Constants.FETCH_CONTRACT_FOR_RESTRICTION,
							HHSR5Constants.COM_NYC_HHS_MODEL_BASE_FILTER);
					count = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoContractFilterBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, "ViewUserAccessDropdownFlag",
							HHSR5Constants.COM_NYC_HHS_MODEL_BASE_FILTER);
					// Release 5 Contract Restriction
					
					//Start: Added in R7.11.0 QC9122
					loContractList = checkbudgetStatus(loContractList , aoMybatisSession) ;
					//End: Added in R7.11.0 QC9122
				}
				else if (asUserType.equals(ApplicationConstants.AGENCY_ORG))
				{
					loContractList = (List<ContractList>) DAOUtil.masterDAO(aoMybatisSession, aoContractFilterBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_CONTRACT_LIST_AGENCY,
							HHSConstants.COM_NYC_HHS_MODEL_CONTRACT_LIST);
					loAmendmentContractNos = (List<String>) DAOUtil.masterDAO(aoMybatisSession, aoContractFilterBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_AMENDMENT_LIST_AGENCY,
							HHSConstants.COM_NYC_HHS_MODEL_CONTRACT_LIST);
					loRenewalContractNos = renewContractList(aoMybatisSession);
					loContractForUpdate = (List<String>) DAOUtil.masterDAO(aoMybatisSession, aoContractFilterBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
							HHSConstants.FETCH_CONTRACT_UPDATE_APPROVED_ACTIVE_BUDGET,
							HHSConstants.COM_NYC_HHS_MODEL_CONTRACT_LIST);

					//Start: Added in R7.11.0 QC9122
					loContractList = checkbudgetStatus(loContractList , aoMybatisSession) ;
					//End: Added in R7.11.0 QC9122
				}
				else
				{
					loContractList = (List<ContractList>) DAOUtil.masterDAO(aoMybatisSession, aoContractFilterBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_CONTRACT_LIST_ACCELERATOR,
							HHSConstants.COM_NYC_HHS_MODEL_CONTRACT_LIST);
					loAmendmentContractNos = (List<String>) DAOUtil.masterDAO(aoMybatisSession, aoContractFilterBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_AMENDMENT_LIST_ACCELEARATOR,
							HHSConstants.COM_NYC_HHS_MODEL_CONTRACT_LIST);
					loRenewalContractNos = renewContractList(aoMybatisSession);
					loContractForUpdate = (List<String>) DAOUtil.masterDAO(aoMybatisSession, aoContractFilterBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
							HHSConstants.FETCH_CONTRACT_UPDATE_APPROVED_ACTIVE_BUDGET,
							HHSConstants.COM_NYC_HHS_MODEL_CONTRACT_LIST);

					//Start: Added in R7.11.0 QC9122
					loContractList = checkbudgetStatus(loContractList , aoMybatisSession) ;
					//End: Added in R7.11.0 QC9122
				}
			}
			String lsProviderName = aoContractFilterBean.getProvider();
			if (null != lsProviderName)
			{
				lsProviderName = lsProviderName.substring(HHSConstants.INT_ONE);
				lsProviderName = lsProviderName.substring(HHSConstants.INT_ZERO, lsProviderName.length()
						- HHSConstants.INT_ONE);
				aoContractFilterBean.setProvider(lsProviderName);
			}
			setMoState("Contract List fetched successfully for org Type:" + asUserType);
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while fetching Contracts List for org Id:" + asUserType);
			throw loExp;
		}
		catch (Exception aoAppExp)
		{
			setMoState("Error while fetching contract details");
			LOG_OBJECT.Error("Error while fetching contract details", aoAppExp);
			throw new ApplicationException("Error occured while getting contract data", aoAppExp);
		}

		if (loAmendmentContractNos != null)
		{
			List<Integer> loAllAmendedContracts = new ArrayList<Integer>();
			
			for (String lsAmendment : loAmendmentContractNos)
			{
				loAllAmendedContracts.add(Integer.parseInt(lsAmendment));
			}
			for (ContractList loContract : loContractList)
			{
				if (loAllAmendedContracts.contains(Integer.parseInt(loContract.getContractId())))
				{
					loContract.setPendingAmendment(true);
				}
			}
		}
		if (loRenewalContractNos != null)
		{
			List<Integer> loAllRenewContracts = new ArrayList<Integer>();
			
			for (String lsRenewal : loRenewalContractNos)
			{
				loAllRenewContracts.add(Integer.parseInt(lsRenewal));
			}
			for (ContractList loContract : loContractList)
			{
				if (loAllRenewContracts.contains(Integer.parseInt(loContract.getContractId())))
				{
					loContract.setAlreadyRenewed(true);
				}
			}
		}
		// Start Release 3.8.0 Enhancement 6481
		if (loContractForUpdate != null)
		{
			List<Integer> loAllUpdatewContracts = new ArrayList<Integer>();
			
			for (String lsContractUpdate : loContractForUpdate)
			{
				loAllUpdatewContracts.add(Integer.parseInt(lsContractUpdate));
			}
			for (ContractList loContract : loContractList)
			{
					if (loAllUpdatewContracts.contains(Integer.parseInt(loContract.getContractId())))
					{
						loContract.setContractUpdate(true);
					}
			}
		}
		// Release 5 Contract Restriction
		if (loContractForRestriction != null)
		{
			List<Integer> loAllRestrictionContracts = new ArrayList<Integer>();
			
			for (String lsContractForRestriction : loContractForRestriction)
			{
				loAllRestrictionContracts.add(Integer.parseInt(lsContractForRestriction));
			}
			for (ContractList loContract : loContractList)
			{
					if (loAllRestrictionContracts.contains(Integer.parseInt(loContract
									.getContractId())))
					{
						loContract.setContractAccess(false);
					}
			}
		}
		// Release 5 Contract Restriction
		if (loContractList != null && count != null && count == 0)
		{
			for (ContractList loContract : loContractList)
			{
				loContract.setViewUserAccessDropdown(false);
			}
		}

		Date loCurrentDate = DateUtil.getSqlDate(DateUtil.getCurrentDate());
		loContractList = displayFYconfig(loContractList, loCurrentDate, aoMybatisSession);
		loContractList = displayViewCof(loContractList, aoMybatisSession);
		return loContractList;
	}

	/**
	 * This method check contract list 
	 * <ul>
	 * <li>set needAmendRemoved true when contract is in 'Pending Registration' and budget is in 'Pending Submission' </li>
	 * </ul>
	 * 
	 * @param aoMybatisSession batis session as input
	 * @return loRenewalContractNos as input
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<ContractList> checkbudgetStatus(    List<ContractList> aoConList , SqlSession aoMybatisSession )
			throws ApplicationException	{
		if(aoConList == null || aoConList.isEmpty()) return aoConList;
	      
        /* [Start] R8.11.1      */
        HashMap <String, List <String>> loMap = new HashMap <String, List <String> >();
        ArrayList <String> loConL  = new ArrayList<String>();
        for(ContractList  loCon : aoConList  ){
            if( loCon.getContractId() != null && loCon.getContractId().length() > 0){
                loConL.add(loCon.getContractId());
            }
        }
        loMap.put(HHSConstants.FETCH_CONTRACT_ID_LIST_N, loConL);
        List<ContractList> loContractBudgetStatusList = (List<ContractList>) DAOUtil.masterDAO(aoMybatisSession, loMap,
                HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_CONTRACT_BUDGET_STATUS_ID_LIST_N,
                HHSConstants.JAVA_UTIL_MAP);
        /* [End] R8.11.1      */
		
		if(loContractBudgetStatusList == null || loContractBudgetStatusList.isEmpty()) return aoConList;
		
		/* Start QC 9122 R 8.1 Contract list Pending Registration Status (61) with at least one Approved Budget (86) */
		List<ContractList> loContractBudgetApprovedStatusList = (List<ContractList>) DAOUtil.masterDAO(aoMybatisSession, null,
				HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_CONTRACT_BUDGET_APPROVED_STATUS_ID_LIST,
				HHSConstants.COM_NYC_HHS_MODEL_CONTRACT_LIST);
				
		Map<String,String> loConMapApproved = new HashMap<String,String>();
		
		if(loContractBudgetApprovedStatusList != null && !loContractBudgetApprovedStatusList.isEmpty()) 
		{		
			for( ContractList  loConBudgetAprvd :  loContractBudgetApprovedStatusList  ){
				loConMapApproved.put(loConBudgetAprvd.getContractId(), loConBudgetAprvd.getBudgetStatusId() ); 
			}
		}
		/* End QC 9122 R 8.1 Contract list Pending Registration Status (61) with at least one Approved Budget (86) */  
		
		Map<String,String> loConMap = new HashMap<String,String>();
		for( ContractList  loConBudget :  loContractBudgetStatusList ){
			loConMap.put(loConBudget.getContractId(), loConBudget.getBudgetStatusId() ); 
		}
		
		for( ContractList  loCon : aoConList   ){
			if( loConMap.containsKey(loCon.getContractId()) 
			    /* Start R 8.1 QC 9122 add condition */
				&& 	loConMapApproved != null && !loConMapApproved.containsKey(loCon.getContractId())
				/* End R 8.1 QC 9122 add condition */
				)
			{
				loCon.setNeedAmendRemoved(Boolean.TRUE);
			}
		}
				
		return aoConList;
	}
	
	/**
	 * This method check renew contract list
	 * <ul>
	 * <li>calls query 'renewalRecordExistForContractDropDown'</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession batis session as input
	 * @return loRenewalContractNos as input
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<String> renewContractList(SqlSession aoMybatisSession) throws ApplicationException
	{
		List<String> loRenewalContractNos;
		List loList = new ArrayList();
		loList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_CONTRACT_PENDING_REGISTARTION));
		loList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_CONTRACT_PENDING_CONFIGURATION));
		loList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_CONTRACT_PENDING_COF));
		HashMap loHashMap = new HashMap();
		loHashMap.put(HHSConstants.CLC_STATUS_ID, loList);
		loHashMap.put(HHSConstants.CL_CONTRACT_TYPE_ID, HHSConstants.CONTRACT_RENEWAL_TYPE_ID);
		loRenewalContractNos = (List<String>) DAOUtil.masterDAO(aoMybatisSession, loHashMap,
				HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.RENEWAL_RECORD_EXIST_FOR_CONTRACT_DROP_DOWN,
				HHSConstants.JAVA_UTIL_HASH_MAP);
		return loRenewalContractNos;
	}

	/**
	 * This method is used to fetch agency list to be used on contract list
	 * filter page for Accelerator and provider users.
	 * <ul>
	 * <li>If usertype is Accelerator/Provider, then fetchAgencyNames will be
	 * called to fetch the list of agencies</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession mybatis SQL session
	 * @param asUserType string containing user type
	 * @return loAgencyDetails a list of hashmap containing agencyid and
	 *         agencyname
	 */
	@SuppressWarnings("unchecked")
	public List<HashMap<String, String>> fetchAgencyNames(SqlSession aoMybatisSession, String asUserType)
			throws ApplicationException
	{
		List<HashMap<String, String>> loAgencyDetails = new ArrayList<HashMap<String, String>>();
		try
		{
			if (!asUserType.equals(ApplicationConstants.AGENCY_ORG))
			{
				loAgencyDetails = (List<HashMap<String, String>>) DAOUtil.masterDAO(aoMybatisSession, null,
						HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_AGENCY_NAMES, null);
			}
			setMoState("FinancialsListService: fetchAgencyNames() All agency names fetched successfully.");
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while fetching agency names");
			throw loExp;
		}
		catch (Exception aoAppExp)
		{
			setMoState("Error while fetching agency names");
			LOG_OBJECT.Error("Error while fetching agency names", aoAppExp);
			throw new ApplicationException("Error while fetching agency names", aoAppExp);
		}
		return loAgencyDetails;
	}

	/**
	 * This method gets count of all contracts available for particular user.
	 * <ul>
	 * <li>1.If user type is Provider, then we will call the
	 * fetchContractCountProvider query to get the count of contract list
	 * according to selected filters and sort parameters.</li>
	 * <li>2.If user type is Agency, then we will call the
	 * fetchContractCountAgency query to get the count of contract list
	 * according to selected filters and sort parameters.</li>
	 * <li>3.If user type is Accelerator, then we will call the
	 * fetchContractCountAccelerator query to get the count of contract list
	 * according to selected filters and sort parameters.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession mybatis SQL session
	 * @param aoContractBean an object of ContractList
	 * @param asUserType string containing user type
	 * @return an integer value of contracts count
	 * 
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Integer getContractsCount(SqlSession aoMybatisSession, ContractList aoContractBean, String asUserType)
			throws ApplicationException
	{
		Integer loContractCount = HHSConstants.INT_ZERO;
		try
		{
			if (null != aoContractBean.getProvider())
			{
				StringBuffer loProviderNameSb = new StringBuffer(HHSConstants.PERCENT).append(
						aoContractBean.getProvider()).append(HHSConstants.PERCENT);
				aoContractBean.setProvider(loProviderNameSb.toString());
			}
			if (asUserType.equals(ApplicationConstants.PROVIDER_ORG))
			{
				loContractCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoContractBean,
						HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_CONTRACT_COUNT_PROVIDER,
						HHSConstants.COM_NYC_HHS_MODEL_CONTRACT_LIST);

			}
			else if (asUserType.equals(ApplicationConstants.AGENCY_ORG))
			{
				loContractCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoContractBean,
						HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_CONTRACT_COUNT_AGENCY,
						HHSConstants.COM_NYC_HHS_MODEL_CONTRACT_LIST);
			}
			else
			{
				loContractCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoContractBean,
						HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_CONTRACT_COUNT_ACCELERATOR,
						HHSConstants.COM_NYC_HHS_MODEL_CONTRACT_LIST);
			}
			String lsProviderName = aoContractBean.getProvider();
			if (null != lsProviderName)
			{
				lsProviderName = lsProviderName.substring(HHSConstants.INT_ONE);
				lsProviderName = lsProviderName.substring(HHSConstants.INT_ZERO, lsProviderName.length()
						- HHSConstants.INT_ONE);
				aoContractBean.setProvider(lsProviderName);
			}
			setMoState("Contracts count fetched successfully for org type:" + asUserType);
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting contracts count for org type:" + asUserType);
			throw loExp;
		}
		catch (Exception aoAppExp)
		{
			setMoState("Error while getting contracts count");
			LOG_OBJECT.Error("Error while getting contracts count", aoAppExp);
			throw new ApplicationException("Error while getting contracts count", aoAppExp);
		}
		return loContractCount;
	}

	/**
	 * This method gets sum of contract amounts with contractstatus equal to
	 * “Pending Registration”, “Registered” and “Suspended”.
	 * <ul>
	 * <li>If user type is Provider, then we will call the
	 * fetchContractCountProvider query to get the sum of amounts of contracts
	 * with contractstatus equal to “Pending Registration”, “Registered” and
	 * “Suspended”.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession mybatis SQL session
	 * @param aoContractBean an object of ContractList
	 * @param asUserType string containing user type
	 * @return an integer value of contracts count
	 * 
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public String getContractsValue(SqlSession aoMybatisSession, ContractList aoContractBean, String asUserType)
			throws ApplicationException
	{
		String lsContractValue = HHSConstants.BULK_UPLOAD_FLAG_ZERO;
		try
		{
			if (asUserType.equals(ApplicationConstants.PROVIDER_ORG))
			{
				lsContractValue = (String) DAOUtil.masterDAO(aoMybatisSession, aoContractBean,
						HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_CONTRACT_AMOUNT_PROVIDER,
						HHSConstants.COM_NYC_HHS_MODEL_CONTRACT_LIST);

			}

			setMoState("Contracts amount fetched successfully for org type:" + asUserType);
			if (lsContractValue == null)
			{
				return HHSConstants.BULK_UPLOAD_FLAG_ZERO;
			}
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting contracts amount for org type:" + asUserType);
			throw loExp;
		}
		catch (Exception aoAppExp)
		{
			setMoState("Error while getting contracts amount");
			LOG_OBJECT.Error("Error while getting contracts amount", aoAppExp);
			throw new ApplicationException("Error while getting contracts amount", aoAppExp);
		}
		return lsContractValue;
	}

	/**
	 * The Method will select the particular contractID for Amend contract on
	 * the basis of original contractId and update the contract status
	 * <ul>
	 * <li>select contract id for Amend contract</li>
	 * <li>calls query 'selectContractAmendmentId'</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Mybatis Session
	 * @param aoContractBean ContractList bean
	 * @return ContractList bean obj
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public Map selectContractAmendmentId(SqlSession aoMybatisSession, ContractList aoContractBean)
			throws ApplicationException
	{
		Map loContractMap = null;
		try
		{
			loContractMap = (Map) DAOUtil.masterDAO(aoMybatisSession, aoContractBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.SELECT_CONTRACT_AMENDMENT_ID,
					HHSConstants.COM_NYC_HHS_MODEL_CONTRACT_LIST);

			setMoState("Transaction Success:: FinancialsListService: selectContractAmendmentId method -  "
					+ aoContractBean.getContractId() + " \n");
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.ORIGINAL_CONTRACT_ID, aoContractBean.getContractId());
			LOG_OBJECT.Error("Exception occured while fetching Contract Id from  Contract Table", loAppEx);
			setMoState("Transaction Failed:: FinancialsListService:selectContractAmendmentId method -Exception occured "
					+ ": /n");
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			setMoState("Transaction Failed:: FinancialsListService:selectContractAmendmentId method - failed to update record "
					+ " \n");
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while fetching Contract Id from  Contract Table", loEx);
			loAppEx.addContextData(HHSConstants.ORIGINAL_CONTRACT_ID, aoContractBean.getContractId());
			LOG_OBJECT.Error("Exception occured while fetching Contract Id from  Contract Table", loEx);
			throw loAppEx;
		}
		return loContractMap;
	}

	/**
	 * The Method will select the particular contractID for Amend contract on
	 * the basis of original contractId and update the contract status
	 * <ul>
	 * <li>select contract id for Amend contract</li>
	 * <li>update the status id for Amend contract</li>
	 * <li>calls query 'updateContractAmendStatus'</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Mybatis Session
	 * @param aoContractBean ContractList bean
	 * @return ContractList bean obj
	 * @throws ApplicationException
	 */
	public ContractList updateContractAmendmentStatus(SqlSession aoMybatisSession, ContractList aoContractBean)
			throws ApplicationException
	{

		try
		{
			int liNoOfRowUpdated = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoContractBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.UPDATE_CONTRACT_AMEND_STATUS,
					HHSConstants.COM_NYC_HHS_MODEL_CONTRACT_LIST);
			if (liNoOfRowUpdated <= HHSConstants.INT_ZERO)
			{
				throw new ApplicationException("Exception occured IN updateContractAmendmentStatus  ");
			}
			setMoState("Transaction Success:: FinancialsListService: updateContractAmendmentStatus method -  "
					+ aoContractBean.getContractId() + " \n");
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.AMEND_CONTRACT_ID, aoContractBean.getContractId());
			setMoState("Transaction Failed:: FinancialsListService:updateContractAmendmentStatus method -Exception occured "
					+ ": /n");
			LOG_OBJECT.Error("Exception occured while updating cancel Amendment status to cancel in Contract Table  ",
					loAppEx);
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Exception occured while updating cancel Amendment status to cancel in Contract Table  ",
					loEx);
			setMoState("Transaction Failed:: FinancialsListService:updateContractAmendmentStatus method - failed to update record "
					+ " \n");
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while updating cancel Amendment status to cancel in Contract Table", loEx);
			throw loAppEx;
		}
		return aoContractBean;
	}

	/**
	 * The Method will update the budget status to cancel where budget type =
	 * amendment
	 * <ul>
	 * <li></li>
	 * <li>calls query 'selectAmenBudgetStatus'</li>
	 * <li>calls query 'updateAmenBudgetStatus'</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Mybatis Session
	 * @param aoContractBean ContractList bean
	 * @return lbStatus
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public boolean updateAmenBudgetStatus(SqlSession aoMybatisSession, ContractList aoContractBean)
			throws ApplicationException
	{
		boolean lbStatus = false;
		List<HhsAuditBean> loAuditList = null;
		HhsAuditService loHhsAuditService = new HhsAuditService();
		try
		{
			// audit table entries.
			List<PaymentSortAndFilter> loBudgetAuditInfo = (List<PaymentSortAndFilter>) DAOUtil.masterDAO(
					aoMybatisSession, aoContractBean, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSConstants.SELECT_AMEN_BUDGET_STATUS, HHSConstants.COM_NYC_HHS_MODEL_CONTRACT_LIST);
			loAuditList = addAuditInfo(aoContractBean.getModifyBy(), aoContractBean.getOrgType(), null, null,
					loBudgetAuditInfo);
			loHhsAuditService.hhsMultiAuditInsert(aoMybatisSession, loAuditList, true);

			DAOUtil.masterDAO(aoMybatisSession, aoContractBean, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSConstants.UPDATE_AMEN_BUDGET_STATUS, HHSConstants.COM_NYC_HHS_MODEL_CONTRACT_LIST);

			lbStatus = true;
			setMoState("Transaction Success:: FinancialsListService: updateAmenBudgetStatus method -  "
					+ aoContractBean.getContractId() + " \n");
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT
					.Error("Exception occured while updating the  status in the budget table in updateAmenBudgetStatus method  ",
							loAppEx);
			setMoState("Transaction Failed:: FinancialsListService:updateAmenBudgetStatus method -Exception occured "
					+ ": /n");
			loAppEx.addContextData(HHSConstants.AMEND_CONTRACT_ID, aoContractBean.getContractId());

			throw loAppEx;
		}
		catch (Exception loEx)
		{
			LOG_OBJECT
					.Error("Exception occured while updating the  status in the budget table in updateAmenBudgetStatus method  ",
							loEx);
			setMoState("Transaction Failed:: FinancialsListService:updateAmenBudgetStatus method - failed to update record "
					+ " \n");
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while updating the  status in the budget table in updateAmenBudgetStatus method",
					loEx);
			loAppEx.addContextData(HHSConstants.AMEND_CONTRACT_ID, aoContractBean.getContractId());
			throw loAppEx;
		}
		return lbStatus;
	}

	/**
	 * This method is used to fetch the details of a contract based upon the
	 * E-PIN entered by the user on the renew Contract screen. Made changes for
	 * agency name changes enhancement id 6394 release 3.4.0. Updated for
	 * Release 6 Apt Interface
	 * <ul>
	 * <li>With the E-PIN, the respective details are queried from the Database</li>
	 * <li>The list of details as <code>loContractList</code> are returned from
	 * the query</li>
	 * <li>These are returned back to the controller class
	 * <code>ContractListController</code></li>
	 * <li>Transaction used : <code>fetchContractDetailsByEPIN</code></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Mybatis Session
	 * @param loContractDetails HashMap
	 * @return loContractDetail EPinDetailBean
	 * @throws ApplicationException
	 */

	public EPinDetailBean findContractDetailsByEPIN(EPinDetailBean aoContractDetails, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		//*** Start QC 9585 R 8.9 do not expose password for service account in logs
		String param = CommonUtil.maskPassword(aoContractDetails);
		LOG_OBJECT.Debug("Entered into validateEpinIsUnique() with paramters:: " + param);
		//LOG_OBJECT.Info("Entered into validateEpinIsUnique() with paramters::" + aoContractDetails);
		//*** End QC 9585 R 8.9 do not expose password for service account in logs

		
		EPinDetailBean loContractDetail = null;
		EPinDetailBean loContractDetailByContract = null;
		try
		{
			// Changes for Release 6 Apt interface start

			if (null != aoContractDetails && null != aoContractDetails.getEpinId())
			{
				// Made Changes after FindBug Report
				String lsEpin = aoContractDetails.getEpinId();
				if (null != lsEpin)
				{
					aoContractDetails.setEpinId(lsEpin.split(HHSConstants.HYPHEN)[0].trim());
					aoContractDetails.setAgencyDiv(lsEpin.split(HHSConstants.HYPHEN)[1].trim());
				}
				loContractDetail = (EPinDetailBean) DAOUtil.masterDAO(aoMybatisSession, aoContractDetails,
						HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FIND_CONTRACT_DETAILS_BY_EPIN,
						HHSConstants.COM_NYC_HHS_MODEL_E_PIN_DETAIL_BEAN);
				
			}
			// Changes for Release 6 Apt interface end
			if (null != loContractDetail)
			{
				// Made changes for agency name changes enhancement id 6394
				// release 3.4.0
				if (aoContractDetails != null
						&& aoContractDetails.getContractTypeId().equalsIgnoreCase(HHSConstants.TWO))
				{
					loContractDetail.setAmendmentStart(loContractDetail.getContractStart());
					loContractDetail.setAmendmentEnd(loContractDetail.getContractEnd());
					loContractDetailByContract = (EPinDetailBean) DAOUtil.masterDAO(aoMybatisSession,
							aoContractDetails.getContractId(), HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
							HHSConstants.FIND_CONTRACT_DETAILS_BY_CONTRACT_FOR_AMEND, HHSConstants.JAVA_LANG_STRING);
					loContractDetail.setContractStart(HHSUtil.formatDateToMMDDYYYY(loContractDetailByContract
							.getContractStart()));
					loContractDetail.setContractEnd(HHSUtil.formatDateToMMDDYYYY(loContractDetailByContract
							.getContractEnd()));

					/*[Start] R9.5.0 QC9657 */
/*					Date conDate = new SimpleDateFormat( HHSConstants.MMDDYYFORMAT ).parse(loContractDetail.getContractStart() );
					Date amdDate = new SimpleDateFormat( HHSConstants.MMDDYYFORMAT ).parse(loContractDetail.getAmendmentStart() );
					if ( amdDate.before(conDate) ){
						loContractDetail.setAmendmentStart(loContractDetail.getContractStart());
					}*/
					/*[End] R9.5.0 QC9657   */
					loContractDetail.setProgramNameId(loContractDetailByContract.getProgramNameId());
					loContractDetail.setProgramName(loContractDetailByContract.getProgramName());
					loContractDetail.setContractValue(loContractDetailByContract.getContractValue());

					BigDecimal loNewTotalContractAmount = new BigDecimal(loContractDetail.getContractValue())
							.add(new BigDecimal(loContractDetail.getAmendValue()));
					loContractDetail.setNewTotalContractAmount(loNewTotalContractAmount.toString());
				}
				else
				{
					if (null != aoContractDetails && null != aoContractDetails.getContractId())
					{
						loContractDetailByContract = (EPinDetailBean) DAOUtil.masterDAO(aoMybatisSession,
								aoContractDetails.getContractId(), HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
								HHSConstants.FIND_CONTRACT_DETAILS_BY_CONTRACT, HHSConstants.JAVA_LANG_STRING);
						loContractDetail.setAgencyId(loContractDetailByContract.getAgencyId());
						loContractDetail.setContractTitle(loContractDetailByContract.getContractTitle());
						loContractDetail.setProgramNameId(loContractDetailByContract.getProgramNameId());
						loContractDetail.setProgramName(loContractDetailByContract.getProgramName());
						loContractDetail.setContractValue(loContractDetailByContract.getContractValue());
					}
				}
			}
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.AO_EPIN_DETAIL_BEAN, CommonUtil.convertBeanToString(aoContractDetails));
			LOG_OBJECT.Error("Error while Fetching contract List by Epin", loAppEx);
			setMoState("Error while Fetching contract List by Epin : " + aoContractDetails);
			throw loAppEx;
		}

		catch (Exception loAppEx)
		{
			LOG_OBJECT
					.Error("Exception occured for fetching Contract details from findContractDetailsByEPIN in FinancialsListService ",
							loAppEx);
			setMoState("Transaction Failed:: FinancialsListService: findContractDetailsByEPIN method - failed to validate"
					+ aoContractDetails + " \n");
			throw new ApplicationException("Error occured while fetching the details for cotract by Epin", loAppEx);
		}

		return loContractDetail;

	}

	/**
	 * Added for enhancement 6482 for Release 3.8.0 This method is used to fetch
	 * the epin of a contract for a contractid Updated in R6 - getting
	 * ref_apt_epin_id as well from the same query as epin is no longer unique
	 * <ul>
	 * <li>Query used : <code>findContractEpin</code></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Mybatis Session
	 * @param loContractDetails EPinDetailBean
	 * @return loContractDetail EPinDetailBean
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked" })
	public EPinDetailBean fetchContractEpin(EPinDetailBean aoContractDetails, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		Map<String, String> loResultMap = null;
		try
		{
			if (null != aoContractDetails && null != aoContractDetails.getContractId()
					&& !aoContractDetails.getContractId().equalsIgnoreCase(HHSConstants.EMPTY_STRING))
			{
				loResultMap = (Map<String, String>) DAOUtil.masterDAO(aoMybatisSession,
						aoContractDetails.getContractId(), HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
						HHSConstants.FIND_CONTRACT_EPIN, HHSConstants.JAVA_LANG_STRING);
				if (null != loResultMap && !loResultMap.isEmpty())
				{
					aoContractDetails.setEpinId(loResultMap.get(HHSConstants.EXT_EPIN));
					aoContractDetails.setAgencyDiv(loResultMap.get(HHSConstants.AGENCY_ID_COL));
					aoContractDetails
							.setRefAptEpinId(String.valueOf(loResultMap.get(HHSConstants.REF_APT_EPIN_ID_COL)));
				}
			}
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.AO_EPIN_DETAIL_BEAN, CommonUtil.convertBeanToString(aoContractDetails));
			LOG_OBJECT.Error("fetchContractEpin::: Error while Fetching contract Epin", loAppEx);
			setMoState("fetchContractEpin:::Error while Fetching contract Epin : " + aoContractDetails);
			throw loAppEx;
		}

		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error(
					"Exception occured for fetching Contract epin from fetchContractEpin in FinancialsListService ",
					loAppEx);
			setMoState("Transaction Failed:: FinancialsListService: fetchContractEpin method - failed to validate"
					+ aoContractDetails + " \n");
			throw new ApplicationException("Error occured while fetching the details for cotract Epin", loAppEx);
		}
		return aoContractDetails;

	}

	/**
	 * The method is updated in Release 6: APT Interface Added for enhancement
	 * 6482 for Release 3.8.0 This method is used to fetch the details of a
	 * contract based upon the E-PIN entered by the user on the renew Contract
	 * screen.
	 * <ul>
	 * <li>With the E-PIN, the respective details are queried from the Database</li>
	 * <li>The list of details as <code>loContractList</code> are returned from
	 * the query</li>
	 * <li>These are returned back to the controller class
	 * <code>ContractListController</code></li>
	 * <li>Transaction used : <code>fetchContractDetailsByEPIN</code></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Mybatis Session
	 * @param loContractDetails HashMap
	 * @return loContractDetail EPinDetailBean
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public EPinDetailBean fetchContractDetailsForUpdate(EPinDetailBean aoContractDetails, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered into fetchContractDetailsForUpdate() with paramters::" + aoContractDetails);
		EPinDetailBean loContractDetail = null;
		EPinDetailBean loContractDetailByContract = null;
		try
		{
			if (null != aoContractDetails)
			{
				// Updated in Release 6:APT Interface Epin changes
				loContractDetail = (EPinDetailBean) DAOUtil.masterDAO(aoMybatisSession, aoContractDetails,
						HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FIND_CONTRACT_DETAILS_BY_EPIN,
						HHSConstants.COM_NYC_HHS_MODEL_E_PIN_DETAIL_BEAN);
				// Updated in Release 6:changes end
				if (null != loContractDetail)
				{
					if (null != aoContractDetails.getContractId()
							&& !aoContractDetails.getContractId().equalsIgnoreCase(HHSConstants.EMPTY_STRING))
					{
						loContractDetailByContract = (EPinDetailBean) DAOUtil.masterDAO(aoMybatisSession,
								aoContractDetails.getContractId(), HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
								HHSConstants.FIND_CONTRACT_DETAILS_BY_CONTRACT, HHSConstants.JAVA_LANG_STRING);
						loContractDetail.setAgencyId(loContractDetailByContract.getAgencyId());
						loContractDetail.setContractTitle(StringEscapeUtils
								.escapeHtml((String) loContractDetailByContract.getContractTitle()));
						loContractDetail.setProgramNameId(loContractDetailByContract.getProgramNameId());
						loContractDetail.setProgramName(loContractDetailByContract.getProgramName());
						loContractDetail.setContractValue(loContractDetailByContract.getContractValue());
						loContractDetail.setContractStart(HHSUtil.formatDateToMMDDYYYY(loContractDetailByContract
								.getContractStart()));
						loContractDetail.setContractEnd(HHSUtil.formatDateToMMDDYYYY(loContractDetailByContract
								.getContractEnd()));

					}
					if (null != loContractDetail.getAgencyId()
							&& !loContractDetail.getAgencyId().equalsIgnoreCase(HHSConstants.EMPTY_STRING))
					{
						String lsAgencyName = (String) DAOUtil.masterDAO(aoMybatisSession,
								loContractDetail.getAgencyId(), HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
								HHSConstants.FIND_AGENCY_NAME, HHSConstants.JAVA_LANG_STRING);
						loContractDetail.setAgencyName(lsAgencyName);
						HashMap<String, String> loProgramMap = new HashMap<String, String>();
						loProgramMap.put(HHSConstants.ACTIVE_FLAG, HHSConstants.ONE);
						loProgramMap.put(HHSConstants.ORG_ID, loContractDetail.getAgencyId());
						List<ProgramNameInfo> loProgramNameList = (List<ProgramNameInfo>) DAOUtil.masterDAO(
								aoMybatisSession, loProgramMap, HHSConstants.MAPPER_CLASS_FINANCIAL_PAYMENT_MAPPER,
								HHSConstants.PL_GET_PROGRAM_NAME_AGENCY, HHSConstants.JAVA_UTIL_HASH_MAP);
						loContractDetail.setProgramNameList(loProgramNameList);
					}
				}
			}
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.AO_EPIN_DETAIL_BEAN, CommonUtil.convertBeanToString(aoContractDetails));
			LOG_OBJECT.Error("Error while Fetching contract List by Epin", loAppEx);
			setMoState("Error while Fetching contract List by Epin : " + aoContractDetails);
			throw loAppEx;
		}

		catch (Exception loAppEx)
		{
			LOG_OBJECT
					.Error("Exception occured for fetching Contract details from findContractDetailsByEPIN in FinancialsListService ",
							loAppEx);
			setMoState("Transaction Failed:: FinancialsListService: findContractDetailsByEPIN method - failed to validate"
					+ aoContractDetails + " \n");
			throw new ApplicationException("Error occured while fetching the details for cotract by Epin", loAppEx);
		}

		return loContractDetail;

	}

	/**
	 * This method is used to fetch the details of a contract based upon the
	 * E-PIN entered by the user on the Add Contract screen. Made changes for
	 * agency name changes enhancement id 6394 release 3.4.0. R6: Getting
	 * combination of epin and epin agency as now this combination is unique and
	 * epin is not unique
	 * <ul>
	 * <li>With the E-PIN, the respective details are queried from the Database</li>
	 * <li>The details in <code>loContractDetail</code> are returned back to
	 * controller class <code>ContractListController</code></li>
	 * <li>Transaction used : <code>fetchContractDetailsByEPINforNew</code></li>
	 * </ul>
	 * 
	 * @param aoEPinDetailBean EPinDetailBean
	 * @param aoMybatisSession SqlSession Session
	 * @return loContractDetail EPinDetailBean
	 * @throws ApplicationException - ApplicationException object
	 */

	public EPinDetailBean findContractDetailsByEPINforNew(EPinDetailBean aoEPinDetailBean, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered into findContractDetailsByEPINforNew() with paramters::" + aoEPinDetailBean);
		EPinDetailBean loContractDetail = null;
		try
		{ // Start: Release 6 APT interface changes for new Epin types
			String lsEpin = aoEPinDetailBean.getEpinId();
			if (null != lsEpin)
			{
				aoEPinDetailBean.setEpinId(lsEpin.split(HHSConstants.HYPHEN)[0].trim());
				aoEPinDetailBean.setAgencyDiv(lsEpin.split(HHSConstants.HYPHEN)[1].trim());
			}
			loContractDetail = (EPinDetailBean) DAOUtil.masterDAO(aoMybatisSession, aoEPinDetailBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FIND_CONTRACT_DETAILS_BY_EPI_NFOR_NEW,
					HHSConstants.COM_NYC_HHS_MODEL_E_PIN_DETAIL_BEAN);
			// End: Release 6 APT interface changes
			// Made changes for agency name changes enhancement id 6394 release
			// 3.4.0
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.AO_EPIN_DETAIL_BEAN, CommonUtil.convertBeanToString(aoEPinDetailBean));
			LOG_OBJECT.Error("Error while Fetching contract List by Epin", loAppEx);
			setMoState("Error while Fetching contract List by Epin : " + aoEPinDetailBean);
			throw loAppEx;
		}

		catch (Exception loAppEx)
		{
			LOG_OBJECT
					.Error("Exception occured for fetching Contract details from findContractDetailsByEPIN in FinancialsListService ",
							loAppEx);
			setMoState("Transaction Failed:: FinancialsListService: findContractDetailsByEPIN method - failed to validate"
					+ aoEPinDetailBean + " \n");
			throw new ApplicationException("Error occured while fetching the details for cotract by Epin", loAppEx);
		}

		return loContractDetail;

	}

	/**
	 * This method Adds a New Contract
	 * 
	 * <ul>
	 * <li>All the fields entered by the User is taken from the jsp
	 * <code>ContractListController</code></li>
	 * <li>The values taken as a EPinDetailBean (
	 * <code>aoContractDetailByEpin</code>) from the request are put in the
	 * Database using the <code>FinancialsListService</code></li>
	 * <li>A new contract is inserted in the database</li>
	 * <li>Transaction used : <code>addContractDetails</code></li>
	 * <li>calls query 'addNewContract'</li>
	 * </ul>
	 * 
	 * @param aoCurrentSeq Integer
	 * @param aoContractDetailByEpin EPinDetailBean
	 * @param aoMybatisSession SqlSession Session
	 * @return aoHMWFRequiredProps HashMap
	 * @throws ApplicationException - ApplicationException object
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public HashMap addNewContractDetails(Integer aoCurrentSeq, EPinDetailBean aoContractDetailByEpin,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		Integer loAddContractCount = HHSConstants.INT_ZERO;
		HashMap loHMWFRequiredProps = new HashMap();
		try
		{
			aoContractDetailByEpin.setContractId(String.valueOf(aoCurrentSeq).trim());
			loAddContractCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoContractDetailByEpin,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.ADD_NEW_CONTRACT,
					HHSConstants.COM_NYC_HHS_MODEL_E_PIN_DETAIL_BEAN);

			if (loAddContractCount <= HHSConstants.INT_ZERO)
			{
				setMoState("Transaction Failed:: FinancialsListService: addNewContractDetails method - inserted the add contract Details for "
						+ aoContractDetailByEpin.getEpinId() + " \n");
			}
			else
			{
				setMoState("Transaction Success:: FinancialsListService: addNewContractDetails method - inserted the add contract Details for "
						+ aoContractDetailByEpin.getEpinId() + " \n");
				loHMWFRequiredProps.put(HHSConstants.CONTRACT_ID_WORKFLOW, aoContractDetailByEpin.getContractId());
				loHMWFRequiredProps.put(HHSConstants.SUBMITTED_BY, aoContractDetailByEpin.getCreateByUserId());
				loHMWFRequiredProps.put(HHSConstants.PROPERTY_PE_LAUNCH_COF, aoContractDetailByEpin.getLaunchCOF());
				loHMWFRequiredProps.put(HHSConstants.WORKFLOW_NAME, HHSConstants.WF_CONTRACT_CONFIGURATION);
			}

		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.AO_EPIN_DETAIL_BEAN,
					CommonUtil.convertBeanToString(aoContractDetailByEpin));
			LOG_OBJECT.Error("Exception occured for inserting Contract Details in FinancialsListService ", loAppEx);
			setMoState("Transaction Failed:: FinancialsListService: addNewContractDetails method - failed to validate"
					+ aoContractDetailByEpin.getEpinId() + " \n");
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT
					.Error("Exception occured for inserting Contract details from addNewContractDetails in FinancialsListService ",
							loAppEx);
			setMoState("Transaction Failed:: FinancialsListService: addNewContractDetails method - failed to validate"
					+ aoContractDetailByEpin.getEpinId() + " \n");
			throw new ApplicationException("Error occured while inserting the details for add contract", loAppEx);
		}
		return loHMWFRequiredProps;
	}

	/**
	 * The Method will validate the contract before closing.
	 * <ul>
	 * <li>1: If Authentication and Error validation false,We will not proceed
	 * further.</li>
	 * <li>2: If Authentication Flag is true,check if any task is open for the
	 * contract</li>
	 * <li>3: If any task is open, return error message
	 * "! Cannot close contract until all open tasks related to this contract are completed."
	 * </li>
	 * <li>4: If no task is open,check for the <Budget Modification Status>,
	 * <Budget Amendment Status>, <Budget Update Status>. If for any FY status
	 * is not approved, return error message
	 * "! Cannot close contract until all budget modifications, amendments and updates have been approved"
	 * , otherwise go to step 5</li>
	 * <li>5: if <Invoice Status> for any invoice associated with that contract
	 * does not equal “Approved” AND if <Payment Status> for any Payment
	 * associated with that contract does not equal “Disbursed” then display
	 * error message: “! Cannot close contract until all invoices have been
	 * approved and all payments have been disbursed for this contract."</li>
	 * 
	 * <li>calls query 'getAllContractIds'</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession Session
	 * @param asContractId Contract Id
	 * @param lbValidateStatusFlag Authentication Flag
	 * @return lbValidateStatusFlag Validation status Flag
	 * @throws ApplicationException
	 * 
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public HashMap validateCloseContract(SqlSession aoMybatisSession, P8UserSession aoUserSession, String asContractId,
			Boolean abAuthStatusFlag) throws ApplicationException
	{
		HashMap loHMError = new HashMap();
		HashMap loHMReqdProp = new HashMap();
		int liErrorCode = HHSConstants.INT_ZERO;
		int liOpenTaskRelatedContract = HHSConstants.INT_ZERO;
		int liBudgetNotApprovedCount = HHSConstants.INT_ZERO;
		int liNotApprovedInvoiceCount = HHSConstants.INT_ZERO;
		int liNotDisbursedPaymentCount = HHSConstants.INT_ZERO;
		// Start: Changes for Defect 8572
		ArrayList<String> loTaskList = new ArrayList();
		getTaskTypesForContract(loTaskList);
		// End: Changes for Defect 8572
		Channel loChannel = new Channel();
		String lsRuleReturnValue;
		String lsErrorMsg = HHSConstants.EMPTY_STRING;
		if (abAuthStatusFlag)
		{
			loHMReqdProp.put(HHSConstants.PROPERTY_PE_CONTRACT_ID, asContractId);
			List<String> loList = null;
			loList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, loHMReqdProp,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.CONTRACT_IDS_UPDATE_MODIFICATION_AMENDMENT,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			for (String lsContractId : loList)
			{
				loHMReqdProp.put(HHSConstants.PROPERTY_PE_CONTRACT_ID, lsContractId);
				// Start: Changes for Defect 8572
				loHMReqdProp.put(HHSConstants.PROPERTY_PE_TASK_TYPE, loTaskList);
				// End: Changes for Defect 8572
				liOpenTaskRelatedContract = liOpenTaskRelatedContract
						+ new P8ProcessServiceForSolicitationFinancials().getOpenTaskCount(aoUserSession, loHMReqdProp);
			}
			loHMReqdProp.put(HHSConstants.CONTRACT_ID_WORKFLOW, asContractId);
			loHMReqdProp.put(HHSConstants.BUDGET_AMENDMENT, HHSConstants.ONE);
			loHMReqdProp.put(HHSConstants.BUDGET_MODIFICATION, HHSConstants.THREE);
			loHMReqdProp.put(HHSConstants.BUDGET_UPDATE, HHSConstants.FOUR);
			loHMReqdProp.put(HHSConstants.BUDGET_APPROVED, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_BUDGET_APPROVED));

			liBudgetNotApprovedCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loHMReqdProp,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.GET_BUDGET_NOT_APPROVED_COUNT,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			loHMReqdProp.put(HHSConstants.INVOICE_APPROVED, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_INVOICE_APPROVED));
			liNotApprovedInvoiceCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loHMReqdProp,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.GET_NOT_APPROVED_INVOICE_COUNT,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			loHMReqdProp.put(HHSConstants.PAYMENT_DISBURSED, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PAYMENT_DISBURSED));
			liNotDisbursedPaymentCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loHMReqdProp,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.GET_NOT_DISBURSED_PAYMENT_COUNT,
					HHSConstants.JAVA_UTIL_HASH_MAP);

			loChannel.setData(HHSConstants.OPEN_TASK_RELATED_CONTRACT, liOpenTaskRelatedContract);
			loChannel.setData(HHSConstants.BUDGET_NOT_APPROVED_COUNT, liBudgetNotApprovedCount);
			loChannel.setData(HHSConstants.NOT_APPROVED_INVOICE_COUNT, liNotApprovedInvoiceCount);
			loChannel.setData(HHSConstants.NOT_DISBURSED_PAYMENT_COUNT, liNotDisbursedPaymentCount);
			lsRuleReturnValue = (String) Rule.evaluateRule(HHSConstants.CLOSE_CONTRACT_ERROR_RULE01, loChannel);
			if (!(lsRuleReturnValue.equalsIgnoreCase(HHSConstants.FALSE) || lsRuleReturnValue.isEmpty()))
			{
				liErrorCode = HHSConstants.INT_TWO;
				lsErrorMsg = lsRuleReturnValue;
			}
		}
		else
		{
			liErrorCode = HHSConstants.INT_ONE;
			lsErrorMsg = PropertyLoader
					.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.MESSAGE_M38);
		}
		loHMError.put(HHSConstants.ERROR_CODE, liErrorCode);
		loHMError.put(HHSConstants.CLC_ERROR_MSG, lsErrorMsg);
		return loHMError;
	}

	/**
	 * The Method will close the contract.
	 * <ul>
	 * <li>1: If Authentication and Error validation false,We will not proceed
	 * further.</li>
	 * <li>2: Else update contract status as 'Closed'</li>
	 * <li>3: And update Contract Budget Status as 'Closed'</li>
	 * <li>calls query 'closeContract'</li>
	 * <li>calls query 'closeBudgetContract'</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession Session
	 * @param asContractId Contract Id
	 * @param lbValidateStatusFlag Validation Flag
	 * @return lbStatus Status Flag
	 * @throws ApplicationException
	 * 
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public boolean closeContract(SqlSession aoMybatisSession, String asContractId, HashMap aoHMErrorRule)
			throws ApplicationException
	{
		boolean lbStatus = false;
		int liErrorCode = (Integer) aoHMErrorRule.get(HHSConstants.ERROR_CODE);
		HashMap loHMReqdProp = new HashMap();
		if (liErrorCode == HHSConstants.INT_ZERO)
		{
			try
			{
				loHMReqdProp.put(HHSConstants.CLC_CONTRACT_ID, asContractId);
				loHMReqdProp.put(HHSConstants.CONTRACT_STATUS_ID, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_CONTRACT_CLOSED));
				loHMReqdProp.put(HHSConstants.CONTRACT_BUDGET_STATUS_ID, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_BUDGET_CLOSED));
				DAOUtil.masterDAO(aoMybatisSession, loHMReqdProp, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
						HHSConstants.CLC_CLOSE_CONTRACT, HHSConstants.JAVA_UTIL_HASH_MAP);
				DAOUtil.masterDAO(aoMybatisSession, loHMReqdProp, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
						HHSConstants.CLOSE_BUDGET_CONTRACT, HHSConstants.JAVA_UTIL_HASH_MAP);
				lbStatus = true;
			}
			catch (ApplicationException loExp)
			{
				setMoState("Error while closing contract for contract id :" + asContractId);
				throw loExp;
			}
		}

		return lbStatus;
	}

	/**
	 * The Method will retrieve Procurement Status on the basis of ProcurementId
	 * <ul>
	 * <li>Condition 1: If Authentication Flag is false,It will not proceed
	 * further</li>
	 * <li>Condition 2: If Authentication Flag is true. <br>
	 * 1.Update status to Cancelled in Contract Table. <br>
	 * 2.Update status to Cancelled in Contract_configuation Table for that
	 * contract. <br>
	 * 3.Update status to Cancelled in budget Table for that contract. <br>
	 * 4.Keep hold All task Contract Configuration,Contract certification of
	 * Task and contract budget review task(if any )for that contract <br>
	 * 5.Save cancel comment in Comment Table.</li>
	 * <li>calls query 'selectContractBudgetStatus'</li>
	 * <li>Made changes for release 3.10.0 enhancement 5686</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession Session
	 * @param asContractId Contract Id
	 * @param lbErrorCheckRule Business Rule Validation Flag
	 * @param asUserId User Id
	 * @param asOrgType Organization Type Id
	 * @return boolean value
	 * @throws ApplicationException if ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public boolean cancelContract(SqlSession aoMybatisSession, String asContractId, Boolean lbErrorCheckRule,
			String asUserId, String asOrgType, String asReuseEpin) throws ApplicationException
	{
		boolean lbStatus = false;
		HashMap<String, String> loHMReqProp = new HashMap<String, String>();
		List<HhsAuditBean> loAuditList = null;
		if (lbErrorCheckRule)
		{
			try
			{
				loHMReqProp.put(HHSConstants.CONTRACT_ID_WORKFLOW, asContractId);
				loHMReqProp.put(HHSConstants.USER_ID, asUserId);
				// made changes for release 3.10.0 enhancement 5686
				if (asReuseEpin == null)
				{
					asReuseEpin = HHSConstants.NOT_REUSE;
				}
				loHMReqProp.put(HHSConstants.REUSE_EPIN, asReuseEpin);

				updateStatusInDB(aoMybatisSession, HHSConstants.CS_UPDATE_CONTRACT_STATUS, loHMReqProp);

				Integer loBudgetCount = fetchBudgetCountForContract(aoMybatisSession, loHMReqProp);

				if (loBudgetCount == HHSConstants.INT_ONE)
				{
					HhsAuditService loHhsAuditService = new HhsAuditService();
					// audit table entries.
					List<PaymentSortAndFilter> loBudgetAuditInfo = (List<PaymentSortAndFilter>) DAOUtil.masterDAO(
							aoMybatisSession, loHMReqProp, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
							HHSConstants.SELECT_CONTRACT_BUDGET_STATUS, HHSConstants.JAVA_UTIL_HASH_MAP);
					loAuditList = addAuditInfo(asUserId, asOrgType, null, null, loBudgetAuditInfo);
					loHhsAuditService.hhsMultiAuditInsert(aoMybatisSession, loAuditList, true);
					updateStatusInDB(aoMybatisSession, HHSConstants.UPDATE_CONTRACT_BUDGET_STATUS, loHMReqProp);
				}
				lbStatus = true;

			}
			catch (ApplicationException loAppEx)
			{
				LOG_OBJECT.Error("Application Exception occured while updating the statuses in the db ", loAppEx);
				setMoState("Transaction Failed:: FinancialsListService:cancelContract method - failed to update record "
						+ ": /n");
				throw loAppEx;
			}
			catch (Exception loEx)
			{
				LOG_OBJECT.Error("Exception occured while updating the statuses in the db   ", loEx);
				setMoState("Transaction Failed:: FinancialsListService:cancelContract method - failed to update record "
						+ " \n");
				throw new ApplicationException("Exception occured while updating the statuses in the db", loEx);
			}
		}

		return lbStatus;
	}

	/**
	 * This method is used to fetch contract for updation of task
	 * @param aoMybatisSession a SqlSession object
	 * @param asContractId
	 * @return fetchContractForUpdateTask
	 * @throws ApplicationException
	 */
	public String fetchContractForUpdateTask(SqlSession aoMybatisSession, String asContractId)
			throws ApplicationException
	{

		try
		{
			return (String) DAOUtil.masterDAO(aoMybatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_CONTRACTID_FOR_UPDATE_TASK,
					HHSConstants.JAVA_LANG_STRING);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Application Exception occured while geting parent contract ID contract", loAppEx);
			setMoState("Transaction Failed:: getParentContractId method - failed to fetch parent record " + ": /n");
			throw loAppEx;
		}
		catch (Exception loEx)
		{

			LOG_OBJECT.Error("Application Exception occured while geting parent contract ID contract", loEx);
			setMoState("Transaction Failed:: getParentContractId method - failed to fetch parent record " + ": /n");
			throw new ApplicationException("Exception occured while deleting contract", loEx);
		}
	}

	/**
	 * @param aoMybatisSession
	 * @param asContractId
	 * @param lbErrorCheckRule
	 * @return
	 * @throws ApplicationException
	 */
	public String getParentContractId(SqlSession aoMybatisSession, String asContractId, Boolean lbErrorCheckRule)
			throws ApplicationException
	{
		String lsContractIdList = HHSConstants.EMPTY_STRING;
		if (lbErrorCheckRule)
		{
			try
			{
				lsContractIdList = (String) DAOUtil.masterDAO(aoMybatisSession, asContractId,
						HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_PARENT_CONTRACT_ID_LIST,
						HHSConstants.JAVA_LANG_STRING);
			}
			catch (ApplicationException loAppEx)
			{
				LOG_OBJECT.Error("Application Exception occured while geting parent contract ID contract", loAppEx);
				setMoState("Transaction Failed:: getParentContractId method - failed to fetch parent record " + ": /n");
				throw loAppEx;
			}
			catch (Exception loEx)
			{

				LOG_OBJECT.Error("Application Exception occured while geting parent contract ID contract", loEx);
				setMoState("Transaction Failed:: getParentContractId method - failed to fetch parent record " + ": /n");
				throw new ApplicationException("Exception occured while deleting contract", loEx);
			}
		}
		return lsContractIdList;
	}

	/**
	 * Added For Enhancement 6000 for Release 3.8.0 The Method will delete
	 * contract and teh associated budgets based on contract id
	 * <ul>
	 * <li>Condition 1: If Authentication Flag is false,It will not proceed
	 * further</li>
	 * <li>Condition 2: If Authentication Flag is true. <br>
	 * will delete contract and all the associated entries</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession Session
	 * @param asContractId Contract Id
	 * @param lbErrorCheckRule Business Rule Validation Flag
	 * @return boolean value
	 * @throws ApplicationException if ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public boolean deleteContract(SqlSession aoMybatisSession, String asContractIdList, Boolean lbErrorCheckRule)
			throws ApplicationException
	{
		boolean lbContractDeleteStatus = Boolean.FALSE;
		LOG_OBJECT.Debug("Entered into deleteContract:: " + asContractIdList);
		if (lbErrorCheckRule)
		{
			try
			{
				if (asContractIdList != null && !asContractIdList.equalsIgnoreCase(HHSConstants.EMPTY_STRING))
				{
					List<String> loContractIdList = Arrays.asList(asContractIdList.split(HHSConstants.COMMA));
					for (String asContractId : loContractIdList)
					{
						deleteSubBudgetLineItems(aoMybatisSession, asContractId);

						DAOUtil.masterDAO(aoMybatisSession, asContractId, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
								HHSConstants.DELETE_SUB_BUDGET_SITE, HHSConstants.JAVA_LANG_STRING);
						DAOUtil.masterDAO(aoMybatisSession, asContractId, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
								HHSConstants.DELETE_SUB_BUDGET, HHSConstants.JAVA_LANG_STRING);

						DAOUtil.masterDAO(aoMybatisSession, asContractId, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
								HHSConstants.DELETE_BUDGET_CUSTOMIZ, HHSConstants.JAVA_LANG_STRING);
						DAOUtil.masterDAO(aoMybatisSession, asContractId, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
								HHSConstants.DELETE_BUDGET_DOC, HHSConstants.JAVA_LANG_STRING);
						DAOUtil.masterDAO(aoMybatisSession, asContractId, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
								HHSConstants.DELETE_ASSOCIATED_BUDGETS, HHSConstants.JAVA_LANG_STRING);
						
						//[Start] QC 9452 R 8.5.0 Delete Contract not working for Flagged Contracts
						// Delete record per contact from Flag_Contract_Details 
						// and Flag_Contract_History tables before deleting contract from Contract table.
						DAOUtil.masterDAO(aoMybatisSession, asContractId, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
								HHSR5Constants.UPDATE_CONTRACT_LEVEL_MESSAGE, HHSConstants.JAVA_LANG_STRING);
						
						DAOUtil.masterDAO(aoMybatisSession, asContractId, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
								HHSR5Constants.DELETE_CONTRACT_LEVEL_MESSAGE_HISTORY, HHSConstants.JAVA_LANG_STRING);
						
						//[End] QC 9452 R 8.5.0 Delete Contract not working for Flagged Contracts
						
						DAOUtil.masterDAO(aoMybatisSession, asContractId, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
								HHSConstants.DELETE_CONTRACT_FIN_FUNDING, HHSConstants.JAVA_LANG_STRING);
						DAOUtil.masterDAO(aoMybatisSession, asContractId, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
								HHSConstants.DELETE_CONTRACT_FINANCIALS, HHSConstants.JAVA_LANG_STRING);
						DAOUtil.masterDAO(aoMybatisSession, asContractId, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
								HHSConstants.DELETE_CONTRACT_DOC, HHSConstants.JAVA_LANG_STRING);
						// R5 change starts
						DAOUtil.masterDAO(aoMybatisSession, asContractId, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
								HHSR5Constants.DELETE_DEFAULT_ASSIGNMENT, HHSConstants.JAVA_LANG_STRING);
						DAOUtil.masterDAO(aoMybatisSession, asContractId, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
								HHSR5Constants.DELETE_CONTRACT_RESTRICTIONS, HHSConstants.JAVA_LANG_STRING);
						// R5 change ends
						
						Integer loContractsDeleted = (Integer) DAOUtil.masterDAO(aoMybatisSession, asContractId,
								HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.DELETE_CONTRACT,
								HHSConstants.JAVA_LANG_STRING);
						//[R8.8.0] QC9400 : 
						LOG_OBJECT.Debug("[Tracce 1]Entered into deleteContract::loContractsDeleted=" + loContractsDeleted);
						if (loContractsDeleted.intValue() > HHSConstants.INT_ZERO)
						{
							lbContractDeleteStatus = Boolean.TRUE;
						}
						LOG_OBJECT.Debug("[Tracce 1]Entered into deleteContract::lbContractDeleteStatus=" + lbContractDeleteStatus);
						
					}
				}
			}
			catch (ApplicationException loAppEx)
			{
				LOG_OBJECT.Error("Application Exception occured while deleting contract", loAppEx);
				setMoState("Transaction Failed:: FinancialsListService:deleteContract method - failed to delete record "
						+ ": /n");
				throw loAppEx;
			}
			catch (Exception loEx)
			{
				LOG_OBJECT.Error("Exception occured while deleting contract", loEx);
				setMoState("Transaction Failed:: FinancialsListService:deleteContract method - failed to delete record "
						+ " \n");
				throw new ApplicationException("Exception occured while deleting contract", loEx);
			}
		}
		LOG_OBJECT.Debug("Entered into deleteContract::lbContractDeleteStatus=" + lbContractDeleteStatus);
		
		return lbContractDeleteStatus;
	}

	/**
	 * Added For Enhancement 6000 for Release 3.8.0 The Method will delete
	 * contract and teh associated budgets based on contract id
	 * <ul>
	 * <li>Condition 1: If Authentication Flag is false,It will not proceed
	 * further</li>
	 * <li>Condition 2: If Authentication Flag is true. <br>
	 * will fetch budgets for the corresponding contract</li>
	 * <li>calls query 'fetchBudgetsForContract'</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession Session
	 * @param asContractId Contract Id
	 * @param lbErrorCheckRule Business Rule Validation Flag
	 * @return List containing all budget ids
	 * @throws ApplicationException if ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<String> fetchBudgetsForContract(SqlSession aoMybatisSession, String asContractId,
			Boolean lbTerminateStatus) throws ApplicationException
	{
		List<String> loBudgetList = null;
		LOG_OBJECT.Debug("Entered into fetchBudgetsForContract:: " + asContractId);
		if (lbTerminateStatus)
		{
			try
			{
				loBudgetList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, asContractId,
						HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_BUDGETS_FOR_CONTRACT,
						HHSConstants.JAVA_LANG_STRING);
			}
			catch (ApplicationException loAppEx)
			{
				LOG_OBJECT.Error("Application Exception occured while fetch Budgets For Contract " + asContractId,
						loAppEx);
				setMoState("Transaction Failed:: FinancialsListService:fetchBudgetsForContract method - failed to fetch Budgets For Contract"
						+ asContractId + "\n");
				throw loAppEx;
			}
			catch (Exception loEx)
			{
				LOG_OBJECT.Error("Exception occured while fetch Budgets For Contract " + asContractId, loEx);
				setMoState("Transaction Failed:: FinancialsListService:fetchBudgetsForContract method - failed to fetch Budgets For Contract"
						+ asContractId + " \n");
				throw new ApplicationException("Exception occured while fetch Budgets For Contract " + asContractId,
						loEx);
			}
		}
		return loBudgetList;
	}

	/**
	 * This method is used to delete line items of sub budget while deleting
	 * contract
	 * @param aoMybatisSession
	 * @param asContractId
	 * @throws ApplicationException
	 */
	private void deleteSubBudgetLineItems(SqlSession aoMybatisSession, String asContractId) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into deleteSubBudgetLineItems:: " + asContractId);
		// R6 changes start
		DAOUtil.masterDAO(aoMybatisSession, asContractId, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
				HHSR5Constants.DELETE_PERSONNEL_SERVICE_DETAIL, HHSConstants.JAVA_LANG_STRING);
		DAOUtil.masterDAO(aoMybatisSession, asContractId, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
				HHSR5Constants.DELETE_FRINGE_BENEFIT_DETAIL, HHSConstants.JAVA_LANG_STRING);
		// R6 changes end
		DAOUtil.masterDAO(aoMybatisSession, asContractId, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
				HHSConstants.DELETE_PERSONNEL_SERVICE, HHSConstants.JAVA_LANG_STRING);
		DAOUtil.masterDAO(aoMybatisSession, asContractId, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
				HHSConstants.DELETE_OP_SUPPORT_OTHERS, HHSConstants.JAVA_LANG_STRING);
		DAOUtil.masterDAO(aoMybatisSession, asContractId, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
				HHSConstants.DELETE_OP_SUPPORT, HHSConstants.JAVA_LANG_STRING);
		DAOUtil.masterDAO(aoMybatisSession, asContractId, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
				HHSConstants.DELETE_UTILITIES, HHSConstants.JAVA_LANG_STRING);
		DAOUtil.masterDAO(aoMybatisSession, asContractId, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
				HHSConstants.DELETE_PROFF_SERVICE_OTHERS, HHSConstants.JAVA_LANG_STRING);
		DAOUtil.masterDAO(aoMybatisSession, asContractId, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
				HHSConstants.DELETE_PROFF_SERVICE, HHSConstants.JAVA_LANG_STRING);
		DAOUtil.masterDAO(aoMybatisSession, asContractId, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
				HHSConstants.DELETE_RENT, HHSConstants.JAVA_LANG_STRING);
		DAOUtil.masterDAO(aoMybatisSession, asContractId, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
				HHSConstants.DELETE_CONTRACTED_SERVICE, HHSConstants.JAVA_LANG_STRING);
		DAOUtil.masterDAO(aoMybatisSession, asContractId, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
				HHSConstants.DELETE_RATE, HHSConstants.JAVA_LANG_STRING);
		DAOUtil.masterDAO(aoMybatisSession, asContractId, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
				HHSConstants.DELETE_MILESTONE, HHSConstants.JAVA_LANG_STRING);
		DAOUtil.masterDAO(aoMybatisSession, asContractId, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
				HHSConstants.DELETE_UNALLOCATED, HHSConstants.JAVA_LANG_STRING);
		DAOUtil.masterDAO(aoMybatisSession, asContractId, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
				HHSConstants.DELETE_INDIRECT_RATE, HHSConstants.JAVA_LANG_STRING);
		DAOUtil.masterDAO(aoMybatisSession, asContractId, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
				HHSConstants.DELETE_PROG_INCOME_OTHERS, HHSConstants.JAVA_LANG_STRING);
		DAOUtil.masterDAO(aoMybatisSession, asContractId, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
				HHSConstants.DELETE_PROGRAM_INCOME, HHSConstants.JAVA_LANG_STRING);
		DAOUtil.masterDAO(aoMybatisSession, asContractId, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
				HHSConstants.DELETE_FRINGE_BENEFIT, HHSConstants.JAVA_LANG_STRING);
		DAOUtil.masterDAO(aoMybatisSession, asContractId, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
				HHSConstants.DELETE_EQUIPMENT, HHSConstants.JAVA_LANG_STRING);
		//Start:Added in R7 for Cost-Center
		DAOUtil.masterDAO(aoMybatisSession, asContractId, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
				HHSR5Constants.DELETE_SERVICES, HHSConstants.JAVA_LANG_STRING);
		DAOUtil.masterDAO(aoMybatisSession, asContractId, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
				HHSR5Constants.DELETE_COSTCENTER, HHSConstants.JAVA_LANG_STRING);
		DAOUtil.masterDAO(aoMybatisSession, asContractId, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
				HHSR5Constants.DELETE_SERVICES_CONFIG, HHSConstants.JAVA_LANG_STRING);
		//End:Added in R7 for Cost-Center
	}

	/**
	 * This method validate Contract's Epin
	 * <ul>
	 * <li>All the fields entered by the User is taken from the Add Contract or
	 * Renew Contract jsp <code>ContractListController</code></li>
	 * <li>The values taken as a Integer (<code>loContractEpin</code>) from the
	 * request get from the Database using the
	 * <code>FinancialsListService</code></li>
	 * <li>Validating for the EPin is associated with contract</li>
	 * <li>Transaction used : <code>validateRenewContractDetails</code></li>
	 * <li>calls query 'validateRenewContractDetails'</li>
	 * </ul>
	 * 
	 * @param asEPin EPin
	 * @param aoMybatisSession SqlSession Session
	 * @return lbSuccessStatus SuccessStatus
	 * @throws ApplicationException
	 */
	public boolean validateRenewContractDetails(String asEPin, SqlSession aoMybatisSession) throws ApplicationException
	{
		boolean lbSuccessStatus = true;
		Integer loContractEpin = HHSConstants.INT_ZERO;
		try
		{
			loContractEpin = (Integer) DAOUtil.masterDAO(aoMybatisSession, asEPin,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.VALIDATE_RENEW_CONTRACT_DETAILS,
					HHSConstants.JAVA_LANG_STRING);
			if (loContractEpin > HHSConstants.INT_ZERO)
			{
				lbSuccessStatus = false;
			}

			setMoState("Transaction Success:: FinancialsListService: validateRenewContractDetails method - validated the renew contract for "
					+ asEPin + " \n");
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.EPIN_KEY, asEPin);
			LOG_OBJECT.Error("Exception occured while validating Renew Contract in FinancialsListService ", loAppEx);
			setMoState("Transaction Failed:: FinancialsListService: validateRenewContractDetails method - failed to validate"
					+ asEPin + " \n");
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured  validating Renew Contract in FinancialsListService ", loAppEx);
			setMoState("Transaction Failed:: FinancialsListService: validateRenewContractDetails method - failed to validate"
					+ asEPin + " \n");
			throw new ApplicationException("Error occured while validating Renew Contract", loAppEx);
		}
		return lbSuccessStatus;
	}

	/**
	 * R6: Method for validating uniqueness of EPIN
	 * @param aoEpinDetailBean : EPinDetailBean object for epin details
	 * @param aoMybatisSession : SqlSession object
	 * @return lbSuccessStatus : Boolean value to show success status of query
	 * @throws ApplicationException
	 */
	public boolean validateEpinIsUnique(EPinDetailBean aoEpinDetailBean, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered into validateEpinIsUnique() with paramters::" + aoEpinDetailBean);
		boolean lbSuccessStatus = true;
		Integer liContractEpinCount = HHSConstants.INT_ZERO;
		try
		{
			liContractEpinCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoEpinDetailBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.VALIDATE_EPIN_IS_UNIQUE,
					HHSConstants.COM_NYC_HHS_MODEL_E_PIN_DETAIL_BEAN);
			if (liContractEpinCount > HHSConstants.INT_ZERO)
			{
				lbSuccessStatus = false;
			}

			setMoState("Transaction Success:: FinancialsListService: validateEpinIsUnique method - validated uniqueness of EPIN for "
					+ aoEpinDetailBean.getEpinId() + " \n");
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.EPIN_KEY, aoEpinDetailBean.getEpinId());
			LOG_OBJECT
					.Error("Exception occured while validating uniqueness of EPIN in FinancialsListService ", loAppEx);
			setMoState("Transaction Failed:: FinancialsListService: validateEpinIsUnique method - failed to validate"
					+ aoEpinDetailBean.getEpinId() + " \n");
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured  validating uniqueness of EPIN in FinancialsListService ", loAppEx);
			setMoState("Transaction Failed:: FinancialsListService: validateEpinIsUnique method - failed to validate"
					+ aoEpinDetailBean.getEpinId() + " \n");
			throw new ApplicationException("Error occured while validating uniqueness of EPIN", loAppEx);
		}
		return lbSuccessStatus;
	}

	/**
	 * This method validate Provider profile
	 * <ul>
	 * <li>All the fields are populated from APT</li>
	 * <li>Before adding new contract provider must exist in Accelerator</li>
	 * <li>Controller used : <code>ContractListController</code></li>
	 * <li>Transaction used : <code>validateProviderInAccelerator</code></li>
	 * <li>calls query 'validateProviderInAccelerator'</li>
	 * </ul>
	 * 
	 * @param asVendorFmsId String
	 * @param aoMybatisSession SqlSession Session
	 * @return lbProviderInAccelerator boolean
	 * @throws ApplicationException
	 */
	public boolean validateProvider(String asVendorFmsId, SqlSession aoMybatisSession) throws ApplicationException
	{
		boolean lbProviderInAccelerator = false;
		Integer loProviderValidate = HHSConstants.INT_ZERO;
		try
		{
			loProviderValidate = (Integer) DAOUtil.masterDAO(aoMybatisSession, asVendorFmsId,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.VALIDATE_PROVIDER_ACCELERATOR,
					HHSConstants.JAVA_LANG_STRING);
			if (loProviderValidate > HHSConstants.INT_ZERO)
			{
				lbProviderInAccelerator = true;
				setMoState("Transaction Success:: FinancialsListService: validateProvider method - "
						+ "validated the provider in accelerator " + asVendorFmsId + " \n");
			}
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.VENDOR_ID, asVendorFmsId);
			LOG_OBJECT.Error("Exception occured while validating provider in FinancialsListService ", loAppEx);
			setMoState("Transaction Failed:: FinancialsListService: validateProvider method - failed to validate"
					+ asVendorFmsId + " \n");
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured  validating provider in FinancialsListService ", loAppEx);
			setMoState("Transaction Failed:: FinancialsListService: validateProvider method - failed to validate"
					+ asVendorFmsId + " \n");
			throw new ApplicationException("Error occured while validating provider", loAppEx);
		}
		return lbProviderInAccelerator;
	}

	/**
	 * This method renews a Contract
	 * 
	 * <ul>
	 * <li>All the fields entered by the User is taken from the jsp
	 * <code>ContractListController</code></li>
	 * <li>The values taken as a EPinDetailBean (
	 * <code>loContractDetailByEpin</code>) from the request are put in the
	 * Database using the <code>FinancialsListService</code></li>
	 * <li>A new contract is inserted in the database</li>
	 * <li>Transaction used : <code>renewContractDetails</code></li>
	 * <li>calls query 'renewContractDetails'</li>
	 * </ul>
	 * 
	 * @param aiCurrentSeq Integer
	 * @param aoContractDetailByEpin EPinDetailBean
	 * @param aoMybatisSession SqlSession Session
	 * @return aoHMWFRequiredProps HashMap
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public HashMap renewContractDetails(Integer aiCurrentSeq, EPinDetailBean aoContractDetailByEpin,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		HashMap aoHMWFRequiredProps = new HashMap();
		Integer loRenewContractCount = HHSConstants.INT_ZERO;
		try
		{
          /*[Start]QC9139 R7.7.0     */
		    if(aoContractDetailByEpin.getContractTypeId() == HHSConstants.CONTRACT_RENEWAL_TYPE_ID){
		        aoContractDetailByEpin.setContractTypeId( HHSConstants.CONTRACT_BASE_TYPE_ID ) ;
		    }
          /*[End]QC9139 R7.7.0       */

			aoContractDetailByEpin.setParentContractId(aoContractDetailByEpin.getContractId());
			aoContractDetailByEpin.setContractId(String.valueOf(aiCurrentSeq).trim());
			loRenewContractCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoContractDetailByEpin,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FLS_RENEW_CONTRACT_DETAILS,
					HHSConstants.COM_NYC_HHS_MODEL_E_PIN_DETAIL_BEAN);

			if (loRenewContractCount <= HHSConstants.INT_ZERO)
			{
				setMoState("Transaction Failed:: FinancialsListService: renewContractDetails method - inserted the renew contract Details for "
						+ aoContractDetailByEpin.getEpinId() + " \n");
				throw new ApplicationException(
						"Exception occured for inserting Renewing Contract Details in FinancialsListService");
			}
			else
			{
				setMoState("Transaction Success:: FinancialsListService: renewContractDetails method - inserted the renew contract Details for "
						+ aoContractDetailByEpin.getEpinId() + " \n");
				aoHMWFRequiredProps.put(HHSConstants.CONTRACT_ID_WORKFLOW, aoContractDetailByEpin.getContractId());
				aoHMWFRequiredProps.put(HHSConstants.SUBMITTED_BY, aoContractDetailByEpin.getCreateByUserId());
				aoHMWFRequiredProps.put(HHSConstants.WORKFLOW_NAME, HHSConstants.WF_CONTRACT_CONFIGURATION);

			}

		}

		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.AO_CONTRACT_DETAILS_BY_EPIN,
					CommonUtil.convertBeanToString(aoContractDetailByEpin));
			LOG_OBJECT.Error("Exception occured for inserting Renewing Contract Details in FinancialsListService ",
					loAppEx);
			setMoState("Transaction Failed:: FinancialsListService: renewContractDetails method - failed to validate"
					+ aoContractDetailByEpin.getEpinId() + " \n");
			throw loAppEx;
		}

		catch (Exception loAppEx)
		{
			LOG_OBJECT
					.Error("Exception occured for inserting Renewing Contract details from renewContractDetails in FinancialsListService ",
							loAppEx);
			setMoState("Transaction Failed:: FinancialsListService: renewContractDetails method - failed to validate"
					+ aoContractDetailByEpin.getEpinId() + " \n");
			throw new ApplicationException("Error occured while inserting the details for renew cotract", loAppEx);
		}

		return aoHMWFRequiredProps;
	}

	/**
	 * The Method will update the Contract to SuspendContract
	 * <ul>
	 * <li>Condition 1: If Authentication Flag is false,It will not proceed
	 * further</li>
	 * <li>Condition 2: If Authentication Flag is true,It will proceed for
	 * Suspend contract and update comments in Database</li>
	 * <li>calls query 'updateContractReason'</li>
	 * <li>calls query 'updateInvoiceSuspend'</li>
	 * <li>calls query 'updateInvoiceSuspend'</li>
	 * <li>calls query 'updateContractSuspend'</li>
	 * <li></li>
	 * </ul>
	 * 
	 * @param asContractId Contract Id as input
	 * @param asContractReason Contract Reason as input
	 * @param aoMybatisSession SqlSession session as input
	 * @param aoStatusCheck true if valid username/password
	 * @param asUserId User Id
	 * @param asUserOrgType User Org type
	 * @return loUnsuspendStatus true if database updated/false if not updated
	 * @throws ApplicationException Exception thrown in case query fails.
	 */
	@SuppressWarnings("unchecked")
	public List<HhsAuditBean> suspendContract(String asContractId, String asContractReason,
			SqlSession aoMybatisSession, Boolean aoStatusCheck, String asUserId, String asUserOrgType)
			throws ApplicationException
	{
		HashMap<String, String> loContractInfo = null;
		List<HhsAuditBean> loAuditList = null;
		try
		{
			if (aoStatusCheck != null && aoStatusCheck)
			{
				loContractInfo = new HashMap<String, String>();
				loContractInfo.put(HHSConstants.CONTRACT_ID_KEY, asContractId);
				loContractInfo.put(HHSConstants.MOD_BY_USER_ID, asUserId);
				loContractInfo.put(HHSConstants.AS_CONTRACT_REASON, asContractReason);
				loContractInfo.put(HHSConstants.LS_SUSPEND_CONTRACT_STATUS_ID, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_CONTRACT_SUSPENDED));
				loContractInfo.put(HHSConstants.LS_SUSPEND_BUDGET_STATUS_ID, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_BUDGET_SUSPENDED));
				loContractInfo.put(HHSConstants.LS_SUSPEND_INVOICE_STATUS_ID, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_INVOICE_SUSPENDED));
				loContractInfo.put(HHSConstants.LS_SUSPEND_PAYMNET_STATUS_ID, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PAYMENT_SUSPENDED));
				loContractInfo.put(HHSConstants.LS_BUDGET_APPROVED_STATUS_ID, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_BUDGET_APPROVED));
				loContractInfo.put(HHSConstants.LS_BUDGET_CANCELLED_STATUS_ID, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_BUDGET_CANCELLED));
				loContractInfo.put(HHSConstants.LS_INVOICE_APPROVED_STATUS_ID, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_INVOICE_APPROVED));
				loContractInfo.put(HHSConstants.LS_INVOICE_CANCELLED_STATUS_ID, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_INVOICE_CANCELLED));
				loContractInfo.put(HHSConstants.LS_PAYMENT_APPROVED_STATUS_ID, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PAYMENT_APPROVED));
				loContractInfo.put(HHSConstants.LS_PAYMENT_CANCELLED_STATUS_ID, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PAYMENT_WITHDRAWN));
				loContractInfo.put(HHSConstants.LS_PAYMENT_DISBURSED_STATUS_ID, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PAYMENT_DISBURSED));
				// Below is new status payment and invoice rejected exclude for
				// suspend-release 3.1.0- 6023
				loContractInfo.put(HHSConstants.LS_PAYMENT_REJECTED_STATUS_ID, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.PAYMENT_REJECTED));
				loContractInfo.put(HHSConstants.LS_INVOICE_REJECTED_STATUS_ID, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.INVOICE_REJECTED));

				// code to suspend contract
				DAOUtil.masterDAO(aoMybatisSession, loContractInfo, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
						HHSConstants.UPDATE_CONTRACT_REASON, HHSConstants.JAVA_UTIL_HASH_MAP);
				DAOUtil.masterDAO(aoMybatisSession, loContractInfo, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
						HHSConstants.UPDATE_PAYMENT_SUSPEND, HHSConstants.JAVA_UTIL_HASH_MAP);

				DAOUtil.masterDAO(aoMybatisSession, loContractInfo, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
						HHSConstants.UPDATE_INVOICE_SUSPEND, HHSConstants.JAVA_UTIL_HASH_MAP);
				DAOUtil.masterDAO(aoMybatisSession, loContractInfo, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
						HHSConstants.UPDATE_BUDGET_SUSPEND, HHSConstants.JAVA_UTIL_HASH_MAP);
				DAOUtil.masterDAO(aoMybatisSession, loContractInfo, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
						HHSConstants.UPDATE_CONTRACT_SUSPEND, HHSConstants.JAVA_UTIL_HASH_MAP);

				// audit table entries
				List<PaymentSortAndFilter> loPaymentAuditInfo = (List<PaymentSortAndFilter>) DAOUtil.masterDAO(
						aoMybatisSession, loContractInfo, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
						HHSConstants.SELECT_PAYMENT_SUSPEND, HHSConstants.JAVA_UTIL_HASH_MAP);

				List<PaymentSortAndFilter> loInvoiceAuditInfo = (List<PaymentSortAndFilter>) DAOUtil.masterDAO(
						aoMybatisSession, loContractInfo, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
						HHSConstants.SELECT_INVOICE_SUSPEND, HHSConstants.JAVA_UTIL_HASH_MAP);

				List<PaymentSortAndFilter> loBudgetAuditInfo = (List<PaymentSortAndFilter>) DAOUtil.masterDAO(
						aoMybatisSession, loContractInfo, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
						HHSConstants.SELECT_BUDGET_SUSPEND, HHSConstants.JAVA_UTIL_HASH_MAP);

				loAuditList = addAuditInfo(asUserId, asUserOrgType, loPaymentAuditInfo, loInvoiceAuditInfo,
						loBudgetAuditInfo);
				setMoState("Contract suspended succesfully");

			}
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.CONTRACT_ID_KEY, asContractId);
			LOG_OBJECT.Error("Error occurred while suspending a contract", loAppEx);
			setMoState("Error occurred while suspending a contract for contract id : " + asContractId);
			throw loAppEx;
		}
		return loAuditList;
	}

	/**
	 * This method checks Error rules for launching Update contract
	 * Configuration workflow
	 * <ul>
	 * <li>if <Configuration Update Task Status> for contract = “In Review” or
	 * “Returned for Revision” OR if <Budget Update Status> for that contract =
	 * “Pending Submission”, “Pending Approval”, or “Returned for Revisions”
	 * then set error message</li>
	 * <li>Else if there is an Amendment which is in progress for the contract
	 * with Amendment Value less than zero dollars and the <Amendment
	 * Configuration Task Status> or <Amendment Certification of Funds Task
	 * Status> not equal to “Complete”> is open OR if the <Budget Amendment
	 * Status> = “Pending Submission”, “Pending Approval” or “Returned for
	 * Revision” then set error message</li>
	 * <li>Else if <Budget Modification Status> for that contract = “Pending
	 * Submission”, “Pending Approval” or “Returned for Revision” then set error
	 * message</li>
	 * <li>Else if <Invoice Status> for any invoice associated with that
	 * contract = “Pending Submission”, “Pending Approval”, or “Returned for
	 * Revision” OR if <Payment Status> for any Payment associated with that
	 * contract does not equal “Disbursed” then then set error message</li>
	 * <li>Else if <New FY Configuration Task Status> for that contract = “In
	 * Review” then set error message</li>
	 * <li>Else set no error message</li>
	 * <li>calls query 'fetchContractBudgetUpdateStatus'</li>
	 * <li>calls query 'fetchContractBudgetStatus'</li>
	 * <li>calls query 'fetchContractBudgetAmendmentStatus'</li>
	 * <li>calls query 'getContractAmendmentAmmount'</li>
	 * <li>calls query 'fetchContractInvoiceStatus'</li>
	 * <li>calls query 'fetchAllContractId'</li>
	 * 
	 * </ul>
	 * 
	 * @param aoMybatisSession SQL Session object
	 * @param aoUserSession P8UserSession object
	 * @param asContractId Contract Id
	 * @param abAuthFlag Authentication Flag
	 * @return HashMap object
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public HashMap updateConfigurationErrorCheckRule(SqlSession aoMybatisSession, P8UserSession aoUserSession,
			String asContractId, Boolean abAuthFlag) throws ApplicationException
	{
		HashMap loHMError = new HashMap();
		HashMap loHMReqdProp = new HashMap();
		Channel loChannel = new Channel();
		int liOpenTaskAmendmentConf = HHSConstants.INT_ZERO;
		int liOpenTaskAmendmentCOF = HHSConstants.INT_ZERO;
		int liBudgetAmendmentStatusCount = HHSConstants.INT_ZERO;
		String lsConfUpdateTaskStatus = HHSConstants.EMPTY_STRING;
		String lsBudgetUpdateStatus = HHSConstants.EMPTY_STRING;
		String lsBudgetModificationStatus = HHSConstants.EMPTY_STRING;
		String lsFYConfTaskStatus = HHSConstants.EMPTY_STRING;
		List<String> lsContractInvoiceStatus;
		List<String> lsContractPaymentStatus;
		boolean lbErrorCheck = false;
		boolean lbErrorCheckForNegAmendment = false;
		if (abAuthFlag)
		{
			loHMReqdProp.put(HHSConstants.CONTRACT_ID_WORKFLOW, asContractId);
			lsConfUpdateTaskStatus = fetchTaskStatus(aoUserSession, asContractId, HHSConstants.TASK_CONTRACT_UPDATE);
			lsFYConfTaskStatus = fetchTaskStatus(aoUserSession, asContractId, HHSConstants.TASK_NEW_FY_CONFIGURATION);

			loHMReqdProp.put(HHSConstants.BUDGET_TYPE_ID, HHSConstants.FOUR);
			lsBudgetUpdateStatus = (String) DAOUtil.masterDAO(aoMybatisSession, loHMReqdProp,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_CONTRACT_BUDGET_UPDATE_STATUS,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			loHMReqdProp.put(HHSConstants.BUDGET_TYPE_ID, HHSConstants.THREE);
			lsBudgetModificationStatus = (String) DAOUtil.masterDAO(aoMybatisSession, loHMReqdProp,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_CONTRACT_BUDGET_STATUS,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			loHMReqdProp.put(HHSConstants.BUDGET_TYPE_ID, HHSConstants.ONE);
			liBudgetAmendmentStatusCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loHMReqdProp,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_CONTRACT_BUDGET_AMEND_STATUS,
					HHSConstants.JAVA_UTIL_HASH_MAP);

			loHMReqdProp.put(HHSConstants.CONTRACT_TYPE_ID, HHSConstants.TWO);
			lsContractInvoiceStatus = (List<String>) DAOUtil.masterDAO(aoMybatisSession, loHMReqdProp,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_CONTRACT_INVOICE_STATUS,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			lsContractPaymentStatus = (List<String>) DAOUtil.masterDAO(aoMybatisSession, loHMReqdProp,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_CONTRACT_PAYMENT_STATUS,
					HHSConstants.JAVA_UTIL_HASH_MAP);

			List<String> loContractId = (List<String>) DAOUtil.masterDAO(aoMybatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_ALL_CONTRACT_ID_FOR_UPDATE_CHECK,
					HHSConstants.JAVA_LANG_STRING);

			if (loContractId != null)
			{
				for (String lsContractId : loContractId)
				{
					loHMReqdProp = new HashMap();
					loHMReqdProp.put(HHSConstants.PROPERTY_PE_CONTRACT_ID, lsContractId);
					loHMReqdProp.put(HHSConstants.PROPERTY_PE_TASK_TYPE, HHSConstants.TASK_AMENDMENT_CONFIGURATION);
					liOpenTaskAmendmentConf = new P8ProcessServiceForSolicitationFinancials().getOpenTaskCount(
							aoUserSession, loHMReqdProp);
					if (liOpenTaskAmendmentConf > 0)
					{
						break;
					}
				}
				for (String lsContractId : loContractId)
				{

					loHMReqdProp = new HashMap();
					loHMReqdProp.put(HHSConstants.PROPERTY_PE_CONTRACT_ID, lsContractId);
					loHMReqdProp.put(HHSConstants.PROPERTY_PE_TASK_TYPE, HHSConstants.TASK_AMENDMENT_COF);
					liOpenTaskAmendmentCOF = new P8ProcessServiceForSolicitationFinancials().getOpenTaskCount(
							aoUserSession, loHMReqdProp);
					if (liOpenTaskAmendmentCOF > 0)
					{
						break;
					}
				}

			}
			// Payment status for "status payment approved" has been removed for defect number 6662 in R7
			if (null != lsContractPaymentStatus
					&& (lsContractPaymentStatus.contains(PropertyLoader.getProperty(
									HHSConstants.PROPERTIES_STATUS_CONSTANT,
									HHSConstants.STATUS_PAYMENT_PENDING_APPROVAL))
							|| lsContractPaymentStatus.contains(PropertyLoader.getProperty(
									HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PAYMENT_SUSPENDED)) 
							))
			{
				lbErrorCheck = true;
			}
			else if (null != lsContractInvoiceStatus
					&& (lsContractInvoiceStatus.contains(PropertyLoader.getProperty(
							HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_INVOICE_PENDING_SUBMISSION))
							|| lsContractInvoiceStatus.contains(PropertyLoader.getProperty(
									HHSConstants.PROPERTIES_STATUS_CONSTANT,
									HHSConstants.STATUS_INVOICE_PENDING_APPROVAL)) || lsContractInvoiceStatus
								.contains(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
										HHSConstants.STATUS_INVOICE_RETURNED_FOR_REVISION))))
			{
				lbErrorCheck = true;
			}

			if (liOpenTaskAmendmentConf > 0 || liOpenTaskAmendmentCOF > 0 || liBudgetAmendmentStatusCount > 0)
			{
				lbErrorCheckForNegAmendment = true;
			}
			// Start Release 3.8.0 Enhancement 6481
			Integer loContractRegOrPenRegStatusId = (Integer) DAOUtil.masterDAO(aoMybatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.CHECK_STATUS_FOR_SUS_OR_UN_SUS,
					HHSConstants.JAVA_LANG_STRING);
			Integer loPendingRegContractApprovedBudgetCount = (Integer) DAOUtil.masterDAO(aoMybatisSession,
					asContractId, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSConstants.GET_PEND_REG_CON_APPROVED_BUD_COUNT, HHSConstants.JAVA_LANG_STRING);
			// End Release 3.8.0 Enhancement 6481
			setRuleDataInChannel(loChannel, lsConfUpdateTaskStatus, lsBudgetUpdateStatus, lsBudgetModificationStatus,
					lsFYConfTaskStatus, loContractRegOrPenRegStatusId, loPendingRegContractApprovedBudgetCount);
			return callupdateConfigurationBusinessRule(loChannel, lbErrorCheck, lbErrorCheckForNegAmendment);
		}
		loHMError.put(HHSConstants.CLC_ERROR_CHECK, HHSConstants.SUCCESS);
		return loHMError;
	}

	/**
	 * This method set all required statuses in channel to evaluate Rule for
	 * Contract configuration update
	 * 
	 * <ul>
	 * <li>Check for null conditions</li>
	 * <li>Set all data in channel object</li>
	 * </ul>
	 * 
	 * @param aoChannel
	 * @param asConfUpdateTaskStatus
	 * @param asBudgetUpdateStatus
	 * @param asBudgetModificationStatus
	 * @param asBudgetAmendmentStatus
	 * @param asFYConfTaskStatus
	 * @param asContractAmmendmentAmmount
	 */
	private void setRuleDataInChannel(Channel aoChannel, String asConfUpdateTaskStatus, String asBudgetUpdateStatus,
			String asBudgetModificationStatus, String asFYConfTaskStatus, Integer aoContractStatusId,
			Integer aoPendingRegContractApprovedBudgetCount)
	{
		if (null == asConfUpdateTaskStatus)
		{
			asConfUpdateTaskStatus = HHSConstants.EMPTY_STRING;
		}
		if (null == asBudgetUpdateStatus)
		{
			asBudgetUpdateStatus = HHSConstants.EMPTY_STRING;
		}
		if (null == asBudgetModificationStatus)
		{
			asBudgetModificationStatus = HHSConstants.EMPTY_STRING;
		}
		if (null == asFYConfTaskStatus)
		{
			asFYConfTaskStatus = HHSConstants.EMPTY_STRING;
		}
		aoChannel.setData(HHSConstants.CONF_UPDATE_TASK_STATUS, asConfUpdateTaskStatus);
		aoChannel.setData(HHSConstants.BUDGET_UPDATE_STATUS, asBudgetUpdateStatus);
		aoChannel.setData(HHSConstants.BUDGET_MODIFICATION_STATUS, asBudgetModificationStatus);
		aoChannel.setData(HHSConstants.FY_CONF_TASK_STATUS, asFYConfTaskStatus);
		aoChannel.setData(HHSConstants.CONTRACT_STATUS_ID_KEY, aoContractStatusId.toString());
		aoChannel.setData(HHSConstants.GET_PEND_REG_CON_APPROVED_BUD_COUNT, aoPendingRegContractApprovedBudgetCount);
	}

	/**
	 * This method Evaluate Rule define business-rule.xml file for Update
	 * contract configuration
	 * 
	 * <ul>
	 * <li>Evaluate Rule 'UpdateConfigurationErrorRule01'</li>
	 * <li>For error return error message else set success in hashmap object</li>
	 * </ul>
	 * 
	 * @param aoChannel Channel object
	 * @param abErrorCheck Error check Rule Id
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private HashMap callupdateConfigurationBusinessRule(Channel aoChannel, boolean abErrorCheck,
			boolean abErrorCheckForNegAmendment) throws ApplicationException
	{
		HashMap loHMError = new HashMap();
		String lsRuleReturnValue;
		lsRuleReturnValue = (String) Rule.evaluateRule(HHSConstants.UPDATE_CONFIGURATION_ERROR_RULE01, aoChannel);
		if (lsRuleReturnValue.equalsIgnoreCase(HHSConstants.FALSE) || lsRuleReturnValue.isEmpty())
		{
			if (abErrorCheck)
			{
				loHMError.put(HHSConstants.CLC_ERROR_CHECK, HHSConstants.ERROR_FLAG);
				loHMError.put(HHSConstants.CLC_ERROR_MSG, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.CONTRATC_UPDATE_CHECK04));
				return loHMError;
			}
			else if (abErrorCheckForNegAmendment)
			{
				loHMError.put(HHSConstants.CLC_ERROR_CHECK, HHSConstants.ERROR_FLAG);
				loHMError.put(HHSConstants.CLC_ERROR_MSG, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.CONTRATC_UPDATE_CHECK02));
				return loHMError;
			}
		}
		else
		{
			loHMError.put(HHSConstants.CLC_ERROR_CHECK, HHSConstants.ERROR_FLAG);
			loHMError.put(HHSConstants.CLC_ERROR_MSG, lsRuleReturnValue);
			return loHMError;
		}
		loHMError.put(HHSConstants.CLC_ERROR_CHECK, HHSConstants.SUCCESS);
		return loHMError;
	}

	/**
	 * This method Get All the values required for Contract configuration
	 * workflows .
	 * <ul>
	 * <li>Call 'findContractDetailsByContractForWF' mapper</li>
	 * <li>Return 'FinancialWFBean' object .</li>
	 * </ul>
	 * <br>
	 * 
	 * This method was updated in R4.
	 * 
	 * @param aoMyBatisSession SqlSession object
	 * @param aoHMWFRequiredProps HashMap object
	 * @return aoHMWFRequiredProps
	 * @throws ApplicationException if ApplicationException occurs
	 */

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public HashMap getFinancialWFProperty(SqlSession aoMyBatisSession, HashMap aoHMWFRequiredProps)
			throws ApplicationException
	{
		try
		{
			FinancialWFBean loFinancialWFBean = null;
			String lsContractId = (String) aoHMWFRequiredProps.get(HHSConstants.CONTRACT_ID_WORKFLOW);
			// R5 change starts
			Boolean loPSRFlag = Boolean.FALSE;
			if (aoHMWFRequiredProps.containsKey(HHSR5Constants.ONLY_PSR))
				loPSRFlag = (Boolean) aoHMWFRequiredProps.get(HHSR5Constants.ONLY_PSR);
			// R5 change ends
			String lsContractIdNew = lsContractId;
			String lsProcurementId = (String) aoHMWFRequiredProps.get(HHSConstants.PROCUREMENT_ID);
			String lsWorkflowName = (String) aoHMWFRequiredProps.get(HHSConstants.WORKFLOW_NAME);
			String lsUserId = (String) aoHMWFRequiredProps.get(HHSConstants.SUBMITTED_BY);
			String lsBudgetId = (String) aoHMWFRequiredProps.get(HHSConstants.BUDGET_ID_WORKFLOW);
			// Added for R6: return payment review task
			String lsReturnPaymentId = (String) aoHMWFRequiredProps.get(HHSConstants.RETURN_PAYMENT_DETAIL_ID);
			// Added for R6: return payment review task end
			String lsLaunchByOrgType = (String) aoHMWFRequiredProps.get(HHSConstants.PROPERTY_PE_LAUNCH_ORG_TYPE);
			String lsInvoiceId = (String) aoHMWFRequiredProps.get(HHSConstants.INVOICE_ID);
			String lsFYId = (String) aoHMWFRequiredProps.get(HHSConstants.CLC_FISCAL_YEAR_ID);
			String lsBudgetAdvanceId = (String) aoHMWFRequiredProps.get(HHSConstants.BUDGET_ADVANCE_ID);
			String lsTaskSource = (String) aoHMWFRequiredProps.get(HHSConstants.PROPERTY_PE_TASK_SOURCE);
			Boolean loProviderInitiated = (Boolean) aoHMWFRequiredProps.get(HHSConstants.AUDIT_PROVIDER_INITIATED_TASK);
			if (null != lsProcurementId && null != lsWorkflowName
					&& lsWorkflowName.equalsIgnoreCase(HHSConstants.WF_PROCUREMENT_CERTIFICATION_FUND))
			{
				loFinancialWFBean = (FinancialWFBean) DAOUtil.masterDAO(aoMyBatisSession, aoHMWFRequiredProps,
						HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FIND_PROCUREMENT_DETAILS_FOR_WF,
						HHSConstants.JAVA_UTIL_HASH_MAP);
			}
			else
			{
				Map loContractInfo = (HashMap) DAOUtil.masterDAO(aoMyBatisSession, lsContractId,
						HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_CONTRACT_INFO,
						HHSConstants.JAVA_LANG_STRING);
				if (String.valueOf(loContractInfo.get(HHSConstants.FLS_CONTRACT_TYPE_ID)).equalsIgnoreCase(
						HHSConstants.TWO))
				{
					lsContractIdNew = String.valueOf(loContractInfo.get(HHSConstants.CONTRACT_ID_UNDERSCORE));
				}
				loFinancialWFBean = (FinancialWFBean) DAOUtil.masterDAO(aoMyBatisSession, lsContractIdNew,
						HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.CM_FIND_CONTRACT_DTLS_WF,
						HHSConstants.JAVA_LANG_STRING);

				if (String.valueOf(loContractInfo.get(HHSConstants.CONTRACT_SOURCE_ID)).equalsIgnoreCase(
						HHSConstants.TWO)
						&& (HHSConstants.NA_KEY.equalsIgnoreCase(loFinancialWFBean.getProcEpin()) || null == loFinancialWFBean
								.getProcEpin()))
				{
					FinancialWFBean loFinWFBean = null;
					loFinWFBean = (FinancialWFBean) DAOUtil.masterDAO(aoMyBatisSession, lsContractIdNew,
							HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FIND_PROC_EPIN_R3_CONTRACT_FOR_WF,
							HHSConstants.JAVA_LANG_STRING);
					if (null != loFinWFBean)
					{
						loFinancialWFBean.setProcEpin(loFinWFBean.getProcEpin());
					}
				}

			}
			if (null != loFinancialWFBean)
			{
				loFinancialWFBean.setContractId(lsContractId);
				loFinancialWFBean.setProcurementId(lsProcurementId);
				loFinancialWFBean.setUserId(lsUserId);
				loFinancialWFBean.setBudgetId(lsBudgetId);
				loFinancialWFBean.setInvoiceId(lsInvoiceId);
				loFinancialWFBean.setFiscalYearId(lsFYId);
				loFinancialWFBean.setAdvanceNumber(lsBudgetAdvanceId);
				// Added for R6: return payment review task
				loFinancialWFBean.setReturnPaymentDetailsId(lsReturnPaymentId);
				// Added for R6: return payment review task end
				loFinancialWFBean.setLaunchCOF((Boolean) aoHMWFRequiredProps.get(HHSConstants.PROPERTY_PE_LAUNCH_COF));
				loFinancialWFBean.setLaunchByOrgType(lsLaunchByOrgType);
				aoHMWFRequiredProps = HHSUtil.setFinancialWFProperty(loFinancialWFBean,
						(String) aoHMWFRequiredProps.get(HHSConstants.WORKFLOW_NAME));
				aoHMWFRequiredProps.put(HHSConstants.AUDIT_PROVIDER_INITIATED_TASK, loProviderInitiated);
				aoHMWFRequiredProps.put(HHSConstants.CONTRACT_ID_WORKFLOW, lsContractId);
				aoHMWFRequiredProps.put(HHSConstants.SUBMITTED_BY, lsUserId);
				aoHMWFRequiredProps.put(HHSConstants.PROPERTY_PE_TASK_SOURCE, lsTaskSource);
				// R5 change starts
				aoHMWFRequiredProps.put(HHSR5Constants.ONLY_PSR, loPSRFlag);
				// R5 change ends
				return aoHMWFRequiredProps;
			}
			else
			{
				throw new ApplicationException("Error while getting Workflow Properties for Task :" + lsWorkflowName
						+ "ContractId :" + lsContractId + "Procurement Id:" + lsProcurementId);
			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured getting properties for Workflow in getFinancialWFProperty() method.",
					loAppEx);
			setMoState("Exception occured getting properties for Workflow in getFinancialWFProperty() method./n");
			throw loAppEx;
		}

		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Exception occured getting properties for Workflow in getFinancialWFProperty() method.",
					loEx);
			setMoState("Exception occured getting properties for Workflow in getFinancialWFProperty() method.\n");
			throw new ApplicationException(
					"Exception occured getting properties for Workflow in getFinancialWFProperty() method.", loEx);
		}
	}

	/**
	 * This method is triggered from Add/Renew Contract.
	 * <ul>
	 * <li>Get the Sequence for contractId from contract</li>
	 * <li>An integer value is returned which determines the sequence of
	 * contractId from contract table.</li>
	 * <li>calls query 'getContractSeqFromTable'</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - aoMybatisSession
	 * @throws ApplicationException - ApplicationException object
	 * @return aiCurrentSeq - int
	 */
	public int getNextSeqFromTable(SqlSession aoMybatisSession) throws ApplicationException
	{
		int liCurrentSeq;
		try
		{
			liCurrentSeq = (Integer) DAOUtil.masterDAO(aoMybatisSession, null,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.GET_CONTRACT_SEQ_FROM_TABLE, null);
			setMoState("Transaction Success:: FinancialsListService:getNextSeqFromTable method - Current Sequence from Table "
					+ "have been retreived successfully: /n");
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while retreiving Current Sequence from Contract Table", loAppEx);
			setMoState("Transaction Failed:: FinancialsListService:getNextSeqFromTable method -Exception occured while while retreiving "
					+ "Current Sequence from Contract Table: /n");
			throw loAppEx;
		}

		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Exception occured while retreiving Current Sequence from Contract Table", loEx);
			setMoState("Transaction Failed:: FinancialsListService: getNextSeqFromTable method - failed to get the sequence"
					+ " \n");
			throw new ApplicationException("Error occured while getting the sequence from Contract table", loEx);
		}
		return liCurrentSeq;
	}

	/**
	 * The Method will update the Contract to UnSuspenContract
	 * <ul>
	 * <li>Condition 1: If Authentication Flag is false,It will not proceed
	 * further</li>
	 * <li>Condition 2: If Authentication Flag is true,It will proceed for
	 * unSuspend contract and update comments in Database</li>
	 * <li></li>
	 * <li>calls query 'updateContractReason'</li>
	 * <li>calls query 'updatePaymentUnsuspend'</li>
	 * <li>calls query 'updateInvoiceUnsuspend'</li>
	 * <li>calls query 'updateBudgetUnsuspend'</li>
	 * <li>calls query 'updateContractUnsuspend'</li>
	 * </ul>
	 * 
	 * @param asContractId Contract Id as input
	 * @param asContractReason Contract Reason as input
	 * @param aoMybatisSession sql session as input
	 * @param aoStatusCheck true if valid username/password
	 * @param asUserId User id
	 * @param asUserOrgType user org type
	 * @return loUnsuspendStatus true if database updated/false if not updated
	 * @throws ApplicationException Exception thrown in case query fails.
	 */
	@SuppressWarnings("unchecked")
	public List<HhsAuditBean> unSuspendContract(String asContractId, String asContractReason,
			SqlSession aoMybatisSession, Boolean aoStatusCheck, String asUserId, String asUserOrgType)
			throws ApplicationException
	{
		HashMap<String, String> loContractInfo = null;
		List<HhsAuditBean> loAuditList = null;

		try
		{
			if (aoStatusCheck != null && aoStatusCheck)
			{
				loContractInfo = new HashMap<String, String>();
				loContractInfo.put(HHSConstants.CONTRACT_ID_KEY, asContractId);
				loContractInfo.put(HHSConstants.MOD_BY_USER_ID, asUserId);
				loContractInfo.put(HHSConstants.AS_CONTRACT_REASON, asContractReason);
				loContractInfo.put(HHSConstants.LS_SUSPEND_CONTRACT_STATUS_ID, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_CONTRACT_SUSPENDED));
				loContractInfo.put(HHSConstants.LS_SUSPEND_BUDGET_STATUS_ID, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_BUDGET_SUSPENDED));
				loContractInfo.put(HHSConstants.LS_SUSPEND_INVOICE_STATUS_ID, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_INVOICE_SUSPENDED));
				loContractInfo.put(HHSConstants.LS_SUSPEND_PAYMNET_STATUS_ID, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PAYMENT_SUSPENDED));

				// audit table entries
				List<PaymentSortAndFilter> loPaymentAuditInfo = (List<PaymentSortAndFilter>) DAOUtil.masterDAO(
						aoMybatisSession, loContractInfo, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
						HHSConstants.SELECT_PAYMENT_UNSUSPEND, HHSConstants.JAVA_UTIL_HASH_MAP);

				List<PaymentSortAndFilter> loInvoiceAuditInfo = (List<PaymentSortAndFilter>) DAOUtil.masterDAO(
						aoMybatisSession, loContractInfo, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
						HHSConstants.SELECT_INVOICE_UNSUSPEND, HHSConstants.JAVA_UTIL_HASH_MAP);

				List<PaymentSortAndFilter> loBudgetAuditInfo = (List<PaymentSortAndFilter>) DAOUtil.masterDAO(
						aoMybatisSession, loContractInfo, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
						HHSConstants.SELECT_BUDGET_UNSUSPEND, HHSConstants.JAVA_UTIL_HASH_MAP);

				loAuditList = addAuditInfo(asUserId, asUserOrgType, loPaymentAuditInfo, loInvoiceAuditInfo,
						loBudgetAuditInfo);

				// code to unsuspend contract
				DAOUtil.masterDAO(aoMybatisSession, loContractInfo, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
						HHSConstants.UPDATE_CONTRACT_REASON, HHSConstants.JAVA_UTIL_HASH_MAP);
				DAOUtil.masterDAO(aoMybatisSession, loContractInfo, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
						HHSConstants.UPDATE_PAYMENT_UNSUSPEND, HHSConstants.JAVA_UTIL_HASH_MAP);

				DAOUtil.masterDAO(aoMybatisSession, loContractInfo, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
						HHSConstants.UPDATE_INVOICE_UNSUSPEND, HHSConstants.JAVA_UTIL_HASH_MAP);
				DAOUtil.masterDAO(aoMybatisSession, loContractInfo, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
						HHSConstants.UPDATE_BUDGET_UNSUSPEND, HHSConstants.JAVA_UTIL_HASH_MAP);
				DAOUtil.masterDAO(aoMybatisSession, loContractInfo, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
						HHSConstants.UPDATE_CONTRACT_UNSUSPEND, HHSConstants.JAVA_UTIL_HASH_MAP);

				setMoState("Contract unsuspended succesfully");

			}
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.CONTRACT_ID_KEY, asContractId);
			LOG_OBJECT.Error("Error occurred while unsuspending a contract", loAppEx);
			setMoState("Error occurred while unsuspending a contract for contract id : " + asContractId);
			throw loAppEx;
		}
		return loAuditList;
	}

	/**
	 * This method set the information related to audit.
	 * @param lsUserId user id as input.
	 * @param lsUserOrgType org type as input.
	 * @param loPaymentAuditInfo payment related audit info.
	 * @param loInvoiceAuditInfo invoice related audit info.
	 * @param loBudgetAuditInfo budget related audit info.
	 * @return loAuditList as audit ralted info as output.
	 * @throws ApplicationException Application Exception thrown in case query
	 *             fails.
	 */
	private List<HhsAuditBean> addAuditInfo(String lsUserId, String lsUserOrgType,
			List<PaymentSortAndFilter> loPaymentAuditInfo, List<PaymentSortAndFilter> loInvoiceAuditInfo,
			List<PaymentSortAndFilter> loBudgetAuditInfo) throws ApplicationException
	{
		List<HhsAuditBean> loAuditList = new ArrayList<HhsAuditBean>();
		String lsAuditType = null;

		if (lsUserOrgType != null && lsUserOrgType.equalsIgnoreCase(ApplicationConstants.AGENCY_ORG))
		{
			lsAuditType = HHSConstants.AGENCY_AUDIT;
		}
		else
		{
			lsAuditType = HHSConstants.ACCELERATOR_AUDIT;
		}
		if (loInvoiceAuditInfo != null && !loInvoiceAuditInfo.isEmpty())
		{
			for (PaymentSortAndFilter loPaymentSortAndFilter : loInvoiceAuditInfo)
			{
				auditDataToChannel(
						loAuditList,
						HHSUtil.getStatusName(HHSConstants.INVOICE,
								Integer.valueOf(loPaymentSortAndFilter.getPrevStatusId())),
						HHSConstants.AUDIT_INVOICES,
						loPaymentSortAndFilter.getInvoiceId(),
						lsUserId,
						lsAuditType,
						HHSUtil.getStatusName(HHSConstants.INVOICE,
								Integer.valueOf(loPaymentSortAndFilter.getStatusId())));
			}
		}
		if (loBudgetAuditInfo != null && !loBudgetAuditInfo.isEmpty())
		{
			for (PaymentSortAndFilter loPaymentSortAndFilter : loBudgetAuditInfo)
			{
				String lsEntityType = HHSConstants.BUDGET_ENTITY_TYPE_MAP.get(loPaymentSortAndFilter.getBudgetTypeId());
				if (null != lsEntityType)
				{
					auditDataToChannel(
							loAuditList,
							HHSUtil.getStatusName(HHSConstants.BUDGETLIST_BUDGET,
									Integer.valueOf(loPaymentSortAndFilter.getPrevStatusId())),
							lsEntityType,
							loPaymentSortAndFilter.getBudgetId(),
							lsUserId,
							lsAuditType,
							HHSUtil.getStatusName(HHSConstants.BUDGETLIST_BUDGET,
									Integer.valueOf(loPaymentSortAndFilter.getStatusId())));
				}
			}
		}
		return loAuditList;
	}

	/**
	 * This method set info related to audit.
	 * @param aoAuditList audit related info as input.
	 * @param asPrevStatus previous status as input.
	 * @param asEntityType entity type as input.
	 * @param asId id, it may be budget, invoice or payment id.
	 * @param asUserId user id as input.
	 * @param asAuditType audit type as input.
	 * @param asCurrentStatus current status as input.
	 */
	private void auditDataToChannel(List<HhsAuditBean> aoAuditList, String asPrevStatus, String asEntityType,
			String asId, String asUserId, String asAuditType, String asCurrentStatus)
	{
		aoAuditList.add(HHSUtil.addAuditDataToChannel(HHSConstants.STATUS_CHANGE, HHSConstants.STATUS_CHANGE,
				ApplicationConstants.STATUS_CHANGED_FROM + HHSConstants.SPACE + HHSConstants.STR + asCurrentStatus
						+ HHSConstants.STR + HHSConstants.TO + HHSConstants.STR + asPrevStatus + HHSConstants.STR,
				asEntityType, asId, asUserId, asAuditType));
	}

	/**
	 * This method is used for workflow related action while suspending contract
	 * <ul>
	 * <li>calls query 'fetchAllContractId'</li>
	 * </ul>
	 * @param aoUserSession a user bean having information about user
	 * @param asContractId contract id as input
	 * @param asUserId status check id as input
	 * @param aoMybatisSession SqlSession object
	 * @return boolean if workflow is success
	 * @throws ApplicationException Exception thrown in case query fails.
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public boolean suspendContractRelatedWorkflow(P8UserSession aoUserSession, String asContractId, String asUserId,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		Boolean loSuspendStatusWorkflow = false;
		List<String> loContractId = null;
		HashMap loHmWFProperties = null;
		try
		{
			loContractId = (List<String>) DAOUtil.masterDAO(aoMybatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_ALL_CONTRACT_ID,
					HHSConstants.JAVA_LANG_STRING);
			if (loContractId != null)
			{
				for (String lsContractId : loContractId)
				{
					loHmWFProperties = new HashMap();
					loHmWFProperties.put(HHSConstants.PROPERTY_PE_CONTRACT_ID, lsContractId);
					loHmWFProperties.put(HHSConstants.COMPONENT_ACTION, HHSConstants.SUSPEND_ALL_FINANCIAL_TASKS);
					loHmWFProperties.put(HHSConstants.PROPERTY_PE_SUBMITTED_BY, asUserId);
					new P8ProcessServiceForSolicitationFinancials().launchWorkflow(aoUserSession,
							HHSConstants.WF_FINANCIAL_UTILITY, loHmWFProperties);
				}
			}
			loSuspendStatusWorkflow = true;
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.CONTRACT_ID_KEY, asContractId);
			LOG_OBJECT.Error("Error occurred while workflow suspending a contract", loAppEx);
			setMoState("Error occurred while workflow suspending a contract for contract id : " + asContractId);
			throw loAppEx;
		}
		return loSuspendStatusWorkflow;
	}

	/***
	 * This method is used for workflow related action while unsuspending
	 * contract
	 * <ul>
	 * <li>calls query 'fetchAllContractId'</li>
	 * </ul>
	 * @param aoUserSession a user bean having information about user
	 * @param asContractId contract id as input
	 * @param aoStatusCheck status check id as input
	 * @return boolean if worflow is sucess
	 * @throws ApplicationException Exception thrown in case query fails.
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public boolean unsuspendContractRelatedWorkflow(P8UserSession aoUserSession, String asContractId, String asUserId,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		// Initially set true, change to false when workflow is success
		Boolean loUnSuspendStatusWorkflow = false;
		List<String> loContractId = null;
		HashMap loHmWFProperties = null;
		try
		{
			loContractId = (List<String>) DAOUtil.masterDAO(aoMybatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_ALL_CONTRACT_ID,
					HHSConstants.JAVA_LANG_STRING);
			if (loContractId != null)
			{
				for (String lsContractId : loContractId)
				{
					loHmWFProperties = new HashMap();
					loHmWFProperties.put(HHSConstants.PROPERTY_PE_CONTRACT_ID, lsContractId);
					loHmWFProperties.put(HHSConstants.COMPONENT_ACTION, HHSConstants.UNSUSPEND_ALL_FINANCIAL_TASK);
					loHmWFProperties.put(HHSConstants.PROPERTY_PE_SUBMITTED_BY, asUserId);
					new P8ProcessServiceForSolicitationFinancials().launchWorkflow(aoUserSession,
							HHSConstants.WF_FINANCIAL_UTILITY, loHmWFProperties);
				}
				loUnSuspendStatusWorkflow = true;
			}
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.CONTRACT_ID_KEY, asContractId);
			LOG_OBJECT.Error("Error occurred while workflow unsuspending a contract", loAppEx);
			setMoState("Error occurred while workflow unsuspending a contract for contract id : " + asContractId);
			throw loAppEx;
		}
		return loUnSuspendStatusWorkflow;
	}

	/**
	 * This method check id status for contract is “Pending Registration” or
	 * “Registered”
	 * <ul>
	 * <li>calls query 'checkStatusForSusOrUnSus'</li>
	 * </ul>
	 * @param aoAuthStatusFlag authrisation Status Flag as input
	 * @param asContractId contract id as input
	 * @param aoMyBatisSession sql session as input
	 * @return loStatusCheck as input
	 * @throws ApplicationException ApplicationException Exception thrown in
	 *             case query fails.
	 */
	public Boolean checkStatusIdForSuspended(String asContractId, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		Boolean loStatusCheck = false;
		Channel loChannel = new Channel();
		try
		{
			Integer loContractRegOrPenRegStatusId = (Integer) DAOUtil.masterDAO(aoMyBatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.CHECK_STATUS_FOR_SUS_OR_UN_SUS,
					HHSConstants.JAVA_LANG_STRING);
			if (loContractRegOrPenRegStatusId != null)
			{
				loChannel.setData(HHSConstants.CONTRACT_STATUS_ID, loContractRegOrPenRegStatusId.toString());
				String lsStatusCheck = (String) Rule.evaluateRule(HHSConstants.CHECK_STATUS_FOR_SUSPENDED, loChannel);
				loStatusCheck = (lsStatusCheck != null && lsStatusCheck.equalsIgnoreCase(HHSConstants.TRUE)) ? Boolean.TRUE
						: Boolean.FALSE;

			}
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.CONTRACT_ID_KEY, asContractId);
			LOG_OBJECT.Error("Error occurred while checking status for suspend contract", loAppEx);
			setMoState("Error occurred while checking status for suspend contract id : " + asContractId);
			throw loAppEx;
		}
		return loStatusCheck;
	}

	/**
	 * This method check id status for contract is unsuspended
	 * <ul>
	 * <li>calls query 'checkStatusForSusOrUnSus'</li>
	 * </ul>
	 * @param aoAuthStatusFlag authrisation Status Flag as input
	 * @param asContractId contract id as input
	 * @param aoMyBatisSession sql session as input
	 * @return loStatusCheck as input
	 * @throws ApplicationException ApplicationException Exception thrown in
	 *             case query fails.
	 */
	public Boolean checkStatusIdForUnSuspended(String asContractId, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		Boolean loStatusCheck = false;
		Channel loChannel = new Channel();
		try
		{
			Integer loContractSuspendedStatusId = (Integer) DAOUtil.masterDAO(aoMyBatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.CHECK_STATUS_FOR_SUS_OR_UN_SUS,
					HHSConstants.JAVA_LANG_STRING);
			loChannel.setData(HHSConstants.CONTRACT_STATUS_ID, loContractSuspendedStatusId.toString());
			String lsStatusCheck = (String) Rule.evaluateRule(HHSConstants.CHECK_STATUS_FOR_UN_SUSPENDED, loChannel);
			loStatusCheck = lsStatusCheck != null && lsStatusCheck.equalsIgnoreCase(HHSConstants.TRUE) ? Boolean.TRUE
					: Boolean.FALSE;
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.CONTRACT_ID_KEY, asContractId);
			LOG_OBJECT.Error("Error occurred while checking status for unsuspend contract", loAppEx);
			setMoState("Error occurred while checking status for unsuspend contract id : " + asContractId);
			throw loAppEx;
		}
		return loStatusCheck;
	}

	/**
	 * This method is triggered from Renew Contract Existing.
	 * <ul>
	 * <li>Pass the HashMap for contractId & statusId</li>
	 * <li>An integer value is returned which determines as the existence of the
	 * renewal record.</li>
	 * <li>calls query 'renewalRecordExist'</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession
	 * @param aoContractStatus
	 * @throws ApplicationException
	 * @return lbContractRenewalStatus
	 */
	@SuppressWarnings("rawtypes")
	public boolean renewalRecordExist(HashMap aoContractStatus, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		int liTempStatus;
		boolean lbContractRenewalStatus = false;
		try
		{
			liTempStatus = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoContractStatus,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.RENEWAL_RECORD_EXIST,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			setMoState("Transation Success:: FinancialsListService:renewalRecordExist method - record renewal from contract Table "
					+ "have been retreived successfully: /n");
			if (liTempStatus <= HHSConstants.INT_ZERO)
			{
				lbContractRenewalStatus = true;
			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while retreiving existing records status from Contract Table", loAppEx);
			setMoState("Transaction Failed:: FinancialsListService:renewalRecordExist method -Exception occured "
					+ "record renewal from contract Table: /n");
			throw loAppEx;
		}

		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Exception occured while retreiving existing records status from Contract Table", loEx);
			setMoState("Transaction Failed:: FinancialsListService:renewalRecordExist method - failed to get record "
					+ "renewal from contract Table \n");
			throw new ApplicationException("Error occured while getting the renewalExist from Contract table", loEx);
		}
		return lbContractRenewalStatus;
	}

	/**
	 * This method is triggered from cancel Amendment to validate whether
	 * <Contract Amendment Status> equal to "Pending Registration" exists for
	 * the Contract Contract Existing.
	 * <ul>
	 * <li>Pass the HashMap for contractId & statusId & contract_type_Id</li>
	 * <li>An integer value is returned which determines the status.</li>
	 * <li>calls query 'validateAmendContract'</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession
	 * @param aoContractStatus
	 * @throws ApplicationException
	 * @return lbpendingRegistration
	 */
	@SuppressWarnings("rawtypes")
	public boolean validateAmendContract(SqlSession aoMybatisSession, HashMap aoContractStatus)
			throws ApplicationException
	{
		int liTempStatus;
		boolean lbPendingRegistration = false;
		try
		{
			liTempStatus = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoContractStatus,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.VALIDATE_AMEND_CONTRACT,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			setMoState("Transation Success:: FinancialsListService:validateAmendContract method - : /n");
			if (liTempStatus <= HHSConstants.INT_ZERO)
			{
				lbPendingRegistration = true;
			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error(
					"Exception occured while validating Pending Registration exists for Amendment from Contract Table  "
							+ aoContractStatus, loAppEx);
			setMoState("Transaction Failed:: FinancialsListService:validateAmendContract method -Exception occured "
					+ "record renewal from contract Table: /n");
			throw loAppEx;
		}

		catch (Exception loEx)
		{
			LOG_OBJECT.Error(
					"Exception occured while validating Pending Registration exists for Amendment from Contract Table  "
							+ aoContractStatus, loEx);
			setMoState("Transaction Failed:: FinancialsListService:validateAmendContract method - failed to get record "
					+ "renewal from contract Table \n");
			throw new ApplicationException(
					"Exception occured while validating Pending Registration exists for Amendment from Contract Table",
					loEx);
		}
		return lbPendingRegistration;
	}

	/**
	 * 
	 * This method is triggered from cancel Amendment to validate If a negative
	 * amendment is being cancelled and if there are open Configuration Update
	 * Tasks OR open Contract Budget Update Review tasks OR Contract Budget
	 * Update in “Pending Submission”, “Returned for Revision” or “Pending
	 * Approval” status for that contract than validation return false
	 * <ul>
	 * <li>Check If Amendment is negative by calling 'negativeAmendment'
	 * transaction</li>
	 * <li>If above is true check for Contract update and Contract Budget task
	 * is exist if yes return false</li>
	 * <li>If above condition do not match check for budget count by calling
	 * 'updateBudgetCount' transaction</li>
	 * </ul>
	 * <br>
	 * 
	 * This method was added in R4.
	 * 
	 * @param aoMybatisSession SQL session
	 * @param aoContractStatus Contract Details Hashmap object
	 * @param aoUserSession Filenet session details
	 * @param loNegativeAmendment Integer containing negative amount.
	 * @return true or false
	 * @throws ApplicationException if ApplicationException occurs.
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public boolean cancellingNegativeAmendmentCheck(SqlSession aoMybatisSession, HashMap aoContractStatus,
			P8UserSession aoUserSession, Integer loNegativeAmendment) throws ApplicationException
	{
		boolean lbValidation = true;
		String lsContractId = (String) aoContractStatus.get(HHSConstants.CLC_CONTRACT_ID_UNDERSCORE);
		int liOpenTaskUpdateConf = 0;
		int liOpenTaskUpdateBudget = 0;
		int liUpdateBudgetCount = 0;

		if (loNegativeAmendment == 1)
		{
			HashMap loHMReqdProp = new HashMap();
			loHMReqdProp.put(HHSConstants.PROPERTY_PE_CONTRACT_ID, lsContractId);
			loHMReqdProp.put(HHSConstants.PROPERTY_PE_TASK_TYPE, HHSConstants.TASK_CONTRACT_UPDATE);
			liOpenTaskUpdateConf = new P8ProcessServiceForSolicitationFinancials().getOpenTaskCount(aoUserSession,
					loHMReqdProp);
			if (liOpenTaskUpdateConf > 0)
			{
				return false;
			}
			loHMReqdProp = new HashMap();
			loHMReqdProp.put(HHSConstants.PROPERTY_PE_CONTRACT_ID, lsContractId);
			loHMReqdProp.put(HHSConstants.PROPERTY_PE_TASK_TYPE, HHSConstants.TASK_BUDGET_UPDATE);
			liOpenTaskUpdateBudget = new P8ProcessServiceForSolicitationFinancials().getOpenTaskCount(aoUserSession,
					loHMReqdProp);
			if (liOpenTaskUpdateBudget > 0)
			{
				return false;
			}
			liUpdateBudgetCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, lsContractId,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.UPDATE_BUDGET_COUNT,
					HHSConstants.JAVA_LANG_STRING);

			if (liUpdateBudgetCount > 0)
			{
				return false;
			}
		}

		return lbValidation;
	}

	/**
	 * This method check whether amendment is negative
	 * @param aoMybatisSession sql session as input
	 * @param lsAmendmentId amendment id as input
	 * @return loNegativeAmendment count of negative amendment
	 * @throws ApplicationException Exception in case a query fails
	 */
	@SuppressWarnings("rawtypes")
	public Integer isNegativeAmendment(SqlSession aoMybatisSession, HashMap aoContractStatus)
			throws ApplicationException
	{
		String lsAmendmentId = null;
		Integer loNegativeAmendment = null;
		try
		{
			lsAmendmentId = (String) aoContractStatus.get(HHSConstants.CLC_AMENDMENT_ID_UNDERSCORE);
			loNegativeAmendment = (Integer) DAOUtil.masterDAO(aoMybatisSession, lsAmendmentId,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.NEGATIVE_AMENDMENT_COUNT,
					HHSConstants.JAVA_LANG_STRING);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Application Exception Occured in isNegativeAmendment", loAppEx);
			setMoState("Application Exception Occured in isNegativeAmendment \n");
			throw loAppEx;
		}
		return loNegativeAmendment;
	}

	/**
	 * This is a error check while performing negative amendment.
	 * @param aoMybatisSession sql session as input
	 * @param aoContractStatus map contain contract details
	 * @param lbValidation validation check
	 * @param loNegativeAmendment negative amendment count
	 * @return HashMap map containing error message
	 * @throws ApplicationException Exception in case of code failure.
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public HashMap cancellingNegativeAmendmentCheckSecond(SqlSession aoMybatisSession, HashMap aoContractStatus,
			Boolean lbValidation, Integer loNegativeAmendment) throws ApplicationException
	{
		String lsAmendmentId = (String) aoContractStatus.get(HHSConstants.CLC_AMENDMENT_ID_UNDERSCORE);
		HashMap loHashMapErrorCheck = new HashMap();
		HashMap loHashMap = new HashMap();
		loHashMap.put(HHSConstants.AMEND_CONTRACT_ID_WORKFLOW, lsAmendmentId);
		loHashMap.put(HHSConstants.CONTRACT_TYPE_ID, HHSConstants.TWO);
		loHashMap.put(HHSConstants.AS_STATUS_ID, PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_BUDGET_APPROVED));
		List<String> loStatusList = new ArrayList<String>();
		loStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_CONTRACT_PENDING_CONFIGURATION));
		loStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_CONTRACT_PENDING_COF));
		loStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_CONTRACT_PENDING_SUBMISSION));
		loStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_CONTRACT_PENDING_APPROVAL));
		loHashMap.put(HHSConstants.STATUS_LIST, loStatusList);

LOG_OBJECT.Info("[cancellingNegativeAmendmentCheckSecond] loNegativeAmendment="+ loNegativeAmendment+ "   lbValidation:" + lbValidation + 
		"\n Map:"  + loHashMap);

		if (loNegativeAmendment == 1)
		{
			if (lbValidation)
			{
				LOG_OBJECT.Info("[cancellingNegativeAmendmentCheckSecond] loNegativeAmendment == 1 && lbValidation == true " );
				Integer loBudgetApprovedCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loHashMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.BUDGET_APPROVED_COUNT,
						HHSConstants.JAVA_UTIL_HASH_MAP);

				if (loBudgetApprovedCount > 0)
				{
					LOG_OBJECT.Info("[cancellingNegativeAmendmentCheckSecond] loNegativeAmendment == 1 && lbValidation == true  && (loBudgetApprovedCount > 0) " );
					
					Integer loNegAmendCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loHashMap,
							HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.OTHER_NEGATIVE_AMENDMENT_COUNT,
							HHSConstants.JAVA_UTIL_HASH_MAP);
					if (loNegAmendCount > 0)
					{
						LOG_OBJECT.Info("[cancellingNegativeAmendmentCheckSecond] loNegativeAmendment == 1 && lbValidation == true  && (loBudgetApprovedCount > 0) && (loNegAmendCount > 0) " );
						loHashMapErrorCheck.put(HHSConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
								HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
								HHSConstants.CANCELLING_NEGATIVE_AMENDMENT_CHECK_ERROR_MESSAGE_SECOND));
					}
				}
			}
			else
			{
				LOG_OBJECT.Info("[cancellingNegativeAmendmentCheckSecond] loNegativeAmendment == 1 && lbValidation == false " );
				loHashMapErrorCheck.put(HHSConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.CANCELLING_NEGATIVE_AMENDMENT_CHECK_ERROR_MESSAGE));
			}
		}
		// Start || Changes done for release 3.12.0 for enhancement 6601
		Integer loIsAmendmentRegInFMS = (Integer) DAOUtil.masterDAO(aoMybatisSession, loHashMap,
				HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.CANCEL_AMENDMENT_CHECK_REG_IN_FMS,
				HHSConstants.JAVA_UTIL_HASH_MAP);
		
		//[Start] R8.8.0 QC9400  
		Integer loAmendmentStatusId = (Integer) DAOUtil.masterDAO(aoMybatisSession, loHashMap,
				HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.CANCEL_AMENDMENT_CHECK_STATUS,
				HHSConstants.JAVA_UTIL_HASH_MAP);
		LOG_OBJECT.Info("[cancellingNegativeAmendmentCheckSecond] loAmendmentStatusId ==   " + loAmendmentStatusId + 
				" \n Error" +  loHashMapErrorCheck.get(HHSConstants.ERROR_MESSAGE) );

		
		if ( loAmendmentStatusId.intValue() !=  HHSConstants.FINALCIAL_PENDING_COF.intValue() 
				&&  loAmendmentStatusId.intValue() != HHSConstants.FINALCIAL_PENDING_CONFIGURATION.intValue()  ) 
		{
				if( null != loIsAmendmentRegInFMS && loIsAmendmentRegInFMS != 0  )
				{
					loHashMapErrorCheck.put(HHSConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.CANCEL_AMENDMENT_REGISTERED_IN_FMS_CHECK));
				}
		}
		//[End] R8.8.0 QC9400 
		// End || Changes done for release 3.12.0 for enhancement 6601
		return loHashMapErrorCheck;
	}

	/**
	 * <p>
	 * This method checks Error rules for launching Amend Contract workflow
	 * <ul>
	 * <li>It sets the error message:</li>
	 * <ul>
	 * <li>if the Amendment Amount is less than zero dollars and the Amendment
	 * Amount entered would cause the ‘New Total Contract Amount’ to fall below
	 * the Cash Balance for the Contract,</li>
	 * <li>if the Amendment Amount is less than zero dollars and <Configuration
	 * Update Task Status> = “In Review” OR <Budget Update Status> = “Pending
	 * Submission” or “Pending Approval” or “Return for Revision”</li>
	 * <li>if the Amendment Amount is less than zero dollars and <Budget
	 * Modification Status> = “Pending Submission” or “Pending Approval” or
	 * “Return for Revision”</li>
	 * <li>if the Amendment Amount is less than zero dollars and <New FY
	 * Configuration Task Status> = “In Review” OR <Budget Status> of the New FY
	 * budget = “Pending Submission” or “Pending Approval” or “Return for
	 * Revision”</li>
	 * <li>if the Amendment Amount is less than zero dollars and <Invoice
	 * Status> for any invoice associated with that contract = “Pending
	 * Submission”, “Pending Approval”, or “Returned for Revision” OR if
	 * <Payment Status> for any payments associated with that contract does not
	 * equal “Disbursed”</li> *
	 * <li>Else set no error message</li>
	 * </ul>
	 * This method is updated in R4.
	 * </p>
	 * 
	 * @param aoMybatisSession SQL Session
	 * @param aoUserSession P8UserSession Object
	 * @param asContractAmount Contract Amount
	 * @param asContractAmount Amendment Amount
	 * @param asContractId Contract Id
	 * @return HashMap hashmap with set error message.
	 * @throws ApplicationException Exception in case of code failure
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public HashMap validateContractAmendmentBusinessRules(SqlSession aoMybatisSession, P8UserSession aoUserSession,
			String asContractAmount, String asAmendmentAmount, String asContractId) throws ApplicationException
	{
		HashMap loHMError = new HashMap();
		// Default Error Code for success
		loHMError.put(HHSConstants.ERROR_CODE, HHSConstants.STRING_ZERO);
		try
		{
			BigDecimal loContractAmount = new BigDecimal(HHSUtil.formatAmount(asContractAmount));
			BigDecimal loAmendmentAmount = new BigDecimal(HHSUtil.formatAmount(asAmendmentAmount));
			// r4 start
			BigDecimal loNewTotalContractAmount = loContractAmount.add(loAmendmentAmount);
			// if negative amendment, then subtract all negative amendment
			// amount
			if (loAmendmentAmount.compareTo(BigDecimal.ZERO) < HHSConstants.INT_ZERO)
			{
				BigDecimal loTotalNegativeAmendmentsAmt = new BigDecimal(fetchAllNegativeAmendmentAmounts(
						aoMybatisSession, asContractId));
				loNewTotalContractAmount = loNewTotalContractAmount.add(loTotalNegativeAmendmentsAmt);
			}
			// r4 end
			Channel loChannel = new Channel();
			// below scenario is for negative amendment
			if (loContractAmount.compareTo(loNewTotalContractAmount) > HHSConstants.INT_ZERO)
			{
				HashMap loHMReqdProp = new HashMap();
				String lsContractDisbursedAmount = setPaymentChannelData(aoMybatisSession, asContractId,
						loAmendmentAmount, loHMReqdProp);
				if (null == lsContractDisbursedAmount)
				{
					lsContractDisbursedAmount = HHSConstants.DECIMAL_ZERO;
				}

				// Check if the New Total is still acceptable against the Rule
				// that it must be less than the Disbursed Amount
				if (loNewTotalContractAmount.compareTo(new BigDecimal(lsContractDisbursedAmount)) < HHSConstants.INT_ZERO)
				{
					loHMError.put(HHSConstants.ERROR_CODE, HHSConstants.ONE);
					loHMError.put(HHSConstants.CLC_ERROR_MSG, PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.AMEND_NEGATIVE_CONTRACT_FAILURE01));
					return loHMError;
				}
				setBudgetChannelData(aoMybatisSession, aoUserSession, asContractId, loAmendmentAmount, loChannel,
						loHMReqdProp);
				if ((Boolean) Rule.evaluateRule(HHSConstants.AMEND_CONTRACT_ERROR_RULE_SET01, loChannel, true))
				{
					loHMError.put(HHSConstants.ERROR_CODE, HHSConstants.TWO);
					loHMError.put(HHSConstants.CLC_ERROR_MSG, PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.AMEND_CONTRACT_FAILURE02));
					return loHMError;
				}
				setBudgetModData(aoMybatisSession, loChannel, loHMReqdProp);
				if ((Boolean) Rule.evaluateRule(HHSConstants.AMEND_CONTRACT_ERROR_RULE_SET02, loChannel, true))
				{
					loHMError.put(HHSConstants.ERROR_CODE, HHSConstants.TWO);
					loHMError.put(HHSConstants.CLC_ERROR_MSG, PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.AMEND_CONTRACT_FAILURE03));
					return loHMError;
				}
				setFiscalYearData(aoMybatisSession, aoUserSession, asContractId, loChannel, loHMReqdProp);
				if ((Boolean) Rule.evaluateRule(HHSConstants.AMEND_CONTRACT_ERROR_RULE_SET03, loChannel, true))
				{
					loHMError.put(HHSConstants.ERROR_CODE, HHSConstants.TWO);
					loHMError.put(HHSConstants.CLC_ERROR_MSG, PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.AMEND_CONTRACT_FAILURE04));
					return loHMError;
				}
				List<String> loContractInvoiceStatus = (List<String>) DAOUtil.masterDAO(aoMybatisSession, loHMReqdProp,
						HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_CONTRACT_INVOICE_STATUS,
						HHSConstants.JAVA_UTIL_HASH_MAP);
				List<String> loContractPaymentStatus = (List<String>) DAOUtil.masterDAO(aoMybatisSession, loHMReqdProp,
						HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_CONTRACT_PAYMENT_STATUS,
						HHSConstants.JAVA_UTIL_HASH_MAP);

				if (paymentErrorCheck(loContractInvoiceStatus, loContractPaymentStatus))
				{
					loHMError.put(HHSConstants.CLC_ERROR_MSG, PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.AMEND_CONTRACT_FAILURE05));
					loHMError.put(HHSConstants.ERROR_CODE, HHSConstants.TWO);
					return loHMError;
				}
			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Application Exception Occured in validateContractAmendmentBusinessRules()", loAppEx);
			setMoState("Application Exception Occured in validateContractAmendmentBusinessRules() \n");
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Application Exception Occured in validateContractAmendmentBusinessRules()", loEx);
			setMoState("Application Exception Occured in validateContractAmendmentBusinessRules() \n");
			throw new ApplicationException(
					"Application Exception Occured in validateContractAmendmentBusinessRules() - ", loEx);
		}
		return loHMError;
	}

	/**
	 * This method check if there are any outstanding payments.
	 * @param loContractInvoiceStatus List of contract invoice status
	 * @param loContractPaymentStatus list of contract payment status
	 * @return boolean true if success
	 * @throws ApplicationException Exception in case of code failure
	 */
	private boolean paymentErrorCheck(List<String> loContractInvoiceStatus, List<String> loContractPaymentStatus)
			throws ApplicationException
	{
		return (null != loContractPaymentStatus && (loContractPaymentStatus.contains(PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PAYMENT_APPROVED))
				|| loContractPaymentStatus.contains(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_PAYMENT_PENDING_APPROVAL))
				|| loContractPaymentStatus.contains(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_PAYMENT_SUSPENDED)) || loContractPaymentStatus.contains(PropertyLoader
				.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PAYMENT_PENDING_FMS_ACTION))))
				|| (null != loContractInvoiceStatus && (loContractInvoiceStatus.contains(PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_INVOICE_PENDING_SUBMISSION))
						|| loContractInvoiceStatus.contains(PropertyLoader.getProperty(
								HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_INVOICE_PENDING_APPROVAL)) || loContractInvoiceStatus
							.contains(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
									HHSConstants.STATUS_INVOICE_RETURNED_FOR_REVISION))));
	}

	/**
	 * This method set payment related data.
	 * @param aoMybatisSession sql session as input.
	 * @param asContractId contract id as input.
	 * @param loAmendmentAmount amendment amount as input.
	 * @param loHMReqdProp map as input.
	 * @return lsContractDisbursedAmount contract disbursement status
	 * @throws ApplicationException Exception in case of code failure.
	 */
	@SuppressWarnings("unchecked")
	private String setPaymentChannelData(SqlSession aoMybatisSession, String asContractId,
			BigDecimal loAmendmentAmount, @SuppressWarnings("rawtypes") HashMap loHMReqdProp)
			throws ApplicationException
	{
		loHMReqdProp.put(HHSConstants.CONTRACT_ID_WORKFLOW, asContractId);
		loHMReqdProp.put(HHSConstants.AMENDMENT_VALUE, loAmendmentAmount);
		loHMReqdProp.put(HHSConstants.CONTRACT_TYPE_ID, HHSConstants.FOUR);
		String lsPaymentDisbursedStatusId = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_PAYMENT_DISBURSED);
		loHMReqdProp.put(HHSConstants.PAYMENT_STATUS_ID, lsPaymentDisbursedStatusId);

		String lsContractDisbursedAmount = (String) DAOUtil.masterDAO(aoMybatisSession, loHMReqdProp,
				HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_CONTRACT_DISBURSED_AMOUNT,
				HHSConstants.JAVA_UTIL_HASH_MAP);
		return lsContractDisbursedAmount;
	}

	/**
	 * @param aoMybatisSession sql session as input.
	 * @param aoUserSession file net session as input.
	 * @param asContractId contract id as input.
	 * @param loChannel channel as input.
	 * @param loHMReqdProp map as input.
	 * @throws ApplicationException Exception in case of code failure.
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private void setFiscalYearData(SqlSession aoMybatisSession, P8UserSession aoUserSession, String asContractId,
			Channel loChannel, HashMap loHMReqdProp) throws ApplicationException
	{
		loChannel.setData(HHSConstants.FY_CONF_TASK_STATUS,
				fetchTaskStatus(aoUserSession, asContractId, HHSConstants.TASK_NEW_FY_CONFIGURATION));

		loHMReqdProp.put(HHSConstants.BUDGET_TYPE_ID, HHSConstants.TWO);
		String lsNewFYBudgetStatus = (String) DAOUtil.masterDAO(aoMybatisSession, loHMReqdProp,
				HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_CONTRACT_BUDGET_STATUS,
				HHSConstants.JAVA_UTIL_HASH_MAP);
		loChannel.setData(HHSConstants.BUDGET_NEW_FY_STATUS, null == lsNewFYBudgetStatus ? HHSConstants.EMPTY_STRING
				: lsNewFYBudgetStatus);
	}

	/**
	 * This method set budget modification data.
	 * <ul>
	 * <li>calls query 'fetchContractBudgetStatus'</li>
	 * </ul>
	 * @param aoMybatisSession sql session as input.
	 * @param loChannel channel as input.
	 * @param loHMReqdProp map as input.
	 * @throws ApplicationException Exception in case of code failure.
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private void setBudgetModData(SqlSession aoMybatisSession, Channel loChannel, HashMap loHMReqdProp)
			throws ApplicationException
	{
		loHMReqdProp.put(HHSConstants.BUDGET_TYPE_ID, HHSConstants.THREE);
		String lsBudgetModificationStatus = (String) DAOUtil.masterDAO(aoMybatisSession, loHMReqdProp,
				HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_CONTRACT_BUDGET_STATUS,
				HHSConstants.JAVA_UTIL_HASH_MAP);
		loChannel.setData(HHSConstants.BUDGET_MODIFICATION_STATUS,
				null == lsBudgetModificationStatus ? HHSConstants.EMPTY_STRING : lsBudgetModificationStatus);
	}

	/**
	 * This method set budget data in channel.
	 * @param aoMybatisSession sql session as input.
	 * @param aoUserSession Filenet session.
	 * @param asContractId contract id input.
	 * @param loAmendmentAmount amendment amount as input.
	 * @param loChannel channel as input.
	 * @param loHMReqdProp map as input.
	 * @throws ApplicationException Exception in case of code failure.
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private void setBudgetChannelData(SqlSession aoMybatisSession, P8UserSession aoUserSession, String asContractId,
			BigDecimal loAmendmentAmount, Channel loChannel, HashMap loHMReqdProp) throws ApplicationException
	{
		loHMReqdProp.put(HHSConstants.CONTRACT_TYPE_ID, HHSConstants.TWO);
		loHMReqdProp.put(HHSConstants.BUDGET_TYPE_ID, HHSConstants.FOUR);
		String lsBudgetUpdateStatus = (String) DAOUtil.masterDAO(aoMybatisSession, loHMReqdProp,
				HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_CONTRACT_BUDGET_UPDATE_STATUS,
				HHSConstants.JAVA_UTIL_HASH_MAP);
		loChannel.setData(HHSConstants.AMENDMENT_VALUE, loAmendmentAmount);

		loChannel.setData(HHSConstants.CONF_UPDATE_TASK_STATUS,
				fetchTaskStatus(aoUserSession, asContractId, HHSConstants.TASK_CONTRACT_UPDATE));
		loChannel.setData(HHSConstants.BUDGET_UPDATE_STATUS, null == lsBudgetUpdateStatus ? HHSConstants.EMPTY_STRING
				: lsBudgetUpdateStatus);
	}

	/**
	 * This method fetch all negative amendment amounts corresponding to a
	 * contract id
	 * 
	 * <ul>
	 * <li>fetch sum of all negative amendment amount for a particular contract</li>
	 * <li>calls query 'fetchAllNegativeAmendmentAmounts'</li>
	 * <li>This method was added in R4</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession object
	 * @param asContractId string containing ContractId
	 */
	private String fetchAllNegativeAmendmentAmounts(SqlSession aoMyBatisSession, String asContractId)
			throws ApplicationException
	{
		String lsAllNegativeAmendAmount = HHSConstants.STRING_ZERO;
		try
		{
			lsAllNegativeAmendAmount = (String) DAOUtil.masterDAO(aoMyBatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_ALL_NEGATIVE_AMEND_AMOUNTS,
					HHSConstants.JAVA_LANG_STRING);
			setMoState(" FinancialsListService: fetchAllNegativeAmendmentAmounts method successful");
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("contractID", asContractId);
			LOG_OBJECT.Error("App Exception occured in FinancialsListService: fetchAllNegativeAmendmentAmounts:: ",
					loAppEx);
			setMoState("Transaction Failed:: FinancialsListService: fetchAllNegativeAmendmentAmounts method - failed. App Exception "
					+ "occured fetching all negative amendment amounts for a contract. \n");
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "FinancialsListService: fetchAllNegativeAmendmentAmounts:: ", loEx);
			loAppEx.addContextData("contractID", asContractId);
			LOG_OBJECT
					.Error("Exception occured in FinancialsListService: fetchAllNegativeAmendmentAmounts:: ", loAppEx);
			setMoState("Transaction Failed:: FinancialsListService: fetchAllNegativeAmendmentAmounts method - failed. Exception "
					+ "occured fetching all negative amendment amounts for a contract. \n");
			throw loAppEx;
		}
		return lsAllNegativeAmendAmount;
	}

	/**
	 * This method set all required statuses in channel to evaluate Rule for
	 * Amend Contract (called by validateContractAmendmentBusinessRules())
	 * 
	 * <ul>
	 * <li>Check for null conditions</li>
	 * <li>Set all data in channel object</li>
	 * </ul>
	 * 
	 * @param aoChannel
	 * @param asConfUpdateTaskStatus
	 * @param asBudgetUpdateStatus
	 * @param asBudgetModificationStatus
	 * @param asFYConfTaskStatus
	 */
	@SuppressWarnings("unused")
	private void setRuleDataInChannelForAmendContract(Channel aoChannel, String asConfUpdateTaskStatus,
			String asBudgetUpdateStatus, String asBudgetModificationStatus, String asFYConfTaskStatus,
			String asContractAmmendmentAmmount)
	{
		if (null == asConfUpdateTaskStatus)
		{
			asConfUpdateTaskStatus = HHSConstants.EMPTY_STRING;
		}
		if (null == asBudgetUpdateStatus)
		{
			asBudgetUpdateStatus = HHSConstants.EMPTY_STRING;
		}
		if (null == asBudgetModificationStatus)
		{
			asBudgetModificationStatus = HHSConstants.EMPTY_STRING;
		}

		if (null == asFYConfTaskStatus)
		{
			asFYConfTaskStatus = HHSConstants.EMPTY_STRING;
		}

		if (null == asContractAmmendmentAmmount)
		{
			asContractAmmendmentAmmount = HHSConstants.STRING_ZERO;
		}
		aoChannel.setData(HHSConstants.CONF_UPDATE_TASK_STATUS, asConfUpdateTaskStatus);
		aoChannel.setData(HHSConstants.BUDGET_UPDATE_STATUS, asBudgetUpdateStatus);
		aoChannel.setData(HHSConstants.BUDGET_MODIFICATION_STATUS, asBudgetModificationStatus);
		aoChannel.setData(HHSConstants.AMENDMENT_VALUE, asContractAmmendmentAmmount);
		aoChannel.setData(HHSConstants.FY_CONF_TASK_STATUS, asFYConfTaskStatus);

	}

	/**
	 * This method Evaluate Rule define business-rule.xml file for Amend
	 * Contract
	 * 
	 * <ul>
	 * <li>Evaluate Rule 'AmendContractErrorRuleSet01'</li>
	 * <li>For error return error message else set success in hashmap object</li>
	 * </ul>
	 * 
	 * @param aoChannel Channel object
	 * @param abErrorCheck04 Error check Rule Id
	 * @return loHMError - a HashMap object containg the result status -
	 *         success/error and the message
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unused", "unchecked" })
	private HashMap callAmendContractBusinessRule(Channel aoChannel, boolean abErrorCheck) throws ApplicationException
	{
		HashMap loHMError = new HashMap();
		String lsRuleReturnValue;
		lsRuleReturnValue = (String) Rule.evaluateRule(HHSConstants.AMEND_CONTRACT_ERROR_RULE_SET01, aoChannel);
		if (lsRuleReturnValue.equalsIgnoreCase(HHSConstants.FALSE))
		{
			if (abErrorCheck)
			{
				loHMError.put(HHSConstants.CLC_ERROR_MSG, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.AMEND_CONTRACT_FAILURE05));
				loHMError.put(HHSConstants.ERROR_CODE, HHSConstants.TWO);
				return loHMError;
			}
		}
		else
		{
			loHMError.put(HHSConstants.CLC_ERROR_MSG, lsRuleReturnValue);
			loHMError.put(HHSConstants.ERROR_CODE, HHSConstants.TWO);
			return loHMError;
		}
		loHMError.put(HHSConstants.ERROR_CODE, HHSConstants.STRING_ZERO);
		return loHMError;
	}

	/**
	 * This method fetches contract title and organization id for a contract
	 * Added for enhancement 6000 for Release 3.8.0
	 * <ul>
	 * <li>calls query 'fetchContractTitleAndOrgID'</li>
	 * </ul>
	 * 
	 * @param asContractId contractId
	 * @return loContractDetails HashMap
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public HashMap<String, String> fetchContractTitleAndOrgID(SqlSession aoMybatisSession, String asContractId)
			throws ApplicationException
	{
		HashMap<String, String> loContractDetails = new HashMap<String, String>();
		LOG_OBJECT.Debug("Entered into fetchContractTitleAndOrgID:: " + asContractId);
		try
		{
			loContractDetails = (HashMap<String, String>) DAOUtil.masterDAO(aoMybatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_CONTRACT_TITLE_AND_ORGID,
					HHSConstants.JAVA_LANG_STRING);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT
					.Error("Exception occured for fetching Contract Details in FinancialsListService: fetchContractTitleAndOrgID ",
							loAppEx);
			setMoState("Transaction Failed:: FinancialsListService: fetchContractTitleAndOrgID method - failed to fetch"
					+ asContractId + " \n");
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			// Handle the exception of type ApplicationException
			LOG_OBJECT
					.Error("Exception occured for fetching Contract details in FinancialsListService: fetchContractTitleAndOrgID ",
							loAppEx);
			setMoState("Transaction Failed:: FinancialsListService: fetchContractTitleAndOrgID method - failed to fetch"
					+ asContractId + " \n");
			throw new ApplicationException("Error occured while fetching Contract details", loAppEx);
		}
		return loContractDetails;
	}

	/**
	 * This method fetches budget in 'Pending SUbmission' and 'Pending Approval'
	 * status for a contract Added for enhancement 6000 for Release 3.8.0
	 * <ul>
	 * <li>calls query 'fetchPendingBudget'</li>
	 * </ul>
	 * 
	 * @param asContractId contractId
	 * @return lbPendingBudget boolean
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Boolean fetchPendingBudget(SqlSession aoMybatisSession, String asContractId) throws ApplicationException
	{
		Boolean lbPendingBudget = Boolean.FALSE;
		LOG_OBJECT.Debug("Entered into fetchPendingBudget:: " + asContractId);
		try
		{
			Integer loPendingBudget = (Integer) DAOUtil.masterDAO(aoMybatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_PENDING_BUDGET,
					HHSConstants.JAVA_LANG_STRING);
			if (loPendingBudget > HHSConstants.INT_ZERO)
			{
				lbPendingBudget = Boolean.TRUE;
			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error(
					"Exception occured for fetching Pending Budgets in FinancialsListService: fetchPendingBudget ",
					loAppEx);
			setMoState("Transaction Failed:: FinancialsListService: fetchPendingBudget method - failed to fetch"
					+ asContractId + " \n");
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error(
					"Exception occured for fetching Pending Budgets in FinancialsListService: fetchPendingBudget ",
					loAppEx);
			setMoState("Transaction Failed:: FinancialsListService: fetchPendingBudget method - failed to fetch"
					+ asContractId + " \n");
			throw new ApplicationException("Error occured while fetching Pending Budgets", loAppEx);
		}
		return lbPendingBudget;
	}

	/**
	 * This method Amend a Contract
	 * 
	 * <ul>
	 * <li>All the fields entered by the User is taken from the jsp
	 * <code>ContractListController</code></li>
	 * <li>The values taken as a EPinDetailBean (
	 * <code>aoContractDetailByEpin</code>) from the request are put in the
	 * Database using the <code>FinancialsListService</code></li>
	 * <li>A new contract is inserted in the database</li>
	 * <li>Transaction used : <code>amendContractDetails</code></li>
	 * <li>calls query 'amendContractDetails'</li>
	 * <li>This method was updated in R4</li>
	 * </ul>
	 * 
	 * @param aiCurrentSeq contractId's sequence
	 * @param aoContractDetailByEpin EPinDetailBean
	 * @param aoMybatisSession SqlSession Session
	 * @return aoHMWFRequiredProps HashMap
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public HashMap amendContractDetails(Integer aiCurrentSeq, EPinDetailBean aoContractDetailByEpin,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		// Declare local variables and initialize them with default values
		HashMap aoHMWFRequiredProps = new HashMap();
		Integer loAmendContractCount = HHSConstants.INT_ZERO;

		try
		{
			// Set ParentId of the contract being added as amendment which would
			// be id of Amending Contract
			aoContractDetailByEpin.setParentContractId(aoContractDetailByEpin.getContractId());
			aoContractDetailByEpin.setContractId(String.valueOf(aiCurrentSeq).trim());
			loAmendContractCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoContractDetailByEpin,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.AMEND_CONTRACT_DETAILS,
					HHSConstants.COM_NYC_HHS_MODEL_E_PIN_DETAIL_BEAN);

			addFYsInContractFinancialsTable(aiCurrentSeq, aoContractDetailByEpin, aoMybatisSession);

			// If could not insert new record for Contract Amendment
			if (loAmendContractCount <= HHSConstants.INT_ZERO)
			{
				setMoState("Transaction Failed:: FinancialsListService: amendContractDetails method - inserted the amend contract Details for "
						+ aoContractDetailByEpin.getEpinId() + " \n");
				throw new ApplicationException(
						"Exception occured for inserting Renewing Contract Details in FinancialsListService");
			}
			// If successfully inserted new record for Contract Amendment
			else
			{
				setMoState("Transaction Success:: FinancialsListService: amendContractDetails method - inserted the amend contract Details for "
						+ aoContractDetailByEpin.getEpinId() + " \n");
				// Set the required parameters in input set for following
				// File-net action
				aoHMWFRequiredProps.put(HHSConstants.CONTRACT_ID_WORKFLOW, aoContractDetailByEpin.getContractId());
				aoHMWFRequiredProps.put(HHSConstants.SUBMITTED_BY, aoContractDetailByEpin.getCreateByUserId());
				aoHMWFRequiredProps.put(HHSConstants.WORKFLOW_NAME, HHSConstants.WF_AMENDMENT_CONFIGURATION);
			}
		}
		catch (ApplicationException loAppEx)
		{
			// Handle the exception of type ApplicationException
			loAppEx.addContextData(HHSConstants.AO_CONTRACT_DETAILS_BY_EPIN,
					CommonUtil.convertBeanToString(aoContractDetailByEpin));
			LOG_OBJECT.Error("Exception occured for inserting Amend Contract Details in FinancialsListService ",
					loAppEx);
			setMoState("Transaction Failed:: FinancialsListService: amendContractDetails method - failed to validate"
					+ aoContractDetailByEpin.getEpinId() + " \n");
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			// Handle the exception of type ApplicationException
			LOG_OBJECT
					.Error("Exception occured for inserting Amend Contract details from amendContractDetails in FinancialsListService ",
							loAppEx);
			setMoState("Transaction Failed:: FinancialsListService: amendContractDetails method - failed to validate"
					+ aoContractDetailByEpin.getEpinId() + " \n");
			throw new ApplicationException("Error occured while inserting the details for amend contract", loAppEx);
		}

		return aoHMWFRequiredProps;
	}

	/**
	 * This method adds the Fiscal Years in to Contract_Financials table in DB
	 * 
	 * <ul>
	 * <li>Gets rows for a Existing First Fiscal Year entries of the COntract</li>
	 * <li>Replicates the fetched row again against newly created contract for
	 * Amendment</li>
	 * <li>This method was updated in R4</li>
	 * </ul>
	 * 
	 * @param aoAmendmentContractId - Integer type - contractId (created for
	 *            Amendment)
	 * @param aoContractDetailByEpin EPinDetailBean Bean
	 * @param aoMybatisSession SqlSession Session
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private void addFYsInContractFinancialsTable(Integer aoAmendmentContractId, EPinDetailBean aoContractDetailByEpin,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		HashMap loHMArgsFetchContFinancials = new HashMap();

		// fetch contract start date, proposed contract end date and contract
		// end date
		Date loContractStartDate = DateUtil.getSqlDate(aoContractDetailByEpin.getContractStart());
		Date loContractEndDate = DateUtil.getSqlDate(aoContractDetailByEpin.getProposedContractEnd());
		Date loOriginalContractEndDate = DateUtil.getSqlDate(aoContractDetailByEpin.getContractEnd());

		// Get FYs based on contract start date and proposed contract end date
		HashMap<String, Integer> loContractFYDetails = HHSUtil.getFirstAndLastFYOfContract(loContractStartDate,
				loContractEndDate);
		Integer loContractStartFiscalYear = loContractFYDetails.get(HHSConstants.START_FISCAL_YEAR);
		Integer loContractEndFiscalYear = loContractFYDetails.get(HHSConstants.CONTRACT_END_FY);

		// Get FYs based on contract start date and original contract end date
		HashMap<String, Integer> loOriginalContractFYDetails = HHSUtil.getFirstAndLastFYOfContract(loContractStartDate,
				loOriginalContractEndDate);
		Integer loOriginalContractEndFiscalYear = loOriginalContractFYDetails.get(HHSConstants.CONTRACT_END_FY);

		// **********start

		// 1. create copy of all entries with 0 value, all means fiscal years
		// which are common in
		// contract start date, contract end date and
		// contract start date, proposed contract end date
		Integer loEndFYCalculated = null;
		if (loContractEndFiscalYear > loOriginalContractEndFiscalYear)
		{
			loEndFYCalculated = loOriginalContractEndFiscalYear;
		}
		else
		{
			loEndFYCalculated = loContractEndFiscalYear;
		}

		loHMArgsFetchContFinancials.put(HHSConstants.CONTRACT_ID, aoContractDetailByEpin.getParentContractId());
		for (Integer liFY = loContractStartFiscalYear; liFY <= loEndFYCalculated; liFY++)
		{
			// fetch all accounting units for a particular year
			loHMArgsFetchContFinancials.put(HHSConstants.FISCAL_YEAR, liFY.toString());
			List<ContractFinancialBean> loContractFinancialBeanList = (List<ContractFinancialBean>) DAOUtil.masterDAO(
					aoMybatisSession, loHMArgsFetchContFinancials, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSConstants.CS_FETCH_CONTRACT_FINANCIAL_DETAILS, HHSConstants.JAVA_UTIL_HASH_MAP);

			// traverse each accounting unit and do entry for that particular
			// year with 0 value
			for (ContractFinancialBean loContractFinancialBean : loContractFinancialBeanList)
			{
				loContractFinancialBean.setFiscalYear(liFY.toString());
				loContractFinancialBean.setContractTypeId(HHSConstants.CONTRACT_AMENDMENT_TYPE_ID);
				// Set newly inserted ContractId
				loContractFinancialBean.setContractId(aoAmendmentContractId.toString());
				loContractFinancialBean.setAmmount(HHSConstants.STRING_ZERO);

				DAOUtil.masterDAO(aoMybatisSession, loContractFinancialBean, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
						HHSConstants.CS_INSERT_FETCH_CONTRACT_FINANCIAL_DETAILS,
						HHSConstants.CS_CONTRACT_FINANCIAL_BEAN);
			}
		}

		// do new entries for extra fiscal years added due to proposed contract
		// end date
		if (loContractEndFiscalYear > loOriginalContractEndFiscalYear)
		{

			// fetch list of accounting units for any year ( say start fiscal
			// year)
			loHMArgsFetchContFinancials.put(HHSConstants.FISCAL_YEAR, loContractStartFiscalYear.toString());
			List<ContractFinancialBean> loContractFinancialBeanList = (List<ContractFinancialBean>) DAOUtil.masterDAO(
					aoMybatisSession, loHMArgsFetchContFinancials, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSConstants.CS_FETCH_CONTRACT_FINANCIAL_DETAILS, HHSConstants.JAVA_UTIL_HASH_MAP);

			// do insert call for each fiscal year that is newly added, with 0
			// value
			for (ContractFinancialBean loContractFinancialBean : loContractFinancialBeanList)
			{

				loContractFinancialBean.setContractTypeId(HHSConstants.CONTRACT_AMENDMENT_TYPE_ID);
				loContractFinancialBean.setContractId(aoAmendmentContractId.toString());
				loContractFinancialBean.setAmmount(HHSConstants.STRING_ZERO);
				loContractFinancialBean.setContractFinancialId("");
				for (Integer loCount = (loOriginalContractEndFiscalYear + 1); loCount <= loContractEndFiscalYear; loCount++)
				{
					loContractFinancialBean.setFiscalYear(loCount.toString());
					DAOUtil.masterDAO(aoMybatisSession, loContractFinancialBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
							HHSConstants.CS_INSERT_FETCH_CONTRACT_FINANCIAL_DETAILS,
							HHSConstants.CS_CONTRACT_FINANCIAL_BEAN);
				}
			}

		}
	}

	// 2. insert new entries with 0 value for new fiscal years which are added
	// as part of extension in
	// contract end date means proposed contract end date

	// ***********end

	/**
	 * This method checks Error rules before cancelling a contract
	 * <ul>
	 * <li>For Accelerator user if the <Contract Type> = “Original” or
	 * “Renewals” and Contract Source = APT and <Contract Status> = “Pending
	 * Configuration” or “Pending CoF” or “Pending Registration”, then allow
	 * user to cancel the contract else show error message</li>
	 * <li>For Agency user if the <Contract Type> = “Renewals” and Contract
	 * Source = APT and < Contract Status> = “Pending Configuration” or “Pending
	 * CoF” or “Pending Registration”,then allow user to cancel the contract
	 * else show error message</li>
	 * <li>calls query 'fetchCancelContractDetails'</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SQL Session
	 *            <ul>
	 *            </ul>
	 * @param asContractId Contract Id
	 * @param asUserType User Type
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public Boolean cancelContractErrorCheckRule(SqlSession aoMybatisSession, String asContractId, String asOrgType)
			throws ApplicationException
	{

		Channel loChannel;
		String lsRuleReturnValue = HHSConstants.EMPTY_STRING;
		boolean lbErrorCheckRule = Boolean.FALSE;
		try
		{
			HashMap<Object, Object> loVal = (HashMap<Object, Object>) DAOUtil.masterDAO(aoMybatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_CANCEL_CONTRACT_DETAILS,
					HHSConstants.JAVA_LANG_STRING);

			if (loVal == null)
			{
				throw new ApplicationException(
						"Exception occured in cancelContract method while executing the fetchCancelContractDetails query");
			}

			loChannel = new Channel();
			loChannel.setData(HHSConstants.CONTRACT_SOURCE_TYPE, loVal.get(HHSConstants.CONTRACT_SOURCE_ID).toString());
			loChannel.setData(HHSConstants.FLS_CONTRACT_STATUS, loVal.get(HHSConstants.STATUS).toString());
			loChannel.setData(HHSConstants.CONTRACT_TYPE, loVal.get(HHSConstants.FLS_CONTRACT_TYPE_ID).toString());

			if (asOrgType != null && (asOrgType.equalsIgnoreCase(ApplicationConstants.CITY_ORG)))
			{
				lsRuleReturnValue = (String) Rule.evaluateRule(HHSConstants.CANCEL_CONTRACT_RULE_FOR_ACCELERATOR,
						loChannel);
			}
			else if (asOrgType != null && (asOrgType.equalsIgnoreCase(ApplicationConstants.AGENCY_ORG)))
			{
				lsRuleReturnValue = (String) Rule.evaluateRule(HHSConstants.CANCEL_CONTRACT_RULE_FOR_AGENCY, loChannel);
			}

			lbErrorCheckRule = lsRuleReturnValue.equalsIgnoreCase(HHSConstants.TRUE) ? Boolean.TRUE : Boolean.FALSE;
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("ApplicationException occured while validating business rule before cancelling contract",
					loAppEx);
			setMoState("Transaction Failed:: FinancialsListService:fetchCancelContractDetails method -Exception occured while validating business rule before cancelling contract");
			throw loAppEx;

		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Exception occured while validating business rule before cancelling contract", loEx);
			setMoState("Transaction Failed:: FinancialsListService:fetchCancelContractDetails method -Exception occured while validating business rule before cancelling contract");
			throw new ApplicationException("Error occured while validating business rule before cancelling contract",
					loEx);
		}
		return lbErrorCheckRule;
	}

	/**
	 * Added For Enhancement 6000 for Release 3.8.0 This method checks Error
	 * rules before deleting a contract
	 * <ul>
	 * <li>For Accelerator user if the budget is Approved or Active, then allow
	 * user to delete the contract else show error message</li>
	 * <li>calls query 'fetchApproveActiveBudget'</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SQL Session
	 * @param asContractId Contract Id
	 * @return lbErrorCheckRule
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public Boolean deleteContractErrorCheckRule(SqlSession aoMybatisSession, String asContractId)
			throws ApplicationException
	{
		Integer loApprovedActiveBudg = HHSConstants.INT_ZERO;
		boolean lbErrorCheckRule = Boolean.TRUE;
		LOG_OBJECT.Debug("Entered into deleteContractErrorCheckRule:: " + asContractId);
		try
		{
			loApprovedActiveBudg = (Integer) DAOUtil.masterDAO(aoMybatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_APPROVE_ACTIVE_BUDGET,
					HHSConstants.JAVA_LANG_STRING);
			if (loApprovedActiveBudg > HHSConstants.INT_ZERO)
			{
				lbErrorCheckRule = Boolean.FALSE;
			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("ApplicationException occured while validating business rule before deleting contract",
					loAppEx);
			setMoState("Transaction Failed:: FinancialsListService:deleteContractErrorCheckRule method -Exception occured while validating business rule before deleting contract");
			throw loAppEx;

		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Exception occured while validating business rule before deleting contract", loEx);
			setMoState("Transaction Failed:: FinancialsListService:deleteContractErrorCheckRule method -Exception occured while validating business rule before deleting contract");
			throw new ApplicationException("Error occured while validating business rule before deleting contract",
					loEx);
		}
		return lbErrorCheckRule;
	}

	/**
	 * This method is used to update status in Database
	 * @param aoMybatisSession a SqlSession object
	 * @param queryId the required query
	 * @param loHMReqProp hashmap for required properties
	 * @return liNoOfRowUpdated Number of rows updated
	 * @throws ApplicationException
	 */
	private Integer updateStatusInDB(SqlSession aoMybatisSession, String queryId, HashMap<String, String> loHMReqProp)
			throws ApplicationException
	{
		Integer liNoOfRowUpdated;
		liNoOfRowUpdated = (Integer) DAOUtil.masterDAO(aoMybatisSession, loHMReqProp,
				HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, queryId, HHSConstants.JAVA_UTIL_HASH_MAP);
		if (liNoOfRowUpdated <= HHSConstants.INT_ZERO)
		{
			throw new ApplicationException("Exception occured in cancelContract method while executing the " + queryId);
		}
		return liNoOfRowUpdated;
	}

	/**
	 * The Method will fetch count of budgets with type as Contract Budget for a
	 * particular Contract
	 * <ul>
	 * <li>delete Contract Configuration Task .</li>
	 * <li>delete Contract CoF Task .</li>
	 * <li>delete Contract Budget Review Task .</li>
	 * <li>calls query 'fetchBudgetCount'</li>
	 * </ul>
	 * 
	 * @param aoUserSession
	 * @param aoWorkFlowIdList List of WorkflowIds
	 * @return lbTerminationFlag Termination Flag
	 * @throws ApplicationException
	 */

	private Integer fetchBudgetCountForContract(SqlSession aoMybatisSession, HashMap<String, String> loHMReqProp)
			throws ApplicationException
	{
		Integer liBudgetCount;
		liBudgetCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loHMReqProp,
				HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_BUDGET_COUNT,
				HHSConstants.JAVA_UTIL_HASH_MAP);
		return liBudgetCount;
	}

	/**
	 * The Method will terminate all workFlows based on workFlow id passed in
	 * the List in case of Cancel Contract
	 * <ul>
	 * <li>delete Contract Configuration Task .</li>
	 * <li>delete Contract CoF Task .</li>
	 * <li>delete Contract Budget Review Task .</li>
	 * 
	 * </ul>
	 * 
	 * @param aoUserSession
	 * @param aoWorkFlowIdList List of WorkflowIds
	 * @return lbTerminationFlag Termination Flag
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Boolean terminateCancelContractWorkFlows(P8UserSession aoUserSession, Boolean aoStatusFlag,
			String asContractId) throws ApplicationException
	{
		Boolean lbTerminationFlag = Boolean.FALSE;

		if (aoStatusFlag)
		{
			TaskService loTaskService = new TaskService();
			HashMap loWorkflowProperties = new HashMap();
			loWorkflowProperties.put(HHSConstants.PROPERTY_PE_CONTRACT_ID, asContractId);
			lbTerminationFlag = loTaskService.closeAllOpenTask(aoUserSession, loWorkflowProperties);

		}
		return lbTerminationFlag;
	}

	/**
	 * Added For Enhancement 6000 for Release 3.8.0 The Method will terminate
	 * all workFlows based on workFlow id passed in the List in case of Delete
	 * Contract
	 * <ul>
	 * <li>delete Contract Configuration Task .</li>
	 * <li>delete Contract CoF Task .</li>
	 * <li>delete Contract Budget Review Task .</li>
	 * </ul>
	 * 
	 * @param aoUserSession
	 * @param aoWorkFlowIdList List of WorkflowIds
	 * @return lbTerminationFlag Termination Flag
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Boolean terminateDeleteContractWorkFlows(P8UserSession aoUserSession, Boolean aoStatusFlag,
			String asContractId) throws ApplicationException
	{
		Boolean lbTerminationFlag = Boolean.FALSE;
		LOG_OBJECT.Debug("Entered into terminateDeleteContractWorkFlows:: " + asContractId + "     aoStatusFlag:" + aoStatusFlag
				+ "       lbTerminationFlag:" + lbTerminationFlag);
		if (aoStatusFlag && asContractId != null && !asContractId.isEmpty())
		{
			TaskService loTaskService = new TaskService();
			HashMap loWorkflowProperties = new HashMap();
			loWorkflowProperties.put(HHSConstants.PROPERTY_PE_CONTRACT_ID, asContractId);
			lbTerminationFlag = loTaskService.closeAllOpenTask(aoUserSession, loWorkflowProperties);
            LOG_OBJECT.Debug("[Tracce 2.1]Entered into terminateDeleteContractWorkFlows::lbTerminationFlag=" + lbTerminationFlag);

		}
		
		return lbTerminationFlag;
	}

	/**
	 * Added new method for Release 3.8.0 Enhancement 6481
	 * @param aoUserSession Filnet Session as input
	 * @param aoStatusFlag status flag as input
	 * @param asContractId Contract id as input
	 * @return lbTerminationFlag terminate flag as output
	 * @throws ApplicationException Exception in case a query fails
	 */
	public Boolean terminateDeleteContractUpdateWorkFlowsBatch(P8UserSession aoUserSession, Boolean aoStatusFlag,
			String asContractId) throws ApplicationException
	{
		Boolean lbTerminationFlag = Boolean.FALSE;
		LOG_OBJECT.Debug("Entered into terminateDeleteContractWorkFlows:: " + asContractId);
		WorkflowOperations loWorkflowOperations = new WorkflowOperations(LOG_OBJECT);
		// Prepare the filter to get the
		String lsFilter = HHSP8Constants.CONTRACT_ID + "='" + asContractId + "'";
		String[] loWorkflowNames =
		{ HHSConstants.WF_CONTRACT_CONFIGURATION_UPDATE, HHSConstants.WF_CONTRACT_BUDGET_UPDATE_REVIEW };
		try
		{
			if (aoStatusFlag && asContractId != null && !asContractId.isEmpty())
			{
				P8SecurityOperations loFilenetConnection = new P8SecurityOperations();
				VWSession moPeSession = loFilenetConnection.getPESession(aoUserSession);
				// Call the method that will cancel the award workflows.
				loWorkflowOperations.cancelWorkflows(lsFilter, loWorkflowNames, moPeSession);
				lbTerminationFlag = true;
			}
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured on executing the terminateDeleteContractUpdateWorkFlowsBatch with filter:"
					+ lsFilter, aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Error occured on executing the terminateDeleteContractUpdateWorkFlowsBatch with filter:"
					+ lsFilter, aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the terminateDeleteContractUpdateWorkFlowsBatch method", aoAppEx);
		}
		return lbTerminationFlag;
	}

	/**
	 * Added For Enhancement 6577 for Release 3.10.0 The Method will terminate
	 * all workFlows based on workFlow id passed in the List in case of Delete
	 * Contract
	 * <ul>
	 * <li>delete Contract Configuration Task .</li>
	 * <li>delete Contract CoF Task .</li>
	 * <li>delete Contract Budget Review Task .</li>
	 * </ul>
	 * 
	 * @param aoUserSession
	 * @param aoWorkFlowIdList List of WorkflowIds
	 * @return lbTerminationFlag Termination Flag
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Boolean terminateCancelCompWorkFlows(P8UserSession aoUserSession, Boolean aoStatusFlag,
			String asEvaluationPoolMappingId/* List<String> asProposalIdList */) throws ApplicationException
	{
		Boolean lbTerminationFlag = Boolean.FALSE;
		LOG_OBJECT.Debug("Entered into terminateCancelCompWorkFlows:: " + asEvaluationPoolMappingId);
		if (asEvaluationPoolMappingId != null && !asEvaluationPoolMappingId.isEmpty())
		{
			TaskService loTaskService = new TaskService();
			HashMap loWorkflowProperties = new HashMap();
			loWorkflowProperties.put(HHSP8Constants.PROPERTY_PE_EVAL_POOL_MAPPING_ID, asEvaluationPoolMappingId);
			lbTerminationFlag = loTaskService.closeAllOpenTask(aoUserSession, loWorkflowProperties);
		}
		return lbTerminationFlag;
	}

	/**
	 * This private method is triggered from fetchContractListSummary. This
	 * method sets setIsNewFYConfigPending property of Contract List bean to
	 * true if the system date is 60 days prior to the end of the current fiscal
	 * year and if the current fiscal year is not the last fiscal year of the
	 * contract. This method is updated in R7 for Defect 8644
	 * <ul>
	 * <li>calls query 'fetchLastFYConfigured'</li>
	 * <li>calls query 'isFYBudgetEntry'</li>
	 * </ul>
	 * 
	 * @param aoContractList
	 * @param aoMybatisSession
	 * @return
	 * @throws ApplicationException
	 */
	private List<ContractList> displayFYconfig(List<ContractList> aoContractList, Date aoCurrentDate,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		Map<String, Object> loQueryMap = new HashMap<String, Object>();
		
		if (aoContractList != null)
		{
			for (ContractList loContract : aoContractList)
			{
				loContract.setIsNewFYConfigPending(false);
				// Start: Updated in R7 for defect 8644: validates to get the
				// contract list
				if (loContract.getContractStatusId().equalsIgnoreCase(HHSConstants.CONTRACT_REGISTERED_STATUS_ID)
						|| (loContract.getIsBudgetApproved() != HHSConstants.ZERO && loContract.getContractStatusId()
								.equalsIgnoreCase(HHSConstants.STATUS_PENDING_REG)))
				// End :R7 changes
				{
					try
					{
						HashMap<String, Integer> loFYDetails = null;
						int liCurrentFiscalYear = HHSConstants.INT_ZERO;
						int liContractEndYear = HHSConstants.INT_ZERO;
						Date loContractStartDate = DateUtil.getSqlDate(loContract.getContractStartDate());
						Date loContractEndDate = DateUtil.getSqlDate(loContract.getContractEndDate());

						loFYDetails = HHSUtil.getFYDetails(loContractStartDate, loContractEndDate, aoCurrentDate);
						liContractEndYear = loFYDetails.get(HHSConstants.CONTRACT_END_FY);
						liCurrentFiscalYear = loFYDetails.get(HHSConstants.CURRENT_FISCAL_YEAR);
						Integer liBudgetEntry = HHSConstants.INT_ZERO;
						Integer liLastConfiguredFY = HHSConstants.INT_ZERO;
											
						loQueryMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, loContract.getContractId());
						Date loCurrentFYEndDate = DateUtil.getSqlDate(HHSConstants.FISCAL_YEAR_END_DATE
								+ liCurrentFiscalYear);
						long liDaysDifference = DateUtil.getDateDifference(aoCurrentDate, loCurrentFYEndDate);								

						liLastConfiguredFY = (Integer) DAOUtil.masterDAO(aoMybatisSession, loContract.getContractId(),
								HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
								HHSConstants.FETCH_LAST_FY_CONFIGURED_AND_NEXT_NEW_FY, HHSConstants.JAVA_LANG_STRING);						
										
						 /*[Start] R8.3.0 QC 9467 */
						//handle the contract end date is less than current FY, up to the Contract End Year
						if(null != liLastConfiguredFY && liContractEndYear < liCurrentFiscalYear ){
							
							for (int liCount = liLastConfiguredFY; liCount <= liContractEndYear ; liCount++){
								loQueryMap.put(HHSConstants.FISCAL_YEAR, liCount);
								liBudgetEntry = (Integer) DAOUtil.masterDAO(aoMybatisSession, loQueryMap,
										HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.IS_FY_BUDGET_ENTRY,
										HHSConstants.JAVA_UTIL_MAP);
								if (liBudgetEntry == HHSConstants.INT_ZERO)
								{
									loContract.setIsNewFYConfigPending(true);
									loContract.setFyConfigFiscalYear(Integer.toString(liCount));
									break; // this is to enable the first one only in the list
								}
							}
							
						}
						// existing logic to handle Last Configured Year to Current Fiscal Year and Contract End Year greater than Current Fiscal Year
												
						else {
							if (null != liLastConfiguredFY && liCurrentFiscalYear <= liContractEndYear
									&& liLastConfiguredFY <= liCurrentFiscalYear)
								{
								for (int liCount = liLastConfiguredFY; liCount <= liCurrentFiscalYear; liCount++)
								{
									loQueryMap.put(HHSConstants.FISCAL_YEAR, liCount);
									liBudgetEntry = (Integer) DAOUtil.masterDAO(aoMybatisSession, loQueryMap,
											HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.IS_FY_BUDGET_ENTRY,
											HHSConstants.JAVA_UTIL_MAP);
									if (liBudgetEntry == HHSConstants.INT_ZERO)
									{
										loContract.setIsNewFYConfigPending(true);
										loContract.setFyConfigFiscalYear(Integer.toString(liCount));
										break;
									}
								}
	
								if (liBudgetEntry > HHSConstants.INT_ZERO && liCurrentFiscalYear != liContractEndYear
										&& liDaysDifference < getNewFYTaskDaysValue(aoMybatisSession))
								{
									liCurrentFiscalYear = liCurrentFiscalYear + HHSConstants.INT_ONE;
									loQueryMap.put(HHSConstants.FISCAL_YEAR, liCurrentFiscalYear);
									liBudgetEntry = (Integer) DAOUtil.masterDAO(aoMybatisSession, loQueryMap,
											HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.IS_FY_BUDGET_ENTRY,
											HHSConstants.JAVA_UTIL_MAP);
									if (liBudgetEntry == HHSConstants.INT_ZERO)
									{
										loContract.setIsNewFYConfigPending(true);
										loContract.setFyConfigFiscalYear(Integer.toString(liCurrentFiscalYear));
									}
								}
								setMoState("displayFYconfig executed successfully " + " \n");
							}
						}//if (null != liLastConfiguredFY && liCurrentFiscalYear <= liContractEndYear && liLastConfiguredFY <= liCurrentFiscalYear)
					}//else
					 /*[End] R8.3.0 QC 9467 */	
					catch (ApplicationException loAppEx)
					{
						LOG_OBJECT.Error("Exception occured while executing displayFYconfig ", loAppEx);
						throw loAppEx;
					}
				}
			}
		}

		return aoContractList;
	}

	/**
	 * This method dispaly certification of funds
	 * <ul>
	 * <li>calls query 'fetchBaseAmendmentContractDetails'</li>
	 * </ul>
	 * @param aoContractList
	 * @param aoMybatisSession
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	private List<ContractList> displayViewCof(List<ContractList> aoContractList, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		if (aoContractList != null)
		{
			for (ContractList loContract : aoContractList)
			{
				try
				{
					List<ContractList> loContractList = null;
					loContractList = (List<ContractList>) DAOUtil.masterDAO(aoMybatisSession,
							loContract.getContractId(), HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
							HHSConstants.FETCH_BASE_AMENDMENT_CONTRACT_DETAILS, HHSConstants.JAVA_LANG_STRING);
					if (!loContractList.isEmpty())
					{
						loContract.setIsViewCOF(true);
					}
				}
				catch (ApplicationException loAppEx)
				{
					LOG_OBJECT.Error("Exception occured while executing isFYBudgetEntry ", loAppEx);
					throw loAppEx;
				}

			}

		}
		return aoContractList;
	}

	/**
	 * This private method is used to fetch number of days at which new fy
	 * configuration task should be initiated
	 * 
	 * This method is updated at Release 8.3.0 to public from private 
	 * <ul>
	 * <li>Else if <New FY Configuration Task Status> for that contract = “In
	 * Review” then set error message</li>
	 * <li>Else set no error message</li>
	 * <li>calls query 'fetchNewFYTaskDaysValue'</li>
	 * </ul>
	 * @param aoMybatisSession
	 * @return Integer number of days
	 * @throws ApplicationException
	 */
	public Integer getNewFYTaskDaysValue(SqlSession aoMybatisSession) throws ApplicationException
	{
		Integer liNumOfDays = (Integer) DAOUtil.masterDAO(aoMybatisSession, null,
				HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_NEW_FY_TASK_DAYS_VALUE, null);
		if (null == liNumOfDays)
		{
			throw new ApplicationException("FinancialListService:: getNewFYTaskDaysValue method. No of days are null.");
		}
		return liNumOfDays;
	}

	/**
	 * This method checks Error rules for launching New FY Configuration
	 * workflow
	 * 
	 * <ul>
	 * <li>if <Configuration Update Task Status> for contract = “In Review” or
	 * “Returned for Revision” then set error message</li>
	 * <li>Else , if there is an Amendment which is in progress for the contract
	 * and the <Amendment Configuration Task Status> or <Amendment Certification
	 * of Funds Task > is open then display error message: “! Cannot initiate
	 * update while an amendment is in progress and the amendment budget has not
	 * been approved.”</li>
	 * <li>Else if <New FY Configuration Task Status> for that contract = “In
	 * Review” then set error message</li>
	 * <li>Else set no error message</li>
	 * <li>calls query 'fetchAllContractId'</li>
	 * </ul>
	 * @param asContractId
	 * @param aoMybatisSession
	 * @param asContractTypeId
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public HashMap newFYConfigErrorCheckRule(String asContractId, SqlSession aoMybatisSession,
			P8UserSession aoUserSession) throws ApplicationException
	{

		Channel loChannel = new Channel();
		HashMap loHMReqdProp = new HashMap();
		String lsConfUpdateTaskStatus = HHSConstants.EMPTY_STRING;
		String lsFYConfTaskStatus = HHSConstants.EMPTY_STRING;
		boolean lbErrorCheck = false;
		int liOpenTaskAmendmentConf = HHSConstants.INT_ZERO;
		int liOpenTaskAmendmentCOF = HHSConstants.INT_ZERO;
		HashMap<String, Object> loQueryMap = new HashMap<String, Object>();
		loQueryMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, asContractId);
		try
		{
			lsConfUpdateTaskStatus = fetchTaskStatus(aoUserSession, asContractId, HHSConstants.TASK_CONTRACT_UPDATE);

			lsFYConfTaskStatus = fetchTaskStatus(aoUserSession, asContractId, HHSConstants.TASK_NEW_FY_CONFIGURATION);
			List<String> loContractId = (List<String>) DAOUtil.masterDAO(aoMybatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_ALL_CONTRACT_ID,
					HHSConstants.JAVA_LANG_STRING);

			if (loContractId != null)
			{
				for (String lsContractId : loContractId)
				{

					loHMReqdProp.put(HHSConstants.PROPERTY_PE_CONTRACT_ID, lsContractId);
					loHMReqdProp.put(HHSConstants.PROPERTY_PE_TASK_TYPE, HHSConstants.TASK_AMENDMENT_CONFIGURATION);
					liOpenTaskAmendmentConf = new P8ProcessServiceForSolicitationFinancials().getOpenTaskCount(
							aoUserSession, loHMReqdProp);
					if (liOpenTaskAmendmentConf > 0)
					{
						break;
					}
				}
				for (String lsContractId : loContractId)
				{

					loHMReqdProp = new HashMap();
					loHMReqdProp.put(HHSConstants.PROPERTY_PE_CONTRACT_ID, lsContractId);
					loHMReqdProp.put(HHSConstants.PROPERTY_PE_TASK_TYPE, HHSConstants.TASK_AMENDMENT_COF);
					liOpenTaskAmendmentCOF = new P8ProcessServiceForSolicitationFinancials().getOpenTaskCount(
							aoUserSession, loHMReqdProp);
					if (liOpenTaskAmendmentCOF > 0)
					{
						break;
					}
				}
			}
			setMoState("Workflow task status fetched succesfully:" + loQueryMap);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while executing fetchContractConfTaskStatus ", loAppEx);
			setMoState("Error occured while fetching Workflow task status:" + loQueryMap);
			throw loAppEx;
		}
		setRuleDataInChannelNewFYConfig(loChannel, lsConfUpdateTaskStatus, lsFYConfTaskStatus, liOpenTaskAmendmentConf,
				liOpenTaskAmendmentCOF);
		return callNewFYConfigBusinessRule(loChannel, lbErrorCheck);
	}

	/**
	 * This method set all required statuses in channel to evaluate Rule for New
	 * FY Configuration
	 * 
	 * <ul>
	 * <li>Check for null conditions</li>
	 * <li>Set all data in channel object</li>
	 * </ul>
	 * 
	 * @param aoChannel
	 * @param asConfUpdateTaskStatus
	 * @param asAmendConfTaskStatus
	 * @param asFYConfTaskStatus
	 */
	private void setRuleDataInChannelNewFYConfig(Channel aoChannel, String asConfUpdateTaskStatus,
			String asFYConfTaskStatus, int aiOpenTaskAmendmentConf, int aiOpenTaskAmendmentCOF)
	{
		if (null == asConfUpdateTaskStatus)
		{
			asConfUpdateTaskStatus = HHSConstants.EMPTY_STRING;
		}
		if (null == asFYConfTaskStatus)
		{
			asFYConfTaskStatus = HHSConstants.EMPTY_STRING;
		}
		aoChannel.setData(HHSConstants.CONF_UPDATE_TASK_STATUS, asConfUpdateTaskStatus);
		aoChannel.setData(HHSConstants.FY_CONF_TASK_STATUS, asFYConfTaskStatus);
		aoChannel.setData(HHSConstants.OPEN_TASK_AMEND_CONF, aiOpenTaskAmendmentConf);
		aoChannel.setData(HHSConstants.OPEN_TASK_AMEND_CERT_FUND, aiOpenTaskAmendmentCOF);
	}

	/**
	 * This method Evaluate Rule define business-rule.xml file for New FY
	 * Configuration
	 * 
	 * <ul>
	 * <li>Evaluate Rule 'NewFYConfigErrorRule01'</li>
	 * <li>For error return error message else set success in hashmap object</li>
	 * </ul>
	 * 
	 * @param aoChannel Channel object
	 * @param abErrorCheck04 Error check Rule Id
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private HashMap callNewFYConfigBusinessRule(Channel aoChannel, boolean abErrorCheck) throws ApplicationException
	{
		HashMap loHMError = new HashMap();
		String lsRuleReturnValue;
		String lsRuleReturnValue1;
		String lsRuleReturnValue2;
		lsRuleReturnValue = (String) Rule.evaluateRule(HHSConstants.NEW_FY_CONFIG_ERROR_RULE01, aoChannel);
		if (lsRuleReturnValue.equalsIgnoreCase(HHSConstants.FALSE) || lsRuleReturnValue.isEmpty())
		{
			lsRuleReturnValue1 = (String) Rule.evaluateRule(HHSConstants.NEW_FY_CONFIG_ERROR_RULE02, aoChannel);
			if (lsRuleReturnValue1.equalsIgnoreCase(HHSConstants.FALSE) || lsRuleReturnValue1.isEmpty())
			{
				lsRuleReturnValue2 = HHSConstants.FALSE;
				if (lsRuleReturnValue2.equalsIgnoreCase(HHSConstants.FALSE) || lsRuleReturnValue1.isEmpty())
				{
					loHMError.put(HHSConstants.CLC_ERROR_CHECK, HHSConstants.SUCCESS);
				}
				else
				{
					loHMError.put(HHSConstants.CLC_ERROR_CHECK, HHSConstants.ERROR_FLAG);
					loHMError.put(HHSConstants.CLC_ERROR_MSG, lsRuleReturnValue2);
				}
			}
			else
			{
				loHMError.put(HHSConstants.CLC_ERROR_CHECK, HHSConstants.ERROR_FLAG);
				loHMError.put(HHSConstants.CLC_ERROR_MSG, lsRuleReturnValue1);
			}

		}
		else
		{
			loHMError.put(HHSConstants.CLC_ERROR_CHECK, HHSConstants.ERROR_FLAG);
			loHMError.put(HHSConstants.CLC_ERROR_MSG, lsRuleReturnValue);
		}

		return loHMError;
	}

	/**
	 * @param aoUserSession
	 * @param asContractId
	 * @param asWorkflowType
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private String fetchTaskStatus(P8UserSession aoUserSession, String asContractId, String asWorkflowType)
			throws ApplicationException
	{
		HashMap loHmWFProperties = new HashMap();
		loHmWFProperties.put(HHSConstants.PROPERTY_PE_CONTRACT_ID, asContractId);
		loHmWFProperties.put(HHSConstants.PROPERTY_PE_TASK_TYPE, asWorkflowType);

		return new P8ProcessServiceForSolicitationFinancials().fetchTaskStatusFromView(aoUserSession, loHmWFProperties);

	}

	/**
	 * This method fetches Award Epin of Base Contract and all its Amendment
	 * Contract
	 * <ul>
	 * <li>calls query 'fetchBaseAmendmentContractDetails'</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession Object
	 * @param asContractId String containing ContractId
	 * @return List of ContractList
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<ContractList> fetchBaseAmendmentContractDetails(SqlSession aoMybatisSession, String asContractId)
			throws ApplicationException
	{
		List<ContractList> loContractList = null;
		try
		{
			loContractList = (List<ContractList>) DAOUtil.masterDAO(aoMybatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_BASE_AMENDMENT_CONTRACT_DETAILS,
					HHSConstants.JAVA_LANG_STRING);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handled over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.CONTRACTID, asContractId);
			LOG_OBJECT.Error("Exception occured in FinancialsListService: fetchBaseAmendmentContractDetails method:: ",
					loAppEx);
			setMoState("Transaction Failed:: FinancialsListService: fetchBaseAmendmentContractDetails method - failed to fetch"
					+ asContractId + " \n");
			throw loAppEx;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception occured in FinancialsListService: fetchBaseAmendmentContractDetails method:: ",
					loExp);
			setMoState("Transaction Failed:: FinancialsListService: fetchBaseAmendmentContractDetails method - failed to fetch"
					+ asContractId + " \n");
			throw new ApplicationException("Error occured while retrieving Contract Summary", loExp);
		}
		return loContractList;
	}

	/**
	 * This method fetches Award Epin of Base Contract and all its Amendment
	 * Contract
	 * <ul>
	 * <li>calls query 'fetchBaseAmendmentContractDetailsAmendmentList'</li>
	 * <li>This method was added in R4</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession object
	 * @param asContractId String containing Contract Id
	 * @return loContractList List of ContractList
	 * @throws ApplicationException If ApplicationException occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public List<ContractList> fetchBaseAmendmentContractDetailsAmendmentList(SqlSession aoMybatisSession,
			String asContractId, String asAmendContractId) throws ApplicationException
	{
		List<ContractList> loContractList = null;
		try
		{
			Map loFetchBaseAmendMap = new HashMap();
			loFetchBaseAmendMap.put(HHSConstants.CONTRACT_ID_KEY, asContractId);
			loFetchBaseAmendMap.put(HHSConstants.AMENDED_CONTRACT_ID, asAmendContractId);
			loContractList = (List<ContractList>) DAOUtil.masterDAO(aoMybatisSession, loFetchBaseAmendMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSConstants.FETCH_BASE_AMENDMENT_CONTRACT_DETAILS_AMENDMENT_LIST, HHSConstants.JAVA_UTIL_HASH_MAP);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handled over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.CONTRACTID, asContractId);
			LOG_OBJECT.Error("Exception occured in FinancialsListService: fetchBaseAmendmentContractDetails method:: ",
					loAppEx);
			setMoState("Transaction Failed:: FinancialsListService: fetchBaseAmendmentContractDetails method - failed to fetch"
					+ asContractId + " \n");
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured in FinancialsListService: fetchBaseAmendmentContractDetails method:: ",
					loAppEx);
			setMoState("Transaction Failed:: FinancialsListService: fetchBaseAmendmentContractDetails method - failed to fetch"
					+ asContractId + " \n");
			throw new ApplicationException("Error occured while retrieving Contract Summary", loAppEx);
		}
		return loContractList;
	}

	/**
	 * This method fetches the base award pin
	 * <ul>
	 * <li>calls query 'fetchBaseAwardEpin'</li>
	 * </ul>
	 * @param aoMybatisSession
	 * @param asContractId
	 * @return
	 * @throws ApplicationException
	 */
	public String fetchBaseAwardEpin(SqlSession aoMybatisSession, String asContractId) throws ApplicationException
	{
		String lsBaseAwardEpin = null;
		try
		{
			lsBaseAwardEpin = (String) DAOUtil.masterDAO(aoMybatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, "fetchBaseAwardEpin", HHSConstants.JAVA_LANG_STRING);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handled over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.CONTRACTID, asContractId);
			LOG_OBJECT.Error("Exception occured in FinancialsListService: fetchBaseAwardEpin method:: ", loAppEx);
			setMoState("Transaction Failed:: FinancialsListService: fetchBaseAwardEpin method - failed to fetch"
					+ asContractId + " \n");
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured in FinancialsListService: fetchBaseAwardEpin method:: ", loAppEx);
			setMoState("Transaction Failed:: FinancialsListService: fetchBaseAwardEpin method - failed to fetch"
					+ asContractId + " \n");
			throw new ApplicationException("Error occured while fetching base award epin", loAppEx);
		}
		return lsBaseAwardEpin;
	}

	/**
	 * This method is for Renew Contract's validation i.e., If 'Contract Start
	 * Date' entered for the Renewal is before the 'Contract End Date' of the
	 * base contract that is being renewed
	 * <ul>
	 * <li>calls query 'renewContractDateValidation'</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession's object
	 * @param aoContractDetailByEpin EPinDetailBean's object
	 * @return lbSuccessStatus Boolean's object
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "deprecation" })
	public Boolean renewContractDateValidation(SqlSession aoMybatisSession, EPinDetailBean aoContractDetailByEpin)
			throws ApplicationException
	{
		Boolean lbSuccessStatus = false;
		String lsContractEndDate = HHSConstants.EMPTY_STRING;
		int liCompare = HHSConstants.INT_ZERO;
		try
		{
			lsContractEndDate = (String) DAOUtil.masterDAO(aoMybatisSession, aoContractDetailByEpin.getContractId(),
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.RENEW_CONTRACT_DATE_VALIDATION,
					HHSConstants.JAVA_LANG_STRING);

			Date loBaseEndDate = new Date(lsContractEndDate);
			Date loRenewStartDate = new Date(aoContractDetailByEpin.getContractStart());

			liCompare = loBaseEndDate.compareTo(loRenewStartDate);
			if (liCompare == -1)
			{
				lbSuccessStatus = true;
			}

		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handled over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.CONTRACTID, aoContractDetailByEpin.getContractId());
			LOG_OBJECT.Error("Exception occured in FinancialsListService: renewContractDateValidation method:: ",
					loAppEx);
			setMoState("Transaction Failed:: FinancialsListService: renewContractDateValidation method - failed to validate"
					+ aoContractDetailByEpin.getContractId() + " \n");
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured in FinancialsListService: renewContractDateValidation method:: ",
					loAppEx);
			setMoState("Transaction Failed:: FinancialsListService: renewContractDateValidation method - failed to validate"
					+ aoContractDetailByEpin.getContractId() + " \n");
			throw new ApplicationException("Error occured while renew contract'c validation ", loAppEx);
		}
		return lbSuccessStatus;
	}

	/**
	 * This method is for getting the count of budgets configured for a
	 * contracts in a particular FY
	 * <ul>
	 * <li>calls query 'fetchCountractBudgetCountForFY'</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession's object
	 * @param asContractId - String type object
	 * @param asFiscalYearId - String type object
	 * @return loCount - Integer object
	 * @throws ApplicationException
	 */
	public Integer fetchCountractBudgetCountForFY(SqlSession aoMybatisSession, String asContractId,
			String asFiscalYearId) throws ApplicationException
	{
		Integer loCount = 0;
		Map<String, String> loBudgetInfo = new HashMap<String, String>();
		try
		{
			// Set the parameters required in HashMap
			loBudgetInfo.put(HHSConstants.CONTRACT_ID_KEY, asContractId);
			loBudgetInfo.put(HHSConstants.FISCAL_YEAR_ID_KEY, asFiscalYearId);

			loCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loBudgetInfo,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.FETCH_COUNTRACT_BUDGET_COUNT_FOR_FY,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			setMoState("Budgets count for contract fetched successfully:");
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting contract budget count in fetchCountractBudgetCountForFY() method :");
			throw loExp;
		}
		return loCount;
	}

	/**
	 * This method is for reverting the contract anmount to original value <li>
	 * calls query 'revertContractToBaseValue'</li>
	 * @param aoMybatisSession SqlSession's object
	 * @param asContractId - String type object
	 * @param aoContractMergeHashMap - Hashmap
	 * @param abAuthFlag - Boolean
	 * @return abRevertStatus - Boolean
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public Boolean revertContractToBaseValue(SqlSession aoMybatisSession, HashMap aoContractMergeHashMap,
			Boolean abAuthFlag) throws ApplicationException
	{
		boolean abRevertStatus = false;
		try
		{
			if (abAuthFlag)
			{
				Integer lbUpdateCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoContractMergeHashMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.REVERT_CONTRACT_TO_BASE_VALUE,
						HHSConstants.JAVA_UTIL_HASH_MAP);
				if (lbUpdateCount == 1)
				{
					abRevertStatus = true;
					setMoState("contract value is reverted to base value when amendment is cancelled");
				}
				else
				{
					throw new ApplicationException("FinancialsListService revertContractToBaseValue updated rows::"
							+ lbUpdateCount + "expected is 1");
				}
			}
			setMoState("FinancialsListService method revertContractToBaseValue passed successfully:");
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error inf FinancialsListService method revertContractToBaseValue method:");
			throw loExp;
		}
		return abRevertStatus;
	}

	/**
	 * This method is added for Release 3.3.0 Defect 6458. This method populates
	 * the Notification HashMap aoHmNotifyParam with the Procurement title from
	 * Workflow HashMap aoHMWFRequiredProps
	 * @param aoMybatisSession SqlSession's object
	 * @param aoHMWFRequiredProps Workflow HashMap
	 * @param aoHmNotifyParam Notification HashMap
	 * @return HashMap<String, Object> aoHmNotifyParam
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public HashMap<String, Object> mergeMapForPcofTask(HashMap aoHMWFRequiredProps,
			HashMap<String, Object> aoHmNotifyParam) throws ApplicationException
	{
		try
		{
			HashMap<String, String> loRequestMap = (HashMap<String, String>) aoHmNotifyParam
					.get(TransactionConstants.REQUEST_MAP_PARAMETER_NAME);
			loRequestMap.put(HHSConstants.PROC_TITLE,
					(String) aoHMWFRequiredProps.get(HHSConstants.PROPERTY_PE_PROCUREMENT_TITLE));
			aoHmNotifyParam.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
			setMoState("mergeMapForPcofTask() method map merged successfully:");
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error Occured in mergeMapForPcofTask() method",
					aoEx);
			LOG_OBJECT.Error("Error Occured in mergeMapForPcofTask() method", loAppEx);
			throw loAppEx;
		}
		return aoHmNotifyParam;
	}

	/**
	 * This method fetches Contract and Budget Docs for a contract Added for
	 * enhancement 6000 for Release 3.8.0
	 * <ul>
	 * <li>calls query 'fetchContractBudgetDocs'</li>
	 * </ul>
	 * 
	 * @param asContractId contractId
	 * @return loUnlinkedContractDocsList List of unlinked docs
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public List<String> fetchContractBudgetDocs(SqlSession aoMybatisSession, String asContractId,
			Boolean lbErrorCheckRule) throws ApplicationException
	{
		List<String> loContractDocsList = null;
		List<String> loUnlinkedContractDocsList = new ArrayList<String>();
		LOG_OBJECT.Debug("Entered into fetchContractBudgetDocs:: " + asContractId);
		try
		{
			if (lbErrorCheckRule)
			{
				loContractDocsList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, asContractId,
						HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_CONTRACT_BUDGET_DOCS,
						HHSConstants.JAVA_LANG_STRING);
				if (loContractDocsList != null && !loContractDocsList.isEmpty())
				{
					for (String loContractDoc : loContractDocsList)
					{
						Integer liNoRowsExists = (Integer) DAOUtil.masterDAO(aoMybatisSession, loContractDoc,
								HHSConstants.MAPPER_CLASS_COMMON_MAPPER,
								HHSConstants.CHECK_DOCUMENT_EXISTS_IN_ANY_TABLE, HHSConstants.JAVA_LANG_STRING);
						if (liNoRowsExists <= HHSConstants.INT_ONE)
						{
							loUnlinkedContractDocsList.add(loContractDoc);
						}
					}
				}
			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT
					.Error("Exception occured for fetching Contract Details in FinancialsListService: fetchContractBudgetDocs ",
							loAppEx);
			setMoState("Transaction Failed:: FinancialsListService: fetchContractBudgetDocs method - failed to fetch"
					+ asContractId + " \n");
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			// Handle the exception of type ApplicationException
			LOG_OBJECT
					.Error("Exception occured for fetching Contract details in FinancialsListService: fetchContractBudgetDocs ",
							loAppEx);
			setMoState("Transaction Failed:: FinancialsListService: fetchContractBudgetDocs method - failed to fetch"
					+ asContractId + " \n");
			throw new ApplicationException("Error occured while fetching Contract doc details", loAppEx);
		}
		return loUnlinkedContractDocsList;
	}

	/**
	 * This method added for Release 3.8.0 #6482 This method updates the
	 * contract title and program name for Update Contract Information screen.
	 * @param aoMybatisSession SqlSession's object
	 * @param aoContractDetailByEpin EPinDetailBean bean
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Boolean updateContractInfo(SqlSession aoMybatisSession, EPinDetailBean aoContractDetailByEpin,
			Boolean aoErrorCheckRule) throws ApplicationException
	{
		Boolean loUpdateInfo = Boolean.FALSE;
		LOG_OBJECT.Debug("Entered into updateContractInfo:: " + aoContractDetailByEpin.getContractId());
		try
		{
			if (aoErrorCheckRule)
			{
				Map updateContractInfoMap = new HashMap();
				updateContractInfoMap.put(HHSConstants.CONTRACT_ID_KEY, aoContractDetailByEpin.getContractId());
				updateContractInfoMap.put(HHSConstants.AS_UPDATED_CONTRACT_TITLE,
						aoContractDetailByEpin.getContractTitle());
				updateContractInfoMap.put(HHSConstants.AS_PROGRAM_ID, aoContractDetailByEpin.getProgramNameId());
				updateContractInfoMap.put(HHSConstants.AS_USER_ID, aoContractDetailByEpin.getModifyByUserId());
				DAOUtil.masterDAO(aoMybatisSession, updateContractInfoMap, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
						HHSConstants.AS_UPDATE_CONTRACT__FOR_TITLE_PROG_NAME, HHSConstants.JAVA_UTIL_HASH_MAP);
				DAOUtil.masterDAO(aoMybatisSession, updateContractInfoMap, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
						HHSConstants.AS_UPDATE_BUDGET_FOR_TITLE_PROG_NAME, HHSConstants.JAVA_UTIL_HASH_MAP);
				loUpdateInfo = Boolean.TRUE;
				setMoState("Contract title and Program name successfully updated for: "
						+ aoContractDetailByEpin.getContractId() + " \n");
			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT
					.Error("Exception occured while updating contract information in FinancialsListService: updateContractInfo ",
							loAppEx);
			setMoState("Transaction Failed:: FinancialsListService: updateContractInfo method - failed to fetch"
					+ aoContractDetailByEpin.getContractId() + " \n");
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT
					.Error("Exception occured while updating contract information in FinancialsListService: updateContractInfo ",
							loAppEx);
			setMoState("Transaction Failed:: FinancialsListService: updateContractInfo method - failed to fetch"
					+ aoContractDetailByEpin.getContractId() + " \n");
			throw new ApplicationException("Error occured while updating contract information", loAppEx);
		}
		return loUpdateInfo;
	}

	// R5 Change starts
	/**
	 * This method is addded in Release 5 for CancelAll Awards functionality.
	 * @param aoMybatisSession
	 * @param asEvaluationPoolMappingId
	 * @return contractIdList - String
	 * @throws ApplicationException
	 */
	public String fetchParentContractIdForCancelAllAwards(SqlSession aoMybatisSession, String asEvaluationPoolMappingId)
			throws ApplicationException
	{
		String lsContractIdList = HHSConstants.EMPTY_STRING;
		try
		{
			lsContractIdList = (String) DAOUtil.masterDAO(aoMybatisSession, asEvaluationPoolMappingId,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSR5Constants.GET_CONTRACT_ID_FOR_CANCEL_ALL_AWARDS,
					HHSConstants.JAVA_LANG_STRING);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Application Exception occured while geting parent contract ID contract", loAppEx);
			setMoState("Transaction Failed:: getParentContractId method - failed to fetch parent record " + ": /n");
			throw loAppEx;
		}
		catch (Exception loEx)
		{

			LOG_OBJECT.Error("Application Exception occured while geting parent contract ID contract", loEx);
			setMoState("Transaction Failed:: getParentContractId method - failed to fetch parent record " + ": /n");
			throw new ApplicationException("Exception occured while deleting contract", loEx);
		}
		return lsContractIdList;
	}

	// R5 Change ends

	// R6 Changes for Defect 8572 starts
	/**
	 * This method is added for 8572. It will add financial tasks list to the
	 * ArrayList Changed method access specifier to be used globally.
	 * @param aoTaskTypeList
	 */
	public static void getTaskTypesForContract(List<String> aoTaskTypeList)
	{
		aoTaskTypeList.add(HHSConstants.TASK_ADVANCE_REVIEW);
		aoTaskTypeList.add(HHSConstants.TASK_ADVANCE_PAYMENT_REVIEW);
		aoTaskTypeList.add(HHSConstants.TASK_AMENDMENT_CONFIGURATION);
		aoTaskTypeList.add(HHSConstants.TASK_AMENDMENT_COF);
		aoTaskTypeList.add(HHSConstants.TASK_BUDGET_AMENDMENT);
		aoTaskTypeList.add(HHSConstants.TASK_BUDGET_MODIFICATION);
		aoTaskTypeList.add(HHSConstants.TASK_BUDGET_UPDATE);
		aoTaskTypeList.add(HHSConstants.TASK_BUDGET_REVIEW);
		aoTaskTypeList.add(HHSConstants.TASK_CONTRACT_COF);
		aoTaskTypeList.add(HHSConstants.TASK_CONTRACT_CONFIGURATION);
		aoTaskTypeList.add(HHSConstants.TASK_CONTRACT_UPDATE);
		aoTaskTypeList.add(HHSConstants.TASK_INVOICE_REVIEW);
		aoTaskTypeList.add(HHSConstants.TASK_NEW_FY_CONFIGURATION);
		aoTaskTypeList.add(HHSConstants.TASK_PAYMENT_REVIEW);
	}

	// R6 Changes for Defect 8572 ends
	// R7 changes start
	/**
	 * This method is added in R7 insert message functionality. This method will
	 * check if previously method is flagged or not. If flagged, unflag all the
	 * previous entries in the Flag_Contract_Detail table for the input contract
	 * id. insert the new flag message details in Table.
	 * @param aoMybatisSession
	 * @param aoMap
	 * @return
	 * @throws ApplicationException
	 */
	public Integer flagContract(SqlSession aoMybatisSession, Map aoMap) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered inside flagContract method");
		String lsContractId = aoMap.get(HHSConstants.CONTRACT_ID1).toString();
		Integer loInsertStatus = 0;
		try
		{
			loInsertStatus = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSR5Constants.SAVE_CONTRACT_LEVEL_MESSAGE,
					HHSConstants.JAVA_UTIL_MAP);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error(
					"Application Exception occured while inserting Flag messagae in Flag_Contract_Detail table:"
							+ lsContractId, loAppEx);
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error(
					"Exception occured while inserting Flag messagae in Flag_Contract_Detail table for contract id:"
							+ lsContractId, loEx);
			throw new ApplicationException(
					" Exception occured while inserting Flag messagae in Flag_Contract_Detail table", loEx);
		}

		return loInsertStatus;
	}
	/**
	 * This method is added in R7 unflag Functionality. This method will
	 * unflagged all the enteries of input contract id.
	 * @param aoMybatisSession
	 * @param asContractId
	 * @param asUserId
	 * @return
	 * @throws ApplicationException
	 */
	public Integer unflagContract(SqlSession aoMybatisSession, String asContractId, String asUserId)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered inside unflagContract with contractId: " + asContractId);
		Integer loInsertStatus = 0;
		Map<String, String> loInputMap = new HashMap<String, String>();
		try
		{
			if (null != asContractId && !(asContractId.equalsIgnoreCase(HHSR5Constants.EMPTY_STRING)))
			{
				loInputMap.put(HHSR5Constants.CONTRACT_ID1, asContractId);
				loInputMap.put(HHSR5Constants.USER_ID, asUserId);
				DAOUtil.masterDAO(aoMybatisSession, loInputMap, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
						HHSR5Constants.SAVE_CONTRACT_LEVEL_MESSAGE_HISTORY, HHSConstants.JAVA_UTIL_MAP);

				loInsertStatus = (Integer) DAOUtil.masterDAO(aoMybatisSession, asContractId,
						HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSR5Constants.UPDATE_CONTRACT_LEVEL_MESSAGE,
						HHSConstants.JAVA_LANG_STRING);
			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Application Exception occured while updating Flag in Flag_Contract_Detail table:"
					+ asContractId, loAppEx);
			throw loAppEx;
		}
		catch (Exception loEx)
		{

			LOG_OBJECT.Error("Exception occured while updating Flag in Flag_Contract_Detail table for contract id:"
					+ asContractId, loEx);
			throw new ApplicationException(" Exception occured while updating Flag in Flag_Contract_Detail table", loEx);
		}
		return loInsertStatus;
	}

	/**
	 * This method is added in R7. This method will fetch the contract message
	 * overlay details.
	 * @param aoMybatisSession
	 * @param asContractId
	 * @param asAction
	 * @return
	 * @throws ApplicationException
	 */
	public ContractList fetchContractMessageOverlayDetails(SqlSession aoMybatisSession, String asContractId,
			String asAction) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered inside fetchContractMessageOverlayDetails with action: " + asAction);
		ContractList loContractList = null;
		try
		{
			if (null != asAction && asAction.equalsIgnoreCase(HHSR5Constants.UNFLAG_CONTRACT))
			{
				loContractList = (ContractList) DAOUtil.masterDAO(aoMybatisSession, asContractId,
						HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSR5Constants.FETCH_UNFLAG_OVERLAY_DETAILS,
						HHSConstants.JAVA_LANG_STRING);
			}
			else if (null != asAction && asAction.equalsIgnoreCase(HHSR5Constants.FLAG_CONTRACT))
			{
				loContractList = (ContractList) DAOUtil.masterDAO(aoMybatisSession, asContractId,
						HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSR5Constants.FETCH_FLAG_OVERLAY_DETAILS,
						HHSConstants.JAVA_LANG_STRING);
			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Application Exception occured while fetching details:" + asContractId, loAppEx);
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Exception Exception occured while fetching details:", loEx);
			throw new ApplicationException(" Exception occured while fetching details:", loEx);
		}
		return loContractList;
	}

	// End : Added in R7
	// Start : Added in R7 defect 8644 cancel and merge
	/**
	 * This method is added for Defect 8644 to check if partial merging is
	 * already completed.
	 * @param aoMybatisSession
	 * @param asContractId
	 * @return lbUpdateStatus - Boolean
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public Boolean checkIfMergingCompleted(SqlSession aoMybatisSession, String asContractId)
			throws ApplicationException
	{
		Boolean lbIsAmendmentRegisteredInFMS = false;
		try
		{
			Integer liAmendmentRegisteredInFMSFlag = (Integer) DAOUtil.masterDAO(aoMybatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.IS_AMENDMNET_REGISTERED_IN_FMS, HHSConstants.JAVA_LANG_STRING);
			if (liAmendmentRegisteredInFMSFlag != null && liAmendmentRegisteredInFMSFlag == 1)
			{
				lbIsAmendmentRegisteredInFMS = true;
			}
			setMoState("FinancialsListService method addYearToContractFinancials passed successfully:");
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error inf FinancialsListService method addYearToContractFinancials method:");
			throw loExp;
		}
		return lbIsAmendmentRegisteredInFMS;
	}

	// End : Added in R7

	// Start : Added in R7
	/**
	 * This method is added for adding fiscal to a contract.
	 * @param aoMybatisSession
	 * @param aoMap
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public Boolean addYearToContractEndDate(SqlSession aoMybatisSession, String asContractId)
			throws ApplicationException
	{
		Integer loReturnCount = 0;
		Boolean lbUpdateStatus=false;
		try
		{
			loReturnCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSR5Constants.ADD_YEAR_TO_CONTRACT_DATE,
					HHSConstants.JAVA_LANG_STRING);
			if (loReturnCount > 0)
			{
				lbUpdateStatus = true;
			}
			setMoState("FinancialsListService method addYearToContractEndDate passed successfully:");
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error(
					"Application Exception occured while inserting Flag messagae in Flag_Contract_Detail table:"
							, loAppEx);
			throw loAppEx;
		}
		return lbUpdateStatus;
	}

	/**
	 * This method is added for newly added fiscal to a contract financials.
	 * @param aoMybatisSession
	 * @param asContractId
	 * @param asAction
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public Boolean addYearToContractFinancials(SqlSession aoMybatisSession, String asContractId)
			throws ApplicationException
	{
		Integer loReturnCount = 0;
		Boolean lbUpdateStatus=false;
		try
		{
			loReturnCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSR5Constants.ADD_YEAR_TO_CONTRACT_FINANCIALS,
					HHSConstants.JAVA_LANG_STRING);
			if (loReturnCount > 0)
			{
				lbUpdateStatus = true;
			}
			setMoState("FinancialsListService method addYearToContractFinancials passed successfully:");
		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Exception Exception occured while fetching details:", loEx);
			throw new ApplicationException(" Exception occured while fetching details:", loEx);
		}
		return lbUpdateStatus;
	}

	// End : Added in R7
	
	/* Start QC 9122 R 8.1 get Base Contract Status  */
	/**
	 * This method fetches the base award pin
	 * <ul>
	 * <li>calls query 'fetchBaseAwardEpin'</li>
	 * </ul>
	 * @param aoMybatisSession
	 * @param asContractId
	 * @return
	 * @throws ApplicationException
	 */
	public Integer fetchBaseContractStatusId(SqlSession aoMybatisSession, String asContractId) throws ApplicationException
	{
		Integer lsBaseContractStatusId = null;
		try
		{
			lsBaseContractStatusId = (Integer) DAOUtil.masterDAO(aoMybatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, "fetchBaseContractStatusId", HHSConstants.JAVA_LANG_STRING);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handled over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.CONTRACTID, asContractId);
			LOG_OBJECT.Error("Exception occured in FinancialsListService: fetchBaseContractStatusId method:: ", loAppEx);
			setMoState("Transaction Failed:: FinancialsListService: fetchBaseContractStatusId method - failed to fetch"
					+ asContractId + " \n");
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured in FinancialsListService: fetchBaseContractStatusId method:: ", loAppEx);
			setMoState("Transaction Failed:: FinancialsListService: fetchBaseContractStatusId method - failed to fetch"
					+ asContractId + " \n");
			throw new ApplicationException("Error occured while fetching base contract status", loAppEx);
		}
		return lsBaseContractStatusId;
	}
	/* End QC 9122 R 8.1 get Base Contract Status  */
	
	/* Start QC 9680 R 9.5  EXT QC9654 - Amendment Config Muliti-Tab Browsing Missing Extra Services and Cost Centers        */
	
	/**
	 * This method fetches the contract status 
	 * <ul>
	 * <li>calls query 'checkStatusForSusOrUnSus'</li>
	 * </ul>
	 * @param aoMybatisSession
	 * @param asContractId
	 * @return
	 * @throws ApplicationException
	 */
	public Integer fetchContractStatusId(SqlSession aoMybatisSession, String asContractId) throws ApplicationException
	{
		Integer lsContractStatusId = null;
		try
		{
			lsContractStatusId = (Integer) DAOUtil.masterDAO(aoMybatisSession, asContractId,
								HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.CHECK_STATUS_FOR_SUS_OR_UN_SUS,
								HHSConstants.JAVA_LANG_STRING);
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.CONTRACTID, asContractId);
			LOG_OBJECT.Error("Exception occured in FinancialsListService: fetchContractStatusId method:: ", loAppEx);
			setMoState("Transaction Failed:: FinancialsListService: fetchContractStatusId method - failed to fetch"
					+ asContractId + " \n");
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured in FinancialsListService: fetchBaseContractStatusId method:: ", loAppEx);
			setMoState("Transaction Failed:: FinancialsListService: fetchBaseContractStatusId method - failed to fetch"
					+ asContractId + " \n");
			throw new ApplicationException("Error occured while fetching base contract status", loAppEx);
		}
		return lsContractStatusId;
	}

	/* End QC 9680 R 9.5  EXT QC9654 - Amendment Config Muliti-Tab Browsing Missing Extra Services and Cost Centers        */
	
}
