package com.nyc.hhs.frameworks.grid;

import java.util.List;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.model.TaxonomyTaggingBean;

public class AddNewTaxonomyTagExtension implements DecoratorInterface
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
		TaxonomyTaggingBean loTaxonomyTaggingBean = (TaxonomyTaggingBean) aoEachObject;
		String lsProcurementId = loTaxonomyTaggingBean.getProcurementId();
		String lsProposalId = loTaxonomyTaggingBean.getProposalId();
		String lsContractId = loTaxonomyTaggingBean.getContractId();
		String lsTaggingId = loTaxonomyTaggingBean.getTaxonomyTaggingId();
		String lsElementId = loTaxonomyTaggingBean.getElementId();
		String lsLinkageBranchId = loTaxonomyTaggingBean.getModifierBranchId();
		String lsControlForTag = HHSConstants.EMPTY_STRING;

		if (HHSConstants.ACTIONS.equalsIgnoreCase(aoCol.getColumnName()))
		{
			lsControlForTag = "<select class=terms name=actions2 id=actions" + aiSeqNo
					+ " style='width: auto' onChange=\"javascript: editDeleteTags('" + lsProcurementId + "',this, '"
					+ lsProposalId + "', '" + lsContractId + "', '" + lsElementId + "', '" + lsLinkageBranchId + "', '"
					+ lsTaggingId + "')\"><option value=I need to... >I need to...</option>"
					+ "<option>Edit Tag</option>" + "<option>Delete Tag</option>" + "</select>";
		}
		else if (HHSConstants.MODIFIERS.equalsIgnoreCase(aoCol.getColumnName()))
		{
			List<String> loTaggedElementName = loTaxonomyTaggingBean.getTaggedElementName();
			if (loTaggedElementName != null && !loTaggedElementName.isEmpty())
			{
				lsControlForTag = loTaggedElementName.toString()
						.replace(HHSConstants.SQUARE_BRAC_BEGIN, HHSConstants.EMPTY_STRING)
						.replace(HHSConstants.SQUARE_BRAC_END, HHSConstants.EMPTY_STRING)
						.replace(HHSConstants.COMMA, HHSConstants.COMMA_SPACE);
			}
			else
			{
				lsControlForTag = "--";
			}
		}
		return lsControlForTag;
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
		String lsControlForTagging = HHSConstants.RESUME;
		return lsControlForTagging;
	}

}
