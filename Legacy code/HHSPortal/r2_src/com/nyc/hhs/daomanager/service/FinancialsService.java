package com.nyc.hhs.daomanager.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.ContractBean;
import com.nyc.hhs.model.ContractList;
import com.nyc.hhs.model.RateBean;
import com.nyc.hhs.model.SiteDetailsBean;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This service class will be used to get all data for Amendment List page from
 * database using mappings and queries defined in ContractMapper.xml. Insertion
 * or updation of data can also be performed.
 * <ul>
 * <li>The Service Class has been added for R4</li>
 * </ul>
 * 
 */
public class FinancialsService extends ServiceState
{

	private static final LogInfo LOG_OBJECT = new LogInfo(FinancialsService.class);

	/**
	 * This method is called when user select view amendment from the drop
	 * down/on click on amendment tab/user click on filter button on amendment
	 * list It will return the contract list on basis of organization type.
	 * <ul>
	 * <li>query 'fetchAmendmentListScreenProvider' is executed if UserType =
	 * 'provider_org'</li>
	 * <li>query 'fetchAmendmentListScreenAgency' is executed if UserType =
	 * 'agency_org'</li>
	 * <li>query 'fetchAmendmentListScreenAccelerator' is executed if UserType =
	 * 'city_org'</li>
	 * </ul>
	 * @param aoMybatisSession sql session as input
	 * @param aoContractFilterBean ContractList bean as input
	 * @param asUserType organization type as input
	 * @return loContractList Amended Contract list
	 * @throws ApplicationException Exception in case of query failure
	 */
	@SuppressWarnings("unchecked")
	public List<ContractList> fetchAmendmentListSummary(SqlSession aoMybatisSession, ContractList aoContractFilterBean,
			String asUserType) throws ApplicationException
	{
		//LOG_OBJECT.Debug("======fetchAmendmentListSummary========");
		//LOG_OBJECT.Debug("======param aoContractFilterBean :: "+aoContractFilterBean);
		List<ContractList> loContractList = null;
		String lsStatus = null;
		try
		{   
			if (null != aoContractFilterBean.getProvider() && !aoContractFilterBean.getProvider().isEmpty())
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
					if (ascontractStatus.equalsIgnoreCase(HHSConstants.STATUS_SENT_FOR_REG))
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
					if (aoContractFilterBean.getContractStatusList().contains(
							PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
									HHSConstants.STATUS_CONTRACT_PENDING_REGISTARTION)))
					{
						aoContractFilterBean.getContractStatusList().add(
								PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
										HHSConstants.STATUS_CONTRACT_SENT_FOR_REGISTRATION));
					}
					loContractList = (List<ContractList>) DAOUtil.masterDAO(aoMybatisSession, aoContractFilterBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
							HHSConstants.FETCH_AMENDMENT_LISTSCREEN_PROVIDER,
							HHSConstants.COM_NYC_HHS_MODEL_CONTRACT_LIST);

