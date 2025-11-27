package com.nyc.hhs.service.db.services.application;

import java.util.List;
import java.util.Map;

import com.nyc.hhs.model.TaxonomyModifiersBean;
import com.nyc.hhs.model.TaxonomyTaggingBean;

/**
 * TaxonomyMapper is an interface between the DAO and database layer for
 * business and service application,organization profile related transactions to
 * insert, update and select entries.
 * 
 */
public interface TaxonomyTaggingMapper
{

	List<TaxonomyTaggingBean> fetchProcurementProposalDetails(TaxonomyTaggingBean aoTaxonomyTaggingBean);

	List<TaxonomyTaggingBean> fetchGridProcurementProposalDetailsInBulk(Map asTaxonomyTaggingMap);

	List<TaxonomyTaggingBean> getTaxonomyTaggingList(Map asTaxonomyTaggingMap);

	List<TaxonomyTaggingBean> getTaxonomyTaggingInBulkList(Map asTaxonomyTaggingMap);

	void insertTaxonomyModifierDetails(TaxonomyModifiersBean aoTaxonomyModifiersBean);

	String selectTaxonomyModifierDetails(Map loTaxDetails);

	int updateTaxonomyModifierDetails(TaxonomyModifiersBean aoTaxonomyModifiersBean);

	int deleteFromTaxonomyTagging(TaxonomyTaggingBean aoTaxonomyTaggingBean);

	int deleteFromTaxonomyTaggingInBulk(TaxonomyTaggingBean aoTaxonomyTaggingBean);

	int deleteFromTaxonomyTaggingModifiers(TaxonomyTaggingBean aoTaxonomyTaggingBean);

	int deleteFromTaxonomyTaggingModifiersInBulk(TaxonomyTaggingBean aoTaxonomyTaggingBean);

	int removeAllFromTaxonomyTaggingModifiersInBulk(TaxonomyTaggingBean aoTaxonomyTaggingBean);

	int removeAllFromTaxonomyTaggingInBulk(TaxonomyTaggingBean aoTaxonomyTaggingBean);

	int updateTaxonomyTaggingDetails(TaxonomyTaggingBean aoTaxonomyTaggingBean);

	int updateTaxonomyTaggingDetailsInBulk(TaxonomyTaggingBean aoTaxonomyTaggingBean);

	int insertTaxonomyTaggingDetails(TaxonomyTaggingBean aoTaxonomyTaggingBean);

	int insertTaxonomyTaggingDetailsInBulkProposal(TaxonomyTaggingBean aoTaxonomyTaggingBean);

	int selectProcurementRecordCount(TaxonomyTaggingBean aoTaxonomyTaggingBean);

	String getNextTaggingId();

	TaxonomyTaggingBean getProposalTitle(String asProposalId);

	String getTaxonomyTaggingIds(TaxonomyTaggingBean aoTaxonomyTaggingBean);

	List<String> getTaxonomyTaggingIds1(Map<String, String> aoId);
}
