package com.nyc.hhs.frameworks.grid;

import java.util.List;
import java.util.Map;

import javax.portlet.PortletSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.jdom.Document;
import org.jdom.Element;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.model.ActionStatusBean;
import com.nyc.hhs.model.InvoiceList;
import com.nyc.hhs.util.ActionStatusUtil;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.XMLUtil;

/**
 * This class is used to create custom select element for inview list grid view.
 * Values in Action column on invoice list page for Acclerator/Agency are
 * populated using it as a decorator class in grid.
 * 
 * The method getControlForColumn() will get the Object instance and we will
 * fetch the bean details from this object.
 * <ul>
 * <li>Create a local InvoiceList object and parse the Object instance to
 * InvoiceList object.
 * <li>Create your empty String object as lsControl
 * <li>We will need the organisation type for defining the hyper links for each
 * bean
 * <li>As per FD the Withdraw link will be displayed only for the provider users
 * and status as "Returned for revision"
 * <li>The Delete Link will be displayed only for the Provider users and the
 * status as "Pending submission"
 * <li>Now we need to compare for the Organisation type and status and then
 * populate the lsControl with the Hyper link
 * <li>The Hyper link will also contain the java script with it which will open
 * the screens for them.
 * <li>We have put the hyper link for view for all the bean objects.
 * <ul>
 * 
 * @Author virender.x.kumar
 * 
 */
public class InvoiceActionExtension implements DecoratorInterface
{

