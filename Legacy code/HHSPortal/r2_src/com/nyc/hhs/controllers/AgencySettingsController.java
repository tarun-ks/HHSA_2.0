package com.nyc.hhs.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
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

import org.apache.axis.utils.StringUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.controllers.util.BaseControllerUtil;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.AgencySettingsBean;
import com.nyc.hhs.model.AutoApprovalConfigBean;
import com.nyc.hhs.model.CityUserDetailsBean;
import com.nyc.hhs.model.OrganizationBean;
import com.nyc.hhs.model.ProgramNameInfo;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.HHSPortalUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PortalUtil;
import com.nyc.hhs.util.PortletSessionHandler;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This controller is for Agency Settings module accessed by 'Agency and City'
 * users City users sets level of review for a particular agency --> particular
 * review process Agency user then assigns users across levels This class is
 * updated in R6 for Handling task of Return Payment.
 * This class has been updated in R7 for Modification Auto Approval Enhancement.
 */
@Controller(value = "agencySettings")
@RequestMapping("view")
public class AgencySettingsController extends BaseController
{

	/**
	 * Log object to record all logs
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(AgencySettingsController.class);

	/**
	 * <p>
	 * This method is default render method to display S404/S405 screen for city
	 * and agency users respectively It is called for agency setting module
	 * where accelerator user can assign review levels for particular agency and
	 * agency users can assign users to particular level of review
	 * </p>
	 * <ul>
	 * <li>For accelerator user it displays all agency names and review process</li>
	 * <li>For agency user it displays all review process to which they can
	 * assign users level wise</li>
	 * <li>Method updated for Release 3.2.0 enhancement 5684</li>
	 * </ul>
	 * @param aoRequest RenderRequest object
	 * @param aoResponse RenderResponse object
	 * @return loModelAndView ModelAndView as return type with jsp name and data
	 *         to be displayed dynamically
	 */
	@SuppressWarnings("unchecked")
	@RenderMapping
	protected ModelAndView handleRenderRequestInternal(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		String lsFormPath = null;
		ModelAndView loModelAndView = null;
		Map<String, Object> loAgencyUserMap = new HashMap<String, Object>();
		AgencySettingsBean loAgencySettingsBean = null;
		// Update in R6 for Return Payment: Added to check which JSP to
		// render(agencySetting or bulkNotification)
		String lsComponentName = HHSR5Constants.BULK_NOTIFICATION;
		String lsSettingsNameNT3 = HHSR5Constants.SAMPLE_NOTIFICATION_3;
		String lsSettingsNameNT4 = HHSR5Constants.SAMPLE_NOTIFICATION_4;
		String lsNavigationPath = null;
		try
		{
			PortletSession loSession = aoRequest.getPortletSession();
			lsFormPath = HHSConstants.AS_AGENCY_SETTINGS;
			String lsUserRole = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ROLE,
					PortletSession.APPLICATION_SCOPE);
			lsNavigationPath = PortalUtil.parseQueryString(aoRequest, HHSR5Constants.AGENCY_SETTING_TAB);
			// R6 End
			if (HHSConstants.AS_AGENCY_LOGIN.equalsIgnoreCase(PortalUtil.parseQueryString(aoRequest,
					HHSConstants.AS_AGENCY_SETTING_LOGIN)))
			{
				String lsUserType = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
						PortletSession.APPLICATION_SCOPE);
				// Start of changes or Release 3.2.0 enhancement 5684
				// If condition added for NT402
				// If user role is cfo redirect to agency setting else redirect
				// to agency home
				if (HHSConstants.NT402.equalsIgnoreCase(PortalUtil.parseQueryString(aoRequest,
						HHSConstants.NOTIFICATION_GENERATED_FROM)))
				{
					if (lsUserRole.equalsIgnoreCase(HHSConstants.CFO_ROLE))
					{
						lsFormPath = HHSConstants.AS_AGENCY_SETTINGS;
					}
					else
					{
						lsFormPath = HHSConstants.REDIRECT_HOME_FROM_NOTIFICATION;
						aoRequest.setAttribute(HHSConstants.ROLE_ATT, lsUserRole);
						aoRequest.setAttribute(HHSConstants.USER_TYPE_ATTR, lsUserType);
					}
				}
				else
				{
					lsFormPath = HHSConstants.AS_AGENCY_SETTINGS;
				}
				// End of changes or Release 3.2.0 enhancement 5684
				loAgencySettingsBean = getAllReviewProcessData();
				// Start:Added in R6 for Return Payment.
				bulkNotificationDetails(aoRequest);
				// End:Added in R6 for Return Payment
			}
			else
			{
				// Start : Added in R7 for Auto Approve MOdification
				// Configuration
				String configureAutoApproval = HHSPortalUtil.parseQueryString(aoRequest, HHSR5Constants.AS_CITY_RENDER_AUTO_APPROVAL);
				if (HHSConstants.STRING_TRUE.equals(configureAutoApproval))
				{
					lsFormPath = HHSR5Constants.AS_CITY_CONFIGURE_AUTO_APPROVAL;
					loAgencySettingsBean = getAgencyList();
				}
				else
				{
					lsFormPath = HHSConstants.AS_CITY_AGENCY_SETTINGS;
					loAgencySettingsBean = getAgencyAndReviewProcessData();
				}
				// End : Added in R7 for Auto Approve MOdification Configuration

				// Start:Added in R6 for Return Payment.
				bulkNotificationDetails(aoRequest);
				// End:Added in R6 for Return Payment
			}
			// This is Added in R6 for Return Payment to navigate between Bulk
			// Notifications and Agency
			// Settings.
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
			String lsSampleTextNT3 = (String) loApplicationSettingMap.get(lsComponentName + HHSConstants.UNDERSCORE
					+ lsSettingsNameNT3);
			String lsSampleTextNT4 = (String) loApplicationSettingMap.get(lsComponentName + HHSConstants.UNDERSCORE
					+ lsSettingsNameNT4);
			aoRequest.setAttribute(HHSR5Constants.STRING_SAMPLE_NOTIFICATION, lsSampleTextNT3);
			aoRequest.setAttribute(HHSR5Constants.SAMPLE_INFORMATION, lsSampleTextNT4);
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
			LOG_OBJECT.Error("Error occured while processing agency settings", loAppEx);
		}
		catch (Exception loEx)
		{
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_STATUS, HHSConstants.AS_FAILED);
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_MSG, HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED);
			LOG_OBJECT.Error("Error occured while processing agency settings", loEx);
		}
		loAgencyUserMap.put(HHSConstants.AS_AGENCY_SETTING_BEAN, loAgencySettingsBean);
		loModelAndView = new ModelAndView(lsFormPath, loAgencyUserMap);
		return loModelAndView;
	}

	// ***BELOW ARE CITY USER USER METHODS**************
	/**
	 * <p>
	 * This method is called by accelerator users on page load to fetch all
	 * agency names and all review process from database. Accelerator user can
	 * select any review process under an agency and can associate no of review
	 * levels that needs to be done for the selected review process task
	 * </p>
	 * <ul>
	 * <li>Fetch all agency names</li>
	 * <li>Fetch all review process task</li>
	 * </ul>
	 * @return loAgencySettingsBean AgencySettingsBean - bean containing list of
	 *         all agency names and list of all review process
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	private AgencySettingsBean getAgencyAndReviewProcessData() throws ApplicationException
	{
		Channel loChannelObj = new Channel();
		AgencySettingsBean loAgencySettingsBean = null;
		HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.AS_GET_AGENCY_REVIEW_PROCESS_DATA);
		loAgencySettingsBean = (AgencySettingsBean) loChannelObj.getData(HHSConstants.AS_AGENCY_SETTING_BEAN_OBJ);
		return loAgencySettingsBean;
	}

	/**
	 * <p>
	 * This method is called called by accelerator user as an ajax call. When
	 * user selects an agency and then selects review process and hit 'Go'
	 * button then it retrieves levels of review that accelerator user has set
	 * for a particular review process under particular agency.
	 * </p>
	 * <ul>
	 * <li>With agency id and review process id, database call is made to fetch
	 * review levels if it exist</li>
	 * <li>fetched review levels are set in print writer and retrieved in jsp</li>
	 * <li>Dynamically radio button is enabled based upon review levels that is
	 * set</li>
	 * <li>If no review levels are set earlier then no radio button is selected</li>
	 * </ul>
	 * @param aoResourceRequest ResourceRequest object
	 * @param aoResourceResponse ResourceResponse object
	 */
	@ResourceMapping("fetchLevelsOfReview")
	public void fetchReviewLevels(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		PrintWriter loPrintWriter = null;
		int liReviewLevelsAssgnd = HHSConstants.INT_ONE;
		try
		{
			aoResourceResponse.setContentType(HHSConstants.TEXT_HTML);
			String lsAgencyId = (String) aoResourceRequest.getParameter(HHSConstants.AS_HIDDEN_AGENCY_ID);
			String lsReviewProcId = (String) aoResourceRequest.getParameter(HHSConstants.AS_HIDDEN_REVIEW_PROC_ID);
			int liReviewProcId = Integer.parseInt(lsReviewProcId);

			Channel loChannelObj = new Channel();
			loChannelObj.setData(HHSConstants.AS_AGENCY_ID, lsAgencyId);
			loChannelObj.setData(HHSConstants.AI_REVIEW_PROC_ID, liReviewProcId);

			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.GET_REVIEW_LEVELS);
			liReviewLevelsAssgnd = (Integer) loChannelObj.getData(HHSConstants.AI_REVIEW_LEVELS);

			loPrintWriter = aoResourceResponse.getWriter();
			aoResourceRequest.getPortletSession().setAttribute(HHSConstants.OLD_REVIEW_LEVELS, liReviewLevelsAssgnd,
					PortletSession.APPLICATION_SCOPE);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Error occured while fetching review levels", loAppEx);
		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Error occured while fetching review levels", loEx);
		}
		finally
		{
			if (null != loPrintWriter)
			{
				StringBuffer loStringBuffer = new StringBuffer();
				loStringBuffer.append(HHSConstants.AS_REVIEW_LEVEL).append(liReviewLevelsAssgnd);
				loPrintWriter.print(loStringBuffer.toString());
				loPrintWriter.flush();
				loPrintWriter.close();
			}
		}
	}

	/**
	 * This Method modified as a part of release 3.8.0 enhancement 6534
	 * <ul>
	 * This method Enable Change of Task Levels of Approvals for all Tasks
	 * Regardless of Whether there are Tasks Inflight
	 * </ul>
	 * <p>
	 * This method is called called by accelerator user as an ajax call. When
	 * user selects an agency and then selects review process and hit 'Go'
	 * button then it retrieves levels of review that accelerator user has set
	 * for a particular review process under particular agency. Here the user
	 * can select any level of review and hit on save button to save levels of
	 * review
	 * </p>
	 * <ul>
	 * <li>With agency id and review process id, database call is made to save
	 * review levels</li>
	 * <li>If no review levels are set earlier then insert call is made to
	 * database</li>
	 * <li>If review levels are set earlier then update call is made to database
	 * </li>
	 * </ul>
	 * @param aoResourceRequest ResourceRequest object
	 * @param aoResourceResponse ResourceResponse object
	 */
	@SuppressWarnings("unchecked")
	@ResourceMapping("saveLevelsOfReview")
	public void saveReviewLevels(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		PrintWriter loPrintWriter = null;
		String lsTransactionStatusMsg = HHSConstants.AS_FAILURE + HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
		try
		{
			aoResourceResponse.setContentType(HHSConstants.TEXT_HTML);
			String lsAgencyId = (String) aoResourceRequest.getParameter(HHSConstants.AS_HIDDEN_AGENCY_ID);
			String lsReviewProcId = (String) aoResourceRequest.getParameter(HHSConstants.AS_HIDDEN_REVIEW_PROC_ID);
			String lsReviewLevels = (String) aoResourceRequest.getParameter(HHSConstants.HDN_REVIEW_LEVELS);
			int liReviewProcId = Integer.parseInt(lsReviewProcId);
			int liReviewLevels = Integer.parseInt(lsReviewLevels);

			String lsUserId = (String) aoResourceRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			// START || Change done for enhancement 6534 with Release 3.8.0
			String lsTaskType = (String) aoResourceRequest.getParameter(HHSConstants.TASK_TYPE);
			if (lsTaskType.equalsIgnoreCase(HHSConstants.TASK_CONTRACT_CONFIGURATION))
			{
				lsTaskType = HHSConstants.TASK_CONTRACT_CONFIGURATION_FULL;
			}
			Channel loChannel = new Channel();
			HashMap<String, String> loAgencyDetailsMap = new HashMap<String, String>();
			loAgencyDetailsMap.put(HHSConstants.TASK_TYPE, lsTaskType);
			loAgencyDetailsMap.put(HHSConstants.AGENCYID, lsAgencyId);
			loChannel.setData(HHSConstants.AGENCY_DETAILS_MAP, loAgencyDetailsMap);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_REVIEW_PROGRESS_FLAG);
			String loReviewInProgressFlag = (String) loChannel.getData(HHSConstants.REVIEW_PROGRESS_FLAG);
			loPrintWriter = aoResourceResponse.getWriter();
			if (null != loReviewInProgressFlag && loReviewInProgressFlag.equalsIgnoreCase(HHSConstants.YES))
			{
				aoResourceRequest.getPortletSession().setAttribute(HHSConstants.OLD_REVIEW_LEVELS, liReviewLevels,
						PortletSession.APPLICATION_SCOPE);
				lsTransactionStatusMsg = HHSConstants.AS_FAILURE
						+ PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGE_PROP_FILE,
								HHSConstants.ERROR_MESSSAGE_REVIEW_LEVEL);
			}
			else
			{
				// END || Change done for enhancement 6534 with Release 3.8.0
				Channel loChannelObj = new Channel();
				AgencySettingsBean loAgencySettingsBean = new AgencySettingsBean();
				loAgencySettingsBean.setAgencyId(lsAgencyId);
				loAgencySettingsBean.setReviewProcessId(liReviewProcId);
				loAgencySettingsBean.setLevelOfReview(liReviewLevels);
				loAgencySettingsBean.setCreatedByUserId(lsUserId);
				loAgencySettingsBean.setModifiedByUserId(lsUserId);
				int liOldLevelsofReview = (Integer) aoResourceRequest.getPortletSession().getAttribute(
						HHSConstants.OLD_REVIEW_LEVELS, PortletSession.APPLICATION_SCOPE);
				loAgencySettingsBean.setOldLevelOfReview(liOldLevelsofReview);

				P8UserSession loUserSession = (P8UserSession) aoResourceRequest.getPortletSession().getAttribute(
						ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
				loChannelObj.setData(HHSConstants.AS_AGENCY_SETTING_BEAN_OBJ, loAgencySettingsBean);
				loChannelObj.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
				HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.AS_SET_REVIEW_LEVELS);
				Map<String, Object> loSaveReviewLevRetrndMap = (Map<String, Object>) loChannelObj
						.getData(HHSConstants.AS_AO_SAVE_REVIEW_LEVEL_RETURNED_MAP);
				Boolean lbSaveStatus = (Boolean) loSaveReviewLevRetrndMap.get(HHSConstants.SAVE_STATUS);
				// Below code commented as a part of release 3.8.0 enhancement
				// 6534
				/*
				 * if (null != lbSaveStatus && lbSaveStatus == false) { Integer
				 * liOpenTaskCount = (Integer)
				 * loSaveReviewLevRetrndMap.get(HHSConstants
				 * .AS_OPEN_TASK_COUNT); if (null != liOpenTaskCount &&
				 * liOpenTaskCount != HHSConstants.INT_ZERO) {
				 * lsTransactionStatusMsg = HHSConstants.AS_FAILURE +
				 * PropertyLoader
				 * .getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
				 * HHSConstants.PROPERTY_TASK_IN_OPEN_STATUS);
				 * lsTransactionStatusMsg = HHSConstants.AS_SUCCESS +
				 * PropertyLoader
				 * .getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
				 * HHSConstants.PROPERTY_REVIEW_LEVELS_SAVED); } }
				 */
				if (null != lbSaveStatus && lbSaveStatus == true)
				{
					aoResourceRequest.getPortletSession().setAttribute(HHSConstants.OLD_REVIEW_LEVELS, liReviewLevels,
							PortletSession.APPLICATION_SCOPE);
					lsTransactionStatusMsg = HHSConstants.AS_SUCCESS
							+ PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
									HHSConstants.PROPERTY_REVIEW_LEVELS_SAVED);
				}
			}
		}
		catch (ApplicationException loAppEx)
		{
			lsTransactionStatusMsg = HHSConstants.AS_FAILURE + HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("Error occured while saving review levels", loAppEx);
		}
		catch (Exception loEx)
		{
			lsTransactionStatusMsg = HHSConstants.AS_FAILURE + HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("Error occured while saving review levels", loEx);
		}
		finally
		{
			if (null != loPrintWriter)
			{
				StringBuffer loStringBuffer = new StringBuffer();
				loStringBuffer.append(lsTransactionStatusMsg);
				loPrintWriter.print(loStringBuffer.toString());
				loPrintWriter.flush();
				loPrintWriter.close();
				loStringBuffer = null;
			}
		}
	}

	// ***BELOW ARE AGENCY USER METHODS*****************
	/**
	 * <p>
	 * This method is called by agency users on page load to fetch all review
	 * process from database. Agency user can select any review process and hit
	 * on "Go" button which will fetch all assigned users level wise. Levels are
	 * based upon the review levels that accelerator user has set for this
	 * agency and this review process.
	 * </p>
	 * <ul>
	 * <li>Fetch all review process task</li>
	 * </ul>
	 * @return loAgencySettingsBean AgencySettingsBean - bean containing list of
	 *         all review process
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	private AgencySettingsBean getAllReviewProcessData() throws ApplicationException
	{
		Channel loChannelObj = new Channel();
		AgencySettingsBean loAgencySettingsBean = null;
		HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.GET_ALL_REVIEW_PROCESS_DATA);
		loAgencySettingsBean = (AgencySettingsBean) loChannelObj.getData(HHSConstants.AS_AGENCY_SETTING_BEAN_OBJ);
		return loAgencySettingsBean;
	}

	/**
	 * <p>
	 * This method is called by agency users as an ajax call to fetch all
	 * assigned users. Users are retrieved level wise users level wise. Levels
	 * are based upon the review levels that accelerator user has set for this
	 * agency and this review process. This method returns model and view which
	 * is further included in the main jsp
	 * </p>
	 * <ul>
	 * <li>Levels are set by accelerator users, based upon it review levels are
	 * shown</li>
	 * <li>Users are retrieved level wise</li>
	 * <li>Agency user can reassign users on this screen and save settings</li>
	 * </ul>
	 * @param aoResourceRequest ResourceRequest object
	 * @param aoResourceResponse ResourceResponse object
	 * @return ModelAndView ModelAndView object
	 */
	@ResourceMapping("getAgencySetAssgndUsrInfo")
	protected ModelAndView getAgencySetAssgndUsrInfo(ResourceRequest aoResourceRequest,
			ResourceResponse aoResourceResponse)
	{
		String lsFormPath = null;
		ModelAndView loModelAndView = null;
		AgencySettingsBean loAgencySettingsBean = null;
		PrintWriter loPrintWriter = null;
		String lsTransactionStatusMsg = HHSConstants.AS_FAILURE + HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
		try
		{
			aoResourceResponse.setContentType(HHSConstants.TEXT_HTML);
			Channel loChannelObj = new Channel();
			String lsReviewProcessId = (String) aoResourceRequest
					.getParameter(HHSConstants.AS_HIDDEN_REVIEW_PROCESS_ID);
			String lsConfigFlag = (String) aoResourceRequest.getParameter(HHSConstants.AS_HIDDEN_CONFIG_FLAG);
			int liReviewProcessId = Integer.parseInt(lsReviewProcessId);
			String lsAgencyId = (String) aoResourceRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);
			String lsUserId = (String) aoResourceRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			loChannelObj.setData(HHSConstants.AS_USER_ID, lsUserId);
			loChannelObj.setData(HHSConstants.AS_AI_REVIEW_PROCESS_ID, liReviewProcessId);
			loChannelObj.setData(HHSConstants.AS_AGENCY_ID, lsAgencyId);
			loChannelObj.setData(HHSConstants.AS_CONFIG_FLAG, lsConfigFlag);
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.AS_GET_AGENCY_SET_ASSGND_USR_DATA);
			loAgencySettingsBean = (AgencySettingsBean) loChannelObj.getData(HHSConstants.AS_AGENCY_SETTING_BEAN_OBJ);
			if (loAgencySettingsBean.getLevelOfReview() == HHSConstants.INT_ZERO)
			{
				lsTransactionStatusMsg = HHSConstants.AS_FAILURE
						+ PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
								HHSConstants.PROPERTY_LEVELS_OF_REVIEW_NOT_SET);
				StringBuffer loStringBuffer = new StringBuffer();
				loPrintWriter = aoResourceResponse.getWriter();
				loStringBuffer.append(lsTransactionStatusMsg);
				loPrintWriter.print(loStringBuffer.toString());
			}
			else
			{
				lsFormPath = HHSConstants.AS_AGENCY_SET_LEVEL_USER;
				aoResourceRequest.getPortletSession().setAttribute(HHSConstants.AS_AGENCY_SETTINGS_BEAN,
						loAgencySettingsBean, PortletSession.APPLICATION_SCOPE);
				aoResourceRequest.getPortletSession().setAttribute(HHSConstants.AS_LEVEL_OF_REVIEW_ASSGND,
						loAgencySettingsBean.getLevelOfReview(), PortletSession.APPLICATION_SCOPE);
			}
		}
		catch (ApplicationException loAppEx)
		{
			lsTransactionStatusMsg = HHSConstants.AS_FAILURE + HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("Error occured while fetching assigned users", loAppEx);
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

	/**
	 * <p>
	 * Changes made for enhancement 6534 for Release 3.8.0 This method is called
	 * by agency users as an ajax call to save newly assigned/changed level
	 * users. Based upon the review levels set by accelerator user for a
	 * particular review task, agency user can view review levels and can
	 * associate users to a particular review level. These assigned users can
	 * take action on any workflow of a this particular task
	 * </p>
	 * <ul>
	 * <li>Agency user can reassign users on this screen and save settings</li>
	 * <li>All new users are received as hidden parameter delimited by '|'
	 * symbol</li>
	 * <li>Separate list are formed from level1 to level4 users</li>
	 * <li>These new lists are set in AgencySettingsBean</li>
	 * <li>Transaction setAgencyLevelUsers is called with old and new
	 * AgencySettingsBean</li>
	 * <li>In service class old and new bean are compared and segregated into
	 * insert and delete list from level1 to level4 for database operation</li>
	 * </ul>
	 * @param aoResourceRequest ResourceRequest object
	 * @param aoResourceResponse ResourceResponse object
	 */
	@ResourceMapping("saveAgencyLevelUsers")
	protected void saveAgencyLevelUsers(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		String lsTransactionStatusMsg = HHSConstants.AS_FAILURE + HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
		PrintWriter loPrintWriter = null;
		StringBuffer loStringBuffer = new StringBuffer();
		AgencySettingsBean loNewAgencySettingsBean = new AgencySettingsBean();
		AgencySettingsBean loOldAgencySettingsBean = null;
		try
		{
			aoResourceResponse.setContentType(HHSConstants.TEXT_HTML);
			Channel loChannelObj = new Channel();
			String lsUserId = (String) aoResourceRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			loOldAgencySettingsBean = (AgencySettingsBean) aoResourceRequest.getPortletSession().getAttribute(
					HHSConstants.AS_AGENCY_SETTINGS_BEAN, PortletSession.APPLICATION_SCOPE);

			int liLevelOfReviewAssigned = (Integer) aoResourceRequest.getPortletSession().getAttribute(
					HHSConstants.AS_LEVEL_OF_REVIEW_ASSGND, PortletSession.APPLICATION_SCOPE);
			String lsRevProcessId = (String) aoResourceRequest.getParameter(HHSConstants.AS_HIDDEN_REVIEW_PROC_ID);
			int liReviewProcId = Integer.parseInt(lsRevProcessId);

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

			aoResourceRequest.getPortletSession().setAttribute(HHSConstants.AS_AGENCY_SETTINGS_BEAN,
					loNewAgencySettingsBean, PortletSession.APPLICATION_SCOPE);
			loPrintWriter = aoResourceResponse.getWriter();
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.AS_SET_AGENCY_LEVEL_USERS);
			// START || Changes made for enhancement 6534 for Release 3.8.0
			Boolean loSaveStatus = (Boolean) loChannelObj.getData(HHSConstants.USER_SAVE_STATUS);
			if (!loSaveStatus)
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
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Error occured while processing save agency level users", loAppEx);
		}
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
	 * This is a private method to set new users list that agency user has
	 * changed on S405 screen This method reads hidden parameters from request
	 * and set them to AgencySettingsBean
	 * </p>
	 * <ul>
	 * <li>Reads all users and level1 to level4 users from request</li>
	 * <li>Users are received as hidden parameter delimited by '|' symbol</li>
	 * <li>Separate list are formed from level1 to level4 users</li>
	 * <li>Call populateCityUserDetailBean() that returns list of
	 * CityUserDetailBean</li>
	 * <li>These new lists are set in AgencySettingsBean</li>
	 * </ul>
	 * @param aoResourceRequest ResourceRequest object
	 * @param aoNewAgencySettingsBean AgencySettingsBean object
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 * @return AgencySettingsBean AgencySettingsBean object
	 */
	private AgencySettingsBean setNewUsersList(ResourceRequest aoResourceRequest,
			AgencySettingsBean aoNewAgencySettingsBean) throws ApplicationException
	{
		AgencySettingsBean loNewAgencySettingsBean = new AgencySettingsBean();
		String lsAllUserNames = (String) aoResourceRequest.getParameter(HHSConstants.AS_HIDDEN_ALL_USERS);
		String lsLevel1UserNames = (String) aoResourceRequest.getParameter(HHSConstants.AS_HIDDEN_LEV1_USERS);
		String lsLevel2UserNames = (String) aoResourceRequest.getParameter(HHSConstants.AS_HIDDEN_LEV2_USERS);
		String lsLevel3UserNames = (String) aoResourceRequest.getParameter(HHSConstants.AS_HIDDEN_LEV3_USERS);
		String lsLevel4UserNames = (String) aoResourceRequest.getParameter(HHSConstants.AS_HIDDEN_LEV4_USERS);

		String lsAllUserArr[] = HHSUtil.convertStringToArray(lsAllUserNames);
		String lsLev1UserArr[] = HHSUtil.convertStringToArray(lsLevel1UserNames);
		String lsLev2UserArr[] = HHSUtil.convertStringToArray(lsLevel2UserNames);
		String lsLev3UserArr[] = HHSUtil.convertStringToArray(lsLevel3UserNames);
		String lsLev4UserArr[] = HHSUtil.convertStringToArray(lsLevel4UserNames);

		// Below converts string array to list
		List<String> loNewAllUserList = Arrays.asList(lsAllUserArr);
		List<String> loNewLev1UserList = Arrays.asList(lsLev1UserArr);
		List<String> loNewLev2UserList = Arrays.asList(lsLev2UserArr);
		List<String> loNewLev3UserList = Arrays.asList(lsLev3UserArr);
		List<String> loNewLev4UserList = Arrays.asList(lsLev4UserArr);

		loNewAgencySettingsBean.setAllAgencyUsersList(populateCityUserDetailBean(HHSConstants.INT_ZERO,
				loNewAllUserList));
		loNewAgencySettingsBean.setAllLevel1UsersList(populateCityUserDetailBean(HHSConstants.INT_ONE,
				loNewLev1UserList));
		loNewAgencySettingsBean.setAllLevel2UsersList(populateCityUserDetailBean(HHSConstants.INT_TWO,
				loNewLev2UserList));
		loNewAgencySettingsBean.setAllLevel3UsersList(populateCityUserDetailBean(HHSConstants.INT_THREE,
				loNewLev3UserList));
		loNewAgencySettingsBean.setAllLevel4UsersList(populateCityUserDetailBean(HHSConstants.INT_FOUR,
				loNewLev4UserList));
		return loNewAgencySettingsBean;
	}

	/**
	 * <p>
	 * This is a private method that receives level id and list of users. It
	 * further reads whole list and for each element create CityUserDetailsBean
	 * object
	 * </p>
	 * <ul>
	 * <li>Gets list of user and their level as input parameter</li>
	 * <li>Associate iterator with list and create new bean each time of type
	 * CityUserDetailsBean</li>
	 * <li>set level id and user id and add to CityUserDetailsBean List</li>
	 * <li>Once whole list is iterated, return list of CityUserDetailsBean</li>
	 * <li>These new lists are set in AgencySettingsBean</li>
	 * </ul>
	 * @param aiLevelId Level id of users
	 * @param aoUserList UserList level wise
	 * @return List<CityUserDetailsBean> list of type <CityUserDetailsBean>
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	private List<CityUserDetailsBean> populateCityUserDetailBean(int aiLevelId, List<String> aoUserList)
			throws ApplicationException
	{
		CityUserDetailsBean loTempCityUsrBeanDet = null;
		List<CityUserDetailsBean> loCityUserDetailsBeanList = new ArrayList<CityUserDetailsBean>();

		if (null != aoUserList && !aoUserList.isEmpty() && aoUserList.size() > HHSConstants.INT_ZERO)
		{
			Iterator<String> loItr = aoUserList.iterator();
			while (loItr.hasNext())
			{
				loTempCityUsrBeanDet = new CityUserDetailsBean();
				loTempCityUsrBeanDet.setLevelId(aiLevelId);
				loTempCityUsrBeanDet.setUserId(loItr.next());

				loCityUserDetailsBeanList.add(loTempCityUsrBeanDet);
			}
		}
		return loCityUserDetailsBeanList;
	}

	/**
	 * This method is added in R6 for Return Payment. This method will fetch the
	 * details of bulk notifications screen.
	 * @param aoRequest
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void bulkNotificationDetails(RenderRequest aoRequest) throws ApplicationException
	{
		Channel loChannelObj = new Channel();
		try
		{
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);
			String lsOrgnizationType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			loChannelObj.setData(HHSConstants.ORG_ID, lsUserId);
			loChannelObj.setData(HHSConstants.AS_ORG_TYPE, lsOrgnizationType);
			HHSTransactionManager.executeTransaction(loChannelObj, HHSR5Constants.GET_BULK_NOTIFICATION_BEAN,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			aoRequest.setAttribute(HHSR5Constants.LO_FISCAL_YEAR,
					(List<String>) loChannelObj.getData(HHSR5Constants.AO_FISCAL_YEAR));
			aoRequest.setAttribute(HHSConstants.PY_LO_PROGRAM_NAME_LIST,
					(List<ProgramNameInfo>) loChannelObj.getData(HHSConstants.PY_LO_PROGRAM_NAME_LIST));
		}
		catch (ApplicationException loApx)
		{
			LOG_OBJECT.Error("Error while fetching bulkNotificationDetails  :", loApx);
			throw loApx;
		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Error while fetching bulkNotificationDetails  :", loEx);
			throw new ApplicationException(
					"Error while fetching bulkNotificationDetails :", loEx);
		}
	}

	/**
	 * This method is Added in R6 for Return Payment. This method will perform
	 * action when bulk notifications action or export bulk task action is
	 * selected.
	 * @param aoActionRequest
	 * @param aoActionResponse
	 * @throws ApplicationException
	 * @throws IOException
	 */
	@ActionMapping(params = "submit_action=sendNotificationAlert")
	public void sendBulkNotifications(ActionRequest aoActionRequest, ActionResponse aoActionResponse)
			throws ApplicationException, IOException
	{
		Map<String, String> loRequestInput = new HashMap<String, String>();
		boolean lbResponse = Boolean.FALSE;
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
			loRequestInput.put(HHSConstants.PROGRAM_NAME, aoActionRequest.getParameter(HHSConstants.PROGRAM_NAME));
			loRequestInput.put(
					HHSConstants.USER_ORG,
					(String) aoActionRequest.getPortletSession().getAttribute(
							ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE));
			String lsActionSelected = aoActionRequest.getParameter(HHSR5Constants.ACTION_SELECTED);
			loRequestInput.put(HHSR5Constants.ACTION_SELECTED, lsActionSelected);
			if (null != lsActionSelected && lsActionSelected.equalsIgnoreCase(HHSConstants.SEND_NOTFICATION))
			{
				lbResponse = sendBulkNotificationsAction(loRequestInput);
				if (lbResponse)
				{
					aoActionResponse.setRenderParameter(HHSConstants.STATUS_COLUMN, HHSConstants.AS_PASSED);
				}
				else
				{
					aoActionResponse.setRenderParameter(HHSConstants.STATUS_COLUMN, HHSConstants.AS_FAILED);
				}
			}
			else if (null != lsActionSelected)
			{
				lbResponse = exportSentNotificationsAction(loRequestInput);
				if (lbResponse)
				{
					aoActionResponse.setRenderParameter(HHSConstants.STATUS_COLUMN, HHSConstants.AS_PASSED);
				}
				else
				{
					aoActionResponse.setRenderParameter(HHSConstants.STATUS_COLUMN, HHSConstants.AS_FAILED);
				}
			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Error while Executing Notification Request :", loAppEx);
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error while Executing Notification Request :", loExp);
		}
	}

	/**
	 * Method Added in R6 for Return Payment. This method will handle task for
	 * Export Bulk notification.<br>
	 * <b>transaction:</b> insertPendingBulkNotificationStatus
	 * @param aoRequestInput
	 * @return insert Status
	 * @throws Exception
	 */
	private boolean exportSentNotificationsAction(Map<String, String> aoRequestInput) throws ApplicationException
	{
		Channel loChannel = new Channel();
		boolean lbResponse = Boolean.FALSE;
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		try
		{
			loHashMap.put(HHSConstants.USER_ID, aoRequestInput.get(HHSR5Constants.USER_ID));
			loHashMap.put(HHSConstants.FISCAL_YEAR, aoRequestInput.get(HHSR5Constants.FISCAL_YEAR));
			loHashMap.put(HHSConstants.STATUS_COLUMN, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSR5Constants.EXPORT_NOTIFICATIONS_NOT_STARTED));
			loHashMap.put(HHSConstants.PROGRAM_ID, aoRequestInput.get(HHSConstants.PROGRAM_ID));
			loChannel.setData(HHSR5Constants.EXPORT_MAP, loHashMap);
			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.REQUEST_EXPORT_NOTIFICATION,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);

			lbResponse = (Boolean) loChannel.getData(HHSR5Constants.EXPORT_NOTIFICATIONS_STATUS);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Error while Inserting Details for Export Request :", loAppEx);
			throw loAppEx;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error while Inserting Details for Export Request :", loExp);
			throw new ApplicationException(
					"Error while Inserting Details for Export Request :", loExp);
		}
		return lbResponse;
	}

	//R7 start :Modification Auto Approval Enhancement
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

	/**
	 * <p>
	 * This method is added in R7
	 * This method is called by city user as an ajax call. When user selects an
	 * agency and hit 'Go' button then it
	 * retrieves default threshold value that has been set 
	 * selected agency.
	 * </p>
	 * <ul>
	 * <li>With agency id, database call is made to fetch
	 * threshold value if it exist</li>
	 * <li>fetched threshold value is set in print writer and retrieved in jsp</li>
	 * <li>If no threshold value is set earlier then 0 value is displayed in
	 * jsp</li>
	 * </ul>
	 * @param aoResourceRequest ResourceRequest object
	 * @param aoResourceResponse ResourceResponse object
	 * @return ModelAndView  
	 */
	@SuppressWarnings("unchecked")
	@ResourceMapping("fetchAutoApprovalThreshold")
	public ModelAndView fetchAutoApprovalThreshold(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		String lsAgencyId = aoResourceRequest.getParameter(HHSConstants.AS_HIDDEN_AGENCY_ID);
		Channel loChannelObj = new Channel();
		loChannelObj.setData(HHSConstants.AS_AGENCY_ID, lsAgencyId);
		getModificationAutoApprovalThreshold(loChannelObj, aoResourceRequest, aoResourceResponse);
		
		return new ModelAndView(HHSR5Constants.AGENCY_PROV_CUSTOM_AUTO_CONFIG);
	}
	
	@SuppressWarnings("unchecked")
	private void getModificationAutoApprovalThreshold(Channel loChannelObj, ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse){
		
		AutoApprovalConfigBean loDefaultThresholdBean;
		List<OrganizationBean> loAgencyProviders = new ArrayList<OrganizationBean>() ;
		List<OrganizationBean> loAgencyProvidersList;
		String loProviders = null;
		try{
		HHSTransactionManager.executeTransaction(loChannelObj, HHSR5Constants.GET_MODIFICATION_AUTO_APPROVAL_THRESHOLD);
		
		loDefaultThresholdBean =(AutoApprovalConfigBean) loChannelObj.getData(HHSR5Constants.DEFAULT_THRESHOLD_BEAN);
		loAgencyProvidersList = (List<OrganizationBean>) loChannelObj.getData(HHSR5Constants.AGENCY_PROVIDERS);
		if(loAgencyProvidersList.isEmpty()) {
			OrganizationBean orgBean = new OrganizationBean();
			orgBean.setMsOrgLegalName(HHSR5Constants.SELECT_PROVIDER);
			loAgencyProviders.add(orgBean);
		} else {
			for (OrganizationBean organizationBean : loAgencyProvidersList)
			{
				if(organizationBean.getMsOrgLegalName().contains("\"")){
					String orgLegalName = organizationBean.getMsOrgLegalName().replace("\"", "'");
					organizationBean.setMsOrgLegalName(orgLegalName);
				}
				loAgencyProviders.add(organizationBean);
			}
		}
		
		aoResourceRequest.setAttribute(HHSR5Constants.DEFAULT_AUTO_CONFIG_THRESHOLD,loDefaultThresholdBean);
		if(!loAgencyProviders.isEmpty()){
			loProviders = HHSUtil.convertListToGridDropDown(loAgencyProviders, aoResourceRequest);
		}
		aoResourceRequest.setAttribute(HHSR5Constants.PROVIDER_LIST,loProviders);
		aoResourceRequest.setAttribute(HHSConstants.AS_AGENCY_ID,loChannelObj.getData(HHSConstants.AS_AGENCY_ID));
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Error occured while fetching threshold value", loAppEx);
		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Error occured while fetching threshold value", loEx);
		}
	}

	/**
	 * <p>
	 * This method is added in R7
	 * This method is called by city user as an ajax call. When user enters the
	 * threshold value for a particular agency and hit
	 * 'Save' button then it saves the threshold value city user has set for a
	 * particular agency.
	 * </p>
	 * <ul>
	 * <li>With agency id and threshold value database call
	 * is made to save threshold value</li>
	 * @param aoResourceRequest ResourceRequest object
	 * @param aoResourceResponse ResourceResponse object
	 */
	@SuppressWarnings("unchecked")
	@ResourceMapping("saveAutoApprovalThreshold")
	public ModelAndView saveAutoApprovalThreshold(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		PrintWriter loPrintWriter = null;
		String lsTransactionStatusMsg = HHSConstants.AS_FAILURE + HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
		String lsThresholdValue = "";
		int ldThresholdValue = HHSConstants.INT_ZERO;
		AutoApprovalConfigBean loDefaultThresholdBean;
		List<OrganizationBean> loAgencyProviders = new ArrayList<OrganizationBean>() ;
		List<OrganizationBean> loAgencyProvidersList;
		String loProviders = null;
		Boolean lbStatus = false;
		try
		{
			aoResourceResponse.setContentType(HHSConstants.TEXT_HTML);
			String lsAgencyId = aoResourceRequest.getParameter(HHSConstants.AS_HIDDEN_AGENCY_ID);
			String lsUserId = (String) aoResourceRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			lsThresholdValue = aoResourceRequest.getParameter(HHSR5Constants.AI_HIDDEN_THRESHOLD_VALUE);
			loProviders = aoResourceRequest.getParameter(HHSR5Constants.AGENCY_PROVIDERS);
			Channel loChannelObj = new Channel();
			loChannelObj.setData(HHSConstants.AS_AGENCY_ID, lsAgencyId);
			
			if (null==lsThresholdValue || lsThresholdValue.equals(HHSConstants.EMPTY_STRING))
			{
				lsThresholdValue=HHSConstants.STRING_ZERO;
				lsTransactionStatusMsg = HHSConstants.AS_FAILURE
						+ PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
								HHSR5Constants.PROPERTY_THRESHOLD_VALUE_EMPTY);
				getModificationAutoApprovalThreshold(loChannelObj, aoResourceRequest, aoResourceResponse);
				loPrintWriter = aoResourceResponse.getWriter();
				return new ModelAndView(HHSR5Constants.AGENCY_PROV_CUSTOM_AUTO_CONFIG);
			}

			ldThresholdValue = Integer.parseInt(lsThresholdValue);
			if (ldThresholdValue < HHSConstants.INT_ZERO || ldThresholdValue > HHSConstants.INT_HUNDRED)
			{
				lsTransactionStatusMsg = HHSConstants.AS_FAILURE
						+ PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
								HHSR5Constants.PROPERTY_THRESHOLD_VALUE_LIMIT);
				getModificationAutoApprovalThreshold(loChannelObj, aoResourceRequest, aoResourceResponse);
				loPrintWriter = aoResourceResponse.getWriter();
				return new ModelAndView(HHSR5Constants.AGENCY_PROV_CUSTOM_AUTO_CONFIG);
			}

			
			loChannelObj.setData(HHSR5Constants.AI_THRESHOLD_VALUE, ldThresholdValue);
			loChannelObj.setData(HHSConstants.AS_USER_ID, lsUserId);
		
			HHSTransactionManager.executeTransaction(loChannelObj, HHSR5Constants.SAVE_AUTO_APPROVE_THRESHOLD);
			
			lbStatus = (Boolean) loChannelObj.getData(HHSConstants.SAVE_STATUS);
			loAgencyProvidersList = (List<OrganizationBean>) loChannelObj.getData(HHSR5Constants.AGENCY_PROVIDERS);
			loDefaultThresholdBean =(AutoApprovalConfigBean) loChannelObj.getData(HHSR5Constants.DEFAULT_THRESHOLD_BEAN);

			aoResourceRequest.setAttribute(HHSR5Constants.DEFAULT_AUTO_CONFIG_THRESHOLD,loDefaultThresholdBean);
			
			if(loAgencyProvidersList.isEmpty()) {
				OrganizationBean orgBean = new OrganizationBean();
				orgBean.setMsOrgLegalName(HHSR5Constants.SELECT_PROVIDER);
				loAgencyProviders.add(orgBean);
			} else {
				for (OrganizationBean organizationBean : loAgencyProvidersList)
				{
					if(organizationBean.getMsOrgLegalName().contains("\"")){
						String orgLegalName = organizationBean.getMsOrgLegalName().replace("\"", "\'");
						organizationBean.setMsOrgLegalName(orgLegalName);
					}
					loAgencyProviders.add(organizationBean);
				}
			}
			
			if(loAgencyProviders!=null && !loAgencyProviders.isEmpty()){
				loProviders = HHSUtil.convertListToGridDropDown(loAgencyProviders, aoResourceRequest);
			}
			aoResourceRequest.setAttribute(HHSR5Constants.PROVIDER_LIST,loProviders);
			aoResourceRequest.setAttribute(HHSR5Constants.AS_AGENCY_ID,lsAgencyId);

			loPrintWriter = aoResourceResponse.getWriter();
			aoResourceRequest.getPortletSession().setAttribute(HHSConstants.SAVE_STATUS, lbStatus,
					PortletSession.APPLICATION_SCOPE);
			if (null != lbStatus && lbStatus == Boolean.TRUE)
			{
				lsTransactionStatusMsg = HHSConstants.AS_SUCCESS
						+ PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
								HHSR5Constants.PROPERTY_THRESHOLD_VALUE_SAVED);
			}
		}
		catch (ApplicationException loAppEx)
		{
			lsTransactionStatusMsg = HHSConstants.AS_FAILURE + HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("Error occured while saving threshold value", loAppEx);
		}
		catch (Exception loEx)
		{
			lsTransactionStatusMsg = HHSConstants.AS_FAILURE + HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("Error occured while saving threshold value", loEx);
		}
		finally
		{
			if (null != loPrintWriter)
			{
				StringBuffer loStringBuffer = new StringBuffer();
				loStringBuffer.append(lsTransactionStatusMsg);
				loPrintWriter.print(loStringBuffer.toString());
				loPrintWriter.flush();
				loPrintWriter.close();
			}
		}
		return new ModelAndView(HHSR5Constants.AGENCY_PROV_CUSTOM_AUTO_CONFIG);
	}
	
	
	/**
	 * <p>
	 * This method is added in R7
	 * This method is called to load the grid data for providers
	 * under an agency.
	 * <ul>
	 * @param aoResourceRequest ResourceRequest object
	 * @param aoResourceResponse ResourceResponse object
	 */
	@SuppressWarnings({ "rawtypes" })
	@ResourceMapping("customloadGridData")
	public void loadGridData(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws ApplicationException
	{
		PrintWriter loOut = null;
		LOG_OBJECT.Debug("Inside loadGridData method");
		
		try
		{
			synchronized (this)
			{
				loOut = aoResourceResponse.getWriter();
				String lsGridLabel = aoResourceRequest.getParameter(HHSConstants.GRID_LABEL);
				String lsTransactionName = aoResourceRequest.getParameter(HHSConstants.TRANSACTION_NAME);
				String lsClass = aoResourceRequest.getParameter(HHSConstants.BEAN_NAME);
				String loagencyId = aoResourceRequest.getParameter(HHSConstants.AGENCYID);
				StringBuffer loPropertyName = new StringBuffer(lsTransactionName);
				loPropertyName.append(HHSConstants.FETCH);
				lsTransactionName = PropertyLoader.getProperty(HHSConstants.PROPERTIES_GRIDTRANSACTION,
						loPropertyName.toString());
				aoResourceResponse.setContentType(HHSConstants.APPLICATION_JSON);
				String lsRowsPerPage = "10";
				String lsPage = HHSConstants.ONE;
				if (null != PortletSessionHandler.getAttribute(aoResourceRequest, true, HHSConstants.PAGINATION))
				{
					lsPage = (String) PortletSessionHandler.getAttribute(aoResourceRequest, false,
							HHSConstants.PAGINATION);
				}
				String lsErrorMsg = HHSConstants.EMPTY_STRING;
				if (null != PortletSessionHandler.getAttribute(aoResourceRequest, true, HHSConstants.GRID_ERROR))
				{
					lsErrorMsg = (String) PortletSessionHandler.getAttribute(aoResourceRequest, false,
							HHSConstants.GRID_ERROR);
				}				
				
				Channel loChannelObj = new Channel();
				loChannelObj.setData(HHSR5Constants.AS_AGENCY_ID, loagencyId);
				
				HHSTransactionManager.executeTransaction(loChannelObj, lsTransactionName);
				List loReturnedGridList = (List) loChannelObj.getData(HHSConstants.AO_RETURNED_GRID_LIST);
				LOG_OBJECT.Debug("aoReturnedGridList fetched successfully");
				/* [Start] R9.4.0 qc_9634 -- Vuln 1: CWE 470 - Use of Externally Controlled Input to Select Classes or Code ('Unsafe Reflection')   */
				lsClass = HHSUtil.checkClassAccessControl(lsClass); // throws ApplicationException if not valid class
				/* [End] R9.4.0 qc_9634 -- Vuln 1: CWE 470 - Use of Externally Controlled Input to Select Classes or Code ('Unsafe Reflection')   */
				Class loClass = Class.forName(lsClass);
				Object loBeanObj = loClass.newInstance();
				StringBuffer loBuffer = HHSUtil.populateSubGridRows(loBeanObj, loReturnedGridList, lsRowsPerPage,
						lsPage, lsErrorMsg, lsGridLabel);
				LOG_OBJECT.Debug("json for loadGridData: userid:: "
						+ (String) aoResourceRequest.getPortletSession().getAttribute(
								ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE)
						+ "\n json:: " + loBuffer.toString());
				
				loOut.print(loBuffer.toString().replaceAll("\\\\'", "'"));
			}
		}

		// ApplicationException is thrown from various points
		// from getProperty(if property is not found) and getCacheObject(getting
		// object from cache)
		// executeTransaction (while executing transaction)
		catch (ApplicationException aoAppExe)
		{
			// Set the error log if any application exception occurs
			LOG_OBJECT.Error("ApplicationException occured in loadGridData while fetching data from database ",
					aoAppExe);
		}
		// ApplicationException is thrown from various points
		// from getProperty(if property is not found) and getCacheObject(getting
		// object from cache),from forName and newInstance and from
		// executeTransaction (while executing transaction)
		catch (Exception aoExe)
		{
			// Set the error log if any application exception occurs
			LOG_OBJECT.Error("Exception occured in loadGridData while fetching data from database  ", aoExe);
		}
		finally
		{
			BaseControllerUtil.closingPrintWriter(loOut);
		}
	}
	
	/**
	 * <p>
	 * This method is added in R7
	 * This method is called for custom grid operations.
	 * @param aoResourceRequest ResourceRequest object
	 * @param aoResourceResponse ResourceResponse object
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ResourceMapping("customgridOperation")
	public void gridOperation(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws ApplicationException
	{
		
		LOG_OBJECT.Debug("Inside gridOperation method");
		String lsGridSuccessMessage;
		String lsOperation = aoResourceRequest.getParameter(HHSConstants.GRID_OPERATION);
		String lsTransactionName = aoResourceRequest.getParameter(HHSConstants.TRANSACTION_NAME);
		String lsClass = aoResourceRequest.getParameter(HHSConstants.BEAN_NAME);
		String lsUserId = (String) aoResourceRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
		StringBuffer loPropertyName = new StringBuffer(lsTransactionName);
		String lsId = aoResourceRequest.getParameter(HHSConstants.ID);
		String loOrgName;
		String loIndex = aoResourceRequest.getParameter(HHSR5Constants.EMP_POSITION);
		
		if (lsOperation != null && HHSConstants.OPERATION_ADD.equalsIgnoreCase(lsOperation)
				&& !lsId.equalsIgnoreCase(HHSConstants.NEW_ROW_IDENTIFIER))
		{
			lsOperation = HHSConstants.OPERATION_EDIT;
		}
		if (aoResourceRequest.getParameter(HHSConstants.PAGE) != null)
		{
			PortletSessionHandler.setAttribute(aoResourceRequest.getParameter(HHSConstants.PAGE), aoResourceRequest,
					HHSConstants.PAGINATION);
		}
		loPropertyName.append(BaseControllerUtil.lsOperationUpperCaseUtil(lsOperation));			
		try
		{
			Channel loChannelObj = new Channel();
			lsTransactionName = PropertyLoader.getProperty(HHSConstants.PROPERTIES_GRIDTRANSACTION,
					loPropertyName.toString());
			/* [Start] R9.4.0 qc_9634 -- Vuln 1: CWE 470 - Use of Externally Controlled Input to Select Classes or Code ('Unsafe Reflection')   */
			lsClass = HHSUtil.checkClassAccessControl(lsClass); // throws ApplicationException if not valid class
			/* [End] R9.4.0 qc_9634 -- Vuln 1: CWE 470 - Use of Externally Controlled Input to Select Classes or Code ('Unsafe Reflection')   */
			Class loClass = Class.forName(lsClass);
			Object loBeanObj = loClass.newInstance();
			
			BeanUtils.setProperty(loBeanObj, HHSConstants.MOD_BY_USER_ID, lsUserId);
			BeanUtils.setProperty(loBeanObj, HHSConstants.CREATED_BY_USER_ID,lsUserId);
			populateBeanFromRequest(aoResourceRequest, loBeanObj);
			
			Map<String, OrganizationBean> loOrgMap = (Map<String, OrganizationBean>) aoResourceRequest.getPortletSession().getAttribute(HHSR5Constants.ORGANIZATION_MAP, PortletSession.APPLICATION_SCOPE);
			
			if(loIndex!=null && !loIndex.equalsIgnoreCase(HHSConstants.EMPTY_STRING) && loOrgMap != null && !loOrgMap.isEmpty()){
				OrganizationBean loSelectedProvider = loOrgMap.get(loIndex);
				loOrgName = loSelectedProvider.getMsOrgLegalName();
				BeanUtils.setProperty(loBeanObj, HHSR5Constants.ORGANIZATION, loOrgName);
				BeanUtils.setProperty(loBeanObj, HHSR5Constants.ORGANIZATION_ID, loSelectedProvider.getMsOrgId());
			}
			BeanUtils.setProperty(loBeanObj,HHSR5Constants.REVIEW_PROCESS_ID,5);
			
			BaseControllerUtil
					.executeStaticGridTransaction(lsOperation, lsTransactionName, loChannelObj, loBeanObj);//transaction hit
			LOG_OBJECT.Debug("Executed grid transaction :"+ lsTransactionName +" Grid Operation" + lsOperation);
			lsGridSuccessMessage = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSR5Constants.PROPERTY_THRESHOLD_VALUE_SAVED);
			
			PortletSessionHandler.setAttribute(lsGridSuccessMessage, aoResourceRequest, HHSR5Constants.GRID_SUCCESS_MESSAGE);
			
		}
		
		// Application exception is handled here.
		catch (ApplicationException aoAppExe)
		{
			// If Application exception occur then get root cause ,get its
			// context data, store message in session and add error log.
			String lsGridErrorMessage = null;
			ApplicationException loAppEx = (ApplicationException) aoAppExe.getRootCause();
			if (null != loAppEx)
			{
				lsGridErrorMessage = (String) loAppEx.getContextData().get(HHSConstants.GRID_ERROR_MESSAGE);
			}
			if (null == lsGridErrorMessage)
			{
				lsGridErrorMessage = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
				LOG_OBJECT.Error("ApplicationException occured in gridOperation method while performing operation on grid  "
								+ aoAppExe);
			}
			PortletSessionHandler.setAttribute(lsGridErrorMessage, aoResourceRequest, HHSConstants.GRID_ERROR);
		}
		// Exception is thrown from
		// executeTransaction (while executing transaction)
		catch (Exception aoExe)
		{
			// If exception occur then store the error message in session and
			// set error log.
			String lsGridErrorMessage = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			PortletSessionHandler.setAttribute(lsGridErrorMessage, aoResourceRequest, HHSConstants.GRID_ERROR);
			LOG_OBJECT.Error("Error occured in gridOperation method while performing operation on grid  ", aoExe);
		}
	}
	
	/**
	 * <p>
	 * This method is added in R7
	 * This method is called as call back funtion of grid to display the 
	 * success message on grid operations.
	 * @param aoResourceRequest ResourceRequest object
	 * @param aoResourceResponse ResourceResponse object
	 */
	@ResourceMapping("getGridOperationMessage")
	public void getGridOperationMessage(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws ApplicationException
	{
		String lsGridSuccessMessage = (String) PortletSessionHandler.getAttribute(aoResourceRequest, HHSR5Constants.GRID_SUCCESS_MESSAGE);
		if(null != lsGridSuccessMessage){
			PrintWriter loPrintWriter = null;
			try
			{
				loPrintWriter = aoResourceResponse.getWriter();
			}
			catch (IOException e)
			{
				LOG_OBJECT.Error("IOException occured in getGridOperationMessage while fetching success message for grid operation ",
						e);
			}
			if (null != loPrintWriter)
						{
							StringBuffer loStringBuffer = new StringBuffer();
							loStringBuffer.append(lsGridSuccessMessage);
							loPrintWriter.print(loStringBuffer.toString());
							loPrintWriter.flush();
							loPrintWriter.close();
						}
		}
	}	
	//R7 end :Modification Auto Approval Enhancement
}
