package com.nyc.hhs.daomanager.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.model.TaxonomyServiceBean;
import com.nyc.hhs.model.TaxonomyTree;
import com.nyc.hhs.util.BusinessApplicationUtil;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.DateUtil;

/**
 * TaxonomyMaintenanceBatchService: This Service is for implementation of
 * Taxonomy rules for 'approval','active' and 'evidence' flags
 */
public class TaxonomyMaintenanceBatchService extends ServiceState
{

	private static final LogInfo LOG_OBJECT = new LogInfo(TaxonomyMaintenanceBatchService.class);

	private static final String DEACTIVATED = "Deactivated";

	/**
	 * This method provide a list of taxonomy items where validation flags
	 * changed from Approval to non-approval, Evidence to non-evidence and
	 * Evidence flag moved from child to parent
	 * 
	 * @param aoMyBatisSession to connect to database
	 * @param aoHashMap has taxonomy item element id's
	 * @return List loTaxonomyList list of taxonomyTree bean from DB
	 * @throws ApplicationException
	 */

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public List<TaxonomyTree> executeTaxonomyBtch(SqlSession aoMyBatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		List<TaxonomyTree> loTaxonomyList = null;
		try
		{
			loTaxonomyList = (List<TaxonomyTree>) DAOUtil.masterDAO(aoMyBatisSession, null,
					"com.nyc.hhs.service.db.services.application.TaxonomyBatchMapper", "executeTaxonomyBtch", null);

		}
		catch (ApplicationException loAppEx)
		{
			setMoState("Transaction failed ::: Service TaxonomyMaintenanceBatchService in method while getting list of taxonomy items\n");
			LOG_OBJECT.Error("Error occured while getting list of taxonomy items", loAppEx);
			throw new ApplicationException("Error occured while getting list of taxonomy items", loAppEx);
		}
		setMoState("Transaction successfully :::  Service TaxonomyMaintenanceBatchService in method executeTaxonomyBtch  Successfully list "
				+ "of taxonomy items where validation flags changed from Approval to non-approval, Evidence to non-evidence and Evidence flag moved "
				+ "from child to parent\n");
		return loTaxonomyList;
	}

