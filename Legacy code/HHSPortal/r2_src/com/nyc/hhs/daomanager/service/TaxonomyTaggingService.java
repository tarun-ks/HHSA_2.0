package com.nyc.hhs.daomanager.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.jdom.Document;
import org.jdom.Element;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.TaxonomyTaggingBean;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.XMLUtil;

/**
 * TaxonomyService: This service class used to perform operations for add/remove
 * and update Taxonomy items and also used for taxonomy maintenance.
 */
public class TaxonomyTaggingService extends ServiceState
{
	/**
	 * This is a log object used to log any exception into log file.
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(TaxonomyTaggingService.class);

	/**
	 * This method is getting ProcurementProposalDetails. It is initialising a
	 * bean as null then checking if proposal title and ContractProcurementTitle
	 * is not empty it is setting it into TaxonomyTaggingBean.then fetching
	 * procurement and proposal detail transaction returning a list
	 * ProcurementProposalList.
	 * @param aoMyBatisSession - My batis session
	 * @param aoTaxonomyTaggingBean - Taxonomy tagging bean
	 * @return - list of taxonomy tagging beans
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings("unchecked")
	public List<TaxonomyTaggingBean> getProcurementProposalDetails(SqlSession aoMyBatisSession,
			TaxonomyTaggingBean aoTaxonomyTaggingBean) throws ApplicationException
	{
		List<TaxonomyTaggingBean> loProcurementProposalList = null;
		try
		{
			String lsProposalTitle = aoTaxonomyTaggingBean.getProposalTitle();
			// append percentage signs to procurement title/ contract title to
			// search in db
			if (lsProposalTitle != null && !lsProposalTitle.isEmpty())
			{
				aoTaxonomyTaggingBean.setProposalTitle(HHSConstants.PERCENT + lsProposalTitle + HHSConstants.PERCENT);
			}
			String lsContractProcurementTitle = aoTaxonomyTaggingBean.getProcurementContractTitle();
			if (lsContractProcurementTitle != null && !lsContractProcurementTitle.isEmpty())
			{
				aoTaxonomyTaggingBean.setProcurementContractTitle(HHSConstants.PERCENT + lsContractProcurementTitle
						+ HHSConstants.PERCENT);
			}
			String lsCompetitionPoolTitle = aoTaxonomyTaggingBean.getCompetitionPoolTitle();
			if (lsCompetitionPoolTitle != null && !lsCompetitionPoolTitle.isEmpty())
			{
				aoTaxonomyTaggingBean.setCompetitionPoolTitle(HHSConstants.PERCENT + lsCompetitionPoolTitle
						+ HHSConstants.PERCENT);
			}
			// get list of proposal/contracts qualified to
			// filter/sort/pagination
			loProcurementProposalList = (List<TaxonomyTaggingBean>) DAOUtil.masterDAO(aoMyBatisSession,
					aoTaxonomyTaggingBean, HHSConstants.MAPPER_CLASS_TAXONOMY_TAGGING_MAPPER,
					HHSConstants.FETCH_PROC_PROP_DETAIL_TRANS, HHSConstants.COM_NYC_HHS_MODEL_TAXONOMY_TAGGING_BEAN);
			if (lsProposalTitle != null && !lsProposalTitle.isEmpty())
			{
				aoTaxonomyTaggingBean.setProposalTitle(lsProposalTitle);
			}
			if (lsContractProcurementTitle != null && !lsContractProcurementTitle.isEmpty())
			{
				aoTaxonomyTaggingBean.setProcurementContractTitle(lsContractProcurementTitle);
			}
			if (lsCompetitionPoolTitle != null && !lsCompetitionPoolTitle.isEmpty())
			{
				aoTaxonomyTaggingBean.setCompetitionPoolTitle(lsCompetitionPoolTitle);
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData(HHSConstants.TAXONOMY_LIST, loProcurementProposalList);
			LOG_OBJECT.Error("Exception occured while retreiving complete Taxonomy Data from Database", aoAppEx);
			setMoState("Transaction Failed:: TaxonomyService: getTaxonomyMasterDB method -Exception occured while retrieving complete Taxonomy "
					+ "List from Database where delete status is N:" + loProcurementProposalList + "/n");
			throw aoAppEx;
		}
		setMoState("Transaction Success:: TaxonomyService: getTaxonomyMasterDB method Complete Taxonomy Tree List retrieved successfully for "
				+ "delete status N:" + loProcurementProposalList + "/n");
		return loProcurementProposalList;
	}

	/**
	 * This method is returning ProcurementProposalList.
	 * @param aoMyBatisSession - My batis session
	 * @param aoTaxonomyTaggingMap - Map of taxonomy tagging item details
	 * @return - list of taxonomy tagging beans
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings("unchecked")
	public List<TaxonomyTaggingBean> getTaxonomyTaggingList(SqlSession aoMyBatisSession,
			Map<String, Object> aoTaxonomyTaggingMap) throws ApplicationException
	{
		List<TaxonomyTaggingBean> loProcurementProposalList = null;
		try
		{
			loProcurementProposalList = (List<TaxonomyTaggingBean>) DAOUtil.masterDAO(aoMyBatisSession,
					aoTaxonomyTaggingMap, HHSConstants.MAPPER_CLASS_TAXONOMY_TAGGING_MAPPER,
					HHSConstants.GET_TAXONOMY_TAGGING_LIST, HHSConstants.JAVA_UTIL_MAP);
			getTaxonomyNames(loProcurementProposalList);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData(HHSConstants.TAXONOMY_LIST, loProcurementProposalList);
			LOG_OBJECT.Error("Exception occured while retreiving complete Taxonomy Data from Database", aoAppEx);
			setMoState("Transaction Failed:: TaxonomyService: getTaxonomyMasterDB method -Exception occured while retrieving complete Taxonomy "
					+ "List from Database where delete status is N:" + loProcurementProposalList + "/n");
			throw aoAppEx;
		}
		setMoState("Transaction Success:: TaxonomyService: getTaxonomyMasterDB method Complete Taxonomy Tree List retrieved successfully for "
				+ "delete status N:" + loProcurementProposalList + "/n");
		return loProcurementProposalList;
	}

	/**
	 * This method is to delete Taxonomy tags and it will return number of rows
	 * deleted.
	 * <ul>
	 * <li>1.Deletes from taxonomy tagging using query
	 * "deleteFromTaxonomyTagging"</li>
	 * <li>1.Deletes from taxonomy modifiers using query
	 * "deleteFromTaxonomyTaggingModifiers"</li>
	 * </ul>
	 * @param aoMyBatisSession - my batis session
	 * @param aoTaxonomyTaggingBean - tagging details bean to be deleted
	 * @return - count of deleted tags
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public int deleteTaxonomyTags(SqlSession aoMyBatisSession, TaxonomyTaggingBean aoTaxonomyTaggingBean)
			throws ApplicationException
	{
		int liNumOfRowDeleted = HHSConstants.INT_ZERO;
		try
		{
			liNumOfRowDeleted = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoTaxonomyTaggingBean,
					HHSConstants.MAPPER_CLASS_TAXONOMY_TAGGING_MAPPER, HHSConstants.DELETE_FROM_TAXONOMY_TAGGING,
					HHSConstants.COM_NYC_HHS_MODEL_TAXONOMY_TAGGING_BEAN);
			DAOUtil.masterDAO(aoMyBatisSession, aoTaxonomyTaggingBean,
					HHSConstants.MAPPER_CLASS_TAXONOMY_TAGGING_MAPPER,
					HHSConstants.DELETE_FROM_TAXONOMY_TAGGING_MODIFIERS,
					HHSConstants.COM_NYC_HHS_MODEL_TAXONOMY_TAGGING_BEAN);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData(HHSConstants.TAXONOMY_TREE, CommonUtil.convertBeanToString(aoTaxonomyTaggingBean));
			LOG_OBJECT.Error("Exception occured while updating Taxonomy data for selected Item", aoAppEx);
			setMoState("Transaction Failed:: TaxonomyService: updateItemDetails method -Exception occured while updating data for selected Item:"
					+ aoTaxonomyTaggingBean + "/n");
			throw aoAppEx;
		}
		setMoState("Transaction Success:: TaxonomyService: updateItemDetails method -Selected Item successfully updated in database:"
				+ aoTaxonomyTaggingBean + "/n");
		return liNumOfRowDeleted;
	}

	/**
	 * This method is to get number of rows in the procurement record. If
	 * ContractProcurementTitle is not null it will set it into taxonomy tagging
	 * bean.
	 * @param aoMyBatisSession - My batis session
	 * @param aoTaxonomyTaggingBean - Taxonomy tagging bean
	 * @return - Count of taxonomy list returned
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public int selectProcurementRecordCount(SqlSession aoMyBatisSession, TaxonomyTaggingBean aoTaxonomyTaggingBean)
			throws ApplicationException
	{
		int liRowCount = HHSConstants.INT_ZERO;
		try
		{
			String lsProposalTitle = aoTaxonomyTaggingBean.getProposalTitle();
			// append percentage signs to procurement title/ contract title to
			// search in db
			if (lsProposalTitle != null && !lsProposalTitle.isEmpty())
			{
				aoTaxonomyTaggingBean.setProposalTitle(HHSConstants.PERCENT + lsProposalTitle + HHSConstants.PERCENT);
			}
			String lsContractProcurementTitle = aoTaxonomyTaggingBean.getProcurementContractTitle();
			if (lsContractProcurementTitle != null && !lsContractProcurementTitle.isEmpty())
			{
				aoTaxonomyTaggingBean.setProcurementContractTitle(HHSConstants.PERCENT + lsContractProcurementTitle
						+ HHSConstants.PERCENT);
			}
			String lsCompetitionPoolTitle = aoTaxonomyTaggingBean.getCompetitionPoolTitle();
			if (lsCompetitionPoolTitle != null && !lsCompetitionPoolTitle.isEmpty())
			{
				aoTaxonomyTaggingBean.setCompetitionPoolTitle(HHSConstants.PERCENT + lsCompetitionPoolTitle
						+ HHSConstants.PERCENT);
			}
			// get count of proposal/contracts qualified to
			// filter/sort/pagination
			liRowCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoTaxonomyTaggingBean,
					HHSConstants.MAPPER_CLASS_TAXONOMY_TAGGING_MAPPER, HHSConstants.SELECTED_PROCUREMENT_RECORD_COUNT,
					HHSConstants.COM_NYC_HHS_MODEL_TAXONOMY_TAGGING_BEAN);
			if (lsProposalTitle != null && !lsProposalTitle.isEmpty())
			{
				aoTaxonomyTaggingBean.setProposalTitle(lsProposalTitle);
			}
			if (lsContractProcurementTitle != null && !lsContractProcurementTitle.isEmpty())
			{
				aoTaxonomyTaggingBean.setProcurementContractTitle(lsContractProcurementTitle);
			}
			if (lsCompetitionPoolTitle != null && !lsCompetitionPoolTitle.isEmpty())
			{
				aoTaxonomyTaggingBean.setCompetitionPoolTitle(lsCompetitionPoolTitle);
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData(HHSConstants.TAXONOMY_TREE, CommonUtil.convertBeanToString(aoTaxonomyTaggingBean));
			LOG_OBJECT.Error("Exception occured while updating Taxonomy data for selected Item", aoAppEx);
			setMoState("Transaction Failed:: TaxonomyService: updateItemDetails method -Exception occured while updating data for selected Item:"
					+ aoTaxonomyTaggingBean + "/n");
			throw aoAppEx;
		}
		setMoState("Transaction Success:: TaxonomyService: updateItemDetails method -Selected Item successfully updated in database:"
				+ aoTaxonomyTaggingBean + "/n");
		return liRowCount;
	}

	/**
	 * This method gets the taxonomy name of tags
	 * <ul>
	 * <li>Check if taxonomy list has some data</li>
	 * <li>If yes, iterate and fetch tagIds'</li>
	 * <li>Get taxonomy name of corresponding id from cached taxonomy dom</li>
	 * <li>Sort the list over name</li>
	 * </ul>
	 * @param aoTaxonomyTaggingList - taxonomy tagging list
	 * @throws ApplicationException If an Application Exception occurs
	 */
	private void getTaxonomyNames(List<TaxonomyTaggingBean> aoTaxonomyTaggingList) throws ApplicationException
	{
		try
		{
			Document loTaxonomyDom = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
					ApplicationConstants.TAXONOMY_ELEMENT);
			List<String> loTagNameList = null;
			StringBuffer loBranchIdList = null;
			StringBuffer loBranchPath = null;
			if (aoTaxonomyTaggingList != null && aoTaxonomyTaggingList.size() > HHSConstants.INT_ZERO)
			{
				for (TaxonomyTaggingBean loTaxonomyTagBean : aoTaxonomyTaggingList)
				{
					loTagNameList = new ArrayList<String>();
					loBranchIdList = new StringBuffer();
					String loTaggingArray[] = loTaxonomyTagBean.getModifiers().split(HHSConstants.COMMA);
					String lsElementId = loTaxonomyTagBean.getElementId();
					loTaxonomyTagBean.setServiceFunctionName(XMLUtil.getElement(
							HHSConstants.ELEMENT_XPATH + lsElementId + HHSConstants.RULE_SET_XPATH_STR_RIGHT_PART,
							loTaxonomyDom).getAttributeValue(HHSConstants.NAME));
					// get tag names for taxonomy elements
					loBranchPath = new StringBuffer();
					for (String lsTagId : loTaggingArray)
					{
						if (lsTagId != null && !lsTagId.isEmpty())
						{
							Element loTargetElt = XMLUtil.getElement(HHSConstants.ELEMENT_XPATH + lsTagId
									+ HHSConstants.RULE_SET_XPATH_STR_RIGHT_PART, loTaxonomyDom);
							loTagNameList.add(loTargetElt.getAttributeValue(HHSConstants.NAME));
							loBranchIdList.append(loTargetElt.getAttributeValue(HHSConstants.BRANCH_ID)).append(
									HHSConstants.HHSUTIL_DELIM_PIPE);
							for (String lsTagName : loTargetElt.getAttributeValue(HHSConstants.BRANCH_ID).split(
									HHSConstants.COMMA))
							{
								loTargetElt = XMLUtil.getElement(HHSConstants.ELEMENT_XPATH + lsTagName
										+ HHSConstants.RULE_SET_XPATH_STR_RIGHT_PART, loTaxonomyDom);
								loBranchPath.append(loTargetElt.getAttributeValue(HHSConstants.NAME)).append(
										HHSConstants.GREATER_THAN);
							}
							loBranchPath.append(HHSConstants.KEY_SEPARATOR);
						}
					}
					// sort list on tagnames
					Collections.sort(loTagNameList);
					loTaxonomyTagBean.setTaggedElementName(loTagNameList);
					loTaxonomyTagBean.setCompleteBranchPath(loBranchPath.toString());
					if (loBranchIdList.length() > HHSConstants.INT_ZERO)
					{
						loTaxonomyTagBean.setModifierBranchId(loBranchIdList.substring(HHSConstants.INT_ZERO,
								loBranchIdList.length() - HHSConstants.INT_ONE).toString());
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
			aoAppEx.addContextData(HHSConstants.TAXONOMY_LIST, CommonUtil.convertBeanToString(aoTaxonomyTaggingList));
			LOG_OBJECT.Error("Exception occured while updating Taxonomy data for services", aoAppEx);
			setMoState("Transaction Failed:: TaxonomyService: getTaxonomyNames method -Exception occured while updating data for services:"
					+ aoTaxonomyTaggingList + "/n");
			throw aoAppEx;
		}
	}
}
