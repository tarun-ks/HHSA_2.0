package com.nyc.hhs.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.validation.Valid;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
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
import com.nyc.hhs.model.AccountsAllocationBean;
import com.nyc.hhs.model.AuthenticationBean;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.ContractBean;
import com.nyc.hhs.model.ContractBudgetBean;
import com.nyc.hhs.model.ContractCOFDetails;
import com.nyc.hhs.model.ContractList;
import com.nyc.hhs.model.EPinDetailBean;
import com.nyc.hhs.model.FundingAllocationBean;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.NotificationDataBean;
import com.nyc.hhs.model.Procurement;
import com.nyc.hhs.model.ProcurementCOF;
import com.nyc.hhs.model.ProgramNameInfo;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.ApplicationSession;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.FileNetOperationsUtils;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.JSONUtility;
import com.nyc.hhs.util.MultipartActionRequestParser;
import com.nyc.hhs.util.PortalUtil;
import com.nyc.hhs.util.PortletSessionHandler;
import com.nyc.hhs.util.PropertyLoader;

/**
 * <p>
 * ContractListController serves as the controller for contract list page.
 * Contractlist page for Accelerator/Agency/Provider is rendered through
 * handleRenderRequestInternal method of this controller. All the actions on
 * contractlist page are handled by this controller e.g. sorting on column
 * headers, pagination, filteration on list page based on different criterias.
 * </p>
 * <p>
 * This class is updated in Release 7
 * </p>
 */

@Controller(value = "contractListAction")
@RequestMapping("view")
public class ContractListController extends BaseController
{
	private static final LogInfo LOG_OBJECT = new LogInfo(ContractListController.class);
	@Autowired
	private Validator validator;

	public void setValidator(Validator validator)
	{
		this.validator = validator;
	}

	@ModelAttribute("ContractList")
	public ContractList getCommandObject()
	{
		return new ContractList();
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

	@InitBinder
	public void initBinder(WebDataBinder binder)
	{
		SimpleDateFormat loFormat = new SimpleDateFormat(HHSConstants.MMDDYYFORMAT);
		loFormat.setLenient(true);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(loFormat, true, 10));
	}

