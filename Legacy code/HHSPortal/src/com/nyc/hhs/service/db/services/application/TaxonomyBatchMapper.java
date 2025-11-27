package com.nyc.hhs.service.db.services.application;

import java.util.List;
import java.util.Map;

import com.nyc.hhs.model.TaxonomyServiceBean;
import com.nyc.hhs.model.TaxonomyTree;

/**
 * TaxonomyBatchMapper is an interface between the DAO and database layer for
 * taxonomy batch process to insert, update entries.
 * 
 */
public interface TaxonomyBatchMapper
{
	List<TaxonomyTree> executeTaxonomyBtch();

	List<TaxonomyTree> parentEvidenceFlagCheck(TaxonomyTree aoParentElementIdList);

	Integer updateServiceAppForEvidenceCheck(TaxonomyTree aoEvedenceToNonEvedenceLst);

	Integer updateServiceAppForApprovalCheck(TaxonomyTree aoApprovalToNonApprovalLst);

	Integer updateForEvidenceMovedToParentCheck(TaxonomyTree aoParElmntIdLst);

	void insertIntoServiceApplication(TaxonomyServiceBean aoApplicationSummaryBean);

	void insertIntoSupersedingStatus(Map<String, Object> aoSupersedingMap);

	List<TaxonomyServiceBean> getDataFromServiceApplication(TaxonomyTree aoChildElementId);

	List<TaxonomyServiceBean> getDataFromServiceApplication(String aoChildElementId);

	List<TaxonomyServiceBean> getServiceApplicationID(String asElementId);

	TaxonomyServiceBean getOldServiceApplicationId(String asServiceApplicationId);

	Integer deleteFromSupersedingStatus(String asElementId);

	TaxonomyTree getApprovedServiceCount(TaxonomyTree aoParentElementIdList);

	TaxonomyTree checkEventInSuperseding(String asEntityId);
}
