package com.nyc.hhs.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.mvc.AbstractController;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.TransactionConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.EINBean;
import com.nyc.hhs.model.MissingNameBean;
import com.nyc.hhs.model.NotificationDataBean;
import com.nyc.hhs.model.OrgStaffDetailBean;
import com.nyc.hhs.model.OrganizationBean;
import com.nyc.hhs.model.StaffDetails;
import com.nyc.hhs.model.UserBean;
import com.nyc.hhs.model.UserThreadLocal;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.ApplicationSession;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.PortalUtil;

/**
 * This controller is for maintenance of missing profile of user and to create
 * new organization account for the user
 * 
 */

public class MissingProfileInfoController extends AbstractController
{

	private static final LogInfo LOG_OBJECT = new LogInfo(MissingProfileInfoController.class);

	String msAction = "";
	private static final String MISSING_PROFILE_INFO = "missingProfileInfo";
	private static final String TERMS_CONDITIONS = "termsConditions";
	private static final String MISSING_SCREEN = "missingScreen";
	private static final String EIN_TIN_SERACH = "einTinSearch";
	private static final String ORG_ACCT_CREATED = "orgAcctAlreadyCreated";
	private static final String USER_ACCT_REQ_SUBMITTED = "userAcctReqSubmitted";
	private static final String ACCT_ADMIN = "acctAdmin";
	private static final String ACCT_ADMIN_IDENTIFICATION = "acctAdminIdentification";
	private static final String CREAT_ADMIN_ACCT = "createAdminAcct";
	private static final String ORG_ACCT_REQUEST_SUBMITTED = "orgAcctRequestSubmitted";
	private static final String ORG_ACCOUNT_CREATED = "orgAcctCreated";

	private static final String RETURN_ORG_ACCT_CREATE_NUMBER = "ReturnContactUsNumber";
	private static final String WF_NAME = "WFName";
	private static final String ORG_ACCT_REQ_FILE_NET = "orgAcctCreateListFileNet";
	private static final String WORKFLOWFAIL = "workflowfail";

	private static final String HOME_PAGE = "HomePage";
	private static final String FOUND = "Found";
	private static final String REQUEST_COULD_NOT_BE_COMPLETED = "This request could not be completed. Please try again in a few minutes.";
	private static final String ACCOUNT_REQUEST_ALREADY_RAISED = "Your request for organization account access has already been sent for review.";

