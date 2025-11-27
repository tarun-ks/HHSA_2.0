package com.nyc.hhs.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;

import org.jdom.Element;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.mvc.AbstractController;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.ComponentMappingConstant;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.AlertInboxBean;
import com.nyc.hhs.model.UserThreadLocal;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.util.ApplicationSession;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.PortalUtil;
import com.nyc.hhs.util.XMLUtil;

/**
 * HHS Accelerator AlertView Controller works for getting alert list,details of
 * a particular alert,deleting one or multiple alerts,filtering alerts on basis
 * of date and type and displaying total no. of alerts.
 * 
 */

public class AlertViewController extends AbstractController
{
	private static final LogInfo LOG_OBJECT = new LogInfo(AlertViewController.class);

	/**
	 * This method handle the render of a portlet page. when a request is
	 * initiated from a jsp, after processing the request it render the jsp and
	 * display the result to the end use this is an out of box method from
	 * portlet frame work which we override to achieve further functionalities
	 * 
	 * Updated for R4: Adding OrgId and OrgType to add join of Organization
	 * while fetching data for Alert Inbox list Count for OrgTpe =
	 * 'provider_org' Also adding a check to redirect to homepage with error in
	 * case of opening non-accessible Alert for provider.
	 * @param aoRequest Render request object
	 * @param aoResponse Render response object
	 * @return loModelAndView model view object of the required JSP
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	protected ModelAndView handleRenderRequestInternal(RenderRequest aoRequest, RenderResponse aoResponse)
			throws ApplicationException
	{

		long loStartTime = System.currentTimeMillis();
		ModelAndView loModelAndView = null;
		Map loMapToRender = null;
		PortletSession loPortletSessionThread = aoRequest.getPortletSession();
		String lsUserIdThreadLocal = (String) loPortletSessionThread.getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
		UserThreadLocal.setUser(lsUserIdThreadLocal);
		String lsNotificationType = "%";
		Date loFromDate = DateUtil.getDate(ApplicationConstants.ALERT_VIEW_FROM_DATE_PARAMETER);
		String lsCurrentDate = DateUtil.getCurrentDate();
		GregorianCalendar loCal = new GregorianCalendar();
		loCal.setTime(DateUtil.getDate(lsCurrentDate));
		loCal.add(GregorianCalendar.YEAR, 50);
		Date loToDate = loCal.getTime();
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsOrgId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
				PortletSession.APPLICATION_SCOPE);
		String lsOrgType = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
		String lsProviderUser = lsUserId;
		aoRequest.getPortletSession().removeAttribute("cityUserSearchProviderId", PortletSession.APPLICATION_SCOPE);
		try
		{
			String lsAction = (String) ApplicationSession.getAttribute(aoRequest,
					ApplicationConstants.ALERT_VIEW_NEXT_ACTION_RESULT);
			if (null == lsAction)
			{
				lsAction = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.ALERT_VIEW_NEXT_ACTION_RESULT);
			}
			if (lsAction != null)
			{
				if (lsAction.equalsIgnoreCase(ApplicationConstants.ALERT_VIEW_SHOW_PAGE_PARAMETER))
				{
					String lsAlertType = aoRequest.getParameter("alertType");
					Date loDateFrom = DateUtil.getDate(aoRequest.getParameter("dateFrom"));
					Date loDateTo = DateUtil.getDate(aoRequest.getParameter("dateTo"));
					String lsNextPage = aoRequest.getParameter("nextPageParam");
					getAlertList(aoRequest, lsAlertType, loDateFrom, loDateTo, lsNextPage);

					aoRequest.setAttribute("itemList", ApplicationSession.getAttribute(aoRequest,
							ApplicationConstants.ALERT_VIEW_ITEM_LIST_RESULT));
					aoRequest.setAttribute("filterList", ApplicationSession.getAttribute(aoRequest, true,
							ApplicationConstants.ALERT_VIEW_FILTER_LIST_RESULT));
					aoRequest.setAttribute("rowCount", ApplicationSession.getAttribute(aoRequest,
							ApplicationConstants.ALERT_VIEW_ROW_COUNT_RESULT));
					aoRequest.setAttribute("next_open", ApplicationSession.getAttribute(aoRequest,
							ApplicationConstants.ALERT_VIEW_NEXT_OPEN_RESULT));
					loModelAndView = new ModelAndView("alertinbox", loMapToRender);
				}
				else if (lsAction.equalsIgnoreCase(ApplicationConstants.ALERT_VIEW_SHOW_DETAIL_PAGE_PARAMETER))
				{
					loMapToRender = new HashMap();
					aoRequest.setAttribute("alertInboxBean", ApplicationSession.getAttribute(aoRequest,
							ApplicationConstants.ALERT_VIEW_SHOW_DETAIL_RESULT));
					loModelAndView = new ModelAndView("AlertDetail", loMapToRender);
				}
			}
			else
			{
				Channel loChannelObjRowCount = new Channel();
				HashMap<String, Object> loChannelHashMap = new HashMap<String, Object>();
				loChannelHashMap.put("asNotificationType", lsNotificationType);
				loChannelHashMap.put("asUserId", lsProviderUser);
				loChannelHashMap.put("asFromDate", loFromDate);
				loChannelHashMap.put("asToDate", loToDate);
				// R4 - Adding OrgId to add join of Organization while fetching
				// data for Alert Inbox list Count
				loChannelHashMap.put("asOrgId", lsOrgId);
				loChannelHashMap.put("orgType", lsOrgType);
				loChannelObjRowCount.setData("loChannelHashMap", loChannelHashMap);

				TransactionManager.executeTransaction(loChannelObjRowCount, "countAlertInboxListItem");
				int liRowCount = (Integer) loChannelObjRowCount.getData("liAlertCount");
				aoRequest.setAttribute("alertInboxCount", liRowCount);

				loModelAndView = new ModelAndView("alertportlet", loMapToRender);
			}
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error occured while displaying Alert Notification", aoExp);
			String lsErrorMsg = aoExp.getStackTraceAsString();
			lsErrorMsg = lsErrorMsg.substring(lsErrorMsg.lastIndexOf(":") + 1, lsErrorMsg.length()).trim();
			if (lsErrorMsg.equalsIgnoreCase(""))
			{
				lsErrorMsg = "Error occurred while processing your request";
			}
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		}

		if (null != aoRequest.getParameter("alertPermissionError")
				&& ApplicationConstants.TRUE.equalsIgnoreCase((String) aoRequest.getParameter("alertPermissionError")))
		{
			aoRequest.setAttribute("alertPermissionError", ApplicationConstants.TRUE);
		}
		long loEndTimeTime = System.currentTimeMillis();
		try
		{
			LOG_OBJECT.Debug("TIME TAKEN for execution of render Method in AlertViewController = "
					+ (loEndTimeTime - loStartTime));
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Error while execution of render Method in AlertViewController", aoEx);
		}
		UserThreadLocal.unSet();
		return loModelAndView;
	}

	/**
	 * This method handle the action of a portlet page. when a action is
	 * initiated from a jsp, it process the action by calling multiple
	 * transactions and forward it to render method for further processing this
	 * is an out of box method from portlet frame work which we override to
	 * achieve further functionalities Updated for R4 - Adding a check to
	 * redirect to homepage with error in case of opening non-accessible Alert
	 * for provider.
	 * @param aoRequest Render request object
	 * @param aoResponse Render response object
	 * @return loModelAndView model view object of the required JSP
	 * @throws ApplicationException when an Application Exception occurred
	 */
	protected void handleActionRequestInternal(ActionRequest aoRequest, ActionResponse aoResponse)
			throws ApplicationException
	{
		long loStartTime = System.currentTimeMillis();
		PortletSession loPortletSessionThread = aoRequest.getPortletSession();
		String lsUserIdThreadLocal = (String) loPortletSessionThread.getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
		UserThreadLocal.setUser(lsUserIdThreadLocal);
		String lsNextAction = (aoRequest.getParameter(ApplicationConstants.ALERT_VIEW_NEXT_ACTION_PARAMETER));
		try
		{
			if (null != lsNextAction)
			{
				if (lsNextAction.equalsIgnoreCase(ApplicationConstants.ALERT_VIEW_SHOW_PAGE_PARAMETER))
				{
					if (null != aoRequest.getParameter(ApplicationConstants.ALERT_VIEW_FILTER_ALERT_TYPE))
					{
						aoResponse.setRenderParameter("alertType",
								aoRequest.getParameter(ApplicationConstants.ALERT_VIEW_FILTER_ALERT_TYPE));
					}
					if (null != aoRequest.getParameter(ApplicationConstants.ALERT_VIEW__FILTER_DATE_FROM))
					{
						aoResponse.setRenderParameter("dateFrom",
								aoRequest.getParameter(ApplicationConstants.ALERT_VIEW__FILTER_DATE_FROM));
					}
					if (null != aoRequest.getParameter(ApplicationConstants.ALERT_VIEW_FILTER_DATE_TO))
					{
						aoResponse.setRenderParameter("dateTo",
								aoRequest.getParameter(ApplicationConstants.ALERT_VIEW_FILTER_DATE_TO));
					}
					if (null != aoRequest.getParameter(ApplicationConstants.ALERT_VIEW_NEXT_PAGE_PAGING_PARAMETER))
					{
						aoResponse.setRenderParameter("nextPageParam",
								aoRequest.getParameter(ApplicationConstants.ALERT_VIEW_NEXT_PAGE_PAGING_PARAMETER));
					}
				}
				else if (lsNextAction.equalsIgnoreCase(ApplicationConstants.ALERT_VIEW_SHOW_DETAIL_PAGE_PARAMETER))
				{
					String lsId = aoRequest.getParameter(ApplicationConstants.ALERT_VIEW_NOTIFICATION_ID_PARAMETER);
					AlertInboxBean loAlertInboxBean = new AlertInboxBean();
					Channel loChannelObj = new Channel();
					loChannelObj.setData("asNotificationId", lsId);
					TransactionManager.executeTransaction(loChannelObj, "selectAlertInboxList");
					loAlertInboxBean = (AlertInboxBean) loChannelObj.getData("loAlertInboxBean");
					boolean lbPermissionAccessReturnToHome = checkForPermissionAccessandRedirect(loAlertInboxBean,
							loPortletSessionThread);
					ApplicationSession.setAttribute(loAlertInboxBean, aoRequest,
							ApplicationConstants.ALERT_VIEW_SHOW_DETAIL_RESULT);
					if (lbPermissionAccessReturnToHome)
					{
						aoResponse.setRenderParameter("alertPermissionError", ApplicationConstants.TRUE);
					}
				}
				else if (lsNextAction.equalsIgnoreCase(ApplicationConstants.ALERT_VIEW_DELETE_PARAMETER))
				{
					String lsNotificationId = aoRequest
							.getParameter(ApplicationConstants.ALERT_VIEW_NOTIFICATION_ID_PARAMETER);
					List loNotificationList = new ArrayList();
					loNotificationList.add(lsNotificationId);
					Channel loChannelObj = new Channel();
					loChannelObj.setData("asNotificationIds", loNotificationList);
					TransactionManager.executeTransaction(loChannelObj, "deleteAlertInboxListItem");
					lsNextAction = ApplicationConstants.ALERT_VIEW_SHOW_PAGE_PARAMETER;
				}
				else if (lsNextAction.equalsIgnoreCase(ApplicationConstants.ALERT_VIEW_DELETE_MANY_PARAMETER))
				{
					String lsNotificationIds = aoRequest
							.getParameter(ApplicationConstants.ALERT_VIEW_NOTIFICATION_IDS_PARAMETER);
					String[] lsNotificationIdArray = lsNotificationIds.split("\\|");
					List loNotificationList = Arrays.asList(lsNotificationIdArray);
					Channel loChannelObj = new Channel();
					loChannelObj.setData("asNotificationIds", loNotificationList);
					TransactionManager.executeTransaction(loChannelObj, "deleteAlertInboxListItem");
					lsNextAction = ApplicationConstants.ALERT_VIEW_SHOW_PAGE_PARAMETER;
				}
				else if (lsNextAction.equalsIgnoreCase(ApplicationConstants.ALERT_VIEW_APPLY_FILTER_PARAMETER))
				{
					getFilterList(aoRequest);
					lsNextAction = ApplicationConstants.ALERT_VIEW_SHOW_PAGE_PARAMETER;
				}
				ApplicationSession.setAttribute(lsNextAction, aoRequest,
						ApplicationConstants.ALERT_VIEW_NEXT_ACTION_RESULT);
				try
				{
					aoResponse.setWindowState(WindowState.MAXIMIZED);
				}
				catch (WindowStateException aoExp)
				{
					ApplicationException loAppEx = new ApplicationException(
							"Error occured while getting entire Alert information  from notifications table", aoExp);
					throw loAppEx;
				}
			}
		}
		catch (ApplicationException aoAppEe)
		{
			LOG_OBJECT.Error("Error while execution of action Method in AlertViewController", aoAppEe);
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while execution of action Method in AlertViewController", aoExp);
		}
		long loEndTimeTime = System.currentTimeMillis();
		try
		{
			LOG_OBJECT.Debug("TIME TAKEN for execution of action Method in AlertViewController = "
					+ (loEndTimeTime - loStartTime));
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Error while execution of action Method in AlertViewController", aoEx);
		}
		UserThreadLocal.unSet();
	}

