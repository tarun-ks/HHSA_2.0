package com.nyc.hhs.frameworks.grid;

import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.model.ReturnedPayment;
import com.nyc.hhs.util.ActionStatusUtil;
import com.nyc.hhs.util.XMLUtil;

/**
 * Added in Release 6: This class is used to create custom select element for
 * returned payment check grid view. Values in Action column on returned payment
 * list page for Agency are populated using it as a decorator class in grid.
 * 
 */
public class ReturnedPaymentAgencyActionExtension implements DecoratorInterface
{

	@Override
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aoSeqNo) throws ApplicationException
	{

		ReturnedPayment loReturnedPayment = (ReturnedPayment) aoEachObject;
		StringBuilder lsControl = new StringBuilder();
		
		if ("action".equalsIgnoreCase(aoCol.getColumnName()))
		{
			String lsStatusId = loReturnedPayment.getCheckStatus();

			Document loDoc = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
					HHSR5Constants.RETURNED_PAYMENT_MAPPING_FILE);

/*[Start] QC9701   */
			String trace1 =  loReturnedPayment.getAgencyId() + "||" + loReturnedPayment.getAction() + "@" + ActionStatusUtil.getMoActionByAgency(loReturnedPayment.getAgencyId()).toStringBi();
/*[End] QC9701   */
			lsControl.append("<select name=action" + aoSeqNo + " class='contractAmend' id='action" + aoSeqNo
					+ "' style='width: 150px' onchange=\"onSelectChange(this);\"" + "><option title="
					+ "'I need to...'" + "value=I need to... >I need to...</option>");
			String lsXPathTop = HHSConstants.EMPTY_STRING;
			if (HHSConstants.USER_AGENCY.equalsIgnoreCase(loReturnedPayment.getLoggedInUserOrgType())
					&& ((HHSConstants.CFO_ROLE.equalsIgnoreCase(loReturnedPayment.getLoggedInUserRole())
							|| HHSConstants.FINANCE_MANAGER_ROLE.equalsIgnoreCase(loReturnedPayment
									.getLoggedInUserRole())
							|| HHSConstants.FINANCE_ADMIN_STAFF_ROLE.equalsIgnoreCase(loReturnedPayment
									.getLoggedInUserRole()) || HHSConstants.FINANCE_STAFF_ROLE
								.equalsIgnoreCase(loReturnedPayment.getLoggedInUserRole())) && (HHSR5Constants.STATUS_EIGHTY_FIVE
							.equalsIgnoreCase(loReturnedPayment.getBudgetStatusId())
							|| HHSR5Constants.STATUS_EIGHTY_SEVEN.equalsIgnoreCase(loReturnedPayment
									.getBudgetStatusId()) || HHSR5Constants.STATUS_EIGHTY_EIGHT
								.equalsIgnoreCase(loReturnedPayment.getBudgetStatusId()))))
			{
				lsXPathTop = "//page[(@name=\"returnedPaymentAgency\")]//status";
			}
			else
			{
				lsXPathTop = "//page[(@name=\"returnedPaymentProviderCity\")]//status";
			}
			List<Element> loNodeList = XMLUtil.getElementList(lsXPathTop, loDoc);
			for (Element loNode : loNodeList)
			{
				if (loNode.getAttribute("name").getValue().equals(lsStatusId))
				{
					lsControl = getControle(lsControl, loNode, loReturnedPayment, loReturnedPayment.getAgencyId());
					break;
				}
			}
			lsControl.append("</select>");
		}
		else if ("checkAmount".equalsIgnoreCase(aoCol.getColumnName()))
		{ //Fix for defect of returned payment alignment grid
			lsControl = lsControl.append("<label class='tableCheckAmountValue' style='text-align:right; float:right; margin-right: 23%'>"
					+ loReturnedPayment.getCheckAmount() + "</label>");
		}
		// Added else-if statement for enhancement 8652
		else if(HHSR5Constants.CHECK_NUMBER.equalsIgnoreCase(aoCol.getColumnName()))
		{
			String lsCheckNumber = null;
			if (null != loReturnedPayment.getCheckNumber())
			{
				// Below if-else is checking that whether to show complete check number or only last four digits
				if (loReturnedPayment.getCheckNumber().length() > 4)
				{
					lsCheckNumber = loReturnedPayment.getCheckNumber().substring(
							loReturnedPayment.getCheckNumber().length() - 4,
							loReturnedPayment.getCheckNumber().length());
				}
				else
				{
					lsCheckNumber = loReturnedPayment.getCheckNumber();
				}
				
				//Below if-else is checking whether to show tool-tip or not
				if (loReturnedPayment.getCheckNumber().contains(HHSConstants.HYPHEN))
				{
					lsControl = lsControl.append("<label title='' style='text-align:left; float:left'>" + lsCheckNumber
							+ "</label>");
				}
				else
				{
					lsControl = lsControl.append("<label title=" + loReturnedPayment.getCheckNumber()
							+ " style='text-align:left; float:left'>" + lsCheckNumber + "</label>");
				}
			}
			
		}

		return lsControl.toString();
	}

	/**
	 * This Method is used to check all the checkboxes if present.
	 * 
	 * @param aoCol column name
	 * @return lsControl - String
	 */
	public String getControlForHeading(Column aoCol)
	{

		String lsControl = HHSConstants.RESUME;
		return lsControl;
	}

	/**
	 * This Method is used to build controle from node.
	 * 
	 * @param asControl - StringBuilder object
	 * @param aoNode - Element object
	 * @throws ApplicationException
	 */
	private void setNodeValue(StringBuilder asControl, Element aoNode, String asId)
	{
		asControl.append("<option title=" + "'" + aoNode.getValue() + "'" + " value=\"" + aoNode.getValue() + "-" + asId
				+ "\">" + aoNode.getValue() + "</option>");
	}

	/**
	 * This Method is used to build controle from node.<br>
	 * 
	 * @param asControl - StringBuilder object
	 * @param aoNode - Element object
	 * @param aoReturnedPayment - ReturnedPayment Object
	 * @return asControl - StringBuilder Object
	 * @throws ApplicationException - ApplicationException Object
	 * 
	 */
	@SuppressWarnings("unchecked")
	private StringBuilder getControle(StringBuilder asControl, Element aoNode, ReturnedPayment aoReturnedPayment, String aoAgencyId)
			throws ApplicationException
	{
		List<Element> loElements = (List<Element>) aoNode.getChildren();
		
		boolean lbRtnPayCancel = ActionStatusUtil.isActionEnabled(aoAgencyId, HHSR5Constants.ACTION_DROPDOWN_CANCEL_RETURN_PAYMENT_INX);
		/* [Start] R9.7.3 QC9719 */
		if(  aoReturnedPayment.getActionDisable()  > 0 )  { lbRtnPayCancel  = false;}
		/* [end] R9.7.3 QC9719 */
		//<!-- [Start] R9.7.6 QC9730 -->
		boolean  lbActionExceptionFlag = true ;
		if(  aoReturnedPayment.getActionException()  > 0 )  {	lbActionExceptionFlag  = true;
		}else {   lbActionExceptionFlag  = false;  }
		//<!-- [End] R9.7.6 QC9730 -->
		
		for (Element loNode : loElements)
		{
			/*[Start] QC9701   */
			if( (/*//<!-- [Start] R9.7.6 QC9730 -->*/
					   lbActionExceptionFlag || 
						  /*<!-- [End] R9.7.6 QC9730 -->*/
					loNode.getValue().equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_CANCEL_RETURN_PAYMENT_NAME)
				)   
					&&  lbRtnPayCancel == false ) {
				continue;
			}
			/*[End] QC9701   */	
			setNodeValue(asControl, loNode, aoReturnedPayment.getReturnedPaymentId());
		}
		return asControl;
	}
}
