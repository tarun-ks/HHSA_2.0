package com.nyc.hhs.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
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
import com.nyc.hhs.model.AuthenticationBean;
import com.nyc.hhs.model.ContractList;
import com.nyc.hhs.model.FiscalYear;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.InvoiceList;
import com.nyc.hhs.model.InvoiceSort;
import com.nyc.hhs.model.ProgramNameInfo;
import com.nyc.hhs.model.StatusR3;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.ApplicationSession;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PortalUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * @author virender.x.kumar
 * 
 */

/**
 * The InvoiceListController will be used to handle all action and render
 * request from Invoice List page
 */
@Controller(value = "invoiceListAction")
@RequestMapping("view")
public class InvoiceListController extends BaseController implements ResourceAwareController
{

	/**
	 * @return InvoiceList Object
	 */
	@ModelAttribute("InvoiceList")
	public InvoiceList getCommandObject()
	{
		return new InvoiceList();
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
	 * Validator object
	 */
	@Autowired
	private Validator validator;

	/**
	 * 
	 * @param aoValidator Validator object
	 */
	public void setValidator(Validator aoValidator)
	{
		this.validator = aoValidator;
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
	 * LogInfo object
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(InvoiceListController.class);

	/**
	 * This method is default render method to display Invoice list
	 * screen(S309). The page will get loaded once the render method
	 * handleRenderRequestInternal() is called. ModelAndView will return the
	 * render values depending in the Sort form, this will initialise the
	 * sorting for the referred column once the request is loaded the
	 * invoiceList JSP page will be rendered.
	 * <ul>
	 * <li>set the orgType in the channelObject check wether the
	 * invoiceFilterBean object is null, which will be for the dafault screen
	 * navigation
	 * <li>In case the invoiceFilterBean is null all the items in the grid will
	 * be displayed in default order set at the sortCOnfiguration.xml
	 * <li>Else if execute the transaction mapping in transactionConfigR2.xml
	 * and set the invoiceFilterBean data
	 * <li>Fetch the output of the transaction executed in the service class
	 * FinancialsInvoiceListService and store it in the InvoiceList to render
	 * the page
	 * <li>Next we need to get the invoiceCount: fetch the invioce count by
	 * executing the count query in the service class and store it in the
	 * invoiceCount object.
	 * <li>Set the invoiceList object in the request object.
	 * <li>If the invoiceCount is zero which will happen for the
	 * "no data matching found", incase the data is not found convert the count
	 * to "zero" from null, to avoid null pointer exception.
	 * <li>Set the count in portletSession and the request object now.
	 * <li>Check the navigation is hapening from home page or from some otheer
	 * page.Based on the result data will display.</li>
	 * <ul>
	 * 
	 * @param aoRequest The Result object to gather the result set
	 * @param aoResponse The Response object will gather the response from the
	 *            service after the transaction
	 * @return loModelAndView The modelAndView object to get the object of the
	 *         JSP name to be loaded
	 */
	@RenderMapping
	protected ModelAndView handleRenderRequestInternal(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		return mainRenderMethod(aoRequest, null, null);
	}

	/**
	 * The Main Render method to handle request <li>The transaction
	 * used:getFinancialsInvoiceList</li> <li>This method was updated in R4.</li>
	 * * * This method is modified as a part of Release 3.1.2 Defect 6420
	 * 
	 * <ul>
	 * <li>Comparing page level invoice id and session invoice id and returning
	 * null in case of discrepancy</li>
	 * </ul>
	 * 
	 * @param aoRequest Request object
	 * @param asOverlayPageParam Response object
	 * @param asOverLay String overlay
	 * @return ModelAndView Object
	 */
	@SuppressWarnings("unchecked")
	private ModelAndView mainRenderMethod(RenderRequest aoRequest, String asOverlayPageParam, String asOverLay)
	{

		PortletSession loPortletSession = aoRequest.getPortletSession();
		boolean lbFirstLoad = false;
		String lsOrgId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
				PortletSession.APPLICATION_SCOPE);
		String lsUserId = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsUserRole = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ROLE, PortletSession.APPLICATION_SCOPE);
		String lsProviderId = null;
		InvoiceList loInvoiceFilterBean = null;
		Channel loChannelObj = new Channel();
		try
		{
			String lsOrgnizationType = (String) loPortletSession.getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			loChannelObj.setData(HHSConstants.ORGTYPE, lsOrgnizationType);
			String lsNextPage = null;
			String lsMultipleInvoice = HHSConstants.FALSE;
			String lbRequestFromHomePage = HHSConstants.FALSE;
			// Release 5
			String lsPermissionType = (String) loPortletSession.getAttribute(HHSConstants.PERMISSION_TYPE,
					PortletSession.APPLICATION_SCOPE);

			if (null != PortalUtil.parseQueryString(aoRequest, HHSConstants.FROM_HOME_PAGE))
			{
				lbRequestFromHomePage = PortalUtil.parseQueryString(aoRequest, HHSConstants.FROM_HOME_PAGE);
			}
			// fix done as a part of release 3.1.2 defect 6420 - start.
			if (null != PortalUtil.parseQueryString(aoRequest, HHSConstants.FROM_MULTIPLE_INVOICE))
			{
				lsMultipleInvoice = PortalUtil.parseQueryString(aoRequest, HHSConstants.FROM_MULTIPLE_INVOICE);
			}
			// fix done as a part of release 3.1.2 defect 6420 - end.
			if (asOverlayPageParam != null && asOverLay != null)
			{
				lsNextPage = (String) ApplicationSession.getAttribute(aoRequest, asOverlayPageParam);
				loInvoiceFilterBean = (InvoiceList) ApplicationSession.getAttribute(aoRequest, true, asOverLay);
			}
			else
			{
				lsNextPage = (String) ApplicationSession.getAttribute(aoRequest, HHSConstants.NEXT_PAGE_PARAM);
				loInvoiceFilterBean = (InvoiceList) ApplicationSession.getAttribute(aoRequest, true,
						HHSConstants.INVOICE_SESSION_BEAN);
				loPortletSession.removeAttribute(HHSConstants.CONTRACT_SESSION_BEAN);
				loPortletSession.removeAttribute(HHSConstants.BUDGET_SESSION_BEAN);
				loPortletSession.removeAttribute(HHSConstants.PAYMENT_SESSION_BEAN);
				loPortletSession.removeAttribute(HHSConstants.AMENDED_CONTRACT_SESSION_BEAN);
			}

			if (null != lsOrgnizationType && lsOrgnizationType.equals(ApplicationConstants.PROVIDER_ORG)
					&& !lbRequestFromHomePage.equals(HHSConstants.FALSE))
			{
				loInvoiceFilterBean = getFinancialBeanForProviderHomePages(aoRequest, loInvoiceFilterBean);
				aoRequest.setAttribute(HHSConstants.LO_INVOICE_FILTER_BEAN,
						Arrays.asList(loInvoiceFilterBean.getInvoiceStatusList()));
			}
			else if (null != lsOrgnizationType
					&& (lsOrgnizationType.equals(ApplicationConstants.CITY_ORG) || lsOrgnizationType
							.equals(ApplicationConstants.AGENCY_ORG))
					&& !lbRequestFromHomePage.equals(HHSConstants.FALSE))
			{
				loInvoiceFilterBean = getFinancialBeanForCityAndAgencyHomePages(aoRequest, loInvoiceFilterBean);
				aoRequest.setAttribute(HHSConstants.LO_INVOICE_FILTER_BEAN,
						Arrays.asList(loInvoiceFilterBean.getInvoiceStatusList()));
			}
			if (null == loInvoiceFilterBean)
			{
				loInvoiceFilterBean = new InvoiceList(lsOrgnizationType);
				lbFirstLoad = true;
			}
			// Start : R5 Added
			// Added for Emergency Build 4.0.0.1 defect 8360
			loInvoiceFilterBean.setUserIdContractRestriction(lsUserId);
			Boolean lbFromFinancialTab = Boolean.valueOf(aoRequest.getParameter(HHSR5Constants.FROM_FINANCIAL_TAB));
			if (null != lbFromFinancialTab && lbFromFinancialTab)
			{
				// Updated for Emergency Build 4.0.0.2 defect 8377
				removeRedirectFromListSessionData(loPortletSession);
			}

			String lsContractIdForInvoiceList = (String) loPortletSession.getAttribute(
					HHSR5Constants.CONTRACT_ID_FOR_LIST, PortletSession.APPLICATION_SCOPE);
			if (StringUtils.isNotBlank(lsContractIdForInvoiceList))
			{
				loInvoiceFilterBean.setInvoiceContractId(lsContractIdForInvoiceList);
			}
			String lsBudgetIdForInvoiceList = (String) loPortletSession.getAttribute(HHSR5Constants.BUDGET_ID_FOR_LIST,
					PortletSession.APPLICATION_SCOPE);
			if (StringUtils.isNotBlank(lsBudgetIdForInvoiceList))
			{
				loInvoiceFilterBean.setInvoiceBudgetId(lsBudgetIdForInvoiceList);
			}
			String lsInvoiceIdForInvoiceList = (String) loPortletSession.getAttribute(
					HHSR5Constants.INVOICE_ID_FOR_LIST, PortletSession.APPLICATION_SCOPE);
			if (StringUtils.isNotBlank(lsInvoiceIdForInvoiceList))
			{
				loInvoiceFilterBean.setInvoiceId(lsInvoiceIdForInvoiceList);
			}
			// End : R5 Added
			if (lsOrgnizationType != null && lsOrgnizationType.equalsIgnoreCase(ApplicationConstants.AGENCY_ORG))
			{
				loInvoiceFilterBean.setAgency(lsOrgId);
			}
			lsProviderId = (String) ApplicationSession.getAttribute(aoRequest, HHSConstants.PROVIDER_ID);
			if (null != lsProviderId)
			{
				loInvoiceFilterBean.setProviderId(lsProviderId);
			}
			loInvoiceFilterBean.setLsRequestFromHomePage(lbRequestFromHomePage);
			loInvoiceFilterBean.setOrgType(lsOrgnizationType);
			// defect id 6248 release 3.3.0.
			String lsContractTitle = null;
			if (loInvoiceFilterBean.getInvoiceContractTitle() != null)
			{
				lsContractTitle = loInvoiceFilterBean.getInvoiceContractTitle();
				loInvoiceFilterBean.setInvoiceContractTitle(loInvoiceFilterBean.getInvoiceContractTitle().replaceAll(
						HHSConstants.STR, HHSConstants.STR_DOUBLE));
			}
			getPagingParams(loPortletSession, loInvoiceFilterBean, lsNextPage, HHSConstants.INVOICE_LIST_PAGE);
			loInvoiceFilterBean.setOrgId(lsOrgId);
			loChannelObj.setData(HHSConstants.INVOICE_FILTER_BEAN, loInvoiceFilterBean);
			loChannelObj.setData(HHSConstants.AS_ORG_TYPE, lsOrgnizationType);
			loChannelObj.setData(HHSConstants.AS_ORG_ID, lsOrgId);
			loChannelObj.setData(HHSConstants.AS_PROCESS_TYPE, HHSConstants.INVOICE);
			loChannelObj.setData(HHSConstants.ORG_ID, lsOrgId);
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.GET_FINANCIALS_INVOICE_LIST);
			List<InvoiceList> loInvoiceList = (List<InvoiceList>) loChannelObj
					.getData(HHSConstants.AO_CB_INVOICE_LIST_BEAN);
			// Release 5 User Notification
			if (lsPermissionType != null
					&& (lsPermissionType.equalsIgnoreCase(ApplicationConstants.ROLE_READ_ONLY) || lsPermissionType
							.equalsIgnoreCase(ApplicationConstants.ROLE_PROCUREMENT))
					&& lsOrgnizationType.equalsIgnoreCase(HHSConstants.PROVIDER_ORG))
			{
				if (loInvoiceList != null)
				{
					for (InvoiceList loInvoice : loInvoiceList)
					{
						loInvoice.setUserAccess(false);
					}
				}
			}
			// Release 5 User Notification
			if (loInvoiceFilterBean.getInvoiceContractTitle() != null && lsContractTitle != null)
			{
				loInvoiceFilterBean.setInvoiceContractTitle(lsContractTitle);
			}
			Integer loInvoiceCount = (Integer) loChannelObj.getData(HHSConstants.INVOICE_COUNT);
			List<FiscalYear> loFiscalInformation = (List<FiscalYear>) loChannelObj
					.getData(HHSConstants.FISCAL_INFORMATION);
			List<StatusR3> loInvoiceStatus = (List<StatusR3>) loChannelObj.getData(HHSConstants.INVOICE_LO_STATUS_LIST);
			List<ProgramNameInfo> loProgramNameList = (List<ProgramNameInfo>) loPortletSession.getAttribute(
					HHSConstants.PROGRAM_NAME_LIST, PortletSession.APPLICATION_SCOPE);
			List<HashMap<String, String>> loAgencyDetails = (List<HashMap<String, String>>) loPortletSession
					.getAttribute(HHSConstants.AGENCY_DETAILS, PortletSession.APPLICATION_SCOPE);

			if ((loProgramNameList == null || loProgramNameList.isEmpty())
					&& lsOrgnizationType.equalsIgnoreCase(HHSConstants.USER_AGENCY))
			{

				loProgramNameList = ContractListUtils.getProgramNameList(loChannelObj);
				loPortletSession.setAttribute(HHSConstants.PROGRAM_NAME_LIST, loProgramNameList);
			}
			if (loAgencyDetails == null || loAgencyDetails.isEmpty())
			{
				loAgencyDetails = ContractListUtils.getAgencyDetails(loChannelObj);
				loPortletSession.setAttribute(HHSConstants.AGENCY_DETAILS, loAgencyDetails,
						PortletSession.APPLICATION_SCOPE);
			}
			if (null != loInvoiceList)
			{
				for (InvoiceList loInvoiceListBean : loInvoiceList)
				{
					loInvoiceListBean.setOrgType((String) aoRequest.getPortletSession().getAttribute(
							ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE));
					loInvoiceListBean.setInvoiceAction(lsUserRole);
				}
			}

			if (lsMultipleInvoice.equals(HHSConstants.TRUE))
			{
				aoRequest.getPortletSession().removeAttribute(HHSConstants.INVOICE_MIS_MATCHED,
						PortletSession.APPLICATION_SCOPE);
				aoRequest.getPortletSession().setAttribute(
						HHSConstants.TRANSACTION_FAILURE,
						PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
								HHSConstants.MULTIPLE_INVOICE_OPEN), PortletSession.APPLICATION_SCOPE);
			}
			
