/**
 * Class added for R4
 */
package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.model.EvaluationSummaryBean;
import com.nyc.hhs.rule.Rule;
import com.nyc.hhs.util.CommonUtil;

public class EvaluationSummaryExtension implements DecoratorInterface
{
	/**
	 * This method is used to show drop down on the proposal summary page having
	 * proposal grid
	 * @param aoEachObject an object of list to be displayed in grid
	 * @param aoCol a column object
	 * @param aoSeqNo an integer value of sequence number
	 * 
	 * @return a string value of html code formed
	 */
	@Override
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aoSeqNo) throws ApplicationException
	{
		EvaluationSummaryBean loEvaluationSummaryBean = (EvaluationSummaryBean) aoEachObject;
		StringBuilder lsControl = new StringBuilder();
		Channel loChannel = new Channel();
		loChannel.setData(HHSConstants.ACCELERATOR_USER_ROLE, loEvaluationSummaryBean.getUserRole());
		loChannel.setData(HHSConstants.STAT, loEvaluationSummaryBean.getEvaluationStatus());
		loChannel.setData(HHSConstants.AWARD_APPROVAL_COUNT, loEvaluationSummaryBean.getAwardApprovalCount());
		Boolean loIsAccoUSer = Boolean.valueOf((String) Rule.evaluateRule(HHSConstants.EVAL_SUMMARY_ACCO_USER_CHECK,
				loChannel));
		Boolean loIsNonAccoUSer = Boolean.valueOf((String) Rule.evaluateRule(
				HHSConstants.EVAL_SUMMARY_NON_ACCO_USER_CHECK, loChannel));
		if (aoCol.getColumnName().equalsIgnoreCase(HHSConstants.ACTIONS))
		{
			getControlForActionsColumn(aoSeqNo, loEvaluationSummaryBean, lsControl, loIsAccoUSer, loIsNonAccoUSer);
		}
		else if (aoCol.getColumnName().equalsIgnoreCase(HHSConstants.COMPETITION_POOL_TITLE))
		{
			if (loIsAccoUSer)
			{
				lsControl.append("<a href=\"javascript: editEvaluationSettings(");
				lsControl.append(loEvaluationSummaryBean.getCompetitionPoolId());
				lsControl.append(",");
				lsControl.append(loEvaluationSummaryBean.getEvaluationGroupId());
				lsControl.append(",");
				lsControl.append(loEvaluationSummaryBean.getEvaluationPoolMappingId());
				lsControl.append(");\">");
				lsControl.append(loEvaluationSummaryBean.getCompetitionPoolTitle());
				lsControl.append("</a>");
			}
			else if (loIsNonAccoUSer)
			{
				lsControl.append("<a href=\"javascript: viewEvaluationResults(");
				lsControl.append(loEvaluationSummaryBean.getCompetitionPoolId());
				lsControl.append(",");
				lsControl.append(loEvaluationSummaryBean.getEvaluationGroupId());
				lsControl.append(",");
				lsControl.append(loEvaluationSummaryBean.getEvaluationPoolMappingId());
				lsControl.append(");\">");
				lsControl.append(loEvaluationSummaryBean.getCompetitionPoolTitle());
				lsControl.append("</a>");
			}
			else
			{
				lsControl.append(loEvaluationSummaryBean.getCompetitionPoolTitle());
			}
		}
		return lsControl.toString();
	}

	/**
	 * This method is used to show drop down on the proposal summary page having
	 * proposal grid
	 * 
	 * @param aoSeqNo an integer value of sequence number
	 * @param loEvaluationSummaryBean EvaluationSummaryBean object
	 * @param lsControl append String
	 * @param loIsAccoUSer flag to depict whether it is acco user
	 * @param loIsNonAccoUSer flag to depict whether it is non acco user
	 */
	private void getControlForActionsColumn(Integer aoSeqNo, EvaluationSummaryBean loEvaluationSummaryBean,
			StringBuilder lsControl, Boolean loIsAccoUSer, Boolean loIsNonAccoUSer) throws ApplicationException
	{
		if (loIsAccoUSer)
		{
			lsControl.append("<select name=actions1 id=actions");
			lsControl.append(aoSeqNo);
			lsControl.append(" style='width: 177px;' onChange=\"javascript: viewEvaluationActions('");
			lsControl.append(loEvaluationSummaryBean.getCompetitionPoolId());
			lsControl.append("','");
			lsControl.append(loEvaluationSummaryBean.getEvaluationGroupId());
			lsControl.append("','");
			lsControl.append(loEvaluationSummaryBean.getEvaluationPoolMappingId());
			lsControl.append("',this)\"><option title='I need to...'>I need to...</option>");
			lsControl.append("<option title='Edit Evaluation Settings'>Edit Evaluation Settings</option>");
			lsControl.append("<option title='View Evaluation Status'>View Evaluation Status</option>");
			if (null != loEvaluationSummaryBean.getEvaluationStatus()
					&& !loEvaluationSummaryBean.getEvaluationStatus().equalsIgnoreCase(
							HHSConstants.PROCUREMENT_STATUS_RELEASED))
			{
				lsControl.append("<option title='View Evaluation Results and Selections'>View Evaluation Results and Selections</option>");
			}
			// Start || Changes done for Enhancement #6577 for Release 3.10.0 
			if(loEvaluationSummaryBean.getUserType().equalsIgnoreCase(HHSConstants.CITY_ORG)){
/*				if(null != loEvaluationSummaryBean.getEvaluationStatus() && !loEvaluationSummaryBean.getEvaluationStatus().equals(HHSConstants.SELECTIONS_MADE) 
						&& !loEvaluationSummaryBean.getEvaluationStatus().equals(HHSConstants.STATUS_NON_RESPONSIVE)
						&& !loEvaluationSummaryBean.getEvaluationStatus().equals(HHSConstants.EVALUATIONS_COMPLETE)){
*/				lsControl.append("<option title='Cancel Competition'>Cancel Competition</option>");
				//}
			}
			// End || Changes done for Enhancement #6577 for Release 3.10.0 
			lsControl.append("</select>");
		}
		else if (loIsNonAccoUSer)
		{
			lsControl.append("<select name=actions1 id=actions").append(aoSeqNo);
			lsControl.append(" style='width: 177px;' onChange=\"javascript: viewEvaluationActions('");
			lsControl.append(loEvaluationSummaryBean.getCompetitionPoolId());
			lsControl.append("','");
			lsControl.append(loEvaluationSummaryBean.getEvaluationGroupId());
			lsControl.append("','");
			lsControl.append(loEvaluationSummaryBean.getEvaluationPoolMappingId());
			lsControl.append("',this)\"><option title='I need to...'>I need to...</option>");
			lsControl.append("<option title='View Evaluation Results and Selections'>View Evaluation Results and Selections</option>");
			lsControl.append("</select>");
		}
		else
		{
			lsControl.append("<select name=actions1 id=actions");
			lsControl.append(aoSeqNo);
			lsControl.append(" style='width: 177px;' onChange=\"javascript: viewEvaluationActions('");
			lsControl.append(loEvaluationSummaryBean.getCompetitionPoolId());
			lsControl.append("','");
			lsControl.append(loEvaluationSummaryBean.getEvaluationGroupId());
			lsControl.append("','");
			lsControl.append(loEvaluationSummaryBean.getEvaluationPoolMappingId());
			lsControl.append("',this)\" disabled = \"disabled\"><option title='I need to...'>I need to...</option>");
			lsControl.append("</select>");
		}
		
		// R 7.2.0 QC9059 Action drop-down should not contain modifiable Options
		if (lsControl != null && ApplicationConstants.ROLE_OBSERVER.equalsIgnoreCase(loEvaluationSummaryBean.getRoleCurrent()))
		{
			CommonUtil.keepReadOnlyActions(lsControl);
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
