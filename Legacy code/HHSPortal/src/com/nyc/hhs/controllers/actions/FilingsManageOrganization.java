package com.nyc.hhs.controllers.actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.model.ApplicationAuditBean;
import com.nyc.hhs.model.BaseFilter;
import com.nyc.hhs.model.FiscalYear;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.DateUtil;

/**Release 5 Proposal Activity and Char 500 History
 * This class sets the required values in the channel object, required to
 * execute the transaction for displaying the questions and to save the
 * questions. Also it sets the values, required in the in jsp, in the request
 * object.
 * 
 */

// Added in release 5 for module Proposal Activity History
public class FilingsManageOrganization extends BusinessApplication
{
	private static final LogInfo LOG_OBJECT = new LogInfo(
			FilingsManageOrganization.class);

	/**
	 * This method returns the channel object
	 * to be used in transaction
	 * 
	 * @param asSectionName Section name as Input
	 * @param asOrgId Organization ID as Input
	 * @param asAppId App ID as Input
	 * @param asAppStatus App Status as Input
	 * @param asAppDataForUpdate Date as Input
	 * @param asAction String as Input
	 * @param asUserRole String as Input
	 * @param aoRequest String as Input
	 * @param asTaxonomyName as Input
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	
	@Override
	public Channel getChannelObject(String asSectionName, String asOrgId, String asAppId, String asAppStatus,
			String asAppDataForUpdate, String asAction, String asUserRole, ActionRequest aoRequest,
			String asTaxonomyName) throws ApplicationException
	{
		return null;
	}

	/**
	 * This method populates bean with paging parameters
	 * <ul>
	 * <li>
	 * Gets PortletSession object, BaseFilter bean object, next page value and
	 * page key as input.</li>
	 * <li>Gets allowedObjectCount from cache object by passing modified key as
	 * page key and set in session object</li>
	 * <li>Checks for next page value.</li>
	 * <li>If available, use the same for calculating start node and end node
	 * and set these values in BaseFilter bean object.</li>
	 * </ul>
	 * 
	 * @param aoSession Portal Session object
	 * @param aoFilterBean a bean object
	 * @param asNextPage a String value containing paging node
	 * @param asPageKey key used to map the value in db table.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */

	@SuppressWarnings("unchecked")
	protected void getPagingParams(PortletSession aoSession, BaseFilter aoFilterBean, String asNextPage,
			String asPageKey) throws ApplicationException
	{
		try 
		{
		String lsAppSettingMapKey = asPageKey + HHSConstants.UNDERSCORE
				+ P8Constants.DOCUMENT_VAULT_ALLOWED_OBJECT_PER_PAGE;
		HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb.getInstance()
				.getCacheObject(ApplicationConstants.APPLICATION_SETTING);
		if (loApplicationSettingMap == null || null == loApplicationSettingMap.get(lsAppSettingMapKey))
		{
			throw new ApplicationException("Error occurred while getting cache object for key : "
					+ ApplicationConstants.APPLICATION_SETTING);
		}
		int loAllowedObjectCount = Integer.valueOf(loApplicationSettingMap.get(lsAppSettingMapKey));
		aoSession.setAttribute(HHSConstants.ALLOWED_OBJECT_COUNT, loAllowedObjectCount,
				PortletSession.APPLICATION_SCOPE);
		int lsPageIndex = HHSConstants.INT_ONE;
		int liStartNode = HHSConstants.INT_ZERO;
		if (null != asNextPage)
		{
			lsPageIndex = Integer.valueOf(asNextPage);
		}
		if ((lsPageIndex - HHSConstants.INT_ONE) == HHSConstants.INT_ZERO)
		{
			liStartNode = HHSConstants.INT_ONE;
		}
		else
		{
			liStartNode = (lsPageIndex - HHSConstants.INT_ONE) * Integer.valueOf(String.valueOf(loAllowedObjectCount))
					+ HHSConstants.INT_ONE;
		}
		int liEndNode = lsPageIndex * loAllowedObjectCount;
		aoSession.setAttribute(ApplicationConstants.ALERT_VIEW_PAGING_PAGE_INDEX, String.valueOf(lsPageIndex),
				PortletSession.APPLICATION_SCOPE);
		aoFilterBean.setStartNode(liStartNode);
		aoFilterBean.setEndNode(liEndNode);
		}
		catch (ApplicationException aoAoExp) {
			LOG_OBJECT.Error("Error occurred while getPagingParams", aoAoExp);
			throw aoAoExp;
		}
	}
    
