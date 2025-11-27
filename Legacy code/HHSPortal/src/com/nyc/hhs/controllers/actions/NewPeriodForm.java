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


/**
 * This script sets the required values in the channel object, required to
 * execute the transaction for updating the accounting period. Also sets the
 * values, required in the in jsp, in the request object.
 * 
 */

public class NewPeriodForm extends BusinessApplication {
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

		PortletSession loPortletSession = aoRequest.getPortletSession();
		String lsOrgId = (String) loPortletSession.getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ORG,
				PortletSession.APPLICATION_SCOPE);
		Channel loChannel = new Channel();
		// Checks if the action is showquestion then execute this block to set
		// values in channel object to update accounting period
		if (asAction != null && asAction.equalsIgnoreCase("showquestion")) {
			String lsNewFromMonth = aoRequest.getParameter("Frommonth");
			String lsNewToMonth = aoRequest.getParameter("Tomonth");
			
			String lsUserId = (String) loPortletSession.getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);
			Map<String, Object> loNewAccountPeriodMap = new HashMap<String, Object>();
			loNewAccountPeriodMap.put("providerId", lsOrgId);
			loNewAccountPeriodMap.put("uploadOrder", 2);
			loNewAccountPeriodMap.put("newStartMonth", lsNewFromMonth);
			loNewAccountPeriodMap.put("newEndMonth", lsNewToMonth);
			loNewAccountPeriodMap.put("effectiveYear", aoRequest.getParameter("effectiveYear"));
			loNewAccountPeriodMap.put("userId", lsUserId);
			loChannel.setData("aoNewAccountPeriodMap", loNewAccountPeriodMap);
			
			loChannel.setData("transaction_name", "fetchAccReqInfo");
			loChannel.setData("EntityIdentifier", ApplicationConstants.AUDIT_ORG_ACCOUNTING_PERIOD_CHANGE_ENTITY_TYPE);
			CommonUtil
					.addAuditDataToChannel(
							loChannel,
							lsOrgId,
							ApplicationConstants.AUDIT_ORG_ACCOUNTING_PERIOD_CHANGE_EVENT_NAME,
							ApplicationConstants.AUDIT_ORG_ACCOUNTING_PERIOD_CHANGE_EVENT_TYPE,
							new Date(System.currentTimeMillis()),
							lsUserId,
							ApplicationConstants.AUDIT_ORG_ACCOUNTING_PERIOD_CHANGE_EVENT_DATA,
							ApplicationConstants.AUDIT_ORG_ACCOUNTING_PERIOD_CHANGE_ENTITY_TYPE,
							lsOrgId,
							ApplicationConstants.AUDIT_APP_SUBMISSION_PROVIDER_FLAG_FALSE,
							lsOrgId, lsOrgId,
							ApplicationConstants.AUDIT_TYPE_GENERAL);
		} else if (asAction != null
				&& asAction.equalsIgnoreCase("getOldPeriod")) {
			loChannel.setData("asOrgId", lsOrgId);
			loChannel.setData("asProviderId", lsOrgId);
			loChannel.setData("transaction_name", "getOldPeriod");
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
	public Channel getChannelObject(String asSectionName, String asOrgId,
			String asAppId, String asAppStatus, String asAppDataForUpdate,
			String asAction, String asUserRole, RenderRequest aoRequest,
			String asTaxonomyName) throws ApplicationException {
		return null;
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

		return null;
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
