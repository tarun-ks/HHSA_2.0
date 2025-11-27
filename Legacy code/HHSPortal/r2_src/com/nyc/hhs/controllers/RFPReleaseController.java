package com.nyc.hhs.controllers;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.AuthenticationBean;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.CommentsHistoryBean;
import com.nyc.hhs.model.Document;
import com.nyc.hhs.model.DocumentPropertiesBean;
import com.nyc.hhs.model.ExtendedDocument;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.Navigation;
import com.nyc.hhs.model.NotificationDataBean;
import com.nyc.hhs.model.Procurement;
import com.nyc.hhs.model.ProcurementCOF;
import com.nyc.hhs.model.ProcurementInfo;
import com.nyc.hhs.model.ProposalDetailsBean;
import com.nyc.hhs.model.ProposalFilterBean;
import com.nyc.hhs.model.ProposalQuestionAnswerBean;
import com.nyc.hhs.model.RFPReleaseBean;
import com.nyc.hhs.model.SiteDetailsBean;
import com.nyc.hhs.rule.Rule;
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
import com.nyc.hhs.validator.EvaluationCriteriaDetailsValidator;
import com.nyc.hhs.validator.EvaluationCriteriaScoreValidator;
import com.nyc.hhs.validator.ProposalAnswerValidator;
import com.nyc.hhs.validator.ProposalConfigurationValidator;
import com.nyc.hhs.validator.ProposalCustomQuestionValidator;
import com.nyc.hhs.validator.ProposalDetailsValidator;

/**
 * This controller class will handle all the request made by user from any tab
 * on RFP Release Details tab, and when the same data is accessed on other
 * screens
 * 
 */
@Controller(value = "rfpReleaseHandler")
@RequestMapping("view")
public class RFPReleaseController extends BaseControllerSM
{
	/**
	 * This is the log object which will be used to log any exception thrown
	 * from service class
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(RFPReleaseController.class);

	/**
	 * This method will initialize the documentBean object
	 * 
	 * @return Document Bean Object
	 */
	@ModelAttribute("rfpDocuments")
	public ExtendedDocument getRfpDocuments()
	{
		return new ExtendedDocument();
	}

	/**
	 * @return Authentication Bean Object
	 */
	@ModelAttribute("Authentication")
	public AuthenticationBean getAuthentication()
	{
		return new AuthenticationBean();
	}

	/**
	 * @return Proposal Details Bean Object
	 */
	@ModelAttribute("ProposalDetailsBean")
	public ProposalDetailsBean getProposalDetailsBean()
	{
		return new ProposalDetailsBean();
	}

	/**
	 * This method will initialize the procurement bean object
	 * 
	 * @return Procurement Bean Object
	 */
	@ModelAttribute("RFPReleaseBean")
	public RFPReleaseBean getCommandObject()
	{
		return new RFPReleaseBean();
	}

	/**
	 * Validator Object
	 */
	@Autowired
	private Validator validator;

	/**
	 * default constructor for the validator
	 * 
	 * @param aoValidator validator Object
	 */
	public void setValidator(Validator aoValidator)
	{
		this.validator = aoValidator;
	}

	/**
	 * For proposal details page validation
	 */
	private final Validator proposalDetailsValidator = new ProposalDetailsValidator(new ProposalAnswerValidator());

	/**
	 * For proposal configuration page validation
	 */
	private final Validator proposalConfigurationValidator = new ProposalConfigurationValidator(
			new ProposalCustomQuestionValidator());

