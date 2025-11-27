/**
 * 
 */
package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.model.Document;

/**
 * This class generates an extension that creates drop-down options
 * corresponding to the document.
 * 
 */

public class SharedDocumentActionExtension implements DecoratorInterface
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
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aiSeqNo)
	{
		Document loFileUpload = (Document) aoEachObject;
		String lsControl = "<select name=actions1 id=actions" + aiSeqNo + " style='width: auto' onChange=\"javascript: openDocument('"
					+ loFileUpload.getDocumentId() + "',this, '" + loFileUpload.getDocName()
					+ "')\"><option value=I need to... >I need to...</option>" + "<option>View Document</option>"
					+ "<option>View Document Information</option></select>";
		
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
		String lsMsg = "RESUME";
		return lsMsg;
	}
}
