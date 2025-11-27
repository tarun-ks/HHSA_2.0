package com.nyc.hhs.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.StaffDetails;
import com.nyc.hhs.model.UserBean;
import com.nyc.hhs.model.UserThreadLocal;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.FileNetOperationsUtils;

@Controller(value = "multiRoleController")
@RequestMapping("view")
public class MultiRoleController extends BaseController
{
	/**
	 * This the log object which is used to log any error into log file when any
	 * exception occurred
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(MultiRoleController.class);

	/**
	 * Default Render Action Created 
	 * @param aoRequest Render Request
	 * @param aoResponse Render Response
	 * @return Model and View
	 */
	@RenderMapping
	protected ModelAndView handleRenderRequestInternal(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		return getSelectRoleRenderRequest(aoRequest, aoResponse);
	}

	/**
	 * Default Resource Action Created 
	 * @param aoRequest Action Request
	 * @param aoResponse Action Response
	 * @throws ApplicationException return ModelAndView
	 */
	@Override
	@ResourceMapping
	public ModelAndView handleResourceRequest(ResourceRequest aoRequest, ResourceResponse aoResponse)
			throws ApplicationException
	{
		ModelAndView loModelandView = new ModelAndView(generateSelectRoleScreen(aoRequest, aoResponse));
		return loModelandView;
	}

	/**
	 * Select Role Render Action
	 * @param aoRequest Action Request
	 * @param aoResponse Action Response return Model And View Created for R4
	 */
	@RenderMapping(params = "render_action=" + ApplicationConstants.SELECT_OVERSIGHT_ROLE)
	protected ModelAndView getSelectRoleRenderRequest(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		setExceptionMessageInResponse(aoRequest);
		ModelAndView loModelandView = new ModelAndView(generateSelectRoleScreen(aoRequest, aoResponse));
		return loModelandView;
	}

	/**
	 * This method generates Select Role Screen for User with access to
	 * multiple roles.
	 * <ul>
	 * <li>This method sets aoRequest</li>
	 * <li>If loStaffDetailsBeanList is not null then iterate the BeanList and
	 * set the map accordingly</li>
	 * </ul>
	 * 
	 * @param aoRequest Action Request
	 * @param aoResponse Action Response return String
	 */
	@SuppressWarnings("unchecked")
	protected String generateSelectRoleScreen(PortletRequest aoRequest, PortletResponse aoResponse)
	{
		PortletSession loSession = aoRequest.getPortletSession();
		String lsLoginPagePath = ApplicationConstants.SELECT_OVERSIGHT_ROLE;
		String orgName_original = (String)loSession.getAttribute( ApplicationConstants.KEY_SESSION_ORG_NAME_ORIGINAL, PortletSession.APPLICATION_SCOPE);
		// Star QC 9093 - remove escape character from agency name
		orgName_original = orgName_original.replaceAll("\\\\", "");
		// end QC 9093 - remove escape character from agency name
				
		Map<String, String> loMultiRoleMap = new HashMap<String, String>();
		try
		{
			
			List<StaffDetails> loStaffDetailsBeanList = (List<StaffDetails>) loSession.getAttribute(
					ApplicationConstants.STAFF_DETAILS_BEAN_LIST_PARAM, PortletSession.APPLICATION_SCOPE);
			
			LOG_OBJECT.Debug("STAFF_DETAILS_BEAN_LIST_PARAM  "+ loStaffDetailsBeanList);
			
			String role_current = (String)loSession.getAttribute( ApplicationConstants.KEY_SESSION_ROLE_CURRENT, PortletSession.APPLICATION_SCOPE);
			LOG_OBJECT.Debug("ROLE_CURRENT :: "+role_current);
			String role_original = (String)loSession.getAttribute( ApplicationConstants.KEY_SESSION_ROLE_ORIGINAL, PortletSession.APPLICATION_SCOPE);
			LOG_OBJECT.Debug("ROLE_ORIGINAL :: "+role_original);
			
		    //loMultiRoleMap.put("ROLE_CURRENT", role_current);
			if (role_current.equalsIgnoreCase(role_original))
			{
				loMultiRoleMap.put("ROLE_OBSERVER", "Read Only") ; //ApplicationConstants.ROLE_OBSERVER);
			}
			else
			{
				loMultiRoleMap.put("ROLE_ORIGINAL", orgName_original); //role_original);
			}
		    
		    aoRequest.setAttribute(ApplicationConstants.ROLE_DETAILS_MAP, loMultiRoleMap);
		    			
		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error(ApplicationConstants.ERROR_MSG_SELECT_ORGANIZATION_LOAD_FAIL + loEx);
			setGenericErrorMessage(aoRequest);
		}

		return lsLoginPagePath;
		
		

		
	}

