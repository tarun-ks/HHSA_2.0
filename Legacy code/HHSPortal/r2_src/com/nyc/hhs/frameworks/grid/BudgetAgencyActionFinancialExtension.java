package com.nyc.hhs.frameworks.grid;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.jdom.Document;
import org.jdom.Element;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.model.ActionStatusBean;
import com.nyc.hhs.model.BudgetList;
import com.nyc.hhs.util.ActionStatusUtil;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.XMLUtil;

/**
 * 
 * Decorator class for agency to be called from budgetList Class accessed when
 * City or Agency user login.
 * 
 */
public class BudgetAgencyActionFinancialExtension implements DecoratorInterface
{

	/**
	 * <ul>
	 * This method validate the chart Allocation FYI
	 * </ul>
	 * @param aoEachObject loModifiedAllocationBean
	 * @param aoCol Column
	 * @param aoSeqNo Sequence No
	 * @return lsControl Control String
	 * @throws ApplicationException If an Application Exception occurs
	 */

	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aoSeqNo) throws ApplicationException
	{
		BudgetList loBudgetList = (BudgetList) aoEachObject;
		
		// Observation Changes Release 3.4.0.
		loBudgetList.setContractTitle(StringEscapeUtils.escapeHtml(loBudgetList.getContractTitle()));
		String lsUserRole = loBudgetList.getActions();
		StringBuilder lsControl = null;
		String lsReturn = HHSConstants.EMPTY_STRING;
		
        /*[Start] R9.7.0 QC9701 */
		String lsAgencyName = loBudgetList.getAgencyName();
		Map<Integer,String> loMap = ActionStatusUtil.getMoActionMapByAgency(lsAgencyName);
		
		ActionStatusBean loActionStatusvo = ActionStatusUtil.getMoActionByAgency(lsAgencyName);
        /*[End] R9.7.0 QC9701 */
		//<!-- [Start] R9.7.3 QC9719 -->
		boolean  lbActionEnableFlag = true ;
		if(  loBudgetList.getActionDisable()  > 0 )  { lbActionEnableFlag  = false;}
		//<!-- [End] R9.7.3 QC9719 -->
		//<!-- [Start] R9.7.6 QC9730 -->
		boolean  lbActionExceptionFlag = true ;
		if(  loBudgetList.getActionException()  > 0 )  {	lbActionExceptionFlag  = true;
		}else {   lbActionExceptionFlag  = false;  }
		//<!-- [End] R9.7.6 QC9730 -->
		//<!-- [Start] R9.7.8 QC9742 -->
		boolean  lbInvoiceExceptionFlag  =  loBudgetList.getInvoiceActionSetting();
		boolean  lbAdvanceExceptionFlag  =  loBudgetList.getAdvanceActionSetting();
		//<!-- [End] R9.7.8 QC9742 -->

		Document loDoc = (Document) BaseCacheManagerWeb.getInstance()
				.getCacheObject(HHSConstants.BUDGET_ACTION_MAPPING);
		// Code to get the organization type of User.
		if (loBudgetList.getOrgType().equalsIgnoreCase(HHSConstants.USER_AGENCY))
		{
			String lsXPathTop = HHSConstants.BUD_AG_AC_FIN_LS_XPATH_TOP_PART_1_AGENCY + loBudgetList.getBudgetType()
					+ HHSConstants.BUD_AG_AC_FIN_LS_XPATH_TOP_PART_2_AGENCY_CITY + loBudgetList.getStatusId()
					+ HHSConstants.BUD_AG_AC_FIN_LS_XPATH_TOP_PART_3_AGENCY_CITY;
			
			List<Element> loNodeList = XMLUtil.getElementList(lsXPathTop, loDoc);
			lsControl = new StringBuilder();
		    /** Start QC9149  R 7.7.0 */
			lsControl.append(HHSConstants.BUD_AG_AC_FIN_HTML_CODE_PART_1 + aoSeqNo
					+ HHSConstants.BUD_AG_AC_FIN_HTML_CODE_PART_2 + HHSConstants.BUD_AG_AC_FIN_HTML_CODE_PART_3
					+ loBudgetList.getContractTitle() + HHSConstants.BUD_AG_AC_FIN_HTML_CODE_PART_4
					+ loBudgetList.getAgencyName() + HHSConstants.BUD_AG_AC_FIN_HTML_CODE_PART_5
					+ loBudgetList.getFiscalYear() + HHSConstants.BUD_AG_AC_FIN_HTML_CODE_PART_6
					+ loBudgetList.getBudgetValue() + HHSConstants.BUD_AG_AC_FIN_HTML_CODE_PART_7
					+ loBudgetList.getDateOfLastUpdate() + HHSConstants.BUD_AG_AC_FIN_HTML_CODE_PART_8
					+ loBudgetList.getBudgetId()
					+ HHSConstants.BUD_AG_AC_FIN_HTML_CODE_PART_14
					+ loBudgetList.getNegativeAmendCnt() 
					+ HHSConstants.BUD_AG_AC_FIN_HTML_CODE_PART_9
					+ loBudgetList.getStatus() + HHSConstants.BUD_AG_AC_FIN_HTML_CODE_PART_12
					+ loBudgetList.getContractId() + HHSConstants.BUD_AG_AC_FIN_HTML_CODE_PART_13
					+ loBudgetList.getBudgetType() + HHSConstants.BUD_AG_AC_FIN_HTML_CODE_PART_10
					+ "\"><option title=" + "'I need to...'" + " value=I need to... >I need to..." 
					+ "</option>");
					//+ HHSConstants.BUD_AG_AC_FIN_HTML_CODE_PART_11);
		    /** End QC9149  R 7.7.0 */
			for (Element loNode : loNodeList)
			{
				if ((loNode.getValue().equalsIgnoreCase(HHSConstants.CBL_INITIATE_ADVANCE)))
				{
					/*[Start] R9.7.0 QC9701 */
					if( /*[Start] R9.7.3 QC9719*/
							lbActionEnableFlag &&  
							/*[End] R9.7.3 QC9719*/
							//<!-- [Start] R9.7.6 QC9730 -->
							( (lbActionExceptionFlag || lbAdvanceExceptionFlag)  || 
  						    //<!-- [End] R9.7.6 QC9730 --> 
									  (loMap != null &&  loMap.get(HHSR5Constants.ACTION_DROPDOWN_INITIATE_ADVANCE_INX) != null 
							&& loMap.get(HHSR5Constants.ACTION_DROPDOWN_INITIATE_ADVANCE_INX).equalsIgnoreCase("1") ) )  
					) {
						if (lsUserRole.equalsIgnoreCase(HHSConstants.FINANCE_STAFF_ROLE)
								|| lsUserRole.equalsIgnoreCase(HHSConstants.FINANCE_ADMIN_STAFF_ROLE)
								|| lsUserRole.equalsIgnoreCase(HHSConstants.FINANCE_MANAGER_ROLE)
								|| lsUserRole.equalsIgnoreCase(HHSConstants.CFO_ROLE))
						{
						    lsControl.append(HHSConstants.BUD_AG_AC_FIN_HTML_CODE_OPTION_PART + loNode.getValue()
									+ "'  title='" + loNode.getValue() + "'>" + loNode.getValue()
									+ HHSConstants.BUD_AG_AC_FIN_OPTION);
	
						}
					}
					/*[End] R9.7.0 QC9701 */

				}

				else if (loNode.getValue().equalsIgnoreCase("View Invoices"))
				{
					if (Integer.parseInt(loBudgetList.getInvoiceCount()) > 0)
					{
                        System.out.println(HHSConstants.BUD_AG_AC_FIN_HTML_CODE_OPTION_PART + loNode.getValue()
                                + "'  title='" + loNode.getValue() + "'>" + loNode.getValue()
                                + HHSConstants.BUD_AG_AC_FIN_OPTION);


						lsControl.append(HHSConstants.BUD_AG_AC_FIN_HTML_CODE_OPTION_PART + loNode.getValue()
								+ "'  title='" + loNode.getValue() + "'>" + loNode.getValue()
								+ HHSConstants.BUD_AG_AC_FIN_OPTION);
					}
				}
				else if (loNode.getValue().equalsIgnoreCase("View Payments"))
				{
					if (Integer.parseInt(loBudgetList.getPaymentCount()) > 0)
					{
						lsControl.append(HHSConstants.BUD_AG_AC_FIN_HTML_CODE_OPTION_PART + loNode.getValue()
								+ "'  title='" + loNode.getValue() + "'>" + loNode.getValue()
								+ HHSConstants.BUD_AG_AC_FIN_OPTION);
					}
				}
				else if (loNode.getValue().equalsIgnoreCase("View Amendment"))
				{
					if (Integer.parseInt(loBudgetList.getAmendmentCount()) > 0)
					{
						lsControl.append(HHSConstants.BUD_AG_AC_FIN_HTML_CODE_OPTION_PART + loNode.getValue()
								+ "'  title='" + loNode.getValue() + "'>" + loNode.getValue()
								+ HHSConstants.BUD_AG_AC_FIN_OPTION);
					}
				}
                else if (loNode.getValue().equalsIgnoreCase("Request Advance")   )
                {
					/*[Start] R9.7.0 QC9701 */
					if(   /*[Start] R9.7.3 QC9719*/
							lbActionEnableFlag &&  
							/*[End] R9.7.3 QC9719*/
							//<!-- [Start] R9.7.6 QC9730 -->
							( (lbActionExceptionFlag || lbAdvanceExceptionFlag) 
						    //<!-- [End] R9.7.6 QC9730 -->
							 ||  ActionStatusUtil.getActionEnabledStatus(lsAgencyName, loNode.getValue() )  ) 
							) {
	                    if (Integer.parseInt(loBudgetList.getAmendmentCount()) > 0)
	                    {
	                        lsControl.append(HHSConstants.BUD_AG_AC_FIN_HTML_CODE_OPTION_PART + loNode.getValue()
	                                + "'  title='" + loNode.getValue() + "'>" + loNode.getValue()
	                                + HHSConstants.BUD_AG_AC_FIN_OPTION);
	                    }
					}
					/*[End] R9.7.0 QC9701 */
                }
				else
				{
					/*[Start] R9.7.0 QC9701 */
					if(   /*[Start] R9.7.3 QC9719*/
							lbActionEnableFlag &&  
							/*[End] R9.7.3 QC9719*/
							//<!-- [Start] R9.7.6 QC9730 -->
							( (lbActionExceptionFlag    ) || 
						    //<!-- [End] R9.7.6 QC9730 -->
							(ActionStatusUtil.getActionInx(loNode.getValue()) < 0)  ) 
					 ) {
						lsControl.append(HHSConstants.BUD_AG_AC_FIN_HTML_CODE_OPTION_PART + loNode.getValue()
						+ "'  title='" + loNode.getValue() + "'>" + loNode.getValue()
						+ HHSConstants.BUD_AG_AC_FIN_OPTION);
					} else {
						if(	ActionStatusUtil.getActionEnabledStatus(lsAgencyName, loNode.getValue())  ) {
							lsControl.append(HHSConstants.BUD_AG_AC_FIN_HTML_CODE_OPTION_PART + loNode.getValue()
							+ "'  title='" + loNode.getValue() + "'>" + loNode.getValue()
							+ HHSConstants.BUD_AG_AC_FIN_OPTION);
					     }
					}
					/*[End] R9.7.0 QC9701 */

				}
			}
			lsControl.append(HHSConstants.BUD_AG_AC_FIN_SELECT);

		}
		else if (loBudgetList.getOrgType().equalsIgnoreCase(HHSConstants.USER_CITY))
		{
			String lsXPathTop = HHSConstants.BUD_AG_AC_FIN_LS_XPATH_TOP_PART_1_CITY + loBudgetList.getBudgetType()
					+ HHSConstants.BUD_AG_AC_FIN_LS_XPATH_TOP_PART_2_AGENCY_CITY + loBudgetList.getStatusId()
					+ HHSConstants.BUD_AG_AC_FIN_LS_XPATH_TOP_PART_3_AGENCY_CITY;
			
			List<Element> loNodeList = XMLUtil.getElementList(lsXPathTop, loDoc);
			
			lsControl = new StringBuilder();

			lsControl.append(HHSConstants.BUD_AG_AC_FIN_HTML_CODE_PART_1 + aoSeqNo
					+ HHSConstants.BUD_AG_AC_FIN_HTML_CODE_PART_2 + HHSConstants.BUD_AG_AC_FIN_HTML_CODE_PART_3
					+ loBudgetList.getContractTitle() + HHSConstants.BUD_AG_AC_FIN_HTML_CODE_PART_4
					+ loBudgetList.getAgency() + HHSConstants.BUD_AG_AC_FIN_HTML_CODE_PART_5
					+ loBudgetList.getFiscalYear() + HHSConstants.BUD_AG_AC_FIN_HTML_CODE_PART_6
					+ loBudgetList.getBudgetValue() + HHSConstants.BUD_AG_AC_FIN_HTML_CODE_PART_7
					+ loBudgetList.getDateOfLastUpdate() + HHSConstants.BUD_AG_AC_FIN_HTML_CODE_PART_8
					+ loBudgetList.getBudgetId() 
					+ HHSConstants.BUD_AG_AC_FIN_HTML_CODE_PART_9
					+ loBudgetList.getStatus() 
					+ HHSConstants.BUD_AG_AC_FIN_HTML_CODE_PART_12
					+ loBudgetList.getContractId() 
					+ HHSConstants.BUD_AG_AC_FIN_HTML_CODE_PART_13
					+ loBudgetList.getBudgetType() 
					+ HHSConstants.BUD_AG_AC_FIN_HTML_CODE_PART_15
					+ loBudgetList.getDeleteBudgetUpdateFlag()  
					+ HHSConstants.BUD_AG_AC_FIN_HTML_CODE_PART_10
					+ HHSConstants.BUD_AG_AC_FIN_HTML_CODE_PART_11);
			
			
			for (Element loNode : loNodeList)
			{
				if (loNode.getValue().equalsIgnoreCase("View Invoices"))
				{
					if (Integer.parseInt(loBudgetList.getInvoiceCount()) > 0)
					{
						lsControl.append(HHSConstants.BUD_AG_AC_FIN_HTML_CODE_OPTION_PART + loNode.getValue()
								+ "'  title='" + loNode.getValue() + "'>" + loNode.getValue()
								+ HHSConstants.BUD_AG_AC_FIN_OPTION);
					}
				}
				else if (loNode.getValue().equalsIgnoreCase("View Payments"))
				{
					if (Integer.parseInt(loBudgetList.getPaymentCount()) > 0)
					{
						lsControl.append(HHSConstants.BUD_AG_AC_FIN_HTML_CODE_OPTION_PART + loNode.getValue()
								+ "'  title='" + loNode.getValue() + "'>" + loNode.getValue()
								+ HHSConstants.BUD_AG_AC_FIN_OPTION);
					}
				}
				else if (loNode.getValue().equalsIgnoreCase("View Amendment"))
				{
					if (Integer.parseInt(loBudgetList.getAmendmentCount()) > 0)
					{
						lsControl.append(HHSConstants.BUD_AG_AC_FIN_HTML_CODE_OPTION_PART + loNode.getValue()
								+ "'  title='" + loNode.getValue() + "'>" + loNode.getValue()
								+ HHSConstants.BUD_AG_AC_FIN_OPTION);
					}
				}
				// Start R7 for defect 8644 for cancel and merge, it will show the "Cancel and Merge" option in the Budget List when the budget satisfies the 
				//given conditions.
				else 
				{
					/*[Start] R9.7.0 QC9701 */
					if(  /*[Start] R9.7.3 QC9719*/
							lbActionEnableFlag &&  
							/*[End] R9.7.3 QC9719*/
							//<!-- [Start] R9.7.6 QC9730 -->
							( lbActionExceptionFlag || 
						    //<!-- [End] R9.7.6 QC9730 -->
							(loMap != null &&  loMap.get(HHSR5Constants.ACTION_DROPDOWN_CANCEL_MERGE_INX) != null 
							&& loMap.get(HHSR5Constants.ACTION_DROPDOWN_CANCEL_MERGE_INX).equalsIgnoreCase("1") )  ) 
						) {
				    /*[End] R9.7.0 QC9701 */
						if (Integer.parseInt(loBudgetList.getMergeOutYearFlag()) > 0 
								&& (loBudgetList.getStatusId().equalsIgnoreCase(HHSConstants.BUDGET_PENDING_SUBMISSION_STATUS_ID) 
										||loBudgetList.getStatusId().equalsIgnoreCase(HHSConstants.BUDGET_RETURNED_FOR_REVISION_STATUS_ID)))
					    {
						    lsControl.append(HHSConstants.BUD_AG_AC_FIN_HTML_CODE_OPTION_PART + HHSConstants.CANCEL_MERGE
								+ "'  title='" + HHSConstants.CANCEL_MERGE + "'>" + HHSConstants.CANCEL_MERGE
								+ HHSConstants.BUD_AG_AC_FIN_OPTION);
	
					    }
					}

					/*[Start] R9.7.0 QC9701 */
					if(   /*[Start] R9.7.3 QC9719*/
							lbActionEnableFlag &&  
							/*[End] R9.7.3 QC9719*/ 
							//<!-- [Start] R9.7.6 QC9730 -->
							( lbActionExceptionFlag || 
						  //<!-- [End] R9.7.6 QC9730 -->
						  ActionStatusUtil.getActionEnabledStatus(lsAgencyName, loNode.getValue() ) )
					) {
						lsControl.append(HHSConstants.BUD_AG_AC_FIN_HTML_CODE_OPTION_PART + loNode.getValue()
								+ "'  title='" + loNode.getValue() + "'>" + loNode.getValue()  + HHSConstants.BUD_AG_AC_FIN_OPTION);
					}
					/*[End] R9.7.0 QC9701 */

				}
			}
			// Added in R7 for Cost Center
			//updated for defect 8763- Pending Approval-82 check added
			if (null != loBudgetList.getCostCenterOpted()
					&& Integer.parseInt(loBudgetList.getCostCenterOpted()) > 0
					&& ((loBudgetList.getStatusId().equalsIgnoreCase(HHSConstants.BUDGET_PENDING_SUBMISSION_STATUS_ID) 
							|| loBudgetList.getStatusId().equalsIgnoreCase(HHSConstants.BUDGET_RETURNED_FOR_REVISION_STATUS_ID) 
							|| loBudgetList.getStatusId().equalsIgnoreCase(HHSConstants.BUDGET_APPROVED_STATUS_ID)	
							) && !loBudgetList.getBudgetType()
							.equalsIgnoreCase(HHSConstants.BUDGET_TYPE2)))
			{
				lsControl.append(HHSConstants.BUD_AG_AC_FIN_HTML_CODE_OPTION_PART + HHSR5Constants.UPDATE_SERVICES
						+ "'  title='" + HHSR5Constants.UPDATE_SERVICES + "'>" + HHSR5Constants.UPDATE_SERVICES
						+ HHSConstants.BUD_AG_AC_FIN_OPTION);
			}
			// End R7
			
			// Start Added in R 8.4.0 QC 9490 add action  Delete Budget Update for Budget Update
			if ( ( (null != loBudgetList.getDeleteBudgetUpdateFlag() && loBudgetList.getDeleteBudgetUpdateFlag() == 0)
					|| (null == loBudgetList.getDeleteBudgetUpdateFlag() )
					&& ((loBudgetList.getStatusId().equalsIgnoreCase(HHSConstants.BUDGET_PENDING_SUBMISSION_STATUS_ID) 
							|| loBudgetList.getStatusId().equalsIgnoreCase(HHSConstants.BUDGET_RETURNED_FOR_REVISION_STATUS_ID) 
							|| loBudgetList.getStatusId().equalsIgnoreCase(HHSConstants.BUDGET_APPROVED_STATUS_ID)	
							) 
					&& loBudgetList.getBudgetType().equalsIgnoreCase(HHSConstants.BUDGET_TYPE4)) ))
			{
				lsControl.append(HHSConstants.BUD_AG_AC_FIN_HTML_CODE_OPTION_PART + HHSR5Constants.DELETE_BUDGET_UPDATE
						+ "'  title='" + HHSR5Constants.DELETE_BUDGET_UPDATE + "'>" + HHSR5Constants.DELETE_BUDGET_UPDATE
						+ HHSConstants.BUD_AG_AC_FIN_OPTION);
			}
			// End Added in R 8.4.0 QC 9490 add action  Delete Budget Update for Budget Update
			
			lsControl.append(HHSConstants.BUD_AG_AC_FIN_SELECT);
			
		}
		if (null != lsControl)
		{
		    if(loBudgetList.getUserSubRole().equalsIgnoreCase(ApplicationConstants.ROLE_OBSERVER)){
		        CommonUtil.keepReadOnlyActions(lsControl);
		    }
		    
			lsReturn = lsControl.toString();
		}
		return lsReturn;
	}

	/**
	 * This Method is used to check all the check boxes if present.
	 * @param aoCol column name
	 * @return String
	 */
	public String getControlForHeading(Column aoCol)
	{
		return HHSConstants.RESUME;
	}
	

}