	/**
	 * This method provide a list of taxonomy items where validation flags
	 * changed from Approval to non-approval, Evidence to non-evidence and
	 * Evidence flag moved from child to parent
	 * 
	 * @param aoMyBatisSession to connect to database
	 * @param aoHashMap has taxonomy item element id's
	 * @return List loTaxonomyList list of taxonomyTree bean from DB
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public List<TaxonomyTree> executeTaxonomyBatchAndReCache(SqlSession aoMyBatisSession, Map aoHashMap)
			throws ApplicationException
	{
		List<TaxonomyTree> loTaxonomyList = null;
		TaxonomyTree loTaxonomyTree = new TaxonomyTree();
		List<String> loEvidenceToNonEvidenceList = new ArrayList<String>();
		List<String> loApprovalToNonApprovalList = new ArrayList<String>();
		String lsUserId = (String) aoHashMap.get("userId");
		String lsOrgId = (String) aoHashMap.get("orgId");
		Iterator<TaxonomyTree> loIterator;
		Iterator<String> loEvidenceToNonEvidenceItr;
		List<String> loElementIdList = new ArrayList<String>();

		try
		{
			loTaxonomyList = (List<TaxonomyTree>) DAOUtil.masterDAO(aoMyBatisSession, null,
					"com.nyc.hhs.service.db.services.application.TaxonomyBatchMapper", "executeTaxonomyBtch", null);

			if (null != loTaxonomyList)
			{
				loIterator = loTaxonomyList.iterator();
				// Segregating the List received into six list based upon state
				// change from (0 to 1, 1 to 0) for each flag
				while (loIterator.hasNext())
				{
					loTaxonomyTree = loIterator.next();
					if ((loTaxonomyTree.getMsTaxTranRecEvent().equalsIgnoreCase("Evidence"))
							&& (loTaxonomyTree.getMsTaxTranRecStatus().equalsIgnoreCase("1")))
					{
						loEvidenceToNonEvidenceList.add(loTaxonomyTree.getMsElementid());
					}
					else if ((loTaxonomyTree.getMsTaxTranRecEvent().equalsIgnoreCase("Approval"))
							&& (loTaxonomyTree.getMsTaxTranRecStatus().equalsIgnoreCase("1")))
					{
						loApprovalToNonApprovalList.add(loTaxonomyTree.getMsElementid());
					}
				}
			}
			loEvidenceToNonEvidenceItr = loEvidenceToNonEvidenceList.iterator();

			while (loEvidenceToNonEvidenceItr.hasNext())
			{
				loElementIdList.add(loEvidenceToNonEvidenceItr.next());
			}
			// method called for evidence flag check
			evidenceFlagCheck(aoMyBatisSession, loElementIdList, lsUserId, lsOrgId);

			// update superseding status table for taxonomy items where approval
			// flag changed to non-approval
			if (!loApprovalToNonApprovalList.isEmpty())
			{
				String lsEvent = "approvalToNonApproval";
				Iterator<String> loApprvlToNonApprvlElmntIdItr = loApprovalToNonApprovalList.iterator();
				while (loApprvlToNonApprvlElmntIdItr.hasNext())
				{
					String lsElementId = (String) loApprvlToNonApprvlElmntIdItr.next();
					loTaxonomyTree.setMsModifyBy(lsUserId);
					DAOUtil.masterDAO(aoMyBatisSession, loTaxonomyTree,
							"com.nyc.hhs.service.db.services.application.TaxonomyBatchMapper",
							"updateServiceAppForApprovalCheck", "com.nyc.hhs.model.TaxonomyTree");

					// insert data into superseding table for service
					// application id's if they do not exist in superseding
					// table itself
					// if (liRowUpdateCount < 1)
					// {
					insertIntoSuperSedingStatus(aoMyBatisSession, lsElementId, lsEvent, lsUserId, lsOrgId);
					// }
				}
			}
		}
		catch (ApplicationException loAppEx)
		{
			setMoState("Transaction failed ::: Service TaxonomyMaintenanceBatchService in method executeTaxonomyBatchAndReCache - while "
					+ "getting list of taxonomy items for which evidence changed to non-evidence and approval flag changed to non-approval\n");
			LOG_OBJECT.Error(
					"Error occured while getting list of taxonomy items for which evidence changed to non-evidence and approval flag "
							+ "changed to non-approval ", loAppEx);
			throw new ApplicationException("Error occured while getting list of taxonomy items", loAppEx);
		}
		setMoState("Transaction successfully :::  Service TaxonomyMaintenanceBatchService in method executeTaxonomyBatchAndReCache. "
				+ "Successfully got list of taxonomy items getting list of taxonomy items for which evidence changed to non-evidence "
				+ "and approval flag changed to non-approval \n");
		return loTaxonomyList;
	}

	/**
	 * This method update status to Deactivated in Superseding Status table for
	 * those items whose evidence flag changed from evidence to non-evidence
	 * 
	 * @param aoMyBatisSession to connect to database
	 * @param aoHashMap containing taxonomy item element id's that are changed
	 *            from evidence to non evidence
	 * @return Integer liRowUpdateCount update row count
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes" })
	public Integer updateServiceAppForEvidenceCheck(SqlSession aoMyBatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		TaxonomyTree loTaxonomyTree = new TaxonomyTree();
		int liRowUpdateCount = 0;
		try
		{
			HashMap loParentIdMap = (HashMap) aoHashMap.get("aoElementIdMap");
			String loEvedneToNonEvedneId = (String) loParentIdMap.get("evidenceToNonEvidenceId");
			loTaxonomyTree.setMsElementid(loEvedneToNonEvedneId);

			liRowUpdateCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loTaxonomyTree,
					"com.nyc.hhs.service.db.services.application.TaxonomyBatchMapper",
					"updateServiceAppForEvidenceCheck", "com.nyc.hhs.model.TaxonomyTree");
		}
		catch (ApplicationException loAppEx)
		{
			setMoState("Transaction failed ::: Service TaxonomyMaintenanceBatchService in method updateServiceAppForEvidenceCheck "
					+ "while updating Service Application Table for element ids having evidence flag changed to non evidence \n");
			LOG_OBJECT
					.Error("Error occured while updating Service Application Table for element ids having evidence flag changed to non evidence",
							loAppEx);
			throw new ApplicationException(
					"Error occured while updating Service Application Table for element ids having evidence flag changed to non evidence",
					loAppEx);
		}
		setMoState("Transaction successfully ::: Service TaxonomyMaintenanceBatchService in method updateServiceAppForEvidenceCheck  Service "
				+ "Application table updated successfully for element ids having evidence flag changed to non evidence \n");
		return liRowUpdateCount;
	}

	/**
	 * This method update status to Deactivated in Superseding_Status table for
	 * those items whose approval flag changed from approval to non-approval
	 * 
	 * @param aoMyBatisSession to connect to database
	 * @param aoHashMap containing taxonomy item element id's that are changed
	 *            from approval to non approval
	 * @return Integer liRowUpdateCount update row count
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes" })
	public Integer updateServiceAppForApprovalCheck(SqlSession aoMyBatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		TaxonomyTree loTaxonomyTree = new TaxonomyTree();
		int liRowUpdateCount = 0;
		try
		{
			HashMap loParentIdMap = (HashMap) aoHashMap.get("aoElementIdMap");
			String loAppToNonAppId = (String) loParentIdMap.get("approvalToNonApprovalId");
			loTaxonomyTree.setMsElementid(loAppToNonAppId);

			liRowUpdateCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loTaxonomyTree,
					"com.nyc.hhs.service.db.services.application.TaxonomyBatchMapper",
					"updateServiceAppForApprovalCheck", "com.nyc.hhs.model.TaxonomyTree");
		}
		catch (ApplicationException loAppEx)
		{
			setMoState("Transaction failed ::: Service TaxonomyMaintenanceBatchService in method updateServiceAppForApprovalCheck "
					+ "while updating Service Application Table for element ids having approval flag changed to non approval \n");
			LOG_OBJECT
					.Error("Error occured while updating Service Application Table for element ids having approval flag changed to non approval",
							loAppEx);
			throw new ApplicationException(
					"Error occured while updating Service Application Table for element ids having approval flag changed to non approval",
					loAppEx);
		}
		setMoState("Transaction successfully :::  Service TaxonomyMaintenanceBatchService in method updateServiceAppForApprovalCheck Service "
				+ "Application table updated successfully for element ids having approval flag changed to non approval \n");
		return liRowUpdateCount;
	}

	/**
	 * This method update status to Deactivated in Superseding_Status table for
	 * those items where evidence moved from child to parent
	 * 
	 * @param aoMyBatisSession to connect to database
	 * @param aoHashMap containing taxonomy child item element id's
	 * @return Integer liRowUpdateCount update row count
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes" })
	public Integer updateForEvidenceMovedToParentCheck(SqlSession aoMyBatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		TaxonomyTree loTaxonomyTree = new TaxonomyTree();
		int liRowUpdateCount = 0;
		try
		{
			HashMap loParentIdMap = (HashMap) aoHashMap.get("aoElementIdMap");
			String loChildElementId = (String) loParentIdMap.get("childElementId");
			loTaxonomyTree.setMsElementid(loChildElementId);

			liRowUpdateCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loTaxonomyTree,
					"com.nyc.hhs.service.db.services.application.TaxonomyBatchMapper",
					"updateForEvidenceMovedToParentCheck", "com.nyc.hhs.model.TaxonomyTree");
		}
		catch (ApplicationException loAppEx)
		{
			setMoState("Transaction failed ::: Service TaxonomyMaintenanceBatchService in method updateForEvidenceMovedToParentCheck while "
					+ "updating Service Application Table for element ids whose evidence flag moved from child to parent \n");
			LOG_OBJECT
					.Error("Error occured while updating Service Application Table for element ids whose evidence flag moved from child to parent",
							loAppEx);
			throw new ApplicationException(
					"Error occured while updating Service Application Table for element ids whose evidence flag "
							+ "moved from child to parent", loAppEx);
		}
		setMoState("Transaction successfully :::  Service TaxonomyMaintenanceBatchService in method updateForEvidenceMovedToParentCheck  Service "
				+ "Application table updated successfully for element ids whose evidence flag moved from child to parent \n");
		return liRowUpdateCount;
	}

	/**
	 * This method gets data from Service Application table where evidence moved
	 * from child to parent
	 * 
	 * @param aoMyBatisSession to connect to database
	 * @param aoHashMap map containing taxonomy child item id list
	 * @return Map loAppSummaryBeanMap child element id and taxonomy tree bean
	 *         map
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public Map<String, TaxonomyServiceBean> getDataFromServiceApplication(SqlSession aoMyBatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		TaxonomyTree loTaxonomyTree = new TaxonomyTree();
		TaxonomyServiceBean loTaxonomyServiceBean = null;
		Map<String, TaxonomyServiceBean> loAppSummaryBeanMap = new HashMap<String, TaxonomyServiceBean>();
		try
		{
			HashMap loParentIdMap = (HashMap) aoHashMap.get("aoElementIdMap");
			List loChildElementIdLst = (List) loParentIdMap.get("childElementIdLst");

			Set loChildElementIdSet = new HashSet(loChildElementIdLst);
			loChildElementIdLst = new ArrayList(loChildElementIdSet);
			Iterator<String> loTaxonomyTreeListItr = loChildElementIdLst.iterator();

			while (loTaxonomyTreeListItr.hasNext())
			{
				String lsElementId = loTaxonomyTreeListItr.next();
				loTaxonomyTree.setMsElementid(lsElementId);

				loTaxonomyServiceBean = (TaxonomyServiceBean) DAOUtil.masterDAO(aoMyBatisSession, loTaxonomyTree,
						"com.nyc.hhs.service.db.services.application.TaxonomyBatchMapper",
						"getDataFromServiceApplication", "com.nyc.hhs.model.TaxonomyTree");
				if (loTaxonomyServiceBean != null)
				{
					loAppSummaryBeanMap.put(lsElementId, loTaxonomyServiceBean);
				}
			}
		}
		catch (ApplicationException loAppEx)
		{
			setMoState("Transaction failed ::: Service TaxonomyMaintenanceBatchService in method getDataFromServiceApplication "
					+ "while updating Service Application Table for element ids whose evidence flag moved from child to parent \n");
			LOG_OBJECT
					.Error("Error occured while updating Service Application Table for element ids whose evidence flag moved from child to parent",
							loAppEx);
			throw new ApplicationException(
					"Error occured while updating Service Application Table for element ids whose evidence flag moved from "
							+ "child to parent", loAppEx);
		}
		setMoState("Transaction successfully :::  Service TaxonomyMaintenanceBatchService in method getDataFromServiceApplication Service "
				+ "Application table updated successfully for element ids whose evidence flag moved from child to parent \n");
		return loAppSummaryBeanMap;
	}

	/**
	 * This method insert data into Service Application table where evidence
	 * moved from child to parent
	 * 
	 * @param aoMyBatisSession to connect to database
	 * @param aoHashMap contains element id map
	 * @throws ApplicationException
	 */
	public void insertIntoServiceApplication(SqlSession aoMyBatisSession,
			Map<String, TaxonomyServiceBean> aoParentIdMap, String asUserId) throws ApplicationException
	{
		String lsServiceElementId = "";
		TaxonomyServiceBean loTaxonomyServiceBean;
		long llTimeStamp = System.currentTimeMillis();
		Integer loCounter = 1;
		try
		{
			loTaxonomyServiceBean = null;
			for (Map.Entry<String, TaxonomyServiceBean> loEntry : aoParentIdMap.entrySet())
			{
				lsServiceElementId = (String) loEntry.getKey();
				loTaxonomyServiceBean = (TaxonomyServiceBean) loEntry.getValue();
				if (loTaxonomyServiceBean != null)
				{
					String lsServiceAppId = loTaxonomyServiceBean.getServiceApplicationId();
					TaxonomyServiceBean loTaxSrvcBeanForOldAppServcId = (TaxonomyServiceBean) DAOUtil.masterDAO(
							aoMyBatisSession, lsServiceAppId,
							"com.nyc.hhs.service.db.services.application.TaxonomyBatchMapper",
							"getOldServiceApplicationId", "java.lang.String");

					if (loTaxSrvcBeanForOldAppServcId != null)
					{
						loTaxonomyServiceBean.setOldServiceApplicationId(loTaxSrvcBeanForOldAppServcId
								.getOldServiceApplicationId());
					}
					else
					{
						loTaxonomyServiceBean.setOldServiceApplicationId(loTaxonomyServiceBean
								.getServiceApplicationId());
					}
					loTaxonomyServiceBean.setServiceElementId(lsServiceElementId);
					loTaxonomyServiceBean.setServiceApplicationId("sr_" + llTimeStamp + loCounter);
					loTaxonomyServiceBean.setServiceStatus("Approved");
					loTaxonomyServiceBean.setApplicationId(" ");
					loTaxonomyServiceBean.setExpirationDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
					loTaxonomyServiceBean.setModifiedDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
					loTaxonomyServiceBean.setModifiedBy(asUserId);
					loTaxonomyServiceBean.setCreatedBy(asUserId);
					loTaxonomyServiceBean.setRemovedFlag("0");
					loTaxonomyServiceBean.setSubmittionDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
					loTaxonomyServiceBean.setStartDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
					loCounter++;
					DAOUtil.masterDAO(aoMyBatisSession, loTaxonomyServiceBean,
							"com.nyc.hhs.service.db.services.application.TaxonomyBatchMapper",
							"insertIntoServiceApplication", "com.nyc.hhs.model.TaxonomyServiceBean");
				}
			}
		}
		catch (ApplicationException loAppEx)
		{
			setMoState("Transaction failed ::: Service TaxonomyMaintenanceBatchService in method insertIntoServiceApplication "
					+ "while inserting data into service application  Table \n");
			LOG_OBJECT.Error("Error occured while while inserting data into service application Table", loAppEx);
			throw new ApplicationException("Error occured while while inserting data into service application Table",
					loAppEx);
		}
		setMoState("Transaction successfully ::: Service TaxonomyMaintenanceBatchService in method insertIntoServiceApplication "
				+ "Data in service application table inserted successfully for element ids whose evidence flag moved from child to parent \n");
	}

