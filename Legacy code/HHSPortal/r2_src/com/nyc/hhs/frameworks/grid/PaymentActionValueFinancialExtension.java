package com.nyc.hhs.frameworks.grid;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.model.PaymentSortAndFilter;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This class will be used to create custom select element for document grid
 * view
 * 
 */
public class PaymentActionValueFinancialExtension implements DecoratorInterface
{

	/**
	 * This method modified as a part of release 3.1.0 for enhancement 6023 to
	 * add status for rejected payment
	 * 
	 * This method is used to create drop down for finance payment screen grid
	 * table.
	 * 
	 * @param aoEachObject an object of list to be displayed in grid
	 * @param aoCol a column object
	 * @param aoSeqNo an integer value of sequence number
	 * @return a string value of html code formed
	 * @throws ApplicationException ApplicationException incase any query fails
	 */
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aoSeqNo) throws ApplicationException
	{
		PaymentSortAndFilter loPaymentList = (PaymentSortAndFilter) aoEachObject;
		// Observation Changes Release 3.4.0.
		loPaymentList.setPaymentContractTitle((StringEscapeUtils.escapeHtml(loPaymentList.getPaymentContractTitle())));
		String lsControl = HHSConstants.EMPTY_STRING;
		if (aoCol.getColumnName().equalsIgnoreCase(HHSConstants.PAYMENT_VALUE))
		{
			StringBuffer loControl1 = new StringBuffer();
			loControl1.append("<label class='tablePaymentValue' style='text-align:right; float:right'>"
					+ loPaymentList.getPaymentValue() + "</label>");
			lsControl = loControl1.toString();
		}
		else if (aoCol.getColumnName().equalsIgnoreCase(HHSConstants.PAYMENT_DIS_DATE))
		{
			// made changes for observation release 3.4.0
			if (loPaymentList.getPaymentDisDate() != null)
			{
				lsControl = DateUtil.getDateMMddYYYYFormat(loPaymentList.getPaymentDisDate());
			}
			return lsControl;
		}
		else if (aoCol.getColumnName().equalsIgnoreCase(HHSConstants.PAYMENT_LAST_UPDATE_DATE))
		{
			lsControl = DateUtil.getDateMMddYYYYFormat(loPaymentList.getPaymentLastUpdateDate());
			return lsControl;
		}
		// Enhancement id 6356 release 3.4.0
		else if (aoCol.getColumnName().equalsIgnoreCase(HHSConstants.PAYMENT_CT_ID))
		{
			String lsCtId = loPaymentList.getPaymentCtId();
			if (lsCtId != null)
			{// added in release 5
				if (loPaymentList.isContractAccess() && loPaymentList.isUserAccess())
				{
					lsControl = "<label >" + "<a href=\"#\" title=\"" + loPaymentList.getPaymentContractTitle()
							+ "\" onclick=\"javascript: launchBudget('" + loPaymentList.getBudgetId() + "','"
							+ loPaymentList.getContractId() + "','" + loPaymentList.getPaymentFiscalYearId() + "','"
							+ HHSConstants.BUDGET_TYPE3 + "','" + loPaymentList.getPaymentCtId() + "');\">"
							+ loPaymentList.getPaymentCtId() + "</a></label>";
				}
				else
				{
					lsControl = "<label>" + loPaymentList.getPaymentCtId() + "</label>";
				}// added in release 5
			}
		}
		// Enhancement id 6356 release 3.4.0
		else if (aoCol.getColumnName().equalsIgnoreCase(HHSConstants.PAYMENT_VOUCHER_NUM))
		{
			String lsInvoiceId = loPaymentList.getInvoiceId();
			if (lsInvoiceId != null && loPaymentList.isContractAccess() && loPaymentList.isUserAccess())
			{
				lsControl = "<a href=\"#\" onclick=\"javascript: viewInvoice(" + "'" + lsInvoiceId + "' );\">"
						+ loPaymentList.getPaymentVoucherNumber() + "</a>";
			}
			else
			{
				lsControl = loPaymentList.getPaymentVoucherNumber();
			}
		}
		else if (aoCol.getColumnName().equalsIgnoreCase(HHSConstants.PAYMENT_CONTRACT_TITLE))
		{
			if (loPaymentList != null
					&& loPaymentList.getPaymentStatusId() != null
					&& (loPaymentList.getPaymentStatusId().equalsIgnoreCase(
							PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
									HHSConstants.STATUS_PAYMENT_PENDING_FMS_ACTION))
							|| loPaymentList.getPaymentStatusId().equalsIgnoreCase(
									PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
											HHSConstants.STATUS_PAYMENT_APPROVED))
							|| loPaymentList.getPaymentStatusId().equalsIgnoreCase(
									PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
											HHSConstants.STATUS_PAYMENT_DISBURSED)) || loPaymentList
							.getPaymentStatusId().equalsIgnoreCase(
									PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
											HHSConstants.STATUS_PAYMENT_REJECTED))))
			{
				lsControl = (loPaymentList.getPaymentProcurementTitle() == null && null != loPaymentList
						.getPaymentContractTitle()) ? loPaymentList.getPaymentContractTitle() : loPaymentList
						.getPaymentProcurementTitle();

				lsControl = "<a style='width: 231px;cursor:pointer' onclick=\"javascript: openPaymentDetails('"
						+ loPaymentList.getContractId() + "','" + loPaymentList.getBudgetId() + "','"
						+ loPaymentList.getInvoiceId() + "','" + loPaymentList.getPaymentId() + "','"
						+ loPaymentList.getBudgetAdvanceId() + "' ,this)\">" + HHSUtil.convertToWrappingWord(lsControl)
						+ "</a>";
			}
			else
			{
				if (null != loPaymentList)
				{
					lsControl = (loPaymentList.getPaymentProcurementTitle() == null && null != loPaymentList
							.getPaymentContractTitle()) ? HHSUtil.convertToWrappingWord(loPaymentList
							.getPaymentContractTitle()) : HHSUtil.convertToWrappingWord(loPaymentList
							.getPaymentProcurementTitle());
				}
			}
		}
		/* Start : Added in R5 */
		else if (aoCol.getColumnName().equalsIgnoreCase(HHSR5Constants.ACTION))
		{
			if (StringUtils.isBlank(loPaymentList.getInvoiceId())
					|| PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
							HHSConstants.STATUS_PAYMENT_REJECTED).equalsIgnoreCase(loPaymentList.getPaymentStatusId()))
			{
				lsControl = lsControl.concat("<select name=action" + aoSeqNo + " id='action" + aoSeqNo
						+ "' style='width: 90%' contractTitle=\"" + loPaymentList.getPaymentContractTitle()
						+ "\" contractId=\"" + loPaymentList.getContractId() + "\" invoiceId=\""
						+ loPaymentList.getInvoiceId() + "\" budgetId=\"" + loPaymentList.getBudgetId()
						+ "\"><option title='I need to...' value='I need to...'>I need to...</option>"
						+ "<option value='view contract'>View Contract</option>"
						+ "<option value='view budget'>View Budget</option>" + "</select>");
			}
			else
			{
				lsControl = lsControl.concat("<select name=action" + aoSeqNo + " id='action" + aoSeqNo
						+ "' style='width: 90%' contractTitle=\"" + loPaymentList.getPaymentContractTitle()
						+ "\" contractId=\"" + loPaymentList.getContractId() + "\" invoiceId=\""
						+ loPaymentList.getInvoiceId() + "\" budgetId=\"" + loPaymentList.getBudgetId()
						+ "\"><option title='I need to...' value='I need to...'>I need to...</option>"
						+ "<option value='view contract'>View Contract</option>"
						+ "<option value='view budget'>View Budget</option>"
						+ "<option value='view invoice'>View Invoice</option></select>");
			}
		}
		else if (aoCol.getColumnName().equalsIgnoreCase("paymentProvider"))
		{
			lsControl = "<a href =\"javascript:void(0)\" onClick=\"viewOrganizationInformation(\'"
					+ loPaymentList.getOrgId() + "\',\'"
					+ StringEscapeUtils.escapeJavaScript(loPaymentList.getPaymentProvider()) + "\')\" >"
					+ loPaymentList.getPaymentProvider() + "</a>";
		}
		/* End : Added in R5 */
		
        /*[Start] R7.2.0 QC8914 Set indicator for Access control     */
        StringBuilder loCon = new StringBuilder(lsControl); 
        if(lsControl!=null
                && loPaymentList.getUserSubRole().equalsIgnoreCase(ApplicationConstants.ROLE_OBSERVER)){
            CommonUtil.keepReadOnlyActions(loCon);
        }
        /*[End] R7.2.0 QC8914 Set indicator for Access control     */ 

		return loCon.toString();
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

		return HHSConstants.RESUME;
	}
}
