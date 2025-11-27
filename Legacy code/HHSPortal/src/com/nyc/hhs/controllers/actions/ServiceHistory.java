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
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.FileNetOperationsUtils;

/**
 * This class sets the required values in the channel object, required to
 * execute the transaction for the Service withdrawal and Service Application.
 * Also it sets the values, required in the in jsp, in the request object
 * 
 */

public class ServiceHistory extends BusinessApplication
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
		PortletSession loPortletSession = aoRequest.getPortletSession();
		String lsUserId = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_EMAIL_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsRequester = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsProviderName = StringEscapeUtils.unescapeJava(FileNetOperationsUtils.getProviderName(
				(List) BaseCacheManagerWeb.getInstance().getCacheObject(ApplicationConstants.PROV_LIST), asOrgId));
		// Checks if the action is servicewithdraw then execute this block
		if (asAction != null && asAction.equalsIgnoreCase("servicewithdraw"))
		{
			createChannelForServiceWithdraw(asAppId, asAppStatus, aoRequest, loChannel, lsUserId, asOrgId,
					lsProviderName, lsRequester);
		}
		else if (asAction != null && asAction.equalsIgnoreCase("applicationSubmit"))
		{
			createChannelForServiceSubmission(asOrgId, asAppId, asAppStatus, asAppDataForUpdate, aoRequest, loChannel,
					lsUserId, lsProviderName, lsRequester);
		}
		return loChannel;
	}

	/**
	 * This method creates the channel object for service submission.
	 * 
	 * @param asOrgId - the organization id of the current organization
	 * @param asAppId - Business application id of the application
	 * @param asAppStatus - the current application status
	 * @param asAppDataForUpdate - data to be updated in application
	 * @param aoRequest - Action request
	 * @param aoChannel - channel object to be used for further processing
	 * @param asUserId - current user id
	 * @param lsProviderName - current provider name
	 * @throws ApplicationException
	 */
	private void createChannelForServiceSubmission(String asOrgId, String asAppId, String asAppStatus,
			String asAppDataForUpdate, ActionRequest aoRequest, Channel aoChannel, String asUserId,
			String asProviderName, String asLaunchBy) throws ApplicationException
	{
		Map<String, Object> loWithdrawServiceMap = new HashMap<String, Object>();
		PortletSession loPortletSession1 = aoRequest.getPortletSession();
		String lsRequester = (String) loPortletSession1.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsServiceAppId = (String) aoRequest.getAttribute(ApplicationConstants.SERVICE_APPLICATION_ID);
		String lsApplicationId = (String) loPortletSession1.getAttribute("applicationId",
				PortletSession.APPLICATION_SCOPE);
		// if lsServiceAppId is null then retrieve the same from request
		// object
		if (lsServiceAppId == null)
		{
			lsServiceAppId = (String) aoRequest.getAttribute("lsServiceApplicationId");
		}
		P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		loWithdrawServiceMap.put("orgId", asOrgId);
		loWithdrawServiceMap.put("subDate", DateUtil.getSqlDate(DateUtil.getCurrentDate()));
		loWithdrawServiceMap.put("sunmittedBy", lsRequester);
		loWithdrawServiceMap.put("businessAppId", asAppId);
		loWithdrawServiceMap.put("comments", "Application Submit");
		loWithdrawServiceMap.put("asServiceAppId", lsServiceAppId);
		loWithdrawServiceMap.put("serviceStatus", "In Review");
		loWithdrawServiceMap.put(P8Constants.PROPERTY_PE_APPLICTION_ID, asAppId);
		loWithdrawServiceMap.put(P8Constants.PROPERTY_PE_PARENT_APPLICATION_ID, lsApplicationId);
		loWithdrawServiceMap.put(P8Constants.PROPERTY_PE_PROVIDER_ID, asOrgId);
		loWithdrawServiceMap.put(P8Constants.PROPERTY_PE_PROVIDER_NAME, asProviderName);
		loWithdrawServiceMap.put(P8Constants.PROPERTY_PE_LAUNCH_BY, asLaunchBy);
		loWithdrawServiceMap.put(P8Constants.PROPERTY_PE_TASK_NAME, "");
		loWithdrawServiceMap.put(P8Constants.PROPERTY_PE_SECTION_ID, lsServiceAppId);
		loWithdrawServiceMap.put("eventName", asAppStatus + " to " + "Submission Requested");
		loWithdrawServiceMap.put("lsParentAppId", lsApplicationId);
		loWithdrawServiceMap.put("eventType", asAppStatus);
		loWithdrawServiceMap.put("entityType", asAppStatus);
		loWithdrawServiceMap.put("entityId", asAppStatus);
		loWithdrawServiceMap.put("asProviderVisibilityFlag", "Inserted");
		loWithdrawServiceMap.put("lsServiceAppId", lsServiceAppId);
		loWithdrawServiceMap.put("asServiceAppId", lsServiceAppId);

		aoChannel.setData("lolaunchWorkflowMap", loWithdrawServiceMap);
		aoChannel.setData("aoUserSession", loUserSession);
		aoChannel.setData("workFlowName", P8Constants.PROPERTY_SERVICE_CAPACITY_WORKFLOW_NAME);
		// Transaction required to launch the workflow for Service
		// Submission

		aoChannel.setData("asOrgId", asOrgId);
		aoChannel.setData("asAppId", lsApplicationId);
		aoChannel.setData("asBussAppId", asAppId);
		aoChannel.setData("asUserId", lsRequester);
		aoChannel.setData("lsServiceAppId", lsServiceAppId);
		aoChannel.setData("asServiceAppId", lsServiceAppId);
		// values required to update document table
		aoChannel.setData("asStatus", "In Review");
		aoChannel.setData("aoDate", DateUtil.getSqlDate(DateUtil.getCurrentDate()));
		aoChannel.setData("asBusinessApplicationId", asAppId);
		if (asAppDataForUpdate != null)
		{
			createChannelForServiceReSubmission(asOrgId, asAppId, aoChannel, asUserId, lsServiceAppId);
		}
		else
		{
			createChannelForServiceSubmission(asOrgId, asAppId, aoChannel, asUserId, lsServiceAppId);
		}
	}

	/**
	 * This method creates part of the channel object for service submission.
	 * 
	 * @param asOrgId - the organization id of the current organization
	 * @param asAppId - Business application id of the application
	 * @param aoChannel - channel object to be used for further processing
	 * @param asUserId - current user id
	 * @param lsServiceAppId- current service application id
	 * @throws ApplicationException
	 */
	private void createChannelForServiceSubmission(String asOrgId, String asAppId, Channel aoChannel, String asUserId,
			String asServiceAppId) throws ApplicationException
	{
		// Set data in Channel to Add data in Audit for TnC
		aoChannel.setData("orgId", asOrgId);
		aoChannel.setData("tnceventName", ApplicationConstants.AUDIT_TERMSNCONDITIONS_EVENT_NAME);
		aoChannel.setData("tnceventType", ApplicationConstants.AUDIT_TERMSNCONDITIONS_EVENT_TYPE);
		aoChannel.setData("tncauditDate",
				DateUtil.getFormattedDated("dd/MM/yyyy HH:mm:ss", new Date(System.currentTimeMillis())));
		aoChannel.setData("userId", asUserId);
		aoChannel.setData("tncdata", ApplicationConstants.AUDIT_TERMSNCONDITIONS_EVENT_DATA + " : System");
		aoChannel.setData("tncentityType", ApplicationConstants.AUDIT_TERMSNCONDITIONS_ENTITY_TYPE);
		aoChannel.setData("tncentityId", asAppId);
		aoChannel.setData("tncproviderFlag", ApplicationConstants.AUDIT_APP_SUBMISSION_PROVIDER_FLAG_TRUE);
		aoChannel.setData("appId", asAppId);
		aoChannel.setData("documentId", asAppId);
		aoChannel.setData("asAuditType", ApplicationConstants.AUDIT_TYPE_APPLICATION);
		aoChannel.setData("transaction_name", "launchWorkFlowforServiceSubmission");
		aoChannel.setData("tncEntityIdentifier", ApplicationConstants.AUDIT_TERMSNCONDITIONS_ENTITY_TYPE);
		aoChannel.setData("EntityIdentifier", ApplicationConstants.AUDIT_SERVICE_APP_SUBMISSION_ENTITY_TYPE);
		CommonUtil.addAuditDataToChannel(aoChannel, asOrgId,
				ApplicationConstants.AUDIT_SERVICE_APP_SUBMISSION_EVENT_NAME,
				ApplicationConstants.AUDIT_SERVICE_APP_SUBMISSION_EVENT_TYPE, new Date(System.currentTimeMillis()),
				asUserId, ApplicationConstants.AUDIT_SERVICE_APP_SUBMISSION_EVENT_DATA,
				ApplicationConstants.AUDIT_SERVICE_APP_SUBMISSION_ENTITY_TYPE, asServiceAppId,
				ApplicationConstants.AUDIT_APP_SUBMISSION_PROVIDER_FLAG_TRUE, asAppId, asServiceAppId,
				ApplicationConstants.AUDIT_TYPE_APPLICATION);
	}

	/**
	 * This method creates part of the the channel object for service
	 * re-submission.
	 * 
	 * @param asOrgId - the organization id of the current organization
	 * @param asAppId - Business application id of the application
	 * @param aoChannel - channel object to be used for further processing
	 * @param asUserId - current user id
	 * @param lsServiceAppId- current service application id
	 * @throws ApplicationException
	 */
	private void createChannelForServiceReSubmission(String asOrgId, String asAppId, Channel aoChannel,
			String asUserId, String asServiceAppId) throws ApplicationException
	{
		// Set data in Channel to Add data in Audit for TnC
		aoChannel.setData("orgId", asOrgId);
		aoChannel.setData("tnceventName", ApplicationConstants.AUDIT_TERMSNCONDITIONS_EVENT_NAME
				+ ", After Returned for Revision");
		aoChannel.setData("tnceventType", ApplicationConstants.AUDIT_TERMSNCONDITIONS_EVENT_TYPE
				+ ", After Returned for Revision");
		aoChannel.setData("tncauditDate", new Date(System.currentTimeMillis()));
		aoChannel.setData("userId", asUserId);
		aoChannel.setData("tncdata", ApplicationConstants.AUDIT_TERMSNCONDITIONS_EVENT_DATA
				+ ", After Returned for Revision");
		aoChannel.setData("tncentityType", ApplicationConstants.AUDIT_TERMSNCONDITIONS_ENTITY_TYPE
				+ ", After Returned for Revision");
		aoChannel.setData("tncentityId", asAppId);
		aoChannel.setData("tncproviderFlag", ApplicationConstants.AUDIT_APP_SUBMISSION_PROVIDER_FLAG_TRUE);
		aoChannel.setData("appId", asAppId);
		aoChannel.setData("documentId", asAppId);
		aoChannel.setData("asAuditType", ApplicationConstants.AUDIT_TYPE_APPLICATION);
		aoChannel.setData("transaction_name", "launchWorkFlowforServiceReSubmission");
		aoChannel.setData("tncEntityIdentifier", ApplicationConstants.AUDIT_TERMSNCONDITIONS_ENTITY_TYPE);
		aoChannel.setData("EntityIdentifier", ApplicationConstants.AUDIT_SERVICE_APP_SUBMISSION_ENTITY_TYPE);
		CommonUtil.addAuditDataToChannel(aoChannel, asOrgId,
				ApplicationConstants.AUDIT_SERVICE_APP_SUBMISSION_EVENT_NAME + ", After Returned for Revision",
				ApplicationConstants.AUDIT_SERVICE_APP_SUBMISSION_EVENT_TYPE + ", After Returned for Revision",
				new Date(System.currentTimeMillis()), asUserId,
				ApplicationConstants.AUDIT_SERVICE_APP_SUBMISSION_EVENT_DATA + " After Returned for Revision",
				ApplicationConstants.AUDIT_SERVICE_APP_SUBMISSION_ENTITY_TYPE, asAppId,
				ApplicationConstants.AUDIT_APP_SUBMISSION_PROVIDER_FLAG_TRUE, asAppId, asServiceAppId,
				ApplicationConstants.AUDIT_TYPE_APPLICATION);
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
	private void createChannelForServiceWithdraw(String asAppId, String asAppStatus, ActionRequest aoRequest,
			Channel aoChannel, String asUserId, String asOrgId, String asProviderName, String asLaunchBy)
			throws ApplicationException
	{
		Map<String, Object> loWithdrawServiceMap = new HashMap<String, Object>();
		PortletSession loPortletSession1 = aoRequest.getPortletSession();
		String lsRequester = (String) loPortletSession1.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsComments = aoRequest.getParameter("comments");
		String lsServiceAppId = (String) aoRequest.getAttribute(ApplicationConstants.SERVICE_APPLICATION_ID);// "sr_13444084329531";//
		P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		loWithdrawServiceMap.put("orgId", asOrgId);
		loWithdrawServiceMap.put("subDate", DateUtil.getSqlDate(DateUtil.getCurrentDate()));
		loWithdrawServiceMap.put("sunmittedBy", lsRequester);
		loWithdrawServiceMap.put("comments", lsComments);
		loWithdrawServiceMap.put("withdrawlStatus", "In Review");
		loWithdrawServiceMap.put("serviceStatus", "Withdrawal Requested");
		loWithdrawServiceMap.put("businessAppId", asAppId);
		loWithdrawServiceMap.put("asServiceAppId", lsServiceAppId);
		loWithdrawServiceMap.put(P8Constants.PROPERTY_PE_APPLICTION_ID, asAppId);
		loWithdrawServiceMap.put(P8Constants.PROPERTY_PE_SECTION_ID, lsServiceAppId);
		loWithdrawServiceMap.put(P8Constants.PROPERTY_PE_PROVIDER_ID, asOrgId);
		loWithdrawServiceMap.put(P8Constants.PROPERTY_PE_PROVIDER_NAME, asProviderName);
		loWithdrawServiceMap.put(P8Constants.PROPERTY_PE_LAUNCH_BY, asLaunchBy);
		loWithdrawServiceMap.put(P8Constants.PROPERTY_PE_TASK_NAME, "Withdrawal Request - Service Application - ");
		loWithdrawServiceMap.put("eventName", asAppStatus + " to " + "withdrawal Requested");
		loWithdrawServiceMap.put("eventType", asAppStatus);
		loWithdrawServiceMap.put("entityType", asAppStatus);
		loWithdrawServiceMap.put("entityId", asAppStatus);
		loWithdrawServiceMap.put("asProviderVisibilityFlag", "true");
		loWithdrawServiceMap.put("lsServiceAppId", lsServiceAppId);
		// Data Req for superseding_status Table
		loWithdrawServiceMap.put("entityType", ApplicationConstants.ENTITY_TYPE_SERVICE_APPLICATION);
		loWithdrawServiceMap.put("entityId", lsServiceAppId);
		loWithdrawServiceMap.put("event", "Withdrawal");
		loWithdrawServiceMap.put("flag", "A");
		loWithdrawServiceMap.put("status", "Withdrawal Requested");
		Date loCurrentTime = new Date(System.currentTimeMillis());
		loWithdrawServiceMap.put("timeStamp", loCurrentTime);
		aoChannel.setData("newWithdrawlService", loWithdrawServiceMap);
		aoChannel.setData("aoUserSession", loUserSession);
		aoChannel.setData("workFlowName", P8Constants.PROPERTY_SERVICE_APPLICATION_WITHDRAWL_WORKFLOW_NAME);
		// Transaction required to launch the workflow for Service
		// withdrawal
		aoChannel.setData("transaction_name", "insertWithdrawlService");
		aoChannel.setData("EntityIdentifier", ApplicationConstants.AUDIT_SERVICE_APP_WITHDRAWAL_ENTITY_TYPE);
		CommonUtil.addAuditDataToChannel(aoChannel, asOrgId,
				ApplicationConstants.AUDIT_SERVICE_APP_WITHDRAWAL_EVENT_NAME,
				ApplicationConstants.AUDIT_SERVICE_APP_WITHDRAWAL_EVENT_TYPE, new Date(System.currentTimeMillis()),
				asUserId, ApplicationConstants.AUDIT_SERVICE_APP_WITHDRAWAL_EVENT_DATA + ": " + lsComments,
				ApplicationConstants.AUDIT_SERVICE_APP_WITHDRAWAL_ENTITY_TYPE, lsServiceAppId,
				ApplicationConstants.AUDIT_APP_SUBMISSION_PROVIDER_FLAG_FALSE, asAppId, lsServiceAppId,
				ApplicationConstants.AUDIT_TYPE_APPLICATION);
	}

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
		String lsServiceAppId = (String) aoRequest.getAttribute(ApplicationConstants.SERVICE_APPLICATION_ID);
		Map<String, String> loDisplayAppHIstoryMap = new HashMap<String, String>();
		loDisplayAppHIstoryMap.put("orgId", asOrgId);
		loDisplayAppHIstoryMap.put("serviceAppId", lsServiceAppId);
		loDisplayAppHIstoryMap.put("providerFlag", "true");
		loChannel.setData("reqAppInfo", loDisplayAppHIstoryMap);
		loChannel.setData("transaction_name", "displayServiceApplicationHistoryInfo");

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

		return null;
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
