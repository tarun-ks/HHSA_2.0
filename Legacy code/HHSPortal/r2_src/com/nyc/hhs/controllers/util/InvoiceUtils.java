package com.nyc.hhs.controllers.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * <p>
 * This util class will be used to for Controllers. All decision making or
 * control flow is executed here.
 * </p>
 * 
 */
public class InvoiceUtils
{
	
	/**
	 * LogInfo Object
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(InvoiceUtils.class);
	
	/**
	 * This method adds assignee if validation is successful i.e if Assignee
	 * does not exist <li>The transaction used : validateAssignee</li> <li>The
	 * transaction used : addAssigneeForBudget</li>
	 * @param aoChannel Channel
	 * @return String Message
	 * @throws ApplicationException Application Exception
	 */
	public static String addAssignee(Channel aoChannel) throws ApplicationException
	{
		String lsMessage = HHSConstants.EMPTY_STRING;
		boolean lbValid = Boolean.FALSE;
		// Validate Assignee
		try
		{
			HHSTransactionManager.executeTransaction(aoChannel, HHSConstants.VALIDATE_ASSIGNEE);
			
			lbValid = (Boolean) aoChannel.getData(HHSConstants.S431_CHANNEL_KEY_LB_VALID);
			if (lbValid)
			{
				// Validation successful: Assignee does not exit, Add new
				HHSTransactionManager.executeTransaction(aoChannel, HHSConstants.INSERT_ASSIGNEE_FOR_BUDGET);
			}
			else
			{
				lsMessage = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.MSG_KEY_DUPLICATE_ASSIGNEE_NOT_ADDED);
			}
		}
		// handling Application Exception if Assignee cannot be added
		catch (ApplicationException aoAppEx)
		{
			lsMessage = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("ApplicationException Occured in addAssignee while adding Assignee to the database",
					aoAppEx);
			throw aoAppEx;
			
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			lsMessage = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("Exception Occured in addAssignee while adding Assignee to the database", aoEx);
		}
		return lsMessage;
		
	}
	
	/**
	 * This is Utility method for submitInvoiceConfirmationOverlay in
	 * InvoiceController launch WF305 – Invoice Review. <li>The transaction used
	 * : launchWFInvoice</li> <li>Two service method are added for agency
	 * interface module.It update remaining and YTD invoiced amount in line
	 * items table on invoice submission.</li>
	 * @param aoChannel Channel for Transaction
	 * @throws ApplicationException Application Exception
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public static void submitInvoiceConfirmationOverlayUtil(Channel aoChannel) throws ApplicationException
	{
		String lsInvoiceStatus = null;
		
		try
		{
			if (aoChannel != null)
			{
				Map loHmInvoiceRequiredProps = (Map) aoChannel.getData(HHSConstants.AO_HM_INVOICE_REQUIRED_PROPS);
				String lsInvoiceId = (String) loHmInvoiceRequiredProps.get(HHSConstants.INVOICE_ID);
				// Invoice not-submitted earlier
				
				// Get Invoice Status
				lsInvoiceStatus = fetchCurrentInvoiceStatus(lsInvoiceId);
				// Set Invoice-Status String in Map
				if (lsInvoiceStatus != null && lsInvoiceStatus.equals(HHSConstants.RETURNED_FOR_REVISION))
				{
					loHmInvoiceRequiredProps.put(HHSConstants.INVOICE_STATUS_ID, HHSConstants.TASK_RFR);
				}
				else
				{
					loHmInvoiceRequiredProps
							.put(HHSConstants.INVOICE_STATUS_ID, HHSConstants.STATUS_PENDING_SUBMISSION);
				}
				// Set Audit data in Channel for transaction
				setAuditDataInChannel(aoChannel, lsInvoiceId, loHmInvoiceRequiredProps.get(HHSConstants.USER_ID)
						.toString(), loHmInvoiceRequiredProps.get(HHSConstants.PUBLIC_CMNT_ID).toString(),
						loHmInvoiceRequiredProps.get(HHSConstants.INVOICE_STATUS_ID).toString());
				// Execute Transaction to launch Invoice Work-flow
				if ((lsInvoiceStatus != null)
						&& (lsInvoiceStatus.equals(HHSConstants.RETURNED_FOR_REVISION) || lsInvoiceStatus
								.equals(HHSConstants.PENDING_SUBMISSION)))
				{
					// Start R5 : set EntityId and EntityName for AutoSave
					CommonUtil.setChannelForAutoSaveData(aoChannel, lsInvoiceId, HHSConstants.INVOICE);
					// End R5 : set EntityId and EntityName for AutoSave
					HHSTransactionManager.executeTransaction(aoChannel, HHSConstants.LAUNCH_WF_CONTRACT_INVOICE);
				}
			}
		}
		// Application Exception thrown while transaction in service class is
		// handled here
		// using the Transaction Framework.
		catch (ApplicationException aoAppExe)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			String lsLevelErrorMessage = null;
			ApplicationException loAppEx = (ApplicationException) aoAppExe.getRootCause();
			if (null != loAppEx)
			{
				lsLevelErrorMessage = (String) loAppEx.getContextData().get(HHSConstants.LEVEL_ERROR_MESSAGE);
			}
			if (null == lsLevelErrorMessage)
			{
				lsLevelErrorMessage = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			}
			LOG_OBJECT.Error("Exception occured in submitConfirmationOverlay", aoAppExe);
			throw new ApplicationException(lsLevelErrorMessage, aoAppExe);
		}
	}
	
	/**
	 * This method set audit data in channel object for
	 * submitInvoiceConfirmationOverlay <li>This method was updated in R4</li>
	 * @param aoChannel Channel object
	 * @param asInvoiceId InvoiceId
	 * @param asUserId User Id
	 * @param asComment comments
	 * @param asStatus Budget Status
	 * @throws ApplicationException Application Exception
	 */
	private static void setAuditDataInChannel(Channel aoChannel, String asInvoiceId, String asUserId, String asComment,
			String asStatus) throws ApplicationException
	{
		try
		{
			List<HhsAuditBean> loAuditList = new ArrayList<HhsAuditBean>();
			HhsAuditBean loHhsAuditBean = new HhsAuditBean();
			String lsPrevStatus = asStatus;
			String lsStatusChange = HHSConstants.STATUS_CHANGED_FROM;
			
			StringBuilder loBStatusChange = new StringBuilder();
			loBStatusChange.append(lsStatusChange);
			loBStatusChange.append(HHSConstants.SPACE);
			loBStatusChange.append(HHSConstants.STR);
			loBStatusChange.append(lsPrevStatus);
			loBStatusChange.append(HHSConstants.STR);
			loBStatusChange.append(HHSConstants._TO_);
			loBStatusChange.append(HHSConstants.STR);
			loBStatusChange.append(HHSConstants.STATUS_PENDING_APPROVAL);
			loBStatusChange.append(HHSConstants.STR);
			if (null != asComment && !asComment.isEmpty())
			{
				loAuditList.add(HHSUtil.addAuditDataToChannel(P8Constants.PROPERTY_PE_TH_PROVIDER_COMMENT,
						P8Constants.PROPERTY_PE_TH_PROVIDER_COMMENT, asComment, HHSConstants.AUDIT_INVOICES,
						asInvoiceId, asUserId, HHSConstants.PROVIDER_AUDIT));
			}
			
			loAuditList.add(HHSUtil.addAuditDataToChannel(HHSConstants.STATUS_CHANGE, HHSConstants.STATUS_CHANGE,
					loBStatusChange.toString(), HHSConstants.AUDIT_INVOICES, asInvoiceId, asUserId,
					HHSConstants.PROVIDER_AUDIT));
			loHhsAuditBean.setEntityId(asInvoiceId);
			loHhsAuditBean.setEntityType(HHSConstants.AUDIT_INVOICES);
			aoChannel.setData(HHSConstants.LO_AUDIT_LIST, loAuditList);
			aoChannel.setData(HHSConstants.AUDIT_BEAN, loHhsAuditBean);
			aoChannel.setData(HHSConstants.INVOICE_ID, asInvoiceId);
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While setting audit data in channel object", aoEx);
			LOG_OBJECT.Error("Error Occured While setting audit data in channel object", loAppEx);
			throw loAppEx;
		}
	}
	
	/**
	 * This method is used to fetch the current Invoice Status <li>The
	 * transaction used : fetchCurrInvoiceStatus</li>
	 * @param aoInvoiceId InvoiceId
	 * @return lsInvoiceStatus String : Invoice Status
	 * @throws ApplicationException Application Exception
	 */
	private static String fetchCurrentInvoiceStatus(String aoInvoiceId) throws ApplicationException
	{
		String lsInvoiceStatus = null;
		try
		{
			Channel loChannel = new Channel();
			loChannel.setData(HHSConstants.INVOICE_ID, aoInvoiceId);
			
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_CURRENT_INVOICE_STATUS);
			
			lsInvoiceStatus = (String) loChannel.getData(HHSConstants.INVOICE_STATUS);
		}
		// handling Application Exception if error while fetching the current
		// Invoice Status
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("ApplicationException Occured while fetching the current Invoice Status", aoAppEx);
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while fetching the current Invoice Status", aoEx);
			ApplicationException loAppEx = new ApplicationException(
					"Exception Occured while fetching the current Invoice Status", aoEx);
			throw loAppEx;
		}
		return lsInvoiceStatus;
	}
	
	/**
	 * <ul>
	 * This method validates Invoice Status and set Error message accordingly
	 * <li>For Canceled,Suspended and closed Invoices, it sets Error Message</li>
	 * <li>FOr any other status, it sets Success Message</li>
	 * </ul>
	 * @param aoInvoiceId Invoice Id
	 * @return Map
	 * @throws ApplicationException Application Exception
	 */
	public static Map<String, String> validateInvoiceStatus(String aoInvoiceId) throws ApplicationException
	{
		String lsErrorMsg = HHSConstants.EMPTY_STRING;
		Map<String, String> loMap = null;
		try
		{
			loMap = new HashMap<String, String>();
			
			String lsInvoiceStatus = fetchCurrentInvoiceStatus(aoInvoiceId);
			
			if (lsInvoiceStatus.equalsIgnoreCase(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_INVOICE_CANCELLED)))
			{
				lsErrorMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.INV_STATUS_CANCEL);
				loMap.put(HHSConstants.ERROR_MESSAGE, lsErrorMsg);
			}
			else if (lsInvoiceStatus.equalsIgnoreCase(PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_INVOICE_SUSPENDED)))
			{
				lsErrorMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.INV_STATUS_SUSPEND);
				loMap.put(HHSConstants.ERROR_MESSAGE, lsErrorMsg);
			}
			else if (lsInvoiceStatus.equalsIgnoreCase(PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_INVOICE_CLOSED)))
			{
				lsErrorMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.INV_STATUS_CLOSED);
				loMap.put(HHSConstants.ERROR_MESSAGE, lsErrorMsg);
			}
			else
			{
				// successMessage
				lsErrorMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.CB_SAVED);
				loMap.put(HHSConstants.SUCCESS_MESSAGE, lsErrorMsg);
			}
		}
		// handling Application Exception if occur while validating Invoice
		// Status
		catch (ApplicationException aoAppEx)
		{
			
			LOG_OBJECT.Error("ApplicationException Occured while validating Invoice Status", aoAppEx);
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while validating Invoice Status", aoEx);
		}
		return loMap;
	}
	
	/**
	 * <ul>
	 * This method sets UserId in CBGridBean according to UserType.
	 * <li>For Provider user, set userId in CBGridBean.modifyByProviderId</li>
	 * <li>For Agency user, set UserId in CBGridBean.modifyByAgencyId</li>
	 * </ul>
	 * 
	 * @param aoCBGridBean CB Grid Bean
	 * @param aoUserId :USerId
	 * @param aoUserOrgType : USerType
	 * @throws ApplicationException Application Exception
	 */
	public static void setUserForUserType(CBGridBean aoCBGridBean, String aoUserId, String aoUserOrgType)
			throws ApplicationException
	{
		try
		{
			if (ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(aoUserOrgType))
			{
				aoCBGridBean.setModifyByProvider(aoUserId);
			}
			else
			{
				aoCBGridBean.setModifyByAgency(aoUserId);
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured while setting UserId in CBGridBean according to UserType", aoEx);
			LOG_OBJECT.Error("Error Occured while setting UserId in CBGridBean according to UserType", loAppEx);
			throw loAppEx;
		}
		
	}
	
	/**
	 * This method sets JSP Name for InvoiceReview Task and Print Invoice
	 * Modified for Release 3.4.0, #5681
	 * @param aoJspPath : JSP Path
	 * @param aoActionReqParam : Action to be performed
	 * @return String JSP Path
	 * @throws ApplicationException Application Exception
	 */
	public static String setJSPNameForInvoiceReviewTask(String aoJspPath, String aoActionReqParam)
			throws ApplicationException
	{
		try
		{
			if (null != aoActionReqParam && aoActionReqParam.equalsIgnoreCase(HHSConstants.TASK_INVOICE_REVIEW))
			
			{
				aoJspPath = HHSConstants.JSP_PATH_CONTRACT_INVOICE_REVIEW_TASK;
			}
			// Added check for Release 3.4.0, #5681 - Starts
			else if (null != aoActionReqParam && aoActionReqParam.equalsIgnoreCase(HHSConstants.PRINTER_VIEW_INVOICE))
			{
				aoJspPath = HHSConstants.JSP_INVOICE_PRINT;
			}
			// Added check for Release 3.4.0, #5681 - Ends
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured while setting JSP Name for InvoiceReview Task", aoEx);
			LOG_OBJECT.Error("Error Occured while setting JSP Name for InvoiceReview Task", loAppEx);
			throw loAppEx;
		}
		return aoJspPath;
	}
}
