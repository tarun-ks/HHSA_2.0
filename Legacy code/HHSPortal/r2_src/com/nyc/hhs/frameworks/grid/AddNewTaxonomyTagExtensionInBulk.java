package com.nyc.hhs.frameworks.grid;

import java.util.List;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.model.TaxonomyTaggingBean;

public class AddNewTaxonomyTagExtensionInBulk implements DecoratorInterface
{

	/**
	 * This method will generate html code for a particular column of table
	 * depending upon the input column name
	 * 
	 * @param aoEachObject an object of list to be displayed in grid
	 * @param aoCol a column object
	 * @param aiSeqNo an integer value of sequence number
	 * @return a string value of html code formed
	 */
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aiSeqNo)
	{
		TaxonomyTaggingBean loTaxonomyTaggingBeanForBulk = (TaxonomyTaggingBean) aoEachObject;
		String lsProcurementIdForBulk = loTaxonomyTaggingBeanForBulk.getProcurementId();
		String lsProposalIdForBulk = loTaxonomyTaggingBeanForBulk.getProposalId();
		String lsContractIdForBulk = loTaxonomyTaggingBeanForBulk.getContractId();
		String lsTaggingIdForBulk = loTaxonomyTaggingBeanForBulk.getTaxonomyTaggingId();
		String lsElementIdForBulk = loTaxonomyTaggingBeanForBulk.getElementId();
		String lsLinkageBranchIdForBulk = loTaxonomyTaggingBeanForBulk.getModifierBranchId();
		String lsControlForBulk = HHSConstants.EMPTY_STRING;

		if (HHSConstants.ACTIONS.equalsIgnoreCase(aoCol.getColumnName()))
		{
			lsControlForBulk = "<select class=terms name=actions2 id=actions" + aiSeqNo
					+ " style='width: auto' onChange=\"javascript: editDeleteTagsInBulk('" + lsProcurementIdForBulk
					+ "',this, '" + lsProposalIdForBulk + "', '" + lsContractIdForBulk + "', '" + lsElementIdForBulk + "', '"
					+ lsLinkageBranchIdForBulk + "', '" + lsTaggingIdForBulk
					+ "')\"><option value=I need to... >I need to...</option>" + "<option>Edit Tag</option>"
					+ "<option>Delete Tag</option>" + "</select>";
		}
		else if (HHSConstants.MODIFIERS.equalsIgnoreCase(aoCol.getColumnName()))
		{
			List<String> loTaggedElementName = loTaxonomyTaggingBeanForBulk.getTaggedElementName();
			if (loTaggedElementName != null && !loTaggedElementName.isEmpty())
			{
				lsControlForBulk = loTaggedElementName.toString()
						.replace(HHSConstants.SQUARE_BRAC_BEGIN, HHSConstants.EMPTY_STRING)
						.replace(HHSConstants.SQUARE_BRAC_END, HHSConstants.EMPTY_STRING)
						.replace(HHSConstants.COMMA, HHSConstants.COMMA_SPACE);
			}
			else
			{
				lsControlForBulk = "--";
			}
		}
		return lsControlForBulk;
	}

	/**
	 * This method will generate html code for a particular column header of
	 * table depending upon the input column name
	 * 
	 * @param aoCol a column object
	 * 
	 * @return a string value of html code formed
	 */
	public String getControlForHeading(Column aoCol)
	{
		String lsControlForBulk = HHSConstants.RESUME;
		return lsControlForBulk;
	}

}
