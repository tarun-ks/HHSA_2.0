package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.model.Document;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.HHSUtil;

/**
 * This class performs the shared search operation
 */
public class SharedSearchExtension implements DecoratorInterface
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
		String lsControl = "";
		aiSeqNo = aiSeqNo + 1;
		Boolean loIsCeoName = loDocumentBean.isMbIsCeoName();
		Boolean loIsCfoName = loDocumentBean.isMbIsCfoName();
		Boolean lbOrgFlag = false;
		String lsCurrentOrgId = loDocumentBean.getCurrentOrgId();
		/*[Start] R7.2.0 QC8914	get indicator for Access control	 */
		String indecatorForReadOnlyRole = null;
		indecatorForReadOnlyRole = loDocumentBean.getUserSubRole();
		
		/*[End] R7.2.0 QC8914 get indicator for Access control     */ 
		
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
			if ("docName".equals(aoCol.getColumnName()) && null != loDocumentBean.getDocName())
			{

				if (null != loDocumentBean.getDocCategory())
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
						lsControl = "<img width='18px' height='20px' style='margin-left: 3px;' src=\"../framework/skins/hhsa/images/file-black.png\">"
								+ "<span title='You do not have access to this document. Contact your Account Administrator for access'>&nbsp;&nbsp;"
								+ HHSUtil.convertToWrappingWord(loDocumentBean.getDocName()) + "</span>";
					}
				}
				else
				{
					lsControl = "<span class='folderImage'></span>&nbsp;<a href=\"#\"  style='top:4px;position: relative;' title='"
							+ loDocumentBean.getDocName()
							+ "' alt='"
							+ loDocumentBean.getShareStatus()
							+ "'onclick=\"javascript: selectTreeNodeForOpenFolder("
							+ "'"
							+ loDocumentBean.getDocumentId() + "' );\">" + HHSUtil.convertToWrappingWord(loDocumentBean.getDocName()) + "</a>";
				}
			}

			else if ("date".equalsIgnoreCase(aoCol.getColumnName()))
			{
				lsControl = loDocumentBean.getDate();
			}

			else if ("OrgName".equalsIgnoreCase(aoCol.getColumnName()))
			{
				lsControl = loDocumentBean.getOrgName();
			}

			// Added for R5

			else if ("fileOptions".equals(aoCol.getColumnName()))
			{
                if ((null != loDocumentBean.getProviderId()	&& !lsCurrentOrgId.equalsIgnoreCase(loDocumentBean.getProviderId()) ) 
						|| ApplicationConstants.ROLE_OBSERVER.equalsIgnoreCase(indecatorForReadOnlyRole))
				{
					lbOrgFlag = false;
				}
				lsControl = "<div class='filemenuoptions'><img title='Options' src=\'../framework/skins/hhsa/images/fileoptions.png' class='fileoptions' onclick='displayFileOptionsRecycleBin(this,event,\""
						+ lbOrgFlag
						+ "\");'>"
						+ "<div class='chatbox-content-shared' id='chatbox-content' style='display:none;'><ul><li><a href='#' onclick=\"javascript: viewInfo("
						+ "'"
						+ loDocumentBean.getDocumentId()
						+ "'"
						+ ","
						+ "'"
						+ loDocumentBean.getDocType()
						+ "'"
						+ ","
						+ "'"
						+ loDocumentBean.getDocCategory()
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
						+ ")\"><img src=\'../framework/skins/hhsa/images/information_icon.png' alt='someimage'/><p>Information</p></a></li></ul></div></div>";
				/*
				 * else { lsControl = ""; }
				 */
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
