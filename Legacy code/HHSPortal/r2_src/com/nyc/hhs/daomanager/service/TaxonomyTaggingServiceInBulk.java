package com.nyc.hhs.daomanager.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.TaxonomyModifiersBean;
import com.nyc.hhs.model.TaxonomyTaggingBean;
import com.nyc.hhs.util.DAOUtil;

/**
 * TaxonomyService: This service class used to perform operations for add/remove
 * and update Taxonomy items and also used for taxonomy maintenance.
 * Class Added in R4
 */
public class TaxonomyTaggingServiceInBulk extends ServiceState
{
	/**
	 * This is a log object used to log any exception into log file.
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(TaxonomyTaggingServiceInBulk.class);

	/**
	 * This method saves the TaxonomyTaggingDetailsInBulk.
	 * <ul>
	 * <li>1.Iterates through the aoProcurementIdBulkArray and aoServiceIdArray
	 * and set the data into loTaxonomyTaggingBean for saving into database</li>
	 * <li>2.If aoTaxonomyTaggingIdBulkArray is not empty. Get the next tagging
	 * id using query "getNextTaggingId" and insert the details in the database
	 * using query "insertTaxonomyTaggingDetails"</li>
	 * <li>3.If aoTaxonomyTaggingIdBulkArray is empty. Then the queries
	 * "deleteFromTaxonomyTagging", "deleteFromTaxonomyTaggingModifiers" and
	 * "updateTaxonomyTaggingDetails" will be executed.</li>
	 * <li>4.Iterating through the modifier list if the modifier is not empty.
	 * Then update taxonomy modifiers using query
	 * "updateTaxonomyModifierDetails"</li>
	 * <li>5.If there is not entry for the modifier in the database. Then insert
	 * using "insertTaxonomyModifierDetails"</li>
	 * </ul>
	 * 
	 * 
	 * @param aoMyBatisSession - my batis session
	 * @param aoServiceIdArray - service Ids
	 * @param aoModifierIdsArray - modifier Ids
	 * @param aoContractIdBulkArray - Contract Ids List
	 * @param aoProposalIdBulkArray - Proposal Ids List
	 * @param aoProcurementIdBulkArray - Procurement Ids List
	 * @param aoTaxonomyTaggingIdBulkArray Taxonomy Tagging Id Bulk Array
	 * @param asUserId - User Id
	 * @return - Flag value
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Boolean saveAllSelectedProposalsInbulk(SqlSession aoMyBatisSession, List<String> aoServiceIdArray,
			List<String> aoModifierIdsArray, List<String> aoContractIdBulkArray, List<String> aoProposalIdBulkArray,
			List<String> aoProcurementIdBulkArray, List<String> aoTaxonomyTaggingIdBulkArray, String asUserId)
			throws ApplicationException
	{
		try
		{
			int liUpdateRowCount = HHSConstants.INT_ZERO;
			String lsTaxTagId = null;
			TaxonomyTaggingBean loTaxonomyTaggingBean = new TaxonomyTaggingBean();
			TaxonomyModifiersBean loModifiersBean = new TaxonomyModifiersBean();
			// loop for iterating through the contract, proposal,procurement ids
			for (int liCount = 0; liCount < aoProcurementIdBulkArray.size(); liCount++)
			{
				// updates value in taxonomy tagging bean
				loTaxonomyTaggingBean.setContractId(aoContractIdBulkArray.get(liCount).replace(HHSConstants.NULL,
						HHSConstants.EMPTY_STRING));
				loTaxonomyTaggingBean.setProcurementId(aoProcurementIdBulkArray.get(liCount).replace(HHSConstants.NULL,
						HHSConstants.EMPTY_STRING));
				loTaxonomyTaggingBean.setProposalId(aoProposalIdBulkArray.get(liCount).replace(HHSConstants.NULL,
						HHSConstants.EMPTY_STRING));
				loTaxonomyTaggingBean.setCreatedByUserId(asUserId);
				loTaxonomyTaggingBean.setModifyByUserId(asUserId);
				loModifiersBean.setCreatedBy(asUserId);
				loModifiersBean.setModifyBy(asUserId);
				// loop for iterating through the service Ids and updating
				// Taxonomy tagging
				for (int liSecondCount = 0; liSecondCount < aoServiceIdArray.size(); liSecondCount++)
				{
					loTaxonomyTaggingBean.setElementId(aoServiceIdArray.get(liSecondCount));
					if (aoTaxonomyTaggingIdBulkArray.get(liSecondCount) != null
							&& aoTaxonomyTaggingIdBulkArray.get(liSecondCount).equalsIgnoreCase(HHSConstants.HYPHEN))
					{
						lsTaxTagId = (String) DAOUtil.masterDAO(aoMyBatisSession, null,
								HHSConstants.MAPPER_CLASS_TAXONOMY_TAGGING_MAPPER, HHSConstants.GET_NEXT_TAGGING_ID,
								null);
						loTaxonomyTaggingBean.setTaxonomyTaggingId(lsTaxTagId);
						DAOUtil.masterDAO(aoMyBatisSession, loTaxonomyTaggingBean,
								HHSConstants.MAPPER_CLASS_TAXONOMY_TAGGING_MAPPER,
								HHSConstants.INSERT_TAXONOMY_TAGGING_DETAILS,
								HHSConstants.COM_NYC_HHS_MODEL_TAXONOMY_TAGGING_BEAN);
					}
					else
					{
						lsTaxTagId = updateModifierData(aoMyBatisSession, aoTaxonomyTaggingIdBulkArray,
								loTaxonomyTaggingBean, liSecondCount);
					}
					String loModifiers[] = aoModifierIdsArray.get(liSecondCount).split(",");
					loModifiersBean.setTaxonomyTaggingId(lsTaxTagId);
					// loop for iterating through the modifier Ids and updating
					// Taxonomy modifiers
					for (int liEleCount = 0; liEleCount < loModifiers.length; liEleCount++)
					{
						liUpdateRowCount = updateModifiersId(aoMyBatisSession, liUpdateRowCount, loModifiersBean,
								loModifiers, liEleCount);
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
			LOG_OBJECT.Error("Exception occured while saving bulk taxonomy Data from Database", aoAppEx);
			setMoState("Transaction Failed:: TaxonomyTaggingService: saveAllSelectedProposalsInbulk method -Exception occured"
					+ "while saving bulk taxonomy" + "/n");
			throw aoAppEx;
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception occured while saving bulk taxonomy Data from Database", aoExp);
			setMoState("Transaction Failed:: TaxonomyTaggingService: saveAllSelectedProposalsInbulk method -Exception occured"
					+ "while saving bulk taxonomy" + "/n");
			throw new ApplicationException("Exception occured while saving bulk taxonomy Data from Database", aoExp);
		}
		return true;
	}

	/**
	 * This method get modifier data.
	 * <ul>
	 * <li> This method executes query <b>deleteFromTaxonomyTagging</b></li>
	 * <li> This method executes query <b>deleteFromTaxonomyTaggingModifiers</b></li>
	 * <li> This method executes query<b>updateTaxonomyTaggingDetails</b></li>
	 * </ul>
	 * @param aoMyBatisSession sql session as input
	 * @param aoTaxonomyTaggingIdBulkArray array as input
	 * @param loTaxonomyTaggingBean bean as input
	 * @param liSecondCount count as input
	 * @return lsTaxTagId return tax id
	 * @throws ApplicationException Exception in case a query fails
	 */
	private String updateModifierData(SqlSession aoMyBatisSession, List<String> aoTaxonomyTaggingIdBulkArray,
			TaxonomyTaggingBean loTaxonomyTaggingBean, int liSecondCount) throws ApplicationException
	{
		String lsTaxTagId = aoTaxonomyTaggingIdBulkArray.get(liSecondCount);
		loTaxonomyTaggingBean.setTaxonomyTaggingId(lsTaxTagId);
		DAOUtil.masterDAO(aoMyBatisSession, loTaxonomyTaggingBean, HHSConstants.MAPPER_CLASS_TAXONOMY_TAGGING_MAPPER,
				HHSConstants.DELETE_FROM_TAXONOMY_TAGGING, HHSConstants.COM_NYC_HHS_MODEL_TAXONOMY_TAGGING_BEAN);
		// update taxonomy tagging modifiers data(flag down)
		DAOUtil.masterDAO(aoMyBatisSession, loTaxonomyTaggingBean, HHSConstants.MAPPER_CLASS_TAXONOMY_TAGGING_MAPPER,
				HHSConstants.DELETE_FROM_TAXONOMY_TAGGING_MODIFIERS,
				HHSConstants.COM_NYC_HHS_MODEL_TAXONOMY_TAGGING_BEAN);
		// update taxonomy tagging data
		DAOUtil.masterDAO(aoMyBatisSession, loTaxonomyTaggingBean, HHSConstants.MAPPER_CLASS_TAXONOMY_TAGGING_MAPPER,
				HHSConstants.UPDATE_TAXONOMYTAGGING_DETAILS, HHSConstants.COM_NYC_HHS_MODEL_TAXONOMY_TAGGING_BEAN);
		return lsTaxTagId;
	}