	/**
	 * This method will get alert list depending upon input parameters Updated
	 * for R4 - Adding OrgId and OrgType to add join of Organization while
	 * fetching data for Alert Inbox list Count for OrgTpe = 'provider_org'
	 * @param aoRequest an Action Request object
	 * @param aoResponse an Action Response Object
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public void getAlertList(RenderRequest aoRequest, String asAlertType, Date aoDateFrom, Date aoDateTo,
			String asNextPage) throws ApplicationException
	{
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsOrgType = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
		String lsOrgId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
				PortletSession.APPLICATION_SCOPE);
		Date loFromDate = null;
		String lsAlertType = "";
		Date loDateTo = null;
		AlertInboxBean loAlertFilterInboxBean = null;
		PortletSession loSession = aoRequest.getPortletSession();
		loAlertFilterInboxBean = (AlertInboxBean) ApplicationSession.getAttribute(aoRequest, true,
				ApplicationConstants.ALERT_VIEW_FILTER_LIST_RESULT);
		boolean lbVisibleTo = CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HEADER_AGENCY, loSession);
		if (null == loAlertFilterInboxBean)
		{
			loAlertFilterInboxBean = setFilterBean(asAlertType, aoDateFrom, aoDateTo, lsOrgType,
					DateUtil.getDateMMddYYYYFormat(aoDateFrom), DateUtil.getDateMMddYYYYFormat(aoDateTo), lbVisibleTo);
		}
		HashMap loFormattedHm = formatFilterColums(loAlertFilterInboxBean.getMsSelectedAlertType(),
				loAlertFilterInboxBean.getMsFromFilterModifyDate(), loAlertFilterInboxBean.getMsToFilterModifyDate());
		lsAlertType = (String) loFormattedHm.get("asAlertType");
		loFromDate = (Date) loFormattedHm.get("asDateFrom");
		loDateTo = (Date) loFormattedHm.get("asDateTo");
		HashMap loPagingHm = getPagingHashMap(aoRequest.getPortletSession(), asNextPage);

		ApplicationSession.setAttribute(
				getAlertInboxList(lsAlertType, lsUserId, lsOrgId, loFromDate, (Integer) loPagingHm.get("liStartNode"),
						(Integer) loPagingHm.get("liEndNode"), loDateTo, lsOrgType), aoRequest,
				ApplicationConstants.ALERT_VIEW_ITEM_LIST_RESULT);
		ApplicationSession.setAttribute(
				getRowCount(aoRequest.getPortletSession(), lsAlertType, lsUserId, lsOrgId, loFromDate, loDateTo,
						lsOrgType), aoRequest, ApplicationConstants.ALERT_VIEW_ROW_COUNT_RESULT);

		aoRequest.getPortletSession().setAttribute("pageNext", ApplicationConstants.ALERT_VIEW_SHOW_PAGE_PARAMETER,
				PortletSession.APPLICATION_SCOPE);
		ApplicationSession.setAttribute(loAlertFilterInboxBean, aoRequest,
				ApplicationConstants.ALERT_VIEW_FILTER_LIST_RESULT);
		ApplicationSession.setAttribute(ApplicationConstants.ALERT_VIEW_SHOW_PAGE_PARAMETER, aoRequest,
				ApplicationConstants.ALERT_VIEW_NEXT_OPEN_RESULT);
	}

	/**
	 * This method will get filter list depending upon filter parameters Updated
	 * for R4 - Adding OrgId and OrgType to add join of Organization while
	 * fetching data for Alert Inbox list Count for OrgTpe = 'provider_org'
	 * @param aoRequest an Action Request object
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public void getFilterList(ActionRequest aoRequest) throws ApplicationException
	{
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsOrgType = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
		String lsOrgId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
				PortletSession.APPLICATION_SCOPE);
		String lsAlertType = aoRequest.getParameter(ApplicationConstants.ALERT_VIEW_FILTER_ALERT_TYPE);
		Date loDateFrom = DateUtil.getDate((String) aoRequest
				.getParameter(ApplicationConstants.ALERT_VIEW__FILTER_DATE_FROM));
		Date loDateTo = DateUtil.getDate((String) aoRequest
				.getParameter(ApplicationConstants.ALERT_VIEW_FILTER_DATE_TO));
		String lsDateFrom = aoRequest.getParameter(ApplicationConstants.ALERT_VIEW__FILTER_DATE_FROM);
		String lsDateTo = aoRequest.getParameter(ApplicationConstants.ALERT_VIEW_FILTER_DATE_TO);
		String lsNextPage = aoRequest.getParameter(ApplicationConstants.ALERT_VIEW_NEXT_PAGE_PAGING_PARAMETER);
		PortletSession loSession = aoRequest.getPortletSession();
		boolean lbVisibleTo = CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HEADER_AGENCY, loSession);
		AlertInboxBean loAlertFilterInboxBean = setFilterBean(lsAlertType, loDateFrom, loDateTo, lsOrgType, lsDateFrom,
				lsDateTo, lbVisibleTo);
		HashMap loPagingHm = getPagingHashMap(aoRequest.getPortletSession(), lsNextPage);
		HashMap loFormattedHm = formatFilterColums(loAlertFilterInboxBean.getMsSelectedAlertType(),
				loAlertFilterInboxBean.getMsFromFilterModifyDate(), loAlertFilterInboxBean.getMsToFilterModifyDate());
		lsAlertType = (String) loFormattedHm.get("asAlertType");
		loDateFrom = (Date) loFormattedHm.get("asDateFrom");
		loDateTo = (Date) loFormattedHm.get("asDateTo");

		ApplicationSession.setAttribute(
				getAlertInboxList(lsAlertType, lsUserId, lsOrgId, loDateFrom, (Integer) loPagingHm.get("liStartNode"),
						(Integer) loPagingHm.get("liEndNode"), loDateTo, lsOrgType), aoRequest,
				ApplicationConstants.ALERT_VIEW_ITEM_LIST_RESULT);
		ApplicationSession.setAttribute(
				getRowCount(aoRequest.getPortletSession(), lsAlertType, lsUserId, lsOrgId, loDateFrom, loDateTo,
						lsOrgType), aoRequest, ApplicationConstants.ALERT_VIEW_ROW_COUNT_RESULT);
		ApplicationSession.setAttribute(loAlertFilterInboxBean, aoRequest,
				ApplicationConstants.ALERT_VIEW_FILTER_LIST_RESULT);
		ApplicationSession.setAttribute(ApplicationConstants.ALERT_VIEW_APPLY_FILTER_PARAMETER, aoRequest,
				ApplicationConstants.ALERT_VIEW_NEXT_OPEN_RESULT);
	}

	/**
	 * This method will get alert inbox list for input parameters Updated for R4
	 * - Adding OrgId and OrgType to add join of Organization while fetching
	 * data for Alert Inbox list Count for OrgTpe = 'provider_org'
	 * @param aoRequest an Action Request object
	 * @param asAlertType a string value of alert type
	 * @param asUserRole a string value of user role
	 * @param asOrgId a string value of user organization id
	 * @param asDateFrom a string value of from date
	 * @param aiStartNode a string value of start node
	 * @param aiEndNode a string value of end node
	 * @param asOrgType a string containing Organization Type
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public List getAlertInboxList(String asAlertType, String asUserRole, String asOrgId, Date aoDateFrom,
			int aiStartNode, int aiEndNode, Date aoDateTo, String asOrgType) throws ApplicationException
	{
		Channel loChannelObj = new Channel();
		HashMap<String, Object> loChannelHashMap = new HashMap<String, Object>();
		loChannelHashMap.put("asNotificationType", asAlertType);
		loChannelHashMap.put("asUserId", asUserRole);
		loChannelHashMap.put("asOrgId", asOrgId);
		loChannelHashMap.put("asFromDate", aoDateFrom);
		loChannelHashMap.put("asStartNode", aiStartNode);
		loChannelHashMap.put("asEndNode", aiEndNode);
		loChannelHashMap.put("asToDate", aoDateTo);
		loChannelHashMap.put("orgType", asOrgType);
		loChannelObj.setData("loChannelHashMap", loChannelHashMap);
		TransactionManager.executeTransaction(loChannelObj, "getAlertInboxList");
		return (ArrayList<AlertInboxBean>) loChannelObj.getData("loAlertInboxList");
	}

	/**
	 * This method will set filter properties for bean object
	 * 
	 * @param aoRequest an Action Request object
	 * @param aoResponse an Action Response object
	 * @return a object containing alert properties
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public AlertInboxBean setFilterBean(String asAlertType, Date aoDateFrom, Date aoDateTo, String asOrgType,
			String asFromDate, String asToDate, boolean abVisibleTo) throws ApplicationException
	{
		AlertInboxBean loAlertInboxBean = new AlertInboxBean();
		loAlertInboxBean.setMsSelectedAlertType(asAlertType);
		loAlertInboxBean.setMsToFilterDate(asToDate);
		loAlertInboxBean.setMsFromFilterDate(asFromDate);
		loAlertInboxBean.setMsToFilterModifyDate(aoDateTo);
		loAlertInboxBean.setMsFromFilterModifyDate(aoDateFrom);

		ArrayList<String> loCategoryList = new ArrayList<String>();
		org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENET_EXTENDED_DOC_TYPE);

		Element loElt = XMLUtil.getElement("//" + P8Constants.OBJECT + "[@name=\"alert\"] //"
				+ P8Constants.ORGANIZATION_TYPE + "[@name=\"" + asOrgType + "\"]", loXMLDoc);

		if (null != loElt)
		{
			List<Element> loChildrenElementList = loElt.getChildren(P8Constants.XML_DOC_TYPE_NODE);
			for (Element loElement : loChildrenElementList)
			{
				String lsVisibleToLocal = loElement.getAttributeValue("visibleTo");
				String lsElementText = loElement.getAttributeValue("name");
				if ((abVisibleTo && lsVisibleToLocal != null && lsVisibleToLocal.equalsIgnoreCase("r1"))
						|| !abVisibleTo)
				{
					loCategoryList.add(lsElementText);
				}
			}
			loCategoryList.add(0, "");
		}
		loAlertInboxBean.setMsAlertTypeList(loCategoryList);
		return loAlertInboxBean;
	}

	/**
	 * This method will get row count of alerts for user Updated for R4 - Adding
	 * OrgId and OrgType to add join of Organization while fetching data for
	 * Alert Inbox list Count for OrgTpe = 'provider_org'
	 * @param aoRequest an Action Request object
	 * @param asAlertType a string valure of alert type
	 * @param asUserRole a string value of user role
	 * @param asOrgId a string value of user Org Id
	 * @param asDateFrom a string value of from date
	 * @param asOrgType a string containing Organization Type
	 * @return an integer value of row count
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public int getRowCount(PortletSession aoSession, String asAlertType, String asUserRole, String asOrgId,
			Date aoDateFrom, Date aoDateTo, String asOrgType) throws ApplicationException
	{
		Channel loChannelObjRowCount = new Channel();
		HashMap<String, Object> loChannelHashMap = new HashMap<String, Object>();
		loChannelHashMap.put("asNotificationType", asAlertType);
		loChannelHashMap.put("asUserId", asUserRole);
		loChannelHashMap.put("asOrgId", asOrgId);
		loChannelHashMap.put("asFromDate", aoDateFrom);
		loChannelHashMap.put("asToDate", aoDateTo);
		loChannelHashMap.put("orgType", asOrgType);
		loChannelObjRowCount.setData("loChannelHashMap", loChannelHashMap);
		TransactionManager.executeTransaction(loChannelObjRowCount, "countAlertInboxListItem");
		int liRowCount = (Integer) loChannelObjRowCount.getData("liAlertCount");

		aoSession.setAttribute(ApplicationConstants.ALERT_VIEW_PAGING_RECORDS, liRowCount,
				PortletSession.APPLICATION_SCOPE);
		return liRowCount;
	}

	/**
	 * This method will get the map object containing paging values
	 * 
	 * @param aoRequest an Action Request object
	 * @return a paging hashmap object
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public HashMap getPagingHashMap(PortletSession aoSession, String asNextPage) throws ApplicationException
	{
		String lsAppSettingMapKey = "AlertViewController" + "_" + P8Constants.DOCUMENT_VAULT_ALLOWED_OBJECT_PER_PAGE;
		HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb.getInstance()
				.getCacheObject(ApplicationConstants.APPLICATION_SETTING);
		long llAllowedObjectCount = Long.valueOf(loApplicationSettingMap.get(lsAppSettingMapKey));
		aoSession.setAttribute("allowedObjectCount", Integer.valueOf(String.valueOf(llAllowedObjectCount)),
				PortletSession.APPLICATION_SCOPE);
		String lsPageIndex = "1";
		if (null != asNextPage)
		{
			lsPageIndex = asNextPage;
		}
		int liStartNode = 0;

		if (Integer.valueOf(lsPageIndex) - 1 == 0)
		{
			liStartNode = 1;
		}
		else
		{
			liStartNode = (Integer.valueOf(lsPageIndex) - 1) * Integer.valueOf(String.valueOf(llAllowedObjectCount))
					+ 1;
		}
		int liEndNode = Integer.valueOf(lsPageIndex) * Integer.valueOf(String.valueOf(llAllowedObjectCount));
		aoSession.setAttribute(ApplicationConstants.ALERT_VIEW_PAGING_PAGE_INDEX, lsPageIndex,
				PortletSession.APPLICATION_SCOPE);
		HashMap loPagingObject = new HashMap();
		loPagingObject.put("lsPageIndex", lsPageIndex);
		loPagingObject.put("liStartNode", liStartNode);
		loPagingObject.put("liEndNode", liEndNode);
		return loPagingObject;
	}

	/**
	 * This method will format filter parameters
	 * 
	 * @param asAlertType a string value of alert type
	 * @param aoDateFrom a string value of from date
	 * @param aoDateTo a string value of to date
	 * @return a formatted map object
	 * @throws ApplicationException
	 */