    /**
     * This is submit action for Multi-Account Login Created for R4
     * @param aoRequest Action Request
     * @param aoResponse Action Response
     */
    @ActionMapping(params = "submit_action=multiRole")
    /**
     * This method selects Organization Login
     * @param aoRequest Action Request
     * @param aoResponse Action Response
     */
    protected void selectRoleLoginAction(ActionRequest aoRequest, ActionResponse aoResponse)
    {
        selectSwitchRoleLoginAction(aoRequest, aoResponse);
    }
    
    /**
	 * This method select Switch Role Login
	 * <ul>
	 * <li>If lsUserDn is not null and length is greater than 0 then set the
	 * StaffDetails list</li>
	 * <li>If the 'if' condition is satisfied then iterate the StaffDetails List
	 * </li>
	 * <li>The control is redirected to different page</li>
	 * </ul>
	 * @param aoRequest Action Request
	 * @param aoResponse Action Response return String
	 */
    @SuppressWarnings("unchecked")
    protected String selectSwitchRoleLoginAction(ActionRequest aoRequest, ActionResponse aoResponse)
    {	
    	PortletSession loSession = aoRequest.getPortletSession();
        String lsRoleId = ApplicationConstants.EMPTY_STRING;
       	String lsRole = ApplicationConstants.EMPTY_STRING;
		String lsAgencyOrCityHomePagePath = ApplicationConstants.EMPTY_STRING;
		String lsActionStatus = ApplicationConstants.SUCCESS;
		String lsUserDn = null;
		
		UserBean loUserBean = (UserBean) loSession.getAttribute(ApplicationConstants.GET_USER_ROLES,
				PortletSession.APPLICATION_SCOPE);
								
		try
		{
			lsRoleId = aoRequest.getParameter(ApplicationConstants.ROLEIDKEY); //ROLE_OBSERVER/ROLE_ORIGINAL
			lsRole = aoRequest.getParameter(ApplicationConstants.TYPEAHEADBOX); 
			
			if (null != lsRoleId && null != lsRole)
			{
				String role_current = (String)loSession.getAttribute( ApplicationConstants.KEY_SESSION_ROLE_CURRENT, PortletSession.APPLICATION_SCOPE);
				LOG_OBJECT.Debug("ROLE_CURRENT :: "+role_current);
				String role_original = (String)loSession.getAttribute( ApplicationConstants.KEY_SESSION_ROLE_ORIGINAL, PortletSession.APPLICATION_SCOPE);
				String orgName_original = (String)loSession.getAttribute( ApplicationConstants.KEY_SESSION_ORG_NAME_ORIGINAL, PortletSession.APPLICATION_SCOPE);
				String userName_original = (String)loSession.getAttribute( ApplicationConstants.KEY_SESSION_USER_NAME_ORIGINAL, PortletSession.APPLICATION_SCOPE);
				String email_original = (String)loSession.getAttribute( ApplicationConstants.KEY_SESSION_EMAIL_ID_ORIGINAL, PortletSession.APPLICATION_SCOPE);
				String dn_original = (String)loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_DN_ORIGINAL, PortletSession.APPLICATION_SCOPE);
				lsActionStatus = ApplicationConstants.SUCCESS;
				
				if (lsRoleId.equalsIgnoreCase("ROLE_OBSERVER"))
				{
					StaffDetails loUserDetails = fetchCityUserDetail(ApplicationConstants.READONLY_EMAIL);
					loUserBean.setMsOrgType(ApplicationConstants.CITY_ORG);
		            loUserBean.setMsOrgName(ApplicationConstants.CITY);
		            loUserBean.setMsRole(loUserDetails.getMsStaffRole()); 
		            loUserBean.setMsOrgId(ApplicationConstants.CITY);
		            loUserBean.setMsUserName(loUserDetails.getMsStaffFirstName().concat(" ").concat(loUserDetails.getMsStaffLastName()));
		            loUserBean.setMsUserId(loUserDetails.getMsStaffId());
	    			loUserBean.setMsUserEmail(loUserDetails.getMsStaffEmail());
	    			loUserBean.setMsRole(loUserDetails.getMsStaffRole());
	    			loUserBean.setMsOrgType(loUserDetails.getMsUserType());
	    			     	
		            updateUserbeanCity("staff",  loUserBean);
		            loSession.removeAttribute(ApplicationConstants.KEY_SESSION_ROLE_CURRENT);
			        loSession.setAttribute(ApplicationConstants.KEY_SESSION_ROLE_CURRENT, "OBSERVER", PortletSession.APPLICATION_SCOPE);
			       
			        lsAgencyOrCityHomePagePath = aoRequest.getScheme() + ApplicationConstants.NOTIFICATION_HREF_1
					   												 + aoRequest.getServerName() + HHSConstants.COLON + aoRequest.getServerPort()
					   												 + aoRequest.getContextPath() + ApplicationConstants.PORTAL_URL
					   												+ "&_pageLabel=portlet_hhsweb_portal_page_city_home";
			   
				}
				
				if (lsRoleId.equalsIgnoreCase("ROLE_ORIGINAL") )
				{
					// set up user bean for ORIGINAL_ROLE
					
					StaffDetails loUserDetails = fetchCityUserDetail(email_original);
					LOG_OBJECT.Debug("UserId from StaffDetails :: "+loUserDetails.getMsStaffId());
					
			        loUserBean.setMsOrgType(loUserDetails.getMsUserType());
			        loUserBean.setMsRole(role_original);
			        loUserBean.setMsUserId(loUserDetails.getMsStaffId());
			        loUserBean.setMsUserName(loUserDetails.getMsStaffFirstName().concat(" ").concat(loUserDetails.getMsStaffLastName()));
			        loUserBean.setMsUserEmail(loUserDetails.getMsStaffEmail());
			        loUserBean.setMsOrgId(loUserDetails.getMsOrganisationName());
			        loUserBean.setMsOrgType(loUserDetails.getMsUserType());
					loUserBean.setMsOrgName(orgName_original);
					loUserBean.setMsLoginId(loUserDetails.getMsStaffEmail());
					
			        loUserBean.setMsOrgName(FileNetOperationsUtils.getAgencyName((TreeSet) BaseCacheManagerWeb.getInstance()
			                  .getCacheObject(ApplicationConstants.AGENCY_LIST), loUserDetails.getMsOrganisationName()));
			        
			        loSession.removeAttribute(ApplicationConstants.KEY_SESSION_ROLE_CURRENT);
			        loSession.setAttribute(ApplicationConstants.KEY_SESSION_ROLE_CURRENT, role_original, PortletSession.APPLICATION_SCOPE);
			        
			        lsAgencyOrCityHomePagePath = aoRequest.getScheme() + ApplicationConstants.NOTIFICATION_HREF_1
																	   + aoRequest.getServerName() + HHSConstants.COLON + aoRequest.getServerPort()
																	   + aoRequest.getContextPath() + ApplicationConstants.PORTAL_URL
																	   + "&_pageLabel=";
	       		            
					if ("Manager".equalsIgnoreCase(role_original) || "Staff".equalsIgnoreCase(role_original) )
					{
						lsAgencyOrCityHomePagePath = lsAgencyOrCityHomePagePath.concat("portlet_hhsweb_agency_r1");
					}
					else
					{
						lsAgencyOrCityHomePagePath = lsAgencyOrCityHomePagePath.concat("portlet_hhsweb_portal_page_agency_home");
					}
					
				}
			    				
				lsUserDn = (String) loSession.getAttribute(ApplicationConstants.USER_DN,
						PortletSession.APPLICATION_SCOPE);
				
				if ( ApplicationConstants.SUCCESS.equalsIgnoreCase(lsActionStatus))
				{
					aoRequest.setAttribute("user_type", loUserBean.getMsOrgType());
					createRoleMappingMap(loSession, loUserBean);
					
					loSession.setAttribute(ApplicationConstants.KEY_SESSION_USER_ID, loUserBean.getMsUserId(), PortletSession.APPLICATION_SCOPE);
					loSession.setAttribute(ApplicationConstants.KEY_SESSION_USER_VALIDATED, ApplicationConstants.TRUE, PortletSession.APPLICATION_SCOPE);
					
					UserThreadLocal.setUser(loUserBean.getMsUserId());
					loSession.setAttribute(ApplicationConstants.KEY_SESSION_USER_ROLE, loUserBean.getMsRole(),	PortletSession.APPLICATION_SCOPE);
					loSession.setAttribute(ApplicationConstants.KEY_SESSION_USER_ORG, loUserBean.getMsOrgId(),	PortletSession.APPLICATION_SCOPE);
					loSession.setAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE, loUserBean.getMsOrgType(), PortletSession.APPLICATION_SCOPE);
					loSession.setAttribute(ApplicationConstants.KEY_SESSION_ORG_NAME, loUserBean.getMsOrgName(), PortletSession.APPLICATION_SCOPE);
					loSession.setAttribute(ApplicationConstants.KEY_SESSION_EMAIL_ID, loUserBean.getMsUserEmail(),	PortletSession.APPLICATION_SCOPE);
																	
					if ( ApplicationConstants.CITY_ORG.equalsIgnoreCase(loUserBean.getMsOrgType()) )
					{
						loSession.setAttribute(ApplicationConstants.KEY_SESSION_USER_NAME, loUserBean.getMsUserName(),	PortletSession.APPLICATION_SCOPE);
						
						HashMap<String, String> loUserHashMap = fetchCityUserDetails();
						loSession.removeAttribute("UserMap",  PortletSession.APPLICATION_SCOPE);
						loSession.setAttribute("UserMap", loUserHashMap, PortletSession.APPLICATION_SCOPE);
						loSession.setAttribute(ApplicationConstants.KEY_SESSION_ROLE_CURRENT, ApplicationConstants.ROLE_OBSERVER, PortletSession.APPLICATION_SCOPE);
					}
					else
					{
						if ( ApplicationConstants.AGENCY_ORG.equalsIgnoreCase(loUserBean.getMsOrgType()))
						{
							loSession.setAttribute(ApplicationConstants.KEY_SESSION_USER_NAME, loUserBean.getMsUserName(),	PortletSession.APPLICATION_SCOPE);
						}
																				
						loSession.setAttribute("userDN", dn_original, PortletSession.APPLICATION_SCOPE);
						loSession.removeAttribute("UserMap", PortletSession.APPLICATION_SCOPE);
						loSession.setAttribute(ApplicationConstants.KEY_SESSION_ROLE_CURRENT, role_original, PortletSession.APPLICATION_SCOPE);
					}
														
					aoRequest.setAttribute("user_type", loUserBean.getMsOrgType());
					lsAgencyOrCityHomePagePath = lsAgencyOrCityHomePagePath+"&app_menu_name=home_icon";
					LOG_OBJECT.Debug("Redirect to ::  "+lsAgencyOrCityHomePagePath);					
					aoResponse.sendRedirect(lsAgencyOrCityHomePagePath);
				}
				else
				{
					lsActionStatus = HHSConstants.AS_FAILURE + ApplicationConstants.ERROR_MSG_UNABLE_TO_REDIRECT;
				}
			}
		}	
		catch (ApplicationException loExp)
		{
			setGenericErrorMessage(aoRequest);
		}
		catch (Exception loEx)
		{
			lsActionStatus = HHSConstants.AS_FAILURE;
			LOG_OBJECT.Error(ApplicationConstants.ERROR_MSG_UNABLE_TO_REDIRECT + loEx);
			setExceptionMessageFromAction((ActionResponse) aoResponse,
					ApplicationConstants.ERROR_WHILE_PROCESSING_REQUEST, ApplicationConstants.MESSAGE_FAIL_TYPE, null,
					null);
			aoResponse.setRenderParameter(ApplicationConstants.RENDER_ACTION,
					ApplicationConstants.RENDER_ACTION_SELECT_ROLE);
		}
		finally
		{
			if (null != lsActionStatus && lsActionStatus.startsWith(HHSConstants.AS_FAILURE))
			{
				setExceptionMessageFromAction((ActionResponse) aoResponse,
						ApplicationConstants.ERROR_MSG_AUTHORIZATION2, ApplicationConstants.MESSAGE_FAIL_TYPE, null,
						null);
				aoResponse.setRenderParameter(ApplicationConstants.RENDER_ACTION,
						ApplicationConstants.RENDER_ACTION_SELECT_ROLE);
			}
		}
		return lsActionStatus;
         
    }
  
	/**
	 * This method update the UsderBean for city <li>This method was updated in
	 * R4</li>
	 * 
	 * @param asRole Principle of the subject
	 * @param aoUserBean Bean containing user details
	 */
	private void updateUserbeanCity(String asRole, UserBean aoUserBean)
	{
		aoUserBean.setMsOrgType(ApplicationConstants.CITY_ORG);
		aoUserBean.setMsOrgName(ApplicationConstants.CITY);
		aoUserBean.setMsOrgId(asRole);
		
		if (asRole.toLowerCase().contains("staff"))
		{
			aoUserBean.setMsRole(ApplicationConstants.ROLE_STAFF);
		}
		else if (asRole.toLowerCase().contains("manager"))
		{
			aoUserBean.setMsRole(ApplicationConstants.ROLE_MANAGER);
		}
		else
		{
			aoUserBean.setMsRole(ApplicationConstants.ROLE_EXECUTIVE);
		}
	}

	    
	/**
	 * This method creates role mapping for user
	 * 
	 * @param aoSession PortletSession to set variables in application scope
	 * @param aoUserBean UserBean with user details (user name, role, etc..)
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	private void createRoleMappingMap(PortletSession aoSession, UserBean aoUserBean) throws ApplicationException
	{
		List loCompoRoleMappingList = (List) createRoleMappingMap(aoUserBean);
		Map loRoleComponentMap = CommonUtil.getComponentRoleMap(loCompoRoleMappingList);
		aoSession.setAttribute("roleMappingMap", loRoleComponentMap, PortletSession.APPLICATION_SCOPE);
		aoSession.setAttribute(ApplicationConstants.KEY_SESSION_USER_ROLE, aoUserBean.getMsRole(),
				PortletSession.APPLICATION_SCOPE);
		aoSession.setAttribute(ApplicationConstants.KEY_SESSION_USER_PERMISSION_LEVEL,
				aoUserBean.getMsPermissionLevel(), PortletSession.APPLICATION_SCOPE);
		aoSession.setAttribute(ApplicationConstants.KEY_SESSION_USER_PERMISSION_TYPE, aoUserBean.getMsPermissionType(),
				PortletSession.APPLICATION_SCOPE);
		aoSession.setAttribute("getUserRoles", aoUserBean, PortletSession.APPLICATION_SCOPE);
		
	}	
	
	/**
	 * This method creates role mapping for user
	 * 
	 * @param aoUserBean UserBean with user details (user name, role, etc..)
	 * @return List component role mapping list with component id and role
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	private List createRoleMappingMap(UserBean aoUserBean) throws ApplicationException
	{
		List loCompoRoleMappingList = null;
		Channel loChannelObj = new Channel();
		loChannelObj.setData("userBean", aoUserBean);
		TransactionManager.executeTransaction(loChannelObj, "roleComponentMapping");
		loCompoRoleMappingList = (List) loChannelObj.getData("loCompoRoleMappingList");
		
		return loCompoRoleMappingList;

	}
    	
	
	/**
	 * This method fetches details for city user(name, role, userid, emailid,
	 * etc..)
	 * 
	 * Changes made as a part of Enhancement #6280 for Release 3.3.0
	 * 
	 * @return HashMap<String, String> has fields containing name, role, userid,
	 *         emailid, etc..
	 * @throws ApplicationException
	 */
	
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private StaffDetails fetchCityUserDetail(String asLoginId) throws ApplicationException
	{
		StaffDetails loUserDetails = new StaffDetails();
		List<StaffDetails> loCityUserDetails = null;
		Channel loChannel = new Channel();
		TransactionManager.executeTransaction(loChannel, "fetchCityUserDetails");
		loCityUserDetails = (List<StaffDetails>) loChannel.getData("masterUserList");

		Iterator loItr = loCityUserDetails.iterator();
		while (loItr.hasNext())
		{
			loUserDetails = (StaffDetails) loItr.next();

			if (asLoginId.equalsIgnoreCase(loUserDetails.getMsStaffEmail()))
			{
				break;
			}
		}
		return loUserDetails;
	}



	/**
	 * This method fetches details for city user(name, role, userid, emailid,
	 * etc..)
	 * 
	 * Changes made as a part of Enhancement #6280 for Release 3.3.0
	 * 
	 * @return HashMap<String, String> has fields containing name, role, userid,
	 *         emailid, etc..
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private HashMap<String, String> fetchCityUserDetails() throws ApplicationException
	{
		// Start || Changes made as a part of Enhancement #6280 for Release
		// 3.3.0
		LinkedHashMap<String, String> loUserHashMap = new LinkedHashMap<String, String>();
		// End || Changes made as a part of Enhancement #6280 for Release 3.3.0
		StaffDetails loUserDetails = null;
		List<StaffDetails> loCityUserDetails = null;
		Channel loChannel = new Channel();
		TransactionManager.executeTransaction(loChannel, "fetchCityUserDetails");
		loCityUserDetails = (List<StaffDetails>) loChannel.getData("masterUserList");
	
		Iterator loItr = loCityUserDetails.iterator();
		while (loItr.hasNext())
		{
			loUserDetails = (StaffDetails) loItr.next();
			if (!loUserDetails.getMsUserType().toLowerCase().contains(ApplicationConstants.AGENCY))
			{
				if (loUserDetails.getMsStaffRole() != null
						&& !(loUserDetails.getMsStaffRole().equalsIgnoreCase("staff")))
				{
					loUserDetails.setMsStaffRole("manager");
				}
				loUserHashMap.put(loUserDetails.getMsStaffRole() + "|" + loUserDetails.getMsStaffId(),
						loUserDetails.getMsStaffFirstName() + " " + loUserDetails.getMsStaffLastName());
			}
		}
	
		return loUserHashMap;
	}

}