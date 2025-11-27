package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.model.ExtendedDocument;
import com.nyc.hhs.rule.Rule;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This is a utility file for Proposal
 */
public class HHSGridUtil
{

	/**
	 * This method is used to create the action drop down for proposal document
	 * and award documents
	 * @param aiSeqNo sequence number of the document
	 * @param aoProposalDocumentBean proposal document bean object
	 * @param asProposalDocStatusId proposal document status
	 * @param ascontrolBuffer string buffer to display in jsp
	 * @throws ApplicationException if any exception occurs
	 */
	public static void createActionDropDown(Integer aiSeqNo, ExtendedDocument aoProposalDocumentBean,
			String asProposalDocStatusId, StringBuffer ascontrolBuffer) throws ApplicationException
	{
		boolean lbIsProposalDocScreenReanOnly = false;
		boolean lbIsDocumentCompleted = false;
		boolean lbIsDocumentSubmitted = false;
		Channel loChannel = new Channel();
		loChannel.setData(HHSConstants.PROPOSAL_STATUS_ID_KEY, aoProposalDocumentBean.getProposalStatusId());
		loChannel.setData(HHSConstants.PROC_STA_ID, aoProposalDocumentBean.getProcurementStatusId());
		loChannel.setData(HHSConstants.DOC_STAT_ID, asProposalDocStatusId);
		lbIsProposalDocScreenReanOnly = Boolean.valueOf((String) Rule.evaluateRule(HHSConstants.PROP_STATUS_READ_ONLY,
				loChannel));
		lbIsDocumentCompleted = Boolean.valueOf((String) Rule.evaluateRule(HHSConstants.PROPDOC_STATUS_COMPLETE,
				loChannel));
		lbIsDocumentSubmitted = Boolean.valueOf((String) Rule.evaluateRule(HHSConstants.PROPDOC_STATUS_SUBMITTED_RULE,
				loChannel));
		if (null != asProposalDocStatusId)
		{
			if (asProposalDocStatusId.equalsIgnoreCase(PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_DOCUMENT_NOT_STARTED)))
			{
				createActionDropdownForNotStartedDocs(aiSeqNo, aoProposalDocumentBean, ascontrolBuffer,
						lbIsProposalDocScreenReanOnly);

			}
			else if (lbIsDocumentCompleted)
			{
				if (lbIsProposalDocScreenReanOnly)
				{
					ascontrolBuffer.append("<select class=terms name=actions1 id=actions").append(aiSeqNo)
							.append(" style='width: 200px' onChange=\"javascript: actionDropDownChanged('")
							.append(aoProposalDocumentBean.getDocumentId()).append("',this, '")
							.append(aoProposalDocumentBean.getDocumentTitle()).append("','")
							.append(aoProposalDocumentBean.getDocumentStatus()).append("','")
							.append(aoProposalDocumentBean.getReferenceDocSeqNo()).append("','")
							.append(aoProposalDocumentBean.getDocumentType())
							.append("')\"><option value=I need to... >I need to...</option>")
							.append("<option>View Document</option>")
							.append("<option>View Document Information</option></select>")
							.append("<input type='hidden' id='pageReadOnly' name='pageReadOnly' value='")
							.append(lbIsProposalDocScreenReanOnly).append("'/>");
				}
				else
				{
					// Release 5 User Notification
					if (aoProposalDocumentBean.isUserAccess())
					{
						ascontrolBuffer.append("<select class=terms name=actions1 id=actions").append(aiSeqNo)
								.append(" style='width: 200px' onChange=\"javascript: actionDropDownChanged('")
								.append(aoProposalDocumentBean.getDocumentId()).append("',this, '")
								.append(aoProposalDocumentBean.getDocumentTitle()).append("','")
								.append(aoProposalDocumentBean.getDocumentStatus()).append("','")
								.append(aoProposalDocumentBean.getReferenceDocSeqNo()).append("','")
								.append(aoProposalDocumentBean.getDocumentType())
								.append("')\"><option value=I need to... >I need to...</option>")
								.append("<option>View Document</option>")
								.append("<option>View Document Information</option>")
								.append("<option>Upload Document</option>")
								.append("<option>Select Document from Vault</option>")
								.append("<option>Remove Document</option></select>")
								.append("<input type='hidden' id='pageReadOnly' name='pageReadOnly' value='")
								.append(lbIsProposalDocScreenReanOnly).append("'/>");
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
								.append("<option>View Document</option>")
								.append("<option>View Document Information</option></select>")
								.append("<input type='hidden' id='pageReadOnly' name='pageReadOnly' value='")
								.append(lbIsProposalDocScreenReanOnly).append("'/>");
					}
					// Release 5 User Notification Ends
				}
			}
			else if (lbIsDocumentSubmitted)
			{
				ascontrolBuffer.append("<select class=terms name=actions1 id=actions").append(aiSeqNo)
						.append(" style='width: 200px' onChange=\"javascript: actionDropDownChanged('")
						.append(aoProposalDocumentBean.getDocumentId()).append("',this, '")
						.append(aoProposalDocumentBean.getDocumentTitle()).append("','")
						.append(aoProposalDocumentBean.getDocumentStatus()).append("','")
						.append(aoProposalDocumentBean.getReferenceDocSeqNo()).append("','")
						.append(aoProposalDocumentBean.getDocumentType())
						.append("')\"><option value=I need to... >I need to...</option>")
						.append("<option>View Document</option>")
						.append("<option>View Document Information</option></select>")
						.append("<input type='hidden' id='pageReadOnly' name='pageReadOnly' value='")
						.append(lbIsProposalDocScreenReanOnly).append("'/>");
			}
		}
	}

	/**
	 * This method is used to generate the action drop down for the document not
	 * started
	 * @param aiSeqNo sequece number
	 * @param aoProposalDocumentBean proposal document bean
	 * @param ascontrolBuffer control string buffer
	 * @param abIsProposalDocScreenReanOnly is screen read only
	 * @throws ApplicationException
	 */
	private static void createActionDropdownForNotStartedDocs(Integer aiSeqNo, ExtendedDocument aoProposalDocumentBean,
			StringBuffer ascontrolBuffer, boolean abIsProposalDocScreenReanOnly) throws ApplicationException
	{
		String lsProcurementStatusId = aoProposalDocumentBean.getProcurementStatusId();
		String lsProcurementCancelledStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_PROCUREMENT_CANCELLED);
		if (abIsProposalDocScreenReanOnly)
		{
			if (lsProcurementStatusId.equals(lsProcurementCancelledStatus))
			{
				ascontrolBuffer.append("<select class=terms name=actions1 id=actions").append(aiSeqNo)
						.append(" style='width: 200px' disabled='disable'")
						.append("')\"><option>I need to...</option></select>");
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
						.append("')\"><option>I need to...</option></select>")
						.append("<input type='hidden' id='pageReadOnly' name='pageReadOnly' value='")
						.append(abIsProposalDocScreenReanOnly).append("'/>");
			}
		}
		else
		{
			if (lsProcurementStatusId.equals(lsProcurementCancelledStatus))
			{
				ascontrolBuffer.append("<select class=terms name=actions1 id=actions").append(aiSeqNo)
						.append(" style='width: 200px' disabled='disable'")
						.append("')\"><option>I need to...</option></select>");
			}
			else
			{
				// Release 5 User Notification
				if (aoProposalDocumentBean.isUserAccess())
				{
					ascontrolBuffer.append("<select class=terms name=actions1 id=actions").append(aiSeqNo)
							.append(" style='width: 200px' onChange=\"javascript: actionDropDownChanged('")
							.append(aoProposalDocumentBean.getDocumentId()).append("',this, '")
							.append(aoProposalDocumentBean.getDocumentTitle()).append("','")
							.append(aoProposalDocumentBean.getDocumentStatus()).append("','")
							.append(aoProposalDocumentBean.getReferenceDocSeqNo()).append("','")
							.append(aoProposalDocumentBean.getDocumentType())
							.append("')\"><option>I need to...</option><option>Upload Document</option>")
							.append("<option>Select Document from Vault</option></select>")
							.append("<input type='hidden' id='pageReadOnly' name='pageReadOnly' value='")
							.append(abIsProposalDocScreenReanOnly).append("'/>");
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
							.append("')\"><option>I need to...</option></select>")
							.append("<input type='hidden' id='pageReadOnly' name='pageReadOnly' value='")
							.append(abIsProposalDocScreenReanOnly).append("'/>");
				}
				// Release 5 User Notification Ends
			}
		}
	}
}
