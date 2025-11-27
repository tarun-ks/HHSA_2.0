package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.model.ExtendedDocument;
import com.nyc.hhs.util.PropertyLoader;

public class AwardDocumentExtention
{
	/**
	 * This method will generate html code for a particular column of table
	 * depending upon the input column name
	 * 
	 * @param aoEachObject an object of list to be displayed in grid
	 * @param aoCol a column object
	 * @param aiSeqNo an integer value of sequence number
	 * @return a string value of html code formed
	 */
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aiSeqNo) throws ApplicationException
	{
		ExtendedDocument loProposalDocumentBean = (ExtendedDocument) aoEachObject;
		String lsProposalDocStatusId = loProposalDocumentBean.getStatusId();
		Channel loChannelObj = new Channel();
		loChannelObj.setData(HHSConstants.PROCUREMENT_STATUS, loProposalDocumentBean.getStatus());
		StringBuffer loControlBuffer = new StringBuffer();
		if (HHSConstants.DOCUMENT_TITLE.equalsIgnoreCase(aoCol.getColumnName()))
		{
			if (loProposalDocumentBean.getDocumentTitle() != null)
			{
				loControlBuffer.append("<a href=\"#\"").append(" onclick=\"javascript: viewRFPDocument('")
						.append(loProposalDocumentBean.getDocumentId()).append("','")
						.append(loProposalDocumentBean.getDocumentTitle()).append("' );\">")
						.append(loProposalDocumentBean.getDocumentTitle()).append("</a>");
			}
			else if (loProposalDocumentBean.getProposalTitle() != null)
			{
				loControlBuffer.append("<a style=\"cursor:pointer\" onclick=\"javascript: viewProposalSummary('")
						.append(loProposalDocumentBean.getProposalId()).append("','")
						.append(loProposalDocumentBean.getProcurementId()).append("' );\">")
						.append(loProposalDocumentBean.getProposalTitle()).append("</a>");
			}
		}
		else if (HHSConstants.ACTIONS.equalsIgnoreCase(aoCol.getColumnName()))
		{
			createActionDropDown(aiSeqNo, loProposalDocumentBean, lsProposalDocStatusId, loControlBuffer);
		}
		else if (HHSConstants.DOC_ID.equalsIgnoreCase(aoCol.getColumnName()))
		{
			if (loProposalDocumentBean.getDocumentId() != null
					&& !loProposalDocumentBean.getDocumentId().equalsIgnoreCase(HHSConstants.NA_KEY))
			{
				loControlBuffer.append("<a style=\"cursor:pointer\" onclick=\"javascript: viewDocumentInfo('")
						.append(loProposalDocumentBean.getDocumentId()).append("',this);\">").append("Info</a>");
			}
			else
			{
				loControlBuffer.append(loProposalDocumentBean.getDocumentId());
			}
		}
		return loControlBuffer.toString();
	}

	/**
	 * Modified By - Tanuj Mudgal This Method is changed to fix defect 5523 for
	 * release 2.6.0
	 * 
	 * This method is used to create the drop down while creating table via grid
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * @param aiSeqNo sequence number
	 * @param aoProposalDocumentBean proposal document bean
	 * @param asProposalDocStatusId proposal document status id
	 * @param ascontrolBuffer String butter for drop down
	 * @throws ApplicationException when any exception occured
	 */
	private void createActionDropDown(Integer aiSeqNo, ExtendedDocument aoProposalDocumentBean,
			String asProposalDocStatusId, StringBuffer ascontrolBuffer) throws ApplicationException
	{
		String lsUserRole = aoProposalDocumentBean.getUserRole();
		String lsProcurementStatusId = aoProposalDocumentBean.getProcurementStatusId();
		String lsProcurementCancelledStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_PROCUREMENT_CANCELLED);
		if (null != asProposalDocStatusId)
		{
			if (asProposalDocStatusId.equalsIgnoreCase(PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_DOCUMENT_NOT_STARTED)))
			{
				if (!((HHSConstants.STAFF_ROLE.equalsIgnoreCase(lsUserRole) || ApplicationConstants.ROLE_ADMINISTRATOR_PROV_STAFF
						.equalsIgnoreCase(lsUserRole))))
				{
					if (!lsProcurementStatusId.equals(lsProcurementCancelledStatus))
					{
						// Release 5 User Notification
						if (aoProposalDocumentBean.isUserAccess())
						{
							// 5523 fix starts
							ascontrolBuffer.append("<select class=terms name=actions1 id=actions").append(aiSeqNo)
									.append(" style='width: 200px' onChange=\"javascript: actionDropDownChanged('")
									.append(aoProposalDocumentBean.getDocumentId()).append("',this, '")
									.append(aoProposalDocumentBean.getDocumentTitle()).append("','")
									.append(aoProposalDocumentBean.getDocumentStatus()).append("','")
									.append(aoProposalDocumentBean.getReferenceDocSeqNo()).append("','")
									.append(aoProposalDocumentBean.getDocumentType()).append("','")
									.append(aoProposalDocumentBean.getIsRequiredDoc())
									.append("')\"><option>I need to...</option>")
									.append("<option>Upload Document</option>")
									.append("<option>Select Document from Vault</option>").append("</select>");
							// 5523 fix ends
						}
						else
						{
							ascontrolBuffer.append("<select class=terms name=actions1 id=actions").append(aiSeqNo)
									.append(" style='width: 200px' onChange=\"javascript: actionDropDownChanged('")
									.append(aoProposalDocumentBean.getDocumentId()).append("',this, '")
									.append(aoProposalDocumentBean.getDocumentTitle()).append("','")
									.append(aoProposalDocumentBean.getDocumentStatus()).append("','")
									.append(aoProposalDocumentBean.getReferenceDocSeqNo()).append("','")
									.append(aoProposalDocumentBean.getDocumentType()).append("','")
									.append(aoProposalDocumentBean.getIsRequiredDoc())
									.append("')\"><option>I need to...</option></select>");
						}
						// Release 5 User Notification
					}
					else
					{
						ascontrolBuffer
								.append("<select class=terms name=actions1 id=actions style='width: 200px' disabled=\"disabled\">")
								.append("<option>I need to...</option></select>");
					}
				}
				else
				{
					ascontrolBuffer.append(
							"<select class=terms name=actions1 id=actions style='width: 200px' disabled=\"disabled\">")
							.append("<option>I need to...</option></select>");
				}

			}
			else
			{
				createActionDropdownForUploadedDocs(aiSeqNo, aoProposalDocumentBean, ascontrolBuffer, lsUserRole,
						lsProcurementStatusId, lsProcurementCancelledStatus);
			}
		}
		else
		{
			if (null != aoProposalDocumentBean.getRequiredFlag()
					&& aoProposalDocumentBean.getRequiredFlag().equalsIgnoreCase(HHSConstants.YES))
			{
				ascontrolBuffer.append("<select class='selectbox' name=actions1 id=actions").append(aiSeqNo)
						.append(" style='width: auto' onChange=\"javascript: actionDropDownForDocuments('")
						.append(aoProposalDocumentBean.getDocumentId()).append("',this,'")
						.append(aoProposalDocumentBean.getDocumentSeqNumber()).append("','")
						.append(aoProposalDocumentBean.getDocumentType()).append("','")
						.append(aoProposalDocumentBean.getDocumentTitle()).append("')\"")
						.append("><option value=I need to... >I need to...</option>")
						.append("<option>View Document</option>").append("<option>View Document Information</option>")
						.append("</select>")
						.append("<input type='hidden' id='pageReadOnly' name='pageReadOnly' value='true'/>");
			}
			else
			{
				ascontrolBuffer.append("<select class='selectbox' name=actions1 id=actions").append(aiSeqNo)
						.append(" style='width: auto' onChange=\"javascript: actionDropDownForDocuments('")
						.append(aoProposalDocumentBean.getDocumentId()).append("',this,'")
						.append(aoProposalDocumentBean.getDocumentSeqNumber()).append("','")
						.append(aoProposalDocumentBean.getDocumentType()).append("','")
						.append(aoProposalDocumentBean.getDocumentTitle()).append("')\"")
						.append("><option value=I need to... >I need to...</option>")
						.append("<option>View Document</option>").append("<option>View Document Information</option>")
						.append("<option>Remove Document</option>").append("</select>")
						.append("<input type='hidden' id='pageReadOnly' name='pageReadOnly' value='true'/>");
			}
		}
	}

	/**
	 * Modified By - Tanuj Mudgal This Method is changed to fix defect 5523 for
	 * release 2.6.0
	 * 
	 * @param aiSeqNo sequesnce no
	 * @param aoProposalDocumentBean proposal document bean
	 * @param ascontrolBuffer string buffer
	 * @param asUserRole user role
	 * @param asProcurementStatusId procurement status id
	 * @param asProcurementCancelledStatus procurement cancelled Status
	 */
	private void createActionDropdownForUploadedDocs(Integer aiSeqNo, ExtendedDocument aoProposalDocumentBean,
			StringBuffer ascontrolBuffer, String asUserRole, String asProcurementStatusId,
			String asProcurementCancelledStatus)
	{
		if (!(HHSConstants.STAFF_ROLE.equalsIgnoreCase(asUserRole) || ApplicationConstants.ROLE_ADMINISTRATOR_PROV_STAFF
				.equalsIgnoreCase(asUserRole)))
		{
			if (!asProcurementStatusId.equals(asProcurementCancelledStatus))
			{
				// Release 5 User Notification
				if (aoProposalDocumentBean.isUserAccess())
				{
					// fix start 5523
					ascontrolBuffer.append("<select class=terms name=actions1 id=actions").append(aiSeqNo)
							.append(" style='width: 200px' onChange=\"javascript: actionDropDownChanged('")
							.append(aoProposalDocumentBean.getDocumentId()).append("',this, '")
							.append(aoProposalDocumentBean.getDocumentTitle()).append("','")
							.append(aoProposalDocumentBean.getDocumentStatus()).append("','")
							.append(aoProposalDocumentBean.getReferenceDocSeqNo()).append("','")
							.append(aoProposalDocumentBean.getDocumentType()).append("','")
							.append(aoProposalDocumentBean.getIsRequiredDoc())
							.append("')\"><option value=I need to... >I need to...</option>")
							.append("<option>View Document</option>")
							.append("<option>View Document Information</option>")
							.append("<option>Upload Document</option>")
							.append("<option>Select Document from Vault</option>")
							.append("<option>Remove Document</option></select>")
							.append("<input type='hidden' id='pageReadOnly' name='pageReadOnly' value='false'/>");
					// fix ends 5523
				}
				else
				{
					ascontrolBuffer.append("<select class=terms name=actions1 id=actions").append(aiSeqNo)
							.append(" style='width: 200px' onChange=\"javascript: actionDropDownChanged('")
							.append(aoProposalDocumentBean.getDocumentId()).append("',this, '")
							.append(aoProposalDocumentBean.getDocumentTitle()).append("','")
							.append(aoProposalDocumentBean.getDocumentStatus()).append("','")
							.append(aoProposalDocumentBean.getReferenceDocSeqNo()).append("','")
							.append(aoProposalDocumentBean.getDocumentType()).append("','")
							.append(aoProposalDocumentBean.getIsRequiredDoc())
							.append("')\"><option value=I need to... >I need to...</option>")
							.append("<option>View Document</option>")
							.append("<option>View Document Information</option></select>")
							.append("<input type='hidden' id='pageReadOnly' name='pageReadOnly' value='false'/>");
				}
				// Release 5 User Notification
			}
			else
			{
				ascontrolBuffer.append("<select class=terms name=actions1 id=actions").append(aiSeqNo)
						.append(" style='width: 200px' onChange=\"javascript: actionDropDownChanged('")
						.append(aoProposalDocumentBean.getDocumentId()).append("',this, '")
						.append(aoProposalDocumentBean.getDocumentTitle()).append("','")
						.append(aoProposalDocumentBean.getDocumentStatus()).append("','")
						.append(aoProposalDocumentBean.getReferenceDocSeqNo()).append("','")
						.append(aoProposalDocumentBean.getDocumentType())
						.append("')\"><option value=I need to... >I need to...</option>")
						.append("<option>View Document</option>").append("<option>View Document Information</option>")
						.append("</select>")
						.append("<input type='hidden' id='pageReadOnly' name='pageReadOnly' value='true'/>");
			}
		}
		else
		{
			ascontrolBuffer.append("<select class=terms name=actions1 id=actions").append(aiSeqNo)
					.append(" style='width: 200px' onChange=\"javascript: actionDropDownChanged('")
					.append(aoProposalDocumentBean.getDocumentId()).append("',this, '")
					.append(aoProposalDocumentBean.getDocumentTitle()).append("','")
					.append(aoProposalDocumentBean.getDocumentStatus()).append("','")
					.append(aoProposalDocumentBean.getReferenceDocSeqNo()).append("','")
					.append(aoProposalDocumentBean.getDocumentType())
					.append("')\"><option value=I need to... >I need to...</option>")
					.append("<option>View Document</option>").append("<option>View Document Information</option>")
					.append("<input type='hidden' id='pageReadOnly' name='pageReadOnly' value='true'/>");
		}
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
