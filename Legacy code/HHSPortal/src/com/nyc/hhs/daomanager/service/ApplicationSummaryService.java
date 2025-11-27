package com.nyc.hhs.daomanager.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.cache.ICacheManager;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.ApplicationAuditBean;
import com.nyc.hhs.model.ApplicationSummary;
import com.nyc.hhs.model.ContractDetails;
import com.nyc.hhs.model.Document;
import com.nyc.hhs.model.NYCAgency;
import com.nyc.hhs.model.ServiceQuestions;
import com.nyc.hhs.model.StaffDetails;
import com.nyc.hhs.model.WithdrawRequestDetails;
import com.nyc.hhs.service.db.dao.FileUploadDAO;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.util.BusinessApplicationUtil;
import com.nyc.hhs.util.CustomComparator;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.DocumentLapsingUtility;
import com.nyc.hhs.util.FileNetOperationsUtils;
import com.nyc.hhs.util.PropertyUtil;
import com.nyc.hhs.util.XMLUtil;

/**
 * ApplicationSummaryService: This class is used to call the DAO layer for the
 * application summary
 */
public class ApplicationSummaryService extends ServiceState
{
	private static final LogInfo LOG_OBJECT = new LogInfo(ApplicationSummaryService.class);

	/**
	 * This method is used to get the taxonomy name in parent child relationship
	 * and to display on the page
	 * 
	 * @param asElementType top element name
	 * @param abFromCache cache or database
	 * @param aoMyBatisSession database connection
	 * @return loParentChildTaxonomyName taxonomy name
	 * @throws ApplicationException exception
	 */
	private String getTaxonomyServiceName(final String asElementType, String abFromCache, SqlSession aoMyBatisSession,
			String asElementId) throws Exception
	{
		String loParentChildTaxonomyName = null;
		if (!abFromCache.equals(ApplicationConstants.TRUE))
		{
			try
			{
				// Set values retrieved from db in cache
				ICacheManager loCacheManager = BaseCacheManagerWeb.getInstance();
				PropertyUtil loPropertyUtil = new PropertyUtil();
				loPropertyUtil.setTaxonomyInCache(loCacheManager, ApplicationConstants.TAXONOMY_ELEMENT);
				abFromCache = ApplicationConstants.TRUE;
			}
			catch (ApplicationException aoAppEx)
			{
				throw aoAppEx;
			}
		}
		if (abFromCache.equals(ApplicationConstants.TRUE))
		{
			org.jdom.Document loTaxonomyDom = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
					ApplicationConstants.TAXONOMY_ELEMENT);
			loParentChildTaxonomyName = BusinessApplicationUtil.getTaxonomyName(asElementId, loTaxonomyDom);
		}
		if (loParentChildTaxonomyName == null || loParentChildTaxonomyName.equalsIgnoreCase(""))
		{
			Map<String, String> loApplicationMap = new LinkedHashMap<String, String>();
			loApplicationMap.put("lsElementId", asElementId);
			return getDeletedServiceName(aoMyBatisSession, loApplicationMap);
		}
		return loParentChildTaxonomyName;
	}
	/**
	 * This method is used to retrieve deleted service name
	 * @param aoMyBatisSession
	 * @param aoApplicationMap
	 * @return loStringBuilder
	 * @throws ApplicationException
	 */
	public String getDeletedServiceName(SqlSession aoMyBatisSession, Map<String, String> aoApplicationMap)
			throws ApplicationException
	{
		List<Map<String, String>> lsServiceName = (List<Map<String, String>>) DAOUtil.masterDAO(aoMyBatisSession,
				aoApplicationMap, ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER,
				"getDeletedServiceName", "java.util.Map");
		StringBuilder loStringBuilder = new StringBuilder();
		if (lsServiceName != null && !lsServiceName.isEmpty())
		{
			String lsLoopDelim = "";
			for (Map<String, String> aoMap : lsServiceName)
			{
				String lsElementName = (String) aoMap.get("ELEMENT_NAME");
				loStringBuilder.append(lsLoopDelim);
				loStringBuilder.append(lsElementName);
				lsLoopDelim = " > ";
			}
		}
		return loStringBuilder.toString();
	}

	/**
	 * This method is used to select the summary of business application and
	 * their services
	 * 
	 * @method pickDataFromDb
	 * @param asOrgId organization id
	 * @param aoMyBatisSession mybatis session
	 * @return getSortedByDate user bean with Application Summary values
	 * @throws ApplicationException application session
	 */
	@SuppressWarnings("unchecked")
	public Map<ApplicationSummary, List<ApplicationSummary>> pickDataFromDb(SqlSession aoMyBatisSession,
			String asOrgId, String asNavigationType) throws ApplicationException
	{

		Map<String, String> loMapSummary = new HashMap<String, String>();
		loMapSummary.put("asOrgId", asOrgId);
		ArrayList<ApplicationSummary> loApplicationSummaryList = (ArrayList<ApplicationSummary>) DAOUtil.masterDAO(
				aoMyBatisSession, loMapSummary, ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER,
				"selectAppInfo", "java.util.Map");

		org.jdom.Document loDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.TAXONOMY_ELEMENT);
		String lsFromCache = "false";
		if (loDoc != null)
		{
			String lsDocumentString = XMLUtil.getXMLAsString(loDoc);
			lsFromCache = (lsDocumentString.length() > 0) ? ApplicationConstants.TRUE : ApplicationConstants.FALSE;
		}

		String lsRequester = null;
		Date ldRequestDate = null;
		String lsWithdrawalStatus = null;
		// create hash map to sent on the controller
		Map<ApplicationSummary, List<ApplicationSummary>> loSummaryMap = new LinkedHashMap<ApplicationSummary, List<ApplicationSummary>>();
		List<ApplicationSummary> loSummaryList = new ArrayList<ApplicationSummary>();
		Integer liCounter = 0;
		try
		{
			pickDataFromDb(aoMyBatisSession, loApplicationSummaryList, lsFromCache, lsRequester, ldRequestDate,
					lsWithdrawalStatus, loSummaryMap, loSummaryList, liCounter, asNavigationType);
		}
		catch (Exception loEx)
		{
			throw new ApplicationException(
					"Exception occured while manipulating answer data from Service Application and Business Application table ",
					loEx);
		}
		return getSortedByDate(loSummaryMap);
	}

	/**
	 * This method create a business application as a parent the create a list
	 * of service with that business application
	 * 
	 * @param aoMyBatisSession
	 * @param aoApplicationSummaryList
	 * @param asFromCache
	 * @param asRequester
	 * @param adRequestDate
	 * @param asWithdrawalStatus
	 * @param aoSummaryMap
	 * @param aoSummaryList
	 * @param aiCounter
	 * @throws ApplicationException
	 * @throws Exception
	 */
	private void pickDataFromDb(SqlSession aoMyBatisSession, ArrayList<ApplicationSummary> aoApplicationSummaryList,
			String asFromCache, String asRequester, Date adRequestDate, String asWithdrawalStatus,
			Map<ApplicationSummary, List<ApplicationSummary>> aoSummaryMap, List<ApplicationSummary> aoSummaryList,
			Integer aiCounter, String asNavigationType) throws ApplicationException, Exception
	{
		Boolean lsDraftServiceLink = false;
		// now create a business application as a parent the create a list
		// of service with that business application
		for (ApplicationSummary loApplicationSummary : aoApplicationSummaryList)
		{
			if (loApplicationSummary.getMsAppSubmittedBy() != null)
			{
				loApplicationSummary.setMsAppSubmittedBy(FileNetOperationsUtils.getUserName(loApplicationSummary
						.getMsAppSubmittedBy()));
			}
			String lsRequester1 = loApplicationSummary.getMsRequester();
			if (lsRequester1 != null && lsRequester1.startsWith("city"))
			{
				String lsCityUserName = (String) DAOUtil.masterDAO(aoMyBatisSession, lsRequester1,
						ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getCityUserName",
						"java.lang.String");
				loApplicationSummary.setMsRequester(lsCityUserName);
			}
			if (loApplicationSummary.getMsAppType().equalsIgnoreCase("Business"))
			{
				aoSummaryList = new ArrayList<ApplicationSummary>();
				if (loApplicationSummary.getMsAppStatus().equalsIgnoreCase(ApplicationConstants.STATUS_DRAFT))
				{
					loApplicationSummary.setMdAppSubmissionDate(null);
					loApplicationSummary.setMsAppSubmittedBy(null);
				}
				if (!(loApplicationSummary.getMsAppStatus().equalsIgnoreCase(ApplicationConstants.STATUS_DRAFT)))
				{
					lsDraftServiceLink = true;
				}
				else
				{
					lsDraftServiceLink = false;
				}

				if (loApplicationSummary.getMsAppStatus().equalsIgnoreCase(ApplicationConstants.STATUS_WITHDRAWL))
				{
					asRequester = loApplicationSummary.getMsRequester();
					adRequestDate = loApplicationSummary.getMdRequestDate();
					asWithdrawalStatus = loApplicationSummary.getMsWithdrawanStatus();
				}
				aoSummaryMap.put(loApplicationSummary, aoSummaryList);
				aiCounter = 0;
			}
			// code added for showing Pending Count on provider home page -
			// start
			if (null != asNavigationType && asNavigationType.equalsIgnoreCase(ApplicationConstants.FROM_HOME_PAGE))
			{
				if (loApplicationSummary.getMsAppType().equalsIgnoreCase("Service"))
				{
					String lsSupersedingStatus = (String) DAOUtil.masterDAO(aoMyBatisSession,
							loApplicationSummary.getMsServiceAppId(),
							ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getSupersedingStatus",
							"java.lang.String");
					if (null != lsSupersedingStatus && !lsSupersedingStatus.isEmpty())
					{
						loApplicationSummary.setMsAppStatus(lsSupersedingStatus);
					}
				}

			}
			// end
			if (aiCounter > 0)
			{
				if (loApplicationSummary.getMsAppStatus().equalsIgnoreCase(ApplicationConstants.STATUS_DRAFT)
						|| loApplicationSummary.getMsAppStatus().equalsIgnoreCase(
								ApplicationConstants.NOT_STARTED_STATE)
						|| loApplicationSummary.getMsAppStatus().equalsIgnoreCase(ApplicationConstants.COMPLETED_STATE))
				{
					loApplicationSummary.setMdAppSubmissionDate(null);
					loApplicationSummary.setMsAppSubmittedBy(null);
					loApplicationSummary.setMsAppStatus(ApplicationConstants.STATUS_DRAFT);
				}
				if (asRequester != null)
				{
					loApplicationSummary.setMsRequester(asRequester);
					loApplicationSummary.setMdRequestDate(adRequestDate);
					loApplicationSummary.setMsWithdrawanStatus(asWithdrawalStatus);
				}
				if (loApplicationSummary.getMsAppStatus().equalsIgnoreCase(ApplicationConstants.STATUS_DRAFT)
						&& lsDraftServiceLink)
				{
					loApplicationSummary.setIsDraftServiceLink(true);
				}
				loApplicationSummary.setMsAppName(getTaxonomyServiceName("TAXONOMY", asFromCache, aoMyBatisSession,
						loApplicationSummary.getMsServiceElementId()));
				aoSummaryList.add(loApplicationSummary);
			}
			aiCounter++;
		}
	}

	/**
	 * This method is used to sort the map from the object property and draft
	 * will always be the 1st record if any
	 * 
	 * @param aoSummaryMap map
	 * @return loSortedMap sorted map
	 */
	private Map<ApplicationSummary, List<ApplicationSummary>> getSortedByDate(
			final Map<ApplicationSummary, List<ApplicationSummary>> aoSummaryMap)
	{
		// list of sorted keys
		List<ApplicationSummary> loSortedKey = new ArrayList<ApplicationSummary>();
		// final map that will return to the main method as sorted
		Map<ApplicationSummary, List<ApplicationSummary>> loSortedMap = new LinkedHashMap<ApplicationSummary, List<ApplicationSummary>>();
		// add the key object into the list
		if (aoSummaryMap != null && !aoSummaryMap.isEmpty())
		{
			for (Map.Entry<ApplicationSummary, List<ApplicationSummary>> loEntry : aoSummaryMap.entrySet())
			{
				loSortedKey.add(loEntry.getKey());
			}
			// sort the key list based on date
			Collections.sort(loSortedKey, new Comparator<ApplicationSummary>()
			{
				@Override
				public int compare(ApplicationSummary aoObject1, ApplicationSummary aoObject2)
				{
					int liCounter = 0;
					if (aoObject1.getApplicationCreatedDate() != null && aoObject2.getApplicationCreatedDate() != null)
					{
						liCounter = aoObject2.getApplicationCreatedDate().compareTo(
								aoObject1.getApplicationCreatedDate());
					}
					return liCounter;
				}
			});
			// draft status will always be the first record so keeping this in
			// map
			Map<ApplicationSummary, List<ApplicationSummary>> loFinalMap = new LinkedHashMap<ApplicationSummary, List<ApplicationSummary>>();
			for (Map.Entry<ApplicationSummary, List<ApplicationSummary>> loEntry : aoSummaryMap.entrySet())
			{
				List<ApplicationSummary> loNonDraftList = new ArrayList<ApplicationSummary>();
				List<ApplicationSummary> loDraftList = new ArrayList<ApplicationSummary>();
				List<ApplicationSummary> loFinalList = new ArrayList<ApplicationSummary>();
				if (loEntry.getValue() != null && !loEntry.getValue().isEmpty())
				{
					for (ApplicationSummary loApplicationSummary : loEntry.getValue())
					{
						if (loApplicationSummary.getMsAppStatus().equalsIgnoreCase(ApplicationConstants.STATUS_DRAFT))
						{
							loDraftList.add(loApplicationSummary);
						}
						else
						{
							loNonDraftList.add(loApplicationSummary);
						}
					}
				}
				Comparator<ApplicationSummary> loComparator = CustomComparator.getComparator(SortParameter.DATE,
						SortParameter.NAME_ASCENDING);
				Collections.sort(loNonDraftList, loComparator);

				Collections.sort(loDraftList, new Comparator<ApplicationSummary>()
				{
					@Override
					public int compare(ApplicationSummary aoObject1, ApplicationSummary aoObject2)
					{
						int liCounter = 0;
						if (aoObject1.getMsAppName() != null && aoObject2.getMsAppName() != null)
						{
							liCounter = aoObject1.getMsAppName().compareTo(aoObject2.getMsAppName());
						}
						return liCounter;
					}
				});
				loFinalList.addAll(loNonDraftList);
				loFinalList.addAll(loDraftList);
				loFinalMap.put(loEntry.getKey(), loFinalList);
			}
			// now get all the record except the draft one from the list and map
			// and in sorted order
			if (loSortedKey != null && !loSortedKey.isEmpty())
			{
				for (ApplicationSummary loApplicationSummary : loSortedKey)
				{
					for (Map.Entry<ApplicationSummary, List<ApplicationSummary>> loEntry : loFinalMap.entrySet())
					{
						ApplicationSummary loMapKey = loEntry.getKey();
						List<ApplicationSummary> loMapValue = loEntry.getValue();
						if (loMapKey.getMsBusinessAppId().equals(loApplicationSummary.getMsBusinessAppId()))
						{
							loSortedMap.put(loApplicationSummary, loMapValue);
							// break;
						}
					}
				}
			}

		}
		// return the sorted map based on submitted date;
		return loSortedMap;
	}
	/**
	 * This method is used to sort parameter 
	 *
	 */
	public enum SortParameter
	{
		DATE, NAME_ASCENDING
		// NAME_DESCENDING, ADDRESS_ASCENDING, ADDRESS_DESCENDING
	}

	/**
	 * This method added to insert data when a new business application is
	 * added.
	 * 
	 * @param asAppId Application Id for a city, provider or agency user
	 * @param asBusinessAppId Business Application Id city, provider or agency
	 *            user
	 * @param asOrgId Organization Id city, provider or agency user
	 * @param asStatusId Status Id city, provider or agency user
	 * @param asUserID User Id city, provider or agency user
	 * @param aoMyBatisSession Sql session object
	 * @return lbStatus insert success/failure
	 * @throws ApplicationException
	 */
	public boolean insertIntoDb(String asAppId, String asBusinessAppId, String asOrgId, String asStatusId,
			String asUserID, SqlSession aoMyBatisSession) throws ApplicationException
	{
		Boolean lbStatus = false;
		try
		{
			HashMap<String, Object> loApplicationMap = new HashMap<String, Object>();
			loApplicationMap.put("asAppId", asAppId);
			loApplicationMap.put("asBusinessAppId", asBusinessAppId);
			loApplicationMap.put("asOrgId", asOrgId);
			loApplicationMap.put("asStatusId", asStatusId);
			loApplicationMap.put("asUserID", asUserID);
			DAOUtil.masterDAO(aoMyBatisSession, loApplicationMap,
					ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "insertBusinessAppInfo",
					"java.util.HashMap");
			lbStatus = true;
		}
		catch (ApplicationException aoAppEx)
		{
			throw aoAppEx;
		}
		return lbStatus;
	}

	/**
	 * This method is used to update the status
	 * 
	 * @param aoActionMap input map
	 * @param aoMyBatisSession ibatis session
	 * @return lbStatus
	 * @throws ApplicationException application exception
	 */
	public Boolean updateStatus(final Map<String, Object> aoActionMap, final SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		Boolean lbStatus = true;
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoActionMap,
					ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "insertIntoAuditTable",
					"java.util.Map");

			DAOUtil.masterDAO(aoMyBatisSession, aoActionMap,
					ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "insertIntoSuperSedingTable",
					"java.util.Map");

			final String lsStatus = (String) aoActionMap.get("asStatus");
			if (lsStatus != null && lsStatus.equalsIgnoreCase(ApplicationConstants.STATUS_SUSPEND))
			{
				Integer liRowsUpdated = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoActionMap,
						ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "updatePrintViewGeneration",
						"java.util.Map");
				if (liRowsUpdated == 0)
				{
					DAOUtil.masterDAO(aoMyBatisSession, aoActionMap,
							ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER,
							"insertIntoPrintViewGeneration", "java.util.Map");
				}
			}

			final String lsApplicationType = (String) aoActionMap.get("applicationType");
			if (lsApplicationType != null && lsApplicationType.equalsIgnoreCase("Business"))
			{

				if (lsStatus != null && lsStatus.equalsIgnoreCase(ApplicationConstants.STATUS_SUSPEND))
				{
					DAOUtil.masterDAO(aoMyBatisSession, aoActionMap,
							ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER,
							"updateBusinessAppExpirationDate", "java.util.Map");
				}
				else
				{
					Calendar loCal = Calendar.getInstance();
					aoActionMap.put("startDate", loCal.getTime());
					loCal.add(Calendar.DATE, 90);
					aoActionMap.put("next90Days", loCal.getTime());
					aoActionMap.put("currentEffectiveDate", new Date(System.currentTimeMillis()));
					DAOUtil.masterDAO(aoMyBatisSession, aoActionMap,
							ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER,
							"bussAppExpDateConditionallyApprove", "java.util.Map");
				}
			}
			if (lsApplicationType != null && lsApplicationType.equalsIgnoreCase("Service"))
			{
				if (lsStatus != null && lsStatus.equalsIgnoreCase(ApplicationConstants.STATUS_CONDITIONALLY_APPROVED))
				{
					DAOUtil.masterDAO(aoMyBatisSession, aoActionMap,
							ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER,
							"serviceAppExpDateConditionallyApprove", "java.util.Map");
				}
				else
				{
					DAOUtil.masterDAO(aoMyBatisSession, aoActionMap,
							ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "serviceAppExpDateSuspend",
							"java.util.Map");
				}
			}
		}
		catch (ApplicationException aoAppEx)
		{
			throw aoAppEx;
		}
		// return
		return lbStatus;
	}

	/**
	 * This method is used to get the application summary history
	 * 
	 * @param aoActionMap input map
	 * @param aoMyBatisSession ibatis session
	 * @return historyList list of history
	 * @throws ApplicationException application exception
	 */
	@SuppressWarnings("unchecked")
	public List<ApplicationSummary> getApplicationSummaryHistory(final Map<String, Object> aoActionMap,
			final SqlSession aoMyBatisSession) throws ApplicationException
	{
		List<ApplicationSummary> loHistoryList = null;
		try
		{
			if (aoActionMap.get("applicationType").equals("Business"))
			{
				loHistoryList = (List<ApplicationSummary>) DAOUtil.masterDAO(aoMyBatisSession, aoActionMap,
						ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getApplicationSummaryHistory",
						"java.util.Map");
			}
			else
			{
				loHistoryList = (List<ApplicationSummary>) DAOUtil.masterDAO(aoMyBatisSession, aoActionMap,
						ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getApplicationServiceHistory",
						"java.util.Map");
			}

		}
		catch (ApplicationException aoAppEx)
		{
			throw aoAppEx;
		}
		// return the history list
		return loHistoryList;
	}

	/**
	 * This method is used to get the provider details to display the provider
	 * status and expiration date
	 * 
	 * @param asOrgId organization id as input
	 * @param aoMyBatisSession mybatis session
	 * @return application summary object
	 * @throws ApplicationException application exception
	 */
	public ApplicationSummary checkUserAppDetails(final String asOrgId, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		ApplicationSummary loProviderObj = null;
		try
		{
			// call dao method
			loProviderObj = (ApplicationSummary) DAOUtil
					.masterDAO(aoMyBatisSession, asOrgId, ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER,
							"getProviderData", "java.lang.String");
		}
		catch (ApplicationException aoAppEx)
		{
			throw aoAppEx;
		}
		// return object
		return loProviderObj;
	}

	/**
	 * This code is used to get the contract details for a selected id.
	 * 
	 * @param aoContractInfo Map of Contract Id and orgnaization Id
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return loResult Contract details for a selected contract Id.
	 * @throws ApplicationException
	 */

	public ContractDetails getContactDetailsById(Map<String, String> aoContractInfo, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		ContractDetails loResult = null;
		loResult = (ContractDetails) DAOUtil.masterDAO(aoMybatisSession, aoContractInfo,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getContractById", "java.util.Map");
		return loResult;
	}

	/**
	 * This method fetch agency name from NYC_AGENCY_DETAILS Table
	 * 
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return loResultList List of NYCAgency details having Agency name
	 *         information
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<NYCAgency> getAllNYCAgency(SqlSession aoMybatisSession) throws ApplicationException
	{
		List<NYCAgency> loResultList = null;
		loResultList = (List<NYCAgency>) DAOUtil.masterDAO(aoMybatisSession, null,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getAllNYCAgency", null);
		return loResultList;
	}

	/**
	 * This method fetch all entry from CONTRACT_DETAILS Table
	 * 
	 * @param asOrgId Organization Id for city, provider or agency user
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return loResultList List of Contract details for the the ornaization
	 * @throws ApplicationException
	 */

	@SuppressWarnings("unchecked")
	public List<ContractDetails> fetchAllContracts(String asOrgId, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		List<ContractDetails> loResultList = null;
		loResultList = (List<ContractDetails>) DAOUtil.masterDAO(aoMybatisSession, asOrgId,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getAllContract", "java.lang.String");
		return loResultList;
	}

	/**
	 * This method update contract details in CONTRACT_DETAILS Table
	 * 
	 * @param aoNewContractt Map having contract related details to be updated
	 *            for a given contract id
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return lbInsertStatus Update is successful or not
	 * @throws ApplicationException
	 */
	public boolean updateContract(Map<String, Object> aoContractMapping, ContractDetails aoNewContractt,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		boolean lbInsertStatus = false;
		String loResultToCheck = null;
		Boolean lbContractCheckFlag = (Boolean) aoContractMapping.get("contractCheckFlag");
		if (lbContractCheckFlag)
		{
			String lsContractId = aoNewContractt.getMsContractID();
			Map<String, String> loContractInfo = new HashMap<String, String>();
			loContractInfo.put("asContractId", lsContractId);
			loContractInfo.put("orgId", aoNewContractt.getMsOrgId());
			loContractInfo.put("contractDescription", aoNewContractt.getMsContractDescription());
			loContractInfo.put("contractFunderName", aoNewContractt.getMsContractFunderName());
			loContractInfo.put("contractNYCAgency", aoNewContractt.getMsContractNYCAgency());
			loResultToCheck = (String) DAOUtil.masterDAO(aoMybatisSession, loContractInfo,
					ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "checkExistingContractId",
					"java.util.Map");
		}
		if (loResultToCheck == null)
		{
			DAOUtil.masterDAO(aoMybatisSession, aoNewContractt,
					ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "updateContract",
					"com.nyc.hhs.model.ContractDetails");
			String loResult = null;
			loResult = (String) DAOUtil.masterDAO(aoMybatisSession, aoContractMapping,
					ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "checkContractMapping",
					"java.util.Map");
			if (loResult == null)
			{
				DAOUtil.masterDAO(aoMybatisSession, aoContractMapping,
						ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "insertContractMapping",
						"java.util.Map");
			}
			else
			{
				if (aoNewContractt.getMsContractDetailsId() != null)
				{
					aoContractMapping.put("msContractDetailsId", aoNewContractt.getMsContractDetailsId());
				}
				DAOUtil.masterDAO(aoMybatisSession, aoContractMapping,
						ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "updateContractMapping",
						"java.util.Map");
			}
			lbInsertStatus = true;
		}
		return lbInsertStatus;
	}

	/**
	 * This method insert contract details in CONTRACT_DETAILS Table
	 * 
	 * @param aoNewContractt Map having contract related details(contract id and
	 *            organization Id) for updation.
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return lbInsertStatus Insertion is successful or not
	 * @throws ApplicationException
	 */
	public boolean insertContract(ContractDetails aoNewContractt, Map<String, Object> aoContractMapping,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		boolean lbInsertStatus = false;
		String lsContractId = aoNewContractt.getMsContractID();
		Map<String, String> loContractInfo = new HashMap<String, String>();
		loContractInfo.put("asContractId", lsContractId);
		loContractInfo.put("orgId", aoNewContractt.getMsOrgId());
		loContractInfo.put("contractDescription", aoNewContractt.getMsContractDescription());
		loContractInfo.put("contractFunderName", aoNewContractt.getMsContractFunderName());
		loContractInfo.put("contractNYCAgency", aoNewContractt.getMsContractNYCAgency());
		String loResult = (String) DAOUtil.masterDAO(aoMybatisSession, loContractInfo,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "checkExistingContractId",
				"java.util.Map");
		if (loResult == null)
		{
			DAOUtil.masterDAO(aoMybatisSession, aoNewContractt,
					ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "insertContract",
					"com.nyc.hhs.model.ContractDetails");
			String lsRecentlyAddedContractId = (String) DAOUtil.masterDAO(aoMybatisSession, null,
					ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getRecentlyAddedContractId", null);
			aoContractMapping.put("msContractDetailsId", lsRecentlyAddedContractId);
			DAOUtil.masterDAO(aoMybatisSession, aoContractMapping,
					ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "insertContractMapping",
					"java.util.Map");
			lbInsertStatus = true;
		}
		return lbInsertStatus;
	}

	/**
	 * This method insert contract details in CONTRACT_SERVICE_MAPPING Table
	 * 
	 * @param aoContractMapping True if contract details inserted succesfully in
	 *            contract_details table.
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return lbInsertStatus Insertion is successful if its true
	 * @throws ApplicationException
	 */
	public boolean insertContractMapping(Boolean abContractInsertStatus, Map<String, Object> aoContractMapping,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		boolean lbInsertStatus = false;
		if (abContractInsertStatus)
		{
			DAOUtil.masterDAO(aoMybatisSession, aoContractMapping,
					ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "insertContractMapping",
					"java.util.Map");
			lbInsertStatus = true;
		}
		return lbInsertStatus;
	}

	/**
	 * This method one record from STAFF_DETAILS Table
	 * 
	 * <li>This method was updated in R4</li>
	 * 
	 * @param asStaffPhone selected staff id
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return loResult Give the staff detail for that particular staff Id
	 * @throws ApplicationException
	 */
	public StaffDetails getStaffDetailsById(Map<String, String> aoParamMap, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		StaffDetails loResult = null;
		loResult = (StaffDetails) DAOUtil.masterDAO(aoMybatisSession, aoParamMap,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getStaffById", "java.util.Map");
		return loResult;
	}

	/**
	 * This method is used to count the number of admin user
	 * 
	 * @param aoParamMap aoParamMap
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return integer count
	 * @throws ApplicationException
	 */
	public Integer getAdminCountToDeactivate(Map<String, String> aoParamMap, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		Integer lsCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoParamMap,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getAdminCountToDeactivate",
				"java.util.Map");
		return lsCount;
	}

	/**
	 * This method give one record from STAFF_DETAILS Table
	 * 
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return loResult Give the staff detail for that recently added staff Id
	 * @throws ApplicationException
	 */
	public StaffDetails getRecentlyAddedStaffId(SqlSession aoMybatisSession) throws ApplicationException
	{
		StaffDetails loResult = null;
		loResult = (StaffDetails) DAOUtil.masterDAO(aoMybatisSession, null,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getRecentlyAddedStaffId", null);
		return loResult;
	}

	/**
	 * This method fetch all entry from STAFF_DETAILS Table
	 * 
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return loResultList List of all the staff details
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<StaffDetails> fetchAllStaff(String asOrgId, SqlSession aoMybatisSession) throws ApplicationException
	{
		List<StaffDetails> loResultList = null;
		loResultList = (List<StaffDetails>) DAOUtil.masterDAO(aoMybatisSession, asOrgId,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getAllStaff", "java.lang.String");
		return loResultList;
	}
	

	/**
	 * This method update contract details in STAFF_DETAILS Table Updated for
	 * R4: Adding additional Query for STAFF_ORGANIZATION table entry
	 * 
	 * @param aoExistingStaff Map having staff related details(staff id and
	 *            organization Id) required for updation.
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return lbInsertStatus Insertion is successful if its true
	 * @throws ApplicationException
	 */
	public boolean updateStaff(Map<String, Object> aoStaffMapping, StaffDetails aoExistingStaff,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		boolean lbInsertStatus = false;
		DAOUtil.masterDAO(aoMybatisSession, aoExistingStaff,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "updateStaff",
				"com.nyc.hhs.model.StaffDetails");
		// R4: Adding additional Query for STAFF_ORGANIZATION table entry
		DAOUtil.masterDAO(aoMybatisSession, aoExistingStaff,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "updateMemberStaffOrgMapping",
				"com.nyc.hhs.model.StaffDetails");
		String loResult = null;
		loResult = (String) DAOUtil.masterDAO(aoMybatisSession, aoStaffMapping,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "checkStaffMapping", "java.util.Map");
		if (loResult == null)
		{
			DAOUtil.masterDAO(aoMybatisSession, aoStaffMapping,
					ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "insertStaffMapping", "java.util.Map");
		}
		lbInsertStatus = true;
		return lbInsertStatus;
	}

	/**
	 * This method modified as a part of release 3.2.0 for enhancement 5650.
	 * <ul>
	 * <li>Code added to segregate DB queries for de-activate and Remove user</li>
	 * </ul>
	 * 
	 * This method insert contract details in STAFF_DETAILS Table <li>This
	 * method was updated in R4</li>
	 * 
	 * @param aoNewStaff Map containing staff related confirmation required for
	 *            insertion
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return lbInsertStatus Insertion is successful if its true
	 * @throws ApplicationException
	 */
	public boolean insertStaff(StaffDetails aoNewStaff, SqlSession aoMybatisSession) throws ApplicationException
	{
		LOG_OBJECT.Debug("Transaction Name : insertOrgMemberDetails(insertStaff method start)");
		boolean lbInsertStatus = false;
		String lsOperationType = aoNewStaff.getOperationType();
		String lsUserAction = aoNewStaff.getMsUserAction();
		StaffDetails loStaffDetails = null;
		if (aoNewStaff.getMsStaffTitle().equalsIgnoreCase("1") && lsOperationType != null
				&& lsOperationType.equalsIgnoreCase("insertStaff"))
		{
			LOG_OBJECT.Debug("CEO check ::: ");
			Map<String, String> loStaffInfo = new HashMap<String, String>();
			loStaffInfo.put("asOrgId", aoNewStaff.getMsOrgId());
			loStaffInfo.put("asCEOId", aoNewStaff.getMsStaffTitle());
			loStaffDetails = (StaffDetails) DAOUtil.masterDAO(aoMybatisSession, loStaffInfo,
					ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "checkCEOOfficer", "java.util.Map");
		}
		if (loStaffDetails == null)
		{
			if (lsOperationType.equalsIgnoreCase("insertStaff"))
			{
				LOG_OBJECT.Debug("$$$ inside insertStaff if block of insertStaff() method ");
				// R4: Adding DAO call to fetch to be inserted STAFF_ID from
				// sequence and insert data in STAF_DETAILs &
				// STAFF_ORGANIZATION_MAPPING
				String lsStaffIdToInsert = (String) DAOUtil.masterDAO(aoMybatisSession, null,
						ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getStaffIdSequence", null);
				aoNewStaff.setMsStaffId(lsStaffIdToInsert);
				DAOUtil.masterDAO(aoMybatisSession, aoNewStaff,
						ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, lsOperationType,
						"com.nyc.hhs.model.StaffDetails");
				// R4: Adding DAO call to insert data in
				// STAFF_ORGANIZATION_MAPPING
				DAOUtil.masterDAO(aoMybatisSession, aoNewStaff,
						ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "insertStaffOrgMapping",
						"com.nyc.hhs.model.StaffDetails");
			}
			else if (lsOperationType.equalsIgnoreCase("editStaff"))
			{
				// changes done as part of release 3.2.0 defect 5650 - start
				int liStaffORgCount = 0;
				if (null != lsUserAction
						&& (lsUserAction.equalsIgnoreCase(ApplicationConstants.DEACTIVATE_USER) || lsUserAction
								.equalsIgnoreCase(ApplicationConstants.REMOVE_USER)))
				{
					LOG_OBJECT.Debug("$$$ inside updateMemberStaff if block of insertStaff() method ");
					liStaffORgCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoNewStaff,
							ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER,
							"getStaffCountToSkipUserdnUpdate", "com.nyc.hhs.model.StaffDetails");
					if (liStaffORgCount <= 1)
					{
						aoNewStaff.setMbMutiAccount(false);
						LOG_OBJECT.Debug("$$$ inside editStaff .. user action is  :: " + lsUserAction);
						DAOUtil.masterDAO(aoMybatisSession, aoNewStaff,
								ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER,
								ApplicationConstants.DEACTIVATE_USER, "com.nyc.hhs.model.StaffDetails");
					}
					DAOUtil.masterDAO(aoMybatisSession, aoNewStaff,
							ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER,
							"updateMemberStaffOrgMapping", "com.nyc.hhs.model.StaffDetails");
					LOG_OBJECT.Debug("$$$ user deactivated or removed successfully  :: ");
				}
				else
				{
					int liStaffinOrgMapping = 0;
					liStaffORgCount = 0;
					LOG_OBJECT.Debug("$$$ inside editStaff if block of insertStaff() method ");
					if (aoNewStaff.getMemberAsUser() != null && !aoNewStaff.getMemberAsUser().equalsIgnoreCase(""))
					{
						LOG_OBJECT.Debug("$$$ inside updateMemberStaff if block of insertStaff() method ");
						liStaffORgCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoNewStaff,
								ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER,
								"getStaffCountToSkipUserdnUpdate", "com.nyc.hhs.model.StaffDetails");
						if (liStaffORgCount > 1)
						{
							aoNewStaff.setMbMutiAccount(true);
						}
						DAOUtil.masterDAO(aoMybatisSession, aoNewStaff,
								ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "updateMemberStaff",
								"com.nyc.hhs.model.StaffDetails");
						DAOUtil.masterDAO(aoMybatisSession, aoNewStaff,
								ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER,
								"updateMemberStaffOrgMapping", "com.nyc.hhs.model.StaffDetails");
						DAOUtil.masterDAO(aoMybatisSession, aoNewStaff,
								ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "linkMemberToUser",
								"com.nyc.hhs.model.StaffDetails");
						liStaffinOrgMapping = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoNewStaff,
								ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getStaffCountToDelete",
								"com.nyc.hhs.model.StaffDetails");
						if (liStaffinOrgMapping == 0)
						{
							DAOUtil.masterDAO(aoMybatisSession, aoNewStaff,
									ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER,
									"linkMemberToUserDeleteAllStaff", "com.nyc.hhs.model.StaffDetails");
						}
						// changes done as part of release 3.2.0 defect 5650 -
						// end
					}
					else
					{
						LOG_OBJECT.Debug("$$$ inside updateStaff else block of insertStaff() method ");
						if (null != aoNewStaff.getMsUserDN() && !aoNewStaff.getMsUserDN().isEmpty())
						{
							LOG_OBJECT
									.Debug("$$$ inside updateStaff else block of insertStaff() method ::: User DN is :: "
											+ aoNewStaff.getMsUserDN());
						}
						else
						{
							LOG_OBJECT
									.Debug("$$$ inside updateStaff else block of insertStaff() method ::: User DN is :: null");
						}
						liStaffORgCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoNewStaff,
								ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER,
								"getStaffCountToSkipUserdnUpdate", "com.nyc.hhs.model.StaffDetails");
						if (liStaffORgCount > 1)
						{
							aoNewStaff.setMbMutiAccount(true);
						}
						DAOUtil.masterDAO(aoMybatisSession, aoNewStaff,
								ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "updateStaff",
								"com.nyc.hhs.model.StaffDetails");
						DAOUtil.masterDAO(aoMybatisSession, aoNewStaff,
								ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER,
								"updateMemberStaffOrgMapping", "com.nyc.hhs.model.StaffDetails");
					}
				}

				lbInsertStatus = true;
			}
		}
		LOG_OBJECT.Debug("Transaction Name : insertOrgMemberDetails(insertStaff method stop)");
		return lbInsertStatus;
	}

	/**
	 * This method is used to get the member title
	 * 
	 * @throws ApplicationException
	 */
	public Map<String, String> getMemberTitles(SqlSession aoMybatisSession) throws ApplicationException
	{
		List<Map<String, Object>> lbResult = null;
		lbResult = (List<Map<String, Object>>) DAOUtil.masterDAO(aoMybatisSession, new HashMap(),
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getMemberTitles", "java.util.Map");
		Map<String, String> loMemberMap = new LinkedHashMap<String, String>();
		if (lbResult != null)
		{
			for (Map<String, Object> loMap : lbResult)
			{
				loMemberMap.put((String) loMap.get("MEMBER_ID"), (String) loMap.get("MEMBER_NAME"));
			}
		}
		return loMemberMap;
	}

	/**
	 * This method is used to check the CEO officer for the organization profile
	 * 
	 * @param aoParamMap Map having staff details to get the staff title for CEO
	 *            officer.
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return loStaffDetails Staff detail having staff title.
	 * @throws ApplicationException
	 */
	public StaffDetails checkCEOOfficer(final Map<String, String> aoParamMap, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		StaffDetails loStaffDetails = (StaffDetails) DAOUtil.masterDAO(aoMybatisSession, aoParamMap,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "checkCEOOfficer", "java.util.Map");
		return loStaffDetails;
	}

	/**
	 * This method is used to deny the user request by the admin
	 * 
	 * @param aoParamMap Map having Staff related details
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return lbUpdateStatus Update is successful if its true
	 * @throws ApplicationException
	 */
	public Boolean denyUserRequestProfile(final Map<String, String> aoParamMap, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		Boolean lbUpdateStatus = false;
		int liStaffinOrgMapping = 0;
		DAOUtil.masterDAO(aoMybatisSession, aoParamMap, ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER,
				"denyUserRequestProfile", "java.util.Map");
		liStaffinOrgMapping = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoParamMap,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER,
				"getStaffCountToDeleteDenyUserRequestProfile", "java.util.Map");
		if (liStaffinOrgMapping == 0)
		{
			DAOUtil.masterDAO(aoMybatisSession, aoParamMap,
					ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER,
					"denyUserRequestProfileDeleteAllStaff", "java.util.Map");
		}
		lbUpdateStatus = true;
		return lbUpdateStatus;
	}

	/**
	 * This method insert staff details in STAFF_SERVICE_MAPPING Table
	 * 
	 * @param aoStaffMapping Map having Staff related details
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return lbInsertStatus Insert is successful if its true
	 * @throws ApplicationException
	 */
	public boolean insertStaffMapping(Map<String, Object> aoStaffMapping, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		boolean lbInsertStatus = false;
		DAOUtil.masterDAO(aoMybatisSession, aoStaffMapping,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "insertStaffMapping", "java.util.Map");
		lbInsertStatus = true;
		return lbInsertStatus;
	}

	/**
	 * This method gets the list of contract to be displayed in grid on service
	 * question screen.
	 * 
	 * @param aoContractMappingDetails Map having organization , application and
	 *            service application id
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return loResultList List of contract details for a particular service
	 *         applcation id.
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<ContractDetails> getContractListForGrid(Map<String, String> aoContractMappingDetails,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		List<ContractDetails> loResultList = null;
		loResultList = (List<ContractDetails>) DAOUtil
				.masterDAO(aoMybatisSession, aoContractMappingDetails,
						ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getContractListForGrid",
						"java.util.Map");
		return loResultList;
	}

	/**
	 * This method get the question details from SERVICE_QUESTION Table
	 * 
	 * @param aoQuestionDetailList Map having organization , application and
	 *            service application id
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return loResultList List of serivce Question details for a particular
	 *         service applcation id.
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<ServiceQuestions> getQuestionDetails(Map<String, String> aoQuestionDetailList,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		List<ServiceQuestions> loResultList = null;
		loResultList = (List<ServiceQuestions>) DAOUtil.masterDAO(aoMybatisSession, aoQuestionDetailList,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getQuestionDetailList", "java.util.Map");
		return loResultList;
	}

	/**
	 * This method insert service question details DOCUMENT Table
	 * 
	 * @param aoQuesDocMap Map having document status and document type
	 * @param asOrgId Organization id as input
	 * @param asAppId Application id as input
	 * @param asSectionId Section id as input
	 * @param asServiceAppID Service application id as input
	 * @param asDocStatus Document status as not started initially
	 * @param asBusinessAppId Business Application id as input
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return lbInsertStatus Insertion is successful if its true
	 * @throws ApplicationException
	 */
	public boolean insertServiceQuesInfo(Map<String, Object> aoQuesDocMap, String asOrgId, String asAppId,
			String asSectionId, String asServiceAppID, String asDocStatus, String asBusinessAppId,
			String asBussAppStatus, String asUserId, SqlSession aoMybatisSession) throws ApplicationException
	{
		boolean lbInsertStatus = false;
		boolean lbSkipService = false;
		lbSkipService = BusinessApplicationUtil.getServiceApplicationStatus(asBussAppStatus);
		if (lbSkipService)
		{
			Map<String, Object> loServiceQuesMap = new HashMap<String, Object>();
			loServiceQuesMap.put("asOrgId", asOrgId);
			loServiceQuesMap.put("asAppId", asAppId);
			loServiceQuesMap.put("asSectionId", asSectionId);
			loServiceQuesMap.put("asServiceAppID", asServiceAppID);
			loServiceQuesMap.put("asBusinessAppId", asBusinessAppId);
			loServiceQuesMap.put("asDocCat", aoQuesDocMap.get("asDocCat"));
			loServiceQuesMap.put("asDocType", aoQuesDocMap.get("asDocType"));
			loServiceQuesMap.put("asDocStatus", asDocStatus);
			loServiceQuesMap.put("asUserId", asUserId);
			String lsDocType = (String) aoQuesDocMap.get("asDocType");

			// Defect #1805 fix
			// List to contain Mapping Service/Contract IDs
			List<String> loEntityIdsList = null;

			if (lsDocType.equalsIgnoreCase(ApplicationConstants.CONTRACT_GRANT_DOC_TYPE))
			{
				DAOUtil.masterDAO(aoMybatisSession, loServiceQuesMap,
						ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER,
						"deleteStaffMappingOnQuestionSave", "java.util.Map");
				loEntityIdsList = fetchContractMappingIdsAssociatedToService(loServiceQuesMap, aoMybatisSession);
			}
			else if (lsDocType.equalsIgnoreCase(ApplicationConstants.KEY_STAFF_DOC_TYPE))
			{
				DAOUtil.masterDAO(aoMybatisSession, loServiceQuesMap,
						ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER,
						"deleteContractMappingOnQuestionSave", "java.util.Map");
				loEntityIdsList = fetchStaffMappingIdsAssociatedToService(loServiceQuesMap, aoMybatisSession);
			}
			else
			{
				DAOUtil.masterDAO(aoMybatisSession, loServiceQuesMap,
						ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER,
						"deleteStaffMappingOnQuestionSave", "java.util.Map");
				DAOUtil.masterDAO(aoMybatisSession, loServiceQuesMap,
						ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER,
						"deleteContractMappingOnQuestionSave", "java.util.Map");
			}
			// Call to insert method
			if (!aoQuesDocMap.get("asDocCat").equals(""))
			{
				lbInsertStatus = insertServiceDocuments(lsDocType, loEntityIdsList, loServiceQuesMap, aoMybatisSession);
			}
		}
		return lbInsertStatus;
	}

	/**
	 * This method update sercvice question details DOCUMENT Table
	 * 
	 * @param aoQuesDocMap Map having document status and document type
	 * @param asOrgId Organization id as input
	 * @param asAppId Application id as input
	 * @param asSectionId Section id as input
	 * @param asServiceAppID Service application id as input
	 * @param asDocStatus Document status as not started initially
	 * @param asBusinessAppId Business Application id as input
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return lbUpdateStatus Update is successful if its true
	 * @throws ApplicationException
	 */

	public boolean updateServiceQuesInDocument(Map<String, Object> aoQuesDocMap, String asOrgId, String asAppId,
			String asSectionId, String asServiceAppID, String asDocStatus, String asBusinessAppId,
			String asBussAppStatus, String asUserId, SqlSession aoMybatisSession) throws ApplicationException
	{
		boolean lbUpdateStatus = false;
		boolean lbSkipService = false;
		lbSkipService = BusinessApplicationUtil.getServiceApplicationStatus(asBussAppStatus);
		if (lbSkipService)
		{
			FileUploadDAO loFileUploadDao = new FileUploadDAO();
			List<Document> loDocs = loFileUploadDao.selectDetailsServiceSummary(asServiceAppID, asOrgId, asAppId,
					aoMybatisSession);
			Map<String, Object> loServiceQuesMap = new HashMap<String, Object>();
			loServiceQuesMap.put("asOrgId", asOrgId);
			loServiceQuesMap.put("asAppId", asAppId);
			loServiceQuesMap.put("asSectionId", asSectionId);
			loServiceQuesMap.put("asServiceAppID", asServiceAppID);
			loServiceQuesMap.put("asBusinessAppId", asBusinessAppId);
			loServiceQuesMap.put("asDocStatus", asDocStatus);
			loServiceQuesMap.put("asDocCat", aoQuesDocMap.get("asDocCat"));
			loServiceQuesMap.put("asDocType", aoQuesDocMap.get("asDocType"));
			loServiceQuesMap.put("asDocStatus", asDocStatus);
			loServiceQuesMap.put("asUserId", asUserId);
			String lsDocType = (String) aoQuesDocMap.get("asDocType");
			boolean lbExistCapabilitySatementDoc = false;
			List<String> loStaffFundeerMappingList = getContractStaffMappingsServiceDocuments(lsDocType,
					loServiceQuesMap, aoMybatisSession);

			// code for nyc goveranment as contract type
			if (loDocs.isEmpty())
			{
				insertServiceQuesInfo(aoQuesDocMap, asOrgId, asAppId, asSectionId, asServiceAppID, asDocStatus,
						asBusinessAppId, asBussAppStatus, asUserId, aoMybatisSession);
			}// New Fix with changed approach comes here //
			else
			{

				List<String> loDocumetsToDelete = new ArrayList<String>();
				if (null != loStaffFundeerMappingList && !loStaffFundeerMappingList.isEmpty())
				{
					// Loop to compare if mapping IDs exist in documents already
					// there in document table
					String lsEntityId = null;
					for (Document loDoc : loDocs)
					{
						lsEntityId = loDoc.getMsEntityId();
						if (null != lsEntityId && loStaffFundeerMappingList.contains(lsEntityId))
						{
							loStaffFundeerMappingList.remove(lsEntityId);
						}
						else
						{
							loDocumetsToDelete.add(lsEntityId);
						}
						if (null != loDoc.getDocType()
								&& loDoc.getDocType().equals(ApplicationConstants.CAPABILITY_STATEMENT_DOC_TYPE))
						{
							lbExistCapabilitySatementDoc = true;
						}
					}
				}
				else
				{
					for (Document loDoc : loDocs)
					{
						if (null != loDoc.getDocType()
								&& loDoc.getDocType().equals(ApplicationConstants.CAPABILITY_STATEMENT_DOC_TYPE))
						{
							lbExistCapabilitySatementDoc = true;
						}
					}
				}
				// Call to AddServiceDocumentsonServiceQuestionsUpdate method to
				// delete non existent mapping IDs and insert new ones.
				addServiceDocumentsOnServiceQuestionsUpdate(lsDocType, lbExistCapabilitySatementDoc,
						loStaffFundeerMappingList, loDocumetsToDelete, loServiceQuesMap, aoMybatisSession);
				// New Fix end here//
			}
			for (Document loDoc : loDocs)
			{
				if (!(loDoc.getDocCategory().equalsIgnoreCase((String) aoQuesDocMap.get("asDocCat")) && loDoc
						.getDocType().equalsIgnoreCase((String) aoQuesDocMap.get("asDocType"))))
				{
					if (lsDocType.equalsIgnoreCase(ApplicationConstants.CONTRACT_GRANT_DOC_TYPE))
					{
						DAOUtil.masterDAO(aoMybatisSession, loServiceQuesMap,
								ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER,
								"deleteStaffMappingOnQuestionSave", "java.util.Map");
					}
					else if (lsDocType.equalsIgnoreCase(ApplicationConstants.KEY_STAFF_DOC_TYPE))
					{
						DAOUtil.masterDAO(aoMybatisSession, loServiceQuesMap,
								ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER,
								"deleteContractMappingOnQuestionSave", "java.util.Map");
					}
					else
					{
						DAOUtil.masterDAO(aoMybatisSession, loServiceQuesMap,
								ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER,
								"deleteStaffMappingOnQuestionSave", "java.util.Map");
						DAOUtil.masterDAO(aoMybatisSession, loServiceQuesMap,
								ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER,
								"deleteContractMappingOnQuestionSave", "java.util.Map");
					}
				}
			}
			if (aoQuesDocMap.get("asDocCat").equals(""))
			{
				DAOUtil.masterDAO(aoMybatisSession, loServiceQuesMap,
						ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "deleteServiceQuesInDocument",
						"java.util.Map");
			}
		}
		return lbUpdateStatus;
	}

	/**
	 * This method is used to fetch Contract details prepare a List containing
	 * Mapping ID and update variable entity_type as Contract and variable
	 * InsertionCount
	 * 
	 * @param aoServiceQuesMap Service Question Details Map
	 * @param aoMybatisSession SQL session
	 * @return List of Contracts
	 * @throws ApplicationException
	 */
	public List<String> fetchContractMappingIdsAssociatedToService(Map<String, Object> aoServiceQuesMap,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		List<String> loContractIdsList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, aoServiceQuesMap,
				ApplicationConstants.MAPPER_CLASS_APPLICATION, "getServiceContractMappingIds", "java.util.Map");
		return loContractIdsList;

	}

	/**
	 * This method is used to fetch Staff details and prepare a List containing
	 * Mapping ID and update variable entity type as Staff and variable
	 * InsertionCount
	 * 
	 * @param aoServiceQuesMap Service Question Details Map
	 * @param aoMybatisSession SQL Session
	 * @return List of Contracts
	 * @throws ApplicationException
	 */
	public List<String> fetchStaffMappingIdsAssociatedToService(Map<String, Object> aoServiceQuesMap,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		List<String> loContractIdsList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, aoServiceQuesMap,
				ApplicationConstants.MAPPER_CLASS_APPLICATION, "getServiceStaffMappingIds", "java.util.Map");
		return loContractIdsList;

	}

	/**
	 * This method is used to update loServiceQuesMap with the entity type from
	 * the above declared variable. This method executes a loop 'insertionCount'
	 * times.
	 * 
	 * @param asDocType Document Type
	 * @param loEntityIds Entity IDs
	 * @param aoServiceQuesMap Service Question Map
	 * @param aoMybatisSession SQL session
	 * @return Insert Status
	 * @throws ApplicationException
	 */
	public Boolean insertServiceDocuments(String asDocType, List<String> loEntityIds,
			Map<String, Object> aoServiceQuesMap, SqlSession aoMybatisSession) throws ApplicationException
	{
		boolean lbInsertStatus = false;
		int liInsertCounter = 0;
		if (null != loEntityIds && !loEntityIds.isEmpty())
		{
			liInsertCounter = loEntityIds.size();
		}

		if (null != asDocType && asDocType.equalsIgnoreCase(ApplicationConstants.CONTRACT_GRANT_DOC_TYPE))
		{
			aoServiceQuesMap.put("entityType", ApplicationConstants.CONTRACT_GRANT_DISPLAY);
			while (liInsertCounter > 0)
			{
				aoServiceQuesMap.put("entityId", loEntityIds.get(liInsertCounter - 1));
				DAOUtil.masterDAO(aoMybatisSession, aoServiceQuesMap, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"insertServiceQuesInfo", "java.util.Map");
				lbInsertStatus = true;
				liInsertCounter--;
			}
		}
		else if (null != asDocType && asDocType.equalsIgnoreCase(ApplicationConstants.KEY_STAFF_DOC_TYPE))
		{
			aoServiceQuesMap.put("entityType", ApplicationConstants.KEY_STAFF_DISPLAY);
			while (liInsertCounter > 0)
			{
				aoServiceQuesMap.put("entityId", loEntityIds.get(liInsertCounter - 1));
				DAOUtil.masterDAO(aoMybatisSession, aoServiceQuesMap, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"insertServiceQuesInfo", "java.util.Map");
				lbInsertStatus = true;
				liInsertCounter--;
			}
		}
		else if (null != asDocType && asDocType.equalsIgnoreCase(ApplicationConstants.CAPABILITY_STATEMENT_DOC_TYPE))
		{
			DAOUtil.masterDAO(aoMybatisSession, aoServiceQuesMap, ApplicationConstants.MAPPER_CLASS_APPLICATION,
					"deleteServiceDocumentsNotCapabilityStatement", "java.util.Map");
			aoServiceQuesMap.put("entityId", ApplicationConstants.CAPABILITY_STATEMENT_DOC_TYPE);
			aoServiceQuesMap.put("entityType", ApplicationConstants.CAPABILITY_STATEMENT_DOC_TYPE);
			DAOUtil.masterDAO(aoMybatisSession, aoServiceQuesMap, ApplicationConstants.MAPPER_CLASS_APPLICATION,
					"insertServiceQuesInfo", "java.util.Map");
			lbInsertStatus = true;
		}
		else
		{
			lbInsertStatus = false;
		}
		return lbInsertStatus;
	}

	/**
	 * This method is used to update Document table for service Documents. This
	 * methods removes the Mapping IDs entries from the table and inserts the
	 * new row against the new added Mapping IDs.
	 * 
	 * @param asDocType Document Type
	 * @param abExistCapabilitySatementDoc Capability Statement type document
	 * @param aoDocumetsToAdd List of documents to add
	 * @param aoDocumetsToDelete List of documents to delete
	 * @param aoServiceQuesMap Service Question Map
	 * @param aoMybatisSession SQL session
	 * @return Updation Status
	 * @throws ApplicationException
	 */
	public Boolean addServiceDocumentsOnServiceQuestionsUpdate(String asDocType, boolean abExistCapabilitySatementDoc,
			List<String> aoDocumetsToAdd, List<String> aoDocumetsToDelete, Map<String, Object> aoServiceQuesMap,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		boolean lbUpdateStatus = false;
		if (null != aoDocumetsToDelete && !aoDocumetsToDelete.isEmpty())
		{
			int liDeleteCounter = aoDocumetsToDelete.size();
			while (liDeleteCounter > 0)
			{
				aoServiceQuesMap.put("entityId", aoDocumetsToDelete.get(liDeleteCounter - 1));
				DAOUtil.masterDAO(aoMybatisSession, aoServiceQuesMap, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"deleteServiceDocumentonMappingId", "java.util.Map");
				lbUpdateStatus = true;
				liDeleteCounter--;
			}

			lbUpdateStatus = true;
		}
		// Code to delete entry of Capability Statement entry from Document
		// table
		if (null != asDocType && !ApplicationConstants.CAPABILITY_STATEMENT_DOC_TYPE.equalsIgnoreCase(asDocType)
				&& abExistCapabilitySatementDoc)
		{
			DAOUtil.masterDAO(aoMybatisSession, aoServiceQuesMap, ApplicationConstants.MAPPER_CLASS_APPLICATION,
					"deleteServiceDocumentCapabilityStatement", "java.util.Map");
		}
		if ((null != aoDocumetsToAdd && !aoDocumetsToAdd.isEmpty())
				|| (null != asDocType && ApplicationConstants.CAPABILITY_STATEMENT_DOC_TYPE.equalsIgnoreCase(asDocType) && !abExistCapabilitySatementDoc))
		{
			lbUpdateStatus = insertServiceDocuments(asDocType, aoDocumetsToAdd, aoServiceQuesMap, aoMybatisSession);
		}
		return lbUpdateStatus;
	}

	/**
	 * This method fetches the final list of Mapping IDs Staff/Contract based on
	 * the DocType
	 * 
	 * @param asDocType document type
	 * @param aoServiceQuesMap Service Question Map
	 * @param aoMybatisSession SQL session
	 * @return List of Contract Staff IDs
	 * @throws ApplicationException
	 */
	public List<String> getContractStaffMappingsServiceDocuments(String asDocType,
			Map<String, Object> aoServiceQuesMap, SqlSession aoMybatisSession) throws ApplicationException
	{
		List<String> loContractStaffIds = null;
		if (null != asDocType && asDocType.equalsIgnoreCase(ApplicationConstants.CONTRACT_GRANT_DOC_TYPE))
		{
			loContractStaffIds = (List<String>) DAOUtil.masterDAO(aoMybatisSession, aoServiceQuesMap,
					ApplicationConstants.MAPPER_CLASS_APPLICATION, "getServiceContractMappingIds", "java.util.Map");
		}
		else if (null != asDocType && asDocType.equalsIgnoreCase(ApplicationConstants.KEY_STAFF_DOC_TYPE))
		{
			loContractStaffIds = (List<String>) DAOUtil.masterDAO(aoMybatisSession, aoServiceQuesMap,
					ApplicationConstants.MAPPER_CLASS_APPLICATION, "getServiceStaffMappingIds", "java.util.Map");
		}

		return loContractStaffIds;
	}

	/**
	 * This method insert service question details in SERVICE_QUESTIONS Table
	 * 
	 * @param asQues1 First question is yes, no, nothing for service application
	 *            question screen
	 * @param asQues2 Second question is yes, no, nothing for service
	 *            application question screen
	 * @param asQues3 Third question is yes, no, nothing for service application
	 *            question screen
	 * @param asOrgId Organization id as input
	 * @param asServiceAppID Service application id as input
	 * @param asBusinessAppId Business Application id as input
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return lbInsertStatus Insertion is successful if its true
	 * @throws ApplicationException
	 */
	public boolean insertServiceQuesInQuestion(String asQues1, String asQues2, String asQues3, String asOrgId,
			String asServiceAppID, String asBusinessAppId, String asBussAppStatus, String asUserId,
			String asServiceStatus, String asModifiedBy, String asModifiedDate, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		boolean lbInsertStatus = false;
		boolean lbSkipService = false;
		lbSkipService = BusinessApplicationUtil.getServiceApplicationStatus(asBussAppStatus);
		if (lbSkipService)
		{
			Map<String, Object> loServiceQuesMap = new HashMap<String, Object>();
			loServiceQuesMap.put("asQues1", asQues1);
			loServiceQuesMap.put("asQues2", asQues2);
			loServiceQuesMap.put("asQues3", asQues3);
			loServiceQuesMap.put("asOrgId", asOrgId);
			loServiceQuesMap.put("asServiceAppID", asServiceAppID);
			loServiceQuesMap.put("asBusinessAppId", asBusinessAppId);
			loServiceQuesMap.put("asUserId", asUserId);
			loServiceQuesMap.put("asServiceStatus", asServiceStatus);
			loServiceQuesMap.put("asModifiedBy", asModifiedBy);
			loServiceQuesMap.put("asModifiedDate", asModifiedDate);
			DAOUtil.masterDAO(aoMybatisSession, loServiceQuesMap,
					ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "insertServiceQuesInQuestion",
					"java.util.Map");
			lbInsertStatus = true;
		}
		return lbInsertStatus;
	}

	/**
	 * @param asOrgId Organization Id
	 * @param asServiceAppID Service Application Id
	 * @param asBusinessAppId Business Application Id
	 * @param asBussAppStatus Business Status
	 * @param asAppId Application Id
	 * @param asUserId user id
	 * @param aoMybatisSession
	 * @return lbInsertStatus true if delete is successful
	 * @throws ApplicationException
	 */
	public boolean deleteServiceInfo(String asOrgId, String asBusinessAppId, String asServiceAppID,
			String asBussAppStatus, String asAppId, String asUserId, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		boolean lbInsertStatus = false;
		boolean lbSkipService = false;
		lbSkipService = BusinessApplicationUtil.getServiceApplicationStatus(asBussAppStatus);
		if (lbSkipService)
		{
			Map<String, Object> loServiceQuesMap = new HashMap<String, Object>();
			loServiceQuesMap.put("asOrgId", asOrgId);
			loServiceQuesMap.put("asServiceAppID", asServiceAppID);
			loServiceQuesMap.put("asBusinessAppId", asBusinessAppId);
			loServiceQuesMap.put("asAppId", asAppId);// asAppId is also business
														// application id.
			DAOUtil.masterDAO(aoMybatisSession, loServiceQuesMap,
					ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "deleteServiceQuesInQuestion",
					"java.util.Map");
			DAOUtil.masterDAO(aoMybatisSession, loServiceQuesMap,
					ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "deleteServiceQuesInSubSection",
					"java.util.Map");
			loServiceQuesMap.put("asServiceId", asServiceAppID);
			loServiceQuesMap.put("asUserId", asUserId);
			loServiceQuesMap.put("asBussAppId", asBusinessAppId);
			DAOUtil.masterDAO(aoMybatisSession, loServiceQuesMap, ApplicationConstants.MAPPER_CLASS_APPLICATION,
					"updateBusiServiceAppModifiedDate", "java.util.Map");
			DAOUtil.masterDAO(aoMybatisSession, loServiceQuesMap, ApplicationConstants.MAPPER_CLASS_APPLICATION,
					"updateServiceAppModifiedDate", "java.util.Map");
			DAOUtil.masterDAO(aoMybatisSession, loServiceQuesMap,
					ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "deleteServiceQuesInDocument",
					"java.util.Map");
			lbInsertStatus = true;
		}
		return lbInsertStatus;
	}

	/**
	 * This method insert service question details in SUB_SECTION_SUMMARY Table
	 * 
	 * @param asOrgId Organization id as input
	 * @param asServiceAppID Service application id as input
	 * @param asBusinessAppId Business Application id as input
	 * @param asSectionId Section id as input
	 * @param asSubSectionId Sub Section id as input
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return lbInsertStatus Insertion is successful if its true
	 * @throws ApplicationException
	 */
	public boolean insertServiceQuesInSubSectionSummary(String asOrgId, String asServiceAppID, String asUserId,
			String asBusinessAppId, String asSectionId, String asSubSectionId, String asBussAppStatus,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		boolean lbInsertStatus = false;
		boolean lbSkipService = false;
		lbSkipService = BusinessApplicationUtil.getServiceApplicationStatus(asBussAppStatus);
		if (lbSkipService)
		{
			Map<String, Object> loServiceQuesMap = new HashMap<String, Object>();
			loServiceQuesMap.put("asOrgId", asOrgId);
			loServiceQuesMap.put("asServiceAppID", asServiceAppID);
			loServiceQuesMap.put("asBusinessAppId", asBusinessAppId);
			loServiceQuesMap.put("asSectionId", asSectionId);
			loServiceQuesMap.put("modifiedBy", asUserId);
			loServiceQuesMap.put("modifiedDate", new Date(System.currentTimeMillis()));
			loServiceQuesMap.put("asSubSectionId", asSubSectionId);
			loServiceQuesMap.put("asSubSectionStatus", ApplicationConstants.COMPLETED_STATE);
			DAOUtil.masterDAO(aoMybatisSession, loServiceQuesMap,
					ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER,
					"insertServiceQuesInSubSectionSummary", "java.util.Map");
			loServiceQuesMap.put("asServiceId", asServiceAppID);
			loServiceQuesMap.put("asUserId", asUserId);
			loServiceQuesMap.put("asBussAppId", asBusinessAppId);
			DAOUtil.masterDAO(aoMybatisSession, loServiceQuesMap, ApplicationConstants.MAPPER_CLASS_APPLICATION,
					"updateBusiServiceAppModifiedDate", "java.util.Map");
			DAOUtil.masterDAO(aoMybatisSession, loServiceQuesMap, ApplicationConstants.MAPPER_CLASS_APPLICATION,
					"updateServiceAppModifiedDate", "java.util.Map");
			lbInsertStatus = true;
		}
		return lbInsertStatus;
	}

	/**
	 * This method update service question details SERVICE_QUESTIONS Table
	 * 
	 * @param asQues1 First question is yes, no, nothing for service application
	 *            question screen
	 * @param asQues2 Second question is yes, no, nothing for service
	 *            application question screen
	 * @param asQues3 Third question is yes, no, nothing for service application
	 *            question screen
	 * @param asOrgId Organization id as input
	 * @param asServiceAppID Service application id as input
	 * @param asBusinessAppId Business Application id as input
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return lbInsertStatus Insertion is successful if its true
	 * @throws ApplicationException
	 */
	public boolean updateServiceQuesInQuestion(String asQues1, String asQues2, String asQues3, String asOrgId,
			String asServiceAppID, String asBusinessAppId, String asBussAppStatus, String asUserId,
			String asServiceStatus, String asModifiedBy, String asModifiedDate, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		boolean lbInsertStatus = false;
		boolean lbSkipService = false;
		lbSkipService = BusinessApplicationUtil.getServiceApplicationStatus(asBussAppStatus);
		if (lbSkipService)
		{
			Map<String, Object> loServiceQuesMap = new HashMap<String, Object>();
			loServiceQuesMap.put("asQues1", asQues1);
			loServiceQuesMap.put("asQues2", asQues2);
			loServiceQuesMap.put("asQues3", asQues3);
			loServiceQuesMap.put("asOrgId", asOrgId);
			loServiceQuesMap.put("asServiceAppID", asServiceAppID);
			loServiceQuesMap.put("asBusinessAppId", asBusinessAppId);
			loServiceQuesMap.put("asUserId", asUserId);
			loServiceQuesMap.put("asServiceStatus", asServiceStatus);
			loServiceQuesMap.put("asModifiedBy", asModifiedBy);
			loServiceQuesMap.put("asModifiedDate", asModifiedDate);
			DAOUtil.masterDAO(aoMybatisSession, loServiceQuesMap,
					ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "updateServiceQuesInQuestion",
					"java.util.Map");
			lbInsertStatus = true;
		}
		return lbInsertStatus;
	}

	/**
	 * This method update service question details in SUB_SECTION_SUMMARY Table
	 * 
	 * @param asOrgId Organization id as input
	 * @param asServiceAppID Service application id as input
	 * @param asBusinessAppId Business Application id as input
	 * @param asSectionId Section id as input
	 * @param asSubSectionId Sub Section id as input
	 * @param aoMybatisSession Mybatis Sql Session
	 * @param asUserId User id
	 * @return lbInsertStatus Update is successful if its true
	 * @throws ApplicationException
	 */
	public boolean updateServiceQuesInSubSectionSummary(String asOrgId, String asServiceAppID, String asBusinessAppId,
			String asSectionId, String asSubSectionId, String asBussAppStatus, String asUserId,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		boolean lbInsertStatus = false;
		boolean lbSkipService = false;
		lbSkipService = BusinessApplicationUtil.getServiceApplicationStatus(asBussAppStatus);
		if (lbSkipService)
		{
			Map<String, Object> loServiceQuesMap = new HashMap<String, Object>();
			loServiceQuesMap.put("asOrgId", asOrgId);
			loServiceQuesMap.put("asServiceAppID", asServiceAppID);
			loServiceQuesMap.put("asBusinessAppId", asBusinessAppId);
			loServiceQuesMap.put("asSectionId", asSectionId);
			loServiceQuesMap.put("asSubSectionId", asSubSectionId);
			loServiceQuesMap.put("asSubSectionStatus", ApplicationConstants.COMPLETED_STATE);
			// Call to insert method
			DAOUtil.masterDAO(aoMybatisSession, loServiceQuesMap,
					ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER,
					"updateServiceQuesInSubSectionSummary", "java.util.Map");
			loServiceQuesMap.put("asServiceId", asServiceAppID);
			loServiceQuesMap.put("asUserId", asUserId);
			loServiceQuesMap.put("asBussAppId", asBusinessAppId);
			DAOUtil.masterDAO(aoMybatisSession, loServiceQuesMap, ApplicationConstants.MAPPER_CLASS_APPLICATION,
					"updateBusiServiceAppModifiedDate", "java.util.Map");
			DAOUtil.masterDAO(aoMybatisSession, loServiceQuesMap, ApplicationConstants.MAPPER_CLASS_APPLICATION,
					"updateServiceAppModifiedDate", "java.util.Map");
			lbInsertStatus = true;
		}
		return lbInsertStatus;
	}

	/**
	 * This method fetch all entry from STAFF_DETAILS Table to display on
	 * service question screen.
	 * 
	 * @param aoStaffMappingDetails Map having organization , application and
	 *            service application id
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return loResultList List of all the Staff details
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<StaffDetails> getStaffListForGrid(Map<String, String> aoStaffMappingDetails, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		List<StaffDetails> loResultList = null;
		loResultList = (List<StaffDetails>) DAOUtil.masterDAO(aoMybatisSession, aoStaffMappingDetails,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getStaffListForGrid", "java.util.Map");
		return loResultList;
	}

	/**
	 * This method delete staff details from STAFF_DETAILS Table
	 * 
	 * @param asStaffId Staff Id to be deleted
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return lbInsertStatus Update is successful if its true
	 * @throws ApplicationException
	 */
	public boolean deleteSelectedStaff(String asStaffId, SqlSession aoMybatisSession) throws ApplicationException
	{
		boolean lbInsertStatus = false;
		DAOUtil.masterDAO(aoMybatisSession, asStaffId, ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER,
				"deleteSelectedStaff", "java.lang.String");
		lbInsertStatus = true;
		return lbInsertStatus;
	}

	/**
	 * This method delete contract details from CONTRACT_DETAILS Table
	 * 
	 * @param asContractId Contract Id to be deleted
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return lbInsertStatus Delete is successful if its true
	 * @throws ApplicationException
	 */
	public boolean deleteSelectedContract(String asContractId, SqlSession aoMybatisSession) throws ApplicationException
	{
		boolean lbInsertStatus = false;
		DAOUtil.masterDAO(aoMybatisSession, asContractId, ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER,
				"deleteSelectedContract", "java.lang.String");
		lbInsertStatus = true;
		return lbInsertStatus;
	}

	/**
	 * This method delete staff mapping details from STAFF_SERVICE_MAPPING Table
	 * when delete is selected on service question screen.
	 * 
	 * @param asStaffId Staff Id to be deleted
	 * @param aoMybatisSession Mybatis Sql Session
	 * @param asOrgId Organization Id
	 * @param asBusinessAppId Business Application Id
	 * @param asServiceAppId Service Application Id
	 * @param asBussAppStatus Business Application Status
	 * @param asAppId Application Id
	 * @param aoMybatisSession Sql session
	 * @return lbInsertStatus Delete is successful if its true
	 * @throws ApplicationException
	 */
	public boolean deleteStaffMapping(String asStaffId, String asOrgId, String asBusinessAppId, String asServiceAppId,
			String asBussAppStatus, String asAppId, String asUserId, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		Integer loStaffCount = 0;
		Map<String, Object> loServiceQuesMap = new HashMap<String, Object>();
		loServiceQuesMap.put("asOrgId", asOrgId);
		loServiceQuesMap.put("asServiceAppId", asServiceAppId);
		loServiceQuesMap.put("asBusinessAppId", asBusinessAppId);
		loServiceQuesMap.put("asAppId", asAppId);
		loServiceQuesMap.put("staffId", asStaffId);
		loStaffCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loServiceQuesMap,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getStaffCount", "java.util.Map");
		if (loStaffCount == 1)
		{
			deleteServiceInfo(asOrgId, asBusinessAppId, asServiceAppId, asBussAppStatus, asAppId, asUserId,
					aoMybatisSession);
		}
		boolean lbInsertStatus = false;
		DAOUtil.masterDAO(aoMybatisSession, loServiceQuesMap,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "deleteStaffMapping", "java.util.Map");
		// Fix for defect #1805, deleting entry from Document table for
		// corresponding Staff ID
		DAOUtil.masterDAO(aoMybatisSession, loServiceQuesMap, ApplicationConstants.MAPPER_CLASS_APPLICATION,
				"deleteServiceDocumentonStaff", "java.util.Map");
		lbInsertStatus = true;
		return lbInsertStatus;
	}

	/**
	 * This method delete contract mapping details from CONTRACT_SERVICE_MAPPING
	 * Table when delete is selected on service question screen.
	 * 
	 * @param asContractId Contract Id to be deleted
	 * @param aoMybatisSession Mybatis Sql Session
	 * @param asOrgId Organization Id
	 * @param asBusinessAppId Business Application Id
	 * @param asServiceAppId Service Application Id
	 * @param asBussAppStatus Business Application Status
	 * @param asAppId Application Id
	 * @param aoMybatisSession sql session
	 * @return lbInsertStatus Delete is successful if its true
	 * @throws ApplicationException
	 */
	public boolean deleteContractMapping(String asContractId, String asOrgId, String asBusinessAppId,
			String asServiceAppId, String asBussAppStatus, String asAppId, String asUserId, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		Integer loContractCount = 0;
		Map<String, Object> loServiceQuesMap = new HashMap<String, Object>();
		loServiceQuesMap.put("asOrgId", asOrgId);
		loServiceQuesMap.put("asServiceAppId", asServiceAppId);
		loServiceQuesMap.put("asBusinessAppId", asBusinessAppId);
		loServiceQuesMap.put("asAppId", asAppId);
		loServiceQuesMap.put("contractId", asContractId);
		loContractCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loServiceQuesMap,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getContractCount", "java.util.Map");
		if (loContractCount == 1)
		{
			deleteServiceInfo(asOrgId, asBusinessAppId, asServiceAppId, asBussAppStatus, asAppId, asUserId,
					aoMybatisSession);
		}
		boolean lbInsertStatus = false;
		DAOUtil.masterDAO(aoMybatisSession, loServiceQuesMap,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "deleteContractMapping", "java.util.Map");
		// Fix for defect #1805, deleting entry from Document table for
		// corresponding Staff ID
		DAOUtil.masterDAO(aoMybatisSession, loServiceQuesMap, ApplicationConstants.MAPPER_CLASS_APPLICATION,
				"deleteServiceDocumentonContract", "java.util.Map");
		lbInsertStatus = true;
		return lbInsertStatus;
	}

	/**
	 * This method fetch all entry from STAFF_DETAILS Table to display on
	 * service question screen.
	 * 
	 * @param asOrgId Organization Id as input
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return loResultList List of Staff details for that organization id.
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<StaffDetails> getOrgMemberListForGrid(String asOrgId, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Transaction Name : insertOrgMemberDetails(getOrgMemberListForGrid method start)");
		List<StaffDetails> loResultList = null;
		loResultList = (List<StaffDetails>) DAOUtil.masterDAO(aoMybatisSession, asOrgId,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getOrgMemberListForGrid",
				"java.lang.String");
		LOG_OBJECT.Debug("Transaction Name : insertOrgMemberDetails(getOrgMemberListForGrid method stop)");
		return loResultList;
	}

	/**
	 * This method update contract details in CONTRACT_SERVICE_MAPPING Table for
	 * existing funder.
	 * 
	 * @param aoContractMapping Map having the details to update the mapping
	 *            table
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return lbInsertStatus Update is successful if its true
	 * @throws ApplicationException
	 */
	public boolean updateContractMapping(Map<String, String> aoContractMapping, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		boolean lbInsertStatus = false;
		DAOUtil.masterDAO(aoMybatisSession, aoContractMapping,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "updateContractMapping", "java.util.Map");
		lbInsertStatus = true;
		return lbInsertStatus;
	}

	/**
	 * This method update contract details in BUSINESS_WITHDRAWAL Table
	 * 
	 * @param aoBusinessWithdrawl Map having Business withdraw Id
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return lbUpdateStatus Update is successful if its true
	 * @throws ApplicationException
	 */
	public boolean updateBusinessWithdrawlRequest(Map<String, String> aoBusinessWithdrawl, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		boolean lbUpdateStatus = false;
		DAOUtil.masterDAO(aoMybatisSession, aoBusinessWithdrawl,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "updateBusinessWithdrawlRequest",
				"java.util.Map");
		lbUpdateStatus = true;
		return lbUpdateStatus;
	}

	/**
	 * This method insert contract details in BUSINESS_WITHDRAWAL Table
	 * 
	 * @param aoBusinessWithdrawl Map having information related to business
	 *            withdrawal.
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return lbInsertStatus Update is successful if its true
	 * @throws ApplicationException
	 */
	public Map<String, String> insertBusinessWithdrawlRequest(Map<String, String> aoBusinessWithdrawl,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		int liUpdateCount = 0;
		liUpdateCount = (Integer) DAOUtil
				.masterDAO(aoMybatisSession, aoBusinessWithdrawl,
						ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "updateBusinessIfExists",
						"java.util.Map");
		if (liUpdateCount == 0)
		{
			DAOUtil.masterDAO(aoMybatisSession, aoBusinessWithdrawl,
					ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "insertBusinessWithdrawlRequest",
					"java.util.Map");
		}
		WithdrawRequestDetails loBAWithdrawalId = null;
		loBAWithdrawalId = (WithdrawRequestDetails) DAOUtil.masterDAO(aoMybatisSession, null,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getBAWithdrawlId", null);
		WithdrawRequestDetails loParentAppId = null;
		loParentAppId = (WithdrawRequestDetails) DAOUtil
				.masterDAO(aoMybatisSession, aoBusinessWithdrawl,
						ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getParentApplicationId",
						"java.util.Map");
		aoBusinessWithdrawl = updateMapForWorkflow(aoBusinessWithdrawl, loBAWithdrawalId, loParentAppId);
		return aoBusinessWithdrawl;
	}

	/**
	 * This method update details in BUSINESS_Application Table
	 * 
	 * @param aoBusinessIdOrgId Map of business and organization id
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return lbUpdateStatus Update is successful if its true
	 * @throws ApplicationException
	 */
	public boolean updateBusinessApplication(Map<String, String> aoBusinessIdOrgId, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		boolean lbUpdateStatus = false;
		DAOUtil.masterDAO(aoMybatisSession, aoBusinessIdOrgId,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "updateBusinessApplication",
				"java.util.Map");
		lbUpdateStatus = true;
		return lbUpdateStatus;
	}

	/**
	 * This method gets withdrawal details
	 * 
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return loResult Get the Withdraw Request Details.
	 * @throws ApplicationException
	 */
	public WithdrawRequestDetails getBAWithdrawlId(SqlSession aoMybatisSession) throws ApplicationException
	{
		WithdrawRequestDetails loResult = null;
		loResult = (WithdrawRequestDetails) DAOUtil.masterDAO(aoMybatisSession, null,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getBAWithdrawlId", null);
		return loResult;
	}

	/**
	 * This method gets parent application id for business withdrawal
	 * 
	 * @param aoBusinessWithdrawl Map having business application Id
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return loResult Get the Withdraw Request Details.
	 * @throws ApplicationException
	 */
	public WithdrawRequestDetails getParentApplicationId(Map<String, String> aoBusinessWithdrawl,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		WithdrawRequestDetails loResult = null;
		loResult = (WithdrawRequestDetails) DAOUtil
				.masterDAO(aoMybatisSession, aoBusinessWithdrawl,
						ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getParentApplicationId",
						"java.util.Map");
		return loResult;
	}

	/**
	 * This method update contract details in Service_WITHDRAWAL Table
	 * 
	 * @param aoServiceWithdrawl Map having business Withdrawal Id
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return lbUpdateStatusq Update is successful if its true
	 * @throws ApplicationException
	 */
	public boolean updateServiceWithdrawlRequest(Map<String, String> aoServiceWithdrawl, SqlSession aoMybatisSession)
			throws ApplicationException
	{

		boolean lbUpdateStatus = false;
		DAOUtil.masterDAO(aoMybatisSession, aoServiceWithdrawl,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "updateServiceWithdrawlRequest",
				"java.util.Map");
		lbUpdateStatus = true;
		return lbUpdateStatus;
	}

	/**
	 * This method insert contract details in Service_WITHDRAWAL Table
	 * 
	 * @param aoServiceWithdrawl Map containing information regarding Service
	 *            Withdrawl Request
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return lbInsertStatus insert is successful if its true
	 * @throws ApplicationException
	 */
	public Map<String, String> insertServiceWithdrawlRequest(Map<String, String> aoServiceWithdrawl,
			SqlSession aoMybatisSession) throws ApplicationException
	{

		int liUpdateCount = 0;
		liUpdateCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoServiceWithdrawl,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "UpdateServiceIfExist", "java.util.Map");
		if (liUpdateCount == 0)
		{
			DAOUtil.masterDAO(aoMybatisSession, aoServiceWithdrawl,
					ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "insertServiceWithdrawlRequest",
					"java.util.Map");
		}
		WithdrawRequestDetails loSAWithdrawalId = null;
		loSAWithdrawalId = (WithdrawRequestDetails) DAOUtil.masterDAO(aoMybatisSession, null,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getSAWithdrawlId", null);
		WithdrawRequestDetails loServiceParentAppId = null;
		loServiceParentAppId = (WithdrawRequestDetails) DAOUtil.masterDAO(aoMybatisSession, aoServiceWithdrawl,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getServiceParentApplicationId",
				"java.util.Map");
		WithdrawRequestDetails loServiceName = null;
		loServiceName = (WithdrawRequestDetails) DAOUtil.masterDAO(aoMybatisSession, aoServiceWithdrawl,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getServiceName", "java.util.Map");
		aoServiceWithdrawl = updateMapForServiceWorkflow(aoServiceWithdrawl, loSAWithdrawalId, loServiceParentAppId,
				loServiceName);
		return aoServiceWithdrawl;
	}

	/**
	 * This method update details in Service_Application Table
	 * 
	 * @param asServiceIdOrgId Map containing service and organization Id
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return lbUpdateStatus Update is successful if its true
	 * @throws ApplicationException
	 */
	public boolean updateServiceApplication(Map<String, String> asServiceIdOrgId, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		boolean lbUpdateStatus = false;
		DAOUtil.masterDAO(aoMybatisSession, asServiceIdOrgId,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "updateServiceApplication",
				"java.util.Map");
		lbUpdateStatus = true;
		return lbUpdateStatus;
	}

	/**
	 * This method update details in Service_Application Table
	 * 
	 * @param asServiceIdOrgId Map containing service and organization Id
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return lbUpdateStatus Update is successful if its true
	 * @throws ApplicationException
	 */
	public boolean updateServiceAppForBusinessWithdrawal(Map<String, String> asServiceIdOrgId,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		boolean lbUpdateStatus = false;
		DAOUtil.masterDAO(aoMybatisSession, asServiceIdOrgId,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "updateServiceAppForBusinessWithdrawal",
				"java.util.Map");
		lbUpdateStatus = true;
		return lbUpdateStatus;
	}

	/**
	 * This method update details in Service_Application Table
	 * 
	 * @param asServiceIdOrgId Map containing service and organization Id
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return lbUpdateStatus Update is successful if its true
	 * @throws ApplicationException
	 */
	public boolean updateServiceApplicationForSubmission(Map<String, String> asServiceIdOrgId,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		boolean lbUpdateStatus = false;
		DAOUtil.masterDAO(aoMybatisSession, asServiceIdOrgId,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "updateServiceApplicationForSubmission",
				"java.util.Map");
		lbUpdateStatus = true;
		return lbUpdateStatus;
	}

	/**
	 * This method gets service application ID for service withdrawal
	 * 
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return loResult Withdraw Request Details information
	 * @throws ApplicationException
	 */
	public WithdrawRequestDetails getSAWithdrawlId(SqlSession aoMybatisSession) throws ApplicationException
	{
		WithdrawRequestDetails loResult = null;
		loResult = (WithdrawRequestDetails) DAOUtil.masterDAO(aoMybatisSession, null,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getSAWithdrawlId", null);
		return loResult;
	}

	/**
	 * This method gets service's parent application id for service withdrawal
	 * 
	 * @param aoServiceWithdrawl Map containing service Id
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return loResult Withdraw Request Details information
	 * @throws ApplicationException
	 */
	public WithdrawRequestDetails getServiceParentApplicationId(Map<String, String> aoServiceWithdrawl,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		WithdrawRequestDetails loResult = null;
		loResult = (WithdrawRequestDetails) DAOUtil.masterDAO(aoMybatisSession, aoServiceWithdrawl,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getServiceParentApplicationId",
				"java.util.Map");

		return loResult;

	}

	/**
	 * This method inserts the data into Application_Audit Table after
	 * successful launch of workflow
	 * 
	 * @param aoApplicationAuditEntry map contains all the required data
	 * @param aoMybatiSession Mybatis session required to make the database
	 *            transaction
	 * @return lbUpdateStatus
	 * @throws ApplicationException
	 */
	public boolean insertApplicationAudit(Map<String, String> aoApplicationAuditEntry, SqlSession aoMybatiSession)
			throws ApplicationException
	{

		boolean lbUpdateStatus = false;
		DAOUtil.masterDAO(aoMybatiSession, aoApplicationAuditEntry,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "insertApplicationAudit", "java.util.Map");

		return lbUpdateStatus;
	}

	/**
	 * This method updates the aoMapForWorkflow Map with the values required for
	 * the next service of the same transaction.
	 * 
	 * @param aoMapForWorkflow map to be updated
	 * @param asBAWithdrawlId Bean Object contains the required valure to update
	 *            the Map
	 * @param asParentAppId Bean Object contains the required valure to update
	 *            the Map
	 * @param asServiceName Bean Object contains the required valure to update
	 *            the Map
	 * @return aoMapForWorkflow
	 * @throws ApplicationException
	 */
	public Map<String, String> updateMapForServiceWorkflow(Map<String, String> aoMapForWorkflow,
			WithdrawRequestDetails asBAWithdrawlId, WithdrawRequestDetails asParentAppId,
			WithdrawRequestDetails asServiceName) throws ApplicationException
	{
		aoMapForWorkflow.put("BAWithdrawlId", asBAWithdrawlId.getMsBAwithdrawlId());
		aoMapForWorkflow.put(P8Constants.PROPERTY_PE_ENTITY_ID, asBAWithdrawlId.getMsBAwithdrawlId());
		String lsParentAppId = asParentAppId.getMsAppId();
		aoMapForWorkflow.put("requestId", asBAWithdrawlId.getMsBAwithdrawlId());
		if (lsParentAppId != null)
		{
			aoMapForWorkflow.put("lsParentAppId", lsParentAppId);
			aoMapForWorkflow.put("ParentApplicationID", lsParentAppId);
		}
		else
		{
			aoMapForWorkflow.put("lsParentAppId", "");
			aoMapForWorkflow.put("ParentApplicationID", "");
		}

		StringBuffer lsBfServiceName = new StringBuffer(aoMapForWorkflow.get(P8Constants.PROPERTY_PE_TASK_NAME));
		lsBfServiceName.append(asServiceName.getMsProviderName());
		aoMapForWorkflow.put(P8Constants.PROPERTY_PE_TASK_NAME, lsBfServiceName.toString());
		return aoMapForWorkflow;
	}

	/**
	 * 
	 * This method updates the aoMapForWorkflow Map with the values required for
	 * the next service of the same transaction.
	 * 
	 * @param aoMapForWorkflow map to be updated
	 * @param asBAWithdrawlId Bean Object contains the required valure to update
	 *            the Map
	 * @param asParentAppId Bean Object contains the required valure to update
	 *            the Map
	 * @return aoMapForWorkflow
	 * @throws ApplicationException
	 */
	public Map<String, String> updateMapForWorkflow(Map<String, String> aoMapForWorkflow,
			WithdrawRequestDetails asBAWithdrawlId, WithdrawRequestDetails asParentAppId) throws ApplicationException
	{
		aoMapForWorkflow.put("BAWithdrawlId", asBAWithdrawlId.getMsBAwithdrawlId());
		aoMapForWorkflow.put("lsParentAppId", asParentAppId.getMsAppId());
		aoMapForWorkflow.put("ParentApplicationID", asParentAppId.getMsAppId());
		aoMapForWorkflow.put(P8Constants.PROPERTY_PE_ENTITY_ID, asBAWithdrawlId.getMsBAwithdrawlId());
		aoMapForWorkflow.put("requestId", asBAWithdrawlId.getMsBAwithdrawlId());
		return aoMapForWorkflow;
	}

	/**
	 * This method updates the aoMapForWorkflow Map with the values required for
	 * the next service of the same transaction.
	 * 
	 * @param aoMapForWorkflow map to be updated
	 * @param asServiceName Bean Object contains the required valure to update
	 *            the Map
	 * @param asParentAppId Bean Object contains the required valure to update
	 *            the Map
	 * @return aoMapForWorkflow
	 * @throws ApplicationException
	 */
	public Map<String, String> updateMapForServiceSubmissionWorkflow(Map<String, String> aoMapForWorkflow,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		WithdrawRequestDetails loServiceName = null;
		loServiceName = (WithdrawRequestDetails) DAOUtil.masterDAO(aoMybatisSession, aoMapForWorkflow,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getServiceName", "java.util.Map");
		WithdrawRequestDetails loServiceParentAppId = null;
		loServiceParentAppId = (WithdrawRequestDetails) DAOUtil.masterDAO(aoMybatisSession, aoMapForWorkflow,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getServiceParentApplicationId",
				"java.util.Map");
		StringBuffer lsBfServiceName = new StringBuffer(aoMapForWorkflow.get(P8Constants.PROPERTY_PE_TASK_NAME));
		lsBfServiceName.append(loServiceName.getMsProviderName());
		aoMapForWorkflow.put(P8Constants.PROPERTY_PE_TASK_NAME, "Service Application - " + lsBfServiceName.toString());
		aoMapForWorkflow.put("lsParentAppId", loServiceParentAppId.getMsAppId());
		aoMapForWorkflow.put("ParentApplicationID", loServiceParentAppId.getMsAppId());
		return aoMapForWorkflow;
	}

	/**
	 * 
	 * This method updates the aoMapForWorkflow Map with the values required for
	 * the next service of the same transaction.
	 * 
	 * @param aoMapForWorkflow map to be updated
	 * @param asBAWithdrawlId Bean Object contains the required valure to update
	 *            the Map
	 * @param asParentAppId Bean Object contains the required valure to update
	 *            the Map
	 * @return aoMapForWorkflow
	 * @throws ApplicationException
	 */
	public Map<String, String> updateServiceMapForWorkflow(Map<String, String> aoMapForWorkflow,
			WithdrawRequestDetails asBAWithdrawlId, WithdrawRequestDetails asParentAppId) throws ApplicationException
	{
		aoMapForWorkflow.put("BAWithdrawlId", asBAWithdrawlId.getMsBAwithdrawlId());
		aoMapForWorkflow.put("lsParentAppId", asParentAppId.getMsAppId());
		aoMapForWorkflow.put("ParentApplicationID", asParentAppId.getMsAppId());
		aoMapForWorkflow.put("requestId", asBAWithdrawlId.getMsBAwithdrawlId());
		return aoMapForWorkflow;
	}

	/**
	 * 
	 * This method updates the aoMapForWorkflow Map with the values required for
	 * the next service of the same transaction.
	 * 
	 * @param aoMapForWorkflow map to be updated
	 * @param asWorkflowId Bean Object contains the required valure to update
	 *            the Map
	 * @return aoMapForWorkflow
	 * @throws ApplicationException
	 */
	public Map<String, String> updateMapTOUpdateBusinessAppTable(Map<String, String> aoMapForWorkflow,
			String asWorkflowId) throws ApplicationException
	{
		aoMapForWorkflow.put("workflowId", asWorkflowId);
		return aoMapForWorkflow;
	}

	/**
	 * This method retrieves all the service application ids corresponding to
	 * one business application id.
	 * 
	 * @param aoBusinessWithdrawl Map containing service Application Id
	 * @param aoMybatisSession Mybatis session required to make the database
	 *            transaction
	 * @return loResult Withdraw Request Details information
	 * @throws ApplicationException
	 */
	public List<WithdrawRequestDetails> getServiceApplicationIds(Map<String, String> aoBusinessWithdrawl,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		List<WithdrawRequestDetails> loResult = null;
		loResult = (List<WithdrawRequestDetails>) DAOUtil.masterDAO(aoMybatisSession, aoBusinessWithdrawl,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getServiceApplicationIds",
				"java.util.Map");
		return loResult;
	}

	/**
	 * This method retrieves the service name corresponding to service
	 * application id.
	 * 
	 * @param aoServiceWithdrawl ap containing service Application Id
	 * @param aoMybatisSession Mybatis session required to make the database
	 *            transaction
	 * @return loResult Withdraw Request Details information
	 * @throws ApplicationException
	 */
	public WithdrawRequestDetails getServiceName(Map<String, String> aoServiceWithdrawl, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		WithdrawRequestDetails loResult = null;
		loResult = (WithdrawRequestDetails) DAOUtil.masterDAO(aoMybatisSession, aoServiceWithdrawl,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getServiceName", "java.util.Map");
		return loResult;
	}

	/**
	 * This method is used to update aoNewOrgNameMap
	 * 
	 * @param aoNewOrgNameMap Map as input
	 * @param asWorkflowId Workflow Number
	 * @return aoNewOrgNameMap updated map as output
	 * @throws ApplicationException
	 */
	public Map<String, String> updateOrgLegalNameMap(Map<String, String> aoNewOrgNameMap, String asWorkflowId)
			throws ApplicationException
	{
		aoNewOrgNameMap.put("asWorkflowId", asWorkflowId);
		return aoNewOrgNameMap;
	}

	/**
	 * This method insert proposed name change details details in
	 * ORGANIZATION_NAME_CHANGE Table
	 * 
	 * @param aonewOrgNameMap Map as input from prvious transaction
	 * @param aoMybatisSession Mybatis session required to make the database
	 *            transaction
	 * @return lbInsertStatus Insert is successful it its true
	 * @throws ApplicationException
	 */
	public boolean insertOrgNameChange(Map<String, String> aonewOrgNameMap, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		boolean lbInsertStatus = false;
		DAOUtil.masterDAO(aoMybatisSession, aonewOrgNameMap,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "insertOrgNameChange", "java.util.Map");
		lbInsertStatus = true;
		return lbInsertStatus;
	}

	/**
	 * This method insert withdrawal details in SUPERSEDING_STATUS Table
	 * 
	 * @param aoWithdrawalMap Map containg supersending related info
	 * @param aoMybatisSession Mybatis session required to make the database
	 *            transaction
	 * @return lbInsertStatus Insert is successful it its true
	 * @throws ApplicationException
	 */
	public boolean insertSuperSedingData(Map<String, String> aoWithdrawalMap, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		boolean lbInsertStatus = false;
		DAOUtil.masterDAO(aoMybatisSession, aoWithdrawalMap,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "insertSuperSedingData", "java.util.Map");
		lbInsertStatus = true;
		return lbInsertStatus;
	}

	/**
	 * This method insert Accounting period details in DOC_LAPSING_RULES_MASTER
	 * Table
	 * 
	 * @param aoNewAccountPeriodMap map as input
	 * @param aoMybatisSession Mybatis session required to make the database
	 *            transaction
	 * @return lbInsertStatus Insert is successful it its true
	 * @throws ApplicationException
	 */
	public boolean insertNewAccountingPeriod(Map<String, Object> aoNewAccountPeriodMap, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		boolean lbInsertStatus = false;
		DAOUtil.masterDAO(aoMybatisSession, aoNewAccountPeriodMap,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "insertNewAccountingPeriod",
				"java.util.Map");
		lbInsertStatus = true;
		return lbInsertStatus;
	}

	/**
	 * This method deletes Accounting period details for existing user from
	 * DOC_LAPSING_RULES_MASTER Table
	 * 
	 * @param aoNewAccountPeriodMap Map conataining new account period
	 *            information
	 * @param aoMybatisSession Mybatis session required to make the database
	 *            transaction
	 * @return lbInsertStatus delete is successful it its true
	 * @throws ApplicationException
	 */
	public boolean deleteExistingAccountingPeriod(Map<String, Object> aoNewAccountPeriodMap, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		boolean lbInsertStatus = false;
		DAOUtil.masterDAO(aoMybatisSession, aoNewAccountPeriodMap,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "deleteExistingAccountingPeriod",
				"java.util.Map");
		lbInsertStatus = true;
		return lbInsertStatus;
	}

	/**
	 * This method check if there is a entry in staff_details for title
	 * Executive Director/Chief Executive Officer (or equivalent).
	 * 
	 * @param asOrgId Organization Id as input
	 * @param asTitle Staff Title
	 * @param aoMybatisSession Mybatis session required to make the database
	 *            transaction
	 * @return lbSelectStatus select is successful it its true
	 * @throws ApplicationException
	 */
	public Boolean getCfoEntry(String asOrgId, String asTitle, String asBussAppStatus, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		Boolean lbSelectStatus = false;
		boolean lbSkipService = false;
		lbSkipService = BusinessApplicationUtil.getServiceApplicationStatus(asBussAppStatus);
		if (lbSkipService)
		{
			if (asTitle != null)
			{
				Map<String, Object> loCfoEntryMap = new HashMap<String, Object>();
				loCfoEntryMap.put("asOrgId", asOrgId);
				loCfoEntryMap.put("asTitle", asTitle);
				StaffDetails loGetCfoEntry = null;
				loGetCfoEntry = (StaffDetails) DAOUtil.masterDAO(aoMybatisSession, loCfoEntryMap,
						ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getCfoEntry", "java.util.Map");
				if (loGetCfoEntry != null && loGetCfoEntry.getMsStaffTitle() != null)
				{
					lbSelectStatus = true;
				}
			}
		}
		return lbSelectStatus;
	}

	/**
	 * This method fetch all the draft services of a business application from
	 * database
	 * 
	 * @param asBusinessAppId Business application Id
	 * @param aoMybatisSession Mybatis session required to make the database
	 *            transaction
	 * @return loResult List having Withdraw Request Details
	 * @throws ApplicationException
	 */
	public List<WithdrawRequestDetails> getDraftServiceApplicationId(String asBusinessAppId, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		List<WithdrawRequestDetails> loResult = null;
		Map<String, String> loServiceInfoMap = new HashMap<String, String>();
		loServiceInfoMap.put("businessAppId", asBusinessAppId);
		loServiceInfoMap.put("businessAppStatus", ApplicationConstants.COMPLETED_STATE.toLowerCase());
		loResult = (List<WithdrawRequestDetails>) DAOUtil.masterDAO(aoMybatisSession, loServiceInfoMap,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getDraftServiceApplicationId",
				"java.util.Map");
		return loResult;
	}

	/**
	 * This method give us the count of approved provider to display on business
	 * application home page.
	 * 
	 * @param aoMybatisSession Mybatis session required to make the database
	 *            transaction
	 * @param asProviderStatus Approved status is passed
	 * @return liApprovedProviderStatus Get the number of approved request
	 * @throws ApplicationException
	 */
	public Integer getApprovedProviderStatus(SqlSession aoMybatisSession, String asProviderStatus)
			throws ApplicationException
	{
		Integer liApprovedProviderStatus = null;
		liApprovedProviderStatus = (Integer) DAOUtil.masterDAO(aoMybatisSession, asProviderStatus,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getApprovedProviderStatus",
				"java.lang.String");
		return liApprovedProviderStatus;
	}

	/**
	 * This method give us the count of draft , in review, returned for revision
	 * provider status to display on business application home page.
	 * 
	 * @param aoMybatisSession Mybatis session required to make the database
	 *            transaction
	 * @param asProviderStatus Status draft, in review or returned for revision
	 *            is passed
	 * @param asOrgId Organization Id
	 * @return loDraftReviewRevisionStatusMap
	 * @throws ApplicationException
	 */
	public List<Map<String, Object>> getDraftReviewRevisionProviderStatus(SqlSession aoMybatisSession,
			String asProviderStatus, String asOrgId) throws ApplicationException
	{
		List<Map<String, Object>> loDraftReviewRevisionStatusMap = null;
		Map<String, Object> loDraftReviewRevisionProviderStatusMap = new LinkedHashMap<String, Object>();
		loDraftReviewRevisionProviderStatusMap.put("asDraftStatus",
				ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_DRAFT);
		loDraftReviewRevisionProviderStatusMap.put("asInReviewStatus",
				ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_IN_REVIEW);
		loDraftReviewRevisionProviderStatusMap.put("asReturnedStatus",
				ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS);
		loDraftReviewRevisionProviderStatusMap.put("asOrgId", asOrgId);
		loDraftReviewRevisionStatusMap = (List<Map<String, Object>>) DAOUtil.masterDAO(aoMybatisSession,
				loDraftReviewRevisionProviderStatusMap, ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER,
				"getDraftReviewRevisionProviderStatus", "java.util.Map");
		return loDraftReviewRevisionStatusMap;
	}

	/**
	 * This method fetch all provider comments entry from APPLICATION_AUDIT
	 * Table
	 * 
	 * @param asAppId Application Id
	 * @param asProviderComments Provider comments are passed
	 * @param aoMybatisSession Mybatis session required to make the database
	 *            transaction
	 * @return loResultList Give the data to be displayed as comment for
	 *         business application summary
	 * @throws ApplicationException
	 */
	public List<ApplicationAuditBean> fetchBusinessAppSummaryComments(String asAppId, String asProviderComments,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		Map<String, Object> loHMApplicationAudit = new HashMap<String, Object>();
		loHMApplicationAudit.put("asAppId", asAppId);
		loHMApplicationAudit.put("asProviderComments", asProviderComments);
		loHMApplicationAudit.put("asBasics", ApplicationConstants.BUSINESS_APPLICATION_SECTION_BASICS);
		loHMApplicationAudit.put("asFilings", ApplicationConstants.BUSINESS_APPLICATION_SECTION_FILINGS);
		loHMApplicationAudit.put("asBoard", ApplicationConstants.BUSINESS_APPLICATION_SECTION_BOARD);
		loHMApplicationAudit.put("asPolicies", ApplicationConstants.BUSINESS_APPLICATION_SECTION_POLICIES);
		List<ApplicationAuditBean> loResultList = (List<ApplicationAuditBean>) DAOUtil.masterDAO(aoMybatisSession,
				loHMApplicationAudit, ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER,
				"fetchBusinessAppSummaryComments", "java.util.Map");

		return loResultList;
	}

	/**
	 * This method retrieves the provider status from organization table.
	 * 
	 * @param asOrgId Organization Id
	 * @param aoMybatisSession Mybatis session required to make the database
	 *            transaction
	 * @return loResult Result of provider status
	 * @throws ApplicationException
	 */
	public WithdrawRequestDetails getProviderStatus(String asOrgId, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		WithdrawRequestDetails loResult = null;
		loResult = (WithdrawRequestDetails) DAOUtil.masterDAO(aoMybatisSession, asOrgId,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getProviderStatus", "java.lang.String");
		return loResult;
	}

	/**
	 * This method is used to get the Business Application Updated status.
	 * 
	 * @param asBussAppId Business application Id
	 * @param asOrdId Organization Id
	 * @param aoMyBatisSession Mybatis session required to make the database
	 *            transaction
	 * @return loUpdatedStatus Result of business status
	 * @throws ApplicationException
	 */

	public Map<String, Object> getBussAppUpdatedStatus(final String asBussAppId, final String asOrdId,
			final SqlSession aoMyBatisSession) throws ApplicationException
	{
		Map<String, Object> loParamMap = new LinkedHashMap<String, Object>();
		loParamMap.put("bussAppId", asBussAppId);
		loParamMap.put("asOrdId", asOrdId);
		Map<String, Object> loUpdatedStatus = null;
		java.util.Date ldExpirationDate = null;
		List<Map<String, Object>> loBusinessAppList = (List<Map<String, Object>>) DAOUtil.masterDAO(aoMyBatisSession,
				asOrdId, ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getAllApplicationsOfProvider",
				"java.lang.String");
		int liListSize = loBusinessAppList.size();
		if (liListSize == 1)
		{
			loUpdatedStatus = (Map<String, Object>) DAOUtil.masterDAO(aoMyBatisSession, loParamMap,
					ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getBussAppUpdatedStatus",
					"java.util.Map");
		}
		else
		{
			for (Map<String, Object> loMap : loBusinessAppList)
			{
				String lsBusinessAppId = (String) loMap.get("BUSINESS_APPLICATION_ID");
				if (lsBusinessAppId != null && lsBusinessAppId.equalsIgnoreCase(asBussAppId))
				{
					ldExpirationDate = (java.util.Date) loMap.get("EXPIRATION_DATE");
					if (liListSize > 1)
					{
						loUpdatedStatus = (Map<String, Object>) DAOUtil.masterDAO(aoMyBatisSession, loParamMap,
								ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER,
								"getBussAppUpdatedStatus", "java.util.Map");
						java.util.Date ldCurrentDate = DateUtil.getDate(DateUtil.getCurrentDate());
						if (ldExpirationDate != null && ldCurrentDate.after(ldExpirationDate))
						{
							if (loUpdatedStatus == null)
							{
								loUpdatedStatus = new HashMap<String, Object>();
							}
							loUpdatedStatus.put("expired", "expired");
						}
					}
					else
					{
						loUpdatedStatus = (Map<String, Object>) DAOUtil.masterDAO(aoMyBatisSession, loParamMap,
								ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER,
								"getBussAppUpdatedStatus", "java.util.Map");
					}
				}
				liListSize--;
			}
		}
		return loUpdatedStatus;
	}

	/**
	 * This method is used to get the Service Application Updated status.
	 * 
	 * @param asServiceAppId Service application Id
	 * @param asOrdId Organization Id
	 * @param aoMyBatisSession Mybatis session required to make the database
	 *            transaction
	 * @return loUpdatedStatus Result of service status
	 * @throws ApplicationException
	 */
	public Map<String, Object> getServiceAppUpdatedStatus(final String asServiceAppId, final String asOrdId,
			final SqlSession aoMyBatisSession) throws ApplicationException
	{
		Map<String, Object> loParamMap = new LinkedHashMap<String, Object>();
		loParamMap.put("serviceAppId", asServiceAppId);
		loParamMap.put("asOrdId", asOrdId);
		Map<String, Object> loUpdatedStatus = (Map<String, Object>) DAOUtil.masterDAO(aoMyBatisSession, loParamMap,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getServiceAppUpdatedStatus",
				"java.util.Map");
		return loUpdatedStatus;
	}

	/**
	 * This method RETRIEVES THE DETAILS OF AN BUSINESS APPLICATION
	 * 
	 * @param aoDisplayAppHIstoryMap Map as input
	 * @param aoMybatisSession Mybatis session required to make the database
	 *            transaction
	 * @return lbResult Result of application history
	 * @throws ApplicationException
	 */
	public List<Map<String, Object>> displayApplicationHistoryInfo(Map<String, String> aoDisplayAppHIstoryMap,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		List<Map<String, Object>> lbResult = null;
		aoDisplayAppHIstoryMap.put("basics", ApplicationConstants.BUSINESS_APPLICATION_SECTION_BASICS);
		aoDisplayAppHIstoryMap.put("filings", ApplicationConstants.BUSINESS_APPLICATION_SECTION_FILINGS);
		aoDisplayAppHIstoryMap.put("board", ApplicationConstants.BUSINESS_APPLICATION_SECTION_BOARD);
		aoDisplayAppHIstoryMap.put("policies", ApplicationConstants.BUSINESS_APPLICATION_SECTION_POLICIES);
		lbResult = (List<Map<String, Object>>) DAOUtil.masterDAO(aoMybatisSession, aoDisplayAppHIstoryMap,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "displayApplicationHistoryInfo",
				"java.util.Map");
		for (Map<String, Object> loMap : lbResult)
		{
			if (loMap.get("USER_TYPE") != null && loMap.get("USER_TYPE").equals(ApplicationConstants.CITY_TYPE))
			{
				loMap.put("USER_ID", ApplicationConstants.ACCELERATOR);
			}
		}
		return lbResult;
	}

	/**
	 * This method RETRIEVES THE DETAILS OF AN SERVICE APPLICATION
	 * 
	 * @param aoDisplayAppHIstoryMap Map as input
	 * @param aoMybatisSession Mybatis session required to make the database
	 *            transaction
	 * @return lbResult Result of serivce application history
	 * @throws ApplicationException
	 */
	public List<Map<String, Object>> displayServiceApplicationHistoryInfo(Map<String, String> aoDisplayAppHIstoryMap,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		List<Map<String, Object>> lbResult = null;
		lbResult = (List<Map<String, Object>>) DAOUtil.masterDAO(aoMybatisSession, aoDisplayAppHIstoryMap,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "displayServiceApplicationHistoryInfo",
				"java.util.Map");
		for (Map<String, Object> loMap : lbResult)
		{
			if (loMap.get("USER_TYPE") != null && loMap.get("USER_TYPE").equals(ApplicationConstants.CITY_TYPE))
			{
				loMap.put("USER_ID", ApplicationConstants.ACCELERATOR);
			}
		}
		return lbResult;
	}

	/**
	 * This method updates the status of provider in organization table
	 * 
	 * @param aoOrgDetails Map having organization details
	 * @param aoMybatisSession Mybatis session required to make the database
	 *            transaction
	 * @return lbStatus update is success if its true
	 * @throws ApplicationException
	 */
	public boolean updateOrganizationTable(Map<String, String> aoOrgDetails, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		boolean lbStatus = false;
		// Defect #6201 Fix: Blocking Organization table update in case if New
		// Provider Status is empty or null
		if (null != aoOrgDetails && null != aoOrgDetails.get("orgStatus") && !aoOrgDetails.get("orgStatus").isEmpty())
		{
			DAOUtil.masterDAO(aoMybatisSession, aoOrgDetails,
					ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "updateOrganizationTable",
					"java.util.Map");
			lbStatus = true;
		}
		return lbStatus;
	}

	/**
	 * This method fetch the status of provider from organization table
	 * 
	 * @param asOrgId Organization Id
	 * @param aoMybatisSession Mybatis session required to make the database
	 *            transaction
	 * @return lbStatus result of organization status
	 * @throws ApplicationException
	 */
	public String fetchOrganizationStatus(String asOrgId, SqlSession aoMybatisSession) throws ApplicationException
	{
		String lbStatus = "";
		lbStatus = (String) DAOUtil.masterDAO(aoMybatisSession, asOrgId,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "fetchOrganizationStatus",
				"java.lang.String");

		return lbStatus;
	}

	/**
	 * This method retrieves the withdrawal status of application
	 * 
	 * @param asBusinessAppId Business application status
	 * @param aoMybatisSession Mybatis session required to make the database
	 *            transaction
	 * @return lbWithdrawStatus Result of business application withdrawal status
	 * @throws ApplicationException
	 */
	public String fetchBussAppWithdrawStatus(String asBusinessAppId, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		String lbWithdrawStatus = "";
		lbWithdrawStatus = (String) DAOUtil.masterDAO(aoMybatisSession, asBusinessAppId,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "fetchBussAppWithdrawStatus",
				"java.lang.String");

		return lbWithdrawStatus;
	}

	/**
	 * This method retrieves the withdrawal status of Service application
	 * 
	 * @param asServiceAppId Service application status
	 * @param aoMybatisSession Mybatis session required to make the database
	 *            transaction
	 * @return lbWithdrawStatus Result of service application withdrawal status
	 * @throws ApplicationException
	 */
	public String fetchServiceAppWithdrawStatus(String asServiceAppId, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		String lsWithdrawStatus = "";
		lsWithdrawStatus = (String) DAOUtil.masterDAO(aoMybatisSession, asServiceAppId,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "fetchServiceAppWithdrawStatus",
				"java.lang.String");
		if (lsWithdrawStatus == null || lsWithdrawStatus.trim().length() <= 0)
		{
			lsWithdrawStatus = (String) DAOUtil.masterDAO(aoMybatisSession, asServiceAppId,
					ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "serviceBusAppWithdrwStatus",
					"java.lang.String");
		}

		return lsWithdrawStatus;
	}

	/**
	 * This method updates the accounting period in organization table
	 * 
	 * @param aoOrgDetails Map having organization details
	 * @param aoMybatisSession Mybatis session required to make the database
	 *            transaction
	 * @return lbStatus update is success if its true
	 * @throws ApplicationException
	 */
	public boolean updateOrgAccountingPeriod(Map<String, String> aoNewAccountPeriodMap, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		boolean lbStatus = false;
		DAOUtil.masterDAO(aoMybatisSession, aoNewAccountPeriodMap,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "updateOrgAccountingPeriod",
				"java.util.Map");
		lbStatus = true;
		return lbStatus;
	}

	/**
	 * This method retrieves the status of Service application
	 * 
	 * @param asServiceAppId Service application status
	 * @param aoMybatisSession Mybatis session required to make the database
	 *            transaction
	 * @return lbWithdrawStatus Result of service application withdrawal status
	 * @throws ApplicationException
	 */
	public String fetchServiceAppStatus(String asServiceAppId, SqlSession aoMybatisSession) throws ApplicationException
	{
		String lbServiceStatus = "";
		lbServiceStatus = (String) DAOUtil.masterDAO(aoMybatisSession, asServiceAppId,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "fetchServiceAppStatus",
				"java.lang.String");

		return lbServiceStatus;
	}

	/**
	 * This method retrieves the status of Service application
	 * 
	 * @param asServiceAppId Service application status
	 * @param aoMybatisSession Mybatis session required to make the database
	 *            transaction
	 * @return lbWithdrawStatus Result of service application withdrawal status
	 * @throws ApplicationException
	 */
	public String fetchServiceWorkFlowId(String asServiceAppId, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		String lbServiceWorkFlowId = "";
		lbServiceWorkFlowId = (String) DAOUtil.masterDAO(aoMybatisSession, asServiceAppId,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "fetchServiceWorkFlowId",
				"java.lang.String");

		return lbServiceWorkFlowId;
	}

	/**
	 * This method update details in Service_Application Table
	 * 
	 * @param asServiceIdOrgId Map containing service and organization Id
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return lbUpdateStatus Update is successful if its true
	 * @throws ApplicationException
	 */
	public boolean updateServiceApplicationForReSubmission(Map<String, String> asServiceIdOrgId,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		boolean lbUpdateStatus = false;
		DAOUtil.masterDAO(aoMybatisSession, asServiceIdOrgId,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER,
				"updateServiceApplicationForReSubmission", "java.util.Map");
		lbUpdateStatus = true;
		return lbUpdateStatus;
	}

	/**
	 * This method number of application against the organization id
	 * 
	 * @param asOrgId organization id
	 * @param aoMybatisSession session object
	 * @return list
	 * @throws ApplicationException application exception
	 */
	public ApplicationSummary numberOfBrAppAgainstOrg(final Map<String, Object> aoInputParams,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		List<ApplicationSummary> lbResult = null;
		lbResult = (List<ApplicationSummary>) DAOUtil.masterDAO(aoMybatisSession, aoInputParams,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "numberOfBrAppAgainstOrg",
				"java.util.Map");

		ApplicationSummary loAppSummaryObj = new ApplicationSummary();
		Integer liCounter = 0;
		if (lbResult != null && !lbResult.isEmpty())
		{
			liCounter = lbResult.size();
			if (liCounter > 1)
			{
				loAppSummaryObj.setMoreThanOne(true);
			}
			else
			{
				loAppSummaryObj.setMoreThanOne(false);
			}
			if (!lbResult.isEmpty())
			{
				if (ApplicationConstants.FINAL_VIEW_STATUSES.contains(lbResult.get(HHSConstants.INT_ZERO)
						.getMsAppStatus().toLowerCase()))
				{
					loAppSummaryObj.setCreatedDate(lbResult.get(HHSConstants.INT_ZERO).getCreatedDate());
					loAppSummaryObj.setTopBusinessAppId(null);
				}
				else
				{
					loAppSummaryObj.setTopBusinessAppId(lbResult.get(HHSConstants.INT_ZERO).getTopBusinessAppId());
					loAppSummaryObj.setCreatedDate(lbResult.get(HHSConstants.INT_ZERO).getCreatedDate());
				}
			}
		}
		else
		{
			loAppSummaryObj.setMoreThanOne(false);
			loAppSummaryObj.setTopBusinessAppId(null);
		}
		return loAppSummaryObj;
	}

	/**
	 * This method fetches the accounting period information.
	 * 
	 * @param asOrgId - organization id
	 * @param aoMybatisSession - MybatisSession
	 * @return - loAccInfo
	 * @throws ApplicationException - throws ApplicationException
	 */
	public String fetchAccReqInfo(Map<String, Object> aoNewAccountPeriodMap, SqlSession aoMybatisSession)
			throws ApplicationException
	{

		String lsOrgId = (String) aoNewAccountPeriodMap.get("providerId");
		List<Map<String, Object>> loLDocLapsinInfo = (List<Map<String, Object>>) DAOUtil.masterDAO(aoMybatisSession,
				lsOrgId, ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "fetchAccReqInfo",
				"java.lang.String");
		String lsUserId = (String) aoNewAccountPeriodMap.get("userId");
		boolean lbCanInsertDueDate = false;
		String lsStatus = "yes";
		int liYearEffective = 0;
		if (aoNewAccountPeriodMap.get("effectiveYear") != null)
		{
			liYearEffective = Integer.parseInt(aoNewAccountPeriodMap.get("effectiveYear").toString());
		}
		if (loLDocLapsinInfo == null || loLDocLapsinInfo.isEmpty())
		{
			int liCurrentYear = Calendar.getInstance().get(Calendar.YEAR) + 1;
			if (liYearEffective >= ApplicationConstants.ACCOUNTING_PERIOD_START_RANGE
					&& liYearEffective <= liCurrentYear)
			{
				DAOUtil.masterDAO(aoMybatisSession, aoNewAccountPeriodMap,
						ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "updateOrgAccountingPeriod",
						"java.util.Map");
			}
			else
			{
				lsStatus = "Year can only be in the range of " + ApplicationConstants.ACCOUNTING_PERIOD_START_RANGE
						+ " to " + liCurrentYear;
			}
		}
		else if (loLDocLapsinInfo.size() == 1)
		{
			Map<String, Object> loAccInfo = loLDocLapsinInfo.get(0);
			String lsStartMonth = (String) loAccInfo.get("STARTMONTH");
			int liStartYear = Integer.valueOf(loAccInfo.get("STARTYEAR").toString());
			Calendar loStartDate = DocumentLapsingUtility.getCalendar(lsStartMonth, liStartYear);

			String lsEndMonth = (String) loAccInfo.get("ENDMONTH");
			int liEndYear = Integer.valueOf(loAccInfo.get("ENDYEAR").toString());
			Calendar loEndDate = DocumentLapsingUtility.getCalendar(lsEndMonth, liEndYear);

			String lsNewStartMonth = (String) aoNewAccountPeriodMap.get("newStartMonth");
			int liNewStartYear = Integer.valueOf((String) aoNewAccountPeriodMap.get("effectiveYear"));
			Calendar loNextEffectiveDate = DocumentLapsingUtility.getCalendar(lsNewStartMonth, liNewStartYear);
			if ((loNextEffectiveDate.before(loEndDate) || (lsNewStartMonth + "-" + liNewStartYear)
					.equalsIgnoreCase(lsEndMonth + "-" + liEndYear)) && loNextEffectiveDate.after(loStartDate))
			{
				String lsWorkFlowStatus = null;
				lbCanInsertDueDate = true;
				if (loAccInfo.containsKey("FILLINGSTATUS") && loAccInfo.get("FILLINGSTATUS") != null)
				{
					lsWorkFlowStatus = (String) loAccInfo.get("FILLINGSTATUS");
					if (lsWorkFlowStatus.equalsIgnoreCase("In Progress"))
					{
						lbCanInsertDueDate = false;
						lsStatus = "You cannot change your organization's accounting period until all CHAR500s for the old period have been uploaded and approved.";
					}
				}
			}
			else if (lsStartMonth != null && lsNewStartMonth != null && lsNewStartMonth.equalsIgnoreCase(lsStartMonth)
					&& liStartYear == liNewStartYear)
			{
				lsStatus = "The new accounting period must be different from the old accounting period. Click Cancel if the accounting period is not changing.";
				lbCanInsertDueDate = false;
			}
			else
			{
				lbCanInsertDueDate = false;

				lsStatus = "You cannot change your organization's accounting period until all CHAR500s for the old period have been uploaded and approved.";
			}

			if (lbCanInsertDueDate)
			{
				// update account period and set due date
				String lsLawType = (String) DAOUtil.masterDAO(aoMybatisSession, lsOrgId,
						ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER, "getLawType", "java.lang.String");
				String lsNewEndMonth = (String) aoNewAccountPeriodMap.get("newEndMonth");
				Map<String, Object> loMapToUpdateAccountPeriod = DocumentLapsingUtility
						.calculateDueDateOnFiscalYearChange(lsStartMonth, lsEndMonth, lsNewStartMonth, lsNewEndMonth,
								liNewStartYear, lsLawType);
				Map<String, Object> loNewAccountPeriodMap = (Map<String, Object>) loMapToUpdateAccountPeriod
						.get("loDuedateDetailsMap");
				Date lsDueDate = (Date) loNewAccountPeriodMap.get("DUE_DATE");
				aoNewAccountPeriodMap.put("DUE_DATE", lsDueDate);
				if (lsUserId != null)
				{
					aoNewAccountPeriodMap.put("userId", lsUserId);
				}
				aoNewAccountPeriodMap.putAll(loNewAccountPeriodMap);
				Map<String, Object> loNewAccountPeriodMapUpload2 = (Map<String, Object>) loMapToUpdateAccountPeriod
						.get("loShortFiling");
				loNewAccountPeriodMapUpload2.put("providerId", lsOrgId);
				loNewAccountPeriodMapUpload2.put("uploadOrder", 1);
				loNewAccountPeriodMapUpload2.put("DUE_DATE", lsDueDate);
				if (lsUserId != null)
				{
					loNewAccountPeriodMapUpload2.put("userId", lsUserId);
				}

				DAOUtil.masterDAO(aoMybatisSession, aoNewAccountPeriodMap,
						ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "deleteExistingAccountingPeriod",
						"java.util.Map");
				DAOUtil.masterDAO(aoMybatisSession, aoNewAccountPeriodMap,
						ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "insertNewAccountingPeriod",
						"java.util.Map");
				DAOUtil.masterDAO(aoMybatisSession, loNewAccountPeriodMapUpload2,
						ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "insertNewAccountingPeriod",
						"java.util.Map");

			}

		}
		else
		{
			lsStatus = "Please upload Short Filing document before changing your accounting period again.";
		}
		return lsStatus;
	}

	/**
	 * This method update contract details in STAFF_DETAILS Table
	 * 
	 * @param aoExistingStaff Map having staff related details(staff id and
	 *            organization Id) required for updation.
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return lbInsertStatus Insertion is successful if its true
	 * @throws ApplicationException - throws ApplicationException
	 */
	public boolean updateServiceStaffDetails(Map<String, Object> aoStaffMapping, StaffDetails aoExistingStaff,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		boolean lbInsertStatus = false;
		StaffDetails loStaffDetails = null;
		if (aoExistingStaff.getMsStaffTitle().equalsIgnoreCase("1")
				&& !((String) aoStaffMapping.get("oldTitle")).equalsIgnoreCase("1"))
		{
			Map<String, String> loStaffInfo = new HashMap<String, String>();
			loStaffInfo.put("asOrgId", aoExistingStaff.getMsOrgId());
			loStaffInfo.put("asCEOId", aoExistingStaff.getMsStaffTitle());
			loStaffDetails = (StaffDetails) DAOUtil.masterDAO(aoMybatisSession, loStaffInfo,
					ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "checkCEOOfficer", "java.util.Map");
		}
		if (loStaffDetails == null)
		{
			DAOUtil.masterDAO(aoMybatisSession, aoExistingStaff,
					ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "updateServiceStaffDetails",
					"com.nyc.hhs.model.StaffDetails");
			DAOUtil.masterDAO(aoMybatisSession, aoExistingStaff,
					ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER,
					"updateServiceStaffOrgMappingDetails", "com.nyc.hhs.model.StaffDetails");
			String loResult = null;
			loResult = (String) DAOUtil.masterDAO(aoMybatisSession, aoStaffMapping,
					ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "checkStaffMapping", "java.util.Map");
			if (loResult == null)
			{
				DAOUtil.masterDAO(aoMybatisSession, aoStaffMapping,
						ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "insertStaffMapping",
						"java.util.Map");
			}
			lbInsertStatus = true;
		}
		return lbInsertStatus;
	}

	/**
	 * This method retrieves the status of application
	 * 
	 * @param asBussAppId application id
	 * @param aoMybatisSession Mybatis session required to make the database
	 *            transaction
	 * @return lbAppStatus Result of application status
	 * @throws ApplicationException
	 */
	public String fetchAppStatus(String asBussAppId, SqlSession aoMybatisSession) throws ApplicationException
	{
		String lbAppStatus = "";
		lbAppStatus = (String) DAOUtil.masterDAO(aoMybatisSession, asBussAppId,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "fetchAppStatus", "java.lang.String");

		return lbAppStatus;
	}

	/**
	 * This method RETRIEVES THE DETAILS OF AN SERVICE APPLICATION
	 * 
	 * @param aoServiceInfoMap Map as input
	 * @param aoMybatisSession Mybatis session required to make the database
	 *            transaction
	 * @return lbResult Result of serivce application history
	 * @throws ApplicationException
	 */
	public List<Map<String, Object>> getServiceComments(Map<String, String> aoServiceInfoMap,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		List<Map<String, Object>> lbResult = null;
		aoServiceInfoMap.put("userType", "City");
		aoServiceInfoMap.put("providerComments", P8Constants.PROPERTY_PE_TH_PROVIDER_COMMENT);
		lbResult = (List<Map<String, Object>>) DAOUtil.masterDAO(aoMybatisSession, aoServiceInfoMap,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getServiceComments", "java.util.Map");
		for (Map<String, Object> loMap : lbResult)
		{
			if (loMap.get("USER_TYPE") != null && loMap.get("USER_TYPE").equals(ApplicationConstants.CITY_TYPE))
			{
				loMap.put("USER_ID", ApplicationConstants.ACCELERATOR);
			}
		}
		return lbResult;
	}

	/**
	 * This method RETRIEVES THE DETAILS OF AN SERVICE APPLICATION
	 * 
	 * @param aoServiceInfoMap Map as input
	 * @param aoMybatisSession Mybatis session required to make the database
	 *            transaction
	 * @return lbResult Result of serivce application history
	 * @throws ApplicationException
	 */
	public Boolean getDeactivatedService(Map<String, Object> aoServiceInfoMap, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		String lsServiceType = (String) aoServiceInfoMap.get("serviceType");
		List<String> loStatusList = new ArrayList<String>();

		if (null != lsServiceType && lsServiceType.equalsIgnoreCase("new"))
		{
			loStatusList.add(ApplicationConstants.STATUS_DRAFT.toLowerCase());
			loStatusList.add(ApplicationConstants.NOT_STARTED_STATE.toLowerCase());
			loStatusList.add(ApplicationConstants.COMPLETED_STATE.toLowerCase());
		}
		else
		{
			loStatusList.add(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS.toLowerCase());
			loStatusList.add(ApplicationConstants.STATUS_DEFFERED.toLowerCase());
		}
		aoServiceInfoMap.put("deactivated", "Deactivated");
		aoServiceInfoMap.put("serviceStatuses", loStatusList);
		String lsDeactivateResult = null;
		Boolean lbResult = false;
		lsDeactivateResult = (String) DAOUtil.masterDAO(aoMybatisSession, aoServiceInfoMap,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getDeactivatedService", "java.util.Map");
		if (lsDeactivateResult != null && !lsDeactivateResult.equalsIgnoreCase(""))
		{
			lbResult = true;
		}
		return lbResult;
	}

	/**
	 * This method RETRIEVES THE DETAILS OF AN SERVICE APPLICATION
	 * 
	 * @param aoServiceInfoMap Map as input
	 * @param aoMybatisSession Mybatis session required to make the database
	 *            transaction
	 * @return lbResult Result of serivce application history
	 * @throws ApplicationException
	 */
	public List<Object> getDeactivatedServiceForApp(Map<String, Object> aoBusinessInfoMap, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		String lsServiceType = (String) aoBusinessInfoMap.get("serviceType");
		List<String> loStatusList = new ArrayList<String>();

		if (lsServiceType.equalsIgnoreCase("new"))
		{
			loStatusList.add(ApplicationConstants.STATUS_DRAFT.toLowerCase());
			loStatusList.add(ApplicationConstants.NOT_STARTED_STATE.toLowerCase());
			loStatusList.add(ApplicationConstants.COMPLETED_STATE.toLowerCase());
		}
		else
		{
			loStatusList.add(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS.toLowerCase());
			loStatusList.add(ApplicationConstants.STATUS_DEFFERED.toLowerCase());
		}
		aoBusinessInfoMap.put("deactivated", "Deactivated");
		aoBusinessInfoMap.put("serviceStatuses", loStatusList);
		List<Object> lsDeactivateResult = null;
		Boolean lbResult = false;
		lsDeactivateResult = (List<Object>) DAOUtil.masterDAO(aoMybatisSession, aoBusinessInfoMap,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getDeactivatedServiceForApp",
				"java.util.Map");

		if (lsDeactivateResult != null)
		{
			if (!lsDeactivateResult.isEmpty())
			{
				lbResult = true;
			}

			lsDeactivateResult.add(lbResult);
		}

		return lsDeactivateResult;

	}

	/**
	 * This code is used to get the contract details for a selected id.
	 * 
	 * @param aoContractInfo Map of Contract Id and orgnaization Id
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return loResult Contract details for a selected contract Id.
	 * @throws ApplicationException
	 */

	public String checkExistingContractId(Map<String, String> aoContractInfo, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		String loResult = null;
		loResult = (String) DAOUtil.masterDAO(aoMybatisSession, aoContractInfo,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "checkExistingContractId",
				"java.util.Map");
		return loResult;
	}

	/**
	 * This method is used to update the cache
	 * 
	 * @param aoParamMap Map of inputs
	 * @param aoMybatisSession Mybatis Sql Session
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> recacheTaxonomy(Map<String, String> aoRecacheMap, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		List<Map<String, Object>> loUpdatedStatus = (List<Map<String, Object>>) DAOUtil.masterDAO(aoMybatisSession,
				aoRecacheMap, ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "recacheTaxonomy",
				"java.util.Map");
		return loUpdatedStatus;
	}

	/**
	 * This method is used to update the status
	 * 
	 * @param aoActionMap input map
	 * @param aoMyBatisSession ibatis session
	 * @return lbStatus
	 * @throws ApplicationException application exception
	 */
	public Boolean updateRecacheTaxonomyFlag(final Map<String, Object> aoActionMap, final SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		Boolean lbStatus = true;
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoActionMap,
					ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "updateRecacheTaxonomyFlag",
					"java.util.Map");
		}
		catch (ApplicationException aoAppEx)
		{
			throw aoAppEx;
		}
		return lbStatus;
	}

	/**
	 * This method returns Superseding Stauts and Expiration date for a Business
	 * Application
	 * 
	 * @param aoQueryMap Map containing Business Application ID and Organization
	 *            ID
	 * @param aoMyBatisSession SQL MyBatis Session
	 * @return Map with Superseding Status and Expiration date for provided
	 *         Business Application ID
	 * @throws ApplicationException
	 */
	public Map<String, Object> getSupersedingandExpiry(Map<String, String> aoQueryMap, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		Map<String, Object> loStatusMap = (Map<String, Object>) DAOUtil.masterDAO(aoMyBatisSession, aoQueryMap,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, "getSupersedingandExpiry",
				"java.util.Map");
		return loStatusMap;
	}

	/**
	 * This method returns List of all service applications corresponds to
	 * business application with service status
	 * 
	 * @param aoMyBatisSession SQL MyBatis Session
	 * @param asOrgId organization Id
	 * @param asBrAppId business application id
	 * @return ApplicationSummary List of all service applications corresponds
	 *         to business application with service status
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<ApplicationSummary> selectAppInfoByOrgId(SqlSession aoMyBatisSession, String asOrgId,
			String asBrAppId) throws ApplicationException
	{
		Map<String, String> loMapSummary = new HashMap<String, String>();
		loMapSummary.put("asOrgId", asOrgId);
		loMapSummary.put("asBrAppId", asBrAppId);
		ArrayList<ApplicationSummary> loApplicationSummaryList = (ArrayList<ApplicationSummary>) DAOUtil.masterDAO(
				aoMyBatisSession, loMapSummary, ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER,
				"selectAppInfoByOrgId", "java.util.Map");
		return loApplicationSummaryList;
	}

	// R5 Starts
	/**
	 * This method returns Expiration date of latest Business Application for
	 * organization user logged in
	 * 
	 * @param aoMyBatisSession SQL MyBatis Session
	 * @param asOrgId organization Id
	 * @return lsBusinesExpDate BusinessExpirationDate
	 * @throws ApplicationException
	 */
	public String getBussAppExpiringDate(SqlSession aoMyBatisSession, String asOrdId,Map<String, Object> aoUpdatedStatus) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entering getBussAppExpiringDate");
		String lsBusinesExpDate = null;
		try
		{
			HashMap<String, String> loParam = new HashMap<String, String>();
			loParam.put(HHSConstants.USER_ORG_ID, asOrdId);
			String lsStatus = HHSConstants.EMPTY_STRING;
			if (null != aoUpdatedStatus
					&& StringUtils.isNotBlank((String) aoUpdatedStatus.get(HHSConstants.STATUS_UPPERCASE)))
			{
				lsStatus = (String) aoUpdatedStatus.get(HHSConstants.STATUS_UPPERCASE);
			}
			if (lsStatus.equalsIgnoreCase(ApplicationConstants.STATUS_EXPIRED))
			{
				loParam.put(HHSConstants.STATUS_COLUMN, HHSConstants.TRUE);
			}
			else
			{
				loParam.put(HHSConstants.STATUS_COLUMN, HHSConstants.FALSE);
			} 
			loParam.put(HHSR5Constants.STR_BUDGET_APPROVED, HHSR5Constants.STR_BUDGET_APPROVED);
			lsBusinesExpDate = (String) DAOUtil.masterDAO(aoMyBatisSession, loParam,
					HHSR5Constants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, HHSR5Constants.GET_BUSS_APP_EXP_DATE,
					HHSR5Constants.JAVA_UTIL_HASH_MAP);
		}

		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while getBussAppExpiringDate", aoAppEx);
			setMoState("Error while getBussAppExpiringDate");
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while getBussAppExpiringDate", aoExp);
			setMoState("Error while getBussAppExpiringDate");
		}
		return lsBusinesExpDate;
	}

	/**
	 * This method returns Expiration date of latest services Application for
	 * organization user logged in
	 * 
	 * @param aoMyBatisSession SQL MyBatis Session
	 * @param asOrgId organization Id
	 * @return lsServiceExpDate ServiceExpirationDate
	 * @throws ApplicationException
	 */
	public String getServiceAppExpiringDate(SqlSession aoMyBatisSession, String asOrdId) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entering getServiceAppExpiringDate");
		String lsServiceExpDate = null;
		try
		{
			HashMap<String, String> loParam = new HashMap<String, String>();
			loParam.put(HHSConstants.USER_ORG_ID, asOrdId);
			loParam.put(HHSR5Constants.SIX, HHSR5Constants.SIX);
			loParam.put(HHSR5Constants.STR_BUDGET_APPROVED, HHSR5Constants.STR_BUDGET_APPROVED);
			lsServiceExpDate = (String) DAOUtil.masterDAO(aoMyBatisSession, loParam,
					HHSR5Constants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, HHSR5Constants.GET_SERVICE_APP_EXP_DATE,
					HHSR5Constants.JAVA_UTIL_HASH_MAP);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while getServiceAppExpiringDate", aoAppEx);
			setMoState("Error while getServiceAppExpiringDate");
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while getServiceAppExpiringDate", aoExp);
			setMoState("Error while getServiceAppExpiringDate");
		}
		return lsServiceExpDate;
	}
	// R5 End
	
	
	// Start SAML QC 9165 R 7.8.0

	/**
	 * This method fetch all entry from STAFF_DETAILS Table
	 * for Active Provider Members
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return loResultList List of all the staff details
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<StaffDetails> getAllActiveOrgStaff(SqlSession aoMybatisSession) throws ApplicationException
	{
		List<StaffDetails> loResultList = null;
		try
		{
			loResultList = (List<StaffDetails>) DAOUtil.masterDAO(aoMybatisSession, null,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, ApplicationConstants.GET_ALL_ACTIVE_ORG_STAFF, null);
		
		}
		catch (ApplicationException aoAppEx)
		{
			setMoState("Transaction Failure: Error Occurred while fetching staff details for all active providers members");
			throw aoAppEx;
		}
		catch (Exception loEx)
		{
			setMoState("Transaction Failure: Error Occurred while fetching staff details for all active providers members");
			throw new ApplicationException(
					"Transaction Failure: Error Occurred while fetching staff details for all active providers members", loEx);
		}
		return loResultList;
	}
	
	/**
	 * This method is used to update the User Status
	 * in STAFF_ORGANIZATION Table
	 * for Active Provider Members
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return 
	 * @throws ApplicationException
	 */
	public Boolean updateStaffOrganizationUserStatus(SqlSession aoMyBatisSession, StaffDetails aoStaffDetails)
			throws ApplicationException
	{
		Boolean lbStatus = true;
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoStaffDetails,
					ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, HHSConstants.UPDATE_STAFF_ORGANIZATION_USER_STATUS,
					HHSConstants.INPUT_PARAM_CLASS_STAFF_DETAILS_BEAN);
		}
		catch (ApplicationException aoAppEx)
		{
			throw aoAppEx;
		}
		return lbStatus;
	}
	
	/**
	 * This method fetch the last successful update date 
	 * for syncronise Orividers mwmbers from StuffDetails table with NYCID profiles 
	 * @param componentName 
	 * @param aoMybatisSession Mybatis session required to make the database
	 *            transaction
	 * @return lbDate result 
	 * @throws ApplicationException
	 */
	public String getSettingsValue(SqlSession aoMybatisSession, String componentName) throws ApplicationException
	{
		String lbDate = "";
		try
		{
		lbDate = (String) DAOUtil.masterDAO(aoMybatisSession, componentName,
				ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, HHSConstants.GET_SETTINGS_VALUE,
				HHSConstants.JAVA_LANG_STRING );
		}
		catch (ApplicationException aoAppEx)
		{
			throw aoAppEx;
		}
		return lbDate;
	}

	/**
	 * This method is used to update the settins value
	 * in APPLICATION_SETTINGS
	 * @param aoHashMap input map
	 * @param aoMyBatisSession ibatis session
	 * @return lbStatus
	 * @throws ApplicationException application exception
	 */
	public Boolean updateSettingsValue(SqlSession aoMyBatisSession, HashMap<String, String> loParam )
			throws ApplicationException
	{
		Boolean lbStatus = true;
		LOG_OBJECT.Info("=====updateSettingsValue param ::"+ loParam);
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, loParam, 
					ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER, HHSConstants.UPDATE_SETTINGS_VALUE,
					HHSConstants.JAVA_UTIL_HASH_MAP);
		}
		catch (ApplicationException aoAppEx)
		{
			throw aoAppEx;
		}
		return lbStatus;
	}	
	// End SAML QC 9165 R 7.8.0
}
