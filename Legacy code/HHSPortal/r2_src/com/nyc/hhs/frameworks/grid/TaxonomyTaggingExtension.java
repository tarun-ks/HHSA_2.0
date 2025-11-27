/**
 * 
 */
package com.nyc.hhs.frameworks.grid;

import org.apache.commons.lang.StringEscapeUtils;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.model.TaxonomyTaggingBean;
import com.nyc.hhs.util.DateUtil;

/**
 * This class is used to generate an extension which creates a drop down for
 * provider users.
 * 
 */

public class TaxonomyTaggingExtension implements DecoratorInterface
{
	/**
	 * <ul><li>This method will generate html code for a particular column of table
	 * depending upon the input column name</li></ul>
	 * 
	 * @param aoEachObject an object of list to be displayed in grid
	 * @param aoCol a column object
	 * @param aiSeqNo an integer value of sequence number
	 * @return a string value of html code formed
	 * @throws ApplicationException Updated Method in R4
	 */
	@Override
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aiSeqNo) throws ApplicationException
	{
		String lsControl = HHSConstants.EMPTY_STRING;
		TaxonomyTaggingBean loTaxonomyTaggingBean = (TaxonomyTaggingBean) aoEachObject;
		String lsProcurementId = String.valueOf(loTaxonomyTaggingBean.getProcurementId());
		String lsProposalId = String.valueOf(loTaxonomyTaggingBean.getProposalId());
		String lsContractId = String.valueOf(loTaxonomyTaggingBean.getContractId());
		String lsOrganizationId = String.valueOf(loTaxonomyTaggingBean.getOrganizationId());
		if (HHSConstants.PROPOSAL_TITLE.equalsIgnoreCase(aoCol.getColumnName())
				&& null != loTaxonomyTaggingBean.getProposalTitle())
		{
			String lsProposalTitle = StringEscapeUtils.escapeXml(loTaxonomyTaggingBean.getProposalTitle());
			if (loTaxonomyTaggingBean.getType() != null
					&& loTaxonomyTaggingBean.getType().equalsIgnoreCase(HHSConstants.PROC))
			{
				lsControl = "<a title='" + lsProposalTitle + "' alt='" + lsProposalTitle
						+ "' href='javascript:void(0);' type='proposalLink'>" + lsProposalTitle + "</a>";
			}
			else
			{
				lsControl = lsProposalTitle;
			}
		}
		else if (HHSConstants.ORGANIZATION_LEGAL_NAME.equalsIgnoreCase(aoCol.getColumnName())
				&& null != loTaxonomyTaggingBean.getProviderName())
		{
			String lsProviderName = StringEscapeUtils.escapeXml(loTaxonomyTaggingBean.getProviderName());
			lsControl = "<a title='" + lsProviderName + "' alt='" + lsProviderName
					+ "' href='javascript:void(0);' type='providerLink'>" + lsProviderName + "</a>"
					+ "<input type='hidden' id='hiddenProposalId' name='hiddenProposalId' value='" + lsProposalId
					+ "'/>" + "<input type='hidden' id='hiddenProcurementId' name='hiddenProcurementId' value='"
					+ lsProcurementId + "'/>"
					+ "<input type='hidden' id='hiddenContractId' name='hiddenContractId' value='" + lsContractId
					+ "'/>" + "<input type='hidden' id='hiddenOrganizationId' name='hiddenOrganizationId' value='"
					+ lsOrganizationId + "'/>";
		}
		else if (HHSConstants.PROCUREMENT_CONTRACT_TITLE.equalsIgnoreCase(aoCol.getColumnName())
				&& null != loTaxonomyTaggingBean.getProcurementContractTitle())
		{
			String lsContractTitle = StringEscapeUtils.escapeXml(loTaxonomyTaggingBean.getProcurementContractTitle());
			if (loTaxonomyTaggingBean.getType() != null
					&& loTaxonomyTaggingBean.getType().equalsIgnoreCase(HHSConstants.PROC))
			{
				lsControl = "<a title='" + lsContractTitle + "' alt='" + lsContractTitle
						+ "' href='javascript:void(0);' onclick=\"javascript: viewProcurementSummary('"
						+ loTaxonomyTaggingBean.getProcurementId() + "')\">" + lsContractTitle + "</a>";
			}
			else
			{
				lsControl = lsContractTitle;
			}
		}
		else if (HHSConstants.ACTION.equalsIgnoreCase(aoCol.getColumnName())
				&& null != loTaxonomyTaggingBean.getAction())
		{
			String lsProposalTitle = StringEscapeUtils.escapeXml(loTaxonomyTaggingBean.getProposalTitle());
			String lsProviderName = StringEscapeUtils.escapeXml(loTaxonomyTaggingBean.getProviderName());
			String lsContractTitle = StringEscapeUtils.escapeXml(loTaxonomyTaggingBean.getProcurementContractTitle());
			lsControl = "<a title='" + loTaxonomyTaggingBean.getAction() + "' alt='"
					+ loTaxonomyTaggingBean.getAction() + "' type='editTag' href='javascript:void(0);'>"
					+ loTaxonomyTaggingBean.getAction() + "</a>"
					+ "<input type='hidden' id='hiddenProposalTitle' name='hiddenProposalTitle' value='"
					+ lsProposalTitle + "'/>"
					+ "<input type='hidden' id='hiddenProviderName' name='hiddenProviderName' value='" + lsProviderName
					+ "'/>" + "<input type='hidden' id='hiddenContractTitle' name='hiddenContractTitle' value='"
					+ lsContractTitle + "'/>";
		}
		else if (HHSConstants.APPROVAL_DATE.equalsIgnoreCase(aoCol.getColumnName()))
		{
			if (loTaxonomyTaggingBean.getApprovalDate() != null)
			{
				lsControl = DateUtil.getDateByFormat(HHSConstants.YYYY_MM_DD_HH_MM_SS, HHSConstants.MMDDYYFORMAT,
						loTaxonomyTaggingBean.getApprovalDate());
			}
		}
		else if (HHSConstants.PROCUREMENT_ID.equalsIgnoreCase(aoCol.getColumnName()))
		{
			lsControl = "<input type=checkbox name=check id=columnCheckBox " + "value=" + lsProposalId + "_"
					+ lsProcurementId + "_" + lsContractId + " onClick=\"javascript: enableAddNewTagsBulk("
					+ loTaxonomyTaggingBean.getSelectedTaxonomy() + ")\"/>";
		}
		return lsControl;
	}

	/**
	 * This method will generate html code for a particular column header of
	 * table depending upon the input column name
	 * 
	 * @param aoCol a column object
	 * 
	 * @return a string value of html code formed Updated Method in R4
	 */
	public String getControlForHeading(Column aoCol)
	{
		String lsControl = HHSConstants.RESUME;
		if ("procurementId".equalsIgnoreCase(aoCol.getColumnName()))
		{
			lsControl = "<input type=checkbox name=selectAll id=selectAll value=selectAll onClick=\"javascript: selectAllCheck()\"/>";
		}
		return lsControl;
	}
}
