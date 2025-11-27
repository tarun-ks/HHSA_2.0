/**
 * 
 */
package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.model.Document;

/**
 * This class is used to generate an extension which creates a drop down for
 * provider users.
 * 
 */

public class DocumentNameExtension implements DecoratorInterface
{
	/**
	 * This method will generate html code for a particular column of table
	 * depending upon the input column name
	 * 
	 * @param aoEachObject
	 *            an object of list to be displayed in grid
	 * @param aoCol
	 *            a column object
	 * @param aiSeqNo
	 *            an integer value of sequence number
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
		//Defect #1805 fix, added check for Funder/Staff name
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
			if(lbIsStaffFundername)
			{
				lsControl = loDocumentBean.getMsServiceDocumentName();
			}
		}
		else
		{
			if ("docName".equals(aoCol.getColumnName()) && null != loDocumentBean.getDocName())
			{
				if(loDocumentBean.isUserAccess())
				{
					lsControl = "<a href=\"#\" title='" + loDocumentBean.getDocName() + "' alt='" + loDocumentBean.getShareStatus()
							+ "'onclick=\"javascript: viewDocument(" + "'" + loDocumentBean.getDocumentId() + "','" + loDocumentBean.getDocName()
							+ "' );\">" + loDocumentBean.getDocName() + "</a>";
				}
				else
				{
					lsControl = loDocumentBean.getDocName();
				}
				
			}
			else if ("documentId".equalsIgnoreCase(aoCol.getColumnName()))
			{
				lsControl = "<input type=checkbox name=check id=checkbox" + aiSeqNo + " value=" + loDocumentBean.getDocumentId()
						+ " onclick=\"javascript:enabledisablebutton('" + loDocumentBean.getShareStatus()
						+ "',this)\"/><input type=hidden name=sharestatus value='" + loDocumentBean.getShareStatus() + "'\\>";
			}
			else if ("shareStatus".equals(aoCol.getColumnName()))
			{
				if (loDocumentBean.getShareStatus().equalsIgnoreCase("Shared"))
				{
					lsControl = "<a href=\"#\" class=\"linkclass\" title='" + loDocumentBean.getShareStatus() + "' alt='"
							+ loDocumentBean.getShareStatus() + "' id='" + loDocumentBean.getDocumentId() + "' name='" + loDocumentBean.getDocName()
							+ "'>" + loDocumentBean.getShareStatus() + "</a>";
				}
				else
				{
					lsControl = loDocumentBean.getShareStatus();
				}
			}
			else if ("actions".equals(aoCol.getColumnName()))
			{
				lsControl = "<a href=\"#\" onclick=\"javascript: viewDocument(" + "'" + loDocumentBean.getDocumentId() + "','"
						+ loDocumentBean.getDocName() + "');\">View Sample</a>";
			}
		}
		return lsControl;
	}

	/**
	 * This method will generate html code for a particular column header of
	 * table depending upon the input column name
	 * 
	 * @param aoCol
	 *            a column object
	 * 
	 * @return a string value of html code formed
	 */
	public String getControlForHeading(Column aoCol)
	{
		return HHSConstants.RESUME;
	}
}
