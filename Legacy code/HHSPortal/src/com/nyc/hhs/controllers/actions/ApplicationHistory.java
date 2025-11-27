package com.nyc.hhs.controllers.actions;

import java.util.Date;
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
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.FileNetOperationsUtils;

/**
 * This class sets the required values in the channel object, required to
 * execute the transaction for the Application withdrawal. Also it sets the
 * values, required in the in jsp, in the request object.
 * 
 */

public class ApplicationHistory extends BusinessApplication
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
		// Checks if the action is businesswithdraw then execute this block to
		// launch the work flow for withdrawal
		if (asAction != null && asAction.equalsIgnoreCase("businesswithdraw"))
		{
			PortletSession loPortletSession = aoRequest.getPortletSession();
			Map<String, Object> loWithdrawBusinessMap = new HashMap<String, Object>();
			String lsRequester = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);
			String lsComments = aoRequest.getParameter("comments");
			P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			String lsUserId = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_EMAIL_ID,
					PortletSession.APPLICATION_SCOPE);
			String lsProviderName = StringEscapeUtils.unescapeJava(FileNetOperationsUtils.getProviderName(
					(List) BaseCacheManagerWeb.getInstance().getCacheObject(ApplicationConstants.PROV_LIST), asOrgId));
			loWithdrawBusinessMap.put("orgId", asOrgId);
			loWithdrawBusinessMap.put("subDate", DateUtil.getSqlDate(DateUtil.getCurrentDate()));
			loWithdrawBusinessMap.put("sunmittedBy", lsRequester);
			loWithdrawBusinessMap.put("comments", lsComments);
			loWithdrawBusinessMap.put("withdrawlStatus", "In Review");
			loWithdrawBusinessMap.put("applicationStatus", "Withdrawal Requested");
			loWithdrawBusinessMap.put("businessAppId", asAppId);
			loWithdrawBusinessMap.put(P8Constants.PROPERTY_PE_APPLICTION_ID, asAppId);
			loWithdrawBusinessMap.put(P8Constants.PROPERTY_PE_PROVIDER_ID, asOrgId);
			loWithdrawBusinessMap.put(P8Constants.PROPERTY_PE_PROVIDER_NAME, lsProviderName);
			loWithdrawBusinessMap.put(P8Constants.PROPERTY_PE_LAUNCH_BY, lsRequester);
			loWithdrawBusinessMap.put(P8Constants.PROPERTY_PE_TASK_NAME, "Withdrawal Request - Business Application");
			loWithdrawBusinessMap.put("eventName", asAppStatus + " to " + "withdrawal Requested");
			loWithdrawBusinessMap.put("eventType", asAppStatus);
			loWithdrawBusinessMap.put("entityType", asAppStatus);
			loWithdrawBusinessMap.put("entityId", asAppStatus);
			loWithdrawBusinessMap.put("lsStatus", "Pending");
			loWithdrawBusinessMap.put("asProviderVisibilityFlag", "false");
			loWithdrawBusinessMap.put("lsServiceAppId", "");
			// Data Req for superseding_status Table
			loWithdrawBusinessMap.put("entityType", ApplicationConstants.ENTITY_TYPE_BUSINESS_APPLICATION);
			loWithdrawBusinessMap.put("entityId", asAppId);
			loWithdrawBusinessMap.put("event", "Withdrawal");
			loWithdrawBusinessMap.put("flag", "A");
			loWithdrawBusinessMap.put("status", "Withdrawal Requested");
			Date loCurrentTime = new Date(System.currentTimeMillis());
			loWithdrawBusinessMap.put("timeStamp", loCurrentTime);
			loChannel.setData("newWithdrawlBusiness", loWithdrawBusinessMap);
			loChannel.setData("aoUserSession", loUserSession);
			loChannel.setData("workFlowName", P8Constants.PROPERTY_BUSINESS_APPLICATION_WITHDRAWL_WORKFLOW_NAME);
			loChannel.setData("transaction_name", "insertWithdrawlBusiness");
			loChannel.setData("EntityIdentifier", ApplicationConstants.AUDIT_APP_WITHDRAWAL_ENTITY_TYPE);
			CommonUtil.addAuditDataToChannel(loChannel, asOrgId, ApplicationConstants.AUDIT_APP_WITHDRAWAL_EVENT_NAME,
					ApplicationConstants.AUDIT_APP_WITHDRAWAL_EVENT_TYPE, new Date(System.currentTimeMillis()),
					lsUserId, ApplicationConstants.AUDIT_APP_WITHDRAWAL_EVENT_DATA + ": " + lsComments,
					ApplicationConstants.AUDIT_APP_WITHDRAWAL_ENTITY_TYPE, asAppId,
					ApplicationConstants.AUDIT_APP_SUBMISSION_PROVIDER_FLAG_FALSE, asAppId, asAppId,
					ApplicationConstants.AUDIT_TYPE_APPLICATION);
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
		Channel loChannel = new Channel();
		Map<String, String> loDisplayAppHIstoryMap = new HashMap<String, String>();
		loDisplayAppHIstoryMap.put("orgId", asOrgId);
		loDisplayAppHIstoryMap.put("businessAppId", asAppId);
		loDisplayAppHIstoryMap.put("asTermsCond", ApplicationConstants.AUDIT_TERMSNCONDITIONS_EVENT_NAME);
		loDisplayAppHIstoryMap.put("asBappSubm", ApplicationConstants.AUDIT_APP_SUBMISSION_EVENT_NAME);
		loDisplayAppHIstoryMap.put("asSnapShot", ApplicationConstants.SNAPSHOT_NAME);
		loDisplayAppHIstoryMap.put("asBappWith", ApplicationConstants.AUDIT_APP_WITHDRAWAL_EVENT_NAME);
		loDisplayAppHIstoryMap.put("asBappWithCity", ApplicationConstants.AUDIT_APP_WITHDRAWAL_BUSINESS_APPLICATION);
		loDisplayAppHIstoryMap.put("asProvStatusChanged", "Business Application Suspended");
		loDisplayAppHIstoryMap.put("asProvStatusCondApp", "Business Application Conditionally Approved");
		loDisplayAppHIstoryMap.put("providerFlag", "true");
		loChannel.setData("reqAppInfo", loDisplayAppHIstoryMap);
		loChannel.setData("transaction_name", "displayApplicationHistoryInfo");

		return loChannel;
	}

	/**
	 * This method gets map to be rendered.
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

	/**
	 * This method gets map to be rendered.
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