	/**
	 * This method populates getChannelObject with parameters
	 * to be used in transaction
	 * 
	 * @param asSectionName Section name as Input
	 * @param asOrgId Organization ID as Input
	 * @param asAppId Application ID as Input
	 * @param asAppStatus Application Status as Input
	 * @param asAppDataForUpdate Date as Input
	 * @param asAction as Input
	 * @param asUserRole as Input
	 * @param aoRequest as Input
	 * @param asTaxonomyName as Input
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Override
	public Channel getChannelObject(String asSectionName, String asOrgId, String asAppId, String asAppStatus,
			String asAppDataForUpdate, String asAction, String asUserRole, RenderRequest aoRequest,
			String asTaxonomyName) throws ApplicationException
	{
		Channel loChannelobj = new Channel();
		try
		{
		PortletSession loPortletSession = aoRequest.getPortletSession();
		String lsNextPage = aoRequest.getParameter(HHSConstants.NEXT_PAGE);
		String lsActivefilingssFrom = aoRequest.getParameter(HHSR5Constants.ACTIVE_FILING_FROM);
		String lsActivefilingssTo = aoRequest.getParameter(HHSR5Constants.ACTIVE_FILING_TO);
		ApplicationAuditBean loApplicationAuditBean = new ApplicationAuditBean();
		getPagingParams(loPortletSession, loApplicationAuditBean, lsNextPage, HHSR5Constants.FILINGS_LIST_KEY);
		loApplicationAuditBean.setMsOrgId(asOrgId);
		loApplicationAuditBean.setMsEntityIdentifier(HHSR5Constants.NEW_FILINGS);
		loApplicationAuditBean.setMsEventtype(ApplicationConstants.SNAPSHOT_NAME);
		// filter params
		if (lsActivefilingssFrom != null && !lsActivefilingssFrom.isEmpty())
			loApplicationAuditBean.setActivefilingssFrom(DateUtil.getDate(lsActivefilingssFrom));
		if (lsActivefilingssTo != null && !lsActivefilingssTo.isEmpty())
			loApplicationAuditBean.setActivefilingssTo(DateUtil.getDate(lsActivefilingssTo));

		loApplicationAuditBean.setFilingPeriodFromMonth(aoRequest.getParameter(HHSR5Constants.FILING_PERIOD_FROM_MONTH));
		loApplicationAuditBean.setFilingPeriodToMonth(aoRequest.getParameter(HHSR5Constants.FILING_PERIOD_TO_MONTH));
		loApplicationAuditBean.setFiscalYearFilterFromMonth(aoRequest.getParameter(HHSR5Constants.FISCAL_YEAR_FROM_MONTH));
		loApplicationAuditBean.setFiscalYearFilterToMonth(aoRequest.getParameter(HHSR5Constants.FISCAL_YEAR_TO_MONTH));
		// filter params
		loChannelobj.setData(HHSR5Constants.LO_APPLICATION_AUDIT_BEAN, loApplicationAuditBean);
		// filings history
		loChannelobj.setData(HHSR5Constants.AS_ORG_ID, asOrgId);
		P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		loChannelobj.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
		HashMap loHashMap = new HashMap();
		loHashMap.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, "");
		loChannelobj.setData(HHSConstants.HM_REQIRED_PROPERTY_MAP, loHashMap);
		}
		catch (ApplicationException aoAoExp) 
		{
			LOG_OBJECT.Error("Error occurred while getChannelObject", aoAoExp);
			throw aoAoExp;
		}
		return loChannelobj;
	}
    
	
	/**
	 * This method fetches values from getChannelObject using RenderRequest
	 * The values are then stored in Map
	 * This Method returns the Map
	 * 
	 * @param asSectionName Section name as Input
	 * @param asAction  String as Input 
	 * @param aoRequest as Input
	 * @param aoChannel Channel as Input
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getMapForRender(String asAction, String asSectionName, Channel aoChannel,
			RenderRequest aoRequest) throws ApplicationException
	{
		Map<String, Object> loMapForRender = new HashMap<String, Object>();
		try
		{
		loMapForRender.put(ApplicationConstants.FILE_TO_INCLUDE, HHSR5Constants.FILING_HISTORY_PATH);
		String lsMenu = getSubMenu(asSectionName);
		loMapForRender.put(ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_MENU, lsMenu);
		if (null == asAction || asAction.equals(ApplicationConstants.BUSINESS_APPLICATION_ACTION_OPEN))
		{
			List<ApplicationAuditBean> loApplicationAuditBeanList = (List<ApplicationAuditBean>) aoChannel
					.getData(HHSR5Constants.LO_APPLICATION_AUDIT_BEAN_LIST);
			Integer loFiingsCount = (Integer) aoChannel.getData(HHSR5Constants.FILINGS_COUNT);
			List<FiscalYear> loFiscalInformation = (List<FiscalYear>) aoChannel
					.getData(HHSConstants.FISCAL_INFORMATION);
			Map<String, Object> loFilingDetailsMap = (Map<String, Object>) aoChannel
					.getData(HHSR5Constants.LO_FILING_MAP);
			loMapForRender.put(HHSConstants.PY_AO_FISCAL_INFORMATION, loFiscalInformation);
			loMapForRender.put(HHSR5Constants.LO_APPLICATION_AUDIT_BEAN_LIST, loApplicationAuditBeanList);
			List<String> loFilingsDropDownList= (List<String>) aoChannel.getData("filingsDropDownList");
			loMapForRender.put("filingsDropDownList", loFilingsDropDownList);
			// filter
			loMapForRender.put(HHSR5Constants.ACTIVE_FILING_FROM, aoRequest.getParameter(HHSR5Constants.ACTIVE_FILING_FROM));
			loMapForRender.put(HHSR5Constants.ACTIVE_FILING_TO, aoRequest.getParameter(HHSR5Constants.ACTIVE_FILING_TO));

			loMapForRender.put(HHSR5Constants.FILING_PERIOD_FROM_MONTH, aoRequest.getParameter(HHSR5Constants.FILING_PERIOD_FROM_MONTH));
			loMapForRender.put(HHSR5Constants.FILING_PERIOD_TO_MONTH, aoRequest.getParameter(HHSR5Constants.FILING_PERIOD_TO_MONTH));
			loMapForRender.put(HHSR5Constants.FISCAL_YEAR_FROM_MONTH, aoRequest.getParameter(HHSR5Constants.FISCAL_YEAR_FROM_MONTH));
			loMapForRender.put(HHSR5Constants.FISCAL_YEAR_TO_MONTH, aoRequest.getParameter(HHSR5Constants.FISCAL_YEAR_TO_MONTH));
			loMapForRender.put(HHSR5Constants.FILING_DETAILS_BEAN_KEY, loFilingDetailsMap);
			// filter
			loMapForRender.put(HHSConstants.PY_AO_FISCAL_INFORMATION, loFiscalInformation);
			aoRequest.getPortletSession().setAttribute(ApplicationConstants.ALERT_VIEW_PAGING_RECORDS,
					((loFiingsCount == null) ? 0 : loFiingsCount), PortletSession.APPLICATION_SCOPE);
		}
		}
		catch (ApplicationException aoAoExp) 
		{
			LOG_OBJECT.Error("Error occurred while getChannelObject", aoAoExp);
			throw aoAoExp;
		}
		return loMapForRender;
	}

	/**
	 * This method fetches values from getChannelObject using RenderRequest
	 * The values are then stored in Map
	 * This Method returns the Map
	 * 
	 * @param asSectionName Section name as Input
	 * @param asAction String as Input
	 * @param aoRequest Action Request as Input
	 * @param aoChannel Channel  as Input
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Override
	public Map<String, Object> getMapForRender(String asAction, String asSectionName, Channel aoChannel,
			ActionRequest aoRequest) throws ApplicationException
	{
		return null;
	}

}
