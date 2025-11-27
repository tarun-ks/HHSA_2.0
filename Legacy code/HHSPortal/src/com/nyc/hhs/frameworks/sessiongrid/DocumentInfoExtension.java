package com.nyc.hhs.frameworks.sessiongrid;

import com.nyc.hhs.model.WorkFlowDetailBean;


/**
 * This class generates an extension which creates a link that
 * will display the information of the selected document.
 *
 */

public class DocumentInfoExtension implements DecoratorInterface{
	
	/**
	 * This Method is used to generate the Info link
	 * 
	 * @param aoEachObject
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
		WorkFlowDetailBean loWorkFlowBean = (WorkFlowDetailBean)aoEachObject;
 		String lsControl = "";
 		if(null != loWorkFlowBean.getMsDocType()&& !loWorkFlowBean.getMsDocType().isEmpty()){
 			lsControl = "<a href=\"#\" title='Info' onclick=\"javascript: openInfo(" +
			"'"+loWorkFlowBean.getMsDocOrSectionID()+"','"+loWorkFlowBean.getMsQuestionDocumentName()+"');\">"+"Info"+"</a>";
 			return lsControl;
 		}	
 		else{
 			lsControl="N/A";
 		}
		return lsControl;
	}
	
	/**
	 * This Method is used to check all the checkboxes if present.
	 * 
	 * @param aoCol 
	 * 			column name
	 * @return String
	 */
	public String getControlForHeading(Column aoCol)
	{
		
		String lsControl = "RESUME";	
		return lsControl;
	}
}
