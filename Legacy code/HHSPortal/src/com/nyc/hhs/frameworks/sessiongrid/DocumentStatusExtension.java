/**
 * 
 */
package com.nyc.hhs.frameworks.sessiongrid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.WorkFlowDetailBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;

/**
 * This class generates an extension that will create a document or sub-section
 * drop-down
 * 
 */

public class DocumentStatusExtension implements DecoratorInterface
{

	/**
	 * This Method is used to generate the Status of the Documents on the Task
	 * Details page
	 * @param aoEachObject Bean Name
	 * @param aoCol Column name
	 * @param aiSeqNo Sequence Number
	 * @return String
	 * 
	 */
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aiSeqNo) throws ApplicationException
	{
		WorkFlowDetailBean loWorkFlowBean = (WorkFlowDetailBean) aoEachObject;
		List<String> loStatusList = getMasterData("fetchBRStatus");

		StringBuffer loSBcontrol = new StringBuffer();
		if (null != loWorkFlowBean.getMsQuestionDocumentName())
		{
			String lsDocName = loWorkFlowBean.getMsQuestionDocumentName();
			String lsDocNameForAudit = loWorkFlowBean.getMsQuestionDocumentName();
			String lsDocType = loWorkFlowBean.getMsDocType();
			// String lsCurrentStatus=loWorkFlowBean.getMsCurrentStatus();
			String lsAssignedStatus = loWorkFlowBean.getMsAssignedStatus();
			if (lsAssignedStatus == null)
			{
				lsAssignedStatus = " ";
			}
			
			/***** Begin QC6439 Doc name can be same as Section name  ****/
			if (!loWorkFlowBean.getMsDocOrSectionID().equalsIgnoreCase(P8Constants.PROPERTY_PE_BR_SUBSECTION_GEOGRAPHY)
					&& !loWorkFlowBean.getMsDocOrSectionID().equalsIgnoreCase(P8Constants.PROPERTY_PE_BR_SUBSECTION_LANGUAGES)
					&& !loWorkFlowBean.getMsDocOrSectionID().equalsIgnoreCase(P8Constants.PROPERTY_PE_BR_SUBSECTION_POPULATIONS)
					&& !loWorkFlowBean.getMsDocOrSectionID().equalsIgnoreCase(P8Constants.PROPERTY_PE_BR_SUBSECTION_QUESTION)
					&& !loWorkFlowBean.getMsDocOrSectionID().equalsIgnoreCase(P8Constants.PROPERTY_PE_BR_SUBSECTION_POLICIES_QUESTION)
					&& !loWorkFlowBean.getMsDocOrSectionID().equalsIgnoreCase(P8Constants.PROPERTY_PE_BR_SUBSECTION_BOARD_QUESTION)
					&& !loWorkFlowBean.getMsDocOrSectionID().equalsIgnoreCase(P8Constants.PROPERTY_PE_BR_SUBSECTION_FILINGS_QUESTION)

			)
			/***** End QC6439 Doc name can be same as Section name  ****/
			{
				lsDocName = loWorkFlowBean.getMsDocOrSectionID();
			}
			Iterator loIterator = loStatusList.iterator();

			loSBcontrol.append("<select class='selectReturned' name='assignedstatus'> <option value='' > </option> ");
			while (loIterator.hasNext())
			{
				String lsStatus = (String) loIterator.next();

				if (null != lsStatus && !lsStatus.isEmpty() && lsAssignedStatus.trim().equals(lsStatus.trim()))
				{
					loSBcontrol.append("<option value='");
					loSBcontrol.append(lsDocName);
					loSBcontrol.append("_");
					loSBcontrol.append(lsStatus);
					loSBcontrol.append("_");
					loSBcontrol.append(lsAssignedStatus);
					loSBcontrol.append("_");
					loSBcontrol.append(lsDocType);
					loSBcontrol.append("_");
					loSBcontrol.append(lsDocNameForAudit);
					loSBcontrol.append("' selected >");
					loSBcontrol.append(lsStatus);
					loSBcontrol.append("</option>");
				}
				else
				{
					loSBcontrol.append("<option value='");
					loSBcontrol.append(lsDocName);
					loSBcontrol.append("_");
					loSBcontrol.append(lsStatus);
					loSBcontrol.append("_");
					loSBcontrol.append(lsAssignedStatus);
					loSBcontrol.append("_");
					loSBcontrol.append(lsDocType);
					loSBcontrol.append("_");
					loSBcontrol.append(lsDocNameForAudit);
					loSBcontrol.append("'>");
					loSBcontrol.append(lsStatus);
					loSBcontrol.append("</option>");
				}
			}
			loSBcontrol.append("</select>");
		}
		return loSBcontrol.toString();
	}

	/**
	 * This Method is used to check all the checkboxes if present.
	 * @param aoCol column name
	 * @return String
	 */
	public String getControlForHeading(Column aoCol)
	{
		String lsControl = "RESUME";
		return lsControl;
	}

	/**
	 * This Method is used to fetch the Status for the documents from the master
	 * table.
	 * @param aoMethodName
	 * @return
	 * @throws ApplicationException
	 */
	public static List<String> getMasterData(String aoMethodName) throws ApplicationException
	{
		Channel loChannel = new Channel();
		loChannel.setData("aoMethodName", aoMethodName);
		TransactionManager.executeTransaction(loChannel, "getMasterData_DB");
		List<String> loStatusList = new ArrayList<String>();
		loStatusList.addAll((List<String>) loChannel.getData("masterList"));
		return loStatusList;
	}
}
