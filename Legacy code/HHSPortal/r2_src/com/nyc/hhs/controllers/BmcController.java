package com.nyc.hhs.controllers;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;
import org.springframework.web.portlet.context.PortletRequestAttributes;

import com.nyc.hhs.annotation.HHSExtToken;
import com.nyc.hhs.annotation.HHSTokenValidator;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.controllers.util.BmcControllerUtil;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.ContractBudgetBean;
import com.nyc.hhs.model.Procurement;
import com.nyc.hhs.model.ProcurementCOF;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PortalUtil;
import com.nyc.hhs.util.PortletSessionHandler;
import com.nyc.hhs.util.PropertyLoader;

@Controller(value = "bmcHandler")
@RequestMapping("view")
/**
 * This method is default render method to display BMC Task screens for
 * Agency.BMC Task screens include
 * procurementFundTask,contractConfigurationTaskn newFYConfigurationTask and
 * contractCertificationFundsTask
 * */
public class BmcController extends BaseController
{
	// Logging object
	private static final LogInfo LOG_OBJECT = new LogInfo(BmcController.class);

	/**
	 * This method is default render method to display BMC Task screens for
	 * Agency.BMC Task screens include
	 * procurementFundTask,contractConfigurationTaskn newFYConfigurationTask and
	 * contractCertificationFundsTask. We get lsActionReqParam from session and
	 * depending upon this param we do make operations required for that screens
	 * and redirected to corresponding Task JSP pages. <li>This method was
	 * updated in R4</li>
	 * @param aoRequest a RenderRequest object
	 * @param aoResponse a RenderResponse object
	 * @return loModelAndView - ModelAndView object
	 * @throws ApplicationException - ApplicationException object
	 */
	@RenderMapping
	/* Start QC 9654 R 9.4 Multi-Tab Browsing Causing Records to not be Inserted for Services and Cost Centers*/
	@HHSExtToken
	/* End QC 9654 R 9.4 Multi-Tab Browsing Causing Records to not be Inserted for Services and Cost Centers*/
	protected ModelAndView handleRenderRequestInternal(RenderRequest aoRequest, RenderResponse aoResponse)
			throws ApplicationException
	{
		ModelAndView loModelAndView = null;
		TaskDetailsBean loTaskDetailsBean = null;
		String lsActionReqParam = PortalUtil.parseQueryString(aoRequest, HHSConstants.RENDER_ACTION);
		try
		{
			PortletSession loSession = aoRequest.getPortletSession();
			String lsWorkflowId = PortalUtil.parseQueryString(aoRequest, HHSConstants.WORKFLOW_ID);
			if (null != lsWorkflowId)
			{
				loTaskDetailsBean = fetchTaskDetailsFromFilenet(aoRequest, lsWorkflowId);
				// loTaskDetailsBean object will contains all required
				// information
				// for Task like Contract Id,Procurement Id etc.
			}
			if (null != loTaskDetailsBean)
			{
				if (null != lsActionReqParam)
				{

					loModelAndView = updateRequestForTaskType(aoRequest, loModelAndView, loTaskDetailsBean,
							lsActionReqParam, loSession, lsWorkflowId, loTaskDetailsBean.getContractId(),
							loTaskDetailsBean.getNewFYId(), loTaskDetailsBean.getCompetitionPoolTitle());

				}
				else
				{
					setAccountGridDataInSession(aoRequest);
					setFundingGridDataInSession(aoRequest);
					loModelAndView = new ModelAndView(HHSConstants.BMC_PROC_CERT_FUND_TASK_JSP_PATH);
				}
				loSession.setAttribute(HHSConstants.TASK_DETAIL_BEAN_SESSION, loTaskDetailsBean,
						PortletSession.APPLICATION_SCOPE);
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
						PortalUtil.parseQueryString(aoRequest, HHSConstants.CBL_MESSAGE));
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
						aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE_TYPE));
			}
		}
		catch (ApplicationException loExp)
		{
			loModelAndView = new ModelAndView(HHSConstants.ERROR_PAGE_JSP);
			LOG_OBJECT.Error("ApplicationException occured in BMC Controller" + lsActionReqParam, loExp);
		}
		// handling exception other than Application Exception.
		catch (Exception loEx)
		{
			loModelAndView = new ModelAndView(HHSConstants.ERROR_PAGE_JSP);
			LOG_OBJECT.Error("Error occured in BMC Controller" + lsActionReqParam, loEx);
		}
		return loModelAndView;
	}

	/**
	 * This method is updated for Release 3.8.0 #6483
	 * This method updates the request for different task type like 'contract
	 * update' and returns the view
	 * @param aoRequest Request object
	 * @param loModelAndView Model and view object
	 * @param loTaskDetailsBean Task detail bean object
	 * @param lsActionReqParam Action request parameter
	 * @param loSession session object
	 * @param lsWorkflowId workflow id
	 * @param lsContractId contract id
	 * @param lsConfigurableFiscalYear fiscal year value
	 * @param asCompPoolTitle competition pool title
	 * @return model and view object
	 * @throws ApplicationException application exception object
	 * @throws Exception exception object
	 */
	private ModelAndView updateRequestForTaskType(RenderRequest aoRequest, ModelAndView loModelAndView,
			TaskDetailsBean loTaskDetailsBean, String lsActionReqParam, PortletSession loSession, String lsWorkflowId,
			String lsContractId, String lsConfigurableFiscalYear, String asCompPoolTitle) throws ApplicationException,
			Exception
	{
		Integer loFiscalStartYr;
		String lsAmendmentContractId;
		if (lsActionReqParam.equalsIgnoreCase(HHSConstants.TASK_PROCUREMENT_COF))
		{
			loModelAndView = ifProcurementFundTask(aoRequest, loTaskDetailsBean);
		}
		else if (lsActionReqParam.equalsIgnoreCase(HHSConstants.TASK_CONTRACT_CONFIGURATION))
		{
			Boolean loIsOpenEndedRfpStartEndDateNotSet = openEndedRfpStartEndDateNotSet(lsContractId);
			loFiscalStartYr = fetchContractConfigurationDetails(aoRequest, lsContractId, null, false, null, false,
					loIsOpenEndedRfpStartEndDateNotSet, asCompPoolTitle);
			if (!loIsOpenEndedRfpStartEndDateNotSet)
			{
				//start Build 3.1.0, enhancement id: 6020 changes to get pop up for next fiscal year 
				Channel loChannel= new Channel();
				loChannel.setData(HHSConstants.CONTRACT_ID_WORKFLOW,lsContractId);
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.BMC_GET_BUDGET_FISCAL_YEAR_ID);
				String lsBudgetStartYearId = (String)loChannel.getData(HHSConstants.FISCAL_YEAR_ID);
				if(lsBudgetStartYearId!=null)
				{
					loTaskDetailsBean.setStartFiscalYear(lsBudgetStartYearId);
				}
				else
				{
					loTaskDetailsBean.setStartFiscalYear(HHSUtil.getFYForContractBudgetConfig(loFiscalStartYr).toString());
				}
				//end
				setAccountGridDataInSession(aoRequest);
				setFundingGridDataInSession(aoRequest);
			}
			else
			{
				aoRequest.setAttribute(HHSConstants.IS_OPEN_ENDED_RFP_START_END_DATE_NOT_SET, true);
				aoRequest.setAttribute(ApplicationConstants.WORKFLOW_ID, lsWorkflowId);
				aoRequest.setAttribute(HHSConstants.CONTRACT_ID_WORKFLOW, lsContractId);
				aoRequest.setAttribute(HHSConstants.FINANCIALS_MESSAGE,(String)aoRequest.getParameter(HHSConstants.FINANCIALS_MESSAGE));
			}
			loModelAndView = new ModelAndView(HHSConstants.BMC_CONTRACT_FINANCIALS_JSP_PATH);
		}
		else if (lsActionReqParam.equalsIgnoreCase(HHSConstants.TASK_CONTRACT_UPDATE))
		{
			// fetchDiscrepencyDetailsForUpdateTask method is called when contract update task is launch which sets the discrepancy flag 
			// to true in taskdetails bean if update task is launched from batch.
			BmcControllerUtil.fetchDiscrepencyDetailsForUpdateTask(loTaskDetailsBean, lsContractId);
			fetchContractConfigurationUpdateDetails(aoRequest, lsContractId, HHSConstants.FOUR);
			fetchContractConfigurationDetails(aoRequest, lsContractId, HHSConstants.FOUR, false, null, true, false,
					null);
			setAccountGridDataInSession(aoRequest);
			setAmendmentAccountGridDataInSession(aoRequest);
			setFundingGridDataInSession(aoRequest);
			setAmendmentFundingGridDataInSession(aoRequest);
			loModelAndView = new ModelAndView(HHSConstants.BMC_CONTRACT_CONFIG_UPDATE_JSP_PATH);
		}
		else if (lsActionReqParam.equalsIgnoreCase(HHSConstants.TASK_CONTRACT_COF))
		{
			fetchContractCofTaskDetails(aoRequest, lsContractId, asCompPoolTitle);
			setAccountGridDataInSession(aoRequest);
			setFundingGridDataInSession(aoRequest);
			loModelAndView = new ModelAndView(HHSConstants.BMC_CONTRACT_CERT_FUND_TASK_JSP_PATH);
		}
		else if (lsActionReqParam.equalsIgnoreCase(HHSConstants.TASK_NEW_FY_CONFIGURATION))
		{
			loSession.setAttribute(HHSConstants.BASE_LS_CONFIGURABLE_FISCAL_YEAR, lsConfigurableFiscalYear,
					PortletSession.PORTLET_SCOPE);
			aoRequest.setAttribute(HHSConstants.BMC_ACTION_REQ_PARAM, lsActionReqParam);
			fetchNewFYConfigurationDetails(aoRequest, lsContractId);
			setAccountGridDataInSession(aoRequest);
			setFundingGridDataInSession(aoRequest);
			loModelAndView = new ModelAndView(HHSConstants.BMC_NEWFY_CONFIG_TASK_PATH);
		}
		else if (lsActionReqParam.equalsIgnoreCase(HHSConstants.TASK_AMENDMENT_CONFIGURATION))
		{
			lsAmendmentContractId = lsContractId;
			lsContractId = fetchBaseContractId(lsContractId);
			loTaskDetailsBean.setBaseContractId(lsContractId);
			fetchContractConfigurationDetails(aoRequest, lsContractId, HHSConstants.TWO, true, lsAmendmentContractId,
					false, false, null);
			setAccountGridDataInSession(aoRequest);
			setAmendmentAccountGridDataInSession(aoRequest);
			setFundingGridDataInSession(aoRequest);
			setAmendmentFundingGridDataInSession(aoRequest);
			loModelAndView = new ModelAndView(HHSConstants.BMC_AMENDMENT_CONFIGURATION_JSP_PATH);
		}
		else if (lsActionReqParam.equalsIgnoreCase(HHSConstants.TASK_AMENDMENT_COF))
		{
			lsAmendmentContractId = lsContractId;
			lsContractId = fetchBaseContractId(lsContractId);
			loTaskDetailsBean.setBaseContractId(lsContractId);
			fetchContractConfigurationDetails(aoRequest, lsContractId, HHSConstants.TWO, true, lsAmendmentContractId,
					false, false, null);
			setAccountGridDataInSession(aoRequest);
			setAmendmentAccountGridDataInSession(aoRequest);
			setFundingGridDataInSession(aoRequest);
			setAmendmentFundingGridDataInSession(aoRequest);
			loModelAndView = new ModelAndView(HHSConstants.BMC_AMENDMENT_CERT_FUNDS_JSP_PATH);
		}
		return loModelAndView;
	}

	/**
	 * This method check if contract start and end date is set for open ended
	 * rfp.
	 * @param lsContractId contract id as input
	 * @return loIsOpenEndedRfpStartEndDateNotSet check if contract start and
	 *         end date is set.
	 * @throws ApplicationException Exception in case a query fails
	 */
	private Boolean openEndedRfpStartEndDateNotSet(String lsContractId) throws ApplicationException
	{
		Channel loChannel = new Channel();
		loChannel.setData(HHSConstants.CONTRACT_ID_KEY, lsContractId);
		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.IS_OPEN_ENDED_RFP_START_END_DATE_NOT_SET);
		Boolean loIsOpenEndedRfpStartEndDateNotSet = (Boolean) loChannel
				.getData(HHSConstants.IS_OPEN_ENDED_RFP_START_END_DATE_NOT_SET);
		return loIsOpenEndedRfpStartEndDateNotSet;
	}

	/*
	 * Changed method - By: Siddharth Bhola Reason: Enhancement id: 5653 changed
	 * date parameters in method call setContractDatesInSession and set screen
	 * on load values in task detail bean.
	 * 
	 * 
	 * This method is called if Procurement Certification of funds task is
	 * launched <li>1. Get the procurement details.</li> <li>2. Get contract
	 * fiscal years abd set required data in locbgridbean.</li> <li>3. Set
	 * account and funding grid properties in session.</li> <li>4. Redirect to
	 * procurement certification of funds jsp.</li> <li>the transaction used:
	 * fetchProcurementCoF</li>
	 * 
	 * @param aoRequest - RenderRequest object
	 * 
	 * @param aoTaskDetailsBean - TaskDetailsBean object
	 * 
	 * @return loModelAndView - ModelAndView object
	 * 
	 * @throws ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public ModelAndView ifProcurementFundTask(RenderRequest aoRequest, TaskDetailsBean aoTaskDetailsBean)
			throws ApplicationException
	{
		ModelAndView loModelAndView = null;
		Integer loFiscalStartYr = 0;
		Map<String, Object> loFiscalYrMap = null;
		String lsProcurementId = aoTaskDetailsBean.getProcurementId();
		String lsLevel = aoTaskDetailsBean.getLevel();
		Channel loChannelObj = new Channel();
		loChannelObj.setData(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
		HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.FETCH_PROC_COF);
		ProcurementCOF loProcurementCOF = (ProcurementCOF) loChannelObj.getData(HHSConstants.PROC_COF_DETAILS);

		// latest changed dates are passed, instead of original dates when
		// procurement was published first time, as part of enhancement 5653
		setContractDatesInSession(aoRequest.getPortletSession(), loProcurementCOF.getContractStartDate(),
				loProcurementCOF.getContractEndDate());

		// build 2.6.0, defect id 5653
		aoTaskDetailsBean.setOldProcValTask(loProcurementCOF.getProcurementValue());
		aoTaskDetailsBean.setOldContStartDateTask(loProcurementCOF.getContractStartDate());
		aoTaskDetailsBean.setOldContEndDateTask(loProcurementCOF.getContractEndDate());

		loFiscalYrMap = getContractFiscalYears(aoRequest);
		loFiscalStartYr = (Integer) loFiscalYrMap.get(HHSConstants.LI_START_YEAR);
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setProcurementID(lsProcurementId);
		loCBGridBean.setFiscalYearID(String.valueOf(loFiscalStartYr));
		loCBGridBean.setNoOfyears((Integer) loFiscalYrMap.get(HHSConstants.LI_FYCOUNT));
		loCBGridBean.setCreatedByUserId(String.valueOf(aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE)));
		loCBGridBean.setModifiedByUserId(String.valueOf(aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE)));
		String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		if (ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(lsUserOrgType))
		{
			loCBGridBean.setModifyByProvider(lsUserId);
		}
		else
		{
			loCBGridBean.setModifyByAgency(lsUserId);
		}
		PortletSessionHandler.setAttribute(loCBGridBean, aoRequest, HHSConstants.CBGRIDBEAN_IN_SESSION);
		setAccountGridDataInSession(aoRequest);
		setFundingGridDataInSession(aoRequest);
		if (lsLevel.equalsIgnoreCase(HHSConstants.ONE))
		{
			aoRequest.setAttribute(HHSConstants.IS_READ_ONLY, HHSConstants.FALSE);
		}
		else
		{
			aoRequest.setAttribute(HHSConstants.IS_READ_ONLY, HHSConstants.TRUE);
		}
		loModelAndView = new ModelAndView(HHSConstants.BMC_PROC_CERT_FUND_TASK_JSP_PATH, HHSConstants.PROC_COF,
				loProcurementCOF);
		return loModelAndView;
	}

	/**
	 * This method will handle actions from procurement roadmap screen
	 * @param aoProcurementParam a Procurement bean object
	 * @param aoResult a Binding Result object
	 * @param aoRequest an Action Request object
	 * @param aoResponse an Action Response object
	 * @param aoModel - Model type object
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@ActionMapping(params = "taskcontrollerAction=save")
	protected void actionProcurementRoadmap(@ModelAttribute(HHSConstants.PROCUREMENT) Procurement aoProcurementParam,
			BindingResult aoResult, ActionRequest aoRequest, ActionResponse aoResponse, Model aoModel)
			throws ApplicationException
	{
		aoResponse.setRenderParameter(HHSConstants.BMC_RENDER_ATTR, HHSConstants.BMC_PROGRAM_LIST);
	}

	/**
	 * Sets procurement certification of funds details in request.
	 * <ul>
	 * <li>1. Set procurement Id in Channel.</li>
	 * <li>2. Execute transaction <b>fetchProcurementCoF</b></li>
	 * <li>3. Set Output Bean <b>ProcurementCOF</b> in request</li>
	 * </ul>
	 * @param aoRequest render request
	 * @param asProcurementId procurement Id
	 * @throws ApplicationException - ApplicationException object
	 */
	protected void fetchProcurementCOFDetails(RenderRequest aoRequest, String asProcurementId)
			throws ApplicationException
	{
		ProcurementCOF loPprocureCof = null;
		Channel loChannel = new Channel();
		loChannel.setData(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_PROC_DETAILS_FINANCIALS);
		loPprocureCof = (ProcurementCOF) loChannel.getData(HHSConstants.PROC_COF_DETAILS);
		aoRequest.setAttribute(HHSConstants.PROC_COF_DETAILS, loPprocureCof);
	}

	/**
	 * This method will be called on page load of New FY configuration screen.
	 * This method will execute fetchContractConfigurationDetails method which
	 * will fetch contract details.
	 * @param aoRequest RenderRequest object
	 * @param asContractId contract id on the basis of which details will be
	 *            fetched from db
	 * @throws ApplicationException - ApplicationException object
	 */

	protected void fetchNewFYConfigurationDetails(RenderRequest aoRequest, String asContractId)
			throws ApplicationException
	{
		Channel loChannel = new Channel();
		loChannel.setData(HHSConstants.CONTRACT_ID_KEY, asContractId);
		loChannel.setData(HHSConstants.FISCAL_YEAR_ID_KEY,
				aoRequest.getPortletSession().getAttribute(HHSConstants.BASE_LS_CONFIGURABLE_FISCAL_YEAR));
		fetchContractConfigurationDetails(aoRequest, asContractId, null, false, null, false, false, null);
		String lsBudgetAmount = BmcControllerUtil.fetchNewFYBudgetAmount(loChannel);
		String lsNonEditColumnString = getNonEditColumnName(aoRequest);
		aoRequest.setAttribute(HHSConstants.NON_EDIT_COLNAME, lsNonEditColumnString);
		aoRequest.setAttribute(HHSConstants.NEW_FY_CONFIGURABLE_YEAR_AMOUNT, lsBudgetAmount);
		aoRequest.setAttribute(HHSConstants.BASE_LS_CONFIGURABLE_FISCAL_YEAR, aoRequest.getPortletSession()
				.getAttribute(HHSConstants.BASE_LS_CONFIGURABLE_FISCAL_YEAR));
	}

	/**
	 * This method returns the string defining the non-edit columns at the time
	 * of new row addition in CoA grid.
	 * 
	 * @param aoRequest - the request parameter
	 * @return lsNonEditColumnString - the final string defining the non-edit
	 *         columns
	 * @throws ApplicationException - the application exception object
	 */
	@SuppressWarnings("rawtypes")
	private String getNonEditColumnName(RenderRequest aoRequest) throws ApplicationException
	{
		Map loContractMap = getContractFiscalYears(aoRequest);
		StringBuffer loNonEditColumnString = new StringBuffer();
		int liYearCount = (Integer) loContractMap.get(HHSConstants.LI_FYCOUNT);
		int liStartFYCounter = (Integer) loContractMap.get(HHSConstants.LI_START_FY_COUNTER);
		Integer liCurrentFY = Integer.parseInt((String) aoRequest.getPortletSession().getAttribute(
				HHSConstants.BASE_LS_CONFIGURABLE_FISCAL_YEAR));
		int liCurrentFYCounter = Integer.parseInt(liCurrentFY.toString().substring(2));
		for (int liCounter = 1, liFYCounter = liStartFYCounter; liCounter <= liYearCount; liFYCounter++, liCounter++)
		{
			if (liFYCounter < liCurrentFYCounter)
			{
				loNonEditColumnString.append(HHSConstants.SMALL_FY);
				loNonEditColumnString.append(HHSUtil.getFiscalYearCounter(liFYCounter));
				loNonEditColumnString.append(HHSConstants.COMMA);
			}
		}
		loNonEditColumnString.append(HHSConstants.TOTAL_COLUMN_APPENDER);
		return loNonEditColumnString.toString();
	}

	/**
	 * This is private method used to fetch base contract id for the input
	 * contract.
	 * @param asContractId Contract id whose base contract id is required
	 * @return String lsBaseContractId
	 * @throws ApplicationException ApplicationException object
	 */
	private String fetchBaseContractId(String asContractId) throws Exception
	{
		String lsBaseContractId = null;
		Channel loChannel = new Channel();
		loChannel.setData("asContractId", asContractId);
		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.BMC_FETCH_BASE_CONTRACT_ID);
		lsBaseContractId = (String) loChannel.getData("asBaseContractId");

		return lsBaseContractId;
	}

	/**
	 * This method will be called on page load of contract configuration screen.
	 * This method will execute fetchContractConfigurationDetails transaction
	 * which will fetch contract details.
	 * @param aoRequest RenderRequest object
	 * @param asContractId contract id on the basis of which details will be
	 *            fetched from db
	 * @return liFiscalStartYr
	 * @throws ApplicationException - ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	protected int fetchContractConfigurationDetails(RenderRequest aoRequest, String asContractId,
			String asContractTypeId, boolean abAmendment, String asAmendmentContractId, boolean abUpdate,
			boolean lbIsOpenEndedRfpStartEndDateNotSet, String asCompPoolTitle) throws ApplicationException
	{
		ProcurementCOF loPprocureCof = null;
		Channel loChannel = new Channel();
		Integer liFiscalStartYr = HHSConstants.INT_ZERO;
		loChannel.setData(HHSConstants.CONTRACT_ID_KEY, asContractId);
		loChannel.setData(HHSConstants.FISCAL_YEAR_ID_KEY,
				aoRequest.getPortletSession().getAttribute(HHSConstants.BASE_LS_CONFIGURABLE_FISCAL_YEAR));
		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.BMC_FETCH_CONTRACT_CONFIGURATION_DETAILS);
		loPprocureCof = (ProcurementCOF) loChannel.getData(HHSConstants.BMC_PROCUREMENT_CON_DETAILS);

		setContractDatesInSession(aoRequest.getPortletSession(), loPprocureCof.getContractStartDate(),
				loPprocureCof.getContractEndDate());
		if (abAmendment)
		{
			populateAmendmentConfigDetails(aoRequest, asContractId, asContractTypeId, asAmendmentContractId,
					loPprocureCof);
			//fix for defect : not able to save on Funding Source Allocation - Amendment(Optional)
			//when contract end date is changed while amending contract. 
			aoRequest.setAttribute(HHSConstants.IS_AMENDMENT_FLOW, HHSConstants.TRUE);
		}

		if (abUpdate)
		{
			populateUpdateConfigDetails(aoRequest, asContractId, asContractTypeId, loPprocureCof);
		}
		CBGridBean loGridBean = new CBGridBean();

		if (!lbIsOpenEndedRfpStartEndDateNotSet)
		{
			Map<String, Object> loFiscalYrMap = getContractFiscalYears(aoRequest);
			aoRequest.setAttribute(HHSConstants.IS_AMENDMENT_FLOW, null);
			liFiscalStartYr = (Integer) loFiscalYrMap.get(HHSConstants.LI_START_YEAR);
			loGridBean.setNoOfyears((Integer) loFiscalYrMap.get(HHSConstants.LI_FYCOUNT));
			loGridBean.setFiscalYearID(String.valueOf(liFiscalStartYr));
		}

		loGridBean.setContractID(asContractId);
		loGridBean.setAmendmentContractID(asAmendmentContractId);
		loGridBean.setProcurementID(loPprocureCof.getProcurementId());
		loGridBean.setAmendmentType(loPprocureCof.getAmendmentType());
		if (asContractTypeId != null)
		{
			loGridBean.setContractTypeId(asContractTypeId);
		}
		loGridBean.setCreatedByUserId(String.valueOf(aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE)));
		loGridBean.setModifiedByUserId(String.valueOf(aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE)));
		String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		if (ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(lsUserOrgType))
		{
			loGridBean.setModifyByProvider(lsUserId);
		}
		else
		{
			loGridBean.setModifyByAgency(lsUserId);
		}
		PortletSessionHandler.setAttribute(loGridBean, aoRequest, HHSConstants.CBGRIDBEAN_IN_SESSION);
		aoRequest.getPortletSession().setAttribute(HHSConstants.START_FISCAL_YEAR, liFiscalStartYr,
				PortletSession.APPLICATION_SCOPE);
		aoRequest.getPortletSession().setAttribute(HHSConstants.NUMBER_OF_YEARS, loGridBean.getNoOfyears(),
				PortletSession.APPLICATION_SCOPE);
		Calendar loCal = Calendar.getInstance();
		aoRequest.setAttribute(HHSConstants.BMC_CURRENT_FISCAL_YEAR, loCal.get(Calendar.YEAR));
		aoRequest.setAttribute(HHSConstants.BMC_CONTRACT_FIRST_YEAR, liFiscalStartYr);
		loPprocureCof.setCompPoolTitle(asCompPoolTitle);
		aoRequest.getPortletSession().setAttribute(HHSConstants.BMC_CONTRACT_DATA_PARAM, loPprocureCof,
				PortletSession.APPLICATION_SCOPE);
		return liFiscalStartYr;
	}

	/**
	 * This method fetch Contract update related details like contract start
	 * date,contract end date etc by calling transaction
	 * fetchUpdateConfigurationDetails to show details Contract configuration
	 * update task screen. <li>This method was added in R4</li>
	 * @param aoRequest request object
	 * @param asContractId contract id
	 * @param asContractTypeId contract type id
	 * @param loPprocureCof ProcurementCOF bean object
	 * @throws ApplicationException
	 */
	private void populateUpdateConfigDetails(RenderRequest aoRequest, String asContractId, String asContractTypeId,
			ProcurementCOF loPprocureCof) throws ApplicationException
	{
		Channel loChannel1 = new Channel();
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.CONTRACT_ID, asContractId);
		loHashMap.put(HHSConstants.CONTRACT_TYPE_ID, asContractTypeId);
		loChannel1.setData(HHSConstants.AO_HASH_MAP, loHashMap);

		HHSTransactionManager.executeTransaction(loChannel1, HHSConstants.FETCH_UPDATE_CONFIGURATION_DETAILS);

		ProcurementCOF loProcurementCOFBean = (ProcurementCOF) loChannel1
				.getData(HHSConstants.BMC_PROCUREMENT_CON_DETAILS);
		if (null != loProcurementCOFBean)
		{
			loPprocureCof.setAmendmentValue(loProcurementCOFBean.getAmendmentValue());
			loPprocureCof.setAmendmentType(loProcurementCOFBean.getAmendmentType());

			loPprocureCof.setAgencyCode(loProcurementCOFBean.getAgencyCode());
			loPprocureCof.setAgencyName(loProcurementCOFBean.getAgencyName());
			loPprocureCof.setProviderName(loProcurementCOFBean.getProviderName());
			loPprocureCof.setUpdatedContractStartDate(loProcurementCOFBean.getContractStartDate());
			loPprocureCof.setUpdatedContractEndDate(loProcurementCOFBean.getContractEndDate());
			aoRequest.getPortletSession().setAttribute(HHSConstants.TASK_CONTRACT_ID,
					loProcurementCOFBean.getUpdatedContractId(), PortletSession.APPLICATION_SCOPE);
			setContractAmendmentDatesInSession(aoRequest.getPortletSession(),
					loProcurementCOFBean.getContractStartDate(), loProcurementCOFBean.getContractEndDate());
		}
	}

	/**
	 * This method fetch Amendment related details like Amendment value,
	 * Amendment type count etc by calling transaction
	 * fetchContractAmendmentConfigurationDetails to show details on Amendment
	 * configuration and Amendment COF task screens. <li>This method was added
	 * in R4</li>
	 * @param aoRequest Request Object
	 * @param asContractId Contract Id
	 * @param asContractTypeId Contract type Id
	 * @param asAmendmentContractId Amendment Contract Id
	 * @param loPprocureCof ProcurementCOF bean object
	 * @throws ApplicationException
	 */
	private void populateAmendmentConfigDetails(RenderRequest aoRequest, String asContractId, String asContractTypeId,
			String asAmendmentContractId, ProcurementCOF loPprocureCof) throws ApplicationException
	{
		Channel loChannel1 = new Channel();
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.CONTRACT_ID, asContractId);
		loHashMap.put(HHSConstants.CONTRACT_TYPE_ID, asContractTypeId);
		loHashMap.put(HHSConstants.AMEND_CONTRACT_ID, asAmendmentContractId);
		loChannel1.setData(HHSConstants.AO_HASH_MAP, loHashMap);

		HHSTransactionManager.executeTransaction(loChannel1,
				HHSConstants.BMC_FETCH_CONTRACT_AMENDMENT_CONFIGURATION_DETAILS);
		ProcurementCOF loProcurementCOFBean = (ProcurementCOF) loChannel1
				.getData(HHSConstants.BMC_PROCUREMENT_CON_DETAILS);
		if (null != loProcurementCOFBean)
		{
			loPprocureCof.setAmendmentValue(loProcurementCOFBean.getAmendmentValue());
			loPprocureCof.setPositiveAmendmentValue(loProcurementCOFBean.getPositiveAmendmentValue());
			loPprocureCof.setNegativeAmendmentValue(loProcurementCOFBean.getNegativeAmendmentValue());
			loPprocureCof.setAgencyCode(loProcurementCOFBean.getAgencyCode());
			loPprocureCof.setAgencyName(loProcurementCOFBean.getAgencyName());
			loPprocureCof.setProviderName(loProcurementCOFBean.getProviderName());
			loPprocureCof.setUpdatedContractStartDate(loProcurementCOFBean.getContractStartDate());
			loPprocureCof.setUpdatedContractEndDate(loProcurementCOFBean.getContractEndDate());
			loPprocureCof.setAmendmentType(loProcurementCOFBean.getAmendmentType());
			loPprocureCof.setAmendmentCount(loProcurementCOFBean.getAmendmentCount());

			loPprocureCof.setAmendmentStartDate(loProcurementCOFBean.getAmendmentStartDate());
			loPprocureCof.setAmendmentEndDate(loProcurementCOFBean.getAmendmentEndDate());
			loPprocureCof.setAmendmentEpin(loProcurementCOFBean.getAmendmentEpin());

			loPprocureCof.setAmendmentTitle(loProcurementCOFBean.getAmendmentTitle());
			loPprocureCof.setProcurementTitle(loProcurementCOFBean.getProcurementTitle());

			aoRequest.getPortletSession().setAttribute(HHSConstants.TASK_CONTRACT_ID,
					loProcurementCOFBean.getUpdatedContractId(), PortletSession.APPLICATION_SCOPE);
			setContractAmendmentDatesInSession(aoRequest.getPortletSession(),
					loProcurementCOFBean.getContractStartDate(), loProcurementCOFBean.getContractEndDate());
		}
	}

	/**
	 * This method will be called on page load of contract budget screen.
	 * <ul>
	 * <li>1.Execute fetchContractBudgetDetails transaction.</li>
	 * <li>2.Fetches list of sub budgets.</li>
	 * <li>This method was updated in R4</li>
	 * @param aoResourceRequest ResourceRequest object
	 * @param aoResourceResponse ResourceResponse object
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@ResourceMapping("fetchContractBudgetDetails")
	public void fetchContractBudgetDetails(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{   
		PrintWriter loOut = null;
		try
		{   LOG_OBJECT.Info("====fetchContractBudgetDetails==="); 
			loOut = aoResourceResponse.getWriter();
			String lsGridLabel = aoResourceRequest.getParameter(HHSConstants.GRID_LABEL);
			String lsTransactionName = aoResourceRequest.getParameter(HHSConstants.TRANSACTION_NAME);
			String lsClass = aoResourceRequest.getParameter(HHSConstants.BEAN_NAME);
			StringBuffer loPropertyName = new StringBuffer(lsTransactionName);
			loPropertyName.append(HHSConstants.FETCH);
			lsTransactionName = PropertyLoader.getProperty(HHSConstants.PROPERTIES_GRIDTRANSACTION,
					loPropertyName.toString());
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
			CBGridBean loCBGridBean = (CBGridBean) PortletSessionHandler.getAttribute(aoResourceRequest, true,
					HHSConstants.CBGRIDBEAN_IN_SESSION);
			Channel loChannelObj = new Channel();
			loChannelObj.setData(HHSConstants.CB_GRID_BEAN_OBJ, loCBGridBean);
			Integer loConfigurationFiscalYear = (Integer) HHSUtil.getFYForContractBudgetConfig(Integer
					.parseInt(loCBGridBean.getFiscalYearID()));

			if (aoResourceRequest.getPortletSession().getAttribute(HHSConstants.BASE_LS_CONFIGURABLE_FISCAL_YEAR) != null)
			{
				loConfigurationFiscalYear = Integer.parseInt(((String) aoResourceRequest.getPortletSession()
						.getAttribute(HHSConstants.BASE_LS_CONFIGURABLE_FISCAL_YEAR)));
			}
			loChannelObj.setData(HHSConstants.CONTRACT_ID_KEY, loCBGridBean.getContractID());
			loChannelObj.setData(HHSConstants.FISCAL_YEAR_ID_KEY, loConfigurationFiscalYear.toString());
			// Added for NewFYConfiguration Contract Budget Tab
			if (null != aoResourceRequest.getPortletSession().getAttribute(
					HHSConstants.BASE_LS_CONFIGURABLE_FISCAL_YEAR))
			{
				loChannelObj.setData(HHSConstants.FISCAL_YEAR_ID_KEY, (String) aoResourceRequest.getPortletSession()
						.getAttribute(HHSConstants.BASE_LS_CONFIGURABLE_FISCAL_YEAR));
			}
			HHSTransactionManager.executeTransaction(loChannelObj, lsTransactionName);
			List loReturnedGridList = (List) loChannelObj.getData(HHSConstants.AO_RETURNED_GRID_LIST);
			/* [Start] R9.4.0 qc_9634 -- Vuln 1: CWE 470 - Use of Externally Controlled Input to Select Classes or Code ('Unsafe Reflection')   */
			lsClass = HHSUtil.checkClassAccessControl(lsClass); // throws ApplicationException if not valid class
			/* [End] R9.4.0 qc_9634 -- Vuln 1: CWE 470 - Use of Externally Controlled Input to Select Classes or Code ('Unsafe Reflection')   */
			Class loClass = Class.forName(lsClass);
			Object loBeanObj = loClass.newInstance();
			StringBuffer loBuffer = HHSUtil.populateSubGridRows(loBeanObj, loReturnedGridList, lsRowsPerPage, lsPage,
					lsErrorMsg, lsGridLabel);
			LOG_OBJECT.Debug("json for fetchContractBudgetDetails: userid:: "
					+ (String) aoResourceRequest.getPortletSession().getAttribute(
							ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE) + "\n json:: "
					+ loBuffer.toString());
			loOut.print(loBuffer.toString().replaceAll("\\\\'", "'"));
		}
		catch (ApplicationException aoExe)
		{
			LOG_OBJECT.Error("ApplicationException occured in fetchContractBudgetDetails "
					+ "while fetching data from database ", aoExe);
		}
		catch (Exception aoExe)
		{
			LOG_OBJECT.Error("Exception occured in fetchContractBudgetDetails while fetching data from database ",
					aoExe);
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

	/** This method will be called on page load of contract certification of
	 * fund task screen. This method will execute fetchContractCofTaskDetails
	 * transaction which will fetch contract related details.
	 * 
	 * @param aoRequest RenderRequest object
	 * 
	 * @param asContractId contract id on the basis of which details will be
	 * fetched from db
	 * 
	 * @throws ApplicationException Exception thrown in case of any error
	 */
	@SuppressWarnings("unchecked")
	private void fetchContractCofTaskDetails(RenderRequest aoRequest, String asContractId, String asCompPoolTitle)
			throws ApplicationException
	{   LOG_OBJECT.Info("====fetchContractCofTaskDetails===");
		ProcurementCOF loPprocureCof = null;
		Channel loChannel = new Channel();
		loChannel.setData(HHSConstants.CONTRACT_ID_KEY, asContractId);
		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.BMC_FETCH_CONTRACT_COF_TASK_DETAILS);
		loPprocureCof = (ProcurementCOF) loChannel.getData(HHSConstants.BMC_CONTRACT_COF_TASK_DETAILS);
		setContractDatesInSession(aoRequest.getPortletSession(), loPprocureCof.getContractStartDate(),
				loPprocureCof.getContractEndDate());
		Map<String, Object> loFiscalYrMap = getContractFiscalYears(aoRequest);
		int liFiscalStartYr = (Integer) loFiscalYrMap.get(HHSConstants.LI_START_YEAR);
		CBGridBean loGridBean = new CBGridBean();
		loGridBean.setContractID(asContractId);
		loGridBean.setFiscalYearID(String.valueOf(liFiscalStartYr));
		String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		if (ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(lsUserOrgType))
		{
			loGridBean.setModifyByProvider(lsUserId);
		}
		else
		{
			loGridBean.setModifyByAgency(lsUserId);
		}
		PortletSessionHandler.setAttribute(loGridBean, aoRequest, HHSConstants.CBGRIDBEAN_IN_SESSION);
		setAccountGridDataInSession(aoRequest);
		loPprocureCof.setCompPoolTitle(asCompPoolTitle);
		aoRequest.setAttribute(HHSConstants.AO_PROCUREMENTCOFBEAN, loPprocureCof);
	}

	/**
	 * This method perform actions on database based on operation performed on
	 * Subgrid for Sub Budgets (Budget Configuration)
	 * <ul>
	 * <li>If add operation perform on the grid insert row into database</li>
	 * <li>If delete operation perform on the grid delete row from database</li>
	 * <li>If edit operation perform on the grid update row into database</li>
	 * </ul>
	 * @param aoResourceRequest to get screen parameters
	 */
	@ResourceMapping("gridOperationForBudgetConfig")
	public void gridOperationForBudgetConfig(ResourceRequest aoResourceRequest)
	{   LOG_OBJECT.Info("====gridOperationForBudgetConfig===");
		String lsOperation = aoResourceRequest.getParameter(HHSConstants.GRID_OPERATION);
		String lsId = aoResourceRequest.getParameter(HHSConstants.ID);
		if (lsOperation != null && HHSConstants.OPERATION_ADD.equalsIgnoreCase(lsOperation)
				&& !lsId.equalsIgnoreCase(HHSConstants.NEW_ROW_IDENTIFIER))
		{
			lsOperation = HHSConstants.OPERATION_EDIT;
		}
		String lsTransactionName = aoResourceRequest.getParameter(HHSConstants.TRANSACTION_NAME);
		StringBuffer loPropertyName = new StringBuffer(lsTransactionName);
		if (null != lsOperation)
		{
			loPropertyName.append(lsOperation.substring(0, 1).toUpperCase()
					+ lsOperation.substring(1, lsOperation.length()));
		}
		ContractBudgetBean loBeanObj = new ContractBudgetBean();
		Channel loChannelObj = new Channel();
		try
		{
			lsTransactionName = PropertyLoader.getProperty(HHSConstants.PROPERTIES_GRIDTRANSACTION,
					loPropertyName.toString());
			if (lsOperation != null && HHSConstants.OPERATION_EDIT.equalsIgnoreCase(lsOperation)
					&& aoResourceRequest.getParameter(HHSConstants.PAGE) != null)
			{
				PortletSessionHandler.setAttribute(aoResourceRequest.getParameter(HHSConstants.PAGE),
						aoResourceRequest, HHSConstants.PAGINATION);
			}
			populateBeanFromRequest(aoResourceRequest, loBeanObj);
			CBGridBean loCBGridBean = (CBGridBean) PortletSessionHandler.getAttribute(aoResourceRequest, true,
					HHSConstants.CBGRIDBEAN_IN_SESSION);
			Integer loConfigurationFiscalYear = (Integer) HHSUtil.getFYForContractBudgetConfig(Integer
					.parseInt(loCBGridBean.getFiscalYearID()));

			// Added for S393 New Year Fiscal Configuration to get the current
			// configurable Fiscal Year
			if (aoResourceRequest.getPortletSession().getAttribute(HHSConstants.BASE_LS_CONFIGURABLE_FISCAL_YEAR) != null)
			{
				loConfigurationFiscalYear = Integer.parseInt(((String) aoResourceRequest.getPortletSession()
						.getAttribute(HHSConstants.BASE_LS_CONFIGURABLE_FISCAL_YEAR)));
			}
			ContractBudgetBean loContractBudgetBean = (ContractBudgetBean) PortletSessionHandler.getAttribute(
					aoResourceRequest, true, HHSConstants.BMC_CONTRACT_BUDGET_BEAN);
			loBeanObj.setBudgetId(loContractBudgetBean.getBudgetId());
			loBeanObj.setBudgetTypeId(loContractBudgetBean.getBudgetTypeId());
			loBeanObj.setBudgetfiscalYear(loConfigurationFiscalYear.toString());
			loBeanObj.setContractValue(loContractBudgetBean.getContractValue());
			loBeanObj.setTotalbudgetAmount(loContractBudgetBean.getTotalbudgetAmount());
			loBeanObj.setCreatedByUserId(String.valueOf(aoResourceRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE)));
			BmcControllerUtil.executeGridTransactionForBudgetConfig(lsOperation, lsTransactionName, loChannelObj,
					loBeanObj);
		}
		catch (ApplicationException aoAppExe)
		{
			//start release 3.14.0
			String lsGridErrorMessage = (String) aoAppExe.getContextData().get(HHSConstants.GRID_ERROR_MESSAGE);
			ApplicationException loAppEx = (ApplicationException) aoAppExe.getRootCause();
			if (null != loAppEx && (lsGridErrorMessage== null || lsGridErrorMessage.isEmpty()))
			{
				lsGridErrorMessage = (String) loAppEx.getContextData().get(HHSConstants.GRID_ERROR_MESSAGE);
			}
			//end release 3.14.0
			PortletSessionHandler.setAttribute(lsGridErrorMessage, aoResourceRequest, HHSConstants.GRID_ERROR);
			LOG_OBJECT
					.Error("ApplicationException occured in gridOperation method while performing operation on grid  "
							+ aoAppExe);
		}
		catch (Exception aoExe)
		{
			String lsGridErrorMessage = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			PortletSessionHandler.setAttribute(lsGridErrorMessage, aoResourceRequest, HHSConstants.GRID_ERROR);
			LOG_OBJECT.Error("Error occured in gridOperation method while performing operation on grid  ", aoExe);
		}
	}

	/**
	 * This method is called on click of save button on contract finance jsp.
	 * This save button is visible if there is open ended rfp and contract start
	 * and end date is null. <li>the transaction used:
	 * updateContractStartEndDateForOpenEndedRfp</li>
	 * @param aoActionRequest action request as input
	 * @param aoActionResponse action response as input
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@ActionMapping(params = "submit_action=updateContractStartEndDateForOpenEndedRfp")
	public void updateContractStartEndDateForOpenEndedRfp(ActionRequest aoActionRequest, ActionResponse aoActionResponse)
			throws ApplicationException
	{
		Channel loChannel = new Channel();
		HashMap aoContractDetails = new HashMap();
		String lsContractStartDate =(String) aoActionRequest.getParameter(HHSConstants.CONTRACT_START_DATE);
		String lsContractEndDate =(String) aoActionRequest.getParameter(HHSConstants.CONTRACT_END_DATE);
		aoContractDetails.put(HHSConstants.CONTRACT_START_DATE,
				lsContractStartDate);
		aoContractDetails.put(HHSConstants.CONTRACT_END_DATE,
				(String) aoActionRequest.getParameter(HHSConstants.CONTRACT_END_DATE));
		aoContractDetails.put(HHSConstants.CONTRACT_ID_WORKFLOW,
				(String) aoActionRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW));
		loChannel.setData(HHSConstants.LO_CONTRACT_DETAILS, aoContractDetails);
		try
		{
			boolean lbfiscalYearSpanFlag = HHSUtil.checkContractFiscalYearsSpan(lsContractStartDate ,
					lsContractEndDate);
			if (!lbfiscalYearSpanFlag)
			{
			HHSTransactionManager.executeTransaction(loChannel,
					HHSConstants.UPDATE_CONTRACT_START_END_DATE_FOR_OPEN_ENDED_RFP);
			}
			else
			{
				aoActionResponse.setRenderParameter(HHSConstants.FINANCIALS_MESSAGE,PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.ERROR_CONTRACT_TERM_EXCEED));
			}
			aoActionResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.TASK_CONTRACT_CONFIGURATION);
			aoActionResponse.setRenderParameter(HHSConstants.WORKFLOW_ID,
					(String) aoActionRequest.getParameter(HHSConstants.WORKFLOW_ID));
		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("ApplicationException occured in BMC Controller", aoAppExp);
			aoActionResponse.setRenderParameter(HHSConstants.CBL_MESSAGE, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST);
			aoActionResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE);
		}

	}

	/**
	 * This method is used to populate the Tab for Budget Configuration
	 * <ul>
	 * <li>This method also sets the Current Configurable Budget Information in
	 * session</li>
	 * <li>This method is restructured to call a single transaction</li>
	 * <ul>
	 * @param aoResourceRequest Resource Request Object
	 * @param aoResourceResponse Resource Response Object
	 * @return loModelAndView
	 * @throws Exception
	 */
	@ResourceMapping("contractBudgetPage")
	public ModelAndView getContractBudgetPage(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{   LOG_OBJECT.Info("====getContractBudgetPage===");
		ModelAndView loModelAndView = null;
		try
		{
			Channel loChannelObj = new Channel();
			CBGridBean loCBGridBean = (CBGridBean) PortletSessionHandler.getAttribute(aoResourceRequest, true,
					HHSConstants.CBGRIDBEAN_IN_SESSION);

			Integer loConfigurationFiscalYear = (Integer) HHSUtil.getFYForContractBudgetConfig(Integer
					.parseInt(loCBGridBean.getFiscalYearID()));
			loCBGridBean.setBudgetStartYear(loConfigurationFiscalYear.toString());

			loChannelObj.setData(HHSConstants.CB_GRID_BEAN_OBJ, loCBGridBean);
			HHSTransactionManager.executeTransaction(loChannelObj,
					HHSConstants.BMC_CONTRACT_BUDGET_PROCESSING);
			ContractBudgetBean loContractBudgetBean = (ContractBudgetBean) loChannelObj.getData(HHSConstants.AO_CONTRACT_BUDGET_BEAN);
			// Start: Added in R7 to Read-only Contract COF screen
			aoResourceRequest.setAttribute(HHSConstants.SCREEN_READ_ONLY, aoResourceRequest.getParameter(HHSConstants.SCREEN_READ_ONLY));
			// end: Added in R7 to Read-only Contract COF screen
			aoResourceRequest.setAttribute(HHSConstants.BMC_ADD_ENABLED, HHSConstants.TRUE);

			PortletSessionHandler.setAttribute(loContractBudgetBean, aoResourceRequest,
					HHSConstants.BMC_CONTRACT_BUDGET_BEAN);

			loCBGridBean.setContractBudgetID(loContractBudgetBean.getBudgetId());
			loCBGridBean.setBudgetAmount(loContractBudgetBean.getTotalbudgetAmount());			
			PortletSessionHandler.setAttribute(loCBGridBean, aoResourceRequest, HHSConstants.CBGRIDBEAN_IN_SESSION);

			// Set attributes to be placed in JSP
			aoResourceRequest.setAttribute(HHSConstants.BMC_FY_BUDGET_PLANNED_AMOUNT, loContractBudgetBean.getTotalbudgetAmount());
			// R4 Fetch EntryType Details
			aoResourceRequest.setAttribute(HHSConstants.ENTRY_TYPE_ID, HHSUtil.getEntryTypeDetail(
					loCBGridBean.getContractID(), loCBGridBean.getContractBudgetID(), null, null, null));
			aoResourceRequest.setAttribute(HHSConstants.BMC_CONTRACT_ID, loCBGridBean.getContractID());
			aoResourceRequest.setAttribute(HHSConstants.BMC_CONTRACT_VALUE, loContractBudgetBean.getContractValue());
			aoResourceRequest.setAttribute(HHSConstants.BMC_BUDGET_FISCAL_YEAR,
					loContractBudgetBean.getBudgetfiscalYear());
		}
		catch (Exception aoExe)
		{
			aoResourceRequest.setAttribute(HHSConstants.BMC_FY_BUDGET_PLANNED_AMOUNT, HHSConstants.STRING_ZERO);
			aoResourceRequest.setAttribute(HHSConstants.BMC_CONTRACT_VALUE, HHSConstants.STRING_ZERO);
			aoResourceRequest.setAttribute(HHSConstants.BMC_BUDGET_FISCAL_YEAR, HHSConstants.EMPTY_STRING);
			aoResourceRequest.setAttribute(HHSConstants.BMC_FY_BUDGET_PLANNED_AMOUNT, HHSConstants.STRING_ZERO);
			aoResourceRequest.setAttribute(HHSConstants.BMC_ADD_ENABLED, HHSConstants.FALSE);
			LOG_OBJECT.Error("Error occured in contractBudgetPage method while performing operation on grid  ", aoExe);
		}
		loModelAndView = new ModelAndView(HHSConstants.BMC_CONTRACTBUDGETS_JSP_PATH);
		return loModelAndView;
	}

	/**
	 * This method is used to populate the Tab for Budget Configuration
	 * <ul>
	 * <li>This method also sets the Current Configurable Budget Information in
	 * session</li>
	 * <li>This method was updated in R4</li>
	 * <li>the transaction used: insertAmendmentSubBudgetDetails</li>
	 * <ul>
	 * @param aoResourceRequest Resource Request Object
	 * @param aoResourceResponse Resource Response Object
	 * @return loModelAndView
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ResourceMapping("contractAmendmentBudgetPage")
	public ModelAndView contractAmendmentBudgetPage(ResourceRequest aoResourceRequest,
			ResourceResponse aoResourceResponse)
	{   LOG_OBJECT.Info("====contractAmendmentBudgetPage====="); //qc9680
		ModelAndView loModelAndView = null;
		try
		{
			Channel loChannelObj = new Channel();
			CBGridBean loCBGridBean = (CBGridBean) PortletSessionHandler.getAttribute(aoResourceRequest, true,
					HHSConstants.CBGRIDBEAN_IN_SESSION);
			ProcurementCOF loPprocureCof = (ProcurementCOF) aoResourceRequest.getPortletSession().getAttribute(
					HHSConstants.BMC_CONTRACT_DATA_PARAM, PortletSession.APPLICATION_SCOPE);
			String lsFyYear = aoResourceRequest.getParameter(HHSConstants.FY_YEAR);
			String lsFyArray = aoResourceRequest.getParameter(HHSConstants.TOTAL_FY_ARRAY);
			String lsContractFinancialsFyYears[] = null;
			List<String> lsBudgetYears = new ArrayList<String>();
			List loReturnedBudgetDetails = null;
			if (null != lsFyArray)
			{
				lsContractFinancialsFyYears = lsFyArray.split(",");
			}
			loChannelObj.setData(HHSConstants.CONTRACT_ID_KEY, loCBGridBean.getContractID());
			loChannelObj.setData(HHSConstants.AMENDED_CONTRACT_ID, loCBGridBean.getAmendmentContractID());
			loChannelObj.setData(HHSConstants.CONTRACT_TYPE_ID_KEY, loCBGridBean.getContractTypeId());
			
			// Start: R7 changes for Amendment Certification of funds
			aoResourceRequest.setAttribute(HHSConstants.SCREEN_READ_ONLY, aoResourceRequest.getParameter(HHSConstants.SCREEN_READ_ONLY));
			// End: R7 changes for Amendment Certification of funds
			
			aoResourceRequest.setAttribute(HHSConstants.AMENDMENT_VALUE, loPprocureCof.getAmendmentValue());
			//start release 3.14.0
			Integer loNextNewFy = BmcControllerUtil.getNextNewFYBudgetYear(loChannelObj);
			//not applicable for negative amendment
			if( loPprocureCof!=null &&  loPprocureCof.getAmendmentValue()!=null 
					//start release 3.14.1
					&& new BigDecimal(loPprocureCof.getAmendmentValue()).compareTo(BigDecimal.ZERO)< HHSConstants.INT_ZERO)
					//end release 3.14.1
			{
				loNextNewFy =null;
			}
			//end release 3.14.0
			LOG_OBJECT.Info("====lsContractFinancialsFyYears :: "+lsContractFinancialsFyYears); //qc9680
			if (null != lsContractFinancialsFyYears)
			{
				//start changes for enhancement 6601 release 3.12.0
				aoResourceRequest.setAttribute(HHSConstants.IS_AMENDMENT_FLOW, HHSConstants.TRUE);
				LOG_OBJECT.Info("====aoResourceRequest.setAttribute(HHSConstants.IS_AMENDMENT_FLOW, HHSConstants.TRUE");
				Map<String, Object> loFiscalYrMap = getContractFiscalYears(aoResourceRequest);
				aoResourceRequest.getPortletSession().setAttribute(HHSConstants.START_FISCAL_YEAR, (Integer) loFiscalYrMap.get(HHSConstants.LI_START_YEAR),
						PortletSession.APPLICATION_SCOPE);
				aoResourceRequest.getPortletSession().setAttribute(HHSConstants.NUMBER_OF_YEARS, (Integer) loFiscalYrMap.get(HHSConstants.LI_FYCOUNT),
						PortletSession.APPLICATION_SCOPE);
				//end changes for enhancement 6601 release 3.12.0
				aoResourceRequest.setAttribute(HHSConstants.SELECTED_FISCAL_YEAR, lsFyYear);
				for (String lsYear : lsContractFinancialsFyYears)
				{
					loChannelObj.setData(HHSConstants.FISCAL_YEAR_ID_KEY, lsYear);
					List<ContractBudgetBean> loBudgetDetails = BmcControllerUtil
							.getActiveApprovedBudgetDetails(loChannelObj);
					if (null != loBudgetDetails && !loBudgetDetails.isEmpty())
					{
						lsBudgetYears.add(lsYear);
					}
					//start release 3.14.0
					if(loNextNewFy!=null && Integer.parseInt(lsYear)==loNextNewFy)
					{
						lsBudgetYears.add(loNextNewFy.toString());
					}
					//end release 3.14.0
				}
				aoResourceRequest.setAttribute(HHSConstants.BMC_BUDGET_FISCAL_YEAR, lsBudgetYears);
			}
			if (lsFyYear != null && HHSConstants.FALSE.equalsIgnoreCase(lsFyYear))
			{

				// Set attributes to be placed in JSP
				aoResourceRequest.setAttribute(HHSConstants.CURRENT_FY_PLANNED_AMOUNT, HHSConstants.INT_ZERO);
				aoResourceRequest.setAttribute(HHSConstants.AMENDMENT_FY_PLANNED_AMOUNT, HHSConstants.INT_ZERO);
				aoResourceRequest.setAttribute(HHSConstants.NEW_FY_PLANNED_AMOUNT, HHSConstants.INT_ZERO);

				for (String lsBudgetYear : lsBudgetYears)
				{
					//start release 3.14.0
					if(loNextNewFy!=null && Integer.parseInt(lsBudgetYear)==loNextNewFy)
					{
						contractAmendmentBudgetPageFinalNextNewFy(aoResourceRequest, loChannelObj, loCBGridBean, loPprocureCof,
								lsBudgetYear);
					}
					else
					{
						contractAmendmentBudgetPageFinal(aoResourceRequest, loChannelObj, loCBGridBean, loPprocureCof,
								lsBudgetYear);
					}
					//end release 3.14.0
				}
			}
			else
			{
				loChannelObj.setData(HHSConstants.FISCAL_YEAR_ID_KEY, lsFyYear);
				loReturnedBudgetDetails = BmcControllerUtil.getAmendmentBudgetDetails(loChannelObj);
				String lsFYBudgetPlannedAmount = BmcControllerUtil.getFYBudgetPlannedAmount(loChannelObj,
						HHSConstants.TRUE);
				HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.BMC_CONTRACT_BUDGET_AMEND_FY_DATA);

				ContractBudgetBean loContractBudgetBean = (ContractBudgetBean) loChannelObj
						.getData(HHSConstants.AO_CONTRACT_BUDGET_BEAN);
				// Made changes to update budget bean object in session
				if (!loReturnedBudgetDetails.isEmpty())
				{
					ContractBudgetBean loContractBudgetBeanUpdated = (ContractBudgetBean) loReturnedBudgetDetails.get(0);
					loContractBudgetBeanUpdated.setTotalbudgetAmount(lsFYBudgetPlannedAmount);
					PortletSessionHandler.setAttribute(loContractBudgetBeanUpdated, aoResourceRequest,
							HHSConstants.BMC_CONTRACT_BUDGET_BEAN);
				}
				aoResourceRequest.setAttribute(HHSConstants.CURRENT_FY_PLANNED_AMOUNT,
						loContractBudgetBean.getTotalbudgetAmount());
				aoResourceRequest.setAttribute(HHSConstants.AMENDMENT_FY_PLANNED_AMOUNT,
						loContractBudgetBean.getAmendAmt());
				aoResourceRequest
						.setAttribute(HHSConstants.POS_AMEND_FY_PENDING, loContractBudgetBean.getPosAmendAmt());
				aoResourceRequest
						.setAttribute(HHSConstants.NEG_AMEND_FY_PENDING, loContractBudgetBean.getNegAmendAmt());
				// R4 start fetch entrytype details
				aoResourceRequest.setAttribute(HHSConstants.ENTRY_TYPE_ID, HHSUtil.getEntryTypeDetail(
						loCBGridBean.getContractID(), null, loCBGridBean.getAmendmentContractID(),
						HHSConstants.TASK_AMENDMENT_CONFIGURATION, lsFyYear));
				// R4end fetch entrytype details
			}
		}
		catch (Exception aoExe)
		{
			aoResourceRequest.setAttribute(HHSConstants.CURRENT_FY_PLANNED_AMOUNT, HHSConstants.INT_ZERO);
			aoResourceRequest.setAttribute(HHSConstants.AMENDMENT_FY_PLANNED_AMOUNT, HHSConstants.INT_ZERO);
			aoResourceRequest.setAttribute(HHSConstants.POS_AMEND_FY_PENDING, HHSConstants.INT_ZERO);
			aoResourceRequest.setAttribute(HHSConstants.NEG_AMEND_FY_PENDING, HHSConstants.INT_ZERO);
			aoResourceRequest.setAttribute(HHSConstants.NEW_FY_PLANNED_AMOUNT, HHSConstants.INT_ZERO);
			aoResourceRequest.setAttribute(HHSConstants.BMC_BUDGET_FISCAL_YEAR, HHSConstants.EMPTY_STRING);
			LOG_OBJECT.Error("Error occured in contractBudgetPage method while performing operation on grid  ", aoExe);
		}
		loModelAndView = new ModelAndView(HHSConstants.AMENDMENT_CONTRACT_BUDGETS);
		return loModelAndView;
	}

	/**release 3.14.0
	 * This method is used to populate the Tab for Budget Configuration
	 * <ul>
	 * <li>This method also sets the Current Configurable Budget Information in
	 * session</li>
	 * <li>This method was updated in R4</li>
	 * <li>the transaction used: insertAmendmentSubBudgetDetails</li>
	 * <ul>
	 * 
	 * @param aoResourceRequest Resource Request Object
	 * @param loChannelObj channel object
	 * @param loCBGridBean CBGridBean object
	 * @param loPprocureCof ProcurementCOF object
	 * @param lsBudgetYear budget year
	 * @throws ApplicationException If an exception occurs
	 * @throws NumberFormatException If NumberFormatException occurs
	 */
	private void contractAmendmentBudgetPageFinalNextNewFy(ResourceRequest aoResourceRequest, Channel loChannelObj,
			CBGridBean loCBGridBean, ProcurementCOF loPprocureCof, String lsBudgetYear) throws ApplicationException,
			NumberFormatException
	{   LOG_OBJECT.Info("====contractAmendmentBudgetPageFinalNextNewFy===");
		List loReturnedBudgetDetails;
		loChannelObj.setData(HHSConstants.FISCAL_YEAR_ID_KEY, lsBudgetYear);
		loChannelObj.setData(HHSConstants.CB_GRID_BEAN_OBJ,loCBGridBean);
		loReturnedBudgetDetails = BmcControllerUtil.getAmendmentBudgetDetails(loChannelObj);
		ContractBudgetBean loContractBudgetBean = new ContractBudgetBean();
		String lsFYBudgetPlannedAmount = BmcControllerUtil.getFYBudgetPlannedAmount(loChannelObj,
				HHSConstants.TRUE);
		List<ContractBudgetBean> loBudgetDetails = BmcControllerUtil
				.getNextNewFYBudgetDetails(loChannelObj);
		loContractBudgetBean.setBudgetId(loBudgetDetails.get(0).getBudgetId());
		if (loReturnedBudgetDetails.isEmpty())
		{
			BmcControllerUtil.addNewAmendmentBudget(aoResourceRequest, loContractBudgetBean, loChannelObj,
					loCBGridBean, loPprocureCof, Integer.parseInt(lsBudgetYear), lsFYBudgetPlannedAmount);
			ContractBudgetBean loContractBudget = new ContractBudgetBean();
			loContractBudget.setBudgetfiscalYear(lsBudgetYear);
			loContractBudget.setBudgetStartDate(HHSUtil.getNewBudgetStartDate(
					loPprocureCof.getContractStartDate(), lsBudgetYear));
			loContractBudget.setBudgetEndDate(HHSUtil.getNewBudgetEndDate(
					loPprocureCof.getContractEndDate(), lsBudgetYear));
			loContractBudget.setBudgetTypeId(Integer.parseInt(HHSConstants.ONE));
			loContractBudget.setContractId(loCBGridBean.getContractID());
			String lsUserId = String.valueOf(aoResourceRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE));
			loContractBudget.setCreatedByUserId(lsUserId);
			loContractBudget.setModifiedByUserId(lsUserId);
			loContractBudget.setContractTypeId(HHSConstants.TWO);
			loContractBudget.setStatusId(HHSConstants.BUDGET_PENDING_CONFIGURATION_STATUS_ID);
			loChannelObj.setData(HHSConstants.AO_CONTRACT_BUDGET_BEAN, loContractBudget);
		}
		// If some changes are there in Total Planned Amount for
		// this Budget
		// Update it in the DB too
		else
		{
			loContractBudgetBean = (ContractBudgetBean) loReturnedBudgetDetails.get(0);

			if (new BigDecimal(lsFYBudgetPlannedAmount).compareTo(new BigDecimal(loContractBudgetBean
					.getTotalbudgetAmount())) != HHSConstants.INT_ZERO)
			{
				loContractBudgetBean.setTotalbudgetAmount(lsFYBudgetPlannedAmount);
				loChannelObj.setData(HHSConstants.AO_CONTRACT_BUDGET_BEAN, loContractBudgetBean);
				HHSTransactionManager.executeTransaction(loChannelObj,
						HHSConstants.BMC_UPDATE_BUDGET_FY_TOTAL_BUDGET_AMOUNT);
			}
			PortletSessionHandler.setAttribute(loContractBudgetBean, aoResourceRequest,
					HHSConstants.BMC_CONTRACT_BUDGET_BEAN);
		}
	}

	
	/**
	 * This method is used to populate the Tab for Budget Configuration
	 * <ul>
	 * <li>This method also sets the Current Configurable Budget Information in
	 * session</li>
	 * <li>This method was updated in R4</li>
	 * <li>the transaction used: insertAmendmentSubBudgetDetails</li>
	 * <ul>
	 * 
	 * @param aoResourceRequest Resource Request Object
	 * @param loChannelObj channel object
	 * @param loCBGridBean CBGridBean object
	 * @param loPprocureCof ProcurementCOF object
	 * @param lsBudgetYear budget year
	 * @throws ApplicationException If an exception occurs
	 * @throws NumberFormatException If NumberFormatException occurs
	 */
	private void contractAmendmentBudgetPageFinal(ResourceRequest aoResourceRequest, Channel loChannelObj,
			CBGridBean loCBGridBean, ProcurementCOF loPprocureCof, String lsBudgetYear) throws ApplicationException,
			NumberFormatException
	{   LOG_OBJECT.Info("====contractAmendmentBudgetPageFinal===");
		List loReturnedBudgetDetails;
		loChannelObj.setData(HHSConstants.FISCAL_YEAR_ID_KEY, lsBudgetYear);

		loReturnedBudgetDetails = BmcControllerUtil.getAmendmentBudgetDetails(loChannelObj);
		ContractBudgetBean loContractBudgetBean = new ContractBudgetBean();
		String lsFYBudgetPlannedAmount = BmcControllerUtil.getFYBudgetPlannedAmount(loChannelObj,
				HHSConstants.TRUE);
		List<ContractBudgetBean> loBudgetDetails = BmcControllerUtil
				.getActiveApprovedBudgetDetails(loChannelObj);
		loContractBudgetBean.setBudgetId(loBudgetDetails.get(0).getBudgetId());
		if (loReturnedBudgetDetails.isEmpty())
		{
			BmcControllerUtil.addNewAmendmentBudget(aoResourceRequest, loContractBudgetBean, loChannelObj,
					loCBGridBean, loPprocureCof, Integer.parseInt(lsBudgetYear), lsFYBudgetPlannedAmount);
			ContractBudgetBean loContractBudget = new ContractBudgetBean();
			loContractBudget.setBudgetfiscalYear(lsBudgetYear);
			loContractBudget.setBudgetStartDate(HHSUtil.getNewBudgetStartDate(
					loPprocureCof.getContractStartDate(), lsBudgetYear));
			loContractBudget.setBudgetEndDate(HHSUtil.getNewBudgetEndDate(
					loPprocureCof.getContractEndDate(), lsBudgetYear));
			loContractBudget.setBudgetTypeId(Integer.parseInt(HHSConstants.ONE));
			loContractBudget.setContractId(loCBGridBean.getContractID());
			String lsUserId = String.valueOf(aoResourceRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE));
			loContractBudget.setCreatedByUserId(lsUserId);
			loContractBudget.setModifiedByUserId(lsUserId);
			loContractBudget.setContractTypeId(HHSConstants.TWO);
			loContractBudget.setStatusId(HHSConstants.BUDGET_PENDING_CONFIGURATION_STATUS_ID);
			loChannelObj.setData(HHSConstants.AO_CONTRACT_BUDGET_BEAN, loContractBudget);
			HHSTransactionManager.executeTransaction(loChannelObj,
					HHSConstants.BMC_INSERT_AMENDMENT_SUB_BUDGET_DETAILS);
		}
		// If some changes are there in Total Planned Amount for
		// this Budget
		// Update it in the DB too
		else
		{
			loContractBudgetBean = (ContractBudgetBean) loReturnedBudgetDetails.get(0);

			if (new BigDecimal(lsFYBudgetPlannedAmount).compareTo(new BigDecimal(loContractBudgetBean
					.getTotalbudgetAmount())) != HHSConstants.INT_ZERO)
			{
				loContractBudgetBean.setTotalbudgetAmount(lsFYBudgetPlannedAmount);
				loChannelObj.setData(HHSConstants.AO_CONTRACT_BUDGET_BEAN, loContractBudgetBean);
				HHSTransactionManager.executeTransaction(loChannelObj,
						HHSConstants.BMC_UPDATE_BUDGET_FY_TOTAL_BUDGET_AMOUNT);
			}
			PortletSessionHandler.setAttribute(loContractBudgetBean, aoResourceRequest,
					HHSConstants.BMC_CONTRACT_BUDGET_BEAN);
		}
	}
	/**
	 * This method is used to populate the Tab for New FY Budget Configuration
	 * <ul>
	 * <li>This method gets called when contract budget tab is hit on new fiscal year configuration task </li>
	 * <li>This method is restructured to call a single transaction</li>
	 * <ul>
	 * @param aoResourceRequest Resource Request Object
	 * @param aoResourceResponse Resource Response Object
	 * @return loModelAndView
	 * @throws Exception
	 */
	@ResourceMapping("newFYBudgetPage")
	public ModelAndView getNewFYBudgetPage(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{   LOG_OBJECT.Info("====getNewFYBudgetPage====");
		ModelAndView loModelAndView = null;
		String lsViewTab = aoResourceRequest.getParameter(HHSConstants.BMC_VIEW_TAB);
		Boolean loNewFYBudgetTab = false;
		try
		{
			Channel loChannelObj = new Channel();
			CBGridBean loCBGridBean = (CBGridBean) PortletSessionHandler.getAttribute(aoResourceRequest, true,
					HHSConstants.CBGRIDBEAN_IN_SESSION);

			Integer loConfigurationFiscalYear = Integer.parseInt(((String) aoResourceRequest.getPortletSession() 
					.getAttribute(HHSConstants.BASE_LS_CONFIGURABLE_FISCAL_YEAR)));               

			if (null != lsViewTab && lsViewTab.equalsIgnoreCase(HHSConstants.BMC_NEW_FY_CONFIGURATION_BUDGET_TAB))
			{
				loNewFYBudgetTab = true;
			}

			loCBGridBean.setBudgetStartYear(loConfigurationFiscalYear.toString());
			loChannelObj.setData(HHSConstants.CB_GRID_BEAN_OBJ, loCBGridBean);
			loChannelObj.setData(HHSConstants.NEW_FY_TASK_BUDGET_TAB, loNewFYBudgetTab);

			HHSTransactionManager.executeTransaction(loChannelObj,
					HHSConstants.BMC_NEW_FY_BUDGET_PROCESSING);
			
			aoResourceRequest.setAttribute(HHSConstants.BMC_ADD_ENABLED, HHSConstants.TRUE);

			ContractBudgetBean loContractBudgetBean = (ContractBudgetBean) loChannelObj.getData(HHSConstants.AO_CONTRACT_BUDGET_BEAN);
			PortletSessionHandler.setAttribute(loContractBudgetBean, aoResourceRequest,
					HHSConstants.BMC_CONTRACT_BUDGET_BEAN);

			loCBGridBean.setContractBudgetID(loContractBudgetBean.getBudgetId());
			loCBGridBean.setBudgetAmount(loContractBudgetBean.getTotalbudgetAmount());			
			PortletSessionHandler.setAttribute(loCBGridBean, aoResourceRequest, HHSConstants.CBGRIDBEAN_IN_SESSION);

			// Set attributes to be placed in JSP
			aoResourceRequest.setAttribute(HHSConstants.BMC_FY_BUDGET_PLANNED_AMOUNT, loContractBudgetBean.getTotalbudgetAmount());
			
			if(loNewFYBudgetTab){
			// R4 get EntryType Details for NewFY Config.
			aoResourceRequest.setAttribute(HHSConstants.ENTRY_TYPE_ID, HHSUtil.getEntryTypeDetail(
					loCBGridBean.getContractID(), loContractBudgetBean.getBudgetId(), null, null,
					loConfigurationFiscalYear.toString()));
			}
			
			aoResourceRequest.setAttribute(HHSConstants.BMC_CONTRACT_ID, loCBGridBean.getContractID());
			aoResourceRequest.setAttribute(HHSConstants.BMC_CONTRACT_VALUE, loContractBudgetBean.getContractValue());
			aoResourceRequest.setAttribute(HHSConstants.BMC_BUDGET_FISCAL_YEAR,
					loContractBudgetBean.getBudgetfiscalYear());
			aoResourceRequest.setAttribute(HHSConstants.NON_EDIT_COLNAME,
					aoResourceRequest.getParameter(HHSConstants.NON_EDIT_COLNAME));

		}
		catch (Exception aoExe)
		{
			aoResourceRequest.setAttribute(HHSConstants.BMC_FY_BUDGET_PLANNED_AMOUNT, HHSConstants.STRING_ZERO);
			aoResourceRequest.setAttribute(HHSConstants.BMC_CONTRACT_VALUE, HHSConstants.STRING_ZERO);
			aoResourceRequest.setAttribute(HHSConstants.BMC_BUDGET_FISCAL_YEAR, HHSConstants.EMPTY_STRING);
			aoResourceRequest.setAttribute(HHSConstants.BMC_FY_BUDGET_PLANNED_AMOUNT, HHSConstants.STRING_ZERO);
			LOG_OBJECT.Error("Error occured in newFYBudgetPage method while performing operation on grid  ", aoExe);
		}
		loModelAndView = new ModelAndView(HHSConstants.BMC_NEW_FY_CONFIGURATION_BUDGET_JSP_PATH);
		return loModelAndView;
	}

	/***
	 * This function fetch the ContractConfigurationUpdateDetails details in
	 * request.
	 * <ul>
	 * <li>1. Set asContractId in Channel.</li>
	 * <li>2. Execute transaction <b>fetchContractConfigurationUpdateDetails</b>
	 * <li>This method was updated in R4</li>
	 * </li> *
	 * </ul>
	 * 
	 * @param aoRequest render request
	 * @param asContractId String
	 * @throws ApplicationException object
	 */
	protected void fetchContractConfigurationUpdateDetails(RenderRequest aoRequest, String asContractId,
			String asContractTypeId) throws ApplicationException
	{   LOG_OBJECT.Info("====fetchContractConfigurationUpdateDetails====");
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.CONTRACT_ID, asContractId);
		loHashMap.put(HHSConstants.CONTRACT_TYPE_ID, asContractTypeId);
		Channel loChannel = new Channel();
		loChannel.setData(HHSConstants.AO_HASH_MAP, loHashMap);
		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.BMC_FETCH_UPDATED_CONFIGURATION_DETAILS);
		
	}

	/**
	 * This method perform actions on database based on operation performed on
	 * Subgrid for Sub Budgets (Budget Configuration)
	 * <ul>
	 * <li>If add operation perform on the grid insert row into database</li>
	 * <li>If delete operation perform on the grid delete row from database</li>
	 * <li>If edit operation perform on the grid update row into database</li>
	 * <li>This method was updated in R4</li>
	 * </ul>
	 * 
	 * @param aoResourceRequest to get screen parameters
	 */
	@ResourceMapping("gridOperationForBudgetConfigUpdateTask")
	public void gridOperationForBudgetConfigUpdateTask(ResourceRequest aoResourceRequest)
	{
		LOG_OBJECT.Info("====gridOperationForBudgetConfigUpdateTask====");
		String lsOperation = aoResourceRequest.getParameter(HHSConstants.GRID_OPERATION);
		String lsId = aoResourceRequest.getParameter(HHSConstants.ID);
		if (lsOperation != null && HHSConstants.OPERATION_ADD.equalsIgnoreCase(lsOperation)
				&& !lsId.equalsIgnoreCase(HHSConstants.NEW_ROW_IDENTIFIER))
		{
			lsOperation = HHSConstants.OPERATION_EDIT;
		}
		String lsTransactionName = aoResourceRequest.getParameter(HHSConstants.TRANSACTION_NAME);
		String lsBudgetType = aoResourceRequest.getParameter(HHSConstants.BUDGET_TYPE_ID);
		String lsAmendmentFlow = aoResourceRequest.getParameter(HHSConstants.IS_AMENDMENT_FLOW);
		LOG_OBJECT.Info("====gridOperationForBudgetConfigUpdateTask====lsAmendmentFlow :: "+lsAmendmentFlow);
		LOG_OBJECT.Info("====gridOperationForBudgetConfigUpdateTask====lsBudgetType :: "+lsBudgetType);
		LOG_OBJECT.Info("====gridOperationForBudgetConfigUpdateTask====lsTransactionName :: "+lsTransactionName);
		StringBuffer loPropertyName = new StringBuffer(lsTransactionName);
		if (null != lsOperation)
		{
			loPropertyName.append(lsOperation.substring(0, 1).toUpperCase()
					+ lsOperation.substring(1, lsOperation.length()));
		}
		try
		{
			Channel loChannelObj = new Channel();
			if (lsOperation != null && HHSConstants.OPERATION_EDIT.equalsIgnoreCase(lsOperation)
					&& aoResourceRequest.getParameter(HHSConstants.PAGE) != null)
			{
				PortletSessionHandler.setAttribute(aoResourceRequest.getParameter(HHSConstants.PAGE),
						aoResourceRequest, HHSConstants.PAGINATION);
			}
			lsTransactionName = PropertyLoader.getProperty(HHSConstants.PROPERTIES_GRIDTRANSACTION,
					loPropertyName.toString());
			ContractBudgetBean loBeanObj = new ContractBudgetBean();
			populateBeanFromRequest(aoResourceRequest, loBeanObj);
			CBGridBean loCBGridBean = (CBGridBean) PortletSessionHandler.getAttribute(aoResourceRequest, true,
					HHSConstants.CBGRIDBEAN_IN_SESSION);
			String lsSelectedFiscalYear = aoResourceRequest.getParameter(HHSConstants.SELECTED_FISCAL_YEAR);

			ContractBudgetBean loContractBudgetBean = (ContractBudgetBean) PortletSessionHandler.getAttribute(
					aoResourceRequest, true, HHSConstants.BMC_CONTRACT_BUDGET_BEAN);

			if (null != loContractBudgetBean)
			{
				loBeanObj.setBudgetId(loContractBudgetBean.getBudgetId());
				loBeanObj.setContractValue(loContractBudgetBean.getContractValue());
				loBeanObj.setTotalbudgetAmount(loContractBudgetBean.getTotalbudgetAmount());
			}

			loBeanObj.setBudgetTypeId(Integer.parseInt(lsBudgetType));
			loBeanObj.setBudgetfiscalYear(lsSelectedFiscalYear);

			loBeanObj.setContractId(loCBGridBean.getContractID());
			loBeanObj.setContractTypeId(loCBGridBean.getContractTypeId());
			loBeanObj.setAmendmentContractId(loCBGridBean.getAmendmentContractID());

			loBeanObj.setCreatedByUserId(String.valueOf(aoResourceRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE)));

			Boolean lbTransactionStatus = BmcControllerUtil.executeGridTransactionForBudgetConfigUpdate(lsOperation,
					lsTransactionName, loChannelObj, loBeanObj, lsAmendmentFlow);

			if (!lbTransactionStatus)
			{
				PortletSessionHandler.setAttribute(PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.BMC_PROP_CONTRACT_BUGET_OVERCONFIG_ERROR), aoResourceRequest,
						HHSConstants.GRID_ERROR);
			}

		}
		// ApplicationException and Exception are thrown from method
		// executeGridTransactionForBudgetConfigUpdate and
		// populateBeanFromRequest and while loading the property
		catch (ApplicationException aoAppExe)
		{
			ApplicationException loAppEx = (ApplicationException) aoAppExe.getRootCause();
			String lsGridErrorMessage = null;
			if (null != loAppEx)
			{
				lsGridErrorMessage = (String) loAppEx.getContextData().get(HHSConstants.GRID_ERROR_MESSAGE);
			}
			if (null == lsGridErrorMessage || lsGridErrorMessage.trim().isEmpty())
			{
				lsGridErrorMessage = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			}
			PortletSessionHandler.setAttribute(lsGridErrorMessage, aoResourceRequest, HHSConstants.GRID_ERROR);
			LOG_OBJECT
					.Error("ApplicationException occured in gridOperation method while performing operation on grid  "
							+ aoAppExe);
		}
		catch (Exception aoExe)
		{
			String lsGridErrorMessage = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			PortletSessionHandler.setAttribute(lsGridErrorMessage, aoResourceRequest, HHSConstants.GRID_ERROR);
			LOG_OBJECT.Error("Error occured in gridOperation method while performing operation on grid  ", aoExe);
		}
	}

	/**
	 * This method is used to populate the Tab for Budget Configuration
	 * <ul>
	 * <li>This method also sets the Current Configurable Budget Information in
	 * session</li>
	 * <li>This method was updated in R4</li>
	 * <li>the transaction used: insertUpdatedSubBudgetDetails</li>
	 * <ul>
	 * 
	 * @param aoResourceRequest Resource Request Object
	 * @param aoResourceResponse Resource Response Object
	 * @return loModelAndView - ModelAndView object
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	@ResourceMapping("contractConfigUpdateBudget")
	public ModelAndView getcontractConfigUpdateBudget(ResourceRequest aoResourceRequest,
			ResourceResponse aoResourceResponse)
	{   LOG_OBJECT.Info("====getcontractConfigUpdateBudget==== ");
		ModelAndView loModelAndView = null;
		ContractBudgetBean loContractBudgetBean = null;
		try
		{
			Channel loChannelObj = new Channel();
			CBGridBean loCBGridBean = (CBGridBean) PortletSessionHandler.getAttribute(aoResourceRequest, true,
					HHSConstants.CBGRIDBEAN_IN_SESSION);
			ProcurementCOF loPprocureCof = (ProcurementCOF) aoResourceRequest.getPortletSession().getAttribute(
					HHSConstants.BMC_CONTRACT_DATA_PARAM, PortletSession.APPLICATION_SCOPE);
			String lsTab = aoResourceRequest.getParameter(HHSConstants.SELECTED_FISCAL_YEAR);
			loChannelObj.setData(HHSConstants.CONTRACT_ID_KEY, loCBGridBean.getContractID());
			loChannelObj.setData(HHSConstants.FISCAL_YEAR_ID_KEY, lsTab);
			if (lsTab != null && HHSConstants.TAB.equalsIgnoreCase(lsTab))
			{
				aoResourceRequest.setAttribute(HHSConstants.BMC_CONTRACT_VALUE, HHSConstants.EMPTY_STRING);
				aoResourceRequest.setAttribute(HHSConstants.BMC_TOTAL_BUDGET_AMOUNT, HHSConstants.EMPTY_STRING);
			}
			else
			{
				loChannelObj.setData(HHSConstants.CB_GRID_BEAN_OBJ, loCBGridBean);
				List loContractBudgetBeanList = BmcControllerUtil.getBudgetDetails(loChannelObj);
				String lsUpdatedBudgetId = BmcControllerUtil.checkBudgetDetails(loChannelObj);
				if (null == loContractBudgetBeanList || loContractBudgetBeanList.isEmpty())
				{
					aoResourceRequest.setAttribute(HHSConstants.BMC_TOTAL_BUDGET_AMOUNT, HHSConstants.STRING_ZERO);
					aoResourceRequest.setAttribute(HHSConstants.BMC_ADD_ENABLED, HHSConstants.FALSE);
					aoResourceRequest.setAttribute(HHSConstants.BMC_CONTRACT_VALUE, HHSConstants.EMPTY_STRING);
				}
				else
				{
					loContractBudgetBean = (ContractBudgetBean) loContractBudgetBeanList.get(0);
					loContractBudgetBean.setContractValue(loPprocureCof.getContractValue());
					PortletSessionHandler.setAttribute(loContractBudgetBean, aoResourceRequest,
							HHSConstants.BMC_CONTRACT_BUDGET_BEAN);
					// FETCH PLANNED AMOUNT FOR UPCOMING FISCAL YEAR
					String lsFYBudgetPlannedAmount = BmcControllerUtil
							.getFYBudgetPlannedForUpdatedContractId(loChannelObj);
					aoResourceRequest.setAttribute(HHSConstants.BMC_TOTAL_BUDGET_AMOUNT, lsFYBudgetPlannedAmount);
					aoResourceRequest.setAttribute(HHSConstants.BMC_ADD_ENABLED, HHSConstants.TRUE);
				}

				if (lsUpdatedBudgetId == null)
				{
					loContractBudgetBean.setBudgetfiscalYear(lsTab);
					loContractBudgetBean.setBudgetStartDate(HHSUtil.getNewBudgetStartDate(
							loPprocureCof.getContractStartDate(), loContractBudgetBean.getBudgetfiscalYear()));
					loContractBudgetBean.setBudgetEndDate(HHSUtil.getNewBudgetEndDate(
							loPprocureCof.getContractEndDate(), loContractBudgetBean.getBudgetfiscalYear()));
					loContractBudgetBean.setBudgetTypeId(Integer.parseInt(HHSConstants.FOUR));
					loContractBudgetBean.setContractId(loCBGridBean.getContractID());
					String lsUserId = String.valueOf(aoResourceRequest.getPortletSession().getAttribute(
							ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE));
					loContractBudgetBean.setCreatedByUserId(lsUserId);
					loContractBudgetBean.setModifiedByUserId(lsUserId);
					loContractBudgetBean.setContractTypeId(HHSConstants.FOUR);
					loContractBudgetBean.setTotalbudgetAmount(loContractBudgetBean.getTotalbudgetAmount());
					loContractBudgetBean.setPlannedAmount(loContractBudgetBean.getTotalbudgetAmount());
					loContractBudgetBean.setStatusId(HHSConstants.BUDGET_PENDING_CONFIGURATION_STATUS_ID);
					loChannelObj.setData(HHSConstants.AO_CONTRACT_BUDGET_BEAN, loContractBudgetBean);
					HHSTransactionManager.executeTransaction(loChannelObj,
							HHSConstants.BMC_INSERT_UPDATED_SUB_BUDGET_DETAILS);
				}
				aoResourceRequest.setAttribute(HHSConstants.BMC_CONTRACT_VALUE, loPprocureCof.getAmendmentValue());
				// R4 get EntryType Details
				if (null != loContractBudgetBean)
				{
					aoResourceRequest.setAttribute(HHSConstants.ENTRY_TYPE_ID, HHSUtil.getEntryTypeDetail(
							loCBGridBean.getContractID(), loContractBudgetBean.getBudgetId(), lsUpdatedBudgetId,
							HHSConstants.CONTRACT_CONFIG_UPDATE, lsTab));
				}
			}
			List loFYIList = BmcControllerUtil.getFYList(loChannelObj);
			aoResourceRequest.setAttribute(HHSConstants.BMC_BUDGET_FISCAL_YEAR, loFYIList);
			aoResourceRequest.setAttribute(HHSConstants.SELECTED_FISCAL_YEAR, lsTab);
		}
		catch (Exception aoExe)
		{
			aoResourceRequest.setAttribute(HHSConstants.BMC_TOTAL_BUDGET_AMOUNT, HHSConstants.STRING_ZERO);
			aoResourceRequest.setAttribute(HHSConstants.BMC_CONTRACT_VALUE, HHSConstants.STRING_ZERO);
			aoResourceRequest.setAttribute(HHSConstants.BMC_BUDGET_FISCAL_YEAR, HHSConstants.EMPTY_STRING);
			LOG_OBJECT.Error("Error occured in contractBudgetPage method while performing operation on grid  ", aoExe);
		}
		loModelAndView = new ModelAndView(HHSConstants.BMC_CONTRACT_CONFIG_UPDATE_BUDGETS_JSP_PATH);
		return loModelAndView;
	}

	/**
	 * This method will be called on page load of contract budget screen.
	 * <ul>
	 * <li>1.Execute fetchContractBudgetDetailsBySelectedFY transaction.</li>
	 * <li>2.Fetches list of sub budgets.</li>
	 * <li>This method was updated in R4</li>
	 * 
	 * @param aoResourceRequest - ResourceRequest object
	 * @param aoResourceResponse - ResourceResponse object
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@ResourceMapping("fetchContractBudgetDetailsBySelectedFY")
	public void fetchContractBudgetDetailsBySelectedFY(ResourceRequest aoResourceRequest,
			ResourceResponse aoResourceResponse)
	{
		PrintWriter loOut = null;
		try
		{
			loOut = aoResourceResponse.getWriter();
			String lsGridLabel = aoResourceRequest.getParameter(HHSConstants.GRID_LABEL);
			String lsTransactionName = aoResourceRequest.getParameter(HHSConstants.TRANSACTION_NAME);
			String lsClass = aoResourceRequest.getParameter(HHSConstants.BEAN_NAME);
			String lsBudgetType = aoResourceRequest.getParameter(HHSConstants.BUDGET_TYPE_ID);
			StringBuffer loPropertyName = new StringBuffer(lsTransactionName);
			loPropertyName.append(HHSConstants.FETCH);
			lsTransactionName = PropertyLoader.getProperty(HHSConstants.PROPERTIES_GRIDTRANSACTION,
					loPropertyName.toString());
			aoResourceResponse.setContentType(HHSConstants.APPLICATION_JSON);
			String lsSelectedYear = aoResourceRequest.getParameter(HHSConstants.SELECTED_FISCAL_YEAR);
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
			CBGridBean loCBGridBean = (CBGridBean) PortletSessionHandler.getAttribute(aoResourceRequest, true,
					HHSConstants.CBGRIDBEAN_IN_SESSION);
			Channel loChannelObj = new Channel();
			loChannelObj.setData(HHSConstants.CONTRACT_ID_KEY, loCBGridBean.getContractID());
			loChannelObj.setData(HHSConstants.FISCAL_YEAR_ID_KEY, lsSelectedYear);
			loChannelObj.setData(HHSConstants.AS_BUDGET_TYPE_ID, lsBudgetType);
			loChannelObj.setData(HHSConstants.CONTRACT_TYPE_ID_KEY, loCBGridBean.getContractTypeId());
			loChannelObj.setData(HHSConstants.AMENDED_CONTRACT_ID, loCBGridBean.getAmendmentContractID());
			HHSTransactionManager.executeTransaction(loChannelObj, lsTransactionName);
			List loReturnedGridList = (List) loChannelObj.getData(HHSConstants.AO_RETURNED_GRID_LIST);
			/* [Start] R9.4.0 qc_9634 -- Vuln 1: CWE 470 - Use of Externally Controlled Input to Select Classes or Code ('Unsafe Reflection')   */
			lsClass = HHSUtil.checkClassAccessControl(lsClass); // throws ApplicationException if not valid class
			/* [End] R9.4.0 qc_9634 -- Vuln 1: CWE 470 - Use of Externally Controlled Input to Select Classes or Code ('Unsafe Reflection')   */
			Class loClass = Class.forName(lsClass);
			Object loBeanObj = loClass.newInstance();
			StringBuffer loBuffer = HHSUtil.populateSubGridRows(loBeanObj, loReturnedGridList, lsRowsPerPage, lsPage,
					lsErrorMsg, lsGridLabel);
			LOG_OBJECT.Debug("json for fetchContractBudgetDetailsBySelectedFY: userid:: "
					+ (String) aoResourceRequest.getPortletSession().getAttribute(
							ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE) + "\n json:: "
					+ loBuffer.toString());
			loOut.print(loBuffer.toString().replaceAll("\\\\'", "'"));
		}
		// ApplicationException and exception are thrown from
		// populateSubGridRows ,while
		// getting property by loader , getting data from cache and while
		// executing transaction
		// exception are thrown also while getting class instance and getting
		// writer
		catch (ApplicationException aoAppExe)
		{
			LOG_OBJECT.Error("ApplicationException occured in fetchContractBudgetDetailsBySelectedFY"
					+ " while fetching data from database ", aoAppExe);
		}
		catch (Exception aoExe)
		{
			LOG_OBJECT.Error("Exception occured in fetchContractBudgetDetailsBySelectedFY"
					+ " while fetching data from database  ", aoExe);
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
	 * AJAX CALL : This method will update contractBudget EntryTypeId
	 * <ul>
	 * <li>1.Execute updateEntryType transaction.</li>
	 * <li>2. Update BudgetCustomization Table on check/uncheck EntryTypeId</li>
	 * <li>This method was updated in R4</li>
	 * 
	 * @param aoResourceRequest - ResourceRequest object
	 */
	@ResourceMapping("UpdateBudgetCustomizedTab")
	public ModelAndView UpdateBudgetCustomizedTab(ResourceRequest aoResourceRequest)
	{
		ModelAndView loModelAndView = null;
		CBGridBean loCBGridBean = (CBGridBean) PortletSessionHandler.getAttribute(aoResourceRequest, true,
				HHSConstants.CBGRIDBEAN_IN_SESSION);
		try
		{
			Channel loChannel = new Channel();
			HashMap<String, String> loHashMap = new HashMap<String, String>();
			if (loCBGridBean.getContractID() != null
					&& !(HHSConstants.EMPTY_STRING).equals(loCBGridBean.getContractID()))
			{
				loHashMap.put(HHSConstants.CONTRACT_ID, loCBGridBean.getContractID());
			}
			if (loCBGridBean.getContractBudgetID() != null
					&& !(HHSConstants.EMPTY_STRING).equals(loCBGridBean.getContractBudgetID()))
			{
				loHashMap.put(HHSConstants.BUDGET_ID, loCBGridBean.getContractBudgetID());
			}
			else if (HHSConstants.TASK_AMENDMENT_CONFIGURATION.equals(aoResourceRequest
					.getParameter(HHSConstants.SCREEN_NAME)))
			{
				ContractBudgetBean loContractBudgetBean = (ContractBudgetBean) PortletSessionHandler.getAttribute(
						aoResourceRequest, true, HHSConstants.BMC_CONTRACT_BUDGET_BEAN);
				loHashMap.put(HHSConstants.BUDGET_ID, loContractBudgetBean.getBudgetId());
				loHashMap.put(HHSConstants.CONTRACT_ID, loCBGridBean.getAmendmentContractID());
			}
			else
			{
				loHashMap.put(HHSConstants.SCREEN_NAME, aoResourceRequest.getParameter(HHSConstants.SCREEN_NAME));
			}
			loHashMap.put(
					HHSConstants.ENTRY_TYPE_ID,
					aoResourceRequest.getParameter(HHSConstants.ID).replaceAll(
							aoResourceRequest.getParameter(HHSConstants.BUDGET_YEAR), HHSConstants.EMPTY_STRING));
			loHashMap.put(HHSConstants.IS_CHECKED, aoResourceRequest.getParameter(HHSConstants.PARAM_KEY_OPERATION));
			loHashMap.put(
					HHSConstants.CREATED_BY_USER_ID,
					(String) aoResourceRequest.getPortletSession().getAttribute(
							ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE));
			loHashMap.put(HHSConstants.CONTRACT_TYPE_ID, loCBGridBean.getContractTypeId());
			loHashMap
					.put(HHSConstants.BMC_BUDGET_FISCAL_YEAR, aoResourceRequest.getParameter(HHSConstants.BUDGET_YEAR));
			loChannel.setData(HHSConstants.AO_HASH_MAP, loHashMap);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.UPDATE_ENTRY_TYPE);
		}
		catch (ApplicationException aoAppExe)
		{
			LOG_OBJECT.Error("ApplicationException occured in UpdateBudgetCustomizedTab" + " while update database ",
					aoAppExe);
		}
		catch (Exception aoExe)
		{
			LOG_OBJECT.Error("Exception occured in UpdateBudgetCustomizedTab" + " while update database  ", aoExe);
		}
		return loModelAndView;
	}

	//Start : Added in R7 for Cost Center
	/**
	 * This method is added in R7 for Cost Center.It is used to populate the
	 * Details for Cost Center
	 * <ul>
	 * <li>This method calls fetchCostCenterDetails transaction</li>
	 * <ul>
	 * @param aoResourceRequest Resource Request Object
	 * @param aoResourceResponse Resource Response Object
	 * @return loModelAndView
	 * @throws Exception
	 */
	@ResourceMapping("getCostCenterDetails")
	public ModelAndView getCostCenterDetails(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{   LOG_OBJECT.Info("=====getCostCenterDetails=====");
		ModelAndView loModelAndView = null;
		try
		{
			Channel loChannelObj = new Channel();
			String lsNewFyScreen = aoResourceRequest.getParameter(HHSConstants.CHECK_FOR_NEW_FY);
			LOG_OBJECT.Info("=====lsNewFyScreen :: "+ lsNewFyScreen);
			if(StringUtils.isNotEmpty(lsNewFyScreen))
			{
				loChannelObj.setData(HHSConstants.CHECK_FOR_NEW_FY, lsNewFyScreen);
			}
			CBGridBean loCBGridBean = (CBGridBean) PortletSessionHandler.getAttribute(aoResourceRequest, true,	HHSConstants.CBGRIDBEAN_IN_SESSION);
			LOG_OBJECT.Info("=====loCBGridBean :: "+ loCBGridBean);
			String lsUserId = (String) aoResourceRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			String lsBudgetId = aoResourceRequest.getParameter(HHSConstants.BUDGET_YEAR);
			LOG_OBJECT.Info("=====FiscalYearID :: "+lsBudgetId);
			if(StringUtils.isNotEmpty(lsBudgetId))
			{
				loCBGridBean.setFiscalYearID(lsBudgetId);
			}
			loCBGridBean.setCreatedByUserId(lsUserId);
			//Added for defect 8776
			if(HHSConstants.TRUE.equalsIgnoreCase(aoResourceRequest.getParameter(HHSConstants.BMC_CONTRACT_CONFIG_TASK)))
			{
				loCBGridBean.setTransactionName(HHSConstants.BMC_CONTRACT_CONFIG_TASK);
			}
			//Start  QC 9680 R 9.5
			LOG_OBJECT.Info("=====isAmendmentFlow :: "+aoResourceRequest.getParameter("isAmendmentFlow"));
			LOG_OBJECT.Info("=====BMC_TASK_NAME :: "+ aoResourceRequest.getParameter(HHSConstants.BMC_TASK_NAME));
						/*
			if(HHSConstants.TRUE.equalsIgnoreCase(aoResourceRequest.getParameter("isAmendmentFlow")))
			{
				loCBGridBean.setTransactionName("ContractConfigurationAmendmentTask");
			}
			*/
			//End QC 9680 R 9.5
			if(null == loCBGridBean.getContractTypeId())
			{
				loCBGridBean.setContractTypeId(HHSConstants.ONE);
			}
			loChannelObj.setData(HHSConstants.CB_GRID_BEAN_OBJ, loCBGridBean);
			HHSTransactionManager.executeTransaction(loChannelObj, HHSR5Constants.FETCH_COST_CENTER_DETAILS,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			String lsServiceStatusFlag = (String) loChannelObj.getData(HHSR5Constants.SELECTION_FLAG);
			aoResourceRequest.setAttribute(HHSConstants.CONTRACT_TYPE, loCBGridBean.getContractTypeId());
			// Disabling services in case of negative amendment
			if(HHSConstants.NEGATIVE.equalsIgnoreCase(loCBGridBean.getAmendmentType()))
			{
				aoResourceRequest.setAttribute(HHSConstants.SCREEN_READ_ONLY, HHSConstants.TRUE);
			}
			else
			{
				aoResourceRequest.setAttribute(HHSConstants.SCREEN_READ_ONLY,
						aoResourceRequest.getParameter(HHSConstants.SCREEN_READ_ONLY));
			}
			if(null == lsServiceStatusFlag)
			{
				lsServiceStatusFlag = HHSConstants.ZERO;
			}
			aoResourceRequest.setAttribute(HHSR5Constants.SELECTION_FLAG, lsServiceStatusFlag);
			// Displaying Services for Contracts in Pending COF status
			if (HHSConstants.TRUE.equalsIgnoreCase(aoResourceRequest.getParameter(HHSConstants.RENDER_CONTRACT_COF))
					&& HHSConstants.ZERO.equalsIgnoreCase(lsServiceStatusFlag))
			{
				HHSTransactionManager.executeTransaction(loChannelObj, HHSR5Constants.FETCH_AGENCY_SERVICE_FLAG,
						HHSR5Constants.TRANSACTION_ELEMENT_R5);
				String lsAgencyFlag = (String) loChannelObj.getData(HHSR5Constants.AS_RETURN_STATUS);
				if (HHSConstants.ONE.equalsIgnoreCase(lsAgencyFlag) || HHSConstants.TWO.equalsIgnoreCase(lsAgencyFlag))
				{
					aoResourceRequest.setAttribute(HHSR5Constants.SELECTION_FLAG, HHSConstants.ONE);
				}
			}
			HashMap loServicesMap = (HashMap) loChannelObj.getData(HHSR5Constants.AO_SERVICES_MAP);
			// Setting attribute for New FY Task
			aoResourceRequest.setAttribute(HHSR5Constants.CHECK_FOR_NEW_FY, lsNewFyScreen);
			aoResourceRequest.setAttribute(HHSR5Constants.SERVICES_MAP, loServicesMap);
			
			loModelAndView = new ModelAndView(HHSR5Constants.JSP_COST_CENTER_BUDGET_DETAILS);
			return loModelAndView;
		}
		catch (Exception aoExe)
		{
			LOG_OBJECT.Error("Error occured in getCostCenterDetails method while performing operation ", aoExe);
			return loModelAndView;
		}
		
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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ResourceMapping("updateSelectedServices")
	public void updateSelectedServices(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{   
		try
		{   LOG_OBJECT.Info("=====updateSelectedServices=====");
			boolean proceed = true; // start qc 9654
			// Start QC 9680 R 9.5 Multi tab issue Amendment Configuration task
			Integer contractStatusId = 0; //qc9680
			String budgetStatusId = " "; //qc9680
			//PortletSession loSession = aoResourceRequest.getPortletSession(); do not need it
			// End QC 9680 R 9.5 Multi tab issue Amendment Configuration task
			String[] loUserSelectedList = aoResourceRequest.getParameter(HHSConstants.SEL_SER_LIST).split(HHSConstants.COMMA);
			String[] loUserDeleteList = aoResourceRequest.getParameter(HHSR5Constants.DELETE_ITEMS_LIST).split(HHSConstants.COMMA);
			String lsUserId = (String) aoResourceRequest.getPortletSession().getAttribute(
											ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			CBGridBean loCBGridBean = (CBGridBean) PortletSessionHandler.getAttribute(aoResourceRequest, true, HHSConstants.CBGRIDBEAN_IN_SESSION);
			String lsNewFyScreen = aoResourceRequest.getParameter(HHSConstants.CHECK_FOR_NEW_FY);
			String lsBudgetYear = aoResourceRequest.getParameter(HHSConstants.BUDGET_YEAR);
			LOG_OBJECT.Debug("=====updateSelectedServices===lsNewFyScreen : " + lsNewFyScreen);
			LOG_OBJECT.Debug("=====updateSelectedServices===lsBudgetYear : " + lsBudgetYear);
			
			/* Start QC 9654 R 9.4 Multi-Tab Browsing Causing Records to not be Inserted for Services and Cost Centers - add @HHSTokenValidator*/
			TaskDetailsBean aoTaskDetailBean = (TaskDetailsBean) aoResourceRequest.getPortletSession().getAttribute(
					HHSConstants.TASK_DETAIL_BEAN_SESSION, PortletSession.APPLICATION_SCOPE);
			LOG_OBJECT.Debug("=====updateSelectedServices===aoTaskDetailBean : " + aoTaskDetailBean);	
			LOG_OBJECT.Debug("=====updateSelectedServices===aoTaskDetailBean.getTaskName : " + aoTaskDetailBean.getTaskName());	
			LOG_OBJECT.Debug("=====updateSelectedServices===aoTaskDetailBean.getTaskType : " +  aoTaskDetailBean.getTaskType());		
			LOG_OBJECT.Debug("=====updateSelectedServices===loCBGridBean : " + loCBGridBean);
			LOG_OBJECT.Debug("=====updateSelectedServices===task name : " + loCBGridBean.getTransactionName());
			LOG_OBJECT.Debug("=====updateSelectedServices===contractId : " + loCBGridBean.getContractID());
			LOG_OBJECT.Debug("=====updateSelectedServices===contract type : " + loCBGridBean.getContractTypeId());
			LOG_OBJECT.Debug("=====updateSelectedServices===ContractBudgetID : " + loCBGridBean.getContractBudgetID());
			LOG_OBJECT.Debug("=====updateSelectedServices===AmendmentContractID : " + loCBGridBean.getAmendmentContractID());
			LOG_OBJECT.Debug("=====updateSelectedServices===AmendmentType() : " + loCBGridBean.getAmendmentType());
			
			if(loCBGridBean.getContractTypeId().equalsIgnoreCase("1") && HHSConstants.BMC_CONTRACT_CONFIG_TASK.equalsIgnoreCase(loCBGridBean.getTransactionName()))
			{
				Channel aoChannel = new Channel();
				HashMap<String, Object> loHMResult = new HashMap<String, Object>();
				LOG_OBJECT.Debug("Base ContractId :: "+loCBGridBean.getContractID());
				aoChannel.setData(HHSConstants.CONTRACT_ID_KEY, loCBGridBean.getContractID());
				HHSTransactionManager.executeTransaction(aoChannel, HHSConstants.FETCH_BASE_CONTRACT_STATUS_ID);
				contractStatusId = (Integer) aoChannel.getData("status_id");
				LOG_OBJECT.Debug("Base Contract Status :: "+contractStatusId);
			}
			// Start QC 9682 R 9.5 EXT QC9654 - New Fiscal Year Config Muliti-Tab Browsing Missing Extra Services and Cost Centers
			else if(loCBGridBean.getContractTypeId().equalsIgnoreCase("1") && lsNewFyScreen.equalsIgnoreCase("true")
					&& HHSConstants.TASK_NEW_FY_CONFIGURATION.equalsIgnoreCase(aoTaskDetailBean.getTaskName()))	
			{ 
				// Check the status of budget != 106 anymore
				Channel aoChannel = new Channel();
				LOG_OBJECT.Debug("Base ContractId :: "+loCBGridBean.getContractID());
				LOG_OBJECT.Debug("FiscalYearID :: "+ loCBGridBean.getFiscalYearID());
				aoChannel.setData(HHSConstants.CB_GRID_BEAN_OBJ, loCBGridBean);
				HHSTransactionManager.executeTransaction(aoChannel, HHSR5Constants.FETCH_BASE_BUDGET_STATUS);
				budgetStatusId = (String) aoChannel.getData("status_id");
				LOG_OBJECT.Debug("Base Budget Status Id for FY "+ loCBGridBean.getFiscalYearID() +" :: "+budgetStatusId);
				
			}
			// End QC 9682 R 9.5 EXT QC9654 - New Fiscal Year Config Muliti-Tab Browsing Missing Extra Services and Cost Centers
			// Start QC 9680 R 9.5 Multi tab issue Amendment Configuration task
			else if(loCBGridBean.getContractTypeId().equalsIgnoreCase("2") && HHSConstants.TASK_AMENDMENT_CONFIGURATION.equals(aoTaskDetailBean.getTaskName()))	//QC9680 
			{
				Channel aoChannel = new Channel();
				LOG_OBJECT.Debug("Amendment ContractId :: "+loCBGridBean.getAmendmentContractID());
				aoChannel.setData(HHSConstants.CONTRACT_ID_KEY, loCBGridBean.getAmendmentContractID());
				HHSTransactionManager.executeTransaction(aoChannel, HHSConstants.FETCH_CONTRACT_STATUS_ID);
				contractStatusId = (Integer) aoChannel.getData("status_id");
				LOG_OBJECT.Debug("Contract Status :: "+contractStatusId);
			}
			// End QC 9680 R 9.5 Multi tab issue Amendment Configuration task

			// Start R9.5.0 QC9681 Contract Configuration Update task - 
			else if(loCBGridBean.getContractTypeId().equalsIgnoreCase("4") && HHSConstants.TASK_CONTRACT_UPDATE.equals(aoTaskDetailBean.getTaskName()))	
			{
				Channel aoChannel = new Channel();
				LOG_OBJECT.Debug( "["+aoTaskDetailBean.getTaskName()+ "] Update Task ContractId :: "+loCBGridBean.getContractID() 
						          + "FiscalYearID :: "+ loCBGridBean.getFiscalYearID());
				aoChannel.setData(HHSConstants.CB_GRID_BEAN_OBJ, loCBGridBean);
				HHSTransactionManager.executeTransaction(aoChannel, HHSConstants.FETCH_CONTRACT_UPDATE_BUDGET_STATUS);
				HashMap<String,String> loStatusMap = (HashMap<String,String>) aoChannel.getData(HHSConstants.CONTRACT_UPDATE_BUDGET_STATUS_MAP);
				
				budgetStatusId =  loStatusMap.get(HHSConstants.CONTRACT_UPDATE_BUDGET_STATUS_ID);
				String loUpdateStatusId = loStatusMap.get(HHSConstants.CONTRACT_UPDATE_STATUS_ID);
				LOG_OBJECT.Debug( "["+aoTaskDetailBean.getTaskName()+ "] Update Task Contract  Status :: "+ loUpdateStatusId
				          + "Budget Status Id :: "+ budgetStatusId );
				if( budgetStatusId == null || budgetStatusId.isEmpty() ){    
					budgetStatusId   = HHSConstants.STRING_ZERO ;
				}
				if( loUpdateStatusId == null || loUpdateStatusId.isEmpty() ){    
					loUpdateStatusId   = HHSConstants.STRING_ZERO ;
				}
				contractStatusId = Integer.valueOf(loUpdateStatusId).intValue();
			}

			// Start QC 9681 Contract Configuration Update task -
			//This block of if state is to distingush Contract Configuration Update from Contract Configuration and Contract Configuration Amendment.
			if( HHSConstants.TASK_CONTRACT_UPDATE.equals(aoTaskDetailBean.getTaskName() ) ) {
				LOG_OBJECT.Debug( "["+aoTaskDetailBean.getTaskName()+ "] Update Task Contract  Status :: "+ contractStatusId
				          + "Budget Status Id :: "+ budgetStatusId );

				if(  ! ( 59 == contractStatusId  && "106".equalsIgnoreCase(budgetStatusId) )  )
				{
					proceed = false;
					String asDebugMessage = HHSConstants.PAGE_ERROR + HHSConstants.COLON + PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.DUPLICATE_SUBMISSION);
					LOG_OBJECT.Debug("message : " + asDebugMessage);
					response(asDebugMessage);
				}
			}else {
				LOG_OBJECT.Debug( "["+aoTaskDetailBean.getTaskName()+ "] Update Task Contract  Status :: "+ contractStatusId
				          + "Budget Status Id :: "+ budgetStatusId );
				if(59 != contractStatusId  && !"106".equalsIgnoreCase(budgetStatusId))
				{
					proceed = false;
					String asDebugMessage = HHSConstants.PAGE_ERROR + HHSConstants.COLON + PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.DUPLICATE_SUBMISSION);
					LOG_OBJECT.Debug("message : " + asDebugMessage);
					response(asDebugMessage);
				}	
			}
			// End QC 9681 Contract Configuration Update task - do not know solution yet
			
			if(proceed)
			{	
			/* End QC 9654 R 9.4 Multi-Tab Browsing Causing Records to not be Inserted for Services and Cost Centers - add @HHSTokenValidator*/
			
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
				loHmMap.put(HHSConstants.CONTRACT_ID1, loCBGridBean.getContractID());
				loHmMap.put(HHSConstants.AMEND_CONTRACT_ID, loCBGridBean.getAmendmentContractID());
				loHmMap.put(HHSConstants.CONTRACT_TYPE, (null == loCBGridBean.getContractTypeId()) ? HHSConstants.ONE
						: loCBGridBean.getContractTypeId());
				loHmMap.put(HHSConstants.BUDGET_ID, loCBGridBean.getContractBudgetID());
				loHmMap.put(HHSConstants.FISCAL_YEAR_ID, lsBudgetYear);
				loHmMap.put(HHSConstants.CHECK_FOR_NEW_FY, lsNewFyScreen);
				loChannelObj.setData(HHSConstants.AO_HASH_MAP, loHmMap);
				HHSTransactionManager.executeTransaction(loChannelObj, HHSR5Constants.UPDATE_SERVICES_DETAILS,
						HHSR5Constants.TRANSACTION_ELEMENT_R5);
				
			} // QC9654 R 9.4.0
		}
		catch (Exception aoExe)
		{
			LOG_OBJECT.Error("Error occured in updateSelectedServices method while updating Services", aoExe);
		}
		/* End QC 9654 R 9.4 Multi-Tab Browsing Causing Records to not be Inserted for Services and Cost Centers - add @HHSTokenValidator*/
	}

	/**
	 * This method is used to update Cost Center flag for a contract.It is added
	 * in R7 If User unchecks Cost Center . We remove Budget Template entry for
	 * Program Income also and Vice-versa.
	 * <ul>
	 * <li>This method calls updateCostCenterEnabled transaction</li>
	 * <ul>
	 * @param aoResourceRequest Resource Request Object
	 * @param aoResourceResponse Resource Response Object
	 * @return loModelAndView
	 * @throws Exception
	 */
	/* QC 9654 R 9.4 Multi-Tab Browsing Causing Records to not be Inserted for Services and Cost Centers - add @HHSTokenValidator*/
	@ResourceMapping("updateCostCenterEnabled")
	public void updateCostCenterEnabled(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		try
		{   
			boolean proceed = true; //qc 9654
			Integer contractStatusId = null; //qc9680
			CBGridBean loCBGridBean = (CBGridBean) PortletSessionHandler.getAttribute(aoResourceRequest, true,
					HHSConstants.CBGRIDBEAN_IN_SESSION);
			/* Start QC 9654 R 9.4 Multi-Tab Browsing Causing Records to not be Inserted for Services and Cost Centers - add @HHSTokenValidator*/
			LOG_OBJECT.Info("======updateCostCenterEnabled==task name :: "+loCBGridBean.getTransactionName());	
			LOG_OBJECT.Debug("=====updateCostCenterEnabled===contractId : " + loCBGridBean.getContractID());
			LOG_OBJECT.Debug("=====updateCostCenterEnabled===contract type : " + loCBGridBean.getContractTypeId());
			LOG_OBJECT.Debug("=====updateCostCenterEnabled===AmendmentContractID : " + loCBGridBean.getAmendmentContractID());
			LOG_OBJECT.Debug("=====updateCostCenterEnabled===AmendmentType() : " + loCBGridBean.getAmendmentType());
			
			if(loCBGridBean.getContractTypeId().equalsIgnoreCase("1") && HHSConstants.BMC_CONTRACT_CONFIG_TASK.equalsIgnoreCase(loCBGridBean.getTransactionName()))
			{
				Channel aoChannel = new Channel();
				HashMap<String, Object> loHMResult = new HashMap<String, Object>();
				LOG_OBJECT.Debug("Base ContractId :: "+loCBGridBean.getContractID());
				aoChannel.setData(HHSConstants.CONTRACT_ID_KEY, loCBGridBean.getContractID());
				HHSTransactionManager.executeTransaction(aoChannel, HHSConstants.FETCH_BASE_CONTRACT_STATUS_ID);
				contractStatusId = (Integer) aoChannel.getData("status_id");
				LOG_OBJECT.Debug("Base Contract Status :: "+contractStatusId);

			}
						
			if(contractStatusId != 59)
			{
				proceed = false;
				String asDebugMessage = HHSConstants.PAGE_ERROR + HHSConstants.COLON + PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.DUPLICATE_SUBMISSION);
				LOG_OBJECT.Debug("message : " + asDebugMessage);
				response(asDebugMessage);
			}	
			
			if(proceed)
			{	
			/* End QC 9654 R 9.4 Multi-Tab Browsing Causing Records to not be Inserted for Services and Cost Centers - add @HHSTokenValidator*/
			
				String lsCostCenterFlag = aoResourceRequest.getParameter(HHSConstants.PARAM_KEY_OPERATION);
				String lsUserId = (String) aoResourceRequest.getPortletSession().getAttribute(
						ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
				HashMap<String, String> loHmMap = new HashMap<String, String>();
				loHmMap.put(HHSConstants.CREATED_BY_USER_ID, lsUserId);
				loHmMap.put(HHSConstants.CONTRACT_ID, loCBGridBean.getContractID());
				loHmMap.put(HHSConstants.STATUS_ID, lsCostCenterFlag);
				loHmMap.put(HHSConstants.ENTRY_TYPE_ID, HHSConstants.STRING_ELEVEN);
				loHmMap.put(HHSConstants.BUDGET_ID, loCBGridBean.getContractBudgetID());
				loHmMap.put(HHSConstants.BMC_BUDGET_FISCAL_YEAR, loCBGridBean.getFiscalYearID());
				loHmMap.put(HHSConstants.PUBLISHED, HHSConstants.ZERO);
				Channel loChannelObj = new Channel();
				loChannelObj.setData(HHSConstants.AO_HASH_MAP, loHmMap);
				HHSTransactionManager.executeTransaction(loChannelObj, HHSR5Constants.UPDATE_COST_CENTER_ENABLED,
						HHSR5Constants.TRANSACTION_ELEMENT_R5);
			} //qc9654
		}
		catch (Exception aoExe)
		{
			LOG_OBJECT.Error("Error occured in updateCostCenterEnabled method while updating Services", aoExe);
		}
	}
	//End : Added in R7 for Cost Center
	
	/* Start QC 9654 R 9.4 Multi-Tab Browsing Causing Records to not be Inserted for Services and Cost Centers*/
	//this is to override the method in BaseController
	@ResourceMapping("finishTaskApprove")
	@HHSTokenValidator
	protected void finishTaskApprove(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws ApplicationException
	{
		
		super.finishTaskApprove(aoResourceRequest, aoResourceResponse);
	}
	
	//this is to override the method in BaseController
	@ResourceMapping("finishTaskReturn")
	@HHSTokenValidator
	protected void finishTaskReturn(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws ApplicationException
	{
		super.finishTaskReturn(aoResourceRequest, aoResourceResponse);
	}
	
	
	public void response(String msg) 
	{
		PrintWriter writer = null;
		try {
			RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
			PortletRequestAttributes portletRequestAttributes = (PortletRequestAttributes) requestAttributes;
			PortletRequest portletRequest = portletRequestAttributes.getRequest();
		
			HttpServletResponse response = (HttpServletResponse)portletRequest.getAttribute(HHSConstants.JAVAX_SERVLET_RESPONSE);		
			writer = response.getWriter();		
			writer.println(msg);
		} catch (Exception e) {
			LOG_OBJECT.Error("Error on HHSAopToken response:" +e.getMessage(),e);
		} finally {
			if(writer!=null){
				writer.flush();
				writer.close();
			}
		}

	}
	
	/* End QC 9654 R 9.4 Multi-Tab Browsing Causing Records to not be Inserted for Services and Cost Centers*/

}