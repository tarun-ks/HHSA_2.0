package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.model.ExtendedDocument;
import com.nyc.hhs.rule.Rule;
import com.nyc.hhs.util.PropertyLoader;

public class RFPReleaseDocumentsExtension
{

	/**
	 * This method will generate html code for a particular column of table
	 * depending upon the input column name
	 * <ul>
	 * <li>Based on the column name values it will set the options of dropdown
	 * on interface</li>
	 * 
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * @param aoEachObject an object of list to be displayed in grid
	 * @param aoCol a column object
	 * @param aiSeqNo an integer value of sequence number
	 * @return a string value of html code formed throws ApplicationException
	 * 
	 */
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aiSeqNo) throws ApplicationException
	{
		ExtendedDocument loFileUpload = (ExtendedDocument) aoEachObject;
		String lsAddendumType = loFileUpload.getAddendumType();
		String lsProcurementStatusId = null;
		String lsProcurementReleasedStatus = null;
		boolean lbIsRfpDocScreenReadOnly = false;
		Channel loChannelObj = new Channel();
		lsProcurementStatusId = loFileUpload.getProcurementStatusId();
		lsProcurementReleasedStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_PROCUREMENT_RELEASED);
		loChannelObj.setData(HHSConstants.PROCUREMENT_STATUS, loFileUpload.getStatus());
		lbIsRfpDocScreenReadOnly = Boolean.valueOf((String) Rule.evaluateRule(HHSConstants.RFP_DOC_SCREEN_READ_ONLY,
				loChannelObj));
		StringBuffer loControlBuffer = new StringBuffer();
		if (null != loFileUpload)
		{
			if (HHSConstants.DOCUMENT_TITLE_LOWER_CASE.equalsIgnoreCase(aoCol.getColumnName()))
			{
				loControlBuffer.append("<a href=\"#\" ").append("onclick=\"javascript: viewRFPDocument('")
						.append(loFileUpload.getDocumentId()).append("','").append(loFileUpload.getDocumentTitle())
						.append("' );\">").append(loFileUpload.getDocumentTitle()).append("</a>");
			}
			else if (HHSConstants.ACTIONS.equalsIgnoreCase(aoCol.getColumnName()))
			{
				//Start of changes for defect : 6235
				if (!lbIsRfpDocScreenReadOnly && 
						loFileUpload.getOrganizationType().equalsIgnoreCase(ApplicationConstants.CITY_ORG))
				{
					loControlBuffer.append("<select class=terms name=actions1 id=actions").append(aiSeqNo)
							.append(" style='width: 200px' onChange=\"javascript: actionDropDownChanged('")
							.append(loFileUpload.getDocumentId()).append("',this, '")
							.append(loFileUpload.getDocumentTitle()).append("','").append(lsAddendumType).append("','")
							.append(loFileUpload.getDocumentStatus()).append("','")
							.append(loFileUpload.getReferenceDocSeqNo()).append("','")
							.append(loFileUpload.getDocumentType())
							.append("','false')\"><option value=I need to... >I need to...</option>")
							.append("<option>View Document</option>")
							.append("<option>View Document Information</option>");
					if (loFileUpload.getOrganizationType().equalsIgnoreCase(ApplicationConstants.CITY_ORG))
					{
						loControlBuffer.append("<option>Remove Document from List</option>").append("</select>");
					}
					else
					{
						loControlBuffer.append("</select>");
					}
				}
				else
				{
					createActionDropdownForEditableDocuments(aiSeqNo, loFileUpload, lsAddendumType,
							lsProcurementStatusId, lsProcurementReleasedStatus, loControlBuffer);
				}
				//End of changes for defect : 6235
			}
		}
		return loControlBuffer.toString();
	}

	/**
	 * This method creates Dropdown For Editable Documents
	 * <ul>
	 * <li>
	 * If the condition is true then based on the Procurement status id the
	 * values in dropdown is set.</li>
	 * <li>
	 * Otherwise else condition is executed which states that if asAddendumType
	 * equals to zero then the values in dropdown is set.</li>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * @param aiSeqNo Seq No
	 * @param aoFileUpload File Upload
	 * @param asAddendumType Addendum Type
	 * @param asProcurementStatusId Procurement StatusId
	 * @param asProcurementReleasedStatus Procurement Released Status
	 * @param aoControlBuffer Control Buffer throws ApplicationException
	 * 
	 */
	private void createActionDropdownForEditableDocuments(Integer aiSeqNo, ExtendedDocument aoFileUpload,
			String asAddendumType, String asProcurementStatusId, String asProcurementReleasedStatus,
			StringBuffer aoControlBuffer) throws ApplicationException
	{

		String lsReadonlyVal = null;
		if (HHSConstants.DRAFT.equalsIgnoreCase(aoFileUpload.getDocumentStatus()))
		{
			lsReadonlyVal = "false";
		}
		else
		{
			lsReadonlyVal = "true";
		}
		if (aoFileUpload.getOrganizationType().equalsIgnoreCase(ApplicationConstants.CITY_ORG))
		{
			if (asProcurementStatusId.equalsIgnoreCase(asProcurementReleasedStatus))
			{
				aoControlBuffer
						.append("<select class=terms name=actions1 id=actions")
						.append(aiSeqNo)
						.append(" style='width: 200px' onChange=\"javascript: actionDropDownChanged('")
						.append(aoFileUpload.getDocumentId())
						.append("',this, '")
						.append(aoFileUpload.getDocumentTitle())
						.append("','")
						.append(asAddendumType)
						.append("','")
						.append(aoFileUpload.getDocumentStatus())
						.append("','")
						.append(aoFileUpload.getReferenceDocSeqNo())
						.append("','")
						.append(aoFileUpload.getDocumentType())
						.append("','" + lsReadonlyVal + "')\"><option value=I need to... >I need to...</option>")
						.append("<option>View Document</option>")
						.append("<option>View Document Information</option>");
				
				if (aoFileUpload.getDocumentStatus().equalsIgnoreCase(HHSConstants.DRAFT)){
					aoControlBuffer.append("<option>Remove Document from List</option>") ;
				} else {
					aoControlBuffer.append("<option>Replace Document From Vault</option><option>Replace Document By Uploading New Document</option>");
				}
				aoControlBuffer.append("</select>");
			}
			else
			{
				if (asAddendumType.equals(HHSConstants.ZERO))
				{
					aoControlBuffer.append("<select class=terms name=actions1 id=actions").append(aiSeqNo)
							.append(" style='width: 200px' onChange=\"javascript: actionDropDownChanged('")
							.append(aoFileUpload.getDocumentId()).append("',this, '")
							.append(aoFileUpload.getDocumentTitle()).append("','").append(asAddendumType).append("','")
							.append(aoFileUpload.getDocumentStatus()).append("','")
							.append(aoFileUpload.getReferenceDocSeqNo()).append("','")
							.append(aoFileUpload.getDocumentType())
							.append("','true')\"><option value=I need to... >I need to...</option>")
							.append("<option>View Document</option>")
							.append("<option>View Document Information</option>").append("</select>");
				}
				else
				{
					aoControlBuffer.append("<select class=terms name=actions1 id=actions").append(aiSeqNo)
							.append(" style='width: 200px' onChange=\"javascript: actionDropDownChanged('")
							.append(aoFileUpload.getDocumentId()).append("',this, '")
							.append(aoFileUpload.getDocumentTitle()).append("','").append(asAddendumType).append("','")
							.append(aoFileUpload.getDocumentStatus()).append("','")
							.append(aoFileUpload.getReferenceDocSeqNo()).append("','")
							.append(aoFileUpload.getDocumentType())
							.append("','false')\"><option value=I need to... >I need to...</option>")
							.append("<option>View Document</option>")
							.append("<option>View Document Information</option>")
							.append("<option>Remove Document from List</option>");
					
					if (aoFileUpload.getDocumentStatus().equalsIgnoreCase(HHSConstants.DRAFT)){
						aoControlBuffer.append("<option>Remove Document from List</option>") ;
					}
					aoControlBuffer.append("</select>");
				}
			}
		}
		else
		{
			aoControlBuffer.append("<select class=terms name=actions1 id=actions").append(aiSeqNo)
					.append(" style='width: 200px' onChange=\"javascript: actionDropDownChanged('")
					.append(aoFileUpload.getDocumentId()).append("',this, '").append(aoFileUpload.getDocumentTitle())
					.append("','").append(asAddendumType).append("','").append(aoFileUpload.getDocumentStatus())
					.append("','").append(aoFileUpload.getReferenceDocSeqNo()).append("','")
					.append(aoFileUpload.getDocumentType())
					.append("','true')\"><option value=I need to... >I need to...</option>")
					.append("<option>View Document</option>").append("<option>View Document Information</option>")
					.append("</select>");
		}
	}

	/**
	 * This method will generate html code for a particular column header of
	 * table depending upon the input column name
	 * 
	 * @param aoCol a column object
	 * 
	 * @return lsControl a string value of html code formed
	 */
	public String getControlForHeading(Column aoCol)
	{
		String lsControl = HHSConstants.RESUME;
		return lsControl;
	}

}