	/**
	 * This method will generate the action depending upon the invoice status.
	 * On the click of the hyper links present for that particular invoice, we
	 * get actions to be performed on that invoice.
	 * 
	 * @param aoEachObject an object of list to be displayed in grid
	 * @param aoCol a column object
	 * @param aiSeqNo an integer value of sequence number
	 * @return a string value of html code formed
	 * 
	 * @throws ApplicationException throws ApplictionException
	 * 
	 */
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aiSeqNo) throws ApplicationException
	{

		InvoiceList loInvoiceList = (InvoiceList) aoEachObject;
		// Observation Changes Release 3.4.0.
		loInvoiceList.setInvoiceContractTitle(StringEscapeUtils.escapeHtml(loInvoiceList.getInvoiceContractTitle()));
		String lsControl = HHSConstants.EMPTY_STRING;
		String lsUserOrg = loInvoiceList.getOrgType();
		String lsInvoiceStatusId = loInvoiceList.getInvoiceStatusId();

        /*[Start] R9.6.4 QC9701 */
		String lsInvoiceAgencyId = loInvoiceList.getInvoiceAgencyId();   
		Map<Integer,String> loMap = ActionStatusUtil.getMoActionMapByAgency(lsInvoiceAgencyId);
		ActionStatusBean loActionStat = ActionStatusUtil.getMoActionByAgency(lsInvoiceAgencyId);
		String trace1 = null;
		
		if(loMap != null) {
			trace1 =lsInvoiceAgencyId +":" + lsUserOrg + "|"+ loActionStat.toStringBi()  ;
		}
        /*[End] R9.6.4 QC9701 */		
		//<!-- [Start] R9.7.3 QC9719 -->
		boolean  lbActionEnableFlag = true ;
		if( loInvoiceList.getActionDisable()  > 0 )  { lbActionEnableFlag  = false;}
		//<!-- [End] R9.7.3 QC9719 -->
		//<!-- [End] R9.7.3 QC9719 -->   
		boolean  lbActionExceptionFlag = true ;
		if(  loInvoiceList.getActionException()  > 0 )  {	lbActionExceptionFlag  = true;
		}else {   lbActionExceptionFlag  = false;  }
		//<!-- [End] R9.7.6 QC9730 -->
		
		
		if (HHSConstants.INVOICE_DATE_APPROVED.equalsIgnoreCase(aoCol.getColumnName()))
		{
			if (null != loInvoiceList.getInvoiceDateApproved() && !loInvoiceList.getInvoiceDateApproved().isEmpty())
			// changed the code for Defect 4283 for Date Approved as We need the
			// Approved
			// Date to be displayed for all cases where Date is there in DB
			{
				lsControl = DateUtil.getDateByFormat(HHSConstants.YYYY_MM_DD_HH_MM_SS, HHSConstants.MMDDYYFORMAT,
						loInvoiceList.getInvoiceDateApproved());
			}
			else
			{
				lsControl = HHSConstants.NA_KEY;
			}
		}
		else if (HHSConstants.INVOICE_DATE_SUBMITTED.equalsIgnoreCase(aoCol.getColumnName()))
		{
			if (null != loInvoiceList.getInvoiceDateSubmitted() && !loInvoiceList.getInvoiceDateSubmitted().isEmpty())
			// changed the code for Defect 4283 for Date Approved as We need the
			// Approved
			// Date to be displayed for all cases where Date is there in DB
			{
				lsControl = DateUtil.getDateByFormat(HHSConstants.YYYY_MM_DD_HH_MM_SS, HHSConstants.MMDDYYFORMAT,
						loInvoiceList.getInvoiceDateSubmitted());
			}
			else
			{
				lsControl = HHSConstants.NOT_SUBMITTED;
			}
		}
		else if (HHSConstants.INVOICE_VALUE.equalsIgnoreCase(aoCol.getColumnName()))
		{

			lsControl = lsControl.concat("<label class='tableInvoiceValue alignRht floatRht'>"
					+ loInvoiceList.getInvoiceValue() + "</label>");

		}
		// Changes for enhancement id 6461 release 3.4.0
		else if (HHSConstants.INVOICE_CT_ID.equalsIgnoreCase(aoCol.getColumnName()))
		{
			String lsCtId = loInvoiceList.getInvoiceCtId();
			if (lsCtId != null)
			{
				if (loInvoiceList.isContractAccess() && loInvoiceList.isUserAccess())
				{
					lsControl = "<label>" + "<a href=\"#\" title=\"" + loInvoiceList.getInvoiceContractTitle()
							+ "\" onclick=\"javascript: launchBudget('" + loInvoiceList.getInvoiceBudgetId() + "','"
							+ loInvoiceList.getInvoiceContractId() + "','" + loInvoiceList.getInvoiceFiscalYearId()
							+ "','" + HHSConstants.BUDGET_TYPE3 + "','" + loInvoiceList.getInvoiceCtId() + "');\">"
							+ loInvoiceList.getInvoiceCtId() + "</a></label>";
				}
				else
				{
					lsControl = lsControl.concat("<label>" + loInvoiceList.getInvoiceCtId() + "</label>");
				}
			}
		}
		else if (HHSConstants.INVOICE_ACTION.equalsIgnoreCase(aoCol.getColumnName()))
		{
			/* Start : Added in R5 */
			Document loDoc = (Document) BaseCacheManagerWeb.getInstance().getCacheObject("financeInvoiceListMapping");
			lsControl = lsControl.concat("<select name=action" + aiSeqNo + " class='contractAmend' id='action"
					+ aiSeqNo + "' style='width: 150px' contractTitle=\"" + loInvoiceList.getInvoiceContractTitle()
					+ "\" invoiceId=\"" + loInvoiceList.getInvoiceId() + "\" contractId=\""
					+ loInvoiceList.getInvoiceContractId() + "\" budgetId=\"" + loInvoiceList.getInvoiceBudgetId()
					+ "\"><option title='I need to...' value='I need to...'>I need to...</option>");

			if (ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(lsUserOrg))
			{
				String lsXPathTop = "//page[(@name=\"invoiceListProvider\")]//status[(@name=\"" + lsInvoiceStatusId
						+ "\")]//action";

				lsControl = getOptionData(lsControl, loDoc, lsXPathTop, loInvoiceList,
						loInvoiceList.isContractAccess(), loInvoiceList.isUserAccess()  , lsInvoiceAgencyId);
			}
			else
			{
				String lsXPathTop = "//page[(@name=\"invoiceListAgencyOrCity\")]//status[(@name=\"" + lsInvoiceStatusId
						+ "\")]//action";
				lsControl = getOptionData(lsControl, loDoc, lsXPathTop, loInvoiceList,
						loInvoiceList.isContractAccess(), loInvoiceList.isUserAccess()  , lsInvoiceAgencyId );
			}

			lsControl = lsControl.concat("</select>");
			/* End : Added in R5 */
		}
		/* Start : Added in R5 */
		else if (aoCol.getColumnName().equalsIgnoreCase("invoiceProvider"))
		{
			lsControl = "<a href =\"javascript:void(0)\" onClick=\"viewOrganizationInformation(\'"
					+ loInvoiceList.getOrgId() + "\',\'"
					+ StringEscapeUtils.escapeJavaScript(loInvoiceList.getInvoiceProvider()) + "\')\" >"
					+ loInvoiceList.getInvoiceProvider() + "</a>";
		}
		/* End : Added in R5 */

        /*[Start] R7.2.0 QC8914 Set indicator for Access control     */
		StringBuilder loCon = new StringBuilder(lsControl); 
        if(lsControl!=null
                && loInvoiceList.getUserSubRole().equalsIgnoreCase(ApplicationConstants.ROLE_OBSERVER)){
            CommonUtil.keepReadOnlyActions(loCon);
        }
        /*[End] R7.2.0 QC8914 Set indicator for Access control     */ 

		return loCon.toString();
	}

	/* Start : Added in R5 */
	/**
	 * This method will generate the action depending upon the invoice status.
	 * On the click of the hyper links present for that particular invoice, we
	 * get actions to be performed on that invoice.
	 * 
	 * @param lsControl
	 * @param loDoc
	 * @param lsXPathTop
	 * @param loInvoiceList
	 * @param aoContractAccess
	 * @param aoUserAccess
	 * @return a string value lsControl
	 * 
	 * @throws ApplicationException throws ApplictionException
	 * 
	 */
	private String getOptionData(String lsControl, Document loDoc, String lsXPathTop, InvoiceList loInvoiceList,
			Boolean aoContractAccess, Boolean aoUserAccess , String aoInvoiceAgencyId) throws ApplicationException
	{
		//<!-- [Start] R9.7.3 QC9719 -->
		boolean  lbActionEnableFlag = true ;
		if(  loInvoiceList.getActionDisable()  > 0 )  { lbActionEnableFlag  = false;}
		//<!-- [End] R9.7.3 QC9719 -->
		//<!-- [End] R9.7.3 QC9719 -->   
		boolean  lbActionExceptionFlag = true ;
		if(  loInvoiceList.getActionException()  > 0 )  {	lbActionExceptionFlag  = true;
		}else {   lbActionExceptionFlag  = false;  }
		//<!-- [End] R9.7.6 QC9730 -->

		
		List<Element> loNodeList = XMLUtil.getElementList(lsXPathTop, loDoc);
		for (Element loNode : loNodeList)
		{

			if (loNode.getValue().equalsIgnoreCase("View Payments"))
			{
				if (Integer.parseInt(loInvoiceList.getPaymentCount()) > 0)
				{
					lsControl = lsControl.concat("<option title='" + loNode.getValue() + "' value=\""
							+ loNode.getValue() + "\">" + loNode.getValue() + "</option>");
				}
			}
			else
			{
				// Start Release 5 user notification
				if ((loNode.getValue().equalsIgnoreCase("Withdraw") || loNode.getValue().equalsIgnoreCase("Delete")))
				{
					/*[Start]   QC9701 */
					if(/*[Start] R9.7.5 QC9719*/
							lbActionEnableFlag &&  
							/*[End] R9.7.5 QC9719*/
							/*//<!-- [Start] R9.7.6 QC9730 -->*/
							   (lbActionExceptionFlag || 
									  /*<!-- [End] R9.7.6 QC9730 -->*/
							   ActionStatusUtil.getActionEnabledStatus(aoInvoiceAgencyId, loNode.getValue() )
							   )
							) {
					/*[End]   QC9701 */
	
						if (aoContractAccess && aoUserAccess  )
						{
							lsControl = lsControl.concat("<option title='" + loNode.getValue() + "' value=\""
									+ loNode.getValue() + "\">" + loNode.getValue() + "</option>");
						}
					}

				}
				else if ((loNode.getValue().equalsIgnoreCase("View Invoice")))
				{
					if (aoContractAccess && aoUserAccess)
					{
						lsControl = lsControl.concat("<option title='" + loNode.getValue() + "' value=\""
								+ loNode.getValue() + "\">" + loNode.getValue() + "</option>");
					}
				}
				else
				{
					lsControl = lsControl.concat("<option title='" + loNode.getValue() + "' value=\""
							+ loNode.getValue() + "\">" + loNode.getValue() + "</option>");
				}
				// Start Release 5 user notification
			}
		}
		return lsControl;
	}

	/* End : Added in R5 */
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
