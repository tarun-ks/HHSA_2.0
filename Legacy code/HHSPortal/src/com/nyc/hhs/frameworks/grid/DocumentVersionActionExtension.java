package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.model.Document;

/**
 * This class is used to generate an extension which creates a drop down 
 * for city_staff and city_manager.
 * 
 */

public class DocumentVersionActionExtension implements DecoratorInterface
{

	/**
	 * This Method is used to open the document
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
		String lsControl = "<select name=actions2 id=actions" + aiSeqNo + " style='width: auto' onChange=\"javascript: openDocument('"
				+ loFileUpload.getDocumentId() + "',this,'" + loFileUpload.getDocName() + "')\"><option value=I need to... >I need to...</option>"
				+ "<option value=View document >View Document</option>" + "<option>View Document Information</option>" + "</select>";
		return lsControl;
	}

	/**
	 * This Method is used to check all the check boxes if present.
	 * 
	 *  @param aoCol 
	 * 			column name
	 * @return String
	 */
	public String getControlForHeading(Column aoCol)
	{

		String lsScript = "RESUME";
		return lsScript;
	}

}
