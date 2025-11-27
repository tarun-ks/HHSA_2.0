package com.nyc.hhs.frameworks.grid;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.jdom.Document;
import org.jdom.Element;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.model.ActionStatusBean;
import com.nyc.hhs.model.BudgetList;
import com.nyc.hhs.util.ActionStatusUtil;
import com.nyc.hhs.util.XMLUtil;

/**
 * 
 * Decorator class for Provider to be called from budgetList Class accessed when
 * provider user login.
 * 
 */
public class BudgetProviderActionFinancialExtension implements DecoratorInterface
{

	private boolean getActionStatus(BudgetList  aoBudgetList) {
		
		return true;
	}
	
	/**
	 * This Method is used to control for column. Modfied this method for build
	 * 3.2.0, defect id 6384.
	 * 
	 * @param aoEachObject Object
	 * @param aoCol - Column object
	 * @param aoSeqNo - Integer object
	 * @return String
	 * @throws ApplicationException
	 */
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aoSeqNo) throws ApplicationException
	{
		BudgetList loBudgetList = (BudgetList) aoEachObject;
		// Observation Changes Release 3.4.0.
		loBudgetList.setContractTitle(StringEscapeUtils.escapeHtml(loBudgetList.getContractTitle()));
		String lsUserRole = loBudgetList.getActions();
		Document loDoc = (Document) BaseCacheManagerWeb.getInstance()
				.getCacheObject(HHSConstants.BUDGET_ACTION_MAPPING);
		// Code to get the organization type of User.
		String lsXPathTop = "//page[(@name=\"budgetProviderAction\")]//type[(@name=\"" + loBudgetList.getBudgetType()
				+ "\")]//status[(@name=\"" + loBudgetList.getStatusId() + "\")]//action";
		List<Element> loNodeList = XMLUtil.getElementList(lsXPathTop, loDoc);
		StringBuilder lsControl = new StringBuilder();

        /*[Start] R9.6.4 QC9701 */
		String lsAgencyName = loBudgetList.getAgencyName();

        /*[End] R9.6.4 QC9701 */

		//<!-- [Start] R9.7.3 QC9719 -->
		boolean  lbActionEnableFlag = true ;
		if(  loBudgetList.getActionDisable()  > 0)  { lbActionEnableFlag  = false;}
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
	    /** Start QC9149  R 7.7.0 */
		lsControl.append("<select name=action" + aoSeqNo
				+ " class='budgetRequestAdvance' id='budgetRequestAdvance' style='width: 150px' "
				+ "procurementTitle=\"" + loBudgetList.getContractTitle() + "\" providerName=\""
				+ loBudgetList.getProviderName() + "\" fiscalYear=\"" + loBudgetList.getFiscalYear()
				+ "\" budgetValue=\"" + loBudgetList.getBudgetValue() + "\" agencyName=\""
				+ loBudgetList.getAgencyName() + "\" dateOfLastUpdate=\"" + loBudgetList.getDateOfLastUpdate()
				+ "\" budgetId=\"" + loBudgetList.getBudgetId() + "\" programId=\"" + loBudgetList.getProgramId()
				+ "\" contractId=\"" + loBudgetList.getContractId() + "\" ctId=\"" + loBudgetList.getCtId()
				+ HHSConstants.BUD_AG_AC_FIN_HTML_CODE_PART_14 + loBudgetList.getNegativeAmendCnt()
				+ "\" budgetType=\"" + loBudgetList.getBudgetType() + "\" functionName=\"requestAdvance" + "\">"
				+ "<option title=" + "'I need to...'" + " value=I need to... >I need to..."
				+ "</option>");
	    /** End QC9149  R 7.7.0 */
		// Release 5 Contract Restriction

		for (Element loNode : loNodeList)
		{
			if ((
			/* loNode.getValue().equalsIgnoreCase(HHSConstants.CBL_REQUEST_ADV) || */ 
					loNode.getValue().equalsIgnoreCase(HHSConstants.CBL_CANCEL_MODIFICATION)))
			{
				// build 3.2.0, defect id 6384
				if (!(lsUserRole.toUpperCase().contains(HHSConstants.STAFF.toUpperCase()))
						&& loBudgetList.getIsFandFP()     
						/*[Start] R9.6.4 QC9701 *//*[Start] R9.6.4 QC9719 */
						
						&& (  //<!-- [Start] R9.7.6 QC9730 -->
								( lbActionExceptionFlag || 
							  //<!-- [End] R9.7.6 QC9730 -->
								ActionStatusUtil.getActionEnabledStatus(lsAgencyName, loNode.getValue()) ) 
								/*<!-- [Start] R9.7.7 QC9719 -->*/
								&& lbActionEnableFlag
								/*<!-- [End] R9.7.7 QC9719 -->*/
								)   
						 /*[End] R9.6.4 QC9701 *//*[End] R9.6.4 QC9719 */
						)
				{
					// Start Release 5 user notification
					if (loBudgetList.isContractAccess() && loBudgetList.isUserAccess())
					{
						setNodeValue(lsControl, loNode);
					}
					// End Release 5 user notification

				}
			}
			else if (loNode.getValue().equalsIgnoreCase("View Invoices"))
			{
				if (Integer.parseInt(loBudgetList.getInvoiceCount()) > 0)
				{
					setNodeValue(lsControl, loNode);
				}
			}
			else if (loNode.getValue().equalsIgnoreCase("View Payments"))
			{
				if (Integer.parseInt(loBudgetList.getPaymentCount()) > 0)
				{
					setNodeValue(lsControl, loNode);
				}
			}
			else if (loNode.getValue().equalsIgnoreCase("View Contract"))
			{
				setNodeValue(lsControl, loNode);
			}
			else if (loNode.getValue().equalsIgnoreCase("View Amendment"))
			{
				if (Integer.parseInt(loBudgetList.getAmendmentCount()) > 0)
				{
					setNodeValue(lsControl, loNode);
				}
			}
			// Start Release 5 user notification
			else if ((loNode.getValue().equalsIgnoreCase("Modify Budget")
					|| loNode.getValue().equalsIgnoreCase("Withdraw")
					))
			{
				/*[Start] R9.6.4 QC9701 */
				if(/*[Start] R9.7.5 QC9719*/
						lbActionEnableFlag &&  
						/*[End] R9.7.5 QC9719*/
						   /*<!-- [Start] R9.7.6 QC9730 -->*/
						   ( lbActionExceptionFlag || 
					       /*<!-- [End] R9.7.6 QC9730 --> */ 
								ActionStatusUtil.getActionEnabledStatus(lsAgencyName, loNode.getValue() ) 
							) 
						) {
					if (loBudgetList.isContractAccess() && loBudgetList.isUserAccess())
					{
						lsControl.append("<option title='" + loNode.getValue() + "' value='" + loNode.getValue() + "'>"
								+ loNode.getValue() + "</option>");
					}
				}
				/*[End] R9.6.4 QC9701 */
			}
			// End Release 5 user notification
			/*[Start] R9.7.8 QC9742*/
			else if ( loNode.getValue().equalsIgnoreCase(HHSConstants.CBL_REQUEST_ADV)  )
			{
				/*[Start] R9.6.4 QC9701 */
				if(/*[Start] R9.7.5 QC9719*/
						lbActionEnableFlag &&  
						/*[End] R9.7.5 QC9719*/
						   /*<!-- [Start] R9.7.6 QC9730 -->*/
						   ( ( lbActionExceptionFlag  || lbAdvanceExceptionFlag ) || 
					       /*<!-- [End] R9.7.6 QC9730 --> */ 
								ActionStatusUtil.getActionEnabledStatus(lsAgencyName, loNode.getValue() ) 
							) 
						) {
					if (loBudgetList.isContractAccess() && loBudgetList.isUserAccess())
					{
						lsControl.append("<option title='" + loNode.getValue() + "' value='" + loNode.getValue() + "'>"
								+ loNode.getValue() + "</option>");
					}
				}
				/*[End] R9.6.4 QC9701 */
			}
			else if (  loNode.getValue().equalsIgnoreCase("Submit Invoice")    )
			{
				/*[Start] R9.6.4 QC9701 */
				if(/*[Start] R9.7.5 QC9719*/
						lbActionEnableFlag &&  
						/*[End] R9.7.5 QC9719*/
						   /*<!-- [Start] R9.7.6 QC9730 -->*/
						   ( (lbActionExceptionFlag   || lbInvoiceExceptionFlag ) || 
					       /*<!-- [End] R9.7.6 QC9730 --> */ 
								ActionStatusUtil.getActionEnabledStatus(lsAgencyName, loNode.getValue() ) 
							) 
						) {
					if (loBudgetList.isContractAccess() && loBudgetList.isUserAccess())
					{
						lsControl.append("<option title='" + loNode.getValue() + "' value='" + loNode.getValue() + "'>"
								+ loNode.getValue() + "</option>");
					}
				}
				/*[End] R9.6.4 QC9701 */
			}

			/*[End] R9.7.8 QC9742*/			
			else if (loBudgetList.isContractAccess() && loBudgetList.isUserAccess())
			{
				setNodeValue(lsControl, loNode);
			}
		}

		lsControl.append("</select>");
		return lsControl.toString();

	}

	// added for release 5
	/**
	 * This Method is used to build controle from node.
	 * 
	 * @param lsControl - StringBuilder object
	 * @param loNode - Element object
	 */
	private void setNodeValue(StringBuilder lsControl, Element loNode)
	{
		lsControl.append("<option title='" + loNode.getValue() + "' value='" + loNode.getValue() + "'>"
				+ loNode.getValue() + "</option>");
	}

	
	
	// added for release 5
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
