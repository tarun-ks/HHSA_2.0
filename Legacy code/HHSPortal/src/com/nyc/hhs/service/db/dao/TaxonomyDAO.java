package com.nyc.hhs.service.db.dao;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.KeyValue;
import com.nyc.hhs.model.ServiceSummary;
import com.nyc.hhs.model.TaxonomyLinkageBean;
import com.nyc.hhs.model.TaxonomyServiceBean;
import com.nyc.hhs.model.TaxonomySynonymBean;
import com.nyc.hhs.model.TaxonomyTree;
import com.nyc.hhs.model.WithdrawRequestDetails;
import com.nyc.hhs.service.db.services.application.TaxonomyMapper;

/**
 * This DAO class provides the functionality related to Taxonomy and its
 * maintenance This includes the database operations like fetching selecting,
 * updating and deleting the Taxonomy on the basis of input parameters.
 * 
 */

public class TaxonomyDAO
{

	private static final LogInfo LOG_OBJECT = new LogInfo(TaxonomyDAO.class);

	/**
	 * This method fetches taxonomy tree to be displayed on population page.
	 * 
	 * @param aoMyBatisSession - sql session
	 * @param asTaxonomyType - taxonomy type
	 * @return - loTaxonomyTreeList
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public ArrayList getTaxonomyTreeForPopulation(SqlSession aoMyBatisSession, String asTaxonomyType)
			throws ApplicationException
	{
		ArrayList loTaxonomyTreeList = null;
		try
		{
			TaxonomyMapper loMapper = aoMyBatisSession.getMapper(TaxonomyMapper.class);
			loTaxonomyTreeList = loMapper.selectPopulationTaxonomyTree(asTaxonomyType);
		}
		catch (Exception loException)
		{
			throw new ApplicationException("Exception occured in TaxonomyDAO while getting Population tree data for :"
					+ asTaxonomyType, loException);
		}
		return loTaxonomyTreeList;
	}

	/**
	 * This method fetches taxonomy data from master table
	 * 
	 * @param aoSession - sql session
	 * @return - loTaxonomyTree
	 * @throws ApplicationException
	 */
	public List<TaxonomyTree> selectFromTaxonomyMasterTable(SqlSession aoSession) throws ApplicationException
	{
		List<TaxonomyTree> loTaxonomyTree;
		try
		{
			TaxonomyMapper loMapper = aoSession.getMapper(TaxonomyMapper.class);
			loTaxonomyTree = loMapper.selectTaxonomyDetails();
		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Exception occured while fetching Taxonomy Master Data", loEx);
			throw new ApplicationException("Exception occured while fetching Taxonomy Master Data", loEx);
		}
		return loTaxonomyTree;
	}

	/**
	 * This method fetches taxonomy data by type
	 * 
	 * @param asElementType - element type
	 * @param aoSession - sql session
	 * @return - loTaxonomyMap
	 * @throws ApplicationException
	 */
	public List<TaxonomyTree> getTaxonomyTreeByType(final String asElementType, final SqlSession aoSession)
			throws ApplicationException
	{
		List<TaxonomyTree> loTaxonomyMap = null;
		try
		{
			TaxonomyMapper loTaxonomyMapper = aoSession.getMapper(TaxonomyMapper.class);
			loTaxonomyMap = loTaxonomyMapper.getTaxonomyTreeByType(asElementType);
		}
		catch (Exception loEx)
		{
			throw new ApplicationException(
					"Exception occured while trying to fetch the data from getTaxonomyTreeByType method", loEx);
		}
		return loTaxonomyMap;
	}

