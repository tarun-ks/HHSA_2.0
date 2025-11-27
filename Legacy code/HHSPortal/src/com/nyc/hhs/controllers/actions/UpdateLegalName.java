package com.nyc.hhs.controllers.actions;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;

import org.apache.commons.lang.StringEscapeUtils;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.BusinessApplicationUtil;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.FileNetOperationsUtils;
import com.nyc.hhs.util.PortalUtil;

/**
 * This class provides the functionality to the user for requesting a change to
 * Organization's Legal Name via Organization Basics screen (Organization Legal
 * Name Change Request)
 * 
 */

public class UpdateLegalName extends BusinessApplication
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
		Channel loChannel = new Channel();
		// Below section executes when show question screen come up for
		// organization profile and when organization try to change the legal
		// name.
		if (asAction != null && asAction.equalsIgnoreCase("showquestion"))
		{
			Map<String, Object> loNewOrgNameMap = new HashMap<String, Object>();
			PortletSession loPortletSession1 = aoRequest.getPortletSession();
			String lsEmailId = (String) loPortletSession1.getAttribute(ApplicationConstants.KEY_SESSION_EMAIL_ID,
					PortletSession.APPLICATION_SCOPE);
			PortletSession loPortletSession = aoRequest.getPortletSession();
			String lsOrgId = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
					PortletSession.APPLICATION_SCOPE);
			P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);

			String lsCurrentOrgLegalName = PortalUtil.parseQueryString(aoRequest, "currentName");
			String lsProposedOrgLegalName = PortalUtil.parseQueryString(aoRequest, "newLegalName");
			String lsNewLegalNameReason = PortalUtil.parseQueryString(aoRequest, "newLegalNameReason");
			loNewOrgNameMap.put("asCurrentOrgLegalName", lsCurrentOrgLegalName);
			loNewOrgNameMap.put("asProposedOrgLegalName", lsProposedOrgLegalName);
			loNewOrgNameMap.put("asNewLegalNameReason", lsNewLegalNameReason);
			loNewOrgNameMap.put("asModifiedDate", new Date(System.currentTimeMillis()));
			loNewOrgNameMap.put("asProcStatus", "In Review");
			loNewOrgNameMap.put("asorgId", lsOrgId);
			loNewOrgNameMap
					.put(P8Constants.PROPERTY_PE_TASK_NAME, ApplicationConstants.ORGANIZATION_LEGAL_NAME_REQUEST);
			String lsReqId = BusinessApplicationUtil.generateReqId();
			loNewOrgNameMap.put(P8Constants.APPLICATION_ID, lsReqId);
			PortletSession loSession = aoRequest.getPortletSession();
			String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);
			loNewOrgNameMap.put(P8Constants.PROPERTY_PE_PROVIDER_ID, lsOrgId);
			String lsProviderName = StringEscapeUtils.unescapeJava(FileNetOperationsUtils.getProviderName(
					(List) BaseCacheManagerWeb.getInstance().getCacheObject(ApplicationConstants.PROV_LIST), lsOrgId));
			loNewOrgNameMap.put(P8Constants.PROPERTY_PE_PROVIDER_NAME, lsProviderName);
			loNewOrgNameMap.put(P8Constants.PROPERTY_PE_LAUNCH_BY, lsUserId);
			loNewOrgNameMap.put(P8Constants.PROPERTY_PE_LAUNCH_DATE, new Date(System.currentTimeMillis()));
			loNewOrgNameMap.put(P8Constants.PROPERTY_PE_PROVIDER_NEW_NAME, lsProposedOrgLegalName);
			loNewOrgNameMap.put("userId", lsUserId);
			loChannel.setData("newOrgNameMap", loNewOrgNameMap);
			loChannel.setData("aoUserSession", loUserSession);
			loChannel.setData("workFlowName", P8Constants.PROPERTY_ORG_LEGAL_NAME_UPDATE_WORKFLOW_NAME);
			loChannel.setData("transaction_name", "insertLegalName");
			loChannel.setData("EntityIdentifier", ApplicationConstants.AUDIT_ORG_NAME_CHANGE_ENTITY_TYPE);
			loChannel.setData("EntityIdentifier", ApplicationConstants.AUDIT_ORG_NAME_CHANGE_ENTITY_TYPE);
			CommonUtil.addAuditDataToChannel(loChannel, lsOrgId, ApplicationConstants.AUDIT_ORG_NAME_CHANGE_EVENT_NAME,
					ApplicationConstants.AUDIT_ORG_NAME_CHANGE_EVENT_TYPE, new Date(System.currentTimeMillis()),
					lsEmailId, ApplicationConstants.AUDIT_ORG_NAME_CHANGE_EVENT_DATA + ": " + lsNewLegalNameReason,
					ApplicationConstants.AUDIT_ORG_NAME_CHANGE_ENTITY_TYPE, lsReqId,
					ApplicationConstants.AUDIT_APP_SUBMISSION_PROVIDER_FLAG_FALSE, lsOrgId, lsOrgId,
					ApplicationConstants.AUDIT_TYPE_GENERAL);
		}
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
		return null;
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
		return null;
	}

	@Override
	/**This method fetches the map to be rendered 
	 * @param asAction
	 *            - the action to be performed
	 * @param asSectionName
	 *            - current section name
	 * @param aoChannel
	 *            - channel object with data, will be needed to be passed to
	 *            MapForRender
	 * @param aoRequest
	 *            - Render Request
	 * @return the map to be rendered on front end
	 * @throws ApplicationException
	 */
	public Map<String, Object> getMapForRender(String asAction, String asSectionName, Channel aoChannel,
			ActionRequest aoRequest) throws ApplicationException
	{
		return null;
	}

}