					for (ContractList loConListBean : loContractList)
					{
						if (loConListBean.getContractStatus().equalsIgnoreCase(HHSConstants.SENT_FOR_REG))
						{
							loConListBean.setContractStatus(HHSConstants.PENDING_REG);
						}
					}
					// [Start] QC9517 R 8.7.0 Cancel option should not be available for a Negative Amendment after Budget has been Approved
					fetchNegativeAmendApprovedBudgetCountAndPartialMergeCount(aoMybatisSession, loContractList);
					// [End] QC9517 R 8.7.0 Cancel option should not be available for a Negative Amendment after Budget has been Approved

				}
				else if (asUserType.equals(ApplicationConstants.AGENCY_ORG))
				{
					loContractList = (List<ContractList>) DAOUtil.masterDAO(aoMybatisSession, aoContractFilterBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_AMENDMENT_LISTSCREEN_AGENCY,
							HHSConstants.COM_NYC_HHS_MODEL_CONTRACT_LIST);
					//LOG_OBJECT.Debug("======fetchAmendmentListSummary for AGENCY_ORG :: "+ loContractList);
					// [Start] QC9517 R 8.7.0 Cancel option should not be available for a Negative Amendment after Budget has been Approved
					fetchNegativeAmendApprovedBudgetCountAndPartialMergeCount(aoMybatisSession, loContractList);
					// [End] QC9517 R 8.7.0 Cancel option should not be available for a Negative Amendment after Budget has been Approved
					isViewCoF(aoMybatisSession, loContractList);
				}
				else
				{   
					loContractList = (List<ContractList>) DAOUtil.masterDAO(aoMybatisSession, aoContractFilterBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
							HHSConstants.FETCH_AMENDMENT_LISTSCREEN_ACCELERATOR,
							HHSConstants.COM_NYC_HHS_MODEL_CONTRACT_LIST);
					//LOG_OBJECT.Debug("======fetchAmendmentListSummary for CITY_ORG :: "+ loContractList);
					// Start QC 9448 extension of 9122 R 8.1 Do not allow "Mark As Registered" action for Amendment if  Base Contract is not Registered yet
					if(loContractList != null)
					{
						int seq = 0;
						for(ContractList tmpContractList :  loContractList)
						{
							if(tmpContractList.getMarkAsFmsRegistered()!=null &&  tmpContractList.getMarkAsFmsRegistered().equalsIgnoreCase("1"))
							{	
								String asContractId = tmpContractList.getParentContractId();
								LOG_OBJECT.Debug("Base Contract ID :: "+asContractId);
								//get status
								Integer lsBaseContractStatusId = (Integer) DAOUtil.masterDAO(aoMybatisSession, asContractId,
									HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, "fetchBaseContractStatusId", HHSConstants.JAVA_LANG_STRING);
								LOG_OBJECT.Debug("Base Contract Status :: "+lsBaseContractStatusId);
								if(lsBaseContractStatusId != 62)
								{
									loContractList.get(seq).setMarkAsFmsRegistered("0");
									LOG_OBJECT.Debug("Mark As Registered change to 0 :: "+loContractList.get(seq).getMarkAsFmsRegistered());
								}
							}
							seq++;
						}
					}
					// End  QC 9448 extension of QC 9122 R 8.1 Do not allow "Mark As Registered action for Amendment if  Base Contract is not Registered yet	
					
					// [Start] QC9517 R 8.7.0 Cancel option should not be available for a Negative Amendment after Budget has been Approved
					fetchNegativeAmendApprovedBudgetCountAndPartialMergeCount(aoMybatisSession, loContractList);
					// [End] QC9517 R 8.7.0 Cancel option should not be available for a Negative Amendment after Budget has been Approved
					isViewCoF(aoMybatisSession, loContractList);
				}
			}
			
	
			String lsProviderName = aoContractFilterBean.getProvider();
			if (null != lsProviderName && !lsProviderName.isEmpty())
			{
				lsProviderName = lsProviderName.substring(HHSConstants.INT_ONE);
				lsProviderName = lsProviderName.substring(HHSConstants.INT_ZERO, lsProviderName.length()
						- HHSConstants.INT_ONE);
				aoContractFilterBean.setProvider(lsProviderName);
			}
			setMoState("Amended Contract List fetched successfully for org Type:" + asUserType);
		}
		catch (ApplicationException loExp)
		{
			loExp.addContextData(HHSConstants.CONTRACTID, aoContractFilterBean.getContractId());
			LOG_OBJECT.Error("Exception occured in FinancialsService: fetchAmendmentListSummary method:: ", loExp);
			setMoState("Error while fetching Amended Contract List for org Id:" + asUserType);
			throw loExp;
		}
		//LOG_OBJECT.Debug("=== return loContractList ::  "+ loContractList);
		return loContractList;
	}

	/**
	 * This method set whether View Cof will be visible on amendment list
	 * screen. This method calls the query
	 * 'fetchBaseAmendmentContractDetailsAmendmentList'
	 * @param aoMybatisSession sql session as input
	 * @param loContractList List of amendment contract fetched
	 * @throws ApplicationException Exception in case of query failure
	 * 
	 * 
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private void isViewCoF(SqlSession aoMybatisSession, List<ContractList> loContractList) throws ApplicationException
	{
		if (loContractList != null)
		{
			for (ContractList loContract : loContractList)
			{
				try
				{
					List<ContractList> loContractListViewCOF = null;
					Map loFetchBaseAmendMap = new HashMap();
					loFetchBaseAmendMap.put(HHSConstants.CONTRACT_ID_KEY, loContract.getParentContractId());
					loFetchBaseAmendMap.put(HHSConstants.AMENDED_CONTRACT_ID, loContract.getContractId());
					loContractListViewCOF = (List<ContractList>) DAOUtil.masterDAO(aoMybatisSession,
							loFetchBaseAmendMap, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
							HHSConstants.FETCH_BASE_AMENDMENT_CONTRACT_DETAILS_AMENDMENT_LIST,
							HHSConstants.JAVA_UTIL_HASH_MAP);
					if (!loContractListViewCOF.isEmpty())
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
	}

	/**
	 * This method is called when user select view amendment from the drop
	 * down/on click on amendment tab/user click on filter button on amendment
	 * list It will return count of contract list on basis of organization type.
	 * <ul>
	 * <li>query 'fetchAmendmentCountProvider' is executed if UserType =
	 * 'provider_org'</li>
	 * <li>query 'fetchAmendmentCountAgency' is executed if UserType =
	 * 'agency_org'</li>
	 * <li>query 'fetchAmendmentCountAccelerator' is executed if UserType =
	 * 'city_org'</li>
	 * </ul>
	 * @param aoMybatisSession mybatis SQL session
	 * @param aoContractBean an object of ContractList
	 * @param asUserType string containing user type
	 * @return an integer value of amended contracts count
	 * @throws ApplicationException Exception in case of query failure
	 * 
	 * 
	 */
	public Integer getContractsCount(SqlSession aoMybatisSession, ContractList aoContractBean, String asUserType)
			throws ApplicationException
	{
		Integer loContractCount = HHSConstants.INT_ZERO;
		try
		{
			if (null != aoContractBean.getProvider() && !aoContractBean.getProvider().isEmpty())
			{
				StringBuffer loProviderNameSb = new StringBuffer(HHSConstants.PERCENT).append(
						aoContractBean.getProvider()).append(HHSConstants.PERCENT);
				aoContractBean.setProvider(loProviderNameSb.toString());
			}
			if (aoContractBean.getContractStatusList() != null)
			{
				if (asUserType.equals(ApplicationConstants.PROVIDER_ORG))
				{
					loContractCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoContractBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_AMENDMENT_COUNT_PROVIDER,
							HHSConstants.COM_NYC_HHS_MODEL_CONTRACT_LIST);
				}
				else if (asUserType.equals(ApplicationConstants.AGENCY_ORG))
				{
					loContractCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoContractBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_AMENDMENT_COUNT_AGENCY,
							HHSConstants.COM_NYC_HHS_MODEL_CONTRACT_LIST);
				}
				else
				{
					loContractCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoContractBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_AMENDMENT_COUNT_ACCELERATOR,
							HHSConstants.COM_NYC_HHS_MODEL_CONTRACT_LIST);
				}
			}
			String lsProviderName = aoContractBean.getProvider();
			if (null != lsProviderName && !lsProviderName.isEmpty())
			{
				lsProviderName = lsProviderName.substring(HHSConstants.INT_ONE);
				lsProviderName = lsProviderName.substring(HHSConstants.INT_ZERO, lsProviderName.length()
						- HHSConstants.INT_ONE);
				aoContractBean.setProvider(lsProviderName);
			}
			setMoState("Amended Contract count fetched successfully for org type:" + asUserType);
		}
		catch (ApplicationException loExp)
		{
			loExp.addContextData(HHSConstants.CONTRACTID, aoContractBean.getContractId());
			LOG_OBJECT.Error("Exception occured in FinancialsService: getContractsCount method:: ", loExp);
			setMoState("Error while getting amended contract count for org type:" + asUserType);
			throw loExp;
		}
		return loContractCount;
	}

	/**
	 * This method will be called after amendment configuration and amendment
	 * COF task has been finished.It updates amendment contract status to
	 * pending COF on finish of amendment configuration task and set status to
	 * pending configuration on returned from Amendment COF task at level 2.
	 * <ul>
	 * <li>calls the query 'updateAmendmentContractStatusCoF'</li>
	 * </ul>
	 * @param aoMybatisSession- aoMybatisSession Object
	 * @param aoTaskDetailsBean -TaskDetailsBean Object
	 * @param asStatusId -String Object
	 * @return lbProcessFlag - Boolean Object
	 * @throws ApplicationException ApplicationException object
	 * 
	 * 
	 */

	public Boolean processContractAfterAmendmentTask(SqlSession aoMybatisSession, TaskDetailsBean aoTaskDetailsBean,
			String asStatusId, Boolean aoFinishTaskStatus) throws ApplicationException
	{
		Boolean lbProcessFlag = Boolean.FALSE;
		HashMap<String, String> loReqdMap = new HashMap<String, String>();
		if (aoFinishTaskStatus)
		{
			try
			{
				// R4, updating amendment contract status to pending CoF
				loReqdMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, aoTaskDetailsBean.getContractId());
				loReqdMap.put(HHSConstants.STATUS_ID, asStatusId);
				DAOUtil.masterDAO(aoMybatisSession, loReqdMap, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.UPDATE_AMENDMENT_STATUS_COF, HHSConstants.JAVA_UTIL_HASH_MAP);
	
			}
			catch (ApplicationException loExp)
			{
				loExp.addContextData(HHSConstants.CONTRACTID, aoTaskDetailsBean.getContractId());
				LOG_OBJECT.Error("Exception occured in FinancialsService: processContractAfterAmendmentTask method:: ",
						loExp);
				setMoState("ApplicationException while executing processContractAfterAmendmentTask method ");
				throw loExp;
	
			}
			catch (Exception loEx)
			{
				LOG_OBJECT.Error("Exception occured in FinancialsService: processContractAfterAmendmentTask method:: ",
						loEx);
				setMoState("Exception while executing processContractAfterAmendmentTask method ");
				throw new ApplicationException("Exception while executing processContractAfterAmendmentTask method",
						loEx);
	
			}
		}

		return lbProcessFlag;

	}

	/**
	 * This method is used to insert properties of document uploaded in bulk
	 * upload
	 * <ul>
	 * <li>calls the query 'insertBulkUploadDocumentProperties'</li>
	 * </ul>
	 * @param aoMyBatisSession holds the SQL session to do DB operations
	 * 
	 * @param aoPropertyMap holds the properties of the document uploaded
	 * 
	 * @param lsDocumentId is the id returned by upload service after uploading
	 *            the bulk upload document.
	 * @return boolean return type holds the result of
	 *         transaction;true=success;false=failure
	 * @throws ApplicationException
	 * 
	 * 
	 */
	public boolean insertBulkuploadDocumentInDB(SqlSession aoMyBatisSession, HashMap aoPropertyMap, String lsDocumentId)
			throws ApplicationException
	{
		boolean lbStatus = false;
		HashMap<String, String> loReqdMap = new HashMap<String, String>();

		try
		{
			loReqdMap.put(HHSConstants.DOC_ID, lsDocumentId);
			loReqdMap.put(HHSConstants.BULK_UPLOAD_FILE_NAME,
					(String) aoPropertyMap.get(P8Constants.PROPERTY_CE_DOCUMENT_TITLE));
			loReqdMap.put(HHSConstants.CREATED_BY,
					(String) aoPropertyMap.get(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY));
			loReqdMap.put(HHSConstants.BULK_UPLOAD_FILE_MODIFIED_BY,
					(String) aoPropertyMap.get(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY));
			loReqdMap.put(HHSConstants.BULK_UPLOAD_FILE_STATUS_ID, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_BULK_UPLOAD_NOT_PROCESSED));
			DAOUtil.masterDAO(aoMyBatisSession, loReqdMap, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSConstants.BULK_UPLOAD_FILE_PROPERTIES_SAVE_QUERY, HHSConstants.JAVA_UTIL_HASH_MAP);
			lbStatus = true;
		}
		catch (ApplicationException loExp)
		{
			loExp.addContextData(HHSConstants.CONTRACTID, aoPropertyMap);
			LOG_OBJECT.Error("Exception occured in FinancialsService: insertBulkuploadDocumentInDB method:: ", loExp);
			setMoState("ApplicationException while executing insertBulkuploadDocumentInDB method ");
			throw loExp;
		}
		return lbStatus;
	}

	/**
	 * <ul>
	 * <li>This method is used to insert data in budgetCustomization table on
	 * check/uncheck the EntityTypeId</li>
	 * <li>If calls the query 'getBudgetFromContractAndFiscalYEar'</li>
	 * <li>calls the query 'insertBudgetCustomizationForUpdate'</li>
	 * <li>calls the query 'insertBudgetCustomization'</li>
	 * <li>calls the query 'deleteBudgetCustomizationForUpdate'</li>
	 * <li>calls the query 'deleteBudgetCustomization'</li>
	 * <li>Method Added in R4</li>
	 * </ul>
	 * @param aoMyBatisSession holds the SQL session to do DB operations
	 * @param aoHashMap holds the parameter for budgetCustomization table
	 * @return boolean return type holds the result of
	 *         transaction;true=success;false=failure
	 * @throws ApplicationException if Exception occurs
	 */
	public Boolean updateEntryTypeDetails(SqlSession aoMybatisSession, HashMap<String, String> aoHashMap)
			throws ApplicationException
	{
		Boolean loStatusFlag = Boolean.FALSE;
		try
		{
			if (HHSConstants.BMC_NEWFY_CONFIG_TASK.equals(aoHashMap.get(HHSConstants.SCREEN_NAME)))
			{
				aoHashMap.put(HHSConstants.BUDGET_ID, (String) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.GET_BUDGET_FROM_CONTRACT_AND_FY,
						HHSConstants.JAVA_UTIL_HASH_MAP));
			}
			if (Boolean.valueOf(aoHashMap.get(HHSConstants.IS_CHECKED)))
			{
				if (HHSConstants.CONTRACT_CONFIG_UPDATE.equals(aoHashMap.get(HHSConstants.SCREEN_NAME)))
				{
					DAOUtil.masterDAO(aoMybatisSession, aoHashMap, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.INSERT_BUDGET_CUSTOMIZATION_FOR_UPDATE, HHSConstants.JAVA_UTIL_HASH_MAP);
				}
				else
				{
					aoHashMap.put(HHSConstants.PUBLISHED, HHSConstants.ZERO);
					DAOUtil.masterDAO(aoMybatisSession, aoHashMap, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.INSERT_BUDGET_CUSTOMIZATION, HHSConstants.JAVA_UTIL_HASH_MAP);
				}

				loStatusFlag = Boolean.TRUE;
			}
			else
			{
				// it's unChecked
				if (HHSConstants.CONTRACT_CONFIG_UPDATE.equals(aoHashMap.get(HHSConstants.SCREEN_NAME)))
				{
					DAOUtil.masterDAO(aoMybatisSession, aoHashMap, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.DELETE_BUDGET_CUSTOMIZATION_FOR_UPDATE, HHSConstants.JAVA_UTIL_HASH_MAP);
				}
				else
				{
					DAOUtil.masterDAO(aoMybatisSession, aoHashMap, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.DELETE_BUDGET_CUSTOMIZATION, HHSConstants.JAVA_UTIL_HASH_MAP);
				}
				loStatusFlag = Boolean.TRUE;
			}
			setMoState("sucessfully executed updateEntryTypeDetails method");
		}
		catch (ApplicationException loExp)
		{
			loExp.addContextData(HHSConstants.CONTRACTID, aoHashMap);
			LOG_OBJECT.Error("Exception occured in FinancialsService: updateEntryTypeDetails method:: ", loExp);
			setMoState("ApplicationException while executing updateEntryTypeDetails method ");
			throw loExp;

		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Exception occured in FinancialsService: updateEntryTypeDetails method:: ", loEx);
			setMoState("Exception while executing updateEntryTypeDetails method ");
			throw new ApplicationException("Exception while executing updateEntryTypeDetails method", loEx);

		}
		return loStatusFlag;
	}

	/**
	 * <ul>
	 * <li>This method is used to fetch data from budgetCustomization table It
	 * use to know the check/uncheck of EntityTypeId for respective contract</li>
	 * <li>If calls the query 'fetchEntryTypeDetailsForUpdateContract'</li>
	 * <li>calls the query 'fetchEntryTypeDetailsForContractUpdateLanding'</li>
	 * <li>calls the query 'fetchEntryTypeDetailsForAmendment'</li>
	 * <li>calls the query 'fetchEntryTypeDetailsFromModification'</li>
	 * <li>calls the query 'fetchEntryTypeDetails'</li>
	 * <li>Method Added in R4</li>
	 * </ul>
	 * @param aoMyBatisSession holds the SQL session to do DB operations
	 * @param aoHashMap holds the parameter for budgetCustomization table
	 * @return List<String> return type holds the EntryTypeList
	 * @throws ApplicationException throws Application Exception.
	 */
	@SuppressWarnings("unchecked")
	public List<String> getLineItemsState(SqlSession aoMybatisSession, HashMap<String, String> aoHashMap)
			throws ApplicationException
	{
		List<String> loEntryType = null;
		try
		{
			if (HHSConstants.CONTRACT_CONFIG_UPDATE.equals(aoHashMap.get(HHSConstants.SCREEN_NAME)))
			{
				aoHashMap.put(HHSConstants.CONTRACT_TYPE_ID, HHSConstants.FOUR);
				loEntryType = (List<String>) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.FETCH_ENTRY_TYPE_DETAILS_FOR_UPDATE_CONTRACT, HHSConstants.JAVA_UTIL_HASH_MAP);
			}
			else if (HHSConstants.UPDATE_LANDING_JSP.equals(aoHashMap.get(HHSConstants.SCREEN_NAME))
					|| HHSConstants.UPDATE_REVIEW_TASK_JSP.equals(aoHashMap.get(HHSConstants.SCREEN_NAME))
					|| HHSConstants.TASK_BUDGET_AMENDMENT.equals(aoHashMap.get(HHSConstants.SCREEN_NAME)))
			{
				loEntryType = (List<String>) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.FETCH_ENTRY_TYPE_DETAILS_FOR_CONTRACT_UPDATE_LANDING,
						HHSConstants.JAVA_UTIL_HASH_MAP);
			}
			else if (HHSConstants.TASK_AMENDMENT_CONFIGURATION.equals(aoHashMap.get(HHSConstants.SCREEN_NAME)))
			{
				loEntryType = (List<String>) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.FETCH_ENTRY_TYPE_DETAILS_FOR_AMENDMENT, HHSConstants.JAVA_UTIL_HASH_MAP);
			}
			else if (HHSConstants.AUDIT_CONTRACT_BUDGET_MODIFICATION.equals(aoHashMap.get(HHSConstants.SCREEN_NAME)))
			{
				loEntryType = (List<String>) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.FETCH_ENTRY_TYPE_DETAILS_FROM_MODIFICATION, HHSConstants.JAVA_UTIL_HASH_MAP);
			}
			else
			{
				loEntryType = (List<String>) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.FETCH_ENTRY_TYPE_DETAILS,
						HHSConstants.JAVA_UTIL_HASH_MAP);
			}
			if (loEntryType.size() == HHSConstants.INT_ZERO && aoHashMap.get(HHSConstants.SCREEN_NAME) != null)
			{
				loEntryType.addAll(HHSConstants.ENTRY_TYPE_PUBLISHED);
			}
			setMoState("sucessfully executed getLineItemsState method");
		}
		catch (ApplicationException loExp)
		{
			loExp.addContextData(HHSConstants.CONTRACTID, aoHashMap);
			LOG_OBJECT.Error("Exception occured in FinancialsService: getLineItemsState method:: ", loExp);
			setMoState("ApplicationException while executing getLineItemsState method ");
			throw loExp;
		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Exception occured in FinancialsService: getLineItemsState method:: ", loEx);
			setMoState("Exception while executing getLineItemsState method ");
			throw new ApplicationException("Exception while executing getLineItemsState method", loEx);
		}
		return loEntryType;
	}

	/**
	 * Start: R7 defect 8644: part 3 update value of column
	 * REQUEST_PARTIAL_MERGE=1 in the table contract.
	 */
	
	public Boolean updateRequestMRFlag(SqlSession aoMybatisSession, String asContractId) throws ApplicationException
	{
		Boolean lbMRStatus = Boolean.FALSE;
		try
		{
			DAOUtil.masterDAO(aoMybatisSession, asContractId, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSConstants.UPDATE_REQUEST_PARTIAL_MERGE_CONTRACT_LIST, HHSConstants.JAVA_LANG_STRING);
			lbMRStatus = Boolean.TRUE;
			setMoState("sucessfully updated the request for partial merge method");
		}
		catch (ApplicationException loExp)
		{
			loExp.addContextData(HHSConstants.CONTRACTID, asContractId);
			LOG_OBJECT.Error("Exception occured in FinancialsService: updateRequestMRFlag method:: ", loExp);
			setMoState("ApplicationException while executing updateRequestMRFlag method ");
			throw loExp;

		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Exception occured in FinancialsService: updateRequestMRFlag method:: ", loEx);
			setMoState("Exception while executing updateRequestMRFlag method ");
			throw new ApplicationException("Exception while executing updateRequestMRFlag method", loEx);

		}
		
		return lbMRStatus;
	}
	
	// End: R7 defect 8644 Part 3
	
	/**
	 * This method is used to update the contract's EntryTyeID for
	 * budgetCustomization table It update the EntryTypeId for contract <br>
	 * calls the query 'updateBudgetCustomization'
	 * @param aoMyBatisSession holds the SQL session to do DB operations
	 * @param aoHashMap holds the parameter for budgetCustomization table
	 * @return boolean return type holds the result of
	 *         transaction;true=success;false=failure
	 * @throws ApplicationException
	 * 
	 */
	public Boolean publishEntryTypeDetails(SqlSession aoMybatisSession, HashMap<String, String> aoHashMap)
			throws ApplicationException
	{
		Boolean lbStatusFlag = Boolean.FALSE;
		try
		{
			DAOUtil.masterDAO(aoMybatisSession, aoHashMap, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.UPDATE_BUDGET_CUSTOMIZATION, HHSConstants.JAVA_UTIL_HASH_MAP);
			lbStatusFlag = Boolean.TRUE;
			setMoState("sucessfully executed publishEntryTypeDetails method");
		}
		catch (ApplicationException loExp)
		{
			loExp.addContextData(HHSConstants.CONTRACTID, aoHashMap);
			LOG_OBJECT.Error("Exception occured in FinancialsService: publishEntryTypeDetails method:: ", loExp);
			setMoState("ApplicationException while executing publishEntryTypeDetails method ");
			throw loExp;

		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Exception occured in FinancialsService: publishEntryTypeDetails method:: ", loEx);
			setMoState("Exception while executing publishEntryTypeDetails method ");
			throw new ApplicationException("Exception while executing publishEntryTypeDetails method", loEx);

		}
		return lbStatusFlag;
	}

	/**
	 * <ul>
	 * <li>This method is used to insert the Contract Budget Customization of
	 * Update Contract Budget Customization to base Contract.</li>
	 * <li>calls the query 'fetchEntryTypeDetails'</li>
	 * <li>calls the query 'mergeBudCustomizationCBUpdateReview'</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession Object
	 * @param aoHashMap HashMap Object
	 * @return lbCopyDataToCurrentYear Boolean holds the result of
	 *         transaction;true=success;false=failure
	 * @throws ApplicationException ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public Boolean mergeBaseActiveBudget(SqlSession aoMybatisSession, HashMap<String, String> aoHashMap)
			throws ApplicationException
	{
		Boolean lbStatusFlag = Boolean.FALSE;
		List<String> loEntryType = null;
		try
		{
			loEntryType = (List<String>) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.FETCH_ENTRY_TYPE_DETAILS,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			for (int liCount = 0; liCount < loEntryType.size(); liCount++)
			{
				aoHashMap.put(HHSConstants.ENTRY_TYPE_ID, loEntryType.get(liCount).split(HHSConstants.COLON)[0]);
				DAOUtil.masterDAO(aoMybatisSession, aoHashMap, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.MERGE_BUDGET_CUSTOMIZATION_CONTRACT_BUDGET_UPDATE_REVIEW,
						HHSConstants.JAVA_UTIL_HASH_MAP);
			}
			lbStatusFlag = Boolean.TRUE;
		}
		catch (ApplicationException loExp)
		{
			loExp.addContextData(HHSConstants.CONTRACTID, aoHashMap);
			LOG_OBJECT.Error("Exception occured in FinancialsService: processContractAfterAmendmentTask method:: ",
					loExp);
			setMoState("ApplicationException while executing processContractAfterAmendmentTask method ");
			throw loExp;

		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Exception occured in FinancialsService: processContractAfterAmendmentTask method:: ",
					loEx);
			setMoState("Exception while executing processContractAfterAmendmentTask method ");
			throw new ApplicationException("Exception while executing processContractAfterAmendmentTask method", loEx);

		}
		return lbStatusFlag;
	}

	/**
	 * Created this method as part of release 3.6.0 Enhancement id 6484
	 * @param aoMybatisSession SQL session as input.
	 * @param aoHashMap Map as input.
	 * @throws ApplicationException Application Exception in case of application
	 *             exception.
	 */
	@SuppressWarnings("unchecked")
	public Boolean mergeSubBudetSite(SqlSession aoMybatisSession, TaskDetailsBean aoTaskDetailsBean,
			Boolean loFinalFinish) throws ApplicationException
	{
		Boolean loStatus = false;
		//3.6.0 enhancement id 6484
		try
		{
			if(loFinalFinish)
			{
				ArrayList<SiteDetailsBean> loSubBudgetDetails = (ArrayList<SiteDetailsBean>) DAOUtil.masterDAO(
						aoMybatisSession, aoTaskDetailsBean.getBudgetId(),
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.FETCH_SUB_BUDGET_DETAILS_FOR_BUDGET_UPDATE_TASK, HHSConstants.JAVA_LANG_STRING);
				for(SiteDetailsBean loSiteDetailsBean : loSubBudgetDetails)
				{
					loSiteDetailsBean.setModifiedBy(aoTaskDetailsBean.getUserId());
					DAOUtil.masterDAO(aoMybatisSession, loSiteDetailsBean,
							HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.SOFT_DELETE_PARENT,
							HHSConstants.COM_NYC_HHS_MODEL_SITEDETAILSBEAN);
					DAOUtil.masterDAO(aoMybatisSession, loSiteDetailsBean,
							HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.UPDATE_PARENT_SUB_BUDGET_SITE,
							HHSConstants.COM_NYC_HHS_MODEL_SITEDETAILSBEAN);
				}
			}
		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Exception occured while merging sub buddget ", aoAppExp);
			setMoState("Exception occured while merging sub buddget");
			throw aoAppExp;
		}
		loStatus = true;
		return loStatus;
	}

	/**
	 * This method check id amendment docs have been generated or not.
	 * @param aoMyBatisSession batis session as input
	 * @param aoContractFilterBean bean as input
	 * @return loSuccess true in case of success
	 * @throws ApplicationException Exception in case as query fails.
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public Boolean pdfAmendmetDocGenerated(SqlSession aoMyBatisSession, ContractList aoContractFilterBean)
			throws ApplicationException
	{
		Integer loPDFAmendmentCofGenerated = 0;

		HashMap loDataMap = new HashMap();
		Boolean loSuccess = false;
		try
		{
			loDataMap.put(HHSConstants.NEW_ENTITY_TYPE, HHSConstants.CONTRACT_AMENDMENT);
			loDataMap.put(HHSConstants.SUB_ENTITY_TYPE, HHSConstants.BUDGET_TYPE1);
			loDataMap.put(HHSConstants.NEW_ENTITY_ID, aoContractFilterBean.getContractId());
			loDataMap.put(HHSConstants.SUB_ENTITY_ID, aoContractFilterBean.getBudgetId());
			loDataMap.put(HHSConstants.STATUS_COLUMN,
					PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.PDF_GENERATED));
			loPDFAmendmentCofGenerated = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loDataMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.IS_AMENDMENT_COF_PDF_GENERATED,
					HHSConstants.JAVA_UTIL_HASH_MAP);

			if (loPDFAmendmentCofGenerated > 0)
			{
				loSuccess = true;
			}
		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Exception occured in FinancialsService: pdfAmendmetDocGenerated method:: ", loEx);
			setMoState("Exception while executing pdfAmendmetDocGenerated method ");
			throw new ApplicationException("Exception while executing pdfAmendmetDocGenerated method", loEx);

		}
		return loSuccess;
	}

	/**
	 * <ul>
	 * <li>This method do check before downloading amendment document and
	 * changing the status to "sent for registration".</li>
	 * <li>calls the query 'isStatusSentForReg'</li>
	 * <li>calls the query 'sentForRegOrCanCheck'</li>
	 * <li>calls the query 'negAmendRegOrCanCheck'</li>
	 * <li>calls the query 'updateStatusToSentForReg'</li>
	 * </ul>
	 * @param aoMyBatisSession sql session as input.
	 * @param aoContractFilterBean bean as input.
	 * @return loAmendMap map that contain error message
	 * @throws ApplicationException Exception in case query fails.
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public HashMap sentForRegOrCanCheck(SqlSession aoMyBatisSession, ContractList aoContractFilterBean)
			throws ApplicationException
	{
		HashMap loAmendMap = new HashMap();
		try
		{
			Integer loItselfSentForReg = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoContractFilterBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.IS_STATUS_SENT_FOR_REG,
					HHSConstants.COM_NYC_HHS_MODEL_CONTRACT_LIST);

			if (loItselfSentForReg == 0)
			{
				Integer loSentForRegCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoContractFilterBean,
						HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.SENT_FOR_REG_OR_CAN_CHECK,
						HHSConstants.COM_NYC_HHS_MODEL_CONTRACT_LIST);
				if (loSentForRegCount > 0)
				{
					loAmendMap.put(HHSConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.SENT_FOR_REG_OR_CAN_CHECK));
				}
				else
				{
					Integer loNegPAPRCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoContractFilterBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
							HHSConstants.NEGATIVE_AMENDMENT_REG_OR_CAN_CHECK,
							HHSConstants.COM_NYC_HHS_MODEL_CONTRACT_LIST);
					if (loNegPAPRCount > 0)
					{
						loAmendMap.put(HHSConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
								HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
								HHSConstants.NEGATIVE_AMENDMENT_REG_OR_CAN_CHECK));
					}
					else
					{
						DAOUtil.masterDAO(aoMyBatisSession, aoContractFilterBean,
								HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.UPDATE_STATUS_TO_SENT_FOR_REG,
								HHSConstants.COM_NYC_HHS_MODEL_CONTRACT_LIST);
					}
				}
			}
		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Exception occured while checking status is not sent for registeration for any amendment",
					aoAppExp);
			setMoState("Exception occured while checking status is not sent for registeration for any amendment");
			throw aoAppExp;
		}
		return loAmendMap;
	}

	/**
	 * This method gets Document title from DB <br>
	 * 
	 * @param aoMyBatisSession SqlSession Object
	 * @param aoContractFilterBean ContractList Object
	 * @return String Object
	 * @throws ApplicationException If query fails
	 */
	public String getDocumentTitle(SqlSession aoMyBatisSession, ContractList aoContractFilterBean)
			throws ApplicationException
	{
		StringBuffer lsDocumentsTitle = new StringBuffer(HHSConstants.STR);
		List<String> loBudgetIds;
		String lsBudgetIds = (String) DAOUtil.masterDAO(aoMyBatisSession, aoContractFilterBean,
				HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.GET_BUDGET_IDS_FROM_CONTRACT_ID,
				HHSConstants.COM_NYC_HHS_MODEL_CONTRACT_LIST);
		if(lsBudgetIds !=null)
		{
			loBudgetIds = Arrays.asList(lsBudgetIds.split(HHSConstants.COMMA));
			for (String lsBudgetId : loBudgetIds)
			{
				lsDocumentsTitle.append(HHSConstants.CONTRACT_BUDGET_AMENDMENT_SUMMARY_DETAILS);
				lsDocumentsTitle.append(HHSConstants.UNDERSCORE);
				lsDocumentsTitle.append(lsBudgetId);
				lsDocumentsTitle.append(HHSConstants.STR);
				lsDocumentsTitle.append(HHSConstants.COMMA);
				lsDocumentsTitle.append(HHSConstants.STR);
			}
		}
		lsDocumentsTitle.append(HHSConstants.CONTRACT_CERTIFICATION_FUND_AMENDMENT);
		lsDocumentsTitle.append(HHSConstants.UNDERSCORE);
		lsDocumentsTitle.append(aoContractFilterBean.getContractId());
		lsDocumentsTitle.append(HHSConstants.STR);
		return lsDocumentsTitle.toString();
	}

	/**
	 * This is method will fetch the list of Document associated with the
	 * organization
	 * 
	 * @param loHmProps 
	 * @param asOrgId
	 * @param asOrgName
	 * @return
	 * @throws ApplicationException
	 */
	public List<Map<String, String>> getFinancialsListOfMap(HashMap<String, Map<String, String>> loHmProps,
			String asOrgId, String asOrgName) throws ApplicationException
	{
		List<Map<String, String>> loFinalDocumentList = new ArrayList<Map<String, String>>();
		Map<String, String> loFinalMap = null;
		if (null != loHmProps)
		{
			for (Entry<String, Map<String, String>> loEntry : loHmProps.entrySet())
			{
				String lsIterator = loEntry.getKey();
				Map<String, String> loRequiredParamMap = loEntry.getValue();
				String lsContractTitle = loRequiredParamMap.get(P8Constants.PROPERTY_CE_CONTRACT_TITLE);
				loFinalMap = new HashMap<String, String>();
				loFinalMap.put(HHSConstants.ORGANIZATION_NAME, asOrgName);
				loFinalMap.put(HHSConstants.DOCUMENT_IDENTIFIER_ID, lsIterator);
				loFinalMap.put(HHSConstants.PROP_TITLE, lsContractTitle);
				loFinalMap.put(HHSConstants.DOC_PROPOSAL_ID,
						loRequiredParamMap.get(P8Constants.PROPERTY_CE_CONTRACT_ID));
				loFinalMap.put(HHSConstants.FILE_DOCUMENT_TITLE,
						loRequiredParamMap.get(P8Constants.PROPERTY_CE_DOCUMENT_TITLE));
				loFinalMap.put(HHSConstants.ORGANIZATION_ID_KEY, asOrgId);
				loFinalDocumentList.add(loFinalMap);
			}
		}
		return loFinalDocumentList;
	}

	/**
	 * Get base budget in from invoice id for agency interface module.<br>
	 * calls the query 'getBaseBudgetIdFromInvoiceId'
	 * @param aoMyBatisSession sql session as input
	 * @param aoBudgetId base budget id as input
	 * @param loFinalFinish to merge on final finish
	 * @return aoBudgetId base budget id as output
	 * @throws ApplicationException Exception in case of query failure
	 * 
	 * 
	 */
	public String getBaseBudgetIdFromInvoiceId(SqlSession aoMyBatisSession, String asInvoiceId, Boolean loFinalFinish)
			throws ApplicationException
	{
		String lsBudgetId = null;
		try
		{
			if (loFinalFinish)
			{
				lsBudgetId = (String) DAOUtil.masterDAO(aoMyBatisSession, asInvoiceId,
						HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.GET_BASE_BUDGET_ID_FROM_INVOICE_ID,
						HHSConstants.JAVA_LANG_STRING);
			}
		}
		catch (ApplicationException aoExp)
		{
			setMoState("Exception while getting base budget id from invoice id");
			LOG_OBJECT.Error("Exception while  getting base budget id from invoice id" + lsBudgetId, aoExp);
			throw aoExp;
		}
		return lsBudgetId;
	}
	//Updated in R7 for Cost-Center
	/**
	 * This method is for module agency interface. This method update ytd and
	 * remaining amount for line items on various scenarios: <br>
	 * calls the query 'updateLineItemsYTDAndRemainingForAgencyInterface'
	 * Updates SERVICES and COST_CENTER remaining amount.
	 * @param aoMyBatisSession sql session as input
	 * @param aoBudgetId base budget id as input
	 * @param loFinalFinish to merge on final finish
	 * @param asUserId - UserId
	 * @return lbSuccessfullyUpdated success status as output
	 * @throws ApplicationException Exception in case of query failure
	 * 
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public boolean updateYtdAndRemainingAmountInLineItems(SqlSession aoMyBatisSession, String aoBudgetId,
			Boolean loFinalFinish,String asUserId) throws ApplicationException
	{
		boolean lbSuccessfullyUpdated = false;
		HashMap loHashMap = new HashMap<String, String>();
		try
		{
			if (loFinalFinish && aoBudgetId != null)
			{
				String[] loAffectedBudgetBaseIds = aoBudgetId.split(HHSConstants.COMMA);
				loHashMap.put(HHSConstants.TT_USERID, asUserId);
				for (String lsTableName : HHSConstants.UPDATE_LINE_ITEMS_YTD_INVOICE_AND_REMAINING_AMOUNT_FOR_AGENCY_INTERFACE)
				{
					loHashMap.put(HHSConstants.ID, HHSConstants.GET_ID_DETAILS.get(lsTableName));
					loHashMap.put(HHSConstants.TABLE, lsTableName);
					loHashMap.put(HHSConstants.INVOICE_ENTRY_TYPE_ID,
							HHSConstants.GET_ENTRY_TYPE_ID_DETAILS.get(lsTableName));
					loHashMap.put(HHSConstants.AMOUNT, HHSConstants.GET_AMOUNT_DETAILS.get(lsTableName));
					for (String lsBudgetId : loAffectedBudgetBaseIds)
					{
						loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, lsBudgetId);
						DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
								HHSConstants.UPDATE_LINE_ITEMS_YTD_AND_REMAINING_AMOUNT_FOR_AGENCY_INTERFACE_QUERY,
								HHSConstants.JAVA_UTIL_HASH_MAP);
					}
				}
				for (String lsBudgetId : loAffectedBudgetBaseIds)
				{
					loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, lsBudgetId);
					DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
							HHSConstants.UPDATE_LINE_ITEMS_YTD_AND_REMAINING_UNITS_FOR_RATE,
							HHSConstants.JAVA_UTIL_HASH_MAP);
					//Start:Added in R7 for Cost-Center
					DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
							HHSR5Constants.UPDATE_LINE_ITEMS_YTD_AND_REMAINING_AMOUNT_UNITS_SERVICES,
							HHSConstants.JAVA_UTIL_HASH_MAP);
					DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
							HHSR5Constants.UPDATE_LINE_ITEMS_YTD_AND_REMAINING_AMOUNT_COSTCENTER,
							HHSConstants.JAVA_UTIL_HASH_MAP);
					//End:Added in R7 for Cost-Center
				}
				lbSuccessfullyUpdated = true;
			}
		}
		catch (ApplicationException aoExp)
		{
			setMoState("Exception while updating ytd and remaining amount column for line items.");
			LOG_OBJECT.Error("Exception while updating ytd and remaining amount column for line items for budget id"
					+ aoBudgetId, aoExp);
			throw aoExp;
		}
		return lbSuccessfullyUpdated;
	}

	/**
	 * <ul>
	 * <li>This method is for module agency interface. This method update ytd
	 * and remaining amount for budget and sub budget.</li>
	 * <li>calls the query 'updateSubBudgetsYTDAndRemainingForAgencyInterface'</li>
	 * <li>calls the query 'updateBudgetsYTDAndRemainingForAgencyInterface'</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession sql session as input
	 * @param asBudgetId base budget id as input
	 * @return lbSuccessfullyUpdated success status as output
	 * @throws ApplicationException Exception in case of query failure
	 * 
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public boolean updateYtdAndRemainingAmountInBudgetAndSubBudget(SqlSession aoMyBatisSession, String aoBudgetId,
			Boolean loFinalFinish,String asUserId) throws ApplicationException
	{
		boolean lbSuccessfullyUpdated = false;
		HashMap loHashMap = new HashMap<String, String>();
		try
		{
			if (loFinalFinish && aoBudgetId != null)
			{
				String[] loAffectedBudgetBaseIds = aoBudgetId.split(HHSConstants.COMMA);
				loHashMap.put(HHSConstants.TT_USERID, asUserId);
				loHashMap.put(HHSConstants.MODIFY_BY, asUserId);
				for (String lsBudgetId : loAffectedBudgetBaseIds)
				{
					loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, lsBudgetId);
					Integer loBudgetUpdateCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
							HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
							HHSConstants.UPDATE_SUB_BUDGETS_YTD_AND_REMAINING_AMOUNT_FOR_AGENCY_INTERFACE_QUERY,
							HHSConstants.JAVA_UTIL_HASH_MAP);
					Integer loSubBudgetUpdateCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
							HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
							HHSConstants.UPDATE_BUDGETS_YTD_AND_REMAINING_AMOUNT_FOR_AGENCY_INTERFACE_QUERY,
							HHSConstants.JAVA_UTIL_HASH_MAP);
					// START || Added as a part of release 3.12.0 for
					// enhancement request 6643
					if(loBudgetUpdateCount==0)
					{
						DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
								HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
							HHSConstants.UPDATE_SUB_BUDGET_YTD_INVOICE_AMOUNT, HHSConstants.JAVA_UTIL_MAP);
					}
					if(loSubBudgetUpdateCount==0)
					{
						DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
								HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
							HHSConstants.UPDATE_BUDGET_YTD_INVOICE_AMOUNT, HHSConstants.JAVA_UTIL_MAP);
					}
					// START || Added as a part of release 3.12.0 for
					// enhancement request 6643
					lbSuccessfullyUpdated = true;
				}
			}
		}
		catch (ApplicationException aoExp)
		{
			setMoState("Exception while updating ytd and remaining amount column for budget and sub budget");
			LOG_OBJECT
					.Error("Exception while updating ytd and remaining amount column in budget and sub budget for budget id"
							+ aoBudgetId);
			throw aoExp;
		}
		return lbSuccessfullyUpdated;
	}

	/**
	 * 
	 * This method update the status of a filenet document in db on the basis of
	 * id provided <br>
	 * calls the query 'updateBulkUploadDocStatus'
	 * @param aoMyBatisSession- SQL session
	 * @param asDocId--defined id of the document
	 * @param aoFileStatus-status to be updated for file
	 * @return
	 * @throws ApplicationException
	 */
	public boolean updateDocumentStatusInDB(SqlSession aoMyBatisSession, String asDocId, String aoFileStatus)
			throws ApplicationException
	{
		boolean lbStatus = false;
		HashMap<String, String> loReqdMap = new HashMap<String, String>();

		try
		{
			loReqdMap.put(HHSConstants.BULK_UPLOAD_FILE_STATUS_DOCUMENT_STATUS, aoFileStatus);
			loReqdMap.put(HHSConstants.BULK_UPLOAD_DOC_ID, asDocId);
			loReqdMap.put(HHSConstants.BULK_UPLOAD_FILE_MODIFIED_BY, HHSConstants.BULK_UPLOAD_SYSTEM_USER);

			DAOUtil.masterDAO(aoMyBatisSession, loReqdMap, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSConstants.BULK_UPLOAD_FILE_STATUS_UPDATE, HHSConstants.JAVA_UTIL_HASH_MAP);
			lbStatus = true;
		}
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("Exception occured in FinancialsService: updateDocumentStatusInDB method:: ", loExp);
			setMoState("ApplicationException while executing updateDocumentStatusInDB method ");
			ApplicationException loEx = new ApplicationException("Error occured while executing service.", loExp);
			throw loEx;
		}
		return lbStatus;
	}

	/**
	 * 
	 * This update that lock status of a file so that in case some othere thread
	 * is trying to process this document,
	 * 
	 * the thread has to wait for the time period configured <br>
	 * calls the query 'updateBulkUploadDocLockStatus'
	 * 
	 * @param aoMyBatisSession- SQL Session
	 * @param asDocId- id of the document whose status need to be updated.
	 * @return
	 * @throws ApplicationException
	 * 
	 */
	public boolean updateBulkUploadDocLockStatus(SqlSession aoMyBatisSession, String asDocId)
			throws ApplicationException
	{
		boolean lbStatus = false;
		HashMap<String, String> loReqdMap = new HashMap<String, String>();

		try
		{
			loReqdMap.put(HHSConstants.BULK_UPLOAD_DOC_ID, asDocId);
			loReqdMap.put(HHSConstants.BULK_UPLOAD_FILE_MODIFIED_BY, HHSConstants.BULK_UPLOAD_SYSTEM_USER);

			DAOUtil.masterDAO(aoMyBatisSession, loReqdMap, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSConstants.BULK_UPLOAD_FILE_LOCK_STATUS_UPDATE, HHSConstants.JAVA_UTIL_HASH_MAP);
			lbStatus = true;
		}
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("Exception occured in FinancialsService: updateDocumentStatusInDB method:: ", loExp);
			setMoState("ApplicationException while executing updateDocumentStatusInDB method ");
			ApplicationException loEx = new ApplicationException("Error occured while executing service.", loExp);
			throw loEx;
		}
		return lbStatus;
	}

	/**
	 * This method check whether contract id open ended rfp and contract start
	 * and end date is null. <br>
	 * calls the query 'isOpenEndedRfpStartEndDateNotSet'
	 * @param aoMyBatisSession sql session as input
	 * @param asContractId contract id as input
	 * @return Boolean true in case start end date is null
	 * @throws ApplicationException Exception in case of query failre.
	 * 
	 * 
	 */
	public Boolean openEndedRfpStartEndDateNotSet(SqlSession aoMyBatisSession, String asContractId)
			throws ApplicationException
	{
		Boolean loIsOpenEndedRfpStartEndDateNotSet = false;
		try
		{
			Integer loIsOpenEndedRfpStartEndDateNotSetCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession,
					asContractId, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSConstants.IS_OPEN_ENDED_RFP_START_END_DATE_NOT_SET, HHSConstants.JAVA_LANG_STRING);
			if (loIsOpenEndedRfpStartEndDateNotSetCount != 0)
			{
				loIsOpenEndedRfpStartEndDateNotSet = true;
			}
		}
		catch (ApplicationException loAppExp)
		{
			loAppExp.addContextData(HHSConstants.CONTRACTID, asContractId);
			LOG_OBJECT.Error("Exception occured in FinancialsService: openEndedRfpStartEndDateNotSet method:: ",
					loAppExp);
			setMoState("ApplicationException while executing openEndedRfpStartEndDateNotSet method ");
			throw loAppExp;
		}
		return loIsOpenEndedRfpStartEndDateNotSet;
	}

	/**
	 * This method udpate contract start and end date in case of opend ended
	 * rfp.<br>
	 * Calls the query 'updateContractStartEndDateForOpenEndedRfp'
	 * @param aoMyBatisSession sql session as input
	 * @param loContractDetails contract details hashmap as input
	 * @return Boolean true in case update is success
	 * @throws ApplicationException exception in case a query fails.
	 * 
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public Boolean updateContractStartEndDateForOpenEndedRfp(SqlSession aoMyBatisSession, HashMap loContractDetails)
			throws ApplicationException
	{
		Boolean loUpdatedContractStartEndDateForOpenEndedRfp = false;
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, loContractDetails, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSConstants.UPDATE_CONTRACT_START_END_DATE_FOR_OPEN_ENDED_RFP, HHSConstants.JAVA_UTIL_HASH_MAP);
			loUpdatedContractStartEndDateForOpenEndedRfp = true;
		}
		catch (ApplicationException loAppExp)
		{
			loAppExp.addContextData(HHSConstants.LO_CONTRACT_DETAILS, loContractDetails);
			LOG_OBJECT.Error("Exception occured in FinancialsService: openEndedRfpStartEndDateNotSet method:: ",
					loAppExp);
			setMoState("ApplicationException while executing openEndedRfpStartEndDateNotSet method ");
			throw loAppExp;
		}
		return loUpdatedContractStartEndDateForOpenEndedRfp;
	}

	/**
	 * This method is used to insert the Contract Budget Customization of Update
	 * Contract Budget Customization to base Contract.<br>
	 * calls the query 'insertBudgetCustomization' Updated in R7: Program Income
	 * to delete default program income entries if Program income is selected as
	 * category.
	 * @param aoMybatisSession SqlSession Object
	 * @param aoHashMap HashMap Object
	 * @return lbStatusFlag Boolean holds the result of
	 *         transaction;true=success;false=failure
	 * @throws ApplicationException ApplicationException object
	 * 
	 * 
	 */
	public Boolean updateBudgetTemplate(SqlSession aoMybatisSession, HashMap<String, String> aoHashMap)
			throws ApplicationException
	{
		Boolean lbStatusFlag = Boolean.FALSE;
		try
		{
			String[] loEntryTypeList = aoHashMap.get(HHSConstants.ENTRY_TYPE_LIST).split(HHSConstants.COMMA);
			for (int liCount = HHSConstants.INT_ZERO; liCount < loEntryTypeList.length; liCount++)
			{
				/*
				 * Start: R7 Program Income changes - Delete default PI entries
				 * from program income table if entry list contains 11
				 */
				if (HHSConstants.STRING_ELEVEN.equals(loEntryTypeList[liCount]))
				{
					
					ContractBudgetService loContractBudgetService = new ContractBudgetService();
					TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
					
					loTaskDetailsBean.setContractId(aoHashMap.get(HHSConstants.CONTRACT_ID));
					loTaskDetailsBean.setEntityId(aoHashMap.get(HHSConstants.CONTRACT_ID));
					loTaskDetailsBean.setAssignedTo(aoHashMap.get(HHSConstants.CREATED_BY_USER_ID));
					loTaskDetailsBean.setStartFiscalYear(aoHashMap.get(HHSConstants.FISCAL_YEAR_ID));
                      
					// If there is no amendment beyond pending COF delete default entries and update flag
					loContractBudgetService.validateAndDeleteDefaultPI(aoMybatisSession, loTaskDetailsBean,
							HHSR5Constants.BOOLEAN_TRUE);
				}
				
				//End: R7 Program Income
				aoHashMap.put(HHSConstants.ENTRY_TYPE_ID, loEntryTypeList[liCount]);
				DAOUtil.masterDAO(aoMybatisSession, aoHashMap, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.INSERT_BUDGET_CUSTOMIZATION, HHSConstants.JAVA_UTIL_HASH_MAP);
				
			}
			lbStatusFlag = Boolean.TRUE;
		}
		catch (ApplicationException loExp)
		{
			loExp.addContextData(HHSConstants.CONTRACTID, aoHashMap);
			LOG_OBJECT.Error("Exception occured in FinancialsService: updateBudgetTemplate method:: ", loExp);
			setMoState("ApplicationException while executing updateBudgetTemplate method ");
			throw loExp;

		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Exception occured in FinancialsService: updateBudgetTemplate method:: ", loEx);
			setMoState("Exception while executing updateBudgetTemplate method ");
			throw new ApplicationException("Exception while executing updateBudgetTemplate method", loEx);

		}
		return lbStatusFlag;
	}

	/**
	 * This method is used to update PDF Entry of Amendment Contract Budget in
	 * DB.
	 * @param aoMybatisSession SqlSession - Object
	 * @param aoTaskDetailsBean TaskDetailsBean - Object
	 * @param aoFinalFininsh Boolean - Object
	 * @return loSuccess Boolean - Object
	 * @throws ApplicationException ApplicationException - Object
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public Boolean updatePdfEntryBudgetAmendment(SqlSession aoMybatisSession, TaskDetailsBean aoTaskDetailsBean,
			Boolean aoFinalFininsh) throws ApplicationException
	{

		Boolean loIsSuccess = false;
		HashMap loDataMap = new HashMap();
		try
		{
			if (aoFinalFininsh)
			{
				// Inserting rows in PDF_BATCH_DETAILS for budget amendment
				// summary
				loDataMap.put(HHSConstants.NEW_ENTITY_TYPE, HHSConstants.CONTRACT_AMENDMENT);
				loDataMap.put(HHSConstants.SUB_ENTITY_TYPE, HHSConstants.BUDGET_TYPE1);
				loDataMap.put(HHSConstants.NEW_ENTITY_ID, aoTaskDetailsBean.getContractId());
				loDataMap.put(HHSConstants.SUB_ENTITY_ID, aoTaskDetailsBean.getBudgetId());
				loDataMap.put(HHSConstants.TT_USERID, aoTaskDetailsBean.getUserId());
				loDataMap.put(HHSConstants.STATUS_COLUMN, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.PDF_NOT_STARTED));
				DAOUtil.masterDAO(aoMybatisSession, loDataMap, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.INSERT_PDF_BACTH_FOR_AMENDMENT_COF, HHSConstants.JAVA_UTIL_HASH_MAP);
				loIsSuccess = true;
			}
		}
		catch (ApplicationException loAppExp)
		{
			loAppExp.addContextData(HHSConstants.CONTRACTID, loDataMap);
			LOG_OBJECT.Error("Exception occured in FinancialsService: updateBudgetTemplate method:: ", loAppExp);
			setMoState("ApplicationException while executing updateBudgetTemplate method ");
			throw loAppExp;

		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception occured in FinancialsService: updateBudgetTemplate method:: ", loExp);
			setMoState("Exception while executing updateBudgetTemplate method ");
			throw new ApplicationException("Exception while executing updateBudgetTemplate method", loExp);

		}
		return loIsSuccess;
	}

	/**
	 * This method is used to update PDF Entry of Contract Certification Of
	 * Funds in database.
	 * @param aoMybatisSession SqlSession - Object
	 * @param aoTaskDetailsBean TaskDetailsBean - Object
	 * @param aoFinalFininsh Boolean - Object
	 * @return loSuccess Boolean - Object
	 * @throws ApplicationException ApplicationException - Object
	 */

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public Boolean updatePdfEntryContractCof(SqlSession aoMybatisSession, TaskDetailsBean aoTaskDetailsBean,
			Boolean aoFinalFininsh) throws ApplicationException
	{

		Boolean loIsSuccess = false;
		HashMap loHashMap = new HashMap();
		try
		{
			if (aoFinalFininsh)
			{
				// Inserting rows in PDF_BATCH_DETAILS for budget amendment
				// summary
				loHashMap.put(HHSConstants.NEW_ENTITY_TYPE, HHSConstants.CONTRACT);
				loHashMap.put(HHSConstants.SUB_ENTITY_TYPE, HHSConstants.CONTRACT);
				loHashMap.put(HHSConstants.NEW_ENTITY_ID, aoTaskDetailsBean.getContractId());
				loHashMap.put(HHSConstants.SUB_ENTITY_ID, aoTaskDetailsBean.getContractId());
				loHashMap.put(HHSConstants.TT_USERID, aoTaskDetailsBean.getUserId());
				loHashMap.put(HHSConstants.STATUS_COLUMN, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.PDF_NOT_STARTED));
				DAOUtil.masterDAO(aoMybatisSession, loHashMap, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.INSERT_PDF_BACTH_FOR_AMENDMENT_COF, HHSConstants.JAVA_UTIL_HASH_MAP);
				loIsSuccess = true;
			}
		}
		catch (ApplicationException loAppExp)
		{
			loAppExp.addContextData(HHSConstants.CONTRACTID, loHashMap);
			LOG_OBJECT.Error("Exception occured in FinancialsService: updateBudgetTemplate method:: ", loAppExp);
			setMoState("ApplicationException while executing updateBudgetTemplate method ");
			throw loAppExp;

		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Exception occured in FinancialsService: updateBudgetTemplate method:: ", loEx);
			setMoState("Exception while executing updateBudgetTemplate method ");
			throw new ApplicationException("Exception while executing updateBudgetTemplate method", loEx);

		}
		return loIsSuccess;
	}

	/**
	 * This method is used to update PDF Entry of Contract Budget in DB.
	 * @param aoMybatisSession SqlSession - Object
	 * @param aoTaskDetailsBean TaskDetailsBean - Object
	 * @param aoFinalFininsh Boolean - Object
	 * @return loSuccess Boolean - Object
	 * @throws ApplicationException ApplicationException - Object
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public Boolean updatePdfEntryContractBudget(SqlSession aoMybatisSession, TaskDetailsBean aoTaskDetailsBean,
			Boolean aoFinalFininsh) throws ApplicationException
	{

		Boolean loSuccess = false;
		HashMap loHashMap = new HashMap();
		try
		{
			if (aoFinalFininsh)
			{
				// Inserting rows in PDF_BATCH_DETAILS for budget amendment
				// summary
				loHashMap.put(HHSConstants.NEW_ENTITY_TYPE, HHSConstants.CONTRACT);
				loHashMap.put(HHSConstants.SUB_ENTITY_TYPE, HHSConstants.BUDGETLIST_BUDGET);
				loHashMap.put(HHSConstants.NEW_ENTITY_ID, aoTaskDetailsBean.getContractId());
				loHashMap.put(HHSConstants.SUB_ENTITY_ID, aoTaskDetailsBean.getBudgetId());
				loHashMap.put(HHSConstants.TT_USERID, aoTaskDetailsBean.getUserId());
				loHashMap.put(HHSConstants.STATUS_COLUMN, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.PDF_NOT_STARTED));
				DAOUtil.masterDAO(aoMybatisSession, loHashMap, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.INSERT_PDF_BACTH_FOR_AMENDMENT_COF, HHSConstants.JAVA_UTIL_HASH_MAP);
				loSuccess = true;
			}
		}
		catch (ApplicationException loExp)
		{
			loExp.addContextData(HHSConstants.CONTRACTID, loHashMap);
			LOG_OBJECT.Error("Exception occured in FinancialsService: updateBudgetTemplate method:: ", loExp);
			setMoState("ApplicationException while executing updateBudgetTemplate method ");
			throw loExp;

		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Exception occured in FinancialsService: updateBudgetTemplate method:: ", loEx);
			setMoState("Exception while executing updateBudgetTemplate method ");
			throw new ApplicationException("Exception while executing updateBudgetTemplate method", loEx);

		}
		return loSuccess;
	}

	/**
	 * R4 Batch Change for merging budget customization This method merges
	 * amendment budget customization entries with base budget entries.
	 * @param SqlSession aoMybatisSession Object
	 * @param HashMap aoHMArgs Object
	 * @return Boolean loStatusResult Object
	 * @throws ApplicationException If Exception is thrown
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public Boolean mergeBaseBudCustomization(SqlSession aoMybatisSession, HashMap aoHMArgs) throws ApplicationException
	{
		Boolean loStatusResult = Boolean.FALSE;

		try
		{
			ContractBean loContractBean = (ContractBean) aoHMArgs.get(HHSConstants.AO_CONTRACT_BEAN);

			setMoState("Merging Amendment Contract: '" + loContractBean.getContractId() + " : "
					+ loContractBean.getContractTitle() + "' into it's Base Contract.\n");

			List<String> loAmendAffectedBudgetIds = (List<String>) DAOUtil.masterDAO(aoMybatisSession, loContractBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.GET_AMEND_AFFECTED_BUDGET_IDS,
					HHSConstants.CONTRACT_BEAN_PATH);
			if (!loAmendAffectedBudgetIds.isEmpty())
			{
				for (String lsAffectedBudgetId : loAmendAffectedBudgetIds)
				{
					loContractBean.setAmendAffectedBudgetId(lsAffectedBudgetId);
					List<String> loEntryTypeList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, loContractBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
							HHSConstants.FETCH_ENTRY_TYPE_FOR_BASE_AMEND_MERGE, HHSConstants.CONTRACT_BEAN_PATH);
					HashMap loParams = new HashMap<String, String>();
					loParams.put(HHSConstants.CONTRACT_ID, loContractBean.getParentContractId());
					loParams.put(HHSConstants.BUDGET_ID, loContractBean.getAmendAffectedBudgetId());
					loParams.put(HHSConstants.USERNAME, HHSConstants.SYSTEM_USER);
					for (String lsEntryType : loEntryTypeList)
					{
						loParams.put(HHSConstants.ENTRY_TYPE_ID, lsEntryType);
						DAOUtil.masterDAO(aoMybatisSession, loParams, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
								HHSConstants.MERGE_BASE_BUD_CUSTOMIZATION, HHSConstants.JAVA_UTIL_HASH_MAP);
					}
				}
				setMoState("Merging Amendment Contract: '" + loContractBean.getContractId() + " : "
						+ loContractBean.getContractTitle() + "' into it's Base Contract finished successfully.\n");
			}
			loStatusResult = Boolean.TRUE;
		}
		catch (ApplicationException aoAppExp)
		{
			// Handle the ApplicationException type exception and set moState
			// and context data
			setMoState("Error occured while Batch Process in mergeBaseBudCustomization() method.\n");
			aoAppExp.addContextData("Exception occured while Batch Process in mergeBaseBudCustomization() method.",
					aoAppExp);
			LOG_OBJECT.Error("Error occured while Batch Process in mergeBaseBudCustomization() method.", aoAppExp);
			throw aoAppExp;
		}
		catch (Exception aoExp)
		{
			// Handle the Exception type exception and set moState
			// and context data
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while Batch Process in mergeBaseBudCustomization() method.", aoExp);
			setMoState("Error occured while Batch Process in mergeBaseBudCustomization() method.\n");
			loAppEx.addContextData("Exception occured while Batch Process in mergeBaseBudCustomization() method.",
					loAppEx);
			LOG_OBJECT.Error("Error occured while Batch Process in mergeBaseBudCustomization() method.", loAppEx);
			throw loAppEx;
		}
		return loStatusResult;
	}

	/**
	 * R4 Batch Change This method fetches a string which contains budget Id's
	 * of all affected budgets of Contract Amendment.
	 * @param SqlSession aoMybatisSession Object
	 * @param HashMap aoHMArgs Object
	 * @return String aoBudgetIds Object
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public String fetchAmendAffectedBaseBudgetIds(SqlSession aoMybatisSession, HashMap aoHMArgs)
			throws ApplicationException
	{
		String aoBudgetIds = HHSConstants.EMPTY_STRING;
		try
		{
			ContractBean loContractBean = (ContractBean) aoHMArgs.get(HHSConstants.AO_CONTRACT_BEAN);

			aoBudgetIds = (String) DAOUtil.masterDAO(aoMybatisSession, loContractBean.getContractId(),
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_AMEND_AFFECTED_BASE_BUDGET_IDS,
					HHSConstants.JAVA_LANG_STRING);

			setMoState("Fetching affected Amendment Budget Id's : '" + loContractBean.getContractId() + " : "
					+ loContractBean.getContractTitle());

		}
		catch (ApplicationException aoAppExp)
		{
			// Handle the ApplicationException type exception and set moState
			// and context data
			setMoState("Error occured while Batch Process in fetchAmendAffectedBaseBudgetIds() method.\n");
			aoAppExp.addContextData(
					"Exception occured while Batch Process in fetchAmendAffectedBaseBudgetIds() method.", aoAppExp);
			LOG_OBJECT
					.Error("Error occured while Batch Process in fetchAmendAffectedBaseBudgetIds() method.", aoAppExp);
			throw aoAppExp;
		}
		catch (Exception aoExp)
		{
			// Handle the Exception type exception and set moState
			// and context data
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while Batch Process in fetchAmendAffectedBaseBudgetIds() method.", aoExp);
			setMoState("Error occured while Batch Process in fetchAmendAffectedBaseBudgetIds() method.\n");
			loAppEx.addContextData(
					"Exception occured while Batch Process in fetchAmendAffectedBaseBudgetIds() method.", loAppEx);
			LOG_OBJECT.Error("Error occured while Batch Process in fetchAmendAffectedBaseBudgetIds() method.", loAppEx);
			throw loAppEx;
		}
		return aoBudgetIds;
	}

	/**
	 * <p>
	 * This method is used for Invoicing to edit the rows in Rate grid for a
	 * particular sub-budget.
	 * <ul>
	 * <li>1)Provider is able to Edit the Rate line for Invoicing items</li>
	 * <li>2)It Updates Invoice Amount entered in INVOICE_DETAIL table if there
	 * is an entry in the table for for a particular Invoice Id, Line Item Id,
	 * Entry Type and INSERTS a new row in the table if no entry exists.</li>
	 * <li>Query Used: fetchRateValidationRemAmt</li>
	 * <li>Query Used: fetchRateValidationRemUnits</li>
	 * <li>This method was updated in R4.</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoRateBean RateBean object as input.
	 * @param aoMybatiSession Sql session object as input.
	 * @return boolean boolean value stating success or failure of transaction
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	public boolean editInvoiceRateGrid(RateBean aoRateBean, SqlSession aoMybatiSession) throws ApplicationException
	{
		boolean lbUpdateStatus = false;
		boolean lbError = false;
		HashMap<String, Object> loQueryParam = new HashMap<String, Object>();
		try
		{
			//Start Enhancement 6535 Release 3.8.0
			aoRateBean.setInvoiceAmountCurrent(aoRateBean.getYtdInvoiceAmt());
			aoRateBean.setInvoiceUnitsCurrent(aoRateBean.getInvUnits());
			aoRateBean.setTableAmountColumn(HHSConstants.GET_AMOUNT_DETAILS.get(HHSConstants.RATE));
			aoRateBean.setTableName(HHSConstants.RATE);
			aoRateBean.setTableId(HHSConstants.GET_ID_DETAILS.get(HHSConstants.RATE));
			aoRateBean.setEntryTypeId(HHSConstants.SEVEN);
			aoRateBean.setLineItemId(aoRateBean.getId());
			BigDecimal loRemainingAmountPaymentDisbursed = (BigDecimal) DAOUtil.masterDAO(aoMybatiSession, aoRateBean,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_RATE_REMAINING_PAYMENT_DISBURSED,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			BigDecimal loRemainingAmountPaymentDisbursedUnits = (BigDecimal) DAOUtil.masterDAO(aoMybatiSession,
					aoRateBean, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
					HHSConstants.FETCH_RATE_REMAINING_PAYMENT_DISBURSED_UNITS,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			BigDecimal loFYBudgetLineItem = (BigDecimal) DAOUtil.masterDAO(aoMybatiSession, aoRateBean,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_FY_BUDGET_LINE_ITEM,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			BigDecimal loFYBudgetLineItemUnits = (BigDecimal) DAOUtil.masterDAO(aoMybatiSession, aoRateBean,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_FY_BUDGET_LINE_ITEM_UNITS,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			//End Enhancement 6535 Release 3.8.0
			BigDecimal loRemainingAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatiSession, aoRateBean,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.CBI_FETCH_VALIDATION_AMOUNT,
					HHSConstants.CBY_COM_NYC_HHS_MODEL_RATE_BEAN);
			// R4 changes: fetching remaining units to be displayed in the grid.
			BigDecimal loRemainingUnits = (BigDecimal) DAOUtil.masterDAO(aoMybatiSession, aoRateBean,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.CBI_FETCH_VALIDATION_UNITS,
					HHSConstants.CBY_COM_NYC_HHS_MODEL_RATE_BEAN);
			//Start Enhancement 6535 Release 3.8.0
			loRemainingAmount = loRemainingAmount.subtract(loRemainingAmountPaymentDisbursed);
			loRemainingUnits = loRemainingUnits.subtract(loRemainingAmountPaymentDisbursedUnits);
			//End Enhancement 6535 Release 3.8.0
			//postive invoices
			if (new BigDecimal(aoRateBean.getYtdInvoiceAmt()).compareTo(new BigDecimal(0)) >= 0)
			{
				if(loRemainingAmount.compareTo(new BigDecimal(aoRateBean.getYtdInvoiceAmt())) >= 0)
				{
				checkForUnitsAndInsertIntoInvoiceDetails(aoRateBean, aoMybatiSession, loQueryParam,
						loFYBudgetLineItemUnits, loRemainingUnits,lbError);
				}
				else 
				{
					lbError=true;
					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSConstants.MSG_KEY_INVOICE_AMOUNT_MORE_THAN_REMAINING_AMOUNT));
				}
			}
			else if (loFYBudgetLineItem.compareTo(loRemainingAmount.subtract(new BigDecimal(aoRateBean
					.getYtdInvoiceAmt()))) >= 0)
			{
				checkForUnitsAndInsertIntoInvoiceDetails(aoRateBean, aoMybatiSession, loQueryParam,
						loFYBudgetLineItemUnits, loRemainingUnits,lbError);
			}
			else
			{
				lbError=true;
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.REMAINING_AMOUNT_LESS_THAN_FY_BUDGET));
			}
			lbUpdateStatus = true;
			setMoState("Transaction passed:: InvoiceService: editInvoiceRateGrid.");
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoAppEx)
		{
			if (lbError)
			{
				aoAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoAppEx.toString());
			}
			else
			{
				LOG_OBJECT.Error("Application Exception occured in InvoiceService: editInvoiceRateGrid method.",
						aoAppEx);
				setMoState("Transaction Failed:: InvoiceService: editInvoiceRateGrid method - failed."
						+ " Exception occured while fetching rate list for details aoCBGridBeanObj::" + aoRateBean
						+ ". \n");
			}
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception occured in InvoiceService: editInvoiceRateGrid method.", aoEx);
			setMoState("Transaction Failed:: InvoiceService: editInvoiceRateGrid method - failed."
					+ " Exception occured while fetching rate list for details aoCBGridBeanObj::" + aoRateBean + ". \n");
			throw new ApplicationException("Error occured in  InvoiceService: editInvoiceRateGrid method:: ", aoEx);
		}
		return lbUpdateStatus;
	}

	/**
	 * @param aoRateBean
	 * @param aoMybatiSession
	 * @param loQueryParam
	 * @param loFYBudgetLineItemUnits
	 * @param loRemainingUnits
	 * @throws ApplicationException
	 */
	private void checkForUnitsAndInsertIntoInvoiceDetails(RateBean aoRateBean, SqlSession aoMybatiSession,
			HashMap<String, Object> loQueryParam, BigDecimal loFYBudgetLineItemUnits, BigDecimal loRemainingUnits,
			Boolean abError) throws ApplicationException
	{
		try
		{
		if(new BigDecimal(aoRateBean.getInvUnits()).compareTo(new BigDecimal(0)) >= 0 )
		{
			if(loRemainingUnits.compareTo(new BigDecimal(aoRateBean.getInvUnits())) >= 0)
		{
					// Prepare data to be sent for inserting the new row in
					// invoice
			// detail
			loQueryParam.put(HHSConstants.ID, aoRateBean.getId());
			loQueryParam.put(HHSConstants.INVOICE_ID, aoRateBean.getInvoiceId());
			loQueryParam.put(HHSConstants.ENTRY_TYPE_ID, HHSConstants.SEVEN);
			loQueryParam.put(HHSConstants.IS_LINE_ITEM_INVOICE_AMT, aoRateBean.getYtdInvoiceAmt());
			loQueryParam.put(HHSConstants.MODIFIED_BY_AGENCY, aoRateBean.getModifyByAgency());
			loQueryParam.put(HHSConstants.MODIFIED_BY_PROVIDER, aoRateBean.getModifyByProvider());
			loQueryParam.put(HHSConstants.SUB_BUDGET_ID, aoRateBean.getSubBudgetID());
			loQueryParam.put(HHSConstants.INVOICE_UNITS, aoRateBean.getInvUnits());

			// Insert new row of invoice details against this line item
			InvoiceService.insertUpdateInvoiceDetail(aoMybatiSession, loQueryParam);
		}
		else
		{
			abError = true;
			throw new ApplicationException(PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
					HHSConstants.MSG_KEY_INVOICE_UNIT_MORE_THAN_REMAINING_UNIT));
		}
		}
			else if (loFYBudgetLineItemUnits.compareTo(loRemainingUnits.subtract(new BigDecimal(aoRateBean
					.getInvUnits()))) >= 0)
		{

			// Prepare data to be sent for inserting the new row in invoice
			// detail
			loQueryParam.put(HHSConstants.ID, aoRateBean.getId());
			loQueryParam.put(HHSConstants.INVOICE_ID, aoRateBean.getInvoiceId());
			loQueryParam.put(HHSConstants.ENTRY_TYPE_ID, HHSConstants.SEVEN);
			loQueryParam.put(HHSConstants.IS_LINE_ITEM_INVOICE_AMT, aoRateBean.getYtdInvoiceAmt());
			loQueryParam.put(HHSConstants.MODIFIED_BY_AGENCY, aoRateBean.getModifyByAgency());
			loQueryParam.put(HHSConstants.MODIFIED_BY_PROVIDER, aoRateBean.getModifyByProvider());
			loQueryParam.put(HHSConstants.SUB_BUDGET_ID, aoRateBean.getSubBudgetID());
			loQueryParam.put(HHSConstants.INVOICE_UNITS, aoRateBean.getInvUnits());

			// Insert new row of invoice details against this line item
			InvoiceService.insertUpdateInvoiceDetail(aoMybatiSession, loQueryParam);
		
		}
		else
		{
			abError= true;
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
					HHSConstants.REMAINING_UNITS_LESS_THAN_FY_UNITS));
		}
	}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoAppEx)
		{
			if (abError)
			{
				aoAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoAppEx.toString());
			}
			else
			{
				LOG_OBJECT.Error("Application Exception occured in InvoiceService: editInvoiceRateGrid method.",
						aoAppEx);
				setMoState("Transaction Failed:: InvoiceService: editInvoiceRateGrid method - failed."
						+ " Exception occured while fetching rate list for details aoCBGridBeanObj::" + aoRateBean
						+ ". \n");
			}
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception occured in InvoiceService: editInvoiceRateGrid method.", aoEx);
			setMoState("Transaction Failed:: InvoiceService: editInvoiceRateGrid method - failed."
					+ " Exception occured while fetching rate list for details aoCBGridBeanObj::" + aoRateBean + ". \n");
			throw new ApplicationException("Error occured in  InvoiceService: editInvoiceRateGrid method:: ", aoEx);
		}
	}
	
	/**
	 * This method is added in R7. This method will fetch the contract level
	 * message from contract table.
	 * @param aoMybatisSession
	 * @param asStatus
	 * @return
	 * @throws ApplicationException
	 */
	public ContractList retrieveContractMessage(SqlSession aoMybatisSession, String asContractId)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered in retrieveContractMessage with contract Id" + asContractId);
		ContractList loContractList = new ContractList();
		try
		{
			loContractList = (ContractList) DAOUtil.masterDAO(aoMybatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSR5Constants.RETRIEVE_CONTRACT_MESSAGE,
					HHSConstants.JAVA_LANG_STRING);
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting the message for Contract Id:" + asContractId);
			throw loExp;
		}
		catch (Exception aoAppExp)
		{
			LOG_OBJECT.Error("Error while getting the message", aoAppExp);
			throw new ApplicationException("Error while getting the message", aoAppExp);
		}
		return loContractList;
	}
	
	// Start: Added in R7 for Cost Center
		/**
		 * This method is added in R7 for Cost Center.It is used to update the
		 * Selected List of Services on Configuration screens. <li>Query Used:
		 * delCostCenterServices</li> <li>Query Used: insertCostCenterServices</li>
		 * @param aoMyBatisSession
		 * @param aoMap
		 * @return
		 * @throws ApplicationException
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Boolean updateServiceListDetails(SqlSession aoMyBatisSession, HashMap aoMap) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into updateServiceListDetails for Details : " + aoMap.toString());
		Boolean lbUpdateStatus = Boolean.FALSE;
		List<String> lsSeletedServices = (ArrayList) aoMap.get(HHSConstants.SEL_SER_LIST);
		List<String> lsDeleteServices = (ArrayList) aoMap.get(HHSR5Constants.DELETE_ITEMS_LIST);
		try
		{
			if (lsDeleteServices.size() > 0)
			{
				aoMap.put(HHSConstants.ACTIVE_FLAG, HHSConstants.ZERO);
				DAOUtil.masterDAO(aoMyBatisSession, aoMap, HHSConstants.MAPPER_CLASS_BUDGET_MAPPER,
						HHSR5Constants.UPDATE_SERVICES_CONF, HHSConstants.JAVA_UTIL_HASH_MAP);
			}
			// Updating Map to re-use updatedServicesConf query
			aoMap.put(HHSR5Constants.DELETE_ITEMS_LIST, (ArrayList) aoMap.get(HHSConstants.SEL_SER_LIST));
			aoMap.put(HHSConstants.ACTIVE_FLAG, HHSConstants.ONE);
			if (lsSeletedServices.size() > 0)
			{
				DAOUtil.masterDAO(aoMyBatisSession, aoMap, HHSConstants.MAPPER_CLASS_BUDGET_MAPPER,
						HHSR5Constants.UPDATE_SERVICES_CONF, HHSConstants.JAVA_UTIL_HASH_MAP);
				DAOUtil.masterDAO(aoMyBatisSession, aoMap, HHSConstants.MAPPER_CLASS_BUDGET_MAPPER,
						HHSR5Constants.INSERT_UPDATED_SERVICES_CONF, HHSConstants.JAVA_UTIL_HASH_MAP);
			}
			lbUpdateStatus = Boolean.TRUE;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData("ApplicationException occured while updateServiceListDetails ", loExp);
			LOG_OBJECT.Error("ApplicationException occured while updateServiceListDetails ", loExp);
			setMoState("ApplicationException occured while updateServiceListDetails for contract id = "
					+ aoMap.toString());
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"ApplicationException occured while updateServiceListDetails ", loExp);
			loAppEx.addContextData("ApplicationException occured while updateServiceListDetails ", loExp);
			LOG_OBJECT.Error("ApplicationException occured while updateServiceListDetails ", loExp);
			setMoState("ApplicationException occured while updateServiceListDetails for paramters = "
					+ aoMap.toString());
			throw loAppEx;
		}
		return lbUpdateStatus;
	}

	/**
	 * This method is added in R7 for Cost Center.It is used to update the
	 * Selected List of Services on Configuration screens. <li>Query Used:
	 * delCostCenterServices</li> <li>Query Used: insertCostCenterServices</li>
	 * @param aoMyBatisSession
	 * @param aoMap
	 * @return
	 * @throws ApplicationException
	 */
	public Boolean updateCostCenterEnabled(SqlSession aoMyBatisSession, HashMap aoMap) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into updateCostCenterEnabled for Details : " + aoMap.toString());
		Boolean lbUpdateStatus = Boolean.FALSE;
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoMap, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSR5Constants.UPDATE_COST_CENTER_ENABLED, HHSConstants.JAVA_UTIL_HASH_MAP);
			// if COST CENTER is selected. Enable Program Income Category also
			if (((String) aoMap.get(HHSConstants.STATUS_ID)).equalsIgnoreCase(HHSConstants.TWO))
			{
				Integer liProgramIncomeCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoMap,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSR5Constants.FETCH_IF_PI_ALREADY_SELECTED,
						HHSConstants.JAVA_UTIL_HASH_MAP);
				if (liProgramIncomeCount == HHSConstants.INT_ZERO)
				{
					DAOUtil.masterDAO(aoMyBatisSession, aoMap, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.INSERT_BUDGET_CUSTOMIZATION, HHSConstants.JAVA_UTIL_HASH_MAP);
				}

			}
			lbUpdateStatus = Boolean.TRUE;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData("ApplicationException occured while updateCostCenterEnabled ", loExp);
			LOG_OBJECT.Error("ApplicationException occured while updateCostCenterEnabled ", loExp);
			setMoState("ApplicationException occured while updateCostCenterEnabled for parameters = "
					+ aoMap.toString());
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"ApplicationException occured while updateCostCenterEnabled ", loExp);
			loAppEx.addContextData("ApplicationException occured while updateCostCenterEnabled ", loExp);
			LOG_OBJECT.Error("ApplicationException occured while updateCostCenterEnabled ", loExp);
			setMoState("ApplicationException occured while updateCostCenterEnabled for parameters = "
					+ aoMap.toString());
			throw loAppEx;
		}

		return lbUpdateStatus;
	}
	// End: Added in R7 for Cost center
	
	// [Start] QC9517 R 8.7.0 Cancel option should not be available for a Negative Amendment after Budget has been Approved
	/**
	 * This method is added in R 8.7.0 for Negative Amendment.It is used to count
	 * Approved Budgets for Negative Amendment. 
	 * @param aoMyBatisSession
	 * @param loContractList
	 * @return
	 * @throws ApplicationException
	 */
	public	void fetchNegativeAmendApprovedBudgetCountAndPartialMergeCount(SqlSession aoMybatisSession, List<ContractList> loContractList
			) throws ApplicationException
	{			
	
		LOG_OBJECT.Debug("Entered into fetchNegativeAmendApprovedBudgetCountAndPartialMergeCount ");
		Integer loCount = null;
		Integer loPartialMergeCount = null;
		if(loContractList!=null)
		{
			try{
				for (ContractList loConListBean : loContractList)
				{  
				    //LOG_OBJECT.Debug("=== Contract ::  "+ loConListBean.getContractId());
					String amnt = loConListBean.getContractValue();
					//LOG_OBJECT.Debug("=== Amendment Amount ::  "+ loConListBean.getContractValue());
					//LOG_OBJECT.Debug("=== Negative Amount ::  "+ amnt.substring(0,1));
					//LOG_OBJECT.Debug("=== Budget Count ::  "+ loConListBean.getBudgetCount());
					//LOG_OBJECT.Debug("=== Budget Status ::  "+ loConListBean.getBudgetStatus());
					String requestPartialMergeStatus = HHSConstants.EMPTY_STRING;
					loPartialMergeCount = 0;
					if ("2".equalsIgnoreCase(loConListBean.getContractTypeId()))
					{
						if (amnt!= null && amnt.substring(0,1).equalsIgnoreCase("-"))
						{
							//LOG_OBJECT.Debug("====negative Amendment======= ");
							loConListBean.setNegativeAmend(true);					
							
							loCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loConListBean.getContractId(),
											HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
											HHSConstants.CBM_FETCH_COUNT_APPROVED_BUDGET, HHSConstants.JAVA_LANG_STRING);
							//LOG_OBJECT.Debug("Negative Amd Approved Budget Count :: "+loCount);
							if(loCount!= null && loCount > 0)
							{	
								loConListBean.setAmendBudgetApprovedCount(loCount);
								//LOG_OBJECT.Debug("=== loConListBean.getAmendBudgetApprovedCount()===  "+loConListBean.getAmendBudgetApprovedCount());
								//LOG_OBJECT.Debug("=== Neg Amd Contract after update::  "+ loConListBean);
							}	
							
						}
						// [Start] QC9304 R 8.8.0 Do not allow Cancel Amendment after an Out-Year Amendment has been Marked as Registered
						requestPartialMergeStatus = (String) DAOUtil.masterDAO(aoMybatisSession, loConListBean.getContractId(),
								HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_REQUEST_PARTIAL_MERGE_VALUE,
								HHSConstants.JAVA_LANG_STRING);
						loPartialMergeCount = Integer.parseInt(requestPartialMergeStatus);
						//LOG_OBJECT.Debug("=== loPartialMergeCount ::  "+ loPartialMergeCount);
						loConListBean.setPartialMergeCount(loPartialMergeCount);
						// [End] QC9304 R 8.8.0 Do not allow Cancel Amendment after an Out-Year Amendment has been Marked as Registered
					}
					
				}
			}
			catch (ApplicationException loAppEx)
			{
				LOG_OBJECT.Error("Exception occured while executing fetchCountOfApprovedBudget ", loAppEx);
				throw loAppEx;
			}
					
		}
		
	}
	// [End] QC9517 R 8.7.0 Cancel option should not be available for a Negative Amendment after Budget has been Approved
	//*** Start 9592 R 8.10.0 - Remaining Amount in Sub Budgets Created in Update Config Task are NULL when merged with Base
		/**
		 * <ul>
		 * <li>This method is for module agency interface. This method update ytd
		 * and remaining amount for budget and sub budget.</li>
		 * <li>calls the query 'updateSubBudgetsYTDAndRemainingForAgencyInterface'</li>
		 * <li>calls the query 'updateBudgetsYTDAndRemainingForAgencyInterface'</li>
		 * </ul>
		 * 
		 * @param aoMyBatisSession sql session as input
		 * @param asBudgetId base budget id as input
		 * @return lbSuccessfullyUpdated success status as output
		 * @throws ApplicationException Exception in case of query failure
		 * 
		 */
		@SuppressWarnings(
		{ "rawtypes", "unchecked" })
		public boolean updateYtdAndRemainingAmountInBudgetAndSubBudgetBase(SqlSession aoMyBatisSession, String aoBudgetId,
				Boolean loFinalFinish,String asUserId) throws ApplicationException
		{   LOG_OBJECT.Debug("**********updateYtdAndRemainingAmountInBudgetAndSubBudgetBase **********");
			boolean lbSuccessfullyUpdated = false;
			HashMap loHashMap = new HashMap<String, String>();
			try
			{
				if (loFinalFinish && aoBudgetId != null)
				{   LOG_OBJECT.Debug("**********loAffectedBudgetBaseIds :: "+aoBudgetId);
					String[] loAffectedBudgetBaseIds = aoBudgetId.split(HHSConstants.COMMA);
					loHashMap.put(HHSConstants.TT_USERID, asUserId);
					loHashMap.put(HHSConstants.MODIFY_BY, asUserId);
					for (String lsBudgetId : loAffectedBudgetBaseIds)
					{
						loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, lsBudgetId);
						Integer loSubBudgetUpdateCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
								HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
								HHSConstants.UPDATE_SUB_BUDGETS_YTD_AND_REMAINING_AMOUNT_FOR_AGENCY_INTERFACE_QUERY,
								HHSConstants.JAVA_UTIL_HASH_MAP);
						Integer loBudgetUpdateCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
								HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
								HHSConstants.UPDATE_BUDGETS_YTD_AND_REMAINING_AMOUNT_FOR_AGENCY_INTERFACE_QUERY,
								HHSConstants.JAVA_UTIL_HASH_MAP);
						// START || Added as a part of release 3.12.0 for
						// enhancement request 6643
						LOG_OBJECT.Debug("**********loSubBudgetUpdateCount :: "+loSubBudgetUpdateCount);
						LOG_OBJECT.Debug("**********loBudgetUpdateCount :: "+loBudgetUpdateCount);
						if(loSubBudgetUpdateCount==0)
						{
							DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
									HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
								HHSConstants.UPDATE_SUB_BUDGET_YTD_INVOICE_AMOUNT, HHSConstants.JAVA_UTIL_MAP);
							//*** Start 9592 R 8.10.0 - Remaining Amount in Sub Budgets Created in Update Config Task are NULL when merged with Base
							LOG_OBJECT.Debug("**********updateSubBudgetYtdInvoiceAmoutntBase :: Update BudgetId :: "+ lsBudgetId);
							DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
									HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
								HHSConstants.UPDATE_SUB_BUDGET_YTD_INVOICE_AMOUNT_BASE, HHSConstants.JAVA_UTIL_MAP);
							//*** Start 9592 R 8.10 - Remaining Amount in Sub Budgets Created in Update Config Task are NULL when merged with Base
						}
						if(loBudgetUpdateCount==0)
						{
							DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
									HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
								HHSConstants.UPDATE_BUDGET_YTD_INVOICE_AMOUNT, HHSConstants.JAVA_UTIL_MAP);
						}
						// START || Added as a part of release 3.12.0 for
						// enhancement request 6643
						lbSuccessfullyUpdated = true;
					}
				}
			}
			catch (ApplicationException aoExp)
			{
				setMoState("Exception while updating ytd and remaining amount column for budget and sub budget");
				LOG_OBJECT
						.Error("Exception while updating ytd and remaining amount column in budget and sub budget for budget id"
								+ aoBudgetId);
				throw aoExp;
			}
			return lbSuccessfullyUpdated;
		}
		//*** End 9592 R 8.10.0 - Remaining Amount in Sub Budgets Created in Update Config Task are NULL when merged with Base
	
}