	/**
	 * This method fetches data of selected service from database.
	 * 
	 * @param aoBusinessApplicationId - business application id
	 * @param asOrgId - current application id
	 * @param aoMyBatisSession - sql session
	 * @return - loServiceList
	 * @throws ApplicationException
	 */
	public List<TaxonomyServiceBean> getSelectedService(final String aoBusinessApplicationId, final String asOrgId,
			final SqlSession aoMyBatisSession) throws ApplicationException
	{
		List<TaxonomyServiceBean> loServiceList = null;
		try
		{
			// create a map to send for the ibatis input params
			Map<String, String> loQueryMap = new HashMap<String, String>();
			loQueryMap.put("aoBusinessApplicationId", aoBusinessApplicationId);
			loQueryMap.put("asOrgId", asOrgId);
			TaxonomyMapper loTaxonomyMapper = aoMyBatisSession.getMapper(TaxonomyMapper.class);
			loServiceList = loTaxonomyMapper.getSelectedService(loQueryMap);
		}
		catch (Exception loEx)
		{
			throw new ApplicationException(
					"Exception occured while trying to fetch the data from table service application", loEx);
		}
		// return the value
		return loServiceList;
	}

	/**
	 * This method save data of selected services in database.
	 * 
	 * @param aoSelectedServicesList - list of selected services
	 * @param aoApplicationId - current application id
	 * @param aoMyBatisSession - sql session
	 * @return - loSuccessStatus
	 * @throws ApplicationException
	 */
	public Boolean saveSelectedService(final List<TaxonomyServiceBean> aoSelectedServicesList,
			final String aoApplicationId, final SqlSession aoMyBatisSession) throws ApplicationException
	{
		Boolean loSuccessStatus = false;
		try
		{
			TaxonomyMapper loTaxonomyMapper = aoMyBatisSession.getMapper(TaxonomyMapper.class);
			// iterate the list of services and save into the service
			// application table
			if (aoSelectedServicesList != null && !aoSelectedServicesList.isEmpty())
			{
				for (TaxonomyServiceBean loTaxonomyServiceBean : aoSelectedServicesList)
				{
					String lsServiceElementId = loTaxonomyServiceBean.getServiceElementId();
					String lsBussAppId = loTaxonomyServiceBean.getBusinessApplicationId();
					String lsOrgId = loTaxonomyServiceBean.getOrganizationId();
					Map<String, String> loCheckForService = new HashMap<String, String>();
					loCheckForService.put("lsServiceElementId", lsServiceElementId);
					loCheckForService.put("lsBussAppId", lsBussAppId);
					loCheckForService.put("lsOrgId", lsOrgId);
					List<WithdrawRequestDetails> loWithdrawRequestDetailsList = loTaxonomyMapper
							.checkBeforeAddingService(loCheckForService);
					if (loWithdrawRequestDetailsList.isEmpty())
					{
						loTaxonomyServiceBean.setApplicationId(aoApplicationId);
						loTaxonomyMapper.saveSelectedServices(loTaxonomyServiceBean);
					}
					loSuccessStatus = true;
				}
			}
		}
		catch (Exception loException)
		{
			throw new ApplicationException("Error occured while saving data in saveSelectedService", loException);
		}
		// return the value
		return loSuccessStatus;
	}

	/**
	 * This method deletes data of selected services from database.
	 * 
	 * @param aoSelectedServicesList - list of selected services
	 * @param aoMyBatisSession - sql session
	 * @return - loDeletionSuccessStatus
	 * @throws ApplicationException
	 */
	public Boolean deleteSelectedService(final List<TaxonomyServiceBean> aoSelectedServicesList,
			final SqlSession aoMyBatisSession) throws ApplicationException
	{
		Boolean loDeletionSuccessStatus;
		try
		{
			// create a map to send for the ibatis input params
			TaxonomyMapper loTaxonomyMapper = aoMyBatisSession.getMapper(TaxonomyMapper.class);

			if (aoSelectedServicesList != null && !aoSelectedServicesList.isEmpty())
			{
				for (TaxonomyServiceBean loTaxonomyServiceBean : aoSelectedServicesList)
				{
					// where condition to delete service
					Map<String, String> loQueryMap = new HashMap<String, String>();
					loQueryMap.put("aoBusinessApplicationId", loTaxonomyServiceBean.getBusinessApplicationId());
					loQueryMap.put("asOrgId", loTaxonomyServiceBean.getOrganizationId());
					loQueryMap.put("aoServiceElementId", loTaxonomyServiceBean.getServiceElementId());
					loTaxonomyMapper.deleteSelectedService(loQueryMap);
				}
			}
			loDeletionSuccessStatus = true;
		}
		catch (Exception loException)
		{
			throw new ApplicationException(
					"Error occured while deleting data for basic population in deleteSelectedService", loException);
		}
		return loDeletionSuccessStatus;
	}

