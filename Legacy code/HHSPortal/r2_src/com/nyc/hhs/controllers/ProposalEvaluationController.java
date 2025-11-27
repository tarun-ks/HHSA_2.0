package com.nyc.hhs.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.owasp.esapi.ESAPI;
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
import org.springframework.web.portlet.mvc.ResourceAwareController;

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
import com.nyc.hhs.model.AuthenticationBean;
import com.nyc.hhs.model.AutoCompleteBean;
import com.nyc.hhs.model.EvaluationBean;
import com.nyc.hhs.model.EvaluationFilterBean;
import com.nyc.hhs.model.EvaluationGroupsProposalBean;
import com.nyc.hhs.model.EvaluationSummaryBean;
import com.nyc.hhs.model.Evaluator;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.NotificationDataBean;
import com.nyc.hhs.model.Procurement;
import com.nyc.hhs.model.ProcurementInfo;
import com.nyc.hhs.model.ProposalDetailsBean;
import com.nyc.hhs.model.ProposalFilterBean;
import com.nyc.hhs.model.RFPReleaseBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.ApplicationSession;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.HHSPortalUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PortalUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This controller class will handle all the request made by user from the
 * Proposal and Evaluations tab like fetching Provider Evaluation Scores,viewing
 * proposal summary, retracting proposal, cancelling proposal, fetching
 * evaluation Results and Selections details, mark proposals selected or not
 * selected, viewing award review comments or selection comments, finalizing or
 * updating evaluation results. Also it will handle the sorting of the columns
 * and navigating through different pages.
 * 
 * This controller will be executed from different screens. Below are the screen
 * ids 1- S215 2- S216 3- S218 4- S219 5- S261
 */
@Controller(value = "propEvalHandler")
@RequestMapping("view")
public class ProposalEvaluationController extends BaseControllerSM implements ResourceAwareController
{

	/**
	 * This is the logger object which is used to log exception into log file
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(ProposalEvaluationController.class);

	/**
	 * This method will initialize the evaluation bean object
	 * 
	 * @return EvaluationBean Object
	 */
	@ModelAttribute("EvaluationBean")
	public EvaluationBean getCommandObject()
	{
		return new EvaluationBean();
	}

	/**
	 * This method will initialize the procurement bean object
	 * 
	 * @return Procurement Bean Object
	 */
	@ModelAttribute("Procurement")
	public Procurement getProcurementCommandObject()
	{
		return new Procurement();
	}

	/**
	 * This method will initialize the evaluation bean object
	 * 
	 * @return EvaluationBean Object
	 */
	@ModelAttribute("ProposalDetailsBean")
	public ProposalDetailsBean getProposalCommandObject()
	{
		return new ProposalDetailsBean();
	}

