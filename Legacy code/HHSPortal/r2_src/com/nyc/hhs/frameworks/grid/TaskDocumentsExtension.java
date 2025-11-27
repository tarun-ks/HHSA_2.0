package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.model.ExtendedDocument;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

/**
 * This is a extension class for Task document functionality.
 */
public class TaskDocumentsExtension
{

	/**
	 * This method will generate html code for a particular column of table
	 * depending upon the input column name
	 * 
	 * @param aoEachObject an object of list to be displayed in grid
	 * @param aoCol a column object
	 * @param aiSeqNo an integer value of sequence number
	 * @return a string value of html code formed
	 * @throws ApplicationException object of type ApplicationException
	 */
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aiSeqNo) throws ApplicationException
	{   
		ExtendedDocument loFileUpload = (ExtendedDocument) aoEachObject;
		String lsAddendumType = loFileUpload.getAddendumType();
		String lsOrgType = loFileUpload.getOrganizationType();
		String lsOrgTypeInSession = (String) loFileUpload.getOrgTypeInSession();
		StringBuffer lsControlBuffer = new StringBuffer();

		if (HHSConstants.DOCUMENT_TITLE_LOWER_CASE.equalsIgnoreCase(aoCol.getColumnName()))
		{
			lsControlBuffer.append("<a href=\"#\" title='").append(loFileUpload.getDocumentTitle()).append("' alt='")
					.append(loFileUpload.getDocumentTitle()).append("'onclick=\"javascript: viewRFPDocument('")
					.append(loFileUpload.getDocumentId()).append("','").append(loFileUpload.getDocumentTitle())
					.append("' );\">").append(loFileUpload.getDocumentTitle()).append("</a>");
		}
		else if (HHSConstants.ACTIONS.equalsIgnoreCase(aoCol.getColumnName()))
		{   
			lsControlBuffer.append("<select name=documentDropdown class=terms  id=actions").append(aiSeqNo)
					.append(" style='width: 200px' onChange=\"javascript: actionDropDownChanged('")
					.append(loFileUpload.getDocumentId()).append("',this, '").append(loFileUpload.getDocumentTitle())
					.append("','").append(lsAddendumType).append("','").append(loFileUpload.getDocumentStatus())
					.append("','").append(loFileUpload.getDocumentSeq()).append("','")
					.append(loFileUpload.getTableName()).append("','").append(lsOrgType).append("','")
					// QC9614 R 9.3 Provider not able to remove document/view document/view document information for a budget document type Provider's Board Approved Budget
             		//.append(loFileUpload.getDocumentType()) //QC9614 R 9.3
					.append(StringEscapeUtils.escapeJavaScript(loFileUpload.getDocumentType())) //QC9614 R 9.3
					.append("')\"><option value=I need to... >I need to...</option>")
					.append("<option>View Document</option>").append("<option>View Document Information</option>");
			// condition added for release 6 to handle remove document option for return payment task
			if (lsOrgTypeInSession.equalsIgnoreCase(lsOrgType)) {
				// below condition will be executed when it will be called from any landing screen
				if (null == loFileUpload.getTaskName())
				{
					lsControlBuffer.append("<option>Remove Document</option>");
				}
				// below condition will be executed if it is called from a task detail screen
				else if(((HHSConstants.TASK_RETURN_PAYMENT_REVIEW.equalsIgnoreCase(loFileUpload.getTaskName())
					&& HHSConstants.ONE.equalsIgnoreCase(loFileUpload.getCurrentReviewLevel())) ||
					(!HHSConstants.TASK_RETURN_PAYMENT_REVIEW.equalsIgnoreCase(loFileUpload.getTaskName()))))
				{
						lsControlBuffer.append("<option>Remove Document</option>");
				}
				
			}
			lsControlBuffer.append("</select>");
		}
		else if ("documentType".equalsIgnoreCase(aoCol.getColumnName()))
		{	
			lsControlBuffer.append("<input type='hidden' value='");
			lsControlBuffer.append(loFileUpload.getDocumentType()); 
			lsControlBuffer.append("' id='docType' name='docType'/>");
			// Added for R5- docType value
			lsControlBuffer.append("<span value='");
			lsControlBuffer.append(loFileUpload.getDocumentType()); 
			lsControlBuffer.append("' id='docType' name='docType'>");
			lsControlBuffer.append(loFileUpload.getDocumentType());
			lsControlBuffer.append("</span>");
			// R5 ends
		}
		
		return lsControlBuffer.toString();
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
