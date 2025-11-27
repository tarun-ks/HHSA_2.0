package com.nyc.hhs.controllers;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.jdom.Document;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.constants.TransactionConstants;
import com.nyc.hhs.controllers.util.ContractListUtils;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.ApprovedProvidersBean;
import com.nyc.hhs.model.AuthenticationBean;
import com.nyc.hhs.model.BaseFilter;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.EPinDetailBean;
import com.nyc.hhs.model.EvaluationBean;
import com.nyc.hhs.model.EvidenceBean;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.NotificationDataBean;
import com.nyc.hhs.model.Procurement;
import com.nyc.hhs.model.ProcurementInfo;
import com.nyc.hhs.model.SelectedServicesBean;
import com.nyc.hhs.model.StaffDetails;
import com.nyc.hhs.rule.Rule;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.ApplicationSession;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.HHSPortalUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.JSONUtility;
import com.nyc.hhs.util.PortalUtil;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.XMLUtil;

/**
 * This controller class will handle all the request made by user from the
 * Procurement Roadmap details tab and screen like populating list of active
 * procurements from the database,adding new procurement, viewing details of
 * existing procurement, selecting services, viewing list of approved proved
 * providers and publishing the procurement. Also it will handle the sorting of
 * the columns and navigating through different pages.
 * 
 * This controller will be executed from different screens. Below are the screen
 * ids 1- S201 2- S202 3- S203 4- S248 5- S207 6- S229 7-S230 8-S208
 */

@Controller(value = "procurementHandler")
@RequestMapping("view")
public class ProcurementController extends BaseControllerSM
{
	/**
	 * This the log object which is used to log any error into log file when any
	 * exception occured
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(ProcurementController.class);

	/**
	 * This method will initialize the procurement bean object
	 * 
	 * @return Procurement Bean Object
	 */
	@ModelAttribute("Procurement")
	public Procurement getCommandObject()
	{
		return new Procurement();
	}

	/**
	 * This method will initialize the SelectedServicesBean object
	 * 
	 * @return SelectedServicesBean Object
	 */
	@ModelAttribute("SelectedServicesBean")
	public SelectedServicesBean getCommandObj()
	{
		return new SelectedServicesBean();
	}

	/**
	 * Validator Object
	 */
	@Autowired
	private Validator validator;

	/**
	 * This is used to validate procurement Summary
	 * 
	 * @param aoValidator validator object
	 */
	public void setValidator(Validator aoValidator)
	{
		this.validator = aoValidator;
	}

	/**
	 * This method will initialize the AuthenticationBean object
	 * 
	 * @return AuthenticationBean Object
	 */
	@ModelAttribute("AuthenticationBean")
	public AuthenticationBean getAuthenticationBean()
	{
		return new AuthenticationBean();
	}

