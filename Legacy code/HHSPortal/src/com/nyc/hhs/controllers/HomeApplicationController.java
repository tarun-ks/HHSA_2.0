package com.nyc.hhs.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.mvc.AbstractController;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.ApplicationSummary;
import com.nyc.hhs.model.UserThreadLocal;

/**
 * This controller is used to manipulate and handle the home portlet for the
 * provider to show the details required on the home portlet like display
 * provider and business status and number of services count
 * 
 */

public class HomeApplicationController extends AbstractController
{

	private static final LogInfo LOG_OBJECT = new LogInfo(HomeApplicationController.class);

	/**
	 * This method is handle all the rendering activities for the service
	 * application, also method sets the values in the RenderRequest reference,
	 * so that same values can be displayed on the required jsp.
	 * @param aoRequest - RenderRequest
	 * @param aoResponse - RenderResponse
	 * @return loModelAndView
	 * @throws ApplicationException
	 */
	protected ModelAndView handleRenderRequestInternal(RenderRequest aoRequest, RenderResponse aoResponse)
			throws ApplicationException
	{
		long loStartTime = System.currentTimeMillis();
		PortletSession loPortletSessionThread = aoRequest.getPortletSession();
		String lsUserIdThreadLocal = (String) loPortletSessionThread.getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
		UserThreadLocal.setUser(lsUserIdThreadLocal);
		String lsFormPath = "homeapplicationinitial";
		final String lsOrgId = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);
		// map is used to contain the business object as a key and their
		// services as a child in a list
		Map<ApplicationSummary, List<ApplicationSummary>> loApplicationSummaryMap = null;
		Channel loChannelobj = new Channel();
		loChannelobj.setData(ApplicationConstants.ORG_ID, lsOrgId);
		try
		{
			// execute transaction to check the provider status and expiration
			// date that will return the object
			TransactionManager.executeTransaction(loChannelobj, ApplicationConstants.CHECK_APP);
			// get the data after executing the transaction
			ApplicationSummary loApplicationSummary = (ApplicationSummary) loChannelobj.getData("loApplicationBean");
			// if the application exits
			if (loApplicationSummary != null)
			{
				// create new channel to get the business application and
				// service application data
				Channel loChannel = new Channel();
				loChannel.setData(ApplicationConstants.KEY_SESSION_ORG_ID, lsOrgId);
				// flag passed to distinguish whether request is coming from
				// home page or service summary page
				loChannel.setData(ApplicationConstants.NAVIGATION_TYPE, ApplicationConstants.FROM_HOME_PAGE);
				// execute transaction to get the date for latest business and
				// service application for logged in provider
				TransactionManager.executeTransaction(loChannel, ApplicationConstants.RETRIEVE_APPLICATION_SUMMARY);
				// get the data after executing the transaction
				loApplicationSummaryMap = (Map<ApplicationSummary, List<ApplicationSummary>>) loChannel
						.getData("loApplicationSummaryMap");
				// counter for approved services
				Integer loApprovedServicesCounter = 0;
				// counter for services except de activated
				Integer loNumberOfServicesCounter = 0;
				// Application status
				String lsBussAppStatus = null;
				// manipulation to show the approved services count and total
				// services count without de activated status
				if (loApplicationSummaryMap != null && !loApplicationSummaryMap.isEmpty())
				{
					Entry<ApplicationSummary, List<ApplicationSummary>> loTmpObj = loApplicationSummaryMap.entrySet()
							.iterator().next();
					// channel object
					loChannel = new Channel();
					loChannel.setData("businessAppId", loTmpObj.getKey().getMsBusinessAppId());
					loChannel.setData("lsOrgId", lsOrgId);
					// check the super sedding status to display the updated
					// application status
					TransactionManager.executeTransaction(loChannel, "getBussAppUpdatedStatus");
					Map<String, Object> loUpdateStatusObj = (Map<String, Object>) loChannel
							.getData("bussAppUpdatedStatus");
					if (loUpdateStatusObj != null && !loUpdateStatusObj.isEmpty())
					{
						// super sedding status
						lsBussAppStatus = (String) loUpdateStatusObj.get("STATUS");
					}
					else
					{
						// transaction table
						lsBussAppStatus = loTmpObj.getKey().getMsAppStatus();
					}
					if (loTmpObj.getValue() != null && !loTmpObj.getValue().isEmpty())
					{
						// count the services and approved services
						for (ApplicationSummary loAppSummaryObj : loTmpObj.getValue())
						{
							String lsDeactivatedStatus = loAppSummaryObj.getMsAppStatus();
							if (lsDeactivatedStatus != null
									&& lsDeactivatedStatus.equalsIgnoreCase(ApplicationConstants.STATUS_IN_REVIEW))
							{
								++loNumberOfServicesCounter;
							}
							if (lsDeactivatedStatus != null
									&& lsDeactivatedStatus.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED))
							{
								++loApprovedServicesCounter;
							}
						}
					}
					// set all the values in request to show the details on home
					// portlet
					aoRequest.setAttribute("isDataAvailable", true);
					aoRequest.setAttribute("organizationStatus", loApplicationSummary.getMsProviderStatus());
					aoRequest.setAttribute("businessAppStatus", lsBussAppStatus);
					aoRequest.setAttribute("numberOfServices", loNumberOfServicesCounter);
					aoRequest.setAttribute("numberOfApprovedService", loApprovedServicesCounter);
					aoRequest.setAttribute("isDataAvailable", true);
					// Start : R5 Added
					aoRequest.setAttribute(HHSR5Constants.BUSS_APP_EXPIRING_DATE,
							loChannel.getData(HHSR5Constants.BUSS_APP_EXPIRING_DATE));
					aoRequest.setAttribute(HHSR5Constants.SERVICE_APP_EXPIRING_DATE,
							loChannel.getData(HHSR5Constants.SERVICE_APP_EXPIRING_DATE));
					// End : R5 Added
				}
				else
				{
					aoRequest.setAttribute("isDataAvailable", false);
				}
			}
			else
			{
				aoRequest.setAttribute("isDataAvailable", false);
			}
		}
		catch (ApplicationException aoFbAppEx)
		{
			LOG_OBJECT.Error("Error occured while getting application data", aoFbAppEx);
		}
		ModelAndView loModelAndView = new ModelAndView(lsFormPath);

		long loEndTimeTime = System.currentTimeMillis();
		try
		{
			LOG_OBJECT.Debug("TIME TAKEN for execution of render Method in HomeApplicationController = "
					+ (loEndTimeTime - loStartTime));
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Error while execution of render Method in HomeApplicationController ", aoEx);
		}
		UserThreadLocal.unSet();
		return loModelAndView;
	}

	/**
	 * This method performs the required action, by setting the required values
	 * in the channel object and thereafter executing the transaction.
	 * @param aoRequest render request object
	 * @param aoResponse render response object
	 * @throws ApplicationException
	 */
	protected void handleActionRequestInternal(ActionRequest aoRequest, ActionResponse aoResponse)
			throws ApplicationException
	{
		long loStartTime = System.currentTimeMillis();
		PortletSession loPortletSessionThread = aoRequest.getPortletSession();
		String lsUserIdThreadLocal = (String) loPortletSessionThread.getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
		UserThreadLocal.setUser(lsUserIdThreadLocal);
		String lsUrl = aoRequest
				.getContextPath()
				.concat("/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_business_summary&_nfls=false&app_menu_name=header_application&lsTermsConditions=termsAndCondition&first_action=provider");
		// it will redirect to Terms and condition
		try
		{
			aoResponse.sendRedirect(lsUrl);
		}
		catch (IOException aoEx)
		{
			throw new ApplicationException(" Not able to redirect to open Terms and conditionpage", aoEx);
		}

		long loEndTimeTime = System.currentTimeMillis();
		try
		{
			LOG_OBJECT.Debug("TIME TAKEN for execution of action Method in HomeApplicationController = "
					+ (loEndTimeTime - loStartTime));
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Error while execution of action Method in HomeApplicationController ", aoEx);
		}
		UserThreadLocal.unSet();
	}
}