            /*[Start] R7.2.0 QC8914 Set indicator for Access control     */
            PortletSession loSession  = aoRequest.getPortletSession();
            if( loSession.getAttribute(ApplicationConstants.KEY_SESSION_ROLE_CURRENT, PortletSession.APPLICATION_SCOPE) != null
                    && ApplicationConstants.ROLE_OBSERVER.equalsIgnoreCase((String)loSession.getAttribute(ApplicationConstants.KEY_SESSION_ROLE_CURRENT, PortletSession.APPLICATION_SCOPE)))  {
                setIndecatorForReadOnlyRole(loInvoiceList, ApplicationConstants.ROLE_OBSERVER);
            }
            /*[End] R7.2.0 QC8914 Set indicator for Access control     */ 
			
			
			aoRequest.setAttribute(HHSConstants.INVOICE_LB_FIRST_LOAD, lbFirstLoad);
			aoRequest.setAttribute(HHSConstants.PY_AO_FISCAL_INFORMATION, loFiscalInformation);
			aoRequest.setAttribute(HHSConstants.AO_AGENCY_LIST, loAgencyDetails);
			aoRequest.setAttribute(HHSConstants.INVOICE_AO_INVOICE_STATUS, loInvoiceStatus);
			aoRequest.setAttribute(HHSConstants.INVOICE_AO_INVOICE_COUNT, loInvoiceCount);
			aoRequest.setAttribute(HHSConstants.INVOICE_LIST, loInvoiceList);
			aoRequest.getPortletSession().setAttribute(HHSConstants.SORT_TYPE, loInvoiceFilterBean.getFirstSortType(),
					PortletSession.APPLICATION_SCOPE);
			aoRequest.getPortletSession().setAttribute(HHSConstants.SORT_BY, loInvoiceFilterBean.getSortColumnName(),
					PortletSession.APPLICATION_SCOPE);
			aoRequest.setAttribute(HHSConstants.TOTAL_COUNT, ((loInvoiceCount == null) ? 0 : loInvoiceCount));
			loPortletSession.setAttribute(ApplicationConstants.ALERT_VIEW_PAGING_RECORDS, ((loInvoiceCount == null) ? 0
					: loInvoiceCount), PortletSession.APPLICATION_SCOPE);
		}
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("Error occured while processing invoices", loExp);
			String lsErrorMsg = HHSConstants.ERROR_WHILE_PROCESSING_REQUEST;
			loPortletSession.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg,
					PortletSession.APPLICATION_SCOPE);
			loPortletSession.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE, PortletSession.APPLICATION_SCOPE);

		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error occured while processing invoices", loExp);
			String lsErrorMsg = HHSConstants.ERROR_WHILE_PROCESSING_REQUEST;
			loPortletSession.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg,
					PortletSession.APPLICATION_SCOPE);
			loPortletSession.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE, PortletSession.APPLICATION_SCOPE);

		}

		return new ModelAndView(HHSConstants.INVOICE_LIST, HHSConstants.INVOICE_LIST_PAGE, loInvoiceFilterBean);
	}

    /*[Start] R7.2.0 QC8914 Set indicator for Access control     */
    /**
     * This method will handle setting indicator setIndecatorForReadOnlyRole ApplicationConstants.ROLE_OBSERVER 
     * for access control.
     * @param List<ContractList> loContractList
     * @throws ApplicationException
     */
    private void setIndecatorForReadOnlyRole(List<InvoiceList> aoList, String asUserRole) {
        if( aoList == null || aoList.isEmpty() == true)  return;

        for( InvoiceList  invObj :   aoList){
            invObj.setUserSubRole(asUserRole);
        }
    }
    /*[End] R7.2.0 QC8914 Set indicator for Access control     */ 
    
	/**
	 * This method will handle all the resource request when user clicks on
	 * count in Invoice portlet under Financial portlet on home page. * This
	 * method returns the bean based on the filter criteria on home page.
	 * <ul>
	 * ========================================<br/>
	 * Conditions to be checked in this method<br/>
	 * ========================================<br/>
	 * <li>Get InvoiceList bean from session.</li>
	 * <li>we have different links for the Invoice list,so based on the
	 * requirements we are setting parameter into the bean and returning the
	 * bean to mainRenderMethod for further processing.
	 * <li>Internally calling the methods
	 * getInvoicesPendSubmission,getInvoicesPendApproval
	 * getBudgetsPendingApproval,getBudgetsReturnedRevision,getModPendSubmission
	 * getInvoicesRetRevision.
	 * <li>Return InvoiceList bean to the method MainRender.</li>
	 * </ul>
	 * 
	 * @param aoRequest
	 * @param loInvoiceFilterBean
	 * @return
	 * @throws ApplicationException
	 */
	private InvoiceList getFinancialBeanForProviderHomePages(RenderRequest aoRequest, InvoiceList aoInvoiceFilterBean)
			throws ApplicationException
	{
		if (null == aoInvoiceFilterBean)
		{
			aoInvoiceFilterBean = new InvoiceList();
		}

		String lsFilterCriteria = PortalUtil.parseQueryString(aoRequest, HHSConstants.FILTER_CRITERIA);
		if (null != lsFilterCriteria)
		{
			if (lsFilterCriteria.equalsIgnoreCase(HHSConstants.INVOICES_PEND_SUBMISSION))
			{
				getInvoicesPendSubmission(aoRequest, aoInvoiceFilterBean);
			}
			else if (lsFilterCriteria.equalsIgnoreCase(HHSConstants.INVOICES_PEND_APPROVAL))
			{
				getInvoicesPendApproval(aoRequest, aoInvoiceFilterBean);
			}
			else if (lsFilterCriteria.equalsIgnoreCase(HHSConstants.INVOICES_RET_REVISION))
			{
				getInvoicesRetRevision(aoRequest, aoInvoiceFilterBean);
			}
		}
		return aoInvoiceFilterBean;
	}

	/**
	 * This method will handle all the resource request when user clicks on
	 * count of Invoices pending submission in Invoice portlet under Financial
	 * portlet on home page. This method returns the bean based on the filter
	 * criteria on home page.
	 * <ul>
	 * ========================================<br/>
	 * Conditions to be checked in this method<br/>
	 * ========================================<br/>
	 * <li>Get InvoiceList bean from session.</li>
	 * <li>Based on the requirement we have set the parameters as Pending
	 * Invoice should be active into the bean and returning the bean to
	 * mainRenderMethod for further processing.
	 * <li>Return InvoiceList bean to the method MainRender.</li>
	 * </ul>
	 * 
	 * @param aoRequest
	 * @param loInvoiceFilterBean
	 * @return
	 * @throws ApplicationException
	 */
	private InvoiceList getInvoicesPendSubmission(RenderRequest aoRequest, InvoiceList aoInvoiceFilterBean)
			throws ApplicationException
	{
		ArrayList<String> loInvoiceStatusList = new ArrayList<String>();
		loInvoiceStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_INVOICE_PENDING_SUBMISSION));
		aoInvoiceFilterBean.setInvoiceStatusList(loInvoiceStatusList);
		aoInvoiceFilterBean.setFirstSort(HHSConstants.STATUS);
		aoInvoiceFilterBean.setFirstSortType(HHSConstants.ASCENDING);
		return aoInvoiceFilterBean;
	}

	/**
	 * This method will handle all the resource request when user clicks on
	 * count of Invoices pending submission in Invoice portlet under Financial
	 * portlet on home page. This method returns the bean based on the filter
	 * criteria on home page.
	 * <ul>
	 * ========================================<br/>
	 * Conditions to be checked in this method<br/>
	 * ========================================<br/>
	 * <li>Get InvoiceList bean from session.</li>
	 * <li>Based on the requirement we have set the parameters as Pending
	 * Approval Invoice should be active into the bean and returning the bean to
	 * mainRenderMethod for further processing.
	 * <li>Return InvoiceList bean to the method MainRender.</li>
	 * </ul>
	 * 
	 * @param aoRequest
	 * @param loInvoiceFilterBean
	 * @return
	 * @throws ApplicationException
	 */

	private InvoiceList getInvoicesPendApproval(RenderRequest aoRequest, InvoiceList aoInvoiceFilterBean)
			throws ApplicationException
	{
		List<String> loInvoiceStatusList = new ArrayList<String>();
		loInvoiceStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_INVOICE_PENDING_APPROVAL));
		aoInvoiceFilterBean.setInvoiceStatusList(loInvoiceStatusList);
		aoInvoiceFilterBean.setFirstSort(HHSConstants.STATUS);
		aoInvoiceFilterBean.setFirstSortType(HHSConstants.ASCENDING);
		return aoInvoiceFilterBean;
	}

	/**
	 * This method will handle all the resource request when user clicks on
	 * count of Invoices pending submission in Invoice portlet under Financial
	 * portlet on home page. This method returns the bean based on the filter
	 * criteria on home page.
	 * <ul>
	 * ========================================<br/>
	 * Conditions to be checked in this method<br/>
	 * ========================================<br/>
	 * <li>Get InvoiceList bean from session.</li>
	 * <li>Based on the requirement we have set the parameters as Invoice
	 * Returned Revision into the bean for further processing.
	 * <li>Return InvoiceList bean to the method MainRender.</li>
	 * </ul>
	 * 
	 * @param aoRequest
	 * @param loInvoiceFilterBean
	 * @return
	 * @throws ApplicationException
	 */

	private InvoiceList getInvoicesRetRevision(RenderRequest aoRequest, InvoiceList aoInvoiceFilterBean)
			throws ApplicationException
	{
		List<String> loInvoiceStatusList = new ArrayList<String>();
		loInvoiceStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_INVOICE_RETURNED_FOR_REVISION));
		aoInvoiceFilterBean.setInvoiceStatusList(loInvoiceStatusList);
		aoInvoiceFilterBean.setFirstSort(HHSConstants.STATUS);
		aoInvoiceFilterBean.setFirstSortType(HHSConstants.ASCENDING);
		return aoInvoiceFilterBean;
	}

	/**
	 * This method will get Financial task details For City And Agency Home
	 * Pages.
	 * @param aoRequest RenderRequest
	 * @param aoInvoiceFilterBean
	 * @return aoInvoiceFilterBean InvoiceList
	 * @throws ApplicationException - if any exception occurs
	 */
	private InvoiceList getFinancialBeanForCityAndAgencyHomePages(RenderRequest aoRequest,
			InvoiceList aoInvoiceFilterBean) throws ApplicationException
	{
		if (null == aoInvoiceFilterBean)
		{
			aoInvoiceFilterBean = new InvoiceList();
		}

		String lsFilterCriteria = PortalUtil.parseQueryString(aoRequest, HHSConstants.FILTER_CRITERIA);
		if (null != lsFilterCriteria && lsFilterCriteria.equalsIgnoreCase(HHSConstants.INVOICES_PEND_APPROVAL))
		{
			getInvoicesPendApproval(aoRequest, aoInvoiceFilterBean);
		}
		return aoInvoiceFilterBean;
	}

	/**
	 * Render method to handleDuplicateRender
	 * 
	 * @param aoRequest Request object
	 * @param aoResponse Response object
	 * @return ModelAndView Object
	 * @throws ApplicationException ApplicationException
	 */
	@RenderMapping(params = "duplicate_render_invoice=duplicateRenderInvoice")
	protected ModelAndView handleDuplicateRender(RenderRequest aoRequest, RenderResponse aoResponse)
			throws ApplicationException
	{
		return mainRenderMethod(aoRequest, HHSConstants.OVERLAY_PAGE_PARAM_INVOICE, HHSConstants.OVER_LAY_INVOICE);
	}

	/**
	 * This is a referenced method inside the SORT_INVOICE.It provides the
	 * parameters to the request object based on the configuration in
	 * sortConfiguration.xml it will sort the elements
	 * 
	 * @param aoRequest ActionRequest object to gather the result set
	 * @param aoInvoiceFilter InvoiceSort object to sort the Invoice column
	 */
	public static void setFilterHiddenParams(ActionRequest aoRequest, InvoiceSort aoInvoiceFilter)
	{

		String lsInvoiceNumber = aoRequest.getParameter(HHSConstants.INVOICE_NUMBER);
		String lsDateSubmitted = aoRequest.getParameter(HHSConstants.INVOICE_DATE_SUBMITTED);
		String lsInvoiceDateApproved = aoRequest.getParameter(HHSConstants.INVOICE_DATE_APPROVED);
		String lsInvoiceValue = aoRequest.getParameter(HHSConstants.INVOICE_VALUE);
		String lsInvoiceStatus = aoRequest.getParameter(HHSConstants.INVOICE_STATUS);

		String lsProviderName = aoRequest.getParameter(HHSConstants.ORGANIZATION_LEGAL_NAME);
		String lsInvoiceAction = aoRequest.getParameter(HHSConstants.INVOICE_ACTION);

		aoInvoiceFilter.setInvoiceAction(lsInvoiceAction);
		aoInvoiceFilter.setInvoiceNumber(lsInvoiceNumber);
		aoInvoiceFilter.setInvoiceProvider(lsProviderName);
		aoInvoiceFilter.setInvoiceDateSubmitted(lsDateSubmitted);
		aoInvoiceFilter.setInvoiceDateApproved(lsInvoiceDateApproved);
		aoInvoiceFilter.setInvoiceValue(lsInvoiceValue);
		aoInvoiceFilter.setInvoiceStatus(lsInvoiceStatus);
	}

	/**
	 * This method withdraw the Invoice
	 * <ul>
	 * <li>1.Get the required info i.e InvoiceId,USerName,Password and pass the
	 * value to channel</li>
	 * <li>2.Call the withdrawInvoice Transaction</li>
	 * <ul>
	 * 
	 * @param aoAuthenticationBean AuthenticationBean object
	 * @param aoResult Result object
	 * @param aoResourceRequest Resource Request Object
	 * @param aoResourceResponse Resource Response Object
	 * @return ModelAndView Object
	 */
	@SuppressWarnings("rawtypes")
	@ResourceMapping("withdrawInvoice")
	protected ModelAndView actionWithdrawInvoice(
			@ModelAttribute("AuthenticationBean") AuthenticationBean aoAuthenticationBean, BindingResult aoResult,
			ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse) throws Exception
	{   
		ModelAndView loModelAndView = null;
		Channel loChannel = new Channel();
		PrintWriter loOut = null;
		String lsErrorMsg = HHSConstants.EMPTY_STRING;
		String lsUserEmailId = aoResourceRequest.getParameter(HHSConstants.KEY_SESSION_USER_NAME);
		String lsPassword = aoResourceRequest.getParameter(HHSConstants.PASSWORD);
		Boolean loUserValidation;
		aoResourceResponse.setContentType(HHSConstants.TEXT_HTML);
		List<HhsAuditBean> loAuditList = new ArrayList<HhsAuditBean>();

		String lsInvoiceId = null;
		String lsNextAction = aoResourceRequest.getParameter(ApplicationConstants.NEXT_ACTION);
		if (lsNextAction != null && lsNextAction.equalsIgnoreCase(HHSConstants.WITHDRAW_INVOICE))
		{
			loModelAndView = new ModelAndView(HHSConstants.INVOICE_WITHDRAW);
			lsInvoiceId = aoResourceRequest.getParameter(HHSConstants.INVOICE_ID);
			aoResourceRequest.setAttribute(HHSConstants.INVOICE_ID, lsInvoiceId);
		}
		else
		{
			try
			{   
				validator.validate(aoAuthenticationBean, aoResult);
				if (!aoResult.hasErrors())
				{
					String lsUserId = (String) aoResourceRequest.getPortletSession().getAttribute(
							ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);

					PortletSession loSession = aoResourceRequest.getPortletSession();
					P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
							ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);

					loOut = aoResourceResponse.getWriter();
					loChannel.setData(HHSConstants.INVOICE_AO_AUTH_BEAN1, aoAuthenticationBean);
					loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
					loChannel.setData(HHSConstants.INVOICE_ID_KEY, aoAuthenticationBean.getInvoiceId());
					Map loValidateMap = validateUser(lsUserEmailId, lsPassword, aoResourceRequest);
					loUserValidation = (Boolean) loValidateMap.get(HHSConstants.IS_VALID_USER);
					if (!loUserValidation || loUserValidation == null)
					{
						lsErrorMsg = (String) loValidateMap.get(HHSConstants.ERROR_MESSAGE);
					}
					else
					{
						loChannel.setData(HHSConstants.AUTH_STATUS_FLAG, true);
						loChannel.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
						loAuditList.add(HHSUtil.addAuditDataToChannel(HHSConstants.PROVIDER_COMMENT,
								HHSConstants.PROVIDER_COMMENT, aoAuthenticationBean.getReason(),
								HHSConstants.WITHDRAW_INVOICE_AUDIT, aoAuthenticationBean.getInvoiceId(), lsUserId,
								HHSConstants.PROVIDER_AUDIT));
						loAuditList.add(HHSUtil.addAuditDataToChannel(HHSConstants.STATUS_CHANGE,
								HHSConstants.STATUS_CHANGE, ApplicationConstants.STATUS_CHANGED_FROM
										+ HHSConstants.SPACE + HHSConstants.STR + HHSConstants.TASK_RFR
										+ HHSConstants.STR + HHSConstants.TO + HHSConstants.STR
										+ HHSConstants.TASK_WITHDRAWN + HHSConstants.STR, HHSConstants.AUDIT_INVOICES,
								aoAuthenticationBean.getInvoiceId(), lsUserId, HHSConstants.PROVIDER_AUDIT));
						loChannel.setData(HHSConstants.LO_AUDIT_LIST, loAuditList);
						HHSTransactionManager.executeTransaction(loChannel, HHSConstants.WITHDRAW_INVOICE_ID);
						Boolean loWithdrawInvoiceSuccess;
						loWithdrawInvoiceSuccess = (Boolean) loChannel.getData(HHSConstants.INVOICE_LB_STATUS);

						if (loWithdrawInvoiceSuccess == null || !loWithdrawInvoiceSuccess)
						{
							lsErrorMsg = HHSConstants.WITHDRAWINVOICE_FAILURE;
						}
						else
						{
							aoResourceRequest.getPortletSession().setAttribute(
									HHSConstants.TRANSACTION_SUCCESS,
									PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
											HHSConstants.WITHDRAW_INVOICE), PortletSession.APPLICATION_SCOPE);
						}
					}

				}
				else
				{
					lsErrorMsg = HHSConstants.WITHDRAWINVOICE_FAILURE;
				}
			}
			catch (ApplicationException aoExe)
			{
				lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
				LOG_OBJECT.Error("ApplicationException occured in Withdraw Invoice", aoExe);
			}
			finally
			{
				catchTaskError(loOut, lsErrorMsg);
			}
		}
		return loModelAndView;

	}

	/**
	 * This method is called when any column header to sort the table or any
	 * page no to fetch next set of records or when we need to apply filter for
	 * invoices on Invoice list screen.
	 * 
	 * <ul>
	 * <li>1. We get the lsNextAction from request parameters and lsUserOrgType
	 * from session attributes.</li>
	 * <li>2. Now, if lsNextAction is sortInvoiceList that means user has
	 * clicked on one of the table headers to sort the table.
	 * <ul>
	 * <li>i)sorttype and columnname are retrieved using parseQueryString()
	 * method in PortalUtil.</li>
	 * <li>ii)SortOnColumnBean is populated with information of
	 * firstSort,secondSort, firstSortType, secondSortType depending on the
	 * column header selected. This information is retrieved by
	 * getSortDetailsFromXML() method from SortConfiguration.xml.</li>
	 * <li>iii)Then invoicelistfilter object is populated with information from
	 * SortOnColumnBean.</li>
	 * <li>iv)Then we calculate paging params through getPagingParams method
	 * depending on page no on which user is and maximum no of records which we
	 * can display on a page. pageIndex,startNode,endNode in loInvoiceFilterBean
	 * object are set by this method.</li>
	 * <li>v)then sortType and columnName are set in session and default render
	 * method is called to display the page</li>
	 * </ul>
	 * </li>
	 * <li>3.else if lsNextAction is fetchNextInvoices,
	 * <ul>
	 * <li>i)getSortParams method is called to set the default sort types for
	 * table.</li>
	 * <li>ii)Then nextpageParam is set in response parameter.</li>
	 * <li>iii)loInvoiceFilter is set in Session and default render is called to
	 * display the page.</li>
	 * </ul>
	 * </li>
	 * <li>4.else if lsNextAction is filterInvoices,
	 * <ul>
	 * <li>i)filterInvoices method is called to set filter parameter in
	 * InvoiceListFilter bean.</li>
	 * <li>ii)Then nextpageParam is set in response parameter.</li>
	 * <li>iii)loInvoiceFilter is set in Session and default render is called to
	 * display the page.</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @see PortalUtil#parseQueryString(javax.servlet.http.HttpServletRequest,
	 *      String)
	 * @see BaseController#getSortDetailsFromXML(String, String, String, String)
	 * @see BaseController#getSortParams(SortOnColumnsBean,
	 *      com.nyc.hhs.model.BaseFilter, String, String, String, String)
	 * @see BaseController#getPagingParams(PortletSession,
	 *      com.nyc.hhs.model.BaseFilter, String, String)
	 * @param aoInvoiceBean InvoiceList object
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse object
	 */
	@ActionMapping(params = "submit_action=filterInvoices")
	protected void actionInvoicesFilter(@ModelAttribute("InvoiceList") InvoiceList aoInvoiceBean,
			ActionRequest aoRequest, ActionResponse aoResponse)
	{

		PortletSession loPortletSession = aoRequest.getPortletSession();
		String lsNextAction = aoRequest.getParameter(ApplicationConstants.NEXT_ACTION);
		String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
		String lsProviderId = null;

		try
		{
			if (null != aoRequest.getParameter(HHSConstants.PROVIDER_ID)
					&& !aoRequest.getParameter(HHSConstants.PROVIDER_ID).equalsIgnoreCase(HHSR5Constants.EMPTY_STRING))
			{
				lsProviderId = (String) aoRequest.getParameter(HHSConstants.PROVIDER_ID);
				ApplicationSession.setAttribute(aoRequest.getParameter(HHSConstants.PROVIDER_ID), aoRequest,
						HHSConstants.PROVIDER_ID);
				loPortletSession.setAttribute(HHSConstants.PROVIDER_ID,
						aoRequest.getParameter(HHSConstants.PROVIDER_ID), PortletSession.APPLICATION_SCOPE);
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
			if (lsNextAction != null)
			{
				if (lsNextAction.equalsIgnoreCase(HHSConstants.SORT_INVOICE_LIST))
				{
					String lsSortType = PortalUtil.parseQueryString(aoRequest, HHSConstants.SORT_TYPE);
					String lsColumnName = PortalUtil.parseQueryString(aoRequest, HHSConstants.COLUMN_NAME);

					getSortDetailsFromXML(lsColumnName, lsUserOrgType,
							PortalUtil.parseQueryString(aoRequest, HHSConstants.SORT_GRID_NAME), aoInvoiceBean,
							lsSortType);
				}
				else if (lsNextAction.equalsIgnoreCase(HHSConstants.FETCH_NEXT_INVOICES))
				{
					ApplicationSession.setAttribute(aoRequest.getParameter(HHSConstants.NEXT_PAGE), aoRequest,
							HHSConstants.NEXT_PAGE_PARAM);
					ApplicationSession.setAttribute(aoRequest.getParameter(HHSConstants.NEXT_PAGE), aoRequest,
							HHSConstants.OVERLAY_PAGE_PARAM_INVOICE);
				}
				else if (lsNextAction.equalsIgnoreCase(HHSConstants.FILTER_INVOICE))
				{
					aoInvoiceBean.setIsFilter(true);
					aoInvoiceBean.setDefaultSortData(lsUserOrgType);
					// Updated for Emergency Build 4.0.0.2 defect 8360, 8377
					removeRedirectFromListSessionData(loPortletSession);
				}
			}
			ApplicationSession.setAttribute(aoInvoiceBean, aoRequest, HHSConstants.INVOICE_SESSION_BEAN);
			ApplicationSession.setAttribute(aoInvoiceBean, aoRequest, HHSConstants.OVER_LAY_INVOICE);
			ApplicationSession.setAttribute(lsProviderId, aoRequest, HHSConstants.PROVIDER_ID);
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.INVOICE_LIST_ACTION);

		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Error occured in Invoice Filter page ", aoAppExp);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, ApplicationConstants.ERROR_MESSAGE_FILENET_DOWN);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error occured in Invoice Filter page ", aoExp);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, ApplicationConstants.ERROR_MESSAGE_FILENET_DOWN);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
	}

	/**
	 * This method view's the Invoice. There will be different invoice displayed
	 * on the S309 & S310, against each invoice there will be view Invoice
	 * hyperlink on the click of which the invoice will be displayed. The in
	 * <ul>
	 * <li>1.Get the required info i.e InvoiceId,USerName,Password and pass the
	 * value to channel</li>
	 * <li>2.Call the viewInvoice Transaction</li>
	 * <ul>
	 * 
	 * @param aoRequest Resource Request Object
	 * @param aoResponse Resource Response Object
	 * @throws ApplicationException
	 */
	@ActionMapping(params = "launchInvoice=contractInvoiceScreen")
	public void actionViewInvoice(ActionRequest aoRequest, ActionResponse aoResponse) throws ApplicationException
	{
		PortletSession loPortletSession = aoRequest.getPortletSession();
		Channel loChannelObj = new Channel();
		HashMap<String, Object> aoHashMap = new HashMap<String, Object>();
		String lsErrorMsg = HHSConstants.EMPTY_STRING;

		try
		{
			String lsBudgetId = aoRequest.getParameter(HHSConstants.BUDGET_ID_WORKFLOW);
			String lsContractId = aoRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW);
			String lsFiscalYearID = aoRequest.getParameter(HHSConstants.FISCAL_YEAR_ID);
			String lsAgencyId = aoRequest.getParameter(HHSConstants.AGENCYID);
			String lsProgramId = aoRequest.getParameter(HHSConstants.PROGRAM_ID);
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			String lsOrgId = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
					PortletSession.APPLICATION_SCOPE);
			aoHashMap.put(HHSConstants.ORG_ID, lsOrgId);
			aoHashMap.put(HHSConstants.AGENCYID, lsAgencyId);
			aoHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, lsBudgetId);
			aoHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, lsContractId);
			aoHashMap.put(HHSConstants.FISCAL_YEAR_ID, lsFiscalYearID);
			aoHashMap.put(HHSConstants.USER_ID, lsUserId);
			aoHashMap.put(HHSConstants.PROGRAM_ID, lsProgramId);
			P8UserSession loUserSession = (P8UserSession) loPortletSession.getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			loChannelObj.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
			loChannelObj.setData(HHSConstants.BUDGET_ID_KEY, lsBudgetId);
			loChannelObj.setData(HHSConstants.CONTRACT_ID_KEY, lsContractId);
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.CBY_CHECK_SUBMIT_INVOICE_FEASIBILITY);
			HashMap<String, String> loStatusError = (HashMap<String, String>) loChannelObj
					.getData(HHSConstants.IS_STATUS_ERROR);
			// add OPeration and BUdgetList in HashMap
			aoHashMap.put(HHSConstants.PARAM_KEY_OPERATION, HHSConstants.INVOICE);
			// Put HashMap in channel
			loChannelObj.setData(HHSConstants.AO_HMWF_REQUIRED_PROPS, aoHashMap);

			if (loStatusError.get(HHSConstants.IS_SUCCESS).equals(HHSConstants.TRUE))
			{
				// Create invoice in DB
				HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.INV_INSERT_INVOICE);

				Integer lsInvoiceId = (Integer) loChannelObj.getData(HHSConstants.AI_CURRENT_SEQ);
				String lsProcurementSummaryPath = aoRequest.getScheme() + HHSConstants.NOTIFICATION_HREF_1
						+ aoRequest.getServerName() + HHSConstants.COLON + aoRequest.getServerPort()
						+ aoRequest.getContextPath() + ApplicationConstants.PORTAL_URL
						+ HHSConstants.PAGE_LABEL_PORTAL_CONTRACT_INVOICE_PAGE_URL + HHSConstants.CONTRACT_ID_URL
						+ aoRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW) + HHSConstants.BUDGET_ID_URL
						+ aoRequest.getParameter(HHSConstants.BUDGET_ID_WORKFLOW) + HHSConstants.FISCAL_YEAR_URL
						+ aoRequest.getParameter(HHSConstants.FISCAL_YEAR_ID) + HHSConstants.INVOICE_ID_URL
						+ lsInvoiceId.toString();
				/** [Start] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
				//aoResponse.sendRedirect(lsProcurementSummaryPath);
				aoResponse.sendRedirect(HHSUtil.sanitizeCarriageReturns(lsProcurementSummaryPath));
				/** [End] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
			}
			else
			{
				lsErrorMsg = loStatusError.get(HHSConstants.ERROR_MESSAGE);

				loPortletSession.setAttribute(HHSConstants.IS_SUBMIT_INVOICE_ERROR_MSG, lsErrorMsg,
						PortletSession.APPLICATION_SCOPE);
				aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.CBL_BUDGET_LIST_ACTION);
			}
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error(HHSConstants.CBL_EXCEPTION_CONTRACT_BUDGET_DETAILS, loExp);
			lsErrorMsg = HHSConstants.ERROR_WHILE_PROCESSING_REQUEST;
			aoRequest.setAttribute(HHSConstants.ERROR_MESSAGE_BUDGET_LIST, lsErrorMsg);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
	}

	/**
	 * This method delete the Invoice
	 * <ul>
	 * <li>1.Get the required info i.e invoice Id,USerName,Password and pass the
	 * value to channel</li>
	 * <li>2.Call the deleteInvoice Transaction</li>
	 * <ul>
	 * Modified this method for defect id 6445 release 3.3.0
	 * @param aoAuthenticationBean AuthenticationBean object
	 * @param aoResult Result object
	 * @param aoResourceRequest Resource Request Object
	 * @param aoResourceResponse Resource Response Object
	 * @return ModelAndView Object
	 */
	@SuppressWarnings("rawtypes")
	@ResourceMapping("deleteInvoice")
	protected ModelAndView deleteInvoice(@ModelAttribute("deleteInvoice") AuthenticationBean aoAuthenticationBean,
			BindingResult aoResult, ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		ModelAndView loModelAndView = null;
		Channel loChannel = new Channel();
		String lsUserEmailId = aoResourceRequest.getParameter(HHSConstants.KEY_SESSION_USER_NAME);
		String lsPassword = aoResourceRequest.getParameter(HHSConstants.PASSWORD);
		Boolean loValidCredentials = false;
		Boolean loDeleteInvoiceSuccess = false;
		PrintWriter loOut = null;
		String lsErrorMsg = HHSConstants.EMPTY_STRING;
		int liErrorCode = 0;
		aoResourceResponse.setContentType(HHSConstants.TEXT_HTML);
		HashMap loHmDocReqProps = new HashMap();
		P8UserSession loUserSession = (P8UserSession) aoResourceRequest.getPortletSession().getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		String lsInvoiceId = null;
		String lsNextAction = aoResourceRequest.getParameter(ApplicationConstants.NEXT_ACTION);
		
		if (lsNextAction != null && lsNextAction.equalsIgnoreCase(HHSConstants.DELETE_INVOICE))
		{   
			loModelAndView = new ModelAndView(HHSConstants.DELETE_INVOICE_CONFIRMATION);
			lsInvoiceId = aoResourceRequest.getParameter(HHSConstants.INVOICE_ID);
			aoResourceRequest.setAttribute(HHSConstants.INVOICE_ID, lsInvoiceId);
		}
		else
		{
			try
			{  
				validator.validate(aoAuthenticationBean, aoResult);
				
				if (!aoResult.hasErrors())
				{   
					loOut = aoResourceResponse.getWriter();
					loChannel.setData(HHSConstants.INVOICE_AO_AUTH_BEAN1, aoAuthenticationBean);
					loChannel.setData(HHSConstants.INVOICE_ID_KEY, aoAuthenticationBean.getInvoiceId());
					// LDAP Login Credentials Check.
					@SuppressWarnings("rawtypes")
					Map loValidateMap = validateUser(lsUserEmailId, lsPassword, aoResourceRequest);
					loValidCredentials = (Boolean) loValidateMap.get(HHSConstants.IS_VALID_USER);
					
					if (loValidCredentials == null || !loValidCredentials)
					{
						lsErrorMsg = HHSConstants.INVALID_USER_MSG;
						
					}
					else
					{   
						// made changes for defect id 6445 release 3.3.0
						loHmDocReqProps.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, false);
						loChannel.setData(HHSConstants.LO_HM_DOC_REQ_PROPS, loHmDocReqProps);
						loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
						loChannel.setData(HHSConstants.AUTH_STATUS_FLAG, loValidCredentials);
						HHSTransactionManager.executeTransaction(loChannel, HHSConstants.DELETE_INVOICE_ID);
						loDeleteInvoiceSuccess = (Boolean) loChannel.getData(HHSConstants.INVOICE_LB_STATUS);
						if (loDeleteInvoiceSuccess == null || !loDeleteInvoiceSuccess)
						{
							lsErrorMsg = HHSConstants.DELETE_INVOICE_FAILURE;
						}
						else
						{
							aoResourceRequest.getPortletSession().setAttribute(
									HHSConstants.TRANSACTION_SUCCESS,
									PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
											HHSConstants.DELETE_INVOICE), PortletSession.APPLICATION_SCOPE);
						}
					}

				}
			}
			catch (ApplicationException aoExe)
			{
				lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
				LOG_OBJECT.Error("ApplicationException occured in Delete invoice", aoExe);
				// R 7.8
				lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			}
			catch (IOException aoException)
			{
				LOG_OBJECT.Error("IOException occured in Delete invoice", aoException);
			}
			finally
			{
				catchTaskError(loOut, lsErrorMsg);
			}
		}
		return loModelAndView;

	}

	/**
	 * This method will get the payment CT id from the cache when user type
	 * three characters using EpinList method defined in basecontroller.
	 * 
	 * @param aoRequest a ResourceRequest Object
	 * @param aoResponse a ResourceResponse Object
	 * @return ModelAndView object.
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             ApplicationException
	 */
	@ResourceMapping("getInvoiceCtResourceUrl")
	public void getEpinListResourceRequest(ResourceRequest aoRequest, ResourceResponse aoResponse)
			throws ApplicationException
	{
		try
		{
			getEpinList(aoRequest, aoResponse);
		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Error occured while processing invoices CT id Type head", aoAppExp);
		}
	}

	/**
	 * This method print the error using print Writer object
	 * 
	 * @param aoOut PrintWriter object
	 * @param asError String error
	 */
	private void catchTaskError(PrintWriter aoOut, String asError)
	{
		aoOut.print(asError);
		aoOut.flush();
		aoOut.close();
	}
	

}
