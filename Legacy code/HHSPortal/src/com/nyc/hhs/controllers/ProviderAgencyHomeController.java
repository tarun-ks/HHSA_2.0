package com.nyc.hhs.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.mvc.AbstractController;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.ProviderBean;
import com.nyc.hhs.model.UserBean;
import com.nyc.hhs.model.UserThreadLocal;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.ApplicationSession;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.FileNetOperationsUtils;
import com.nyc.hhs.util.PortalUtil;

/**
 * ProviderAgencyHomeController cover the Organization Basics and Documents
 * shared within the HHS Accelerator system for provider and agency.
 * 
 */
public class ProviderAgencyHomeController extends AbstractController
{

	/**
	 * This method is used to handle all the render operation for the provider
	 * and agency portlets
	 * 
	 * @param aoRequest request object
	 * @param aoResponse response object
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(ProviderAgencyHomeController.class);

	@Override
	protected ModelAndView handleRenderRequestInternal(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		long loStartTime = System.currentTimeMillis();
		String lsHomeJSPName = "";
		ModelAndView loModelAndView = null;
		PortletSession loPortletSessionThread = aoRequest.getPortletSession();
		String lsUserIdThreadLocal = (String) loPortletSessionThread.getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
		UserThreadLocal.setUser(lsUserIdThreadLocal);
		PortletSession loPortletSession = aoRequest.getPortletSession();
		String lsAction = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.BUSINESS_APPLICATION_ACTION);
		String lsOrgId = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
				PortletSession.APPLICATION_SCOPE);
		String lsOrgType = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
				PortletSession.APPLICATION_SCOPE);

		// Added in release 5
		if (null != lsOrgType && lsOrgType.equalsIgnoreCase(HHSR5Constants.USER_CITY))
		{
			lsOrgId = lsOrgType;
		} 

		// Added in release 5
		aoRequest.getPortletSession().removeAttribute("cityUserSearchProviderId", PortletSession.APPLICATION_SCOPE);
		Map<String, Object> loMapForRender = new HashMap<String, Object>();
		P8UserSession loUserSession = (P8UserSession) loPortletSession.getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);

		try
		{
	        
			// Start QC 8998 R 8.8  remove password from logs
			String param = CommonUtil.maskPassword(loUserSession);
        	// End QC 8998 R 8.8  remove password from logs
            
	        if(loUserSession != null) {
                LOG_OBJECT.Debug("##########################TRACE[ProviderAgencyHomeController]" + param); //loUserSession.toString());
                // QC 8998 R 8.8  remove password from logs
	        }else{
	            LOG_OBJECT.Debug("##########################TRACE[ProviderAgencyHomeController]loUserSession is NULL" );
	        }
	        
		    
			if (aoRequest.getParameter(ApplicationConstants.RENDER_ACTION) != null
					&& !aoRequest.getParameter(ApplicationConstants.RENDER_ACTION).isEmpty()
					&& aoRequest.getParameter(ApplicationConstants.RENDER_ACTION).equals(ApplicationConstants.ERROR))
			{
				LOG_OBJECT.Debug("Internal Error occured in ProviderAgencyHomeController Action");
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
						ApplicationConstants.ERROR_MESSAGE_FILENET_DOWN);
				lsHomeJSPName = "homedocumentsharedinitial";
				loModelAndView = new ModelAndView(lsHomeJSPName, loMapForRender);
				
				return loModelAndView;
			}
			
			// Checking for logged in user is a provider or agency user
			if (ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(lsOrgType))
			{
				// By Default user will view organization information
				if (lsAction != null && lsAction.equalsIgnoreCase("showOrgInformation"))
				{
					String lsProviderId = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.PROVIDER_ID);
					Map<String, String> loMProvider = (Map<String, String>) ApplicationSession.getAttribute(aoRequest,
							true, "sharedDocForProvider");
					loMapForRender.put(ApplicationConstants.PROVIDER_ID, lsProviderId);
					loMapForRender.put("providerName", loMProvider.get(lsProviderId));
					loMapForRender.put("fileToInclude", "/portlet/homeprovider/documentshared/homedocumentsharedfinal");
					lsHomeJSPName = "shareDocheader";
				}
				else
				{// else user will view share documents

		            if(loUserSession != null) {
		                LOG_OBJECT.Debug("##########################TRACE[ProviderAgencyHomeController]" + param); //loUserSession.toString());
		                // QC 8998 R 8.8  remove password from logs
		            }else{
		                LOG_OBJECT.Debug("##########################TRACE[ProviderAgencyHomeController]loUserSession is NULL" );
		            }
                    LOG_OBJECT.Debug("##########################TRACE[ProviderAgencyHomeController]lsOrgId is "  + lsOrgId);


		            
		            
				    
					Map<String, String> loMProvider = getSharedAgencyProviderList(loUserSession,
							P8Constants.PROPERTY_CE_SHARED_PROVIDER_ID, lsOrgId, aoRequest);
					if (loMProvider != null && !loMProvider.isEmpty())
					{
						ApplicationSession.setAttribute(loMProvider, aoRequest, "sharedDocForProvider");
						lsHomeJSPName = "homedocumentsharedfinal";
					}
					else
					{
						lsHomeJSPName = "homedocumentsharedinitial";
					}
				}
			}
			else if (ApplicationConstants.AGENCY_ORG.equalsIgnoreCase(lsOrgType))
			{// Checking user is agency
				// user or not
				/* Start QC 8719 - Accelerator Home Page slow response time for Agency users 
				 * since Home Page for Agency doesn't show any information about Shared Documents
				 * remove Shared Documents FileNet call from home page.
                                */
                                /* 
				Map<String, String> loMProvider = getSharedAgencyProviderList(loUserSession,
						P8Constants.PROPERTY_CE_SHARED_AGENCY_ID, lsOrgId, aoRequest);
					ApplicationSession.setAttribute(loMProvider, aoRequest, "sharedDocForProvider");
					// Combo Box comemnt code
					aoRequest.getPortletSession().setAttribute("sharedOrgDetailsForAgency", loMProvider,
							PortletSession.APPLICATION_SCOPE);
				*/
				/* End QC 8719 - Accelerator Home Page slow response time for Agency users */
					lsHomeJSPName = "homedocumentsharedfinal";
					
			}
			else if (ApplicationConstants.CITY_ORG.equalsIgnoreCase(lsOrgType))
			{// Checking user is city user
				// or not
				lsHomeJSPName = "homemanageprovider";
				
			}
			LOG_OBJECT.Debug("lsHomeJSPName :: " +lsHomeJSPName);
			// Fetching details of share with
			
			aoRequest.getPortletSession().setAttribute(HHSR5Constants.SHARED_WITH_DETAILS,
					getShareWithOrgDetails(loUserSession, lsOrgId), PortletSession.APPLICATION_SCOPE);
			
		}
		catch (ApplicationException aoExp)
		{
			String lsErrorMsg = aoExp.toString();
			lsErrorMsg = lsErrorMsg.substring(lsErrorMsg.lastIndexOf(":") + 1, lsErrorMsg.length()).trim();
			if (lsErrorMsg.equalsIgnoreCase(""))
			{
				lsErrorMsg = ApplicationConstants.ERROR_MESSAGE_FILENET_DOWN;
			}
			aoRequest.setAttribute(ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
					ApplicationConstants.DOCUMENT_EXCEPTION);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
			lsHomeJSPName = "homedocumentsharedinitial";
		}
		loModelAndView = new ModelAndView(lsHomeJSPName, loMapForRender);
		
		long loEndTimeTime = System.currentTimeMillis();
		try
		{			
			LOG_OBJECT.Debug("TIME TAKEN for execution of render Method in ProviderAgencyHomeController = "
					+ (loEndTimeTime - loStartTime));
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Error while execution of render Method in ProviderAgencyHomeController ", aoEx);
		}
		UserThreadLocal.unSet();
		
		return loModelAndView;
	}

	/**
	 * This method is used to handle all the action operation to save the data
	 * <ul>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * 
	 * @param aoRequest request object
	 * @param aoResponse response object
	 * @throws ApplicationException application exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void handleActionRequestInternal(ActionRequest aoRequest, ActionResponse aoResponse)
			throws ApplicationException
	{
		long loStartTime = System.currentTimeMillis();
		try
		{
			String lsAction = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.BUSINESS_APPLICATION_ACTION);
			PortletSession loPortletSessionThread = aoRequest.getPortletSession();
			String lsUserIdThreadLocal = (String) loPortletSessionThread.getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			LOG_OBJECT.Debug("from PortletSession get KEY_SESSION_USER_ID :: " + lsUserIdThreadLocal);
			UserThreadLocal.setUser(lsUserIdThreadLocal);
			PortletSession loPortletSession = aoRequest.getPortletSession();
			P8UserSession loP8Usersession = (P8UserSession) loPortletSession.getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			FileNetOperationsUtils.reInitializePageIterator(loPortletSession, loP8Usersession);
			// check action values to forward user to home screen
			if (lsAction != null && lsAction.equals("returnToHome"))
			{
				aoResponse.setWindowState(WindowState.NORMAL);
			}
			else
			{// get selected provider id from request and redirect
				// controller to
				// organization information controller to show read only
				// organization information
				String lsProviderId = aoRequest.getParameter(ApplicationConstants.PROVIDER_ID);
				String lsTempVal = (String) aoRequest.getPortletSession().getAttribute(
						HHSR5Constants.CITY_USER_SEARCH_PROVIDER_ID_NEW, PortletSession.APPLICATION_SCOPE);
				if(null != lsProviderId && lsProviderId.contains(ApplicationConstants.TILD))
				{
					loPortletSession.setAttribute(HHSR5Constants.CITY_USER_SEARCH_PROVIDER_ID_NEW, lsProviderId,
							PortletSession.APPLICATION_SCOPE);
				}
				
				String lsAgencyName = aoRequest.getParameter("providerName");
				
				if (lsProviderId == null)
				{
					lsProviderId = (String) aoRequest.getPortletSession().getAttribute("cityUserSearchProviderId",
							PortletSession.APPLICATION_SCOPE);
					aoResponse.setRenderParameter("action", "businessSummary");
					aoResponse.setRenderParameter("subsection", "questions");
					aoResponse.setRenderParameter("first_action", "accelerator");
					aoResponse.setRenderParameter("section", "basics");
					aoResponse.setRenderParameter("headerJSPName", "shareDocheader");
					
				}
				// R4 Homepage changes: get selected provider id from request
				// and redirect controller to Document Vault Controller if
				// agency or city is selected from
				// 'Documents shared with your Organization' on provider/agency
				// homepage or Agency Name is selected from 'Manage
				// Organization' section on Accelerator homepage.
				else if (lsProviderId.contains(ApplicationConstants.TILD)
						&& ((lsProviderId.split(ApplicationConstants.TILD))[1]
								.equalsIgnoreCase(ApplicationConstants.AGENCY_ORG) || (lsProviderId
								.split(ApplicationConstants.TILD))[1].equalsIgnoreCase(ApplicationConstants.CITY_ORG)))
				{
					aoResponse.setRenderParameter("action", "documentVault");
					aoResponse.setRenderParameter("section", "sharedDoc");
					aoResponse.setRenderParameter("subSection", "documentlist");
					aoResponse.setRenderParameter("next_action", "open");
					aoResponse.setRenderParameter("provider", lsProviderId);
					aoResponse.setRenderParameter("headerClick", "false");
					loPortletSession.setAttribute("cityUserSearchProviderId", lsProviderId,
							PortletSession.APPLICATION_SCOPE);
					loPortletSession.setAttribute("agencyName", lsAgencyName, PortletSession.APPLICATION_SCOPE);
					loPortletSession.setAttribute("homePageManageOrg", true, PortletSession.APPLICATION_SCOPE);
					Map<String, String> loMProvider = (Map<String, String>) ApplicationSession.getAttribute(aoRequest,
							true, "sharedDocForProvider");
					if (loMProvider != null && !loMProvider.isEmpty())
					{// setting
						// provider
						// name
						// to
						// session
						String lsProviderName = loMProvider.get(lsProviderId);
						ApplicationSession.setAttribute(lsProviderName, aoRequest, "providerNameForSharedDoc");
					}
				}
				// R4 HomePage Changes: get selected provider id from request
				// and redirect controller to organization information
				// controller to show read only organization information if a
				// provideris selected from
				// 'Documents shared with your Organization' on provider/agency
				// homepage or from 'Manage Organization' section on Accelerator
				// homepage.
				else
				{
					aoResponse.setRenderParameter("action", "OrgInformation");
					aoResponse.setRenderParameter("subsection", "questions");
					aoResponse.setRenderParameter(ApplicationConstants.BUSINESS_APPLICATION_ACTION,
							ApplicationConstants.SHOW_QUESTION);
					aoResponse.setWindowState(WindowState.MAXIMIZED);
					aoResponse.setRenderParameter("cityUserSearchProviderId", lsProviderId);
					aoResponse.setRenderParameter("section", "basics");
					aoResponse.setRenderParameter("headerJSPName", "shareDocheader");
					loPortletSession.setAttribute("homePageManageOrg", true, PortletSession.APPLICATION_SCOPE);
					Map<String, String> loMProvider1 = (Map<String, String>) ApplicationSession.getAttribute(aoRequest,
							true, "sharedDocForProvider");
					if (loMProvider1 != null && !loMProvider1.isEmpty())
					{// setting
						// provider
						// name
						// to
						// session
						String lsProviderName = loMProvider1.get(lsProviderId);
						ApplicationSession.setAttribute(lsProviderName, aoRequest, "providerNameForSharedDoc");
					}
				}
				aoResponse.setWindowState(WindowState.MAXIMIZED);
				aoResponse.setRenderParameter("cityUserSearchProviderId", lsProviderId);
				aoResponse.setRenderParameter("needPrintableView", "true");
			}
			long loEndTimeTime = System.currentTimeMillis();
			LOG_OBJECT.Debug("TIME TAKEN for execution of action Method in ProviderAgencyHomeController = "
					+ (loEndTimeTime - loStartTime));
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Error while execution of action Method in ProviderAgencyHomeController ", aoEx);
			aoResponse.setRenderParameter(ApplicationConstants.RENDER_ACTION, ApplicationConstants.ERROR);
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while execution of action Method in BusinessSummaryController", aoExp);
			aoResponse.setRenderParameter(ApplicationConstants.RENDER_ACTION, ApplicationConstants.ERROR);
		}
		UserThreadLocal.unSet();
	}

	/**
	 * This method is used to get the shared document provider/agency list
	 * <ul>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * 
	 * @param aoUserSession P8UserSession object
	 * @param asAgencyType string containing agency type
	 * @param asProviderId string containing provider id
	 * @param aoRequest RenderRequest object
	 * @return map
	 * @throws ApplicationException application exception
	 */
	@SuppressWarnings("rawtypes")
	private Map<String, String> getSharedAgencyProviderList(P8UserSession aoUserSession, String asAgencyType,
			String asProviderId, RenderRequest aoRequest) throws ApplicationException
	{
		Channel loChannel = new Channel();
		loChannel.setData("aoFilenetSession", aoUserSession);
		loChannel.setData("asAgencyType", asAgencyType);
		loChannel.setData("asProviderId", asProviderId);

		// Start QC 8998 R 8.8  remove password from logs
    	String param = CommonUtil.maskPassword(aoUserSession);
    		
        if(aoUserSession != null) {
            LOG_OBJECT.Debug("##########################TRACE[getSharedAgencyProviderList]" + param); //aoUserSession.toString());
         // End QC 8998 R 8.8  remove password from logs
        }else{
            LOG_OBJECT.Debug("##########################TRACE[getSharedAgencyProviderList]loUserSession is NULL" );
        }
        LOG_OBJECT.Debug("##########################TRACE[getSharedAgencyProviderList]asAgencyType is "  + asAgencyType);
        LOG_OBJECT.Debug("##########################TRACE[getSharedAgencyProviderList]asProviderId is "  + asProviderId);



		TransactionManager.executeTransaction(loChannel, "getSharedDocumentsOwnerList_filenet");
		TreeSet loTemploProviderSet = (TreeSet) loChannel.getData("providerList");
		List<ProviderBean> loProviderList = null;
		// First checking provided list in session, if not found then search the
		// DB
		if (aoRequest.getPortletSession().getAttribute("provList", PortletSession.APPLICATION_SCOPE) != null)
		{
			loProviderList = (List) aoRequest.getPortletSession().getAttribute("provList",
					PortletSession.APPLICATION_SCOPE);
		}
		else
		{
			loProviderList = FileNetOperationsUtils.getProviderList();
		}
		// R4 Homepage changes: fetching agency list from cache.
		TreeSet<String> loAgencyList = (TreeSet<String>) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.AGENCY_LIST);
		
		if (null == loAgencyList || loAgencyList.isEmpty())
		{
			loAgencyList = FileNetOperationsUtils.getNYCAgencyListFromDB();
			
		}
		// R4 Homepage changes: Passing additional parameter : loAgencyList
		// since the sorted map would contain both provider and agency shared
		// list
		// which would be populated in 'Select an Organization' dropdown in
		// 'Documents shared with your Organization' section on Provider/Agency
		// Homepage.
		
		return convertToMap(loProviderList, loAgencyList, loTemploProviderSet);
	}

	/**
	 * This method is used to sort the map
	 * <ul>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * 
	 * @param aoProviderList provider list
	 * @param aoAgencyList agency list
	 * @param aoProviderSet provider set
	 * @return sorted map
	 */
	private Map<String, String> convertToMap(List<ProviderBean> aoProviderList, TreeSet<String> aoAgencyList,
			TreeSet aoProviderSet)
	{
		
		Map<String, String> loProviderMap = new HashMap<String, String>();
		if (aoProviderList != null)
		{
			Iterator<ProviderBean> loItrProvider = aoProviderList.iterator();
			while (loItrProvider.hasNext())
			{
				ProviderBean loProvider = loItrProvider.next();
				loProviderMap.put(loProvider.getHiddenValue(), loProvider.getDisplayValue());
			}
		}
		// R4 homepage changes
		Map<String, String> loAgencyMap = new HashMap<String, String>();
		if (aoAgencyList != null)
		{
			Iterator loIterator = aoAgencyList.iterator();
			while (loIterator.hasNext())
			{
				String lsAgency = (String) loIterator.next();
				String[] loAgencyName = lsAgency.split(ApplicationConstants.TILD);
				loAgencyMap.put(loAgencyName[0], loAgencyName[1]);
			}
		}

		
		Map<String, String> loProviderToDisplay = new HashMap<String, String>();
		/* QC 8719 - Accelerator Home Page slow response time for Agency users */
		/* add condition to prevent runtime error */
		if (aoProviderSet != null) 
		{ 
			Iterator loSelectedProvider = aoProviderSet.iterator();
			while (loSelectedProvider.hasNext())
			{
				String lsProviderId = (String) loSelectedProvider.next();
				if (loProviderMap.containsKey(lsProviderId))
				{
					loProviderToDisplay.put(lsProviderId.concat((ApplicationConstants.TILD_PROVIDER)),
						StringEscapeUtils.unescapeJavaScript(loProviderMap.get(lsProviderId)));
				}
				if (loAgencyMap.containsKey(lsProviderId))
				{
					loProviderToDisplay.put(lsProviderId.concat((ApplicationConstants.TILD_AGENCY)),
						StringEscapeUtils.unescapeJavaScript(loAgencyMap.get(lsProviderId)));
				}
				if (lsProviderId.equalsIgnoreCase(ApplicationConstants.CITY_ORG))
				{
					loProviderToDisplay.put(ApplicationConstants.TILD_CITY, ApplicationConstants.CITY_USER_NAME);
				}
			}
		} /* end QC 8719 */

		// Sort Map<String, String> loProviderToDisplay; Defect Fix #2554
		Map<String, String> loSortProviderMapToDisplay = new LinkedHashMap<String, String>();
		List<String> loKeyList = new ArrayList<String>(loProviderToDisplay.keySet());
		List<String> loValueList = new ArrayList<String>(loProviderToDisplay.values());
		Set<String> loSortedSet = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);// Additional
																						// fix
																						// for
																						// defect
																						// #2554
																						// for
																						// case
																						// insensitive
																						// sorting
		loSortedSet.addAll(loValueList);

		Object[] loSortedArray = loSortedSet.toArray();
		for (int loMapCounter = 0; loMapCounter < loSortedArray.length; loMapCounter++)
		{
			loSortProviderMapToDisplay.put(loKeyList.get(loValueList.indexOf(loSortedArray[loMapCounter])),
					(String) loSortedArray[loMapCounter]);
		}
		return loSortProviderMapToDisplay;
	}

	// Added in release 5
       /**
		 * This Method Fetches all the details of city & agency users and it returns
		 * a hashmap with key as User Dn & values as city user bean
		 * <ul>
		 * <li>Execute transaction id <b> getSharedOwnerList_filenet</b></li>
		 * <li> Method Added in R5 </li>
		 * <li> Changed in build 4.0.1- added static keyword in the definition </li>
		 * </ul> 
		 * @return loDataMap HashMap<String, String>
		 * @throws ApplicationException
		 */
	@SuppressWarnings("unchecked")
	public static HashMap<String, String> getShareWithOrgDetails(P8UserSession aoUserSession, String asOrgId)
			throws ApplicationException
	{
		Channel loChannel = new Channel();
		HashMap<String, String> loDataTempMap = new HashMap<String, String>();
		HashMap<String, String> loDataMap = new HashMap<String, String>();

		try
		{			
			loChannel.setData("aoFilenetSession", aoUserSession);
			loChannel.setData("aoOrgId", asOrgId);

			TransactionManager.executeTransaction(loChannel, "getSharedOwnerList_filenet",
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			loDataTempMap = (HashMap<String,String>) loChannel.getData("loOrgMap");
			List<ProviderBean> loProvList = (List<ProviderBean>) BaseCacheManagerWeb.getInstance().getCacheObject(
					HHSR5Constants.PROV_LIST);
			TreeSet<String> loAgencySet = (TreeSet<String>) BaseCacheManagerWeb.getInstance().getCacheObject(
					ApplicationConstants.AGENCY_LIST);
			
			
			String lsProvName = null;
			for (Iterator iterator = loDataTempMap.keySet().iterator(); iterator.hasNext();)
			{
				String lsOrgId = (String) iterator.next();
				if(loDataTempMap.get(lsOrgId).equals(HHSR5Constants.PROVIDER_ID) || loDataTempMap.get(lsOrgId).equals(HHSR5Constants.PROVIDER))
				{
					lsProvName = FileNetOperationsUtils.getProviderName(loProvList, lsOrgId);	
				}
				else
				{
					lsProvName = FileNetOperationsUtils.getAgencyName(loAgencySet, lsOrgId);
				}
				loDataMap.put(lsOrgId, lsProvName);
			}
		}
		catch (ApplicationException aoAppEx)
		{
			throw aoAppEx;
		}
		return loDataMap;

	}
}
// Added in release 5
