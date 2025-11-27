package com.nyc.hhs.frameworks.grid;

import org.apache.commons.lang.StringEscapeUtils;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.model.ExtendedDocument;

/**
 * This class generates an extension element in the Grid which displays the
 * documents selected by the user from Document Vault via Business Application
 * 
 */

public class AddDocumentFromVaultExtension implements DecoratorInterface
{
	/**
	 * This method is used to create radio button on the grid which is displayed
	 * when a user select document from vault on the document screen and set the
	 * hidden values to be passed.
	 * @param aoEachObject an object of list to be displayed in grid
	 * @param aoCol a column object
	 * @param aiSeqNo an integer value of sequence number
	 * 
	 * @return a string value of html code formed
	 */

	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aiSeqNo) throws ApplicationException
	{
		ExtendedDocument loDocumentBean = (ExtendedDocument) aoEachObject;
		String lsControl = HHSConstants.EMPTY_STRING;
		if (HHSConstants.DOCUMENT_TITLE.equalsIgnoreCase(aoCol.getColumnName())
				&& null != loDocumentBean.getDocumentTitle())
		{
			lsControl = "<div id="
					+ aiSeqNo
					+ "><input type=radio name=radio id=radio onclick=\"javascript: setHiddenParams('"
					+ StringEscapeUtils.escapeJavaScript(loDocumentBean.getDocumentType())
					+ "','"
					+ loDocumentBean.getDocumentCategory()
					+ "','"
					+ loDocumentBean.getLastModifiedById()
					+ "','"
					+ loDocumentBean.getModifiedDate()
					+ "','"
					+ loDocumentBean.getCreatedDate()
					+ "','"
					+ loDocumentBean.getCreatedBy()
					+ "','"
					+ loDocumentBean.getDocumentTitle()
					+ "','"
					+ loDocumentBean.getDocumentId()
					+ "','"
					+ loDocumentBean.getFilePath()
					+ "')\" value='"
					+ loDocumentBean.getDocumentId()
					+ "'>"
					+ "<img width='18px' height='20px' style='margin-left: 3px;' src=\"../framework/skins/hhsa/images/file.png\">"
					+ "<a style='top:-5px;position: relative;left: 5px;'href=\"#\" onclick=\"javascript: viewDocument("
					+ "'" + loDocumentBean.getDocumentId() + "','" + loDocumentBean.getDocumentTitle() + "');\">"
					+ loDocumentBean.getDocumentTitle() + "</a>" + "</input></div>";
		}
		// Added for Release 5 - getting File path of document
		if ("filePath".equalsIgnoreCase(aoCol.getColumnName()) && null != loDocumentBean.getFilePath()
				&& !loDocumentBean.getFilePath().isEmpty())
		{
			String lsPath = "";
			if (null != loDocumentBean.getFilePath() && !loDocumentBean.getFilePath().isEmpty())
			{
				lsPath = loDocumentBean.getFilePath().substring(
						loDocumentBean.getFilePath().indexOf("/" + HHSR5Constants.DOCUMENT_VAULT),
						loDocumentBean.getFilePath().length());
			}
			// Fixed Defect #7609
			lsControl = ("<label style='float:left' title=\"" + lsPath.replace("/", "\\") + "\" value='"
					+ loDocumentBean.getFilePath().substring(loDocumentBean.getFilePath().lastIndexOf("/") + 1) + "'>"
					+ loDocumentBean.getFilePath().substring(loDocumentBean.getFilePath().lastIndexOf("/") + 1) + "</label>");
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

		return HHSConstants.RESUME;
	}

}
