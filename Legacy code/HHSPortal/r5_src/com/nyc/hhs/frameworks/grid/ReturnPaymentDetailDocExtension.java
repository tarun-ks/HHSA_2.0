package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.model.ExtendedDocument;

/**
 * This class was created to fix defect # 8645.It is 
 * responsible for handling extension level things from Returned Payment
 * Detail Screen.
 *
 */
public class ReturnPaymentDetailDocExtension {


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
					.append(loFileUpload.getDocumentType())
					.append("')\"><option value=I need to... >I need to...</option>")
					.append("<option>View Document</option>").append("<option>View Document Information</option>");
			lsControlBuffer.append("</select>");
		}
		else if (HHSConstants.DOCUMENT_TYPE.equalsIgnoreCase(aoCol.getColumnName()))
		{
			lsControlBuffer.append("<input type='hidden' value='");
			lsControlBuffer.append(loFileUpload.getDocumentType());
			lsControlBuffer.append("' id='docType' name='docType'/>");
			lsControlBuffer.append("<span value='");
			lsControlBuffer.append(loFileUpload.getDocumentType());
			lsControlBuffer.append("' id='docType' name='docType'>");
			lsControlBuffer.append(loFileUpload.getDocumentType());
			lsControlBuffer.append("</span>");
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
