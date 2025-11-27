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
 * All the actions on contract budget screens will be handled by this controller
 * only.
 * 
 * The method submitConfirmationOverlay is updated in Release 7.
 * </p>
 */
@Controller(value = "contractBudgetHandler")
@RequestMapping("view")
public class ContractBudgetModificationController extends BaseController implements ResourceAwareController
{
	/**
	 * Log object to record all logs
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(ContractBudgetModificationController.class);
	
	/**
	 * object for ContractBudgetControllerUtils class
	 */
	ContractBudgetControllerUtils moContractBudgetControllerUtils = new ContractBudgetControllerUtils();
	
	/**
	 * This method will be executed when budget summary screen will be loaded
	 * for first time and it will call service to fetch all details for budget
	 * summary screen.
	 * 
	 * <ul>
	 * <li>Transaction manager will be called to call the fetchBudgetSummary
	 * method of ContractBudgetService class which will fetch all details for
	 * budget summary screen.</li>
	 * </ul>
	 * <br>
	 * This method was updated in R4.
	 * 
	 * @param aoRequest an Action Request object
	 * @param aoResponse an Action Response object
	 * @return ModelAndView containing details of the page to be displayed to
	 *         the end user
	 * @throws ApplicationException an application exception
	 */
	@SuppressWarnings(
	{ "static-access" })
	@RenderMapping
	protected ModelAndView handleRenderRequestInternal(RenderRequest aoRequest, RenderResponse aoResponse)
			throws ApplicationException
	{
		ModelAndView loModelAndView = null;
		Channel loChannelObj = new Channel();
		String lsJspPath = HHSConstants.JSP_CONTRACTBUDGET_CONTRACT_BUDGET_MODIFICATION_LANDING;
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsContractId = PortalUtil.parseQueryString(aoRequest, HHSConstants.CONTRACT_ID_WORKFLOW);
		String lsCtId = PortalUtil.parseQueryString(aoRequest, HHSConstants.CT_ID);
		String lsBudgetId = PortalUtil.parseQueryString(aoRequest, HHSConstants.BUDGET_ID_WORKFLOW);
		String lsBudgetType = PortalUtil.parseQueryString(aoRequest, HHSConstants.BUDGET_TYPE);
		String lsFiscalYearId = PortalUtil.parseQueryString(aoRequest, HHSConstants.CLC_FISCAL_YEAR_ID);
		String lsActionReqParam = PortalUtil.parseQueryString(aoRequest, HHSConstants.RENDER_ACTION);
		PortletSession loSession = aoRequest.getPortletSession();
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		String lsWorkflowId = PortalUtil.parseQueryString(aoRequest, HHSConstants.WORKFLOW_ID);
		CBGridBean loCBGridBean = new CBGridBean();
		try
		{
			if (null != lsWorkflowId)
			{
				loTaskDetailsBean = fetchTaskDetailsFromFilenet(aoRequest, lsWorkflowId);
				lsContractId = loTaskDetailsBean.getContractId();
				lsBudgetId = loTaskDetailsBean.getBudgetId();
			}
			loTaskDetailsBean.setBudgetId(lsBudgetId);
			loTaskDetailsBean.setContractId(lsContractId);
			HashMap<String, String> loHashMap = new HashMap<String, String>();
			loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, lsContractId);
			loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, lsBudgetId);
			loHashMap.put(HHSConstants.USER_ID_2, lsUserId);
			loHashMap.put(HHSConstants.CT_ID, lsCtId);
			setBudgetType(lsBudgetType, lsActionReqParam, loHashMap);
			loHashMap.put(HHSConstants.FISCAL_YEAR_ID, lsFiscalYearId);
			aoRequest.setAttribute(HHSConstants.AO_HASH_MAP, loHashMap);
			loChannelObj.setData(HHSConstants.AO_HASH_MAP, loHashMap);
			// R7 changes ::: Approved modification 
			MasterBean loMasterBean = null;
			P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
								ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			loMasterBean = BaseControllerUtil.generateMasterBean(lsBudgetId, loUserSession);
			loChannelObj.setData(HHSConstants.MASTERBEAN_OBJ, loMasterBean);
			// R7 changes end
			loCBGridBean = getJspLandingData(loChannelObj, aoRequest, loCBGridBean);
			lsJspPath = getJspPath(lsJspPath, lsBudgetType, lsActionReqParam, loCBGridBean);
			// R4 fetch EntryType for contractBudget
			aoRequest.setAttribute(HHSConstants.ENTRY_TYPE_ID,
					setEntryType(lsContractId, lsBudgetId, lsJspPath, lsFiscalYearId));
			String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			if (null != loCBGridBean)
			{
				if (ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(lsUserOrgType))
				{
					loCBGridBean.setModifyByProvider(lsUserId);
				}
				else
				{
					loCBGridBean.setModifyByAgency(lsUserId);
				}
			}
			
			// setting parameter for making screen readonly
			loChannelObj.setData(HHSConstants.ORG_TYPE, lsUserOrgType);
			if (null != loCBGridBean && null != loCBGridBean.getBudgetStatusId())
			{
				loChannelObj.setData(HHSConstants.BUDGET_STATUS, loCBGridBean.getBudgetStatusId());
			}
			Boolean loReadOnlyCondition = Boolean.valueOf((String) Rule.evaluateRule(
					HHSConstants.READ_ONLY_PAGE_ATTRIBUTE_RULE, loChannelObj));
			// Release 5
			// Start Release 5 user notification
			loReadOnlyCondition = getProcurementReadOnly(aoRequest, lsUserOrgType, loReadOnlyCondition);
			// End Release 5 user notification
			aoRequest.getPortletSession().setAttribute(HHSConstants.CONTRACT_BUDGET_READ_ONLY,
					loReadOnlyCondition.toString(), PortletSession.APPLICATION_SCOPE);
			aoRequest.setAttribute(HHSConstants.CHANNEL_ACCESS, loChannelObj);
			
