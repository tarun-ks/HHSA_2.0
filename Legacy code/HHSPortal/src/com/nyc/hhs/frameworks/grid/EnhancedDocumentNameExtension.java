/**
 * 
 */
package com.nyc.hhs.frameworks.grid;


import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.model.Document;
import com.nyc.hhs.util.HHSUtil;

/**
 * This class is used to generate an extension which creates a drop down for
 * provider users.
 * 
 */

public class EnhancedDocumentNameExtension implements DecoratorInterface 
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
	@Override
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aiSeqNo)
	{
		Document loDocumentBean = (Document) aoEachObject;
	    Boolean lbOrgFlag = true;
		String lsCurrentOrgId = null;
		lsCurrentOrgId = loDocumentBean.getCurrentOrgId();
		/*[Start] R7.2.0 QC8914	get indicator for Access control	 */
		String indecatorForReadOnlyRole = null;
		indecatorForReadOnlyRole = loDocumentBean.getUserSubRole();
		/*[End] R7.2.0 QC8914 get indicator for Access control     */ 

		String lsControl = "";
		Integer liSeqNo = aiSeqNo + 1;
		Boolean loIsCeoName = loDocumentBean.isMbIsCeoName();
		Boolean loIsCfoName = loDocumentBean.isMbIsCfoName();
		// Defect #1805 fix, added check for Funder/Staff name
		Boolean lbIsStaffFundername = loDocumentBean.isMbIsStaffFundername();
				
		if ((loIsCeoName || loIsCfoName || lbIsStaffFundername) && null == loDocumentBean.getDocName())
		{
			if (loIsCeoName)
			{
				lsControl = loDocumentBean.getMsCeoName();
			}
			if (loIsCfoName)
			{
				lsControl = loDocumentBean.getMsCfoName();
			}
			if (lbIsStaffFundername)
			{
				lsControl = loDocumentBean.getMsServiceDocumentName();
			}
		}
		else
		{
			// Release 5 Contract Restriction
			if ("docName".equals(aoCol.getColumnName()) && null != loDocumentBean.getDocName())
			{
				// Adding code for R5
				if (null == loDocumentBean.getDocCategory())
				{
					lsControl = "<span class='folderImage'></span>&nbsp;<a href=\"#\"  style='top:4px;position: relative;' title='"
							+ loDocumentBean.getDocName()
							+ "' alt='"
							+ loDocumentBean.getShareStatus()
							+ "'onclick=\"javascript: selectTreeNodeForOpenFolder("
							+ "'"
							+ loDocumentBean.getDocumentId() + "' );\">" + HHSUtil.convertToWrappingWord(loDocumentBean.getDocName()) + "</a>";
				}
				else
				{
					if (loDocumentBean.isContractAccess() && loDocumentBean.isUserAccess())
					{
						lsControl = "<span class='documentImage'></span>&nbsp;<a href=\"#\" style='top:4px;position: relative;' title='"
								+ loDocumentBean.getDocName()
								+ "' alt='"
								+ loDocumentBean.getShareStatus()
								+ "'onclick=\"javascript: viewDocument("
								+ "'"
								+ loDocumentBean.getDocumentId()
								+ "','"
								+ loDocumentBean.getDocName() + "' );\">" + HHSUtil.convertToWrappingWord(loDocumentBean.getDocName()) + "</a>";
					}
					else
					{
						lsControl = "<img width='20px' height='25px' style='margin-left: 6px;' src=\"../framework/skins/hhsa/images/file-black.png\">"
								+ "<span title='You do not have access to this document. Contact your Account Administrator for access'>&nbsp;&nbsp;"
								+ loDocumentBean.getDocName() + "</span>";
					}
				}
			}
			// Release 5 Contract Restriction
			else if ("documentId".equalsIgnoreCase(aoCol.getColumnName()) && loDocumentBean.isContractAccess()
					&& loDocumentBean.isUserAccess())
			{
				lsControl = "<input type=checkbox class=isChecked name=check id='checkbox'" + liSeqNo + " value='"
						+ loDocumentBean.getDocumentId() + ',' + loDocumentBean.getDocType() + ','
						+ loDocumentBean.getSharedEntityId() + ',' + loDocumentBean.getShareStatus()
						+ "' onclick=\"javascript:enabledisablebutton('" + loDocumentBean.getShareStatus()
						+ "',this)\"/><input type=hidden name=sharestatus value='" + loDocumentBean.getShareStatus()
						+ "'\\>";
			}
			else if ("shareStatus".equals(aoCol.getColumnName()))
			{

				if ((null != loDocumentBean.getProviderId()
						&& !lsCurrentOrgId.equalsIgnoreCase(loDocumentBean.getProviderId()))
						|| ApplicationConstants.ROLE_OBSERVER.equalsIgnoreCase(indecatorForReadOnlyRole))
				{
					lbOrgFlag = false;
				}
				if (StringUtils.isNotBlank(loDocumentBean.getShareStatus()))
				{
					if (loDocumentBean.getShareStatus().equalsIgnoreCase("2"))
					{
						String lsToolTip = "This folder is currently shared";
						if (null != loDocumentBean.getDocCategory())
						{
							lsToolTip = "This document is currently shared";
						}
						lsControl = "<a href=\"#\" class='sharedStatusFull' title='" + "' alt='"
								+ "'onclick=\"javascript: sharedStatus(" + "'"
								+ loDocumentBean.getDocumentId()
								+ "'"
								+ ","
								+ "'"
								+ loDocumentBean.getDocType()
								+ "'"
								+ ","
								+ "'"
								+ loDocumentBean.getDocName()
								+ "'"
								+ ","
								+ "'"
								+ loDocumentBean.getShareStatus()
								+ "','"
								+ lbOrgFlag
								+ "'"
								+ ","
								+ "'"
								+ loDocumentBean.getSharedEntityId()
								+ "')\"><img title='"
								+ lsToolTip
								+ "' src=\"../framework/skins/hhsa/images/shared.png\">" + "</a>";
					}
					else if (loDocumentBean.getShareStatus().equalsIgnoreCase("1"))
					{
						if (StringUtils.isNotBlank(loDocumentBean.getDocCategory()))
						{
							lsControl = "<a href=\"#\" title='" + "' alt='" + "'onclick=\"javascript: sharedStatus("
									+ "'"
									+ loDocumentBean.getDocumentId()
									+ "'"
									+ ","
									+ "'"
									+ loDocumentBean.getDocType()
									+ "'"
									+ ","
									+ "'"
									+ loDocumentBean.getDocName()
									+ "'"
									+ ","
									+ "'"
									+ loDocumentBean.getShareStatus()
									+ "'"
									+ ","
									+ "'"
									+ lbOrgFlag
									+ "'"
									+ ","
									+ "'"
									+ loDocumentBean.getSharedEntityId()
									+ "'"
									+ ")\"><img title='This document is currently shared' src=\"../framework/skins/hhsa/images/shared.png\">"
									+ "</a>";
						}
						else
						{
							lsControl = "<img title='A document in this folder is currently shared' src='../framework/skins/hhsa/images/emptyShare.png' class='sharedStatusShallow' >";
						}
					}
				}
				else
				{
					// Changes for R5
					lsControl = "";
				}
			}
			// Added for R5
			else if ("linkStatus".equals(aoCol.getColumnName()))
			{
				// Need to check linked status in below if condition
				if (loDocumentBean.getDocType() == null && loDocumentBean.isLinkToApplication())
				{
					lsControl = "<img title='A document within this folder is linked to a submitted item' src=\"../framework/skins/hhsa/images/linked.png\">";
				}
				else if (loDocumentBean.isLinkToApplication())
				{
					lsControl = "<a href=\"#\" title='This document is linked to a submitted item'" + "' alt='"
							+ "'onclick=\"javascript: linkStatus(" + "'" + loDocumentBean.getDocumentId() + "'" + ","
							+ "'" + StringEscapeUtils.escapeJavaScript(loDocumentBean.getDocType()) + "'" + "," + "'"
							+ loDocumentBean.isContractAccess() + "'"
							+ ")\"><img src=\"../framework/skins/hhsa/images/linked.png\">" + "</a>";
					
				}
			}
			// Release 5 Contract Restriction
			else if ("fileOptions".equals(aoCol.getColumnName()) && loDocumentBean.isContractAccess()
					&& loDocumentBean.isUserAccess())
			{
				// Need to check restricted status in below if condition
				// R7.2 QC8914 read only role
				// add condition to setup lbOrgFlag to false
				if ( (null != loDocumentBean.getProviderId()&& !lsCurrentOrgId.equalsIgnoreCase(loDocumentBean.getProviderId())) 
						|| ApplicationConstants.ROLE_OBSERVER.equalsIgnoreCase(indecatorForReadOnlyRole) )
				{
					lbOrgFlag = false;
				}
					lsControl = "<div class='filemenuoptions'><img title='Options' src=\'../framework/skins/hhsa/images/fileoptions.png' class='fileoptions' onclick='displayFileOptions(this,event,\""
						+ lbOrgFlag
						+ "\");'/>"
						+ "<div class='chatbox-content' id='chatbox-content' style='display:none;'><ul><li><a href='#' onclick=\"javascript: viewInfo("
						+ "'"
						+ loDocumentBean.getDocumentId()
						+ "'"
						+ ","
						+ "'"
						+ StringEscapeUtils.escapeJavaScript(loDocumentBean.getDocType())
						+ "'"
						+ ","
						+ "'"
						+ StringEscapeUtils.escapeJavaScript(loDocumentBean.getDocCategory())
						+ "'"
						+ ","
						+ "'"
						+ loDocumentBean.getOrganizationId()
						+ "'"
						+ ","
						+ "'"
						+ loDocumentBean.getProviderId()
						+ "'"
						+ ","
						+ "'"
						+ lbOrgFlag
						+ "'"
						+ ")\"><img src=\'../framework/skins/hhsa/images/information_icon.png' alt='someimage'/><p>Information</p></a></li><hr align='center' width='80%'>"
						+ "<li><a href='#' onclick=\"javascript: move("
						+ "'"
						+ loDocumentBean.getDocumentId()
						+ "'"
						+ ","
						+ "'"
						+ StringEscapeUtils.escapeJavaScript(loDocumentBean.getDocType())
						+ "'"
						+ ")\"><img src=\'../framework/skins/hhsa/images/Black_Move_icon.png' alt='someimage'/><p>Move<p></a></li>";
				if (null != loDocumentBean.getSharedEntityId()
						&& (loDocumentBean.getSharedEntityId().equalsIgnoreCase(loDocumentBean.getDocumentId())))
				{
					lsControl = lsControl
							+ "<li class='showShareUnshare'><a id='shareDoc'class='anchoralign' href='#' onclick=\"javascript: shareEntityInfo("
							+ "'"
							+ loDocumentBean.getDocumentId()
							+ "'"
							+ ","
							+ "'"
							+ StringEscapeUtils.escapeJavaScript(loDocumentBean.getDocType())
							+ "'"
							+ ","
							+ "'"
							+ loDocumentBean.getSharedEntityId()
							+ "'"
							+ ")\"><img src=\'../framework/skins/hhsa/images/share_icon.png' alt='someimage'/><p>Share</p></a></li><hr align='center' width='80%'>";
				}
				else if (null == loDocumentBean.getSharedEntityId())
				{
					lsControl = lsControl
							+ "<li class='showShareUnshare'><a id='shareDoc'class='anchoralign' href='#' onclick=\"javascript: shareEntityInfo("
							+ "'"
							+ loDocumentBean.getDocumentId()
							+ "'"
							+ ","
							+ "'"
							+ StringEscapeUtils.escapeJavaScript(loDocumentBean.getDocType())
							+ "'"
							+ ","
							+ "'"
							+ loDocumentBean.getSharedEntityId()
							+ "'"
							+ ")\"><img src=\'../framework/skins/hhsa/images/share_icon.png' alt='someimage'/><p>Share</p></a></li><hr align='center' width='80%'>";
				}
				lsControl = lsControl
						+ "<li><a href='#' onclick=\"javascript: deleteData("
						+ "'"
						+ loDocumentBean.getDocumentId()
						+ "'"
						+ ","
						+ "'"
						+ loDocumentBean.getDocType()
						+ "'"
						+ ","
						+ "'"
						+ loDocumentBean.getShareStatus()
						+ "'"
						+ ")\"><img src=\'../framework/skins/hhsa/images/delete_icon.png' alt='someimage'/><p>Delete</p></a></li></ul></div></div>"
						+ "<div class='chatbox-content-shared' id='chatbox-content' style='display:none;'><ul><li><a href='#' onclick=\"javascript: viewInfo("
						+ "'"
						+ loDocumentBean.getDocumentId()
						+ "'"
						+ ","
						+ "'"
						+ StringEscapeUtils.escapeJavaScript(loDocumentBean.getDocType())
						+ "'"
						+ ","
						+ "'"
						+ StringEscapeUtils.escapeJavaScript(loDocumentBean.getDocCategory())
						+ "'"
						+ ","
						+ "'"
						+ loDocumentBean.getOrganizationId()
						+ "'"
						+ ","
						+ "'"
						+ loDocumentBean.getProviderId()
						+ "'"
						+ ","
						+ "'"
						+ lbOrgFlag
						+ "'"
						+ ")\"><img src=\'../framework/skins/hhsa/images/information_icon.png' alt='someimage'/><p>Information</p></a></li></ul></div>";
			}
			// End
		}
		return lsControl;
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
		String lsControlTxt = "RESUME";

		return lsControlTxt;
	}
}
