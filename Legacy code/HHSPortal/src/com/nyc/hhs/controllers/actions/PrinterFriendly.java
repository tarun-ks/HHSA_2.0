package com.nyc.hhs.controllers.actions;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.transaction.Channel;

/**
 * This class sets the required values in the channel object, required to execute the
 * transaction for displaying the contents of the page in the printer friendly form.
 * Also it sets the values, required in the in jsp, in the request object.  
 *
 */

public class PrinterFriendly extends BusinessApplication {
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
	public Channel getChannelObject(String asSectionName, String asOrgId,
			String asAppId, String asAppStatus, String asAppDataForUpdate,
			String asAction, String asUserRole, ActionRequest aoRequest,
			String asTaxonomyName) throws ApplicationException {
		return null;
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
	public Channel getChannelObject(String asSectionName, String asOrgId,
			String asAppId, String asAppStatus, String asAppDataForUpdate,
			String asAction, String asUserRole, RenderRequest aoRequest,
			String asTaxonomyName) throws ApplicationException {
		String lsUserId=null;
		String lsFormPath=null;
		//Block of code to be executed if aoRequest is not null
		if(aoRequest!=null){
			lsFormPath = aoRequest.getPortletSession().getPortletContext().getRealPath(ApplicationConstants.FORMS_FOLDER_NAME);
			PortletSession loPortletSession = aoRequest.getPortletSession(true);
			lsUserId = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
		}
		Channel loChannel = new Channel();
		loChannel.setData(ApplicationConstants.ORGANIZATION_ID, asOrgId);
		loChannel.setData(ApplicationConstants.APPID, asAppId);
		loChannel.setData(ApplicationConstants.USER_ID, lsUserId);
		loChannel.setData("abIsFinalView", false);
		if(lsFormPath != null && lsFormPath.lastIndexOf("/") > -1)
		{
			lsFormPath = lsFormPath.substring(0, lsFormPath.lastIndexOf("/"));
		}
		if(lsFormPath != null && lsFormPath.lastIndexOf("\\") > -1)
		{
			lsFormPath = lsFormPath.substring(0, lsFormPath.lastIndexOf("\\"));
		}
		loChannel.setData("asWebContentPath", lsFormPath);
		loChannel.setData("lsTransactionName", "printerFriendly");
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
	public Map<String, Object> getMapForRender(String asAction,
			String asSectionName, Channel aoChannel, RenderRequest aoRequest)
			throws ApplicationException {
		Map<String,Object> loMapForRender = new HashMap<String, Object>();
		loMapForRender.put("content", aoChannel.getData("loPrinterFriendlyContent"));
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
