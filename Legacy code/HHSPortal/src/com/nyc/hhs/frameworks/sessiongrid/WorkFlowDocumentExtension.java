package com.nyc.hhs.frameworks.sessiongrid;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.model.WorkFlowDetailBean;


/**
 * This class generates an extension which creates a link which 
 * forwards the users to the selected documents and forms.
 *
 */

public class WorkFlowDocumentExtension implements DecoratorInterface{
	/**
	 * This Method is used to generate the links on the SubSection Forms and Documents 
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
 		if(null != loWorkFlowBean.getMsQuestionDocumentName()){
 			String lsQuesDocName=loWorkFlowBean.getMsQuestionDocumentName();
 			if(lsQuesDocName.equalsIgnoreCase(ApplicationConstants.SERVICE_SETTING))
 			{
 				lsQuesDocName=ApplicationConstants.SERVICE_SETTING_DISPLAYNAME;
 			}
 			lsControl = "<a style='text-transform: capitalize' name=\"taskArrow\" id='"+loWorkFlowBean.getMsDocOrSectionID()+"'"+" class=\"taskNormal\" href=\"#\" title='"+loWorkFlowBean.getMsQuestionDocumentName()+"' onclick=\"javascript: openDocument(" +
			"'"+loWorkFlowBean.getMsDocOrSectionID()+"','"+loWorkFlowBean.getMsQuestionDocumentName()+"');\">"+lsQuesDocName+"</a>";
 		}	
		return lsControl;
	}
	
	/**
	 * This Method is used to check all the check boxes if present.
	 * 
	 * @param aoCol 
	 * 			column name
	 * @return String
	 */
	public String getControlForHeading(Column aoCol)
	{
		
		String lsString = "RESUME";	
		return lsString;
	}
	
}