	public HashMap formatFilterColums(String asAlertType, Date aoDateFrom, Date aoDateTo) throws ApplicationException
	{
		if (null == asAlertType || "".equalsIgnoreCase(asAlertType))
		{
			asAlertType = "%";
		}
		if (null == aoDateFrom)
		{
			aoDateFrom = DateUtil.getDate(ApplicationConstants.ALERT_VIEW_FROM_DATE_PARAMETER);
		}
		if (null == aoDateTo)
		{
			String lsCurrentDate = DateUtil.getCurrentDate();
			GregorianCalendar loCal = new GregorianCalendar();
			loCal.setTime(DateUtil.getDate(lsCurrentDate));
			loCal.add(GregorianCalendar.YEAR, 50);
			aoDateTo = loCal.getTime();
		}
		HashMap loFormatObject = new HashMap();
		loFormatObject.put("asAlertType", asAlertType);
		loFormatObject.put("asDateFrom", aoDateFrom);
		loFormatObject.put("asDateTo", aoDateTo);
		return loFormatObject;
	}

	/**
	 * This method identifies whether the Alert to display is accessible to
	 * logged-in user. and returns a true flag if user doesn't matches the user
	 * role and permission at the time Alert was sent to Alert is displayed
	 * Created for R4
	 * @param aoAlertInboxBean AlertInbox Bean received corresponding to the
	 *            Alert to display
	 * @param aoPortletSession Portlet Session
	 * @return Boolean flag to determine whether or not to redirect to homepage
	 *         with error message
	 */
	private boolean checkForPermissionAccessandRedirect(AlertInboxBean aoAlertInboxBean, PortletSession aoPortletSession)
	{
		boolean lbRedirectToHome = false;
		String lsUserOrgType = (String) aoPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
				PortletSession.APPLICATION_SCOPE);
		String lsUserPermissionFromSession = (String) aoPortletSession.getAttribute(
				ApplicationConstants.KEY_SESSION_USER_PERMISSION_TYPE, PortletSession.APPLICATION_SCOPE);
		String lsUserPermissionLevelFromSession = (String) aoPortletSession.getAttribute(
				ApplicationConstants.KEY_SESSION_USER_PERMISSION_LEVEL, PortletSession.APPLICATION_SCOPE);
		if (null != lsUserOrgType && lsUserOrgType.equalsIgnoreCase(ApplicationConstants.PROVIDER_ORG)
				&& null != lsUserPermissionFromSession
				&& !lsUserPermissionFromSession.equalsIgnoreCase(ApplicationConstants.ROLE_FINANCIALPROCUREMENT)
				&& null != aoAlertInboxBean && null != aoAlertInboxBean.getMsPermissionType()
				&& !aoAlertInboxBean.getMsPermissionType().isEmpty())
		{
			String lsAlertPermissionType = aoAlertInboxBean.getMsPermissionType();
			String[] loAlertPermissionTypeArray = lsAlertPermissionType.split(HHSConstants.COMMA);
			for (String lsTempPermissionType : loAlertPermissionTypeArray)
			{
				if (!lsTempPermissionType.equalsIgnoreCase(lsUserPermissionFromSession))
				{
					lbRedirectToHome = true;
				}
				else
				{
					lbRedirectToHome = false;
					break;
				}
			}
		}
		if (!lbRedirectToHome && null != lsUserOrgType
				&& lsUserOrgType.equalsIgnoreCase(ApplicationConstants.PROVIDER_ORG)
				&& null != lsUserPermissionLevelFromSession
				&& !lsUserPermissionLevelFromSession.equalsIgnoreCase(ApplicationConstants.PROVIDER_PERMISSION_LEVEL_2)
				&& null != aoAlertInboxBean && null != aoAlertInboxBean.getMsPermissionLevel())
		{
			String lsAlertPermissionLevel = aoAlertInboxBean.getMsPermissionLevel();
			String[] loAlertPermissionLevelArray = lsAlertPermissionLevel.split(HHSConstants.COMMA);
			for (String lsTempPermissionLevel : loAlertPermissionLevelArray)
			{
				// Updated in R6 for defect 8577
				if (!lsTempPermissionLevel.equalsIgnoreCase(lsUserPermissionLevelFromSession))
				{
					if (aoAlertInboxBean.getMsEventId().startsWith(HHSR5Constants.AL321)
							|| aoAlertInboxBean.getMsEventId().startsWith(HHSR5Constants.AL323))
					{
						if (lsTempPermissionLevel.equalsIgnoreCase(HHSR5Constants.EXECUTIVE_DIRECTOR))
						{
							lbRedirectToHome = false;
							break;
						}
					}
					else
					{
						lbRedirectToHome = true;
					}
					// R6 End:
				}
				else
				{
					lbRedirectToHome = false;
					break;
				}
			}
		}
		return lbRedirectToHome;
	}
}