	/**
	 * This method updates data of selected services in database.
	 * 
	 * @param aoSelectedServicesList - list of selected services
	 * @param aoMyBatisSession - sql session
	 * @return - loDeletionSuccessStatus
	 * @throws ApplicationException
	 */
	public Boolean updateSelectedService(final List<TaxonomyServiceBean> aoSelectedServicesList,
			final SqlSession aoMyBatisSession) throws ApplicationException
	{
		Boolean loDeletionSuccessStatus;
		try
		{
			// create a map to send for the ibatis input params
			TaxonomyMapper loTaxonomyMapper = aoMyBatisSession.getMapper(TaxonomyMapper.class);
			if (aoSelectedServicesList != null && !aoSelectedServicesList.isEmpty())
			{
				for (TaxonomyServiceBean loTaxonomyServiceBean : aoSelectedServicesList)
				{
					Map<String, Object> loQueryMap = new HashMap<String, Object>();
					// where condition for update and change property
					loQueryMap
							.put("aoBusinessApplicationId", (String) loTaxonomyServiceBean.getBusinessApplicationId());
					loQueryMap.put("asOrgId", (String) loTaxonomyServiceBean.getOrganizationId());
					loQueryMap.put("aoServiceElementId", (String) loTaxonomyServiceBean.getServiceElementId());
					loQueryMap.put("aoModifiedBy", (String) loTaxonomyServiceBean.getModifiedBy());
					loQueryMap.put("aoModifiedDate", (Date) loTaxonomyServiceBean.getModifiedDate());
					loTaxonomyMapper.updateSelectedService(loQueryMap);
				}
			}
			loDeletionSuccessStatus = true;
		}
		catch (Exception loException)
		{
			throw new ApplicationException(
					"Error occured while deleting data for basic population in deleteSelectedService", loException);
		}
		return loDeletionSuccessStatus;
	}

	/**
	 * This method fetches geography data from database.
	 * 
	 * @param asOrgId - current organization id
	 * @param aoMyBatisSession - sql session
	 * @return - loTaxonomyIdList
	 * @throws ApplicationException
	 */
	public List<String> getSelectedGeography(String asOrgId, SqlSession aoMyBatisSession) throws ApplicationException
	{
		List<String> loTaxonomyIdList = null;

		try
		{

			TaxonomyMapper loMapper = aoMyBatisSession.getMapper(TaxonomyMapper.class);
			Map<String, Object> loRequiredFields = new HashMap<String, Object>();
			loRequiredFields.put("ElementType", ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_GEOGRAPHY);
			loRequiredFields.put("OrgId", asOrgId);
			loTaxonomyIdList = loMapper.getSelectedGeography(loRequiredFields);
		}
		catch (Exception loException)
		{
			throw new ApplicationException(
					"Error occured while getting service summary data to be populated on page in method "
							+ "getSelectedGeography", loException);
		}
		return loTaxonomyIdList;
	}

