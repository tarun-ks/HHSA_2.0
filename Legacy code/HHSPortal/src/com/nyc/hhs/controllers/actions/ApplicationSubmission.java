package com.nyc.hhs.controllers.actions;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DateUtil;

/**
 * This class sets the required values in the Channel object, required to execute the 
 * transaction for launching the work flow for submission of business application, to
 * display the existing services, or to save the services in the business application.
 * Also it sets the values, required in the in jsp, in the request object.  
 * 
 */

public class ApplicationSubmission extends BusinessApplication
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
		PortletSession loPortletSession = aoRequest.getPortletSession();
		String lsEmailId = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_EMAIL_ID, PortletSession.APPLICATION_SCOPE);
		Channel loChannel = new Channel();
		//Set data in Channel to Add data in Audit for TnC 
		loChannel.setData("orgId", asOrgId);
		loChannel.setData("tnceventName", ApplicationConstants.AUDIT_TERMSNCONDITIONS_EVENT_NAME);
		loChannel.setData("tnceventType", ApplicationConstants.AUDIT_TERMSNCONDITIONS_EVENT_TYPE);
		loChannel.setData("tncauditDate", DateUtil.getFormattedDated("dd/MM/yyyy HH:mm:ss",new Date(System.currentTimeMillis())));
		loChannel.setData("userId", lsEmailId);
		loChannel.setData("tncdata", ApplicationConstants.AUDIT_TERMSNCONDITIONS_EVENT_DATA +" : Application" );
		loChannel.setData("tncentityType", ApplicationConstants.AUDIT_TERMSNCONDITIONS_ENTITY_TYPE);
		loChannel.setData("tncentityId", asAppId);
		loChannel.setData("tncproviderFlag", ApplicationConstants.AUDIT_APP_SUBMISSION_PROVIDER_FLAG_TRUE);
		loChannel.setData("appId", asAppId);
		loChannel.setData("documentId", asAppId);
		loChannel.setData("asAuditType", ApplicationConstants.AUDIT_TYPE_APPLICATION);
		
		loChannel.setData("asOrgId", asOrgId);
		loChannel.setData("asBussAppId", asAppId);
		loChannel.setData("tncEntityIdentifier", ApplicationConstants.AUDIT_TERMSNCONDITIONS_ENTITY_TYPE);
		loChannel.setData("EntityIdentifier", ApplicationConstants.AUDIT_APP_SUBMISSION_ENTITY_TYPE);
		// Set data in channel to add data in Audit for Application submission workflow initiation
		CommonUtil.addAuditDataToChannel(loChannel, asOrgId, ApplicationConstants.AUDIT_APP_SUBMISSION_EVENT_NAME,
				ApplicationConstants.AUDIT_APP_SUBMISSION_EVENT_TYPE, new Date(System.currentTimeMillis()),	lsEmailId,
				ApplicationConstants.AUDIT_APP_SUBMISSION_EVENT_DATA, ApplicationConstants.AUDIT_APP_SUBMISSION_ENTITY_TYPE,
				asAppId, ApplicationConstants.AUDIT_APP_SUBMISSION_PROVIDER_FLAG_TRUE, asAppId, asAppId,
				ApplicationConstants.AUDIT_TYPE_APPLICATION);
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
		return null;
	}
	
	/**
	 * This method gets map to be rendered.
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
		loMapForRender.put(ApplicationConstants.FILE_TO_INCLUDE, "/WEB-INF/jsp/businessapplication/applicationsubmission.jsp");
		//Checks if the action is applicationSubmit then execute this block
		if (null != asAction && asAction.equalsIgnoreCase("applicationSubmit")){
		    loMapForRender.put("workflowInitated","Application submitrted successfully !!");
		}
		return loMapForRender;
	}
	
	/**
	 * This method gets map to be rendered.
	 * 
	 * @param asAction - the action to be performed
	 * @param asSectionName - current section name
	 * @param aoChannel - channel object with data, will be needed to be passed to MapForRender
	 * @param aoRequest - Action Request
	 * @return the map to be rendered on front end
	 * @throws ApplicationException
	 */
	@Override
	public Map<String, Object> getMapForRender(String asAction, String asSectionName, Channel aoChannel, ActionRequest aoRequest)
			throws ApplicationException
	{
		Map<String,Object> loMapForRender  = new HashMap<String, Object>();
		//Checks if the action is applicationSubmit then execute this block
		if (null != asAction && asAction.equalsIgnoreCase("applicationSubmit")){
		    loMapForRender.put("workflowInitated","Application submitrted successfully !!");
		}
		return loMapForRender;
	}

}
