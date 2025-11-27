package com.nyc.hhs.controllers;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
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
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.springframework.stereotype.Controller;
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
import com.nyc.hhs.constants.TransactionConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.AgencySettingsBean;
import com.nyc.hhs.model.AgencyTaskBean;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.CommentsHistoryBean;
import com.nyc.hhs.model.Document;
import com.nyc.hhs.model.DocumentPropertiesBean;
import com.nyc.hhs.model.EvaluationBean;
import com.nyc.hhs.model.ExtendedDocument;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.InvoiceList;
import com.nyc.hhs.model.NotificationDataBean;
import com.nyc.hhs.model.Procurement;
import com.nyc.hhs.model.ProcurementCOF;
import com.nyc.hhs.model.ProposalDetailsBean;
import com.nyc.hhs.model.PsrBean;
import com.nyc.hhs.model.ScoreDetailsBean;
import com.nyc.hhs.model.StaffDetails;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.model.UserBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.ApplicationSession;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.FileNetOperationsUtils;
import com.nyc.hhs.util.HHSPortalUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PortalUtil;
import com.nyc.hhs.util.PortletSessionHandler;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.RFPReleaseDocsUtil;
import com.nyc.hhs.util.XMLUtil;

/**
 * This class is added for release 5 This controller class is used to handle all
 * the task related activities for the agency.It is used to execute various
 * transactions and navigating user to the desired screen.It is used to
 * Finish,Reassign,Reject tasks.
 * 
 */
@Controller(value = "agencyWorkflow")
@RequestMapping("view")
public class AgencyWorkflowController extends BaseControllerSM implements ResourceAwareController
{
	/**
	 * Log object which is used to log any error into log file
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(AgencyWorkflowController.class);

	/**
	 * This method will initialize the ScoreDetailsBean bean object
	 * 
	 * @return ScoreDetailsBean bean Object
	 */
	@ModelAttribute("ScoreDetailsBean")
	public ScoreDetailsBean getScoreDetailsBeanObject()
	{
		return new ScoreDetailsBean();
	}

	/**
	 * @return Proposal Details Bean Object
	 */
	@ModelAttribute("ProposalDetailsBean")
	public ProposalDetailsBean getProposalDetailsBean()
	{
		return new ProposalDetailsBean();
	}

	// Start Added in R5
	/**
	 * contructor for psrbean which will initialise psrbean  
	 * */
	@ModelAttribute("PsrBean")
	public PsrBean getPsrBean()
	{
		return new PsrBean();
	}

	// End Added in R5

	/**
	 * This is default render method which will be called when agency user
	 * clicks on task inbox icon
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Changes done for enhancement QC : 5688 for Release 3.2.0</li>
	 * </ul>
	 * 
	 * @param aoRequest RenderRequest object
	 * @param aoResponse RenderResponse object
	 * @return model and view object containing view name
	 */
	@RenderMapping
	protected ModelAndView handleRenderRequestInternal(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		ModelAndView loModelAndView = null;
		String lsCurrentTab = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.CHOOSEN_TAB);
		String lsTaskAction = PortalUtil.parseQueryString(aoRequest, HHSConstants.TASK_ACTION);
		String lsInboxPath = HHSConstants.AGENCY_INBOX_JSP;
		String lsManagementPath = HHSConstants.AGENCY_TASK_MANAGEMENT_JSP;
		String lsFilterClick = aoRequest.getParameter(HHSConstants.FILTER_CHECKED);
		String lsReturnToAgencyTask = PortalUtil.parseQueryString(aoRequest, HHSConstants.RETURN_TO_AGENCY_TASK);
		String lsError = PortalUtil.parseQueryString(aoRequest, HHSConstants.ERROR_FLAG);
		String lsUserRole = null;
		Channel loChannel = new Channel();
		boolean lbAgencyScreen = true;
		// String lsChoosenTab =
		// aoRequest.getParameter(HHSConstants.CONTROLLER_ACTION);
		String lsChoosenTab = PortalUtil.parseQueryString(aoRequest, HHSConstants.CONTROLLER_ACTION);
		aoRequest.setAttribute(HHSConstants.INCLUDE_FILE_PATH, lsInboxPath);
		try
		{
			PortletSession loSession = aoRequest.getPortletSession();
			P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			lsUserRole = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ROLE, PortletSession.APPLICATION_SCOPE);
			// Start of Changes done for enhancement QC : 5688 for Release 3.2.0
			if (lsChoosenTab != null && lsChoosenTab.equalsIgnoreCase(HHSConstants.AGENCY_WORKFLOW_FOR_CITY))
			{
				lsCurrentTab = P8Constants.PROPERTY_PAGE_TASK_MANAGMENT;
				lsUserRole = HHSConstants.CITY;
				lbAgencyScreen = false;
			}
			// End of Changes done for enhancement QC : 5688 for Release 3.2.0
			// Default render method
			lsCurrentTab = getCurrentTab(aoRequest, aoResponse, lsCurrentTab, lsTaskAction, lsFilterClick,
					lsReturnToAgencyTask, loChannel, loSession, loUserSession);

