package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.model.Document;

/**
 * This class generates an extension that creates drop-down options
 * corresponding to the document.
 * 
 */

public class DocumentActionExtension implements DecoratorInterface
{

	/**
	 * This method will generate html code for a particular column of table
	 * depending upon the input column name
	 * <ul>
	 * <li> Method Updated in R4 </li>
	 * </ul>
	 * @param aoEachObject
	 *            an object of list to be displayed in grid
	 * @param aoCol
	 *            a column object
	 * @param aiSeqNo
	 *            an integer value of sequence number
	 * @return a string value of html code formed

	 */
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aiSeqNo)
	{
		Document loFileUpload = (Document) aoEachObject;
		String lsUserOrg = loFileUpload.getUserOrg();
		String lsHtmlScript = "";
		if (ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(lsUserOrg))
		{
			lsHtmlScript = "<select name=actions1 id=actions" + aiSeqNo + " style='width: auto' onChange=\"javascript: openDocument('"
					+ loFileUpload.getDocumentId() + "',this, '" + loFileUpload.getDocName()
					+ "')\"><option value=I need to... >I need to...</option>" + "<option>View Document</option>"
					+ "<option>View Document Information</option>" + "<option>Delete Document</option>" + "</select>";
		}
		else if (ApplicationConstants.CITY_ORG.equalsIgnoreCase(lsUserOrg))
		{
			lsHtmlScript = "<select class=terms name=actions2 id=actions" + aiSeqNo + " style='width: auto' onChange=\"javascript: openDocument('"
					+ loFileUpload.getDocumentId() + "',this, '" + loFileUpload.getDocName()
					+ "')\"><option value=I need to... >I need to...</option>" + "<option>View Document</option>"
					+ "<option>View Document Information</option>"
					+ "<option>Delete Document</option>" + "</select>";
		}
		//R4 Document Vault changes: Removing 'View Version History' and 'Upload New Version' option from action dropdown
		// on City Document Vault screen.
		else{
			lsHtmlScript = "<select name=actions1 id=actions" + aiSeqNo + " style='width: auto' onChange=\"javascript: openDocument('"
			+ loFileUpload.getDocumentId() + "',this, '" + loFileUpload.getDocName()
			+ "')\"><option value=I need to... >I need to...</option>" + "<option>View Document</option>"
			+ "<option>View Document Information</option>" + "<option>Delete Document</option>" + "</select>";
			
		}
		
		return lsHtmlScript;
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
		String lsHTMLControl = "RESUME";
		return lsHTMLControl;
	}
}
