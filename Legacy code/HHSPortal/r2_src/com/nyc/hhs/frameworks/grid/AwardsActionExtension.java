package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.model.AwardBean;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * The Class will be called while rendering awards and contracts Screen
 * 
 */
public class AwardsActionExtension implements DecoratorInterface
{

	/**
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. "Assign E-PIN" is available in the drop down if the E-Pin has not
	 * been assigned to the contract</li>
	 * <li>2."View APT Progress" is available in the drop down if and E-Pin has
	 * already been assigned to the contract.</li>
	 * <li>3."View Award Documents" is available if in the drop down the
	 * documents are present for the contract.</li>
	 * <li>4."Cancel Award" will be Available to All</li>
	 * </ul>
	 * 
	 * @param aoEachObject Object
	 * @param aoCol Column
	 * @param aoSeqNo Integer
	 * @return String loControl html format string
	 * @throws ApplicationException if any exception occurred
	 */
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aoSeqNo) throws ApplicationException
	{
		AwardBean loAwardBean = (AwardBean) aoEachObject;
		
		StringBuilder loControl = new StringBuilder();
		if (aoCol.getColumnName().equalsIgnoreCase(HHSConstants.ACTIONS))
		{
			loControl.append("<select class=terms name=actions1 id=actions");
			loControl.append(loAwardBean.getContractId());
			if (loAwardBean.getContractStatus().equals(HHSConstants.TASK_CANCELLED))
			{
				loControl.append(" disabled=true");
			}
			loControl.append(" style='width: 180px' onChange=\"javascript: selectOption(this,'")
					.append(loAwardBean.getProcurementId()).append("','").append(loAwardBean.getEvaluationGroupId())
					.append("','").append(loAwardBean.getOrganizationId()).append("','")
					.append(loAwardBean.getContractNumber()).append("','").append(loAwardBean.getIsFinancial())
					.append("','").append(loAwardBean.getContractId()).append("','")
					.append(loAwardBean.getProcurementStatus()).append("','").append(loAwardBean.getIsOpenEndedRFP())
					.append("','").append(loAwardBean.getContractTypeId()).append("','")
					.append(loAwardBean.getEvaluationPoolMappingId()).append("','").append(loAwardBean.getEpin())
					.append("')").append("\"><option>I need to...</option>");
			if (!loAwardBean.getContractStatus().equals(HHSConstants.TASK_CANCELLED))
			{
				if (loAwardBean.getEpin().equals(HHSConstants.NOT_ASSIGNED)
						&& loAwardBean.getOrgType().equals(HHSConstants.USER_CITY)
						&& !loAwardBean.getProcurementStatus().equals(
								PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
										HHSConstants.STATUS_PROCUREMENT_CLOSED))
						&& !loAwardBean.getProcurementStatus().equals(
								PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
										HHSConstants.STATUS_PROCUREMENT_CANCELLED)) )
				{
					loControl.append("<option title='Assign E-PIN' value='1'>Assign E-PIN</option>");
				}
				else if (!loAwardBean.getEpin().equals(HHSConstants.NOT_ASSIGNED))
				{
					loControl.append("<option title='View APT Progress' value='2'>View APT Progress</option>");
				}
				loControl.append("<option title='View Award Documents' value='3'>View Award Documents</option>");
				if (loAwardBean.getOrgType().equals(HHSConstants.USER_AGENCY)
						&& loAwardBean.getUserRole().equals(HHSConstants.ACCO_MANAGER_ROLE)
						&& !loAwardBean.getContractStatus().equals(HHSConstants.STATUS_CLOSED)
						&& !loAwardBean.getContractStatus().equals(HHSConstants.STATUS_REGISTERED)
						&& !loAwardBean.getProcurementStatus().equals(
								PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
										HHSConstants.STATUS_PROCUREMENT_CLOSED))
						&& !loAwardBean.getProcurementStatus().equals(
								PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
										HHSConstants.STATUS_PROCUREMENT_CANCELLED)))
				{
					loControl.append("<option title='Cancel Award' value='4'>Cancel Award</option>");
				}
			}
			loControl.append("</select>");
		}
		// R5 change starts
		else if (aoCol.getColumnName().equalsIgnoreCase(HHSConstants.AWARD_AMOUNT))
		{
			loControl.append("<span><label class='tableAwardAmount'>").append(loAwardBean.getAwardAmount())
					.append("</label></span>");
			if (HHSR5Constants.STATUS_NEGOTIATION_LIST.contains(loAwardBean.getStatus()))
			{
				loControl
						.append("<span class='red-ex-mark' title='This is the preliminary award amount. Final award amount is pending'></span>");
			}
		}
		
		// R 7.2.0 QC9071 Action drop-down should not contain modifiable Options
        if (loControl != null && ApplicationConstants.ROLE_OBSERVER.equalsIgnoreCase(loAwardBean.getRoleCurrent()))
        {
              CommonUtil.keepReadOnlyActions(loControl);
        }

		// R5 change starts
		return loControl.toString();
	}

	/**
	 * @param aoCol
	 * @return String
	 */
	public String getControlForHeading(Column aoCol)
	{
		return HHSConstants.RESUME;
	}
}
