package com.nyc.hhs.controllers.actions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.portlet.ActionRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;

import org.jdom.Document;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.ServiceSummary;
import com.nyc.hhs.model.ServiceSummaryStatus;
import com.nyc.hhs.util.BusinessApplicationUtil;
import com.nyc.hhs.util.PortalUtil;

/**
 * This class sets the required values in the channel object, required to
 * execute the transaction for displaying the service summary. Also it sets the
 * values, required in the in jsp, in the request object.
 * 
 */

public class ServiceApplicationSummary extends BusinessApplication
{

	/**
	 * Gets the channel object for action
	 * 
	 * @param asSectionName - current section name
	 * @param asOrgId - the organization id of the current organization
	 * @param asAppId - Business application id of the application
	 * @param asAppStatus - the current application status
	 * @param asAppDataForUpdate - data to be updated in application
	 * @param asAction - the action to be performed
	 * @param asUserRole - current user role
	 * @param aoRequest - Action request
	 * @param asTaxonomyName - taxonomy name to be used in factory
	 * @return the channel object to be used for further processing
	 * @throws ApplicationException
	 */
	@Override
	public Channel getChannelObject(String asSectionName, String asOrgId, String asAppId, String asAppStatus,
			String asAppDataForUpdate, String asAction, String asUserRole, ActionRequest aoRequest,
			String asTaxonomyName) throws ApplicationException
	{

		String lsServiceId = (String) aoRequest.getParameter("element_id");
		boolean loServiceDeleteFlag = true;
		aoRequest.getPortletSession().setAttribute("ServiceDeleteFlag", loServiceDeleteFlag);
		Channel loChannel = new Channel();
		loChannel.setData("lsServiceId", lsServiceId);
		loChannel.setData("lsUserId", asAppId);
		loChannel.setData("asOrgId", asOrgId);
		loChannel.setData("lsOrgId", asOrgId);
		loChannel.setData("asBussAppId", asAppId);
		loChannel.setData("asAppId", asSectionName);
		loChannel.setData("abIsFinalView", true);
		PortletSession loPortletSession = aoRequest.getPortletSession();
		String lsUserId = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		loChannel.setData("asModBy", lsUserId);
		String lsServiceAppId = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.SERVICE_APPLICATION_ID);
		loChannel.setData("asServiceAppId", lsServiceAppId);
		return loChannel;
	}

	/**
	 * Gets the channel object for render
	 * 
	 * @param asSectionName - current section name
	 * @param asOrgId - the organization id of the current organization
	 * @param asAppId - Business application id of the application
	 * @param asAppStatus - the current application status
	 * @param asAppDataForUpdate - data to be updated in application
	 * @param asAction - the action to be performed
	 * @param asUserRole - current user role
	 * @param aoRequest - Render request
	 * @param asTaxonomyName - taxonomy name to be used in factory
	 * @return the channel object to be used for further processing
	 * @throws ApplicationException
	 */
	@Override
	public Channel getChannelObject(String asSectionName, String asOrgId, String asAppId, String asAppStatus,
			String asAppDataForUpdate, String asAction, String asUserRole, RenderRequest aoRequest,
			String asTaxonomyName) throws ApplicationException
	{

		String lsServiceAppId = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.SERVICE_APPLICATION_ID);

		Channel loChannel = new Channel();
		Map<String, String> loServiceInfoMap = new HashMap<String, String>();
		loServiceInfoMap.put("serviceAppId",
				PortalUtil.parseQueryString(aoRequest, ApplicationConstants.SERVICE_APPLICATION_ID));
		loServiceInfoMap.put("orgId", asOrgId);
		loChannel.setData("reqServiceInfo", loServiceInfoMap);
		loChannel.setData("asBussAppId", asAppId);
		loChannel.setData("asAppId", asAppId);
		loChannel.setData("asOrgId", asOrgId);
		loChannel.setData("abIsFinalView", true);
		loChannel.setData("asServiceAppId", lsServiceAppId);
		return loChannel;
	}

	/**
	 * This method fetches the map to be rendered
	 * 
	 * @param asAction - the action to be performed
	 * @param asSectionName - current section name
	 * @param aoChannel - channel object with data, will be needed to be passed
	 *            to MapForRender
	 * @param aoRequest - Render Request
	 * @return the map to be rendered on front end
	 * @throws ApplicationException
	 */
	@Override
	public Map<String, Object> getMapForRender(String asAction, String asSectionName, Channel aoChannel,
			RenderRequest aoRequest) throws ApplicationException
	{
		Map<String, Object> loMapForRender = new HashMap<String, Object>();

		
		// Checks if the action is singleService then execute this block
		if (asAction != null && asAction.equalsIgnoreCase("singleService"))
		{
			ServiceSummaryStatus loServiceSummary = (ServiceSummaryStatus) aoChannel.getData("loServiceSummary");
			Document loDoc = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
					ApplicationConstants.TAXONOMY_ELEMENT);
			if (loDoc != null)
			{
				String lsElementId = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.ELEMENT_ID);
				String lsName = BusinessApplicationUtil.getTaxonomyName(lsElementId, loDoc);
				if (lsName == null || lsName.equalsIgnoreCase(""))
				{
					// code added for deleted service name
					Map<String, String> loApplicationMap = new LinkedHashMap<String, String>();
					loApplicationMap.put("lsElementId", lsElementId);
					Map<String, String> loActionMap = new HashMap<String, String>();
					loActionMap.put("lsElementId", lsElementId);
					Channel loChannel = new Channel();
					loChannel.setData("loActionMap", loActionMap);
					TransactionManager.executeTransaction(loChannel, "getDeletedServiceName");
					lsName = (String) loChannel.getData("serviceName");
					// code added for deleted service name
				}
				loServiceSummary.setDocumentStatus(lsName);
			}
			loMapForRender.put("ServiceSummary", loServiceSummary);
		}
		else
		{
			List<ServiceSummary> loServiceSummaryList = (List<ServiceSummary>) aoChannel
					.getData("lohServiceSummaryMap");
			Collections.sort(loServiceSummaryList);
			Boolean loServiceDeleteFlag = (Boolean) aoRequest.getPortletSession().getAttribute("ServiceDeleteFlag");
			aoRequest.getPortletSession().setAttribute(
					ApplicationConstants.BUSINESS_APPLICATION_SESSION_PARAMETER_TAXONOMY, loServiceSummaryList);
			loMapForRender.put("ServiceDeleteFlag", loServiceDeleteFlag);
			loMapForRender.put("SummaryServiceListOfMap", loServiceSummaryList);
			List<Object> loDeactivatedServiceList = (List<Object>) aoChannel.getData("deactivatedServiceList");
			StringBuffer lsDeactivatedServices = new StringBuffer();
			if (loDeactivatedServiceList != null)
			{
				Iterator<ServiceSummary> loServiceSummaryIterator = loServiceSummaryList.iterator();
				while (loServiceSummaryIterator.hasNext())
				{
					ServiceSummary loServiceSummary = (ServiceSummary) loServiceSummaryIterator.next();
					if (loDeactivatedServiceList.contains(loServiceSummary.getMsServiceAppId()))
					{
						lsDeactivatedServices.append(loServiceSummary.getMsServiceName()).append(", ");
					}
				}
				lsDeactivatedServices.replace(lsDeactivatedServices.lastIndexOf(", "),
						lsDeactivatedServices.length() - 1, "");
			}
			loMapForRender.put("lsDeactivatedServices", lsDeactivatedServices);
		}
		Map<String, StringBuffer> loServiceSummaryMap = (Map<String, StringBuffer>) aoChannel
				.getData("aoPrinterFriendlyComments");
		if (loServiceSummaryMap != null)
		{
			for (Entry<String, StringBuffer> loentry : loServiceSummaryMap.entrySet())
			{
				StringBuffer loPrintableHtmlContent = new StringBuffer();
				loPrintableHtmlContent.append("<div class='commentBox' id='").append(loentry.getKey())
						.append("_comments' >").append(loentry.getValue()).append("</div>");
				loServiceSummaryMap.put(loentry.getKey(), loPrintableHtmlContent);
			}
		}
		loMapForRender.put("aoServiceSummaryMap", loServiceSummaryMap);
		
		return loMapForRender;
	}

	/**
	 * This method fetches the map to be rendered
	 * 
	 * @param asAction - the action to be performed
	 * @param asSectionName - current section name
	 * @param aoChannel - channel object with data, will be needed to be passed
	 *            to MapForRender
	 * @param aoRequest - Action Request
	 * @return the map to be rendered on front end
	 * @throws ApplicationException
	 */
	@Override
	public Map<String, Object> getMapForRender(String asAction, String asSectionName, Channel aoChannel,
			ActionRequest aoRequest) throws ApplicationException
	{
		return null;
	}

}
