package com.nyc.hhs.report.action;

import java.util.ArrayList;
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
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.controllers.BaseController;
import com.nyc.hhs.controllers.util.ContractListUtils;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.grid.ColumnTag;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.ProposalStatusInfo;
import com.nyc.hhs.model.ReportBean;
import com.nyc.hhs.model.ReportMapping;
import com.nyc.hhs.util.ApplicationSession;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.HHSPortalUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PortalUtil;
import com.nyc.hhs.util.XMLUtil;

@Controller(value = "BirtReportController")
@RequestMapping("view")
/**
 * <p>
 * BirtReportController serves as the controller for report list page.
 * Report page for Accelerator/Agency/Provider is rendered through
 * handleRenderRequestInternal method of this controller. All the actions on
 * report page are handled by this controller e.g. sorting on column
 * headers, pagination, filteration on report page based on different criterias.
 * </p>
 */
public class BirtReportController extends BaseController
{
	private static final LogInfo LOG_OBJECT = new LogInfo(BirtReportController.class);
	/**
	 * Constructor of the object.
	 */
	public BirtReportController()
	{
		super();
	}
	/**
	 * Constructor of the object This will return object of report bean.
	 */
	@ModelAttribute("ReportBean")
	public ReportBean getCommandObject()
	{
		return new ReportBean();
	}