	/**
	 * For proposal configuration page validation
	 */
	private final Validator evaluationCriteriaDetailsValidator = new EvaluationCriteriaDetailsValidator(
			new EvaluationCriteriaScoreValidator());

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
		aoBinder.registerCustomEditor(Date.class, new CustomDateEditor(loFormat, false, HHSConstants.INT_TEN));
	}

	/**
	 * This method will be executed when the page will be loaded first time when
	 * user clicks on RFP Release Details tab from top level navigation and then
	 * for rendering the actions
	 * 
	 * <ul>
	 * <li>Get the procurement Id from request</li>
	 * <li>Call getPageHeader() from BaseControllerSM to get navigation page
	 * headers</li>
	 * <li>Get selected child tab</li>
	 * <li>If it is screen S204 - Financials, render to financials view</li>
	 * <li>If it is screen S209 - Proposal Configuration, call
	 * renderProposalConfiguration() which will render it to proposal
	 * configuration view</li>
	 * <ul>
	 * 
	 * @param aoRequest a render request
	 * @param aoResponse a response request
	 * @return a model and view object containing view name
	 */
	@RenderMapping
	protected ModelAndView handleRenderRequestInternal(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		ModelAndView loModelAndView = null;
		try
		{
			String lsProcurementId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
			getPageHeader(aoRequest, lsProcurementId);
			String lsSelectChildScreen = (String) aoRequest.getAttribute(HHSConstants.SELECTED_CHILD_TAB);
			String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			Channel loChannelObj = new Channel();
			loChannelObj.setData(HHSConstants.ORG_TYPE, lsUserOrgType);
			if (lsSelectChildScreen != null
					&& lsSelectChildScreen.equalsIgnoreCase(HHSConstants.SECOND_LEVEL_ID[HHSConstants.INT_TWO]))
			{
				loModelAndView = handleRenderRequestInternalForSecondLevel(aoRequest, lsProcurementId, loChannelObj);
			}
			else if (lsSelectChildScreen != null
					&& lsSelectChildScreen.equalsIgnoreCase(HHSConstants.SECOND_LEVEL_ID[HHSConstants.INT_THREE]))
			{
				loModelAndView = renderProposalConfiguration(aoRequest, aoResponse);
			}
			aoRequest.setAttribute(HHSConstants.CHANNEL_ACCESS, loChannelObj);
		}
		// Catch the application exception thrown by transaction and set the
		// error message
		// in request object and pass to jsp
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("Error occurred while processing procurements", loExp);
			setGenericErrorMessage(aoRequest);
		}
		// Catch the exception thrown by transaction and set the error message
		// in request object and pass to jsp
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error occurred while processing procurements", loExp);
			setGenericErrorMessage(aoRequest);
		}
		return loModelAndView;
	}

	/**
	 * This method is modified as part of Production Support release (2.6.0) for
	 * defect 5629 This method is used to render the default screen if the
	 * screen level is 2
	 * <ul>
	 * <li>Condition modified for procurement canceled, procurement closed and
	 * Procurement COF task canceled.</li>
	 * <li>Execute transaction <code>fetchProcurementCoF</code></li>
	 * <li>Get procurement certification of fund details from the channel object
	 * </li>
	 * <li>Depending upon various condition set the section read only in the
	 * request</li>
	 * <li>Method modified as part of release 3.2.0 enhancement 5684. The
	 * financial screen would not be available till PCOF task is done with final
	 * level approval</li>
	 * </ul>
	 * @param aoRequest Render request object
	 * @param asProcurementId procurement id
	 * @param aoChannelObj channel object
	 * @return model and view
	 * @throws ApplicationException if exception occurred
	 */
	@SuppressWarnings("unchecked")
	private ModelAndView handleRenderRequestInternalForSecondLevel(RenderRequest aoRequest, String asProcurementId,
			Channel aoChannelObj) throws ApplicationException
	{
		ModelAndView loModelAndView = null;
		String lsProcurementStatus = null;
		try
		{
			aoChannelObj.setData(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
			HHSTransactionManager.executeTransaction(aoChannelObj, HHSConstants.FETCH_PROC_COF);
			boolean lbIsOpenEndedOrZeroValue = (Boolean) aoChannelObj.getData(HHSConstants.IS_OPEN_ENDED_OR_ZERO_VALUE);
			// Start of changes for release 3.2.0 enhancement 5684
			// Fetching procurement status since only for those procurements
			// with PCOF status approved, the financial screen would be
			// available.
			// else error message would be displayed.
			lsProcurementStatus = (String) aoChannelObj.getData(HHSConstants.PROCUREMENT_STATUS_KEY);
			if (!lbIsOpenEndedOrZeroValue
					&& !lsProcurementStatus.equalsIgnoreCase(HHSConstants.EMPTY_STRING)
					&& lsProcurementStatus.equalsIgnoreCase(PropertyLoader.getProperty(
							HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PCOF_APPROVED)))
			{
				ProcurementCOF loProcurementCOF = (ProcurementCOF) aoChannelObj.getData(HHSConstants.PROC_COF_DETAILS);
				setContractDatesInSession(aoRequest.getPortletSession(), loProcurementCOF.getContractStartDate(),
						loProcurementCOF.getContractEndDate());
				Map<String, Object> loFiscalYrMap = getContractFiscalYears(aoRequest);
				int liFiscalStartYr = (Integer) loFiscalYrMap.get(HHSConstants.LI_START_YEAR);
				int liNoOfYears = (Integer) loFiscalYrMap.get(HHSConstants.LI_FYCOUNT);
				String lsAgency = String.valueOf(aoRequest.getPortletSession().getAttribute(
						ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE));
				String lsUserId = String.valueOf(aoRequest.getPortletSession().getAttribute(
						ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE));
				String lsUserRole = String.valueOf(aoRequest.getPortletSession().getAttribute(
						ApplicationConstants.KEY_SESSION_USER_ROLE, PortletSession.APPLICATION_SCOPE));
				String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
						ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
				aoChannelObj.setData(HHSConstants.USER_ROLE, lsUserRole);
				aoChannelObj.setData(HHSConstants.PROCUREMENT_STATUS, loProcurementCOF.getProcurementStatus());
				aoRequest.setAttribute(HHSConstants.CHANNEL_ACCESS, aoChannelObj);
				CBGridBean loCBGridBean = getCbGridBean(asProcurementId, liFiscalStartYr, liNoOfYears, lsAgency,
						lsUserId, lsUserOrgType);
				PortletSessionHandler.setAttribute(loCBGridBean, aoRequest, HHSConstants.CBGRIDBEAN_IN_SESSION);
				setAccountGridDataInSession(aoRequest);
				setFundingGridDataInSession(aoRequest);
				String lsAttrSuccess = (String) ApplicationSession
						.getAttribute(aoRequest, HHSConstants.SUCCESS_MESSAGE);
				String lsAttrError = (String) ApplicationSession.getAttribute(aoRequest, HHSConstants.ERROR_MESSAGE);
				aoRequest.setAttribute(HHSConstants.PROCUREMENT_ID, asProcurementId);
				aoRequest.setAttribute(HHSConstants.PROCID, asProcurementId);
				aoRequest.setAttribute(HHSConstants.CONTRACT_START_DATE, loProcurementCOF.getContractStartDate());
				aoRequest.setAttribute(HHSConstants.CONTRACT_END_DATE, loProcurementCOF.getContractEndDate());
				if (lsAttrSuccess == null)
				{
					aoRequest.setAttribute(HHSConstants.ERROR_MESSAGE, lsAttrError);
				}
				else if (lsAttrSuccess != null)
				{
					aoRequest.setAttribute(HHSConstants.SUCCESS_MESSAGE, lsAttrSuccess);
				}
				// Financials page to be editable only for same agency and when
				// PCOF status != In Review
				aoRequest.setAttribute(HHSConstants.READ_ONLY_SEC, HHSConstants.FALSE);
				if (!(lsAgency.equalsIgnoreCase(loProcurementCOF.getAgencyId())))
				{
					aoRequest.setAttribute(HHSConstants.READ_ONLY_SEC, HHSConstants.TRUE);
				}
				// The below else if condition is modified as per defect 5629
				// for release 2.6.0
				else if (HHSConstants.TASK_CANCELLED.equalsIgnoreCase(loProcurementCOF.getStatus())
						|| HHSConstants.STATUS_CLOSED.equalsIgnoreCase(loProcurementCOF.getStatus())
						|| HHSConstants.TASK_CANCELLED.equalsIgnoreCase(loProcurementCOF.getProcurementStatus()))
				{
					aoRequest.setAttribute(HHSConstants.READ_ONLY_SEC, HHSConstants.TRUE);
				}
				else if (lsAgency.equalsIgnoreCase(loProcurementCOF.getAgencyId())
						&& HHSConstants.TASK_IN_REVIEW.equalsIgnoreCase(loProcurementCOF.getProcurementStatus()))
				{
					aoRequest.setAttribute(HHSConstants.READ_ONLY_SEC, HHSConstants.TRUE);
				}
				else if (lsAgency.equalsIgnoreCase(loProcurementCOF.getAgencyId())
						&& (!(lsUserRole.equalsIgnoreCase(HHSConstants.FINANCE_MANAGER_ROLE) || lsUserRole
								.equalsIgnoreCase(HHSConstants.CFO_ROLE)))
						&& HHSConstants.STATUS_APPROVED.equalsIgnoreCase(loProcurementCOF.getProcurementStatus()))
				{
					aoRequest.setAttribute(HHSConstants.READ_ONLY_SEC, HHSConstants.TRUE);
				}
				loModelAndView = new ModelAndView(HHSConstants.FINANCIALS, HHSConstants.PROC_COF, loProcurementCOF);
			}
			else if (lbIsOpenEndedOrZeroValue)
			{
				aoRequest.setAttribute(HHSConstants.FINANCE_OPEN_ZERO, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGE_PROP_FILE, HHSConstants.FINANCE_OPEN_ZERO));
				aoRequest.setAttribute(HHSConstants.FINANCE_SCREEN_CHECK, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGE_PROP_FILE, HHSConstants.FINANCE_SCREEN_CHECK));
				loModelAndView = new ModelAndView(HHSConstants.FINANCIALS_MESSAGE);
			}
			else
			{
				aoRequest.setAttribute(HHSConstants.FINANCE_SCREEN_CHECK, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGE_PROP_FILE, HHSConstants.FINANCE_SCREEN_CHECK));
				loModelAndView = new ModelAndView(HHSConstants.FINANCIALS_MESSAGE);
			}
			// End of changes for release 3.2.0 enhancement 5684
		}
		// Catch the application exception thrown by transaction and set the
		// error message
		// in request object and pass to jsp
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error occurred while processing procurements", aoExp);
			throw new ApplicationException("Error occurred while processing procurements", aoExp);
		}
		return loModelAndView;
	}

	/**
	 * This method is used to get CB grid bean for input procrement Id, fiscal
	 * year, number of years, agency, user ID and user org type
	 * 
	 * @param asProcurementId a string value of procurement Id
	 * @param aiFiscalStartYr an integer vaue of fiscal year
	 * @param aiNoOfYears an integer value of number of years
	 * @param asAgency a string value of user organization
	 * @param asUserId a string value of user Id
	 * @param asUserOrgType a string value of user organization type
	 * @return CBGridBean object
	 */
	private CBGridBean getCbGridBean(String asProcurementId, int aiFiscalStartYr, int aiNoOfYears, String asAgency,
			String asUserId, String asUserOrgType)
	{
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setProcurementID(asProcurementId);
		loCBGridBean.setFiscalYearID(String.valueOf(aiFiscalStartYr));
		loCBGridBean.setNoOfyears(aiNoOfYears);
		loCBGridBean.setCreatedByUserId(asUserId);
		loCBGridBean.setModifiedByUserId(asUserId);
		loCBGridBean.setAgencyId(asAgency);
		if (ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(asUserOrgType))
		{
			loCBGridBean.setModifyByProvider(asUserId);
		}
		else
		{
			loCBGridBean.setModifyByAgency(asUserId);
		}
		return loCBGridBean;
	}

	/**
	 * This method is used to render competition configuration screen when
	 * Competition Configuration tab is clicked
	 * 
	 * <ul>
	 * <li>1.Get the procurement Id from request</li>
	 * <li>2.Get navigation details by calling getPageHeader() for input
	 * procurement Id</li>
	 * <li>3.Set channel objects and execute transaction
	 * <b>getCompetitionPoolData</b> to get competition pool details</li>
	 * <li>4.Set transaction output in request and render to
	 * competitionPoolConfiguration jsp</li>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * @param aoRequest RenderRequest object
	 * @param aoResponse RenderResponse object
	 * @return ModelAndView containing jsp name
	 */
	@SuppressWarnings("unchecked")
	@RenderMapping(params = "render_action=displayCompetitionConfiguration")
	protected ModelAndView renderCompetitionConfiguration(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		ModelAndView loModelAndView = null;
		try
		{
			Channel loChannelObj = new Channel();
			String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			loChannelObj.setData(HHSConstants.ORG_TYPE, lsUserOrgType);
			String lsProcurementId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
			// if the procurement id in the request is not null
			if (lsProcurementId != null)
			{
				Integer loProcurementPlannedStatus = Integer.valueOf(PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_PLANNED));
				getPageHeader(aoRequest, lsProcurementId);
				ProcurementInfo loProcurementBean = (ProcurementInfo) aoRequest
						.getAttribute(HHSConstants.PROCUREMENT_BEAN);
				Channel loChannel = new Channel();
				loChannel.setData(HHSConstants.PROCUREMENT_ID, lsProcurementId);
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.GET_COMP_POOL_DATA);
				List<String> loSelectedPool = (List<String>) loChannel.getData(HHSConstants.SELECTED_POOL);
				aoRequest.setAttribute(HHSConstants.SELECTED_POOL, loSelectedPool);
				Integer loProcurementAddendumDataCount = (Integer) loChannel
						.getData(HHSConstants.PROC_ADDENDUM_DATA_COUNT);

			    //[Start] R6.3 QC6627 add ability to create new competition pool and delete during addendum.
				if(loProcurementAddendumDataCount == null || loProcurementAddendumDataCount == 0) {
					loProcurementBean.setProcurementAddendumFlag(0);
				}else{
					loProcurementBean.setProcurementAddendumFlag(loProcurementAddendumDataCount);
				}
			    //[End] R6.3 QC6627 add ability to create new competition pool and delete during addendum.

				if (loProcurementAddendumDataCount != null && loProcurementAddendumDataCount > 0
						&& !loProcurementBean.getStatus().equals(loProcurementPlannedStatus))
				{
					aoRequest.setAttribute(HHSConstants.UNPUBLISHED_DATA_MSG_KEY, HHSConstants.UNPUBLISHED_DATA_MSG);
				}
			}
			else
			{
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.GENERIC_ERROR_MESSAGE));
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
			}
			aoRequest.setAttribute(HHSConstants.CHANNEL_ACCESS, loChannelObj);
		}
		// Catch the exception thrown by transaction and set the error message
		// in request object and pass to jsp
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error Occured while fetching connection pool details", aoExp);
			setGenericErrorMessage(aoRequest);
		}
		// Catch the exception thrown by transaction and set the error message
		// in request object and pass to jsp
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error Occured while fetching connection pool details", aoExp);
			setGenericErrorMessage(aoRequest);
		}
		loModelAndView = new ModelAndView(HHSConstants.COMPETITION_POOL_CONFIGURATION);
		return loModelAndView;
	}

	/**
	 * This method is used to save competition pool details added for given
	 * procurement Id
	 * 
	 * <ul>
	 * <li>1.Get the procurement Id and competition pool list from request</li>
	 * <li>2.Set required attributes in channel</li>
	 * <li>3.Execute transaction <b>saveCompetitionPool</b> to save competition
	 * pool details</li>
	 * <li>Set the required parameters in the response object</li>
	 * <li>4.Set navigation parameters in Render</li>
	 * 
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse object
	 */
	@ActionMapping(params = "submit_action=saveCompetitionPool")
	protected void saveCompetitionPool(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		PortletSession loSession = null;
		String lsProcurementId = null;
		String lsUserId = null;
		try
		{
			loSession = aoRequest.getPortletSession();
			lsProcurementId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
			lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);
			String loSelectedPool[] = aoRequest.getParameterValues(HHSConstants.SELECTED_COMP_POOLS);
			List<String> loSelectedPoolList;
			if (loSelectedPool == null)
			{
				loSelectedPoolList = new ArrayList<String>();
			}
			else
			{
				loSelectedPoolList = Arrays.asList(loSelectedPool);
			}
			Channel loChannel = new Channel();
			loChannel.setData(HHSConstants.AS_USER_ID, lsUserId);
			loChannel.setData(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			loChannel.setData(HHSConstants.SELECTED_POOL, loSelectedPoolList);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.SAVE_COMP_POOL);
		}
		// Catch the application exception log it
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error Occured while saving connection pool details", aoAppEx);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
		// Catch the exception thrown from the transaction layer and log it
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error Occured while saving connection pool details", aoExp);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
		aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
		aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.DISPLAY_COMP_POOL_CONF);
		aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, lsProcurementId);
		setNavigationParamsInRender(aoRequest, aoResponse);
	}

	/**
	 * This method will be executed when the page will be loaded first time
	 * 
	 * This Method will get the procurement Id by calling
	 * <b>parseQueryString</b> method of <b>HHSPortalUtil</b> class from request
	 * object and with this id it will execute transaction with id
	 * <b>getRfpDocuments_db</b> to get the document list from database. Then it
	 * will set the list into the request. and based on the value set on the
	 * request jsp page will be displayed to the end user
	 * 
	 * Updated Method in R4
	 * 
	 * @param aoRequest RenderRequestObject
	 * @param aoResponse RenderResponseObject
	 * @return ModelAndView containing details of the page to be displayed to
	 *         the end user
	 */
	@RenderMapping(params = "render_action=displayRFPDocumentList")
	protected ModelAndView renderDisplayRFPDocumentList(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		ModelAndView loModelAndView = null;
		PortletSession loSession = aoRequest.getPortletSession();
		Channel loChannelObj = new Channel();
		boolean lbIsUploadAllowed = false;
		ExtendedDocument loRfpDocumentBean = new ExtendedDocument();
		List<ExtendedDocument> loRfpDocumentList = new ArrayList<ExtendedDocument>();
		HashMap<String, String> loRequiredParamMap = new HashMap<String, String>();
		try
		{
			String lsUserOrgType = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
					PortletSession.APPLICATION_SCOPE);
			P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			String lsProcurementId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
			loChannelObj.setData(HHSConstants.ORG_TYPE, lsUserOrgType);
			// if the procurement id in the request is not null
			if (lsProcurementId != null)
			{
				getPageHeader(aoRequest, lsProcurementId);
				String lsSelectChildScreen = (String) aoRequest.getAttribute(HHSConstants.SELECTED_CHILD_TAB);
				ProcurementInfo loProcurementBeanObj = (ProcurementInfo) aoRequest
						.getAttribute(HHSConstants.PROCUREMENT_BEAN);
				if (lsSelectChildScreen != null
						&& (lsSelectChildScreen.equalsIgnoreCase(HHSConstants.RFP_DOC) || lsSelectChildScreen
								.equalsIgnoreCase(HHSConstants.RFP_DOCUMENTS)))
				{
					loRfpDocumentList = getProcurementDocList(aoRequest, loChannelObj, loRfpDocumentBean,
							loRequiredParamMap, lsUserOrgType, loUserSession, lsProcurementId, loProcurementBeanObj);
					lbIsUploadAllowed = Boolean.valueOf((String) Rule.evaluateRule(HHSConstants.RFP_DOC_UPLOAD_FILE,
							loChannelObj));
				}
			}
			// if the procurement id in the request is null
			else
			{
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.GENERIC_ERROR_MESSAGE));
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
			}
			aoRequest.setAttribute(ApplicationConstants.SESSION_DOCUMENT_LIST, loRfpDocumentList);
			aoRequest.setAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE, lsUserOrgType);
			aoRequest.setAttribute(HHSConstants.SHOW_UPLOAD_DOC, lbIsUploadAllowed);
			// If there is something in the error message attribute in the
			// session
			if (null != loSession.getAttribute(ApplicationConstants.ERROR_MESSAGE, PortletSession.APPLICATION_SCOPE))
			{
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, (String) loSession.getAttribute(
						ApplicationConstants.ERROR_MESSAGE, PortletSession.APPLICATION_SCOPE));
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, (String) loSession.getAttribute(
						ApplicationConstants.ERROR_MESSAGE_TYPE, PortletSession.APPLICATION_SCOPE));
				loSession.removeAttribute(ApplicationConstants.ERROR_MESSAGE, PortletSession.APPLICATION_SCOPE);
				loSession.removeAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, PortletSession.APPLICATION_SCOPE);
			}
			// if the success attribute in the session is not null and equals to
			// success
			if (null != PortalUtil.parseQueryString(aoRequest, HHSConstants.SUCCESS)
					&& PortalUtil.parseQueryString(aoRequest, HHSConstants.SUCCESS).equalsIgnoreCase(
							HHSConstants.UPLOAD))
			{
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
						ApplicationConstants.ERROR_MESSAGE_PROP_FILE, HHSConstants.FILE_UPLOAD_PASS_MESSAGE));
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_PASS_TYPE);
			}
			aoRequest.setAttribute(HHSConstants.CHANNEL_ACCESS, loChannelObj);
		}
		// Catch the exception thrown by transaction and set the error message
		// in request object and pass to jsp
		catch (ApplicationException aoExp)
		{
			String lsErrorMsg = aoExp.toString();
			lsErrorMsg = lsErrorMsg.substring(lsErrorMsg.lastIndexOf(':') + HHSConstants.INT_ONE, lsErrorMsg.length())
					.trim();
			if (lsErrorMsg.isEmpty())
			{
				lsErrorMsg = HHSConstants.ERROR_WHILE_PROCESSING_REQUEST;
			}
			loSession.setAttribute(ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
					ApplicationConstants.DOCUMENT_EXCEPTION, PortletSession.APPLICATION_SCOPE);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MAP, aoExp.getContextData());
			loSession.removeAttribute(ApplicationConstants.ERROR_MESSAGE);
			loSession.removeAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE);
			LOG_OBJECT.Error(lsErrorMsg, aoExp);
		}
		loModelAndView = new ModelAndView(HHSConstants.RFP_REL_DOC, HHSConstants.BASE_RFP_DOCUMENTS,
				new ExtendedDocument());
		return loModelAndView;
	}

	/**
	 * This method modified as a part of Release 2.6.0 enhancement 5571
	 * 
	 * This method is used to get the procurement document list for the selected
	 * procurement
	 * <ul>
	 * <li>get the procurement id from the request</li>
	 * <li>Execute transaction <code>getRfpDocuments_db</code></li>
	 * <li>If the logged in user is provider refine the filter accordingly</li>
	 * <li>instead of FINAL_RFP_DOC_LIST, sorted list FINAL_SORTED_RFP_DOC_LIST
	 * is being used to display documents of type other than "RFP" and "Addenda"
	 * in ascending order</li>
	 * </ul>
	 * @param aoRequest render request object
	 * @param aoChannelObj channel object to execute transaction
	 * @param aoRfpDocumentBean document bean object
	 * @param aoRequiredParamMap required parameter map to display
	 * @param asUserOrgType user organization type
	 * @param aoUserSession filenet session bean object
	 * @param asProcurementId procurement id
	 * @param aoProcurementBeanObj procurement bean object
	 * @return list of extended documents bean
	 * @throws ApplicationException if any exception occurred
	 */
	@SuppressWarnings("unchecked")
	private List<ExtendedDocument> getProcurementDocList(RenderRequest aoRequest, Channel aoChannelObj,
			ExtendedDocument aoRfpDocumentBean, HashMap<String, String> aoRequiredParamMap, String asUserOrgType,
			P8UserSession aoUserSession, String asProcurementId, ProcurementInfo aoProcurementBeanObj)
			throws ApplicationException
	{
		List<ExtendedDocument> loRfpDocumentList = null;
		ExtendedDocument loExtendedDocument = null;
		Integer loProcurementPlannedStatusID = null;
		try
		{
			aoRfpDocumentBean.setProcurementId(asProcurementId);
			aoRfpDocumentBean.setOrganizationType(asUserOrgType);
			aoChannelObj.setData(HHSConstants.DOCUMENT_BEAN, aoRfpDocumentBean);
			setRequiredParam(aoRequiredParamMap);
			aoChannelObj.setData(HHSConstants.HM_REQIRED_PROPERTY_MAP, aoRequiredParamMap);
			aoChannelObj.setData(HHSConstants.AO_FILENET_SESSION, aoUserSession);
			aoChannelObj.setData(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
			// added to fetch provider status
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_ID, PortletSession.APPLICATION_SCOPE);
			aoChannelObj.setData(HHSConstants.PROVIDER_ID_KEY, lsUserId);
			HHSTransactionManager.executeTransaction(aoChannelObj, HHSConstants.GET_RFP_DOCS);
			Map<Object, Object> loProviderWidgetData = (Map<Object, Object>) aoChannelObj
					.getData(HHSConstants.LO_PROVIDER_MAP);
			if (loProviderWidgetData != null && !loProviderWidgetData.isEmpty())
			{
				aoRequest.setAttribute(HHSConstants.PROVIDER_STATUS_ID,
						Integer.parseInt(loProviderWidgetData.get(HHSConstants.PROVIDER_STATUS).toString()));
			}
			Integer loProcurementAddendumDataCount = (Integer) aoChannelObj
					.getData(HHSConstants.PROC_ADDENDUM_DATA_COUNT);
			loProcurementPlannedStatusID = Integer.parseInt(PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_PLANNED));
			if (loProcurementAddendumDataCount != null && loProcurementAddendumDataCount > HHSConstants.INT_ZERO
					&& !aoProcurementBeanObj.getStatus().equals(loProcurementPlannedStatusID))
			{
				aoRequest.setAttribute(HHSConstants.UNPUBLISHED_DATA_MSG_KEY, HHSConstants.UNPUBLISHED_DATA_MSG);
			}
			// below line is modified as a part of Release 2.6.0 enhancement
			// 5571
			// instead of FINAL_RFP_DOC_LIST, sorted list
			// FINAL_SORTED_RFP_DOC_LIST is being used,
			// to display documents of type other than "RFP" and "Addenda" in
			// ascending order
			loRfpDocumentList = (List<ExtendedDocument>) aoChannelObj.getData(HHSConstants.FINAL_SORTED_RFP_DOC_LIST);

           // Start QC9665 R 9.3.2 Citywide cyber assessment program (CCAP) - vulnerabilities -- Users Can Access Other User's Uploaded Documents
			aoRequest.getPortletSession().setAttribute(ApplicationConstants.SESSION_EXTENDED_DOCUMENT_LIST, loRfpDocumentList, PortletSession.APPLICATION_SCOPE);
			LOG_OBJECT.Info("save Document List in Session on Application scope");
			//End QC9665 R 9.3.2 Citywide cyber assessment program (CCAP) - vulnerabilities -- Users Can Access Other User's Uploaded Documents


			// if the logged in user belongs to the provider
			// organization type
			if (ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(asUserOrgType) && null != loRfpDocumentList
					&& loRfpDocumentList.size() > HHSConstants.INT_ZERO)
			{
				for (int liCount = HHSConstants.INT_ZERO; liCount < loRfpDocumentList.size(); liCount++)
				{
					loExtendedDocument = loRfpDocumentList.get(liCount);
					// if the document is of addendum type
					if (HHSConstants.ONE.equalsIgnoreCase(loExtendedDocument.getAddendumType()))
					{
						loRfpDocumentList.remove(loExtendedDocument);
						liCount--;
					}
				}
			}
			aoChannelObj.setData(HHSConstants.PROCUREMENT_STATUS, aoProcurementBeanObj.getProcurementStatus());
			aoChannelObj.setData(HHSConstants.ORGTYPE, asUserOrgType);
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error Occured while fetching Procurement Documents", aoAppEx);
			throw aoAppEx;
		}
		return loRfpDocumentList;
	}

	/**
	 * This method is modified to resolve
	 * "BEA-000000-Could not complete request" error in AdminServer logs This
	 * method executed when user click on upload document link from the document
	 * vault home screen
	 * <ul>
	 * <li>Get the document Category value from Request Object</li>
	 * <li>Depending on the document Category value Below logic</li>
	 * <li>If DocumentCategory is null<br/>
	 * Then<br/>
	 * Read document property details xml from cache Get the document Categories
	 * for the Organization Else<br/>
	 * Get the Document Type list according to the Selected Document Category</li>
	 * <li>Updated Method in R4</li>
	 * <li>call to method "renderProcurementSuccessMessage"nothing is commented
	 * as nothing returned in Model and View which was causing BEA-000000 error</li>
	 * </ul>
	 * 
	 * Changes done for Enhancement #6429 for Release 3.4.0
	 * 
	 * @param aoRequest action request object
	 * @param aoResponse action response object
	 * 
	 */
	@Override
	@ActionMapping(params = "submit_action=uploadFile")
	protected void documentFinalUploadAction(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		PortletSession loSession = null;
		String lsDocumentId = null;
		String lsProcurementId = null;
		String lsUserOrgType = null;
		String lsProposalId = null;
		String lsUploadingDocType = null;
		String lsAwardId = null;
		String lsReplacingDocumentId = null;
		String lsOrganizationId = null;
		String lsIsAddendumDoc = null;
		// Start || Changes done for Enhancement #6429 for Release 3.4.0
		String lsEvalPoolMappingId = null;
		String lsIsFinancials = null;
		String lsContractId = null;
		// End || Changes done for Enhancement #6429 for Release 3.4.0
		try
		{
			loSession = aoRequest.getPortletSession();
			// Start || Changes done for Enhancement #6429 for Release 3.4.0
			lsEvalPoolMappingId = aoRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID);
			lsIsFinancials = aoRequest.getParameter(HHSConstants.IS_FINANCIAL);
			lsContractId = aoRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW);
			Document loDocument = (Document) ApplicationSession.getAttribute(aoRequest, true,
					ApplicationConstants.SESSION_DOCUMENT_OBJ);
			// End || Changes done for Enhancement #6429 for Release 3.4.0
			lsProcurementId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
			lsProposalId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROPOSAL_ID);
			lsAwardId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.AWARD_ID);
			lsUploadingDocType = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.UPLOAD_DOC_TYPE);
			lsDocumentId = FileNetOperationsUtils.actionFileUpload(aoRequest, aoResponse);
			lsIsAddendumDoc = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.IS_ADDENDUM_DOC);
			lsUserOrgType = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
					PortletSession.APPLICATION_SCOPE);
			lsOrganizationId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
					PortletSession.APPLICATION_SCOPE);
			// Start || Changes done for Enhancement #6429 for Release 3.4.0
			if (null != aoRequest.getParameter(HHSConstants.UPLOAD_DOC_TYPE)
					&& aoRequest.getParameter(HHSConstants.UPLOAD_DOC_TYPE).equalsIgnoreCase(
							HHSConstants.STRING_AWARD_DOC))
			{
				lsOrganizationId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.ORGANIZATION_ID);
			}
			// End || Changes done for Enhancement #6429 for Release 3.4.0
			String lsUserName = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);
			P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			String lsDocRefNum = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.HIDDEN_DOC_REF_SEQ_NO);
			lsReplacingDocumentId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.REPLACING_DOCUMENT_ID);
			// if the document id not null means the document is uploaded
			// successfully to filenet
			if (lsDocumentId != null)
			{
				// Start || Changes done for Enhancement #6429 for Release 3.4.0
				insertDocumentDetailsInDBOnUpload(aoRequest, lsDocumentId, lsProcurementId, lsProposalId, lsAwardId,
						lsContractId, lsUserOrgType, lsUserName, loUserSession, lsDocRefNum, lsUploadingDocType,
						lsReplacingDocumentId, lsOrganizationId, lsIsAddendumDoc, lsEvalPoolMappingId, loDocument);
				// End || Changes done for Enhancement #6429 for Release 3.4.0
			}
			else
			{
				String lsMessage = PropertyLoader.getProperty(ApplicationConstants.ERROR_MESSAGE_PROP_FILE,
						HHSConstants.FILE_UPLOAD_FAIL_MESSAGE);
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, lsMessage);
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE);
			}
			// Start || Changes done for Enhancement #6429 for Release 3.4.0
			if (null != lsUploadingDocType
					&& (HHSConstants.AWARD_UPPER_CASE.equalsIgnoreCase(lsUploadingDocType) || HHSConstants.STRING_AWARD_DOC
							.equalsIgnoreCase(lsUploadingDocType)))
			// End || Changes done for Enhancement #6429 for Release 3.4.0
			{
				aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.SEL_DETAIL);
			}
			else
			{
				aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
			}

			aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			// Start || Changes done for Enhancement #6429 for Release 3.4.0
			aoResponse.setRenderParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolMappingId);
			aoResponse.setRenderParameter(HHSConstants.UPLOAD_DOC_TYPE, lsUploadingDocType);
			aoResponse.setRenderParameter(HHSConstants.ORGANIZATION_ID, lsOrganizationId);
			if (null != lsIsFinancials)
			{
				aoResponse.setRenderParameter(HHSConstants.IS_FINANCIAL, lsIsFinancials);
			}
			if (null != lsContractId)
			{
				aoResponse.setRenderParameter(HHSConstants.CONTRACT_ID_WORKFLOW, lsContractId);
			}
			// End || Changes done for Enhancement #6429 for Release 3.4.0
			setNavigationParamsInRender(aoRequest, aoResponse);
		}
		// Catch the application exception log it
		catch (ApplicationException aoAppEx)
		{
			try
			{
				setErrorMessageInResponse(aoRequest, aoResponse, aoAppEx, HHSConstants.UPLOADING_FILE_INFO);
			}
			catch (IOException aoIoExp)
			{
				setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
						ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
			}
		}
		// Catch the exception thrown from the transaction layer and log it
		catch (Exception aoExp)
		{
			try
			{
				setErrorMessageInResponse(aoRequest, aoResponse, aoExp, HHSConstants.UPLOADING_FILE_INFO);
			}
			catch (IOException aoIoExp)
			{
				setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
						ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
			}
		}

	}

	/**
	 * This method will insert the details of the uploaded document to the
	 * corresponding table This method is modified to fix 5523 in release 2.6.0.
	 * An extra attribute LS_IS_REQUIRED_DOC is fetched from session & sets in
	 * channel
	 * <ul>
	 * <li>It will execute two different transactions depending upon the type of
	 * the organization of the user</li>
	 * <li>If the organization type is Provider the execute the transaction
	 * <b>insertRfpDocumentDetails_db</b></li>
	 * <li>If the organization type is City/Agency then execute the transaction
	 * <b>insertProposalDocumentDetails_db</b></li>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * Changes done for Enhancement #6429 for Release 3.4.0
	 * 
	 * @param aoRequest request object
	 * @param asDocumentId document Id
	 * @param asProcurementId procurement id
	 * @param asProposalId proposal id
	 * @param asAwardId award id
	 * @param asUserOrgType user organization type
	 * @param asUserName user name
	 * @param aoUserSession p8 filenet session
	 * @param asDocRefNum doc reference number
	 * @param asUploadingDocType uploading document type
	 * @param asReplacingDocumentId replacing document id
	 * @param asOrganizationId Organization Id
	 * @param asIsAddendumDoc Is document in addendum state
	 * @throws ApplicationException if any exception occurred
	 */
	private void insertDocumentDetailsInDBOnUpload(ActionRequest aoRequest, String asDocumentId,
			String asProcurementId, String asProposalId, String asAwardId, String asContractId, String asUserOrgType,
			String asUserName, P8UserSession aoUserSession, String asDocRefNum, String asUploadingDocType,
			String asReplacingDocumentId, String asOrganizationId, String asIsAddendumDoc, String asEvalPoolMappingId,
			Document aoDocument) throws ApplicationException
	{
		try
		{
			// 5523 fix starts
			Boolean loIsRequiredDoc = null;
			if (PortletSessionHandler.getAttribute(aoRequest, true, HHSConstants.LS_IS_REQUIRED_DOC) != null)
			{
				loIsRequiredDoc = (Boolean) PortletSessionHandler.getAttribute(aoRequest, false,
						HHSConstants.LS_IS_REQUIRED_DOC);
			}
			// 5523 fix ends
			String lsOrgName = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_NAME, PortletSession.APPLICATION_SCOPE);
			Map<String, Object> loParameterMap = null;
			Channel loChannel = new Channel();
			HashMap<String, Object> loHmDocReqProps = new HashMap<String, Object>();
			loChannel.setData(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
			loParameterMap = RFPReleaseDocsUtil.getDocumentInfo(aoUserSession, asUserOrgType, asDocumentId);
			Date loCreatedDate = (Date) loParameterMap.get(P8Constants.PROPERTY_CE_DATE_CREATED);
			loParameterMap.put(HHSConstants.PROCUREMENT_ID, asProcurementId);
			loParameterMap.put(HHSConstants.PROPOSAL_ID, asProposalId);
			loParameterMap.put(HHSConstants.DOC_ID, asDocumentId);
			loParameterMap.put(HHSConstants.DOC_CREATED_DATE, loCreatedDate);
			loParameterMap.put(HHSConstants.USER_ID, asUserName);
			loParameterMap.put(HHSConstants.MOD_BY_USER_ID, asUserName);
			loParameterMap.put(HHSConstants.DOC_REF_NO, asDocRefNum);
			loParameterMap.put(HHSConstants.REPLACING_DOCUMENT_ID, asReplacingDocumentId);
			loParameterMap.put(HHSConstants.IS_ADDENDUM_DOC, asIsAddendumDoc);
			loChannel.setData(HHSConstants.AO_PARAMETER_MAP, loParameterMap);
			loHmDocReqProps.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, true);
			loHmDocReqProps.put(HHSConstants.IS_DOCUMENT_RFP_AWARD_TYPE, true);
			loChannel.setData(HHSConstants.DOCUMENT_TYPE, HHSConstants.EMPTY_STRING);
			loChannel.setData(HHSConstants.DOC_ID, asDocumentId);
			loChannel.setData(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
			loChannel.setData(HHSConstants.LO_HM_DOC_REQ_PROPS, loHmDocReqProps);
			loChannel.setData(HHSConstants.AO_FILENET_SESSION, aoUserSession);
			loChannel.setData(HHSConstants.LO_LAST_MOD_HASHMAP, loParameterMap);
			loChannel.setData(HHSConstants.LB_SUCCESS_STATUS, true);
			loChannel.setData(HHSConstants.REPLACING_DOCUMENT_ID, asReplacingDocumentId);
			// NT 219 fix
			loChannel.setData(HHSConstants.ORGA_ID, asOrganizationId);
			// 5523 fix starts
			loChannel.setData(HHSConstants.LO_IS_REQUIRED_DOC, loIsRequiredDoc);
			// 5523 fix ends
			String lsTransactionName = HHSUtil.getTransactionNameInsertDocumentDetailsInDBOnUpload(asUserOrgType,
					asUploadingDocType, loParameterMap, asProposalId, asAwardId, asOrganizationId);
			if (null != lsTransactionName && lsTransactionName.equals(HHSConstants.INS_AWARD_DOC_DETAILS_DB))
			{
				// set attributes for sending notification for award documents
				HashMap<String, Object> loNotificationMap = getNotificationMapForAwardDocuments(aoRequest, lsOrgName);
				loChannel.setData(HHSConstants.LO_HM_NOTIFY_PARAM, loNotificationMap);
				loChannel.setData(HHSConstants.AWARD_ID, asAwardId);
			}
			// Start || Changes done for Enhancement #6429 for Release 3.4.0
			if (null != lsTransactionName && lsTransactionName.equals(HHSConstants.INSERT_AGENCY_AWARD_DOCS_DETAILS))
			{
				// set attributes for sending notification for agency award
				// documents
				HashMap<String, String> loDocPropsMap = new HashMap<String, String>();
				loDocPropsMap.put(HHSConstants.PROCUREMENT_ID, asProcurementId);
				loDocPropsMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvalPoolMappingId);
				loDocPropsMap.put(HHSConstants.USER_ID, asUserName);
				loDocPropsMap.put(HHSConstants.STATUS_COLUMN, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.DOCUMENT_SUBMITTED));
				loDocPropsMap.put(HHSConstants.DOC_ID, asDocumentId);
				loDocPropsMap.put(HHSConstants.DOC_CATEGORY_LOWERCASE, aoDocument.getDocCategory());
				loDocPropsMap.put(HHSConstants.DOCTYPE, aoDocument.getDocType());
				loDocPropsMap.put(HHSConstants.AWARD_ID, asAwardId);
				loDocPropsMap.put(HHSConstants.ORGANIZATION_ID, asOrganizationId);
				loHmDocReqProps = new HashMap<String, Object>();
				loHmDocReqProps.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, true);
				loHmDocReqProps.put(HHSConstants.IS_DOCUMENT_RFP_AWARD_TYPE, false);
				loChannel.setData(HHSConstants.DOC_ID, asDocumentId);
				loChannel.setData(HHSConstants.LO_HM_DOC_REQ_PROPS, loHmDocReqProps);
				loChannel.setData(HHSConstants.DOC_PROPS_MAP, loDocPropsMap);
				loChannel.setData(HHSConstants.DOCUMENT_TYPE, aoDocument.getDocType());
			}
			// End || Changes done for Enhancement #6429 for Release 3.4.0
			HHSTransactionManager.executeTransaction(loChannel, lsTransactionName);
		}
		// catch the application thrown from the transaction layer and propagate
		// ahead
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("ApplicationException during adding file ", aoExp);
			throw aoExp;
		}
	}

	/**
	 * This method is used to get the details properties of the selected
	 * document.
	 * <ul>
	 * <li>Execute <b>actionFileInformation</b> Method of
	 * <b>FileNetOperationsUtils</b> class</li>
	 * <li>get all the meta data details required for the document from doctype
	 * configuration file</li>
	 * <li>populate second tab on the upload screen according to the number of
	 * the required meta data</li>
	 * <li>Get the document bean from the ApplicationSession and set it in
	 * Request</li>
	 * <li>Set the render action parameter</li>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * @param aoActionRequestForFileInfo ActionRequest Object
	 * @param aoActionResponseForFileInfo ActionResponse Object
	 * 
	 */
	@Override
	@ActionMapping(params = "submit_action=uploadingFileInformation")
	public void displayUploadingFileInformationAction(ActionRequest aoActionRequestForFileInfo,
			ActionResponse aoActionResponseForFileInfo)
	{
		String lsReplacingdocId = null;
		try
		{
			String lsUploadProcess = HHSPortalUtil.parseQueryString(aoActionRequestForFileInfo,
					HHSConstants.UPLOAD_PROCESS);
			lsReplacingdocId = HHSPortalUtil.parseQueryString(aoActionRequestForFileInfo,
					HHSConstants.REPLACING_DOCUMENT_ID);
			String lsIsAddendumDoc = HHSPortalUtil.parseQueryString(aoActionRequestForFileInfo,
					HHSConstants.IS_ADDENDUM_DOC);
			// below if will be executed when a logged in user belongs
			// to city organization
			PortletSession loSession = aoActionRequestForFileInfo.getPortletSession();
			String lsUserOrgType = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
					PortletSession.APPLICATION_SCOPE);
			String lsUploadingDocumentType = HHSPortalUtil.parseQueryString(aoActionRequestForFileInfo,
					HHSConstants.UPLOAD_DOC_TYPE);
			if (HHSConstants.BAFO.equalsIgnoreCase(lsUploadingDocumentType))
			{
				lsUserOrgType = HHSConstants.PROVIDER_ORG;
			}
			FileNetOperationsUtils.actionFileInformation(aoActionRequestForFileInfo, aoActionResponseForFileInfo);
			String lsErrorMessageInSession = (String) loSession.getAttribute(ApplicationConstants.MESSAGE,
					PortletSession.APPLICATION_SCOPE);
			String lsErrorMessageTypeInSession = (String) loSession.getAttribute(ApplicationConstants.MESSAGE_TYPE,
					PortletSession.APPLICATION_SCOPE);
			// if there is no message set in the message attribute of the
			// session
			if ((null == lsErrorMessageInSession || lsErrorMessageInSession.isEmpty())
					|| lsErrorMessageInSession.contains(HHSConstants.SAME_FILE_ERROR_MESSAGE_CHECK))
			{
				Document loUploadingDocObj = (Document) ApplicationSession.getAttribute(aoActionRequestForFileInfo,
						true, ApplicationConstants.SESSION_DOCUMENT_OBJ);
				displayUploadingFileInfoActionFinal(aoActionRequestForFileInfo, aoActionResponseForFileInfo,
						lsReplacingdocId, lsUploadProcess, lsIsAddendumDoc, lsUserOrgType, lsUploadingDocumentType,
						lsErrorMessageInSession, lsErrorMessageTypeInSession, loUploadingDocObj);
				loSession.removeAttribute(HHSConstants.MESSAGE, PortletSession.APPLICATION_SCOPE);
				loSession.removeAttribute(HHSConstants.MESSAGE_TYPE, PortletSession.APPLICATION_SCOPE);
			}
		}
		// Catch the Application exception thrown by transaction and set the
		// error message
		// in request object and pass to jsp
		catch (ApplicationException aoAppEx)
		{
			try
			{
				setErrorMessageInResponse(aoActionRequestForFileInfo, aoActionResponseForFileInfo, aoAppEx, null);
			}
			catch (IOException e)
			{
				setExceptionMessageFromAction(aoActionResponseForFileInfo, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
						ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
			}
		}
		// Catch the exception thrown by transaction and set the error message
		// in request object and pass to jsp
		catch (Exception aoExp)
		{
			setExceptionMessageFromAction(aoActionResponseForFileInfo, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
	}

	/**
	 * Changed in 3.1.0 . Added check for Enhancement 6021 - char500 - extension
	 * This method is used to get the details properties of the selected
	 * document.
	 * <ul>
	 * <li>populate second tab on the upload screen according to the number of
	 * the required meta data</li>
	 * <li>Get the document bean from the ApplicationSession and set it in
	 * Request</li>
	 * <li>Set the render action parameter</li>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * Changes done for Enhancement #6429 for Release 3.4.0
	 * 
	 * @param aoActionRequestForFileInfo ActionRequest Object
	 * @param aoActionResponseForFileInfo ActionResponse Object
	 * @param lsReplacingdocId Replacing Document Id
	 * @param lsUploadProcess upload process
	 * @param lsIsAddendumDoc addendum doc
	 * @param lsUserOrgType user org type
	 * @param lsUploadingDocumentType uploading document type
	 * @param lsErrorMessageInSession error message
	 * @param lsErrorMessageTypeInSession error message type
	 * @param loUploadingDocObj uploading document object
	 * @throws ApplicationException If an ApplicationException Occurs
	 */
	private void displayUploadingFileInfoActionFinal(ActionRequest aoActionRequestForFileInfo,
			ActionResponse aoActionResponseForFileInfo, String lsReplacingdocId, String lsUploadProcess,
			String lsIsAddendumDoc, String lsUserOrgType, String lsUploadingDocumentType,
			String lsErrorMessageInSession, String lsErrorMessageTypeInSession, Document loUploadingDocObj)
			throws ApplicationException
	{
		// Updated in 3.1.0 . Added check for Enhancement 6021 - char500 -
		// extension
		if (!(loUploadingDocObj.getDocType().equalsIgnoreCase(P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE)
				|| loUploadingDocObj.getDocType().equalsIgnoreCase(P8Constants.PROPERTY_CE_CHAR500_EXT1_DOC_TYPE)
				|| loUploadingDocObj.getDocType().equalsIgnoreCase(P8Constants.PROPERTY_CE_CHAR500_EXT2_DOC_TYPE) || loUploadingDocObj
				.getDocType().equalsIgnoreCase(P8Constants.PROPERTY_CE_CHAR500_EXTENSION)))
		{
			List<DocumentPropertiesBean> loInitialDocPropsBean = FileNetOperationsUtils.getDocumentProperties(
					loUploadingDocObj.getDocCategory(), loUploadingDocObj.getDocType(), lsUserOrgType);
			loUploadingDocObj.setDocumentProperties(loInitialDocPropsBean);
		}
		aoActionRequestForFileInfo.setAttribute(HHSConstants.BASE_RFP_DOCUMENTS, loUploadingDocObj);
		String lsDocRefNum = HHSPortalUtil.parseQueryString(aoActionRequestForFileInfo,
				HHSConstants.HIDDEN_DOC_REF_SEQ_NO);
		String lsProposalId = HHSPortalUtil.parseQueryString(aoActionRequestForFileInfo, HHSConstants.PROPOSAL_ID);
		String lsProcurementId = HHSPortalUtil
				.parseQueryString(aoActionRequestForFileInfo, HHSConstants.PROCUREMENT_ID);
		if (lsDocRefNum != null)
		{
			aoActionResponseForFileInfo.setRenderParameter(HHSConstants.HIDDEN_DOC_REF_SEQ_NO, lsDocRefNum);
		}
		if (lsProposalId != null)
		{
			aoActionResponseForFileInfo.setRenderParameter(HHSConstants.PROPOSAL_ID, lsProposalId);
		}
		// ORG_ID,STAFF_ID,USER are passed further so as to read the
		// BAFO Document details
		// from DOCTYPE.xml as the login user is agency user
		if (null != aoActionRequestForFileInfo.getParameter(HHSConstants.ORGA_ID))
		{
			aoActionResponseForFileInfo.setRenderParameter(HHSConstants.ORGA_ID,
					aoActionRequestForFileInfo.getParameter(HHSConstants.ORGA_ID));
		}
		if (null != aoActionRequestForFileInfo.getParameter(HHSConstants.STAFF_ID))
		{
			aoActionResponseForFileInfo.setRenderParameter(HHSConstants.STAFF_ID,
					aoActionRequestForFileInfo.getParameter(HHSConstants.STAFF_ID));
		}
		if (null != aoActionRequestForFileInfo.getParameter(HHSConstants.USER))
		{
			aoActionResponseForFileInfo.setRenderParameter(HHSConstants.USER,
					aoActionRequestForFileInfo.getParameter(HHSConstants.USER));
		}
		if (null != PortalUtil.parseQueryString(aoActionRequestForFileInfo, HHSConstants.AWARD_ID))
		{
			aoActionResponseForFileInfo.setRenderParameter(HHSConstants.AWARD_ID,
					PortalUtil.parseQueryString(aoActionRequestForFileInfo, HHSConstants.AWARD_ID));
		}
		if (null != PortalUtil.parseQueryString(aoActionRequestForFileInfo, HHSConstants.EVALUATION_POOL_MAPPING_ID))
		{
			aoActionResponseForFileInfo.setRenderParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID,
					PortalUtil.parseQueryString(aoActionRequestForFileInfo, HHSConstants.EVALUATION_POOL_MAPPING_ID));
		}
		// Start || Changes done for Enhancement #6429 for Release 3.4.0
		if (null != PortalUtil.parseQueryString(aoActionRequestForFileInfo, HHSConstants.IS_FINANCIAL))
		{
			aoActionResponseForFileInfo.setRenderParameter(HHSConstants.IS_FINANCIAL,
					PortalUtil.parseQueryString(aoActionRequestForFileInfo, HHSConstants.IS_FINANCIAL));
		}
		if (null != HHSPortalUtil.parseQueryString(aoActionRequestForFileInfo, HHSConstants.HIDDEN_HDNCONTRACTID))
		{
			aoActionResponseForFileInfo.setRenderParameter(HHSConstants.HIDDEN_HDNCONTRACTID,
					HHSPortalUtil.parseQueryString(aoActionRequestForFileInfo, HHSConstants.HIDDEN_HDNCONTRACTID));
		}
		if (null != HHSPortalUtil.parseQueryString(aoActionRequestForFileInfo, HHSConstants.AS_PROC_STATUS))
		{
			aoActionResponseForFileInfo.setRenderParameter(HHSConstants.AS_PROC_STATUS,
					HHSPortalUtil.parseQueryString(aoActionRequestForFileInfo, HHSConstants.AS_PROC_STATUS));
		}
		// End || Changes done for Enhancement #6429 for Release 3.4.0
		if (lsUploadingDocumentType != null)
		{
			aoActionResponseForFileInfo.setRenderParameter(HHSConstants.UPLOAD_DOC_TYPE, lsUploadingDocumentType);
		}
		if (lsProcurementId != null)
		{
			aoActionResponseForFileInfo.setRenderParameter(HHSConstants.PROCUREMENT_ID, lsProcurementId);
		}
		if (null != lsReplacingdocId)
		{
			aoActionResponseForFileInfo.setRenderParameter(HHSConstants.REPLACING_DOCUMENT_ID, lsReplacingdocId);
		}
		aoActionResponseForFileInfo.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
		aoActionResponseForFileInfo.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.UPLOADING_FILE_INFO);
		if (null != PortalUtil.parseQueryString(aoActionRequestForFileInfo, HHSConstants.TOP_LEVEL_FROM_REQ))
		{
			aoActionResponseForFileInfo.setRenderParameter(HHSConstants.TOP_LEVEL_FROM_REQ,
					PortalUtil.parseQueryString(aoActionRequestForFileInfo, HHSConstants.TOP_LEVEL_FROM_REQ));
		}
		if (null != PortalUtil.parseQueryString(aoActionRequestForFileInfo, HHSConstants.MID_LEVEL_FROM_REQ))
		{
			aoActionResponseForFileInfo.setRenderParameter(HHSConstants.MID_LEVEL_FROM_REQ,
					PortalUtil.parseQueryString(aoActionRequestForFileInfo, HHSConstants.MID_LEVEL_FROM_REQ));
		}
		if (null != lsErrorMessageInSession)
		{
			aoActionResponseForFileInfo.setRenderParameter(HHSConstants.MESSAGE, lsErrorMessageInSession);
		}
		if (null != lsErrorMessageTypeInSession)
		{
			aoActionResponseForFileInfo.setRenderParameter(HHSConstants.MESSAGE_TYPE, lsErrorMessageTypeInSession);
		}
		if (null != lsUploadProcess)
		{
			aoActionResponseForFileInfo.setRenderParameter(HHSConstants.UPLOAD_PROCESS, lsUploadProcess);
		}
		if (null != lsIsAddendumDoc)
		{
			aoActionResponseForFileInfo.setRenderParameter(HHSConstants.IS_ADDENDUM, lsIsAddendumDoc);
		}
	}

	/**
	 * This method will redirect user to the next tab of the upload document
	 * screen
	 * <ul>
	 * <li>Get the user organization type from the request</li>
	 * <li>If the user is is of provider organization type then redirect user to
	 * <b>displayUploadingDocumentInfoProvider</b> view</li>
	 * <li>Else redirect user to the <b>displayUploadingDocumentInfoAgency</b>
	 * view</li>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * Changes done for Enhancement #6429 for Release 3.4.0
	 * 
	 * @param aoRequest Render Request Object
	 * @param aoResponse Render Response Object
	 * @return ModelAndView to display displayUploadingDocumentInfoProvider.jsp
	 * 
	 */
	@Override
	@RenderMapping(params = "render_action=uploadingFileInformation")
	protected ModelAndView displayUploadingFileInformationRender(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		try
		{
			aoRequest.setAttribute(HHSConstants.HIDDEN_DOC_REF_SEQ_NO,
					HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.HIDDEN_DOC_REF_SEQ_NO));
			aoRequest.setAttribute(HHSConstants.PROPOSAL_ID,
					HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROPOSAL_ID));
			// ORG_ID,STAFF_ID,USER are passed further so as to read the BAFO
			// Document details
			// from DOCTYPE.xml as the login user is agency user
			aoRequest.setAttribute(HHSConstants.ORGA_ID,
					HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.ORGA_ID));
			aoRequest.setAttribute(HHSConstants.STAFF_ID,
					HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.STAFF_ID));
			aoRequest.setAttribute(HHSConstants.USER, HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.USER));
			aoRequest.setAttribute(HHSConstants.AWARD_ID,
					HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.AWARD_ID));
			aoRequest.setAttribute(HHSConstants.EVALUATION_POOL_MAPPING_ID,
					HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.EVALUATION_POOL_MAPPING_ID));
			// Start || Changes done for Enhancement #6429 for Release 3.4.0
			if (null != HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.IS_FINANCIAL))
			{
				aoRequest.setAttribute(HHSConstants.IS_FINANCIAL,
						HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.IS_FINANCIAL));
			}
			if (null != HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.HIDDEN_HDNCONTRACTID))
			{
				aoRequest.setAttribute(HHSConstants.HIDDEN_HDNCONTRACTID,
						HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.HIDDEN_HDNCONTRACTID));
			}
			if (null != HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.AS_PROC_STATUS))
			{
				aoRequest.setAttribute(HHSConstants.AS_PROC_STATUS,
						HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.AS_PROC_STATUS));
			}
			// End || Changes done for Enhancement #6429 for Release 3.4.0
			aoRequest.setAttribute(HHSConstants.PROCUREMENT_ID,
					HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID));
			aoRequest.setAttribute(HHSConstants.UPLOAD_DOC_TYPE,
					HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.UPLOAD_DOC_TYPE));
			aoRequest.setAttribute(ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_PARAMETER,
					(Document) ApplicationSession.getAttribute(aoRequest, true,
							ApplicationConstants.SESSION_DOCUMENT_OBJ));
			aoRequest.setAttribute(HHSConstants.TOP_LEVEL_FROM_REQ,
					PortalUtil.parseQueryString(aoRequest, HHSConstants.TOP_LEVEL_FROM_REQ));
			aoRequest.setAttribute(HHSConstants.MID_LEVEL_FROM_REQ,
					PortalUtil.parseQueryString(aoRequest, HHSConstants.MID_LEVEL_FROM_REQ));
			aoRequest.setAttribute(HHSConstants.REPLACING_DOCUMENT_ID,
					PortalUtil.parseQueryString(aoRequest, HHSConstants.REPLACING_DOCUMENT_ID));
			aoRequest.setAttribute(HHSConstants.MESSAGE, PortalUtil.parseQueryString(aoRequest, HHSConstants.MESSAGE));
			aoRequest.setAttribute(HHSConstants.MESSAGE_TYPE,
					PortalUtil.parseQueryString(aoRequest, HHSConstants.MESSAGE_TYPE));
			aoRequest.setAttribute(HHSConstants.UPLOAD_PROCESS,
					PortalUtil.parseQueryString(aoRequest, HHSConstants.UPLOAD_PROCESS));
			aoRequest.setAttribute(HHSConstants.IS_ADDENDUM,
					PortalUtil.parseQueryString(aoRequest, HHSConstants.IS_ADDENDUM));
		}
		// Catch the exception thrown by transaction and set the error message
		// in request object and pass to jsp
		catch (Exception aoExp)
		{
			setGenericErrorMessage(aoRequest);
			LOG_OBJECT.Error("ApplicationException during file upload", aoExp);
		}
		return new ModelAndView(HHSConstants.DISPLAY_UPLOAD_DOC_INFO_PROVIDER);
	}

	/**
	 * This method will open the document upload overlay screen when user click
	 * on upload document button
	 * <ul>
	 * <li>Get the organization type and document bean object from session</li>
	 * <li>Get the document category and document type list and set it in
	 * document bean</li>
	 * <li>Then it will execute the transaction <b>getProcurementStatus_db</b>
	 * to get the procurement status</li>
	 * <li>Then it will set the attributes in the aoResponse parameter</li>
	 * <li>Set the document bean in session and redirect user to next tab of the
	 * upload</li>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * @param aoRequest Action Request Object
	 * @param aoResponse Action Response Object
	 */
	@ActionMapping(params = "submit_action=uploadDocument")
	protected void documentUploadAction(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		Document loDocument = new Document();
		Channel loChannel = new Channel();
		try
		{
			setNavigationParamsInRender(aoRequest, aoResponse);
			String lsProcurementId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
			String lsUploadingDocumentType = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.UPLOAD_DOC_TYPE);
			String lsReplacingDocumentId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.DOCUMENT_ID);
			String lsUploadProcess = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.UPLOAD_PROCESS);
			String lsDocType = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.DOCTYPE);
			String lsDocRefSeqNum = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.HIDDEN_DOC_REF);
			String lsIsAddendumDoc = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.IS_ADD_TYPE);
			loChannel.setData(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.GET_PROC_STATUS_DB);
			String lsProcurementStatus = (String) loChannel.getData(HHSConstants.PROCUREMENT_STATUS);
			// if the procurement status is changed planned or released
			if (!PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_PROCUREMENT_PLANNED).equalsIgnoreCase(lsProcurementStatus)
					&& !PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
							HHSConstants.STATUS_PROCUREMENT_RELEASED).equalsIgnoreCase(lsProcurementStatus))
			{
				String lsErrorMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.PREVENT_ADD_DOC);
				aoRequest.getPortletSession().setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg,
						PortletSession.APPLICATION_SCOPE);
				aoRequest.getPortletSession().setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE, PortletSession.APPLICATION_SCOPE);
				aoRequest.getPortletSession().setAttribute(HHSConstants.DIS_NEXT, true,
						PortletSession.APPLICATION_SCOPE);
			}
			PortletSession loSession = aoRequest.getPortletSession();
			String lsDocCategory = aoRequest
					.getParameter(ApplicationConstants.DOCUMENT_VAULT_DOC_CATEGORY_REQ_PARAMETER);
			String lsUserOrgType = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
					PortletSession.APPLICATION_SCOPE);
			aoRequest.setAttribute(ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_PARAMETER, loDocument);
			RFPReleaseDocsUtil.setRFPDocCategorynDocType(loDocument, lsDocCategory, lsUserOrgType, null);
			ApplicationSession.setAttribute(loDocument, aoRequest, ApplicationConstants.SESSION_DOCUMENT_OBJ);
			aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			aoResponse.setRenderParameter(HHSConstants.TOP_LEVEL_FROM_REQ,
					PortalUtil.parseQueryString(aoRequest, HHSConstants.TOP_LEVEL_FROM_REQ));
			aoResponse.setRenderParameter(HHSConstants.MID_LEVEL_FROM_REQ,
					PortalUtil.parseQueryString(aoRequest, HHSConstants.MID_LEVEL_FROM_REQ));
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.UPLOAD_DOC);
			// Added for R5- combo box for DocType - fix for Defect id 7270
			List<String> loDocTypeList = FileNetOperationsUtils
					.getDocType(lsUserOrgType, lsUploadingDocumentType, null);
			ApplicationSession.setAttribute(loDocTypeList, aoRequest, HHSR5Constants.DOC_TYPE_DROP_DOWN_COMBO);
			// R5 end
			if (null != lsUploadingDocumentType)
			{
				aoResponse.setRenderParameter(HHSConstants.UPLOAD_DOC_TYPE, lsUploadingDocumentType);
			}
			if (null != lsReplacingDocumentId)
			{
				aoResponse.setRenderParameter(HHSConstants.REPLACING_DOCUMENT_ID, lsReplacingDocumentId);
			}
			if (null != lsDocRefSeqNum && !lsDocRefSeqNum.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.HIDDEN_DOC_REF_SEQ_NO, lsDocRefSeqNum);
			}
			if (null != lsIsAddendumDoc && !lsIsAddendumDoc.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.IS_ADDENDUM_DOC, lsIsAddendumDoc);
			}
			if (null != lsUploadProcess && !lsUploadProcess.isEmpty())
			{
				lsDocCategory = RFPReleaseDocsUtil.getDocCategoryForDocType(lsDocType, lsUserOrgType);
				loDocument.setDocCategory(lsDocCategory);
				loDocument.setDocType(lsDocType);
				aoResponse.setRenderParameter(HHSConstants.UPLOAD_PROCESS, lsUploadProcess);
			}

		}
		// Catch the Application exception thrown by transaction and set the
		// error message
		// in request object and pass to jsp
		catch (ApplicationException aoAppEx)
		{
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);

		}
		// Catch the exception thrown by transaction and set the error message
		// in request object and pass to jsp
		catch (Exception aoExp)
		{
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
	}

	/**
	 * This method is modified to fix 5523 in release 2.6.0. An extra attribute
	 * lsIsRequiredDoc is been set in session. This method will open the
	 * document upload overlay screen when user select upload document option
	 * from the proposal document screen
	 * <ul>
	 * <li>Get the organization type and document bean object from session</li>
	 * <li>Get the document category and document type list and set it in
	 * document bean</li>
	 * <li>Set the document bean in session and redirect user to next tab of the
	 * upload</li>
	 * </ul>
	 * 
	 * @param aoRequest Action Request Object
	 * @param aoResponse Action Response Object
	 */
	@ActionMapping(params = "submit_action=uploadProposalDocument")
	protected void uploadProposalDocumentAction(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		Document loDocument = new Document();
		Channel loChannel = new Channel();
		String lsReplacingdocId = null;
		String lsUploadingDocType = null;
		try
		{
			// 5523 fix starts
			String lsIsRequiredDoc = aoRequest.getParameter(HHSConstants.HIDDEN_IS_DOC_REQUIRED);
			if (lsIsRequiredDoc != null && lsIsRequiredDoc.equalsIgnoreCase(ApplicationConstants.ONE))
			{
				PortletSessionHandler.setAttribute(Boolean.TRUE, aoRequest, HHSConstants.LS_IS_REQUIRED_DOC);
			}
			else
			{
				PortletSessionHandler.setAttribute(Boolean.FALSE, aoRequest, HHSConstants.LS_IS_REQUIRED_DOC);
			}
			// 5523 fix ends
			lsReplacingdocId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.DOC_ID);
			String lsProposalId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROPOSAL_ID);
			lsUploadingDocType = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.UPLOAD_DOC_TYPE);
			loChannel.setData(HHSConstants.PROPOSAL_ID, lsProposalId);
			loChannel.setData(HHSConstants.UPLOAD_DOC_TYPE, lsUploadingDocType);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.GET_PROP_STAT_ID);
			Boolean loProposalStatus = (Boolean) loChannel.getData(HHSConstants.PROPOSAL_STATUS_FLAG);
			// if the proposal status has been changed from draft
			if (loProposalStatus)
			{
				uploadProposalDocumentActionFinal(aoRequest, aoResponse, loDocument, lsReplacingdocId,
						lsUploadingDocType);
			}
			else
			{
				setResponseForProposalDocAction(aoResponse);
			}
		}
		catch (ApplicationException aoAppEx)
		{
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
	}

	/**
	 * This method is used by agency to upload award documents from the view
	 * award document screen
	 * <ul>
	 * <li>Get parameters from request object</li>
	 * <li>Set the parameters in response object</li>
	 * <li>Call render_action "uploadDocument"</li>
	 * </ul>
	 * 
	 * Added for Enhancement #6429 for Release 3.4.0
	 * 
	 * @param aoRequest Action Request Object
	 * @param aoResponse Action Response Object
	 */
	@ActionMapping(params = "submit_action=addAwardDocument")
	protected void addAwardDocumentAction(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		Document loDocument = new Document();
		String lsForAwardScreen = HHSConstants.STRING_FOR_AWARD_SCREEN;
		String lsUserOrgType = null;
		String lsUploadingDocType = null;
		PortletSession loSession = null;
		String lsDocCategory = null;
		String lsOrgId = null;
		try
		{
			loSession = aoRequest.getPortletSession();
			lsUserOrgType = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
					PortletSession.APPLICATION_SCOPE);
			lsOrgId = PortalUtil.parseQueryString(aoRequest, HHSConstants.ORGA_ID);
			lsUploadingDocType = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.UPLOAD_DOC_TYPE);
			aoRequest.setAttribute(ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_PARAMETER, loDocument);
			RFPReleaseDocsUtil.setRFPDocCategorynDocType(loDocument, lsDocCategory, lsUserOrgType, lsForAwardScreen);
			PortletSessionHandler.setAttribute(loDocument, aoRequest, ApplicationConstants.SESSION_DOCUMENT_OBJ);
			aoRequest.setAttribute(ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_PARAMETER,
					(Document) ApplicationSession.getAttribute(aoRequest, true,
							ApplicationConstants.SESSION_DOCUMENT_OBJ));
			if (null != lsUploadingDocType)
			{
				aoResponse.setRenderParameter(HHSConstants.UPLOAD_DOC_TYPE, lsUploadingDocType);

			}
			if (null != PortalUtil.parseQueryString(aoRequest, HHSConstants.TOP_LEVEL_FROM_REQ))
			{
				aoResponse.setRenderParameter(HHSConstants.TOP_LEVEL_FROM_REQ,
						PortalUtil.parseQueryString(aoRequest, HHSConstants.TOP_LEVEL_FROM_REQ));
			}
			if (null != PortalUtil.parseQueryString(aoRequest, HHSConstants.EVALUATION_POOL_MAPPING_ID))
			{
				aoResponse.setRenderParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID,
						PortalUtil.parseQueryString(aoRequest, HHSConstants.EVALUATION_POOL_MAPPING_ID));
			}
			if (null != PortalUtil.parseQueryString(aoRequest, HHSConstants.IS_FINANCIAL))
			{
				aoResponse.setRenderParameter(HHSConstants.IS_FINANCIAL,
						PortalUtil.parseQueryString(aoRequest, HHSConstants.IS_FINANCIAL));
			}
			if (null != lsOrgId)
			{
				aoResponse.setRenderParameter(HHSConstants.ORGA_ID, lsOrgId);
			}
			if (null != PortalUtil.parseQueryString(aoRequest, HHSConstants.HIDDEN_HDNCONTRACTID))
			{
				aoResponse.setRenderParameter(HHSConstants.HIDDEN_HDNCONTRACTID,
						PortalUtil.parseQueryString(aoRequest, HHSConstants.HIDDEN_HDNCONTRACTID));
			}
			if (null != PortalUtil.parseQueryString(aoRequest, HHSConstants.AS_PROC_STATUS))
			{
				aoResponse.setRenderParameter(HHSConstants.AS_PROC_STATUS,
						PortalUtil.parseQueryString(aoRequest, HHSConstants.AS_PROC_STATUS));
			}
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.UPLOAD_DOC);
			// /Added for R5- combo box for DocType - fix for Defect id 7270
			List<String> loDocTypeList = FileNetOperationsUtils.getDocType(lsUserOrgType,
					HHSR5Constants.AGENCY_AWARD_DOC, null);
			ApplicationSession.setAttribute(loDocTypeList, aoRequest, HHSR5Constants.DOC_TYPE_AWARD_DROP_DOWN_COMBO);
			// R5 end
		}
		catch (ApplicationException aoAppEx)
		{
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
	}

	/**
	 * This method will open the document upload overlay screen when user select
	 * upload document option from the proposal document screen
	 * <ul>
	 * <li>Get the organization type and document bean object from session</li>
	 * <li>Get the document category and document type list and set it in
	 * document bean</li>
	 * <li>Set the document bean in session and redirect user to next tab of the
	 * upload</li>
	 * </ul>
	 * 
	 * @param aoRequest Action Request Object
	 * @param aoResponse Action Response Object
	 * @param loDocument Document to be uploaded
	 * @param lsReplacingdocId Replacing Document Id
	 * @param lsUploadingDocType Uploading Document Type
	 * @throws ApplicationException
	 */
	private void uploadProposalDocumentActionFinal(ActionRequest aoRequest, ActionResponse aoResponse,
			Document loDocument, String lsReplacingdocId, String lsUploadingDocType) throws ApplicationException
	{
		String lsDocCategory;
		String lsDocType;
		PortletSession loSession = aoRequest.getPortletSession();
		String lsUserOrgType = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
				PortletSession.APPLICATION_SCOPE);
		lsDocType = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.DOCTYPE);
		if (lsDocType.equalsIgnoreCase(HHSConstants.OTHER))
		{
			lsDocCategory = RFPReleaseDocsUtil.getDocCategoryForRfpOtherDocType(lsDocType, lsUserOrgType);
		}
		else if (HHSConstants.BAFO.equalsIgnoreCase(HHSPortalUtil.parseQueryString(aoRequest,
				HHSConstants.UPLOAD_DOC_TYPE)))
		{
			lsDocCategory = RFPReleaseDocsUtil.getDocCategoryForDocType(lsDocType, HHSConstants.PROVIDER_ORG);
		}
		else
		{
			lsDocCategory = RFPReleaseDocsUtil.getDocCategoryForDocType(lsDocType, lsUserOrgType);
		}
		loDocument.setDocCategory(lsDocCategory);
		loDocument.setDocType(lsDocType);
		ApplicationSession.setAttribute(loDocument, aoRequest, ApplicationConstants.SESSION_DOCUMENT_OBJ);
		aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
		String lsDocRefSeqNum = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.HIDDEN_DOC_REF_SEQ_NO);
		aoResponse.setRenderParameter(HHSConstants.LS_DOC_REF_SQ_NUM, lsDocRefSeqNum);
		if (null != aoRequest.getParameter(HHSConstants.PROPOSAL_ID))
		{
			aoResponse.setRenderParameter(HHSConstants.PROPOSAL_ID, aoRequest.getParameter(HHSConstants.PROPOSAL_ID));
		}
		// ORG_ID,STAFF_ID,USER are passed further so as to read the
		// BAFO Document details for R4 updates
		// from DOCTYPE.xml as the login user is agency user
		if (null != aoRequest.getParameter(HHSConstants.ORGA_ID))
		{
			aoResponse.setRenderParameter(HHSConstants.ORGA_ID, aoRequest.getParameter(HHSConstants.ORGA_ID));
		}
		if (null != aoRequest.getParameter(HHSConstants.STAFF_ID))
		{
			aoResponse.setRenderParameter(HHSConstants.STAFF_ID, aoRequest.getParameter(HHSConstants.STAFF_ID));
		}
		if (null != aoRequest.getParameter(HHSConstants.USER))
		{
			aoResponse.setRenderParameter(HHSConstants.USER, aoRequest.getParameter(HHSConstants.USER));
		}
		if (null != aoRequest.getParameter(HHSConstants.PROCUREMENT_ID))
		{
			aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID,
					aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
		}
		if (null != aoRequest.getParameter(HHSConstants.AWARD_ID))
		{
			aoResponse.setRenderParameter(HHSConstants.AWARD_ID, aoRequest.getParameter(HHSConstants.AWARD_ID));
		}
		aoResponse.setRenderParameter(HHSConstants.UPLOAD_DOC_TYPE, lsUploadingDocType);
		if (null != PortalUtil.parseQueryString(aoRequest, HHSConstants.TOP_LEVEL_FROM_REQ))
		{
			aoResponse.setRenderParameter(HHSConstants.TOP_LEVEL_FROM_REQ,
					PortalUtil.parseQueryString(aoRequest, HHSConstants.TOP_LEVEL_FROM_REQ));
		}
		if (null != PortalUtil.parseQueryString(aoRequest, HHSConstants.MID_LEVEL_FROM_REQ))
		{
			aoResponse.setRenderParameter(HHSConstants.MID_LEVEL_FROM_REQ,
					PortalUtil.parseQueryString(aoRequest, HHSConstants.MID_LEVEL_FROM_REQ));
		}
		aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.UPLOAD_DOC);
		if (null != lsReplacingdocId)
		{
			aoResponse.setRenderParameter(HHSConstants.REPLACING_DOCUMENT_ID, lsReplacingdocId);
		}
		setNavigationParamsInRender(aoRequest, aoResponse);
	}

	/**
	 * This method sets response for Upload Proposal Document Action
	 * @param aoResponse ActionResponse
	 * @throws ApplicationException
	 */
	private void setResponseForProposalDocAction(ActionResponse aoResponse) throws ApplicationException
	{
		aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
				HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.PROPOSAL_SUBMIT_FAIL));
		aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
		aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.RENDER_PROVIDER_PROPOSAL);
		aoResponse.setRenderParameter(HHSConstants.ERROR_FLAG, HHSConstants.ERROR_FLAG);
	}

	/**
	 * This method will redirect user to the upload screen with all the details
	 * <ul>
	 * <li>Get the procurementId, Doc Ref Seq Num, addendum document type from
	 * the request</li>
	 * <li>Get the document bean object from the session and set it in the
	 * request</li>
	 * <li>Redirect user to the upload document screen</li>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * Changes done for Enhancement #6429 for Release 3.4.0
	 * 
	 * @param aoRenderRequestForUpload Render Request Object
	 * @param aoResponseForUpload Response Request Object
	 * @return ModelAndView for upload File to show the
	 *         uploadRfpAndAwardDocs.jsp page
	 */
	@Override
	@RenderMapping(params = "render_action=uploadDocument")
	protected ModelAndView uploadDocumentRender(RenderRequest aoRenderRequestForUpload,
			RenderResponse aoResponseForUpload)
	{
		try
		{
			String lsProcurementId = HHSPortalUtil.parseQueryString(aoRenderRequestForUpload,
					HHSConstants.PROCUREMENT_ID);
			String lsDocRefSeqNum = HHSPortalUtil.parseQueryString(aoRenderRequestForUpload,
					HHSConstants.HIDDEN_DOC_REF_SEQ_NO);
			String lsUploadProcess = HHSPortalUtil.parseQueryString(aoRenderRequestForUpload,
					HHSConstants.UPLOAD_PROCESS);
			String lsIsAddendumTypeDoc = HHSPortalUtil.parseQueryString(aoRenderRequestForUpload,
					HHSConstants.IS_ADDENDUM_DOC);
			aoRenderRequestForUpload.setAttribute(ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_PARAMETER,
					(Document) ApplicationSession.getAttribute(aoRenderRequestForUpload, true,
							ApplicationConstants.SESSION_DOCUMENT_OBJ));
			aoRenderRequestForUpload.setAttribute(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			aoRenderRequestForUpload.setAttribute(HHSConstants.HIDDEN_DOC_REF_SEQ_NO, lsDocRefSeqNum);
			aoRenderRequestForUpload.setAttribute(HHSConstants.PROPOSAL_ID,
					HHSPortalUtil.parseQueryString(aoRenderRequestForUpload, HHSConstants.PROPOSAL_ID));
			// ORG_ID,STAFF_ID,USER are passed further so as to read the BAFO
			// Document details
			// from DOCTYPE.xml as the login user is agency user
			aoRenderRequestForUpload.setAttribute(HHSConstants.ORGA_ID,
					HHSPortalUtil.parseQueryString(aoRenderRequestForUpload, HHSConstants.ORGA_ID));
			aoRenderRequestForUpload.setAttribute(HHSConstants.USER,
					HHSPortalUtil.parseQueryString(aoRenderRequestForUpload, HHSConstants.USER));
			aoRenderRequestForUpload.setAttribute(HHSConstants.STAFF_ID,
					HHSPortalUtil.parseQueryString(aoRenderRequestForUpload, HHSConstants.STAFF_ID));
			aoRenderRequestForUpload.setAttribute(HHSConstants.AWARD_ID,
					HHSPortalUtil.parseQueryString(aoRenderRequestForUpload, HHSConstants.AWARD_ID));
			aoRenderRequestForUpload.setAttribute(HHSConstants.EVALUATION_POOL_MAPPING_ID,
					HHSPortalUtil.parseQueryString(aoRenderRequestForUpload, HHSConstants.EVALUATION_POOL_MAPPING_ID));
			// Start || Changes done for Enhancement #6429 for Release 3.4.0
			if (null != HHSPortalUtil.parseQueryString(aoRenderRequestForUpload, HHSConstants.IS_FINANCIAL))
			{
				aoRenderRequestForUpload.setAttribute(HHSConstants.IS_FINANCIAL,
						HHSPortalUtil.parseQueryString(aoRenderRequestForUpload, HHSConstants.IS_FINANCIAL));
			}
			if (null != HHSPortalUtil.parseQueryString(aoRenderRequestForUpload, HHSConstants.HIDDEN_HDNCONTRACTID))
			{
				aoRenderRequestForUpload.setAttribute(HHSConstants.HIDDEN_HDNCONTRACTID,
						HHSPortalUtil.parseQueryString(aoRenderRequestForUpload, HHSConstants.HIDDEN_HDNCONTRACTID));
			}
			if (null != HHSPortalUtil.parseQueryString(aoRenderRequestForUpload, HHSConstants.AS_PROC_STATUS))
			{
				aoRenderRequestForUpload.setAttribute(HHSConstants.AS_PROC_STATUS,
						HHSPortalUtil.parseQueryString(aoRenderRequestForUpload, HHSConstants.AS_PROC_STATUS));
			}
			// End || Changes done for Enhancement #6429 for Release 3.4.0
			aoRenderRequestForUpload.setAttribute(HHSConstants.UPLOAD_DOC_TYPE,
					HHSPortalUtil.parseQueryString(aoRenderRequestForUpload, HHSConstants.UPLOAD_DOC_TYPE));
			aoRenderRequestForUpload.setAttribute(HHSConstants.TOP_LEVEL_FROM_REQ,
					PortalUtil.parseQueryString(aoRenderRequestForUpload, HHSConstants.TOP_LEVEL_FROM_REQ));
			aoRenderRequestForUpload.setAttribute(HHSConstants.MID_LEVEL_FROM_REQ,
					PortalUtil.parseQueryString(aoRenderRequestForUpload, HHSConstants.MID_LEVEL_FROM_REQ));
			aoRenderRequestForUpload.setAttribute(HHSConstants.REPLACING_DOCUMENT_ID,
					PortalUtil.parseQueryString(aoRenderRequestForUpload, HHSConstants.REPLACING_DOCUMENT_ID));
			aoRenderRequestForUpload.setAttribute(HHSConstants.UPLOAD_PROCESS, lsUploadProcess);
			aoRenderRequestForUpload.setAttribute(HHSConstants.IS_ADD_TYPE, lsIsAddendumTypeDoc);
			// if there is some message set in the session
			if (aoRenderRequestForUpload.getPortletSession().getAttribute(ApplicationConstants.ERROR_MESSAGE,
					PortletSession.APPLICATION_SCOPE) != null)
			{
				aoRenderRequestForUpload.setAttribute(
						ApplicationConstants.ERROR_MESSAGE,
						aoRenderRequestForUpload.getPortletSession().getAttribute(ApplicationConstants.ERROR_MESSAGE,
								PortletSession.APPLICATION_SCOPE));
				aoRenderRequestForUpload.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE);
				ApplicationSession.getAttribute(aoRenderRequestForUpload, ApplicationConstants.LINKED_TO_APP_FLAG);
			}
			setExceptionMessageInResponse(aoRenderRequestForUpload);
			// Added for R5- combo box for DocType - fix for Defect id 7270
			aoRenderRequestForUpload.setAttribute(HHSR5Constants.DOC_TYPE_DROP_DOWN_COMBO, ApplicationSession
					.getAttribute(aoRenderRequestForUpload, true, HHSR5Constants.DOC_TYPE_DROP_DOWN_COMBO));
			List<String> loListForAward = (List<String>) ApplicationSession.getAttribute(aoRenderRequestForUpload,
					true, HHSR5Constants.DOC_TYPE_AWARD_DROP_DOWN_COMBO);
			if (null != loListForAward && !loListForAward.isEmpty())
			{
				aoRenderRequestForUpload.setAttribute(HHSR5Constants.DOC_TYPE_DROP_DOWN_COMBO, loListForAward);
			}
			// R5 end
		}
		// Catch the exception thrown by transaction and set the error message
		// in request object and pass to jsp
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("ApplicationException during file upload", aoExp);
			setGenericErrorMessage(aoRenderRequestForUpload);
		}
		return new ModelAndView(HHSConstants.UPLOAD_RFP_AWARD_DOCS);
	}

	/**
	 * This method will create the document bean with appropriate data and send
	 * it to the requested view
	 * <ul>
	 * <li>Get the transaction name set in the Channel Object</li>
	 * <li>Execute <b>getProcurementStatus_db</b> Transaction to get the
	 * procurement status and title</li>
	 * <li>Get the list of document objects list from Channel Object</li>
	 * <li>Set the list of document bean objects into the request object</li>
	 * <li>Create the model and view object of the <b>addDocumentFromVault</b>
	 * jsp and render user to the desired page</li>
	 * <li>It will open the add document screen with all the available document
	 * listed</li>
	 * </li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * @param aoRequest ResourceRequest Object
	 * @param aoResponse ResourceResponse Object
	 * @return ModelAndView with view name
	 */
	@Override
	@ResourceMapping("addRfpDocumentResource")
	public ModelAndView resourceAddRfpDocuments(ResourceRequest aoRequest, ResourceResponse aoResponse)
	{
		PortletSession loSession = aoRequest.getPortletSession();
		try
		{
			P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			String lsProcurementId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
			String lsReplacingDocumentId = HHSPortalUtil
					.parseQueryString(aoRequest, HHSConstants.REPLACING_DOCUMENT_ID);
			String lsDocumentType = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.DOCTYPE);
			String lsIsAddendumDoc = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.IS_ADDENDUM_DOC);
			String lsDocRefSeqNum = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.HIDDEN_DOC_REF_SEQ_NO);
			List<ExtendedDocument> loRfpDocumentList = null;
			Channel loChannel = new Channel();
			String lsFolderPath = null;
			loChannel.setData(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
			// Added for Release 5
			loChannel.setData(HHSR5Constants.DOCUMENT_TYPE, lsDocumentType);
			// End Release 5
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.GET_PROC_STATUS_DB);
			String lsProcurementStatus = (String) loChannel.getData(HHSConstants.PROCUREMENT_STATUS);
			// if the procurement status has been changed by different userv
			// from planned or released
			if (!PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_PROCUREMENT_PLANNED).equalsIgnoreCase(lsProcurementStatus)
					&& !PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
							HHSConstants.STATUS_PROCUREMENT_RELEASED).equalsIgnoreCase(lsProcurementStatus))
			{
				String lsErrorMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.PREVENT_ADD_DOC);
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
			}
			String lsNextPage = aoRequest.getParameter(HHSConstants.NEXT_PAGE_PARAM);
			if (null == lsNextPage)
			{
				FileNetOperationsUtils.reInitializePageIterator(loSession, loUserSession);
			}
			else
			{
				loUserSession.setNextPageIndex(Integer.valueOf(lsNextPage) - HHSConstants.INT_ONE);
			}
			String lsUserOrgType = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
					PortletSession.APPLICATION_SCOPE);
			String lsOrgType = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
					PortletSession.APPLICATION_SCOPE);
			String lsUserOrg = aoRequest.getParameter(HHSR5Constants.ORG_NAME);
			// Added for Release 5- selectVault
			String lsUserOrgType1 = aoRequest.getParameter(HHSConstants.LS_USER_ORG_TYPE);
			if (lsUserOrgType1 == null && lsUserOrg == null)
			{
				lsUserOrgType1 = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
						PortletSession.APPLICATION_SCOPE);
				if (lsUserOrgType1.equalsIgnoreCase(ApplicationConstants.CITY_ORG))
				{
					lsUserOrg = lsUserOrgType1;
				}
				else
				{
					lsUserOrg = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
							PortletSession.APPLICATION_SCOPE);
				}
			}

			String lsFolderId = aoRequest.getParameter(HHSR5Constants.FOLDER_ID);
			String lsSelectAll = aoRequest.getParameter(HHSConstants.SELECT_ALL_FLAG);
			if ((null == lsFolderId || lsFolderId.equalsIgnoreCase(HHSR5Constants.NULL))
					&& (null != lsSelectAll && !lsSelectAll.isEmpty()))
			{
				lsFolderPath = FileNetOperationsUtils.setFolderPath(lsUserOrgType, lsUserOrg,
						HHSR5Constants.DOCUMENT_VAULT);
				aoRequest.getPortletSession().setAttribute(HHSR5Constants.FOLDER_PATH, lsFolderPath);
			}
			HashMap<String, Object> loFilterProps = new HashMap<String, Object>();
			String lsvaultFilter = aoRequest.getParameter(HHSR5Constants.SELECT_VAULT);
			loFilterProps.put(HHSR5Constants.SELECT_VAULT, lsvaultFilter);
			loFilterProps.put(HHSConstants.SELECT_ALL_FLAG, lsSelectAll);
			// Relese 5 ends- selectVAult
			FileNetOperationsUtils.getOrgTypeFilter(loFilterProps, lsUserOrg, lsUserOrgType1);
			FileNetOperationsUtils.setPropFilter(loFilterProps, lsFolderPath, lsFolderId, lsUserOrgType, lsUserOrg);
			RFPReleaseDocsUtil.getSelectDocFromVaultChannel(lsUserOrgType, loUserSession, loChannel, lsOrgType,
					loFilterProps, lsDocumentType);
			if (null != lsReplacingDocumentId && !lsReplacingDocumentId.isEmpty())
			{
				loChannel.setData(ApplicationConstants.DOC_TYPE, lsDocumentType);
			}
			if (null != lsReplacingDocumentId && !lsReplacingDocumentId.isEmpty())
			{
				loChannel.setData(ApplicationConstants.DOC_TYPE, lsDocumentType);
			}
			// if the transaction name is not null
			if (loChannel.getData(HHSConstants.BASE_TRANSACTION_NAME) != null)
			{
				addRFPDocs(aoRequest, loSession, loUserSession, lsProcurementId, lsReplacingDocumentId,
						lsIsAddendumDoc, lsDocRefSeqNum, loChannel, lsNextPage);
			}
		}
		// Catch the application exception thrown by transaction and set the
		// error message
		// in request object and pass to jsp
		catch (ApplicationException aoAppExp)
		{
			handleApplicationException(aoRequest, aoAppExp);
		}
		// Catch the exception thrown by transaction and set the error message
		// in request object and pass to jsp
		catch (Exception aoExp)
		{
			handleException(aoRequest, aoExp);
		}
		return new ModelAndView(HHSConstants.ADD_DOC_FROM_VAULT);
	}

	/**
	 * This method will create the document bean with appropriate data and send
	 * it to the requested view
	 * <ul>
	 * <li>Get the list of document objects list from Channel Object</li>
	 * <li>Set the list of document bean objects into the request object</li>
	 * <li>Create the model and view object of the <b>addDocumentFromVault</b>
	 * jsp and render user to the desired page</li>
	 * <li>It will open the add document screen with all the available document
	 * listed</li>
	 * </li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * @param aoRequest ResourceRequest Object
	 * @param loSession Portlet Session
	 * @param loUserSession Filenet session
	 * @param lsProcurementId procurement Id
	 * @param lsReplacingDocumentId Replacing Document Id
	 * @param lsIsAddendumDoc addendum doc flag
	 * @param lsDocRefSeqNum doc refernce seq number
	 * @param loChannel Channel object
	 * @param lsNextPage Next page string
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	private void addRFPDocs(ResourceRequest aoRequest, PortletSession loSession, P8UserSession loUserSession,
			String lsProcurementId, String lsReplacingDocumentId, String lsIsAddendumDoc, String lsDocRefSeqNum,
			Channel loChannel, String lsNextPage) throws ApplicationException
	{
		List<ExtendedDocument> loRfpDocumentList;
		String lsTransactionName = (String) loChannel.getData(HHSConstants.BASE_TRANSACTION_NAME);
		// Execute the transaction obtained from the file
		// DocumentBusinessApp.java.

		// TransactionManager.executeTransaction(loChannel, lsTransactionName);
		// Release 5- selectVault- change transactionName
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		loChannel.setData(HHSR5Constants.AO_USER_ID, lsUserId);
		TransactionManager.executeTransaction(loChannel, HHSR5Constants.DISPLAY_DOC_LIST_FILENET_TRANS_NAME,
				HHSR5Constants.TRANSACTION_ELEMENT_R5);
		// Release 5 ends
		loRfpDocumentList = RFPReleaseDocsUtil.setSelectedDocumentBean(loChannel);
		RFPReleaseDocsUtil.setReqRequestParameter(loSession, loUserSession, lsNextPage);

		aoRequest.setAttribute(ApplicationConstants.SESSION_DOCUMENT_LIST, loRfpDocumentList);
		aoRequest.setAttribute(HHSConstants.PROCUREMENT_ID, lsProcurementId);
		aoRequest.setAttribute(HHSConstants.TOP_LEVEL_FROM_REQ,
				HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.TOP_LEVEL_FROM_REQ));
		aoRequest.setAttribute(HHSConstants.MID_LEVEL_FROM_REQ,
				HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.MID_LEVEL_FROM_REQ));
		if (null != lsReplacingDocumentId)
		{
			aoRequest.setAttribute(HHSConstants.REPLACING_DOCUMENT_ID, lsReplacingDocumentId);
		}
		if (null != lsIsAddendumDoc)
		{
			aoRequest.setAttribute(HHSConstants.IS_ADDENDUM_DOC, lsIsAddendumDoc);
		}
		if (null != lsDocRefSeqNum)
		{
			aoRequest.setAttribute(HHSConstants.HIDDEN_DOC_REF_SEQ_NO, lsDocRefSeqNum);
		}
		//Added for fix if defect # 8378
		if (null != loChannel.getData(HHSConstants.DOCTYPE) && !loChannel.getData(HHSConstants.DOCTYPE).toString().isEmpty())
		{
			aoRequest.setAttribute(HHSConstants.DOCTYPE, loChannel.getData(HHSConstants.DOCTYPE));
		}
		//Added for fix if defect # 8378 end
	}

	/**
	 * This method handles Exception
	 * @param aoRequest ResourceRequest
	 * @param aoExp Exception caught
	 */
	private void handleException(ResourceRequest aoRequest, Exception aoExp)
	{
		String lsMessage = aoExp.getMessage();
		if (lsMessage == null)
		{
			try
			{
				lsMessage = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.GENERIC_ERROR_MESSAGE);
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsMessage);
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
			}
			catch (ApplicationException aoAppEx)
			{
				LOG_OBJECT.Error("Exception occured while fetching message", aoAppEx);
			}
		}
	}

	/**
	 * This method handles Application Exception
	 * @param aoRequest ResourceRequest
	 * @param aoAppExp ApplicationException caught
	 */
	private void handleApplicationException(ResourceRequest aoRequest, ApplicationException aoAppExp)
	{
		String lsMessage = aoAppExp.getMessage();
		if (lsMessage == null || lsMessage.isEmpty())
		{
			try
			{
				lsMessage = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.GENERIC_ERROR_MESSAGE);
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsMessage);
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
			}
			catch (ApplicationException aoAppEx)
			{
				LOG_OBJECT.Error("Exception occured while fetching message", aoAppEx);
			}
		}
	}

	/**
	 * This method is modified to fix 5523 in release 2.6.0. An extra attribute
	 * lsIsRequiredDoc is been set in session. This method will create the
	 * document bean with appropriate data and send it to the requested view
	 * <ul>
	 * <li>Get the transaction name set in the Channel Object</li>
	 * <li>Execute <b>displayDocList_filenet</b> Transaction to get the detail
	 * list of the document present in the vault</li>
	 * <li>Get the list of document objects list from Channel Object</li>
	 * <li>Set the list of document bean objects into the request object</li>
	 * <li>Create the model and view object of the <b>addDocumentFromVault</b>
	 * jsp and render user to the desired page</li>
	 * </ul>
	 * 
	 * @param aoRequest ResourceRequest Object
	 * @param aoResponse ResourceResponse Object
	 * @return ModelAndView with view name to display the
	 *         addDocumentFromVault.jsp page
	 */
	@ResourceMapping("addProposalDocumentResource")
	public ModelAndView resourceAddProposalDocuments(ResourceRequest aoRequest, ResourceResponse aoResponse)
	{
		PortletSession loSession = aoRequest.getPortletSession();
		Channel loChannelObj = null;
		try
		{
			String lsFolderPath = null;
			String lsDocType = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.DOCTYPE);
			String lsReplacingDocId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.HIDDEN_DOCUMENT_ID);
			String lsDocRefSeqNum = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.HIDDEN_DOC_REF_SEQ_NO);
			String lsProposalId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROPOSAL_ID);
			String lsAwardId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.AWARD_ID);
			String lsUploadingDocumentType = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.UPLOAD_DOC_TYPE);
			String lsUserOrg = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);
			String lsUserOrgType = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
					PortletSession.APPLICATION_SCOPE);
			P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			// 5523 fix starts
			String lsIsRequiredDoc = aoRequest.getParameter(HHSConstants.HIDDEN_IS_DOC_REQUIRED);
			if (lsIsRequiredDoc != null && lsIsRequiredDoc.equalsIgnoreCase(ApplicationConstants.ONE))
			{

				PortletSessionHandler.setAttribute(Boolean.TRUE, aoRequest, HHSConstants.LS_IS_REQUIRED_DOC);
			}
			else
			{
				PortletSessionHandler.setAttribute(Boolean.FALSE, aoRequest, HHSConstants.LS_IS_REQUIRED_DOC);
			}
			// 5523 fix ends
			String lsNextPage = aoRequest.getParameter(HHSConstants.NEXT_PAGE_PARAM);
			if (null == lsNextPage)
			{
				FileNetOperationsUtils.reInitializePageIterator(loSession, loUserSession);
			}
			else
			{
				loUserSession.setNextPageIndex(Integer.valueOf(lsNextPage) - HHSConstants.INT_ONE);
			}
			// added for Release 5- required hashMap
			HashMap<String, Object> loFilterProps = new HashMap<String, Object>();
			String lsvaultFilter = aoRequest.getParameter(HHSR5Constants.SELECT_VAULT);
			String lsSelectAll = aoRequest.getParameter(HHSConstants.SELECT_ALL_FLAG);
			loFilterProps.put(HHSR5Constants.SELECT_VAULT, lsvaultFilter);
			loFilterProps.put(HHSConstants.SELECT_ALL_FLAG, lsSelectAll);
			String lsFolderId = aoRequest.getParameter(HHSR5Constants.FOLDER_ID);
			if ((null == lsFolderId || lsFolderId.equalsIgnoreCase(HHSR5Constants.NULL))
					&& (null != lsSelectAll && !lsSelectAll.isEmpty()))
			{
				lsFolderPath = FileNetOperationsUtils.setFolderPath(lsUserOrgType, lsUserOrg,
						HHSR5Constants.DOCUMENT_VAULT);
				aoRequest.getPortletSession().setAttribute(HHSR5Constants.FOLDER_PATH, lsFolderPath);
			}
			FileNetOperationsUtils.setPropFilter(loFilterProps, lsFolderPath, lsFolderId, lsUserOrgType, lsUserOrg);
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			loChannelObj = RFPReleaseDocsUtil.getProposalDocsFromVaultChannel(lsUserOrg, lsDocType, loUserSession,
					lsUserOrgType, loFilterProps);
			loChannelObj.setData(HHSR5Constants.AO_USER_ID, lsUserId);
			// Release 5 ends
			loChannelObj.setData(HHSConstants.CHECK_PROPOSAL_EDIT_SUBMIT, Boolean.TRUE);
			String lsProcurementId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
			// if the transaction name is not null
			if (loChannelObj.getData(HHSConstants.BASE_TRANSACTION_NAME) != null)
			{
				resourceAddDocumentExecuteTransaction(aoRequest, loSession, lsDocType, lsReplacingDocId,
						lsDocRefSeqNum, lsProposalId, lsAwardId, lsUploadingDocumentType, loUserSession, lsNextPage,
						loChannelObj, lsProcurementId);
			}
		}
		// Catch the application exception thrown by transaction and set the
		// error message
		// in request object and pass to jsp
		catch (ApplicationException aoAppExp)
		{
			String lsMessage = aoAppExp.getMessage();
			if (lsMessage == null || lsMessage.isEmpty())
			{
				lsMessage = HHSConstants.ERROR_WHILE_PROCESSING_REQUEST;
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsMessage);
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
			}
		}
		// Catch the exception thrown by transaction and set the error message
		// in request object and pass to jsp
		catch (Exception aoExp)
		{
			String lsMessage = aoExp.getMessage();
			if (lsMessage == null)
			{
				lsMessage = HHSConstants.ERROR_WHILE_PROCESSING_REQUEST;
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsMessage);
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
			}
		}
		return new ModelAndView(HHSConstants.ADD_DOC_FROM_VAULT);
	}

	/**
	 * This method is used to get the document list from the filenet and display
	 * it on the overlay
	 * <ul>
	 * <li>Execute <b>displayDocList_filenet</b> Transaction to get the detail
	 * list of the document present in the vault</li>
	 * <li>Get the list of document objects list from Channel Object</li>
	 * <li>Set the list of document bean objects into the request object</li>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * @param aoRequest render request object
	 * @param aoSession portlet session
	 * @param asDocType document type
	 * @param asReplacingDocId replacing document id
	 * @param asDocRefSeqNum document reference number
	 * @param asProposalId proposal id
	 * @param asAwardId award id
	 * @param asUploadingDocumentType uploading document type
	 * @param aoUserSession filenet session bean
	 * @param asNextPage next page number
	 * @param aoChannel channel object to execute transaction
	 * @param asProcurementId procurement id
	 * @throws ApplicationException if any exception occurred
	 */
	private void resourceAddDocumentExecuteTransaction(ResourceRequest aoRequest, PortletSession aoSession,
			String asDocType, String asReplacingDocId, String asDocRefSeqNum, String asProposalId, String asAwardId,
			String asUploadingDocumentType, P8UserSession aoUserSession, String asNextPage, Channel aoChannel,
			String asProcurementId) throws ApplicationException
	{
		List<ExtendedDocument> loRfpDocumentList;
		try
		{
			String lsTransactionName = (String) aoChannel.getData(HHSConstants.BASE_TRANSACTION_NAME);
			// Execute the transaction obtained from the file
			// DocumentBusinessApp.java.
			aoChannel.setData(HHSConstants.PROPOSAL_ID_KEY, asProposalId);
			aoChannel.setData(HHSConstants.UPLOAD_DOC_TYPE, asUploadingDocumentType);
			HHSTransactionManager.executeTransaction(aoChannel, lsTransactionName);
			loRfpDocumentList = RFPReleaseDocsUtil.setSelectedDocumentBean(aoChannel);
			if (null == asNextPage)
			{
				RFPReleaseDocsUtil.setReqRequestParameter(aoSession, aoUserSession, null);
			}
			aoRequest.setAttribute(ApplicationConstants.SESSION_DOCUMENT_LIST, loRfpDocumentList);
			aoRequest.setAttribute(HHSConstants.PROCUREMENT_ID, asProcurementId);
			aoRequest.setAttribute(HHSConstants.PROPOSAL_ID, asProposalId);
			if (null != asAwardId)
			{
				aoRequest.setAttribute(HHSConstants.AWARD_ID, asAwardId);
			}
			if (null != aoRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID))
			{
				aoRequest.setAttribute(HHSConstants.EVALUATION_POOL_MAPPING_ID,
						aoRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID));
			}
			aoRequest.setAttribute(HHSConstants.HIDDEN_DOC_REF_SEQ_NO, asDocRefSeqNum);
			aoRequest.setAttribute(HHSConstants.TOP_LEVEL_FROM_REQ,
					HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.TOP_LEVEL_FROM_REQ));
			aoRequest.setAttribute(HHSConstants.MID_LEVEL_FROM_REQ,
					HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.MID_LEVEL_FROM_REQ));
			aoRequest.setAttribute(HHSConstants.UPLOAD_DOC_TYPE,
					HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.UPLOAD_DOC_TYPE));
			aoRequest.setAttribute(HHSConstants.DOCTYPE, asDocType);
			if (null != asReplacingDocId && !HHSConstants.NULL.equalsIgnoreCase(asReplacingDocId)
					&& !asReplacingDocId.isEmpty())
			{
				aoRequest.setAttribute(HHSConstants.REPLACING_DOCUMENT_ID, asReplacingDocId);
			}
			Boolean loProposalStatusFlag = (Boolean) aoChannel.getData(HHSConstants.PROPOSAL_STATUS_FLAG);
			// if proposal status has been changed by different user
			// simultaneously
			if (!loProposalStatusFlag)
			{
				String lsStatusMessage = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.ADD_DOCUMENT_ERROR);
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsStatusMessage);
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
			}
		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Error Occured While getting document list from filenet", aoAppExp);
			throw aoAppExp;
		}
		catch (Exception aoExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While Getting document list from filenet", aoExp);
			LOG_OBJECT.Error("Error Occured While Getting document list from filenet", loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * This method will handle the resource request from screen S235 and will
	 * fetch organization member details from DB
	 * 
	 * <ul>
	 * <li>1. Get user id from the request</li>
	 * <li>2. Invoke <b>getMemberDetails</b> transaction</li>
	 * <li>3. Get user list from the channel</li>
	 * <li>4. Convert the list to JSON object and set it in response</li>
	 * <li>4. Close the Writer object in the finally block</li>
	 * 
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * @param aoResourceRequest - a ResourceRequest object
	 * @param aoResourceResponse - a ResourceResponse object
	 */
	@SuppressWarnings("unchecked")
	@ResourceMapping("getMemberDetails")
	public void getMemberDetails(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		String lsProviderContactId = HHSPortalUtil.parseQueryString(aoResourceRequest,
				HHSConstants.PROVIDER_CONTRACT_ID);
		Channel loChannel = new Channel();
		PrintWriter loOut = null;
		try
		{
			aoResourceResponse.setContentType(HHSConstants.TEXT_HTML);
			loOut = aoResourceResponse.getWriter();
			loChannel.setData(HHSConstants.AS_USER_ID, lsProviderContactId);
			loChannel.setData(
					HHSConstants.AS_ORGANIZATION_ID,
					aoResourceRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
							PortletSession.APPLICATION_SCOPE));
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.GET_MEMBER_DETAILS);
			Map<String, String> loMap = (Map<String, String>) loChannel.getData(HHSConstants.MEMBER_DETAILS);
			StringBuffer loSbData = new StringBuffer();
			loSbData.append(HHSConstants.GET_MEMBER_DETAILS_STRING_LTERAL1);
			if (loMap != null)
			{
				for (Map.Entry<String, String> loEntry : loMap.entrySet())
				{
					loSbData.append(HHSConstants.DOUBLE_QUOTES).append(loEntry.getKey())
							.append(HHSConstants.GET_MEMBER_DETAILS_STRING_LTERAL2).append(loEntry.getValue())
							.append(HHSConstants.GET_MEMBER_DETAILS_STRING_LTERAL3);
				}
				loOut.write(loSbData.substring(HHSConstants.INT_ZERO, loSbData.length() - HHSConstants.INT_TWO)
						+ HHSConstants.GET_MEMBER_DETAILS_STRING_LTERAL4);
			}
			// Catch the application exception thrown by transaction and set the
			// error message
			// in request object and pass to jsp
		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Error Occured while fetching member details", aoAppExp);
			aoResourceRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
					HHSConstants.ERROR_WHILE_PROCESSING_REQUEST);
			aoResourceRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.ERROR_MESSAGE_TYPE);
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error Occured while fetching member details", aoExp);
			aoResourceRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
					HHSConstants.ERROR_WHILE_PROCESSING_REQUEST);
			aoResourceRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.ERROR_MESSAGE_TYPE);
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
	 * This method will insert all the details of the selected document into
	 * This method is modified to fix 5523 in release 2.6.0. An extra attribute
	 * LS_IS_REQUIRED_DOC is fetched from session & sets in channel.
	 * <b>RFP_DOCUMENT</b> table
	 * <ul>
	 * <li>Get the Organization type and OrganizationName from the request
	 * object</li>
	 * <li>Execute <b>insertRfpDocumentDetails_db</b> transaction of procurement
	 * mapper <b>RFPReleaseDocsUtil</b> class</li>
	 * <li>After Successfully inserting the record redirect user to the screen
	 * no.<b>S212</b> with the updated document list</li>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * @param aoRfpDocument - Document Bean Object
	 * @param aoRequest - ActionRequest Object
	 * @param aoResponse - ActionResponse Object
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@ActionMapping(params = "submit_action=addDocumentFromVault")
	protected void addDocumentFromVaultAction(@ModelAttribute("rfpDocuments") ExtendedDocument aoRfpDocument,
			ActionRequest aoRequest, ActionResponse aoResponse)
	{
		PortletSession loSession = aoRequest.getPortletSession();
		P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		Channel loChannel = new Channel();
		// 5523 fix starts
		Boolean loIsRequiredDoc = null;
		Map<Object, Object> loParameterMap = new HashMap<Object, Object>();
		try
		{
			if (PortletSessionHandler.getAttribute(aoRequest, true, HHSConstants.LS_IS_REQUIRED_DOC) != null)
			{
				loIsRequiredDoc = (Boolean) PortletSessionHandler.getAttribute(aoRequest, false,
						HHSConstants.LS_IS_REQUIRED_DOC);
			}
			// 5523 fix ends
			setNavigationParamsInRender(aoRequest, aoResponse);
			String lsReplacingDocId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.REPLACING_DOCUMENT_ID);
			String lsProcurementId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
			String lsPocurementDocId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.HIDDEN_DOC_REF_SEQ_NO);
			String lsUploadingDocType = HHSPortalUtil.parseQueryString(aoRequest,
					HHSConstants.UPLOAD_DOC_TYPE_FROM_ADD_DOCUMENT);
			String lsProposalId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROPOSAL_ID);
			String lsAwardId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.AWARD_ID);
			String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);
			String lsOrgName = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_NAME, PortletSession.APPLICATION_SCOPE);
			String lsUserOrgType = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
					PortletSession.APPLICATION_SCOPE);
			String lsUserOrgId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
					PortletSession.APPLICATION_SCOPE);
			String lsIsAddendumDoc = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.IS_ADDENDUM_DOC);
			String lsDocumentName = aoRequest.getParameter(HHSConstants.DOC_TITLE);
			String lsDocumentType = aoRequest.getParameter(HHSConstants.ADD_DOC_TYPE);
			String lsDocumentId = aoRequest.getParameter(HHSConstants.DOCID);
			String lsDocumentCategory = aoRequest.getParameter(HHSConstants.DOC_CATEGORY_LOWERCASE);
			String lsDocCreatedBy = aoRequest.getParameter(HHSConstants.SUBMISSION_BY);
			String lsDocModifiedBy = aoRequest.getParameter(HHSConstants.BASE_LAST_MODIFIED_BY);
			Date loCreatedDate = DateUtil.getDate(aoRequest.getParameter(HHSConstants.CREATION_DATE));
			Date loDocModifiedDate = DateUtil.getDate(aoRequest.getParameter(HHSConstants.BASE_LAST_MODIFIED_DATE));
			setParametersMapValue(loParameterMap, lsProcurementId, lsPocurementDocId, lsProposalId, lsUserId,
					lsDocumentName, lsDocumentType, lsDocumentId, lsDocumentCategory, lsDocCreatedBy, lsDocModifiedBy,
					loCreatedDate, loDocModifiedDate);
			if (null != lsAwardId)
			{
				loParameterMap.put(HHSConstants.AWARD_ID, lsAwardId);
			}
			if (null != lsReplacingDocId)
			{
				loParameterMap.put(HHSConstants.REPLACING_DOCUMENT_ID, lsReplacingDocId);
			}
			if (null != lsIsAddendumDoc)
			{
				loParameterMap.put(HHSConstants.IS_ADDENDUM_DOC, lsIsAddendumDoc);
			}
			addDocFromVaultActionFinal(aoRequest, aoResponse, loUserSession, loChannel, loIsRequiredDoc,
					loParameterMap, lsReplacingDocId, lsProcurementId, lsUploadingDocType, lsProposalId, lsAwardId,
					lsOrgName, lsUserOrgType, lsUserOrgId, lsDocumentId, loDocModifiedDate);
			String lsErrorMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
					HHSConstants.RFP_DOC_ADDED_SUCCESS);
			loSession.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg, PortletSession.APPLICATION_SCOPE);
			loSession.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_PASS_TYPE,
					PortletSession.APPLICATION_SCOPE);
		}
		// Catch the exception thrown by transaction and set the error message
		// in request object and pass to jsp
		catch (Exception aoExp)
		{
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
	}

	/**
	 * This method executes transaction for add document from vault based on the
	 * received transaction name
	 * @param aoRequest ActionRequest
	 * @param aoResponse ActionResponse
	 * @param loUserSession Filenet session
	 * @param loChannel Channel object
	 * @param loIsRequiredDoc Required Doc Flag
	 * @param loParameterMap input param map
	 * @param lsReplacingDocId replacing document id
	 * @param lsProcurementId proc id
	 * @param lsUploadingDocType uploading document type
	 * @param lsProposalId proposal id
	 * @param lsAwardId award id
	 * @param lsOrgName organizationname
	 * @param lsUserOrgType user organization type
	 * @param lsUserOrgId user organization id
	 * @param lsDocumentId document id
	 * @param loDocModifiedDate document modified date
	 * @throws ApplicationException If an ApplicationExcpetion occurs
	 */
	private void addDocFromVaultActionFinal(ActionRequest aoRequest, ActionResponse aoResponse,
			P8UserSession loUserSession, Channel loChannel, Boolean loIsRequiredDoc,
			Map<Object, Object> loParameterMap, String lsReplacingDocId, String lsProcurementId,
			String lsUploadingDocType, String lsProposalId, String lsAwardId, String lsOrgName, String lsUserOrgType,
			String lsUserOrgId, String lsDocumentId, Date loDocModifiedDate) throws ApplicationException
	{
		loChannel.setData(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
		loChannel.setData(HHSConstants.AO_PARAMETER_MAP, loParameterMap);
		HashMap loHmDocReqProps = new HashMap();
		loHmDocReqProps.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, true);
		loHmDocReqProps.put(HHSConstants.IS_DOCUMENT_RFP_AWARD_TYPE, true);
		loChannel.setData(HHSConstants.DOCUMENT_TYPE, HHSConstants.EMPTY_STRING);
		loChannel.setData(HHSConstants.DOC_ID, lsDocumentId);
		loChannel.setData(HHSConstants.LO_HM_DOC_REQ_PROPS, loHmDocReqProps);
		loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
		loChannel.setData(HHSConstants.LO_LAST_MOD_HASHMAP, loParameterMap);
		loChannel.setData(HHSConstants.LB_SUCCESS_STATUS, true);
		loChannel.setData(HHSConstants.REPLACING_DOCUMENT_ID, lsReplacingDocId);
		// NT 219 fix
		loChannel.setData(HHSConstants.ORGA_ID, lsUserOrgId);
		// 5523 fix starts
		loChannel.setData(HHSConstants.LO_IS_REQUIRED_DOC, loIsRequiredDoc);
		// 5523 fix ends
		// if the logged in user belongs to the provider organization type
		String lsTransactionName = HHSUtil.addDocumentFromVaultTransactionName(lsUploadingDocType, lsUserOrgType,
				loParameterMap, loDocModifiedDate, lsUserOrgId);
		if (null != lsTransactionName && lsTransactionName.equals(HHSConstants.INS_AWARD_DOC_DETAILS_DB))
		{
			// set attributes for sending notification for award documents
			HashMap<String, Object> loNotificationMap = getNotificationMapForAwardDocuments(aoRequest, lsOrgName);
			loChannel.setData(HHSConstants.LO_HM_NOTIFY_PARAM, loNotificationMap);
			loChannel.setData(HHSConstants.AWARD_ID, lsAwardId);
		}
		HHSTransactionManager.executeTransaction(loChannel, lsTransactionName);
		if (lsTransactionName != null && lsTransactionName.equals(HHSConstants.INS_AWARD_DOC_DETAILS_DB))
		{
			aoResponse.setRenderParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID,
					aoRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID));
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.SEL_DETAIL);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.VIEW_SEL_DETAILS);
		}
		else if (lsTransactionName != null && HHSConstants.INS_PROPOSAL_DOC_DETAILS_DB.equals(lsTransactionName))
		{
			aoResponse.setRenderParameter(HHSConstants.PROPOSAL_ID, lsProposalId);
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.PROC_PROPOSAL_DOC_LIST);
		}
		else if (lsTransactionName != null && HHSConstants.INS_RFP_DOC_DETAILS_DB.equals(lsTransactionName))
		{
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.DISP_RFF_DOC_LIST);
		}
		aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, lsProcurementId);
	}

	/**
	 * This method will return the notification map for award documents
	 * <ul>
	 * <li>Added the alert and notification into alert list and then put the
	 * list into notification map</li>
	 * <li>put the username into request map</li>
	 * </li>Set the agency URL in the NotificationDataBean </li>
	 * <li>put the request map,NotificationDataBean,loNotificationAlertList,
	 * created by, modified by, Entity Id and Entity Type into notification map</li>
	 * </ul>
	 */
	/**
	 * @param aoRequest request object
	 * @param asUserName user name
	 * @return notification map
	 * @throws ApplicationException
	 */
	private HashMap<String, Object> getNotificationMapForAwardDocuments(ActionRequest aoRequest, String asUserName)
			throws ApplicationException
	{
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		String lsCityUrl = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
				HHSConstants.PROP_CITY_URL);
		List<String> loNotificationAlertList = new ArrayList<String>();
		loNotificationAlertList.add(HHSConstants.AL220);
		loNotificationAlertList.add(HHSConstants.NT219);
		NotificationDataBean loNotificationDataBean = new NotificationDataBean();

		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		HashMap<String, String> loLinkMap = new HashMap<String, String>();
		StringBuffer lsBfAgencyUrl = new StringBuffer(256);
		lsBfAgencyUrl.append(lsCityUrl).append(HHSConstants.AWARDS_CONTRACTS_URL)
				.append(aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
		loLinkMap.put(HHSConstants.LINK, lsBfAgencyUrl.toString());
		loNotificationDataBean.setLinkMap(loLinkMap);
		loNotificationDataBean.setAgencyLinkMap(loLinkMap);
		loRequestMap.put(HHSConstants.PROVIDER_NAME, asUserName);
		loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		loNotificationMap.put(ApplicationConstants.ENTITY_ID,
				HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.AWARD_ID));
		loNotificationMap.put(ApplicationConstants.ENTITY_TYPE, HHSConstants.AWARD_UPPER_CASE);
		loNotificationMap.put(HHSConstants.CREATED_BY_USER_ID, lsUserId);
		loNotificationMap.put(HHSConstants.MODIFIED_BY, lsUserId);
		loNotificationMap.put(HHSConstants.AL220, loNotificationDataBean);
		loNotificationMap.put(HHSConstants.NT219, loNotificationDataBean);
		loNotificationMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
		return loNotificationMap;
	}

	/**
	 * This method will set the values into the parameter map which will be
	 * later used to insert the details into the data base
	 * <ul>
	 * <li>1.Set the parameters of the method in a map/li>
	 * 
	 * </ul>
	 * @param aoParameterMap parameter map
	 * @param asProcurementId procurement id
	 * @param asPocurementDocId procurement doc id
	 * @param asProposalId proposal id
	 * @param asUserId user id
	 * @param asDocumentName document name
	 * @param asDocumentType document type
	 * @param asDocumentId document identifier id
	 * @param asDocumentCategory document category
	 * @param asDocCreatedBy docuemnt created by
	 * @param asDocModifiedBy document modified by
	 * @param aoCreatedDate document created date
	 * @param aoDocModifiedDate document modified date
	 */
	private void setParametersMapValue(Map<Object, Object> aoParameterMap, String asProcurementId,
			String asPocurementDocId, String asProposalId, String asUserId, String asDocumentName,
			String asDocumentType, String asDocumentId, String asDocumentCategory, String asDocCreatedBy,
			String asDocModifiedBy, Date aoCreatedDate, Date aoDocModifiedDate)
	{

		aoParameterMap.put(HHSConstants.PROCUREMENT_ID, asProcurementId);
		aoParameterMap.put(HHSConstants.DOC_REF_NO, asPocurementDocId);
		aoParameterMap.put(HHSConstants.PROPOSAL_ID, asProposalId);
		aoParameterMap.put(HHSConstants.DOCUMENT_TITLE, asDocumentName);
		aoParameterMap.put(HHSConstants.DOC_TYPE, asDocumentType);
		aoParameterMap.put(HHSConstants.DOC_CATEGORY, asDocumentCategory);
		aoParameterMap.put(HHSConstants.DOC_ID, asDocumentId);
		aoParameterMap.put(HHSConstants.HHS_DOC_CREATED_BY_ID, asDocCreatedBy);
		aoParameterMap.put(HHSConstants.DOC_CREATED_DATE, aoCreatedDate);
		aoParameterMap.put(HHSConstants.DOC_MODIFIED_DATE, aoDocModifiedDate);
		aoParameterMap.put(HHSConstants.DATE_LAST_MODIFIED, aoDocModifiedDate);
		aoParameterMap.put(HHSConstants.DOC_MODIFIED_BY, asDocModifiedBy);
		aoParameterMap.put(HHSConstants.USER_ID, asUserId);
		aoParameterMap.put(HHSConstants.MOD_BY_USER_ID, asUserId);
	}

	/**
	 * This method will remove the document from the list
	 * <ul>
	 * <li>Get documentId and procurementId from the request object</li>
	 * <li>Execute Transaction with transaction id <b>removeRfpDocs_db</b> which
	 * will remove the file from the data base</li>
	 * <li>Set the user friendly message in the response which will be displayed
	 * to user</li>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * @param aoRequest - ActionRequest Object
	 * @param aoResponse - ActionResponse Object
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	@ActionMapping(params = "submit_action=removeDocumentFromList")
	protected void actionRemoveDocumentFromList(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		Channel loChannel = new Channel();
		HashMap loHmDocReqProps = new HashMap();
		try
		{
			String lsUserName = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			String lsProcurementId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
			String lsDocReferenceId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.HIDDEN_DOC_REF);
			String lsProcurementStatus = (String) loChannel.getData(HHSConstants.PROCUREMENT_STATUS);
			P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			Map<String, String> loParamMap = new HashMap<String, String>();
			String lsDeletedDocumentId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.DEL_DOC_ID);
			setNavigationParamsInRender(aoRequest, aoResponse);
			loParamMap.put(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			loParamMap.put(HHSConstants.USER_ID, lsUserName);
			loParamMap.put(HHSConstants.MOD_BY_USER_ID, lsUserName);
			loParamMap.put(HHSConstants.AS_DEL_DOC_ID, lsDeletedDocumentId);
			loParamMap.put(HHSConstants.DOC_REF_NUM, lsDocReferenceId);
			loChannel.setData(HHSConstants.AO_PARAM_MAP, loParamMap);
			loHmDocReqProps.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, false);
			loChannel.setData(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
			loChannel.setData(HHSConstants.DOCUMENT_TYPE, HHSConstants.EMPTY_STRING);
			loChannel.setData(HHSConstants.DOC_ID, lsDeletedDocumentId);
			loChannel.setData(HHSConstants.LO_HM_DOC_REQ_PROPS, loHmDocReqProps);
			loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
			loChannel.setData(HHSConstants.LS_PROC_STATUS, lsProcurementStatus);
			loChannel.setData(HHSConstants.USER_ID, lsUserName);
			loChannel.setData(HHSConstants.LO_LAST_MOD_HASHMAP, loParamMap);
			loChannel.setData(HHSConstants.LB_SUCCESS_STATUS, true);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.REMOVE_RFP_DOCS_DB);
			aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.DISP_RFF_DOC_LIST);

		}
		// Catch the application exception thrown by transaction and set the
		// error message
		// in request object and pass to jsp
		catch (ApplicationException aoAppExp)
		{
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}

	}

	/**
	 * This method will remove the document from the list
	 * <ul>
	 * <li>Get documentId and procurementId from the request object</li>
	 * <li>Execute Transaction with transaction id <b>removeRfpDocs_db</b> which
	 * will remove the file from the data base</li>
	 * <li>Set the user friendly message in the response which will be displayed
	 * to user</li>
	 * </ul>
	 * 
	 * @param aoRequest - ActionRequest Object
	 * @param aoResponse - ActionResponse Object
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@ActionMapping(params = "submit_action=removeProposalDocumentFromList")
	protected void actionRemoveProposalDocumentFromList(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		Channel loChannel = new Channel();
		HashMap loHmDocReqProps = new HashMap();
		Map<String, String> loParamMap = new HashMap<String, String>();
		String lsProcurementId = null;
		String lsProposalId = null;
		String lsDocRefId = null;
		String lsAwardId = null;
		String lsDocumentNotStarted = null;
		try
		{
			P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			String lsDeletedDocumentId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.DEL_DOC_ID);
			String lsUserName = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			lsDocumentNotStarted = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_DOCUMENT_NOT_STARTED);
			lsDocRefId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.HIDDEN_DOC_REF_SEQ_NO);
			lsProcurementId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
			String lsDocType = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.DOCTYPE);
			lsProposalId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROPOSAL_ID);
			lsAwardId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.AWARD_ID);
			setNavigationParamsInRender(aoRequest, aoResponse);
			loParamMap.put(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			loParamMap.put(HHSConstants.PROPOSAL_ID, lsProposalId);
			loParamMap.put(HHSConstants.AS_DEL_DOC_ID, lsDeletedDocumentId);
			loParamMap.put(HHSConstants.PROC_DOC_ID, lsDocRefId);
			loParamMap.put(HHSConstants.AWARD_ID, lsAwardId);
			loParamMap.put(HHSConstants.STATUS_ID, lsDocumentNotStarted);
			loParamMap.put(HHSConstants.USER_ID, lsUserName);
			loChannel.setData(HHSConstants.AO_PARAM_MAP, loParamMap);
			loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
			loChannel.setData(HHSConstants.DOC_ID, lsDeletedDocumentId);
			loChannel.setData(HHSConstants.DOCUMENT_TYPE, lsDocType);
			loChannel.setData(HHSConstants.PROPOSAL_ID, lsProposalId);
			loHmDocReqProps.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, false);
			loChannel.setData(HHSConstants.LO_HM_DOC_REQ_PROPS, loHmDocReqProps);
			// Execute Transaction to remove proposal documents from list
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.REMOVE_PROPOSAL_DOCS);
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.PROC_PROPOSAL_DOC_LIST);
		}
		// Catch the application exception thrown by transaction and set the
		// error message
		// in request object and pass to jsp
		catch (ApplicationException aoAppExp)
		{
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}

	}

	/**
	 * This method will remove the award documents from the list
	 * <ul>
	 * <li>Get documentId and awardID from the request object</li>
	 * <li>Execute Transaction with transaction id <b>removeAwardDocument</b>
	 * which will remove the file from the data base</li>
	 * <li>Set the user friendly message in the response which will be displayed
	 * to user</li>
	 * </ul>
	 * 
	 * Changes done for Enhancement #6429 for Release 3.4.0
	 * 
	 * @param aoRequest - ActionRequest Object
	 * @param aoResponse - ActionResponse Object
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@ActionMapping(params = "submit_action=removeAwardDocumentFromList")
	protected void actionRemoveAwardDocumentFromList(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		Channel loChannel = new Channel();
		HashMap loHmDocReqProps = new HashMap();
		Map<String, String> loParamMap = new HashMap<String, String>();
		String lsProcurementId = null;
		String lsProposalId = null;
		String lsDocRefId = null;
		String lsAwardId = HHSConstants.EMPTY_STRING;
		// Start || Changes done for Enhancement #6429 for Release 3.4.0
		String lsDeleteAgencyAward = null;
		String lsOrgId = null;
		PortletSession loSession = null;
		// End || Changes done for Enhancement #6429 for Release 3.4.0
		try
		{
			setNavigationParamsInRender(aoRequest, aoResponse);
			// Start || Changes done for Enhancement #6429 for Release 3.4.0
			loSession = aoRequest.getPortletSession();
			P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			lsOrgId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
					PortletSession.APPLICATION_SCOPE);
			String lsDeletedDocumentId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.DEL_DOC_ID);
			if (null != aoRequest.getParameter(HHSConstants.UPLOAD_DOC_TYPE)
					&& aoRequest.getParameter(HHSConstants.UPLOAD_DOC_TYPE).equalsIgnoreCase(
							HHSConstants.STRING_AWARD_DOC))
			{
				lsOrgId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.ORGANIZATION_ID);
			}
			// End || Changes done for Enhancement #6429 for Release 3.4.0
			lsAwardId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.AWARD_ID);
			lsDocRefId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.HIDDEN_DOC_REF_SEQ_NO);
			lsProcurementId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
			String lsDocType = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.DOCTYPE);
			lsProposalId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROPOSAL_ID);
			// Start || Changes done for Enhancement #6429 for Release 3.4.0
			lsDeleteAgencyAward = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.DELETE_AGENCY_AWARD);
			// End || Changes done for Enhancement #6429 for Release 3.4.0
			loParamMap.put(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			loParamMap.put(HHSConstants.PROPOSAL_ID, lsProposalId);
			loParamMap.put(HHSConstants.DOC_ID, lsDeletedDocumentId);
			loParamMap.put(HHSConstants.DOC_REF_NO, lsDocRefId);
			loParamMap.put(HHSConstants.AWARD_ID, lsAwardId);
			loChannel.setData(HHSConstants.AO_PARAM_MAP, loParamMap);
			loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
			loChannel.setData(HHSConstants.DOC_ID, lsDeletedDocumentId);
			loChannel.setData(HHSConstants.DOCUMENT_TYPE, lsDocType);
			loHmDocReqProps.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, false);
			loChannel.setData(HHSConstants.LO_HM_DOC_REQ_PROPS, loHmDocReqProps);
			// Start || Changes done for Enhancement #6429 for Release 3.4.0
			if (null != lsDeleteAgencyAward && lsDeleteAgencyAward.equals(HHSConstants.ONE))
			{
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.REMOVE_AGENCY_AWARD_DOCS);
			}
			else
			{
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.REMOVE_AWARD_DOC);
			}
			// End || Changes done for Enhancement #6429 for Release 3.4.0
			aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			String lsErrorMsg = HHSConstants.DOC_REMOVED_SUCCESS;
			// Start || Changes done for Enhancement #6429 for Release 3.4.0
			if (null != lsOrgId)
			{
				aoResponse.setRenderParameter(HHSConstants.ORGA_ID, lsOrgId);
			}
			if (null != PortalUtil.parseQueryString(aoRequest, HHSConstants.UPLOAD_DOC_TYPE))
			{
				aoResponse.setRenderParameter(HHSConstants.UPLOAD_DOC_TYPE,
						PortalUtil.parseQueryString(aoRequest, HHSConstants.UPLOAD_DOC_TYPE));
			}
			if (null != PortalUtil.parseQueryString(aoRequest, HHSConstants.TOP_LEVEL_FROM_REQ))
			{
				aoResponse.setRenderParameter(HHSConstants.TOP_LEVEL_FROM_REQ,
						PortalUtil.parseQueryString(aoRequest, HHSConstants.TOP_LEVEL_FROM_REQ));
			}
			if (null != PortalUtil.parseQueryString(aoRequest, HHSConstants.EVALUATION_POOL_MAPPING_ID))
			{
				aoResponse.setRenderParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID,
						PortalUtil.parseQueryString(aoRequest, HHSConstants.EVALUATION_POOL_MAPPING_ID));
			}
			if (null != PortalUtil.parseQueryString(aoRequest, HHSConstants.IS_FINANCIAL))
			{
				aoResponse.setRenderParameter(HHSConstants.IS_FINANCIAL,
						PortalUtil.parseQueryString(aoRequest, HHSConstants.IS_FINANCIAL));
			}
			if (null != PortalUtil.parseQueryString(aoRequest, HHSConstants.HIDDEN_HDNCONTRACTID))
			{
				aoResponse.setRenderParameter(HHSConstants.HIDDEN_HDNCONTRACTID,
						PortalUtil.parseQueryString(aoRequest, HHSConstants.HIDDEN_HDNCONTRACTID));
			}
			if (null != PortalUtil.parseQueryString(aoRequest, HHSConstants.AS_PROC_STATUS))
			{
				aoResponse.setRenderParameter(HHSConstants.AS_PROC_STATUS,
						PortalUtil.parseQueryString(aoRequest, HHSConstants.AS_PROC_STATUS));
			}
			// End || Changes done for Enhancement #6429 for Release 3.4.0
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_PASS_TYPE);
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.SEL_DETAIL);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.VIEW_SEL_DETAILS);
		}
		// Catch the application exception thrown by transaction and set the
		// error message
		// in request object and pass to jsp
		catch (ApplicationException aoAppExp)
		{
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}

	}

	/**
	 * This method performs render action of S235 screen.
	 * <p>
	 * <ul>
	 * <li>1. Read required parameters from request</li>
	 * <li>2. Invoke the transaction fetchProposalDetails which fetches various
	 * informations like Custom questions, proposal site details, organization
	 * member details (needed to be displayed on screen)</li>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoRequest - Render Request
	 * @param aoResponse - Render Response
	 * @return model and view
	 */
	@SuppressWarnings("unchecked")
	@RenderMapping(params = "render_action=procurementProposalDetails")
	protected ModelAndView getProposalDetails(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		PortletSession loSession = aoRequest.getPortletSession();
		String lsUserOrgType = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
				PortletSession.APPLICATION_SCOPE);
		String lsOrgId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsProcurementId = PortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
		String lsProposalId = PortalUtil.parseQueryString(aoRequest, HHSConstants.PROPOSAL_ID);
		String lsReadOnlySection = PortalUtil.parseQueryString(aoRequest, HHSConstants.READ_ONLY_SEC);
		ProposalDetailsBean loVersionInfoBean = null;
		if (lsReadOnlySection != null && !lsReadOnlySection.equalsIgnoreCase(HHSConstants.READ_ONLY_SEC))
		{
			aoRequest.setAttribute(HHSConstants.READ_ONLY_SEC, true);
		}
		ProposalDetailsBean loProposalDetailsBean = null;
		try
		{
			getPageHeader(aoRequest, lsProcurementId);
			String lsSelectChildScreen = (String) aoRequest.getAttribute(HHSConstants.SELECTED_CHILD_TAB);
			String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);
			if (lsSelectChildScreen != null && lsSelectChildScreen.equalsIgnoreCase(HHSConstants.PROPOSAL_DET)
					&& lsProcurementId != null && lsProposalId != null)
			{
				Channel loChannel = new Channel();
				loChannel.setData(HHSConstants.PROPOSAL_ID_KEY, lsProposalId);
				loChannel.setData(HHSConstants.AS_USER_TYPE, lsUserOrgType);
				loChannel.setData(HHSConstants.AS_ORG_ID, lsOrgId);
				loChannel.setData(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
				loChannel.setData(HHSConstants.AS_USER_ID, lsUserId);
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_PROPOSAL_DETAILS);
				loProposalDetailsBean = getProposalDetailsFinal(aoRequest, lsProposalId, loChannel);
				aoRequest.setAttribute(HHSConstants.STATUS_CHANNEL, loChannel);
				aoRequest.setAttribute(HHSConstants.CHANNEL_ACCESS, loChannel);
				aoRequest.setAttribute(HHSConstants.PROPOSAL_STATUS, loProposalDetailsBean.getProposalStatusName());
				loProposalDetailsBean.setServiceUnitFlag((String) loChannel.getData(HHSConstants.SERVICE_UNIT_VALUE));
				// Modified as a part of release 3.1.0 for enhancement request
				// 6024
				loVersionInfoBean = (ProposalDetailsBean) loChannel.getData(HHSConstants.VERSION_INFO_BEAN);
				if (null != loVersionInfoBean)
				{
					String loProposalStatus = loVersionInfoBean.getProposalStatusId();
					String loEvalGrpStatus = loVersionInfoBean.getEvaluationGroupStatus();
					String loQuesVersion = loVersionInfoBean.getQuesVersion();
					String loDocVersion = loVersionInfoBean.getDocVersion();
					if (null != loProposalStatus)
					{
						if (loProposalStatus.equals(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_PROPOSAL_SUBMITTED))
								&& (proposalCond2(loEvalGrpStatus))
								&& (proposalCond1(loVersionInfoBean, loQuesVersion, loDocVersion)))
						{
							String lsErrorMsg = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE,
									ApplicationConstants.NOT_LATEST_VERSION);
							aoRequest.setAttribute(ApplicationConstants.ERROR_INFORMATION, lsErrorMsg);
						}
					}
				}
				if (aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE) != null)
				{
					String lsErrorMsg = (String) aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE);
					aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
					aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
							ApplicationConstants.MESSAGE_FAIL_TYPE);
				}
			}
			else
			{
				throw new ApplicationException("Invalid access to screen S235 : userId:: " + lsUserId
						+ HHSConstants.PROPOSALID + lsProposalId + HHSConstants.PROCUREMENTID + lsProcurementId);
			}
			setExceptionMessageInResponse(aoRequest);
		}
		// Handling Exception while rendering proposal details
		catch (ApplicationException loExp)
		{
			setGenericErrorMessage(aoRequest);

		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error occurred while rendering proposal details", loExp);
			setGenericErrorMessage(aoRequest);
		}
		return new ModelAndView(HHSConstants.MODEL_VIEW_PROVIDER_PROPOSAL_DET,
				HHSConstants.PROPOSAL_DETAILS_BEAN_UPPERCASE, loProposalDetailsBean);
	}
	/**
	 * This method gets internal call from getProposalDetails which
	 * performs render action of S235 screen.
	 * @param loEvalGrpStatus - String
	 * @return boolean
	 * @throws ApplicationException - if any exception occurs
	 */
	private boolean proposalCond2(String loEvalGrpStatus) throws ApplicationException
	{
		return null != loEvalGrpStatus
				&& loEvalGrpStatus.equals(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_EVALUATION_GROUP_RELEASED));
	}
	/**
	 * This method gets internal call from getProposalDetails which
	 * performs render action of S235 screen.
	 * @param loVersionInfoBean - ProposalDetailsBean
	 * @param loQuesVersion String
	 * @param loDocVersion String
	 * @return boolean
	 */
	private boolean proposalCond1(ProposalDetailsBean loVersionInfoBean, String loQuesVersion, String loDocVersion)
	{
		// Modified to solve Release Addendum Issue in Emergency Build 3.1.1
		return !loVersionInfoBean.getLatestVersionQues().equals(HHSConstants.STRING_ZERO)
				&& !loVersionInfoBean.getLatestVersionQues().equals(loQuesVersion)
				|| !loVersionInfoBean.getLatestVersionDoc().equals(loDocVersion);
	}

	/**
	 * Modified as a part of release 3.1.0 for enhancement request 6024
	 * 
	 * This method retreives values from channel object and sets them in request
	 * object and channel object to pass it further
	 * @param aoRequest RenderRequest
	 * @param asProposalId Proposal Id
	 * @param aoChannel channel object
	 * @return ProposalDetailsBean object
	 */
	private ProposalDetailsBean getProposalDetailsFinal(RenderRequest aoRequest, String asProposalId, Channel aoChannel)
	{
		ProposalDetailsBean loProposalDetailsBean;
		Boolean loProposalDetailsReadonlyFlag = (Boolean) aoChannel
				.getData(HHSConstants.PROPOSAL_DETAILS_READONLY_FLAG);
		loProposalDetailsBean = (ProposalDetailsBean) aoChannel.getData(HHSConstants.PROPOSAL_DETAILS_BEAN);
		if (loProposalDetailsBean == null)
		{
			loProposalDetailsBean = new ProposalDetailsBean();
		}
		List<ProposalQuestionAnswerBean> loQueAnsList = (List<ProposalQuestionAnswerBean>) aoChannel
				.getData(HHSConstants.CUSTOM_QUE_LIST);
		List<CommentsHistoryBean> loCommentBean = (List<CommentsHistoryBean>) aoChannel
				.getData(HHSConstants.COMMENT_LIST);
		List<SiteDetailsBean> loSiteDetails = (List<SiteDetailsBean>) aoChannel.getData(HHSConstants.SITE_DETAIL_LIST);
		List<Map<String, String>> loOrgMemList = (List<Map<String, String>>) aoChannel
				.getData(HHSConstants.ORG_MEMBER_LIST);
		List<Map<String, String>> loCompPoolList = (List<Map<String, String>>) aoChannel
				.getData(HHSConstants.SELECTED_POOL);
		List<ProposalFilterBean> loCommentList = (List<ProposalFilterBean>) aoChannel
				.getData(HHSConstants.PROPOSAL_COMMENT_LIST);
		loProposalDetailsBean.setQuestionAnswerBeanList(loQueAnsList);
		if (loQueAnsList != null && !loQueAnsList.isEmpty())
		{
			loProposalDetailsBean.setVersionNoQuestion(loQueAnsList.get(0).getVersionNo());
		}
		loProposalDetailsBean.setSiteDetailsList(loSiteDetails);
		// Start Release 5 user notification
		aoRequest.setAttribute(
				HHSConstants.PROPOSAL_DETAILS_READONLY_FLAG,
				getFinancialsReadOnly(
						aoRequest,
						(String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
								PortletSession.APPLICATION_SCOPE))
						|| loProposalDetailsReadonlyFlag);
		// End Release 5 user notification
		aoRequest.setAttribute(HHSConstants.LO_ORG_MEM_LIST, loOrgMemList);
		aoRequest.setAttribute(HHSConstants.LO_COMMENT_BEAN, loCommentBean);
		aoRequest.setAttribute(HHSConstants.PROPOSAL_ID, asProposalId);
		aoRequest.setAttribute(HHSConstants.SELECTED_POOL, loCompPoolList);
		aoChannel.setData(HHSConstants.PROCUREMENT_STATUS,
				((ProcurementInfo) aoRequest.getAttribute(HHSConstants.PROCUREMENT_BEAN)).getStatus());
		aoChannel.setData(HHSConstants.PROPOSAL_STATUS, loProposalDetailsBean.getProposalStatus());
		Boolean loShowCommentsFlag = Boolean.FALSE;
		for (ProposalFilterBean loProposalFilterBean : loCommentList)
		{
			if (loProposalFilterBean.getUserComment() != null
					&& !(loProposalFilterBean.getUserComment().equals(HHSConstants.EMPTY_STRING)))
			{
				loShowCommentsFlag = Boolean.TRUE;
			}
		}
		if (loShowCommentsFlag)
		{
			aoChannel.setData(HHSConstants.COMMENT_SIZE, loCommentList.size());
		}
		else
		{
			aoChannel.setData(HHSConstants.COMMENT_SIZE, 0);
		}
		return loProposalDetailsBean;
	}

	/**
	 * This method performs the save action of S235 screen.
	 * <p>
	 * <ul>
	 * <li>1. Read required parameters from request</li>
	 * <li>2. Invoke the transaction saveProposalDetails(which first tries to
	 * update the data in data base, if update is successful it invokes Render
	 * method, else will insert new record to DB)</li>
	 * </ul>
	 * 
	 * @param aoProposalDetailsBean - Proposal details from jsp
	 * @param aoResult - BindingResult
	 * @param aoRequest - ActionRequest
	 * @param aoResponse - ActionResponse
	 * @param aoModel - model map object
	 */
	@ActionMapping(params = "submit_action=saveProposalDetails")
	protected void saveProposalDetails(
			@ModelAttribute("ProposalDetailsBean") ProposalDetailsBean aoProposalDetailsBean, BindingResult aoResult,
			ActionRequest aoRequest, ActionResponse aoResponse, ModelMap aoModel)
	{
		proposalDetailsValidator.validate(aoProposalDetailsBean, aoResult);
		String lsProcurementId = aoProposalDetailsBean.getProcurementId();
		String lsProposalId = aoProposalDetailsBean.getProposalId();
		aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, lsProcurementId);
		aoResponse.setRenderParameter(HHSConstants.PROPOSAL_ID, lsProposalId);
		aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.PROC_PROPOSAL_DETAILS);
		setNavigationParamsInRender(aoRequest, aoResponse);
		aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
		try
		{
			if (!aoResult.hasErrors())
			{
				String lsSaveType = PortalUtil.parseQueryString(aoRequest, HHSConstants.SAVE_TYPE);
				PortletSession loSession = aoRequest.getPortletSession();
				String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
						PortletSession.APPLICATION_SCOPE);
				aoProposalDetailsBean.setModifiedBy(lsUserId);
				Channel loChannel = new Channel();
				loChannel.setData(HHSConstants.AO_PROP_DETAILS, aoProposalDetailsBean);
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.SAVE_PROP_DETAILS);
				aoModel.remove(HHSConstants.PROPOSAL_DETAILS_BEAN_UPPERCASE);
				if (lsSaveType != null && lsSaveType.equalsIgnoreCase(HHSConstants.SAVE_NEXT_BUTTON))
				{
					aoResponse.setRenderParameter(HHSConstants.MID_LEVEL_FROM_REQ, HHSConstants.PROPOSAL_DOCUMENTS);
					aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.PROC_PROPOSAL_DOC_LIST);
				}
			}
		}
		// Handling Exception while saving procurement
		catch (ApplicationException loEx)
		{
			Map<String, ProposalDetailsBean> loParamMap = new HashMap<String, ProposalDetailsBean>();
			loParamMap.put(HHSConstants.PROPOSAL_DETAIL_BEAN_KEY, aoProposalDetailsBean);
			String lsErrorMsg = HHSConstants.ERROR_WHILE_PROCESSING_REQUEST;
			setExceptionMessageFromAction(aoResponse, lsErrorMsg, ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap,
					loEx);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.PROC_PROPOSAL_DETAILS);
		}

		// handling exception other than Application Exception.
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Exception Occured while saving proposal : ", loEx);
			String lsErrorMsg = HHSConstants.ERROR_WHILE_PROCESSING_REQUEST;
			setExceptionMessageFromAction(aoResponse, lsErrorMsg, ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.PROC_PROPOSAL_DETAILS);
		}
	}

	/**
	 * This method gets performs next action on proposal details screen
	 * <ul>
	 * <li>1. Read required parameters from request</li>
	 * <li>2. Set the parameters in response to be passed to proposal document
	 * screen. Namely -procurementId, proposalId, action, topLevelFromRequest,
	 * midLevelFromRequest, render_action</li>
	 * </ul>
	 * 
	 * @param aoRequest - ActionRequest
	 * @param aoResponse - ActionResponse
	 */
	@ActionMapping(params = "submit_action=nextProposalDetails")
	protected void actionNextProposalDetails(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		String lsProcurementId = PortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
		String lsProposalId = PortalUtil.parseQueryString(aoRequest, HHSConstants.PROPOSAL_ID);
		aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, lsProcurementId);
		aoResponse.setRenderParameter(HHSConstants.PROPOSAL_ID, lsProposalId);
		setNavigationParamsInRender(aoRequest, aoResponse);
		aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
		aoResponse.setRenderParameter(HHSConstants.MID_LEVEL_FROM_REQ, HHSConstants.PROPOSAL_DOCUMENTS);
		aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.PROC_PROPOSAL_DOC_LIST);
	}

	/**
	 * This method renders submit Proposal screen on click of Submit Proposal
	 * tab.
	 * 
	 * <ul>
	 * <li>Get the procurement Id from request</li>
	 * <li>Get the document content of Application Terms & Conditions type
	 * document and set in request attribute</li>
	 * <li>Set proposal ID in request</li>
	 * <li>Render to submit Proposal jsp</li>
	 * </ul>
	 * 
	 * @param aoRequest RenderRequest
	 * @param aoResponse RenderResponse
	 * @return ModelAndView to display the providerSubmitProposal.jsp page
	 */
	@SuppressWarnings("unchecked")
	@RenderMapping(params = "render_action=renderProviderProposal")
	protected ModelAndView renderProviderProposal(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		ModelAndView loModelAndView = null;
		try
		{
			String lsProcurementId = PortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
			String lsProposalId = PortalUtil.parseQueryString(aoRequest, HHSConstants.PROPOSAL_ID);
			// get navigation parameters
			getPageHeader(aoRequest, lsProcurementId);
			String lsSelectChildScreen = (String) aoRequest.getAttribute(HHSConstants.SELECTED_CHILD_TAB);
			ProposalDetailsBean loVersionInfoBean = null;
			if (lsSelectChildScreen != null && lsSelectChildScreen.equalsIgnoreCase(HHSConstants.SUBMIT_PROPOSAL))
			{
				Navigation loStatusMap = (Navigation) aoRequest.getAttribute(HHSR5Constants.SUBMIT_PROPOSAL);
				if (loStatusMap != null
						&& (loStatusMap.getTabState().equalsIgnoreCase(HHSConstants.E) || loStatusMap.getTabState()
								.equalsIgnoreCase(HHSConstants.D)) && loStatusMap.isSelected())
				{
					Channel loChannel = new Channel();
					P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
							ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
					loChannel.setData(HHSConstants.AO_USER_SESSION, loUserSession);
					loChannel.setData(HHSConstants.AS_DOC_TYPE,
							ApplicationConstants.DOCUMENT_TYPE_APPLICATION_TERMS_AND_CONDITIONS);
					loChannel.setData(HHSConstants.PROPOSAL_ID, lsProposalId);
					loChannel.setData(HHSConstants.PROCUREMENT_ID, lsProcurementId);
					// call transaction to get document content for Application
					// terms
					// and conditions
					HHSTransactionManager.executeTransaction(loChannel, HHSConstants.GET_DOC_CONTENT_FOR_SUB_PROPOSAL);
					Map<String, String> loProposalMap = (Map<String, String>) loChannel
							.getData(HHSConstants.PROPOSAL_MAP);
					// Modified as a part of release 3.1.0 for enhancement
					// request 6024
					loVersionInfoBean = (ProposalDetailsBean) loChannel.getData(HHSConstants.VERSION_INFO_BEAN);
					if (null != loVersionInfoBean)
					{
						String loProposalStatus = loVersionInfoBean.getProposalStatusId();
						String loEvalGrpStatus = loVersionInfoBean.getEvaluationGroupStatus();
						String loQuesVersion = loVersionInfoBean.getQuesVersion();
						String loDocVersion = loVersionInfoBean.getDocVersion();
						if (null != loProposalStatus)
						{
							if (loProposalStatus.equals(PropertyLoader.getProperty(
									HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_SUBMITTED))
									&& (proposalCond2(loEvalGrpStatus))
									&& (proposalCond1(loVersionInfoBean, loQuesVersion, loDocVersion)))
							{
								String lsErrorMsg = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE,
										ApplicationConstants.NOT_LATEST_VERSION);
								aoRequest.setAttribute(ApplicationConstants.ERROR_INFORMATION, lsErrorMsg);
							}
						}
					} // set parameters in request attribute
					aoRequest.setAttribute(HHSConstants.DISPLAY_TERMS_CONDITION, getTermsAndCondition(loChannel));
					aoRequest.setAttribute(HHSConstants.PROPOSAL_TITLE, loProposalMap.get(HHSConstants.PROP_TITLE));
					aoRequest.setAttribute(HHSConstants.PROPOSAL_ID, lsProposalId);
					aoRequest.setAttribute(HHSConstants.PROPOSAL_STATUS,
							loProposalMap.get(HHSConstants.STATUS_UPPERCASE));
					aoRequest.setAttribute(HHSConstants.ERROR_FLAG, aoRequest.getParameter(HHSConstants.ERROR_FLAG));
					loChannel.setData(HHSConstants.PROCUREMENT_STATUS,
							loProposalMap.get(HHSConstants.PROCUREMENT_STATUS_ID));
					loChannel.setData(HHSConstants.PROPOSAL_STATUS, loProposalMap.get(HHSConstants.PROPOSAL_STATUS_ID));
					aoRequest.setAttribute(HHSConstants.STATUS_CHANNEL, loChannel);
					aoRequest.setAttribute(HHSConstants.CHANNEL_ACCESS, loChannel);
					// set error messages in request object if exist
					setExceptionMessageInResponse(aoRequest);
					// Start Release 5 user notification
					aoRequest.setAttribute(
							HHSR5Constants.SUBMIT_PROPOSAL_READONLY_FLAG,
							getFinancialsReadOnly(
									aoRequest,
									(String) aoRequest.getPortletSession()
											.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
													PortletSession.APPLICATION_SCOPE)));
					// End Release 5 user notification
					loModelAndView = new ModelAndView(HHSConstants.PROVIDER_SUBMIT_PROPOSAL,
							HHSConstants.AUTHENICATION, new AuthenticationBean());
				}
				else
				{
					ProposalEvaluationController loProposalEvaluationController = new ProposalEvaluationController();
					aoRequest.setAttribute(HHSConstants.TOP_LEVEL_FROM_REQ, HHSR5Constants.PROPOSAL_SUMMARY);
					aoRequest.removeAttribute(HHSConstants.MID_LEVEL_FROM_REQ);
					aoRequest.setAttribute(HHSR5Constants.TAKE_VALUE_FROM_ATTRIBUTE, HHSR5Constants.TRUE);
					aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.PROPOSAL_SUBMIT_FAIL));
					aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
							ApplicationConstants.MESSAGE_FAIL_TYPE);
					loModelAndView = loProposalEvaluationController
							.renderProposalSummaryProvider(aoRequest, aoResponse);
				}
			}
		}
		catch (ApplicationException loExp)
		{
			setGenericErrorMessage(aoRequest);
			LOG_OBJECT.Error("Error occured while rendering on submit proposal", loExp);
		}
		catch (Exception loExp)
		{
			setGenericErrorMessage(aoRequest);
		}
		return loModelAndView;
	}

	/**
	 * This method performs the submission of proposal
	 * <ul>
	 * <li>Perform Server Side Validation</li>
	 * <li>Set channel objects</li>
	 * <li>Calling Transaction submitProposal</li>
	 * <li>If status is true then get the Authentication Status[Method
	 * :authenticateLoginUser]</li>
	 * <li>Get Validation Status flag of Validation of all required fields are
	 * complete on S236 (proposal details) and S238 (proposal documents) and set
	 * as render Parameter[Method :checkAllRequiredFieldsCompleted
	 * QueryId:getProposalDetailsCount, getProposalDocumentCount ]</li>
	 * <li>If no errors, update the Proposal’s status to Submitted</li>
	 * <li>If no errors, update the Proposal?s status to Submitted</li>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * @param aoAuthenticationParam authentication bean object
	 * @param aoResult binding result object
	 * @param aoRequest ActionRequest
	 * @param aoResponse ActionResponse
	 */
	@SuppressWarnings("rawtypes")
	@ActionMapping(params = "submit_action=submitProposal")
	protected void submitProposal(@ModelAttribute("Authentication") AuthenticationBean aoAuthenticationParam,
			BindingResult aoResult, ActionRequest aoRequest, ActionResponse aoResponse)
	{
		List<HhsAuditBean> loAuditBeanList = new ArrayList<HhsAuditBean>();
		P8UserSession loUserSession = null;
		try
		{
			String lsCityUrl = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
					HHSConstants.PROP_CITY_URL);

			loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			// set navigation parameters in render
			setNavigationParamsInRender(aoRequest, aoResponse);
			validator.validate(aoAuthenticationParam, aoResult);
			if (!aoResult.hasErrors())
			{
				submitValidProposal(aoAuthenticationParam, aoRequest, aoResponse, loAuditBeanList, loUserSession,
						lsCityUrl);
			}
			else
			{
				aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.RENDER_PROVIDER_PROPOSAL);
				aoResponse.setRenderParameter(HHSConstants.ERROR_FLAG, HHSConstants.ERROR_FLAG);
			}
		}
		catch (ApplicationException loExp)
		{
			Map<String, HhsAuditBean> loParamMap = new HashMap<String, HhsAuditBean>();
			String lsErrorMsg = HHSConstants.ERROR_WHILE_PROCESSING_REQUEST;
			setExceptionMessageFromAction(aoResponse, lsErrorMsg, ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap,
					loExp);
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.RENDER_PROVIDER_PROPOSAL);
			aoResponse.setRenderParameter(HHSConstants.ERROR_FLAG, HHSConstants.ERROR_FLAG);
			LOG_OBJECT.Error("Error occured while rendering on submit proposal", loExp);
		}
		catch (Exception loException)
		{
			LOG_OBJECT.Error("Error occured while rendering on submit proposal", loException);
			String lsErrorMsg = HHSConstants.ERROR_WHILE_PROCESSING_REQUEST;
			setExceptionMessageFromAction(aoResponse, lsErrorMsg, ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.RENDER_PROVIDER_PROPOSAL);
			aoResponse.setRenderParameter(HHSConstants.ERROR_FLAG, HHSConstants.ERROR_FLAG);
		}
	}

	/**
	 * This method performs the submission of valid proposal
	 * <ul>
	 * <li>Perform Server Side Validation</li>
	 * <li>Set channel objects</li>
	 * <li>Calling Transaction submitProposal</li>
	 * <li>If status is true then get the Authentication Status[Method
	 * :authenticateLoginUser]</li>
	 * <li>Get Validation Status flag of Validation of all required fields are
	 * complete on S236 (proposal details) and S238 (proposal documents) and set
	 * as render Parameter[Method :checkAllRequiredFieldsCompleted
	 * QueryId:getProposalDetailsCount, getProposalDocumentCount ]</li>
	 * <li>If no errors, update the Proposal’s status to Submitted</li>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * @param aoAuthenticationParam authentication bean object
	 * @param aoRequest ActionRequest
	 * @param aoResponse ActionResponse
	 * @param loAuditBeanList Audit Bean List
	 * @param loUserSession Filenetsession
	 * @param lsCityUrl city url
	 * @throws ApplicationException
	 */
	private void submitValidProposal(AuthenticationBean aoAuthenticationParam, ActionRequest aoRequest,
			ActionResponse aoResponse, List<HhsAuditBean> loAuditBeanList, P8UserSession loUserSession, String lsCityUrl)
			throws ApplicationException
	{
		// authenticate user by calling method validateUser
		Map loAuthenticateMap = validateUser(aoAuthenticationParam.getUserName(), aoAuthenticationParam.getPassword(),
				aoRequest);
		Boolean loAuthenticateStatus = (Boolean) loAuthenticateMap.get(HHSConstants.IS_VALID_USER);
		// if user is not authenticated, set error message in response
		// and call render method
		if (!loAuthenticateStatus)
		{
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE,
					PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.MESSAGE_M38));
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE);
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.RENDER_PROVIDER_PROPOSAL);
			aoResponse.setRenderParameter(HHSConstants.ERROR_FLAG, HHSConstants.ERROR_FLAG);
		}
		// proceed further to submit proposal if user is authenticated
		else
		{
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			String lsProposalId = aoRequest.getParameter(HHSConstants.PROPOSAL_ID);
			// set attributes in channel object
			Channel loChannel = new Channel();
			loChannel.setData(HHSConstants.PROPOSAL_ID, aoRequest.getParameter(HHSConstants.PROPOSAL_ID));
			loChannel.setData(HHSConstants.PROCUREMENT_ID, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			loChannel.setData(HHSConstants.USER_ID, lsUserId);
			loChannel.setData(
					HHSConstants.AS_ORGANIZATION_ID,
					aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
							PortletSession.APPLICATION_SCOPE));
			loChannel.setData(HHSConstants.AUDIT_BEAN_LIST, loAuditBeanList);
			loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);

			// set param values for unlocking proposal and submitting to
			// different pool
			Map<String, Object> loInputParam = new HashMap<String, Object>();
			loInputParam.put(HHSConstants.STATUS_COMPETITION_POOL_NO_PROPOSALS, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_COMPETITION_POOL_NO_PROPOSALS));
			loInputParam.put(HHSConstants.STATUS_COMPETITION_POOL_PROPOSALS_RECEIVED, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_COMPETITION_POOL_PROPOSALS_RECEIVED));
			loInputParam.put(HHSConstants.STATUS_PROPOSAL_DRAFT, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_DRAFT));
			loInputParam.put(HHSConstants.PROPOSAL_ID, lsProposalId);
			loInputParam.put(HHSConstants.USER_ID, lsUserId);
			loChannel.setData(HHSConstants.INPUT_PARAM_MAP, loInputParam);
			getNotificationMapForSubmitProposal(aoRequest, lsCityUrl, lsUserId, loChannel);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.SUBMIT_PROPOSAL);
			Boolean loRequiredFieldsFlag = (Boolean) loChannel.getData(HHSConstants.REQ_FIELDS_FLAG);
			Boolean loProposalStatuFlag = (Boolean) loChannel.getData(HHSConstants.PROPOSAL_STATUS_FLAG);
			Boolean loEvaluationSentFlag = (Boolean) loChannel.getData(HHSConstants.EVALUATION_SENT_FLAG);
			// Start || Changes done for Enhancement #6577 for Release 3.10.0
			Boolean locompPoolNotCancelledFlag = (Boolean) loChannel.getData(HHSConstants.COMP_POOL_NOT_CANCELLED);
			Map<String, String> loStatusMap = (Map<String, String>) loChannel.getData(HHSConstants.STATUS_MAP_KEY);
			String lsAgencyName = HHSR5Constants.EMPTY_STRING;
			if (null != loStatusMap && null != loStatusMap.get(HHSConstants.AGENCY_NAME_COLUMN))
			{
				lsAgencyName = loStatusMap.get(HHSConstants.AGENCY_NAME_COLUMN);
			}
			// check if required field flags are completed. If not set
			// error message and render to submit proposal screen
			if (!loRequiredFieldsFlag)
			{
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.REQ_FIELDS_SUBMIT_PROPOSAL));
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE);
				aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.RENDER_PROVIDER_PROPOSAL);
				aoResponse.setRenderParameter(HHSConstants.ERROR_FLAG, HHSConstants.ERROR_FLAG);
			}
			else if (!locompPoolNotCancelledFlag)
			{
				final String lsMessage = MessageFormat.format(PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.COMP_POOL_CANCELLED), lsAgencyName);
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, lsMessage);
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE);
				aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.RENDER_PROVIDER_PROPOSAL);
				aoResponse.setRenderParameter(HHSConstants.ERROR_FLAG, HHSConstants.ERROR_FLAG);
			}
			// End || Changes done for Enhancement #6577 for Release 3.10.0
			else if (!loEvaluationSentFlag)
			{
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.COMP_POOL_NOT_AVAILABLE));
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE);
				aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.RENDER_PROVIDER_PROPOSAL);
				aoResponse.setRenderParameter(HHSConstants.ERROR_FLAG, HHSConstants.ERROR_FLAG);
			}
			else if (!loProposalStatuFlag)
			{
				setResponseForProposalDocAction(aoResponse);
			}
			else
			{
				processSubmitProposalForErrorMessage(aoRequest, aoResponse, loChannel);
			}
		}
	}

	/**
	 * This method is used to get notification map for submit Proposal
	 * <ul>
	 * <li>Create a Notification map that is passed to the channed</li>
	 * <li>Creates the request param map and add the provider link, agency link
	 * and city link to this map</li>
	 * <li>Then add the request map, created by, modified by, Entity ID and
	 * Entity Type to the Notification map</li>
	 * </ul>
	 * @param aoRequest ActionRequest object
	 * @param asCityUrl a string value of city url
	 * @param asUserId a string value of user Id
	 * @param aoChannel Channel object
	 */
	private void getNotificationMapForSubmitProposal(ActionRequest aoRequest, String asCityUrl, String asUserId,
			Channel aoChannel) throws ApplicationException
	{
		try
		{
			HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
			HashMap<String, String> loRequestMap = new HashMap<String, String>();
			StringBuilder lsBfProviderUrl = new StringBuilder(256);
			lsBfProviderUrl.append(aoRequest.getScheme()).append(HHSConstants.NOTIFICATION_HREF_1)
					.append(aoRequest.getServerName()).append(HHSConstants.COLON).append(aoRequest.getServerPort())
					.append(aoRequest.getContextPath()).append(HHSConstants.PROPOSAL_SUMMARY_URL)
					.append(aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			StringBuilder lsBfAgencyUrl = new StringBuilder(256);
			lsBfAgencyUrl.append(asCityUrl).append(HHSConstants.AGENCY_TASK_INBOX_URL);
			loRequestMap.put(HHSConstants.PROVIDER_LINK, lsBfProviderUrl.toString());
			loRequestMap.put(HHSConstants.AGENCY_LINK, lsBfAgencyUrl.toString());
			// Modified as a part of release 3.1.0 for enhancement request 6024
			String lsCityUrl = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
					HHSConstants.PROP_CITY_URL);
			StringBuilder lsPropAndEvalSummUrl = new StringBuilder(256);
			lsPropAndEvalSummUrl.append(lsCityUrl).append(HHSConstants.PROP_AND_EVAL_SUMM_AGENCY_LINK)
					.append(aoRequest.getParameter(HHSConstants.PROCUREMENT_ID))
					.append(HHSConstants.EVAL_GROUP_PARAMETER);
			loRequestMap.put(HHSConstants.PROP_AND_EVAL_SUMM_LINK, lsPropAndEvalSummUrl.toString());
			loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
			loNotificationMap.put(ApplicationConstants.ENTITY_ID, aoRequest.getParameter(HHSConstants.PROPOSAL_ID));
			loNotificationMap.put(ApplicationConstants.ENTITY_TYPE, HHSConstants.PROPOSAL);
			loNotificationMap.put(HHSConstants.CREATED_BY_USER_ID, asUserId);
			loNotificationMap.put(HHSConstants.MODIFIED_BY, asUserId);
			aoChannel.setData(HHSConstants.LO_HM_NOTIFY_PARAM, loNotificationMap);
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occured while setting notification map for submit proposal", aoEx);
			throw new ApplicationException(
					"Error was occurred while get notfication for getNotificationMapForSubmitProposal", aoEx);
		}
	}

	/**
	 * This method processes output returned from channel for error messages.
	 * <ul>
	 * <li>If any error message exist, render to submit proposal screen with
	 * appropriate error message. Else, render to proposal summary with success
	 * message.</li>
	 * </ul>
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse object
	 * @param aoChannel a channel object
	 * @throws ApplicationException If an Application Exception occurs
	 */
	private void processSubmitProposalForErrorMessage(ActionRequest aoRequest, ActionResponse aoResponse,
			Channel aoChannel) throws ApplicationException
	{
		Boolean loUpdateProposalFlag = (Boolean) aoChannel.getData(HHSConstants.UPDATE_PROPOSAL_FLAG);
		// check for update proposal flag. If true, return
		// to
		// proposal summary with success message
		if (loUpdateProposalFlag)
		{
			String lsMessage = MessageFormat.format(PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.PROPOSAL_SUBMIT_SUCCESFULLY), aoRequest
					.getParameter(HHSConstants.PROPOSAL_TITLE));
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, lsMessage);
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_PASS_TYPE);
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.PROPOSAL_SUMMARY_LOWERCASE);
			aoResponse.setRenderParameter(HHSConstants.MID_LEVEL_FROM_REQ, HHSConstants.PROPOSAL_SUMMARY);
			aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID,
					aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
		}
	}

	/**
	 * This method handles the process of rendering the Release rfp screen
	 * <ul>
	 * <li>Get the procurement Id from request</li>
	 * <li>Set navigation tab related information using getPageHeader()</li>
	 * <li>Set error messages if there occurs any error</li>
	 * <li>Return releaseRfp view name</li>
	 * </ul>
	 * 
	 * @param aoRequest - RenderRequest object
	 * @param aoResponse - RenderResponse
	 * @return loModelAndView to display the releaserfp.jsp page
	 */
	@RenderMapping(params = "render_action=renderReleaseRfp")
	protected ModelAndView renderReleaseRfp(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		try
		{
			String loProcurementId = PortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
			String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			String lsProcurementPlannedStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_PROCUREMENT_PLANNED);
			getPageHeader(aoRequest, loProcurementId);
			Channel loChannel = new Channel();
			loChannel.setData(HHSConstants.PROCUREMENT_ID_KEY, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			loChannel.setData(HHSConstants.ORG_TYPE, lsUserOrgType);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_PROC_ADDENDUM_DATA);
			Integer loProcurementAddendumDataCount = (Integer) loChannel.getData(HHSConstants.PROC_ADDENDUM_DATA_COUNT);
			String lsProcurementStatus = (String) loChannel.getData(HHSConstants.PROCUREMENT_STATUS);
			if (loProcurementAddendumDataCount != null && loProcurementAddendumDataCount > HHSConstants.INT_ZERO
					&& !lsProcurementStatus.equals(lsProcurementPlannedStatus))
			{
				aoRequest.setAttribute(HHSConstants.UNPUBLISHED_DATA_MSG_KEY, HHSConstants.UNPUBLISHED_DATA_MSG);
			}
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
					aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE));
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE_TYPE));
			aoRequest.setAttribute(HHSConstants.SER_NAME_LIST,
					ApplicationSession.getAttribute(aoRequest, HHSConstants.SER_NAME_LIST));
			aoRequest.setAttribute(HHSConstants.MISSING_INFO_LIST,
					ApplicationSession.getAttribute(aoRequest, HHSConstants.MISSING_INFO_LIST));
			aoRequest.setAttribute(HHSConstants.CHANNEL_ACCESS, loChannel);
			setExceptionMessageInResponse(aoRequest);
		}
		catch (ApplicationException loExp)
		{
			setGenericErrorMessage(aoRequest);
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error occured while rendering release RFP", loExp);
			setGenericErrorMessage(aoRequest);
		}
		return new ModelAndView(HHSConstants.RELEASE_RFP, HHSConstants.AUTHENICATION, new AuthenticationBean());
	}

	/**
	 * This method handles the action on click of Release RFP button
	 * <ul>
	 * <li>Perform server side validation on Release rfp screen</li>
	 * <li>If validation fails, redirect user to same screen with error messages
	 * </li>
	 * <li>Else execute Transaction to release RFP.</li>
	 * <li>It will check if user is authenticated, Epin is associated with
	 * Procurements, associated services have evidence flag checked, atleast one
	 * required document type is selected in Proposal Configuration, atleast one
	 * row in Evaluation Criteria, atleast one rfp document is uploaded for
	 * procurement</li>
	 * <li>If any error exist, set the appropriate error message in request
	 * object</li>
	 * <li>Else update procurement status to released and set success message in
	 * request object</li>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * @param aoAuthenticationBean - an authentication bean object
	 * @param aoResult - a binding result object indicating if errors exist
	 * @param aoRequest - an ActionRequest object
	 * @param aoResponse - an ActionResponse object
	 */
	@SuppressWarnings("rawtypes")
	@ActionMapping(params = "submit_action=releaseRfp")
	public void actionReleaserfp(@ModelAttribute("Authentication") AuthenticationBean aoAuthenticationBean,
			BindingResult aoResult, ActionRequest aoRequest, ActionResponse aoResponse)
	{
		HhsAuditBean loAuditBean = new HhsAuditBean();
		try
		{
			validator.validate(aoAuthenticationBean, aoResult);
			aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID,
					aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			if (!aoResult.hasErrors())
			{
				Map loAuthenticateMap = validateUser(aoAuthenticationBean.getUserName(),
						aoAuthenticationBean.getPassword(), aoRequest);
				Boolean loAuthStatus = (Boolean) loAuthenticateMap.get(HHSConstants.IS_VALID_USER);
				if (!loAuthStatus)
				{
					aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.MESSAGE_M38));
					aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
							ApplicationConstants.MESSAGE_FAIL_TYPE);
					aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
					aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.RENDER_RELEASE_RFP);
					setNavigationParamsInRender(aoRequest, aoResponse);
				}
				else
				{
					releaseRFPFinal(aoRequest, aoResponse, loAuditBean);
				}
			}
			else
			{
				aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.RENDER_RELEASE_RFP);
			}
		}
		catch (ApplicationException loExp)
		{
			Map<String, HhsAuditBean> loParamMap = new HashMap<String, HhsAuditBean>();
			loParamMap.put(HHSConstants.HHS_AUDIT_BEAN, loAuditBean);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap, loExp);
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.RENDER_RELEASE_RFP);
			setNavigationParamsInRender(aoRequest, aoResponse);
			LOG_OBJECT.Error("Error occured while release RFP", loExp);
		}
		catch (Exception loException)
		{
			LOG_OBJECT.Error("Error occured while release RFP", loException);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
	}

	/**
	 * This method executes the transaction for Release RFP button
	 * <ul>
	 * <li>It executes Transaction to release RFP.</li>
	 * <li>It will check if user is authenticated, Epin is associated with
	 * Procurements, associated services have evidence flag checked, atleast one
	 * required document type is selected in Proposal Configuration, atleast one
	 * row in Evaluation Criteria, atleast one rfp document is uploaded for
	 * procurement</li>
	 * <li>If any error exist, set the appropriate error message in request
	 * object</li>
	 * <li>Else update procurement status to released and set success message in
	 * request object</li>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest
	 * @param aoResponse ActionResponse
	 * @param loAuditBean HhsAuditBean
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	private void releaseRFPFinal(ActionRequest aoRequest, ActionResponse aoResponse, HhsAuditBean loAuditBean)
			throws ApplicationException
	{
		String lsServerName = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
				HHSR5Constants.SERVER_NAME_FOR_PROVIDER_BATCH);
		String lsServerPort = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
				HHSR5Constants.SERVER_PORT_FOR_PROVIDER_BATCH);
		String lsContextPath = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
				HHSR5Constants.CONTEXT_PATH_FOR_PROVIDER_BATCH);
		String lsAppProtocol = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
				HHSR5Constants.SERVER_PROTOCOL_FOR_PROVIDER_BATCH);
		Channel loChannel = new Channel();
		String lsProcurementId = aoRequest.getParameter(HHSConstants.PROCUREMENT_ID);
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		loAuditBean.setAuditTableIdentifier(HHSConstants.ACCELERATOR_AUDIT);
		loAuditBean.setEntityType(HHSConstants.PROCUREMENT);
		loAuditBean.setData(HHSConstants.PROC_STATUS_CHANGED_RELEASED);
		loAuditBean.setEntityId(aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
		loAuditBean.setEventName(HHSConstants.RELEASE);
		loAuditBean.setUserId(lsUserId);
		loAuditBean.setEventType(HHSConstants.PROC_RELEASE);
		getNotificationMapForReleaseRfp(aoRequest, lsServerName, lsServerPort, lsContextPath, lsAppProtocol, loChannel,
				lsUserId);
		Map<String, String> loProcurementInputMap = new HashMap<String, String>();
		loProcurementInputMap.put(HHSConstants.PROCUREMENT_ID, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
		loProcurementInputMap.put(HHSConstants.USER_ID, lsUserId);
		loProcurementInputMap.put(HHSConstants.IS_REL_PROCUREMENT, HHSConstants.TRUE);
		loChannel.setData(HHSConstants.PROCUREMENT_ID, lsProcurementId);
		loChannel.setData(HHSConstants.USER_ID, lsUserId);
		loChannel.setData(HHSConstants.AUDIT_BEAN, loAuditBean);
		loChannel.setData(HHSConstants.PROCUREMENT_MAP, loProcurementInputMap);
		loChannel.setData(HHSConstants.CLOSE_GROUP_FLAG, HHSConstants.TRUE);
		// insert grp pool mapping details
		RFPReleaseBean loRFPReleaseBean = new RFPReleaseBean();
		loRFPReleaseBean.setProcurementId(lsProcurementId);
		loRFPReleaseBean.setCreatedByUserId(lsUserId);
		loRFPReleaseBean.setModifiedByUserId(lsUserId);
		loRFPReleaseBean.setEvalGroupStatus(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_COMPETITION_POOL_RELEASED));
		loChannel.setData(HHSConstants.RFP_RELEASE_BEAN, loRFPReleaseBean);
		// insert evaluation grp details
		Procurement loProcBean = new Procurement();
		loProcBean.setProcurementId(lsProcurementId);
		loProcBean.setCreatedBy(lsUserId);
		loProcBean.setModifiedBy(lsUserId);
		loProcBean.setEvaluationGroupTitle(HHSConstants.EVALUATION_GROUP_TITLE);
		loProcBean.setEvalGroupStatus(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_EVALUATION_GROUP_RELEASED));
		loChannel.setData(HHSConstants.PROCUREMENT_BEAN, loProcBean);
		loChannel.setData(HHSR5Constants.PROCUREMENT_PSR_PCOF_MAP, new HashMap<String, Boolean>());
		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.RELEASE_RFP);
		processReleaseRFPForErrors(aoRequest, aoResponse, loChannel);
	}

	/**
	 * <ul>
	 * <li>Create a local object Notification map</li>
	 * <li>Create the Notification Alert List that is added to the Notification
	 * Map</li>
	 * <li>Creates the request param map</li>
	 * <li>Create NotificationDataBean type object and set the provider list,
	 * agency list, linkMap and agencyLinkMap attributes</li>
	 * <li>Then add the request map, created by, modified by, Entity ID, Entity
	 * Type,NotificationDataBean to the Notification map</li>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest
	 * @param asServerName a string value of server name
	 * @param asServerPort a string value of server port
	 * @param asContextPath a string value of context path
	 * @param asAppProtocol a string value of application protocol
	 * @param aoChannel Channel object
	 * @param asUserId a string value of user Id
	 */
	private void getNotificationMapForReleaseRfp(ActionRequest aoRequest, String asServerName, String asServerPort,
			String asContextPath, String asAppProtocol, Channel aoChannel, String asUserId)
	{
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		StringBuffer lsBfProviderUrl = new StringBuffer(256);
		lsBfProviderUrl.append(asAppProtocol).append(HHSConstants.NOTIFICATION_HREF_1).append(asServerName)
				.append(HHSConstants.COLON).append(asServerPort).append(HHSConstants.FORWARD_SLASH)
				.append(asContextPath).append(HHSConstants.PROCUREMENT_SUMMARY_PROVIDER_URL)
				.append(aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
		StringBuffer lsBfAgencyUrl = new StringBuffer(256);
		lsBfAgencyUrl.append(aoRequest.getScheme()).append(HHSConstants.NOTIFICATION_HREF_1)
				.append(aoRequest.getServerName()).append(HHSConstants.COLON).append(aoRequest.getServerPort())
				.append(aoRequest.getContextPath()).append(HHSConstants.PROCUREMENT_SUMMARY_AGENCY_URL)
				.append(aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
		List<String> loNotificationAlertList = new ArrayList<String>();
		loNotificationAlertList.add(HHSConstants.AL201);
		loNotificationAlertList.add(HHSConstants.NT201);
		loNotificationAlertList.add(HHSConstants.AL202);
		loNotificationAlertList.add(HHSConstants.NT202);
		loNotificationMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
		NotificationDataBean loNotificationAL201 = new NotificationDataBean();
		NotificationDataBean loNotificationAL202 = new NotificationDataBean();
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		HashMap<String, String> loProviderLinkMap = new HashMap<String, String>();
		HashMap<String, String> loAgencyLinkMap = new HashMap<String, String>();
		loProviderLinkMap.put(HHSConstants.PROVIDER_LINK, lsBfProviderUrl.toString());
		loAgencyLinkMap.put(HHSConstants.AGENCY_LINK, lsBfAgencyUrl.toString());
		loNotificationAL201.setLinkMap(loProviderLinkMap);
		loNotificationAL202.setAgencyLinkMap(loAgencyLinkMap);
		loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		loNotificationMap.put(ApplicationConstants.ENTITY_ID, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
		loNotificationMap.put(ApplicationConstants.ENTITY_TYPE, HHSConstants.PROCUREMENT);
		loNotificationMap.put(HHSConstants.CREATED_BY_USER_ID, asUserId);
		loNotificationMap.put(HHSConstants.MODIFIED_BY, asUserId);
		loNotificationMap.put(HHSConstants.AL201, loNotificationAL201);
		loNotificationMap.put(HHSConstants.NT201, loNotificationAL201);
		loNotificationMap.put(HHSConstants.AL202, loNotificationAL202);
		loNotificationMap.put(HHSConstants.NT202, loNotificationAL202);
		aoChannel.setData(HHSConstants.LO_HM_NOTIFY_PARAM, loNotificationMap);
	}

	/**
	 * This method will process the channel output and display appropriate
	 * errors
	 * 
	 * <ul>
	 * <li>Check the status flag for epin</li>
	 * <li>If true, check for service evidence flag, else render to release rfp
	 * screen with appropriate error message</li>
	 * <li>Check for services associated with procurement with evidence flag
	 * unchecked</li>
	 * <li>If any such service exist, display appropriate error message on
	 * release rfp screen else check for required count</li>
	 * <li>Check for required document count, evaluation criteria count, rfp
	 * document count and COF approval flag</li>
	 * <li>If any of the above condition fails, display appropriate error
	 * message on release rfp screen</li>
	 * <li>Else check for update procurement status. If updated, display success
	 * message on procurement roadmap screen</li>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * @param aoRequest - an ActionRequest object
	 * @param aoResponse - an ActionResponse object
	 * @param aoChannel - a channel object
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings("unchecked")
	private void processReleaseRFPForErrors(ActionRequest aoRequest, ActionResponse aoResponse, Channel aoChannel)
			throws ApplicationException
	{
		Map<String, Object> loServiceNameMap = (Map<String, Object>) aoChannel.getData(HHSConstants.SERV_NAME_MAP);
		List<String> loServiceNameList = null;
		Integer loServiceCount = null;
		if (null != loServiceNameMap)
		{
			loServiceNameList = (List<String>) loServiceNameMap.get(HHSConstants.SER_NAME_LIST);
			List<String> loEvidenceIdList = (List<String>) loServiceNameMap.get(HHSConstants.ELEMENT_ID_LIST);
			if (null != loEvidenceIdList)
			{
				loServiceCount = loEvidenceIdList.size();
			}
		}
		if (null != loServiceNameList && loServiceNameList.size() > HHSConstants.INT_ZERO)
		{
			ApplicationSession.setAttribute(loServiceNameList, aoRequest, HHSConstants.SER_NAME_LIST);
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.REL_RFP_EVI_CHECK));
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE);
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.RENDER_RELEASE_RFP);
			setNavigationParamsInRender(aoRequest, aoResponse);
		}
		else
		{
			RFPReleaseBean loRfpRelease = (RFPReleaseBean) aoChannel.getData(HHSConstants.RFP_REQUISITES);
			Boolean loCofApprovedFlag = (Boolean) aoChannel.getData(HHSConstants.APPROVED_CFP_FLAGS);
			Boolean loValidateStatusFlag = (Boolean) aoChannel.getData(HHSConstants.VAL_STATUS_FLAG);
			if (null != loRfpRelease
					&& (loRfpRelease.getReqDocTypeCount() == HHSConstants.INT_ZERO
							|| loRfpRelease.getReqDocCount() == HHSConstants.INT_ZERO
							|| loRfpRelease.getEvaluationCriteriaCount() == HHSConstants.INT_ZERO
							|| loRfpRelease.getCompetitionPoolCount() == HHSConstants.INT_ZERO
							|| loRfpRelease.getDuplicatePoolCount() != HHSConstants.INT_ZERO || (null != loServiceCount && loServiceCount == HHSConstants.INT_ZERO))
					|| !loCofApprovedFlag || !loValidateStatusFlag)
			{
				processReleaseRFPForErrorsFinal(aoRequest, aoResponse, loServiceCount, loRfpRelease,
						(Map<String, Boolean>) aoChannel.getData(HHSR5Constants.PROCUREMENT_PSR_PCOF_MAP),
						loValidateStatusFlag);
			}
			else
			{
				Boolean loUpdateStatus = (Boolean) aoChannel.getData(HHSConstants.UPDATE_PROC_STATUS);
				if (loUpdateStatus)
				{
					aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.PROC_RELEASED_SUCCESFULLY));
					aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
							ApplicationConstants.MESSAGE_PASS_TYPE);
					aoResponse.setRenderParameter(HHSConstants.RESET_SESSION_PROC, HHSConstants.TRUE);
				}
				else
				{
					aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE,
							HHSConstants.ERROR_WHILE_PROCESSING_REQUEST);
					aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
							ApplicationConstants.MESSAGE_PASS_TYPE);
					aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
					aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.RENDER_RELEASE_RFP);
				}
			}
		}
	}

	/**
	 * Method changed in R5 This method displays appropriate error message on
	 * release rfp screen
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ACtionResponse object
	 * @param loServiceCount services count
	 * @param loRfpRelease RFPReleaseBean object
	 * @param asProcurementPSRPCofMap map to depict approval of cof/psr
	 * @param loValidateStatusFlag flag to depict validation status
	 * @throws ApplicationException If an ApplicationException Occurs
	 */
	private void processReleaseRFPForErrorsFinal(ActionRequest aoRequest, ActionResponse aoResponse,
			Integer loServiceCount, RFPReleaseBean loRfpRelease, Map<String, Boolean> aoProcurementPSRPCofMap,
			Boolean loValidateStatusFlag) throws ApplicationException
	{
		aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
				HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.REL_RFP_MISSING_INFO));
		aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		List<String> loMissingInfoList = new ArrayList<String>();
		if (!loValidateStatusFlag)
		{
			loMissingInfoList.add(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
					HHSConstants.ASSIGN_EPIN_BEFORE_RELEASE));
		}
		if (null != loServiceCount && loServiceCount == HHSConstants.INT_ZERO)
		{
			loMissingInfoList.add(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
					HHSConstants.PROC_SERVICE_MISSING));
		}
		pcofPsrErrors(aoProcurementPSRPCofMap, loMissingInfoList);
		if (loRfpRelease != null)
		{
			if (loRfpRelease.getReqDocTypeCount() == HHSConstants.INT_ZERO)
			{
				loMissingInfoList.add(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.REQ_DOC_TYPE_MISSING));
			}
			if (loRfpRelease.getReqDocCount() == HHSConstants.INT_ZERO)
			{
				loMissingInfoList.add(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.RFP_DOC_MISSING));
			}
			if (loRfpRelease.getEvaluationCriteriaCount() == HHSConstants.INT_ZERO)
			{
				loMissingInfoList.add(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.EVAL_CRITERIA_MISSING));
			}
			if (loRfpRelease.getCompetitionPoolCount() == HHSConstants.INT_ZERO)
			{
				loMissingInfoList.add(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.COMPETITION_POOL_MISSING));
			}
			if (loRfpRelease.getDuplicatePoolCount() != HHSConstants.INT_ZERO)
			{
				loMissingInfoList.add(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.COMPETITION_POOL_DUPLICATE));
			}
		}
		ApplicationSession.setAttribute(loMissingInfoList, aoRequest, HHSConstants.MISSING_INFO_LIST);
		aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
		aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.RENDER_RELEASE_RFP);
		setNavigationParamsInRender(aoRequest, aoResponse);
	}

	/**
	 * Method added in R5 for PSR PCOF error validation message
	 * @param aoProcurementPSRPCofMap
	 * @param aoMissingInfoList
	 * @throws ApplicationException
	 */
	private void pcofPsrErrors(Map<String, Boolean> aoProcurementPSRPCofMap, List<String> aoMissingInfoList)
			throws ApplicationException
	{
		if (!aoProcurementPSRPCofMap.get(HHSR5Constants.APPROVE_PCOF_FLAG))
		{
			aoMissingInfoList.add(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
					HHSConstants.PROC_COF_NOT_APPROVED));
		}
		if (!aoProcurementPSRPCofMap.get(HHSR5Constants.APPROVE_PSR_FLAG))
		{
			aoMissingInfoList.add(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
					HHSR5Constants.PROC_PSR_NOT_APPROVED));
		}
		if (aoProcurementPSRPCofMap.get(HHSR5Constants.APPROVE_PSR_FLAG)
				&& !aoProcurementPSRPCofMap.get(HHSR5Constants.APPROVE_PSR_AMOUNT_FLAG))
		{
			aoMissingInfoList.add(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
					HHSR5Constants.PROC_PSR_AMOUNT_ERROR));
		}
		if (aoProcurementPSRPCofMap.get(HHSR5Constants.APPROVE_PCOF_FLAG)
				&& !aoProcurementPSRPCofMap.get(HHSR5Constants.APPROVE_PCOF_AMOUNT_FLAG))
		{
			aoMissingInfoList.add(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
					HHSR5Constants.PROC_PCOF_AMOUNT_ERROR));
		}
		if (aoProcurementPSRPCofMap.get(HHSR5Constants.APPROVE_PSR_FLAG)
				&& !aoProcurementPSRPCofMap.get(HHSR5Constants.APPROVE_PSR_DATE_FLAG))
		{
			aoMissingInfoList.add(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
					HHSR5Constants.PROC_PSR_DATES_ERROR));
		}
		if (aoProcurementPSRPCofMap.get(HHSR5Constants.APPROVE_PCOF_FLAG)
				&& !aoProcurementPSRPCofMap.get(HHSR5Constants.APPROVE_PCOF_DATE_FLAG))
		{
			aoMissingInfoList.add(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
					HHSR5Constants.PROC_PCOF_DATES_ERROR));
		}
	}

	/**
	 * This function will load evaluation criteria page for Accelerator and
	 * agency users
	 * <ul>
	 * <li>1.Get procurementId from request.</li>
	 * <li>2.Set procurementId in Channel object and execute transaction
	 * fetchEvaluationCriteria</li>
	 * <li>3.Get the RFPReleaseBean from the Channel, which is to be rendered on
	 * the jsp</li>
	 * <li>4. Set the procurement status, to be used on the jsp, in the request.
	 * </li>
	 * <li>1.return the ModelAndView reference containind the data and the name
	 * of the jsp to be rendered
	 * </ul>
	 * 
	 * @param aoRequest - a RenderRequest object
	 * @param aoResponse - a RenderResponse object
	 * @return jsp page name(evaluationCriteria.jsp) along with required bean
	 */

	@RenderMapping(params = "render_action=evaluationCriteria")
	protected ModelAndView renderEvaluationCriteria(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		RFPReleaseBean loRFPReleaseBean = null;
		String lsProcurementId = PortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
		Channel loChannel = new Channel();
		loChannel.setData(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
		PortletSession loSession = aoRequest.getPortletSession();
		// Fetching user org type from session
		String lsUserOrgType = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
				PortletSession.APPLICATION_SCOPE);
		try
		{
			String lsProcurementPlannedStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_PROCUREMENT_PLANNED);
			getPageHeader(aoRequest, lsProcurementId);
			loChannel.setData(HHSConstants.ORG_TYPE, lsUserOrgType);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_EVAL_CRIERIA);
			Integer loProcurementAddendumDataCount = (Integer) loChannel.getData(HHSConstants.PROC_ADDENDUM_DATA_COUNT);
			String lsProcurementStatus = (String) loChannel.getData(HHSConstants.PROCUREMENT_STATUS);
			if (loProcurementAddendumDataCount != null && loProcurementAddendumDataCount > HHSConstants.INT_ZERO
					&& !lsProcurementStatus.equals(lsProcurementPlannedStatus))
			{
				aoRequest.setAttribute(HHSConstants.UNPUBLISHED_DATA_MSG_KEY, HHSConstants.UNPUBLISHED_DATA_MSG);
			}
			aoRequest.setAttribute(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			aoRequest.setAttribute(HHSConstants.ORG_TYPE, lsUserOrgType);
			// Fetching RFPReleaseBean from Channel object
			loRFPReleaseBean = (RFPReleaseBean) loChannel.getData(HHSConstants.EVAL_CRITERIA_DETAILS);
			if (loRFPReleaseBean == null)
			{
				loRFPReleaseBean = new RFPReleaseBean();
			}
			aoRequest.setAttribute(HHSConstants.CHANNEL_ACCESS, loChannel);
			// Modified as a part of release 3.1.0 for enhancement request 6024
			Integer loPublishedReleasedCount = (Integer) loChannel.getData(HHSConstants.PUBLISHED_RELEASED_COUNT);
			if (null != loPublishedReleasedCount && loPublishedReleasedCount == 0)
			{
				aoRequest.setAttribute(HHSConstants.IS_READ_ONLY, true);
			}
			if (lsUserOrgType.equalsIgnoreCase(ApplicationConstants.AGENCY_ORG))
			{
				aoRequest.setAttribute(HHSConstants.IS_READ_ONLY, true);
			}
			setExceptionMessageInResponse(aoRequest);
		}
		// handling exception while getting evaluation criteria details
		catch (ApplicationException loEx)
		{
			setGenericErrorMessage(aoRequest);
		}
		// handling exception other than Application Exception.
		catch (Exception loEx)
		{
			setGenericErrorMessage(aoRequest);
			LOG_OBJECT.Error("Exception Occured while getting evaluation criteria details : ", loEx);
		}
		aoRequest.setAttribute(HHSConstants.PROCUREMENT_STATUS,
				(String) loChannel.getData(HHSConstants.PROCUREMENT_STATUS));
		return new ModelAndView(HHSConstants.EVAL_CRITERIA, HHSConstants.RFP_REL_BEAN, loRFPReleaseBean);
	}

	/**
	 * This method is hit when the <b>"Save"</b> button is clicked on page 252
	 * <ul>
	 * <li>The following actions are made on the click of the button</li>
	 * 
	 * <li>Method performs server side validations.</li>
	 * <li>If validation fails, it persists the values and render the page with
	 * appropriate errors”</li>
	 * <li>If validation fails, it persists the values and render the page with
	 * appropriate errors?</li>
	 * <li>If validations pass successfully then set criteria details data
	 * fetched from the jsp in the channel and execute transaction
	 * "saveEvaluationCriteria"</li>
	 * <li>Set the render action in response.</li>
	 * </ul>
	 * 
	 * @param aoRFPReleaseBean rfp release bean object
	 * @param aoResult binding result bean
	 * @param aoRequest action request object
	 * @param aoResponse action response object
	 * @param aoModelMap model map bean object
	 */
	@ActionMapping(params = "submit_action=processEvaluationCriteria")
	public void processEvaluationAction(@ModelAttribute("RFPReleaseBean") RFPReleaseBean aoRFPReleaseBean,
			BindingResult aoResult, ActionRequest aoRequest, ActionResponse aoResponse, ModelMap aoModelMap)
	{
		validator.validate(aoRFPReleaseBean, aoResult);
		evaluationCriteriaDetailsValidator.validate(aoRFPReleaseBean, aoResult);
		setNavigationParamsInRender(aoRequest, aoResponse);
		String lsProcurementId = aoRequest.getParameter(HHSConstants.PROCUREMENT_ID);
		if (!aoResult.hasErrors())
		{
			try
			{
				String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
						ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
				aoRFPReleaseBean.setCreatedByUserId(lsUserId);
				aoRFPReleaseBean.setModifiedByUserId(lsUserId);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.EVAL_CRITERIA);
				String lsNextAction = aoRequest.getParameter(HHSConstants.NEXT_ACTION);
				aoRFPReleaseBean.setProcurementId(lsProcurementId);
				Channel loChannel = new Channel();
				loChannel.setData(HHSConstants.EVAL_CRITERIA_DETAILS, aoRFPReleaseBean);
				loChannel.setData(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
				Map<String, String> loLastModifiedHashMap = new HashMap<String, String>();
				loLastModifiedHashMap.put(HHSConstants.MOD_BY_USER_ID, lsUserId);
				loLastModifiedHashMap.put(HHSConstants.PROCUREMENT_ID, lsProcurementId);
				loChannel.setData(HHSConstants.LO_LAST_MOD_HASHMAP, loLastModifiedHashMap);
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.SAVE_EVAL_CRITERIA);
				Integer loProcurementStatus = Integer.parseInt((String) loChannel
						.getData(HHSConstants.PROCUREMENT_STATUS));
				aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
				aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_STATUS, loProcurementStatus.toString());
				aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, lsProcurementId);
				if (lsNextAction != null && lsNextAction.equalsIgnoreCase(HHSConstants.SAVE))
				{
					aoModelMap.remove(HHSConstants.RFP_REL_BEAN);
				}
			}
			// handling exception while processing evaluation criteria details.
			catch (ApplicationException loEx)
			{
				Map<String, RFPReleaseBean> loParamMap = new HashMap<String, RFPReleaseBean>();
				loParamMap.put(HHSConstants.AO_RFP_REL_BEAN, aoRFPReleaseBean);
				setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
						ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap, loEx);

			}
			// handling exception other than Application Exception.
			catch (Exception loEx)
			{
				LOG_OBJECT.Error("Exception Occured while saving evaluation criteria details : ", loEx);
				setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
						ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
			}
		}
		else
		{
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
			aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.EVAL_CRITERIA);
		}
	}

	/**
	 * 
	 * This method is used to render the financials screen within the RFP
	 * Release Details tab. <li>
	 * This is accessible to the <b>Agency users only</b> in order to maintain
	 * the <u>Procurement Certification of Funds</u> details against a
	 * procurement.</li> <li>
	 * It holds the information about the <i>Procurement</i> against the fiscal
	 * years that occur from the Start date - End date. <b>P.S.</b> No contract
	 * has yet been generated.
	 * <ul>
	 * <li>Procurement Value : <i>Estimated Procurement Value</i></li>
	 * <li>Contract start date : <i>Planned start date</i></li>
	 * <li>Contract end date : <i>Planned end date</i></li>
	 * <li>Certification of Funds Status : <i>'Not Submitted'</i> ( by default)
	 * else mapped to status respective to the procurementID</li>
	 * </ul>
	 * </li> <li>
	 * <u>ACCO and the CFO</u> roles review the information once filled by the
	 * agency users, during which the page will open in a <b>read only</b> mode.
	 * </li> <li>
	 * Sets procurement financials details in request.
	 * <ul>
	 * <li>1. Set <code>procurementId</code> in Channel.</li>
	 * <li>2. Execute transaction <code>fetchProcurementCoF</code></li>
	 * <li>3. Set Output Bean <code>ProcurementCOF</code> in request. Fields
	 * shown on the UI apart from the grid have been explained in the
	 * <code>financials.jsp</code></li>
	 * </ul>
	 * </li>
	 * 
	 * @param aoRequest render request
	 * @param asProcurementId procurementId
	 */
	@RenderMapping(params = "render_action=procurementCOFDetails")
	protected void fetchProcurementCOFDetails(RenderRequest aoRequest, String asProcurementId)
	{
		try
		{
			ProcurementCOF loPprocureCof = null;
			Channel loChannel = new Channel();
			loChannel.setData(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_PROC_COF);
			loPprocureCof = (ProcurementCOF) loChannel.getData(HHSConstants.PROC_COF_DETAILS);
			aoRequest.setAttribute(HHSConstants.PROC_CFP_DETAILS, loPprocureCof);
			setExceptionMessageInResponse(aoRequest);
		}
		catch (ApplicationException loApplicationException)
		{
			setGenericErrorMessage(aoRequest);
		}
		catch (Exception loApplicationException)
		{
			setGenericErrorMessage(aoRequest);
		}
	}

	/**
	 * Below method will fetch list of mandatory and optional document list
	 * <ul>
	 * <li>1. Get the proposal id from request and set it to channel</li>
	 * <li>2. Invoke <b>getProposalDocumentList</b> transaction</li>
	 * <li>3. Get list of required and optional document from channel and set it
	 * in request</li>
	 * <li>4. Return the jsp name on which user needs to be directed</li>
	 * </ul>
	 * 
	 * @param aoRequest RenderRequest Object
	 * @param aoResponse RenderResponse Object
	 * @return JSP page name(proposalDocuments.jsp) to be rendered
	 */
	@SuppressWarnings("unchecked")
	@RenderMapping(params = "render_action=procurementProposalDocumentList")
	protected String getProposalDocumentList(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		String lsProcurementId = null;
		String lsProposalId = null;
		Channel loChannel = new Channel();
		List<ExtendedDocument> loProposalDocsList = null;
		List<ExtendedDocument> loRequiredProposalDocsList = new ArrayList<ExtendedDocument>();
		List<ExtendedDocument> loOptionalProposalDocsList = new ArrayList<ExtendedDocument>();
		Map<String, String> loParamsMap = new HashMap<String, String>();
		HashMap<String, String> loRequiredParamMap = new HashMap<String, String>();
		ProposalDetailsBean loVersionInfoBean = null;
		try
		{
			PortletSession loSession = aoRequest.getPortletSession();
			String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			lsProcurementId = PortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
			lsProposalId = PortalUtil.parseQueryString(aoRequest, HHSConstants.PROPOSAL_ID);
			getPageHeader(aoRequest, lsProcurementId);
			String lsSelectChildScreen = (String) aoRequest.getAttribute(HHSConstants.SELECTED_CHILD_TAB);
			// if the mid level navigation tab name is proposal document
			if (lsSelectChildScreen != null && lsSelectChildScreen.equalsIgnoreCase(HHSConstants.PROPOSAL_DOCUMENTS))
			{
				loParamsMap.put(HHSConstants.PROCUREMENT_ID, lsProcurementId);
				loParamsMap.put(HHSConstants.PROPOSAL_ID, lsProposalId);
				loChannel.setData(HHSConstants.PARAMETERS_MAP, loParamsMap);
				loChannel.setData(HHSConstants.PROPOSAL_ID_KEY, lsProposalId);
				loChannel.setData(HHSConstants.PROCUREMENT_ID, lsProcurementId);
				setRequiredParam(loRequiredParamMap);
				loChannel.setData(HHSConstants.HM_REQIRED_PROPERTY_MAP, loRequiredParamMap);
				loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.GET_PROP_DOC_LIST);
				// Modified as a part of release 3.1.0 for enhancement request
				// 6024
				loVersionInfoBean = (ProposalDetailsBean) loChannel.getData(HHSConstants.VERSION_INFO_BEAN);
				if (null != loVersionInfoBean)
				{
					String loProposalStatus = loVersionInfoBean.getProposalStatusId();
					String loEvalGrpStatus = loVersionInfoBean.getEvaluationGroupStatus();
					String loQuesVersion = loVersionInfoBean.getQuesVersion();
					String loDocVersion = loVersionInfoBean.getDocVersion();
					if (null != loProposalStatus)
					{
						if (loProposalStatus.equals(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_PROPOSAL_SUBMITTED))
								&& (proposalCond2(loEvalGrpStatus))
								&& (proposalCond1(loVersionInfoBean, loQuesVersion, loDocVersion)))
						{
							String lsErrorMsg = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE,
									ApplicationConstants.NOT_LATEST_VERSION);
							aoRequest.setAttribute(ApplicationConstants.ERROR_INFORMATION, lsErrorMsg);
						}
					}
				}
				loProposalDocsList = (List<ExtendedDocument>) loChannel.getData(HHSConstants.FINAL_PROPOSAL_DOC_LIST);
				List<ProposalFilterBean> loCommentList = (List<ProposalFilterBean>) loChannel
						.getData(HHSConstants.PROPOSAL_COMMENT_LIST);
				Map<String, String> loProposalMap = (Map<String, String>) loChannel.getData(HHSConstants.PROPOSAL_MAP);
				aoRequest.setAttribute(HHSConstants.PROPOSAL_TITLE, loProposalMap.get(HHSConstants.PROP_TITLE));
				aoRequest.setAttribute(HHSConstants.PROPOSAL_STATUS, loProposalMap.get(HHSConstants.STATUS_UPPERCASE));
				loChannel.setData(HHSConstants.PROCUREMENT_STATUS,
						((ProcurementInfo) aoRequest.getAttribute(HHSConstants.PROCUREMENT_BEAN)).getStatus());
				loChannel.setData(HHSConstants.PROPOSAL_STATUS, loProposalMap.get(HHSConstants.PROPOSAL_STATUS_ID));
				loChannel.setData(HHSConstants.COMMENT_SIZE, loCommentList.size());
				aoRequest.setAttribute(HHSConstants.STATUS_CHANNEL, loChannel);
				aoRequest.setAttribute(HHSConstants.CHANNEL_ACCESS, loChannel);
				aoRequest.setAttribute(HHSConstants.PROCUREMENT_STATUS,
						((ProcurementInfo) aoRequest.getAttribute(HHSConstants.PROCUREMENT_BEAN)).getStatus());
				if (aoRequest.getParameter(HHSConstants.READ_ONLY_SEC) != null)
				{
					aoRequest.setAttribute(HHSConstants.READ_ONLY_SEC,
							aoRequest.getParameter(HHSConstants.READ_ONLY_SEC));
				}
				if (null != loProposalDocsList)
				{
		            // Start QC9665 R 9.3.2 Citywide cyber assessment program (CCAP) - vulnerabilities -- Users Can Access Other User's Uploaded Documents
					aoRequest.getPortletSession().setAttribute(ApplicationConstants.SESSION_EXTENDED_DOCUMENT_LIST,loProposalDocsList , PortletSession.APPLICATION_SCOPE);
					LOG_OBJECT.Info("save Document List in Session on Application scope");
					//End QC9665 R 9.3.2 Citywide cyber assessment program (CCAP) - vulnerabilities -- Users Can Access Other User's Uploaded Documents

					for (Iterator<ExtendedDocument> loIterator = loProposalDocsList.iterator(); loIterator.hasNext();)
					{
						ExtendedDocument loExtendedDocumentBean = (ExtendedDocument) loIterator.next();
						if (loExtendedDocumentBean.getIsRequiredDoc().equalsIgnoreCase(HHSConstants.ONE))
						{
							loRequiredProposalDocsList.add(loExtendedDocumentBean);
						}
						else
						{
							loOptionalProposalDocsList.add(loExtendedDocumentBean);
						}
					}
				}
				displayErrorMessageForAddendumDocs(aoRequest, loRequiredProposalDocsList,
						String.valueOf(loProposalMap.get(HHSConstants.PROPOSAL_STATUS_ID)));
				// Release 5 User Notification
				String lsPermissionType = (String) aoRequest.getPortletSession().getAttribute(
						HHSConstants.PERMISSION_TYPE, PortletSession.APPLICATION_SCOPE);
				if (lsPermissionType != null
						&& (lsPermissionType.equalsIgnoreCase(ApplicationConstants.ROLE_READ_ONLY) || lsPermissionType
								.equalsIgnoreCase(ApplicationConstants.ROLE_FINANCIAL))
						&& lsUserOrgType.equalsIgnoreCase(HHSConstants.PROVIDER_ORG))
				{
					if (loRequiredProposalDocsList != null)
					{
						for (ExtendedDocument loExtendedDocument : loRequiredProposalDocsList)
						{
							loExtendedDocument.setUserAccess(false);
						}
					}
					if (loOptionalProposalDocsList != null)
					{
						for (ExtendedDocument loExtendedDocument : loOptionalProposalDocsList)
						{
							loExtendedDocument.setUserAccess(false);
						}
					}
				}
				aoRequest.setAttribute(HHSR5Constants.PROC_ROADMAP_READONLY_FLAG,
						getFinancialsReadOnly(aoRequest, lsUserOrgType));
				// Release 5 User Notification
				aoRequest.setAttribute(HHSConstants.REQUIRED_PROPOSAL_DOC_LIST, loRequiredProposalDocsList);
				aoRequest.setAttribute(HHSConstants.OPTIONAL_PROPOSAL_DOC_LIST, loOptionalProposalDocsList);
				aoRequest.setAttribute(HHSConstants.PROPOSAL_ID, lsProposalId);
				aoRequest.setAttribute(HHSConstants.PROPOSAL_STATUS_ID_KEY,
						loProposalMap.get(HHSConstants.PROPOSAL_STATUS_ID));
				// If there is something in the error message attribute in the
				// session
				if (null != loSession
						.getAttribute(ApplicationConstants.ERROR_MESSAGE, PortletSession.APPLICATION_SCOPE))
				{
					aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, (String) loSession.getAttribute(
							ApplicationConstants.ERROR_MESSAGE, PortletSession.APPLICATION_SCOPE));
					aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, (String) loSession.getAttribute(
							ApplicationConstants.ERROR_MESSAGE_TYPE, PortletSession.APPLICATION_SCOPE));
					loSession.removeAttribute(ApplicationConstants.ERROR_MESSAGE, PortletSession.APPLICATION_SCOPE);
					loSession
							.removeAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, PortletSession.APPLICATION_SCOPE);
				}
				// if the success parameter in request is equals to upload
				if (null != PortalUtil.parseQueryString(aoRequest, HHSConstants.SUCCESS)
						&& PortalUtil.parseQueryString(aoRequest, HHSConstants.SUCCESS).equalsIgnoreCase(
								HHSConstants.UPLOAD))
				{
					aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
							ApplicationConstants.ERROR_MESSAGE_PROP_FILE, HHSConstants.FILE_UPLOAD_PASS_MESSAGE));
					aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
							ApplicationConstants.MESSAGE_PASS_TYPE);
				}
			}
		}
		// catch the application exception and set the error message to display
		catch (ApplicationException aoAppEx)
		{
			setGenericErrorMessage(aoRequest);
			LOG_OBJECT.Error("Error occured while fetching list of proposal documents", aoAppEx);
		}
		// catch the exception and set the error message to display in the front
		// end
		catch (Exception aoAppEx)
		{
			setGenericErrorMessage(aoRequest);
			LOG_OBJECT.Error("Error occured while fetching list of proposal documents", aoAppEx);
		}
		return HHSConstants.PROVIDER_PROPOSAL_DOCS;
	}

	/**
	 * This method handles the process of rendering the Release Addendum screen
	 * <ul>
	 * <li>1. Set navigation tab related information using getHeader</li>
	 * <li>2. Retrieve the value of "selectedChildTab" from the request object</li>
	 * <li>3. If the retrieved "selectedChildTab" value is "ReleaseAddendum"
	 * then set error_message and error_message_type in the request object</li>
	 * <li>4. Return "releaseaddendum" screen</li>
	 * </ul>
	 * 
	 * @param aoRequest - RenderRequest object
	 * @param aoResponse - RenderResponse
	 * @return loModelAndView - a ModelAndView object to display the
	 *         releaseAddendum.jsp page
	 */
	@RenderMapping(params = "render_action=renderReleaseAddendum")
	protected ModelAndView renderReleaseAddendum(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		try
		{
			getPageHeader(aoRequest, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			String lsProcurementPlannedStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_PROCUREMENT_PLANNED);
			Channel loChannel = new Channel();
			loChannel.setData(HHSConstants.PROCUREMENT_ID_KEY, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			loChannel.setData(HHSConstants.ORG_TYPE, lsUserOrgType);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_PROC_ADDENDUM_DATA);
			Integer loProcurementAddendumDataCount = (Integer) loChannel.getData(HHSConstants.PROC_ADDENDUM_DATA_COUNT);
			String lsProcurementStatus = (String) loChannel.getData(HHSConstants.PROCUREMENT_STATUS);
			if (loProcurementAddendumDataCount != null && loProcurementAddendumDataCount > HHSConstants.INT_ZERO
					&& !lsProcurementStatus.equals(lsProcurementPlannedStatus))
			{
				aoRequest.setAttribute(HHSConstants.UNPUBLISHED_DATA_MSG_KEY, HHSConstants.UNPUBLISHED_DATA_MSG);
			}
			aoRequest.setAttribute(HHSConstants.CHANNEL_ACCESS, loChannel);
			setExceptionMessageInResponse(aoRequest);
		}
		catch (ApplicationException loExp)
		{
			setGenericErrorMessage(aoRequest);
		}
		catch (Exception loExp)
		{
			setGenericErrorMessage(aoRequest);
			LOG_OBJECT.Error("Error occured while rendering release Addendum", loExp);
		}
		return new ModelAndView(HHSConstants.REL_ADDENDUM, HHSConstants.PUB_PROC, new AuthenticationBean());
	}

	/**
	 * This method handles the action on click of "Release Addendum" button
	 * <ul>
	 * <li>1. Set navigation parameters in render via
	 * setNavigationParamsInRender() method</li>
	 * <li>2. Implement server side validation via validator and fetch the
	 * result in BindingResult object</li>
	 * <li>3. If the fetched result does not contains any errors then create
	 * Channel object and populate it with procurement Id, User Id, HhsAuditBean
	 * and AuthenticationBean</li>
	 * <li>4. Call transaction<b>releaseAddendun</b> to initiate Release
	 * Addendum process</li>
	 * <li>5. Retrieve "lbAuthFlag" and "lbAddendumDocs" from the transaction.</li>
	 * <li>6. If the value of "lbAuthFlag" is false then set Error message in
	 * the render object and redirect to renderReleaseAddendum()</li>
	 * <li>7. Else If the value of "lbAddendumDocs" is false then set Error
	 * message in the render object and redirect to renderReleaseAddendum()</li>
	 * <li>6. Else redirect to "procurement roadmap" screen</li>
	 * </ul>
	 * 
	 * @param aoAuthenticationBean - Authentication bean object
	 * @param aoResult - a BindingResult object
	 * @param aoRequest - an ActionRequest object
	 * @param aoResponse - an ActionResponse object
	 */
	@SuppressWarnings("rawtypes")
	@ActionMapping(params = "submit_action=actionReleaseAddendum")
	public void actionReleaseAddendum(@ModelAttribute("AuthenticationBean") AuthenticationBean aoAuthenticationBean,
			BindingResult aoResult, ActionRequest aoRequest, ActionResponse aoResponse)
	{
		HhsAuditBean loAuditBean = null;
		try
		{
			setNavigationParamsInRender(aoRequest, aoResponse);
			validator.validate(aoAuthenticationBean, aoResult);
			aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID,
					aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			if (!aoResult.hasErrors())
			{
				Map loAuthenticateMap = validateUser(aoAuthenticationBean.getUserName(),
						aoAuthenticationBean.getPassword(), aoRequest);
				Boolean loAuthFlag = (Boolean) loAuthenticateMap.get(HHSConstants.IS_VALID_USER);
				if (!loAuthFlag)
				{
					aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.AUTH_FAIL));

					aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
							ApplicationConstants.MESSAGE_FAIL_TYPE);
					aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
					aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.RENDER_REL_ADDENDUM);
				}
				else
				{
					releaseAddnedumForAuthenticUser(aoRequest, aoResponse, loAuthFlag);
				}
			}
			else
			{
				aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.RENDER_REL_ADDENDUM);
			}
		}
		catch (ApplicationException loExp)
		{
			// aoAuditBean
			Map<String, HhsAuditBean> loParamMap = new HashMap<String, HhsAuditBean>();
			loParamMap.put(HHSConstants.HHS_AUDIT_BEAN, loAuditBean);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap, loExp);
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.RENDER_REL_ADDENDUM);
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error occured while releasing RFP", loExp);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.RENDER_REL_ADDENDUM);
		}
	}

	/**
	 * This method is used to release the addendum if the user authentication
	 * pass
	 * <ul>
	 * <li>It will fetch the server name, port,context path from the property
	 * file</li>
	 * *
	 * <li>Create a local object Notification map</li>
	 * <li>Create the Notification Alert List that is added to the Notification
	 * Map</li>
	 * <li>Creates the request param map</li>
	 * <li>Create NotificationDataBean type object and set the provider list,
	 * agency list, linkMap and agencyLinkMap attributes</li>
	 * <li>Then add the request map, created by, modified by, Entity ID, Entity
	 * Type,NotificationDataBean to the Notification map</li>
	 * <li>Execute transaction <code>releaseAddendum</code></li>
	 * <li>If the boolean returned from the service is false then set error
	 * message in request</li>
	 * </ul>
	 * @param aoRequest action request object
	 * @param aoResponse action response object
	 * @param aoAuthFlag authenticated flag
	 * @return the bean object of type HhsAuditBean
	 * @throws ApplicationException if any exception occurred
	 */
	private HhsAuditBean releaseAddnedumForAuthenticUser(ActionRequest aoRequest, ActionResponse aoResponse,
			Boolean aoAuthFlag) throws ApplicationException
	{
		HhsAuditBean loAuditBean = new HhsAuditBean();
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		try
		{
			String lsServerName = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
					HHSR5Constants.SERVER_NAME_FOR_PROVIDER_BATCH);
			String lsServerPort = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
					HHSR5Constants.SERVER_PORT_FOR_PROVIDER_BATCH);
			String lsContextPath = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
					HHSR5Constants.CONTEXT_PATH_FOR_PROVIDER_BATCH);
			String lsAppProtocol = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
					HHSR5Constants.SERVER_PROTOCOL_FOR_PROVIDER_BATCH);
			Channel loChannel = new Channel();
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
			HashMap<String, Object> loNotificationAgencyMap = new HashMap<String, Object>();
			List<String> loNotificationAlertList = new ArrayList<String>();
			loNotificationAlertList.add(HHSConstants.AL215);
			loNotificationAlertList.add(HHSConstants.NT209);
			loNotificationAlertList.add(HHSConstants.AL214);
			NotificationDataBean loNotificationAL215 = new NotificationDataBean();
			NotificationDataBean loNotificationNT209 = new NotificationDataBean();
			NotificationDataBean loNotificationAL214 = new NotificationDataBean();
			HashMap<String, String> loAgencyLinkMap = new HashMap<String, String>();
			HashMap<String, String> loProviderLinkMap = new HashMap<String, String>();
			StringBuffer lsBfAgencyUrl = new StringBuffer(HHSConstants.INT_CAPACITY_256);
			lsBfAgencyUrl.append(aoRequest.getScheme()).append(HHSConstants.NOTIFICATION_HREF_1)
					.append(aoRequest.getServerName()).append(HHSConstants.COLON).append(aoRequest.getServerPort())
					.append(aoRequest.getContextPath()).append(HHSConstants.RFP_DOCUMENTS_AGENCY_URL)
					.append(aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			loAgencyLinkMap.put(HHSConstants.AGENCY_LINK, lsBfAgencyUrl.toString());
			loNotificationAL214.setAgencyLinkMap(loAgencyLinkMap);
			StringBuffer lsBfProviderUrl = new StringBuffer(HHSConstants.INT_CAPACITY_256);
			lsBfProviderUrl.append(lsAppProtocol).append(HHSConstants.NOTIFICATION_HREF_1).append(lsServerName)
					.append(HHSConstants.COLON).append(lsServerPort).append(HHSConstants.FORWARD_SLASH)
					.append(lsContextPath).append(HHSConstants.RFP_DOCUMENTS_PROVIDER_URL)
					.append(aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			loProviderLinkMap.put(HHSConstants.PROVIDER_LINK, lsBfProviderUrl.toString());
			loNotificationAL215.setLinkMap(loProviderLinkMap);
			loNotificationNT209.setLinkMap(loProviderLinkMap);
			loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
			loNotificationMap.put(ApplicationConstants.ENTITY_ID, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			loNotificationMap.put(ApplicationConstants.ENTITY_TYPE, HHSConstants.PROCUREMENT);
			loNotificationMap.put(HHSConstants.CREATED_BY_USER_ID, lsUserId);
			loNotificationMap.put(HHSConstants.MODIFIED_BY, lsUserId);
			loNotificationMap.put(HHSConstants.AL215, loNotificationAL215);
			loNotificationMap.put(HHSConstants.NT209, loNotificationNT209);
			loNotificationMap.put(HHSConstants.AL214, loNotificationAL214);
			loNotificationMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
			loAuditBean.setAuditTableIdentifier(HHSConstants.ACCELERATOR_AUDIT);
			loAuditBean.setEntityType(HHSConstants.PROCUREMENT);
			loAuditBean.setData(HHSConstants.NEW_ADD_RELEASED);
			loAuditBean.setEntityId(aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			loAuditBean.setEventName(HHSConstants.RELEASE);
			loAuditBean.setUserId(lsUserId);
			loAuditBean.setEventType(HHSConstants.ADD_RELEASE);
			loChannel.setData(HHSConstants.LO_HM_NOTIFY_PARAM, loNotificationMap);
			loChannel.setData(HHSConstants.AGENCY_NOTIFY_PARAM, loNotificationAgencyMap);
			loChannel.setData(HHSConstants.PROCUREMENT_ID, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			loChannel.setData(HHSConstants.USER_ID, lsUserId);
			loChannel.setData(HHSConstants.AUDIT_BEAN, loAuditBean);
			loChannel.setData(HHSConstants.LB_AUTH_FLAG, aoAuthFlag);
			// Start R5 : set EntityId and EntityName for AutoSave
			CommonUtil.setChannelForAutoSaveData(loChannel, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID),
					HHSR5Constants.PROCUREMENT);
			// End R5 : set EntityId and EntityName for AutoSave
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.REL_ADDENDUM);
			// Modified as a part of release 3.1.0 for enhancement request 6024
			Boolean loEvalProgress = (Boolean) loChannel.getData(HHSConstants.LB_EVAL_PROGRESS);
			if (loEvalProgress)
			{
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.RESTRICT_RELEASE_ADDENDUM));
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE);
				aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.RENDER_REL_ADDENDUM);
			}
			else
			{
				Boolean loAddendumDocs = (Boolean) loChannel.getData(HHSConstants.LB_ADD_DOC);
				if (!loAddendumDocs)
				{
					aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.ADDENDUM_DOC_FAIL));
					aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
							ApplicationConstants.MESSAGE_FAIL_TYPE);
					aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
					aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.RENDER_REL_ADDENDUM);
				}
				else
				{
					aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.SUCCESS_ADDENDUM));
					aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
							ApplicationConstants.MESSAGE_PASS_TYPE);
					aoResponse.setRenderParameter(HHSConstants.RESET_SESSION_PROC, HHSConstants.TRUE);
				}
			}
		}
		catch (Exception aoExp)
		{
			ApplicationException loAppEx = new ApplicationException("Error Occured while releasing addendum", aoExp);
			LOG_OBJECT.Error("Error Occured while releasing addendum", loAppEx);
			throw loAppEx;
		}
		return loAuditBean;
	}

	/**
	 * The method will be called by handleRenderRequestInternal() when selected
	 * child tab from navigation is Proposal Configuration
	 * 
	 * <ul>
	 * <li>Get the procurement Id from request</li>
	 * <li>Get the document type list by calling method getDoctypesFromXML()
	 * from class RFPReleaseDocsUtil</li>
	 * <li>Call Transaction layer with id fetchProposalConfigurationDetails to
	 * get proposal custom list and Proposal document type list</li>
	 * <li>Split Required and Optional Document Type List</li>
	 * <li>Set these values in request object</li>
	 * <li>Populate the above data in jsp. If we got above list as null ,jsp
	 * will be loaded with blank field</li>
	 * </ul>
	 * 
	 * @param aoRequest RenderRequest
	 * @param aoResponse RenderResponse
	 * @return ModelAndView to display the proposalconfiguration.jsp page
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private ModelAndView renderProposalConfiguration(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		String lsProcurementId = PortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
		ProposalDetailsBean loProposalDetailsBean = new ProposalDetailsBean();
		HashMap loHmReqExceProp = new HashMap();
		try
		{
			Integer loProcurementPlannedStatus = Integer.valueOf(PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_PLANNED));
			org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
					ApplicationConstants.FILENET_EXTENDED_DOC_TYPE);
			ProcurementInfo loProcurementBean = (ProcurementInfo) aoRequest.getAttribute(HHSConstants.PROCUREMENT_BEAN);
			String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			loHmReqExceProp.put(HHSConstants.LS_USER_ORG_TYPE, lsUserOrgType);
			String lsIsReadOnly = null;
			if (null != lsUserOrgType && lsUserOrgType.equalsIgnoreCase(ApplicationConstants.AGENCY_ORG))
			{
				lsIsReadOnly = HHSConstants.TRUE;
			}
			Channel loChannel = new Channel();
			List<String> loDocumentTypeList = RFPReleaseDocsUtil.getDoctypesFromXML(loXMLDoc,
					ApplicationConstants.PROVIDER_ORG);
			loChannel.setData(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			loChannel.setData(HHSConstants.PROCUREMENT_STATUS, String.valueOf(loProcurementBean.getStatus()));
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_PROPOSAL_CONFIG_DETAILS);
			Integer loProcurementAddendumDataCount = (Integer) loChannel.getData(HHSConstants.PROC_ADDENDUM_DATA_COUNT);
			if (loProcurementAddendumDataCount != null && loProcurementAddendumDataCount > HHSConstants.INT_ZERO
					&& !loProcurementBean.getStatus().equals(loProcurementPlannedStatus))
			{
				aoRequest.setAttribute(HHSConstants.UNPUBLISHED_DATA_MSG_KEY, HHSConstants.UNPUBLISHED_DATA_MSG);
			}
			List<ProposalQuestionAnswerBean> loCustomQuestionList = (List<ProposalQuestionAnswerBean>) loChannel
					.getData(HHSConstants.PROPOSAL_CUSTOM_QUE_LIST);
			loProposalDetailsBean.setQuestionAnswerBeanList(loCustomQuestionList);
			loHmReqExceProp.put(HHSConstants.LO_CUSTOM_QUE_LIST, loCustomQuestionList);
			List<ExtendedDocument> loProposalDocTypeList = (List<ExtendedDocument>) loChannel
					.getData(HHSConstants.PROPOSAL_DOC_TYPE_LIST);
			List<ExtendedDocument> loRequiredProposalDocTypeList = new ArrayList<ExtendedDocument>();
			List<ExtendedDocument> loOptionalProposalDocTypeList = new ArrayList<ExtendedDocument>();
			if (null != loProposalDocTypeList && CollectionUtils.isNotEmpty(loProposalDocTypeList))
			{
				for (ExtendedDocument loExtendedDocument : loProposalDocTypeList)
				{
					if (null != loExtendedDocument.getRequiredFlag()
							&& loExtendedDocument.getRequiredFlag().equalsIgnoreCase(HHSConstants.ONE))
					{
						loRequiredProposalDocTypeList.add(loExtendedDocument);
					}
					else
					{
						loOptionalProposalDocTypeList.add(loExtendedDocument);
					}
				}
			}
			// Modified as a part of release 3.1.0 for enhancement request 6024
			Integer loPublishedReleasedCount = (Integer) loChannel.getData(HHSConstants.PUBLISHED_RELEASED_COUNT);
			if (null != loPublishedReleasedCount && loPublishedReleasedCount == 0)
			{
				lsIsReadOnly = HHSConstants.TRUE;
			}
			loChannel.setData(HHSConstants.ORGTYPE, lsUserOrgType);
			loChannel.setData(HHSConstants.PROCUREMENT_STATUS, loProcurementBean.getStatus());
			Boolean loSaveButtonStatus = Boolean.valueOf((String) Rule.evaluateRule(
					HHSConstants.PROPOSAL_CONFIG_SAVE_RULE, loChannel));
			loProposalDetailsBean.setRequiredDocumentList(loRequiredProposalDocTypeList);
			loProposalDetailsBean.setOptionalDocumentList(loOptionalProposalDocTypeList);
			loProposalDetailsBean.setProcurementStatus(loProcurementBean.getStatus().toString());
			aoRequest.setAttribute(HHSConstants.DOC_TYPE_LIST, loDocumentTypeList);
			aoRequest.setAttribute(HHSConstants.SAVE_BUTTON_STATUS, loSaveButtonStatus);
			aoRequest.setAttribute(HHSConstants.IS_READ_ONLY, lsIsReadOnly);
			aoRequest.setAttribute(HHSConstants.STATUS, loProcurementBean.getStatus());
			setExceptionMessageInResponse(aoRequest);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handled over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error occured while configuring proposal", loExp);
			setGenericErrorMessage(aoRequest);
		}
		// catching Exception thrown by transaction layer
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error occured while configuring proposal", loExp);
			setGenericErrorMessage(aoRequest);
		}
		return new ModelAndView(HHSConstants.PROPOSAL_CONFIG, HHSConstants.PROPOSAL_DETAILS_BEAN_UPPERCASE,
				loProposalDetailsBean);
	}

	/**
	 * The method will save Proposal Configuration details. This will be
	 * Responsible for functionality of Save Button from Proposal Configuration
	 * page
	 * <ul>
	 * <li>Set navigation parameters in render by calling method
	 * setNavigationParamsInRender()</li>
	 * <li>Set procurement Id, Modified By and Created By in model attribute</li>
	 * <li>Call Transaction with Id saveProposalConfigurationDetails to save
	 * proposal custom questions and proposal document types</li>
	 * <li>If action is triggered by Save button, render to same page with
	 * updated values</li>
	 * </ul>
	 * 
	 * @param aoProposalDetailsBean ProposalDetailsBean
	 * @param aoResult binding result object indicating errors
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse object
	 * @param aoModel model map object
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	@ActionMapping(params = "submit_action=saveProposalConfiguration")
	public void saveProposalConfiguration(
			@ModelAttribute("ProposalDetailsBean") ProposalDetailsBean aoProposalDetailsBean, BindingResult aoResult,
			ActionRequest aoRequest, ActionResponse aoResponse, ModelMap aoModel)
	{
		HashMap loHmReqExceProp = new HashMap();
		try
		{
			proposalConfigurationValidator.validate(aoProposalDetailsBean, aoResult);
			setNavigationParamsInRender(aoRequest, aoResponse);
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			String lsProcurementId = aoRequest.getParameter(HHSConstants.PROCUREMENT_ID);
			loHmReqExceProp.put(HHSConstants.LS_PRO_ID, lsProcurementId);
			loHmReqExceProp.put(HHSConstants.LS_USER_ID, lsUserId);

			if (!aoResult.hasErrors())
			{
				aoProposalDetailsBean.setProcurementId(lsProcurementId);
				aoProposalDetailsBean.setModifiedBy(lsUserId);
				aoProposalDetailsBean.setCreatedBy(lsUserId);
				Channel loChannel = new Channel();
				loChannel.setData(HHSConstants.PROPOSAL_DETAILS_BEAN, aoProposalDetailsBean);
				Map<String, String> loModifiedMap = new HashMap<String, String>();
				loModifiedMap.put(HHSConstants.MOD_BY_USER_ID, lsUserId);
				loModifiedMap.put(HHSConstants.PROCUREMENT_ID, lsProcurementId);
				loChannel.setData(HHSConstants.LAST_MOD_HASHMAP, loModifiedMap);
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.SAVE_PROPOSAL_CONGFIG_DETAILS);
				aoModel.remove(HHSConstants.PROPOSAL_DETAILS_BEAN_UPPERCASE);
			}
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handled over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{

			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loHmReqExceProp, loExp);
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error occured while processing procurements", loExp);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
		}
	}

	/**
	 * This method will redirect user to the back screen while uploading a
	 * document
	 * <ul>
	 * <li>Get the temporary file path from the request</li>
	 * <li>Delete the temporary file from the path mentioned</li>
	 * <li>Then set the parameters in the response object</li>
	 * <li>Redirect user to the back page</li>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * Changes done for Enhancement #6429 for Release 3.4.0
	 * 
	 * @param aoRequest Action Request
	 * @param aoResponse Action Response
	 */
	@Override
	@ActionMapping(params = "submit_action=goBackAction")
	public void goBackAction(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		String lsProcurementId = null;
		String lsUploadingDocumentType = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.UPLOAD_DOC_TYPE);
		try
		{
			File loFilePath = new File(aoRequest.getParameter(ApplicationConstants.FILE_PATH));
			deleteTempFile(loFilePath);
			// Start || Changes done for Enhancement #6429 for Release 3.4.0
			Document loDocument = null;
			if (null != lsUploadingDocumentType
					&& lsUploadingDocumentType.equalsIgnoreCase(HHSConstants.STRING_AWARD_DOC))
			{
				loDocument = (Document) ApplicationSession.getAttribute(aoRequest, true,
						ApplicationConstants.SESSION_DOCUMENT_OBJ);
			}
			else
			{
				loDocument = new Document();
			}
			// End || Changes done for Enhancement #6429 for Release 3.4.0
			String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			String lsDocCategory = HHSPortalUtil.parseQueryString(aoRequest, ApplicationConstants.DOCS_CATEGORY);
			String lsDocType = HHSPortalUtil.parseQueryString(aoRequest, ApplicationConstants.DOCS_TYPE);
			loDocument.setDocCategory(lsDocCategory);
			loDocument.setDocType(lsDocType);
			RFPReleaseDocsUtil.setRFPDocCategorynDocType(loDocument, lsDocCategory, lsUserOrgType, null);
			if (null != PortalUtil.parseQueryString(aoRequest, HHSConstants.TOP_LEVEL_FROM_REQ))
			{
				aoResponse.setRenderParameter(HHSConstants.TOP_LEVEL_FROM_REQ,
						PortalUtil.parseQueryString(aoRequest, HHSConstants.TOP_LEVEL_FROM_REQ));
			}
			if (null != PortalUtil.parseQueryString(aoRequest, HHSConstants.MID_LEVEL_FROM_REQ))
			{
				aoResponse.setRenderParameter(HHSConstants.MID_LEVEL_FROM_REQ,
						PortalUtil.parseQueryString(aoRequest, HHSConstants.MID_LEVEL_FROM_REQ));
			}
			ApplicationSession.setAttribute(loDocument, aoRequest, ApplicationConstants.SESSION_DOCUMENT_OBJ);
			lsProcurementId = (null != HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.HIDDEN_HDNCONTRACTID)) ? HHSPortalUtil
					.parseQueryString(aoRequest, HHSConstants.HIDDEN_HDNCONTRACTID) : HHSPortalUtil.parseQueryString(
					aoRequest, HHSConstants.HIDDEN_HDNCONTRACTID);
			//Fix for defect 8325		
			if (null == lsProcurementId || lsProcurementId.isEmpty())
			{
			lsProcurementId= aoRequest.getParameter(HHSConstants.PROCUREMENT_ID);
			}
			//Fix for defect 8325 end
			aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			if (null != HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROPOSAL_ID))
			{
				aoResponse.setRenderParameter(HHSConstants.PROPOSAL_ID,
						HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROPOSAL_ID));
			}
			// ORG_ID,STAFF_ID,USER are passed further so as to read the BAFO
			// Document details
			// from DOCTYPE.xml as the login user is agency user
			if (null != HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.ORGA_ID))
			{
				aoResponse.setRenderParameter(HHSConstants.ORGA_ID,
						HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.ORGA_ID));
			}
			if (null != HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.STAFF_ID))
			{
				aoResponse.setRenderParameter(HHSConstants.STAFF_ID,
						HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.STAFF_ID));
			}
			if (null != HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.USER))
			{
				aoResponse.setRenderParameter(HHSConstants.USER,
						HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.USER));
			}
			if (null != HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.EVALUATION_POOL_MAPPING_ID))
			{
				aoResponse.setRenderParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID,
						HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.EVALUATION_POOL_MAPPING_ID));
			}
			if (null != HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.AWARD_ID))
			{
				aoResponse.setRenderParameter(HHSConstants.AWARD_ID,
						HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.AWARD_ID));
			}
			// Start || Changes done for Enhancement #6429 for Release 3.4.0
			if (null != HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.IS_FINANCIAL))
			{
				aoResponse.setRenderParameter(HHSConstants.IS_FINANCIAL,
						HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.IS_FINANCIAL));
			}
			if (null != HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.HIDDEN_HDNCONTRACTID))
			{
				aoResponse.setRenderParameter(HHSConstants.HIDDEN_HDNCONTRACTID,
						HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.HIDDEN_HDNCONTRACTID));
			}
			if (null != HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.AS_PROC_STATUS))
			{
				aoResponse.setRenderParameter(HHSConstants.AS_PROC_STATUS,
						HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.AS_PROC_STATUS));
			}
			if (null != HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.UPLOAD_DOC_TYPE))
			{
				aoResponse.setRenderParameter(HHSConstants.UPLOAD_DOC_TYPE,
						HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.UPLOAD_DOC_TYPE));
			}
			// End || Changes done for Enhancement #6429 for Release 3.4.0
			if (null != HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.HIDDEN_DOC_REF_SEQ_NO))
			{
				aoResponse.setRenderParameter(HHSConstants.HIDDEN_DOC_REF_SEQ_NO,
						HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.HIDDEN_DOC_REF_SEQ_NO));
			}
			if (null != PortalUtil.parseQueryString(aoRequest, HHSConstants.UPLOAD_PROCESS))
			{
				aoResponse.setRenderParameter(HHSConstants.UPLOAD_PROCESS,
						PortalUtil.parseQueryString(aoRequest, HHSConstants.UPLOAD_PROCESS));
			}

			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.UPLOAD_DOC);

		}
		// catch the application exception and set the error message to display
		// in the front end
		catch (ApplicationException aoAppEx)
		{
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
		// catch the exception and set the error message to display in the front
		// end
		catch (Exception aoEXP)
		{
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
	}

	/**
	 * This method will be executed after the document has been uploaded
	 * successfully or the upload document process gives any exception
	 * 
	 * @param aoFilePath temporary file object
	 */
	private void deleteTempFile(File aoFilePath)
	{
		if (null != aoFilePath)
		{
			aoFilePath.delete();
		}
	}

	/**
	 * This method will be executed when user click on cancel button while
	 * uploading a document
	 * <ul>
	 * <li>Delete the temporary file created from the context path</li>
	 * <li>Redirect user to the rfp document screen</li>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * @param aoActionRequestForCancel ActionRequest Object
	 * @param aoResponseForCancel ActionResponse Object
	 */
	@ActionMapping(params = "submit_action=cancelUploadDocument")
	public void cancelUploadDocumentAction(ActionRequest aoActionRequestForCancel, ActionResponse aoResponseForCancel)
	{
		try
		{
			File loFilePath = new File(aoActionRequestForCancel.getParameter(ApplicationConstants.FILE_PATH));
			deleteTempFile(loFilePath);
			setNavigationParamsInRender(aoActionRequestForCancel, aoResponseForCancel);
			aoResponseForCancel.setRenderParameter(HHSConstants.PROCUREMENT_ID,
					HHSPortalUtil.parseQueryString(aoActionRequestForCancel, HHSConstants.PROCUREMENT_ID));
			if (null != HHSPortalUtil.parseQueryString(aoActionRequestForCancel, HHSConstants.PROPOSAL_ID))
			{
				aoResponseForCancel.setRenderParameter(HHSConstants.PROPOSAL_ID,
						HHSPortalUtil.parseQueryString(aoActionRequestForCancel, HHSConstants.PROPOSAL_ID));
			}
			if (null != HHSPortalUtil.parseQueryString(aoActionRequestForCancel,
					HHSConstants.EVALUATION_POOL_MAPPING_ID))
			{
				aoResponseForCancel.setRenderParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID, HHSPortalUtil
						.parseQueryString(aoActionRequestForCancel, HHSConstants.EVALUATION_POOL_MAPPING_ID));
			}
			if (null != HHSPortalUtil.parseQueryString(aoActionRequestForCancel, HHSConstants.AWARD_ID))
			{
				aoResponseForCancel.setRenderParameter(HHSConstants.AWARD_ID,
						HHSPortalUtil.parseQueryString(aoActionRequestForCancel, HHSConstants.AWARD_ID));
			}
			aoResponseForCancel.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
			if (HHSConstants.PROPOSAL_DOCUMENTS.equalsIgnoreCase(PortalUtil.parseQueryString(aoActionRequestForCancel,
					HHSConstants.MID_LEVEL_FROM_REQ)))
			{
				aoResponseForCancel.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.PROC_PROPOSAL_DOC_LIST);
			}
			else if (HHSConstants.RFP_DOC.equalsIgnoreCase(PortalUtil.parseQueryString(aoActionRequestForCancel,
					HHSConstants.MID_LEVEL_FROM_REQ)))
			{
				aoResponseForCancel.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.DISP_RFF_DOC_LIST);
			}
			else if (HHSConstants.SELECTION_DETAILS.equalsIgnoreCase(PortalUtil.parseQueryString(
					aoActionRequestForCancel, HHSConstants.TOP_LEVEL_FROM_REQ)))
			{
				aoResponseForCancel.setRenderParameter(HHSConstants.ACTION, HHSConstants.SEL_DETAIL);
				aoResponseForCancel.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.VIEW_SEL_DETAILS);
			}
		}
		// catch the exception and set the error message to display in the front
		// end
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("IOException during file upload", aoExp);
			aoResponseForCancel.setRenderParameter(ApplicationConstants.RENDER_ACTION, ApplicationConstants.ERROR);
			setExceptionMessageFromAction(aoResponseForCancel, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
	}

	/**
	 * This method will be executed when user click on cancel button while
	 * uploading a document from first tab
	 * <ul>
	 * <li>Grt the screen from where user was trying to upload the document</li>
	 * <li>Redirect user the screen he was uploading from</li>
	 * <li>Fetch the latest documents list and display to user</li>
	 * </ul>
	 * 
	 * @param aoActionRequestForCancelStep1 ActionRequest Object
	 * @param aoActionResponseForCancelStep1 ActionResponse Object
	 * 
	 */
	@ActionMapping(params = "submit_action=cancelUploadActionStep1")
	public void cancelUploadDocumentFropStep1Action(ActionRequest aoActionRequestForCancelStep1,
			ActionResponse aoActionResponseForCancelStep1)
	{
		try
		{
			setNavigationParamsInRender(aoActionRequestForCancelStep1, aoActionResponseForCancelStep1);
			aoActionResponseForCancelStep1.setRenderParameter(HHSConstants.PROCUREMENT_ID,
					HHSPortalUtil.parseQueryString(aoActionRequestForCancelStep1, HHSConstants.PROCUREMENT_ID));
			if (null != HHSPortalUtil.parseQueryString(aoActionRequestForCancelStep1, HHSConstants.PROPOSAL_ID))
			{
				aoActionResponseForCancelStep1.setRenderParameter(HHSConstants.PROPOSAL_ID,
						HHSPortalUtil.parseQueryString(aoActionRequestForCancelStep1, HHSConstants.PROPOSAL_ID));
			}
			aoActionResponseForCancelStep1.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
			if (HHSConstants.PROPOSAL_DOCUMENTS.equalsIgnoreCase(PortalUtil.parseQueryString(
					aoActionRequestForCancelStep1, HHSConstants.MID_LEVEL_FROM_REQ)))
			{
				aoActionResponseForCancelStep1.setRenderParameter(HHSConstants.RENDER_ACTION,
						HHSConstants.PROC_PROPOSAL_DOC_LIST);
			}
			else if (HHSConstants.RFP_DOC.equalsIgnoreCase(PortalUtil.parseQueryString(aoActionRequestForCancelStep1,
					HHSConstants.MID_LEVEL_FROM_REQ)))
			{
				aoActionResponseForCancelStep1.setRenderParameter(HHSConstants.RENDER_ACTION,
						HHSConstants.DISP_RFF_DOC_LIST);
			}
			else if (HHSConstants.SELECTION_DETAILS.equalsIgnoreCase(PortalUtil.parseQueryString(
					aoActionRequestForCancelStep1, HHSConstants.TOP_LEVEL_FROM_REQ)))
			{
				aoActionResponseForCancelStep1.setRenderParameter(HHSConstants.ACTION, HHSConstants.SEL_DETAIL);
				aoActionResponseForCancelStep1.setRenderParameter(HHSConstants.RENDER_ACTION,
						HHSConstants.VIEW_SEL_DETAILS);
			}
		}
		// catch the exception and set the error message to display in the front
		// end
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("IOException during file upload", aoExp);
			aoActionResponseForCancelStep1.setRenderParameter(ApplicationConstants.RENDER_ACTION,
					ApplicationConstants.ERROR);
			setExceptionMessageFromAction(aoActionResponseForCancelStep1, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
	}

	/**
	 * This method is used to Launch the Procurement Certification of Funds task
	 * from the S 204 Financials screen by the Agency (<b>Finance & CFO users
	 * only</b>).
	 * 
	 * <ul>
	 * <li>After the Workflow is launched the PCOF status is changed for the
	 * Procurement Financials table in the database and then the base page is
	 * refreshed by calling the default renderer of this class.</li>
	 * <li>First it will fetch the procurement id and agency id from the request
	 * object</li>
	 * <li>Then it will set the paramaters for the channel object</li>
	 * <li>Then it will execute the transaction <b>SubmitFinancialsWF</b></li>
	 * <li>Then it will set the attributes of the CBGridBean</li>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * @param aoRequest action request object
	 * @param aoResponse action response object
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	@ActionMapping(params = "submit_action=launchWFproc")
	protected void launchFinancialWF(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		HashMap loHmRequiredProps = new HashMap();
		String lsProcID = aoRequest.getParameter(HHSConstants.PROCID);
		String lsAgencyId = aoRequest.getParameter(HHSConstants.AGENCYID);
		Channel loChannel = new Channel();
		String lsStatusId;
		CBGridBean loCbGridBean = new CBGridBean();
		PortletSession loSession = aoRequest.getPortletSession();
		String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
		loHmRequiredProps.put(HHSConstants.PROCUREMENT_ID, lsProcID);
		loHmRequiredProps.put(HHSConstants.SUBMITTED_BY, lsUserId);
		loHmRequiredProps.put(HHSConstants.WORKFLOW_NAME, HHSConstants.WF_PROCUREMENT_CERTIFICATION_FUND);
		loChannel.setData(HHSConstants.AO_HMWF_REQUIRED_PROPS, loHmRequiredProps);
		loChannel.setData(HHSConstants.LB_AUTH_STATUS_FLAG, true);
		loChannel.setData(HHSConstants.PROCUREMENT_ID, lsProcID);

		// Set userId and Procurement Id to update the PCOF status
		loCbGridBean.setProcurementID(lsProcID);
		loCbGridBean.setModifyByAgency(lsUserId);
		loCbGridBean.setAgencyId(lsAgencyId);
		loChannel.setData(HHSConstants.CB_GRID_BEAN_OBJ, loCbGridBean);

		try
		{
			lsStatusId = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_PCOF_IN_REVIEW);
			loCbGridBean.setStatusId(lsStatusId);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.SUBMIT_FINANCIALS_WF);
			String lsSuccessMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
					HHSConstants.PROC_SUBMIT_SUCCESS);
			ApplicationSession.setAttribute(lsSuccessMsg, aoRequest, HHSConstants.SUCCESS_MESSAGE);
		}
		catch (ApplicationException aoAppExe)
		{
			String lsLevelErrorMessage = null;
			ApplicationException loAppEx = (ApplicationException) aoAppExe.getRootCause();
			if (null != loAppEx)
			{
				lsLevelErrorMessage = (String) loAppEx.getContextData().get(HHSConstants.LEVEL_ERROR_MESSAGE);
			}
			if (null == lsLevelErrorMessage)
			{
				lsLevelErrorMessage = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			}
			ApplicationSession.setAttribute(lsLevelErrorMessage, aoRequest, HHSConstants.ERROR_MESSAGE);
			LOG_OBJECT.Error("ApplicationException occured in launchFinancialWF", aoAppExe);
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error occured while launching financial workflow", aoExp);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
		aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, lsProcID);
		aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
		setNavigationParamsInRender(aoRequest, aoResponse);
	}

	/**
	 * Below method will set the required parameter into the map
	 * <ul>
	 * <li>Get the parameter map from argument and set required properties with
	 * empty string into it</li>
	 * </ul>
	 * @param aoParamMap parameter map
	 */
	private void setRequiredParam(HashMap<String, String> aoParamMap)
	{
		aoParamMap.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, ApplicationConstants.EMPTY_STRING);
		aoParamMap.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY_ID, ApplicationConstants.EMPTY_STRING);
		aoParamMap.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY, ApplicationConstants.EMPTY_STRING);
		aoParamMap.put(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY, ApplicationConstants.EMPTY_STRING);
		aoParamMap.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY_ID, ApplicationConstants.EMPTY_STRING);
		aoParamMap.put(P8Constants.PROPERTY_CE_LAST_MODIFIED_DATE, ApplicationConstants.EMPTY_STRING);
	}

	/**
	 * This method is used to get read only version of information from the S204
	 * - Financials screen and the S389 – Procurement Certification of Funds
	 * Task. The user can navigate to the document by clicking on the S204.05
	 * ‘View CoF’ button in the S204 – Financials screen. This is for S410
	 * Procurement certification of funds doc.
	 * <ul>
	 * <li>Transaction used - <code>procurementCOFDetailsFetch<code></li>
	 * </ul>
	 * 
	 * @param aoRequest - ActionRequest
	 * @param aoResponse - ActionResponse
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	@ActionMapping(params = "submit_action=showProcCOF")
	protected void showProcurementCOF(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		String lsProcurementId = aoRequest.getParameter(HHSConstants.PROC_ID_LOWERCASE);
		PortletSession loSession = aoRequest.getPortletSession();
		String lsAgencyId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
				PortletSession.APPLICATION_SCOPE);
		String lsContractStartDt = aoRequest.getParameter(HHSConstants.CONTRACT_START_DATE_UPPERCASE);
		String lsContractEndDT = aoRequest.getParameter(HHSConstants.CONTRACT_END_DATE_UPPERCASE);

		List loFundingBeanList = null;
		List loHeaderList = null;
		List loAccountsAllocationBeanList = null;

		ProcurementCOF loPprocureCof = null;
		CBGridBean loCBGridBean = new CBGridBean();
		try
		{
			setContractDatesInSession(aoRequest.getPortletSession(), lsContractStartDt, lsContractEndDT);
			loHeaderList = setCOFAccountHeaderDataInSession(aoRequest);
			Map<String, Object> loFiscalYrMap = getContractFiscalYears(aoRequest);
			int liFiscalStartYr = (Integer) loFiscalYrMap.get(HHSConstants.LI_START_YEAR);
			loCBGridBean.setProcurementID(lsProcurementId);
			loCBGridBean.setAgencyId(lsAgencyId);
			loCBGridBean.setFiscalYearID(String.valueOf(liFiscalStartYr));
			loCBGridBean.setCoaDocType(true);
			loCBGridBean.setIsProcCerTaskScreen(false);
			Channel loChannelObj = new Channel();
			loChannelObj.setData(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
			loChannelObj.setData(HHSConstants.CB_GRID_BEAN_OBJ, loCBGridBean);
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.PROC_COF_DETAILS_FETCH);
			loPprocureCof = (ProcurementCOF) loChannelObj.getData(HHSConstants.AO_RET_COF_DETAILS);
			loAccountsAllocationBeanList = (List) loChannelObj.getData(HHSConstants.AO_RET_COA_LIST);
			loFundingBeanList = (List) loChannelObj.getData(HHSConstants.AO_RETURNED_GRID_LIST);
			ApplicationSession.setAttribute(loHeaderList, aoRequest, HHSConstants.CONTRACT_COF_COA_HEADER);
			ApplicationSession.setAttribute(loAccountsAllocationBeanList, aoRequest,
					HHSConstants.PROCUREMENT_COF_COA_DETAILS);
			ApplicationSession.setAttribute(loFundingBeanList, aoRequest, HHSConstants.PROCUREMENT_COF_FUNDING_DETAILS);
			ApplicationSession.setAttribute(loPprocureCof, aoRequest, HHSConstants.PROC_COF);
		}
		catch (ApplicationException aoAppExp)
		{
			Map<String, CBGridBean> loParamMap = new HashMap<String, CBGridBean>();
			loParamMap.put(HHSConstants.LO_CB_GRID_BEAN, loCBGridBean);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap, aoAppExp);

		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error occured while processing payments", aoExp);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);

		}
		aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
		aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.RENDER_PROC_COF);
	}

	/**
	 * This method returns ModelAndView with jsp file procurementCertFundsDoc
	 * for Procurement Certification of Funds Doc.
	 * @param aoRequest - ActionRequest
	 * @param aoResponse - ActionResponse
	 * @return loModelAndView - ModelAndView object to display
	 *         procurementCertFundsDoc.jsp page
	 */
	@SuppressWarnings(
	{ "rawtypes" })
	@RenderMapping(params = "render_action=renderProcCOF")
	protected ModelAndView renderProcCOF(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		ModelAndView loModelAndView = null;
		List loHeaderList = (List) ApplicationSession.getAttribute(aoRequest, HHSConstants.CONTRACT_COF_COA_HEADER);
		List loDetailsList = (List) ApplicationSession
				.getAttribute(aoRequest, HHSConstants.PROCUREMENT_COF_COA_DETAILS);
		List loFundingList = (List) ApplicationSession.getAttribute(aoRequest,
				HHSConstants.PROCUREMENT_COF_FUNDING_DETAILS);
		ProcurementCOF loPprocureCof = (ProcurementCOF) ApplicationSession.getAttribute(aoRequest,
				HHSConstants.PROC_COF);
		aoRequest.setAttribute(HHSConstants.DETAIL_LIST, loDetailsList);
		aoRequest.setAttribute(HHSConstants.HEADER_LIST, loHeaderList);
		aoRequest.setAttribute(HHSConstants.FUNDING_LIST, loFundingList);
		aoRequest.setAttribute(HHSConstants.PROC_COF, loPprocureCof);
		loModelAndView = new ModelAndView(HHSConstants.PROC_CERT_FUNDS_DOC);
		return loModelAndView;
	}

	/**
	 * This method is used to display Error Messages for Addendum Docs
	 * <ul>
	 * <li>Set the warning message to the user if there is any addendum released
	 * after proposal submission.</li>
	 * </ul>
	 * 
	 * @param aoRequest renser request object
	 * @param aoRequiredDocumentList document bean list
	 * @param asProposalStatus proposal status
	 * @throws ApplicationException if any exception occurred
	 */
	private void displayErrorMessageForAddendumDocs(RenderRequest aoRequest,
			List<ExtendedDocument> aoRequiredDocumentList, String asProposalStatus) throws ApplicationException
	{
		String lsProposalSubmitStatus = null;
		String lsDocumentNotStarted = null;
		String lsAddendumProposalDocMessage = null;
		try
		{
			lsProposalSubmitStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_PROPOSAL_SUBMITTED);
			lsDocumentNotStarted = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_DOCUMENT_NOT_STARTED);
			lsAddendumProposalDocMessage = PropertyLoader.getProperty(ApplicationConstants.ERROR_MESSAGE_PROP_FILE,
					HHSConstants.PROPOSAL_DOC_ADDENDUM_MESSAGE);
			if (null != aoRequiredDocumentList && !aoRequiredDocumentList.isEmpty())
			{
				for (Iterator<ExtendedDocument> loDocItr = aoRequiredDocumentList.iterator(); loDocItr.hasNext();)
				{
					ExtendedDocument loExtendedDocObj = (ExtendedDocument) loDocItr.next();
					if (lsProposalSubmitStatus.equalsIgnoreCase(asProposalStatus)
							&& loExtendedDocObj.getStatusId().equalsIgnoreCase(lsDocumentNotStarted))
					{
						aoRequest.setAttribute(HHSConstants.SHOW_ADDENDUM_DOC_MESSAGE, Boolean.TRUE);
						aoRequest.setAttribute(HHSConstants.ERROR_MESSAGE, lsAddendumProposalDocMessage);
						break;
					}
				}
			}
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error occured while processing Required Proposal document List" + aoRequiredDocumentList,
					aoExp);
			throw new ApplicationException("Error occured while processing Required Proposal document List", aoExp);
		}
	}

	/**
	 * This method renders user to Proposal Summary screen
	 * <ul>
	 * <li>The control will transfer to Summary Screen when next button is
	 * clicked from RFP Documents by provider user</li>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse object
	 */
	@ActionMapping(params = "submit_action=rfpDocumentsNextAction")
	public void nextRfpDocuments(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		setNavigationParamsInRender(aoRequest, aoResponse);
		aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
		aoResponse.setRenderParameter(HHSConstants.TOP_LEVEL_FROM_REQ, HHSConstants.PROPOSAL_SUMMARY);
		aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.PROPOSAL_SUMMARY_LOWERCASE);
		aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
	}

	/**
	 * <p>
	 * This method will insert a new record whenever a new zip file request is
	 * made from View Award Documents Screen
	 * <ul>
	 * <li>Get the document status from Request.</li>
	 * <li>If the Document Status is already requested then an error message is
	 * set in request and returned</li>
	 * <li>Else if Get the transaction name set in the Channel Object</li>
	 * <li>Execute <b>requestZipFile</b> Transaction which insert a new record
	 * entry in DB for the requested Zip File</li>
	 * <li>Success Message is set in request and returned</li>
	 * <li>Method added for 3.1.0 enhancement: 6025</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoRequest ResourceRequest object
	 * @param aoResponse ResourceResponse object
	 * @return String object the view of jsp to be returned
	 * @throws ApplicationException if any exception occurred
	 */
	@ResourceMapping("requestZip")
	protected String requestZipFile(ResourceRequest aoRequest, ResourceResponse aoResponse)
	{
		try
		{
			ExtendedDocument loAwardDocDetails = null;
			String lsFileName = aoRequest.getParameter(HHSConstants.FILE_NAME_PARAMETER);
			String lsDocStatus = aoRequest.getParameter(HHSConstants.DOC_STATUS);
			String lsProviderOrgID = aoRequest.getParameter(HHSConstants.PROVIDER_ORG_ID);
			String lsProcurementId = aoRequest.getParameter(HHSConstants.PROCUREMENT_ID);
			String lsProcStatus = aoRequest.getParameter(HHSConstants.AS_PROC_STATUS);
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			String lsEvaluationPoolMappingId = aoRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID);
			if (!lsDocStatus.equalsIgnoreCase(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.DOCUMENTS_REQUESTED)))
			{
				Map<String, String> loInputParam = new HashMap<String, String>();
				loInputParam.put(HHSConstants.AS_FILE_NAME_PARAMETER, lsFileName);
				loInputParam.put(HHSConstants.AS_DOC_STATUS, lsDocStatus);
				loInputParam.put(HHSConstants.AS_PROVIDER_ORG_ID, lsProviderOrgID);
				loInputParam.put(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
				loInputParam.put(HHSConstants.AS_PROC_STATUS, lsProcStatus);
				loInputParam.put(HHSConstants.AS_USER_ID, lsUserId);
				loInputParam.put(HHSConstants.AS_EVALUATION_POOL_MAPPING_ID, lsEvaluationPoolMappingId);
				Channel loChannel = new Channel();
				loChannel.setData(HHSConstants.AO_PARAMETER_MAP, loInputParam);
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.REQUEST_ZIP_FILE);
				loAwardDocDetails = (ExtendedDocument) loChannel.getData(HHSConstants.AO_AWARD_DOC_DETAILS);
				aoRequest.setAttribute(HHSConstants.AWARD_DOC_DETAILS, loAwardDocDetails);
				aoRequest
						.setAttribute(HHSConstants.SUCCESS_MSG, PropertyLoader.getProperty(
								HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.DOC_REQ_MSG));
			}
			else
			{
				Channel loChannel = new Channel();
				loChannel.setData(HHSConstants.AS_DOC_STATUS, lsDocStatus);
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_DOCUMENT_STATUS);
				String lsStatus = (String) loChannel.getData(HHSConstants.LS_DOC_STATUS);
				aoRequest.setAttribute(HHSConstants.FAIL_MSG, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.DOC_REQ_ERROR_MSG));
				loAwardDocDetails = new ExtendedDocument();
				loAwardDocDetails.setDocumentTitle(lsFileName);
				loAwardDocDetails.setStatusId(lsDocStatus);
				loAwardDocDetails.setStatus(lsStatus);
				aoRequest.setAttribute(HHSConstants.AWARD_DOC_DETAILS, loAwardDocDetails);
			}

		}
		catch (ApplicationException aoAppExp)
		{
			handleApplicationException(aoRequest, aoAppExp);
		}
		catch (Exception aoExp)
		{
			handleException(aoRequest, aoExp);

		}
		return HHSConstants.VIEW_AWARD_DOCUMENTS;
	}

	/**
	 * Added in R5: This method will redirect user to the next tab of the upload
	 * document screen
	 * <ul>
	 * <li>Get the user organization type from the request</li>
	 * <li>If the user is is of provider organization type then redirect user to
	 * <b>displayUploadingDocumentInfoProvider</b> view</li>
	 * <li>Else redirect user to the <b>displayUploadingDocumentInfoAgency</b>
	 * view</li>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * Changes done for Enhancement #6429 for Release 3.4.0
	 * 
	 * @param aoRequest Render Request Object
	 * @param aoResponse Render Response Object
	 * @return ModelAndView to display displayUploadingDocumentInfoProvider.jsp
	 * 
	 */
	@ActionMapping(params = "submit_action=fileLocation")
	protected void displayUploadingFileLocationRender(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		Document loDocument = new Document();
		String lsUserOrgType = null;
		Map<String, Object> loPropertyMapInfo = new HashMap<String, Object>();
		HashMap<String, Object> loPropertyMap = new HashMap<String, Object>();
		try
		{
			lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			loDocument = (Document) ApplicationSession.getAttribute(aoRequest, true,
					ApplicationConstants.SESSION_DOCUMENT_OBJ);

			if (null == loDocument)
			{
				loDocument = (Document) aoRequest.getPortletSession().getAttribute(HHSR5Constants.DOC_SESSION_BEAN);
			}
			String lsEvalPoolMappingId = aoRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID);
			String lsIsFinancials = aoRequest.getParameter(HHSConstants.IS_FINANCIAL);
			String lsContractId = aoRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW);
			String lsProcurementId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
			String lsProposalId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROPOSAL_ID);
			String lsAwardId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.AWARD_ID);
			String lsUploadingDocType = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.UPLOAD_DOC_TYPE);
			String lsIsAddendumDoc = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.IS_ADDENDUM_DOC);
			String lsDocRefNum = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.HIDDEN_DOC_REF_SEQ_NO);
			String lsReplacingDocumentId = HHSPortalUtil
					.parseQueryString(aoRequest, HHSConstants.REPLACING_DOCUMENT_ID);
			String lsToplevelFromReq = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.TOP_LEVEL_FROM_REQ);
			String lsMiddlelevelFromReq = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.MID_LEVEL_FROM_REQ);
			String lsOrganizationId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.ORGA_ID);
			String lsStaffId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.STAFF_ID);
			String lsHiddenContractId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.HIDDEN_HDNCONTRACTID);
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
					if (HHSR5Constants.ON.equalsIgnoreCase(aoRequest.getParameter(loDocProps.getPropertyId()))) // request.getParameter("accidentalCover").equals("checked"))
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
					// Added for setting the property value in the
					// DocumentPropertiesBean
					loDocProps.setPropValue(aoRequest.getParameter(loDocProps.getPropertyId()));
					// end
				}
			}
			FileNetOperationsUtils.validatorForUpload((HashMap) loPropertyMapInfo, loDocument.getDocType());
			aoRequest.getPortletSession().setAttribute(HHSR5Constants.DOC_SESSION_BEAN, loDocument);
			ApplicationSession.setAttribute(loDocument, aoRequest, ApplicationConstants.SESSION_DOCUMENT_OBJ);
			aoRequest.getPortletSession().setAttribute(HHSR5Constants.PROPERTY_MAP_INFO, loPropertyMapInfo,
					PortletSession.APPLICATION_SCOPE);
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSR5Constants.CREATE_TREE);
			if (lsDocRefNum != null && !lsDocRefNum.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.HIDDEN_DOC_REF_SEQ_NO, lsDocRefNum);
			}
			if (lsEvalPoolMappingId != null && !lsEvalPoolMappingId.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolMappingId);
			}
			if (lsIsFinancials != null && !lsIsFinancials.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.IS_FINANCIAL, lsIsFinancials);
			}
			if (lsContractId != null && !lsContractId.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.CONTRACT_ID_WORKFLOW, lsContractId);
			}
			if (lsProcurementId != null && !lsProcurementId.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			}
			if (lsProposalId != null && !lsProposalId.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.PROPOSAL_ID, lsProposalId);
			}
			if (lsIsAddendumDoc != null && !lsIsAddendumDoc.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.IS_ADDENDUM_DOC, lsIsAddendumDoc);
			}
			if (lsAwardId != null && !lsAwardId.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.AWARD_ID, lsAwardId);
			}
			if (lsUploadingDocType != null && !lsUploadingDocType.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.UPLOAD_DOC_TYPE, lsUploadingDocType);
			}
			if (lsReplacingDocumentId != null && !lsReplacingDocumentId.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.REPLACING_DOCUMENT_ID, lsReplacingDocumentId);
			}
			if (lsToplevelFromReq != null && !lsToplevelFromReq.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.TOP_LEVEL_FROM_REQ, lsToplevelFromReq);
			}
			if (lsMiddlelevelFromReq != null && !lsMiddlelevelFromReq.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.MID_LEVEL_FROM_REQ, lsMiddlelevelFromReq);
			}
			if (lsOrganizationId != null && !lsOrganizationId.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.ORGA_ID, lsOrganizationId);
			}
			if (lsStaffId != null && !lsStaffId.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.STAFF_ID, lsStaffId);
			}
			if (lsHiddenContractId != null && !lsHiddenContractId.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.HIDDEN_HDNCONTRACTID, lsHiddenContractId);
			}
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception in action file upload screen in Document Vault", aoExp);
			try
			{
				setErrorMessageInResponse(aoRequest, aoResponse, aoExp, null);
			}
			catch (IOException e)
			{
				setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
						ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
			}

		}
	}

	/**
	 * This method will will show the tree structure of Document vault in upload
	 * step3 for file location.
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
		//Fix for defect 8378
		aoRequest.setAttribute(HHSConstants.IS_ADDENDUM, aoRequest.getParameter(HHSConstants.IS_ADDENDUM_DOC));
		//Fix for defect 8378 end
		aoRequest.setAttribute(HHSConstants.AWARD_ID, aoRequest.getParameter(HHSConstants.AWARD_ID));
		aoRequest.setAttribute(HHSConstants.UPLOAD_DOC_TYPE, aoRequest.getParameter(HHSConstants.UPLOAD_DOC_TYPE));
		aoRequest.setAttribute(HHSConstants.REPLACING_DOCUMENT_ID,
				aoRequest.getParameter(HHSConstants.REPLACING_DOCUMENT_ID));
		aoRequest
				.setAttribute(HHSConstants.TOP_LEVEL_FROM_REQ, aoRequest.getParameter(HHSConstants.TOP_LEVEL_FROM_REQ));
		aoRequest
				.setAttribute(HHSConstants.MID_LEVEL_FROM_REQ, aoRequest.getParameter(HHSConstants.MID_LEVEL_FROM_REQ));
		aoRequest.setAttribute(HHSConstants.ORGA_ID, aoRequest.getParameter(HHSConstants.ORGA_ID));
		aoRequest.setAttribute(HHSConstants.STAFF_ID, aoRequest.getParameter(HHSConstants.STAFF_ID));
		aoRequest.setAttribute(HHSConstants.HIDDEN_HDNCONTRACTID,
				aoRequest.getParameter(HHSConstants.HIDDEN_HDNCONTRACTID));
		lsFormPath = HHSR5Constants.RFP_UPLOAD_LOCATION;
		return lsFormPath;
	}

	/**
	 * This method will redirect user to the back screen from step 3 while
	 * uploading a document
	 * @param aoRequest Action Request
	 * @param aoResponse Action Response
	 */
	@ActionMapping(params = "submit_action=goBackActionFromStep3")
	public void goBackActionFromStep3(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		String lsProcurementId = null;
		Document loDocument = null;
		String lsUploadingDocumentType = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.UPLOAD_DOC_TYPE);
		try
		{
			String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);

			loDocument = (Document) ApplicationSession.getAttribute(aoRequest, true,
					ApplicationConstants.SESSION_DOCUMENT_OBJ);

			if (null == loDocument)
			{
				loDocument = (Document) aoRequest.getPortletSession().getAttribute(HHSR5Constants.DOC_SESSION_BEAN);
			}
			String lsDocCategory = HHSPortalUtil.parseQueryString(aoRequest, ApplicationConstants.DOCS_CATEGORY);
			String lsDocType = HHSPortalUtil.parseQueryString(aoRequest, ApplicationConstants.DOCS_TYPE);
			loDocument.setDocCategory(lsDocCategory);
			loDocument.setDocType(lsDocType);
			RFPReleaseDocsUtil.setRFPDocCategorynDocType(loDocument, lsDocCategory, lsUserOrgType, null);
			if (null != PortalUtil.parseQueryString(aoRequest, HHSConstants.TOP_LEVEL_FROM_REQ))
			{
				aoResponse.setRenderParameter(HHSConstants.TOP_LEVEL_FROM_REQ,
						PortalUtil.parseQueryString(aoRequest, HHSConstants.TOP_LEVEL_FROM_REQ));
			}
			if (null != PortalUtil.parseQueryString(aoRequest, HHSConstants.MID_LEVEL_FROM_REQ))
			{
				aoResponse.setRenderParameter(HHSConstants.MID_LEVEL_FROM_REQ,
						PortalUtil.parseQueryString(aoRequest, HHSConstants.MID_LEVEL_FROM_REQ));
			}
			ApplicationSession.setAttribute(loDocument, aoRequest, ApplicationConstants.SESSION_DOCUMENT_OBJ);
			lsProcurementId = PortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
			if (null != lsProcurementId && !lsProcurementId.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			}
			if (null != HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROPOSAL_ID))
			{
				aoResponse.setRenderParameter(HHSConstants.PROPOSAL_ID,
						HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROPOSAL_ID));
			}
			if (null != HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.ORGA_ID))
			{
				aoResponse.setRenderParameter(HHSConstants.ORGA_ID,
						HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.ORGA_ID));
			}
			if (null != HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.STAFF_ID))
			{
				aoResponse.setRenderParameter(HHSConstants.STAFF_ID,
						HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.STAFF_ID));
			}
			if (null != HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.USER))
			{
				aoResponse.setRenderParameter(HHSConstants.USER,
						HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.USER));
			}
			if (null != HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.EVALUATION_POOL_MAPPING_ID))
			{
				aoResponse.setRenderParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID,
						HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.EVALUATION_POOL_MAPPING_ID));
			}
			if (null != HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.AWARD_ID))
			{
				aoResponse.setRenderParameter(HHSConstants.AWARD_ID,
						HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.AWARD_ID));
			}
			if (null != HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.IS_FINANCIAL))
			{
				aoResponse.setRenderParameter(HHSConstants.IS_FINANCIAL,
						HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.IS_FINANCIAL));
			}
			if (null != HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.HIDDEN_HDNCONTRACTID))
			{
				aoResponse.setRenderParameter(HHSConstants.HIDDEN_HDNCONTRACTID,
						HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.HIDDEN_HDNCONTRACTID));
			}
			if (null != HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.AS_PROC_STATUS))
			{
				aoResponse.setRenderParameter(HHSConstants.AS_PROC_STATUS,
						HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.AS_PROC_STATUS));
			}
			if (null != HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.UPLOAD_DOC_TYPE))
			{
				aoResponse.setRenderParameter(HHSConstants.UPLOAD_DOC_TYPE,
						HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.UPLOAD_DOC_TYPE));
			}
			if (null != HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.HIDDEN_DOC_REF_SEQ_NO))
			{
				aoResponse.setRenderParameter(HHSConstants.HIDDEN_DOC_REF_SEQ_NO,
						HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.HIDDEN_DOC_REF_SEQ_NO));
			}
			if (null != PortalUtil.parseQueryString(aoRequest, HHSConstants.UPLOAD_PROCESS))
			{
				aoResponse.setRenderParameter(HHSConstants.UPLOAD_PROCESS,
						PortalUtil.parseQueryString(aoRequest, HHSConstants.UPLOAD_PROCESS));
			}

			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.UPLOADING_FILE_INFO);

		}
		catch (ApplicationException aoAppEx)
		{
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
		catch (Exception aoEXP)
		{
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
	}
}
