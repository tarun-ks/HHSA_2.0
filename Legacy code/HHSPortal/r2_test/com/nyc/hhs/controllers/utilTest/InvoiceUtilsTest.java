package com.nyc.hhs.controllers.utilTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.controllers.util.InvoiceUtils;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.junit.util.JUnitUtil;
import com.nyc.hhs.model.CBGridBean;

public class InvoiceUtilsTest
{
	private static SqlSession moSession = null; // SQL Session

	/**
	 * SQL session created ONCE before the class
	 * 
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		Boolean lbThrown = false;
		try
		{
			JUnitUtil.getTransactionManager();
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	/**
	 * Close the SQL session created at the beginning
	 * 
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		Boolean lbThrown = false;
		try
		{
			moSession.close();
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	/**
	 * Add Assignee successfully by Agency
	 * 
	 */
	@Test
	public void testAddAssigneeForBudget1() throws ApplicationException
	{

		String lsMsg = null;
		Boolean lbThrown = false;
		Channel loChannel = new Channel();
		String lsVendorId = "10";
		String lsBudgetId = "555";
		try
		{
			CBGridBean loCBGridBean = new CBGridBean();
			loCBGridBean.setModifyByProvider("");
			loCBGridBean.setModifyByAgency("city_142");
			// Set vendorId, BudgetId and CBGridBean in Channel
			loChannel.setData(HHSConstants.S431_CHANNEL_VENDOR, lsVendorId);
			loChannel.setData(HHSConstants.BUDGET_ID_KEY, lsBudgetId);
			loChannel.setData(HHSConstants.LO_CB_GRID_BEAN, loCBGridBean);
			lsMsg = InvoiceUtils.addAssignee(loChannel);
			assertTrue(lsMsg != null);
			assertTrue(lsMsg.equals(HHSConstants.EMPTY_STRING));
		}
		catch (ApplicationException e)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	/**
	 * Add Assignee successfully by Provider
	 * 
	 */
	@Test
	public void testAddAssigneeForBudget12() throws ApplicationException
	{
		String lsMsg = null;
		Boolean lbThrown = false;
		Channel loChannel = new Channel();
		String lsVendorId = "10";
		String lsBudgetId = "555";
		try
		{
			CBGridBean loCBGridBean = new CBGridBean();
			loCBGridBean.setModifyByProvider("803");
			loCBGridBean.setModifyByAgency("");
			// Set vendorId, BudgetId and CBGridBean in Channel
			loChannel.setData(HHSConstants.S431_CHANNEL_VENDOR, lsVendorId);
			loChannel.setData(HHSConstants.BUDGET_ID_KEY, lsBudgetId);
			loChannel.setData(HHSConstants.LO_CB_GRID_BEAN, loCBGridBean);
			lsMsg = InvoiceUtils.addAssignee(loChannel);
			assertTrue(lsMsg != null);
			assertTrue(lsMsg.equals(HHSConstants.EMPTY_STRING));
		}
		catch (ApplicationException e)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	/**
	 * Add Assignee : test case for Duplicate Assignee It will show a message
	 * when we try to add duplicate assignee
	 * 
	 */
	@Test
	public void testAddAssigneeForBudget2() throws ApplicationException
	{
		Boolean lbThrown = false;
		Channel loChannel = new Channel();
		String lsVendorId = "10";
		String lsBudgetId = "555";
		try
		{
			CBGridBean loCBGridBean = new CBGridBean();
			loCBGridBean.setModifyByProvider("803");
			loCBGridBean.setModifyByAgency("");
			// Set vendorId, BudgetId and CBGridBean in Channel
			loChannel.setData(HHSConstants.S431_CHANNEL_VENDOR, lsVendorId);
			loChannel.setData(HHSConstants.BUDGET_ID_KEY, lsBudgetId);
			loChannel.setData(HHSConstants.LO_CB_GRID_BEAN, loCBGridBean);
			InvoiceUtils.addAssignee(loChannel);
		}
		catch (ApplicationException aoAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	/**
	 * Add Assignee : Negative scenario - An Exception is thrown if any of the
	 * input is invalid
	 * 
	 */
	@Test(expected = ApplicationException.class)
	public void testAddAssigneeForBudget3() throws ApplicationException
	{
		Boolean lbThrown = false;
		Channel loChannel = new Channel();
		String lsVendorId = "10";
		String lsBudgetId = null;
		try
		{
			CBGridBean loCBGridBean = new CBGridBean();
			loCBGridBean.setModifyByProvider("803");
			loCBGridBean.setModifyByAgency("");
			// Set vendorId, BudgetId and CBGridBean in Channel
			loChannel.setData(HHSConstants.S431_CHANNEL_VENDOR, lsVendorId);
			loChannel.setData(HHSConstants.BUDGET_ID_KEY, lsBudgetId);
			loChannel.setData(HHSConstants.LO_CB_GRID_BEAN, loCBGridBean);
			InvoiceUtils.addAssignee(loChannel);
		}
		catch (ApplicationException aoAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	/**
	 * Add Assignee : Negative scenario - An Exception is thrown if any of the
	 * input is invalid
	 * 
	 */
	@Test
	public void testAddAssigneeForBudgetSessionNull() throws ApplicationException
	{
		InvoiceUtils.addAssignee(null);
	}
	
	/**
	 * Validate InvoiceStatus : Negative scenario - An Exception is thrown if
	 * any of the input is invalid
	 * 
	 */
	@SuppressWarnings("unused")
	@Test
	public void testValidateInvoiceStatus1() throws ApplicationException
	{
		Map<String, String> loMap = new HashMap<String, String>();
		String lsInvoiceId = null;
		Boolean lbThrown = false;
		try
		{
			loMap = InvoiceUtils.validateInvoiceStatus(lsInvoiceId);
		}
		catch (ApplicationException aoAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	/**
	 * Validate InvoiceStatus : Validate Status
	 * 
	 */
	@Test
	public void testValidateInvoiceStatus2() throws ApplicationException
	{
		Map<String, String> loMap = new HashMap<String, String>();
		String lsInvoiceId = "55";
		Boolean lbThrown = false;
		try
		{
			loMap = InvoiceUtils.validateInvoiceStatus(lsInvoiceId);
			assertTrue(loMap != null);
		}
		catch (ApplicationException aoAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	/**
	 * Validate InvoiceStatus : Validate Status
	 * 
	 */
	@Test
	public void testValidateInvoiceStatus3() throws ApplicationException
	{
		Map<String, String> loMap = new HashMap<String, String>();
		String lsInvoiceId = "59";
		Boolean lbThrown = false;
		try
		{
			loMap = InvoiceUtils.validateInvoiceStatus(lsInvoiceId);
			assertTrue(loMap != null);
		}
		catch (ApplicationException aoAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	/**
	 * Validate InvoiceStatus : Validate Status
	 * 
	 */
	@Test
	public void testValidateInvoiceStatus4() throws ApplicationException
	{
		Map<String, String> loMap = new HashMap<String, String>();
		String lsInvoiceId = "61";
		Boolean lbThrown = false;
		try
		{
			loMap = InvoiceUtils.validateInvoiceStatus(lsInvoiceId);
			assertTrue(loMap != null);
		}
		catch (ApplicationException aoAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	/**
	 * Checks and set the User Type if user_org_type is provider_org
	 */
	@Test
	public void testSetUserForUserTypeIsProvider() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		String loUserId = "";
		String loUserOrgType = "provider_org";
		InvoiceUtils.setUserForUserType(loCBGridBean, loUserId, loUserOrgType);
	}

	/**
	 * Checks and set the User Type if user_org_type is not provider_org
	 */
	@Test
	public void testSetUserForUserTypeNotProvider() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		String loUserId = "";
		String loUserOrgType = "";
		InvoiceUtils.setUserForUserType(loCBGridBean, loUserId, loUserOrgType);
	}

	/**
	 * Tests the return JSP path on the basis of ActionReqParam
	 */
	@Test
	public void testSetJSPNameForInvoiceReviewTaskSuccess() throws ApplicationException
	{
		String loJspPath = "/invoice.jsp";
		String loActionReqParam = "invoiceReviewTask";
		loJspPath = InvoiceUtils.setJSPNameForInvoiceReviewTask(loJspPath, loActionReqParam);
		assertEquals(HHSConstants.JSP_PATH_CONTRACT_INVOICE_REVIEW_TASK, loJspPath);
	}

	/**
	 * Tests the return JSP path if ActionReqParam = "invoiceReviewTask"
	 */
	@Test
	public void testSetJSPNameForInvoiceReviewTaskJspPathNull() throws ApplicationException
	{
		String loActionReqParam = "invoiceReviewTask";
		String loJspPath = InvoiceUtils.setJSPNameForInvoiceReviewTask(null, loActionReqParam);
		assertEquals(HHSConstants.JSP_PATH_CONTRACT_INVOICE_REVIEW_TASK, loJspPath);
	}

	/**
	 * Tests the return JSP path if ActionReqParam = null
	 */
	@Test
	public void testSetJSPNameForInvoiceReviewTaskActionNull() throws ApplicationException
	{
		String loJspPath = "/invoice.jsp";
		loJspPath = InvoiceUtils.setJSPNameForInvoiceReviewTask(loJspPath, null);
		assertEquals("/invoice.jsp", loJspPath);
	}

	/**
	 * Will throw ApplicationException if Review level not set for the workflow.
	 * Positive case
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Test(expected = ApplicationException.class)
	public void testSubmitInvoiceConfirmationOverlayUtil() throws ApplicationException
	{
		Channel loChannel = new Channel();
		HashMap loHmRequiredProps = new HashMap();
		HashMap loHmInvoiceRequiredProps = new HashMap();

		loHmInvoiceRequiredProps.put(HHSConstants.INVOICE_ID, "55");
		loHmInvoiceRequiredProps.put(HHSConstants.PUBLIC_CMNT_ID, "Saved");
		loHmInvoiceRequiredProps.put(HHSConstants.USER_ID, "803");

		loHmRequiredProps.put(HHSConstants.CONTRACT_ID_WORKFLOW, "111777");
		loHmRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW, "555");
		loHmRequiredProps.put(HHSConstants.INVOICE_ID, "55");
		loHmRequiredProps.put(HHSConstants.SUBMITTED_BY, "803");
		loHmRequiredProps.put(HHSConstants.AUDIT_PROVIDER_INITIATED_TASK, true);
		loHmRequiredProps.put(HHSConstants.WORKFLOW_NAME, HHSConstants.WF_INVOICE_REVIEW);

		loChannel.setData(HHSConstants.USER_ID, "803");
		loChannel.setData(HHSConstants.COMMENTS, HHSConstants.EMPTY_STRING);
		loChannel.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
		loChannel.setData(HHSConstants.LB_AUTH_STATUS_FLAG, true);
		loChannel.setData(HHSConstants.AO_HMWF_REQUIRED_PROPS, loHmRequiredProps);
		loChannel.setData(HHSConstants.AO_HM_INVOICE_REQUIRED_PROPS, loHmInvoiceRequiredProps);

		InvoiceUtils.submitInvoiceConfirmationOverlayUtil(loChannel);
	}

	/**
	 * Will throw ApplicationException if Review level not set for the workflow.
	 * IsSubmitted is true, workflow already submitted
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Test
	public void testSubmitInvoiceConfirmationOverlayUtilSubmittedTrue() throws ApplicationException
	{
		Channel loChannel = new Channel();
		HashMap loHmRequiredProps = new HashMap();
		HashMap loHmInvoiceRequiredProps = new HashMap();

		loHmInvoiceRequiredProps.put(HHSConstants.INVOICE_ID, "55");
		loHmInvoiceRequiredProps.put(HHSConstants.PUBLIC_CMNT_ID, "Saved");
		loHmInvoiceRequiredProps.put(HHSConstants.USER_ID, "803");

		loHmRequiredProps.put(HHSConstants.CONTRACT_ID_WORKFLOW, "111777");
		loHmRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW, "555");
		loHmRequiredProps.put(HHSConstants.INVOICE_ID, "55");
		loHmRequiredProps.put(HHSConstants.SUBMITTED_BY, "803");
		loHmRequiredProps.put(HHSConstants.AUDIT_PROVIDER_INITIATED_TASK, true);
		loHmRequiredProps.put(HHSConstants.WORKFLOW_NAME, HHSConstants.WF_INVOICE_REVIEW);

		loChannel.setData(HHSConstants.USER_ID, "803");
		loChannel.setData(HHSConstants.COMMENTS, HHSConstants.EMPTY_STRING);
		loChannel.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
		loChannel.setData(HHSConstants.LB_AUTH_STATUS_FLAG, true);
		loChannel.setData(HHSConstants.AO_HMWF_REQUIRED_PROPS, loHmRequiredProps);
		loChannel.setData(HHSConstants.AO_HM_INVOICE_REQUIRED_PROPS, loHmInvoiceRequiredProps);

		InvoiceUtils.submitInvoiceConfirmationOverlayUtil(loChannel);
	}

	/**
	 * Tests the invoice submission action if channel is null
	 * @throws ApplicationException
	 */
	@Test
	public void testSubmitInvoiceConfirmationOverlayUtilChannelNull() throws ApplicationException
	{
		InvoiceUtils.submitInvoiceConfirmationOverlayUtil(null);
	}

	/**
	 * Tests the invoice submission action if channel is null and workflow already Submitted
	 * @throws ApplicationException
	 */
	@Test
	public void testSubmitInvoiceConfirmationOverlayUtilSubmittedNotNull() throws ApplicationException
	{
		InvoiceUtils.submitInvoiceConfirmationOverlayUtil(null);
	}

	/**
	 * Will throw ApplicationException if Review level not set for the workflow.
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Test(expected = ApplicationException.class)
	public void testSubmitInvoiceConfirmationOverlayUtilInvoiceMapNull() throws ApplicationException
	{
		Channel loChannel = new Channel();
		HashMap loHmRequiredProps = new HashMap();
		HashMap loHmInvoiceRequiredProps = new HashMap();

		loHmInvoiceRequiredProps.put(HHSConstants.INVOICE_ID, "");
		loHmInvoiceRequiredProps.put(HHSConstants.PUBLIC_CMNT_ID, "");
		loHmInvoiceRequiredProps.put(HHSConstants.USER_ID, "");

		loHmRequiredProps.put(HHSConstants.CONTRACT_ID_WORKFLOW, "111777");
		loHmRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW, "555");
		loHmRequiredProps.put(HHSConstants.INVOICE_ID, "55");
		loHmRequiredProps.put(HHSConstants.SUBMITTED_BY, "803");
		loHmRequiredProps.put(HHSConstants.AUDIT_PROVIDER_INITIATED_TASK, true);
		loHmRequiredProps.put(HHSConstants.WORKFLOW_NAME, HHSConstants.WF_INVOICE_REVIEW);

		loChannel.setData(HHSConstants.USER_ID, "803");
		loChannel.setData(HHSConstants.COMMENTS, HHSConstants.EMPTY_STRING);
		loChannel.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
		loChannel.setData(HHSConstants.LB_AUTH_STATUS_FLAG, true);
		loChannel.setData(HHSConstants.AO_HMWF_REQUIRED_PROPS, loHmRequiredProps);
		loChannel.setData(HHSConstants.AO_HM_INVOICE_REQUIRED_PROPS, loHmInvoiceRequiredProps);

		InvoiceUtils.submitInvoiceConfirmationOverlayUtil(loChannel);
	}

	/**
	 * Tests the invoice submission if :
	 * Required properties are set empty in the loHmInvoiceRequiredProps Map.
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Test
	public void testSubmitInvoiceConfirmationOverlayUtilIsSubmittedTrue() throws ApplicationException
	{
		Channel loChannel = new Channel();
		HashMap loHmRequiredProps = new HashMap();
		HashMap loHmInvoiceRequiredProps = new HashMap();

		loHmInvoiceRequiredProps.put(HHSConstants.INVOICE_ID, "");
		loHmInvoiceRequiredProps.put(HHSConstants.PUBLIC_CMNT_ID, "");
		loHmInvoiceRequiredProps.put(HHSConstants.USER_ID, "");

		loHmRequiredProps.put(HHSConstants.CONTRACT_ID_WORKFLOW, "111777");
		loHmRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW, "555");
		loHmRequiredProps.put(HHSConstants.INVOICE_ID, "55");
		loHmRequiredProps.put(HHSConstants.SUBMITTED_BY, "803");
		loHmRequiredProps.put(HHSConstants.AUDIT_PROVIDER_INITIATED_TASK, true);
		loHmRequiredProps.put(HHSConstants.WORKFLOW_NAME, HHSConstants.WF_INVOICE_REVIEW);

		loChannel.setData(HHSConstants.USER_ID, "803");
		loChannel.setData(HHSConstants.COMMENTS, HHSConstants.EMPTY_STRING);
		loChannel.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
		loChannel.setData(HHSConstants.LB_AUTH_STATUS_FLAG, true);
		loChannel.setData(HHSConstants.AO_HMWF_REQUIRED_PROPS, loHmRequiredProps);
		loChannel.setData(HHSConstants.AO_HM_INVOICE_REQUIRED_PROPS, loHmInvoiceRequiredProps);

		InvoiceUtils.submitInvoiceConfirmationOverlayUtil(loChannel);
	}

	/**
	 * Tests the invoice submission if :
	 * Required properties are set null in the loHmInvoiceRequiredProps Map.
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Test(expected = ApplicationException.class)
	public void testSubmitInvoiceConfirmationOverlayUtilInvoiceMapKeyNull() throws ApplicationException
	{
		Channel loChannel = new Channel();
		HashMap loHmRequiredProps = new HashMap();
		HashMap loHmInvoiceRequiredProps = new HashMap();

		loHmInvoiceRequiredProps.put(HHSConstants.INVOICE_ID, null);
		loHmInvoiceRequiredProps.put(HHSConstants.PUBLIC_CMNT_ID, null);
		loHmInvoiceRequiredProps.put(HHSConstants.USER_ID, null);

		loHmRequiredProps.put(HHSConstants.CONTRACT_ID_WORKFLOW, "111777");
		loHmRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW, "555");
		loHmRequiredProps.put(HHSConstants.INVOICE_ID, "55");
		loHmRequiredProps.put(HHSConstants.SUBMITTED_BY, "803");
		loHmRequiredProps.put(HHSConstants.AUDIT_PROVIDER_INITIATED_TASK, true);
		loHmRequiredProps.put(HHSConstants.WORKFLOW_NAME, HHSConstants.WF_INVOICE_REVIEW);

		loChannel.setData(HHSConstants.USER_ID, "803");
		loChannel.setData(HHSConstants.COMMENTS, HHSConstants.EMPTY_STRING);
		loChannel.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
		loChannel.setData(HHSConstants.LB_AUTH_STATUS_FLAG, true);
		loChannel.setData(HHSConstants.AO_HMWF_REQUIRED_PROPS, loHmRequiredProps);
		loChannel.setData(HHSConstants.AO_HM_INVOICE_REQUIRED_PROPS, loHmInvoiceRequiredProps);

		InvoiceUtils.submitInvoiceConfirmationOverlayUtil(loChannel);
	}
}