	/**
	 * This method insert data into Superseding status table where evidence
	 * moved from child to parent
	 * 
	 * @param aoMyBatisSession to connect to database
	 * @param aoHashMap map containing superseding details
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void insertIntoSupersedingStatus(SqlSession aoMyBatisSession, HashMap aoHashMap) throws ApplicationException
	{
		try
		{
			Map<String, Object> loSupersedingMap = null;
			loSupersedingMap = (Map<String, Object>) aoHashMap.get("aoSupersedingMap");
			DAOUtil.masterDAO(aoMyBatisSession, loSupersedingMap,
					"com.nyc.hhs.service.db.services.application.TaxonomyBatchMapper", "insertIntoSupersedingStatus",
					"java.util.Map");
		}
		catch (ApplicationException loAppEx)
		{
			setMoState("Transaction failed ::: Service TaxonomyMaintenanceBatchService in method insertIntoSupersedingStatus "
					+ "while inserting data into Superseding status Table \n");
			LOG_OBJECT.Error("Error occured while while inserting data into Superseding status Table", loAppEx);
			throw new ApplicationException("Error occured while while inserting data into Superseding status Table",
					loAppEx);
		}
		setMoState("Transaction successfully ::: Service TaxonomyMaintenanceBatchService in method insertIntoSupersedingStatus "
				+ "Data in Superseding status table inserted successfully for element ids where evidence flag moved from child to parent \n");
	}

	/**
	 * This method get service application id from service application table
	 * where evidence moved from child to parent
	 * 
	 * @param aoMyBatisSession to connect to database
	 * @param aoHashMap element id map
	 * @return TaxonomyServiceBean loTaxonomyServiceBean contains service
	 *         application id based on element id
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public TaxonomyServiceBean getServiceApplicationID(SqlSession aoMyBatisSession, HashMap aoElementIdMap)
			throws ApplicationException
	{
		TaxonomyServiceBean loTaxonomyServiceBean = null;
		try
		{
			String lsElementId = (String) aoElementIdMap.get("aoElementIdMap");
			loTaxonomyServiceBean = (TaxonomyServiceBean) DAOUtil.masterDAO(aoMyBatisSession, lsElementId,
					"com.nyc.hhs.service.db.services.application.TaxonomyBatchMapper", "getServiceApplicationID",
					"java.lang.String");
		}
		catch (ApplicationException loAppEx)
		{
			setMoState("Transaction failed ::: Service TaxonomyMaintenanceBatchService in method getServiceApplicationID while fetching "
					+ "service application id from service application table  \n");
			LOG_OBJECT.Error("Error occured while fetching  service application id from service application table",
					loAppEx);
			throw new ApplicationException(
					"Error occured while fetching  service application id from service application table", loAppEx);
		}
		setMoState("Transaction successfully ::: Service TaxonomyMaintenanceBatchService in method getServiceApplicationID  Service "
				+ "application id successfully fetched from service application table \n");
		return loTaxonomyServiceBean;
	}

	/**
	 * This method get old service application id from service application table
	 * where evidence moved from child to parent
	 * 
	 * @param aoMyBatisSession to connect to database
	 * @param aoHashMap service and application id map
	 * @return TaxonomyServiceBean loTaxonomyServiceBean old service application
	 *         id
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public TaxonomyServiceBean getOldServiceApplicationId(SqlSession aoMyBatisSession, HashMap aoElementIdMap)
			throws ApplicationException
	{
		TaxonomyServiceBean loTaxonomyServiceBean = null;
		try
		{
			String lsServiceApplicationId = (String) aoElementIdMap.get("aoServiceAppIdMap");
			loTaxonomyServiceBean = (TaxonomyServiceBean) DAOUtil.masterDAO(aoMyBatisSession, lsServiceApplicationId,
					"com.nyc.hhs.service.db.services.application.TaxonomyBatchMapper", "getOldServiceApplicationId",
					"java.lang.String");
		}
		catch (ApplicationException loAppEx)
		{
			setMoState("Transaction failed ::: Service TaxonomyMaintenanceBatchService in method getOldServiceApplicationId while fetching "
					+ "old service application id from service application table  \n");
			LOG_OBJECT.Error("Error occured while fetching  old service application id from service application table",
					loAppEx);
			throw new ApplicationException(
					"Error occured while fetching  old service application id from service application table", loAppEx);
		}
		setMoState("Transaction successfully ::: Service TaxonomyMaintenanceBatchService in method getOldServiceApplicationId  Service "
				+ "application id successfully fetched from service application table \n");

		return loTaxonomyServiceBean;
	}

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	/**
	 * This method performs Evidence Flag Check and update Superseding table entries accordingly
	 * 
	 * @param aoElementIdList is the list containing element Ids of taxonomy items that are changed
	 *        from approval to non approval, evidence to non evidence and active to inactive.
	 * @throws ApplicationException
	 */
	private void evidenceFlagCheck(SqlSession aoMyBatisSession, List<String> aoElementIdList, String asUserId,
			String asOrgId) throws ApplicationException
	{
		Channel loChannel;
		Map<String, List> loElementIdToFindParentMap;
		List<String> loParentElementIdList = new ArrayList<String>();
		List<String> loChildElementIdList = new ArrayList<String>();
		TaxonomyTree loTaxonomyTree = new TaxonomyTree();
		List<TaxonomyTree> loTaxonomyList = new ArrayList<TaxonomyTree>();
		Map<String, String> loParentChildIdMap = new HashMap<String, String>();
		String lsElementNameFromCache = "";
		String lsOldElementNameAssumed = "zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz";
		String lsOldElementId = "";
		String lsParentCheck = "";
		Map<String, String> loElementIdNameMap = new HashMap<String, String>();
		/*
		 * Update Service Application Table for Element Id whose Evidence
		 * changed to non evidence
		 */
		try
		{
			if (!aoElementIdList.isEmpty())
			{
				loChannel = new Channel();
				String lsEvent = "evidenceToNonEvidence";
				Iterator<String> loElementIdItr = aoElementIdList.iterator();
				while (loElementIdItr.hasNext())
				{
					String loEvedneToNonEvedneId = loElementIdItr.next();
					loTaxonomyTree.setMsElementid(loEvedneToNonEvedneId);
					loTaxonomyTree.setMsModifyBy(asUserId);
					DAOUtil.masterDAO(aoMyBatisSession, loTaxonomyTree,
							"com.nyc.hhs.service.db.services.application.TaxonomyBatchMapper",
							"updateServiceAppForEvidenceCheck", "com.nyc.hhs.model.TaxonomyTree");
					// if (liRowUpdateCount < 1)
					// {
					insertIntoSuperSedingStatus(aoMyBatisSession, loEvedneToNonEvedneId, lsEvent, asUserId, asOrgId);
					// }
				}
				// Update Service Application Table for Element Id whose
				// Evidence changed to non evidence
				// finding parent element id whose evidence flag is changed
				loElementIdToFindParentMap = new HashMap<String, List>();
				loElementIdToFindParentMap.put("parentElementIdList", aoElementIdList);
				loChannel.setData("aoElementIdMap", loElementIdToFindParentMap);
				// Below transaction provides the list of parent element id's
				// where evidence flag moved from child to parent
				loTaxonomyTree.setMsParentElementIdList(aoElementIdList);
			
				loTaxonomyList = (List<TaxonomyTree>) DAOUtil.masterDAO(aoMyBatisSession, loTaxonomyTree,
						"com.nyc.hhs.service.db.services.application.TaxonomyBatchMapper", "parentEvidenceFlagCheck",
						"com.nyc.hhs.model.TaxonomyTree");
				org.jdom.Document loTaxonomyDom = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
						ApplicationConstants.TAXONOMY_ELEMENT);
				for (TaxonomyTree loPaTaxonomyTree : loTaxonomyList)
				{
				
					loParentElementIdList.add(loPaTaxonomyTree.getMsElementid());
					loChildElementIdList.add(loPaTaxonomyTree.getMsLeaf());
					String lsLeafNode = loPaTaxonomyTree.getMsLeaf();
					String lsParentNode = loPaTaxonomyTree.getMsElementid();
					List loTaxonomyServiceBeanLst = (List) DAOUtil.masterDAO(aoMyBatisSession, lsLeafNode,
							"com.nyc.hhs.service.db.services.application.TaxonomyBatchMapper",
							"getDataFromServiceApplication", "java.lang.String");
					// for defect 1322 - alphabetically first child entry in
					// service application
					lsElementNameFromCache = BusinessApplicationUtil.getTaxonomyName(lsLeafNode, loTaxonomyDom);
					if (null != lsElementNameFromCache)
					{
						if (lsParentNode.equalsIgnoreCase(lsParentCheck))
						{
							loElementIdNameMap.put(lsLeafNode, lsElementNameFromCache.trim());
						}
						else
						{
							loElementIdNameMap = new HashMap<String, String>();
							lsOldElementNameAssumed = "zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz";
							loElementIdNameMap.put(lsLeafNode, lsElementNameFromCache.trim());
						}
					}
					String lsSortedElemetId = loElementIdNameMap.get(lsLeafNode).compareTo(lsOldElementNameAssumed) < 0 ? lsLeafNode
							: lsOldElementId;
					lsOldElementNameAssumed = loElementIdNameMap.get(lsSortedElemetId);
					lsOldElementId = lsSortedElemetId;
					lsParentCheck = lsParentNode;
					if (!loTaxonomyServiceBeanLst.isEmpty())
					{
						loParentChildIdMap.put(loPaTaxonomyTree.getMsElementid(), lsSortedElemetId);
					}
				}
				// method called to process those taxonomy items where evidence
				// flag is moved from child to parent
				evidenceMvdFrmChldToPrntProcess(aoMyBatisSession, loParentElementIdList, loChildElementIdList,
						loParentChildIdMap, asUserId, asOrgId);
			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Error occurred while checking evidence flag ", loAppEx);
			setMoState("Transaction Failed:: TaxonomyMaintenanceBatchService: evidenceFlagCheck method -Exception occured Error occurred "
					+ "while checking evidence flag for taxonomy items:" + loTaxonomyList + "/n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: TaxonomyMaintenanceBatchService: evidenceFlagCheck method evidence flag successfully checked "
				+ "for taxonomy items:" + loTaxonomyList + "/n");
	}

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	/**
	 * This method update Superseding and service application table for taxonomy items where 
	 * Evidence Flag moved from Child to Parent and insert data into Service Application table 
	 * for parent Id's
	 * 
	 * @param loParentElementIdList is the parent list containing element Ids of taxonomy items where evidence 
	 *        is moved from child to parent.
	 * @param loChildElementIdList is the child list containing element Ids of taxonomy items where evidence
	 *        is moved from child to parent.
	 * @param aoParentChildIdMap is the Map containing parent and child element Id map.
	 * @throws ApplicationException
	 */
	private void evidenceMvdFrmChldToPrntProcess(SqlSession aoMyBatisSession, List<String> aoParentElementIdList,
			List<String> aoChildElementIdList, Map<String, String> aoParentChildIdMap, String asUserId, String asOrgId)
			throws ApplicationException
	{
		Channel loChannel;
		Map<String, List> loElementIdMap;
		Map<String, String> loElementIdEvidenceMap;
		Map<String, TaxonomyServiceBean> loParentIdBeanMap = new HashMap<String, TaxonomyServiceBean>();
		TaxonomyServiceBean loTaxonomyServiceBean = new TaxonomyServiceBean();
		TaxonomyTree loTaxonomyTree = new TaxonomyTree();
		Map<String, TaxonomyServiceBean> loAppSummaryBeanMap = new HashMap<String, TaxonomyServiceBean>();
		int liRowUpdateCount = 0;
		try
		{
			/*
			 * updating superseding table for taxonomy items where Evidence Flag
			 * moved from Child to Parent
			 */
			if (!aoChildElementIdList.isEmpty())
			{
				Set loChildElementIdSet = new HashSet(aoChildElementIdList);
				List loChildElementIdLst = new ArrayList(loChildElementIdSet);
				loChannel = new Channel();
				String lsEvent = "EvidenceMovedFromChildToParent";
				loElementIdMap = new HashMap<String, List>();
				loElementIdMap.put("childElementIdLst", loChildElementIdLst);
				loChannel.setData("aoElementIdMap", loElementIdMap);
				// Below transaction fetch service application id from service
				// application table corresponding to service element id and
				// return map of Element id and taxonomy Service bean
				loChildElementIdLst = new ArrayList(loChildElementIdSet);
				Iterator<String> loTaxonomyTreeListItr = loChildElementIdLst.iterator();
				List<TaxonomyServiceBean> loTaxonomyServiceBeanLst = null;
				while (loTaxonomyTreeListItr.hasNext())
				{
					String lsElementId = loTaxonomyTreeListItr.next();
					loTaxonomyTree.setMsElementid(lsElementId);
					loTaxonomyServiceBeanLst = (List) DAOUtil.masterDAO(aoMyBatisSession, loTaxonomyTree,
							"com.nyc.hhs.service.db.services.application.TaxonomyBatchMapper",
							"getDataFromServiceApplication", "com.nyc.hhs.model.TaxonomyTree");
					Iterator<TaxonomyServiceBean> loTaxonomyServiceBeanItr = loTaxonomyServiceBeanLst.iterator();
					while (loTaxonomyServiceBeanItr.hasNext())
					{
						if (loTaxonomyServiceBean != null)
						{
							loTaxonomyServiceBean = loTaxonomyServiceBeanItr.next();
							loAppSummaryBeanMap.put(lsElementId, loTaxonomyServiceBean);
						}
					}
				}
				loChannel = new Channel();
				loElementIdEvidenceMap = new HashMap<String, String>();
				Iterator loChildElementIdItr = aoChildElementIdList.iterator();
				while (loChildElementIdItr.hasNext())
				{
					String lsElementId = (String) loChildElementIdItr.next();
					loElementIdEvidenceMap.put("childElementId", lsElementId);
					loChannel.setData("aoElementIdMap", loElementIdEvidenceMap);
					/*
					 * Below transaction updates superseding table for taxonomy
					 * items where Evidence Flag moved from Child to Parent
					 */
					String loChildElementId = (String) loElementIdEvidenceMap.get("childElementId");
					loTaxonomyTree.setMsElementid(loChildElementId);
					loTaxonomyTree.setMsModifyBy(asUserId);
					liRowUpdateCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loTaxonomyTree,
							"com.nyc.hhs.service.db.services.application.TaxonomyBatchMapper",
							"updateForEvidenceMovedToParentCheck", "com.nyc.hhs.model.TaxonomyTree");
					insertIntoSuperSedingStatus(aoMyBatisSession, lsElementId, lsEvent, asUserId, asOrgId);
				}
			}
			Iterator<String> loParentElementIdListItr = aoParentElementIdList.iterator();
			/*
			 * updating service application for element id of child whose
			 * evidence flag moved to parent
			 */
			// inserting into service application for parent element id
			if (!aoParentElementIdList.isEmpty())
			{
				while (loParentElementIdListItr.hasNext())
				{
					String lsParentId = loParentElementIdListItr.next();
					loParentIdBeanMap.put(lsParentId, loAppSummaryBeanMap.get(aoParentChildIdMap.get(lsParentId)));
				}
				// Below transaction insert data into service application for
				// parent id's evidence Flag moved from Child to Parent for
				// Approved services only
				
				loTaxonomyTree.setMsParentElementIdList(aoChildElementIdList);
				liRowUpdateCount = 0;
				loTaxonomyTree = (TaxonomyTree) DAOUtil.masterDAO(aoMyBatisSession, loTaxonomyTree,
						"com.nyc.hhs.service.db.services.application.TaxonomyBatchMapper", "getApprovedServiceCount",
						"com.nyc.hhs.model.TaxonomyTree");
				if (loTaxonomyTree != null)
				{
					liRowUpdateCount = Integer.valueOf(loTaxonomyTree.getMsCount());
				}
				if (liRowUpdateCount > 0)
				{
					insertIntoServiceApplication(aoMyBatisSession, loParentIdBeanMap, asUserId);
				}
			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error(
					"Error occurred while processing taxonomy items for which evidence is moved from child to parent ",
					loAppEx);
			setMoState("Transaction Failed:: TaxonomyMaintenanceBatchService: evidenceMvdFrmChldToPrntProcess method -Exception occured "
					+ "Error occured for taxonomy items for which evidence is moved from child to parent /n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: TaxonomyMaintenanceBatchService: evidenceMvdFrmChldToPrntProcess method -succesfully checked "
				+ "taxonomy items for which evidence is moved from child to parent /n");
	}

	/**
	 * This method insert data into Superseding table corresponding to Service
	 * application id
	 * 
	 * @param aoChannel is for the transaction execution
	 * @param aoElementIdList is the list containing element Ids of taxonomy
	 *            items that are changed from approval to non approval, evidence
	 *            to non evidence and active to inactive.
	 * @param asEvent decides active approval or evidence validation
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	private void insertIntoSuperSedingStatus(SqlSession aoMyBatisSession, String asElementId, String asEvent,
			String asUserId, String asOrgId) throws ApplicationException
	{
		TaxonomyServiceBean loTaxonomyServiceBean = new TaxonomyServiceBean();
		List<TaxonomyServiceBean> loTaxonomyServiceBeanList = null;
		Map<String, Object> loSupersedingMap = new HashMap<String, Object>();
		boolean lbSkip = true;
		
		try
		{
			loSupersedingMap.put("entityType", "Service Application");
			if (asEvent.equalsIgnoreCase("approvalToNonApproval")
					|| asEvent.equalsIgnoreCase("EvidenceMovedFromChildToParent")
					|| asEvent.equalsIgnoreCase("evidenceToNonEvidence"))
			{
				// Below transaction fetches service application id
				// corresponding to service element id
				loTaxonomyServiceBeanList = (List<TaxonomyServiceBean>) DAOUtil.masterDAO(aoMyBatisSession,
						asElementId, "com.nyc.hhs.service.db.services.application.TaxonomyBatchMapper",
						"getServiceApplicationID", "java.lang.String");
				if (null != loTaxonomyServiceBeanList)
				{
					Iterator<TaxonomyServiceBean> loTaxonomyServiceBeanItr = loTaxonomyServiceBeanList.iterator();
					while (loTaxonomyServiceBeanItr.hasNext())
					{
						if (loTaxonomyServiceBean != null)
						{
							loTaxonomyServiceBean = loTaxonomyServiceBeanItr.next();
							loSupersedingMap.put("entityId", loTaxonomyServiceBean.getServiceApplicationId());
							if (asEvent.equalsIgnoreCase("approvalToNonApproval"))
							{
								TaxonomyTree loTaxonomyTree = (TaxonomyTree) DAOUtil.masterDAO(aoMyBatisSession,
										loTaxonomyServiceBean.getServiceApplicationId(),
										"com.nyc.hhs.service.db.services.application.TaxonomyBatchMapper",
										"checkEventInSuperseding", "java.lang.String");
								if (null != loTaxonomyTree && Integer.valueOf(loTaxonomyTree.getMsCount()) > 0)
								{
									lbSkip = false;
								}
								else
								{
									loSupersedingMap.put("event", "approvalToNonApproval");
									DAOUtil.masterDAO(aoMyBatisSession, asElementId,
											"com.nyc.hhs.service.db.services.application.TaxonomyBatchMapper",
											"deleteFromSupersedingStatus", "java.lang.String");
								}
							}
							else if (asEvent.equalsIgnoreCase("evidenceToNonEvidence"))
							{
								loSupersedingMap.put("event", "evidenceToNonEvidence");
							}
							else if (asEvent.equalsIgnoreCase("EvidenceMovedFromChildToParent"))
							{
								loSupersedingMap.put("event", "EvidenceMovedFromChildToParent");
								DAOUtil.masterDAO(aoMyBatisSession, asElementId,
										"com.nyc.hhs.service.db.services.application.TaxonomyBatchMapper",
										"deleteFromSupersedingStatus", "java.lang.String");
								
							}
							if (lbSkip)
							{
								loSupersedingMap.put("flag", "Y"); // for future
																	// use
								loSupersedingMap.put("status", TaxonomyMaintenanceBatchService.DEACTIVATED);
								loSupersedingMap.put("userId", asUserId); // session
								loSupersedingMap.put("orgId", loTaxonomyServiceBean.getOrganizationId()); // session
								//[Start]R9.3.0 qc 9638 Vuln 5: CWE 331 - Insufficient Entropy
								//loSupersedingMap.put("requestId", CommonUtil.getRandomString(6));
								//[End]R9.3.0 qc 9638 Vuln 5: CWE 331 - Insufficient Entropy
								// Below transaction inserts data into
								// Superseding table
								// corresponding to Service application id
								DAOUtil.masterDAO(aoMyBatisSession, loSupersedingMap,
										"com.nyc.hhs.service.db.services.application.TaxonomyBatchMapper",
										"insertIntoSupersedingStatus", "java.util.Map");
							}
						}
					}
				}
			}
		}
		catch (ApplicationException loAppErr)
		{
			LOG_OBJECT.Error("Error occurred while inserting SuperSeding Status ", loAppErr);
			setMoState("Transaction Failed:: TaxonomyMaintenanceBatchService: insertIntoSuperSedingStatus method -Exception"
					+ " occured Error occurred while inserting data into SuperSedingStatus table /n");
			throw loAppErr;
		}
		setMoState("Transaction Success:: TaxonomyMaintenanceBatchService: insertIntoSuperSedingStatus method successfully "
				+ "inserted data into SuperSedingStatus table/n");
	}
}
