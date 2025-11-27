package com.nyc.hhs.frameworks.grid;

import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.model.EvaluationBean;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.XMLUtil;

/**
 * The Class will be called while rendering Evaluation Status Screen
 * 
 */
public class EvaluationActionExtension implements DecoratorInterface
{

	/**
	 * <ul>
	 * <li>1. View Evaluation Summary is available in the drop down if the
	 * evaluation tasks have been sent and Proposal Status is NOT
	 * "Non-Responsive"</li>
	 * <li>2. "Mark Non-Responsive" is available in the drop down if the
	 * proposal status is “Returned for Revision” "Mark Non-Responsive" is only
	 * available for ACCO staff and ACCO manager users. All other users will not
	 * see this option.</li>
	 * <li>3.Mark Returned for Revision
	 * " is only available in the drop down if the proposal status is "
	 * Non-Responsive" and the evaluation task has not been sent yet
	 * "Mark Returned for Revision" is only available for ACCO staff and ACCO</li>
	 * <li>4.View Proposal will be Available to All</li>
	 * <li>Updated method in R4</li>
	 * </ul>
	 * 
	 * @param aoEachObject Object
	 * @param aoCol Column
	 * @param aoSeqNo Integer
	 * @return String
	 * @throws ApplicationException if any exception occurred
	 */

	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aoSeqNo) throws ApplicationException
	{
		EvaluationBean loEvaluationBean = (EvaluationBean) aoEachObject;

		StringBuilder lsControl = new StringBuilder();
		if (aoCol.getColumnName().equalsIgnoreCase(HHSConstants.ACTIONS))
		{
			String lsXPathTop = "";
			Document loDoc;
			try
			{
				loDoc = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
						HHSConstants.EVALUATION_ACTION_MAPPING_FILE_NAME);
				if (loEvaluationBean.getUserRole() != null
						&& loEvaluationBean.getEvalPoolMappingStatus() != null
						&& !loEvaluationBean.getEvalPoolMappingStatus().equalsIgnoreCase(
								PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
										HHSConstants.STATUS_COMPETITION_POOL_RELEASED))
						&& (loEvaluationBean.getUserRole().equalsIgnoreCase(HHSConstants.ACCO_MANAGER_ROLE)))
				{
					lsXPathTop = "//role[(@name=\"accomanager\")]//status[(@name=\""
							+ loEvaluationBean.getProposalStatusId() + "\")]//action";
				}
				else if (loEvaluationBean.getUserRole() != null
						&& loEvaluationBean.getEvalPoolMappingStatus() != null
						&& !loEvaluationBean.getEvalPoolMappingStatus().equalsIgnoreCase(
								PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
										HHSConstants.STATUS_COMPETITION_POOL_RELEASED))
						&& (loEvaluationBean.getUserRole().equalsIgnoreCase(HHSConstants.ACCO_STAFF_ROLE) || loEvaluationBean
								.getUserRole().equalsIgnoreCase(HHSConstants.ACCO_ADMIN_STAFF_ROLE)))
				{
					lsXPathTop = "//role[(@name=\"others\")]//status[(@name=\""
							+ loEvaluationBean.getProposalStatusId() + "\")]//action";
				}
				else
				{
					lsXPathTop = "//role[(@name=\"\")]//status[(@name=\"" + loEvaluationBean.getProposalStatusId()
							+ "\")]//action";
				}
				List<Element> loNodeList = XMLUtil.getElementList(lsXPathTop, loDoc);
				lsControl.append("<select class=terms name=actions1 id='actions");
				lsControl.append(loEvaluationBean.getRowNum());
				lsControl.append("'");
				lsControl.append(" style='width: 200px' onChange=\"javascript: processActionDropDown('");
				lsControl.append(loEvaluationBean.getProposalId()).append("','");
				lsControl.append(loEvaluationBean.getEvaluationPoolMappingId()).append("',this)");
				//Start || Changes done for Enhancement #6577 for Release 3.10.0
				if (loEvaluationBean.getSubmissionCloseDate() == null || loEvaluationBean.getEvalPoolMappingStatus().equals(
						PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_COMPETITION_POOL_CANCELLED)))
				{
					lsControl.append("\" disabled='disabled' ");
				}
				//End || Changes done for Enhancement #6577 for Release 3.10.0
				else
				{
					lsControl.append("\"");
				}
				lsControl.append("><option>I need to...</option>");
				for (Element loNode : loNodeList)
				{
					getControlForColumnFinal(loEvaluationBean, lsControl, loNode);
				}
				lsControl.append("</select>");
			}
			catch (ApplicationException loEx)
			{
				throw loEx;
			}
		}
		return lsControl.toString();
	}

	/**
	 * <ul>
	 * <li>1. View Evaluation Summary is available in the drop down if the
	 * evaluation tasks have been sent and Proposal Status is NOT
	 * "Non-Responsive"</li>
	 * <li>2. "Mark Non-Responsive" is available in the drop down if the
	 * proposal status is “Returned for Revision” "Mark Non-Responsive" is only
	 * available for ACCO staff and ACCO manager users. All other users will not
	 * see this option.</li>
	 * <li>3.Mark Returned for Revision
	 * " is only available in the drop down if the proposal status is "
	 * Non-Responsive" and the evaluation task has not been sent yet
	 * "Mark Returned for Revision" is only available for ACCO staff and ACCO</li>
	 * <li>4.View Proposal will be Available to All</li>
	 * <li>Updated method in R4</li>
	 * </ul>
	 * 
	 * @param loEvaluationBean EvaluationBean object
	 * @param lsControl append string
	 * @param loNode select option value
	 * @throws ApplicationException If an exception occurs
	 */
	private void getControlForColumnFinal(EvaluationBean loEvaluationBean, StringBuilder lsControl, Element loNode)
			throws ApplicationException
	{
		if ((loNode.getValue().equalsIgnoreCase(HHSConstants.VIEW_EVAL_SUMMARY)
				&& loEvaluationBean.getSendEvaluationStatus().equalsIgnoreCase(HHSConstants.YES)
				&& loEvaluationBean.getProcurementStatus() != null
				&& !loEvaluationBean.getProcurementStatus().equalsIgnoreCase(
						PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_PROCUREMENT_CLOSED)) && (!loEvaluationBean
				.getProcurementStatus().equalsIgnoreCase(
						PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_PROCUREMENT_CANCELLED)) || (loEvaluationBean
				.getProcurementStatus().equalsIgnoreCase(
						PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_PROCUREMENT_CANCELLED))
				&& loEvaluationBean.getEvaluationSent() != null && loEvaluationBean.getEvaluationSent()
				.equals(HHSConstants.ONE)))))
		{
			lsControl.append("<option value=\"");
			lsControl.append(loNode.getValue());
			lsControl.append("\">");
			lsControl.append(loNode.getValue());
			lsControl.append("</option>");
		}
		else if (loNode.getValue().equalsIgnoreCase(HHSConstants.MARK_RETURNDED_FOR_REVISION))
		{
			if ((loEvaluationBean.getProcurementStatus() != null
					&& !(loEvaluationBean.getProcurementStatus().equalsIgnoreCase(
							PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
									HHSConstants.STATUS_PROCUREMENT_CLOSED)) || loEvaluationBean
							.getProcurementStatus().equalsIgnoreCase(
									PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
											HHSConstants.STATUS_PROCUREMENT_CANCELLED)))
					&& loEvaluationBean.getSendEvaluationStatus() != null && !loEvaluationBean
					.getSendEvaluationStatus().equalsIgnoreCase(HHSConstants.YES))
					&& (loEvaluationBean.getSentEvalStatusNonResponsive() == null || !loEvaluationBean
							.getSentEvalStatusNonResponsive().equalsIgnoreCase(HHSConstants.ONE)))
			{
				lsControl.append("<option value=\"");
				lsControl.append(loNode.getValue());
				lsControl.append("\">");
				lsControl.append(loNode.getValue());
				lsControl.append("</option>");
			}

		}
		else if (loNode.getValue().equalsIgnoreCase(HHSConstants.MARK_NON_RESPONSIVE))
		{
			if (loEvaluationBean.getProcurementStatus() != null
					&& !(loEvaluationBean.getProcurementStatus().equalsIgnoreCase(
							PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
									HHSConstants.STATUS_PROCUREMENT_CLOSED)) || loEvaluationBean
							.getProcurementStatus().equalsIgnoreCase(
									PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
											HHSConstants.STATUS_PROCUREMENT_CANCELLED))))
			{
				lsControl.append("<option value=\"");
				lsControl.append(loNode.getValue());
				lsControl.append("\">");
				lsControl.append(loNode.getValue());
				lsControl.append("</option>");
			}

		}
		else if (loNode.getValue().equalsIgnoreCase(HHSConstants.UNLOCK_PROPOSAL))
		{
			if (!(loEvaluationBean.getEvaluationSent() != null && loEvaluationBean.getEvaluationSent()
					.equals(HHSConstants.ONE))
					&& loEvaluationBean.getProcurementStatus() != null
					&& !loEvaluationBean.getProcurementStatus().equalsIgnoreCase(
							PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
									HHSConstants.STATUS_PROCUREMENT_CLOSED))
					&& !loEvaluationBean.getProcurementStatus().equalsIgnoreCase(
							PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
									HHSConstants.STATUS_PROCUREMENT_CANCELLED))
					&& !loEvaluationBean.getEvalPoolMappingStatus().equalsIgnoreCase(
							PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
									HHSConstants.STATUS_COMPETITION_POOL_NON_RESPONSIVE)))
			{
				lsControl.append("<option value=\"");
				lsControl.append(loNode.getValue());
				lsControl.append("\">");
				lsControl.append(loNode.getValue());
				lsControl.append("</option>");
			}
		}
		else if (!loNode.getValue().equalsIgnoreCase(HHSConstants.VIEW_EVAL_SUMMARY))
		{
			lsControl.append("<option value=\"");
			lsControl.append(loNode.getValue());
			lsControl.append("\">");
			lsControl.append(loNode.getValue());
			lsControl.append("</option>");
		}
	}

	/**
	 * @param aoCol
	 * @return string
	 */
	public String getControlForHeading(Column aoCol)
	{
		return HHSConstants.RESUME;
	}

}
