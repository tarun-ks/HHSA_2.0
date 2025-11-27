package com.nyc.hhs.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;
import org.springframework.web.portlet.mvc.ResourceAwareController;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.controllers.util.BaseControllerUtil;
import com.nyc.hhs.controllers.util.ContractBudgetControllerUtils;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.BudgetDetails;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.ContractList;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.MasterBean;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.rule.Rule;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.ApplicationSession;
import com.nyc.hhs.util.CommonUtil;
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
 * All the actions on contract amend screens will be handled by this controller
 * only.
 * </p>
 */
@Controller(value = "contractBudgetAmendmentHandler")
@RequestMapping("view")
public class ContractBudgetAmendmentController extends BaseController implements ResourceAwareController
{
	/**
	 * Log object to record all logs
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(ContractBudgetAmendmentController.class);
	/**
	 * object for ContractBudgetControllerUtils class
	 */
	ContractBudgetControllerUtils moContractBudgetControllerUtils = new ContractBudgetControllerUtils();
	
	/**
	 * This method will be executed when budget summary screen will be loaded
	 * for first time and it will call service to fetch all details for budget
	 * summary screen. <br>
	 * Method is updated in R4.
	 * <ul>
	 * <li>Transaction manager will be called to call the fetchBudgetSummary
	 * method of ContractBudgetAmendmentService class which will fetch all
	 * details for budget summary screen and the transaction id is
	 * <b>getContractBudgetAmendmen</b>.</li>
	 * </ul>
	 * 
	 * @param aoRequest an Action Request object
	 * @param aoResponse an Action Response object
	 * @return ModelAndView containing details of the page to be displayed to
	 *         the end user
	 * @throws ApplicationException an application exception
	 */
	@SuppressWarnings(
	{ "unchecked" })
	@RenderMapping
	protected ModelAndView handleRenderRequestInternal(RenderRequest aoRequest, RenderResponse aoResponse)
			throws ApplicationException
	{
		String lsJspPath = HHSConstants.EMPTY_STRING;
		//added in R5
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		// R5 changes ends
		String lsActionReqParam = HHSConstants.EMPTY_STRING;
		ModelAndView loModelAndView = null;
		Channel loChannelObj = new Channel();
		CBGridBean loCBGridBean = new CBGridBean();
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		try
		{
			lsJspPath = HHSConstants.JSP_CONTRACTBUDGET_CONTRACT_BUDGET_AMENDMENT_LANDING;
			String lsContractId = PortalUtil.parseQueryString(aoRequest, HHSConstants.CONTRACT_ID_WORKFLOW);
			String lsBudgetId = PortalUtil.parseQueryString(aoRequest, HHSConstants.BUDGET_ID_WORKFLOW);
			String lsBudgetType = HHSConstants.ONE;
			String lsFiscalYearId = PortalUtil.parseQueryString(aoRequest, HHSConstants.CLC_FISCAL_YEAR_ID);
			lsActionReqParam = PortalUtil.parseQueryString(aoRequest, HHSConstants.RENDER_ACTION);
			String lsPrintRender = (String) aoRequest.getParameter(HHSConstants.RENDER_ACTION);
			PortletSession loSession = aoRequest.getPortletSession();
			String lsWorkflowId = PortalUtil.parseQueryString(aoRequest, HHSConstants.WORKFLOW_ID);
			if (null != lsWorkflowId)
			{
				loTaskDetailsBean = fetchTaskDetailsFromFilenet(aoRequest, lsWorkflowId);
				lsContractId = loTaskDetailsBean.getContractId();
				lsBudgetId = loTaskDetailsBean.getBudgetId();
			}
			if (null != lsPrintRender && lsPrintRender.equalsIgnoreCase(HHSConstants.PRINTER_VIEW))
			{
				lsJspPath = HHSConstants.JSP_CONTRACT_BUDGET_AMENDMENT_LANDING_PRINT;
			}
			loTaskDetailsBean.setBudgetId(lsBudgetId);
			loTaskDetailsBean.setContractId(lsContractId);
			HashMap<String, String> loHashMap = new HashMap<String, String>();
			loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, lsContractId);
			loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, lsBudgetId);
			if (null != lsPrintRender && lsPrintRender.equalsIgnoreCase(HHSConstants.PRINTER_VIEW))
			{
				loHashMap.put(HHSConstants.SUBBUDGET_ID,
						PortalUtil.parseQueryString(aoRequest, HHSConstants.PRINTER_SUB_BUDGET_ID));
			}
			loHashMap.put(HHSConstants.BUDGET_TYPE, lsBudgetType);
			loHashMap.put(HHSConstants.FISCAL_YEAR_ID, lsFiscalYearId);
			//added in R5
			loHashMap.put(HHSConstants.USER_ID_2, lsUserId);
			// R5 changes Ends
			aoRequest.setAttribute(HHSConstants.AO_HASH_MAP, loHashMap);
			loChannelObj.setData(HHSConstants.AO_HASH_MAP, loHashMap);
			// Start For fetch data from XML
			MasterBean loMasterBean = null;
			P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			loMasterBean = BaseControllerUtil.generateMasterBean(lsBudgetId, loUserSession);
			loChannelObj.setData(HHSConstants.MASTERBEAN_OBJ, loMasterBean);
			// end For fetch data from XML
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.GET_CONTRACT_BUDGET_AMENDMENT);
			ContractList loContractList = (ContractList) loChannelObj.getData(HHSConstants.LO_CONTRACT_LIST);
			aoRequest.setAttribute(HHSConstants.CONTRACT_INFO, loContractList);
			BudgetDetails loBudgetDetails = (BudgetDetails) loChannelObj.getData(HHSConstants.LO_BUDGET_DETAILS);
			aoRequest.setAttribute(HHSConstants.FISCAL_BUDGET_INFO, loBudgetDetails);
			List<CBGridBean> loSubBudgetList = (List<CBGridBean>) loChannelObj.getData(HHSConstants.SUB_BUDGET_LIST);
			HHSUtil.changeSubBudgetNameForHTMLView(loSubBudgetList);
			aoRequest.getPortletSession().setAttribute(HHSConstants.BUDGET_ACCORDIAN_DATA, loSubBudgetList,
					PortletSession.APPLICATION_SCOPE);
			loCBGridBean = (CBGridBean) loChannelObj.getData(HHSConstants.LO_CB_GRID_BEAN);
			loCBGridBean.setBudgetTypeId(HHSConstants.ONE);
			
			String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			if (ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(lsUserOrgType))
			{
				loCBGridBean.setModifyByProvider(lsUserId);
			}
			else
			{
				loCBGridBean.setModifyByAgency(lsUserId);
			}
			
			PortletSessionHandler.setAttribute(loCBGridBean, aoRequest, HHSConstants.CBGRIDBEAN_IN_SESSION);
			// This method will fetch the document list for this screen
			generateDocumentSection(aoRequest);
			
			// setting parameter for making screen readonly
			loChannelObj.setData(HHSConstants.ORG_TYPE, lsUserOrgType);
			loChannelObj.setData(HHSConstants.BUDGET_STATUS, loCBGridBean.getBudgetStatusId());
			Boolean loReadOnlyCondition = Boolean.valueOf((String) Rule.evaluateRule(
					HHSConstants.READ_ONLY_PAGE_ATTRIBUTE_RULE, loChannelObj));
			// Start Release 5 user notification
			loReadOnlyCondition = getProcurementReadOnly(aoRequest, lsUserOrgType, loReadOnlyCondition);
			// End Release 5 user notification
			aoRequest.getPortletSession().setAttribute(HHSConstants.CONTRACT_BUDGET_READ_ONLY,
					loReadOnlyCondition.toString(), PortletSession.APPLICATION_SCOPE);
			aoRequest.setAttribute(HHSConstants.CHANNEL_ACCESS, loChannelObj);
			
			loTaskDetailsBean.setAmendmentType(loCBGridBean.getAmendmentType());
			if (null == lsPrintRender)
			{
				loSession.setAttribute(HHSConstants.TASK_DETAIL_BEAN_SESSION, loTaskDetailsBean,
						PortletSession.APPLICATION_SCOPE);
			}
			// Get errors messages after Submit confirmation transactions
			String lsErrorMsg = (String) ApplicationSession.getAttribute(aoRequest, HHSConstants.ERROR_MESSAGE);
			aoRequest.setAttribute(HHSConstants.ERROR_MESSAGE, lsErrorMsg);
			if (null != lsActionReqParam && lsActionReqParam.equalsIgnoreCase(HHSConstants.TASK_BUDGET_AMENDMENT))
			{
				lsJspPath = HHSConstants.JSP_AMENDMENT_BUDGET_REVIEW_TASK;
			}
			if ((lsErrorMsg == null || HHSConstants.EMPTY_STRING.equalsIgnoreCase(lsErrorMsg))
					&& aoRequest.getParameter(HHSConstants.SUBMIT_OVERLAY_SUCCESS) != null)
			{
				aoRequest.setAttribute(HHSConstants.TRANSACTION_STATUS, HHSConstants.SUCCESS);
				aoRequest.setAttribute(HHSConstants.TRANSACTION_MESSAGE, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.MSG_SUCCESSFULL_AMENDMENTSUBMIT));
			}
			// R4 fetch EntryType for contractBudget
			aoRequest.setAttribute(HHSConstants.ENTRY_TYPE_ID, HHSUtil.getEntryTypeDetail(lsContractId, lsBudgetId,
					null, HHSConstants.TASK_BUDGET_AMENDMENT, lsFiscalYearId));
		}
		// Application Exception handled here
		catch (ApplicationException aoAppExp)
		{
			// Log is generated in case of any Error and Error message
			// Error message is set for JSP
			lsJspPath = HHSConstants.ERROR_PAGE_JSP;
			LOG_OBJECT
					.Error("Error in ContractBudgetAmendmentController while fetching Amendment Budget for sub budget id:"
							+ loCBGridBean.getSubBudgetID());
		}
		catch (Exception aoAppExp)
		{
			// Log is generated in case of any Error and Error message
			// Error message is set for JSP
			lsJspPath = HHSConstants.ERROR_PAGE_JSP;
			LOG_OBJECT
					.Error("Error in ContractBudgetAmendmentController while fetching Amendment Budget for sub budget id:"
							+ loCBGridBean.getSubBudgetID());
		}
		loModelAndView = new ModelAndView(lsJspPath);
		return loModelAndView;
	}
	
	/**
	 * <ul>
	 * <li>This method is triggered on clicking save button on Contract Budget
	 * Modification screens (Base/Amend/Modification/Update)</li>
	 * <li>Transaction Id : fetchAmendFyBudgetSummary</li>
	 * 
	 * <li>Service Class : ContractBudget</li>
	 * 
	 * <li>Validations on click of save button:-</li>
	 * <ol>
	 * <li>Visible and enabled for provider users if Invoice Status = “Pending
	 * Submission” or “Returned for Revision”</li>
	 * <li>Hidden for provider users if Invoice Status = “Pending Approval”,
	 * “Approved”, “Withdrawn” or “Suspended”</li>
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
	 * @param aoResourceRequest request param
	 * @param aoResourceResponse response param
	 * @return loModelAndView ModelAndView object
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked", "static-access" })
	@ResourceMapping("saveContractBudgetAmend")
	public ModelAndView saveContractBudgetAmend(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		ModelAndView loModelAndView = null;
		PrintWriter loOut = null;
		String lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
		try
		{
			aoResourceResponse.setContentType(HHSConstants.TEXT_HTML);
			Map<String, String> loMap = moContractBudgetControllerUtils.validateBudgetStatus(aoResourceRequest
					.getParameter(HHSConstants.BUDGET_ID));
			loOut = aoResourceResponse.getWriter();
			if (!loMap.containsKey(HHSConstants.SUCCESS_MESSAGE))
			{
				lsErrorMsg = loMap.get(HHSConstants.ERROR_MESSAGE);
			}
			else
			{
				String lsUserId = (String) aoResourceRequest.getPortletSession().getAttribute(
						ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
				String lsProviderComment = aoResourceRequest.getParameter(HHSConstants.PUBLIC_COMMENT_AREA);
				TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
				loTaskDetailsBean.setEntityId(aoResourceRequest.getParameter(HHSConstants.BUDGET_ID));
				loTaskDetailsBean.setEntityType(HHSConstants.AUDIT_CONTRACT_BUDGET_AMENDMENT);
				loTaskDetailsBean.setIsTaskScreen(false);
				loTaskDetailsBean.setProviderComment(lsProviderComment);
				loTaskDetailsBean.setUserId(lsUserId);
				saveCommentNonAudit(loTaskDetailsBean);
				Channel loChannel = new Channel();
				Map loInputMap = new HashMap();
				loInputMap.put(HHSConstants.CONTRACT_ID_WORKFLOW,
						aoResourceRequest.getParameter(HHSConstants.CONTRACT_ID));
				loInputMap.put(HHSConstants.BUDGET_ID_WORKFLOW, aoResourceRequest.getParameter(HHSConstants.BUDGET_ID));
				loChannel.setData(HHSConstants.AO_HASH_MAP, loInputMap);
				loChannel.setData(HHSConstants.MASTERBEAN_OBJ, null);
				loChannel.setData(HHSConstants.LO_CB_GRID_BEAN, null);
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_AMEND_FY_BUDGET_SUMMARY);
				BudgetDetails loBudgetDetails = (BudgetDetails) loChannel.getData(HHSConstants.LO_BUDGET_DETAILS);
				aoResourceRequest.setAttribute(HHSConstants.FISCAL_BUDGET_INFO, loBudgetDetails);
				lsErrorMsg = HHSConstants.EMPTY_STRING;
				loModelAndView = new ModelAndView(HHSConstants.JSP_PATH_AMEND_FY_BUDGET);
			}
		}
		// Application Exception handled here
		catch (ApplicationException aoExp)
		{
			// Log is generated in case of any Error and Error message
			LOG_OBJECT.Error("Application Exception in saveContractBudgetModification method", aoExp);
		}
		catch (Exception aoExp)
		{
			// Log is generated in case of any Error and Error message
			LOG_OBJECT.Error("Exception in saveContractBudgetModification method", aoExp);
		}
		finally
		{
			if (null != loOut)
			{
				// added in R5
				if (!HHSR5Constants.EMPTY_STRING.equalsIgnoreCase(lsErrorMsg))
				{
					loOut.print(lsErrorMsg);
				}
				// R5 change Ends
				loOut.flush();
				loOut.close();
			}
		}
		return loModelAndView;
	}
	
	/**
	 * This method write error message on jsp in JSON format
	 * 
	 * @param aoResourceResponse Resource Response object
	 * @param lsErrorMsg Error message
	 * @param liErrorCode Error code
	 */
	private void setErrorMsgJSPForAmendement(ResourceResponse aoResourceResponse, String lsErrorMsg, int liErrorCode)
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
	// Start: Updated in R6
	/**
	 * This method is used to display the Submit Contract Amendement
	 * Confirmation Overlay
	 * <ul>
	 * <li>1.Get the required view name from request</li>
	 * <li>Release 3.6.0 Enhancement id 6484</li>
	 * <li>Updated in R6</li>
	 * <ul>
	 * 
	 * @param aoResourceRequest Resource Request Object
	 * @param aoResourceResponse Resource Response Object
	 * @return ModelAndView Overlay JSP
	 * @throws Exception an Exception
	 */
	@ResourceMapping("submitContractBudgetAmendOverlay")
	public ModelAndView submitContractBudgetAmendOverlay(ResourceRequest aoResourceRequest,
			ResourceResponse aoResourceResponse)
	{
		ModelAndView loModelAndView = null;
		PrintWriter loOut = null;
		Map<String, String> loMap = null;
		String lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
		int liErrorCode = HHSConstants.INT_ZERO;
		try
		{
			aoResourceResponse.setContentType(HHSConstants.TEXT_HTML);
			loMap = ContractBudgetControllerUtils.validateBudgetStatus(aoResourceRequest
					.getParameter(HHSConstants.BUDGET_ID));
			// Release 3.6.0 Enhancement id 6484
			ContractBudgetControllerUtils.validateSubBudgetSite(aoResourceRequest.getParameter(HHSConstants.BUDGET_ID),
					loMap, aoResourceRequest.getParameter(HHSConstants.FISCAL_YEAR_ID));
			loOut = aoResourceResponse.getWriter();
			if (loMap.containsKey(HHSConstants.SUCCESS_MESSAGE))
			{
				// Validate total modificationAmount should be equal to Zero
				Channel loChannel = new Channel();
				loChannel.setData(HHSConstants.BUDGET_ID_KEY, aoResourceRequest.getParameter(HHSConstants.BUDGET_ID));
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.CB_VALIDATE_SUBMIT_AMOUNT_TOTAL);
				Boolean loValid = (Boolean) loChannel.getData(HHSConstants.AB_VALIDATE_STATUS);
				// R6 Change starts
				Boolean lbPendingBudgetFlag = (Boolean) loChannel.getData(HHSConstants.VAL_STATUS_FLAG);
				// R6 Change ends
				//Start: Added in R7 for Cost-Center
				HashMap loServiceValid = (HashMap) loChannel.getData(HHSConstants.AO_HASH_MAP);
				String lsIsServicesSuccess =(String) loServiceValid.get(HHSConstants.SUCCESS);
				//End: Added in R7 for Cost-Center
				// Following condition updated in R7 for Cost-Center
				if (loValid.booleanValue() && lbPendingBudgetFlag && Boolean.parseBoolean(lsIsServicesSuccess))
				{
					liErrorCode = HHSConstants.INT_THREE;
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
					// R6 Change starts
					if(!lbPendingBudgetFlag)
					{
						liErrorCode = HHSConstants.INT_ONE;
						lsErrorMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
								HHSR5Constants.MSG_PENDING_BUDGET_AMENDERR);
					}
					// R6 Change ends
					else
					{
						//Start:Updated in R7 for Cost-Center
						if (!loValid.booleanValue())
						{
							liErrorCode = HHSConstants.INT_TWO;
							lsErrorMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
									HHSConstants.MSG_KEY_SUBMIT_BUDGET_AMEND_ERR);
						}
						else
						{
							liErrorCode = HHSConstants.INT_ONE;
							lsErrorMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
									(String) loServiceValid.get(HHSConstants.CBL_MESSAGE));
						}
						//End:Updated in R7 for Cost-Center
						
					}
					setErrorMsgJSPForAmendement(aoResourceResponse, lsErrorMsg, liErrorCode);
				}
			}
			else
			{
				// set error message
				setErrorMsgJSPForAmendement(aoResourceResponse, loMap.get(HHSConstants.ERROR_MESSAGE),
						HHSConstants.INT_ONE);
			}
		}
		// Application Exception handled here
		catch (ApplicationException aoExp)
		{
			// Log is generated in case of any Error and Error message
			// Error message is set for JSP
			liErrorCode = HHSConstants.INT_TWO;
			setErrorMsgJSPForAmendement(aoResourceResponse, lsErrorMsg, liErrorCode);
			LOG_OBJECT.Error("Application Exception in submitContractBudgetAmendOverlay method", aoExp);
		}
		catch (Exception aoExp)
		{
			// Log is generated in case of any Error and Error message
			// Error message is set for JSP
			liErrorCode = HHSConstants.INT_TWO;
			setErrorMsgJSPForAmendement(aoResourceResponse, lsErrorMsg, liErrorCode);
			LOG_OBJECT.Error("Exception in submitContractBudgetAmendOverlay method", aoExp);
		}
		finally
		{
			if (null != loOut)
			{
				loOut.flush();
				loOut.close();
			}
		}
		return loModelAndView;
	}
	// End: Updated in R6
	
	/**
	 * This method is used to check --> Review levels set for the Agency in
	 * order to launch WF312 - Contract Budget Amendment After which WF312 -
	 * Contract Budget Amendment is launched and Budget status is changed to
	 * 'Pending Approval'
	 * <ul>
	 * <li>Execute transaction id <b>launchWFContractAmendment</b></li>
	 * </ul>
	 * <br>
	 * Method updated for R4.
	 * 
	 * @param aoActRequest Action request object
	 * @param aoActResponse Action response object
	 * @throws ApplicationException an ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes", "static-access" })
	@ActionMapping(params = "submit_action=submitAmendConfirmation")
	public void submitAmendConfirmationOverlay(ActionRequest aoActRequest, ActionResponse aoActResponse)
	{
		Channel loChannel = new Channel();
		String lsContractId = null;
		String lsBudgetId = null;
		HashMap loHmRequiredProps = new HashMap();
		// R4: Tab Level Comments
		Map<String, String> loTabLevelCommentsMap = null;
		try
		{
			PortletSession loSession = aoActRequest.getPortletSession();
			String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);
			P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			lsContractId = aoActRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW);
			lsBudgetId = aoActRequest.getParameter(HHSConstants.BUDGET_ID_WORKFLOW);
			// Fetch budget AMENDMENT status for launching the Contract
			// Budget AMENDMENT Task
			// and changing the status
			String lsBudgetStatus = moContractBudgetControllerUtils.fetchCurrentBudgetStatus(lsBudgetId);
			
			// Map to launch WF315 - Contract Budget Amendment
			loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
			loHmRequiredProps.put(HHSConstants.CONTRACT_ID_WORKFLOW, lsContractId);
			loHmRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW, lsBudgetId);
			loHmRequiredProps.put(HHSConstants.SUBMITTED_BY, lsUserId);
			loHmRequiredProps.put(HHSConstants.MOD_BY_USER_ID, HHSConstants.EMPTY_STRING);
			loHmRequiredProps.put(HHSConstants.AUDIT_PROVIDER_INITIATED_TASK, true);
			loHmRequiredProps.put(HHSConstants.WORKFLOW_NAME, HHSConstants.WF_CONTRACT_BUDGET_AMENDMENT);
			String lsProviderComment = aoActRequest.getParameter(HHSConstants.PUBLIC_COMMENT_AREA);
			// Set Audit Data in channel to be saved in History
			setAuditDataInChannel(loChannel, lsBudgetId, lsUserId, lsProviderComment, lsBudgetStatus,
					HHSConstants.STATUS_CHANGED_FROM, HHSConstants.AUDIT_CONTRACT_BUDGET_AMENDMENT);
			loHmRequiredProps.put(HHSConstants.AS_STATUS_ID,
					HHSUtil.getStatusID(HHSConstants.BUDGETLIST_BUDGET, HHSConstants.STATUS_PENDING_APPROVAL));
			loChannel.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
			loChannel.setData(HHSConstants.LB_AUTH_STATUS_FLAG, true);
			loChannel.setData(HHSConstants.AO_HMWF_REQUIRED_PROPS, loHmRequiredProps);
			// R4: Setting TabLevelComments Map in Channel
			loChannel.setData(HHSConstants.CHANNEL_PARAM_TAB_LEVEL_COMMENTS_MAP_PROVIDER, loTabLevelCommentsMap);
			// Start R5 : set EntityId and EntityName for AutoSave
			CommonUtil.setChannelForAutoSaveData(loChannel, lsBudgetId, HHSConstants.AUDIT_CONTRACT_BUDGET_AMENDMENT);
			// End R5 : set EntityId and EntityName for AutoSave
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.LAUNCH_WF_CONTRACT_AMENDMENT);
			
		}
		/**
		 * Any Exception occurs while executing transaction will be catch as
		 * Application Exception here and If exception object contains defined
		 * error message it will set in session to display on jsp page else by
		 * default error message is shown on page
		 */
		catch (ApplicationException aoAppExe)
		{
			String lsErrorMessage = null;
			ApplicationException loAppEx = (ApplicationException) aoAppExe.getRootCause();
			if (null != loAppEx)
			{
				lsErrorMessage = (String) loAppEx.getContextData().get(HHSConstants.LEVEL_ERROR_MESSAGE);
			}
			if (null == lsErrorMessage)
			{
				lsErrorMessage = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			}
			ApplicationSession.setAttribute(lsErrorMessage, aoActRequest, HHSConstants.ERROR_MESSAGE);
			LOG_OBJECT.Error("Exception occured in submitConfirmationOverlay", aoAppExe);
		}
		/**
		 * Any non application Exception occurs catches here and default error
		 * message is shown on page
		 */
		catch (Exception aoExe)
		{
			ApplicationSession.setAttribute(HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED, aoActRequest,
					HHSConstants.ERROR_MESSAGE);
			LOG_OBJECT.Error("Exception occured in submitConfirmationOverlay  ", aoExe);
		}
		aoActResponse.setRenderParameter(HHSConstants.CONTRACT_ID_WORKFLOW, lsContractId);
		aoActResponse.setRenderParameter(HHSConstants.BUDGET_ID_WORKFLOW, lsBudgetId);
		aoActResponse.setRenderParameter(HHSConstants.BUDGET_TYPE, HHSConstants.BUDGET_TYPE4);
		aoActResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.CONTRACT_BUDGET_HANDLER);
		aoActResponse.setRenderParameter(HHSConstants.SUBMIT_OVERLAY_SUCCESS, HHSConstants.SUBMIT_OVERLAY_SUCCESS);
	}
	
	/**
	 * This method is used to validate the User log in on Submit button from S -
	 * 313 Contract Budget screen
	 * @param aoResourceRequest Resource request object
	 * @param aoResourceResponse Resource response object
	 * @throws ApplicationException an applicationException
	 */
	@SuppressWarnings("rawtypes")
	@ResourceMapping("validateUser")
	public void validateUserCB(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws ApplicationException
	{
		Map loHmValidateMap = null;
		Boolean loUserValidation = false;
		String lsErrorMessage = HHSConstants.EMPTY_STRING;
		int liErrorCode = HHSConstants.INT_ZERO;
		try
		{
			String lsUserEmailId = aoResourceRequest.getParameter(HHSConstants.KEY_SESSION_USER_NAME);
			String lsPassword = aoResourceRequest.getParameter(HHSConstants.PASSWORD);
			loHmValidateMap = validateUser(lsUserEmailId, lsPassword, aoResourceRequest);
			loUserValidation = (Boolean) loHmValidateMap.get(HHSConstants.IS_VALID_USER);
			if (!loUserValidation)
			{
				// Validation error
				lsErrorMessage = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.CB_VALIDATE_ERROR);
				liErrorCode = HHSConstants.INT_ONE;
				setErrorMsgJSPForAmendement(aoResourceResponse, lsErrorMessage, liErrorCode);
			}
		}
		// Application Exception handled here
		catch (ApplicationException aoExe)
		{
			LOG_OBJECT.Error("Exception occured in submitConfirmationOverlay", aoExe);
			// Application Exception occurred
			lsErrorMessage = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			liErrorCode = HHSConstants.INT_TWO;
			setErrorMsgJSPForAmendement(aoResourceResponse, lsErrorMessage, liErrorCode);
		}
	}
	
	/**
	 * This method set all audit data in channel object required for Audit
	 * purpose
	 * <ul>
	 * <li>Set audit data for Status changed for Budget</li>
	 * <li>Set audit data for provider comments entered by provider</li>
	 * </ul>
	 * @param aoChannel Channel object
	 * @param asBudgetId Budget Id
	 * @param asUserId User Id
	 * @param asComment Comments
	 * @param asStatus Budget Status
	 * @param asStatusChange Changed Budget Status
	 * @param asEntityType Entity Type
	 * @throws ApplicationException an applicationException
	 */
	private void setAuditDataInChannel(Channel aoChannel, String asBudgetId, String asUserId, String asComment,
			String asStatus, String asStatusChange, String asEntityType) throws ApplicationException
	{
		List<HhsAuditBean> loAuditList = new ArrayList<HhsAuditBean>();
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		String lsPrevStatus = HHSUtil.getStatusName(HHSConstants.BUDGETLIST_BUDGET, Integer.valueOf(asStatus));
		StringBuilder loSBStatusChange = new StringBuilder();
		loSBStatusChange.append(asStatusChange);
		loSBStatusChange.append(HHSConstants.SPACE);
		loSBStatusChange.append(HHSConstants.STR);
		loSBStatusChange.append(lsPrevStatus);
		loSBStatusChange.append(HHSConstants.STR);
		loSBStatusChange.append(HHSConstants._TO_);
		loSBStatusChange.append(HHSConstants.STR + HHSConstants.STATUS_PENDING_APPROVAL + HHSConstants.STR);
		if (null != asComment && !asComment.isEmpty())
		{
			loAuditList.add(HHSUtil.addAuditDataToChannel(P8Constants.PROPERTY_PE_TH_PROVIDER_COMMENT,
					P8Constants.PROPERTY_PE_TH_PROVIDER_COMMENT, asComment, asEntityType, asBudgetId, asUserId,
					HHSConstants.PROVIDER_AUDIT));
		}
		loAuditList.add(HHSUtil.addAuditDataToChannel(HHSConstants.STATUS_CHANGE, HHSConstants.STATUS_CHANGE,
				loSBStatusChange.toString(), asEntityType, asBudgetId, asUserId, HHSConstants.PROVIDER_AUDIT));
		loHhsAuditBean.setEntityId(asBudgetId);
		loHhsAuditBean.setEntityType(asEntityType);
		aoChannel.setData(HHSConstants.LO_AUDIT_LIST, loAuditList);
		aoChannel.setData(HHSConstants.AUDIT_BEAN, loHhsAuditBean);
	}
	
}
