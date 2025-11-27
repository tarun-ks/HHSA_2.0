package com.nyc.hhs.portlet;

import java.io.IOException;
import java.util.Map;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;

/**
 * This controller will get the count of documents for the provider logged in
 * and will also fetch due date for CHAR500 document.
 * 
 */

public class HomeDocumentVaultView extends GenericPortlet
{
	private static final LogInfo LOG_OBJECT = new LogInfo(HomeDocumentVaultView.class);

	/**
	 * Release 5 Proposal Activity and Char 500 History--> This method handle
	 * the do view request of a portlet page. When a request is initiated from a
	 * jsp, after processing the request it render the jsp and display the
	 * result to the end use this is an out of box method from portlet frame
	 * work which we override to achieve further functionalities
	 * 
	 * @param aoRequest Render request object
	 * @param aoResponse Render response object
	 * @throws PortletException If an Portlet Exception occurs
	 * @throws IOException If an Input Output Exception occurs
	 */
	public void doView(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		try
		{
			// Updated in R5
			Map<String, Object> loFilingDetailsMap = getFilingDetails(aoRequest);
			aoRequest.setAttribute(HHSR5Constants.FILING_DETAILS_BEAN_KEY, loFilingDetailsMap);
			PortletRequestDispatcher loPrd = getPortletContext().getRequestDispatcher(
					"/portlet/homeprovider/documentvault/filingsInformationHomepages.jsp");
			loPrd.include(aoRequest, aoResponse);
			// R5 changes ends
		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Error while getting count of documents for current provider", aoAppExp);
			String lsErrorMsg = "Internal Error Occured While Processing Your Request";
			aoRequest.setAttribute(ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
					ApplicationConstants.DOCUMENT_EXCEPTION);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
		// Added in R5
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while getting count of documents for current provider", aoExp);
			String lsErrorMsg = "Internal Error Occured While Processing Your Request";
			aoRequest.setAttribute(ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
					ApplicationConstants.DOCUMENT_EXCEPTION);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
		// R5 Changes Ends
	}

	/**
	 * This method will set the due date of the char500 id it is less then 60
	 * days. This method is updated in Release 5 for Proposal History.
	 * 
	 * @param aoRequest render request object
	 * @throws ApplicationException when any application exception Occurred
	 */
	private Map<String, Object> getFilingDetails(RenderRequest aoRequest) throws ApplicationException
	{
		// Added in R5
		Map<String, Object> loFilingDetailsMap;
		try
		{
			PortletSession loPortletSession = aoRequest.getPortletSession();
			String lsOrgId = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
					PortletSession.APPLICATION_SCOPE);
			P8UserSession loUserSession = (P8UserSession) loPortletSession.getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			Channel loChannel = new Channel();
			loChannel.setData(HHSR5Constants.AS_ORG_ID, lsOrgId);
			TransactionManager.executeTransaction(loChannel, HHSR5Constants.GET_FILINGINFORMATION_HOMEPAGE,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			loFilingDetailsMap = (Map<String, Object>) loChannel.getData(HHSR5Constants.LO_FILING_MAP);
		}
		catch (ApplicationException aoAppEx)
		{
			throw aoAppEx;
		}
		catch (Exception aoExp)
		{
			throw new ApplicationException("Error occured while fetching Filing Details", aoExp);
		}
		return loFilingDetailsMap;
	}
	// R5 update ends

}
