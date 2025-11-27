package com.nyc.hhs.controllers;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.StaffDetails;

@Controller(value = "providerSettingsController")
@RequestMapping("view")
public class ProviderSettingsController extends BaseController
{

	private static final LogInfo LOG_OBJECT = new LogInfo(ProviderSettingsController.class);

	/**
	 * Default Render Method
	 * @param aoRequest Render Request
	 * @param aoResponse Render Response
	 * @return Model & View
	 */
	@RenderMapping
	protected ModelAndView handleRenderRequestInternal(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		setExceptionMessageInResponse(aoRequest);
		ModelAndView loModelandView = new ModelAndView(ApplicationConstants.JSP_PROVIDER_SETTINGS);
		return loModelandView;
	}

	/**
	 * This method fetches Provider User Information on basis of Staff ID
	 * 
	 * <ul>
	 * <li>Transaction Invoked: "getStaffDetailsFromId"</li>
	 * <li>This method is setting aoRequest</li>
	 * </ul>
	 * 
	 * @param aoRequest Resource Request
	 * @param aoResponse Resource Response
	 * @return Model and View Created for R4
	 */
	@SuppressWarnings("unchecked")
	@ResourceMapping("getProviderUsrInfoResourceUrl")
	public ModelAndView getProviderUsrInfoResourceRequest(ResourceRequest aoRequest, ResourceResponse aoResponse)
	{
		ModelAndView loModelAndView = null;
		String lsFormPath = ApplicationConstants.JSP_SUB_PROVIDER_SETTINGS;
		PrintWriter loPrintWriter = null;
		String lsStaffId = null;
		try
		{
			lsStaffId = aoRequest.getParameter(HHSConstants.STAFF_ID);
			if (null != lsStaffId && !lsStaffId.isEmpty())
			{
				Channel loChannel = new Channel();
				loChannel.setData(ApplicationConstants.CHANNEL_ELEMET_SET_STAFF_ID, lsStaffId);
				TransactionManager.executeTransaction(loChannel,
						ApplicationConstants.TRANSACTION_GET_STAFF_DETAILS_FROM_ID);
				List<StaffDetails> loStaffDetailsList = (List<StaffDetails>) loChannel
						.getData(ApplicationConstants.CHANNEL_ELEMET_GET_STAFF_DETAILS_BEAN);
				if (null != loStaffDetailsList && !loStaffDetailsList.isEmpty())
				{
					populateProviderDetailProviderSettingsSubJsp(loStaffDetailsList, aoRequest);
				}
				else
				{
					String lsTransactionStatusMsg = HHSConstants.AS_FAILURE
							+ HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
					aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsTransactionStatusMsg);
					aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
							ApplicationConstants.MESSAGE_FAIL_TYPE);
					loPrintWriter = aoResponse.getWriter();
					loPrintWriter.print(lsTransactionStatusMsg);
				}
			}
		}
		catch (Exception loEx)
		{
			String lsTransactionStatusMsg = HHSConstants.AS_FAILURE + HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsTransactionStatusMsg);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
			LOG_OBJECT.Error(ApplicationConstants.ERROR_MSG_PROVIDER_SETTINGS_FAILED_RENDER, loEx);
			loPrintWriter.print(lsTransactionStatusMsg);
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

	/**
	 * Provider Settings Screen Submit Action - Processes Submit Access Request
	 * <ul>
	 * <li>Map is filled with ApplicationConstants</li>
	 * <li>aoResponse is set</li>
	 * <li>Transaction Invoked: "submitAccessRequestProvider"</li>
	 * </ul>
	 * @param aoRequest Resource Request
	 * @param aoResponse Resource Response Created for R4
	 */
	@SuppressWarnings("unchecked")
	@ResourceMapping("submitAccessRequestUrl")
	protected ModelAndView submitAccessRequestAction(ResourceRequest aoRequest, ResourceResponse aoResponse)
	{
		PortletSession loSession = aoRequest.getPortletSession();
		ModelAndView loModelAndView = null;
		String lsFormPath = ApplicationConstants.JSP_SUB_PROVIDER_SETTINGS;
		String lsStaffID = ApplicationConstants.EMPTY_STRING;
		String[] lsOrgIds = null;
		int liInsertRowCount = 0;
		PrintWriter loPrintWriter = null;
		String lsSubmitAccessRequestStatus = HHSConstants.AS_FAILURE;

		try
		{
			String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);
			lsStaffID = aoRequest.getParameter(ApplicationConstants.ATTRIBUTE_GET_STAFF_ID);
			lsOrgIds = aoRequest.getParameterValues(ApplicationConstants.ATTRIBUTE_GET_ITEMS);
			if (null != lsUserId && null != lsStaffID && null != lsOrgIds)
			{
				Map<String, Object> loParamMap = new HashMap<String, Object>();
				loParamMap.put(ApplicationConstants.CHANNEL_PARAM_STAFF_ID, lsStaffID);
				loParamMap.put(ApplicationConstants.ORG_ID, lsOrgIds);
				loParamMap.put(ApplicationConstants.CHANNEL_PARAM_CREATOR_USER, lsUserId);
				Channel loChannel = new Channel();
				loChannel.setData(ApplicationConstants.CHANNEL_ELEMET_SET_STAFF_ID, lsStaffID);
				loChannel.setData(ApplicationConstants.CHANNEL_SET_INSERT_DETAILS_MAP, loParamMap);
				TransactionManager.executeTransaction(loChannel,
						ApplicationConstants.TRANSACTION_SUBMIT_ACCESS_REQUEST_PROVIDER_SETTINGS);
				liInsertRowCount = (Integer) loChannel
						.getData(ApplicationConstants.CHANNEL_GET_INSERT_STATUS_SUBMIT_ACCESS_REQUEST);
				aoResponse.setContentType(HHSConstants.TEXT_HTML);
				loPrintWriter = aoResponse.getWriter();
				if (liInsertRowCount > 0)
				{
					StringBuffer loSuccessMsgBuffer = new StringBuffer();
					loSuccessMsgBuffer.append(HHSConstants.SUCCESS).append(HHSConstants.DELIMITER_SINGLE_HASH)
							.append(ApplicationConstants.KEY_SEPARATOR)
							.append(ApplicationConstants.MSG_PROVIDER_SETTINGS_SUCCESSFUL_SUBMIT_ACCESS_REQUEST)
							.append(HHSConstants.DELIMITER_SINGLE_HASH).append(ApplicationConstants.KEY_SEPARATOR);
					lsSubmitAccessRequestStatus = loSuccessMsgBuffer.toString();
					loPrintWriter.print(lsSubmitAccessRequestStatus);
					List<StaffDetails> loStaffDetailsList = (List<StaffDetails>) loChannel
							.getData(ApplicationConstants.CHANNEL_ELEMET_GET_STAFF_DETAILS_BEAN);
					if (null != loStaffDetailsList && !loStaffDetailsList.isEmpty())
					{
						populateProviderDetailProviderSettingsSubJsp(loStaffDetailsList, aoRequest);
					}
				}
				else{
					loPrintWriter.print(lsSubmitAccessRequestStatus);
				}
			}
		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error(ApplicationConstants.ERROR_MSG_PROVIDER_SETTINGS_SUCCESSFUL_SUBMIT_ACCESS_REQUEST + loEx);
			StringBuffer loFailureBuffer = new StringBuffer();
			loFailureBuffer.append(HHSConstants.AS_FAILURE).append(ApplicationConstants.KEY_SEPARATOR)
					.append(HHSConstants.ERROR_WHILE_PROCESSING_REQUEST);
			lsSubmitAccessRequestStatus = loFailureBuffer.toString();
			loPrintWriter.print(lsSubmitAccessRequestStatus);
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

	/**
	 * This method populates the Provider Details on Provider Setting Sub JSP
	 * Created for R4
	 * @param aoStaffDetailsList Staff DEtails List
	 * @param aoRequest Resource REquest
	 */
	protected void populateProviderDetailProviderSettingsSubJsp(List<StaffDetails> aoStaffDetailsList,
			ResourceRequest aoRequest)
	{
		String lsFname = aoStaffDetailsList.get(0).getMsStaffFirstName();
		String lsMname = aoStaffDetailsList.get(0).getMsStaffMidInitial();
		String lsLname = aoStaffDetailsList.get(0).getMsStaffLastName();
		aoRequest.setAttribute(ApplicationConstants.ATTRIBUTE_SET_FIRST_NAME, lsFname);
		aoRequest.setAttribute(ApplicationConstants.ATTRIBUTE_SET_MIDDLE_NAME, lsMname);
		aoRequest.setAttribute(ApplicationConstants.ATTRIBUTE_SET_LAST_NAME, lsLname);
		aoRequest.setAttribute(ApplicationConstants.STAFF_DETAILS_BEAN_LIST_PARAM, aoStaffDetailsList);
		Iterator<StaffDetails> loStaffDetailsListItr = aoStaffDetailsList.iterator();
		ArrayList<String> loExistingProviderList = new ArrayList<String>();
		while (loStaffDetailsListItr.hasNext())
		{
			StaffDetails loTempObj = loStaffDetailsListItr.next();
			loExistingProviderList.add(loTempObj.getMsOrgId());
		}
		aoRequest.setAttribute(ApplicationConstants.ATTRIBUTE_SET_EXISTING_PROVIDER_LIST, loExistingProviderList);
	}
}
