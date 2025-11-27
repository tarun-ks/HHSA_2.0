package com.nyc.hhs.frameworks.grid;

import org.apache.commons.lang.StringEscapeUtils;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.model.BudgetList;
import com.nyc.hhs.util.DateUtil;

/**
 * 
 * @author naman.chopra
 * 
 */
public class BudgetActionFinancialExtension implements DecoratorInterface
{
	/**
	 * <ol>
	 * <li>On the basis of the column for which Decorator class is called:-</li>
	 * <ul>
	 * <li>When the class is called for date of last update column:-</li>
	 * <li>Decorator Class for showing date of last update</li>
	 * <li>When we fetch date of last update from database its under time stamp</li>
	 * <li>In this class thats formatted and converted into date only</li>
	 * </ul>
	 * <ul>
	 * <li>When the class is called for Contract Title column:-</li>
	 * <li>I have checked for the budgetType of the contract</li>
	 * <li>On the basis of the budgetType its corresponding class is returned
	 * after appending HTML inside string</li>
	 * </ul>
	 * </ol>
	 */
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aoSeqNo) throws ApplicationException
	{   
		BudgetList loBudgetList = (BudgetList) aoEachObject;
		// Observation Changes Release 3.4.0.
		loBudgetList.setContractTitle(StringEscapeUtils.escapeHtml(loBudgetList.getContractTitle()));
		String lsControl = HHSConstants.EMPTY_STRING;
		if (HHSConstants.BUDGET_DATE_OF_LAST_UPDATE.equalsIgnoreCase(aoCol.getColumnName()))
		{
			if (null != loBudgetList.getDateOfLastUpdate() && !loBudgetList.getDateOfLastUpdate().isEmpty())
			{
				lsControl = DateUtil.getDateByFormat(HHSConstants.YYYY_MM_DD_HH_MM_SS, HHSConstants.MMDDYYFORMAT,
						loBudgetList.getDateOfLastUpdate());
			}
		}
		// added for r5
		else if (HHSConstants.BUDGET_CONTRACT_TITLE.equalsIgnoreCase(aoCol.getColumnName()))
		{
			if (loBudgetList.isContractAccess() && loBudgetList.isUserAccess())
			{
				if (loBudgetList.getBudgetType().equalsIgnoreCase(HHSConstants.BUDGET_TYPE1))
				{
					lsControl = "<label class='BudgetAmendment'>" + "<a href=\"#\" title=\""
							+ loBudgetList.getContractTitle() + "\" onclick=\"javascript: launchBudget('"
							+ loBudgetList.getBudgetId() + "','" + loBudgetList.getContractId() + "','"
							+ loBudgetList.getFiscalYear() + "','" + loBudgetList.getBudgetType() + "','"
							+ loBudgetList.getCtId() + "');\">" + loBudgetList.getContractTitle() + "</a></label>";
				}
				else if (loBudgetList.getBudgetType().equalsIgnoreCase(HHSConstants.BUDGET_TYPE2))
				{
					//updated in R7 for defect 8979
					if(loBudgetList.getAutoApprovedFlag()!=null && 
							loBudgetList.getAutoApprovedFlag().equalsIgnoreCase(HHSR5Constants.ONE) && 
							loBudgetList.getStatusId().equalsIgnoreCase(HHSR5Constants.CBL_86))
					{
						lsControl = "<label class='budgetAutoModification' style='float:none'>" + "<a href=\"#\" title=\""
						+ loBudgetList.getContractTitle() + "\" onclick=\"javascript: launchBudget('"
						+ loBudgetList.getBudgetId() + "','" + loBudgetList.getContractId() + "','"
						+ loBudgetList.getFiscalYear() + "','" + loBudgetList.getBudgetType() + "','"
						+ loBudgetList.getCtId() + "');\">" + loBudgetList.getContractTitle() + "</a></label>";
					}
					else{
					lsControl = "<label class='budgetModification'>" + "<a href=\"#\" title=\""
							+ loBudgetList.getContractTitle() + "\" onclick=\"javascript: launchBudget('"
							+ loBudgetList.getBudgetId() + "','" + loBudgetList.getContractId() + "','"
							+ loBudgetList.getFiscalYear() + "','" + loBudgetList.getBudgetType() + "','"
							+ loBudgetList.getCtId() + "');\">" + loBudgetList.getContractTitle() + "</a></label>";
					}
					//R7 End
				}
				else if (loBudgetList.getBudgetType().equalsIgnoreCase((HHSConstants.BUDGET_TYPE3)))
				{
					lsControl = "<label class='ContractBudget'>" + "<a href=\"#\" title=\""
							+ loBudgetList.getContractTitle() + "\" onclick=\"javascript: launchBudget('"
							+ loBudgetList.getBudgetId() + "','" + loBudgetList.getContractId() + "','"
							+ loBudgetList.getFiscalYear() + "','" + loBudgetList.getBudgetType() + "','"
							+ loBudgetList.getCtId() + "');\">" + loBudgetList.getContractTitle() + "</a></label>";
				}
				else if (loBudgetList.getBudgetType().equalsIgnoreCase(HHSConstants.BUDGET_TYPE4))
				{
					lsControl = "<label class='BudgetUpdate'>" + "<a href=\"#\" title=\""
							+ loBudgetList.getContractTitle() + "\" onclick=\"javascript: launchBudget('"
							+ loBudgetList.getBudgetId() + "','" + loBudgetList.getContractId() + "','"
							+ loBudgetList.getFiscalYear() + "','" + loBudgetList.getBudgetType() + "','"
							+ loBudgetList.getCtId() + "');\">" + loBudgetList.getContractTitle() + "</a></label>";
				}
			}
			else
			{
				lsControl = "<label class=" + HHSR5Constants.GET_BUDGET_CLASS.get(loBudgetList.getBudgetType()) + ">"
						+ loBudgetList.getContractTitle() + "</label>";
			}
			// added for r5
		}
		else if (HHSConstants.BUDGET_VALUE.equalsIgnoreCase(aoCol.getColumnName()))
		{
			lsControl = "<label class='tableBudgetValue' style='text-align:right; float:right'>"
					+ loBudgetList.getBudgetValue() + "</label>";
		}
		/* Start : Added in R5 */
		else if ("providerName".equalsIgnoreCase(aoCol.getColumnName()))
		{
			lsControl = "<a href =\"javascript:void(0)\" onClick=\"viewOrganizationInformation(\'"
					+ loBudgetList.getOrgId() + "\',\'"
					+ StringEscapeUtils.escapeJavaScript(loBudgetList.getProviderName()) + "\')\" >"
					+ loBudgetList.getProviderName() + "</a>";
		}
		/* End : Added in R5 */
		return lsControl;

	}

	/**
	 * This Method is used to check all the checkboxes if present.
	 * 
	 * @param aoCol column name
	 * @return String
	 */
	public String getControlForHeading(Column aoCol)
	{

		return HHSConstants.RESUME;
	}
}