	/**
	 * This method fetches service data from database.
	 * 
	 * @param asOrgId - current organization id
	 * @param asAppId - current application id
	 * @param asBussAppId - current business application id
	 * @param abIsAfterSubmission - submission flag
	 * @param aoSession - sql session
	 * @return - loServiceDetails
	 * @throws ApplicationException
	 */
	public Map<String, Object> selectFromServiceApplicationTable(String asOrgId, String asAppId, String asBussAppId,
			boolean abIsAfterSubmission, SqlSession aoSession) throws ApplicationException
	{
		Map<String, Object> loServiceDetails = new HashMap<String, Object>();
		try
		{
			TaxonomyMapper loMapper = aoSession.getMapper(TaxonomyMapper.class);
			List<String> loServiceList = new ArrayList<String>();
			loServiceList.add("Complete");
			loServiceList.add("Draft");
			loServiceList.add("Not Started");

			Map<String, Object> loHmParameter = new HashMap<String, Object>();
			loHmParameter.put("asOrgId", asOrgId);
			loHmParameter.put("asBussAppId", asBussAppId);
			loHmParameter.put("lsAllServiceAppStatus", loServiceList);
			List<ServiceSummary> loServiceSummary = null;
			if (!abIsAfterSubmission)
			{
				loServiceSummary = loMapper.selectServiceSummaryDetails(loHmParameter);
			}
			else
			{
				loServiceSummary = loMapper.selectServiceSummaryDetailsAfterSubmit(loHmParameter);
			}
			if (loServiceSummary != null && loServiceSummary.isEmpty())
			{
				return null;
			}
			List<KeyValue> loDocStatus = loMapper.getDocumentStatus(loHmParameter);
			List<KeyValue> loAppSettingStatu = loMapper.getAppSettingStatus(loHmParameter);
			List<ServiceSummary> loGetAllDataForSpesSetting = loMapper.getAllSpecilizationSetting(loHmParameter);
			loServiceDetails.put("service_information", loServiceSummary);
			loServiceDetails.put("service_doc_status", KeyValue.convertListToMap(loDocStatus));
			loServiceDetails.put("service_app_status", KeyValue.convertListToMap(loAppSettingStatu));
			loServiceDetails.put("service_setting_info", loGetAllDataForSpesSetting);
		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Exception occured while fetching Service Summary data Data", loEx);
			throw new ApplicationException("Exception occured while fetching Service Application  Data", loEx);
		}
		return loServiceDetails;
	}

	/**
	 * This method fetches Service Information from database.
	 * 
	 * @param aoHmParameters - map containing required parameter
	 * @param aoSession - sql session
	 * @return - loServiceSummary
	 * @throws ApplicationException
	 */
	public List<KeyValue> getServiceInformation(Map<String, Object> aoHmParameters, SqlSession aoSession)
			throws ApplicationException
	{
		List<KeyValue> loServiceSummary = null;
		try
		{
			TaxonomyMapper loMapper = aoSession.getMapper(TaxonomyMapper.class);
			loServiceSummary = loMapper.getServiceInformation(aoHmParameters);
		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Exception occured while fetching Service Summary data Data", loEx);
			throw new ApplicationException("Exception occured while fetching Service Information for service ", loEx);
		}
		return loServiceSummary;
	}

	/**
	 * This method deletes data From Service Application Table
	 * @param aoMyBatisSession - sql session
	 * @param aoHmParameter - map containing required parameter
	 * @return - loServiceSummaryStatus
	 */
	public Boolean deleteFromServiceApplicationTable(SqlSession aoMyBatisSession, HashMap<String, Object> aoHmParameter)
	{

		Boolean loServiceSummaryStatus = false;
		TaxonomyMapper loMapper = aoMyBatisSession.getMapper(TaxonomyMapper.class);
		loMapper.deleteServiceSummaryDetails(aoHmParameter);
		loServiceSummaryStatus = true;
		return loServiceSummaryStatus;
	}

