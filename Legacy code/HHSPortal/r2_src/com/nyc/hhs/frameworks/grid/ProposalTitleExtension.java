/**
 * 
 */
package com.nyc.hhs.frameworks.grid;

import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.model.ProposalDetailsBean;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.XMLUtil;

public class ProposalTitleExtension implements DecoratorInterface
{
	/**
	 * This method is used to show drop down on the proposal summary page having
	 * proposal grid
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * @param aoEachObject an object of list to be displayed in grid
	 * @param aoCol a column object
	 * @param aoSeqNo an integer value of sequence number
	 * @return a string value of html code formed
	 * @throws ApplicationException
	 */
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aoSeqNo) throws ApplicationException
	{
		ProposalDetailsBean loProposalDetailsBean = (ProposalDetailsBean) aoEachObject;
		StringBuilder lsControl = new StringBuilder();
		if (aoCol.getColumnName().equalsIgnoreCase(HHSConstants.ACTION))
		{
			Document loDoc;
			String lsXPathTop = HHSConstants.EMPTY_STRING;
			try
			{
				loDoc = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
						HHSConstants.PROPOSAL_ACTION_MAPPING_FILE_NAME);
				if (null != loProposalDetailsBean
						&& null != loProposalDetailsBean.getProcurementStatus()
						&& (loProposalDetailsBean.getProcurementStatus().equals(
								PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
										HHSConstants.STATUS_PROCUREMENT_CANCELLED)) || loProposalDetailsBean
								.getProcurementStatus().equals(
										PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
												HHSConstants.STATUS_PROCUREMENT_CLOSED))))
				{
					if (loProposalDetailsBean.getUserRole() != null
							&& (loProposalDetailsBean.getUserRole().equalsIgnoreCase(
									ApplicationConstants.ROLE_ADMINISTRATOR_PROV_MANAGER) || loProposalDetailsBean
									.getUserRole().equalsIgnoreCase(ApplicationConstants.ROLE_MANAGER)))
					{
						lsXPathTop = "//page[(@name=\"proposalSummaryActions\")]//type[(@name=\"" + "manager"
								+ "\")]//status[(@name=\"" + loProposalDetailsBean.getProcurementStatus()
								+ "\")]//action";
					}
					else
					{
						lsXPathTop = "//page[(@name=\"proposalSummaryActions\")]//type[(@name=\"" + "staff"
								+ "\")]//status[(@name=\"" + loProposalDetailsBean.getProcurementStatus()
								+ "\")]//action";
					}
				}
				else if (loProposalDetailsBean != null
						&& loProposalDetailsBean.getUserRole() != null
						&& (loProposalDetailsBean.getUserRole().equalsIgnoreCase(
								ApplicationConstants.ROLE_ADMINISTRATOR_PROV_MANAGER) || loProposalDetailsBean
								.getUserRole().equalsIgnoreCase(ApplicationConstants.ROLE_MANAGER)))
				{
					lsXPathTop = "//page[(@name=\"proposalSummaryActions\")]//type[(@name=\"" + "manager"
							+ "\")]//status[(@name=\"" + loProposalDetailsBean.getProposalStatusId() + "\")]//action";
				}
				else
				{
					if (null != loProposalDetailsBean && null != loProposalDetailsBean.getProposalStatusId())
					{
						lsXPathTop = "//page[(@name=\"proposalSummaryActions\")]//type[(@name=\"" + "staff"
								+ "\")]//status[(@name=\"" + loProposalDetailsBean.getProposalStatusId()
								+ "\")]//action";
					}
				}

				List<Element> loNodeList = XMLUtil.getElementList(lsXPathTop, loDoc);
				if (null != loProposalDetailsBean && null != loProposalDetailsBean.getProposalId())
				{
					lsControl.append("<select name=\"action\" style=\"width: 231px\"  onchange=\"processAction(this,"
							+ loProposalDetailsBean.getProposalId() + ");\">"
							+ "<option value=I need to... >I need to...</option>");
				}
				for (Element loNode : loNodeList)
				{
					getControlForColumnFinal(loProposalDetailsBean, lsControl, loNode);
				}
				lsControl.append("</select>");
			}
			catch (ApplicationException loEx)
			{
				throw loEx;
			}
		}
		else if (aoCol.getColumnName().equalsIgnoreCase(HHSConstants.PROPOSAL_TITLE))
		{
			lsControl.append("<a href=\"javascript: viewProposalSummary(" + loProposalDetailsBean.getProposalId()
					+ ");\">" + loProposalDetailsBean.getProposalTitle() + "</a>");
		}
		else if (aoCol.getColumnName().equalsIgnoreCase(HHSConstants.PROPOSAL_STATUS))
		{
			if (loProposalDetailsBean.getProposalStatusId() != null
					&& loProposalDetailsBean.getProposalStatusId().equalsIgnoreCase(
							PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
									HHSConstants.STATUS_PROPOSAL_SELECTED)))
			{
				lsControl.append("<a href=\"javascript: viewSelectionDetails(" + loProposalDetailsBean.getProposalId()
						+ ", " + loProposalDetailsBean.getEvaluationPoolMappingId() + ");\">"
						+ loProposalDetailsBean.getProposalStatus() + "</a>");
			}
			else
			{
				lsControl.append(loProposalDetailsBean.getProposalStatus());
			}
		}
		else if (aoCol.getColumnName().equalsIgnoreCase(HHSConstants.LAT_VER_QUES))
		{
			// Modified as a part of release 3.1.0 for enhancement request 6024
			//Further modified to solve Release Addendum Issue in Emergency Build 3.1.1
			if (null != loProposalDetailsBean)
			{
				String loProposalStatus = loProposalDetailsBean.getProposalStatusId();
				String loEvalGrpStatus = loProposalDetailsBean.getEvaluationGroupStatus();
				String loQuesVersion = loProposalDetailsBean.getQuesVersion();
				String loDocVersion = loProposalDetailsBean.getDocVersion();
				if (null != loProposalStatus)
				{
					if (loProposalStatus.equals(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
							HHSConstants.STATUS_PROPOSAL_SUBMITTED))
							&& null != loEvalGrpStatus
							&& loEvalGrpStatus.equals(PropertyLoader.getProperty(
									HHSConstants.PROPERTIES_STATUS_CONSTANT,
									HHSConstants.STATUS_EVALUATION_GROUP_RELEASED))
							&& (!loProposalDetailsBean.getLatestVersionQues().equals(HHSConstants.STRING_ZERO) && !loProposalDetailsBean.getLatestVersionQues().equals(loQuesVersion) || !loProposalDetailsBean
									.getLatestVersionDoc().equals(loDocVersion)))
					{
						lsControl
								.append("<img style='float: left' class='exclamationIcon' " +
										"src='../framework/skins/hhsa/images/exclamation.png' " +
										"alt='Exclamation' title='Exclamation'/>");
					}
				}
			}
		}
		return lsControl.toString();
	}

	/**
	 * This method is used to show drop down on the proposal summary page having
	 * proposal grid
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * @param loProposalDetailsBean ProposalDetailsBean object
	 * @param lsControl append string
	 * @param loNode element to be appended
	 * @throws ApplicationException If an exception occurs
	 */
	private void getControlForColumnFinal(ProposalDetailsBean loProposalDetailsBean, StringBuilder lsControl,
			Element loNode) throws ApplicationException
	{
		if (loNode.getValue() != null && loNode.getValue().equalsIgnoreCase(HHSConstants.STR_RETRACT_PROPOSAL))
		{
			if (loProposalDetailsBean.getEvaluationGroupStatus() != null
					&& loProposalDetailsBean.getEvaluationGroupStatus().equalsIgnoreCase(
							PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
									HHSConstants.STATUS_EVALUATION_GROUP_RELEASED)))
			{
				lsControl.append("<option value=\"" + loNode.getValue() + "\">" + loNode.getValue() + "</option>");
			}
		}
		else if (loNode.getValue() != null && loNode.getValue().equalsIgnoreCase(HHSConstants.STR_SUBMIT_PROPOSAL))
		{
			if (loProposalDetailsBean.getProcurementStatus() != null
					&& (loProposalDetailsBean.getProcurementStatus().equalsIgnoreCase(
							PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
									HHSConstants.STATUS_PROCUREMENT_RELEASED)) || loProposalDetailsBean
							.getProposalStatusId().equalsIgnoreCase(
									PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
											HHSConstants.STATUS_PROPOSAL_RETURNED_FOR_REVISION)))
			// && (null == loProposalDetailsBean.getRestrictSubmit() ||
			// HHSConstants.ZERO
			// .equalsIgnoreCase(loProposalDetailsBean.getRestrictSubmit()))
			)
			{
				lsControl.append("<option value=\"" + loNode.getValue() + "\">" + loNode.getValue() + "</option>");
			}
		}
		else if (loNode.getValue() != null && loNode.getValue().equalsIgnoreCase(HHSConstants.VIEW_EVALUATION_SCORE))
		{
			if ((loProposalDetailsBean.getIsOpenEndedRFP().equalsIgnoreCase(HHSConstants.ZERO) && loProposalDetailsBean
					.getProcurementStatus().equalsIgnoreCase(
							PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
									HHSConstants.STATUS_PROCUREMENT_CLOSED)))
					||

					(loProposalDetailsBean.getIsOpenEndedRFP().equalsIgnoreCase(HHSConstants.ONE) && (loProposalDetailsBean
							.getProposalStatusId().equalsIgnoreCase(
									PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
											HHSConstants.STATUS_PROPOSAL_SELECTED)) || loProposalDetailsBean
							.getProposalStatusId().equalsIgnoreCase(
									PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
											HHSConstants.STATUS_PROPOSAL_NOT_SELECTED)))))
			{
				lsControl.append("<option value=\"" + loNode.getValue() + "\">" + loNode.getValue() + "</option>");
			}
		}
		else
		{
			lsControl.append("<option value=\"" + loNode.getValue() + "\">" + loNode.getValue() + "</option>");
		}
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
		String lsControl = HHSConstants.RESUME;
		return lsControl;
	}
}
