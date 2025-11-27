package com.nyc.hhs.daomanager.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.ibatis.session.SqlSession;
import org.jdom.Document;
import org.jdom.Element;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.cache.ICacheManager;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.GeneralAuditBean;
import com.nyc.hhs.model.KeyValue;
import com.nyc.hhs.model.ServiceSummary;
import com.nyc.hhs.model.ServiceSummaryStatus;
import com.nyc.hhs.model.TaxonomyLinkageBean;
import com.nyc.hhs.model.TaxonomyServiceBean;
import com.nyc.hhs.model.TaxonomySynonymBean;
import com.nyc.hhs.model.TaxonomyTree;
import com.nyc.hhs.service.db.dao.TaxonomyDAO;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.util.BusinessApplicationUtil;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.PropertyUtil;
import com.nyc.hhs.util.TaxonomyDOMUtil;
import com.nyc.hhs.util.XMLUtil;

/**
 * TaxonomyService: This service class used to perform operations for add/remove
 * and update Taxonomy items and also used for taxonomy maintenance.
 */
public class TaxonomyService extends ServiceState
{
	private static final LogInfo LOG_OBJECT = new LogInfo(TaxonomyService.class);

	/**
	 * This method retrieves the population taxonomy from Taxonomy master table
	 * to show for Basic population page
	 * 
	 * @param asTaxonomyType : determines taxonomy type (population, languages,
	 *            etc..)
	 * @param abFromCache : Boolean value to determine where we get data from
	 *            cache or not
	 * @param aoMyBatisSession : Sql session object
	 * @return List of TaxonomyTree object
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public ArrayList<TaxonomyTree> getTaxonomyTreeByType(String asTaxonomyType, String abFromCache,
			SqlSession aoMyBatisSession) throws ApplicationException
	{
		Map<String, Object> loParamMap = new LinkedHashMap<String, Object>();
		loParamMap.put("taxonomyType", asTaxonomyType);
		loParamMap.put("abFromCache", abFromCache);

		if (!abFromCache.equals(ApplicationConstants.TRUE))
		{
			// Get data from DB and set in cache
			try
			{
				// Set values retrieved from db in cache
				ICacheManager loCacheManager = BaseCacheManagerWeb.getInstance();
				PropertyUtil loPropertyUtil = new PropertyUtil();
				loPropertyUtil.setTaxonomyInCache(loCacheManager, ApplicationConstants.TAXONOMY_ELEMENT);
				abFromCache = ApplicationConstants.TRUE;
			}
			catch (ApplicationException loAppEx)
			{
				loAppEx.setContextData((HashMap) loParamMap);
				LOG_OBJECT.Error("Problem in reading taxonomy tree from the cache in 'getTaxonomyTreeByType' method",
						loAppEx);
				setMoState("Transaction failed ::: in getTaxonomyTreeByType method : when trying to execute data from taxonomy tree\n");
				throw new ApplicationException("Problem in getting taxonomy data from the cache ", loAppEx);
			}
		}
		ArrayList<TaxonomyTree> loTaxonomyTreeList = null;
		if (abFromCache.equals(ApplicationConstants.TRUE))
		{
			loTaxonomyTreeList = new ArrayList<TaxonomyTree>();
			// Get data from cache
			Document loDoc = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
					ApplicationConstants.TAXONOMY_ELEMENT);
			String lsXPath = "//element[starts-with(@type,\""
					+ ApplicationConstants.TAXONOMY_TYPE_MAP.get(asTaxonomyType) + "\")]";
			List<Element> lsPopulationNode = XMLUtil.getElementList(lsXPath, loDoc);
			Iterator<Element> loIterate = lsPopulationNode.iterator();
			while (loIterate.hasNext())
			{
				TaxonomyTree loTaxonomyTree = new TaxonomyTree();
				Element loPopulationElement = loIterate.next();
				loTaxonomyTree.setMsElementid(loPopulationElement.getAttributeValue("id"));
				loTaxonomyTree.setMsElementName(loPopulationElement.getAttributeValue("name"));
				loTaxonomyTree.setMsElementType(loPopulationElement.getAttributeValue("type"));
				loTaxonomyTree.setMsBranchid(loPopulationElement.getAttributeValue("branchid"));
				loTaxonomyTree.setMsParentid(loPopulationElement.getAttributeValue("parentid"));
				loTaxonomyTree.setMsEvidenceReqd(loPopulationElement.getAttributeValue("evidencerequiredflag"));
				loTaxonomyTree.setMsActiveFlag(loPopulationElement.getAttributeValue("activeflag"));
				loTaxonomyTree.setMsSelectionFlag(loPopulationElement.getAttributeValue("selectionflag"));
				loTaxonomyTree.setMsElementDescription(loPopulationElement.getChildText("description"));
				if (loPopulationElement.getAttributeValue("activeflag") != null
						&& loPopulationElement.getAttributeValue("activeflag").equalsIgnoreCase("1"))
				{
					loTaxonomyTreeList.add(loTaxonomyTree);
				}
			}
		}
		setMoState("Transaction successfully ::: executed  in getTaxonomyTreeByType method \n");
		return loTaxonomyTreeList;
	}

	/**
	 * This method gets complete Taxonomy Data from database
	 * 
	 * @param aoMyBatisSession : Sql session object
	 * @return List of TaxonomyTree Object
	 * @throws ApplicationException
	 */
	public List<TaxonomyTree> getTaxonomyMaster(SqlSession aoMyBatisSession) throws ApplicationException
	{
		TaxonomyDAO loTaxonomyDAO = new TaxonomyDAO();
		List<TaxonomyTree> loTaxonomyList = null;
		try
		{
			loTaxonomyList = loTaxonomyDAO.selectFromTaxonomyMasterTable(aoMyBatisSession);
		}
		catch (ApplicationException loAppEx)
		{
			setMoState("Transaction failed ::: when trying yo get the data from taxonomy master table in getTaxonomyMaster method\n");
			LOG_OBJECT.Error("Error occured while getting form data from Taxonomy Maser", loAppEx);
			throw new ApplicationException("Error occured while getting form data from Taxonomy Maser", loAppEx);
		}
		setMoState("Transaction successfully :::  fetch the data from taxonomy master in getTaxonomyMaster method \n");
		return loTaxonomyList;
	}