	/**
	 * This method fetches data From Service Application Table
	 * 
	 * @param aoMyBatisSession - sql session
	 * @param aoHmParameter - map containing required parameter
	 * @return - loLServiceSummary
	 * @throws ApplicationException
	 */
	public List<String> retrieveFromServiceApplicationTable(SqlSession aoMyBatisSession,
			HashMap<String, Object> aoHmParameter) throws ApplicationException
	{
		List<String> loLServiceSummary = null;
		try
		{
			TaxonomyMapper loMapper = aoMyBatisSession.getMapper(TaxonomyMapper.class);
			loLServiceSummary = loMapper.selectServiceSummary(aoHmParameter);
		}
		catch (Exception loException)
		{
			throw new ApplicationException("Error occured while getting service summary data to be populated on page "
					+ "in method getPopulationForApplication", loException);
		}
		return loLServiceSummary;
	}

	/**
	 * This method fetches service application ids From Service Application
	 * Table
	 * 
	 * @param asOrgId - current organization id
	 * @param asAppId - current application id
	 * @param asBussAppId - current business application id
	 * @param aoSession - sql session
	 * @return - loServiceDetails
	 * @throws ApplicationException
	 */
	public List<Map<String, Object>> selectServiceAppIDs(String asOrgId, String asAppId, String asBussAppId,
			SqlSession aoSession) throws ApplicationException
	{
		List<Map<String, Object>> loServiceDetails;
		try
		{
			TaxonomyMapper loMapper = aoSession.getMapper(TaxonomyMapper.class);
			Map<String, Object> loHmParameter = new HashMap<String, Object>();
			loHmParameter.put("asOrgId", asOrgId);
			loHmParameter.put("asBussAppId", asBussAppId);
			loServiceDetails = loMapper.selectServiceAppIDs(loHmParameter);
		}
		catch (Exception loEx)
		{
			LOG_OBJECT
					.Error("Exception occured while fetching Service Application Data for 'selectServiceAppIDs' in TaxonomyDAO",
							loEx);
			throw new ApplicationException(
					"Exception occured while fetching Service Application Data for 'selectServiceAppIDs' "
							+ "in TaxonomyDAO", loEx);
		}
		return loServiceDetails;
	}

	/**
	 * This method updates the application status on application submission
	 * 
	 * @param aoQueryMap - map containing all the required data
	 * @param aoSession - sql session
	 * @return - lbInsertStatus
	 * @throws ApplicationException
	 */
	public Boolean updateStatusAppSubmission(Map<String, Object> aoQueryMap, SqlSession aoSession)
			throws ApplicationException
	{

		Boolean lbInsertStatus = false;
		TaxonomyMapper loMapper = aoSession.getMapper(TaxonomyMapper.class);
		try
		{
			if (aoQueryMap != null
					&& ((String) aoQueryMap.get("asSectionStatus"))
							.equalsIgnoreCase(ApplicationConstants.STATUS_CONDITIONALLY_APPROVED))
			{
				loMapper.updateBAStatusOnAppSubmissionForCA(aoQueryMap);
				loMapper.updateSAStatusOnAppSubmissionForCA(aoQueryMap);
			}
			else
			{
				loMapper.updateBAStatusOnAppSubmission(aoQueryMap);
				loMapper.updateSAStatusOnAppSubmission(aoQueryMap);
			}
			loMapper.updateSectionStatusOnAppSubmission(aoQueryMap);
			loMapper.updateSSSStatusOnAppSubmission(aoQueryMap);
			loMapper.updateDocumentOnAppSubmission(aoQueryMap);
			lbInsertStatus = true;
		}
		catch (Exception loEx)
		{
			LOG_OBJECT
					.Error("Exception occured while updating status in Tables on Launch of Application submission workflow in Taxonomy DAO",
							loEx);
			throw new ApplicationException(
					"Exception occured while updating status in Tables on Launch of Application submission workflow in "
							+ "Taxonomy DAO", loEx);
		}
		return lbInsertStatus;
	}