			if (lsCurrentTab != null && lsCurrentTab.equalsIgnoreCase(P8Constants.PROPERTY_PAGE_TASK_MANAGMENT))
			{
				aoRequest.setAttribute(HHSConstants.INCLUDE_FILE_PATH, lsManagementPath);
			}
			if (null != lsError && !lsError.isEmpty())
			{
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsError);
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
			}
			else
			{
				// Start Added in R5
				// Changes for Defect 7149
				// If we are navigating from Cancel Award Screen
				if (aoRequest.getParameter(HHSConstants.PARAM_VALUE) != null
						&& aoRequest.getParameter(HHSConstants.PARAM_VALUE).equalsIgnoreCase(HHSConstants.CANCEL_AWARD))
				{
					String lsCancelMessage = MessageFormat.format(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.CANCEL_AWARD_SUCESS), HHSPortalUtil
							.parseQueryString(aoRequest, HHSConstants.ORGANIZATN_NAME));
					aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsCancelMessage);
					aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
							ApplicationConstants.MESSAGE_PASS_TYPE);
				}
				else
				{
					aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
							aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE));
					aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
							aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE_TYPE));
				}
				// End Added in R5
			}
		}
		// handling Application Exception thrown by any action/resource
		// method.shows error message on page
		catch (ApplicationException aoAppEx)
		{
			setGenericErrorMessage(aoRequest);
			LOG_OBJECT.Error("Error occurred while processing agency inbox", aoAppEx);
		}
		// handling Exception other than Application Exception
		catch (Exception aoEx)
		{
			setGenericErrorMessage(aoRequest);
			LOG_OBJECT.Error("Error occurred while processing agency inbox", aoEx);

		}
		// set task type and status list in request for Filter screens
		getTaskData(aoRequest, lsUserRole, lsCurrentTab);
		// Start of Changes done for enhancement QC : 5688
		if (lbAgencyScreen)
		{
			loModelAndView = new ModelAndView(HHSConstants.AGENCY_WF);
		}
		else
		{
			aoRequest.setAttribute(HHSConstants.INCLUDE_AGENCY_TASK_MANAGEMENT,
					HHSConstants.INCLUDE_AGENCY_TASK_MANAGEMENT_PATH);
			loModelAndView = new ModelAndView(HHSConstants.TASK_HOME_CITY);
		}
		// End of Changes done for enhancement QC : 5688
		return loModelAndView;
	}

	/**
	 * This method is used to get current tab
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * @param aoRequest
	 * @param aoResponse
	 * @param asCurrentTab
	 * @param asTaskAction
	 * @param asFilterClick
	 * @param asReturnToAgencyTask
	 * @param loChannel
	 * @param aoSession
	 * @param aoUserSession
	 * @return
	 * @throws ApplicationException
	 */
	private String getCurrentTab(RenderRequest aoRequest, RenderResponse aoResponse, String asCurrentTab,
			String asTaskAction, String asFilterClick, String asReturnToAgencyTask, Channel loChannel,
			PortletSession aoSession, P8UserSession aoUserSession) throws ApplicationException
	{
		AgencyTaskBean loAgencyTaskBean;
		if (null != asFilterClick)
		{
			loAgencyTaskBean = (AgencyTaskBean) ApplicationSession.getAttribute(aoRequest, Boolean.TRUE,
					HHSConstants.FILTER_RETAINED);
			if (null == loAgencyTaskBean)
			{
				loAgencyTaskBean = new AgencyTaskBean();
			}
			getTaskDetailsAndSetInRequest(aoRequest, aoResponse, asFilterClick, aoSession, loAgencyTaskBean,
					aoUserSession, loChannel, asCurrentTab);

		}
		// This is called on click of home page screens
		else if (null != asTaskAction)
		{
			processTaskListForReqFromHomepages(aoRequest, aoResponse, asCurrentTab, asTaskAction, aoSession,
					aoUserSession);
		}
		// This is called when page is redirect from Task screens to
		// Inbox/management screen
		else if (null != asReturnToAgencyTask)
		{
			asCurrentTab = (String) ApplicationSession.getAttribute(aoRequest, HHSConstants.SELECTED_TAB);
			loAgencyTaskBean = (AgencyTaskBean) ApplicationSession.getAttribute(aoRequest, Boolean.TRUE,
					HHSConstants.FILTER_RETAINED);
			Integer loTotalTaskCount = getTaskListFromFilenet(aoRequest, aoResponse, loChannel, aoUserSession,
					loAgencyTaskBean, asCurrentTab);
			aoRequest.setAttribute(HHSConstants.FILTER_CHECKED, HHSConstants.DISPLAY_BLOCK);
			aoSession.setAttribute(HHSConstants.AGENCY_TASK_ITEM_LIST,
					ApplicationSession.getAttribute(aoRequest, Boolean.TRUE, HHSConstants.AGENCY_TASK_ITEM_LIST),
					PortletSession.APPLICATION_SCOPE);
			aoRequest.setAttribute(HHSConstants.TOTAL_TASK, loTotalTaskCount);
			aoSession.setAttribute(ApplicationConstants.ALERT_VIEW_PAGING_RECORDS, loTotalTaskCount,
					PortletSession.APPLICATION_SCOPE);
			aoSession.setAttribute(HHSConstants.ALLOWED_OBJECT_COUNT, HHSConstants.PE_GRID_PAGE_SIZE,
					PortletSession.APPLICATION_SCOPE);
			aoSession.setAttribute(ApplicationConstants.ALERT_VIEW_PAGING_PAGE_INDEX,
					String.valueOf(loAgencyTaskBean.getPaginationNum()), PortletSession.APPLICATION_SCOPE);
			ApplicationSession.setAttribute(loAgencyTaskBean, aoRequest, HHSConstants.FILTER_RETAINED);
			aoRequest.setAttribute(HHSConstants.FILTER_RETAINED, loAgencyTaskBean);
		}
		return asCurrentTab;
	}

	/**
	 * This method set all details in request or session required for Agency
	 * Home screens
	 * 
	 * @param aoRequest Request Object
	 * @param aoResponse Response Object
	 * @param asCurrentTab Current Tab
	 * @param asTaskAction Task Action
	 * @param aoSession Seesion object
	 * @param aoUserSession Filenet Session
	 * @throws ApplicationException if any exception occurred
	 */
	private void processTaskListForReqFromHomepages(RenderRequest aoRequest, RenderResponse aoResponse,
			String asCurrentTab, String asTaskAction, PortletSession aoSession, P8UserSession aoUserSession)
			throws ApplicationException
	{
		Channel loChannel = new Channel();
		String lsTaskType = PortalUtil.parseQueryString(aoRequest, HHSConstants.TASK_TYPE);
		String lsAssignedTo = null;
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsUserOrg = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);
		String lsUserRole = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ROLE, PortletSession.APPLICATION_SCOPE);
		// if the action is for unassigned task
		if (asTaskAction.equalsIgnoreCase(HHSConstants.UNASSIGN))
		{
			if (lsTaskType.equalsIgnoreCase(P8Constants.TASK_ACCEPT_PROPOSAL)
					|| lsTaskType.equalsIgnoreCase(P8Constants.TASK_CONFIGURE_AWARDS_DOCUMENTS))
			{
				lsAssignedTo = P8Constants.UNASSIGNED_ACCO_MANAGER;
			}
			else if (lsTaskType.equalsIgnoreCase(P8Constants.TASK_REVIEW_SCORES))
			{
				lsAssignedTo = P8Constants.UNASSIGNED_ACCO_STAFF;
			}
			else
			{
				lsAssignedTo = P8Constants.UNASSIGNED_ALL_LEVELS;
			}
		}
		// if the action is for the home task
		else if (asTaskAction.equalsIgnoreCase(HHSConstants.TASK_HOME))
		{
			lsAssignedTo = (String) aoSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);
		}
		else if (asTaskAction.equalsIgnoreCase(HHSConstants.ASSIGN))
		{
			lsAssignedTo = P8Constants.PROPERTY_PE_VALUE_ALL_STAFF;
		}
		AgencyTaskBean loAgencyTaskBean = createFilter(lsUserRole, lsUserOrg, lsUserId, lsTaskType, null, null, null,
				null, null, null, null, null, lsAssignedTo, asCurrentTab, null);
		loAgencyTaskBean.setPaginationNum(HHSConstants.INT_ONE);
		getTaskDetailsAndSetInRequest(aoRequest, aoResponse, HHSConstants.DISPLAY_BLOCK, aoSession, loAgencyTaskBean,
				aoUserSession, loChannel, asCurrentTab);
	}

	/**
	 * 
	 * THis method set all the details fetch from filenet or database in session
	 * or request
	 * 
	 * @param aoRequest Request Object
	 * @param aoResponse Response Object
	 * @param asFilterClick Filter Click
	 * @param aoSession Session object
	 * @param aoAgencyTaskBean Agency Task Bean object
	 * @param aoUserSession User Filenet Session
	 * @param aoChannel Channel object
	 * @param asCurrentTab Current Tab
	 * @throws ApplicationException if any exception occurs
	 */
	private void getTaskDetailsAndSetInRequest(RenderRequest aoRequest, RenderResponse aoResponse,
			String asFilterClick, PortletSession aoSession, AgencyTaskBean aoAgencyTaskBean,
			P8UserSession aoUserSession, Channel aoChannel, String asCurrentTab) throws ApplicationException
	{
		StringBuffer loOrderSb = new StringBuffer();
		if (aoAgencyTaskBean.isFirstSortDate())
		{
			//[Start]R9.4.0 QC_9664 HHS does not always organize invoices task chronologically
			/*if (HHSConstants.PROPERTY_PE_SUBMITTED_DATE.equalsIgnoreCase(aoAgencyTaskBean.getFirstSort())
					&& HHSConstants.PROPERTY_PE_ASSIGNED_DATE.equalsIgnoreCase(aoAgencyTaskBean.getSecondSort()))
			{
				loOrderSb.append("floor(( 1 / 86400 ) * ");
				loOrderSb.append("\"");
				loOrderSb.append(aoAgencyTaskBean.getFirstSort());
				loOrderSb.append("\" )");
			}
			else
			{*/
				loOrderSb.append("\"");
				loOrderSb.append(aoAgencyTaskBean.getFirstSort());
				loOrderSb.append("\" ");
			//}
			//[End]R9.4.0 QC_9664 HHS does not always organize invoices task chronologically
		}
		else
		{
			// Start || Added for enhancement 6636 for Release 3.12.0
			if ("ProposalID".equalsIgnoreCase(aoAgencyTaskBean.getFirstSort()))
			{
				loOrderSb.append("TO_NUMBER(");
				loOrderSb.append("\"");
				loOrderSb.append(aoAgencyTaskBean.getFirstSort());
				loOrderSb.append("\" )");
			}
			else
			{
				// End || Added for enhancement 6636 for Release 3.12.0
				loOrderSb.append("lower(\"");
				loOrderSb.append(aoAgencyTaskBean.getFirstSort());
				loOrderSb.append("\") ");
			}
		}
		loOrderSb.append(aoAgencyTaskBean.getFirstSortType());
		if (aoAgencyTaskBean.isSecondSortDate())
		{
			loOrderSb.append(", \"");
			loOrderSb.append(aoAgencyTaskBean.getSecondSort());
			loOrderSb.append("\" ");
		}
		else
		{
			loOrderSb.append(", lower(\"");
			loOrderSb.append(aoAgencyTaskBean.getSecondSort());
			loOrderSb.append("\") ");
		}
		loOrderSb.append(aoAgencyTaskBean.getSecondSortType());
		aoAgencyTaskBean.setOrderBy(loOrderSb.toString());
		Integer loTotalTaskCount = getTaskListFromFilenet(aoRequest, aoResponse, aoChannel, aoUserSession,
				aoAgencyTaskBean, asCurrentTab);
		aoRequest.setAttribute(HHSConstants.FILTER_CHECKED, asFilterClick);
		aoSession.setAttribute(HHSConstants.AGENCY_TASK_ITEM_LIST,
				ApplicationSession.getAttribute(aoRequest, Boolean.TRUE, HHSConstants.AGENCY_TASK_ITEM_LIST),
				PortletSession.APPLICATION_SCOPE);
		aoRequest.setAttribute(HHSConstants.TOTAL_TASK, loTotalTaskCount);
		aoSession.setAttribute(ApplicationConstants.ALERT_VIEW_PAGING_RECORDS, loTotalTaskCount,
				PortletSession.APPLICATION_SCOPE);
		aoSession.setAttribute(HHSConstants.ALLOWED_OBJECT_COUNT, HHSConstants.PE_GRID_PAGE_SIZE,
				PortletSession.APPLICATION_SCOPE);
		aoSession.setAttribute(ApplicationConstants.ALERT_VIEW_PAGING_PAGE_INDEX,
				String.valueOf(aoAgencyTaskBean.getPaginationNum()), PortletSession.APPLICATION_SCOPE);
		ApplicationSession.setAttribute(aoAgencyTaskBean, aoRequest, HHSConstants.FILTER_RETAINED);
		aoRequest.setAttribute(HHSConstants.FILTER_RETAINED, aoAgencyTaskBean);
	}

	/**
	 * This method Read 'agencyTaskConfiguration.xml' to get Task Type and
	 * status mapping to show drop down values in filter screens of Agency Inbox
	 * and management
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Modification done as a part of Enhancement #5688 for Release 3.2.0</li>
	 * </ul>
	 * 
	 * @param aoRequest Request Object
	 * @param asUserRole User Role
	 * @param asCurrentTab Current Tab
	 * @throws ApplicationException if any exception occurs
	 * 
	 *             calls the transaction 'getProgramNameForAgency'
	 */
	@SuppressWarnings("unchecked")
	private void getTaskData(RenderRequest aoRequest, String asUserRole, String asCurrentTab)

	{
		Channel loChannelObj = new Channel();
		List<Procurement> loProgramNameList = null;
		PortletSession loSession = aoRequest.getPortletSession();
		String lsUserOrg = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
				PortletSession.APPLICATION_SCOPE);
		try
		{
			org.jdom.Document loConfigurationDom = (org.jdom.Document) BaseCacheManagerWeb.getInstance()
					.getCacheObject(HHSConstants.AGENCY_TASK_CONFIG);
			String lsXPath = "//role[@usertype = \"" + asUserRole + "\"]";
			Element loRequiredElt = XMLUtil.getElement(lsXPath, loConfigurationDom);
			if (loRequiredElt != null)
			{
				StringBuffer loStatusStringBuffer = null;
				StringBuffer loStringBuffer = new StringBuffer();
				Map<String, String> loStatusMap = new HashMap<String, String>();
				String lsTaskType = null;
				List<Element> loStatusEltList = null;
				List<Element> loFinancialEltList = null;
				String lsStatusXpath = HHSConstants.EMPTY_STRING;
				String lsFinancialsXpath = HHSConstants.EMPTY_STRING;
				String lsFinancialTask = null;
				if (loRequiredElt != null)
				{
					lsFinancialTask = loRequiredElt.getAttributeValue(HHSConstants.IS_FINANCIAL);
				}
				if (lsFinancialTask != null && lsFinancialTask.equalsIgnoreCase(HHSConstants.TRUE))
				{
					lsFinancialsXpath = "//financial-task//option";
					loFinancialEltList = (List<Element>) XMLUtil.getElementList(lsFinancialsXpath, loConfigurationDom);
				}
				List<Element> loOptionEltList = loRequiredElt.getChildren(HHSConstants.OPTION);
				if (loOptionEltList == null)
				{
					loOptionEltList = new ArrayList<Element>();
				}
				if (loOptionEltList != null)
				{
					List<Element> loCopiedList = HHSUtil.copyListToList(loFinancialEltList, loOptionEltList,
							asCurrentTab);
					for (Element loElement : loCopiedList)
					{
						lsTaskType = loElement.getAttributeValue(HHSConstants.VALUE);
						loStringBuffer.append(XMLUtil.getXMLAsString(loElement));
						if (lsTaskType != null)
						{
							lsStatusXpath = "//tasktype[@value = \"" + lsTaskType + "\"]//option";
							loStatusEltList = (List<Element>) XMLUtil.getElementList(lsStatusXpath, loConfigurationDom);
							if (loStatusEltList != null)
							{
								loStatusStringBuffer = new StringBuffer();
								for (Element loStatusElement : loStatusEltList)
								{
									loStatusStringBuffer.append(StringEscapeUtils.escapeXml(XMLUtil
											.getXMLAsString(loStatusElement)));
								}
								loStatusMap.put(lsTaskType, loStatusStringBuffer.toString());
							}
						}
					}
				}

				loChannelObj.setData(HHSConstants.AGENCYID, lsUserOrg);
				loChannelObj.setData(HHSConstants.AGENCY_ORG, true);
				// Start || Modification done as a part of Enhancement #5688 for
				// Release 3.2.0
				if (!asUserRole.equalsIgnoreCase(HHSConstants.CITY))
				{
					HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.GET_PROGRAM_NAME_FOR_AGENCY);
					loProgramNameList = (List<Procurement>) loChannelObj.getData(HHSConstants.PROGRAM_NAME_LIST);
					if (null != loProgramNameList)
					{
						aoRequest.setAttribute(HHSConstants.BMC_PROGRAM_LIST, loProgramNameList);
					}
				}
				// End || Modification done as a part of Enhancement #5688 for
				// Release 3.2.0
				aoRequest.setAttribute(HHSConstants.TASK_TYPES, loStringBuffer.toString());
				aoRequest.setAttribute(HHSConstants.TASK_STATUS_MAP, loStatusMap);

			}
			// Start of Changes done for enhancement QC : 5688
			// Start Updated in R5
			// if (asUserRole.equalsIgnoreCase(HHSConstants.CITY))
			// {
			AgencySettingsBean loAgencySettingsBean = null;
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.AS_GET_AGENCY_REVIEW_PROCESS_DATA);
			loAgencySettingsBean = (AgencySettingsBean) loChannelObj.getData(HHSConstants.AS_AGENCY_SETTING_BEAN_OBJ);

			// List<ReviewProcessBean> loReviewProcessBeanList =
			// loAgencySettingsBean.getAllReviewProcessBeanList();
			aoRequest.setAttribute(HHSConstants.AS_AGENCY_SETTING_BEAN, loAgencySettingsBean);
			// End Updated in R5
			// }
			// End of Changes done for enhancement QC : 5688
		}
		catch (ApplicationException aoAppEx)
		{
			setGenericErrorMessage(aoRequest);
			LOG_OBJECT.Error("Error occurred while processing Filter data", aoAppEx);
		}
	}

	/**
	 * This method called on click of filenet button in agency inbox or
	 * management screens
	 * 
	 * Modification done as a part of Enhancement #5688 for Release 3.2.0
	 * 
	 * @param aoRequest Request Object
	 * @param aoResponse Response Object
	 */
	@ActionMapping(params = "submit_action=inboxFilter")
	protected void filterInboxAction(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		AgencyTaskBean loAgencyTaskBean = null;
		int liPageNum = HHSConstants.INT_ONE;
		String lsPageIndex = aoRequest.getParameter(HHSConstants.NEXT_PAGE);
		String lsControllerAction = aoRequest.getParameter(HHSConstants.CONTROLLER_ACTION);
		if (null != lsPageIndex)
		{
			liPageNum = Integer.parseInt(lsPageIndex);
		}
		try
		{
			String lsChoosenTab = aoRequest.getParameter(ApplicationConstants.CHOOSEN_TAB);
			aoResponse.setRenderParameter(ApplicationConstants.CHOOSEN_TAB, lsChoosenTab);
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			String lsUserOrg = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);
			String lsUserRole = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ROLE, PortletSession.APPLICATION_SCOPE);
			String lsTaskType = aoRequest.getParameter(HHSConstants.TASK_FILTER_TYPE);
			String lsProviderName = aoRequest.getParameter(HHSConstants.TASK_FILTER_PROVIDER_NAME);
			String lsProcurementTitle = aoRequest.getParameter(HHSConstants.PROCUREMENT_TITLE);
			String lsProgramName = aoRequest.getParameter(HHSConstants.TASK_FILTER_PROGRAM_NAME);
			String lsStatus = aoRequest.getParameter(HHSConstants.STATUS_COLUMN);
			String lsSubmittedFrom = aoRequest.getParameter(HHSConstants.TASK_FILTER_DATE_FROM);
			String lsSubmittedTo = aoRequest.getParameter(HHSConstants.TASK_FILTER_DATE_TO);
			String lsDateAssignedFrom = aoRequest.getParameter(HHSConstants.TASK_FILTER_DATE_ASSIGNED_FROM);
			String lsDateAssignedTo = aoRequest.getParameter(HHSConstants.TASK_FILTER_DATE_ASSIGNED_TO);
			String lsAssignedTo = aoRequest.getParameter(HHSConstants.TASK_FILTER_ASSIGNED_TO);
			String lsCompPoolTtile = aoRequest.getParameter(HHSConstants.COMPETITION_POOL_TITLE);
			// Start of Changes done for enhancement QC : 5688
			String lsCityTab = PortalUtil.parseQueryString(aoRequest, HHSConstants.CONTROLLER_ACTION);
			if (lsCityTab != null && lsCityTab.equalsIgnoreCase(HHSConstants.AGENCY_WORKFLOW_FOR_CITY))
			{
				lsUserOrg = HHSConstants.USER_CITY;
				String lsAgencyId = aoRequest.getParameter(HHSConstants.AGENCY_SELECT_BOX);
				if (null != lsAgencyId && !lsAgencyId.isEmpty())
				{
					lsUserOrg = lsAgencyId;
				}
			}
			// End of Changes done for enhancement QC : 5688
			loAgencyTaskBean = createFilter(lsUserRole, lsUserOrg, lsUserId, lsTaskType, lsProviderName,
					lsProcurementTitle, lsProgramName, lsStatus, lsSubmittedFrom, lsSubmittedTo, lsDateAssignedFrom,
					lsDateAssignedTo, lsAssignedTo, lsChoosenTab, lsCompPoolTtile);
			loAgencyTaskBean.setPaginationNum(liPageNum);
			ApplicationSession.setAttribute(loAgencyTaskBean, aoRequest, HHSConstants.FILTER_RETAINED);
			aoResponse.setRenderParameter(HHSConstants.FILTER_CHECKED, HHSConstants.DISPLAY_BLOCK);
			// Start of Changes done for enhancement QC : 5688
			if (null != lsControllerAction && !lsControllerAction.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.CONTROLLER_ACTION, HHSConstants.AGENCY_WORKFLOW_FOR_CITY);
			}
			// End of Changes done for enhancement QC : 5688
		}
		// handling Application Exception thrown by transaction layer
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occurred while filtering agency inbox", aoAppEx);
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE,
					HHSConstants.ERROR_WHILE_PROCESSING_REQUEST);
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
	}

	/**
	 * This method set the paging index in response Modification done as a part
	 * of Enhancement #5688 for Release 3.2.0
	 * 
	 * @param aoRequest request object
	 * @param aoResponse response object
	 */
	@ActionMapping(params = "submit_action=agencyPagination")
	protected void agencyPaginationAction(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		String lsChoosenTab = aoRequest.getParameter(ApplicationConstants.CHOOSEN_TAB);
		String lsControllerAction = aoRequest.getParameter(HHSConstants.CONTROLLER_ACTION);// added
																							// 5688
		try
		{
			aoResponse.setRenderParameter(ApplicationConstants.CHOOSEN_TAB, lsChoosenTab);
			AgencyTaskBean loAgencyTaskBean = null;
			int liPageNum = HHSConstants.INT_ONE;
			String lsPageIndex = aoRequest.getParameter(HHSConstants.NEXT_PAGE);
			if (null != lsPageIndex)
			{
				liPageNum = Integer.parseInt(lsPageIndex);
			}
			loAgencyTaskBean = (AgencyTaskBean) ApplicationSession.getAttribute(aoRequest, Boolean.TRUE,
					HHSConstants.FILTER_RETAINED);
			loAgencyTaskBean.setPaginationNum(liPageNum);
			ApplicationSession.setAttribute(loAgencyTaskBean, aoRequest, HHSConstants.FILTER_RETAINED);
			aoResponse.setRenderParameter(HHSConstants.FILTER_CHECKED, HHSConstants.DISPLAY_BLOCK);
			// Start of Changes done for enhancement QC : 5688
			if (null != lsControllerAction && !lsControllerAction.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.CONTROLLER_ACTION, HHSConstants.AGENCY_WORKFLOW_FOR_CITY);
			}
			// End of Changes done for enhancement QC : 5688
		}
		// handling Exception other than Application Exception
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occurred while paging index", aoEx);
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE,
					HHSConstants.ERROR_WHILE_PROCESSING_REQUEST);
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
	}

	/**
	 * This method get the sort detail by calling 'getSortDetailsFromXML' method
	 * and set sorting attribute in request Modification done as a part of
	 * Enhancement #5688 for Release 3.2.0
	 * 
	 * @param aoRequest Request Object
	 * @param aoResponse Response Object
	 */
	@ActionMapping(params = "submit_action=agencySorting")
	protected void agencySortingAction(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		String lsChoosenTab = aoRequest.getParameter(ApplicationConstants.CHOOSEN_TAB);
		String lsControllerAction = aoRequest.getParameter(HHSConstants.CONTROLLER_ACTION);// added
																							// 5688
		aoResponse.setRenderParameter(ApplicationConstants.CHOOSEN_TAB, lsChoosenTab);

		AgencyTaskBean loAgencyTaskBean = null;
		int liPageNum = HHSConstants.INT_ONE;
		try
		{
			String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			loAgencyTaskBean = (AgencyTaskBean) ApplicationSession.getAttribute(aoRequest, Boolean.TRUE,
					HHSConstants.FILTER_RETAINED);
			String lsSortType = PortalUtil.parseQueryString(aoRequest, HHSConstants.SORT_TYPE);
			String lsColumnName = PortalUtil.parseQueryString(aoRequest, HHSConstants.COLUMN_NAME);
			getSortDetailsFromXML(lsColumnName, lsUserOrgType,
					PortalUtil.parseQueryString(aoRequest, HHSConstants.SORT_GRID_NAME), loAgencyTaskBean, lsSortType);
			loAgencyTaskBean.setPaginationNum(liPageNum);
			ApplicationSession.setAttribute(loAgencyTaskBean, aoRequest, HHSConstants.FILTER_RETAINED);
			aoResponse.setRenderParameter(HHSConstants.FILTER_CHECKED, HHSConstants.DISPLAY_BLOCK);
			// Start of Changes done for enhancement QC : 5688
			if (null != lsControllerAction && !lsControllerAction.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.CONTROLLER_ACTION, HHSConstants.AGENCY_WORKFLOW_FOR_CITY);
			}
			// End of Changes done for enhancement QC : 5688
		}
		// handling Application Exception thrown by transaction layer
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured while sorting Action", aoAppEx);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
		// handling Exception other than Application Exception
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occured while sorting Action", aoEx);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}

	}

	/**
	 * This method get all the Task list for agency Inbox and management on the
	 * This method is updated in build 3.2.0 for enhancement #6361 basis of
	 * filter selected in Filter screen
	 * 
	 * @param aoRequest Request Object
	 * @param aoResponse Response Object
	 * @param aoChannel Channel Object
	 * @param aoUserSession Filenet Session object
	 * @param aoAgencyTaskBean Agency Task Bean
	 * @param asCurrentTab current tab
	 * @return Total task Count
	 * @throws ApplicationException if any exception occurs
	 * 
	 *             calls the transaction 'fetchAgencyTaskList'
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private Integer getTaskListFromFilenet(RenderRequest aoRequest, RenderResponse aoResponse, Channel aoChannel,
			P8UserSession aoUserSession, AgencyTaskBean aoAgencyTaskBean, String asCurrentTab)
			throws ApplicationException
	{
		List<AgencyTaskBean> loAgencyTaskBeanList;
		HashMap loUserMap = null;
		Integer loTaskCount = HHSConstants.INT_ZERO;
		String lsUserOrg = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);
		String lsSessionUserName = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_NAME, PortletSession.APPLICATION_SCOPE);
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsUserRole = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ROLE, PortletSession.APPLICATION_SCOPE);
		aoChannel.setData(HHSConstants.AO_FILENET_SESSION, aoUserSession);
		aoChannel.setData(HHSConstants.AGENCY_TASK_BEAN, aoAgencyTaskBean);
		HHSTransactionManager.executeTransaction(aoChannel, HHSConstants.FETCH_AGENCY_TASK_LIST);
		loAgencyTaskBeanList = (List<AgencyTaskBean>) aoChannel.getData(HHSConstants.AGENCY_TASK_LIST); // <---
																										// add
																										// portal
																										// db
																										// call

		/**** [Start] R3.7.0 enhancement #6361 */
		if (aoAgencyTaskBean.getTaskName().equalsIgnoreCase(HHSConstants.TASK_INVOICE_REVIEW))
			getInvoiceReviewInfo(loAgencyTaskBeanList);
		/**** [End] R3.7.0 enhancement #6361 */

		loTaskCount = (Integer) aoChannel.getData(HHSConstants.AGENCY_TASK_COUNT);
		if (null == loTaskCount)
		{
			loTaskCount = HHSConstants.INT_ZERO;
		}
		loUserMap = creatUserDropDown(aoAgencyTaskBean, lsUserRole, asCurrentTab, lsUserId, lsSessionUserName,
				lsUserOrg);
		// start change for build 3.2.0 for enhancement #6361
		if (null != loUserMap && loUserMap.size() > 0)
		{
			if ((lsUserRole.equalsIgnoreCase(HHSConstants.PROGRAM_STAFF_ROLE)
					|| lsUserRole.equalsIgnoreCase(HHSConstants.ACCO_STAFF_ROLE) || lsUserRole
						.equalsIgnoreCase(HHSConstants.FINANCE_STAFF_ROLE)) && loUserMap.containsKey(lsUserId))
			{
				loUserMap.clear();
				loUserMap.put(lsUserId, lsSessionUserName);
				aoRequest.setAttribute(HHSConstants.ENABLE_BULK_ASSIGN, Boolean.TRUE);
			}
			else if (!(lsUserRole.equalsIgnoreCase(HHSConstants.PROGRAM_STAFF_ROLE)
					|| lsUserRole.equalsIgnoreCase(HHSConstants.ACCO_STAFF_ROLE) || lsUserRole
						.equalsIgnoreCase(HHSConstants.FINANCE_STAFF_ROLE)))
			{
				aoRequest.setAttribute(HHSConstants.ENABLE_BULK_ASSIGN, Boolean.TRUE);
			}

		}
		// End change for build 3.2.0 for enhancement #6361
		aoRequest.setAttribute(HHSConstants.USER_MAP, loUserMap);
		aoRequest.getPortletSession().setAttribute(HHSConstants.SORT_TYPE, aoAgencyTaskBean.getFirstSortType(),
				PortletSession.APPLICATION_SCOPE);
		aoRequest.getPortletSession().setAttribute(HHSConstants.SORT_BY, aoAgencyTaskBean.getSortColumnName(),
				PortletSession.APPLICATION_SCOPE);
		ApplicationSession.setAttribute(loAgencyTaskBeanList, aoRequest, HHSConstants.AGENCY_TASK_ITEM_LIST);

		return loTaskCount;
	}

	/**
	 * This method create filter values on click of filter button in Agency
	 * Inbox and management screens
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * @param asUserRole User Role
	 * @param asAgencyId Agency Id
	 * @param asUserId User Id
	 * @param asTaskType Task Type
	 * @param asProviderName Provider Name
	 * @param asProcurementTitle Procurement Title
	 * @param asProgramName Program Name
	 * @param asStatus Status
	 * @param asSubmittedFrom Submitted From
	 * @param asSubmittedTo Submitted To
	 * @param asDateAssignedFrom Date Assigned from
	 * @param asDateAssignedTo Date Assigned To
	 * @param asAssignedTo Assigned to
	 * @param asCurrentTab current tab
	 * @param asCompPoolTitle Compittion pool title
	 * @return Agency Task Detail been
	 * @throws ApplicationException if any exception occurs
	 */
	private AgencyTaskBean createFilter(String asUserRole, String asAgencyId, String asUserId, String asTaskType,
			String asProviderName, String asProcurementTitle, String asProgramName, String asStatus,
			String asSubmittedFrom, String asSubmittedTo, String asDateAssignedFrom, String asDateAssignedTo,
			String asAssignedTo, String asCurrentTab, String asCompPoolTitle) throws ApplicationException
	{
		HashMap<String, String> loFilterDetails = new HashMap<String, String>();
		AgencyTaskBean loAgencyTaskBean = new AgencyTaskBean();
		if (null != asAgencyId && asAgencyId.equalsIgnoreCase(HHSConstants.USER_CITY))
		{
			asAgencyId = null;
		}

		if (HHSConstants.FINANCIAL_TASK_PROCESS_ID_MAP.containsKey(asTaskType))
		{
			loAgencyTaskBean.setR3Task(HHSConstants.TRUE);
		}
		else
		{
			loAgencyTaskBean.setR3Task(HHSConstants.FALSE);
		}
		if (asUserRole.equalsIgnoreCase(HHSConstants.ACCO_STAFF_ROLE))
		{
			loAgencyTaskBean.setR2TaskSelectAllDisable(HHSConstants.TRUE);
		}
		else
		{
			loAgencyTaskBean.setR2TaskSelectAllDisable(HHSConstants.FALSE);
		}
		if (null != asCurrentTab && !asCurrentTab.equalsIgnoreCase(P8Constants.PROPERTY_PAGE_TASK_MANAGMENT))
		{
			loAgencyTaskBean.setFirstSort(HHSConstants.PROPERTY_PE_ASSIGNED_DATE);
			loAgencyTaskBean.setSortColumnName(HHSConstants.LAST_ASSIGNED);

			loFilterDetails.put(HHSConstants.PROPERTY_PE_ASSIGNED_TO, asUserId);
		}
		if (asTaskType != null && !asTaskType.isEmpty())
		{
			loFilterDetails.put(HHSConstants.PROPERTY_PE_TASK_TYPE, asTaskType);
			loAgencyTaskBean.setTaskName(asTaskType);
		}
		if (asProviderName != null && !asProviderName.isEmpty())
		{
			loFilterDetails.put(HHSConstants.PROPERTY_PE_PROVIDER_NAME, asProviderName);
			loAgencyTaskBean.setProviderName(asProviderName);
		}
		setOtherAgencyAttributes(asAgencyId, asProcurementTitle, asProgramName, asStatus, asSubmittedFrom,
				asSubmittedTo, asDateAssignedFrom, asDateAssignedTo, asAssignedTo, asCompPoolTitle, loFilterDetails,
				loAgencyTaskBean);

		loAgencyTaskBean.setFilterProp(loFilterDetails);
		return loAgencyTaskBean;
	}

	/**
	 * This method is called from method 'createFilter' and sets other agency
	 * attributes
	 * 
	 * @param asAgencyId
	 * @param asProcurementTitle
	 * @param asProgramName
	 * @param asStatus
	 * @param asSubmittedFrom
	 * @param asSubmittedTo
	 * @param asDateAssignedFrom
	 * @param asDateAssignedTo
	 * @param asAssignedTo
	 * @param asCompPoolTitle
	 * @param aoFilterDetails
	 * @param aoAgencyTaskBean
	 * @throws ApplicationException
	 */
	private void setOtherAgencyAttributes(String asAgencyId, String asProcurementTitle, String asProgramName,
			String asStatus, String asSubmittedFrom, String asSubmittedTo, String asDateAssignedFrom,
			String asDateAssignedTo, String asAssignedTo, String asCompPoolTitle,
			HashMap<String, String> aoFilterDetails, AgencyTaskBean aoAgencyTaskBean) throws ApplicationException
	{
		if (asProcurementTitle != null && !asProcurementTitle.isEmpty())
		{
			aoFilterDetails.put(HHSConstants.PROPERTY_PE_PROCUREMENT_TITLE, asProcurementTitle);
			aoAgencyTaskBean.setProcurementTitle(asProcurementTitle);
		}
		if (asProgramName != null && !asProgramName.isEmpty())
		{
			aoFilterDetails.put(HHSConstants.PROPERTY_PE_PROGRAM_NAME, asProgramName);
			aoAgencyTaskBean.setProgramName(asProgramName);
		}
		if (asStatus != null && !asStatus.isEmpty())
		{
			aoFilterDetails.put(HHSConstants.TASK_STATUS, asStatus);
			aoAgencyTaskBean.setStatus(asStatus);
		}
		if (asSubmittedFrom != null && !asSubmittedFrom.isEmpty())
		{
			aoFilterDetails
					.put(HHSConstants.PROPERTY_HMP_SUBMITTED_FROM, HHSUtil.getEpochTimeFromDate(asSubmittedFrom));
			aoAgencyTaskBean.setSubmittedFromDate(asSubmittedFrom);
		}
		if (asSubmittedTo != null && !asSubmittedTo.isEmpty())
		{
			String lsSubmittedToTimeStamp = HHSUtil.getEpochTimeFromDate(asSubmittedTo);
			long loLastMomentOfSubmittedToTimeStamp = Long.parseLong(lsSubmittedToTimeStamp) + 86399;
			aoFilterDetails.put(HHSConstants.PROPERTY_HMP_SUBMITTED_TO,
					String.valueOf(loLastMomentOfSubmittedToTimeStamp));
			aoAgencyTaskBean.setSubmittedToDate(asSubmittedTo);
		}
		if (asDateAssignedFrom != null && !asDateAssignedFrom.isEmpty())
		{
			aoFilterDetails.put(HHSConstants.PROPERTY_HMP_ASSIGNED_FROM,
					HHSUtil.getEpochTimeFromDate(asDateAssignedFrom));
			aoAgencyTaskBean.setAssignedFromDate(asDateAssignedFrom);
		}
		if (asDateAssignedTo != null && !asDateAssignedTo.isEmpty())
		{
			String lsAssignedToTimeStamp = HHSUtil.getEpochTimeFromDate(asDateAssignedTo);
			long loLastMomentOfAssignedToTimeStamp = Long.parseLong(lsAssignedToTimeStamp) + 86399;
			aoFilterDetails.put(HHSConstants.PROPERTY_HMP_ASSIGNED_TO,
					String.valueOf(loLastMomentOfAssignedToTimeStamp));
			aoAgencyTaskBean.setAssignedToDate(asDateAssignedTo);
		}
		if (asAssignedTo != null && !asAssignedTo.isEmpty())
		{
			aoFilterDetails.put(HHSConstants.PROPERTY_PE_ASSIGNED_TO, asAssignedTo);
			aoAgencyTaskBean.setAssignedTo(asAssignedTo);
		}
		if (asAgencyId != null && !asAgencyId.isEmpty())
		{
			aoFilterDetails.put(HHSConstants.PROPERTY_PE_AGENCY_ID, asAgencyId);
			aoAgencyTaskBean.setAgencyId(asAgencyId);
		}
		if (asCompPoolTitle != null && !asCompPoolTitle.isEmpty())
		{
			aoFilterDetails.put(P8Constants.PE_WORKFLOW_COMPETITION_POOL_TITLE, asCompPoolTitle);
			aoAgencyTaskBean.setCompetitionPoolTitle(asCompPoolTitle);
		}
	}

	/**
	 * This method modified as a part of Release 2.6.0 enhancement 5571
	 * 
	 * <ul>
	 * <li>instead of FINAL_RFP_DOC_LIST, sorted list FINAL_SORTED_RFP_DOC_LIST
	 * is being used to display documents of type other than "RFP" and "Addenda"
	 * in ascending order</li>
	 * </ul>
	 * 
	 * This method is executed when an agency user opens Accept Proposal Task
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Modification done as a part of Enhancement #5688 for Release 3.2.0</li>
	 * <li>Fetch required parameters from request like WorkflowId, UserId, User
	 * orgType</li>
	 * <li>Set channel with required parameters for fetching procurement and
	 * task details, RFP documents, proposal Documents, taskHistoryDetails,
	 * public provider and internal comments</li>
	 * <li>Invoke the transaction <b>fetchProposalTaskDetails</b></li>
	 * <li>Set transaction outputs in request</li>
	 * </ul>
	 * 
	 * @param aoRequest - Render Request
	 * @param aoResponse - Render Response
	 * @return ModelAndView containing JSP name on which user has to be
	 *         redirected
	 * 
	 *         calls transaction 'fetchAcceptProposalTaskDetails'
	 * 
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	@RenderMapping(params = "render_action=showAcceptProposalTaskDetails")
	protected ModelAndView showAcceptProposalTaskDetails(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		String lsWobNumber = aoRequest.getParameter(HHSConstants.WOB_NUMBER);
		String lsControllerAction = aoRequest.getParameter(HHSConstants.CONTROLLER_ACTION);
		P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		try
		{
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			HashMap<String, String> loRequiredParamMap = new HashMap<String, String>();
			// fetching procurement and task details
			Channel loChannel = new Channel();
			loChannel.setData(ApplicationConstants.WOB_NUMBER, lsWobNumber);
			loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
			loChannel.setData(ApplicationConstants.KEY_SESSION_USER_ID, lsUserId);
			// fetching RFP documents
			ExtendedDocument loExtendedDocument = new ExtendedDocument();
			loExtendedDocument.setOrganizationType(lsUserOrgType);
			loChannel.setData(HHSConstants.DOCUMENT_BEAN, loExtendedDocument);
			// fetching Task history details
			HashMap loAuditMap = new HashMap();
			loAuditMap.put(HHSConstants.ENTITY_TYPE, HHSConstants.ACCEPT_PROPOSAL);
			loChannel.setData(ApplicationConstants.REQUIRED_PROPS, loAuditMap);
			// fetching internal and provider comments
			TaskDetailsBean loTaskDetailBean = new TaskDetailsBean();
			loTaskDetailBean.setEntityType(HHSConstants.ACCEPT_PROPOSAL);
			loChannel.setData(ApplicationConstants.TASK_DETAILS_BEAN_KEY, loTaskDetailBean);
			loChannel.setData(ApplicationConstants.REQ_PROPS_TASK_HASHMAP, HHSUtil.getTaskPropertiesHashMap());
			loChannel.setData(ApplicationConstants.REQ_PROPS_DOCUMENT, setRequiredParam(loRequiredParamMap));
			// fetching unassigned Acco managers
			List<String> loUserRoleList = new ArrayList<String>();
			loUserRoleList.add(HHSConstants.ACCO_MANAGER_ROLE);
			loChannel.setData(ApplicationConstants.ACCELERATOR_USER_ROLE_LIST, loUserRoleList);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_ACCEPT_PROPOSAL_TASK_DETAILS);
			TaskDetailsBean loTaskDetailsBean = setTaskDetailsBeanProperties(aoRequest, lsWobNumber, loChannel);
			// Check if User Id is same as User Assigned To
			if (null != loTaskDetailsBean && null != loTaskDetailsBean.getAssignedTo()
					&& loTaskDetailsBean.getAssignedTo().equalsIgnoreCase(lsUserId))
			{
				aoRequest.setAttribute(HHSConstants.SCREEN_READ_ONLY, false);
			}
			else
			{
				aoRequest.setAttribute(HHSConstants.SCREEN_READ_ONLY, true);
			}
			String lsErrorMsg = (String) aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
		// handling Application Exception thrown by transaction layer
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured while processing accept proposal task details", aoAppEx);
			setGenericErrorMessage(aoRequest);
		}
		// handling Exception other than Application Exception
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occured while processing accept proposal task details", aoEx);
			setGenericErrorMessage(aoRequest);
		}
		// Start of Changes done for enhancement QC : 5688
		if (null != lsControllerAction && !lsControllerAction.isEmpty())
		{
			return new ModelAndView(HHSConstants.ACCEPT_PROPOSAL_TASK_CITY);
		}
		else
		{
			return new ModelAndView(HHSConstants.ACCEPT_PROPOSAL_TASK_JSP);
		}
		// End of Changes done for enhancement QC : 5688
	}

	/**
	 * Modified as a part of release 3.1.0 for enhancement request 6024 Modified
	 * as a part of release 3.6.0 for enhancement request 6485 for adding custom
	 * labels This method sets the taskdetailsbean properties and is called by
	 * method 'showAcceptProposalTaskDetails'
	 * 
	 * @param aoRequest
	 * @param asWobNumber
	 * @param aoChannel
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	private TaskDetailsBean setTaskDetailsBeanProperties(RenderRequest aoRequest, String asWobNumber, Channel aoChannel)
			throws ApplicationException
	{
		TaskDetailsBean loTaskDetailBean;
		HashMap<String, Object> loTaskDetailMap = (HashMap<String, Object>) aoChannel
				.getData(ApplicationConstants.TASK_DETAIL_MAP);
		Map<String, String> loProposalDetailsMap = (Map<String, String>) aoChannel
				.getData(HHSConstants.PROPOSAL_DETAIL_MAP);
		// made changes for enhancement 6467 release 3.4.0
		Integer loRequiredQuestionDocument = (Integer) aoChannel.getData(HHSConstants.REQUIRED_QUESTION_DOCUMENT);
		TaskDetailsBean loTaskDetailsBean = HHSUtil.getTaskDetailsBeanFromMap(loTaskDetailMap, asWobNumber);
		if (null != loProposalDetailsMap)
		{
			loTaskDetailsBean.setSubmittedDate((String) loProposalDetailsMap.get(HHSConstants.PROP_SUBMIT_DATE));
		}
		aoRequest.setAttribute(ApplicationConstants.TASK_DETAILS_BEAN, loTaskDetailsBean);
		List<ExtendedDocument> loProposalDocumentList = (List<ExtendedDocument>) aoChannel
				.getData(HHSConstants.FINAL_PROPOSAL_DOC_LIST);
		if (null != loProposalDocumentList && !loProposalDocumentList.isEmpty() && null != loProposalDetailsMap)
		{
			ExtendedDocument loExtendedDoc = new ExtendedDocument();
			loExtendedDoc.setRequiredQuesDocCount(loRequiredQuestionDocument);
			loExtendedDoc.setProposalTitle(HHSConstants.PROPOSAL_DETAILS_KEY);
			loExtendedDoc.setDocumentType(HHSConstants.NA_KEY);
			loExtendedDoc.setCustomLabelName(HHSConstants.NA_KEY);
			loExtendedDoc.setIsRequiredDoc(HHSConstants.REQUIRED_FLAG);
			loExtendedDoc.setDocumentId(HHSConstants.NA_KEY);
			loExtendedDoc.setDocumentStatus(loProposalDetailsMap.get(HHSConstants.PREV_PROC_STATUS));
			loExtendedDoc.setModifiedDate(loProposalDetailsMap.get(ApplicationConstants.MODIFIED_DATE));
			loExtendedDoc.setProposalId(loTaskDetailsBean.getProposalId());
			loExtendedDoc.setProcurementId(loTaskDetailsBean.getProcurementId());
			loExtendedDoc.setAssignStatus(loProposalDetailsMap.get(HHSConstants.CURR_PROC_STATUS));
			// Modified as a part of release 3.1.0 for enhancement request 6024
			loExtendedDoc.setQuesVersion(loProposalDetailsMap.get(HHSConstants.VERSION_NUMBER_QC));
			loExtendedDoc.setDocVersion(loProposalDetailsMap.get(HHSConstants.VERSION_NUMBER_DC));
			loExtendedDoc.setEvalGrpQuesVersion(loProposalDetailsMap.get(HHSConstants.EVAL_VERSION_NUMBER_PQC));
			loExtendedDoc.setEvalGrpDocVersion(loProposalDetailsMap.get(HHSConstants.EVAL_VERSION_NUMBER_PDC));
			for (ExtendedDocument loProposalDocument : loProposalDocumentList)
			{
				loProposalDocument.setQuesVersion(loProposalDetailsMap.get(HHSConstants.VERSION_NUMBER_QC));
				loProposalDocument.setDocVersion(loProposalDetailsMap.get(HHSConstants.VERSION_NUMBER_DC));
				loProposalDocument
						.setEvalGrpQuesVersion(loProposalDetailsMap.get(HHSConstants.EVAL_VERSION_NUMBER_PQC));
				loProposalDocument.setEvalGrpDocVersion(loProposalDetailsMap.get(HHSConstants.EVAL_VERSION_NUMBER_PDC));
			}
			loProposalDocumentList.add(0, loExtendedDoc);
			loTaskDetailsBean.setSubmittedDate((String) loProposalDetailsMap.get(HHSConstants.PROP_SUBMIT_DATE));
			aoRequest
					.setAttribute(HHSConstants.PROPOSAL_TASK_STATUS, processProposalTaskStatus(loProposalDocumentList));
		}
		// Get the permitted user list for reassign dropdown
		List<UserBean> loUserBeanList = (List<UserBean>) aoChannel.getData(ApplicationConstants.PERMITTED_USER_LIST);
		aoRequest.setAttribute(ApplicationConstants.TASK_DETAILS_BEAN, loTaskDetailsBean);
		aoRequest.setAttribute(ApplicationConstants.REASSIGN_USER_MAP, HHSUtil.getReassignUserMap(loUserBeanList));
		aoRequest.setAttribute(HHSConstants.WORKFLOW_ID, asWobNumber);
		aoRequest.setAttribute(HHSConstants.TASK_ID, loTaskDetailsBean.getTaskId());
		aoRequest.setAttribute(HHSConstants.PROPOSAL_DOCUMENT_LIST, loProposalDocumentList);
		// below line is modified as a part of Release 2.6.0 enhancement 5571
		// instead of FINAL_RFP_DOC_LIST, sorted list FINAL_SORTED_RFP_DOC_LIST
		// is being used,
		// to display documents of type other than "RFP" and "Addenda" in
		// ascending order
		aoRequest.setAttribute(HHSConstants.RFP_DOCUMENT_LIST,
				aoChannel.getData(HHSConstants.FINAL_SORTED_RFP_DOC_LIST));

        // Start QC9665 R 9.3.2 Citywide cyber assessment program (CCAP) - vulnerabilities -- Users Can Access Other User's Uploaded Documents
		List<ExtendedDocument>  loRFPdocLst = (List<ExtendedDocument>) aoChannel.getData(HHSConstants.FINAL_SORTED_RFP_DOC_LIST);
		List<ExtendedDocument>  loDocumentList = ListUtils.union(  loRFPdocLst,  loProposalDocumentList );
		aoRequest.getPortletSession().setAttribute(ApplicationConstants.SESSION_EXTENDED_DOCUMENT_LIST, loDocumentList, PortletSession.APPLICATION_SCOPE);
		LOG_OBJECT.Info("save Document List in Session on Application scope");
		//End QC9665 R 9.3.2 Citywide cyber assessment program (CCAP) - vulnerabilities -- Users Can Access Other User's Uploaded Documents

		aoRequest.setAttribute(ApplicationConstants.TASK_HISTORY_LIST,
				aoChannel.getData(ApplicationConstants.TASK_HISTORY_LIST));
		loTaskDetailBean = (TaskDetailsBean) aoChannel.getData(HHSConstants.AS_TASK_COMMENT);
		aoRequest.setAttribute(HHSConstants.PROVIDER_COMMENTS, loTaskDetailBean.getProviderComment());
		aoRequest.setAttribute(ApplicationConstants.INTERNAL_COMMENTS, loTaskDetailBean.getInternalComment());
		return loTaskDetailsBean;
	}

	/**
	 * This method will fetch the details of the selected document and display
	 * the properties to the end user This method will execute the method
	 * <b>actionViewDocumentInfo</b> method of <b>FileNetOperationsUtils</b> and
	 * the above mention method will execute one R1 transaction with the
	 * transaction id <b>displayDocProp_filenet</b> class and then set the
	 * render action for document view Modification done as a part of
	 * Enhancement #5688 for Release 3.2.0
	 * 
	 * @param aoRequest - ActionRequest Object
	 * @param aoResponse - ActionResponse Object
	 */
	@Override
	@ActionMapping(params = "submit_action=viewDocumentInfo")
	protected void viewDocumentInfoAction(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		String lsControllerAction = aoRequest.getParameter(HHSConstants.CONTROLLER_ACTION);
		// Start || Changes done for enhancement QC : 5688 for Release 3.2.0
		String lsUserOrgType = null;
		String lsViewDocInfoFromAgency = aoRequest.getParameter(HHSConstants.IS_VIEW_DOC_INFO_FROM_AGENCY);
		if (lsViewDocInfoFromAgency != null)
		{
			aoRequest.setAttribute(HHSConstants.ORGTYPE, ApplicationConstants.PROVIDER_ORG);
			lsUserOrgType = ApplicationConstants.PROVIDER_ORG;
		}
		else
		{
			lsUserOrgType = aoRequest.getParameter(HHSConstants.ORGTYPE);
		}
		// End || Changes done for enhancement QC : 5688 for Release 3.2.0
		try
		{
			FileNetOperationsUtils.viewDocumentInformation(aoRequest, aoResponse, lsUserOrgType);
		}
		// handling Application Exception thrown by transaction layer
		catch (ApplicationException aoAppEx)
		{
			String lsOrgType = aoRequest.getParameter(HHSConstants.ORGTYPE);
			Map<String, String> loParamMap = new HashMap<String, String>();
			loParamMap.put(HHSConstants.ORGTYPE, lsOrgType);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap, aoAppEx);
		}
		// handling Exception other than Application Exception
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("ApplicationException during view document properties", aoEx);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
		// Start of Changes done for enhancement QC : 5688
		if (null != lsControllerAction && !lsControllerAction.isEmpty())
		{
			aoResponse.setRenderParameter(HHSConstants.CONTROLLER_ACTION, HHSConstants.AGENCY_WORKFLOW_FOR_CITY);
		}
		// End of Changes done for enhancement QC : 5688
		aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, ApplicationConstants.VIEW_DOCUMENT_INFO);
	}

	/**
	 * This method will set the document object bean into request and render
	 * forward the user to the view document info screen with all the details
	 * which later displayed to the user. Modification done as a part of
	 * Enhancement #5688 for Release 3.2.0
	 * 
	 * @param aoRequest RenderRequest Object
	 * @param aoResponse RenderResponse Object
	 * @return ModelAndView Object with details where to navigate the user
	 */
	@Override
	@RenderMapping(params = "render_action=viewDocumentInfo")
	protected ModelAndView viewDocumentInfoRender(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		String lsControllerAction = aoRequest.getParameter(HHSConstants.CONTROLLER_ACTION);
		aoRequest.setAttribute(ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_PARAMETER,
				(Document) ApplicationSession.getAttribute(aoRequest, true, ApplicationConstants.SESSION_DOCUMENT_OBJ));
		// Start of Changes done for enhancement QC : 5688
		if (null != lsControllerAction && !lsControllerAction.isEmpty())
		{
			return new ModelAndView(HHSConstants.VIEW_DOC_FOR_ACCELERATOR);
		}
		// End of Changes done for enhancement QC : 5688
		else
		{
			return new ModelAndView(HHSConstants.VIEW_DOCUMENT_INFO_JSP);
		}
	}

	/**
	 * This method handles action when save button is clicked from S248 screen
	 * 
	 * <ul>
	 * <li>Get request and session parameters like: userId, workflowId,taskId,
	 * proposalId</li>
	 * <li>If provider and internal comments are not null, add audit bean object
	 * by calling method getBeanForSavingUserComments() for saving user comments
	 * </li>
	 * <li>Get extended document bean object if document status is changed by
	 * calling method processProposalDocumentDetails() and set it to channel</li>
	 * <li>Execute transaction with Id "saveAcceptProposalTaskDetails"</li></li>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse object
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@ActionMapping(params = "submit_action=saveAcceptProposalTaskDetails")
	protected void saveAcceptProposalTaskDetails(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsUserOrg = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);
		String lsWorkflowId = aoRequest.getParameter(HHSConstants.WORKFLOW_ID);
		String lsTaskId = aoRequest.getParameter(HHSConstants.TASK_ID);
		String lsProposalId = aoRequest.getParameter(HHSConstants.PROPOSAL_ID);
		String lsProcurementId = aoRequest.getParameter(HHSConstants.PROCUREMENT_ID);
		String lsProposalTaskStatus = aoRequest.getParameter(HHSConstants.PROPOSAL_TASK_STATUS);
		try
		{
			P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			// Get provider and internal comments
			String lsProviderComments = aoRequest.getParameter(HHSConstants.PROVIDER_COMMENTS);
			String lsInternalComments = aoRequest.getParameter(ApplicationConstants.INTERNAL_COMMENTS);
			// Get assigned status values
			String[] loAssignedStatusArray = aoRequest.getParameterValues(HHSConstants.ASSIGNED_STATUS);
			Channel loChannel = new Channel();
			List<ExtendedDocument> loExtendedList = null;
			List<HhsAuditBean> loUserCommentsList = null;
			// Check for null value of provider and internal comments
			if ((null != lsProviderComments && !lsProviderComments.isEmpty())
					|| (null != lsInternalComments && !lsInternalComments.isEmpty()))
			{
				loUserCommentsList = new ArrayList<HhsAuditBean>();
				// get bean for saving comments in user comments table
				HhsAuditBean loHhsAuditBean = HHSUtil.getBeanForSavingUserComments(lsTaskId, lsWorkflowId,
						HHSConstants.ACCEPT_PROPOSAL, lsProposalId, lsUserId, lsUserOrg, lsProviderComments,
						lsInternalComments);
				loUserCommentsList.add(loHhsAuditBean);
				loChannel.setData(ApplicationConstants.AUDIT_BEAN_LIST, loUserCommentsList);
			}
			// check for assigned status values
			if (null != loAssignedStatusArray && loAssignedStatusArray.length > 0)
			{
				// Get extended document list for proposal status and proposal
				// document status
				loExtendedList = processProposalDocumentDetails(lsProposalId, loAssignedStatusArray,
						lsProposalTaskStatus, lsProcurementId, lsUserId);
				if (null != loExtendedList && !loExtendedList.isEmpty())
				{
					loChannel.setData(HHSConstants.PROPOSAL_DOC_DETAILS, loExtendedList);
				}
			}
			// execute transaction if user comments or extended documents are
			// not null
			if (null != loExtendedList || null != loUserCommentsList)
			{
				HashMap loHmWFProperties = new HashMap();
				loHmWFProperties.put(P8Constants.PE_WORKFLOW_LAST_MODIFIED_DATE, new Date());
				loChannel.setData(HHSConstants.REQ_PROPS_HASHMAP, loHmWFProperties);
				loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
				loChannel.setData(HHSConstants.WORKFLOW_ID, lsWorkflowId);
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.SAVE_ACCEPT_PROPOSAL_TASK_DETAILS);
			}
		}
		// handling exception thrown by transaction layer.
		catch (ApplicationException aoAppEx)
		{
			Map<String, String> loParamMap = new HashMap<String, String>();
			loParamMap.put(ApplicationConstants.KEY_SESSION_USER_ID, lsUserId);
			loParamMap.put(ApplicationConstants.KEY_SESSION_USER_ORG, lsUserOrg);
			loParamMap.put(HHSConstants.WORKFLOW_ID, lsWorkflowId);
			loParamMap.put(HHSConstants.TASK_ID, lsTaskId);
			loParamMap.put(HHSConstants.PROPOSAL_ID, lsProposalId);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap, aoAppEx);
			LOG_OBJECT.Error("Exception Occured while saving accept proposal task details: ", aoAppEx);
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while saving accept proposal task details: ", aoEx);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
		aoResponse.setRenderParameter(HHSConstants.WOB_NUMBER, lsWorkflowId);
		aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.SHOW_ACCEPT_PROPOSAL_TASK_DETAILS);
	}

	/**
	 * This method process Assigned Status array and generate extended document
	 * bean object if there is any status change
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Get the assigned status array and iterate it</li>
	 * <li>Get the document name, previous status, changed status and document
	 * name by splitting status value</li>
	 * <li>If previous status and changed status are different, call
	 * getDocumentBeanForStatusUpdate() to create extended document bean object
	 * and add it to extended document list</li>
	 * </ul>
	 * 
	 * @param asProposalId a string value of proposal Id
	 * @param aoAssignedStatusArray assigned status array
	 * @param asTaskStatus a string value of task status
	 * @param asProcurementId a string value of procurement id
	 * @param asUserId a string value of user id
	 * @return a list extended document
	 * @throws ApplicationException If an Application Exception occurs
	 */
	private List<ExtendedDocument> processProposalDocumentDetails(String asProposalId, String[] aoAssignedStatusArray,
			String asTaskStatus, String asProcurementId, String asUserId) throws ApplicationException
	{
		List<ExtendedDocument> loExtendedList = null;
		if (null != aoAssignedStatusArray)
		{
			loExtendedList = new ArrayList<ExtendedDocument>();
			// Iterate through assigned status array
			for (String lsAssignedStatus : aoAssignedStatusArray)
			{
				String lsDelimiter = HHSConstants.DELIMITER_DOUBLE_HASH;
				String[] loBreakStr = null;
				String lsStatusAfterSplit = null;
				// split values using delimiter
				loBreakStr = lsAssignedStatus.split(lsDelimiter);
				if (loBreakStr.length < 3)
				{
					lsStatusAfterSplit = HHSConstants.EMPTY_STRING;
					continue;
				}
				String lsDocSectionName = loBreakStr[0];
				lsStatusAfterSplit = loBreakStr[1];
				String lsStatusChanged = HHSConstants.EMPTY_STRING;
				// For Proposal Documents section, set status Ids for different
				// document status
				if (null != lsDocSectionName && !lsDocSectionName.equalsIgnoreCase(HHSConstants.PROPOSAL_DETAILS_KEY))
				{
					if (lsStatusAfterSplit.equalsIgnoreCase(HHSConstants.STATUS_VERIFIED))
					{
						lsStatusChanged = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.DOCUMENT_VERIFIED_KEY);
					}
					else if (lsStatusAfterSplit.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED))
					{
						lsStatusChanged = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.DOCUMENT_RETURNED_KEY);
					}
					else if (lsStatusAfterSplit.equalsIgnoreCase(HHSConstants.STATUS_NON_RESPONSIVE))
					{
						lsStatusChanged = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.DOCUMENT_NON_RESPONSIVE_KEY);
					}
				}
				// For Proposal Details section, set status Ids for
				// proposal status
				else if (null != lsDocSectionName
						&& lsDocSectionName.equalsIgnoreCase(HHSConstants.PROPOSAL_DETAILS_KEY))
				{
					if (lsStatusAfterSplit.equalsIgnoreCase(HHSConstants.STATUS_VERIFIED))
					{
						lsStatusChanged = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.PROPOSAL_ACCEPTED_FOR_EVALUATION_KEY);
					}
					else if (lsStatusAfterSplit.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED))
					{
						lsStatusChanged = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_PROPOSAL_RETURNED_FOR_REVISION);
					}
					else if (lsStatusAfterSplit.equalsIgnoreCase(HHSConstants.STATUS_NON_RESPONSIVE))
					{
						lsStatusChanged = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_PROPOSAL_NON_RESPONSIVE);
					}
				}
				loExtendedList.add(getDocumentBeanForStatusUpdate(lsDocSectionName, lsStatusChanged, asProposalId,
						asProcurementId, asUserId));
			}
		}
		return loExtendedList;
	}

	/**
	 * This method generates document bean object when status is changed
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * @param asSectionName a string value of section or document name
	 * @param asStatusChanged a string value of changed status
	 * @param asProposalId a string value of proposal Id
	 * @param asProcurementId a string value of procurement Id
	 * @param asModifiedUserId a string value of modified by user Id
	 * @return extended document object
	 */
	private ExtendedDocument getDocumentBeanForStatusUpdate(String asSectionName, String asStatusChanged,
			String asProposalId, String asProcurementId, String asModifiedUserId)
	{
		ExtendedDocument loExtendedDocument = new ExtendedDocument();
		loExtendedDocument.setProcurementDocumentId(asSectionName);
		loExtendedDocument.setDocumentStatus(asStatusChanged);
		loExtendedDocument.setProposalId(asProposalId);
		loExtendedDocument.setProposalTitle(asSectionName);
		loExtendedDocument.setProcurementId(asProcurementId);
		loExtendedDocument.setLastModifiedById(asModifiedUserId);
		return loExtendedDocument;
	}

	/**
	 * This method handles action when reassign button is clicked from S248
	 * 
	 * <ul>
	 * <li>Get request and session parameters like: userId, workflowId,taskId,
	 * proposalId, P8UserSession, ReassignedTo, ReassignedToUserName</li>
	 * <li>Set task details in channel for reassigning task</li>
	 * <li>If provider and internal comments are not null, add audit bean object
	 * by calling method addAuditDataToChannel() from HHSUtil</li>
	 * <li>Also If provider and internal comments are not null, add audit bean
	 * object by calling method getBeanForSavingUserComments() for saving
	 * comments in user comments table</li>
	 * <li>Add audit bean object for reassigning task by calling method
	 * addAuditDataToChannel() from HHSUtil</li>
	 * <li>calls the transaction 'reassignWFTask'</LI>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse object
	 */
	@ActionMapping(params = "submit_action=reassignAcceptProposalTask")
	protected void reassignAcceptProposalTask(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		String lsWorkflowId = aoRequest.getParameter(HHSConstants.WORKFLOW_ID);
		String lsTaskId = aoRequest.getParameter(HHSConstants.TASK_ID);
		String lsProposalId = aoRequest.getParameter(HHSConstants.PROPOSAL_ID);
		String lsAssignedToUserId = aoRequest.getParameter(ApplicationConstants.REASSIGNED_TO);
		String lsAssignedToUserName = aoRequest.getParameter(ApplicationConstants.REASSIGNED_TO_USER_NAME);
		try
		{
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			String lsUserOrg = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);
			// Set user Id and user name to which task has been reassigned
			TaskDetailsBean loTaskBean = new TaskDetailsBean();
			loTaskBean.setWorkFlowId(lsWorkflowId);
			loTaskBean.setReassignUserId(lsAssignedToUserId);
			loTaskBean.setReassignUserName(lsAssignedToUserName);
			P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			// save assigned to field for selected user
			Channel loChannel = new Channel();
			loChannel.setData(ApplicationConstants.TASK_DETAILS_BEAN, loTaskBean);
			loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
			// save provider comments to task history
			String lsProviderComments = aoRequest.getParameter(HHSConstants.PROVIDER_COMMENTS);
			String lsInternalComments = aoRequest.getParameter(ApplicationConstants.INTERNAL_COMMENTS);
			List<HhsAuditBean> loAuditBeanList = new ArrayList<HhsAuditBean>();
			if (null != lsProviderComments && !lsProviderComments.equals(HHSConstants.EMPTY_STRING))
			{
				loAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSConstants.PROVIDER_COMMENTS_DATA,
						HHSConstants.ACCEPT_PROPOSAL, lsProviderComments, HHSConstants.ACCEPT_PROPOSAL, lsProposalId,
						lsUserId, HHSConstants.AGENCY_AUDIT));
			}
			if (null != lsInternalComments && !lsInternalComments.equals(HHSConstants.EMPTY_STRING))
			{
				loAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSConstants.AGENCY_COMMENTS_DATA,
						HHSConstants.ACCEPT_PROPOSAL, lsInternalComments, HHSConstants.ACCEPT_PROPOSAL, lsProposalId,
						lsUserId, HHSConstants.AGENCY_AUDIT));
			}
			// save provider and agency comments to user comments
			if ((null != lsProviderComments && !lsProviderComments.equals(HHSConstants.EMPTY_STRING))
					|| (null != lsInternalComments && !lsInternalComments.equals(HHSConstants.EMPTY_STRING)))
			{
				loAuditBeanList.add(HHSUtil.getBeanForSavingUserComments(lsTaskId, lsWorkflowId,
						HHSConstants.ACCEPT_PROPOSAL, lsProposalId, lsUserId, lsUserOrg, lsProviderComments,
						lsInternalComments));
			}
			loAuditBeanList.add(HHSUtil.addAuditDataToChannel(ApplicationConstants.TASK_ASSIGNMENT,
					HHSConstants.ACCEPT_PROPOSAL, ApplicationConstants.TASK_ASSIGNED_TO + HHSConstants.COLON_AOP
							+ lsAssignedToUserName, HHSConstants.ACCEPT_PROPOSAL, lsProposalId, lsUserId,
					HHSConstants.AGENCY_AUDIT));
			loChannel.setData(ApplicationConstants.AUDIT_BEAN_LIST, loAuditBeanList);
			HHSTransactionManager.executeTransaction(loChannel, ApplicationConstants.REASSIGN_WF_TASK);
		}
		// handling exception other than Application Exception.
		catch (ApplicationException loExp)
		{
			Map<String, String> loParamMap = new HashMap<String, String>();
			loParamMap.put(HHSConstants.WORKFLOW_ID, lsWorkflowId);
			loParamMap.put(HHSConstants.TASK_ID, lsTaskId);
			loParamMap.put(HHSConstants.PROPOSAL_ID, lsProposalId);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap, loExp);

		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception Occured while reassigning accept proposal task details: ", loExp);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
		aoResponse.setRenderParameter(HHSConstants.RETURN_TO_AGENCY_TASK, HHSConstants.TRUE);
	}

	/**
	 * This method handles action when finish button is clicked from S243
	 * 
	 * <ul>
	 * <li>Get request and session parameters like: userId, workflowId,taskId,
	 * proposalId, P8UserSession</li>
	 * <li>Get process task status Id based on proposal task status from request
	 * </li>
	 * <li>Get notification map by calling method
	 * getNotificationMapForProposalTask() and set in channel</li>
	 * <li>If provider and internal comments are not null, add audit bean object
	 * by calling method addAuditDataToChannel() from HHSUtil</li>
	 * <li>Get audit bean object if document status is changed by calling method
	 * getAuditBeanForStatusChangedRecords() and set it to channel</li>
	 * <li>Set task details in channel and execute transaction with Id
	 * "finishAcceptProposalTask"</li>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse object
	 */
	@ActionMapping(params = "submit_action=finishAcceptProposalTask")
	protected void finishAcceptProposalTask(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		String lsProposalTaskStatus = aoRequest.getParameter(HHSConstants.PROPOSAL_TASK_STATUS);
		String lsProposalId = aoRequest.getParameter(HHSConstants.PROPOSAL_ID);
		String lsWorkflowId = aoRequest.getParameter(HHSConstants.WORKFLOW_ID);
		String lsTaskId = aoRequest.getParameter(HHSConstants.TASK_ID);
		try
		{
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			String lsUserOrg = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);
			Channel loChannel = new Channel();
			if (null != lsProposalTaskStatus
					&& (lsProposalTaskStatus.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS) || lsProposalTaskStatus
							.equalsIgnoreCase(HHSConstants.ACCEPTED_FOR_EVALUATION)))
			{
				// update proposal status and proposal document status
				String lsProposalStatusCode = null;
				if (lsProposalTaskStatus.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS))
				{
					lsProposalStatusCode = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
							HHSConstants.STATUS_PROPOSAL_RETURNED_FOR_REVISION);
				}
				else if (lsProposalTaskStatus.equalsIgnoreCase(HHSConstants.ACCEPTED_FOR_EVALUATION))
				{
					lsProposalStatusCode = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
							HHSConstants.PROPOSAL_ACCEPTED_FOR_EVALUATION_KEY);
				}
				loChannel.setData(HHSConstants.PROPOSAL_ID, lsProposalId);
				loChannel.setData(HHSConstants.PROPOSAL_STATUS_ID_KEY, lsProposalStatusCode);
				// save provider comments to task history
				String lsProviderComments = aoRequest.getParameter(HHSConstants.PROVIDER_COMMENTS);
				String lsInternalComments = aoRequest.getParameter(ApplicationConstants.INTERNAL_COMMENTS);
				List<HhsAuditBean> loAuditBeanList = new ArrayList<HhsAuditBean>();
				if (null != lsProviderComments && !lsProviderComments.equals(HHSConstants.EMPTY_STRING))
				{
					loAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSConstants.PROVIDER_COMMENTS_DATA,
							HHSConstants.ACCEPT_PROPOSAL, lsProviderComments, HHSConstants.ACCEPT_PROPOSAL,
							lsProposalId, lsUserId, HHSConstants.AGENCY_AUDIT));
				}
				if (null != lsInternalComments && !lsInternalComments.equals(HHSConstants.EMPTY_STRING))
				{
					loAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSConstants.AGENCY_COMMENTS_DATA,
							HHSConstants.ACCEPT_PROPOSAL, lsInternalComments, HHSConstants.ACCEPT_PROPOSAL,
							lsProposalId, lsUserId, HHSConstants.AGENCY_AUDIT));
				}
				// get bean for saving comments in user comments table
				loAuditBeanList.add(HHSUtil.getBeanForSavingUserComments(lsTaskId, lsWorkflowId,
						HHSConstants.ACCEPT_PROPOSAL, lsProposalId, lsUserId, lsUserOrg, lsProviderComments,
						lsInternalComments));
				StringBuffer loDataSb = new StringBuffer();
				loDataSb.append(HHSConstants.STATUS_CHANGED_FROM).append(HHSConstants.DOUBLE_QUOTE)
						.append(HHSConstants.TASK_IN_REVIEW).append(HHSConstants.STR).append(HHSConstants._TO_)
						.append(HHSConstants.STR).append(lsProposalTaskStatus).append(HHSConstants.STR);
				loAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSConstants.STATUS_CHANGE,
						HHSConstants.ACCEPT_PROPOSAL, loDataSb.toString(), HHSConstants.ACCEPT_PROPOSAL, lsProposalId,
						lsUserId, HHSConstants.AGENCY_AUDIT));
				// save status change records to task history
				loChannel.setData(ApplicationConstants.AUDIT_BEAN_LIST,
						getAuditBeanForStatusChangedRecords(aoRequest, loAuditBeanList, lsUserId, lsProposalId));
				// save task details in filenet
				TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
				loTaskDetailsBean.setTaskId(lsTaskId);
				loTaskDetailsBean.setWorkFlowId(lsWorkflowId);
				loTaskDetailsBean.setTaskStatus(lsProposalTaskStatus);
				loChannel.setData(ApplicationConstants.TASK_DETAILS_BEAN, loTaskDetailsBean);
				loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
				// Start R5 : set EntityId and EntityName for AutoSave
				CommonUtil.setChannelForAutoSaveData(loChannel, lsWorkflowId, HHSR5Constants.TASKS);
				// End R5 : set EntityId and EntityName for AutoSave
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FINISH_ACCEPT_PROPOSAL_TASK);
			}
		}
		// handling Application Exception thrown by transaction layer
		catch (ApplicationException aoAppEx)
		{
			Map<String, String> loParamMap = new HashMap<String, String>();
			loParamMap.put(HHSConstants.WORKFLOW_ID, lsWorkflowId);
			loParamMap.put(HHSConstants.TASK_ID, lsTaskId);
			loParamMap.put(HHSConstants.PROPOSAL_ID, lsProposalId);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap, aoAppEx);
			LOG_OBJECT.Error("Exception Occured while finishing accept proposal task details: ", aoAppEx);
		}
		// handling Exception other than Application Exception
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while finishing accept proposal task details: ", aoEx);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);

		}
		aoResponse.setRenderParameter(HHSConstants.RETURN_TO_AGENCY_TASK, HHSConstants.TRUE);
	}

	/**
	 * 
	 * This method is modified to resolve
	 * "BEA-000000-Could not complete request" error in AdminServer logs
	 * 
	 * <ul>
	 * <li>call to method "renderProcurementSuccessMessage"nothing is commented
	 * as nothing returned in Model and View which was causing BEA-000000 error</li>
	 * </ul>
	 * 
	 * This method handles action when mark non-responsive button is clicked
	 * from S258
	 * 
	 * <ul>
	 * <li>Get request and session parameters like: userId, workflowId,
	 * proposalId, P8UserSession</li>
	 * <li>Get notification map by calling method
	 * getNotificationMapForProposalTask() and set in channel</li>
	 * <li>If provider and internal comments are not null, add audit bean object
	 * by calling method addAuditDataToChannel() from HHSUtil</li>
	 * <li>Get audit bean object if document status is changed by calling method
	 * getAuditBeanForStatusChangedRecords() and set it to channel</li>
	 * <li>Set task details in channel and execute transaction with Id
	 * "finishAcceptProposalTask"</li>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse object
	 */
	@ActionMapping(params = "submit_action=markProposalNonResponsive")
	protected void markProposalNonResponsive(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsProposalId = aoRequest.getParameter(HHSConstants.PROPOSAL_ID);
		String lsWorkflowId = aoRequest.getParameter(HHSConstants.WORKFLOW_ID);
		String lsTaskId = aoRequest.getParameter(HHSConstants.TASK_ID);
		P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		String lsUserOrg = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);
		try
		{
			String lsProposalStatusCode = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_PROPOSAL_NON_RESPONSIVE);
			List<HhsAuditBean> loAuditBeanList = new ArrayList<HhsAuditBean>();
			Channel loChannel = new Channel();
			loChannel.setData(HHSConstants.PROPOSAL_ID, lsProposalId);
			loChannel.setData(HHSConstants.PROPOSAL_STATUS_ID_KEY, lsProposalStatusCode);
			// save provider comments to task history
			String lsProviderComments = aoRequest.getParameter(HHSConstants.PROVIDER_COMMENTS);
			String lsInternalComments = aoRequest.getParameter(ApplicationConstants.INTERNAL_COMMENTS);
			if (null != lsProviderComments && !lsProviderComments.equals(HHSConstants.EMPTY_STRING))
			{
				loAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSConstants.PROVIDER_COMMENTS_DATA,
						HHSConstants.ACCEPT_PROPOSAL, lsProviderComments, HHSConstants.ACCEPT_PROPOSAL, lsProposalId,
						lsUserId, HHSConstants.AGENCY_AUDIT));
			}
			if (null != lsInternalComments && !lsInternalComments.equals(HHSConstants.EMPTY_STRING))
			{
				loAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSConstants.AGENCY_COMMENTS_DATA,
						HHSConstants.ACCEPT_PROPOSAL, lsInternalComments, HHSConstants.ACCEPT_PROPOSAL, lsProposalId,
						lsUserId, HHSConstants.AGENCY_AUDIT));
			}
			// get bean for saving comments in user comments table
			loAuditBeanList.add(HHSUtil.getBeanForSavingUserComments(lsTaskId, lsWorkflowId,
					HHSConstants.ACCEPT_PROPOSAL, lsProposalId, lsUserId, lsUserOrg, lsProviderComments,
					lsInternalComments));
			// save status change records to task history
			loChannel.setData(ApplicationConstants.AUDIT_BEAN_LIST,
					getAuditBeanForStatusChangedRecords(aoRequest, loAuditBeanList, lsUserId, lsProposalId));
			// save task details in filenet
			TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
			loTaskDetailsBean.setTaskId(aoRequest.getParameter(HHSConstants.TASK_ID));
			loTaskDetailsBean.setWorkFlowId(lsWorkflowId);
			loTaskDetailsBean.setTaskStatus(aoRequest.getParameter(HHSConstants.PROPOSAL_TASK_STATUS));
			loChannel.setData(ApplicationConstants.TASK_DETAILS_BEAN, loTaskDetailsBean);
			loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FINISH_ACCEPT_PROPOSAL_TASK);
		}
		// handling Application Exception thrown by transaction layer
		catch (ApplicationException loExp)
		{
			Map<String, String> loParamMap = new HashMap<String, String>();
			loParamMap.put(ApplicationConstants.KEY_SESSION_USER_ID, lsUserId);
			loParamMap.put(ApplicationConstants.KEY_SESSION_USER_ORG, lsUserOrg);
			loParamMap.put(HHSConstants.WORKFLOW_ID, lsWorkflowId);
			loParamMap.put(HHSConstants.TASK_ID, lsTaskId);
			loParamMap.put(HHSConstants.PROPOSAL_ID, lsProposalId);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap, loExp);

		}
		// handling Exception other than Application Exception
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception Occured while finishing accept proposal task details: ", loExp);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);

		}
	}

	/**
	 * This method process Assigned Status array and generate audit bean object
	 * if there is any status change
	 * 
	 * <ul>
	 * <li>Get the assigned status array and iterate it</li>
	 * <li>Get the document name, previous status, changed status and document
	 * name by splitting status value</li>
	 * <li>If previous status and changed status are different, call
	 * addAuditDataToChannel() of HHSUtil to create audit bean object and add it
	 * to input task history list</li>
	 * </ul>
	 * 
	 * @param aoRequest an Action Request object
	 * @param aoAuditBeanList list of audit bean for task history
	 * @param asUserId a string value of user Id
	 * @param asProposalId a string value of proposal Id
	 * @return modified list of audit bean for task history
	 */
	private List<HhsAuditBean> getAuditBeanForStatusChangedRecords(ActionRequest aoRequest,
			List<HhsAuditBean> aoAuditBeanList, String asUserId, String asProposalId)
	{
		String[] loAssignedStatusArray = aoRequest.getParameterValues(HHSConstants.ASSIGNED_STATUS);
		if (null != loAssignedStatusArray)
		{
			for (String loAssignedStatus : loAssignedStatusArray)
			{
				String lsDelimiter = HHSConstants.DELIMITER_DOUBLE_HASH;
				String[] loBreakStr = null;
				String lsStatusAfterSplit = null;
				loBreakStr = loAssignedStatus.split(lsDelimiter);
				if (loBreakStr.length < 3)
				{
					continue;
				}
				String lsDocSectionName = loBreakStr[0];
				lsStatusAfterSplit = loBreakStr[1];
				String lsPrevCurrentStatus = loBreakStr[2];
				if (null == lsStatusAfterSplit || lsStatusAfterSplit.equals(HHSConstants.EMPTY_STRING)
						|| lsPrevCurrentStatus.equalsIgnoreCase(lsStatusAfterSplit))
				{
					continue;
				}
				if (null != lsDocSectionName && lsDocSectionName.equalsIgnoreCase(HHSConstants.PROPOSAL_DETAILS_KEY))
				{
					lsDocSectionName = HHSConstants.ACCEPT_PROPOSAL + HHSConstants.COLON_AOP
							+ HHSConstants.PROPOSAL_DETAILS_KEY;
				}
				else
				{
					lsDocSectionName = HHSConstants.ACCEPT_PROPOSAL + HHSConstants.COLON_AOP + loBreakStr[3];
				}
				StringBuffer loDataSb = new StringBuffer();
				loDataSb.append(HHSConstants.STATUS_CHANGED_FROM).append(HHSConstants.DOUBLE_QUOTE)
						.append(lsPrevCurrentStatus).append(HHSConstants.STR).append(HHSConstants._TO_)
						.append(HHSConstants.STR).append(lsStatusAfterSplit).append(HHSConstants.STR);
				aoAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSConstants.STATUS_CHANGE, lsDocSectionName,
						loDataSb.toString(), HHSConstants.ACCEPT_PROPOSAL, asProposalId, asUserId,
						HHSConstants.AGENCY_AUDIT));
			}
		}
		return aoAuditBeanList;
	}

	/**
	 * This method processes accept proposal task depending upon status of
	 * proposal summary and proposal documents submitted by provider
	 * 
	 * <ul>
	 * <li>Get the proposal document list</li>
	 * <li>Set default proposal task status as "In Review"</li>
	 * <li>If list size is greater than 0, get the status list</li>
	 * <li>Check if list of status contains any of the status values
	 * ("Returned","Non-Responsive","Verified")</li>
	 * <li>Set proposal task status value accordingly</li>
	 * </ul>
	 * 
	 * @param aoProposalDocStatusList a list of proposal document and proposal
	 *            summary
	 * @return a string value of accept proposal task
	 */
	private String processProposalTaskStatus(List<ExtendedDocument> aoProposalDocStatusList)
	{
		String lsProposalTaskStatus = HHSConstants.TASK_IN_REVIEW;
		List<String> loStatusList = new ArrayList<String>();
		if (null != aoProposalDocStatusList && !aoProposalDocStatusList.isEmpty())
		{
			int liStatusCounter = 0;
			for (ExtendedDocument loDocObj : aoProposalDocStatusList)
			{
				if ((null != loDocObj.getAssignStatus() && !loDocObj.getAssignStatus().equalsIgnoreCase(
						HHSConstants.STATUS_SUBMITTED)))
				{
					loStatusList.add(loDocObj.getAssignStatus());
					liStatusCounter++;
				}
			}
			if (liStatusCounter == aoProposalDocStatusList.size())
			{
				if (loStatusList.contains(ApplicationConstants.STATUS_RETURNED))
				{
					lsProposalTaskStatus = ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS;
				}
				else if (loStatusList.contains(HHSConstants.STATUS_NON_RESPONSIVE))
				{
					lsProposalTaskStatus = HHSConstants.STATUS_NON_RESPONSIVE;
				}
				else if (loStatusList.contains(HHSConstants.STATUS_VERIFIED))
				{
					lsProposalTaskStatus = HHSConstants.ACCEPTED_FOR_EVALUATION;
				}
			}
		}
		return lsProposalTaskStatus;
	}

	/**
	 * Below method will set the required parameter into the map
	 * 
	 * @param aoParamMap a map containing required parameter values
	 * @return a hashmap of document properties to be fetched from filenet
	 */
	private HashMap<String, String> setRequiredParam(HashMap<String, String> aoParamMap)
	{
		aoParamMap.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, ApplicationConstants.EMPTY_STRING);
		aoParamMap.put(P8Constants.PROPERTY_CE_LAST_MODIFIED_DATE, ApplicationConstants.EMPTY_STRING);
		aoParamMap.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY, ApplicationConstants.EMPTY_STRING);
		// Added for R5- combo box for DocType - fix for Defect id 7270
		aoParamMap.put(P8Constants.PROPERTY_CE_DOC_TYPE, ApplicationConstants.EMPTY_STRING);
		// R5 end
		return aoParamMap;
	}

	/**
	 * This method modified as a part of Release 2.6.0 enhancement 5571
	 * *Modified as a part of release 3.6.0 for enhancement request 6485 for
	 * adding custom labels
	 * <ul>
	 * <li>instead of FINAL_RFP_DOC_LIST, sorted list FINAL_SORTED_RFP_DOC_LIST
	 * is being used to display documents of type other than "RFP" and "Addenda"
	 * in ascending order</li>
	 * <li>Modification done as a part of Enhancement #5688 for Release 3.2.0</li>
	 * </ul>
	 * 
	 * This method is executed when an agency user opens Evaluate Proposal Task
	 * <ul>
	 * <li>1. Fetch required parameters from request(Procurement Id, Proposal
	 * Id, user details etc)</li>
	 * <li>2. Set channel with required parameters</li>
	 * <li>3. Invoke the transaction <b>fetchEvaluateProposalTaskDetails</b></li>
	 * <li>4. Set transaction outputs in request</li>
	 * </ul>
	 * 
	 * @param aoRequest - Render Request
	 * @param aoResponse - Render Response
	 * @return ModelAndView containing JSP name on which user has to be
	 *         redirected
	 */
	@SuppressWarnings("unchecked")
	@RenderMapping(params = "render_action=showEvaluateProposalTaskDetails")
	protected ModelAndView showEvaluateProposalTaskDetails(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		String lsWobNumber = aoRequest.getParameter(HHSConstants.WOB_NUMBER);
		String lsControllerAction = aoRequest.getParameter(HHSConstants.CONTROLLER_ACTION);
		P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
		ScoreDetailsBean loScoreBean = new ScoreDetailsBean();
		// fetching procurement and task details
		Channel loChannel = new Channel();
		loChannel.setData(ApplicationConstants.WOB_NUMBER, lsWobNumber);
		loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
		loChannel.setData(ApplicationConstants.KEY_SESSION_USER_ID, lsUserId);
		// fetching RFP documents
		ExtendedDocument loExtendedDocument = new ExtendedDocument();
		loExtendedDocument.setOrganizationType(lsUserOrgType);
		loChannel.setData(HHSConstants.DOCUMENT_BEAN, loExtendedDocument);
		// fetching internal comments
		TaskDetailsBean loTaskDetailBean = new TaskDetailsBean();
		loTaskDetailBean.setEntityType(HHSConstants.EVALUATOR);
		loTaskDetailBean.setEntityTypeForAgency(HHSConstants.TEMP_EVALUATOR);
		loChannel.setData(ApplicationConstants.TASK_DETAILS_BEAN_KEY, loTaskDetailBean);
		loChannel.setData(ApplicationConstants.REQ_PROPS_TASK_HASHMAP, HHSUtil.getTaskPropertiesHashMap());
		HashMap<String, String> loRequiredParamMap = new HashMap<String, String>();
		loChannel.setData(ApplicationConstants.REQ_PROPS_DOCUMENT, setRequiredParam(loRequiredParamMap));
		try
		{
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_EVALUATE_PROPOSAL_TASK);
			HashMap<String, Object> loTaskDetailMap = (HashMap<String, Object>) loChannel
					.getData(ApplicationConstants.TASK_DETAIL_MAP);
			TaskDetailsBean loTaskDetailsBean = HHSUtil.getTaskDetailsBeanFromMap(loTaskDetailMap, lsWobNumber);
			aoRequest.setAttribute(ApplicationConstants.TASK_DETAILS_BEAN, loTaskDetailsBean);
			List<ExtendedDocument> loProposalDocumentList = (List<ExtendedDocument>) loChannel
					.getData(HHSConstants.FINAL_PROPOSAL_DOC_LIST);
			String lsEvaluatorName = (String) loChannel.getData(HHSConstants.EVALUATOR_NAME);
			List<EvaluationBean> loScoreDetailList = (List<EvaluationBean>) loChannel.getData(HHSConstants.SCORE_LIST);
			Map<String, String> loProposalMap = (Map<String, String>) loChannel
					.getData(HHSConstants.PROPOSAL_DETAIL_MAP);
			if (null != loProposalDocumentList && !loProposalDocumentList.isEmpty() && null != loProposalMap)
			{
				ExtendedDocument loExtendedDoc = new ExtendedDocument();
				loExtendedDoc.setProposalTitle(HHSConstants.PROPOSAL_DETAILS_KEY);
				loExtendedDoc.setDocumentType(HHSConstants.NA_KEY);
				loExtendedDoc.setIsRequiredDoc(HHSConstants.REQUIRED_FLAG);
				loExtendedDoc.setDocumentId(HHSConstants.NA_KEY);
				loExtendedDoc.setCustomLabelName(HHSConstants.NA_KEY);
				loExtendedDoc.setModifiedDate(loProposalMap.get(ApplicationConstants.MODIFIED_DATE));
				loExtendedDoc.setProposalId(loTaskDetailsBean.getProposalId());
				loExtendedDoc.setProcurementId(loTaskDetailsBean.getProcurementId());
				loProposalDocumentList.add(0, loExtendedDoc);
				aoRequest.setAttribute(HHSConstants.PROPOSAL_TASK_STATUS, loTaskDetailsBean.getTaskStatus());
			}
			if (null != lsEvaluatorName)
			{
				aoRequest.setAttribute(HHSConstants.EVALUATOR_NAME, lsEvaluatorName);
			}
			aoRequest.setAttribute(HHSConstants.PROCUREMENT_ID, loTaskDetailsBean.getProcurementId());
			aoRequest.setAttribute(HHSConstants.PROPOSAL_ID, loTaskDetailsBean.getProposalId());
			aoRequest.setAttribute(HHSConstants.WORKFLOW_ID, lsWobNumber);
			aoRequest.setAttribute(HHSConstants.TASK_ID, loTaskDetailsBean.getTaskId());
			aoRequest.setAttribute(HHSConstants.PROPOSAL_DOCUMENT_LIST, loProposalDocumentList);
			// R5 starts : added
			aoRequest.setAttribute(HHSR5Constants.EVALUATOR_ROUND_DETAILS,
					(List<String>) loChannel.getData(HHSR5Constants.LO_EVALUATION_SCORE_LIST));
			// R5 ends : added
			// below line is modified as a part of Release 2.6.0 enhancement
			// 5571
			// instead of FINAL_RFP_DOC_LIST, sorted list
			// FINAL_SORTED_RFP_DOC_LIST is being used,
			// to display documents of type other than "RFP" and "Addenda" in
			// ascending order
			aoRequest.setAttribute(HHSConstants.RFP_DOCUMENT_LIST,
					loChannel.getData(HHSConstants.FINAL_SORTED_RFP_DOC_LIST));

            // Start QC9665 R 9.3.2 Citywide cyber assessment program (CCAP) - vulnerabilities -- Users Can Access Other User's Uploaded Documents
			List<ExtendedDocument> loRfpDocLsty = (List<ExtendedDocument>)  loChannel.getData(HHSConstants.FINAL_SORTED_RFP_DOC_LIST);
			List<ExtendedDocument> loDocumentList = ListUtils.union(loProposalDocumentList, loRfpDocLsty);
			aoRequest.getPortletSession().setAttribute(ApplicationConstants.SESSION_EXTENDED_DOCUMENT_LIST, loDocumentList, PortletSession.APPLICATION_SCOPE);
			LOG_OBJECT.Info("save Document List in Session on Application scope");
			//End QC9665 R 9.3.2 Citywide cyber assessment program (CCAP) - vulnerabilities -- Users Can Access Other User's Uploaded Documents

			
			
			aoRequest.setAttribute(HHSConstants.SCORE_LIST, loScoreDetailList);
			loScoreBean.setMiProposalId(loTaskDetailsBean.getProposalId());
			loScoreBean.setMiEvaluationBeanList((List<EvaluationBean>) loChannel.getData(HHSConstants.SCORE_LIST));
			loTaskDetailBean = (TaskDetailsBean) loChannel.getData(HHSConstants.AS_TASK_COMMENT);
			loScoreBean.setInternalComments(loTaskDetailBean.getInternalComment());
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
					aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE));
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE_TYPE));
			aoRequest
					.setAttribute(HHSConstants.FROM_SAVE_BUTTON, aoRequest.getParameter(HHSConstants.FROM_SAVE_BUTTON));
			// Release: 2.5.0 #5415 added parameter for confirm scores tab on
			// evaluate proposal task for tab persistence
			aoRequest.setAttribute(HHSConstants.CONFIRM_SCORES_TAB,
					aoRequest.getParameter(HHSConstants.CONFIRM_SCORES_TAB));
		}
		// handling Application Exception thrown by transaction layer
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Error occured while processing evaluate proposal task details", aoAppExp);
			setGenericErrorMessage(aoRequest);
		}
		// handling Application Exception other than Application Exception
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occured while processing evaluate proposal task details", aoEx);
			setGenericErrorMessage(aoRequest);
		}
		// Start of Changes done for enhancement QC : 5688
		if (null != lsControllerAction && !lsControllerAction.isEmpty())
		{
			aoRequest.setAttribute(HHSConstants.SCREEN_READ_ONLY, true);
			return new ModelAndView(HHSConstants.EVALUATE_PROPOSAL_TASK_JSP_CITY,
					HHSConstants.MODEL_BEAN_SCOREDETAILSBEAN, loScoreBean);
		}
		else
		{
			return new ModelAndView(HHSConstants.EVALUATE_PROPOSAL_TASK_JSP, HHSConstants.MODEL_BEAN_SCOREDETAILSBEAN,
					loScoreBean);
		}
		// End of Changes done for enhancement QC : 5688
	}

	/**
	 * This method handles action when save button is clicked from S242 screen
	 * 
	 * <ul>
	 * <li>Get request and session parameters like: userId, workflowId,taskId,
	 * proposalId</li>
	 * <li>If internal comments are not null, add audit bean object by calling
	 * method getBeanForSavingUserComments() for saving user comments</li>
	 * </ul>
	 * 
	 * <ul>
	 * <li>Build part: 2.5.0 production support</li>
	 * <li>Removed bypassing of blank value when blank comments are tried to be
	 * saved</li>
	 * </ul>
	 * 
	 * @param aoScoreDetailsBean score detail bean object
	 * @param aoResult binding result object
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse object
	 * @param aoModelMap a model map object
	 * 
	 *            calls the transaction 'saveEvaluateProposalTaskDetails'
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	@ActionMapping(params = "submit_action=saveEvaluateProposalTaskDetails")
	public void saveEvaluateProposalTaskDetails(
			@ModelAttribute("ScoreDetailsBean") ScoreDetailsBean aoScoreDetailsBean, BindingResult aoResult,
			ActionRequest aoRequest, ActionResponse aoResponse, ModelMap aoModelMap)
	{
		// Release: 2.5.0 #5415 added parameter for confirm scores tab on
		// evaluate proposal task for tab persistence
		aoResponse.setRenderParameter(HHSConstants.FROM_SAVE_BUTTON, HHSConstants.TRUE);
		aoResponse.setRenderParameter(HHSConstants.CONFIRM_SCORES_TAB, HHSConstants.FALSE);

		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsUserOrg = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);
		String lsWorkflowId = aoRequest.getParameter(HHSConstants.WORKFLOW_ID);
		String lsTaskId = aoRequest.getParameter(HHSConstants.TASK_ID);
		String lsEvalId = aoRequest.getParameter(HHSConstants.EVALUATION_STATUS_ID);
		String lsInternalComments = aoRequest.getParameter(ApplicationConstants.INTERNAL_COMMENTS);
		String lsProposalId = aoRequest.getParameter(HHSConstants.PROPOSAL_ID);
		String lsProposalTaskStatus = aoRequest.getParameter(HHSConstants.PROPOSAL_TASK_STATUS);
		try
		{
			P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			Channel loChannel = new Channel();
			List<HhsAuditBean> loUserCommentsList = null;

			// Release: 2.5.0 #5415 removed bypassing of blank comments save
			if (null == lsInternalComments)
			{
				lsInternalComments = HHSConstants.EMPTY_STRING;
			}

			loUserCommentsList = new ArrayList<HhsAuditBean>();
			loUserCommentsList.add(HHSUtil.getBeanForSavingUserComments(lsTaskId, lsWorkflowId,
					HHSConstants.TEMP_EVALUATOR, lsEvalId, lsUserId, lsUserOrg, null, lsInternalComments));
			loChannel.setData(ApplicationConstants.AUDIT_BEAN_LIST, loUserCommentsList);

			if ((aoScoreDetailsBean.getMiEvaluationBeanList().size()) > 0)
			{
				aoScoreDetailsBean.setCreatedBy(lsUserId);
				aoScoreDetailsBean.setModifiedBy(lsUserId);
				aoScoreDetailsBean.setAction(HHSConstants.ACTION_SAVED);
				loChannel.setData(HHSConstants.SCORE_DETAILS, aoScoreDetailsBean);
				loChannel.setData(HHSConstants.SAVE_STATUS, true);
				loChannel.setData(HHSConstants.PROPOSAL_ID, lsProposalId);
				HashMap loHmWFProperties = new HashMap();
				loHmWFProperties.put(P8Constants.PE_WORKFLOW_LAST_MODIFIED_DATE, new Date());
				loChannel.setData(HHSConstants.REQ_PROPS_HASHMAP, loHmWFProperties);
				loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
				loChannel.setData(HHSConstants.WORKFLOW_ID, lsWorkflowId);
				loChannel.setData(HHSConstants.PROPOSAL_TASK_STATUS, lsProposalTaskStatus);
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.SAVE_EVALUATE_PROPOSAL_TASK_DETAILS);
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE,
						HHSConstants.SCORES_SAVED_SUCCESSFULLY);
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_PASS_TYPE);
			}
		}
		// handling Application Exception thrown by Transaction layer.
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception Occured while saving evaluate proposal task details: ", aoAppEx);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while saving evaluate proposal task details: ", aoEx);
			LOG_OBJECT.Error("Exception Occured while saving evaluate proposal task details: ", aoEx);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
		aoResponse.setRenderParameter(HHSConstants.WOB_NUMBER, lsWorkflowId);
		aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.SHOW_EVALUATE_PROPOSAL_TASK_DETAILS);
	}

	/**
	 * This method handles action when finish button is clicked from S242
	 * 
	 * <ul>
	 * <li>Get request and session parameters like: userId, workflowId,taskId,
	 * proposalId, P8UserSession</li>
	 * <li>Get process task status Id based on proposal task status from request
	 * </li>
	 * <li>If internal comments are not null, add audit bean object by calling
	 * method addAuditDataToChannel() from HHSUtil</li> *
	 * <li>Set task details in channel and execute transaction with Id
	 * "finishEvaluateProposalTask"</li>
	 * </ul>
	 * 
	 * <ul>
	 * <li>Build part: 2.5.0 production support</li>
	 * <li>Removed bypassing of blank value when blank comments are tried to be
	 * saved</li>
	 * </ul>
	 * 
	 * @param aoScoreDetailsBean score detail bean object
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse object
	 */
	@SuppressWarnings("rawtypes")
	@ActionMapping(params = "submit_action=finishEvaluateProposalTask")
	protected void finishEvaluateProposalTask(@ModelAttribute("ScoreDetailsBean") ScoreDetailsBean aoScoreDetailsBean,
			ActionRequest aoRequest, ActionResponse aoResponse)
	{
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsProposalTaskStatus = aoRequest.getParameter(HHSConstants.PROPOSAL_TASK_STATUS);
		String lsProposalId = aoRequest.getParameter(HHSConstants.PROPOSAL_ID);
		String lsWorkflowId = aoRequest.getParameter(HHSConstants.WORKFLOW_ID);
		String lsTaskId = aoRequest.getParameter(HHSConstants.TASK_ID);
		String lsUserName = aoRequest.getParameter(HHSConstants.KEY_SESSION_USER_NAME);
		String lsPassword = aoRequest.getParameter(HHSConstants.PASSWORD);
		String lsUserOrg = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);
		String lsEvalId = aoRequest.getParameter(HHSConstants.EVALUATION_STATUS_ID);
		try
		{
			// Release: 2.5.0 #5415 added parameter for confirm scores tab on
			// evaluate proposal task for tab persistence
			aoResponse.setRenderParameter(HHSConstants.FROM_SAVE_BUTTON, HHSConstants.FALSE);
			aoResponse.setRenderParameter(HHSConstants.CONFIRM_SCORES_TAB, HHSConstants.TRUE);

			Map loAuthenticateMap = validateUser(lsUserName, lsPassword, aoRequest);
			Boolean loAuthStatus = (Boolean) loAuthenticateMap.get(HHSConstants.IS_VALID_USER);
			P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			Channel loChannel = new Channel();
			if (!loAuthStatus)
			{
				aoResponse
						.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
								HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.MESSAGE_M38));
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE);
				aoResponse.setRenderParameter(HHSConstants.WOB_NUMBER, lsWorkflowId);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION,
						HHSConstants.SHOW_EVALUATE_PROPOSAL_TASK_DETAILS);
			}
			else
			{
				finishEvaluateProposalTask(aoScoreDetailsBean, aoRequest, lsUserId, lsProposalTaskStatus, lsProposalId,
						lsWorkflowId, lsTaskId, lsUserOrg, lsEvalId, loUserSession, loChannel);
			}
		}
		// handling Application Exception thrown by transaction layer
		catch (ApplicationException aoAppEx)
		{
			Map<String, String> loParamMap = new HashMap<String, String>();
			loParamMap.put(ApplicationConstants.KEY_SESSION_USER_ID, lsUserId);
			loParamMap.put(ApplicationConstants.KEY_SESSION_USER_ORG, lsUserOrg);
			loParamMap.put(HHSConstants.WORKFLOW_ID, lsWorkflowId);
			loParamMap.put(HHSConstants.TASK_ID, lsTaskId);
			loParamMap.put(HHSConstants.PROPOSAL_ID, lsProposalId);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap, aoAppEx);
			aoResponse.setRenderParameter(HHSConstants.WOB_NUMBER, lsWorkflowId);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.SHOW_EVALUATE_PROPOSAL_TASK_DETAILS);
		}
		// handling Exception other than Application Exception
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while finishing evaluate proposal task details: ", aoEx);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
			aoResponse.setRenderParameter(HHSConstants.WOB_NUMBER, lsWorkflowId);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.SHOW_EVALUATE_PROPOSAL_TASK_DETAILS);
		}
		aoResponse.setRenderParameter(HHSConstants.RETURN_TO_AGENCY_TASK, HHSConstants.TRUE);
	}

	/**
	 * This method is used to finish and evaluate proposal task
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * @param aoScoreDetailsBean object of type ScoreDetailsBean
	 * @param aoRequest ActionRequest object
	 * @param asUserId User ID
	 * @param asProposalTaskStatus Proposal Task Status
	 * @param asProposalId Proposal Id
	 * @param asWorkflowId WorkFlow Id
	 * @param asTaskId Task Id
	 * @param asUserOrg User Organization
	 * @param asEvalId Evaluation Status ID
	 * @param aoUserSession P8UserSession Object
	 * @param aoChannel Channel Object
	 * @throws ApplicationException
	 */
	private void finishEvaluateProposalTask(ScoreDetailsBean aoScoreDetailsBean, ActionRequest aoRequest,
			String asUserId, String asProposalTaskStatus, String asProposalId, String asWorkflowId, String asTaskId,
			String asUserOrg, String asEvalId, P8UserSession aoUserSession, Channel aoChannel)
			throws ApplicationException
	{
		String lsInternalComments = aoRequest.getParameter(ApplicationConstants.INTERNAL_COMMENTS);
		List<HhsAuditBean> loAuditBeanList = new ArrayList<HhsAuditBean>();

		// Release: 2.5.0 #5415 removed bypassing of blank comments save
		if (null == lsInternalComments)
		{
			lsInternalComments = HHSConstants.EMPTY_STRING;
		}

		loAuditBeanList.add(HHSUtil.getBeanForSavingUserComments(asTaskId, asWorkflowId, HHSConstants.EVALUATOR,
				asEvalId, asUserId, asUserOrg, null, lsInternalComments));
		aoChannel.setData(ApplicationConstants.AUDIT_BEAN_LIST, loAuditBeanList);

		if ((aoScoreDetailsBean.getMiEvaluationBeanList().size()) > 0)
		{
			aoScoreDetailsBean.setCreatedBy(asUserId);
			aoScoreDetailsBean.setModifiedBy(asUserId);
			aoScoreDetailsBean.setAction(HHSConstants.TASK_FINISHED);
			aoChannel.setData(HHSConstants.SCORE_DETAILS, aoScoreDetailsBean);
			aoChannel.setData(HHSConstants.PROPOSAL_ID, asProposalId);
			aoChannel.setData(HHSConstants.PROPOSAL_TASK_STATUS, asProposalTaskStatus);
		}
		HashMap<String, Object> loEvalStatusMap = new HashMap<String, Object>();
		loEvalStatusMap.put(HHSConstants.STATUS_ID, PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.EVALUATE_PROPOSAL_TASK_SCORES_COMPLETED));
		loEvalStatusMap.put(HHSConstants.USER_ID, asUserId);
		loEvalStatusMap.put(HHSConstants.EVALUATION_STATUS_ID, asEvalId);
		aoChannel.setData(HHSConstants.EVAL_STATUS_MAP, loEvalStatusMap);
		// save task details in filenet
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setTaskId(asTaskId);
		loTaskDetailsBean.setWorkFlowId(asWorkflowId);
		loTaskDetailsBean.setTaskStatus(asProposalTaskStatus);
		aoChannel.setData(ApplicationConstants.TASK_DETAILS_BEAN, loTaskDetailsBean);
		aoChannel.setData(ApplicationConstants.FILENET_SESSION, aoUserSession);
		// Start R5 : set EntityId and EntityName for AutoSave
		CommonUtil.setChannelForAutoSaveData(aoChannel, asWorkflowId, HHSR5Constants.TASKS);
		// End R5 : set EntityId and EntityName for AutoSave
		HHSTransactionManager.executeTransaction(aoChannel, HHSConstants.FINISH_EVALUATE_PROPOSAL_TASK);
	}

	/**
	 * This method is executed when an agency user opens Configure Award
	 * Document Task
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Modification done as a part of Enhancement #5688 for Release 3.2.0</li>
	 * <li>Fetch required parameters from request like WorkflowId, UserId, User
	 * orgType</li>
	 * <li>Set channel with required parameters for fetching procurement and
	 * task details, Award documents</li>
	 * <li>Invoke the transaction <b>fetchAwardDocumentTaskDetails</b></li>
	 * <li>Set transaction outputs in request</li>
	 * </ul>
	 * 
	 * @param aoRequest - Render Request
	 * @param aoResponse - Render Response
	 * @return ModelAndView containing JSP name on which user has to be
	 *         redirected
	 * 
	 */
	@SuppressWarnings(
	{ "unchecked" })
	@RenderMapping(params = "render_action=configureAwardDocumentTaskDetails")
	protected ModelAndView configureAwardDocumentTaskDetails(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		ProposalDetailsBean loAwardConfigBean = new ProposalDetailsBean();
		String lsControllerAction = aoRequest.getParameter(HHSConstants.CONTROLLER_ACTION);
		try
		{
			String lsWobNumber = aoRequest.getParameter(HHSConstants.WOB_NUMBER);
			P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			// R5 changes start
			HashMap loHmReqProps = HHSUtil.getTaskPropertiesHashMap();
			loHmReqProps.put(P8Constants.PROPERTY_WORKFLOW_IS_NEGOTIATION_REQUIRED, HHSConstants.EMPTY_STRING);
			// R5 changes end
			// fetching procurement and task details
			Channel loChannel = new Channel();
			loChannel.setData(ApplicationConstants.WOB_NUMBER, lsWobNumber);
			loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
			loChannel.setData(ApplicationConstants.KEY_SESSION_USER_ID, lsUserId);
			loChannel.setData(ApplicationConstants.REQ_PROPS_TASK_HASHMAP, loHmReqProps);
			HashMap<String, String> loReqPropsMap = new HashMap<String, String>();
			loChannel.setData(ApplicationConstants.REQ_PROPS_DOCUMENT, setRequiredParam(loReqPropsMap));

			// fetching unassigned Acco managers
			List<String> loUserRoleList = new ArrayList<String>();
			loUserRoleList.add(HHSConstants.ACCO_MANAGER_ROLE);
			loChannel.setData(ApplicationConstants.ACCELERATOR_USER_ROLE_LIST, loUserRoleList);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_AWARD_DOC_TASK_DETAILS);
			HashMap<String, Object> loTaskDetailMap = (HashMap<String, Object>) loChannel
					.getData(ApplicationConstants.TASK_DETAIL_MAP);
			TaskDetailsBean loTaskDetailsBean = HHSUtil.getTaskDetailsBeanFromMap(loTaskDetailMap, lsWobNumber);
			aoRequest.setAttribute(ApplicationConstants.TASK_DETAILS_BEAN, loTaskDetailsBean);

			// Get the permitted user list for reassign dropdown
			List<UserBean> loUserBeanList = (List<UserBean>) loChannel
					.getData(ApplicationConstants.PERMITTED_USER_LIST);
			aoRequest.setAttribute(ApplicationConstants.REASSIGN_USER_MAP, HHSUtil.getReassignUserMap(loUserBeanList));
			aoRequest.setAttribute(HHSConstants.WORKFLOW_ID, lsWobNumber);

			// Set award document list in request attribute
			setAwardDocumentListToRequest(aoRequest, loAwardConfigBean, loChannel);
			// Check if User Id is same as User Assigned To
			if (null != loTaskDetailsBean && null != loTaskDetailsBean.getAssignedTo()
					&& loTaskDetailsBean.getAssignedTo().equalsIgnoreCase(lsUserId))
			{
				aoRequest.setAttribute(HHSConstants.SCREEN_READ_ONLY, false);
			}
			else
			{
				aoRequest.setAttribute(HHSConstants.SCREEN_READ_ONLY, true);
			}
			aoRequest.setAttribute(HHSConstants.DEFAULT_CONFIG_ID, loChannel.getData(HHSConstants.DEFAULT_CONFIG_ID));
			String lsErrorMsg = (String) aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE_TYPE));
			if (null != PortalUtil.parseQueryString(aoRequest, HHSConstants.SUCCESS)
					&& PortalUtil.parseQueryString(aoRequest, HHSConstants.SUCCESS).equalsIgnoreCase(
							HHSConstants.UPLOAD))
			{
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
						ApplicationConstants.ERROR_MESSAGE_PROP_FILE, HHSConstants.FILE_UPLOAD_PASS_MESSAGE));
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_PASS_TYPE);
			}
		}
		// Handling Application Exception thrown by Transaction layer
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured while processing configure award task details", aoAppEx);
			setGenericErrorMessage(aoRequest);
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occured while processing configure award task details", aoEx);
			setGenericErrorMessage(aoRequest);
		}
		// Start || Modification done as a part of Enhancement #5688 for Release
		// 3.2.0
		if (null != lsControllerAction && !lsControllerAction.isEmpty())
		{
			return new ModelAndView(HHSConstants.CONFIGURE_AWARD_DOC_TASK_JSP_CITY,
					HHSConstants.PROPOSAL_DETAILS_BEAN_UPPERCASE, loAwardConfigBean);
		}
		else
		{
			return new ModelAndView(HHSConstants.CONFIGURE_AWARD_DOC_TASK_JSP,
					HHSConstants.PROPOSAL_DETAILS_BEAN_UPPERCASE, loAwardConfigBean);

		}
		// End || Modification done as a part of Enhancement #5688 for Release
		// 3.2.0
	}

	/**
	 * This method is used to set award document list to request
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * @param aoRequest
	 * @param aoAwardConfigBean
	 * @param aoChannel
	 * @throws ApplicationException
	 */
	private void setAwardDocumentListToRequest(RenderRequest aoRequest, ProposalDetailsBean aoAwardConfigBean,
			Channel aoChannel) throws ApplicationException
	{
		List<ExtendedDocument> loAwardList = (List<ExtendedDocument>) aoChannel
				.getData(HHSConstants.FINAL_AWARD_DOC_LIST);
		if (null != loAwardList && !loAwardList.isEmpty())
		{
			HHSUtil.sortList(loAwardList, HHSConstants.DOCUMENT_TITLE_LOWER_CASE);
		}
		aoRequest.setAttribute(HHSConstants.AWARD_DOC_LIST, loAwardList);
		List<ExtendedDocument> loAwardDocTypeList = (List<ExtendedDocument>) aoChannel
				.getData(HHSConstants.FETCH_AWARD_DOCUMENT_TYPE_LIST);
		List<ExtendedDocument> loRequiredAwardDocTypeList = new ArrayList<ExtendedDocument>();
		List<ExtendedDocument> loOptionalAwardDocTypeList = new ArrayList<ExtendedDocument>();
		if (null != loAwardDocTypeList && CollectionUtils.isNotEmpty(loAwardDocTypeList))
		{
			for (ExtendedDocument loExtendedDocument : loAwardDocTypeList)
			{
				if (null != loExtendedDocument.getRequiredFlag()
						&& loExtendedDocument.getRequiredFlag().equalsIgnoreCase(HHSConstants.ONE))
				{
					loRequiredAwardDocTypeList.add(loExtendedDocument);
				}
				else
				{
					loOptionalAwardDocTypeList.add(loExtendedDocument);
				}
			}
		}
		aoAwardConfigBean.setRequiredDocumentList(loRequiredAwardDocTypeList);
		aoAwardConfigBean.setOptionalDocumentList(loOptionalAwardDocTypeList);
		aoRequest.setAttribute(HHSConstants.REQUIRED_AWARD_DOCUMENT_TYPE_LIST, loRequiredAwardDocTypeList);
		// fetch and Set Document type list
		org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENET_EXTENDED_DOC_TYPE);
		List<String> loDocumentTypeList = RFPReleaseDocsUtil.getDoctypesFromXML(loXMLDoc,
				ApplicationConstants.PROVIDER_ORG);
		aoRequest.setAttribute(HHSConstants.DOC_TYPE_LIST, loDocumentTypeList);
	}

	/**
	 * This method is executed when user clicks on Upload Document button or
	 * selects Upload Document option from actions drop down
	 * 
	 * <ul>
	 * <li>Get document Category for doc type "Agency Document"</li>
	 * <li>Set document Category and Document type in document session object</li>
	 * <li>Set procurement Id and workflow Id in response parameters and render
	 * to upload document screen</li>
	 * 
	 * <li>This method was updated in R4.</li>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse object
	 * 
	 */
	@ActionMapping(params = "submit_action=uploadAwardDocument")
	public void actionUploadAwardDocument(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
		String lsUploadingDocumentType = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.UPLOAD_DOC_TYPE);
		Document loDocument = new Document();
		try
		{
			// Start of Changes done for defect QC : 5725
			FileNetOperationsUtils.setDocCategorynDocType(loDocument, null, lsUserOrgType);
			// End of of Changes done for defect QC : 5725
			ApplicationSession.setAttribute(loDocument, aoRequest, ApplicationConstants.SESSION_DOCUMENT_OBJ);
			aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID,
					aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			aoResponse.setRenderParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID,
					aoRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID));
			aoResponse.setRenderParameter(HHSConstants.WORKFLOW_ID, aoRequest.getParameter(HHSConstants.WORKFLOW_ID));
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.UPLOAD_DOC);
			if (null != lsUploadingDocumentType)
			{
				aoResponse.setRenderParameter(HHSConstants.UPLOAD_DOC_TYPE, lsUploadingDocumentType);
			}
			// Added for R5- combo box for DocType - fix for Defect id 7270
			List<String> loDocTypeList = FileNetOperationsUtils.getDocType(lsUserOrgType, "agencyAwardDoc", null);
			ApplicationSession.setAttribute(loDocTypeList, aoRequest, "docTypedropDownCombo");
			// R5 end
		}
		// handling Application Exception thrown by service layer
		catch (ApplicationException loExp)
		{
			Map<String, String> loParamMap = new HashMap<String, String>();
			loParamMap.put(HHSConstants.LS_USER_ORG_TYPE, lsUserOrgType);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap, loExp);

		}
		// handling Exception other than Application Exception
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error occurred while uploading award Document", loExp);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
	}

	/**
	 * This method is used to render user to upload screen for uploading award
	 * documents
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Get document bean from session object</li>
	 * <li>Set procurement Id and workflow Id in request object</li>
	 * <li>Set error messages and message type in request object if exists</li>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse object
	 * @return Model and View Object containing jsp name
	 * 
	 */
	@RenderMapping(params = "render_action=uploadDocument")
	protected ModelAndView renderUploadAwardDocument(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		aoRequest.setAttribute(ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_PARAMETER,
				(Document) ApplicationSession.getAttribute(aoRequest, true, ApplicationConstants.SESSION_DOCUMENT_OBJ));
		aoRequest.setAttribute(HHSConstants.PROCUREMENT_ID, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
		aoRequest.setAttribute(HHSConstants.EVALUATION_POOL_MAPPING_ID,
				aoRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID));
		aoRequest.setAttribute(HHSConstants.WORKFLOW_ID, aoRequest.getParameter(HHSConstants.WORKFLOW_ID));
		aoRequest.setAttribute(
				ApplicationConstants.ERROR_MESSAGE,
				aoRequest.getPortletSession().getAttribute(ApplicationConstants.ERROR_MESSAGE,
						PortletSession.APPLICATION_SCOPE));
		aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		aoRequest.setAttribute(HHSConstants.UPLOAD_DOC_TYPE, aoRequest.getParameter(HHSConstants.UPLOAD_DOC_TYPE));
		// Added for R5- combo box for DocType - fix for Defect id 7270
		aoRequest.setAttribute("docTypedropDownCombo",
				ApplicationSession.getAttribute(aoRequest, true, "docTypedropDownCombo"));
		// R5 end
		return new ModelAndView(HHSConstants.UPLOAD_AWARD_TASK_DOCS);
	}

	/**
	 * This method is used to get the details properties of the selected
	 * document.
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Execute <b>actionFileInformation</b> Method of
	 * <b>FileNetOperationsUtils</b> class</li>
	 * <li>get all the meta data details required for the document from doctype
	 * configuration file</li>
	 * <li>populate second tab on the upload screen according to the number of
	 * the required meta data</li>
	 * <li>Get the document bean from the ApplicationSession and set it in
	 * Request</li>
	 * <li>Set the render action parameter</li>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest Object
	 * @param aoResponse ActionResponse Object
	 * 
	 * 
	 */
	@Override
	@ActionMapping(params = "submit_action=uploadingFileInformation")
	public void displayUploadingFileInformationAction(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		PortletSession loSession = aoRequest.getPortletSession();
		String lsUserOrgType = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
				PortletSession.APPLICATION_SCOPE);
		String lsWorkflowId = PortalUtil.parseQueryString(aoRequest, HHSConstants.WORKFLOW_ID);
		try
		{
			FileNetOperationsUtils.actionFileInformation(aoRequest, aoResponse);
			String lsErrorMessage = (String) loSession.getAttribute(ApplicationConstants.MESSAGE,
					PortletSession.APPLICATION_SCOPE);
			// extra check is added in the if block to identify that next step
			// to display. This is added as part of Release 2.6.0 defect:5612
			if ((null == lsErrorMessage || lsErrorMessage.isEmpty())
					|| (null != lsErrorMessage && !lsErrorMessage.isEmpty()
							&& null != aoRequest.getAttribute(HHSConstants.MOVE_TO_NEXT_PAGE) && ApplicationConstants.FILE_INFORMATION
								.equalsIgnoreCase((String) aoRequest.getAttribute(HHSConstants.MOVE_TO_NEXT_PAGE))))
			{
				Document loUploadingDocObj = (Document) ApplicationSession.getAttribute(aoRequest, true,
						ApplicationConstants.SESSION_DOCUMENT_OBJ);
				List<DocumentPropertiesBean> loInitialDocPropsBean = FileNetOperationsUtils.getDocumentProperties(
						loUploadingDocObj.getDocCategory(), loUploadingDocObj.getDocType(), lsUserOrgType);
				loUploadingDocObj.setDocumentProperties(loInitialDocPropsBean);
				aoRequest.setAttribute(HHSConstants.BASE_RFP_DOCUMENTS, loUploadingDocObj);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.UPLOADING_FILE_INFO);
				aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID,
						aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
				aoResponse.setRenderParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID,
						aoRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID));
				aoResponse.setRenderParameter(HHSConstants.WORKFLOW_ID, lsWorkflowId);
			}
		}
		// handling Application Exception thrown by service layer
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("ApplicationException during file upload", aoAppEx);
			try
			{
				setErrorMessageInResponse(aoRequest,aoResponse,aoAppEx,null);
			}
			catch (IOException e)
			{
				setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
						ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
			}
		}
		// handling Exception other than Application Exception
		catch (Exception aoExp)
		{

			LOG_OBJECT.Error("Exception during file upload", aoExp);
			aoResponse.setRenderParameter(ApplicationConstants.RENDER_ACTION, ApplicationConstants.ERROR);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);

		}
	}

	/**
	 * This method will redirect user to the next tab of the upload document
	 * screen
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Get document bean from session object</li>
	 * <li>Set procurement Id and workflow Id in request object</li>
	 * <li>Set error messages and message type in request object if exists</li>
	 * </ul>
	 * 
	 * @param aoRequest Render Request Object
	 * @param aoResponse Render Response Object
	 * @return ModelAndView containing view name
	 * 
	 */
	@Override
	@RenderMapping(params = "render_action=uploadingFileInformation")
	protected ModelAndView displayUploadingFileInformationRender(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		String lsRender = null;
		aoRequest.setAttribute(HHSConstants.PROCUREMENT_ID,
				HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID));
		aoRequest.setAttribute(HHSConstants.EVALUATION_POOL_MAPPING_ID,
				HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.EVALUATION_POOL_MAPPING_ID));
		aoRequest.setAttribute(HHSConstants.WORKFLOW_ID,
				HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.WORKFLOW_ID));
		aoRequest.setAttribute(ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_PARAMETER,
				(Document) ApplicationSession.getAttribute(aoRequest, true, ApplicationConstants.SESSION_DOCUMENT_OBJ));
		aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
				aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE));
		aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		String lsErrorMessage = aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE);
		if (null != lsErrorMessage && !lsErrorMessage.equals(HHSConstants.EMPTY_STRING))
		{
			lsRender = HHSConstants.UPLOAD_AWARD_TASK_DOCS;
		}
		else
		{
			lsRender = HHSConstants.DISPLAY_UPLOAD_DOC_INFO;
		}
		return new ModelAndView(lsRender);
	}

	/**
	 * This method executed when user click on upload document link from the
	 * document vault home screen
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Get the document Category value from Request Object</li>
	 * <li>Depending on the document Category value Below logic</li>
	 * <li>If DocumentCategory is null<br/>
	 * Then<br/>
	 * Read document property details xml from cache Get the document Categories
	 * for the Organization Else<br/>
	 * Get the Document Type list according to the Selected Document Category</li>
	 * </ul>
	 * 
	 * @param aoRequest action request object
	 * @param aoResponse action response object
	 * 
	 *            calls transaction 'insertAwardTaskDocDetails'
	 * 
	 */
	@Override
	@ActionMapping(params = "submit_action=uploadFile")
	protected void documentFinalUploadAction(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		String lsDocumentId = null;
		String lsProcurementId = null;
		String lsEvalPoolMappingId = null;
		try
		{
			lsProcurementId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
			lsEvalPoolMappingId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.EVALUATION_POOL_MAPPING_ID);
			Document loDocument = (Document) ApplicationSession.getAttribute(aoRequest, true,
					ApplicationConstants.SESSION_DOCUMENT_OBJ);
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			// call method to upload document in filenet
			lsDocumentId = FileNetOperationsUtils.actionFileUpload(aoRequest, aoResponse);
			if (lsDocumentId != null)
			{
				// If document uploaded successfully, set document properties in
				// map
				HashMap<String, String> loDocPropsMap = new HashMap<String, String>();
				loDocPropsMap.put(HHSConstants.PROCUREMENT_ID, lsProcurementId);
				loDocPropsMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolMappingId);
				loDocPropsMap.put(HHSConstants.USER_ID, lsUserId);
				loDocPropsMap.put(HHSConstants.STATUS_COLUMN, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.DOCUMENT_SUBMITTED));
				loDocPropsMap.put(HHSConstants.DOC_ID, lsDocumentId);
				loDocPropsMap.put(HHSConstants.DOC_CATEGORY_LOWERCASE, loDocument.getDocCategory());
				loDocPropsMap.put(HHSConstants.DOC_NAME, loDocument.getDocName());
				loDocPropsMap.put(HHSConstants.DOCTYPE, loDocument.getDocType());
				// call transaction to insert award document details in db
				// Start Updated in R5
				P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
						ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
				HashMap<String, Object> loReqMap = new HashMap<String, Object>();
				loReqMap.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, true);
				Channel loChannel = new Channel();
				loChannel.setData(HHSConstants.DOC_PROPS_MAP, loDocPropsMap);
				loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
				loChannel.setData(HHSConstants.DOC_ID, lsDocumentId);
				loChannel.setData(HHSConstants.DOCUMENT_TYPE, loDocument.getDocType());
				loChannel.setData(HHSConstants.LO_HM_DOC_REQ_PROPS, loReqMap);
				// End Updated in R5
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.INSERT_AWARD_TASK_DOC_DETAILS);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.CONFIGURE_AWARD_DOC_TASK);
				aoResponse
						.setRenderParameter(HHSConstants.WOB_NUMBER, aoRequest.getParameter(HHSConstants.WORKFLOW_ID));
			}
		}
		// Catch the application exception log it
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("IOException during file upload", aoAppEx);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);

		}
		// Catch the exception thrown from the transaction layer and log it
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("IOException during file upload", aoExp);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
	}

	/**
	 * This method will be executed when user click on cancel button while
	 * uploading a document
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Delete the temporary file created from the context path</li>
	 * <li>Redirect user to the configure award document screen</li>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest Object
	 * @param aoResponse ActionResponse Object
	 * 
	 */
	@ActionMapping(params = "submit_action=cancelUploadDocument")
	public void cancelUploadDocumentAction(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		try
		{
			File loFilePath = new File(aoRequest.getParameter(ApplicationConstants.FILE_PATH));
			deleteTempFile(loFilePath);
			aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID,
					HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID));
			aoResponse.setRenderParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID,
					HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.EVALUATION_POOL_MAPPING_ID));
			aoResponse.setRenderParameter(HHSConstants.WOB_NUMBER, aoRequest.getParameter(HHSConstants.WORKFLOW_ID));
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.CONFIGURE_AWARD_DOC_TASK);
		}
		// handling exception other than Application Exception
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception during file upload", aoExp);
			aoResponse.setRenderParameter(ApplicationConstants.RENDER_ACTION, HHSConstants.CONFIGURE_AWARD_DOC_TASK);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
	}

	/**
	 * This method will redirect user to the back screen while uploading a
	 * document
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Get the temporary file path from the request</li>
	 * <li>Delete the temporary file from the path mentioned</li>
	 * <li>Redirect user to the back page</li>
	 * </ul>
	 * 
	 * @param aoRequest Action Request
	 * @param aoResponse Action Response
	 * 
	 */
	@Override
	@ActionMapping(params = "submit_action=goBackAction")
	public void goBackAction(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		try
		{
			File loFilePath = new File(aoRequest.getParameter(ApplicationConstants.FILE_PATH));
			String lsWorkflowId = PortalUtil.parseQueryString(aoRequest, HHSConstants.WORKFLOW_ID);
			// delete temporary file
			deleteTempFile(loFilePath);
			Document loDocument = new Document();

			// Start of Changes done for defect QC : 5725
			String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			loDocument.setDocCategory(aoRequest.getParameter(ApplicationConstants.DOCS_CATEGORY));
			loDocument.setDocType(aoRequest.getParameter(ApplicationConstants.DOCS_TYPE));
			loDocument.setCategoryList((ArrayList) FileNetOperationsUtils.getDocCategoryList(lsUserOrgType));
			loDocument.setTypeList((ArrayList) FileNetOperationsUtils.getDocTypeForDocCategory(
					aoRequest.getParameter(ApplicationConstants.DOCS_CATEGORY), lsUserOrgType, null));
			// End of Changes done for defect QC : 5725
			ApplicationSession.setAttribute(loDocument, aoRequest, ApplicationConstants.SESSION_DOCUMENT_OBJ);
			aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID,
					HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID));
			aoResponse.setRenderParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID,
					HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.EVALUATION_POOL_MAPPING_ID));
			aoResponse.setRenderParameter(HHSConstants.WORKFLOW_ID, lsWorkflowId);
			aoResponse.setRenderParameter(ApplicationConstants.DOC_CATEGORY,
					aoRequest.getParameter(ApplicationConstants.DOCS_CATEGORY));
			if (null != aoRequest.getParameter(ApplicationConstants.DOCS_TYPE))
			{
				aoResponse.setRenderParameter(ApplicationConstants.DOCS_TYPE,
						aoRequest.getParameter(ApplicationConstants.DOCS_TYPE));
			}
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.UPLOAD_DOC);
		}
		// handling exception other than Application Exception
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("IOException during file upload", aoExp);
			aoResponse.setRenderParameter(ApplicationConstants.RENDER_ACTION, HHSConstants.CONFIGURE_AWARD_DOC_TASK);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
	}

	/**
	 * This method will be executed after the document has been uploaded
	 * successfully or the upload document process gives any exception
	 * 
	 * @param aoFilePath a file object containing file path
	 */
	private void deleteTempFile(File aoFilePath)
	{
		if (null != aoFilePath)
		{
			aoFilePath.delete();
		}
	}

	/**
	 * This method will remove the document from the list
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Get documentId from the request object</li>
	 * <li>Execute Transaction with transaction id <b>removeAwardTaskDocs_db</b>
	 * which will remove the file from the data base</li>
	 * <li>Set the user friendly message in the response which will be displayed
	 * to user</li>
	 * </ul>
	 * 
	 * @param aoRequest - ActionRequest Object
	 * @param aoResponse - ActionResponse Object
	 * 
	 */
	@ActionMapping(params = "submit_action=removeProposalDocumentFromList")
	protected void actionRemoveProposalDocumentFromList(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		Channel loChannel = new Channel();
		String lsDeletedDocumentId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.DOC_ID);
		String lsDocSeqID = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.DOC_SEQ_ID);
		// Start Updated in R5
		String lsDocType = HHSPortalUtil.parseQueryString(aoRequest, HHSR5Constants.DOC_TYPE_HIDDEN);
		P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
		HashMap<String, Object> loMap = new HashMap<String, Object>();
		loMap.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, false);
		loChannel.setData(HHSConstants.DOC_ID, lsDeletedDocumentId);
		loChannel.setData(HHSConstants.DOC_SEQ_ID, lsDocSeqID);
		loChannel.setData(HHSConstants.DOCUMENT_TYPE, lsDocType);
		loChannel.setData(HHSConstants.LO_HM_DOC_REQ_PROPS, loMap);
		// End Updated in R5
		try
		{
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.REMOVE_AWARD_TASK_DOCS);
			Boolean loRemoveStatus = (Boolean) loChannel.getData(HHSConstants.REMOVE_STATUS);
			// set success message if document removed successfully
			if (!loRemoveStatus)
			{
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE,
						HHSConstants.ERROR_WHILE_PROCESSING_REQUEST);
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_PASS_TYPE);
			}
			else
			{
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.CONFIGURE_AWARD_DOC_TASK);
				aoResponse
						.setRenderParameter(HHSConstants.WOB_NUMBER, aoRequest.getParameter(HHSConstants.WORKFLOW_ID));
			}
		}
		// handling exception thrown by service layer
		catch (ApplicationException aoAppEx)
		{
			Map<String, String> loParamMap = new HashMap<String, String>();
			loParamMap.put(HHSConstants.DELETED_DOCUMENT_ID, lsDeletedDocumentId);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap, aoAppEx);
			LOG_OBJECT.Error("Error occurred while removing document from list for Configure Award Document Screen",
					aoAppEx);
		}
		// handling exception other than Application Exception
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occurred while removing document from list for Configure Award Document Screen",
					aoEx);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
	}

	/**
	 * This method handles action when reassign button is clicked from S247
	 * 
	 * <ul>
	 * <li>Get request and session parameters like: userId, workflowId,taskId,
	 * proposalId, P8UserSession, ReassignedTo, ReassignedToUserName</li>
	 * <li>Set task details in channel for reassigning task</li>
	 * <li>Execute query with Id "reassignTask" for reassigning task to other
	 * user</li>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse object
	 * 
	 *            calls the transaction 'reassignTask'
	 */
	@ActionMapping(params = "submit_action=reassignConfigureAwardTask")
	protected void reassignConfigureAwardTask(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		String lsWorkflowId = aoRequest.getParameter(HHSConstants.WORKFLOW_ID);
		String lsAssignedToUserId = aoRequest.getParameter(ApplicationConstants.REASSIGNED_TO);
		String lsAssignedToUserName = aoRequest.getParameter(ApplicationConstants.REASSIGNED_TO_USER_NAME);
		try
		{
			TaskDetailsBean loTaskBean = new TaskDetailsBean();
			loTaskBean.setWorkFlowId(lsWorkflowId);
			loTaskBean.setReassignUserId(lsAssignedToUserId);
			loTaskBean.setReassignUserName(lsAssignedToUserName);
			P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			// save assigned to field for selected user
			Channel loChannel = new Channel();
			loChannel.setData(ApplicationConstants.TASK_DETAILS_BEAN_KEY, loTaskBean);
			loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.REASSIGN_TASK);
		}
		// handling exception thrown by service layer
		catch (ApplicationException aoAppEx)
		{
			Map<String, String> loParamMap = new HashMap<String, String>();
			loParamMap.put(HHSConstants.WORK_FLOW_ID, lsWorkflowId);
			loParamMap.put(HHSConstants.USER_ID, lsAssignedToUserId);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap, aoAppEx);
			LOG_OBJECT.Error("Exception Occured while reassigning task:", aoAppEx);
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while reassigning task:", aoEx);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
		aoResponse.setRenderParameter(HHSConstants.RETURN_TO_AGENCY_TASK, HHSConstants.TRUE);
	}

	/**
	 * This method will handle when award document task is finish *
	 * <ul>
	 * reassignConfigureAwardTask
	 * <li>Get request and session parameters like: userId, workflowId,
	 * procurement id, P8UserSession,</li>
	 * <li>Set proposal details in channel for finsh the award document task</li>
	 * <li>Execute the transaction with id finishAwardDocumentTask user</li>
	 * </ul>
	 * 
	 * @param aoProposalDetailsBean - contain Proposal Information
	 * @param aoRequest - ActionRequest Object
	 * @param aoResponse - ActionResponse Object
	 * 
	 *            calls the transaction 'finishAwardDocumentTask'
	 * 
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	@ActionMapping(params = "submit_action=finishAwardDocumentTask")
	public void finishAwardDocumentTask(
			@ModelAttribute("ProposalDetailsBean") ProposalDetailsBean aoProposalDetailsBean, ActionRequest aoRequest,
			ActionResponse aoResponse)
	{
		HashMap loHmReqExceProp = new HashMap();
		String lsUserId = null;
		String lsWorkflowId = null;
		String lsProcurementId = null;
		String lsEvalPoolMappingId = null;
		try
		{
			lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);
			// R5 change starts
			String lsNegotiationRequired = aoRequest.getParameter(HHSR5Constants.IS_NEGOTIATION_REQUIRED);
			lsNegotiationRequired = lsNegotiationRequired == HHSConstants.EMPTY_STRING ? HHSConstants.STRING_FALSE
					: lsNegotiationRequired;
			// R5 change ends
			loHmReqExceProp.put(HHSConstants.LS_USER_ID, lsUserId);
			P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			lsWorkflowId = aoRequest.getParameter(ApplicationConstants.WORKFLOW_ID);
			lsProcurementId = aoRequest.getParameter(HHSConstants.PROCUREMENT_ID);
			lsEvalPoolMappingId = aoRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID);
			String lsDefaultConfigurationChecked = aoRequest.getParameter(HHSConstants.DEFAULT_CONFIGURATIONS_CHECKED);
			// set modified by and created by in proposal bean
			aoProposalDetailsBean.setModifiedBy(lsUserId);
			aoProposalDetailsBean.setCreatedBy(lsUserId);
			aoProposalDetailsBean.setProcurementId(lsProcurementId);
			aoProposalDetailsBean.setEvaluationPoolMappingId(lsEvalPoolMappingId);
			// set audit parameters in Audit bean
			Channel loChannel = new Channel();
			loChannel.setData(HHSConstants.PROPOSAL_DETAILS_BEAN, aoProposalDetailsBean);
			// set competition pool mapping details for updating pool
			// status
			Map<String, Object> loInputParamMap = new HashMap<String, Object>();
			loInputParamMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolMappingId);
			loInputParamMap.put(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
			// PARAMETERS ADDED FOR AUDIT CHANGE
			loInputParamMap.put(HHSConstants.EVENT_NAME, HHSConstants.CONFIG_AWARD_DOC);
			loInputParamMap.put(HHSConstants.EVENT_TYPE, HHSConstants.SELECTIONS_MADE);
			loInputParamMap.put(HHSConstants.USER_ID, lsUserId);
			loInputParamMap.put(HHSConstants.COMPETITION_POOL_STATUS, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_COMPETITION_POOL_SELECTIONS_MADE));
			loInputParamMap.put(ApplicationConstants.KEY_SESSION_USER_ID, lsUserId);
			if (null != lsDefaultConfigurationChecked)
			{
				loInputParamMap.put(HHSConstants.DEFAULT_CONFIG_ID, lsEvalPoolMappingId);
			}
			loChannel.setData(HHSConstants.INPUT_PARAM_MAP, loInputParamMap);
			// save task details in filenet
			loChannel.setData(ApplicationConstants.WORKFLOW_ID, lsWorkflowId);
			loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
			loChannel.setData(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			loChannel.setData(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolMappingId);
			loChannel.setData(P8Constants.PROPERTY_PE_TASK_STATUS, ApplicationConstants.STATUS_APPROVED);
			// Start Updated in R5
			if (null != lsNegotiationRequired && lsNegotiationRequired.equalsIgnoreCase(HHSConstants.TRUE))
			{
				HHSTransactionManager.executeTransaction(loChannel,
						HHSR5Constants.FINISH_AWARD_DOCUMENT_WITH_NEGOTIATIONS, HHSR5Constants.TRANSACTION_ELEMENT_R5);
			}
			else
			{
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FINISH_AWARD_DOCUMENT_TASK);
			}
			// End Updated in R5
		}
		// Handling Application Exception thrown by Transaction layer
		catch (ApplicationException aoAppEx)
		{
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loHmReqExceProp, aoAppEx);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.CONFIGURE_AWARD_DOC_TASK);
			aoResponse.setRenderParameter(HHSConstants.WOB_NUMBER, lsWorkflowId);
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occured while processing procurements", aoEx);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.CONFIGURE_AWARD_DOC_TASK);
			aoResponse.setRenderParameter(HHSConstants.WOB_NUMBER, lsWorkflowId);
		}
		aoResponse.setRenderParameter(HHSConstants.RETURN_TO_AGENCY_TASK, HHSConstants.TRUE);
	}

	/**
	 * This method is executed when an agency user opens Accept Proposal Task
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Modification done as a part of Enhancement #5688 for Release 3.2.0</li>
	 * <li>Fetch required parameters from request like WorkflowId, UserId, User
	 * orgType</li>
	 * <li>Set channel with required parameters for fetching procurement and
	 * task details, RFP documents, proposal Documents, taskHistoryDetails,
	 * public provider and internal comments</li>
	 * <li>Invoke the transaction <b>fetchProposalTaskDetails</b></li>
	 * <li>Set transaction outputs in request</li>
	 * </ul>
	 * 
	 * @param aoRequest - Render Request
	 * @param aoResponse - Render Response
	 * @return ModelAndView containing JSP name on which user has to be
	 *         redirected
	 * 
	 *         calls transaction 'fetchEvaluationScoresTask'
	 * 
	 */
	@SuppressWarnings(
	{ "unchecked" })
	@RenderMapping(params = "render_action=fetchReviewScoresTask")
	protected ModelAndView fetchReviewScoresTask(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		String lsControllerAction = aoRequest.getParameter(HHSConstants.CONTROLLER_ACTION);
		try
		{
			String lsWobNumber = aoRequest.getParameter(HHSConstants.WOB_NUMBER);
			P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			// fetching procurement and task details
			Channel loChannel = new Channel();
			loChannel.setData(ApplicationConstants.WOB_NUMBER, lsWobNumber);
			loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
			loChannel.setData(ApplicationConstants.KEY_SESSION_USER_ID, lsUserId);
			loChannel.setData(ApplicationConstants.REQ_PROPS_TASK_HASHMAP, HHSUtil.getTaskPropertiesHashMap());
			// for fetching unassigned Acco staff users
			List<String> loUserRoleList = new ArrayList<String>();
			loUserRoleList.add(HHSConstants.ACCO_MANAGER_ROLE);
			loUserRoleList.add(HHSConstants.ACCO_STAFF_ROLE);
			loUserRoleList.add(HHSConstants.ACCO_ADMIN_STAFF_ROLE);
			loChannel.setData(ApplicationConstants.ACCELERATOR_USER_ROLE_LIST, loUserRoleList);
			// fetching internal and provider comments
			TaskDetailsBean loTaskDetailBean = new TaskDetailsBean();
			loTaskDetailBean.setEntityType(HHSConstants.REVIEW_EVALUATION_TASK);
			loTaskDetailBean.setEntityTypeForAgency(HHSConstants.SAVED_EVALUATION);
			loChannel.setData(ApplicationConstants.TASK_DETAILS_BEAN_KEY, loTaskDetailBean);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_EVALUATION_SCORES_TASK);
			HashMap<String, Object> loTaskDetailMap = (HashMap<String, Object>) loChannel
					.getData(ApplicationConstants.TASK_DETAIL_MAP);
			TaskDetailsBean loTaskDetailsBean = HHSUtil.getTaskDetailsBeanFromMap(loTaskDetailMap, lsWobNumber);
			aoRequest.setAttribute(ApplicationConstants.TASK_DETAILS_BEAN, loTaskDetailsBean);
			List<EvaluationBean> loEvaluationList = (List<EvaluationBean>) loChannel
					.getData(HHSConstants.TRAN_RESULT_EVAL_SCORE);
			List<EvaluationBean> loEvaluatorScoreList = (List<EvaluationBean>) loChannel
					.getData(HHSConstants.LOCAL_EVAL_SCORES_LIST);
			List<EvaluationBean> loEvalCommentsList = (List<EvaluationBean>) loChannel
					.getData(HHSConstants.LOCAL_EVAL_COMMENTS_LIST);
			// Get the permitted user list for reassign dropdown
			List<UserBean> loUserBeanList = (List<UserBean>) loChannel
					.getData(ApplicationConstants.PERMITTED_USER_LIST);
			loTaskDetailBean = (TaskDetailsBean) loChannel.getData(HHSConstants.AS_TASK_COMMENT);
			// Start Updated in R5
			aoRequest.setAttribute(HHSR5Constants.REQUEST_AMEND_FLAG,
					(String) loChannel.getData(HHSR5Constants.LO_REQUEST_AMEND_FLAG));
			// End Updated in R5
			aoRequest.setAttribute(ApplicationConstants.REASSIGN_USER_MAP, HHSUtil.getReassignUserMap(loUserBeanList));
			aoRequest.setAttribute(HHSConstants.WORKFLOW_ID, lsWobNumber);
			aoRequest.setAttribute(HHSConstants.SCREEN_READ_ONLY,
					checkReadOnlyTask(loTaskDetailMap, lsWobNumber, lsUserId));
			aoRequest.setAttribute(HHSConstants.EVAL_RESULT_LIST, loEvaluationList);
			aoRequest.setAttribute(HHSConstants.EVAL_SCORES_LIST, loEvaluatorScoreList);
			aoRequest.setAttribute(HHSConstants.EVAL_COMMENT_LIST, loEvalCommentsList);
			aoRequest.setAttribute(HHSConstants.AO_TASK_DETAILS_BEAN, loTaskDetailBean);
			aoRequest.setAttribute(ApplicationConstants.INTERNAL_COMMENTS, loTaskDetailBean.getInternalComment());
			String lsErrorMsg = (String) aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
		// handling Application Exception thrown by transaction layer
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured while processing review scores task details", aoAppEx);
			setGenericErrorMessage(aoRequest);
		}
		// handling Exception other than Application Exception
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occured while processing review scores task details", aoEx);
			setGenericErrorMessage(aoRequest);
		}
		// Start of Changes done for enhancement QC : 5688
		if (null != lsControllerAction && !lsControllerAction.isEmpty())
		{
			return new ModelAndView(HHSConstants.AGENCY_WORKFLOW_MODEL_VIEW_CITY);
		}
		else
		{
			return new ModelAndView(HHSConstants.AGENCY_WORKFLOW_MODEL_VIEW);
		}
		// End of Changes done for enhancement QC : 5688
	}

	/**
	 * This method handles action when reassign button is clicked from S247
	 * 
	 * <ul>
	 * <li>Get request and session parameters like: userId, workflowId,taskId,
	 * proposalId, P8UserSession, ReassignedTo, ReassignedToUserName</li>
	 * <li>Set task details in channel for reassigning task</li>
	 * <li>Execute query with Id "reassignTask" for reassigning task to other
	 * user</li>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse object
	 * 
	 *            calls the transaction 'reassignTask'
	 */
	@ActionMapping(params = "submit_action=reassignReviewScoresTask")
	protected void reassignReviewScoresTask(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		String lsWorkflowId = aoRequest.getParameter(HHSConstants.WORKFLOW_ID);
		String lsAssignedToUserId = aoRequest.getParameter(ApplicationConstants.REASSIGNED_TO);
		String lsAssignedToUserName = aoRequest.getParameter(ApplicationConstants.REASSIGNED_TO_USER_NAME);
		try
		{
			TaskDetailsBean loTaskBean = new TaskDetailsBean();
			loTaskBean.setWorkFlowId(lsWorkflowId);
			loTaskBean.setReassignUserId(lsAssignedToUserId);
			loTaskBean.setReassignUserName(lsAssignedToUserName);
			P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			// save assigned to field for selected user
			Channel loChannel = new Channel();
			loChannel.setData(ApplicationConstants.TASK_DETAILS_BEAN_KEY, loTaskBean);
			loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.REASSIGN_TASK);
		}
		// handling exception thrown by service layer
		catch (ApplicationException aoAppEx)
		{
			Map<String, String> loParamMap = new HashMap<String, String>();
			loParamMap.put(HHSConstants.AS_WORKFLOW_ID, lsWorkflowId);
			loParamMap.put(HHSConstants.REASSIGNED_TO, lsAssignedToUserId);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap, aoAppEx);
			LOG_OBJECT.Error("Exception Occured while reassigning task:", aoAppEx);
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while reassigning task:", aoEx);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
		aoResponse.setRenderParameter(HHSConstants.RETURN_TO_AGENCY_TASK, HHSConstants.TRUE);
	}

	/**
	 * This method handles action when save button is clicked from S248 screen
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Get request and session parameters like: userId, workflowId,taskId,
	 * proposalId</li>
	 * <li>If provider and internal comments are not null, add audit bean object
	 * by calling method getBeanForSavingUserComments() for saving user comments
	 * </li>
	 * <li>Get extended document bean object if document status is changed by
	 * calling method processProposalDocumentDetails() and set it to channel</li>
	 * <li>Execute transaction with Id "saveEvaluationReviewTaskDetails"</li>
	 * </li>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse object
	 * 
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@ActionMapping(params = "submit_action=saveReviewScoresTaskDetails")
	protected void saveReviewScoresTaskDetails(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		String lsUserId = null;
		String lsUserOrg = null;
		String lsWorkflowId = null;
		String lsTaskId = null;
		String lsProposalId = null;
		EvaluationBean loEvaluationBean = null;
		Enumeration<String> loRequestParamNames = aoRequest.getParameterNames();
		List<EvaluationBean> loEvaluationList = new ArrayList<EvaluationBean>();
		try
		{
			P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);
			lsUserOrg = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
					PortletSession.APPLICATION_SCOPE);
			lsWorkflowId = aoRequest.getParameter(HHSConstants.WORKFLOW_ID);
			lsTaskId = aoRequest.getParameter(HHSConstants.TASK_ID);
			lsProposalId = aoRequest.getParameter(HHSConstants.PROPOSAL_ID);
			// Get provider and internal comments
			String lsInternalComments = aoRequest.getParameter(ApplicationConstants.INTERNAL_COMMENTS);
			Channel loChannel = new Channel();
			List<HhsAuditBean> loUserCommentsList = null;
			// Check for null value of internal comments
			if (null != lsInternalComments && !lsInternalComments.isEmpty())
			{
				loUserCommentsList = new ArrayList<HhsAuditBean>();
				// get bean for saving comments in user comments table
				HhsAuditBean loHhsAuditBean = HHSUtil.getBeanForSavingUserComments(lsTaskId, lsWorkflowId,
						HHSConstants.SAVED_EVALUATION, lsProposalId, lsUserId, lsUserOrg, null, lsInternalComments);
				loUserCommentsList.add(loHhsAuditBean);
				loChannel.setData(ApplicationConstants.AUDIT_BEAN_LIST, loUserCommentsList);
			}
			// get action for evaluators
			String lsActionValue = null;
			while (loRequestParamNames.hasMoreElements())
			{
				String lsParamName = loRequestParamNames.nextElement();
				if (lsParamName.startsWith(HHSConstants.ACTION_TAG_UNDERSCORE))
				{
					loEvaluationBean = new EvaluationBean();
					loEvaluationBean.setEvaluationStatusId(lsParamName.replace(HHSConstants.ACTION_TAG_UNDERSCORE,
							HHSConstants.EMPTY_STRING));
					loEvaluationBean.setProposalId(lsProposalId);
					lsActionValue = aoRequest.getParameter(lsParamName);
					if (lsActionValue != null && lsActionValue.equalsIgnoreCase(HHSConstants.ONE))
					{
						loEvaluationBean.setProcStatusId(PropertyLoader.getProperty(
								HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_REVIEW_PROPOSAL_TASK_ACCEPTED));
					}
					else if (lsActionValue != null && lsActionValue.equalsIgnoreCase(HHSConstants.TWO))
					{
						loEvaluationBean.setProcStatusId(PropertyLoader.getProperty(
								HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_REVIEW_PROPOSAL_TASK_SCORES_RETURNED));
					}
					else
					{
						loEvaluationBean.setProcStatusId(PropertyLoader.getProperty(
								HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_REVIEW_PROPOSAL_TASK_IN_REVIEW));
					}
					loEvaluationList.add(loEvaluationBean);
				}
			}
			loChannel.setData(HHSConstants.EVALUATOR_LIST, loEvaluationList);
			HashMap loHmWFProperties = new HashMap();
			loHmWFProperties.put(P8Constants.PE_WORKFLOW_LAST_MODIFIED_DATE, new Date());
			loChannel.setData(HHSConstants.REQ_PROPS_HASHMAP, loHmWFProperties);
			loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
			loChannel.setData(HHSConstants.WORKFLOW_ID, lsWorkflowId);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.SAVE_EVALUATION_REVIEW_TASK);
		}
		// handling exception thrown by transaction layer.
		catch (ApplicationException aoAppEx)
		{
			Map<String, String> loParamMap = new HashMap<String, String>();
			loParamMap.put(HHSConstants.WORKFLOW_ID, lsWorkflowId);
			loParamMap.put(HHSConstants.PROPOSAL_ID, lsProposalId);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap, aoAppEx);
			LOG_OBJECT.Error("Exception Occured while saving review scores task details: ", aoAppEx);
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while saving review scores task details: ", aoEx);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
	}

	/**
	 * This method handles action when finish button is clicked from S243
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Get request and session parameters like: userId, workflowId,taskId,
	 * proposalId, P8UserSession</li>
	 * <li>Get process task status Id based on proposal task status from request
	 * </li>
	 * <li>Get notification map by calling method
	 * getNotificationMapForProposalTask() and set in channel</li>
	 * <li>If provider and internal comments are not null, add audit bean object
	 * by calling method addAuditDataToChannel() from HHSUtil</li>
	 * <li>Get audit bean object if document status is changed by calling method
	 * getAuditBeanForStatusChangedRecords() and set it to channel</li>
	 * <li>Set task details in channel and execute transaction with Id
	 * "finishEvaluationReviewTask"</li>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse object
	 * @throws ApplicationException 
	 * 
	 */
	@ActionMapping(params = "submit_action=finishReviewScoresTask")
	protected void finishReviewScoresTask(ActionRequest aoRequest, ActionResponse aoResponse) throws ApplicationException
	{   LOG_OBJECT.Debug("======finishReviewScoresTask===="); 
		String lsWorkflowId = null;
		P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		HashMap<String, Object> loFinishWFPropMap = new HashMap<String, Object>();
		Enumeration<String> loRequestParamNames = aoRequest.getParameterNames();
		List<EvaluationBean> loEvaluationList = new ArrayList<EvaluationBean>();
		EvaluationBean loEvaluationBean = null;
		try
		{
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			String lsUserOrg = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);
			lsWorkflowId = aoRequest.getParameter(HHSConstants.WORKFLOW_ID);
			String lsTaskId = aoRequest.getParameter(HHSConstants.TASK_ID);
			String lsTaskStatus = aoRequest.getParameter(HHSConstants.PROPERTY_PE_TASK_STATUS_LOWER);
			String lsProposalId = aoRequest.getParameter(HHSConstants.PROPOSAL_ID);
			String lsProcurementId = aoRequest.getParameter(HHSConstants.PROCUREMENT_ID);
			String lsAverageScore = aoRequest.getParameter(HHSConstants.SCORE);
			String lsFinalTaskStatus = HHSConstants.TASK_FINISHED;
			String lsEvalPoolMappingId = aoRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID);
			loFinishWFPropMap.put(P8Constants.PROPERTY_PE_IS_TASK_VISSIBLE, Boolean.FALSE);
			// Get provider and internal comments
			String lsInternalComments = aoRequest.getParameter(ApplicationConstants.INTERNAL_COMMENTS);
			Channel loChannel = new Channel();
			// Check for null value of internal comments
			if (null != lsInternalComments && !lsInternalComments.isEmpty())
			{
				List<HhsAuditBean> loUserCommentsList = new ArrayList<HhsAuditBean>();
				// get bean for saving comments in user comments table
				HhsAuditBean loHhsAuditBean = HHSUtil.getBeanForSavingUserComments(lsTaskId, lsWorkflowId,
						HHSConstants.REVIEW_EVALUATION_TASK, lsProposalId, lsUserId, lsUserOrg, null,
						lsInternalComments);
				loHhsAuditBean.setIsTaskScreen(false);
				loUserCommentsList.add(loHhsAuditBean);
				loChannel.setData(ApplicationConstants.AUDIT_BEAN_LIST, loUserCommentsList);
			}
			// get action for evaluators
			String lsActionValue = null;
			List<String> loListReturned = new ArrayList<String>();
			while (loRequestParamNames.hasMoreElements())
			{
				String lsParamName = loRequestParamNames.nextElement();
				if (lsParamName.startsWith(HHSConstants.ACTION_TAG_UNDERSCORE))
				{
					loEvaluationBean = new EvaluationBean();
					loEvaluationBean.setEvaluationStatusId(lsParamName.replace(HHSConstants.ACTION_TAG_UNDERSCORE,
							HHSConstants.EMPTY_STRING));
					loEvaluationBean.setProposalId(lsProposalId);
					lsActionValue = aoRequest.getParameter(lsParamName);
					if (lsActionValue != null && lsActionValue.equalsIgnoreCase(HHSConstants.ONE))
					{
						loEvaluationBean.setProcStatusId(PropertyLoader.getProperty(
								HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_REVIEW_PROPOSAL_TASK_ACCEPTED));
					}
					else if (lsActionValue != null && lsActionValue.equalsIgnoreCase(HHSConstants.TWO))
					{
						loListReturned.add(loEvaluationBean.getEvaluationStatusId());
						loEvaluationBean.setProcStatusId(PropertyLoader.getProperty(
								HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_EVALUATE_PROPOSAL_TASK_SCORES_RETURNED));
					}
					else
					{
						loEvaluationBean.setProcStatusId(PropertyLoader.getProperty(
								HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_REVIEW_PROPOSAL_TASK_IN_REVIEW));
					}
					loEvaluationList.add(loEvaluationBean);
				}
			}
			LOG_OBJECT.Debug("======loListReturned :: "+loListReturned);
			LOG_OBJECT.Debug("======loEvaluationList :: "+loEvaluationList);
			finishEvaluationReviewTask(aoRequest, lsWorkflowId, loUserSession, loFinishWFPropMap, loEvaluationList,
					lsUserId, lsTaskStatus, lsProposalId, lsProcurementId, lsAverageScore, lsFinalTaskStatus,
					lsEvalPoolMappingId, loChannel, loListReturned);
		}
		// handling exception thrown by transaction layer.
		catch (ApplicationException aoAppEx)
		{
			Map<String, String> loParamMap = new HashMap<String, String>();
			loParamMap.put(HHSConstants.WORKFLOW_ID, lsWorkflowId);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap, aoAppEx);
			LOG_OBJECT.Error("Exception Occured while saving accept proposal task details: ", aoAppEx);
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while saving accept proposal task details: ", aoEx);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
		aoResponse.setRenderParameter(HHSConstants.RETURN_TO_AGENCY_TASK, HHSConstants.TRUE);
	}

	/**
	 * This method is used to finish the Evaluation review task
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest object
	 * @param asWorkflowId Work Flow Id
	 * @param aoUserSession P8UserSession Object
	 * @param aoFinishWFPropMap WorkFlowMa;pp
	 * @param aoEvaluationList Evaluation List
	 * @param asUserId User Id
	 * @param asTaskStatus Task Status
	 * @param asProposalId Proposal Id
	 * @param asProcurementId Procurement ID
	 * @param asAverageScore Average Score
	 * @param asFinalTaskStatus Final Task Status
	 * @param asEvalPoolMappingId Evaluation Pool Mappping Id
	 * @param aoChannel Channel Object
	 * @param aoListReturned
	 * @throws ApplicationException
	 */
	private void finishEvaluationReviewTask(ActionRequest aoRequest, String asWorkflowId, P8UserSession aoUserSession,
			HashMap<String, Object> aoFinishWFPropMap, List<EvaluationBean> aoEvaluationList, String asUserId,
			String asTaskStatus, String asProposalId, String asProcurementId, String asAverageScore,
			String asFinalTaskStatus, String asEvalPoolMappingId, Channel aoChannel, List<String> aoListReturned)
			throws ApplicationException
	{   LOG_OBJECT.Debug("******Start finishEvaluationReviewTask******");
		aoChannel.setData(HHSConstants.EVALUATOR_LIST, aoEvaluationList);
		aoChannel.setData(HHSConstants.AO_FILENET_SESSION, aoUserSession);
		aoChannel.setData(HHSConstants.AS_STATUS, asTaskStatus);
		aoChannel.setData(HHSConstants.STAT, asFinalTaskStatus);
		aoChannel.setData(HHSConstants.AS_WORKFLOW_ID, asWorkflowId);
		aoChannel.setData(HHSConstants.AS_USER_ID, asUserId);
		aoChannel.setData(HHSConstants.SCORE, asAverageScore);
		aoChannel.setData(HHSConstants.PROPOSAL_ID, asProposalId);
		aoChannel.setData(HHSConstants.LO_HM_WF_REQ_PROPS, aoFinishWFPropMap);
		aoChannel.setData(HHSConstants.LO_LIST_RETURNED, aoListReturned);
		aoChannel.setData(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		aoChannel.setData(HHSConstants.UPDATE, true);
		aoChannel.setData(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvalPoolMappingId);
		List<String> loAlertList = new ArrayList<String>();
		loAlertList.add(HHSConstants.NT220);
		aoChannel.setData(HHSConstants.LO_HM_NOTIFY_PARAM, getNotificationMapForScoreAmendment(aoRequest, loAlertList));
		// Start R5 : set EntityId and EntityName for AutoSave
		CommonUtil.setChannelForAutoSaveData(aoChannel, asWorkflowId, HHSR5Constants.TASKS);
		// End R5 : set EntityId and EntityName for AutoSave
		HHSTransactionManager.executeTransaction(aoChannel, HHSConstants.FINISH_EVALUATION_REVIEW_TASK);
		LOG_OBJECT.Debug("******Finish finishEvaluationReviewTask******");
		// Start QC 9017 R 8.9 Evaluators does not see the task when ACCO returns their score
		
		LOG_OBJECT.Debug("******Start finishReviewScoreWF******");
		HHSTransactionManager.executeTransaction(aoChannel, HHSConstants.FINISH_REVIEW_SCORE_WF); //FileNet/Component
		LOG_OBJECT.Debug("******Finish finishReviewScoreWF******");
		
		// End QC 9017 R 8.9 Evaluators does not see the task when ACCO returns their score
	}

	/**
	 * This method generates notification map for Accept Proposal task depending
	 * upon input alert list
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Create a local object Notification map</li>
	 * <li>Create the Notification Alert List that is added to the Notification
	 * Map</li>
	 * <li>Creates the request param map</li>
	 * <li>Create NotificationDataBean type object and set the provider list,
	 * agency list, linkMap and agencyLinkMap attributes</li>
	 * <li>Then add the request map, created by, modified by, Entity ID, Entity
	 * Type,NotificationDataBean to the Notification map</li>
	 * <li>Set dynamic parameters to be replaced in request map according to the
	 * value of the request score amendment boolean flag</li>
	 * <li>Set alert list in map and return modified notification map</li>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest object
	 * @param aoAlertList a list of notification ID to be sent
	 * @return a notification hashmap
	 * @throws ApplicationException In case exception occurs
	 */
	private HashMap<String, Object> getNotificationMapForScoreAmendment(ActionRequest aoRequest,
			List<String> aoAlertList) throws ApplicationException
	{
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		loNotificationMap.put(HHSConstants.NOTIFICATION_ALERT_ID, aoAlertList);
		try
		{
			HashMap<String, String> loRequestMap = new HashMap<String, String>();
			StringBuffer loApplicationUrl = new StringBuffer(256);
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			loApplicationUrl.append(aoRequest.getScheme()).append(HHSConstants.NOTIFICATION_HREF_1)
					.append(aoRequest.getServerName()).append(HHSConstants.COLON).append(aoRequest.getServerPort())
					.append(aoRequest.getContextPath()).append(HHSConstants.AGENCY_TASK_INBOX_URL);

			for (String lsALertId : aoAlertList)
			{
				NotificationDataBean loNotificationDataBean = new NotificationDataBean();
				HashMap<String, String> loLinkMap = new HashMap<String, String>();
				loLinkMap.put(HHSConstants.LINK, loApplicationUrl.toString());
				loNotificationDataBean.setLinkMap(loLinkMap);
				loNotificationDataBean.setAgencyLinkMap(loLinkMap);
				loNotificationMap.put(lsALertId, loNotificationDataBean);
			}
			loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
			loNotificationMap.put(ApplicationConstants.ENTITY_ID, aoRequest.getParameter(HHSConstants.PROPOSAL_ID));
			loNotificationMap.put(ApplicationConstants.ENTITY_TYPE, HHSConstants.PROPOSAL);
			loNotificationMap.put(HHSConstants.CREATED_BY_USER_ID, lsUserId);
			loNotificationMap.put(HHSConstants.MODIFIED_BY, lsUserId);
		}
		// handling Exception thrown by the application
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error was occurred while get notfication", aoEx);
			throw new ApplicationException("Error was occurred while get notfication", aoEx);
		}
		return loNotificationMap;
	}

	/**
	 * Change done for enhancement 6534 with Release 3.8.0
	 * 
	 * This method will fetch the details of the selected document and display
	 * the properties to the end user This method will execute the method
	 * <b>actionViewDocumentInfo</b> method of <b>FileNetOperationsUtils</b> and
	 * the above mention method will execute one R1 transaction with the
	 * transaction id <b>displayDocProp_filenet</b> class and then set the
	 * render action for document view Modification done as a part of
	 * Enhancement #5688 for Release 3.2.0
	 * 
	 * @param aoRequest - ActionRequest Object
	 * @param aoResponse - ActionResponse Object
	 */
	@ActionMapping(params = "submit_action=viewTaskDetails")
	protected void viewTaskDetails(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		String lsTaskType = aoRequest.getParameter(HHSConstants.TASK_TYPE);
		String lsControllerName = HHSConstants.TASK_TYPE_CONTROLLER_MAP.get(lsTaskType);
		String lsWorkFlowId = aoRequest.getParameter(HHSConstants.WOB_NUMBER);
		String lsChoosenTab = aoRequest.getParameter(ApplicationConstants.CHOOSEN_TAB);
		String lsControllerAction = aoRequest.getParameter(HHSConstants.CONTROLLER_ACTION);
		// Start || Change done for enhancement 6534 with Release 3.8.0
		String lsAgencyId = aoRequest.getParameter(HHSConstants.AGENCYID);
		try
		{
			// Start R5 : set EntityId and EntityName for AutoSave
			CommonUtil.setSessionForAutoSaveData(aoRequest.getPortletSession(), lsWorkFlowId, HHSR5Constants.TASKS);
			// End R5 : set EntityId and EntityName for AutoSave
			if (null == lsControllerName && null != lsTaskType)
			{
				if (lsTaskType.equalsIgnoreCase(HHSConstants.ACCEPT_PROPOSAL))
				{
					aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION,
							HHSConstants.SHOW_ACCEPT_PROPOSAL_TASK_DETAILS);
				}
				else if (lsTaskType.equalsIgnoreCase(HHSConstants.EVALUATE_PROPOSAL))
				{
					aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION,
							HHSConstants.SHOW_EVALUATE_PROPOSAL_TASK_DETAILS);
				}
				else if (lsTaskType.equalsIgnoreCase(HHSConstants.CONFIG_AWARD_DOC))
				{
					aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.CONFIGURE_AWARD_DOC_TASK);
				}
				else if (lsTaskType.equalsIgnoreCase(HHSConstants.REVIEW_SCORES))
				{
					aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.FETCH_REVIEW_SCORE_TASK);
				}
				// Release 5 change starts
				else if (lsTaskType.equalsIgnoreCase(HHSR5Constants.TASK_COMPLETE_PSR))
				{
					aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSR5Constants.SHOW_COMPLETE_PSR_TASK);
				}
				else if (lsTaskType.equalsIgnoreCase(HHSR5Constants.FINALIZE_AWARD_AMOUNT))
				{
					aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSR5Constants.FINALIZE_AWARD_TASK);
				}
				// Release 5 change ends
				aoResponse.setRenderParameter(HHSConstants.WOB_NUMBER, lsWorkFlowId);
				// Start of Changes done for enhancement QC : 5688
				if (null != lsControllerAction && !lsControllerAction.isEmpty())
				{
					aoResponse
							.setRenderParameter(HHSConstants.CONTROLLER_ACTION, HHSConstants.AGENCY_WORKFLOW_FOR_CITY);
				}
				// End of Changes done for enhancement QC : 5688
			}
			else
			{
				// Start || Change done for enhancement 6534 with Release 3.8.0
				Channel loChannel = new Channel();
				HashMap<String, String> loAgencyDetailsMap = new HashMap<String, String>();
				loAgencyDetailsMap.put(HHSConstants.TASK_TYPE, lsTaskType);
				loAgencyDetailsMap.put(HHSConstants.AGENCYID, lsAgencyId);
				loChannel.setData(HHSConstants.AGENCY_DETAILS_MAP, loAgencyDetailsMap);
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_REVIEW_PROGRESS_FLAG);
				String loReviewInProgressFlag = (String) loChannel.getData(HHSConstants.REVIEW_PROGRESS_FLAG);
				if ((null != loReviewInProgressFlag && loReviewInProgressFlag.equalsIgnoreCase(HHSConstants.YES))
						&& (null == lsControllerAction || lsControllerAction.isEmpty()))
				{
					aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGE_PROP_FILE, HHSConstants.ERROR_MESSSAGE_REVIEW_LEVEL_AGENCY));
					aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
							ApplicationConstants.MESSAGE_FAIL_TYPE);
				}
				else
				{
					// End || Change done for enhancement 6534 with Release
					// 3.8.0
					String lsControllerPagePath = aoRequest.getScheme() + HHSConstants.NOTIFICATION_HREF_1
							+ aoRequest.getServerName() + HHSConstants.COLON + aoRequest.getServerPort()
							+ aoRequest.getContextPath() + ApplicationConstants.PORTAL_URL + lsControllerName + "&"
							+ HHSConstants.WORKFLOW_ID + "=" + lsWorkFlowId + "&" + HHSConstants.RENDER_ACTION + "="
							+ lsTaskType;
					/** [Start] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
					aoResponse.sendRedirect(HHSUtil.sanitizeCarriageReturns(lsControllerPagePath));
					/** [End] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
				}
			}
			ApplicationSession.setAttribute(lsChoosenTab, aoRequest, HHSConstants.SELECTED_TAB);
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
	}

	/**
	 * This method checks if the current user id has access to edit the screen
	 * or not
	 * 
	 * @param aoTaskMap a map containing task details
	 * @param asWobNumber a string value of wob number
	 * @param asUserId user id of the user accessing the screen
	 * @return boolean value of read only
	 */
	@SuppressWarnings("unchecked")
	private Boolean checkReadOnlyTask(HashMap<String, Object> aoTaskMap, String asWobNumber, String asUserId)
	{
		Boolean loIsReadOnly = Boolean.TRUE;
		LOG_OBJECT.Info("Entered into checkReadOnlyTask");
		if (null != aoTaskMap)
		{
			HashMap<String, Object> loTaskDetailMap = (HashMap<String, Object>) aoTaskMap.get(asWobNumber);
			if (null != loTaskDetailMap)
			{
				String lsAssignedTo = (String) loTaskDetailMap.get(P8Constants.PE_WORKFLOW_ASSIGNED_TO);
				if (lsAssignedTo != null && lsAssignedTo.equalsIgnoreCase(asUserId))
				{
					loIsReadOnly = Boolean.FALSE;
				}
			}
		}
		return loIsReadOnly;
	}

	/**
	 * This method create user drop down for financial task on click of Task
	 * Type in Task management screen filter
	 * <ul>
	 * <li>
	 * calls the transaction <b>'fetchAgencyDetails'</b></li>
	 * </ul>
	 * 
	 * @param aoRequest Request Object
	 * @param aoResponse Response Object
	 * @throws ApplicationException if any exception occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	@ResourceMapping("getReassignListFinance")
	public void getReassignListFinance(ResourceRequest aoRequest, ResourceResponse aoResponse)
	{
		StaffDetails loUserDetails = null;
		PrintWriter loOut = null;
		List<StaffDetails> loAgencyUserDetails = null;
		StringBuffer loUserOption = new StringBuffer();
		try
		{
			PortletSession loSession = aoRequest.getPortletSession();
			String lsUserRole = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ROLE,
					PortletSession.APPLICATION_SCOPE);
			// 3.2.0 enhancement for bulk assignment QC: 6361
			String lsUserOrg = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
					PortletSession.APPLICATION_SCOPE);
			// End 3.2.0 enhancement for bulk assignment QC: 6361
			String lsSessionUserName = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_NAME,
					PortletSession.APPLICATION_SCOPE);
			String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);
			P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);

			aoResponse.setContentType(HHSConstants.APPLICATION_JSON);
			loOut = aoResponse.getWriter();
			String lsTaskLevel = aoRequest.getParameter(HHSConstants.TASK_LEVEL);
			String lsTaskType = aoRequest.getParameter(HHSConstants.TASK_TYPE);
			String lsAgencyId = aoRequest.getParameter(HHSConstants.AGENCYID);
			String lsTaskDetails = aoRequest.getParameter(HHSConstants.TASK_DETAILS);
			// Start Added in R5
			String lsEntityId = aoRequest.getParameter(HHSConstants.ENTITY_ID);
			// End Added in R5
			// 3.2.0 enhancement for bulk assignment QC: 6361
			if (lsTaskDetails.equalsIgnoreCase(HHSConstants.SELECT_ALL_FLAG))
			{
				lsTaskLevel = lsTaskLevel.substring(lsTaskLevel.length() - 1, lsTaskLevel.length());
				lsAgencyId = lsUserOrg;
			}
			// End 3.2.0 enhancement for bulk assignment QC: 6361
			String lsProcessId = String.valueOf(HHSConstants.FINANCIAL_TASK_PROCESS_ID_MAP.get(lsTaskType));
			Channel loChannel = new Channel();

			loChannel.setData(HHSConstants.AS_AGENCY_ID, lsAgencyId);
			loChannel.setData(HHSConstants.AS_TASK_LEVEL, lsTaskLevel);
			loChannel.setData(HHSConstants.AS_PROCESS_ID, lsProcessId);
			// Start Updated in R5
			loChannel.setData(HHSConstants.TASK_TYPE, lsTaskType);
			loChannel.setData(HHSConstants.ENTITY_ID, lsEntityId);
			Map loMap = new HashMap();
			loMap.put(HHSConstants.F_WOB_NUM, lsTaskDetails);
			loChannel.setData(HHSConstants.AO_HMWF_REQUIRED_PROPS, loMap);
			loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_AGENCY_DETAILS);
			loAgencyUserDetails = (List<StaffDetails>) loChannel.getData(HHSConstants.AGENCY_USER_LIST);
			String lsAskAgainFlag = (String) loChannel.getData(HHSR5Constants.ASKFLAG);
			String lsLaunchFlag = (String) loChannel.getData(HHSConstants.STATUS_FLAG);
			// End Updated in R5
			Iterator loItr = loAgencyUserDetails.iterator();
			loUserOption.append("<option></option>");
			while (loItr.hasNext())
			{
				loUserDetails = (StaffDetails) loItr.next();
				if (lsUserRole.equalsIgnoreCase(HHSConstants.PROGRAM_STAFF_ROLE)
						|| lsUserRole.equalsIgnoreCase(HHSConstants.ACCO_STAFF_ROLE)
						|| lsUserRole.equalsIgnoreCase(HHSConstants.FINANCE_STAFF_ROLE))
				{
					if (loUserDetails.getMsStaffId().equalsIgnoreCase(lsUserId))
					{
						// Start Added in R5
						loUserOption.append("<option value=\\\"");
						loUserOption.append(lsUserId);
						loUserOption.append("\\\">");
						// End Updated in R5
						loUserOption.append(lsSessionUserName);
						loUserOption.append("</option>");
						break;
					}
				}
				else
				{ // Start Updated in R5
					loUserOption.append("<option value=\\\"");
					loUserOption.append(loUserDetails.getMsStaffId());
					loUserOption.append("\\\">").append(loUserDetails.getMsStaffFirstName()).append(HHSConstants.SPACE)
							.append(loUserDetails.getMsStaffLastName()).append("</option>");
					// End Updated in R5
				}
			}
			// Start Added in R5
			if (StringUtils.isNotBlank(lsLaunchFlag) && lsLaunchFlag.equals(HHSConstants.ZERO))
			{
				lsAskAgainFlag = HHSConstants.YES_UPPERCASE;
			}
			String lsJsonString = HHSR5Constants.MESSAGE_START + loUserOption.toString() + HHSR5Constants.MESSAGE_2
					+ (lsAskAgainFlag == null ? HHSConstants.EMPTY_STRING : lsAskAgainFlag)
					+ HHSConstants.CLOSING_BRACE_1;
			loOut.write(lsJsonString);
			// End Added in R5
		}
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("error occurred while getReassignListFinance:", loExp);
			setGenericErrorMessage(aoRequest);
		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("error occurred while getReassignListFinance :", loExp);
			setGenericErrorMessage(aoRequest);
		}
		finally
		{
			if (loOut != null)
			{
				loOut.flush();
				loOut.close();
			}
		}
	}

	/**
	 * This method added in Release 5, to create user drop down for financial
	 * task on click of Task Type in Task management screen filter
	 * <ul>
	 * <li>
	 * calls the transaction <b>'fetchAskAgainFlag'</b></li>
	 * </ul>
	 * 
	 * @param aoRequest Request Object
	 * @param aoResponse Response Object
	 * @throws ApplicationException if any exception occurs
	 */
	@ResourceMapping("getReassignListFinanceInbox")
	public void getReassignListFinanceInbox(ResourceRequest aoRequest, ResourceResponse aoResponse)
	{
		PrintWriter loOut = null;
		try
		{
			aoResponse.setContentType(HHSConstants.APPLICATION_JSON);
			loOut = aoResponse.getWriter();

			String lsTaskLevel = aoRequest.getParameter(HHSConstants.TASK_LEVEL);
			String lsTaskType = aoRequest.getParameter(HHSConstants.TASK_TYPE);
			String lsAgencyId = aoRequest.getParameter(HHSConstants.AGENCYID);
			String lsEntityId = aoRequest.getParameter(HHSConstants.ENTITY_ID);
			String lsProcessId = String.valueOf(HHSConstants.FINANCIAL_TASK_PROCESS_ID_MAP.get(lsTaskType));
			Channel loChannel = new Channel();
			loChannel.setData(HHSConstants.AS_AGENCY_ID, lsAgencyId);
			loChannel.setData(HHSConstants.AS_TASK_LEVEL, lsTaskLevel);
			loChannel.setData(HHSConstants.TASK_TYPE, lsTaskType);
			loChannel.setData(HHSConstants.AS_PROCESS_ID, lsProcessId);
			loChannel.setData(HHSConstants.ENTITY_ID, lsEntityId);
			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.FETCH_ASK_AGAIN_FLAG);
			String lsAskAgainFlag = (String) loChannel.getData(HHSR5Constants.ASKFLAG);
			String lsJsonString = HHSR5Constants.MESSAGE_START
					+ (lsAskAgainFlag == null ? HHSConstants.EMPTY_STRING : lsAskAgainFlag)
					+ HHSConstants.CLOSING_BRACE_1;
			loOut.write(lsJsonString);
		}
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("error occurred while getReassignListFinance:", loExp);
			setGenericErrorMessage(aoRequest);
		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("error occurred while getReassignListFinance :", loExp);
			setGenericErrorMessage(aoRequest);
		}
		finally
		{
			if (loOut != null)
			{
				loOut.flush();
				loOut.close();
			}
		}
	}

	/**
	 * This method create user drop down for Agency in box and management
	 * screens This method has been updated as a part of build 3.2.0 for
	 * enhancement #6361 on the basis of R2 and R3 Tasks
	 * 
	 * @param aoAgencyTaskBean Agency Task Detail Bean object
	 * @param asUserRole User Role
	 * @param asCurrentTab Current Tab
	 * @param asLoginUserId Login user Id
	 * @param asLoginUserName Login User Name
	 * @param asUserOrg User Org
	 * @return User HashMap
	 * @throws ApplicationException if any exception occurs
	 */
	private HashMap<String, String> creatUserDropDown(AgencyTaskBean aoAgencyTaskBean, String asUserRole,
			String asCurrentTab, String asLoginUserId, String asLoginUserName, String asUserOrg)
			throws ApplicationException
	{
		HashMap<String, String> loUserHashMap = new HashMap<String, String>();
		if (asCurrentTab != null && asCurrentTab.equalsIgnoreCase(P8Constants.PROPERTY_PAGE_TASK_MANAGMENT))
		{
			/*
			 * // For Task management screen if
			 * (asUserRole.equalsIgnoreCase(HHSConstants.PROGRAM_STAFF_ROLE) ||
			 * asUserRole.equalsIgnoreCase(HHSConstants.ACCO_STAFF_ROLE) ||
			 * asUserRole.equalsIgnoreCase(HHSConstants.FINANCE_STAFF_ROLE)) {
			 * loUserHashMap.put(asLoginUserId, asLoginUserName); } else {
			 */
			if (!HHSConstants.FINANCIAL_TASK_PROCESS_ID_MAP.containsKey(aoAgencyTaskBean.getTaskName()))
			{
				// fetch user list for R2 Task
				loUserHashMap = getUserRolesBasedOnTasks(aoAgencyTaskBean.getTaskName(), asUserOrg);
			}
			// Added extra check as a part of enhancement #6361 of build 3.2.0
			else if (HHSConstants.FINANCIAL_TASK_PROCESS_ID_MAP.containsKey(aoAgencyTaskBean.getTaskName()))
			{
				// fetch user list for Filtered Task
				loUserHashMap = getUserDetailsBasedOnFilterLevel(aoAgencyTaskBean);
			}
			// }
		}
		else
		{
			// for Inbox screen
			if (HHSConstants.FINANCIAL_TASK_PROCESS_ID_MAP.containsKey(aoAgencyTaskBean.getTaskName()))
			{
				// fetch user list for R3 Task
				loUserHashMap = fetchAgencyFinancialUserInbox(asUserOrg, asLoginUserId, aoAgencyTaskBean.getTaskName());
			}
			else
			{
				// fetch user list for R2 Task
				loUserHashMap = getUserRolesBasedOnTasks(aoAgencyTaskBean.getTaskName(), asUserOrg);
			}
		}
		return loUserHashMap;
	}

	/**
	 * This method fetches user list for Financial agency workflows for inbox
	 * screens
	 * <ul>
	 * <li>calls the transaction <b>fetchAgencyDetailsForInbox</b></li>
	 * </ul>
	 * 
	 * Changes made as a part of Enhancement #6280 for Release 3.3.0
	 * 
	 * @param asAgencyId Agency Id
	 * @param asUserId User Id
	 * @param asTaskName Task Name
	 * @return User HashMap
	 * @throws ApplicationException if any exception occurs
	 * 
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private HashMap<String, String> fetchAgencyFinancialUserInbox(String asAgencyId, String asUserId, String asTaskName)
			throws ApplicationException
	{ // Start || Changes made as a part of
		// Enhancement #6280 for Release
		// 3.3.0
		LinkedHashMap<String, String> loUserHashMap = new LinkedHashMap<String, String>();
		// End || Changes made as a part of Enhancement #6280 for Release 3.3.0
		StaffDetails loUserDetails = null;
		List<StaffDetails> loAgencyUserDetails = null;
		String lsProcessId = String.valueOf(HHSConstants.FINANCIAL_TASK_PROCESS_ID_MAP.get(asTaskName));
		Channel loChannel = new Channel();
		loChannel.setData(HHSConstants.AS_AGENCY_ID, asAgencyId);
		loChannel.setData(HHSConstants.AS_USER_ID, asUserId);
		loChannel.setData(HHSConstants.AS_PROCESS_ID, lsProcessId);
		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_AGENCY_DETAILS_INBOX);
		loAgencyUserDetails = (List<StaffDetails>) loChannel.getData(HHSConstants.AGENCY_USER_LIST);
		Iterator loItr = loAgencyUserDetails.iterator();
		while (loItr.hasNext())
		{
			loUserDetails = (StaffDetails) loItr.next();
			if (!asUserId.equalsIgnoreCase(loUserDetails.getMsStaffId()))
			{
				loUserHashMap.put(loUserDetails.getMsStaffId(), loUserDetails.getMsStaffFirstName()
						+ HHSConstants.SPACE + loUserDetails.getMsStaffLastName());
			}
		}

		return loUserHashMap;
	}

	/**
	 * THis method called on click on Task Type dropdown in Filters screens of
	 * Agency in box and management.
	 * <ul>
	 * <li>calls the transaction <b>fetchReviewLevelCB</b></li>
	 * <li>Get request and session parameters like: userId, User Role</li>
	 * <li>Depending upon task type it prepares assigned to field</li>
	 * <li>Modification done as a part of Enhancement #5688 for Release 3.2.0</li>
	 * <li>Condition modified while testing Enhancement #6280 for Release 3.3.0</li>
	 * </ul>
	 * 
	 * @param aoRequest Request Object
	 * @param aoResponse Response Object
	 * @throws ApplicationException if any exception occurs
	 * 
	 */
	@ResourceMapping("getAssignedToFilter")
	public void getAssignedToFilter(ResourceRequest aoRequest, ResourceResponse aoResponse)
	{
		PrintWriter loOut = null;
		StringBuffer loAssignedOption = new StringBuffer();
		Channel loChannel = new Channel();
		try
		{
			String lsUserRole = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ROLE, PortletSession.APPLICATION_SCOPE);
			// Start || Modification done as a part of Enhancement #5688 for
			// Release 3.2.0
			String lsAgencyId = aoRequest.getParameter("agencyId");
			String lsControllerAction = aoRequest.getParameter(HHSConstants.CONTROLLER_ACTION);
			String lsUserOrg = null;
			if (null != lsControllerAction && !lsControllerAction.isEmpty() && null != lsAgencyId
					&& !lsAgencyId.isEmpty())
			{
				lsUserOrg = lsAgencyId;
			}
			else
			{
				lsUserOrg = (String) aoRequest.getPortletSession().getAttribute(
						ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);
			}
			// End || Modification done as a part of Enhancement #5688 for
			// Release 3.2.0
			aoResponse.setContentType("text/html");
			loOut = aoResponse.getWriter();
			String lsTaskType = aoRequest.getParameter("taskType");
			loAssignedOption.append("<option></option><option value=\"All Staff\">All Staff</option>");
			if (HHSConstants.FINANCIAL_TASK_PROCESS_ID_MAP.containsKey(lsTaskType))
			{
				loChannel.setData(HHSConstants.REVIEW_PROC_ID,
						HHSConstants.FINANCIAL_TASK_PROCESS_ID_MAP.get(lsTaskType));
				loChannel.setData(HHSConstants.PROPERTY_PE_AGENCY_ID, lsUserOrg);
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_REVIEW_LEVEL_CB);

				Integer liReviewLevel = (Integer) loChannel.getData(HHSConstants.REVIEW_LEVEL);
				if (liReviewLevel == 0)
				{
					liReviewLevel = 1;
				}
				for (int liTemp = 1; liTemp <= liReviewLevel; liTemp++)
				{
					loAssignedOption.append("<option value=\"");
					loAssignedOption.append(HHSConstants.UNASSIGNED_LEVEL);
					loAssignedOption.append(liTemp);
					loAssignedOption.append("\">");
					loAssignedOption.append(HHSConstants.UNASSIGNED_LEVEL);
					loAssignedOption.append(liTemp);
					loAssignedOption.append("</option>");
				}
			}
			else
			{
				loAssignedOption.append("<option value=\"");
				loAssignedOption.append(HHSConstants.UNASSIGNED_ACCO_STAFF);
				loAssignedOption.append("\">");
				loAssignedOption.append(HHSConstants.UNASSIGNED_ACCO_STAFF);
				loAssignedOption.append("</option>");
				// Condition modified (lsControllerAction check added) while
				// testing Enhancement #6280 for Release 3.3.0
				if (lsUserRole.equalsIgnoreCase(HHSConstants.ACCO_MANAGER_ROLE)
						|| (null != lsControllerAction && !lsControllerAction.isEmpty()))
				{
					loAssignedOption.append("<option value=\"");
					loAssignedOption.append(HHSConstants.UNASSIGNED_ACCO_MANAGER);
					loAssignedOption.append("\">");
					loAssignedOption.append(HHSConstants.UNASSIGNED_ACCO_MANAGER);
					loAssignedOption.append("</option>");
				}
			}
			fetchAgencyUserDetails(loAssignedOption, lsUserOrg);
			loOut.write(loAssignedOption.toString());
		}
		// handling exception other than Application Exception.
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("Error occurred while populating assigned to dropdown value :", loExp);
			setGenericErrorMessage(aoRequest);
		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error occurred while populating assigned to dropdown value :", loExp);
			setGenericErrorMessage(aoRequest);
		}
		finally
		{
			if (loOut != null)
			{
				loOut.flush();
				loOut.close();
			}
		}
	}

	/**
	 * This method get the User List for input Agency Id and Task Type by
	 * calling 'fetchSMUserList' transaction
	 * 
	 * Changes made as a part of Enhancement #6280 for Release 3.3.0
	 * 
	 * @param asTaskType Task Type
	 * @param asUserOrg Agency Id
	 * @return User HashMap object
	 * @throws ApplicationException if any exception occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private HashMap<String, String> getUserRolesBasedOnTasks(String asTaskType, String asUserOrg)
			throws ApplicationException
	{
		List<String> loUserRoleList = null;
		List<UserBean> loUsers = null;
		// Start || Changes made as a part of Enhancement #6280 for Release
		// 3.3.0
		LinkedHashMap<String, String> loUserHashMap = new LinkedHashMap<String, String>();
		// End || Changes made as a part of Enhancement #6280 for Release 3.3.0
		UserBean loUserBean = null;
		Channel loChannel = new Channel();
		if (null != asTaskType)
		{
			loUserRoleList = new ArrayList<String>();
			if (asTaskType.equalsIgnoreCase(P8Constants.TASK_ACCEPT_PROPOSAL))
			{
				loUserRoleList.add(HHSConstants.ACCO_MANAGER_ROLE);
			}
			else if (asTaskType.equalsIgnoreCase(P8Constants.TASK_REVIEW_SCORES))
			{
				loUserRoleList.add(HHSConstants.ACCO_MANAGER_ROLE);
				loUserRoleList.add(HHSConstants.ACCO_STAFF_ROLE);
				loUserRoleList.add(HHSConstants.ACCO_ADMIN_STAFF_ROLE);
			}
			else if (asTaskType.equalsIgnoreCase(P8Constants.TASK_CONFIGURE_AWARDS_DOCUMENTS))
			{
				loUserRoleList.add(HHSConstants.ACCO_MANAGER_ROLE);
			}
			// Release 5 change starts
			else if (asTaskType.equalsIgnoreCase(HHSR5Constants.TASK_COMPLETE_PSR)
					|| asTaskType.equalsIgnoreCase(HHSR5Constants.FINALIZE_AWARD_AMOUNT))
			{
				loUserRoleList.add(HHSConstants.ACCO_MANAGER_ROLE);
			}
			// Release 5 change ends
			if (null != loUserRoleList && CollectionUtils.isNotEmpty(loUserRoleList))
			{
				loChannel.setData(HHSConstants.USER_ROLE_LIST, loUserRoleList);
				loChannel.setData(HHSConstants.ORGID, asUserOrg);
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_SM_USERS);
				loUsers = (List<UserBean>) loChannel.getData(HHSConstants.PERMITTED_USER_LIST);
				if (null != loUsers)
				{
					Iterator loItr = loUsers.iterator();
					while (loItr.hasNext())
					{
						loUserBean = (UserBean) loItr.next();
						loUserHashMap.put(loUserBean.getMsUserId(), loUserBean.getMsUserName());

					}
				}
			}
		}
		return loUserHashMap;
	}

	/**
	 * This method handles action when assign button is clicked from agency
	 * inbox and management screens
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Get request and session parameters like: userId, username, workflow
	 * ids</li>
	 * <li>Get audit bean object by calling method reassignAuditDataInChannel()
	 * and set it to channel</li>
	 * <li>Set task details in channel and execute transaction with Id
	 * "reassignMultiAgencyTask"</li>
	 * </ul>
	 * 
	 * @param aoRequest Request Object
	 * @param aoResponse Response Object
	 * 
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	@ActionMapping(params = "submit_action=assignAgencyTask")
	protected void assignAgencyTask(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		List<HhsAuditBean> loAuditList = new ArrayList<HhsAuditBean>();
		String lsTaskName = null;
		String lsEntityId = null;
		String lsChoosenTab = null;
		String lsTaskId = null;
		// Start Updated in R5
		String lsAskFlag = aoRequest.getParameter(HHSR5Constants.ASK_AGAIN);
		String lsWorkflowId = null;
		TaskDetailsBean loTaskDetailsBean = null;
		Channel loChannel = new Channel();
		boolean lbValidate = Boolean.TRUE;
		boolean lbIsFinancials = Boolean.FALSE;
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsAssignment = aoRequest.getParameter(HHSR5Constants.ASSIGNMENT);
		String lsHiddenAskFlagOverlay = aoRequest.getParameter("hiddenAskFlagOverlay");

		String lsTaskType = aoRequest.getParameter(HHSR5Constants.ASSIGNEE_TASKTYPE);
		try
		{
			String lsReassignedUserName = aoRequest.getParameter(HHSConstants.REASSIGNTOUSER_TEXT);
			String lsReassignedUserId = aoRequest.getParameter(HHSConstants.REASSIGN_TO_USER);
			String[] loReassignProp = aoRequest.getParameterValues(HHSConstants.CHECK);
			lsChoosenTab = aoRequest.getParameter(ApplicationConstants.CHOOSEN_TAB);

			if (null != loReassignProp && null != lsReassignedUserId && !lsReassignedUserId.isEmpty())
			{
				PortletSession loSession = aoRequest.getPortletSession();
				P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
						ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
				String lsUserRole = (String) aoRequest.getPortletSession().getAttribute(
						ApplicationConstants.KEY_SESSION_USER_ROLE, PortletSession.APPLICATION_SCOPE);
				String lsDelimiter = HHSConstants.DELIMITER_SINGLE_HASH;
				List<String> loWobList = new ArrayList<String>();
				String[] loBreakStr = null;
				List<TaskDetailsBean> loTaskDetailsBeanList = new ArrayList<TaskDetailsBean>();
				for (int liA = 0; liA < loReassignProp.length; liA++)
				{
					loBreakStr = loReassignProp[liA].split(lsDelimiter);
					lsWorkflowId = loBreakStr[0];
					loWobList.add(lsWorkflowId);
					lsTaskName = loBreakStr[1];
					lsEntityId = loBreakStr[2];
					lsTaskId = loBreakStr[3];
					if (lsChoosenTab.equalsIgnoreCase(P8Constants.PROPERTY_PAGE_TASK_MANAGMENT)
							&& !validateAssignTaskUser(aoResponse, loUserSession, lsUserRole, lsUserId, loBreakStr[0]))
					{
						if (!validateAssignTaskUser(aoResponse, loUserSession, lsUserRole, lsUserId, loBreakStr[0]))
						{
							lbValidate = Boolean.FALSE;
							break;
						}
					}

					loTaskDetailsBean = fetchLastTaskCommentForTaskDetailBean(aoRequest, loAuditList, lsTaskName,
							lsEntityId, lsTaskId, lsWorkflowId, loChannel, lsReassignedUserName, lsUserId, loBreakStr);
					if (lsHiddenAskFlagOverlay != null && lsHiddenAskFlagOverlay.equalsIgnoreCase("true"))
					{
						if (null !=loBreakStr && loBreakStr.length > 4)
						{
							loTaskDetailsBean.setAskFlag(loBreakStr[4]);
						}
					}
					else
					{
						loTaskDetailsBean.setAskFlag(lsAskFlag);
					}
					loTaskDetailsBean.setKeepDefault(aoRequest.getParameter(HHSR5Constants.KEEP_CURRENT_DEFAULT));
					loTaskDetailsBean.setDefaultAssignments(aoRequest.getParameter(HHSR5Constants.ASSIGNMENT));

					lsTaskType = HHSUtil.setTaskType(lsTaskType);
					loTaskDetailsBean.setTaskType(lsTaskType);
					loTaskDetailsBean.setTaskLevel(aoRequest.getParameter(HHSR5Constants.ASSIGNEE_TASKLEVEL));
					loTaskDetailsBean.setCreatedByUserId(lsUserId);
					loTaskDetailsBean.setModifiedByUserId(lsUserId);
					loTaskDetailsBean.setAssigneeUserId(aoRequest.getParameter(HHSR5Constants.ASSIGNEE_USERID));
					loTaskDetailsBean.setIsfinancials(aoRequest.getParameter(HHSR5Constants.IS_FINANCIALS));
					loTaskDetailsBean.setEntityId(lsEntityId);
					loTaskDetailsBean.setReassignUserId(lsReassignedUserId);
					loTaskDetailsBeanList.add(loTaskDetailsBean);
				}
				if (lbValidate)
				{
					HashMap loHmRequiredProps = new HashMap();
					loHmRequiredProps.put(HHSConstants.CURR_LEVEL, HHSConstants.EMPTY_STRING);
					loChannel = new Channel();
					loChannel.setData(HHSR5Constants.IS_FINANCIALS, lbIsFinancials);
					loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
					loChannel.setData(HHSConstants.KEY_SESSION_USER_NAME, lsReassignedUserName);
					loChannel.setData(HHSConstants.USER_ID, lsReassignedUserId);
					loChannel.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, Boolean.TRUE);
					loChannel.setData(HHSConstants.WOB_LIST, loWobList);
					loChannel.setData(HHSConstants.AO_TASK_DETAILS_BEAN, loTaskDetailsBean);
					loChannel.setData(HHSConstants.LO_AUDIT_LIST, loAuditList);
					loChannel.setData(HHSConstants.TASK_DETAILS_BEAN_LIST, loTaskDetailsBeanList);
					loChannel.setData(HHSConstants.REQ_PROPS_HASHMAP, loHmRequiredProps);
					loChannel.setData(HHSConstants.WOB_NUMBER, loWobList.get(0));
					HHSTransactionManager.executeTransaction(loChannel, HHSConstants.ASSIGN_MULTI_TASK);
					if (lsAssignment != null && lsAssignment.equalsIgnoreCase(ApplicationConstants.SYSTEM_YES))
					{
						aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, MessageFormat.format(
								PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
										HHSR5Constants.DEFAULT_ASSIGNEE_STATUS), lsReassignedUserName,
								loTaskDetailsBean.getTaskLevel(), loTaskDetailsBean.getTaskType()));
						aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
								ApplicationConstants.MESSAGE_PASS_TYPE);
					}
					//[Start] R9.6.0 QC9663 System Allowing Duplicate Records in DEFAULT_ASSIGNMENT Table - Task Details Not Appearing as a Result 
					HHSTransactionManager.executeTransaction(loChannel, HHSConstants.ASSIGN_MULTI_TASK_FILENET);
					//[End] R9.6.0 QC9663 System Allowing Duplicate Records in DEFAULT_ASSIGNMENT Table - Task Details Not Appearing as a Result 

				}
			}
			else
			{
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.USER_NOT_SELECTED));
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE);
			}
			// End Updated in R5
		}
		// handling exception thrown by transaction layer.
		catch (ApplicationException aoAppEx)
		{
			Map<String, String> loParamMap = new HashMap<String, String>();
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap, aoAppEx);
			LOG_OBJECT.Error("Exception Occured while reassigning  Agency task : ", aoAppEx);
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while reassigning  Agency task : ", aoEx);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
		ApplicationSession.setAttribute(lsChoosenTab, aoRequest, HHSConstants.SELECTED_TAB);
		aoResponse.setRenderParameter(HHSConstants.RETURN_TO_AGENCY_TASK, HHSConstants.RETURN_TO_AGENCY_TASK);
	}

	/**
	 * This method fetches the last task comment and updates it to task details
	 * bean.
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest Object
	 * @param aoAuditList list of type HhsAuditBean
	 * @param asTaskName Task Name
	 * @param asEntityId Entity Id
	 * @param asTaskId Task Id
	 * @param asWorkflowId WorkFlow Id
	 * @param aoChannel Channel Object
	 * @param asReassignedUserName
	 * @param asUserId User Id
	 * @param aoBreakStr array of type String
	 * @return
	 * @throws ApplicationException
	 */
	private TaskDetailsBean fetchLastTaskCommentForTaskDetailBean(ActionRequest aoRequest,
			List<HhsAuditBean> aoAuditList, String asTaskName, String asEntityId, String asTaskId, String asWorkflowId,
			Channel aoChannel, String asReassignedUserName, String asUserId, String[] aoBreakStr)
			throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setIsTaskScreen(true);
		loTaskDetailsBean.setTaskId(asTaskId);
		loTaskDetailsBean.setEntityType(asTaskName);
		loTaskDetailsBean.setWorkFlowId(aoBreakStr[0]);
		aoChannel.setData(ApplicationConstants.TASK_DETAILS_BEAN_KEY, loTaskDetailsBean);

		HHSTransactionManager.executeTransaction(aoChannel, HHSConstants.FETCH_LAST_TASK_COMMENT);
		loTaskDetailsBean = (TaskDetailsBean) aoChannel.getData(HHSConstants.AS_TASK_COMMENT);

		loTaskDetailsBean.setEntityId(asEntityId);
		if (asTaskName.equalsIgnoreCase(P8Constants.TASK_ACCEPT_PROPOSAL))
		{
			loTaskDetailsBean.setEntityType(HHSConstants.ACCEPT_PROPOSAL);
			loTaskDetailsBean.setEventType(HHSConstants.ACCEPT_PROPOSAL);
		}
		// R5 Change starts
		else if (asTaskName.equalsIgnoreCase(HHSR5Constants.TASK_COMPLETE_PSR))
		{
			loTaskDetailsBean.setEntityType(asTaskName);
			loTaskDetailsBean.setEventType(HHSR5Constants.TASK_COMPLETE_PSR);
		}
		else if (asTaskName.equalsIgnoreCase(HHSR5Constants.FINALIZE_AWARD_AMOUNT))
		{
			loTaskDetailsBean.setEntityType(asTaskName);
			loTaskDetailsBean.setEventType(HHSR5Constants.FINALIZE_AWARD_AMOUNT);
		}
		// R5 Change ends
		else
		{
			loTaskDetailsBean.setEntityType(asTaskName);
			loTaskDetailsBean.setEventType(P8Constants.EVENT_NAME_ASSIGN);
		}
		reassignAuditDataInChannel(aoAuditList, asUserId, asReassignedUserName, loTaskDetailsBean);
		// Added for task audit
		loTaskDetailsBean.setWorkFlowId(asWorkflowId);
		loTaskDetailsBean.setUserId(asUserId);
		return loTaskDetailsBean;
	}

	/**
	 * This method Validate whether Agency User can do assign operation or not
	 * .if not set error message
	 * <ul>
	 * <li>calls the transaction 'fetchCurrentAgencyTaskOwner'</li>
	 * </ul>
	 * 
	 * @param aoResponse Response object
	 * @param aoUserSession Filenet session
	 * @param asUserRole User Role
	 * @param asUserId User Id
	 * @param asWobNum Workflow Id
	 * @return return true if validated else false
	 * @throws ApplicationException
	 * 
	 * 
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private boolean validateAssignTaskUser(ActionResponse aoResponse, P8UserSession aoUserSession, String asUserRole,
			String asUserId, String asWobNum) throws ApplicationException
	{
		boolean lbValid = false;
		String lsCurrentTaskOwner = null;
		String lsCurrentTaskStatus = null;
		Channel loChannel = new Channel();
		int liTaskCount = 0;
		HashMap loHMReqdProp = new HashMap();
		try
		{
			if ((asUserRole.equalsIgnoreCase(HHSConstants.PROGRAM_STAFF_ROLE)
					|| asUserRole.equalsIgnoreCase(HHSConstants.ACCO_STAFF_ROLE) || asUserRole
						.equalsIgnoreCase(HHSConstants.FINANCE_STAFF_ROLE)))
			{
				loHMReqdProp.put(HHSConstants.F_WOB_NUM, asWobNum);
				loChannel.setData(ApplicationConstants.FILENET_SESSION, aoUserSession);
				loChannel.setData(HHSConstants.LS_WOB_NUM, asWobNum);
				loChannel.setData(HHSConstants.HM_REQIRED_PROPERTY_MAP, loHMReqdProp);
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_CURRENT_TASK_OWNER);
				liTaskCount = (Integer) loChannel.getData(HHSConstants.COUNT);
				if (liTaskCount > 0)
				{
					loHMReqdProp = (HashMap) loChannel.getData(HHSConstants.HM_REQIRED_PROPERTY_MAP);
					if (null != loHMReqdProp)
					{
						lsCurrentTaskOwner = (String) loHMReqdProp.get(P8Constants.PROPERTY_PE_ASSIGNED_TO);
						lsCurrentTaskStatus = (String) loHMReqdProp.get(HHSConstants.TASK_STATUS);
					}
					if (null != lsCurrentTaskOwner && !lsCurrentTaskOwner.contains(HHSConstants.WF_INITIAL_REVIEWER)
							&& !lsCurrentTaskOwner.equalsIgnoreCase(asUserId))
					{
						aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
								HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.CAN_NOT_ASSIGN_TASK));
						aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
								ApplicationConstants.MESSAGE_FAIL_TYPE);
					}
					else if (null != lsCurrentTaskStatus
							&& lsCurrentTaskStatus.equalsIgnoreCase(HHSConstants.STATUS_SUSPENDED))
					{
						aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
								HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.ERROR_FINISH_SUSPEND));
						aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
								ApplicationConstants.MESSAGE_FAIL_TYPE);
					}
					else
					{
						lbValid = true;
					}
				}
				else
				{
					aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.TASK_CANCELLED_ERROR));
					aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
							ApplicationConstants.MESSAGE_FAIL_TYPE);
				}
			}
			else
			{
				lbValid = true;
			}
		}
		// handling exception thrown by transaction layer.
		catch (ApplicationException aoAppEx)
		{
			Map<String, String> loParamMap = new HashMap<String, String>();
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap, aoAppEx);
			LOG_OBJECT.Error("Exception Occured while validateAssignTaskUser : ", aoAppEx);
		}
		return lbValid;
	}

	/**
	 * This method set audit data in channel for assign event
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * @param aoAuditList Audit List
	 * @param asLoginUserId Login User Id
	 * @param asReassignUserName Reassign User Name
	 * @param aoTaskDetailsBean TaskDetailsBean Object
	 */
	private void reassignAuditDataInChannel(List<HhsAuditBean> aoAuditList, String asLoginUserId,
			String asReassignUserName, TaskDetailsBean aoTaskDetailsBean)
	{
		aoAuditList.add(HHSUtil.addAuditDataToChannel(P8Constants.EVENT_NAME_ASSIGN, aoTaskDetailsBean.getEventType(),
				HHSConstants.TASK_ASSIGNED_TO + HHSConstants.COLON + HHSConstants.SPACE + asReassignUserName,
				aoTaskDetailsBean.getEntityType(), aoTaskDetailsBean.getEntityId(), asLoginUserId,
				HHSConstants.AGENCY_AUDIT));

		if (null != aoTaskDetailsBean.getProviderComment()
				&& !(HHSConstants.EMPTY_STRING.equalsIgnoreCase(aoTaskDetailsBean.getProviderComment())))
		{
			aoAuditList.add(HHSUtil.addAuditDataToChannel(HHSConstants.AGENCY_COMMENTS_DATA,
					HHSConstants.AGENCY_COMMENTS_DATA, aoTaskDetailsBean.getProviderComment(),
					aoTaskDetailsBean.getEntityType(), aoTaskDetailsBean.getEntityId(),
					aoTaskDetailsBean.getAuditUserId(), HHSConstants.AGENCY_AUDIT));
		}
		if (null != aoTaskDetailsBean.getInternalComment()
				&& !(HHSConstants.EMPTY_STRING.equalsIgnoreCase(aoTaskDetailsBean.getInternalComment())))
		{
			aoAuditList.add(HHSUtil.addAuditDataToChannel(HHSConstants.AUDIT_TASK_INTERNAL_COMMENTS,
					HHSConstants.AUDIT_TASK_INTERNAL_COMMENTS, aoTaskDetailsBean.getInternalComment(),
					aoTaskDetailsBean.getEntityType(), aoTaskDetailsBean.getEntityId(),
					aoTaskDetailsBean.getAuditUserId(), HHSConstants.AGENCY_AUDIT));
		}
	}

	/**
	 * This method fetches agency user list to show in Assigned to dropdown in
	 * task management filter
	 * <ul>
	 * <li>calls the transaction<b>fetchAgencyUserDetails</b></li>
	 * </ul>
	 * 
	 * @param aoAssignedOption String Buffer Object
	 * @param asAgencyId Agency Id
	 * @throws ApplicationException if any exception occurs
	 * 
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private void fetchAgencyUserDetails(StringBuffer aoAssignedOption, String asAgencyId) throws ApplicationException
	{
		StaffDetails loUserDetails = null;
		List<StaffDetails> loAgencyUserDetails = null;
		Channel loChannel = new Channel();
		loChannel.setData(HHSConstants.AS_AGENCY_ID, asAgencyId);
		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_AGENCY_USER_DETAILS);
		loAgencyUserDetails = (List<StaffDetails>) loChannel.getData(HHSConstants.AGENCY_USER_LIST);
		Iterator loItr = loAgencyUserDetails.iterator();
		while (loItr.hasNext())
		{
			loUserDetails = (StaffDetails) loItr.next();
			aoAssignedOption.append("<option value=\"");
			aoAssignedOption.append(loUserDetails.getMsStaffId());
			aoAssignedOption.append("\">");
			aoAssignedOption.append(loUserDetails.getMsStaffFirstName()).append(HHSConstants.SPACE)
					.append(loUserDetails.getMsStaffLastName()).append("</option>");
		}

	}

	/**
	 * This method will create the document bean with appropriate data and send
	 * it to the requested view
	 * <ul>
	 * <li>This method was updated in R4.</li>
	 * <li>Get the transaction name set in the Channel Object</li>
	 * <li>Execute <b>displayDocList_filenet</b> Transaction to get the detail
	 * list of the document present in the vault</li>
	 * <li>Get the list of document objects list from Channel Object</li>
	 * <li>Set the list of document bean objects into the request object</li>
	 * <li>Create the model and view object of the <b>addDocumentFromVault</b>
	 * jsp and render user to the desired page</li>
	 * <li>It will open the add document screen with all the available document
	 * listed</li>
	 * </ul>
	 * 
	 * @param aoResRequest ResourceRequest Object
	 * @param aoResResponse ResourceResponse Object
	 * @return ModelAndView with view name
	 */
	@ResourceMapping("addDocumentFromVaultUrl")
	public ModelAndView getaddDocumentFromVault(ResourceRequest aoResRequest, ResourceResponse aoResResponse)
			throws ApplicationException
	{
		PortletSession loPortalSession = aoResRequest.getPortletSession();
		List<ExtendedDocument> loRfpDocumentList = null;
		Channel loChannel = null;
		String lsDocCategory = null;
		new ArrayList<String>();
		try
		{
			P8UserSession loUserSession = (P8UserSession) loPortalSession.getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			String lsProcurementId = HHSPortalUtil.parseQueryString(aoResRequest, HHSConstants.PROCUREMENT_ID);
			String lsWorkFlowId = HHSPortalUtil.parseQueryString(aoResRequest, HHSConstants.WORKFLOW_ID);
			String lsEvaluationPoolMappingId = HHSPortalUtil.parseQueryString(aoResRequest,
					HHSConstants.EVALUATION_POOL_MAPPING_ID);
			// Start of Changes done for defect QC : 5702
			String lsNextPage = aoResRequest.getParameter(HHSConstants.NEXT_PAGE_PARAM);
			if (null == lsNextPage)
			{
				FileNetOperationsUtils.reInitializePageIterator(loPortalSession, loUserSession);
			}
			else
			{
				loUserSession.setNextPageIndex(Integer.valueOf(lsNextPage) - HHSConstants.INT_ONE);
			}
			String lsUserOrg = (String) loPortalSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
					PortletSession.APPLICATION_SCOPE);
			String lsUserOrgType = (String) loPortalSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
					PortletSession.APPLICATION_SCOPE);
			// Added for Release 5
			HashMap<String, Object> loFilterProps = new HashMap<String, Object>();
			String lsvaultFilter = aoResRequest.getParameter(HHSR5Constants.SELECT_VAULT);
			String lsSelectAll = aoResRequest.getParameter(HHSConstants.SELECT_ALL_FLAG);
			loFilterProps.put(HHSR5Constants.SELECT_VAULT, lsvaultFilter);
			loFilterProps.put(HHSConstants.SELECT_ALL_FLAG, lsSelectAll);
			String lsFolderId = aoResRequest.getParameter(HHSR5Constants.FOLDER_ID);
			String lsFolderPath = null;
			if ((null == lsFolderId || lsFolderId.equalsIgnoreCase(HHSR5Constants.NULL)|| !lsFolderId.isEmpty())
					&& (null != lsSelectAll && !lsSelectAll.isEmpty()))
			{
				lsFolderPath = FileNetOperationsUtils.setFolderPath(lsUserOrgType, lsUserOrg,
						HHSR5Constants.DOCUMENT_VAULT);
				aoResRequest.getPortletSession().setAttribute(HHSR5Constants.FOLDER_PATH, lsFolderPath);
			}
			FileNetOperationsUtils.setPropFilter(loFilterProps, lsFolderPath, lsFolderId, lsUserOrgType, lsUserOrg);
			loChannel = RFPReleaseDocsUtil.getProposalDocsFromVaultChannel(lsUserOrg, null, loUserSession,
					lsUserOrgType, loFilterProps);
			// Release 5 ends
			// End of Changes done for defect QC : 5702
			loChannel.setData(HHSConstants.DOC_CATEGORY_LOWERCASE, lsDocCategory);
			loChannel.setData(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
			if (loChannel.getData(HHSConstants.BASE_TRANSACTION_NAME) != null)
			{
				String lsTransactionName = (String) loChannel.getData(HHSConstants.BASE_TRANSACTION_NAME);
				// Execute the transaction obtained from the file
				// DocumentBusinessApp.java.
				HHSTransactionManager.executeTransaction(loChannel, lsTransactionName);
				loRfpDocumentList = RFPReleaseDocsUtil.setSelectedDocumentBean(loChannel);
				RFPReleaseDocsUtil.setReqRequestParameter(loPortalSession, loUserSession, lsNextPage);
				aoResRequest.setAttribute(ApplicationConstants.SESSION_DOCUMENT_LIST, loRfpDocumentList);
			}
			if (null == loRfpDocumentList)
			{
				// there are no documents to show
				loPortalSession.setAttribute(ApplicationConstants.ALERT_VIEW_PAGING_RECORDS, 0,
						PortletSession.APPLICATION_SCOPE);
			}
			aoResRequest.setAttribute(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			aoResRequest.setAttribute(HHSConstants.WORKFLOW_ID, lsWorkFlowId);
			aoResRequest.setAttribute(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvaluationPoolMappingId);
		}
		catch (ApplicationException aoAppExp)
		{
			String lsMessage = aoAppExp.getMessage();
			resourceAddError(aoResRequest, lsMessage);
		}
		catch (Exception aoExp)
		{
			String lsMessage = aoExp.getMessage();
			resourceAddError(aoResRequest, lsMessage);
		}
		return new ModelAndView(HHSConstants.ADD_FROM_VAULT_FROM_AWARD);
	}

	/**
	 * <ul>
	 * <li>generic exception code for resourceAddError method</li>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * @param aoResourceRequest Resource request Object
	 * @param asMessage Message string
	 * 
	 */
	private void resourceAddError(ResourceRequest aoResourceRequest, String asMessage)
	{
		String lsMessage;
		if (asMessage == null || asMessage.isEmpty())
		{
			try
			{
				lsMessage = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.GENERIC_ERROR_MESSAGE);
				aoResourceRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsMessage);
				aoResourceRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE);
			}
			// ApplicationException is thrown from getProperty in PropertyLoader
			// when no property is found
			catch (ApplicationException aoAppEx)
			{
				LOG_OBJECT.Error("Exception occured while fetching message resourceAddError", aoAppEx);
			}
		}
	}

	/**
	 * This method will insert all the details of the selected document into
	 * <b>RFP_DOCUMENT</b> table
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Get the Organization type and OrganizationName from the request
	 * object</li>
	 * <li>Execute <b>insertRfpDocumentDetails_db</b> transaction of procurement
	 * mapper <b>RFPReleaseDocsUtil</b> class</li>
	 * <li>After Successfully inserting the record redirect user to the screen
	 * no.<b>S212</b> with the updated document list</li>
	 * <li>calls the transaction <b>insertAwardTaskDocDetails</b></li>
	 * </ul>
	 * 
	 * @param aoRfpDocument - Document Bean Object
	 * @param aoRequest - ActionRequest Object
	 * @param aoResponse - ActionResponse Object
	 * 
	 *            calls the transaction 'insertAwardTaskDocDetails'
	 * 
	 */
	@ActionMapping(params = "submit_action=addDocumentFromVault")
	protected void addDocumentFromVaultAction(@ModelAttribute("rfpDocuments") ExtendedDocument aoRfpDocument,
			ActionRequest aoRequest, ActionResponse aoResponse)
	{
		String lsDocumentId = null;
		String lsProcurementId = null;
		String lsEvalPoolMappingId = null;
		try
		{
			lsProcurementId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
			lsEvalPoolMappingId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.EVALUATION_POOL_MAPPING_ID);

			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			// call method to upload document in filenet
			lsDocumentId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.DOCID);
			if (lsDocumentId != null)
			{
				// If document uploaded successfully, set document properties in
				// map
				HashMap<String, String> loDocPropsMap = new HashMap<String, String>();
				loDocPropsMap.put(HHSConstants.PROCUREMENT_ID, lsProcurementId);
				loDocPropsMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolMappingId);
				loDocPropsMap.put(HHSConstants.USER_ID, lsUserId);
				loDocPropsMap.put(HHSConstants.STATUS_COLUMN, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.DOCUMENT_SUBMITTED));
				loDocPropsMap.put(HHSConstants.DOC_ID, lsDocumentId);
				loDocPropsMap.put(HHSConstants.DOC_CATEGORY_LOWERCASE,
						aoRequest.getParameter(HHSConstants.DOC_CATEGORY_LOWERCASE));
				loDocPropsMap.put(HHSConstants.DOC_NAME, aoRequest.getParameter(HHSConstants.DOC_TITLE));
				loDocPropsMap.put(HHSConstants.DOCTYPE, aoRequest.getParameter(HHSConstants.ADD_DOC_TYPE));
				// call transaction to inser award document details in db
				Channel loChannel = new Channel();
				/*Change for R5 starts*/
				P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
						ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
				HashMap<String, Object> loMap = new HashMap<String, Object>();
				loMap.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, true);
				loChannel.setData(HHSConstants.DOC_ID, lsDocumentId);
				loChannel.setData(HHSConstants.DOCUMENT_TYPE, aoRequest.getParameter(HHSConstants.ADD_DOC_TYPE));
				loChannel.setData(HHSConstants.LO_HM_DOC_REQ_PROPS, loMap);
				loChannel.setData(HHSConstants.DOC_PROPS_MAP, loDocPropsMap);
				loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
				/*Change for R5 ends */
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.INSERT_AWARD_TASK_DOC_DETAILS);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.CONFIGURE_AWARD_DOC_TASK);
				aoResponse
						.setRenderParameter(HHSConstants.WOB_NUMBER, aoRequest.getParameter(HHSConstants.WORKFLOW_ID));
			}
		}
		// Catch the application exception log it
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("IOException during file upload", aoAppEx);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);

		}
		// Catch the exception thrown from the transaction layer and log it
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("IOException during file upload", aoExp);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
	}

	/**
	 * This method fetch the program list from the database. Added as part of
	 * Enhancement #5688 for Release 3.2.0
	 * <ul>
	 * <li>Gets agency id from request and set it into the channel object.</li>
	 * <li>Execute the transaction "getProgramNameForAgency" and set the program
	 * Name List in request.</li>
	 * </ul>
	 * 
	 * @param aoRequest ResourceRequest Object
	 * @param aoResponse ResourceResponse Object
	 * @return ModelAndView
	 */
	@SuppressWarnings("unchecked")
	@ResourceMapping("getProgramListForAccelerator")
	protected ModelAndView getProgramList(ResourceRequest aoRequest, ResourceResponse aoResponse)
	{
		ModelAndView loModelAndView = null;
		String lsAgencyId = aoRequest.getParameter(HHSConstants.AGENCYID);
		Channel loChannel = new Channel();
		List<Procurement> loProgramNameList = null;
		try
		{
			loChannel.setData(HHSConstants.AGENCYID, lsAgencyId);
			loChannel.setData(HHSConstants.AGENCY_ORG, true);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.GET_PROGRAM_NAME_FOR_AGENCY);
			loProgramNameList = (List<Procurement>) loChannel.getData(HHSConstants.PROGRAM_NAME_LIST);
			if (null != loProgramNameList)
			{
				aoRequest.getPortletSession().setAttribute(HHSConstants.PROGRAM_NAME_LIST, loProgramNameList);
			}
		}
		// Catch the exception thrown by transaction and set the error message
		// in request object and pass to jsp
		catch (ApplicationException loEx)
		{
			LOG_OBJECT.Error("Exception Occured while getting program name list for NYC Agency ", loEx);
		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception Occured while getting program name list for NYC Agency ", loExp);
		}
		loModelAndView = new ModelAndView(HHSConstants.PROGRAM_NAME);
		return loModelAndView;
	}

	/**
	 * This method is added as of 3.2.0 enhancement for bulk assignment QC: 6361
	 * This method will fetch the user assigned level for the selected user in
	 * filter execute the transaction <b>fetchUserListForFilteredTask</b> and
	 * return the user list assigned for the same level
	 * 
	 * Changes made as a part of Enhancement #6280 for Release 3.3.0
	 * 
	 * @param aoAgencyTaskBean Agency Task details
	 * @return Hashmap of users in the selected level
	 * @throws ApplicationException if any exception occurred
	 */
	private HashMap<String, String> getUserDetailsBasedOnFilterLevel(AgencyTaskBean aoAgencyTaskBean)
			throws ApplicationException

	{
		// Start || Changes made as a part of Enhancement #6280 for Release
		// 3.3.0
		LinkedHashMap<String, String> loUserHashMap = new LinkedHashMap<String, String>();
		// End || Changes made as a part of Enhancement #6280 for Release 3.3.0
		HashMap<String, String> loFilterProp = null;
		Channel loChannelObj = new Channel();
		String lsProcessId = null;
		List<StaffDetails> loStaffDetailsBean = null;
		StaffDetails loStaffDetail = null;
		try
		{
			lsProcessId = String
					.valueOf(HHSConstants.FINANCIAL_TASK_PROCESS_ID_MAP.get(aoAgencyTaskBean.getTaskName()));
			loFilterProp = aoAgencyTaskBean.getFilterProp();
			loChannelObj.setData(HHSConstants.AO_PARAMETER_MAP, loFilterProp);
			loChannelObj.setData(HHSConstants.AS_AGENCY_ID, aoAgencyTaskBean.getAgencyId());
			loChannelObj.setData(HHSConstants.AS_PROCESS_ID, lsProcessId);
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.FETCH_USER_LIST_FOR_FILTERED_TASK);
			loStaffDetailsBean = (List<StaffDetails>) loChannelObj.getData(HHSConstants.AGENCY_USER_LIST);
			if (null != loStaffDetailsBean)
			{
				Iterator<StaffDetails> loItr = loStaffDetailsBean.iterator();
				while (loItr.hasNext())
				{
					loStaffDetail = (StaffDetails) loItr.next();
					loUserHashMap.put(loStaffDetail.getMsStaffId(), loStaffDetail.getMsStaffFirstName()
							+ HHSConstants.SPACE + loStaffDetail.getMsStaffLastName());
				}
			}
		}
		// Catch the application exception log it
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception during fetching User list", aoAppEx);
			throw aoAppEx;

		}
		// Catch the exception thrown from the transaction layer and log it
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception during fetching User list", aoExp);
			throw new ApplicationException(aoExp.getMessage(), aoExp);
		}
		return loUserHashMap;
	}

	/**
	 * This method is added as of R3.7.0 enhancement #6361 for adding Service
	 * Start Date and End date when Invoice Review Task filter execute the
	 * transaction <b>fetchContractInvoiceInfoList</b> and return invoicedInfo
	 * 
	 * 
	 * @param aoAgencyTaskBean Agency Task details
	 * @return Hashmap of users in the selected level
	 * @throws ApplicationException if any exception occurred
	 */

	private void getInvoiceReviewInfo(List<AgencyTaskBean> aoAgencyTaskBeanLst) throws ApplicationException
	{
		if (aoAgencyTaskBeanLst == null || aoAgencyTaskBeanLst.isEmpty())
		{
			return;
		}
		if (!aoAgencyTaskBeanLst.get(0).getTaskName().equalsIgnoreCase(HHSConstants.TASK_INVOICE_REVIEW))
		{
			return;
		}

		StringBuffer loSb = new StringBuffer();
		for (AgencyTaskBean loBean : aoAgencyTaskBeanLst)
		{
			if (loSb.length() > 0)
			{
				loSb.append("," + loBean.getInvoiceNumber());
			}
			else
			{
				loSb.append(loBean.getInvoiceNumber());
			}
		}
		LOG_OBJECT.Debug(loSb.toString());

		Channel loChannelObj = new Channel();
		HashMap<String, Object> loChannelHashMap = new HashMap<String, Object>();
		loChannelHashMap.put(HHSConstants.INBOUND_PARAM_INVOICE_ID_LIST, loSb.toString());
		loChannelObj.setData(HHSConstants.AO_HASH_MAP, loChannelHashMap);

		HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.CI_FETCH_CONTRACT_INVOICE_INFO_LIST);
		List<InvoiceList> loInvoiceList = (List<InvoiceList>) loChannelObj.getData(HHSConstants.CI_INVOICE_LIST);

		// Convert Data Format
		Map<String, InvoiceList> loInvoiceMap = new HashMap<String, InvoiceList>();
		for (InvoiceList obj : loInvoiceList)
		{
			loInvoiceMap.put(obj.getInvoiceId(), obj);
		}
		for (AgencyTaskBean loBean : aoAgencyTaskBeanLst)
		{
			if (loInvoiceMap.containsKey(loBean.getEntityId()))
			{
				InvoiceList loInv = loInvoiceMap.get(loBean.getEntityId());
				loBean.setServiceStartDate(loInv.getInvoiceStartDate());
				loBean.setServiceEndDate(loInv.getInvoiceEndDate());
				loBean.setInvoiceSvcDate(loInv.genInvoiceSvcDate());
			}
			else
			{
				loBean.setServiceStartDate(HHSConstants.EMPTY_STRING);
				loBean.setServiceEndDate(HHSConstants.EMPTY_STRING);
				loBean.setInvoiceSvcDate(HHSConstants.EMPTY_STRING);
			}
		}
	}

	/**
	 * Added as part of Release 5 : This method fetch the Evaluator Score list
	 * from the database
	 * <ul>
	 * <li>Gets roundId and evalStatusId from request, put it to HashMap and
	 * then set it into the channel object.</li>
	 * <li>Execute the transaction "fetchEvalScoreOfSelectRound" and set the
	 * evaluation score list in request.</li>
	 * </ul>
	 * 
	 * @param aoRequest ResourceRequest Object
	 * @param aoResponse ResourceResponse Object
	 * @return ModelAndView
	 */
	@SuppressWarnings(
	{ "unchecked" })
	@ResourceMapping("getEvaluatorRoundDetails")
	protected ModelAndView getEvaluatorRoundDetails(ResourceRequest aoResourceRequest,
			ResourceResponse aoResourceResponse) throws ApplicationException
	{
		Channel loChannel = new Channel();
		ModelAndView loModelAndView = new ModelAndView(HHSR5Constants.EVALUATE_PROPOSAL_TASK_ROUND_PATH);
		aoResourceResponse.setContentType(HHSR5Constants.TEXT_HTML);
		Map<String, Object> loChannelMap = new HashMap<String, Object>();
		try
		{
			loChannelMap.put(HHSR5Constants.VERSION_NUMBER,
					aoResourceRequest.getParameter(HHSR5Constants.VERSION_NUMBER));
			loChannelMap.put(HHSR5Constants.EVALUATION_STATUS_ID,
					aoResourceRequest.getParameter(HHSR5Constants.EVALUATION_STATUS_ID));
			loChannel.setData(HHSR5Constants.AO_QUERY_MAP, loChannelMap);

			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.FETCH_EVAL_SCORE_OF_SELECT_ROUND,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			List<EvaluationBean> loEvaluationScoreDetailsList = (List<EvaluationBean>) loChannel
					.getData(HHSR5Constants.AO_EVALUATION_BEAN_LIST);
			aoResourceRequest.setAttribute(HHSR5Constants.EVALUATION_SCORE_DETAILS_LIST, loEvaluationScoreDetailsList);
		}
		// handling ApplicationException thrown from the transaction layer.
		catch (ApplicationException aoException)
		{
			// populating context data map for exceptional handling
			aoException.setContextData(loChannelMap);
			setGenericErrorMessage(aoResourceRequest);
			LOG_OBJECT.Error("Error Occured while fetching Evaluator Round Details", aoException);
		}
		// handling exception other than Application Exception.
		catch (Exception aoException)
		{
			LOG_OBJECT.Error("Error Occured while fetching Evaluator Round Details", aoException);
			setGenericErrorMessage(aoResourceRequest);
		}
		return loModelAndView;
	}

	// End of Changes done for enhancement QC : 5688
	/**
	 * Added as part of Release 5 <li>This method handles request and display
	 * Complete PSR form S472.</li> <li>If the user is not assigned user,then
	 * <li> <li>The S472 is display in read-only mode for unassigned user.<li>
	 * <li>If the User is assigned user,The user can update/insert PSR details</li>
	 * <li>This method also sets required attributes in request for re-assigned
	 * task, return tasks,insert comments</li> calls the transaction
	 * 'fetchPsrCompleteDetails'
	 * 
	 * @param aoRequest
	 * @param aoResponse
	 * @return ModelAndView
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	@RenderMapping(params = "render_action=showPsrTaskDetails")
	protected ModelAndView showPsrTaskDetails(RenderRequest aoRequest, RenderResponse aoResponse)
			throws ApplicationException
	{
		ModelAndView loModelAndView = new ModelAndView(HHSR5Constants.COMPLETE_PSR_JSP);
		HashMap loTaskDetailmap = new HashMap();
		PortletSession loSession = null;
		PsrBean loPsrFormDetails = null;
		PsrBean loPsr = null;
		ProcurementCOF loProcurementCOF = null;
		String lsUserFormComments = null;
		P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		Channel loChannel = new Channel();
		String lsWobNumber = aoRequest.getParameter(HHSR5Constants.WOB_NUMBER);
		aoRequest
				.setAttribute(HHSR5Constants.ERROR_MESSAGE, aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE));
		loPsrFormDetails = (PsrBean) ApplicationSession.getAttribute(aoRequest, HHSR5Constants.AO_PROCUREMENT_BEAN);
		lsUserFormComments = aoRequest.getParameter(HHSR5Constants.AGENCY_COMMENTS_DATA);
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		try
		{
			executeFetchPsrDetails(aoRequest, loUserSession, loTaskDetailsBean, loChannel, lsWobNumber, lsUserId);
			// Element Services name
			List<Procurement> loServiceList = (List<Procurement>) loChannel.getData(HHSR5Constants.PSR_SERVICES_LIST);
			aoRequest.setAttribute(HHSR5Constants.PSR_SERVICES_LIST, loServiceList);
			// Task assign details
			List<CommentsHistoryBean> loCommentsHistoryBeanList = null;
			loCommentsHistoryBeanList = (List<CommentsHistoryBean>) loChannel
					.getData(ApplicationConstants.TASK_HISTORY_LIST);
			loTaskDetailsBean.setCommentsHistory(loCommentsHistoryBeanList);
			loTaskDetailmap = (HashMap) loChannel.getData(ApplicationConstants.TASK_DETAIL_MAP);
			loTaskDetailsBean = HHSUtil.getTaskDetailsBeanFromMap(loTaskDetailmap, lsWobNumber);
			loChannel.setData(HHSR5Constants.PROCUREMENT_ID_KEY, loTaskDetailsBean.getProcurementId());
			loChannel.getData(ApplicationConstants.PERMITTED_USER_LIST);
			setTaskDetailsBeanProperties(aoRequest, lsWobNumber, loChannel);
			loSession = aoRequest.getPortletSession();
			loSession.setAttribute(HHSR5Constants.TASK_DETAIL_BEAN_SESSION, loTaskDetailsBean,
					PortletSession.APPLICATION_SCOPE);
			loPsr = (PsrBean) loChannel.getData(HHSR5Constants.AO_PSR_BEAN);
			loProcurementCOF = (ProcurementCOF) loChannel.getData(HHSR5Constants.AO_PROCUREMENTCOFBEAN);
			// Setting Already provided Form Details(Re - Render flow)
			if (loPsrFormDetails != null)
			{
				loPsr.setAnticipateLevelComp(loPsrFormDetails.getAnticipateLevelComp());
				loPsr.setBasisContractOut(loPsrFormDetails.getBasisContractOut());
				loPsr.setConsiderationPrice(loPsrFormDetails.getConsiderationPrice());
				loPsr.setRenewalOption(loPsrFormDetails.getRenewalOption());
				loPsr.setMultiYearHumanServContract(loPsrFormDetails.getMultiYearHumanServContract());
				loPsr.setContractTermInfo(loPsrFormDetails.getContractTermInfo());
				loPsr.setMultiYearHumanServOpt(loPsrFormDetails.getMultiYearHumanServOpt());
				aoRequest.setAttribute(ApplicationConstants.INTERNAL_COMMENTS, lsUserFormComments);
			}
			if (null != loProcurementCOF)
			{
				String startDate = DateUtil.getDateByFormat(HHSR5Constants.NFCTH_DATE_FORMAT,
						HHSR5Constants.MMDDYYFORMAT, loPsr.getContractStartDatePlanned());
				String endDate = DateUtil.getDateByFormat(HHSR5Constants.NFCTH_DATE_FORMAT,
						HHSR5Constants.MMDDYYFORMAT, loPsr.getContractEndDateUpdated());
				CBGridBean loCBGridBean = new CBGridBean();
				loCBGridBean.setProcurementID(loPsr.getProcurementId());
				PortletSessionHandler.setAttribute(loCBGridBean, aoRequest, HHSR5Constants.CBGRIDBEAN_IN_SESSION);
				setContractDatesInSession(aoRequest.getPortletSession(), startDate, endDate);
				setAccountGridDataInSession(aoRequest);
				setFundingGridDataInSession(aoRequest);
				// JQ GRID
				Map<String, Object> loFiscalYrMap = null;
				loFiscalYrMap = getContractFiscalYears(aoRequest);
				Integer loFiscalStartYr = (Integer) loFiscalYrMap.get(HHSR5Constants.LI_START_YEAR);
				loCBGridBean.setFiscalYearID(String.valueOf(loFiscalStartYr));
				aoRequest.setAttribute(HHSR5Constants.PROC_COF, loProcurementCOF);
			}
			String isOpenEnded = loPsr.getIsOpenEndedRFP();
			String lsIsOpenEndedRfp = ApplicationConstants.SYSTEM_NO;
			if (isOpenEnded.equalsIgnoreCase(HHSR5Constants.ONE))
			{
				lsIsOpenEndedRfp = ApplicationConstants.SYSTEM_YES;
			}
			loTaskDetailsBean.setIsOpenEndedRfp(lsIsOpenEndedRfp);
			// Funding GRID display or not
			Boolean loShowGrid = (Boolean) loChannel.getData(HHSConstants.FUNDING_OPERATION_GRID);
			aoRequest.setAttribute(HHSR5Constants.SHOW_PSR_FUNDING_SUBGRID, loShowGrid);
			TaskDetailsBean loTaskCommentsBean = (TaskDetailsBean) loChannel.getData(HHSConstants.AS_TASK_COMMENT);
			aoRequest.setAttribute(HHSConstants.COMMENTS, loTaskCommentsBean.getInternalComment());

			if (null != loTaskDetailsBean && null != loTaskDetailsBean.getAssignedTo()
					&& loTaskDetailsBean.getAssignedTo().equalsIgnoreCase(lsUserId))
			{
				aoRequest.setAttribute(HHSR5Constants.SCREEN_READ_ONLY, ApplicationConstants.BOOLEAN_FALSE);
			}
			else
			{
				aoRequest.setAttribute(HHSR5Constants.SCREEN_READ_ONLY, ApplicationConstants.BOOLEAN_TRUE);
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		catch (ApplicationException aoException)
		{
			loTaskDetailsBean = null;
			LOG_OBJECT.Error("Error Occured while fetching Complete PSR Details", aoException);
			aoRequest.setAttribute(HHSConstants.ERROR_MESSAGE, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
		// handling exception other than Application Exception.
		catch (Exception aoException)
		{
			LOG_OBJECT.Error("Error Occured while fetching Complete PSR Details", aoException);
			aoRequest.setAttribute(HHSConstants.ERROR_MESSAGE, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
		aoRequest.setAttribute(ApplicationConstants.TASK_DETAILS_BEAN, loTaskDetailsBean);
		aoRequest.setAttribute(HHSR5Constants.WOB_NUMBER, lsWobNumber);
		aoRequest.setAttribute(HHSR5Constants.AO_PSR_JSP_BEAN, loPsr);
		return loModelAndView;
	}

	/**
	 * Added as part of Release 5 This method executes to Fetch Psr Details.
	 * <ul>
	 * <li>set wobNumber, aoFilenetSession, reqPropsMap, orgId, userRoleList, aoTaskDetailsBean in channel</li>
	 * <li>set lsWobNumber, lsUserId, IsTaskScreen, EntityType in loTaskDetailsBean</li>
	 * <li>Execute the transaction "fetchPsrCompleteDetails"</li>
	 * </ul>
	 * 
	 * @param aoRequest
	 * @param loUserSession
	 * @param loTaskDetailsBean
	 * @param loChannel
	 * @param lsWobNumber
	 * @param lsUserId
	 * @throws ApplicationException
	 */
	private void executeFetchPsrDetails(RenderRequest aoRequest, P8UserSession loUserSession,
			TaskDetailsBean loTaskDetailsBean, Channel loChannel, String lsWobNumber, String lsUserId)
			throws ApplicationException
	{
		loChannel.setData(ApplicationConstants.WOB_NUMBER, lsWobNumber);
		loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
		loChannel.setData(ApplicationConstants.REQ_PROPS_TASK_HASHMAP, HHSUtil.getTaskPropertiesHashMap());
		loTaskDetailsBean.setWorkFlowId(lsWobNumber);
		loTaskDetailsBean.setUserId(lsUserId);
		loTaskDetailsBean.setIsTaskScreen(HHSR5Constants.BOOLEAN_FALSE);
		loTaskDetailsBean.setEntityType(HHSR5Constants.TASK_COMPLETE_PSR);
		loChannel.setData(HHSR5Constants.AO_TASK_DETAILS_BEAN, loTaskDetailsBean);
		String lsUserOrg = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);
		List<String> loUserRoleList = new ArrayList<String>();
		// Assign ROle
		loUserRoleList.add(HHSR5Constants.ACCO_MANAGER_ROLE);
		loChannel.setData(HHSR5Constants.USER_ROLE_LIST, loUserRoleList);
		loChannel.setData(HHSR5Constants.ORGID, lsUserOrg);
		HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.FETCH_PSR_DETAILS,
				HHSR5Constants.TRANSACTION_ELEMENT_R5);
	}

	/**
	 * Added as part of Release 5: This method handles action when reassign
	 * button is clicked from S454.05
	 * 
	 * <ul>
	 * <li>Validate Username/Password in Complete PSR Screen.</li>
	 * <li>Get request and session parameters like: userId, workflowId,taskId,
	 * procurmentId, P8UserSession, Internal comments</li>
	 * <li>Set task details in channel for Complete PSR task</li>
	 * <li>If comments are not null, add audit bean object by calling method
	 * addAuditDataToChannel() from HHSUtil</li>
	 * <li>Also If internal comments are not null, add audit bean object by
	 * calling method getBeanForSavingUserComments() for saving comments in user
	 * comments table</li>
	 * <li>Add TaskDetailsBean Object to channel for Complete PSR Workflow.</li>
	 * <li>calls the transaction 'finshCompletePsr'</LI>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse object
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@ActionMapping(params = "submit_action=finishPSRComplete")
	protected void updatePsrForm(@ModelAttribute("PsrBean") PsrBean aoPsrBean, ActionRequest aoRequest,
			ActionResponse aoResponse) throws ApplicationException
	{
		PortletSession loSession = null;
		String lsWobNumber = aoRequest.getParameter(HHSR5Constants.WOB_NUMBER);
		String lsProcurementId = aoRequest.getParameter(HHSR5Constants.PROCUREMENT_ID);
		String lsTaskId = aoRequest.getParameter(ApplicationConstants.TASK_ID);
		String lsTaskComments = aoRequest.getParameter(HHSConstants.COMMENTS);
		// validate user
		String lsComments = aoRequest.getParameter(ApplicationConstants.INTERNAL_COMMENTS);
		String lsUserName = aoRequest.getParameter(HHSConstants.USER);
		String lsPassword = aoRequest.getParameter(HHSR5Constants.PASSWORD);
		try
		{
			Map loAuthenticateMap = validateUser(lsUserName, lsPassword, aoRequest);
			Boolean loAuthStatus = (Boolean) loAuthenticateMap.get(HHSR5Constants.IS_VALID_USER);
			if (loAuthStatus != null && loAuthStatus == true)
			{
				Channel loChannel = new Channel();
				String lsStatusId = PropertyLoader.getProperty(HHSR5Constants.PROPERTIES_STATUS_CONSTANT,
						HHSR5Constants.STATUS_PSR_SUBMITTED);
				String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
						ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
				String lsUserOrg = (String) aoRequest.getPortletSession().getAttribute(
						ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);
				TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
				// Setting Required Fields
				aoPsrBean.setStatusId(lsStatusId);
				aoPsrBean.setUserId(lsUserId);
				loChannel.setData(HHSR5Constants.REQUIRED_AO_PSR_BEAN, aoPsrBean);
				// Finish Task Required Fields
				Map loHmWFReqProps = new HashMap();
				loHmWFReqProps.put(P8Constants.PROPERTY_PE_TASK_STATUS, ApplicationConstants.STATUS_APPROVED);
				P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
						ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
				loTaskDetailsBean.setWorkFlowId(lsWobNumber);
				loTaskDetailsBean.setUserId(lsUserId);
				loTaskDetailsBean.setTaskName(HHSR5Constants.TASK_COMPLETE_PSR);
				loTaskDetailsBean.setProcurementId(lsProcurementId);
				loChannel.setData(HHSR5Constants.AO_FILENET_SESSION, loUserSession);
				loChannel.setData(HHSR5Constants.AO_TASK_DETAILS_BEAN, loTaskDetailsBean);
				loChannel.setData(HHSR5Constants.LO_HM_WF_REQ_PROPS, loHmWFReqProps);
				// Task View History WorkFlow
				List<HhsAuditBean> loAuditBeanList = new ArrayList<HhsAuditBean>();
				loAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSR5Constants.PROPERTY_TASK_CREATION_EVENT,
						HHSR5Constants.TASK_APPROVE_PSR, HHSR5Constants.TASK_ASSIGNED_TO_UNASSIGNED_ACCELERATOR,
						HHSR5Constants.TASK_APPROVE_PSR, lsProcurementId, lsUserId, HHSR5Constants.AGENCY_AUDIT));
				List<HhsAuditBean> loAuditBeanReturn = new ArrayList<HhsAuditBean>();
				loAuditBeanReturn.add(HHSUtil.addAuditDataToChannel(HHSConstants.STATUS_CHANGE,
						HHSR5Constants.TASK_APPROVE_PSR, HHSR5Constants.STATUS_RETURN_TO_IN_REVIEW,
						HHSR5Constants.TASK_APPROVE_PSR, lsProcurementId, lsUserId, HHSR5Constants.AGENCY_AUDIT));
				List<HhsAuditBean> loAuditBeanListPsr = new ArrayList<HhsAuditBean>();
				if (null != lsComments && !lsComments.equals(HHSR5Constants.EMPTY_STRING) & null != lsTaskComments
						& !lsTaskComments.equals(lsComments))
				{
					loAuditBeanListPsr.add(HHSUtil.addAuditDataToChannel(HHSR5Constants.AUDIT_TASK_INTERNAL_COMMENTS,
							HHSR5Constants.TASK_COMPLETE_PSR, lsComments, HHSR5Constants.TASK_COMPLETE_PSR,
							lsProcurementId, lsUserId, HHSR5Constants.AGENCY_AUDIT));
				}
				loAuditBeanListPsr.add(HHSUtil.addAuditDataToChannel(HHSConstants.STATUS_CHANGE,
						HHSR5Constants.TASK_COMPLETE_PSR, HHSConstants.STATUS_CHANGED_FROM + HHSConstants.SPACE
								+ HHSConstants.STR + HHSConstants.TASK_IN_REVIEW + HHSConstants.STR
								+ HHSConstants.SPACE + HHSR5Constants.STR_TO_TASK + HHSConstants.SPACE
								+ HHSConstants.STR + HHSR5Constants.STATUS_SUBMITTED_FOR_APPROVAL + HHSConstants.STR,
						HHSR5Constants.TASK_COMPLETE_PSR, lsProcurementId, lsUserId, HHSR5Constants.AGENCY_AUDIT));
				HhsAuditBean loAuditBean = new HhsAuditBean();
				Boolean lsCommentStatus = HHSConstants.BOOLEAN_FALSE;
				if (null != lsComments && !lsComments.equals(HHSConstants.EMPTY_STRING) & null != lsTaskComments
						& !lsTaskComments.equals(lsComments))
				{
					loAuditBean.setInternalComments(lsComments);
					loAuditBean.setUserId(lsUserId);
					loAuditBean.setWorkflowId(lsWobNumber);
					loAuditBean.setEntityType(HHSR5Constants.TASK_COMPLETE_PSR);
					loAuditBean.setEntityId(lsProcurementId);
					loAuditBean.setAgencyId(lsUserOrg);
					loAuditBean.setData(lsComments);
					loAuditBean.setAuditTableIdentifier(HHSR5Constants.NON_AUDIT_COMMENTS);
					loAuditBean.setTaskId(lsTaskId);
					lsCommentStatus = HHSConstants.BOOLEAN_TRUE;
				}
				loChannel.setData(HHSR5Constants.REQUIRED_AO_PSR_BEAN, aoPsrBean);
				loChannel.setData(HHSConstants.STATUS_COLUMN, lsCommentStatus);
				loChannel.setData(HHSConstants.AUDIT_STATUS, HHSConstants.BOOLEAN_TRUE);
				loChannel.setData(HHSR5Constants.AUDIT_BEAN, loAuditBean);
				loChannel.setData(HHSR5Constants.PROCUREMENT_ID, lsProcurementId);
				loChannel.setData(HHSConstants.AUDIT_BEAN_LIST, loAuditBeanList);
				loChannel.setData(HHSConstants.AO_AUDIT_BEAN, loAuditBeanReturn);
				loChannel.setData(HHSR5Constants.AUDIT_BEAN_LIST_PSR, loAuditBeanListPsr);
				CommonUtil.setChannelForAutoSaveData(loChannel, lsWobNumber, HHSR5Constants.TASKS);
				HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.FINISH_COMPLETE_PSR,
						HHSR5Constants.TRANSACTION_ELEMENT_R5);
			}
			else
			{
				String lsErrormsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.MESSAGE_M38);
				loSession = aoRequest.getPortletSession();
				loSession.setAttribute(HHSR5Constants.AO_PROCUREMENT_BEAN, aoPsrBean);
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, lsErrormsg);
				aoResponse.setRenderParameter(HHSR5Constants.WOB_NUMBER, lsWobNumber);
				aoResponse.setRenderParameter(HHSR5Constants.AGENCY_COMMENTS_DATA, lsComments);
				aoResponse.setRenderParameter(HHSR5Constants.RENDER_ACTION, HHSR5Constants.SHOW_COMPLETE_PSR_TASK);
			}
		}
		catch (ApplicationException loExp)
		{
			Map<String, String> loParamMap = new HashMap<String, String>();
			loParamMap.put(HHSR5Constants.WORKFLOW_ID, lsWobNumber);
			setExceptionMessageFromAction(aoResponse, HHSR5Constants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap, loExp);
		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception Occured while reassigning complete PSR task details: ", loExp);
			setExceptionMessageFromAction(aoResponse, HHSR5Constants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
		aoResponse.setRenderParameter(HHSR5Constants.RETURN_TO_AGENCY_TASK, HHSR5Constants.TRUE);
	}

	/**
	 * This method handles call when save button is clicked from S454.10 This
	 * method is part of Release 5 it saves Psr Comments
	 * <ul>
	 * <li>set data into loTaskDetailsBean, loAuditBean, aoPsrBean </li>
	 * <li>setData into channel object  </li>
	 * <li>calls the transaction 'saveCompletePsrDetails'</LI>
	 * </ul>
	 * @param aoPsrBean
	 * @param aoRequest
	 * @param aoResponse
	 * @throws ApplicationException
	 */
	@ResourceMapping("savePsrComments")
	protected void savePsrCompleteComments(@ModelAttribute("PsrBean") PsrBean aoPsrBean, ResourceRequest aoRequest,
			ResourceResponse aoResponse) throws ApplicationException
	{
		String lsStatusId = PropertyLoader.getProperty(HHSR5Constants.PROPERTIES_STATUS_CONSTANT,
				HHSR5Constants.STATUS_PSR_IN_REVIEW);
		String lsComments = HHSPortalUtil.parseQueryString(aoRequest, HHSR5Constants.INTERNAL_COMMENTS);
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		TaskDetailsBean loTaskDetailsBean = (TaskDetailsBean) aoRequest.getPortletSession().getAttribute(
				HHSR5Constants.TASK_DETAIL_BEAN_SESSION, PortletSession.APPLICATION_SCOPE);
		String lsProcurementId = loTaskDetailsBean.getProcurementId();
		// Setting Required Fields
		aoPsrBean.setStatusId(lsStatusId);
		aoPsrBean.setProcurementId(loTaskDetailsBean.getProcurementId());
		aoPsrBean.setUserId(lsUserId);
		String lsWobNumber = HHSPortalUtil.parseQueryString(aoRequest, HHSR5Constants.WOB_NUMBER);
		loTaskDetailsBean.setInternalComment(lsComments);
		loTaskDetailsBean.setWorkFlowId(lsWobNumber);
		loTaskDetailsBean.setUserId(lsUserId);
		loTaskDetailsBean.setEntityType(HHSR5Constants.TASK_COMPLETE_PSR);
		loTaskDetailsBean.setEntityId(lsProcurementId);
		HhsAuditBean loAuditBean = new HhsAuditBean();
		loAuditBean.setInternalComments(lsComments);
		loAuditBean.setUserId(lsUserId);
		loAuditBean.setWorkflowId(lsWobNumber);
		loAuditBean.setEntityType(HHSR5Constants.TASK_COMPLETE_PSR);
		loAuditBean.setEntityId(lsProcurementId);
		loAuditBean.setAgencyId(loTaskDetailsBean.getAgencyId());
		loAuditBean.setData(lsComments);
		loAuditBean.setAuditTableIdentifier(HHSR5Constants.NON_AUDIT_COMMENTS);
		loAuditBean.setTaskId(loTaskDetailsBean.getTaskId());
		Channel loChannel = new Channel();
		loChannel.setData(HHSR5Constants.REQUIRED_AO_PSR_BEAN, aoPsrBean);
		loChannel.setData(HHSR5Constants.AUDIT_BEAN, loAuditBean);
		// Add comments to Task History
		List<HhsAuditBean> loAuditBeanList = new ArrayList<HhsAuditBean>();
		if (null != lsComments && !lsComments.equals(HHSR5Constants.EMPTY_STRING))
		{
			loAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSR5Constants.AUDIT_TASK_INTERNAL_COMMENTS,
					HHSR5Constants.TASK_COMPLETE_PSR, lsComments, HHSR5Constants.TASK_COMPLETE_PSR, lsProcurementId,
					lsUserId, HHSR5Constants.AGENCY_AUDIT));
		}
		try
		{
			loChannel.setData(ApplicationConstants.AUDIT_BEAN_LIST, loAuditBeanList);
			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.SAVE_COMPLETE_PSR_DETAILS,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
		}
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("Exception Occured while saving comments for CompletePsr task : ", loExp);
		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception Occured while saving comments for CompletePsr task : ", loExp);
		}
	}

	/**
	 * Added as part of Release 5 : This method handles action when reassign
	 * button is clicked from S454.03
	 * 
	 * <ul>
	 * <li>Get request and session parameters like: userId, workflowId,taskId,
	 * procurmentId, P8UserSession, ReassignedTo, ReassignedToUserName</li>
	 * <li>Set task details in channel for reassigning task</li>
	 * <li>If provider and internal comments are not null, add audit bean object
	 * by calling method addAuditDataToChannel() from HHSUtil</li>
	 * <li>Also If provider and internal comments are not null, add audit bean
	 * object by calling method getBeanForSavingUserComments() for saving
	 * comments in user comments table</li>
	 * <li>Add audit bean object for reassigning task by calling method
	 * addAuditDataToChannel() from HHSUtil</li>
	 * <li>calls the transaction 'reassignWFTask'</LI>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse object
	 */
	@ActionMapping(params = "submit_action=reassignCompletePsrTask")
	protected void reassignCompletePsrTask(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		String lsWorkflowId = aoRequest.getParameter(ApplicationConstants.WORKFLOW_ID);
		String lsTaskId = aoRequest.getParameter(ApplicationConstants.TASK_ID);
		String lsProcurementId = aoRequest.getParameter(HHSR5Constants.PROCUREMENT_ID);
		String lsAssignedToUserId = aoRequest.getParameter(ApplicationConstants.REASSIGNED_TO);
		String lsAssignedToUserName = aoRequest.getParameter(ApplicationConstants.REASSIGNED_TO_USER_NAME);
		String lsComments = HHSPortalUtil.parseQueryString(aoRequest, HHSR5Constants.INTERNAL_COMMENTS);
		String lsPreviousComments = aoRequest.getParameter(HHSConstants.COMMENTS);
		try
		{
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			String lsUserOrg = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);
			// Set user Id and user name to which task has been reassigned
			TaskDetailsBean loTaskBean = new TaskDetailsBean();
			loTaskBean.setWorkFlowId(lsWorkflowId);
			loTaskBean.setReassignUserId(lsAssignedToUserId);
			loTaskBean.setReassignUserName(lsAssignedToUserName);
			P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			// save assigned to field for selected user
			Channel loChannel = new Channel();
			loChannel.setData(ApplicationConstants.TASK_DETAILS_BEAN, loTaskBean);
			loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
			// save comments to task history
			List<HhsAuditBean> loAuditBeanList = new ArrayList<HhsAuditBean>();
			if (null != lsComments && !lsComments.equals(HHSR5Constants.EMPTY_STRING))
			{
				HhsAuditBean loAuditBean = new HhsAuditBean();
				loAuditBean.setInternalComments(lsComments);
				loAuditBean.setUserId(lsUserId);
				loAuditBean.setWorkflowId(lsWorkflowId);
				loAuditBean.setEntityType(HHSR5Constants.TASK_COMPLETE_PSR);
				loAuditBean.setEntityId(lsProcurementId);
				loAuditBean.setAgencyId(lsUserOrg);
				loAuditBean.setData(lsComments);
				loAuditBean.setAuditTableIdentifier(HHSR5Constants.NON_AUDIT_COMMENTS);
				loAuditBean.setTaskId(lsTaskId);
				loAuditBeanList.add(loAuditBean);
			}
			if (null != lsComments && !lsComments.equals(HHSR5Constants.EMPTY_STRING) & null != lsPreviousComments
					& !lsPreviousComments.equals(lsComments))
			{
				loAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSR5Constants.AUDIT_TASK_INTERNAL_COMMENTS,
						HHSR5Constants.TASK_COMPLETE_PSR, lsComments, HHSR5Constants.TASK_COMPLETE_PSR,
						lsProcurementId, lsUserId, HHSR5Constants.AGENCY_AUDIT));
			}
			if (null != lsComments && !lsComments.equals(HHSR5Constants.EMPTY_STRING) & null == lsPreviousComments)
			{
				loAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSR5Constants.AUDIT_TASK_INTERNAL_COMMENTS,
						HHSR5Constants.TASK_COMPLETE_PSR, lsComments, HHSR5Constants.TASK_COMPLETE_PSR,
						lsProcurementId, lsUserId, HHSR5Constants.AGENCY_AUDIT));
			}
			loAuditBeanList.add(HHSUtil.addAuditDataToChannel(ApplicationConstants.TASK_ASSIGNMENT,
					HHSR5Constants.TASK_COMPLETE_PSR, ApplicationConstants.TASK_ASSIGNED_TO + HHSR5Constants.COLON_AOP
							+ lsAssignedToUserName, HHSR5Constants.TASK_COMPLETE_PSR, lsProcurementId, lsUserId,
					HHSR5Constants.AGENCY_AUDIT));
			loChannel.setData(ApplicationConstants.AUDIT_BEAN_LIST, loAuditBeanList);
			HHSTransactionManager.executeTransaction(loChannel, ApplicationConstants.REASSIGN_WF_TASK);
		}
		// handling exception other than Application Exception.
		catch (ApplicationException loExp)
		{
			Map<String, String> loParamMap = new HashMap<String, String>();
			loParamMap.put(HHSR5Constants.WORKFLOW_ID, lsWorkflowId);
			loParamMap.put(HHSR5Constants.TASK_ID, lsTaskId);
			loParamMap.put(HHSR5Constants.PROCUREMENT_ID, lsProcurementId);
			setExceptionMessageFromAction(aoResponse, HHSR5Constants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap, loExp);

		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception Occured while reassigning complete PSR task details: ", loExp);
			setExceptionMessageFromAction(aoResponse, HHSR5Constants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
		aoResponse.setRenderParameter(HHSR5Constants.RETURN_TO_AGENCY_TASK, HHSR5Constants.TRUE);
	}

	/**
	 * Added as part of Release 5 <li>This method handles request and display
	 * Finalize Award Amount Task.</li> <li>If the user is not assigned
	 * user,then<li> <li>The S472 is display in read-only mode for unassigned
	 * user.<li> <li>If the User is assigned user,The user can update/insert PSR
	 * details</li> <li>This method also sets required attributes in request for
	 * re-assigned task, return tasks,insert comments</li> calls the transaction
	 * 'fetchFinalizeAwardTaskDetails'
	 * 
	 * @param aoRequest
	 * @param aoResponse
	 * @return ModelAndView
	 */
	@SuppressWarnings(
	{ "unchecked" })
	@RenderMapping(params = "render_action=finalizeAwardTaskForAgency")
	protected ModelAndView showFinalizeAwardAmountTask(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		String lsWobNumber = aoRequest.getParameter(ApplicationConstants.WOB_NUMBER);
		P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsProcurementId = (String) aoRequest.getParameter(HHSConstants.PROCUREMENT_ID);
		Map loHmTaskDetails = HHSUtil.getTaskPropertiesHashMap();
		loHmTaskDetails.put(P8Constants.PE_WORKFLOW_CONTRACT_ID, "");
		List<EvaluationBean> loEvalListOthers = null;
		// fetching procurement and task details
		Channel loChannel = new Channel();
		loChannel.setData(ApplicationConstants.WOB_NUMBER, lsWobNumber);
		loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
		loChannel.setData(ApplicationConstants.KEY_SESSION_USER_ID, lsUserId);
		loChannel.setData(ApplicationConstants.REQ_PROPS_TASK_HASHMAP, loHmTaskDetails);
		// fetching Task history details
		Map loAuditMap = new HashMap();
		loAuditMap.put(ApplicationConstants.ENTITY_TYPE, HHSR5Constants.FINALIZE_AWARD_AMOUNT);
		loAuditMap.put(ApplicationConstants.EVENT_NAME, ApplicationConstants.TASK_CREATION);
		loChannel.setData(ApplicationConstants.REQUIRED_PROPS, loAuditMap);
		// fetching internal comments
		TaskDetailsBean loTaskDetailBean = new TaskDetailsBean();
		loTaskDetailBean.setEntityType(HHSR5Constants.FINALIZE_AWARD_AMOUNT);
		loTaskDetailBean.setEntityId(lsProcurementId);
		loTaskDetailBean.setWorkFlowId(lsWobNumber);
		loChannel.setData(ApplicationConstants.TASK_DETAILS_BEAN_KEY, loTaskDetailBean);
		// fetching unassigned Acco managers
		List<String> loUserRoleList = new ArrayList<String>();
		loUserRoleList.add(HHSR5Constants.ACCO_MANAGER_ROLE);
		loChannel.setData(HHSR5Constants.USER_ROLE_LIST, loUserRoleList);
		try
		{
			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.FETCH_FINALIZE_AWARD_TASK_DETAILS,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			// get task and procurement details from channel
			HashMap<String, Object> loTaskDetailMap = (HashMap<String, Object>) loChannel
					.getData(ApplicationConstants.TASK_DETAIL_MAP);
			TaskDetailsBean loTaskDetailsBean = HHSUtil.getTaskDetailsBeanFromMap(loTaskDetailMap, lsWobNumber);
			aoRequest.setAttribute(ApplicationConstants.TASK_DETAILS_BEAN, loTaskDetailsBean);
			// Get the permitted user list for reassign dropdown
			List<UserBean> loUserBeanList = (List<UserBean>) loChannel
					.getData(ApplicationConstants.PERMITTED_USER_LIST);
			aoRequest.setAttribute(ApplicationConstants.REASSIGN_USER_MAP, HHSUtil.getReassignUserMap(loUserBeanList));
			// Set Attributes in request object
			aoRequest.setAttribute(ApplicationConstants.PROCUREMENT_ID, loTaskDetailsBean.getProcurementId());
			aoRequest.setAttribute(ApplicationConstants.WORKFLOW_ID, lsWobNumber);
			aoRequest.setAttribute(ApplicationConstants.TASK_ID, loTaskDetailsBean.getTaskId());
			// Set Evaluation Results list in request object
			HashMap loEvalMap = (HashMap) loChannel.getData(ApplicationConstants.EVALUATION_RESULTS_LIST);
			List<EvaluationBean> loEvalResultsList = (List<EvaluationBean>) loEvalMap
					.get(HHSR5Constants.FINALIZE_SAME_PROVIDER_LIST);
			if (null != loEvalMap.get(HHSR5Constants.FINALIZE_OTHER_PROVIDER_LIST))
			{
				loEvalListOthers = (List<EvaluationBean>) loEvalMap.get(HHSR5Constants.FINALIZE_OTHER_PROVIDER_LIST);
			}
			aoRequest.setAttribute(ApplicationConstants.EVALUATION_RESULTS_LIST, loEvalResultsList);
			aoRequest.setAttribute(HHSConstants.EVALUATION_LIST, loEvalListOthers);
			TaskDetailsBean loTaskCommentsBean;
			// Set task history list in request object
			aoRequest.setAttribute(ApplicationConstants.TASK_HISTORY_LIST,
					loChannel.getData(ApplicationConstants.TASK_HISTORY_LIST));
			// Set Internal Comments in request object
			loTaskCommentsBean = (TaskDetailsBean) loChannel.getData(ApplicationConstants.AS_TASK_COMMENT);
			if (null != loTaskCommentsBean)
			{
				aoRequest.setAttribute(ApplicationConstants.INTERNAL_COMMENTS, loTaskCommentsBean.getInternalComment());
				aoRequest.setAttribute(ApplicationConstants.COMMENTS, loTaskCommentsBean.getInternalComment());
			}
			// Check if User Id is same as User Assigned To
			if (null != loTaskDetailsBean && null != loTaskDetailsBean.getAssignedTo()
					&& loTaskDetailsBean.getAssignedTo().equalsIgnoreCase(lsUserId))
			{
				aoRequest.setAttribute(ApplicationConstants.SCREEN_READ_ONLY, false);
			}
			else
			{
				aoRequest.setAttribute(ApplicationConstants.SCREEN_READ_ONLY, true);
			}
			Procurement loProcurementBean = (Procurement) loChannel.getData(ApplicationConstants.PROCUREMENT_SUMMARY);
			aoRequest.setAttribute(ApplicationConstants.PROCUREMENT_BEAN, loProcurementBean);
			// Set error message and message type if an Exception occurs
			HashMap loHmTaskDetail = (HashMap) loChannel.getData(ApplicationConstants.TASK_DETAIL_MAP);
			HashMap loHmTaskDetailInfo = (HashMap) loHmTaskDetail.get(lsWobNumber);
			aoRequest.setAttribute(HHSConstants.ENTITY_ID,
					(String) loHmTaskDetailInfo.get(P8Constants.PROPERTY_PE_ENTITY_ID));
			String lsErrorMsg = (String) aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
		// Handling Application Exception thrown by Transaction layer
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured while processing configure award task details", aoAppEx);
			setGenericErrorMessage(aoRequest);
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occured while processing configure award task details", aoEx);
			setGenericErrorMessage(aoRequest);
		}
		return new ModelAndView(HHSR5Constants.JSP_FINALIZE_AWARD_AMOUNT);

	}

	/**
	 * This method is added in Release 5, to handles action when reassign button
	 * is clicked from S454.03
	 * 
	 * <ul>
	 * <li>Get request and session parameters like: userId, workflowId,taskId,
	 * procurmentId, P8UserSession, ReassignedTo, ReassignedToUserName</li>
	 * <li>Set task details in channel for reassigning task</li>
	 * <li>If provider and internal comments are not null, add audit bean object
	 * by calling method addAuditDataToChannel() from HHSUtil</li>
	 * <li>Also If provider and internal comments are not null, add audit bean
	 * object by calling method getBeanForSavingUserComments() for saving
	 * comments in user comments table</li>
	 * <li>Add audit bean object for reassigning task by calling method
	 * addAuditDataToChannel() from HHSUtil</li>
	 * <li>calls the transaction 'reassignWFTask'</LI>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse object
	 */
	@ActionMapping(params = "submit_action=reassignFinalizeAwardTask")
	protected void reassignFinalizeAwardTask(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		String lsWorkflowId = aoRequest.getParameter(ApplicationConstants.WORKFLOW_ID);
		String lsTaskId = aoRequest.getParameter(ApplicationConstants.TASK_ID);
		String lsEntityId = aoRequest.getParameter(HHSConstants.ENTITY_ID);
		String lsProcurementId = (String) aoRequest.getParameter(HHSConstants.PROCUREMENT_ID);
		String lsAssignedToUserId = aoRequest.getParameter(ApplicationConstants.REASSIGNED_TO);
		String lsAssignedToUserName = aoRequest.getParameter(ApplicationConstants.REASSIGNED_TO_USER_NAME);
		try
		{
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			String lsUserOrg = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);
			// Set user Id and user name to which task has been reassigned
			TaskDetailsBean loTaskBean = new TaskDetailsBean();
			loTaskBean.setWorkFlowId(lsWorkflowId);
			loTaskBean.setReassignUserId(lsAssignedToUserId);
			loTaskBean.setReassignUserName(lsAssignedToUserName);
			P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			// save assigned to field for selected user
			Channel loChannel = new Channel();
			loChannel.setData(ApplicationConstants.TASK_DETAILS_BEAN, loTaskBean);
			loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
			// save comments to task history
			String lsInternalComments = aoRequest.getParameter(ApplicationConstants.INTERNAL_COMMENTS);
			String lsPreviousComments = aoRequest.getParameter(ApplicationConstants.COMMENTS);
			List<HhsAuditBean> loAuditBeanList = new ArrayList<HhsAuditBean>();
			if (null == lsPreviousComments && null != lsInternalComments
					&& !lsInternalComments.equals(HHSR5Constants.EMPTY_STRING))
			{
				loAuditBeanList.add(HHSUtil.getBeanForSavingUserComments(lsTaskId, lsWorkflowId,
						HHSR5Constants.FINALIZE_AWARD_AMOUNT, lsProcurementId, lsUserId, lsUserOrg, null,
						lsInternalComments));
				loAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSConstants.AUDIT_TASK_INTERNAL_COMMENTS,
						HHSR5Constants.FINALIZE_AWARD_AMOUNT, lsInternalComments, HHSR5Constants.FINALIZE_AWARD_AMOUNT,
						lsEntityId, lsUserId, HHSConstants.AGENCY_AUDIT));
			}
			// save agency comments to user comments
			if (null != lsInternalComments && !lsInternalComments.equals(HHSR5Constants.EMPTY_STRING)
					&& null != lsPreviousComments && !lsPreviousComments.equalsIgnoreCase(lsInternalComments))
			{
				loAuditBeanList.add(HHSUtil.getBeanForSavingUserComments(lsTaskId, lsWorkflowId,
						HHSR5Constants.FINALIZE_AWARD_AMOUNT, lsProcurementId, lsUserId, lsUserOrg, null,
						lsInternalComments));
				loAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSConstants.AUDIT_TASK_INTERNAL_COMMENTS,
						HHSR5Constants.FINALIZE_AWARD_AMOUNT, lsInternalComments, HHSR5Constants.FINALIZE_AWARD_AMOUNT,
						lsEntityId, lsUserId, HHSConstants.AGENCY_AUDIT));
			}
			loAuditBeanList.add(HHSUtil.addAuditDataToChannel(ApplicationConstants.TASK_ASSIGNMENT,
					HHSR5Constants.FINALIZE_AWARD_AMOUNT, HHSConstants.TASK_ASSIGNED_TO + HHSR5Constants.COLON_AOP
							+ lsAssignedToUserName, HHSR5Constants.FINALIZE_AWARD_AMOUNT, lsEntityId, lsUserId,
					HHSR5Constants.AGENCY_AUDIT));
			loChannel.setData(ApplicationConstants.AUDIT_BEAN_LIST, loAuditBeanList);
			HHSTransactionManager.executeTransaction(loChannel, ApplicationConstants.REASSIGN_WF_TASK);
		}
		// handling exception other than Application Exception.
		catch (ApplicationException loExp)
		{
			Map<String, String> loParamMap = new HashMap<String, String>();
			loParamMap.put(HHSR5Constants.WORKFLOW_ID, lsWorkflowId);
			loParamMap.put(HHSR5Constants.TASK_ID, lsTaskId);
			loParamMap.put(HHSConstants.ENTITY_ID, lsEntityId);
			setExceptionMessageFromAction(aoResponse, HHSR5Constants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap, loExp);

		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception Occured while reassigning complete PSR task details: ", loExp);
			setExceptionMessageFromAction(aoResponse, HHSR5Constants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
		aoResponse.setRenderParameter(HHSR5Constants.RETURN_TO_AGENCY_TASK, HHSR5Constants.TRUE);
	}

	/**
	 * This method is added in Release 5, to handles call when save button is
	 * clicked from S455.10 It stores Agency comments. <li>If internal comments
	 * are not null, add audit bean object by calling method
	 * addAuditDataToChannel() from HHSUtil</li> <li>Also If provider and
	 * internal comments are not null, add audit bean object by calling method
	 * getBeanForSavingUserComments() for saving comments in user comments table
	 * </li> <li>Add audit bean object for reassigning task by calling method
	 * addAuditDataToChannel() from HHSUtil</li> <li>calls the transaction
	 * 'reassignWFTask'</LI>
	 * 
	 * @param aoRequest
	 * @param aoResponse
	 * @throws ApplicationException
	 */
	@ResourceMapping("saveFinalizeAwardComments")
	protected void saveFinalizeAwardComments(ResourceRequest aoRequest, ResourceResponse aoResponse)
			throws ApplicationException
	{
		String lsComments = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.INTERNAL_COMMENTS);
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsProcurementId = (String) aoRequest.getParameter(HHSConstants.PROCUREMENT_ID);
		String lsEntityId = aoRequest.getParameter(HHSConstants.ENTITY_ID);
		String lsWobNumber = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.WOB_NUMBER);
		String asTaskId = HHSPortalUtil.parseQueryString(aoRequest, ApplicationConstants.TASK_ID);
		Boolean loUpdateStatus = HHSConstants.BOOLEAN_FALSE;
		HhsAuditBean loAuditBean = new HhsAuditBean();
		if (null != lsComments && !lsComments.equals(HHSConstants.EMPTY_STRING))
		{
			loAuditBean.setInternalComments(lsComments);
			loAuditBean.setUserId(lsUserId);
			loAuditBean.setWorkflowId(lsWobNumber);
			loAuditBean.setEntityType(HHSR5Constants.FINALIZE_AWARD_AMOUNT);
			loAuditBean.setEntityId(lsProcurementId);
			loAuditBean.setData(lsComments);
			loAuditBean.setAuditTableIdentifier(HHSConstants.NON_AUDIT_COMMENTS);
			loAuditBean.setTaskId(asTaskId);
			loUpdateStatus = HHSConstants.BOOLEAN_TRUE;
		}
		try
		{
			Channel loChannel = new Channel();
			loChannel.setData(HHSConstants.AUDIT_BEAN, loAuditBean);
			loChannel.setData(HHSR5Constants.UPDATE_STATUS, loUpdateStatus);
			// Add comments to Task History
			List<HhsAuditBean> loAuditBeanList = new ArrayList<HhsAuditBean>();
			if (null != lsComments && !lsComments.equals(HHSConstants.EMPTY_STRING))
			{
				loAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSConstants.AUDIT_TASK_INTERNAL_COMMENTS,
						HHSR5Constants.FINALIZE_AWARD_AMOUNT, lsComments, HHSR5Constants.FINALIZE_AWARD_AMOUNT,
						lsEntityId, lsUserId, HHSConstants.AGENCY_AUDIT));
			}
			loChannel.setData(ApplicationConstants.AUDIT_BEAN_LIST, loAuditBeanList);
			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.SAVE_APPROVE_PSR_COMMENTS,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
		}
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("Exception Occured while saving comments for Agency : ", loExp);
		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception Occured while saving comments for Agency : ", loExp);
		}
	}

	/**
	 * This method will handle the request from Input Finalize Award Amount
	 * Screen
	 * <ul>
	 * <li>Added Method in R5</li>
	 * <li>1. Create channel object and set proposal Id and Finalized Amount in
	 * it</li>
	 * <li>2. Call <b>editFinalizeAmount</b> transaction to set the amount value
	 * </li>
	 * </ul>
	 * 
	 * @param aoResourceRequest - a ResourceRequest object
	 * @param aoResourceResponse - a ResourceResponse object
	 * @return - a ModelAndView object containing the JSP name
	 *         finalizeupdateresults.jsp
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	@ResourceMapping("editFinalizeAmountTask")
	protected void editFinalizeAmount(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws ApplicationException
	{
		Channel loChannel = new Channel();
		String lsNegotiatedAmount = null;
		Map loHmRequiredProps = new HashMap();
		PrintWriter loPrintWriter = null;
		try
		{
			String lsProposalId = aoResourceRequest.getParameter(HHSR5Constants.SELECTED_PROPOSAL_ID);
			lsNegotiatedAmount = aoResourceRequest.getParameter(HHSConstants.AMOUNT);
			loHmRequiredProps.put(HHSConstants.PROPOSAL_ID, lsProposalId);
			loHmRequiredProps.put(HHSConstants.AMOUNT, lsNegotiatedAmount);
			loChannel.setData(HHSConstants.AO_HASH_MAP, loHmRequiredProps);
			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.SET_AWARD_FINALIZED_AMOUNT,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			aoResourceResponse.setContentType(HHSConstants.TEXT_HTML);
			loPrintWriter = aoResourceResponse.getWriter();
			loPrintWriter.write(lsNegotiatedAmount);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppExp)
		{
			setGenericErrorMessage(aoResourceRequest);
			aoAppExp.setContextData(loHmRequiredProps);
			LOG_OBJECT.Error("Exception Occured in editFinalizeAmount method", aoAppExp);
		}
		// handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			setGenericErrorMessage(aoResourceRequest);
			LOG_OBJECT.Error("Exception Occured in editFinalizeAmount method", aoExp);
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

	/**
	 * This method handles action when reassign button is clicked from S452.05
	 * 
	 * <ul>
	 * <li>Validate Username/Password in Complete PSR Screen.</li>
	 * <li>Get request and session parameters like: userId, workflowId,taskId,
	 * procurmentId, P8UserSession, Internal comments</li>
	 * <li>Set task details in channel for Complete PSR task</li>
	 * <li>If comments are not null, add audit bean object by calling method
	 * addAuditDataToChannel() from HHSUtil</li>
	 * <li>Also If internal comments are not null, add audit bean object by
	 * calling method getBeanForSavingUserComments() for saving comments in user
	 * comments table</li>
	 * <li>Add TaskDetailsBean Object to channel for Complete PSR Workflow.</li>
	 * <li>calls the transaction 'finishFinalizeAward'</LI>
	 * <li>Added Method in R5</li>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse object
	 */
	@ActionMapping(params = "submit_action=finishFinalizeAmountTask")
	protected void finishFinalizeAmountTask(ActionRequest aoRequest, ActionResponse aoResponse)
			throws ApplicationException
	{
		String lsWobNumber = aoRequest.getParameter(ApplicationConstants.WORKFLOW_ID);
		String lsProcurementId = aoRequest.getParameter(HHSR5Constants.PROCUREMENT_ID);
		String lsEntityId = aoRequest.getParameter(HHSConstants.ENTITY_ID);
		String lsTaskId = aoRequest.getParameter(ApplicationConstants.TASK_ID);
		String lsPreviousComments = aoRequest.getParameter(HHSConstants.COMMENTS);
		String lsInternalComments = aoRequest.getParameter(ApplicationConstants.INTERNAL_COMMENTS);
		String lsFirstLaunch = aoRequest.getParameter(ApplicationConstants.IS_FIRST_LAUNCH);
		Boolean loCommentAuditStatus = HHSConstants.BOOLEAN_FALSE;
		String lsEvalPoolMappingId = aoRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID);
		String lsProviderId = aoRequest.getParameter(HHSConstants.PROVIDER_ID);
		String lsApproveAmountFlag = aoRequest.getParameter(HHSR5Constants.APPROVE_FINALIZED_AWARD_FLAG);
		try
		{
			Channel loChannel = new Channel();
			String lsEvaluationResultsStatus = null;
			String lsContractStatus = null;
			String lsContractType = aoRequest.getParameter(HHSConstants.CONTRACT_TYPE_ID);
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			String lsUserOrg = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);
			TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
			Map loHmWFReqProps = new HashMap();
			loHmWFReqProps.put(P8Constants.PROPERTY_PE_TASK_STATUS, ApplicationConstants.STATUS_APPROVED);
			if (null != lsApproveAmountFlag && lsApproveAmountFlag.equalsIgnoreCase(HHSConstants.TRUE))
			{
				loHmWFReqProps.put(HHSR5Constants.APPROVE_FINALIZED_AWARD_FLAG, HHSConstants.BOOLEAN_TRUE);
				lsEvaluationResultsStatus = PropertyLoader.getProperty(HHSR5Constants.PROPERTIES_STATUS_CONSTANT,
						HHSR5Constants.STATUS_FINALIZE_AWARD_SUBMITTED);
				lsContractStatus = PropertyLoader.getProperty(HHSR5Constants.PROPERTIES_STATUS_CONSTANT,
						HHSR5Constants.STATUS_CONTRACT_PENDING_FINAL_AWARD_AMOUNT_APPROVAL);

			}
			else
			{
				loHmWFReqProps.put(HHSR5Constants.APPROVE_FINALIZED_AWARD_FLAG, HHSConstants.BOOLEAN_FALSE);
				lsEvaluationResultsStatus = PropertyLoader.getProperty(HHSR5Constants.PROPERTIES_STATUS_CONSTANT,
						HHSR5Constants.STATUS_FINALIZE_AWARD_COMPLETED);
				lsContractStatus = PropertyLoader.getProperty(HHSR5Constants.PROPERTIES_STATUS_CONSTANT,
						HHSR5Constants.STATUS_CONTRACT_PENDING_REGISTARTION);
				if (null != lsContractType && lsContractType.equalsIgnoreCase(HHSConstants.ONE))
				{
					lsContractStatus = PropertyLoader.getProperty(HHSR5Constants.PROPERTIES_STATUS_CONSTANT,
							HHSR5Constants.STATUS_CONTRACT_PENDING_CONFIGURATION);
				}
			}
			P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			Map loAwardMap = new HashMap();
			loAwardMap.put(HHSConstants.AWARD_STATUS_ID, lsEvaluationResultsStatus);
			loAwardMap.put(HHSConstants.CONTRACT_STATUS, lsContractStatus);
			loAwardMap.put(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			loAwardMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolMappingId);
			loAwardMap.put(HHSConstants.PROVIDER_ID, lsProviderId);
			loTaskDetailsBean.setWorkFlowId(lsWobNumber);
			loTaskDetailsBean.setUserId(lsUserId);
			loTaskDetailsBean.setTaskName(HHSR5Constants.FINALIZE_AWARD_AMOUNT);
			loTaskDetailsBean.setProcurementId(lsProcurementId);
			loChannel.setData(HHSR5Constants.AO_FILENET_SESSION, loUserSession);
			loChannel.setData(HHSR5Constants.AO_TASK_DETAILS_BEAN, loTaskDetailsBean);
			loChannel.setData(HHSR5Constants.LO_HM_WF_REQ_PROPS, loHmWFReqProps);
			loChannel.setData(ApplicationConstants.AWARD_MAP, loAwardMap);
			// Task View History WorkFlow
			List<HhsAuditBean> loAuditBeanList = new ArrayList<HhsAuditBean>();
			createFinalizeAwardTaskAudit(lsWobNumber, lsProcurementId, lsEntityId, lsTaskId, lsPreviousComments,
					lsInternalComments, lsFirstLaunch, loCommentAuditStatus, lsUserId, lsUserOrg, loAuditBeanList);
			loChannel.setData(HHSConstants.AUDIT_BEAN_LIST, loAuditBeanList);
			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.FINISH_FINALIZE_AWARD_AMOUNT_TASK,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			setFinalizeAwardAmountSuccessMessage(aoResponse, lsApproveAmountFlag);
		}
		catch (ApplicationException loExp)
		{
			Map<String, String> loParamMap = new HashMap<String, String>();
			loParamMap.put(HHSR5Constants.WORKFLOW_ID, lsWobNumber);
			setExceptionMessageFromAction(aoResponse, HHSR5Constants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap, loExp);
		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception Occured while reassigning complete PSR task details: ", loExp);
			setExceptionMessageFromAction(aoResponse, HHSR5Constants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
		aoResponse.setRenderParameter(HHSR5Constants.RETURN_TO_AGENCY_TASK, HHSR5Constants.TRUE);
	}

	/**
	 * This method Added in Release 5, to sets the success message which is
	 * displayed at the finish of Finalize Award Amount Task.It is called from
	 * finishFinalizeAmountTask.
	 * 
	 * @param aoResponse - ActionResponce Object
	 * @param asApproveAmountFlag - String Object
	 * @throws ApplicationException - ApplicationException
	 */
	private void setFinalizeAwardAmountSuccessMessage(ActionResponse aoResponse, String asApproveAmountFlag)
			throws ApplicationException
	{
		String lsSuccessMessage = null;
		try
		{
			if (asApproveAmountFlag.equalsIgnoreCase(HHSConstants.TRUE))
			{
				lsSuccessMessage = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSR5Constants.MESSAGE_M82);

			}
			else
			{
				lsSuccessMessage = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSR5Constants.MESSAGE_M83);
			}
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, lsSuccessMessage);
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_PASS_TYPE);
		}
		catch (ApplicationException loExp)
		{
			Map<String, String> loParamMap = new HashMap<String, String>();
			setExceptionMessageFromAction(aoResponse, HHSR5Constants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap, loExp);
		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception Occured while setFinalizeAwardAmountSuccessMessage: ", loExp);
			setExceptionMessageFromAction(aoResponse, HHSR5Constants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}

	}

	/**
	 * This method is called from finishFinalizeAmountTask.This method insert
	 * audits based on firstLaunch Flag.
	 * 
	 * @param asWobNumber - WorkflowId
	 * @param asProcurementId - Procurement's Id
	 * @param asEntityId - EntityId for Audit Table
	 * @param asTaskId - Task Id for Audit insert
	 * @param asPreviousComments - Task Previous comment
	 * @param asInternalComments - Agency User Comment
	 * @param asFirstLaunch - IsFirstLaunch flag
	 * @param aoCommentAuditStatus - Condition check for inserts
	 * @param asUserId - Logged in User Id
	 * @param asUserOrg - Organization Id
	 * @param aoAuditBeanList - AuditBean List
	 */
	private void createFinalizeAwardTaskAudit(String asWobNumber, String asProcurementId, String asEntityId,
			String asTaskId, String asPreviousComments, String asInternalComments, String asFirstLaunch,
			Boolean aoCommentAuditStatus, String asUserId, String asUserOrg, List<HhsAuditBean> aoAuditBeanList)
	{
		Boolean loCommentAuditStatus = aoCommentAuditStatus;
		if (null == asPreviousComments && null != asInternalComments
				&& !asInternalComments.equals(HHSR5Constants.EMPTY_STRING))
		{
			loCommentAuditStatus = true;
		}
		// save agency comments to user comments
		else if (null != asInternalComments && !asInternalComments.equals(HHSR5Constants.EMPTY_STRING)
				&& null != asPreviousComments && !asPreviousComments.equalsIgnoreCase(asInternalComments))
		{
			loCommentAuditStatus = true;
		}
		if (loCommentAuditStatus)
		{
			aoAuditBeanList.add(HHSUtil.getBeanForSavingUserComments(asTaskId, asWobNumber,
					HHSR5Constants.FINALIZE_AWARD_AMOUNT, asProcurementId, asUserId, asUserOrg, null,
					asInternalComments));
			aoAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSConstants.AUDIT_TASK_INTERNAL_COMMENTS,
					HHSR5Constants.FINALIZE_AWARD_AMOUNT, asInternalComments, HHSR5Constants.FINALIZE_AWARD_AMOUNT,
					asEntityId, asUserId, HHSConstants.AGENCY_AUDIT));
		}
		if (asFirstLaunch.equalsIgnoreCase(HHSConstants.STRING_TRUE))
		{
			aoAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSConstants.STATUS_CHANGE,
					HHSR5Constants.FINALIZE_AWARD_AMOUNT, HHSConstants.STATUS_CHANGED_FROM + HHSConstants.SPACE
							+ HHSConstants.STR + HHSConstants.TASK_IN_REVIEW + HHSConstants.STR + HHSConstants.SPACE
							+ HHSR5Constants.STR_TO_TASK + HHSConstants.SPACE + HHSConstants.STR
							+ HHSR5Constants.STATUS_SUBMITTED_FOR_APPROVAL + HHSConstants.STR,
					HHSR5Constants.FINALIZE_AWARD_AMOUNT, asEntityId, asUserId, HHSR5Constants.AGENCY_AUDIT));
			aoAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSR5Constants.PROPERTY_TASK_CREATION_EVENT,
					HHSR5Constants.APPROVE_AWARD_AMOUNT, HHSR5Constants.TASK_ASSIGNED_TO_UNASSIGNED_ACCELERATOR,
					HHSR5Constants.APPROVE_AWARD_AMOUNT, asEntityId, asUserId, HHSR5Constants.AGENCY_AUDIT));
		}
		else
		{
			aoAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSConstants.STATUS_CHANGE,
					HHSR5Constants.APPROVE_AWARD_AMOUNT, HHSR5Constants.STATUS_RETURN_TO_IN_REVIEW,
					HHSR5Constants.APPROVE_AWARD_AMOUNT, asEntityId, asUserId, HHSR5Constants.AGENCY_AUDIT));
		}
	}

	/**
	 * This method Added in Release 5, will redirect user to the next tab of the
	 * upload document screen step 2
	 * @param aoRequest Render Request Object
	 * @param aoResponse Render Response Object
	 * 
	 */
	@ActionMapping(params = "submit_action=fileLocation")
	protected void displayUploadingFileLocationRender(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		Document loDocument = new Document();
		Map<String, Object> loPropertyMapInfo = new HashMap<String, Object>();
		try
		{
			loDocument = (Document) ApplicationSession.getAttribute(aoRequest, true,
					ApplicationConstants.SESSION_DOCUMENT_OBJ);
			if (null == loDocument)
			{
				loDocument = (Document) aoRequest.getPortletSession().getAttribute(HHSR5Constants.DOC_SESSION_BEAN);
			}
			String lsEvalPoolMappingId = aoRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID);
			String lsWobNumber = aoRequest.getParameter(ApplicationConstants.WORKFLOW_ID);
			String lsProcurementId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
			String lsUploadingDocType = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.UPLOAD_DOC_TYPE);
			String lsReplacingDocumentId = HHSPortalUtil
					.parseQueryString(aoRequest, HHSConstants.REPLACING_DOCUMENT_ID);
			String lsOrganizationId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.ORGA_ID);
			loDocument.setHelpCategory(aoRequest.getParameter(ApplicationConstants.HELP_CATEGORY));
			loDocument.setHelpRadioButton(aoRequest.getParameter(ApplicationConstants.HELP));
			loDocument.setHelpDocDesc(aoRequest.getParameter(ApplicationConstants.DOCUMENT_DESCRIPTION));
			List<DocumentPropertiesBean> loDocumentPropsBeans = loDocument.getDocumentProperties();
			Iterator<DocumentPropertiesBean> loDocPropsIt = loDocumentPropsBeans.iterator();
			while (loDocPropsIt.hasNext())
			{
				DocumentPropertiesBean loDocProps = loDocPropsIt.next();
				if (ApplicationConstants.PROPERTY_TYPE_BOOLEAN.equalsIgnoreCase(loDocProps.getPropertyType()))
				{
					if (HHSR5Constants.ON.equalsIgnoreCase(aoRequest.getParameter(loDocProps.getPropertyId())))
					{
						loPropertyMapInfo.put(loDocProps.getPropertyId(), true);
					}
					else
					{
						loPropertyMapInfo.put(loDocProps.getPropertyId(), false);
					}
				}
				else
				{
					loPropertyMapInfo.put(loDocProps.getPropertyId(),
							aoRequest.getParameter(loDocProps.getPropertyId()));
					loDocProps.setPropValue(aoRequest.getParameter(loDocProps.getPropertyId()));
				}
			}
			FileNetOperationsUtils.validatorForUpload((HashMap) loPropertyMapInfo,loDocument.getDocType());
			aoRequest.getPortletSession().setAttribute(HHSR5Constants.DOC_SESSION_BEAN, loDocument);
			ApplicationSession.setAttribute(loDocument, aoRequest, ApplicationConstants.SESSION_DOCUMENT_OBJ);
			aoRequest.getPortletSession().setAttribute(HHSR5Constants.PROPERTY_MAP_INFO, loPropertyMapInfo,
					PortletSession.APPLICATION_SCOPE);
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSR5Constants.CREATE_TREE);
			if (lsEvalPoolMappingId != null && !lsEvalPoolMappingId.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolMappingId);
			}
			if (lsProcurementId != null && !lsProcurementId.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			}
			if (lsUploadingDocType != null && !lsUploadingDocType.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.UPLOAD_DOC_TYPE, lsUploadingDocType);
			}
			if (lsReplacingDocumentId != null && !lsReplacingDocumentId.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.REPLACING_DOCUMENT_ID, lsReplacingDocumentId);
			}
			if (lsOrganizationId != null && !lsOrganizationId.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.ORGA_ID, lsOrganizationId);
			}
			if (lsWobNumber != null && !lsWobNumber.isEmpty())
			{
				aoResponse.setRenderParameter(ApplicationConstants.WORKFLOW_ID, lsWobNumber);
			}
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception in action file upload screen step 2 in Document Vault", aoExp);
			try
			{
				setErrorMessageInResponse(aoRequest,aoResponse,aoExp,null);
			}
			catch (IOException e)
			{
				setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
				ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
			}

		}
	}

	/**
	 * This method Added in Release 5, will will show the tree structure of
	 * Document vault in upload step3 for file location.
	 * @param aoRequest render request object
	 * @param aoResponse render response object
	 * @return formpath as document location
	 */
	@RenderMapping(params = "render_action=treeCreation")
	protected String documentupload(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		String lsFormPath = null;
		Document loDocument = (Document) aoRequest.getPortletSession().getAttribute(HHSR5Constants.DOC_SESSION_BEAN);
		ApplicationSession.setAttribute(loDocument, aoRequest, ApplicationConstants.SESSION_DOCUMENT_OBJ);
		aoRequest.setAttribute(ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_PARAMETER, loDocument);
		aoRequest.setAttribute(HHSConstants.HIDDEN_DOC_REF_SEQ_NO,
				aoRequest.getParameter(HHSConstants.HIDDEN_DOC_REF_SEQ_NO));
		aoRequest.setAttribute(HHSConstants.EVALUATION_POOL_MAPPING_ID,
				aoRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID));
		aoRequest.setAttribute(HHSConstants.IS_FINANCIAL, aoRequest.getParameter(HHSConstants.IS_FINANCIAL));
		aoRequest.setAttribute(HHSConstants.CONTRACT_ID_WORKFLOW,
				aoRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW));
		aoRequest.setAttribute(HHSConstants.PROCUREMENT_ID, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
		aoRequest.setAttribute(HHSConstants.PROPOSAL_ID, aoRequest.getParameter(HHSConstants.PROPOSAL_ID));
		aoRequest.setAttribute(HHSConstants.IS_ADDENDUM_DOC, aoRequest.getParameter(HHSConstants.IS_ADDENDUM_DOC));
		aoRequest.setAttribute(HHSConstants.AWARD_ID, aoRequest.getParameter(HHSConstants.AWARD_ID));
		aoRequest.setAttribute(HHSConstants.UPLOAD_DOC_TYPE, aoRequest.getParameter(HHSConstants.UPLOAD_DOC_TYPE));
		aoRequest.setAttribute(HHSConstants.REPLACING_DOCUMENT_ID,
				aoRequest.getParameter(HHSConstants.REPLACING_DOCUMENT_ID));
		aoRequest.setAttribute(HHSConstants.ORGA_ID, aoRequest.getParameter(HHSConstants.ORGA_ID));
		aoRequest.setAttribute(HHSConstants.STAFF_ID, aoRequest.getParameter(HHSConstants.STAFF_ID));
		aoRequest.setAttribute(HHSConstants.HIDDEN_HDNCONTRACTID,
				aoRequest.getParameter(HHSConstants.HIDDEN_HDNCONTRACTID));
		aoRequest.setAttribute(ApplicationConstants.WORKFLOW_ID,
				aoRequest.getParameter(ApplicationConstants.WORKFLOW_ID));
		lsFormPath = HHSR5Constants.AGENCY_TASK_UPLOAD_LOCATION;
		return lsFormPath;
	}

	/**
	 * This method Added in Release 5, will redirect user to the back screen
	 * from step 3 while uploading a document.
	 * @param aoRequest Action Request
	 * @param aoResponse Action Response
	 */
	@ActionMapping(params = "submit_action=goBackActionFromStep3")
	public void goBackActionFromStep3(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		Document loDocument = null;
		try
		{
			String lsWorkflowId = PortalUtil.parseQueryString(aoRequest, HHSConstants.WORKFLOW_ID);
			loDocument = (Document) ApplicationSession.getAttribute(aoRequest, true,
					ApplicationConstants.SESSION_DOCUMENT_OBJ);
			ApplicationSession.setAttribute(loDocument, aoRequest, ApplicationConstants.SESSION_DOCUMENT_OBJ);
			aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID,
					HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID));
			aoResponse.setRenderParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID,
					HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.EVALUATION_POOL_MAPPING_ID));
			aoResponse.setRenderParameter(HHSConstants.WORKFLOW_ID, lsWorkflowId);
			if (null != aoRequest.getParameter(ApplicationConstants.DOC_CATEGORY))
			{
				aoResponse.setRenderParameter(ApplicationConstants.DOC_CATEGORY,
						aoRequest.getParameter(ApplicationConstants.DOCS_CATEGORY));
			}
			if (null != aoRequest.getParameter(ApplicationConstants.DOCS_TYPE))
			{
				aoResponse.setRenderParameter(ApplicationConstants.DOCS_TYPE,
						aoRequest.getParameter(ApplicationConstants.DOCS_TYPE));
			}
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.UPLOADING_FILE_INFO);
		}
		catch (Exception aoEXP)
		{
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
	}

	/**
	 * This method Added in Release 5, sets the success message which is
	 * displayed at the finish of Finalize Award Amount Task.It is called from
	 * finishFinalizeAmountTask.
	 * 
	 * @param aoResponse - ActionResponce Object
	 * @param asApproveAmountFlag - String Object
	 * @throws ApplicationException - ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@ResourceMapping("exportAllTask")
	public void exportAllTask(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws ApplicationException
	{
		try
		{
			PortletSession loPortletSession = aoResourceRequest.getPortletSession();

			String lsOrgnizationType = (String) loPortletSession.getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			String lsUserOrg = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
					PortletSession.APPLICATION_SCOPE);
			String lsRole = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ROLE,
					PortletSession.APPLICATION_SCOPE);
			String lsUserId = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);
			HashMap loDefaultMap = new HashMap();
			loDefaultMap.put("statusId", PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSR5Constants.EXPORT_ALL_TASK_REQUESTED));
			loDefaultMap.put("userOrgType", lsOrgnizationType);
			loDefaultMap.put("orgType", aoResourceRequest.getParameter("selectedAgency").split("~")[0]);
			loDefaultMap.put("userRRole", lsRole);
			loDefaultMap.put("userId", lsUserId);
			Channel loChannelObj = new Channel();
			loChannelObj.setData("loDefaultMap", loDefaultMap);
			TransactionManager.executeTransaction(loChannelObj, HHSR5Constants.EXPORT_ALL_TASK,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
		}
		catch (ApplicationException aoExe)
		{
			LOG_OBJECT.Error("ApplicationException occured in showFundingSubGrid while fetching data from database  ",
					aoExe);
		}
		catch (Exception aoExe)
		{
			LOG_OBJECT.Error("Error occured in showFundingSubGrid while fetching data from database ", aoExe);
		}
	}

}
