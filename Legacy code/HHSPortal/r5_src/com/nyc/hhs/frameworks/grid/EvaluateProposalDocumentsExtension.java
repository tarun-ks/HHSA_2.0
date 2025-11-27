package com.nyc.hhs.frameworks.grid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.model.ExtendedDocument;

/**
 * Added for release 5 This class is used to generate html code for a particular
 * column of table depending upon the input column name
 */
public class EvaluateProposalDocumentsExtension
{
	/**
	 * This method will generate html code for a particular column of table
	 * depending upon the input column name
	 * 
	 * @param aoEachObject an object of list to be displayed in grid
	 * @param aoCol a column object
	 * @param aiSeqNo an integer value of sequence number
	 * @return a string value of html code formed
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aiSeqNo) throws ApplicationException
	{
		ExtendedDocument loProposalDocumentBean = (ExtendedDocument) aoEachObject;
		String lsProposalDocStatusId = loProposalDocumentBean.getStatusId();
		Channel loChannelObj = new Channel();
		loChannelObj.setData(HHSConstants.PROCUREMENT_STATUS, loProposalDocumentBean.getStatus());
		StringBuffer loControlBuffer = new StringBuffer();
		if (HHSConstants.DOCUMENT_TITLE_LOWER_CASE.equalsIgnoreCase(aoCol.getColumnName()))
		{
			if (StringUtils.isNotBlank(loProposalDocumentBean.getDocumentTitle()))
			{
				if (loProposalDocumentBean.getVisibility().equalsIgnoreCase(HHSConstants.ONE))
				{
					loControlBuffer.append("<a href=\"javascript:void(0);\" id= '")
							.append(loProposalDocumentBean.getDocumentId())
							.append("' class='localTabs' name='taskArrow' ")
							.append("onclick=\"javascript: viewRFPDocument('")
							.append(loProposalDocumentBean.getDocumentId()).append("','")
							.append(loProposalDocumentBean.getDocumentTitle()).append("' );\">")
							.append(loProposalDocumentBean.getDocumentTitle()).append("</a>");
				}
				else
				{
					loControlBuffer.append(loProposalDocumentBean.getDocumentTitle());
				}
			}
			else if (StringUtils.isNotBlank(loProposalDocumentBean.getProposalTitle()))
			{
				loControlBuffer.append("<a href=\"javascript:void(0);\" id='")
						.append(loProposalDocumentBean.getProposalId()).append("' class='localTabs' name='taskArrow' ")
						.append("onclick=\"javascript: viewProposalSummary('")
						.append(loProposalDocumentBean.getProposalId()).append("','")
						.append(loProposalDocumentBean.getProcurementId()).append("' );\">")
						.append(loProposalDocumentBean.getProposalTitle()).append("</a>");
			}
			else
			{
				loControlBuffer.append(HHSConstants.NA_KEY);
			}
		}
		else if (HHSConstants.ACTIONS.equalsIgnoreCase(aoCol.getColumnName()))
		{
			HHSGridUtil.createActionDropDown(aiSeqNo, loProposalDocumentBean, lsProposalDocStatusId, loControlBuffer);
		}
		else if (HHSConstants.DOC_ID.equalsIgnoreCase(aoCol.getColumnName()))
		{
			if (StringUtils.isBlank(loProposalDocumentBean.getDocumentId())
					|| loProposalDocumentBean.getDocumentId().equalsIgnoreCase(HHSConstants.NA_KEY))
			{
				loControlBuffer.append(HHSConstants.NA_KEY);
			}
			else
			{
				loControlBuffer
						.append("<a style=\"cursor:pointer\" class='localTabs' onclick=\"javascript: viewDocumentInfo('")
						.append(loProposalDocumentBean.getDocumentId()).append("','")
						.append(loProposalDocumentBean.getDocumentType()).append("',this);\">").append("Info</a>");
			}
		}
		else if (HHSConstants.ASSIGN_STATUS.equalsIgnoreCase(aoCol.getColumnName()))
		{
			String loQuesVersion = loProposalDocumentBean.getQuesVersion();
			String loDocVersion = loProposalDocumentBean.getDocVersion();
			String loEvalGroupQuesVersion = loProposalDocumentBean.getEvalGrpQuesVersion();
			String loEvalGroupDocVersion = loProposalDocumentBean.getEvalGrpDocVersion();
			List<String> loStatusList = new ArrayList<String>();
			loStatusList.add(HHSConstants.EMPTY_STRING);
			loStatusList.add(HHSConstants.STATUS_VERIFIED);
			loStatusList.add(HHSConstants.RETURNED);
			loStatusList.add(HHSConstants.STATUS_NON_RESPONSIVE);
			Iterator<String> loIterator = loStatusList.iterator();
			loControlBuffer.append("<select class='selectReturned' name='assignedstatus' id='assignedstatus" + aiSeqNo
					+ "' onChange='javascript:disableFinishButtonOnChange(this);'>");
			while (loIterator.hasNext())
			{
				String lsStatus = (String) loIterator.next();
				// Modified as a part of release 3.1.0 for enhancement request
				// 6024
				// Further modified to solve Release Addendum Issue in Emergency
				// Build 3.1.1
				if (lsStatus.equals(HHSConstants.STATUS_VERIFIED)
						&& (loEvalGroupQuesVersion != null && !loEvalGroupQuesVersion.equalsIgnoreCase(loQuesVersion) || !loEvalGroupDocVersion
								.equalsIgnoreCase(loDocVersion)) && null != loProposalDocumentBean.getProposalTitle()
						&& !HHSConstants.EMPTY_STRING.equals(loProposalDocumentBean.getProposalTitle()))
				{
					// Made changes for enhancement 6467 release 3.4.0
					if (loProposalDocumentBean.getRequiredQuesDocCount() != 0)
					{
						continue;
					}
				}
				if (StringUtils.isNotBlank(loProposalDocumentBean.getProposalTitle()))
				{
					if (null != loProposalDocumentBean.getAssignStatus()
							&& !loProposalDocumentBean.getAssignStatus().equals(HHSConstants.EMPTY_STRING)
							&& loProposalDocumentBean.getAssignStatus().trim().equalsIgnoreCase(lsStatus.trim()))
					{
						loControlBuffer.append("<option value='").append(loProposalDocumentBean.getProposalTitle())
								.append("##").append(lsStatus).append("##")
								.append(loProposalDocumentBean.getDocumentStatus()).append("##")
								.append(loProposalDocumentBean.getProposalTitle()).append("' selected >")
								.append(lsStatus).append("</option>");
					}
					else
					{
						loControlBuffer.append("<option value='").append(loProposalDocumentBean.getProposalTitle())
								.append("##").append(lsStatus).append("##")
								.append(loProposalDocumentBean.getDocumentStatus()).append("##")
								.append(loProposalDocumentBean.getProposalTitle()).append("'>").append(lsStatus)
								.append("</option>");
					}
				}
				else if (StringUtils.isNotBlank(loProposalDocumentBean.getDocumentId()))
				{
					if (StringUtils.isNotBlank(loProposalDocumentBean.getAssignStatus())
							&& loProposalDocumentBean.getAssignStatus().trim().equalsIgnoreCase(lsStatus.trim()))
					{
						loControlBuffer.append("<option value='")
								.append(loProposalDocumentBean.getProcurementDocumentId()).append("##")
								.append(lsStatus).append("##").append(loProposalDocumentBean.getDocumentStatus())
								.append("##").append(loProposalDocumentBean.getDocumentTitle()).append("' selected >")
								.append(lsStatus).append("</option>");
					}
					else
					{
						loControlBuffer.append("<option value='")
								.append(loProposalDocumentBean.getProcurementDocumentId()).append("##")
								.append(lsStatus).append("##").append(loProposalDocumentBean.getDocumentStatus())
								.append("##").append(loProposalDocumentBean.getDocumentTitle()).append("'>")
								.append(lsStatus).append("</option>");
					}
				}
				else
				{
					if (lsStatus.equals(HHSConstants.STATUS_VERIFIED))
					{
						continue;
					}
					else if (null != loProposalDocumentBean.getAssignStatus()
							&& !loProposalDocumentBean.getAssignStatus().isEmpty()
							&& loProposalDocumentBean.getAssignStatus().trim().equalsIgnoreCase(lsStatus.trim()))
					{
						loControlBuffer.append("<option value='")
								.append(loProposalDocumentBean.getProcurementDocumentId()).append("##")
								.append(lsStatus).append("##").append(loProposalDocumentBean.getDocumentStatus())
								.append("##").append(loProposalDocumentBean.getDocumentTitle()).append("' selected >")
								.append(lsStatus).append("</option>");
					}
					else
					{
						loControlBuffer.append("<option value='")
								.append(loProposalDocumentBean.getProcurementDocumentId()).append("##")
								.append(lsStatus).append("##").append(loProposalDocumentBean.getDocumentStatus())
								.append("##").append(loProposalDocumentBean.getDocumentTitle()).append("'>")
								.append(lsStatus).append("</option>");
					}
				}
			}
			loControlBuffer.append("</select>");
			loControlBuffer.append("<input type='hidden' id='prevSelectedVal' value='")
					.append(loProposalDocumentBean.getAssignStatus()).append("' />");
		}
		else if (HHSConstants.LAST_MODIFIED_BY_NAME.equalsIgnoreCase(aoCol.getColumnName()))
		{
			if (null == loProposalDocumentBean.getDocumentId() || loProposalDocumentBean.getDocumentId().isEmpty())
			{
				loControlBuffer.append(HHSConstants.NA_KEY);
			}
			else
			{
				loControlBuffer.append(loProposalDocumentBean.getLastModifiedByName());
			}
		}
		else if (HHSConstants.MODIFIED_DATE.equalsIgnoreCase(aoCol.getColumnName()))
		{
			if (null == loProposalDocumentBean.getDocumentId() || loProposalDocumentBean.getDocumentId().isEmpty())
			{
				loControlBuffer.append(HHSConstants.NA_KEY);
			}
			else
			{
				loControlBuffer.append(loProposalDocumentBean.getModifiedDate());
			}
		}
		return loControlBuffer.toString();
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
