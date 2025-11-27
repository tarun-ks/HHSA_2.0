package com.nyc.hhs.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;
import org.springframework.web.portlet.mvc.ResourceAwareController;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSP8Constants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.controllers.util.ContractBudgetControllerUtils;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.BudgetDetails;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.ContractBudgetBean;
import com.nyc.hhs.model.ContractList;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.ReturnPaymentNotification;
import com.nyc.hhs.model.ReturnedPayment;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.rule.Rule;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.ApplicationSession;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.HHSPortalUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PortalUtil;
import com.nyc.hhs.util.PortletSessionHandler;
import com.nyc.hhs.util.PropertyLoader;

/**
 * <p>
 * This controller class will serve as a controller for all contract budget
 * related screens. Budget Summary and all budget related details on various
 * screens will be shown using this controller only.
 * 
 * All the actions on contract budget screens will be handled by this controller
 * only.
 * </p>
 */
@Controller(value = "contractBudget")
@RequestMapping("view")
public class ContractBudgetController extends BaseController implements ResourceAwareController
{

	/**
	 * Log object to record all logs
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(ContractBudgetController.class);

	/**
	 * This method modified as a part of release 2.7.0 for enhancement request
	 * 5618
	 * <ul>
	 * <li>"Remove Link to Complete Budget List" should not be displayed if
	 * navigate from View Award Document page</li>
	 * <ul>
	 * 
	 * This method will be executed when budget summary screen will be loaded
	 * for first time and it will call service to fetch all details for budget
	 * summary screen. <br>
	 * Method updated for R4.
	 * <ul>
	 * <li>Transaction manager will be called to call the fetchBudgetSummary
	 * method of ContractBudgetService class which will fetch all details for
	 * budget summary screen.</li>
	 * </ul>
	 * 
	 * @param aoRequest an Action Request object
	 * @param aoResponse an Action Response object
	 * @return ModelAndView containing details of the page to be displayed to
	 *         the end user
	 * @throws ApplicationException an application exception
	 */
	@RenderMapping
	protected ModelAndView mainRenderMethod(RenderRequest aoRequest, RenderResponse aoResponse)
			throws ApplicationException
	{
		ModelAndView loModelAndView = null;
		Channel loChannelObj = new Channel();
		String lsJspPath = HHSConstants.JSP_CONTRACTBUDGET_CONTRACT_BUDGET_LANDING;
		String lsContractId = PortalUtil.parseQueryString(aoRequest, HHSConstants.CONTRACT_ID_WORKFLOW);
		String lsBudgetId = PortalUtil.parseQueryString(aoRequest, HHSConstants.BUDGET_ID_WORKFLOW);
		// Added for R6: return payment review task
		String lsReturnPaymentDetailId = PortalUtil.parseQueryString(aoRequest, HHSConstants.RETURN_PAYMENT_DETAIL_ID);
		// Added for R6: return payment review task end
		String lsFiscalYearId = PortalUtil.parseQueryString(aoRequest, HHSConstants.CLC_FISCAL_YEAR_ID);
		String lsPrintRender = (String) aoRequest.getParameter(HHSConstants.RENDER_ACTION);
		PortletSession loSession = aoRequest.getPortletSession();
		P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setP8UserSession(loUserSession);
		String lsWorkflowId = PortalUtil.parseQueryString(aoRequest, HHSConstants.WORKFLOW_ID);
		try
		{
			if (null != lsWorkflowId)
			{
				loTaskDetailsBean = fetchTaskDetailsFromFilenet(aoRequest, lsWorkflowId);
				// loTaskDetailsBean object will contains all required
				// information for Task like Contract Id,Procurement Id etc.
				lsContractId = loTaskDetailsBean.getContractId();
				lsBudgetId = loTaskDetailsBean.getBudgetId();
				// Added for R6: return payment review task
				lsReturnPaymentDetailId = loTaskDetailsBean.getReturnPaymentDetailId();
				loChannelObj.setData(HHSConstants.RETURN_PAYMENT_DETAIL_ID,lsReturnPaymentDetailId);
				// Added for R6: return payment review task
			}
			// Updated for R6: return payment review task
			lsJspPath = getJspPath(aoRequest, loChannelObj, lsContractId, lsBudgetId, lsPrintRender, loTaskDetailsBean,
					lsFiscalYearId, lsReturnPaymentDetailId);
			// Updated for R6: return payment review task
			String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			setCBGridBean(aoRequest, loChannelObj, lsFiscalYearId, lsUserOrgType, lsUserId);
			// setting parameter for making screen readonly
			loChannelObj.setData(HHSConstants.ORG_TYPE, lsUserOrgType);
			Boolean loReadOnlyCondition = Boolean.valueOf((String) Rule.evaluateRule(
					HHSConstants.READ_ONLY_PAGE_ATTRIBUTE_RULE, loChannelObj));
			// Start Release 5 user notification
			loReadOnlyCondition = getProcurementReadOnly(aoRequest, lsUserOrgType, loReadOnlyCondition);
			// End Release 5 user notification
			//Added for R6- Returned Payment Task
			Boolean lbReadOnlyForReturnedPayment = HHSR5Constants.BOOLEAN_FALSE;
			if (null != loTaskDetailsBean.getTaskName() && loTaskDetailsBean.getTaskName().equalsIgnoreCase(HHSConstants.TASK_RETURN_PAYMENT_REVIEW)
					&& (!loTaskDetailsBean.getLevel().equalsIgnoreCase(HHSR5Constants.ONE) || !loTaskDetailsBean.getIsTaskAssigned()))
			{
				lbReadOnlyForReturnedPayment = HHSR5Constants.BOOLEAN_TRUE;
			}
			aoRequest.getPortletSession().setAttribute(HHSR5Constants.RETURNED_PAYMENT_READ_ONLY,
					lbReadOnlyForReturnedPayment.toString(), PortletSession.APPLICATION_SCOPE);
			//Added for R6- Returned Payment Task
			aoRequest.getPortletSession().setAttribute(HHSConstants.CONTRACT_BUDGET_READ_ONLY,
					loReadOnlyCondition.toString(), PortletSession.APPLICATION_SCOPE);
			aoRequest.setAttribute(HHSConstants.CHANNEL_ACCESS, loChannelObj);

			// This method will fetch the document list for this screen
			generateDocumentSection(aoRequest);
			// Get errors messages after Submit CB confirmation transactions
			String lsErrorMsg = (String) ApplicationSession.getAttribute(aoRequest, HHSConstants.ERROR_MESSAGE);
			aoRequest.setAttribute(HHSConstants.ERROR_MESSAGE, lsErrorMsg);
			if ((lsErrorMsg == null || HHSConstants.EMPTY_STRING.equalsIgnoreCase(lsErrorMsg))
					&& aoRequest.getParameter(HHSConstants.SUBMIT_OVERLAY_SUCCESS) != null)
			{
				aoRequest.setAttribute(HHSConstants.SUCCESS_MESSAGE, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.MSG_SUCCESSFULL_BUDGETSUBMIT));
			}

			// R4 fetch EntryType for contractBudget
			aoRequest.setAttribute(HHSConstants.ENTRY_TYPE_ID,
					HHSUtil.getEntryTypeDetail(lsContractId, lsBudgetId, null, null, lsFiscalYearId));
		}
		catch (ApplicationException loExp)
		{
			lsJspPath = getJspPath(loExp, loSession);
		}
		catch (Exception loExp)
		{
			lsJspPath = getJspPath(new ApplicationException("Exception in Contract Budget", loExp), loSession);
		}
		if (null == lsPrintRender)
		{
			loSession.setAttribute(HHSConstants.TASK_DETAIL_BEAN_SESSION, loTaskDetailsBean,
					PortletSession.APPLICATION_SCOPE);
		}
		// below condition is added for enhancement 5618 as part of release
		// 2.7.0
		if (null != PortalUtil.parseQueryString(aoRequest, HHSConstants.VIEW_BUDGET_DOCUMENT)
				&& ApplicationConstants.TRUE.equalsIgnoreCase((String) PortalUtil.parseQueryString(aoRequest,
						HHSConstants.VIEW_BUDGET_DOCUMENT)))
		{
			aoRequest.setAttribute(HHSConstants.VIEW_BUDGET_DOCUMENT, ApplicationConstants.TRUE);
		}
		loModelAndView = new ModelAndView(lsJspPath);
		return loModelAndView;
	}

	/**
	 * This method will be set the necessary contract Information i.e.,
	 * CBGridBean, PortletSessionHandler, RenderRequest
	 * @param aoRequest Request Object
	 * @param loChannelObj Channel Object
	 * @param lsFiscalYearId Fiscal Year
	 * @param lsUserOrgType User-OrgType
	 * @param lsUserId UserId
	 */
	private void setCBGridBean(RenderRequest aoRequest, Channel loChannelObj, String lsFiscalYearId,
			String lsUserOrgType, String lsUserId)
	{
		CBGridBean loCBGridBean = null;
		loCBGridBean = (CBGridBean) loChannelObj.getData(HHSConstants.LO_CB_GRID_BEAN);
		loCBGridBean.setBudgetTypeId(HHSConstants.TWO);
		if (ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(lsUserOrgType))
		{
			loCBGridBean.setModifyByProvider(lsUserId);
		}
		else
		{
			loCBGridBean.setModifyByAgency(lsUserId);
		}

		if (null != lsFiscalYearId)
		{
			loCBGridBean.setFiscalYearID(lsFiscalYearId);
		}
		PortletSessionHandler.setAttribute(loCBGridBean, aoRequest, HHSConstants.CBGRIDBEAN_IN_SESSION);
		loChannelObj.setData(HHSConstants.BUDGET_STATUS, loCBGridBean.getBudgetStatusId());
		aoRequest.setAttribute(HHSConstants.CURRENT_PROC_ID, loCBGridBean.getProcurementID());
	}

	/**
	 * This method will be get the data from DB for jsp's contract Information,
	 * Fiscal Year Information, SubBudgetList and grid's session
	 * @param aoRequest Request Object
	 * @param loChannelObj Channel Object
	 * @param lsContractId ContractId Object
	 * @param lsBudgetId BudgetID Object
	 * @param lsPrintRender PrintRender object
	 * @param loTaskDetailsBean TaskDetailsBean Object
	 * @param asReturnPaymentDetailId ReturnPaymentId Object
	 * @return lsJspPath JspPath Object
	 * @throws ApplicationException an application exception
	 */
	private String getJspPath(RenderRequest aoRequest, Channel loChannelObj, String lsContractId, String lsBudgetId,
			String lsPrintRender, TaskDetailsBean loTaskDetailsBean, String asFiscalYearID,
			String asReturnPaymentDetailId) throws ApplicationException
	{
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsJspPath;
		loTaskDetailsBean.setBudgetId(lsBudgetId);
		loTaskDetailsBean.setContractId(lsContractId);
		String lsActionReqParam = PortalUtil.parseQueryString(aoRequest, HHSConstants.RENDER_ACTION);
		lsJspPath = ContractBudgetControllerUtils.getlsJspPath(lsPrintRender, lsActionReqParam);
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		// values are hard-code will get from BudgetList screen
		loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, lsContractId);
		loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, lsBudgetId);
		loHashMap.put(HHSConstants.FISCAL_YEAR_ID, asFiscalYearID);
		// Added for R6: return payment review task
		loHashMap.put(HHSConstants.RETURN_PAYMENT_DETAIL_ID, asReturnPaymentDetailId);
		// Added for R6: return payment review task end
		loHashMap.put(HHSConstants.USER_ID_2, lsUserId);
		loHashMap.put(
				HHSConstants.AS_ORG_TYPE,
				(String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
						PortletSession.APPLICATION_SCOPE));
		if (null != lsPrintRender && lsPrintRender.equalsIgnoreCase(HHSConstants.PRINTER_VIEW))
		{
			loHashMap.put(HHSConstants.SUBBUDGET_ID,
					PortalUtil.parseQueryString(aoRequest, HHSConstants.PRINTER_SUB_BUDGET_ID));
		}
		aoRequest.setAttribute(HHSConstants.AO_HASH_MAP, loHashMap);
		loChannelObj.setData(HHSConstants.AO_HASH_MAP, loHashMap);
		getJspLandingData(loChannelObj, aoRequest);
		return lsJspPath;
	}

	/**
	 * This method will be get the data from DB for jsp's contract Information,
	 * Fiscal Year Information, SubBudgetList and grid's session
	 * 
	 * @param aoChannelObj channel object
	 * @param aoRequest request object
	 * @throws ApplicationException an application exception
	 */
	@SuppressWarnings("unchecked")
	private void getJspLandingData(Channel aoChannelObj, RenderRequest aoRequest) throws ApplicationException
	{
		try
		{
			String lsActionReqParam = PortalUtil.parseQueryString(aoRequest, HHSConstants.RENDER_ACTION);

			HHSTransactionManager.executeTransaction(aoChannelObj, HHSConstants.GET_CONTRACT_BUDGET);
			ContractList loContractList = (ContractList) aoChannelObj.getData(HHSConstants.LO_CONTRACT_LIST);
			aoRequest.setAttribute(HHSConstants.CONTRACT_INFO, loContractList);

			BudgetDetails loBudgetDetails = (BudgetDetails) aoChannelObj.getData(HHSConstants.LO_BUDGET_DETAILS);
			aoRequest.setAttribute(HHSConstants.FISCAL_BUDGET_INFO, loBudgetDetails);
			// Added for R6: returned payment task
			if (null != lsActionReqParam && lsActionReqParam.equalsIgnoreCase(HHSConstants.TASK_RETURN_PAYMENT_REVIEW))
			{
				ReturnedPayment loReturnedPayment = (ReturnedPayment) aoChannelObj
						.getData(HHSConstants.RETURN_PAYMENT_DETAILS);
				aoRequest.setAttribute(HHSConstants.RETURN_PAYMENT_DETAILS, loReturnedPayment);
			}
			// Added for R6: returned payment review end
			else
			{
				List<CBGridBean> loSubBudgetList = (List<CBGridBean>) aoChannelObj
						.getData(HHSConstants.SUB_BUDGET_LIST);
				HHSUtil.changeSubBudgetNameForHTMLView(loSubBudgetList);
				aoRequest.getPortletSession().setAttribute(HHSConstants.BUDGET_ACCORDIAN_DATA, loSubBudgetList,
						PortletSession.APPLICATION_SCOPE);
			}
		}
		catch (ApplicationException aoExe)
		{
			LOG_OBJECT.Error("Exception occured in getJspLandingData method", aoExe);
			throw aoExe;
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception occured in getJspLandingData method", aoEx);
			throw new ApplicationException("Exception occured in getJspLandingData method");
		}
	}

	/**
	 * This method will be return the error jsp page provide the logger error,
	 * and set error message
	 * 
	 * @param aoExp exception object
	 * @param aoSession PortletSession object
	 * @return lsJspPath error jsp page
	 */
	private String getJspPath(ApplicationException aoExp, PortletSession aoSession)
	{
		LOG_OBJECT.Error("Error occured while processing budgets", aoExp);
		String lsJspPath = HHSConstants.ERROR_PAGE_JSP;
		return lsJspPath;
	}

	/**
	 * This method will be executed when the page will be loaded first time and
	 * then for rendering the actions
	 * 
	 * @param aoRequest an Action Request object
	 * @param aoResponse an Action Response object
	 * @return ModelAndView containing details of the page to be displayed to
	 *         the end user
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@ActionMapping(params = "submit_action=fetchBudgetSummary")
	protected ModelAndView fetchBudgetSummary(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		String lsFormPath = null;
		ModelAndView loModelAndView = null;
		Map loJSPMap = new HashMap();
		loJSPMap.put(HHSConstants.RENDERMAP, loJSPMap);
		loModelAndView = new ModelAndView(lsFormPath, loJSPMap);
		return loModelAndView;
	}

	/**
	 * This method is triggered from Modification Submission Confirmation screen
	 * every time a provider user selects the 'Yes, submit this modification’
	 * button. The provider user is able to confirm that they intend to submit
	 * the Contract Budget Modification in “Pending submission” or “Returned for
	 * revision” status to the Agency for review before the associated workflow
	 * and status updates are triggered.
	 * <ul>
	 * <li>i)Validation are done for username and password fetched from request
	 * parameters to check if they are non empty fields.</li>
	 * <li>ii)Validation are done to validate that the user name entered matches
	 * the user name of the user logged into the system.</li>
	 * <li>iii)If validation fails, error message is displayed, else if
	 * validation is successful then a Channel object loChannelObj is created.</li>
	 * <li>iv)budgetID and budgetTypeId is set in ContractBudgetBean bean
	 * object:aoContractBudgetBean.</li>
	 * <li>v)username and password are set in loChannelObj which serve as input.
	 * parameters to submitBudget transaction for the service
	 * method:authenticateLoginUser in SecurityService Class.</li>
	 * <li>vi)aoContractBudgetBean is set in loChannelObj which serve as input
	 * parameters to submitBudget transaction for the service
	 * method:updatBudgetStatus in ContractBudgetService Class.</li>
	 * <li>vii)We call TransactionManagerR2 to execute submitBudget transaction
	 * which in turn calls authenticateLoginUser and updatBudgetStatus method.</li>
	 * <li>viii)If authenticateLoginUser method returns a boolean value:false
	 * i.e username/password validation fails, then an error message is
	 * displayed on the page.</li>
	 * <li>ix)If authenticateLoginUser method returns a boolean value:true, then
	 * action is done to update <Modification Budget Status> in the DB through
	 * the service method updatBudgetStatus</li>
	 * <li>x) A boolean value is returned from updatBudgetStatus which
	 * determines whether the Modification Budget Status was updated
	 * successfully or not in DB.</li>
	 * </ul>
	 * 
	 * @param aoContractBudgetBean ContractBudgetBean object
	 * @param aoRequest ActionRequest object
	 * @throws ApplicationException an application exception
	 */
	@ActionMapping(params = "modificationSubmissionConfirmationAction=modificationSubmissionConfirmationMap")
	protected void modificationSubmissionConfirmationAction(
			@ModelAttribute("ContractBudgetBean") ContractBudgetBean aoContractBudgetBean, ActionRequest aoRequest)
			throws ApplicationException
	{
		submitConfirmationAction(aoRequest);
	}

	/**
	 * This method is triggered from Update Submission Confirmation screen every
	 * time a provider user selects the 'Yes, submit this Update’ button. The
	 * provider user is able to confirm that they intend to submit the Contract
	 * Budget Update in “Pending submission” or “Returned for revision” status
	 * to the Agency for review before the associated workflow and status
	 * updates are triggered.
	 * <ul>
	 * <li>i)Validation are done for username and password fetched from request
	 * parameters to check if they are non empty fields.</li>
	 * <li>ii)Validation are done to validate that the user name entered matches
	 * the user name of the user logged into the system.</li>
	 * <li>iii)If validation fails, error message is displayed, else if
	 * validation is successful then a Channel object loChannelObj is created.</li>
	 * <li>iv)budgetID and budgetTypeId is set in ContractBudgetBean bean
	 * object:aoContractBudgetBean.</li>
	 * <li>v)username and password are set in loChannelObj which serve as input.
	 * parameters to submitBudget transaction for the service
	 * method:authenticateLoginUser in SecurityService Class.</li>
	 * <li>vi)aoContractBudgetBean is set in loChannelObj which serve as input
	 * parameters to submitBudget transaction for the service
	 * method:updatBudgetStatus in ContractBudgetService Class.</li>
	 * <li>vii)We call TransactionManagerR2 to execute submitBudget transaction
	 * which in turn calls authenticateLoginUser and updatBudgetStatus method.</li>
	 * <li>viii)If authenticateLoginUser method returns a boolean value:false
	 * i.e username/password validation fails, then an error message is
	 * displayed on the page.</li>
	 * <li>ix)If authenticateLoginUser method returns a boolean value:true, then
	 * action is done to update <Update Budget Status> in the DB through the
	 * service method updatBudgetStatus</li>
	 * <li>x) A boolean value is returned from updatBudgetStatus which
	 * determines whether the Update Budget Status was updated successfully or
	 * not in DB.</li>
	 * </ul>
	 * 
	 * @param aoContractBudgetBean ContractBudgetBean object
	 * @param aoRequest ActionRequest object
	 * @throws ApplicationException an application exception
	 */
	@ActionMapping(params = "updateSubmitConfirmationAction=updateSubmitConfirmationMap")
	protected void updateSubmitConfirmationAction(
			@ModelAttribute("ContractBudgetBean") ContractBudgetBean aoContractBudgetBean, ActionRequest aoRequest)
			throws ApplicationException
	{
		submitConfirmationAction(aoRequest);
	}

	/**
	 * This is the common method for updateSubmitConfirmationAction and
	 * modificationSubmissionConfirmationAction
	 * 
	 * @param aoRequest ActionRequest object
	 * @throws ApplicationException an application exception
	 */
	private void submitConfirmationAction(ActionRequest aoRequest) throws ApplicationException
	{
		PortletSession loPortletSession = aoRequest.getPortletSession();
		String lsSubmitUserId = null;
		lsSubmitUserId = aoRequest.getParameter(HHSConstants.KEY_SESSION_USER_NAME);
		String lsLoggedInUserId = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_EMAIL_ID,
				PortletSession.APPLICATION_SCOPE);
		if (lsLoggedInUserId.equalsIgnoreCase(lsSubmitUserId))
		{
			Channel loChannel = new Channel();

			Boolean loIsValidUser = (Boolean) loChannel.getData(HHSConstants.LB_AUTHENTICATE_USER);
			Boolean loIsEntryCorrect = (Boolean) loChannel.getData(HHSConstants.LB_SAVE_STATUS);

			if (!loIsValidUser)
			{
				aoRequest.setAttribute(HHSConstants.CLC_ERROR_MSG, HHSConstants.THIS_NOT_VALID_USER);
			}
			else if (!loIsEntryCorrect)
			{
				aoRequest.setAttribute(HHSConstants.CLC_ERROR_MSG, HHSConstants.THIS_NOT_VALID_USER);
			}
		}
		else
		{
			aoRequest.setAttribute(HHSConstants.CLC_ERROR_MSG, HHSConstants.THIS_NOT_VALID_USER);
		}
	}

	/**
	 * <ul>
	 * <li>This method is triggered on clicking save button on Contract Budget
	 * screens (Base/Amend/Modification/Update)</li>
	 * <li>Transaction Id : fetchFyBudgetSummary</li>
	 * 
	 * <li>Service Class : ContractBudgetService</li>
	 * 
	 * <li>Validations on click of save button:-</li>
	 * <ol>
	 * <li>Visible and enabled for provider users if Amendment Budget Status =
	 * “Pending Submission” or “Returned for Revision”</li>
	 * <li>Hidden for provider users if Amendment Budget Status = “Pending
	 * Approval”, “Pending Registration”, “Approved”, “Cancelled”, “Suspended”
	 * or “Closed”</li>
	 * <li>Hidden for Agency and Accelerator users</li>
	 * <li>Save all the data entered, including comments and an Active grid row
	 * if one was being edited.</li>
	 * <li>If comments exist, append Comments from S346.45 Comment Text Box to
	 * S346.49 View Comments History table.</li>
	 * <li>Stay on the same page and keep any expanded sections as expanded.</li>
	 * <li>If the budget section was open, then display the same tab which was
	 * displayed before save and refresh all screen elements.</li>
	 * </ol>
	 * </ul>
	 * 
	 * @param aoResourceRequestForBudget request param
	 * @param aoResourceResponseForBudget responce param
	 * @return loModelAndView ModelAndView object
	 * @throws ApplicationException an application exception
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@ResourceMapping("saveContractBudget")
	public ModelAndView saveContractBudget(ResourceRequest aoResourceRequestForBudget,
			ResourceResponse aoResourceResponseForBudget) throws ApplicationException
	{
		ModelAndView loModelAndViewForBudget = null;
		PrintWriter loOutForBudget = null;
		Map<String, String> loMap = null;
		String lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;

		try
		{
			aoResourceResponseForBudget.setContentType(HHSConstants.TEXT_HTML);
			loOutForBudget = aoResourceResponseForBudget.getWriter();
			loMap = ContractBudgetControllerUtils.validateBudgetStatus(aoResourceRequestForBudget
					.getParameter(HHSConstants.BUDGET_ID));
			if (loMap.containsKey(HHSConstants.SUCCESS_MESSAGE))
			{
				String lsUserId = (String) aoResourceRequestForBudget.getPortletSession().getAttribute(
						ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
				String lsProviderComment = aoResourceRequestForBudget.getParameter(HHSConstants.PUBLIC_COMMENT_AREA);
				TaskDetailsBean loTaskBeanForBudget = new TaskDetailsBean();
				loTaskBeanForBudget.setEntityId(aoResourceRequestForBudget.getParameter(HHSConstants.BUDGET_ID));
				loTaskBeanForBudget.setEntityType(HHSConstants.BUDGET_TYPE3);
				loTaskBeanForBudget.setIsTaskScreen(false);
				loTaskBeanForBudget.setProviderComment(lsProviderComment);
				loTaskBeanForBudget.setUserId(lsUserId);
				saveCommentNonAudit(loTaskBeanForBudget);
				Channel loChannel = new Channel();
				Map loInputContractMap = new HashMap();
				loInputContractMap.put(HHSConstants.CONTRACT_ID_WORKFLOW,
						aoResourceRequestForBudget.getParameter(HHSConstants.CONTRACT_ID));
				loInputContractMap.put(HHSConstants.BUDGET_ID_WORKFLOW,
						aoResourceRequestForBudget.getParameter(HHSConstants.BUDGET_ID));
				loChannel.setData(HHSConstants.AO_HASH_MAP, loInputContractMap);
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_FY_BUDGET_SUMMARY);
				BudgetDetails loBudgetDetails = (BudgetDetails) loChannel.getData(HHSConstants.LO_BUDGET_DETAILS);
				aoResourceRequestForBudget.setAttribute(HHSConstants.FISCAL_BUDGET_INFO, loBudgetDetails);
				lsErrorMsg = HHSConstants.EMPTY_STRING;
				loModelAndViewForBudget = new ModelAndView(HHSConstants.JSP_PATH_CONTRACT_FY_BUDGET);
			}
			else
			{
				// Validation error
				lsErrorMsg = loMap.get(HHSConstants.ERROR_MESSAGE);
			}
		}
		catch (ApplicationException aoExp)
		{
			// Made changes for enhancement id 6000 release 3.8.0.
			lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("Application Exception in saveContractBudget method", aoExp);
		}
		catch (Exception aoExp)
		{
			// Made changes for enhancement id 6000 release 3.8.0.
			lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("Exception in saveContractBudget method", aoExp);
		}
		finally
		{
			if (null != loOutForBudget)
			{
				if (!HHSR5Constants.EMPTY_STRING.equalsIgnoreCase(lsErrorMsg))
				{
					loOutForBudget.print(lsErrorMsg);
				}
				loOutForBudget.flush();
				loOutForBudget.close();
			}
		}
		return loModelAndViewForBudget;
	}

	/**
	 * This method is used to check --> Review levels set for the Agency in
	 * order to launch WF 304 Contract Budget After which WF 304 Contract Budget
	 * is launched and Budget status is changed to 'Pending Approval' <li>
	 * Execute transaction <b> fetchReviewLevelCB </b></li> <br>
	 * Method updated for R4.
	 * @param aoActionRequest ActionRequest object
	 * @param aoActionResponse ActionResponse object
	 * @throws ApplicationException an application exception
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@ActionMapping(params = "submit_action=submitConfirmation")
	public void submitConfirmationOverlay(ActionRequest aoActionRequest, ActionResponse aoActionResponse)
			throws ApplicationException
	{
		PortletSession loSession = aoActionRequest.getPortletSession();
		String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		Channel loChannel = new Channel();
		HashMap loHmRequiredProps = new HashMap();
		String lsContractId = aoActionRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW);
		String lsBudgetId = aoActionRequest.getParameter(HHSConstants.BUDGET_ID_WORKFLOW);
		String lsAgencyId = null;
		String lsProcurementId = aoActionRequest.getParameter(HHSConstants.CURRENT_PROC_ID);
		String lsMsg;
		// R4: Tab Level Comments
		Map<String, String> loTabLevelCommentsMap = null;
		// R4: Tab Level Comments Ends
		try
		{
			loChannel.setData(HHSConstants.BUDGET_ID_KEY, lsBudgetId);
			loChannel.setData(HHSConstants.CONTRACT_ID_KEY, lsContractId);
			loChannel.setData(HHSConstants.REVIEW_PROC_ID,
					HHSConstants.FINANCIAL_TASK_PROCESS_ID_MAP.get(HHSConstants.TASK_BUDGET_REVIEW));
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_AGENCY_ID);
			lsAgencyId = (String) loChannel.getData(HHSConstants.AS_AGENCY_ID);
			loChannel.setData(HHSConstants.PROPERTY_PE_AGENCY_ID, lsAgencyId);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_REVIEW_LEVEL_CB);
			Integer liReviewLevel = (Integer) loChannel.getData(HHSConstants.REVIEW_LEVEL);
			if (liReviewLevel == HHSConstants.INT_ZERO)
			{
				// No Agency Review Levels set
				lsMsg = ContractBudgetControllerUtils.fetchErrorMsg(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.CB_REVIEW_LEVEL_ERROR);
				ApplicationSession.setAttribute(lsMsg, aoActionRequest, HHSConstants.ERROR_MESSAGE);
			}
			else
			{
				// Fetch budget status for launching the Contract Budget Task
				// and changing the status
				String lsBudgetStatus = ContractBudgetControllerUtils.fetchCurrentBudgetStatus(lsBudgetId);
				// Map to launch WF304 - Contract Budget
				loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
				loHmRequiredProps.put(HHSConstants.PROCUREMENT_ID, lsProcurementId);
				loHmRequiredProps.put(HHSConstants.CONTRACT_ID_WORKFLOW, lsContractId);
				loHmRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW, lsBudgetId);
				loHmRequiredProps.put(HHSConstants.SUBMITTED_BY, lsUserId);
				loHmRequiredProps.put(HHSConstants.MOD_BY_USER_ID, HHSConstants.EMPTY_STRING);
				loHmRequiredProps.put(HHSConstants.AUDIT_PROVIDER_INITIATED_TASK, true);

				// Provider initiated task flag
				loHmRequiredProps.put(HHSConstants.WORKFLOW_NAME, HHSConstants.WF_CONTRACT_BUDGET_REVIEW);

				// Map for Comments to be saved in History
				String lsProviderComment = aoActionRequest.getParameter(HHSConstants.PUBLIC_COMMENT_AREA);

				// Set Audit Data in channel to be saved in History
				setAuditDataInChannel(loChannel, lsBudgetId, lsUserId, lsProviderComment, lsBudgetStatus,
						loTabLevelCommentsMap);

				loHmRequiredProps.put(HHSConstants.AS_STATUS_ID,
						HHSUtil.getStatusID(HHSConstants.BUDGETLIST_BUDGET, HHSConstants.STATUS_PENDING_APPROVAL));

				loChannel.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
				loChannel.setData(HHSConstants.LB_AUTH_STATUS_FLAG, true);
				loChannel.setData(HHSConstants.AO_HMWF_REQUIRED_PROPS, loHmRequiredProps);
				// R4: Setting TabLevelComments Map in Channel
				loChannel.setData(HHSConstants.CHANNEL_PARAM_TAB_LEVEL_COMMENTS_MAP_PROVIDER, loTabLevelCommentsMap);
				// Start R5 : set EntityId and EntityName for AutoSave
				CommonUtil.setChannelForAutoSaveData(loChannel, lsBudgetId, HHSConstants.BUDGET_TYPE3);
				// End R5 : set EntityId and EntityName for AutoSave
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.LAUNCH_WF_CONTRACT_BUDGET);
				aoActionResponse.setRenderParameter(HHSConstants.SUBMIT_OVERLAY_SUCCESS,
						HHSConstants.SUBMIT_OVERLAY_SUCCESS);
			}
		}
		catch (ApplicationException aoAppExe)
		{
			LOG_OBJECT.Error("Exception occured in submitConfirmationOverlay", aoAppExe);
			// Application Exception occurred
			ApplicationSession.setAttribute(HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED, aoActionRequest,
					HHSConstants.ERROR_MESSAGE);
		}
		aoActionResponse.setRenderParameter(HHSConstants.CONTRACT_ID_WORKFLOW, lsContractId);
		aoActionResponse.setRenderParameter(HHSConstants.BUDGET_ID_WORKFLOW, lsBudgetId);
		aoActionResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.CONTRACT_BUDGET_HANDLER);
	}

	//Start:Updated in R7 for Cost-Center
	/**
	 * This method is used to display the Submit Contract Budget Confirmation
	 * Overlay
	 * <ul>
	 * <li>Get the required view name from request</li>
	 * <li>Execute transaction <b> validateCBAmountTotal </b></li>
	 * <li>Release 3.6.0 Enhancement id 6484</li>
	 * </ul>
	 * 
	 * @param aoResourceRequest ResourceRequest Object
	 * @param aoResourceResponse ResourceResponse Object
	 * @return loModelAndView ModelAndView Object
	 * @throws Exception an exception
	 */
	@ResourceMapping("submitContractBudgetOverlay")
	public ModelAndView submitContractBudgetOverlay(ResourceRequest aoResourceRequest,
			ResourceResponse aoResourceResponse) throws Exception
	{
		ModelAndView loModelAndView = null;
		Map<String, String> loMap = null;
		// Made changes for enhancement id 6000 release 3.8.0.
		String lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
		try
		{
			loMap = ContractBudgetControllerUtils.validateBudgetStatus(aoResourceRequest
					.getParameter(HHSConstants.BUDGET_ID));
			// Release 3.6.0 Enhancement id 6484
			ContractBudgetControllerUtils.validateSubBudgetSite(aoResourceRequest.getParameter(HHSConstants.BUDGET_ID),
					loMap, aoResourceRequest.getParameter(HHSConstants.FISCAL_YEAR_ID));
			if (loMap.containsKey(HHSConstants.SUCCESS_MESSAGE))
			{
				// validate for FY Amount doesn't match with total city funded
				Channel loChannel = new Channel();
				loChannel.setData(HHSConstants.BUDGET_ID_KEY, aoResourceRequest.getParameter(HHSConstants.BUDGET_ID));
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.CB_VALIDATE_SUBMIT_AMOUNT_TOTAL);
				Boolean loValid = (Boolean) loChannel.getData(HHSConstants.AB_VALIDATE_STATUS);
				//Start: Added in R7 for Cost-Center
				HashMap loServiceValid = (HashMap) loChannel.getData(HHSConstants.AO_HASH_MAP);
				String lsIsServicesSuccess =(String) loServiceValid.get(HHSConstants.SUCCESS);
				//End: Added in R7 for Cost-Center
				// Following condition updated in Release 7 for Cost-Center
				if (loValid.booleanValue() && Boolean.parseBoolean(lsIsServicesSuccess))
				{
					// amount if it success then redirect to overlay
					String lsJspName = aoResourceRequest.getParameter(HHSConstants.JSP_NAME);
					aoResourceRequest.setAttribute(HHSConstants.BUDGET_ID_KEY,
							aoResourceRequest.getParameter(HHSConstants.BUDGET_ID));
					aoResourceRequest.setAttribute(HHSConstants.AGENCY_ID,
							aoResourceRequest.getParameter(HHSConstants.AGENCY_ID1));
					aoResourceRequest.setAttribute(HHSConstants.CONTRACT_ID_KEY,
							aoResourceRequest.getParameter(HHSConstants.CONTRACT_ID));
					String lsJspPath = HHSConstants.JSP_CONTRACTBUDGET + lsJspName;
					loModelAndView = new ModelAndView(lsJspPath);
				}
				else
				{
					if (!loValid.booleanValue())
					{
						setErrorMsgJSP(aoResourceResponse, PropertyLoader.getProperty(
								HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.MSG_KEY_SUBMIT_BUDGET_ERR),
								HHSConstants.INT_ONE);
					}
					else
					{
						setErrorMsgJSP(aoResourceResponse, PropertyLoader.getProperty(
								HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, (String)loServiceValid.get(HHSConstants.CBL_MESSAGE)),
								HHSConstants.INT_ONE);
					}
				}
			}
			else
			{
				// set error message
				setErrorMsgJSP(aoResourceResponse, loMap.get(HHSConstants.ERROR_MESSAGE), HHSConstants.INT_ONE);
			}
		}
		// Made changes for enhancement id 6000 release 3.8.0.
		catch (ApplicationException aoExp)
		{
			lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("Application Exception in saveContractBudget method", aoExp);
			setErrorMsgJSP(aoResourceResponse, lsErrorMsg, HHSConstants.INT_ONE);
		}
		catch (Exception aoExp)
		{
			lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("Exception in saveContractBudget method", aoExp);
			setErrorMsgJSP(aoResourceResponse, lsErrorMsg, HHSConstants.INT_ONE);
		}
		return loModelAndView;
	}
	
	//End: Updated in R7 for Cost-Center

	/**
	 * This method is used to validate the User log in on Submit button from S -
	 * 313 Contract Budget screen
	 * 
	 * 
	 * @param aoResourceRequest ResourceRequest object
	 * @param aoResourceResponse ResourceResponse object
	 * @throws ApplicationException an application exception
	 */
	@ResourceMapping("validateUser")
	@SuppressWarnings(
	{ "rawtypes" })
	public void validateUserCB(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
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
				lsErrorMsg = ContractBudgetControllerUtils.fetchErrorMsg(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.CB_VALIDATE_ERROR);
				liErrorCode = HHSConstants.INT_ONE;
				setErrorMsgJSP(aoResourceResponse, lsErrorMsg, liErrorCode);
			}
		}
		catch (ApplicationException aoAppExe)
		{
			LOG_OBJECT.Error("Exception occured in submitConfirmationOverlay", aoAppExe);
			// Application Exception occurred
			liErrorCode = HHSConstants.INT_TWO;
			setErrorMsgJSP(aoResourceResponse, HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED, liErrorCode);
		}

	}

	/**
	 * This method write error message on jsp in JSON format
	 * 
	 * @param aoResourceResponse Resource Response object
	 * @param lsErrorMsg Error message
	 * @param liErrorCode Error code
	 */
	private void setErrorMsgJSP(ResourceResponse aoResourceResponse, String lsErrorMsg, int liErrorCode)
	{
		PrintWriter loOut = null;
		try
		{
			aoResourceResponse.setContentType(HHSConstants.APPLICATION_JSON);
			loOut = aoResourceResponse.getWriter();
			lsErrorMsg = HHSConstants.ERROR_1 + liErrorCode + HHSConstants.MESSAGE_1 + lsErrorMsg
					+ HHSConstants.CLOSING_BRACE_1;
			loOut.print(lsErrorMsg);
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
	 * This method set audit data in channel object <br>
	 * Method updated for R4.
	 * @param aoChannel Channel object
	 * @param asBudgetId Budget Id
	 * @param asUserId User Id
	 * @param asComment comments
	 * @param asStatus Budget Status
	 * @param aoTabLevelCommentsMap R4: Adding Map Containing Tab Level Comments
	 * @throws ApplicationException an application exception
	 */
	public void setAuditDataInChannel(Channel aoChannel, String asBudgetId, String asUserId, String asComment,
			String asStatus, Map<String, String> aoTabLevelCommentsMap) throws ApplicationException
	{
		List<HhsAuditBean> loAuditList = new ArrayList<HhsAuditBean>();
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		String lsPrevStatus = HHSUtil.getStatusName(HHSConstants.BUDGETLIST_BUDGET, Integer.valueOf(asStatus));
		String lsStatusChange = HHSConstants.STATUS_CHANGED_FROM;

		StringBuilder loStatusChange = new StringBuilder();
		loStatusChange.append(lsStatusChange);
		loStatusChange.append(HHSConstants.SPACE);
		loStatusChange.append(HHSConstants.STR);
		loStatusChange.append(lsPrevStatus);
		loStatusChange.append(HHSConstants.STR);
		loStatusChange.append(HHSConstants._TO_);
		loStatusChange.append(HHSConstants.STR);
		loStatusChange.append(HHSConstants.STATUS_PENDING_APPROVAL);
		loStatusChange.append(HHSConstants.STR);
		// R4: Adding Tab Level Comments data to Audit List
		if (null != aoTabLevelCommentsMap && !aoTabLevelCommentsMap.isEmpty())
		{
			Iterator loTabLevelCommentsMapItr = aoTabLevelCommentsMap.entrySet().iterator();
			while (loTabLevelCommentsMapItr.hasNext())
			{
				Map.Entry<String, String> loTabLevelCommentsMapTemp = (Entry<String, String>) loTabLevelCommentsMapItr
						.next();
				if (null != loTabLevelCommentsMapTemp.getKey() && null != loTabLevelCommentsMapTemp.getValue())
				{
					loAuditList.add(HHSUtil.addAuditDataToChannel(P8Constants.PROPERTY_PE_TH_PROVIDER_COMMENT,
							loTabLevelCommentsMapTemp.getKey(), loTabLevelCommentsMapTemp.getValue(),
							HHSConstants.BUDGET_TYPE3, asBudgetId, asUserId, HHSConstants.PROVIDER_AUDIT));
				}
			}
		}
		// R4: Adding Tab Level Comments Ends
		if (null != asComment && !asComment.isEmpty())
		{
			loAuditList.add(HHSUtil.addAuditDataToChannel(P8Constants.PROPERTY_PE_TH_PROVIDER_COMMENT,
					P8Constants.EVENT_TYPE_WORKFLOW, asComment, HHSConstants.BUDGET_TYPE3, asBudgetId, asUserId,
					HHSConstants.PROVIDER_AUDIT));
		}

		loAuditList.add(HHSUtil
				.addAuditDataToChannel(HHSConstants.STATUS_CHANGE, HHSConstants.STATUS_CHANGE,
						loStatusChange.toString(), HHSConstants.BUDGET_TYPE3, asBudgetId, asUserId,
						HHSConstants.PROVIDER_AUDIT));

		loHhsAuditBean.setEntityId(asBudgetId);
		loHhsAuditBean.setEntityType(HHSConstants.BUDGET_TYPE3);
		aoChannel.setData(HHSConstants.LO_AUDIT_LIST, loAuditList);
		aoChannel.setData(HHSConstants.AUDIT_BEAN, loHhsAuditBean);
	}

	/**
	 * This method is added in Release 6 for Return Payment. 
	 * It will save details of Return Payment
	 * bean on return payment review task detail screen.
	 * @param aoResourceRequest request param
	 * @param aoResourceResponse response param
	 * @throws ApplicationException
	 */
	@ResourceMapping("saveReturnPaymentDetails")
	public void saveReturnPaymentDetails(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse) 
			throws ApplicationException
	{

		Channel loChannel = new Channel();
		HashMap<String, String> loHmRequiredProps = new HashMap<String, String>();
		String lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
		boolean lbSaveStatus = false;
		try
		{
			String lsreturnPaymentDetailId = aoResourceRequest.getParameter(HHSConstants.RETURN_PAYMENT_DETAIL_ID);
			loHmRequiredProps.put(HHSConstants.RETURN_PAYMENT_DETAIL_ID, lsreturnPaymentDetailId);
			loHmRequiredProps.put(HHSR5Constants.CHECK_NUMBER,
					aoResourceRequest.getParameter(HHSR5Constants.CHECK_NUMBER));
			loHmRequiredProps.put(HHSR5Constants.CHECK_DATE, aoResourceRequest.getParameter(HHSR5Constants.CHECK_DATE));
			loHmRequiredProps.put(HHSR5Constants.RECEIVED_DATE_FOR_RETURN_PAYMENT,
					aoResourceRequest.getParameter(HHSR5Constants.RECEIVED_DATE_FOR_RETURN_PAYMENT));
			loHmRequiredProps.put(HHSR5Constants.DESCRIPTION_FOR_RETURN_PAYMENT,
					aoResourceRequest.getParameter(HHSR5Constants.DESCRIPTION_FOR_RETURN_PAYMENT));
			loHmRequiredProps.put(HHSR5Constants.CHECK_AMOUNT_FOR_RETURN_PAYMENT,
					aoResourceRequest.getParameter(HHSR5Constants.CHECK_AMOUNT_FOR_RETURN_PAYMENT).replaceAll(
							HHSConstants.COMMA, HHSConstants.EMPTY_STRING));
			loHmRequiredProps.put(HHSR5Constants.AGENCY_TRACKING_FOR_RETURN_PAYMENT,
					aoResourceRequest.getParameter(HHSR5Constants.AGENCY_TRACKING_FOR_RETURN_PAYMENT));
			loHmRequiredProps.put(HHSConstants.USER_ID,
					(String) aoResourceRequest.getPortletSession().getAttribute(
							ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE));
			loChannel.setData(HHSConstants.AO_HASH_MAP, loHmRequiredProps);
			HHSTransactionManager.executeTransaction(loChannel,
					HHSR5Constants.TRANSACTION_FOR_SAVE_DETAILS_RETURN_PAYMENT_REVIEW,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			lbSaveStatus = (Boolean) loChannel.getData(HHSConstants.SAVE_BUTTON_STATUS);
			if(lbSaveStatus){
				String lsSuccessMessage = HHSR5Constants.SAVE_SUCCESS_MSG;
				setErrorMsgJSP(aoResourceResponse, lsSuccessMessage, HHSConstants.INT_TWO);
			}
		}
		catch (ApplicationException aoAppExp)
		{
			lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("Application Exception in saveReturnPaymentDetails method", aoAppExp);
			setErrorMsgJSP(aoResourceResponse, lsErrorMsg, HHSConstants.INT_ONE);
		}
		catch (Exception aoExp)
		{
			lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("Exception in saveReturnPaymentDetails method", aoExp);
			setErrorMsgJSP(aoResourceResponse, lsErrorMsg, HHSConstants.INT_ONE);
		}
	}
	
	/**
	 * This method is used to get returned payment notification history for particular Budget
	 * The method is added in Release 6 for Return Payment. 
	 * @param aoResourceRequest ResourceRequest object 
	 * @param aoResourceResponse ResourceResponse object
	 * @return ModelAndView 
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	@ResourceMapping("getNotificationHistory")
	public ModelAndView getNotificationHistory(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws ApplicationException
	{
		String lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
		Channel loChannel = new Channel();
		List<ReturnPaymentNotification> loResult = new ArrayList<ReturnPaymentNotification>();
		String lsJspName = HHSConstants.JSP_CONTRACTBUDGET + HHSR5Constants.NOTIFICATION_HISTORY_OVERLAY;
		
		try
		{
			String lsBudgetId = HHSPortalUtil.parseQueryString(aoResourceRequest, HHSP8Constants.BUDGET_ID);
			loChannel.setData(HHSConstants.RSLT_BUDGET_ID, lsBudgetId);
			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.GET_NOTIFICATION_HISTORY,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
		}
		catch (ApplicationException aoExp)
		{
			lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("Exception in getNotificationHistory method", aoExp);
			setErrorMsgJSP(aoResourceResponse, lsErrorMsg, HHSConstants.INT_ONE);
		}
		catch (Exception aoExp)
		{
			lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("Exception in getNotificationHistory method", aoExp);
			setErrorMsgJSP(aoResourceResponse, lsErrorMsg, HHSConstants.INT_ONE);
		}
		loResult = (List<ReturnPaymentNotification>) loChannel.getData(HHSR5Constants.NOTIFICATION_HISTORY);
		aoResourceRequest.setAttribute(HHSR5Constants.LIST_NOTIFICATION_HISTORY, loResult);
		return new ModelAndView(lsJspName);
	}
	
	/**
	 * This method is added in Release 6 for Return Payment.  
	 * It will get list of providers for notifying.
	 * @param aoResourceRequest ResourceRequest object 
	 * @param aoResourceResponse ResourceResponse object 
	 * @return ModelAndView
	 * @throws ApplicationException
	 */
	@ResourceMapping("getNotificationProvider")
	public ModelAndView getNotificationProvider(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws ApplicationException
	{
		String lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
		String lsJspName = HHSConstants.JSP_CONTRACTBUDGET + HHSR5Constants.NOTIFY_PROVIDER_OVERLAY;
		String lsComponentName = HHSR5Constants.STRING_RETURN_PAYMENT;
		String lsSettingsName = HHSR5Constants.SAMPLE_NOTIFICATION_2;
		try
		{
			HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb.getInstance()
					.getCacheObject(ApplicationConstants.APPLICATION_SETTING);
			String lsSampleText = (String) loApplicationSettingMap.get(lsComponentName + HHSConstants.UNDERSCORE
					+ lsSettingsName);
			aoResourceRequest.setAttribute(HHSR5Constants.NOTIFICATION_PROVIDER_LIST,
					HHSR5Constants.NOTIFICATION_PROVIDER_ROLE_MAP);
			aoResourceRequest.setAttribute(HHSR5Constants.STRING_SAMPLE_NOTIFICATION, lsSampleText);
		}
		catch (Exception aoExp)
		{
			lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("Exception in getNotificationHistory method", aoExp);
			setErrorMsgJSP(aoResourceResponse, lsErrorMsg, HHSConstants.INT_ONE);
		}
		return new ModelAndView(lsJspName);
	}
	
	/**
	 * This method is added in Release 6 for Return Payment.  
	 * It will get list of providers for notifying.
	 * Method updated as part of emergency build 6.0.1 - INC000001386100/INC000001385777
	 * Fix for Budget page not loading due to single quote in contract title
	 * @param aoResourceRequest Resource Request
	 * @param aoResourceResponse Resource Response
	 * @return ModelAndView
	 * @throws ApplicationException
	 */
	@ResourceMapping("launchReturnedPaymentOverlay")
	public ModelAndView launchReturnedPaymentOverlay(ResourceRequest aoResourceRequest,
			ResourceResponse aoResourceResponse)
	{
		String lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
		String lsJspName = HHSConstants.JSP_CONTRACTBUDGET + HHSR5Constants.ADD_RETURNED_PAYMENT_OVERLAY;
		String lsComponentName = HHSR5Constants.STRING_RETURN_PAYMENT;
		String lsSettingsName = HHSR5Constants.SAMPLE_NOTIFICATION;
		//emergency build 6.0.1 - INC000001386100/INC000001385777
		//Fix for Budget page not loading due to single quote in contract title
		Channel loChannel = new Channel();
		try
		{
			HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb.getInstance()
					.getCacheObject(ApplicationConstants.APPLICATION_SETTING);
			String lsSampleText = (String) loApplicationSettingMap.get(lsComponentName + HHSConstants.UNDERSCORE
					+ lsSettingsName);
			String lsUserId = (String) aoResourceRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);
			HashMap<String, String> loHashMap = new HashMap<String, String>();
			loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, aoResourceRequest.getParameter(HHSConstants.CONTRACT_ID));
			loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, aoResourceRequest.getParameter(HHSConstants.BUDGET_ID));
			loHashMap.put(HHSConstants.USER_ID_2, lsUserId);
			loChannel.setData(HHSConstants.AO_HASH_MAP, loHashMap);
			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.GET_CONTRACT_SUMMARY_TRANSACTION,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			ContractList loContractList = (ContractList) loChannel.getData(HHSConstants.LO_CONTRACT_LIST);
			aoResourceRequest.setAttribute(HHSConstants.CONTRACT_INFO, loContractList);
			aoResourceRequest.setAttribute(HHSR5Constants.NOTIFICATION_PROVIDER_LIST,
					HHSR5Constants.NOTIFICATION_PROVIDER_ROLE_MAP);
			aoResourceRequest.setAttribute(HHSR5Constants.STRING_SAMPLE_NOTIFICATION, lsSampleText);
		}
		catch (Exception aoExp)
		{
			lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("Exception in launchReturnedPaymentOverlay method", aoExp);
			setErrorMsgJSP(aoResourceResponse, lsErrorMsg, HHSConstants.INT_ONE);
		}
		return new ModelAndView(lsJspName);
	}
	
	
	/**
	 * This method is added in Release 6 for Return Payment. 
	 * This method will update particular returned payment's status 
	 * to cancelled.
	 * @param aoResourceRequest Resource Request
	 * @param aoResourceResponse Resource Response
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	@ResourceMapping("cancelReturnedPayment")
	protected void cancelReturnedPayment(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws ApplicationException
	{
		Channel loChannel = new Channel();
		HashMap<String, String> loWorkFlowTerminateMap = new HashMap<String, String>();
		String lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
		try
		{
			String lsReturnedPaymentId = aoResourceRequest.getParameter(HHSR5Constants.PARAMETER_RETURNED_PAYMENT_ID);
			String lsReturnedPaymentCurrentStatus = ContractBudgetControllerUtils
					.fetchCurrentReturnedPaymentStatus(lsReturnedPaymentId);
			ReturnedPayment loReturnedPayment = new ReturnedPayment();
			loReturnedPayment.setReturnedPaymentId(lsReturnedPaymentId);
			loReturnedPayment.setModifiedByUserId((String) aoResourceRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE));
			loReturnedPayment.setCheckStatus(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSR5Constants.STATUS_RET_PAY_CANCELLED));
			loWorkFlowTerminateMap.put(HHSConstants.RETURN_PAYMENT_DETAIL_ID, lsReturnedPaymentId);
			PortletSession loSession = aoResourceRequest.getPortletSession();
			P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);
			StringBuffer loDataSb = new StringBuffer();
			if (null != lsReturnedPaymentCurrentStatus
					&& lsReturnedPaymentCurrentStatus.equals(HHSR5Constants.STATUS_ONE_EIGHT_FIVE))
			{
				loDataSb.append(HHSConstants.STATUS_CHANGED_FROM).append(HHSConstants.DOUBLE_QUOTE)
						.append(HHSConstants.STATUS_PENDING_SUBMISSION).append(HHSConstants.STR)
						.append(HHSConstants._TO_).append(HHSConstants.STR).append(HHSR5Constants.STATUS_CANCELLED)
						.append(HHSConstants.STR);
			}
			else if (null != lsReturnedPaymentCurrentStatus
					&& lsReturnedPaymentCurrentStatus.equals(HHSR5Constants.STATUS_ONE_EIGHT_SIX))
			{
				loDataSb.append(HHSConstants.STATUS_CHANGED_FROM).append(HHSConstants.DOUBLE_QUOTE)
						.append(HHSConstants.STATUS_PENDING_APPROVAL).append(HHSConstants.STR)
						.append(HHSConstants._TO_).append(HHSConstants.STR).append(HHSR5Constants.STATUS_CANCELLED)
						.append(HHSConstants.STR);
			}
			else if (null != lsReturnedPaymentCurrentStatus
					&& lsReturnedPaymentCurrentStatus.equals(HHSR5Constants.STATUS_ONE_EIGHT_SEVEN))
			{
				loDataSb.append(HHSConstants.STATUS_CHANGED_FROM).append(HHSConstants.DOUBLE_QUOTE)
						.append(HHSConstants.STATUS_APPROVED).append(HHSConstants.STR)
						.append(HHSConstants._TO_).append(HHSConstants.STR).append(HHSR5Constants.STATUS_CANCELLED)
						.append(HHSConstants.STR);
			}
			List<HhsAuditBean> loAuditBeanList = new ArrayList<HhsAuditBean>();
			loAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSConstants.STATUS_CHANGE, HHSConstants.STATUS_CHANGE,
					loDataSb.toString(), HHSR5Constants.RETURNED_PAYMENT_STRING, lsReturnedPaymentId, lsUserId,
					HHSR5Constants.AGENCY_AUDIT));
			loChannel.setData(HHSConstants.LO_AUDIT_LIST, loAuditBeanList);
			loChannel.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
			loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
			loChannel.setData(HHSR5Constants.AO_RETURNED_PAYMENT_ID, lsReturnedPaymentId);
			loChannel.setData(HHSConstants.LO_HM_WF_REQ_PROPS, loWorkFlowTerminateMap);
			loChannel.setData(HHSR5Constants.LO_RETURNED_PAYMENT, loReturnedPayment);
			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.TRANSACTION_RET_PAYMENT,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			String lsSuccessMessage = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE,
					HHSR5Constants.RETURNED_PAYMENT_TASK_CANCELLED);
			setErrorMsgJSP(aoResourceResponse, lsSuccessMessage, HHSConstants.INT_TWO);
		}
		catch (ApplicationException aoExe)
		{
			LOG_OBJECT.Error("Exception occured in cancelReturnedPayment method", aoExe);
			lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			setErrorMsgJSP(aoResourceResponse, lsErrorMsg, HHSConstants.INT_ONE);
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception occured in cancelReturnedPayment method", aoEx);
			lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			setErrorMsgJSP(aoResourceResponse, lsErrorMsg, HHSConstants.INT_ONE);
		}
	}
	
	/**
	 * This method is added in Release 6 for Return Payment. 
	 * This method will initiate particular returned
	 * payment's task
	 * 
	 * @param aoResourceRequest
	 *            Resource Request
	 * @param aoResourceResponse
	 *            Resource Response
	 * @throws ApplicationException
	 *             Exception thrown in case of any application code failure.
	 */
	@ResourceMapping("initiateReturnedPayment")
	protected void initiateReturnedPayment(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		Channel loChannel = new Channel();
		String lsMsg = HHSConstants.EMPTY_STRING;
		String lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
		HashMap<String, Object> loHmRequiredProps = new HashMap<String, Object>();
		Map<String, String> loTabLevelCommentsMap = null;
		//[Start] Defect Fix- 8612 in Release 6
		boolean lbErrorMsgFlag = HHSR5Constants.BOOLEAN_FALSE;
		//[End] Defect Fix- 8612 in Release 6
		try
		{
			String lsReturnedPaymentId = aoResourceRequest.getParameter(HHSR5Constants.PARAMETER_RETURNED_PAYMENT_ID);
			ReturnedPayment loReturnedPayment = new ReturnedPayment();
			loReturnedPayment.setReturnedPaymentId(lsReturnedPaymentId);
			loReturnedPayment.setModifiedByUserId((String) aoResourceRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE));
			loReturnedPayment.setCheckStatus(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSR5Constants.STATUS_RET_PAY_PENDING_APPROVAL));
			PortletSession loSession = aoResourceRequest.getPortletSession();
			String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);
			P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			String lsContractId = aoResourceRequest.getParameter(HHSConstants.CONTRACT_ID);
			String lsBudgetId = aoResourceRequest.getParameter(HHSConstants.BUDGET_ID);
			loChannel.setData(HHSConstants.BUDGET_ID_KEY, lsBudgetId);
			loChannel.setData(HHSConstants.CONTRACT_ID_KEY, lsContractId);
			loChannel.setData(HHSConstants.REVIEW_PROC_ID,
					HHSConstants.FINANCIAL_TASK_PROCESS_ID_MAP.get(HHSConstants.TASK_RETURN_PAYMENT_REVIEW));
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_AGENCY_ID);
			String lsAgencyId = (String) loChannel.getData(HHSConstants.AS_AGENCY_ID);
			loChannel.setData(HHSConstants.PROPERTY_PE_AGENCY_ID, lsAgencyId);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_REVIEW_LEVEL_CB);
			Integer liReviewLevel = (Integer) loChannel.getData(HHSConstants.REVIEW_LEVEL);
			if (liReviewLevel == HHSConstants.INT_ZERO)
			{
				lsMsg = ContractBudgetControllerUtils.fetchErrorMsg(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.CB_REVIEW_LEVEL_ERROR);
				loSession.setAttribute(HHSConstants.ERROR_MESSAGE, lsMsg);
				//[Start] Defect Fix- 8612 in Release 6
				lbErrorMsgFlag = HHSR5Constants.BOOLEAN_TRUE;
				throw new ApplicationException(PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.CB_REVIEW_LEVEL_ERROR));
				//[End] Defect Fix- 8612 in Release 6
			}
			else
			{
				loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
				loHmRequiredProps = (HashMap<String, Object>) setLaunchWFReqProperties(aoResourceRequest,
						lsReturnedPaymentId);
				StringBuffer loDataSb = new StringBuffer();
				loDataSb.append(HHSConstants.STATUS_CHANGED_FROM).append(HHSConstants.DOUBLE_QUOTE)
						.append(HHSConstants.STATUS_PENDING_SUBMISSION).append(HHSConstants.STR)
						.append(HHSConstants._TO_).append(HHSConstants.STR)
						.append(HHSConstants.STATUS_PENDING_APPROVAL).append(HHSConstants.STR);
				List<HhsAuditBean> loAuditBeanList = new ArrayList<HhsAuditBean>();
				loAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSConstants.STATUS_CHANGE,
						HHSConstants.STATUS_CHANGE, loDataSb.toString(), HHSR5Constants.RETURNED_PAYMENT_STRING,
						lsReturnedPaymentId, lsUserId, HHSR5Constants.AGENCY_AUDIT));
				loAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSConstants.PROPERTY_TASK_CREATION_EVENT,
						HHSConstants.PROPERTY_TASK_CREATION_EVENT, HHSConstants.PROPERTY_TASK_CREATION_DATA,
						HHSR5Constants.TASK_RETURN_PAYMENT_REVIEW, lsReturnedPaymentId, lsUserId,
						HHSR5Constants.AGENCY_AUDIT));
				loChannel.setData(HHSConstants.LO_AUDIT_LIST, loAuditBeanList);
				loChannel.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
				loChannel.setData(HHSConstants.LB_AUTH_STATUS_FLAG, true);
				loChannel.setData(HHSConstants.AO_HMWF_REQUIRED_PROPS, loHmRequiredProps);
				loChannel.setData(HHSConstants.CHANNEL_PARAM_TAB_LEVEL_COMMENTS_MAP_PROVIDER, loTabLevelCommentsMap);
				CommonUtil.setChannelForAutoSaveData(loChannel, lsBudgetId, HHSConstants.BUDGET_TYPE3);
			}
			loChannel.setData(HHSR5Constants.LO_RETURNED_PAYMENT, loReturnedPayment);
			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.TRANSACTION_INITIATE_RETURNED_PAYMENT,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			String lsSuccessMessage = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE,
					HHSR5Constants.RETURNED_PAYMENT_TASK_GENERATED);
			setErrorMsgJSP(aoResourceResponse, lsSuccessMessage, HHSConstants.INT_TWO);
		}
		catch (ApplicationException aoAppExe)
		{
			LOG_OBJECT.Error("Exception occured in initiateReturnedPayment method", aoAppExe);
			//[Start] Defect Fix- 8612 in Release 6
			if (lbErrorMsgFlag)
			{
				aoAppExe.addContextData(HHSConstants.LEVEL_ERROR_MESSAGE, aoAppExe.toString());
			}
			//[End] Defect Fix- 8612 in Release 6
			ApplicationException loAppEx = (ApplicationException) aoAppExe.getRootCause();
			if (null != loAppEx)
			{
				lsErrorMsg = (String) loAppEx.getContextData().get(HHSConstants.LEVEL_ERROR_MESSAGE);
			}
			if (null == lsErrorMsg)
			{
				lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			}
			setErrorMsgJSP(aoResourceResponse, lsErrorMsg, HHSConstants.INT_ONE);
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception occured in initiateReturnedPayment method", aoEx);
			lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			setErrorMsgJSP(aoResourceResponse, lsErrorMsg, HHSConstants.INT_ONE);
		}
	}
	
	/**
	 * This method is added in Release 6 for Return Payment. 
	 * This method sets {@link ReturnedPayment} fields from 
	 * {@link ResourceRequest}
	 * @param aoRequest Action Request
	 * @return {@link ReturnedPayment}
	 */
	private ReturnedPayment setReturnedPaymentObj(ResourceRequest aoResourceRequest) throws ApplicationException
	{
		ReturnedPayment loReturnedPayment = new ReturnedPayment();
		try
		{
			String lsUserId = (String) aoResourceRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			String lsCheckReceived = (String) aoResourceRequest.getParameter(HHSR5Constants.CHECK_RECEIVED_RADIO);
			String lsPendingPayment = (String) aoResourceRequest.getParameter(HHSR5Constants.NOTIFY_PROVIDER_VAL);
			if (null == lsPendingPayment || lsPendingPayment.equalsIgnoreCase(HHSConstants.UNDEFINED))
			{
				lsPendingPayment = HHSConstants.NO_UPPERCASE;
			}
			loReturnedPayment.setDescription((String) aoResourceRequest.getParameter(HHSR5Constants.DESCRIPTION_INPUT));
			loReturnedPayment.setCheckAmount(((String) aoResourceRequest.getParameter(HHSR5Constants.CHECK_AMOUNT_VAL))
					.replaceAll(HHSConstants.COMMA, HHSConstants.EMPTY_STRING));
			loReturnedPayment.setCheckReceived(lsCheckReceived);
			loReturnedPayment.setNotifyProvider(lsPendingPayment);
			loReturnedPayment.setCreatedByUserId(lsUserId);
			loReturnedPayment.setModifiedByUserId(lsUserId);
			if (HHSConstants.YES_UPPERCASE.equalsIgnoreCase(lsCheckReceived))
			{
				loReturnedPayment.setCheckStatus(HHSR5Constants.STATUS_ONE_EIGHT_SIX);
			}
			else
			{
				loReturnedPayment.setCheckStatus(HHSR5Constants.STATUS_ONE_EIGHT_FIVE);
			}
			loReturnedPayment.setBudgetId((String) aoResourceRequest.getParameter(HHSConstants.BUDGET_ID));
			loReturnedPayment.setAgencyTrackingNumber((String) aoResourceRequest
					.getParameter(HHSR5Constants.AGENCY_TRACKING_NUMBER));
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception occured in setReturnedPaymentObj method", aoEx);
			throw new ApplicationException("Exception occured in setReturnedPaymentObj method");
		}
		return loReturnedPayment;
	}
	
	/**
	 * This method is added in Release 6 for Return Payment. 
	 * This method creates HashMap<String,Object> with required properties to launch workflow
	 * @param aoResourceRequest
	 * @param asReturnedPaymentId
	 * @return loHmRequiredProps
	 * @throws ApplicationException
	 */
	private Map<String, Object> setLaunchWFReqProperties(ResourceRequest aoResourceRequest, String asReturnedPaymentId)
			throws ApplicationException
	{
		Map<String, Object> loHmRequiredProps = new HashMap<String, Object>();
		try
		{
			loHmRequiredProps.put(HHSConstants.RETURN_PAYMENT_DETAIL_ID, asReturnedPaymentId);
			loHmRequiredProps.put(HHSConstants.CONTRACT_ID_WORKFLOW,
					aoResourceRequest.getParameter(HHSConstants.CONTRACT_ID));
			loHmRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW,
					aoResourceRequest.getParameter(HHSConstants.BUDGET_ID));
			loHmRequiredProps.put(HHSConstants.SUBMITTED_BY, (String) aoResourceRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE));
			loHmRequiredProps.put(HHSConstants.PROPERTY_PE_SUBMITTED_BY, (String) aoResourceRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE));
			loHmRequiredProps.put(HHSConstants.MOD_BY_USER_ID, HHSConstants.EMPTY_STRING);
			loHmRequiredProps.put(HHSConstants.WORKFLOW_NAME, HHSConstants.WF_RETURNED_PAYMENT_REVIEW);

			loHmRequiredProps.put(HHSConstants.AS_STATUS_ID,
					HHSUtil.getStatusID(HHSConstants.BUDGETLIST_BUDGET, HHSConstants.STATUS_PENDING_APPROVAL));
		}
		catch (ApplicationException aoExe)
		{
			LOG_OBJECT.Error("Exception occured in setLaunchWFReqProperties method", aoExe);
			throw aoExe;
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception occured in setLaunchWFReqProperties method", aoEx);
			throw new ApplicationException("Exception occured in setLaunchWFReqProperties method");
		}
		return loHmRequiredProps;

	}
	
	/**
	 * This method is added in R6 this method will perform action when bulk
	 * notifications action or export bulk task action is selected.
	 * @param aoActionRequest
	 * @param aoActionResponse
	 * @throws ApplicationException
	 * @throws IOException
	 */
	@ResourceMapping("sendNotificationAlert")
	public void sendBulkNotifications(ResourceRequest aoActionRequest, ResourceResponse aoActionResponse)
			throws ApplicationException
	{
		HashMap<String, String> loRequestInput = new HashMap<String, String>();
		aoActionResponse.setContentType(HHSConstants.TEXT_HTML);
		PrintWriter loPrintWriter = null;
		String lsErrorMsg = HHSConstants.EMPTY_STRING;
		boolean response = HHSR5Constants.BOOLEAN_FALSE;
		try
		{
			loRequestInput.put(HHSR5Constants.FISCAL_YEAR, aoActionRequest.getParameter(HHSR5Constants.FISCAL_YEAR));
			loRequestInput.put(
					HHSR5Constants.USER_ID,
					(String) aoActionRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
							PortletSession.APPLICATION_SCOPE));
			loRequestInput.put(HHSConstants.PROGRAM_NAME, aoActionRequest.getParameter(HHSConstants.PROGRAM_NAME));
			loRequestInput.put(HHSR5Constants.ACTION_SELECTED,
					aoActionRequest.getParameter(HHSR5Constants.ACTION_SELECTED));
			loRequestInput.put(HHSR5Constants.ORGANIZATION_TYPE, (String) aoActionRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE));
			loRequestInput.put(HHSR5Constants.ROLE_ATT, aoActionRequest.getParameter(HHSR5Constants.NOT_PROVIDER));
			loRequestInput.put(HHSR5Constants.TARGET_USER, aoActionRequest.getParameter(HHSR5Constants.TARGET_USER));
			loRequestInput.put(HHSConstants.PROGRAM_ID, aoActionRequest.getParameter(HHSConstants.PROGRAM_ID));
			loRequestInput.put(
					HHSConstants.USER_ORG,
					(String) aoActionRequest.getPortletSession().getAttribute(
							ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE));
			loRequestInput.put(HHSConstants.BUDGET_ID_WORKFLOW, aoActionRequest.getParameter(HHSConstants.BUDGET_ID));
			response = sendBulkNotificationsAction(loRequestInput);
			loPrintWriter = aoActionResponse.getWriter();
			if (HHSConstants.BOOLEAN_TRUE == response)
			{
				loPrintWriter.write(HHSR5Constants.NOTIFICATION_SENT);	
			}
			else
			{
				loPrintWriter.write(HHSR5Constants.NOTIFICATION_NOT_SENT);
			}
		}
		catch (IOException aoExp)
		{
			LOG_OBJECT.Error("Exception in sendBulkNotifications method", aoExp);
			lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			setErrorMsgJSP(aoActionResponse, lsErrorMsg, HHSConstants.INT_ONE);
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception in sendBulkNotifications method", aoExp);
			lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			setErrorMsgJSP(aoActionResponse, lsErrorMsg, HHSConstants.INT_ONE);
		}
	}
	
	/**
	 * This method is added in Release 6 as part of Return payment
	 * It is used to insert data in data base for add return payment
	 * <ul>
	 * <li>1). Get required parameters from ResourceRequest.</li>
	 * <li>2). Set values in ReturnedPayment object</li> 
	 * <li>3). executeTransaction <b>addReturnPayment</b>
	 * </li>
	 * </ul>
	 * @param aoResourceRequest to get screen parameters and next action to be
	 *            performed
	 * @param aoResourceResponse decides the next execution flow
	 * @return loModelAndView ModelAndView object
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ResourceMapping("addReturnPaymentUrl")
	public ModelAndView addReturnPayment(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws ApplicationException
	{
		Channel loChannel = new Channel();
		String lsErrorMsg = HHSConstants.EMPTY_STRING;
		try
		{
			ReturnedPayment loReturnedPayment = setReturnedPaymentObj(aoResourceRequest);
			loChannel.setData(HHSR5Constants.LO_RETURNED_PAYMENT, loReturnedPayment);
			String lsCheckReceived = loReturnedPayment.getCheckReceived();
			String lsPendingPayment = (String) aoResourceRequest.getParameter(HHSR5Constants.NOTIFY_PROVIDER_VAL);
			loChannel.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, HHSConstants.BOOLEAN_TRUE);
			loChannel.setData(HHSR5Constants.NOTIFY_FLAG, HHSConstants.BOOLEAN_FALSE);
			loChannel.setData(HHSConstants.LB_AUTH_STATUS_FLAG, HHSConstants.BOOLEAN_FALSE);
			HashMap<String, Object> loHmRequiredProps = new HashMap<String, Object>();
			loHmRequiredProps = (HashMap) setLaunchWFReqProperties(aoResourceRequest, null);
			loHmRequiredProps.put(HHSConstants.LB_AUTH_STATUS_FLAG, HHSConstants.BOOLEAN_FALSE);
			loChannel.setData(HHSR5Constants.LO_HM_REQUIRED_PROPS, loHmRequiredProps);
			if (null != lsCheckReceived && lsCheckReceived.equalsIgnoreCase(HHSConstants.YES_UPPERCASE))
			{
				PortletSession loSession = aoResourceRequest.getPortletSession();
				P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
						ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
				loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);

				String lsContractId = aoResourceRequest.getParameter(HHSConstants.CONTRACT_ID);
				loChannel.setData(HHSConstants.CONTRACT_ID_KEY, lsContractId);
				loHmRequiredProps.put(HHSConstants.LB_AUTH_STATUS_FLAG, HHSConstants.BOOLEAN_TRUE);
				loChannel.setData(HHSR5Constants.LO_HM_REQUIRED_PROPS, loHmRequiredProps);
				loChannel.setData(HHSConstants.LB_AUTH_STATUS_FLAG, HHSConstants.BOOLEAN_TRUE);
			}
			else if (null != lsPendingPayment && lsPendingPayment.equalsIgnoreCase(HHSConstants.YES_UPPERCASE))
			{
				HashMap<String, String> loRequestInput = createNotificationInputMap(aoResourceRequest);
				loRequestInput.put(HHSR5Constants.RETURNED_PAYMENT_DESCRIPTION, loReturnedPayment.getDescription());
				//Added for defect 8544
				loRequestInput.put(HHSR5Constants.REMITTANCE_AMOUNT, loReturnedPayment.getCheckAmount());
				//R6 defect End
				setChannelForNotification(loChannel, loRequestInput);
			}
			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.ADD_RETURN_PAYMENT,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			Integer liInsertedCount = (Integer) loChannel.getData(HHSConstants.LOCAL_COUNT);
			LOG_OBJECT.Info("no. of rows inserted in database ::" + liInsertedCount);
			if (null != liInsertedCount && liInsertedCount > 0)
			{
				aoResourceRequest.setAttribute(HHSR5Constants.SUCCESS_MESSAGE, HHSR5Constants.MESSAGE_RET_PAYMENT_SUCCESS);
			}
			return new ModelAndView(HHSConstants.JSP_CONTRACTBUDGET + HHSR5Constants.CONTRACT_BUDGET_LANDING);
		}
		catch (ApplicationException aoAppExe)
		{
			LOG_OBJECT.Error("Exception occured in inserting add Return Payment data", aoAppExe);
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
			setErrorMsgJSP(aoResourceResponse, lsLevelErrorMessage, HHSConstants.INT_ONE);
			return new ModelAndView();
			
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception occured in inserting add Return Payment data", aoEx);
			lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			setErrorMsgJSP(aoResourceResponse, lsErrorMsg, HHSConstants.INT_ONE);
			return new ModelAndView();
		}
		
	}
	
	/**
	 * This method is added in Release 6 as part of Return payment.
	 * It will create required map for sending notification.
	 * @param aoResourceRequest
	 * @return loRequestInput
	 * @throws ApplicationException
	 */
	private HashMap<String, String> createNotificationInputMap(ResourceRequest aoResourceRequest) throws ApplicationException
	{
		HashMap<String, String> loRequestInput = new HashMap<String, String>();

		try
		{
			loRequestInput.put(HHSR5Constants.FISCAL_YEAR, aoResourceRequest.getParameter(HHSR5Constants.FISCAL_YEAR));
			loRequestInput.put(
					HHSR5Constants.USER_ID,
					(String) aoResourceRequest.getPortletSession().getAttribute(
							ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE));
			loRequestInput.put(HHSConstants.PROGRAM_NAME, aoResourceRequest.getParameter(HHSConstants.PROGRAM_NAME));
			loRequestInput.put(HHSR5Constants.ACTION_SELECTED,
					aoResourceRequest.getParameter(HHSR5Constants.ACTION_SELECTED));
			loRequestInput.put(HHSR5Constants.ORGANIZATION_TYPE, (String) aoResourceRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE));
			loRequestInput.put(HHSR5Constants.ROLE_ATT, aoResourceRequest.getParameter(HHSR5Constants.NOT_PROVIDER));
			loRequestInput.put(HHSR5Constants.TARGET_USER, aoResourceRequest.getParameter(HHSR5Constants.TARGET_USER));
			loRequestInput.put(HHSConstants.PROGRAM_ID, aoResourceRequest.getParameter(HHSConstants.PROGRAM_ID));
			loRequestInput.put(HHSConstants.BUDGET_ID_WORKFLOW, aoResourceRequest.getParameter(HHSConstants.BUDGET_ID));
			loRequestInput.put(
					HHSConstants.USER_ORG,
					(String) aoResourceRequest.getPortletSession().getAttribute(
							ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE));
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception occured in createNotificationInputMap", aoEx);
			throw new ApplicationException("Exception occured in createNotificationInputMap method");
		}
		return loRequestInput;
	}
}
