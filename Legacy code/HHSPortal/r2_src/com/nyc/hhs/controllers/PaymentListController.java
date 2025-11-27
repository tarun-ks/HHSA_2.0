/**
 * 
 */
package com.nyc.hhs.controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.controllers.util.ContractListUtils;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.BudgetList;
import com.nyc.hhs.model.FiscalYear;
import com.nyc.hhs.model.PaymentSortAndFilter;
import com.nyc.hhs.model.ProgramNameInfo;
import com.nyc.hhs.model.StatusR3;
import com.nyc.hhs.util.ApplicationSession;
import com.nyc.hhs.util.PortalUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This controller will be used to handle all action and render request from
 * Payment List page
 */
@Controller(value = "paymentListHandler")
@RequestMapping("view")
public class PaymentListController extends BaseController
{

	/**
	 * LOG OBJECT for PaymentListController class.
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(PaymentListController.class);

	/**
	 * This method get the command object for PaymentSortAndFilter bean.
	 * @return new PaymentSortAndFilter.
	 */
	@ModelAttribute("PaymentSortAndFilter")
	public PaymentSortAndFilter getCommandObject()
	{
		return new PaymentSortAndFilter(HHSConstants.ORG_TYPE);
	}

	/**
	 * This method binds the date field on the jsp page with the date fields in
	 * the bean. also this method checks for the format and number of character
	 * to perform the server side validations. and if the format or number of
	 * character differs from the one specified in this method the errors will
	 * get stored in the resultBinder.
	 * 
	 * @param aoBinder - WebDataBinder
	 */
	@InitBinder
	public void initBinder(WebDataBinder aoBinder)
	{
		SimpleDateFormat loFormat = new SimpleDateFormat(HHSConstants.MMDDYYFORMAT);
		loFormat.setLenient(true);
		aoBinder.registerCustomEditor(Date.class, new CustomDateEditor(loFormat, false, 10));
	}