	/**
	 * This method updates update Modifiers Id
	 * <ul>
	 * <li>This method executes query <b>updateTaxonomyModifierDetails</b></li>
	 * <li>This method executes query <b>insertTaxonomyModifierDetails</b></li>
	 * </ul>
	 * @param aoMyBatisSession sql session as input
	 * @param liUpdateRowCount count as input
	 * @param loModifiersBean bean as input
	 * @param loModifiers string array as input
	 * @param liEleCount element count
	 * @return lsModifierId as output
	 * @throws ApplicationException Exception in case a query fails
	 */
	private int updateModifiersId(SqlSession aoMyBatisSession, int liUpdateRowCount,
			TaxonomyModifiersBean loModifiersBean, String[] loModifiers, int liEleCount) throws ApplicationException
	{
		String lsModifierId = loModifiers[liEleCount];
		if (lsModifierId != null && !lsModifierId.equalsIgnoreCase(HHSConstants.NULL) && !lsModifierId.isEmpty())
		{
			loModifiersBean.setElementId(loModifiers[liEleCount]);
			liUpdateRowCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loModifiersBean,
					HHSConstants.MAPPER_CLASS_TAXONOMY_TAGGING_MAPPER, HHSConstants.UPDATE_TAXONOMY_MODIFIER_DETAIL,
					HHSConstants.COM_NYC_HHS_MODEL_TAXONOMY_MODIFIERS_BEAN);
			if (liUpdateRowCount <= HHSConstants.INT_ZERO)
			{
				DAOUtil.masterDAO(aoMyBatisSession, loModifiersBean, HHSConstants.MAPPER_CLASS_TAXONOMY_TAGGING_MAPPER,
						HHSConstants.INSERT_TAXONOMY_MODIFIER_DETAIL,
						HHSConstants.COM_NYC_HHS_MODEL_TAXONOMY_MODIFIERS_BEAN);
			}
		}
		return liUpdateRowCount;
	}

	/**
	 * This method deletes the TaxonomyTaggingDetailsInBulk. It iterates through
	 * the list and updates the active flag in the database into the database.
	 * <ul>
	 * <li>1.If asDeletedTaxonomyTaggingId is not empty. Then iterate through
	 * the Ids and call "deleteTaxonomyTags" to delete the tags</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession - my batis session
	 * @param asDeletedTaxonomyTaggingId - String containing the Taxonomy Ids to
	 *            be deleted
	 * @param asUserId - User Id
	 * @return - Flag value
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Boolean deleteTaxonomyTaggingDetailsInBulk(SqlSession aoMyBatisSession, String asDeletedTaxonomyTaggingId,
			String asUserId) throws ApplicationException
	{
		try
		{
			if (asDeletedTaxonomyTaggingId != null && !asDeletedTaxonomyTaggingId.isEmpty())
			{
				TaxonomyTaggingService loTaxonomyTaggingService = new TaxonomyTaggingService();
				TaxonomyTaggingBean loTaxonomyTaggingBean = new TaxonomyTaggingBean();
				String loDeletedTaxonomyTaggingId[] = asDeletedTaxonomyTaggingId.split(",");
				loTaxonomyTaggingBean.setModifyByUserId(asUserId);
				// loop for iterating through the taxonomy tag ids
				for (int liDelCount = 0; liDelCount < loDeletedTaxonomyTaggingId.length; liDelCount++)
				{
					if (loDeletedTaxonomyTaggingId[liDelCount] != null
							&& !loDeletedTaxonomyTaggingId[liDelCount].equalsIgnoreCase("-"))
					{
						loTaxonomyTaggingBean.setTaxonomyTaggingId(loDeletedTaxonomyTaggingId[liDelCount]);
						loTaxonomyTaggingService.deleteTaxonomyTags(aoMyBatisSession, loTaxonomyTaggingBean);
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
			LOG_OBJECT.Error("Exception occured while deleting bulk taxonomy Data from Database", aoAppEx);
			setMoState("Transaction Failed:: TaxonomyTaggingService: deleteTaxonomyTaggingDetailsInBulk method -Exception occured"
					+ "while saving bulk taxonomy" + "/n");
			throw aoAppEx;
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception occured while deleting bulk taxonomy Data from Database", aoExp);
			setMoState("Transaction Failed:: TaxonomyTaggingService: deleteTaxonomyTaggingDetailsInBulk method -Exception occured"
					+ "while saving bulk taxonomy" + "/n");
			throw new ApplicationException("Exception occured while deleting bulk taxonomy Data from Database", aoExp);
		}
		return true;
	}

	/**
	 * This method is used to remove selected taxonomy in bulk by iterating
	 * through the IDs provided and changing the update active flag.
	 * <ul>
	 * <li>1.Iterate through Ids and set all the contract, proposal, procurement
	 * Ids in a Map</li>
	 * <li>2.If tagging Id list is not empty. Then delete from taxonomy tagging
	 * and taxonomy modifier using "removeAllFromTaxonomyTaggingModifiersInBulk"
	 * and "removeAllFromTaxonomyTaggingInBulk" queries.</li>
	 * </ul>
	 * @param aoMyBatisSession - my batis session
	 * @param aoTaxonomyTaggingBean - tagging details bean to be deleted
	 * @return lbStatus - if deleted successfully
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings("unchecked")
	public boolean removeAllTaxonomyTaggingDetailsInBulk(SqlSession aoMyBatisSession,
			TaxonomyTaggingBean aoTaxonomyTaggingBean) throws ApplicationException
	{
		List<String> loContractId = aoTaxonomyTaggingBean.getContractIdList();
		List<String> loProposalId = aoTaxonomyTaggingBean.getProposalIdList();
		List<String> loProcurementId = aoTaxonomyTaggingBean.getProcurementIdList();

		Map loId = new HashMap();
		List<String> lsTaxonomyTaggingIds = null;
		boolean lbStatus = false;
		try
		{
			for (int liContractCount = 0; liContractCount < loContractId.size(); liContractCount++)
			{
				loId.put("contractId", loContractId.get(liContractCount));
				loId.put("proposalId", loProposalId.get(liContractCount));
				loId.put("procurementId", loProcurementId.get(liContractCount));
				lsTaxonomyTaggingIds = (List<String>) DAOUtil.masterDAO(aoMyBatisSession, loId,
						HHSConstants.MAPPER_CLASS_TAXONOMY_TAGGING_MAPPER, HHSConstants.TT_GETTAXONOMYTAGGINGIDS1,
						HHSConstants.JAVA_UTIL_MAP);
				lsTaxonomyTaggingIds.addAll(aoTaxonomyTaggingBean.getTaxonomyTaggingIdList());
				aoTaxonomyTaggingBean.setTaxonomyTaggingIdList(lsTaxonomyTaggingIds);

			}

			if (aoTaxonomyTaggingBean.getTaxonomyTaggingIdList() != null
					&& !aoTaxonomyTaggingBean.getTaxonomyTaggingIdList().isEmpty())
			{
				DAOUtil.masterDAO(aoMyBatisSession, aoTaxonomyTaggingBean,
						HHSConstants.MAPPER_CLASS_TAXONOMY_TAGGING_MAPPER,
						HHSConstants.REMOVE_ALL_FROM_TAXONOMY_TAGGING_MODIFIERS_IN_BULK,
						HHSConstants.COM_NYC_HHS_MODEL_TAXONOMY_TAGGING_BEAN);
				DAOUtil.masterDAO(aoMyBatisSession, aoTaxonomyTaggingBean,
						HHSConstants.MAPPER_CLASS_TAXONOMY_TAGGING_MAPPER,
						HHSConstants.REMOVE_ALL_FROM_TAXONOMY_TAGGING_IN_BULK,
						HHSConstants.COM_NYC_HHS_MODEL_TAXONOMY_TAGGING_BEAN);
			}
			lbStatus = true;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData(HHSConstants.TAXONOMY_TAGGING_ID, aoTaxonomyTaggingBean.getTaxonomyTaggingId());
			LOG_OBJECT.Error("Exception occured while retreiving complete Taxonomy Data from Database", aoAppEx);
			setMoState("Transaction Failed:: TaxonomyService: getTaxonomyMasterDB method -Exception occured while retrieving complete Taxonomy "
					+ "List from Database where delete status is N:"
					+ aoTaxonomyTaggingBean.getTaxonomyTaggingId()
					+ "/n");
			throw aoAppEx;
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception occured while retreiving complete Taxonomy Data from Database", aoExp);
			setMoState("Transaction Failed:: TaxonomyService: getTaxonomyMasterDB method -Exception occured while retrieving complete Taxonomy "
					+ "List from Database where delete status is N:"
					+ aoTaxonomyTaggingBean.getTaxonomyTaggingId()
					+ "/n");
			throw new ApplicationException("Exception occured while retreiving complete Taxonomy Data from Database",
					aoExp);
		}

		setMoState("Transaction Success:: TaxonomyService: getTaxonomyMasterDB method Complete Taxonomy Tree List retrieved successfully for "
				+ "delete status N:" + aoTaxonomyTaggingBean.getTaxonomyTaggingId() + "/n");
		return lbStatus;
	}
}