	/**
	 * This method is to render the next page depending on the action while
	 * creating organization account
	 * 
	 * @param aoRequest to get screen parameters and next page to be displayed
	 * @param aoResponse setting response parameter for email display in JSP
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	protected ModelAndView handleRenderRequestInternal(RenderRequest aoRequest, RenderResponse aoResponse)
			throws ApplicationException
	{
		long loStartTime = System.currentTimeMillis();
		aoResponse.setContentType("text/html");
		ModelAndView loModelAndView = null;
		String lsWorkFlow = PortalUtil.parseQueryString(aoRequest, "workflow");
		String lsOrgId = PortalUtil.parseQueryString(aoRequest, "orgId");
		// new header line
		if (null == lsWorkFlow)
		{
			aoRequest.getPortletSession().setAttribute("MissinProfileHeader", "MissinProfileHeader",
					PortletSession.APPLICATION_SCOPE);
		}
		String lsFormPath = ApplicationConstants.MISSING_PROFILE_INFORMATION;
		Map<String, Object> loMapForRender = new HashMap<String, Object>();
		MissingNameBean loMissingNameBean = new MissingNameBean();

		// This condition renders create organization account ready only screen
		// for the provider workflow launched
		if (null != lsWorkFlow && lsWorkFlow.equalsIgnoreCase("organizationAccountRequest"))
		{
			lsFormPath = ApplicationConstants.MISSING_PROFILE_CREATE_ADMIN_ACCT_READONLY;
			Channel loChannel = new Channel();
			loMissingNameBean = new MissingNameBean();
			List<OrgStaffDetailBean> loList = null;

			loChannel.setData("aoOrgId", lsOrgId);
			TransactionManager.executeTransaction(loChannel, "getMissingBeanDetails");
			loList = (List<OrgStaffDetailBean>) loChannel.getData("aoOrgStaffDetailList");
			loMissingNameBean = populateMissingBeanForNycUserId(loList);
			ApplicationSession.setAttribute(loMissingNameBean, aoRequest, "MissingNameBean");
			loMissingNameBean = (MissingNameBean) ApplicationSession.getAttribute(aoRequest, true, "MissingNameBean");
			aoRequest.setAttribute("MissingNameBean", loMissingNameBean);
		}
		// setting parameters in application session that are received from
		// Login Controller
		setParameterInSession(aoRequest);
		// setting UserDN, FirstName,LastName in Application Session
		try
		{
			lsFormPath = getNavigationPath(aoRequest, lsFormPath);
			loMapForRender.put("memberState", getState());
			loMapForRender.put("adminOffTitle", getAdminTitle());
			/**
			 * This condition sets the top level status message of the
			 * transaction on screen
			 */
			if (aoRequest.getParameter("transactionMessage") != null
					&& !"".equalsIgnoreCase(aoRequest.getParameter("transactionMessage")))
			{
				aoRequest.setAttribute("transactionStatus", aoRequest.getParameter("transactionStatus"));
				aoRequest.setAttribute("transactionMessage", aoRequest.getParameter("transactionMessage"));
			}
			loModelAndView = new ModelAndView(lsFormPath, loMapForRender);
		}
		catch (Exception aoFbAppEx)
		{
			LOG_OBJECT.Error("Error occurred while inserting Missing profile details", aoFbAppEx);
		}
		msAction = "";
		long loEndTimeTime = System.currentTimeMillis();
		try
		{
			LOG_OBJECT.Debug("TIME TAKEN for execution of render Method in MissingProfileInfoController = "
					+ (loEndTimeTime - loStartTime));
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Error while execution of render Method in MissingProfileInfoController ", aoEx);
		}
		return loModelAndView;

	}

	/**
	 * This method is to get the navigation path while creating organization
	 * account for the user
	 * 
	 * @param aoRequest to get screen parameters and next action to be performed
	 * @param asFormPath the next flow in the execution
	 * @throws IOException
	 * @throws ApplicationException
	 */
	private String getNavigationPath(RenderRequest aoRequest, String asFormPath) throws IOException,
			ApplicationException
	{
		MissingNameBean loMissingNameBean;
		LOG_OBJECT.Debug("MI$$ Missing profile getNavigationPath for email:: "
				+ aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_EMAIL_ID,
						PortletSession.APPLICATION_SCOPE) + " and formpath:: " + asFormPath + " and msAction:: "
				+ msAction);
		// This condition renders missing profile information screen
		if (null != msAction && msAction.equals(MISSING_PROFILE_INFO))
		{
			LOG_OBJECT.Debug("Missing profile screen rendered");
			asFormPath = ApplicationConstants.MISSING_PROFILE_INFORMATION;
		}
		// This condition fetch terms and conditions from File Net and renders
		// terms and conditions screen
		else if (null != msAction && msAction.equals(TERMS_CONDITIONS))
		{
			LOG_OBJECT.Debug("Missing profile terms and conditions rendered");
			asFormPath = ApplicationConstants.MISSING_PROFILE_TERMS_CONDITIONS;
			String lsDisplayTermsCondition = getTermsAndCondition(aoRequest, "System Terms & Conditions");
			aoRequest.setAttribute("lsDisplayTermsCondition", lsDisplayTermsCondition);
		}
		// This condition renders EIN Search screen
		else if (null != msAction && msAction.equals(EIN_TIN_SERACH))
		{
			LOG_OBJECT.Debug("MI$$ Missing profile ein tin search rendered ");
			asFormPath = ApplicationConstants.MISSING_PROFILE_EIN_TIN_SEARCH;
		}
		// This condition renders organization account created screen
		else if (null != msAction && msAction.equals(ORG_ACCT_CREATED))
		{
			LOG_OBJECT.Debug("MI$$ Org account created screen rendered ");
			if (null != ApplicationSession.getAttribute(aoRequest, true, "einNumber"))
			{
				aoRequest.setAttribute("einNumber", ApplicationSession.getAttribute(aoRequest, false, "einNumber"));
			}
			asFormPath = ApplicationConstants.MISSING_PROFILE_ACCT_ALREADY_CREATED;
		}
		// This condition renders user account request submitted screen
		else if (null != msAction && msAction.equals(USER_ACCT_REQ_SUBMITTED))
		{
			LOG_OBJECT.Debug("MI$$ accnt request sumbitted screen rendered ");
			// start
			aoRequest.setAttribute("emailAddr", ApplicationSession.getAttribute(aoRequest, false, "loEmailAddress"));
			// end
			asFormPath = ApplicationConstants.MISSING_PROFILE_USER_REQUEST_SUBMITTED;
		}
		// This condition renders account administrator screen
		else if (null != msAction && msAction.equals(ACCT_ADMIN))
		{
			LOG_OBJECT.Debug("MI$$ Missing profile acct admin ");
			asFormPath = ApplicationConstants.MISSING_PROFILE_ACCT_ADMIN;
		}
		// This condition renders account administrator identification screen
		else if (null != msAction && msAction.equals(ACCT_ADMIN_IDENTIFICATION))
		{
			LOG_OBJECT.Debug("MI$$ Missing profile acct admin identification ");
			asFormPath = ApplicationConstants.MISSING_PROFILE_ACCT_ADMIN_IDENTIFICATION;
		}
		// This condition renders create administrator account screen
		else if (null != msAction && msAction.equals(CREAT_ADMIN_ACCT))
		{
			LOG_OBJECT.Debug("MI$$ Missing profile create admin account ");
			loMissingNameBean = new MissingNameBean();
			boolean lbFlag = false;
			if (null != ApplicationSession.getAttribute(aoRequest, true, "MissingFlag"))
			{
				lbFlag = (Boolean) ApplicationSession.getAttribute(aoRequest, true, "MissingFlag");
			}
			// if missing flag is true then set the bean variables with missing
			// info screen fields else
			// set the bean variables with FirstName,MiddleName and LastName
			// from LDAP
			if (!lbFlag)
			{
				loMissingNameBean.setMsAdminFirstName((String) ApplicationSession.getAttribute(aoRequest, true,
						"userFirstName"));
				loMissingNameBean.setMsAdminMiddleInitial((String) ApplicationSession.getAttribute(aoRequest, true,
						"userMiddleName"));
				loMissingNameBean.setMsAdminLastName((String) ApplicationSession.getAttribute(aoRequest, true,
						"userLastName"));
			}
			else
			{
				loMissingNameBean.setMsAdminFirstName((String) ApplicationSession.getAttribute(aoRequest, true,
						"adminFirstName"));
				loMissingNameBean.setMsAdminMiddleInitial((String) ApplicationSession.getAttribute(aoRequest, true,
						"adminMiddleName"));
				loMissingNameBean.setMsAdminLastName((String) ApplicationSession.getAttribute(aoRequest, true,
						"adminLastName"));
			}
			loMissingNameBean.setMsAdminNYCId((String) ApplicationSession.getAttribute(aoRequest, true, "adminNycId"));
			loMissingNameBean.setMsAdminEmailAdd((String) ApplicationSession.getAttribute(aoRequest, true,
					"adminEmailId"));
			loMissingNameBean.setMsOrgEinTinNumber((String) ApplicationSession.getAttribute(aoRequest, true,
					"einNumber"));

			aoRequest.setAttribute("MissingNameBean", loMissingNameBean);
			asFormPath = ApplicationConstants.MISSING_PROFILE_CREATE_ADMIN_ACCOUNT;
		}
		// This condition renders organization account request submitted screen
		else if (null != msAction && msAction.equals(ORG_ACCT_REQUEST_SUBMITTED))
		{
			aoRequest.setAttribute("adminEmail", ApplicationSession.getAttribute(aoRequest, false, "lsAdminEmail"));
			aoRequest.setAttribute("ceoEmail", ApplicationSession.getAttribute(aoRequest, false, "lsCeoEmail"));
			asFormPath = ApplicationConstants.MISSING_PROFILE_ORG_ACCT_REQUEST_SUBMITTED;
		}
		// This condition renders organization account created screen
		else if (null != msAction && msAction.equals(ORG_ACCOUNT_CREATED))
		{
			aoRequest.setAttribute("adminEmail", ApplicationSession.getAttribute(aoRequest, false, "lsAdminEmail"));
			asFormPath = ApplicationConstants.MISSING_PROFILE_ORG_ACCT_CREATED;
		}
		// This condition renders organization account request submitted
		// readonly screen
		else if (null != msAction && msAction.equals("readonly"))
		{
			loMissingNameBean = (MissingNameBean) ApplicationSession.getAttribute(aoRequest, false, "MissingNameBean");
			aoRequest.setAttribute("MissingNameBean", loMissingNameBean);
			asFormPath = ApplicationConstants.MISSING_PROFILE_CREATE_ADMIN_ACCT_READONLY;
		}
		// This condition renders Login screen
		else if (null != msAction && msAction.equals(ApplicationConstants.LOGIN_PORTLET))
		{
			asFormPath = ApplicationConstants.LOGIN_PORTLET;
		}
		else if (null != msAction && msAction.trim().equals(""))
		{
			LOG_OBJECT.Debug("MI$$ ein search page when msAction blank ");
			asFormPath = ApplicationConstants.MISSING_PROFILE_EIN_TIN_SEARCH;
		}
		else
		{
			LOG_OBJECT.Debug("MI$$ ein search page when msAction blank else");
			asFormPath = ApplicationConstants.MISSING_PROFILE_EIN_TIN_SEARCH;
		}

		return asFormPath;
	}

	/**
	 * This method set user profile details in session that are retrieved later
	 * in the application
	 * 
	 * @param aoRequest to get screen parameters and next action to be performed
	 */
	private void setParameterInSession(RenderRequest aoRequest)
	{
		boolean lbMissingFlag = false;
		final String lsAction = PortalUtil.parseQueryString(aoRequest, "next_form");
		if (lsAction != null)
		{
			msAction = lsAction;
			if (lsAction.equalsIgnoreCase("missingProfileInfo"))
			{
				lbMissingFlag = true;
				ApplicationSession.setAttribute(lbMissingFlag, aoRequest, "MissingFlag");
			}
		}
		final String lsHomePage = PortalUtil.parseQueryString(aoRequest, "nextAction");
		if (lsHomePage != null && lsHomePage.equalsIgnoreCase(MissingProfileInfoController.HOME_PAGE))
		{
			ApplicationSession.setAttribute(lsHomePage, aoRequest, "HomePage");
		}
		// setting UserDN, FirstName,LastName in Application Session
		final String lsAdminNycId = PortalUtil.parseQueryString(aoRequest, "useremail");
		if (lsAdminNycId != null)
		{
			ApplicationSession.setAttribute(lsAdminNycId, aoRequest, "adminNycId");
		}

		final String lsUserDn = PortalUtil.parseQueryString(aoRequest, "userDn");
		if (lsUserDn != null)
		{
			ApplicationSession.setAttribute(lsUserDn, aoRequest, "userDN");
		}

		final String lsAdminEmailId = PortalUtil.parseQueryString(aoRequest, "useremail");
		if (lsAdminEmailId != null)
		{
			ApplicationSession.setAttribute(lsAdminEmailId, aoRequest, "adminEmailId");
		}

		final String lsUserFirstName = PortalUtil.parseQueryString(aoRequest, "firstName");
		if (lsUserFirstName != null)
		{
			ApplicationSession.setAttribute(lsUserFirstName, aoRequest, "userFirstName");
		}
		final String lsUserMiddleName = PortalUtil.parseQueryString(aoRequest, "middleName");
		if (lsUserMiddleName != null)
		{
			ApplicationSession.setAttribute(lsUserMiddleName, aoRequest, "userMiddleName");
		}
		final String lsUserLastName = PortalUtil.parseQueryString(aoRequest, "lastName");
		if (lsUserMiddleName != null)
		{
			ApplicationSession.setAttribute(lsUserLastName, aoRequest, "userLastName");
		}
		final String lsUserExistInLDAP = PortalUtil.parseQueryString(aoRequest, "ldap");
		if (lsUserExistInLDAP != null && !lsUserExistInLDAP.equalsIgnoreCase(""))
		{
			ApplicationSession.setAttribute(lsUserExistInLDAP, aoRequest, "userLdapRecord");
		}
		final String lbTcRequired = PortalUtil.parseQueryString(aoRequest, "tcRequired");
		if (lbTcRequired != null && !lbTcRequired.equalsIgnoreCase(""))
		{
			ApplicationSession.setAttribute(lbTcRequired, aoRequest, "tcRequired");
		}
		final String lsModifyBy = PortalUtil.parseQueryString(aoRequest, "modifyBy");
		if (lsModifyBy != null && !lsModifyBy.equalsIgnoreCase(""))
		{
			ApplicationSession.setAttribute(lsModifyBy, aoRequest, "modifyBy");
		}
	}

	/**
	 * This method decide the execution flow of the organization account
	 * creation process
	 * 
	 * @param aoRequest to get screen parameters and next action to be performed
	 * @param aoResponse decides the next execution flow
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	protected void handleActionRequestInternal(ActionRequest aoRequest, ActionResponse aoResponse)
			throws ApplicationException
	{
		long loStartTime = System.currentTimeMillis();
		String lsNextAction = aoRequest.getParameter("next_action");
		Channel loChannelObj = new Channel();
		String lsTransactionStatusMsg = "", lsTransactionStatus = "", lsTcCheck = "";
		String lsEmailId = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_EMAIL_ID, PortletSession.APPLICATION_SCOPE);
		String lsAdminEmailId = "", lsCeoEmailId = "";
		if (null == lsEmailId)
		{
			lsEmailId = ((String) ApplicationSession.getAttribute(aoRequest, true, "adminEmailId"));
		}
		boolean lbWorkFlowLaunch = false;
		LOG_OBJECT.Debug("Inside HandleActionRequestMethod in Missing controller for emailId:: " + lsEmailId
				+ " and lsNext_action:: " + lsNextAction);
		boolean lbSendRedirectFlag = false;
		LOG_OBJECT.Debug("MI$$ Missing profile request called : lsNext_action::: " + lsNextAction);
		try
		{
			if (null != lsNextAction && lsNextAction.equals(MISSING_SCREEN)
					&& null != aoRequest.getParameter("firstName"))
			{
				LOG_OBJECT.Debug(" Mi$4 missing screen rendered for ::: " + lsEmailId);
				lsTransactionStatusMsg = "failed";
				lsTransactionStatusMsg = MissingProfileInfoController.REQUEST_COULD_NOT_BE_COMPLETED;
				msAction = MISSING_PROFILE_INFO;
				updateUserProfile(aoRequest, loChannelObj);
				lsTransactionStatus = "passed";
				if (null != (String) ApplicationSession.getAttribute(aoRequest, true, "tcRequired"))
				{
					lsTcCheck = (String) ApplicationSession.getAttribute(aoRequest, true, "tcRequired");
				}
				if (lsTcCheck.equalsIgnoreCase("No"))
				{
					lbSendRedirectFlag = rendersHomePage(aoRequest, aoResponse, loChannelObj, lbSendRedirectFlag);
				}
				else
				{
					msAction = lsNextAction;
				}
			}
			else if (null != lsNextAction
					&& ((lsNextAction.equals(MISSING_SCREEN) && null == aoRequest.getParameter("firstName"))
							|| lsNextAction.equals(EIN_TIN_SERACH) || lsNextAction.equals(TERMS_CONDITIONS)))
			{ // renders home page if Terms and conditions are already checked
				// else render EIN Search
				if (null != (String) ApplicationSession.getAttribute(aoRequest, true,
						MissingProfileInfoController.HOME_PAGE))
				{
					LOG_OBJECT.Debug("Terms And Condition Accepted  Yes and Redirected to Home for ::: " + lsEmailId);
					lbSendRedirectFlag = rendersHomePage(aoRequest, aoResponse, loChannelObj, lbSendRedirectFlag);
					LOG_OBJECT.Debug("Terms And Condition Accepted  Yes and Redirected to Home successful ::: "
							+ lsEmailId);
				}
				else
				{
					LOG_OBJECT.Debug("Terms And Condition Accepted  Yes and Redirected to EIN Search for ::: "
							+ lsEmailId);
					msAction = lsNextAction;
				}
			}// This condition search EIN in EIN Master table and renders
				// organization account created page or Account admin page
			else if (null != lsNextAction && lsNextAction.equals(ORG_ACCT_CREATED))
			{
				lsTransactionStatus = "failed";
				lsTransactionStatusMsg = MissingProfileInfoController.REQUEST_COULD_NOT_BE_COMPLETED;
				Map loStatusMap = new HashMap();
				loStatusMap = (Map) userEinSearch(aoRequest, lsNextAction, lsTransactionStatus, lsTransactionStatusMsg);
				lsTransactionStatus = (String) loStatusMap.get("TransactionStatus");
				lsTransactionStatusMsg = (String) loStatusMap.get("TransactionStatusMsg");
			}// This condition send alert and notification to provider and
				// renders user account submitted screen
			else if (null != lsNextAction && lsNextAction.equals(USER_ACCT_REQ_SUBMITTED))
			{
				lsTransactionStatus = "failed";
				lsTransactionStatusMsg = MissingProfileInfoController.REQUEST_COULD_NOT_BE_COMPLETED;
				msAction = ORG_ACCT_CREATED;
				String lsTransStatus = sendRequestForOrgAccount(aoRequest);
				if (null != lsTransStatus && lsTransStatus.equalsIgnoreCase("failed"))
				{
					lsTransactionStatus = "failed";
					if (null != aoRequest.getParameter("orgEinTinNo"))
					{
						String lsEinNumber = (aoRequest.getParameter("orgEinTinNo"));
						ApplicationSession.setAttribute(lsEinNumber, aoRequest, "einNumber");
					}
					lsTransactionStatusMsg = MissingProfileInfoController.ACCOUNT_REQUEST_ALREADY_RAISED;
				}
				else
				{
					lsTransactionStatus = "passed";
					msAction = lsNextAction;
				}
				// notification end
			}// This condition renders account admin screen
			else if (null != lsNextAction && lsNextAction.equals(ACCT_ADMIN))
			{
				msAction = lsNextAction;
			}// This condition renders account admin identification screen
			else if (null != lsNextAction && lsNextAction.equals(ACCT_ADMIN_IDENTIFICATION))
			{
				msAction = lsNextAction;
			}// This condition renders create admin account screen
			else if (null != lsNextAction && lsNextAction.equals(CREAT_ADMIN_ACCT))
			{
				msAction = lsNextAction;
			}
			else if (null != lsNextAction && lsNextAction.equals(ORG_ACCOUNT_CREATED))
			{
				msAction = lsNextAction;
			}
			else if (null != lsNextAction && lsNextAction.equals(ORG_ACCT_REQUEST_SUBMITTED))
			{
				lsTransactionStatus = "failed";
				lsTransactionStatusMsg = MissingProfileInfoController.REQUEST_COULD_NOT_BE_COMPLETED;
				msAction = CREAT_ADMIN_ACCT;
				if (null != ApplicationSession.getAttribute(aoRequest, true, "workflowRequired"))
				{
					lbWorkFlowLaunch = (Boolean) ApplicationSession.getAttribute(aoRequest, true, "workflowRequired");
				}
				createOrganizationAccount(aoRequest, lsNextAction);
				lsTransactionStatus = "passed";
				if (lbWorkFlowLaunch)
				{
					// enhancement request 1816 - start
					if (null != ApplicationSession.getAttribute(aoRequest, true, "lsAdminEmail")
							&& null != ApplicationSession.getAttribute(aoRequest, true, "lsCeoEmail"))
					{
						lsAdminEmailId = (String) ApplicationSession.getAttribute(aoRequest, false, "lsAdminEmail");
						lsCeoEmailId = (String) ApplicationSession.getAttribute(aoRequest, false, "lsCeoEmail");
					}
					String lsLoginPagePagePath = aoRequest.getScheme()
							+ "://"
							+ aoRequest.getServerName()
							+ ":"
							+ aoRequest.getServerPort()
							+ aoRequest.getContextPath()
							+ ApplicationConstants.PORTAL_URL
							+ "&_pageLabel=portlet_hhsweb_portal_login_page&_nfls=false&logout=logout&app_menu_name=logout_icon"
							+ "&adminEmail=" + lsAdminEmailId + "&ceoEmail=" + lsCeoEmailId;
					lbSendRedirectFlag = true;
					aoResponse.sendRedirect(lsLoginPagePagePath);
				}
				// 1816 end
			}
			// This condition insert record in staff details and organization
			// and renders org account submitted screen
			else if (null != lsNextAction && lsNextAction.equals(HOME_PAGE))
			{
				String lsProviderHomePagePath = aoRequest.getScheme() + "://" + aoRequest.getServerName() + ":"
						+ aoRequest.getServerPort() + aoRequest.getContextPath() + ApplicationConstants.PORTAL_URL
						+ "&_pageLabel=portlet_hhsweb_portal_page_provider_home";
				try
				{
					lbSendRedirectFlag = true;
					aoRequest.getPortletSession().removeAttribute("MissinProfileHeader",
							PortletSession.APPLICATION_SCOPE);
					aoResponse.sendRedirect(lsProviderHomePagePath);
				}
				catch (IOException aoExp)
				{
					throw new ApplicationException("Not able to redirect on provider home page after validating user.",
							aoExp);
				}
				msAction = lsNextAction;
			}
			if (!lbSendRedirectFlag)
			{
				aoResponse.setRenderParameter("transactionStatus", lsTransactionStatus);
				aoResponse.setRenderParameter("transactionMessage", lsTransactionStatusMsg);

			}
		}
		catch (ApplicationException aoFbAppEx)
		{
			catchApplicationExc(aoResponse, lsTransactionStatusMsg, aoFbAppEx);
		}
		catch (Exception aoFbAppEx)
		{
			catchThrowable(aoResponse, lsTransactionStatusMsg, aoFbAppEx);
		}

		long loEndTimeTime = System.currentTimeMillis();
		try
		{
			LOG_OBJECT.Debug("TIME TAKEN for execution of action Method in MissingProfileInfoController = "
					+ (loEndTimeTime - loStartTime));
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Error while execution of action Method in MissingProfileInfoController ", aoEx);
		}
		UserThreadLocal.unSet();
	}

	/**
	 * This method catch Throwable class exception
	 * 
	 * @param aoResponse decides the next execution flow
	 * @param asTransactionStatusMsg message that needs to be displayed on
	 *            screen
	 * @param aoFbAppEx
	 */
	private void catchThrowable(ActionResponse aoResponse, String asTransactionStatusMsg, Throwable aoFbAppEx)
	{
		aoResponse.setRenderParameter("transactionStatus", "failed");
		aoResponse.setRenderParameter("transactionMessage", asTransactionStatusMsg);
		LOG_OBJECT.Error("Error occurred while inserting Missing profile details", aoFbAppEx);
	}

	/**
	 * This method catch Application class exception
	 * 
	 * @param aoResponse decides the next execution flow
	 * @param asTransactionStatusMsg message that needs to be displayed on
	 *            screen
	 * @param aoFbAppEx
	 */
	private void catchApplicationExc(ActionResponse aoResponse, String asTransactionStatusMsg,
			ApplicationException aoFbAppEx)
	{
		aoResponse.setRenderParameter("transactionStatus", "failed");
		aoResponse.setRenderParameter("transactionMessage", asTransactionStatusMsg);
		LOG_OBJECT.Error("Error occured while inserting Missing profile details ", aoFbAppEx);
	}

	/**
	 * This method searches EIN in EIN Master table and renders organization
	 * account created page or Account admin page
	 * 
	 * @param aoRequest to get screen parameters and next action to be performed
	 * @param asNext_action decides the next execution flow
	 * @param asTransactionStatus specifies whether transaction is passed or
	 *            fail
	 * @param asTransactionStatusMsg displays the message in accordance with
	 *            transaction's status
	 * @return loStatusMap map containing lsTransactionStatus and
	 *         lsTransactionStatusMsg
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private Map userEinSearch(ActionRequest aoRequest, String asNextAction, String asTransactionStatus,
			String asTransactionStatusMsg) throws ApplicationException
	{
		Channel loChannelObj;
		String lsOrgId;
		Map loStatusMap = new HashMap();
		msAction = EIN_TIN_SERACH;
		boolean lbWorkflowRequired = false;
		String lsEinNumber = (aoRequest.getParameter("orgEinTinNo"));
		ApplicationSession.setAttribute(lsEinNumber, aoRequest, "einNumber");
		loChannelObj = new Channel();
		OrganizationBean loOrganizationBean = new OrganizationBean();
		loOrganizationBean.setMsEinId(lsEinNumber);
		loChannelObj.setData("einNumber", loOrganizationBean.getMsEinId());
		TransactionManager.executeTransaction(loChannelObj, "einSearchInOrganization");
		loOrganizationBean = (OrganizationBean) loChannelObj.getData("aoEINResult");
		if (null != loOrganizationBean)
		{
			lsOrgId = loOrganizationBean.getMsOrgId();
			ApplicationSession.setAttribute(lsOrgId, aoRequest, "orgId");
			ApplicationSession.setAttribute(lsEinNumber, aoRequest, "einNumber");
			if (loOrganizationBean.getMsEinId().equalsIgnoreCase(lsEinNumber))
			{
				msAction = asNextAction;
				asTransactionStatus = "passed";
			}
		}
		else
		{
			loChannelObj = new Channel();
			EINBean loEINBean = new EINBean();
			loEINBean.setMsEINid(aoRequest.getParameter("orgEinTinNo"));
			loChannelObj.setData("einNumber", loEINBean.getMsEINid());
			TransactionManager.executeTransaction(loChannelObj, "EINSearchResults");
			asTransactionStatus = "passed";
			loEINBean = (EINBean) loChannelObj.getData("loEINResult");
			if (null != loEINBean)
			{
				if (loEINBean.getMsEINid().equalsIgnoreCase(lsEinNumber))
				{
					msAction = ACCT_ADMIN;
					asTransactionStatus = "passed";
					ApplicationSession.setAttribute(false, aoRequest, "workflowRequired");
				}
			}
			else
			{
				asTransactionStatus = "passed";
				lbWorkflowRequired = true;
				ApplicationSession.setAttribute(lbWorkflowRequired, aoRequest, "workflowRequired");
				msAction = ACCT_ADMIN;
			}
		}
		loStatusMap.put("TransactionStatus", asTransactionStatus);
		loStatusMap.put("TransactionStatusMsg", asTransactionStatusMsg);
		return loStatusMap;
	}

	/**
	 * This method renders home page if Terms and conditions are already checked
	 * else render EIN Search
	 * 
	 * @param aoRequest to get screen parameters and next action to be performed
	 * @param aoResponse decides the next execution flow
	 * @param aoChannelObj is the Channel object
	 * @param abSendRedirectFlag is the flag which determines whether or not
	 *            home page will be displayed
	 * @return abSendRedirectFlag is the flag which determines whether or not
	 *         home page will be displayed
	 * @throws ApplicationException
	 */
	private boolean rendersHomePage(ActionRequest aoRequest, ActionResponse aoResponse, Channel aoChannelObj,
			boolean lbSendRedirectFlag) throws ApplicationException
	{
		StaffDetails loStaffDetails;
		String lsNextHomePage = (String) ApplicationSession.getAttribute(aoRequest, true,
				MissingProfileInfoController.HOME_PAGE);
		if (lsNextHomePage.equalsIgnoreCase(MissingProfileInfoController.HOME_PAGE))
		{
			loStaffDetails = new StaffDetails();
			loStaffDetails.setMsTermConditionStatus("Yes");
			loStaffDetails.setMsModifiedBy((String) ApplicationSession.getAttribute(aoRequest, true, "modifyBy"));
			loStaffDetails.setMsUserDN((String) ApplicationSession.getAttribute(aoRequest, true, "userDN"));
			aoChannelObj.setData("aoStaffDetailsBean", loStaffDetails);
			TransactionManager.executeTransaction(aoChannelObj, "updateTCinStaffDetails");

			String lsProviderHomePagePath = "";
			if (null != aoRequest.getPortletSession().getAttribute("ShowAccountSwitchIcon",
					PortletSession.APPLICATION_SCOPE))
			{
				lsProviderHomePagePath = aoRequest.getScheme()
						+ "://"
						+ aoRequest.getServerName()
						+ ":"
						+ aoRequest.getServerPort()
						+ aoRequest.getContextPath()
						+ ApplicationConstants.PORTAL_URL
						+ "&_urlType=render&_pageLabel=portlet_hhsweb_portal_page_chooseOrganization&render_action=selectOrganization";
			}
			else
			{
				lsProviderHomePagePath = aoRequest.getScheme() + "://" + aoRequest.getServerName() + ":"
						+ aoRequest.getServerPort() + aoRequest.getContextPath() + ApplicationConstants.PORTAL_URL
						+ "&_pageLabel=portlet_hhsweb_portal_page_provider_home";
			}
			try
			{
				lbSendRedirectFlag = true;
				aoRequest.getPortletSession().removeAttribute("MissinProfileHeader", PortletSession.APPLICATION_SCOPE);
				aoResponse.sendRedirect(lsProviderHomePagePath);
			}
			catch (IOException aoIoExp)
			{
				throw new ApplicationException("Not able to redirect on provider home page after validating user.",
						aoIoExp);
			}
		}
		return lbSendRedirectFlag;
	}

	/**
	 * This method is to create organization account for non-profit and
	 * non-profit organization, entries made in staff details and organization
	 * table whether user profile details are present in LDAP or not
	 * 
	 * @param aoRequest to get screen parameters
	 * @param asNext_action decides the next execution flow
	 * @throws ApplicationException
	 */
	private void createOrganizationAccount(ActionRequest aoRequest, String asNextAction) throws ApplicationException
	{
		Channel loChannelObj;
		MissingNameBean loMissingNameBean;
		loChannelObj = new Channel();
		Integer liCurrentSeq = 0;
		loMissingNameBean = new MissingNameBean();
		String lsOrganizationId = "";
		String lsUserDN = "";
		String lsAdminEmailId = "";
		boolean lbWorkFlowLaunch = false;
		if (null != ApplicationSession.getAttribute(aoRequest, true, "workflowRequired"))
		{
			lbWorkFlowLaunch = (Boolean) ApplicationSession.getAttribute(aoRequest, true, "workflowRequired");
		}
		// get current sequence no. from organization
		TransactionManager.executeTransaction(loChannelObj, "getCurrentSequenceFromOrg");
		liCurrentSeq = (Integer) loChannelObj.getData("aiCurrentSeq");
		lsOrganizationId = "Org_" + (liCurrentSeq);
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_EMAIL_ID, PortletSession.APPLICATION_SCOPE);
		lsUserDN = ((String) ApplicationSession.getAttribute(aoRequest, true, "userDN"));
		lsAdminEmailId = ((String) ApplicationSession.getAttribute(aoRequest, true, "adminEmailId"));
		loMissingNameBean = populateMissingNameBean(aoRequest, loMissingNameBean);
		loMissingNameBean.setMsOrgId(lsOrganizationId);
		loMissingNameBean.setMsUserId(lsUserId);
		loMissingNameBean.setMsUserDN(lsUserDN);
		loMissingNameBean.setMsAdminEmailAdd(lsAdminEmailId);
		loMissingNameBean.setMsWorkFlowRequired(lbWorkFlowLaunch);
		String lsOrgLegalName = loMissingNameBean.getMsOrgLegalName();
		ApplicationSession.setAttribute(loMissingNameBean.getMsAdminEmailAdd(), aoRequest, "lsAdminEmail");
		ApplicationSession.setAttribute(loMissingNameBean.getMsCeoEmailAdd(), aoRequest, "lsCeoEmail");
		loChannelObj.setData("aoMissingNameBean", loMissingNameBean);
		setChannelObjectForWorkFlow(loChannelObj, aoRequest, lsOrganizationId, lsUserId, lsOrgLegalName);
		P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		HashMap<String,String> loMap = new HashMap<String, String>();
		if(loMissingNameBean.getMsOrgCorpStructure().equalsIgnoreCase(
				ApplicationConstants.ORG_CORPORATE_NON_PROFIT) && !loMissingNameBean.getMsWorkFlowRequired())
		{
			loMap.put("status", ApplicationConstants.STATUS_APPROVED);
		}
		loMap.put("providerID", loMissingNameBean.getMsUserId());
		loMap.put("newName", loMissingNameBean.getMsOrgLegalName());
		
		loChannelObj.setData("aoFilenetSession", loUserSession);
		loChannelObj.setData("asUserDN", lsUserDN);
		TransactionManager.executeTransaction(loChannelObj, "insertIntoStaffDetails");
		// This condition launch work-flow if organization is For Profit
		PortletSession loSession = aoRequest.getPortletSession(true);
		loSession.setAttribute(ApplicationConstants.KEY_SESSION_USER_ORG, lsOrganizationId,
				PortletSession.APPLICATION_SCOPE);
		loSession.setAttribute(ApplicationConstants.KEY_SESSION_ORG_NAME, loMissingNameBean.getMsOrgLegalName(),
				PortletSession.APPLICATION_SCOPE);
		lsUserDN = ((String) ApplicationSession.getAttribute(aoRequest, true, "userDN"));
		LOG_OBJECT.Info("UserDN is : " + lsUserDN);
		StaffDetails loStaffDetails = (StaffDetails) loChannelObj.getData("aoStaffDetailsBean");
		UserBean loUserBean = null;
		if (loSession.getAttribute("getUserRoles", PortletSession.APPLICATION_SCOPE) != null)
		{
			loUserBean = (UserBean) loSession.getAttribute("getUserRoles", PortletSession.APPLICATION_SCOPE);
			loUserBean.setMsRole(ApplicationConstants.ROLE_ADMINISTRATOR_PROV_MANAGER);
			loUserBean.setMsUserId(loStaffDetails.getMsStaffId());
		}
		else
		{
			loUserBean = new UserBean();
			loUserBean.setMsRole(ApplicationConstants.ROLE_ADMINISTRATOR_PROV_MANAGER);
			loUserBean.setMsUserId(loStaffDetails.getMsStaffId());
		}
		aoRequest.getPortletSession().setAttribute(ApplicationConstants.KEY_SESSION_USER_ROLE, loUserBean.getMsRole(),
				PortletSession.APPLICATION_SCOPE);
		loSession.setAttribute("getUserRoles", loUserBean, PortletSession.APPLICATION_SCOPE);
		aoRequest.getPortletSession().setAttribute(ApplicationConstants.KEY_SESSION_USER_ID, loUserBean.getMsUserId(),
				PortletSession.APPLICATION_SCOPE);
		UserThreadLocal.setUser(loUserBean.getMsUserId());
		// User Roles Added
		if (loMissingNameBean.getMsOrgCorpStructure().equalsIgnoreCase(ApplicationConstants.ORG_CORPORATE_FOR_PROFIT))
		{
			String lsStatusFromFileNet = (String) loChannelObj
					.getData(MissingProfileInfoController.RETURN_ORG_ACCT_CREATE_NUMBER);
			if (null == lsStatusFromFileNet || "".equalsIgnoreCase(lsStatusFromFileNet)
					|| lsStatusFromFileNet.length() != 32)
			{
				aoRequest.setAttribute(MissingProfileInfoController.WORKFLOWFAIL,
						MissingProfileInfoController.WORKFLOWFAIL);
			}
			msAction = asNextAction;
		}
		else if (loMissingNameBean.getMsOrgCorpStructure().equalsIgnoreCase(
				ApplicationConstants.ORG_CORPORATE_NON_PROFIT))
		{
			if (lbWorkFlowLaunch)
			{
				String lsStatusFileNet = (String) loChannelObj
						.getData(MissingProfileInfoController.RETURN_ORG_ACCT_CREATE_NUMBER);
				if (null == lsStatusFileNet || "".equalsIgnoreCase(lsStatusFileNet) || lsStatusFileNet.length() != 32)
				{
					aoRequest.setAttribute(MissingProfileInfoController.WORKFLOWFAIL,
							MissingProfileInfoController.WORKFLOWFAIL);
				}
				msAction = asNextAction;
			}
			else
			{
				msAction = MissingProfileInfoController.ORG_ACCOUNT_CREATED;
			}
		}
	}

	/**
	 * This method is to update user profile details in staff details and LDAP
	 * depending upon whether user profile details are present in LDAP or not
	 * 
	 * @param aoRequest to get screen parameters
	 * @param aoChannelObj channel object required to execute transaction
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private void updateUserProfile(ActionRequest aoRequest, Channel aoChannelObj) throws ApplicationException
	{
		String lsAdminFirstName = aoRequest.getParameter("firstName");
		String lsAdminMiddleName = (aoRequest.getParameter("middleName"));
		String lsAdminLastName = (aoRequest.getParameter("lastName"));
		ApplicationSession.setAttribute(lsAdminFirstName, aoRequest, "adminFirstName");
		ApplicationSession.setAttribute(lsAdminMiddleName, aoRequest, "adminMiddleName");
		ApplicationSession.setAttribute(lsAdminLastName, aoRequest, "adminLastName");
		String lsModifyBy = ((String) ApplicationSession.getAttribute(aoRequest, true, "modifyBy"));
		// Based on LDAP flag ... if FirstName,LastName is missing in LDAP THEN
		// update LDAP and Session update
		String lbRecordFoundInLdap = (String) ApplicationSession.getAttribute(aoRequest, true, "userLdapRecord");
		if (!lbRecordFoundInLdap.equalsIgnoreCase(MissingProfileInfoController.FOUND))
		{
			// USER PROFILE UPDATE IN LDAP
			Map loUserUpdateMap = new HashMap();
			loUserUpdateMap.put("FirstName", lsAdminFirstName);
			loUserUpdateMap.put("Initials", lsAdminMiddleName);
			loUserUpdateMap.put("LastName", lsAdminLastName);
			loUserUpdateMap.put("UserDN", (String) ApplicationSession.getAttribute(aoRequest, true, "userDN"));
			aoChannelObj.setData("aoUserDetailsMap", loUserUpdateMap);
			TransactionManager.executeTransaction(aoChannelObj, "userDetailsUPdate");
			aoRequest.getPortletSession().setAttribute(ApplicationConstants.KEY_SESSION_USER_NAME,
					lsAdminFirstName + " " + lsAdminMiddleName + " " + lsAdminLastName,
					PortletSession.APPLICATION_SCOPE);
		}
		else
		{
			// if FirstName and LastName missing in DB then update LDAP and DB
			// both
			// USER PROFILE UPDATE IN DB & LDAP
			Map loUserDetailsMap = new HashMap();
			loUserDetailsMap.put("FirstName",
					(String) ApplicationSession.getAttribute(aoRequest, true, "adminFirstName"));
			loUserDetailsMap.put("Initials",
					(String) ApplicationSession.getAttribute(aoRequest, true, "adminMiddleName"));
			loUserDetailsMap
					.put("LastName", (String) ApplicationSession.getAttribute(aoRequest, true, "adminLastName"));
			loUserDetailsMap.put("UserDN", (String) ApplicationSession.getAttribute(aoRequest, true, "userDN"));
			loUserDetailsMap.put("ModifiedBy", lsModifyBy);
			aoChannelObj.setData("aoUserDetailsMap", loUserDetailsMap);
			TransactionManager.executeTransaction(aoChannelObj, "updateUserProfileinStaffDetails");
			aoRequest.getPortletSession().setAttribute(ApplicationConstants.KEY_SESSION_USER_NAME,
					lsAdminFirstName + " " + lsAdminMiddleName + " " + lsAdminLastName,
					PortletSession.APPLICATION_SCOPE);
		}
	}

	/**
	 * This method is to send notification an alert while requesting access to
	 * their account (organization account)
	 * 
	 * @param aoRequest channel list containing all entries(User and Members)
	 *            that are made while creating organization account
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private String sendRequestForOrgAccount(ActionRequest aoRequest) throws ApplicationException
	{
		Channel loChannelObj;
		StaffDetails loStaffDetails;
		String lsOrgId;
		String lsUserName = (String) ApplicationSession.getAttribute(aoRequest, true, "userFirstName") + " "
				+ (String) ApplicationSession.getAttribute(aoRequest, true, "userMiddleName") + " "
				+ (String) ApplicationSession.getAttribute(aoRequest, true, "userLastName");
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_EMAIL_ID, PortletSession.APPLICATION_SCOPE);
		String lsSharedBy = (String) ApplicationSession.getAttribute(aoRequest, true, "adminNycId");
		ApplicationSession.setAttribute(lsSharedBy, aoRequest, "loEmailAddress");
		String lsTransactionStatusMsg = "";
		// insert into staff details for admin
		loChannelObj = new Channel();
		loStaffDetails = new StaffDetails();
		loStaffDetails.setMsUserStatus("Pending");
		loStaffDetails.setMsMemberStatus("Pending");
		loStaffDetails.setMsStaffFirstName((String) ApplicationSession.getAttribute(aoRequest, true, "userFirstName"));
		loStaffDetails.setMsStaffLastName((String) ApplicationSession.getAttribute(aoRequest, true, "userLastName"));
		loStaffDetails
				.setMsStaffMidInitial((String) ApplicationSession.getAttribute(aoRequest, true, "userMiddleName"));
		loStaffDetails.setMsTermConditionStatus("Yes");
		loStaffDetails.setMsStaffActiveFlag("No");
		loStaffDetails.setMsStaffPhone("");
		loStaffDetails.setMsStaffTitle("");
		loStaffDetails.setMsNYCUserId((String) ApplicationSession.getAttribute(aoRequest, true, "adminEmailId"));
		loStaffDetails.setMsPermissionLevel("");
		loStaffDetails.setMsAdminPermission("");
		lsOrgId = (String) ApplicationSession.getAttribute(aoRequest, true, "orgId");
		loStaffDetails.setMsOrgId(lsOrgId);
		loStaffDetails.setMsSystemUser("Yes");
		loStaffDetails.setMsUserDN((String) ApplicationSession.getAttribute(aoRequest, true, "userDN"));
		loStaffDetails.setMsStaffEmail((String) ApplicationSession.getAttribute(aoRequest, true, "adminEmailId"));
		loStaffDetails.setMsUserAcctCreationDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));

		loChannelObj.setData("aoStaffDetails", loStaffDetails);
		TransactionManager.executeTransaction(loChannelObj, "insertIntoStaffDetail");
		Integer liInsertedStaffId = (Integer) loChannelObj.getData("aiInsertRowCount");
		if (null != liInsertedStaffId && liInsertedStaffId > 0)
		{
			// notification start
			loChannelObj = new Channel();
			List<String> loProviderList = new ArrayList<String>();
			loProviderList.add(lsOrgId);

			HashMap<String, Object> loHmNotifyParam = new HashMap<String, Object>();
			List<String> loNotificationAlertList = new ArrayList<String>();
			loNotificationAlertList.add("AL019");
			loNotificationAlertList.add("NT019");
			NotificationDataBean loNotificationDataBean = new NotificationDataBean();
			HashMap<String, String> loLinkMap = new HashMap<String, String>();
			loNotificationDataBean.setProviderList(loProviderList);

			loHmNotifyParam.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);

			loHmNotifyParam.put(HHSConstants.CREATED_BY_USER_ID, String.valueOf(liInsertedStaffId.intValue()));
			loHmNotifyParam.put(HHSConstants.MODIFIED_BY, String.valueOf(liInsertedStaffId.intValue()));
			loHmNotifyParam.put(ApplicationConstants.ENTITY_ID, lsUserId);
			loHmNotifyParam.put(ApplicationConstants.ENTITY_TYPE, ApplicationConstants.ATTRIBUTE_GET_STAFF_ID);
			HashMap<Object, String> loParamMap = new HashMap<Object, String>();
			loParamMap.put("NAME", lsUserName); // name replace ..
			String lsURLNotificationBasicForm = aoRequest.getScheme()
					+ "://"
					+ aoRequest.getServerName()
					+ ":"
					+ aoRequest.getServerPort()
					+ aoRequest.getContextPath()
					+ ApplicationConstants.PORTAL_URL
					+ "&_pageLabel=hhsweb_page_manage_users&action=manageMembers&_nfls=false&app_menu_name=header_organization_information"
					+ "&section=basics&subsection=memberandusers&next_action=displayOrgMember&fb_formName=OrgProfile";
			loLinkMap.put("LINK", lsURLNotificationBasicForm);
			loNotificationDataBean.setLinkMap(loLinkMap);
			loNotificationDataBean.setAgencyLinkMap(loLinkMap);
			loHmNotifyParam.put("AL019", loNotificationDataBean);
			loHmNotifyParam.put("NT019", loNotificationDataBean);
			loHmNotifyParam.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loParamMap);
			loChannelObj.setData("loHmNotifyParam", loHmNotifyParam);
			TransactionManager.executeTransaction(loChannelObj, "insertNotificationDetail");
			lsTransactionStatusMsg = "passed";
		}
		else
		{
			lsTransactionStatusMsg = "failed";
		}
		return lsTransactionStatusMsg;
	}

	/**
	 * This method is to populate missing name bean for read only screen that is
	 * visible for city user while creating for-profit organization account
	 * 
	 * @param aoList channel list containing all entries(User and Members) that
	 *            are made while creating organization account
	 * @return loMissingNameBean read only screen for city manager approval
	 * @throws ApplicationException
	 */
	private MissingNameBean populateMissingBeanForNycUserId(List<OrgStaffDetailBean> aoList)
			throws ApplicationException
	{
		MissingNameBean loMissingNameBean = new MissingNameBean();
		OrgStaffDetailBean loOrgStaffDetailBean;
		Iterator<OrgStaffDetailBean> loListLtr = aoList.iterator();
		int liCount = 0;
		while (loListLtr.hasNext())
		{
			loOrgStaffDetailBean = (OrgStaffDetailBean) loListLtr.next();
			if (liCount == 0)
			{
				loMissingNameBean.setMsOrgAcctPeriodEnd(loOrgStaffDetailBean.getMsOrgAcctPeriodEnd());
				loMissingNameBean.setMsOrgAcctPeriodStart(loOrgStaffDetailBean.getMsOrgAcctPeriodStart());
				loMissingNameBean.setMsOrgCorpStructure(loOrgStaffDetailBean.getMsOrgCorpStructure());
				loMissingNameBean.setMsOrgDoingBusAs(loOrgStaffDetailBean.getMsOrgDoingBusAs());
				loMissingNameBean.setMsOrgDunsNumber(loOrgStaffDetailBean.getMsOrgDunsNumber());
				loMissingNameBean.setMsOrgEinTinNumber(loOrgStaffDetailBean.getMsOrgEinTinNumber());
				loMissingNameBean.setMsOrgEntityType(loOrgStaffDetailBean.getMsOrgEntityType());
				loMissingNameBean.setMsOrgEntityTypeOther(loOrgStaffDetailBean.getMsOrgEntityTypeOther());
				loMissingNameBean.setMsOrgLegalName(loOrgStaffDetailBean.getMsOrgLegalName());
				loMissingNameBean.setMsExecAddrLine1(loOrgStaffDetailBean.getMsExecAddrLine1());
				loMissingNameBean.setMsExecAddrLine2(loOrgStaffDetailBean.getMsExecAddrLine2());
				loMissingNameBean.setMsExecCity(loOrgStaffDetailBean.getMsExecCity());
				loMissingNameBean.setMsExecFaxNo(loOrgStaffDetailBean.getMsExecFaxNo());
				loMissingNameBean.setMsExecPhoneNo(loOrgStaffDetailBean.getMsExecPhoneNo());
				loMissingNameBean.setMsExecState(loOrgStaffDetailBean.getMsExecState());
				loMissingNameBean.setMsExecWebSite(loOrgStaffDetailBean.getMsExecWebSite());
				loMissingNameBean.setMsExecZipCode(loOrgStaffDetailBean.getMsExecZipCode());
			}
			if (loOrgStaffDetailBean.getMsTitle().equalsIgnoreCase("1"))
			{ // ceo
				if (loOrgStaffDetailBean.getMsAdminUserDN() != null
						&& !loOrgStaffDetailBean.getMsAdminUserDN().trim().equalsIgnoreCase(""))
				{ // admin 4
					loMissingNameBean.setMsAdminEmailAdd(loOrgStaffDetailBean.getMsEmailAdd());
					loMissingNameBean.setMsAdminFirstName(loOrgStaffDetailBean.getMsFirstName());
					loMissingNameBean.setMsAdminLastName(loOrgStaffDetailBean.getMsLastName());
					loMissingNameBean.setMsAdminMiddleInitial(loOrgStaffDetailBean.getMsMiddleInitial());
					loMissingNameBean.setMsAdminNYCId(loOrgStaffDetailBean.getMsAdminNYCId());
					loMissingNameBean.setMsAdminOfficeTitle(loOrgStaffDetailBean.getMsTitle());
					loMissingNameBean.setMsAdminPhoneNo(loOrgStaffDetailBean.getMsPhoneNo());
				}
				else
				{
					loMissingNameBean.setMsCeoEmailAdd(loOrgStaffDetailBean.getMsEmailAdd());
					loMissingNameBean.setMsCeoFirstName(loOrgStaffDetailBean.getMsFirstName());
					loMissingNameBean.setMsCeoLastName(loOrgStaffDetailBean.getMsLastName());
					loMissingNameBean.setMsCeoMiddleInitial(loOrgStaffDetailBean.getMsMiddleInitial());
					loMissingNameBean.setMsCeoPhoneNo(loOrgStaffDetailBean.getMsPhoneNo());
				}
			}
			else if (loOrgStaffDetailBean.getMsTitle().equalsIgnoreCase("2"))
			{ // cfo
				if (loOrgStaffDetailBean.getMsAdminUserDN() != null
						&& !loOrgStaffDetailBean.getMsAdminUserDN().trim().equalsIgnoreCase(""))
				{ // admin 4
					loMissingNameBean.setMsAdminEmailAdd(loOrgStaffDetailBean.getMsEmailAdd());
					loMissingNameBean.setMsAdminFirstName(loOrgStaffDetailBean.getMsFirstName());
					loMissingNameBean.setMsAdminLastName(loOrgStaffDetailBean.getMsLastName());
					loMissingNameBean.setMsAdminMiddleInitial(loOrgStaffDetailBean.getMsMiddleInitial());
					loMissingNameBean.setMsAdminNYCId(loOrgStaffDetailBean.getMsAdminNYCId());
					loMissingNameBean.setMsAdminOfficeTitle(loOrgStaffDetailBean.getMsTitle());
					loMissingNameBean.setMsAdminPhoneNo(loOrgStaffDetailBean.getMsPhoneNo());
				}
				else
				{
					loMissingNameBean.setMsCfoEmailAdd(loOrgStaffDetailBean.getMsEmailAdd());
					loMissingNameBean.setMsCfoFirstName(loOrgStaffDetailBean.getMsFirstName());
					loMissingNameBean.setMsCfoLastName(loOrgStaffDetailBean.getMsLastName());
					loMissingNameBean.setMsCfoMiddleInitial(loOrgStaffDetailBean.getMsMiddleInitial());
					loMissingNameBean.setMsCfoPhoneNo(loOrgStaffDetailBean.getMsPhoneNo());
				}
			}
			else if (loOrgStaffDetailBean.getMsTitle().equalsIgnoreCase("6"))
			{ // pres 3
				if (loOrgStaffDetailBean.getMsAdminUserDN() != null
						&& !loOrgStaffDetailBean.getMsAdminUserDN().trim().equalsIgnoreCase(""))
				{ // admin 4
					loMissingNameBean.setMsAdminEmailAdd(loOrgStaffDetailBean.getMsEmailAdd());
					loMissingNameBean.setMsAdminFirstName(loOrgStaffDetailBean.getMsFirstName());
					loMissingNameBean.setMsAdminLastName(loOrgStaffDetailBean.getMsLastName());
					loMissingNameBean.setMsAdminMiddleInitial(loOrgStaffDetailBean.getMsMiddleInitial());
					loMissingNameBean.setMsAdminNYCId(loOrgStaffDetailBean.getMsAdminNYCId());
					loMissingNameBean.setMsAdminOfficeTitle(loOrgStaffDetailBean.getMsTitle());
					loMissingNameBean.setMsAdminPhoneNo(loOrgStaffDetailBean.getMsPhoneNo());
				}
				else
				{
					loMissingNameBean.setMsPresEmailAdd(loOrgStaffDetailBean.getMsEmailAdd());
					loMissingNameBean.setMsPresFirstName(loOrgStaffDetailBean.getMsFirstName());
					loMissingNameBean.setMsPresLastName(loOrgStaffDetailBean.getMsLastName());
					loMissingNameBean.setMsPresMiddleInitial(loOrgStaffDetailBean.getMsMiddleInitial());
					loMissingNameBean.setMsPresPhoneNo(loOrgStaffDetailBean.getMsPhoneNo());
				}
			}
			else if (loOrgStaffDetailBean.getMsTitle().equalsIgnoreCase("4")
					|| loOrgStaffDetailBean.getMsTitle().equalsIgnoreCase("5")
					|| loOrgStaffDetailBean.getMsTitle().equalsIgnoreCase("3"))
			{ // admin 4
				loMissingNameBean.setMsAdminEmailAdd(loOrgStaffDetailBean.getMsEmailAdd());
				loMissingNameBean.setMsAdminFirstName(loOrgStaffDetailBean.getMsFirstName());
				loMissingNameBean.setMsAdminLastName(loOrgStaffDetailBean.getMsLastName());
				loMissingNameBean.setMsAdminMiddleInitial(loOrgStaffDetailBean.getMsMiddleInitial());
				loMissingNameBean.setMsAdminNYCId(loOrgStaffDetailBean.getMsAdminNYCId());
				loMissingNameBean.setMsAdminOfficeTitle(loOrgStaffDetailBean.getMsTitle());
				loMissingNameBean.setMsAdminPhoneNo(loOrgStaffDetailBean.getMsPhoneNo());
			}
			liCount++;
		}
		msAction = "readonly";
		return loMissingNameBean;
	}

	/**
	 * This method is to set channel object for FileNet Workflow
	 * 
	 * @param aoChannelObj channel object required to execute transaction
	 * @param aoRequest to get portlet session
	 * @param asOrganizationId organization id to launch work-flow
	 * @param asUserEmailId work-flow launch by user email id
	 * @throws ApplicationException
	 */
	private void setChannelObjectForWorkFlow(Channel aoChannelObj, ActionRequest aoRequest, String asOrganizationId,
			String asUserEmailId, String asOrgLegalName) throws ApplicationException
	{

		Map<String, Object> loOrgAccountFileNetMap = new HashMap<String, Object>();

		loOrgAccountFileNetMap.put(P8Constants.PROPERTY_PE_APPLICTION_ID, asOrganizationId);
		loOrgAccountFileNetMap.put(P8Constants.PROPERTY_PE_PROVIDER_ID, asOrganizationId);
		loOrgAccountFileNetMap.put(P8Constants.PROPERTY_PE_PROVIDER_NAME, asOrgLegalName);
		loOrgAccountFileNetMap.put(P8Constants.PROPERTY_PE_TASK_NAME, "Provider Account Request");
		loOrgAccountFileNetMap.put(P8Constants.PROPERTY_PE_TASK_MODIFIED_DATE, new Date());
		loOrgAccountFileNetMap.put(P8Constants.PROPERTY_PE_LAUNCH_BY, asUserEmailId);
		loOrgAccountFileNetMap.put(P8Constants.PROPERTY_PE_LAUNCH_DATE, new Date());

		aoChannelObj.setData(MissingProfileInfoController.ORG_ACCT_REQ_FILE_NET, loOrgAccountFileNetMap);
		aoChannelObj.setData(MissingProfileInfoController.WF_NAME, P8Constants.PROPERTY_ORG_ACC_REQ_WORKFLOW_NAME);

		PortletSession loSession = aoRequest.getPortletSession();
		P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		aoChannelObj.setData("aoFilenetSession", loUserSession);

	}

	/**
	 * This method is to get a map containing member and title mapping
	 * 
	 * @return loMemberTitle map containing member and title mapping
	 */
	private Map<String, String> getAdminTitle()
	{
		Channel loChannel = new Channel();
		try
		{
			TransactionManager.executeTransaction(loChannel, "getMemberTitles");
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("exception while fetching getMemberInfoTitle", aoExp);
		}
		return (Map<String, String>) loChannel.getData("getMemberTitles");
	}

	/**
	 * This method is to populate missing name bean for organization account
	 * creation
	 * 
	 * @param aoRequest to get screen parameters
	 * @param aoMissingNameBean to get screen parameters into bean
	 * @return aoMissingNameBean missing name bean containing all screen
	 *         parameters required to create organization account
	 */
	private MissingNameBean populateMissingNameBean(ActionRequest aoRequest, MissingNameBean aoMissingNameBean)
	{
		aoMissingNameBean.setMsOrgAcctPeriodEnd(aoRequest.getParameter("acctPeriodTo"));
		aoMissingNameBean.setMsOrgAcctPeriodStart(aoRequest.getParameter("acctPeriodFrom"));
		aoMissingNameBean.setMsOrgCorpStructure(aoRequest.getParameter("orgCorpStruc"));
		aoMissingNameBean.setMsOrgDoingBusAs(aoRequest.getParameter("orgAltName"));
		aoMissingNameBean.setMsOrgDunsNumber(aoRequest.getParameter("orgDunNo"));
		aoMissingNameBean.setMsOrgEinTinNumber(aoRequest.getParameter("orgEinTinNo"));
		aoMissingNameBean.setMsOrgEntityType(aoRequest.getParameter("entityType"));
		aoMissingNameBean.setMsOrgEntityTypeOther(aoRequest.getParameter("others"));
		aoMissingNameBean.setMsOrgLegalName(aoRequest.getParameter("orgLegalName").trim());
		aoMissingNameBean.setMsExecAddrLine1(aoRequest.getParameter("execAddLine1").trim());
		aoMissingNameBean.setMsExecAddrLine2(aoRequest.getParameter("execAddLine2"));
		aoMissingNameBean.setMsExecCity(aoRequest.getParameter("execCity").trim());
		aoMissingNameBean.setMsExecFaxNo(aoRequest.getParameter("execFaxNo"));
		aoMissingNameBean.setMsExecPhoneNo(aoRequest.getParameter("execPhNo"));
		aoMissingNameBean.setMsExecState(aoRequest.getParameter("execState"));
		aoMissingNameBean.setMsExecWebSite(aoRequest.getParameter("execWebSite"));
		aoMissingNameBean.setMsExecZipCode(aoRequest.getParameter("execZipCode"));
		aoMissingNameBean.setMsAdminEmailAdd((String) aoRequest.getParameter("adminEmail"));
		aoMissingNameBean.setMsAdminFirstName(aoRequest.getParameter("adminFirstName"));
		aoMissingNameBean.setMsAdminLastName(aoRequest.getParameter("adminLastName"));
		aoMissingNameBean.setMsAdminMiddleInitial(aoRequest.getParameter("adminMiddleName"));
		aoMissingNameBean.setMsAdminNYCId(aoRequest.getParameter("nycId"));
		aoMissingNameBean.setMsAdminOfficeTitle(aoRequest.getParameter("adminOfficeTitle"));
		aoMissingNameBean.setMsAdminPhoneNo(aoRequest.getParameter("adminPhNo"));
		aoMissingNameBean.setMsCeoEmailAdd(aoRequest.getParameter("ceoEmail"));
		aoMissingNameBean.setMsCeoFirstName(aoRequest.getParameter("ceoFirstName").trim());
		aoMissingNameBean.setMsCeoLastName(aoRequest.getParameter("ceoLastName").trim());
		aoMissingNameBean.setMsCeoMiddleInitial(aoRequest.getParameter("ceoMiddleName").trim());
		aoMissingNameBean.setMsCeoPhoneNo(aoRequest.getParameter("ceoPhNo"));
		aoMissingNameBean.setMsPresEmailAdd(aoRequest.getParameter("presEmail").trim());
		aoMissingNameBean.setMsPresFirstName(aoRequest.getParameter("presFirstName").trim());
		aoMissingNameBean.setMsPresLastName(aoRequest.getParameter("presLastName").trim());
		aoMissingNameBean.setMsPresMiddleInitial(aoRequest.getParameter("presMiddleName").trim());
		aoMissingNameBean.setMsPresPhoneNo(aoRequest.getParameter("presPhNo"));
		aoMissingNameBean.setMsCfoEmailAdd(aoRequest.getParameter("cfoEmail").trim());
		aoMissingNameBean.setMsCfoFirstName(aoRequest.getParameter("cfoFirstName").trim());
		aoMissingNameBean.setMsCfoLastName(aoRequest.getParameter("cfoLastName").trim());
		aoMissingNameBean.setMsCfoMiddleInitial(aoRequest.getParameter("cfoMiddleName").trim());
		aoMissingNameBean.setMsCfoPhoneNo(aoRequest.getParameter("cfoPhNo"));
		aoMissingNameBean.setMsRequestId(aoRequest.getParameter("orgEinTinNo"));
		aoMissingNameBean.setStatusDescriptionText(isNull(aoRequest.getParameter("StatusDescriptionTextReg")));
		aoMissingNameBean.setStatusReason(isNull(aoRequest.getParameter("StatusReasonReg")));
		aoMissingNameBean.setMsNormHouseNumber(isNull(aoRequest.getParameter("NormHouseNumberReg")));
		aoMissingNameBean.setStreetNumberText(isNull(aoRequest.getParameter("StreetNumberTextReg")));
		aoMissingNameBean.setCongressionalDistrictName(isNull(aoRequest.getParameter("CongressionalDistrictNameReg")));
		aoMissingNameBean.setLatitude(isNull(aoRequest.getParameter("LatitudeReg")));
		aoMissingNameBean.setLongitude(isNull(aoRequest.getParameter("LongitudeReg")));
		aoMissingNameBean.setMsValXCoordinate(isNull(aoRequest.getParameter("XCoordinateReg")));
		aoMissingNameBean.setMsValYCoordinate(isNull(aoRequest.getParameter("YCoordinateReg")));
		aoMissingNameBean.setMsCommDistt(isNull(aoRequest.getParameter("CommunityDistrictReg")));
		aoMissingNameBean.setMsCivilCourtDistt(isNull(aoRequest.getParameter("CivilCourtDistrictReg")));
		aoMissingNameBean.setMsHealthArea(isNull(aoRequest.getParameter("HealthAreaReg")));
		aoMissingNameBean.setMsBuildIdNumber(isNull(aoRequest.getParameter("BuildingIdNumberReg")));
		aoMissingNameBean.setMsTaxBlock(isNull(aoRequest.getParameter("TaxBlockReg")));
		aoMissingNameBean.setMsTaxLot(isNull(aoRequest.getParameter("TaxLotReg")));
		aoMissingNameBean.setMsSenatorDistt(isNull(aoRequest.getParameter("SenatorialDistrictReg")));
		aoMissingNameBean.setMsAssemblyDistt(isNull(aoRequest.getParameter("AssemblyDistrictReg")));
		aoMissingNameBean.setMsCouncilDistt(isNull(aoRequest.getParameter("CouncilDistrictReg")));
		aoMissingNameBean.setMsLowEndCrossStreetNo(isNull(aoRequest.getParameter("LowEndStreetNumberReg")));
		aoMissingNameBean.setMsHighEndCrossStreetNo(isNull(aoRequest.getParameter("HighEndStreetNumberReg")));
		aoMissingNameBean.setMsLowEndCrossStreetName(isNull(aoRequest.getParameter("LowEndStreetNameReg")));
		aoMissingNameBean.setMsHighEndCrossStreetName(isNull(aoRequest.getParameter("HighEndStreetNameReg")));
		aoMissingNameBean.setMsValCity(aoRequest.getParameter("execCity").trim());
		aoMissingNameBean.setMsValState(aoRequest.getParameter("execState"));
		aoMissingNameBean.setMsValZipCode(aoRequest.getParameter("execZipCode"));
		aoMissingNameBean.setMsValBorough(isNull(aoRequest.getParameter("NYCBoroughReg")));
		aoMissingNameBean.setMsSchoolDisttName(aoRequest.getParameter("SchoolDistrictNameReg"));
		return aoMissingNameBean;
	}

	/**
	 * This method is to get a map containing all the states
	 * 
	 * @return loMemberTitle map containing all the states
	 */

	private Map<String, String> getState()
	{
		Map<String, String> loMemberTitle = new LinkedHashMap<String, String>();
		loMemberTitle.put("AK", "AK");
		loMemberTitle.put("AL", "AL");
		loMemberTitle.put("AR", "AR");
		loMemberTitle.put("AS", "AS");
		loMemberTitle.put("AZ", "AZ");
		loMemberTitle.put("CA", "CA");
		loMemberTitle.put("CO", "CO");
		loMemberTitle.put("CT", "CT");
		loMemberTitle.put("DC", "DC");
		loMemberTitle.put("DE", "DE");
		loMemberTitle.put("FL", "FL");
		loMemberTitle.put("GA", "GA");
		loMemberTitle.put("GU", "GU");
		loMemberTitle.put("HI", "HI");
		loMemberTitle.put("IA", "IA");
		loMemberTitle.put("ID", "ID");
		loMemberTitle.put("IL", "IL");
		loMemberTitle.put("IN", "IN");
		loMemberTitle.put("KS", "KS");
		loMemberTitle.put("KY", "KY");
		loMemberTitle.put("LA", "LA");
		loMemberTitle.put("MA", "MA");
		loMemberTitle.put("MD", "MD");
		loMemberTitle.put("ME", "ME");
		loMemberTitle.put("MI", "MI");
		loMemberTitle.put("MN", "MN");
		loMemberTitle.put("MO", "MO");
		loMemberTitle.put("MP", "MP");
		loMemberTitle.put("MS", "MS");
		loMemberTitle.put("MT", "MT");
		loMemberTitle.put("NC", "NC");
		loMemberTitle.put("ND", "ND");
		loMemberTitle.put("NE", "NE");
		loMemberTitle.put("NH", "NH");
		loMemberTitle.put("NJ", "NJ");
		loMemberTitle.put("NM", "NM");
		loMemberTitle.put("NV", "NV");
		loMemberTitle.put("NY", "NY");
		loMemberTitle.put("OH", "OH");
		loMemberTitle.put("OK", "OK");
		loMemberTitle.put("OR", "OR");
		loMemberTitle.put("PA", "PA");
		loMemberTitle.put("PR", "PR");
		loMemberTitle.put("RI", "RI");
		loMemberTitle.put("SC", "SC");
		loMemberTitle.put("SD", "SD");
		loMemberTitle.put("TN", "TN");
		loMemberTitle.put("TX", "TX");
		loMemberTitle.put("UT", "UT");
		loMemberTitle.put("VA", "VA");
		loMemberTitle.put("VI", "VI");
		loMemberTitle.put("VT", "VT");
		loMemberTitle.put("WA", "WA");
		loMemberTitle.put("WI", "WI");
		loMemberTitle.put("WV", "WV");
		loMemberTitle.put("WY", "WY");
		return loMemberTitle;
	}

	/**
	 * This method is to get application terms and conditions from file net
	 * 
	 * @param aoRequest to get portlet session
	 * @param asChannelInput containing Application Terms & Conditions
	 * @throws IOException
	 */

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private String getTermsAndCondition(RenderRequest aoRequest, String asChannelInput)
	{
		Channel loChannel = new Channel();
		P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);

		loChannel.setData("aoUserSession", loUserSession);
		loChannel.setData("asDocType", asChannelInput);
		String lsSystemTermsAndCond = "";
		try
		{
			TransactionManager.executeTransaction(loChannel, "getDocumentContentByType");
			Map<String, InputStream> loInputStreamMap = (Map) loChannel.getData("contentByType");
			InputStream loInputStream = null;
			for (Map.Entry<String, InputStream> loEntry : loInputStreamMap.entrySet())
			{

				loInputStream = (InputStream) loEntry.getValue();
			}
			if (loInputStream != null)
			{

				Writer loWriter = new StringWriter();
				char[] loCharBuffer = new char[1024];
				Reader loReader = null;
				try
				{

					loReader = new BufferedReader(new InputStreamReader(loInputStream));
					if (loReader.ready())
					{
						int liTempVar;
						while ((liTempVar = loReader.read(loCharBuffer)) != -1)
						{
							loWriter.write(loCharBuffer, 0, liTempVar);
						}
					}
					lsSystemTermsAndCond = loWriter.toString();
				}
				catch (IOException aoExp)
				{
					LOG_OBJECT.Error("Error occured while getting terms and conditions", aoExp);
					aoRequest.setAttribute("transactionStatus", "failed");
					aoRequest.setAttribute("transactionMessage", REQUEST_COULD_NOT_BE_COMPLETED);
				}
				finally
				{
					try
					{
						loInputStream.close();
						if (loReader != null)
						{
							loReader.close();
						}
						if (loWriter != null)
						{
							loWriter.close();
						}
					}
					catch (IOException aoIoExp)
					{
						LOG_OBJECT.Error("Error occured while getting terms and conditions", aoIoExp);
						aoRequest.setAttribute("transactionStatus", "failed");
						aoRequest.setAttribute("transactionMessage", REQUEST_COULD_NOT_BE_COMPLETED);
					}

				}
			}
		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Error occured while getting terms and conditions", aoAppExp);
			aoRequest.setAttribute("transactionStatus", "failed");
			aoRequest.setAttribute("transactionMessage", REQUEST_COULD_NOT_BE_COMPLETED);
		}
		return lsSystemTermsAndCond;
	}

	/**
	 * This method return blank String in case input is null
	 * 
	 * @param asInputStr Input String
	 * @param asChannelInput containing Application Terms & Conditions
	 * @throws IOException
	 */

	private String isNull(String asInputStr)
	{
		String lsReturnVal = "";
		if (asInputStr != null && !"null".equalsIgnoreCase(asInputStr))
		{
			lsReturnVal = asInputStr;
		}
		return lsReturnVal;
	}

	/**
	 * This method redirects Provider user to Select Organization screen after
	 * login to select Organization for Login.
	 * @param aoRequest Action Request
	 * @param aoResponse Action Response
	 * @throws ApplicationException
	 */
	private void redirectToSelectOrganization(ActionRequest aoRequest, ActionResponse aoResponse)
			throws ApplicationException
	{
		String lsSelectOrgPagePath = "";
		try
		{
			lsSelectOrgPagePath = aoRequest.getScheme()
					+ "://"
					+ aoRequest.getServerName()
					+ ":"
					+ aoRequest.getServerPort()
					+ aoRequest.getContextPath()
					+ ApplicationConstants.PORTAL_URL
					+ "&_urlType=render&_pageLabel=portlet_hhsweb_portal_page_chooseOrganization&render_action=selectOrganization";
			aoResponse.sendRedirect(lsSelectOrgPagePath);
		}
		catch (Exception loEx)
		{
			throw new ApplicationException("Not able to redirect on select organization page after validating user.",
					loEx);
		}
	}

}
