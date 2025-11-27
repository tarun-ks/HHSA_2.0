package com.nyc.hhs.controllers;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.axis.utils.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.ActionStatusBean;
import com.nyc.hhs.model.AgencySettingsBean;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.ActionStatusUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PortalUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This controller is for Agency Settings module accessed by 'Agency and City'
 * users City users sets level of review for a particular agency --> particular
 * review process Agency user then assigns users across levels This class is
 * updated in R6 for Handling task of Return Payment.
 * This class has been updated in R7 for Modification Auto Approval Enhancement.
 */
@Controller(value = "actionMenuCtrl")
@RequestMapping("view")
public class ActionMenuController extends BaseController
{

	/**
	 * Log object to record all logs
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(ActionMenuController.class);

	/**
	 * This method is a rendering handler after action base on parameters from actions
	 * 
	 * @param aoRequest RenderRequest object
	 * @param aoResponse RenderResponse object
	 * @return loModelAndView ModelAndView as return type with jsp name and data
	 *         to be displayed dynamically
	 */
	@SuppressWarnings("unchecked")
	@RenderMapping
	protected ModelAndView handleRenderRequestInternal(RenderRequest aoRequest, RenderResponse aoResponse)
	{

		String lsFormPath = HHSR5Constants.AS_ACTION_MENU_AGENCY_SELECTION;
		ModelAndView loModelAndView = null;
		Map<String, Object> loAgencyUserMap = new HashMap<String, Object>();
		AgencySettingsBean loAgencySettingsBean = null;
		String lsNavigationPath = null;
		// Update in R6 for Return Payment: Added to check which JSP to
		// render(agencySetting or bulkNotification)
		try
		{
			PortletSession loSession = aoRequest.getPortletSession();
			lsFormPath = HHSR5Constants.AS_ACTION_MENU_AGENCY_SELECTION;
			String lsUserRole = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ROLE, PortletSession.APPLICATION_SCOPE);
			lsNavigationPath = PortalUtil.parseQueryString(aoRequest, HHSR5Constants.AGENCY_SETTING_TAB);

			loAgencySettingsBean = getAgencyList();
			
			if (!(StringUtils.isEmpty(lsNavigationPath))
					&& lsUserRole.equalsIgnoreCase(HHSConstants.CFO_ROLE)
					&& (lsNavigationPath.equalsIgnoreCase(HHSR5Constants.BULK_NOTIFICATIONS) || lsNavigationPath
							.equalsIgnoreCase(HHSConstants.AS_AGENCY_SETTINGS)))
			{
				lsFormPath = lsNavigationPath;
				aoRequest.setAttribute(HHSConstants.STATUS_COLUMN, aoRequest.getParameter(HHSConstants.STATUS_COLUMN));
			}
			HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb
					.getInstance().getCacheObject(ApplicationConstants.APPLICATION_SETTING);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
					aoRequest.getPortletSession().getAttribute(ApplicationConstants.MESSAGE));
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					aoRequest.getPortletSession().getAttribute(ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE));
			// End:Added in R6 for Return Payment.
		}
		catch (ApplicationException loAppEx)
		{
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_STATUS, HHSConstants.AS_FAILED);
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_MSG, HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED);
			LOG_OBJECT.Error("Error occured while processing  ", loAppEx);
		}
		catch (Exception loEx)
		{
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_STATUS, HHSConstants.AS_FAILED);
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_MSG, HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED);
			LOG_OBJECT.Error("Error occured while processing  ", loEx);
		}

		loAgencyUserMap.put(HHSConstants.AS_AGENCY_SETTING_BEAN, loAgencySettingsBean);

		loModelAndView = new ModelAndView(lsFormPath, loAgencyUserMap);
		return loModelAndView;
	}

	
	
	@ResourceMapping("getAgencyActionMenuInfo")
	protected ModelAndView getAgencyActionMenuInfo(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		String lsFormPath = null;
		ModelAndView loModelAndView = null;
		AgencySettingsBean loAgencySettingsBean = new AgencySettingsBean();
		PrintWriter loPrintWriter = null;
		String lsTransactionStatusMsg = HHSConstants.AS_FAILURE + HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
		try
		{
			aoResourceResponse.setContentType(HHSConstants.TEXT_HTML);

			String lsSelectedAgencyId = (String) aoResourceRequest.getParameter(HHSConstants.AS_HIDDEN_REVIEW_PROCESS_ID);
			loAgencySettingsBean.setAgencyId(lsSelectedAgencyId);
			ActionStatusBean loActionStat = ActionStatusUtil.getMoActionByAgency(lsSelectedAgencyId);
			if(loActionStat != null ) {
				loAgencySettingsBean.setActionMenuList(loActionStat.toList());
			}

				lsFormPath = HHSConstants.AS_ACTION_MENU_MANAGE;
				aoResourceRequest.getPortletSession().setAttribute(HHSConstants.AS_HIDDEN_REVIEW_PROCESS_ID, lsSelectedAgencyId, PortletSession.APPLICATION_SCOPE);
				aoResourceRequest.getPortletSession().setAttribute(HHSConstants.AS_AGENCY_SETTINGS_BEAN, loAgencySettingsBean, PortletSession.APPLICATION_SCOPE);

		}
		catch (Exception loEx)
		{
			lsTransactionStatusMsg = HHSConstants.AS_FAILURE + HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("Error occured while fetching assigned users", loEx);
		}
		finally
		{
			if (null != loPrintWriter)
			{
				loPrintWriter.flush();
				loPrintWriter.close();
			}
		}
		loModelAndView = new ModelAndView(lsFormPath);
		return loModelAndView;
	}
	

	@ResourceMapping("saveAgencyActionMenus")
	protected void saveAgencyActionMenu(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		String lsTransactionStatusMsg = HHSConstants.AS_FAILURE + HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
		PrintWriter loPrintWriter = null;
		StringBuffer loStringBuffer = new StringBuffer();
		
		try
		{
			LOG_OBJECT.Debug("Start saveAgencyActionMenu method");
			aoResourceResponse.setContentType(HHSConstants.TEXT_HTML);
			Channel loChannelObj = new Channel();
			String lsUserId = (String) aoResourceRequest.getPortletSession().getAttribute( ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);

			String lsSelectedAgencyId = (String) aoResourceRequest.getPortletSession().getAttribute(HHSConstants.AS_HIDDEN_REVIEW_PROCESS_ID,  PortletSession.APPLICATION_SCOPE);
			//String lsRevProcessId = (String) aoResourceRequest.getParameter(HHSConstants.AS_HIDDEN_REVIEW_PROC_ID);
			//int liLevelOfReviewAssigned = (Integer) aoResourceRequest.getPortletSession().getAttribute(	HHSConstants.AS_LEVEL_OF_REVIEW_ASSGND, PortletSession.APPLICATION_SCOPE);
			//AgencySettingsBean   loAgencySettingsBean     = (AgencySettingsBean) aoResourceRequest.getPortletSession().getAttribute(HHSConstants.AS_AGENCY_SETTINGS_BEAN, PortletSession.APPLICATION_SCOPE);
/*
			String lsAgencyname = loOldAgencySettingsBean.getAgencyId();
			loNewAgencySettingsBean = setNewUsersList(aoResourceRequest, loNewAgencySettingsBean);
			loNewAgencySettingsBean.setReviewProcessId(liReviewProcId);
			loNewAgencySettingsBean.setAgencyId(lsAgencyname);
			loNewAgencySettingsBean.setLastUpdateDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
			loNewAgencySettingsBean.setCreatedDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
			loNewAgencySettingsBean.setCreatedByUserId(lsUserId);
			loNewAgencySettingsBean.setModifiedDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
			loNewAgencySettingsBean.setModifiedByUserId(lsUserId);

			P8UserSession loUserSession = (P8UserSession) aoResourceRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			
			loChannelObj.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
			loChannelObj.setData(HHSConstants.AS_AI_LEVEL_OF_REVIEW_ASSGND, liLevelOfReviewAssigned);
			loChannelObj.setData(HHSConstants.AS_AO_OLD_AGENCYSETTINGSBEAN, loOldAgencySettingsBean);
			loChannelObj.setData(HHSConstants.AS_AO_NEW_AGENCYSETTINGSBEAN, loNewAgencySettingsBean);

			aoResourceRequest.getPortletSession().setAttribute(HHSConstants.AS_AGENCY_SETTINGS_BEAN,loNewAgencySettingsBean, PortletSession.APPLICATION_SCOPE);
			*/
			ActionStatusBean loActionStatusvo = setNewActionVo(aoResourceRequest);
			loActionStatusvo.setAgencyId(lsSelectedAgencyId);
			loActionStatusvo.setModifiedByUserid(lsUserId);loActionStatusvo.setCreatedByUserid(lsUserId);
			loChannelObj.setData(HHSConstants.AO_ACTION_MENU_VALUE, loActionStatusvo);
			
			LOG_OBJECT.Debug(loActionStatusvo.toString());

			loPrintWriter = aoResourceResponse.getWriter();
			TransactionManager.executeTransaction(loChannelObj, HHSConstants.AO_ACTION_MENU_UPDATE_STATUS );
			Integer loSaveCnt = (Integer) loChannelObj.getData(HHSConstants.AO_ACTION_MENU_UPDATE_ROW_CNT);
			if (loSaveCnt.intValue() < 1 )
			{
				lsTransactionStatusMsg = HHSConstants.AS_FAILURE
						+ PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
								HHSConstants.USERS_CANT_BE_SAVED);
			}
			else
			{
				// END || Changes made for enhancement 6534 for Release 3.8.0
				lsTransactionStatusMsg = HHSConstants.AS_SUCCESS
						+ PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
								HHSConstants.PROPERTY_REVIEW_LEVELS_USER_SAVED);
			}
			
	        /*[Start]   QC9701 */
	        Channel loActionChannel = new Channel();
	        TransactionManager.executeTransaction(loActionChannel, "fetchActionStatusMap");
	        List<ActionStatusBean> loActionStatuslst = (List<ActionStatusBean>) loActionChannel.getData("allActionStatusLst");
	        LOG_OBJECT.Debug("@@@@@#####loActionStatuslst reloaded \n" );
	        ActionStatusUtil.setMoActionLst(loActionStatuslst);
	        /*[End]  QC9701 */
	        
		}