			// This method will fetch the document list for this screen
			generateDocumentSection(aoRequest);
			// Get errors messages after Submit confirmation transactions
			String lsErrorMsg = (String) ApplicationSession.getAttribute(aoRequest, HHSConstants.ERROR_MESSAGE);
			aoRequest.setAttribute(HHSConstants.ERROR_MESSAGE, lsErrorMsg);
			loSession.setAttribute(HHSConstants.TASK_DETAIL_BEAN_SESSION, loTaskDetailsBean,
					PortletSession.APPLICATION_SCOPE);
			if ((lsErrorMsg == null || HHSConstants.EMPTY_STRING.equalsIgnoreCase(lsErrorMsg))
					&& aoRequest.getParameter(HHSConstants.SUBMIT_OVERLAY_SUCCESS) != null)
			{
				aoRequest.setAttribute(HHSConstants.TRANSACTION_STATUS, HHSConstants.SUCCESS);
				if (null != loCBGridBean && null != loCBGridBean.getBudgetTypeId())
				{
					if (loCBGridBean.getBudgetTypeId().equals(HHSConstants.THREE))
					{
						aoRequest.setAttribute(HHSConstants.TRANSACTION_MESSAGE, PropertyLoader.getProperty(
								HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
								HHSConstants.MSG_SUCCESSFULL_MODIFICATIONSUBMIT));
					}
					else if (loCBGridBean.getBudgetTypeId().equals(HHSConstants.FOUR))
					{
						aoRequest.setAttribute(HHSConstants.TRANSACTION_MESSAGE, PropertyLoader.getProperty(
								HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.MSG_SUCCESSFULL_UPDATESUBMIT));
					}
				}
			}
		}
		// Application Exception handled here
		catch (ApplicationException aoAppExp)
		{
			// Log is generated in case of any Error and Error message
			// Error message is set for JSP
			lsJspPath = getErrorJspPath(aoAppExp, loSession, loCBGridBean);
		}
		catch (Exception aoAppExp)
		{
			// Log is generated in case of any Error and Error message
			// Error message is set for JSP
			lsJspPath = getErrorJspPath(
					new ApplicationException("Exception in Contract Budget Modification", aoAppExp), loSession,
					loCBGridBean);
		}
		loModelAndView = new ModelAndView(lsJspPath);
		return loModelAndView;
	}
	
	/**
	 * This method will be return the entryTypeList on the basis of
	 * update/modification contractBudget <li>This method was added in R4.</li>
	 * @param lsContractId String
	 * @param lsBudgetId String
	 * @param lsJspPath String
	 * @return loEntryList List<String>
	 * @throws ApplicationException Application Exception
	 */
	private List<String> setEntryType(String lsContractId, String lsBudgetId, String lsJspPath, String lsFiscalYearId)
			throws ApplicationException
	{
		List<String> loEntryList = null;
		if (lsJspPath.equals(HHSConstants.UPDATE_LANDING_JSP))
		{
			loEntryList = HHSUtil.getEntryTypeDetail(lsContractId, lsBudgetId, null, HHSConstants.UPDATE_LANDING_JSP,
					lsFiscalYearId);
		}
		else if (lsJspPath.equals(HHSConstants.UPDATE_REVIEW_TASK_JSP))
		{
			loEntryList = HHSUtil.getEntryTypeDetail(lsContractId, lsBudgetId, null,
					HHSConstants.UPDATE_REVIEW_TASK_JSP, lsFiscalYearId);
		}
		else if (lsJspPath.equals(HHSConstants.JSP_CONTRACTBUDGET_CONTRACT_BUDGET_MODIFICATION_LANDING)
				|| lsJspPath.equals(HHSConstants.MODIFICATION_REVIEW_TASK_JSP))
		{
			loEntryList = HHSUtil.getEntryTypeDetail(lsContractId, lsBudgetId, null,
					HHSConstants.AUDIT_CONTRACT_BUDGET_MODIFICATION, lsFiscalYearId);
		}
		return loEntryList;
	}
	
	/**
	 * This method will be return the error jsp page provide the logger error,
	 * and set error message
	 * 
	 * @param aoAppExp exception object
	 * @param aoSession PortletSession object
	 * @param aoCBGridBean CBGridBean object
	 * @return lsJspPath error jsp page
	 */
	private String getErrorJspPath(ApplicationException aoAppExp, PortletSession aoSession, CBGridBean aoCBGridBean)
	{
		LOG_OBJECT
				.Error("Error in ContractBudgetModificationController while fetching Modification Budget for sub budget id:"
						+ aoCBGridBean.getSubBudgetID());
		String lsJspPath = HHSConstants.ERROR_PAGE_JSP;
		return lsJspPath;
	}
	
	/**
	 * This method will be get the data from DB for jsp's contract Information,
	 * Fiscal Year Information, SubBudgetList and grid's session <li>The
	 * transaction used: getContractBudgetModification</li>
	 * @param aoChannelObj channel object
	 * @param aoRequest request object
	 * @param loCBGridBean CBGridBean object
	 * @throws ApplicationException - ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	private CBGridBean getJspLandingData(Channel aoChannelObj, RenderRequest aoRequest, CBGridBean loCBGridBean)
			throws ApplicationException
	{
		HHSTransactionManager.executeTransaction(aoChannelObj, HHSConstants.GET_CONTRACT_BUDGET_MODIFICATION);
		ContractList loContractList = (ContractList) aoChannelObj.getData(HHSConstants.LO_CONTRACT_LIST);
		aoRequest.setAttribute(HHSConstants.CONTRACT_INFO, loContractList);
		BudgetDetails loBudgetDetails = (BudgetDetails) aoChannelObj.getData(HHSConstants.LO_BUDGET_DETAILS);
		// R7 Changes
		Map<String, String> loInputMap = (HashMap<String, String>) aoChannelObj.getData(HHSConstants.AO_HASH_MAP);
		String lsModificationUrlStatus = fetchModificationBudgetCount(loInputMap.get(HHSConstants.BUDGET_ID_WORKFLOW));
		if (null != lsModificationUrlStatus && Integer.parseInt(lsModificationUrlStatus) > 0)
		{
			loBudgetDetails.setBudgetModification(HHSR5Constants.TRUE);
		}
		// R7 Changes
		aoRequest.setAttribute(HHSConstants.FISCAL_BUDGET_INFO, loBudgetDetails);
		List<CBGridBean> loSubBudgetList = (List<CBGridBean>) aoChannelObj.getData(HHSConstants.SUB_BUDGET_LIST);
		HHSUtil.changeSubBudgetNameForHTMLView(loSubBudgetList);
		aoRequest.getPortletSession().setAttribute(HHSConstants.BUDGET_ACCORDIAN_DATA, loSubBudgetList,
				PortletSession.APPLICATION_SCOPE);
		loCBGridBean = (CBGridBean) aoChannelObj.getData(HHSConstants.LO_CB_GRID_BEAN);
		PortletSessionHandler.setAttribute(loCBGridBean, aoRequest, HHSConstants.CBGRIDBEAN_IN_SESSION);
		return loCBGridBean;
	}
	
	/**
	 * This method will set the budgetType in hashMap based on query partameter
	 * 
	 * @param asBudgetType budget Type
	 * @param asActionReqParam action paramater
	 * @param aoHashMap hashMap
	 */
	private void setBudgetType(String asBudgetType, String asActionReqParam, HashMap<String, String> aoHashMap)
	{
		if (null != asBudgetType && asBudgetType.equalsIgnoreCase(HHSConstants.BUDGET_TYPE2))
		{
			aoHashMap.put(HHSConstants.BUDGET_TYPE, HHSConstants.THREE);
		}
		else if (null != asBudgetType && asBudgetType.equalsIgnoreCase(HHSConstants.BUDGET_TYPE4))
		{
			aoHashMap.put(HHSConstants.BUDGET_TYPE, HHSConstants.FOUR);
		}
		else if (null != asActionReqParam && asActionReqParam.equalsIgnoreCase((HHSConstants.TASK_BUDGET_MODIFICATION)))
		{
			aoHashMap.put(HHSConstants.BUDGET_TYPE, HHSConstants.THREE);
		}
		else if (null != asActionReqParam && asActionReqParam.equalsIgnoreCase((HHSConstants.TASK_BUDGET_UPDATE)))
		{
			aoHashMap.put(HHSConstants.BUDGET_TYPE, HHSConstants.FOUR);
		}
	}
	
	/**
	 * This will return the corresponding jsp based on the different conditions
	 * 
	 * @param asJspPath jsp path
	 * @param asBudgetType budget Type
	 * @param asActionReqParam action paramater
	 * @param aoCBGridBean CBGridBean object
	 * @throws ApplicationException an ApplicationException
	 * @return asJspPath
	 */
	private String getJspPath(String asJspPath, String asBudgetType, String asActionReqParam, CBGridBean aoCBGridBean)
			throws ApplicationException
	{
		if (null != asBudgetType && asBudgetType.equalsIgnoreCase(HHSConstants.BUDGET_TYPE2))
		{
			aoCBGridBean.setBudgetTypeId(HHSConstants.THREE);
		}
		else if (null != asBudgetType && asBudgetType.equalsIgnoreCase(HHSConstants.BUDGET_TYPE4))
		{
			aoCBGridBean.setBudgetTypeId(HHSConstants.FOUR);
			asJspPath = HHSConstants.UPDATE_LANDING_JSP;
		}
		else if (null != asActionReqParam && asActionReqParam.equalsIgnoreCase((HHSConstants.TASK_BUDGET_MODIFICATION)))
		{
			aoCBGridBean.setBudgetTypeId(HHSConstants.THREE);
			asJspPath = HHSConstants.MODIFICATION_REVIEW_TASK_JSP;
		}
		else if (null != asActionReqParam && asActionReqParam.equalsIgnoreCase((HHSConstants.TASK_BUDGET_UPDATE)))
		{
			aoCBGridBean.setBudgetTypeId(HHSConstants.FOUR);
			asJspPath = HHSConstants.UPDATE_REVIEW_TASK_JSP;
		}
		return asJspPath;
	}
	
	/**
	 * This method is used to display the Submit Contract Modification
	 * Confirmation Overlay
	 * <ul>
	 * <li>1.Get the required view name from request</li>
	 * <li>The transaction used: validateCBMAmountTotal</li>
	 * <li>Release 3.6.0 Enhancement id 6484</li>
	 * <ul>
	 * 
	 * @param aoResourceRequest Resource Request Object
	 * @param aoResResponse Resource Response Object
	 * @return ModelAndView Overlay JSP
	 * @throws Exception an Exception
	 */
	@ResourceMapping("submitContractBudgetModification")
	public ModelAndView submitContractBudgetOverlay(ResourceRequest aoResourceRequest, ResourceResponse aoResResponse)
			throws Exception
	{
		ModelAndView loModelAndView = null;
		String lsJspPath = null;
		PrintWriter loOut = null;
		Map<String, String> loMap = null;
		String lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
		int liErrorCode = HHSConstants.INT_ZERO;
		String lsJspName = aoResourceRequest.getParameter(HHSConstants.JSP_NAME);
		try
		{
			aoResResponse.setContentType(HHSConstants.TEXT_HTML);
			loMap = ContractBudgetControllerUtils.validateBudgetStatus(aoResourceRequest
					.getParameter(HHSConstants.BUDGET_ID));
			// Release 3.6.0 Enhancement id 6484
			loMap = ContractBudgetControllerUtils.validateSubBudgetSite(
					aoResourceRequest.getParameter(HHSConstants.BUDGET_ID), loMap,
					aoResourceRequest.getParameter(HHSConstants.FISCAL_YEAR_ID));
			
			loOut = aoResResponse.getWriter();
			if (loMap.containsKey(HHSConstants.SUCCESS_MESSAGE))
			{
				
				if (lsJspName.equalsIgnoreCase(HHSConstants.SUBMIT_CBM_CONFIRMATION))
				{
					// Validate total modificationAmount should be equal to Zero
					Channel loChannel = new Channel();
					loChannel.setData(HHSConstants.BUDGET_ID_KEY,
							aoResourceRequest.getParameter(HHSConstants.BUDGET_ID));
					HHSTransactionManager.executeTransaction(loChannel, HHSConstants.CBM_VALIDATE_SUBMIT_AMOUNT_TOTAL);
					Boolean loValid = (Boolean) loChannel.getData(HHSConstants.AB_VALIDATE_STATUS);
					//Start: Added in Release 7
                    HashMap loServiceValid = (HashMap) loChannel.getData(HHSConstants.AO_HASH_MAP);
                    String lsIsServicesSuccess =(String) loServiceValid.get(HHSConstants.SUCCESS);
                    //End: Added in Release 7
                    // Following condition updated in Release 7
					if (loValid.booleanValue() && Boolean.parseBoolean(lsIsServicesSuccess))
					{
						liErrorCode = HHSConstants.INT_THREE;
						// redirect to overlay
						aoResourceRequest.setAttribute(HHSConstants.BUDGET_ID_KEY,
								aoResourceRequest.getParameter(HHSConstants.BUDGET_ID));
						aoResourceRequest.setAttribute(HHSConstants.AGENCY_ID,
								aoResourceRequest.getParameter(HHSConstants.AGENCY_ID1));
						aoResourceRequest.setAttribute(HHSConstants.CONTRACT_ID_KEY,
								aoResourceRequest.getParameter(HHSConstants.CONTRACT_ID));
						lsJspPath = HHSConstants.JSP_CONTRACTBUDGET + lsJspName;
						loModelAndView = new ModelAndView(lsJspPath);
					}
					else
					{
						if (!loValid.booleanValue()){
							setErrorMsgForJSPForContractBudget(aoResResponse, PropertyLoader.getProperty(
									HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.MSG_KEY_SUBMIT_BUDGET_ERR),
									HHSConstants.INT_ONE);
						}
						else
						{
							setErrorMsgForJSPForContractBudget(aoResResponse, PropertyLoader.getProperty(
									HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
									(String) loServiceValid.get(HHSConstants.CBL_MESSAGE)), HHSConstants.INT_ONE);
						}
					}
				}
				else
				{
					lsJspPath = HHSConstants.JSP_CONTRACTBUDGET + lsJspName;
					loModelAndView = new ModelAndView(lsJspPath);
				}
			}
			else
			{
				// set error message
				setErrorMsgForJSPForContractBudget(aoResResponse, loMap.get(HHSConstants.ERROR_MESSAGE),
						HHSConstants.INT_ONE);
			}
		}
		// Application Exception handled here
		catch (ApplicationException aoExp)
		{
			// Log is generated in case of any Error and Error message
			// Error message is set for JSP
			liErrorCode = HHSConstants.INT_TWO;
			setErrorMsgForJSPForContractBudget(aoResResponse, lsErrorMsg, liErrorCode);
			LOG_OBJECT.Error("Application Exception in submitContractBudgetOverlay method", aoExp);
		}
		catch (Exception aoExp)
		{
			// Log is generated in case of any Error and Error message
			// Error message is set for JSP
			liErrorCode = HHSConstants.INT_TWO;
			setErrorMsgForJSPForContractBudget(aoResResponse, lsErrorMsg, liErrorCode);
			LOG_OBJECT.Error("Exception in submitContractBudgetOverlay method", aoExp);
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
	
	/**
	 * This method is used to check --> Review levels set for the Agency in
	 * order to launch WF312 - Contract Budget Modification After which WF312 -
	 * Contract Budget Modification is launched and Budget status is changed to
	 * 'Pending Approval' <li>The transaction used: launchWFContractModification
	 * </li> This method is updated in R4
	 * @param aoActionRequest Action request object
	 * @param aoActionResponse Action response object
	 * @throws ApplicationException an applicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "static-access", "rawtypes" })
	@ActionMapping(params = "submit_action=submitConfirmation")
	public void submitConfirmationOverlay(ActionRequest aoActionRequest, ActionResponse aoActionResponse)
			throws ApplicationException
	{
		String lsMsg = null;
		String lsContractId = null;
		String lsBudgetId = null;
		Channel loChannel = new Channel();
		HashMap loHmRequiredProps = new HashMap();
		Map<String, String> loTabLevelCommentsMap = null; // R4
		try
		{
			PortletSession loSession = aoActionRequest.getPortletSession();
			String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);
			P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			lsContractId = aoActionRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW);
			lsBudgetId = aoActionRequest.getParameter(HHSConstants.BUDGET_ID_WORKFLOW);
			// Fetch budget modification status for launching the Contract
			// Budget modification Task and changing the status
			String lsBudgetStatus = moContractBudgetControllerUtils.fetchCurrentBudgetStatus(lsBudgetId);
			if (lsBudgetStatus.equalsIgnoreCase(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_BUDGET_SUSPENDED)))
			{
				lsMsg = PropertyLoader
						.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.CB_SUSPENDED);
				ApplicationSession.setAttribute(lsMsg, aoActionRequest, HHSConstants.ERROR_MESSAGE);
			}
			else
			{ // Map to launch WF312 - Contract Budget Modification
				loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
				loHmRequiredProps.put(HHSConstants.CONTRACT_ID_WORKFLOW, lsContractId);
				loHmRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW, lsBudgetId);
				loHmRequiredProps.put(HHSConstants.SUBMITTED_BY, lsUserId);
				loHmRequiredProps.put(HHSConstants.MOD_BY_USER_ID, HHSConstants.EMPTY_STRING);
				loHmRequiredProps.put(HHSConstants.AUDIT_PROVIDER_INITIATED_TASK, true);
				loHmRequiredProps.put(HHSConstants.WORKFLOW_NAME, HHSConstants.WF_CONTRACT_BUDGET_MODIFICATION);
				String lsProviderComment = aoActionRequest.getParameter(HHSConstants.PUBLIC_COMMENT_AREA);
				// Set Audit Data in channel to be saved in History
				setAuditDataInChannel(loChannel, lsBudgetId, lsUserId, lsProviderComment, lsBudgetStatus,
						HHSConstants.STATUS_CHANGED_FROM, HHSConstants.AUDIT_CONTRACT_BUDGET_MODIFICATION);
				loHmRequiredProps.put(HHSConstants.AS_STATUS_ID,
						HHSUtil.getStatusID(HHSConstants.BUDGETLIST_BUDGET, HHSConstants.STATUS_PENDING_APPROVAL));
				loChannel.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
				loChannel.setData(HHSConstants.LB_AUTH_STATUS_FLAG, true);
				loChannel.setData(HHSConstants.AO_HMWF_REQUIRED_PROPS, loHmRequiredProps);
				loChannel.setData(HHSConstants.CHANNEL_PARAM_TAB_LEVEL_COMMENTS_MAP_PROVIDER, loTabLevelCommentsMap);
				// Start R5 : set EntityId and EntityName for AutoSave
				CommonUtil.setChannelForAutoSaveData(loChannel, lsBudgetId,
						HHSConstants.AUDIT_CONTRACT_BUDGET_MODIFICATION);
				// End R5 : set EntityId and EntityName for AutoSave
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.LAUNCH_WF_CONTRACT_MODIFICATION);
				
			}
		}
		/**
		 * Any Exception occurs while executing transaction will be catch as
		 * Application Exception here and If exception object contains defined
		 * error message it will set in session to display on jsp page else by
		 * defalut error message is shown on page
		 */
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
			ApplicationSession.setAttribute(lsLevelErrorMessage, aoActionRequest, HHSConstants.ERROR_MESSAGE);
			LOG_OBJECT.Error("Exception occured in submitConfirmationOverlay", aoAppExe);
		}
		/**
		 * Any non application Exception occurs catches here and default error
		 * message is shown on page
		 */
		catch (Exception aoExe)
		{
			ApplicationSession.setAttribute(HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED, aoActionRequest,
					HHSConstants.ERROR_MESSAGE);
			LOG_OBJECT.Error("Exception occured in submitConfirmationOverlay  ", aoExe);
		}
		aoActionResponse.setRenderParameter(HHSConstants.CONTRACT_ID_WORKFLOW, lsContractId);
		aoActionResponse.setRenderParameter(HHSConstants.BUDGET_ID_WORKFLOW, lsBudgetId);
		aoActionResponse.setRenderParameter(HHSConstants.BUDGET_TYPE, HHSConstants.BUDGET_TYPE2);
		aoActionResponse.setRenderParameter(HHSConstants.SUBMIT_OVERLAY_SUCCESS, HHSConstants.SUBMIT_OVERLAY_SUCCESS);
		aoActionResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.CONTRACT_BUDGET_HANDLER);
	}
	
	/**
	 * This method is used to check --> Review levels set for the Agency in
	 * order to launch WF312 - Contract Budget Modification After which WF312 -
	 * Contract Budget Modification is launched and Budget status is changed to
	 * 'Pending Approval' <li>The transaction used: launchWFContractUpdate</li>
	 * This method is updated in R4
	 * @param aoActRequest Action request object
	 * @param aoActResponse Action response object
	 * @throws ApplicationException an ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes", "static-access" })
	@ActionMapping(params = "submit_action=submitUpdateConfirmation")
	public void submitUpdateConfirmationOverlay(ActionRequest aoActRequest, ActionResponse aoActResponse)
			throws ApplicationException
	{
		Channel loChannel = new Channel();
		String lsContractId = null;
		String lsBudgetId = null;
		String lsCtId = null;
		HashMap loHmRequiredProps = new HashMap();
		String lsMsg = null;
		boolean lbFlag = true;
		Map<String, String> loTabLevelCommentsMap = null; // R4
		try
		{
			PortletSession loSession = aoActRequest.getPortletSession();
			String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);
			P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			lsContractId = aoActRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW);
			lsBudgetId = aoActRequest.getParameter(HHSConstants.BUDGET_ID_WORKFLOW);
			lsCtId = aoActRequest.getParameter(HHSConstants.CT_ID);
			// Fetch budget modification status for launching the Contract
			// Budget modification Task
			// and changing the status
			String lsBudgetStatus = moContractBudgetControllerUtils.fetchCurrentBudgetStatus(lsBudgetId);
			if (lsBudgetStatus.equalsIgnoreCase(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_BUDGET_SUSPENDED)))
			{
				lsMsg = PropertyLoader
						.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.CB_SUSPENDED);
				ApplicationSession.setAttribute(lsMsg, aoActRequest, HHSConstants.ERROR_MESSAGE);
			}
			else
			{
				// Map to launch WF312 - Contract Budget Modification
				loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
				loHmRequiredProps.put(HHSConstants.CONTRACT_ID_WORKFLOW, lsContractId);
				loHmRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW, lsBudgetId);
				loHmRequiredProps.put(HHSConstants.PROPERTY_PE_CT, lsCtId);
				loHmRequiredProps.put(HHSConstants.SUBMITTED_BY, lsUserId);
				loHmRequiredProps.put(HHSConstants.MOD_BY_USER_ID, HHSConstants.EMPTY_STRING);
				loHmRequiredProps.put(HHSConstants.AUDIT_PROVIDER_INITIATED_TASK, lbFlag);
				loHmRequiredProps.put(HHSConstants.WORKFLOW_NAME, HHSConstants.WF_CONTRACT_BUDGET_UPDATE_REVIEW);
				String lsProviderComment = aoActRequest.getParameter(HHSConstants.PUBLIC_COMMENT_AREA);
				// Set Audit Data in channel to be saved in History
				setAuditDataInChannel(loChannel, lsBudgetId, lsUserId, lsProviderComment, lsBudgetStatus,
						HHSConstants.STATUS_CHANGED_FROM, HHSConstants.AUDIT_CONTRACT_BUDGET_UPDATE);
				loHmRequiredProps.put(HHSConstants.AS_STATUS_ID,
						HHSUtil.getStatusID(HHSConstants.BUDGETLIST_BUDGET, HHSConstants.STATUS_PENDING_APPROVAL));
				loChannel.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, lbFlag);
				loChannel.setData(HHSConstants.LB_AUTH_STATUS_FLAG, lbFlag);
				loChannel.setData(HHSConstants.AO_HMWF_REQUIRED_PROPS, loHmRequiredProps);
				// R4: Setting TabLevelComments Map in Channel
				loChannel.setData(HHSConstants.CHANNEL_PARAM_TAB_LEVEL_COMMENTS_MAP_PROVIDER, loTabLevelCommentsMap);
				// Start R5 : set EntityId and EntityName for AutoSave
				CommonUtil.setChannelForAutoSaveData(loChannel, lsBudgetId, HHSConstants.AUDIT_CONTRACT_BUDGET_UPDATE);
				// End R5 : set EntityId and EntityName for AutoSave
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.LAUNCH_WF_CONTRACT_UPDATE);
			}
		}
		/**
		 * Any Exception occurs while executing transaction will be catch as
		 * Application Exception here and If exception object contains defined
		 * error message it will set in session to display on jsp page else by
		 * default error message is shown on page
		 */
		catch (ApplicationException aoAppExe)
		{
			String lsLevelErrMessage = null;
			ApplicationException loAppEx = (ApplicationException) aoAppExe.getRootCause();
			if (null != loAppEx)
			{
				lsLevelErrMessage = (String) loAppEx.getContextData().get(HHSConstants.LEVEL_ERROR_MESSAGE);
			}
			if (null == lsLevelErrMessage)
			{
				lsLevelErrMessage = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			}
			ApplicationSession.setAttribute(lsLevelErrMessage, aoActRequest, HHSConstants.ERROR_MESSAGE);
			LOG_OBJECT.Error("Exception occured in submitUpdateConfirmation", aoAppExe);
		}
		/**
		 * Any non application Exception occurs catches here and default error
		 * message is shown on page
		 */
		catch (Exception aoExe)
		{
			ApplicationSession.setAttribute(HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED, aoActRequest,
					HHSConstants.ERROR_MESSAGE);
			LOG_OBJECT.Error("Exception occured in submitUpdateConfirmation  ", aoExe);
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
	 * 
	 * 
	 * @param aoResourceRequest Resource request object
	 * @param aoResourceResponse Resource response object
	 * @throws ApplicationException an applicationException
	 */
	@SuppressWarnings("rawtypes")
	@ResourceMapping("validateUser")
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
				lsErrorMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.CB_VALIDATE_ERROR);
				liErrorCode = HHSConstants.INT_ONE;
				setErrorMsgForJSPForContractBudget(aoResourceResponse, lsErrorMsg, liErrorCode);
			}
		}
		// Application Exception handled here
		catch (ApplicationException aoAppExe)
		{
			LOG_OBJECT.Error("Exception occured in submitConfirmationOverlay", aoAppExe);
			// Application Exception occurred
			lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			liErrorCode = HHSConstants.INT_TWO;
			setErrorMsgForJSPForContractBudget(aoResourceResponse, lsErrorMsg, liErrorCode);
		}
	}
	
	/**
	 * This method set all audit data in channel object required for Audit
	 * purpose
	 * 
	 * <ul>
	 * <li>Set audit data for Status changed for Budget</li>
	 * <li>Set audit data for provider comments entered by provider</li>
	 * </ul>
	 * 
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
		StringBuilder loStatusChange = new StringBuilder();
		loStatusChange.append(asStatusChange);
		loStatusChange.append(HHSConstants.SPACE);
		loStatusChange.append(HHSConstants.STR);
		loStatusChange.append(lsPrevStatus);
		loStatusChange.append(HHSConstants.STR);
		loStatusChange.append(HHSConstants._TO_);
		loStatusChange.append(HHSConstants.STR);
		loStatusChange.append(HHSConstants.STATUS_PENDING_APPROVAL);
		loStatusChange.append(HHSConstants.STR);
		if (null != asComment && !asComment.isEmpty())
		{
			loAuditList.add(HHSUtil.addAuditDataToChannel(P8Constants.PROPERTY_PE_TH_PROVIDER_COMMENT,
					P8Constants.PROPERTY_PE_TH_PROVIDER_COMMENT, asComment, asEntityType, asBudgetId, asUserId,
					HHSConstants.PROVIDER_AUDIT));
		}
		loAuditList.add(HHSUtil.addAuditDataToChannel(HHSConstants.STATUS_CHANGE, HHSConstants.STATUS_CHANGE,
				loStatusChange.toString(), asEntityType, asBudgetId, asUserId, HHSConstants.PROVIDER_AUDIT));
		loHhsAuditBean.setEntityId(asBudgetId);
		loHhsAuditBean.setEntityType(asEntityType);
		aoChannel.setData(HHSConstants.LO_AUDIT_LIST, loAuditList);
		aoChannel.setData(HHSConstants.AUDIT_BEAN, loHhsAuditBean);
	}
	
	/**
	 * This method write error message on jsp in JSON format
	 * 
	 * @param aoResResponse Resource Response object
	 * @param lsErrMsg Error message
	 * @param liErrCode Error code
	 */
	private void setErrorMsgForJSPForContractBudget(ResourceResponse aoResResponse, String lsErrMsg, int liErrCode)
	{
		PrintWriter loOut = null;
		try
		{
			aoResResponse.setContentType(HHSConstants.APPLICATION_JSON);
			loOut = aoResResponse.getWriter();
			lsErrMsg = HHSConstants.ERROR_1 + liErrCode + HHSConstants.MESSAGE_1 + lsErrMsg
					+ HHSConstants.CLOSING_BRACE_1;
			loOut.print(lsErrMsg);
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
	
	/**
	 * This method write to validate the budgetStatus
	 * <ul>
	 * <li>1. If Budget status is Canceled then add Error Message</li>
	 * <li>2. If Budget status is Suspended add Error Message</li>
	 * <li>3. Other wise add Success Message</li>
	 * </ul>
	 * 
	 * @param asbudgetID budgetId
	 * @return loMap hashMap
	 * @throws ApplicationException an applicationException
	 */
	@SuppressWarnings("static-access")
	public Map<String, String> validateBudgetStatus(String asbudgetID) throws ApplicationException
	{
		Map<String, String> loMap = new HashMap<String, String>();
		
		String lsBudgetStatus = moContractBudgetControllerUtils.fetchCurrentBudgetStatus(asbudgetID);
		
		if (lsBudgetStatus.equalsIgnoreCase(moContractBudgetControllerUtils.fetchErrorMsg(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_BUDGET_CANCELLED)))
		{
			loMap.put(HHSConstants.ERROR_MESSAGE, moContractBudgetControllerUtils.fetchErrorMsg(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.CB_CANCELLED));
		}
		else if (lsBudgetStatus.equalsIgnoreCase(moContractBudgetControllerUtils.fetchErrorMsg(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_BUDGET_SUSPENDED)))
		{
			loMap.put(HHSConstants.ERROR_MESSAGE, moContractBudgetControllerUtils.fetchErrorMsg(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.CB_CLOSED));
		}
		else
		{
			// successMessage
			loMap.put(HHSConstants.SUCCESS_MESSAGE, moContractBudgetControllerUtils.fetchErrorMsg(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.CB_SAVED));
		}
		return loMap;
	}
	
	/**
	 * <ul>
	 * <li>This method is triggered on clicking save button on Contract Budget
	 * Modification screens</li>
	 * <li>Transaction Id : fetchFyBudgetSummary</li>
	 * 
	 * <li>Service Class : ContractBudgetModification</li>
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
	 * @param aoResourceRequestForSave request param
	 * @param aoResourceResponseForSave response param
	 * @return loModelAndView ModelAndView object
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked", "static-access" })
	@ResourceMapping("saveContractBudgetModification")
	public ModelAndView saveContractBudgetModification(ResourceRequest aoResourceRequestForSave,
			ResourceResponse aoResourceResponseForSave)
	{
		ModelAndView loModelAndView = null;
		PrintWriter loOutForBudgetModification = null;
		Map<String, String> loMap = null;
		String lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
		try
		{
			aoResourceResponseForSave.setContentType(HHSConstants.TEXT_HTML);
			loMap = moContractBudgetControllerUtils.validateBudgetStatus(aoResourceRequestForSave
					.getParameter(HHSConstants.BUDGET_ID));
			loOutForBudgetModification = aoResourceResponseForSave.getWriter();
			if (!loMap.containsKey(HHSConstants.SUCCESS_MESSAGE))
			{
				lsErrorMsg = loMap.get(HHSConstants.ERROR_MESSAGE);
			}
			else
			{
				String lsUserId = (String) aoResourceRequestForSave.getPortletSession().getAttribute(
						ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
				String lsProviderComment = aoResourceRequestForSave.getParameter(HHSConstants.PUBLIC_COMMENT_AREA);
				TaskDetailsBean loTaskDetailsBeanForSave = new TaskDetailsBean();
				loTaskDetailsBeanForSave.setEntityId(aoResourceRequestForSave.getParameter(HHSConstants.BUDGET_ID));
				loTaskDetailsBeanForSave.setEntityType(HHSConstants.AUDIT_CONTRACT_BUDGET_MODIFICATION);
				loTaskDetailsBeanForSave.setIsTaskScreen(false);
				loTaskDetailsBeanForSave.setProviderComment(lsProviderComment);
				loTaskDetailsBeanForSave.setUserId(lsUserId);
				saveCommentNonAudit(loTaskDetailsBeanForSave);
				Channel loChannelForSave = new Channel();
				Map loInputMap = new HashMap();
				loInputMap.put(HHSConstants.CONTRACT_ID_WORKFLOW,
						aoResourceRequestForSave.getParameter(HHSConstants.CONTRACT_ID));
				loInputMap.put(HHSConstants.BUDGET_ID_WORKFLOW,
						aoResourceRequestForSave.getParameter(HHSConstants.BUDGET_ID));
				loChannelForSave.setData(HHSConstants.AO_HASH_MAP, loInputMap);
				HHSTransactionManager.executeTransaction(loChannelForSave, HHSConstants.FETCH_FY_BUDGET_SUMMARY);
				BudgetDetails loBudgetDetails = (BudgetDetails) loChannelForSave
						.getData(HHSConstants.LO_BUDGET_DETAILS);
				aoResourceRequestForSave.setAttribute(HHSConstants.FISCAL_BUDGET_INFO, loBudgetDetails);
				lsErrorMsg = HHSConstants.EMPTY_STRING;
				loModelAndView = new ModelAndView(HHSConstants.JSP_PATH_CONTRACT_FY_BUDGET);
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
			if (null != loOutForBudgetModification)
			{
				if (!HHSR5Constants.EMPTY_STRING.equalsIgnoreCase(lsErrorMsg))
				{
					loOutForBudgetModification.print(lsErrorMsg);
				}
				loOutForBudgetModification.flush();
				loOutForBudgetModification.close();
			}
		}
		return loModelAndView;
	}
	
	/**
	 * <ul>
	 * <li>This method is triggered on clicking save button on Contract Budget
	 * Update screens</li>
	 * <li>Transaction Id : fetchFyBudgetSummary</li>
	 * 
	 * <li>Service Class : ContractBudgetModification</li>
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
	 * <li>The transaction used: loBudgetDetails</li>
	 * </ol>
	 * </ul>
	 * 
	 * @param aoResourceRequestForUpdate request param
	 * @param aoResourceResponseForUpdate response param
	 * @return loModelAndView ModelAndView object
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked", "static-access" })
	@ResourceMapping("saveContractBudgetUpdate")
	public ModelAndView saveContractBudgetUpdate(ResourceRequest aoResourceRequestForUpdate,
			ResourceResponse aoResourceResponseForUpdate)
	{
		ModelAndView loModelAndView = null;
		PrintWriter loOutForBudgetUpdate = null;
		Map<String, String> loMap = null;
		String lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
		try
		{
			aoResourceResponseForUpdate.setContentType(HHSConstants.TEXT_HTML);
			loMap = moContractBudgetControllerUtils.validateBudgetStatusForSuspended(aoResourceRequestForUpdate
					.getParameter(HHSConstants.BUDGET_ID));
			loOutForBudgetUpdate = aoResourceResponseForUpdate.getWriter();
			if (!loMap.containsKey(HHSConstants.SUCCESS_MESSAGE))
			{
				lsErrorMsg = loMap.get(HHSConstants.ERROR_MESSAGE);
			}
			else
			{
				String lsUserId = (String) aoResourceRequestForUpdate.getPortletSession().getAttribute(
						ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
				String lsProviderComment = aoResourceRequestForUpdate.getParameter(HHSConstants.PUBLIC_COMMENT_AREA);
				TaskDetailsBean loTaskDetailsBeanForUpdate = new TaskDetailsBean();
				loTaskDetailsBeanForUpdate.setEntityId(aoResourceRequestForUpdate.getParameter(HHSConstants.BUDGET_ID));
				loTaskDetailsBeanForUpdate.setEntityType(HHSConstants.AUDIT_CONTRACT_BUDGET_UPDATE);
				loTaskDetailsBeanForUpdate.setIsTaskScreen(false);
				loTaskDetailsBeanForUpdate.setProviderComment(lsProviderComment);
				loTaskDetailsBeanForUpdate.setUserId(lsUserId);
				saveCommentNonAudit(loTaskDetailsBeanForUpdate);
				Channel loChannel = new Channel();
				Map loInputMapForUpdate = new HashMap();
				loInputMapForUpdate.put(HHSConstants.CONTRACT_ID_WORKFLOW,
						aoResourceRequestForUpdate.getParameter(HHSConstants.CONTRACT_ID));
				loInputMapForUpdate.put(HHSConstants.BUDGET_ID_WORKFLOW,
						aoResourceRequestForUpdate.getParameter(HHSConstants.BUDGET_ID));
				loInputMapForUpdate.put(HHSConstants.BUDGET_TYPE, HHSConstants.FOUR);
				loChannel.setData(HHSConstants.AO_HASH_MAP, loInputMapForUpdate);
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_FY_BUDGET_SUMMARY);
				BudgetDetails loBudgetDetails = (BudgetDetails) loChannel.getData(HHSConstants.LO_BUDGET_DETAILS);
				aoResourceRequestForUpdate.setAttribute(HHSConstants.FISCAL_BUDGET_INFO, loBudgetDetails);
				lsErrorMsg = HHSConstants.EMPTY_STRING;
				loModelAndView = new ModelAndView(HHSConstants.JSP_PATH_CONTRACT_UPDATE_FY_BUDGET);
			}
		}
		// Application Exception handled here
		catch (ApplicationException aoExp)
		{
			// Log is generated in case of any Error and Error message
			LOG_OBJECT.Error("Application Exception in saveContractBudgetUpdate method", aoExp);
		}
		catch (Exception aoExp)
		{
			// Log is generated in case of any Error and Error message
			LOG_OBJECT.Error("Exception in saveContractBudgetUpdate method", aoExp);
		}
		finally
		{
			if (null != loOutForBudgetUpdate)
			{
				if (!HHSR5Constants.EMPTY_STRING.equalsIgnoreCase(lsErrorMsg))
				{
					loOutForBudgetUpdate.print(lsErrorMsg);
				}
				loOutForBudgetUpdate.flush();
				loOutForBudgetUpdate.close();
			}
			
		}
		return loModelAndView;
	}
	
	/**
	 * This method is used to display the Submit Contract Update Confirmation
	 * Overlay
	 * <ul>
	 * <li>1.Get the required view name from request</li>
	 * <li>The transaction used: validateCBUAmountTotal</li>
	 * <li>Release 3.6.0 Enhancement id 6484</li>
	 * <ul>
	 * 
	 * @param aoResourceRequest Resource Request Object
	 * @param aoResourceResponse Resource Response Object
	 * @return ModelAndView Overlay JSP
	 * @throws Exception an Exception
	 */
	@ResourceMapping("submitContractBudgetUpdateOverlay")
	public ModelAndView submitContractBudgetUpdateOverlay(ResourceRequest aoResourceRequest,
			ResourceResponse aoResourceResponse) throws Exception
	{
		ModelAndView loModelAndView = null;
		PrintWriter loOut = null;
		Map<String, String> loMap = null;
		String lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
		int liErrorCode = HHSConstants.INT_ZERO;
		try
		{
			aoResourceResponse.setContentType(HHSConstants.TEXT_HTML);
			loMap = ContractBudgetControllerUtils.validateBudgetStatusForSuspended(aoResourceRequest
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
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.CBU_VALIDATE_SUBMIT_AMOUNT_TOTAL);
				Boolean loValid = (Boolean) loChannel.getData(HHSConstants.AB_VALIDATE_STATUS);				
				//Start: Added in Release 7
                HashMap loServiceValid = (HashMap) loChannel.getData(HHSConstants.AO_HASH_MAP);
                String lsIsServicesSuccess =(String) loServiceValid.get(HHSConstants.SUCCESS);
                //End: Added in Release 7
                // Following condition updated in Release 7
				if (loValid.booleanValue() && Boolean.parseBoolean(lsIsServicesSuccess))
				{
					liErrorCode = HHSConstants.INT_THREE;
					// redirect to overlay
					String lsJspName = aoResourceRequest.getParameter(HHSConstants.JSP_NAME);
					aoResourceRequest.setAttribute(HHSConstants.BUDGET_ID_KEY,
							aoResourceRequest.getParameter(HHSConstants.BUDGET_ID));
					aoResourceRequest.setAttribute(HHSConstants.AGENCY_ID,
							aoResourceRequest.getParameter(HHSConstants.AGENCY_ID1));
					aoResourceRequest.setAttribute(HHSConstants.CONTRACT_ID_KEY,
							aoResourceRequest.getParameter(HHSConstants.CONTRACT_ID));
					aoResourceRequest.setAttribute(HHSConstants.CT_ID,
							aoResourceRequest.getParameter(HHSConstants.CT_ID));
					String lsJspPath = HHSConstants.JSP_CONTRACTBUDGET + lsJspName;
					loModelAndView = new ModelAndView(lsJspPath);
				}
				else
				{
					if (!loValid.booleanValue())
					{
						setErrorMsgForJSPForContractBudget(aoResourceResponse, PropertyLoader.getProperty(
								HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.MSG_KEY_SUBMIT_BUDGET_ERR),
								HHSConstants.INT_ONE);
					}
					else
					{
						setErrorMsgForJSPForContractBudget(aoResourceResponse, PropertyLoader.getProperty(
								HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
								(String) loServiceValid.get(HHSConstants.CBL_MESSAGE)), HHSConstants.INT_ONE);
					}
				}
			}
			else
			{
				// set error message
				// Release 3.6.0 Enhancement id 6484
				setErrorMsgForJSPForContractBudget(aoResourceResponse, loMap.get(HHSConstants.ERROR_MESSAGE),
						HHSConstants.INT_ONE);
			}
		}
		// Application Exception handled here
		catch (ApplicationException aoExp)
		{
			// Log is generated in case of any Error and Error message
			// Error message is set for JSP
			liErrorCode = HHSConstants.INT_TWO;
			setErrorMsgForJSPForContractBudget(aoResourceResponse, lsErrorMsg, liErrorCode);
			LOG_OBJECT.Error("Application Exception in submitContractBudgetOverlay method", aoExp);
		}
		catch (Exception aoExp)
		{
			// Log is generated in case of any Error and Error message
			// Error message is set for JSP
			liErrorCode = HHSConstants.INT_TWO;
			setErrorMsgForJSPForContractBudget(aoResourceResponse, lsErrorMsg, liErrorCode);
			LOG_OBJECT.Error("Exception in submitContractBudgetOverlay method", aoExp);
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

	/**
	 * This method is added in R7. This method will fetch the count of
	 * modification Budget
	 * @param asBudgetId
	 * @return
	 * @throws ApplicationException
	 */
	private String fetchModificationBudgetCount(String asBudgetId) throws ApplicationException
	{
		String lsModificationCount = HHSConstants.ZERO;
		try
		{
			if (null != asBudgetId && !asBudgetId.equalsIgnoreCase(HHSR5Constants.EMPTY_STRING))
			{
				Channel loChannel = new Channel();
				HashMap<String, String> loHashMap = new HashMap<String, String>();
				loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, asBudgetId);
				loChannel.setData(HHSConstants.AO_HASH_MAP, loHashMap);
				HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.GET_MODIFICATION_COUNT_FOR_TASK,
						HHSR5Constants.TRANSACTION_ELEMENT_R5);
				lsModificationCount = (String) loChannel.getData(HHSConstants.RETURN_STATUS);
			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Error while fetching Modification budget count :", loAppEx);
			throw loAppEx;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error while fetching Modification budget count :", loExp);
			throw new ApplicationException("Error while fetching Details :", loExp);
		}
		return lsModificationCount;
	}
}
