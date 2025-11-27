/**
This Class is updated in R7.

 * 
 */
package com.nyc.hhs.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;
import org.springframework.web.portlet.mvc.ResourceAwareController;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.controllers.util.ContractListUtils;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.AuthenticationBean;
import com.nyc.hhs.model.BudgetAdvanceBean;
import com.nyc.hhs.model.BudgetList;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.ContractBudgetBean;
import com.nyc.hhs.model.FiscalYear;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.NotificationBean;
import com.nyc.hhs.model.ProgramNameInfo;
import com.nyc.hhs.model.StatusR3;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.model.UserThreadLocal;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.ApplicationSession;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.HHSPortalUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PortalUtil;
import com.nyc.hhs.util.PortletSessionHandler;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This controller will be used to handle all action and render request from
 * Budget List page This file is updated in R7.
 */
@Controller(value = "budgetListHandler")
@RequestMapping("view")
public class BudgetListController extends BaseController implements ResourceAwareController
{
	/**
	 * Logger Object
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(BudgetListController.class);

	/**
	 * Binding BudgetList Bean
	 * 
	 * @return BudgetList Object
	 */
	@ModelAttribute("BudgetList")
	public BudgetList getCommandObject()
	{
		return new BudgetList(HHSConstants.ORG_TYPE);
	}

