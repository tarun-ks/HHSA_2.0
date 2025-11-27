package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.model.ExtendedDocument;
import com.nyc.hhs.util.PropertyLoader;

public class ViewAwardDocumentExtention
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
		StringBuffer lsControlBuffer = new StringBuffer();
		if (HHSConstants.DOCUMENT_TITLE.equalsIgnoreCase(aoCol.getColumnName()))
		{
			if (loProposalDocumentBean.getDocumentTitle() != null)
			{
				lsControlBuffer.append("<a href=\"#\" title='").append(loProposalDocumentBean.getDocumentTitle())
						.append("' alt='").append(loProposalDocumentBean.getDocumentTitle())
						.append("'onclick=\"javascript: viewRFPDocument('")
						.append(loProposalDocumentBean.getDocumentId()).append("','")
						.append(loProposalDocumentBean.getDocumentTitle()).append("' );\">")
						.append(loProposalDocumentBean.getDocumentTitle()).append("</a>");
			}
		}
		else if (HHSConstants.ACTIONS.equalsIgnoreCase(aoCol.getColumnName()))
		{
			createActionDropDown(aiSeqNo, loProposalDocumentBean, lsProposalDocStatusId, lsControlBuffer);
		}
		else if (HHSConstants.DOC_ID.equalsIgnoreCase(aoCol.getColumnName()))
		{
			if (loProposalDocumentBean.getDocumentId() != null
					&& !loProposalDocumentBean.getDocumentId().equalsIgnoreCase(HHSConstants.NA_KEY))
			{
				lsControlBuffer.append("<a style=\"cursor:pointer\" onclick=\"javascript: viewDocumentInfo('")
						.append(loProposalDocumentBean.getDocumentId()).append("',this);\">").append("Info</a>");
			}
			else
			{
				lsControlBuffer.append(loProposalDocumentBean.getDocumentId());
			}
		}
		return lsControlBuffer.toString();
	}

	private void createActionDropDown(Integer aiSeqNo, ExtendedDocument aoProposalDocumentBean,
			String asProposalDocStatusId, StringBuffer ascontrolBuffer) throws ApplicationException
	{
		if (null != asProposalDocStatusId)
		{
			if ((asProposalDocStatusId.equalsIgnoreCase(PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.DOCUMENT_COMPLETED)))
					|| (asProposalDocStatusId.equalsIgnoreCase(PropertyLoader.getProperty(
							HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.DOCUMENT_RETURNED_KEY))))
			{
				ascontrolBuffer.append("<select class=terms name=actions1 id=actions").append(aiSeqNo)
						.append(" style='width: 200px' onChange=\"javascript: actionDropDownChangedComplete('")
						.append(aoProposalDocumentBean.getDocumentId())
						.append("',this, '")
						.append(aoProposalDocumentBean.getDocumentTitle())
						.append("','")
						.append(aoProposalDocumentBean.getDocumentStatus())
						.append("','")
						.append(aoProposalDocumentBean.getReferenceDocSeqNo())
						.append("','")
						// Start || Changes done for Enhancement #6429 for
						// Release 3.4.0
						.append(aoProposalDocumentBean.getDocumentType())
						.append("','")
						.append(aoProposalDocumentBean.getAgencyAward())
						// End || Changes done for Enhancement #6429 for Release
						// 3.4.0
						.append("')\"><option value=I need to... >I need to...</option>")
						.append("<option>View Document</option>").append("<option>View Document Information</option>")
						.append("</select>")
						.append("<input type='hidden' id='pageReadOnly' name='pageReadOnly' value='true'/>");
			}
			else if ((asProposalDocStatusId.equalsIgnoreCase(PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.DOCUMENT_SUBMITTED)))
					|| (asProposalDocStatusId.equalsIgnoreCase(PropertyLoader.getProperty(
							HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.DOCUMENT_VERIFIED_KEY)))
					|| (asProposalDocStatusId.equalsIgnoreCase(PropertyLoader.getProperty(
							HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.DOCUMENT_NON_RESPONSIVE_KEY))))
			// Start || Changes done for Enhancement #6429 for Release 3.4.0
			{
				if (null != aoProposalDocumentBean.getAgencyAward()
						&& HHSConstants.ONE.equals(aoProposalDocumentBean.getAgencyAward()))
				{
					if (!aoProposalDocumentBean.getProcurementStatusId().equals(
							PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
									HHSConstants.STATUS_PROCUREMENT_CLOSED))
							&& !aoProposalDocumentBean.getProcurementStatusId().equals(
									PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
											HHSConstants.STATUS_PROCUREMENT_CANCELLED)))
					{
						ascontrolBuffer.append("<select class=terms name=actions1 id=actions").append(aiSeqNo)
								.append(" style='width: 200px' onChange=\"javascript: actionDropDownChangedComplete('")
								.append(aoProposalDocumentBean.getDocumentId()).append("',this, '")
								.append(aoProposalDocumentBean.getDocumentTitle()).append("','")
								.append(aoProposalDocumentBean.getDocumentStatus()).append("','")
								.append(aoProposalDocumentBean.getReferenceDocSeqNo()).append("','")
								.append(aoProposalDocumentBean.getDocumentType()).append("','")
								.append(aoProposalDocumentBean.getAgencyAward())
								.append("')\"><option value=I need to... >I need to...</option>")
								.append("<option>View Document</option>")
								.append("<option>View Document Information</option>")
								.append("<option>Remove Document</option>").append("</select>")
								.append("<input type='hidden' id='pageReadOnly' name='pageReadOnly' value='true'/>");
					}
					else
					{
						ascontrolBuffer.append("<select class=terms name=actions1 id=actions").append(aiSeqNo)
								.append(" style='width: 200px' onChange=\"javascript: actionDropDownChangedComplete('")
								.append(aoProposalDocumentBean.getDocumentId()).append("',this, '")
								.append(aoProposalDocumentBean.getDocumentTitle()).append("','")
								.append(aoProposalDocumentBean.getDocumentStatus()).append("','")
								.append(aoProposalDocumentBean.getReferenceDocSeqNo()).append("','")
								.append(aoProposalDocumentBean.getDocumentType()).append("','")
								.append(aoProposalDocumentBean.getAgencyAward())
								.append("')\"><option value=I need to... >I need to...</option>")
								.append("<option>View Document</option>")
								.append("<option>View Document Information</option>").append("</select>")
								.append("<input type='hidden' id='pageReadOnly' name='pageReadOnly' value='true'/>");
					}
				}
				else
				{
					// End || Changes done for Enhancement #6429 for Release
					// 3.4.0
					ascontrolBuffer.append("<select class=terms name=actions1 id=actions").append(aiSeqNo)
							.append(" style='width: 200px' onChange=\"javascript: actionDropDownChangedComplete('")
							.append(aoProposalDocumentBean.getDocumentId())
							.append("',this, '")
							.append(aoProposalDocumentBean.getDocumentTitle())
							.append("','")
							.append(aoProposalDocumentBean.getDocumentStatus())
							.append("','")
							.append(aoProposalDocumentBean.getReferenceDocSeqNo())
							.append("','")
							// Start || Changes done for Enhancement #6429 for
							// Release 3.4.0
							.append(aoProposalDocumentBean.getDocumentType())
							.append("','")
							.append(aoProposalDocumentBean.getAgencyAward())
							// End || Changes done for Enhancement #6429 for
							// Release 3.4.0
							.append("')\"><option value=I need to... >I need to...</option>")
							.append("<option>View Document</option>")
							.append("<option>View Document Information</option>").append("</select>")
							.append("<input type='hidden' id='pageReadOnly' name='pageReadOnly' value='true'/>");
				}
			}
			else
			{
				ascontrolBuffer
						.append("<select class=terms name=actions1 id=actions")
						.append(aiSeqNo)
						.append(" style='width: 200px' disabled='disabled' onChange=\"javascript: actionDropDownChangedComplete('")
						.append(aoProposalDocumentBean.getDocumentId())
						.append("',this, '")
						.append(aoProposalDocumentBean.getDocumentTitle())
						.append("','")
						.append(aoProposalDocumentBean.getDocumentStatus())
						.append("','")
						.append(aoProposalDocumentBean.getReferenceDocSeqNo())
						.append("','")
						// Start || Changes done for Enhancement #6429 for
						// Release 3.4.0
						.append(aoProposalDocumentBean.getDocumentType())
						.append("','")
						.append(aoProposalDocumentBean.getAgencyAward())
						// End || Changes done for Enhancement #6429 for Release
						// 3.4.0
						.append("')\"><option value=I need to... >I need to...</option>")
						.append("<option>View Document</option>").append("<option>View Document Information</option>")
						.append("</select>")
						.append("<input type='hidden' id='pageReadOnly' name='pageReadOnly' value='true' />");
			}
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
		String lsControl = HHSConstants.RESUME;
		return lsControl;
	}
}