/*		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Error occured while processing save agency level users", loAppEx);
		}*/
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Error occured while processing save agency level users", loEx);
		}
		finally
		{
			if (null != loPrintWriter)
			{
				loStringBuffer.append(lsTransactionStatusMsg);
				loPrintWriter.print(loStringBuffer.toString());
				loPrintWriter.flush();
				loPrintWriter.close();
			}
		}
	}
	
	/**
	 * <p>
	 * This method is added in R7
	 * This method is called by city users on page load to fetch all
	 * agency names from database. City user can
	 * select any agency and can associate threshold
	 * values for the selected agency
	 * </p>
	 * <ul>
	 * <li>Fetch all agency names</li>
	 * </ul>
	 * @return loAgencySettingsBean AgencySettingsBean - bean containing list of
	 *         all agency names 
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	private AgencySettingsBean getAgencyList() throws ApplicationException
	{
		Channel loChannelObj = new Channel();
		AgencySettingsBean loAgencySettingsBean;
		HHSTransactionManager.executeTransaction(loChannelObj, HHSR5Constants.GET_AGENCY_LIST);
		loAgencySettingsBean = (AgencySettingsBean) loChannelObj.getData(HHSConstants.AS_AGENCY_SETTING_BEAN_OBJ);
		return loAgencySettingsBean;
	}
	
	private ActionStatusBean setNewActionVo(ResourceRequest aoResourceRequest) throws ApplicationException
	{
		ActionStatusBean loActionStatusBean = new ActionStatusBean();
		String lsAllActionMenus = (String) aoResourceRequest.getParameter(HHSConstants.AS_HIDDEN_ALL_USERS);
		String lsInactiveActionMenu = (String) aoResourceRequest.getParameter(HHSConstants.AS_HIDDEN_LEV1_USERS);
		String lsAgencyId = (String) aoResourceRequest.getParameter(HHSConstants.AS_HIDDEN_AGENCY_ID);

		String lsActiveActionArr[] = HHSUtil.convertStringToArray(lsAllActionMenus);
		String lsInactiveActionArr[] = HHSUtil.convertStringToArray(lsInactiveActionMenu);

		// Below converts string array to list
		List<String> loNewAllUserList = Arrays.asList(lsActiveActionArr);
		List<String> lsInactiveActionList = Arrays.asList(lsInactiveActionArr);
		
		loActionStatusBean.reset();
		loActionStatusBean.setAgencyId(lsAgencyId);
		
		if( loNewAllUserList != null && loNewAllUserList.size() > 0 ) {
			for( String loS : loNewAllUserList ) {
				try {
					int loInx = Integer.parseInt(loS);
					setActionStatus( loActionStatusBean , loInx );
				}catch( NumberFormatException nw) {
					continue;
				}
			}
		}

		return loActionStatusBean;
	}

	private void setActionStatus( ActionStatusBean aoActionStatusBean , int aoActionInx) {
			switch( aoActionInx ) {
			case  HHSR5Constants.ACTION_DROPDOWN_SUBMIT_INVOICE_INX        :
				aoActionStatusBean.setSubmitInvoice(ActionStatusBean.ACTION_DROPDOWN_ENABLE_VALUE);
				return;
			case  HHSR5Constants.ACTION_DROPDOWN_BUDGET_MOD_INX            : 
				aoActionStatusBean.setBudgetMod(ActionStatusBean.ACTION_DROPDOWN_ENABLE_VALUE);
				return;
			case  HHSR5Constants.ACTION_DROPDOWN_CANCEL_MOD_INX            : 
				aoActionStatusBean.setCancelMod(ActionStatusBean.ACTION_DROPDOWN_ENABLE_VALUE);
				return;
			case  HHSR5Constants.ACTION_DROPDOWN_REQUEST_ADVANCE_INX       : 
				aoActionStatusBean.setRequestAdvance(ActionStatusBean.ACTION_DROPDOWN_ENABLE_VALUE);
				return;
			case  HHSR5Constants.ACTION_DROPDOWN_UPDATE_BUDGET_TEMP_INX    : 
				aoActionStatusBean.setUpdateBudgetTemp(ActionStatusBean.ACTION_DROPDOWN_ENABLE_VALUE);
				return;
			case  HHSR5Constants.ACTION_DROPDOWN_INITIATE_ADVANCE_INX      : 
				aoActionStatusBean.setInitiateAdvance(ActionStatusBean.ACTION_DROPDOWN_ENABLE_VALUE);
				return;
			case  HHSR5Constants.ACTION_DROPDOWN_UPDATE_CONTRACT_CONF_INX  : 
				aoActionStatusBean.setUpdateContractConf(ActionStatusBean.ACTION_DROPDOWN_ENABLE_VALUE);
				return;
			case  HHSR5Constants.ACTION_DROPDOWN_AMEND_CONTRACT_INX        : 
				aoActionStatusBean.setAmendContract(ActionStatusBean.ACTION_DROPDOWN_ENABLE_VALUE);
				return;
			case  HHSR5Constants.ACTION_DROPDOWN_SUSPEND_CONTRACT_INX      : 
				aoActionStatusBean.setSuspendContract(ActionStatusBean.ACTION_DROPDOWN_ENABLE_VALUE);
				return;
			case  HHSR5Constants.ACTION_DROPDOWN_CLOSE_CONTRACT_INX        : 
				aoActionStatusBean.setCloseContract(ActionStatusBean.ACTION_DROPDOWN_ENABLE_VALUE);
				return;
			case  HHSR5Constants.ACTION_DROPDOWN_FLAG_CONTRACT_INX         : 
				aoActionStatusBean.setFlagContract(ActionStatusBean.ACTION_DROPDOWN_ENABLE_VALUE);
				return;
			case  HHSR5Constants.ACTION_DROPDOWN_CANCEL_AMENDMENT_INX      : 
				aoActionStatusBean.setCancelAmendment(ActionStatusBean.ACTION_DROPDOWN_ENABLE_VALUE);
				return;
			case  HHSR5Constants.ACTION_DROPDOWN_CANCEL_CONTRACT_INX       : 
				aoActionStatusBean.setCancelContract(ActionStatusBean.ACTION_DROPDOWN_ENABLE_VALUE);
				return;
			case  HHSR5Constants.ACTION_DROPDOWN_DELETE_CONTRACT_INX       : 
				aoActionStatusBean.setDeleteContract(ActionStatusBean.ACTION_DROPDOWN_ENABLE_VALUE);
				return;
			case  HHSR5Constants.ACTION_DROPDOWN_UPDATE_CONTRACTINFO_INX   : 
				aoActionStatusBean.setUpdateContractinfo(ActionStatusBean.ACTION_DROPDOWN_ENABLE_VALUE);
				return;
			case  HHSR5Constants.ACTION_DROPDOWN_NEW_FISCAL_YEAR_INX       : 
				aoActionStatusBean.setNewFiscalYear(ActionStatusBean.ACTION_DROPDOWN_ENABLE_VALUE);
				return;
			case  HHSR5Constants.ACTION_DROPDOWN_DELETE_INVOICE_INX        : 
				aoActionStatusBean.setDeleteInvoice(ActionStatusBean.ACTION_DROPDOWN_ENABLE_VALUE);
				return;
			case  HHSR5Constants.ACTION_DROPDOWN_WITHDRAW_INVOICE_INX      : 
				aoActionStatusBean.setWithdrawInvoice(ActionStatusBean.ACTION_DROPDOWN_ENABLE_VALUE);
				return;
			case  HHSR5Constants.ACTION_DROPDOWN_MARK_AS_REGISTERED_INX      : 
				aoActionStatusBean.setMarkAsRegistered(ActionStatusBean.ACTION_DROPDOWN_ENABLE_VALUE);
				return;
			case  HHSR5Constants.ACTION_DROPDOWN_RETURN_PAYMENT_INX      : 
				aoActionStatusBean.setReturnPayment(ActionStatusBean.ACTION_DROPDOWN_ENABLE_VALUE);
				return;
			case  HHSR5Constants.ACTION_DROPDOWN_CANCEL_RETURN_PAYMENT_INX      : 
				aoActionStatusBean.setReturnPaymentCancel(ActionStatusBean.ACTION_DROPDOWN_ENABLE_VALUE);
				return;
			case  HHSR5Constants.ACTION_DROPDOWN_DOWNLOAD_FOR_REGISTRATION_INX      : 
				aoActionStatusBean.setDownloadForRegistration(ActionStatusBean.ACTION_DROPDOWN_ENABLE_VALUE);
				return;
			case  HHSR5Constants.ACTION_DROPDOWN_CANCEL_MERGE_INX      : 
				aoActionStatusBean.setCancelAndMerge(ActionStatusBean.ACTION_DROPDOWN_ENABLE_VALUE);
				return;

				default:
			}
	}


}



