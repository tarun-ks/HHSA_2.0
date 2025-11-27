package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.model.ExtendedDocument;

public class ProposalDocumentsExtensionForDocumentScreen
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
		if (HHSConstants.DOCUMENT_TITLE_LOWER_CASE.equalsIgnoreCase(aoCol.getColumnName()))
		{
			if (loProposalDocumentBean.getDocumentTitle() != null)
			{
				loControlBuffer.append("<a href=\"javascript:void(0);\" id= '")
						.append(loProposalDocumentBean.getDocumentId()).append("' class='localTabs' name='taskArrow' ")
						.append("onclick=\"javascript: viewRFPDocument('")
						.append(loProposalDocumentBean.getDocumentId()).append("','")
						.append(loProposalDocumentBean.getDocumentTitle()).append("' );\">")
						.append(loProposalDocumentBean.getDocumentTitle()).append("</a>");
			}
		}
		else if (HHSConstants.ACTIONS.equalsIgnoreCase(aoCol.getColumnName()))
		{
			HHSGridUtil.createActionDropDown(aiSeqNo, loProposalDocumentBean, lsProposalDocStatusId, loControlBuffer);
		}
		return loControlBuffer.toString();
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
