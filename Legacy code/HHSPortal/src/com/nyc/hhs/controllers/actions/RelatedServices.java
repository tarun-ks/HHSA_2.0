package com.nyc.hhs.controllers.actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.RenderRequest;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.model.TaxonomyTree;

/**
 * This class sets the required values in the channel object, required to execute 
 * the transaction for displaying the related services of an existing service.Also 
 * it sets the values, required in the in jsp, in the request object. 
 * 
 */

public class RelatedServices extends BusinessApplication

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
	public Channel getChannelObject(String asSectionName, String asOrgId, String asAppId, String asAppStatus, String asAppDataForUpdate,
			String asAction, String asUserRole, ActionRequest aoRequest, String asTaxonomyName) throws ApplicationException
	{
		Channel loChannel = new Channel();
		loChannel.setData(ApplicationConstants.BUSINESS_APPLICATION_ID,asAppId);
		loChannel.setData(ApplicationConstants.NEW_ORGANIZATION_ID,asOrgId);
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
	public Channel getChannelObject(String asSectionName, String asOrgId, String asAppId, String asAppStatus, String asAppDataForUpdate,
			String asAction, String asUserRole, RenderRequest aoRequest, String asTaxonomyName) throws ApplicationException
	{
		Channel loChannel = new Channel();
		loChannel.setData(ApplicationConstants.BUSINESS_APPLICATION_ID,asAppId);
		loChannel.setData(ApplicationConstants.NEW_ORGANIZATION_ID,asOrgId);
		return loChannel;
	}
	
	/**
	 * This method fetches the map to be rendered
	 * 
	 * @param asAction - the action to be performed
	 * @param asSectionName - current section name
	 * @param aoChannel - channel object with data, will be needed to be passed to MapForRender
	 * @param aoRequest - Render Request
	 * @return the map to be rendered on front end
	 * @throws ApplicationException
	 */
	@Override
	public Map<String, Object> getMapForRender(String asAction, String asSectionName, Channel aoChannel, RenderRequest aoRequest)
			throws ApplicationException
	{
		Map<String,Object> loMapForRender  = new HashMap<String, Object>();
		String lsMenu = getSubMenu(asSectionName);
		loMapForRender.put(ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_MENU, lsMenu);
		loMapForRender.put(ApplicationConstants.FILE_TO_INCLUDE, "/WEB-INF/jsp/businessapplication/relatedservices.jsp");
		List<TaxonomyTree> loRelatedServicesList = (List<TaxonomyTree>) aoChannel.getData("loRelatedServiceDetails");
		loMapForRender.put("loRelatedServicesList", loRelatedServicesList);
		return loMapForRender;
	}
	
	/**
	 * This method fetches the map to be rendered
	 * 
	 * @param asAction - the action to be performed
	 * @param asSectionName - current section name
	 * @param aoChannel - channel object with data, will be needed to be passed to MapForRender
	 * @param aoRequest - Action Request
	 * @return the map to be rendered on front end
	 * @throws ApplicationException
	 */
	@Override
	public Map<String, Object> getMapForRender(String asAction,
			String asSectionName, Channel aoChannel, ActionRequest aoRequest)
			throws ApplicationException {
		return null;
	}

}