	/**
	 * This method updates the service application status on service application
	 * submission
	 * 
	 * @param aoQueryMap - map containing all the required data
	 * @param aoSession - sql session
	 * @return - lbInsertStatus
	 * @throws ApplicationException
	 */
	public Boolean updateStatusServiceSubmission(Map<String, Object> aoQueryMap, SqlSession aoSession)
			throws ApplicationException
	{

		Boolean lbInsertStatus = false;
		TaxonomyMapper loMapper = aoSession.getMapper(TaxonomyMapper.class);
		try
		{

			loMapper.updateSSSStatusOnServiceSubmission(aoQueryMap);
			loMapper.updateDocumentOnServiceSubmission(aoQueryMap);

			Integer liSuperStatus = -1;
			liSuperStatus = (Integer) loMapper.getServiceSuperStatus(aoQueryMap);
			if (!(liSuperStatus > 0))
			{
				loMapper.updateServiceAppOnServicePostSubmission(aoQueryMap);
			}
			lbInsertStatus = true;
		}
		catch (Exception loEx)
		{
			LOG_OBJECT
					.Error("Exception occured while updating status in Tables on Launch of Application submission workflow in Taxonomy DAO",
							loEx);
			throw new ApplicationException(
					"Exception occured while updating status in Tables on Launch of Application "
							+ "submission workflow in Taxonomy DAO", loEx);
		}
		return lbInsertStatus;
	}

	/**
	 * This method is used to fetch taxonomy Item left menu detail page
	 * 
	 * @param aoElementId element id of the taxonomy selected
	 * @param aoMyBatisSession to connect to database
	 * @return loTaxonomyTreeBean containing taxonomy tree bean attribute for
	 *         detail page
	 * @throws ApplicationException
	 */

	public TaxonomyTree getTaxonomyMasterDataLeftMenu(String aoElementId, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		TaxonomyTree loTaxonomyTreeBean = null;
		TaxonomyMapper loMapper = aoMyBatisSession.getMapper(TaxonomyMapper.class);
		loTaxonomyTreeBean = loMapper.getTaxonomyItemDetailsLeftMenu(aoElementId);
		return loTaxonomyTreeBean;
	}

	/**
	 * This method is used to fetch taxonomy Item for the left menu detail
	 * page(Synonym taxonomy)
	 * 
	 * @param aoElementId element id of the taxonomy selected
	 * @param aoMyBatisSession to connect to database
	 * @return loTaxonomySynonymList List containing taxonomy synonym
	 *         bean(element id, branch id, flags ...etc) (element id, branch id,
	 *         flags ...etc)
	 * @throws ApplicationException
	 */

	public List<TaxonomySynonymBean> getTaxonomySynonymDataLeftMenu(String aoElementId, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		List<TaxonomySynonymBean> loTaxonomySynonymList = null;
		TaxonomyMapper loMapper = aoMyBatisSession.getMapper(TaxonomyMapper.class);
		loTaxonomySynonymList = loMapper.getTaxonomySynonymDataLeftMenu(aoElementId);
		return loTaxonomySynonymList;
	}

	/**
	 * This method is used fetch taxonomy Item for the left menu (Linkage
	 * taxonomy)
	 * 
	 * @param aoElementId element id of the taxonomy selected
	 * @param aoMyBatisSession to connect to database
	 * @return loTaxonomyLinkageList List containing taxonomy linkage
	 *         bean(element id, branch id, flags ...etc)
	 * @throws ApplicationException
	 */
	public List<TaxonomyLinkageBean> getTaxonomyLinkageDataLeftMenu(String aoElementId, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		List<TaxonomyLinkageBean> loTaxonomyLinkageList = null;
		TaxonomyMapper loMapper = aoMyBatisSession.getMapper(TaxonomyMapper.class);
		loTaxonomyLinkageList = loMapper.getTaxonomyLinkageDataLeftMenu(aoElementId);
		return loTaxonomyLinkageList;
	}

}