	/**
	 * This method will be executed when the page will be loaded first time and
	 * then for rendering the actions
	 * 
	 * <ul>
	 * <li>If lsActionReqParam is null Call handleDefaultRendering method to
	 * render the page.</li>
	 * <li>If lsActionReqParam is not null Call renderProcurementSummary method
	 * to render the page.</li>
	 * <li>Return ModelAndView object returned by
	 * handleDefaultRendering/renderProcurementSummary method.</li>
	 * </ul>
	 * 
	 * @param aoRequest an Render Request object
	 * @param aoResponse an Render Response object
	 * @return ModelAndView
	 */
	@RenderMapping
	protected ModelAndView handleRenderRequestInternal(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		PortletSession loSession = aoRequest.getPortletSession();
		String lsActionReqParam = PortalUtil.parseQueryString(aoRequest, HHSConstants.RENDER_ACTION);
		String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
		String lsUserOrg = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
				PortletSession.APPLICATION_SCOPE);
		String lsResetSessonObject = PortalUtil.parseQueryString(aoRequest, HHSConstants.RESET_SESSION_PROC);
		String lsFilteredValue = PortalUtil.parseQueryString(aoRequest, HHSConstants.FILTERED);
		if (null != lsResetSessonObject && lsResetSessonObject.equalsIgnoreCase(HHSConstants.TRUE))
		{
			aoRequest.getPortletSession().setAttribute(HHSConstants.PROCUREMENT_SESSION_BEAN, null);
			lsFilteredValue = null;
		}
		if (aoRequest.getPortletSession().getAttribute(HHSConstants.EVALUATION_SESSION_BEAN) != null)
		{
			aoRequest.getPortletSession().removeAttribute(HHSConstants.EVALUATION_SESSION_BEAN);
			aoRequest.getPortletSession().removeAttribute(HHSConstants.PROPOSAL_FILTERED);
		}
		if (aoRequest.getPortletSession().getAttribute(HHSConstants.EVALUATION_SESSION_FILTER_BEAN) != null)
		{
			aoRequest.getPortletSession().removeAttribute(HHSConstants.EVALUATION_SESSION_FILTER_BEAN);
			aoRequest.getPortletSession().removeAttribute(HHSConstants.PROPOSAL_FILTERED_RESULT);
		}
		Boolean loIsAgencyOrg = false;
		ModelAndView loModelAndView = null;
		try
		{
			if (null == lsActionReqParam)
			{
				loModelAndView = handleDefaultRendering(aoRequest, loSession, lsUserOrgType, lsUserOrg,
						lsFilteredValue, loIsAgencyOrg);
			}
			else if (lsActionReqParam.equalsIgnoreCase(HHSConstants.VIEW_PROCUREMENT))
			{
				loModelAndView = renderProcurementSummary(aoRequest, aoResponse);
			}
			// Start : R5 Added
			else if (lsActionReqParam.equalsIgnoreCase(HHSR5Constants.NAVIGATE_TO_COMPETITION_POOL))
			{
				String lsJspName = getEvaluationStatus(aoRequest, aoResponse);
				loModelAndView = new ModelAndView(lsJspName, HHSR5Constants.EVALUATION_BEAN, new EvaluationBean());
				aoRequest.setAttribute(HHSR5Constants.EVALUATION_POOL_MAPPING_ID,
						PortalUtil.parseQueryString(aoRequest, HHSR5Constants.EVALUATION_POOL_MAPPING_ID));
			}
			// End : R5 Added
		}
		// Catch the exception thrown by transaction and set the error message
		// in request object and pass to jsp
		catch (ApplicationException loExp)
		{
			setGenericErrorMessage(aoRequest);
		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error occured while processing procurements", loExp);
			setGenericErrorMessage(aoRequest);
		}
		return loModelAndView;
	}

	/**
	 * This method renders the page if default rendering is required. ie no
	 * action method sets the render_action parameter.
	 * 
	 * <ul>
	 * ========================================<br/>
	 * Conditions to be checked in this method<br/>
	 * ========================================<br/>
	 * <li>Get Procurement bean from session.</li>
	 * <li>Condition 1:If user is a agency user, set agency Id in
	 * ProcurementFilter object as users own agency</li>
	 * <li>Condition 2:If agency Id in ProcurementFilter is All NYC Agencies,
	 * set it as null</li>
	 * <li>Condition 3:If agency Id in ProcurementFilter is not null, set it in
	 * request object</li>
	 * <li>Set services list in request object by calling getServicesList()
	 * method from HHSUtil class.</li>
	 * <li>Also set Procurement roadmap list and total count of active
	 * procurements in request object.</li>
	 * <li>Execute transaction "fetchActiveProcurements".</li>
	 * <li>call the method processProcurementResult to set the result in reques</li>
	 * <li>Return ModelAndView containing name of the jsp and the data to be
	 * rendered on the page.</li>
	 * <li>We are checking whether we are coming from home page or directly to
	 * procurement page based on that we are displaying the procurements.</li>
	 * </ul>
	 * </ul>
	 * <ul>
	 * <li>Method updated for R4</li>
	 * </ul>
	 * @param aoRequest request object
	 * @param aoSession Session object
	 * @param asUserOrgType organization type
	 * @param asUserOrg UserOrg
	 * @param asFilteredValue FilteredValue
	 * @param aoIsAgencyOrg AgencyOrg
	 * @return model and view
	 * @throws ApplicationException when exception occurs
	 * @throws NumberFormatException when exception occurs
	 */
	private ModelAndView handleDefaultRendering(RenderRequest aoRequest, PortletSession aoSession,
			String asUserOrgType, String asUserOrg, String asFilteredValue, Boolean aoIsAgencyOrg)
			throws ApplicationException, NumberFormatException
	{
		Procurement loProcBean = null;
		ModelAndView loModelAndView;
		loProcBean = (Procurement) aoRequest.getPortletSession().getAttribute(HHSConstants.PROCUREMENT_SESSION_BEAN,
				PortletSession.PORTLET_SCOPE);
		String lsRequestFromHomePage = PortalUtil.parseQueryString(aoRequest, HHSConstants.FROM_HOME_PAGE);
		// check whether we are navigating from home page or from some other
		// page
		if (null != lsRequestFromHomePage && lsRequestFromHomePage.equalsIgnoreCase(HHSConstants.TRUE))
		{
			if (null != asUserOrgType && asUserOrgType.equals(ApplicationConstants.PROVIDER_ORG))
			{
				loProcBean = getProcurementBeanForProviderHomePages(aoRequest);
			}
			else if (null != asUserOrgType
					&& (asUserOrgType.equals(ApplicationConstants.CITY_ORG) || asUserOrgType
							.equals(ApplicationConstants.AGENCY_ORG)))
			{
				loProcBean = getProcurementBeanForCityAndAgencyHomePages(aoRequest);
			}
		}
		Channel loChannelObj = new Channel();
		// check for null value of procurement session object
		if (null == loProcBean)
		{
			loProcBean = new Procurement();
		}
		// set agency org for all agency users
		if (null != asUserOrgType && asUserOrgType.equals(ApplicationConstants.AGENCY_ORG)
				&& loProcBean.getAgencyId() == null)
		{
			loProcBean.setAgencyId(asUserOrg);
		}
		// set agency id to null if its value is All NYC Agencies
		if (null != loProcBean.getAgencyId()
				&& loProcBean.getAgencyId().equalsIgnoreCase(HHSConstants.ALL_NYC_AGENCIES))
		{
			loProcBean.setAgencyId(null);
		}
		// set favorite flag
		if (null != aoRequest.getParameter(HHSConstants.FAVORITE_FLAG))
		{
			loProcBean.setIsFavoriteDisplayed(aoRequest.getParameter(HHSConstants.FAVORITE_FLAG));
		}
		if (null != loProcBean.getAgencyId() && !loProcBean.getAgencyId().equals(HHSConstants.EMPTY_STRING))
		{
			aoIsAgencyOrg = Boolean.TRUE;
		}
		String lsNextPage = aoRequest.getParameter(HHSConstants.NEXT_PAGE_PARAM);
		loChannelObj.setData(HHSConstants.ORGTYPE, asUserOrgType);
		loChannelObj.setData(HHSConstants.AGENCYID, loProcBean.getAgencyId());
		loChannelObj.setData(HHSConstants.AGENCY_ORG, aoIsAgencyOrg);
		
		//Begin R 7.2.0 QC 8914 This is to exclude Draft Procurements
		String roleCurrent = (String)aoSession.getAttribute(ApplicationConstants.KEY_SESSION_ROLE_CURRENT, PortletSession.APPLICATION_SCOPE); 
		LOG_OBJECT.Info("Checking curr_Role to exclude Draft Procurements , curr_role = " + roleCurrent);
		if( ApplicationConstants.ROLE_OBSERVER.equalsIgnoreCase(roleCurrent)) {
			loProcBean.setRoleCurrent(roleCurrent);
		}
		//End R 7.2.0 QC 8914
		
		// set pagination parameters in session object by calling
		// getPagingParams()
		getPagingParams(aoSession, loProcBean, lsNextPage, HHSConstants.PROCUREMENT_ROADMAP_KEY);
		PortletSession loSession = aoRequest.getPortletSession();
		String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		loProcBean.setStaffId(lsUserId);
		loChannelObj.setData(HHSConstants.PROCUREMENT_BEAN, loProcBean);
		loProcBean.setOrganizationId(asUserOrg);

		if (null != asUserOrgType && asUserOrgType.equals(ApplicationConstants.PROVIDER_ORG))
		{
			handleDefaultRenderingFinal(loProcBean);
		}
		HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.FETCH_ACTIVE_PROCUREMENTS);
		// Start Release 5 user notification
		processProcurementResult(aoRequest, aoSession, loProcBean, loChannelObj, asFilteredValue, asUserOrgType,
				aoRequest);
		// End Release 5 user notification
		loModelAndView = new ModelAndView(HHSConstants.PROCUREMENT_ROADMAP, HHSConstants.PROCUREMENT, loProcBean);
		return loModelAndView;
	}

	/**
	 * This method renders the page if default rendering is required. ie no
	 * action method sets the render_action parameter.
	 * 
	 * @param loProcBean procurement object
	 * @throws ApplicationException If an Exception Occurs
	 * @throws NumberFormatException If a NumberFOrmatException Occurs
	 */
	private void handleDefaultRenderingFinal(Procurement loProcBean) throws ApplicationException, NumberFormatException
	{
		List<String> loProcStatusList = loProcBean.getProcurementStatusList();
		if (null != loProcStatusList && !loProcStatusList.isEmpty())
		{
			// For Provider users, if provider status contains value
			// "Proposal Received", add "Evaluations Complete" status in
			// list
			if (loProcStatusList.contains(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_PROCUREMENT_PROPOSALS_RECEIVED)))
			{
				loProcStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_PROCUREMENT_EVALUATIONS_COMPLETE));
				loProcBean.setProcurementStatusList(loProcStatusList);
			}
			// For Provider Users, if provider searches for procurement with
			// "Cancelled" status, set previous status value as "Draft"
			else if (loProcStatusList.contains(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_PROCUREMENT_CANCELLED)))
			{
				loProcBean.setPreviousStatus(Integer.parseInt(PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_DRAFT)));
			}
		}
	}

	/**
	 * This method will handle all the resource request when user clicks on
	 * count in procurement portlet on home page. * This method returns the bean
	 * based on the filter criteria on home page.
	 * <ul>
	 * ========================================<br/>
	 * Conditions to be checked in this method<br/>
	 * ========================================<br/>
	 * <li>Get Procurement bean from session.</li>
	 * <li>Condition 1:If user clicks on link RFPs you're eligible for will be
	 * released within 30 days set the required parameters into bean and return
	 * the bean to method handleDefaultRendering</li>
	 * <li>Condition 2:If user clicks on link RFPs you're eligible for have due
	 * dates within 30 days set the required parameters into bean and return the
	 * bean to method handleDefaultRendering</li>
	 * <li>Condition 3:If user clicks on link RFPs with at least 1 draft
	 * proposal link set the required parameters into bean and return the bean
	 * to method handleDefaultRendering</li>
	 * <li>Condition 4:If user clicks on link RFPs with at least 1 proposal
	 * submitted set the required parameters into bean and return the bean to
	 * method handleDefaultRendering</li>
	 * <li>Condition 5:If user clicks on link RFPs with proposals determined
	 * eligible for award set the required parameters into bean and return the
	 * bean to method handleDefaultRendering</li>
	 * <li>Internally calling the methods getRFPRelease30Days and
	 * getRFPDueIn30Days for retrieving the bean</li>
	 * <li>Return procurement bean to the method handleDefaultRendering.</li>
	 * </ul>
	 * 
	 */
	/**
	 * @param aoRequest Request
	 * @return Procurement Bean
	 * @throws ApplicationException when any exception occurred
	 */
	private Procurement getProcurementBeanForProviderHomePages(RenderRequest aoRequest) throws ApplicationException
	{
		Procurement loProcBean = new Procurement();
		String lsFilterCriteria = PortalUtil.parseQueryString(aoRequest, HHSConstants.FILTER_CRITERIA);
		if (null != lsFilterCriteria)
		{
			if (lsFilterCriteria.equalsIgnoreCase(HHSConstants.RFP_RELEASED_THIRTY_DAYS))
			{
				getRFPRelease30Days(aoRequest, loProcBean);
			}
			else if (lsFilterCriteria.equalsIgnoreCase(HHSConstants.RFP_DUE_IN_THIRTY_DAYS))
			{
				getRFPDueIn30Days(aoRequest, loProcBean);
			}
			else if (lsFilterCriteria.equalsIgnoreCase(HHSConstants.PROPOSAL_IN_DRAFT_STATUS))
			{
				List<String> loProviderStatusesList = new ArrayList<String>();
				loProviderStatusesList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_PROVIDER_DRAFT));
				loProviderStatusesList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_PROVIDER_SUBMITTED_PROPOSAL));
				loProcBean.setProviderStatusList(loProviderStatusesList);
				List<String> loProStatusList = new ArrayList<String>();
				loProStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_PROCUREMENT_PLANNED));
				loProStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_PROCUREMENT_RELEASED));
				loProStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_PROCUREMENT_PROPOSALS_RECEIVED));
				loProStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_PROCUREMENT_SELECTIONS_MADE));
				loProcBean.setProcurementStatusList(loProStatusList);
			}
			else if (lsFilterCriteria.equalsIgnoreCase(HHSConstants.RFP_IN_SEL_MADE_STATUS))
			{
				List<String> loProviderStatusesList = new ArrayList<String>();
				loProviderStatusesList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_PROVIDER_SELECTED));
				loProcBean.setProviderStatusList(loProviderStatusesList);
				List<String> loProcStatusList = new ArrayList<String>();
				loProcStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_PROCUREMENT_PLANNED));
				loProcStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_PROCUREMENT_RELEASED));
				loProcStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_PROCUREMENT_PROPOSALS_RECEIVED));
				loProcStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_PROCUREMENT_SELECTIONS_MADE));
				loProcBean.setProcurementStatusList(loProcStatusList);
			}
			/* Start : R5 Added */
			else if (lsFilterCriteria.equalsIgnoreCase(HHSR5Constants.SUBMITTED_PROPOSAL))
			{
				List<String> loProcStatusList = new ArrayList<String>();
				loProcStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_PROVIDER_SUBMITTED_PROPOSAL));
				loProcStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_PROVIDER_ELIGIBLE_TO_PROPOSE));
				loProcBean.setProviderStatusList(loProcStatusList);
			}
			/* End : R5 Added */
		}
		return loProcBean;
	}

	/**
	 * This method will handle all the resource request when user clicks on
	 * count in procurement portlet on home page. * This method returns the bean
	 * based on the filter criteria on home page.
	 * <ul>
	 * ========================================<br/>
	 * Conditions to be checked in this method<br/>
	 * ========================================<br/>
	 * <li>Get Procurement bean from session.</li>
	 * <li>Condition 1:If user clicks on link RFPs scheduled to be released
	 * within 10 days set the required parameters into bean and return the bean
	 * to method handleDefaultRendering</li>
	 * <li>Condition 2:If user clicks on link RFPs scheduled to be released
	 * within 60 days set the required parameters into bean and return the bean
	 * to method handleDefaultRendering</li>
	 * <li>Condition 3:If user clicks on link RFPs in released status link set
	 * the required parameters into bean and return the bean to method
	 * handleDefaultRendering</li>
	 * <li>Condition 4:If user clicks on link RFPs with proposal due dates
	 * within 10 days set the required parameters into bean and return the bean
	 * to method handleDefaultRendering</li>
	 * <li>Condition 5:If user clicks on link RFPs with proposals received set
	 * the required parameters into bean and return the bean to method
	 * handleDefaultRendering</li>
	 * <li>Condition 6:If user clicks on link RFPs with evaluations complete set
	 * the required parameters into bean and return the bean to method
	 * handleDefaultRendering</li>
	 * <li>Condition 7:If user clicks on link RFPs with selections made and
	 * submitted to Accelerator set the required parameters into bean and return
	 * the bean to method handleDefaultRendering</li>
	 * <li>Internally calling the methods getRFPRelease30Days and
	 * getRFPDueIn30Days for retrieving the bean</li>
	 * <li>Return procurement bean to the method handleDefaultRendering.</li>
	 * </ul>
	 * 
	 */
	/**
	 * @param aoRequest Request
	 * @return Procurement Bean
	 * @throws ApplicationException when any exception occurred
	 */
	private Procurement getProcurementBeanForCityAndAgencyHomePages(RenderRequest aoRequest)
			throws ApplicationException
	{
		Procurement loProcBean = new Procurement();
		String lsFilterCriteria = PortalUtil.parseQueryString(aoRequest, HHSConstants.FILTER_CRITERIA);
		if (null != lsFilterCriteria)
		{
			if (lsFilterCriteria.equalsIgnoreCase(HHSConstants.RFP_RELEASED_TEN_DAYS))
			{
				getRFPRelease10Days(aoRequest, loProcBean);
			}
			else if (lsFilterCriteria.equalsIgnoreCase(HHSConstants.RFP_RELEASED_SIXTY_DAYS))
			{
				getRFPRelease60Days(aoRequest, loProcBean);
			}
			else if (lsFilterCriteria.equalsIgnoreCase(HHSConstants.RFP_IN_RELEASED_STATUS))
			{
				List<String> loProcurementStatusList = new ArrayList<String>();
				loProcurementStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_PROCUREMENT_RELEASED));
				loProcBean.setProcurementStatusList(loProcurementStatusList);
			}
			else if (lsFilterCriteria.equalsIgnoreCase(HHSConstants.RFP_PROPOSAL_DUE_DATE_TEN_DAYS))
			{
				getRFPDueIn10Days(aoRequest, loProcBean);
			}
			else if (lsFilterCriteria.equalsIgnoreCase(HHSConstants.RFP_PROPOSAL_RECEIVED))
			{
				List<String> loProcurementStatusList = new ArrayList<String>();
				loProcurementStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_PROCUREMENT_PROPOSALS_RECEIVED));
				loProcBean.setProcurementStatusList(loProcurementStatusList);
			}
			else if (lsFilterCriteria.equalsIgnoreCase(HHSConstants.RFP_EVALUATION_COMPLETE))
			{
				List<String> loProcurementStatusList = new ArrayList<String>();
				loProcurementStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_PROCUREMENT_EVALUATIONS_COMPLETE));
				loProcBean.setProcurementStatusList(loProcurementStatusList);
			}
			else if (lsFilterCriteria.equalsIgnoreCase(HHSConstants.RFP_SELECTION_MADE))
			{
				List<String> loProcurementStatusList = new ArrayList<String>();
				loProcurementStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_PROCUREMENT_SELECTIONS_MADE));
				loProcBean.setProcurementStatusList(loProcurementStatusList);
			}
		}
		return loProcBean;
	}

	/**
	 * This method will handle all the resource request when user clicks on
	 * count in procurement portlet for the link RFPs you're eligible for will
	 * be released within 30 days on home page. This method returns the bean
	 * based on the filter criteria on home page.
	 * 
	 * <ul>
	 * ========================================<br/>
	 * Conditions to be checked in this method<br/>
	 * ========================================<br/>
	 * <li>Get Procurement bean from session.</li>
	 * <li>Condition 1:If user clicks on link RFPs you're eligible for will be
	 * released within 30 days set the required parameters such as provider
	 * status should be eligible to propose and procurement status should be
	 * planned into bean and return the bean to method
	 * getProcurementBeanForHomePages</li>
	 * <li>Return procurement bean to the method getProcurementBeanForHomePages.
	 * </li>
	 * </ul>
	 * 
	 * @param aoRequest request
	 * @param loProcBean Procurement bean
	 * @return Procurement bean
	 * @throws ApplicationException when any exception occurs
	 */
	private Procurement getRFPRelease30Days(RenderRequest aoRequest, Procurement loProcBean)
			throws ApplicationException
	{
		String lsCurrentDate = DateUtil.getCurrentDate();
		GregorianCalendar loCal = new GregorianCalendar();
		loCal.setTime(DateUtil.getDate(lsCurrentDate));
		loCal.add(GregorianCalendar.DAY_OF_MONTH, 30);
		List<String> loProviderStatusList = new ArrayList<String>();
		loProviderStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_PROVIDER_ELIGIBLE_TO_PROPOSE));
		List<String> loProcurementStatusList = new ArrayList<String>();
		loProcurementStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_PROCUREMENT_PLANNED));
		loProcBean.setProcurementStatusList(loProcurementStatusList);
		loProcBean.setProviderStatusList(loProviderStatusList);
		String lsReleaseToDate = DateUtil.getDateMMddYYYYFormat(loCal.getTime());
		loProcBean.setReleaseFrom(DateUtil.getCurrentDate());
		loProcBean.setReleaseTo(lsReleaseToDate);
		return loProcBean;
	}

	/**
	 * This method will handle all the resource request when user clicks on
	 * count in procurement portlet for the link RFPs you're eligible for have
	 * due dates within 30 days. This method returns the bean based on the
	 * filter criteria on home page.
	 * 
	 * <ul>
	 * ========================================<br/>
	 * Conditions to be checked in this method<br/>
	 * ========================================<br/>
	 * <li>Get Procurement bean from session.</li>
	 * <li>Condition 1:If user clicks on link RFPs you're eligible for have due
	 * dates within 30 days set the required parameters such as provider status
	 * should be eligible to propose and procurement status should be Released
	 * into bean and return the bean to method getProcurementBeanForHomePages</li>
	 * <li>Return procurement bean to the method getProcurementBeanForHomePages.
	 * </li>
	 * </ul>
	 * @param aoRequest render request object
	 * @param aoProcBean Procurement bean object
	 * @return Procurement
	 * @throws ApplicationException when exception occurs
	 */
	private Procurement getRFPDueIn30Days(RenderRequest aoRequest, Procurement aoProcBean) throws ApplicationException
	{
		String lsCurrentDate = DateUtil.getCurrentDate();
		GregorianCalendar loCal = new GregorianCalendar();
		loCal.setTime(DateUtil.getDate(lsCurrentDate));
		loCal.add(GregorianCalendar.DAY_OF_MONTH, 30);
		List<String> loProviderStatusList = new ArrayList<String>();
		loProviderStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_PROVIDER_ELIGIBLE_TO_PROPOSE));
		List<String> loProcurementStatusList = new ArrayList<String>();
		loProcurementStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_PROCUREMENT_RELEASED));
		aoProcBean.setProcurementStatusList(loProcurementStatusList);
		aoProcBean.setProviderStatusList(loProviderStatusList);
		String lsProposalDueTo = DateUtil.getDateMMddYYYYFormat(loCal.getTime());
		aoProcBean.setProposalDueFrom(DateUtil.getCurrentDate());
		aoProcBean.setProposalDueTo(lsProposalDueTo);
		return aoProcBean;
	}

	/**
	 * This method will handle all the resource request when user clicks on
	 * count in procurement portlet for the link RFPs you're eligible for will
	 * be released within 10 days on home page. This method returns the bean
	 * based on the filter criteria on home page.
	 * 
	 * <ul>
	 * ========================================<br/>
	 * Conditions to be checked in this method<br/>
	 * ========================================<br/>
	 * <li>Get Procurement bean from session.</li>
	 * <li>Condition 1:If user clicks on link RFPs you're eligible for will be
	 * released within 10 days set the required parameters such as provider
	 * status should be eligible to propose and procurement status should be
	 * planned into bean and return the bean to method
	 * getProcurementBeanForCityAndAgencyHomePages</li>
	 * <li>Return procurement bean to the method getProcurementBeanForHomePages.
	 * </li>
	 * </ul>
	 * 
	 * @param aoRequest request
	 * @param loProcBeanForTenDays Procurement bean
	 * @return Procurement bean
	 * @throws ApplicationException when any exception occurs
	 */
	private Procurement getRFPRelease10Days(RenderRequest aoRequest, Procurement loProcBeanForTenDays)
			throws ApplicationException
	{
		String lsCurrentDate = DateUtil.getCurrentDate();
		GregorianCalendar loCal = new GregorianCalendar();
		loCal.setTime(DateUtil.getDate(lsCurrentDate));
		loCal.add(GregorianCalendar.DAY_OF_MONTH, 10);
		List<String> loProcurementStatusList = new ArrayList<String>();
		loProcurementStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_PROCUREMENT_PLANNED));
		loProcBeanForTenDays.setProcurementStatusList(loProcurementStatusList);
		String lsReleaseToDate = DateUtil.getDateMMddYYYYFormat(loCal.getTime());
		loProcBeanForTenDays.setReleaseFrom(DateUtil.getCurrentDate());
		loProcBeanForTenDays.setReleaseTo(lsReleaseToDate);
		return loProcBeanForTenDays;
	}

	/**
	 * This method will handle all the resource request when user clicks on
	 * count in procurement portlet for the link RFPs you're eligible for will
	 * be released within 60 days on home page. This method returns the bean
	 * based on the filter criteria on home page.
	 * 
	 * <ul>
	 * ========================================<br/>
	 * Conditions to be checked in this method<br/>
	 * ========================================<br/>
	 * <li>Get Procurement bean from session.</li>
	 * <li>Condition 1:If user clicks on link RFPs you're eligible for will be
	 * released within 60 days set the required parameters such as provider
	 * status should be eligible to propose and procurement status should be
	 * planned into bean and return the bean to method
	 * getProcurementBeanForCityAndAgencyHomePages</li>
	 * <li>Return procurement bean to the method
	 * getProcurementBeanForCityAndAgencyHomePages.</li>
	 * </ul>
	 * 
	 * @param aoRequest request
	 * @param loProcBeanForSixtyDays Procurement bean
	 * @return Procurement bean
	 * @throws ApplicationException when any exception occurs
	 */
	private Procurement getRFPRelease60Days(RenderRequest aoRequest, Procurement loProcBeanForSixtyDays)
			throws ApplicationException
	{
		String lsCurrentDate = DateUtil.getCurrentDate();
		GregorianCalendar loCal = new GregorianCalendar();
		loCal.setTime(DateUtil.getDate(lsCurrentDate));
		loCal.add(GregorianCalendar.DAY_OF_MONTH, 60);
		List<String> loProcurementStatusList = new ArrayList<String>();
		loProcurementStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_PROCUREMENT_PLANNED));
		loProcBeanForSixtyDays.setProcurementStatusList(loProcurementStatusList);
		String lsReleaseToDate = DateUtil.getDateMMddYYYYFormat(loCal.getTime());
		loProcBeanForSixtyDays.setReleaseFrom(DateUtil.getCurrentDate());
		loProcBeanForSixtyDays.setReleaseTo(lsReleaseToDate);
		return loProcBeanForSixtyDays;
	}

	/**
	 * This method will handle all the resource request when user clicks on
	 * count in procurement portlet for the link RFPs you're eligible for have
	 * due dates within 10 days. This method returns the bean based on the
	 * filter criteria on home page.
	 * 
	 * <ul>
	 * ========================================<br/>
	 * Conditions to be checked in this method<br/>
	 * ========================================<br/>
	 * <li>Get Procurement bean from session.</li>
	 * <li>Condition 1:If user clicks on link RFPs you're eligible for have due
	 * dates within 30 days set the required parameters such as provider status
	 * should be eligible to propose and procurement status should be Released
	 * into bean and return the bean to method getProcurementBeanForHomePages</li>
	 * <li>Return procurement bean to the method
	 * getProcurementBeanForCityAndAgencyHomePages.</li>
	 * </ul>
	 * @param aoRequest Render Request object
	 * @param loProcBeanForProposal Procurement bean object
	 * @return Procurement
	 * @throws ApplicationException when exception occurs
	 */
	private Procurement getRFPDueIn10Days(RenderRequest aoRequest, Procurement loProcBeanForProposal)
			throws ApplicationException
	{
		String lsCurrentDate = DateUtil.getCurrentDate();
		GregorianCalendar loCal = new GregorianCalendar();
		loCal.setTime(DateUtil.getDate(lsCurrentDate));
		loCal.add(GregorianCalendar.DAY_OF_MONTH, 10);
		List<String> loProcurementStatusList = new ArrayList<String>();
		loProcurementStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_PROCUREMENT_RELEASED));
		loProcBeanForProposal.setProcurementStatusList(loProcurementStatusList);
		String lsProposalDueTo = DateUtil.getDateMMddYYYYFormat(loCal.getTime());
		loProcBeanForProposal.setProposalDueFrom(DateUtil.getCurrentDate());
		loProcBeanForProposal.setProposalDueTo(lsProposalDueTo);
		return loProcBeanForProposal;
	}

	/**
	 * This method will get the channel results and process to set various
	 * attributes in request object
	 * 
	 * <ul>
	 * <li>Get the channel data</li>
	 * <li>Set the attributes in request object</li>
	 * <li>Set appropriate error messages if exist</li>
	 * </ul>
	 * 
	 * @param aoRenderRequestForProc a render request object
	 * @param aoSession a portlet session object
	 * @param aoProcurementBean a procurement bean object
	 * @param aoChannelObjForProc a channel object containing results
	 * @param asFilteredValue a string indicating filter implemented or not
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings("unchecked")
	private void processProcurementResult(RenderRequest aoRenderRequestForProc, PortletSession aoSession,
			Procurement aoProcurementBean, Channel aoChannelObjForProc, String asFilteredValue, String asUserOrgType,
			RenderRequest aoRequest) throws ApplicationException
	{
		// get the procurement list from channel
		List<Procurement> loProcurementList = (List<Procurement>) aoChannelObjForProc
				.getData(HHSConstants.PROCUREMENT_LIST);
		
		// BEGIN R 7.2.0 QC 8914
		setOversightRoleFlag(aoSession, loProcurementList);
		// END R 7.2.0 QC 8914
		
		// Release 5 User Notification
		String lsPermissionType = (String) aoSession.getAttribute(HHSConstants.PERMISSION_TYPE,
				PortletSession.APPLICATION_SCOPE);
		if (lsPermissionType != null
				&& (lsPermissionType.equalsIgnoreCase(ApplicationConstants.ROLE_READ_ONLY) || lsPermissionType
						.equalsIgnoreCase(ApplicationConstants.ROLE_FINANCIAL))
				&& asUserOrgType.equalsIgnoreCase(HHSConstants.PROVIDER_ORG))
		{
			if (loProcurementList != null)
			{
				for (Procurement loProcurement : loProcurementList)
				{
					loProcurement.setUserAccess(false);
				}
			}
		}
		aoRequest.setAttribute(HHSR5Constants.PROC_ROADMAP_READONLY_FLAG,
				getFinancialsReadOnly(aoRequest, asUserOrgType));
		// Release 5 User Notification
		// get the procurement count from channel
		Integer loProcurementCount = (Integer) aoChannelObjForProc.getData(HHSConstants.PROCUREMENT_COUNT);
		// get the program name list from channel for agency users
		List<Procurement> loProgramNameList = (List<Procurement>) aoChannelObjForProc
				.getData(HHSConstants.PROGRAM_NAME_LIST);
		aoRenderRequestForProc.setAttribute(HHSConstants.SELECTED_PROGRAM_NAME, aoProcurementBean.getProgramName());
		aoRenderRequestForProc.setAttribute(HHSConstants.PROGRAM_NAME_LIST, loProgramNameList);
		aoRenderRequestForProc.setAttribute(HHSConstants.PROCUREMENT_LIST, loProcurementList);
		aoRenderRequestForProc.setAttribute(HHSConstants.TOTAL_COUNT, ((loProcurementCount == null) ? 0
				: loProcurementCount));
		aoRenderRequestForProc.setAttribute(ApplicationConstants.AGENCY_SET, HHSUtil.getAgencyMapForProcurement());
		if (null != aoProcurementBean.getAgencyId())
		{
			aoRenderRequestForProc.setAttribute(HHSConstants.SELECTED_AGENCY, aoProcurementBean.getAgencyId());
		}
		else
		{
			aoRenderRequestForProc.setAttribute(HHSConstants.SELECTED_AGENCY, HHSConstants.ALL_NYC_AGENCIES);
		}
		if (null == asFilteredValue)
		{
			asFilteredValue = aoRenderRequestForProc.getParameter(HHSConstants.FILTERED);
		}
		aoRenderRequestForProc.setAttribute(HHSConstants.FILTERED, asFilteredValue);
		if (aoRenderRequestForProc.getPortletSession().getAttribute(HHSConstants.PROCUREMENT_SESSION_BEAN,
				PortletSession.PORTLET_SCOPE) != null)
		{
			aoRenderRequestForProc.setAttribute(HHSConstants.PROC_FILTERED, aoRenderRequestForProc.getPortletSession()
					.getAttribute(HHSConstants.PROC_FILTERED, PortletSession.PORTLET_SCOPE));
		}

		// set services list in request object
		aoRenderRequestForProc.setAttribute(HHSConstants.SERVICE_MAP, HHSUtil.getServicesList());
		aoSession.setAttribute(ApplicationConstants.ALERT_VIEW_PAGING_RECORDS, ((loProcurementCount == null) ? 0
				: loProcurementCount), PortletSession.APPLICATION_SCOPE);
		aoRenderRequestForProc.getPortletSession().setAttribute(HHSConstants.SORT_TYPE,
				aoProcurementBean.getFirstSortType(), PortletSession.APPLICATION_SCOPE);
		aoRenderRequestForProc.getPortletSession().setAttribute(HHSConstants.SORT_BY,
				aoProcurementBean.getSortColumnName(), PortletSession.APPLICATION_SCOPE);
		// process success messages for cancel and close procurement
		if (null != PortalUtil.parseQueryString(aoRenderRequestForProc, HHSConstants.SUCCESS))
		{
			if (PortalUtil.parseQueryString(aoRenderRequestForProc, HHSConstants.SUCCESS).equalsIgnoreCase(
					HHSConstants.CLOSE_PROCUREMENT))
			{
				aoRenderRequestForProc.setAttribute(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.CLOSE_PROCUREMENT));
			}
			if (PortalUtil.parseQueryString(aoRenderRequestForProc, HHSConstants.SUCCESS).equalsIgnoreCase(
					HHSConstants.CANCEL_PROCUREMENT))
			{
				aoRenderRequestForProc.setAttribute(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.CANCEL_PROCUREMENT));
			}
			aoRenderRequestForProc.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_PASS_TYPE);
		}
		else
		{
			aoRenderRequestForProc.setAttribute(ApplicationConstants.ERROR_MESSAGE,
					aoRenderRequestForProc.getParameter(ApplicationConstants.ERROR_MESSAGE));
			aoRenderRequestForProc.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					aoRenderRequestForProc.getParameter(ApplicationConstants.ERROR_MESSAGE_TYPE));
		}
	}

	/**
	 * This method fetches the users and program list from DB
	 * 
	 * <ul>
	 * 
	 * <li>1. Set the Procurement bean reference in the channel.</li>
	 * <li>2. Execute the transaction "fetchUserProgramNameList" and set the
	 * result in the application session.</li>
	 * <li>3. Set the User List and program Name List in the application
	 * session.</li>
	 * </ul>
	 * 
	 * @param aoRequest - ActionRequest
	 * @param aoProcurementParam - Procurement object
	 * @throws ApplicationException - throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	private void getUserAndProgramList(ActionRequest aoRequest, Procurement aoProcurementParam)
			throws ApplicationException
	{
		Channel loChannel = new Channel();
		List<Procurement> loProgramNameList = null;
		loChannel.setData(HHSConstants.PROC_SUMMARY, aoProcurementParam);
		Map<String, String> loUserType = new HashMap<String, String>();
		loUserType.put(HHSConstants.USER_CITY, HHSConstants.USER_CITY);
		loUserType.put(HHSConstants.USER_AGENCY, HHSConstants.USER_AGENCY);
		loChannel.setData(HHSConstants.USER_TYPE, loUserType);
		try
		{
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_USER_PROGRAM_NAME_LIST);
			loProgramNameList = (List<Procurement>) loChannel.getData(HHSConstants.PROGRAM_NAME_LIST);
			Map<String, List<StaffDetails>> loUserMap = (Map<String, List<StaffDetails>>) loChannel
					.getData(HHSConstants.ACCELERATOR_USER_LIST);
			List<StaffDetails> loAccUserDetails = (List<StaffDetails>) loUserMap.get(HHSConstants.USER_CITY);
			List<StaffDetails> loAgencyUserDetails = (List<StaffDetails>) loChannel
					.getData(HHSConstants.AGENCY_USER_LIST);
			ApplicationSession.setAttribute(loAccUserDetails, aoRequest, HHSConstants.ACC_USER_LIST);
			ApplicationSession.setAttribute(loAgencyUserDetails, aoRequest, HHSConstants.AGENCY_USER_LIST);
			ApplicationSession.setAttribute(loProgramNameList, aoRequest, HHSConstants.PROGRAM_NAME_LIST);
		}
		// Handling Exception while fetching Agency list
		catch (ApplicationException loEx)
		{
			LOG_OBJECT.Error("Exception Occured while getting program name list for NYC Agency ", loEx);
			throw loEx;
		}
		// handling exception other than Application Exception.
		catch (Exception loEx)
		{
			throw new ApplicationException("Exception Occured while getting program name list for NYC Agency ", loEx);
		}
	}

	/**
	 * This method performs the function of saving the new procurement details
	 * or updating existing procurement details into the database. The method is
	 * modified for enhancement 5707 as part of release 2.6.1
	 * <ul>
	 * <li>Validate if contract planned and updated start and end date fall
	 * under expected span</li>
	 * <li>1. Call setNavigationParamsInRender() method for navigation.</li>
	 * <li>1. If AgencyId parameter from JSP is null then get hiddenAgency from
	 * JSP and set in bean</li>
	 * <li>2. Call validate() method to perform server side validation.</li>
	 * <li>3. Call saveProcurementDetails() method to save the procurement
	 * details.</li>
	 * <li>4. Remove the current state from ModelMap.</li>
	 * <li>4. If server side validations fails render the same page with the
	 * appropriate messages.</li>
	 * </ul>
	 * <ul>
	 * <li>Method updated for R4</li>
	 * </ul>
	 * 
	 * @param aoProcurementParam - Procurement
	 * @param aoResult - BindingResult
	 * @param aoRequest - ActionRequest
	 * @param aoResponse - ActionResponse
	 * @param aoModel - Model
	 * @param aoModelMap model map
	 */
	@ActionMapping(params = "submit_action=addProcurement")
	protected void addNewProcurementAction(@ModelAttribute("Procurement") Procurement aoProcurementParam,
			BindingResult aoResult, ActionRequest aoRequest, ActionResponse aoResponse, Model aoModel,
			ModelMap aoModelMap)
	{
		setNavigationParamsInRender(aoRequest, aoResponse);
		String lsSaveAction = PortalUtil.parseQueryString(aoRequest, HHSConstants.SAVE_ACTION);
		if (aoProcurementParam.getAgencyId() == null
				|| aoProcurementParam.getAgencyId().equals(HHSConstants.EMPTY_STRING))
		{
			aoProcurementParam.setAgencyId(aoRequest.getParameter(HHSConstants.HIDDEN_AGENCY));
		}
		if (aoProcurementParam.getIsOpenEndedRFP() == null
				|| aoProcurementParam.getIsOpenEndedRFP().equals(HHSConstants.EMPTY_STRING))
		{
			aoProcurementParam.setIsOpenEndedRFP(aoRequest.getParameter(HHSConstants.HIDDEN_OPEN_ENDED_FLAG));
		}
		try
		{
			validator.validate(aoProcurementParam, aoResult);
			// below 2 flags are added as fixed for enhancement 5707 as part of
			// release 2.6.1
			boolean lbfiscalYearPlannedSpanFlag = HHSUtil.checkContractFiscalYearsSpan(
					aoProcurementParam.getContractStartDatePlanned(), aoProcurementParam.getContractEndDatePlanned());
			boolean lbfiscalYearUpdatedSpanFlag = HHSUtil.checkContractFiscalYearsSpan(
					aoProcurementParam.getContractStartDateUpdated(), aoProcurementParam.getContractEndDateUpdated());
			
			/*
			 * R6: Added validation for unique Epin as part of new non APT EPINS
			 */
			EPinDetailBean loEPinDetailBean = new EPinDetailBean();
			loEPinDetailBean.setAgencyId(aoProcurementParam.getAgencyId());
			//Null Check added in R6 for FindBug
			if(null != aoProcurementParam && null != aoProcurementParam.getProcurementEpin() && !aoProcurementParam.getProcurementEpin().isEmpty())
			{
				loEPinDetailBean.setEpinId(aoProcurementParam.getProcurementEpin());
			}
			//R6 emergency build - setting procurementId in epindetailbean for validation defect-8646
			if(null != aoProcurementParam)
			{
				loEPinDetailBean.setProcurementId(aoProcurementParam.getProcurementId());
			}
			boolean isEpinUnique = ContractListUtils.validateEpinUnique(loEPinDetailBean);
			
			if (!aoResult.hasErrors() && !lbfiscalYearPlannedSpanFlag && !lbfiscalYearUpdatedSpanFlag && isEpinUnique)
			{
				saveProcurementDetails(aoProcurementParam, aoRequest, aoResponse, lsSaveAction);
				aoModelMap.remove(HHSConstants.PROCUREMENT);
			}
			else
			{
				if (aoProcurementParam.getProcurementId() != null
						&& !aoProcurementParam.getProcurementId().equals(HHSConstants.EMPTY_STRING))
				{
					aoProcurementParam.setProcurementId(aoProcurementParam.getProcurementId());
					aoModel.addAttribute(aoProcurementParam);
				}
				if (lbfiscalYearPlannedSpanFlag || lbfiscalYearUpdatedSpanFlag)
				{
					aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.ERROR_CONTRACT_TERM_EXCEED));
					aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
							ApplicationConstants.MESSAGE_FAIL_TYPE);
				}
				/*R6: EPIN uniquess validation error message start */
				if(!isEpinUnique)
				{
					aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSR5Constants.EPIN_ALREADY_USE));
					aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
							ApplicationConstants.MESSAGE_FAIL_TYPE);
					aoProcurementParam.setProcurementEpin(HHSConstants.PENDING);
					aoProcurementParam.setRefEpinId(null);
				}
				/*R6: EPIN uniquess validation error message ends */
				getUserAndProgramList(aoRequest, aoProcurementParam);
				ApplicationSession.setAttribute(aoProcurementParam, aoRequest, HHSConstants.PROCUREMENT_BEAN);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.VIEW_PROCUREMENT);
			}
		}
		// Handling Exception while saving procurement
		catch (ApplicationException loEx)
		{
			Map<String, Procurement> loParamMap = new HashMap<String, Procurement>();
			loParamMap.put(HHSConstants.PROCUREMENT_LOWERCASE, aoProcurementParam);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap, loEx);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.VIEW_PROCUREMENT);
		}
		// handling exception other than Application Exception.
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Exception Occured while saving procurement : ", loEx);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.VIEW_PROCUREMENT);
		}
	}

	/**
	 * This method save the procurement summary if server side validation pass
	 * successfully.
	 * 
	 * <ul>
	 * <li>1. If agency is not null then trim the agency id.</li>
	 * <li>2. Get user id from the session and set in the bean.</li>
	 * <li>3. Check procurement status, if the status is draft then call
	 * setUpdatedDatesForDraftProcurement() method.</li>
	 * <li>4. Execute the transaction "saveProcurementSummary" and set the
	 * result in the application session.</li>
	 * <li>1. If epin is null then set the value of epin in bean as Pending.</li>
	 * <li>5. If the saveAction is save then set the name of the rendermethod as
	 * viewProcurement to stay on the same page.</li>
	 * </ul>
	 * <ul>
	 * <li>Method updated for R4</li>
	 * </ul>
	 * 
	 * @param aoProcurementParam - Procurement
	 * @param aoRequest - ActionRequest
	 * @param aoResponse - ActionResponse
	 * @param asSaveAction - Save Action to be performed
	 * @throws ApplicationException when any exception occurs
	 */
	private void saveProcurementDetails(Procurement aoProcurementParam, ActionRequest aoRequest,
			ActionResponse aoResponse, String asSaveAction) throws ApplicationException
	{
		if (null != aoProcurementParam && aoProcurementParam.getAgencyId() != null)
		{
			aoProcurementParam.setAgencyId(aoProcurementParam.getAgencyId().trim());
		}
		if (null != aoProcurementParam && aoProcurementParam.getIsOpenEndedRFP() != null
				&& aoProcurementParam.getIsOpenEndedRFP().equalsIgnoreCase(HHSConstants.ONE))
		{
			updateProcurementDatesForOpenEnded(aoProcurementParam);
		}
		Channel loChannelObj = new Channel();
		PortletSession loSession = aoRequest.getPortletSession();
		// changes for R5 starts
		P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		loChannelObj.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
		// changes for R5 ends
		String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		if (null != aoProcurementParam)
		{
			aoProcurementParam.setCreatedBy(lsUserId);
			aoProcurementParam.setModifiedBy(lsUserId);

			if (null == aoProcurementParam.getServiceUnitRequiredFlag())
			{
				aoProcurementParam.setServiceUnitRequiredFlag(HHSConstants.ZERO);
			}
		}

		loChannelObj.setData(HHSConstants.PROCUREMENT_DETAILS, aoProcurementParam);
		loChannelObj.setData(HHSConstants.PROCUREMENT_SUMMARY, aoProcurementParam);
		if (aoProcurementParam != null
				&& aoProcurementParam.getStatus() != null
				&& aoProcurementParam.getStatus() == Integer.parseInt(PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_DRAFT)))
		{
			setUpdatedDatesForDraftProcurement(aoProcurementParam);
		}
		Map<String, String> loUserType = new HashMap<String, String>();
		loUserType.put(HHSConstants.USER_CITY, HHSConstants.USER_CITY);
		loUserType.put(HHSConstants.USER_AGENCY, HHSConstants.USER_AGENCY);
		loChannelObj.setData(HHSConstants.USER_TYPE, loUserType);
		if (aoProcurementParam != null && aoProcurementParam.getProcurementEpin() != null
				&& aoProcurementParam.getProcurementEpin().equalsIgnoreCase(HHSConstants.PENDING))
		{
			aoProcurementParam.setProcurementEpin(HHSConstants.EMPTY_STRING);
		}
		loChannelObj.setData(HHSConstants.PROCUREMENT_STATUS_FLAG, Boolean.TRUE);
		HashMap<Object, Object> loLastModifiedHashMap = new HashMap<Object, Object>();
		loLastModifiedHashMap.put(HHSConstants.MOD_BY_USER_ID, lsUserId);
		loChannelObj.setData(HHSConstants.LO_LAST_MOD_HASHMAP, loLastModifiedHashMap);
		// Start R5 : set EntityId and EntityName for AutoSave
		CommonUtil.setChannelForAutoSaveData(loChannelObj, aoProcurementParam.getProcurementId(),
				HHSR5Constants.PROCUREMENT);
		// End R5 : set EntityId and EntityName for AutoSave

		HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.SAVE_ROCUREMENT_SUMMARY);
		aoProcurementParam = (Procurement) loChannelObj.getData(HHSConstants.PROC_SUMMARY);
		if (aoProcurementParam != null
				&& (aoProcurementParam.getProcurementEpin() == null || aoProcurementParam.getProcurementEpin().equals(
						HHSConstants.EMPTY_STRING)))
		{
			aoProcurementParam.setProcurementEpin(HHSConstants.PENDING);
		}
		if (asSaveAction != null && asSaveAction.equalsIgnoreCase(HHSConstants.SAVE))
		{
			String lsProcurementId = (String) loChannelObj.getData(HHSConstants.PROCUREMENT_ID);
			aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.VIEW_PROCUREMENT);
		}
	}

	/**
	 * This method updates the procurement bean and sets dates to null
	 * <ul>
	 * <li>1. Set procurement dates to null for :: Pre-Proposal Conference Date
	 * (Planned), Pre-Proposal Conference Date (Updated), Proposal Due Date
	 * (Planned), Proposal Due Date (Updated), First Draft of RFP & Evaluation
	 * Criteria Date (Planned), First Draft of RFP & Evaluation Criteria Date
	 * (Updated), Finalize RFP & Evaluation Criteria Date (Planned), Finalize
	 * RFP & Evaluation Criteria Date (Updated), Evaluator Training Date
	 * (Planned), Evaluator Training Date (Updated), First Round of Evaluation
	 * Completion Date (Planned), First Round of Evaluation Completion Date
	 * (Updated), Finalize Evaluation Date (Planned), Finalize Evaluation Date
	 * (Updated), Award Selection Date (Planned), Award Selection Date
	 * (Updated), Contract Start Date (Planned), Contract Start Date (Updated),
	 * Contract End Date (Planned)</li>
	 * <li>2. else do nothing</li>
	 * </ul>
	 * <ul>
	 * <li>Method added in R4</li>
	 * </ul>
	 * @param aoProcurementBean -
	 * 
	 * 
	 */
	private void updateProcurementDatesForOpenEnded(Procurement aoProcurementBean)
	{
		aoProcurementBean.setPreProposalConferenceDatePlanned(null);
		aoProcurementBean.setPreProposalConferenceDateUpdated(null);
		aoProcurementBean.setProposalDueDatePlanned(null);
		aoProcurementBean.setProposalDueDateUpdated(null);
		aoProcurementBean.setFirstRFPEvalDatePlanned(null);
		aoProcurementBean.setFirstRFPEvalDateUpdated(null);
		aoProcurementBean.setFinalRFPEvalDatePlanned(null);
		aoProcurementBean.setFinalRFPEvalDateUpdated(null);
		aoProcurementBean.setEvaluatorTrainingDatePlanned(null);
		aoProcurementBean.setEvaluatorTrainingDateUpdated(null);
		aoProcurementBean.setFirstEvalCompletionDatePlanned(null);
		aoProcurementBean.setFirstEvalCompletionDateUpdated(null);
		aoProcurementBean.setFinalEvalCompletionDatePlanned(null);
		aoProcurementBean.setFinalEvalCompletionDateUpdated(null);
		aoProcurementBean.setAwardSelectionDatePlanned(null);
		aoProcurementBean.setAwardSelectionDateUpdated(null);
		aoProcurementBean.setContractStartDatePlanned(null);
		aoProcurementBean.setContractStartDateUpdated(null);
		aoProcurementBean.setContractEndDatePlanned(null);
		aoProcurementBean.setContractEndDateUpdated(null);
	}

	/**
	 * This method sets the updated dates with the same values as of the planned
	 * dates for draft status.
	 * 
	 * <ul>
	 * 
	 * <li>Get all the planned date from the bean and set in the corresponding
	 * updated dates.</li>
	 * </ul>
	 * 
	 * 
	 * @param aoProcurementParam a procurement bean object
	 * 
	 */
	private void setUpdatedDatesForDraftProcurement(Procurement aoProcurementParam)
	{
		aoProcurementParam.setRfpReleaseDateUpdated((aoProcurementParam.getRfpReleaseDatePlanned()));
		aoProcurementParam.setProposalDueDateUpdated((aoProcurementParam.getProposalDueDatePlanned()));
		aoProcurementParam.setFirstRFPEvalDateUpdated((aoProcurementParam.getFirstRFPEvalDatePlanned()));
		aoProcurementParam.setFinalRFPEvalDateUpdated((aoProcurementParam.getFinalRFPEvalDatePlanned()));
		aoProcurementParam.setPreProposalConferenceDateUpdated((aoProcurementParam
				.getPreProposalConferenceDatePlanned()));
		aoProcurementParam.setAwardSelectionDateUpdated((aoProcurementParam.getAwardSelectionDatePlanned()));
		aoProcurementParam.setFinalEvalCompletionDateUpdated((aoProcurementParam.getFinalEvalCompletionDatePlanned()));
		aoProcurementParam.setFirstEvalCompletionDateUpdated((aoProcurementParam.getFirstEvalCompletionDatePlanned()));
		aoProcurementParam.setEvaluatorTrainingDateUpdated((aoProcurementParam.getEvaluatorTrainingDatePlanned()));
		aoProcurementParam.setContractStartDateUpdated((aoProcurementParam.getContractStartDatePlanned()));
		aoProcurementParam.setContractEndDateUpdated((aoProcurementParam.getContractEndDatePlanned()));
	}

	/**
	 * This method will handle pagination action from procurement roadmap
	 * screen.
	 * 
	 * <ul>
	 * <li>
	 * This method will check for next action parameter and call methods
	 * accordingly.</li>
	 * <li>set filtered bean in ProcurementFilter Session object.</li>
	 * </ul>
	 * 
	 * @param aoProcurementBean a procurement bean object
	 * @param aoRequest an Action Request object
	 * @param aoResponse an Action Response object
	 */
	@ActionMapping(params = "submit_action=fetchActiveProcurements")
	protected void actionPaginateProcurementRoadmap(@ModelAttribute("Procurement") Procurement aoProcurementBean,
			ActionRequest aoRequest, ActionResponse aoResponse)
	{

		aoResponse.setRenderParameter(HHSConstants.NEXT_PAGE_PARAM, aoRequest.getParameter(HHSConstants.NEXT_PAGE));
		aoRequest.getPortletSession().setAttribute(HHSConstants.PROCUREMENT_SESSION_BEAN, aoProcurementBean,
				PortletSession.PORTLET_SCOPE);
		if (null != aoRequest.getParameter(HHSConstants.FILTER_ITEM_KEY))
		{
			aoResponse.setRenderParameter(HHSConstants.FILTERED, aoRequest.getParameter(HHSConstants.FILTER_ITEM_KEY));
		}
	}

	/**
	 * This method will handle filter action from procurement roadmap screen.
	 * 
	 * <ul>
	 * <li>
	 * This method will check for next action parameter and call methods
	 * accordingly.</li>
	 * <li>Filter and sort values will be available in bean object.</li>
	 * <li>Set the procurement bean in session object</li>
	 * </ul>
	 * 
	 * @param aoProcurementBean a procurement bean object
	 * @param aoRequest an Action Request object
	 * @param aoResponse an Action Response object
	 */
	@ActionMapping(params = "submit_action=filterProcurement")
	protected void actionFilterProcurementRoadmap(@ModelAttribute("Procurement") Procurement aoProcurementBean,
			ActionRequest aoRequest, ActionResponse aoResponse)
	{
		// Begin QC 6531 REL 3.9.0
		aoProcurementBean.setFirstSort(HHSConstants.STATUS_PROCESS_TYPE_ID);
		aoProcurementBean.setSecondSort(HHSConstants.UPD_PROPOSAL_DUE_DATE);
		aoProcurementBean.setFirstSortType(HHSConstants.ASCENDING);
		aoProcurementBean.setSecondSortType(HHSConstants.ASCENDING);
		aoProcurementBean.setSortColumnName(HHSConstants.PROCUREMENT_STATUS);
		aoProcurementBean.setSecondSortDate(true);
		// End QC 6531 REL 3.9.0
		aoRequest.getPortletSession().setAttribute(HHSConstants.PROCUREMENT_SESSION_BEAN, aoProcurementBean,
				PortletSession.PORTLET_SCOPE);
		aoRequest.getPortletSession().setAttribute(HHSConstants.PROC_FILTERED, HHSConstants.PROC_FILTERED,
				PortletSession.PORTLET_SCOPE);
		aoResponse.setRenderParameter(HHSConstants.FILTERED, HHSConstants.FILTERED);
	}

	/**
	 * This method will handle add procurement action from procurement roadmap
	 * screen.
	 * 
	 * <ul>
	 * <li>
	 * This method redirect flow to showAddNewProcurementPage() method for
	 * screen S203</li>
	 * </ul>
	 * 
	 * @param aoRequest an Action Request object
	 * @param aoResponse an Action Response object
	 * @param aoProcurementBean Procurement Bean
	 * @param aoModelMap Model Map
	 */
	@ActionMapping(params = "submit_action=addNewProcurement")
	protected void actionAddProcurementRoadmap(@ModelAttribute("Procurement") Procurement aoProcurementBean,
			ActionRequest aoRequest, ActionResponse aoResponse, ModelMap aoModelMap)
	{
		try
		{
			setNavigationParamsInRender(aoRequest, aoResponse);
			getPagingParams(aoRequest.getPortletSession(), aoProcurementBean, null,
					HHSConstants.PROCUREMENT_ROADMAP_KEY);
			aoProcurementBean.setFirstSort(HHSConstants.UPDATED_RFP_RELEASE_DATE);
			aoProcurementBean.setSecondSort(HHSConstants.AGENCY_ID);
			aoProcurementBean.setFirstSortType(HHSConstants.ASCENDING);
			aoProcurementBean.setSecondSortType(HHSConstants.ASCENDING);
			aoProcurementBean.setSortColumnName(HHSConstants.RELEASE_DATE);
			aoProcurementBean.setFirstSortDate(true);
			aoProcurementBean.setSecondSortDate(false);
			aoRequest.getPortletSession().setAttribute(HHSConstants.PROCUREMENT_SESSION_BEAN, aoProcurementBean,
					PortletSession.PORTLET_SCOPE);
			aoModelMap.remove(HHSConstants.PROCUREMENT);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.VIEW_PROCUREMENT);
		}
		// Catch the exception thrown by transaction and set the error message
		// in request object and pass to jsp
		catch (ApplicationException loExp)
		{
			Map<String, Procurement> loParamMap = new HashMap<String, Procurement>();
			loParamMap.put(HHSConstants.PROCUREMENT_LOWERCASE, aoProcurementBean);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap, loExp);

		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error occured while adding new procurements", loExp);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);

		}
	}

	/**
	 * This method handles view procurement summary action from procurement
	 * roadmap screen for provider, accelerator and agency users
	 * 
	 * <ul>
	 * <li>Get the procurement Id from request</li>
	 * <li>Set navigation parameters in Render request</li>
	 * <li>Set procurement bean in session</li>
	 * <li>Redirect user to procurement summary render action</li>
	 * </ul>
	 * 
	 * @param aoProcurementBean a procurement model attribute
	 * @param aoRequest an Action Request object
	 * @param aoResponse an Action Response object
	 * @param aoModel a model map object
	 */
	@ActionMapping(params = "submit_action=viewProcurement")
	protected void actionViewProcurementFromRoadmap(@ModelAttribute("Procurement") Procurement aoProcurementBean,
			ActionRequest aoRequest, ActionResponse aoResponse, ModelMap aoModel)
	{
		String lsRenderAction = HHSConstants.PROCUREMENT_ROADMAP;
		try
		{
			String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID,
					aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			setNavigationParamsInRender(aoRequest, aoResponse);
			getPagingParams(aoRequest.getPortletSession(), aoProcurementBean, null,
					HHSConstants.PROCUREMENT_ROADMAP_KEY);
			aoProcurementBean.setFirstSort(HHSConstants.UPDATED_RFP_RELEASE_DATE);
			aoProcurementBean.setSecondSort(HHSConstants.AGENCY_ID);
			aoProcurementBean.setFirstSortType(HHSConstants.ASCENDING);
			aoProcurementBean.setSecondSortType(HHSConstants.ASCENDING);
			aoProcurementBean.setSortColumnName(HHSConstants.RELEASE_DATE);
			aoProcurementBean.setFirstSortDate(true);
			aoProcurementBean.setSecondSortDate(false);
			aoRequest.getPortletSession().setAttribute(HHSConstants.PROCUREMENT_SESSION_BEAN, aoProcurementBean,
					PortletSession.PORTLET_SCOPE);
			aoModel.remove(HHSConstants.PROCUREMENT);
			if (null != lsUserOrgType && lsUserOrgType.equalsIgnoreCase(ApplicationConstants.PROVIDER_ORG))
			{
				lsRenderAction = HHSConstants.PROCUREMENT_DETAILS;
			}
			else
			{
				lsRenderAction = HHSConstants.VIEW_PROCUREMENT;
			}
		}
		// Catch the exception thrown by transaction and set the error message
		// in request object and pass to jsp
		catch (ApplicationException loExp)
		{
			Map<String, Procurement> loParamMap = new HashMap<String, Procurement>();
			loParamMap.put(HHSConstants.PROCUREMENT_LOWERCASE, aoProcurementBean);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap, loExp);

		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception Occured while displaying procurement details : ", loExp);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);

		}
		aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, lsRenderAction);
	}

	/**
	 * This method will handle sort action from procurement roadmap screen.
	 * 
	 * <ul>
	 * <li>
	 * This method will check for next action parameter and call methods
	 * accordingly.</li>
	 * <li>Get sortType and columnName values by calling parseQueryString()
	 * method from class PortalUtil. Get sorting details by calling method
	 * getSortDetailsFromXML() from class BaseController.</li>
	 * <li>Set sorting and paging parameters in ProcurementFilter bean object by
	 * calling method getSortParams(), getPagingParams() from class
	 * ProcurementController and set this bean to ProcurementFilter Session
	 * object.</li>
	 * </ul>
	 * 
	 * @param aoProcurementBean a procurement bean object
	 * @param aoRequest an Action Request object
	 * @param aoResponse an Action Response object
	 */
	@ActionMapping(params = "submit_action=sortProcurement")
	protected void actionSortProcurementRoadmap(@ModelAttribute("Procurement") Procurement aoProcurementBean,
			ActionRequest aoRequest, ActionResponse aoResponse)
	{
		try
		{
			String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			String lsSortType = PortalUtil.parseQueryString(aoRequest, HHSConstants.SORT_TYPE);
			String lsColumnName = PortalUtil.parseQueryString(aoRequest, HHSConstants.COLUMN_NAME);
			getSortDetailsFromXML(lsColumnName, lsUserOrgType,
					PortalUtil.parseQueryString(aoRequest, HHSConstants.SORT_GRID_NAME), aoProcurementBean, lsSortType);
			aoRequest.getPortletSession().setAttribute(HHSConstants.PROCUREMENT_SESSION_BEAN, aoProcurementBean,
					PortletSession.PORTLET_SCOPE);
			if (null != aoRequest.getParameter(HHSConstants.FILTER_ITEM_KEY))
			{
				aoResponse.setRenderParameter(HHSConstants.FILTERED,
						aoRequest.getParameter(HHSConstants.FILTER_ITEM_KEY));
			}
		}
		// Catch the exception thrown by transaction and set the error message
		// in request object and pass to jsp
		catch (ApplicationException loExp)
		{
			Map<String, Procurement> loParamMap = new HashMap<String, Procurement>();
			loParamMap.put(HHSConstants.PROCUREMENT_LOWERCASE, aoProcurementBean);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap, loExp);
			LOG_OBJECT.Error("ApplicationException Occurred while sorting procurement list : ", loExp);
		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception Occurred while sorting procurement list : ", loExp);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);

		}
	}

	/**
	 * This method gets the users list from the database.
	 * 
	 * <ul>
	 * <li>1. Set both user types in the Map object</li>
	 * <li>2. Set the userMap in the channel object</li>
	 * <li>3. Execute the transaction "getNewProcurementDetails" and set the
	 * userList in the application session.</li>
	 * </ul>
	 * 
	 * @param aoRequest - ActionRequest
	 * @param aoResponse - ActionResponse
	 * @param aoProcurementBean
	 * @throws ApplicationException - throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	private void getUserList(RenderRequest aoRequest, RenderResponse aoResponse, Procurement aoProcurementBean)
			throws ApplicationException
	{
		Channel loChannelObj = new Channel();
		Map<String, String> loUserType = new HashMap<String, String>();
		loUserType.put(HHSConstants.USER_CITY, HHSConstants.USER_CITY);
		loUserType.put(HHSConstants.USER_AGENCY, HHSConstants.USER_AGENCY);
		loChannelObj.setData(HHSConstants.USER_TYPE, loUserType);
		loChannelObj.setData(HHSConstants.PROC_SUMMARY, aoProcurementBean);
		try
		{
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.GET_NEW_PROCUREMENT_DETAILS);

			Map<String, List<StaffDetails>> loUserMap = (Map<String, List<StaffDetails>>) loChannelObj
					.getData(HHSConstants.ACCELERATOR_USER_LIST);
			List<StaffDetails> loAccUserDetails = (List<StaffDetails>) loUserMap.get(HHSConstants.USER_CITY);
			List<StaffDetails> loAgencyUserDetails = (List<StaffDetails>) loChannelObj
					.getData(HHSConstants.AGENCY_USER_LIST);
			aoRequest.setAttribute(HHSConstants.ACC_USER_LIST, loAccUserDetails);
			aoRequest.setAttribute(HHSConstants.AGENCY_USER_LIST, loAgencyUserDetails);
			List<Procurement> loProgramNameList = (List<Procurement>) loChannelObj
					.getData(HHSConstants.PROGRAM_NAME_LIST);
			aoRequest.setAttribute(HHSConstants.PROGRAM_NAME_LIST, loProgramNameList);
		}
		// handling exception while fetching accelerator & agency users list
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("Exception Occured while getting users list : ", loExp);
			throw loExp;
		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			throw new ApplicationException("Exception Occured while getting users list : " + loExp);
		}
	}

	/**
	 * This method is used to render proc provider summary
	 * <ul>
	 * <li>1. Set asProcrumentId</li>
	 * <li>2. Execute the transaction "fetchProcurementSummary"</li>
	 * </ul>
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * @param aoRequest - RenderRequest
	 * @param aoResponse Response
	 * @return - String
	 */
	@SuppressWarnings("unchecked")
	@RenderMapping(params = "render_action=procurementPageRender")
	protected String renderProcurementSummaryProvider(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		Channel loChannel = new Channel();
		try
		{
			String lsProcurementId = PortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
			getPageHeader(aoRequest, lsProcurementId);
			loChannel.setData(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.TRAN_FETCH_PROC_SUMMARY);
			Procurement loProcurementBean = (Procurement) loChannel.getData(HHSConstants.LO_PROCUREMENT_BEAN);
			aoRequest.setAttribute(HHSConstants.LO_PROCUREMENT_BEAN, loProcurementBean);
			HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb
					.getInstance().getCacheObject(ApplicationConstants.APPLICATION_SETTING);
			String loReleaseTime = (String) loApplicationSettingMap.get(HHSConstants.PROPOSAL_RELEASE_TIME_KEY);
			aoRequest.setAttribute(HHSConstants.RELEASE_TIME, loReleaseTime);
		}
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error(HHSConstants.EXCEPTION_OCCURED_RENDERING_PROC_DETAILS_COLON, loExp);
			setGenericErrorMessage(aoRequest);
		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error(HHSConstants.EXCEPTION_OCCURED_RENDERING_PROC_DETAILS_COLON, loExp);
			setGenericErrorMessage(aoRequest);
		}
		return HHSConstants.PROC_SUM_PROVIDER;
	}

	/**
	 * This method is used to fetch the detail of the e-pin searched by the user
	 * <ul>
	 * <li>Get the selected E-pin ID from the request</li>
	 * <li>Set E-pin Id in Channel Object</li>
	 * <li>Execute <b>fetchEpinDetails_db</b> transaction</li>
	 * <li>get <b>aoEpinDetailsBean</b> from the Channel Object</li>
	 * </ul>
	 * 
	 * @param aoRequest action Request object
	 * @return EPinDetailBean details of the selected E-pin
	 * @throws ApplicationException ApplicationException object
	 */
	private static EPinDetailBean generateEpinDetailsBean(ResourceRequest aoRequest) throws ApplicationException
	{
		EPinDetailBean loEPinDetailBean = null;
		Channel loChannel = new Channel();
		String lsEpinKey = aoRequest.getParameter(HHSConstants.E_PIN_ID);
		if (null != lsEpinKey && !lsEpinKey.isEmpty())
		{
			loChannel.setData(HHSConstants.LS_EPIN_KEY, lsEpinKey);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_EPIN_DETAILS_DB);
			loEPinDetailBean = (EPinDetailBean) loChannel.getData(HHSConstants.AO_EPIN_DETAILS_BEAN);
		}
		return loEPinDetailBean;
	}

	/**
	 * This method will get the Epin list from the cache when user type three
	 * characters
	 * <ul>
	 * <li>Get the String entered by the user from request</li>
	 * <li>Get <b>procurementEpin</b> from Request</li>
	 * <li>Get all unassigned E-Pin from the cache</li>
	 * <li>Create the formatted json array from the list retrieved from cache
	 * and set it to print writer</li>
	 * </ul>
	 * @param aoRequest ResourceRequest Object
	 * @param aoResponse ResourceResponse Object
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             ApplicationException
	 */
	@ResourceMapping("getEpinListResourceUrl")
	public void getEpinListResourceRequest(ResourceRequest aoRequest, ResourceResponse aoResponse)
			throws ApplicationException
	{
		try
		{
			getEpinList(aoRequest, aoResponse);
		}
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("error occurred while getting epin master list :", loExp);
		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("error occurred while getting epin master list :", loExp);
		}

	}

	/**
	 * Below method will be executed when user search the E-pin and click on
	 * E-pin Details button
	 * <ul>
	 * <li>Get the selected E-pin Id from the request object.</li>
	 * <li>Execute <code>generateEpinDetailsBean</code> method to get the E-pin
	 * Detail bean</li>
	 * <li>Generate json formated string buffer with the details of the E-pin</li>
	 * </ul>
	 * @param aoRequest ResourceRequest Object
	 * @param aoResponse ResourceResponse Object
	 * @throws ApplicationException Wrap the Exception into ApplicationException
	 */
	@ResourceMapping("getEpinDetailsResourceUrl")
	public void getEpinDetailsResourceRequest(ResourceRequest aoRequest, ResourceResponse aoResponse)
	{
		PrintWriter loOut = null;
		try
		{
			loOut = aoResponse.getWriter();
			StringBuffer loSbData = new StringBuffer();
			EPinDetailBean loEpinDetailsBean = generateEpinDetailsBean(aoRequest);
			if (null != loEpinDetailsBean)
			{   
				aoResponse.setContentType(HHSConstants.TEXT_HTML);
				loSbData.append("{\"epinDetails\": [{\"EpinId\": \"").append(loEpinDetailsBean.getEpinId())
						.append("\",\"ProcurementStartDate\": \"").append(loEpinDetailsBean.getProcurementStartDate())
						//R6: Agency division converted to upper case
						.append("\", \"AgencyDiv\": \"").append(loEpinDetailsBean.getAgencyDiv().toUpperCase())
						.append("\", \"AgencyId\": \"").append(loEpinDetailsBean.getAgencyId())
						.append("\", \"Description\": \"").append(loEpinDetailsBean.getDescription())
						.append("\", \"ProjProg\": \"").append(loEpinDetailsBean.getProjProg())
						.append("\", \"RefEpinId\": \"").append(loEpinDetailsBean.getRefAptEpinId()).append("\"}]}");

			}
			else
			{
				loSbData.append("{\"epinDetails\": [{\"ErrorMessage\": \"").append(HHSConstants.NO_EPIN_ENTRIES)
						.append("\"}]}");
			}
			loOut.write(loSbData.toString());
		}
		catch (ApplicationException aoExp)
		{
			setGenericErrorMessage(aoRequest);
			LOG_OBJECT.Error("Error occurred while fetching epin details", aoExp);
		}
		catch (Exception aoExp)
		{
			setGenericErrorMessage(aoRequest);
			LOG_OBJECT.Error("Error occurred while fetching epin details", aoExp);
		}
		finally
		{
			if (null != loOut)
			{
				loOut.flush();
				loOut.close();
			}
		}
	}

	/**
	 * This method fetch the program list from the database.
	 * 
	 * <ul>
	 * <li>Gets agency id from request and set it into procurement bean
	 * reference.</li>
	 * <li>Set procurement bean reference in the channel object.</li>
	 * <li>Execute the transaction "fetchPragramNameList" and set the program
	 * Name List in request.</li>
	 * </ul>
	 * 
	 * @param aoRequest
	 */
	/**
	 * @param aoRequest ResourceRequest Object
	 * @param aoResponse ResourceResponse Object
	 * @return ModelAndView
	 */
	@Override
	@SuppressWarnings("unchecked")
	@ResourceMapping("getProgramListForAgency")
	public ModelAndView getProgramListOnAjaxCall(ResourceRequest aoRequest, ResourceResponse aoResponse)
	{
		ModelAndView loModelAndView = null;
		String lsAgencyId = aoRequest.getParameter(HHSConstants.AGENCYID);
		String lsRoadMapFilter = aoRequest.getParameter(HHSConstants.ROADMAP_FILTER);
		Procurement loProcurement = new Procurement();
		loProcurement.setAgencyId(lsAgencyId);
		List<Procurement> loProgramNameList = null;
		List<StaffDetails> loAgencyUserList = null;
		Channel loChannel = new Channel();
		Map<String, String> loUserType = new HashMap<String, String>();
		loUserType.put(HHSConstants.USER_CITY, HHSConstants.USER_CITY);
		loUserType.put(HHSConstants.USER_AGENCY, HHSConstants.USER_AGENCY);
		loChannel.setData(HHSConstants.PROC_SUMMARY, loProcurement);
		loChannel.setData(HHSConstants.AGENCY_ORG, Boolean.TRUE);
		loChannel.setData(HHSConstants.USER_TYPE, loUserType);
		try
		{
			if (lsRoadMapFilter == null || !lsRoadMapFilter.equalsIgnoreCase(HHSConstants.FILTER))
			{
				getPageHeader(aoRequest, null);
			}
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_PROG_NAME_LIST);
			loProgramNameList = (List<Procurement>) loChannel.getData(HHSConstants.PROGRAM_NAME_LIST);
			loAgencyUserList = (List<StaffDetails>) loChannel.getData(HHSConstants.AGENCY_USER_LIST);
			aoRequest.setAttribute(HHSConstants.NAME_TO_BE_DISPLAYED, HHSConstants.PROGRAM_NAME_UPPERCASE);
			aoRequest.setAttribute(HHSConstants.NAME_OF_THE_DROPDOWN, HHSConstants.PROGRAM_NAME_LOWERCASE);
			aoRequest.setAttribute(HHSConstants.DROPDOWN_TO_BE_CHANGED, HHSConstants.PROGRAM_NAME_ID);
			aoRequest.setAttribute(HHSConstants.PROGRAM_NAME_LIST, loProgramNameList);
			aoRequest.setAttribute(HHSConstants.AGENCY_USER_LIST, loAgencyUserList);
		}
		// Catch the exception thrown by transaction and set the error message
		// in request object and pass to jsp
		catch (ApplicationException loEx)
		{
			LOG_OBJECT.Error("Exception Occured while getting program name list for NYC Agency ", loEx);
			setGenericErrorMessage(aoRequest);
		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception Occured while getting program name list for NYC Agency ", loExp);
			setGenericErrorMessage(aoRequest);
		}
		if (lsRoadMapFilter != null && lsRoadMapFilter.equalsIgnoreCase(HHSConstants.FILTER))
		{
			loModelAndView = new ModelAndView(HHSConstants.PROCUREMENT_ROADMAP);
		}
		else
		{
			loModelAndView = new ModelAndView(HHSConstants.ADD_PROCUREMENT_LOWERCASE);
		}
		return loModelAndView;
	}

	/**
	 * This method renders the procurement summary Changes made for enhancement
	 * 6448 for Release 3.8.0
	 * <ul>
	 * <li>1. Get the procurement Id from request and set in the channel.</li>
	 * <li>1. Call getPageHeader() method for navigation.</li>
	 * <li>1. Set the usertype in the map.</li>
	 * <li>1. Set the map and procurement id in the channel.</li>
	 * <li>2. Execute the transaction "getProcurementSummary" and</li>
	 * <li>3. Get the result from channel and set in the request.</li>
	 * <li>4. Call method getAgencyMap() and set the agency map in request.</li>
	 * <li>5. Get user list from application session and set in request.</li>
	 * <li>6. Call method executeRuleForProcurementSummary() to execute rules
	 * and set the results of rules in request.</li>
	 * <li>7. Return ModelAndView containing name of the jsp and the data to be
	 * rendered on the page.</li>
	 * </ul>
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * @param aoRequest - RenderRequest
	 * @param aoResponse - RenderResponse
	 * @return ModelAndView
	 */
	@SuppressWarnings("unchecked")
	@RenderMapping(params = "render_action=viewProcurement")
	protected ModelAndView renderProcurementSummary(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		Channel loChannel = new Channel();
		Procurement loProcurementParam = null;
		try
		{
			String lsProcurementId = PortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
			getPageHeader(aoRequest, lsProcurementId);
			List<StaffDetails> loAccUserDetails = null;
			List<StaffDetails> loAgencyUserDetails = null;
			String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			if (lsProcurementId != null && !lsProcurementId.equals(HHSConstants.EMPTY_STRING))
			{
				loProcurementParam = renderProcurementSummaryFinal(aoRequest, loChannel, lsProcurementId,
						loAccUserDetails, loAgencyUserDetails, lsUserOrgType);
			}
			else
			{
				loProcurementParam = (Procurement) ApplicationSession.getAttribute(aoRequest,
						HHSConstants.PROCUREMENT_BEAN);
				loChannel.setData(HHSConstants.ORG_TYPE, lsUserOrgType);
				aoRequest.setAttribute(HHSConstants.CHANNEL_ACCESS, loChannel);
				getUserList(aoRequest, aoResponse, loProcurementParam);
			}
			HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb
					.getInstance().getCacheObject(ApplicationConstants.APPLICATION_SETTING);
			String loReleaseTime = (String) loApplicationSettingMap.get(HHSR5Constants.PROPOSAL_RELEASE_TIME_KEY);
			aoRequest.setAttribute(HHSConstants.RELEASE_TIME, loReleaseTime);
			aoRequest.setAttribute(HHSConstants.NYC_AGENCY, HHSUtil.getAgencyMapForProcurement());
			aoRequest.setAttribute(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
					aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE));
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE_TYPE));
			executeRuleForProcurementSummary(aoRequest, loChannel, loProcurementParam);
			if (aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE) != null)
			{
				String lsErrorMsg = (String) aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE);
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
			}
			// START || Changes done for enhancement 6448 for Release 3.8.0
			if (PortalUtil.parseQueryString(aoRequest, HHSConstants.CLOSE_PROCUREMENT_SUCCESS) != null
					&& PortalUtil.parseQueryString(aoRequest, HHSConstants.CLOSE_PROCUREMENT_SUCCESS).equals(
							HHSConstants.NO))
			{
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.PENDING_APPROVE_AWARD));
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
				aoRequest.setAttribute(HHSConstants.CLOSE_PROCUREMENT_SUCCESS, HHSConstants.NO);
			}
			// END || Changes done for enhancement 6448 for Release 3.8.0
			if (aoRequest.getParameter(HHSConstants.PARAM_VALUE) != null
					&& aoRequest.getParameter(HHSConstants.PARAM_VALUE).equalsIgnoreCase(
							HHSConstants.CANCEL_EVALUATION_TASK))
			{
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.CANCEL_EVALUATION_TASK_SUCESS));
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_PASS_TYPE);
			}
		}
		// Handling Exception while rendering procurement summary
		catch (ApplicationException loExp)
		{
			setGenericErrorMessage(aoRequest);
		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error(HHSConstants.EXCEPTION_OCCURED_RENDERING_PROC_DETAILS_COLON, loExp);
			setGenericErrorMessage(aoRequest);
		}
		if (loProcurementParam == null)
		{
			loProcurementParam = new Procurement();
		}
		return new ModelAndView(HHSConstants.ADD_PROCUREMENT_LOWERCASE, HHSConstants.PROCUREMENT, loProcurementParam);
	}

	/**
	 * This method renders the procurement summary
	 * <ul>
	 * <li>1. Set the usertype in the map.</li>
	 * <li>1. Set the map and procurement id in the channel.</li>
	 * <li>2. Execute the transaction "getProcurementSummary" and</li>
	 * <li>3. Get the result from channel and set in the request.</li>
	 * <li>4. Call method getAgencyMap() and set the agency map in request.</li>
	 * <li>5. Get user list from application session and set in request.</li>
	 * <li>6. Call method executeRuleForProcurementSummary() to execute rules
	 * and set the results of rules in request.</li>
	 * <li>7. Return ModelAndView containing name of the jsp and the data to be
	 * rendered on the page.</li>
	 * </ul>
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Updated Method for build no 3.2.0 for enhancement#5684 -->
	 * </ul>
	 * 
	 * @param aoRequest RenderRequest
	 * @param loChannel channel object
	 * @param lsProcurementId procurement id
	 * @param loAccUserDetails list of acc user details
	 * @param loAgencyUserDetails list of agency user details
	 * @param lsUserOrgType org type
	 * @return Procurement object
	 * @throws ApplicationException If an exception occurs
	 * @throws NumberFormatException If a NumberFormatException Occurs
	 */
	private Procurement renderProcurementSummaryFinal(RenderRequest aoRequest, Channel loChannel,
			String lsProcurementId, List<StaffDetails> loAccUserDetails, List<StaffDetails> loAgencyUserDetails,
			String lsUserOrgType) throws ApplicationException, NumberFormatException
	{
		Procurement loProcurementParam;
		Map<String, String> loOrgTpyeMap = new HashMap<String, String>();
		P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		loOrgTpyeMap.put(HHSConstants.USER_CITY, HHSConstants.USER_CITY);
		loOrgTpyeMap.put(HHSConstants.USER_AGENCY, HHSConstants.USER_AGENCY);
		loChannel.setData(HHSConstants.USER_TYPE, loOrgTpyeMap);
		loChannel.setData(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
		loChannel.setData(HHSConstants.ORG_TYPE, lsUserOrgType);
		loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.GET_PROCUREMENT_SUMMARY);
		List<Procurement> loProgramNameList = (List<Procurement>) loChannel.getData(HHSConstants.PROGRAM_NAME_LIST);
		loProcurementParam = (Procurement) loChannel.getData(HHSConstants.PROC_SUMMARY);

		// Start of change for Release 3.2.0 enhancement 5684
		// check if procurement is open ended or zero valued procurement for all
		// Planned Procurements since 'Generate PCOF Task' button is enabled on
		// Procurement Summary Screen
		// only for close ended or non zero planned procurements
		if (loProcurementParam != null
				&& loProcurementParam.getStatus() != null
				&& (loProcurementParam.getStatus() == Integer.parseInt(PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_PLANNED))))
		{

			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.CHECK_IF_OPENENDED_ZEROVALUE);
			boolean lbIsOpenEndedOrZeroValue = (Boolean) loChannel.getData(HHSConstants.IS_OPEN_ENDED_OR_ZERO_VALUE);
			aoRequest.setAttribute(HHSConstants.IS_OPEN_ENDED_OR_ZERO_VALUE, lbIsOpenEndedOrZeroValue);

		}

        /*[Start] R7.2.0 QC 9058 9063 to remove Save button and disable all editable items */
        PortletSession loSession  = aoRequest.getPortletSession();
        if( loSession.getAttribute(ApplicationConstants.KEY_SESSION_ROLE_CURRENT, PortletSession.APPLICATION_SCOPE) != null
                && ApplicationConstants.ROLE_OBSERVER.equalsIgnoreCase((String)loSession.getAttribute(ApplicationConstants.KEY_SESSION_ROLE_CURRENT, PortletSession.APPLICATION_SCOPE)))  {
            aoRequest.setAttribute(HHSConstants.READ_ONLY_SEC, true);
        }
        /*[End] R7.2.0 QC 9058 9063 to remove Save button and disable all editable items   */ 

		// End of change for Release 3.2.0 enhancement 5684
		if (loProcurementParam != null
			&& loProcurementParam.getStatus() != null
			&& (loProcurementParam.getStatus() == Integer.parseInt(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_CANCELLED)) 
			    || loProcurementParam.getStatus() == Integer.parseInt(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_CLOSED))) )
		{
			loProcurementParam.setStatus(loProcurementParam.getPreviousStatus());
			aoRequest.setAttribute(HHSConstants.READ_ONLY_SEC, true);
		}
		Integer loProcurementAddendumDataCount = (Integer) loChannel.getData(HHSConstants.PROC_ADDENDUM_DATA_COUNT);
		if (loProcurementAddendumDataCount != null && loProcurementAddendumDataCount > 0)
		{
			aoRequest.setAttribute(HHSConstants.UNPUBLISHED_DATA_MSG_KEY, HHSConstants.UNPUBLISHED_DATA_MSG);
		}
		if (loProcurementParam != null
				&& (loProcurementParam.getProcurementEpin() == null || loProcurementParam.getProcurementEpin().equals(
						HHSConstants.EMPTY_STRING)))
		{
			loProcurementParam.setProcurementEpin(HHSConstants.PENDING);
		}
		Map<String, List<StaffDetails>> loUserMap = (Map<String, List<StaffDetails>>) loChannel
				.getData(HHSConstants.ACCELERATOR_USER_LIST);
		if (loUserMap != null)
		{
			loAccUserDetails = (List<StaffDetails>) loUserMap.get(HHSConstants.USER_CITY);
			loAgencyUserDetails = (List<StaffDetails>) loChannel.getData(HHSConstants.AGENCY_USER_LIST);
		}
		if (null != loProcurementParam)
		{
			aoRequest.setAttribute(HHSConstants.RFP_RELEASED_DATE,
					HHSUtil.getUtilDate(loProcurementParam.getRfpReleaseDateUpdated()));
			aoRequest.setAttribute(HHSConstants.PROCUREMENT_STATUS, loProcurementParam.getStatus());
		}
		aoRequest.setAttribute(HHSConstants.PROGRAM_NAME_LIST, loProgramNameList);
		aoRequest.setAttribute(HHSConstants.ACC_USER_LIST, loAccUserDetails);
		aoRequest.setAttribute(HHSConstants.AGENCY_USER_LIST, loAgencyUserDetails);
		aoRequest.setAttribute(HHSConstants.CHANNEL_ACCESS, loChannel);
		aoRequest.setAttribute(HHSConstants.CONTRACT_REGISTERED,
				(Boolean) loChannel.getData(HHSConstants.CONTRACT_REGISTERED));
		aoRequest.setAttribute(HHSConstants.TASK_LAUNCH_VARIABLE,
				(Boolean) loChannel.getData(HHSConstants.INVOICE_LB_STATUS));
		aoRequest.setAttribute(HHSConstants.ORG_TYPE, (Boolean) loChannel.getData(HHSConstants.LS_USER_ORG_TYPE));
		return loProcurementParam;
	}

	/**
	 * This method sets the values in the channel required for executing all the
	 * rules for the jsp page and then execute those rules and set results in
	 * the request.
	 * 
	 * <ul>
	 * <li>1. Get user type and user role from the session.</li>
	 * <li>2. Set the fetched data in the channel.</li>
	 * <li>2. Execute the rules by calling evaluateRule method.</li>
	 * <li>3. set results of the rules executed in the request.</li>
	 * </ul>
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * @param aoRequest request
	 * @param aoChannel channel
	 * @param aoProcurementParam procurement bean
	 * @throws ApplicationException when exception occurs
	 */
	private void executeRuleForProcurementSummary(RenderRequest aoRequest, Channel aoChannel,
			Procurement aoProcurementParam) throws ApplicationException
	{
		PortletSession loSession = aoRequest.getPortletSession();
		String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
		String lsUserRole = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ROLE,
				PortletSession.APPLICATION_SCOPE);
		Boolean loProcurementStatusDraft = false;
		Boolean loProcurementStatusNotDraft = false;
		aoChannel.setData(HHSConstants.ORG_TYPE, lsUserOrgType);
		aoChannel.setData(HHSConstants.USER_ROLE, lsUserRole);
		// If procurement status is not null then set the status in the channel
		if (aoProcurementParam != null
				&& (aoProcurementParam.getProcurementStatus() != null && !(aoProcurementParam.getProcurementStatus()
						.equals(HHSConstants.EMPTY_STRING) || aoProcurementParam.getProcurementStatus()
						.equalsIgnoreCase(HHSConstants.NULL)))
				&& aoProcurementParam.getProcurementStatus().equalsIgnoreCase(HHSConstants.PROCUREMENT_STATUS_PLANNED))
		{
			loProcurementStatusNotDraft = true;
			// Else set the status as Draft in the channel
		}
		else if (aoProcurementParam == null
				|| (aoProcurementParam.getProcurementStatus() != null && !(aoProcurementParam.getProcurementStatus()
						.equals(HHSConstants.EMPTY_STRING) || aoProcurementParam.getProcurementStatus()
						.equalsIgnoreCase(HHSConstants.NULL)))
				&& aoProcurementParam.getProcurementStatus().equalsIgnoreCase(HHSConstants.DRAFT))
		{
			loProcurementStatusDraft = true;
		}
		aoRequest.setAttribute(HHSConstants.LB_PROC_STATUS_DRAFT, loProcurementStatusDraft);
		aoRequest.setAttribute(HHSConstants.LB_PROC_STATUS_NOT_DRAFT, loProcurementStatusNotDraft);
	}

	/**
	 * This function will load service selection page containing list of
	 * services fetched from cache to display on Service Selection page
	 * <ul>
	 * <li>1.Setting procurementStatus and procurementId</li>
	 * <li>2.Call the getSelectedServiceAccelerator Transaction to fetch list of
	 * services and alreay saved services if any respective to particular
	 * procurement</li>
	 * <li>3.Setting finalTreeAsString which is list of all the services from
	 * cache</li>
	 * <li>4.Setting Selected Service List</li>
	 * <li>5.Loading fetched data in serviceSelection.jsp page and display it</li>
	 * </ul>
	 * @param aoRequest - a RenderRequest object
	 * @param aoResponse - a RenderResponse object
	 * @return jsp page name
	 * @throws ApplicationException
	 */

	@SuppressWarnings("unchecked")
	@RenderMapping(params = "render_action=serviceSelectionRender")
	protected String renderServiceSelectionPage(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		// get required parameters from the request
		String lsProcurementId = PortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
		String lsProcurementStatus = aoRequest.getParameter(HHSConstants.PROCUREMENT_STATUS);
		try
		{
			Channel loChannel = new Channel();
			Boolean loStatusFlag = Boolean.FALSE;
			// set header
			getPageHeader(aoRequest, lsProcurementId);
			// get the values from the session
			String lsUserOrg = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			String lsUserRole = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ROLE, PortletSession.APPLICATION_SCOPE);
			aoRequest.setAttribute(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			aoRequest.setAttribute(HHSConstants.PROCUREMENT_STATUS, lsProcurementStatus);
			String lsErrorMsg = PortalUtil.parseQueryString(aoRequest, HHSConstants.ERROR_MSG);
			if (lsErrorMsg != null)
			{
				return HHSConstants.SERVICE_SELECTION;
			}

			String lsFromCache = HHSConstants.FALSE;
			// set the values into the channel
			loChannel.setData(HHSConstants.ORGTYPE, lsUserOrg);
			loChannel.setData(HHSConstants.ORG_TYPE, lsUserOrg);
			loChannel.setData(ApplicationConstants.ELEMENT_TYPE, ApplicationConstants.SERVICE_AREA);
			loChannel.setData(HHSConstants.AB_FROM_CACHE, lsFromCache);
			loChannel.setData(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
			loChannel.setData(HHSConstants.AGENCY_ROLE, lsUserRole);
			// set the rule for the page
			Boolean loSaveButtonStatus = Boolean.valueOf((String) Rule.evaluateRule(
					HHSConstants.SERVICE_SELECTION_SAVE_RULE, loChannel));
			aoRequest.setAttribute(HHSConstants.LO_ELEMENT_ID_LIST,
					ApplicationSession.getAttribute(aoRequest, HHSConstants.ACTIVE_FLAG_LIST));
			// hit the transaction
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.GET_SELECTED_SERVICE_ACC);
			if (loChannel.getData(HHSConstants.PROC_STA_ID) != null
					&& (Integer) loChannel.getData(HHSConstants.PROC_STA_ID) == 7)
			{
				loStatusFlag = Boolean.TRUE;
			}
			aoRequest.setAttribute(HHSConstants.IS_PROC_CANCELLED, loStatusFlag);

			// get the transaction response data
			Integer loProcurementAddendumDataCount = (Integer) loChannel.getData(HHSConstants.PROC_ADDENDUM_DATA_COUNT);
			if (loProcurementAddendumDataCount != null && loProcurementAddendumDataCount > 0)
			{
				aoRequest.setAttribute(HHSConstants.UNPUBLISHED_DATA_MSG_KEY, HHSConstants.UNPUBLISHED_DATA_MSG);
			}
			String lsTaxonomyTree = (String) loChannel.getData(HHSConstants.LO_TAX_TREE);
			String lsIsSave = (String) ApplicationSession.getAttribute(aoRequest, HHSConstants.IS_SAVE);
			if (lsIsSave == null || lsIsSave.equalsIgnoreCase(HHSConstants.TRUE))
			{
				aoRequest.setAttribute(HHSConstants.SELECTED_SERVICES_LIST,
						(List<SelectedServicesBean>) loChannel.getData(HHSConstants.SAVED_SERVICES_LIST));
			}
			else
			{
				aoRequest.setAttribute(HHSConstants.SELECTED_SERVICES_LIST,
						ApplicationSession.getAttribute(aoRequest, HHSConstants.LO_INSERT_LIST));
				aoRequest.setAttribute(HHSConstants.IS_SAVE, lsIsSave);
			}
			// set the required values into the request
			aoRequest.setAttribute(HHSConstants.FINAL_TREE_AS_STRING, lsTaxonomyTree);
			aoRequest.setAttribute(HHSConstants.SAVE_BUTTON_STATUS, loSaveButtonStatus);
			aoRequest.setAttribute(HHSConstants.CHANNEL_ACCESS, loChannel);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setGenericErrorMessage(aoRequest);
		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception Occured while loading services details : ", loExp);
			setGenericErrorMessage(aoRequest);
		}

		return HHSConstants.SERVICE_SELECTION;
	}

	/**
	 * The Method handles the landing of Publish Procurement page.The page is
	 * gateway for Publishing procurement.After Entering the Credential user can
	 * start process for publish procurement
	 * 
	 * <ul>
	 * <li>1.Setting Navigation Tab Related information using getHeader</li>
	 * <li>2.Check if publish Procurement done ,proceed for 3,4</li>
	 * <li>3.Setting List of Selected Service whose Evidence flag is 0</li>
	 * <li>4.Setting Error Message and Type which is obtain from Submit Method</li>
	 * <li>5.Land on publishProcurement jsp</li>
	 * <ul>
	 * 
	 * @param aoRequest RenderRequest
	 * @param aoResponse RenderResponse
	 * @return publishProcurement String
	 */
	@SuppressWarnings("unchecked")
	@RenderMapping(params = "render_action=renderpublishProcurement")
	protected String renderPublishProcurement(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		try
		{
			getPageHeader(aoRequest, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			Channel loChannel = new Channel();
			loChannel.setData(HHSConstants.PROCUREMENT_ID_KEY, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			loChannel.setData(HHSConstants.ORG_TYPE, lsUserOrgType);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_PROC_ADDENDUM_DATA);
			Integer loProcurementAddendumDataCount = (Integer) loChannel.getData(HHSConstants.PROC_ADDENDUM_DATA_COUNT);
			if (loProcurementAddendumDataCount != null && loProcurementAddendumDataCount > 0)
			{
				aoRequest.setAttribute(HHSConstants.UNPUBLISHED_DATA_MSG_KEY, HHSConstants.UNPUBLISHED_DATA_MSG);
			}
			if (aoRequest.getParameter(HHSConstants.PUB_PROC_FLAG) != null)
			{
				List<String> loServiceNameList = (List<String>) ApplicationSession.getAttribute(aoRequest,
						HHSConstants.SER_NAME_LIST);
				aoRequest.setAttribute(HHSConstants.SER_NAME_LIST, loServiceNameList);
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
						aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE));
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
						aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE_TYPE));
				aoRequest.setAttribute(HHSConstants.MISSING_INFO_LIST,
						ApplicationSession.getAttribute(aoRequest, HHSConstants.MISSING_INFO_LIST));
			}
			aoRequest.setAttribute(HHSConstants.CHANNEL_ACCESS, loChannel);
		}
		// handing Application Exception
		catch (ApplicationException loExp)
		{
			setGenericErrorMessage(aoRequest);
		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception Occured while rendering Publish Procurement", loExp);
			setGenericErrorMessage(aoRequest);
		}
		return HHSConstants.PUB_PROC;
	}

	/**
	 * The method handles the flow after the submission of publish procurement
	 * and server side validation
	 * 
	 * <ul>
	 * <li>1. Setting Navigation Render Parameter</li>
	 * <li>2. Perform Server Side Validation on data entered by the user.</li>
	 * <li>3 If Validation passes then call validate User for Authentication.</li>
	 * <li>4. Make call to the transaction for publish procurement if user is
	 * authenticated</li>
	 * <li>5. Get the Service List whose Evidence Flag is 0 [call
	 * authenticateLoginUser method in transaction]</li>
	 * <li>6.If any of the service against Procurement having EvidenceFlag 0 ,We
	 * can't proceed further[call validateUpdateProcurementStatus,QueryId
	 * :getElementIdList]</li>
	 * <li>7.If this is the first time the procurement is being published [Call
	 * getProcurementStatus to check the status Query Id : getProcurementStatus]
	 * (meaning the procurement is now moving from Draft to Planned status), set
	 * the Procurement Certification of Funds status to Not Submitted.[call
	 * updateProcurementCoFStatus method QueryIdupdateProcurementCoFStatus]</li>
	 * <li>.8 Move Procurment_Addendum data to Procurment table if exists</li>
	 * <li>.9 Move Procurment_Services_Addendum data to Procurment_Services
	 * table if exists</li>
	 * <li>10.Update the procurement status Planned [call
	 * updateProcurementStatus QueryId :updateProcurementStatus].</li>
	 * <li>11.Update the RFP document status as Submitted [call
	 * updateRFPdocumentStatus QueryId :updateRFPdocumentStatus].</li>
	 * <li>12.Update Last Published Details[Call Service
	 * :modifyLastPublishedDetails,QueryId :updateLastPublishedDetails]</li>
	 * <li>13.Perform the Audit Log [Call Service :hhsauditInsert]</li>
	 * <li>14.Call the process notification[Call Service :processNotification]</li>
	 * <li>15. Direct user to S201 with Sucess Message</li>
	 * <ul>
	 * 
	 * @param aoAuthParam AuthenticationBean
	 * @param aoResult BindingResult
	 * @param aoRequest ActionRequest
	 * @param aoResponse ActionResponse
	 */
	@SuppressWarnings("unchecked")
	@ActionMapping(params = "submitAction=publishProcurement")
	protected void submitProcurement(@ModelAttribute("AuthenticationBean") AuthenticationBean aoAuthParam,
			BindingResult aoResult, ActionRequest aoRequest, ActionResponse aoResponse)
	{
		HashMap loHmReqExceProp = new HashMap();
		LOG_OBJECT.Info("Entered into Publish Procurement details::" + loHmReqExceProp.toString());
		try
		{
			setNavigationParamsInRender(aoRequest, aoResponse);
			validator.validate(aoAuthParam, aoResult);
			String lsProcurementId = aoAuthParam.getProcurementId();
			aoResponse.setRenderParameter(HHSConstants.PUB_PROC_FLAG, HHSConstants.YES);
			aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			loHmReqExceProp.put(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			// Checking if No Validation Error
			if (!aoResult.hasErrors())
			{
				Map<String, Object> loAuthenticateMap = validateUser(aoAuthParam.getUserName(),
						aoAuthParam.getPassword(), aoRequest);
				Boolean loAuthStatus = (Boolean) loAuthenticateMap.get(HHSConstants.IS_VALID_USER);
				loHmReqExceProp.put(HHSConstants.LB_AUTH_STATUS, loAuthStatus);
				// Checking if Authentication Status is not false
				if (!loAuthStatus)
				{
					aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.RENDER_PUB_PROC);
					aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.MESSAGE_M38));
					aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
							ApplicationConstants.MESSAGE_FAIL_TYPE);
				}
				else
				{
					submitProcurementForAuthenticUser(aoAuthParam, aoRequest, aoResponse, loHmReqExceProp, loAuthStatus);
				}
			}
			else
			{
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.RENDER_PUB_PROC);
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework

		catch (ApplicationException aoEx)
		{
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loHmReqExceProp, aoEx);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.RENDER_PUB_PROC);
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while process Publish Procurement:", aoEx);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.RENDER_PUB_PROC);
		}
	}

	/**
	 * This method is used to submit one procurement if the user credentials
	 * entered are valid
	 * <ul>
	 * <li>If the user is authenticated</li>
	 * <li>call the private method <code>publishProcurement</code></li>
	 * <li>If any error occurred then set the error message in request object</li>
	 * <li>Else display success message to the user</li>
	 * </ul>
	 * @param aoAuthParam authentication bean
	 * @param aoRequest action request object
	 * @param aoResponse action response object
	 * @param aoHmReqExceProp required parameter map
	 * @param aoAuthStatus authentication status
	 * @throws ApplicationException if any exception occurred
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private void submitProcurementForAuthenticUser(AuthenticationBean aoAuthParam, ActionRequest aoRequest,
			ActionResponse aoResponse, HashMap aoHmReqExceProp, Boolean aoAuthStatus) throws ApplicationException
	{
		try
		{
			Channel loChannel = publishProcurement(aoRequest, aoAuthParam, aoAuthStatus);
			Map<String, Object> loServiceData = (Map<String, Object>) loChannel.getData(HHSConstants.SERV_NAME_MAP);
			Boolean loEvidenceErrorFlag = (Boolean) loServiceData.get(HHSConstants.EVIDENCE_ERROR_FLAG);
			Boolean loServicesListError = (Boolean) loServiceData.get(HHSConstants.SERV_LIST_ERROR);
			aoHmReqExceProp.put(HHSConstants.LO_SERVICE_DATA, loServiceData);
			// checking if Evidence Error Flag not null and true
			if (loEvidenceErrorFlag != null && loEvidenceErrorFlag)
			{
				ApplicationSession.setAttribute((List<String>) loServiceData.get(HHSConstants.SER_NAME_LIST),
						aoRequest, HHSConstants.SER_NAME_LIST);
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.PROC_SERVICE_ASS_ERR));
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.RENDER_PUB_PROC);
				aoResponse.setRenderParameter(HHSConstants.RESET_SESSION_PROC, HHSConstants.TRUE);
				aoResponse.setRenderParameter(HHSConstants.RESET_SESSION_PROC, HHSConstants.TRUE);
			}

			// checking if Service List Error Flag not null and true
			if (loServicesListError != null && loServicesListError)
			{
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.PUBLISH_RFP_MISSING_INFO));
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE);
				List<String> loMissingInfoList = new ArrayList<String>();
				loMissingInfoList.add(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.SERVICE_SELECTION_VALIDATION));
				ApplicationSession.setAttribute(loMissingInfoList, aoRequest, HHSConstants.MISSING_INFO_LIST);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.RENDER_PUB_PROC);

			}
			else
			{
				Boolean loProcurementStatusFlag = (Boolean) loChannel.getData(HHSConstants.PROCUREMENT_STATUS_FLAG);
				Boolean loUpdateSuccessful = (Boolean) loChannel.getData(HHSConstants.LB_UPDATE_SUCC);
				aoHmReqExceProp.put(HHSConstants.PROCUREMENT_STATUS_FLAG, loProcurementStatusFlag);
				aoHmReqExceProp.put(HHSConstants.LB_UPDATE_SUCC, loUpdateSuccessful);
				// Checking of Procurement Status Flag or Update flag is
				// true
				if (loProcurementStatusFlag || loUpdateSuccessful)
				{
					aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.PROC_PUB_SUCC));
					aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
							ApplicationConstants.MESSAGE_PASS_TYPE);
					aoResponse.setRenderParameter(HHSConstants.RESET_SESSION_PROC, HHSConstants.TRUE);
				}
				else
				{
					aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.RENDER_PUB_PROC);
				}
			}
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error Occured while Submitting Procurement", aoExp);
			throw aoExp;
		}
		catch (Exception aoExp)
		{
			ApplicationException loAppEx = new ApplicationException("Error Occured while Submitting Procurement", aoExp);
			LOG_OBJECT.Error("Error Occured while Submitting Procurement", loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * This method will handle action of Save button.
	 * <ul>
	 * <li>1. Fetch procurement id from request parameter.</li>
	 * <li>2. Fetch list of selected services on basis of element id on page
	 * load if any in the database.</li>
	 * <li>3.if no services is selected in Service Selection textarea then on
	 * click of Save button; display error message on Service Selection page
	 * <li>4. on click of Save button; check evidence flag for all selected
	 * services in Service Selection textarea. If evidence flag is not checked
	 * for any of the selected services,display following top level error
	 * message (RED): "<Service> can no longer be used for Procurements due to
	 * taxonomy changes. Please remove this service." If evidence flag is
	 * available for all the service, save selected services in
	 * procurement_services table and navigate on the same page.</li>
	 * <li>5. if evidence flag is available for all the selected services; call
	 * up transaction "insertUpdateServiceAccelerator"
	 * <li>6. If procurement status is Draft;save all the selected services in
	 * table PROCUREMENT_SERVICES and if procurement status is Draft;save all
	 * the selected services in table REPUBLISH_PROCUREMENT_SERVICES
	 * </ul>
	 * @param aoProcurementParam - an object of type Procurement Bean
	 * @param aoResult - a BindingResult object
	 * @param aoRequest - a ActionRequest object
	 * @param aoResponse - a ActionResponse object
	 */

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	@ActionMapping(params = "submit_action=processServiceAction")
	protected void processServiceAction(@ModelAttribute("Procurement") Procurement aoProcurementParam,
			BindingResult aoResult, ActionRequest aoRequest, ActionResponse aoResponse)
	{
		HashMap loHmReqExceProp = new HashMap();
		LOG_OBJECT.Info("Entered into loading services details::" + loHmReqExceProp.toString());
		try
		{
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			setNavigationParamsInRender(aoRequest, aoResponse);
			String lsProcurementId = PortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
			final String lsSelectedService = aoRequest.getParameter(HHSConstants.SEL_SERVICES);
			if (lsSelectedService == null || lsSelectedService.isEmpty())
			{
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.SERVICE_SELECTION_UPPERCASE));
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.SERVICE_SEL_RENDER);
			}
			else
			{
				Channel loChannel = new Channel();
				loChannel.setData(HHSConstants.PROCUREMENT_ID, lsProcurementId);
				loChannel.setData(HHSConstants.USER_ID, lsUserId);
				HashMap<Object, Object> loLastModifiedHashMap = new HashMap<Object, Object>();
				loLastModifiedHashMap.put(HHSConstants.MOD_BY_USER_ID, lsUserId);
				loLastModifiedHashMap.put(HHSConstants.PROCUREMENT_ID, lsProcurementId);
				loChannel.setData(HHSConstants.LO_LAST_MOD_HASHMAP, loLastModifiedHashMap);
				List<SelectedServicesBean> loInsertSelectedServiceList = null;
				loInsertSelectedServiceList = saveTaxonomyService(aoRequest, lsProcurementId, lsSelectedService);
				Boolean loIsSave = (Boolean) ApplicationSession.getAttribute(aoRequest, HHSConstants.SAVE_SERVICES);
				if (loIsSave)
				{
					loChannel.setData(HHSConstants.LO_INSERT_SEL_SERVICES_LIST, loInsertSelectedServiceList);
					HHSTransactionManager.executeTransaction(loChannel, HHSConstants.INS_UPD_SER_ACC);
				}
				loHmReqExceProp.put(HHSConstants.LO_INSERT_SEL_SERVICES_LIST, loInsertSelectedServiceList);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.SERVICE_SEL_RENDER);
				ApplicationSession.setAttribute(loIsSave.toString(), aoRequest, HHSConstants.IS_SAVE);
				aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			}
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */

		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while process selected services:", aoEx);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loHmReqExceProp, aoEx);
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while process selected services:", aoEx);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
	}

	/**
	 * This method is called in turn in method processServiceAction to assign
	 * result of this method in list loInsertSelectedServiceList
	 * <ul>
	 * <li>1.Split all selected services in textbox and assign in the list</li>
	 * <li>2. check evidence flag for all the selected services</li>
	 * <li>3. If evidence flag is missing for any of the service then set
	 * Boolean flag loSave as false other set it as true
	 * <li>4. add all selected services in selectedServicesList</li>
	 * <li>5. return list of all selected services</li>
	 * <ul>
	 * @return List<SelectedServicesBean>
	 * @throws ApplicationException exception when exception occurs
	 * @param asSelectedService SelectedService
	 * @param aoRequest ActionRequest
	 * @param asProcurementId ProcurementId
	 */

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private List<SelectedServicesBean> saveTaxonomyService(ActionRequest aoRequest, String asProcurementId,
			final String asSelectedService) throws ApplicationException
	{
		List<SelectedServicesBean> loInsertList = new ArrayList<SelectedServicesBean>();
		HashMap loHmReqExceProp = new HashMap();
		LOG_OBJECT.Info("Entered into loading services details::" + loHmReqExceProp.toString());
		try
		{
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID,

					PortletSession.APPLICATION_SCOPE);
			if (asSelectedService != null && !asSelectedService.equals(HHSConstants.EMPTY_STRING))
			{
				ArrayList<String> lsSelectedServiceList = new ArrayList<String>(Arrays.asList(asSelectedService
						.split(HHSConstants.COMMA)));
				List<EvidenceBean> loServicesEvidenceFlagBean = HHSUtil.getEvidenceFlag(lsSelectedServiceList);
				List<EvidenceBean> loElementIdList = new ArrayList<EvidenceBean>();
				Map<String, String> loActiveFlagMap = new LinkedHashMap<String, String>();
				Boolean loSave = true;
				for (EvidenceBean loEvidenceBean : loServicesEvidenceFlagBean)
				{
					loActiveFlagMap.put(loEvidenceBean.getElementId(), loEvidenceBean.getActiveFlag());
					if (loEvidenceBean.getEvidenceFlag().equals(HHSConstants.STRING_ZERO))
					{
						loElementIdList.add(loEvidenceBean);
						loSave = false;
					}
				}
				for (String lsElementId : lsSelectedServiceList)
				{
					SelectedServicesBean loSelectedServicesBean = new SelectedServicesBean();
					loSelectedServicesBean.setProcurementId(asProcurementId);
					loSelectedServicesBean.setElementId(lsElementId);
					loSelectedServicesBean.setActiveFlag(loActiveFlagMap.get(lsElementId));
					loSelectedServicesBean.setCreatedByUserId(lsUserId);
					loSelectedServicesBean.setModifiedByUserId(lsUserId);
					loInsertList.add(loSelectedServicesBean);
				}
				ApplicationSession.setAttribute(loSave, aoRequest, HHSConstants.SAVE_SERVICES);
				ApplicationSession.setAttribute(loElementIdList, aoRequest, HHSConstants.ACTIVE_FLAG_LIST);
				ApplicationSession.setAttribute(loInsertList, aoRequest, HHSConstants.LO_INSERT_LIST);
				loHmReqExceProp.put(HHSConstants.SAVE_SERVICES, loSave);
				loHmReqExceProp.put(HHSConstants.ACTIVE_FLAG_LIST, loElementIdList);
				loHmReqExceProp.put(HHSConstants.LO_INSERT_LIST, loInsertList);
			}
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */

		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Exception while adding all selected services in selectedServicesList", aoEx);
			aoEx.setContextData(loHmReqExceProp);
			throw aoEx;
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception while adding all selected services in selectedServicesList", aoEx);
			throw new ApplicationException("Exception while adding all selected services in selectedServicesList", aoEx);
		}
		return loInsertList;
	}

	/**
	 * The method handles the validating process of Publish Procurement
	 * 
	 * <ul>
	 * <li>1.Get the required info i.e
	 * ProcurementId,USerName,Password,UserId,Procurement Bean and pass the
	 * value to channel</li>
	 * <li>1.Get the required info i.e ProcurementId,USerName,Password and pass
	 * the value to channel</li>
	 * <li>2.Call the publishProcurement Transaction</li>
	 * <li>3.Get the required info i.e Service List,Authentication
	 * Status,Procurement Status Flag</li>
	 * <li>4.Call Procurement Status updated successfully then update the log in
	 * Audit table</li>
	 * <ul>
	 * <ul>
	 * <li>Method updated in R4</li>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest
	 * @param aoAuthenticateParam AuthenticationBean Bean
	 * @param aoAuthStatusFlag AuthStatusFlag
	 * @return Channel
	 * @throws ApplicationException
	 */

	private Channel publishProcurement(ActionRequest aoRequest, AuthenticationBean aoAuthenticateParam,
			Boolean aoAuthStatusFlag) throws ApplicationException
	{
		Channel loChannel = new Channel();
		Map<String, String> loProcurementInputMap = new HashMap<String, String>();
		HashMap<String, Object> loHmReqExceProp = new HashMap<String, Object>();
		loHmReqExceProp.put(HHSConstants.AO_AUTH_PARAM, aoAuthenticateParam);
		loHmReqExceProp.put(HHSConstants.AO_AUTH_STATUS_FLAG, aoAuthStatusFlag);
		try
		{
			P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			String lsOrgId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);
			loChannel.setData(HHSConstants.AUTH_BEAN_LOWERCASE, aoAuthenticateParam);
			loChannel.setData(HHSConstants.PROCUREMENT_ID, aoAuthenticateParam.getProcurementId());
			loChannel.setData(HHSConstants.USER_ID, lsUserId);
			loChannel.setData(HHSConstants.VAL_STATUS_FLAG, true);
			loProcurementInputMap.put(HHSConstants.PROCUREMENT_ID, aoAuthenticateParam.getProcurementId());
			loProcurementInputMap.put(HHSConstants.USER_ID, lsUserId);
			loProcurementInputMap.put(HHSConstants.ORGANIZATION_ID, lsOrgId);
			loProcurementInputMap.put(HHSConstants.IS_PUB_PROC, HHSConstants.TRUE);
			HhsAuditBean loAuditBean = new HhsAuditBean();
			loAuditBean.setAuditTableIdentifier(HHSConstants.ACCELERATOR_AUDIT);
			loAuditBean.setEntityType(HHSConstants.PROCUREMENT);
			loAuditBean.setData(HHSConstants.PROC_STATUS_CHANGED_TO_PUB);
			loAuditBean.setEntityId(aoAuthenticateParam.getProcurementId());
			loAuditBean.setEventName(HHSConstants.PUBLISH);
			loAuditBean.setUserId(lsUserId);
			loAuditBean.setEventType(HHSConstants.PUBLISH_PROCUREMENT);
			HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
			StringBuffer lsBfApplicationUrl = new StringBuffer(256);
			lsBfApplicationUrl.append(aoRequest.getScheme()).append(HHSConstants.NOTIFICATION_HREF_1)
					.append(aoRequest.getServerName()).append(HHSConstants.COLON).append(aoRequest.getServerPort())
					.append(aoRequest.getContextPath()).append(HHSConstants.PROCUREMENT_SUMMARY_AGENCY_URL)
					.append(aoAuthenticateParam.getProcurementId());
			HashMap<String, String> loLinkMap = new HashMap<String, String>();
			List<String> loNotificationAlertList = new ArrayList<String>();
			loNotificationAlertList.add(HHSConstants.AL216);
			loNotificationMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
			NotificationDataBean loNotificationAL216 = new NotificationDataBean();
			HashMap<String, String> loRequestMap = new HashMap<String, String>();
			loLinkMap.put(HHSConstants.LINK, lsBfApplicationUrl.toString());
			loNotificationAL216.setLinkMap(loLinkMap);
			loNotificationAL216.setAgencyLinkMap(loLinkMap);
			loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
			loNotificationMap.put(ApplicationConstants.ENTITY_ID, aoAuthenticateParam.getProcurementId());
			loNotificationMap.put(ApplicationConstants.ENTITY_TYPE, HHSConstants.PROCUREMENT);
			loNotificationMap.put(HHSConstants.CREATED_BY_USER_ID, lsUserId);
			loNotificationMap.put(HHSConstants.MODIFIED_BY, lsUserId);
			loNotificationMap.put(HHSConstants.AL216, loNotificationAL216);
			loChannel.setData(HHSConstants.LO_HM_NOTIFY_PARAM, loNotificationMap);
			loChannel.setData(HHSConstants.PRO_INPUT_MAP, loProcurementInputMap);
			loChannel.setData(HHSConstants.AUDIT_BEAN, loAuditBean);
			loChannel.setData(HHSConstants.AUTH_STATUS_FLAG, aoAuthStatusFlag);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.PUB_PROC);
		}// Handling Application Exception for Transaction PublishProcurement
		catch (ApplicationException loEx)
		{
			LOG_OBJECT.Error("Exception while publish Procurement", loEx);
			loEx.setContextData(loHmReqExceProp);
			throw loEx;
		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception while publish Procurement", loExp);
			throw new ApplicationException("Exception while publish Procurement", loExp);
		}
		return loChannel;
	}

	/**
	 * This method open the page having approved services and providers.
	 * 
	 * <ul>
	 * <li>1.Get the procurement id from request and set the values in the
	 * response.</li>
	 * <li>2. Pass the control to the render method with params
	 * render_action=renderServicesAndProviderInfo</li>
	 * <ul>
	 * 
	 * @param aoRequest - ActionRequest
	 * @param aoResponse - ActionResponse
	 */
	@ActionMapping(params = "submit_action=openServiceSelection")
	protected void openApprovedServiceProviders(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID,
				PortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID));
		aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.RENDER_SERVICES_AND_PROV_INFO);
		aoResponse.setRenderParameter(HHSConstants.TOP_LEVEL_FROM_REQ,
				aoRequest.getParameter(HHSConstants.TOP_LEVEL_FROM_REQ));
	}

	/**
	 * This method opens the procurement summary page for the provider.
	 * <ul>
	 * <li>1. Get the procurement id and selectedChildTab from request and set
	 * it into channel object.</li>
	 * <li>2. Retrieve Page Header details via getPageHeader method</li>
	 * <li>3. If the retrieved selectedChildTab equals
	 * "ProcurementSummaryHeader" then set procurement Id in the Channel object
	 * and execute transaction <b>fetchProcurementSummary</b> corresponding to
	 * the procurement Id</li>
	 * <li>4. Retrieve the output and set them in the request object</li>
	 * <li>5. Return the name of the jsp to be rendered.</li>
	 * <ul>
	 * <ul>
	 * <li>Method updated in R4</li>
	 * </ul>
	 * 
	 * @param aoRequest - RenderRequest object
	 * @param aoResponse - RenderResponse object
	 * @return String - string representation of screen name
	 */
	@RenderMapping(params = "render_action=procurementDetails")
	protected String getProcurementSummaryForProvider(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		Channel loChannel = new Channel();
		try
		{
			String lsProcurementId = PortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
			getPageHeader(aoRequest, lsProcurementId);
			String lsSelectChildScreen = (String) aoRequest.getAttribute(HHSConstants.SELECTED_CHILD_TAB);
			// Fetching user id from session
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			if (lsSelectChildScreen != null && lsSelectChildScreen.equalsIgnoreCase(HHSConstants.PROCUREMENT_SUMMARY))
			{
				loChannel.setData(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
				loChannel.setData(HHSConstants.PROVIDER_ID_KEY, lsUserId);
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.TRAN_FETCH_PROC_SUMMARY);
				Procurement loProcurementBean = (Procurement) loChannel.getData(HHSConstants.PROC_SUMMARY);
				String lsProviderStatus = HHSUtil.getStatusName(HHSConstants.PROVIDER,
						Integer.parseInt((String) loChannel.getData(HHSConstants.PROVIDER_STATUS_KEY)));
				HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb
						.getInstance().getCacheObject(ApplicationConstants.APPLICATION_SETTING);
				String loReleaseTime = (String) loApplicationSettingMap.get(HHSConstants.PROPOSAL_RELEASE_TIME_KEY);
				aoRequest.setAttribute(HHSConstants.RELEASE_TIME, loReleaseTime);
				aoRequest.setAttribute(HHSConstants.PROCUREMENT_BEAN, loProcurementBean);
				aoRequest.setAttribute(HHSConstants.PROVIDER_STATUS_KEY, lsProviderStatus);
			}
		}
		// handling exception Occured while rendering procurement details.
		catch (ApplicationException loExp)
		{
			setGenericErrorMessage(aoRequest);
		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error(HHSConstants.EXCEPTION_OCCURED_RENDERING_PROC_DETAILS_COLON, loExp);
			setGenericErrorMessage(aoRequest);
		}
		return HHSConstants.PROC_SUM_PROVIDER;
	}

	/**
	 * This method is used to render the approved providers screen.
	 * <ul>
	 * <li>1. Fetch procurement id and procurement status</li>
	 * <li>2. Call fetchSelectedServices() method and retrieve selected services
	 * list, approved providers based on lbFetchDefault flag and change control
	 * widget list.</li>
	 * <li>3. Iterate over the widget list</li>
	 * <li>4. If the last modified and last published date are not null then
	 * convert them as per the DateFormat</li>
	 * <li>5. Else set last modified and last published date as N/A
	 * <li>6. Fetch providers list corresponding to the service list and the
	 * drop down value if the Generate List button has been clicked.</li>
	 * <li>7. Redirect all the retrieved data to the approved providers screen</li>
	 * </ul>
	 * 
	 * @param aoRequest - RenderResponse
	 * @param aoResponse - RenderResponse
	 * @return loModelAndView - a ModelAndView object
	 * @param aoProcurementParam ProcurementParam
	 */
	@RenderMapping(params = "render_action=approvedproviders")
	protected ModelAndView renderApprovedProviders(RenderRequest aoRequest, RenderResponse aoResponse,
			Procurement aoProcurementParam)
	{
		String lsProcurementId = PortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
		PortletSession loSession = aoRequest.getPortletSession();
		try
		{
			getPageHeader(aoRequest, lsProcurementId);
			String lsSelectChildScreen = (String) aoRequest.getAttribute(HHSConstants.SELECTED_CHILD_TAB);
			String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			if (lsSelectChildScreen != null && lsSelectChildScreen.equalsIgnoreCase(HHSConstants.APPROVED_PROVIDERS))
			{
				aoRequest.setAttribute(HHSConstants.DROPDOWN_STATUS, HHSConstants.FALSE);
				aoRequest.setAttribute(HHSConstants.GEN_BUTTON_STATUS, HHSConstants.FALSE);
				Integer loProcStatusId = ((ProcurementInfo) aoRequest.getAttribute(HHSConstants.PROCUREMENT_BEAN))
						.getStatus();
				Integer loProcStatusPrevId = ((ProcurementInfo) aoRequest.getAttribute(HHSConstants.PROCUREMENT_BEAN))
						.getPreviousStatus();
				boolean lbFetchDefault = false;
				if (loProcStatusId == Integer.parseInt(PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_EVALUATIONS_COMPLETE))
						|| loProcStatusId == Integer.parseInt(PropertyLoader.getProperty(
								HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_PROCUREMENT_PROPOSALS_RECEIVED))
						|| loProcStatusId == Integer.parseInt(PropertyLoader.getProperty(
								HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_PROCUREMENT_SELECTIONS_MADE))
						|| loProcStatusId == Integer.parseInt(PropertyLoader.getProperty(
								HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_CLOSED)))
				{
					lbFetchDefault = true;
				}
				else if (loProcStatusId == Integer.parseInt(PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_CANCELLED))
						&& (loProcStatusPrevId == Integer.parseInt(PropertyLoader.getProperty(
								HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_PROCUREMENT_EVALUATIONS_COMPLETE))
								|| loProcStatusPrevId == Integer.parseInt(PropertyLoader.getProperty(
										HHSConstants.PROPERTIES_STATUS_CONSTANT,
										HHSConstants.STATUS_PROCUREMENT_PROPOSALS_RECEIVED))
								|| loProcStatusPrevId == Integer.parseInt(PropertyLoader.getProperty(
										HHSConstants.PROPERTIES_STATUS_CONSTANT,
										HHSConstants.STATUS_PROCUREMENT_SELECTIONS_MADE)) || loProcStatusPrevId == Integer
								.parseInt(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
										HHSConstants.STATUS_PROCUREMENT_CLOSED))))
				{
					lbFetchDefault = true;
				}
				else
				{
					lbFetchDefault = false;
				}
				aoRequest.setAttribute(HHSConstants.ERROR_FLAG, aoRequest.getParameter(HHSConstants.ERROR_FLAG));
				List<SelectedServicesBean> loSelectedServiceList = fetchSelectedServices(aoRequest, lsProcurementId,
						lbFetchDefault);
				aoRequest.setAttribute(HHSConstants.SELECTED_SERVICES_LIST, loSelectedServiceList);
				loSession.setAttribute(HHSConstants.SELECTED_SERVICES_LIST, loSelectedServiceList);
				List<String> loFlagUncheckedServiceList = (List<String>) ApplicationSession.getAttribute(aoRequest,
						HHSConstants.FLAG_UNCHECKED_STATUS);
				aoRequest.setAttribute(HHSConstants.EVIDENCE_SERVICE_LIST, loFlagUncheckedServiceList);
				// set status in channel for rule tld on jsp
				Channel loChannel = new Channel();
				loChannel.setData(HHSConstants.STAT, loProcStatusId);
				loChannel.setData(HHSConstants.PREV_STATUS, loProcStatusPrevId);
				loChannel.setData(HHSConstants.ORG_TYPE, lsUserOrgType);
				aoRequest.setAttribute(HHSConstants.STATUS_CHANNEL, loChannel);
				aoRequest.setAttribute(HHSConstants.CHANNEL_ACCESS, loChannel);
			}
		}
		catch (ApplicationException loExp)
		{
			setGenericErrorMessage(aoRequest);
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error occured while processing approved providers", loExp);
			setGenericErrorMessage(aoRequest);
		}
		return new ModelAndView(HHSConstants.APPROVED_PROVIDERS_LOWERCASE);
	}

	/**
	 * This method fetches the services corresponding to the element id
	 * <ul>
	 * <li>1. Fetch all the element id corresponding to procurement_id from the
	 * procurement_service table</li>
	 * <li>2. Fetch taxonomy element from cache.</li>
	 * <li>3. Create a dom object from this fetched taxonmoy element</li>
	 * <li>4. Iterate over the fetched element Id list and retrieve element Id.</li>
	 * <li>5. Fetch element from the xml util corresponding to element_id</li>
	 * <li>6. Fetch element_name from cache corresponding to the element_ids.</li>
	 * </ul>
	 * 
	 * @param aoRequest - a RenderResponse object
	 * @param asProcurementId - Procurement Id
	 * @param abFetchDefault - Flag whether to fetch default list of approved
	 *            providers
	 * @return loServiceList a list of selected services
	 * @throws ApplicationException when exception occurs
	 */
	@SuppressWarnings("unchecked")
	private List<SelectedServicesBean> fetchSelectedServices(RenderRequest aoRequest, String asProcurementId,
			boolean abFetchDefault) throws ApplicationException
	{
		List<SelectedServicesBean> loElementIdList = null;
		try
		{
			Channel loChannelObj = new Channel();
			loChannelObj.setData(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
			loChannelObj.setData(HHSConstants.AB_FETCH_DEF, abFetchDefault);
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.FETCH_PAGE_LOAD_DETAILS);
			Integer loProcurementAddendumDataCount = (Integer) loChannelObj
					.getData(HHSConstants.PROC_ADDENDUM_DATA_COUNT);
			if (loProcurementAddendumDataCount != null && loProcurementAddendumDataCount > 0)
			{
				aoRequest.setAttribute(HHSConstants.UNPUBLISHED_DATA_MSG_KEY, HHSConstants.UNPUBLISHED_DATA_MSG);
			}
			loElementIdList = (List<SelectedServicesBean>) loChannelObj.getData(HHSConstants.LO_SELECTED_SER_LIST);
			String lsSelectedType = (String) loChannelObj.getData(HHSConstants.LS_SELECTED_TYPE);
			List<ApprovedProvidersBean> loApprovedProviders = (List<ApprovedProvidersBean>) loChannelObj
					.getData(HHSConstants.LO_APPROVED_PROVIDERS_LIST);
			aoRequest.setAttribute(HHSConstants.SELECTED_TYPE, lsSelectedType);
			aoRequest.setAttribute(HHSConstants.APP_PRO_LIST, loApprovedProviders);
			if (abFetchDefault && (loApprovedProviders == null || loApprovedProviders.isEmpty()))
			{
				aoRequest.setAttribute(HHSConstants.NO_APP_PROVS, true);
			}
			aoRequest.getPortletSession().setAttribute(HHSConstants.SORT_TYPE, HHSConstants.ASCENDING,
					PortletSession.APPLICATION_SCOPE);
			aoRequest.getPortletSession().setAttribute(HHSConstants.SORT_BY, HHSConstants.ORG_LEGAL_NAME,
					PortletSession.APPLICATION_SCOPE);
		}
		catch (ApplicationException loEx)
		{
			LOG_OBJECT.Error("Error occurred while fetching selected services", loEx);
			throw loEx;
		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error occurred while fetching selected services", loExp);
			throw new ApplicationException("Exception Occured while fetching Selected Services List from database",
					loExp);
		}
		return loElementIdList;
	}

	/**
	 * This method will fetch the list of approved providers on click of
	 * Generate List button.
	 * <ul>
	 * <li>1. Fetch the value of the drop down option against which list has to
	 * be generated</li>
	 * <li>2. Fetch the list of all approved providers corresponding to the drop
	 * down value on click of Generate List button.</li>
	 * </ul>
	 * @param aoRequest - an ActionRequest object
	 * @param aoResponse - an ActionResponse object
	 * @return String
	 */
	@ResourceMapping("fetchAppProviders")
	protected String actionfetchRequiredProviders(ResourceRequest aoRequest, ResourceResponse aoResponse)
	{
		PortletSession loSession = aoRequest.getPortletSession();
		try
		{
			String lsChangeDropDownValueProvider = HHSPortalUtil.parseQueryString(aoRequest,
					HHSConstants.CHANGE_DROPDOWN_VALUES);
			String lsUserType = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
					PortletSession.APPLICATION_SCOPE);
			String lsSelectedProvOption = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.SEL_BOX_DROPDOWN);
			String lsEltId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.ELT_ID);
			String lsProcurementId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
			String lsSortGridName = HHSConstants.APP_PROVIDERS;
			String lsColumnName = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.COLUMN_NAME);
			String lsSortType = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.SORT_TYPE);
			String lsFromProvider = null;

			if (lsUserType != null && lsUserType.equalsIgnoreCase(ApplicationConstants.PROVIDER_ORG))
			{
				lsFromProvider = HHSConstants.TRUE;
			}
			if (null == lsColumnName)
			{
				lsColumnName = HHSConstants.ORG_LEGAL_NAME;
				lsSortType = HHSConstants.ASCENDING;
			}
			BaseFilter loBaseFilter = new BaseFilter();
			List<String> loSelectedServicesList = new ArrayList<String>();
			getSortDetailsFromXML(lsColumnName, lsUserType, lsSortGridName, loBaseFilter, lsSortType);
			if (lsChangeDropDownValueProvider != null
					&& lsChangeDropDownValueProvider.equalsIgnoreCase(HHSConstants.CHANGE_DROPDOWN_VAL_PROVIDER))
			{
				loSelectedServicesList.add(lsEltId);
			}
			else
			{
				String lsElementList = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.SER_ELEMENT_ID_LIST);
				if (lsElementList != null)
				{
					loSelectedServicesList = Arrays.asList(lsElementList.split(HHSConstants.COMMA));
				}
			}

			Channel loChannelObj = new Channel();
			loChannelObj.setData(HHSConstants.SEL_SER_LIST, loSelectedServicesList);
			loChannelObj.setData(HHSConstants.SEL_PROV_DROPDOWN_VAL, lsSelectedProvOption);
			loChannelObj.setData(HHSConstants.ELEMENT_ID, lsEltId);
			loChannelObj.setData(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			loChannelObj.setData(HHSConstants.BASE_FILTER, loBaseFilter);
			loChannelObj.setData(HHSConstants.FROM_PROVIDER, lsFromProvider);
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.FETCH_APP_PROVIDERS_LIST);
			List<ApprovedProvidersBean> loApprovedProvidersList = (List<ApprovedProvidersBean>) loChannelObj
					.getData(HHSConstants.LO_APPROVED_PROVIDERS_LIST);
			aoRequest.setAttribute(HHSConstants.APP_PRO_LIST, loApprovedProvidersList);
			aoRequest.setAttribute(HHSConstants.SELECTED_CHILD_TAB, HHSConstants.APPROVED_PROVIDERS);
			aoRequest.setAttribute(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			aoRequest.getPortletSession().setAttribute(HHSConstants.SORT_TYPE, loBaseFilter.getFirstSortType(),
					PortletSession.APPLICATION_SCOPE);
			aoRequest.getPortletSession().setAttribute(HHSConstants.SORT_BY, loBaseFilter.getSortColumnName(),
					PortletSession.APPLICATION_SCOPE);
			aoRequest.setAttribute(HHSConstants.IS_AJAX, HHSConstants.TRUE);
		}
		catch (ApplicationException loEx)
		{
			String lsErrorMsg = HHSConstants.ERROR_OCCURED_FETCHING_APPROVED_PROVIDERS;
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
			LOG_OBJECT.Error(HHSConstants.ERROR_OCCURED_FETCHING_APPROVED_PROVIDERS, loEx);
		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			String lsErrorMsg = HHSConstants.ERROR_OCCURED_FETCHING_APPROVED_PROVIDERS;
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
			LOG_OBJECT.Error(HHSConstants.ERROR_OCCURED_FETCHING_APPROVED_PROVIDERS, loExp);
		}
		return HHSConstants.APPROVED_PROVIDERS_LOWERCASE;
	}

	/**
	 * This method will handle action on click of Save and Save & Next button.
	 * 
	 * <ul>
	 * <li>1. Fetch next_action value from action request.</li>
	 * <li>2. If next_action value is not null then retrieve the list of
	 * SelectedServicesBean</li>
	 * <li>3. Validate the services for which the evidence flag has been
	 * unchecked via calling fetchEvidenceFlag</li>
	 * <li>4. If retrieved list does not contain any service name then call
	 * transaction <b>saveAppProvDetails</b></li>
	 * <li>4. If the retrieved flag value is true then check the value of
	 * next_action</li>
	 * <li>5. if the next_action value is saveButtonAction then redirect the
	 * user to approved providers screen</li>
	 * <li>6. Else if the next_action value is saveNextButtonAction then
	 * redirect the user to publish procurement screen else display error
	 * message.</li>
	 * </ul>
	 * 
	 * @param aoSelectedServicesBean - an object of type SelectedServicesBean
	 * @param aoRequest - a ActionRequest object
	 * @param aoResponse - a ActionResponse object
	 * @param aoProcurementParam - a Procurement object
	 */
	@SuppressWarnings("unchecked")
	@ActionMapping(params = "submit_action=saveApprovedProviders")
	protected void saveApprovedProviders(
			@ModelAttribute("SelectedServicesBean") SelectedServicesBean aoSelectedServicesBean,
			ActionRequest aoRequest, ActionResponse aoResponse, Procurement aoProcurementParam)

	{
		try
		{

			PortletSession loSession = aoRequest.getPortletSession();
			String lsProcurementId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
			String lsDropDownValue = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.SEL_BOX_DROPDOWN);
			List<SelectedServicesBean> loSelectedServiceList = (List<SelectedServicesBean>) loSession
					.getAttribute(HHSConstants.SELECTED_SERVICES_LIST);
			List<String> loFlagUncheckedServiceList = fetchEvidenceFlag(loSelectedServiceList);
			String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);
			if (loFlagUncheckedServiceList.isEmpty())
			{
				aoSelectedServicesBean.setProcurementId(lsProcurementId);
				aoSelectedServicesBean.setApprovedForDropDown(lsDropDownValue);
				aoSelectedServicesBean.setUserId(lsUserId);
				Channel loChannelObj = new Channel();
				loChannelObj.setData(HHSConstants.SEL_SERVE_BEAN, aoSelectedServicesBean);
				HashMap<Object, Object> loLastModifiedHashMap = new HashMap<Object, Object>();
				loLastModifiedHashMap.put(HHSConstants.MOD_BY_USER_ID, lsUserId);
				loLastModifiedHashMap.put(HHSConstants.PROCUREMENT_ID, lsProcurementId);
				loChannelObj.setData(HHSConstants.LO_LAST_MOD_HASHMAP, loLastModifiedHashMap);
				loChannelObj.setData(HHSConstants.PROCUREMENT_STATUS_FLAG, true);
				HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.SAVE_APP_PROV_DETAILS);
				String lsTransOutput = (String) loChannelObj.getData(HHSConstants.LS_OUTPUT);
				if (lsTransOutput != null && lsTransOutput.equalsIgnoreCase(HHSConstants.PASS_FLAG))
				{
					aoResponse
							.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.APPROVED_PROVIDERS_LOWERCASE);
					aoResponse.setRenderParameter(HHSConstants.MID_LEVEL_FROM_REQ, HHSConstants.APPROVED_PROVIDERS);
				}
				else if (lsTransOutput != null && lsTransOutput.equalsIgnoreCase(HHSConstants.ERROR_FLAG))
				{
					aoResponse
							.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.APPROVED_PROVIDERS_LOWERCASE);
					aoResponse.setRenderParameter(HHSConstants.MID_LEVEL_FROM_REQ, HHSConstants.APPROVED_PROVIDERS);
					aoResponse.setRenderParameter(HHSConstants.ERROR_FLAG, HHSConstants.TRUE);
				}
			}
			else
			{
				loSession.setAttribute(HHSConstants.FLAG_UNCHECKED_STATUS, loFlagUncheckedServiceList);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.APPROVED_PROVIDERS_LOWERCASE);
				aoResponse.setRenderParameter(HHSConstants.MID_LEVEL_FROM_REQ, HHSConstants.APPROVED_PROVIDERS);
			}
			aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			setNavigationParamsInRender(aoRequest, aoResponse);
		}
		catch (ApplicationException loExp)
		{
			Map<String, SelectedServicesBean> loParamMap = new HashMap<String, SelectedServicesBean>();
			loParamMap.put(HHSConstants.SELECTED_SERVICES_BEAN, aoSelectedServicesBean);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap, loExp);
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error is occurred while saving the provider", loExp);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
	}

	/**
	 * This method fetched the list of services for which evidence flag has been
	 * unchecked
	 * 
	 * <ul>
	 * <li>1. Retrieve taxonomy object from cache</li>
	 * <li>2. Iterate over list of SelectedServicesBean</li>
	 * <li>3. Retrieve the Element object and fetch evidence flag from it
	 * <li>
	 * <li>4. If retrieved evidence flag value has been "0" then fetch the name
	 * of the service</li>
	 * <li>5. Add the service name to the created list and return the same</li>
	 * </ul>
	 * 
	 * @param loSelectedServiceList - list of SelectedServicesBean
	 * @return loFlagUncheckedServiceList - list containing services name
	 * @throws ApplicationException when exception occurs
	 */
	private List<String> fetchEvidenceFlag(List<SelectedServicesBean> loSelectedServiceList)
			throws ApplicationException
	{
		List<String> loFlagUncheckedServiceList = new ArrayList<String>();
		try
		{
			Document loDoc = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(HHSConstants.TAXONOMY_ELEMENT);
			String lsElementId = null;
			Element loCorrRuleElt = null;
			String lsServiceEvidenceFlag = null;
			if (loSelectedServiceList != null)
			{
				for (SelectedServicesBean lsProcServBean : loSelectedServiceList)
				{
					lsElementId = lsProcServBean.getElementId();
					loCorrRuleElt = XMLUtil.getElement("//element[@id=\"" + lsElementId + "\"]", loDoc);
					lsServiceEvidenceFlag = loCorrRuleElt.getAttributeValue(HHSConstants.EVE_REQ_FLAG);
					if (lsServiceEvidenceFlag != null
							&& lsServiceEvidenceFlag.equalsIgnoreCase(HHSConstants.STRING_ZERO))
					{
						loFlagUncheckedServiceList.add(loCorrRuleElt.getAttributeValue(HHSConstants.NAME));
					}
				}
			}
		}
		catch (ApplicationException loEx)
		{
			LOG_OBJECT.Error("Error occured while checking evidence flags for approved providers save", loEx);
			throw loEx;
		}
		// handling exception other than Application Exception.
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Error occured while checking evidence flags for approved providers save", loEx);
			throw new ApplicationException("Error occured while checking evidence flags for approved providers save",
					loEx);
		}
		return loFlagUncheckedServiceList;
	}

	/**
	 * The method will open the overlay content for cancel procurement
	 * 
	 * <ul>
	 * <li>A ModelAndView object loModelAndViewAjax which renders the content of
	 * cancelProcurement jsp.</li>
	 * </ul>
	 * @param aoResourceRequest ResourceRequest
	 * @return model and view for cancelProcurement jsp
	 * @throws ApplicationException when exception occurs
	 */
	@ResourceMapping("selectOverlayContent")
	protected ModelAndView getSelectOverlayContent(ResourceRequest aoResourceRequest) throws ApplicationException
	{
		ModelAndView loModelAndView = new ModelAndView(HHSConstants.CANCEL_PROCUREMENT);
		aoResourceRequest.setAttribute(HHSConstants.CAN_PROC_ID,
				aoResourceRequest.getParameter(HHSConstants.PROCUREMENT_ID));
		return loModelAndView;
	}

	/**
	 * Changed method - Build 3.1.0, Enhancement id: 6025
	 * 
	 * This method is modified to add channel variables for changing procurement
	 * status in DOCUMENT_DOWNLOAD_REQUEST table in DB when procurement gets
	 * canceled.
	 * 
	 * This method is modified to resolve
	 * "BEA-000000-Could not complete request" error in AdminServer logs
	 * 
	 * <ul>
	 * <li>call to method "renderProcurementSuccessMessage"nothing is commented
	 * as nothing returned in Model and View which was causing BEA-000000 error</li>
	 * </ul>
	 * 
	 * The method will handle canceling of procurement
	 * <ul>
	 * <li>1.Set the required data in Channel</li>
	 * <li>2.Perform Server Side Validations</li>
	 * <li>3.Call transaction cancelProcurement</li>
	 * <li>4.All provider submitted data (that was entered on S235-Proposal
	 * Details) will be deleted. [Method:deleteProvidersData,QueryId
	 * :deleteProvidersData]</li>
	 * <li>5.update procurement status to canceled [Method
	 * :updateProcurementStatus QueryId : updateProcurementStatus ]in
	 * transaction</li>
	 * If any exception is thrown it will be handled here in the catch block
	 * </ul>
	 * @param aoResult BindingResult
	 * @param aoRequest ActionRequest
	 * @param aoResponse ActionResponse
	 * @param aoAuthenticationBean AuthenticationBean
	 */
	@ActionMapping(params = "submit_action=cancelProcurement")
	protected void cancelProcurement(AuthenticationBean aoAuthenticationBean, BindingResult aoResult,
			ActionRequest aoRequest, ActionResponse aoResponse)
	{
		Channel loChannelObj = new Channel();
		HhsAuditBean loAuditBean = null;
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		HashMap<String, Object> loCancelProcurementTaskMap = new HashMap<String, Object>();
		try
		{
			validator.validate(aoAuthenticationBean, aoResult);
			Map loAuthenticateMap = validateUser(aoAuthenticationBean.getUserName(),
					aoAuthenticationBean.getPassword(), aoRequest);
			Boolean loAuthStatus = (Boolean) loAuthenticateMap.get(HHSConstants.IS_VALID_USER);
			if (!loAuthStatus)
			{
				aoResponse
						.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
								HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.MESSAGE_M38));
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.ERROR_PAGE_CLOSE);
			}
			else
			{
				String lsWorkflowName = P8Constants.PE_EVALUATION_UTILITY_WORKFLOW_NAME;
				loCancelProcurementTaskMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID,
						aoAuthenticationBean.getProcurementId());
				loCancelProcurementTaskMap.put(P8Constants.ACTION_KEY_FOR_UTILITY_WORKFLOW,
						P8Constants.CANCEL_PROCUREMENT_ACTION_FOR_UTILITY_WORKFLOW);
				loChannelObj.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
				loChannelObj.setData(HHSConstants.AUTH_BEAN_LOWERCASE, aoAuthenticationBean);
				loChannelObj.setData(HHSConstants.LS_PRO_ID, aoAuthenticationBean.getProcurementId());
				HashMap<String, String> loDeleteProviderDataMap = new HashMap<String, String>();
				HashMap<String, String> loStatusUpdateMap = new HashMap<String, String>();
				HashMap<String, Object> loStatusUpdateFilenet = new HashMap<String, Object>();
				loDeleteProviderDataMap.put(HHSConstants.LS_PRO_ID, aoAuthenticationBean.getProcurementId());
				loDeleteProviderDataMap.put(HHSConstants.DEL_PRO_DATA_DRAFT, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_DRAFT));
				loDeleteProviderDataMap.put(HHSConstants.DEL_PRO_DATA_PLAN, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_PLANNED));
				loDeleteProviderDataMap.put(HHSConstants.DEL_PRO_DATA_REL, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_RELEASED));
				loStatusUpdateMap.put(HHSConstants.PROC_STATUS_CODE, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_CANCELLED));
				loStatusUpdateMap.put(HHSConstants.USER_ID, lsUserId);
				loStatusUpdateMap.put(HHSConstants.PROCUREMENT_ID, aoAuthenticationBean.getProcurementId());
				loStatusUpdateFilenet.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, false);
				loStatusUpdateFilenet.put(HHSConstants.IS_DOCUMENT_RFP_AWARD_TYPE, false);
				loChannelObj.setData(HHSConstants.LO_DEL_PRO_DATA_MAP, loDeleteProviderDataMap);
				loChannelObj.setData(HHSConstants.LO_STATUS_UPDATE_MAP, loStatusUpdateMap);
				loChannelObj.setData(HHSConstants.LO_STATUS_UPDATE_FILENET, loStatusUpdateFilenet);
				loAuditBean = new HhsAuditBean();
				loAuditBean.setAuditTableIdentifier(HHSConstants.ACCELERATOR_AUDIT);
				loAuditBean.setEntityType(HHSConstants.PROCUREMENT);
				loAuditBean.setData(HHSConstants.PROC_STATUS_CHANGED_CANCEL);
				loAuditBean.setEntityId(aoAuthenticationBean.getProcurementId());
				loAuditBean.setEventName(HHSConstants.CANCEL);
				loAuditBean.setUserId(lsUserId);
				loAuditBean.setEventType(HHSConstants.CANCEL_PROC);
				loChannelObj.setData(HHSConstants.AUDIT_BEAN, loAuditBean);
				loChannelObj.setData(HHSConstants.LO_WORKFLOW_NAME, lsWorkflowName);
				loChannelObj.setData(HHSConstants.AO_HMWF_REQUIRED_PROPS, loCancelProcurementTaskMap);

				// channel variables added for enhancement 3.1.0 : 6025
				loChannelObj.setData(HHSConstants.AS_USER_ID, lsUserId);
				loChannelObj.setData(HHSConstants.AS_STATUS_ID, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_CANCELLED));
				loChannelObj.setData(HHSConstants.PROCUREMENT_ID, aoAuthenticationBean.getProcurementId());

				HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.CANCEL_PROCUREMENT);
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.CANCEL_PROCUREMENT));
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_PASS_TYPE);
			}
		}
		catch (ApplicationException loAppex)
		{
			Map<String, HhsAuditBean> loParamMap = new HashMap<String, HhsAuditBean>();
			loParamMap.put(HHSConstants.AO_AUDIT_BEAN, loAuditBean);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap, loAppex);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.ERROR_PAGE_CLOSE);
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error occured while cancelling your procurement ", loExp);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.ERROR_PAGE_CLOSE);
		}
	}

	/**
	 * This method fetches epin list associated with procurements
	 * <ul>
	 * <li>1.Calls method getEpinList() from BaseController</li>
	 * </ul>
	 * @param aoResourceRequest resource request object
	 * @param aoResourceResponse resource response object
	 */

	@ResourceMapping("procurementEpinList")
	public void getProcurementEpinList(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		try
		{
			getEpinList(aoResourceRequest, aoResourceResponse);
		}
		// Catch the exception thrown by transaction and set the error message
		// in request object and pass to jsp
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("error occurred while getting procurement epin list :", loExp);
		}
		// handling exception other than Application Exception.
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("error occurred while getting procurement epin list :", loEx);
		}
	}

	/**
	 * This method renders services and providers screen for Provider users
	 * 
	 * <ul>
	 * <li>1. Invoke method getPageHeader</li>
	 * <li>2. Fetch the value of attribute "selectedChildTab" and if its value
	 * is equals to "ServicesAndProviders" then set procurement Id in the
	 * Channel object</li>
	 * <li>3. Call transaction <b>displayServiceAndProviders</b> and fetch
	 * services list, drop down value and approved providers list</li>
	 * <li>4. Set the fetched attributes in the request object</li>
	 * <li>5. Redirect the user to S232 - Services and Providers
	 * </ul>
	 * 
	 * @param aoRequest - a RenderResponse object
	 * @param aoResponse - a RenderResponse object
	 * @return ModelAndView
	 */

	@SuppressWarnings("unchecked")
	@RenderMapping(params = "render_action=renderServicesAndProviderInfo")
	protected ModelAndView renderProviderViewPage(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		ModelAndView loModelAndView = null;
		try
		{
			getPageHeader(aoRequest, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			String lsSelectChildScreen = (String) aoRequest.getAttribute(HHSConstants.SELECTED_CHILD_TAB);
			// Checking if the value of lsSelectChildScreen is not null and is
			// equal to "ServicesAndProviders"
			if (lsSelectChildScreen != null
					&& lsSelectChildScreen.equalsIgnoreCase(HHSConstants.SERVICES_AND_PPROVIDER))
			{
				Channel loChannel = new Channel();
				loChannel.setData(HHSConstants.PROCUREMENT_ID_KEY, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.DIS_SER_AND_PROV);
				List<SelectedServicesBean> loServiceList = (List<SelectedServicesBean>) loChannel
						.getData(HHSConstants.LO_SELECTED_SER_LIST);
				String lsDropDownValue = (String) loChannel.getData(HHSConstants.DROPDOWN_VAL);
				List<ApprovedProvidersBean> loProvidersList = (List<ApprovedProvidersBean>) loChannel
						.getData(HHSConstants.LO_APP_PROV);
				aoRequest.setAttribute(HHSConstants.SELECTED_SERVICES_LIST, loServiceList);
				aoRequest.setAttribute(HHSConstants.SEL_BOX_DROPDOWN, lsDropDownValue);
				aoRequest.setAttribute(HHSConstants.APP_PRO_LIST, loProvidersList);
			}
		}
		// Handling Exception while rendering procurement summary
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("Exception Occured while rendering Services and providers details : ", loExp);
			setGenericErrorMessage(aoRequest);
		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception Occured while rendering Services and providers details : ", loExp);
			setGenericErrorMessage(aoRequest);
		}
		loModelAndView = new ModelAndView(HHSConstants.APPROVED_PROVIDERS_AND_SERVICES);
		return loModelAndView;
	}

	/**
	 * This method handles the execution on click of "Back" button on S232
	 * <ul>
	 * <li>1. Invoke method setNavigationParamsInRender() to set navigation
	 * parameters</li>
	 * <li>2. Set procurement Id and "topLevelFromRequest" in the render
	 * parameters</li>
	 * <li>3. Redirect the user to S231 - Provider-Procurement Summary
	 * </ul>
	 * 
	 * @param aoRequest - an ActionRequest object
	 * @param aoResponse - an ActionResponse object
	 */
	@ActionMapping(params = "submit_action=servicesAndProvidersBackAction")
	public void backServicesAndProviders(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		setNavigationParamsInRender(aoRequest, aoResponse);
		aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
		aoResponse.setRenderParameter(HHSConstants.TOP_LEVEL_FROM_REQ, HHSConstants.PROCUREMENT_SUMMARY);
		aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.PROCUREMENT_DETAILS);
	}

	/**
	 * This method handles the execution on click of "Next" button on S232
	 * <ul>
	 * <li>1. Invoke method setNavigationParamsInRender() to set navigation
	 * parameters</li>
	 * <li>2. Set procurement Id and "topLevelFromRequest" in the render
	 * parameters</li>
	 * <li>3. Redirect the user to S233 - Provider-RFP Documents
	 * </ul>
	 * 
	 * @param aoRequest - an ActionRequest object
	 * @param aoResponse - an ActionResponse object
	 */
	@ActionMapping(params = "submit_action=servicesAndProvidersNextAction")
	public void nextServicesAndProviders(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		setNavigationParamsInRender(aoRequest, aoResponse);
		aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
		aoResponse.setRenderParameter(HHSConstants.TOP_LEVEL_FROM_REQ, HHSConstants.RFP_DOCUMENTS);
		aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
		aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.DISP_RFF_DOC_LIST);
	}

	/**
	 * This method handles the execution on click of "Back" button on S212
	 * <ul>
	 * <li>1. Invoke method setNavigationParamsInRender() to set navigation
	 * parameters</li>
	 * <li>2. Set procurement Id and "topLevelFromRequest" in the render
	 * parameters</li>
	 * <li>3. Redirect the user to S232 - Provider-Services And Providers
	 * </ul>
	 * 
	 * @param aoRequest - an ActionRequest object
	 * @param aoResponse - an ActionResponse object
	 */
	@ActionMapping(params = "submit_action=rfpDocumentsBackAction")
	public void backRfpDocuments(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		setNavigationParamsInRender(aoRequest, aoResponse);
		aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
		aoResponse.setRenderParameter(HHSConstants.TOP_LEVEL_FROM_REQ, HHSConstants.SERVICES_AND_PPROVIDER);
		aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.RENDER_SERVICES_AND_PROV_INFO);
	}

	/**
	 * The method will open the overlay content for close procurement
	 * <ul>
	 * <li>A ModelAndView object which renders the content of closeProcurement
	 * jsp.</li>
	 * </ul>
	 * 
	 * @param aoResourceRequest ResourceRequest
	 * @return ModelAndView for the closeProcurement jsp.
	 * @throws ApplicationException when exception occurs
	 */
	@ResourceMapping("closeProcurementOverlay")
	protected ModelAndView getCloseProcurementOverlay(ResourceRequest aoResourceRequest) throws ApplicationException
	{
		ModelAndView loModelAndView = new ModelAndView(HHSConstants.CLOSE_PROCUREMENT);
		aoResourceRequest.setAttribute(HHSConstants.CLOSE_PROC_ID,
				aoResourceRequest.getParameter(HHSConstants.PROCUREMENT_ID));
		return loModelAndView;
	}

	/**
	 * Changed method - Build 3.1.0, Enhancement id: 6025 Changes made for
	 * enhancement 6448 for Release 3.8.0 This method is modified to add channel
	 * variables for changing procurement status in DOCUMENT_DOWNLOAD_REQUEST
	 * table in DB when procurement gets closed.
	 * 
	 * This method is modified to resolve
	 * "BEA-000000-Could not complete request" error in AdminServer logs
	 * 
	 * <ul>
	 * <li>call to method "renderProcurementSuccessMessage"nothing is commented
	 * as nothing returned in Model and View which was causing BEA-000000 error</li>
	 * </ul>
	 * 
	 * The method will handle close procurement
	 * <ul>
	 * <li>1.Set the required data in Channel</li>
	 * <li>2.Call transaction closeProcurement</li>
	 * <li>3.Get the Auth_Status flag [Method:AuthenticateLoginUser]
	 * <li>4.if auth_status true , and if all the awards/contracts are either
	 * Closed, Canceled or Registered, update procurement status to close
	 * [Method:closeProcurement QueryId:updateProcurementStatus] else display
	 * validation message on the screen.</li>
	 * If any exception is thrown it will be handled here in the catch block
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * @param aoAuthenticationBean AuthenticationBean
	 * @param aoResult BindingResult
	 * @param aoRequest ActionRequest
	 * @param aoResponse ActionResponse
	 */
	@SuppressWarnings("rawtypes")
	@ActionMapping(params = "submit_action=closeProcurement")
	protected void closeProcurement(AuthenticationBean aoAuthenticationBean, BindingResult aoResult,
			ActionRequest aoRequest, ActionResponse aoResponse)
	{
		Channel loChannelObj = new Channel();
		HhsAuditBean loAuditBean = null;
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		try
		{
			validator.validate(aoAuthenticationBean, aoResult);
			Map loAuthenticateMap = validateUser(aoAuthenticationBean.getUserName(),
					aoAuthenticationBean.getPassword(), aoRequest);
			Boolean loAuthStatus = (Boolean) loAuthenticateMap.get(HHSConstants.IS_VALID_USER);
			if (!loAuthStatus)
			{
				aoResponse
						.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
								HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.MESSAGE_M38));
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.ERROR_PAGE_CLOSE);
			}
			else
			{
				loChannelObj.setData(HHSConstants.AUTH_BEAN_LOWERCASE, aoAuthenticationBean);
				HashMap<String, String> loStatusUpdateMap = new HashMap<String, String>();
				loStatusUpdateMap.put(HHSConstants.PROC_STATUS_CODE, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_CLOSED));
				loStatusUpdateMap.put(HHSConstants.USER_ID, lsUserId);
				loStatusUpdateMap.put(HHSConstants.PROCUREMENT_ID, aoAuthenticationBean.getProcurementId());
				loChannelObj.setData(HHSConstants.LO_STATUS_UPDATE_MAP, loStatusUpdateMap);
				HashMap<String, Object> loCloseProcMap = new HashMap<String, Object>();
				loCloseProcMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, aoAuthenticationBean.getProcurementId());
				loCloseProcMap.put(P8Constants.PE_WORKFLOW_TASK_TYPE, P8Constants.PROPERTY_PE_TASK_TYPE_AWARD_APPROVAL);
				// Start || Changes made for enhancement 6448 for Release 3.8.0
				loCloseProcMap.put(HHSConstants.PROPERTY_PE_TASK_VISIBILITY, HHSConstants.ONE);
				// End || Changes made for enhancement 6448 for Release 3.8.0
				loAuditBean = new HhsAuditBean();
				loAuditBean.setAuditTableIdentifier(HHSConstants.ACCELERATOR_AUDIT);
				loAuditBean.setEntityType(HHSConstants.PROCUREMENT);
				loAuditBean.setData(HHSConstants.PROCUREMENT_STATUS_CHANGED_CLOSE);
				loAuditBean.setEntityId(aoAuthenticationBean.getProcurementId());
				loAuditBean.setEventName(HHSConstants.CLOSE);
				loAuditBean.setUserId(lsUserId);
				loAuditBean.setEventType(HHSConstants.CLOSE_PROC);
				loChannelObj.setData(HHSConstants.PROCUREMENT_ID, aoAuthenticationBean.getProcurementId());
				loChannelObj.setData(HHSConstants.AUDIT_BEAN, loAuditBean);
				loChannelObj.setData(HHSConstants.AUTH_STATUS_FLAG, true);
				loChannelObj.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
				loChannelObj.setData(HHSConstants.AO_HMWF_REQUIRED_PROPS, loCloseProcMap);

				// channel variables added for enhancement 3.1.0 : 6025
				loChannelObj.setData(HHSConstants.AS_USER_ID, lsUserId);
				loChannelObj.setData(HHSConstants.AS_STATUS_ID, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_CLOSED));
				loChannelObj.setData(HHSConstants.PROCUREMENT_ID, aoAuthenticationBean.getProcurementId());

				HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.CLOSE_PROCUREMENT);
				Boolean loProcStatusFlag = (Boolean) loChannelObj.getData(HHSConstants.PROCUREMENT_STATUS_FLAG);
				// Start || Changes made for enhancement 6448 for Release 3.8.0
				Boolean loTaskAvailable = (Boolean) loChannelObj.getData(HHSConstants.TASK_AVAILABLE);
				if (loTaskAvailable)
				{
					aoResponse.setRenderParameter(HHSConstants.CLOSE_PROCUREMENT_SUCCESS, HHSConstants.NO);
					aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.VIEW_PROCUREMENT);
				}
				else if (loProcStatusFlag)
				{
					aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.CLOSE_PROCUREMENT));
					aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
							ApplicationConstants.MESSAGE_PASS_TYPE);
				}
				// End || Changes made for enhancement 6448 for Release 3.8.0
			}
		}
		catch (ApplicationException loEx)
		{
			Map<String, HhsAuditBean> loParamMap = new HashMap<String, HhsAuditBean>();
			loParamMap.put(HHSConstants.AO_AUDIT_BEAN, loAuditBean);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap, loEx);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.ERROR_PAGE_CLOSE);
		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("error occurred while closing procurement :", loEx);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.ERROR_PAGE_CLOSE);
		}
	}

	/**
	 * This method is used to render the error page when credentials are wrong
	 * while closing a procurement.
	 * 
	 * <ul>
	 * <li>1. Get user name from closeProcurement jsp and set in request.</li>
	 * </ul>
	 * 
	 * @param aoRequest - RenderRequest
	 * @param aoResponse - RenderResponse
	 * @return - ModelAndView
	 */
	@RenderMapping(params = "render_action=errorPageClose")
	protected ModelAndView renderCloseProcurement(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		ModelAndView loModelAndView = new ModelAndView(HHSConstants.CLOSE_PROCUREMENT);
		try
		{

			aoRequest.setAttribute(HHSConstants.CLOSE_PROC_ID, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
					aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE));
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE_TYPE));
		}
		catch (Exception loException)
		{
			setGenericErrorMessage(aoRequest);
		}
		return loModelAndView;
	}

	/**
	 * This method is used to redirect user to the tab clicked
	 * 
	 * <ul>
	 * <li>Read request params from the request</li>
	 * <li>Set Render params for
	 * procurementId,ProposalId,topLevelfromReques,midLevelFromRequest
	 * ,competitionPoolId ,render_action,action</li>
	 * 
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * @param aoRequest - action request
	 * @param aoResponse - action response
	 */
	@ActionMapping(params = "submit_action=navigationAction")
	public void actionNavigation(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		try
		{
			String lsAction = PortalUtil.parseQueryString(aoRequest, HHSConstants.FOR_ACTION);
			String lsProcurementId = PortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
			String lsProposalId = PortalUtil.parseQueryString(aoRequest, HHSConstants.PROPOSAL_ID);
			String lsRenderAction = PortalUtil.parseQueryString(aoRequest, HHSConstants.RENDER_ACTION);
			String lsHideExitButton = PortalUtil.parseQueryString(aoRequest, HHSConstants.HIDE_EXIT_PROCUREMENT);
			String lsCompetitionPoolId = PortalUtil.parseQueryString(aoRequest, HHSConstants.COMPETITION_POOL_ID);
			String lsEvaluationGroupId = PortalUtil.parseQueryString(aoRequest, HHSConstants.EVALUATION_GROUP_ID);
			String lsESValue = PortalUtil.parseQueryString(aoRequest, HHSConstants.ES);
			String lsEvaluationPoolMappingId = PortalUtil.parseQueryString(aoRequest,
					HHSConstants.EVALUATION_POOL_MAPPING_ID);
			if (lsAction != null && !lsAction.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.ACTION, lsAction);
			}
			if (lsProcurementId != null && !lsProcurementId.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			}
			if (lsProposalId != null && !lsProposalId.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.PROPOSAL_ID, lsProposalId);
			}
			if (lsRenderAction != null && !lsRenderAction.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, lsRenderAction);
			}
			if (lsHideExitButton != null && !lsHideExitButton.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.HIDE_EXIT_PROCUREMENT, lsHideExitButton);
			}
			if (lsCompetitionPoolId != null && !lsCompetitionPoolId.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.COMPETITION_POOL_ID, lsCompetitionPoolId);
			}
			if (lsESValue != null && !lsESValue.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.ES, lsESValue);
			}
			if (lsEvaluationGroupId != null && !lsEvaluationGroupId.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.EVALUATION_GROUP_ID, lsEvaluationGroupId);
			}
			if (lsEvaluationPoolMappingId != null && !lsEvaluationPoolMappingId.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvaluationPoolMappingId);
			}
			setNavigationParamsInRender(aoRequest, aoResponse);
		}
		catch (Exception loException)
		{
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
	}

	/**
	 * This method will be executed for default action, namely - URL refresh
	 * <ul>
	 * <li>This method capture all the action request which are not been
	 * captured by any of parameterized action methods</li>
	 * </ul>
	 * @param aoRequest - action request
	 * @param aoResponse - action response
	 */
	@ActionMapping
	protected void handleActionRequestInternal(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		// Note Donot remove this method as it will give the action mapping does
		// not exists error
	}

	/**
	 * This method is used to save favorites for a provider user
	 * 
	 * <ul>
	 * <li>Get the favorite, non favorite keys from request</li>
	 * <li>Get user id, organization id from session and set them in channel</li>
	 * <li>Invoke transaction "updateFavoritesTransaction" to update the
	 * favorite list</li>
	 * </ul>
	 * <ul>
	 * <li>New Method in R4</li>
	 * </ul>
	 * 
	 * @param aoProcurementBean a procurement bean object
	 * @param aoRequest - action request
	 * @param aoResponse - action response
	 */
	@ActionMapping(params = "submit_action=saveFavorites")
	public void saveFavorites(@ModelAttribute("Procurement") Procurement aoProcurementBean, ActionRequest aoRequest,
			ActionResponse aoResponse)
	{
		try
		{
			PortletSession loSession = aoRequest.getPortletSession();
			String lsFavoriteIds = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.FAVORITE_IDS);
			String lsNonFavoriteIds = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.NON_FAVORITE_IDS);
			String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);
			String lsOrgId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);
			Channel loChannel = new Channel();
			loChannel.setData(HHSConstants.FAVORITE_IDS, lsFavoriteIds);
			loChannel.setData(HHSConstants.NON_FAVORITE_IDS, lsNonFavoriteIds);
			loChannel.setData(HHSConstants.USER_ID, lsUserId);
			loChannel.setData(HHSConstants.ORGANIZATION_ID, lsOrgId);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.UPDATE_FAVORITES_TRX);
			boolean lbSaveSuccessfull = (Boolean) loChannel.getData(HHSConstants.LB_SUCCESS_STATUS);
			if (aoProcurementBean.getIsFavoriteDisplayed() != null
					&& aoProcurementBean.getIsFavoriteDisplayed().equalsIgnoreCase(HHSConstants.TRUE))
			{
				getPagingParams(loSession, aoProcurementBean, HHSConstants.ONE, HHSConstants.PROCUREMENT_ROADMAP_KEY);
			}
			else
			{
				aoResponse.setRenderParameter(HHSConstants.NEXT_PAGE_PARAM, aoProcurementBean.getPageIndex());
			}
			aoRequest.getPortletSession().setAttribute(HHSConstants.PROCUREMENT_SESSION_BEAN, aoProcurementBean,
					PortletSession.PORTLET_SCOPE);
			if (lbSaveSuccessfull)
			{
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE,
						"Updates to your organization's favorites were successfully saved");
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_PASS_TYPE);
			}
		}
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("error occurred while saving procurement favorites :" + loExp.getMessage());
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("error occurred while saving procurement favorites :" + loExp.getMessage());
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
	}

	/**
	 * This method is invoked to switch between show favorite, show all. This
	 * method gets the updated details to be displayed and sets it in session
	 * <ul>
	 * <li>1.Invoke removeAttribute method</li>
	 * <li>2.Invoke remove method</li>
	 * 
	 * </ul>
	 * <ul>
	 * <li>New Method in R4</li>
	 * </ul>
	 * * @param aoProcurementBean a procurement bean object
	 * @param aoRequest - action request
	 * @param aoResponse - action response
	 * @param aoModelMap - ModelMap
	 */
	@ActionMapping(params = "submit_action=showFavorites")
	protected void actionShowFavoritesRoadmap(@ModelAttribute("Procurement") Procurement aoProcurementBean,
			ActionRequest aoRequest, ActionResponse aoResponse, ModelMap aoModelMap)
	{
		try
		{
			aoRequest.getPortletSession().removeAttribute(HHSConstants.PROCUREMENT_SESSION_BEAN,
					PortletSession.PORTLET_SCOPE);
			aoModelMap.remove(HHSConstants.PROCUREMENT);
			aoResponse.setRenderParameter(HHSConstants.FAVORITE_FLAG, aoProcurementBean.getIsFavoriteDisplayed());
		}
		// Catch the exception thrown by transaction and set the error message
		// in request object and pass to jsp
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception Occurred while displaying favorite list : " + loExp);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
	}

	/**
	 * Method added for Release 3.2.0 enhancement: 5684
	 * @param asProcID
	 * @param asUserID
	 * @param asAgencyId
	 * @return
	 * @throws ApplicationException
	 */
	private HashMap<String, Object> getNotificationMapNT402(String asProcID, String asUserID, String asAgencyId,
			String asUserRole) throws ApplicationException
	{
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		try
		{

			String lsUserId = asUserID;
			String lsProcurementId = asProcID;
			NotificationDataBean loNotificationDataBean = new NotificationDataBean();
			String lsCityUrl = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
					HHSConstants.PROP_CITY_URL);
			List<String> loNotificationAlertList = new ArrayList<String>();
			loNotificationAlertList.add(HHSConstants.NT402);

			HashMap<String, String> loLinkMap = new HashMap<String, String>();
			StringBuilder lsBfApplicationUrl = new StringBuilder();
			lsBfApplicationUrl.append(lsCityUrl).append(HHSConstants.AGENCY_SETTING_CITY)
					.append(HHSConstants.NOTIFICATION_GENERATED_FROM_NT402);
			StringBuilder lsBfApplicationUrl1 = new StringBuilder();
			lsBfApplicationUrl1.append(lsCityUrl).append(HHSConstants.TASK_URL);
			loLinkMap.put(HHSConstants.LINK, lsBfApplicationUrl.toString());
			loLinkMap.put(HHSConstants.LINK1, lsBfApplicationUrl1.toString());
			loNotificationDataBean.setLinkMap(loLinkMap);
			loNotificationDataBean.setAgencyLinkMap(loLinkMap);
			List<String> loAgencyIdList = new ArrayList<String>();
			loAgencyIdList.add(asAgencyId);
			loNotificationDataBean.setAgencyList(loAgencyIdList);
			loNotificationMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
			loNotificationMap.put(HHSConstants.NT402, loNotificationDataBean);
			loRequestMap.put(HHSConstants.AGENCY_USER_ID, lsUserId);
			loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
			loNotificationMap.put(ApplicationConstants.ENTITY_ID, lsProcurementId);
			loNotificationMap.put(ApplicationConstants.ENTITY_TYPE, HHSConstants.PROCUREMENT);
			loNotificationMap.put(HHSConstants.CREATED_BY_USER_ID, lsUserId);
			loNotificationMap.put(HHSConstants.MODIFIED_BY, lsUserId);
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occured while setting notification map", aoEx);
			throw new ApplicationException("Error was occurred while get notfication for getNotificationMapNT402", aoEx);
		}
		return loNotificationMap;
	}

	/**
	 * Method changed in R5<br/>
	 * Method added for Release 3.2.0 enhancement: 5684 Method updated for
	 * Release 3.3.0 #defect 6458 : removed code for fetching Procurement title
	 * from request Parameter .Fetching it from server end instead.
	 * @param aoRequest
	 * @param aoResponse
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	@ResourceMapping("launchPCOF")
	protected void launchPCOFTaskbyCity(ResourceRequest aoRequest, ResourceResponse aoResponse)
			throws ApplicationException
	{
		HashMap loHmRequiredProps = new HashMap();
		PrintWriter loOut = null;
		String lsProcID = aoRequest.getParameter(HHSConstants.PROCID);
		String lsAgencyId = aoRequest.getParameter(HHSConstants.AGENCYID);
		// R5 change starts
		String lsOnlyPSR = aoRequest.getParameter(HHSR5Constants.ONLY_PSR);
		// R5 change ends
		Channel loChannel = new Channel();
		int liMessageCode = 0;
		String lsReturnMsg = HHSConstants.EMPTY_STRING;
		try
		{
			loOut = aoResponse.getWriter();
			PortletSession loSession = aoRequest.getPortletSession();
			P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			loChannel.setData(HHSConstants.PROCUREMENT_ID_KEY, lsProcID);
			loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
			aoResponse.setContentType(HHSConstants.APPLICATION_JSON);
			String lsStatusId;
			CBGridBean loCbGridBean = new CBGridBean();

			String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);
			String lsUserRole = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ROLE,
					PortletSession.APPLICATION_SCOPE);
			loHmRequiredProps.put(HHSConstants.PROCUREMENT_ID, lsProcID);
			loHmRequiredProps.put(HHSConstants.SUBMITTED_BY, lsUserId);
			loHmRequiredProps.put(HHSConstants.WORKFLOW_NAME, HHSConstants.WF_PROCUREMENT_CERTIFICATION_FUND);
			// R5 change starts
			loHmRequiredProps.put(HHSR5Constants.ONLY_PSR, Boolean.parseBoolean(lsOnlyPSR));
			if (Boolean.parseBoolean(lsOnlyPSR))
			{
				lsReturnMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSR5Constants.PSR_SUBMIT_SUCCESS);
			}
			else
			{
				lsReturnMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.PROC_SUBMIT_SUCCESS);
			}
			// R5 change ends
			HashMap<String, Object> loNotificationParams = getNotificationMapNT402(lsProcID, lsUserId, lsAgencyId, lsUserRole);
			loChannel.setData(HHSConstants.LO_HM_NOTIFY_PARAM,loNotificationParams);
			loChannel.setData(HHSConstants.AO_HMWF_REQUIRED_PROPS, loHmRequiredProps);
			loChannel.setData(HHSConstants.LB_AUTH_STATUS_FLAG, true);
			loChannel.setData(HHSConstants.PROCUREMENT_ID, lsProcID);
			loChannel.setData(HHSConstants.PROCUREMENT_STATUS_FLAG, true);
			//8400 changes
			if (HHSConstants.TRUE.equalsIgnoreCase(lsOnlyPSR))
            {
				loChannel.setData(HHSConstants.PROCUREMENT_STATUS_FLAG, false);
				
			}
			//8400 changes end here
			// Set userId and Procurement Id to update the PCOF status
			loCbGridBean.setProcurementID(lsProcID);
			loCbGridBean.setModifyByAgency(lsUserId);
			loCbGridBean.setAgencyId(lsAgencyId);
			loChannel.setData(HHSConstants.CB_GRID_BEAN_OBJ, loCbGridBean);
			lsStatusId = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_PCOF_IN_REVIEW);
			loCbGridBean.setStatusId(lsStatusId);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.LAUNCH_PCOF_TASK_CITY);
		}
		catch (ApplicationException aoAppExe)
		{
			String lsLevelErrorMessage = null;
			ApplicationException loAppEx = (ApplicationException) aoAppExe.getRootCause();
			if (null != loAppEx)
			{
				lsLevelErrorMessage = (String) loAppEx.getContextData().get(HHSConstants.LEVEL_ERROR_MESSAGE);
			}
			if (null == lsLevelErrorMessage)
			{
				lsLevelErrorMessage = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			}
			liMessageCode = 1;
			lsReturnMsg = lsLevelErrorMessage;
			LOG_OBJECT.Error("ApplicationException occured in launchPCOFTaskbyCity", aoAppExe);
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error occured while launching financial workflow", aoExp);
		}
		finally
		{
			loOut.print(HHSConstants.ERROR_1 + liMessageCode + HHSConstants.MESSAGE_1 + lsReturnMsg
					+ HHSConstants.CLOSING_BRACE_1);
			loOut.flush();
			loOut.close();
		}
	}

	/**
	 * This method is added in Release 5.This method is used to validate PcofPSR
	 * task
	 * <ul>
	 * <li>Get amount from Procurement bean and set in channel</li>
	 * <li>Get procurement id from session and set in channel</li>
	 * <li>Invoke transaction "validatePCOFPsrTasks" to verify the task</li>
	 * </ul>
	 * @param aoProcurementBean Procurement Bean
	 * @param aoRequest ResourceRequest
	 * @param aoResponse ResourceResponse
	 * @throws ApplicationException
	 */
	@ResourceMapping("verifyPcofPSR")
	public void verifyPcofPSR(@ModelAttribute("Procurement") Procurement aoProcurementBean, ResourceRequest aoRequest,
			ResourceResponse aoResponse) throws ApplicationException
	{
		PrintWriter loPrintWriter = null;
		try
		{
			loPrintWriter = aoResponse.getWriter();
			aoResponse.setContentType(HHSConstants.APPLICATION_JSON);
			String lsProcurementId = HHSPortalUtil.parseQueryString(aoRequest, HHSR5Constants.PROCUREMENT_ID);
			Channel loChannel = new Channel();
			loChannel.setData(HHSR5Constants.PROCUREMENT_ID, lsProcurementId);
			loChannel.setData(HHSR5Constants.AMOUNT, aoProcurementBean.getEstProcurementValue());
			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.VALIDATE_PCOF_PSR_TASKS,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			Procurement loProcurement = (Procurement) loChannel.getData(HHSConstants.PROCUREMENT_BEAN);
			loPrintWriter.write("{\"" + HHSR5Constants.APPROVE_PSR_FLAG + "\": "
					+ loChannel.getData(HHSR5Constants.APPROVE_PSR_FLAG) + ", \"" + HHSR5Constants.APPROVE_PCOF_FLAG
					+ "\": " + loChannel.getData(HHSR5Constants.APPROVE_PCOF_FLAG) + ", \"Procurement\":"
					+ JSONUtility.convertToString(loProcurement) + "}");
		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("error occurred while verifying PcofPSR already approved:", loExp);
		}
		finally
		{
			if (null != loPrintWriter)
			{
				loPrintWriter.flush();
				loPrintWriter.close();
			}
		}
	}
	// changes for r5 ends
	
	
	
	/**
	 * R 7.2.0 QC 8419
	 * This method sets the User 'CurrRole' on each Procurement, if HttpSession has the value of OBSERVER set for the current user.
	 * 
	 * @param aoSession
	 * @param loProcurementList
	 */
	private void setOversightRoleFlag(PortletSession aoSession, List<Procurement> loProcurementList) {
		
		String roleCurrent = (String)aoSession.getAttribute(ApplicationConstants.KEY_SESSION_ROLE_CURRENT, PortletSession.APPLICATION_SCOPE); 
		if( ApplicationConstants.ROLE_OBSERVER.equalsIgnoreCase(roleCurrent)) {
			
			LOG_OBJECT.Info("Setting currRole as OBSERVER for ProcurmentList = " + loProcurementList);
			for (Procurement loProcurement : loProcurementList)
			{
				loProcurement.setRoleCurrent(roleCurrent);
			}

		}
	}
}