	/**
	 * This method will initialise the BudgetAdvance bean object
	 * 
	 * @return BudgetAdvance Bean Object
	 */
	@ModelAttribute("BudgetAdvance")
	public BudgetAdvanceBean getCommandOb()
	{
		return new BudgetAdvanceBean();
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
	 * <ol>
	 * <li>This is the default render method for BudgetList.jsp</li>
	 * <li>Initially in this method we are retrieving the organisation type of
	 * the user logged in through the Portlet Session attribute</li>
	 * <li>This userType is set in the channel object to be retrieved in the
	 * service class</li>
	 * <li>For sorting and pagination purpose we have made a bean
	 * BudgetListFilter</li>
	 * <li>When the page is loaded for the first time we pass all the default
	 * sorting parameters through our method</li>
	 * <li>When an action is performed for sorting on my page than the sorting
	 * parameters bean are set in the action method</li>
	 * <li>In the action method this bean is set in the session attribute which
	 * is retrieved at the time of render</li>
	 * <li>Once the sorting parameters are retrieved they are set in the channel
	 * object to be send to service class</li>
	 * <li>On setting the channel object the desired transaction is executed</li>
	 * <li>In this method getFinancialsBudgetList transaction invokes
	 * FinancialsBudgetService.java class</li>
	 * <li>FinancialsBudgetService.java triggers sql query to fetch data from
	 * database</li>
	 * <li>List Bean that is returned from service class is fetched from the
	 * channel attribute of current transaction</li>
	 * <li>Fetched list bean is set in the aoRequest parameter to be retrieved
	 * in the jsp</li>
	 * <li>Than at last the modelandview object is returned from this method
	 * that is returning budgetList.jsp as the view</li>
	 * <li>We are checking for the Navigation as well whether we are coming from
	 * Home page or some other page based on that wee are displaying Budgets</li>
	 * </ol>
	 * 
	 * @param aoRequest Request as input
	 * 
	 * @return ModelAndView ModelAndView as output
	 * 
	 * @throws ApplicationException Application Exception thrown in case any
	 *             query fails
	 * 
	 */
	@RenderMapping
	protected ModelAndView displayBudgetList(RenderRequest aoRequest) throws ApplicationException
	{
		return mainRenderMethod(aoRequest, null, null);
	}

	/**
	 * This is the method called for both simple render and when we render from
	 * overlay. <li>The transaction used: getFinancialsBudgetList</li> Modified
	 * method to fetch user id for defect id 6384 and build 3.2.0.
	 * @param aoRequest Request as input
	 * @param asOverlayPageParam OverlayPageParam as input
	 * @param asOverLay OverLay as input
	 * @return ModelAndView as output
	 */
	private ModelAndView mainRenderMethod(RenderRequest aoRequest, String asOverlayPageParam, String asOverLay)
	{
		PortletSession loPortletSession = aoRequest.getPortletSession();
		// For Provider and Agency both we get corresponding Id from this
		// session variable.
		String lsOrgId = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
				PortletSession.APPLICATION_SCOPE);
		// build 3.2.0, defect id 6384
		String lsUserId = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsOrgnizationType = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
				PortletSession.APPLICATION_SCOPE);
		Channel loChannelObj = new Channel();
		// Release 5
		String lsPermissionType = (String) loPortletSession.getAttribute(HHSConstants.PERMISSION_TYPE,
				PortletSession.APPLICATION_SCOPE);
		// Start R7: Variable added to check if request come from task screen
		// page
		String lsBudgetModificationLanding = (String) loPortletSession.getAttribute(
				HHSR5Constants.REDIRECT_TO_MODIFIED_BUDGET_LIST, PortletSession.APPLICATION_SCOPE);
		// End R7
		loChannelObj.setData(HHSConstants.AS_USER_TYPE, lsOrgnizationType);
		String lsNextPage = null;
		BudgetList loBudgetBean = null;
		String lsProviderId = null;
		Boolean loFirstLoad = false;
		String lsRequestFromHomePage = HHSConstants.FALSE;
		if (null != PortalUtil.parseQueryString(aoRequest, HHSConstants.FROM_HOME_PAGE))
		{
			lsRequestFromHomePage = PortalUtil.parseQueryString(aoRequest, HHSConstants.FROM_HOME_PAGE);
		}
		try
		{
			if (asOverlayPageParam != null && asOverLay != null)
			{
				lsNextPage = (String) ApplicationSession.getAttribute(aoRequest, asOverlayPageParam);
				loBudgetBean = (BudgetList) ApplicationSession.getAttribute(aoRequest, true, asOverLay);
			}
			else
			{
				lsNextPage = (String) ApplicationSession.getAttribute(aoRequest, HHSConstants.NEXT_PAGE_PARAM);
				loBudgetBean = (BudgetList) ApplicationSession.getAttribute(aoRequest, true,
						HHSConstants.BUDGET_SESSION_BEAN);
				loPortletSession.removeAttribute(HHSConstants.CONTRACT_SESSION_BEAN);
				loPortletSession.removeAttribute(HHSConstants.PAYMENT_SESSION_BEAN);
				loPortletSession.removeAttribute(HHSConstants.INVOICE_SESSION_BEAN);
				loPortletSession.removeAttribute(HHSConstants.AMENDED_CONTRACT_SESSION_BEAN);
			}

			// Setting BudgetBean Data.
			// Checking for whether we are navigating from home page or from
			// some other page
			if (null != lsOrgnizationType && lsOrgnizationType.equals(ApplicationConstants.PROVIDER_ORG)
					&& !lsRequestFromHomePage.equals(HHSConstants.FALSE))
			{
				loBudgetBean = new BudgetList(lsOrgnizationType);
				loBudgetBean = getFinancialBeanForProviderHomePages(aoRequest, loBudgetBean);
			}
			else if (null != lsOrgnizationType
					&& (lsOrgnizationType.equals(ApplicationConstants.CITY_ORG) || lsOrgnizationType
							.equals(ApplicationConstants.AGENCY_ORG))
					&& !lsRequestFromHomePage.equals(HHSConstants.FALSE))
			{
				loBudgetBean = new BudgetList(lsOrgnizationType);
				loBudgetBean = getFinancialBeanForCityAndAgencyHomePages(aoRequest, loBudgetBean);
			}
						
			if (null == loBudgetBean)
			{
				loFirstLoad = true;
				loBudgetBean = new BudgetList(lsOrgnizationType);
			}
			// Start : R5 Added
			loBudgetBean.setUserIdContractRestriction(lsUserId);
			// Updated for Emergency Build 4.0.0.2 defect 8360, 8377
			Boolean lbFromFinancialTab = Boolean.valueOf(aoRequest.getParameter(HHSR5Constants.FROM_FINANCIAL_TAB));
			if (null != lbFromFinancialTab && lbFromFinancialTab)
			{
				removeRedirectFromListSessionData(loPortletSession);
			}
			String lsContractIdForBudgetList = (String) loPortletSession.getAttribute(
					HHSR5Constants.CONTRACT_ID_FOR_LIST, PortletSession.APPLICATION_SCOPE);
			if (StringUtils.isNotBlank(lsContractIdForBudgetList))
			{
				loBudgetBean.setContractId(lsContractIdForBudgetList);
			}
			String lsAmendContractIdForBudgetList = (String) loPortletSession.getAttribute("amendcontractid",
					PortletSession.APPLICATION_SCOPE);
			if (StringUtils.isNotBlank(lsAmendContractIdForBudgetList))
			{
				loBudgetBean.setAmendmentContractId(lsAmendContractIdForBudgetList);
			}
			String lsBudgetIdForBudgetList = (String) loPortletSession.getAttribute(HHSR5Constants.BUDGET_ID_FOR_LIST,
					PortletSession.APPLICATION_SCOPE);
			if (StringUtils.isNotBlank(lsBudgetIdForBudgetList))
			{
				loBudgetBean.setBudgetId(lsBudgetIdForBudgetList);
			}
			String lsBudgetType = (String) loPortletSession.getAttribute(HHSR5Constants.BUDGET_TYPE_FROM_LANDING,
					PortletSession.APPLICATION_SCOPE);
			Boolean loFromList = Boolean.FALSE;
			
			if (StringUtils.isNotBlank(lsBudgetType))
			{
				loBudgetBean.setBudgetTypeList(new ArrayList<String>(Arrays.asList(lsBudgetType
						.split(HHSConstants.COMMA))));
				lsRequestFromHomePage = HHSConstants.STRING_TRUE;

				// STATUS_APPROVED
				loBudgetBean.getBudgetStatusList().add(
						PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_BUDGET_APPROVED));
				// STATUS_RETURNED_FOR_REVISION
				loBudgetBean.getBudgetStatusList().add(
						PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.BUDGET_RETURNED_FOR_REVISION));
				// STATUS_PENDING_APPROVAL
				loBudgetBean.getBudgetStatusList().add(
						PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_BUDGET_PENDING_APPROVAL));
				// STATUS_PENDING_SUBMISSION
				loBudgetBean.getBudgetStatusList().add(
						PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.BUDGET_PENDING_SUBMISSION));
				// STATUS_ACTIVE
				loBudgetBean.getBudgetStatusList().add(
						PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_BUDGET_ACTIVE));
				// STATUS_BUDGET_CLOSED
				loBudgetBean.getBudgetStatusList().add(
						PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_BUDGET_CLOSED));
				// STATUS_BUDGET_SUSPENDED
				loBudgetBean.getBudgetStatusList().add(
						PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_BUDGET_SUSPENDED));
				// STATUS_BUDGET_CANCELLED
				loBudgetBean.getBudgetStatusList().add(
						PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_BUDGET_CANCELLED));
				loFromList = Boolean.TRUE;
			}
			// End : R5 Added
			lsProviderId = (String) ApplicationSession.getAttribute(aoRequest, HHSConstants.PROVIDER_ID);
			if (null != lsProviderId)
			{
				loBudgetBean.setProviderId(lsProviderId);
			}
			String lsContractTitle = null;
			if (loBudgetBean.getContractTitle() != null)
			{
				lsContractTitle = loBudgetBean.getContractTitle();
				loBudgetBean.setContractTitle(loBudgetBean.getContractTitle().replaceAll(HHSConstants.STR,
						HHSConstants.STR_DOUBLE));
			}
			// R7 START: Set the contract title for the budget to be displayed
			if (null != lsBudgetModificationLanding
					&& lsBudgetModificationLanding.equalsIgnoreCase(HHSR5Constants.TRUE))
			{
				String lsContractBudgetId = (String) loPortletSession.getAttribute(HHSR5Constants.PARENT_BUDGET_ID,
						PortletSession.APPLICATION_SCOPE);
				loBudgetBean.setBudgetId(lsContractBudgetId);
				loBudgetBean.setFilterModificationBudgetRedirection(true);
			}
			// R7 END
			loBudgetBean.setLsRequestFromHomePage(lsRequestFromHomePage);
			loBudgetBean.setOrgId(lsOrgId);
			// build 3.2.0, defect id 6384
			loBudgetBean.setUserId(lsUserId);
			loBudgetBean.setOrgType(lsOrgnizationType);
			loChannelObj.setData(HHSConstants.AO_BUDGET_BEAN, loBudgetBean);
			loChannelObj.setData(HHSConstants.AS_ORG_TYPE, lsOrgnizationType);
			loChannelObj.setData(HHSConstants.AS_ORG_ID, lsOrgId);
			loChannelObj.setData(HHSConstants.AS_PROCESS_TYPE, HHSConstants.BUDGETLIST_BUDGET);
			loChannelObj.setData(HHSConstants.ORG_ID, lsOrgId);
			loChannelObj.setData(HHSConstants.ORGTYPE, lsOrgnizationType);
			getPagingParams(loPortletSession, loBudgetBean, lsNextPage, HHSConstants.BUDGET_LIST_PAGE);
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.CBL_GET_FINANCIALS_BUDGET_LIST); 
			if (loBudgetBean.getContractTitle() != null && lsContractTitle != null)
			{
				loBudgetBean.setContractTitle(lsContractTitle);
			}
			
			
			// Start Updated in R5
			getAndSetBudgetData(aoRequest, loPortletSession, loChannelObj, loBudgetBean, loFirstLoad,
					lsRequestFromHomePage, lsOrgnizationType, lsPermissionType, loFromList);
			// End Updated in R5
			// R7 added: Removing Session Variable
			if (null != lsBudgetModificationLanding
					&& lsBudgetModificationLanding.equalsIgnoreCase(HHSR5Constants.TRUE))
			{
				loPortletSession.removeAttribute(HHSR5Constants.PARENT_BUDGET_ID, PortletSession.APPLICATION_SCOPE);
				loPortletSession.removeAttribute(HHSR5Constants.REDIRECT_TO_MODIFIED_BUDGET_LIST,
						PortletSession.APPLICATION_SCOPE);
			}
			// R7 End
			
			//[Start] R7.7.0 QC9149 
			if( loBudgetBean.getNegativeAmendCnt() != null && loBudgetBean.getNegativeAmendCnt() > 0 )
			{
                aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_STATUS, HHSConstants.AS_FAILED);
                aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_MSG, HHSConstants.SUBMIT_ADVANCE_FAILURE );
			}
			//[End] R7.7.0 QC9149 

			if (null != loPortletSession.getAttribute(HHSConstants.IS_SUBMIT_INVOICE_ERROR_MSG, PortletSession.APPLICATION_SCOPE)
					&& !((String) loPortletSession.getAttribute(HHSConstants.IS_SUBMIT_INVOICE_ERROR_MSG,
							PortletSession.APPLICATION_SCOPE)).equalsIgnoreCase(HHSConstants.EMPTY_STRING))
			{
				aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_STATUS, HHSConstants.AS_FAILED);
				aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_MSG, loPortletSession.getAttribute(
						HHSConstants.IS_SUBMIT_INVOICE_ERROR_MSG, PortletSession.APPLICATION_SCOPE));
				loPortletSession.removeAttribute(HHSConstants.IS_SUBMIT_INVOICE_ERROR_MSG,
						PortletSession.APPLICATION_SCOPE);
			}
			if (null != loPortletSession.getAttribute(HHSConstants.SUCCESS_MESSAGE, PortletSession.APPLICATION_SCOPE)
					&& !loPortletSession.getAttribute(HHSConstants.SUCCESS_MESSAGE, PortletSession.APPLICATION_SCOPE)
							.toString().isEmpty())
			{
				aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_STATUS, HHSConstants.AS_PASSED);
				aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_MSG,
						loPortletSession.getAttribute(HHSConstants.SUCCESS_MESSAGE, PortletSession.APPLICATION_SCOPE));
				loPortletSession.removeAttribute(HHSConstants.SUCCESS_MESSAGE, PortletSession.APPLICATION_SCOPE);
			}
		}
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error(HHSConstants.CBL_ERROR_OCCURED_WHILE_PROCESSING_BUDGETS, loExp);
			String lsErrorMsg = HHSConstants.ERROR_WHILE_PROCESSING_REQUEST;
			loPortletSession.setAttribute(HHSConstants.ERROR_BUDGET_TYPE, lsErrorMsg, PortletSession.APPLICATION_SCOPE);
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error(HHSConstants.PY_ERROR_OCCURED_WHILE_PROCESSING_PAYMENTS, loExp);
			String lsErrorMsg = HHSConstants.ERROR_WHILE_PROCESSING_REQUEST;
			loPortletSession.setAttribute(HHSConstants.ERROR_BUDGET_TYPE, lsErrorMsg, PortletSession.APPLICATION_SCOPE);
		}
		return new ModelAndView(HHSConstants.CBL_BUDGET_LIST, HHSConstants.BUDGET_LIST_PAGE, loBudgetBean);
	}

	/*
	 * validate Negative Amendment with given indicator
	 * 
	 */
	private boolean hasNegativeAmendment(String aoNegativAmendInteger) throws ApplicationException
	{
	    if(aoNegativAmendInteger == null ) return false;
	    
	    try
	    {
	      long i = Integer.parseInt(aoNegativAmendInteger.trim());
	      if( i > 0 ) return true;
	    }
	    catch (NumberFormatException nfe){
	        return false;
	    }
	    return false;
	}
	
	
	/**
	 * This method set and get budget related data.
	 * @param aoRequest Request as input.
	 * @param aoPortletSession PortletSession as input.
	 * @param aoChannelObj channel object as input
	 * @param aoBudgetBean BudgetBean as input
	 * @param aoFirstLoad FirstLoad as input
	 * @param asRequestFromHomePage RequestFromHomePage as input
	 * @param aoPermissionType as input
	 * @param aoPermissionType as input
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	private void getAndSetBudgetData(RenderRequest aoRequest, PortletSession aoPortletSession, Channel aoChannelObj,
			BudgetList aoBudgetBean, Boolean aoFirstLoad, String asRequestFromHomePage, String asOrgnizationType,
			String aoPermissionType, Boolean aoFromList) throws ApplicationException
	{
		String lsRole = (String) aoPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ROLE,
				PortletSession.APPLICATION_SCOPE);
		List<BudgetList> loBudgetList = (List<BudgetList>) aoChannelObj
				.getData(HHSConstants.CBL_AO_CB_BUDGET_IT_LIST_BEAN);

		// Release 5 User Notification
		if (aoPermissionType != null
				&& (aoPermissionType.equalsIgnoreCase(HHSR5Constants.ROLE_READ_ONLY) || aoPermissionType
						.equalsIgnoreCase(HHSR5Constants.ROLE_PROCUREMENT)))
		{
			if (loBudgetList != null)
			{
				for (BudgetList loContract : loBudgetList)
				{
					loContract.setUserAccess(false);
				}
			}
		}
		// Release 5 User Notification

		if (null != loBudgetList)
		{
			for (BudgetList loBudgetListBean : loBudgetList)
			{
				loBudgetListBean.setActions(lsRole);
			}
		}
		Integer loBudgetListCount = (Integer) aoChannelObj.getData(HHSConstants.CBL_LI_BUDGET_LIST_COUNT);
		List<FiscalYear> loFiscalInformation = (List<FiscalYear>) aoChannelObj.getData(HHSConstants.FISCAL_INFORMATION);
		List<StatusR3> loBudgetStatus = (List<StatusR3>) aoChannelObj.getData(HHSConstants.INVOICE_LO_STATUS_LIST);
		List<ProgramNameInfo> loProgramNameList = (List<ProgramNameInfo>) aoPortletSession.getAttribute(
				HHSConstants.PROGRAM_NAME_LIST, PortletSession.APPLICATION_SCOPE);
		List<HashMap<String, String>> loAgencyDetails = (List<HashMap<String, String>>) aoPortletSession.getAttribute(
				HHSConstants.AGENCY_DETAILS, PortletSession.APPLICATION_SCOPE);

		if ((loProgramNameList == null || loProgramNameList.isEmpty())
				&& asOrgnizationType.equalsIgnoreCase(HHSConstants.USER_AGENCY))
		{

			loProgramNameList = ContractListUtils.getProgramNameList(aoChannelObj);
			aoPortletSession.setAttribute(HHSConstants.PROGRAM_NAME_LIST, loProgramNameList);
		}
		if (loAgencyDetails == null || loAgencyDetails.isEmpty())
		{
			loAgencyDetails = ContractListUtils.getAgencyDetails(aoChannelObj);
			aoPortletSession.setAttribute(HHSConstants.AGENCY_DETAILS, loAgencyDetails,
					PortletSession.APPLICATION_SCOPE);
		}
		
		/*[Start] R7.2.0 QC8914   Set indicator for Access control     */
        PortletSession loSession  = aoRequest.getPortletSession();
        if( loSession.getAttribute(ApplicationConstants.KEY_SESSION_ROLE_CURRENT, PortletSession.APPLICATION_SCOPE) != null
                && ApplicationConstants.ROLE_OBSERVER.equalsIgnoreCase((String)loSession.getAttribute(ApplicationConstants.KEY_SESSION_ROLE_CURRENT, PortletSession.APPLICATION_SCOPE)))  {
            setIndecatorForReadOnlyRole(loBudgetList, ApplicationConstants.ROLE_OBSERVER);
        }
        /*[End] R7.2.0 QC8914 Set indicator for Access control     */ 
		
		aoRequest.setAttribute(HHSConstants.PY_AO_FISCAL_INFORMATION, loFiscalInformation);
		aoRequest.setAttribute(HHSConstants.INVOICE_LB_FIRST_LOAD, aoFirstLoad);
		aoRequest.setAttribute(HHSConstants.AO_AGENCY_LIST, loAgencyDetails);

		aoRequest.setAttribute(HHSConstants.CBL_FINANCIALS_BUDGET_LIST, loBudgetList);
		aoRequest.setAttribute(HHSConstants.CBL_AO_BUDGET_LIST_LABEL, loBudgetListCount);
		aoRequest.setAttribute(HHSConstants.BUDGET_LIST_HOME, new BudgetList());
		if (asRequestFromHomePage == HHSConstants.FALSE)
		{
			aoRequest.setAttribute(HHSConstants.AO_BUDGET_STATUS,
					(List<StatusR3>) aoChannelObj.getData(HHSConstants.INVOICE_LO_STATUS_LIST));
		}
		else
		{
			if (aoFromList)
			{
				aoBudgetBean.setBudgetStatusList(new ArrayList<String>());
				// STATUS_APPROVED
				aoBudgetBean.getBudgetStatusList().add(
						PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_BUDGET_APPROVED));
				// STATUS_RETURNED_FOR_REVISION
				aoBudgetBean.getBudgetStatusList().add(
						PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.BUDGET_RETURNED_FOR_REVISION));
				// STATUS_PENDING_APPROVAL
				aoBudgetBean.getBudgetStatusList().add(
						PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_BUDGET_PENDING_APPROVAL));
				// STATUS_PENDING_SUBMISSION
				aoBudgetBean.getBudgetStatusList().add(
						PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.BUDGET_PENDING_SUBMISSION));
				// STATUS_ACTIVE
				aoBudgetBean.getBudgetStatusList().add(
						PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_BUDGET_ACTIVE));
				aoRequest.setAttribute(HHSConstants.BUDGET_LIST_HOME, aoBudgetBean);
			}
			else
			{
				aoRequest.setAttribute(HHSConstants.BUDGET_LIST_HOME, aoBudgetBean);
			}
		}
		aoRequest.setAttribute(HHSConstants.AO_BUDGET_STATUS, loBudgetStatus);
		aoPortletSession.setAttribute(ApplicationConstants.ALERT_VIEW_PAGING_RECORDS, ((loBudgetListCount == null) ? 0
				: loBudgetListCount), PortletSession.APPLICATION_SCOPE);
		aoRequest.getPortletSession().setAttribute(HHSConstants.SORT_TYPE, aoBudgetBean.getFirstSortType(),
				PortletSession.APPLICATION_SCOPE);
		aoRequest.getPortletSession().setAttribute(HHSConstants.SORT_BY, aoBudgetBean.getSortColumnName(),
				PortletSession.APPLICATION_SCOPE);
		aoRequest.setAttribute(HHSConstants.ERROR_MESSAGE_BUDGET_LIST,
				PortalUtil.parseQueryString(aoRequest, HHSConstants.CBL_MESSAGE));
	}

    /**
    * This method will handle setting indicator setIndecatorForReadOnlyRole ApplicationConstants.ROLE_OBSERVER 
    * for access control.
    * @param List<ContractList> loContractList
    * @throws ApplicationException
    */ 	
	private void setIndecatorForReadOnlyRole(List<BudgetList> aoBudgetList, String asUserRole) {
        if( aoBudgetList == null || aoBudgetList.isEmpty() == true)  return;

        for( BudgetList  conObj :   aoBudgetList){
            conObj.setUserSubRole(asUserRole);
        }
    } 
	
	/**
	 * This method will handle all the resource request when user clicks on
	 * count in Budget portlet under Financial portlet on home page. * This
	 * method returns the bean based on the filter criteria on home page.
	 * <ul>
	 * ========================================<br/>
	 * Conditions to be checked in this method<br/>
	 * ========================================<br/>
	 * <li>Get BudgetList bean from session.</li>
	 * <li>we have different links for the budget list,so based on the
	 * requirements we are setting parameter into the bean and returning the
	 * bean to mainRenderMethod for further processing.
	 * <li>Internally calling the methods
	 * getActiveBudgets,getBudgetsPendingSubmission
	 * getBudgetsPendingApproval,getBudgetsReturnedRevision,getModPendSubmission
	 * getModPendApproval and getModUpdatesRetSubmission. getRFPDueIn30Days for
	 * retrieving the bean</li>
	 * <li>Return BudgetList bean to the method MainRender.</li>
	 * </ul>
	 * 
	 * @param aoRequest Request as input
	 * @param aoBudgetBean BudgetList bean as input
	 * @return aoBudgetBean changed BudgetList as output
	 * @throws ApplicationException Application Exception thrown in case any
	 *             query fails
	 */
	private BudgetList getFinancialBeanForProviderHomePages(RenderRequest aoRequest, BudgetList aoBudgetBean)
			throws ApplicationException
	{
		if (null == aoBudgetBean)
		{
			aoBudgetBean = new BudgetList();
		}

		String lsFilterCriteria = PortalUtil.parseQueryString(aoRequest, HHSConstants.FILTER_CRITERIA);
		if (null != lsFilterCriteria)
		{
			if (lsFilterCriteria.equalsIgnoreCase(HHSConstants.ACTIVE_BUDGETS))
			{
				getActiveBudgets(aoRequest, aoBudgetBean);
			}
			else if (lsFilterCriteria.equalsIgnoreCase(HHSConstants.BUDGETS_PEND_SUBMISSION))
			{
				getBudgetsPendingSubmission(aoRequest, aoBudgetBean);
			}
			else if (lsFilterCriteria.equalsIgnoreCase(HHSConstants.BUDGETS_PEND_APPROVAL))
			{
				getBudgetsPendingApproval(aoRequest, aoBudgetBean);
			}
			else if (lsFilterCriteria.equalsIgnoreCase(HHSConstants.BUDGETS_RET_REVISION))
			{
				getBudgetsReturnedRevision(aoRequest, aoBudgetBean);
			}
			else if (lsFilterCriteria.equalsIgnoreCase(HHSConstants.MOD_PEND_SUBMISSION))
			{
				getModPendSubmission(aoRequest, aoBudgetBean);
			}
			else if (lsFilterCriteria.equalsIgnoreCase(HHSConstants.MOD_PEND_APPROVAL))
			{
				getModPendApproval(aoRequest, aoBudgetBean);
			}
			else if (lsFilterCriteria.equalsIgnoreCase(HHSConstants.MOD_UPDATES_RET_SUBMISSION))
			{
				getModUpdatesRetSubmission(aoRequest, aoBudgetBean);
			}
		}
		
		return aoBudgetBean;
	}

	/**
	 * This method will handle all the resource request when user clicks on
	 * count of Active Budgets in Budget portlet under Financial portlet on home
	 * page. This method returns the bean based on the filter criteria on home
	 * page.
	 * <ul>
	 * ========================================<br/>
	 * Conditions to be checked in this method<br/>
	 * ========================================<br/>
	 * <li>Get BudgetList bean from session.</li>
	 * <li>Based on the requirement we have set the parameters as budget status
	 * should be active into the bean and returning the bean to mainRenderMethod
	 * for further processing.
	 * <li>Return BudgetList bean to the method MainRender.</li>
	 * </ul>
	 * 
	 * @param aoRequest Request as input
	 * @param aoBudgetBean BudgetList bean as input
	 * @return aoBudgetBean changed BudgetList bean as output
	 * @throws ApplicationException Application Exception thrown in case any
	 *             query fails
	 */
	private BudgetList getActiveBudgets(RenderRequest aoRequest, BudgetList aoBudgetBean) throws ApplicationException
	{
		List<String> loBudgetStatusList = new ArrayList<String>();
		loBudgetStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_BUDGET_ACTIVE));
		aoBudgetBean.setBudgetStatusList(loBudgetStatusList);
		aoBudgetBean.getBudgetTypeList().add(HHSConstants.BUDGET_TYPE3);
		return aoBudgetBean;
	}

	/**
	 * This method will handle all the resource request when user clicks on
	 * count of Budgets pending submission in Budget portlet under Financial
	 * portlet on home page. This method returns the bean based on the filter
	 * criteria on home page.
	 * <ul>
	 * ========================================<br/>
	 * Conditions to be checked in this method<br/>
	 * ========================================<br/>
	 * <li>Get BudgetList bean from session.</li>
	 * <li>Based on the requirement we have set the parameters as budget status
	 * should be Pending for Submission and budget type should be contract
	 * budget or budget amendment into the bean and returning the bean to
	 * mainRenderMethod for further processing.
	 * <li>Return BudgetList bean to the method MainRender.</li>
	 * </ul>
	 * 
	 * @param aoRequest Request as input
	 * @param aoBudgetBean BudgetList bean as input
	 * @return loBudgetBean BudgetList bean as output
	 * @throws ApplicationException Application Exception thrown in case any
	 *             query fails
	 */
	private BudgetList getBudgetsPendingSubmission(RenderRequest aoRequest, BudgetList aoBudgetBean)
			throws ApplicationException
	{
		List<String> loBudgetStatusList = new ArrayList<String>();
		loBudgetStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.BUDGET_PENDING_SUBMISSION));
		aoBudgetBean.setBudgetStatusList(loBudgetStatusList);
		aoBudgetBean.getBudgetTypeList().add(HHSConstants.BUDGET_TYPE1);
		aoBudgetBean.getBudgetTypeList().add(HHSConstants.BUDGET_TYPE3);
		return aoBudgetBean;
	}

	/**
	 * This method will handle all the resource request when user clicks on
	 * count of Budgets pending Approval in Budget portlet under Financial
	 * portlet on home page. This method returns the bean based on the filter
	 * criteria on home page.
	 * <ul>
	 * ========================================<br/>
	 * Conditions to be checked in this method<br/>
	 * ========================================<br/>
	 * <li>Get BudgetList bean from session.</li>
	 * <li>Based on the requirement we have set the parameters as budget status
	 * should be Pending for Approval and budget type should be contract budget
	 * or budget amendment into the bean and returning the bean to
	 * mainRenderMethod for further processing.
	 * <li>Return BudgetList bean to the method MainRender.</li>
	 * </ul>
	 * 
	 * @param aoRequest Request as input
	 * @param aoBudgetBeanForPendingApproval BudgetList bean as input
	 * @return loBudgetBean BudgetList bean as output
	 * @throws ApplicationException Application Exception thrown in case any
	 *             query fails
	 */
	private BudgetList getBudgetsPendingApproval(RenderRequest aoRequest, BudgetList aoBudgetBeanForPendingApproval)
			throws ApplicationException
	{
		List<String> loBudgetStatusList = new ArrayList<String>();
		loBudgetStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_BUDGET_PENDING_APPROVAL));
		aoBudgetBeanForPendingApproval.setBudgetStatusList(loBudgetStatusList);
		aoBudgetBeanForPendingApproval.getBudgetTypeList().add(HHSConstants.BUDGET_TYPE1);
		aoBudgetBeanForPendingApproval.getBudgetTypeList().add(HHSConstants.BUDGET_TYPE3);
		return aoBudgetBeanForPendingApproval;
	}

	/**
	 * This method will handle all the resource request when user clicks on
	 * count of Budgets Returned for Revision in Budget portlet under Financial
	 * portlet on home page. This method returns the bean based on the filter
	 * criteria on home page.
	 * <ul>
	 * ========================================<br/>
	 * Conditions to be checked in this method<br/>
	 * ========================================<br/>
	 * <li>Get BudgetList bean from session.</li>
	 * <li>Based on the requirement we have set the parameters as budget status
	 * should be Returned for Revision and budget type should be contract budget
	 * or budget amendment into the bean and returning the bean to
	 * mainRenderMethod for further processing.
	 * <li>Return BudgetList bean to the method MainRender.</li>
	 * </ul>
	 * 
	 * @param aoRequest Request as input
	 * @param aoBudgetBeanForReturned BudgetList bean as input
	 * @return loBudgetBean BudgetList bean as output
	 * @throws ApplicationException Application Exception thrown in case any
	 *             query fails
	 */
	private BudgetList getBudgetsReturnedRevision(RenderRequest aoRequest, BudgetList aoBudgetBeanForReturned)
			throws ApplicationException
	{
		List<String> loBudgetStatusList = new ArrayList<String>();
		loBudgetStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.BUDGET_RETURNED_FOR_REVISION));
		aoBudgetBeanForReturned.setBudgetStatusList(loBudgetStatusList);
		aoBudgetBeanForReturned.getBudgetTypeList().add(HHSConstants.BUDGET_TYPE1);
		aoBudgetBeanForReturned.getBudgetTypeList().add(HHSConstants.BUDGET_TYPE3);
		return aoBudgetBeanForReturned;
	}

	/**
	 * This method will handle all the resource request when user clicks on
	 * count of Modifications and Updates pending submission in Budget portlet
	 * under Financial portlet on home page. This method returns the bean based
	 * on the filter criteria on home page.
	 * <ul>
	 * ========================================<br/>
	 * Conditions to be checked in this method<br/>
	 * ========================================<br/>
	 * <li>Get BudgetList bean from session.</li>
	 * <li>Based on the requirement we have set the parameters as budget status
	 * should be Pending Submission and budget type should be Modification
	 * budget or Update Budget into the bean and returning the bean to
	 * mainRenderMethod for further processing.
	 * <li>Return BudgetList bean to the method MainRender.</li>
	 * </ul>
	 * 
	 * @param aoRequest Request as input
	 * @param aoBudgetBean BudgetList bean as input
	 * @return loBudgetBean BudgetList bean as output
	 * @throws ApplicationException Application Exception thrown in case any
	 *             query fails
	 */
	private BudgetList getModPendSubmission(RenderRequest aoRequest, BudgetList aoBudgetBean)
			throws ApplicationException
	{
		List<String> loBudgetStatusList = new ArrayList<String>();
		loBudgetStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.BUDGET_PENDING_SUBMISSION));
		aoBudgetBean.setBudgetStatusList(loBudgetStatusList);
		aoBudgetBean.getBudgetTypeList().add(HHSConstants.BUDGET_TYPE2);
		aoBudgetBean.getBudgetTypeList().add(HHSConstants.BUDGET_TYPE4);
		return aoBudgetBean;
	}

	/**
	 * This method will handle all the resource request when user clicks on
	 * count of Modifications and Updates pending approval in Budget portlet
	 * under Financial portlet on home page. This method returns the bean based
	 * on the filter criteria on home page.
	 * <ul>
	 * ========================================<br/>
	 * Conditions to be checked in this method<br/>
	 * ========================================<br/>
	 * <li>Get BudgetList bean from session.</li>
	 * <li>Based on the requirement we have set the parameters as budget status
	 * should be Pending Approval and budget type should be Modification budget
	 * or Update Budget into the bean and returning the bean to mainRenderMethod
	 * for further processing.
	 * <li>Return BudgetList bean to the method MainRender.</li>
	 * </ul>
	 * 
	 * @param aoRequest Request as input
	 * @param aoBudgetBean BudgetList bean as input
	 * @return loBudgetBean BudgetList bean as output
	 * @throws ApplicationException Application Exception thrown in case any
	 *             query fails
	 */
	private BudgetList getModPendApproval(RenderRequest aoRequest, BudgetList aoBudgetBean) throws ApplicationException
	{
		List<String> loBudgetStatusList = new ArrayList<String>();
		loBudgetStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_BUDGET_PENDING_APPROVAL));
		aoBudgetBean.setBudgetStatusList(loBudgetStatusList);
		aoBudgetBean.getBudgetTypeList().add(HHSConstants.BUDGET_TYPE2);
		aoBudgetBean.getBudgetTypeList().add(HHSConstants.BUDGET_TYPE4);
		return aoBudgetBean;
	}

	/**
	 * This method will handle all the resource request when user clicks on
	 * count of Modifications and Updates returned for revision in Budget
	 * portlet under Financial portlet on home page. This method returns the
	 * bean based on the filter criteria on home page.
	 * <ul>
	 * ========================================<br/>
	 * Conditions to be checked in this method<br/>
	 * ========================================<br/>
	 * <li>Get BudgetList bean from session.</li>
	 * <li>Based on the requirement we have set the parameters as budget status
	 * should be Returned for Revision and budget type should be Modification
	 * budget or Update Budget into the bean and returning the bean to
	 * mainRenderMethod for further processing.
	 * <li>Return BudgetList bean to the method MainRender.</li>
	 * </ul>
	 * 
	 * @param aoRequest Request as input
	 * @param aoBudgetBean BudgetList bean as input
	 * @return loBudgetBean BudgetList bean as output
	 * @throws ApplicationException Application Exception thrown in case any
	 *             query fails
	 */
	private BudgetList getModUpdatesRetSubmission(RenderRequest aoRequest, BudgetList aoBudgetBean)
			throws ApplicationException
	{
		List<String> loBudgetStatusList = new ArrayList<String>();
		loBudgetStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.BUDGET_RETURNED_FOR_REVISION));
		aoBudgetBean.setBudgetStatusList(loBudgetStatusList);
		aoBudgetBean.getBudgetTypeList().add(HHSConstants.BUDGET_TYPE2);
		aoBudgetBean.getBudgetTypeList().add(HHSConstants.BUDGET_TYPE4);
		return aoBudgetBean;
	}

	/**
	 * This method will handle all the resource request when user clicks on
	 * count in Budget portlet under Financial portlet on home page. * This
	 * method returns the bean based on the filter criteria on home page.
	 * <ul>
	 * ========================================<br/>
	 * Conditions to be checked in this method<br/>
	 * ========================================<br/>
	 * <li>Get BudgetList bean from session.</li>
	 * <li>we have different links for the budget list,so based on the
	 * requirements we are setting parameter into the bean and returning the
	 * bean to mainRenderMethod for further processing.
	 * <li>Return BudgetList bean to the method MainRender.</li>
	 * </ul>
	 * /**
	 * @param aoRequest Request as input
	 * @param aoBudgetBean BudgetList bean as input
	 * @return loBudgetBean BudgetList bean as output
	 * @throws ApplicationException Application Exception thrown in case any
	 *             query fails
	 */
	private BudgetList getFinancialBeanForCityAndAgencyHomePages(RenderRequest aoRequest, BudgetList aoBudgetBean)
			throws ApplicationException
	{
		if (null == aoBudgetBean)
		{
			aoBudgetBean = new BudgetList();
		}

		String lsFilterCriteria = PortalUtil.parseQueryString(aoRequest, HHSConstants.FILTER_CRITERIA);
		if (null != lsFilterCriteria)
		{
			if (lsFilterCriteria.equalsIgnoreCase(HHSConstants.BUDGETS_AMEND_PEND_APPROVAL))
			{
				getBudgetsAmendPendApproval(aoRequest, aoBudgetBean);
			}
			else if (lsFilterCriteria.equalsIgnoreCase(HHSConstants.BUDGETS_MOD_UPDATES_PEND_APPROVAL))
			{
				getBudgetsModPendApproval(aoRequest, aoBudgetBean);
			}
		}
		return aoBudgetBean;
	}

	/**
	 * @param aoRequest Request as input
	 * @param aoBudgetBeanForAmend BudgetList bean as input
	 * @return loBudgetBean BudgetList bean as output
	 * @throws ApplicationException Application Exception thrown in case any
	 *             query fails
	 */
	private BudgetList getBudgetsAmendPendApproval(RenderRequest aoRequest, BudgetList aoBudgetBeanForAmend)
			throws ApplicationException
	{
		List<String> loBudgetStatusList = new ArrayList<String>();
		loBudgetStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_BUDGET_PENDING_APPROVAL));
		aoBudgetBeanForAmend.setBudgetStatusList(loBudgetStatusList);
		aoBudgetBeanForAmend.getBudgetTypeList().add(HHSConstants.BUDGET_TYPE1);
		aoBudgetBeanForAmend.getBudgetTypeList().add(HHSConstants.BUDGET_TYPE3);
		return aoBudgetBeanForAmend;
	}

	/**
	 * @param aoRequest Request as input
	 * @param aoBudgetBeanForModPend BudgetList bean as input
	 * @return loBudgetBean BudgetList bean as output
	 * @throws ApplicationException Application Exception thrown in case any
	 *             query fails
	 */
	private BudgetList getBudgetsModPendApproval(RenderRequest aoRequest, BudgetList aoBudgetBeanForModPend)
			throws ApplicationException
	{
		List<String> loBudgetStatusList = new ArrayList<String>();
		loBudgetStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_BUDGET_PENDING_APPROVAL));
		aoBudgetBeanForModPend.setBudgetStatusList(loBudgetStatusList);
		aoBudgetBeanForModPend.getBudgetTypeList().add(HHSConstants.BUDGET_TYPE2);
		aoBudgetBeanForModPend.getBudgetTypeList().add(HHSConstants.BUDGET_TYPE4);
		return aoBudgetBeanForModPend;
	}

	/**
	 * @param aoRequest Request as input
	 * @param aoResponse Response as input
	 * @return ModelAndView object as output
	 * @throws ApplicationException Application Exception thrown in case any
	 *             query fails
	 */
	@RenderMapping(params = "duplicate_render=duplicateRender")
	protected ModelAndView handleDuplicateRender(RenderRequest aoRequest, RenderResponse aoResponse)
			throws ApplicationException
	{
		return mainRenderMethod(aoRequest, HHSConstants.CBL_OVERLAY_PAGE_PARAM_BUDGET, HHSConstants.CBL_OVER_LAY_BUDGET);
	}

	/**
	 * <ol>
	 * <li>This is a parameterised action method for budgetListController.</li>
	 * <li>In this sorting is implemented for the grid table of budgetList
	 * Screen</li>
	 * </ol>
	 * @param aoRequest Request as input
	 * @param aoResponse Response as input
	 * @param aoBudgetFilter BudgetList as input
	 * @throws ApplicationException Application Exception thrown in case any
	 *             query fails
	 */
	@ActionMapping(params = "budgetAction=budgetFinacialsMap")
	protected void actionBudgetRoadmap(ActionRequest aoRequest, ActionResponse aoResponse,
			@ModelAttribute("BudgetList") BudgetList aoBudgetFilter) throws ApplicationException
	{
		LOG_OBJECT.Info("========actionBudgetRoadmap===================");
		PortletSession loPortletSession = aoRequest.getPortletSession();
		String lsNextAction = aoRequest.getParameter(ApplicationConstants.NEXT_ACTION);
		String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
		String lsProviderId = null;
		if (null != aoRequest.getParameter(HHSConstants.PROVIDER_ID)
				&& !aoRequest.getParameter(HHSConstants.PROVIDER_ID).isEmpty())
		{
			lsProviderId = (String) aoRequest.getParameter(HHSConstants.PROVIDER_ID);
			ApplicationSession.setAttribute(aoRequest.getParameter(HHSConstants.PROVIDER_ID), aoRequest,
					HHSConstants.PROVIDER_ID);
			loPortletSession.setAttribute(HHSConstants.PROVIDER_ID, aoRequest.getParameter(HHSConstants.PROVIDER_ID),
					PortletSession.APPLICATION_SCOPE);
		}
		else if (null != loPortletSession.getAttribute(HHSConstants.PROVIDER_ID, PortletSession.APPLICATION_SCOPE))
		{
			lsProviderId = (String) loPortletSession.getAttribute(HHSConstants.PROVIDER_ID,
					PortletSession.APPLICATION_SCOPE);
		}
		else
		{
			lsProviderId = HHSR5Constants.EMPTY_STRING;
		}
		if (lsNextAction != null && lsNextAction.equalsIgnoreCase(HHSConstants.SORT_BUDGET_LIST))
		{
			String lsSortType = PortalUtil.parseQueryString(aoRequest, HHSConstants.SORT_TYPE);
			String lsColumnName = PortalUtil.parseQueryString(aoRequest, HHSConstants.COLUMN_NAME);
			String lsGridName = PortalUtil.parseQueryString(aoRequest, HHSConstants.SORT_GRID_NAME);
			getSortDetailsFromXML(lsColumnName, lsUserOrgType, lsGridName, aoBudgetFilter, lsSortType);
			getPagingParams(aoRequest.getPortletSession(), aoBudgetFilter, null, HHSConstants.BUDGET_LIST_PAGE);

		}
		else if (lsNextAction != null && lsNextAction.equalsIgnoreCase(HHSConstants.FETCH_ACTIVE_BUDGETS))
		{
			ApplicationSession.setAttribute(aoRequest.getParameter(HHSConstants.NEXT_PAGE), aoRequest,
					HHSConstants.NEXT_PAGE_PARAM);
			ApplicationSession.setAttribute(aoRequest.getParameter(HHSConstants.NEXT_PAGE), aoRequest,
					HHSConstants.CBL_OVERLAY_PAGE_PARAM_BUDGET);
		}
		else if (lsNextAction != null && lsNextAction.equalsIgnoreCase(HHSConstants.FILTER_BUDGET))
		{
			// added in R7 to filter the approved modified budget
			String lsApprovedFilter = (String) aoRequest.getParameter(HHSR5Constants.APPROVED_MODIFICATION_CHECKED);
			LOG_OBJECT.Info("approved modification checked::"+lsApprovedFilter);
			if (null != lsApprovedFilter && lsApprovedFilter.equalsIgnoreCase(HHSR5Constants.STRING_TRUE))
			{
				LOG_OBJECT.Info("Entered inside Approved Modification Filter");
				// set budget status
				if (aoBudgetFilter.getBudgetStatusList() == null)
				{
					List<String> loBudgetStatusList = new ArrayList<String>();
					loBudgetStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
							HHSConstants.STATUS_BUDGET_APPROVED));
					aoBudgetFilter.setBudgetStatusList(loBudgetStatusList);
				}
				else if (!aoBudgetFilter.getBudgetStatusList().contains(
						PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_BUDGET_APPROVED)))
				{
					aoBudgetFilter.getBudgetStatusList().add(
							PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
									HHSConstants.STATUS_BUDGET_APPROVED));
				}
				// set budget type
				if (aoBudgetFilter.getBudgetTypeList() == null)
				{
					List<String> loBudgetTypeList = new ArrayList<String>();
					aoBudgetFilter.getBudgetTypeList().add(HHSR5Constants.BUDGET_TYPE2);
					aoBudgetFilter.setBudgetTypeList(loBudgetTypeList);
				}
				else if (!aoBudgetFilter.getBudgetTypeList().contains(HHSR5Constants.BUDGET_TYPE2))
				{
					aoBudgetFilter.getBudgetTypeList().add(HHSR5Constants.BUDGET_TYPE2);
				}

				aoBudgetFilter.setIsApprovedModificationChecked(true);

			}
			else
			{
				aoBudgetFilter.setIsApprovedModificationChecked(false);
			}
			LOG_OBJECT.Info("Is Approved modification filter checked:"
					+ aoBudgetFilter.getIsApprovedModificationChecked());
			// R7 end
			aoBudgetFilter.setIsFilter(true);
			aoBudgetFilter.setDefaultSortData(lsUserOrgType);
			// Updated for Emergency Build 4.0.0.2 defect 8360, 8377
			removeRedirectFromListSessionData(loPortletSession);
		}
		ApplicationSession.setAttribute(aoBudgetFilter, aoRequest, HHSConstants.CBL_OVER_LAY_BUDGET);
		aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.CBL_BUDGET_LIST_ACTION);
		ApplicationSession.setAttribute(lsProviderId, aoRequest, HHSConstants.PROVIDER_ID);
		ApplicationSession.setAttribute(aoBudgetFilter, aoRequest, HHSConstants.BUDGET_SESSION_BEAN);
	}

	/**
	 * 
	 * @param aoRequest Request as input
	 * @param aoResponse Response as input
	 * @return loModelAndView ModelAndView as ouput
	 * @throws ApplicationException Application Exception thrown in case any
	 *             query fails
	 */
	@RenderMapping(params = "finance_budget_action=amendBudgetFinacne")
	protected ModelAndView amendBudgetFinacne(RenderRequest aoRequest, RenderResponse aoResponse)
			throws ApplicationException
	{
		ModelAndView loModelAndView = null;
		Map<String, Object> loMapToRender = new HashMap<String, Object>();
		loMapToRender.put(HHSConstants.USER_PROVIDER,
				ApplicationSession.getAttribute(aoRequest, HHSConstants.ORGANIZATION_LEGAL_NAME));
		loMapToRender.put(HHSConstants.CBL_BUDGET_TITLE,
				ApplicationSession.getAttribute(aoRequest, HHSConstants.CBL_BUDGET_TITLE));
		loMapToRender.put(HHSConstants.BUDGET_VALUE,
				ApplicationSession.getAttribute(aoRequest, HHSConstants.BUDGET_VALUE));
		loMapToRender.put(HHSConstants.CLC_CT_NO, ApplicationSession.getAttribute(aoRequest, HHSConstants.CLC_CT_NO));
		loMapToRender.put(HHSConstants.CBL_BUDGET_DATE_OF_LAST_UPDATE,
				ApplicationSession.getAttribute(aoRequest, HHSConstants.CBL_BUDGET_DATE_OF_LAST_UPDATE));
		loMapToRender.put(HHSConstants.CBL_BUDGET_ADVANCE_REQUEST_DATE,
				ApplicationSession.getAttribute(aoRequest, HHSConstants.CBL_BUDGET_ADVANCE_REQUEST_DATE));
		loModelAndView = new ModelAndView(HHSConstants.CBL_REQUEST_ADVANCE, loMapToRender);
		return loModelAndView;
	}

    /*[Start] R7.4.0  QC9008 add ability to remove Contract budget update*/
    /**
     * 
     * 
     * @param aoResourceRequest ResourceRequest as input
     * @param aoResourceResponse ResourceResponse as input
     * @return loModelAndViewAjax ModelAndViewAjax as output
     * @throws ApplicationException Application Exception thrown in case any
     *             query fails
     */
	@ResourceMapping("confirmDeleteBudgetUpdate")
	protected void confirmDeleteBudgetUpdate(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse) throws Exception{
	    boolean lbUserValidation = true;
        String lsErrorMsg = HHSConstants.EMPTY_STRING;
        int liErrorCode = HHSConstants.INT_ZERO;
        LOG_OBJECT.Debug("=======================confirmDeleteBudgetUpdate");
        String lsBudgetId = aoResourceRequest.getParameter(HHSConstants.BUDGET_ID_WORKFLOW);
        String lsContractId = aoResourceRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW);
        String lsBudgetType = aoResourceRequest.getParameter(HHSConstants.BUDGET_TYPE);
        String lsFiscalYears = aoResourceRequest.getParameter(HHSConstants.FISCAL_YEAR_IS_S);
                
        String lsUserEmailId = aoResourceRequest.getParameter(HHSConstants.KEY_SESSION_USER_NAME);
        String lsPassword = aoResourceRequest.getParameter(HHSConstants.PASSWORD);
        String lsDelBudgetUpdateComment = aoResourceRequest.getParameter(HHSConstants.DELETE_BUDGET_UPDATE_COMMENT);

        
        String lsUserId = (String) aoResourceRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
                PortletSession.APPLICATION_SCOPE);
        
        try{
            Map loValidateMap = validateUser(lsUserEmailId, lsPassword, aoResourceRequest);
            lbUserValidation = (Boolean) loValidateMap.get(HHSConstants.IS_VALID_USER);
            if (!lbUserValidation)
            {
                lsErrorMsg = (String) loValidateMap.get(HHSConstants.ERROR_MESSAGE);
                liErrorCode = HHSConstants.INT_ONE;
            }else{
                PortletSession loSession = aoResourceRequest.getPortletSession();
                P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
                        ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
                LOG_OBJECT.Debug("Check ----------------------------------------------------------------------- \n"  + 
                "\nlsBudgetId"  + lsBudgetId + "\nlsContractId"  + lsContractId + "\nlsBudgetType"  + lsBudgetType +
                "\nlabcContractId1:" );

                Channel loChannelObj = new Channel();

                //loChannelObj.setData(HHSConstants.BUDGET_ID_KEY, lsBudgetId);
                loChannelObj.setData(HHSConstants.CONTRACT_ID_KEY, lsContractId);
                loChannelObj.setData(HHSConstants.AS_BUDGET_TYPE_ID, HHSConstants.BUDGET_UPDATE_TYPE_VAL);
                loChannelObj.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
                loChannelObj.setData(HHSConstants.AS_USER_ID, lsUserId);

                String  loData = HHSConstants.BUDGET_UPDATE_DELETION_AUDIT
                                    .replace(HHSConstants.STATIC_PARAM_FISCAL_YEARS, lsFiscalYears)
                                    .replace(HHSConstants.STATIC_PARAM_CONTRACT_ID, lsContractId);
                HHSUtil.addAuditDataToChannel(loChannelObj, HHSConstants.TASK_REMOVAL, HHSConstants.BUDGET_UPDATE_DELETION, 
                        loData + lsDelBudgetUpdateComment
                        , HHSConstants.CONTRACT, lsContractId, lsUserId, HHSConstants.ACCELERATOR_AUDIT, HHSConstants.AUDIT_BEAN);
                
                HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.TERMINATE_CONTRACT_BUDGET_UPDATE_TASK);
                
                /* [Start] QC 9490 R 8.4.0 Display confirmation message after Budget Update has been deleted successfully */
                LOG_OBJECT.Debug("Budget Update(s) have been Deleted! ");
                liErrorCode = HHSConstants.INT_ZERO;
                aoResourceRequest.getPortletSession().setAttribute(
                							HHSConstants.SUCCESS_MESSAGE,
                							PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
                									HHSConstants.DELETE_BUDGET_UPDATE_STATUS), PortletSession.APPLICATION_SCOPE);									
                /* [End] QC 9490 R 8.4.0 Display confirmation message after Budget Update has been deleted successfully */
                
            }
        } 
        catch (ApplicationException loEx)
        {
            liErrorCode = HHSConstants.INT_ONE;
            lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
            LOG_OBJECT.Error("Exception occured in cancelContract ", loEx);

        }
        catch (Exception loExe)
        {
            liErrorCode = HHSConstants.INT_ONE;
            lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
            LOG_OBJECT.Error("Exception occured in cancelContract ", loExe);
        }
        finally
        {
            returnMessage(aoResourceResponse, liErrorCode, lsErrorMsg);
        }
        

	}
    /*[End] R7.4.0  QC9008 add ability to remove Contract budget update*/

	/**
	 * This method is triggered when an Ajax call is made from budgetList.jsp on
	 * select of options from Action dropdown on Budget List Screen. The method
	 * returns the jsp content to be displayed in the overlay which appears on
	 * select of the dropdown options.
	 * 
	 * <ul>
	 * <li>1. We get the lsNextAction from request parameters</li>
	 * <ul>
	 * <li>If Advance Amount is > FY Total Remaining Amount, then display error,
	 * “! Advance Amount requested cannot exceed FY Total Remaining Amount.”</li>
	 * <li>If required fields are blank, display inline error message(s): “!
	 * This field is required.”</li>
	 * <li>Display an auto-generated Advance number</li>
	 * <ol>
	 * <li>The first three digits will be “ADV”</li>
	 * <li>The next two digits are the last two numbers of the current Fiscal
	 * Year</li>
	 * <li>The next two digits are the current calendar month</li>
	 * <li>The next five digits are a sequential number which starts from zero
	 * every month and increases by 1 every time an advance is requested for the
	 * selected budget</li>
	 * <li>For example, if a third advance was requested for a budget on
	 * 24-Dec-2012, then the advance number would be ADV131200003</li>
	 * </ol>
	 * </ul>
	 * <li>2.else if lsNextAction is initiateAdvance that means user has clicked
	 * on the Initiate Advance option from the Action Dropdown.
	 * <ul>
	 * <li>i)A Channel object loChannelObj is created.</li>
	 * <li>ii)budgetID and contractID are set in BudgetList bean object:
	 * aoBudgetBean.</li>
	 * <li>ii) fiscalYear and budgetID which we get from request parameters are
	 * set into loChannelObj which serve as input parameters to
	 * getAutoGeneratedAdvNo transaction.</li>
	 * <li>iv)We call TransactionManagerR2 to execute getAutoGeneratedAdvNo
	 * transaction which hits the database to get an autogenerated advance
	 * number : asAdvanceNumber .</li>
	 * <li>v)providerName, fiscalYear, dateOfLastUpdate, ctNumber , budgetID
	 * which we get from request parameters and asAdvanceNumber are set in
	 * loMapToRender Map.</li>
	 * <li>vi)loMapToRender is set in a ModelAndView object loModelAndViewAjax
	 * which renders the content of initiateAdvance jsp.</li>
	 * </ul>
	 * </li> <li>3.else if lsNextAction is confirmBudgetModification that means
	 * user has clicked on the Modify Budget option from the Action Dropdown.
	 * <ul>
	 * <li>A ModelAndView object loModelAndViewAjax which renders the content of
	 * confirmBudgetModification jsp.</li>
	 * 
	 * </ul>
	 * </li> <li>4.else if lsNextAction is cancelBudgetModification that means
	 * user has clicked on the Cancel Budget Modification option from the Action
	 * Dropdown.
	 * <ul>
	 * <li>i)A ModelAndView object loModelAndViewAjax which renders the content
	 * of cancelBudgetModification jsp.</li>
	 * </ul>
	 * </li> </ul>
	 * @param aoResourceRequest ResourceRequest as input
	 * @param aoResourceResponse ResourceResponse as input
	 * @return loModelAndViewAjax ModelAndViewAjax as output
	 * @throws ApplicationException Application Exception thrown in case any
	 *             query fails
	 * 
	 */
	@ResourceMapping("selectOverayContent")
	protected ModelAndView getOverlayContent(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{   
		ModelAndView loModelAndViewAjax = new ModelAndView();
		try
		{   LOG_OBJECT.Debug("======getOverlayContent");
			String lsNextAction = aoResourceRequest.getParameter(ApplicationConstants.NEXT_ACTION);
			if (null != lsNextAction)
			{
				if (lsNextAction.equalsIgnoreCase(HHSConstants.CBL_CANCEL_MODIFICATION))
				{
					CBGridBean loCBGridBean = new CBGridBean();
					String lsBudgetId = aoResourceRequest.getParameter(HHSConstants.BUDGET_ID_WORKFLOW);
					String lsContractId = aoResourceRequest.getParameter(HHSConstants.CLC_CONTRACT_ID);
					String lsFiscalYearId = aoResourceRequest.getParameter(HHSConstants.CLC_FISCAL_YEAR_ID);

					loCBGridBean.setContractID(lsContractId);
					loCBGridBean.setContractBudgetID(lsBudgetId);
					loCBGridBean.setFiscalYearID(lsFiscalYearId);
					loCBGridBean.setBudgetTypeId(HHSConstants.THREE);
					PortletSessionHandler.setAttribute(loCBGridBean, aoResourceRequest,
							HHSConstants.CBGRIDBEAN_IN_SESSION);

					loModelAndViewAjax = new ModelAndView(HHSConstants.CBL_CANCEL_BUDGET_MODIFICATION,
							HHSConstants.CANCEL_MODIFICATION_BUDGET_ID,
							aoResourceRequest.getParameter(HHSConstants.BUDGET_ID_WORKFLOW));

				}
				else if (lsNextAction.equalsIgnoreCase(HHSConstants.CBL_REQUEST_ADV)
						|| lsNextAction.equalsIgnoreCase(HHSConstants.CBL_INITIATE_ADVANCE))
				{
					BudgetAdvanceBean loBudgetAdvanceBean = fetchRequestAdvanceInfo(aoResourceRequest);
					PortletSession loPortletSession = aoResourceRequest.getPortletSession();
					String lsOrgnizationType = (String) loPortletSession.getAttribute(
							ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);

					aoResourceRequest.setAttribute(HHSConstants.ORGTYPE, lsOrgnizationType);
					loBudgetAdvanceBean.setBudgetId((String) aoResourceRequest
							.getParameter(HHSConstants.BUDGET_ID_WORKFLOW));
					loBudgetAdvanceBean.setFiscalYear(loBudgetAdvanceBean.getFiscalYear().substring(2));
					loModelAndViewAjax = new ModelAndView(HHSConstants.CBL_BUDGET_ADVANCE,
							HHSConstants.CBL_BUDGET_ADVANCE_BEAN, loBudgetAdvanceBean);
				}
				else if (lsNextAction.equalsIgnoreCase(HHSConstants.MODIFY_BUDGET))
				{
					String lsBudgetId = aoResourceRequest.getParameter(HHSConstants.BUDGET_ID_WORKFLOW);
					String lsContractId = aoResourceRequest.getParameter(HHSConstants.CLC_CONTRACT_ID);
					String lsBudgetType = aoResourceRequest.getParameter(HHSConstants.BUDGET_TYPE);
					String lsFiscalYearId = aoResourceRequest.getParameter(HHSConstants.CLC_FISCAL_YEAR_ID);
					aoResourceRequest.setAttribute(HHSConstants.BUDGET_ID_KEY, (null != lsBudgetId ? lsBudgetId : ' '));
					aoResourceRequest.setAttribute(HHSConstants.CONTRACT_ID_KEY, (null != lsContractId ? lsContractId : ' '));
					aoResourceRequest.setAttribute(HHSConstants.BUDGET_TYPE, (null != lsBudgetType ? lsBudgetType : ' '));
					aoResourceRequest.setAttribute(HHSConstants.CLC_FISCAL_YEAR_ID, (null != lsFiscalYearId ? lsFiscalYearId : ' '));
					loModelAndViewAjax = new ModelAndView(HHSConstants.CBL_CONFIRM_BUDGET_MODIFICATION);
					
				}
				/*[Start] R7.4.0  QC9008 add ability to remove Contract budget update*/
				else if(lsNextAction.equalsIgnoreCase(HHSConstants.DELETE_BUDGET_UPDATE))
				{
                    String lsBudgetId = aoResourceRequest.getParameter(HHSConstants.BUDGET_ID_WORKFLOW);
                    String lsContractId = aoResourceRequest.getParameter(HHSConstants.CLC_CONTRACT_ID);
                    String lsBudgetType = aoResourceRequest.getParameter(HHSConstants.BUDGET_TYPE);
                    String lsFiscalYearId = aoResourceRequest.getParameter(HHSConstants.CLC_FISCAL_YEAR_ID);
                    
                    aoResourceRequest.setAttribute(HHSConstants.BUDGET_ID_KEY, (null != lsBudgetId ? lsBudgetId : ' '));
                    aoResourceRequest.setAttribute(HHSConstants.CONTRACT_ID_KEY, (null != lsContractId ? lsContractId : ' '));
                    aoResourceRequest.setAttribute(HHSConstants.BUDGET_TYPE, (null != lsBudgetType ? lsBudgetType : ' '));
                    aoResourceRequest.setAttribute(HHSConstants.CLC_FISCAL_YEAR_ID, (null != lsFiscalYearId ? lsFiscalYearId : ' '));
                       
                    Channel loChannelObj = new Channel();
	                loChannelObj.setData(HHSConstants.BUDGET_ID_KEY, lsBudgetId);
	                loChannelObj.setData(HHSConstants.CONTRACT_ID_KEY, lsContractId);
	                loChannelObj.setData(HHSConstants.AS_BUDGET_TYPE_ID, HHSConstants.BUDGET_UPDATE_TYPE_VAL);
	                    
	                /*[Start] R 8.4.0  QC9008 add ability to remove Contract budget update*/
	
	                HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.PULL_BUDGET_UPDATE_SUMMARY);
	                List<BudgetList>  loBudgetList = ( List<BudgetList>) loChannelObj.getData(HHSConstants.LO_BUDGET_LIST);
	                if( loBudgetList == null || loBudgetList.isEmpty() || loBudgetList.size() > 1 )
	                    {
	                        aoResourceRequest.setAttribute(HHSConstants.LO_BUDGET_LIST, new BudgetList());
	                    } else {
	                        aoResourceRequest.setAttribute(HHSConstants.LO_BUDGET_LIST, loBudgetList.get(0));
	                    }
	                    loModelAndViewAjax = new ModelAndView(HHSConstants.CBL_CONFIRM_DEL_BUDGET_UPDATE);
                    }
                   	/*[End] R7.4.0  QC9008 add ability to remove Contract budget update*/
    
			}
		}
		catch (ApplicationException aoExp)
		{
			setExceptionMessageInResponse(aoResourceRequest);
			LOG_OBJECT.Error("Error occured while performing action ", aoExp);
			aoResourceRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
					HHSConstants.ERROR_WHILE_PROCESSING_REQUEST);
			aoResourceRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
		return loModelAndViewAjax;
	}

	/**
	 * Provider users will be prompted to confirm that they want to continue
	 * with the budget modification or not. This method redirects the Provider
	 * users to Budget Modification Screen if there is no error found. If a
	 * budget can not be modified then, user will stay on confirm budget
	 * modification popup and respective error will be shown to the user. <li>
	 * The transaction used: getModifyBudgetFeasibility</li>
	 * @param aoRequest Resource Request
	 * @param aoResponse Resource Response
	 * @return loModelAndViewAjax
	 */
	@ResourceMapping("createButtonLink")
	protected ModelAndView renderBudgetModification(ResourceRequest aoRequest, ResourceResponse aoResponse)
	{
		ModelAndView loModelAndViewAjax = null;
		PortletSession loSession = aoRequest.getPortletSession();
		Channel loChannelObj = new Channel();
		P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		loChannelObj.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
		loChannelObj.setData(HHSConstants.BUDGET_ID_KEY, aoRequest.getParameter(HHSConstants.BUDGET_ID_KEY));
		loChannelObj.setData(HHSConstants.CONTRACT_ID_KEY, aoRequest.getParameter(HHSConstants.CONTRACT_ID_KEY));
		try
		{
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.CBL_GET_MODIFY_BUDGET_FEASIBILITY);
			aoRequest.setAttribute(HHSConstants.CBL_AS_ERROR, (String) loChannelObj.getData(HHSConstants.LS_OUTPUT));
		}
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error(HHSConstants.CBL_ERROR_OCCURED_WHILE_PROCESSING_BUDGETS, loExp);
		}
		loModelAndViewAjax = new ModelAndView(HHSConstants.CBL_CONFIRM_BUDGET_MODIFICATION);
		return loModelAndViewAjax;
	}

	/**
	 * This method is triggered from Cancel Budget Modification screen when the
	 * user selects the 'Cancel Modification' button.
	 * <ul>
	 * <li>i)A Channel object loChannelObj is created.</li>
	 * <li>ii)BudgetId and AuthenticationBean are set in channel object</li>
	 * <li>iii)cancelModificationBudget transaction is called</li>
	 * <li>iv)Initially authenticateLoginUser method will be called to validate
	 * user name and password.
	 * <li>vi)If authenticateLoginUser method returns false i.e
	 * username/password validation fails, then an error message is displayed on
	 * the page.</li>
	 * <li>vii)If authenticateLoginUser method returns true, then call
	 * cancelModificationBudget method to cancel the budget in db and return the
	 * lbCancelModificationBudgetStatus flag which will show whether
	 * modification budget is de-activated or not.</li>
	 * <li>viii)If cancelModificationBudget flag is true, call the
	 * fetchWorkflowIdForBudget method to fetch workflowid for the budget.</li>
	 * <li>vv)Call the terminateWorkflow method of TaskService class to
	 * terminate the workflow associated with cancelled modification budget.</li>
	 * 
	 * <li>The transaction used: cancelModificationBudget</li>
	 * 
	 * @param aoAuthenticationBean AuthenticationBean
	 * @param aoResourceRequest ResourceRequest
	 * @param aoResourceResponse ResourceResponse
	 * @throws ApplicationException Application Exception thrown in case any
	 *             query fails
	 */

	@ResourceMapping(HHSConstants.CBL_CANCEL_BUDGET_MODIFICATION)
	protected void actionCancelBudgetModification(
			@ModelAttribute("AuthenticationBean") AuthenticationBean aoAuthenticationBean,
			ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse) throws ApplicationException
	{
		int liErrorCode = 0;
		String lsErrorMsg = HHSConstants.EMPTY_STRING;
		boolean lbUserValidation = false;

		PortletSession loSession = aoResourceRequest.getPortletSession();
		Channel loChannelObj = new Channel();
		Object loUserSession = (P8UserSession) loSession.getAttribute(ApplicationConstants.FILENET_SESSION_OBJECT,
				PortletSession.APPLICATION_SCOPE);

		loChannelObj.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
		loChannelObj.setData(HHSConstants.BUDGET_ID_KEY,
				aoResourceRequest.getParameter(HHSConstants.CANCEL_MODIFICATION_BUDGET_ID));

		String lsUserEmailId = aoAuthenticationBean.getUserName();
		String lsPassword = aoAuthenticationBean.getPassword();

		/*[Start] R9.6.0 QC9605  */
		LOG_OBJECT.Debug("[Cancel Budget Mod] by " + aoAuthenticationBean.getUserName() );
		/*[End] R9.6.0 QC9605  */

		try
		{
			Map loValidateMap = validateUser(lsUserEmailId, lsPassword, aoResourceRequest);
			lbUserValidation = (Boolean) loValidateMap.get(HHSConstants.IS_VALID_USER);
			if (!lbUserValidation)
			{
				liErrorCode = 1;
				lsErrorMsg = (String) loValidateMap.get(HHSConstants.ERROR_MESSAGE);

			}
			else
			{
				HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.TRAN_CANCEL_MODIFICATION_BUDGET);
				Boolean loCancelModificationBudgetStatus = (Boolean) loChannelObj
						.getData(HHSConstants.CANCEL_MODIFICATION_BUDGET_STATUS);
				boolean lbTerminationFlag = (Boolean) loChannelObj.getData(HHSConstants.TERMINATION_FLAG);
				if (!loCancelModificationBudgetStatus)
				{
					liErrorCode = 1;
					lsErrorMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSConstants.CANCEL_BUDGET_MODIFICATION_ERROR);
				}
				else if (!lbTerminationFlag)
				{
					liErrorCode = 1;
					lsErrorMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSConstants.WORKFLOW_TERMINATION_ERROR);
				}
				else
				{
					aoResourceRequest.getPortletSession().setAttribute(
							HHSConstants.SUCCESS_MESSAGE,
							PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
									HHSConstants.CANCEL_BUDGET_MODIFICATION_SUCCESS), PortletSession.APPLICATION_SCOPE);
				}
			}

		}
		catch (ApplicationException aoAppEx)
		{
			liErrorCode = 1;
			lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error(HHSConstants.CBL_EXCEPTION_OCCURED_IN_ACTION_CANCEL_BUDGET_MODIFICATION, aoAppEx);

		}
		catch (Exception aoExe)
		{
			liErrorCode = 1;
			lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error(HHSConstants.CBL_EXCEPTION_OCCURED_IN_ACTION_CANCEL_BUDGET_MODIFICATION, aoExe);
		}
		finally
		{
			returnMessage(aoResourceResponse, liErrorCode, lsErrorMsg);
		}

	}

	/**
	 * This method will get the Epin list from the cache when user type three
	 * characters using getEpinList method defined in basecontroller.
	 * 
	 * @param aoRequest a ResourceRequest Object
	 * @param aoResponse a ResourceResponse Object
	 */
	@ResourceMapping("getEpinListResourceUrl")
	public void getEpinListResourceRequest(ResourceRequest aoRequest, ResourceResponse aoResponse)
	{
		try
		{
			getEpinList(aoRequest, aoResponse);
		}
		catch (ApplicationException aoAppExe)
		{
			LOG_OBJECT.Error(HHSConstants.CBL_APPLICATION_EXCEPTION_GET_EPIN_LIST, aoAppExe);
		}
		catch (Exception aoExe)
		{
			LOG_OBJECT.Error(HHSConstants.CBL_ERROR_OCCURED_IN_GET_EPIN_LIST_RESOURCE_REQUEST, aoExe);
		}
	}

	/**
	 * This method will get the Contract No list from cache when user types
	 * three characters on Ct # textbox on contract filter screen.
	 * 
	 * <ul>
	 * <li>1.Get the String entered by the user from request</li>
	 * <li>2. Get the contractNoQueryId parameter from Request and assign it to
	 * contractNoQueryId</li>
	 * <li>3. Based on the value of contractNoQueryId, get the contract no list
	 * from cache</li>
	 * <li>4. if cache does not contain anything, call the service method to get
	 * the epin list from database.</li>
	 * <li>The transaction used: fetchContractNoList</li>
	 * </ul>
	 * 
	 * @param aoRequest a ResourceRequest Object
	 * @param aoResponse a ResourceResponse Object
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             ApplicationException
	 */
	@SuppressWarnings("unchecked")
	@ResourceMapping("getContractNoListResourceUrl")
	public void getContractNoListResourceRequest(ResourceRequest aoRequest, ResourceResponse aoResponse)
			throws ApplicationException
	{
		String lsPartialUoDenom = HHSConstants.EMPTY_STRING;
		lsPartialUoDenom = aoRequest.getParameter(HHSConstants.QUERY);
		final int liMinLength = 3;
		List<String> loContractNoList = new ArrayList<String>();
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID);
		UserThreadLocal.setUser(lsUserId);
		String lsBudgetNoQueryId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.CBL_BUDGET_NO_QUERY_ID);
		// Get CT# based on inputs passed
		String lsQueryStringFromReq = aoRequest.getParameter(HHSConstants.QUERY);
		if (null != lsBudgetNoQueryId)
		{
			Channel loChannel = null;
			if (HHSConstants.CBL_FETCH_BUDGET_NO_LIST.equalsIgnoreCase(lsBudgetNoQueryId))
			{
				loChannel = new Channel();
				loChannel.setData(HHSConstants.QUERY_ID, lsBudgetNoQueryId);
				loChannel.setData(HHSConstants.TO_SEARCH, lsQueryStringFromReq);
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_CONTRACT_NO_LIST);
				loContractNoList = (List<String>) loChannel.getData(HHSConstants.AO_CONTRACT_NO_LIST);
			}
		}
		if ((lsPartialUoDenom != null) && (lsPartialUoDenom.length() >= liMinLength))
		{
			PrintWriter loOut = null;
			try
			{
				aoResponse.setContentType(HHSConstants.APPLICATION_JSON);
				loOut = aoResponse.getWriter();
				final String lsOutputJSONaoResponse = HHSUtil
						.generateDelimitedResponse(loContractNoList, lsPartialUoDenom, liMinLength).toString().trim();
				loOut.print(lsOutputJSONaoResponse);
				loOut.flush();
			}
			catch (IOException aoExp)
			{
				LOG_OBJECT.Error(HHSConstants.CBL_IO_EXCEPTION_SEARCHING_PROVIDERS, aoExp);
				throw new ApplicationException(HHSConstants.CBL_IO_EXCEPTION_SEARCHING_PROVIDERS, aoExp);
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
	}

	/**
	 * The method fetches the data which needs to be shown on the screen when
	 * Provider request or Agency initiate an advance request.
	 * 
	 * <ul>
	 * <li>1.Get the budget id which is getting passed from budget list screen</li>
	 * <li>2. Set budget Id into channel.</li>
	 * <li>3.Call the fetchRequestAdvanceInfo Transaction</li>
	 * <li>4. Transaction return the bean of type BudgetAdvanceBean which holds
	 * data(ContractId,Provider Name, Fiscal Year,AdvanceRequestDate)
	 * <ul>
	 * 
	 * @param aoResourceRequest ResourceRequest
	 * @return loBudgetAdvanceBean BudgetAdvanceBean
	 * @throws ApplicationException Application Exception thrown in case any
	 *             query fails
	 */

	private BudgetAdvanceBean fetchRequestAdvanceInfo(ResourceRequest aoResourceRequest) throws ApplicationException
	{
		Channel loChannel = new Channel();
		BudgetAdvanceBean loBudgetAdvanceBean = null;
		loChannel.setData(HHSConstants.BUDGET_ID_KEY, aoResourceRequest.getParameter(HHSConstants.BUDGET_ID_WORKFLOW));
		try
		{
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.CBL_FETCH_REQUEST_ADVANCE_INFO);
			loBudgetAdvanceBean = (BudgetAdvanceBean) loChannel.getData(HHSConstants.CBL_LO_BUDGET_ADVANCE_BEAN);
		}
		catch (ApplicationException loEx)
		{
			throw new ApplicationException(HHSConstants.CBL_EXCEPTION_REQUEST_ADVANCE_INFO + loEx);

		}

		return loBudgetAdvanceBean;

	}

	/**
	 * This method print the error using print Writer object
	 * @param aoResourceResponse ResourceResponse as input
	 * @param aiErrorCode error code as input
	 * @param asErrorMsg error msg as input
	 */
	private void returnMessage(ResourceResponse aoResourceResponse, int aiErrorCode, String asErrorMsg)
	{
		PrintWriter loOut = null;
		try
		{
			aoResourceResponse.setContentType(HHSConstants.APPLICATION_JSON);
			loOut = aoResourceResponse.getWriter();
			loOut.print(HHSConstants.ERROR_1 + aiErrorCode + HHSConstants.MESSAGE_1 + asErrorMsg
					+ HHSConstants.CLOSING_BRACE_1);
			
		}
		catch (Exception aoExe)
		{
			LOG_OBJECT.Error(HHSConstants.CBL_EXCEPTION_OCCURED_WHILE_RETURNING_MESSAGE, aoExe);
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
	 * This method handles view Contract Budget from Budget List for provider,
	 * accelerator and agency users
	 * 
	 * <ul>
	 * <li>Get the contractId from request</li>
	 * <li>Get the budgetId from request</li>
	 * <li>Redirect user to Contract Budget render action</li>
	 * 
	 * <li>The transaction used: insertModificationBudgetDetails</li>
	 * <li>The transaction used: fetchModifiedBudgetId</li>
	 * <li>The transaction used: aoModifiedBudgetId</li>
	 * <li>The transaction used: insertModificationSubBudgetDetails</li>
	 * </ul>
	 * 
	 * @param aoRequest an Action Request object
	 * @param aoResponse an Action Response object
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	@ActionMapping(params = "submit_action=viewContractBudget")
	protected void actionViewProcurementSummary(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		try
		{
			String lsProcurementSummaryPath = HHSConstants.EMPTY_STRING;
			String lsModificationInsert = aoRequest.getParameter(HHSConstants.LOAD_MODIFICATION_FIRST);
			String loRemoveSessionValue = aoRequest.getParameter(HHSR5Constants.REMOVE_SESSION_VALUE);
			// Emergency Build 4.0.1 defect 8358
			if (loRemoveSessionValue != null && loRemoveSessionValue.equalsIgnoreCase(HHSConstants.TRUE))
			{
				aoRequest.getPortletSession().removeAttribute(HHSConstants.CONTRACT_SESSION_BEAN);
				aoRequest.getPortletSession().removeAttribute(HHSConstants.INVOICE_SESSION_BEAN);
				aoRequest.getPortletSession().removeAttribute(HHSConstants.PAYMENT_SESSION_BEAN);
				aoRequest.getPortletSession().removeAttribute(HHSConstants.AMENDED_CONTRACT_SESSION_BEAN);
			}
			// Emergency Build 4.0.1 defect 8358
			if (lsModificationInsert.equalsIgnoreCase(HHSConstants.TRUE))
			{
				Channel loChannelObj = new Channel();

				ContractBudgetBean loContractBudgetBean = new ContractBudgetBean();
				loContractBudgetBean.setBudgetfiscalYear(aoRequest.getParameter(HHSConstants.FISCAL_YEAR_ID));
				loContractBudgetBean.setBudgetTypeId(HHSConstants.INT_THREE);
				loContractBudgetBean.setContractId(aoRequest.getParameter(HHSConstants.CONTRACT_ID_KEY));
				String lsUserId = String.valueOf(aoRequest.getPortletSession().getAttribute(
						ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE));
				loContractBudgetBean.setCreatedByUserId(lsUserId);
				loContractBudgetBean.setModifiedByUserId(lsUserId);
				loContractBudgetBean.setTotalbudgetAmount(HHSConstants.STRING_ZERO);
				loContractBudgetBean.setStatusId(HHSConstants.BUDGET_PENDING_SUBMISSION_STATUS_ID);
				loContractBudgetBean.setParentId(aoRequest.getParameter(HHSConstants.BUDGET_ID_KEY));
				loChannelObj.setData(HHSConstants.AO_CONTRACT_BUDGET_BEAN, loContractBudgetBean);
				HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.INSERT_MODIFICATION_BUDGET_DETAILS);
				Integer loRowsAdded = (Integer) loChannelObj.getData(HHSConstants.AO_ADD_BUDGET);
				String lsModifiedBudgetId = HHSConstants.EMPTY_STRING;
				if (loRowsAdded > HHSConstants.INT_ZERO)
				{
					Channel loChannelObj1 = new Channel();
					HashMap<String, String> loHashMap = new HashMap<String, String>();
					loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, aoRequest.getParameter(HHSConstants.BUDGET_ID_KEY));
					loChannelObj1.setData(HHSConstants.AO_HASH_MAP, loHashMap);
					HHSTransactionManager.executeTransaction(loChannelObj1, HHSConstants.FETCH_MODIFIED_BUDGET_ID);
					lsModifiedBudgetId = (String) loChannelObj1.getData(HHSConstants.AO_MODIFIED_BUDGET_ID);
					//Added in R7 to update is_old_pi flag
					HHSTransactionManager.executeTransaction(loChannelObj1,HHSR5Constants.UPDATE_IS_OLD_PI_FLAG_FOR_MODIFICATION );
					//R7 changes end
					HHSTransactionManager.executeTransaction(loChannelObj1,
							HHSConstants.FETCH_SUB_BUDGET_MODIFICATION_SUMMARY);
					List<CBGridBean> loSubBudgetList = (List<CBGridBean>) loChannelObj1
							.getData(HHSConstants.SUB_BUDGET_LIST);
					if (loSubBudgetList.size() > HHSConstants.INT_ZERO)
					{
						for (CBGridBean loGridBean : loSubBudgetList)
						{
							Channel loChannelObj2 = new Channel();
							HashMap<String, String> loHashMap1 = new HashMap<String, String>();
							loHashMap1.put(HHSConstants.CREATED_BY_USER_ID, lsUserId);
							loHashMap1.put(HHSConstants.BUDGET_ID_WORKFLOW, lsModifiedBudgetId);
							loHashMap1.put(HHSConstants.BMC_BUDGET_FISCAL_YEAR,
									aoRequest.getParameter(HHSConstants.FISCAL_YEAR_ID));
							loHashMap1.put(HHSConstants.PARENT_ID, loGridBean.getSubBudgetID());
							loChannelObj2.setData(HHSConstants.AO_HASH_MAP, loHashMap1);
							HHSTransactionManager.executeTransaction(loChannelObj2,
									HHSConstants.INSERT_MODIFICATION_SUB_BUDGET_DETAILS);
						}
					}
				}

				lsProcurementSummaryPath = aoRequest.getScheme() + HHSConstants.NOTIFICATION_HREF_1
						+ aoRequest.getServerName() + HHSConstants.COLON + aoRequest.getServerPort()
						+ aoRequest.getContextPath() + ApplicationConstants.PORTAL_URL
						+ HHSConstants.PAGE_LABEL_PORTLET_URL_MOD + HHSConstants.CONTRACT_ID_URL
						+ aoRequest.getParameter(HHSConstants.CONTRACT_ID_KEY) + HHSConstants.BUDGET_ID_URL
						+ lsModifiedBudgetId + HHSConstants.FISCAL_YEAR_ID_URL
						+ aoRequest.getParameter(HHSConstants.FISCAL_YEAR_ID) + HHSConstants.BUDGET_TYPE_URL
						+ HHSConstants.BUDGET_TYPE2;
				 /** [Start] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
				//aoResponse.sendRedirect(lsProcurementSummaryPath);
				aoResponse.sendRedirect(HHSUtil.sanitizeCarriageReturns(lsProcurementSummaryPath));
				 /** [End] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
			}
			else if (lsModificationInsert.equalsIgnoreCase(HHSConstants.FALSE))
			{
				modificationInsertFalse(aoRequest, aoResponse);
			}
			else
			{
				aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.CBL_BUDGET_LIST_ACTION);
			}
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error(HHSConstants.CBL_EXCEPTION_CONTRACT_BUDGET_DETAILS, loExp);
			String lsErrorMsg = HHSConstants.ERROR_WHILE_PROCESSING_REQUEST;
			aoRequest.setAttribute(HHSConstants.ERROR_MESSAGE_BUDGET_LIST, lsErrorMsg);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
	}

	/**
	 * This method check if modification insert is false.
	 * @param aoRequest request as input
	 * @param aoResponse Response as input
	 * @throws IOException IOException in case of exception Method modified in
	 *             R6. Added for correct Header
	 */
	private void modificationInsertFalse(ActionRequest aoRequest, ActionResponse aoResponse) throws IOException
	{
		String lsProcurementSummaryPath;
		if (aoRequest.getParameter(HHSConstants.BUDGET_TYPE).equalsIgnoreCase(HHSConstants.BUDGET_TYPE3))
		{
			lsProcurementSummaryPath = aoRequest.getScheme() + HHSConstants.NOTIFICATION_HREF_1
					+ aoRequest.getServerName() + HHSConstants.COLON + aoRequest.getServerPort()
					+ aoRequest.getContextPath() + ApplicationConstants.PORTAL_URL
					+ HHSConstants.PAGE_LABEL_PORTLET_URL + HHSConstants.CONTRACT_ID_URL
					+ aoRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW) + HHSConstants.BUDGET_ID_URL
					+ aoRequest.getParameter(HHSConstants.BUDGET_ID_WORKFLOW) + HHSConstants.FISCAL_YEAR_ID_URL
					+ aoRequest.getParameter(HHSConstants.CLC_FISCAL_YEAR_ID) + HHSConstants.BUDGET_TYPE_URL
					+ aoRequest.getParameter(HHSConstants.BUDGET_TYPE)
					// Start: review comment r6
					+ HHSR5Constants.FINANCIAL_BUDGET_TAB;
			// End: review comment r6
			/** [Start] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
			//aoResponse.sendRedirect(lsProcurementSummaryPath);
			aoResponse.sendRedirect(HHSUtil.sanitizeCarriageReturns(lsProcurementSummaryPath));
			/** [End] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
		}
		else if ((aoRequest.getParameter(HHSConstants.BUDGET_TYPE).equalsIgnoreCase(HHSConstants.BUDGET_TYPE2))
				|| (aoRequest.getParameter(HHSConstants.BUDGET_TYPE).equalsIgnoreCase(HHSConstants.BUDGET_TYPE4)))
		{
			lsProcurementSummaryPath = aoRequest.getScheme() + HHSConstants.NOTIFICATION_HREF_1
					+ aoRequest.getServerName() + HHSConstants.COLON + aoRequest.getServerPort()
					+ aoRequest.getContextPath() + ApplicationConstants.PORTAL_URL
					+ HHSConstants.PAGE_LABEL_PORTLET_URL_MOD + HHSConstants.CONTRACT_ID_URL
					+ aoRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW) + HHSConstants.CT_ID_URL
					+ aoRequest.getParameter(HHSConstants.CT_ID) + HHSConstants.BUDGET_ID_URL
					+ aoRequest.getParameter(HHSConstants.BUDGET_ID_WORKFLOW) + HHSConstants.FISCAL_YEAR_ID_URL
					+ aoRequest.getParameter(HHSConstants.CLC_FISCAL_YEAR_ID) + HHSConstants.BUDGET_TYPE_URL
					+ aoRequest.getParameter(HHSConstants.BUDGET_TYPE);
			/** [Start] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
			//aoResponse.sendRedirect(lsProcurementSummaryPath);
			aoResponse.sendRedirect(HHSUtil.sanitizeCarriageReturns(lsProcurementSummaryPath));
			/** [End] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
		}
		else if (aoRequest.getParameter(HHSConstants.BUDGET_TYPE).equalsIgnoreCase(HHSConstants.BUDGET_TYPE1))
		{
			lsProcurementSummaryPath = aoRequest.getScheme() + HHSConstants.NOTIFICATION_HREF_1
					+ aoRequest.getServerName() + HHSConstants.COLON + aoRequest.getServerPort()
					+ aoRequest.getContextPath() + ApplicationConstants.PORTAL_URL
					+ HHSConstants.PAGE_LABEL_PORTAL_CONTRACT_AMENDMENT_PAGE_URL + HHSConstants.CONTRACT_ID_URL
					+ aoRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW) + HHSConstants.BUDGET_ID_URL
					+ aoRequest.getParameter(HHSConstants.BUDGET_ID_WORKFLOW) + HHSConstants.FISCAL_YEAR_ID_URL
					+ aoRequest.getParameter(HHSConstants.CLC_FISCAL_YEAR_ID) + HHSConstants.BUDGET_TYPE_URL
					+ aoRequest.getParameter(HHSConstants.BUDGET_TYPE);
			/** [Start] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
			//aoResponse.sendRedirect(lsProcurementSummaryPath);
			aoResponse.sendRedirect(HHSUtil.sanitizeCarriageReturns(lsProcurementSummaryPath));
			/** [End] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
		}
	}

	/**
	 * This method is triggered when the provider or agency user clicks on
	 * request advance/initiate advance button.
	 * 
	 * <ul>
	 * <li>1)updateAdvanceDetails transaction is triggered which generated an
	 * advance number and inserts a new row into budget advance table for the
	 * newly requested/inititaed advance.
	 * <li>2)WF307 – Advance Request Review task is triggered.
	 * <li>2)WorkflowId and Task status is inserted in Payment table.
	 * <li>The transaction used: updateAdvanceDetails</li>
	 * @param aoBudgetAdvanceParam
	 * @param aoResult
	 * @param aoRequest
	 * @param aoResponse
	 * @param aoModel
	 * @param aoModelMap
	 * @return
	 * @throws ApplicationException
	 */
	@ResourceMapping(HHSConstants.CBL_REQUEST_ADVANCE)
	protected ModelAndView requestAdvanceAction(
			@ModelAttribute("BudgetAdvance") BudgetAdvanceBean aoBudgetAdvanceParam, BindingResult aoResult,
			ResourceRequest aoRequest, ResourceResponse aoResponse, Model aoModel, ModelMap aoModelMap)
			throws ApplicationException
	{   
		String lsLevelErrorMessage = HHSConstants.EMPTY_STRING;
		PrintWriter loOut = null;
		ModelAndView loModelAndView = null;
		aoResponse.setContentType(HHSConstants.TEXT_HTML);
		PortletSession loSession = aoRequest.getPortletSession();
		P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		HashMap<String, Object> loHmRequiredProps = new HashMap<String, Object>();
		String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsOrgnizationType = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
				PortletSession.APPLICATION_SCOPE);
		aoBudgetAdvanceParam.setOrgType(lsOrgnizationType);
		aoBudgetAdvanceParam.setUserId(lsUserId);
		aoBudgetAdvanceParam.setStatus(HHSConstants.ADVANCE_SUBMITTED_STATUS);
		Channel loChannel = new Channel();
		loChannel.setData(HHSConstants.CBL_AO_BUDGET_ADVANCE_BEAN, aoBudgetAdvanceParam);
		try
		{
			loOut = aoResponse.getWriter();
			// Release 3.2.0 enhancement 6262 show error if requested amount is
			// greater
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.CBL_FETCH_CONTRACT_AMOUNT_FOR_VALIDATION);
			String loBudgetAmount = (String) loChannel.getData(HHSConstants.AS_BUDGET_AMOUNT);
			Double loRequestedAmount = Double.parseDouble(aoBudgetAdvanceParam.getAdvAmntRequested().replace(
					HHSConstants.COMMA, HHSR5Constants.EMPTY_STRING));
			Double loBudgetAmountFetched = Double.parseDouble(loBudgetAmount);

			if (loBudgetAmountFetched >= loRequestedAmount)
			{
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.CBL_UPDATE_ADVANCE_DETAILS);
				Integer loBudgetAdvanceId = (Integer) loChannel.getData(HHSConstants.AI_CURRENT_SEQ);
				aoBudgetAdvanceParam.setBudgetAdvanceId(loBudgetAdvanceId.toString());

				loHmRequiredProps.put(HHSConstants.BUDGET_ADVANCE_ID, loBudgetAdvanceId.toString());
				loHmRequiredProps.put(HHSConstants.WORKFLOW_NAME, HHSConstants.WF_ADVANCE_REVIEW);
				loHmRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW, aoBudgetAdvanceParam.getBudgetId());
				loHmRequiredProps.put(HHSConstants.CONTRACT_ID_WORKFLOW, aoBudgetAdvanceParam.getContractId());
				loHmRequiredProps.put(HHSConstants.SUBMITTED_BY, lsUserId);
				// 08_Aug>> Adding BUdgetAdvance bean and OPeration Flag in
				// HashMap
				loHmRequiredProps.put(HHSConstants.PARAM_KEY_OPERATION, HHSConstants.ADVANCE);
				loHmRequiredProps.put(HHSConstants.CBL_AO_BUDGET_ADVANCE_BEAN, aoBudgetAdvanceParam);

				if (lsOrgnizationType.equalsIgnoreCase(HHSConstants.PROVIDER_ORG))
				{
					loHmRequiredProps.put(HHSConstants.AUDIT_PROVIDER_INITIATED_TASK, true);
				}
				else
				{
					loHmRequiredProps.put(HHSConstants.AUDIT_PROVIDER_INITIATED_TASK, false);
				}

				loChannel = new Channel();
				loChannel.setData(HHSConstants.LB_AUTH_STATUS_FLAG, true);
				loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
				loChannel.setData(HHSConstants.AO_HMWF_REQUIRED_PROPS, loHmRequiredProps);

				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.LAUNCH_ADVANCE_REVIEW_TASK);
			}
			else
			{
				lsLevelErrorMessage = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.ADVANCE_AMOUNT_GREATER_THAN_BUDGET);
			}
		}
		catch (ApplicationException loEx)
		{
			ApplicationException loAppEx = (ApplicationException) loEx.getRootCause();
			if (null != loAppEx)
			{
				lsLevelErrorMessage = (String) loAppEx.getContextData().get(HHSConstants.LEVEL_ERROR_MESSAGE);
			}
			if (null == lsLevelErrorMessage)
			{
				lsLevelErrorMessage = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			}
			LOG_OBJECT.Error(HHSConstants.CBL_APPLICATION_EXCEPTION_UPDATE_ADVANCE_DETAILS, loEx);
		}
		catch (Exception loEx)
		{
			lsLevelErrorMessage = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			throw new ApplicationException(HHSConstants.CBL_EXCEPTION_UPDATE_ADVANCE_DETAILS + loEx);
		}
		finally
		{
			catchTaskError(loOut, lsLevelErrorMessage);
		}
		return loModelAndView;

	}

	/**
	 * This method print the error using print Writer object
	 * 
	 * @param loOut
	 * @param asError
	 */
	private void catchTaskError(PrintWriter aoOut, String asError)
	{
		aoOut.print(asError);
		aoOut.flush();
		aoOut.close();
	}

	/**
	 * This method is used to display the Update Budget Template Overlay
	 * <ul>
	 * <li>1.Get the required view name from request</li>
	 * <ul>
	 * 
	 * @param aoResourceRequest ResourceRequest Object
	 * @param aoResourceResponse ResourceResponse Object
	 * @return loModelAndView ModelAndView Object
	 * @throws Exception an exception
	 */
	@ResourceMapping("BudgetCustomizedTabOverlay")
	public ModelAndView BudgetCustomizedTabOverlay(ResourceRequest aoResourceRequest,
			ResourceResponse aoResourceResponse) throws Exception
	{
		ModelAndView loModelAndView = null;
		aoResourceRequest.setAttribute(HHSConstants.ENTRY_TYPE_ID, HHSUtil.getEntryTypeDetail(
				aoResourceRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW),
				aoResourceRequest.getParameter(HHSConstants.BUDGET_ID_WORKFLOW), null, null,
				aoResourceRequest.getParameter(HHSConstants.CLC_FISCAL_YEAR_ID)));
		aoResourceRequest.setAttribute(HHSConstants.CONTRACT_ID_WORKFLOW,
				aoResourceRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW));
		aoResourceRequest.setAttribute(HHSConstants.BUDGET_ID_WORKFLOW,
				aoResourceRequest.getParameter(HHSConstants.BUDGET_ID_WORKFLOW));
		aoResourceRequest.setAttribute(HHSConstants.CLC_FISCAL_YEAR_ID,
				aoResourceRequest.getParameter(HHSConstants.CLC_FISCAL_YEAR_ID));
		loModelAndView = new ModelAndView(HHSConstants.UPDATE_BUDGET_TEMPLATE);
		return loModelAndView;
	}

	// Start R7 8644 for cancel and merge
	/**
	 * This method is used to display the CancelAndMerge Overlay
	 * <ul>
	 * <li>1.Get the required view name from request</li>
	 * <ul>
	 * 
	 * @param aoResourceRequest ResourceRequest Object
	 * @param aoResourceResponse ResourceResponse Object
	 * @return loModelAndView ModelAndView Object
	 * @throws Exception an exception
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@ResourceMapping("CancelAndMergeOverlay")
	public ModelAndView CancelAndMergeOverlay(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws Exception
	{
		ModelAndView loModelAndView = null;
		Channel loChannel = new Channel();

		boolean lbUserValidation = false;
		String lsErrorMsg = HHSConstants.EMPTY_STRING;
		int liErrorCode = HHSConstants.INT_ZERO;
		String lsCheckSubmit = HHSConstants.STRING_TRUE;
		PortletSession loSession = aoResourceRequest.getPortletSession();
		P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		String lsContractId = aoResourceRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW);
		String lsUserEmailId = aoResourceRequest.getParameter(HHSConstants.KEY_SESSION_USER_NAME);
		String lsPassword = aoResourceRequest.getParameter(HHSConstants.PASSWORD);
		String lsRelaunchNewFYTask = aoResourceRequest.getParameter(HHSR5Constants.IS_LAUNCH_NEW_FY_TASK);
		String lsbudgetId = aoResourceRequest.getParameter(HHSConstants.BUDGET_ID_WORKFLOW);
		String lsBudgetFiscalYear = aoResourceRequest.getParameter(HHSConstants.CLC_FISCAL_YEAR_ID);
		String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		try
		{
			if (lsUserEmailId != null || lsPassword != null)
			{

				Map loValidateMap = validateUser(lsUserEmailId, lsPassword, aoResourceRequest);
				lbUserValidation = (Boolean) loValidateMap.get(HHSConstants.IS_VALID_USER);
				if (!lbUserValidation)
				{
					lsErrorMsg = (String) loValidateMap.get(HHSConstants.ERROR_MESSAGE);
					liErrorCode = HHSConstants.INT_ONE;
				}
				else
				{
					loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
					lsContractId = aoResourceRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW);
					loChannel.setData(HHSConstants.CONTRACT_ID_KEY, lsContractId);
					// fetch base contract Id
					HHSTransactionManager.executeTransaction(loChannel, HHSConstants.BMC_FETCH_BASE_CONTRACT_ID,
							HHSConstants.TRANSACTION_ELEMENT);
					String lsBaseContractId = (String) loChannel.getData(HHSConstants.AS_BASE_CONTRACT_ID);
					loChannel.setData(HHSConstants.CONTRACT_ID_KEY, lsBaseContractId);
					String lsErrorCheck = HHSConstants.EMPTY_STRING;
					HashMap loErrorCheckMap = null;
					if (null != lsBaseContractId && !lsBaseContractId.equalsIgnoreCase(HHSR5Constants.EMPTY_STRING)
							&& null != lsRelaunchNewFYTask && lsRelaunchNewFYTask.equalsIgnoreCase(HHSR5Constants.TRUE))
					{
						// check error
						HHSTransactionManager.executeTransaction(loChannel, HHSConstants.NEW_FY_CONFIG_ERROR_CHECK);
						loErrorCheckMap = (HashMap) loChannel.getData(HHSConstants.LO_CONFIG_ERROR_CHECK);
						lsErrorCheck = (String) loErrorCheckMap.get(HHSConstants.CLC_ERROR_CHECK);
						lsErrorMsg = (String) loErrorCheckMap.get(HHSConstants.CLC_ERROR_MSG);
					}
					
					if ((null != lsErrorCheck && lsErrorCheck.equalsIgnoreCase(HHSConstants.SUCCESS))
							|| (lsRelaunchNewFYTask == null || lsRelaunchNewFYTask
									.equalsIgnoreCase(HHSR5Constants.FALSE)))
					{

						HashMap<String, String> loHashMap = new HashMap<String, String>();
						loHashMap.put(HHSConstants.CLC_FISCAL_YEAR_ID, lsBudgetFiscalYear);
						loHashMap.put(HHSConstants.CONTRACT_ID1, lsContractId);
						loHashMap.put(HHSConstants.BUDGET_ID, lsbudgetId);
						loHashMap.put(HHSConstants.ENTRY_TYPE_LIST, aoResourceRequest.getParameter(HHSConstants.ID));
						loHashMap.put(HHSConstants.CREATED_BY_USER_ID, lsUserId);
						loHashMap.put(HHSConstants.PUBLISHED, HHSConstants.ONE);
						loHashMap.put(HHSConstants.FISCAL_YEAR_ID, lsBudgetFiscalYear);
						loHashMap.put(HHSConstants.SUBMITTED_BY, lsUserId);
						loHashMap.put(HHSConstants.WORKFLOW_NAME, HHSConstants.WF_NEW_FY_CONFIGURATION);
						loChannel.setData(HHSConstants.AO_HASH_MAP, loHashMap);
						// delete
						HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.CANCEL_OUT_YEAR_AND_MERGE,
								HHSR5Constants.TRANSACTION_ELEMENT_R5);
						LOG_OBJECT.Info("Budget deleted for the requested Fiscal Year.");
						if (null != lsRelaunchNewFYTask && lsRelaunchNewFYTask.equalsIgnoreCase(HHSR5Constants.TRUE))
						{
							loHashMap.put(HHSConstants.CONTRACT_ID1, lsBaseContractId);
							loChannel.setData(HHSConstants.LB_AUTH_STATUS_FLAG, true);
							loChannel.setData(HHSConstants.CONTRACT_TYPE_ID_KEY, HHSR5Constants.ONE);
							loChannel.setData(HHSConstants.AS_USER_ID, lsUserId);
							loChannel.setData(HHSConstants.AO_HMWF_REQUIRED_PROPS, loHashMap);
							// launch New FY year
							HHSTransactionManager.executeTransaction(loChannel, HHSConstants.CONFIRM_NEW_FY_CONFIG);
							LOG_OBJECT.Info("New FY task launched");
						}
						aoResourceRequest.getPortletSession().setAttribute(
								HHSConstants.SUCCESS_MESSAGE,
								PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
										HHSConstants.MSG_SUCCESSFULL_CANCEL_AND_MERGE_REQUEST),
								PortletSession.APPLICATION_SCOPE);
					}
					else
					{
						liErrorCode = HHSConstants.INT_TWO;
					}
				}
			}
			else
			{
				aoResourceRequest.setAttribute(HHSConstants.CLC_FISCAL_YEAR_ID, lsBudgetFiscalYear);
				aoResourceRequest.setAttribute(HHSConstants.CONTRACT_ID1, lsContractId);
				loModelAndView = new ModelAndView(HHSConstants.CANCEL_AND_MERGE);
				return loModelAndView;

			}
		}
		catch (ApplicationException loEx)
		{
			liErrorCode = HHSConstants.INT_ONE;
			lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("Error occured while requesting for cancel and merge. ", loEx);
			loModelAndView = new ModelAndView(HHSConstants.CANCEL_AND_MERGE);
			return loModelAndView;

		}
		catch (Exception loExe)
		{
			liErrorCode = HHSConstants.INT_ONE;
			lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("Error occured while requesting for cancel and merge.", loExe);
			loModelAndView = new ModelAndView(HHSConstants.CANCEL_AND_MERGE);
			return loModelAndView;

		}
		returnMessage(aoResourceResponse, liErrorCode, lsErrorMsg);
		return loModelAndView;
	}

	// End

	/**
	 * This method is used to update the Update Budget Template on the basis of
	 * bugdetId, ContractId
	 * <ul>
	 * <li>1.Get the required view name from request</li>
	 * <li>The transaction used: updateBudgetTemplate</li>
	 * <li>This method has been added for R4</li>
	 * <ul>
	 * 
	 * @param aoResourceRequest ResourceRequest Object
	 * @param aoResourceResponse ResourceResponse Object
	 * @throws Exception an exception
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@ResourceMapping("UpdateBudgetTemplate")
	public void UpdateBudgetTemplate(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws Exception
	{
		if (null != aoResourceRequest.getParameter(HHSConstants.ID)
				&& !aoResourceRequest.getParameter(HHSConstants.ID).isEmpty())
		{
			HashMap loHashMap = new HashMap<String, String>();
			loHashMap.put(HHSConstants.CONTRACT_ID, aoResourceRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW));
			loHashMap.put(HHSConstants.BUDGET_ID, aoResourceRequest.getParameter(HHSConstants.BUDGET_ID_WORKFLOW));
			loHashMap.put(HHSConstants.ENTRY_TYPE_LIST, aoResourceRequest.getParameter(HHSConstants.ID));
			loHashMap.put(
					HHSConstants.CREATED_BY_USER_ID,
					(String) aoResourceRequest.getPortletSession().getAttribute(
							ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE));
			loHashMap.put(HHSConstants.PUBLISHED, HHSConstants.ONE);
			loHashMap.put(HHSConstants.FISCAL_YEAR_ID, aoResourceRequest.getParameter(HHSConstants.BUDGET_YEAR));
			Channel loChannelObj = new Channel();
			loChannelObj.setData(HHSConstants.AO_HASH_MAP, loHashMap);
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.UPDATE_BUDGET_TEMPLATE);
			aoResourceRequest.getPortletSession().setAttribute(
					HHSConstants.SUCCESS_MESSAGE,
					PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSConstants.BUDGET_TEMPLATE_SUCCESS_MESSAGE), PortletSession.APPLICATION_SCOPE);
		}
	}
	
	/** This method is added in R7. It will open Update Services 
	 * Overlay when click on 'Update Services' from Budget List Screen.
	 * @param aoResourceRequest
	 * @param aoResourceResponse
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@ResourceMapping("updateServicesOverlay")
	public ModelAndView updateServicesOverlay(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws ApplicationException
	{
		ModelAndView loModelAndView = null;
		Channel loChannel = new Channel();
		PortletSession loSession = aoResourceRequest.getPortletSession();
		String lsContractId = aoResourceRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW);
		String lsbudgetId = aoResourceRequest.getParameter(HHSConstants.BUDGET_ID_WORKFLOW);
		String lsBudgetFiscalYear = aoResourceRequest.getParameter(HHSConstants.CLC_FISCAL_YEAR_ID);
		String lsOrgId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
				PortletSession.APPLICATION_SCOPE);
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setContractBudgetID(lsbudgetId);
		loCBGridBean.setContractTypeId(HHSConstants.ONE);
		loCBGridBean.setContractID(lsContractId);
		loCBGridBean.setAgencyId(lsOrgId);
		try
		{
			loChannel.setData(HHSConstants.CB_GRID_BEAN_OBJ, loCBGridBean);
			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.FETCH_UPDATE_SERVICES_DETAILS,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			aoResourceRequest.setAttribute(HHSConstants.BUDGET_ID_WORKFLOW, lsbudgetId);
			aoResourceRequest.setAttribute(HHSConstants.CONTRACT_ID_WORKFLOW, lsContractId);
			aoResourceRequest.setAttribute(HHSConstants.CLC_FISCAL_YEAR_ID, lsBudgetFiscalYear);
			aoResourceRequest.setAttribute(HHSConstants.BUDGET_TYPE,aoResourceRequest.getParameter(HHSConstants.BUDGET_TYPE));
			HashMap loServicesMap = (HashMap) loChannel.getData(HHSR5Constants.AO_SERVICES_MAP);
			aoResourceRequest.setAttribute(HHSR5Constants.SERVICES_MAP, loServicesMap);
			loModelAndView = new ModelAndView(HHSR5Constants.JSP_UPDATE_SERVICES);
		}
		catch (ApplicationException loEx)
		{
			LOG_OBJECT.Error("Error occured while requesting for updateServices.", loEx);

		}
		catch (Exception loExe)
		{
			LOG_OBJECT.Error("Error occured while requesting for updateServices.", loExe);

		}
		return loModelAndView;
	}
	
	/**
	 * This method is used to save updated services for particular Budget.It is
	 * added in R7 for Cost Center
	 * <ul>
	 * <li>This method calls updateServiceListDetails transaction</li>
	 * <ul>
	 * @param aoResourceRequest Resource Request Object
	 * @param aoResourceResponse Resource Response Object
	 * @return loModelAndView
	 * @throws Exception
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@ResourceMapping("updateServicesPendingSubmission")
	public void updateServices(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws ApplicationException
	{
		String lsErrorMsg = HHSConstants.EMPTY_STRING;
		int liErrorCode = HHSConstants.INT_ZERO;
		try
		{
			String lsContractId = aoResourceRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW);
			String lsbudgetId = aoResourceRequest.getParameter(HHSConstants.BUDGET_ID_WORKFLOW);
			String lsbudgetType = aoResourceRequest.getParameter(HHSConstants.BUDGET_TYPE);
			String lsBudgetFiscalYear = aoResourceRequest.getParameter(HHSConstants.CLC_FISCAL_YEAR_ID);
			String[] loUserSelectedList = aoResourceRequest.getParameter(HHSConstants.SEL_SER_LIST).split(
					HHSConstants.COMMA);
			String[] loUserDeleteList = aoResourceRequest.getParameter(HHSR5Constants.DELETE_ITEMS_LIST).split(
					HHSConstants.COMMA);
			String lsUserId = (String) aoResourceRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			CBGridBean loCBGridBean = new CBGridBean();
			TaskDetailsBean loTaskBean = new TaskDetailsBean();
			if (HHSConstants.BUDGET_TYPE1.equalsIgnoreCase(lsbudgetType)
					|| HHSConstants.BUDGET_TYPE4.equalsIgnoreCase(lsbudgetType))
			{
				loCBGridBean.setContractTypeId(HHSConstants.TWO);
			}
			loTaskBean.setContractId(lsContractId);
			loTaskBean.setStartFiscalYear(lsBudgetFiscalYear);
			loTaskBean.setUserId(lsUserId);
			loTaskBean.setEventName(HHSR5Constants.UPDATE_SERVICES);
			loCBGridBean.setContractBudgetID(lsbudgetId);
			loCBGridBean.setAmendmentContractID(lsContractId);
			loCBGridBean.setFiscalYearID(lsBudgetFiscalYear);

			loCBGridBean.setModifiedByUserId(lsUserId);
			HashMap loHmMap = new HashMap();
			List<String> loServicesList = new ArrayList<String>();
			for (int liIndex = 0; liIndex < loUserSelectedList.length; liIndex++)
			{
				if (loUserSelectedList[liIndex] != null && !loUserSelectedList[liIndex].isEmpty())
					loServicesList.add(loUserSelectedList[liIndex]);
			}
			List<String> loDeleteList = new ArrayList<String>();
			for (int liIndex = 0; liIndex < loUserDeleteList.length; liIndex++)
			{
				if (loUserDeleteList[liIndex] != null && !loUserDeleteList[liIndex].isEmpty())
					loDeleteList.add(loUserDeleteList[liIndex]);
			}
			loHmMap.put(HHSConstants.SEL_SER_LIST, loServicesList);
			loHmMap.put(HHSR5Constants.DELETE_ITEMS_LIST, loDeleteList);
			loHmMap.put(ApplicationConstants.KEY_SESSION_USER_ID, lsUserId);
			Channel loChannelObj = new Channel();
			loHmMap.put(HHSConstants.CONTRACT_ID1, lsContractId);
			loHmMap.put(HHSConstants.AMEND_CONTRACT_ID, loCBGridBean.getAmendmentContractID());
			loHmMap.put(HHSConstants.CONTRACT_TYPE, (null == loCBGridBean.getContractTypeId()) ? HHSConstants.ONE
					: loCBGridBean.getContractTypeId());
			loHmMap.put(HHSConstants.BUDGET_ID, loCBGridBean.getContractBudgetID());
			loHmMap.put(HHSConstants.FISCAL_YEAR_ID, lsBudgetFiscalYear);
			loHmMap.put(HHSConstants.CHECK_FOR_NEW_FY, HHSConstants.TRUE);
			loChannelObj.setData(HHSConstants.AO_HASH_MAP, loHmMap);
			loChannelObj.setData(HHSConstants.AO_TASK_DETAILS_BEAN, loTaskBean);
			HHSTransactionManager.executeTransaction(loChannelObj, HHSR5Constants.UPDATE_SERVICES_PENDING_SUBMISSION,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			aoResourceRequest.getPortletSession().setAttribute(
					HHSConstants.SUCCESS_MESSAGE,
					PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSR5Constants.UPDATE_SERVICES_SUCCESS_MESSAGE), PortletSession.APPLICATION_SCOPE);
		}
		catch (ApplicationException loEx)
		{
			liErrorCode = HHSConstants.INT_ONE;
			lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("Error occured updateSelectedServices method while updating Services ", loEx);

		}
		catch (Exception aoExe)
		{
			liErrorCode = HHSConstants.INT_ONE;
			lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("Error occured in updateSelectedServices method while updating Services", aoExe);
		}
		returnMessage(aoResourceResponse, liErrorCode, lsErrorMsg);
	}

			// End

}