	/**
	 * This method retrieves the population taxonomy from Taxonomy master table
	 * to show for Basic population page
	 * 
	 * @param asTaxonomyType : determines taxonomy type (population, languages,
	 *            etc..)
	 * @param abFromCache : Boolean value to determine where we get data from
	 *            cache or not
	 * @param aoMyBatisSession : Sql session object
	 * @return List of TaxonomyTree object
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public TreeMap<String, List<TaxonomyTree>> getTaxonomyTreeForGography(String asTaxonomyType, String abFromCache,
			SqlSession aoMyBatisSession) throws ApplicationException
	{
		Map<String, Object> loParamMap = new LinkedHashMap<String, Object>();
		loParamMap.put("taxonomyType", asTaxonomyType);
		loParamMap.put("abFromCache", abFromCache);

		String lsTopId = null;
		String lsTempName = null;
		List<Element> loLChildlist = null;
		ArrayList<TaxonomyTree> loTaxonomyTreeList = null;
		Map<String, List<TaxonomyTree>> loHMTaxonomyMap = new HashMap<String, List<TaxonomyTree>>();

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
			catch (ApplicationException loAppEx)
			{
				loAppEx.setContextData((HashMap) loParamMap);
				LOG_OBJECT
						.Error("Problem in fetching data from taxonomy tree for geography in method : getTaxonomyTreeForGography",
								loAppEx);
				setMoState("Transaction failed ::: when trying to fetch data from taxonomy tree for geography in "
						+ "getTaxonomyTreeForGography method\n");
				throw loAppEx;
			}
		}
		if (abFromCache.equals(ApplicationConstants.TRUE))
		{
			getTaxonomyTreeForGographyFromCache(asTaxonomyType, lsTopId, lsTempName, loLChildlist, loTaxonomyTreeList,
					loHMTaxonomyMap);
		}
		setMoState("Transaction successfully :::  fetch the data from taxonomy master for geography in getTaxonomyTreeForGography method\n");
		return new TreeMap<String, List<TaxonomyTree>>(loHMTaxonomyMap);
	}

	/***
	 * This method retrieves the population taxonomy from Taxonomy master table
	 * to show for Basic population page
	 * 
	 * @param asTaxonomyType : determines taxonomy type (geography)
	 * @param asTopId : determines root id for the selected taxonomy item
	 * @param asTempName : used to store taxonomy item name temporarily
	 * @param aoLChildlist : list of child taxonomy items within selected
	 *            taxonomy item
	 * @param aoTaxonomyTreeList : taxonomy tree list for selected taxonomy item
	 * @param aoTaxonomyMap : taxonomy map for selected taxonomy item
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private void getTaxonomyTreeForGographyFromCache(String asTaxonomyType, String asTopId, String asTempName,
			List<Element> aoLChildlist, ArrayList<TaxonomyTree> aoTaxonomyTreeList,
			Map<String, List<TaxonomyTree>> aoTaxonomyMap) throws ApplicationException
	{
		Element loTopElement;
		// Get data from cache
		Document loDoc = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.TAXONOMY_ELEMENT);
		String lsXPathTop = "//element[starts-with(@name,\""
				+ ApplicationConstants.TAXONOMY_TYPE_MAP.get(asTaxonomyType) + "\")]";
		List<Element> loPopulationTopNode = XMLUtil.getElementList(lsXPathTop, loDoc);
		Element loTopNode = loPopulationTopNode.get(0);
		List<Element> loTopNodeChildlist = loTopNode.getChildren();
		int liSize = loTopNodeChildlist.size();
		for (int liCount = 1; liCount < liSize; liCount++)
		{
			if (loTopNodeChildlist.get(liCount) != null)
			{
				// We are getting BRONX, BROOKLYN etc
				loTopElement = loTopNodeChildlist.get(liCount);
				asTempName = loTopElement.getAttributeValue("name");
				asTopId = loTopElement.getAttributeValue("id");
			
				aoTaxonomyTreeList = new ArrayList();
				aoLChildlist = loTopElement.getChildren();
			}
			for (int liNo = 1; liNo < aoLChildlist.size(); liNo++)
			{
				if (aoLChildlist.get(liNo) != null)
				{
					if (aoLChildlist.get(liNo).getAttributeValue("parentid") != null
							&& !aoLChildlist.get(liNo).getAttributeValue("parentid").equals(""))
					{
						if (aoLChildlist.get(liNo).getAttributeValue("parentid").equals(asTopId))
						{
							TaxonomyTree loTaxonomyTree = new TaxonomyTree();
							Element loPopulationElement = aoLChildlist.get(liNo);
							loTaxonomyTree.setMsElementid(loPopulationElement.getAttributeValue("id"));
							loTaxonomyTree.setMsElementName(loPopulationElement.getAttributeValue("name"));
							loTaxonomyTree.setMsElementType(loPopulationElement.getAttributeValue("type"));
							loTaxonomyTree.setMsBranchid(loPopulationElement.getAttributeValue("branchid"));
							loTaxonomyTree.setMsParentid(loPopulationElement.getAttributeValue("parentid"));
							loTaxonomyTree.setMsEvidenceReqd(loPopulationElement
									.getAttributeValue("evidencerequiredflag"));
							loTaxonomyTree.setMsActiveFlag(loPopulationElement.getAttributeValue("activeflag"));
							loTaxonomyTree.setMsSelectionFlag(loPopulationElement.getAttributeValue("selectionflag"));
							loTaxonomyTree.setMsElementDescription(loPopulationElement.getChild("description")
									.getText());
							if (loPopulationElement.getAttributeValue("activeflag") != null
									&& loPopulationElement.getAttributeValue("activeflag").equalsIgnoreCase("1"))
							{
								aoTaxonomyTreeList.add(loTaxonomyTree);
							}
						}
					}
				}
			}
			aoTaxonomyMap.put(asTempName, aoTaxonomyTreeList);
		}
		for (Map.Entry<String, List<TaxonomyTree>> loEntry : aoTaxonomyMap.entrySet())
		{
			try
			{
				Collections.sort(loEntry.getValue(), new Comparator<TaxonomyTree>()
				{
					@Override
					public int compare(TaxonomyTree aoObject1, TaxonomyTree aoObject2)
					{
						return (Integer.valueOf(aoObject1.getMsElementName())).compareTo(Integer.valueOf(aoObject2
								.getMsElementName()));
					}
				});
			}
			catch (NumberFormatException loNumEx)
			{
				Collections.sort(loEntry.getValue(), new Comparator<TaxonomyTree>()
				{
					@Override
					public int compare(TaxonomyTree aoObject1, TaxonomyTree aoObject2)
					{
						return (aoObject1.getMsElementName()).compareTo(aoObject2.getMsElementName());
					}
				});
			}
		}
	}

	/**
	 * This method is used to get the taxonomy services and to display on the
	 * page
	 * 
	 * @param asElementType : top element name
	 * @param asFromCache : cache or database
	 * @param aoMyBatisSession : Sql session object
	 * @return map : lsCompleteTree taxonomy tree
	 * @throws ApplicationException loAppEx
	 */
	@SuppressWarnings("rawtypes")
	public String getTaxonomyTree(final String asElementType, String asFromCache, SqlSession aoMyBatisSession)
			throws Exception
	{

		Map<String, Object> loParamMap = new LinkedHashMap<String, Object>();
		loParamMap.put("taxonomyType", asElementType);
		loParamMap.put("abFromCache", asFromCache);
		String lsCompleteTree = null;
		if (!asFromCache.equals(ApplicationConstants.TRUE))
		{
			try
			{
				// Set values retrieved from db in cache
				ICacheManager loCacheManager = BaseCacheManagerWeb.getInstance();
				PropertyUtil loPropertyUtil = new PropertyUtil();
				loPropertyUtil.setTaxonomyInCache(loCacheManager, ApplicationConstants.TAXONOMY_ELEMENT);
				asFromCache = ApplicationConstants.TRUE;
			}
			catch (ApplicationException loAppEx)
			{
				loAppEx.setContextData((HashMap) loParamMap);
				LOG_OBJECT.Error("Problem in fetching data from taxonomy tree in method : getTaxonomyTree", loAppEx);
				setMoState("Transaction failed ::: when trying to fetch data from taxonomy tree in getTaxonomyTree method\n");
				throw loAppEx;
			}
		}
		if (asFromCache.equals(ApplicationConstants.TRUE))
		{
			Document loTaxonomyDom = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
					ApplicationConstants.TAXONOMY_ELEMENT);
			String lsXPathTop = "//element[((@name=\"" + ApplicationConstants.TAXONOMY_TYPE_MAP.get(asElementType)
					+ "\" or @name=\"Function\")" + "and lower-case(@parentid)=\"root\")]";
			List<Element> loNodeList = XMLUtil.getElementList(lsXPathTop, loTaxonomyDom);
			// loTaxElement.getC("element")
			lsCompleteTree = BusinessApplicationUtil.getTree(loTaxonomyDom, loNodeList, "button");
		}
		setMoState("Transaction successfully :::  fetch the data from taxonomy master in getTaxonomyTree method\n");
		return lsCompleteTree;
	}

	/**
	 * This method is used to insert update and delete the selected services
	 * into the service_application table
	 * 
	 * @param aoInsertUpdateDeleteMap map of insert update and delete list of
	 *            selected services
	 * @param aoMyBatisSession : Sql session object
	 * @return boolean true false
	 * @throws ApplicationException loAppEx
	 */
	@SuppressWarnings("rawtypes")
	public Boolean insertUpdateDeleteSelectedServices(
			final Map<String, List<TaxonomyServiceBean>> aoInsertUpdateDeleteMap, final String aoApplicationId,
			final SqlSession aoMyBatisSession) throws ApplicationException
	{
		// Boolean lbSuccessStatus= false;
		TaxonomyDAO loTaxonomyDAO = new TaxonomyDAO();
		Boolean lbSuccessStatus = false;
		List<TaxonomyServiceBean> loSelectedServicesList = null;
		try
		{
			// iterate the map and find the key to insert update and delete key
			if (aoInsertUpdateDeleteMap != null && !aoInsertUpdateDeleteMap.isEmpty())
			{
				for (Map.Entry<String, List<TaxonomyServiceBean>> loEntry : aoInsertUpdateDeleteMap.entrySet())
				{
					final String loMapKey = (String) loEntry.getKey();
					// for insert
					if (loMapKey.equalsIgnoreCase("insert"))
					{
						loSelectedServicesList = loEntry.getValue();
						if (loSelectedServicesList != null && !loSelectedServicesList.isEmpty())
						{
							lbSuccessStatus = loTaxonomyDAO.saveSelectedService(loSelectedServicesList,
									aoApplicationId, aoMyBatisSession);
						}
					}// for delete
					else if (loMapKey.equalsIgnoreCase("delete"))
					{
						loSelectedServicesList = loEntry.getValue();
						if (loSelectedServicesList != null && !loSelectedServicesList.isEmpty())
						{
							lbSuccessStatus = loTaxonomyDAO.deleteSelectedService(loSelectedServicesList,
									aoMyBatisSession);
						}
					}// for update
					if (loMapKey.equalsIgnoreCase("update"))
					{
						loSelectedServicesList = loEntry.getValue();
						if (loSelectedServicesList != null && !loSelectedServicesList.isEmpty())
						{
							lbSuccessStatus = loTaxonomyDAO.updateSelectedService(loSelectedServicesList,
									aoMyBatisSession);
						}
					}
				}
				lbSuccessStatus = true;
			}
		}
		catch (ApplicationException loAppEx)
		{
			lbSuccessStatus = false;
			loAppEx.setContextData(aoInsertUpdateDeleteMap);
			setMoState("Transaction failed : Problem to insert data in service application table in insertUpdateDeleteSelectedServices method\n");
			LOG_OBJECT
					.Error("Problem in inserting data in service application table in method : insertUpdateDeleteSelectedServices",
							loAppEx);
			throw new ApplicationException(
					"Exception occured when trying to insert data in service application table in insertUpdateDeleteSelectedServices method",
					loAppEx);
		}
		setMoState("Transaction successfully : successfully inserted data into the service application table in "
				+ "insertUpdateDeleteSelectedServices method\n");
		return lbSuccessStatus;
	}

	/**
	 * This method is used to get the selected service from the data base to
	 * display on landing page
	 * 
	 * @param asUserId : user id
	 * @param asOrgId : organization id
	 * @param aoMyBatisSession : Sql session object
	 * @return List to display the services
	 * @throws ApplicationException loAppEx
	 */
	@SuppressWarnings("rawtypes")
	public List<TaxonomyServiceBean> getSelectedService(final String aoBusinessApplicationId, final String asOrgId,
			final SqlSession aoMyBatisSession) throws ApplicationException
	{
		TaxonomyDAO loTaxonomyDAO = new TaxonomyDAO();
		Map<String, Object> loParamMap = new LinkedHashMap<String, Object>();
		loParamMap.put("businessApplicationId", aoBusinessApplicationId);
		loParamMap.put("orgId", asOrgId);

		List<TaxonomyServiceBean> loSaveServicesList = null;
		try
		{
			// dao method to call selected services method
			loSaveServicesList = loTaxonomyDAO.getSelectedService(aoBusinessApplicationId, asOrgId, aoMyBatisSession);
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.setContextData((HashMap) loParamMap);
			setMoState("Transaction failed : Problem in getting data from service application table in getSelectedService method\n");
			setMoState("expecting business application id : ".concat(aoBusinessApplicationId)
					.concat("::organization id:").concat(asOrgId).concat("\n"));
			LOG_OBJECT.Error("Error occured while getting form data from getSelectedService method", loAppEx);
			throw new ApplicationException(
					"Exception occured when trying to get data from service application table in getSelectedService method",
					loAppEx);
		}
		setMoState("Transaction successfully : successfully executed in getSelectedService method expecting business application id : "
				.concat(aoBusinessApplicationId).concat("::organization id:").concat(asOrgId).concat("\n"));
		return loSaveServicesList;
	}

	/**
	 * This method is used to delete the selected service from the data base
	 * 
	 * @param asUserId : user id
	 * @param asOrgId : organization id
	 * @param aoMyBatisSession : Sql session object
	 * @return boolean true false
	 * @throws ApplicationException loAppEx
	 */
	public Boolean deleteSelectedService(final List<TaxonomyServiceBean> aoServiceListDB,
			final SqlSession aoMyBatisSession) throws ApplicationException
	{
		TaxonomyDAO loTaxonomyDAO = new TaxonomyDAO();
		Boolean lbSuccessStatus = false;

		try
		{
			// dao method to call selected services method
			lbSuccessStatus = loTaxonomyDAO.deleteSelectedService(aoServiceListDB, aoMyBatisSession);
		}
		catch (ApplicationException loAppEx)
		{
			// aoEx.setContextData((HashMap)loParamMap);
			setMoState("Transaction failed : Problem in deleting data from the service application table in deleteSelectedService method\n");
			LOG_OBJECT.Error("Error occured while deleting data from deleteSelectedService method", loAppEx);
			throw new ApplicationException(
					"Exception occured when trying to delete data from service application table in deleteSelectedService method",
					loAppEx);
		}
		setMoState("Transaction successfully : successfully deleted the data from service application table");
		return lbSuccessStatus;
	}

	/**
	 * This function reads geography from taxonomy database.
	 * 
	 * @param asOrgId : provider user's organization id
	 * @param aoMyBatisSession : Sql session object
	 * @return loTaxonomyIdList List data from geography table
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public List<String> ReadGeographyList(String asOrgId, final SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		Map<String, Object> loParamMap = new LinkedHashMap<String, Object>();
		loParamMap.put("organizationId", asOrgId);

		TaxonomyDAO loTaxonomyDAO = new TaxonomyDAO();
		List<String> loTaxonomyIdList = null;
		try
		{
			// dao method to call selected services method
			loTaxonomyIdList = loTaxonomyDAO.getSelectedGeography(asOrgId, aoMyBatisSession);
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.setContextData((HashMap) loParamMap);
			setMoState("Transaction failed : Problem in reading the records from geagraphy table in ReadGeographyList method\n");
			LOG_OBJECT.Error("Error occured while reading data from ReadGeographyList method", loAppEx);
			throw new ApplicationException(
					"Exception occured when trying to delete data from service application table in ReadGeographyList method",
					loAppEx);
		}
		setMoState("Transaction successfully : successfully read data from geagraphy table in ReadGeographyList method");
		return loTaxonomyIdList;
	}

	/**
	 * This method check all application service information and apply business
	 * rules to display service status
	 * 
	 * @param aoServiceSummary : all services status
	 * @return loServiceSummary : service summary status
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<ServiceSummary> getApplicationTypeService(Map<String, Object> aoServiceSummary)
			throws ApplicationException
	{
		List<ServiceSummary> loServiceSummary = null;
		if (aoServiceSummary != null)
		{
			loServiceSummary = (List<ServiceSummary>) aoServiceSummary.get("service_information");
			Map<String, String> loDocStatus = (Map<String, String>) aoServiceSummary.get("service_doc_status");
			Map<String, String> loAppSettingStatus = (Map<String, String>) aoServiceSummary.get("service_app_status");
			List<ServiceSummary> loGetAllDataForSpesSetting = (List<ServiceSummary>) aoServiceSummary
					.get("service_setting_info");

			if (loServiceSummary != null && !loServiceSummary.isEmpty())
			{
				Iterator<ServiceSummary> loServiceItr = loServiceSummary.iterator();
				Document loTaxonomyDom = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
						ApplicationConstants.TAXONOMY_ELEMENT);

				while (loServiceItr.hasNext())
				{

					iterateToGetApplicationTypeService(loDocStatus, loAppSettingStatus, loGetAllDataForSpesSetting,
							loServiceItr, loTaxonomyDom);
				}
			}

		}
		setMoState("Transaction successfully ::: executed successfully in getApplicationTypeService method");
		return loServiceSummary;
	}

	/**
	 * This method check all application service information and apply business
	 * rules to display service status
	 * 
	 * @param aoDocStatus : Document status for application
	 * @param aoAppSettingStatus : Application status
	 * @param aoGetAllDataForSpesSetting : bean with complete setting data
	 * @param aoServiceItr : Service Iterator
	 * @param aoTaxonomyDom : Dom Object of Taxonomy
	 * @throws ApplicationException
	 */
	private void iterateToGetApplicationTypeService(Map<String, String> aoDocStatus,
			Map<String, String> aoAppSettingStatus, List<ServiceSummary> aoGetAllDataForSpesSetting,
			Iterator<ServiceSummary> aoServiceItr, Document aoTaxonomyDom) throws ApplicationException
	{
		boolean lbQuestionCompleted = false;
		boolean lbDocumentCompleted = false;
		boolean lbSpecializationCompleted = false;
		boolean lbSettingCompleted = false;
		ServiceSummary loService = aoServiceItr.next();
		String lsServiceAppId = loService.getMsServiceAppId();
		String lsElementId = loService.getMsServiceName();
		loService.setMsServiceElementId(lsElementId);
		String lsServiceNameForDisplay = BusinessApplicationUtil.getTaxonomyName(lsElementId, aoTaxonomyDom);
		if (null != lsElementId && null == lsServiceNameForDisplay || lsServiceNameForDisplay.equalsIgnoreCase(""))
		{
			Map<String, String> loActionMap = new HashMap<String, String>();
			loActionMap.put("lsElementId", lsElementId);
			Channel loChannel = new Channel();
			loChannel.setData("loActionMap", loActionMap);
			TransactionManager.executeTransaction(loChannel, "getDeletedServiceName");
			lsServiceNameForDisplay = (String) loChannel.getData("serviceName");
		}
		loService.setMsServiceName(lsServiceNameForDisplay);
		ServiceSummaryStatus loStatus = new ServiceSummaryStatus();
		if (aoAppSettingStatus != null && aoAppSettingStatus.containsKey(lsServiceAppId))
		{
			loStatus.setQuestionStatus(ApplicationConstants.COMPLETED_STATE);
			loStatus.setQuestionClass(ApplicationConstants.COMPLETED_STATE.toLowerCase().replaceAll(" ", ""));
			lbQuestionCompleted = true;
		}
		else
		{
			loStatus.setDocumentStatus(ApplicationConstants.NOT_STARTED_STATE);
			loStatus.setQuestionClass("sub-notstarted");
			loStatus.setDocumentClass("sub-notstarted");
			loStatus.setQuestionStatus(ApplicationConstants.NOT_STARTED_STATE);
		}
		if (aoDocStatus != null && aoDocStatus.containsKey(lsServiceAppId))
		{
			loStatus.setDocumentStatus(ApplicationConstants.STATUS_DRAFT);
			loStatus.setDocumentClass("sub-draft");
		}
		if (null != aoGetAllDataForSpesSetting && !aoGetAllDataForSpesSetting.isEmpty())
		{
			StringBuffer loSbSetting = new StringBuffer();
			StringBuffer loSbSpecialization = new StringBuffer();
			getNames(lsServiceAppId, loSbSetting, loSbSpecialization, aoGetAllDataForSpesSetting, aoTaxonomyDom);
			if (loSbSetting.length() > 0)
			{
				lbSpecializationCompleted = true;
				loStatus.setSelectedSettigNames(loSbSetting.toString());
				loStatus.setSelectedSettigClass(ApplicationConstants.COMPLETED_STATE.toLowerCase().replaceAll(" ", ""));
			}
			else
			{
				loStatus.setSelectedSettigClass("sub-notstarted");
				loStatus.setSelectedSettigNames(ApplicationConstants.NOT_STARTED_STATE);
			}
			if (loSbSpecialization.length() > 0)
			{
				lbSettingCompleted = true;
				loStatus.setSelectedSpecizationNames(loSbSpecialization.toString());
				loStatus.setSelectedSpecizationClass(ApplicationConstants.COMPLETED_STATE.toLowerCase().replaceAll(" ",
						""));
			}
			else
			{
				loStatus.setSelectedSpecizationClass("sub-notstarted");
				loStatus.setSelectedSpecizationNames(ApplicationConstants.NOT_STARTED_STATE);
			}
		}
		else
		{
			loStatus.setSelectedSettigClass("sub-notstarted");
			loStatus.setSelectedSettigNames(ApplicationConstants.NOT_STARTED_STATE);
			loStatus.setSelectedSpecizationClass("sub-notstarted");
			loStatus.setSelectedSpecizationNames(ApplicationConstants.NOT_STARTED_STATE);
		}
		loService.setServiceSubSectionStatus(loStatus);
		if (lbQuestionCompleted && lbDocumentCompleted && lbSettingCompleted && lbSpecializationCompleted)
		{
			loService.setMsStatusId(ApplicationConstants.COMPLETED_STATE.toLowerCase());
			loService.setMsServiceStatus(ApplicationConstants.COMPLETED_STATE);
		}
		else if (!lbQuestionCompleted && !lbDocumentCompleted && !lbSettingCompleted && !lbSpecializationCompleted)
		{
			loService.setMsStatusId("sub-notstarted");
			loService.setMsServiceStatus(ApplicationConstants.NOT_STARTED_STATE);
		}
		else
		{
			loService.setMsStatusId("sub-started");
			loService.setMsServiceStatus(ApplicationConstants.STATUS_DRAFT);
		}
	}

	/**
	 * This method check all application service information and apply business
	 * rules to display service status
	 * 
	 * @param asServiceId : service application id
	 * @param aoSbSetting : it clubs all the setting related to one servise and
	 *            return to calling function
	 * @param aoSbSpecialization : it clubs all the specialization related to
	 *            one specialization and return to calling function
	 * @param aoGetAllDataForSpesSetting : bean with complete setting data
	 * @param aoTaxonomyDom : Dom Object of Taxonomy
	 * @throws ApplicationException
	 */
	public void getNames(String asServiceId, StringBuffer aoSbSetting, StringBuffer aoSbSpecialization,
			List<ServiceSummary> aoGetAllDataForSpesSetting, Document aoTaxonomyDom) throws ApplicationException
	{
		Iterator<ServiceSummary> loItr = aoGetAllDataForSpesSetting.iterator();
		while (loItr.hasNext())
		{
			ServiceSummary loSummary = loItr.next();
			if (loSummary.getMsServiceAppId().equals(asServiceId))
			{
				String lsEleId = loSummary.getMsServiceName();
				if (loSummary.getMsServiceType().equalsIgnoreCase(ApplicationConstants.SERVICE_SETTING))
				{
					String lsServiceNameForDisplay = BusinessApplicationUtil.getTaxonomyName(lsEleId, aoTaxonomyDom);
					aoSbSetting.append(lsServiceNameForDisplay).append(", ");
				}
				else if (loSummary.getMsServiceType().equals(ApplicationConstants.SPECIALIZATION))
				{
					String lsServiceNameForDisplay = BusinessApplicationUtil.getTaxonomyName(lsEleId, aoTaxonomyDom);
					aoSbSpecialization.append(lsServiceNameForDisplay).append(", ");
				}
			}
		}
	}

	/**
	 * This method provides Service name for Service Setting/Specialization
	 * 
	 * @param aoSbSetting : Service setting string buffer object
	 * @param aoSbSpecialization : Specialization string buffer object
	 * @param aoGetAllDataForSpesSetting : data object
	 * @param aoTaxonomyDom : Taxonomy dom object
	 * @throws ApplicationException
	 */
	public void getServiceNames(StringBuffer aoSbSetting, StringBuffer aoSbSpecialization,
			List<KeyValue> aoGetAllDataForSpesSetting, Document aoTaxonomyDom, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		Iterator<KeyValue> loItr = aoGetAllDataForSpesSetting.iterator();
		while (loItr.hasNext())
		{
			KeyValue loSummary = loItr.next();
			String lsEleId = loSummary.getKey();
			if (loSummary.getValue().equalsIgnoreCase(ApplicationConstants.SERVICE_SETTING))
			{
				String lsServiceNameForDisplay = BusinessApplicationUtil.getTaxonomyName(lsEleId, aoTaxonomyDom);
				// code added for deleted service name
				if (null != lsEleId
						&& (lsServiceNameForDisplay == null || lsServiceNameForDisplay.equalsIgnoreCase("")))
				{
					Map<String, String> loApplicationMap = new LinkedHashMap<String, String>();
					loApplicationMap.put("lsElementId", lsEleId);
					lsServiceNameForDisplay = getDeletedServiceName(aoMyBatisSession, loApplicationMap);
				}
				// code added for deleted service name
				aoSbSetting.append(lsServiceNameForDisplay).append(", ");
			}
			else if (loSummary.getValue().equals(ApplicationConstants.SPECIALIZATION))
			{
				String lsServiceNameForDisplay = BusinessApplicationUtil.getTaxonomyName(lsEleId, aoTaxonomyDom);
				// code added for deleted service name
				if (null != lsEleId
						&& (lsServiceNameForDisplay == null || lsServiceNameForDisplay.equalsIgnoreCase("")))
				{
					Map<String, String> loApplicationMap = new LinkedHashMap<String, String>();
					loApplicationMap.put("lsElementId", lsEleId);
					lsServiceNameForDisplay = getDeletedServiceName(aoMyBatisSession, loApplicationMap);
				}
				// code added for deleted service name
				aoSbSpecialization.append(lsServiceNameForDisplay).append(", ");
			}
		}
	}

	/**
	 * This function return service information
	 * 
	 * @param asOrgId Organization Id
	 * @param asAppId Application Id
	 * @param asBussAppId Business Application Id
	 * @param aoMyBatisSession to connect to database
	 * @return loServiceSummary Map to get the service
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public Map<String, Object> getServiceSummary(String asOrgId, String asAppId, String asBussAppId,
			String asAfterSubmissionAdd, SqlSession aoMyBatisSession) throws ApplicationException
	{
		Map<String, Object> loParams = new LinkedHashMap<String, Object>();
		loParams.put("orgId", asOrgId);
		loParams.put("appId", asAppId);
		loParams.put("bussnessAppId", asBussAppId);

		TaxonomyDAO loTaxonomyDAO = new TaxonomyDAO();
		Map<String, Object> loServiceSummary = null;
		try
		{
			if (asAfterSubmissionAdd == null)
			{
				loServiceSummary = loTaxonomyDAO.selectFromServiceApplicationTable(asOrgId, asAppId, asBussAppId,
						false, aoMyBatisSession);
			}
			else
			{
				loServiceSummary = loTaxonomyDAO.selectFromServiceApplicationTable(asOrgId, asAppId, asBussAppId, true,
						aoMyBatisSession);
			}
			Object loNoOfServices = DAOUtil.masterDAO(aoMyBatisSession, loParams,
					ApplicationConstants.MAPPER_CLASS_TAXONOMY, "totalNoOfServices", "java.util.Map");
			if (loServiceSummary != null)
			{
				loServiceSummary.put("NoOfRemainingServices", loNoOfServices);
			}
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.setContextData((HashMap) loParams);
			setMoState("Transaction failed : Problem in reading the records in getServiceSummary method\n");
			setMoState("expecting business application id : ".concat(asBussAppId).concat("::organization id:")
					.concat(asOrgId).concat(":::appId::").concat(asAppId).concat("\n"));
			LOG_OBJECT.Error("Error occured while reading data from getServiceSummary method", loAppEx);
			throw new ApplicationException("Exception occured when trying to get data in getServiceSummary method",
					loAppEx);
		}
		setMoState("Transaction successfully ::: executed successfully in getServiceSummary method\n");
		return loServiceSummary;
	}

	/**
	 * This function will return service summary information for single service
	 * view
	 * 
	 * @param asOrgId user organization id
	 * @param asAppId users application id
	 * @param asBussAppId organization business application id
	 * @param asServiceId Service Application Id
	 * @param aoMyBatisSession to connect to database
	 * @return loServiceDetails Map to get the for single service
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public ServiceSummaryStatus getSingleServiceSummary(String asOrgId, String asAppId, String asBussAppId,
			String asServiceId, SqlSession aoMyBatisSession) throws ApplicationException
	{
		TaxonomyDAO loTaxonomyDAO = new TaxonomyDAO();
		List<KeyValue> loServiceSummary = null;
		Map<String, Object> loHmParameter = new HashMap<String, Object>();
		ServiceSummaryStatus loServiceDetails = new ServiceSummaryStatus();
		try
		{
			loHmParameter.put("asOrgId", asOrgId);
			loHmParameter.put("asBussAppId", asBussAppId);
			loHmParameter.put("asServiceId", asServiceId);
			loHmParameter.put("asAppId", asAppId);
			loServiceSummary = loTaxonomyDAO.getServiceInformation(loHmParameter, aoMyBatisSession);
			if (loServiceSummary != null)
			{
				StringBuffer loSbSetting = new StringBuffer();
				StringBuffer loSbSpecialization = new StringBuffer();
				Document loTaxonomyDom = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
						ApplicationConstants.TAXONOMY_ELEMENT);
				getServiceNames(loSbSetting, loSbSpecialization, loServiceSummary, loTaxonomyDom, aoMyBatisSession);
				if (loSbSetting.length() > 0)
				{
					String lsSelectedSetting = loSbSetting.length() > 2 ? loSbSetting.substring(0,
							loSbSetting.length() - 2) : "";
					loServiceDetails.setSelectedSettigNames(lsSelectedSetting);
				}
				else
				{
					loServiceDetails.setSelectedSettigNames(ApplicationConstants.NOT_STARTED_STATE);
				}
				if (loSbSpecialization.length() > 0)
				{
					String lsSpecilization = loSbSpecialization.length() > 2 ? loSbSpecialization.substring(0,
							loSbSpecialization.length() - 2) : "";
					loServiceDetails.setSelectedSpecizationNames(lsSpecilization);
				}
				else
				{
					loServiceDetails.setSelectedSpecizationNames(ApplicationConstants.NOT_STARTED_STATE);
				}
			}
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.setContextData((HashMap) loHmParameter);
			setMoState("Transaction failed : Problem in reading the records in getSingleServiceSummary method\n");
			setMoState("expecting business application id : ".concat(asBussAppId).concat("::organization id:")
					.concat(asOrgId).concat(":::appId::").concat(asAppId).concat(":::asServiceId:::")
					.concat(asServiceId).concat("\n"));
			LOG_OBJECT.Error("Error occured while reading data from getSingleServiceSummary method", loAppEx);
			throw new ApplicationException(
					"Exception occured when trying to get data in getSingleServiceSummary method", loAppEx);
		}
		setMoState("Transaction successfully ::: executed successfully in getSingleServiceSummary method\n");
		return loServiceDetails;
	}

	/**
	 * This function remove selected service
	 * 
	 * @param asServiceId Service Application Id
	 * @param asOrgId Organization Id
	 * @param asAppId Application Id
	 * @param asBussAppId Business Application Id
	 * @param aoMyBatisSession to connect to database
	 * @return lbServiceStatus delete is successful if its true
	 * @throws ApplicationException
	 */
	public Boolean RemoveServiceSummary(String asServiceId, String asOrgId, String asAppId, String asBussAppId,
			String asModBy, SqlSession aoMyBatisSession) throws ApplicationException
	{
		TaxonomyDAO loTaxonomyDAO = new TaxonomyDAO();
		Boolean lbServiceStatus = false;
		if (asServiceId != null)
		{
			HashMap<String, Object> loHmParameter = new HashMap<String, Object>();
			loHmParameter.put("asOrgId", asOrgId);
			loHmParameter.put("asBussAppId", asBussAppId);
			loHmParameter.put("asServiceId", asServiceId);
			loHmParameter.put("asAppId", asAppId);
			loHmParameter.put("asModBy", asModBy);
			DAOUtil.masterDAO(aoMyBatisSession, loHmParameter, ApplicationConstants.MAPPER_CLASS_TAXONOMY,
					"deleteFromContractServiceMappingTable", "java.util.Map");
			DAOUtil.masterDAO(aoMyBatisSession, loHmParameter, ApplicationConstants.MAPPER_CLASS_TAXONOMY,
					"deleteFromStaffServiceMappingTable", "java.util.Map");
			DAOUtil.masterDAO(aoMyBatisSession, loHmParameter, ApplicationConstants.MAPPER_CLASS_TAXONOMY,
					"deleteFromServiceQuestionTable", "java.util.Map");
			DAOUtil.masterDAO(aoMyBatisSession, loHmParameter, ApplicationConstants.MAPPER_CLASS_TAXONOMY,
					"deleteFromServiceSettingTable", "java.util.Map");
			DAOUtil.masterDAO(aoMyBatisSession, loHmParameter, ApplicationConstants.MAPPER_CLASS_TAXONOMY,
					"deleteFromDocumentTable", "java.util.Map");
			DAOUtil.masterDAO(aoMyBatisSession, loHmParameter, ApplicationConstants.MAPPER_CLASS_TAXONOMY,
					"deleteFromSubSectionSummaryTable", "java.util.Map");
			DAOUtil.masterDAO(aoMyBatisSession, loHmParameter, ApplicationConstants.MAPPER_CLASS_TAXONOMY,
					"updateBusinessApplication", "java.util.Map");
			lbServiceStatus = loTaxonomyDAO.deleteFromServiceApplicationTable(aoMyBatisSession, loHmParameter);
			setMoState("Transaction successfully ::: executed successfully in RemoveServiceSummary method based on service id"
					.concat(asServiceId));
		}
		return lbServiceStatus;
	}

	/**
	 * This function provide service information for given service
	 * 
	 * @param asServiceId Service Application Id
	 * @param asUserId User Id
	 * @param asOrgId Organization Id
	 * @param asAppId Application Id
	 * @param asBussAppId Business Application Id
	 * @param aoMyBatisSession to connect to database
	 * @return loServiceSummaryList List of summary service
	 * @throws ApplicationException
	 */
	public List<String> ReadServiceSummary(String asServiceId, String asUserId, String asOrgId, String asAppId,
			String asBussAppId, SqlSession aoMyBatisSession) throws ApplicationException
	{
		TaxonomyDAO loTaxonomyDAO = new TaxonomyDAO();
		List<String> loServiceSummaryList = null;
		HashMap<String, Object> loHmParameter = new HashMap<String, Object>();
		loHmParameter.put("asOrgId", asOrgId);
		loHmParameter.put("asBussAppId", asBussAppId);
		loHmParameter.put("lsServiceId", asServiceId);
		loHmParameter.put("lsUserId", asUserId);
		loHmParameter.put("asAppId", asAppId);
		loServiceSummaryList = loTaxonomyDAO.retrieveFromServiceApplicationTable(aoMyBatisSession, loHmParameter);

		setMoState("Transaction successfully ::: executed successfully in ReadServiceSummary method \n");
		return loServiceSummaryList;
	}

	/**
	 * This function prepare Map to launch workflows
	 * 
	 * @param asOrgId Organization Id
	 * @param asAppId Application Id
	 * @param asBussAppId Business Application Id
	 * @param asUserId User Id
	 * @param asProviderName
	 * @param aoMyBatisSession to connect to database
	 * @return lolaunchWorkflowMap give the launch workflow map.
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public HashMap<String, Object> getLaunchWorkflowMap(String asOrgId, String asAppId, String asBussAppId,
			String asUserId, String asProviderName, SqlSession aoMyBatisSession) throws ApplicationException
	{
		TaxonomyDAO loTaxonomyDAO = new TaxonomyDAO();
		List<Map<String, Object>> loServiceAppIdsList = null;
		HashMap<String, Object> loLaunchWorkflowMap = new HashMap<String, Object>();
		try
		{
			loServiceAppIdsList = loTaxonomyDAO.selectServiceAppIDs(asOrgId, asAppId, asBussAppId, aoMyBatisSession);
			StringBuffer loStringBufferServiceIDs = new StringBuffer();
			StringBuffer loStringBufferServiceNames = new StringBuffer();
			if (null != loServiceAppIdsList)
			{
				List<Map<String, Object>> loList = loServiceAppIdsList;
				for (Map<String, Object> loMap : loList)
				{
					for (Map.Entry<String, Object> loEntry : loMap.entrySet())
					{
						if (loEntry.getKey().equals("SERVICE_APPLICATION_ID"))
						{
							loStringBufferServiceIDs.append(loEntry.getValue());
							loStringBufferServiceIDs.append("###");
						}
						else if (loEntry.getKey().equals("ELEMENT_NAME"))
						{
							loStringBufferServiceNames.append("Service Application - ").append(loEntry.getValue());
							loStringBufferServiceNames.append("###");
						}
					}
				}
			}
			if (null != loStringBufferServiceIDs && !loStringBufferServiceIDs.toString().equalsIgnoreCase("")
					&& null != loStringBufferServiceNames
					&& !loStringBufferServiceNames.toString().equalsIgnoreCase(""))
			{
				loLaunchWorkflowMap.put(P8Constants.PROPERTY_PE_PARENT_APPLICATION_ID, asAppId);
				loLaunchWorkflowMap.put(P8Constants.PROPERTY_PE_APPLICTION_ID, asBussAppId);
				loLaunchWorkflowMap.put(P8Constants.PROPERTY_PE_PROVIDER_ID, asOrgId);
				loLaunchWorkflowMap.put(P8Constants.PROPERTY_PE_PROVIDER_NAME, asProviderName);
				loLaunchWorkflowMap.put(P8Constants.PROPERTY_PE_LAUNCH_BY, asUserId);
				loLaunchWorkflowMap.put(P8Constants.PROPERTY_PE_SECTION_IDS,
						ApplicationConstants.WORKFLOW_LAUNCH_SECTION_IDS_SEQUENCE);
				loLaunchWorkflowMap.put(P8Constants.PROPERTY_PE_SECTION_TASK_NAMES,
						ApplicationConstants.WORKFLOW_LAUNCH_SECTION_TASKNAMES);
				loLaunchWorkflowMap.put(P8Constants.PROPERTY_PE_SERVICE_CAPACITY_IDS,
						loStringBufferServiceIDs.substring(0, loStringBufferServiceIDs.length() - 3));
				loLaunchWorkflowMap.put(P8Constants.PROPERTY_PE_SERVICE_CAPACITY_NAMES,
						loStringBufferServiceNames.substring(0, loStringBufferServiceNames.length() - 3));
				loLaunchWorkflowMap.put(P8Constants.PROPERTY_PE_TASK_NAME,
						P8Constants.PROPERTY_PE_TASK_TYPE_BR_APPLICATION);
			}
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.setContextData((HashMap) loLaunchWorkflowMap);
			setMoState("Transaction failed : Problem in reading the records in getLaunchWorkflowMap method\n");
			setMoState("expecting business application id : ".concat(asBussAppId).concat("::organization id:")
					.concat(asOrgId).concat(":::appId::").concat(asAppId).concat(":::asUserId:::").concat(asUserId)
					.concat(":::asProviderName:::").concat(asProviderName).concat("\n"));
			LOG_OBJECT.Error("Error occured while reading data from getLaunchWorkflowMap method", loAppEx);
			throw new ApplicationException("Exception occured when trying to get data in getLaunchWorkflowMap method",
					loAppEx);
		}
		setMoState("Transaction successfully ::: executed successfully in getLaunchWorkflowMap method");
		return loLaunchWorkflowMap;
	}

	/**
	 * This function update application status after work flow submitted
	 * 
	 * @param asOrgId Organization Id
	 * @param asAppId Application Id
	 * @param asBussAppId Business Application Id
	 * @param asUserId User Id
	 * @param asWFID Workflow Id
	 * @param aoSession to connect to database
	 * @return lbWFLaunchStatus Update is successful if its true
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public Boolean updateStatusAppSubmission(String asOrgId, String asAppId, String asBussAppId, String asUserId,
			String asWFID, String asSectionStatus, SqlSession aoSession) throws ApplicationException
	{
		boolean lbWFLaunchStatus = false;
		TaxonomyDAO loTaxonomyDAO = new TaxonomyDAO();
		Map<String, Object> loQueryMap = new HashMap<String, Object>();
		try
		{
			loQueryMap.put("asBusinessApplicationId", asBussAppId);
			loQueryMap.put("asOrgId", asOrgId);
			loQueryMap.put("asWFID", asWFID);
			loQueryMap.put("asUserId", asUserId);
			loQueryMap.put("asStatus", ApplicationConstants.STATUS_IN_REVIEW);
			loQueryMap.put("aoDate", new Date(System.currentTimeMillis()));
			loQueryMap.put("aoCityStatusSetBy", "");
			loQueryMap.put("aoCityStatusSetDate", "");
			loQueryMap.put("aoExpirationDate", "");
			loQueryMap.put("asSectionStatus", asSectionStatus);
			lbWFLaunchStatus = loTaxonomyDAO.updateStatusAppSubmission(loQueryMap, aoSession);
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.setContextData((HashMap) loQueryMap);
			setMoState("Transaction failed : Problem in reading the records in updateStatusAppSubmission method\n");
			setMoState("expecting business application id : ".concat(asBussAppId).concat("::organization id:")
					.concat(asOrgId).concat(":::appId::").concat(asAppId).concat(":::asUserId:::").concat(asUserId)
					.concat(":::asWFID:::").concat(asWFID).concat("\n"));
			LOG_OBJECT
					.Error("Error occured while updating status on Application Submission workflow launch for 'updateStatusAppSubmission'",
							loAppEx);
			throw new ApplicationException(
					"Error occured while updating status on Application Submission workflow launch for updateStatusAppSubmission",
					loAppEx);

		}
		setMoState("Transaction successfully ::: executed successfully in updateStatusAppSubmission method");
		return lbWFLaunchStatus;
	}

	/**
	 * This method is used to update the section application sub section and
	 * document table when we resubmit the application
	 * 
	 * @param asOrgId Organization Id
	 * @param asAppId Application Id
	 * @param asBussAppId Business Application Id
	 * @param asUserId User Id
	 * @param aoStatusIsReturnedSection section name
	 * @param aoSession to connect to database
	 * @return lbWFLaunchStatus Update is successful if its true
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public Boolean updateStatusAppReSubmission(String asOrgId, String asAppId, String asBussAppId, String asUserId,
			String asStatusIsReturned, ArrayList aoStatusIsReturnedSection, String asSectionStatus, SqlSession aoSession)
			throws ApplicationException
	{
		boolean lbWFLaunchStatus = false;
		Map<String, Object> loQueryMap = new HashMap<String, Object>();
		try
		{
			loQueryMap.put("asBusinessApplicationId", asBussAppId);
			loQueryMap.put("asAppId", asAppId);
			loQueryMap.put("asOrgId", asOrgId);
			loQueryMap.put("asUserId", asUserId);
			loQueryMap.put("asStatus", ApplicationConstants.STATUS_IN_REVIEW);
			loQueryMap.put("asProcStatus", "");
			loQueryMap.put("aoDate", new Date(System.currentTimeMillis()));
			loQueryMap.put("aoDate", new Date(System.currentTimeMillis()));
			loQueryMap.put("aoCityStatusSetBy", "");
			loQueryMap.put("aoCityStatusSetDate", "");
			loQueryMap.put("aoExpirationDate", "");
			loQueryMap.put("asSectionStatus", asSectionStatus);
			loQueryMap.put("Comments", "");
			for (Object loObject : aoStatusIsReturnedSection)
			{
				loQueryMap.put("asStatusIsReturnedSection", (String) loObject);
				loQueryMap.put("asStatus", ApplicationConstants.STATUS_IN_REVIEW);
				DAOUtil.masterDAO(aoSession, loQueryMap, ApplicationConstants.MAPPER_CLASS_TAXONOMY,
						"updateSectionStatus", "java.util.Map");
				if (asSectionStatus != null
						&& asSectionStatus.equalsIgnoreCase(ApplicationConstants.STATUS_CONDITIONALLY_APPROVED))
				{
					DAOUtil.masterDAO(aoSession, loQueryMap, ApplicationConstants.MAPPER_CLASS_TAXONOMY,
							"updateApplicationStatusForCA", "java.util.Map");
					// DAOUtil.masterDAO(aoSession, loQueryMap,
					// ApplicationConstants.MAPPER_CLASS_TAXONOMY,
					// "updateServiceAppOnServiceSubmissionForCA",
					// "java.util.Map");
				}
				else
				{
					DAOUtil.masterDAO(aoSession, loQueryMap, ApplicationConstants.MAPPER_CLASS_TAXONOMY,
							"updateApplicationStatus", "java.util.Map");
					DAOUtil.masterDAO(aoSession, loQueryMap, ApplicationConstants.MAPPER_CLASS_TAXONOMY,
							"updateServiceAppOnServiceSubmission", "java.util.Map");
				}
				if (asStatusIsReturned.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS))
				{
					DAOUtil.masterDAO(aoSession, loQueryMap, ApplicationConstants.MAPPER_CLASS_TAXONOMY,
							"updateSubSectionStatus", "java.util.Map");
					DAOUtil.masterDAO(aoSession, loQueryMap, ApplicationConstants.MAPPER_CLASS_TAXONOMY,
							"updateDocumentStatus", "java.util.Map");
					DAOUtil.masterDAO(aoSession, loQueryMap, ApplicationConstants.MAPPER_CLASS_TAXONOMY,
							"updateSectionComments", "java.util.Map");
					DAOUtil.masterDAO(aoSession, loQueryMap, ApplicationConstants.MAPPER_CLASS_TAXONOMY,
							"updateBusinessComments", "java.util.Map");
				}
				else
				{
					DAOUtil.masterDAO(aoSession, loQueryMap, ApplicationConstants.MAPPER_CLASS_TAXONOMY,
							"updateSubSectionStatusDef", "java.util.Map");
					DAOUtil.masterDAO(aoSession, loQueryMap, ApplicationConstants.MAPPER_CLASS_TAXONOMY,
							"updateDocumentStatusDef", "java.util.Map");
				}
				lbWFLaunchStatus = true;
			}
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.setContextData((HashMap) loQueryMap);
			setMoState("Transaction failed : Problem in reading the records in updateStatusAppReSubmission method\n");
			// setMoState("expecting business application id : ".concat(asBussAppId).concat("::organization id:").concat(asOrgId).concat(":::appId::").
			// concat(asAppId).concat(":::asUserId:::").concat(asUserId).concat(":::asWFID:::").concat(asWFID).concat("\n"));
			LOG_OBJECT
					.Error("Error occured while updating status on Application Submission workflow launch for 'updateStatusAppSubmission'",
							loAppEx);
			throw new ApplicationException(
					"Error occured while updating status on Application Submission workflow launch for updateStatusAppSubmission",
					loAppEx);

		}
		setMoState("Transaction successfully ::: executed successfully in updateStatusAppSubmission method");
		return lbWFLaunchStatus;
	}

	/*
	 * public Boolean updateSectionStatus(String asOrgId,String
	 * asBussAppId,String asStatusIsReturned,ArrayList<String>
	 * asStatusIsReturnedSection,SqlSession aoSession) throws
	 * ApplicationException{ boolean lbUpdateStatus = false; Map<String,Object>
	 * loQueryMap = new HashMap<String, Object>();
	 * loQueryMap.put("asBusinessApplicationId", asBussAppId);
	 * loQueryMap.put("asOrgId", asOrgId);
	 * 
	 * if(asStatusIsReturned.equalsIgnoreCase(ApplicationConstants.
	 * STATUS_RETURNED_FOR_REVISIONS)) { for (Object object :
	 * asStatusIsReturnedSection) { loQueryMap.put("asStatusIsReturnedSection",
	 * (String)object); loQueryMap.put("asStatus",
	 * ApplicationConstants.STATUS_IN_REVIEW); DAOUtil.masterDAO(aoSession,
	 * loQueryMap, ApplicationConstants.MAPPER_CLASS_TAXONOMY,
	 * "updateSectionStatus", "java.util.Map"); }
	 * 
	 * } lbUpdateStatus=true; return lbUpdateStatus; }
	 */
	/**
	 * This function update application status after work flow submitted
	 * 
	 * @param asOrgId Organization Id
	 * @param asAppId Application Id
	 * @param asBussAppId Business Application Id
	 * @param asUserId User Id
	 * @param asWFID Workflow Number
	 * @param aoSession to connect to database
	 * @return lbWFLaunchStatus update success if its true
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public Boolean updateStatusServiceSubmission(String asOrgId, String asAppId, String asBussAppId, String asUserId,
			String asWFID, String asServiceAppId, SqlSession aoSession) throws ApplicationException
	{
		boolean lbWFLaunchStatus = false;
		TaxonomyDAO loTaxonomyDAO = new TaxonomyDAO();
		Map<String, Object> loQueryMap = new HashMap<String, Object>();
		try
		{
			loQueryMap.put("asBusinessApplicationId", asBussAppId);
			loQueryMap.put("asOrgId", asOrgId);
			loQueryMap.put("asWFID", asWFID);
			loQueryMap.put("asUserId", asUserId);
			loQueryMap.put("asServiceAppId", asServiceAppId);
			loQueryMap.put("asStatus", ApplicationConstants.STATUS_IN_REVIEW);
			loQueryMap.put("asCAStatus", ApplicationConstants.STATUS_CONDITIONALLY_APPROVED);
			loQueryMap.put("asEntityType", ApplicationConstants.ENTITY_TYPE_SERVICE_APPLICATION);
			loQueryMap.put("aoDate", new Date(System.currentTimeMillis()));
			loQueryMap.put("aoCityStatusSetBy", "");
			loQueryMap.put("aoCityStatusSetDate", "");
			loQueryMap.put("aoExpirationDate", "");
			lbWFLaunchStatus = loTaxonomyDAO.updateStatusServiceSubmission(loQueryMap, aoSession);
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.setContextData((HashMap) loQueryMap);
			LOG_OBJECT
					.Error("Error occured while updating status on Service Submission workflow launch for 'updateStatusServiceSubmission'",
							loAppEx);
			throw new ApplicationException(
					"Error occured while updating status on Service Submission workflow launch for 'updateStatusServiceSubmission'",
					loAppEx);

		}
		setMoState("Transaction successfully ::: executed successfully in updateStatusServiceSubmission method");
		return lbWFLaunchStatus;
	}

	// taxonomy maintenance start
	/**
	 * This method updates the Item details to the database. First it deletes
	 * and then insert all the entries to synonym and linkage table.
	 * 
	 * @param aoTaxonomyTreeBean TaxonomyTree is a bean with all fields related
	 *            to Taxonomy (element id, branch id, flags ...etc)
	 * @param aoMyBatisSession to connect to database
	 * @return Boolean status
	 * @throws ApplicationException
	 */
	public Boolean updateItemDetails(TaxonomyTree aoTaxonomyTreeBean, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		Boolean lbSuccessStatus = false;
		try
		{
			List<TaxonomySynonymBean> loSynonymList = aoTaxonomyTreeBean.getMsSynonymList();
			List<TaxonomyLinkageBean> loLinkageList = aoTaxonomyTreeBean.getMsLinkageList();
			TaxonomySynonymBean loSynonymBean = null;
			TaxonomyLinkageBean loLinkageBean = null;

			// delete all old synonyms corresponding to the element id
			DAOUtil.masterDAO(aoMyBatisSession, aoTaxonomyTreeBean, ApplicationConstants.MAPPER_CLASS_TAXONOMY,
					"deleteTaxonomySynonymDetails", "com.nyc.hhs.model.TaxonomyTree");

			Iterator<TaxonomySynonymBean> loSynonymItr = loSynonymList.iterator();
			while (loSynonymItr.hasNext())
			{
				loSynonymBean = (TaxonomySynonymBean) loSynonymItr.next();
				DAOUtil.masterDAO(aoMyBatisSession, loSynonymBean, ApplicationConstants.MAPPER_CLASS_TAXONOMY,
						"insertTaxonomySynonymDetails", "com.nyc.hhs.model.TaxonomySynonymBean");
			}

			DAOUtil.masterDAO(aoMyBatisSession, aoTaxonomyTreeBean, ApplicationConstants.MAPPER_CLASS_TAXONOMY,
					"deleteTaxonomyLinkageDetails", "com.nyc.hhs.model.TaxonomyTree");

			// inserts all new Linkages
			Iterator<TaxonomyLinkageBean> loLinkageItr = loLinkageList.iterator();
			while (loLinkageItr.hasNext())
			{
				loLinkageBean = (TaxonomyLinkageBean) loLinkageItr.next();
				DAOUtil.masterDAO(aoMyBatisSession, loLinkageBean, ApplicationConstants.MAPPER_CLASS_TAXONOMY,
						"insertTaxonomyLinkageDetails", "com.nyc.hhs.model.TaxonomyLinkageBean");
			}

			// update the master table
			DAOUtil.masterDAO(aoMyBatisSession, aoTaxonomyTreeBean, ApplicationConstants.MAPPER_CLASS_TAXONOMY,
					"updateTaxonomyItemDetails", "com.nyc.hhs.model.TaxonomyTree");
		}

		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("TaxonomyTreeBean", CommonUtil.convertBeanToString(aoTaxonomyTreeBean));
			LOG_OBJECT.Error("Exception occured while updating Taxonomy data for selected Item", loAppEx);
			setMoState("Transaction Failed:: TaxonomyService: updateItemDetails method -Exception occured while updating data for selected Item:"
					+ aoTaxonomyTreeBean.getMsElementid() + "/n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: TaxonomyService: updateItemDetails method -Selected Item successfully updated in database:"
				+ aoTaxonomyTreeBean.getMsElementid() + "/n");
		return lbSuccessStatus;
	}

	/**
	 * This method inserts a new record to TAXONOMY_TRANSACTION_RECORD based
	 * upon taxonomy event type (delete or save).
	 * 
	 * @param aoTaxonomyTreeBean TaxonomyTree is a bean with all fields related
	 *            to Taxonomy (element id, branch id, flags ...etc)
	 * @param aoMyBatisSession to connect to database
	 * @return Boolean status
	 * @throws ApplicationException
	 */
	public Boolean insertTaxonomyTransactionRecord(TaxonomyTree aoTaxonomyTreeBean, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		Boolean lbSuccessStatus = false;
		aoTaxonomyTreeBean.setMoTimeStamp(getTimeStamp());
		try
		{
			String lsTrasactionEvent = aoTaxonomyTreeBean.getMsTransactionEvent();

			if (lsTrasactionEvent.equalsIgnoreCase("delete"))
			{
				aoTaxonomyTreeBean.setMsStatus("1");
				DAOUtil.masterDAO(aoMyBatisSession, aoTaxonomyTreeBean, ApplicationConstants.MAPPER_CLASS_TAXONOMY,
						"insertTaxonomyChangeDetailsLog", "com.nyc.hhs.model.TaxonomyTree");
			}
			else if (lsTrasactionEvent.equalsIgnoreCase("save"))
			{
				if (aoTaxonomyTreeBean.isMbEvidenceChanged())
				{
					aoTaxonomyTreeBean.setMsTransactionEvent("Evidence");
					if (aoTaxonomyTreeBean.getMsEvidenceReqd().equalsIgnoreCase("1"))
					{
						aoTaxonomyTreeBean.setMsStatus("0");
					}
					else
					{
						aoTaxonomyTreeBean.setMsStatus("1");
					}
					DAOUtil.masterDAO(aoMyBatisSession, aoTaxonomyTreeBean, ApplicationConstants.MAPPER_CLASS_TAXONOMY,
							"insertTaxonomyChangeDetailsLog", "com.nyc.hhs.model.TaxonomyTree");
				}
				if (aoTaxonomyTreeBean.isMbApprovalChanged())
				{
					aoTaxonomyTreeBean.setMsTransactionEvent("Approval");
					if (aoTaxonomyTreeBean.getMsSelectionFlag().equalsIgnoreCase("1"))
					{
						aoTaxonomyTreeBean.setMsStatus("0");
					}
					else
					{
						aoTaxonomyTreeBean.setMsStatus("1");
					}
					DAOUtil.masterDAO(aoMyBatisSession, aoTaxonomyTreeBean, ApplicationConstants.MAPPER_CLASS_TAXONOMY,
							"insertTaxonomyChangeDetailsLog", "com.nyc.hhs.model.TaxonomyTree");
				}
				if (aoTaxonomyTreeBean.isMbTaxonomyChanged())
				{
					aoTaxonomyTreeBean.setMsTransactionEvent("Active");
					if (aoTaxonomyTreeBean.getMsActiveFlag().equalsIgnoreCase("1"))
					{
						aoTaxonomyTreeBean.setMsStatus("0");
					}
					else
					{
						aoTaxonomyTreeBean.setMsStatus("1");
					}
					DAOUtil.masterDAO(aoMyBatisSession, aoTaxonomyTreeBean, ApplicationConstants.MAPPER_CLASS_TAXONOMY,
							"insertTaxonomyChangeDetailsLog", "com.nyc.hhs.model.TaxonomyTree");
					lbSuccessStatus = true;
				}
			}
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("TaxonomyTreeBean", CommonUtil.convertBeanToString(aoTaxonomyTreeBean));
			LOG_OBJECT.Error("Exception occured while inserting data into TAXONOMY_TRANSACTION_RECORD", loAppEx);
			setMoState("Transaction Failed:: TaxonomyService: insertTaxonomyTransactionRecord method -Exception occured while inserting record "
					+ "into TAXONOMY_TRANSACTION_RECORD for TaxonomyId:" + aoTaxonomyTreeBean.getMsElementid() + "/n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: TaxonomyService: insertTaxonomyTransactionRecord method - Inserted a record successfully "
				+ "in TAXONOMY_TRANSACTION_RECORD for TaxonomyId:" + aoTaxonomyTreeBean.getMsElementid() + "/n");
		return lbSuccessStatus;
	}

	/**
	 * This method creates and saves new element object (taxonomy item) into
	 * taxonomy master.
	 * 
	 * @param aoTaxonomyTree TaxonomyTree is a bean with all fields related to
	 *            Taxonomy (element id, branch id, flags ...etc)
	 * @param aoMyBatisSession to connect to database
	 * @return boolean status
	 * @throws ApplicationException
	 */
	public boolean saveNewTaxonomyObject(Integer aoCurrentSeq, TaxonomyTree aoTaxonomyTree, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		boolean lbNewTaxonomyObjectAdded = true;
		TaxonomyTree loTaxonomyTree = null;
		aoTaxonomyTree.setMsElementid(String.valueOf(aoCurrentSeq));
		try
		{
			loTaxonomyTree = (TaxonomyTree) DAOUtil.masterDAO(aoMyBatisSession, aoTaxonomyTree,
					ApplicationConstants.MAPPER_CLASS_TAXONOMY, "isDuplicateElementName",
					"com.nyc.hhs.model.TaxonomyTree");

			if ((Integer.valueOf(loTaxonomyTree.getMsIsDuplicate()) > 0))
			{
				throw new ApplicationException(
						"Exception occured while adding new taxonomy item to main Taxonomy on Refresh as trying to add duplicate Item");
			}
			else
			{
				DAOUtil.masterDAO(aoMyBatisSession, aoTaxonomyTree, ApplicationConstants.MAPPER_CLASS_TAXONOMY,
						"saveNewTaxonomyObject", "com.nyc.hhs.model.TaxonomyTree");
			}
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("TaxonomyTreeBean", CommonUtil.convertBeanToString(aoTaxonomyTree));
			LOG_OBJECT.Error("Exception occured while adding new taxonomy item to main Taxonomy", loAppEx);
			setMoState("Transaction Failed:: TaxonomyService: saveNewTaxonomyObject method -Exception occured while adding "
					+ "new taxonomy item to main Taxonomy:" + aoTaxonomyTree.getMsElementName() + "/n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: TaxonomyService: saveNewTaxonomyObject method - Successfully added a new taxonomy into main taxonomy:"
				+ aoTaxonomyTree.getMsElementName() + " /n");
		return lbNewTaxonomyObjectAdded;
	}

	/**
	 * This method inserts a new record to TAXONOMY_TRANSACTION_RECORD based
	 * upon taxonomy event type (add).
	 * 
	 * @param aoTaxonomyTreeSelected TaxonomyTree is a bean with all fields
	 *            related to Taxonomy (element id, branch id, flags ...etc)
	 * @param aoTaxonomyTreeBean TaxonomyTree is a bean with all fields related
	 *            to Taxonomy (element id, branch id, flags ...etc)
	 * @param aoMyBatisSession to connect to database
	 * @return Boolean status
	 * @throws ApplicationException
	 */
	public Boolean insertTaxonomyTransactionAddRecord(TaxonomyTree aoTaxonomyTreeSelected,
			TaxonomyTree aoTaxonomyTreeBean, SqlSession aoMyBatisSession) throws ApplicationException
	{
		Boolean lbSuccessStatus = false;
		aoTaxonomyTreeBean.setMoTimeStamp(getTimeStamp());
		try
		{
			aoTaxonomyTreeBean.setMsElementid(aoTaxonomyTreeSelected.getMsElementid());
			aoTaxonomyTreeBean.setMsBranchid(aoTaxonomyTreeSelected.getMsBranchid());
			aoTaxonomyTreeBean.setMsStatus("1");
			DAOUtil.masterDAO(aoMyBatisSession, aoTaxonomyTreeBean, ApplicationConstants.MAPPER_CLASS_TAXONOMY,
					"insertTaxonomyChangeDetailsLog", "com.nyc.hhs.model.TaxonomyTree");
			lbSuccessStatus = true;
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("TaxonomyTreeBean", CommonUtil.convertBeanToString(aoTaxonomyTreeBean));
			LOG_OBJECT.Error("Exception occured while inserting data into TAXONOMY_TRANSACTION_RECORD", loAppEx);
			setMoState("Transaction Failed:: TaxonomyService: insertTaxonomyTransactionAddRecord method -Exception occured while inserting record "
					+ "into TAXONOMY_TRANSACTION_RECORD for add new taxonomy with TaxonomyId:"
					+ aoTaxonomyTreeBean.getMsElementid() + "/n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: TaxonomyService: insertTaxonomyTransactionAddRecord method - Successfully added a new taxonomy and "
				+ "inserted record in TAXONOMY_TRANSACTION_RECORD for TaxonomyId:"
				+ aoTaxonomyTreeBean.getMsElementid() + "/n");
		return lbSuccessStatus;
	}

	/**
	 * This method gets complete Taxonomy Data from database
	 * 
	 * @param aoMyBatisSession to connect to database
	 * @return List of TaxonomyTree Object, TaxonomyTree is a bean with all
	 *         fields related to Taxonomy (element id, branch id, flags ...etc)
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<TaxonomyTree> getTaxonomyMasterDB(SqlSession aoMyBatisSession) throws ApplicationException
	{
		List<TaxonomyTree> loTaxonomyList = null;
		try
		{
			loTaxonomyList = (List<TaxonomyTree>) DAOUtil.masterDAO(aoMyBatisSession, null,
					ApplicationConstants.MAPPER_CLASS_TAXONOMY, "selectTaxonomyDetailsFromDB", null);

		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("TaxonomyList", loTaxonomyList);
			LOG_OBJECT.Error("Exception occured while retreiving complete Taxonomy Data from Database", loAppEx);
			setMoState("Transaction Failed:: TaxonomyService: getTaxonomyMasterDB method -Exception occured while retrieving complete Taxonomy "
					+ "List from Database where delete status is N:" + loTaxonomyList + "/n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: TaxonomyService: getTaxonomyMasterDB method Complete Taxonomy Tree List retrieved successfully for "
				+ "delete status N:" + loTaxonomyList + "/n");
		return loTaxonomyList;
	}

	/**
	 * This method is updates the taxonomy delete status in master table to Y.
	 * 
	 * @param aoTaxonomyTree TaxonomyTree is a bean with all fields related to
	 *            Taxonomy (element id, branch id, flags ...etc)
	 * @param aoMyBatisSession to connect to database
	 * @return Boolean status
	 * @throws ApplicationException
	 */
	public boolean updateTaxonomyDeleteStatus(TaxonomyTree aoTaxonomyTree, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		boolean lbDeleteStatus = false;
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoTaxonomyTree, ApplicationConstants.MAPPER_CLASS_TAXONOMY,
					"updateTaxonomyDeleteStatus", "com.nyc.hhs.model.TaxonomyTree");
			lbDeleteStatus = true;
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("ElementId", aoTaxonomyTree.getMsElementid());
			LOG_OBJECT.Error("Exception occured while retreiving updatedTaxonomyDeletedStatus ", loAppEx);
			setMoState("Transaction Failed:: TaxonomyService:updateTaxonomyDeleteStatus method -Exception occured while  updating Taxonomy "
					+ "Delete Status to Y for ElementId:" + aoTaxonomyTree.getMsElementid() + "/n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: TaxonomyService:updateTaxonomyDeleteStatus method -Taxonomy Delete  status  successfully updated "
				+ "to Y  for element ID:" + aoTaxonomyTree.getMsElementid() + "/n");
		return lbDeleteStatus;
	}

	/**
	 * This method is updates the taxonomy delete status to Y in linkage table
	 * 
	 * @param aoTaxonomyTree TaxonomyTree is a bean with all fields related to
	 *            Taxonomy (element id, branch id, flags ...etc)
	 * @param aoMyBatisSession to connect to database
	 * @return Boolean status
	 * @throws ApplicationException
	 */
	public boolean updateLinkageDeleteStatus(TaxonomyTree aoTaxonomyTree, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		boolean lbDeleteStatus = false;
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoTaxonomyTree, ApplicationConstants.MAPPER_CLASS_TAXONOMY,
					"updateLinkageDeleteStatus", "com.nyc.hhs.model.TaxonomyTree");

			lbDeleteStatus = true;

		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("ElementId", aoTaxonomyTree.getMsElementid());
			LOG_OBJECT.Error("Exception occured while retreiving updatedLinkageDeleteStatus ", loAppEx);
			setMoState("Transaction Failed:: TaxonomyService: updateLinkageDeleteStatus method -Exception occured while updating Taxonomy "
					+ "Linkage Delete Status to Y for element ID:" + aoTaxonomyTree.getMsElementid() + "/n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: TaxonomyService: updateLinkageDeleteStatus method -Taxonomy Linkage Delete status  successfully "
				+ "updated to Y for element ID:" + aoTaxonomyTree.getMsElementid() + "/n");
		return lbDeleteStatus;
	}

	/**
	 * This method is used to update the synonym Delete Status to Y in synonym
	 * table.
	 * 
	 * @param aoTaxonomyTree TaxonomyTree is a bean with all fields related to
	 *            Taxonomy (element id, branch id, flags ...etc)
	 * @param aoMyBatisSession to connect to database
	 * @return Boolean status
	 * @throws ApplicationException
	 */
	public boolean updateSynonymDeleteStatus(TaxonomyTree aoTaxonomyTree, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		boolean lbDeleteStatus = false;
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoTaxonomyTree, ApplicationConstants.MAPPER_CLASS_TAXONOMY,
					"updateSynonymDeleteStatus", "com.nyc.hhs.model.TaxonomyTree");
			lbDeleteStatus = true;
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("ElementId", aoTaxonomyTree.getMsElementid());
			LOG_OBJECT.Error("Exception occured while retreiving updatedLinkageDeleteStatus ", loAppEx);
			setMoState("Transaction Failed:: TaxonomyService:updateSynonymDeleteStatus method -Exception occured while  updating Synonym "
					+ "Delete Status to Y for Element ID:" + aoTaxonomyTree.getMsElementid() + "/n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: TaxonomyService:updateSynonymDeleteStatus method -Synonym Delete Status  successfully  updated "
				+ "to Y for element ID:" + aoTaxonomyTree.getMsElementid() + "/n");
		return lbDeleteStatus;
	}

	/**
	 * This method is used to check the duplicate element name from the taxonomy
	 * tree.
	 * 
	 * @param aoTaxonomyTree TaxonomyTree is a bean with all fields related to
	 *            Taxonomy (element id, branch id, flags ...etc)
	 * @param aoMyBatisSession to connect to database
	 * @return TaxonomyTree is a bean with all fields related to Taxonomy
	 *         (element id, branch id, flags ...etc)
	 * @throws ApplicationException
	 */
	public TaxonomyTree isDuplicateElementName(TaxonomyTree aoTaxonomyTreeBean, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		TaxonomyTree loTaxonomyTree = null;
		try
		{
			if (aoTaxonomyTreeBean.getMsElementType().equalsIgnoreCase("Service Area")
					|| aoTaxonomyTreeBean.getMsElementType().equalsIgnoreCase("Function"))
			{
				aoTaxonomyTreeBean.setMsElementType("service area");
				loTaxonomyTree = (TaxonomyTree) DAOUtil.masterDAO(aoMyBatisSession, aoTaxonomyTreeBean,
						ApplicationConstants.MAPPER_CLASS_TAXONOMY, "isDuplicateElementName",
						"com.nyc.hhs.model.TaxonomyTree");
				if (!(Integer.valueOf(loTaxonomyTree.getMsIsDuplicate()) > 0))
				{
					aoTaxonomyTreeBean.setMsElementType("function");
					loTaxonomyTree = (TaxonomyTree) DAOUtil.masterDAO(aoMyBatisSession, aoTaxonomyTreeBean,
							ApplicationConstants.MAPPER_CLASS_TAXONOMY, "isDuplicateElementName",
							"com.nyc.hhs.model.TaxonomyTree");
				}
			}
			else
			{
				loTaxonomyTree = (TaxonomyTree) DAOUtil.masterDAO(aoMyBatisSession, aoTaxonomyTreeBean,
						ApplicationConstants.MAPPER_CLASS_TAXONOMY, "isDuplicateElementName",
						"com.nyc.hhs.model.TaxonomyTree");
			}
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("TaxonomyTreeBean", CommonUtil.convertBeanToString(aoTaxonomyTreeBean));
			LOG_OBJECT.Error("Exception occured while checking Duplicate Element Name ", loAppEx);
			setMoState("Transaction Failed:: TaxonomyService:isDuplicateElementName method -Exception occured while checking Duplicate Element "
					+ "Name for element name " + loTaxonomyTree.getMsElementName() + "/n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: TaxonomyService:isDuplicateElementName method Duplicate Element Name verying successfully completed "
				+ "for element name " + loTaxonomyTree.getMsElementName() + "/n");
		return loTaxonomyTree;
	}

	/**
	 * This method deletes all records from taxonomy_transaction_record table on
	 * re-caching
	 * 
	 * @param aoMyBatisSession to connect to database
	 * @return Boolean status
	 * @throws ApplicationException
	 */
	public boolean deleteFromTaxonomyTranRec(SqlSession aoMyBatisSession) throws ApplicationException
	{
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, null, ApplicationConstants.MAPPER_CLASS_TAXONOMY,
					"deleteFromTaxonomyTranRec", null);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT
					.Error("Exception occured while deleting all records from taxonomy_transaction_record table on re-caching /n",
							loAppEx);
			setMoState("Transaction Failed:: TaxonomyService: deleteFromTaxonomyTranRec method -Exception occured while deleting all records "
					+ "from taxonomy_transaction_record table on re-caching /n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: TaxonomyService: deleteFromTaxonomyTranRec method - All records successfully deleted from "
				+ "taxonomy_transaction_record table on re-caching /n");
		return true;
	}

	/**
	 * This method is used to fetch taxonomy Item left menu detail page
	 * 
	 * @param aoElementId element id of the taxonomy selected
	 * @param aoMyBatisSession to connect to database
	 * @return TaxonomyTree is a bean with all fields related to Taxonomy
	 *         (element id, branch id, flags ...etc)
	 * @throws ApplicationException
	 */
	public TaxonomyTree getTaxonomyItemDetailsLeftMenu(String aoElementId, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		TaxonomyTree loTaxonomyTreeBean = null;
		TaxonomyDAO loTaxonomyDAO = new TaxonomyDAO();
		try
		{
			loTaxonomyTreeBean = loTaxonomyDAO.getTaxonomyMasterDataLeftMenu(aoElementId, aoMyBatisSession);
			List<TaxonomySynonymBean> loTaxonomySynonymBean = loTaxonomyDAO.getTaxonomySynonymDataLeftMenu(aoElementId,
					aoMyBatisSession);
			List<TaxonomyLinkageBean> loTaxonomyLinkage = loTaxonomyDAO.getTaxonomyLinkageDataLeftMenu(aoElementId,
					aoMyBatisSession);

			loTaxonomyTreeBean.setMsSynonymList(loTaxonomySynonymBean);
			loTaxonomyTreeBean.setMsLinkageList(loTaxonomyLinkage);
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("ElementId", aoElementId);
			LOG_OBJECT.Error("Exception occured while retreiving TaxonomyItemDetails", loAppEx);
			setMoState("Transaction Failed:: TaxonomyService:getTaxonomyItemDetailsLeftMenu method -Exception occured while retreiving "
					+ "TaxonomyItemDetails for left menu for elementId" + aoElementId + "/n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: TaxonomyService:getTaxonomyItemDetailsLeftMenu method - TaxonomyItemDetails for left menu "
				+ "successfully retreived for elementId" + aoElementId + "/n");
		return loTaxonomyTreeBean;
	}

	/**
	 * This method retrieves the Taxonomy Item Details for the Last added
	 * taxonomy Item
	 * 
	 * @param aoCurrentSeq this is the element id of the last added taxonomy
	 *            item
	 * @param aoMyBatisSession to connect to database
	 * @return loTaxonomyTreeBean TaxonomyTree is a bean with all fields related
	 *         to Taxonomy (element id, branch id, flags ...etc)
	 * @throws ApplicationException
	 */
	public TaxonomyTree getTaxonomyItemDetails(Integer aoCurrentSeq, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		TaxonomyTree loTaxonomyTreeBean = null;
		try
		{
			loTaxonomyTreeBean = (TaxonomyTree) DAOUtil.masterDAO(aoMyBatisSession, aoCurrentSeq,
					ApplicationConstants.MAPPER_CLASS_TAXONOMY, "getTaxonomyItemDetails", "java.lang.Integer");
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("SelectedLinkage", loTaxonomyTreeBean);
			LOG_OBJECT.Error("Exception occured while retreiving TaxonomyItemDetails", loAppEx);
			setMoState("Transaction Failed:: TaxonomyService:getTaxonomyItemDetails method -Exception occured while retreiving Taxonomy "
					+ "Item Details for last added taxonomy Item:" + loTaxonomyTreeBean.getMsElementid() + "/n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: TaxonomyService:getTaxonomyItemDetails method -TaxonomyItem Details Successfully retreived for "
				+ "last added taxonomy Item:" + loTaxonomyTreeBean.getMsElementid() + "/n");
		return loTaxonomyTreeBean;
	}

	/**
	 * This method perform evidence flag validation while re-caching
	 * 
	 * @param aoMyBatisSession to connect to database
	 * @return List<TaxonomyTree> TaxonomyTree is a bean with all fields related
	 *         to Taxonomy (element id, branch id, flags ...etc)
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<TaxonomyTree> reCacheEvidenceValidation(SqlSession aoMyBatisSession) throws ApplicationException
	{
		List<TaxonomyTree> loCacheEvidenceValidation = new ArrayList<TaxonomyTree>();
		try
		{
			loCacheEvidenceValidation = (List<TaxonomyTree>) DAOUtil.masterDAO(aoMyBatisSession, null,
					ApplicationConstants.MAPPER_CLASS_TAXONOMY, "reCacheEvidenceValidation", null);
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("Re-CacheEvidenceValidation", loCacheEvidenceValidation);
			LOG_OBJECT.Error("Exception occured while re-cache Evidence Validation", loAppEx);
			setMoState("Transaction Failed:: TaxonomyService:reCacheEvidenceValidation method -Exception occured while "
					+ "re-cache Evidence Validation: /n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: TaxonomyService:reCacheEvidenceValidation method - re-cache Evidence Validation Successfully done:/n");
		return loCacheEvidenceValidation;
	}

	/**
	 * This method insert data into general audit table
	 * 
	 * @param aoGeneralAuditBeanRecache has fields related to general
	 *            audit(time, date, event type... etc)
	 * @param aoMyBatisSession to connect to database
	 * @throws ApplicationException
	 */
	public void insertGeneralAuditDetails(GeneralAuditBean aoGeneralAuditBean, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoGeneralAuditBean, ApplicationConstants.MAPPER_CLASS_TAXONOMY,
					"insertGeneralAuditDetails", "com.nyc.hhs.model.GeneralAuditBean");
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("GeneralAuditDetails", aoGeneralAuditBean);
			LOG_OBJECT.Error("Exception occured while inserting data in GeneralAudit Table", loAppEx);
			setMoState("Transaction Failed:: TaxonomyService:insertGeneralAuditDetails method -Exception occured while inserting "
					+ "data in GeneralAudit Table: /n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: TaxonomyService:insertGeneralAuditDetails method - General Audit details have been successfully "
				+ "inserted into General Audiy Table: /n");
	}

	/**
	 * This method retrieves general audit details from general audit table
	 * 
	 * @param aoGeneralAuditBeanRecache has fields related to general
	 *            audit(time, date, event type... etc)
	 * @param aoMyBatisSession to connect to database
	 * @return loGeneralAuditBean has fields related to general audit(time,
	 *         date, event type... etc)
	 * @throws ApplicationException
	 */
	public GeneralAuditBean getLastUpdatedTaxonomyRecacheDetails(GeneralAuditBean aoGeneralAuditBeanRecache,
			SqlSession aoMyBatisSession) throws ApplicationException
	{
		GeneralAuditBean loGeneralAuditBean = null;
		try
		{
			loGeneralAuditBean = (GeneralAuditBean) DAOUtil.masterDAO(aoMyBatisSession, aoGeneralAuditBeanRecache,
					ApplicationConstants.MAPPER_CLASS_TAXONOMY, "getLastUpdatedTaxonomyRecacheDetails",
					"com.nyc.hhs.model.GeneralAuditBean");

		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while retreiving last updated taxonomy re-cache details", loAppEx);
			setMoState("Transaction Failed:: TaxonomyService:getLastUpdatedTaxonomyRecacheDetails method -Exception occured while while "
					+ "retreiving last updated taxonomy re-cache details: /n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: TaxonomyService:getLastUpdatedTaxonomyRecacheDetails method - Last updated taxonomy re-cache "
				+ "details have been successfully retreived: /n");
		return loGeneralAuditBean;
	}

	/**
	 * This method retrieves general audit details from general audit table
	 * 
	 * @param aoMyBatisSession to connect to database
	 * @return Integer gets element id of the last taxonomy added
	 * @throws ApplicationException
	 */
	public Integer getCurrentSeqFromTable(SqlSession aoMyBatisSession) throws ApplicationException
	{
		int liCurrentSeq;
		try
		{
			liCurrentSeq = (Integer) DAOUtil.masterDAO(aoMyBatisSession, null,
					ApplicationConstants.MAPPER_CLASS_TAXONOMY, "getCurrentSeqFromTable", null);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while retreiving Current Sequence from Table", loAppEx);
			setMoState("Transaction Failed:: TaxonomyService:getCurrentSeqFromTable method -Exception occured while while retreiving "
					+ "Current Sequence from Table: /n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: TaxonomyService:getLastUpdatedTaxonomyRecacheDetails method - Current Sequence from Table "
				+ "have been retreived successfully: /n");
		return liCurrentSeq;
	}

	/**
	 * This method validates active flag status in case a taxonomy is made
	 * active but its parent is inactive and other function is if any taxonomy
	 * is made inactive then all its children are made inactive
	 * 
	 * @param aoTaxonomyTreeBean TaxonomyTree is a bean with all fields related
	 *            to Taxonomy (element id, branch id, flags ...etc)
	 * @param aoMyBatisSession to connect to database
	 * @return Boolean status
	 * @throws ApplicationException
	 */
	public Boolean activeFlagValidate(TaxonomyTree aoTaxonomyTreeBean, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		TaxonomyDAO loTaxonomyDAO = new TaxonomyDAO();
		Boolean lbSuccessStatus = false;
		try
		{
			if (aoTaxonomyTreeBean.getMsActiveFlag().equalsIgnoreCase("1"))
			{
				// start, when active flag is checked
				TaxonomyTree loTaxonomyTreebean = null;
				String loBranchId = aoTaxonomyTreeBean.getMsBranchid();
				String lsArr[] = loBranchId.split(",");
				String lsElementId = "";
				if (lsArr.length == 1)
				{
					lbSuccessStatus = true;
				}
				else
				{
					lsElementId = lsArr[lsArr.length - 2];
					loTaxonomyTreebean = loTaxonomyDAO.getTaxonomyMasterDataLeftMenu(lsElementId, aoMyBatisSession);
					if (loTaxonomyTreebean.getMsActiveFlag().equalsIgnoreCase("0")
							&& !"Populations".equalsIgnoreCase(loTaxonomyTreebean.getMsElementType()))
					{
						lbSuccessStatus = false;
					}
					else
					{
						lbSuccessStatus = true;
					}
				}
			}
			else
			{
				DAOUtil.masterDAO(aoMyBatisSession, aoTaxonomyTreeBean, ApplicationConstants.MAPPER_CLASS_TAXONOMY,
						"updateTaxonomyChildrenActiveFlag", "com.nyc.hhs.model.TaxonomyTree");
				aoTaxonomyTreeBean.setMsStatus("1");
				lbSuccessStatus = true;
			}

		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("TaxonomyTreeBean", CommonUtil.convertBeanToString(aoTaxonomyTreeBean));
			LOG_OBJECT.Error("Exception occured while validating active flag status of taxonomy item", loAppEx);
			setMoState("Transaction Failed:: TaxonomyService: activeFlagValidate method -Exception occured while validating active "
					+ "flag status of taxonomy item /n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: TaxonomyService: activeFlagValidate method - active flag validation performed successfully /n");
		return lbSuccessStatus;
	}

	/**
	 * This method retrieves general audit details from general audit table
	 * 
	 * @param aoGeneralAuditBean has fields related to general audit(time, date,
	 *            event type... etc)
	 * @param aoMyBatisSession to connect to database
	 * @return loGeneralAuditBean has fields related to general audit(time,
	 *         date, event type... etc)
	 * @throws ApplicationException
	 */
	public GeneralAuditBean getLastUpdatedTaxonomyDetails(GeneralAuditBean aoGeneralAuditBean,
			SqlSession aoMyBatisSession) throws ApplicationException
	{
		GeneralAuditBean loGeneralAuditBean = null;
		try
		{
			loGeneralAuditBean = (GeneralAuditBean) DAOUtil.masterDAO(aoMyBatisSession, aoGeneralAuditBean,
					ApplicationConstants.MAPPER_CLASS_TAXONOMY, "getLastUpdatedTaxonomyDetails",
					"com.nyc.hhs.model.GeneralAuditBean");
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while retreiving last updated taxonomy re-cache details", loAppEx);
			setMoState("Transaction Failed:: TaxonomyService:getLastUpdatedTaxonomyRecacheDetails method -Exception occured while while "
					+ "retreiving last updated taxonomy re-cache details: /n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: TaxonomyService:getLastUpdatedTaxonomyRecacheDetails method - Last updated taxonomy re-cache "
				+ "details have been successfully retreived: /n");
		return loGeneralAuditBean;
	}

	public boolean updateFlagApplicationSettings(SqlSession aoMyBatisSession) throws ApplicationException
	{
		boolean lbStatus = false;
		String lsCurrentdate = CommonUtil.getCurrentTime();
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, lsCurrentdate, ApplicationConstants.MAPPER_CLASS_TAXONOMY,
					"insertRecacheTime", "java.lang.String");
			// int lsCacheVersion =
			// ((Integer)BaseCacheManagerWeb.getInstance().getCacheObject("cacheTaxonomyVersion")).intValue();
			// BaseCacheManagerWeb.getInstance().putCacheObject("cacheTaxonomyVersion",lsCacheVersion==100?1:++lsCacheVersion);

			// TaxonomyDAO loTaxonomyDAO = new TaxonomyDAO();
			// loTaxonomyDAO.updateRecacheFlag(aoMyBatisSession,++lsCacheVersion);
			lbStatus = true;
		}
		catch (ApplicationException loAppEx)
		{
			lbStatus = false;
			LOG_OBJECT.Error("Exception occured while putting data into Cache", loAppEx);
			setMoState("Transaction Failed:: TaxonomyService:putTaxonomyintoCache method -Exception occured while putting data into Cache: /n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: TaxonomyService:putTaxonomyintoCache method - Taxonomy data has been put into cache successfully: /n");
		return lbStatus;
	}

	/**
	 * This method put the generated taxonomy DOM object into Cache
	 * 
	 * @return Boolean whether taxonomy DOM object successfully put into cache
	 *         or not
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public boolean putTaxonomyintoCache(SqlSession aoMyBatisSession) throws ApplicationException
	{
		boolean lbStatus = false;
		// TaxonomyDAO loTaxonomyDAO = new TaxonomyDAO();
		try
		{
			// String lsEnvironmentType = (String)
			// BaseCacheManagerWeb.getInstance().getCacheObject(ApplicationConstants.ENVIROMENT_TYPE);
			// if (null != lsEnvironmentType)
			// {
			// // update call to applicationsettings table for recache flag
			// // update to true
			// Integer loRecacheUpdateReturn = (Integer)
			// loTaxonomyDAO.updateRecacheFlag(aoMyBatisSession);
			// if (loRecacheUpdateReturn > 0)
			// {
			// }
			// else
			// {
			// throw new
			// ApplicationException("Failed: Update recache flag failed in applicationsettings table");
			// }
			// }
			// Instantiating TaxonomyDOM to generate DOM Tree for Taxonomy
			List<TaxonomyTree> loTaxonomyList = null;
			String lsKey = ApplicationConstants.TAXONOMY_ELEMENT;
			loTaxonomyList = (List<TaxonomyTree>) DAOUtil.masterDAO(aoMyBatisSession, null,
					ApplicationConstants.MAPPER_CLASS_TAXONOMY, "selectTaxonomyDetails", null);
			TaxonomyDOMUtil loTaxonomyDOM = new TaxonomyDOMUtil();
			Document loTaxonomyDom = loTaxonomyDOM.createTaxonomyDOMObj(loTaxonomyList);
			// storing object in Cache
			ICacheManager loICacheManager = BaseCacheManagerWeb.getInstance();
			loICacheManager.putCacheObject(lsKey, loTaxonomyDom);
			String lsCurrentdate = CommonUtil.getCurrentTime();
			loICacheManager.putCacheObject(ApplicationConstants.LAST_RECACHE_TIME, lsCurrentdate);

			lbStatus = true;
		}
		catch (ApplicationException loAppEx)
		{
			lbStatus = false;
			LOG_OBJECT.Error("Exception occured while putting data into Cache", loAppEx);
			setMoState("Transaction Failed:: TaxonomyService:putTaxonomyintoCache method -Exception occured while putting data into Cache: /n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: TaxonomyService:putTaxonomyintoCache method - Taxonomy data has been put into cache successfully: /n");
		return lbStatus;
	}

	public String selectRecacheTime(SqlSession aoMyBatisSession) throws ApplicationException
	{
		String lsLastRecacheTime = "";
		try
		{
			lsLastRecacheTime = (String) DAOUtil.masterDAO(aoMyBatisSession, null,
					ApplicationConstants.MAPPER_CLASS_TAXONOMY, "selectRecacheTime", null);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while getting last recache time from application setting table",
					loAppEx);
			setMoState("Transaction Failed:: selectRecacheTime method -Exception occured while "
					+ "getting last recache time from application setting table. /n ");
			throw loAppEx;
		}
		setMoState("Transaction Success:: selectRecacheTime method -  "
				+ "last recache time from application setting table " + "successfully retreived: /n");
		return lsLastRecacheTime;
	}

	/**
	 * This method fetches taxonomy Recache flag
	 * 
	 * @param aoMyBatisSession to connect to database
	 * @return loRecacheFlagMap contains Re-cache flag
	 * @throws ApplicationException
	 */
	/*
	 * @SuppressWarnings("rawtypes") public Map getCacheFlag(SqlSession
	 * aoMyBatisSession) throws ApplicationException { TaxonomyDAO loTaxonomyDAO
	 * = new TaxonomyDAO(); Map loRecacheFlagMap = new HashMap(); try {
	 * loRecacheFlagMap = loTaxonomyDAO.getCacheFlag(aoMyBatisSession); } catch
	 * (ApplicationException loAppEx) { LOG_OBJECT.Error(
	 * "Exception occured while retreiving last updated taxonomy re-cache details"
	 * , loAppEx); setMoState(
	 * "Transaction Failed:: getCacheFlag method -Exception occured while " +
	 * "fetching Re-Cache flag from applicationsettings table. /n "); throw
	 * loAppEx; } setMoState("Transaction Success:: getCacheFlag method -  " +
	 * "fetching Re-Cache flag from applicationsettings table " +
	 * "details have been successfully retreived: /n"); return loRecacheFlagMap;
	 * }
	 */

	public static java.sql.Timestamp getTimeStamp()
	{
		Calendar loCalendar = Calendar.getInstance();
		java.util.Date loNow = loCalendar.getTime();
		java.sql.Timestamp loCurrentTimestamp = new java.sql.Timestamp(loNow.getTime());
		return loCurrentTimestamp;
	}

	// taxonomy maintenance end
	// code added for deleted service name
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

	// code added for deleted service name

	/**
	 * This method counts the no. of procurement in Planned status
	 * 
	 * <ul>
	 * <li>1. Retrieve procurement map from channel object</li>
	 * <li>2. If the retrieved map is not null and contains data then execute
	 * query <b>plannedProcurementCount</b> to fetch the count of procurement in
	 * Planned status</li>
	 * <li>3. Return the fetched data</li>
	 * </ul>
	 * @param aoMyBatisSession - mybatis SQL session
	 * @param aoProcurementMap - a Map<String, String> object
	 * @return liPlannedProcurementCount - count of procurement in Planned
	 *         status
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	public Integer getPlannedProcurementCount(SqlSession aoMyBatisSession, Map<String, String> aoProcurementMap)
			throws ApplicationException
	{
		int liPlannedProcurementCount = HHSConstants.INT_ZERO;
		// entering try block
		try
		{
			// checking whether or not procurtement map contains data
			if (aoProcurementMap != null && !aoProcurementMap.isEmpty())
			{
				liPlannedProcurementCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoProcurementMap,
						ApplicationConstants.MAPPER_CLASS_TAXONOMY, HHSConstants.PLANNED_PROC_COUNT,
						HHSConstants.JAVA_UTIL_MAP);
			}

		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while getting planned procurement count", loAppEx);
			setMoState("Transaction Failed:: getPlannedProcurementCount method -Exception occured while "
					+ "getting planned procurement count. /n ");
			throw loAppEx;
		}
		setMoState("Transaction Success:: getPlannedProcurementCount method -  "
				+ "successfully fetched planned procurement count " + "from procurement : /n");
		return liPlannedProcurementCount;
	}

	/**
	 * This method counts the no. of procurement in Released status
	 * 
	 * <ul>
	 * <li>1. Retrieve procurement map from channel object</li>
	 * <li>2. If the retrieved map is not null and contains data then execute
	 * query <b>releasedProcurementCount</b> to fetch the count of procurement
	 * in Released status</li>
	 * <li>3. Return the fetched data</li>
	 * </ul>
	 * @param aoMyBatisSession - mybatis SQL session
	 * @param aoProcurementMap - a Map<String, String> object
	 * @return liReleaseProcurementCount - count of procurement in Released
	 *         status
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	public Integer getReleasedProcurementCount(SqlSession aoMyBatisSession, Map<String, String> aoProcurementMap)
			throws ApplicationException
	{
		int liReleaseProcurementCount = HHSConstants.INT_ZERO;
		// entering try block
		try
		{
			// checking whether or not procurtement map contains data
			if (aoProcurementMap != null && !aoProcurementMap.isEmpty())
			{
				liReleaseProcurementCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoProcurementMap,
						ApplicationConstants.MAPPER_CLASS_TAXONOMY, HHSConstants.RELEASED_PROC_COUNT,
						HHSConstants.JAVA_UTIL_MAP);
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while getting released procurement count", loAppEx);
			setMoState("Transaction Failed:: getReleaseProcurementCount method -Exception occured while "
					+ "getting released procurement count. /n ");
			throw loAppEx;
		}
		setMoState("Transaction Success:: getReleaseProcurementCount method -  "
				+ "successfully fetched released procurement count " + "from procurement : /n");
		return liReleaseProcurementCount;
	}
	
	/** This method will fetch document id list form document table
	 * Added for Defect fix # 8455 Scenario 1
	 * @param asServiceId
	 * @param asOrgId
	 * @param asBussAppId
	 * @param aoMyBatisSession
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<String> getDocumentIdListAttachedToServiceApp(String asServiceId, String asOrgId,String asBussAppId,SqlSession aoMyBatisSession) throws ApplicationException
			{
				List<String> loDocumentIdList = new ArrayList<String>();
				HashMap<String, Object> loHmParameter = new HashMap<String, Object>();
				try
				{
					loHmParameter.put(HHSR5Constants.AS_ORG_ID, asOrgId);
					loHmParameter.put(HHSR5Constants.AS_BUSINESS_APP_ID, asBussAppId);
					loHmParameter.put(HHSR5Constants.AS_SERVICE_APP_ID, asServiceId);
					loDocumentIdList = (List<String>)DAOUtil.masterDAO(aoMyBatisSession, loHmParameter, ApplicationConstants.MAPPER_CLASS_TAXONOMY,
							HHSR5Constants.GET_DOC_ID_FROM_DOCUMENT_TABLE, "java.util.Map");
				}
				catch (ApplicationException loAppEx)
				{
					LOG_OBJECT.Error("Exception occured while getting docuemtId's for service application", loAppEx);
					setMoState("Transaction Failed:: getDocumentIdListAttachedToServiceApp method -Exception occured while "
							+ "docuemtId's for service application. /n ");
					throw loAppEx;
				}
				return loDocumentIdList;
				
			}
	
	/** This method will check Linkage of document to other entities 
	 * Added for Defect fix # 8455 Scenario 1
	 * @param aoMyBatisSession
	 * @param aoDocumentIdList
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Boolean> getDocumentLinkageFlagFromDBView(SqlSession aoMyBatisSession,List<String> aoDocumentIdList) throws ApplicationException
	{

		Map<String,Boolean> loDocumentDataMap = new HashMap<String, Boolean>();
		try
		{
			for (Iterator iterator = aoDocumentIdList.iterator(); iterator
					.hasNext();) {
				String lsDocumentId = (String) iterator.next();
				if(null != lsDocumentId)
				{
					List<String> loLinkedObjNameList = (List<String>) DAOUtil.masterDAO(aoMyBatisSession, lsDocumentId,
							ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER,
							ApplicationConstants.GET_LINKED_TO_OBJECT_NAME, ApplicationConstants.JAVA_LANG_STRING);
					if(null != loLinkedObjNameList && !loLinkedObjNameList.isEmpty())
					{
						loDocumentDataMap.put(lsDocumentId, true);
					}
					else
					{
						loDocumentDataMap.put(lsDocumentId, false);
					}
				}
			}
			
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while getting docuemtId's for service application", loAppEx);
			setMoState("Transaction Failed:: getDocumentIdListAttachedToServiceApp method -Exception occured while "
					+ "docuemtId's for service application. /n ");
			throw loAppEx;
		}
		return loDocumentDataMap;
		
	
	}
}
