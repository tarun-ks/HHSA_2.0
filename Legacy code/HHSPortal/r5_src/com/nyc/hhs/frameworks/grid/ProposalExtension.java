package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.model.ProposalDetailsBean;
import com.nyc.hhs.util.DateUtil;

/* This extension file is added for release 5
 * This class is used to create custom select element for contract list grid
 * view. Values in Action column on contract list page for Acclerator/Agency are
 * populated using it as a decorator class in grid.
 */
public class ProposalExtension implements DecoratorInterface
{

	/**
	 * This method is used to create actions drop down for finance contract list
	 * grid table. <br>
	 * Updated Method in R4
	 * 
	 * <ul>
	 * <li>1. Contract Status to Action mappings are defined in
	 * ContractStatusToActionMapping.xml</li>
	 * <li>2. We get the document object by retrieving the above xml mapping
	 * from cache using its key name financeMapping.xml defined in
	 * cachenew.properties.</li>
	 * <li>3. Then we define the path by passing ContractStatus as a variable so
	 * that we get the the required node list</li>
	 * <li>4. We get the required node list by using getElementList of XMLUtil.</li>
	 * <li>5. Then we declare a string builder and append all node values to
	 * option attribute so that it will be displayed in dropdown on screen.</li>
	 * </ul>
	 * <br>
	 * 
	 * 
	 * @param aoEachObject Bean Name
	 * @param aoCol Column name
	 * @param aoSeqNo Sequence Number
	 * @return control - string
	 * @throws ApplicationException - ApplicationException Object
	 * 
	 */

	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aoSeqNo) throws ApplicationException
	{
		ProposalDetailsBean loProposalList = (ProposalDetailsBean) aoEachObject;

		StringBuilder lsControl = new StringBuilder();
		if (aoCol.getColumnName().equalsIgnoreCase(HHSConstants.AGENCYID))
		{
			lsControl.append(loProposalList.getAgencyId());
		}
		else if (aoCol.getColumnName().equalsIgnoreCase(HHSR5Constants.PROCUREMENT_TITLE1))
		{

			// R 7.2.0 QC 8914 For Observer, only Procurements in certain status are clickable
			// Added the top IF Condition for Observer Role
			if (ApplicationConstants.ROLE_OBSERVER.equalsIgnoreCase(loProposalList.getRoleCurrent()) )
			{
				if (isTitleClickable(loProposalList)) {
					lsControl.append("<a href=\"javascript: viewProcurement('" + loProposalList.getProcurementId()
							+ "');\">" + loProposalList.getProcurementtitle() + "</a>");
				}
				else {
					lsControl.append(loProposalList.getProcurementtitle());
				}
			}
			else
			{
				lsControl.append("<a href=\"javascript: viewProcurement('" + loProposalList.getProcurementId()
						+ "');\">" + loProposalList.getProcurementtitle() + "</a>");
			}
		}
		else if (aoCol.getColumnName().equalsIgnoreCase("competitionPoolTitle"))
		{
			// R 7.2.0 QC 8914 For Observer, only Procurements in certain status are clickable
			// Added the top IF Condition for Observer Role
			if (ApplicationConstants.ROLE_OBSERVER.equalsIgnoreCase(loProposalList.getRoleCurrent()) )
			{
				if (isTitleClickable(loProposalList)) {
					lsControl.append("<a href=\"javascript: viewCompetitionPoolTitle('"
							+ loProposalList.getProcurementId() + "','" + loProposalList.getEvaluationGroupId() + "','"
							+ loProposalList.getCompetitionpoolid() + "','" + loProposalList.getEvaluationPoolMappingId()
							+ "');\">" + loProposalList.getCompetitionPoolTitle() + "</a>");
				}
				else {
					lsControl.append(loProposalList.getCompetitionPoolTitle());
				}
			}
			else
			{
				lsControl.append("<a href=\"javascript: viewCompetitionPoolTitle('"
						+ loProposalList.getProcurementId() + "','" + loProposalList.getEvaluationGroupId() + "','"
						+ loProposalList.getCompetitionpoolid() + "','" + loProposalList.getEvaluationPoolMappingId()
						+ "');\">" + loProposalList.getCompetitionPoolTitle() + "</a>");
			}
		}
		else if (aoCol.getColumnName().equalsIgnoreCase(HHSConstants.PROPOSAL_ID))
		{
			lsControl.append(loProposalList.getProposalId());
		}
		else if (aoCol.getColumnName().equalsIgnoreCase(HHSConstants.PROPOSAL_TITLE))
		{
			// R 7.2.0 QC 8914 For Observer, only Procurements in certain status are clickable
			// Added the top IF Condition for Observer Role
			if (ApplicationConstants.ROLE_OBSERVER.equalsIgnoreCase(loProposalList.getRoleCurrent()) )
			{
				if (isTitleClickable(loProposalList)) {
					lsControl.append("<a href=\"javascript: viewProposalDetail('" + loProposalList.getProcurementId()
							+ "','" + loProposalList.getProposalId() + "');\">" + loProposalList.getProposalTitle()
							+ "</a>");
				}
				else {
					lsControl.append(loProposalList.getProposalTitle());
				}
			}
			else if (loProposalList.getProposalStatus().equalsIgnoreCase(HHSR5Constants.SUBMITTED)
					&& loProposalList.getEvaluationPoolMappingStatus().equalsIgnoreCase(HHSR5Constants.RELEASED))
			{
				lsControl.append(loProposalList.getProposalTitle());
			}
			else
			{
				lsControl.append("<a href=\"javascript: viewProposalDetail('" + loProposalList.getProcurementId()
						+ "','" + loProposalList.getProposalId() + "');\">" + loProposalList.getProposalTitle()
						+ "</a>");
			}

		}
		else if (aoCol.getColumnName().equalsIgnoreCase(HHSConstants.PROP_STATUS_ID))
		{
			lsControl.append(loProposalList.getProposalStatus());
		}
		else if (aoCol.getColumnName().equalsIgnoreCase(HHSConstants.MODIFIED_DATE))
		{
			lsControl.append(DateUtil.getDateMMddYYYYFormat(loProposalList.getModifiedDate()));
		}
		return lsControl.toString();
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
		return HHSConstants.RESUME;
	}
	
	
	/**
	 * R 7.2.0 QC 8914
	 * Procurement title is not clickable if status is 'Planned', 'Released', 'Proposals Received', 'Evaluations Complete'
	 * @param procurement
	 * @return
	 */
	private boolean isTitleClickable (ProposalDetailsBean proposal) {
		if ("2".equalsIgnoreCase(proposal.getProcurementStatus()) 
				|| "3".equalsIgnoreCase(proposal.getProcurementStatus())
				|| "4".equalsIgnoreCase(proposal.getProcurementStatus())
				|| "5".equalsIgnoreCase(proposal.getProcurementStatus()))
			return false;
		else
			return true;
	}
}
