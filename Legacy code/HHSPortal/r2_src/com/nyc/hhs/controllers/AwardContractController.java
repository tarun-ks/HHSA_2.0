package com.nyc.hhs.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
import com.nyc.hhs.controllers.util.BaseControllerUtil;
import com.nyc.hhs.controllers.util.ContractListUtils;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.AuthenticationBean;
import com.nyc.hhs.model.AwardBean;
import com.nyc.hhs.model.AwardsContractSummaryBean;
import com.nyc.hhs.model.EPinDetailBean;
import com.nyc.hhs.model.EvaluationBean;
import com.nyc.hhs.model.EvaluationGroupAwardBean;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.Procurement;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.HHSPortalUtil;
import com.nyc.hhs.util.PortalUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This controller class will handle all the request made by user from the
 * Awards and Contracts tab and screen like rendering evaluation group summary
 * for awards, viewing awards and contracts summary, viewing awards and contract
 * details, assigning award epin, viewing apt progress, cancelling award. Also
 * it will handle the sorting of the columns and navigating through different
 * pages for Summary screens.
 * 
 * This controller will be executed from different screens like ids 1- S282 2-
 * S284 3- S222 4- S249
 */
@Controller(value = "awardContractHandler")
@RequestMapping("view")
public class AwardContractController extends BaseControllerSM
{
	/**
	 * This method will provide a LogInfo object for logging purposes
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(AwardContractController.class);

	/**
	 * This method will return Procurement object
	 */
	/**
	 * @return procurement object
	 */
	@ModelAttribute("Procurement")
	public Procurement getCommandObject()
	{
		return new Procurement();
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
	 * This method is for customization of date
	 * @param aoBinder Databinder object
	 */
	@InitBinder
	public void initBinder(WebDataBinder aoBinder)
	{
		SimpleDateFormat loDateFormat = new SimpleDateFormat(HHSConstants.MMDDYYFORMAT);
		aoBinder.registerCustomEditor(Date.class, null, new CustomDateEditor(loDateFormat, true));
	}

	/**
	 * Validator varibale
	 */
	@Autowired
	private Validator validator;

	/**
	 * This method is pointing to Validator
	 * @param aoValidator validator param
	 */
	public void setValidator(Validator aoValidator)
	{
		this.validator = aoValidator;
	}

	/**
	 * This function will load Awards & Contracts page and allows Agency user to
	 * see a list of providers that only have a status of ‘Selected’ for the
	 * procurement.
	 * <ul>
	 * <li>1. Setting procurementId and provider status</li>
	 * <li>2. Set Channel object and call transaction displayAwardsDetails</li>
	 * <li>3. Table will show all Providers that have a status of “Selected” for
	 * the Procurement and organization Names for those Providers that are
	 * Selected If more than 1 proposal has been Selected for the same Provider,
	 * only 1 row will show for that Provider</li>
	 * <li>4. Award E-PIN column - if E-PIN has not been assigned to Provider,
	 * display static text “Not assigned” If E-PIN has been assigned via S249
	 * (via OnSelect of “Assign E-PIN” in the Actions dropdown), value is
	 * <E-PIN></li>
	 * <li>5. Contract ID – Displays the FMS CT# retrieved from FMS for the
	 * E-PIN</li>
	 * <li>6. Amount – displays the award amount for the proposal,If multiple
	 * proposals have been selected for the same Provider, this field will be
	 * the SUM of the award amount for each proposal (in S223.05)</li>
	 * <li>7. Load awardsAndContracts jsp page with details for the respective
	 * columns
	 * </ul>
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * @param aoRequest - a RenderRequest object
	 * @param aoResponse - a RenderResponse object
	 * @return loModelAndView - jsp name
	 * 
	 */
	@RenderMapping(params = "render_action=awardsAndContracts")
	protected ModelAndView renderAwardsAndContractsDetails(RenderRequest aoRequest, RenderResponse aoResponse)

	{
		ModelAndView loModelAndView = null;
		try
		{
			String lsProcurementId = PortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
			getPageHeader(aoRequest, lsProcurementId);
			String lsSelectChildScreen = (String) aoRequest.getAttribute(HHSConstants.SELECTED_CHILD_TAB);
			if (lsSelectChildScreen != null
					&& lsSelectChildScreen.equalsIgnoreCase(HHSConstants.EVALUATION_GROUPS_SUMMARY_AWARD))
			{
				loModelAndView = renderEvaluationGroupsSummaryAward(aoRequest);
			}
			else if (lsSelectChildScreen != null
					&& lsSelectChildScreen.equalsIgnoreCase(HHSConstants.AWARDS_AND_CONTRACTS_SUMMARY))
			{
				loModelAndView = renderAwardsAndContractSummary(aoRequest);
			}
			else if (lsSelectChildScreen != null
					&& lsSelectChildScreen.equalsIgnoreCase(HHSConstants.AWARDS_CONTRACTS_SCREEN))
			{
				loModelAndView = renderAwardAndContracts(aoRequest);
			}
		}
		// Catch the exception thrown by transaction and set the error message
		// in request object and pass to jsp
		catch (ApplicationException loExp)
		{
			setExceptionMessageInResponse(aoRequest);
		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error occured while processing award and contract ", loExp);
			setExceptionMessageInResponse(aoRequest);
		}
		return loModelAndView;
	}

	/**
	 * This method is used to render evaluation groups awards summary for the
	 * input procurement Id
	 * 
	 * <ul>
	 * <li>1.Get the procurement Id from request</li>
	 * <li>2.Get pagination parameters for Evaluation Groups Awards screen by
	 * calling method getPagingParams() from BaseController</li>
	 * <li>3.Set required attributes in channel object</li>
	 * <li>4.Execute transaction <b>fetchEvaluationGroupAwards</b> to get
	 * Evaluation Groups Awards details</li>
	 * <li>5.Set transaction output in request and render to
	 * evaluationGroupsSummaryAward jsp</li>
	 * </ul>
	 * <ul>
	 * <li>new Method in R4</li>
	 * </ul>
	 * @param aoRequest RenderRequest object
	 * @return ModelAndView object containing jsp name
	 * @throws ApplicationException If an Application Exception occurs
	 * 
	 * 
	 */
	private ModelAndView renderEvaluationGroupsSummaryAward(RenderRequest aoRequest) throws ApplicationException
	{
		String lsProcurementId = PortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
		EvaluationGroupAwardBean loEvaluationGroupAwardBean = null;
		loEvaluationGroupAwardBean = (EvaluationGroupAwardBean) aoRequest.getPortletSession().getAttribute(
				HHSConstants.SESSION_EVALUATION_GROUP_AWARD_BEAN, PortletSession.PORTLET_SCOPE);
		if (null == loEvaluationGroupAwardBean)
		{
			loEvaluationGroupAwardBean = new EvaluationGroupAwardBean();
		}
		loEvaluationGroupAwardBean.setProcurementId(lsProcurementId);
		String lsNextPage = aoRequest.getParameter(HHSConstants.NEXT_PAGE_PARAM);
		getPagingParams(aoRequest.getPortletSession(), loEvaluationGroupAwardBean, lsNextPage,
				HHSConstants.AWARDS_CONTRACT_SUMMARY_KEY);
		Channel loChannel = new Channel();
		loChannel.setData(HHSConstants.EVALUATION_GROUP_AWARD_BEAN, loEvaluationGroupAwardBean);
		loChannel.setData(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_EVALUATION_GROUP_AWARD);
		Integer loEvaluationSummaryListCount = (Integer) loChannel.getData(HHSConstants.EVALUATION_GROUP_AWARD_COUNT);
		aoRequest.setAttribute(HHSConstants.EVALUATION_SUMMARY_LIST,
				loChannel.getData(HHSConstants.EVALUATION_GROUP_AWARDS));
		aoRequest.getPortletSession().setAttribute(ApplicationConstants.ALERT_VIEW_PAGING_RECORDS,
				((loEvaluationSummaryListCount == null) ? 0 : loEvaluationSummaryListCount),
				PortletSession.APPLICATION_SCOPE);
		aoRequest.getPortletSession().setAttribute(HHSConstants.SORT_TYPE,
				loEvaluationGroupAwardBean.getFirstSortType(), PortletSession.APPLICATION_SCOPE);
		aoRequest.getPortletSession().setAttribute(HHSConstants.SORT_BY,
				loEvaluationGroupAwardBean.getSortColumnName(), PortletSession.APPLICATION_SCOPE);
		aoRequest.getPortletSession().removeAttribute(HHSConstants.SESSION_EVALUATION_GROUP_AWARD_BEAN);
		return new ModelAndView(HHSConstants.EVALUATION_GROUPS_SUMMARY_AWARD_JSP);
	}

	/**
	 * This method is used to render awards and contracts summary for the input
	 * procurement Id and evaluation group Id
	 * 
	 * <ul>
	 * <li>1.Get the procurement Id and evaluation group Id from request</li>
	 * <li>2.Get pagination parameters for Awards and Contracts Summary screen
	 * by calling method getPagingParams() from BaseController</li>
	 * <li>3.Set required attributes in channel object</li>
	 * <li>4.Execute transaction <b>fetchGroupAwardsContracts</b> to get Awards
	 * and Contracts Summary</li>
	 * <li>5.Set transaction output in request and render to
	 * awardsAndContractSummary jsp</li>
	 * </ul>
	 * <ul>
	 * <li>new method in R4</li>
	 * </ul>
	 * @param aoRequest RenderRequest object
	 * @return ModelAndView object containing jsp name
	 * @throws ApplicationException If an Application Exception occurs New
	 * 
	 */
	private ModelAndView renderAwardsAndContractSummary(RenderRequest aoRequest) throws ApplicationException
	{
		String lsProcurementId = PortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
		String lsEvaluationGroupId = PortalUtil.parseQueryString(aoRequest, HHSConstants.EVALUATION_GROUP_ID);
		Channel loChannel = new Channel();
		AwardsContractSummaryBean loAwardsContractSummaryBean = null;
		loAwardsContractSummaryBean = (AwardsContractSummaryBean) aoRequest.getPortletSession().getAttribute(
				HHSConstants.AWARD_CONTRACT_SUMMARY_BEAN, PortletSession.PORTLET_SCOPE);
		if (null == loAwardsContractSummaryBean)
		{
			loAwardsContractSummaryBean = new AwardsContractSummaryBean();
		}
		loAwardsContractSummaryBean.setProcurementId(lsProcurementId);
		loAwardsContractSummaryBean.setEvaluationGroupId(lsEvaluationGroupId);
		String lsNextPage = aoRequest.getParameter(HHSConstants.NEXT_PAGE_PARAM);
		// set pagination parameters in session object by calling
		// getPagingParams()
		getPagingParams(aoRequest.getPortletSession(), loAwardsContractSummaryBean, lsNextPage,
				HHSConstants.AWARDS_CONTRACT_SUMMARY_KEY);
		loChannel.setData(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
		loChannel.setData(HHSConstants.EVALUATION_GROUP_ID, lsEvaluationGroupId);
		loChannel.setData(HHSConstants.AWARD_CONTRACT_SUMMARY_BEAN, loAwardsContractSummaryBean);
		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_GROUP_AWARDS_CONTRACTS);
		Integer loAwardContractSummaryCount = (Integer) loChannel.getData(HHSConstants.GROUP_AWARD_CONTRACT_COUNT);
		aoRequest.setAttribute(HHSConstants.GROUP_TITLE_MAP, loChannel.getData(HHSConstants.GROUP_TITLE_MAP));
		aoRequest.setAttribute(HHSConstants.GROUP_AWARDS_CONTRACTS_LIST,
				loChannel.getData(HHSConstants.GROUP_AWARDS_CONTRACTS_LIST));
		aoRequest.setAttribute(HHSConstants.EVALUATION_GROUP_LIST,
				loChannel.getData(HHSConstants.EVALUATION_GROUP_LIST));
		aoRequest.getPortletSession().setAttribute(ApplicationConstants.ALERT_VIEW_PAGING_RECORDS,
				((loAwardContractSummaryCount == null) ? 0 : loAwardContractSummaryCount),
				PortletSession.APPLICATION_SCOPE);
		aoRequest.getPortletSession().setAttribute(HHSConstants.SORT_TYPE,
				loAwardsContractSummaryBean.getFirstSortType(), PortletSession.APPLICATION_SCOPE);
		aoRequest.getPortletSession().setAttribute(HHSConstants.SORT_BY,
				loAwardsContractSummaryBean.getSortColumnName(), PortletSession.APPLICATION_SCOPE);
		aoRequest.getPortletSession().removeAttribute(HHSConstants.EVAL_SUMMARY_SESSION_BEAN);
		return new ModelAndView(HHSConstants.AWARDS_AND_CONTRACTS_SUMMARY_JSP);
	}

	/**
	 * This method is used to render awards and contracts summary for the input
	 * procurement Id, evaluation group Id and evaluation pool mapping Id
	 * 
	 * <ul>
	 * <li>1.Get the procurement Id, evaluation group Id and evaluation pool
	 * mapping Id from request</li>
	 * <li>2.Get pagination parameters for Awards and Contracts screen by
	 * calling method getPagingParams() from BaseController</li>
	 * <li>3.Set required attributes in channel object</li>
	 * <li>4.Execute transaction <b>actionGetawardAndContractsList</b> to get
	 * Awards and Contracts Details for given evaluation pool mapping Id</li>
	 * <li>5.Set transaction output in request and render to awardAndContracts
	 * jsp</li>
	 * </ul>
	 * <ul>
	 * <li>New method in R4</li>
	 * </ul>
	 * @param aoRequest RenderRequest object
	 * @return ModelAndView object containing jsp name
	 * @throws ApplicationException If an Application Exception occurs New
	 * 
	 */
	@SuppressWarnings("unchecked")
	private ModelAndView renderAwardAndContracts(RenderRequest aoRequest) throws ApplicationException
	{   //**  start QC 8914 R7.2 read only role **/
		LOG_OBJECT.Debug("in renderAwardAndContracts ");
		String role_current = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_ROLE_CURRENT, PortletSession.APPLICATION_SCOPE);
		LOG_OBJECT.Debug(" role_current :: "+role_current);
		//**  end QC 8914 R7.2 read only role **/
		List<AwardBean> loAwardList;
		ModelAndView loModelAndView;
		Channel loChannel = new Channel();
		String lsProcurementId = PortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
		String lsEvalGroupMappingId = PortalUtil.parseQueryString(aoRequest, HHSConstants.EVALUATION_POOL_MAPPING_ID);
		String lsEvaluationGroupId = PortalUtil.parseQueryString(aoRequest, HHSConstants.EVALUATION_GROUP_ID);
		String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
		Map<String, Object> loAwardMap = new HashMap<String, Object>();
		loAwardMap.put(HHSConstants.PROCUREMENT_ID, lsProcurementId);
		loAwardMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalGroupMappingId);
		loAwardMap.put(HHSConstants.CONTRACT_TYPE_ID_KEY1, HHSConstants.ONE);
		loAwardMap.put(HHSConstants.CONTRACT_TYPE_ID_KEY5, HHSConstants.FIVE);
		// pagination
		AwardBean loAwardBean = (AwardBean) aoRequest.getPortletSession().getAttribute(HHSConstants.AWARD_BEAN,
				PortletSession.PORTLET_SCOPE);
		if (null == loAwardBean)
		{
			loAwardBean = new AwardBean();
		}
		PortletSession loSession = aoRequest.getPortletSession();
		String loUserRole = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ROLE,
				PortletSession.APPLICATION_SCOPE);
		loAwardBean.setUserRole(loUserRole);
		loAwardBean.setProcurementId(lsProcurementId);
		loAwardBean.setPaginationEnable(HHSConstants.YES_UPPERCASE);
		String lsNextPage = aoRequest.getParameter(HHSConstants.NEXT_PAGE_PARAM);
		getPagingParams(loSession, loAwardBean, lsNextPage, HHSConstants.AWARD_LIST_COUNT);
		loAwardMap.put(HHSConstants.START_NODE, loAwardBean.getStartNode());
		loAwardMap.put(HHSConstants.END_NODE, loAwardBean.getEndNode());
		loChannel.setData(HHSConstants.PROCUREMENT_ID, lsProcurementId);
		loChannel.setData(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalGroupMappingId);
		loChannel.setData(HHSConstants.EVALUATION_GROUP_ID, lsEvaluationGroupId);
		loChannel.setData(HHSConstants.AWARD_MAP, loAwardMap);
		loChannel.setData(HHSConstants.ORG_TYPE, lsUserOrgType);
		loChannel.setData(HHSConstants.USER_ROLE, loUserRole);
		loChannel.setData(HHSConstants.CONTRACT_ID_WORKFLOW, aoRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW));
		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.ACTION_GET_AWARD_AND_CONTRACTS_LIST);
		// pagination
		Integer loAwardCount = (Integer) loChannel.getData(HHSConstants.AWARD_COUNT);
		// R5 Changes starts
		Integer loCancelAllAwardCount = (Integer) loChannel.getData(HHSR5Constants.CANCEL_AWARDS_COUNT);
		aoRequest.setAttribute(HHSR5Constants.CANCEL_AWARDS_COUNT, loCancelAllAwardCount);
		// R5 Changes starts

		loSession.setAttribute(ApplicationConstants.ALERT_VIEW_PAGING_RECORDS, loAwardCount,
				PortletSession.APPLICATION_SCOPE);
		loAwardList = (List<AwardBean>) loChannel.getData(HHSConstants.AWARD_LIST);
		LOG_OBJECT.Debug("List of Action :: "+ loAwardList.toString());
		
		String lsProcStatus = ((Integer) loChannel.getData(HHSConstants.STATUS_ID)).toString();
		// adding new message- No proposals were selected for award for this
		// procurement
		EvaluationBean loEvalBean = (EvaluationBean) loChannel.getData(HHSConstants.AWARD_REVIEW_STATUS);
		if (loEvalBean != null && loEvalBean.getAwardReviewStatus() != null
				&& loEvalBean.getAwardReviewStatus().equals(HHSConstants.STATUS_APPROVED)
				&& (loAwardList == null || loAwardList.isEmpty()))
		{
			aoRequest.setAttribute(HHSConstants.NO_PROPOSALS_FOR_AWARD_FLAG, HHSConstants.YES);
		}
		if (null != loAwardList && !loAwardList.isEmpty())
		{
			// for org type check
			for (int liCount = 0; liCount < loAwardList.size(); liCount++)
			{
				((AwardBean) loAwardList.get(liCount)).setOrgType(lsUserOrgType);
				((AwardBean) loAwardList.get(liCount)).setUserRole(loUserRole);
				((AwardBean) loAwardList.get(liCount)).setProcurementStatus(lsProcStatus);
				/*start  QC 8914 read only role R 7.2*/
				if( ApplicationConstants.ROLE_OBSERVER.equalsIgnoreCase(role_current))  
				{
					((AwardBean) loAwardList.get(liCount)).setRoleCurrent(ApplicationConstants.ROLE_OBSERVER);
					LOG_OBJECT.Debug("AwardBean has been updated with OBSERVER flag :: "+ loAwardList.get(liCount).toString());
				}	
				/*end  QC 8914 read only role R 7.2*/
			
			}
		}
		aoRequest.setAttribute(HHSConstants.AWARD_LIST, loAwardList);
		aoRequest.setAttribute(HHSConstants.GROUP_TITLE_MAP, loChannel.getData(HHSConstants.GROUP_TITLE_MAP));
		aoRequest.setAttribute(HHSConstants.COMPETITION_POOL_LIST,
				loChannel.getData(HHSConstants.COMPETITION_POOL_LIST));
		aoRequest.setAttribute(HHSConstants.EVALUATION_GROUP_ID, loChannel.getData(HHSConstants.EVALUATION_GROUP_ID));
		// If we are navigating from Cancel Award Screen
		if (aoRequest.getParameter(HHSConstants.PARAM_VALUE) != null
				&& aoRequest.getParameter(HHSConstants.PARAM_VALUE).equalsIgnoreCase(HHSConstants.CANCEL_AWARD))
		{
			String lsCancelMessage = MessageFormat.format(PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.CANCEL_AWARD_SUCESS), loChannel
					.getData(HHSConstants.ORGANIZATN_NAME));
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsCancelMessage);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_PASS_TYPE);
		}
		aoRequest.setAttribute(HHSConstants.CHANNEL_ACCESS, loChannel);
		// Changes for 7219
		// R5 change starts
		if (null != aoRequest.getParameter(HHSR5Constants.REFRESH_ON_CANCEL_ALL))
		{
			String lsMessage = MessageFormat.format(PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSR5Constants.MESSAGE_CANCEL_ALL_AWARDS), aoRequest
					.getParameter(HHSConstants.COMPETITION_POOL_TITLE));
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsMessage);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_PASS_TYPE);
		}
		// R5 change ends
		loModelAndView = new ModelAndView(HHSConstants.AWARD_AND_CONTRACTS, HHSConstants.AWARD_BEAN, loAwardList);
		return loModelAndView;
	}

	/**
	 * The method will open the overlay content for Cancel Award
	 * <ul>
	 * <li>1.Fetch the ContractID and Set into Channel</li>
	 * <li>2.Calling transaction fetchAwardsDetails</li>
	 * <li>3.Getting Required Details i.e Organization Name,Award E-Pin,CT Award
	 * Amount,Status [Call method fetchAwardsDetails in transaction,QueryId
	 * :fetchAwardsDetails]</li>
	 * <li>4.Set above fetched Values in Request</li>
	 * <li>A ModelAndView object which renders the content of cancel Award jsp.</li>
	 * </ul>
	 * <ul>
	 * <li>method updated in R4</li>
	 * </ul>
	 * @param aoResourceRequest ResourceRequest
	 * @param aoResourceResponse ResourceResponse
	 * @param aoModel Model
	 * @return String
	 */
	@ResourceMapping("cancelAwardOverlay")
	protected String getCancelAwardOverLay(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse,
			Model aoModel)
	{
		try
		{
			// getting the contract id from the request.
			String lsContractId = aoResourceRequest.getParameter(HHSConstants.CONTRACT_ID);
			String lsEvaluationPoolMappingId = aoResourceRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID);
			Channel loChannel = new Channel();
			// populating the channel object with the contract id
			loChannel.setData(HHSConstants.CONTRACT_ID, lsContractId);
			loChannel.setData(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvaluationPoolMappingId);
			// Transaction framework executes the fetchAwardsDetails transaction
			// and populate the channel object with the result
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_AWARDS_DETAILS);
			// getting the award details from the channel object
			AwardBean loAwardBean = (AwardBean) loChannel.getData(HHSConstants.AWARD_DETAILS);
			// setting the required parameters in the request to be used while
			// rendering the jsp.
			aoResourceRequest.setAttribute(HHSConstants.AWARD_BEAN_VALUE_OBJECT, loAwardBean);
			aoResourceRequest.setAttribute(HHSConstants.CONTRACT_ID, lsContractId);
			aoResourceRequest.setAttribute(HHSConstants.ORGANIZATION_ID,
					aoResourceRequest.getParameter(HHSConstants.ORGANIZATION_ID));
			aoResourceRequest.setAttribute(HHSConstants.PROCUREMENT_ID,
					aoResourceRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			aoResourceRequest
					.setAttribute(HHSConstants.AWARD_ID, aoResourceRequest.getParameter(HHSConstants.AWARD_ID));
			aoResourceRequest.setAttribute(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvaluationPoolMappingId);
			aoResourceRequest.setAttribute(HHSConstants.EVALUATION_GROUP_ID,
					aoResourceRequest.getParameter(HHSConstants.EVALUATION_GROUP_ID));
			aoResourceRequest.setAttribute(HHSConstants.COMPETITION_POOL_STATUS,
					loChannel.getData(HHSConstants.COMPETITION_POOL_STATUS));
		}

		// handling ApplicationException thrown from the transaction layer.
		catch (ApplicationException aoException)
		{
			// populating context data map for exceptional handling
			Map<String, String> loParamMap = new HashMap<String, String>();
			loParamMap.put(HHSConstants.CONTRACT_ID, aoResourceRequest.getParameter(HHSConstants.CONTRACT_ID));
			setExceptionMessageInResponse(aoResourceRequest);
			aoException.setContextData(loParamMap);
			LOG_OBJECT.Error("Error occurred while rendering cancel Award Overlay", aoException);
		}
		// handling exception other than Application Exception.
		catch (Exception aoException)
		{
			LOG_OBJECT.Error("Error occurred while rendering cancel Award Overlay", aoException);
			setExceptionMessageInResponse(aoResourceRequest);
		}
		return HHSConstants.CANCEL_AWARD;
	}

	/**
	 * The method will handle close procurement
	 * <ul>
	 * <li>1.Set the required data in Channel</li>
	 * <li>2.Call transaction cancelAward</li>
	 * <li>3.perform server side validation on credential and check the
	 * Authorization</li>
	 * <li>4.If Authorization fails show error message on jsp</li>
	 * <li>5.else call cancel Award transaction 'cancelAward'</li>
	 * <li>6.Update award status to "Canceled" <b>Service</b> :updateAwardStatus
	 * <li/>
	 * <li>7. Update related proposals for that provider for that procurement to
	 * "Not Selected" <b>Service</b>updateRelatedProposal</li>
	 * <li>8.Update the Award Review Status to
	 * "Update in Progress"<b>service</b>updateAwardReviewStatus</li>
	 * <li>9.//TO-DO Terminate any pending workflows for the cancelled award
	 * (including R3 workflows)</li>
	 * <li>10.Close popup and return to Award and Contract Screen</li>
	 * </ul>
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * @param aoResult BindingResult
	 * @param aoRequest ActionRequest
	 * @param aoResponse ActionResponse
	 * @param aoModel Model
	 * @param aoAuthenticationBean AuthenticationBean
	 */
	@SuppressWarnings("rawtypes")
	@ActionMapping(params = "submit_action=cancelAward")
	protected void cancelAward(@ModelAttribute("AuthenticationBean") AuthenticationBean aoAuthenticationBean,
			BindingResult aoResult, ActionRequest aoRequest, ActionResponse aoResponse, Model aoModel)
	{
		// Work flow integration for terminating all the related work flows
		P8UserSession loUserSession = null;
		HashMap<String, String> loLaunchWorkflowMap = new HashMap<String, String>();
		HashMap<String, String> loHmReqExceProp = new HashMap<String, String>();
		Map<String, String> loStatusInfoMap = new HashMap<String, String>();
		HashMap<String, String> loParamMap = new HashMap<String, String>();
		// made changes for release 3.10.0 enhancement 5686
		String lsReuseEpin = aoRequest.getParameter(HHSConstants.REUSE_EPIN);
		try
		{
			LOG_OBJECT.Debug("Entered into cancelEvaluationTasksAction::" + loHmReqExceProp.toString());
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
				cancelAwardFinal(aoRequest, loUserSession, loLaunchWorkflowMap, loStatusInfoMap, loParamMap,
						lsReuseEpin);
			}
		}
		// AppicationException thrown from the Transaction framework are handled
		// here
		catch (ApplicationException aoEx)
		{
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loHmReqExceProp, aoEx);
			aoEx.setContextData(loStatusInfoMap);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.ERROR_PAGE_CLOSE);
		}
		// Handling Exceptions other than Application Exception
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while Cancel Evaluation Tasks:", aoEx);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.ERROR_PAGE_CLOSE);
		}
	}

	/**
	 * The method will handle close procurement
	 * <ul>
	 * <li>1.Set the required data in Channel</li>
	 * <li>2.Call transaction cancelAward</li>
	 * <li>3.perform server side validation on credential and check the
	 * Authorization</li>
	 * <li>4.If Authorization fails show error message on jsp</li>
	 * <li>5.else call cancel Award transaction 'cancelAward'</li>
	 * <li>6.Update award status to "Canceled" <b>Service</b> :updateAwardStatus
	 * <li/>
	 * <li>7. Update related proposals for that provider for that procurement to
	 * "Not Selected" <b>Service</b>updateRelatedProposal</li>
	 * <li>8.Update the Award Review Status to
	 * "Update in Progress"<b>service</b>updateAwardReviewStatus</li>
	 * <li>9.//TO-DO Terminate any pending workflows for the cancelled award
	 * (including R3 workflows)</li>
	 * <li>10.Close popup and return to Award and Contract Screen</li>
	 * </ul>
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest
	 * @param loUserSession user session
	 * @param loLaunchWorkflowMap input map for workflow launch
	 * @param loStatusInfoMap input param map
	 * @param loParamMap input param map
	 * @throws ApplicationException If an exception occurs
	 */
	private void cancelAwardFinal(ActionRequest aoRequest, P8UserSession loUserSession,
			HashMap<String, String> loLaunchWorkflowMap, Map<String, String> loStatusInfoMap,
			HashMap<String, String> loParamMap, String asReuseEpin) throws ApplicationException
	{
		// populate the map with the required parameters
		loStatusInfoMap.put(HHSConstants.COMPETITION_POOL_STATUS,
				aoRequest.getParameter(HHSConstants.COMPETITION_POOL_STATUS));
		loStatusInfoMap.put(HHSConstants.CONTRACT_ID, aoRequest.getParameter(HHSConstants.CONTRACT_ID));
		loStatusInfoMap.put(HHSConstants.ORGANIZATION_ID, aoRequest.getParameter(HHSConstants.ORGANIZATION_ID));
		loStatusInfoMap.put(HHSConstants.PROCUREMENT_ID, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
		loStatusInfoMap.put(HHSConstants.AWARD_ID, aoRequest.getParameter(HHSConstants.AWARD_ID));
		loStatusInfoMap.put(
				HHSConstants.USER_ID,
				(String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
						PortletSession.APPLICATION_SCOPE));
		loStatusInfoMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID,
				aoRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID));
		loParamMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, aoRequest.getParameter(HHSConstants.CONTRACT_ID));
		loParamMap.put(
				HHSConstants.USER_ID,
				(String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
						PortletSession.APPLICATION_SCOPE));
		Channel loChannel = new Channel();
		// set the map in the channel object
		loChannel.setData(HHSConstants.INPUT_PARAM_MAP, loParamMap);
		loChannel.setData(HHSConstants.STATUS_INFO_MAP, loStatusInfoMap);
		// Work flow integration for terminating all the related work
		// flows
		loLaunchWorkflowMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID,
				aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
		loLaunchWorkflowMap.put(P8Constants.PE_WORKFLOW_CONTRACT_ID, aoRequest.getParameter(HHSConstants.CONTRACT_ID));
		loLaunchWorkflowMap.put(P8Constants.ACTION_KEY_FOR_UTILITY_WORKFLOW,
				P8Constants.CANCEL_AWARD_ACTION_FOR_UTILITY_WORKFLOW);
		loLaunchWorkflowMap.put(P8Constants.EVALUATION_POOL_MAPPING_ID,
				aoRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID));
		loChannel.setData(HHSConstants.AO_HMWF_REQUIRED_PROPS, loLaunchWorkflowMap);
		loChannel.setData(HHSConstants.WORK_FLOW_NAME, P8Constants.PE_EVALUATION_UTILITY_WORKFLOW_NAME);
		loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
		// made changes for release 3.10.0 enhancement 5686
		loChannel.setData(HHSConstants.REUSE_EPIN, asReuseEpin);
		String lsEventName = HHSConstants.CHANGE_STATUS;
		String lsEventType = HHSConstants.CANCELL_AWARD;
		String lsData = HHSConstants.AWARD_STATUS_CHANGED_UPDATE_IN_PROGRESS;
		String lsEntityType = HHSConstants.CANCELL_AWARD;
		String lsEntityId = aoRequest.getParameter(HHSConstants.PROCUREMENT_ID);
		String lsUserID = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsTableIdentifier = HHSConstants.AGENCY_AUDIT;
		HhsAuditBean loAuditBean = CommonUtil.addAuditDataToChannel(lsEventName, lsEventType, lsData, lsEntityType,
				lsEntityId, lsUserID, lsTableIdentifier);
		loChannel.setData(HHSConstants.AUDIT_BEAN, loAuditBean);
		loChannel.setData(HHSConstants.PROCUREMENT_ID, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
		// the transaction framework execute the cancelAward transaction
		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.CANCEL_AWARD);
	}

	/**
	 * OnClick of View APT Progress dropdown under Action section, open popup
	 * S250 – View APT Progress
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
	@ResourceMapping("viewAptInformation")
	protected ModelAndView viewAptProgress(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		Channel loChannel = new Channel();
		AwardBean loViewAptProgressBean = null;
		try
		{
			Map<String, String> loViewProgressMap = new HashMap<String, String>();
			String lsProcurementAwardPin = aoResourceRequest.getParameter(HHSConstants.PROPERTY_PE_AWARD_EPIN);
			if (lsProcurementAwardPin != null)
			{
				loViewProgressMap.put(HHSConstants.PROPERTY_PE_AWARD_EPIN, lsProcurementAwardPin);
				loChannel.setData(HHSConstants.LO_VIEW_PROGRESS_MAP, loViewProgressMap);
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.VIEW_APT_DETAILS);
				loViewAptProgressBean = (AwardBean) loChannel.getData(HHSConstants.LO_VIEW_APT_BEAN);
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException loException)
		{
			// populating context data map for exceptional handling
			Map<String, String> loContextParamMap = new HashMap<String, String>();
			loContextParamMap.put(HHSConstants.PROPERTY_PE_AWARD_EPIN,
					aoResourceRequest.getParameter(HHSConstants.PROPERTY_PE_AWARD_EPIN));
			setExceptionMessageInResponse(aoResourceRequest);
			loException.setContextData(loContextParamMap);
			LOG_OBJECT.Error("Exception Occured while displaying View APT Progress screen", loException);
		}
		// handling exception other than Application Exception.
		catch (Exception loException)
		{
			LOG_OBJECT.Error("Exception Occured while displaying View APT Progress screen", loException);
			setExceptionMessageInResponse(aoResourceRequest);
		}
		return new ModelAndView(HHSConstants.VIEW_APT_PROGRESS, HHSConstants.AWARD_BEAN, loViewAptProgressBean);
	}

	/**
	 * This method is used to render the error page when credentials are wrong
	 * while closing a procurement.
	 * 
	 * <ul>
	 * <li>1. Get user name from closeProcurement jsp and set in request.</li>
	 * </ul>
	 * 
	 * @param aoRequest - RenderRequest
	 * @param aoResponse - RenderResponse
	 * @return - ModelAndView
	 */
	@RenderMapping(params = "render_action=errorPageClose")
	protected ModelAndView renderCloseProcurement(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		ModelAndView loModelAndView = new ModelAndView(HHSConstants.CLOSE_PROCUREMENT);
		aoRequest.setAttribute(HHSConstants.CLOSE_PROC_ID, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
		aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
				aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE));
		aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
				aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE_TYPE));
		return loModelAndView;
	}

	/**
	 * This method will handle paginate contracts action from awards and
	 * contracts screen and will render to awardsAndContracts Render Method
	 * 
	 * <ul>
	 * <li>1.Call setNavigationParamsInRender for Setting Navigation Render
	 * Parameter</li>
	 * <li>2.Setting parameter required for rendering of Awards and Contracts
	 * Screen Rendering</li>
	 * </ul>
	 * 
	 * @param aoEvaluationBean EvaluationBean
	 * @param aoRequest ActionRequest
	 * @param aoResponse ActionResponse
	 */
	@ActionMapping(params = "submit_action=pagingContracts")
	protected void actionPaginateContracts(@ModelAttribute("EvaluationBean") EvaluationBean aoEvaluationBean,
			ActionRequest aoRequest, ActionResponse aoResponse)
	{
		setNavigationParamsInRender(aoRequest, aoResponse);
		aoResponse.setRenderParameter(HHSConstants.NEXT_PAGE_PARAM, aoRequest.getParameter(HHSConstants.NEXT_PAGE));
		aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.AWARDS_AND_CONTRACTS);
		aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.AWARD_CONTRACT);
		aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
	}

	/**
	 * This method will get the details of the Award e pins Associated with the
	 * procurement and redirect user to <b>S249</b>
	 * <ul>
	 * <li>Get the procurement Id from the Request Object</li>
	 * <li>Set the Procurement Id into the channel object</li>
	 * <li>Execute Transaction <b>fetchAwardEPinDetails</b> with all required
	 * parameters</li>
	 * <li>Fetch the Award E pin details bean list from channel object</li>
	 * <li>Set the list of Award E-pin Bean into the request and redirect user
	 * to screen no <b>S249</b></li>
	 * </ul>
	 * <ul>
	 * <li>Method updated in R4</li>
	 * </ul>
	 * 
	 * @param aoResourceRequest ResourceRequest object
	 * @param aoResourceResponse ResourceResponse object
	 * @return ModelAndView assignAwardEpin jsp
	 */
	@SuppressWarnings("unchecked")
	@ResourceMapping("assignAwardEpin")
	protected ModelAndView actionGetAwardEpinList(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		Channel loChannel = new Channel();
		ModelAndView loModelAndView = null;
		HashMap<String, Object> loHmReqExceProp = new HashMap<String, Object>();
		try
		{
			String lsProcurementId = aoResourceRequest.getParameter(HHSConstants.PROCUREMENT_ID);
			String lsContractId = aoResourceRequest.getParameter(HHSConstants.CONTRACT_ID);
			String lsOpenEndedRFP = aoResourceRequest.getParameter(HHSConstants.OPEN_ENDED_PROC);
			String lsFinancial = aoResourceRequest.getParameter(HHSConstants.IS_FINANCIAL);

			aoResourceRequest.setAttribute(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			aoResourceRequest.setAttribute(HHSConstants.CONTRACT_ID, lsContractId);
			aoResourceRequest.setAttribute(HHSConstants.OPEN_ENDED_PROC, lsOpenEndedRFP);
			aoResourceRequest.setAttribute(HHSConstants.IS_FINANCIAL, lsFinancial);
			aoResourceRequest.setAttribute(HHSConstants.EVALUATION_POOL_MAPPING_ID,
					aoResourceRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID));
			aoResourceRequest.setAttribute(HHSConstants.EVALUATION_GROUP_ID,
					aoResourceRequest.getParameter(HHSConstants.EVALUATION_GROUP_ID));

			loHmReqExceProp.put(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
			loChannel.setData(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
			loChannel.setData(HHSConstants.CONTRACT_ID_KEY, lsContractId);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_AWARD_EPIN_DETAILS);
			aoResourceRequest.setAttribute(HHSConstants.AWARD_EPIN_DETAILS,
					(List<AwardBean>) loChannel.getData(HHSConstants.LO_AWARD_EPIN_DETAILS));
			aoResourceRequest.setAttribute(HHSConstants.AMOUNT_PROVIDER_DETAILS,
					(AwardBean) loChannel.getData(HHSConstants.LO_AMOUNT_PROVIDER_DETAILS));
			aoResourceRequest.setAttribute(HHSConstants.CONTRACT_TYPE_ID,
					aoResourceRequest.getParameter(HHSConstants.CONTRACT_TYPE_ID));

			loModelAndView = new ModelAndView(HHSConstants.ASSIGN_AWARD_EPIN);
		}
		// Any Exception from Transaction framework will be thrown as
		// Application Exception
		// which will be handles over here.
		catch (ApplicationException aoExp)
		{
			aoExp.setContextData(loHmReqExceProp);
			setExceptionMessageInResponse(aoResourceRequest);
			LOG_OBJECT.Error("Exception Occured while fetching details of the Award e pins:", aoExp);
		}
		// handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception Occured while fetching details of the Award e pins:", aoExp);
			setExceptionMessageInResponse(aoResourceRequest);
		}
		return loModelAndView;
	}

	/**
	 * This method assigns details of the Award e pins Associated with the
	 * procurement <b>S249</b>
	 * <ul>
	 * <li>Execute Transaction <b>assignAPTAwardEpin</b> with all required
	 * parameters</li>
	 * <li>Fetch the Award E pin details bean list from channel object</li>
	 * </ul>
	 * R6: Updated the method as part of release 6
	 * 
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse Object
	 * 
	 */
	@ActionMapping(params = "submit_action=assignAPTAwardEpin")
	public void assignAPTAwardEpin(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		Channel loChannel = new Channel();
		HashMap<String, Object> loHmReqExceProp = new HashMap<String, Object>();
		//R6: Added a string to modify error message when exception is thrown in validation
		String lsMsg = HHSConstants.EMPTY_STRING;
		try
		{
			String lsProcurementId = aoRequest.getParameter(HHSConstants.PROCUREMENT_ID);
			String lsContractId = aoRequest.getParameter(HHSConstants.CONTRACT_ID);
			//R6: Getting RefAptEpinId for validation of EPIN uniqueness
			String refAptEpinId = aoRequest.getParameter(HHSConstants.REF_APT_EPIN_ID);
			String lsAwardEpin = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.AWARD_EPIN_ID);
			P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			String lsUserID = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			loHmReqExceProp.put(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
			loHmReqExceProp.put(HHSConstants.PROCUMENET_ID, lsProcurementId);
			loHmReqExceProp.put(HHSConstants.CLC_AWARD_EPIN, lsAwardEpin);
			Map<String, String> loAwardEpinMap = new HashMap<String, String>();
			loAwardEpinMap.put(HHSConstants.LS_PRO_ID, lsProcurementId);
			loAwardEpinMap.put(HHSConstants.LS_AWARD_EPIN, lsAwardEpin);
			loAwardEpinMap.put(HHSConstants.CONTRACT_ID_KEY, lsContractId);
			loAwardEpinMap.put(HHSConstants.CONTRACT_TYPE_ID, aoRequest.getParameter(HHSConstants.CONTRACT_TYPE_ID));
			loAwardEpinMap.put(HHSConstants.TT_USERID, lsUserID);
			loAwardEpinMap.put(HHSConstants.STATUS_COLUMN,
					PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.PDF_NOT_STARTED));
			loAwardEpinMap
					.put(HHSConstants.CONTRACT_STATUS, PropertyLoader.getProperty(
							ApplicationConstants.PROPERTIES_STATUS_CONSTANT,
							HHSConstants.STATUS_CONTRACT_PENDING_REGISTARTION));
			loAwardEpinMap.put(HHSConstants.REF_APT_EPIN_ID, refAptEpinId);
			loChannel.setData(HHSConstants.LO_AWARD_EPIN_MAP, loAwardEpinMap);
			loChannel.setData(HHSConstants.CONTRACT_ID_WORKFLOW, lsContractId);
			loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
			loChannel.setData(HHSConstants.CLC_AWARD_EPIN, lsAwardEpin);
			loChannel.setData(HHSConstants.CONTRACT_START_DATE,
					aoRequest.getParameter(HHSConstants.CONTRACT_START_DATE));
			loChannel.setData(HHSConstants.CONTRACT_END_DATE, aoRequest.getParameter(HHSConstants.CONTRACT_END_DATE));
			loChannel.setData(HHSConstants.EVALUATION_POOL_MAPPING_ID,
					aoRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID));
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.ASSIGN_APT_AWARD_EPIN);
			aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.AWARDS_AND_CONTRACTS);
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.AWARD_CONTRACT);
			setNavigationParamsInRender(aoRequest, aoResponse);
		}
		// Any Exception from Transaction framework will be thrown as
		// Application
		// Exception
		// which will be handles over here.
		catch (ApplicationException aoExp)
		{
			String lsErrorMsg = HHSConstants.ERROR_WHILE_PROCESSING_REQUEST;
			aoExp.setContextData(loHmReqExceProp);
			setExceptionMessageFromAction(aoResponse, lsErrorMsg, ApplicationConstants.MESSAGE_FAIL_TYPE,
					loHmReqExceProp, aoExp);
			LOG_OBJECT.Error("Exception Occured while assigning Award E pin:", aoExp);
		}
		// handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			String lsErrorMsg = HHSConstants.ERROR_WHILE_PROCESSING_REQUEST;
			LOG_OBJECT.Error("Exception Occured while assigning Award E pin:", aoExp);
			setExceptionMessageFromAction(aoResponse, lsErrorMsg, ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
	}

	/***
	 * This method will handle sort action from Awards and Contracts Summary
	 * screen by clicking any of the column headers
	 * 
	 * <ul>
	 * <li>1.Setting Navigation parameters in Response by calling method
	 * setNavigationParamsInRender from BaseControllerSM</li>
	 * <li>2.Get sortType and columnName values by calling parseQueryString()
	 * method from class PortalUtil.</li>
	 * <li>3.Get sorting details by calling method getSortDetailsFromXML() from
	 * class BaseController.</li>
	 * <li>4.Setting Award Contract Summary Bean and other required parameter in
	 * Response for successful rendering of Awards and Contracts Summary</li>
	 * <ul>
	 * <ul>
	 * <li>New Method in R4</li>
	 * </ul>
	 * 
	 * @param aoAwardsContractSummaryBean AwardsContractSummaryBean object
	 * @param aoRequest ActionRequest
	 * @param aoResponse ActionResponse
	 */
	@ActionMapping(params = "submit_action=sortAwardContractsSummary")
	protected void actionSortAwardContractsSummary(
			@ModelAttribute("AwardsContractSummaryBean") AwardsContractSummaryBean aoAwardsContractSummaryBean,
			ActionRequest aoRequest, ActionResponse aoResponse)
	{
		Map<String, String> loHmReqExceProp = new HashMap<String, String>();
		loHmReqExceProp.put(HHSConstants.PROCUREMENT_ID, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
		try
		{
			LOG_OBJECT.Debug("Entered into Award Contracts summary Sort Action::");
			setNavigationParamsInRender(aoRequest, aoResponse);
			String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			String lsSortType = PortalUtil.parseQueryString(aoRequest, HHSConstants.SORT_TYPE);
			String lsColumnName = PortalUtil.parseQueryString(aoRequest, HHSConstants.COLUMN_NAME);
			getSortDetailsFromXML(lsColumnName, lsUserOrgType,
					PortalUtil.parseQueryString(aoRequest, HHSConstants.SORT_GRID_NAME), aoAwardsContractSummaryBean,
					lsSortType);
			// Setting param for Rendering getEvaluationStatus Render Method
			aoRequest.getPortletSession().setAttribute(HHSConstants.AWARD_CONTRACT_SUMMARY_BEAN,
					aoAwardsContractSummaryBean, PortletSession.PORTLET_SCOPE);
			aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID,
					aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.AWARDS_AND_CONTRACTS);
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.AWARD_CONTRACT);
		}

		// handling Application Exception while sorting evaluation result.
		catch (ApplicationException aoAppExp)
		{
			String lsErrorMsg = HHSConstants.ERROR_WHILE_PROCESSING_REQUEST;
			setExceptionMessageFromAction(aoResponse, lsErrorMsg, ApplicationConstants.MESSAGE_FAIL_TYPE,
					loHmReqExceProp, aoAppExp);
		}
		// Handling Exception other than ApplicationException
		catch (Exception aoExp)
		{
			String lsErrorMsg = HHSConstants.ERROR_WHILE_PROCESSING_REQUEST;
			LOG_OBJECT.Error("Exception Occured while sorting Award Contracts summary: ", aoExp);
			setExceptionMessageFromAction(aoResponse, lsErrorMsg, ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
	}

	/**
	 * This method will handle pagination action from Awards and Contracts
	 * Summary screen.
	 * 
	 * <ul>
	 * <li>1.Set navigation parameters in Response by calling method
	 * setNavigationParamsInRender from BaseControllerSM</li>
	 * <li>2.This method will check for next action parameter and call methods
	 * accordingly.</li>
	 * </ul>
	 * <ul>
	 * <li>New Method in R4</li>
	 * </ul>
	 * 
	 * @param aoRequest an Action Request object
	 * @param aoResponse an Action Response object
	 */
	@ActionMapping(params = "submit_action=paginateAwardContractsSummary")
	protected void actionPaginateAwardContractsSummary(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		setNavigationParamsInRender(aoRequest, aoResponse);
		aoResponse.setRenderParameter(HHSConstants.NEXT_PAGE_PARAM, aoRequest.getParameter(HHSConstants.NEXT_PAGE));
		aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.AWARDS_AND_CONTRACTS);
		aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.AWARD_CONTRACT);
	}

	/**
	 * This method will handle the sorting of Evaluation Group Awards Summary
	 * whenever any table header is clicked
	 * 
	 * <ul>
	 * <li>1.Set navigation parameters in Render by calling method
	 * setNavigationParamsInRender from BaseControllerSM</li>
	 * <li>2.Get sortType and columnName values by calling parseQueryString()
	 * method from class PortalUtil.</li>
	 * <li>3.Get sorting details by calling method getSortDetailsFromXML() from
	 * class BaseController.</li>
	 * <li>4.Setting Evaluation Group Award Bean and other required parameter in
	 * Response for successful rendering of Evaluation Group Awards Summary</li>
	 * </ul>
	 * <ul>
	 * <li>New Method in R4</li>
	 * </ul>
	 * @param aoEvaluationGroupAwardBean EvaluationGroupAwardBean object
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse object
	 */
	@ActionMapping(params = "submit_action=sortEvaluationGroupSummary")
	protected void actionEvaluationGroupSummary(
			@ModelAttribute("EvaluationSummaryBean") EvaluationGroupAwardBean aoEvaluationGroupAwardBean,
			ActionRequest aoRequest, ActionResponse aoResponse)
	{
		Map<String, String> loHmReqExceProp = new HashMap<String, String>();
		loHmReqExceProp.put(HHSConstants.PROCUREMENT_ID, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
		try
		{
			LOG_OBJECT.Debug("Entered into Evaluation Status Sort Action::");
			setNavigationParamsInRender(aoRequest, aoResponse);
			String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			String lsSortType = PortalUtil.parseQueryString(aoRequest, HHSConstants.SORT_TYPE);
			String lsColumnName = PortalUtil.parseQueryString(aoRequest, HHSConstants.COLUMN_NAME);
			getSortDetailsFromXML(lsColumnName, lsUserOrgType,
					PortalUtil.parseQueryString(aoRequest, HHSConstants.SORT_GRID_NAME), aoEvaluationGroupAwardBean,
					lsSortType);
			// Setting param for Rendering getEvaluationStatus Render Method
			aoRequest.getPortletSession().setAttribute(HHSConstants.SESSION_EVALUATION_GROUP_AWARD_BEAN,
					aoEvaluationGroupAwardBean, PortletSession.PORTLET_SCOPE);
			aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID,
					aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.AWARDS_AND_CONTRACTS);
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.AWARD_CONTRACT);
		}

		// handling Application Exception while sorting evaluation result.
		catch (ApplicationException aoAppExp)
		{
			String lsErrorMsg = HHSConstants.ERROR_WHILE_PROCESSING_REQUEST;
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

	// R5 change starts
	/**
	 * This method will handle the sorting of Evaluation Group Awards Summary
	 * whenever any table header is clicked
	 * 
	 * <ul>
	 * <li>1.Put evaluationPoolMappingId, procurementId, awardStatusId, IsNegotiationRequired in loHmReqExceProp</li>
	 * <li>2.set loHmWFReqProps and aoFilenetSession in channel.</li>
	 * <li>3.add the required parameters for workflow integration in a map</li>
	 * <li>4.transaction called is 'cancelAllAwards'</li>
	 * </ul>
	 * @param aoModel Model object
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse object
	 */
	@ActionMapping(params = "submit_action=cancelAllAward")
	protected void cancelAllAward(ActionRequest aoRequest, ActionResponse aoResponse, Model aoModel)
	{
		// Work flow integration for terminating all the related work flows
		P8UserSession loUserSession = null;
		HashMap<String, String> loHmReqExceProp = new HashMap<String, String>();
		HashMap<String, String> loWorkflowMap = new HashMap<String, String>();
		String lsUserName = aoRequest.getParameter(HHSConstants.USER);
		String lsPassword = aoRequest.getParameter(HHSR5Constants.PASSWORD);
		String lsEvalPoolMappingId = aoRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID);
		try
		{
			LOG_OBJECT.Debug("Entered into cancelAllAward::" + loHmReqExceProp.toString());
			Map loAuthenticateMap = validateUser(lsUserName, lsPassword, aoRequest);
			Boolean loAuthStatus = (Boolean) loAuthenticateMap.get(HHSConstants.IS_VALID_USER);
			// Work flow integration for terminating all the related work flows
			loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			// if authentication passes
			if (loAuthStatus)
			{
				String lsProcurementId = aoRequest.getParameter(HHSConstants.PROCUREMENT_ID);
				loHmReqExceProp.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolMappingId);
				loHmReqExceProp.put(HHSConstants.PROCUREMENT_ID, lsProcurementId);
				loHmReqExceProp.put(HHSConstants.AWARD_STATUS_ID, HHSConstants.EMPTY_STRING);
				loHmReqExceProp.put(HHSR5Constants.IS_NEGOTIATION_REQUIRED, HHSConstants.FALSE);
				Channel loChannel = new Channel();
				loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
				loChannel.setData(HHSConstants.LO_HM_WF_REQ_PROPS, loHmReqExceProp);
				PortletSession loSession = aoRequest.getPortletSession();
				String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
						PortletSession.APPLICATION_SCOPE);
				Map<String, Object> loInputParamMap = new HashMap<String, Object>();
				loInputParamMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolMappingId);
				loInputParamMap.put(HHSConstants.COMPETITION_POOL_STATUS, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_COMPETITION_POOL_EVALUATIONS_COMPLETE));
				loInputParamMap.put(HHSConstants.USER_ID, lsUserId);
				loInputParamMap.put(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
				loInputParamMap.put(HHSConstants.PROCUREMENT_ID, lsProcurementId);
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
				loWorkflowMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, lsProcurementId);
				loWorkflowMap.put(P8Constants.LAUNCHED_BY, lsUserId);
				loWorkflowMap.put(P8Constants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolMappingId);
				loChannel.setData(HHSConstants.AO_HMWF_REQUIRED_PROPS, loWorkflowMap);
				// add parameter map to channel
				loChannel.setData(HHSConstants.INPUT_PARAM_MAP, loInputParamMap);
				HHSTransactionManager.executeTransaction(loChannel, "cancelAllAwards",
						HHSR5Constants.TRANSACTION_ELEMENT_R5);
			}// if Authentication Fails ,Set the Error Message
			else
			{
				aoResponse
						.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
								HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.MESSAGE_M38));
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.ERROR_PAGE_CLOSE);
			}
		}
		// AppicationException thrown from the Transaction framework are handled
		// here
		catch (ApplicationException aoEx)
		{
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loHmReqExceProp, aoEx);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.ERROR_PAGE_CLOSE);
		}
		// Handling Exceptions other than Application Exception
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while Cancel All Awards:", aoEx);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.ERROR_PAGE_CLOSE);
		}
	}
	
	
	/**
	 * R6: This resource handler validates if an award epin is valid to be assigned
	 * by checking in the system that this epin is unique for the assignee agency
	 * Validate Award epin resource request. 
	 * @param aoRequest aoRequest
	 * @param aoResponse aoResponse
	 */
	@ResourceMapping("validateAwardEpinUrl")
	public void validateAwardEpinResourceRequest(ResourceRequest aoRequest, ResourceResponse aoResponse)
	{
		PrintWriter loOut = null;
		final StringBuffer loOutputBuffer = new StringBuffer();
		try
		{
			LOG_OBJECT.Debug("Entered into validateAwardEpinResourceRequest::");
			String procurementAgencyId = aoRequest.getParameter(HHSConstants.PROCUREMENT_AGENCY_ID);
			String lsAwardEpin = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.AWARD_EPIN_ID);
			
			EPinDetailBean loEPinDetailBean = new EPinDetailBean();
			loEPinDetailBean.setEpinId(lsAwardEpin);
			loEPinDetailBean.setAgencyId(procurementAgencyId);
			boolean lbIsValidEpin = ContractListUtils.validateEpinUnique(loEPinDetailBean);
			
			loOut = aoResponse.getWriter();
			String lsMsg = HHSConstants.EMPTY_STRING;
			if(!lbIsValidEpin)
			{
				lsMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSR5Constants.EPIN_ALREADY_USE);
			}
			
			aoResponse.setContentType(HHSConstants.APPLICATION_JSON);
			
			loOutputBuffer.append(HHSConstants.EPIN_VALIDATION_RESPONSE_BODY_START);
			loOutputBuffer.append(lbIsValidEpin);
			loOutputBuffer.append(HHSConstants.DQUOTES_COMMA);
			loOutputBuffer.append(HHSConstants.EPIN_VALIDATION_RESPONSE_BODY_ERROR);
			loOutputBuffer.append(lsMsg);
			loOutputBuffer.append(HHSConstants.DOUBLE_QUOTES);
			loOutputBuffer.append(HHSConstants.CRLI_BRACKT_END);
			loOut.print(loOutputBuffer.toString());
			BaseControllerUtil.closingPrintWriter(loOut);
		}
		catch (ApplicationException aoException)
		{
			setExceptionMessageInResponse(aoRequest);
			LOG_OBJECT.Error("Error occurred while validating epin in Award Overlay", aoException);
		}
		catch (IOException aoIOException)
		{
			setExceptionMessageInResponse(aoRequest);
			LOG_OBJECT.Error("Error occurred while validating epin in Award Overlay", aoIOException);
		}
		// Handling Exceptions other than Application Exception
		catch (Exception aoEx)
		{
			setExceptionMessageInResponse(aoRequest);
			LOG_OBJECT.Error("Error occurred while validating epin in Award Overlay", aoEx);
		}
		
	}
}