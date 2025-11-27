package com.nyc.hhs.controllers.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.RenderRequest;

import org.jdom.Document;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.util.PortalUtil;
import com.nyc.hhs.util.XMLUtil;

/**
 * This class sets the required values in the channel object, required to
 * execute the transaction for displaying service setting section. Also sets the
 * values, required in the in the jsp, in the request object.
 * 
 */

public class ServiceSetting extends BusinessApplication
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
		String[] loSelectedSetting = null;
		String loBottomCheckBox = null;
		ArrayList<String> loALSelectedSetting = new ArrayList<String>();
		// block of code to be executed if aoRequest contains value for
		// servicesetting
		if (null != aoRequest.getParameterValues("servicesetting"))
		{
			loSelectedSetting = aoRequest.getParameterValues("servicesetting");
		}
		// block of code to be executed if aoRequest contains value for
		// noservicesetting
		if (null != aoRequest.getParameter("noservicesetting"))
		{
			loBottomCheckBox = aoRequest.getParameter("noservicesetting");
		}
		// block of code to be executed if loSelectedSetting in not null
		if (null != loSelectedSetting)
		{
			loALSelectedSetting = new ArrayList<String>(Arrays.asList(loSelectedSetting));
			aoRequest.getPortletSession().setAttribute("settingSelectedByUser", loALSelectedSetting);
		}
		String lsErrorMsg = null;
		// block of code to be executed if loBottomCheckBox in not null and
		// loSelectedSetting contains at least one element
		if (!(null != loBottomCheckBox || (null != loSelectedSetting && loSelectedSetting.length > 0)))
		{
			lsErrorMsg = "You must select at least one Service Setting";
		}
		Channel loChanneL = new Channel();
		loChanneL.setData(ApplicationConstants.ORG_ID, asOrgId);
		loChanneL.setData("errorMsg", lsErrorMsg);
		loChanneL.setData("asElementType", asTaxonomyName);
		loChanneL.setData("aoElementIdList", loALSelectedSetting);
		loChanneL.setData("asBottomCheckBox", loBottomCheckBox);
		return loChanneL;
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
		Document loDoc = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.TAXONOMY_ELEMENT);
		String lsFromCache = "false";
		// block of code to be executed if loDoc in not null
		if (loDoc != null)
		{
			String lsDocumentString = XMLUtil.getXMLAsString(loDoc);
			lsFromCache = (lsDocumentString.length() > 0) ? ApplicationConstants.TRUE : ApplicationConstants.FALSE;
		}
		Channel loChannel = new Channel();
		Map<String, String> loServiceInfoMap = new HashMap<String, String>();
		loServiceInfoMap.put("serviceAppId",
				PortalUtil.parseQueryString(aoRequest, ApplicationConstants.SERVICE_APPLICATION_ID));
		loServiceInfoMap.put("orgId", asOrgId);
		loChannel.setData("reqServiceInfo", loServiceInfoMap);
		loChannel.setData(ApplicationConstants.TAXONOMY_TYPE, asTaxonomyName);
		loChannel.setData("asElementType", asTaxonomyName);
		loChannel.setData(ApplicationConstants.FROM_CACHE, lsFromCache);
		loChannel.setData(ApplicationConstants.ORGANIZATION_ID, asOrgId);

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
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	@Override
	public Map<String, Object> getMapForRender(String asAction, String asSectionName, Channel aoChannel,
			RenderRequest aoRequest) throws ApplicationException
	{

		Map<String, Object> loMapForRender = new HashMap<String, Object>();
		String lsMenu = getSubMenu(asSectionName);
		loMapForRender.put(ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_MENU, lsMenu);
		
		List<Map<String, String>> loSettingSelectedByUser = null;
		// Checks if the action is open then execute this block
		if (null == asAction || asAction.equals(ApplicationConstants.BUSINESS_APPLICATION_ACTION_OPEN))
		{
			if (null != aoChannel.getData("loElementIdList"))
			{
				loSettingSelectedByUser = (List<Map<String, String>>) aoChannel.getData("loElementIdList");
			}
			ArrayList loTaxonomyTree = (ArrayList) aoChannel.getData("loTaxonomyTreeList");
			// block of code to be executed if aoRequest in not null
			if (null != aoRequest)
			{
				aoRequest.getPortletSession().setAttribute(
						ApplicationConstants.BUSINESS_APPLICATION_SESSION_PARAMETER_TAXONOMY, loTaxonomyTree);
			}
			loMapForRender.put("TaxonomyElementList", loTaxonomyTree);
			loMapForRender.put("loTaxonomyIdMap", loSettingSelectedByUser);
			loMapForRender.put("errorToDisplay", null);
			loMapForRender.put("loReadOnly", false); // need to pass variable on
														// basis of application
														// status to make page
														// readonly

		}
		else if (asAction.equals(ApplicationConstants.BUSINESS_APPLICATION_ACTION_SAVE))
		{
			
			loSettingSelectedByUser = (List<Map<String, String>>) aoChannel.getData("loElementIdList");
			ArrayList loTaxonomyTree = (ArrayList) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.BUSINESS_APPLICATION_SESSION_PARAMETER_TAXONOMY);
			ArrayList lsErrorMsg = new ArrayList();
			if (null != loSettingSelectedByUser && loSettingSelectedByUser.size() <= 0)
			{
				lsErrorMsg.add(aoRequest.getParameter("errorMsg"));
			}
			loMapForRender.put("loReadOnly", false);
			loMapForRender.put("TaxonomyElementList", loTaxonomyTree);
			loMapForRender.put("loTaxonomyIdMap", loSettingSelectedByUser);
		}
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
