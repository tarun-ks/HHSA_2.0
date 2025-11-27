package com.nyc.hhs.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.constants.TransactionConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.ApplicationSummary;
import com.nyc.hhs.model.NotificationDataBean;
import com.nyc.hhs.model.ProviderStatusBean;
import com.nyc.hhs.model.StatusBean;
import com.nyc.hhs.model.UserThreadLocal;
import com.nyc.hhs.model.WithdrawRequestDetails;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.ApplicationSession;
import com.nyc.hhs.util.BusinessApplicationUtil;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.FileNetOperationsUtils;
import com.nyc.hhs.util.PortalUtil;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.ProviderStatusBusinessRules;

/**
 * Business Summary Controller used to control the behavior of the application
 * summary for provider and accelerator.
 * 
 */

public class BusinessSummaryController extends AbstractController
{

	private static final LogInfo LOG_OBJECT = new LogInfo(BusinessSummaryController.class);

	/**
	 * This method is handle all the rendering activities for the service
	 * application, also method sets the values in the RenderRequest reference,
	 * so that same values can be displayed on the required jsp.
	 * 
	 * @param aoRequest - RenderRequest
	 * @param aoResponse - RenderResponse
	 * @return loModelAndView
	 * @throws ApplicationException
	 */
	@Override
	protected ModelAndView handleRenderRequestInternal(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		long loStartTime = System.currentTimeMillis();
		PortletSession loPortletSessionThread = aoRequest.getPortletSession();
		String lsUserIdThreadLocal = (String) loPortletSessionThread.getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
		UserThreadLocal.setUser(lsUserIdThreadLocal);
		aoResponse.setContentType("text/html");
		String lsUrl = "applicationSummaryInfo";
		ModelAndView loModelAndView = null;
		Map<ApplicationSummary, List<ApplicationSummary>> loApplicationSummaryMap = new LinkedHashMap<ApplicationSummary, List<ApplicationSummary>>();
		try
		{
			getModuleName(aoRequest);
			if (aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE) != null
					&& !aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE).isEmpty())
			{
				LOG_OBJECT.Debug("Internal Error occured in Bussiness Application page ");
				loModelAndView = new ModelAndView("errorpage");
				return loModelAndView;
			}
			PortletSession loPortletSession = aoRequest.getPortletSession(true);
			loPortletSession.removeAttribute("addNewService", PortletSession.APPLICATION_SCOPE);
			loPortletSession.removeAttribute("headerPostService", PortletSession.APPLICATION_SCOPE);
			loPortletSession.removeAttribute("headerPostSubmitionService", PortletSession.APPLICATION_SCOPE);
			loPortletSession.removeAttribute("loReadOnlyStatus", PortletSession.APPLICATION_SCOPE);
			String lsOrgId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);
			String lsCityUserSearchProviderId = (String) aoRequest.getPortletSession().getAttribute(
					"cityUserSearchProviderId", PortletSession.APPLICATION_SCOPE);
			if (null != lsCityUserSearchProviderId && lsCityUserSearchProviderId.contains(HHSConstants.TILD))
			{
				lsCityUserSearchProviderId = lsCityUserSearchProviderId.substring(0,
						lsCityUserSearchProviderId.indexOf(HHSConstants.TILD));
			}
			if (lsCityUserSearchProviderId != null)
			{
				lsOrgId = lsCityUserSearchProviderId;
				lsUrl = "shareDocheader";
				aoRequest.setAttribute(ApplicationConstants.FILE_TO_INCLUDE,
						"/WEB-INF/jsp/businessapplication/applicationSummaryInfo.jsp");
				aoRequest.setAttribute("cityApplicationSummary", true);
				aoRequest.setAttribute("action", "businessSummary");
			}
			String lsTermsConditions = aoRequest.getParameter("lsTermsConditions");
			if (lsTermsConditions == null)
			{
				lsTermsConditions = PortalUtil.parseQueryString(aoRequest, "lsTermsConditions");
			}
			String lsFirstAction = PortalUtil.parseQueryString(aoRequest, "first_action");
			String lsAppExistsCheck = PortalUtil.parseQueryString(aoRequest, "appExistingStatus");
			if (lsAppExistsCheck != null)
			{
				aoRequest.setAttribute("appExistingStatus", "yes");
			}
			final String lsDisplaySummaryPage = (String) aoRequest.getParameter("displaySummaryPage");
			final String lsDisplayHistory = (String) aoRequest.getParameter("displayHistory");
			final String loIsDisplay = PortalUtil.parseQueryString(aoRequest, "loIsDisplay");
			List<ApplicationSummary> loCurrentStatusList = new ArrayList<ApplicationSummary>();
			final String lsStartNewApplication = (String) aoRequest.getParameter("startNewApplication");
			String lsFinalView = null;
			Object loFinalView = ApplicationSession.getAttribute(aoRequest, "finalView");
			if (loFinalView != null)
			{
				lsFinalView = (String) loFinalView;
			}
			else if (PortalUtil.parseQueryString(aoRequest, "finalView") != null)
			{
				lsFinalView = (String) PortalUtil.parseQueryString(aoRequest, "finalView");
			}
			if (lsFinalView != null)
			{
				aoRequest.setAttribute("applicationType", lsFinalView);
				String lsTransaction = "getFinalViewData";
				String lsBussAppId = (String) loPortletSession.getAttribute("bussAppId",
						PortletSession.APPLICATION_SCOPE);
				if (lsBussAppId == null)
				{
					lsBussAppId = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.KEY_BUSINESS_APP_ID);
					if (lsBussAppId == null)
					{
						throw new ApplicationException(
								"Not able to get Business Application id to get final view for organization " + lsOrgId);
					}
				}
				aoRequest.setAttribute(ApplicationConstants.KEY_BUSINESS_APP_ID, lsBussAppId);
				String lsServiceId = (String) loPortletSession.getAttribute("service_app_id",
						PortletSession.APPLICATION_SCOPE);
				if (lsFinalView.equalsIgnoreCase("service"))
				{
					lsBussAppId = PortalUtil.parseQueryString(aoRequest, "elementId");
					aoRequest.setAttribute("elementId", lsBussAppId);
					if (lsServiceId == null)
					{
						lsServiceId = PortalUtil.parseQueryString(aoRequest, "service_app_id");
					}
					aoRequest.setAttribute("service_app_id", lsServiceId);
				}

				P8UserSession loUserSession = (P8UserSession) loPortletSession.getAttribute(
						ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
				Channel loChannelobj = getChannelForFinalView(lsOrgId, lsBussAppId, loUserSession);
				if (lsFinalView.equalsIgnoreCase("service") || lsFinalView.equalsIgnoreCase("business"))
				{
					lsUrl = finalViewForAppType(aoRequest, lsFinalView, lsTransaction, lsServiceId, loChannelobj);
				}
				else if (lsFinalView.equalsIgnoreCase("info"))
				{
					return setFinalViewInfo(aoRequest, ApplicationConstants.PROVIDER_ORG, loUserSession);
				}
			}
			else if (null != lsFirstAction
					&& (lsFirstAction.equalsIgnoreCase("provider") || lsFirstAction.equalsIgnoreCase("accelerator")))
			{
				lsUrl = doFirstAction(aoRequest, lsUrl, loApplicationSummaryMap, loPortletSession, lsOrgId,
						lsFirstAction, loCurrentStatusList);
			}
			else if (lsStartNewApplication != null && lsStartNewApplication.equalsIgnoreCase("startNewApplication"))
			{
				lsUrl = startNewApplication(aoRequest, loPortletSession);
			}
			else if (lsDisplaySummaryPage != null && lsDisplaySummaryPage.equalsIgnoreCase("displaySummaryPage"))
			{

				lsUrl = displaySummaryPage(aoRequest, loCurrentStatusList);
			}
			else if (lsDisplayHistory != null && lsDisplayHistory.equalsIgnoreCase("displayHistory"))
			{
				aoRequest.setAttribute("historyList", ApplicationSession.getAttribute(aoRequest, "displayHistoryPage"));
				lsUrl = "displaySummaryList";
			}
			// this is for when user click on the buttons
			else if (lsTermsConditions != null && lsTermsConditions.equalsIgnoreCase("termsAndCondition"))
			{
				lsUrl = showAppTermsAndCondition(aoRequest);
			}
			else if (loIsDisplay != null && loIsDisplay.equalsIgnoreCase("show") && lsTermsConditions != null
					&& lsTermsConditions.equalsIgnoreCase("serviceTermsAndCondition"))
			{
				lsUrl = showSystemTermsAndCondition(aoRequest);
			}
			// set the url in to the model and view and return that.
			aoRequest.setAttribute("ownerProviderId", lsCityUserSearchProviderId);
			if (lsUrl.equalsIgnoreCase("finalView"))
			{
				String lsAppId = (String) loPortletSession.getAttribute("applicationId",
						PortletSession.APPLICATION_SCOPE);
				if (lsAppId == null)
				{
					lsAppId = PortalUtil.parseQueryString(aoRequest, "applicationId");
					if (lsAppId == null)
					{
						throw new ApplicationException(
								"Not able to get Application id to get final view for organization " + lsOrgId);
					}
				}
				aoRequest.setAttribute("applicationId", lsAppId);
				aoRequest.setAttribute("section", "businessapplicationsummary");
				lsUrl = "finalview_header";
				aoRequest.setAttribute("fileToInclude", "/WEB-INF/jsp/businessapplication/finalView.jsp");
			}
			loModelAndView = new ModelAndView(lsUrl);
		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Error occured in BusinessSummaryController ", aoAppExp);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, ApplicationConstants.ERROR_MESSAGE_FILENET_DOWN);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
			loModelAndView = new ModelAndView("errorHandler");
		}
		catch (Exception aoAppExp)
		{
			LOG_OBJECT.Error("Internal Error occured in BusinessSummaryController ", aoAppExp);
			loModelAndView = new ModelAndView("errorpage");
		}

		long loEndTimeTime = System.currentTimeMillis();
		try
		{
			LOG_OBJECT.Debug("TIME TAKEN for execution of render Method in BusinessSummaryController = "
					+ (loEndTimeTime - loStartTime));
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Error while execution of render Method in BusinessSummaryController", aoEx);
			loModelAndView = new ModelAndView("errorpage");
		}
		UserThreadLocal.unSet();
		return loModelAndView;
	}

	/**
	 * This method gets the module name.
	 * 
	 * @param aoRequest - RenderRequest
	 */
	private void getModuleName(RenderRequest aoRequest)
	{
		String lsModuleName = PortalUtil.parseQueryString(aoRequest,
				ApplicationConstants.KEY_SESSION_APPLICATION_MODULE);
		// get the module name when it is null.
		if (lsModuleName != null && !lsModuleName.equalsIgnoreCase("share"))
		{
			aoRequest.getPortletSession().removeAttribute("cityUserSearchProviderId", PortletSession.APPLICATION_SCOPE);
		}
	}

	/**
	 * This method performs the first Action, as user clicks on the application
	 * tab.
	 * 
	 * @param aoRequest - RenderRequest
	 * @param sUrl - url of the jsp
	 * @param aoApplicationSummaryMap - Application Summary Map
	 * @param aoPortletSession - PortletSession
	 * @param asOrgId - Organization Id
	 * @param asFirstAction - First Action to be performed
	 * @param aoCurrentStatusList - Current Status List
	 * @return - asUrl
	 */
	private String doFirstAction(RenderRequest aoRequest, String asUrl,
			Map<ApplicationSummary, List<ApplicationSummary>> aoApplicationSummaryMap, PortletSession aoPortletSession,
			String asOrgId, String asFirstAction, List<ApplicationSummary> aoCurrentStatusList)
	{
		// when user comes at 1st time
		// as we have a same jsp for provider and accelerator so need to set the
		// application user as provider or accelerator
		aoRequest.setAttribute("applicationUser", asFirstAction);
		aoRequest.setAttribute("unAuthorizedAccessError", aoRequest.getParameter("unAuthorizedAccessError"));
		// channel object
		Channel loChannelobj = new Channel();
		loChannelobj.setData(ApplicationConstants.ORG_ID, asOrgId);
		String lbTermsAndCondFlag = "show";
		try
		{
			// execute transaction to check the provider status and expiration
			// date that will return the object
			TransactionManager.executeTransaction(loChannelobj, ApplicationConstants.CHECK_APP);
			// get the data after executing the transaction
			ApplicationSummary loApplicationSummary = (ApplicationSummary) loChannelobj.getData("loApplicationBean");

			//[Start]]R4.1 Add on : provider organization expiration date
			java.sql.Date loExp_date = loApplicationSummary.getMsExpirationDate();
			//[End]R4.1 Add on : provider organization expiration date

			// if the application exits
			if (loApplicationSummary != null)
			{
				// create new channel to get the business application and
				// service application data
				Channel loChannel = new Channel();
				loChannel.setData(ApplicationConstants.KEY_SESSION_ORG_ID, asOrgId);
				// flag passed to distinguish whether request is coming from
				// home page or service summary page
				loChannel.setData(ApplicationConstants.NAVIGATION_TYPE, ApplicationConstants.FROM_SERVICE_SUMMARY_PAGE);
				// execute transaction to get the date for business and service
				TransactionManager.executeTransaction(loChannel, ApplicationConstants.RETRIEVE_APPLICATION_SUMMARY);
				// get the data after executing the transaction
				aoApplicationSummaryMap = (Map<ApplicationSummary, List<ApplicationSummary>>) loChannel
						.getData("loApplicationSummaryMap");
				Map<ApplicationSummary, List<ApplicationSummary>> loApplicationSummaryMapNew = new LinkedHashMap<ApplicationSummary, List<ApplicationSummary>>();
				// set loApplicationSummaryMap as null to go the terms and
				// condition
				if (aoApplicationSummaryMap != null && !aoApplicationSummaryMap.isEmpty())
				{
					lbTermsAndCondFlag = showAppSummary(aoRequest, aoApplicationSummaryMap, asOrgId,
							aoCurrentStatusList, lbTermsAndCondFlag, loApplicationSummary, loApplicationSummaryMapNew);
				}
				else
				{
					String lsOrgType = (String) aoPortletSession.getAttribute(
							ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
					if (lsOrgType.equalsIgnoreCase(ApplicationConstants.CITY_ORG))
					{
						asUrl = "shareDocheader";
						aoRequest.setAttribute("printView", "no");
					}
					else
					{
						aoPortletSession.setAttribute("loReadOnlyStatus", ApplicationConstants.STATUS_DRAFT,
								PortletSession.APPLICATION_SCOPE);
						String lsDisplayTermsCondition = getTermsAndCondition(aoRequest,
								"Application Terms & Conditions");
						aoRequest.setAttribute("lsDisplayTermsCondition", lsDisplayTermsCondition);
						aoRequest.setAttribute("next_action", "createNewApplication");
						asUrl = "basic";
					}
				}
			}
			else
			{
				aoPortletSession.setAttribute("loReadOnlyStatus", ApplicationConstants.STATUS_DRAFT,
						PortletSession.APPLICATION_SCOPE);
				String lsDisplayTermsCondition = getTermsAndCondition(aoRequest, "Application Terms & Conditions");
				aoRequest.setAttribute("lsDisplayTermsCondition", lsDisplayTermsCondition);
				asUrl = "basic";
				aoRequest.setAttribute("next_action", "createNewApplication");
			}
			if (lbTermsAndCondFlag.equalsIgnoreCase("show"))
			{
				aoPortletSession.setAttribute("loReadOnlyStatus", ApplicationConstants.STATUS_DRAFT,
						PortletSession.APPLICATION_SCOPE);
			}
			//[Start]]R4.1 Add on : provider organization expiration date
			loApplicationSummary.setMsExpirationDate(loExp_date);
			//[End]]R4.1 Add on : provider organization expiration date
		}
		catch (ApplicationException aoFbAppEx)
		{
			LOG_OBJECT.Error("Error occured while getting application data", aoFbAppEx);
		}
		catch (IOException aoExp)
		{
			LOG_OBJECT.Error("IOException occured while getting application data", aoExp);
		}
		return asUrl;
	}

	/**
	 * this method get the channel with required values for final view.
	 * 
	 * @param asOrgId - organization id
	 * @param asBussAppId - Business Application Id
	 * @param aoUserSession - User Session
	 * @return loChannelobj - Channel object
	 */
	private Channel getChannelForFinalView(String asOrgId, String asBussAppId, P8UserSession aoUserSession)
	{
		Channel loChannelobj = new Channel();
		loChannelobj.setData("asAppId", asBussAppId);
		loChannelobj.setData(ApplicationConstants.ORG_ID, asOrgId);
		loChannelobj.setData("abIsFinalView", true);
		loChannelobj.setData("asBussAppId", asBussAppId);

		loChannelobj.setData("aoFilenetSession", aoUserSession);
		return loChannelobj;
	}

	/**
	 * This method sets final view information in request object.
	 * 
	 * @param aoRequest - RenderRequest
	 * @param asOrgType - Organization Type
	 * @param aoUserSession - P8UserSession
	 * @return - loModelAndView
	 * @throws ApplicationException - throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	private ModelAndView setFinalViewInfo(RenderRequest aoRequest, String asOrgType, P8UserSession aoUserSession)
			throws ApplicationException
	{
		String lsDocumentId = PortalUtil.parseQueryString(aoRequest, HHSR5Constants.DOC_ID);
		// added for R5
		String lsUserOrg = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);
		String lsDocType = null;
		String lsDocCategory = null;
		HashMap<String, String> loMap = new HashMap<String, String>();
		Channel loChannel = new Channel();
		loChannel.setData(HHSConstants.DOC_ID, lsDocumentId);
		loChannel.setData("loUserSession", aoUserSession);
		HHSTransactionManager.executeTransaction(loChannel, "getDocTypeAndCategory",
				HHSR5Constants.TRANSACTION_ELEMENT_R5);
		loMap = (HashMap<String, String>) loChannel.getData("loDataMap");
		lsDocType = loMap.get(HHSR5Constants.DOCTYPE);
		lsDocCategory = loMap.get(HHSR5Constants.DOCUMENT_CATEGORY);
		// R5 ends
		com.nyc.hhs.model.Document loDocument = FileNetOperationsUtils.viewDocumentInfo(aoUserSession, asOrgType,
				lsDocumentId, lsDocType, lsUserOrg, lsDocCategory, null);
		aoRequest.setAttribute("document", loDocument);
		// Added for R5- changed jsp name
		aoRequest.setAttribute(ApplicationConstants.FILE_TO_INCLUDE,
				"/WEB-INF/jsp/businessapplication/viewdocumentinfo_overlay_bapp.jsp");
		// R5 end
		ModelAndView loModelAndView = new ModelAndView("finalview_header");
		return loModelAndView;
	}

	/**
	 * This method gives the url for final view of application.
	 * 
	 * @param aoRequest - RenderRequest
	 * @param asFinalView - Final View
	 * @param asTransaction - Transaction name
	 * @param asServiceId - Service Id
	 * @param aoChannelobj - Channel object
	 * @return - lsUrl
	 * @throws ApplicationException
	 */
	private String finalViewForAppType(RenderRequest aoRequest, String asFinalView, String asTransaction,
			String asServiceId, Channel aoChannelobj) throws ApplicationException
	{
		String lsUrl;
		if (asFinalView.equalsIgnoreCase("service"))
		{
			aoChannelobj.setData("service_app_id", asServiceId);
		}
		InputStream loIoStream = null;
		try
		{
			TransactionManager.executeTransaction(aoChannelobj, asTransaction);
			loIoStream = (InputStream) aoChannelobj.getData("documentContent");
			if (loIoStream != null)
			{
				String lsContent = BusinessApplicationUtil.convertStreamToString(loIoStream);
				Map<String, Object> loMap = BusinessApplicationUtil.convertStringToMap(lsContent);
				aoRequest.setAttribute("content", loMap);
			}
		}
		finally
		{
			try
			{
				if (null != loIoStream)
				{
					loIoStream.close();
				}
			}
			catch (Exception aoEx)
			{
				LOG_OBJECT.Error("Exception occured in BusinessSummaryController: finalViewForAppType method::", aoEx);
				throw new ApplicationException(aoEx.getMessage(), aoEx);
			}

		}

		lsUrl = "finalView";
		return lsUrl;
	}

	/**
	 * This method shows the application summary.
	 * 
	 * @param aoRequest - RenderRequest
	 * @param aoApplicationSummaryMap - Application Summary Map
	 * @param asOrgId - organization id
	 * @param aoCurrentStatusList - Current Status List
	 * @param abTermsAndCondFlag - Terms And Condition Flag
	 * @param aoApplicationSummary - ApplicationSummary object
	 * @param aoApplicationSummaryMapNew - Application Summary Map New
	 * @return - abTermsAndCondFlag
	 * @throws ApplicationException - throws ApplicationException
	 */
	private String showAppSummary(RenderRequest aoRequest,
			Map<ApplicationSummary, List<ApplicationSummary>> aoApplicationSummaryMap, String asOrgId,
			List<ApplicationSummary> aoCurrentStatusList, String abTermsAndCondFlag,
			ApplicationSummary aoApplicationSummary,
			Map<ApplicationSummary, List<ApplicationSummary>> aoApplicationSummaryMapNew) throws ApplicationException
	{
		Integer lsApplicationCounter = 1;
		for (Map.Entry<ApplicationSummary, List<ApplicationSummary>> loEntry : aoApplicationSummaryMap.entrySet())
		{
			abTermsAndCondFlag = iterateDeActivatedList(aoRequest, asOrgId, abTermsAndCondFlag,
					aoApplicationSummaryMapNew, loEntry, lsApplicationCounter);
			++lsApplicationCounter;
		}
		aoRequest.setAttribute("lbTermsAndCondFlag", abTermsAndCondFlag);
		// now create 2 list one that will contain the most updated record
		// (always be 1) to display on the 1st grid and other
		// for the second grid
		List<ApplicationSummary> loFinalValueList = new ArrayList<ApplicationSummary>();
		if (null != aoApplicationSummaryMapNew && !aoApplicationSummaryMapNew.isEmpty())
		{
			Entry<ApplicationSummary, List<ApplicationSummary>> loTemp = aoApplicationSummaryMapNew.entrySet()
					.iterator().next();
			aoCurrentStatusList.add(loTemp.getKey());
			List<ApplicationSummary> loValueList = loTemp.getValue();
			if (loValueList != null && !loValueList.isEmpty())
			{
				for (ApplicationSummary loApplicationSummary : loValueList)
				{
					String lsEvent = loApplicationSummary.getEventName();

					if (loApplicationSummary.getMsAppStatus().equalsIgnoreCase(ApplicationConstants.DEACTIVATED))
					{
						if (!(lsEvent != null && (lsEvent.equalsIgnoreCase("approvalToNonApproval") || lsEvent
								.equalsIgnoreCase("EvidenceMovedFromChildToParent"))))
						{
							loFinalValueList.add(loApplicationSummary);
						}
						else
						{
							if (lsEvent.equalsIgnoreCase("EvidenceMovedFromChildToParent")
									|| lsEvent.equalsIgnoreCase("approvalToNonApproval")
									&& (loApplicationSummary.getMsDeactivatedStatus().equalsIgnoreCase(
											ApplicationConstants.STATUS_DRAFT)
											|| loApplicationSummary.getMsDeactivatedStatus().equalsIgnoreCase(
													ApplicationConstants.STATUS_IN_REVIEW)
											|| loApplicationSummary.getMsDeactivatedStatus().equalsIgnoreCase(
													ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS) || loApplicationSummary
											.getMsDeactivatedStatus().equalsIgnoreCase(
													ApplicationConstants.STATUS_DEFFERED)))
							{
								loFinalValueList.add(loApplicationSummary);
							}
						}
					}
					else
					{
						loFinalValueList.add(loApplicationSummary);
					}
				}
			}
			aoCurrentStatusList.addAll(loFinalValueList);
		}
		Integer loCounter = 0;
		Boolean lsDisplayDropDown = false;
		if ((aoApplicationSummaryMapNew != null && !aoApplicationSummaryMapNew.isEmpty())
				&& aoApplicationSummaryMapNew.size() > 1)
		{
			ApplicationSession.setAttribute("true", aoRequest, "moreThanOneOrgForOrg");
			for (Map.Entry<ApplicationSummary, List<ApplicationSummary>> loFinalMap : aoApplicationSummaryMapNew
					.entrySet())
			{
				ApplicationSummary loApplicationSummary = loFinalMap.getKey();
				if (loCounter == 0
						&& (loApplicationSummary.getMsAppStatus().equalsIgnoreCase(ApplicationConstants.STATUS_DRAFT)))
				{
					if (!(loApplicationSummary.getMsSuperSedingStatus() != null && loApplicationSummary
							.getMsSuperSedingStatus().equalsIgnoreCase(
									ApplicationConstants.STATUS_CONDITIONALLY_APPROVED)))
					{
						lsDisplayDropDown = true;
					}
				}
				if (loCounter == 1 && lsDisplayDropDown)
				{
					if (loApplicationSummary.getMsSuperSedingStatus() == null
							&& loApplicationSummary.getMsAppStatus().equalsIgnoreCase(
									(ApplicationConstants.STATUS_APPROVED)))
					{
						loApplicationSummary.setDisplayHistoryDropDown(true);
					}
					else
					{
						loApplicationSummary.setDisplayHistoryDropDown(false);
					}
					if (loFinalMap.getValue() != null && !loFinalMap.getValue().isEmpty())
					{
						for (ApplicationSummary loAppSummary : loFinalMap.getValue())
						{
							if (loAppSummary.getMsSuperSedingStatus() == null
									&& loAppSummary.getMsAppStatus().equalsIgnoreCase(
											(ApplicationConstants.STATUS_APPROVED)))
							{
								loAppSummary.setDisplayHistoryDropDown(true);
							}
							else
							{
								loAppSummary.setDisplayHistoryDropDown(false);
							}
						}
					}
					break;
				}
				++loCounter;
			}
		}
		aoRequest.setAttribute("currentItemList", aoCurrentStatusList);
		aoApplicationSummary.setMsExpirationDate(aoCurrentStatusList.get(0).getMdAppExpirationDate());
		aoRequest.setAttribute("lsDisplayDropDown", lsDisplayDropDown);
		aoRequest.setAttribute("completeItemMap", aoApplicationSummaryMapNew);
		// set the provider data into session because control will not come here
		// when user update the status from the selection box
		ApplicationSession.setAttribute(aoApplicationSummary, aoRequest, "providerRecord");
		// set the provider data into the request
		aoRequest.setAttribute("loApplicationSummaryObj",
				ApplicationSession.getAttribute(aoRequest, true, "providerRecord"));
		Map<String, String> loDisabledButtonMap = getButtonStatus(aoCurrentStatusList.get(0).getMsAppStatus(),
				aoApplicationSummary.getMsExpirationDate(),
				aoCurrentStatusList.get(0).getMsSuperSedingStatus(),
				// R5 : Added for Readonly user
				(String) aoRequest.getPortletSession(true).getAttribute(
						ApplicationConstants.KEY_SESSION_USER_PERMISSION_TYPE, PortletSession.APPLICATION_SCOPE));
		// to display the value in select box when accelarator login
		Map<String, String> loSelectBoxStatusMap = null;
		loSelectBoxStatusMap = getSelectBoxValue(aoCurrentStatusList);
		aoRequest.setAttribute("loDisabledButtonMap", loDisabledButtonMap);
		aoRequest.setAttribute("loSelectBoxStatusMap", loSelectBoxStatusMap);
		return abTermsAndCondFlag;
	}

	/**
	 * This method iterates DeActivated List
	 * 
	 * @param aoRequest - RenderRequest
	 * @param asOrgId - organization id
	 * @param abTermsAndCondFlag - Terms And Condition Flag
	 * @param aoApplicationSummaryMapNew - Application Summary Map New
	 * @param aoEntry - list of ApplicationSummary objects
	 * @return - abTermsAndCondFlag
	 * @throws ApplicationException
	 */
	private String iterateDeActivatedList(RenderRequest aoRequest, String asOrgId, String abTermsAndCondFlag,
			Map<ApplicationSummary, List<ApplicationSummary>> aoApplicationSummaryMapNew,
			Map.Entry<ApplicationSummary, List<ApplicationSummary>> aoEntry, Integer aoApplicationCounter)
			throws ApplicationException
	{
		Channel loChannel;
		String lsBussAppStatus;
		List<ApplicationSummary> loDeactivatedList;
		ApplicationSummary loKey = aoEntry.getKey();
		String lsBusinessWithDrawlStatus = loKey.getMsWithdrawanStatus();
		if (lsBusinessWithDrawlStatus != null
				&& lsBusinessWithDrawlStatus.equalsIgnoreCase(ApplicationConstants.STATUS_IN_REVIEW))
		{
			String lsUserName = loKey.getMsRequester();
			if (loKey.getMsRequester() != null && isNumeric(loKey.getMsRequester()))
			{
				lsUserName = FileNetOperationsUtils.getUserName(loKey.getMsRequester());
			}
			loKey.setDisplayExclamanationBusiness(true);
			aoRequest.setAttribute("withDrawnRequest", true);
			loKey.setMsRequester(lsUserName);
			loKey.setTimeStampStatus1(loKey.getMdRequestDate().toString());
		}

		loDeactivatedList = new ArrayList<ApplicationSummary>();
		loChannel = new Channel();
		loChannel.setData("businessAppId", loKey.getMsBusinessAppId());
		loChannel.setData("lsOrgId", asOrgId);
		TransactionManager.executeTransaction(loChannel, "getBussAppUpdatedStatus");
		Map<String, Object> loUpdateStatusObj = (Map<String, Object>) loChannel.getData("bussAppUpdatedStatus");
		if (loUpdateStatusObj != null && !loUpdateStatusObj.isEmpty())
		{
			if (aoApplicationCounter > 1 && loUpdateStatusObj.size() > 1)
			{
				loKey.setFinalViewBusinessExclamanationSign(true);
			}
			String lsUserName = "";
			if (loUpdateStatusObj.get("USER_ID") != null)
			{
				lsUserName = (String) loUpdateStatusObj.get("USER_ID");
			}
			lsBussAppStatus = (String) loUpdateStatusObj.get("STATUS");
			loKey.setMsSuperSedingStatus(lsBussAppStatus);
			loKey.setMsRequester(lsUserName);
			String lsTimeStamp = "";
			if (loUpdateStatusObj.get("TIMESTAMP") != null)
			{
				lsTimeStamp = loUpdateStatusObj.get("TIMESTAMP").toString();
			}
			loKey.setTimeStampStatus1(lsTimeStamp);
		}
		aoApplicationSummaryMapNew.put(loKey, loDeactivatedList);
		List<ApplicationSummary> loApplicationSummaryList = aoEntry.getValue();
		if (loApplicationSummaryList != null && !loApplicationSummaryList.isEmpty())
		{
			abTermsAndCondFlag = showAppSummaryList(aoRequest, asOrgId, abTermsAndCondFlag, loDeactivatedList,
					loApplicationSummaryList, aoApplicationCounter, lsBusinessWithDrawlStatus);
		}
		return abTermsAndCondFlag;
	}

	/**
	 * This method shows application summary list
	 * 
	 * @param aoRequest - RenderRequest
	 * @param asOrgId - organization id
	 * @param abTermsAndCondFlag - TermsAndCondFlag
	 * @param loDeactivatedList - Deactivated List
	 * @param loApplicationSummaryList - Application ummary List
	 * @return - abTermsAndCondFlag
	 * @throws ApplicationException
	 */
	private String showAppSummaryList(RenderRequest aoRequest, String asOrgId, String abTermsAndCondFlag,
			List<ApplicationSummary> aoDeactivatedList, List<ApplicationSummary> aoApplicationSummaryList,
			Integer aoApplicationCounter, String asBusinessWithDrawlStatus) throws ApplicationException
	{
		Channel loChannel;
		String lsBussAppStatus;
		for (ApplicationSummary loApplicationSummary : aoApplicationSummaryList)
		{
			loApplicationSummary.setMsDeactivatedStatus(loApplicationSummary.getMsAppStatus());

			String lsServiceWithDrawlStatus = loApplicationSummary.getMsWithdrawanStatus();
			if (lsServiceWithDrawlStatus != null
					&& lsServiceWithDrawlStatus.equalsIgnoreCase(ApplicationConstants.STATUS_IN_REVIEW))
			{
				String lsUserName = loApplicationSummary.getMsRequester();
				if (loApplicationSummary.getMsRequester() != null && isNumeric(loApplicationSummary.getMsRequester()))
				{
					lsUserName = FileNetOperationsUtils.getUserName(loApplicationSummary.getMsRequester());
				}

				if (asBusinessWithDrawlStatus != null
						&& (asBusinessWithDrawlStatus.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED) || (asBusinessWithDrawlStatus
								.equalsIgnoreCase(ApplicationConstants.STATUS_REJECTED) && !lsServiceWithDrawlStatus
								.equalsIgnoreCase(ApplicationConstants.STATUS_IN_REVIEW))))
				{
					loApplicationSummary.setDisplayExclamanationService(false);
					aoRequest.setAttribute("withDrawnRequestService", false);
				}
				else
				{
					loApplicationSummary.setDisplayExclamanationService(true);
					aoRequest.setAttribute("withDrawnRequestService", true);
				}

				loApplicationSummary.setMsRequester(lsUserName);
				loApplicationSummary.setTimeStampStatus1(loApplicationSummary.getMdRequestDate().toString());
			}

			if (loApplicationSummary.getMsAppStatus() != null
					&& loApplicationSummary.getMsAppStatus().equalsIgnoreCase(ApplicationConstants.STATUS_DRAFT))
			{
				abTermsAndCondFlag = "hide";
			}

			// get the status based on the service application id
			loChannel = new Channel();
			loChannel.setData("serviceAppId", loApplicationSummary.getMsServiceAppId());
			loChannel.setData("lsOrgId", asOrgId);
			TransactionManager.executeTransaction(loChannel, "getServiceAppUpdatedStatus");
			Map<String, Object> loUpdateStatusServiceObj = (Map<String, Object>) loChannel
					.getData("serviceAppUpdatedStatus");
			if (loUpdateStatusServiceObj != null && !loUpdateStatusServiceObj.isEmpty())
			{
				if (aoApplicationCounter > 1)
				{
					loApplicationSummary.setFinalViewServicdExclamanationSign(true);
				}
				String lsEvent = (String) loUpdateStatusServiceObj.get("EVENT");
				String lsUserName = "";
				if (loUpdateStatusServiceObj.get("USER_ID") != null)
				{
					lsUserName = (String) loUpdateStatusServiceObj.get("USER_ID");
				}
				lsBussAppStatus = (String) loUpdateStatusServiceObj.get("STATUS");
				if (lsEvent != null && lsEvent.equalsIgnoreCase("evidenceToNonEvidence"))
				{
					loApplicationSummary.setEventName(lsEvent);
					loApplicationSummary.setMsAppStatus(ApplicationConstants.DEACTIVATED);
					loApplicationSummary.setMsSuperSedingStatus(ApplicationConstants.DEACTIVATED);
					if (loApplicationSummary.getMsDeactivatedStatus() != null
							&& loApplicationSummary.getMsDeactivatedStatus().equalsIgnoreCase(
									ApplicationConstants.STATUS_REJECTED))
					{
						loApplicationSummary.setMsSuperSedingStatus(ApplicationConstants.STATUS_REJECTED);
						loApplicationSummary.setMsAppStatus(ApplicationConstants.STATUS_REJECTED);
					}
					else if (loApplicationSummary.getMsDeactivatedStatus() != null
							&& loApplicationSummary.getMsDeactivatedStatus().equalsIgnoreCase(
									ApplicationConstants.STATUS_APPROVED))
					{
						loApplicationSummary.setMsSuperSedingStatus(ApplicationConstants.STATUS_APPROVED);
						loApplicationSummary.setMsAppStatus(ApplicationConstants.STATUS_APPROVED);
					}
				}
				else
				{
					loApplicationSummary.setMsSuperSedingStatus(lsBussAppStatus);
				}

				loApplicationSummary.setMsRequester(lsUserName);
				String lsTimeStamp = "";
				if (loUpdateStatusServiceObj.get("TIMESTAMP") != null)
				{
					lsTimeStamp = loUpdateStatusServiceObj.get("TIMESTAMP").toString();
				}
				loApplicationSummary.setTimeStampStatus1(lsTimeStamp);

				if (loApplicationSummary.getMsDeactivatedStatus() != null
						&& (lsEvent != null && (lsEvent.equalsIgnoreCase("approvalToNonApproval") || lsEvent
								.equalsIgnoreCase("EvidenceMovedFromChildToParent"))))
				{
					loApplicationSummary.setEventName(lsEvent);
					if (lsBussAppStatus != null && lsBussAppStatus.equalsIgnoreCase(ApplicationConstants.DEACTIVATED))
					{
						loApplicationSummary.setMsAppStatus(ApplicationConstants.DEACTIVATED);
					}
				}
			}
			aoDeactivatedList.add(loApplicationSummary);
		}
		return abTermsAndCondFlag;
	}

	/**
	 * This method starts New Application.
	 * 
	 * @param aoRequest - RenderRequest
	 * @param aoPortletSession - PortletSession
	 * @return - lsUrl
	 */
	private String startNewApplication(RenderRequest aoRequest, PortletSession aoPortletSession)
	{
		String lsUrl;
		aoPortletSession.setAttribute("loReadOnlyStatus", ApplicationConstants.STATUS_DRAFT,
				PortletSession.APPLICATION_SCOPE);
		String lsDisplayTermsCondition = null;
		try
		{
			lsDisplayTermsCondition = getTermsAndCondition(aoRequest, "Application Terms & Conditions");
		}
		catch (IOException aoEx)
		{
			LOG_OBJECT.Error("Error occurred while starting new application", aoEx);
		}
		aoRequest.setAttribute("lsDisplayTermsCondition", lsDisplayTermsCondition);
		lsUrl = "termsandconditions";
		aoRequest.setAttribute("next_action", "createNewApplication");

		aoRequest.setAttribute("prevBusinessAppId", aoRequest.getParameter("prevBusinessAppId"));

		aoRequest.setAttribute("startNewApplication", true);
		return lsUrl;
	}

	/**
	 * This method displays Summary Page.
	 * 
	 * @param aoRequest - RenderRequest
	 * @param aoCurrentStatusList - Current Status List
	 * @return - lsUrl
	 */
	private String displaySummaryPage(RenderRequest aoRequest, List<ApplicationSummary> aoCurrentStatusList)
	{
		String lsUrl;
		Map<ApplicationSummary, List<ApplicationSummary>> loApplicationSummaryMap;
		// get the map of data to display the updated data
		loApplicationSummaryMap = (Map<ApplicationSummary, List<ApplicationSummary>>) ApplicationSession.getAttribute(
				aoRequest, "displaySummaryPage");
		// set the application user for jsp
		aoRequest.setAttribute("applicationUser", "accelerator");
		// create a current item list
		if (!loApplicationSummaryMap.entrySet().isEmpty())
		{
			Entry<ApplicationSummary, List<ApplicationSummary>> loTempObj = loApplicationSummaryMap.entrySet()
					.iterator().next();
			aoCurrentStatusList.add(loTempObj.getKey());
			aoCurrentStatusList.addAll(loTempObj.getValue());
		}
		// set the current item list in to the request
		aoRequest.setAttribute("currentItemList", aoCurrentStatusList);
		// map into the request
		aoRequest.setAttribute("completeItemMap", loApplicationSummaryMap);
		// provider data into the request
		aoRequest.setAttribute("loApplicationSummaryObj",
				ApplicationSession.getAttribute(aoRequest, true, "providerRecord"));
		// set the URL
		lsUrl = "applicationSummaryInfo";
		return lsUrl;
	}

	/**
	 * This method shows System Terms And Condition
	 * 
	 * @param aoRequest - RenderRequest
	 * @return - lsUrl
	 */
	private String showSystemTermsAndCondition(RenderRequest aoRequest)
	{
		String lsUrl;
		String lsDisplayTermsCondition = null;
		try
		{
			lsDisplayTermsCondition = getTermsAndCondition(aoRequest, "Application Terms & Conditions");
		}
		catch (IOException aoEx)
		{
			LOG_OBJECT.Error("Error occured while displaying system terms and conditions", aoEx);
		}
		aoRequest.setAttribute("lsDisplayTermsCondition", lsDisplayTermsCondition);
		aoRequest.setAttribute("next_action", "createNewServiceApplication");
		lsUrl = "termsandconditions";
		return lsUrl;
	}

	/**
	 * This method shows Application Terms And Condition
	 * 
	 * @param aoRequest - RenderRequest
	 * @return - lsUrl
	 */
	private String showAppTermsAndCondition(RenderRequest aoRequest)
	{
		String lsUrl;
		String lsDisplayTermsCondition = null;
		try
		{
			lsDisplayTermsCondition = getTermsAndCondition(aoRequest, "Application Terms & Conditions");
		}
		catch (IOException aoEx)
		{
			LOG_OBJECT.Error("Error occured while displaying application terms and conditions", aoEx);
		}
		aoRequest.setAttribute("lsDisplayTermsCondition", lsDisplayTermsCondition);
		aoRequest.setAttribute("next_action", "createNewApplication");
		lsUrl = "basic";
		return lsUrl;
	}

	/**
	 * This method fetches the button status
	 * 
	 * @param aoApplicationStatus - application status
	 * @param aoProviderDate - provider date
	 * @param aoPermissionType - user's permission type for R,F,P,FP
	 * @param currentStatus - current status
	 * @return - loButtonMap
	 */
	private Map<String, String> getButtonStatus(String aoApplicationStatus, final Date aoProviderDate,
			final String aoCurrentStatus, String aoPermissionType) throws ApplicationException
	{
		boolean lbAddButtonFlag = false;
		if (aoApplicationStatus != null && aoApplicationStatus.equalsIgnoreCase(ApplicationConstants.STATUS_IN_REVIEW)
				&& aoCurrentStatus != null
				&& aoCurrentStatus.equalsIgnoreCase(ApplicationConstants.STATUS_CONDITIONALLY_APPROVED))
		{
			lbAddButtonFlag = true;
		}
		if (aoCurrentStatus != null)
		{
			aoApplicationStatus = aoCurrentStatus;
		}
		// create a map
		Map<String, String> loButtonMap = new LinkedHashMap<String, String>();
		// current date
		java.util.Date loCurrentDate = new Date(System.currentTimeMillis());
		// calendar object
		Calendar loCalObj = Calendar.getInstance();
		loCalObj.setTime(loCurrentDate);
		// add 6 months to the current date
		HashMap<String, String> loNotificationMap = (HashMap<String, String>) BaseCacheManagerWeb.getInstance()
				.getCacheObject(ApplicationConstants.APPLICATION_SETTING);
		loCalObj.set(Calendar.MONTH, (loCalObj.get(Calendar.MONTH) + Integer.valueOf(loNotificationMap
				.get("ProviderExpirationGracePeriod_BR002"))));
		// change calendar to sql date
		java.sql.Date loSixMonthsLaterDate = new java.sql.Date(loCalObj.getTime().getTime());
		// for add button
		if (lbAddButtonFlag)
		{
			loButtonMap.put("addButton", "");
		}
		else if (aoApplicationStatus != null
				&& ((aoApplicationStatus.equalsIgnoreCase(ApplicationConstants.STATUS_IN_REVIEW)
						|| aoApplicationStatus.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS)
						|| aoApplicationStatus.equalsIgnoreCase(ApplicationConstants.STATUS_DEFFERED) || aoApplicationStatus
							.equalsIgnoreCase(ApplicationConstants.STATUS_SUSPEND_FILING_EXPIRED)) || (aoApplicationStatus
						.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED) && (aoProviderDate != null && aoProviderDate
						.after(loSixMonthsLaterDate))))
				// R5 : Added for Readonly user
				&& (null != aoPermissionType && !ApplicationConstants.ROLE_READ_ONLY.equalsIgnoreCase(aoPermissionType)))
		{
			loButtonMap.put("addButton", "");
		}
		else
		{
			loButtonMap.put("addButton", "disabled='disabled'");
		}
		// for start new application button
		if (aoApplicationStatus != null
				&& ((aoApplicationStatus.equalsIgnoreCase(ApplicationConstants.STATUS_REJECTED)
						|| aoApplicationStatus.equalsIgnoreCase(ApplicationConstants.STATUS_WITHDRAWN)
						|| aoApplicationStatus.equalsIgnoreCase(ApplicationConstants.STATUS_SUSPEND) || aoApplicationStatus
							.equalsIgnoreCase(ApplicationConstants.STATUS_EXPIRED)) || ((aoApplicationStatus
						.equalsIgnoreCase(ApplicationConstants.STATUS_SUSPEND_FILING_EXPIRED) || aoApplicationStatus
						.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED)) && (aoProviderDate != null && aoProviderDate
						.before(loSixMonthsLaterDate))))
				// R5 : Added for Readonly user
				&& (null != aoPermissionType && !ApplicationConstants.ROLE_READ_ONLY.equalsIgnoreCase(aoPermissionType)))
		{
			loButtonMap.put("startButton", "");
		}
		else
		{
			loButtonMap.put("startButton", "disabled='disabled'");
		}
		// return the map
		return loButtonMap;
	}

	/**
	 * this method gets the value of the selected box.
	 * 
	 * @param aoCurrentStatusList - current status list
	 * @return - loSelectBoxMap
	 */
	private Map<String, String> getSelectBoxValue(List<ApplicationSummary> aoCurrentStatusList)
	{
		Integer loCounter = 1;
		String lsBusinessStatus = null;
		Map<String, String> loSelectBoxMap = new LinkedHashMap<String, String>();
		String lsAppStatus = null;
		if (aoCurrentStatusList != null && !aoCurrentStatusList.isEmpty())
		{
			for (ApplicationSummary loApplicationSummary : aoCurrentStatusList)
			{
				lsAppStatus = loApplicationSummary.getMsAppStatus();
				if (loApplicationSummary.getMsSuperSedingStatus() != null)
				{
					lsAppStatus = loApplicationSummary.getMsSuperSedingStatus();
				}
				if (loCounter == 1)
				{
					lsBusinessStatus = lsAppStatus;
				}
				if (lsAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED)
						|| lsAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_REJECTED)
						|| lsAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_WITHDRAWL)
						|| lsAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_WITHDRAWN))
				{
					loApplicationSummary.setDisplaySuspendValue(ApplicationConstants.STATUS_SUSPEND);
				}

				if (loCounter == 1
						|| (lsBusinessStatus.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED) || lsBusinessStatus
								.equalsIgnoreCase(ApplicationConstants.STATUS_CONDITIONALLY_APPROVED)))
				{
					if (lsAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_DEFFERED)
							|| lsAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_DRAFT)
							|| lsAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_IN_REVIEW)
							|| lsAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS)
							|| lsAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_SUSPEND)
							|| lsAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_SUSPEND_FILING_EXPIRED))
					{
						loApplicationSummary
								.setDisplayConditionallyValue(ApplicationConstants.STATUS_CONDITIONALLY_APPROVED);
					}
				}
				++loCounter;
			}
		}
		return loSelectBoxMap;
	}

	/**
	 * Changed in 3.1.0 . Added check for Defect 6346 This method performs the
	 * required action, by setting the required values in the channel object and
	 * thereafter executing the transaction.
	 * 
	 * @param aoRequest - ActionRequest
	 * @param aoResponse - ActionResponse
	 * @throws ApplicationException - throws ApplicationException
	 */
	@Override
	protected void handleActionRequestInternal(ActionRequest aoRequest, ActionResponse aoResponse)
			throws ApplicationException
	{
		long loStartTime = System.currentTimeMillis();
		try
		{
			PortletSession loPortletSessionThread = aoRequest.getPortletSession();
			String lsUserIdThreadLocal = (String) loPortletSessionThread.getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			UserThreadLocal.setUser(lsUserIdThreadLocal);
			// execute when add and new application button clicked
			final String lsTermsConditions = (String) aoRequest.getParameter(ApplicationConstants.ACTION_REDIRECT);
			// applicationType either business or service
			final String lsApplicationType = (String) aoRequest.getParameter(ApplicationConstants.APPLICATION_TYPE);
			// get the application or service status when click on the hyper
			// link
			String lsApplicationStatus = (String) aoRequest.getParameter("applicationStatus");
			// get the application or service application id when click on the
			// hyper
			// link
			final String lsBusinessServiceApplicationId = (String) aoRequest.getParameter("bussAppId");// element
																										// id
			// get the application id
			String lsApplicationId = (String) aoRequest.getParameter("appId");
			// check the display terms and condition shown or hide
			final String loIsDisplay = aoRequest.getParameter("lbTermsAndCondFlag");
			// only use in case if you want the business application id when u
			// click
			// on the service application link
			String lsServiceBusinessAppId = (String) aoRequest.getParameter("businessApplicationId");// br
																										// id
			// when user want to see the history
			final String lsViewHistory = (String) aoRequest.getParameter(ApplicationConstants.VIEW_HISTORY_VALUE);
			PortletSession loPortletSession = aoRequest.getPortletSession(true);
			loPortletSession.removeAttribute("headerPostService", PortletSession.APPLICATION_SCOPE);
			loPortletSession.removeAttribute("headerPostSubmitionService", PortletSession.APPLICATION_SCOPE);
			// Filenet P8 Session
			P8UserSession loUserSession = (P8UserSession) loPortletSession.getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			if (loIsDisplay != null && loIsDisplay.equalsIgnoreCase("show"))
			{
				loPortletSession.setAttribute("loReadOnlyStatus", ApplicationConstants.STATUS_DRAFT,
						PortletSession.APPLICATION_SCOPE);
			}
			// organization id coming from session
			String lsOrgId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);
			final String lsEmailId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_EMAIL_ID, PortletSession.APPLICATION_SCOPE);
			// provider id when city user login and search providers
			final String lsCityUserSearchProviderId = (String) aoRequest.getPortletSession().getAttribute(
					"cityUserSearchProviderId", PortletSession.APPLICATION_SCOPE);
			if (lsCityUserSearchProviderId != null)
			{
				// Changed in 3.1.0 . Added check for Defect 6346
				if (lsCityUserSearchProviderId.contains(ApplicationConstants.TILD))
				{
					lsOrgId = lsCityUserSearchProviderId.substring(0,
							lsCityUserSearchProviderId.indexOf(ApplicationConstants.TILD));
				}
				else
				{
					lsOrgId = lsCityUserSearchProviderId;
				}
				// Defect 6346 check ends
			}
			// user id coming from session
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			String lsServiceId = PortalUtil.parseQueryString(aoRequest, "serviceApplicationId");// service
																								// app
																								// id
			String lsWorkflowId = PortalUtil.parseQueryString(aoRequest, "workflowId");
			// this variable is used to identify the login user whether its city
			// user or provider user
			String lsOrgnizationType = (String) loPortletSession.getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			// R5 code start
			String lsPermissionType = (String) loPortletSession.getAttribute(
					ApplicationConstants.KEY_SESSION_USER_PERMISSION_TYPE, PortletSession.APPLICATION_SCOPE);
			Boolean loIsReadOnly = BusinessApplicationUtil.doCheckReadOnly(lsBusinessServiceApplicationId,
					lsApplicationStatus, null, lsServiceId, lsApplicationType, lsOrgnizationType, lsPermissionType);
			// R5 code ends
			// this map gets status of Add Service and Start New Application
			// Button
			Map<String, String> loQueryMap = new HashMap<String, String>();
			loQueryMap.put("bussAppId", lsApplicationId);
			loQueryMap.put("asOrdId", lsOrgId);
			Channel loChannel = new Channel();
			loChannel.setData("loQueryMap", loQueryMap);
			Map<String, Object> loStatusMap = null;
			if (null != lsApplicationId)
			{
				TransactionManager.executeTransaction(loChannel, "getSupersedingandExpiry");
				if (null != loChannel.getData("statusMap"))
				{
					loStatusMap = (Map<String, Object>) loChannel.getData("statusMap");
				}
				else
				{
					loStatusMap = new HashMap<String, Object>();
				}
			}
			else
			{
				loStatusMap = new HashMap<String, Object>();
			}
			Map<String, String> loDisabledButtonMap = null;
			if (null != lsApplicationStatus)
			{
				// R5 : Added lsPermissionType for Readonly user
				loDisabledButtonMap = getButtonStatus(lsApplicationStatus, (Date) loStatusMap.get("EXPIRATION_DATE"),
						(String) loStatusMap.get("SUPERSEDING_STATUS"), lsPermissionType);
			}
			else
			{
				loDisabledButtonMap = new HashMap<String, String>();
				loDisabledButtonMap.put("addButton", "disabled='disabled'");
				loDisabledButtonMap.put("startButton", "");
			}
			boolean lbStartNewButton = false;
			boolean lbAddServiceButton = false;
			if (loDisabledButtonMap.containsKey("startButton")
					&& null != (String) loDisabledButtonMap.get("startButton")
					&& "disabled='disabled'".equalsIgnoreCase(loDisabledButtonMap.get("startButton")))
			{
				lbStartNewButton = true;
			}
			else
			{
				lbStartNewButton = false;
			}
			if (loDisabledButtonMap.containsKey("addButton") && null != (String) loDisabledButtonMap.get("addButton")
					&& "disabled='disabled'".equalsIgnoreCase(loDisabledButtonMap.get("addButton")))
			{
				lbAddServiceButton = true;
			}
			else
			{
				lbAddServiceButton = false;
			}
			// if add and application button clicked
			// value to click on the start new application button
			final String lsNewApplicationProcess = aoRequest.getParameter("newApplicationProcess");
			if (lsNewApplicationProcess != null && lsNewApplicationProcess.equalsIgnoreCase("newApplicationProcess"))
			{
				if (!lbStartNewButton)
				{
					aoResponse.setRenderParameter("startNewApplication", "startNewApplication");
					aoResponse.setRenderParameter("prevBusinessAppId", lsApplicationId);
				}
				else
				{
					aoResponse.setRenderParameter("first_action", "provider");
					aoResponse.setRenderParameter("unAuthorizedAccessError",
							"New Application can not be started currently");
				}
			}
			if (lsTermsConditions != null && lsTermsConditions.equalsIgnoreCase("createNewApplication"))
			{
				doCreateNewApplication(aoRequest, aoResponse, lsApplicationId, loPortletSession, lsOrgId, lsEmailId,
						lsUserId);
			}
			else if (loIsDisplay != null && loIsDisplay.equalsIgnoreCase("show") && lsTermsConditions != null
					&& lsTermsConditions.equalsIgnoreCase("termsAndCondition"))
			{
				if (!lbStartNewButton)
				{
					aoResponse.setRenderParameter("lsTermsConditions", lsTermsConditions);
					loPortletSession.setAttribute(ApplicationConstants.KEY_BUSINESS_APP_ID, lsApplicationId);
					aoResponse.setRenderParameter("loIsDisplay", loIsDisplay);
				}
				else
				{
					aoResponse.setRenderParameter("first_action", "provider");
					aoResponse.setRenderParameter("unAuthorizedAccessError",
							"New Application can not be started currently");
				}
			}
			else if (lsTermsConditions != null
					&& (lsTermsConditions.equalsIgnoreCase("createNewServiceApplication") || lsTermsConditions
							.equalsIgnoreCase("serviceTermsAndCondition")))
			{
				if (!lbAddServiceButton)
				{
					doCreateNewServiceApplication(aoRequest, aoResponse, lsApplicationStatus, lsApplicationId,
							loIsDisplay, loPortletSession, lsServiceId);
				}
				else
				{
					aoResponse.setRenderParameter("first_action", "provider");
					aoResponse.setRenderParameter("unAuthorizedAccessError",
							"New Service Application can not be added currently");
				}

			}
			else if (null != lsBusinessServiceApplicationId && !lsBusinessServiceApplicationId.isEmpty())
			{
				getApplicationStatus(aoRequest, aoResponse, lsApplicationType, lsApplicationStatus,
						lsBusinessServiceApplicationId, lsApplicationId, lsServiceBusinessAppId, loPortletSession,
						lsOrgId, lsServiceId, loIsReadOnly);
			}
			else
			{
				// when user want to change the status using the pop up
				if (lsApplicationType != null && !lsApplicationType.equalsIgnoreCase(""))
				{
					performCityManagerFunction(aoRequest, aoResponse, lsApplicationType, lsApplicationId,
							loUserSession, lsOrgId, lsEmailId, lsUserId, lsWorkflowId);
				}
				// this condition will be getting executed when user want to
				// show
				// history
				else if (lsViewHistory != null && !lsViewHistory.equals(""))
				{
					viewHistory(aoRequest, aoResponse, lsApplicationId, lsOrgId);
				}
			}

			long loEndTimeTime = System.currentTimeMillis();
			LOG_OBJECT.Debug("TIME TAKEN for execution of action Method in BusinessSummaryController = "
					+ (loEndTimeTime - loStartTime));
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Error while execution of action Method in BusinessSummaryController", aoEx);
			aoResponse.setRenderParameter(ApplicationConstants.RENDER_ACTION, "businessSummary");
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
					ApplicationConstants.ERROR_WHILE_PROCESSING_REQUEST);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while execution of action Method in BusinessSummaryController", aoExp);
			aoResponse.setRenderParameter(ApplicationConstants.RENDER_ACTION, "businessSummary");
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
					ApplicationConstants.ERROR_WHILE_PROCESSING_REQUEST);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
		UserThreadLocal.unSet();
	}

	/**
	 * This method displays history of application.
	 * 
	 * @param aoRequest - ActionRequest
	 * @param aoResponse - ActionResponse
	 * @param asApplicationId - Application Id
	 * @param asOrgId - Organization Id
	 * @throws ApplicationException - throws ApplicationException
	 */
	private void viewHistory(ActionRequest aoRequest, ActionResponse aoResponse, String asApplicationId, String asOrgId)
			throws ApplicationException
	{
		// channel obj
		Channel loChannel = new Channel();
		// create map to set the values
		final Map<String, Object> loActionMap = new LinkedHashMap<String, Object>();
		aoResponse.setRenderParameter("action", "businessSummary");
		final String lsHistoryType = (String) aoRequest.getParameter("historyType");
		final String lsServiceAppId = PortalUtil.parseQueryString(aoRequest, "serviceAppID");
		loActionMap.put(ApplicationConstants.ORG_ID, asOrgId);// organization id
		if (lsHistoryType != null && lsHistoryType.equalsIgnoreCase("Business"))
		{
			loActionMap.put(ApplicationConstants.APPID, asApplicationId); // business
																			// Application
																			// id
		}
		else
		{
			loActionMap.put(ApplicationConstants.APPID, lsServiceAppId); // service
																			// application
																			// id
		}
		loActionMap.put("applicationType", lsHistoryType);
		loActionMap.put("providerType", "Provider");
		loActionMap.put("cityType", "City");
		loActionMap.put("lsSnapshot", ApplicationConstants.SNAPSHOT_NAME);
		// set the data into the map
		loChannel.setData("loActionMap", loActionMap);
		// executing the transaction to display the history
		TransactionManager.executeTransaction(loChannel, "applicationSummaryViewHistory");
		aoResponse.setRenderParameter("action", "businessSummary");
		// set the param into the response
		aoResponse.setRenderParameter("displayHistory", "displayHistory");
		// set the history list into the session that will get into the render
		// view and set into the request
		ApplicationSession.setAttribute((List<ApplicationSummary>) loChannel.getData("historyList"), aoRequest,
				"displayHistoryPage");
	}

	/**
	 * This method performs City Manager Function.
	 * 
	 * @param aoRequest - ActionRequest
	 * @param aoResponse - ActionResponse
	 * @param asApplicationType - Application Type
	 * @param asApplicationId - Application Id
	 * @param aoUserSession - P8UserSession
	 * @param asOrgId - Organization Id
	 * @param asEmailId - Email Id of the user
	 * @param asUserId - User Id
	 * @param asWorkflowId - Work flow Id
	 * @throws ApplicationException - throws ApplicationException
	 */
	private void performCityManagerFunction(ActionRequest aoRequest, ActionResponse aoResponse,
			final String asApplicationType, String asApplicationId, P8UserSession aoUserSession, String asOrgId,
			final String asEmailId, String asUserId, String asWorkflowId) throws ApplicationException
	{
		// create map to set all the values for updating the table
		final Map<String, Object> loActionMap = new LinkedHashMap<String, Object>();
		// new status that is going to change
		final String lsNewStatusValue = (String) aoRequest.getParameter(ApplicationConstants.NEW_STATUS_VALUE);
		// element id of the service
		final String lsElementId = (String) aoRequest.getParameter(ApplicationConstants.SERVICE_ELEMENT);
		// comments to insert and update
		final String lsComments = (String) aoRequest.getParameter(ApplicationConstants.COMMENTS);
		final String lsServiceAppId = PortalUtil.parseQueryString(aoRequest, "serviceAppID");
		loActionMap.put(ApplicationConstants.ORG_ID, asOrgId);// set
																// organization
																// id
		loActionMap.put(ApplicationConstants.APPID, asApplicationId); // set
																		// application
																		// id
		java.util.Date loDate = new java.util.Date();
		loActionMap.put("asCreatedDate", new Timestamp(loDate.getTime()));
		loActionMap.put("asModifiedDate", new Timestamp(loDate.getTime()));
		// channel to set input date
		Channel loChannel = new Channel();
		String lsProviderName = null;
		if (null != FileNetOperationsUtils.getProviderName(FileNetOperationsUtils.getProviderList(), asOrgId))
		{
			lsProviderName = FileNetOperationsUtils.getProviderName(FileNetOperationsUtils.getProviderList(), asOrgId);
		}
		HashMap<String, Object> loHmNotifyParamService = new HashMap<String, Object>();
		HashMap<String, Object> loHmNotifyParamBusiness = new HashMap<String, Object>();
		if (null != asApplicationType && asApplicationType.equalsIgnoreCase("Service"))
		{
			doActionForServiceApp(aoRequest, asOrgId, loActionMap, lsNewStatusValue, lsServiceAppId, lsProviderName,
					loHmNotifyParamService);
		}
		else
		{
			String lsTemp = "";
			if (null != lsNewStatusValue)
			{
				lsTemp = lsNewStatusValue;
			}
			doActionForBusinessApp(aoRequest, asApplicationId, asOrgId, loActionMap, lsTemp, lsProviderName,
					loHmNotifyParamBusiness);
		}
		setValuesInMap(asApplicationType, asEmailId, asUserId, loActionMap, lsNewStatusValue, lsElementId, lsComments);
		// set the map into the channel that will go with transaction
		loChannel.setData("loActionMap", loActionMap);
		// set organization id
		loChannel.setData(ApplicationConstants.KEY_SESSION_ORG_ID, asOrgId);
		// flag passed to distinguish whether request is coming from home page
		// or service summary page
		loChannel.setData(ApplicationConstants.NAVIGATION_TYPE, ApplicationConstants.FROM_SERVICE_SUMMARY_PAGE);
		// now execute the transaction to update the status in
		// audit(insert),business application,service application and withdrawn
		TransactionManager.executeTransaction(loChannel, "updateApplicationHistoryStatus");
		// code to update the provider status on conditionally approve
		if (null != asApplicationType && asApplicationType.equalsIgnoreCase("Service"))
		{
			updateOrgForService(asApplicationId, aoUserSession, asOrgId, asEmailId, asUserId, asWorkflowId,
					lsNewStatusValue, lsServiceAppId, loChannel);
		}
		else
		{
			if (null != lsNewStatusValue && ApplicationConstants.STATUS_SUSPEND.equalsIgnoreCase(lsNewStatusValue))
			{
				updateOrgStatusForAppSuspend(asApplicationId, aoUserSession, asOrgId, asEmailId, asUserId,
						asWorkflowId, loChannel);
			}
		}
		if (null != lsNewStatusValue
				&& ApplicationConstants.STATUS_CONDITIONALLY_APPROVED.equalsIgnoreCase(lsNewStatusValue)
				&& null != asApplicationType && "Service".equalsIgnoreCase(asApplicationType))
		{
			loChannel.setData("loHmNotifyParam", loHmNotifyParamService);
			/*[Start] R9.1.0 QC9610   disabling AL008 & NT010  */
			//TransactionManager.executeTransaction(loChannel, "insertNotificationDetail");
			/*[End]  R9.1.0 QC9610    disabling AL008 & NT010 */
		}
		else if (null != lsNewStatusValue
				&& ApplicationConstants.STATUS_CONDITIONALLY_APPROVED.equalsIgnoreCase(lsNewStatusValue))
		{
		    /*[Start] R9.1.0 QC9610  disabling AL003 & NT009 */
			//loChannel.setData("loHmNotifyParam", loHmNotifyParamBusiness);
            loChannel.setData("loHmNotifyParam", new HashMap<String, Object>());
            /*[End]  R9.1.0 QC9610   disabling AL003 & NT009 */
			updateOrgStatusForAppConditionallyApproved(asApplicationId, aoUserSession, asOrgId, asEmailId, asUserId,
					asWorkflowId, loChannel, lsNewStatusValue);
		}
		aoResponse.setRenderParameter("first_action", "accelerator");
		aoResponse.setRenderParameter("action", "businessSummary");
	}

	/**
	 * This method updates Organization Status For Application Suspend.
	 * 
	 * @param asApplicationId - Application Id
	 * @param aoUserSession - P8UserSession
	 * @param asOrgId - Organization Id
	 * @param asEmailId - Email Id of the user
	 * @param asUserId - User Id
	 * @param asWorkflowId - Work flow Id
	 * @param aoChannel - Channel object
	 * @throws ApplicationException - throws ApplicationException
	 */
	private void updateOrgStatusForAppSuspend(String asApplicationId, P8UserSession aoUserSession, String asOrgId,
			final String asEmailId, String asUserId, String asWorkflowId, Channel aoChannel)
			throws ApplicationException
	{
		Map<String, Object> loOrgDetails = new HashMap<String, Object>();
		loOrgDetails.put("orgId", asOrgId);
		loOrgDetails.put("sunmittedBy", asEmailId);
		loOrgDetails.put("orgStatus", "Suspended");
		loOrgDetails.put("entityType", ApplicationConstants.ENTITY_TYPE_BUSINESS_APPLICATION);
		loOrgDetails.put("entityId", asApplicationId);
		loOrgDetails.put("event", ApplicationConstants.STATUS_SUSPEND);
		loOrgDetails.put("flag", "Y");
		loOrgDetails.put("status", ApplicationConstants.STATUS_SUSPEND);
		loOrgDetails.put("userId", asUserId);
		Date loCurrentTime = new Date(System.currentTimeMillis());
		loOrgDetails.put("timeStamp", loCurrentTime);
		aoChannel.setData("orgDetails", loOrgDetails);
		aoChannel.setData("loMapTOUpdateStatus", loOrgDetails);
		// transaction to update the provider status
		String lsData = "Status Changed To ".concat("Suspended");
		String lsEntityIdentifier = FileNetOperationsUtils.getProviderName(FileNetOperationsUtils.getProviderList(),
				asOrgId);
		CommonUtil.addAuditDataToChannel(aoChannel, asOrgId, ApplicationConstants.PROVIDER_STATUS_CHANGE,
				ApplicationConstants.STATUS_CHANGE, new Date(System.currentTimeMillis()), asEmailId, lsData,
				"Provider", asOrgId, "false", asApplicationId, "", ApplicationConstants.AUDIT_TYPE_APPLICATION);
		aoChannel.setData("EntityIdentifier", lsEntityIdentifier);
		TransactionManager.executeTransaction(aoChannel, "updateOrganizationTable");
		// Set data in channel to execute transaction for workflow termination
		// of a business suspension
		aoChannel.setData("asOrgID", asOrgId);
		aoChannel.setData("asBussAppId", asApplicationId);
		aoChannel.setData("aoFilenetSession", aoUserSession);
		aoChannel.setData("workflowId", asWorkflowId);
		aoChannel.setData("asUserID", asUserId);
		TransactionManager.executeTransaction(aoChannel, "terminateBRApplicationWorkflow");
	}

	/**
	 * This method sets Values In Map.
	 * 
	 * @param asApplicationType - Application Type
	 * @param asEmailId - Email Id of the user
	 * @param asUserId - User Id
	 * @param aoActionMap - Action Map
	 * @param asNewStatusValue - New Status Value
	 * @param asElementId - Element Id
	 * @param asComments - Comments
	 */
	private void setValuesInMap(final String asApplicationType, final String asEmailId, String asUserId,
			final Map<String, Object> aoActionMap, final String asNewStatusValue, final String asElementId,
			final String asComments)
	{
		aoActionMap.put(ApplicationConstants.EVENT, asNewStatusValue);
		aoActionMap.put(ApplicationConstants.FLAG, "Y");
		aoActionMap.put(ApplicationConstants.REQUEST_ID, null);
		aoActionMap.put(ApplicationConstants.AUDIT_DATE, new Date(System.currentTimeMillis()));
		aoActionMap.put(ApplicationConstants.USER_ID, asUserId); // user id
		aoActionMap.put(ApplicationConstants.COMMENTS, asComments);
		aoActionMap.put(ApplicationConstants.SECTIONID, "");
		aoActionMap.put(ApplicationConstants.PROVIDER_VISIBILITY_FLAG, "true");
		aoActionMap.put(ApplicationConstants.MODIFIED_BY, asUserId);
		aoActionMap.put(ApplicationConstants.MODIFIED_DATE1, new Date(System.currentTimeMillis()));
		aoActionMap.put(ApplicationConstants.STATUS, asNewStatusValue);
		aoActionMap.put(ApplicationConstants.SERVICE_ELEMENT_ID, asElementId);
		aoActionMap.put(ApplicationConstants.APPLICATION_TYPE, asApplicationType);
		aoActionMap.put(ApplicationConstants.EMAIL_ID, asEmailId);
		aoActionMap.put(ApplicationConstants.CITY_TYPE, ApplicationConstants.CITY_TYPE);
	}

	/**
	 * This method performs Action For Business Application.
	 * <ul>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * @param aoRequest - ActionRequest
	 * @param asApplicationId - Application Id
	 * @param asOrgId - Organization Id
	 * @param aoActionMap - Action Map
	 * @param asNewStatusValue - New Status Value
	 * @param asProviderName - Provider Name
	 * @param aoHmNotifyParamBusiness - Map containing data to Notify Param
	 *            Business
	 * @throws ApplicationException
	 */
	private void doActionForBusinessApp(ActionRequest aoRequest, String asApplicationId, String asOrgId,
			final Map<String, Object> aoActionMap, final String asNewStatusValue, String asProviderName,
			HashMap<String, Object> aoHmNotifyParamBusiness) throws ApplicationException
	{
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);

		aoActionMap.put(ApplicationConstants.EVENT_NAME, "Business Application ".concat(asNewStatusValue)); // set
																											// event
																											// name
																											// for
																											// audit
																											// table
		aoActionMap.put(ApplicationConstants.EVENT_TYPE, "Application "); // set
																			// event
																			// type
																			// for
																			// audit
																			// table
		aoActionMap.put(ApplicationConstants.ENTITY_TYPE, "Business Application"); // set
																					// entity
																					// type
		aoActionMap.put(ApplicationConstants.ENTITY_ID, asApplicationId); // set
																			// entity
																			// id
		aoActionMap.put(ApplicationConstants.ENTITY_IDENTIFIER, "Business Application ".concat(asNewStatusValue));

		List<String> loNotificationAlertList = new ArrayList<String>();
		loNotificationAlertList.add("NT009");
		loNotificationAlertList.add("AL003");
		// Changed in 3.1.0 . Added check for Defect 6385
		// Fix done for NT009 notification and AL003 alert -
		/*
		 * String lsURLNotificationBasicForm = aoRequest.getScheme() + "://" +
		 * aoRequest.getServerName() + ":" + aoRequest.getServerPort() +
		 * aoRequest.getContextPath() + ApplicationConstants.PORTAL_URL +
		 * "&_pageLabel=portlet_hhsweb_portal_page_business_summary&app_menu_name=header_application&first_action=provider#wlp_portlet_hhsweb_portal_page_business_summary"
		 * ;
		 */

		String lsURLNotificationBasicForm = ApplicationConstants.PORTAL_URL
				+ "&_pageLabel=portlet_hhsweb_portal_page_business_summary&app_menu_name=header_application&first_action=provider#wlp_portlet_hhsweb_portal_page_business_summary";

		List<String> loProviderIdList = new ArrayList<String>();
		loProviderIdList.add(asOrgId);
		NotificationDataBean loNotificationDataBean = new NotificationDataBean();
		HashMap<String, String> loLinkMap = new HashMap<String, String>();
		loLinkMap.put("LINK", getPortalLink(aoRequest, lsURLNotificationBasicForm).toString());
		loNotificationDataBean.setLinkMap(loLinkMap);
		loNotificationDataBean.setAgencyLinkMap(loLinkMap);
		loNotificationDataBean.setProviderList(loProviderIdList);
		aoHmNotifyParamBusiness.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
		aoHmNotifyParamBusiness.put("NT009", loNotificationDataBean);
		aoHmNotifyParamBusiness.put("AL003", loNotificationDataBean);
		aoHmNotifyParamBusiness.put(HHSConstants.CREATED_BY_USER_ID, lsUserId);
		aoHmNotifyParamBusiness.put(HHSConstants.MODIFIED_BY, lsUserId);
		aoHmNotifyParamBusiness.put(ApplicationConstants.ENTITY_TYPE, "Business Application");
		aoHmNotifyParamBusiness.put(ApplicationConstants.ENTITY_ID, asApplicationId);

		HashMap<Object, String> loParamMap = new HashMap<Object, String>();
		loParamMap.put("PROVIDER", asProviderName);
		aoHmNotifyParamBusiness.put(TransactionConstants.PROVIDER_ID, loProviderIdList);
		aoHmNotifyParamBusiness.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loParamMap);
	}

	/**
	 * This method performs Action For Service Application.
	 * <ul>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * @param aoRequest - ActionRequest
	 * @param asOrgId - Organization Id
	 * @param aoActionMap - Action Map
	 * @param asNewStatusValue - New Status Value
	 * @param asServiceAppId - Service Application Id
	 * @param asProviderName - Provider Name
	 * @param aoHmNotifyParamService - Map containing data to Notify Param
	 *            Business
	 * @throws ApplicationException
	 */
	private void doActionForServiceApp(ActionRequest aoRequest, String asOrgId, final Map<String, Object> aoActionMap,
			final String asNewStatusValue, final String asServiceAppId, String asProviderName,
			HashMap<String, Object> aoHmNotifyParamService) throws ApplicationException
	{
		aoActionMap.put(ApplicationConstants.EVENT_NAME, ApplicationConstants.AUDIT_SERVICE_APP_SUBMISSION_ENTITY_TYPE
				.concat(" ").concat(asNewStatusValue)); // set
		// event
		// name
		// for
		// audit
		// table
		aoActionMap.put(ApplicationConstants.EVENT_TYPE, "Application"); // set
																			// event
																			// type
																			// for
																			// audit
																			// table
		aoActionMap.put(ApplicationConstants.ENTITY_TYPE, ApplicationConstants.ENTITY_TYPE_SERVICE_APPLICATION); // set
		// entity
		// type
		aoActionMap.put(ApplicationConstants.ENTITY_ID, asServiceAppId); // service
																			// application
																			// id
		aoActionMap.put(ApplicationConstants.ENTITY_IDENTIFIER, ApplicationConstants.ENTITY_TYPE_SERVICE_APPLICATION
				.concat(" ").concat(asNewStatusValue));
		String lsServiceName = (String) PortalUtil.parseQueryString(aoRequest, "serviceApplicationName");
		if (lsServiceName != null && !lsServiceName.equalsIgnoreCase(""))
		{
			if (lsServiceName.indexOf(">") != -1)
			{
				lsServiceName = lsServiceName.substring(lsServiceName.lastIndexOf(">") + 1, lsServiceName.length());
			}
		}
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);

		String lsURLNotificationBasicForm = ApplicationConstants.PORTAL_URL
				+ "&_pageLabel=portlet_hhsweb_portal_page_business_summary&app_menu_name=header_application&first_action=provider#wlp_portlet_hhsweb_portal_page_business_summary";
		List<String> loProviderIdList = new ArrayList<String>();
		loProviderIdList.add(asOrgId);

		List<String> loNotificationAlertList = new ArrayList<String>();
		loNotificationAlertList.add("NT010");
		loNotificationAlertList.add("AL008");
		NotificationDataBean loNotificationDataBean = new NotificationDataBean();
		HashMap<String, String> loLinkMap = new HashMap<String, String>();
		loLinkMap.put("LINK", getPortalLink(aoRequest, lsURLNotificationBasicForm).toString());
		loNotificationDataBean.setLinkMap(loLinkMap);
		loNotificationDataBean.setAgencyLinkMap(loLinkMap);
		loNotificationDataBean.setProviderList(loProviderIdList);
		aoHmNotifyParamService.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
		aoHmNotifyParamService.put("NT010", loNotificationDataBean);
		aoHmNotifyParamService.put("AL008", loNotificationDataBean);
		aoHmNotifyParamService.put(HHSConstants.CREATED_BY_USER_ID, lsUserId);
		aoHmNotifyParamService.put(HHSConstants.MODIFIED_BY, lsUserId);
		aoHmNotifyParamService.put(ApplicationConstants.ENTITY_TYPE,
				ApplicationConstants.ENTITY_TYPE_SERVICE_APPLICATION);
		aoHmNotifyParamService.put(ApplicationConstants.ENTITY_ID, asServiceAppId);

		HashMap<Object, String> loParamMap = new HashMap<Object, String>();
		loParamMap.put("PROVIDER", asProviderName);
		loParamMap.put("SERVICE", lsServiceName);
		aoHmNotifyParamService.put(TransactionConstants.PROVIDER_ID, loProviderIdList);
		aoHmNotifyParamService.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loParamMap);
	}

	/**
	 * This method updates Organization status For Service application.
	 * 
	 * @param asApplicationId - Application Id
	 * @param aoUserSession - P8UserSession
	 * @param asOrgId - Organization Id
	 * @param asEmailId - Email Id of the user
	 * @param asUserId - User Id
	 * @param asWorkflowId - Work flow Id
	 * @param asNewStatusValue - New Status Value
	 * @param asServiceAppId - Service Application Id
	 * @param aoChannel - Channel object
	 * @throws ApplicationException - throws ApplicationException
	 */
	private void updateOrgForService(String asApplicationId, P8UserSession aoUserSession, String asOrgId,
			final String asEmailId, String asUserId, String asWorkflowId, final String asNewStatusValue,
			final String asServiceAppId, Channel aoChannel) throws ApplicationException
	{
		if (null != asNewStatusValue
				&& ApplicationConstants.STATUS_CONDITIONALLY_APPROVED.equalsIgnoreCase(asNewStatusValue))
		{
			updateOrgStatusForServiceCondApprove(asApplicationId, asOrgId, asUserId, asServiceAppId, aoChannel);
		}
		else if (null != asNewStatusValue && ApplicationConstants.STATUS_SUSPEND.equalsIgnoreCase(asNewStatusValue))
		{
			Map<String, Object> loOrgDetails = setOrgDetailsInMap(asOrgId, asEmailId, asServiceAppId, aoChannel);
			if (null != asWorkflowId && !"".equalsIgnoreCase(asWorkflowId))
			{
				// Set data in channel to execute transaction for workflow
				// termination of service suspension
				aoChannel.setData("aoFilenetSession", aoUserSession);
				aoChannel.setData("workflowId", asWorkflowId);
				TransactionManager.executeTransaction(aoChannel, "terminateSRApplicationWorkflow");
			}
			// creating the list of status
			List<String> loServiceStatusList = new ArrayList<String>();
			Map<String, String> loRequiredProps = new HashMap<String, String>();
			loRequiredProps.put("providerId", asOrgId);
			loRequiredProps.put("businessAppId", asApplicationId);
			aoChannel.setData("aoRequiredProps", loRequiredProps);
			TransactionManager.executeTransaction(aoChannel, "getBusinessAndServiceStatus");
			List<ProviderStatusBean> loProviderStatusBeanList = (List<ProviderStatusBean>) aoChannel
					.getData("statusList");
			Iterator loIterator = loProviderStatusBeanList.iterator();
			String lsBrStatus = "";
			boolean lbSelectedServiceFlag = true;
			while (loIterator.hasNext())
			{
				ProviderStatusBean loProviderStatusBean = (ProviderStatusBean) loIterator.next();
				if (loProviderStatusBean.getApplicationId().equalsIgnoreCase(asServiceAppId) && lbSelectedServiceFlag)
				{
					lbSelectedServiceFlag = false;
					if (null != loProviderStatusBean.getSupersedingStatus()
							&& !loProviderStatusBean.getSupersedingStatus().isEmpty())
					{
						loServiceStatusList.add(loProviderStatusBean.getSupersedingStatus());
					}
					else if (null != loProviderStatusBean.getApplicationStatus()
							&& !loProviderStatusBean.getApplicationStatus().isEmpty())
					{
						loServiceStatusList.add(loProviderStatusBean.getApplicationStatus());
					}
				}
				else if (!loProviderStatusBean.getApplicationId().equalsIgnoreCase(asServiceAppId)
						&& !loProviderStatusBean.getApplicationId().equalsIgnoreCase(asApplicationId))
				{
					if (null != loProviderStatusBean.getSupersedingStatus()
							&& !loProviderStatusBean.getSupersedingStatus().isEmpty())
					{
						loServiceStatusList.add(loProviderStatusBean.getSupersedingStatus());
					}
					else if (null != loProviderStatusBean.getApplicationStatus()
							&& !loProviderStatusBean.getApplicationStatus().isEmpty())
					{
						loServiceStatusList.add(loProviderStatusBean.getApplicationStatus());
					}
				}
				if (loProviderStatusBean.getApplicationId().equalsIgnoreCase(asApplicationId))
				{
					if (loProviderStatusBean.getSupersedingStatus() != null)
					{
						lsBrStatus = loProviderStatusBean.getSupersedingStatus();
					}
					else
					{
						lsBrStatus = loProviderStatusBean.getApplicationStatus();
					}
				}
			}
			String lsNewProviderStatus = ProviderStatusBusinessRules.getProviderStatusOnRemovalOfConditionalApproval(
					lsBrStatus, loServiceStatusList);
			if (lsNewProviderStatus != null && !lsNewProviderStatus.equalsIgnoreCase(""))
			{
				loOrgDetails.put("orgStatus", lsNewProviderStatus);
				loOrgDetails.put("userId", asUserId);
				aoChannel.setData("orgDetails", loOrgDetails);
				// transaction to update the provider status
				String lsData = "Status Changed To ".concat(lsNewProviderStatus);
				String lsEntityIdentifier = FileNetOperationsUtils.getProviderName(
						FileNetOperationsUtils.getProviderList(), asOrgId);
				CommonUtil.addAuditDataToChannel(aoChannel, asOrgId, ApplicationConstants.PROVIDER_STATUS_CHANGE,
						ApplicationConstants.STATUS_CHANGE, new Date(System.currentTimeMillis()), asEmailId, lsData,
						"Provider", asOrgId, "false", asApplicationId, "", ApplicationConstants.AUDIT_TYPE_APPLICATION);
				aoChannel.setData("EntityIdentifier", lsEntityIdentifier);
				TransactionManager.executeTransaction(aoChannel, "updateOrganizationTable");
			}
		}
	}

	/**
	 * This method sets organization details in map.
	 * 
	 * @param asOrgId - Organization Id
	 * @param asEmailId - Email Id
	 * @param asServiceAppId - Service Application Id
	 * @param aoChannel - Channel object
	 * @return - loOrgDetails
	 */
	private Map<String, Object> setOrgDetailsInMap(String asOrgId, final String asEmailId, final String asServiceAppId,
			Channel aoChannel)
	{
		Map<String, Object> loOrgDetails = new HashMap<String, Object>();
		loOrgDetails.put("orgId", asOrgId);
		loOrgDetails.put("sunmittedBy", asEmailId);
		loOrgDetails.put("entityType", ApplicationConstants.ENTITY_TYPE_SERVICE_APPLICATION);
		loOrgDetails.put("entityId", asServiceAppId);
		loOrgDetails.put("event", ApplicationConstants.STATUS_SUSPEND);
		loOrgDetails.put("flag", "Y");
		loOrgDetails.put("status", ApplicationConstants.STATUS_SUSPEND);
		Date loCurrentTime = new Date(System.currentTimeMillis());
		loOrgDetails.put("timeStamp", loCurrentTime);
		aoChannel.setData("loMapTOUpdateStatus", loOrgDetails);
		return loOrgDetails;
	}

	/**
	 * This method updates Organization status For Service application.
	 * 
	 * @param asApplicationId - Application Id
	 * @param asOrgId - Organization Id
	 * @param asUserId - User Id
	 * @param asServiceAppId - Service Application Id
	 * @param aoChannel - Channel object
	 * @throws ApplicationException - throws ApplicationException
	 */
	private void updateOrgStatusForServiceCondApprove(String asApplicationId, String asOrgId, String asUserId,
			final String asServiceAppId, Channel aoChannel) throws ApplicationException
	{
		String lsCurrentAppStatus = getCurrentServiceApplicationStatus(asApplicationId, asOrgId, asServiceAppId);
		Map<String, Object> loOrgDetails = new HashMap<String, Object>();
		loOrgDetails.put("orgId", asOrgId);
		loOrgDetails.put("sunmittedBy", asUserId);
		loOrgDetails.put("entityType", ApplicationConstants.ENTITY_TYPE_BUSINESS_APPLICATION);
		loOrgDetails.put("entityId", asServiceAppId);
		loOrgDetails.put("event", ApplicationConstants.STATUS_CONDITIONALLY_APPROVED);
		loOrgDetails.put("flag", "Y");
		loOrgDetails.put("status", ApplicationConstants.STATUS_CONDITIONALLY_APPROVED);
		Date loCurrentTime = new Date(System.currentTimeMillis());
		loOrgDetails.put("timeStamp", loCurrentTime);
		aoChannel.setData("loMapTOUpdateStatus", loOrgDetails);
		aoChannel.setData("orgId", asOrgId);
		// transaction to fetch the provider status
		TransactionManager.executeTransaction(aoChannel, "fetchOrganizationStatus");
		String lsCurrentProviderStatus = (String) aoChannel.getData("providerStatus");
		String lsNewProviderStatus = ProviderStatusBusinessRules.getProviderStatusOnServiceConditionalApproval(
				lsCurrentProviderStatus, lsCurrentAppStatus);
		if (lsNewProviderStatus != null && !lsNewProviderStatus.equalsIgnoreCase(""))
		{
			loOrgDetails.put("orgStatus", lsNewProviderStatus);
			loOrgDetails.put("userId", asUserId);
			aoChannel.setData("orgDetails", loOrgDetails);
			// transaction to update the provider status
			String lsData = "Status Changed To ".concat(lsNewProviderStatus);
			String lsEntityIdentifier = FileNetOperationsUtils.getProviderName(
					FileNetOperationsUtils.getProviderList(), asOrgId);
			CommonUtil.addAuditDataToChannel(aoChannel, asOrgId, ApplicationConstants.PROVIDER_STATUS_CHANGE,
					ApplicationConstants.STATUS_CHANGE, new Date(System.currentTimeMillis()), asUserId, lsData,
					"Provider", asOrgId, "false", asApplicationId, "", ApplicationConstants.AUDIT_TYPE_APPLICATION);
			aoChannel.setData("EntityIdentifier", lsEntityIdentifier);
			TransactionManager.executeTransaction(aoChannel, "updateOrganizationTable");
		}
	}

	/**
	 * This method gets Application Status.
	 * 
	 * @param aoRequest - ActionRequest
	 * @param aoResponse - ActionResponse
	 * @param asApplicationType - Application Type
	 * @param asApplicationStatus - Application Status
	 * @param asBusinessServiceApplicationId - Business Service Application Id
	 * @param asApplicationId - Application Id
	 * @param asServiceBusinessAppId - Service Business App Id
	 * @param aoPortletSession - PortletSession
	 * @param asOrgId - Organization Id
	 * @param asServiceId - Service Application Id
	 * @param aoIsReadOnly - Read Only flag
	 * @throws ApplicationException - throws ApplicationException
	 */
	private void getApplicationStatus(ActionRequest aoRequest, ActionResponse aoResponse,
			final String asApplicationType, String asApplicationStatus, final String asBusinessServiceApplicationId,
			String asApplicationId, String asServiceBusinessAppId, PortletSession aoPortletSession, String asOrgId,
			String asServiceId, Boolean aoIsReadOnly) throws ApplicationException
	{
		String lsServiceSummaryStatus = "";
		StringBuffer lsUrl = new StringBuffer();
		Channel loChannel = new Channel();// br id asServiceBusinessAppId
		if (asServiceBusinessAppId != null && asServiceBusinessAppId.startsWith("br_"))
		{
			loChannel.setData(ApplicationConstants.APP_ID, asServiceBusinessAppId);
		}
		else
		{
			loChannel.setData(ApplicationConstants.APP_ID, asBusinessServiceApplicationId);
		}
		loChannel.setData(ApplicationConstants.ORG_ID, asOrgId);
		TransactionManager.executeTransaction(loChannel, "getCompleteStatusMap");
		Map<String, StatusBean> loBusinessStatusBeanMap = (Map<String, StatusBean>) loChannel
				.getData("loBusinessStatusBeanMap");
		Map<String, StatusBean> loServicesStatusBeanMap = (Map<String, StatusBean>) loChannel
				.getData("loServiceStatusBeanMap");
		Boolean lbApplicationStatus = ((Map<String, Boolean>) loChannel.getData("applicationStatus"))
				.get("completeStatus");
		Collection<StatusBean> loStatusBean = loServicesStatusBeanMap.values();
		Iterator<StatusBean> loIterator = loStatusBean.iterator();
		lsServiceSummaryStatus = getServiceStatus(lsServiceSummaryStatus, loIterator);
		if (!(lbApplicationStatus && asApplicationType.equalsIgnoreCase("service"))
				&& (lbApplicationStatus || (asApplicationStatus
						.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS) || asApplicationStatus
						.equalsIgnoreCase(ApplicationConstants.STATUS_DEFFERED))))
		{
			if (asApplicationStatus != null
					&& (asApplicationStatus.equalsIgnoreCase(ApplicationConstants.STATUS_DRAFT)
							|| asApplicationStatus.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS) || asApplicationStatus
								.equalsIgnoreCase(ApplicationConstants.STATUS_DEFFERED)))
			{
				String lsProviderStatus = "";
				TransactionManager.executeTransaction(loChannel, "getProviderStatusFlag");
				WithdrawRequestDetails loWithdrawRequestDetails = (WithdrawRequestDetails) loChannel
						.getData("providerStatus");
				if (loWithdrawRequestDetails != null)
				{// expired, not applied, withdrawn, approved, conditionally
					// approved, Rejected
					lsProviderStatus = loWithdrawRequestDetails.getMsProviderStatus();
					if (lsProviderStatus != null
							&& !(lsProviderStatus.equalsIgnoreCase(ApplicationConstants.STATUS_EXPIRED)
									|| lsProviderStatus.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED)
									|| lsProviderStatus.equalsIgnoreCase(ApplicationConstants.STATUS_DEFFERED)
									|| lsProviderStatus.equalsIgnoreCase(ApplicationConstants.STATUS_NOT_APPLIED)
									|| lsProviderStatus.equalsIgnoreCase(ApplicationConstants.STATUS_IN_REVIEW)
									|| lsProviderStatus.equalsIgnoreCase(ApplicationConstants.STATUS_WITHDRAWN)
									|| lsProviderStatus.equalsIgnoreCase(ApplicationConstants.STATUS_SUSPEND)
									|| lsProviderStatus
											.equalsIgnoreCase(ApplicationConstants.STATUS_CONDITIONALLY_APPROVED) || lsProviderStatus
										.equalsIgnoreCase(ApplicationConstants.STATUS_REJECTED)))
					{
						lbApplicationStatus = false;
					}
					else
					{
						lbApplicationStatus = true;
					}
				}
			}
			else
			{
				lbApplicationStatus = false;
			}
		}
		if (lsServiceSummaryStatus.equalsIgnoreCase(""))
		{
			lbApplicationStatus = false;
			lsServiceSummaryStatus = "notstarted";
		}
		aoPortletSession.setAttribute("applicationStatus", lbApplicationStatus, PortletSession.APPLICATION_SCOPE);
		aoPortletSession.setAttribute("loReadOnlyStatus", asApplicationStatus, PortletSession.APPLICATION_SCOPE);
		aoPortletSession.setAttribute("applicationId", asApplicationId, PortletSession.APPLICATION_SCOPE);
		String lsOverwriteStatus = PortalUtil.parseQueryString(aoRequest, "overwriteStatus");
		if (asApplicationType != null && "business".equalsIgnoreCase(asApplicationType))
		{
			if (ApplicationConstants.FINAL_VIEW_STATUSES.contains(asApplicationStatus.toLowerCase())
					|| loBusinessStatusBeanMap.get(ApplicationConstants.BUSINESS_APPLICATION_SECTION_BASICS)
							.getMsSectionStatusToDisplay().equals(ApplicationConstants.STATUS_WITHDRAWN)
					|| loBusinessStatusBeanMap.get(ApplicationConstants.BUSINESS_APPLICATION_SECTION_BASICS)
							.getMsSectionStatusToDisplay().equals(ApplicationConstants.STATUS_SUSPEND))
			{
				ApplicationSession.setAttribute("business", aoRequest, "finalView");
				aoPortletSession.setAttribute("applicationId", asApplicationId, PortletSession.APPLICATION_SCOPE);
				aoPortletSession.setAttribute("bussAppId", asBusinessServiceApplicationId,
						PortletSession.APPLICATION_SCOPE);
				aoResponse.setRenderParameter("action", "businessSummary");
			}
			else if (ApplicationConstants.BUSINESS_APP_SUMMARY_STATUSES.contains(asApplicationStatus.toLowerCase()))
			{
				lsUrl.append(aoRequest.getContextPath()
						+ "/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_section&_nfls=false&app_menu_name=header_application&section=businessapplicationsummary&subsection=applicationsummary&next_action=showquestion&"
						+ ApplicationConstants.KEY_BUSINESS_APP_ID + "=" + asBusinessServiceApplicationId
						+ "&bussAppStatus=" + asApplicationStatus + "&serviceSummaryStatus=" + lsServiceSummaryStatus
						+ "&applicationType=" + asApplicationType + "&loReadOnly=" + aoIsReadOnly + "&applicationId="
						+ asApplicationId);
			}
			else
			{
				if (lsOverwriteStatus != null
						&& lsOverwriteStatus.equalsIgnoreCase(ApplicationConstants.STATUS_SUSPEND))
				{
					asApplicationStatus = lsOverwriteStatus;
				}
				lsUrl.append(aoRequest.getContextPath()
						+ "/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_section&_nfls=false&displayBrReadOnly=trueapp_menu_name=header_application&section=basics&subsection=questions&next_action=showquestion&"
						+ ApplicationConstants.KEY_BUSINESS_APP_ID + "=" + asBusinessServiceApplicationId
						+ "&bussAppStatus=" + asApplicationStatus + "&applicationType=" + asApplicationType
						+ "&loReadOnly=" + aoIsReadOnly + "&applicationId=" + asApplicationId);
			}
		}
		else if (asApplicationType != null && "service".equalsIgnoreCase(asApplicationType))
		{
			if (asServiceId != null
					&& (ApplicationConstants.FINAL_VIEW_STATUSES.contains(asApplicationStatus.toLowerCase())
							|| loServicesStatusBeanMap.get(asServiceId).getMsSectionStatusToDisplay()
									.equals(ApplicationConstants.STATUS_WITHDRAWN) || loServicesStatusBeanMap
							.get(asServiceId).getMsSectionStatusToDisplay().equals(ApplicationConstants.STATUS_SUSPEND)))
			{
				ApplicationSession.setAttribute("service", aoRequest, "finalView");
				aoPortletSession.setAttribute("service_app_id", asServiceId, PortletSession.APPLICATION_SCOPE);
				aoPortletSession.setAttribute("applicationId", asApplicationId, PortletSession.APPLICATION_SCOPE);
				aoPortletSession.setAttribute("bussAppId", asServiceBusinessAppId, PortletSession.APPLICATION_SCOPE);
				aoResponse.setRenderParameter("elementId", asBusinessServiceApplicationId);
				aoResponse.setRenderParameter("action", "businessSummary");
			}
			else
			{
				if (lsOverwriteStatus != null
						&& lsOverwriteStatus.equalsIgnoreCase(ApplicationConstants.STATUS_SUSPEND))
				{
					asApplicationStatus = lsOverwriteStatus;
				}
				lsUrl.append(aoRequest.getContextPath()
						+ "/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_service_post&_nfls=false&section=servicessummary&subsection=summary&next_action=checkForService&headerPostSubmitionService=true&business_app_id="
						+ asServiceBusinessAppId + "&elementId=" + asBusinessServiceApplicationId + "&bussAppStatus="
						+ asApplicationStatus + "&loReadOnly=" + aoIsReadOnly + "&applicationId=" + asApplicationId
						+ "&applicationType=" + asApplicationType + "&displayHistory=displayHistory");

				if (asServiceId != null)
				{
					lsUrl.append("&service_app_id=").append(asServiceId);
				}
			}
		}
		try
		{
			if (lsUrl != null && lsUrl.length() > 0)
			{
				aoResponse.sendRedirect(lsUrl.toString());
			}
		}
		catch (IOException loEx)
		{
			LOG_OBJECT.Error("Error occured while getting application status", loEx);
		}
	}

	/**
	 * This method Creates New Service Application
	 * 
	 * @param aoRequest - ActionRequest
	 * @param aoResponse - ActionResponse
	 * @param asApplicationStatus - Application Status
	 * @param asBusinessAppId - Application Id
	 * @param aoIsDisplay - flag for terms and condition page.
	 * @param aoPortletSession - PortletSession
	 * @param asServiceId - Service Application Id
	 */
	private void doCreateNewServiceApplication(ActionRequest aoRequest, ActionResponse aoResponse,
			String asApplicationStatus, String asBusinessAppId, final String aoIsDisplay,
			PortletSession aoPortletSession, String asServiceId)
	{
		if (asBusinessAppId == null || asBusinessAppId.equalsIgnoreCase(""))
		{
			asBusinessAppId = (String) aoPortletSession.getAttribute(ApplicationConstants.KEY_BUSINESS_APP_ID);
		}
		String lsNewAppIdForServiceApplication = aoRequest.getParameter("newAppIdForServiceApplication");

		aoPortletSession.setAttribute(ApplicationConstants.KEY_BUSINESS_APP_ID, asBusinessAppId);
		aoPortletSession.setAttribute("applicationId", asBusinessAppId);
		StringBuffer lsUrl = new StringBuffer();
		lsUrl.append(aoRequest.getContextPath()
				+ "/portal/hhsweb.portal?action=1&&_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_add_service&_nfls=false&section=servicessummary&subsection=summary&next_action=showServices&createNewServiceApp=true&headerPostSubmitionService=true&service_app_id="
				+ asServiceId + "&" + ApplicationConstants.KEY_BUSINESS_APP_ID + "=" + asBusinessAppId
				+ "&loIsDisplay=" + aoIsDisplay);
		if (lsNewAppIdForServiceApplication != null)
		{
			lsUrl.append("&applicationId=").append(lsNewAppIdForServiceApplication);
			asApplicationStatus = ApplicationConstants.DRAFT_STATE;
		}
		try
		{
			if (lsUrl != null)
			{
				lsUrl.append("&bussAppStatus=").append(asApplicationStatus);
			}
			aoResponse.sendRedirect(lsUrl.toString());
		}
		catch (IOException loEx)
		{
			LOG_OBJECT.Error("Error occured while creating new service application ", loEx);
		}
	}

	/**
	 * This method Creates New Business Application.
	 * 
	 * @param aoRequest - ActionRequest
	 * @param aoResponse - ActionResponse
	 * @param asApplicationId - Application Id
	 * @param aoPortletSession - PortletSession
	 * @param asOrgId - Organization Id
	 * @param asEmailId - Email Id of the user
	 * @param asUserId - User Id
	 * @throws ApplicationException - throws ApplicationException
	 */
	private void doCreateNewApplication(ActionRequest aoRequest, ActionResponse aoResponse, String asApplicationId,
			PortletSession aoPortletSession, String asOrgId, final String asEmailId, String asUserId)
			throws ApplicationException
	{
		String lsBusinessAppId = BusinessApplicationUtil.generateBusinessAppId();
		String lsNewAppId = BusinessApplicationUtil.generatAppId();
		Channel loChannelobj = new Channel();
		loChannelobj.setData(ApplicationConstants.APPID, lsNewAppId);
		loChannelobj.setData(ApplicationConstants.USER_ID, asUserId);
		loChannelobj.setData(ApplicationConstants.BUSINESS_APP_ID, lsBusinessAppId);
		loChannelobj.setData(ApplicationConstants.STATUS, ApplicationConstants.START_STATUS);
		loChannelobj.setData(ApplicationConstants.ORG_ID, asOrgId);
		loChannelobj.setData("asPrevBusinessAppId", asApplicationId);
		// Set data in channel to Add entry in Audit table on Terms and
		// Condition Selection for create new accelerator application
		CommonUtil.addAuditDataToChannel(loChannelobj, asOrgId, ApplicationConstants.AUDIT_TERMSNCONDITIONS_EVENT_NAME,
				ApplicationConstants.AUDIT_TERMSNCONDITIONS_EVENT_TYPE, new Date(System.currentTimeMillis()),
				asEmailId, ApplicationConstants.AUDIT_TERMSNCONDITIONS_EVENT_DATA + " : Application",
				ApplicationConstants.AUDIT_TERMSNCONDITIONS_ENTITY_TYPE, lsBusinessAppId,
				ApplicationConstants.AUDIT_APP_SUBMISSION_PROVIDER_FLAG_TRUE, lsBusinessAppId, lsBusinessAppId,
				ApplicationConstants.AUDIT_TYPE_APPLICATION);
		loChannelobj.setData("EntityIdentifier", ApplicationConstants.AUDIT_TERMSNCONDITIONS_ENTITY_TYPE);
		TransactionManager.executeTransaction(loChannelobj, "createNewBusinessApplication");

		if (loChannelobj.getData("applicationStatusMap") != null)
		{
			Map<String, String> loApplicationStatusMap = (Map<String, String>) loChannelobj
					.getData("applicationStatusMap");
			String lsAppExistingStatus = loApplicationStatusMap.get("appExistingStatus");
			if (lsAppExistingStatus.equalsIgnoreCase("yes"))
			{
				aoResponse.setRenderParameter("first_action", "provider");
				aoResponse.setRenderParameter("appExistingStatus", "yes");
			}
			else
			{
				aoPortletSession.setAttribute("applicationId", lsNewAppId, PortletSession.APPLICATION_SCOPE);
				aoPortletSession.setAttribute("bussAppStatus", ApplicationConstants.STATUS_DRAFT,
						PortletSession.APPLICATION_SCOPE);
				aoPortletSession.setAttribute(ApplicationConstants.KEY_SESSION_APP_ID, lsNewAppId,
						PortletSession.APPLICATION_SCOPE);
				String lsUrl = aoRequest.getContextPath()
						+ "/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_section&_nfls=false&app_menu_name=header_application&section=basics&subsection=questions&next_action=showquestion&"
						+ ApplicationConstants.KEY_BUSINESS_APP_ID + "=" + lsBusinessAppId + "&bussAppStatus="
						+ ApplicationConstants.STATUS_DRAFT;
				try
				{
					aoResponse.sendRedirect(lsUrl);
				}
				catch (IOException loEx)
				{
					LOG_OBJECT.Error("Error occured while creating new application ", loEx);
				}
			}

		}
		else
		{
			throw new ApplicationException("Unable to insert data to create new Business Application ");
		}
	}

	/**
	 * This method is used to read the content from the file net.
	 * 
	 * @param aoRequest - RenderRequest
	 * @param asChannelInput - input for the channel
	 * @throws - IOException
	 */
	private String getTermsAndCondition(RenderRequest aoRequest, String asChannelInput) throws IOException
	{
		Channel loChannel = new Channel();
		P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);

		loChannel.setData("aoUserSession", loUserSession);
		loChannel.setData(HHSR5Constants.AS_DOC_TYPE, asChannelInput);
		try
		{
			// Execute the transaction to get Document Content By Type
			TransactionManager.executeTransaction(loChannel, "getDocumentContentByType");
		}
		catch (ApplicationException loEx)
		{
			LOG_OBJECT.Error("Error occured while getting terms and conditions", loEx);
		}
		HashMap<String, InputStream> loIoMap = (HashMap<String, InputStream>) loChannel.getData("contentByType");
		String lsSystemTermsAndCond = "";
		if (!loIoMap.keySet().isEmpty())
		{
			Writer loWriter = new StringWriter();
			char[] loCharArr = new char[1024];
			Reader loReader = null;
			String lsMapKey = loIoMap.keySet().iterator().next();
			try
			{
				loReader = new BufferedReader(new InputStreamReader(loIoMap.get(lsMapKey)));
				int liTempVar;
				while ((liTempVar = loReader.read(loCharArr)) != -1)
				{
					loWriter.write(loCharArr, 0, liTempVar);
				}
				lsSystemTermsAndCond = loWriter.toString();
			}
			finally
			{
				loIoMap.get(lsMapKey).close();
				try
				{
					if (loReader != null)
					{
						loReader.close();
					}
					if (loWriter != null)
					{
						loWriter.close();
					}
				}
				catch (IOException aoExp)
				{
					LOG_OBJECT.Error("Error occured while getting terms and conditions", aoExp);
				}
			}
		}
		return lsSystemTermsAndCond;
	}

	/**
	 * this method gets Current Application Status.
	 * 
	 * @param lsApplicationId - application id
	 * @param lsOrgId - organization id
	 * @return - lsBrStatus
	 * @throws ApplicationException - throws ApplicationException
	 */
	public String getCurrentApplicationStatus(String asApplicationId, String asOrgId) throws ApplicationException
	{
		Channel loChannel = new Channel();
		Map<String, String> loRequiredProps = new HashMap<String, String>();
		loRequiredProps.put("providerId", asOrgId);
		loRequiredProps.put("businessAppId", asApplicationId);
		loChannel.setData("aoRequiredProps", loRequiredProps);
		TransactionManager.executeTransaction(loChannel, "getBusinessAndServiceStatus");
		List<ProviderStatusBean> loProviderStatusBeanList = (List<ProviderStatusBean>) loChannel.getData("statusList");
		Iterator loIterator = loProviderStatusBeanList.iterator();
		String lsBrStatus = "";
		while (loIterator.hasNext())
		{
			ProviderStatusBean loProviderStatusBean = (ProviderStatusBean) loIterator.next();
			if (loProviderStatusBean.getApplicationId().equalsIgnoreCase(asApplicationId))
			{
				if (loProviderStatusBean.getSupersedingStatus() != null)
				{
					lsBrStatus = loProviderStatusBean.getSupersedingStatus();
				}
				else
				{
					lsBrStatus = loProviderStatusBean.getApplicationStatus();
				}
				return lsBrStatus;
			}

		}
		return null;
	}

	/**
	 * this method gets Current Service Application Status.
	 * 
	 * @param lsApplicationId - application id
	 * @param lsOrgId - organization id
	 * @param lsServiceApplicationId - Service Application Id
	 * @return - lsServiceStatus
	 * @throws ApplicationException - throws ApplicationException
	 */
	public String getCurrentServiceApplicationStatus(String asApplicationId, String asOrgId,
			String asServiceApplicationId) throws ApplicationException
	{
		Channel loChannel = new Channel();
		Map<String, String> loRequiredProps = new HashMap<String, String>();
		loRequiredProps.put("providerId", asOrgId);
		loRequiredProps.put("businessAppId", asApplicationId);
		loChannel.setData("aoRequiredProps", loRequiredProps);
		TransactionManager.executeTransaction(loChannel, "getBusinessAndServiceStatus");
		List<ProviderStatusBean> loProviderStatusBeanList = (List<ProviderStatusBean>) loChannel.getData("statusList");
		Iterator loIterator = loProviderStatusBeanList.iterator();
		String lsServiceStatus = "";
		while (loIterator.hasNext())
		{
			ProviderStatusBean loProviderStatusBean = (ProviderStatusBean) loIterator.next();
			if (loProviderStatusBean.getApplicationId().equalsIgnoreCase(asServiceApplicationId))
			{
				if (loProviderStatusBean.getSupersedingStatus() != null)
				{
					lsServiceStatus = loProviderStatusBean.getSupersedingStatus();
				}
				else
				{
					lsServiceStatus = loProviderStatusBean.getApplicationStatus();
				}
				return lsServiceStatus;
			}
		}
		return null;
	}

	/**
	 * This Method generates the URL for notifications
	 * 
	 * @param aoRequest Action Request
	 * @param asRedirectLink Redirect URL for Notification/Alert
	 * @return lsBfApplicationUrl
	 * @throws ApplicationException
	 */
	private StringBuffer getPortalLink(ActionRequest aoRequest, String asRedirectLink) throws ApplicationException
	{
		StringBuffer lsBfApplicationUrl = new StringBuffer();
		lsBfApplicationUrl.append(aoRequest.getScheme());
		lsBfApplicationUrl.append("://");
		lsBfApplicationUrl.append(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
				"SERVER_NAME_FOR_PROVIDER_BATCH"));
		lsBfApplicationUrl.append(":");
		lsBfApplicationUrl.append(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
				"SERVER_PORT_FOR_PROVIDER_BATCH"));
		lsBfApplicationUrl.append("/");
		lsBfApplicationUrl.append(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
				"CONTEXT_PATH_FOR_PROVIDER_BATCH"));
		lsBfApplicationUrl.append(asRedirectLink);
		return lsBfApplicationUrl;
	}

	/**
	 * This method calculates the service status.
	 * 
	 * @param asServiceSummaryStatus - Service Summary Status
	 * @param aoIterator - Iterator reference
	 * @return asServiceSummaryStatus - Service Summary Status
	 */
	private String getServiceStatus(String asServiceSummaryStatus, Iterator<StatusBean> aoIterator)
	{
		boolean lbExitFlag = false;
		while (aoIterator.hasNext())
		{
			StatusBean loStatusBeanObj = (StatusBean) aoIterator.next();
			if (loStatusBeanObj.getMsSectionStatusToDisplay() != null
					&& loStatusBeanObj.getMsSectionStatusToDisplay().equalsIgnoreCase(
							ApplicationConstants.STATUS_CONDITIONALLY_APPROVED))
			{
				Map<String, String> loSubSectionMap = loStatusBeanObj.getMoHMSubSectionDetailsToDisplay();
				Collection loCollectionRef = loSubSectionMap.values();
				// obtain an Iterator for Collection
				Iterator loItr = loCollectionRef.iterator();
				// iterate through HashMap values iterator
				while (loItr.hasNext())
				{
					String lsSubSecValue = (String) loItr.next();
					if (lsSubSecValue != null
							&& lsSubSecValue.equalsIgnoreCase(ApplicationConstants.STATUS_NOT_STARTED))
					{
						if (asServiceSummaryStatus != null
								&& asServiceSummaryStatus.equalsIgnoreCase(ApplicationConstants.COMPLETED_STATE))
						{
							asServiceSummaryStatus = ApplicationConstants.DRAFT_STATE.toLowerCase();
							lbExitFlag = true;
							break;
						}
						else
						{
							asServiceSummaryStatus = ApplicationConstants.NOT_STARTED_STATE.toLowerCase().replaceAll(
									" ", "");
						}
					}
					else if (lsSubSecValue != null
							&& lsSubSecValue.equalsIgnoreCase(ApplicationConstants.COMPLETED_STATE))
					{
						if (asServiceSummaryStatus != null
								&& asServiceSummaryStatus.equalsIgnoreCase(ApplicationConstants.NOT_STARTED_STATE
										.toLowerCase().replaceAll(" ", "")))
						{
							asServiceSummaryStatus = ApplicationConstants.DRAFT_STATE.toLowerCase();
							lbExitFlag = true;
							break;
						}
						else
						{
							asServiceSummaryStatus = ApplicationConstants.COMPLETED_STATE.toLowerCase();
						}
					}
				}
				if (lbExitFlag)
				{
					break;
				}
			}
			else
			{
				if (loStatusBeanObj.getMsSectionStatusToDisplay() != null
						&& loStatusBeanObj.getMsSectionStatusToDisplay().equalsIgnoreCase(
								ApplicationConstants.PARTIALLY_COMPLETE_STATE))
				{
					asServiceSummaryStatus = ApplicationConstants.DRAFT_STATE.toLowerCase();
					break;
				}
				else if (loStatusBeanObj.getMsSectionStatusToDisplay() != null
						&& loStatusBeanObj.getMsSectionStatusToDisplay().equalsIgnoreCase(
								ApplicationConstants.STATUS_NOT_STARTED))
				{
					if (asServiceSummaryStatus != null
							&& asServiceSummaryStatus.equalsIgnoreCase(ApplicationConstants.COMPLETED_STATE))
					{
						asServiceSummaryStatus = ApplicationConstants.DRAFT_STATE.toLowerCase();
						break;
					}
					else
					{
						asServiceSummaryStatus = ApplicationConstants.DRAFT_STATE.toLowerCase();
						break;
					}
				}
				else if (loStatusBeanObj.getMsSectionStatusToDisplay() != null
						&& loStatusBeanObj.getMsSectionStatusToDisplay().equalsIgnoreCase(
								ApplicationConstants.COMPLETED_STATE))
				{
					if (asServiceSummaryStatus != null
							&& asServiceSummaryStatus.equalsIgnoreCase(ApplicationConstants.NOT_STARTED_STATE
									.replaceAll(" ", "")))
					{
						asServiceSummaryStatus = ApplicationConstants.DRAFT_STATE.toLowerCase();
						break;
					}
					else
					{
						asServiceSummaryStatus = ApplicationConstants.COMPLETED_STATE.toLowerCase();
					}
				}
				else if (loStatusBeanObj.getMsSectionStatusToDisplay() != null
						&& loStatusBeanObj.getMsSectionStatusToDisplay().equalsIgnoreCase(
								ApplicationConstants.DEACTIVATED))
				{
					if (loStatusBeanObj.getMsSectionStatusToDisplay() != null
							&& loStatusBeanObj.getMsSectionStatus().equalsIgnoreCase(ApplicationConstants.DRAFT_STATE))
					{
						asServiceSummaryStatus = ApplicationConstants.DRAFT_STATE.toLowerCase();
						break;
					}
					else if (loStatusBeanObj.getMsSectionStatusToDisplay() != null
							&& loStatusBeanObj.getMsSectionStatus().equalsIgnoreCase("notstarted"))
					{
						if (asServiceSummaryStatus != null
								&& asServiceSummaryStatus.equalsIgnoreCase(ApplicationConstants.COMPLETED_STATE))
						{
							asServiceSummaryStatus = ApplicationConstants.DRAFT_STATE.toLowerCase();
							break;
						}
						else
						{
							asServiceSummaryStatus = ApplicationConstants.NOT_STARTED_STATE.toLowerCase().replaceAll(
									" ", "");
						}
					}
					else if (loStatusBeanObj.getMsSectionStatusToDisplay() != null
							&& loStatusBeanObj.getMsSectionStatus().equalsIgnoreCase(
									ApplicationConstants.COMPLETED_STATE))
					{
						if (asServiceSummaryStatus != null
								&& asServiceSummaryStatus.equalsIgnoreCase(ApplicationConstants.NOT_STARTED_STATE
										.replaceAll(" ", "")))
						{
							asServiceSummaryStatus = ApplicationConstants.DRAFT_STATE.toLowerCase();
							break;
						}
						else
						{
							asServiceSummaryStatus = ApplicationConstants.COMPLETED_STATE.toLowerCase();
						}
					}
				}
			}
		}
		return asServiceSummaryStatus;
	}
	/**
	 * This method checks if the entered string is Numeric or not.
	 * 
	 * @param str - String
	 * @throws ApplicationException - throws ApplicationException
	 * @return Boolean 
	 */
	private Boolean isNumeric(String str)
	{
		return str.matches("-?\\d+(\\.\\d+)?");
	}

	/**
	 * This method updates Organization Status For Application Suspend.
	 * 
	 * @param asApplicationId - Application Id
	 * @param aoUserSession - P8UserSession
	 * @param asOrgId - Organization Id
	 * @param asEmailId - Email Id of the user
	 * @param asUserId - User Id
	 * @param asWorkflowId - Work flow Id
	 * @param aoChannel - Channel object
	 * @throws ApplicationException - throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	private void updateOrgStatusForAppConditionallyApproved(String asApplicationId, P8UserSession aoUserSession,
			String asOrgId, final String asEmailId, String asUserId, String asWorkflowId, Channel aoChannel,
			String asNewStatusValue) throws ApplicationException
	{
		Map<String, Object> loOrgDetails = new HashMap<String, Object>();
		ArrayList<ApplicationSummary> loApplicationSummaryList = null;
		String lsNewProviderStatus = "";

		// calculate provider status - start
		aoChannel.setData("asBrAppId", asApplicationId);
		aoChannel.setData("asOrgId", asOrgId);
		TransactionManager.executeTransaction(aoChannel, "selectAppInfoByOrgId");
		loApplicationSummaryList = (ArrayList<ApplicationSummary>) aoChannel.getData("loApplicationSummaryList");

		lsNewProviderStatus = ProviderStatusBusinessRules.calculateStatusForConditionallyApprovedApplication(
				asNewStatusValue, loApplicationSummaryList, asApplicationId);
		// calculate provider status - end

		loOrgDetails.put("orgId", asOrgId);
		loOrgDetails.put("sunmittedBy", asEmailId);
		loOrgDetails.put("orgStatus", lsNewProviderStatus);
		loOrgDetails.put("entityType", ApplicationConstants.ENTITY_TYPE_BUSINESS_APPLICATION);
		loOrgDetails.put("entityId", asApplicationId);
		loOrgDetails.put("event", ApplicationConstants.STATUS_CONDITIONALLY_APPROVED);
		loOrgDetails.put("flag", "Y");
		loOrgDetails.put("status", lsNewProviderStatus);
		loOrgDetails.put("userId", asUserId);
		Date loCurrentTime = new Date(System.currentTimeMillis());
		loOrgDetails.put("timeStamp", loCurrentTime);
		aoChannel.setData("orgDetails", loOrgDetails);
		aoChannel.setData("loMapTOUpdateStatus", loOrgDetails);
		// transaction to update the provider status
		String lsData = "Status Changed To ".concat(lsNewProviderStatus);
		String lsEntityIdentifier = FileNetOperationsUtils.getProviderName(FileNetOperationsUtils.getProviderList(),
				asOrgId);
		// Defect #6201 Fix: Blocking Audit entry in case if New Provider Status
		// is empty or null
		if (null != lsNewProviderStatus && !lsNewProviderStatus.isEmpty())
		{
			CommonUtil.addAuditDataToChannel(aoChannel, asOrgId, ApplicationConstants.PROVIDER_STATUS_CHANGE,
					ApplicationConstants.STATUS_CHANGE, new Date(System.currentTimeMillis()), asEmailId, lsData,
					"Provider", asOrgId, "false", asApplicationId, "", ApplicationConstants.AUDIT_TYPE_APPLICATION);
			aoChannel.setData("EntityIdentifier", lsEntityIdentifier);
		}
		TransactionManager.executeTransaction(aoChannel, "updateProviderStatusInOrganizationTable");
	}
}
