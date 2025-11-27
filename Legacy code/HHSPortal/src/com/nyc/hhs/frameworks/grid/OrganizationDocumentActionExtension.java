package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.model.Document;

/**
 *  This class generates an extension which creates a drop-down
 *  list for showing document's version history.
 * 
 */

public class OrganizationDocumentActionExtension
{
	
	/** 
	 * This Method is used to open the document.
	 * 
	 *  @param aoEachObject
	 * 				Bean Name
	 * @param aoCol
	 * 			Column name
	 * @param aiSeqNo 
	 * 			Sequence Number
	 * @return String
	 * 				
	 */
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aiSeqNo)
	{
		Document loFileUpload = (Document) aoEachObject;
		
		String lsStr = "<select name=actions1 id=actions" + aiSeqNo + " style='width: auto' onChange=\"javascript: openDocument('"
				+ loFileUpload.getDocumentId() + "',this,'" + loFileUpload.getDocName() + "')\"><option value=I need to... >I need to...</option>"
				+ "<option>View Document</option>" + "<option>View Document Information</option>" + "</select>";
		return lsStr;
	}

	/**
	 * This Method is used to check all the check boxes if present.
	 * @param aoCol 
	 * 			column name
	 * @return String
	 */
	public String getControlForHeading(Column aoCol)
	{
		String lsMsg = "RESUME";
		return lsMsg;
	}
}