	/**
	 * This method will initialize the evaluation filter bean object
	 * 
	 * @return EvaluationFilterBean Object
	 */
	@ModelAttribute("EvaluationFilterBean")
	public EvaluationFilterBean getEvaluationCommandObject()
	{
		return new EvaluationFilterBean();
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
	 * This method will initialize the Evaluator object
	 * 
	 * @return Evaluator Object
	 */
	@ModelAttribute("Evaluator")
	public Evaluator getEvaluatorObject()
	{
		return new Evaluator();
	}

	/**
	 * This method binds the date field on the jsp page with the date fields in
	 * the bean. also this method checks for the format and number of character
	 * to perform the server side validations. and if the format or number of
	 * character differs from the one specified in this method the errors will
	 * get stored in the resultBinder.
	 * 
	 * @param aoWebDataBinder - WebDataBinder
	 */
	@InitBinder
	public void initBinder(WebDataBinder aoWebDataBinder)
	{
		SimpleDateFormat loSimpleDateFormat = new SimpleDateFormat(HHSConstants.MMDDYYFORMAT);
		aoWebDataBinder.registerCustomEditor(Date.class, null, new CustomDateEditor(loSimpleDateFormat, true));
	}

	/**
	 * Validator Object
	 */
	@Autowired
	private Validator validator;

	/**
	 * Constructor to set validator object
	 * @param aoValidatorObject validator value
	 */
	public void setValidator(Validator aoValidatorObject)
	{
		this.validator = aoValidatorObject;
	}

	/**
	 * This method is called when Proposals and Evaluations tab is clicked.
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1.It will get procurement Id, Competition Pool Id and Evaluation
	 * Group Id from request.</li>
	 * <li>2.Get the value of selected child screen.</li>
	 * <li>3.If its value is "EvaluationGroupsSummary", call
	 * getGroupProposalSummary() to render Evaluation Groups - Proposals screen</li>
	 * <li>4.If its value is "evaluationSummary", call
	 * getProposalEvaluationSummary() to render Proposals and Evaluations
	 * Summary</li>
	 * <li>5.If its value is "EvaluationSettings", call
	 * fetchEvaluationSettings() to render Evaluation Settings screen</li>
	 * <li>6.If its value is "EvaluationResultsandSelections", call
	 * fetchEvaluationResultsSelections() to render Evaluation Results and
	 * Selections screen</li>
	 * <li>7.Return model and view object to jsp</li>
	 * </ul>
	 * 
	 * @param aoRenderRequest - RenderRequest of the portlet
	 * @param aoRenderResponse - RenderResponse of the portlet
	 * @return ModelAndView containg the jsp name -- in this evaluation settings
	 */
	@RenderMapping
	protected ModelAndView handleRenderRequest(RenderRequest aoRenderRequest, RenderResponse aoRenderResponse)
	{
		final String lsProcurementId = PortalUtil.parseQueryString(aoRenderRequest, HHSConstants.PROCUREMENT_ID);
		String lsCompetitionPoolId = PortalUtil.parseQueryString(aoRenderRequest, HHSConstants.COMPETITION_POOL_ID);
		if (lsCompetitionPoolId != null && (lsCompetitionPoolId == HHSConstants.NULL || lsCompetitionPoolId.isEmpty()))
		{
			lsCompetitionPoolId = null;
		}
		String lsEvaluationGroupId = PortalUtil.parseQueryString(aoRenderRequest, HHSConstants.EVALUATION_GROUP_ID);
		if (lsEvaluationGroupId != null && (lsEvaluationGroupId == HHSConstants.NULL || lsEvaluationGroupId.isEmpty()))
		{
			lsEvaluationGroupId = null;
		}
		String lsEvaluationPoolMappingId = PortalUtil.parseQueryString(aoRenderRequest,
				HHSConstants.EVALUATION_POOL_MAPPING_ID);
		if (null != lsEvaluationPoolMappingId
				&& (lsEvaluationPoolMappingId == HHSConstants.NULL || lsEvaluationPoolMappingId.isEmpty()))
		{
			lsEvaluationPoolMappingId = null;
		}
		final Map<String, Object> loHmReqExceProp = new HashMap<String, Object>();
		loHmReqExceProp.put(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
		ModelAndView loModelAndView = null;
		try
		{
			getPageHeader(aoRenderRequest, lsProcurementId);
			String lsSelectChildScreen = (String) aoRenderRequest.getAttribute(HHSConstants.SELECTED_CHILD_TAB);
			String lsUserOrgType = (String) aoRenderRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			String lsUserRole = (String) aoRenderRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ROLE, PortletSession.APPLICATION_SCOPE);
			Channel loChannel = new Channel();
			loChannel.setData(HHSConstants.ORG_TYPE, lsUserOrgType);
			loChannel.setData(HHSConstants.USER_ROLE, lsUserRole);
			aoRenderRequest.setAttribute(HHSConstants.CHANNEL_ACCESS, loChannel);
			if (lsSelectChildScreen != null
					&& lsSelectChildScreen.equalsIgnoreCase(HHSConstants.EVALUATION_GROUPS_SUMMARY))
			{
				loModelAndView = getGroupProposalSummary(aoRenderRequest, lsProcurementId);
			}
			else if (lsSelectChildScreen != null && lsSelectChildScreen.equalsIgnoreCase(HHSConstants.EVAL_SUMMARY))
			{
				loModelAndView = getProposalEvaluationSummary(aoRenderRequest, lsProcurementId, lsEvaluationGroupId);
			}
			else if (lsSelectChildScreen != null
					&& lsSelectChildScreen.equalsIgnoreCase(HHSConstants.SECOND_LEVEL_ID[11]))
			{
				loModelAndView = fetchEvaluationSettings(aoRenderRequest, lsProcurementId, lsEvaluationGroupId,
						lsEvaluationPoolMappingId, lsUserOrgType, lsUserRole, loChannel);
			}
			else if (lsSelectChildScreen != null
					&& lsSelectChildScreen.equalsIgnoreCase(HHSConstants.SECOND_LEVEL_ID[13]))
			{
				loModelAndView = fetchEvaluationResultsSelections(aoRenderRequest, aoRenderResponse,
						lsEvaluationPoolMappingId);
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoException)
		{
			setGenericErrorMessage(aoRenderRequest);
		}
		// handling exception other than Application Exception.
		catch (Exception aoException)
		{
			setGenericErrorMessage(aoRenderRequest);
			LOG_OBJECT.Error(
					"Exception other than application exception occured while getting the internal evaluator list"
							+ "from the database", aoException);
		}
		return loModelAndView;
	}

	/**
	 * This method renders to evaluation settings page when
	 * "Edit Evaluation Settings" option is selected from dropdown or
	 * <Competition Pool> hyperlink is clicked from Proposals and Evaluations
	 * Summary screen
	 * <ul>
	 * <li>1. It will set all the parameters</li>
	 * </ul>
	 * 
	 * @param aoRenderRequest RenderRequest object
	 * @param asProcurementId a string value of procurement Id
	 * @param asEvaluationGroupId a string value of evaluation group Id
	 * @param asEvaluationPoolMappingId a string value of evaluation pool
	 *            mapping Id
	 * @param asUserOrgType a string value of user org type
	 * @param asUserRole a string value of user role
	 * @param aoChannel Channel object
	 * @return ModelAndView object containing jsp name
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private ModelAndView fetchEvaluationSettings(RenderRequest aoRenderRequest, final String asProcurementId,
			String asEvaluationGroupId, String asEvaluationPoolMappingId, String asUserOrgType, String asUserRole,
			Channel aoChannel) throws ApplicationException
	{
		ModelAndView loModelAndView = null;
		aoRenderRequest.setAttribute(HHSConstants.NYC_AGENCY_MASTER, HHSUtil.getAgencyMapForProcurement());
		aoChannel.setData(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		aoChannel.setData(HHSConstants.EVALUATION_SEND, true);
		aoChannel.setData(HHSConstants.ORG_TYPE, asUserOrgType);
		aoChannel.setData(HHSConstants.USER_ROLE, asUserRole);
		// loHMWFRequiredProps hashmap is set in channel to fetch
		// whether the "Review Score" task
		// is generated for this procurement or not
		Map loHMWFRequiredProps = new HashMap();
		loHMWFRequiredProps.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, asProcurementId);
		loHMWFRequiredProps.put(P8Constants.PROPERTY_PE_TASK_NAME, P8Constants.TASK_REVIEW_SCORES);
		ProcurementInfo loProcurementInfo = (ProcurementInfo) aoRenderRequest
				.getAttribute(HHSConstants.PROCUREMENT_BEAN);
		String lsIsRfpReleasedBefore = loProcurementInfo.getIsRfpReleasedBefore();
		if (null == lsIsRfpReleasedBefore)
		{
			loHMWFRequiredProps.put(P8Constants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
		}
		P8UserSession loUserSession = (P8UserSession) aoRenderRequest.getPortletSession().getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		aoChannel.setData(HHSConstants.AO_HMWF_REQUIRED_PROPS, loHMWFRequiredProps);
		aoChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
		aoChannel.setData(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
		aoChannel.setData(HHSConstants.EVALUATION_GROUP_ID, asEvaluationGroupId);
		// hit the transaction layer to get the internal and external
		// evaluation list
		HHSTransactionManager.executeTransaction(aoChannel, HHSConstants.FETCH_EVALUATION_SETTINGS);
		// Start : Added in R5
		aoRenderRequest.setAttribute(HHSR5Constants.IS_AWARD_STATUS_RETURNED,
				(Boolean) aoChannel.getData(HHSR5Constants.AWARD_REVIEW_STATUS));
		// End : Added in R5
		List<Evaluator> loInternalEvaluatorUsersList = (List<Evaluator>) aoChannel
				.getData(HHSConstants.EVALUATION_LIST_INTERNAL);
		List<Evaluator> loExternalEvaluatorUsersList = (List<Evaluator>) aoChannel
				.getData(HHSConstants.EVALUATION_LIST_EXTERNAL);
		Boolean lbReviewTaskPresent = (Boolean) aoChannel.getData(HHSConstants.STATUS_FLAG);
		// Start || Changes done for Enhancement #6577 for Release 3.10.0
		Boolean lbEvalPoolCancelled = (Boolean) aoChannel.getData(HHSConstants.EVAL_POOL_CANCELLED);
		aoRenderRequest.setAttribute(HHSConstants.EVAL_POOL_CANCELLED, lbEvalPoolCancelled);
		// End || Changes done for Enhancement #6577 for Release 3.10.0
		aoRenderRequest.setAttribute(HHSConstants.EVALUATION_LIST_INTERNAL, loInternalEvaluatorUsersList);
		aoRenderRequest.setAttribute(HHSConstants.EVALUATION_LIST_EXTERNAL, loExternalEvaluatorUsersList);
		aoRenderRequest.setAttribute(HHSConstants.AGENCY_ID, ((Map<String, String>) aoChannel
				.getData(HHSConstants.PROCUREMENT_AGENCY_STATUS_ID)).get(HHSConstants.AGENCY_ID_TABLE_COLUMN));
		if (lbReviewTaskPresent)
		{
			aoRenderRequest.setAttribute(HHSConstants.REVIEW_TASK_PRESENT, true);
		}
		else
		{
			aoRenderRequest.setAttribute(HHSConstants.REVIEW_TASK_PRESENT, false);
		}
		aoRenderRequest.setAttribute(HHSConstants.COMPETITION_POOL_LIST,
				aoChannel.getData(HHSConstants.COMPETITION_POOL_LIST));
		aoRenderRequest.setAttribute(HHSConstants.GROUP_TITLE_MAP, aoChannel.getData(HHSConstants.GROUP_TITLE_MAP));
		aoRenderRequest.setAttribute(HHSConstants.EVALUATION_GROUP_ID,
				aoChannel.getData(HHSConstants.EVALUATION_GROUP_ID));
		// R5 code Starts
		aoRenderRequest
				.setAttribute(HHSR5Constants.EVALUATOR_DOC_LIST, aoChannel.getData(HHSR5Constants.DOC_TYPE_LIST));
		// R5 code Starts
		Map<String, Object> loProcurementStatusMap = (Map<String, Object>) aoChannel
				.getData(HHSConstants.PROCUREMENT_AGENCY_STATUS_ID);
		String loProcurementStatus = loProcurementStatusMap.get(HHSConstants.STATUS).toString();
		if (loProcurementStatus != null && loProcurementStatus.equals(HHSConstants.SEVEN))
		{
			aoRenderRequest.setAttribute(HHSConstants.EVALUATION_REVIEW_TASK, true);
		}
		else
		{
			aoRenderRequest.setAttribute(HHSConstants.EVALUATION_REVIEW_TASK,
					aoChannel.getData(HHSConstants.EVALUATION_REVIEW_TASK));
		}
		// This code execute when evaluation task has been send and user
		// wants to add more evaluators on the 214 screen
		if (PortalUtil.parseQueryString(aoRenderRequest, HHSConstants.ERROR_FLAG) != null
				&& PortalUtil.parseQueryString(aoRenderRequest, HHSConstants.ERROR_FLAG).equalsIgnoreCase(
						HHSConstants.ERROR_FLAG))
		{
			aoRenderRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
					PortalUtil.parseQueryString(aoRenderRequest, ApplicationConstants.ERROR_MESSAGE));
			aoRenderRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
		aoRenderRequest.getPortletSession().setAttribute(
				HHSConstants.EVALUATION_SETTING_AGENCY_ID,
				((Map<String, String>) aoChannel.getData(HHSConstants.PROCUREMENT_AGENCY_STATUS_ID))
						.get(HHSConstants.AGENCY_ID_TABLE_COLUMN));
		aoRenderRequest.setAttribute(HHSConstants.CHANNEL_ACCESS, aoChannel);
		loModelAndView = new ModelAndView(HHSConstants.EVALUATION_SETTINGS_SCREEN);
		return loModelAndView;
	}

	/**
	 * This method is used to set the agency when change the s214 drop down for
	 * agency, we need this because based on agency we required type head search
	 * for internal and external evaluators
	 * @param aoResourceRequest portal resource request
	 * @param aoResourceResponse portal resource response
	 * @throws ApplicationException application exception
	 */
	@ResourceMapping("setAgency")
	public void setAgencyIntoSession(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		aoResourceRequest.getPortletSession().setAttribute(HHSConstants.EVALUATION_SETTING_AGENCY_ID,
				HHSPortalUtil.parseQueryString(aoResourceRequest, HHSConstants.AGENCYID));
	}

	/**
	 * This method used to fetch the internal evaluator from the 214 screen
	 * <ul>
	 * <li>1. Get the required data from request and set in Channel</li>
	 * <li>2. Execute the transaction "fetchInternalEvaluatorUsers" to get the
	 * internal evaluator list</li>
	 * <li>3. Set the results of the transaction in the json</li>
	 * </ul>
	 * @param aoResourceRequest - a ResourceRequest object
	 * @param aoResourceResponse - a ResourceResponse object
	 * @throws ApplicationException if any exception occurred
	 */
	@SuppressWarnings("unchecked")
	@ResourceMapping("getInternalEvaluatorsUrl")
	public void fetchInternalEvaluatorUsers(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		final String lsInputParam = aoResourceRequest.getParameter(HHSConstants.QUERY);
		final Map<String, Object> loHmReqExceProp = new HashMap<String, Object>();
		PrintWriter loOut = null;
		try
		{
			final String lsAgencyId = (String) aoResourceRequest.getPortletSession().getAttribute(
					HHSConstants.EVALUATION_SETTING_AGENCY_ID);
			loHmReqExceProp.put(HHSConstants.AGENCYID, lsAgencyId);
			Channel loChannel = new Channel();
			loChannel.setData(HHSConstants.AS_AGENCY_ID, lsAgencyId);
			loChannel.setData(HHSConstants.INPUT_PARAM_MAP, lsInputParam);
			// hit the transaction to get the internal evaluator agency list
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_INTERNAL_EVALUATOR_USERS);
			final List<AutoCompleteBean> loInternalEvaluatorList = (List<AutoCompleteBean>) loChannel
					.getData(HHSConstants.INTERNAL_EVALUATOR_LIST);
			if ((lsInputParam != null) && (lsInputParam.length() >= Integer.parseInt(HHSConstants.THREE))
					&& loInternalEvaluatorList != null)
			{
				loOut = aoResourceResponse.getWriter();
				aoResourceResponse.setContentType(HHSConstants.APPLICATION_JSON);
				final String lsOutputJSONaoResponse = HHSUtil
						.generateDelimitedAutoCompleteResponse(loInternalEvaluatorList, lsInputParam,
								Integer.parseInt(HHSConstants.THREE)).toString().trim();
				loOut.print(lsOutputJSONaoResponse);
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoException)
		{
			aoException.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception Occured while getting the internal evaluator list from the database",
					aoException);
			setGenericErrorMessage(aoResourceRequest);
		}
		// handling exception other than Application Exception.
		catch (Exception aoException)
		{
			LOG_OBJECT.Error("Exception occured while getting the internal evaluator list" + "from the database",
					aoException);
			setGenericErrorMessage(aoResourceRequest);
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
	 * This method used to fetch the external evaluator from the 214 screen
	 * 
	 * <ul>
	 * <li>1. Get the required data from request and set in Channel</li>
	 * <li>2. Execute the transaction "fetchExternalEvaluatorUsers" to get the
	 * external evaluator agency list</li>
	 * <li>3. Set the results of the transaction in the json</li>
	 * </ul>
	 * 
	 * @param aoResourceRequest - a ResourceRequest object
	 * @param aoResourceResponse - a ResourceResponse object
	 * 
	 * @throws ApplicationException if any exception occurred
	 */
	@SuppressWarnings("unchecked")
	@ResourceMapping("getExternalEvaluatorsUrl")
	public void fetchExternalEvaluatorUsers(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		final String lsInputParam = aoResourceRequest.getParameter(HHSConstants.QUERY);
		final String lsAgencyId = HHSPortalUtil.parseQueryString(aoResourceRequest, HHSConstants.AGENCYID);
		final Map<String, Object> loHmReqExceProp = new HashMap<String, Object>();
		loHmReqExceProp.put(HHSConstants.AGENCYID, lsAgencyId);
		PrintWriter loOut = null;
		try
		{
			final Channel loChannel = new Channel();
			loChannel.setData(HHSConstants.AS_AGENCY_ID, lsAgencyId);
			loChannel.setData(HHSConstants.INPUT_PARAM_MAP, lsInputParam);
			// hit the transaction to get the external evaluator agency list
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_EXTERNAL_EVALUATOR_USERS);
			final List<AutoCompleteBean> loExternalEvaluatorList = (List<AutoCompleteBean>) loChannel
					.getData(HHSConstants.EXTERNAL_EVALUATOR_LIST);
			if ((lsInputParam != null) && (lsInputParam.length() >= Integer.parseInt(HHSConstants.THREE))
					&& loExternalEvaluatorList != null)
			{
				loOut = aoResourceResponse.getWriter();
				aoResourceResponse.setContentType(HHSConstants.APPLICATION_JSON);
				final String lsOutputJSONaoResponse = HHSUtil
						.generateDelimitedAutoCompleteResponse(loExternalEvaluatorList, lsInputParam,
								Integer.parseInt(HHSConstants.THREE)).toString().trim();
				loOut.print(lsOutputJSONaoResponse);
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoException)
		{
			aoException.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception Occured while getting the external evaluator list from the database",
					aoException);
			setGenericErrorMessage(aoResourceRequest);
		}
		// handling exception other than Application Exception.
		catch (Exception aoException)
		{
			LOG_OBJECT.Error("Exception occured while getting the external evaluator list" + "from the database",
					aoException);
			setGenericErrorMessage(aoResourceRequest);
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
	 * This method used when user tries to save the evaluation setting from the
	 * 214 screen when
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>
	 * If at least 3 evaluators have been chosen</li>
	 * <li>
	 * If evaluation tasks have already been sent, If the number of evaluators
	 * is more than the number of evaluators who were sent tasks</li>
	 * <li>
	 * If evaluation tasks have already been sent, If the number of evaluators
	 * is equal to the number of evaluators who were sent tasks</li>
	 * <li>
	 * If evaluation tasks have already been sent, If the number of evaluators
	 * is less than the number of evaluators who were sent tasks</li>
	 * Modified as a part of release 3.6.0 for enhancement request 5905
	 * </ul>
	 * 
	 * @param aoEvaluator object of the evaluator
	 * @param aoBindingResult binding result of the evaluator bean
	 * @param aoActionRequest action request of the portal
	 * @param aoActionResponse action response of the portal
	 * @param aoModel ModelMap object
	 */
	@SuppressWarnings("unchecked")
	@ActionMapping(params = "submit_action=saveEvaluationSettings")
	protected void saveEvaluationSetting(@ModelAttribute("Evaluator") Evaluator aoEvaluator,
			BindingResult aoBindingResult, ActionRequest aoActionRequest, ActionResponse aoActionResponse,
			ModelMap aoModel)
	{
		final Map<String, List<Evaluator>> loParamMap = new LinkedHashMap<String, List<Evaluator>>();
		final String lsProcurementAgencyId = PortalUtil.parseQueryString(aoActionRequest, HHSConstants.AS_AGENCY_ID);
		String lsProcurementTitle = aoActionRequest.getParameter(HHSConstants.PROCUREMENT_TITLE);
		String lsProcurementId = null;
		String evalProgressMessage = null;
		try
		{
			final String lsUserId = (String) aoActionRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			// Start || Changes as a part of release 3.6.0 for enhancement
			// request 5905
			evalProgressMessage = (PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGE_PROP_FILE,
					HHSConstants.ERROR_MESSSAGE_EVALUATION_PROGRESS));
			lsProcurementId = PortalUtil.parseQueryString(aoActionRequest, HHSConstants.PROCUREMENT_ID);
			// End || Changes as a part of release 3.6.0 for enhancement request
			// 5905
			setNavigationParamsInRender(aoActionRequest, aoActionResponse);
			final String lsEvaluationGroupId = PortalUtil.parseQueryString(aoActionRequest,
					HHSConstants.EVALUATION_GROUP_ID);
			final String lsEvaluationPoolMappingId = PortalUtil.parseQueryString(aoActionRequest,
					HHSConstants.EVALUATION_POOL_MAPPING_ID);
			final Map<String, Evaluator> loFinalEvaluatorsMap = new LinkedHashMap<String, Evaluator>();
			// get the internal evaluator names from the request in the comma
			// separated string
			if (PortalUtil.parseQueryString(aoActionRequest, HHSConstants.INTERNAL_EVALUATOR_NAMES) != null
					&& !PortalUtil.parseQueryString(aoActionRequest, HHSConstants.INTERNAL_EVALUATOR_NAMES).equals(
							HHSConstants.EMPTY_STRING))
			{
				saveEvaluationSettingForInternalEvaluator(aoEvaluator, aoActionRequest, loParamMap, lsUserId,
						lsProcurementId, loFinalEvaluatorsMap, lsEvaluationGroupId, lsEvaluationPoolMappingId);
			}
			// get the external evaluator names from the request in the comma
			// separated string
			if (PortalUtil.parseQueryString(aoActionRequest, HHSConstants.EXTERNAL_EVALUATOR_NAMES) != null
					&& !PortalUtil.parseQueryString(aoActionRequest, HHSConstants.EXTERNAL_EVALUATOR_NAMES).equals(
							HHSConstants.EMPTY_STRING))
			{
				saveEvaluationSettingForExternalEvaluator(aoActionRequest, loParamMap, lsProcurementAgencyId, lsUserId,
						lsProcurementId, loFinalEvaluatorsMap, lsEvaluationGroupId, lsEvaluationPoolMappingId);
			}
			if (null != loFinalEvaluatorsMap && loFinalEvaluatorsMap.size() > HHSConstants.INT_ZERO)
			{
				// set the input parameters into the channel object
				Channel loChannel = saveEvaluationSettingFinal(aoEvaluator, aoActionRequest, loParamMap,
						lsProcurementTitle, lsProcurementId, lsEvaluationGroupId, lsEvaluationPoolMappingId,
						loFinalEvaluatorsMap);
				// output of the saved evaluators settings
				final Map<String, String> loSaveEvaluatorsMap = (Map<String, String>) loChannel
						.getData(HHSConstants.SAVE_EVALUATION_MAP);
				// if evaluators are more then the saved evaluators then display
				// error message
				if (loSaveEvaluatorsMap.containsKey(HHSConstants.MORE_EVALUATOR_ERROR_MESSAGE))
				{
					final String lsMessage = MessageFormat.format(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.EVALUATION_SETTING_MORE_EVALUATOR),
							loSaveEvaluatorsMap.get(HHSConstants.NEW_EVALUATOR_COUNT), loSaveEvaluatorsMap
									.get(HHSConstants.OLD_EVALUATOR_COUNT));
					aoActionResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, lsMessage);
					aoActionResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
							ApplicationConstants.MESSAGE_FAIL_TYPE);
					aoActionResponse.setRenderParameter(HHSConstants.ERROR_FLAG, HHSConstants.ERROR_FLAG);
				}
			}
			aoActionResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			aoActionResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
			// aoModel.remove(HHSConstants.EVALUATOR);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here.
		catch (ApplicationException aoException)
		{
			// Start || Changes as a part of release 3.6.0 for enhancement
			// request 5905
			if (null != evalProgressMessage
					&& evalProgressMessage.equalsIgnoreCase(aoException.getRootCause().getMessage()))
			{
				aoActionResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, evalProgressMessage);
				aoActionResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE);
				aoActionResponse.setRenderParameter(HHSConstants.ERROR_FLAG, HHSConstants.ERROR_FLAG);
				aoActionResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, lsProcurementId);
				aoActionResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
			}
			else
			{
				// End || Changes as a part of release 3.6.0 for enhancement
				// request 5905
				setExceptionMessageFromAction(aoActionResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
						ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap, aoException);
			}
		}
		// handling exception other than Application Exception.
		catch (Exception aoException)
		{
			LOG_OBJECT.Error("Exception Occured while saving the evaluation setting into the database", aoException);
			setExceptionMessageFromAction(aoActionResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
	}

	/**
	 * This method used when user tries to save the evaluation setting from the
	 * 214 screen when
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>
	 * If at least 3 evaluators have been chosen</li>
	 * <li>
	 * If evaluation tasks have already been sent, If the number of evaluators
	 * is more than the number of evaluators who were sent tasks</li>
	 * <li>
	 * If evaluation tasks have already been sent, If the number of evaluators
	 * is equal to the number of evaluators who were sent tasks</li>
	 * <li>
	 * If evaluation tasks have already been sent, If the number of evaluators
	 * is less than the number of evaluators who were sent tasks</li>
	 * </ul>
	 * 
	 * @param aoEvaluator object of the evaluator
	 * @param aoActionRequest object of the evaluator
	 * @param loParamMap input param map
	 * @param lsProcurementTitle proc title
	 * @param lsProcurementId proc id
	 * @param lsEvaluationGroupId eval group id
	 * @param lsEvaluationPoolMappingId eval pool mapping id
	 * @param loFinalEvaluatorsMap evaluators map
	 * @return channel object
	 * @throws NumberFormatException If an NumberFormatException occurs
	 * @throws ApplicationException If ApplicationException ocurs
	 */
	private Channel saveEvaluationSettingFinal(Evaluator aoEvaluator, ActionRequest aoActionRequest,
			final Map<String, List<Evaluator>> loParamMap, String lsProcurementTitle, final String lsProcurementId,
			final String lsEvaluationGroupId, final String lsEvaluationPoolMappingId,
			final Map<String, Evaluator> loFinalEvaluatorsMap) throws NumberFormatException, ApplicationException
	{
		Channel loChannel = new Channel();
		loChannel.setData(HHSConstants.LO_PARAM_MAP, loParamMap);
		loChannel.setData(HHSConstants.LO_PROCUREMENT_ID, lsProcurementId);
		loChannel.setData(HHSConstants.EVALUATION_GROUP_ID, lsEvaluationGroupId);
		loChannel.setData(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvaluationPoolMappingId);
		// R5 code starts
		String lsUserId = (String) aoActionRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
		loChannel.setData(HHSR5Constants.DOCUMENT_VISIBILITY_LIST, aoEvaluator.getDocumentVisibilityList());
		loChannel.setData(HHSR5Constants.USER_ID, lsUserId);
		// R5 code ends
		Integer loEvaluatorCount = 0;
		// Start || Changes as a part of release 3.6.0 for enhancement request
		// 5905
		if (null != loFinalEvaluatorsMap)
		{
			loEvaluatorCount = loFinalEvaluatorsMap.size();
		}
		// setting audit bean
		HhsAuditBean loAuditBean = new HhsAuditBean();
		loAuditBean.setAuditTableIdentifier(HHSConstants.AGENCY_AUDIT);
		loAuditBean.setEntityType(HHSConstants.EVALUATION_POOL_MAPPING_ID);
		loAuditBean.setData("Evaluator Count Updated to " + loEvaluatorCount);
		loAuditBean.setEntityId(lsEvaluationPoolMappingId);
		loAuditBean.setEventName(HHSConstants.SAVE_EVALUATION_SETTINGS);
		loAuditBean.setUserId(lsUserId);
		loAuditBean.setEventType(HHSConstants.SAVE_EVALUATION_SETTINGS);
		loChannel.setData(HHSConstants.AUDIT_BEAN, loAuditBean);
		// End || Changes as a part of release 3.6.0 for enhancement request
		// 5905
		loChannel.setData(HHSConstants.EVALUATOR_COUNT_NEW, loEvaluatorCount);

		loChannel.setData(HHSConstants.ALL_EVALUATORS_MAP, loFinalEvaluatorsMap);
		HashMap<String, Object> loCancelEvaluationTaskMap = new HashMap<String, Object>();
		P8UserSession loUserSession = (P8UserSession) aoActionRequest.getPortletSession().getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);

		String lsWorkflowName = P8Constants.PE_EVALUATION_UTILITY_WORKFLOW_NAME;
		loCancelEvaluationTaskMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, lsProcurementId);
		loCancelEvaluationTaskMap.put(P8Constants.ACTION_KEY_FOR_UTILITY_WORKFLOW, P8Constants.REASSIGN_EVALUATIONS);
		loCancelEvaluationTaskMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_TITLE, lsProcurementTitle);
		loCancelEvaluationTaskMap.put(P8Constants.EVALUATION_POOL_MAPPING_ID, lsEvaluationPoolMappingId);
		loChannel.setData(HHSConstants.PROCUREMENT_ID, lsProcurementId);
		loChannel.setData(
				HHSConstants.USER_ID,
				aoActionRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
						PortletSession.APPLICATION_SCOPE));
		loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
		loChannel.setData(HHSConstants.LO_WORKFLOW_NAME, lsWorkflowName);
		loChannel.setData(HHSConstants.AO_HMWF_REQUIRED_PROPS, loCancelEvaluationTaskMap);
		// execute transaction to save the internal and external settings
		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.SAVE_EVALUATION_SETTINGS);
		return loChannel;
	}

	/**
	 * This method is used to save the External evaluators details which is
	 * later used to save the details into data base
	 * 
	 * @param aoActionRequest Action Request Object
	 * @param aoParamMap Parameter map
	 * @param asProcurementAgencyId procurement id
	 * @param asUserId user id
	 * @param asProcurementId procurement id
	 * @param aoFinalEvaluatorsMap final evaluator map
	 * @param asEvaluationGroupId evaluation Id
	 * @param asEvaluationPoolMappingId evaluation Mapping Id
	 * @throws ApplicationException if any exception occurred
	 */
	private void saveEvaluationSettingForExternalEvaluator(ActionRequest aoActionRequest,
			final Map<String, List<Evaluator>> aoParamMap, final String asProcurementAgencyId, final String asUserId,
			final String asProcurementId, final Map<String, Evaluator> aoFinalEvaluatorsMap,
			String asEvaluationGroupId, String asEvaluationPoolMappingId) throws ApplicationException
	{
		try
		{
			// create list from comma separated string
			final ArrayList<String> loExternalEvaluatorsList = new ArrayList<String>(
					Arrays.asList(PortalUtil.parseQueryString(aoActionRequest, HHSConstants.EXTERNAL_EVALUATOR_NAMES)
							.split(HHSConstants.COMMA)));
			final List<Evaluator> loExternalList = new ArrayList<Evaluator>();
			String lsExtEvalName = null;
			Evaluator loEvaluator = null;
			for (final String lsExternalEvaluatorsName : loExternalEvaluatorsList)
			{
				lsExtEvalName = ESAPI.encoder().canonicalize(lsExternalEvaluatorsName);
				loEvaluator = new Evaluator(asProcurementId, asProcurementAgencyId, lsExtEvalName.substring(0,
						lsExtEvalName.indexOf(HHSConstants.DELIMETER_SIGN)), asUserId, lsExtEvalName.substring(
						lsExtEvalName.indexOf(HHSConstants.DELIMETER_SIGN) + 1, lsExtEvalName.length()),
						HHSConstants.EMPTY_STRING, asEvaluationGroupId, asEvaluationPoolMappingId);
				loExternalList.add(loEvaluator);
				aoFinalEvaluatorsMap.put(lsExtEvalName, loEvaluator);
			}
			aoParamMap.put(HHSConstants.EXTERNAL_LIST, loExternalList);
		}
		catch (Exception aoExp)
		{
			ApplicationException loAppEx = new ApplicationException("Error Occured while saving internal Evaluators",
					aoExp);
			LOG_OBJECT.Error("Error Occured while saving internal Evaluators", loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * This method is used to save the internal evaluators details which is
	 * later used to save the details into data base
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * @param aoEvaluator Evaluator Bean
	 * @param aoActionRequest Action Request Object
	 * @param aoParamMap Parameter map
	 * @param asUserId user id
	 * @param asProcurementId procurement id
	 * @param aoFinalEvaluatorsMap final evaluator map
	 * @param asEvaluationGroupId Evaluation Group Id
	 * @param asEvaluationPoolMappingId Evaluation Pool Mapping Id
	 * @throws ApplicationException
	 */
	private void saveEvaluationSettingForInternalEvaluator(Evaluator aoEvaluator, ActionRequest aoActionRequest,
			final Map<String, List<Evaluator>> aoParamMap, final String asUserId, final String asProcurementId,
			final Map<String, Evaluator> aoFinalEvaluatorsMap, String asEvaluationGroupId,
			String asEvaluationPoolMappingId) throws ApplicationException
	{
		try
		{
			// create list from comma separated string
			final ArrayList<String> loInternalEvaluatorsList = new ArrayList<String>(
					Arrays.asList(PortalUtil.parseQueryString(aoActionRequest, HHSConstants.INTERNAL_EVALUATOR_NAMES)
							.split(HHSConstants.COMMA)));
			final List<Evaluator> loInternalList = new ArrayList<Evaluator>();
			Evaluator loEvaluator = null;
			for (final String lsInternalEvaluatorsId : loInternalEvaluatorsList)
			{
				loEvaluator = new Evaluator(asProcurementId, lsInternalEvaluatorsId.substring(0,
						lsInternalEvaluatorsId.indexOf(HHSConstants.DELIMETER_SIGN)), lsInternalEvaluatorsId.substring(
						lsInternalEvaluatorsId.indexOf(HHSConstants.DELIMETER_SIGN) + 1,
						lsInternalEvaluatorsId.length()), aoEvaluator.getEvaluatorCount(), asUserId,
						asEvaluationGroupId, asEvaluationPoolMappingId);
				loInternalList.add(loEvaluator);
				aoFinalEvaluatorsMap.put(lsInternalEvaluatorsId, loEvaluator);
			}
			aoParamMap.put(HHSConstants.INTERNAL_LIST, loInternalList);
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error Occured while saving internal Evaluators", aoExp);
			throw new ApplicationException("Error Occured while saving internal Evaluators", aoExp);
		}
	}

	/**
	 * This method is used to fetch the Evaluation scores page for a provider.
	 * 
	 * <ul>
	 * <li>1. Get the proposalId from the request.</li>
	 * <li>2. Call transaction fetchProviderEvaluationScores.</li>
	 * <li>3. Transaction will call fetchEvaluationScores for score related info
	 * [Query Id : fetchEvaluationScores]</li>
	 * <li>4. After that fetchProposalDetails method for Header related
	 * details[QueryId :fetchProviderProposalHeader]</li>
	 * <li>5. Return the name of the jsp to be rendered.</li>
	 * </ul>
	 * 
	 * @param aoRenderRequest - ResourceRequest
	 * @param aoRenderResponse - ResourceResponse
	 * @return ModelAndView
	 */
	@SuppressWarnings("unchecked")
	@RenderMapping(params = "render_action=fetchProviderEvaluationScores")
	protected ModelAndView fetchProviderEvaluationScores(RenderRequest aoRenderRequest, RenderResponse aoRenderResponse)
	{
		String lsProposalId = aoRenderRequest.getParameter(HHSConstants.PROPOSAL_ID);
		String lsProcurementId = aoRenderRequest.getParameter(HHSConstants.PROCUREMENT_ID);
		Channel loChannel = new Channel();
		Map<String, String> loProposalDetails = new HashMap<String, String>();
		loProposalDetails.put(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
		loProposalDetails.put(HHSConstants.PROPOSAL_ID_KEY, lsProposalId);
		loChannel.setData(HHSConstants.PROPOSAL_DETAILS_MAP, loProposalDetails);
		loChannel.setData(HHSConstants.PROPOSAL_ID_KEY, lsProposalId);
		try
		{
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.TRAN_FETCH_EVAL_SCORE);
			List<EvaluationBean> loEvalutionBeanList = (List<EvaluationBean>) loChannel
					.getData(HHSConstants.TRAN_RESULT_EVAL_SCORE);
			Map<String, String> loHeaderDetails = (Map<String, String>) loChannel
					.getData(HHSConstants.TRAN_RESULT_HEADER_DETAILS);
			aoRenderRequest.setAttribute(HHSConstants.EVAL_BEAN_LIST, loEvalutionBeanList);
			aoRenderRequest.setAttribute(HHSConstants.HEADER_DETAILS, loHeaderDetails);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error occurred while rendering evaluation score ", aoExp);
			setGenericErrorMessage(aoRenderRequest);
		}
		// handling exception other than Application Exception.
		catch (Exception aoException)
		{
			LOG_OBJECT.Error("Error occurred while rendering evaluation score", aoException);
			setGenericErrorMessage(aoRenderRequest);
		}
		return new ModelAndView(HHSConstants.PROV_EVAL_SCORES);
	}

	/**
	 * <p>
	 * The Method will show popup of Proposal Comments on Proposal details
	 * <ul>
	 * <li>1.Call transaction fetchProposalComments</li>
	 * <li>2. Transaction will call fetchProposalComments for fetching comments
	 * [Query Id : fetchProposalComments]</li>
	 * <li>3. Return the name of the jsp to be rendered.</li>
	 * </ul>
	 * <p>
	 * 
	 * @param aoResourceRequest resource request object
	 * @param aoResourceResponse resource response object
	 * @return String the name of the JSP page
	 * @throws Exception if any exception occurred
	 */
	@SuppressWarnings("unchecked")
	@ResourceMapping("showProposalComments")
	public String showProposalComments(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		try
		{
			Channel loChannel = new Channel();
			String lsProposalId = aoResourceRequest.getParameter(HHSConstants.PROPOSAL_ID);
			loChannel.setData(HHSConstants.PROPOSAL_ID_KEY, lsProposalId);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_PROPOSAL_COMMENTS);
			List<ProposalFilterBean> loProposalCommentsList = (List<ProposalFilterBean>) loChannel
					.getData(HHSConstants.PROPOSAL_COMMENT_LIST);
			aoResourceRequest.setAttribute(HHSConstants.PROPOSAL_COMMENT_LIST, loProposalCommentsList);
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error occurred file fetching proposal comments", aoExp);
			setGenericErrorMessage(aoResourceRequest);
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error occurred file fetching proposal comments", aoExp);
			setGenericErrorMessage(aoResourceRequest);
		}
		return HHSConstants.SHOW_PROPOSAL_COMMENT;
	}

	/**
	 * The method will open the overlay content for cancel proposal
	 * 
	 * <ul>
	 * <li>1. Fetch proposal Id and procurement Id from the request object</li>
	 * <li>2. Set the fetched Id in ResourceRequest</li>
	 * <li>3. Return "cancelProposal" ModelAndView object</li>
	 * </ul>
	 * 
	 * @param aoResourceRequest - ResourceRequest object
	 * @param aoResourceResponse - ResourceResponse object
	 * @return ModelAndView content
	 */
	@ResourceMapping("launchProposalCancelOverlay")
	protected ModelAndView launchProposalCancelOverlay(ResourceRequest aoResourceRequest,
			ResourceResponse aoResourceResponse)
	{
		try
		{
			String lsProcurementId = aoResourceRequest.getParameter(HHSConstants.PROCUREMENT_ID);
			String lsProposalId = aoResourceRequest.getParameter(HHSConstants.PROPOSAL_ID);
			aoResourceRequest.setAttribute(HHSConstants.PROPOSAL_ID, lsProposalId);
			aoResourceRequest.setAttribute(HHSConstants.PROCUREMENT_ID, lsProcurementId);
		}
		// Handling Exception other than ApplicationException
		catch (Exception aoExp)
		{
			setGenericErrorMessage(aoResourceRequest);
			LOG_OBJECT.Error("Error occured while cancelling proposal", aoExp);
		}
		return new ModelAndView(HHSConstants.CANCEL_PROPOSAL);
	}

	/**
	 * The method will open the overlay content for retract proposal
	 * 
	 * <ul>
	 * <li>1. Fetch proposal Id and procurement Id from the request object</li>
	 * <li>2. Set the fetched Id in ResourceRequest</li>
	 * <li>3. Return "retractProposal" ModelAndView object</li>
	 * </ul>
	 * 
	 * @param aoResourceRequest - ResourceRequest object
	 * @param aoResourceResponse - ResourceResponse object
	 * @return ModelAndView content
	 */
	@ResourceMapping("launchProposalRetractOverlay")
	protected ModelAndView launchProposalRetractOverlay(ResourceRequest aoResourceRequest,
			ResourceResponse aoResourceResponse)
	{
		try
		{
			String lsProcurementId = aoResourceRequest.getParameter(HHSConstants.PROCUREMENT_ID);
			String lsProposalId = aoResourceRequest.getParameter(HHSConstants.PROPOSAL_ID);
			aoResourceRequest.setAttribute(HHSConstants.PROPOSAL_ID, lsProposalId);
			aoResourceRequest.setAttribute(HHSConstants.PROCUREMENT_ID, lsProcurementId);
		}
		// Handling Exception other than ApplicationException
		catch (Exception aoExp)
		{
			setExceptionMessageInResponse(aoResourceRequest);
			LOG_OBJECT.Error("Error occured while retracting proposal", aoExp);
		}
		return new ModelAndView(HHSConstants.PROPOSAL_RETRACT);
	}

	/**
	 * This method handles the operation of retracting a proposal
	 * <ul>
	 * <li>1. Fetch proposal id corresponding to the proposal</li>
	 * <li>2. Fetch next_action value</li>
	 * <li>3. If next_action value is doNotRetractProposal or xIcon then
	 * redirect to proposal summary</li>
	 * <li>4. Else if next_action value is retractProposal then call transaction
	 * to update the proposal status corresponding to the proposal Id</li>
	 * <li>5. redirect to proposal summary</li>
	 * <li>Execute Transation retractProposal</li>
	 * </ul>
	 * 
	 * @param aoRequest - a ActionRequest object
	 * @param aoResponse - a ActionResponse object
	 */
	@ActionMapping(params = "submit_action=retractProposal")
	public void retractProposal(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		// get the required value from the request
		String lsProcurementId = aoRequest.getParameter(HHSConstants.PROCUREMENT_ID);
		String lsProposalId = aoRequest.getParameter(HHSConstants.PROPOSAL_ID);
		final Map<String, Object> loHmReqExceProp = new HashMap<String, Object>();
		loHmReqExceProp.put(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
		loHmReqExceProp.put(HHSConstants.PROPOSAL_ID_KEY, lsProposalId);
		PortletSession loSession = aoRequest.getPortletSession();
		try
		{
			// set the required parameters into the session
			Channel loChannelObj = new Channel();
			loChannelObj.setData(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
			loChannelObj.setData(HHSConstants.PROPOSAL_ID_KEY, lsProposalId);
			loChannelObj
					.setData(HHSConstants.AS_ORGANIZATION_ID, loSession.getAttribute(
							ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE));
			// execute the transaction for the retract proposal
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.PROPOSAL_RETRACT);
			aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			Boolean loTransOutput = (Boolean) loChannelObj.getData(HHSConstants.STATUS_FLAG);
			String lsProposalTitle = (String) loChannelObj.getData(HHSConstants.PROPOSAL_TITLE);
			// check the output of the transaction if block for to display the
			// error message
			if (loTransOutput && lsProposalTitle != null)
			{
				// get the message from the property file
				String lsRetractMessage = MessageFormat.format(PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.PROPOSAL_RETRACT_SUCCESSFULLY),
						lsProposalTitle);
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, lsRetractMessage);
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_PASS_TYPE);
			}
			else
			{
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.RETRACT_ERROR));
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE);
			}
			// set the required parameters into the response
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.PROPOSAL_SUMMARY_LOWERCASE);
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
			aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			aoResponse.setRenderParameter(HHSConstants.PROPOSAL_ID, lsProposalId);
			aoResponse.setRenderParameter(HHSConstants.TOP_LEVEL_FROM_REQ, HHSConstants.PROPOSAL_SUMMARY);
			setNavigationParamsInRender(aoRequest, aoResponse);
		}
		// handling exception while canceling proposal
		catch (ApplicationException aoException)
		{
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loHmReqExceProp, aoException);
		}
		// Handling Exception other than ApplicationException
		catch (Exception aoException)
		{
			LOG_OBJECT.Error("Exception Occured while retract the proposal", aoException);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
	}

	/**
	 * This method handles the operation of canceling a proposal
	 * <ul>
	 * <li>1. Fetch proposal id corresponding to the proposal</li>
	 * <li>2. Fetch next_action value</li>
	 * <li>3. If next_action value is doNotCancelProposal or xIcon then redirect
	 * to proposal summary</li>
	 * <li>4. Else if next_action value is cancelProposal then call transaction
	 * to delete the proposal details corresponding to the proposal Id</li>
	 * <li>5. redirect to proposal summary</li>
	 * </ul>
	 * 
	 * @param aoRequest - a ActionRequest object
	 * @param aoResponse - a ActionResponse object
	 */
	@ActionMapping(params = "submit_action=cancelProposal")
	public void cancelProposal(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		// get the required value from the request
		String lsProcurementId = aoRequest.getParameter(HHSConstants.PROCUREMENT_ID);
		String lsProposalId = aoRequest.getParameter(HHSConstants.PROPOSAL_ID);
		final Map<String, Object> loHmReqExceProp = new HashMap<String, Object>();
		loHmReqExceProp.put(HHSConstants.PROPOSAL_ID_KEY, lsProposalId);
		try
		{
			Channel loChannelObj = new Channel();
			PortletSession loSession = aoRequest.getPortletSession();
			// find the file net session for connection
			P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			HashMap<String, Boolean> loUpdateProperties = new HashMap<String, Boolean>();
			loUpdateProperties.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, false);
			loUpdateProperties.put(HHSConstants.IS_DOCUMENT_RFP_AWARD_TYPE, false);
			// set the value into the session
			loChannelObj.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
			loChannelObj.setData(HHSConstants.UPDATE_PROPERTY, loUpdateProperties);
			loChannelObj.setData(HHSConstants.PROPOSAL_ID_KEY, lsProposalId);
			loChannelObj.setData(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
			loChannelObj
					.setData(HHSConstants.AS_ORGANIZATION_ID, loSession.getAttribute(
							ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE));
			// call the transaction to cancel the proposal
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.CANCEL_PROPOSAL);
			Boolean loProposalStatus = (Boolean) loChannelObj.getData(HHSConstants.PROPOSAL_STATUS_FLAG);
			// checking if the fetched "proposalStatus" is true
			if (loProposalStatus)
			{
				Boolean loTransOutput = (Boolean) loChannelObj.getData(HHSConstants.STATUS_FLAG);
				String lsProposalTitle = (String) loChannelObj.getData(HHSConstants.PROPOSAL_TITLE);
				// checking if the value of "loTransOutput" is true and fetched
				// proposal title is not null
				if (loTransOutput && lsProposalTitle != null)
				{
					// read the message from the property file
					String lsCancelMessage = MessageFormat.format(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.PROPOSAL_CANCEL_SUCCESSFULLY),
							lsProposalTitle);
					aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, lsCancelMessage);
					aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
							ApplicationConstants.MESSAGE_PASS_TYPE);
				}
				else
				{
					aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE,
							HHSConstants.PROPOSAL_CANCEL_FAILED);
					aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
							ApplicationConstants.MESSAGE_FAIL_TYPE);
				}
			}
			else
			{
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.PROPOSAL_CANCEL_FAILED));
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE);
			}
			// set the required values in to the response
			aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			aoResponse.setRenderParameter(HHSConstants.PROPOSAL_ID, lsProposalId);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.PROPOSAL_SUMMARY_LOWERCASE);
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
			aoResponse.setRenderParameter(HHSConstants.TOP_LEVEL_FROM_REQ, HHSConstants.PROPOSAL_SUMMARY);
			setNavigationParamsInRender(aoRequest, aoResponse);
		}
		// handling exception while cancelling proposal
		catch (ApplicationException aoException)
		{
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loHmReqExceProp, aoException);
		}
		// Handling Exception other than ApplicationException
		catch (Exception aoException)
		{
			LOG_OBJECT.Error("Exception Occured while cancel the proposal", aoException);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
	}

	/**
	 * This method will be executed when the Evaluation Results and Selections
	 * screen will be loaded first time and then for rendering the actions
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Get EvaluationBean from Session and check for null value.</li>
	 * <li>Check whether userType is Agency Acco User or Agency Non Acco User
	 * and set the flag Accordingly</li>
	 * <li>If it is null, set default sorting parameters by creating new object.
	 * </li>
	 * <li>Set the filtered check if its in the session.</li>
	 * <li>Call getPageHeader() method for navigation</li>
	 * <li>Call getEvaluationResultFromDB() method to get the data from database
	 * </li>
	 * <li>Call getEvaluationResultFromDB() method to get the data from database
	 * </li>
	 * </ul>
	 * 
	 * @param aoRequest - a ResourceRequest object
	 * @param aoResponse - a ResourceResponse object
	 * @param asEvalPoolMappingId - Evaluation Pool Mapping Id
	 * @return - ModelAndView object
	 * @throws ApplicationException
	 */
	private ModelAndView fetchEvaluationResultsSelections(RenderRequest aoRequest, RenderResponse aoResponse,
			String asEvalPoolMappingId) throws ApplicationException
	{
		EvaluationFilterBean loEvaluationBean = null;
		try
		{// Getting EvaluationBean from Session in case of Sorting
			loEvaluationBean = setEvaluationFilterBean(aoRequest, asEvalPoolMappingId);
			if (ApplicationSession.getAttribute(aoRequest, true, ApplicationConstants.ERROR_MESSAGE_TYPE) != null
					&& ((String) ApplicationSession.getAttribute(aoRequest, ApplicationConstants.ERROR_MESSAGE_TYPE))
							.equalsIgnoreCase(ApplicationConstants.MESSAGE_FAIL_TYPE))
			{
				String lsErrorMsg = (String) ApplicationSession.getAttribute(aoRequest,
						ApplicationConstants.ERROR_MESSAGE);
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
				aoRequest.setAttribute(HHSConstants.CLOSE_SUBMITTION, null);
			}// If we are navigating from Cancel Award Screen
			else if (aoRequest.getParameter(HHSConstants.PARAM_VALUE) != null
					&& aoRequest.getParameter(HHSConstants.PARAM_VALUE).equalsIgnoreCase(
							HHSConstants.FINALIZE_UPDATE_RESULTS))
			{
				String lsCancelMessage = MessageFormat.format(PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.FINALIZE_UPDATE_RESULTS), aoRequest
						.getParameter(HHSConstants.ORGANIZATION_LEGAL_NAME));
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsCancelMessage);
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_PASS_TYPE);
			}

			String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			String lsUserRole = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ROLE, PortletSession.APPLICATION_SCOPE);
			Channel loChannel = new Channel();
			loChannel.setData(HHSConstants.ORG_TYPE, lsUserOrgType);
			loChannel.setData(HHSConstants.USER_ROLE, lsUserRole);
			aoRequest.setAttribute(HHSConstants.CHANNEL_ACCESS, loChannel);
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error occured while fetching Evaluation Results and Selections data", aoExp);
			throw aoExp;
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error occured while fetching Evaluation Results and Selections data", aoExp);
			throw new ApplicationException("Error occured while fetching Evaluation Results and Selections data", aoExp);
		}
		return new ModelAndView(HHSConstants.EVALUATION_RESULT_JSP, HHSConstants.EVALUATION_FILTER_BEAN,
				loEvaluationBean);
	}

	/**
	 * This method is used to set EvaluationFilterBean for fetching evaluation
	 * results and selections
	 * @param aoRequest RenderRequest
	 * @param asEvalPoolMappingId eval pool mapping Id
	 * @return EvaluationFilterBean object
	 * @throws ApplicationException If an ApplicationException Occurs
	 */
	private EvaluationFilterBean setEvaluationFilterBean(RenderRequest aoRequest, String asEvalPoolMappingId)
			throws ApplicationException
	{
		EvaluationFilterBean loEvaluationBean;
		loEvaluationBean = (EvaluationFilterBean) aoRequest.getPortletSession().getAttribute(
				HHSConstants.EVALUATION_SESSION_FILTER_BEAN, PortletSession.PORTLET_SCOPE);

		if (null == loEvaluationBean)
		{
			loEvaluationBean = new EvaluationFilterBean();
			loEvaluationBean.setFirstSort(HHSConstants.EVALUATION_SCORE_COL);
			loEvaluationBean.setSecondSort(HHSConstants.ORGANIZATION_NAME);
			loEvaluationBean.setFirstSortType(HHSConstants.DESCENDING);
			loEvaluationBean.setSecondSortType(HHSConstants.ASCENDING);
			loEvaluationBean.setSortColumnName(HHSConstants.EVALUATION_SCORE_STR);
			loEvaluationBean.setFirstSortDate(false);
			loEvaluationBean.setSecondSortDate(false);
			loEvaluationBean.setEvaluationPoolMappingId(asEvalPoolMappingId);
		}
		if (aoRequest.getPortletSession().getAttribute(HHSConstants.PROPOSAL_FILTERED_RESULT,
				PortletSession.PORTLET_SCOPE) != null
				&& loEvaluationBean.getProposalStatusIdList() == null)
		{
			loEvaluationBean.setFilteredCheck(true);
		}
		else
		{
			loEvaluationBean.setFilteredCheck(false);
		}
		String lsProcurementId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
		loEvaluationBean.setProcurementId(lsProcurementId);
		getEvaluationResultFromDB(aoRequest, loEvaluationBean, lsProcurementId);
		if (aoRequest.getParameter(HHSConstants.AWARD_AMT_FROM_HIDDEN) != null)
		{
			loEvaluationBean.setAwardAmountFrom(null);
		}
		if (aoRequest.getParameter(HHSConstants.AWARD_AMT_TO_HIDDEN) != null)
		{
			loEvaluationBean.setAwardAmountTo(null);
		}
		if (aoRequest.getParameter(HHSConstants.SCORE_FROM_HIDDEN) != null)
		{
			loEvaluationBean.setScoreRangeFrom(null);
		}
		if (aoRequest.getParameter(HHSConstants.SCORE_TO_HIDDEN) != null)
		{
			loEvaluationBean.setScoreRangeTo(null);
		}
		return loEvaluationBean;
	}

	/**
	 * This method will be executed when the Evaluation Results and Selections
	 * screen will be loaded first time and then for rendering the actions
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Create new channel object.</li>
	 * <li>Set the user(agency or acco) in the bean</li>
	 * <li>If the user(agency or acco) is invalid, set a flag in the bean</li>
	 * <li>Create new PortletSession object.</li>
	 * <li>get user role from PortletSession object.</li>
	 * <li>Call getPagingParams() method to set paging parameters</li>
	 * <li>Set the required datra in the channel object.</li>
	 * <li>Execute transaction "fetchEvaluationResultsSelections" to get the
	 * data from database</li>
	 * <li>Set the result in Render request i.e need to display on UI i.e Award
	 * Review Status,Eval Result count and Visibilty Status of Button</li>
	 * <li>Set Evaluation Results list in request object.</li>
	 * </ul>
	 * 
	 * @param aoRequest - RenderRequest
	 * @param aoEvalBean - EvaluationFilterBean
	 * @param asProcurementId - Procurement Id
	 * @throws ApplicationException if any exception occurred
	 */
	@SuppressWarnings("unchecked")
	private void getEvaluationResultFromDB(RenderRequest aoRequest, EvaluationFilterBean aoEvalBean,
			String asProcurementId) throws ApplicationException
	{
		Channel loChannel = new Channel();
		try
		{
			String lsEvaluationGroupId = PortalUtil.parseQueryString(aoRequest, HHSConstants.EVALUATION_GROUP_ID);
			String lsEvaluationPoolMappingId = PortalUtil.parseQueryString(aoRequest,
					HHSConstants.EVALUATION_POOL_MAPPING_ID);
			if (HHSUtil.isAgencyAccoUser(aoRequest) || HHSUtil.isAcceleratorUser(aoRequest))
			{
				aoEvalBean.setIsAccoUser(HHSConstants.YES_UPPERCASE);
			}
			else if (HHSUtil.isAgencyNonAccoUser(aoRequest))
			{
				aoEvalBean.setIsAgencyAccoUser(HHSConstants.YES_UPPERCASE);
			}
			else
			{
				aoEvalBean.setIsValidUser(Boolean.FALSE);
			}
			PortletSession loSession = aoRequest.getPortletSession();
			String lsUserRole = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ROLE,
					PortletSession.APPLICATION_SCOPE);
			String lsOrgType = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
					PortletSession.APPLICATION_SCOPE);
			aoEvalBean.setUserRole(lsUserRole);
			aoEvalBean.setOrgType(lsOrgType);
			aoEvalBean.setEvaluationPoolMappingId(lsEvaluationPoolMappingId);
			String lsNextPage = aoRequest.getParameter(HHSConstants.NEXT_PAGE_PARAM);
			getPagingParams(loSession, aoEvalBean, lsNextPage, HHSConstants.EVALUATION_LIST);
			loChannel.setData(HHSConstants.EVALUATION_BEAN_LOWERCASE, aoEvalBean);
			loChannel.setData(HHSConstants.PROCUREMENT_ID, asProcurementId);
			loChannel.setData(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvaluationPoolMappingId);
			loChannel.setData(HHSConstants.EVALUATION_GROUP_ID, lsEvaluationGroupId);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_EVAL_RESULTS_SEL);
			Integer loEvaluationResultsCount = setRequestForEvalResultsFetching(aoRequest, loChannel, loSession,
					lsUserRole, lsOrgType);
			loSession.setAttribute(ApplicationConstants.ALERT_VIEW_PAGING_RECORDS,
					((loEvaluationResultsCount == null) ? 0 : loEvaluationResultsCount),
					PortletSession.APPLICATION_SCOPE);
			loSession.setAttribute(HHSConstants.SORT_TYPE, aoEvalBean.getFirstSortType(),
					PortletSession.APPLICATION_SCOPE);
			loSession.setAttribute(HHSConstants.SORT_BY, aoEvalBean.getSortColumnName(),
					PortletSession.APPLICATION_SCOPE);
		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Error was occurred while getting the result from the database", aoAppExp);
			throw aoAppExp;
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error was occurred while getting the result from the database", aoExp);
			throw new ApplicationException("Error was occurred while getting the result from the database", aoExp);
		}
	}

	/**
	 * This method sets request for Evaluation Results fetching
	 * @param aoRequest RenderRequest
	 * @param loChannel Channel Object
	 * @param loSession Prtlet Session
	 * @param lsUserRole user role
	 * @param lsOrgType organization type
	 * @return evaluationResulsCount Integer
	 * @throws ApplicationException
	 */
	private Integer setRequestForEvalResultsFetching(RenderRequest aoRequest, Channel loChannel,
			PortletSession loSession, String lsUserRole, String lsOrgType) throws ApplicationException
	{
		aoRequest.setAttribute(HHSConstants.COMPETITION_POOL_LIST,
				loChannel.getData(HHSConstants.COMPETITION_POOL_LIST));
		// Change for Defect 8363 starts
		aoRequest.setAttribute(HHSConstants.CONTRACT_TYPE_ID,
				loChannel.getData(HHSConstants.CONTRACT_TYPE_ID));
		// Change for Defect 8363 ends
		aoRequest.setAttribute(HHSConstants.EVALUATION_GROUP_ID, loChannel.getData(HHSConstants.EVALUATION_GROUP_ID));
		aoRequest.setAttribute(HHSConstants.GROUP_TITLE_MAP, loChannel.getData(HHSConstants.GROUP_TITLE_MAP));
		// procurement status check
		Integer loProcStatus = (Integer) loChannel.getData(HHSConstants.PROCUREMENT_STATUS);
		if (loProcStatus != null
				&& (loProcStatus.toString().equals(
						PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_PROCUREMENT_CLOSED)) || loProcStatus.toString().equals(
						PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_PROCUREMENT_CANCELLED))))
		{
			aoRequest.setAttribute(HHSConstants.IS_PROC_CLOSED_CANCELLED, true);
		}
		else
		{
			aoRequest.setAttribute(HHSConstants.IS_PROC_CLOSED_CANCELLED, false);
		}
		aoRequest.setAttribute(HHSConstants.FILTERED, aoRequest.getParameter(HHSConstants.FILTERED));
		List<EvaluationBean> loEvaluationList = (List<EvaluationBean>) loChannel
				.getData(HHSConstants.EVALUATION_RESULTS_LIST);
		Integer loEvaluationResultsCount = (Integer) loChannel.getData(HHSConstants.EVAL_RES_COUNT);
		EvaluationBean loAwardReviewStatus = (EvaluationBean) loChannel.getData(HHSConstants.AWARD_REVIEW_STATUS);
		Map<String, Boolean> loFinalizeVisibiltyStatus = (Map<String, Boolean>) loChannel
				.getData(HHSConstants.FINALIZE_VISIBILTY_STATUS);
		String lsUpdateVisibiltyStatus = (String) loChannel.getData(HHSConstants.UPDATE_VISIBILTY_STATUS);
		// Start || Changes done for Enhancement #6574 for Release 3.10.0
		String lsUpdateafterApprovalStatus = (String) loChannel.getData(HHSConstants.UPDATE_AFTER_APPROVAL);
		aoRequest.setAttribute(HHSConstants.UPDATE_AFTER_APPROVAL, lsUpdateafterApprovalStatus);
		// End || Changes done for Enhancement #6574 for Release 3.10.0
		aoRequest.setAttribute(HHSConstants.PROPOSAL_FILTERED_RESULT,
				loSession.getAttribute(HHSConstants.PROPOSAL_FILTERED_RESULT, PortletSession.PORTLET_SCOPE));
		Map<String, Boolean> loDisplayStarsMap = HHSUtil.getStarDoubleStarStatus(loEvaluationList);
		aoRequest.setAttribute(HHSConstants.SELECTED_PROPOSAL_MESSAGE,
				loDisplayStarsMap.get(HHSConstants.SELECTED_PROPOSAL_MESSAGE));
		aoRequest.setAttribute(HHSConstants.SELECTED_PROPOSAL_MESSAGE_KEY, HHSConstants.SELECTED_PROPOSAL_MESSAGE_INFO);
		aoRequest.setAttribute(HHSConstants.SHOW_STAR, loDisplayStarsMap.get(HHSConstants.STAR));
		aoRequest.setAttribute(HHSConstants.SHOW_DOUBLE_STAR, loDisplayStarsMap.get(HHSConstants.DOUBLE_STAR));
		aoRequest.setAttribute(HHSConstants.FINALIZE_BUTTON_ACTIVE,
				loFinalizeVisibiltyStatus.get(HHSConstants.FINALIZE_BUTTON_ACTIVE));
		aoRequest.setAttribute(HHSConstants.SHOW_FINALIZE_BUTTON,
				loFinalizeVisibiltyStatus.get(HHSConstants.SHOW_FINALIZE_BUTTON));
		aoRequest.setAttribute(HHSConstants.UPDATE_VISIBILTY_STATUS, lsUpdateVisibiltyStatus);
		aoRequest.setAttribute(HHSConstants.USER_ROLE, lsUserRole);
		if (loAwardReviewStatus != null)
		{
			aoRequest.setAttribute(HHSConstants.AWARD_ID, loAwardReviewStatus.getAwardId());
		}
		if (null != loEvaluationList)
		{
			for (EvaluationBean loEvaluationBean : loEvaluationList)
			{
				if (loAwardReviewStatus != null)
				{
					loEvaluationBean.setAwardReviewStatus(loAwardReviewStatus.getAwardReviewStatus());
				}
				loEvaluationBean.setProcurementStatus(loProcStatus.toString());
				loEvaluationBean.setUserOrgType(lsOrgType);
				loEvaluationBean.setUserRole(lsUserRole);
			}
		}
		aoRequest.setAttribute(HHSConstants.EVAL_RESULT_LIST, loEvaluationList);
		aoRequest.setAttribute(HHSConstants.AWARD_REVIEW_STATUS, loAwardReviewStatus);
		return loEvaluationResultsCount;
	}

	/***
	 * This method will handle sort evaluation action from Evaluation Results
	 * and Selections
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1.Setting Navigation parameters</li>
	 * <li>2.Get sortType and columnName values by calling parseQueryString()
	 * method from class PortalUtil. Get sorting details by calling method
	 * getSortDetailsFromXML() from class BaseController.</li>
	 * <li>3.Setting Evaluation Bean and other required parameter for successful
	 * rendering of fetchEvaluationResultsSelections Method</li>
	 * <li></li>
	 * <ul>
	 * @param aoEvaluationBean EvaluationBean
	 * @param aoRequest ActionRequest
	 * @param aoResponse ActionResponse
	 */
	@ActionMapping(params = "submit_action=sortEvaluationResults")
	protected void actionSortEvaluationResults(
			@ModelAttribute("EvaluationFilterBean") EvaluationFilterBean aoEvaluationBean, ActionRequest aoRequest,
			ActionResponse aoResponse)
	{
		Map<String, String> loHmReqExceProp = new HashMap<String, String>();
		try
		{
			LOG_OBJECT.Debug("Entered into Evaluation Status Sort Action::");
			setNavigationParamsInRender(aoRequest, aoResponse);
			String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			String lsSortType = PortalUtil.parseQueryString(aoRequest, HHSConstants.SORT_TYPE);
			String lsColumnName = PortalUtil.parseQueryString(aoRequest, HHSConstants.COLUMN_NAME);
			getSortDetailsFromXML(lsColumnName, lsUserOrgType,
					PortalUtil.parseQueryString(aoRequest, HHSConstants.SORT_GRID_NAME), aoEvaluationBean, lsSortType);
			// Setting param for Rendering getEvaluationStatus Render Method
			aoRequest.getPortletSession().setAttribute(HHSConstants.EVALUATION_SESSION_FILTER_BEAN, aoEvaluationBean,
					PortletSession.PORTLET_SCOPE);
			aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID,
					aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			aoResponse.setRenderParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID,
					aoRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID));
			aoResponse.setRenderParameter(HHSConstants.EVALUATION_GROUP_ID,
					aoRequest.getParameter(HHSConstants.EVALUATION_GROUP_ID));
			aoResponse.setRenderParameter(HHSConstants.COMPETITION_POOL_ID,
					aoRequest.getParameter(HHSConstants.COMPETITION_POOL_ID));
			loHmReqExceProp.put(HHSConstants.PROCUREMENT_ID, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.FETCH_EVAL_RESUTS);
		}
		// handling Application Exception while sorting evaluation result.
		catch (ApplicationException aoAppExp)
		{
			String lsErrorMsg = HHSConstants.ERROR_WHILE_PROCESSING_REQUEST;
			LOG_OBJECT.Error("Exception Occured while sorting Evaluation Status: ", aoAppExp);
			setExceptionMessageFromAction(aoResponse, lsErrorMsg, ApplicationConstants.MESSAGE_FAIL_TYPE,
					loHmReqExceProp, aoAppExp);
		}
		// Handling Exception other than ApplicationException
		catch (Exception aoExp)
		{
			String lsErrorMsg = HHSConstants.ERROR_WHILE_PROCESSING_REQUEST;
			LOG_OBJECT.Error("Exception Occured while sorting Evaluation Status: ", aoExp);
			setExceptionMessageFromAction(aoResponse, lsErrorMsg, ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
	}

	/**
	 * This method will handle the resource request from Evaluation Results &
	 * Selections screen when "Request Score Amendment" option is selected.and
	 * return model and view object containing view name which will render to
	 * screen S262.
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. Fetch proposal Id from the request</li>
	 * <li>2. Create a channel object and populate it with proposal Id</li>
	 * <li>3. Execute transaction <b>requestScoreAmendment</b> to request score
	 * amendments</li>
	 * <li>4. If the fetched amendment flag is true then redirect the user to
	 * S218 : Evaluation Results & Settings</li>
	 * </ul>
	 * 
	 * @param aoRequest - a ActionRequest object
	 * @param aoResponse - a ActionResponse object
	 */
	@ActionMapping(params = "submit_action=requestScoreAmendment")
	public void requestScoreAmendmentEvalResults(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		String lsProposalId = aoRequest.getParameter(HHSConstants.PROPOSAL_ID);
		String lsProcurementId = aoRequest.getParameter(HHSConstants.PROCUREMENT_ID);
		try
		{
			LOG_OBJECT.Debug("Entered into executing request score amendment");
			// checking if proposal Id is not null
			if (lsProposalId != null)
			{
				Channel loChannelObj = new Channel();
				List<String> loAlertList = new ArrayList<String>();
				P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
						ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
				loChannelObj.setData(HHSConstants.PROPOSAL_ID_KEY, lsProposalId);
				loChannelObj.setData(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
				loChannelObj.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
				loAlertList.add(HHSConstants.NT220);
				loChannelObj.setData(HHSConstants.LO_HM_NOTIFY_PARAM,
						getNotificationMapForReturnedForRevision(aoRequest, loAlertList, Boolean.TRUE));
				HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.REQUEST_SCORE_AMENDMENT);
				Boolean loAmendmentFlag = (Boolean) loChannelObj.getData(HHSConstants.REQUEST_SCORE_AMENDMENT_FLAG);
				// checking whether or not the value of amendment flag is true
				if (loAmendmentFlag == true)
				{
					aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID,
							aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
					aoResponse.setRenderParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID,
							aoRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID));
					aoResponse.setRenderParameter(HHSConstants.EVALUATION_GROUP_ID,
							aoRequest.getParameter(HHSConstants.EVALUATION_GROUP_ID));
					aoResponse.setRenderParameter(HHSConstants.COMPETITION_POOL_ID,
							aoRequest.getParameter(HHSConstants.COMPETITION_POOL_ID));
					aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
					aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.FETCH_EVAL_RESUTS);
					aoResponse
							.setRenderParameter(HHSConstants.TOP_LEVEL_FROM_REQ, HHSConstants.PROPOSAL_AND_EVALUATION);
					aoResponse.setRenderParameter(HHSConstants.MID_LEVEL_FROM_REQ,
							HHSConstants.EVALUATION_RESULT_AND_SELECTION);
				}
			}
		}
		// Handling Exception while requesting score amendments
		catch (ApplicationException aoAppExp)
		{
			Map<String, String> loParamMap = new HashMap<String, String>();
			loParamMap.put(HHSConstants.PROPOSAL_ID, lsProposalId);
			loParamMap.put(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap, aoAppExp);
			LOG_OBJECT.Error("Error while executing request score amendments", aoAppExp);
		}
		// Handling Exception other than ApplicationException
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while executing request score amendments", aoExp);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}

	}

	/**
	 * This method will handle the resource request from Evaluation Results &
	 * Selections screen when "Mark Selected" option is selected.and return
	 * model and view object containing view name which will render to screen
	 * S223.
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. Create channel object and set proposal Id in it</li>
	 * <li>2. If the proposal Id is not null then call
	 * <b>fetchReqProposalDetails</b> transaction to retrieve proposal details</li>
	 * <li>3. Retrieve fetched list and set the retrieved list in
	 * "confirmselectedprovider" screen</li>
	 * <li>4. Return ModelAndView object</li>
	 * </ul>
	 * 
	 * @param aoResourceRequestForSelected - a ResourceRequest object
	 * @param aoResourceResponseForNotSelected - a ResourceResponse object
	 * @return - a ModelAndView object containing the jsp name
	 */
	@ResourceMapping("markSelected")
	protected ModelAndView markSelectedEvalResults(ResourceRequest aoResourceRequestForSelected,
			ResourceResponse aoResourceResponseForNotSelected)
	{
		try
		{
			String lsProposalId = aoResourceRequestForSelected.getParameter(HHSConstants.PROPOSAL_ID);
			// checking if proposal Id is not null
			if (lsProposalId != null)
			{
				Channel loChannelObjForSelected = new Channel();
				loChannelObjForSelected.setData(HHSConstants.PROPOSAL_ID_KEY, lsProposalId);
				HHSTransactionManager.executeTransaction(loChannelObjForSelected,
						HHSConstants.FETCH_REQ_PROPOSALS_DETAILS);
				EvaluationBean loEvaluationBean = (EvaluationBean) loChannelObjForSelected
						.getData(HHSConstants.LO_PROPOSAL_DETAILS);
				if (loEvaluationBean != null)
				{
					aoResourceRequestForSelected.setAttribute(HHSConstants.PROPOSAL_DETAILS_MAP, loEvaluationBean);
					aoResourceRequestForSelected.setAttribute(HHSConstants.PROPOSAL_ID, lsProposalId);
					aoResourceRequestForSelected.setAttribute(HHSConstants.PROCUREMENT_ID,
							aoResourceRequestForSelected.getParameter(HHSConstants.PROCUREMENT_ID));
					aoResourceRequestForSelected.setAttribute(HHSConstants.EVALUATION_POOL_MAPPING_ID,
							aoResourceRequestForSelected.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID));
					aoResourceRequestForSelected.setAttribute(HHSConstants.EVALUATION_GROUP_ID,
							aoResourceRequestForSelected.getParameter(HHSConstants.EVALUATION_GROUP_ID));
					aoResourceRequestForSelected.setAttribute(HHSConstants.COMPETITION_POOL_ID,
							aoResourceRequestForSelected.getParameter(HHSConstants.COMPETITION_POOL_ID));
				}
			}
		}
		// Handling Exception while rendering confirm selected provider
		catch (ApplicationException aoAppExp)
		{
			setGenericErrorMessage(aoResourceRequestForSelected);
			LOG_OBJECT.Error("Error occured while marking Proposal selected", aoAppExp);
		}
		// Handling Exception other than ApplicationException
		catch (Exception aoExp)
		{
			setGenericErrorMessage(aoResourceRequestForSelected);
			LOG_OBJECT.Error("Error occured while marking proposal selected", aoExp);
		}
		return new ModelAndView(HHSConstants.CONFIRM_SELECTED_PROVIDER);
	}

	/**
	 * This method will handle the resource request from Evaluation Results &
	 * Selections screen when "Mark Not Selected" option is selected.and return
	 * model and view object containing view name which will render to screen
	 * S224.
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. Create channel object and set proposal Id in it</li>
	 * <li>2. Call <b>fetchReqProposalDetails</b> transaction to retrieve
	 * proposal details</li>
	 * <li>3. Retrieve fetched list and set the retrieved list in
	 * "confirmselectedproviderstatus" screen</li>
	 * <li>4. Return ModelAndView object</li>
	 * </ul>
	 * 
	 * @param aoResourceRequestForNotSelected - a ResourceRequest object
	 * @param aoResourceResponseForNotSelected - a ResourceResponse object
	 * @return - a ModelAndView object containing the JSP name
	 */
	@ResourceMapping("markNotSelected")
	protected ModelAndView markNotSelectedEvalResults(ResourceRequest aoResourceRequestForNotSelected,
			ResourceResponse aoResourceResponseForNotSelected)
	{
		try
		{
			String lsProposalId = aoResourceRequestForNotSelected.getParameter(HHSConstants.PROPOSAL_ID);
			// checking if proposal Id is not null
			if (lsProposalId != null)
			{
				Channel loChannelObjForNotSeleted = new Channel();
				loChannelObjForNotSeleted.setData(HHSConstants.PROPOSAL_ID_KEY, lsProposalId);
				HHSTransactionManager.executeTransaction(loChannelObjForNotSeleted,
						HHSConstants.FETCH_REQ_PROPOSALS_DETAILS);
				EvaluationBean loEvaluationBean = (EvaluationBean) loChannelObjForNotSeleted
						.getData(HHSConstants.LO_PROPOSAL_DETAILS);
				if (loEvaluationBean != null)
				{
					aoResourceRequestForNotSelected.setAttribute(HHSConstants.PROPOSAL_DETAILS_MAP, loEvaluationBean);
					aoResourceRequestForNotSelected.setAttribute(HHSConstants.PROPOSAL_ID, lsProposalId);
					aoResourceRequestForNotSelected.setAttribute(HHSConstants.PROCUREMENT_ID,
							aoResourceRequestForNotSelected.getParameter(HHSConstants.PROCUREMENT_ID));
					aoResourceRequestForNotSelected.setAttribute(HHSConstants.EVALUATION_POOL_MAPPING_ID,
							aoResourceRequestForNotSelected.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID));
					aoResourceRequestForNotSelected.setAttribute(HHSConstants.EVALUATION_GROUP_ID,
							aoResourceRequestForNotSelected.getParameter(HHSConstants.EVALUATION_GROUP_ID));
					aoResourceRequestForNotSelected.setAttribute(HHSConstants.COMPETITION_POOL_ID,
							aoResourceRequestForNotSelected.getParameter(HHSConstants.COMPETITION_POOL_ID));
				}
			}
		}
		// Handling Exception while rendering confirm not selected provider
		catch (ApplicationException aoAppExp)
		{
			setGenericErrorMessage(aoResourceRequestForNotSelected);
			LOG_OBJECT.Error("Error occured while not selecting a proposal", aoAppExp);
		}
		// Handling Exception other than ApplicationException
		catch (Exception aoExp)
		{
			setGenericErrorMessage(aoResourceRequestForNotSelected);
			LOG_OBJECT.Error("Error occured while not selecting a proposal", aoExp);
		}
		return new ModelAndView(HHSConstants.CONFIRM_NOT_SELECTED_PROVIDER);
	}

	/**
	 * The method will show the award review comment on click on "Show Comments"
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. Retrieve procurement Id</li>
	 * <li>2. If the retrieved procurement Id is not null then call transaction
	 * <b>fetchReviewAwardComments<b></li>
	 * <li>3. Retrieve Modified date from the output map and convert the date
	 * into "MM/dd/yyyy" format</li>
	 * <li>4. Populate model with retrieved comments and converted date</li>
	 * <li>5. Redirect the user to S259 : View Award Review Comments as an
	 * overlay</li>
	 * </ul>
	 * 
	 * @param aoResourceRequest - a ResourceRequest object
	 * @param aoResourceResponse - a ResourceResponse object
	 * @return ModelAndView - representing evaluation/awardReviewComments.jsp
	 * 
	 */
	@SuppressWarnings("unchecked")
	@ResourceMapping("viewAwardReviewComments")
	protected ModelAndView viewAwardReviewCommentsEvalResults(ResourceRequest aoResourceRequest,
			ResourceResponse aoResourceResponse)
	{
		String lsEvalPoolMappingId = HHSPortalUtil.parseQueryString(aoResourceRequest,
				HHSConstants.EVALUATION_POOL_MAPPING_ID);
		String lsProcurementId = aoResourceRequest.getParameter(HHSConstants.PROCUREMENT_ID);
		try
		{
			Channel loChannelObj = new Channel();
			loChannelObj.setData(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolMappingId);
			loChannelObj.setData(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.FETCH_REVIEW_AWARD_COMMENTS);
			Map<String, String> loViewAwardComment = (Map<String, String>) loChannelObj
					.getData(HHSConstants.LO_VIEW_AWARD_COMMENT);
			// checking whether or not the fetched map contains data
			if (loViewAwardComment != null)
			{
				aoResourceRequest.setAttribute(HHSConstants.AWARD_REVIEW_DATE,
						loViewAwardComment.get(HHSConstants.AWARD_MODIFIED_DATE));
				aoResourceRequest.setAttribute(HHSConstants.VIEW_AWARD_COMMENTS,
						loViewAwardComment.get(HHSConstants.USER_INTERNAL_COMMENT));
			}
		}
		// Handling Exception while rendering confirm not selected provider
		catch (ApplicationException aoAppExp)
		{
			setGenericErrorMessage(aoResourceRequest);
			LOG_OBJECT.Error("Error occured while fetching review award comments", aoAppExp);
		}
		// Handling Exception other than ApplicationException
		catch (Exception aoExp)
		{
			setGenericErrorMessage(aoResourceRequest);
			LOG_OBJECT.Error("Error occured while fetching review award comments", aoExp);
		}
		return new ModelAndView(HHSConstants.AWARD_REVIEW_COMMENTS);
	}

	/**
	 * This method will handle the resource request from Evaluation Results &
	 * Selections screen when "Finalize Results or Update Results" button is
	 * clicked and return model and view object containing view name which will
	 * open pop up for screen S226 - Finalize/Update Results.
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. Create channel object and set procurement Id in it</li>
	 * <li>2. Call <b>renderFinalizeProcurement</b> transaction to retrieve
	 * finalize procurement details</li>
	 * <li>3. Retrieve fetched list loAwardSelectionDetails</li>
	 * <li>4. Return finalizeupdateresults.jsp screen for S226 - Finalize/Update
	 * Results</li>
	 * </ul>
	 * 
	 * @param aoResourceRequest - a ResourceRequest object
	 * @param aoResourceResponse - a ResourceResponse object
	 * @return - a ModelAndView object containing the JSP name
	 *         finalizeupdateresults.jsp
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	@ResourceMapping("finalizeOrUpdateResults")
	protected ModelAndView finalizeOrUpdateResultsEvalResults(ResourceRequest aoResourceRequest,
			ResourceResponse aoResourceResponse)
	{
		Channel loChannel = new Channel();
		HashMap<String, Object> loHmReqExceProp = new HashMap<String, Object>();
		ModelAndView loModelAndView = null;
		try
		{
			Map aoHMWFRequiredProps = new HashMap();
			P8UserSession loUserSession = (P8UserSession) aoResourceRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			String lsProcurementId = aoResourceRequest.getParameter(HHSConstants.PROCUREMENT_ID);
			String lsEvalPoolMappingId = aoResourceRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID);
			aoResourceRequest.setAttribute(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			aoResourceRequest.setAttribute(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolMappingId);
			aoResourceRequest.setAttribute(HHSConstants.COMPETITION_POOL_ID,
					aoResourceRequest.getParameter(HHSConstants.COMPETITION_POOL_ID));
			aoResourceRequest.setAttribute(HHSConstants.EVALUATION_GROUP_ID,
					aoResourceRequest.getParameter(HHSConstants.EVALUATION_GROUP_ID));
			loHmReqExceProp.put(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
			loHmReqExceProp.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolMappingId);
			loChannel.setData(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
			loChannel.setData(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolMappingId);
			aoHMWFRequiredProps.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, lsProcurementId);
			getPageHeader(aoResourceRequest, lsProcurementId);
			ProcurementInfo loProcurementInfo = (ProcurementInfo) aoResourceRequest
					.getAttribute(HHSConstants.PROCUREMENT_BEAN);
			if (null != loProcurementInfo)
			{
				String lsIsRfpReleasedBefore = loProcurementInfo.getIsRfpReleasedBefore();
				if (null == lsIsRfpReleasedBefore)
				{
					aoHMWFRequiredProps.put(P8Constants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolMappingId);
				}
			}
			aoHMWFRequiredProps.put(HHSConstants.F_SUBJECT, P8Constants.PE_AWARD_WORK_FLOW_SUBJECT);
			loChannel.setData(HHSConstants.AO_HMWF_REQUIRED_PROPS, aoHMWFRequiredProps);
			loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.RENDER_FINALIZE_PROCUREMENT);
			String lsEstimatedProcurementValue = (String) loChannel.getData(HHSConstants.LS_PROCUREMENT_VALUE);
			Map<String, String> loAwardAmount = (Map<String, String>) loChannel.getData(HHSConstants.LS_AWARD_AMOUNT);
			Integer loCountFinalizeProcurementDetils = (Integer) loChannel
					.getData(HHSConstants.COUNT_FINALIZE_PROCUREMENT_DETAILS);
			aoResourceRequest.setAttribute(HHSConstants.LS_PROCUREMENT_VALUE, lsEstimatedProcurementValue);
			aoResourceRequest.setAttribute(HHSConstants.NEXT_ACTION,
					aoResourceRequest.getParameter(HHSConstants.NEXT_ACTION));
			aoResourceRequest.setAttribute(HHSConstants.LS_AWARD_AMOUNT, loAwardAmount.get(HHSConstants.AWARDAMT));
			aoResourceRequest.setAttribute(HHSConstants.NO_OF_AWARDS, loAwardAmount.get(HHSConstants.AWARDCOUNT));
			aoResourceRequest.setAttribute(HHSConstants.NO_OF_PROVIDERS_LOWERCASE, loCountFinalizeProcurementDetils);
			Boolean loFlag = (Boolean) loChannel.getData(HHSConstants.STATUS_FLAG);
			String lsNextAction = aoResourceRequest.getParameter(HHSConstants.NEXT_ACTION);
			if (lsNextAction.equals(HHSConstants.UPDATE_FINALIZE_PROCUREMENT) && !loFlag)
			{
				aoResourceRequest.setAttribute(HHSConstants.DISABLE_STATUS_FLAG, true);
				aoResourceRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
						HHSConstants.ERROR_WHILE_PROCESSING_REQUEST);
				aoResourceRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE);
			}
			loModelAndView = new ModelAndView(HHSConstants.FINALIZE_UPDATE_RESULTS);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppExp)
		{
			setGenericErrorMessage(aoResourceRequest);
			aoAppExp.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception Occured while displaying pop up for Finalize/Update Results screen", aoAppExp);
		}
		// handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			setGenericErrorMessage(aoResourceRequest);
			LOG_OBJECT.Error("Exception Occured while displaying pop up for Finalize/Update Results screen", aoExp);
		}
		return loModelAndView;
	}

	/**
	 * This method will trigger when user will select "View Comments" from the
	 * action dropdown on S218 Accelerator/Agency "Evaluation Results &
	 * Selections" and result in displaying screen 225 where user can see
	 * comments entered on S223 Accelerator/Agency "Mark Selected" and S224
	 * Accelerator/Agency "Mark Not Selected" option. "View Comments" is
	 * available in action dropdown only when comments are entered on one of
	 * those screens for the selected Procurement.
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. Retrieve Proposal id from the request parameter</li>
	 * <li>2. If the retrieved proposal Id is not null then set the retrieved
	 * proposal Id in the Channel object</li>
	 * <li>3. Execute transaction <b>fetchReqProposalDetails</b> corresponding
	 * to that proposal Id</li>
	 * <li>4. Retrieve the results and set them in the ResourceRequest object</li>
	 * <li>5. Redirect the user to the "viewSelectionComments" screen</li>
	 * </ul>
	 * 
	 * @param aoResourceRequest - a ResourceRequest object
	 * @param aoResourceResponse - a ResourceResponse object
	 * @return - a ModelAndView object returning the JSP
	 *         viewSelectionComments.jsp
	 */
	@ResourceMapping("viewSelectionComments")
	protected ModelAndView viewSelectionCommentsEvalResults(ResourceRequest aoResourceRequest,
			ResourceResponse aoResourceResponse)
	{
		try
		{
			String lsProposalId = aoResourceRequest.getParameter(HHSConstants.PROPOSAL_ID);
			// checking if proposal Id is not null
			if (lsProposalId != null)
			{
				Channel loChannelObj = new Channel();
				loChannelObj.setData(HHSConstants.PROPOSAL_ID_KEY, lsProposalId);
				HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.FETCH_REQ_PROPOSALS_DETAILS);
				EvaluationBean loEvaluationBean = (EvaluationBean) loChannelObj
						.getData(HHSConstants.LO_PROPOSAL_DETAILS);
				if (loEvaluationBean != null)
				{
					aoResourceRequest.setAttribute(HHSConstants.PROPOSAL_DETAILS_MAP, loEvaluationBean);
				}
			}
		}
		// Handling Exception while rendering view selection comments
		catch (ApplicationException aoAppExp)
		{
			setGenericErrorMessage(aoResourceRequest);
			LOG_OBJECT.Error("Error occured while fetching required proposal details", aoAppExp);
		}
		// Handling Exception other than ApplicationException
		catch (Exception aoExp)
		{
			setGenericErrorMessage(aoResourceRequest);
			LOG_OBJECT.Error("Error occured while fetching required proposal details", aoExp);
		}
		return new ModelAndView(HHSConstants.VIEW_SELECTION_COMMENTS);
	}

	/**
	 * This method will handle the action on confirmselectedprovider screen on
	 * click of "Confirm" button
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. Retrieve award amount and comments value from the ActionRequest
	 * object</li>
	 * <li>2. Call transaction <b>updateSelectedProposalDetails</b> to update
	 * the proposal details corresponding to the proposal Id.</li>
	 * <li>3. Redirect all the updated data to S218 screen</li>
	 * <li>4. If any exception occurs while executing the transaction then those
	 * exceptions will be caught by the ApplicationException catch block</li>
	 * </ul>
	 * 
	 * @param aoRequest - a ActionRequest object
	 * @param aoResponse - a ActionResponse object
	 * @throws ApplicationException
	 */
	@ActionMapping(params = "submit_action=selectedProvider")
	public void actionSelectedProvider(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		Map<String, EvaluationBean> loParamMap = new HashMap<String, EvaluationBean>();
		try
		{
			// Added User id & passing to query insertSelectedProposalComments
			// in evaluationmapper.xml, Release 2.5.0
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			// Changes for 6574 for Release 3.10.0
			Map<String, String> loStatusInfoMap = new HashMap<String, String>();
			loStatusInfoMap.put(HHSConstants.PROCUREMENT_ID, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			loStatusInfoMap.put(HHSConstants.USER_ID, lsUserId);
			loStatusInfoMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID,
					aoRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID));
			Channel loChannelObj = new Channel();
			EvaluationBean loEvaluationBean = new EvaluationBean();
			loEvaluationBean.setProposalId(aoRequest.getParameter(HHSConstants.PROPOSAL_ID));
			loEvaluationBean.setProcurementId(aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			loEvaluationBean.setAwardAmount(aoRequest.getParameter(HHSConstants.AWARD_AMOUNT));
			loEvaluationBean.setComments(aoRequest.getParameter(HHSConstants.COMMENTS));
			loEvaluationBean
					.setEvaluationPoolMappingId(aoRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID));
			loEvaluationBean.setModifiedByUserId(lsUserId);
			loParamMap.put(HHSConstants.LO_EVAL_BEAN, loEvaluationBean);
			loEvaluationBean.setProposalStatusId(Integer.parseInt(PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_SELECTED)));
			loChannelObj.setData(HHSConstants.LO_EVAL_BEAN, loEvaluationBean);
			loChannelObj.setData(HHSConstants.STATUS_INFO_MAP, loStatusInfoMap);
			// Changes for 6574 for Release 3.10.0--End
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.UPDATE_SELECTED_PROPOSAL_DETAILS);
			Integer loCount = (Integer) loChannelObj.getData(HHSConstants.LOCAL_COUNT);
			// checking if count is greater than zero
			if (loCount > 0)
			{
				aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID,
						aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
				aoResponse.setRenderParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID,
						aoRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID));
				aoResponse.setRenderParameter(HHSConstants.EVALUATION_GROUP_ID,
						aoRequest.getParameter(HHSConstants.EVALUATION_GROUP_ID));
				aoResponse.setRenderParameter(HHSConstants.COMPETITION_POOL_ID,
						aoRequest.getParameter(HHSConstants.COMPETITION_POOL_ID));
				aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.FETCH_EVAL_RESUTS);
				aoResponse.setRenderParameter(HHSConstants.TOP_LEVEL_FROM_REQ, HHSConstants.PROPOSAL_AND_EVALUATION);
				aoResponse.setRenderParameter(HHSConstants.MID_LEVEL_FROM_REQ,
						HHSConstants.EVALUATION_RESULT_AND_SELECTION);
			}
		}
		// Handling Exception while rendering evaluation result and selection
		catch (ApplicationException aoException)
		{
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap, aoException);
			LOG_OBJECT.Error("Exception Occured while confirming a proposal selected", aoException);
		}
		// handling exception other than Application Exception.
		catch (Exception aoException)
		{
			LOG_OBJECT.Error("Exception Occured while confirming a proposal selected", aoException);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
	}

	/**
	 * This method will handle the action on "confirmnotselectedprovider" screen
	 * on click of "Confirm" button
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. Retrieve comments value from the ActionRequest object</li>
	 * <li>2. Call transaction <b>updateNotSelectedProposalDetails</b> to update
	 * the proposal details corresponding to the proposal Id.</li>
	 * <li>3. Redirect all the updated data to S218 screen</li>
	 * <li>4. If any exception occurs while executing the transaction then those
	 * exceptions will be caught by the ApplicationException catch block</li>
	 * </ul>
	 * 
	 * @param aoRequest - a ActionRequest object
	 * @param aoResponse - a ActionResponse object
	 * @param asProposalId - string representation of Proposal Id
	 * @throws ApplicationException
	 */
	@ActionMapping(params = "submit_action=notSelectedProvider")
	public void actionNotSelectedProvider(ActionRequest aoRequest, ActionResponse aoResponse, String asProposalId)

	{
		Map<String, EvaluationBean> loParamMap = new HashMap<String, EvaluationBean>();
		try
		{
			// Added User id & passing to query insertSelectedProposalComments
			// in evaluationmapper.xml, Release 2.5.0
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			Channel loChannelObj = new Channel();
			EvaluationBean loEvaluationBean = new EvaluationBean();
			loEvaluationBean.setProposalId(aoRequest.getParameter(HHSConstants.PROPOSAL_ID));
			loEvaluationBean.setProcurementId(aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			loEvaluationBean
					.setEvaluationPoolMappingId(aoRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID));
			loEvaluationBean.setModifiedByUserId(lsUserId);
			loEvaluationBean.setComments(aoRequest.getParameter(HHSConstants.COMMENTS));
			loEvaluationBean.setModifiedByUserId(lsUserId);
			loEvaluationBean.setProposalStatusId(Integer.parseInt(PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_NOT_SELECTED)));
			loParamMap.put(HHSConstants.LO_EVAL_BEAN, loEvaluationBean);
			loChannelObj.setData(HHSConstants.LO_EVAL_BEAN, loEvaluationBean);
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.UPDATE_NOT_SELECTED_PROPOSAL_DETAILS);
			Integer loCount = (Integer) loChannelObj.getData(HHSConstants.LOCAL_COUNT);
			// checking if count is greater than zero
			if (loCount > 0)
			{
				aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID,
						aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
				aoResponse.setRenderParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID,
						aoRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID));
				aoResponse.setRenderParameter(HHSConstants.EVALUATION_GROUP_ID,
						aoRequest.getParameter(HHSConstants.EVALUATION_GROUP_ID));
				aoResponse.setRenderParameter(HHSConstants.COMPETITION_POOL_ID,
						aoRequest.getParameter(HHSConstants.COMPETITION_POOL_ID));
				aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.FETCH_EVAL_RESUTS);
				aoResponse.setRenderParameter(HHSConstants.TOP_LEVEL_FROM_REQ, HHSConstants.PROPOSAL_AND_EVALUATION);
				aoResponse.setRenderParameter(HHSConstants.MID_LEVEL_FROM_REQ,
						HHSConstants.EVALUATION_RESULT_AND_SELECTION);
			}
		}
		// Handling Exception while rendering evaluation result and selection
		catch (ApplicationException aoException)
		{
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap, aoException);
			LOG_OBJECT.Error("Exception Occured while confirming a proposal not-selected", aoException);
		}
		// handling exception other than Application Exception.
		catch (Exception aoException)
		{
			LOG_OBJECT.Error("Exception Occured while confirming a proposal not-selected", aoException);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);

		}
	}

	/**
	 * This page allows the user to view the evaluation summary for a provider's
	 * response to a Procurement.
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. Retrieve procurement Id, proposal Id and evaluation Status Id from
	 * the request object</li>
	 * <li>2. Call getPageHeader() method to get fetch the navigation related
	 * information</li>
	 * <li>3. Create channel object and set procurement Id, proposal Id and
	 * evaluation status Id in it</li>
	 * <li>4. Call transaction <b>displayEvaluationSummary</b> to retrieve the
	 * evaluation summary</li>
	 * <li>5. Fetch evaluation criteria list, evaluation comments list, Acco
	 * comments list, Evaluation Score list and procurement title from the
	 * executed transaction</li>
	 * <li>6. Set all the retrieved information in the request object</li>
	 * <li>7. Redirect the user to "viewEvaluationSummary" screen</li>
	 * <li>8. If any application exception occurs while executing the
	 * transaction then those exceptions will be caught by the
	 * ApplicationException catch block</li>
	 * <li>9. If any exception occurs while executing the transaction then those
	 * exceptions will be caught by the Exception catch block</li>
	 * </ul>
	 * 
	 * @param aoRequestForEvalution - a ResourceRequest object
	 * @param aoResponseForEvaluation - a ResourceResponse object
	 * @return - a ModelAndView object
	 */
	@SuppressWarnings("unchecked")
	@RenderMapping(params = "render_action=displayEvaluationSummary")
	protected ModelAndView displayEvaluationSummary(RenderRequest aoRequestForEvalution,
			RenderResponse aoResponseForEvaluation)
	{
		try
		{
			String lsProcurementId = aoRequestForEvalution.getParameter(HHSConstants.PROCUREMENT_ID);
			String lsProposalId = aoRequestForEvalution.getParameter(HHSConstants.PROPOSAL_ID);
			String lsEvalPoolMappingId = aoRequestForEvalution.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID);
			getPageHeader(aoRequestForEvalution, lsProcurementId);
			Channel loChannelObjForEvaluation = new Channel();
			loChannelObjForEvaluation.setData(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
			loChannelObjForEvaluation.setData(HHSConstants.PROPOSAL_ID, lsProposalId);
			loChannelObjForEvaluation.setData(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolMappingId);
			HHSTransactionManager
					.executeTransaction(loChannelObjForEvaluation, HHSConstants.DISPLAY_EVALUATION_SUMMARY);

			List<EvaluationBean> loEvalCriteriaList = (List<EvaluationBean>) loChannelObjForEvaluation
					.getData(HHSConstants.LOCAL_EVAL_CRITERIA_LIST);
			List<EvaluationBean> loEvalCommentsList = (List<EvaluationBean>) loChannelObjForEvaluation
					.getData(HHSConstants.LOCAL_EVAL_COMMENTS_LIST);
			List<EvaluationBean> loEvaluatorScoreList = (List<EvaluationBean>) loChannelObjForEvaluation
					.getData(HHSConstants.LOCAL_EVAL_SCORES_LIST);
			// R5 starts : updated to List
			List<Map<String, String>> loAccoCommentsAndTitle = (List<Map<String, String>>) loChannelObjForEvaluation
					.getData(HHSConstants.LOCAL_ACCO_COMMENTS_LIST);
			// R5 ends : updated to List
			Map<String, String> loProposalOrgMap = (Map<String, String>) loChannelObjForEvaluation
					.getData(HHSConstants.PROPOSAL_ORG_MAP);
			aoRequestForEvalution.setAttribute(HHSConstants.EVAL_CRITERIA_LIST, loEvalCriteriaList);
			aoRequestForEvalution.setAttribute(HHSConstants.EVAL_COMMENT_LIST, loEvalCommentsList);
			aoRequestForEvalution.setAttribute(HHSConstants.EVAL_SCORES_LIST, loEvaluatorScoreList);
			aoRequestForEvalution.setAttribute(HHSConstants.LOCAL_ACCO_COMMENT_AND_TITLE, loAccoCommentsAndTitle);
			aoRequestForEvalution.setAttribute(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			// Added below line for for enhancement 5415.
			aoRequestForEvalution.setAttribute(HHSConstants.PROPOSAL_ID, lsProposalId);

			aoRequestForEvalution.setAttribute(HHSConstants.PROCUREMENT_TITLE,
					loChannelObjForEvaluation.getData(HHSConstants.PROCUREMENT_TITLE));
			// COMP_TITLE,EVALUATION_TITLE and OPEN_ENDED_PROC have been added
			// as a result of R4 change for open ended procurement
			aoRequestForEvalution.setAttribute(HHSConstants.COMP_TITLE,
					loProposalOrgMap.get(HHSConstants.COMP_POOL_TITLE));
			aoRequestForEvalution.setAttribute(HHSConstants.EVALUATION_TITLE,
					loProposalOrgMap.get(HHSConstants.EVALUATION_GROUP));
			aoRequestForEvalution.setAttribute(HHSConstants.OPEN_ENDED_PROC,
					loProposalOrgMap.get(HHSConstants.IS_OPEN_ENDED_RFP));
			if (null != loProposalOrgMap)
			{
				aoRequestForEvalution.setAttribute(HHSConstants.ORGANIZATN_NAME,
						loProposalOrgMap.get(HHSConstants.ORGANIZATION_NAME));
				aoRequestForEvalution.setAttribute(HHSConstants.PROPOSAL_TITLE,
						loProposalOrgMap.get(HHSConstants.PROP_TITLE));
			}
			aoRequestForEvalution.setAttribute(HHSConstants.INT_EXT_EVALUATORS_LIST,
					loChannelObjForEvaluation.getData(HHSConstants.INT_EXT_EVALUATORS_LIST));
		}
		// Handling Exception while rendering view evaluation summary
		catch (ApplicationException aoExp)
		{
			setGenericErrorMessage(aoRequestForEvalution);
			LOG_OBJECT.Error("Error occured while fetching evaluation details", aoExp);
		}
		// Handling Exception other than ApplicationException
		catch (Exception aoExp)
		{
			setGenericErrorMessage(aoRequestForEvalution);
			LOG_OBJECT.Error("Error occured while fetching evaluation details", aoExp);

		}
		return new ModelAndView(HHSConstants.VIEW_EVALUATION_SUMMARY, HHSConstants.EVALUATION_BEAN_LOWERCASE,
				new EvaluationBean());
	}

	/**
	 * This method will handle the action on "finalizeProposalStatus" screen
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. when finalize/update button is clicked call
	 * <b>finalizeProcurement</b> transaction to finalize a procurement
	 * corresponding to a procurement Id
	 * <li>2. Fetch details from the transaction</li>
	 * <li>3. Redirect to "evaluationresultsandselections" screen</li>
	 * </ul>
	 * 
	 * @param aoAuthenticationBean AuthenticationBean
	 * @param aoResult BindingResult
	 * @param aoRequest ActionRequest
	 * @param aoResponse ActionResponse
	 * 
	 */
	@SuppressWarnings("rawtypes")
	@ActionMapping(params = "submit_action=finalizeProcurement")
	protected void actionFinalizeProcurement(
			@ModelAttribute("AuthenticationBean") AuthenticationBean aoAuthenticationBean, BindingResult aoResult,
			ActionRequest aoRequest, ActionResponse aoResponse)
	{
		Channel loChannel = new Channel();
		P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		HashMap<String, Object> loHmReqExceProp = new HashMap<String, Object>();
		try
		{
			setNavigationParamsInRender(aoRequest, aoResponse);
			validator.validate(aoAuthenticationBean, aoResult);
			String lsProcurementId = aoRequest.getParameter(HHSConstants.PROCUREMENT_ID);
			String lsEvalPoolMappingId = aoRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID);
			aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			aoResponse.setRenderParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolMappingId);
			HashMap<String, Object> loCancellWorkflowMap = new HashMap<String, Object>();
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
					aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
					aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.ERROR_PAGE_CLOSE_FINALIZE);
				}
				else
				{
					finalizeProcurementFinal(aoRequest, loChannel, loUserSession, lsProcurementId, lsEvalPoolMappingId,
							loCancellWorkflowMap);
				}
			}
			else
			{
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE,
						PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.AUTH_FAIL));
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE);
				aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.ERROR_PAGE_CLOSE_FINALIZE);
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loHmReqExceProp, aoExp);
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.ERROR_PAGE_CLOSE_FINALIZE);
			LOG_OBJECT.Error("Exception Occured while finalizing a procurement:", aoExp);
		}
		// handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception Occured while finalizing a procurement:", aoExp);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.ERROR_PAGE_CLOSE_FINALIZE);
		}
	}

	/**
	 * This method will handle the action on "finalizeProposalStatus" screen
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. when finalize/update button is clicked call
	 * <b>finalizeProcurement</b> transaction to finalize a procurement
	 * corresponding to a procurement Id
	 * <li>2. Fetch details from the transaction</li>
	 * <li>3. Redirect to "evaluationresultsandselections" screen</li>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest
	 * @param loChannel Channel Object
	 * @param loUserSession Protlet session
	 * @param lsProcurementId proc Id
	 * @param lsEvalPoolMappingId eval pool mapping Id
	 * @param loCancellWorkflowMap workflow input param map
	 * @throws ApplicationException If an ApplicationException Occurs
	 */
	@SuppressWarnings("unchecked")
	private void finalizeProcurementFinal(ActionRequest aoRequest, Channel loChannel, P8UserSession loUserSession,
			String lsProcurementId, String lsEvalPoolMappingId, HashMap<String, Object> loCancellWorkflowMap)
			throws ApplicationException
	{
		String lsAction = aoRequest.getParameter(HHSConstants.NEXT_ACTION);
		String lsFirstAction = HHSConstants.FINALIZE_PROCUREMENT;
		String lsSecondAction = HHSConstants.UPDATE_FINALIZE_PROCUREMENT;
		// Start || Changes done for Enhancement #6574 for Release 3.10.0
		String lsThirdAction = HHSConstants.UPDATE_AFTER_AWARD_APPROVAL;
		String lsTransactionName = HHSUtil.getTransactionName(lsAction, lsFirstAction, lsSecondAction, lsThirdAction);
		// End || Changes done for Enhancement #6574 for Release 3.10.0
		PortletSession loSession = aoRequest.getPortletSession();
		String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		Map<String, Object> loInputParamMap = new HashMap<String, Object>();
		loInputParamMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolMappingId);
		loInputParamMap.put(HHSConstants.COMPETITION_POOL_STATUS, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_COMPETITION_POOL_EVALUATIONS_COMPLETE));
		loInputParamMap.put(HHSConstants.USER_ID, lsUserId);
		loInputParamMap.put(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
		loInputParamMap.put(HHSConstants.AWARD_STATUS_ID, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_AWARD_REVIEW_IN_REVIEW));
		// PARAMETERS ADDED FOR AUDIT CHANGE
		loInputParamMap.put(HHSConstants.EVENT_NAME, HHSConstants.FINALIZE_UPDATE);
		loInputParamMap.put(HHSConstants.EVENT_TYPE, HHSConstants.FINALIZE_UPDATE_AWARDS_RESULTS);
		loChannel.setData(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolMappingId);
		loChannel.setData(HHSConstants.LS_PRO_ID, lsProcurementId);
		loChannel.setData(HHSConstants.LO_WORKFLOW_NAME, P8Constants.PE_EVALUATE_AWARD_TASK_NAME);
		loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
		// add the required parameters for workflow integration in a
		// map.
		loCancellWorkflowMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, lsProcurementId);
		loCancellWorkflowMap.put(P8Constants.LAUNCHED_BY, lsUserId);
		loCancellWorkflowMap.put(P8Constants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolMappingId);
		// Start || Changes done for Enhancement #6574 for Release 3.10.0
		if (null != lsTransactionName && lsTransactionName.equalsIgnoreCase(HHSConstants.UPDATE_AFTER_AWARD_APPROVAL))
		{
			loCancellWorkflowMap.put(P8Constants.ACTION_KEY_FOR_UTILITY_WORKFLOW,
					P8Constants.CANCEL_AFTER_AWARD_APPROVAL);
			loChannel.setData(HHSConstants.WORK_FLOW_NAME, P8Constants.PE_EVALUATION_UTILITY_WORKFLOW_NAME);
		}
		// End || Changes done for Enhancement #6574 for Release 3.10.0
		loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
		// add the map to the channel object
		loChannel.setData(HHSConstants.AO_HMWF_REQUIRED_PROPS, loCancellWorkflowMap);
		// add parameter map to channel
		loChannel.setData(HHSConstants.INPUT_PARAM_MAP, loInputParamMap);
		HHSTransactionManager.executeTransaction(loChannel, lsTransactionName);
	}

	/**
	 * This method is used to render the error page when credentials are wrong
	 * while finalize the procurement.
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. Get user name from finalizeupdateresults jsp and set in request.</li>
	 * </ul>
	 * 
	 * @param aoRequest - RenderRequest
	 * @param aoResponse - RenderResponse
	 * @return - ModelAndView containing the JSP name(finalizeupdateresults.jsp)
	 */
	@RenderMapping(params = "render_action=errorPageCloseFinalize")
	protected ModelAndView renderCloseProcurementFinalize(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		ModelAndView loModelAndView = null;
		try
		{
			loModelAndView = new ModelAndView(HHSConstants.FINALIZE_UPDATE_RESULTS);
			aoRequest.setAttribute(HHSConstants.PROCUREMENT_ID, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			aoRequest.setAttribute(HHSConstants.EVALUATION_POOL_MAPPING_ID,
					aoRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID));
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
					aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE));
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE_TYPE));
		}
		catch (Exception aoException)
		{
			setGenericErrorMessage(aoRequest);
			LOG_OBJECT.Error("Exception Occured while rendering Evalution Status", aoException);
		}
		return loModelAndView;
	}

	/**
	 * This method will handle the action on the click of next button from
	 * screen no <b>S214</b>
	 * <ul>
	 * <li>Retrieve the procurement Id from the request Object</li>
	 * <li>Execute transaction Id <b>fetchEvaluationDetails</b></li>
	 * <li>Get the Evolution Bean List for the Specific procurement id from
	 * channel object</li>
	 * <li>Set the Evolution Bean list in the request object and Redirect user
	 * to the screen no <b>S215</b></li>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest Object
	 * @param aoResponse ActionResponse Object
	 * @param asProcurementId Procurement Id
	 */
	@ActionMapping(params = "submit_action=getEvaluationStatusAndList")
	public void actionGetEvaluationStatusAndList(ActionRequest aoRequest, ActionResponse aoResponse,
			String asProcurementId)
	{
		Map<String, String> loParamMap = new HashMap<String, String>();
		try
		{
			Channel loChannel = new Channel();
			loParamMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
			loChannel.setData(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_EVALUATION_DETAILS);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoApplicationException)
		{
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap, aoApplicationException);

		}
		catch (Exception aoException)
		{
			LOG_OBJECT.Error("Exception Occurred in while fetching the evaluatin detail", aoException);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);

		}
	}

	/**
	 * This method will get all the details of the selected proposal and
	 * redirect user to the Screen no <b>S220</b>
	 * <ul>
	 * <li>1. Get the proposalId from the request object</li>
	 * <li>2. Execute Transaction <b>fetchProposalDetails</b></li>
	 * <li>3. Get the proposal details bean from the channel object</li>
	 * <li>4. Set the proposal bean in the request object and redirect user to
	 * the screen no <b>S220</b></li>
	 * <li>5. If any exception occurs while executing the transaction then those
	 * exceptions will be caught by the ApplicationException catch block</li>
	 * </ul>
	 * 
	 * @param aoRequest - a ActionRequest Object
	 * @param aoResponse - a ActionResponse Object
	 * @param asProposalId string representation of Proposal Id
	 * 
	 */
	@ActionMapping(params = "submit_action=getProposalDetails")
	public void actionGetProposalDetails(ActionRequest aoRequest, ActionResponse aoResponse, String asProposalId)
	{
		Map<String, String> loParamMap = new HashMap<String, String>();
		try
		{
			String lsStaffId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			Channel loChannel = new Channel();
			loParamMap.put(HHSConstants.PROPOSAL_ID_KEY, asProposalId);
			loChannel.setData(HHSConstants.PROPOSAL_ID_KEY, asProposalId);
			loChannel.setData(HHSConstants.AS_USER_ID, lsStaffId);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_PROPOSAL_DETAILS);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoApplicationException)
		{
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap, aoApplicationException);
			LOG_OBJECT.Error("Exception Occurred in while fetching the ProposaL detail", aoApplicationException);
		}
		// Handling Exception other than ApplicationException
		catch (Exception aoException)
		{
			LOG_OBJECT.Error("Exception Occurred in while fetching the ProposaL detail", aoException);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);

		}
	}

	/**
	 * This method will assign E-pin to any award
	 * 
	 * <li>Execute transaction Id <b>assignAwardEPin</b></li>
	 * @param aoRequest action request object
	 * @param aoResponse action response object
	 * @param asProcurementId procurement id
	 * 
	 */
	@ActionMapping(params = "submit_action=getAwardEpinList")
	public void actionAssignAwardEpin(ActionRequest aoRequest, ActionResponse aoResponse, String asProcurementId)
	{
		Map<String, String> loParamMap = new HashMap<String, String>();
		try
		{
			String lsOrganizationId = PortalUtil.parseQueryString(aoRequest, HHSConstants.ORGANIZATION_ID_LOWERCASE);
			String lsEpinId = ((ProcurementInfo) aoRequest.getAttribute(HHSConstants.PROCUREMENT_BEAN))
					.getProcurementEpin();
			Channel loChannel = new Channel();
			loParamMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
			loParamMap.put(HHSConstants.LS_ORGANIZATION_ID, lsOrganizationId);
			loParamMap.put(HHSConstants.LS_EPIN_ID, lsEpinId);
			loChannel.setData(HHSConstants.LO_PARAM_MAP, loParamMap);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.ASSIGN_AWARD_PIN);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoEx)
		{
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap, aoEx);
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while assigning award E-PIN", aoEx);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
	}

	/**
	 * This method fetches the proposal data for all the providers and display
	 * it on the page.
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. Check for the proposal Bean in the application session.</li>
	 * <li>2. Fetch the procurement Id from request.</li>
	 * <li>3. Call the getPageHeader() method for navigation.</li>
	 * <li>4. set procurement Id in the channel object.</li>
	 * <li>5. Execute the transaction "fetchProposalSummary" and set the
	 * proposal detail in the request to be displayed on the page.</li>
	 * <li>6. Return ModelAndView containing name of the jsp and the data to be
	 * rendered on the page.</li>
	 * </ul>
	 * 
	 * @param aoRequestForSummary - RenderRequest
	 * @param aoResponseForSummary - RenderResponse
	 * @return ModelAndView
	 */
	@SuppressWarnings("unchecked")
	@RenderMapping(params = "render_action=proposalSummary")
	protected ModelAndView renderProposalSummaryProvider(RenderRequest aoRequestForSummary,
			RenderResponse aoResponseForSummary)
	{
		ProposalDetailsBean loProposalDetailsBean = null;
		// Fetching bean from the session containing sorting details
		loProposalDetailsBean = (ProposalDetailsBean) ApplicationSession.getAttribute(aoRequestForSummary, true,
				HHSConstants.PROPOSAL_DETAIL_BEAN_KEY);
		if (null == loProposalDetailsBean)
		{
			loProposalDetailsBean = new ProposalDetailsBean();
		}
		try
		{
			PortletSession loSession = aoRequestForSummary.getPortletSession();
			String lsUserOrgType = (String) aoRequestForSummary.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			String lsUserRole = (String) aoRequestForSummary.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ROLE, PortletSession.APPLICATION_SCOPE);
			String lsOrganizationId = (String) aoRequestForSummary.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);
			String lsProcurementId = PortalUtil.parseQueryString(aoRequestForSummary, HHSConstants.PROCUREMENT_ID);
			String lsProposalId = PortalUtil.parseQueryString(aoRequestForSummary, HHSConstants.PROPOSAL_ID);
			// set procurement id in bean if its not null.
			if (lsProcurementId != null && !lsProcurementId.equals(HHSConstants.EMPTY_STRING))
			{
				loProposalDetailsBean.setProcurementId(lsProcurementId);
			}
			getPageHeader(aoRequestForSummary, lsProcurementId);
			Channel loChannelForSummary = new Channel();
			String lsNextPage = aoRequestForSummary.getParameter(HHSConstants.NEXT_PAGE_PARAM);
			getPagingParams(loSession, loProposalDetailsBean, lsNextPage, HHSConstants.PROPOSAL_SUMMARY_LOWERCASE);
			loProposalDetailsBean.setOrganizationId(lsOrganizationId);
			loChannelForSummary.setData(HHSConstants.PROPOSAL_DETAILS_BEAN, loProposalDetailsBean);
			HHSTransactionManager.executeTransaction(loChannelForSummary, HHSConstants.FETCH_PROPOSAL_SUMMARY);
			renderProposalSummProviderFinal(aoRequestForSummary, loProposalDetailsBean, loSession, lsUserOrgType,
					lsUserRole, lsProcurementId, lsProposalId, loChannelForSummary);
			// Release 5 User Notification
			aoRequestForSummary.setAttribute(HHSR5Constants.PROC_ROADMAP_READONLY_FLAG,
					getFinancialsReadOnly(aoRequestForSummary, lsUserOrgType));
			// Release 5 User Notification
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			setGenericErrorMessage(aoRequestForSummary);
		}
		// handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			setGenericErrorMessage(aoRequestForSummary);
			LOG_OBJECT.Error("Error Occured while rendering procurement details : ", aoExp);
		}
		return new ModelAndView(HHSConstants.PROPOSAL_SUMMARY_JSP, HHSConstants.PROPOSAL_DETAILS_BEAN_UPPERCASE,
				loProposalDetailsBean);
	}

	/**
	 * This method fetches the proposal data for all the providers and display
	 * it on the page.
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. Check for the proposal Bean in the application session.</li>
	 * <li>2. Fetch the procurement Id from request.</li>
	 * <li>3. Call the getPageHeader() method for navigation.</li>
	 * <li>4. set procurement Id in the channel object.</li>
	 * <li>5. Execute the transaction "fetchProposalSummary" and set the
	 * proposal detail in the request to be displayed on the page.</li>
	 * <li>6. Return ModelAndView containing name of the jsp and the data to be
	 * rendered on the page.</li>
	 * </ul>
	 * 
	 * @param aoRequestForSummary RenderRequest
	 * @param loProposalDetailsBean ProposalDetailsBean object
	 * @param loSession Portlet session
	 * @param lsUserOrgType user organization type
	 * @param lsUserRole user role
	 * @param lsProcurementId proc id
	 * @param lsProposalId proposal id
	 * @param loChannelForSummary cahnnel object
	 * @throws ApplicationException IF an ApplicationException occurs
	 */
	private void renderProposalSummProviderFinal(RenderRequest aoRequestForSummary,
			ProposalDetailsBean loProposalDetailsBean, PortletSession loSession, String lsUserOrgType,
			String lsUserRole, String lsProcurementId, String lsProposalId, Channel loChannelForSummary)
			throws ApplicationException
	{
		List<ProposalDetailsBean> loProposalDetailsBeanList = (List<ProposalDetailsBean>) loChannelForSummary
				.getData(HHSConstants.PROPOSAL_DETAILS_MAP);
		Integer loProposalCount = (Integer) loChannelForSummary.getData(HHSConstants.PROPOSAL_COUNT);
		loSession.setAttribute(ApplicationConstants.ALERT_VIEW_PAGING_RECORDS, loProposalCount,
				PortletSession.APPLICATION_SCOPE);
		if (loProposalDetailsBeanList != null && !loProposalDetailsBeanList.isEmpty())
		{
			if (loProposalDetailsBeanList.get(0) != null)
			{
				if (loProposalDetailsBeanList.get(0).getProposalTitle() != null
						&& !loProposalDetailsBeanList.get(0).getProposalTitle().equals(HHSConstants.EMPTY_STRING))
				{
					aoRequestForSummary.setAttribute(HHSConstants.PROPOSAL_DUE_DATE, loProposalDetailsBeanList.get(0)
							.getProposalDueDate());
					for (ProposalDetailsBean loTempProposalDetailsBean : loProposalDetailsBeanList)
					{
						loTempProposalDetailsBean.setUserRole(lsUserRole);
					}
				}
				else if (((ProcurementInfo) aoRequestForSummary.getAttribute(HHSConstants.PROCUREMENT_BEAN))
						.getIsOpenEndedRFP().equalsIgnoreCase(HHSConstants.ZERO)
						&& loProposalDetailsBeanList.get(0).getProposalDueDate() != null)
				{
					aoRequestForSummary.setAttribute(HHSConstants.PROPOSAL_DUE_DATE, loProposalDetailsBeanList.get(0)
							.getProposalDueDate());
					loProposalDetailsBeanList = null;
				}
			}
			else
			{
				loProposalDetailsBeanList = null;
			}
		}
		aoRequestForSummary.setAttribute(HHSConstants.PROPOSAL_DETAILS_MAP, loProposalDetailsBeanList);
		aoRequestForSummary.setAttribute(HHSConstants.ACCELERATOR_USER_ROLE, lsUserRole);
		aoRequestForSummary.setAttribute(HHSConstants.ORG_TYPE, lsUserOrgType);
		aoRequestForSummary.setAttribute(HHSConstants.PROCUREMENT_ID, lsProcurementId);
		aoRequestForSummary.setAttribute(HHSConstants.PROPOSAL_ID, lsProposalId);
		aoRequestForSummary.getPortletSession().setAttribute(HHSConstants.SORT_TYPE,
				loProposalDetailsBean.getFirstSortType(), PortletSession.APPLICATION_SCOPE);
		aoRequestForSummary.getPortletSession().setAttribute(HHSConstants.SORT_BY,
				loProposalDetailsBean.getSortColumnName(), PortletSession.APPLICATION_SCOPE);
		aoRequestForSummary.setAttribute(ApplicationConstants.ERROR_MESSAGE,
				aoRequestForSummary.getParameter(ApplicationConstants.ERROR_MESSAGE));
		aoRequestForSummary.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
				aoRequestForSummary.getParameter(ApplicationConstants.ERROR_MESSAGE_TYPE));
		HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb.getInstance()
				.getCacheObject(ApplicationConstants.APPLICATION_SETTING);
		String loReleaseTime = loApplicationSettingMap.get(HHSConstants.PROPOSAL_RELEASE_TIME_KEY);
		aoRequestForSummary.setAttribute(HHSConstants.RELEASE_TIME, loReleaseTime);
	}

	/**
	 * This method verify External User Agency.
	 * 
	 * <ul>
	 * <li>1. Get the staff id & provider id from request and set in Map.</li>
	 * <li>2. Set the other proposal Details in Map.</li>
	 * <li>3. Set the Map in the channel.</li>
	 * <li>4. Execute the transaction "insertNewProposalDetails" to save the
	 * proposal details in db.</li>
	 * <li>5. Get the procurement id from request and set in response, to get in
	 * the render method.</li>
	 * <li>6. Set the result of the transaction in response.</li>
	 * <li>7. Set the action name, to identify the controller, in response.</li>
	 * <li>8. Set the render method name, to be used to render the page, in
	 * response.</li>
	 * </ul>
	 * 
	 * @param aoRequest action request object
	 * @param aoResponse action response object
	 */
	@ActionMapping(params = "submit_action=addNewProposal")
	protected void addNewProposal(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		String lsProcurementId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
		Map<String, String> loProposalDetailMap = new HashMap<String, String>();
		try
		{
			PortletSession loSession = aoRequest.getPortletSession();
			String lsStaffId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);
			String lsProviderId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
					PortletSession.APPLICATION_SCOPE);
			Channel loChannel = new Channel();
			loProposalDetailMap.put(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			loProposalDetailMap.put(HHSConstants.STAFF_ID, HHSConstants.EMPTY_STRING);
			loProposalDetailMap.put(HHSConstants.PROVIDER_ID, lsProviderId);
			loProposalDetailMap.put(HHSConstants.PROPOSAL_TITLE, HHSConstants.UNTITLED_PROPOSAL);
			loProposalDetailMap.put(HHSConstants.PROPOSAL_STATUS_ID_KEY, HHSConstants.DEFAULT_PROPOSAL_STATUS);
			loProposalDetailMap.put(HHSConstants.CREATED_BY, lsStaffId);
			loProposalDetailMap.put(HHSConstants.ACTIVE_FLAG, HHSConstants.ONE);
			loChannel.setData(HHSConstants.PROVIDER_ID, lsProviderId);
			loChannel.setData(HHSConstants.AO_PROPOSAL_DETAIL_MAP, loProposalDetailMap);
			loChannel.setData(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.INS_NEW_PROPOSAL_DETAILS);
			String lsProcurmentStatus = (String) loChannel.getData(HHSConstants.PROCUREMENT_STATUS);
			String lsProposalId = (String) loChannel.getData(HHSConstants.PROPOSAL_ID);
			Boolean loProcurementStatusFlag = false;
			if (null != lsProcurmentStatus
					&& (lsProcurmentStatus.equals(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
							HHSConstants.STATUS_PROCUREMENT_RELEASED))))
			{
				loProcurementStatusFlag = true;
			}
			aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID,
					aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			aoResponse.setRenderParameter(HHSConstants.TOP_LEVEL_FROM_REQ, HHSConstants.PROPOSAL_SUMMARY);
			if (!loProcurementStatusFlag || lsProposalId == null)
			{
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.ADD_NEW_PROP_FAIL));
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.PROPOSAL_SUMMARY_LOWERCASE);
				aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
			}
			else
			{
				aoResponse.setRenderParameter(HHSConstants.PROPOSAL_ID, lsProposalId);
				aoResponse.setRenderParameter(HHSConstants.MID_LEVEL_FROM_REQ, HHSConstants.PROPOSAL_DET);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.PROC_PROPOSAL_DETAILS);
				aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loProposalDetailMap, aoExp);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.PROPOSAL_SUMMARY_LOWERCASE);
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
		}
		// handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Eroor Occured while creating new proposal : ", aoExp);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.PROPOSAL_SUMMARY_LOWERCASE);
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
		}
	}

	/**
	 * This method will handle sort action from procurement roadmap screen.
	 * 
	 * <ul>
	 * <li>1. Get sortType and columnName values from request by calling
	 * parseQueryString() method of class PortalUtil.</li>
	 * <li>2. Get sorting details by calling method getSortDetailsFromXML() from
	 * class BaseController.</li>
	 * <li>3. Set sorting parameters in ProposalDetailsBean bean object.</li>
	 * <li>4. Set render_action in response to call the appropriate render
	 * method.</li>
	 * <li>5. Set action in response to identify which controller is to be
	 * called.</li>
	 * </ul>
	 * 
	 * @param aoProposalDetailsBean a proposal bean object
	 * @param aoRequest an Action Request object
	 * @param aoResponse an Action Response object
	 */
	@SuppressWarnings("unchecked")
	@ActionMapping(params = "submit_action=sortProposal")
	protected void sortProposalSummary(
			@ModelAttribute("ProposalDetailsBean") ProposalDetailsBean aoProposalDetailsBean, ActionRequest aoRequest,
			ActionResponse aoResponse)
	{
		try
		{
			String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			String lsSortType = PortalUtil.parseQueryString(aoRequest, HHSConstants.SORT_TYPE);
			String lsColumnName = PortalUtil.parseQueryString(aoRequest, HHSConstants.COLUMN_NAME);
			getSortDetailsFromXML(lsColumnName, lsUserOrgType,
					PortalUtil.parseQueryString(aoRequest, HHSConstants.SORT_GRID_NAME), aoProposalDetailsBean,
					lsSortType);
			ApplicationSession.setAttribute(aoProposalDetailsBean, aoRequest, HHSConstants.PROPOSAL_DETAIL_BEAN_KEY);
			if (null != aoRequest.getParameter(HHSConstants.FILTER_ITEM_KEY))
			{
				aoResponse.setRenderParameter(HHSConstants.FILTERED,
						aoRequest.getParameter(HHSConstants.FILTER_ITEM_KEY));
			}
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.PROPOSAL_SUMMARY_LOWERCASE);
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
			aoResponse.setRenderParameter(HHSConstants.TOP_LEVEL_FROM_REQ, HHSConstants.PROPOSAL_SUMMARY);
		}
		// handling exception while processing proposal summary
		catch (ApplicationException aoExp)
		{
			Map loContextMap = new HashMap();
			loContextMap.put(HHSConstants.PROPOSAL_DETAIL_BEAN_KEY, aoProposalDetailsBean);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loContextMap, aoExp);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.PROPOSAL_SUMMARY_LOWERCASE);
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
			aoResponse.setRenderParameter(HHSConstants.TOP_LEVEL_FROM_REQ, HHSConstants.PROPOSAL_SUMMARY);
		}
		// handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception Occured while processing proposal Summary: ", aoExp);
			String lsErrorMsg = HHSConstants.ERROR_WHILE_PROCESSING_REQUEST;
			setExceptionMessageFromAction(aoResponse, lsErrorMsg, ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.PROPOSAL_SUMMARY_LOWERCASE);
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
			aoResponse.setRenderParameter(HHSConstants.TOP_LEVEL_FROM_REQ, HHSConstants.PROPOSAL_SUMMARY);
		}
	}

	/**
	 * This method will handle paginate evaluation action from evaluation Status
	 * screen and will render to getEvaluationStatus Render Method
	 * 
	 * <ul>
	 * <li>1.Call setNavigationParamsInRender for Setting Navigation Render
	 * Parameter</li>
	 * <li>2.Setting parameter required for rendering of GetEvaluationStatus
	 * Screen Rendering</li>
	 * </ul>
	 * 
	 * @param aoEvaluationBean evaluation bean object
	 * @param aoRequest action request object
	 * @param aoResponse action response object
	 */
	@ActionMapping(params = "submit_action=pagingEvalution")
	protected void actionPaginateEvaluationStatus(@ModelAttribute("EvaluationBean") EvaluationBean aoEvaluationBean,
			ActionRequest aoRequest, ActionResponse aoResponse)
	{
		setNavigationParamsInRender(aoRequest, aoResponse);
		aoResponse.setRenderParameter(HHSConstants.NEXT_PAGE_PARAM, aoRequest.getParameter(HHSConstants.NEXT_PAGE));
		aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.GET_EVALUATION_STATUS);
		aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
		aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
	}

	/**
	 * This method will handle paginate evaluation action from proposal summary
	 * screen and will render to renderProposalSummaryProvider Render Method
	 * 
	 * <ul>
	 * <li>1.Call setNavigationParamsInRender for Setting Navigation Render
	 * Parameter</li>
	 * <li>2.Setting parameter required for rendering of GetEvaluationStatus
	 * Screen Rendering</li>
	 * </ul>
	 * 
	 * @param aoEvaluationBean evaluation bean object
	 * @param aoRequest action request object
	 * @param aoResponse action response object
	 */
	@ActionMapping(params = "submit_action=pagingProposalSummary")
	protected void actionPaginateProposalSummary(
			@ModelAttribute("ProposalDetailsBean") ProposalDetailsBean aoProposalDetailsBean, ActionRequest aoRequest,
			ActionResponse aoResponse)
	{
		setNavigationParamsInRender(aoRequest, aoResponse);
		aoResponse.setRenderParameter(HHSConstants.NEXT_PAGE_PARAM, aoRequest.getParameter(HHSConstants.NEXT_PAGE));
		aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.PROPOSAL_SUMMARY_LOWERCASE);
		aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
		aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
	}

	/***
	 * This method will handle sort evaluation action from Evaluation Status
	 * <ul>
	 * <li>1.Setting Navigation parameters</li>
	 * <li>2.Get sortType and columnName values by calling parseQueryString()
	 * method from class PortalUtil. Get sorting details by calling method
	 * getSortDetailsFromXML() from class BaseController.</li>
	 * <li>3.Setting Evaluation Bean and other required parameter for successful
	 * rendering of getEvaluationStatus Method</li>
	 * <li></li>
	 * <ul>
	 * @param aoEvaluationBean EvaluationBean
	 * @param aoRequest ActionRequest
	 * @param aoResponse ActionResponse
	 */
	@ActionMapping(params = "submit_action=sortEvaluationStatus")
	protected void actionSortEvaluationStatus(@ModelAttribute("EvaluationBean") EvaluationBean aoEvaluationBean,
			ActionRequest aoRequest, ActionResponse aoResponse)
	{
		Map<String, String> loHmReqExceProp = new HashMap<String, String>();

		try
		{
			LOG_OBJECT.Debug("Entered into Evaluation Status Sort Action::");
			setNavigationParamsInRender(aoRequest, aoResponse);
			String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			String lsSortType = PortalUtil.parseQueryString(aoRequest, HHSConstants.SORT_TYPE);
			String lsColumnName = PortalUtil.parseQueryString(aoRequest, HHSConstants.COLUMN_NAME);
			getSortDetailsFromXML(lsColumnName, lsUserOrgType,
					PortalUtil.parseQueryString(aoRequest, HHSConstants.SORT_GRID_NAME), aoEvaluationBean, lsSortType);

			// Setting param for Rendering getEvaluationStatus Render Method
			aoRequest.getPortletSession().setAttribute(HHSConstants.EVALUATION_SESSION_BEAN, aoEvaluationBean,
					PortletSession.PORTLET_SCOPE);
			aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID,
					aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			loHmReqExceProp.put(HHSConstants.PROCUREMENT_ID, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.GET_EVALUATION_STATUS);
		}
		// handling ApplicationException that can be occured while
		// fetching evaluation status data for evaluation status screen
		catch (ApplicationException aoAppEx)
		{
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loHmReqExceProp, aoAppEx);
			LOG_OBJECT.Error("Exception Occured while sorting Evaluation Status: ", aoAppEx);
		}
		// handling Exception other than ApplicationException
		catch (Exception aoEx)
		{
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
			LOG_OBJECT.Error("Exception Occured while sorting Evaluation Status: ", aoEx);
		}
	}

	/**
	 * This method will be executed for default action, namely - URL refresh
	 * <ul>
	 * <li>This method capture all the action request which are not been
	 * captured by any of parameterized action methods</li>
	 * </ul>
	 * @param aoRequest - action request
	 * @param aoResponse - action response
	 * @throws ApplicationException if any exception occurred
	 */
	@ActionMapping
	protected void handleActionRequestInternal(ActionRequest aoRequest, ActionResponse aoResponse)
			throws ApplicationException
	{
		return;
	}

	/**
	 * The method will open the overlay content for cancel Evaluation Tasks.
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Procurement id is set in the request to be used while rendering the
	 * jsp.</li>
	 * </ul>
	 * @param aoResourceRequest ResourceRequest
	 * @param aoResourceResponse ResourceResponse
	 * @return ModelAndView containing the jsp cancelEvaluationTasks.jsp
	 */
	@ResourceMapping("cancelEvaluationTasks")
	protected ModelAndView cancelEvalutionTasksOverLay(ResourceRequest aoResourceRequest,
			ResourceResponse aoResourceResponse)
	{
		// Preparing ModelAndView object containing the JSP page name.
		ModelAndView loModelAndView = new ModelAndView(HHSConstants.CANCEL_EVALUATION_TASKS);
		// setting the procurement id in request to be used on the JSP.
		aoResourceRequest.setAttribute(HHSConstants.PROCUREMENT_ID,
				aoResourceRequest.getParameter(HHSConstants.PROCUREMENT_ID));
		aoResourceRequest.setAttribute(HHSConstants.EVALUATION_POOL_MAPPING_ID,
				aoResourceRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID));
		return loModelAndView;
	}

	/**
	 * The Method is used for Canceling the Evaluation Tasks
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1.Fetch and Validate the credential</li>
	 * <li>2. If Authentication fails display the error message on UI</li>
	 * <li>3. Else Call Transaction cancelEvaluationTasks</li>
	 * <li>4.Will call service to check whether All evaluation task workflows
	 * are terminated</li>
	 * <li>5.if 4 is true delete Evaluation Setting Data using service
	 * deleteEvaluationSettingData</li>
	 * <li>6.Cancel Evaluation button S215.10 will be removed and Send
	 * Evaluation Tasks button S215.04 will be added to S215</li>
	 * <li>Execute the Transaction <b>cancelEvaluationTasks</b></li>
	 * <li>7.Total Evaluations Complete S215.06 and Total Evaluations in
	 * progress S215.07 will be removed from S215</li>
	 * </ul>
	 * @param aoAuthenticationBean AuthenticationBean
	 * @param aoResult BindingResult
	 * @param aoRequest ActionRequest
	 * @param aoResponse ActionResponse
	 */
	@SuppressWarnings("rawtypes")
	@ActionMapping(params = "submit_action=cancelEvaluationTasks")
	protected void cancelEvaluationTasksAction(
			@ModelAttribute("AuthenticationBean") AuthenticationBean aoAuthenticationBean, BindingResult aoResult,
			ActionRequest aoRequest, ActionResponse aoResponse)
	{
		HashMap<String, String> loHmReqExceProp = new HashMap<String, String>();
		String lsProcurementId = aoRequest.getParameter(HHSConstants.PROCUREMENT_ID);
		String lsEvalPoolMappingId = PortalUtil.parseQueryString(aoRequest, HHSConstants.EVALUATION_POOL_MAPPING_ID);
		P8UserSession loUserSession = null;
		HashMap<String, Object> loCancelEvaluationTaskMap = new HashMap<String, Object>();
		String lsWorkflowName = null;
		try
		{
			LOG_OBJECT.Debug("Entered into cancelEvaluationTasksAction::" + loHmReqExceProp.toString());
			loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			validator.validate(aoAuthenticationBean, aoResult);
			Map loAuthenticateMap = validateUser(aoAuthenticationBean.getUserName(),
					aoAuthenticationBean.getPassword(), aoRequest);
			Boolean loAuthStatus = (Boolean) loAuthenticateMap.get(HHSConstants.IS_VALID_USER);
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			// if Authentication Fails ,Set the Error Message
			if (!loAuthStatus)
			{
				aoResponse
						.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
								HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.MESSAGE_M38));
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.ERROR_PAGE_CANCEL_EVALUATION);
				aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
			}
			else
			{
				Channel loChannel = new Channel();
				lsWorkflowName = P8Constants.PE_EVALUATION_UTILITY_WORKFLOW_NAME;
				loCancelEvaluationTaskMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, lsProcurementId);
				loCancelEvaluationTaskMap.put(P8Constants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolMappingId);
				loCancelEvaluationTaskMap.put(P8Constants.ACTION_KEY_FOR_UTILITY_WORKFLOW,
						P8Constants.CANCEL_EVALUATION_ACTION_FOR_UTILITY_WORKFLOW);
				loCancelEvaluationTaskMap.put(P8Constants.LAUNCHED_BY, lsUserId);
				loChannel.setData(
						HHSConstants.USER_ID,
						aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
								PortletSession.APPLICATION_SCOPE));
				loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
				loChannel.setData(HHSConstants.LO_WORKFLOW_NAME, lsWorkflowName);
				loChannel.setData(HHSConstants.AO_HMWF_REQUIRED_PROPS, loCancelEvaluationTaskMap);
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.CANCEL_EVALUATION_TASKS);
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.CANCEL_EVALUATION_TASK_SUCESS));
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_PASS_TYPE);

			}
			// Handling Application Exception
		}
		catch (ApplicationException aoEx)
		{
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loHmReqExceProp, aoEx);
			LOG_OBJECT.Error("Exception Occured while Cancel Evaluation Tasks:", aoEx);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.ERROR_PAGE_CANCEL_EVALUATION);
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
		}
		// Handling Exceptions other than Application Exception
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while Cancel Evaluation Tasks:", aoEx);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.ERROR_PAGE_CANCEL_EVALUATION);
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
		}
	}

	/**
	 * This method is used to render the error page when credentials are wrong
	 * while closing a procurement.
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. Get user name from closeProcurement jsp and set in request.</li>
	 * </ul>
	 * 
	 * @param aoRequest - RenderRequest
	 * @param aoResponse - RenderResponse
	 * @return - ModelAndView containing the JSP sendEvaluationTasks.jsp
	 */
	@RenderMapping(params = "render_action=errorPageClose")
	protected ModelAndView renderCloseProcurement(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		ModelAndView loModelAndView = null;
		try
		{
			loModelAndView = new ModelAndView(HHSConstants.SEND_EVALUATION_TASK_JSP);
			aoRequest.setAttribute(HHSConstants.CLOSE_PROC_ID, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
					aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE));
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE_TYPE));
		}
		catch (Exception aoException)
		{
			setGenericErrorMessage(aoRequest);
			LOG_OBJECT.Error("Exception Occured while rendering Evalution Status", aoException);
		}
		return loModelAndView;
	}

	/**
	 * This method is used to render the error page when credentials are wrong
	 * while cancelling evaluations.
	 * 
	 * <ul>
	 * <li>1. Get user name from cancel Evaluation Task jsp and set in request.</li>
	 * </ul>
	 * 
	 * @param aoRequest - RenderRequest
	 * @param aoResponse - RenderResponse
	 * @return - ModelAndView(cancelEvaluationTasks.jsp)
	 */
	@RenderMapping(params = "render_action=errorPageCancelEvaluation")
	protected ModelAndView renderCancelEvaluations(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		ModelAndView loModelAndView = null;
		try
		{
			loModelAndView = new ModelAndView(HHSConstants.CANCEL_EVALUATION_TASKS);
			aoRequest.setAttribute(HHSConstants.PROCUREMENT_ID, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
					aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE));
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE_TYPE));
		}
		catch (Exception aoException)
		{
			setGenericErrorMessage(aoRequest);
			LOG_OBJECT.Error("Exception Occured while rendering Evalution Status", aoException);
		}

		return loModelAndView;
	}

	/**
	 * This method will download the dbd docs for the procurement
	 * <ul>
	 * <li>Retrieve the procurement id, user id, evaluation pool mapping id from
	 * request. session respectively</li>
	 * <li>Call transaction <b>downloadDBDProcess</b> which will process the
	 * request and will generate the zip file</li>
	 * <li>Retrieve abFetchDBDDocs flag(depecting whether user can download the
	 * zip file), file path of zip file</li>
	 * <li>if user is not allowed to download the zip, show an error to user</li>
	 * <li>set output parameters into request</li>
	 * @param aoRequest - resource request
	 * @param aoResponse - resource response
	 * @throws ApplicationException if any exception occurred
	 */
	@ResourceMapping("getDBDDocs")
	public void getDBDDocsResourceRequest(ResourceRequest aoRequest, ResourceResponse aoResponse)
	{
		String lsErrorMsg = null;
		String lsZipFilePath = null;
		PrintWriter loOut = null;
		Boolean lsIsFinacialDocRequired = false;
		try
		{
			loOut = aoResponse.getWriter();
			String lsProcurementId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
			String lsEvaluationPoolMappingId = HHSPortalUtil.parseQueryString(aoRequest,
					HHSConstants.EVALUATION_POOL_MAPPING_ID);
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			PortletContext loContext = aoRequest.getPortletSession().getPortletContext();
			String lsContextPath = loContext.getRealPath(HHSConstants.DBD_DOC_REAL_PATH);
			Channel loChannel = new Channel();
			loChannel.setData(HHSConstants.IS_FINANCIAL, lsIsFinacialDocRequired);
			loChannel.setData(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
			loChannel.setData(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvaluationPoolMappingId);
			loChannel.setData(HHSConstants.USER_ID, lsUserId);
			loChannel.setData(
					HHSConstants.AS_ORG_TYPE,
					aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ROLE,
							PortletSession.APPLICATION_SCOPE));
			loChannel.setData(HHSConstants.CONTEXT_PATH1, lsContextPath);
			P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
			HashMap<String, String> loRequiredParamMap = new HashMap<String, String>();
			loRequiredParamMap.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, ApplicationConstants.EMPTY_STRING);
			loChannel.setData(ApplicationConstants.REQ_PROPS_DOCUMENT, loRequiredParamMap);
			loChannel.setData(HHSConstants.FOLDER_NAME, HHSConstants.DBD_DOCUMENTS);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.DOWNLOAD_DB_PROCESS);
			lsZipFilePath = (String) loChannel.getData(HHSConstants.ZIP_PATH);
			if (lsZipFilePath == null)
			{
				lsErrorMsg = HHSConstants.DBD_MSG1;
			}
		}
		catch (IOException aoExp)
		{
			LOG_OBJECT.Error("Error occured while fetching DBD Docs", aoExp);
			setGenericErrorMessage(aoRequest);
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error occured while fetching DBD Docs", aoExp);
			setGenericErrorMessage(aoRequest);
		}
		finally
		{
			showDownloadDialogOrErrorForZip(aoResponse, lsErrorMsg, lsZipFilePath, loOut);
		}
	}

	/**
	 * The action Method is used when Filter action is performed on Evaluation
	 * Status Screen Here we will be setting data which will be required to
	 * fetch Evaluation Status data on the basis of Filter
	 * <ul>
	 * <li>1.Once the Filter Search is performed filter search parameter will
	 * bind with EvaluationBean</li>
	 * <li>2.Setting Sorting Related Parameter</li>
	 * <li>3.Setting EvaluationBean in Session</li>
	 * <li>4.Setting Filter Variable in request i.e to identify we are coming
	 * from Filter Screen</li>
	 * <li>5.Setting Render parameter Required for rendering getEvaluationStatus
	 * render method</li>
	 * </ul>
	 * @param aoEvaluationBean EvaluationBean
	 * @param aoResult BindingResult
	 * @param aoRequest ActionRequest
	 * @param aoResponse ActionResponse
	 */
	@ActionMapping(params = "submit_action=filterEvaluation")
	protected void actionFilterEvaluationStatus(@ModelAttribute("EvaluationBean") EvaluationBean aoEvaluationBean,
			BindingResult aoResult, ActionRequest aoRequest, ActionResponse aoResponse)
	{
		try
		{
			LOG_OBJECT.Debug("Entered into Evaluation Status Filter Action::");
			String lsHideExitProcurement = aoRequest.getParameter(HHSConstants.HIDE_EXIT_PROCUREMENT);
			// Setting Sorting Related Parameter
			aoEvaluationBean.setFirstSort(HHSConstants.EVALUATION_COMPLETED);
			aoEvaluationBean.setSecondSort(HHSConstants.ORGANIZATION_NAME);
			aoEvaluationBean.setFirstSortType(HHSConstants.ASCENDING);
			aoEvaluationBean.setSecondSortType(HHSConstants.ASCENDING);
			aoEvaluationBean.setSortColumnName(HHSConstants.EVALUATIONS_COMPLETED);
			aoEvaluationBean.setFirstSortDate(false);
			aoEvaluationBean.setSecondSortDate(false);

			aoRequest.getPortletSession().setAttribute(HHSConstants.EVALUATION_SESSION_BEAN, aoEvaluationBean,
					PortletSession.PORTLET_SCOPE);
			aoRequest.getPortletSession().setAttribute(HHSConstants.PROPOSAL_FILTERED, HHSConstants.PROPOSAL_FILTERED,
					PortletSession.PORTLET_SCOPE);

			aoResponse.setRenderParameter(HHSConstants.FILTERED, HHSConstants.FILTERED);
			aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID,
					aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			aoResponse.setRenderParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID,
					aoRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID));
			aoResponse.setRenderParameter(HHSConstants.EVALUATION_GROUP_ID,
					aoRequest.getParameter(HHSConstants.EVALUATION_GROUP_ID));
			// Setting Parameter for Successfully Rendering of
			// GetEvaluationStatus
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.GET_EVALUATION_STATUS);
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
			aoResponse.setRenderParameter(HHSConstants.TOP_LEVEL_FROM_REQ,
					aoRequest.getParameter(HHSConstants.TOP_LEVEL_FROM_REQ));
			aoResponse.setRenderParameter(HHSConstants.MID_LEVEL_FROM_REQ,
					aoRequest.getParameter(HHSConstants.MID_LEVEL_FROM_REQ));
			if (null != lsHideExitProcurement)
			{
				aoResponse.setRenderParameter(HHSConstants.HIDE_EXIT_PROCUREMENT, lsHideExitProcurement);
			}
		}
		// handling ApplicationException while processing filter action for
		// evaluation status page.
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Exception Occured while Filter Evaluation Status:", aoExp);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);

		}
		// handling exception while processing filter action for evaluation
		// status page.
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception Occured while Filter Evaluation Status:", aoExp);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);

		}
	}

	/**
	 * This method used to fetch the Provider Name for the Evaluation Status
	 * Filter
	 * <ul>
	 * <li>1. Get the required data from request and set in Channel</li>
	 * <li>2.Execute the transaction "fetchProviderNames" to get the provider
	 * Name list</li>
	 * <li>3.Set the results of the transaction in the json</li>
	 * </ul>
	 * 
	 * @param aoResourceRequest - resource request of the portal
	 * @param aoResourceResponse - resource response of the portal
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	@ResourceMapping("getProviderNameList")
	public void fetchProviderNameList(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		final String lsInputParam = aoResourceRequest.getParameter(HHSConstants.QUERY);
		final Map<String, Object> loHmReqExceProp = new HashMap<String, Object>();
		PrintWriter loOut = null;
		try
		{
			LOG_OBJECT.Debug("Entered into Fetch Provider Name List::");
			if (lsInputParam != null && lsInputParam.length() >= Integer.parseInt(HHSConstants.THREE))
			{
				Channel loChannel = new Channel();
				loChannel.setData(HHSConstants.PROVIDER_NAME_PARAM, lsInputParam);
				loHmReqExceProp.put(HHSConstants.PROVIDER_NAME_PARAM, lsInputParam);
				// hit the transaction to get the provider name list
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_PROVIDER_NAMES);
				final List<String> loProviderNameList = (List<String>) loChannel
						.getData(HHSConstants.PROVIDER_NAME_LIST);
				if (loProviderNameList != null)
				{
					loOut = aoResourceResponse.getWriter();
					aoResourceResponse.setContentType(HHSConstants.APPLICATION_JSON);
					final String lsOutputJSONaoResponse = HHSUtil
							.generateDelimitedResponse(loProviderNameList, lsInputParam,
									Integer.parseInt(HHSConstants.THREE)).toString().trim();
					loOut.print(lsOutputJSONaoResponse);
				}
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception Occured while getting the provider name list from the database", aoAppEx);
			setGenericErrorMessage(aoResourceRequest);
		}
		// handling exception other than Application Exception.
		catch (Exception aoException)
		{
			LOG_OBJECT.Error("Exception other than application exception occured while getting the provider name list"
					+ "from the database", aoException);
			setGenericErrorMessage(aoResourceRequest);
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
	 * This method will display pop up for Send Evaluation Tasks screen when
	 * "Send Evaluation Tasks" button is clicked on screen 215 - Evaluation
	 * Settings
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Retrieve the procurement id, evaluation pool mapping id, competition
	 * pool id, evaluation group id from the request and set in the request
	 * attribute</li>
	 * <li>display JSP sendEvaluationTasks</li>
	 * </ul>
	 * This method was modified for defect#6498 in Release 3.6.0 Procurement
	 * Title is fetched by Id for this defect
	 * 
	 * @param aoResourceRequest - ResourceRequest
	 * @param aoResourceResponse - ResourceResponse
	 * @return ModelAndView containing jsp name(sendEvaluationTasks.jsp)
	 * @throws ApplicationException if any exception occurred
	 */
	@ResourceMapping("sendEvaluationTasksOverlay")
	protected ModelAndView getsendEvaluationTasksOverlay(ResourceRequest aoResourceRequest,
			ResourceResponse aoResourceResponse)
	{
		ModelAndView loModelAndView = new ModelAndView(HHSConstants.SEND_EVALUATION_TASK_JSP);
		aoResourceRequest.setAttribute(HHSConstants.SEND_EVALUATION_ID,
				aoResourceRequest.getParameter(HHSConstants.PROCUREMENT_ID));
		aoResourceRequest.setAttribute(HHSConstants.EVALUATION_POOL_MAPPING_ID,
				aoResourceRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID));
		aoResourceRequest.setAttribute(HHSConstants.COMPETITION_POOL_ID,
				aoResourceRequest.getParameter(HHSConstants.COMPETITION_POOL_ID));
		aoResourceRequest.setAttribute(HHSConstants.EVALUATION_GROUP_ID,
				aoResourceRequest.getParameter(HHSConstants.EVALUATION_GROUP_ID));
		try
		{
			aoResourceRequest.setAttribute(HHSConstants.PROCUREMENT_TITLE,
					getProcurementTitle(aoResourceRequest.getParameter(HHSConstants.PROCUREMENT_ID)));
		}
		catch (ApplicationException e)
		{
			LOG_OBJECT.Error("Exception Occured while getting the procurement title from the database", e);
			setGenericErrorMessage(aoResourceRequest);
		}
		return loModelAndView;
	}

	/**
	 * The Method gets the procurement title from id Its added as part of
	 * defect#6498 in Release 3.6.0
	 * 
	 * @param aoProcurementId String
	 * @return lsProcurementTitle String
	 * @throws ApplicationException if any exception occurred
	 */
	private String getProcurementTitle(String asProcurementId) throws ApplicationException
	{
		// Changes made for QC Defect 6530
		Channel loChannelObj = new Channel();
		loChannelObj.setData(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.GET_PROC_TITLE);
		String lsProcurementTitle = (String) loChannelObj.getData(HHSConstants.AS_PROCUREMENT_TITLE);
		return StringEscapeUtils.escapeHtml(lsProcurementTitle);
	}

	/**
	 * The Method allows a user to send evaluation tasks to assigned evaluators
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Fetch the user id from session</li>
	 * <li>Validate the user.</li>
	 * <li>If user is not a valid user, error message is displayed on the page.</li>
	 * <li>If user is a valid user</li>
	 * <li>Populate input parameter map to pass it to the transaction framework.
	 * </li>
	 * <li>Set the required parameters in the channel object to pass them to the
	 * transaction framework.</li>
	 * <li>Set the work flow related parameters in the channel object</li>
	 * <li>Execute the Transaction <b>sendEvaluationTasksDetails</b></li>
	 * <li>Transaction layer executes sendEvaluationTasksDetails transaction,
	 * calls the service to save the number of evaluator, and puts the result
	 * back in the channel object.</li>
	 * <li>Add the successful message o the request to display it on the JSP.</li>
	 * </ul>
	 * 
	 * @param aoAuthenticationBean AuthenticationBean
	 * @param aoResult BindingResult
	 * @param aoRequest ActionRequest
	 * @param aoResponse ActionResponse
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@ActionMapping(params = "submit_action=sendEvaluation")
	protected void sendEvaluation(@ModelAttribute("AuthenticationBean") AuthenticationBean aoAuthenticationBean,
			BindingResult aoResult, ActionRequest aoRequest, ActionResponse aoResponse)
	{
		Channel loChannelObj = new Channel();
		HashMap<String, String> loProcMap = new HashMap<String, String>();
		String lsProcurementId = PortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
		String lsEvalPoolMappingId = PortalUtil.parseQueryString(aoRequest, HHSConstants.EVALUATION_POOL_MAPPING_ID);
		try
		{
			// fetching the user id from session
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			// validating the user.
			validator.validate(aoAuthenticationBean, aoResult);
			Map loAuthenticateMap = validateUser(aoAuthenticationBean.getUserName(),
					aoAuthenticationBean.getPassword(), aoRequest);
			Boolean loAuthStatus = (Boolean) loAuthenticateMap.get(HHSConstants.IS_VALID_USER);
			// if user is no a valid user, error message is displayed on the
			// page.
			if (!loAuthStatus)
			{
				aoResponse
						.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
								HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.MESSAGE_M38));
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.ERROR_PAGE_CLOSE);
				aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
			}// if user is a valid user
			else
			{
				// preparing input parameter map to pass it to the transaction
				// framework
				loProcMap.put(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
				loProcMap.put(HHSConstants.LS_USER_ID, lsUserId);
				loProcMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolMappingId);
				// setting the required parameters in the channel object to pass
				// them to the transaction framework.
				loChannelObj.setData(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
				loChannelObj.setData(HHSConstants.PROC_MAP, loProcMap);
				loChannelObj.setData(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolMappingId);
				HashMap loHmReqProps = new HashMap();
				loHmReqProps.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, lsProcurementId);
				loHmReqProps.put(P8Constants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolMappingId);
				loHmReqProps.put(P8Constants.PE_WORKFLOW_PROCUREMENT_TITLE,
						aoRequest.getParameter(HHSConstants.PROCUREMENT_TITLE));
				loChannelObj.setData(HHSConstants.AO_HMWF_REQUIRED_PROPS, loHmReqProps);
				// setting the work flow related parameters in the channel
				// object
				loChannelObj.setData(HHSConstants.LO_WORKFLOW_NAME,
						P8Constants.PE_EVALUATION_PROPOSAL_MAIN_WORKFLOW_NAME);
				loChannelObj.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
				// added for audit
				Map<String, Object> loInputParamMap = new HashMap<String, Object>();
				loInputParamMap.put(HHSConstants.EVENT_NAME, HHSConstants.SEND_EVALUATION_TASKS);
				loInputParamMap.put(HHSConstants.EVENT_TYPE, HHSConstants.SEND_EVALUATION_TASKS);
				loInputParamMap.put(HHSConstants.USER_ID, lsUserId);
				loInputParamMap.put(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
				loInputParamMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolMappingId);
				loChannelObj.setData(HHSConstants.INPUT_PARAM_MAP, loInputParamMap);
				// hitting the transaction layer to update the required table
				// transaction layer executes the transaction and puts the
				// result back in the channel object.
				HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.SEND_EVALUATION_TASKS_DETAILS);
				// Change log 523
				aoRequest.getPortletSession().setAttribute(HHSConstants.EVALUATION_TASK_SENT,
						loChannelObj.getData(HHSConstants.LB_UPDATE_EVALUATION_STATUS));
				// adding the successful message in the request
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.SEND_EVALUATION_SUCCESSFUL));
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_PASS_TYPE);
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoEx)
		{
			aoEx.setContextData(loProcMap);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.ERROR_PAGE_CLOSE);
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
			LOG_OBJECT.Error("Error occurred while sending evaluation task details", aoEx);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loProcMap, aoEx);
		}
		// handling exception other than Application Exception.
		catch (Exception aoException)
		{
			LOG_OBJECT.Error("Error occurred while sending evaluation task details", aoException);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.ERROR_PAGE_CLOSE);
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
		}
	}

	/**
	 * This method will handle paginate evaluation action from evaluation Status
	 * screen and will render to fetchEvaluationResultsSelections Render Method
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1.Call setNavigationParamsInRender for Setting Navigation Render
	 * Parameter</li>
	 * <li>2.Setting parameter required for rendering of
	 * fetchEvaluationResultsSelections Screen Rendering in ActionResponse
	 * object</li>
	 * </ul>
	 * @param aoRequest action request object
	 * @param aoResponse action response object
	 */
	@ActionMapping(params = "submit_action=pagingEvalutionResults")
	protected void actionPaginateEvaluationResults(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		setNavigationParamsInRender(aoRequest, aoResponse);
		aoResponse.setRenderParameter(HHSConstants.NEXT_PAGE_PARAM, aoRequest.getParameter(HHSConstants.NEXT_PAGE));
		aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.FETCH_EVAL_RESUTS);
		aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
		aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
		aoResponse.setRenderParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID,
				aoRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID));
		aoResponse.setRenderParameter(HHSConstants.EVALUATION_GROUP_ID,
				aoRequest.getParameter(HHSConstants.EVALUATION_GROUP_ID));
		aoResponse.setRenderParameter(HHSConstants.COMPETITION_POOL_ID,
				aoRequest.getParameter(HHSConstants.COMPETITION_POOL_ID));
	}

	/**
	 * This method generates notification map for Accept Proposal task depending
	 * upon input alert list
	 * 
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
	 * @param aoRequest ActionRequest object
	 * @param aoAlertList a list of notification ID to be sent like NT213
	 * @return a notification hashmap
	 * @throws ApplicationException
	 */
	private HashMap<String, Object> getNotificationMapForProposalTask(ActionRequest aoRequest, List<String> aoAlertList)
			throws ApplicationException
	{
		HashMap<String, Object> loNotificationMapForProposal = new HashMap<String, Object>();
		loNotificationMapForProposal.put(HHSConstants.NOTIFICATION_ALERT_ID, aoAlertList);
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		try
		{
			String lsServerName = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
					HHSConstants.SERVER_NAME_FOR_PROVIDER_BATCH);
			String lsServerPort = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
					HHSConstants.SERVER_PORT_FOR_PROVIDER_BATCH);
			String lsContextPath = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
					HHSConstants.CONTEXT_PATH_FOR_PROVIDER_BATCH);
			String lsAppProtocol = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
					HHSConstants.SERVER_PROTOCOL_FOR_PROVIDER_BATCH);
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			for (String lsALertId : aoAlertList)
			{
				NotificationDataBean loNotificationDataBean = new NotificationDataBean();
				HashMap<String, String> loLinkMap = new HashMap<String, String>();
				StringBuffer lsBfApplicationUrl = new StringBuffer(256);
				lsBfApplicationUrl.append(lsAppProtocol).append(HHSConstants.NOTIFICATION_HREF_1).append(lsServerName)
						.append(HHSConstants.COLON).append(lsServerPort).append(HHSConstants.FORWARD_SLASH)
						.append(lsContextPath).append(HHSConstants.PROPOSAL_SUMMARY_URL)
						.append(aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
				loLinkMap.put(HHSConstants.LINK, lsBfApplicationUrl.toString());
				loNotificationDataBean.setLinkMap(loLinkMap);
				loNotificationDataBean.setAgencyLinkMap(loLinkMap);
				loNotificationMapForProposal.put(lsALertId, loNotificationDataBean);
			}
			loNotificationMapForProposal.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
			loNotificationMapForProposal.put(ApplicationConstants.ENTITY_ID,
					aoRequest.getParameter(HHSConstants.PROPOSAL_ID));
			loNotificationMapForProposal.put(ApplicationConstants.ENTITY_TYPE, HHSConstants.PROPOSAL);
			loNotificationMapForProposal.put(HHSConstants.CREATED_BY_USER_ID, lsUserId);
			loNotificationMapForProposal.put(HHSConstants.MODIFIED_BY, lsUserId);
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Error was occurred while get notfication for getNotificationMapForProposalTask", aoEx);
			throw aoEx;
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error was occurred while get notfication for getNotificationMapForProposalTask", aoEx);
			throw new ApplicationException(
					"Error was occurred while get notfication for getNotificationMapForProposalTask", aoEx);
		}
		return loNotificationMapForProposal;
	}

	/**
	 * The Method is used for closing the submissions
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Fetch the user id from session</li>
	 * <li>Validate the user.</li>
	 * <li>If user is not a valid user, error message is displayed on the page.</li>
	 * <li>If user is a valid user</li>
	 * <li>Populate the map for work flow integration</li>
	 * <li>Populated the HhsAuditBean to pass it to the transaction layer to
	 * make entries in the Audit table</li>
	 * <li>Populate input parameter map to pass it to the transaction layer to
	 * make entries in the procurement table.</li>
	 * <li>Set the required parameters in the channel object to pass them to the
	 * transaction framework.</li>
	 * <li>Hit the transaction layer to update the required tables</li>
	 * <li>Transaction layer executes the transaction and puts the result back
	 * in the channel object.</li>
	 * <li>Fetch the result of the transaction from the channel object</li>
	 * <li>If the result is success full, display the success full message on
	 * the jsp.</li>
	 * </ul>
	 * 
	 * @param aoAuthenticationBean AuthenticationBean
	 * @param aoResult BindingResult
	 * @param aoRequest ActionRequest
	 * @param aoResponse ActionResponse
	 */
	@SuppressWarnings("rawtypes")
	@ActionMapping(params = "submit_action=closeSubmissionsAction")
	protected void closeSubmissions(AuthenticationBean aoAuthenticationBean, BindingResult aoResult,
			ActionRequest aoRequest, ActionResponse aoResponse)
	{
		Channel loChannelObj = new Channel();
		// fetching the user id from session
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsButtonValue = aoRequest.getParameter(HHSConstants.BUTTON_VALUE);
		P8UserSession loUserSession = null;
		HashMap<String, String> loLaunchWorkflowMap = new HashMap<String, String>();
		try
		{
			// validating the user.
			validator.validate(aoAuthenticationBean, aoResult);
			Map loAuthenticateMap = validateUser(aoAuthenticationBean.getUserName(),
					aoAuthenticationBean.getPassword(), aoRequest);
			Boolean loAuthStatus = (Boolean) loAuthenticateMap.get(HHSConstants.IS_VALID_USER);
			loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			// if user is no a valid user, error message is displayed on the
			// page.
			if (!loAuthStatus)
			{
				aoResponse
						.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
								HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.MESSAGE_M38));
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.ERROR_PAGE_CLOSE_SUBMISSIONS);
				aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
			}
			// if user is a valid user
			else
			{
				closeSubmissionForAuthenticatedUser(aoAuthenticationBean, aoRequest, aoResponse, loChannelObj,
						lsUserId, loUserSession, loLaunchWorkflowMap);
			}
		}
		// AppicationException thrown from the Transaction framework are handled
		// here
		catch (ApplicationException aoEx)
		{
			// populating context data map for exceptional handling
			Map<String, String> loParamMap = new HashMap<String, String>();
			loParamMap.put(HHSConstants.PROCUREMENT_ID, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			String lsErrorMsg = HHSConstants.ERROR_WHILE_PROCESSING_REQUEST;
			setExceptionMessageFromAction(aoResponse, lsErrorMsg, ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap,
					aoEx);
			LOG_OBJECT.Error("Exception Occured while updating procurement details for close submissions : ", aoEx);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.ERROR_PAGE_CLOSE_SUBMISSIONS);
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while updating procurement details for close submissions : ", aoEx);
			String lsErrorMsg = HHSConstants.ERROR_WHILE_PROCESSING_REQUEST;
			setExceptionMessageFromAction(aoResponse, lsErrorMsg, ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.ERROR_PAGE_CLOSE_SUBMISSIONS);
		}
		aoResponse.setRenderParameter(HHSConstants.BUTTON_VALUE, lsButtonValue);
	}

	/**
	 * The Method is used for cancelling the competition pool Changes done for
	 * Enhancement #6577 for Release 3.10.0
	 * <ul>
	 * <li>Fetch the user id from session</li>
	 * <li>Validate the user.</li>
	 * <li>If user is not a valid user, error message is displayed on the page.</li>
	 * <li>If user is a valid user</li>
	 * <li>Call the transaction</li>
	 * <li>Set the required parameters in the channel object to pass them to the
	 * transaction framework.</li>
	 * <li>Hit the transaction layer to update the required tables</li>
	 * <li>Transaction layer executes the transaction and puts the result back
	 * in the channel object.</li>
	 * <li>Fetch the result of the transaction from the channel object</li>
	 * <li>If the result is success full, display the success full message on
	 * the jsp.</li>
	 * </ul>
	 * 
	 * @param aoAuthenticationBean AuthenticationBean
	 * @param aoResult BindingResult
	 * @param aoRequest ActionRequest
	 * @param aoResponse ActionResponse
	 */
	@SuppressWarnings("rawtypes")
	@ActionMapping(params = "submit_action=cancelCompetitionAction")
	protected void cancelCompetitionAction(AuthenticationBean aoAuthenticationBean, BindingResult aoResult,
			ActionRequest aoRequest, ActionResponse aoResponse)
	{
		// Work flow integration for terminating all the related work flows
		P8UserSession loUserSession = null;
		HashMap<String, String> loHmReqExceProp = new HashMap<String, String>();
		Map<String, String> loStatusInfoMap = new HashMap<String, String>();
		try
		{
			LOG_OBJECT.Debug("Entered into cancelCompetitionAction::");
			validator.validate(aoAuthenticationBean, aoResult);
			Map loAuthenticateMap = validateUser(aoAuthenticationBean.getUserName(),
					aoAuthenticationBean.getPassword(), aoRequest);
			Boolean loAuthStatus = (Boolean) loAuthenticateMap.get(HHSConstants.IS_VALID_USER);
			// Work flow integration for terminating all the related work flows
			loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			// if Authentication Fails ,Set the Error Message
			if (!loAuthStatus)
			{
				aoResponse
						.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
								HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.MESSAGE_M38));
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.ERROR_PAGE_CLOSE);

			}// if authentication passes
			else
			{
				cancelCompetitionFinal(aoRequest, aoResponse, loUserSession, loStatusInfoMap);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.CANCEL_COMPETITION);
				aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
			}
		}
		// AppicationException thrown from the Transaction framework are handled
		// here
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while Cancel Competition:", aoEx);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loHmReqExceProp, aoEx);
			aoEx.setContextData(loStatusInfoMap);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.ERROR_PAGE_CLOSE);
		}
		// Handling Exceptions other than Application Exception
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while Cancel Competition:", aoEx);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.ERROR_PAGE_CLOSE);
		}
	}

	/**
	 * Added for Enhancement #6577 for Release 3.10.0 The method will handle
	 * cancel competition
	 * <ul>
	 * <li>1.Set the required data in Channel</li>
	 * <li>2.Call transaction cancelCompetition</li>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest
	 * @param loUserSession user session
	 * @param loLaunchWorkflowMap input map for workflow launch
	 * @param loStatusInfoMap input param map
	 * @param loParamMap input param map
	 * @throws ApplicationException If an exception occurs
	 */
	private void cancelCompetitionFinal(ActionRequest aoRequest, ActionResponse aoResponse,
			P8UserSession loUserSession, Map<String, String> loStatusInfoMap) throws ApplicationException
	{
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		String lsProcurementId = aoRequest.getParameter(HHSConstants.PROCUREMENT_ID);
		String lsEvaluationGroupId = aoRequest.getParameter(HHSConstants.EVALUATION_GROUP_ID);
		String lsEvalPoolMappingId = aoRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID);
		String lsCompetitionPoolId = aoRequest.getParameter(HHSConstants.COMPETITION_POOL_ID);
		String lsComments = aoRequest.getParameter(HHSConstants.COMMENTS);
		// Start || Changes done for Enhancement #6577 for Release 3.10.0
		HashMap<String, Object> loCancelCompWorkflowMap = new HashMap<String, Object>();
		loCancelCompWorkflowMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, lsProcurementId);
		loCancelCompWorkflowMap.put(
				P8Constants.LAUNCHED_BY,
				(String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
						PortletSession.APPLICATION_SCOPE));
		loCancelCompWorkflowMap.put(P8Constants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolMappingId);
		loCancelCompWorkflowMap.put(P8Constants.ACTION_KEY_FOR_UTILITY_WORKFLOW, P8Constants.CANCEL_COMPETITION);
		// End || Changes done for Enhancement #6577 for Release 3.10.0

		// populate the map with the required parameters
		loStatusInfoMap.put(HHSConstants.PROCUREMENT_ID, lsProcurementId);
		loStatusInfoMap.put(HHSConstants.EVALUATION_GROUP_ID, lsEvaluationGroupId);
		loStatusInfoMap.put(HHSConstants.COMPETITION_POOL_ID, lsCompetitionPoolId);
		loStatusInfoMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolMappingId);
		loStatusInfoMap.put(HHSConstants.COMMENTS, lsComments);
		loStatusInfoMap.put(
				HHSConstants.USER_ID,
				(String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
						PortletSession.APPLICATION_SCOPE));
		loNotificationMap = getNotificationMapForCancelComp(loStatusInfoMap);
		Channel loChannel = new Channel();
		// set the map in the channel object
		loChannel.setData(HHSConstants.LO_NOTIFICATION_MAP, loNotificationMap);
		loChannel.setData(HHSConstants.STATUS_INFO_MAP, loStatusInfoMap);
		loChannel.setData(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolMappingId);
		loChannel.setData(HHSConstants.PROCUREMENT_ID, lsProcurementId);
		loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
		String lsEventName = HHSConstants.CHANGE_STATUS;
		String lsEventType = HHSConstants.CANCEL_COMP;
		String lsData = HHSConstants.CANCEL_COMP_AUDIT_MSG + lsCompetitionPoolId;
		String lsEntityType = HHSConstants.CANCEL_COMP;
		String lsEntityId = lsEvalPoolMappingId;
		String lsUserID = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsTableIdentifier = HHSConstants.ACCELERATOR_AUDIT;
		HhsAuditBean loAuditBean = CommonUtil.addAuditDataToChannel(lsEventName, lsEventType, lsData, lsEntityType,
				lsEntityId, lsUserID, lsTableIdentifier);
		loChannel.setData(HHSConstants.AUDIT_BEAN, loAuditBean);
		// Start || Changes done for Enhancement #6577 for Release 3.10.0
		loChannel.setData(HHSConstants.WORK_FLOW_NAME, P8Constants.PE_EVALUATION_UTILITY_WORKFLOW_NAME);
		loChannel.setData(HHSConstants.AO_HMWF_REQUIRED_PROPS, loCancelCompWorkflowMap);
		// End || Changes done for Enhancement #6577 for Release 3.10.0

		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.CANCEL_COMPETITION);
		String loCancelStatusFlag = (String) loChannel.getData(HHSConstants.CANCEL_STATUS_FLAG);
		LOG_OBJECT.Debug("loCancelStatusFlag while Cancel Competition:" + loCancelStatusFlag);
		aoResponse.setRenderParameter(HHSConstants.CANCEL_STATUS_FLAG, loCancelStatusFlag);
	}

	/**
	 * This method is added for Release 3.10.0 for enhancement request #6577.
	 * The method sets notification parameter map for Notification for cancel
	 * comps
	 * @param loStatusInfoMap Map<String, String>
	 * @return loStatusInfoMap HashMap<String, Object>
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked" })
	private HashMap<String, Object> getNotificationMapForCancelComp(Map<String, String> loStatusInfoMap)
			throws ApplicationException
	{
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		try
		{
			if (null != loStatusInfoMap)
			{
				Channel loChannel = new Channel();
				NotificationDataBean loNotificationDataBean = new NotificationDataBean();
				List<String> loNotificationAlertList = new ArrayList<String>();
				loNotificationAlertList.add(HHSConstants.NT_405);
				loNotificationAlertList.add(HHSConstants.AL_405);
				loChannel.setData(HHSConstants.STATUS_INFO_MAP, loStatusInfoMap);
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_PROVIDERS_IN_COMPETITION);
				HashMap<String, String> aoModifiedInfoMap = (HashMap<String, String>) loChannel
						.getData(HHSConstants.AO_MODIFIED_INFO_MAP);
				HashMap<String, String> loLinkMap = new HashMap<String, String>();
				List<String> loProviderIdList = (List<String>) loChannel.getData(HHSConstants.PROVIDER_ID_LIST);
				loNotificationDataBean.setProviderList(loProviderIdList);
				loNotificationDataBean.setLinkMap(loLinkMap);
				loNotificationMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
				loNotificationMap.put(HHSConstants.NT_405, loNotificationDataBean);
				loNotificationMap.put(HHSConstants.AL_405, loNotificationDataBean);
				loRequestMap.put(HHSConstants.NT_PROCUREMENT_TITLE,
						aoModifiedInfoMap.get(HHSConstants.NT_PROCUREMENT_TITLE));
				loRequestMap.put(HHSConstants.COMPETITION_TITLE,
						aoModifiedInfoMap.get(HHSConstants.COMPETITION_POOL_TITLE_TABLE_COL));
				loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
				loNotificationMap.put(ApplicationConstants.ENTITY_ID,
						loStatusInfoMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID));
				loNotificationMap.put(ApplicationConstants.ENTITY_TYPE, HHSConstants.PROPOSAL);
				loNotificationMap.put(HHSConstants.CREATED_BY_USER_ID, loStatusInfoMap.get(HHSConstants.USER_ID));
				loNotificationMap.put(HHSConstants.MODIFIED_BY, loStatusInfoMap.get(HHSConstants.USER_ID));
			}
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
	 * This method will handle close submissions functionality for open ended
	 * and non-open ended RFP. For open ended RFPs, when Close Group:Aloow
	 * Submissions button is clicked, active evaluation group is closed and new
	 * group is created. Providers will be able to submit their proposals in
	 * newly created group. When Close All Submissions or Close Submissions
	 * button is clicked, provider is not allowed to submit any proposal and
	 * procurement status is changed to Proposals Received
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Get the required parameters from request object</li>
	 * <li>Add input parameters and status in Input parameter map</li>
	 * <li>Create audit bean object to make entries in audit table</li>
	 * <li>Get closeGroupFlag from request and check if its true, Set Properties
	 * for inserting new Evaluation Group</li>
	 * <li>Execute transaction <code>updateProcurementForCloseSubmissions</code>
	 * </li>
	 * <li>If the transaction fails then set error message into request object</li>
	 * </ul>
	 * 
	 * @param aoAuthenticationBean authenticated bean
	 * @param aoRequest action request object
	 * @param aoResponse action response object
	 * @param aoChannelObj channel object
	 * @param asUserId user id
	 * @param aoUserSession filenet session bean object
	 * @param aoLaunchWorkflowMap map details for workflow
	 * @throws ApplicationException if an ApplicationException occurred
	 */
	private void closeSubmissionForAuthenticatedUser(AuthenticationBean aoAuthenticationBean, ActionRequest aoRequest,
			ActionResponse aoResponse, Channel aoChannelObj, String asUserId, P8UserSession aoUserSession,
			HashMap<String, String> aoLaunchWorkflowMap) throws ApplicationException
	{
		String lsCloseGroupFlag = aoRequest.getParameter(HHSConstants.CLOSE_GROUP_FLAG);
		String lsProcurementId = aoRequest.getParameter(HHSConstants.PROCUREMENT_ID);
		String lsEvaluationGroupId = aoRequest.getParameter(HHSConstants.EVALUATION_GROUP_ID);
		// populating the map for work flow integration
		aoLaunchWorkflowMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID,
				aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
		aoLaunchWorkflowMap.put(P8Constants.LAUNCHED_BY, asUserId);
		aoLaunchWorkflowMap.put(P8Constants.EVALUATION_GROUP_ID, lsEvaluationGroupId);
		// preparing input parameter map to pass it
		// to the transaction layer to make entries in the procurement
		// table.
		Map<String, Object> loInputParam = new HashMap<String, Object>();
		loInputParam.put(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
		loInputParam.put(HHSConstants.PROCUREMENT_STATUS_KEY, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_PROPOSALS_RECEIVED));
		loInputParam.put(HHSConstants.USER_ID, asUserId);
		loInputParam.put(HHSConstants.EVALUATION_GROUP_ID, lsEvaluationGroupId);
		loInputParam.put(HHSConstants.EVAL_GROUP_STATUS, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_EVALUATION_GROUP_PROPOSALS_RECEIVED));
		loInputParam.put(HHSConstants.CLOSE_GROUP_FLAG, lsCloseGroupFlag);
		loInputParam.put(HHSConstants.PROPOSAL_STATUS_KEY, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_SUBMITTED));
		loInputParam.put(HHSConstants.COMPETITION_POOL_NO_PROPOSALS, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_COMPETITION_POOL_NO_PROPOSALS));
		loInputParam.put(HHSConstants.COMPETITION_POOL_PROPOSAL_RECEIVED, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_COMPETITION_POOL_PROPOSALS_RECEIVED));
		loInputParam.put(HHSConstants.CLOSE_SUBMISSION_FLAG, HHSConstants.TRUE);
		// PARAMETERS ADDED FOR AUDIT CHANGE
		loInputParam.put(HHSConstants.EVENT_NAME, HHSConstants.PROPOSAL_RECIEVED);
		loInputParam.put(HHSConstants.EVENT_TYPE, HHSConstants.PROPOSAL_RECIEVED);
		loInputParam.put(HHSConstants.IS_EVAL_GRP, HHSConstants.ONE);

		if (null != lsCloseGroupFlag && lsCloseGroupFlag.equalsIgnoreCase(HHSConstants.TRUE))
		{
			// insert evaluation grp details
			RFPReleaseBean loRFPReleaseBean = new RFPReleaseBean();
			loRFPReleaseBean.setProcurementId(lsProcurementId);
			loRFPReleaseBean.setCreatedByUserId(asUserId);
			loRFPReleaseBean.setModifiedByUserId(asUserId);
			loRFPReleaseBean.setEvalGroupStatus(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_COMPETITION_POOL_RELEASED));
			aoChannelObj.setData(HHSConstants.RFP_RELEASE_BEAN, loRFPReleaseBean);
			// insert evaluation grp details
			Procurement loProcBean = new Procurement();
			loProcBean.setProcurementId(lsProcurementId);
			loProcBean.setCreatedBy(asUserId);
			loProcBean.setModifiedBy(asUserId);
			loProcBean.setEvaluationGroupTitle(HHSConstants.EVALUATION_GROUP_TITLE);
			loProcBean.setEvalGroupStatus(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_EVALUATION_GROUP_RELEASED));
			aoChannelObj.setData(HHSConstants.PROCUREMENT_BEAN, loProcBean);
		}
		// setting the required parameters in the channel object to pass
		// them to the transaction framework.
		aoChannelObj.setData(HHSConstants.CLOSE_GROUP_FLAG, Boolean.valueOf(lsCloseGroupFlag));
		aoChannelObj.setData(HHSConstants.INPUT_PARAM_MAP, loInputParam);
		aoChannelObj.setData(HHSConstants.LAUNCH_WORKFLOW_MAP, aoLaunchWorkflowMap);
		aoChannelObj.setData(HHSConstants.WORK_FLOW_NAME, P8Constants.PE_START_ACCEPT_PROPOSAL_WORKFLOW_NAME);
		aoChannelObj.setData(HHSConstants.AO_USER_SESSION, aoUserSession);
		aoChannelObj.setData(HHSConstants.PROCUREMENT_ID, lsProcurementId);
		// hitting the transaction layer to update the required table
		// transaction layer executes the transaction and puts the
		// result back in the channel object.
		HHSTransactionManager.executeTransaction(aoChannelObj, HHSConstants.UPDATE_FOR_CLOSE_SUBMISSION);
		aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, lsProcurementId);
		aoResponse.setRenderParameter(HHSConstants.EVALUATION_GROUP_ID, lsEvaluationGroupId);
	}

	/**
	 * This method is used to render the error page when credentials are wrong
	 * or any exception occurs while closing submissions
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1.Get the button value from request</li>
	 * <li>2.If button value is "closeGroupSubmissions", render it to
	 * closeGroupSubmissions.jsp</li>
	 * <li>3.Else If button value is "closeAllSubmissions", render it to
	 * closeAllSubmissions.jsp</li>
	 * <li>4.Else render it to closeSubmission.jsp</li>
	 * </ul>
	 * 
	 * @param aoRequest - RenderRequest
	 * @param aoResponse - RenderResponse
	 * @return - ModelAndView containing jsp name
	 */
	@RenderMapping(params = "render_action=errorPageCloseSubmissions")
	protected ModelAndView renderCloseSubmissions(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
				aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE));
		aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
				aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE_TYPE));
		String lsButtonValue = aoRequest.getParameter(HHSConstants.BUTTON_VALUE);
		if (null != lsButtonValue && lsButtonValue.equalsIgnoreCase(HHSConstants.CLOSE_GROUP_SUBMISSION))
		{
			return new ModelAndView(HHSConstants.CLOSE_GROUP_SUBMISSION);
		}
		else if (null != lsButtonValue && lsButtonValue.equalsIgnoreCase(HHSConstants.CLOSE_ALL_SUBMISSIONS))
		{
			return new ModelAndView(HHSConstants.CLOSE_ALL_SUBMISSIONS);
		}
		else
		{
			return new ModelAndView(HHSConstants.CLOSE_SUBMISSION);
		}
	}

	/**
	 * The method will open the overlay content for close submissions
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Procurement id is fetched from request.</li>
	 * <li>Required parameters are set in the channel object to pass them to the
	 * transaction framework.</li>
	 * <li>Execute the Transaction<b>fetchProvidersAndProposalsNo</b> to get the
	 * no of proposals and no of providers is executed via transaction framework
	 * </li>
	 * <li>After executing the transaction layer puts the result back in the
	 * channel object.</li>
	 * <li>No of providers and no of proposals are retrived from the channel
	 * object.</li>
	 * <li>These are then set in the the request object to be used on the jsp
	 * page</li>
	 * </ul>
	 * 
	 * @param aoResourceRequest ResourceRequest
	 * @param aoResourceResponse resource response object
	 * @return ModelAndView renders the content of closeSubmission.jsp.
	 */
	@SuppressWarnings("unchecked")
	@ResourceMapping("closeSubmissions")
	protected ModelAndView closeSubmissions(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		// fetching the procurement id from request.
		final String lsProcurementId = aoResourceRequest.getParameter(HHSConstants.PROCUREMENT_ID);
		try
		{
			Channel loChannel = new Channel();
			// setting the required parameters in the channel object to pass
			// them to the transaction framework.
			Map<String, String> loInputParam = new HashMap<String, String>();
			loInputParam.put(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
			loInputParam.put(HHSConstants.PROCUREMENT_STATUS_KEY, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_DRAFT));
			// Start || Changes done for enhancement 6577 for Release 3.10.0
			loInputParam.put(HHSConstants.PROP_STATUS_ID, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_CANCELLED));
			// End || Changes done for enhancement 6577 for Release 3.10.0
			loChannel.setData(HHSConstants.INPUT_PARAM_MAP, loInputParam);
			loChannel.setData(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);

			// hitting the transaction layer to get the no of proposals and
			// providers
			// transaction layer executes the transaction and puts the result
			// back in the channel object.
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_PROVIDERS_AND_PROPOSALS_NO);

			// getting the no of providers and no of proposals from the channel
			// object.
			final Integer loNoOfProviders = (Integer) loChannel.getData(HHSConstants.NO_OF_PROVIDERS);
			final Integer loNoOfProposals = (Integer) loChannel.getData(HHSConstants.NO_OF_PROPOSALS);

			Timestamp lsUpdatedPropDueDate = (Timestamp) loChannel.getData(HHSConstants.UPDATED_PROPOSAL_DUE_DATE);
			// setting the required parameters in the request object to be used
			// on the jsp page.
			aoResourceRequest.setAttribute(HHSConstants.NO_OF_PROVIDERS, loNoOfProviders);
			aoResourceRequest.setAttribute(HHSConstants.NO_OF_PROPOSALS, loNoOfProposals);
			aoResourceRequest.setAttribute(HHSConstants.CURRENT_TIME_STAMP, lsUpdatedPropDueDate);
			aoResourceRequest.setAttribute(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			aoResourceRequest.setAttribute(HHSConstants.EVALUATION_GROUP_ID,
					loChannel.getData(HHSConstants.EVALUATION_GROUP_ID));
			aoResourceRequest.setAttribute(HHSConstants.CLOSE_GROUP_FLAG,
					aoResourceRequest.getParameter(HHSConstants.CLOSE_GROUP_FLAG));
			aoResourceRequest.setAttribute(HHSConstants.BUTTON_VALUE,
					aoResourceRequest.getParameter(HHSConstants.BUTTON_VALUE));
			HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb
					.getInstance().getCacheObject(ApplicationConstants.APPLICATION_SETTING);
			String loReleaseTime = loApplicationSettingMap.get(HHSR5Constants.PROPOSAL_RELEASE_TIME_KEY);
			aoResourceRequest.setAttribute(HHSConstants.RELEASE_TIME, loReleaseTime);
		}
		// handling ApplicationException thrown from the transaction layer.
		catch (ApplicationException aoException)
		{
			// populating context data map for exceptional handling
			Map<String, String> loParamMap = new HashMap<String, String>();
			loParamMap.put(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			setGenericErrorMessage(aoResourceRequest);
			aoException.setContextData(loParamMap);
			LOG_OBJECT.Error("Error Occured while opening close submissions page.", aoException);
		}
		// handling exception other than Application Exception.
		catch (Exception aoException)
		{
			LOG_OBJECT.Error("Error Occured while opening close submissions page.", aoException);
			setGenericErrorMessage(aoResourceRequest);
		}
		return new ModelAndView(HHSConstants.CLOSE_SUBMISSION);
	}

	/**
	 * The method will open the overlay content for close Group when Close
	 * Group:Allow Submissions button is clicked from Proposals and Evaluations
	 * Summary screen for open ended RFPS
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1.Get the required parameters from request and pass on to the jsp</li>
	 * <ul>
	 * 
	 * @param aoResourceRequest ResourceRequest
	 * @param aoResourceResponse resource response object
	 * @return ModelAndView containing JSP name(closeGroupSubmissions.jsp)
	 */
	@ResourceMapping("closeGroup")
	protected ModelAndView closeGroup(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		String lsCloseGroupFlag = aoResourceRequest.getParameter(HHSConstants.CLOSE_GROUP_FLAG);
		String lsProcurementId = aoResourceRequest.getParameter(HHSConstants.PROCUREMENT_ID);
		String lsEvaluationGroupId = aoResourceRequest.getParameter(HHSConstants.EVALUATION_GROUP_ID);
		aoResourceRequest.setAttribute(HHSConstants.CLOSE_GROUP_FLAG, lsCloseGroupFlag);
		aoResourceRequest.setAttribute(HHSConstants.PROCUREMENT_ID, lsProcurementId);
		aoResourceRequest.setAttribute(HHSConstants.EVALUATION_GROUP_ID, lsEvaluationGroupId);
		aoResourceRequest.setAttribute(HHSConstants.BUTTON_VALUE,
				aoResourceRequest.getParameter(HHSConstants.BUTTON_VALUE));
		return new ModelAndView(HHSConstants.CLOSE_GROUP_SUBMISSION);
	}

	/**
	 * Changes done for Enhancement #6577 for Release 3.10.0 This is used to
	 * open cancel competition overlay
	 */
	@ResourceMapping("cancelCompetition")
	protected ModelAndView cancelCompetition(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		String lsProcurementId = aoResourceRequest.getParameter(HHSConstants.PROCUREMENT_ID);
		String lsEvaluationGroupId = aoResourceRequest.getParameter(HHSConstants.EVALUATION_GROUP_ID);
		String lsEvalPoolMappingId = aoResourceRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID);
		String lsCompetitionPoolId = aoResourceRequest.getParameter(HHSConstants.COMPETITION_POOL_ID);
		aoResourceRequest.setAttribute(HHSConstants.PROCUREMENT_ID, lsProcurementId);
		aoResourceRequest.setAttribute(HHSConstants.EVALUATION_GROUP_ID, lsEvaluationGroupId);
		aoResourceRequest.setAttribute(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolMappingId);
		aoResourceRequest.setAttribute(HHSConstants.COMPETITION_POOL_ID, lsCompetitionPoolId);
		aoResourceRequest.setAttribute(HHSConstants.CANCEL_STATUS_FLAG,
				aoResourceRequest.getParameter(HHSConstants.CANCEL_STATUS_FLAG));
		return new ModelAndView(HHSConstants.CANCEL_COMPETITION);
	}

	/**
	 * This method is used to render the error page when credentials are wrong
	 * while cancelling competition pool.
	 * 
	 * <ul>
	 * <li>1. Get user name from cancelCompetition jsp and set in request.</li>
	 * </ul>
	 * 
	 * @param aoRequest - RenderRequest
	 * @param aoResponse - RenderResponse
	 * @return - ModelAndView
	 */
	@RenderMapping(params = "render_action=cancelCompetition")
	protected ModelAndView renderCancelCompetiiton(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		ModelAndView loModelAndView = new ModelAndView(HHSConstants.CANCEL_COMPETITION);
		aoRequest
				.setAttribute(HHSConstants.CANCEL_STATUS_FLAG, aoRequest.getParameter(HHSConstants.CANCEL_STATUS_FLAG));
		return loModelAndView;
	}

	/**
	 * The method will open the overlay content for close all submissions when
	 * Close All Submissions button is clicked from Proposals and Evaluations
	 * Summary screen for open ended RFPS
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1.Get the required parameters from request and pass on to the jsp</li>
	 * <ul>
	 * 
	 * @param aoResourceRequest ResourceRequest
	 * @param aoResourceResponse resource response object
	 * @return ModelAndView containing JSP name(closeAllSubmissions.jsp)
	 */
	@ResourceMapping("closeAllSubmission")
	protected ModelAndView closeAllSubmissions(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		String lsCloseGroupFlag = aoResourceRequest.getParameter(HHSConstants.CLOSE_GROUP_FLAG);
		String lsProcurementId = aoResourceRequest.getParameter(HHSConstants.PROCUREMENT_ID);
		String lsEvaluationGroupId = aoResourceRequest.getParameter(HHSConstants.EVALUATION_GROUP_ID);
		aoResourceRequest.setAttribute(HHSConstants.CLOSE_GROUP_FLAG, lsCloseGroupFlag);
		aoResourceRequest.setAttribute(HHSConstants.PROCUREMENT_ID, lsProcurementId);
		aoResourceRequest.setAttribute(HHSConstants.EVALUATION_GROUP_ID, lsEvaluationGroupId);
		aoResourceRequest.setAttribute(HHSConstants.BUTTON_VALUE,
				aoResourceRequest.getParameter(HHSConstants.BUTTON_VALUE));
		return new ModelAndView(HHSConstants.CLOSE_ALL_SUBMISSIONS);
	}

	/**
	 * The action Method is used when Filter action is performed on Evaluation
	 * Results and Selection Screen Here we will be setting data which will be
	 * required to fetch Evaluation Results and Selection data on the basis of
	 * Filter
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1.Once the Filter Search is performed filter search parameter will
	 * bind with EvaluationFilterBean</li>
	 * <li>2.Setting Sorting Related Parameter</li>
	 * <li>3.Setting EvaluationFilterBean in Session</li>
	 * <li>4.Setting Filter Variable in request i.e to identify we are coming
	 * from Filter Screen</li>
	 * <li>5.Setting Render parameter Required for rendering
	 * fetchEvaluationResults render method</li>
	 * </ul>
	 * @param aoEvaluationBean EvaluationFilterBean
	 * @param aoResult BindingResult
	 * @param aoRequest ActionRequest
	 * @param aoResponse ActionResponse
	 */
	@ActionMapping(params = "submit_action=filterEvaluationResults")
	protected void actionFilterEvaluationResults(
			@ModelAttribute("EvaluationFilterBean") EvaluationFilterBean aoEvaluationBean, BindingResult aoResult,
			ActionRequest aoRequest, ActionResponse aoResponse)
	{
		try
		{
			String lsHideExitProcurement = aoRequest.getParameter(HHSConstants.HIDE_EXIT_PROCUREMENT);
			if (aoEvaluationBean.getAwardAmountFrom() == null && aoEvaluationBean.getAwardAmountTo() != null)
			{
				BigDecimal loAmountFrom = new BigDecimal(aoRequest.getParameter(HHSConstants.AWARD_AMT_FROM_HIDDEN));
				aoEvaluationBean.setAwardAmountFrom(loAmountFrom);
				aoResponse.setRenderParameter(HHSConstants.AWARD_AMT_FROM_HIDDEN, HHSConstants.YES);
			}
			if (aoEvaluationBean.getAwardAmountTo() == null && aoEvaluationBean.getAwardAmountFrom() != null)
			{
				BigDecimal loAmountFrom = new BigDecimal(aoRequest.getParameter(HHSConstants.AWARD_AMT_TO_HIDDEN));
				aoEvaluationBean.setAwardAmountTo(loAmountFrom);
				aoResponse.setRenderParameter(HHSConstants.AWARD_AMT_TO_HIDDEN, HHSConstants.YES);
			}
			if (aoEvaluationBean.getScoreRangeFrom() == null && aoEvaluationBean.getScoreRangeTo() != null)
			{
				aoEvaluationBean.setScoreRangeFrom(Integer.parseInt(aoRequest
						.getParameter(HHSConstants.SCORE_FROM_HIDDEN)));
				aoResponse.setRenderParameter(HHSConstants.SCORE_FROM_HIDDEN, HHSConstants.YES);
			}
			if (aoEvaluationBean.getScoreRangeTo() == null && aoEvaluationBean.getScoreRangeFrom() != null)
			{
				aoEvaluationBean
						.setScoreRangeTo(Integer.parseInt(aoRequest.getParameter(HHSConstants.SCORE_TO_HIDDEN)));
				aoResponse.setRenderParameter(HHSConstants.SCORE_TO_HIDDEN, HHSConstants.YES);
			}
			LOG_OBJECT.Debug("Entered into Evaluation Status Filter Action::");
			// Setting Sorting Related Parameter
			aoEvaluationBean.setFirstSort(HHSConstants.PROP_TITLE);
			aoEvaluationBean.setSecondSort(HHSConstants.ORGANIZATION_NAME);
			aoEvaluationBean.setFirstSortType(HHSConstants.ASCENDING);
			aoEvaluationBean.setSecondSortType(HHSConstants.ASCENDING);
			aoEvaluationBean.setSortColumnName(HHSConstants.PROPOSAL_TITLE);
			aoEvaluationBean.setFirstSortDate(false);
			aoEvaluationBean.setSecondSortDate(false);
			aoEvaluationBean.setIsFiltered(HHSConstants.YES_UPPERCASE);
			aoRequest.getPortletSession().setAttribute(HHSConstants.EVALUATION_SESSION_FILTER_BEAN, aoEvaluationBean,
					PortletSession.PORTLET_SCOPE);
			aoRequest.getPortletSession().setAttribute(HHSConstants.PROPOSAL_FILTERED_RESULT,
					HHSConstants.PROPOSAL_FILTERED_RESULT, PortletSession.PORTLET_SCOPE);
			aoResponse.setRenderParameter(HHSConstants.FILTERED, HHSConstants.FILTERED);
			aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID,
					aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			aoResponse.setRenderParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID,
					aoRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID));
			aoResponse.setRenderParameter(HHSConstants.EVALUATION_GROUP_ID,
					aoRequest.getParameter(HHSConstants.EVALUATION_GROUP_ID));
			aoResponse.setRenderParameter(HHSConstants.COMPETITION_POOL_ID,
					aoRequest.getParameter(HHSConstants.COMPETITION_POOL_ID));
			// Setting Parameter for Successfully Rendering of
			// fetch Evaluation Results and Selections
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.FETCH_EVAL_RESUTS);
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
			aoResponse.setRenderParameter(HHSConstants.TOP_LEVEL_FROM_REQ,
					aoRequest.getParameter(HHSConstants.TOP_LEVEL_FROM_REQ));
			aoResponse.setRenderParameter(HHSConstants.MID_LEVEL_FROM_REQ,
					aoRequest.getParameter(HHSConstants.MID_LEVEL_FROM_REQ));
			if (null != lsHideExitProcurement)
			{
				aoResponse.setRenderParameter(HHSConstants.HIDE_EXIT_PROCUREMENT, lsHideExitProcurement);
			}
		}
		// handling ApplicationException
		catch (ApplicationException aoException)
		{
			LOG_OBJECT.Error("Exception Occured while Filter Evaluation Results and Selections:", aoException);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
		// handling exception while processing filter on evaluation result
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception Occured while Filter Evaluation Results and Selections:", aoExp);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
	}

	/**
	 * This method handles action when mark non-responsive option is being
	 * selected on the Actions drop down on S215
	 * 
	 * <li>1. Fetch proposal Id from the request object</li> <li>2. Create one
	 * channel object and populate the fetched proposal Id in it</li> <li>3. Get
	 * notification map by calling method getNotificationMapForProposalTask()
	 * method and set it in channel</li> <li>4. Execute transaction
	 * <b>markProposalNonResponsive</b> to mark the proposal status as
	 * "Non Responsive"</li> <li>5. Redirect the user to S215 : Evaluation
	 * Status screen</li> <li>6. If any exception occurs while executing the
	 * transaction then they will be caught by the ApplicationException catch
	 * block or Exception catch block</li> </ul>
	 * 
	 * @param aoRequest - a ActionRequest object
	 * @param aoResponse - a ActionResponse object
	 */
	@ActionMapping(params = "submit_action=markProposalNonResponsive")
	protected void markProposalNonResponsive(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		Channel loChannel = new Channel();
		try
		{
			setNavigationParamsInRender(aoRequest, aoResponse);
			String lsProposalId = aoRequest.getParameter(HHSConstants.PROPOSAL_ID);
			loChannel.setData(HHSConstants.PROPOSAL_ID_KEY, lsProposalId);
			// get notification map
			List<String> loAlertList = new ArrayList<String>();
			loAlertList.add(HHSConstants.NT226);
			loAlertList.add(HHSConstants.AL225);
			loChannel.setData(HHSConstants.LO_HM_NOTIFY_PARAM,
					getNotificationMapForProposalTask(aoRequest, loAlertList));
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.MARK_PROPOSAL_NON_RESPONSIVE);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.GET_EVALUATION_STATUS);
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
			aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID,
					aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
		}
		// handling Application Exception thrown by transaction layer
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Exception Occured while marking a proposal Non Responsive:", aoExp);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
		// handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception Occured while marking a proposal Non Responsive:", aoExp);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
	}

	/**
	 * This Method will handle the functionality when
	 * "Mark Returned for Revision" option has been selected in the "Actions"
	 * drop down on S215 screen
	 * <ul>
	 * <li>1. Create one Map object and populate the same with the proposal Id,
	 * procurement Id and proposal Status Id corresponding to proposal returned
	 * for revision</li>
	 * <li>2. Create one channel object and populate the created map object in
	 * it</li>
	 * <li>3. Get notification map by calling method
	 * getNotificationMapForReturnedForRevision() method and set it in channel</li>
	 * <li>4. Execute transaction <b>confirmReturnForAction</b> to mark the
	 * proposal status as "Returned for Revision"</li>
	 * <li>5. If the value of retrieved boolean flag is "true" then redirect the
	 * user to S215 : Evaluation Status screen</li>
	 * </ul>
	 * 
	 * @param aoRequest - ActionRequest Object
	 * @param aoResponse - ActionResponse Object
	 **/
	@ActionMapping(params = "submit_action=confirmReturnForAction")
	protected void confirmReturnForAction(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		Channel loChannelObj = new Channel();
		try
		{
			setNavigationParamsInRender(aoRequest, aoResponse);
			Map<String, String> loParamMap = new HashMap<String, String>();
			loParamMap.put(HHSConstants.PROCUREMENT_ID_KEY, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			loParamMap.put(HHSConstants.PROPOSAL_ID_KEY, aoRequest.getParameter(HHSConstants.PROPOSAL_ID));
			loParamMap.put(HHSConstants.AS_STATUS_ID, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_RETURNED_FOR_REVISION));
			loChannelObj.setData(HHSConstants.STATUS_MAP_KEY, loParamMap);
			loChannelObj.setData(HHSConstants.PROPOSAL_ID, aoRequest.getParameter(HHSConstants.PROPOSAL_ID));

			List<String> loAlertList = new ArrayList<String>();
			loAlertList.add(HHSConstants.NT225);
			loAlertList.add(HHSConstants.AL224);
			loChannelObj.setData(HHSConstants.LO_HM_NOTIFY_PARAM,
					getNotificationMapForReturnedForRevision(aoRequest, loAlertList, Boolean.FALSE));
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.CONFIRM_RETURN_FOR_ACTION);
			Boolean loReturnStatus = (Boolean) loChannelObj.getData(HHSConstants.RETURN_STATUS);
			// checking if the retrieved boolean flag is "true"
			if (loReturnStatus)
			{
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.GET_EVALUATION_STATUS);
				aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
			}
			aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID,
					aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));

		}
		// handling Application Exception thrown by transaction layer
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Exception Occured while updating the proposal status to Returned For Revision", aoExp);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
		// handling Exception other than Application Exception
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception Occured while updating the proposal status to Returned For Revision", aoExp);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
	}

	/**
	 * This method generates notification map for Accept Proposal task depending
	 * upon input alert list
	 * 
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
	 * @param aoRequest ActionRequest object
	 * @param aoAlertList a list of notification ID to be sent like NT213
	 * @param aoScoreAmendmentFlag - request score amendment boolean flag
	 * @return a notification hashmap
	 * @throws ApplicationException
	 */
	private HashMap<String, Object> getNotificationMapForReturnedForRevision(ActionRequest aoRequest,
			List<String> aoAlertList, Boolean aoScoreAmendmentFlag) throws ApplicationException
	{
		HashMap<String, Object> loNotificationMapForReturned = new HashMap<String, Object>();
		try
		{
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_ID, PortletSession.APPLICATION_SCOPE);
			String lsServerName = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
					HHSConstants.SERVER_NAME_FOR_PROVIDER_BATCH);
			String lsServerPort = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
					HHSConstants.SERVER_PORT_FOR_PROVIDER_BATCH);
			String lsContextPath = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
					HHSConstants.CONTEXT_PATH_FOR_PROVIDER_BATCH);
			String lsAppProtocol = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
					HHSConstants.SERVER_PROTOCOL_FOR_PROVIDER_BATCH);
			HashMap<String, String> loRequestMap = new HashMap<String, String>();
			loNotificationMapForReturned.put(HHSConstants.NOTIFICATION_ALERT_ID, aoAlertList);
			for (String lsAlertId : aoAlertList)
			{
				NotificationDataBean loNotificationDataBean = new NotificationDataBean();
				HashMap<String, String> loLinkMap = new HashMap<String, String>();
				StringBuffer loApplicationUrl = new StringBuffer(256);
				// checking if the value of request score amendment boolean flag
				// is true
				if (aoScoreAmendmentFlag)
				{
					loApplicationUrl.append(aoRequest.getScheme()).append(HHSConstants.NOTIFICATION_HREF_1)
							.append(aoRequest.getServerName()).append(HHSConstants.COLON)
							.append(aoRequest.getServerPort()).append(aoRequest.getContextPath())
							.append(HHSConstants.AGENCY_TASK_INBOX_URL);
				}
				else
				{
					loApplicationUrl.append(lsAppProtocol).append(HHSConstants.NOTIFICATION_HREF_1)
							.append(lsServerName).append(HHSConstants.COLON).append(lsServerPort)
							.append(HHSConstants.FORWARD_SLASH).append(lsContextPath)
							.append(HHSConstants.PROPOSAL_SUMMARY_URL)
							.append(aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
				}
				loLinkMap.put(HHSConstants.LINK, loApplicationUrl.toString());
				loNotificationDataBean.setLinkMap(loLinkMap);
				loNotificationDataBean.setAgencyLinkMap(loLinkMap);
				loNotificationMapForReturned.put(lsAlertId, loNotificationDataBean);
			}
			loNotificationMapForReturned.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
			loNotificationMapForReturned.put(ApplicationConstants.ENTITY_ID,
					aoRequest.getParameter(HHSConstants.PROPOSAL_ID));
			loNotificationMapForReturned.put(ApplicationConstants.ENTITY_TYPE, HHSConstants.PROPOSAL);
			loNotificationMapForReturned.put(HHSConstants.CREATED_BY_USER_ID, lsUserId);
			loNotificationMapForReturned.put(HHSConstants.MODIFIED_BY, lsUserId);
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Error was occurred while get notfication for getNotificationMapForReturnedForRevision",
					aoEx);
			throw aoEx;
		}
		// handling Exception thrown by the application
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error was occurred while get notfication for getNotificationMapForReturnedForRevision",
					aoEx);
			throw new ApplicationException(
					"Error was occurred while get notfication for getNotificationMapForReturnedForRevision", aoEx);
		}
		return loNotificationMapForReturned;
	}

	/***
	 * This method will handle sort action from Evaluation Group Proposal screen
	 * when any of the table headers is clicked
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1.Setting Navigation parameters in Render by calling method
	 * setNavigationParamsInRender from BaseControllerSM</li>
	 * <li>2.Get sortType and columnName values by calling parseQueryString()
	 * method from class PortalUtil. Get sorting details by calling method
	 * getSortDetailsFromXML() from class BaseController.</li>
	 * <li>3.Setting Evaluation Bean and other required parameter in Render for
	 * successful rendering of Evaluation Group Proposal screen</li>
	 * <li></li>
	 * <ul>
	 * 
	 * @param aoEvalGroupProposalBean EvaluationGroupsProposalBean object
	 * @param aoRequest ActionRequest
	 * @param aoResponse ActionResponse
	 */
	@ActionMapping(params = "submit_action=sortGroupProposalSummary")
	protected void actionSortGroupProposalSummary(
			@ModelAttribute("EvaluationGroupsProposalBean") EvaluationGroupsProposalBean aoEvalGroupProposalBean,
			ActionRequest aoRequest, ActionResponse aoResponse)
	{
		Map<String, String> loHmReqExceProp = new HashMap<String, String>();
		try
		{
			LOG_OBJECT.Debug("Entered into Group Proposal Summary Sort Action::");
			setNavigationParamsInRender(aoRequest, aoResponse);
			String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			String lsSortType = PortalUtil.parseQueryString(aoRequest, HHSConstants.SORT_TYPE);
			String lsColumnName = PortalUtil.parseQueryString(aoRequest, HHSConstants.COLUMN_NAME);
			getSortDetailsFromXML(lsColumnName, lsUserOrgType,
					PortalUtil.parseQueryString(aoRequest, HHSConstants.SORT_GRID_NAME), aoEvalGroupProposalBean,
					lsSortType);
			// Setting param for Rendering getEvaluationStatus Render Method
			aoRequest.getPortletSession().setAttribute(HHSConstants.EVAL_GROUP_PROPOSAL_SESSION_BEAN,
					aoEvalGroupProposalBean, PortletSession.PORTLET_SCOPE);
			aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID,
					aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			loHmReqExceProp.put(HHSConstants.PROCUREMENT_ID, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
		}
		// handling ApplicationException that can be occured while
		// fetching evaluation status data for evaluation status screen
		catch (ApplicationException aoAppEx)
		{
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loHmReqExceProp, aoAppEx);
			LOG_OBJECT.Error("Exception Occured while sorting Evaluation Status: ", aoAppEx);
		}
		// handling Exception other than ApplicationException
		catch (Exception aoEx)
		{
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
			LOG_OBJECT.Error("Exception Occured while sorting Evaluation Status: ", aoEx);
		}
	}

	/**
	 * This method is used to render Evaluation Group Proposal Summary screen
	 * for a given value of procurement
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1.Get EvaluationGroupsProposalBean from Session object</li>
	 * <li>2.Set pagination parameters in session object by calling
	 * getPagingParams() from BaseController</li>
	 * <li>3.Set channel parameters and execute transaction
	 * <b>fetchEvaluationGroupProposal</b></li>
	 * <li>5.Set output in Request parameter</li>
	 * </ul>
	 * 
	 * @param aoRenderRequest - Render Request object
	 * @param asProcurementId - Procurement Id
	 * @return model and view object containing view name
	 * @throws ApplicationException If an Application Exception occurs
	 */
	private ModelAndView getGroupProposalSummary(RenderRequest aoRenderRequest, final String asProcurementId)
			throws ApplicationException
	{
		ModelAndView loModelAndView;
		EvaluationGroupsProposalBean loEvalGroupsProposalBean = null;
		loEvalGroupsProposalBean = (EvaluationGroupsProposalBean) aoRenderRequest.getPortletSession().getAttribute(
				HHSConstants.EVAL_GROUP_PROPOSAL_SESSION_BEAN, PortletSession.PORTLET_SCOPE);
		if (null == loEvalGroupsProposalBean)
		{
			loEvalGroupsProposalBean = new EvaluationGroupsProposalBean();
		}
		loEvalGroupsProposalBean.setProcurementId(asProcurementId);
		String lsNextPage = aoRenderRequest.getParameter(HHSConstants.NEXT_PAGE_PARAM);
		// set pagination parameters in session object by calling
		// getPagingParams()
		getPagingParams(aoRenderRequest.getPortletSession(), loEvalGroupsProposalBean, lsNextPage,
				HHSConstants.GROUP_PROPOSAL_SUMMARY_KEY);
		Channel loChannel = new Channel();
		loChannel.setData(HHSConstants.EVAL_GROUP_PROPOSAL_BEAN, loEvalGroupsProposalBean);
		loChannel.setData(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_EVALUATION_GROUP_PROPOSAL);
		Integer loEvalGrpProposalCount = (Integer) loChannel.getData(HHSConstants.EVAL_GRP_PROPOSAL_COUNT);
		aoRenderRequest.setAttribute(HHSConstants.EVALUATION_GROUP_PROPOSAL,
				loChannel.getData(HHSConstants.EVALUATION_GROUP_PROPOSAL));
		aoRenderRequest.getPortletSession().setAttribute(ApplicationConstants.ALERT_VIEW_PAGING_RECORDS,
				((loEvalGrpProposalCount == null) ? 0 : loEvalGrpProposalCount), PortletSession.APPLICATION_SCOPE);
		aoRenderRequest.getPortletSession().setAttribute(HHSConstants.SORT_TYPE,
				loEvalGroupsProposalBean.getFirstSortType(), PortletSession.APPLICATION_SCOPE);
		aoRenderRequest.getPortletSession().setAttribute(HHSConstants.SORT_BY,
				loEvalGroupsProposalBean.getSortColumnName(), PortletSession.APPLICATION_SCOPE);
		aoRenderRequest.getPortletSession().removeAttribute(HHSConstants.EVAL_GROUP_PROPOSAL_SESSION_BEAN);
		loModelAndView = new ModelAndView(HHSConstants.EVALUATION_GROUPS_PROPOSAL_JSP_PATH);
		return loModelAndView;
	}

	/**
	 * This method is used to render Proposals and Evaluation Summary screen for
	 * a particular group of a procurement
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1.Check for close Submission success and display success message
	 * accordingly</li>
	 * <li>2.Get EvaluationSummaryBean from Session object</li>
	 * <li>3.Set pagination parameters in session object by calling
	 * getPagingParams() from BaseController</li>
	 * <li>4.Set channel parameters and execute transaction
	 * <b>fetchEvaluationSummary</b></li>
	 * <li>5.Set output in Request parameter</li>
	 * </ul>
	 * 
	 * @param aoRenderRequest - Render Request object
	 * @param asProcurementId - Procurement Id
	 * @param asEvaluationGroupId - Evaluation Group Id
	 * @return model and view object containing view name
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings("unchecked")
	private ModelAndView getProposalEvaluationSummary(RenderRequest aoRenderRequest, final String asProcurementId,
			String asEvaluationGroupId) throws ApplicationException
	{
		ModelAndView loModelAndView;
		if (PortalUtil.parseQueryString(aoRenderRequest, HHSConstants.CLOSE_SUBMITTION) != null)
		{
			if (PortalUtil.parseQueryString(aoRenderRequest, HHSConstants.CLOSE_SUBMITTION).equalsIgnoreCase(
					HHSConstants.CLOSE_SUBMITTION_SUCCESS))
			{
				aoRenderRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.CLOSE_SUBMISSIONS));
				aoRenderRequest.setAttribute(HHSConstants.CLOSE_SUBMITTION, HHSConstants.CLOSE_SUBMITTION_SUCCESS);
			}
			else if (PortalUtil.parseQueryString(aoRenderRequest, HHSConstants.CLOSE_SUBMITTION).equalsIgnoreCase(
					HHSConstants.CLOSE_GROUP_SUCCESS))
			{
				aoRenderRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.CLOSE_GROUP_SUBMISSION));
			}
			else if (PortalUtil.parseQueryString(aoRenderRequest, HHSConstants.CLOSE_SUBMITTION).equalsIgnoreCase(
					HHSConstants.CLOSE_ALL_SUBMITTION_SUCCESS))
			{
				aoRenderRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.CLOSE_ALL_SUBMISSIONS));
			}
			aoRenderRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_PASS_TYPE);
		}
		// Start || Changes done for Enhancement #6577 for Release 3.10.0
		else
		{
			if (null != PortalUtil.parseQueryString(aoRenderRequest, HHSConstants.CANCEL_COMPETITION))
			{
				if (PortalUtil.parseQueryString(aoRenderRequest, HHSConstants.CANCEL_COMPETITION).equalsIgnoreCase(
						HHSConstants.TRUE))
				{
					LOG_OBJECT.Debug("loCancelStatusFlag to render while Cancel Competition:"
							+ PortalUtil.parseQueryString(aoRenderRequest, HHSConstants.CANCEL_STATUS_FLAG));
					if (null != PortalUtil.parseQueryString(aoRenderRequest, HHSConstants.CANCEL_STATUS_FLAG)
							&& PortalUtil.parseQueryString(aoRenderRequest, HHSConstants.CANCEL_STATUS_FLAG)
									.equalsIgnoreCase(HHSConstants.STRING_FALSE))
					{
						LOG_OBJECT.Debug("----cancel Status------------"
								+ PortalUtil.parseQueryString(aoRenderRequest, HHSConstants.CANCEL_STATUS_FLAG)
								+ "----------------");
						aoRenderRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
								HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.CANCEL_COMPETITION_FAILED));
						aoRenderRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
								ApplicationConstants.MESSAGE_FAIL_TYPE);
					}
					else
					{
						LOG_OBJECT.Debug("------Cancel Comp----------"
								+ PortalUtil.parseQueryString(aoRenderRequest, HHSConstants.CANCEL_COMPETITION)
								+ "----------------");
						aoRenderRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
								HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.CANCEL_COMPETITION));
						aoRenderRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
								ApplicationConstants.MESSAGE_PASS_TYPE);
					}
				}
			}
		}
		// END || Changes done for Enhancement #6577 for Release 3.10.0
		EvaluationSummaryBean loEvalSummaryBean = null;
		loEvalSummaryBean = (EvaluationSummaryBean) aoRenderRequest.getPortletSession().getAttribute(
				HHSConstants.EVAL_SUMMARY_SESSION_BEAN, PortletSession.PORTLET_SCOPE);
		if (null == loEvalSummaryBean)
		{
			loEvalSummaryBean = new EvaluationSummaryBean();
		}
		loEvalSummaryBean.setProcurementId(asProcurementId);
		loEvalSummaryBean.setEvaluationGroupId(asEvaluationGroupId);
		String lsNextPage = aoRenderRequest.getParameter(HHSConstants.NEXT_PAGE_PARAM);
		// set pagination parameters in session object by calling
		// getPagingParams()
		getPagingParams(aoRenderRequest.getPortletSession(), loEvalSummaryBean, lsNextPage,
				HHSConstants.EVALUATION_SUMMARY_KEY);
		String lsUserRole = (String) aoRenderRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ROLE, PortletSession.APPLICATION_SCOPE);
		// START || Changes done for Enhancement #6577 for Release 3.10.0
		String lsUserType = (String) aoRenderRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
		loEvalSummaryBean.setUserType(lsUserType);
		loEvalSummaryBean.setUserRole(lsUserRole);
		// END || Changes done for Enhancement #6577 for Release 3.10.0
		Channel loChannel = new Channel();
		loChannel.setData(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		loChannel.setData(HHSConstants.EVALUATION_GROUP_ID, asEvaluationGroupId);
		loChannel.setData(HHSConstants.EVALUATION_SUMMARY_BEAN, loEvalSummaryBean);
		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_EVALUATION_SUMMARY);
		
		//Begin R7.2.0 QC9059 Action Dropdown for Proposals&Evaluations
		List<EvaluationSummaryBean> evaluationSummaryList = (List<EvaluationSummaryBean>) loChannel.getData(HHSConstants.EVALUATION_SUMMARY_LIST);
		setOversightRoleFlagForEvaluationSummary(aoRenderRequest.getPortletSession(), evaluationSummaryList);
		//End R7.2.0 QC9059 Action Dropdown for Proposals&Evaluations
		
		Map<String, String> loCloseVisibiltyStatusMap = (HashMap<String, String>) loChannel
				.getData(HHSConstants.CLOSE_BUTTON_VISIBILTY_FLAG);
		Integer loEvalSummaryCount = (Integer) loChannel.getData(HHSConstants.EVALUATION_SUMMARY_COUNT);
		List<Map<String, String>> loEvaluationGroupList = (List<Map<String, String>>) loChannel
				.getData(HHSConstants.EVALUATION_GROUP_LIST);
		if (null != loEvaluationGroupList && !loEvaluationGroupList.isEmpty() && null == asEvaluationGroupId)
		{
			Map<String, String> loEvaluationGroupMap = loEvaluationGroupList.get(0);
			aoRenderRequest.setAttribute(HHSConstants.EVALUATION_GROUP_ID,
					loEvaluationGroupMap.get(HHSConstants.EVALUATION_GROUP_ID_COL));
		}
		aoRenderRequest.setAttribute(HHSConstants.GROUP_TITLE_MAP, loChannel.getData(HHSConstants.GROUP_TITLE_MAP));
		aoRenderRequest.setAttribute(HHSConstants.CLOSE_BUTTON_VISIBILTY_FLAG,
				loCloseVisibiltyStatusMap.get(HHSConstants.CLOSE_BUTTON_VISIBILTY_FLAG));
		aoRenderRequest.setAttribute(HHSConstants.CLOSE_BUTTON_ENABLE_FLAG,
				loCloseVisibiltyStatusMap.get(HHSConstants.CLOSE_BUTTON_ENABLE_FLAG));
		aoRenderRequest.setAttribute(HHSConstants.EVALUATION_SUMMARY_LIST,
				loChannel.getData(HHSConstants.EVALUATION_SUMMARY_LIST));
		aoRenderRequest.setAttribute(HHSConstants.EVALUATION_GROUP_LIST, loEvaluationGroupList);
		aoRenderRequest.getPortletSession().setAttribute(ApplicationConstants.ALERT_VIEW_PAGING_RECORDS,
				((loEvalSummaryCount == null) ? 0 : loEvalSummaryCount), PortletSession.APPLICATION_SCOPE);
		aoRenderRequest.getPortletSession().setAttribute(HHSConstants.SORT_TYPE, loEvalSummaryBean.getFirstSortType(),
				PortletSession.APPLICATION_SCOPE);
		aoRenderRequest.getPortletSession().setAttribute(HHSConstants.SORT_BY, loEvalSummaryBean.getSortColumnName(),
				PortletSession.APPLICATION_SCOPE);
		aoRenderRequest.getPortletSession().removeAttribute(HHSConstants.EVAL_SUMMARY_SESSION_BEAN);
		loModelAndView = new ModelAndView(HHSConstants.EVAL_SUMMARY_SCREEN_PATH);
		return loModelAndView;
	}

	/***
	 * This method will handle sort action from Proposals and Evaluations
	 * Summary screen by clicking any of the column headers
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1.Setting Navigation parameters in Response by calling method
	 * setNavigationParamsInRender from BaseControllerSM</li>
	 * <li>2.Get sortType and columnName values by calling parseQueryString()
	 * method from class PortalUtil.</li>
	 * <li>3.Get sorting details by calling method getSortDetailsFromXML() from
	 * class BaseController.</li>
	 * <li>4.Setting Evaluation Summary Bean and other required parameter in
	 * Response for successful rendering of Proposals and Evaluations Summary</li>
	 * <ul>
	 * 
	 * @param aoEvaluationSummaryBean EvaluationSummaryBean
	 * @param aoRequest ActionRequest
	 * @param aoResponse ActionResponse
	 */
	@ActionMapping(params = "submit_action=sortEvaluationSummary")
	protected void actionSortEvaluationSummary(
			@ModelAttribute("EvaluationSummaryBean") EvaluationSummaryBean aoEvaluationSummaryBean,
			ActionRequest aoRequest, ActionResponse aoResponse)
	{
		Map<String, String> loHmReqExceProp = new HashMap<String, String>();
		try
		{
			LOG_OBJECT.Debug("Entered into Proposal Evalluation Summary Sort Action::");
			setNavigationParamsInRender(aoRequest, aoResponse);
			String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			String lsSortType = PortalUtil.parseQueryString(aoRequest, HHSConstants.SORT_TYPE);
			String lsColumnName = PortalUtil.parseQueryString(aoRequest, HHSConstants.COLUMN_NAME);
			getSortDetailsFromXML(lsColumnName, lsUserOrgType,
					PortalUtil.parseQueryString(aoRequest, HHSConstants.SORT_GRID_NAME), aoEvaluationSummaryBean,
					lsSortType);
			// Setting param for Rendering getEvaluationStatus Render Method
			aoRequest.getPortletSession().setAttribute(HHSConstants.EVAL_SUMMARY_SESSION_BEAN, aoEvaluationSummaryBean,
					PortletSession.PORTLET_SCOPE);
			aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID,
					aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			loHmReqExceProp.put(HHSConstants.PROCUREMENT_ID, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
		}
		// handling ApplicationException that can be occured while
		// fetching evaluation status data for evaluation status screen
		catch (ApplicationException aoAppEx)
		{
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loHmReqExceProp, aoAppEx);
			LOG_OBJECT.Error("Exception Occured while sorting Proposals and Evaluations Summary: ", aoAppEx);
		}
		// handling Exception other than ApplicationException
		catch (Exception aoEx)
		{
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
			LOG_OBJECT.Error("Exception Occured while sorting Proposals and Evaluations Summary: ", aoEx);
		}
	}

	/**
	 * This method will handle pagination action from Proposals and Evaluation
	 * Summary screen.
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1.Set navigation parameters in Response by calling method
	 * setNavigationParamsInRender from BaseControllerSM</li>
	 * <li>2.This method will check for next action parameter and call methods
	 * accordingly.</li>
	 * <li>3.Set procurement Id and required attributes in Response</li>
	 * </ul>
	 * 
	 * @param aoRequest an Action Request object
	 * @param aoResponse an Action Response object
	 */
	@ActionMapping(params = "submit_action=paginateEvaluationSummary")
	protected void actionPaginateEvaluationSummary(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		setNavigationParamsInRender(aoRequest, aoResponse);
		aoResponse.setRenderParameter(HHSConstants.NEXT_PAGE_PARAM, aoRequest.getParameter(HHSConstants.NEXT_PAGE));
		aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
		aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
	}

	/**
	 * This method will handle pagination action from Evaluation Group Proposal
	 * Summary screen.
	 * 
	 * <ul>
	 * <li>1.Set navigation parameters in Response by calling method
	 * setNavigationParamsInRender from BaseControllerSM</li>
	 * <li>2.This method will check for next action parameter</li>
	 * <li>3.Set procurement Id and required attributes in Response</li>
	 * </ul>
	 * 
	 * @param aoRequest an Action Request object
	 * @param aoResponse an Action Response object
	 */
	@ActionMapping(params = "submit_action=paginateGroupProposalSummary")
	protected void actionPaginateGroupProposalSummary(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		setNavigationParamsInRender(aoRequest, aoResponse);
		aoResponse.setRenderParameter(HHSConstants.NEXT_PAGE_PARAM, aoRequest.getParameter(HHSConstants.NEXT_PAGE));
		aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
		aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
	}

	/**
	 * The method will be called when user select "Unlock Proposal" option from
	 * Evaluation Status screen. It will relaunch Accept proposal task for the
	 * corresponding proposal id and will update the status of proposal to
	 * 'Pending Reassignment'
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1.Set navigation parameters in Response by calling method
	 * setNavigationParamsInRender from BaseControllerSM</li>
	 * <li>2.Add input parameters to map</li>
	 * <li>3.Add audit bean properties</li>
	 * <li>4.Set Attributes in channel and call transaction
	 * <b>unlockProposal</b> which updates proposal status to 'Pending
	 * Reassignment' and relaunch Accept Proposal Task</li>
	 * </ul>
	 * 
	 * @param aoResourceRequest ResourceRequest
	 * @param aoResourceResponse ResourceResponse
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@ActionMapping(params = "submit_action=unlockProposal")
	protected void unLockProposal(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		P8UserSession loUserSession = null;
		HhsAuditBean loAuditBean = new HhsAuditBean();
		Channel loChannel = new Channel();
		Map<String, String> loStatusMap = new HashMap<String, String>();
		String lsProcurementId = null;
		try
		{
			setNavigationParamsInRender(aoRequest, aoResponse);
			loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			String lsProposalId = aoRequest.getParameter(HHSConstants.PROPOSAL_ID);
			lsProcurementId = PortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
			String lsStatusId = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_PROPOSAL_PENDING_REASSIGNMENT);
			loStatusMap.put(HHSConstants.STATUS_PROPOSAL_PENDING_REASSIGNMENT, lsStatusId);
			loStatusMap.put(HHSConstants.PROPOSAL_ID, lsProposalId);
			loStatusMap.put(HHSConstants.USER_ID, lsUserId);
			// setting audit bean
			loAuditBean.setAuditTableIdentifier(HHSConstants.AGENCY_AUDIT);
			loAuditBean.setEntityType(HHSConstants.PROPOSAL);
			loAuditBean.setData(HHSConstants.UNLOCK_PROPOSAL);
			loAuditBean.setEntityId(lsProposalId);
			loAuditBean.setEventName(HHSConstants.UNLOCK_PROPOSAL);
			loAuditBean.setUserId(lsUserId);
			loAuditBean.setEventType(HHSConstants.UNLOCK_PROPOSAL);

			// check if Accept Proposal task exists
			HashMap loPropMap = new HashMap();
			loPropMap.put(P8Constants.PE_WORKFLOW_PROPOSAL_ID, lsProposalId);
			loPropMap.put(P8Constants.PROPERTY_PE_TASK_NAME, P8Constants.PE_ACCEPT_PROPOSAL_TASK_NAME);
			loChannel.setData(HHSConstants.AO_HMWF_REQUIRED_PROPS, loPropMap);

			// set attributes in channel object
			loChannel.setData(HHSConstants.STATUS_MAP_KEY, loStatusMap);
			loChannel.setData(HHSConstants.PROPOSAL_ID, lsProposalId);
			loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
			loChannel.setData(HHSConstants.PROCUREMENT_ID, lsProcurementId);

			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.UNLOCK_PROPOSAL_TRANS);
			Boolean lbAcceptProposalReLaunched = (Boolean) loChannel.getData(HHSConstants.UPDATE_PROPOSAL_FLAG);
			Boolean loStatusFlag = (Boolean) loChannel.getData(HHSConstants.STATUS_FLAG);
			if (lbAcceptProposalReLaunched)
			{
				// read the message from the property file
				ApplicationSession.setAttribute(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.UNLOCK_PROPOSAL_SUCCESS), aoRequest, ApplicationConstants.ERROR_MESSAGE);
				ApplicationSession.setAttribute(ApplicationConstants.MESSAGE_PASS_TYPE, aoRequest,
						ApplicationConstants.ERROR_MESSAGE_TYPE);
			}
			else if (!loStatusFlag)
			{
				// added message for proposals that cannot be unlocked
				ApplicationSession.setAttribute(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.PROPOSAL_NOT_UNLOCKED), aoRequest, ApplicationConstants.ERROR_MESSAGE);
				ApplicationSession.setAttribute(ApplicationConstants.MESSAGE_FAIL_TYPE, aoRequest,
						ApplicationConstants.ERROR_MESSAGE_TYPE);
			}
			else
			{
				ApplicationSession.setAttribute(HHSConstants.ERROR_WHILE_PROCESSING_REQUEST, aoRequest,
						ApplicationConstants.ERROR_MESSAGE);
				ApplicationSession.setAttribute(ApplicationConstants.MESSAGE_FAIL_TYPE, aoRequest,
						ApplicationConstants.ERROR_MESSAGE_TYPE);
			}
		}
		catch (ApplicationException loExp)
		{
			ApplicationSession.setAttribute(HHSConstants.ERROR_WHILE_PROCESSING_REQUEST, aoRequest,
					ApplicationConstants.ERROR_MESSAGE);
			ApplicationSession.setAttribute(ApplicationConstants.MESSAGE_FAIL_TYPE, aoRequest,
					ApplicationConstants.ERROR_MESSAGE_TYPE);
			LOG_OBJECT.Error("Error occured while unlocking proposal", loExp);
		}
		aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, lsProcurementId);
		aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.GET_EVALUATION_STATUS);
		aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
	}

	/**
	 * Start : Release 5 This method will be called when user click on Button
	 * "View Progress" from Evaluation Status screen. It will open a popup with
	 * information of current status of all evaluations for each proposal
	 * 
	 * <ul>
	 * <li>1.Add input parameters procurementId and evaluationPoolMappingId to
	 * hashMap</li>
	 * <li>2.Set Attributes in channel and call transaction
	 * "getEvaluationProgress"
	 * </ul>
	 * 
	 * @param aoResourceRequest ResourceRequest
	 * @param aoResourceResponse ResourceResponse
	 */
	@SuppressWarnings("unchecked")
	@ResourceMapping("viewProgressOverlay")
	protected ModelAndView viewProgressOverlay(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws ApplicationException
	{
		HashMap<String, String> loChannelHashMap = new HashMap<String, String>();
		ModelAndView loModelAndView = new ModelAndView(HHSR5Constants.VIEW_EVALUATION_PROGRESS_PATH);
		try
		{
			Channel loChannel = new Channel();
			aoResourceResponse.setContentType(HHSConstants.TEXT_HTML);
			loChannelHashMap.put(HHSConstants.PROCUREMENT_ID,
					aoResourceRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			loChannelHashMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID,
					aoResourceRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID));
			loChannelHashMap.put(HHSConstants.STATUS_ID, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.EVALUATE_PROPOSAL_TASK_SCORES_COMPLETED));
			loChannelHashMap.put(HHSConstants.PROPOSAL_STATUS_ID_KEY, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_NON_RESPONSIVE));
			loChannel.setData(HHSConstants.AO_PARAMETER_MAP, loChannelHashMap);

			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.GET_EVALUATION_PROGRESS,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);

			List<EvaluationBean> loEvaluationDetailsList = (List<EvaluationBean>) loChannel
					.getData(HHSR5Constants.LO_EVALUATION_DETAILS_LIST);
			aoResourceRequest.setAttribute(HHSConstants.EVALUATION_DETAILS_LIST, loEvaluationDetailsList);
		}
		// handling ApplicationException thrown from the transaction layer.
		catch (ApplicationException aoException)
		{
			// populating context data map for exceptional handling
			aoException.setContextData(loChannelHashMap);
			setGenericErrorMessage(aoResourceRequest);
			LOG_OBJECT.Error("Error Occured while opening view Progress Evaluation Overlay", aoException);
		}
		// handling exception other than Application Exception.
		catch (Exception aoException)
		{
			LOG_OBJECT.Error("Error Occured while opening view Progress Evaluation Overlay", aoException);
			setGenericErrorMessage(aoResourceRequest);
		}
		return loModelAndView;
	}

	/**
	 * Start : Release 5 This method will be called when user click on Button
	 * "Save" from Evaluation Setting screen. It will save the documents show
	 * and hide setting for required and optional documents
	 * 
	 * <ul>
	 * <li>1.Add input parameters lsUserId and aoEvaluator to chanel</li>
	 * <li>2.Set Attributes in channel and call transaction
	 * "saveEvaluationDocumentConfiguration"
	 * </ul>
	 * 
	 * @param aoResourceRequest ResourceRequest
	 * @param aoResourceResponse ResourceResponse
	 */
	@ActionMapping(params = "submit_action=saveDocumentSettings")
	protected void saveDocumentSettings(@ModelAttribute("Evaluator") Evaluator aoEvaluator,
			ActionRequest aoActionRequest, ActionResponse aoActionResponse)
	{
		String lsProcurementId = PortalUtil.parseQueryString(aoActionRequest, HHSR5Constants.PROCUREMENT_ID);
		Channel loChannel = new Channel();
		try
		{
			String lsUserId = (String) aoActionRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			loChannel.setData(HHSR5Constants.USER_ID, lsUserId);
			loChannel.setData(HHSR5Constants.DOCUMENT_VISIBILITY_LIST, aoEvaluator.getDocumentVisibilityList());
			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.SAVE_EVALUATION_DOCUMENT_CONFIGURATION,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
		}
		catch (ApplicationException loExp)
		{
			aoActionResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE,
					HHSR5Constants.ERROR_WHILE_PROCESSING_REQUEST);
			aoActionResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE);
			aoActionResponse.setRenderParameter(HHSR5Constants.ERROR_FLAG, HHSR5Constants.ERROR_FLAG);
			aoActionResponse.setRenderParameter(HHSR5Constants.PROCUREMENT_ID, lsProcurementId);
			aoActionResponse.setRenderParameter(HHSR5Constants.ACTION, HHSR5Constants.PROPOSAL_EVALUATION_ACTION);
		}
		aoActionResponse.setRenderParameter(HHSR5Constants.PROCUREMENT_ID, lsProcurementId);
		aoActionResponse.setRenderParameter(HHSR5Constants.ACTION, HHSR5Constants.PROPOSAL_EVALUATION_ACTION);
		setNavigationParamsInRender(aoActionRequest, aoActionResponse);
	}

	/**
	 * <p>
	 * This method is added as a part of Release 5 for Manage
	 * OrganizationModule. This method gets Proposal list Data to display, when
	 * proposal tab is clicked.
	 * <ul>
	 * <li>If user navigate this screen for the first time, then default sort
	 * sets using method "setDefaultProposalSort"</li>
	 * <li>executeTransaction "getProposalDetails" to get proposal list</li>
	 * </ul>
	 * </p>
	 * @param aoRenderRequest a Render Request object
	 * @param aoRenderResponse a Render Response object
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 * @returns ModelAndView containing details of the page to be displayed to
	 *          the end user
	 */
	@SuppressWarnings("unchecked")
	@RenderMapping(params = "next_action=proposallist")
	protected ModelAndView fetchProposalList(RenderRequest aoRenderRequest, RenderResponse aoRenderResponse)
	{
		Channel loChannel = new Channel();
		ProposalDetailsBean loProposalDetailsBean = null;
		String lsNextPage = aoRenderRequest.getParameter(HHSR5Constants.NEXT_PAGE_PARAM);
		try
		{
			String lsProviderId = aoRenderRequest.getParameter(HHSR5Constants.CITY_USER_SEARCH_PROVIDER_ID);
			String lsOrgType = (String) aoRenderRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			PortletSession loPortletSession = aoRenderRequest.getPortletSession();
			Boolean loFirstLoad = false;
			loProposalDetailsBean = (ProposalDetailsBean) ApplicationSession.getAttribute(aoRenderRequest, false,
					HHSConstants.PROPOSAL_DETAIL_BEAN_KEY);
			if (null == loProposalDetailsBean)
			{
				loProposalDetailsBean = new ProposalDetailsBean();
				loFirstLoad = true;
				setDefaultProposalSort(loProposalDetailsBean, lsOrgType);
			}
			if (ApplicationConstants.AGENCY_ORG.equalsIgnoreCase(lsOrgType))
			{
				loProposalDetailsBean.setAgencyId((String) aoRenderRequest.getPortletSession().getAttribute(
						ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE));
			}
			getPagingParams(loPortletSession, loProposalDetailsBean, lsNextPage, HHSR5Constants.PROPOSALS_LIST);
			loProposalDetailsBean.setOrganizationId(lsProviderId.split(HHSR5Constants.TILD)[HHSR5Constants.INT_ZERO]);
			loProposalDetailsBean.setUserRole(lsOrgType);
			
			//Begin R 7.2.0 QC 8914 This is to exclude Draft Procurements
			PortletSession pSession = aoRenderRequest.getPortletSession();
			String roleCurrent = (String) pSession.getAttribute(ApplicationConstants.KEY_SESSION_ROLE_CURRENT, PortletSession.APPLICATION_SCOPE); 
			LOG_OBJECT.Info("Checking curr_Role to exclude Draft Procurements , curr_role = " + roleCurrent);
			if( ApplicationConstants.ROLE_OBSERVER.equalsIgnoreCase(roleCurrent)) {
				loProposalDetailsBean.setRoleCurrent(roleCurrent);
			}
			//End R 7.2.0 QC 8914
			
			loChannel.setData(HHSR5Constants.PROPOSAL_DETAIL_BEAN, loProposalDetailsBean);
			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.GET_PROPOSAL_DETAILS,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			List<ProposalDetailsBean> loProposalList = (List<ProposalDetailsBean>) loChannel
					.getData(HHSR5Constants.LO_PROPOSAL_DATA_LIST);
			
			// BEGIN R 7.2.0 QC 8914
			setOversightRoleFlag(pSession, loProposalList);
			// END R 7.2.0 QC 8914
			
			aoRenderRequest.setAttribute(HHSR5Constants.PROPOSAL_LIST, loProposalList);
			String loProcurementCount = (String) loChannel.getData(HHSR5Constants.TOTAL_COUNT);
			aoRenderRequest.setAttribute(HHSR5Constants.TOTAL_COUNT, loProcurementCount);
			aoRenderRequest.setAttribute(HHSR5Constants.FIRST_LOAD, loFirstLoad);
			aoRenderRequest.setAttribute(HHSR5Constants.CITY_USER_SEARCH_PROVIDER_ID, lsProviderId);
			aoRenderRequest.getPortletSession().setAttribute(ApplicationConstants.ALERT_VIEW_PAGING_RECORDS,
					((loProcurementCount == null) ? HHSR5Constants.INT_ZERO : loProcurementCount),
					PortletSession.APPLICATION_SCOPE);
			aoRenderRequest.getPortletSession().setAttribute(HHSConstants.SORT_TYPE,
					loProposalDetailsBean.getFirstSortType(), PortletSession.APPLICATION_SCOPE);
			aoRenderRequest.getPortletSession().setAttribute(HHSConstants.SORT_BY,
					loProposalDetailsBean.getSortColumnName(), PortletSession.APPLICATION_SCOPE);

			List<HashMap<String, String>> loAgencyDetails = (List<HashMap<String, String>>) loPortletSession
					.getAttribute(HHSConstants.AGENCY_DETAILS, PortletSession.APPLICATION_SCOPE);

			if (loAgencyDetails == null || loAgencyDetails.isEmpty())
			{
				loChannel.setData(HHSR5Constants.ORGTYPE, lsOrgType);
				loAgencyDetails = ContractListUtils.getAgencyDetails(loChannel);
				loPortletSession.setAttribute(HHSConstants.AGENCY_DETAILS, loAgencyDetails,
						PortletSession.APPLICATION_SCOPE);
			}

		}
		catch (ApplicationException aoException)
		{
			LOG_OBJECT.Error("Error Occured while opening view Progress Evaluation Overlay", aoException);
		}
		// handling exception other than Application Exception.
		catch (Exception aoException)
		{
			LOG_OBJECT.Error("Error Occured while opening view Progress Evaluation Overlay", aoException);
		}
		return new ModelAndView(HHSR5Constants.PROPOSAL_LISTS, HHSConstants.PROPOSAL_DETAILS_BEAN_UPPERCASE,
				loProposalDetailsBean);
	}

	/**
	 * <p>
	 * This method is added as a part of Release 5 for Manage Organization
	 * Module This method perform Default Sorting on Proposal list data.
	 * <ul>
	 * <li>If ProposalDetailsBean is null then call setDefaultProposalSort</li>
	 * <li>Default sorting paramter sets using this method</li>
	 * </ul>
	 * </p>
	 * @param aoProposalDetailsBean ProposalDetailsBean
	 * @param asOrgType String to store the type of organisation
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	private void setDefaultProposalSort(ProposalDetailsBean aoProposalDetailsBean, String asOrgType)
			throws ApplicationException
	{
		String lsSecondSort = null;
		if (asOrgType.equalsIgnoreCase(HHSR5Constants.USER_CITY))
		{
			lsSecondSort = HHSR5Constants.AGENCY_ID;
		}
		else if (asOrgType.equalsIgnoreCase(HHSR5Constants.USER_AGENCY))
		{
			lsSecondSort = HHSR5Constants.PROCUREMENT_TITLES;
		}
		aoProposalDetailsBean.setFirstSort(HHSR5Constants.MODIFIED_DATE);
		aoProposalDetailsBean.setFirstSortType(ApplicationConstants.DESCENDING);
		aoProposalDetailsBean.setFirstSortDate(ApplicationConstants.BOOLEAN_TRUE);
		aoProposalDetailsBean.setSecondSort(lsSecondSort);
		aoProposalDetailsBean.setSecondSortType(ApplicationConstants.ASCENDING);
		aoProposalDetailsBean.setSecondSortDate(ApplicationConstants.BOOLEAN_FALSE);
		List<String> loStatusList = new ArrayList<String>();
		loStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_PROPOSAL_SUBMITTED));
		loStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.PROPOSAL_ACCEPTED_FOR_EVALUATION_KEY));
		loStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_PROPOSAL_RETURNED_FOR_REVISION));
		loStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_PROPOSAL_SELECTED));
		loStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_PROPOSAL_NOT_SELECTED));
		loStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_PROPOSAL_NON_RESPONSIVE));
		loStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_PROPOSAL_EVALUATED));
		loStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_PROPOSAL_SCORES_RETURNED));
		loStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_PROPOSAL_PENDING_REASSIGNMENT));
		aoProposalDetailsBean.setProposalStatusList(loStatusList);
	}

	/**
	 * <p>
	 * This method is added as a part of Release 5 for Manage Organization
	 * Module This method perform Filter actions on Proposal List data displayed
	 * on screen.
	 * <ul>
	 * <li>get parameter (next_action, cityUserSearchProviderId, nextPage)from
	 * Request</li>
	 * <li>set aoProposalDetailsBean in ApplicationSession attribute</li>
	 * </ul>
	 * </p>
	 * @param aoProposalDetailsBean ProposalDetailsBean
	 * @param aoActionRequest an Action Request object
	 * @param aoActionResponse an Action Response object
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	@ActionMapping(params = "submit_action=filterProposal")
	protected void actionProposalFilter(
			@ModelAttribute("ProposalDetailsBean") ProposalDetailsBean aoProposalDetailsBean,
			ActionRequest aoActionRequest, ActionResponse aoActionResponse) throws ApplicationException
	{
		try
		{
			String lsNextAction = aoActionRequest.getParameter(ApplicationConstants.NEXT_ACTION);
			String lsUserOrgType = (String) aoActionRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			String lsProviderId = aoActionRequest.getParameter(HHSR5Constants.CITY_USER_SEARCH_PROVIDER_ID);
			if (lsNextAction.equalsIgnoreCase(HHSR5Constants.SORT_PROPOSAL_LIST))
			{
				String lsSortType = PortalUtil.parseQueryString(aoActionRequest, HHSConstants.SORT_TYPE);
				String lsColumnName = PortalUtil.parseQueryString(aoActionRequest, HHSConstants.COLUMN_NAME);
				getSortDetailsFromXML(lsColumnName, lsUserOrgType,
						PortalUtil.parseQueryString(aoActionRequest, HHSConstants.SORT_GRID_NAME),
						aoProposalDetailsBean, lsSortType);
			}
			else if (lsNextAction.equalsIgnoreCase(HHSR5Constants.FETCH_NEXT_PROPOSAL))
			{
				aoActionResponse.setRenderParameter(HHSConstants.NEXT_PAGE_PARAM,
						aoActionRequest.getParameter(HHSR5Constants.NEXT_PAGE));
			}
			ApplicationSession.setAttribute(aoProposalDetailsBean, aoActionRequest,
					HHSConstants.PROPOSAL_DETAIL_BEAN_KEY);
			aoActionResponse.setRenderParameter(HHSR5Constants.CITY_USER_SEARCH_PROVIDER_ID, lsProviderId);
			aoActionResponse.setRenderParameter(ApplicationConstants.NEXT_ACTION, HHSR5Constants.PROPOSAL_LISTS);
			aoActionResponse.setRenderParameter(HHSR5Constants.ACTION, HHSR5Constants.PROPOSAL_DETAILS_MAP);
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception Occured on action performed on Proposal List screen :", loExp);
			String lsErrorMsg = HHSR5Constants.ERROR_WHILE_PROCESSING_REQUEST;
			aoActionRequest.setAttribute(HHSR5Constants.ERROR_MESSAGE_BUDGET_LIST, lsErrorMsg);
			aoActionRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
	}

	/**
	 * <p>
	 * This method is added as a part of Release 5 for Manage Organization
	 * Module. This method navigates to Procurement Summary Screen, onclick of
	 * Procurement Title Hyperlink
	 * <ul>
	 * <li>get procurementId from Request</li>
	 * <li>set paramter in lsProcurementSummaryPath</li>
	 * </ul>
	 * </p>
	 * @param aoActionRequest an Action Request object
	 * @param aoActionResponse an Action Response object
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	@ActionMapping(params = "submit_action=viewProcurement")
	protected void actionViewProcurementSummaryScreen(ActionRequest aoActionRequest, ActionResponse aoActionResponse)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Started actionViewProcurementSummary Function");
		String lsProcurementSummaryPath = aoActionRequest.getScheme() + HHSR5Constants.NOTIFICATION_HREF_1
				+ aoActionRequest.getServerName() + HHSR5Constants.COLON + aoActionRequest.getServerPort()
				+ aoActionRequest.getContextPath() + ApplicationConstants.PORTAL_URL + HHSR5Constants.AND_SIGN
				+ HHSR5Constants.VIEW_PROCUREMENT_URL + aoActionRequest.getParameter(HHSR5Constants.PROCUREMENT_ID);
		try
		{
			/** [Start] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
			//aoActionResponse.sendRedirect(lsProcurementSummaryPath);
			aoActionResponse.sendRedirect(HHSUtil.sanitizeCarriageReturns(lsProcurementSummaryPath));
			/** [End] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception Occured while displaying navigation View Procurement : ", loExp);
			String lsErrorMsg = HHSR5Constants.ERROR_WHILE_PROCESSING_REQUEST;
			aoActionRequest.setAttribute(HHSR5Constants.ERROR_MESSAGE_BUDGET_LIST, lsErrorMsg);
			aoActionRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
	}

	/**
	 * <p>
	 * This method is added as a part of Release 5 for Manage Organization
	 * Module. This method navigate to Competition Pool screen, onclick of
	 * Competition Pool Hyperlink.
	 * <ul>
	 * <li>get procurementId, evaluationPoolMappingId, competitionPoolId,
	 * evaluationGroupId from Request</li>
	 * <li>set parameters in loProcurementSummaryPathUrl</li>
	 * </ul>
	 * </p>
	 * @param aoActionRequest an Action Request object
	 * @param aoActionResponse an Action Response object
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	@ActionMapping(params = "submit_action=viewCompetitionPool")
	protected void actionViewCompetitionPool(ActionRequest aoActionRequest, ActionResponse aoActionResponse)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Started actionViewCompetitionPool Function");
		try
		{
			StringBuffer loProcurementSummaryPathUrl = new StringBuffer(256);
			loProcurementSummaryPathUrl.append(aoActionRequest.getScheme()).append(HHSR5Constants.NOTIFICATION_HREF_1)
					.append(aoActionRequest.getServerName()).append(HHSR5Constants.COLON)
					.append(aoActionRequest.getServerPort()).append(aoActionRequest.getContextPath())
					.append(ApplicationConstants.PORTAL_URL).append(HHSR5Constants.AND_SIGN)
					.append(HHSR5Constants.COMPETITION_POOL_URL)
					.append(aoActionRequest.getParameter(HHSR5Constants.PROCUREMENT_ID))
					.append(HHSR5Constants.EVAL_POOL_MAPPING_PARAMETER)
					.append(aoActionRequest.getParameter(HHSR5Constants.EVALUATION_POOL_MAPPING_ID))
					.append(HHSR5Constants.AND_SIGN).append(HHSR5Constants.RENDER_ACTION).append(HHSR5Constants.EQUAL)
					.append(HHSR5Constants.NAVIGATE_TO_COMPETITION_POOL).append(HHSR5Constants.COMP_POOL_PARAMETER)
					.append(aoActionRequest.getParameter(HHSR5Constants.COMPETITION_POOL_ID))
					.append(HHSR5Constants.EVAL_GROUP_PARAMETER)
					.append(aoActionRequest.getParameter(HHSR5Constants.EVALUATION_GROUP_ID));
			/** [Start] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
			//aoActionResponse.sendRedirect(loProcurementSummaryPathUrl.toString());
			aoActionResponse.sendRedirect(HHSUtil.sanitizeCarriageReturns(loProcurementSummaryPathUrl.toString()));
			/** [End] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception Occured while displaying navigation View Procurement : ", loExp);
			String lsErrorMsg = HHSR5Constants.ERROR_WHILE_PROCESSING_REQUEST;
			aoActionRequest.setAttribute(HHSR5Constants.ERROR_MESSAGE_BUDGET_LIST, lsErrorMsg);
			aoActionRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
	}

	/**
	 * <p>
	 * This method is added as a part of Release 5 for Manage Organization
	 * Module. This method navigate to Financials screen, onclick of Financial
	 * sub-tab.
	 * <ul>
	 * <li>get Provider name from request</li>
	 * <li>set Provider name into PortletSession</li>
	 * <li>create url path to navigate to Financial screen</li>
	 * </ul>
	 * </p>
	 * @param aoActionRequest an Action Request object
	 * @param aoActionResponse an Action Response object
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	@ActionMapping(params = "submit_action=viewFinancial")
	protected void actionViewFinancial(ActionRequest aoActionRequest, ActionResponse aoActionResponse)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Started actionViewFinancial Function");
		try
		{
			String lsProviderName = aoActionRequest.getParameter(HHSR5Constants.PROPERTY_PE_PROVIDER_NAME);
			if (StringUtils.isNotBlank(lsProviderName))
			{
				PortletSession loPortletSession = aoActionRequest.getPortletSession();
				loPortletSession.setAttribute(HHSR5Constants.PROPERTY_PE_PROVIDER_NAME, lsProviderName,
						PortletSession.APPLICATION_SCOPE);
			}
			StringBuffer loProcurementSummaryPathUrl = new StringBuffer(256);
			loProcurementSummaryPathUrl.append(aoActionRequest.getScheme()).append(HHSR5Constants.NOTIFICATION_HREF_1)
					.append(aoActionRequest.getServerName()).append(HHSR5Constants.COLON)
					.append(aoActionRequest.getServerPort()).append(aoActionRequest.getContextPath())
					.append(HHSR5Constants.CONTRACT_LIST_URL);
			aoActionResponse.sendRedirect(loProcurementSummaryPathUrl.toString());
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception Occured while displaying navigation View Procurement : ", loExp);
			String lsErrorMsg = HHSR5Constants.ERROR_WHILE_PROCESSING_REQUEST;
			aoActionRequest.setAttribute(HHSR5Constants.ERROR_MESSAGE_BUDGET_LIST, lsErrorMsg);
			aoActionRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
	}
	
	/**
	 * R 7.2.0 QC 8419
	 * This method sets the User 'CurrRole' on each Proposal, if HttpSession has the value of OBSERVER for the current user.
	 * 
	 * @param aoSession
	 * @param loProcurementList
	 */
	private void setOversightRoleFlag(PortletSession aoSession, List<ProposalDetailsBean> loProposalList) {
		
		String roleCurrent = (String)aoSession.getAttribute(ApplicationConstants.KEY_SESSION_ROLE_CURRENT, PortletSession.APPLICATION_SCOPE); 
		if( ApplicationConstants.ROLE_OBSERVER.equalsIgnoreCase(roleCurrent)) {
			
			LOG_OBJECT.Info("Setting currRole as OBSERVER for ProposalList = " + loProposalList);
			for (ProposalDetailsBean loProposal : loProposalList)
			{
				loProposal.setRoleCurrent(roleCurrent);
			}

		}
	}
	
	/**
	 * R 7.2.0 QC 8419
	 * This method sets the User 'CurrRole' on each EvaluationSummaryBean, if HttpSession has the value of OBSERVER for the current user.
	 * 
	 * @param aoSession
	 * @param loProcurementList
	 */
	private void setOversightRoleFlagForEvaluationSummary(PortletSession aoSession, List<EvaluationSummaryBean> evaluationSummaryList) {
		
		String roleCurrent = (String)aoSession.getAttribute(ApplicationConstants.KEY_SESSION_ROLE_CURRENT, PortletSession.APPLICATION_SCOPE); 
		if( ApplicationConstants.ROLE_OBSERVER.equalsIgnoreCase(roleCurrent)) {
			
			LOG_OBJECT.Info("Setting currRole as OBSERVER for EvaluationSummaryList = " + evaluationSummaryList);
			for (EvaluationSummaryBean evaluationSummaryBean : evaluationSummaryList)
			{
				evaluationSummaryBean.setRoleCurrent(roleCurrent);
			}

		}
	}
}
