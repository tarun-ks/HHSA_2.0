/**
 * 
 */
package com.nyc.hhs.frameworks.grid;

import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.model.EvaluationBean;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.XMLUtil;

public class EvaluationResultsExtension implements DecoratorInterface
{
	/**
	 * This method will generate html code for a particular column of table
	 * depending upon the input column name
	 * 
	 * @param aoEachObject an object of list to be displayed in grid
	 * @param aoCol a column object
	 * @param aiSeqNo an integer value of sequence number
	 * @return a string value of html code formed
	 * @throws ApplicationException
	 */
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aiSeqNo) throws ApplicationException
	{
		StringBuffer loControl = new StringBuffer();
		String loAwardReviewStatus = HHSConstants.NA;
		EvaluationBean loEvaluationBean = (EvaluationBean) aoEachObject;
		if (aoCol.getColumnName().equalsIgnoreCase(HHSConstants.ACTIONS))
		{

			Document loDoc;
			String lsXPathTop = HHSConstants.EMPTY_STRING;
			String lsXPathTopwithAll = HHSConstants.EMPTY_STRING;
			String lsIsPrevApproved = HHSConstants.FALSE;
			try
			{
				loDoc = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
						HHSConstants.EVALUATION_RESULTS_ROLE_MAPPING);
				if (loEvaluationBean.getAwardReviewStatusId() == null)
				{
					loEvaluationBean.setAwardReviewStatusId(loAwardReviewStatus);
				}
				if (loEvaluationBean.getAwardApprovalDate() != null)
				{
					lsIsPrevApproved = HHSConstants.TRUE;
				}
				if (loEvaluationBean.getApprovedStatus() != null
						&& loEvaluationBean.getApprovedStatus().equalsIgnoreCase(HHSConstants.YES_LOWERCASE))
				{
					lsXPathTop = "//page[(@name=\"evaluationResults\")]/awardReview[(contains(@status,\""
							+ loEvaluationBean.getAwardReviewStatusId() + "\") and contains(@role,\""
							+ loEvaluationBean.getUserRole() + "\") and (@isPreviouslySelected=\""
							+ isPreviouslySelected(loEvaluationBean) + "\") " + "and (@isProviderApproved=\"true\")"
							+ "and (@isPrevApproved=\"" + lsIsPrevApproved + "\"))]/proposal[(contains(@status,\""
							+ loEvaluationBean.getProposalStatusId() + "\"))]/action";

					lsXPathTopwithAll = "//page[(@name=\"evaluationResults\")]/awardReview[(contains(@status,\""
							+ "all" + "\") and contains(@role,\"" + "all" + "\"))]/proposal[(contains(@status,\""
							+ loEvaluationBean.getProposalStatusId() + "\"))]/action";
				}
				else
				{
					lsXPathTop = "//page[(@name=\"evaluationResults\")]/awardReview[(contains(@status,\""
							+ loEvaluationBean.getAwardReviewStatusId() + "\") and contains(@role,\""
							+ loEvaluationBean.getUserRole() + "\") " + "and (@isProviderApproved=\"false\")"
							+ "and (@isPreviouslySelected=\"" + isPreviouslySelected(loEvaluationBean)
							+ "\") and (@isPrevApproved=\"" + lsIsPrevApproved + "\"))]/proposal[(contains(@status,\""
							+ loEvaluationBean.getProposalStatusId() + "\"))]/action";

					lsXPathTopwithAll = "//page[(@name=\"evaluationResults\")]/awardReview[(contains(@status,\""
							+ "all" + "\") and contains(@role,\"" + "all" + "\"))]/proposal[(contains(@status,\""
							+ loEvaluationBean.getProposalStatusId() + "\"))]/action";
				}

				List<Element> loNodeList = XMLUtil.getElementList(lsXPathTop, loDoc);
				List<Element> loNodeList1 = XMLUtil.getElementList(lsXPathTopwithAll, loDoc);

				loControl.append("<select class=terms name=actions1 id='actions" + loEvaluationBean.getRowNum() + "'")
						.append(" style='width: 190px' onChange=\"javascript: processAction('")
						.append(loEvaluationBean.getProposalId()).append("',this)")
						.append("\"><option>I need to...</option>");
				if (!(loEvaluationBean.getProcurementStatus().equalsIgnoreCase(
						PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_PROCUREMENT_CLOSED)) || loEvaluationBean.getProcurementStatus()
						.equalsIgnoreCase(
								PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
										HHSConstants.STATUS_PROCUREMENT_CANCELLED))))
				{
					for (Element loNode : loNodeList)
					{
						loControl.append("<option value=\"" + loNode.getValue() + "\">" + loNode.getValue()
								+ "</option>");
					}
				}
				for (Element loNode : loNodeList1)
				{
					loControl.append("<option value=\"" + loNode.getValue() + "\">" + loNode.getValue() + "</option>");
				}
				loControl.append("</select>");
			}
			catch (Exception aoExp)
			{
				throw new ApplicationException("ErrorOccured while crewating control for Evaluation Result", aoExp);
			}
		}
		else if (aoCol.getColumnName().equalsIgnoreCase(HHSConstants.PROPOSAL_TITLE))
		{
			loControl.append("<a href=\"javascript:void(0)\" ")
					.append("class='localTabs' onclick=\"javascript: viewProposalDetail('")
					.append(loEvaluationBean.getProposalId()).append("');\">")
					.append(loEvaluationBean.getProposalTitle()).append("</a>");
		}
		else if (HHSConstants.EVAL_SUMMARY.equals(aoCol.getColumnName()))
		{
			loControl
					.append("<a href=\"#\" alt='Evaluation Summary' class='localTabs' onclick=\"javascript: viewEvaluationSummary('")
					.append(loEvaluationBean.getProposalId()).append("');\">Evaluation Summary</a>");
		}
		// R5 change starts
		else if ("negotiatedAmount".equals(aoCol.getColumnName()))
		{
			if (null != loEvaluationBean.getNegotiatedAmount())
			{
				loControl
						.append("<input type ='hidden' class = 'selectionComments' name = 'selectionComments' value=\'"
								+ loEvaluationBean.getComments()
								+ "\'> <a href=\"#\" class='localTabs editAmountLink'><label class='tableAwardAmount bluelink linkPointer'>"
								+ loEvaluationBean.getNegotiatedAmount() + "</label></a>");

			}
			else
			{
				loControl
						.append("<input type ='hidden' class = 'selectionComments' name = 'selectionComments' value=\'"
								+ loEvaluationBean.getComments()
								+ "\'> <a href=\"#\" class='localTabs editAmountLink'><label class='tableAwardAmount bluelink linkPointer'>Edit Amount</label></a>");

			}
		}
		// R5 change ends
		else if (HHSConstants.COMMENTS.equals(aoCol.getColumnName()))
		{
			loControl.append("<a href=\"#\" alt='Comments' class='localTabs' onclick=\"javascript: viewAccComments('")
					.append(loEvaluationBean.getProposalId()).append("');\">Comments</a>");
		}
		else if (HHSConstants.EVALUATION_SCORE_STR.equals(aoCol.getColumnName()))
		{
			loControl.append(loEvaluationBean.getEvaluationScore());
		}
		else if (aoCol.getColumnName().equalsIgnoreCase(HHSConstants.AWARD_AMOUNT))
		{
			if (null != loEvaluationBean.getAwardAmount()
					&& !loEvaluationBean.getAwardAmount().equals(HHSConstants.STRING_MINUS_ONE))
			{
				loControl.append("<label class='tableAwardAmount'>").append(loEvaluationBean.getAwardAmount())
						.append("</label>");
				// R5 changes starts
				if (null != loEvaluationBean.getIsPendingNegotiationFlag()
						&& loEvaluationBean.getIsPendingNegotiationFlag().equalsIgnoreCase(HHSConstants.YES))
				{
					loControl
							.append("<span class='red-ex-mark' title='This is the preliminary award amount. Final award amount is pending'/>");
				}
				// R5 changes ends
			}
			else
			{
				loControl.append(HHSConstants.DOUBLE_UNDER_SCORE);
			}
		}
		else if (aoCol.getColumnName().equalsIgnoreCase(HHSConstants.PROPOSAL_STATUS))
		{
			if (loEvaluationBean.getAwardApprovalDate() != null)
			{
				// Start || Changes done for enhancement 6574 for Release 3.10.0
				if (loEvaluationBean.getAwardReviewStatus() != null
						&& (loEvaluationBean.getAwardReviewStatusId().equalsIgnoreCase(
								PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
										HHSConstants.STATUS_AWARD_REVIEW_UPDATE_IN_PROGRESS))
								|| loEvaluationBean.getAwardReviewStatusId().equalsIgnoreCase(
										PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
												HHSConstants.STATUS_AWARD_REVIEW_IN_REVIEW)) || loEvaluationBean
								.getAwardReviewStatusId().equalsIgnoreCase(
										PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
												HHSConstants.STATUS_AWARD_REVIEW_RETURNED))))
				{
					if (loEvaluationBean.getProposalStatus().equalsIgnoreCase(HHSConstants.PROPOSAL_STATUS_SELECTED)
							&& loEvaluationBean.getApprovedStatus() != null
							&& loEvaluationBean.getApprovedStatus().equalsIgnoreCase(HHSConstants.YES_LOWERCASE))
					{
						loControl.append(loEvaluationBean.getProposalStatus()).append(HHSConstants.DOUBLE_STAR);
					}
					else if (loEvaluationBean.getProposalStatus().equalsIgnoreCase(
							HHSConstants.PROPOSAL_STATUS_NOT_SELECTED)
							&& loEvaluationBean.getApprovedStatus() != null
							&& loEvaluationBean.getApprovedStatus().equalsIgnoreCase(HHSConstants.YES_LOWERCASE))
					{
						loControl.append(loEvaluationBean.getProposalStatus()).append(HHSConstants.DOUBLE_STAR);
					}
					else if (loEvaluationBean.getProposalStatus().equalsIgnoreCase(
							HHSConstants.PROPOSAL_STATUS_SELECTED)
							&& !loEvaluationBean.getModifiedFlag().equalsIgnoreCase(HHSConstants.ONE)
							&& loEvaluationBean.getApprovedStatus() != null
							&& loEvaluationBean.getApprovedStatus().equalsIgnoreCase(HHSConstants.NO))
					{
						loControl.append(loEvaluationBean.getProposalStatus()).append(HHSConstants.DOUBLE_STAR);
					}
					else if (loEvaluationBean.getProposalStatus().equalsIgnoreCase(
							HHSConstants.PROPOSAL_STATUS_SELECTED)
							&& loEvaluationBean.getModifiedFlag().equalsIgnoreCase(HHSConstants.ONE)
							&& loEvaluationBean.getApprovedStatus() != null
							&& loEvaluationBean.getApprovedStatus().equalsIgnoreCase(HHSConstants.NO))
					{
						loControl.append(loEvaluationBean.getProposalStatus()).append(HHSConstants.STAR);
					}
					else if (loEvaluationBean.getProposalStatus().equalsIgnoreCase(
							HHSConstants.PROPOSAL_STATUS_NOT_SELECTED)
							&& loEvaluationBean.getModifiedFlag().equalsIgnoreCase(HHSConstants.ONE)
							&& loEvaluationBean.getApprovedStatus() != null
							&& loEvaluationBean.getApprovedStatus().equalsIgnoreCase(HHSConstants.NO))
					{
						loControl.append(loEvaluationBean.getProposalStatus()).append(HHSConstants.STAR);
					}
					else if (loEvaluationBean.getProposalStatus().equalsIgnoreCase(
							HHSConstants.PROPOSAL_STATUS_NOT_SELECTED)
							&& loEvaluationBean.getModifiedFlag().equalsIgnoreCase(HHSConstants.ZERO)
							&& loEvaluationBean.getApprovedStatus() != null
							&& loEvaluationBean.getApprovedStatus().equalsIgnoreCase(HHSConstants.NO))
					{
						loControl.append(loEvaluationBean.getProposalStatus());
					}
				}
				else if (loEvaluationBean.getAwardReviewStatus() != null
						&& (loEvaluationBean.getAwardReviewStatusId().equalsIgnoreCase(PropertyLoader.getProperty(
								HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_AWARD_REVIEW_UPDATE_IN_PROGRESS_TEMP))))
				{
					if (loEvaluationBean.getProposalStatus().equalsIgnoreCase(HHSConstants.PROPOSAL_STATUS_SELECTED)
							&& loEvaluationBean.getApprovedStatus() != null
							&& loEvaluationBean.getApprovedStatus().equalsIgnoreCase(HHSConstants.YES_LOWERCASE)
							&& !loEvaluationBean.getModifiedFlag().equalsIgnoreCase(HHSConstants.ONE))
					{
						loControl.append(loEvaluationBean.getProposalStatus()).append(HHSConstants.DOUBLE_STAR);
					}
					else if (loEvaluationBean.getProposalStatus().equalsIgnoreCase(
							HHSConstants.PROPOSAL_STATUS_SELECTED)
							&& loEvaluationBean.getApprovedStatus() != null
							&& loEvaluationBean.getApprovedStatus().equalsIgnoreCase(HHSConstants.YES_LOWERCASE)
							&& loEvaluationBean.getModifiedFlag().equalsIgnoreCase(HHSConstants.ONE))
					{
						loControl.append(loEvaluationBean.getProposalStatus()).append(HHSConstants.STAR);
					}

					else if (loEvaluationBean.getProposalStatus().equalsIgnoreCase(
							HHSConstants.PROPOSAL_STATUS_SELECTED)
							&& loEvaluationBean.getModifiedFlag().equalsIgnoreCase(HHSConstants.ONE)
							&& loEvaluationBean.getApprovedStatus() != null
							&& loEvaluationBean.getApprovedStatus().equalsIgnoreCase(HHSConstants.NO))
					{
						loControl.append(loEvaluationBean.getProposalStatus()).append(HHSConstants.STAR);
					}

					else if (loEvaluationBean.getProposalStatus().equalsIgnoreCase(
							HHSConstants.PROPOSAL_STATUS_NOT_SELECTED))
					{
						loControl.append(loEvaluationBean.getProposalStatus());
					}
				}
				else
				{
					loControl.append(loEvaluationBean.getProposalStatus());
				}
			}
			// End || Changes done for enhancement 6574 for Release 3.10.0
			else
			{
				loControl.append(loEvaluationBean.getProposalStatus());
			}
		}
		return loControl.toString();
	}
	/**
	 * This method is used to retrieve previously selected value
	 * @param aoEvaluationBean
	 * @return lsIsPrevSelected
	 */
	private String isPreviouslySelected(EvaluationBean aoEvaluationBean)
	{
		String lsIsPrevSelected = HHSConstants.FALSE;
		if (aoEvaluationBean.getAwardApprovalDate() != null
				&& ((aoEvaluationBean.getProposalStatus().equalsIgnoreCase(HHSConstants.PROPOSAL_STATUS_NOT_SELECTED)
						&& aoEvaluationBean.getModifiedFlag().equalsIgnoreCase(HHSConstants.ONE) && aoEvaluationBean
						.getApprovedStatus().equalsIgnoreCase(HHSConstants.YES_LOWERCASE)) || (aoEvaluationBean
						.getProposalStatus().equalsIgnoreCase(HHSConstants.PROPOSAL_STATUS_SELECTED) && aoEvaluationBean
						.getApprovedStatus().equalsIgnoreCase(HHSConstants.YES_LOWERCASE))))
		{
			lsIsPrevSelected = HHSConstants.TRUE;
		}
		return lsIsPrevSelected;
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
}
