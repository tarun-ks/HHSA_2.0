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
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.util.BusinessApplicationUtil;
import com.nyc.hhs.util.PortalUtil;
import com.nyc.hhs.util.PropertyUtil;

/**
 * This class provides the functionality to the user for indicating the
 * sub-specialty their organization provides within the selected Service. Also
 * it searches for the Specialization tree for the selected service. and
 * pre-populate the selected Specialization by the provider.
 * 
 */

public class Specialization extends BusinessApplication
{
	private static final LogInfo LOG_OBJECT = new LogInfo(Specialization.class);

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
		if (null != aoRequest.getParameterValues("taxonomyList"))
		{
			loSelectedSetting = aoRequest.getParameterValues("taxonomyList");
		}
		if (null != aoRequest.getParameter("nospecialization"))
		{
			loBottomCheckBox = aoRequest.getParameter("nospecialization");
		}
		// Below section executes when taxonomy List is not null.
		if (null != loSelectedSetting)
		{
			loALSelectedSetting = new ArrayList<String>(Arrays.asList(loSelectedSetting));
			aoRequest.getPortletSession().setAttribute("settingSelectedByUser", loALSelectedSetting);
		}
		// Below section executes no specialization is selected
		if (null != loBottomCheckBox)
		{
			aoRequest.getPortletSession().setAttribute("bottomCheckBox", loALSelectedSetting);
		}
		String lsErrorMsg = null;
		if (!(null != loBottomCheckBox || (null != loSelectedSetting && loSelectedSetting.length > 0)))
		{
			lsErrorMsg = "You must select at least one Specialization";
		}
		Channel loChanneL = new Channel();
		loChanneL.setData("errorMsg", lsErrorMsg);
		loChanneL.setData(ApplicationConstants.ORG_ID, asOrgId);
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
		Channel loChannel = new Channel();
		Map<String, String> loServiceInfoMap = new HashMap<String, String>();
		loServiceInfoMap.put("serviceAppId",
				PortalUtil.parseQueryString(aoRequest, ApplicationConstants.SERVICE_APPLICATION_ID));
		loServiceInfoMap.put("orgId", asOrgId);
		loChannel.setData("reqServiceInfo", loServiceInfoMap);
		loChannel.setData("asElementType", asTaxonomyName);
		loChannel.setData("asAppId", asAppId);
		loChannel.setData(ApplicationConstants.ORGANIZATION_ID, asOrgId);
		return loChannel;
	}

	/**
	 * This method fetches the map to be rendered
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
		String lsMenu = getSubMenu(asSectionName);
		loMapForRender.put(ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_MENU, lsMenu);
		List<Map<String, String>> loSettingSelectedByUser = null;
		Document loTaxonomyDOM = null;
		String lsElementId = null;
		if (null != aoRequest.getAttribute(ApplicationConstants.ELEMENT_ID))
		{
			lsElementId = (String) aoRequest.getAttribute(ApplicationConstants.ELEMENT_ID);
		}
		if (null != BaseCacheManagerWeb.getInstance().getCacheObject(ApplicationConstants.TAXONOMY_ELEMENT))
		{
			loTaxonomyDOM = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
					ApplicationConstants.TAXONOMY_ELEMENT);
		}
		else
		{
			PropertyUtil loTaxonomyUtil = new PropertyUtil();
			loTaxonomyUtil.setTaxonomyInCache(BaseCacheManagerWeb.getInstance(), ApplicationConstants.TAXONOMY_ELEMENT);
			loTaxonomyDOM = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
					ApplicationConstants.TAXONOMY_ELEMENT);
		}
		String lsSpecializationTree = null;
		try
		{
			lsSpecializationTree = BusinessApplicationUtil.getTree(loTaxonomyDOM, lsElementId, "checkbox");
			if (null == lsSpecializationTree)
			{
				lsSpecializationTree = "No Specializiation available";
			}

			aoRequest.getPortletSession().setAttribute(
					ApplicationConstants.BUSINESS_APPLICATION_SESSION_PARAMETER_TAXONOMY, lsSpecializationTree);

		}
		catch (Exception aoException)
		{
			LOG_OBJECT.Error("Internal exception occured", aoException);
			lsSpecializationTree = "No Specializiation available";
		}
		loMapForRender.put("specializationTree", lsSpecializationTree);
		if (null == asAction || asAction.equals(ApplicationConstants.BUSINESS_APPLICATION_ACTION_OPEN))
		{
			getMapForRenderForOpen(aoChannel, loMapForRender, loSettingSelectedByUser);
		}
		else if (asAction.equals(ApplicationConstants.BUSINESS_APPLICATION_ACTION_SAVE))
		{
			getMapForRenderForSave(aoChannel, aoRequest, loMapForRender, loSettingSelectedByUser, lsSpecializationTree);
		}
		return loMapForRender;
	}

	/**
	 * This method is used to get the channel object for specialization while
	 * rendering.
	 * 
	 * @param aoChannel
	 * @param aoMapForRender
	 * @param aoSettingSelectedByUser
	 */
	private void getMapForRenderForOpen(Channel aoChannel, Map<String, Object> aoMapForRender,
			List<Map<String, String>> aoSettingSelectedByUser)
	{
		if (null != aoChannel.getData("loElementIdList"))
		{
			aoSettingSelectedByUser = (List<Map<String, String>>) aoChannel.getData("loElementIdList");
		}
		aoMapForRender.put("loTaxonomyIdList", aoSettingSelectedByUser);
		aoMapForRender.put("errorToDisplay", null);
		aoMapForRender.put("loReadOnly", false); // need to pass variable on
													// basis of application
													// status to make page
													// readonly
	}

	/**
	 * This method is used to get the channel object for specialization while
	 * saving.
	 * 
	 * @param aoChannel
	 * @param aoRequest
	 * @param aoMapForRender
	 * @param aoSettingSelectedByUser
	 * @param asSpecializationTree
	 */
	private void getMapForRenderForSave(Channel aoChannel, RenderRequest aoRequest, Map<String, Object> aoMapForRender,
			List<Map<String, String>> aoSettingSelectedByUser, String asSpecializationTree)
	{
		if (null == aoRequest.getPortletSession().getAttribute("settingSelectedByUser")
				&& null == aoRequest.getPortletSession().getAttribute("bottomCheckBox"))
		{
			aoMapForRender.put("loReadOnly", false);
			aoMapForRender.put("specializationTree", asSpecializationTree);
			aoMapForRender.put("loTaxonomyIdList", aoSettingSelectedByUser);
		}
		else
		{
			aoSettingSelectedByUser = (List<Map<String, String>>) aoChannel.getData("loElementIdList");
			asSpecializationTree = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.BUSINESS_APPLICATION_SESSION_PARAMETER_TAXONOMY);
			aoMapForRender.put("loReadOnly", false);
			aoMapForRender.put("specializationTree", asSpecializationTree);
			aoMapForRender.put("loTaxonomyIdList", aoSettingSelectedByUser);
		}
	}

	/**
	 * This method fetches the map to be rendered
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
