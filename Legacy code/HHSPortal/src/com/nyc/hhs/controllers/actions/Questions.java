package com.nyc.hhs.controllers.actions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.PortletContext;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;

import com.accenture.constants.ObjectModelConstants;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.PropertyUtil;

/**
 * This class sets the required values in the channel object, required to execute the 
 * transaction for displaying the questions and to save the questions. Also it sets the 
 * values, required in the in jsp, in the request object.
 *
 */

public class Questions extends BusinessApplication {
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
			String asAppDataForUpdate, String asAction,String asUserRole, ActionRequest aoRequest, String asTaxonomyName) 
	throws ApplicationException {

		PortletSession loPortletSession = aoRequest.getPortletSession(true);
		
		String lsUserId = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
		Channel loChannelobj = null;
		//Block of code to be executed if asAppStatus contains draft
		if(asAppStatus != null && asAppStatus.contains(ApplicationConstants.STATUS_DRAFT.toLowerCase()) && asAppId!=null){
			loPortletSession.setAttribute("user_roles", "pub_user_role", PortletSession.APPLICATION_SCOPE);// for edit
		}
		//Checks if the action is save or save_next then execute this block
		if (asAction.equalsIgnoreCase(ApplicationConstants.SAVE_QUESTION) || asAction.equalsIgnoreCase(ApplicationConstants.SAVE_QUESTION_SHOW_DOCUMENT)){
			loChannelobj = new Channel();
			String lsFormName = aoRequest.getParameter(ApplicationConstants.FB_TAG + ApplicationConstants.FILE_NAME);
			
			String lsFormVersion = aoRequest.getParameter(ApplicationConstants.FB_TAG + ApplicationConstants.FORM_VERSION_SMALL_CAPS);

			PortletContext loContext = aoRequest.getPortletSession(true).getPortletContext();
			String lsPath = loContext.getRealPath(PropertyUtil.getDeployedFormLocation(lsFormName, lsFormVersion, false));
			String lsQuestionPath = loContext.getRealPath(PropertyUtil.getDeployedFormLocation(lsFormName, lsFormVersion, true));
			String lsBusinessRulePath = loContext.getRealPath(ApplicationConstants.FORMS_FOLDER_NAME) + ApplicationConstants.BUSINESS_RULE_XML;
			String lsTableName = PropertyUtil.getTableName(asSectionName);

			HashMap<String, Object> loHMRequest = getRequestMap(aoRequest);
			loChannelobj.setData(ApplicationConstants.BUSINESS_APPLICATION_TABLE_NAME, lsTableName);
			loChannelobj.setData(ApplicationConstants.QUES_PATH, lsQuestionPath);
			loChannelobj.setData(ApplicationConstants.BUSINESS_RULE_XML_PATH, lsBusinessRulePath);
			loChannelobj.setData(ApplicationConstants.TEMPLATE_PATH, lsPath);
			loChannelobj.setData(ApplicationConstants.PARAMS, loHMRequest);
			loChannelobj.setData(ApplicationConstants.USER_ROLE, "edit");
			loChannelobj.setData(ApplicationConstants.VALIDATE_CLASS, null);
			loChannelobj.setData(ApplicationConstants.FORM_VERSION, lsFormVersion);
			loChannelobj.setData("asValidationClass", "com.hhs.formbuilder.FormBuilderValidation");
			if("OrgProfile".equalsIgnoreCase(lsFormName)){
				lsFormName = "Basic";
			}
			loChannelobj.setData(ApplicationConstants.FORMNAME, lsFormName);
			loChannelobj.setData(ApplicationConstants.APP_ID, asAppId);
			loChannelobj.setData(ApplicationConstants.USER_ID, lsUserId);
			loChannelobj.setData(ApplicationConstants.ORG_ID, asOrgId);
			if(asSectionName.equalsIgnoreCase(ApplicationConstants.BUSINESS_APPLICATION_SECTION_BASICS))
			{
				loChannelobj.setData("asTitle", ApplicationConstants.CFO_TITLE);
			}
			loChannelobj.setData("asSection", asSectionName);
			loChannelobj.setData("asAppStatus", asAppStatus);
			
			if(null != loHMRequest && !loHMRequest.isEmpty() && loHMRequest.get("section").toString().equalsIgnoreCase("basics") && loHMRequest.containsKey("OLN"))
			{
				P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
						ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
				loChannelobj.setData("aoFilenetSession", loUserSession);
			}
		}
		return loChannelobj;
	}

	/**
	 * This method fetches the hashmap of data
	 * 
	 * @param aoReq - Render Request
	 * @return the hashmap of data(fetched from request)
	 * @throws ApplicationException
	 */
	public static HashMap<String, Object> getRequestMap(ActionRequest aoReq) throws ApplicationException
	{

		HashMap<String, Object> loHashParse = new HashMap<String, Object>();
		HashMap<String, String> loHMHiddenVarForLine = new HashMap<String, String>();
		Map loMapReq = aoReq.getParameterMap();

		Iterator loIter = loMapReq.keySet().iterator();
		String lsActionVal = null;
		String lsFieldName = null;

		while (loIter.hasNext())
		{

			lsFieldName = (String) loIter.next();
			lsActionVal = (String) aoReq.getParameter(lsFieldName);
			//Block of code to be executed if lsFieldName starts with lineElt and contains '~'
			if (lsFieldName.startsWith(ApplicationConstants.LINE_ELT) && lsFieldName.indexOf(ApplicationConstants.TILD) != -1)
			{
				String[] loSplitedFiledName = lsFieldName.split(ApplicationConstants.TILD);
				if (null != loHMHiddenVarForLine && loHMHiddenVarForLine.containsKey(loSplitedFiledName[0]))
				{
					StringBuffer loSBLineIds = new StringBuffer(loHMHiddenVarForLine.get(loSplitedFiledName[0]));
					loSBLineIds.append(ApplicationConstants.COMMA).append(lsActionVal);
					loHMHiddenVarForLine.put(loSplitedFiledName[0], loSBLineIds.toString());
				}
				else
				{
					String lsLineIds = lsActionVal;
					loHMHiddenVarForLine.put(loSplitedFiledName[0], lsLineIds);
				}
			}
			else
			{
				if (loHashParse.containsKey(lsFieldName) && null != lsActionVal && !lsActionVal.isEmpty())
				{
					StringBuffer loBuffer = new StringBuffer((String) loHashParse.get(lsFieldName));
					loBuffer.append(ObjectModelConstants.STRING_SEPARATER).append(lsActionVal);
					lsActionVal = loBuffer.toString();
				}
				loHashParse.put(lsFieldName, lsActionVal);
			}
		}
		//Block of code to be executed if loHMHiddenVarForLine contains at least one element
		if (!loHMHiddenVarForLine.isEmpty())
		{
			loHashParse.putAll(loHMHiddenVarForLine);
		}
		aoReq.setAttribute(ObjectModelConstants.UPLOAD_KEY, loHashParse);

		return loHashParse;
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
			String asAction, String asUserRole, RenderRequest aoRequest, String asTaxonomyName)
	throws ApplicationException {
		String lsSessionUserName=null;
		String lsUserId=null;
		//Block of code to be executed if aoRequest is not null
		if(aoRequest!=null){
			PortletSession loPortletSession = aoRequest.getPortletSession(true);
			lsSessionUserName = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			lsUserId = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
		}

		Channel loChannelobj = null;

		
		String lsTableName = PropertyUtil.getTableName(asSectionName);
		//Checks if the action is showquestion then execute this block
		if (asAction.equalsIgnoreCase(ApplicationConstants.SHOW_QUESTION)){
			loChannelobj = new Channel();
			HashMap<String,Object> loRequiredProps =new HashMap<String, Object>();
			loRequiredProps.put("appid", asAppId);
			loRequiredProps.put("orgid", asOrgId);
			loRequiredProps.put("sectionId", asSectionName);
			loChannelobj.setData("aoRequiredProps", loRequiredProps);
			loChannelobj.setData(ApplicationConstants.BUSINESS_APPLICATION_TABLE_NAME, lsTableName);
			loChannelobj.setData(ApplicationConstants.APPID, asAppId);
			loChannelobj.setData(ApplicationConstants.USER_ID, lsUserId);
			loChannelobj.setData(ApplicationConstants.CREATED_BY, lsSessionUserName);
			loChannelobj.setData(ApplicationConstants.CREATED_DATE, DateUtil.getCurrentDate());
			loChannelobj.setData(ApplicationConstants.UPDATED_BY, null);
			loChannelobj.setData(ApplicationConstants.UPDATED_DATE, null);
			loChannelobj.setData(ApplicationConstants.STATUS, asAppStatus);//ApplicationConstants.START_STATUS
			loChannelobj.setData(ApplicationConstants.ORG_ID, asOrgId);
			loChannelobj.setData("asSection", asSectionName);
			loChannelobj.setData("asAppStatus", asAppStatus);
			HashMap loLastCommentReqProps = new HashMap();
			loLastCommentReqProps.put("appid", asAppId);
			loLastCommentReqProps.put("asProviderVisibilityFlag", "true");
			loLastCommentReqProps.put("sectionId", asSectionName);
			loLastCommentReqProps.put("providerComments", P8Constants.PROPERTY_PE_TH_PROVIDER_COMMENT);
			loChannelobj.setData("aoRequiredProps", loLastCommentReqProps);
		}
		return loChannelobj;
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
	public Map<String, Object> getMapForRender(String asAction,String asSectionName, Channel aoChannel, RenderRequest aoRequest) throws ApplicationException {
		Map<String,Object> loMapForRender = new HashMap<String, Object>();
		String lsMenu = getSubMenu(asSectionName);
		loMapForRender.put(ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_MENU, lsMenu);
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