	/**
	 * This method is default render method to display Contract list screen for
	 * Acclerator/Agency/Provider.
	 * <ul>
	 * <li>1. We get lsOrgnizationType from session to determine the type of
	 * user and user id which is then passed to channel to get the required
	 * information for that user.</li>
	 * <li>2. Then we check if user has already applied any filters on the
	 * screen( whether ContractList object is there in session or not) If not,
	 * then we set it to default filters. Contracts are by default sorted by
	 * first Status in Ascending order and then last update date in descending
	 * order.</li>
	 * <li>3. We also get the lsUserOrg from session in which we get the
	 * organization id which is then set in loContractFilterBean to get the
	 * corresponding data.</li>
	 * <li>4. Then we calculate paging params through getPagingParams method
	 * depending on page no on which user is and maximum no of records which we
	 * can display on a page. pageIndex,startNode,endNode in
	 * loContractFilterBean object are set by this method.</li>
	 * <li>5. We call TransactionManagerR2 to execute getFinancialsList
	 * transaction which hits the database to get the required contracts list in
	 * aoCBContractListBean, total contracts count, contracts value, program
	 * name list and agency list.</li>
	 * <li>6. Then we set all data in in request attributes which can be used in
	 * contractlist.jsp.</li>
	 * <li>7. On First load, contracts with with <Contract Status> of
	 * ?Suspended?, ?Closed? and ?Cancelled? will not be shown in the list.</li>
	 * <li>8. We are checking whether we are coming from home page or directly
	 * to Financials page based on that we are displaying the Contract List.</li>
	 * </ul>
	 * 
	 * @see BaseController#getSortParams(SortOnColumnsBean,
	 *      com.nyc.hhs.model.BaseFilter, String, String, String, String)
	 * @see BaseController#getPagingParams(PortletSession,
	 *      com.nyc.hhs.model.BaseFilter, String, String)
	 * @param aoRequest a RenderRequest object
	 * @param aoResponse a RenderResponse object
	 * @return loModelAndView ModelAndView object to be returned
	 * @throws ApplicationException
	 */
	@RenderMapping
	protected ModelAndView handleRenderRequestInternal(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		ModelAndView loModelAndView = new ModelAndView(HHSConstants.CONTRACT_LIST_JSP);
		try
		{
			loModelAndView = mainRenderMethod(aoRequest, null, null);
		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Error occured in contract list screen ", aoAppExp);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, ApplicationConstants.ERROR_MESSAGE_FILENET_DOWN);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error occured in contract list screen ", aoExp);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, ApplicationConstants.ERROR_MESSAGE_FILENET_DOWN);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
		return loModelAndView;
	}

	/**
	 * This is the default render method for Contract list page and displays the
	 * list of all contracts
	 * 
	 * based on type of user logged in.
	 * 
	 * <ul>
	 * <li>The method calls the transaction 'fetchContractListSummary'</li>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * @param aoRequest - RenderRequest Object
	 * @return model and view of the requested jsp page
	 * @throws ApplicationException -ApplicationException Object
	 * 
	 * 
	 */
	@SuppressWarnings("unchecked")
	private ModelAndView mainRenderMethod(RenderRequest aoRequest, String asOverlayPageParam, String asOverLay)
			throws ApplicationException
	{
		PortletSession loPortletSession = aoRequest.getPortletSession();
		boolean lbFirstLoad = false;
		String lsProviderId = null;
		String lsOrgnizationType = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
				PortletSession.APPLICATION_SCOPE);
		String lsUserOrg = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
				PortletSession.APPLICATION_SCOPE);
		String lsRole = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ROLE,
				PortletSession.APPLICATION_SCOPE);
		// Release 5 added
		String lsUserId = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsPermissionType = (String) loPortletSession.getAttribute(HHSConstants.PERMISSION_TYPE,
				PortletSession.APPLICATION_SCOPE);
		// Ends of R5 changes
		List<ProgramNameInfo> loProgramNameList = (List<ProgramNameInfo>) loPortletSession.getAttribute(
				HHSConstants.PROGRAM_NAME_LIST, PortletSession.APPLICATION_SCOPE);
		List<HashMap<String, String>> loAgencyDetails = (List<HashMap<String, String>>) loPortletSession.getAttribute(
				HHSConstants.AGENCY_DETAILS, PortletSession.APPLICATION_SCOPE);
		String lsNavigateFromR2 = (String) PortalUtil.parseQueryString(aoRequest, HHSConstants.NAVIGATE_FROM_R2);
		ContractList loContractFilterBean = null;
		try
		{
			if (null != lsNavigateFromR2 && lsNavigateFromR2.equalsIgnoreCase(HHSConstants.NAVIGATE_FROM_R2))
			{
				showContractCOFPage(aoRequest);
				ModelAndView loModelAndView = renderContractCOF(aoRequest);
				return loModelAndView;
			}
			else
			{
				Channel loChannelObj = new Channel();
				loChannelObj.setData(HHSConstants.ORGTYPE, lsOrgnizationType);
				String lsNextPage = null;
				String lbRequestFromHomePage = HHSConstants.FALSE;
				if (null != PortalUtil.parseQueryString(aoRequest, HHSConstants.FROM_HOME_PAGE))
				{
					lbRequestFromHomePage = PortalUtil.parseQueryString(aoRequest, HHSConstants.FROM_HOME_PAGE);
				}

				if (asOverlayPageParam != null && asOverLay != null)
				{
					lsNextPage = (String) ApplicationSession.getAttribute(aoRequest, asOverlayPageParam);
					loContractFilterBean = (ContractList) ApplicationSession.getAttribute(aoRequest, true, asOverLay);
				}
				else
				{
					lsNextPage = (String) ApplicationSession.getAttribute(aoRequest, HHSConstants.NEXT_PAGE_PARAM);
					loContractFilterBean = (ContractList) ApplicationSession.getAttribute(aoRequest, true,
							HHSConstants.CONTRACT_SESSION_BEAN);
					removeOtherScreenBeanFromSession(loPortletSession);
					loPortletSession.removeAttribute(HHSConstants.AMENDED_CONTRACT_SESSION_BEAN);
				}

				// check whether we are navigating from home page or from some
				// other
				// page
				if (null != lsOrgnizationType && lsOrgnizationType.equals(ApplicationConstants.PROVIDER_ORG)
						&& !lbRequestFromHomePage.equals(HHSConstants.FALSE))
				{
					loContractFilterBean = getFinancialBeanForProviderHomePages(aoRequest, loContractFilterBean);
				}

				else if (null != lsOrgnizationType
						&& (lsOrgnizationType.equals(ApplicationConstants.CITY_ORG) || lsOrgnizationType
								.equals(ApplicationConstants.AGENCY_ORG))
						&& !lbRequestFromHomePage.equals(HHSConstants.FALSE))
				{
					loContractFilterBean = getFinancialBeanForCityAndAgencyHomePages(aoRequest, loContractFilterBean);
				}

				if (null == loContractFilterBean)
				{
					loContractFilterBean = new ContractList();
					lbFirstLoad = true;
					loContractFilterBean = setDefaultContractBean(lsOrgnizationType);
				}
				// Start : R5 Added
				// Updated for Emergency Build 4.0.0.2 defect 8360, 8377
				String lbFromFinancialTab = PortalUtil.parseQueryString(aoRequest, HHSR5Constants.FROM_FINANCIAL_TAB);
				if (null != lbFromFinancialTab && lbFromFinancialTab.equals(HHSConstants.TRUE))
				{
					removeRedirectFromListSessionData(loPortletSession);
				}
				String lsContractIdForContractList = (String) loPortletSession.getAttribute(
						HHSR5Constants.CONTRACT_ID_FOR_LIST, PortletSession.APPLICATION_SCOPE);
				if (StringUtils.isNotBlank(lsContractIdForContractList))
				{
					// Defect 8103 change starts
					loContractFilterBean = setDefaultContractBean(lsOrgnizationType);
					loPortletSession.removeAttribute(HHSConstants.AMENDED_CONTRACT_SESSION_BEAN);
					// Defect 8103 change ends
					loContractFilterBean.setContractId(lsContractIdForContractList);
				}
				String lsProviderNameForOrganization = (String) loPortletSession.getAttribute(
						HHSR5Constants.PROPERTY_PE_PROVIDER_NAME, PortletSession.APPLICATION_SCOPE);
				if (StringUtils.isNotBlank(lsProviderNameForOrganization))
				{
					loContractFilterBean.setProvider(StringEscapeUtils.unescapeJavaScript((String) loPortletSession
							.getAttribute(ApplicationConstants.KEY_SESSION_ORG_NAME_CITY,
									PortletSession.APPLICATION_SCOPE)));
					loPortletSession.removeAttribute(HHSR5Constants.PROPERTY_PE_PROVIDER_NAME,
							PortletSession.APPLICATION_SCOPE);
				}
				// End : R5 Added

				lsProviderId = (String) ApplicationSession.getAttribute(aoRequest, HHSConstants.PROVIDER_ID);
				if (null != lsProviderId)
				{
					loContractFilterBean.setProviderId(lsProviderId);
				}
				loContractFilterBean.setOrgName(lsUserOrg);
				loContractFilterBean.setOrgType(lsOrgnizationType);
				String lsContractTitle = null;
				if (loContractFilterBean.getContractTitle() != null)
				{
					lsContractTitle = loContractFilterBean.getContractTitle();
					loContractFilterBean.setContractTitle(loContractFilterBean.getContractTitle().replaceAll(
							HHSConstants.STR, HHSConstants.STR_DOUBLE));
				}
				getPagingParams(loPortletSession, loContractFilterBean, lsNextPage, HHSConstants.CONTRACT_LIST_KEY);
				loContractFilterBean.setUserIdContractRestriction(lsUserId);
				loChannelObj.setData(HHSConstants.CONTRACT_FILTER_BEAN, loContractFilterBean);
				loChannelObj.setData(HHSConstants.ORG_ID, lsUserOrg);
				loChannelObj.setData(HHSConstants.AS_ORG_TYPE, lsOrgnizationType);
				HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.GET_FINANCIALS_LIST);
				List<ContractList> loContractList = (List<ContractList>) loChannelObj
						.getData(HHSConstants.CB_CONTRACT_LIST_BEAN);

				/*[Start] R7.2.0 QC8914	Set indicator for Access control	 */
				PortletSession loSession  = aoRequest.getPortletSession();
				if( loSession.getAttribute(ApplicationConstants.KEY_SESSION_ROLE_CURRENT, PortletSession.APPLICATION_SCOPE) != null
				        && ApplicationConstants.ROLE_OBSERVER.equalsIgnoreCase((String)loSession.getAttribute(ApplicationConstants.KEY_SESSION_ROLE_CURRENT, PortletSession.APPLICATION_SCOPE)))  {
				    setIndecatorForReadOnlyRole(loContractList, ApplicationConstants.ROLE_OBSERVER);
				}
                /*[End] R7.2.0 QC8914 Set indicator for Access control     */

				// Release 5 User Notification
				if (lsPermissionType != null
						&& (lsPermissionType.equalsIgnoreCase(ApplicationConstants.ROLE_READ_ONLY) || lsPermissionType
								.equalsIgnoreCase(ApplicationConstants.ROLE_PROCUREMENT))
						&& lsOrgnizationType.equalsIgnoreCase(HHSConstants.PROVIDER_ORG) && loContractList != null)
				{
					for (ContractList loContract : loContractList)
					{
						loContract.setUserAccess(false);
					}
				}
				// Release 5 User Notification

				if (null != loContractList)
				{
					for (ContractList loContractListBean : loContractList)
					{
						loContractListBean.setActions(lsRole);
					}
				}
				if (loContractFilterBean.getContractTitle() != null && lsContractTitle != null)
				{
					loContractFilterBean.setContractTitle(lsContractTitle);
				}
				Integer loContractsCount = (Integer) loChannelObj.getData(HHSConstants.CONTRACT_COUNT);
				String loContractsAmount = (String) loChannelObj.getData(HHSConstants.CONTRACTS_VALUE);
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
				aoRequest.setAttribute(HHSConstants.FINANCIALS_LIST, loContractList);
				aoRequest.setAttribute(HHSConstants.FIRST_LOAD, lbFirstLoad);
				aoRequest.setAttribute(HHSConstants.TOTAL_COUNT, ((loContractsCount == null) ? 0 : loContractsCount));
				aoRequest.setAttribute(HHSConstants.CONTRACTS_VALUE, ((loContractsAmount == null) ? 0
						: loContractsAmount));
				loPortletSession.setAttribute(ApplicationConstants.ALERT_VIEW_PAGING_RECORDS,
						((loContractsCount == null) ? 0 : loContractsCount), PortletSession.APPLICATION_SCOPE);
				aoRequest.getPortletSession().setAttribute(HHSConstants.SORT_TYPE,
						loContractFilterBean.getFirstSortType(), PortletSession.APPLICATION_SCOPE);
				aoRequest.getPortletSession().setAttribute(HHSConstants.SORT_BY,
						loContractFilterBean.getSortColumnName(), PortletSession.APPLICATION_SCOPE);
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
						PortalUtil.parseQueryString(aoRequest, HHSConstants.CBL_MESSAGE));
			}
		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Error occured in contract list screen ", aoAppExp);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, ApplicationConstants.ERROR_MESSAGE_FILENET_DOWN);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error occured in contract list screen ", aoExp);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, ApplicationConstants.ERROR_MESSAGE_FILENET_DOWN);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
		return new ModelAndView(HHSConstants.CONTRACT_LIST_JSP, HHSConstants.CONTRACT_LIST_KEY, loContractFilterBean);
	}

	 /**
     * This method will handle setting indicator setIndecatorForReadOnlyRole ApplicationConstants.ROLE_OBSERVER 
     * for access control.
     * @param List<ContractList> loContractList
     * @throws ApplicationException
     */
	private void setIndecatorForReadOnlyRole(List<ContractList> aoContractList, String asUserRole) {
	    if( aoContractList == null || aoContractList.isEmpty() == true)  return;

	    for( ContractList  conObj :   aoContractList){
	        conObj.setUserSubRole(asUserRole);
	    }
	}

	/**
	 * This method will handle all the resource request when user(provider)
	 * clicks on count in Financial portlet on home page. * This method returns
	 * the bean based on the filter criteria on home page.
	 * <ul>
	 * ========================================<br/>
	 * Conditions to be checked in this method<br/>
	 * ========================================<br/>
	 * <li>Get Contract Filter bean from session.</li>
	 * <li>Condition 1:If user clicks on link Contracts pending registration set
	 * the required parameters such as contract status should be pending
	 * Registration into bean and return the bean to method mainRenderMethod</li>
	 * <li>Return Contract Filter bean to the method mainRenderMethod.</li>
	 * </ul>
	 * @param aoRequest RenderRequest Object
	 * @param loContractFilterBean ContractList object
	 * @return loContractFilterBean ContractList object
	 * @throws ApplicationException
	 */
	private ContractList getFinancialBeanForProviderHomePages(RenderRequest aoRequest, ContractList loContractFilterBean)
			throws ApplicationException
	{
		if (null == loContractFilterBean)
		{
			loContractFilterBean = new ContractList();
		}
		String lsFilterCriteria = PortalUtil.parseQueryString(aoRequest, HHSConstants.FILTER_CRITERIA);
		if (null != lsFilterCriteria && lsFilterCriteria.equalsIgnoreCase(HHSConstants.CONTRACTS_PEND_REGISTRATION))
		{
			List<String> loContractStatusList = new ArrayList<String>();
			loContractStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_CONTRACT_PENDING_REGISTARTION));
			loContractFilterBean.setContractStatusList(loContractStatusList);
		}
		return loContractFilterBean;
	}

	/**
	 * This method will handle all the resource request when user(city or
	 * agency) clicks on count in Financial portlet on home page. * This method
	 * returns the bean based on the filter criteria on home page.
	 * @param aoRequest RenderRequest object
	 * @param loContractFilterBean ContractList object
	 * @return ContractList object
	 * @throws ApplicationException
	 */
	private ContractList getFinancialBeanForCityAndAgencyHomePages(RenderRequest aoRequest,
			ContractList loContractFilterBean) throws ApplicationException
	{
		if (null == loContractFilterBean)
		{
			loContractFilterBean = new ContractList();
		}
		String lsFilterCriteria = PortalUtil.parseQueryString(aoRequest, HHSConstants.FILTER_CRITERIA);
		if (null != lsFilterCriteria)
		{
			if (lsFilterCriteria.equalsIgnoreCase(HHSConstants.CONTRACTS_PEND_CONFIG))
			{
				List<String> loContractStatusList = new ArrayList<String>();
				loContractStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_CONTRACT_PENDING_CONFIGURATION));
				loContractFilterBean.setContractStatusList(loContractStatusList);
			}
			else if (lsFilterCriteria.equalsIgnoreCase(HHSConstants.CONTRACTS_PEND_COF))
			{
				List<String> loContractStatusList = new ArrayList<String>();
				loContractStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_CONTRACT_PENDING_COF));
				loContractFilterBean.setContractStatusList(loContractStatusList);
			}
			else if (lsFilterCriteria.equalsIgnoreCase(HHSConstants.CONTRACTS_PEND_REGISTRATION))
			{
				List<String> loContractStatusList = new ArrayList<String>();
				loContractStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_CONTRACT_PENDING_REGISTARTION));
				loContractFilterBean.setContractStatusList(loContractStatusList);
			}
		}
		return loContractFilterBean;
	}

	@RenderMapping(params = "duplicate_render=duplicateRender")
	protected ModelAndView handleDuplicateRender(RenderRequest aoRequest, RenderResponse aoResponse)
			throws ApplicationException
	{
		return mainRenderMethod(aoRequest, HHSConstants.CLC_OVERLAY_PARAM, HHSConstants.CLC_OVERLAY);
	}

	@ActionMapping
	protected void handleActionRequestInternal(ActionRequest aoRequest, ActionResponse aoResponse)
			throws ApplicationException
	{
		String lsAction = aoRequest.getParameter(HHSConstants.TO_ACTION);
		if (lsAction != null)
		{
			aoResponse.setRenderParameter(HHSConstants.ACTION, lsAction);
		}
	}

	/**
	 * This method is trigerred when any column header to sort the table or any
	 * page no to fetch next set of records on Contract List Page or Filter
	 * Contracts on Contract List Filter for Acclerator/Agency/Provider is
	 * clicked.
	 * 
	 * <ul>
	 * <li>1. We get the lsNextAction from request parameters and lsUserOrgType
	 * from session attributes.</li>
	 * <li>2. Now, if lsNextAction is sortContractList that means user has
	 * clicked on one of the table headers to sort the table.
	 * <ul>
	 * <li>i)sorttype and columnname are retrieved using parseQueryString()
	 * method in PortalUtil.</li>
	 * <li>ii)sorttype and columnname are passed to getSortDetailsFromXML()
	 * method which gets the details from SOrtConfiguration.xml and sets the
	 * firstSort, secondSort, firstSortType, secondSortType in aoContractBean</li>
	 * <li>iii)Default render method is called to display the page</li>
	 * </ul>
	 * </li>
	 * <li>3.else if lsNextAction is fetchNextContracts,
	 * <ul>
	 * <li>i)next page parameter is set in nextPageParam .</li>
	 * <li>ii)Default render method is called to hit the query based on input
	 * params to display the page.</li>
	 * </ul>
	 * </li>
	 * <li>4.aoContractBean is set in session attribute so that it can be
	 * persisted across different actions performed by user.</li>
	 * </ul>
	 * 
	 * @see PortalUtil#parseQueryString(javax.servlet.http.HttpServletRequest,
	 *      String)
	 * @see BaseController#getSortDetailsFromXML(String, String, String, String)
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse object
	 * @return void
	 * @throws ApplicationException
	 */
	@ActionMapping(params = "submit_action=filterContracts")
	protected void actionContractsFilter(@ModelAttribute("ContractList") ContractList aoContractBean,
			ActionRequest aoRequest, ActionResponse aoResponse) throws ApplicationException
	{
		PortletSession loPortletSession = aoRequest.getPortletSession();
		String lsNextAction = aoRequest.getParameter(ApplicationConstants.NEXT_ACTION);
		String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
		String lsProviderId = (String) aoRequest.getParameter(HHSConstants.PROVIDER_ID);
		if (StringUtils.isNotBlank(lsProviderId))
		{
			ApplicationSession.setAttribute(lsProviderId, aoRequest, HHSConstants.PROVIDER_ID);
			loPortletSession.setAttribute(HHSConstants.PROVIDER_ID, lsProviderId, PortletSession.APPLICATION_SCOPE);
		}
		else if (StringUtils.isNotBlank((String) loPortletSession.getAttribute(HHSConstants.PROVIDER_ID,
				PortletSession.APPLICATION_SCOPE)))
		{
			lsProviderId = (String) loPortletSession.getAttribute(HHSConstants.PROVIDER_ID,
					PortletSession.APPLICATION_SCOPE);
		}
		else
		{
			lsProviderId = HHSR5Constants.EMPTY_STRING;
		}
		if (lsNextAction.equalsIgnoreCase(HHSConstants.SORT_CONTRACT_LIST))
		{
			String lsSortType = PortalUtil.parseQueryString(aoRequest, HHSConstants.SORT_TYPE);
			String lsColumnName = PortalUtil.parseQueryString(aoRequest, HHSConstants.COLUMN_NAME);
			getSortDetailsFromXML(lsColumnName, lsUserOrgType,
					PortalUtil.parseQueryString(aoRequest, HHSConstants.SORT_GRID_NAME), aoContractBean, lsSortType);

		}
		else if (lsNextAction.equalsIgnoreCase(HHSConstants.FETCH_NEXT_CONTRACTS))
		{
			ApplicationSession.setAttribute(aoRequest.getParameter(HHSConstants.NEXT_PAGE), aoRequest,
					HHSConstants.NEXT_PAGE_PARAM);
			ApplicationSession.setAttribute(aoRequest.getParameter(HHSConstants.NEXT_PAGE), aoRequest,
					HHSConstants.CLC_OVERLAY_PARAM);
		}
		else if (lsNextAction.equalsIgnoreCase(HHSConstants.FILTER_CONTRACTS))
		{
			aoContractBean.setFilterClicked(true);
			aoContractBean.setDefaultSortData();
			//added in R7 to clear flagged contract
			aoContractBean.setFilterFlaggedContracts(false);
			//R7 End
			// Updated for Emergency Build 4.0.0.2 defect 8360, 8377
			removeRedirectFromListSessionData(loPortletSession);
		}
		// added in R7 for show flagged contract filter
		else if (lsNextAction.equalsIgnoreCase(HHSR5Constants.FILTER_FLAGGED_CONTRACTS))
		{

			aoContractBean.setFilterClicked(false);
			aoContractBean.setDefaultSortData();
			aoContractBean.setFilterFlaggedContracts(true);
			removeRedirectFromListSessionData(loPortletSession);
		}
		// R7 End
		ApplicationSession.setAttribute(aoContractBean, aoRequest, HHSConstants.CONTRACT_SESSION_BEAN);
		ApplicationSession.setAttribute(lsProviderId, aoRequest, HHSConstants.PROVIDER_ID);
		ApplicationSession.setAttribute(aoContractBean, aoRequest, HHSConstants.CLC_OVERLAY);

	}

	/**
	 * Start: This method is added in R7 for defect 8644
	 * This method will be called when there will be request for the Mark as partial merge
	 * @param aoResourceRequest
	 * @param aoResourceResponse
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	@ResourceMapping("markAsRegistered")
	public void markAsRegistered(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse) throws Exception
	{
		Channel loChannel = new Channel();
		boolean lbUserValidation = false;
		String lsErrorMsg = HHSConstants.EMPTY_STRING;
		int liErrorCode = HHSConstants.INT_ZERO;
		String lsAmendContractId=aoResourceRequest.getParameter(HHSConstants.AMEND_CONTRACT_ID_WORKFLOW);
		String lsUserEmailId = aoResourceRequest.getParameter(HHSConstants.KEY_SESSION_USER_NAME);
		String lsPassword = aoResourceRequest.getParameter(HHSConstants.PASSWORD);
		try
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
				loChannel.setData(HHSConstants.CONTRACT_ID_KEY, lsAmendContractId);
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.UPDATE_REQUEST_PARTIAL_MERGE_CONTRACT_LIST);
				liErrorCode = HHSConstants.INT_ZERO;
				lsErrorMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.MSG_SUCCESSFULL_MARK_AS_REGISTERED_REQUEST);
				aoResourceRequest.getPortletSession().setAttribute(HHSConstants.SUCCESS_MESSAGE, lsErrorMsg,
						PortletSession.APPLICATION_SCOPE);
				
			}
		}
		catch (ApplicationException loEx)
		{
			liErrorCode = HHSConstants.INT_ONE;
			lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("Error occured while requesting for partial merge. ", loEx);

		}
		catch (Exception loExe)
		{
			liErrorCode = HHSConstants.INT_ONE;
			lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("Error occured while requesting for partial merge.", loExe);
		}
		finally
		{
			returnMessage(aoResourceResponse, liErrorCode, lsErrorMsg);
		}
		
	}
	//End
	
	/**
	 * This method cancel the Contract
	 * <ul>
	 * <li>1.Get the required info i.e contract Id,USerName,Password and pass
	 * the value to channel</li>
	 * <li>2.Call the cancelContract Transaction</li>
	 * <ul>
	 * 
	 * @param aoResourceRequest Resource Request Object
	 * @param aoResourceResponse Resource Response Object
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	@ResourceMapping("cancelContract")
	public void cancelContract(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse) throws Exception
	{
		Channel loChannel = new Channel();
		boolean lbUserValidation = false;
		Boolean lbCancelContractRule;
		Boolean lbAuditStatus;
		String lsErrorMsg = HHSConstants.EMPTY_STRING;
		int liErrorCode = HHSConstants.INT_ZERO;
		String lsContractId = aoResourceRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW);
		String lsUserEmailId = aoResourceRequest.getParameter(HHSConstants.KEY_SESSION_USER_NAME);
		String lsPassword = aoResourceRequest.getParameter(HHSConstants.PASSWORD);
		String lsCancelComment = aoResourceRequest.getParameter(HHSConstants.CANCEL_CONTRACT_COMMENT);
		// made changes for release 3.10.0 enhancement 5686
		String lsReuseEpin = aoResourceRequest.getParameter(HHSConstants.REUSE_EPIN);
		try
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
				PortletSession loPortletSession = aoResourceRequest.getPortletSession();
				String lsOrgnizationType = (String) loPortletSession.getAttribute(
						ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
				String lsUserId = (String) aoResourceRequest.getPortletSession().getAttribute(
						ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
				Object loUserSession = (P8UserSession) aoResourceRequest.getPortletSession().getAttribute(
						ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
				loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
				loChannel.setData(HHSConstants.AS_ORG_TYPE, lsOrgnizationType);
				loChannel.setData(HHSConstants.CONTRACT_ID_KEY, lsContractId);
				// made changes for release 3.10.0 enhancement 5686
				loChannel.setData(HHSConstants.REUSE_EPIN, lsReuseEpin);
				loChannel.setData(HHSConstants.AS_USER_ID, lsUserId);
				if (lsOrgnizationType != null && (lsOrgnizationType.equalsIgnoreCase(ApplicationConstants.CITY_ORG)))
				{
					HHSUtil.addAuditDataToChannel(loChannel, HHSConstants.CONTRACT_CANCELLATION,
							HHSConstants.CONTRACT_CANCELLATION, lsCancelComment, HHSConstants.CONTRACTS, lsContractId,
							lsUserId, HHSConstants.ACCELERATOR_AUDIT, HHSConstants.AUDIT_BEAN);
				}
				else if (lsOrgnizationType != null
						&& (lsOrgnizationType.equalsIgnoreCase(ApplicationConstants.AGENCY_ORG)))
				{
					HHSUtil.addAuditDataToChannel(loChannel, HHSConstants.CONTRACT_CANCELLATION,
							HHSConstants.CONTRACT_CANCELLATION, lsCancelComment, HHSConstants.CONTRACTS, lsContractId,
							lsUserId, HHSConstants.AGENCY_AUDIT, HHSConstants.AUDIT_BEAN);
				}

				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.CANCEL_CONTRACT);

				lbCancelContractRule = (Boolean) loChannel.getData(HHSConstants.ERROR_CHECK_RULE);
				lbAuditStatus = (Boolean) loChannel.getData(HHSConstants.LB_AUDIT_STATUS);
				if (!lbCancelContractRule)
				{
					liErrorCode = HHSConstants.INT_ONE;
					lsErrorMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSConstants.CANCEL_CONTRACT_RULE1);
				}
				else if (lbAuditStatus)
				{
					liErrorCode = HHSConstants.INT_ZERO;
					aoResourceRequest.getPortletSession().setAttribute(
							HHSConstants.SUCCESS_MESSAGE,
							PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
									HHSConstants.CANCEL_CONTRACT_STATUS), PortletSession.APPLICATION_SCOPE);
				}

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

	/**
	 * Added For Enhancement 6000 for Release 3.8.0 This method delete the
	 * Contract
	 * <ul>
	 * <li>1.Get the required info i.e contract Id,UserName,Password and pass
	 * the value to channel</li>
	 * <li>2.Call the deleteContract Transaction</li>
	 * <ul>
	 * 
	 * @param aoResourceRequest Resource Request Object
	 * @param aoResourceResponse Resource Response Object
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	@ResourceMapping("deleteContract")
	public void deleteContract(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse) throws Exception
	{
		Channel loChannel = new Channel();
		HashMap loHmDocReqProps = new HashMap();
		boolean lbUserValidation = false;
		Boolean lbDeleteContractRule;
		Boolean lbAuditStatus;
		String lsErrorMsg = HHSConstants.EMPTY_STRING;
		int liErrorCode = HHSConstants.INT_ZERO;
		String lsContractId = aoResourceRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW);
		String lsUserEmailId = aoResourceRequest.getParameter(HHSConstants.KEY_SESSION_USER_NAME);
		String lsPassword = aoResourceRequest.getParameter(HHSConstants.PASSWORD);
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		try
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
				PortletSession loPortletSession = aoResourceRequest.getPortletSession();
				String lsOrgnizationType = (String) loPortletSession.getAttribute(
						ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
				String lsUserId = (String) aoResourceRequest.getPortletSession().getAttribute(
						ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
				Object loUserSession = (P8UserSession) aoResourceRequest.getPortletSession().getAttribute(
						ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
				loNotificationMap = getNotificationMapForPendingBudgets(lsContractId, lsUserId);
				loHmDocReqProps.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, false);
				loChannel.setData(HHSConstants.LO_HM_DOC_REQ_PROPS, loHmDocReqProps);
				loChannel.setData(HHSConstants.LO_NOTIFICATION_MAP, loNotificationMap);
				loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
				loChannel.setData(HHSConstants.AS_ORG_TYPE, lsOrgnizationType);
				loChannel.setData(HHSConstants.CONTRACT_ID_KEY, lsContractId);
				loChannel.setData(HHSConstants.AS_USER_ID, lsUserId);
				if (lsOrgnizationType != null && (lsOrgnizationType.equalsIgnoreCase(ApplicationConstants.CITY_ORG)))
				{
					HHSUtil.addAuditDataToChannel(loChannel, HHSConstants.CONTRACT_DELETION,
							HHSConstants.CONTRACT_DELETION, HHSConstants.CONTRACT + HHSR5Constants.SPACE + lsContractId
									+ HHSR5Constants.SPACE + HHSConstants.DELETED, HHSConstants.CONTRACT, lsContractId,
							lsUserId, HHSConstants.ACCELERATOR_AUDIT, HHSConstants.AUDIT_BEAN);
				}
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.DELETE_CONTRACT);

				lbDeleteContractRule = (Boolean) loChannel.getData(HHSConstants.ERROR_CHECK_RULE);
				lbAuditStatus = (Boolean) loChannel.getData(HHSConstants.LB_AUDIT_STATUS);
				
				
				LOG_OBJECT.Info("Deleting process is done:lbDeleteContractRule["+lbDeleteContractRule+"]   lbAuditStatus[" + lbAuditStatus + "]");
				
				if (!lbDeleteContractRule)
				{
					liErrorCode = HHSConstants.INT_ZERO;
					aoResourceRequest.getPortletSession().setAttribute(
							HHSConstants.ERROR_MESSAGE,
							PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
									HHSConstants.DELETE_CONTRACT_RULE), PortletSession.APPLICATION_SCOPE);
				}
				else if (lbAuditStatus)
				{
					liErrorCode = HHSConstants.INT_ZERO;
					aoResourceRequest.getPortletSession().setAttribute(
							HHSConstants.SUCCESS_MESSAGE,
							PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
									HHSConstants.DELETE_CONTRACT_STATUS), PortletSession.APPLICATION_SCOPE);
				}
				
				LOG_OBJECT.Info("ERROR_MESSAGE["+aoResourceRequest.getPortletSession().getAttribute(HHSConstants.ERROR_MESSAGE)+"] \n" +
						"SUCCESS_MESSAGE[" + aoResourceRequest.getPortletSession().getAttribute(HHSConstants.SUCCESS_MESSAGE) + "]");
			}
		}
		catch (ApplicationException loEx)
		{
			liErrorCode = HHSConstants.INT_ONE;
			lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("Exception occured in deleteContract ", loEx);

		}
		catch (Exception loExe)
		{
			liErrorCode = HHSConstants.INT_ONE;
			lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("Exception occured in deleteContract ", loExe);
		}
		finally
		{
			returnMessage(aoResourceResponse, liErrorCode, lsErrorMsg);
		}

	}

	//R7: start
	private void setErrorMsgForJSPForContractBudget(ResourceResponse aoResResponse, String lsErrMsg, int liErrCode)
	{
		PrintWriter loOut = null;
		try
		{
			aoResResponse.setContentType(HHSConstants.APPLICATION_JSON);
			loOut = aoResResponse.getWriter();
			lsErrMsg = HHSConstants.ERROR_1 + liErrCode + HHSConstants.MESSAGE_1 + lsErrMsg
					+ HHSConstants.CLOSING_BRACE_1;
			loOut.print(lsErrMsg);
		}
		// Application Exception handled here
		catch (IOException loIOE)
		{
			// Log is generated in case of any Error and Error message
			// Error message is set for JSP
			LOG_OBJECT.Error("Exception occured in setErrorMsgJSP", loIOE);
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
	
	@SuppressWarnings("rawtypes")
	@ResourceMapping("validateUser")
	public void validateUserMR(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws ApplicationException
	{
		Map loValidateMap = null;
		Boolean lbUserValidation = false;
		String lsErrorMsg = HHSConstants.EMPTY_STRING;
		int liErrorCode = HHSConstants.INT_ZERO;
		try
		{
			String lsUserEmailId = aoResourceRequest.getParameter(HHSConstants.KEY_SESSION_USER_NAME);
			String lsPassword = aoResourceRequest.getParameter(HHSConstants.PASSWORD);
			loValidateMap = validateUser(lsUserEmailId, lsPassword, aoResourceRequest);
			lbUserValidation = (Boolean) loValidateMap.get(HHSConstants.IS_VALID_USER);
			if (!lbUserValidation)
			{
				// Validation error
				lsErrorMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.CB_VALIDATE_ERROR);
				liErrorCode = HHSConstants.INT_ONE;
				setErrorMsgForJSPForContractBudget(aoResourceResponse, lsErrorMsg, liErrorCode);
			}
		}
		// Application Exception handled here
		catch (ApplicationException aoAppExe)
		{
			LOG_OBJECT.Error("Exception occured in submitConfirmationOverlay", aoAppExe);
			// Application Exception occurred
			lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			liErrorCode = HHSConstants.INT_TWO;
			setErrorMsgForJSPForContractBudget(aoResourceResponse, lsErrorMsg, liErrorCode);
		}
	}
	
	//R7: end
	/**
	 * This method is added for Release 3.8.0 for enhancement request #6000. The
	 * method sets notification parameter map for Notification for Pending
	 * Budgets.
	 * @param asContractId contract id
	 * @param asUserId user id
	 * @return HashMap<String, Object> loNotificationMap
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	private HashMap<String, Object> getNotificationMapForPendingBudgets(String asContractId, String asUserId)
			throws ApplicationException
	{
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		try
		{
			Channel loChannel = new Channel();
			NotificationDataBean loNotificationDataBean = new NotificationDataBean();
			List<String> loNotificationAlertList = new ArrayList<String>();
			loNotificationAlertList.add(HHSConstants.NT_404);
			loChannel.setData(HHSConstants.CONTRACT_ID_KEY, asContractId);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_CONTRACT_TITLE_AND_ORGID);
			HashMap<String, String> loContractDetailMap = (HashMap<String, String>) loChannel
					.getData(HHSConstants.CONTRACT_DETAILS);
			HashMap<String, String> loLinkMap = new HashMap<String, String>();
			List<String> loProviderIdList = new ArrayList<String>();
			loProviderIdList.add(loContractDetailMap.get(HHSConstants.ORGANIZATION_ID_KEY));
			loNotificationDataBean.setProviderList(loProviderIdList);
			loNotificationDataBean.setLinkMap(loLinkMap);
			loNotificationMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
			loNotificationMap.put(HHSConstants.NT_404, loNotificationDataBean);
			loRequestMap.put(HHSConstants.CONTRACT_TITLE, loContractDetailMap.get(HHSConstants.CONTRACT_TITLE));
			loRequestMap.put(HHSConstants.AGENCY_ID_COL, loContractDetailMap.get(HHSConstants.AGENCY_ID_COL));
			loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
			loNotificationMap.put(ApplicationConstants.ENTITY_ID, asContractId);
			loNotificationMap.put(ApplicationConstants.ENTITY_TYPE, HHSConstants.CONTRACT);
			loNotificationMap.put(HHSConstants.CREATED_BY_USER_ID, asUserId);
			loNotificationMap.put(HHSConstants.MODIFIED_BY, asUserId);
		}
		catch (ApplicationException aoExp)
		{
			aoExp.addContextData("ApplicationException occured while setting notification map", aoExp);
			throw aoExp;
		}
		catch (Exception aoExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while setting notification map ", aoExp);
			loAppEx.addContextData("ApplicationException occured while setting notification map", aoExp);
			throw loAppEx;
		}
		return loNotificationMap;
	}

	/**
	 * This method close the Contract
	 * <ul>
	 * <li>1.Get the required info i.e contract Id,USerName,Password and pass
	 * the value to channel</li>
	 * <li>2.Call the closeContract Transaction</li>
	 * <ul>
	 * 
	 * @param aoResourceRequest Resource Request Object
	 * @param aoResourceResponse Resource Response Object
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	@ResourceMapping("closeContract")
	public void closeContract(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse) throws Exception
	{
		PortletSession loSession = aoResourceRequest.getPortletSession();
		String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		Channel loChannel = new Channel();
		Boolean lbAuthStatusFlag;
		HashMap loErrorCheckRule = null;
		Map loHashMap = null;
		String lsErrorMsg = HHSConstants.EMPTY_STRING;
		int liErrorCode = HHSConstants.INT_ZERO;
		String lsContractId = aoResourceRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW);
		String lsUserName = aoResourceRequest.getParameter(HHSConstants.KEY_SESSION_USER_NAME);
		String lsPassword = aoResourceRequest.getParameter(HHSConstants.PASSWORD);
		String lsCloseComment = aoResourceRequest.getParameter(HHSConstants.CLOSE_CONTRACT_COMMENT);
		try
		{
			loChannel.setData(HHSConstants.CONTRACT_ID_KEY, lsContractId);
			loHashMap = validateUser(lsUserName, lsPassword, aoResourceRequest);
			lbAuthStatusFlag = (Boolean) loHashMap.get(HHSConstants.IS_VALID_USER);
			loChannel.setData(HHSConstants.LB_AUTH_STATUS_FLAG, lbAuthStatusFlag);
			loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
			HHSUtil.addAuditDataToChannel(loChannel, HHSConstants.CONTRACT_CLOSE, HHSConstants.CONTRACT_CLOSE,
					lsCloseComment, HHSConstants.CONTRACTS, lsContractId, lsUserId, HHSConstants.AGENCY_AUDIT,
					HHSConstants.AUDIT_BEAN);
			List<HhsAuditBean> loAuditList = new ArrayList<HhsAuditBean>();
			HHSUtil.auditConfigCommnetsOnBudgets(loAuditList, lsContractId, HHSConstants.TWO,
					HHSConstants.BUDGET_TYPE3, lsUserId, null, true);
			loChannel.setData(HHSConstants.LO_AUDIT_LIST, loAuditList);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.CLC_CLOSE_CONTRACT);
			loErrorCheckRule = (HashMap) loChannel.getData(HHSConstants.LO_ERROR_CHECK_RULE);
			liErrorCode = (Integer) loErrorCheckRule.get(HHSConstants.ERROR_CODE);
			lsErrorMsg = (String) loErrorCheckRule.get(HHSConstants.CLC_ERROR_MSG);
			if (!lbAuthStatusFlag)
			{
				liErrorCode = HHSConstants.INT_ONE;
				lsErrorMsg = (String) loHashMap.get(HHSConstants.ERROR_MESSAGE);
			}
			if (lbAuthStatusFlag && liErrorCode != HHSConstants.INT_TWO)
			{
				liErrorCode = HHSConstants.INT_ZERO;
				lsErrorMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.CONTRACT_CLOSED);
				aoResourceRequest.getPortletSession().setAttribute(HHSConstants.SUCCESS_MESSAGE, lsErrorMsg,
						PortletSession.APPLICATION_SCOPE);
			}
		}
		catch (ApplicationException aoExe)
		{
			setErrorMsgJSPForContractList(aoResourceResponse, aoExe.getMessage(), 2);
			LOG_OBJECT.Error("ApplicationException occured in updateContractConfiguration", aoExe);
		}
		catch (Exception aoExe)
		{
			setErrorMsgJSPForContractList(aoResourceResponse, aoExe.getMessage(), 2);
			LOG_OBJECT.Error("Exception occured in updateContractConfiguration", aoExe);
		}
		setErrorMsgJSPForContractList(aoResourceResponse, lsErrorMsg, liErrorCode);

	}

	/**
	 * This method will get the Epin list from the cache when user type three
	 * characters using getEpinList method defined in basecontroller.
	 * 
	 * @param aoRequest a ResourceRequest Object
	 * @param aoResponse a ResourceResponse Object
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             ApplicationException
	 */
	@ResourceMapping("getEpinListResourceUrl")
	public void getEpinListResourceRequest(ResourceRequest aoRequest, ResourceResponse aoResponse)
	{
		try
		{
			getEpinList(aoRequest, aoResponse);
		}
		catch (ApplicationException aoExe)
		{
			LOG_OBJECT.Error("ApplicationException occured in getEpinListResourceRequest method while fetching Epin ",
					aoExe);
		}
		catch (Exception aoExe)
		{
			LOG_OBJECT.Error("Error occured in getEpinListResourceRequest method while fetching Epin  ", aoExe);
		}
	}

	/**
	 * Added as part of enhancement 6482 for Release 3.8.0 called on click of
	 * Update Contract Information dropdown under Action section
	 * <ul>
	 * <li>Fetch award e-pin from request</li>
	 * <li>Populate input parameter map to pass it to the transaction layer.</li>
	 * <li>Set the required parameters in the channel object to pass them to the
	 * transaction framework.</li>
	 * <li>Hit the transaction with id <b> viewAptDetails</b> to fetch the
	 * required details</li>
	 * <li>Transaction layer executes the transaction and puts the result back
	 * in the channel object.</li>
	 * <li>Fetch the result of the transaction from the channel object</li>
	 * <li>Display the result on the jsp page</li>
	 * 
	 * </ul>
	 * @param aoResourceRequest ResourceRequest
	 * @param aoResourceResponse ResourceResponse
	 * @return ModelAndView
	 */
	@ResourceMapping("updateContractInforOverlay")
	protected ModelAndView populateUpdateContractPage(ResourceRequest aoResourceRequest,
			ResourceResponse aoResourceResponse) throws Exception
	{
		LOG_OBJECT.Debug("Entered populateUpdateContractPage");
		String lsContracId = (String) aoResourceRequest.getParameter(HHSConstants.CONTRACT_ID1);
		EPinDetailBean updateContractBean = new EPinDetailBean();
		updateContractBean.setContractId(lsContracId);
		String lsError = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
		try
		{
			Channel loChannel = new Channel();
			loChannel.setData(HHSConstants.LO_CONTRACT_DETAILS, updateContractBean);
			if (updateContractBean.getContractId() != null
					&& !updateContractBean.getContractId().equalsIgnoreCase(HHSConstants.EMPTY_STRING))
			{
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_CONTRACT_DETAILS_FOR_UPDATE);
			}
			updateContractBean = (EPinDetailBean) loChannel.getData(HHSConstants.AO_CONTRACT_DETAIL);
			if (updateContractBean != null)
			{
				updateContractBean.setContractId(lsContracId);
				aoResourceRequest.setAttribute(HHSConstants.EPIN_BEAN_DETAILS, updateContractBean);
				lsError = HHSConstants.EMPTY_STRING;
			}
			else
			{
				lsError = HHSConstants.EPIN_ERROR;
			}
		}
		catch (ApplicationException aoExe)
		{
			LOG_OBJECT.Error("ApplicationException occured in populateUpdateContractPage", aoExe);

		}
		catch (Exception aoExe)
		{
			LOG_OBJECT.Error("Exception occured in populateUpdateContractPage", aoExe);
		}
		aoResourceRequest.setAttribute(HHSConstants.CLC_CAP_ERROR, lsError);
		return new ModelAndView(HHSConstants.UPDATE_CONTRACT_JSP);

	}

	/**
	 * This method is added as a part of user access
	 * <ul>
	 * <li>Fetch contract Id from request</li>
	 * <li>Populate input parameter map to pass it to the transaction layer.</li>
	 * <li>Set the required parameters in the channel object to pass them to the
	 * transaction framework.</li>
	 * <li>Hit the transaction with id <b> getUserAccessDetails</b> to fetch the
	 * required details</li>
	 * <li>Transaction layer executes the transaction and puts the result back
	 * in the channel object.</li>
	 * <li>Fetch the result of the transaction from the channel object</li>
	 * <li>Display the result on the jsp page</li>
	 * 
	 * </ul>
	 * @param aoResourceRequest ResourceRequest
	 * @param aoResourceResponse ResourceResponse
	 * @return ModelAndView
	 */
	@SuppressWarnings("unchecked")
	@ResourceMapping("getContractSharedListOverlay")
	protected ModelAndView populateContractSharedOverlay(ResourceRequest aoResourceRequest,
			ResourceResponse aoResourceResponse) throws Exception
	{
		LOG_OBJECT.Debug("Entered populateUpdateContractPage");
		String lsContractId = (String) aoResourceRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW);
		ContractBean updateContractBean = new ContractBean();
		updateContractBean.setContractId(lsContractId);
		String lsError = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
		String lsUserId = (String) aoResourceRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
		try
		{
			Channel loChannel = new Channel();
			loChannel.setData(HHSR5Constants.CONTRACT_ID_KEY, lsContractId);
			loChannel.setData(HHSR5Constants.USER_ID, lsUserId);
			if (updateContractBean.getContractId() != null
					&& !updateContractBean.getContractId().equalsIgnoreCase(HHSConstants.EMPTY_STRING))
			{
				HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.GET_USER_ACCESS_DETAILS,
						HHSR5Constants.TRANSACTION_ELEMENT_R5);
			}
			List<ContractBean> loDataList = (List<ContractBean>) loChannel.getData(HHSR5Constants.GET_USER_ACCESS_LIST);
			if (updateContractBean != null)
			{
				aoResourceRequest.setAttribute(HHSR5Constants.GET_USER_ACCESS_LIST, loDataList);
				lsError = HHSConstants.EMPTY_STRING;
			}

		}
		catch (ApplicationException aoExe)
		{
			LOG_OBJECT.Error("ApplicationException occured in populateContractSharedOverlay", aoExe);

		}
		catch (Exception aoExe)
		{
			LOG_OBJECT.Error("Exception occured in populateContractSharedOverlay", aoExe);
		}
		aoResourceRequest.setAttribute(HHSR5Constants.CONTRACT_ID_WORKFLOW, lsContractId);
		aoResourceRequest.setAttribute(HHSConstants.CLC_CAP_ERROR, lsError);
		return new ModelAndView(HHSR5Constants.USER_ASSIGN_JSP);
	}

	/**
	 * * This will update User Access Url
	 * <ul>
	 * <li>1.Get the required info and pass the value to channel and audit bean</li>
	 * <li>2.Call the Transaction <b>updateUserAccessInformation<b></li>
	 * <ul>
	 * 
	 * @param aoRequest Request Object
	 * @param aoResponse Response Object
	 * @throws Exception exception in case any error occurs
	 */
	@ResourceMapping("userAccessUrl")
	protected void updateUserAccessUrl(ResourceRequest aoRequest, ResourceResponse aoResponse) throws Exception
	{

		String[] loUserWithouAccess = aoRequest.getParameter(HHSR5Constants.SHARED_LIST_2).split(HHSConstants.COMMA);
		List<String> loUserListWithoutAccess = new ArrayList<String>();
		for (int liIndex = 0; liIndex < loUserWithouAccess.length; liIndex++)
		{
			if (loUserWithouAccess[liIndex] != null && !loUserWithouAccess[liIndex].isEmpty())
				loUserListWithoutAccess.add(loUserWithouAccess[liIndex]);
		}
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		LOG_OBJECT.Debug("Entered populateUpdateContractPage");
		String lsContractId = (String) aoRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW);
		PrintWriter loOut = null;
		String lsErrorMsg = null;
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();

		try
		{
			aoResponse.setContentType(HHSConstants.TEXT_HTML);
			loOut = aoResponse.getWriter();
			Channel loChannel = new Channel();
			loChannel.setData(HHSR5Constants.CONTRACT_ID_KEY, lsContractId);
			loChannel.setData(HHSR5Constants.AO_USER_LIST_WITHOUT_ACCESS, loUserListWithoutAccess);
			loChannel.setData(ApplicationConstants.KEY_SESSION_USER_ID, lsUserId);
			loHhsAuditBean.setEventName(HHSR5Constants.USER_RESTRICTION);
			loHhsAuditBean.setEventType(HHSR5Constants.CONTRACT_RESTRICTED);
			loHhsAuditBean.setUserId(lsUserId);
			loHhsAuditBean.setEntityId(lsContractId);
			loHhsAuditBean.setEntityType(HHSR5Constants.CONTRACT);
			loHhsAuditBean.setData(HHSR5Constants.CONTRACT_HAS_BEEN_RESTRICTED);
			loHhsAuditBean.setAuditTableIdentifier(HHSConstants.PROVIDER_AUDIT);
			loChannel.setData(HHSConstants.AUDIT_BEAN, loHhsAuditBean);
			if (StringUtils.isNotBlank(lsContractId))
			{
				HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.UPDATE_USER_ACCESS_INFORMATION,
						HHSR5Constants.TRANSACTION_ELEMENT_R5);
			}
			Boolean loDataList = (Boolean) loChannel.getData(HHSR5Constants.IS_UPDATE_SUCCESS);
			if (loDataList)
			{
				aoRequest.getPortletSession().setAttribute(
						HHSConstants.SUCCESS_MESSAGE,
						PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
								HHSR5Constants.RESTRICTED_CONTRACT), PortletSession.APPLICATION_SCOPE);
			}
			else
			{
				lsErrorMsg = "You must select at least one L2 user to have access to this contract";
			}
		}
		catch (ApplicationException aoExe)
		{
			LOG_OBJECT.Error("ApplicationException occured in updateUserAccessUrl", aoExe);
			lsErrorMsg = "The contract user access settings cannot be saved.";
		}
		catch (Exception aoExe)
		{
			LOG_OBJECT.Error("Exception occured in updateUserAccessUrl", aoExe);
			lsErrorMsg = "The contract user access settings cannot be saved.";
		}
		finally
		{
			catchTaskError(loOut, lsErrorMsg);
		}
	}

	/**
	 * * This will unSuspend the contract
	 * <ul>
	 * <li>1.Get the required info i.e ContractId, UserName, Password and pass
	 * the value to channel</li>
	 * <li>2.Call the unsuspendedContract Transaction</li>
	 * <ul>
	 * 
	 * @param aoAuthenticationBean Authentication Bean as input
	 * @param aoResult binding result set
	 * @param aoResourceRequest Resource Request Object
	 * @param aoResourceResponse Resource Response Object
	 * @throws Exception exception in case any error occurs
	 */
	@ResourceMapping("unSuspendContractUrl")
	public void actionUnSuspendContract(
			@Valid @ModelAttribute("unSuspendContract") AuthenticationBean aoAuthenticationBean,
			BindingResult aoResult, ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws Exception
	{
		Channel loChannel = new Channel();
		PrintWriter loOut = null;
		String lsErrorMsg = HHSConstants.EMPTY_STRING;
		aoResourceResponse.setContentType(HHSConstants.TEXT_HTML);
		try
		{
			loOut = aoResourceResponse.getWriter();
			validator.validate(aoAuthenticationBean, aoResult);
			if (!aoResult.hasErrors())
			{
				Map loValidateMap = validateUser(aoAuthenticationBean.getUserName(),
						aoAuthenticationBean.getPassword(), aoResourceRequest);
				Boolean lbUserValidation = (Boolean) loValidateMap.get(HHSConstants.IS_VALID_USER);
				if (lbUserValidation == null || !lbUserValidation)
				{
					lsErrorMsg = HHSConstants.INVALID_USER_MSG;
				}
				else
				{
					String lsUserId = (String) aoResourceRequest.getPortletSession().getAttribute(
							ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
					String lsOrgnizationType = (String) aoResourceRequest.getPortletSession().getAttribute(
							ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
					P8UserSession loUserSession = (P8UserSession) aoResourceRequest.getPortletSession().getAttribute(
							ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);

					if (lsOrgnizationType != null
							&& lsOrgnizationType.equalsIgnoreCase(ApplicationConstants.AGENCY_ORG))
					{
						HHSUtil.addAuditDataToChannel(loChannel, HHSConstants.CONTRACT_UNSUSPENSION,
								HHSConstants.CONTRACT_UNSUSPENSION, aoAuthenticationBean.getReason(),
								HHSConstants.CONTRACTS, aoAuthenticationBean.getContractId(), lsUserId,
								HHSConstants.AGENCY_AUDIT, HHSConstants.AUDIT_BEAN);
					}
					else
					{
						HHSUtil.addAuditDataToChannel(loChannel, HHSConstants.CONTRACT_UNSUSPENSION,
								HHSConstants.CONTRACT_UNSUSPENSION, aoAuthenticationBean.getReason(),
								HHSConstants.CONTRACTS, aoAuthenticationBean.getContractId(), lsUserId,
								HHSConstants.ACCELERATOR_AUDIT, HHSConstants.AUDIT_BEAN);
					}
					loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
					loChannel.setData(HHSConstants.AUTHENTICATION_BEAN_UNSUSPEND, aoAuthenticationBean);
					loChannel.setData(HHSConstants.CONTRACT_ID_KEY, aoAuthenticationBean.getContractId());
					loChannel.setData(HHSConstants.AS_CONTRACT_REASON, aoAuthenticationBean.getReason());
					loChannel.setData(HHSConstants.LS_USER_ID, lsUserId);
					loChannel.setData(HHSConstants.LS_USER_ORG_TYPE, lsOrgnizationType);
					HHSTransactionManager.executeTransaction(loChannel, HHSConstants.CLC_UNSUSPEND_CONTRACT);
					Boolean loUnSuspendStatusWorkflow = (Boolean) loChannel
							.getData(HHSConstants.UNSUSPEND_STATUS_WORKFLOW);
					if (loUnSuspendStatusWorkflow == null || !loUnSuspendStatusWorkflow)
					{
						lsErrorMsg = HHSConstants.UNSUSPEND_WORKFLOW_NOT_SUCCESS;
					}
					else
					{
						aoResourceRequest.getPortletSession().setAttribute(
								HHSConstants.SUCCESS_MESSAGE,
								PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
										HHSConstants.CAPS_UNSUSPEND_CONTRACT), PortletSession.APPLICATION_SCOPE);
					}
				}
			}
			else
			{
				lsErrorMsg = HHSConstants.UNSUSPEND_FAILURE;
			}
		}
		catch (ApplicationException aoExe)
		{
			lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("ApplicationException occured in unsuspend Contract", aoExe);
		}
		finally
		{
			catchTaskError(loOut, lsErrorMsg);
		}
	}

	/**
	 * This will suspend the contract
	 * <ul>
	 * <li>1.Get the required info i.e ContractId, UserName, Password and pass
	 * the value to channel</li>
	 * <li>2.Call the suspendContract Transaction</li>
	 * <ul>
	 * 
	 * @param aoAuthenticationBean Authentication Bean as input
	 * @param aoResult binding result set
	 * @param aoResourceRequest Resource Request Object
	 * @param aoResourceResponse Resource Response Object
	 * @throws Exception exception in case any error occurs
	 */
	@ResourceMapping("suspendContractUrl")
	public void actionSuspendContract(
			@Valid @ModelAttribute("suspendContract") AuthenticationBean aoAuthenticationBean, BindingResult aoResult,
			ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse) throws Exception
	{
		Channel loChannel = new Channel();
		PrintWriter loOut = null;
		String lsErrorMsg = HHSConstants.EMPTY_STRING;
		aoResourceResponse.setContentType(HHSConstants.TEXT_HTML);
		try
		{
			loOut = aoResourceResponse.getWriter();
			validator.validate(aoAuthenticationBean, aoResult);
			if (!aoResult.hasErrors())
			{
				Map loValidateMap = validateUser(aoAuthenticationBean.getUserName(),
						aoAuthenticationBean.getPassword(), aoResourceRequest);
				Boolean lbUserValidation = (Boolean) loValidateMap.get(HHSConstants.IS_VALID_USER);
				if (lbUserValidation == null || !lbUserValidation)
				{
					lsErrorMsg = HHSConstants.INVALID_USER_MSG;
				}
				else
				{
					String lsUserId = (String) aoResourceRequest.getPortletSession().getAttribute(
							ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
					String lsOrgnizationType = (String) aoResourceRequest.getPortletSession().getAttribute(
							ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
					P8UserSession loUserSession = (P8UserSession) aoResourceRequest.getPortletSession().getAttribute(
							ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);

					if (lsOrgnizationType != null
							&& lsOrgnizationType.equalsIgnoreCase(ApplicationConstants.AGENCY_ORG))
					{
						HHSUtil.addAuditDataToChannel(loChannel, HHSConstants.CONTRACT_SUSPENSION,
								HHSConstants.CONTRACT_SUSPENSION, aoAuthenticationBean.getReason(),
								HHSConstants.CONTRACTS, aoAuthenticationBean.getContractId(), lsUserId,
								HHSConstants.AGENCY_AUDIT, HHSConstants.AUDIT_BEAN);
					}
					else
					{
						HHSUtil.addAuditDataToChannel(loChannel, HHSConstants.CONTRACT_SUSPENSION,
								HHSConstants.CONTRACT_SUSPENSION, aoAuthenticationBean.getReason(),
								HHSConstants.CONTRACTS, aoAuthenticationBean.getContractId(), lsUserId,
								HHSConstants.ACCELERATOR_AUDIT, HHSConstants.AUDIT_BEAN);
					}
					loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
					loChannel.setData(HHSConstants.CLC_AUTH_BEAN_SUSPEND, aoAuthenticationBean);
					loChannel.setData(HHSConstants.CONTRACT_ID_KEY, aoAuthenticationBean.getContractId());
					loChannel.setData(HHSConstants.AS_CONTRACT_REASON, aoAuthenticationBean.getReason());
					loChannel.setData(HHSConstants.LS_USER_ID, lsUserId);
					loChannel.setData(HHSConstants.LS_USER_ORG_TYPE, lsOrgnizationType);
					HHSTransactionManager.executeTransaction(loChannel, HHSConstants.CLC_SUSPEND_CONTRACT);
					Boolean loSuspendStatusWorkflow = (Boolean) loChannel
							.getData(HHSConstants.LO_SUSPEND_STATUS_WORKFLOW);
					if (loSuspendStatusWorkflow == null || !loSuspendStatusWorkflow)
					{
						lsErrorMsg = HHSConstants.SUSPEND_WORKFLOW_NOT_SUCCESS;
					}
					else
					{
						aoResourceRequest.getPortletSession().setAttribute(
								HHSConstants.SUCCESS_MESSAGE,
								PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
										HHSConstants.CAPS_SUSPEND_CONTRACT), PortletSession.APPLICATION_SCOPE);
					}
				}
			}
			else
			{
				lsErrorMsg = HHSConstants.SUSPEND_NOT_SUCCESS;
			}
		}
		catch (ApplicationException aoExe)
		{
			lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("ApplicationException occured in suspend Contract", aoExe);
		}
		finally
		{
			catchTaskError(loOut, lsErrorMsg);
		}
	}

	/**
	 * This method is updated for Release 3.12.0 #6602 This method would be
	 * triggered on the click of 'Start New FY Configuration' button on Confirm
	 * New FY Configuration screen.
	 * 
	 * call the transaction 'newFYConfigErrorCheck'
	 * 
	 * @param aoResourceRequest Resource Request Object
	 * @param aoResourceResponse Resource Response Object
	 * @throws Exception
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@ResourceMapping("fyConfigConfirm")
	public void actionNewConfiguration(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws Exception
	{
		String lsErrorMsg = HHSConstants.EMPTY_STRING;
		int liErrorCode = HHSConstants.INT_ZERO;
		HashMap loErrorCheckRule = null;
		HashMap loHmRequiredProps = new HashMap();
		HashMap<String, Object> loErrorMap = null;
		String lsContractId = aoResourceRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW);
		String lsContractTypeId = aoResourceRequest.getParameter(HHSConstants.CONTRACT_TYPE_ID);
		String lsFYConfigFiscalYear = aoResourceRequest.getParameter(HHSConstants.CONFIG_FISCAL_YEAR);
		Channel loChannel = new Channel();
		PortletSession loSession = aoResourceRequest.getPortletSession();
		String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		loChannel.setData(HHSConstants.LB_AUTH_STATUS_FLAG, true);
		loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
		loChannel.setData(HHSConstants.CONTRACT_ID_KEY, lsContractId);
		loChannel.setData(HHSConstants.CONTRACT_TYPE_ID_KEY, lsContractTypeId);
		// User id set in channel variable for Release 3.12.0 enhancement 6602
		loChannel.setData(HHSConstants.AS_USER_ID, lsUserId);
		loHmRequiredProps.put(HHSConstants.CONTRACT_ID_WORKFLOW, lsContractId);
		loHmRequiredProps.put(HHSConstants.SUBMITTED_BY, lsUserId);
		loHmRequiredProps.put(HHSConstants.WORKFLOW_NAME, HHSConstants.WF_NEW_FY_CONFIGURATION);
		loHmRequiredProps.put(HHSConstants.CLC_FISCAL_YEAR_ID, lsFYConfigFiscalYear);
		loChannel.setData(HHSConstants.AO_HMWF_REQUIRED_PROPS, loHmRequiredProps);
		try
		{
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.NEW_FY_CONFIG_ERROR_CHECK);
			loErrorCheckRule = (HashMap) loChannel.getData(HHSConstants.LO_CONFIG_ERROR_CHECK);
			loErrorMap = setErrorMsgNewFYConfig(aoResourceResponse, aoResourceRequest, loChannel, loErrorCheckRule);
			lsErrorMsg = (String) loErrorMap.get(HHSConstants.CLC_ERROR_MESSAGE);
			liErrorCode = (Integer) loErrorMap.get(HHSConstants.CLC_ERROR_CODE);
		}
		catch (ApplicationException aoExe)
		{
			lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			liErrorCode = HHSConstants.INT_TWO;
			LOG_OBJECT.Error("ApplicationException occured in newFYConfigErrorCheck", aoExe);
		}
		finally
		{
			setErrorMsgJSPForContractList(aoResourceResponse, lsErrorMsg, liErrorCode);
		}

	}

	/**
	 * This method update the contract configuration for a contract
	 * 
	 * <ul>
	 * <li>If Authentication is failed set error code 1 and Authentication
	 * failure error message .</li>
	 * <li>If Authentication is true call 'updateContractConfiguration'
	 * transaction and check for error check rule.</li>
	 * <li>If error check rule gives success set error code 0 and success error
	 * message.</li>
	 * <li>Else set error code 2 and set error message returned from
	 * transaction.</li>
	 * </ul>
	 * 
	 * call the transaction 'confirmNewFYConfig'
	 * 
	 * @param aoResourceResponse Resource Response object
	 * @param aoChannel Channel object
	 * @param aoErrorCheckRule Hashmap object contains error code
	 * @param abAuthStatusFlag Authentication Flag
	 * @throws ApplicationException
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	private HashMap<String, Object> setErrorMsgNewFYConfig(ResourceResponse aoResourceResponse,
			ResourceRequest aoResourceRequest, Channel aoChannel, HashMap aoErrorCheckRule)
			throws ApplicationException, IOException
	{
		HashMap<String, Object> loErrorMap = new HashMap<String, Object>();
		String lsErrorMsg = null;
		int liErrorCode = HHSConstants.INT_ZERO;
		if (null != aoErrorCheckRule)
		{
			if (((String) aoErrorCheckRule.get(HHSConstants.CLC_ERROR_CHECK)).equalsIgnoreCase(HHSConstants.SUCCESS))
			{
				try
				{
					HHSTransactionManager.executeTransaction(aoChannel, HHSConstants.CONFIRM_NEW_FY_CONFIG);
					Boolean lbSuccessStatus = (Boolean) aoChannel.getData(HHSConstants.LB_AUTH_STATUS_FLAG);
					if (lbSuccessStatus)
					{
						aoResourceRequest.getPortletSession().setAttribute(
								HHSConstants.SUCCESS_MESSAGE,
								PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
										HHSConstants.NEW_FY_TASK_CREATED), PortletSession.APPLICATION_SCOPE);
						liErrorCode = HHSConstants.INT_ZERO;
					}
				}

				catch (ApplicationException aoExe)
				{
					lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
					liErrorCode = HHSConstants.INT_TWO;
					LOG_OBJECT.Error("ApplicationException occured in confirmNewFYConfig", aoExe);
				}

			}
			else
			{
				lsErrorMsg = (String) aoErrorCheckRule.get(HHSConstants.CLC_ERROR_MSG);
				liErrorCode = HHSConstants.INT_TWO;
			}
		}
		else
		{
			lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			liErrorCode = HHSConstants.INT_TWO;
		}
		loErrorMap.put(HHSConstants.CLC_ERROR_MESSAGE, lsErrorMsg);
		loErrorMap.put(HHSConstants.CLC_ERROR_CODE, liErrorCode);

		return loErrorMap;
	}

	/**
	 * This method is triggered from Contract list screen in the Actions drop
	 * down. /** This method is triggered from Contract list screen in the
	 * Actions drop down.
	 * <ul>
	 * <li>Validation are done for username and password fetched from request
	 * parameters to check if they are non empty fields.</li>
	 * <li>A Channel object <code>loChannelObj</code> is created.</li>
	 * <li>username and password are set in loChannelObj which serve as input
	 * parameters to confirmBudgetModificationCancellation transaction for the
	 * service method:authenticateLoginUser in SecurityService Class.</li>
	 * <li>We call TransactionManagerR2 to execute
	 * <code>updateContractConfigurationConfirmationContractList</code>
	 * transaction which is used to authenticate Login User and save changes in
	 * Database</li>
	 * <li>A boolean value is returned which determines whether the database was
	 * updated successfully or not.</li>
	 * </ul>
	 * 
	 * calls the transaction 'updateContractConfigurationErrorCheck'
	 * 
	 * @param aoResourceRequest ResourceRequest object
	 * @param aoResourceResponse ResourceResponse object
	 * 
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@ResourceMapping("updateContractConfiguration")
	public void updateContractConfiguration(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		PortletSession loSession = aoResourceRequest.getPortletSession();
		String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		Channel loChannel = new Channel();
		HashMap loHmRequiredProps = new HashMap();
		HashMap loErrorCheckRule = null;
		Map loHashMap = null;
		AuthenticationBean loAuthenticationBean = new AuthenticationBean();
		Boolean lbAuthStatusFlag;
		String lsUserName = (String) aoResourceRequest.getParameter(HHSConstants.KEY_SESSION_USER_NAME);
		String lsPassword = (String) aoResourceRequest.getParameter(HHSConstants.PASSWORD);
		String lsContractId = (String) aoResourceRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW);
		loAuthenticationBean.setUserName(lsUserName);
		loAuthenticationBean.setPassword(lsPassword);
		try
		{
			loChannel.setData(HHSConstants.AO_AUTH_BEAN, loAuthenticationBean);
			loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
			loChannel.setData(HHSConstants.CONTRACT_ID_KEY, lsContractId);
			loHmRequiredProps.put(HHSConstants.CONTRACT_ID_WORKFLOW, lsContractId);
			loHmRequiredProps.put(HHSConstants.SUBMITTED_BY, lsUserId);
			loHmRequiredProps.put(HHSConstants.WORKFLOW_NAME, HHSConstants.WF_CONTRACT_CONFIGURATION_UPDATE);
			loChannel.setData(HHSConstants.AO_HMWF_REQUIRED_PROPS, loHmRequiredProps);
			loChannel.setData(HHSConstants.INVOICE_LB_STATUS, true);
			loHashMap = validateUser(lsUserName, lsPassword, aoResourceRequest);
			lbAuthStatusFlag = (Boolean) loHashMap.get(HHSConstants.IS_VALID_USER);
			loChannel.setData(HHSConstants.LB_AUTH_STATUS_FLAG, lbAuthStatusFlag);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.UPDATE_CONTRACT_CONF_ERROR);
			lbAuthStatusFlag = (Boolean) loChannel.getData(HHSConstants.LB_AUTH_STATUS_FLAG);
			loErrorCheckRule = (HashMap) loChannel.getData(HHSConstants.LO_ERROR_CHECK_RULE);
			setErrorMsgUpdateConfig(aoResourceRequest, aoResourceResponse, loChannel, loErrorCheckRule,
					lbAuthStatusFlag);

		}
		catch (ApplicationException aoExe)
		{
			setErrorMsgJSPForContractList(aoResourceResponse, aoExe.getMessage(), 2);
			LOG_OBJECT.Error("ApplicationException occured in updateContractConfiguration", aoExe);
		}
	}

	/**
	 * This method update the contract configuration for a contract
	 * 
	 * <ul>
	 * <li>If Authentication is failed set error code 1 and Authentication
	 * failure error message .</li>
	 * <li>If Authentication is true call 'updateContractConfiguration'
	 * transaction and check for error check rule.</li>
	 * <li>If error check rule gives success set error code 0 and success error
	 * message.</li>
	 * <li>Else set error code 2 and set error message returned from
	 * transaction.</li>
	 * </ul>
	 * 
	 * @param aoResourceResponse Resource Response object
	 * @param aoResourceRequest ResourceRequest object
	 * @param aoChannel Channel object
	 * @param aoErrorCheckRule Hashmap object contains error code
	 * @param abAuthStatusFlag Authentication Flag
	 * @throws ApplicationException
	 * 
	 */
	@SuppressWarnings("rawtypes")
	private void setErrorMsgUpdateConfig(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse,
			Channel aoChannel, HashMap aoErrorCheckRule, Boolean abAuthStatusFlag) throws ApplicationException
	{
		String lsErrorMsg = HHSConstants.EMPTY_STRING;
		int liErrorCode = HHSConstants.INT_ZERO;
		if (abAuthStatusFlag)
		{
			if (null != aoErrorCheckRule)
			{
				if (((String) aoErrorCheckRule.get(HHSConstants.CLC_ERROR_CHECK))
						.equalsIgnoreCase(HHSConstants.SUCCESS))
				{
					HHSTransactionManager.executeTransaction(aoChannel, HHSConstants.UPDATE_CONTRACT_CONF);
					lsErrorMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSConstants.CONFIGURATION_UPDATE);
					aoResourceRequest.getPortletSession().setAttribute(
							HHSConstants.TRANSACTION_SUCCESS,
							PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
									HHSConstants.CONFIGURATION_UPDATE), PortletSession.APPLICATION_SCOPE);
				}
				else
				{
					lsErrorMsg = (String) aoErrorCheckRule.get(HHSConstants.CLC_ERROR_MSG);
					liErrorCode = HHSConstants.INT_TWO;
				}
			}
			else
			{
				lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
				liErrorCode = HHSConstants.INT_TWO;
			}
		}
		else
		{
			liErrorCode = HHSConstants.INT_ONE;
			lsErrorMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
					HHSConstants.CB_VALIDATE_ERROR);
		}

		// Set the error/success message in session
		if (liErrorCode == HHSConstants.INT_ZERO)
		{
			aoResourceRequest.getPortletSession().setAttribute(HHSConstants.SUCCESS_MESSAGE, lsErrorMsg,
					PortletSession.APPLICATION_SCOPE);
		}
		else if (liErrorCode == HHSConstants.INT_TWO)
		{
			aoResourceRequest.getPortletSession().setAttribute(HHSConstants.ERROR_MESSAGE, lsErrorMsg,
					PortletSession.APPLICATION_SCOPE);
		}

		setErrorMsgJSPForContractList(aoResourceResponse, lsErrorMsg, liErrorCode);
	}

	/**
	 * This method write error message on jsp in JSON format
	 * 
	 * @param aoResResponseForContract Resource Response object
	 * @param lsErrorMsgForContract Error message
	 * @param liErrorCodeForContract Error code
	 */
	private void setErrorMsgJSPForContractList(ResourceResponse aoResResponseForContract, String lsErrorMsgForContract,
			int liErrorCodeForContract)
	{
		PrintWriter loOut = null;
		try
		{
			aoResResponseForContract.setContentType(HHSConstants.APPLICATION_JSON);
			loOut = aoResResponseForContract.getWriter();
			lsErrorMsgForContract = HHSConstants.ERROR_1 + liErrorCodeForContract + HHSConstants.MESSAGE_1
					+ lsErrorMsgForContract + HHSConstants.CLOSING_BRACE_1;
			loOut.print(lsErrorMsgForContract);
		}
		catch (IOException loIOE)
		{
			LOG_OBJECT.Error("Exception occured in setErrorMsgJSP", loIOE);
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
	 * This method is used to display the Contract type Overlay
	 * <ul>
	 * <li>1.Get the required view name from request</li>
	 * <li>Method Updated in R4</li>
	 * <ul>
	 * R6: Updated method to also fetch contract agencyID from drop down action
	 * on financial list screen
	 * @param aoResourceRequest Resource Request Object
	 * @return model and view of the required jsp page
	 */
	@ResourceMapping("getContractTypeOverlayPage")
	public ModelAndView getContractTypeOverlay(ResourceRequest aoResourceRequest)
	{
		ModelAndView loModelAndView = null;
		String lsProviderOrgId = (String) aoResourceRequest.getParameter(HHSConstants.PROVIDER_ORG_ID_AMEND);
		String lsJspName = (String) aoResourceRequest.getParameter(HHSConstants.JSP_NAME);
		String lsContracId = (String) aoResourceRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW);
		String lsContractTypeId = (String) aoResourceRequest.getParameter(HHSConstants.CONTRACT_TYPE_ID);
		String lsFYConfigFiscalYear = (String) aoResourceRequest.getParameter(HHSConstants.CONFIG_FISCAL_YEAR);
		String lsAmendContracId = (String) aoResourceRequest.getParameter(HHSConstants.AMEND_CONTRACT_ID_WORKFLOW);
		String lsContractTitle = (String) aoResourceRequest.getParameter(HHSConstants.BUDGET_CONTRACT_TITLE);
		/*
		 * R6: Getting contract agencyId for the action item and setting it into
		 * bean start
		 */
		String lsAgencyName = (String) aoResourceRequest.getParameter(HHSConstants.CONTRACT_AGENCY_ID);
		EPinDetailBean loEPinDetailBean = new EPinDetailBean();
		loEPinDetailBean.setContractId(lsContracId);
		loEPinDetailBean.setAgencyName(lsAgencyName);
		/* R6: Getting contract agencyId for the action item end */
		aoResourceRequest.setAttribute(HHSConstants.CLC_CONTRACT_ID, lsContracId);
		aoResourceRequest.setAttribute(HHSConstants.EPIN_BEAN_DETAILS, loEPinDetailBean);
		aoResourceRequest.setAttribute(HHSConstants.CLC_CONTRACT_TYPE_ID, lsContractTypeId);
		aoResourceRequest.setAttribute(HHSConstants.CONFIG_FISCAL_YEAR, lsFYConfigFiscalYear);
		aoResourceRequest.setAttribute(HHSConstants.AMEND_CONTRACT_ID_WORKFLOW, lsAmendContracId);
		aoResourceRequest.setAttribute(HHSConstants.BUDGET_CONTRACT_TITLE, lsContractTitle);
		aoResourceRequest.setAttribute(HHSConstants.PROVIDER_ORG_ID_AMEND, lsProviderOrgId);
		loModelAndView = new ModelAndView(lsJspName);
		return loModelAndView;

	}

	/**
	 * This method is used to fetch the bulk upload template file properties.<br>
	 * This method is added in R4
	 * <ul>
	 * <li>1.Get the required view name from request</li>
	 * <ul>
	 * 
	 * @param aoResourceRequest Resource Request Object
	 * @return model and view of the requested jsp page
	 */
	@SuppressWarnings("unchecked")
	@ResourceMapping("getBulkUploadTemplatePage")
	public ModelAndView getBulkContractUploadTemplate(ResourceRequest aoResourceRequest)
	{
		ModelAndView loModelAndView = null;
		try
		{
			String lsJspName = (String) aoResourceRequest.getParameter(HHSConstants.JSP_NAME);

			PortletSession loSession = aoResourceRequest.getPortletSession();
			P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);

			Channel loChannel = new Channel();
			loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);

			HashMap<String, Object> loFilterProps = new HashMap<String, Object>();
			FileNetOperationsUtils.reInitializePageIterator(loSession, loUserSession);
			loFilterProps.put(HHSConstants.IS_BULK_UPLOAD, true);
			HashMap<String, String> loReqProps = new HashMap<String, String>();
			loReqProps.put(HHSConstants.TEMPLATE_VERSION_NO, HHSConstants.EMPTY_STRING);
			loReqProps.put(HHSConstants.TEMPLATE_LAST_MODIFIED_DATE, HHSConstants.EMPTY_STRING);
			loReqProps.put(HHSConstants.TEMPLATE_ID, HHSR5Constants.EMPTY_STRING);
			// Added extra Parameter for Release 5
			List<HashMap<String, Object>> loDocumentList = FileNetOperationsUtils.getDocumentList(loChannel, null,
					loReqProps, loFilterProps, true, HHSR5Constants.EMPTY_STRING);
			if (null != loDocumentList)
			{
				Map loDocProps = (HashMap) loDocumentList.get(0);
				String lsTemplateVersion = (String) loDocProps.get(HHSConstants.TEMPLATE_VERSION_NO);
				String lsLastModDate = DateUtil.getDateMMddYYYYFormat((Date) loDocProps
						.get(HHSConstants.TEMPLATE_LAST_MODIFIED_DATE));
				String lsDocId = loDocProps.get(HHSConstants.TEMPLATE_IDEN).toString();
				loReqProps.put(HHSConstants.TEMPLATE_VERSION_NO, lsTemplateVersion);
				loReqProps.put(HHSConstants.TEMPLATE_LAST_MODIFIED_DATE, lsLastModDate);
				loReqProps.put(HHSConstants.TEMPLATE_ID, lsDocId);
			}
			aoResourceRequest.setAttribute(HHSConstants.BULK_UPLOAD_DOC_PROPS, loReqProps);
			loModelAndView = new ModelAndView(lsJspName);
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("ApplicationException occured while fetching bulk upload template", aoAppEx);
			String lsErrorMessage;
			try
			{
				lsErrorMessage = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.GENERIC_ERROR_MESSAGE);

				aoResourceRequest.getPortletSession().setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMessage,
						PortletSession.APPLICATION_SCOPE);

				aoResourceRequest.getPortletSession().setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE, PortletSession.APPLICATION_SCOPE);
			}
			catch (ApplicationException e)
			{
				LOG_OBJECT
						.Error("ApplicationException occured while fetching properties in ContractListController:getBulkContractUploadTemplate",
								aoAppEx);
			}
		}
		return loModelAndView;
	}

	/**
	 * This method is used to upload the bulk upload file to file system and
	 * update the file's properties to DB <br>
	 * This method added in R4.
	 * <ul>
	 * <li>1.Get the required view name from request</li>
	 * <li>2. This method will execute the transaction
	 * 'bulkContractUpload_filenet'</li>
	 * <ul>
	 * 
	 * @param aoRequest ActionRequest Object
	 * @param aoResponse ActionResponse Object
	 * @throws PortletException if PortletException occurs
	 * @throws MessagingException if MessagingException occurs
	 * @throws ApplicationException if ApplicationException occurs
	 * @throws IOException if IOException occurs
	 */
	@SuppressWarnings("unchecked")
	@ActionMapping(params = "submit_action=getUploadBulkContract")
	protected void getUploadBulkContractUrl(ActionRequest aoRequest, ActionResponse aoResponse)
			throws PortletException, MessagingException, IOException, ApplicationException
	{
		PortletSession loSession = aoRequest.getPortletSession();
		String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		MultipartActionRequestParser loFormdata = new MultipartActionRequestParser(aoRequest);
		MimeBodyPart[] loMimeparts = loFormdata.getMimeBodyParts(ApplicationConstants.MIME_UPLOAD_FILE);
		MimeBodyPart[] loMimepartsNewVersion = loFormdata
				.getMimeBodyParts(ApplicationConstants.MIME_UPLOAD_NEW_VERSION);
		// get and preserve filter values
		MimeBodyPart loFiledata = null;
		Channel loChannel = new Channel();
		HashMap<String, Object> loParamMap = new HashMap<String, Object>();
		InputStream loInputstream = null;
		String lsErrorMessage = HHSConstants.EMPTY_STRING;
		try
		{
			lsErrorMessage = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
					HHSConstants.BULK_FILE_UPLOAD_FAIL_MESSAGE);
			if (null == loMimepartsNewVersion)
			{
				loFiledata = loMimeparts[0];
			}
			String lsContentType = HHSConstants.EMPTY_STRING;
			StringBuffer lsBfContentType = new StringBuffer();
			int liSemiColon = HHSConstants.INT_ZERO;
			if (null != loFiledata && null != loFiledata.getContentType())
			{
				lsContentType = loFiledata.getContentType();
				liSemiColon = lsContentType.lastIndexOf(HHSConstants.DELIMITER_SEMICOLON);
			}
			if (null != loFiledata && null != loFiledata.getInputStream())
			{
				loInputstream = loFiledata.getInputStream();
			}
			if (liSemiColon == -1)
			{
				lsBfContentType.append(lsContentType);
			}
			else
			{
				lsBfContentType.append(lsContentType.substring(0, liSemiColon));
			}
			String lsFileName = HHSR5Constants.EMPTY_STRING;
			if (null != loFiledata && null != loFiledata.getFileName())
			{
				lsFileName = loFiledata.getFileName();
				lsFileName.lastIndexOf(HHSConstants.DOT);
				loParamMap.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, lsFileName);
			}
			loParamMap.put(P8Constants.PROPERTY_CE_MIME_TYPE, lsBfContentType.toString());
			loParamMap.put(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY, lsUserId);
			loParamMap.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY, lsUserId);
			BaseCacheManagerWeb.getInstance().getCacheObject(ApplicationConstants.APPLICATION_SETTING);
			if (null != loFiledata)
			{
				LOG_OBJECT.Debug("Uploaded File Size: " + loFiledata.getSize());
				if (validateUploadFile(aoRequest, aoResponse, loFiledata))
				{
					loChannel.setData(HHSConstants.BULK_UPLOAD_INPUT_STREAM, loInputstream);
					loChannel.setData(HHSConstants.BULK_UPLOAD_FILE_PROPS, loParamMap);
					HHSTransactionManager.executeTransaction(loChannel, HHSConstants.BULK_UPLOAD_TRANSACTION_ID);
					aoRequest.getPortletSession().setAttribute(ApplicationConstants.ERROR_MESSAGE,
							HHSConstants.BULK_UPLOAD_SUCCESS_MESSAGE, PortletSession.APPLICATION_SCOPE);
					aoRequest.getPortletSession().setAttribute(HHSConstants.MESSAGE_TYPE,
							HHSConstants.MESSAGE_PASS_TYPE, PortletSession.APPLICATION_SCOPE);
				}
			}
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Exception occurred in upload file", aoAppEx);
			aoRequest.getPortletSession().setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMessage,
					PortletSession.APPLICATION_SCOPE);

			aoRequest.getPortletSession().setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE, PortletSession.APPLICATION_SCOPE);
		}
		aoResponse.sendRedirect(aoRequest.getContextPath() + ApplicationConstants.ERROR_HANDLER);
	}

	/**
	 * <ul>
	 * <li>
	 * This method validates the file to be uploaded to file system</li>
	 * <li>This method is added in R4</li>
	 * </ul>
	 * 
	 * @param aoRequest-defines the ActionRequest object
	 * @param aoResponse-defines the ActionResponse object
	 * @param aoFiledata- defines the data of the file to be uploaded
	 * @throws MessagingException if MessagingException occurs
	 * @throws ApplicationException- defines the exception thrown from the
	 *             method
	 * 
	 * 
	 */
	public boolean validateUploadFile(ActionRequest aoRequest, ActionResponse aoResponse, MimeBodyPart aoFiledata)
			throws MessagingException, ApplicationException
	{
		boolean lbFileIsFine = true;
		String lsErrorMessage = HHSR5Constants.EMPTY_STRING;
		String lsAppSettingMapKey = HHSConstants.BULK_UPLOAD_APP_SETTING_NAME + HHSConstants.UNDERSCORE
				+ HHSConstants.BULK_UPLOAD_FILE_SIZE;
		HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb.getInstance()
				.getCacheObject(ApplicationConstants.APPLICATION_SETTING);
		long llAllowedDocSize = Long.valueOf(loApplicationSettingMap.get(lsAppSettingMapKey));
		String lsFileName = aoFiledata.getFileName();
		int liExtension = lsFileName.lastIndexOf(HHSR5Constants.DOT);
		String lsFileType = lsFileName.substring(liExtension + 1);
		LOG_OBJECT.Debug("Uploaded File Size: " + aoFiledata.getSize());
		try
		{
			if (aoFiledata.getSize() > llAllowedDocSize)
			{
				lsErrorMessage = HHSConstants.BULK_UPLOAD_FILENET_UPLOAD_FILE_SIZE_ERROR;
				lbFileIsFine = false;
				aoRequest.getPortletSession().setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMessage,
						PortletSession.APPLICATION_SCOPE);
				aoRequest.getPortletSession().setAttribute(ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
						ApplicationConstants.ERROR, PortletSession.APPLICATION_SCOPE);
			}
			if (lsFileName.length() >= 50)
			{
				lsErrorMessage = HHSConstants.BULK_UPLOAD_FILENET_UPLOAD_FILE_NAME_ERROR;
				lbFileIsFine = false;
				aoRequest.getPortletSession().setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMessage,
						PortletSession.APPLICATION_SCOPE);
				aoRequest.getPortletSession().setAttribute(ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
						ApplicationConstants.ERROR, PortletSession.APPLICATION_SCOPE);

			}
			if (!(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGE_PROP_FILE, HHSConstants.BULK_UPLOAD_FILE_TYPE)
					.equalsIgnoreCase(lsFileType)))
			{
				lsErrorMessage = HHSConstants.BULK_UPLOAD_FILENET_UPLOAD_FILE_TYPE_ERROR
						+ PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGE_PROP_FILE,
								HHSConstants.BULK_UPLOAD_FILE_TYPE);
				lbFileIsFine = false;
				aoRequest.getPortletSession().setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMessage,
						PortletSession.APPLICATION_SCOPE);
				aoRequest.getPortletSession().setAttribute(ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
						ApplicationConstants.ERROR, PortletSession.APPLICATION_SCOPE);

			}

			if (!(lsFileName.substring(HHSConstants.INT_ZERO, lsFileName.lastIndexOf(HHSConstants.DOT))
					.matches(HHSConstants.PATTERN_BULK_UPLOAD_FILE)))
			{
				lsErrorMessage = HHSConstants.BULK_UPLOAD_FILENET_UPLOAD_FILE_NAME_CHAR_ERROR;
				lbFileIsFine = false;
				aoRequest.getPortletSession().setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMessage,
						PortletSession.APPLICATION_SCOPE);
				aoRequest.getPortletSession().setAttribute(ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
						ApplicationConstants.ERROR, PortletSession.APPLICATION_SCOPE);

			}

		}
		catch (Exception aoEx)
		{
			throw new ApplicationException(aoEx.getMessage(), aoEx);
		}

		return lbFileIsFine;
	}

	/**
	 * This method is used to display the Renew Contract page populated with
	 * Epin details
	 * <ul>
	 * <li>1.Get all the fields value from database based on epin passed</li>
	 * <li>2.Populate the jsp with the bean values and display the jsp</li>
	 * <ul>
	 * 
	 * calls the transaction 'fetchContractDetailsByEPIN'
	 * 
	 * @param aoResRequest Resource Request Object
	 * @param aoResResponse Resource Response Object
	 * @return ModelAndView object loModelAndView
	 */
	@ResourceMapping("populateRenewContractPage")
	public ModelAndView populateRenewContractPage(ResourceRequest aoResRequest, ResourceResponse aoResResponse)
	{
		ModelAndView loModelAndView = new ModelAndView(HHSConstants.CLC_RENEW_CONTRACT);
		String lsEpin = (String) aoResRequest.getParameter(HHSConstants.EPIN_VALUE);
		String lsContracId = (String) aoResRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW);
		EPinDetailBean loEPinDetail = new EPinDetailBean();
		loEPinDetail.setContractId(lsContracId);
		loEPinDetail.setEpinId(lsEpin);
		aoResRequest.setAttribute(HHSConstants.EPIN_BEAN_DETAILS, loEPinDetail);
		String lsError = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
		try
		{
			Channel loChannel = new Channel();
			loChannel.setData(HHSConstants.LO_CONTRACT_DETAILS, loEPinDetail);
			if (loEPinDetail.getEpinId() != null
					&& !loEPinDetail.getEpinId().equalsIgnoreCase(HHSConstants.EMPTY_STRING))
			{
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_CONTRACT_DETAILS_BY_EPIN);
			}
			loEPinDetail = (EPinDetailBean) loChannel.getData(HHSConstants.AO_CONTRACT_DETAIL);
			if (loEPinDetail != null)
			{
				loEPinDetail.setContractId(lsContracId);
				loEPinDetail.setContractTitle(StringEscapeUtils.escapeHtml((String) loEPinDetail.getContractTitle()));
				aoResRequest.setAttribute(HHSConstants.EPIN_BEAN_DETAILS, loEPinDetail);
				lsError = HHSConstants.EMPTY_STRING;
			}
			else
			{
				lsError = HHSConstants.EPIN_ERROR;
			}
		}
		catch (ApplicationException aoExe)
		{
			LOG_OBJECT.Error("ApplicationException occured in populateRenewContractPage", aoExe);

		}
		catch (Exception aoExe)
		{
			LOG_OBJECT.Error("Exception occured in populateRenewContractPage", aoExe);
		}
		aoResRequest.setAttribute(HHSConstants.CLC_CAP_ERROR, lsError);
		return loModelAndView;

	}

	/**
	 * 
	 * Changed method - Build 3.1.0, Enhancement id: 6020 This method updated
	 * for Release 3.12.0 enhancement 6580
	 * <p>
	 * This method is modified for enhancement to have pop while adding a new
	 * contract. This pop will give user option to configure current year budget
	 * or next year budget. Pop will only come for cases when contract is being
	 * added such that contract start and end dates are in range of new fiscal
	 * year configuration
	 * 
	 * This method is used to display the Add Contract page populated with Epin
	 * details
	 * <ul>
	 * <li>1.Get all the fields value from database based on epin passed</li>
	 * <li>2.Populate the jsp with the bean values and display the jsp</li>
	 * <ul>
	 * 
	 * calls the transaction 'fetchContractDetailsByEPINforNew'
	 * </p>
	 * @param aoResourceRequest Resource Request Object
	 * @param aoResourceResponse Resource Response Object
	 * @return loModelAndView - ModelAndView object
	 */
	@SuppressWarnings("unchecked")
	@ResourceMapping("populateAddContractPage")
	public ModelAndView populateAddContractPage(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		ModelAndView loModelAndView = new ModelAndView(HHSConstants.CLC_ADD_CONTRACT);
		String lsEpin = (String) aoResourceRequest.getParameter(HHSConstants.EPIN_VALUE);
		EPinDetailBean loEPinDetailBean = new EPinDetailBean();
		/* R6: We are updating the query parameter to also check agency id */
		loEPinDetailBean.setEpinId(lsEpin);
		aoResourceRequest.setAttribute(HHSConstants.EPIN_BEAN_DETAILS, loEPinDetailBean);
		String lsError = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
		try
		{

			Channel loChannel = new Channel();
			loChannel.setData(HHSConstants.LO_EPIN_DETAIL, loEPinDetailBean);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_CONTRACT_DETAILS_BY_EPIN_NEW);
			loEPinDetailBean = (EPinDetailBean) loChannel.getData(HHSConstants.AO_CONTRACT_DETAIL);
			if (loEPinDetailBean != null)
			{
				aoResourceRequest.setAttribute(HHSConstants.EPIN_BEAN_DETAILS, loEPinDetailBean);
				lsError = HHSConstants.EMPTY_STRING;
			}
			else
			{
				lsError = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.NO_EPIN_MATCH);
			}
		}
		catch (ApplicationException aoExe)
		{
			LOG_OBJECT.Error("ApplicationException occured in populateAddContractPage", aoExe);

		}
		catch (Exception aoExe)
		{
			LOG_OBJECT.Error("Exception occured in populateAddContractPage", aoExe);
		}
		aoResourceRequest.setAttribute(HHSConstants.CLC_CAP_ERROR, lsError);
		return loModelAndView;

	}

	/**
	 * This method print the error using print Writer object
	 * 
	 * @param loOut
	 * @param asError
	 */
	private void catchTaskError(PrintWriter loOut, String asError)
	{
		loOut.print(asError);
		loOut.flush();
		loOut.close();
	}

	/**
	 * This method decide the execution flow on click of Renew Contract Button
	 * The method is modified for enhancement 5707 as part of release 2.6.1
	 * <ul>
	 * <li>Validate if contract start and end date fall under expected span</li>
	 * <li>Validate is Epin already associated to any contract</li>
	 * <li>Validate is selected contract already has renewal record</li>
	 * <li>Insert the contract record into the database</li>
	 * <li>Trigger WF302 ? Contract Configuration</li>
	 * <li>Set the < Contract Status> = Pending Configuration</li>
	 * <li>Get the status of the insertion</li>
	 * <li>if status is false show error on the Overlay</li>
	 * <li>If status is true close the overlay.</li>
	 * </ul>
	 * 
	 * calls the transaction 'renewContractDateValidation'
	 * 
	 * calls the transaction 'fetchReviewLevelCB'
	 * 
	 * calls the transaction 'renewExistingContractDetails'
	 * 
	 * @param aoResourceRequest ResourceRequest object
	 * @param aoResourceResponse ResourceResponse object
	 */

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	@ResourceMapping("renewContractSubmit")
	protected ModelAndView renewContract(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		EPinDetailBean loEPinDetailBean = new EPinDetailBean();
		Channel loChannel = null;
		String lsMsg = HHSConstants.EMPTY_STRING;
		PrintWriter loOut = null;
		ModelAndView loModelAndView = null;
		aoResourceResponse.setContentType(HHSConstants.TEXT_HTML);
		boolean lbIsRenewalRecordNotExist = true;
		try
		{
			loOut = aoResourceResponse.getWriter();
			loEPinDetailBean.setContractTypeId(HHSConstants.THREE);
			populateEpinBeanFromRequest(aoResourceRequest, loEPinDetailBean);

			loEPinDetailBean.setStatusId(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_CONTRACT_PENDING_CONFIGURATION));
			loEPinDetailBean.setPrevStatusId(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_CONTRACT_REGISTERED));
			loEPinDetailBean.setContractTypeId(HHSConstants.CONTRACT_RENEWAL_TYPE_ID);
			// below flag is added as fixed for enhancement 5707 as part of
			// release 2.6.1
			boolean lbfiscalYearSpanFlag = HHSUtil.checkContractFiscalYearsSpan(loEPinDetailBean.getContractStart(),
					loEPinDetailBean.getContractEnd());
			if (!lbfiscalYearSpanFlag)
			{
				Boolean loAuthStatusFlag = true;
				loChannel = new Channel();
				loChannel.setData(HHSConstants.AS_EPIN, loEPinDetailBean.getEpinId());
				loChannel.setData(HHSConstants.AO_CONTRACT_DETAILS_BY_EPIN, loEPinDetailBean);
				loChannel.setData(HHSConstants.LO_AUTH_STATUS_FLAG, loAuthStatusFlag);
				/*
				 * R6: EPIN uniqueness for EPIn and Agency ID. Removing the
				 * previous validation method
				 */
				boolean lbIsEpinUnique = ContractListUtils.validateEpinUnique(loEPinDetailBean);
				// boolean lbIsEpinValid =
				// ContractListUtils.validateEpin(loChannel);
				if (lbIsEpinUnique)
				{
					loChannel.setData(HHSConstants.AS_VENDOR_FMS_ID, loEPinDetailBean.getVendorFmsId());
					if (ContractListUtils.validateProviderAccelerator(loChannel))
					{
						List loList = new ArrayList();
						loList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_CONTRACT_PENDING_REGISTARTION));
						loList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_CONTRACT_PENDING_CONFIGURATION));
						loList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_CONTRACT_PENDING_COF));
						Map loHashMap = new HashMap();
						loHashMap.put(HHSConstants.CLC_CONTRACT_ID_UNDERSCORE, loEPinDetailBean.getContractId());
						loHashMap.put(HHSConstants.CLC_STATUS_ID, loList);

						loHashMap.put(HHSConstants.CL_CONTRACT_TYPE_ID, HHSConstants.CONTRACT_RENEWAL_TYPE_ID);

						loChannel.setData(HHSConstants.AO_CONTRACT_STATUS, loHashMap);
						lbIsRenewalRecordNotExist = ContractListUtils.getRenewalRecordExist(loChannel);
						if (lbIsRenewalRecordNotExist)
						{
							boolean lbValidationStatus = false;
							// date validation
							HHSTransactionManager.executeTransaction(loChannel, HHSConstants.RENEW_CONTRACT_DATE_VALIDATION);
							lbValidationStatus = (Boolean) loChannel.getData(HHSConstants.LB_SUCCESS_STATUS);
							if (lbValidationStatus)
							{
								loChannel.setData(HHSConstants.PROPERTY_PE_AGENCY_ID, loEPinDetailBean.getAgencyId());
								loChannel.setData(HHSConstants.REVIEW_PROC_ID,
										HHSConstants.CONTRACT_CERTIFICATION_OF_FUNDS_ID);
								// Renew level Validation
								HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_REVIEW_LEVEL_CB);
								Integer loReviewLevel = (Integer) loChannel.getData(HHSConstants.REVIEW_LEVEL);
								// If review level is not set
								if (loReviewLevel == HHSConstants.INT_ZERO)
								{
									lsMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
											HHSConstants.CB_REVIEW_LEVEL_ERROR);
								}
								else
								{
									PortletSession loSession = aoResourceRequest.getPortletSession();
									P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
											ApplicationConstants.FILENET_SESSION_OBJECT,
											PortletSession.APPLICATION_SCOPE);
									loChannel.setData(HHSConstants.LB_AUTH_STATUS_FLAG, true);
									loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
									HHSTransactionManager.executeTransaction(loChannel,
											HHSConstants.RENEW_CONTRACT_DETAILS);
								}
							}
							else
							{
								lsMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
										HHSConstants.RENEW_CONTRACT_DATE_VALIDATION_ERROR);
							}
						}
						else
						{
							lsMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
									HHSConstants.IS_RENEWAL_EXITS);
						}
					}
					else
					{
						lsMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
								HHSConstants.PROVIDER_IN_ACCELERATOR_RENEW);
					}
				}
				else
				{
					/*
					 * R6: EPIN VALIDATION - updated error message when epin is
					 * not unique
					 */
					// lsMsg =
					// PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
					// HHSConstants.IS_VALID_EPIN);
					lsMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSR5Constants.EPIN_ALREADY_USE);
				}
			}
			else
			{
				lsMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.ERROR_CONTRACT_TERM_EXCEED);
			}
		}
		catch (ApplicationException aoExe)
		{
			lsMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("ApplicationException occured in renewContract", aoExe);
		}
		catch (Exception aoExe)
		{
			lsMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("Exception occured in renewContract", aoExe);
		}
		finally
		{
			catchTaskError(loOut, lsMsg);
		}
		return loModelAndView;
	}

	/**
	 * / This method populates the EpinBean bean from the request on click of
	 * Add or Renew contract button
	 * <ul>
	 * <li>Populate the bean from request param</li>
	 * </ul>
	 * 
	 * @param aoResourceRequest - ResourceRequest object
	 * @param aoEPinDetailBean - EPinDetailBean object
	 */
	private void populateEpinBeanFromRequest(ResourceRequest aoResourceRequest, EPinDetailBean aoEPinDetailBean)
	{
		aoEPinDetailBean.setEpinId(aoResourceRequest.getParameter(HHSConstants.CLC_AWARD_EPIN));
		aoEPinDetailBean.setProcurementStartDate(aoResourceRequest
				.getParameter(HHSConstants.CLC_PROCUREMENT_START_DATE));
		aoEPinDetailBean.setAgencyDiv(aoResourceRequest.getParameter(HHSConstants.AGENCY_DIVISION));
		if (aoEPinDetailBean.getContractTypeId() != null
				&& aoEPinDetailBean.getContractTypeId().equalsIgnoreCase(HHSConstants.THREE))
		{
			aoEPinDetailBean.setAgencyId(aoResourceRequest.getParameter(HHSConstants.PREV_CONTRACT_AGENCYID));
		}
		else
		{
			aoEPinDetailBean.setAgencyId(aoResourceRequest.getParameter(HHSConstants.AGENCYID));
		}
		aoEPinDetailBean.setProcMethod(aoResourceRequest.getParameter(HHSConstants.PROCUREMENT_METHOD));
		aoEPinDetailBean.setProjProg(aoResourceRequest.getParameter(HHSConstants.APT_PROJECT));
		aoEPinDetailBean.setProgramId(aoResourceRequest.getParameter(HHSConstants.ACC_PROGRAM_NAME));
		aoEPinDetailBean.setProcDescription(aoResourceRequest.getParameter(HHSConstants.APT_PROCUREMENT_DESC)  );
		aoEPinDetailBean.setContractTitle(aoResourceRequest.getParameter(HHSConstants.CONTRACT_TITLE_POP_UP));
		aoEPinDetailBean.setVendorFmsId(aoResourceRequest.getParameter(HHSConstants.VENDOR_FMS_ID));
		aoEPinDetailBean.setVendorFmsName(aoResourceRequest.getParameter(HHSConstants.VENDOR_FMS_NAME));
		aoEPinDetailBean.setProviderLegalName(aoResourceRequest.getParameter(HHSConstants.PROVIDER_LEGAL_NAME));
		aoEPinDetailBean.setContractValue(aoResourceRequest.getParameter(HHSConstants.BMC_CONTRACT_VALUE).replaceAll(
				HHSConstants.COMMA, HHSConstants.EMPTY_STRING));
		aoEPinDetailBean.setContractStart(aoResourceRequest.getParameter(HHSConstants.CONTRACT_START_DATE));
		aoEPinDetailBean.setContractEnd(aoResourceRequest.getParameter(HHSConstants.CONTRACT_END_DATE));
		aoEPinDetailBean.setContractId(aoResourceRequest.getParameter(HHSConstants.HDN_CONTRACT_ID));
		aoEPinDetailBean.setContractSourceId(HHSConstants.TWO);
		aoEPinDetailBean.setRegistrationFlag(HHSConstants.STRING_ZERO);
		aoEPinDetailBean.setUpdateFlag(HHSConstants.STRING_ZERO);
		aoEPinDetailBean.setDeleteFlag(HHSConstants.STRING_ZERO);
		aoEPinDetailBean.setDiscrepancyFlag(HHSConstants.STRING_ZERO);
		aoEPinDetailBean.setCreateByUserId(String.valueOf(aoResourceRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE)));
		aoEPinDetailBean.setChkContractCertFundsFlag(aoResourceRequest
				.getParameter(HHSConstants.CHK_CONTRACT_CERT_FUNDS));
		if (aoEPinDetailBean.getChkContractCertFundsFlag() == null
				|| aoEPinDetailBean.getChkContractCertFundsFlag().equals(HHSConstants.EMPTY_STRING))
		{
			aoEPinDetailBean.setChkContractCertFundsFlag(HHSConstants.STRING_ZERO);
			aoEPinDetailBean.setLaunchCOF(false);
		}

		/* R6: Setting refAptEpinId into the bean */
		aoEPinDetailBean.setRefAptEpinId(aoResourceRequest.getParameter(HHSConstants.REF_APT_EPIN_ID));
	}

	/**
	 * Changed method - Build 3.1.0, Enhancement id: 6020
	 * <p>
	 * This method is modified for enhancement to save budget start year while
	 * adding a new contract
	 * 
	 * This method decide the execution flow on click of Add Contract Button The
	 * method is modified for enhancement 5707 as part of release 2.6.1
	 * <ul>
	 * <li>Validate if contract start and end date fall under expected span</li>
	 * <li>Insert the contract record into the database</li>
	 * <li>Trigger WF302 Contract Configuration</li>
	 * <li>Set the < Contract Status> = Pending Configuration</li>
	 * <li>Get the status of the insertion</li>
	 * <li>if status is false show error on the Overlay</li>
	 * <li>If status is true close the overlay.</li>
	 * </ul>
	 * calls the transaction 'addContractDetails'
	 * </p>
	 * 
	 * @param aoResourceRequest - ResourceRequest
	 * @param aoResourceResponse - ResourceResponse
	 * @return loModelAndView - ModelAndView
	 */
	@ResourceMapping("addContractSubmit")
	protected ModelAndView addContract(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		EPinDetailBean loEPinDetailBean = new EPinDetailBean();
		Channel loChannel = null;
		String lsMsg = HHSConstants.EMPTY_STRING;
		PrintWriter loOut = null;
		ModelAndView loModelAndView = null;
		aoResourceResponse.setContentType(HHSConstants.TEXT_HTML);
		try
		{
			loOut = aoResourceResponse.getWriter();
			populateEpinBeanFromRequest(aoResourceRequest, loEPinDetailBean);
			loEPinDetailBean.setAwardAgencyId(aoResourceRequest.getParameter(HHSConstants.AWARD_AGENCY_ID));
			loEPinDetailBean.setContractTypeId(HHSConstants.ONE);
			loEPinDetailBean.setStatusId(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_CONTRACT_PENDING_CONFIGURATION));

			// start budget start year is getting populated as part of
			// enhancement 6020 as part of
			// release 3.1.0
			loEPinDetailBean.setBudgetStartYear(aoResourceRequest.getParameter(HHSR5Constants.NEXT_FISCAL_YEAR_VALUE));
			// end

			// below flag is added as fixed for enhancement 5707 as part of
			// release 2.6.1
			boolean lbfiscalYearSpanFlag = HHSUtil.checkContractFiscalYearsSpan(loEPinDetailBean.getContractStart(),
					loEPinDetailBean.getContractEnd());
			if (!lbfiscalYearSpanFlag)
			{
				Boolean loAuthStatusFlag = true;
				loChannel = new Channel();
				loChannel.setData(HHSConstants.AS_EPIN, loEPinDetailBean.getEpinId());
				loChannel.setData(HHSConstants.AO_CONTRACT_DETAILS_BY_EPIN, loEPinDetailBean);
				loChannel.setData(HHSConstants.LO_AUTH_STATUS_FLAG, loAuthStatusFlag);
				/*
				 * R6: EPIN uniqueness for EPIn and Agency ID. Removing the
				 * previous validation method
				 */
				boolean lbIsEpinUnique = ContractListUtils.validateEpinUnique(loEPinDetailBean);
				// boolean lbIsEpinValid =
				// ContractListUtils.validateEpin(loChannel);
				if (lbIsEpinUnique)
				{
					loChannel.setData(HHSConstants.AS_VENDOR_FMS_ID, loEPinDetailBean.getVendorFmsId());
					boolean lbProviderRegistered = ContractListUtils.validateProviderAccelerator(loChannel);
					if (lbProviderRegistered)
					{
						PortletSession loSession = aoResourceRequest.getPortletSession();
						P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
								ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
						loChannel.setData(HHSConstants.LB_AUTH_STATUS_FLAG, true);
						loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);

						HHSTransactionManager.executeTransaction(loChannel, HHSConstants.ADD_CONTRACT_DETAILS);
					}
					else
					{
						lsMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
								HHSConstants.PROVIDER_IN_ACCELERATOR);
					}
				}
				else
				{
					/*
					 * R6: EPIN VALIDATION - Using the new message when contract
					 * fails to save because of duplicacy
					 */
					// lsMsg =
					// PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
					// HHSConstants.IS_VALID_EPIN);
					lsMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSR5Constants.EPIN_ALREADY_USE);
				}
			}
			else
			{
				lsMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.ERROR_CONTRACT_TERM_EXCEED);
			}
		}
		catch (ApplicationException aoAppExe)
		{
			ApplicationException loAppEx = (ApplicationException) aoAppExe.getRootCause();
			if (null != loAppEx)
			{
				lsMsg = (String) loAppEx.getContextData().get(HHSConstants.LEVEL_ERROR_MESSAGE);
				LOG_OBJECT.Error("ApplicationException occured in addContract for Levels of review", aoAppExe);
			}
			if (null == lsMsg)
			{
				lsMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
				LOG_OBJECT.Error("ApplicationException occured in addContract", aoAppExe);
			}
		}

		catch (Exception aoExe)
		{
			lsMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("Exception occured in addContract", aoExe);
		}
		finally
		{
			catchTaskError(loOut, lsMsg);
		}
		return loModelAndView;
	}

	/**
	 * Changed method - Build 3.8.0, Enhancement id: 6482
	 * <p>
	 * This method is added to update existing contract
	 * 
	 * calls the transaction 'updateContractInformation'
	 * </p>
	 * 
	 * @param aoResourceRequest - ResourceRequest
	 * @param aoResourceResponse - ResourceResponse
	 * @return loModelAndView - ModelAndView
	 */
	@ResourceMapping("updateContractSubmit")
	protected void updateContract(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		EPinDetailBean loEPinDetailBean = new EPinDetailBean();
		Channel loChannel = new Channel();
		PrintWriter loOut = null;
		String lsErrorMsg = HHSConstants.EMPTY_STRING;
		try
		{
			loOut = aoResourceResponse.getWriter();
			PortletSession loSession = aoResourceRequest.getPortletSession();
			P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			String lsOrgnizationType = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
					PortletSession.APPLICATION_SCOPE);
			String lsUserId = (String) aoResourceRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			if (null != aoResourceRequest.getParameter(HHSConstants.HDN_CONTRACT_ID))
			{
				loEPinDetailBean.setContractId(aoResourceRequest.getParameter(HHSConstants.HDN_CONTRACT_ID));
			}
			if (null != aoResourceRequest.getParameter(HHSConstants.CONTRACT_TITLE_POP_UP))
			{
				loEPinDetailBean.setContractTitle(aoResourceRequest.getParameter(HHSConstants.CONTRACT_TITLE_POP_UP));
			}
			if (null != aoResourceRequest.getParameter(HHSConstants.PROGRAM_ID))
			{
				loEPinDetailBean.setProgramNameId(aoResourceRequest.getParameter(HHSConstants.PROGRAM_ID));
			}
			if (null != aoResourceRequest.getParameter(HHSConstants.PROGRAM_NAME))
			{
				loEPinDetailBean.setProgramName(aoResourceRequest.getParameter(HHSConstants.PROGRAM_NAME));
			}
			if (null != aoResourceRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE))
			{
				loEPinDetailBean.setModifyByUserId(String.valueOf(aoResourceRequest.getPortletSession().getAttribute(
						ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE)));
			}
			if (lsOrgnizationType != null && (lsOrgnizationType.equalsIgnoreCase(ApplicationConstants.CITY_ORG)))
			{
				HHSUtil.addAuditDataToChannel(loChannel, HHSConstants.CONTRACT_INFORMATION_UPDATE,
						HHSConstants.CONTRACT_INFORMATION_UPDATE,
						HHSR5Constants.CONTRACT_TITLE_UPDATED_TO + loEPinDetailBean.getContractTitle()
								+ HHSR5Constants.AND_PROGRAM_NAME_UPDATED_TO + loEPinDetailBean.getProgramName()
								+ HHSR5Constants.FOR_CONTRACT_ID + loEPinDetailBean.getContractId(),
						HHSConstants.CONTRACT, loEPinDetailBean.getContractId(), lsUserId,
						HHSConstants.ACCELERATOR_AUDIT, HHSConstants.AUDIT_BEAN);
			}
			loChannel.setData(HHSConstants.CONTRACT_ID_KEY, loEPinDetailBean.getContractId());
			loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
			loChannel.setData(HHSConstants.AO_CONTRACT_DETAILS_BY_EPIN, loEPinDetailBean);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.UPDATE_CONTRACT_INFORMATION);
			Boolean lbUpdateContractRule = (Boolean) loChannel.getData(HHSConstants.ERROR_CHECK_RULE);
			if (!lbUpdateContractRule)
			{
				aoResourceRequest.getPortletSession().setAttribute(
						HHSConstants.ERROR_MESSAGE,
						PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
								HHSConstants.UPDATE_CONTRACT_RULE), PortletSession.APPLICATION_SCOPE);
			}
			else
			{
				aoResourceRequest.getPortletSession().setAttribute(
						HHSConstants.SUCCESS_MESSAGE,
						PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
								HHSConstants.UPDATE_CONTRACT_STATUS), PortletSession.APPLICATION_SCOPE);
			}
		}
		catch (ApplicationException loEx)
		{
			lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("Exception occured in updateContract ", loEx);

		}
		catch (Exception loExe)
		{
			lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("Exception occured in updateContract ", loExe);
		}
		finally
		{
			catchTaskError(loOut, lsErrorMsg);
		}
	}

	/**
	 * This method decide the execution flow on click of Yes, cancel this
	 * Amendment Button<br>
	 * Method Updated in R4
	 * <ul>
	 * <li>Validate username and password</li>
	 * <li>validate Amendment Status equal to Pending Registration</li>
	 * <li>If negative amendment with Approved Amendment Budget was cancelled
	 * prior to registration, trigger WF310 ? Contract Configuration Update.</li>
	 * <li>Trigger WF302 ? Contract Configuration</li>
	 * <li>Set the < Contract Status> = Pending Configuration</li>
	 * <li>Get the status of the insertion</li>
	 * <li>if status is false show error on the Overlay</li>
	 * <li>If status is true close the overlay.</li>
	 * </ul>
	 * 
	 * <ul>
	 * <li>executes the transaction 'validateAmendContract'</li>
	 * <li>executes the transaction 'cancellingNegativeAmendmentCheck'</li>
	 * <li>executes the transaction 'cancelAmendment'</li>
	 * <ul>
	 * @param aoResourceRequest ResourceRequest object
	 * @param aoResourceResponse ResourceResponse object
	 * @return ModelAndView object
	 * 
	 * 
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@ResourceMapping("cancelAmendment")
	protected ModelAndView actionCancelAmendment(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		Channel loChannel = null;
		String lsMsg = HHSConstants.EMPTY_STRING;
		PrintWriter loOut = null;
		ModelAndView loModelAndView = null;
		aoResourceResponse.setContentType(HHSConstants.TEXT_HTML);
		boolean lbUserValidation = false, lbStatus = false;
		try
		{
			loOut = aoResourceResponse.getWriter();
			String lsUserEmailId = aoResourceRequest.getParameter(HHSConstants.KEY_SESSION_USER_NAME);
			String lsPassword = aoResourceRequest.getParameter(HHSConstants.PASSWORD);
			String lsCancelReason = aoResourceRequest.getParameter(HHSConstants.COMMENT_AREA);
			String lsContractId = aoResourceRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW);
			String lsAmendContractId = aoResourceRequest.getParameter(HHSConstants.AMEND_CONTRACT_ID_WORKFLOW);
			String lsBaseContractId = lsContractId;
			PortletSession loSession = aoResourceRequest.getPortletSession();
			P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			Map loValidateMap = validateUser(lsUserEmailId, lsPassword, aoResourceRequest);
			lbUserValidation = (Boolean) loValidateMap.get(HHSConstants.IS_VALID_USER);
			if (!lbUserValidation)
			{
				lsMsg = (String) loValidateMap.get(HHSConstants.ERROR_MESSAGE);
			}
			else
			{
				loChannel = new Channel();
				Map loHashMap = new HashMap();
				HashMap loContractMergeHashMap = new HashMap();
				loHashMap.put(HHSConstants.CLC_CONTRACT_ID_UNDERSCORE, lsContractId);
				loHashMap.put(HHSConstants.CLC_AMENDMENT_ID_UNDERSCORE, lsAmendContractId);
				List loList = new ArrayList();
				// Begin R6.3 QC5690
				//loList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						//HHSConstants.STATUS_CONTRACT_PENDING_NOTIFICATION));
				// End R6.3 QC5690
				loList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_CONTRACT_PENDING_REGISTARTION));
				loList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_CONTRACT_PENDING_CONFIGURATION));
				loList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_CONTRACT_PENDING_COF));
				loList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_CONTRACT_PENDING_SUBMISSION));
				loList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_CONTRACT_PENDING_APPROVAL));
				loList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_CONTRACT_SENT_FOR_REGISTRATION));
				loHashMap.put(HHSConstants.CLC_STATUS_ID, loList);
				loHashMap.put(HHSConstants.CL_CONTRACT_TYPE_ID, HHSConstants.TWO);
				loChannel.setData(HHSConstants.AO_CONTRACT_STATUS, loHashMap);
				loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.VALIDATE_AMEND_CONTRACT);
				lbStatus = (Boolean) loChannel.getData(HHSConstants.AB_STATUS);
				if (!lbStatus)
				{
					HHSTransactionManager.executeTransaction(loChannel,
							HHSConstants.CANCELLING_NEGATIVE_AMENDMENT_CHECK);
					HashMap loHashMapErrorCheck = (HashMap) loChannel.getData(HHSConstants.AO_HASH_MAP);
					if (loHashMapErrorCheck.isEmpty())
					{
						boolean lbLaunchWorkFlowStatus = false;
						ContractList loContractListBean = new ContractList();
						populateBeanForCancelAmend(aoResourceRequest, loChannel, lsCancelReason, lsContractId,
								lsAmendContractId, loContractListBean);
						Map loContractMap = ContractListUtils.selectContractAmendmentId(loChannel);
						// Amendment Contract id from map
						lsContractId = String.valueOf(loContractMap.get(HHSConstants.CONTRACT_ID_UNDERSCORE));
						String lsContractAmount = String.valueOf(loContractMap.get(HHSConstants.CLC_CONTRACT_AMOUNT));
						String lsContractStatus = String.valueOf(loContractMap.get(HHSConstants.STATUS));
						int lsBudgetCount = Integer.parseInt(String.valueOf(loContractMap
								.get(HHSConstants.BUDGET_COUNT)));
						loContractListBean.setContractId(lsContractId);
						loContractListBean.setAmendAmount(lsContractAmount);
						loContractListBean.setContractStatus(lsContractStatus);
						HashMap loWorkFlowTerminateMap = new HashMap();
						HashMap loBaseContractTerminateMap = new HashMap();
						loWorkFlowTerminateMap.put(HHSConstants.PROPERTY_PE_CONTRACT_ID, lsContractId);
						loBaseContractTerminateMap.put(HHSConstants.PROPERTY_PE_CONTRACT_ID, lsBaseContractId);
						if (loContractListBean.getAmendAmount() != null
								&& loContractListBean.getAmendAmount().indexOf(HHSConstants.HYPHEN) != -1
								&& lsContractStatus != null
								&& (lsContractStatus.equalsIgnoreCase(PropertyLoader.getProperty(
										HHSConstants.PROPERTIES_STATUS_CONSTANT,
										HHSConstants.STATUS_CONTRACT_PENDING_REGISTARTION))
										|| lsContractStatus.equalsIgnoreCase(PropertyLoader.getProperty(
												HHSConstants.PROPERTIES_STATUS_CONSTANT,
												HHSConstants.STATUS_CONTRACT_PENDING_CONFIGURATION))
										|| lsContractStatus.equalsIgnoreCase(PropertyLoader.getProperty(
												HHSConstants.PROPERTIES_STATUS_CONSTANT,
												HHSConstants.STATUS_CONTRACT_PENDING_COF))
										|| lsContractStatus.equalsIgnoreCase(PropertyLoader.getProperty(
												HHSConstants.PROPERTIES_STATUS_CONSTANT,
												HHSConstants.STATUS_CONTRACT_PENDING_SUBMISSION))
										|| lsContractStatus.equalsIgnoreCase(PropertyLoader.getProperty(
												HHSConstants.PROPERTIES_STATUS_CONSTANT,
												HHSConstants.STATUS_CONTRACT_PENDING_APPROVAL)) || lsContractStatus
											.equalsIgnoreCase(PropertyLoader.getProperty(
													HHSConstants.PROPERTIES_STATUS_CONSTANT,
													HHSConstants.STATUS_CONTRACT_SENT_FOR_REGISTRATION)))
								&& lsBudgetCount > 0)
						{
							lbLaunchWorkFlowStatus = true;
						}
						HashMap loHmRequiredProps = new HashMap();
						Map loUserHashMap = new HashMap();
						loUserHashMap.put(HHSConstants.MODIFY_BY, loContractListBean.getModifyBy());
						loUserHashMap.put(HHSConstants.PROPERTY_PE_CONTRACT_ID, lsBaseContractId);
						loHmRequiredProps
								.put(HHSConstants.WORKFLOW_NAME, HHSConstants.WF_CONTRACT_CONFIGURATION_UPDATE);
						loHmRequiredProps.put(HHSConstants.CONTRACT_ID_WORKFLOW, lsBaseContractId);
						loHmRequiredProps.put(HHSConstants.SUBMITTED_BY, loContractListBean.getModifyBy());
						loContractMergeHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, lsContractId);
						loContractMergeHashMap.put(HHSConstants.SUBMITTED_BY, loContractListBean.getModifyBy());
						loChannel.setData(HHSConstants.LB_AUTH_STATUS_FLAG, lbLaunchWorkFlowStatus);
						loChannel.setData(HHSConstants.AO_HMWF_REQUIRED_PROPS, loHmRequiredProps);
						loChannel.setData(HHSConstants.AO_WORKFLOW_TERMINATE_MAP, loWorkFlowTerminateMap);
						loChannel.setData(HHSConstants.AO_BASE_CONTRACT_WF_TERMINATE_MAP, loBaseContractTerminateMap);
						loChannel.setData(HHSConstants.AO_CONTRACT_MERGE_HASHMAP, loContractMergeHashMap);
						loChannel.setData(HHSConstants.AO_USER_HASH_MAP, loUserHashMap);

						HHSTransactionManager.executeTransaction(loChannel, HHSConstants.CANCEL_AMENDMENT);
						aoResourceRequest.getPortletSession().setAttribute(
								HHSConstants.SUCCESS_MESSAGE,
								PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
										HHSConstants.SUCCESS_CANCEL_AMENDMENT_MSG), PortletSession.APPLICATION_SCOPE);
					}
					else
					{
						lsMsg = (String) loHashMapErrorCheck.get(HHSConstants.ERROR_MESSAGE);
					}
				}
				else
				{
					lsMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSConstants.PENDING_REGISTRATION_AMEND_EXISTS);
				}
			}
		}
		catch (ApplicationException aoExe)
		{
			lsMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("ApplicationException occured in actionCancelAmendment", aoExe);
		}
		catch (Exception aoExe)
		{
			lsMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("Exception occured in actionCancelAmendment", aoExe);
		}
		finally
		{
			catchTaskError(loOut, lsMsg);
		}
		return loModelAndView;
	}

	/**
	 * This method is used to download the Amendment document for a contract.
	 * 
	 * <ul>
	 * <li>Executes the transaction 'downloadAmendmentDocument'</li>
	 * <li>Method Added in R4</li>
	 * </ul>
	 * @param aoResourceRequestForAmendDoc ResourceRequest Object
	 * @param aoResourceResponseForAmendDoc ResourceResponse Object
	 * 
	 * 
	 */

	@SuppressWarnings("rawtypes")
	@ResourceMapping("downloadAmendmentDocument")
	protected void downloadAmendmentDocument(ResourceRequest aoResourceRequestForAmendDoc,
			ResourceResponse aoResourceResponseForAmendDoc)
	{
		String lsMsg = HHSConstants.EMPTY_STRING;
		PrintWriter loOutForAmendDoc = null;
		ContractList loContractFilterBean = new ContractList();
		aoResourceResponseForAmendDoc.setContentType(HHSConstants.TEXT_HTML);
		String lsContractId = (String) aoResourceRequestForAmendDoc.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW);
		String lsContractTitle = (String) aoResourceRequestForAmendDoc.getParameter(HHSConstants.BUDGET_CONTRACT_TITLE);
		String lsProviderOrgId = (String) aoResourceRequestForAmendDoc.getParameter(HHSConstants.PROVIDER_ORG_ID_AMEND);
		String lsRealpath = aoResourceRequestForAmendDoc.getPortletSession().getPortletContext()
				.getRealPath(HHSR5Constants.DBD_DOC_REAL_PATH);
		String lsUserId = (String) aoResourceRequestForAmendDoc.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
		String lsOrgName = (String) aoResourceRequestForAmendDoc.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_NAME, PortletSession.APPLICATION_SCOPE);
		String lsZipFilePath = null;
		Boolean lsIsFinacialDocRequired = true;

		try
		{
			lsOrgName = lsOrgName.replace(HHSConstants.STRING_BACKSLASH, HHSConstants.EMPTY_STRING);
			P8UserSession loUserSession = (P8UserSession) aoResourceRequestForAmendDoc.getPortletSession()
					.getAttribute(ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			loContractFilterBean.setContractId(lsContractId);
			loContractFilterBean.setContractStatusId(PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_CONTRACT_SENT_FOR_REGISTRATION));
			List<String> loContractStatusList = new ArrayList<String>();
			loContractStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_CONTRACT_PENDING_REGISTARTION));
			loContractStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_CONTRACT_PENDING_APPROVAL));
			loContractFilterBean.setContractStatusList(loContractStatusList);
			Channel loChannelForAmendDoc = new Channel();
			loChannelForAmendDoc.setData(HHSConstants.CONTRACT_FILTER_BEAN, loContractFilterBean);
			loChannelForAmendDoc.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
			loChannelForAmendDoc.setData(HHSConstants.AO_OUTPUT_PATH, lsRealpath);
			loChannelForAmendDoc.setData(HHSConstants.CONTRACT_ID_KEY, lsContractId);
			loChannelForAmendDoc.setData(HHSConstants.IS_FINANCIAL, lsIsFinacialDocRequired);
			HashMap<String, String> loRequiredParamMapForAmendDoc = new HashMap<String, String>();
			loRequiredParamMapForAmendDoc
					.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, ApplicationConstants.EMPTY_STRING);
			loRequiredParamMapForAmendDoc.put(P8Constants.PROPERTY_CE_MIME_TYPE, ApplicationConstants.EMPTY_STRING);
			loRequiredParamMapForAmendDoc.put(HHSConstants.FILE_TYPE, ApplicationConstants.EMPTY_STRING);
			loRequiredParamMapForAmendDoc.put(P8Constants.PROPERTY_CE_CONTRACT_ID, ApplicationConstants.EMPTY_STRING);
			loRequiredParamMapForAmendDoc
					.put(P8Constants.PROPERTY_CE_CONTRACT_TITLE, ApplicationConstants.EMPTY_STRING);
			loRequiredParamMapForAmendDoc.put(P8Constants.PROPERTY_CE_DOCUMENT_ID, ApplicationConstants.EMPTY_STRING);
			loChannelForAmendDoc.setData(ApplicationConstants.REQ_PROPS_DOCUMENT, loRequiredParamMapForAmendDoc);
			loChannelForAmendDoc.setData(HHSConstants.ORG_NAME, lsOrgName);
			loChannelForAmendDoc.setData(HHSConstants.PROVIDER_ORG_ID, lsProviderOrgId);
			loChannelForAmendDoc.setData(HHSConstants.FOLDER_NAME, HHSConstants.FORWARD_SLASH + lsProviderOrgId
					+ HHSConstants.AMENDMENT_DOCUMENTS);
			loChannelForAmendDoc.setData(HHSConstants.BUDGET_CONTRACT_TITLE, lsContractTitle);
			loChannelForAmendDoc.setData(
					HHSConstants.USER_ORG_ID,
					(String) aoResourceRequestForAmendDoc.getPortletSession().getAttribute(
							ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE));
			loChannelForAmendDoc.setData(HHSConstants.USER_ID_2, lsUserId);
			loChannelForAmendDoc.setData(
					HHSConstants.FINANCIAL_PDF_DOC_PATH,
					PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
							P8Constants.PREDEFINED_FOLDER_PATH_FINANCIAL_DOC)
							+ HHSConstants.FORWARD_SLASH
							+ lsContractId);
			HHSTransactionManager.executeTransaction(loChannelForAmendDoc, HHSConstants.PDF_AMENDMENT_DOC_GENERATED);
			Boolean lbDocsGenerated = (Boolean) loChannelForAmendDoc.getData(HHSConstants.LO_SUCCESS);
			if (lbDocsGenerated)
			{
				HHSTransactionManager
						.executeTransaction(loChannelForAmendDoc, HHSConstants.DOWNLOAD_AMENDMENT_DOCUMENT);
				lsZipFilePath = (String) loChannelForAmendDoc.getData(HHSConstants.ZIP_PATH);
				HashMap loAmendMap = (HashMap) loChannelForAmendDoc.getData(HHSConstants.AMENDMENT_MAP);
				lsMsg = (String) loAmendMap.get(HHSConstants.ERROR_MESSAGE);
				if (lsZipFilePath == null)
				{
					lsMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
				}
			}
			else
			{
				lsMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.DOCS_NOT_GENERATED_ERROR_MSG);
			}
			loOutForAmendDoc = aoResourceResponseForAmendDoc.getWriter();
		}
		catch (ApplicationException aoExe)
		{
			lsMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("ApplicationException occured in downloading pdf on downloadAmendmentDocument", aoExe);
		}
		catch (Exception aoExe)
		{
			lsMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("ApplicationException occured in downloading pdf on downloadAmendmentDocument", aoExe);
		}
		finally
		{
			showDownloadDialogOrErrorForZip(aoResourceResponseForAmendDoc, lsMsg, lsZipFilePath, loOutForAmendDoc);
		}

	}

	/**
	 * This method populate the bean for cancel amendment<br>
	 * The method added in R4.
	 * @param aoResourceRequest ResourceRequest Object
	 * @param aoChannel Channel object
	 * @param asCancelReason - reason for cancel Amendment
	 * @param asContractId - Contract Id
	 * @param lsAmendContractId - Amendment Contract Id
	 * @param aoContractListBean ContractList Bean Object
	 * @throws ApplicationException
	 * 
	 * 
	 */
	private void populateBeanForCancelAmend(ResourceRequest aoResourceRequest, Channel aoChannel,
			String asCancelReason, String asContractId, String lsAmendContractId, ContractList aoContractListBean)
			throws ApplicationException
	{
		aoContractListBean.setAmendReason(asCancelReason);
		aoContractListBean.setContractId(lsAmendContractId);
		aoContractListBean.setContractTypeId(HHSConstants.TWO);
		String lsUser = (String) aoResourceRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
		aoContractListBean.setModifyBy(lsUser);
		aoContractListBean.setContractStatusId(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_CONTRACT_CANCELLED));
		aoContractListBean.setBudgetStatusId(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_BUDGET_CANCELLED));
		aoContractListBean.setTaskStatus(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_TASK_CANCELLED));
		aoContractListBean.setProcStatusIds(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_TASK_COMPLETE));
		String lsOrgnizationType = (String) aoResourceRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
		aoContractListBean.setOrgType(lsOrgnizationType);
		aoContractListBean.setBudgetTypeId(HHSConstants.ONE);
		aoChannel.setData(HHSConstants.AO_CONTRACT_BEAN, aoContractListBean);
		aoChannel.setData(HHSConstants.LB_AUTH_STATUS_FLAG, true);
	}

	/**
	 * This method is used to display the Amend Contract page populated with
	 * Epin details
	 * <ul>
	 * <li>1.Get all the fields value from database based on epin passed</li>
	 * <li>2.Populate the jsp with the bean values and display the jsp</li>
	 * <ul>
	 * 
	 * @param aoResourceRequest Resource Request Object
	 * @param aoResourceResponse Resource Response Object
	 */
	@ResourceMapping("populateAmendContractPage")
	public ModelAndView populateAmendContractPage(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		ModelAndView loModelAndView = new ModelAndView(HHSConstants.CLC_AMEND_CONTRACT);
		String lsEpin = (String) aoResourceRequest.getParameter(HHSConstants.EPIN_VALUE);
		String lsContracId = (String) aoResourceRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW);
		// R6: Getting base contract's agency Id
		String lsContractAgencyId = (String) aoResourceRequest.getParameter(HHSConstants.CONTRACT_AGENCY_ID);
		EPinDetailBean loEPinDetailBeanForAmend = new EPinDetailBean();
		loEPinDetailBeanForAmend.setContractId(lsContracId);
		loEPinDetailBeanForAmend.setEpinId(lsEpin);
		loEPinDetailBeanForAmend.setContractTypeId(HHSConstants.TWO);
		// R6: Setting the base contract agencyId in render parameter
		loEPinDetailBeanForAmend.setAgencyName(lsContractAgencyId);
		aoResourceRequest.setAttribute(HHSConstants.EPIN_BEAN_DETAILS, loEPinDetailBeanForAmend);
		String lsError = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
		Channel loChannel = new Channel();
		try
		{
			loChannel.setData(HHSConstants.LO_CONTRACT_DETAILS, loEPinDetailBeanForAmend);
			if (loEPinDetailBeanForAmend.getEpinId() != null
					&& !loEPinDetailBeanForAmend.getEpinId().equalsIgnoreCase(HHSConstants.EMPTY_STRING))
			{
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_CONTRACT_DETAILS_BY_EPIN);
			}
			loEPinDetailBeanForAmend = (EPinDetailBean) loChannel.getData(HHSConstants.AO_CONTRACT_DETAIL);
			if (loEPinDetailBeanForAmend != null)
			{
				loEPinDetailBeanForAmend.setContractId(lsContracId);
				// R6: Setting the base contract agencyId and contract type for
				// amendment in render parameter start
				loEPinDetailBeanForAmend.setAgencyName(lsContractAgencyId);
				loEPinDetailBeanForAmend.setContractTypeId(HHSConstants.TWO);
				// R6: Setting the base contract agencyId in render parameter
				// end
				aoResourceRequest.setAttribute(HHSConstants.EPIN_BEAN_DETAILS, loEPinDetailBeanForAmend);

				/*[Start] R9.5.0 QC9657	  **/
				Integer loProcDescSize =  null;
				if( loEPinDetailBeanForAmend.getProcDescription() != null ){ 
					loProcDescSize = new Integer(loEPinDetailBeanForAmend.getProcDescription().length()) ;
				} else { loProcDescSize = 0 ; }
				PortletSession loPortletSession = aoResourceRequest.getPortletSession();
				loPortletSession.setAttribute(HHSConstants.APT_PROCUREMENT_DESC_LENGTH, loProcDescSize , PortletSession.APPLICATION_SCOPE);
				LOG_OBJECT.Info("APT_PROCUREMENT_DESC is " + loEPinDetailBeanForAmend.getProcDescription()  );
				/*[End] R9.5.0 QC9657	  **/

				lsError = HHSConstants.EMPTY_STRING;
			}
			else
			{
				lsError = HHSConstants.EPIN_ERROR;
			}
		}
		catch (ApplicationException aoExe)
		{
			LOG_OBJECT.Error("ApplicationException occured in populateAmendContractPage", aoExe);

		}
		catch (Exception aoExe)
		{
			LOG_OBJECT.Error("Exception occured in populateAmendContractPage", aoExe);
		}
		aoResourceRequest.setAttribute(HHSConstants.CLC_CAP_ERROR, lsError);
		return loModelAndView;

	}

	/**
	 * The method has been updated for Release 6 for new Epin format validation
	 * This method validate the data for Contract Amendment against business
	 * rules 1. Epin validation calls the transaction 'validateEpin'
	 * @param aoChannel Channel object
	 * @param aoEPinDetailBean EPinDetailBean object
	 * @param aoResourceRequest ResourceRequest object
	 * @param aoResourceResponse ResourceResponse object
	 * @return HashMap<String, Object> loHMResult
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	private HashMap<String, Object> validateAmendContractRequest(Channel aoChannel, EPinDetailBean aoEPinDetailBean,
			ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse) throws ApplicationException
	{
		/* Start QC 9122 R 8.1 Do not allow Negative Amendment if Contract is not Registered yet */
		HashMap<String, Object> loHMResult = new HashMap<String, Object>();
		LOG_OBJECT.Debug("Base ContractId :: "+aoEPinDetailBean.getContractId());
		aoChannel.setData(HHSConstants.CONTRACT_ID_KEY, aoEPinDetailBean.getContractId());
		HHSTransactionManager.executeTransaction(aoChannel, HHSConstants.FETCH_BASE_CONTRACT_STATUS_ID);
		Integer baseStatusId = (Integer) aoChannel.getData("status_id");
		LOG_OBJECT.Debug("Base Contract Status :: "+baseStatusId);
		Float amendAmount = Float.valueOf(HHSUtil.formatAmount(aoResourceRequest.getParameter(HHSConstants.AMEND_VALUE)));
		if(baseStatusId != 62 && amendAmount < 0)
		{
			loHMResult.put(HHSConstants.ERROR_CODE, HHSConstants.INT_ONE);
			loHMResult.put(HHSConstants.CLC_ERROR_MSG, PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.NEGATIVE_AMENDMENT_PROHIBITED));
			return loHMResult;
		}
		
		if(baseStatusId != 62 && amendAmount == 0)
		{
			loHMResult.put(HHSConstants.ERROR_CODE, HHSConstants.INT_ONE);
			loHMResult.put(HHSConstants.CLC_ERROR_MSG, PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.ZERO_AMENDMENT_PROHIBITED));
			return loHMResult;
		}
		/* End QC 9122 R 8.1 Do not allow Negative Amendment if Contract is not Registered yet */
				
		aoChannel.setData(HHSConstants.AS_EPIN, aoEPinDetailBean.getEpinId());
		aoChannel.setData(HHSConstants.AO_CONTRACT_DETAILS_BY_EPIN, aoEPinDetailBean);
		/* R6: Validation changed for epin uniqueness as part of release 6 */
		boolean lbIsEpinValid = ContractListUtils.validateEpinUnique(aoEPinDetailBean);
		if (!lbIsEpinValid)
		{
			// set error to be shown if Epin selected is already
			// being used by another contract
			loHMResult.put(HHSConstants.ERROR_CODE, HHSConstants.INT_ONE);
			loHMResult.put(HHSConstants.CLC_ERROR_MSG, PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSR5Constants.EPIN_ALREADY_USE));
		}
		else
		{
			aoChannel.setData(HHSConstants.AS_VENDOR_FMS_ID, aoEPinDetailBean.getVendorFmsId());
			if (!ContractListUtils.validateProviderAccelerator(aoChannel))
			{
				loHMResult.put(HHSConstants.ERROR_CODE, HHSConstants.INT_ONE);
				loHMResult.put(HHSConstants.CLC_ERROR_MSG, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.PROVIDER_IN_ACCELERATOR_AMEND));
			}
			else if (isContractDatesValid(aoEPinDetailBean, loHMResult))
			{
				// set channel data for submitting for adding new contract
				aoChannel.setData(HHSConstants.AS_CONTRACT_AMOUNT,
						HHSUtil.formatAmount(aoResourceRequest.getParameter(HHSConstants.BMC_CONTRACT_VALUE)));
				aoChannel.setData(HHSConstants.AS_NEW_CONTRACT_AMOUNT,
						HHSUtil.formatAmount(aoResourceRequest.getParameter(HHSConstants.NEW_TOTAL_AMOUNT)));
				aoChannel.setData(HHSConstants.AS_AMENDMENT_AMOUNT,
						HHSUtil.formatAmount(aoResourceRequest.getParameter(HHSConstants.AMEND_VALUE)));
				HashMap loErrorCheckRule = null;
				aoChannel.setData(HHSConstants.CONTRACT_ID_KEY,
						aoResourceRequest.getParameter(HHSConstants.HDN_CONTRACT_ID));
				loErrorCheckRule = ContractListUtils.getAmendErrorCheck(aoChannel);
				loHMResult = setErrorAmendContractMsg(loErrorCheckRule);
			}

		}
		return loHMResult;
	}

	/**
	 * R6: This method calls transaction validateEpinIsUnique which returns true
	 * if epin is not associated to this agency Since for amendment we are
	 * getting agencyId in agencyDiv, we are setting agencydiv in agencyid for
	 * new bean
	 * @param aoEPinDetailBean EPinDetailBean object containing Epin Id and and
	 *            division
	 * @return boolean value showing Epin is duplicae or not
	 * @throws ApplicationException
	 */
	private boolean checkDuplicacyForAmendmentEpin(final EPinDetailBean aoEPinDetailBean) throws ApplicationException
	{
		EPinDetailBean loEPinDetailBean = new EPinDetailBean();
		loEPinDetailBean.setAgencyId(aoEPinDetailBean.getAgencyDiv());
		loEPinDetailBean.setEpinId(aoEPinDetailBean.getEpinId());
		return ContractListUtils.validateEpinUnique(loEPinDetailBean);
	}

	/**
	 * Changed method - By: Siddharth Bhola, Enhancement id: 5707 Replaced
	 * amendment end date by proposed contract end date to restrict shrinkage of
	 * proposed contract end date such that budget for that particular year is
	 * not created
	 * 
	 * This method responsible for validating the Dates entries (Contract Start
	 * Date,Contract (Proposed)End Date, Amendment Start Date,Amendment End
	 * Date)
	 * <ul>
	 * <li>Check for Business Rules on Dates Entries</li>
	 * <ul>
	 * 
	 * calls the transaction 'fetchCountractBudgetCountForFY'
	 * 
	 * @param aoEPinDetailBean EPinDetailBean Object
	 * @param aoHMResult HashMap<String, Object> Object
	 * @return Boolean loIsContractDatesValid
	 * @throws ApplicationException
	 */
	private Boolean isContractDatesValid(EPinDetailBean aoEPinDetailBean, HashMap<String, Object> aoHMResult)
			throws ApplicationException
	{
		Boolean loIsContractDatesValid = Boolean.TRUE;
		Channel loChannelObj = new Channel();

		Date loContractStartDate = DateUtil.getDate(aoEPinDetailBean.getContractStart());
		Date loContractEndDate = DateUtil.getDate(aoEPinDetailBean.getProposedContractEnd());
		Date loAmendmentStartDate = DateUtil.getDate(aoEPinDetailBean.getAmendmentStart());
		Date loAmendmentEndDate = DateUtil.getDate(aoEPinDetailBean.getAmendmentEnd());
		
		Date loOriginalContractEndDate = DateUtil.getDate(aoEPinDetailBean.getContractEnd());

		// Build 2.6.1, enhancement id: 5707 passing proposed contract dates
		HashMap<String, Integer> loProposedFyDetails = HHSUtil.getFirstAndLastFYOfContract(loContractStartDate,
				loContractEndDate);
		Integer ProposedEndFiscalYr = loProposedFyDetails.get(HHSConstants.CONTRACT_END_FY);

		if (loContractStartDate.compareTo(loContractEndDate) >= HHSConstants.INT_ZERO)
		{
			// Error throw for contract end date greater than contract start
			// date
			aoHMResult.put(HHSConstants.ERROR_CODE, HHSConstants.INT_ONE);
			aoHMResult.put(HHSConstants.CLC_ERROR_MSG, PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.MSG_INVALID_CONTRACT_START_END_DATES));

			loIsContractDatesValid = Boolean.FALSE;
			return loIsContractDatesValid;
		}
		if (loAmendmentStartDate.compareTo(loAmendmentEndDate) >= HHSConstants.INT_ZERO)
		{
			// Error throw for amendment end date greater than amendment start
			// date
			aoHMResult.put(HHSConstants.ERROR_CODE, HHSConstants.INT_ONE);
			aoHMResult.put(HHSConstants.CLC_ERROR_MSG, PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.MSG_INVALID_AMENDMENT_START_END_DATES));
			loIsContractDatesValid = Boolean.FALSE;
			return loIsContractDatesValid;
		}
		if (loContractEndDate.compareTo(loAmendmentEndDate) < HHSConstants.INT_ZERO)
		{
			// Error throw for contract end date earlier than amendment end date
			aoHMResult.put(HHSConstants.ERROR_CODE, HHSConstants.INT_ONE);
			aoHMResult.put(HHSConstants.CLC_ERROR_MSG, PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.MSG_INVALID_AMENDMENT_END_DATE_NEW));
			loIsContractDatesValid = Boolean.FALSE;
			return loIsContractDatesValid;
		}
		//Start R8.4.0 qc_8537 $0 Amendments do not appear in Amendment CoF task list after configuration
		// for $0 Amendment cannot shorten the term of the contract.
		if ((aoEPinDetailBean.getAmendValue() !=null && Float.valueOf(aoEPinDetailBean.getAmendValue()) == 0 ) && loAmendmentEndDate.compareTo(loOriginalContractEndDate) < HHSConstants.INT_ZERO)
		{
			// Error throw for contract end date earlier than amendment end date
			aoHMResult.put(HHSConstants.ERROR_CODE, HHSConstants.INT_ONE);
			aoHMResult.put(HHSConstants.CLC_ERROR_MSG, PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.MSG_INVALID_ZERO_DOLLAR_AMENDMENT_END_DATE));
			loIsContractDatesValid = Boolean.FALSE;
			return loIsContractDatesValid;
		}
		//End R8.4.0 qc_8537 $0 Amendments do not appear in Amendment CoF task list after configuration
		

		// Check if the Proposed End FY has not missed a Budget already
		// configured
		loChannelObj.setData(HHSConstants.CONTRACT_ID_KEY, aoEPinDetailBean.getContractId());
		loChannelObj.setData(HHSConstants.FISCAL_YEAR_ID_KEY, Integer.valueOf(ProposedEndFiscalYr + 1).toString());
		HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.FETCH_COUNTRACT_BUDGET_COUNT_FOR_FY);
		Integer loReturnedBudgetsCount = (Integer) loChannelObj.getData(HHSConstants.AO_BUDGETS_COUNT);

		if (loReturnedBudgetsCount > HHSConstants.INT_ZERO)
		{
			// Error throw for First Fiscal Year change in Amendment span while
			// amendment
			aoHMResult.put(HHSConstants.ERROR_CODE, HHSConstants.INT_ONE);
			aoHMResult.put(HHSConstants.CLC_ERROR_MSG, PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.MSG_INVALID_CONTRACT_END_DATE));
			loIsContractDatesValid = Boolean.FALSE;
			return loIsContractDatesValid;
		}

		return loIsContractDatesValid;
	}

	/**
	 * This method decide the execution flow on click of Amend Contract Button<br>
	 * This method Added in R4. The method is modified for enhancement 5707 as
	 * part of release 2.6.1
	 * <ul>
	 * <li>Validate if contract and amendment start and end date fall under
	 * expected span</li>
	 * <li>Validate if selected contract already has Amendment record</li>
	 * <li>Insert a new contract record (with Amendment status) into the
	 * database</li>
	 * <li>Trigger WF WF313</li>
	 * <li>Set the < Contract Status> = Pending Registration - in case of
	 * negative amendment</li>
	 * <li>Get the status of the insertion</li>
	 * <li>if status is false show error on the Overlay</li>
	 * <li>If status is true close the overlay.</li>
	 * </ul>
	 * 
	 * calls the transaction 'amendContractDetails'
	 * 
	 * @param aoResourceRequest to get screen parameters and next action to be
	 *            performed
	 * @param aoResourceResponse decides the next execution flow
	 * 
	 */
	@ResourceMapping("amendContractSubmit")
	protected void amendContractSubmit(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		// Declare local variables/objects and initialize them to default values
		EPinDetailBean loEPinDetailBean = new EPinDetailBean();
		Channel loChannel = null;
		String lsMsg = HHSConstants.EMPTY_STRING;
		int liErrorCode = HHSConstants.INT_ZERO;
		HashMap<String, Object> loHMResult = null;
		loChannel = new Channel();
		String lsReviewLevelNotSetError = null;

		try
		{
			lsReviewLevelNotSetError = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
					HHSConstants.CB_REVIEW_LEVEL_ERROR);

			PortletSession loPortletSession = aoResourceRequest.getPortletSession();
			P8UserSession loUserSession = (P8UserSession) loPortletSession.getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
			populateEpinBeanFromRequest(aoResourceRequest, loEPinDetailBean);

			/*[Start] R9.5.0 QC9615  **/
			LOG_OBJECT.Info("APT_PROCUREMENT_DESC  is " + loEPinDetailBean.getProcDescription()   + "\n   procDescription" +  aoResourceRequest.getParameter(HHSConstants.APT_PROCUREMENT_DESC_AMEDMENT)   );
			loEPinDetailBean.setProcDescription( aoResourceRequest.getParameter(HHSConstants.APT_PROCUREMENT_DESC_AMEDMENT)  );
			if( loEPinDetailBean.getProcDescription() != null ){
				LOG_OBJECT.Info("APT_PROCUREMENT_DESC size is " + loEPinDetailBean.getProcDescription().length()  + " Before escapeHTML" );
			}else {
				LOG_OBJECT.Info("APT_PROCUREMENT_DESC size is 0 (null) Before escapeHTML" );
			}
			Integer loProcDescSize = (Integer) loPortletSession.getAttribute(HHSConstants.APT_PROCUREMENT_DESC_LENGTH,  PortletSession.APPLICATION_SCOPE);
			if( loProcDescSize.intValue() > HHSConstants.APT_PROCUREMENT_DESC_MAX_LENGTH  )
			loEPinDetailBean.setProcDescription( StringEscapeUtils.escapeHtml(loEPinDetailBean.getProcDescription() ) );
			if( loEPinDetailBean.getProcDescription() != null  ){
				LOG_OBJECT.Info("APT_PROCUREMENT_DESC size is " + loEPinDetailBean.getProcDescription().length()  + " After escapeHTML" );
			}else {
				LOG_OBJECT.Info("APT_PROCUREMENT_DESC size is 0 (null) After escapeHTML" );
			}
			/*[End] R9.5.0 QC9615 	  **/

			loEPinDetailBean.setPrevStatusId(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_CONTRACT_REGISTERED));
			loEPinDetailBean.setContractValue(HHSUtil.formatAmount(aoResourceRequest
					.getParameter(HHSConstants.BMC_CONTRACT_VALUE)));
			loEPinDetailBean.setAmendValue(HHSUtil.formatAmount(aoResourceRequest
					.getParameter(HHSConstants.AMEND_VALUE)));

			// Set Start and End dates of Amendment
			loEPinDetailBean.setAmendmentStart(aoResourceRequest.getParameter(HHSConstants.AMENDMENT_START));

			/*[Start] R9.5.0 QC9657	  **/
			String loConSrtDate =  aoResourceRequest.getParameter(HHSConstants.CONTRACT_START_DATE); 
			String loAmdSrtDate =  loEPinDetailBean.getAmendmentStart(); 
			Date conDate = new SimpleDateFormat( HHSConstants.MMDDYYFORMAT ).parse(loConSrtDate );
			Date amdDate = new SimpleDateFormat( HHSConstants.MMDDYYFORMAT ).parse(loAmdSrtDate);
			if ( amdDate.before(conDate) ){
				throw new ApplicationException("[Error] Amendment start date mustn't be before Contract Start date!");  
			}
			/*[End] R9.5.0 QC9657	**/

			loEPinDetailBean.setAmendmentEnd(aoResourceRequest.getParameter(HHSConstants.AMENDMENT_END));
			loEPinDetailBean.setProposedContractEnd(aoResourceRequest.getParameter(HHSConstants.PROPOSED_CONTRACT_END));

			loEPinDetailBean.setContractTypeId(HHSConstants.TWO);
			// R6: Setting base contract agencyId instead of epin agency id
			loEPinDetailBean.setAgencyId(aoResourceRequest.getParameter(HHSConstants.CONTRACT_AGENCY_ID));
			loEPinDetailBean.setAmendmentReason(aoResourceRequest.getParameter(HHSConstants.AMENDMENT_REASON));
			loEPinDetailBean.setContractTitle(aoResourceRequest.getParameter(HHSConstants.AMENDMENT_TITLE));
			loEPinDetailBean.setStatusId(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_CONTRACT_PENDING_CONFIGURATION));

			loEPinDetailBean.setContractEnd(aoResourceRequest.getParameter(HHSConstants.ORIGINAL_CONTRACT_END_DATE));
			// below 2 flags are added as fixed for enhancement 5707 as part of
			// release 2.6.1
			boolean lbfiscalYearSpanFlag = HHSUtil.checkContractFiscalYearsSpan(loEPinDetailBean.getContractStart(),
					loEPinDetailBean.getProposedContractEnd());
			boolean lbAmendmentfiscalYearSpanFlag = HHSUtil.checkContractFiscalYearsSpan(
					loEPinDetailBean.getAmendmentStart(), loEPinDetailBean.getAmendmentEnd());
			if (!lbfiscalYearSpanFlag && !lbAmendmentfiscalYearSpanFlag)
			{
				// Validate the inputs for Amend Contract
				loHMResult = validateAmendContractRequest(loChannel, loEPinDetailBean, aoResourceRequest,
						aoResourceResponse);

				// If validated good for Amendment
				if (Integer.valueOf(loHMResult.get(HHSConstants.ERROR_CODE).toString()).equals(HHSConstants.INT_ZERO))
				{

					loChannel.setData(HHSConstants.LB_AUTH_STATUS_FLAG, true);
					loChannel.setData(HHSConstants.AO_CONTRACT_DETAILS_BY_EPIN, loEPinDetailBean);
					HHSTransactionManager.executeTransaction(loChannel, HHSConstants.AMEND_CONTRACT_DETAILS);
					// set error message to 0 for Success
					liErrorCode = HHSConstants.INT_ZERO;
					lsMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSConstants.AMEND_CONTRACT_SUCCESS);
					aoResourceRequest.getPortletSession().setAttribute(HHSConstants.SUCCESS_MESSAGE, lsMsg,
							PortletSession.APPLICATION_SCOPE);
				}
				else
				{
					// If not validated for amendment, set the appropriate
					// message
					// and return
					liErrorCode = Integer.valueOf(loHMResult.get(HHSConstants.ERROR_CODE).toString());
					lsMsg = loHMResult.get(HHSConstants.CLC_ERROR_MSG).toString();
				}
			}
			else
			{
				liErrorCode = HHSConstants.INT_ONE;
				lsMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.ERROR_CONTRACT_TERM_EXCEED);
			}
		}
		catch (ApplicationException aoExe)
		{
			// For ApplicationException type exception, handle it and return the
			// appropriate message as resource result
			liErrorCode = HHSConstants.INT_ONE;

			if (null != lsReviewLevelNotSetError && aoExe.toString().contains(lsReviewLevelNotSetError))
			{
				lsMsg = lsReviewLevelNotSetError;
			}
			else
			{
				lsMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			}

			LOG_OBJECT.Error("ApplicationException occured in amendContract", aoExe);
		}
		catch (Exception aoExe)
		{
			// For Exception other than Application Exception, handle and return
			// the appropriate message as resource result
			liErrorCode = HHSConstants.INT_ONE;
			lsMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("Exception occured in amendContract", aoExe);
		}
		finally
		{
			// Render the JSON object with Error code and message
			setErrorMsgJSPForContractList(aoResourceResponse, lsMsg, liErrorCode);
		}
	}

	/**
	 * This method sets the error message if any for the Error Check business
	 * rule for Amend Contract request
	 * 
	 * <li>If error check rule gives success set error code 0 and success error
	 * message.</li> <li>Else set error code 2 and set error message returned
	 * from transaction.</li> </ul>
	 * @param aoErrorCheckRule Hashmap object contains error code
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	private HashMap<String, Object> setErrorAmendContractMsg(HashMap aoErrorCheckRule) throws ApplicationException
	{
		HashMap<String, Object> loHMResult = new HashMap<String, Object>();

		// IF ErrorCheckRule is not yet defined or set to null
		if (null != aoErrorCheckRule)
		{
			loHMResult.put(HHSConstants.CLC_ERROR_MSG, (String) aoErrorCheckRule.get(HHSConstants.CLC_ERROR_MSG));
			loHMResult.put(HHSConstants.ERROR_CODE, aoErrorCheckRule.get(HHSConstants.ERROR_CODE));
		}
		else
		{
			loHMResult.put(HHSConstants.CLC_ERROR_MSG, HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED);
			loHMResult.put(HHSConstants.ERROR_CODE, HHSConstants.INT_ONE);
		}

		return loHMResult;
	}

	/**
	 * This method print the error using print Writer object
	 * 
	 * @param aoResourceResponse ResourceResponse object
	 * @param aiErrorCode error code
	 * @param asErrorMsg error message
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
			LOG_OBJECT.Error("Exception occured while returning message", aoExe);
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

	// S382 screen start
	/**
	 * This method is used to render S382 screen After an Agency user has
	 * completed the S390 Contract Configuration Task, Agency users have the
	 * option to select 'View Contract Configuration' from the S306 Budget List.
	 * This contract configuration read-only version of the page displays both
	 * S390 Contract Configuration - Chart of Accounts Allocation grid and S390
	 * - Contract Configuration ? Contract Budget Setup grids into a single
	 * page.
	 * 
	 * <ul>
	 * <li>1. Used by Agency users, clicking on View contract configuration.</li>
	 * <li>1. Renders S382 contract configuration read only screen.</li>
	 * <li>Method Updated in R4</li>
	 *  <li>Method Updated in R7 for cost center</li>
	 * </ul>
	 * 
	 * @param aoRequest - RenderRequest object
	 * @param aoResponse - RenderResponse object
	 * @return - ModelAndView jsp to be rendered
	 * 
	 */
	@SuppressWarnings(
	{ "unchecked", "null" })
	@RenderMapping(params = "render_action=showContractConfigReadOnlyDetails")
	protected ModelAndView showContractConfigReadOnlyDetails(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		ModelAndView loModelAndView = null;
		String lsFormPath = HHSConstants.JSP_CONTRACT_CONFIG_READ_ONLY;
		Map<String, Object> loConfigMap = new HashMap<String, Object>();
		// Added in R7
		String lsServiceStatusFlag = null;
		try
		{
			ProcurementCOF loProcurementCOFBean = (ProcurementCOF) ApplicationSession.getAttribute(aoRequest, false,
					HHSConstants.LO_PROC_COF_BEAN);
			List<ContractBudgetBean> loConfiguredFiscalYrList = (List<ContractBudgetBean>) ApplicationSession
					.getAttribute(aoRequest, false, HHSConstants.CONFIGURED_FISCAL_YR_LIST);
			// Start - R4 Budget Customized
			CBGridBean loCBGridBean = (CBGridBean) PortletSessionHandler.getAttribute(aoRequest, true,
					HHSConstants.CBGRIDBEAN_IN_SESSION);
			// Added in R7
			HashMap loMap = new HashMap();
			loMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, loCBGridBean.getContractID());
			StringBuffer loTemp = new StringBuffer();
			for (int liNumber = 0; liNumber < loConfiguredFiscalYrList.size(); liNumber++)
			{
				List<String> loEntryTypeDetails = HHSUtil.getEntryTypeDetail(loCBGridBean.getContractID(),
						loConfiguredFiscalYrList.get(liNumber).getBudgetId(), null, null,
						loConfiguredFiscalYrList.get(liNumber).getBudgetfiscalYear());
				if (liNumber <= 0)
				{
					loTemp.append(loConfiguredFiscalYrList.get(liNumber).getBudgetfiscalYear()
							+ HHSConstants.UNDERSCORE + loEntryTypeDetails);
				}
				else
				{
					loTemp.append(HHSConstants.PIPE_LINE + loConfiguredFiscalYrList.get(liNumber).getBudgetfiscalYear()
							+ HHSConstants.UNDERSCORE + loEntryTypeDetails);
				}
				// Start - Added in R7 for Cost Center
				Channel loChannelObj = new Channel();
				loCBGridBean.setContractTypeId(HHSConstants.ONE);
				loCBGridBean.setContractBudgetID(loConfiguredFiscalYrList.get(liNumber).getBudgetId());
				loChannelObj.setData(HHSConstants.AO_HASH_MAP, loMap);
				loChannelObj.setData(HHSConstants.CB_GRID_BEAN_OBJ, loCBGridBean);
				// transaction executed in loop for multiple budgets
				HHSTransactionManager.executeTransaction(loChannelObj, HHSR5Constants.FETCH_COST_CENTER_READ_ONLY_DATA,
						HHSR5Constants.TRANSACTION_ELEMENT_R5);
				lsServiceStatusFlag = (String) loChannelObj.getData(HHSR5Constants.SELECTION_FLAG);
				HashMap loServicesMap = (HashMap) loChannelObj.getData(HHSR5Constants.AO_SERVICES_MAP);
				loConfiguredFiscalYrList.get(liNumber).setServicesBudgetDetails(loServicesMap);
				// END - Added in R7 for Cost Center
			}
			loConfigMap.put(HHSConstants.ENTRY_TYPE_LIST, loTemp);
			// End - R4 Budget Customized
			loConfigMap.put(HHSConstants.MAP_KEY_PROC_COF_BEAN, loProcurementCOFBean);
			loConfigMap.put(HHSConstants.MAP_KEY_CONF_FISCAL_YR_LIST, loConfiguredFiscalYrList);
			aoRequest.setAttribute(HHSR5Constants.COST_CENTER_OPTED, lsServiceStatusFlag);
			if (null == loProcurementCOFBean)
			{
				throw new ApplicationException("Data not fetched properly");
			}
		}
		catch (ApplicationException loAppEx)
		{
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_STATUS, HHSConstants.AS_FAILED);
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_MSG, HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED);
			LOG_OBJECT.Error("Error occured Model and view Exception while processing"
					+ " S382 Contract configuration read only details", loAppEx);
		}
		catch (Exception aoExp)
		{
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_STATUS, HHSConstants.AS_FAILED);
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_MSG, HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED);
			LOG_OBJECT.Error("Error occured Model and view Exception while processing"
					+ " S382 Contract configuration read only details", aoExp);
		}
		loModelAndView = new ModelAndView(lsFormPath, loConfigMap);
		return loModelAndView;
	}

	/**
	 * This method is used to After an Agency user has completed the S390
	 * Contract Configuration Task, Agency users have the option to select 'View
	 * Contract Configuration' from the S306 ? Budget List. Below steps are
	 * performed when users has selected 'View Contract Configuration'
	 * <ul>
	 * <li>1. Used by Agency users, clicking on View contract configuration.</li>
	 * <li>1. fetch contract configuration details.</li>
	 * <li>2. fetch contract budget setup grid.</li>
	 * <li>3. Fetch chart of accounts allocation grid.</li>
	 * </ul>
	 * 
	 * calls the transaction 'getContractConfigDetails'
	 * 
	 * @param aoRequest - ActionRequest object
	 * @param aoResponse - ActionResponse object
	 */
	@SuppressWarnings("unchecked")
	@ActionMapping(params = "submit_action=fetchContractConfigReadOnlyDetails")
	protected void fetchContractConfigReadOnlyDetails(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		String lsContractId = aoRequest.getParameter(HHSConstants.HIDDEN_HDNCONTRACTID);
		String lsContractAmt = aoRequest.getParameter(HHSConstants.HIDDEN_HDNCONTRACTAMT);
		String lsContStartDt = aoRequest.getParameter(HHSConstants.HIDDEN_HDNCONTRACTSTARTDT);
		String lsContEndDt = aoRequest.getParameter(HHSConstants.HIDDEN_HDNCONTRACTENDDT);
		ProcurementCOF loProcurementCOFBean = null;
		try
		{
			String lsContractStartDt = DateUtil.getDateMMddYYYYFormat(DateUtil.getUnParseDateFormat(lsContStartDt));
			String lsContractEndDT = DateUtil.getDateMMddYYYYFormat(DateUtil.getUnParseDateFormat(lsContEndDt));
			Channel loChannelObj = new Channel();
			loChannelObj.setData(HHSConstants.CONTRACT_ID_KEY, lsContractId);
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.TRANSACTION_GETCONTRACTCONFIGDETAILS);
			loProcurementCOFBean = (ProcurementCOF) loChannelObj.getData(HHSConstants.AO_PROCUREMENTCOFBEAN);
			List<ContractBudgetBean> loContractBudgetBeanList = (List<ContractBudgetBean>) loChannelObj
					.getData(HHSConstants.AO_RETURNED_GRID_LIST);
			List<ContractBudgetBean> loConfiguredFiscalYrList = new ArrayList<ContractBudgetBean>();
			// get count of budgets configured
			if (null != loContractBudgetBeanList)
			{
				Set<ContractBudgetBean> loContractBudgetSet = new LinkedHashSet<ContractBudgetBean>();
				loContractBudgetSet.addAll(loContractBudgetBeanList);
				loConfiguredFiscalYrList.addAll(loContractBudgetSet);
				Integer loConfiguredBudgetCount = loContractBudgetSet.size();
				loProcurementCOFBean.setTotConfiguredBudget(loConfiguredBudgetCount);
			}
			else
			{
				loProcurementCOFBean.setTotConfiguredBudget(0);
			}
			loProcurementCOFBean.setContractValue(lsContractAmt);
			loProcurementCOFBean.setContractStartDate(lsContractStartDt);
			loProcurementCOFBean.setContractEndDate(lsContractEndDT);

			ApplicationSession.setAttribute(loProcurementCOFBean, aoRequest, HHSConstants.LO_PROC_COF_BEAN);
			ApplicationSession
					.setAttribute(loContractBudgetBeanList, aoRequest, HHSConstants.CONTRACT_BUDGET_BEAN_LIST);
			ApplicationSession
					.setAttribute(loConfiguredFiscalYrList, aoRequest, HHSConstants.CONFIGURED_FISCAL_YR_LIST);

			setContractDatesInSession(aoRequest.getPortletSession(), lsContractStartDt, lsContractEndDT);
			Map<String, Object> loFiscalYrMap = getContractFiscalYears(aoRequest);
			int liFiscalStartYr = (Integer) loFiscalYrMap.get(HHSConstants.LI_START_YEAR);

			CBGridBean loCBGridBean = new CBGridBean();
			loCBGridBean.setContractID(lsContractId);
			loCBGridBean.setFiscalYearID(String.valueOf(liFiscalStartYr));
			String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			ContractListUtils.setModifiedBy(loCBGridBean, lsUserOrgType, lsUserId);
			PortletSessionHandler.setAttribute(loCBGridBean, aoRequest, HHSConstants.CBGRIDBEAN_IN_SESSION);
			setAccountGridDataInSession(aoRequest);
		}
		catch (ApplicationException loAppEx)
		{
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_STATUS, HHSConstants.AS_FAILED);
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_MSG, HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED);
			LOG_OBJECT.Error("Error occured while processing S382 Contract configuration read only details", loAppEx);
		}
		catch (Exception loEx)
		{
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_STATUS, HHSConstants.AS_FAILED);
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_MSG, HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED);
			LOG_OBJECT.Error("Error occured while processing S382 Contract configuration read only details", loEx);

		}
		aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION,
				HHSConstants.RENDER_ACTION_SHOWCONTRACTCONFIGREADONLYDETAILS);
	}

	/**
	 * This method is modified as part of Production Support release (2.6.0) for
	 * defect 5608
	 * 
	 * <ul>
	 * <li>Changes done to remove Page Parse Error</li>
	 * </ul>
	 * 
	 * This method is almost similar to loadGrid method in BaseController. It
	 * gets called to populate grid data for sub budget grid in S382 screen
	 * 
	 * <ul>
	 * <li>1. This method gets called n times where n is number of budgets
	 * configured for a contract</li>
	 * <li>2. Each time it calls fetchAllSubBudgetNames which returns names of
	 * all sub-budgets/programs that are under a particular budget for a fiscal
	 * year</li>
	 * <li>3. Further it calls populateSubGridRows which populate rows in grid</li>
	 * </ul>
	 * 
	 * @param aoResourceRequest - ResourceRequest object
	 * @param aoResourceResponse - ResourceResponse object
	 * @throws IOException - IOException
	 */
	@SuppressWarnings("unchecked")
	@ResourceMapping("loadSubBudgetGridData")
	public void loadSubBudgetGridData(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws IOException
	{
		try
		{
			String lsGridFY = aoResourceRequest.getParameter(HHSConstants.FISCAL_YEAR_COUNTER);
			List<ContractBudgetBean> loReturnedGridList = (List<ContractBudgetBean>) fetchAllSubBudgetNames(
					aoResourceRequest, lsGridFY);
			String lsGridLabel = aoResourceRequest.getParameter(HHSConstants.GRID_LABEL);
			aoResourceResponse.setContentType(HHSConstants.APPLICATION_JSON);
			String lsAppSettingMapKey = HHSConstants.FINANCIAL_LIST_SCREEN + HHSConstants.UNDERSCORE
					+ HHSConstants.FINANCIAL_VIEW_PER_PAGE;
			HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb
					.getInstance().getCacheObject(ApplicationConstants.APPLICATION_SETTING);
			String lsRowsPerPage = loApplicationSettingMap.get(lsAppSettingMapKey);
			String lsPage = HHSConstants.ONE;
			String lsErrorMsg = HHSConstants.EMPTY_STRING;
			if (null != PortletSessionHandler.getAttribute(aoResourceRequest, true, HHSConstants.GRID_ERROR))
			{
				lsErrorMsg = (String) PortletSessionHandler.getAttribute(aoResourceRequest, false,
						HHSConstants.GRID_ERROR);
			}
			if (null != PortletSessionHandler.getAttribute(aoResourceRequest, true, HHSConstants.PAGINATION))
			{
				lsPage = (String) PortletSessionHandler.getAttribute(aoResourceRequest, false, HHSConstants.PAGINATION);
			}
			Object loBeanObj = new ContractBudgetBean();
			StringBuffer loBuffer = HHSUtil.populateSubGridRows(loBeanObj, loReturnedGridList, lsRowsPerPage, lsPage,
					lsErrorMsg, lsGridLabel);
			LOG_OBJECT.Debug("json for loadSubBudgetGridData: userid:: "
					+ (String) aoResourceRequest.getPortletSession().getAttribute(
							ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE) + "\n json:: "
					+ loBuffer.toString());
			// Below line is added as per defect 5608 for release 2.6.0
			aoResourceResponse.getWriter().print(loBuffer.toString().replaceAll("\\\\'", HHSR5Constants.STR));
		}
		catch (ApplicationException loAppEx)
		{
			aoResourceRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_STATUS, HHSConstants.AS_FAILED);
			aoResourceRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_MSG,
					HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED);
			LOG_OBJECT.Error("ApplicationException occured in loadSubBudgetGridData", loAppEx);
		}
		catch (Exception loEx)
		{
			aoResourceRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_STATUS, HHSConstants.AS_FAILED);
			aoResourceRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_MSG,
					HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED);
			LOG_OBJECT.Error("Exception occured in loadSubBudgetGridData", loEx);
		}
		finally
		{
			aoResourceResponse.getWriter().flush();
			aoResourceResponse.getWriter().close();
		}
	}

	/**
	 * This is private method which returns list of type ContractBudgetBean. It
	 * takes count which tells which year data needs to be populated It is
	 * called by loadSubBudgetGridData which is further called as an
	 * asynchronous calls from jsp With help of count fiscal year is determined
	 * whose grid needs to be populated on the screen
	 * 
	 * @param aoResourceRequest - ResourceRequest object
	 * @param asGridFY - states which year grid needs to be populated
	 * @return List<ContractBudgetBean> list containing all sub-budgets for a
	 *         fiscal year
	 * @throws ApplicationException ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	private List<ContractBudgetBean> fetchAllSubBudgetNames(ResourceRequest aoResourceRequest, String asGridFY)
			throws ApplicationException
	{
		int liGridFY = 0;
		List<ContractBudgetBean> loReturnedGridList = (List<ContractBudgetBean>) ApplicationSession.getAttribute(
				aoResourceRequest, true, HHSConstants.CONTRACT_BUDGET_BEAN_LIST);
		List<ContractBudgetBean> loContractBudgetBeanList = new ArrayList<ContractBudgetBean>();
		if (null != loReturnedGridList && !loReturnedGridList.isEmpty() && null != asGridFY)
		{
			liGridFY = Integer.parseInt(asGridFY);
			Iterator<ContractBudgetBean> loItr = loReturnedGridList.iterator();
			while (loItr.hasNext())
			{
				ContractBudgetBean loTempContractBudgetBean = (ContractBudgetBean) loItr.next();
				String lsTempFiscalYear = loTempContractBudgetBean.getBudgetfiscalYear();
				int liTempFiscalYear = Integer.parseInt(lsTempFiscalYear);
				if (liGridFY == liTempFiscalYear)
				{
					loContractBudgetBeanList.add(loTempContractBudgetBean);
					loItr.remove();
				}
			}
		}
		return loContractBudgetBeanList;
	}

	// S382 screen end

	/**
	 * This method is used to After an Agency/Accelator user has completed the
	 * S394 - Contract Certification of Funds Task and S395 - Amendment
	 * Certification of Funds Task, Agency users have the option to select 'View
	 * Contract CoF from the S301 Contract List (Accelerator/Agency).Below steps
	 * are performed when users has selected 'View Contract CoF'
	 * <ul>
	 * <li>1. Used by Agency/Accelator users, clicking on View Contract CoF.</li>
	 * <li>2. The user will be able to open a read only version of the Contract
	 * Certification of Funds.</li>
	 * <li>1. fetch contract configuration details.</li>
	 * <li>2. fetch contract budget setup grid.</li>
	 * <li>3. Fetch chart of accounts allocation grid.</li>
	 * <li>This method is added in R4</li>
	 * </ul>
	 * <ul>
	 * <li>Executes the transaction 'getBaseAmendmentContractDetails'</li>
	 * </ul>
	 * 
	 * 
	 * @param aoRequest - ActionRequest Object
	 * @param aoResponse - ActionResponse Object
	 * @throws ParseException
	 */
	@ActionMapping(params = "submit_action=showContractCOF")
	protected void showContractCOF(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		/*
		 * R3.9.0 6524 Due to the new required logic if renewing contract, this
		 * method is split into 2 parts; one for Amendment
		 * (amendmentContractCOF), the other for (renewedContractCOF)
		 */

		String lsContractTypeId = aoRequest.getParameter(HHSConstants.HIDDEN_HDNCONTRACTTYPEID);
		String lsContractId = aoRequest.getParameter(HHSConstants.HIDDEN_HDNCONTRACTID);
		PortletSession loPortletSession = aoRequest.getPortletSession();

		try
		{
			if (HHSConstants.CONTRACT_RENEWAL_TYPE_ID.equalsIgnoreCase(lsContractTypeId))
			{
				// contract renewal
				renewedContractCOF(aoRequest, aoResponse);

			}
			else
			{
				// Not contract renewal
				amendmentContractCOF(aoRequest, aoResponse);
			}
		}
		catch (ApplicationException loEx)
		{
			LOG_OBJECT.Error("Exception occured in contractConfigCOFDocFetch Transaction" + lsContractId, loEx);
			String lsErrorMsg = HHSConstants.ERROR_WHILE_PROCESSING_REQUEST;
			loPortletSession.setAttribute(HHSConstants.ERROR_MESSAGE_BUDGET_LIST, lsErrorMsg,
					PortletSession.APPLICATION_SCOPE);
			loPortletSession.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE, PortletSession.APPLICATION_SCOPE);

		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Exception occured while parsing Contract Start Date and End Date", loEx);
			String lsErrorMsg = HHSConstants.ERROR_WHILE_PROCESSING_REQUEST;
			loPortletSession.setAttribute(HHSConstants.ERROR_MESSAGE_BUDGET_LIST, lsErrorMsg,
					PortletSession.APPLICATION_SCOPE);
			loPortletSession.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE, PortletSession.APPLICATION_SCOPE);
		}
		aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.RENDER_CONTRACT_COF);

	}

	/**
	 * This method is used to After an Agency/Accelator user has completed the
	 * S394 for only contract renewal - Contract Certification of Funds Task ,
	 * Agency users have the option to select 'View Contract CoF' from the S301
	 * Contract List (Accelerator/Agency).Below steps are performed when users
	 * has selected 'View Contract CoF'
	 * 
	 * 
	 * @param aoRequest - ActionRequest Object
	 * @param aoResponse - ActionResponse Object
	 * @throws ParseException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	protected void renewedContractCOF(ActionRequest aoRequest, ActionResponse aoResponse) throws ApplicationException,
			Exception
	{
		String lsContractId = aoRequest.getParameter(HHSConstants.HIDDEN_HDNCONTRACTID);
		// PortletSession loPortletSession = aoRequest.getPortletSession();

		Channel loChanObj = new Channel();
		loChanObj.setData(HHSConstants.CONTRACT_ID_KEY, lsContractId);

		HHSTransactionManager.executeTransaction(loChanObj, HHSConstants.CONTRACT_RENEWAL_DETAIL_INFO);
		List<ContractCOFDetails> loContractCOFList = (List<ContractCOFDetails>) loChanObj
				.getData(HHSConstants.CONTRACT_COF_LIST_BEAN);

		List<ContractList> loContractList = (List<ContractList>) loChanObj.getData(HHSConstants.CONTRACT_LIST_BEAN);
		setContractDatesInSession(aoRequest.getPortletSession(), loContractCOFList.get(0).getContractStartDate(),
				loContractCOFList.get(0).getContract_endDate());
		List loHeaderList = setCOFAccountHeaderDataInSession(aoRequest);
		List<AccountsAllocationBean> loCOAList = conCOFDetailsToAccountsAllocationBean(loContractCOFList);
		ProcurementCOF loPprocureCof = conCOFDetailsToProcCOF(loContractCOFList);
		List<FundingAllocationBean> loFundingList = conCOFDetailsToFundingllocationBean(loContractCOFList);

		ApplicationSession.setAttribute(loCOAList, aoRequest, HHSConstants.CONTRACT_COF_COA_DETAILS); // AccountsAllocationBean
		ApplicationSession.setAttribute(loPprocureCof, aoRequest, HHSConstants.PROC_COF); // ProcurementCOF
		ApplicationSession.setAttribute(loContractList, aoRequest, HHSConstants.AWARD_EPIN_DROPDOWN); // <--
																										// not
																										// need
		ApplicationSession.setAttribute(loFundingList, aoRequest, HHSConstants.CONTRACT_COF_FUNDING_DETAILS); // FundingAllocationBean
		ApplicationSession.setAttribute(loHeaderList, aoRequest, HHSConstants.CONTRACT_COF_COA_HEADER);
	}

	/**
	 * This method is used for data extracting from ContractCOFDetails into
	 * AccountsAllocationBean when users has selected 'View Contract CoF'
	 * 
	 * 
	 * @param loContractList - List of ContractCOFDetails Object
	 * @return List of AccountsAllocationBean Object
	 */
	private List<AccountsAllocationBean> conCOFDetailsToAccountsAllocationBean(List<ContractCOFDetails> loContractList)
	{
		List<AccountsAllocationBean> loAAList = new ArrayList<AccountsAllocationBean>();
		Map<String, AccountsAllocationBean> loAAMap = new HashMap<String, AccountsAllocationBean>();

		for (int loop = 0; loop < loContractList.size(); loop++)
		{
			ContractCOFDetails loObj = loContractList.get(loop);

			AccountsAllocationBean loAcc = null;
			String loAccountId = loObj.getUnitOfAppropriation() + HHSConstants.HYPHEN + loObj.getBudgetCode()
					+ HHSConstants.HYPHEN + loObj.getObjectCode();
			String loAccountCharts = loAccountId + HHSConstants.HYPHEN + loObj.getSubObjectCode() + HHSConstants.HYPHEN
					+ loObj.getReportingCategory();
			if (loAAMap.containsKey(loAccountCharts))
			{
				loAcc = loAAMap.get(loAccountCharts);
			}
			else
			{
				loAcc = new AccountsAllocationBean();
				loAAMap.put(loAccountCharts, loAcc);
				loAAList.add(loAcc);
			}

			loAcc.setChartOfAccount(loAccountCharts);
			loAcc.setFyAmount(loop, loObj.getAmount());
		}

		return loAAList;
	}

	/**
	 * This method is used for data extracting from ContractCOFDetails into
	 * FundingAllocationBean when users has selected 'View Contract CoF'
	 * 
	 * 
	 * @param loContractList - List of ContractCOFDetails Object
	 * @return List of FundingAllocationBean Object
	 */
	private List<FundingAllocationBean> conCOFDetailsToFundingllocationBean(List<ContractCOFDetails> loContractList)
	{
		List<FundingAllocationBean> loAAList = new ArrayList<FundingAllocationBean>();
		Map<String, FundingAllocationBean> loAAMap = new HashMap<String, FundingAllocationBean>();

		loAAMap.put(HHSConstants.FEDERAL, new FundingAllocationBean());
		loAAMap.get(HHSConstants.FEDERAL).setFundingSource(HHSConstants.FEDERAL);
		loAAList.add(loAAMap.get(HHSConstants.FEDERAL));
		loAAMap.put(HHSConstants.CITY, new FundingAllocationBean());
		loAAMap.get(HHSConstants.CITY).setFundingSource(HHSConstants.CITY);
		loAAList.add(loAAMap.get(HHSConstants.CITY));
		loAAMap.put(HHSConstants.STATE, new FundingAllocationBean());
		loAAMap.get(HHSConstants.STATE).setFundingSource(HHSConstants.STATE);
		loAAList.add(loAAMap.get(HHSConstants.STATE));
		loAAMap.put(HHSConstants.OTHER, new FundingAllocationBean());
		loAAMap.get(HHSConstants.OTHER).setFundingSource(HHSConstants.OTHER);
		loAAList.add(loAAMap.get(HHSConstants.OTHER));
		return loAAList;
	}

	/**
	 * This method is used for data extracting from ContractCOFDetails into
	 * ProcurementCOF when users has selected 'View Contract CoF'
	 * 
	 * 
	 * @param loContractList - List of ContractCOFDetails Object
	 * @return List of ProcurementCOF Object
	 * @throws ApplicationException
	 */
	private ProcurementCOF conCOFDetailsToProcCOF(List<ContractCOFDetails> loContractList) throws ApplicationException,
			Exception
	{
		ProcurementCOF loPcofLst = new ProcurementCOF();
		// commenting for sonar changes Release 5.
		/**
		 * BigDecimal totalAmt = new BigDecimal(0);
		 * 
		 * for (ContractCOFDetails loObj : loContractList) {
		 * totalAmt.add(loObj.getContractAmount()); }
		 */
		loPcofLst = loContractList.get(0).toProcurementCOF();
		return loPcofLst;
	}

	/**
	 * This method is display certification of fund
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse object
	 * @throws ApplicationException
	 * @throws Exception
	 */
	protected void amendmentContractCOF(ActionRequest aoRequest, ActionResponse aoResponse)
			throws ApplicationException, Exception
	{
		String lsContractId = aoRequest.getParameter(HHSConstants.HIDDEN_HDNCONTRACTID);
		String lsAmendContractId = aoRequest.getParameter(HHSConstants.HIDDEN_HDNAMENDCONTRACTID);
		PortletSession loPortletSession = aoRequest.getPortletSession();

		Channel loChanObj = new Channel();
		loChanObj.setData(HHSConstants.CONTRACT_ID_KEY, lsContractId);
		HHSTransactionManager.executeTransaction(loChanObj, HHSConstants.GET_BASE_AMENDMENT_CONTRACT_DETAILS);
		List<ContractList> loContractList = (List<ContractList>) loChanObj.getData(HHSConstants.CONTRACT_LIST_BEAN);
		String lsBaseAwardEpin = (String) loChanObj.getData(HHSConstants.AO_BASE_AWARD_EPIN);
		ContractList loContract = null;
		if (lsAmendContractId != null && !lsAmendContractId.equalsIgnoreCase(HHSConstants.UNDEFINED)
				&& !lsAmendContractId.equalsIgnoreCase(HHSConstants.EMPTY_STRING))
		{
			for (ContractList loContractListBean : loContractList)
			{
				if (loContractListBean.getContractId().equalsIgnoreCase(lsAmendContractId))
				{
					loContract = loContractListBean;
					aoResponse.setRenderParameter(HHSConstants.PROPERTY_PE_AWARD_EPIN,
							loContractListBean.getAwardEpin());
					break;
				}
			}
		}
		else
		{
			if (!loContractList.get(0).getContractTypeId().equalsIgnoreCase(HHSConstants.TWO))
			{
				loContractList.get(0).setAwardEpin(lsBaseAwardEpin);
			}
			loContract = (ContractList) loContractList.get(0);
		}
		String lsStartDate = HHSR5Constants.EMPTY_STRING, lsEndDate = HHSR5Constants.EMPTY_STRING;
		if (null != loContract && null != loContract.getContractStartDate())
		{
			lsStartDate = DateUtil.getDateMMDDYYYYFormat(loContract.getContractStartDate());
		}
		if (null != loContract && null != loContract.getContractEndDate())
		{
			lsEndDate = DateUtil.getDateMMDDYYYYFormat(loContract.getContractEndDate());
		}
		setContractDatesInSession(aoRequest.getPortletSession(), lsStartDate, lsEndDate);
		List loHeaderList = setCOFAccountHeaderDataInSession(aoRequest);
		Map<String, Object> loFiscalYrMap = getContractFiscalYears(aoRequest);
		int liFiscalStartYr = (Integer) loFiscalYrMap.get(HHSConstants.LI_START_YEAR);
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setFiscalYearID(String.valueOf(liFiscalStartYr));
		Channel loChannelObj = new Channel();
		if (null != loContract && null != loContract.getContractId())
		{
			loCBGridBean.setContractID(loContract.getContractId());
			loChannelObj.setData(HHSConstants.CONTRACT_ID_KEY, loContract.getContractId());
		}
		loChannelObj.setData(HHSConstants.CB_GRID_BEAN_OBJ, loCBGridBean);
		HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.CONTRACT_CONFIG_DOC_FETCH);
		ProcurementCOF loPprocureCof = (ProcurementCOF) loChannelObj
				.getData(HHSConstants.BMC_CONTRACT_COF_TASK_DETAILS);
		ProcurementCOF loAmountDetails = (ProcurementCOF) loChannelObj
				.getData(HHSConstants.BASE_AMENDMENT_CONTRACT_AMOUNT_DETAILS);
		BigDecimal liNewContractValue = new BigDecimal(loAmountDetails.getContractValue()).add(new BigDecimal(
				loAmountDetails.getAmendedContractValue()));
		loPprocureCof.setContractValue(loAmountDetails.getContractValue());
		if (null != loAmountDetails.getProcurementTitle())
		{
			loPprocureCof.setProcurementTitle(loAmountDetails.getProcurementTitle());
		}
		loPprocureCof.setAmendedContractValue(loAmountDetails.getAmendedContractValue());
		loPprocureCof.setAmendmentStartDate(loAmountDetails.getAmendmentStartDate());
		loPprocureCof.setAmendmentEndDate(loAmountDetails.getAmendmentEndDate());
		loPprocureCof.setAmendmentEpin(loAmountDetails.getAmendmentEpin());
		loPprocureCof.setAmendmentTitle(loAmountDetails.getAmendmentTitle());
		loPprocureCof.setNewContractValue(String.valueOf(liNewContractValue));
		loPprocureCof.setAwardEpin(lsBaseAwardEpin);
		if (null != loContract)
		{
			if (null != loContract.getContractStartDate())
			{
				loPprocureCof.setContractStartDate(DateUtil.getDateMMDDYYYYFormat(loContract.getContractStartDate()));
			}
			if (null != loContract.getContractEndDate())
			{
				loPprocureCof.setContractEndDate(DateUtil.getDateMMDDYYYYFormat(loContract.getContractEndDate()));
			}
		}
		loPprocureCof.setAgencyName(HHSUtil.getAgencyName(loPprocureCof.getAgencyName()));
		List loCOAList = (List) loChannelObj.getData(HHSConstants.AO_RET_COA_LIST);
		List loFundingList = (List) loChannelObj.getData(HHSConstants.AO_RETURN_FUNDING_LIST);
		ApplicationSession.setAttribute(loContractList, aoRequest, HHSConstants.AWARD_EPIN_DROPDOWN);
		ApplicationSession.setAttribute(loHeaderList, aoRequest, HHSConstants.CONTRACT_COF_COA_HEADER);
		ApplicationSession.setAttribute(loCOAList, aoRequest, HHSConstants.CONTRACT_COF_COA_DETAILS);
		ApplicationSession.setAttribute(loFundingList, aoRequest, HHSConstants.CONTRACT_COF_FUNDING_DETAILS);
		ApplicationSession.setAttribute(loPprocureCof, aoRequest, HHSConstants.PROC_COF);

	}

	/**
	 * This method gets executed from R2 flow fron default render method.
	 * 
	 * calls the transaction 'getBaseAmendmentContractDetails'
	 * 
	 * calls the transaction 'contractConfigCOFDocFetch'
	 * @param aoRequest RenderRequest object
	 * @throws ApplicationException
	 * @throws NumberFormatException
	 */
	private void showContractCOFPage(RenderRequest aoRequest) throws ApplicationException, NumberFormatException
	{
		String lsContractId = (String) PortalUtil.parseQueryString(aoRequest, HHSConstants.HIDDEN_HDNCONTRACTID);
		Channel loChanObj = new Channel();
		loChanObj.setData(HHSConstants.CONTRACT_ID_KEY, lsContractId);
		HHSTransactionManager.executeTransaction(loChanObj, HHSConstants.GET_BASE_AMENDMENT_CONTRACT_DETAILS);
		List<ContractList> loContractList = (List<ContractList>) loChanObj.getData(HHSConstants.CONTRACT_LIST_BEAN);
		String lsBaseAwardEpin = (String) loChanObj.getData(HHSConstants.AO_BASE_AWARD_EPIN);
		loContractList.get(0).setAwardEpin(lsBaseAwardEpin);
		ContractList loContract = (ContractList) loContractList.get(0);
		String lsContractStartDt = DateUtil.getDateMMDDYYYYFormat(loContract.getContractStartDate());
		String lsContractEndDT = DateUtil.getDateMMDDYYYYFormat(loContract.getContractEndDate());
		setContractDatesInSession(aoRequest.getPortletSession(), lsContractStartDt, lsContractEndDT);
		List loHeaderList = setCOFAccountHeaderDataInSession(aoRequest);
		Map<String, Object> loFiscalYrMap = getContractFiscalYears(aoRequest);
		int liFiscalStartYr = (Integer) loFiscalYrMap.get(HHSConstants.LI_START_YEAR);

		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setContractID(loContract.getContractId());
		loCBGridBean.setFiscalYearID(String.valueOf(liFiscalStartYr));
		Channel loChannelObj = new Channel();
		loChannelObj.setData(HHSConstants.CONTRACT_ID_KEY, loContract.getContractId());
		loChannelObj.setData(HHSConstants.CB_GRID_BEAN_OBJ, loCBGridBean);

		HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.CONTRACT_CONFIG_DOC_FETCH);
		ProcurementCOF loPprocureCof = (ProcurementCOF) loChannelObj
				.getData(HHSConstants.BMC_CONTRACT_COF_TASK_DETAILS);
		ProcurementCOF loAmountDetails = (ProcurementCOF) loChannelObj
				.getData(HHSConstants.BASE_AMENDMENT_CONTRACT_AMOUNT_DETAILS);
		BigDecimal liNewContractValue = new BigDecimal(loAmountDetails.getContractValue()).add(new BigDecimal(
				loAmountDetails.getAmendedContractValue()));
		loPprocureCof.setContractValue(loAmountDetails.getContractValue());
		loPprocureCof.setAmendedContractValue(loAmountDetails.getAmendedContractValue());
		loPprocureCof.setNewContractValue(String.valueOf(liNewContractValue));
		loPprocureCof.setAwardEpin(lsBaseAwardEpin);
		loPprocureCof.setContractStartDate(lsContractStartDt);
		loPprocureCof.setContractEndDate(lsContractEndDT);
		loPprocureCof.setAgencyName(HHSUtil.getAgencyName(loPprocureCof.getAgencyName()));
		List loCOAList = (List) loChannelObj.getData(HHSConstants.AO_RET_COA_LIST);
		List loFundingList = (List) loChannelObj.getData(HHSConstants.AO_RETURN_FUNDING_LIST);
		ApplicationSession.setAttribute(loContractList, aoRequest, HHSConstants.AWARD_EPIN_DROPDOWN);
		ApplicationSession.setAttribute(loHeaderList, aoRequest, HHSConstants.CONTRACT_COF_COA_HEADER);
		ApplicationSession.setAttribute(loCOAList, aoRequest, HHSConstants.CONTRACT_COF_COA_DETAILS);
		ApplicationSession.setAttribute(loFundingList, aoRequest, HHSConstants.CONTRACT_COF_FUNDING_DETAILS);
		ApplicationSession.setAttribute(loPprocureCof, aoRequest, HHSConstants.PROC_COF);
	}

	/**
	 * This method is used to render S383 screen After an Agency/Accelator user
	 * has select 'View Contract CoF' from the Contract List. This The Contract
	 * Certification of Funds Document is a read only version of S394 - Contract
	 * Certification of Funds Task .
	 * 
	 * @param aoRequest - RenderRequest
	 * @return - ModelAndView jsp to be rendered
	 */
	@SuppressWarnings("rawtypes")
	@RenderMapping(params = "render_action=renderContractCOF")
	protected ModelAndView renderContractCOF(RenderRequest aoRequest)
	{
		ModelAndView loModelAndView = null;

		List loHeaderList = (List) ApplicationSession.getAttribute(aoRequest, HHSConstants.CONTRACT_COF_COA_HEADER);
		List loDetailsList = (List) ApplicationSession.getAttribute(aoRequest, HHSConstants.CONTRACT_COF_COA_DETAILS);
		List loFundingList = (List) ApplicationSession.getAttribute(aoRequest,
				HHSConstants.CONTRACT_COF_FUNDING_DETAILS);
		List loDropDownDetailsList = (List) ApplicationSession
				.getAttribute(aoRequest, HHSConstants.AWARD_EPIN_DROPDOWN);
		ProcurementCOF loPprocureCof = (ProcurementCOF) ApplicationSession.getAttribute(aoRequest,
				HHSConstants.PROC_COF);
		aoRequest.setAttribute(HHSConstants.DETAIL_LIST, loDetailsList);
		aoRequest.setAttribute(HHSConstants.HEADER_LIST, loHeaderList);
		aoRequest.setAttribute(HHSConstants.FUNDING_LIST, loFundingList);
		aoRequest.setAttribute(HHSConstants.PROC_COF, loPprocureCof);
		aoRequest.setAttribute(HHSConstants.AWARD_EPIN_DROPDOWN, loDropDownDetailsList);
		aoRequest.setAttribute(HHSConstants.PROPERTY_PE_AWARD_EPIN,
				aoRequest.getParameter(HHSConstants.PROPERTY_PE_AWARD_EPIN));
		loModelAndView = new ModelAndView(HHSConstants.CONTRACT_CERT_OF_FUNDS_DOC);
		return loModelAndView;
	}

	/**
	 * This resource mapping is called when the user selects the award Epin from
	 * Drop Down Option on Contract Certification Of Funds Doc Screen.
	 * 
	 * <ul>
	 * <li>Executes the transaction 'contractConfigCOFDocResourseFetch'</li>
	 * <li>This method is added in R4</li>
	 * </ul>
	 * @param aoResourceRequest ResourceRequest object
	 * @param aoResourceResponse ResourceResponse object
	 * @return model and view of the requested jsp page
	 * @throws ApplicationException ApplicationException object
	 * 
	 */
	@SuppressWarnings("unchecked")
	@ResourceMapping("viewContractCOF")
	protected ModelAndView viewContractCOF(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws ApplicationException
	{
		PortletSession loPortletSession = aoResourceRequest.getPortletSession();
		ModelAndView loModelAndView = new ModelAndView(HHSConstants.CONTRACT_COF_RESOURCE_DOC);
		String lsSelectDropDownVal = aoResourceRequest.getParameter(HHSConstants.SELECT_VAL);
		String[] loTokens = lsSelectDropDownVal.split(HHSConstants.HYPHEN);
		List<String> loTokenList = Arrays.asList(loTokens);
		String lsContractId = loTokenList.get(0);
		String lsContractStartDt = loTokenList.get(1);
		String lsContractEndDT = loTokenList.get(2);
		lsContractStartDt = DateUtil.getDateMMddYYYYFormat(DateUtil.getUnParseDateFormat(lsContractStartDt));
		lsContractEndDT = DateUtil.getDateMMddYYYYFormat(DateUtil.getUnParseDateFormat(lsContractEndDT));
		try
		{
			setContractDatesInSession(aoResourceRequest.getPortletSession(), lsContractStartDt, lsContractEndDT);
			List loHeaderList = setCOFAccountHeaderDataInSession(aoResourceRequest);
			Map<String, Object> loFiscalYrMap = getContractFiscalYears(aoResourceRequest);
			int liFiscalStartYr = (Integer) loFiscalYrMap.get(HHSConstants.LI_START_YEAR);

			CBGridBean loCBGridBean = new CBGridBean();
			loCBGridBean.setContractID(lsContractId);
			loCBGridBean.setFiscalYearID(String.valueOf(liFiscalStartYr));
			Channel loChannelObj = new Channel();
			loChannelObj.setData(HHSConstants.CONTRACT_ID_KEY, lsContractId);
			loChannelObj.setData(HHSConstants.CB_GRID_BEAN_OBJ, loCBGridBean);

			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.CONTRACT_CONFIG_COF_DOC_RESOURSE_FETCH);
			ProcurementCOF loPprocureCof = (ProcurementCOF) loChannelObj
					.getData(HHSConstants.BMC_CONTRACT_COF_TASK_DETAILS);
			ProcurementCOF loAmountDetails = (ProcurementCOF) loChannelObj
					.getData(HHSConstants.BASE_AMENDMENT_CONTRACT_AMOUNT_DETAILS);
			BigDecimal loNewContractValue = new BigDecimal(loAmountDetails.getContractValue()).add(new BigDecimal(
					loAmountDetails.getAmendedContractValue()));
			loPprocureCof.setAwardEpin(aoResourceRequest.getParameter(HHSConstants.BASE_AWARD_EPIN));
			loPprocureCof.setContractValue(loAmountDetails.getContractValue());
			loPprocureCof.setAmendedContractValue(loAmountDetails.getAmendedContractValue());
			loPprocureCof.setNewContractValue(String.valueOf(loNewContractValue));
			loPprocureCof.setAgencyName(HHSUtil.getAgencyName(loPprocureCof.getAgencyName()));
			loPprocureCof.setContractStartDate(lsContractStartDt);
			loPprocureCof.setContractEndDate(lsContractEndDT);
			if (null != loAmountDetails.getProcurementTitle())
			{
				loPprocureCof.setProcurementTitle(loAmountDetails.getProcurementTitle());
			}
			loPprocureCof.setAmendedContractValue(loAmountDetails.getAmendedContractValue());
			loPprocureCof.setAmendmentStartDate(loAmountDetails.getAmendmentStartDate());
			loPprocureCof.setAmendmentEndDate(loAmountDetails.getAmendmentEndDate());
			loPprocureCof.setAmendmentEpin(loAmountDetails.getAmendmentEpin());
			loPprocureCof.setAmendmentTitle(loAmountDetails.getAmendmentTitle());
			List loCOAList = (List) loChannelObj.getData(HHSConstants.AO_RET_COA_LIST);
			List loFundingList = (List) loChannelObj.getData(HHSConstants.AO_RETURN_FUNDING_LIST);
			aoResourceRequest.setAttribute(HHSConstants.DETAIL_LIST, loCOAList);
			aoResourceRequest.setAttribute(HHSConstants.HEADER_LIST, loHeaderList);
			aoResourceRequest.setAttribute(HHSConstants.FUNDING_LIST, loFundingList);
			aoResourceRequest.setAttribute(HHSConstants.PROC_COF, loPprocureCof);

		}
		catch (ApplicationException loEx)
		{
			LOG_OBJECT
					.Error("Exception occured in contractConfigCOFDocResourseFetch Transaction" + loEx + lsContractId);
			String lsErrorMsg = HHSConstants.ERROR_WHILE_PROCESSING_REQUEST;
			loPortletSession.setAttribute(HHSConstants.ERROR_MESSAGE_BUDGET_LIST, lsErrorMsg,
					PortletSession.APPLICATION_SCOPE);
			loPortletSession.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE, PortletSession.APPLICATION_SCOPE);
		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Exception occured while parsing Contract Start Date and End Date", loEx);
			String lsErrorMsg = HHSConstants.ERROR_WHILE_PROCESSING_REQUEST;
			loPortletSession.setAttribute(HHSConstants.ERROR_MESSAGE_BUDGET_LIST, lsErrorMsg,
					PortletSession.APPLICATION_SCOPE);
			loPortletSession.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE, PortletSession.APPLICATION_SCOPE);
		}
		return loModelAndView;

	}

	/**
	 * This method is to refersh jsp so that status and messages are updated.
	 * <ul>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * @param aoRequest request as input
	 * @param aoResponse response as input
	 * @return loModelAndView view of jsp is returned.
	 * 
	 */
	@RenderMapping(params = "duplicate_render_amendment=duplicateRenderAmendment")
	protected ModelAndView handleDuplicateRenderAmendment(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		ModelAndView loModelAndView = new ModelAndView(HHSConstants.AMENDMENT_LIST);
		try
		{
			loModelAndView = renderAmendmentList(aoRequest, HHSConstants.NEXT_PAGE_PARAM,
					HHSConstants.AMENDED_CONTRACT_SESSION_BEAN);
		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Error occured in contract list screen ", aoAppExp);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, ApplicationConstants.ERROR_MESSAGE_FILENET_DOWN);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error occured in contract list screen ", aoExp);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, ApplicationConstants.ERROR_MESSAGE_FILENET_DOWN);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
		return loModelAndView;
	}

	/**
	 * This is the default render method for amend list.
	 * <ul>
	 * <li>Executes transaction 'getAmendmentList'</li>
	 * <li>This method was added in R4.</li>
	 * </ul>
	 * @param aoRequest request as input
	 * @param asOverlayPageParam String Object
	 * @param asOverLay String Object
	 * @return ModelAndView object as output
	 * @throws ApplicationException Exception in case of code failure.
	 * 
	 * 
	 */
	@SuppressWarnings("unchecked")
	@RenderMapping(params = "next_action=amendment")
	protected ModelAndView renderAmendmentList(RenderRequest aoRequest, String asOverlayPageParam, String asOverLay)
			throws ApplicationException
	{
		PortletSession loPortletSession = aoRequest.getPortletSession();
		boolean lbFirstLoad = false;
		String lsProviderId = null;
		String lsOrgnizationType = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
				PortletSession.APPLICATION_SCOPE);
		String lsUserOrg = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
				PortletSession.APPLICATION_SCOPE);
		String lsRole = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ROLE,
				PortletSession.APPLICATION_SCOPE);
		List<ProgramNameInfo> loProgramNameList = (List<ProgramNameInfo>) loPortletSession.getAttribute(
				HHSConstants.PROGRAM_NAME_LIST, PortletSession.APPLICATION_SCOPE);
		List<HashMap<String, String>> loAgencyDetails = (List<HashMap<String, String>>) loPortletSession.getAttribute(
				HHSConstants.AGENCY_DETAILS, PortletSession.APPLICATION_SCOPE);
		Channel loChannelObj = new Channel();
		loChannelObj.setData(HHSConstants.ORGTYPE, lsOrgnizationType);
		String lsNextPage = null;
		ContractList loContractFilterBean = null;
		String lbRequestFromHomePage = HHSConstants.FALSE;
		try
		{
			if (null != PortalUtil.parseQueryString(aoRequest, HHSConstants.FROM_HOME_PAGE))
			{
				lbRequestFromHomePage = PortalUtil.parseQueryString(aoRequest, HHSConstants.FROM_HOME_PAGE);
			}

			if (asOverlayPageParam != null && asOverLay != null)
			{
				lsNextPage = (String) ApplicationSession.getAttribute(aoRequest, asOverlayPageParam);
				loContractFilterBean = (ContractList) ApplicationSession.getAttribute(aoRequest, true, asOverLay);
			}
			else
			{
				lsNextPage = (String) ApplicationSession.getAttribute(aoRequest, HHSConstants.NEXT_PAGE_PARAM);
				loContractFilterBean = (ContractList) ApplicationSession.getAttribute(aoRequest, true,
						HHSConstants.AMENDED_CONTRACT_SESSION_BEAN);
				removeOtherScreenBeanFromSession(loPortletSession);
				loPortletSession.removeAttribute(HHSConstants.CONTRACT_SESSION_BEAN);
			}
			if (null != lsOrgnizationType && lsOrgnizationType.equals(ApplicationConstants.PROVIDER_ORG)
					&& !lbRequestFromHomePage.equals(HHSConstants.FALSE))
			{
				loContractFilterBean = getFinancialBeanForProviderHomePages(aoRequest, loContractFilterBean);
			}

			else if (null != lsOrgnizationType
					&& (lsOrgnizationType.equals(ApplicationConstants.CITY_ORG) || lsOrgnizationType
							.equals(ApplicationConstants.AGENCY_ORG))
					&& !lbRequestFromHomePage.equals(HHSConstants.FALSE))
			{
				loContractFilterBean = getFinancialBeanForCityAndAgencyHomePages(aoRequest, loContractFilterBean);
			}
			if (null == loContractFilterBean)
			{
				lbFirstLoad = true;
				loContractFilterBean = setDefaultAmendmentBean(lsOrgnizationType);
				loContractFilterBean.setSecondSort(HHSConstants.AWARD_MODIFIED_DATE);
			}
			lsProviderId = (String) ApplicationSession.getAttribute(aoRequest, HHSConstants.PROVIDER_ID);
			if (null != lsProviderId)
			{
				loContractFilterBean.setProviderId(lsProviderId);
			}
			loContractFilterBean.setOrgName(lsUserOrg);
			loContractFilterBean.setOrgType(lsOrgnizationType);
			String lsViewAmendment = (String) ApplicationSession.getAttribute(aoRequest,
					HHSConstants.HIDDEN_IS_VIEW_AMENDMENT);
			String lsContractId = (String) ApplicationSession
					.getAttribute(aoRequest, HHSConstants.HIDDEN_HDNCONTRACTID);
			if (lsContractId != null && !lsContractId.equalsIgnoreCase(HHSR5Constants.UNDEFINED)
					&& lsViewAmendment != null && lsViewAmendment.equalsIgnoreCase(ApplicationConstants.TRUE))
			{
				loContractFilterBean.setContractId(lsContractId);
			}
			// Start : R5 Added
			// Added for Emergency Build 4.0.0.1 defect 8360
			Boolean lbFromFinancialTab = Boolean.valueOf(aoRequest.getParameter(HHSR5Constants.FROM_FINANCIAL_TAB));
			if (null != lbFromFinancialTab && lbFromFinancialTab)
			{
				// Updated for Emergency Build 4.0.0.2 defect 8377
				removeRedirectFromListSessionData(loPortletSession);
			}
			String lsContractIdForContractList = (String) loPortletSession.getAttribute(
					HHSR5Constants.CONTRACT_ID_FOR_LIST, PortletSession.APPLICATION_SCOPE);
			if (StringUtils.isNotBlank(lsContractIdForContractList))
			{
				loContractFilterBean.setContractId(lsContractIdForContractList);
			}
			// Made changes for Emergency Build 4.0.0.3 defect 8397 and 8313
			String lsAmendContractIdForContractList = (String) loPortletSession.getAttribute(
					HHSR5Constants.AMEND_CONTRACT_ID_WORKFLOW, PortletSession.APPLICATION_SCOPE);
			if (StringUtils.isNotBlank(lsAmendContractIdForContractList))
			{
				loContractFilterBean.setAmendmentContractId(lsAmendContractIdForContractList);
			}
			// End : R5 Added
			String lsStatusId = (String) ApplicationSession.getAttribute(aoRequest, HHSConstants.HIDDEN_HDNSTATUSID);
			if (lsStatusId != null
					&& lsStatusId.equalsIgnoreCase(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
							HHSConstants.STATUS_CONTRACT_SUSPENDED)))
			{
				loContractFilterBean.setSuspended(true);
			}
			String lsContractTitle = null;
			String lsBaseContractTitle = null;
			if (loContractFilterBean.getContractTitle() != null)
			{
				lsContractTitle = loContractFilterBean.getContractTitle();
				loContractFilterBean.setContractTitle(loContractFilterBean.getContractTitle().replaceAll(
						HHSConstants.STR, HHSConstants.STR_DOUBLE));
			}
			if (loContractFilterBean.getBaseContractTitle() != null)
			{
				lsBaseContractTitle = loContractFilterBean.getBaseContractTitle();
				loContractFilterBean.setBaseContractTitle(loContractFilterBean.getBaseContractTitle().replaceAll(
						HHSConstants.STR, HHSConstants.STR_DOUBLE));
			}
			getPagingParams(loPortletSession, loContractFilterBean, lsNextPage, HHSConstants.CONTRACT_LIST_KEY);
			loChannelObj.setData(HHSConstants.CONTRACT_FILTER_BEAN, loContractFilterBean);
			loChannelObj.setData(HHSConstants.ORG_ID, lsUserOrg);
			loChannelObj.setData(HHSConstants.AS_ORG_TYPE, lsOrgnizationType);
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.GET_AMENDMENT_LIST);
			LOG_OBJECT.Debug(" GET_AMENDMENT_LIST Transaction has been executed");
			setVariablesInRequest(aoRequest, loPortletSession, lbFirstLoad, lsOrgnizationType, lsRole,
					loProgramNameList, loAgencyDetails, loChannelObj, loContractFilterBean, lsViewAmendment,
					lsContractId, lsStatusId, lsContractTitle, lsBaseContractTitle);
		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Error occured in amendment list screen", aoAppExp);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, ApplicationConstants.ERROR_MESSAGE_FILENET_DOWN);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error occured in amendment list screen", aoExp);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, ApplicationConstants.ERROR_MESSAGE_FILENET_DOWN);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
		return new ModelAndView(HHSConstants.AMENDMENT_LIST, HHSConstants.AMENDED_CONTRACT_LIST_KEY,
				loContractFilterBean);
	}

	/**
	 * Remove data that is not required in session.
	 * @param loPortletSession Portlet session as input
	 * @throws ApplicationException
	 */
	private void removeOtherScreenBeanFromSession(PortletSession loPortletSession) throws ApplicationException
	{
		loPortletSession.removeAttribute(HHSConstants.BUDGET_SESSION_BEAN);
		loPortletSession.removeAttribute(HHSConstants.PAYMENT_SESSION_BEAN);
		loPortletSession.removeAttribute(HHSConstants.INVOICE_SESSION_BEAN);
	}

	/**
	 * This method set amendment default bean for default render
	 * <ul>
	 * <li>Method Added in R4</li>
	 * </ul>
	 * @param lsOrgnizationType org type as input
	 * @return ContractList default contract bean
	 * @throws ApplicationException Exception in case of code failure.
	 * 
	 */
	private ContractList setDefaultAmendmentBean(String lsOrgnizationType) throws ApplicationException
	{
		ContractList loContractFilterBean;
		loContractFilterBean = new ContractList();
		loContractFilterBean.setContractStatusList(new ArrayList<String>());
		loContractFilterBean.getContractStatusList().add(
				PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_CONTRACT_PENDING_CONFIGURATION));
		loContractFilterBean.getContractStatusList().add(
				PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_CONTRACT_PENDING_COF));
		loContractFilterBean.getContractStatusList().add(
				PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_CONTRACT_PENDING_SUBMISSION));
		loContractFilterBean.getContractStatusList().add(
				PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_CONTRACT_PENDING_APPROVAL));
		loContractFilterBean.getContractStatusList().add(
				PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_CONTRACT_PENDING_REGISTARTION));
		loContractFilterBean.getContractStatusList().add(
				PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_CONTRACT_SENT_FOR_REGISTRATION));
		loContractFilterBean.getContractStatusList().add(
				PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_CONTRACT_REGISTERED));
		return loContractFilterBean;
	}

	/**
	 * This method set Contract default bean for default render
	 * <ul>
	 * <li>Method Added in R4</li>
	 * </ul>
	 * @param lsOrgnizationType org type as input
	 * @return ContractList default contract bean
	 * @throws ApplicationException Exception in case of code failure.
	 * 
	 */
	private ContractList setDefaultContractBean(String lsOrgnizationType) throws ApplicationException
	{
		ContractList loContractFilterBean;
		loContractFilterBean = new ContractList();
		loContractFilterBean.setContractStatusList(new ArrayList<String>());
		if (!lsOrgnizationType.equalsIgnoreCase(HHSConstants.PROVIDER_ORG))
		{
			loContractFilterBean.getContractStatusList().add(
					PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
							HHSConstants.STATUS_CONTRACT_PENDING_CONFIGURATION));
			loContractFilterBean.getContractStatusList().add(
					PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
							HHSConstants.STATUS_CONTRACT_PENDING_COF));
		}
		loContractFilterBean.getContractStatusList().add(
				PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_CONTRACT_PENDING_REGISTARTION));
		loContractFilterBean.getContractStatusList().add(
				PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_CONTRACT_REGISTERED));
		return loContractFilterBean;
	}

	/**
	 * This method set the amendment related variable in request.
	 * @param aoRequest request as input
	 * @param loPortletSession portlet session
	 * @param lbFirstLoad whether it is a default render
	 * @param lsOrgnizationType type of organization
	 * @param lsRole role name
	 * @param loProgramNameList list of program
	 * @param loAgencyDetails details of agency
	 * @param loChannelObj data to be set in this channel
	 * @param loContractFilterBean contract bean used for filter and sorting
	 * @param lsViewAmendment whether viewing amenment list
	 * @param lsContractId contract id as input
	 * @param lsStatusId status id
	 * @param lsContractTitle title of contract
	 * @throws ApplicationException exception in case of query failure or other
	 *             reason
	 */
	@SuppressWarnings("unchecked")
	private void setVariablesInRequest(RenderRequest aoRequest, PortletSession loPortletSession, boolean lbFirstLoad,
			String lsOrgnizationType, String lsRole, List<ProgramNameInfo> loProgramNameList,
			List<HashMap<String, String>> loAgencyDetails, Channel loChannelObj, ContractList loContractFilterBean,
			String lsViewAmendment, String lsContractId, String lsStatusId, String lsContractTitle,
			String lsBaseContractTitle) throws ApplicationException
	{
		List<ContractList> loContractList = (List<ContractList>) loChannelObj
				.getData(HHSConstants.CB_CONTRACT_LIST_BEAN);

        /*[Start] R7.2.0 QC8914 Set indicator for Access control     */
        PortletSession loSession  = aoRequest.getPortletSession();
        if( loSession.getAttribute(ApplicationConstants.KEY_SESSION_ROLE_CURRENT, PortletSession.APPLICATION_SCOPE) != null
                && ApplicationConstants.ROLE_OBSERVER.equalsIgnoreCase((String)loSession.getAttribute(ApplicationConstants.KEY_SESSION_ROLE_CURRENT, PortletSession.APPLICATION_SCOPE)))  {
            setIndecatorForReadOnlyRole(loContractList, ApplicationConstants.ROLE_OBSERVER);
        }
        /*[End] R7.2.0 QC8914 Set indicator for Access control     */

		if (null != loContractList)
		{
			for (ContractList loContractListBean : loContractList)
			{
				loContractListBean.setActions(lsRole);
			}
		}
		if (loContractFilterBean.getContractTitle() != null && lsContractTitle != null)
		{
			loContractFilterBean.setContractTitle(lsContractTitle);
		}
		if (loContractFilterBean.getBaseContractTitle() != null && lsBaseContractTitle != null)
		{
			loContractFilterBean.setBaseContractTitle(lsBaseContractTitle);
		}
		Integer loContractsCount = (Integer) loChannelObj.getData(HHSConstants.CONTRACT_COUNT);
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
		aoRequest.setAttribute(HHSConstants.FINANCIALS_LIST, loContractList);
		aoRequest.setAttribute(HHSConstants.FIRST_LOAD, lbFirstLoad);
		aoRequest.setAttribute(HHSConstants.TOTAL_COUNT, ((loContractsCount == null) ? 0 : loContractsCount));
		loPortletSession.setAttribute(ApplicationConstants.ALERT_VIEW_PAGING_RECORDS, ((loContractsCount == null) ? 0
				: loContractsCount), PortletSession.APPLICATION_SCOPE);
		aoRequest.getPortletSession().setAttribute(HHSConstants.SORT_TYPE, loContractFilterBean.getFirstSortType(),
				PortletSession.APPLICATION_SCOPE);
		aoRequest.getPortletSession().setAttribute(HHSConstants.SORT_BY, loContractFilterBean.getSortColumnName(),
				PortletSession.APPLICATION_SCOPE);
		aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
				PortalUtil.parseQueryString(aoRequest, HHSConstants.CBL_MESSAGE));
		aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
				aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE_TYPE));
		aoRequest.setAttribute(HHSConstants.HIDDEN_HDNCONTRACTID, lsContractId);
		aoRequest.setAttribute(HHSConstants.HIDDEN_IS_VIEW_AMENDMENT, lsViewAmendment);
		aoRequest.setAttribute(HHSConstants.HIDDEN_HDNSTATUSID, lsStatusId);
	}

	/**
	 * This method is called when we select view amendments from drop down.
	 * @param aoRequest request as input
	 * @param aoResponse response as input
	 */
	@ActionMapping(params = "submit_action=amendment")
	protected void showSelectedAmendments(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		ApplicationSession.setAttribute(aoRequest.getParameter(HHSConstants.HIDDEN_HDNCONTRACTID), aoRequest,
				HHSConstants.HIDDEN_HDNCONTRACTID);
		ApplicationSession.setAttribute(aoRequest.getParameter(HHSConstants.HIDDEN_HDNSTATUSID), aoRequest,
				HHSConstants.HIDDEN_HDNSTATUSID);
		ApplicationSession.setAttribute(aoRequest.getParameter(HHSConstants.HIDDEN_IS_VIEW_AMENDMENT), aoRequest,
				HHSConstants.HIDDEN_IS_VIEW_AMENDMENT);
		try
		{
			aoResponse.setRenderParameter(ApplicationConstants.NEXT_ACTION, HHSConstants.AMENDMENT_RENDER);
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception Occurred while filetering on amendmentlist screen : " + aoExp);
			aoResponse.setRenderParameter(HHSConstants.CBL_MESSAGE, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST);
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
	}

	/**
	 * This method is called when user click on filter button or press enter key
	 * <ul>
	 * <li>Method Added in R4</li>
	 * </li> Updated for Emergency Build 4.0.0.1 defect 8360</li>
	 * </ul>
	 * @param aoContractBean ContractList bean model attribute.
	 * @param aoRequest ActionRequest as input
	 * @param aoResponse ActionResponse as input
	 * 
	 */
	@ActionMapping(params = "submit_action=filterAmendedContracts")
	protected void actionAmendedContractsFilter(@ModelAttribute("ContractList") ContractList aoContractBean,
			ActionRequest aoRequest, ActionResponse aoResponse)
	{
		String lsNextAction = aoRequest.getParameter(ApplicationConstants.NEXT_ACTION);
		ApplicationSession.setAttribute(aoRequest.getParameter(HHSConstants.HIDDEN_HDNCONTRACTID), aoRequest,
				HHSConstants.HIDDEN_HDNCONTRACTID);
		ApplicationSession.setAttribute(aoRequest.getParameter(HHSConstants.HIDDEN_HDNSTATUSID), aoRequest,
				HHSConstants.HIDDEN_HDNSTATUSID);
		ApplicationSession.setAttribute(aoRequest.getParameter(HHSConstants.HIDDEN_IS_VIEW_AMENDMENT), aoRequest,
				HHSConstants.HIDDEN_IS_VIEW_AMENDMENT);
		String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
		PortletSession loPortletSession = aoRequest.getPortletSession();
		try
		{
			if (lsNextAction != null)
			{
				if (lsNextAction.equalsIgnoreCase(HHSConstants.FILTER_AMENDED_CONTRACTS))
				{
					aoContractBean.setDefaultSortData();
					aoContractBean.setSecondSort(HHSConstants.AWARD_MODIFIED_DATE);
					// Updated for Emergency Build 4.0.0.2 defect 8377
					removeRedirectFromListSessionData(loPortletSession);
				}
				else if (lsNextAction.equalsIgnoreCase(HHSConstants.SORT_AMEND_CONTRACT_LIST))
				{
					String lsSortType = PortalUtil.parseQueryString(aoRequest, HHSConstants.SORT_TYPE);
					String lsColumnName = PortalUtil.parseQueryString(aoRequest, HHSConstants.COLUMN_NAME);

					getSortDetailsFromXML(lsColumnName, lsUserOrgType,
							PortalUtil.parseQueryString(aoRequest, HHSConstants.SORT_GRID_NAME), aoContractBean,
							lsSortType);

				}
				else if (lsNextAction.equalsIgnoreCase(HHSConstants.FETCH_NEXT_AMEND_CONTRACTS))
				{
					ApplicationSession.setAttribute(aoRequest.getParameter(HHSConstants.NEXT_PAGE), aoRequest,
							HHSConstants.NEXT_PAGE_PARAM);
					ApplicationSession.setAttribute(aoRequest.getParameter(HHSConstants.NEXT_PAGE), aoRequest,
							HHSConstants.CLC_OVERLAY_PARAM);
				}
			}
			ApplicationSession.setAttribute(aoContractBean, aoRequest, HHSConstants.AMENDED_CONTRACT_SESSION_BEAN);
			aoResponse.setRenderParameter(ApplicationConstants.NEXT_ACTION, HHSConstants.AMENDMENT_RENDER);
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception Occurred while filetering on amendmentlist screen : " + aoExp);
			aoResponse.setRenderParameter(HHSConstants.CBL_MESSAGE, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST);
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
	}

	/**
	 * This method added for Release 3.12.0 enhancement 6580
	 * @param aoResourceRequest ResourceRequest object
	 * @param aoResourceResponse ResourceResponse object
	 */
	@ResourceMapping("confirmFiscalYear")
	public void confirmFiscalYearOverlayLaunch(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		String lsFYIds = null;
		PrintWriter loOut = null;
		try
		{
			aoResourceResponse.setContentType(HHSConstants.TEXT_HTML);
			loOut = aoResourceResponse.getWriter();

			/*
			 * [Start] Added for QC9398 in R8.3.0 
			 */
			//Set up Date Format
			String pattern = "MM/dd/yyyy";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
			
			//Parameter from addContract.jsp/addContract.js
			String lsContractStartDate = (String) aoResourceRequest.getParameter(HHSConstants.CONTRACT_START_DATE);
			String lsContractEndDate = (String) aoResourceRequest.getParameter(HHSConstants.CONTRACT_END_DATE);
			
			Channel loChannelObj = new Channel();
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.FETCH_NEW_FY_TASK_DAYS_VALUE);
			Integer loNumOfDays = (Integer) loChannelObj.getData(HHSR5Constants.DURATION_IN_DAYS);
			LOG_OBJECT.Info("************* Fiscal Year [Start Date:"+lsContractStartDate+"]  [End Date:"+ lsContractEndDate+ "]  [NEW_FY_TASK_DAYS_VALUE:"+ loNumOfDays +"]");

			//Create two instance for contract start/end Date.
			Calendar calendarSrt = Calendar.getInstance();
			calendarSrt.setTime(simpleDateFormat.parse(lsContractStartDate));
			Calendar calendarEnd = Calendar.getInstance();
			calendarEnd.setTime(simpleDateFormat.parse(lsContractEndDate));

			//Generate FY list. 
			ArrayList<Integer> lsBudgetYears = (new HHSUtil()).FYListInt( calendarSrt , calendarEnd , loNumOfDays) ;

			//Create FY List delimited by comma 
			lsFYIds = lsBudgetYears.toString();
			int indexOfOpenBracket = lsFYIds.indexOf("[");
			int indexOfLastBracket = lsFYIds.lastIndexOf("]");
			lsFYIds = lsFYIds.substring(indexOfOpenBracket + 1, indexOfLastBracket);
			LOG_OBJECT.Info("************* Fiscal Year List ["+lsFYIds+ "]");

			/*
			 * [End] Added for QC9398 in R8.3.0 
			 */
			
			// start Build 3.1.0, enhancement id: 6020 changes to get pop up for
			// next fiscal year
/* commented out 
			String lsAppSettingMapKey = HHSConstants.NEW_FY_TASK + HHSConstants.UNDERSCORE
					+ HHSConstants.DURATION_IN_DAYS;
			Map<String, String> loApplicationSettingMap = (Map<String, String>) BaseCacheManagerWeb.getInstance()
					.getCacheObject(ApplicationConstants.APPLICATION_SETTING);
			Integer loNewFYTaskSpan = Integer.valueOf((String) loApplicationSettingMap.get(lsAppSettingMapKey));
			Integer loCurrentFiscalYear = HHSUtil.GetFiscalYear();
			Date loCurrentFYEndDate = DateUtil.getSqlDate(HHSConstants.FISCAL_YEAR_END_DATE + loCurrentFiscalYear);
			Date loCurrentDate = DateUtil.getSqlDate(DateUtil.getCurrentDate());
			long liDaysDifference = DateUtil.getDateDifference(loCurrentDate, loCurrentFYEndDate);
			String lsContractStartYear = (String) aoResourceRequest
					.getParameter(HHSR5Constants.CONTRACT_START_FISCAL_YEAR);
			String lsContractEndYear = (String) aoResourceRequest.getParameter(HHSR5Constants.CONTRACT_END_FISCAL_YEAR);
			int liContractStartYear = Integer.parseInt(lsContractStartYear);
			int liContractEndYear = Integer.parseInt(lsContractEndYear);
			ArrayList<Integer> lsBudgetYears = new ArrayList<Integer>();
			Integer lsNewFiscalYear = loCurrentFiscalYear + 1;

		    if ((liContractStartYear == loCurrentFiscalYear && liDaysDifference > loNewFYTaskSpan)
					|| liContractStartYear > loCurrentFiscalYear)
			{
				lsFYIds = HHSR5Constants.EMPTY_STRING;
			}
			else
			{
				if (liContractStartYear != loCurrentFiscalYear && liContractStartYear < loCurrentFiscalYear)
				{
					lsBudgetYears.add((loCurrentFiscalYear - 1));
					lsBudgetYears.add((loCurrentFiscalYear));
				}
				else if (liContractStartYear == loCurrentFiscalYear)
				{
					lsBudgetYears.add((loCurrentFiscalYear));
				}
				if (liDaysDifference < loNewFYTaskSpan && (liContractStartYear < lsNewFiscalYear)
						&& (liContractEndYear >= lsNewFiscalYear))
				{
					lsBudgetYears.add((lsNewFiscalYear));
				}
				lsFYIds = lsBudgetYears.toString();
				int indexOfOpenBracket = lsFYIds.indexOf(HHSR5Constants.SQUARE_BRAC_BEGIN);
				int indexOfLastBracket = lsFYIds.lastIndexOf(HHSR5Constants.SQUARE_BRAC_END);
				lsFYIds = lsFYIds.substring(indexOfOpenBracket + 1, indexOfLastBracket);
			}*/
			// end
		}
/*
        catch (ApplicationException aoExe)
		{
            LOG_OBJECT.Error("ApplicationException occured in confirmFiscalYearOverlayLaunch", aoExe);

		}
*/
		catch (ParseException aoPExe)
		{
			LOG_OBJECT.Error("During simpleDateFormat.parse, ParseException occured in confirmFiscalYearOverlayLaunch", aoPExe);
		}
		catch (Exception aoExe)
		{
			LOG_OBJECT.Error("General Exception occured in confirmFiscalYearOverlayLaunch", aoExe);
		}
		finally
		{
			//Sending data OR error message
			catchTaskError(loOut, lsFYIds);
		}

	}

// Start : Added in R7 Defect 8644 Issue 3
	/**
	 * This method added to add one fiscal
	 * year to a contract
	 * @param aoResourceRequest ResourceRequest object
	 * @param aoResourceResponse ResourceResponse object
	 */
	@ResourceMapping("addFiscalYearToContract")
	public void addFiscalYearToContract(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse) throws Exception
	{
		String lsContractId = aoResourceRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW);
		Channel loChannelObj = new Channel();
		loChannelObj.setData(HHSConstants.CONTRACT_ID_KEY, lsContractId);
		HHSTransactionManager.executeTransaction(loChannelObj, "addFiscalYearToContract",HHSR5Constants.TRANSACTION_ELEMENT_R5);
		aoResourceRequest.getPortletSession().setAttribute(
				HHSConstants.SUCCESS_MESSAGE,
				PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.BUDGET_TEMPLATE_SUCCESS_MESSAGE), PortletSession.APPLICATION_SCOPE);	
	}
	/**
	 * This method is added in R7. This method is added to Flag Contract.
	 * @param aoResourceRequest
	 * @param aoResourceResponse
	 * @throws Exception
	 */
	@ResourceMapping("launchSubmitMessageOverlay")
	protected void flagContract(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		LOG_OBJECT.Info("Entered Flag Contract Method Controller");
		String lsContractId = aoResourceRequest.getParameter(HHSR5Constants.CONTRACT_ID1);
		PortletSession loPortletSession = aoResourceRequest.getPortletSession();
		String lsUserId = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsMessage = aoResourceRequest.getParameter(HHSR5Constants.CONTRACT_MESSAGE);
		Map<String, String> loHashMap = new HashMap<String, String>();
		try
		{
			loHashMap.put(HHSR5Constants.CONTRACT_ID1, lsContractId);
			loHashMap.put(HHSR5Constants.USER_ID, lsUserId);
			loHashMap.put(HHSR5Constants.CONTRACT_MESSAGE, lsMessage);
			loHashMap.put(HHSR5Constants.ACTIVE_FLAG, HHSR5Constants.STRING_ONE);
			Channel loChannel = new Channel();
			loChannel.setData(HHSR5Constants.AO_HASH_MAP, loHashMap);

			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.SAVE_CONTRACT_MESSAGE,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			Integer loUpdateStatus = (Integer) loChannel.getData(HHSR5Constants.RETURN_STATUS);
			if (loUpdateStatus > 0)
			{
				aoResourceRequest.getPortletSession().setAttribute(
						HHSConstants.SUCCESS_MESSAGE,
						PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGE_PROP_FILE,
								HHSR5Constants.FLAG_SUCCESS_MESSAGE_M97), PortletSession.APPLICATION_SCOPE);
			}
		}
		catch (ApplicationException aoExe)
		{
			LOG_OBJECT.Error("ApplicationException occured in Flag Contract Method", aoExe);

		}
		catch (Exception aoExe)
		{
			LOG_OBJECT.Error("Exception occured in flag contract method", aoExe);
		}
	}

	/**
	 * This method is added in R7. This method will un-flag the contract that is
	 * flag=0
	 * @param aoActionRequest
	 * @param aoActionResponse
	 * @throws ApplicationException
	 * @throws IOException
	 */
	@ResourceMapping("unflagContractMessage")
	public void unflagContractMessage(ResourceRequest aoActionRequest, ResourceResponse aoActionResponse)
	{
		LOG_OBJECT.Info("Entered unflagContractMessage Method Controller");
		PortletSession loPortletSession = aoActionRequest.getPortletSession();
		try
		{
			String lsUserId = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);
			String lsContractId = aoActionRequest.getParameter(HHSConstants.CONTRACT_ID1);
			Channel loChannel = new Channel();
			loChannel.setData(HHSConstants.CONTRACT_ID1, lsContractId);
			loChannel.setData(HHSR5Constants.USER_ID, lsUserId);
			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.UNFLAG_CONTRACT,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			Integer loUpdateStatus = (Integer) loChannel.getData(HHSR5Constants.RETURN_STATUS);
			if (loUpdateStatus > 0)
			{
				aoActionRequest.getPortletSession().setAttribute(
						HHSConstants.SUCCESS_MESSAGE,
						PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGE_PROP_FILE,
								HHSR5Constants.UNFLAG_SUCCESS_MESSAGE_M98), PortletSession.APPLICATION_SCOPE);
			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Error in Unflag Contract Method :", loAppEx);
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error in Unflag Contract Method :", loExp);
		}
	}

	/**
	 * This method is added in R7. This method will fetch the Flag/Unflag
	 * overlay details.
	 * @param aoResourceRequest
	 * @param aoResourceResponse
	 * @throws Exception
	 */
	@ResourceMapping("fetchMessageOverlayDetails")
	public void fetchMessageOverlayDetails(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		LOG_OBJECT.Info("Entered fetchMessageOverlayDetails Method Controller");
		String lsContractId = aoResourceRequest.getParameter(HHSR5Constants.CONTRACT_ID1);
		PortletSession loPortletSession = aoResourceRequest.getPortletSession();
		String lsUserId = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsMessage = HHSConstants.EMPTY_STRING;
		Map<String, String> loHashMap = new HashMap<String, String>();
		PrintWriter loOut = null;
		try
		{
			aoResourceResponse.setContentType(HHSConstants.APPLICATION_JSON);
			loOut = aoResourceResponse.getWriter();
			loHashMap.put(HHSR5Constants.CONTRACT_ID1, lsContractId);
			loHashMap.put(HHSR5Constants.USER_ID, lsUserId);
			loHashMap.put(HHSR5Constants.CONTRACT_MESSAGE, lsMessage);
			loHashMap.put(HHSR5Constants.ACTIVE_FLAG, HHSR5Constants.STRING_ONE);
			String lsActionSeleted = aoResourceRequest.getParameter(HHSR5Constants.ACTION_SELECTED);
			Channel loChannel = new Channel();
			loChannel.setData(HHSR5Constants.CONTRACT_ID1, lsContractId);
			loChannel.setData(HHSR5Constants.ACTION_SELECTED, lsActionSeleted);
			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.FETCH_CONTRACT_MESSAGE_OVERLAY_DETAILS,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			ContractList loContractList = (ContractList) loChannel
					.getData(HHSR5Constants.CONTRACT_MESSAGE_OVERLAY_BEAN);
			loOut.write("{\"" + HHSR5Constants.CONTRACT_ID_FOR_LIST + "\": "
					+ loChannel.getData(HHSR5Constants.CONTRACT_ID_FOR_LIST) + ", \"ContractList\":"
					+ JSONUtility.convertToString(loContractList) + "}");

		}
		catch (ApplicationException aoExe)
		{
			LOG_OBJECT.Error("ApplicationException occured in fetchMessageOverlayDetails method", aoExe);

		}
		catch (Exception aoExe)
		{
			LOG_OBJECT.Error("Exception occured in fetchMessageOverlayDetails method", aoExe);
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
