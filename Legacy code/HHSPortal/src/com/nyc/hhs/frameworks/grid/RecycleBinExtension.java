package com.nyc.hhs.frameworks.grid;

import org.apache.commons.lang.StringUtils;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.model.Document;
import com.nyc.hhs.util.HHSUtil;

/**
 * Added for Release 5. It is extension file for recycle bin
 */
public class RecycleBinExtension implements DecoratorInterface
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
		Integer liSeqNo = aiSeqNo + 1;
		Boolean loIsCeoName = loDocumentBean.isMbIsCeoName();
		Boolean loIsCfoName = loDocumentBean.isMbIsCfoName();
		/*[Start] R7.2.0 QC8914	get indicator for Access control	 */
		String indecatorForReadOnlyRole = null;
		indecatorForReadOnlyRole = loDocumentBean.getUserSubRole();
		/*[End] R7.2.0 QC8914 get indicator for Access control     */ 
		
		// Defect #1805 fix, added check for Funder/Staff name
		Boolean lbIsStaffFundername = loDocumentBean.isMbIsStaffFundername();
		Boolean lbOrgFlag = true;
		String lsCurrentOrgId = loDocumentBean.getCurrentOrgId();
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
				// Adding code for R5
				if (StringUtils.isNotBlank(loDocumentBean.getDocCategory()))
				{
					lsControl = "<img width='20px' height='25px' style='padding-left:2px;' src=\"../framework/skins/hhsa/images/file_recyclebin.png\">&nbsp;"
							+ "<span style='bottom: 5px;position: relative;'>"
							+ HHSUtil.convertToWrappingWord(loDocumentBean.getDocName())
							+ "</span>";
				}
				else
				{
					lsControl = "<img src=\"../framework/skins/hhsa/images/folder-icon_blue.png\">&nbsp;"
							+ "<span style='bottom: 5px;position: relative;'>" + HHSUtil.convertToWrappingWord(loDocumentBean.getDocName())
							+ "</span>";
				}
			}
			else if (HHSR5Constants.DOC_ID.equalsIgnoreCase(aoCol.getColumnName()))
			{
				lsControl = "<input type=checkbox class=isChecked name=check id='checkbox'" + liSeqNo + " value='"
						+ loDocumentBean.getDocumentId() + ',' + loDocumentBean.getDocType() + ','
						+ loDocumentBean.getShareStatus() + "' onclick=\"javascript:enabledisablebutton('"
						+ loDocumentBean.getShareStatus() + "',this)\"/><input type=hidden name=sharestatus value='"
						+ loDocumentBean.getShareStatus() + "'\\>";
			}

			else if ("date".equalsIgnoreCase(aoCol.getColumnName()))
			{
				lsControl = loDocumentBean.getDeletedDate();
			}

			// Added for R5

			else if ("fileOptions".equals(aoCol.getColumnName()))
			{
				if ((null != loDocumentBean.getProviderId()	&& !lsCurrentOrgId.equalsIgnoreCase(loDocumentBean.getProviderId()))
						|| ApplicationConstants.ROLE_OBSERVER.equalsIgnoreCase(indecatorForReadOnlyRole))
				{
					lbOrgFlag = false;
				}
				// Added title or Defect # 7287

				if (loDocumentBean.isContractAccess())
				{   
					lsControl = "<div class='filemenuoptions'><img title='Options' src=\'../framework/skins/hhsa/images/fileoptions.png' class='fileoptions' onclick='displayFileOptionsRecycleBin(this,event,\""
							+ lbOrgFlag
							+ "\");'/>"
							+ "<div class='chatbox-content' id='chatbox-content-recyclebin' style='display:none;'><ul><li><a href='#' "
							+ "onclick=\"javascript: viewInfo("
							+ "'"
							+ loDocumentBean.getDocumentId()
							+ "'"
							+ ","
							+ "'"
							+ loDocumentBean.getDocType()
							+ "'"
							+ ",'"
							+ loDocumentBean.getDocCategory()
							+ "'"
							+ ")\"><img src=\'../framework/skins/hhsa/images/information_icon.png' alt='someimage'/><p>Information</p></a></li>"
							+ "<li><a href='#' onclick=\"javascript: restore("
							+ "'"
							+ loDocumentBean.getDocumentId()
							+ "'"
							+ ","
							+ "'"
							+ loDocumentBean.getDocType()
							+ "'"
							+ ")\"><img src=\'../framework/skins/hhsa/images/Restore_icon.PNG' alt='someimage'/><p>Restore</p></a></li>"
							+ "<li><a href='#' onclick=\"javascript: deleteForever("
							+ "'"
							+ loDocumentBean.getDocumentId()
							+ "'"
							+ ","
							+ "'"
							+ loDocumentBean.getDocType()
							+ "'"
							+ ")\"><img src=\'../framework/skins/hhsa/images/Delete_forever.PNG' alt='someimage'/><p>Delete Forever</p></a></li></ul></div></div>"
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
							+ ")\"><img src=\'../framework/skins/hhsa/images/information_icon.png' alt='someimage'/><p>Information</p></a></li></ul></div>";
				}
				else
				{
					lsControl = "<div class='filemenuoptions'><img title='Options' src=\'../framework/skins/hhsa/images/fileoptions.png' class='fileoptions' onclick='displayFileOptionsRecycleBin(this,event,\""
							+ lbOrgFlag
							+ "\");'/>"
							+ "<div class='chatbox-content' id='chatbox-content-recyclebin' style='display:none;'><ul><li><a href='#' "
							+ "onclick=\"javascript: viewInfo("
							+ "'"
							+ loDocumentBean.getDocumentId()
							+ "'"
							+ ","
							+ "'"
							+ loDocumentBean.getDocType()
							+ "'"
							+ ",'"
							+ loDocumentBean.getDocCategory()
							+ "'"
							+ ")\"><img src=\'../framework/skins/hhsa/images/information_icon.png' alt='someimage'/><p>Information</p></a></li>"
							+ "<li><a href='#' onclick=\"javascript: restore("
							+ "'"
							+ loDocumentBean.getDocumentId()
							+ "'"
							+ ","
							+ "'"
							+ loDocumentBean.getDocType()
							+ "'"
							+ ")\"><img src=\'../framework/skins/hhsa/images/Restore_icon.PNG' alt='someimage'/><p>Restore</p></a></li>"
							+ "<li><a href='#' onclick=\"javascript: deleteForever("
							+ "'"
							+ loDocumentBean.getDocumentId()
							+ "'"
							+ ","
							+ "'"
							+ loDocumentBean.getDocType()
							+ "'"
							+ ")\"><img src=\'../framework/skins/hhsa/images/Delete_forever.PNG' alt='someimage'/><p>Delete Forever</p></a></li></ul></div></div>"
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
							+ ")\"><img src=\'../framework/skins/hhsa/images/information_icon.png' alt='someimage'/><p>Information</p></a></li></ul></div>";
				}
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