	/**
	 * <p>
	 * This method is default render method to display Payment list screen for
	 * Accelerator/Agency/Provider.
	 * </p>
	 * <ul>
	 * </li> </li>
	 * </ul>
	 * <p>
	 * <b>Flow of algorithm of this method :: </b><br>
	 * </br>
	 * <ul>
	 * <li>We get lsOrgnizationType from session to determine the type of user
	 * which is then passed to channel to get the required information for that
	 * user.
	 * <li><b>SORT PAYMENT :</b><br>
	 * Payments on Accelerator/Agency screen are by default sorted first by
	 * Status as per the order: 1 = Pending Approval 2 = Approved 3 = Disbursed
	 * 4 = Withdrawn 5 = Suspended and then Date of Last Update in descending
	 * order.<br>
	 * 2.Payments on provider screen are by default sorted by first by Date
	 * Disbursed in Descending order and then Payment Voucher Number in
	 * descending order. <br>
	 * For Sorting getSortParams method is called and sorting variable like
	 * firstSort,firstSortType, secondSort,secondSortType in loPaymentSortBean
	 * object are set by this method depending on type, column name and grid
	 * name.
	 * <li><b>PAGING PAYMENT :</b><br>
	 * For pagination getPagingParams method is called and pagination variable
	 * like pageIndex,startNode,endNode in loPaymentSortBean object are set by
	 * this method depending on page no on which user is and maximum no of
	 * records which we can display on a page.
	 * <li><b>FETCHING PAYMENT LIST :</b> <br>
	 * getFinancialsPaymentList transaction is called for rendering of payment
	 * records and this transaction hits the database to get the required
	 * payment list in loPaymentList and total payments count in
	 * liPaymentListCount object
	 * 
	 * <br>
	 * Then we set required variable in request attributes which can be used in
	 * paymentList.jsp.</li>
	 * <li>The transaction used: getFinancialsPaymentList</li>
	 * </ul>
	 * This method was updated in R4.
	 * <p>
	 * 
	 * @see BaseController#getSortParams(SortOnColumnsBean,
	 *      com.nyc.hhs.model.BaseFilter, String, String, String, String)
	 * @see BaseController#getPagingParams(PortletSession,
	 *      com.nyc.hhs.model.BaseFilter, String, String)
	 * @param aoRequest : request as input
	 * @param aoResponse : response as input
	 * @return loModelAndView : ModelAndView as return type for displaying
	 *         record on required jsp.
	 * @throws ApplicationException : Exception thrown in case of any
	 *             application code failure.
	 */
	@RenderMapping
	protected ModelAndView displayPaymentList(RenderRequest aoRequest, RenderResponse aoResponse)
			throws ApplicationException
	{
		PortletSession loPortletSession = aoRequest.getPortletSession();
		String lsOrgId = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
				PortletSession.APPLICATION_SCOPE);
		String lsUserId = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsOrgnizationType = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
				PortletSession.APPLICATION_SCOPE);
		String lbRequestFromHomePage = HHSConstants.FALSE;
		String lsProviderId = null;
		// Release 5
		String lsPermissionType = (String) loPortletSession.getAttribute(HHSConstants.PERMISSION_TYPE,
				PortletSession.APPLICATION_SCOPE);

		if (null != PortalUtil.parseQueryString(aoRequest, HHSConstants.FROM_HOME_PAGE))
		{
			lbRequestFromHomePage = PortalUtil.parseQueryString(aoRequest, HHSConstants.FROM_HOME_PAGE);
		}
		Boolean lbFirstLoad = false;
		PaymentSortAndFilter loPaymentSortBean = null;
		try
		{
			if (ApplicationSession.getAttribute(aoRequest, true, HHSConstants.PAYMENT_SESSION_BEAN) != null)
			{
				loPaymentSortBean = (PaymentSortAndFilter) ApplicationSession.getAttribute(aoRequest, true,
						HHSConstants.PAYMENT_SESSION_BEAN);
				loPortletSession.removeAttribute(HHSConstants.CONTRACT_SESSION_BEAN);
				loPortletSession.removeAttribute(HHSConstants.BUDGET_SESSION_BEAN);
				loPortletSession.removeAttribute(HHSConstants.INVOICE_SESSION_BEAN);
				loPortletSession.removeAttribute(HHSConstants.AMENDED_CONTRACT_SESSION_BEAN);
			}
			Channel loChannelObj = new Channel();
			if (null != lsOrgnizationType
					&& (lsOrgnizationType.equals(ApplicationConstants.CITY_ORG) || lsOrgnizationType
							.equals(ApplicationConstants.AGENCY_ORG))
					&& !lbRequestFromHomePage.equals(HHSConstants.FALSE))
			{
				loPaymentSortBean = getFinancialBeanForCityAndAgencyHomePages(aoRequest, loPaymentSortBean);
				aoRequest.setAttribute(HHSConstants.PAYMENT_STATUS_COLUMN, loPaymentSortBean.getPaymentStatusList());
			}

			if (null == loPaymentSortBean)
			{
				lbFirstLoad = true;
				loPaymentSortBean = new PaymentSortAndFilter(lsOrgnizationType);
			}

			// Start : R5 Added
			loPaymentSortBean.setUserIdContractRestriction(lsUserId);
			// Added for Emergency Build 4.0.0.1 defect 8360, 8377
			Boolean lbFromFinancialTab = Boolean.valueOf(aoRequest.getParameter(HHSR5Constants.FROM_FINANCIAL_TAB));
			if (null != lbFromFinancialTab && lbFromFinancialTab)
			{
				// Updated for Emergency Build 4.0.0.2 defect 8377
				removeRedirectFromListSessionData(loPortletSession);
			}

			String lsContractIdForPaymentList = (String) loPortletSession.getAttribute(
					HHSR5Constants.CONTRACT_ID_FOR_LIST, PortletSession.APPLICATION_SCOPE);
			boolean lbNavigateFromList = false;
			if (StringUtils.isNotBlank(lsContractIdForPaymentList))
			{
				loPaymentSortBean.setContractId(lsContractIdForPaymentList);
				if (lsOrgnizationType.equals(ApplicationConstants.CITY_ORG)
						|| lsOrgnizationType.equals(ApplicationConstants.AGENCY_ORG))
				{
					List<String> loPaymentStatusList = new ArrayList<String>();
					loPaymentStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
							HHSConstants.STATUS_PAYMENT_PENDING_APPROVAL));
					loPaymentStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
							HHSConstants.STATUS_PAYMENT_SUSPENDED));
					loPaymentStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
							HHSConstants.STATUS_PAYMENT_APPROVED));
					loPaymentStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
							HHSConstants.STATUS_PAYMENT_PENDING_FMS_ACTION));
					loPaymentStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
							HHSConstants.STATUS_PAYMENT_DISBURSED));
					loPaymentStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
							HHSConstants.STATUS_PAYMENT_REJECTED));
					loPaymentSortBean.getPaymentStatusList().addAll(loPaymentStatusList);
					lbNavigateFromList = true;
				}
			}
			String lsBudgetIdForPaymentList = (String) loPortletSession.getAttribute(HHSR5Constants.BUDGET_ID_FOR_LIST,
					PortletSession.APPLICATION_SCOPE);
			if (StringUtils.isNotBlank(lsBudgetIdForPaymentList))
			{
				loPaymentSortBean.setBudgetId(lsBudgetIdForPaymentList);
			}
			String lsInvoiceIdForPaymentList = (String) loPortletSession.getAttribute(
					HHSR5Constants.INVOICE_ID_FOR_LIST, PortletSession.APPLICATION_SCOPE);
			if (StringUtils.isNotBlank(lsInvoiceIdForPaymentList))
			{
				loPaymentSortBean.setInvoiceId(lsInvoiceIdForPaymentList);
			}
			// End : R5 Added
			loPaymentSortBean.setOrgType(lsOrgnizationType);
			if (lsOrgnizationType != null && lsOrgnizationType.equalsIgnoreCase(ApplicationConstants.AGENCY_ORG))
			{
				loPaymentSortBean.setAgency(lsOrgId);
			}
			loPaymentSortBean.setOrgId(lsOrgId);
			String lsNextPageParam = aoRequest.getParameter(HHSConstants.NEXT_PAGE_PARAM);
			getPagingParams(loPortletSession, loPaymentSortBean, lsNextPageParam, HHSConstants.PAYMENT_LIST_KEY);
			String lsContractTitle = null;
			if (loPaymentSortBean.getPaymentContractTitle() != null)
			{
				lsContractTitle = loPaymentSortBean.getPaymentContractTitle();
				loPaymentSortBean.setPaymentContractTitle(loPaymentSortBean.getPaymentContractTitle().replaceAll(
						HHSConstants.STR, HHSConstants.STR_DOUBLE));
			}
			lsProviderId = (String) ApplicationSession.getAttribute(aoRequest, HHSConstants.PROVIDER_ID);
			if (null != lsProviderId)
			{
				loPaymentSortBean.setProviderId(lsProviderId);
			}
			loChannelObj.setData(HHSConstants.PY_AO_PAYMENT_BEAN, loPaymentSortBean);
			loChannelObj.setData(HHSConstants.AS_ORG_TYPE, lsOrgnizationType);
			loChannelObj.setData(HHSConstants.ORG_ID, lsOrgId);
			loChannelObj.setData(HHSConstants.AS_PROCESS_TYPE, HHSConstants.PAYMENT);
			loChannelObj.setData(HHSConstants.ORGTYPE, lsOrgnizationType);
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.PY_GET_FINANCIALS_PAYMENT_LIST);

			if (loPaymentSortBean.getPaymentContractTitle() != null && lsContractTitle != null)
			{
				loPaymentSortBean.setPaymentContractTitle(lsContractTitle);
			}
			if (lbNavigateFromList)
			{
				loPaymentSortBean.getPaymentStatusList().remove(
						PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_PAYMENT_REJECTED));
			}
			getChannelDataSetSessionData(aoRequest, loPortletSession, lbFirstLoad, loPaymentSortBean, loChannelObj,
					lsOrgnizationType, lsPermissionType);
		}
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error(HHSConstants.PY_ERROR_OCCURED_WHILE_PROCESSING_PAYMENTS, loExp);
			String lsErrorMsg = HHSConstants.ERROR_WHILE_PROCESSING_REQUEST;
			loPortletSession.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg,
					PortletSession.APPLICATION_SCOPE);
			loPortletSession.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE, PortletSession.APPLICATION_SCOPE);

		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error(HHSConstants.PY_ERROR_OCCURED_WHILE_PROCESSING_PAYMENTS, loExp);
			String lsErrorMsg = HHSConstants.ERROR_WHILE_PROCESSING_REQUEST;
			loPortletSession.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg,
					PortletSession.APPLICATION_SCOPE);
			loPortletSession.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE, PortletSession.APPLICATION_SCOPE);

		}
		return new ModelAndView(HHSConstants.PY_PAYMENT_LIST, HHSConstants.PAYMENT_SORT_AND_FILTER, loPaymentSortBean);
	}

	/**
	 * This method set the status id in payment filter bean as per statuses on
	 * payment list screen.
	 * @param aoRequest : Request as input
	 * @param aoPaymentSortBean : PaymentSortBean as input
	 * @return loPaymentSortBean : PaymentSortBean as output
	 * @throws ApplicationException : ApplicationException thrown in case of any
	 *             application code failure.
	 */
	private PaymentSortAndFilter getFinancialBeanForCityAndAgencyHomePages(RenderRequest aoRequest,
			PaymentSortAndFilter aoPaymentSortBean) throws ApplicationException
	{
		if (null == aoPaymentSortBean)
		{
			aoPaymentSortBean = new PaymentSortAndFilter(HHSConstants.USER_CITY);
		}
		String lsFilterCriteria = PortalUtil.parseQueryString(aoRequest, HHSConstants.FILTER_CRITERIA);
		if (null != lsFilterCriteria)
		{
			if (lsFilterCriteria.equalsIgnoreCase(HHSConstants.PAYMENTS_PEND_APPROVAL))
			{
				List<String> loPaymentStatusList = new ArrayList<String>();
				loPaymentStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_PAYMENT_PENDING_APPROVAL));
				aoPaymentSortBean.setPaymentStatusList(loPaymentStatusList);
			}
			else if (lsFilterCriteria.equalsIgnoreCase(HHSConstants.PAYMENTS_FMS_ERROR))
			{
				List<String> loPaymentStatusList = new ArrayList<String>();
				loPaymentStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_PAYMENT_PENDING_FMS_ACTION));
				aoPaymentSortBean.setPaymentStatusList(loPaymentStatusList);
			}
		}
		return aoPaymentSortBean;
	}

	/**
	 * This method get data from channel and set it in session.
	 * @param aoRequest : request as input.
	 * @param aoPortletSession : Portlet Session as input.
	 * @param aoFirstLoad : Boolean.
	 * @param aoPaymentSortBean : PaymentSortAndFilter as input.
	 * @param aoChannelObj : channel object as input.
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	private void getChannelDataSetSessionData(RenderRequest aoRequest, PortletSession aoPortletSession,
			Boolean aoFirstLoad, PaymentSortAndFilter aoPaymentSortBean, Channel aoChannelObj,
			String asOrgnizationType, String asPermissionType) throws ApplicationException
	{
		try
		{
			List<ProgramNameInfo> loProgramNameList = (List<ProgramNameInfo>) aoPortletSession.getAttribute(
					HHSConstants.PROGRAM_NAME_LIST, PortletSession.APPLICATION_SCOPE);
			List<HashMap<String, String>> loAgencyDetails = (List<HashMap<String, String>>) aoPortletSession
					.getAttribute(HHSConstants.AGENCY_DETAILS, PortletSession.APPLICATION_SCOPE);
			List<PaymentSortAndFilter> loPaymentList = (List<PaymentSortAndFilter>) aoChannelObj
					.getData(HHSConstants.PY_AO_PAYMENT_LIST);
			// Release 5 User Notification
			if (asPermissionType != null
					&& (asPermissionType.equalsIgnoreCase(ApplicationConstants.ROLE_READ_ONLY) || asPermissionType
							.equalsIgnoreCase(ApplicationConstants.ROLE_PROCUREMENT)))
			{
				if (loPaymentList != null)
				{
					for (PaymentSortAndFilter loPayment : loPaymentList)
					{
						loPayment.setUserAccess(false);
					}
				}
			}
			// Release 5 User Notification
			Integer loPaymentListCount = (Integer) aoChannelObj.getData(HHSConstants.PY_LI_PAYMENT_LIST_COUNT);
			List<FiscalYear> loFiscalInformation = (List<FiscalYear>) aoChannelObj
					.getData(HHSConstants.FISCAL_INFORMATION);
			List<StatusR3> loPaymentStatus = (List<StatusR3>) aoChannelObj.getData(HHSConstants.INVOICE_LO_STATUS_LIST);
			if ((loProgramNameList == null || loProgramNameList.isEmpty())
					&& asOrgnizationType.equalsIgnoreCase(HHSConstants.USER_AGENCY))
			{
				loProgramNameList = ContractListUtils.getProgramNameList(aoChannelObj);
				aoPortletSession.setAttribute(HHSConstants.PROGRAM_NAME_LIST, loProgramNameList);
			}
			if (loAgencyDetails == null || loAgencyDetails.isEmpty() && asOrgnizationType != null
					&& !asOrgnizationType.equalsIgnoreCase(HHSConstants.USER_AGENCY))
			{
				loAgencyDetails = ContractListUtils.getAgencyDetails(aoChannelObj);
				aoPortletSession.setAttribute(HHSConstants.AGENCY_DETAILS, loAgencyDetails,
						PortletSession.APPLICATION_SCOPE);
			}
			
            /*[Start] R7.2.0 QC8914 Set indicator for Access control     */
            PortletSession loSession  = aoRequest.getPortletSession();
            if( loSession.getAttribute(ApplicationConstants.KEY_SESSION_ROLE_CURRENT, PortletSession.APPLICATION_SCOPE) != null
                    && ApplicationConstants.ROLE_OBSERVER.equalsIgnoreCase((String)loSession.getAttribute(ApplicationConstants.KEY_SESSION_ROLE_CURRENT, PortletSession.APPLICATION_SCOPE)))  {
                setIndecatorForReadOnlyRole(loPaymentList, ApplicationConstants.ROLE_OBSERVER);
            }
            /*[End] R7.2.0 QC8914 Set indicator for Access control     */ 
			
			aoRequest.setAttribute(HHSConstants.INVOICE_LB_FIRST_LOAD, aoFirstLoad);
			aoRequest.setAttribute(HHSConstants.PY_AO_FISCAL_INFORMATION, loFiscalInformation);
			aoRequest.setAttribute(HHSConstants.AO_AGENCY_LIST, loAgencyDetails);
			aoRequest.setAttribute(HHSConstants.PY_AO_PAYMENT_STATUS, loPaymentStatus);
			aoRequest.setAttribute(HHSConstants.PY_AO_PAYMENT_LIST, loPaymentList);
			aoRequest.setAttribute(HHSConstants.PY_AO_PAYMENT_LIST_SIZE, loPaymentListCount);
			aoPortletSession.setAttribute(ApplicationConstants.ALERT_VIEW_PAGING_RECORDS,
					((loPaymentListCount == null) ? 0 : loPaymentListCount), PortletSession.APPLICATION_SCOPE);
			aoRequest.getPortletSession().setAttribute(HHSConstants.SORT_TYPE, aoPaymentSortBean.getFirstSortType(),
					PortletSession.APPLICATION_SCOPE);
			aoRequest.getPortletSession().setAttribute(HHSConstants.SORT_BY, aoPaymentSortBean.getSortColumnName(),
					PortletSession.APPLICATION_SCOPE);
		}
		catch (ApplicationException aoAppExp)
		{
			throw new ApplicationException("Error occured while setting or gettting data in session" + aoAppExp);
		}
	}
	
    /**
    * This method will handle setting indicator setIndecatorForReadOnlyRole ApplicationConstants.ROLE_OBSERVER 
    * for access control.
    * @param List<ContractList> loContractList
    * @throws ApplicationException
    */  
    private void setIndecatorForReadOnlyRole(List<PaymentSortAndFilter> aoList, String asUserRole) {
        if( aoList == null || aoList.isEmpty() == true)  return;

        for( PaymentSortAndFilter  payObj :   aoList){
            payObj.setUserSubRole(asUserRole);
        }
    } 
    
	/**
	 * This method is default render method to display Payment list screen
	 * 
	 * @param aoRequest request as input parameters
	 * @param aoResponse response as input parameters
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	@ActionMapping
	protected void handleActionRequestInternal(ActionRequest aoRequest, ActionResponse aoResponse)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Default Action method for controller:PaymentListController.java");
	}

	/**
	 * </p>
	 * <ul>
	 * <b>Functionalities of this method ::</b> <br>
	 * </br>
	 * <li><b>FILTER PAYMENT :</b>Get Called when Filter button is clicked and
	 * this method call filterPayment method.
	 * <li><b>SORT PAYMENT :</b><br>
	 * Payments on Accelerator/Agency screen are by default sorted first by
	 * Status as per the order: 1 = Pending Approval 2 = Approved 3 = Disbursed
	 * 4 = Withdrawn 5 = Suspended and then Date of Last Update in descending
	 * order.<br>
	 * 2.Payments on provider screen are by default sorted by first by Date
	 * Disbursed in Descending order and then Payment Voucher Number in
	 * descending order. <br>
	 * For Sorting getSortParams method is called and sorting variable like
	 * firstSort,firstSortType, secondSort,secondSortType in loPaymentSortBean
	 * object are set by this method depending on type, column name and grid
	 * name. *
	 * <li><b>PAGING PAYMENT :</b><br>
	 * For pagination getPagingParams method is called and pagination variable
	 * like pageIndex,startNode,endNode in loPaymentSortBean object are set by
	 * this method depending on page no on which user is and maximum no of
	 * records which we can display on a page.
	 * </ul>
	 * @param aoPaymentSortAndFilter : PaymentSortAndFilter as input
	 * @param aoRequest : nrequest as input
	 * @param aoResponse : response as input
	 * @throws ApplicationException : Exception thrown in case of any
	 *             application code failure.
	 */
	@ActionMapping(params = "paymentAction=finacialsPaymentMap")
	protected void actionPaymentList(
			@ModelAttribute("PaymentSortAndFilter") PaymentSortAndFilter aoPaymentSortAndFilter,
			ActionRequest aoRequest, ActionResponse aoResponse) throws ApplicationException
	{
		PortletSession loPortletSession = aoRequest.getPortletSession();
		String lsNextAction = aoRequest.getParameter(ApplicationConstants.NEXT_ACTION);
		String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
		String lsProviderId = null;
		if (null != aoRequest.getParameter(HHSConstants.PROVIDER_ID)
				&& !aoRequest.getParameter(HHSConstants.PROVIDER_ID).equalsIgnoreCase(HHSR5Constants.EMPTY_STRING))
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
		if (null != lsNextAction)
		{
			if (lsNextAction.equalsIgnoreCase(HHSConstants.SORT_PAYMENT))
			{
				sortPayments(aoRequest, lsUserOrgType, aoPaymentSortAndFilter);
			}
			else if (lsNextAction.equalsIgnoreCase(HHSConstants.FETCH_ACTIVE_PAYMENTS))
			{
				aoResponse.setRenderParameter(HHSConstants.NEXT_PAGE_PARAM,
						aoRequest.getParameter(HHSConstants.NEXT_PAGE));
			}
			else if (lsNextAction.equalsIgnoreCase(HHSConstants.FILTER_PAYMENT))
			{
				aoPaymentSortAndFilter.setIsFilter(true);
				// setting default sorting for filter
				aoPaymentSortAndFilter.setDefaultSortData(lsUserOrgType);
				// Updated for Emergency Build 4.0.0.2 defect 8360, 8377
				removeRedirectFromListSessionData(loPortletSession);
			}
		}
		else
		{
			throw new ApplicationException(
					HHSConstants.PY_ERROR_OCCURRED_WHILE_GETTING_PAYMENT_LIST_SCREEN_FOR_ORG_TYPE + lsUserOrgType);
		}
		ApplicationSession.setAttribute(aoPaymentSortAndFilter, aoRequest, HHSConstants.PAYMENT_SESSION_BEAN);
		aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PY_PAYMENT_LIST_ACTION);
		ApplicationSession.setAttribute(lsProviderId, aoRequest, HHSConstants.PROVIDER_ID);
	}

	/**
	 * This Method sort the list of payment on payment screen.
	 * 
	 * @param aoRequest : request as input
	 * @param asUserOrgType : String as input
	 * @param aoPaymentSortAndFilter : PaymentSortAndFilter as input
	 * @throws ApplicationException : Exception thrown in case of any
	 *             application code failure.
	 */
	private void sortPayments(ActionRequest aoRequest, String asUserOrgType, PaymentSortAndFilter aoPaymentSortAndFilter)
			throws ApplicationException
	{
		String lsNextPage = aoRequest.getParameter(HHSConstants.NEXT_PAGE_PARAM);
		String lsSortType = PortalUtil.parseQueryString(aoRequest, HHSConstants.SORT_TYPE);
		String lsColumnName = PortalUtil.parseQueryString(aoRequest, HHSConstants.COLUMN_NAME);
		String lsGridName = PortalUtil.parseQueryString(aoRequest, HHSConstants.SORT_GRID_NAME);
		getSortDetailsFromXML(lsColumnName, asUserOrgType, lsGridName, aoPaymentSortAndFilter, lsSortType);
		getPagingParams(aoRequest.getPortletSession(), aoPaymentSortAndFilter, lsNextPage,
				HHSConstants.CONTRACT_LIST_KEY);
	}

	/**
	 * This method will get the payment CT id from the cache when user type
	 * three characters using EpinList method defined in basecontroller.
	 * 
	 * @param aoRequest a ResourceRequest Object
	 * @param aoResponse a ResourceResponse Object
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             ApplicationException
	 */
	@ResourceMapping("getPaymentCtResourceUrl")
	public void getEpinListResourceRequest(ResourceRequest aoRequest, ResourceResponse aoResponse)
			throws ApplicationException
	{
		try
		{
			getEpinList(aoRequest, aoResponse);
		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error(HHSConstants.PY_ERROR_OCCURED_WHILE_PROCESSING_PAYMENTS_CT_ID_TYPE_HEAD, aoAppExp);
		}
	}

}