	/**
	 * This method is default render method to display report screen dashboard
	 * for Accelerator/Agency/Provider.
	 * @param aoRequest request as input
	 * @param aoResponse response as input
	 * @return Detailed dashboard
	 * @throws ApplicationException Exception in case a code fails
	 */
	@RenderMapping
	protected ModelAndView handleRenderRequestInternal(RenderRequest aoRequest, RenderResponse aoResponse)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("START  handleRenderRequestInternal ", CommonUtil.getCurrentTimeInMilliSec());
		ModelAndView loModelAndView = new ModelAndView(HHSR5Constants.REPORT_DASHBOARD);
		String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
		String lsUserOrg = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);
		ReportBean loReportBean = null;
		String lsRenderDashBoard = aoRequest.getParameter(HHSR5Constants.FROM_FILTER);
		String lsRequestReportType = PortalUtil.parseQueryString(aoRequest, HHSR5Constants.REPORT_TYPE);
		aoRequest.setAttribute(HHSR5Constants.REQUEST_REPORT_TYPE, lsRequestReportType);
		if (lsRenderDashBoard != null && lsRenderDashBoard.equalsIgnoreCase(HHSR5Constants.TRUE))
		{
			loReportBean = (ReportBean) aoRequest.getPortletSession().getAttribute(HHSR5Constants.REPORT_BEAN_LIST,
					PortletSession.APPLICATION_SCOPE);
		}
		else
		{
			loReportBean = new ReportBean();
			loReportBean.setFyYear(String.valueOf(HHSUtil.GetFiscalYear()));
			loReportBean.setOrganizationType(lsUserOrgType);
		}
		if (lsUserOrgType != null && lsUserOrgType.equalsIgnoreCase(ApplicationConstants.PROVIDER_ORG))
		{
			loReportBean.setProviderIdReport(lsUserOrg);
		}
		if (lsUserOrgType != null && lsUserOrgType.equalsIgnoreCase(ApplicationConstants.AGENCY_ORG))
		{
			loReportBean.setAgencyId(lsUserOrg);
		}
		if (loReportBean.getReportType() == null || loReportBean.getReportType().isEmpty())
		{
			loReportBean.setReportType(lsRequestReportType);
		}
		loReportBean.setDataGrid(false);
		loReportBean.setReportId(null);
		// start Set date for proposal summary
		aoRequest.setAttribute(HHSR5Constants.SUBMIT_DATE_FROM, HHSR5Constants.GO_LIVE_DATE);
		loReportBean.setSubmitDateFrom(HHSR5Constants.GO_LIVE_DATE);
		aoRequest.setAttribute(HHSR5Constants.SUBMIT_DATE_TO, DateUtil.getCurrentDate());
		loReportBean.setSubmitDateTo(DateUtil.getCurrentDate());
		aoRequest.getPortletSession().setAttribute(HHSR5Constants.SUBMIT_DATE_FROM, loReportBean.getSubmitDateFrom(),
				PortletSession.APPLICATION_SCOPE);
		aoRequest.getPortletSession().setAttribute(HHSR5Constants.SUBMIT_DATE_TO, loReportBean.getSubmitDateTo(),
				PortletSession.APPLICATION_SCOPE);
		// end Set date for proposal summary
		aoRequest.getPortletSession().setAttribute(HHSR5Constants.REPORT_BEAN_LIST, loReportBean,
				PortletSession.APPLICATION_SCOPE);
		getReportDropDown(aoRequest, lsUserOrgType);
		loReportBean.setFiscalYearSqlId(HHSR5Constants.GET_FISRT_FISCAL_YEAR_DASHBOARD);
		getFiscalYearInformation(aoRequest, loReportBean);
		loModelAndView.addObject(HHSR5Constants.FY_YEAR_ID, loReportBean.getFyYear());
		loModelAndView.addObject(HHSConstants.ORGANIZATION_TYPE, loReportBean.getOrganizationType());
		aoRequest.setAttribute(HHSConstants.AGENCY_ID, lsUserOrg);
		LOG_OBJECT.Debug("END  handleRenderRequestInternal " + aoRequest.getContextPath() , CommonUtil.getCurrentTimeInMilliSec());
		return loModelAndView;

	}

	/**
	 * This method will get the fiscal year information to display in fiscal
	 * year drop down
	 * @param aoRequest request as input
	 * @param aoReportBean ReportBean object
	 * @return return the fiscal year information
	 * @throws ApplicationException Exception in case code fails
	 */
	private void getFiscalYearInformation(RenderRequest aoRequest, ReportBean aoReportBean) throws ApplicationException
	{
		Channel loChannel = new Channel();
		loChannel.setData(HHSR5Constants.REPORT_BEAN, aoReportBean);
		TransactionManager.executeTransaction(loChannel, HHSR5Constants.GET_FIRST_FISCAL_YEAR,
				HHSR5Constants.TRANSACTION_ELEMENT_R5);
		List<Integer> loFiscalFiscalYearId = (List<Integer>) loChannel.getData(HHSR5Constants.LO_FISCAL_YEAR_ID);
		aoRequest.setAttribute(HHSConstants.FISCAL_INFORMATION, loFiscalFiscalYearId);
	}

	/**
	 * This method will be called when user select fiscal year from the action
	 * drop down/jump to reports/ filter button on detailed report page
	 * @param aoReportBean report bean as input
	 * @param aoRequest request as input
	 * @param aoResponse response as input
	 * @throws ApplicationException Exception in case of code failure
	 */
	@ActionMapping(params = "submit_action=filterReports")
	protected void actionReportFilter(@ModelAttribute("ReportBean") ReportBean aoReportBean, ActionRequest aoRequest,
			ActionResponse aoResponse) throws ApplicationException
	{
		LOG_OBJECT.Debug("START  actionReportFilter ", CommonUtil.getCurrentTimeInMilliSec());
		String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
		String lsUserOrg = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);
		String lsReportId = aoRequest.getParameter(HHSR5Constants.REPORT_ID);
		String lsReportType = aoRequest.getParameter(HHSR5Constants.REQUEST_REPORT_TYPE);
		String lsisCompetitionPoolEnable = (String) aoRequest.getParameter(HHSR5Constants.IS_COMPITITIOPM_POOL_ENABLED);
		if (StringUtils.isNotBlank(lsisCompetitionPoolEnable))
		{
			aoResponse.setRenderParameter(HHSR5Constants.IS_COMPITITIOPM_POOL_ENABLED, lsisCompetitionPoolEnable);
		}

		String lsNextAction = aoRequest.getParameter(ApplicationConstants.NEXT_ACTION);
		try
		{
			// on click of filter button
			if (lsNextAction != null & lsNextAction.equalsIgnoreCase(HHSR5Constants.FILTER_REPORTS))
			{
				ApplicationSession.setAttribute(aoRequest.getParameter(HHSConstants.NEXT_PAGE), aoRequest,
						HHSConstants.NEXT_PAGE_PARAM);
			}
			// reset the filter bean
			if (lsNextAction != null & lsNextAction.equalsIgnoreCase(HHSR5Constants.JUMP_TO_REPORTS))
			{
				resetReportBean(aoReportBean);
			}
			// On click of view detailed report button
			if (null != lsReportId && !lsReportId.isEmpty() && !lsReportId.equalsIgnoreCase(HHSConstants.UNDEFINED))
			{
				if (lsReportId.equalsIgnoreCase(HHSR5Constants.RECEIVABLES_REPORT)
						&& ApplicationConstants.CITY_ORG.equalsIgnoreCase(lsUserOrgType))
				{
					lsReportId = lsReportId + "_city";
				}
				else if (lsReportId.equalsIgnoreCase(HHSR5Constants.RECEIVABLES_REPORT)
						&& ApplicationConstants.AGENCY_ORG.equalsIgnoreCase(lsUserOrgType))
				{
					lsReportId = lsReportId + "_agency";
				}
				if (null != aoReportBean)
					aoReportBean.setReportId(lsReportId);
			}

			if (null != aoReportBean && lsUserOrgType != null
					&& lsUserOrgType.equalsIgnoreCase(ApplicationConstants.PROVIDER_ORG))
			{
				aoReportBean.setProviderIdReport(lsUserOrg);
			}
			else if (null != aoReportBean && lsUserOrgType != null
					&& lsUserOrgType.equalsIgnoreCase(ApplicationConstants.AGENCY_ORG)
					&& aoReportBean.getAgencyId() == null)
			{
				aoReportBean.setAgencyId(lsUserOrg);
			}
			if (aoReportBean != null)
			{
				if (StringUtils.isNotBlank(aoReportBean.getReportId()))
				{
					// set the report type financials or procurement
					setReportTypeDetailedPage(aoReportBean, aoResponse, lsUserOrgType);
					aoResponse.setRenderParameter(HHSR5Constants.RENDER_ACTION, HHSR5Constants.DETAILED_REPORT);
				}
				else if (StringUtils.isNotBlank(lsReportType) && !lsReportType.equalsIgnoreCase(HHSConstants.UNDEFINED))
				{
					aoReportBean.setReportType(lsReportType);
					aoResponse.setRenderParameter(HHSR5Constants.REPORT_TYPE, lsReportType);
				}
			}

			aoResponse.setRenderParameter(HHSR5Constants.FROM_FILTER, HHSR5Constants.TRUE);
			aoReportBean.setOrganizationType(lsUserOrgType);
			if (aoReportBean != null)
			{
				aoRequest.getPortletSession().setAttribute(HHSR5Constants.REPORT_BEAN_LIST, aoReportBean,
						PortletSession.APPLICATION_SCOPE);
			}
			LOG_OBJECT.Debug("END  actionReportFilter ", CommonUtil.getCurrentTimeInMilliSec());
		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Exception Occured while filtering birt reports", aoAppExp);
		}
	}

	/**
	 * This method reset the report bean
	 * @param aoReportBean Report bean as input
	 */
	private void resetReportBean(ReportBean aoReportBean)
	{
		aoReportBean.setAgencyId(null);
		aoReportBean.setProgramName(null);
		aoReportBean.setCtNumber(null);
		aoReportBean.setContractTitle(null);
		aoReportBean.setFyYear(String.valueOf(HHSUtil.GetFiscalYear()));
		aoReportBean.setProcurementTitle(null);
		aoReportBean.setCompitionPool(null);
		aoReportBean.setStatusId(null);
		aoReportBean.setSubmitDateFrom(null);
		aoReportBean.setSubmitDateTo(null);
	}

	/**
	 * This method will get the report detailed information
	 * @param aoReportBean Bean as input
	 * @param aoResponse response as input
	 * @param lsUserOrgType organization type as input
	 * @throws ApplicationException Exception in case a code fails
	 */
	private void setReportTypeDetailedPage(ReportBean aoReportBean, ActionResponse aoResponse, String lsUserOrgType)
			throws ApplicationException
	{
		Document loDocUserReportMapping = null;
		String lsXPathTop = null;
		Element loNodeChart = null;
		loDocUserReportMapping = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				HHSR5Constants.REPORT_MAPPING);
		lsXPathTop = "//user[(@name=\"" + lsUserOrgType + "\")]//reportName[@id='" + aoReportBean.getReportId() + "']";
		loNodeChart = XMLUtil.getElement(lsXPathTop, loDocUserReportMapping);
		aoReportBean.setReportType(loNodeChart.getAttributeValue(HHSR5Constants.REPORT_TYPE));
		aoResponse.setRenderParameter(HHSR5Constants.REPORT_TYPE,
				loNodeChart.getAttributeValue(HHSR5Constants.REPORT_TYPE));
	}

	/**
	 * This method will get the detailed report information
	 * @param aoRequest Request as input
	 * @param aoResponse repsonse as input
	 * @return Return detailed report jsp
	 * @throws ApplicationException Exception in case code fails.
	 */
	@SuppressWarnings("unchecked")
	@RenderMapping(params = "render_action=detailedReport")
	protected ModelAndView handleRenderDetailedReport(RenderRequest aoRequest, RenderResponse aoResponse)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("START  handleRenderDetailedReport ", CommonUtil.getCurrentTimeInMilliSec());
		ModelAndView loModelAndView = new ModelAndView(HHSR5Constants.DETAILED_REPORT);
		Integer loReportDataForGridCount = null;
		String lsNextPage = null;
		List<ColumnTag> loColumnTagList = new ArrayList<ColumnTag>();
		List<ReportBean> loReportDataForGrid = null;
		PortletSession loPortletSession = aoRequest.getPortletSession();
		Channel loChannel = new Channel();
		String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
		String lsUserOrg = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);
		getReportDropDown(aoRequest, lsUserOrgType);
		ReportBean loReportBean = (ReportBean) aoRequest.getPortletSession().getAttribute(
				HHSR5Constants.REPORT_BEAN_LIST, PortletSession.APPLICATION_SCOPE);
		loReportBean.setDataGrid(true);
		loChannel.setData(HHSConstants.ORGTYPE, ApplicationConstants.CITY_ORG);
		List<HashMap<String, String>> loAgencyDetails = (List<HashMap<String, String>>) loPortletSession.getAttribute(
				HHSConstants.AGENCY_DETAILS, PortletSession.APPLICATION_SCOPE);
		if (loAgencyDetails == null || loAgencyDetails.isEmpty())
		{
			loAgencyDetails = ContractListUtils.getAgencyDetails(loChannel);
			loPortletSession.setAttribute(HHSConstants.AGENCY_DETAILS, loAgencyDetails,
					PortletSession.APPLICATION_SCOPE);
		}
		List<ProposalStatusInfo> loProposalStatusInfo = (List<ProposalStatusInfo>) loPortletSession.getAttribute(
				HHSR5Constants.PROPOSAL_STATUS_INFO, PortletSession.PORTLET_SCOPE);
		if (loProposalStatusInfo == null || loProposalStatusInfo.isEmpty())
		{
			loProposalStatusInfo = getProposalStatusInfo();
			loPortletSession.setAttribute(HHSR5Constants.PROPOSAL_STATUS_INFO, loProposalStatusInfo,
					PortletSession.PORTLET_SCOPE);
		}
		if (loReportBean != null
				&& (loReportBean.getSubmitDateFrom() == null || loReportBean.getSubmitDateFrom().isEmpty()))
		{
			// set go live date
			aoRequest.setAttribute(HHSR5Constants.SUBMIT_DATE_FROM, HHSR5Constants.GO_LIVE_DATE);
			loReportBean.setSubmitDateFrom(HHSR5Constants.GO_LIVE_DATE);
		}
		if (loReportBean != null
				&& (loReportBean.getSubmitDateTo() == null || loReportBean.getSubmitDateTo().isEmpty()))
		{
			// set current date
			aoRequest.setAttribute(HHSR5Constants.SUBMIT_DATE_TO, DateUtil.getCurrentDate());
			loReportBean.setSubmitDateTo(DateUtil.getCurrentDate());
		}
		aoRequest.getPortletSession().setAttribute(HHSR5Constants.SUBMIT_DATE_FROM, loReportBean.getSubmitDateFrom(),
				PortletSession.APPLICATION_SCOPE);
		aoRequest.getPortletSession().setAttribute(HHSR5Constants.SUBMIT_DATE_TO, loReportBean.getSubmitDateTo(),
				PortletSession.APPLICATION_SCOPE);
		loChannel.setData(HHSR5Constants.REPORT_BEAN, loReportBean);
		loReportBean.setFiscalYearSqlId(HHSR5Constants.GET_FISRT_FISCAL_YEAR);
		getFiscalYearInformation(aoRequest, loReportBean);
		lsNextPage = (String) ApplicationSession.getAttribute(aoRequest, HHSConstants.NEXT_PAGE_PARAM);
		getPagingParams(loPortletSession, loReportBean, lsNextPage, HHSR5Constants.REPORT_LIST_CACHE);
		getReportingDataGridColumns(aoRequest, loColumnTagList, lsUserOrgType, loReportBean);
		// end
		TransactionManager.executeTransaction(loChannel, HHSR5Constants.GET_REPORT_DATA,
				HHSR5Constants.TRANSACTION_ELEMENT_R5);
		loReportDataForGrid = (List<ReportBean>) loChannel.getData(HHSR5Constants.LO_REPORT_LIST);
		loReportDataForGridCount = loReportDataForGrid.size();
		if (loReportDataForGridCount != null)
		{
			loReportDataForGrid = loReportDataForGrid.subList(loReportBean.getStartNode() - 1, loReportBean
					.getEndNode() > loReportDataForGridCount ? loReportDataForGridCount : loReportBean.getEndNode());
		}
		aoRequest.setAttribute(HHSConstants.PROCUREMENT_ID,
				HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID));
		aoRequest.setAttribute(HHSR5Constants.REPORT_LIST_OBJECT, loReportDataForGrid);
		loPortletSession.setAttribute(ApplicationConstants.ALERT_VIEW_PAGING_RECORDS,
				((loReportDataForGridCount == null) ? 0 : loReportDataForGridCount), PortletSession.APPLICATION_SCOPE);
		aoRequest.setAttribute(HHSR5Constants.JSP_NAME, HHSR5Constants.DETAILED_REPORT);
		aoRequest.setAttribute(HHSR5Constants.REQUEST_REPORT_TYPE, loReportBean.getReportType());
		LOG_OBJECT.Info("Birt Dashboard page fetched successfully");
		aoRequest.setAttribute(HHSR5Constants.FY_YEAR_ID, loReportBean.getFyYear());
		aoRequest.setAttribute(HHSConstants.AGENCY_ID, lsUserOrg);
		loModelAndView.addObject(HHSConstants.ORGANIZATION_TYPE, loReportBean.getOrganizationType());
		String lsisCompetitionPoolEnable = (String) aoRequest.getParameter(HHSR5Constants.IS_COMPITITIOPM_POOL_ENABLED);
		if (StringUtils.isNotBlank(lsisCompetitionPoolEnable))
		{
			aoRequest.setAttribute(HHSR5Constants.IS_COMPITITIOPM_POOL_ENABLED, lsisCompetitionPoolEnable);
		}
		LOG_OBJECT.Debug("END  handleRenderDetailedReport ", CommonUtil.getCurrentTimeInMilliSec());
		return loModelAndView;
	}

	/**
	 * This method will get the reporting data grid information
	 * @param aoRequest request as input
	 * @param loColumnTagList column tag as input
	 * @param lsUserOrgType org type as input
	 * @param loReportBean bean as input
	 * @throws ApplicationException Exception in case code fails
	 */
	private void getReportingDataGridColumns(RenderRequest aoRequest, List<ColumnTag> loColumnTagList,
			String lsUserOrgType, ReportBean loReportBean) throws ApplicationException
	{
		String lsUserType = null;
		ColumnTag loColumnTag = null;
		List<Element> loDataGridParamElementList = null;
		Document loDocReportChartMapping = null;
		String lsXPathDataGridDetails = null;
		Element loNodeChart = null;
		Document loDocUserReportMapping = null;
		String lsXPathTop = null;
		// start
		loDocUserReportMapping = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				HHSR5Constants.REPORT_MAPPING);

		lsXPathTop = HHSR5Constants.X_PATH + lsUserOrgType + HHSR5Constants.REPORT_X_PATH + loReportBean.getReportId()
				+ HHSR5Constants.PATH;
		loNodeChart = XMLUtil.getElement(lsXPathTop, loDocUserReportMapping);
		aoRequest.setAttribute(HHSR5Constants.LO_REPORT_VALUE, loNodeChart.getValue());
		aoRequest.setAttribute(HHSR5Constants.LO_REPORT_ID, loNodeChart.getAttributeValue(HHSR5Constants.ID));
		loDocReportChartMapping = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				loNodeChart.getAttributeValue(HHSR5Constants.CONFIG_FILE));
		lsXPathDataGridDetails = HHSR5Constants.GRID_X_PATH + loReportBean.getReportId() + HHSR5Constants.PATH;
		loNodeChart = XMLUtil.getElement(lsXPathDataGridDetails, loDocReportChartMapping);

		// for data grid
		lsXPathDataGridDetails = HHSR5Constants.GRID_X_PATH + loReportBean.getReportId() + HHSR5Constants.DATA_COLUMNS;
		Element loDataGridColumn = XMLUtil.getElement(lsXPathDataGridDetails, loDocReportChartMapping);
		if (null != loDataGridColumn)
		{
			String lsGridQuery = loDataGridColumn.getAttributeValue(HHSR5Constants.SQL_ID);
			loReportBean.setSqlId(lsGridQuery);
			loDataGridParamElementList = (List<Element>) loDataGridColumn.getChildren();
			if (loDataGridParamElementList != null && !loDataGridParamElementList.isEmpty())
			{
				for (Element loDataGridElt : loDataGridParamElementList)
				{
					lsUserType = (String) loDataGridElt.getAttributeValue(HHSR5Constants.USER_TYPE);
					if ((null == lsUserType || lsUserType.isEmpty()) || lsUserType.contains(lsUserOrgType))
					{
						loColumnTag = new ColumnTag();
						loColumnTag.setColumnName((String) loDataGridElt.getAttributeValue(HHSR5Constants.COLUMN_NAME));
						loColumnTag.setHeadingName((String) loDataGridElt
								.getAttributeValue(HHSR5Constants.HEADING_NAME));
						loColumnTag.setSize((String) loDataGridElt.getAttributeValue(HHSR5Constants.SIZE));
						loColumnTagList.add(loColumnTag);
					}
				}
			}
			aoRequest.setAttribute(HHSR5Constants.LO_COLUMN_TAG_LIST, loColumnTagList);
		}
	}

	/**
	 * This method will get the reporting data grid information
	 * @param aoRequest ResourceRequest object
	 * @param loColumnTagList
	 * @param lsUserOrgType User organization type string
	 * @param loReportBean ReportBean object
	 * @param asTabReportId Tab Report id string
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	@SuppressWarnings("unchecked")
	private void getReportingDataGridColumnsForResourceAjax(ResourceRequest aoRequest, List<ColumnTag> loColumnTagList,
			String lsUserOrgType, ReportBean loReportBean, String asTabReportId) throws ApplicationException
	{
		String lsUserType = null;
		ColumnTag loColumnTag = null;
		List<Element> loDataGridParamElementList = null;
		Document loDocReportChartMapping = null;
		String lsXPathDataGridDetails = null;
		Element loNodeChart = null;
		Document loDocUserReportMapping = null;
		String lsXPathTop = null;
		// start
		loDocUserReportMapping = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				HHSR5Constants.REPORT_MAPPING);

		lsXPathTop = HHSR5Constants.X_PATH + lsUserOrgType + HHSR5Constants.REPORT_X_PATH + loReportBean.getReportId()
				+ HHSR5Constants.PATH;
		loNodeChart = XMLUtil.getElement(lsXPathTop, loDocUserReportMapping);
		aoRequest.setAttribute(HHSR5Constants.LO_REPORT_VALUE, loNodeChart.getValue());
		aoRequest.setAttribute(HHSR5Constants.LO_REPORT_ID, loNodeChart.getAttributeValue(HHSR5Constants.ID));
		loDocReportChartMapping = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				loNodeChart.getAttributeValue(HHSR5Constants.CONFIG_FILE));
		lsXPathDataGridDetails = HHSR5Constants.GRID_X_PATH + loReportBean.getReportId() + HHSR5Constants.PATH;
		loNodeChart = XMLUtil.getElement(lsXPathDataGridDetails, loDocReportChartMapping);

		// for data grid
		lsXPathDataGridDetails = HHSR5Constants.GRID_X_PATH + asTabReportId + HHSR5Constants.DATA_COLUMNS;
		Element loDataGridColumn = XMLUtil.getElement(lsXPathDataGridDetails, loDocReportChartMapping);
		if (null != loDataGridColumn)
		{
			String lsGridQuery = loDataGridColumn.getAttributeValue(HHSR5Constants.SQL_ID);
			loReportBean.setSqlId(lsGridQuery);
			loDataGridParamElementList = (List<Element>) loDataGridColumn.getChildren();
			if (loDataGridParamElementList != null && !loDataGridParamElementList.isEmpty())
			{
				for (Element loDataGridElt : loDataGridParamElementList)
				{
					lsUserType = (String) loDataGridElt.getAttributeValue(HHSR5Constants.USER_TYPE);
					if ((null == lsUserType || lsUserType.isEmpty()) || lsUserType.contains(lsUserOrgType))
					{
						loColumnTag = new ColumnTag();
						loColumnTag.setColumnName((String) loDataGridElt.getAttributeValue(HHSR5Constants.COLUMN_NAME));
						loColumnTag.setHeadingName((String) loDataGridElt
								.getAttributeValue(HHSR5Constants.HEADING_NAME));
						loColumnTag.setSize((String) loDataGridElt.getAttributeValue(HHSR5Constants.SIZE));
						loColumnTagList.add(loColumnTag);
					}
				}
			}
			aoRequest.setAttribute(HHSR5Constants.LO_COLUMN_TAG_LIST, loColumnTagList);
		}
	}

	/**
	 * This method get the list of drop report to be displayed
	 * @param lsUserOrgType org type as input
	 * @param aoRequest RenderRequest object
	 * @throws ApplicationException Exception in case code fails
	 */
	private void getReportDropDown(RenderRequest aoRequest, String lsUserOrgType) throws ApplicationException
	{

		Document loDocUserReportMapping = null;
		String lsXPathTop = null;
		// for performance testing
		String lsUpdatedReportString = PortalUtil.parseQueryString(aoRequest, HHSR5Constants.REPORT_MAPPING);
		if (lsUpdatedReportString != null && lsUpdatedReportString.equalsIgnoreCase(HHSConstants.TYPE_UPDATED))
		{
			loDocUserReportMapping = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
					HHSR5Constants.REPORT_MAPPING_UPDATED);
		}
		else
		{
			loDocUserReportMapping = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
					HHSR5Constants.REPORT_MAPPING);
		}
		lsXPathTop = HHSR5Constants.X_PATH + lsUserOrgType + HHSR5Constants.REPORT_PATHS;
		List<Element> loNodeList = XMLUtil.getElementList(lsXPathTop, loDocUserReportMapping);
		List<ReportMapping> loReportList = new ArrayList<ReportMapping>();
		ReportMapping loReportListBean = null;
		for (Element loNode : loNodeList)
		{
			loReportListBean = new ReportMapping();
			loReportListBean.setReportValue(loNode.getValue());
			loReportListBean.setReportType(loNode.getAttributeValue(HHSR5Constants.REPORT_TYPE));
			loReportListBean.setReportId(loNode.getAttributeValue(HHSR5Constants.ID));
			loReportList.add(loReportListBean);
		}
		aoRequest.setAttribute(HHSR5Constants.REPORT_LIST_OPTION, loReportList);
	}

	/**
	 * This method is called when user clicks on pagination in detailed report
	 * jsp
	 * @param aoResourceRequest request as input
	 * @param aoResourceResponse response as input
	 * @return It will return the data grid jsp
	 * @throws Exception Exception in case code fails
	 */
	@SuppressWarnings("unchecked")
	@ResourceMapping("dataGridReportPaging")
	protected ModelAndView pagingDataGridReport(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws Exception
	{
		LOG_OBJECT.Debug("START  pagingDataGridReport ", CommonUtil.getCurrentTimeInMilliSec());
		Channel loChannel = new Channel();
		List<ColumnTag> loColumnTagList = new ArrayList<ColumnTag>();
		String lsUserOrgType = (String) aoResourceRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
		ReportBean loReportBean = null;
		String lsTabName = aoResourceRequest.getParameter(HHSR5Constants.TAB_NAME);
		String lsTabReportId = null;
		String lsNextPage = aoResourceRequest.getParameter(HHSConstants.NEXT_PAGE);
		try
		{
			loReportBean = (ReportBean) aoResourceRequest.getPortletSession().getAttribute(
					HHSR5Constants.REPORT_BEAN_LIST, PortletSession.APPLICATION_SCOPE);
			if (loReportBean == null)
			{
				loReportBean = new ReportBean();
			}
			lsTabReportId = loReportBean.getReportId();
			if (null != lsTabName && !lsTabName.isEmpty() && !lsTabName.equalsIgnoreCase(HHSConstants.UNDEFINED)
					&& loReportBean.getReportId() != null)
			{
				lsTabReportId = loReportBean.getReportId().concat(lsTabName);
			}
			if (lsNextPage != null && (lsNextPage.isEmpty() || lsNextPage.equalsIgnoreCase(HHSConstants.UNDEFINED)))
			{
				lsNextPage = null;
			}
			getPagingParams(aoResourceRequest.getPortletSession(), loReportBean, lsNextPage,
					HHSR5Constants.REPORT_LIST_CACHE);
			getReportingDataGridColumnsForResourceAjax(aoResourceRequest, loColumnTagList, lsUserOrgType, loReportBean,
					lsTabReportId);
			loChannel.setData(HHSR5Constants.REPORT_BEAN, loReportBean);
			TransactionManager.executeTransaction(loChannel, HHSR5Constants.GET_REPORT_DATA,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			List<ReportBean> loReportDataForGrid = (List<ReportBean>) loChannel.getData(HHSR5Constants.LO_REPORT_LIST);
			Integer loReportDataForGridCount = loReportDataForGrid.size();
			if (loReportDataForGridCount != null)
			{
				loReportDataForGrid = loReportDataForGrid
						.subList(loReportBean.getStartNode() - 1,
								loReportBean.getEndNode() > loReportDataForGridCount ? loReportDataForGridCount
										: loReportBean.getEndNode());
			}
			aoResourceRequest.setAttribute(HHSR5Constants.REPORT_LIST_OBJECT, loReportDataForGrid);
			aoResourceRequest.getPortletSession().setAttribute(ApplicationConstants.ALERT_VIEW_PAGING_RECORDS,
					((loReportDataForGridCount == null) ? 0 : loReportDataForGridCount),
					PortletSession.APPLICATION_SCOPE);
			aoResourceRequest.setAttribute(HHSR5Constants.TAB_NAME, lsTabName);
			LOG_OBJECT.Debug("END  pagingDataGridReport ", CommonUtil.getCurrentTimeInMilliSec());
		}
		catch (ApplicationException aoExe)
		{
			LOG_OBJECT.Error("ApplicationException occured in pagingDataGridReport", aoExe);

		}
		catch (Exception aoExe)
		{
			LOG_OBJECT.Error("Exception occured in pagingDataGridReport", aoExe);
		}
		return new ModelAndView(HHSR5Constants.REPORT_DATA_GRID);
	}

	/**
	 * This method will get proposal status information
	 * @return It will return list of proposal status
	 */
	private List<ProposalStatusInfo> getProposalStatusInfo()
	{
		List<ProposalStatusInfo> loProposalStatusList = null;
		Channel loChannel = new Channel();
		try
		{
			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.GET_PROPOSAL_STATUS_INFO,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			loProposalStatusList = (List<ProposalStatusInfo>) loChannel.getData(HHSR5Constants.LO_PROPOSAL_STATUS_INFO);
		}
		// Catch the exception thrown by transaction and set the error message
		// in request object and pass to jsp
		catch (ApplicationException loEx)
		{
			LOG_OBJECT.Error("Exception Occured while getting proposal name list for NYC Agency ", loEx);
		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception Occured while getting proposal name list for NYC Agency ", loExp);
		}
		return loProposalStatusList;
	}
}