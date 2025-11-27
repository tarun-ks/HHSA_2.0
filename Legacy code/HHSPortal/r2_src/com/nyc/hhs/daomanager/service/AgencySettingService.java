package com.nyc.hhs.daomanager.service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.AgencyDetailsBean;
import com.nyc.hhs.model.AgencySettingsBean;
import com.nyc.hhs.model.AutoApprovalConfigBean;
import com.nyc.hhs.model.CityUserDetailsBean;
import com.nyc.hhs.model.ContractBudgetBean;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.OrganizationBean;
import com.nyc.hhs.model.ReviewProcessBean;
import com.nyc.hhs.service.filenetmanager.p8services.P8ProcessServiceForSolicitationFinancials;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.HHSUtil;

/**
 * <p>
 * AgencySettingService: This service class is used to perform Agency Settings
 * Operation for retrieve, save and update in agency settings: S404-screen:
 * Accelerator users use it to assign review levels to review task for a
 * particular agency S405-screen: Agency users use it to assign users across
 * levels, where number of levels are set by accelerator users in S404 screen.
 * This class has been updated in R7 for Modification Auto Approval Enhancement.
 * </p>
 */
public class AgencySettingService extends ServiceState
{
	/**
	 * Log object to record all logs
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(AgencySettingService.class);

	// ***CITY USER USER METHODS**************
	/**
	 * <p>
	 * This is AgencySettingService service class method and is called by
	 * accelerator users on page load to fetch all agency names and all review
	 * process from database. Accelerator user can select any review process
	 * under an agency and can associate number of review levels that needs to
	 * be done for the selected review process task.
	 * </p>
	 * <ul>
	 * <li>Called by Accelerator user, screen-S404</li>
	 * <li>Step executed are:</li>
	 * <li>1.Fetch all agency names</li>
	 * <li>2.Fetch all review process task</li>
	 * <li>3.From list of review process task remove all configuration task</li>
	 * </ul>
	 * @param aoMyBatisSession SQLSession object
	 * @return loAgencySettingsBean AgencySettingsBean - bean containing list of
	 *         all agency names and list of all review process
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	@SuppressWarnings("unchecked")
	public AgencySettingsBean fetchAgencyAndReviewProcessData(SqlSession aoMyBatisSession) throws ApplicationException
	{
		AgencySettingsBean loAgencySettingsBean = null;
		List<AgencyDetailsBean> loNYCAgencyList = null;
		List<ReviewProcessBean> loReviewProcessBeanList = null;
		Map<String, Object> loQueryMap = new HashMap<String, Object>();
		try
		{
			// fetch all agency names (11 agencies for which document sharing
			// flag is set to 1)
			loNYCAgencyList = (List<AgencyDetailsBean>) DAOUtil.masterDAO(aoMyBatisSession, null,
					HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS, HHSConstants.FETCH_AGENCY_NAMES, null);

			// fetch all review process (except configuration review process)
			loQueryMap.put(HHSConstants.AS_NON_CONFIG_TASK, true);
			loReviewProcessBeanList = (List<ReviewProcessBean>) DAOUtil.masterDAO(aoMyBatisSession, loQueryMap,
					HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS, HHSConstants.AS_FETCH_REVIEW_PROCESS,
					HHSConstants.JAVA_UTIL_MAP);
			loNYCAgencyList = formatAgencyNameList(loNYCAgencyList);
			loAgencySettingsBean = new AgencySettingsBean();
			loAgencySettingsBean.setAllAgencyDetailsBeanList(loNYCAgencyList);
			loAgencySettingsBean.setAllReviewProcessBeanList(loReviewProcessBeanList);

			setMoState("AgencySettingService: fetchAgencyAndReviewProcessData() All agency names and all review"
					+ " process are fetched successfully.");
			return loAgencySettingsBean;
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured in AgencySettingService: fetchAgencyAndReviewProcessData method:: ",
					loAppEx);
			setMoState("Transaction Failed:: AgencySettingService: fetchAgencyAndReviewProcessData method - failed."
					+ " Exception occured while fetching all agency user names and review process data. \n");
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Exception occured in AgencySettingService: fetchAgencyAndReviewProcessData method:: ",
					loEx);
			setMoState("Transaction Failed:: AgencySettingService: fetchAgencyAndReviewProcessData method - failed."
					+ " Exception occured while fetching all agency user names and review process data. \n");
			throw new ApplicationException(
					"Error occured in AgencySettingService: fetchAgencyAndReviewProcessData method:: ", loEx);
		}
	}

	/**
	 * <p>
	 * This is a private method used to format agency names in particular format
	 * from xxx - yyyyy to yyyyy (xxx)
	 * </p>
	 * <ul>
	 * <li>Get list of AgencyDetailsBean</li>
	 * <li>Read each bean element from list</li>
	 * <li>call formatter method, getAgencyName present in HHSUtil class</li>
	 * </ul>
	 * 
	 * @param aoNYCAgencyList contains list of bean, AgencyDetailsBean
	 * @return List<AgencyDetailsBean> loNYCAgencyList formatted list
	 * @throws Exception Exception thrown in case of any application code
	 *             failure.
	 */
	private List<AgencyDetailsBean> formatAgencyNameList(List<AgencyDetailsBean> aoNYCAgencyList) throws Exception
	{
		List<AgencyDetailsBean> loNYCAgencyList = new ArrayList<AgencyDetailsBean>();
		if (null != aoNYCAgencyList && !aoNYCAgencyList.isEmpty())
		{
			for (AgencyDetailsBean loTempAgencyDetBean : aoNYCAgencyList)
			{
				AgencyDetailsBean loAgencyDetailsBean = new AgencyDetailsBean();
				loAgencyDetailsBean.setAgencyName(HHSUtil.getAgencyName((loTempAgencyDetBean.getAgencyName())));
				loAgencyDetailsBean.setAgencyId(loTempAgencyDetBean.getAgencyId());
				loNYCAgencyList.add(loAgencyDetailsBean);
			}
			Collections.sort(loNYCAgencyList, new Comparator<AgencyDetailsBean>()
			{
				@Override
				public int compare(AgencyDetailsBean c1, AgencyDetailsBean c2)
				{
					return c1.getAgencyName().compareTo(c2.getAgencyName());
				}
			});
		}
		return loNYCAgencyList;
	}

	/**
	 * <p>
	 * This is AgencySettingService service class method and is called by
	 * accelerator users when user selects an agency and then selects review
	 * process and hit 'Go' button. It retrieves levels of review that
	 * accelerator user has set for a particular review process.
	 * </p>
	 * <ul>
	 * <li>Called by Accelerator user, screen-S404</li>
	 * <li>Step executed are:</li>
	 * <li>1.Get the review level if it exist in database for a particular
	 * agency and review process</li>
	 * <li>2.If no record found the explicitly set review levels to zero</li>
	 * <li>3.Else assign review levels to levels retrieved from database</li>
	 * </ul>
	 * 
	 * @param asAgencyId String - agency id of Agency
	 * @param aoReviewProcId Integer - review process id
	 * @param aoMyBatisSession SQLSession object
	 * @return int number of review levels retrieved
	 * @throws ApplicationException ApplicationException object
	 */
	public int fetchReviewLevels(String asAgencyId, Integer aoReviewProcId, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		int liReviewLevels = HHSConstants.INT_ZERO;
		Integer loTempValue = null;
		Map<String, Object> loQueryMap = new HashMap<String, Object>();
		try
		{
			loQueryMap.put(HHSConstants.REVIEW_PROCESS_ID_KEY, aoReviewProcId);
			loQueryMap.put(HHSConstants.AGENCYID, asAgencyId);
			loTempValue = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loQueryMap,
					HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS, HHSConstants.AS_FETCH_REVIEW_LEVELS,
					HHSConstants.JAVA_UTIL_MAP);
			if (loTempValue == null)
			{
				liReviewLevels = HHSConstants.INT_ZERO;
			}
			else
			{
				liReviewLevels = loTempValue;
			}
			setMoState("AgencySettingService: fetchReviewLevels - Review level is successfully retrieved for"
					+ " agency id ::" + loQueryMap.get(HHSConstants.AGENCYID) + " and review process id::"
					+ loQueryMap.get(HHSConstants.REVIEW_PROCESS_ID_KEY));
			return liReviewLevels;
		}
		// Application Exception is handled here
		catch (ApplicationException aoAppEx)
		{
			// Context Data is added into Exception object and Log is updated
			aoAppEx.addContextData(HHSConstants.AGENCYID, loQueryMap.get(HHSConstants.AGENCYID));
			aoAppEx.addContextData(HHSConstants.REVIEW_PROCESS_ID_KEY,
					loQueryMap.get(HHSConstants.REVIEW_PROCESS_ID_KEY));
			LOG_OBJECT.Error("Exception occured in AgencySettingService: fetchReviewLevels method:: ", aoAppEx);
			setMoState("Transaction Failed:: AgencySettingService: fetchReviewLevels method - failed Exception "
					+ "occured while fetching review levels for agency id ::" + loQueryMap.get(HHSConstants.AGENCYID)
					+ " and review process id::" + loQueryMap.get(HHSConstants.REVIEW_PROCESS_ID_KEY));
			throw aoAppEx;
		}
		// Exception is handled here
		catch (Exception aoEx)
		{
			// Context Data is added into Exception object and Log is updated
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "AgencySettingService: fetchReviewLevels method:: ", aoEx);
			loAppEx.addContextData(HHSConstants.AGENCYID, loQueryMap.get(HHSConstants.AGENCYID));
			loAppEx.addContextData(HHSConstants.REVIEW_PROCESS_ID_KEY,
					loQueryMap.get(HHSConstants.REVIEW_PROCESS_ID_KEY));
			LOG_OBJECT.Error("Exception occured in AgencySettingService: fetchReviewLevels method:: ", loAppEx);
			setMoState("Transaction Failed:: AgencySettingService: fetchReviewLevels method - failed "
					+ "Exception occured while fetching review levels for agency id ::"
					+ loQueryMap.get(HHSConstants.AGENCYID) + " and review process id::"
					+ loQueryMap.get(HHSConstants.REVIEW_PROCESS_ID_KEY));
			throw loAppEx;
		}
	}

	/**
	 * This Method modified as a part of release 3.8.0 enhancement 6534
	 * <ul>
	 * This method Enable Change of Task Levels of Approvals for all Tasks
	 * Regardless of Whether there are Tasks Inflight
	 * </ul>
	 * 
	 * <p>
	 * This is AgencySettingService service class method and is called by
	 * accelerator users when user selects any radio button, selecting levels of
	 * review that an agency needs to do for a particular review process type.
	 * It saves the level of review selected in database.
	 * </p>
	 * <ul>
	 * <li>Called by Accelerator user, screen-S404</li>
	 * <li>Step executed are:</li>
	 * 
	 * <li>1.Check if levels are being set for the first time then do an insert
	 * call to save levels of review, check oldLevelOfReview in
	 * aoAgencySettingsBean</li>
	 * <li>2.Else check count of all opened task of this particular task type
	 * under this agency</li>
	 * <li>2.1.Check if no open task are present then proceed with update call
	 * to level of review</li>
	 * <li>2.2.Else if open task are present then do not update and return with
	 * failure message stating cannot change level of review as currently open
	 * task are present</li>
	 * </ul>
	 * 
	 * @param aoAgencySettingsBean AgencySettingsBean containing agency id,
	 *            review process id and review level to be set
	 * @param aoMyBatisSession SQLSession object
	 * @param aoFilenetSession P8UserSession
	 * @return boolean boolean status of insert/update database call
	 * @throws ApplicationException ApplicationException object
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Map<String, Object> saveReviewLevels(AgencySettingsBean aoAgencySettingsBean, SqlSession aoMyBatisSession,
			P8UserSession aoFilenetSession) throws ApplicationException
	{
		Map<String, Object> loQueryMap = new HashMap<String, Object>();
		Map<String, Object> loSaveReviewLevRetrndMap = new HashMap<String, Object>();
		loQueryMap.put(HHSConstants.REVIEW_PROCESS_ID_KEY, aoAgencySettingsBean.getReviewProcessId());
		loQueryMap.put(HHSConstants.AGENCYID, aoAgencySettingsBean.getAgencyId());
		loSaveReviewLevRetrndMap.put(HHSConstants.SAVE_STATUS, false);
		try
		{
			if (aoAgencySettingsBean.getOldLevelOfReview() == HHSConstants.INT_ZERO)
			{
				// code modified as a part of release 3.8.0 enhancement 6534 -
				// start
				aoAgencySettingsBean.setReviewLevelChangeInProgress(HHSConstants.NO);
				// code modified as a part of release 3.8.0 enhancement 6534 -
				// end
				DAOUtil.masterDAO(aoMyBatisSession, aoAgencySettingsBean, HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS,
						HHSConstants.AS_INSERT_REVIEW_LEVELS, HHSConstants.AS_AGENCY_SETTING_BEAN_FILE_PATH);
				loSaveReviewLevRetrndMap.put(HHSConstants.SAVE_STATUS, true);
				setMoState("AgencySettingService: saveReviewLevels method -Review level is successfully saved for "
						+ "values aoAgencySettingsBean::" + aoAgencySettingsBean);
			}
			else
			{
				loSaveReviewLevRetrndMap = fetchOpenedTaskCount(loQueryMap, aoFilenetSession);
				loSaveReviewLevRetrndMap.put(HHSConstants.SAVE_STATUS, false);
				int liOpenedTaskCount = (Integer) loSaveReviewLevRetrndMap.get(HHSConstants.AS_OPEN_TASK_COUNT);
				if (liOpenedTaskCount == HHSConstants.INT_ZERO)
				{
					// code modified as a part of release 3.8.0 enhancement 6534
					// - start
					aoAgencySettingsBean.setReviewLevelChangeInProgress(HHSConstants.NO);
					// code modified as a part of release 3.8.0 enhancement 6534
					// - end
					DAOUtil.masterDAO(aoMyBatisSession, aoAgencySettingsBean,
							HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS, HHSConstants.AS_UPDATE_REVIEW_LEVELS,
							HHSConstants.AS_AGENCY_SETTING_BEAN_FILE_PATH);

					checkAndDeleteLevelUsers(aoAgencySettingsBean, aoMyBatisSession);

					loSaveReviewLevRetrndMap.put(HHSConstants.SAVE_STATUS, true);
					setMoState("AgencySettingService: saveReviewLevels method -Review level is successfully saved for"
							+ " values aoAgencySettingsBean::" + aoAgencySettingsBean);
				}
				else if (liOpenedTaskCount != HHSConstants.INT_ZERO)
				{
					// code modified as a part of release 3.8.0 enhancement 6534
					// - start
					aoAgencySettingsBean.setReviewLevelChangeInProgress(HHSConstants.YES);
					DAOUtil.masterDAO(aoMyBatisSession, aoAgencySettingsBean,
							HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS, HHSConstants.AS_UPDATE_REVIEW_LEVELS,
							HHSConstants.AS_AGENCY_SETTING_BEAN_FILE_PATH);

					checkAndDeleteLevelUsers(aoAgencySettingsBean, aoMyBatisSession);

					HashMap loHMWFRequiredProps = new HashMap();
					String lsProcessId = (String.valueOf(aoAgencySettingsBean.getReviewProcessId()));
					String lsTaskType = (String) HHSConstants.FINANCIAL_TASK_ID_PROCESS_MAP.get(lsProcessId);
					loHMWFRequiredProps.put(HHSConstants.COMPONENT_ACTION,
							HHSConstants.SET_REVIEW_LEVEL_PROPERTIES_IN_FILENET);
					loHMWFRequiredProps.put(HHSConstants.PROPERTY_PE_TASK_TYPE, lsTaskType);
					loHMWFRequiredProps.put(HHSConstants.COMP_AGENCY_ID, aoAgencySettingsBean.getAgencyId());
					loHMWFRequiredProps.put(HHSConstants.OLD_REVIEW_LEVEL, aoAgencySettingsBean.getOldLevelOfReview());
					loHMWFRequiredProps.put(HHSConstants.NEW_REVIEW_LEVEL, aoAgencySettingsBean.getLevelOfReview());
					new P8ProcessServiceForSolicitationFinancials().launchWorkflow(aoFilenetSession,
							HHSConstants.WF_FINANCIAL_UTILITY, loHMWFRequiredProps);

					loSaveReviewLevRetrndMap.put(HHSConstants.SAVE_STATUS, true);
					setMoState("AgencySettingService: saveReviewLevels method -Review level is successfully saved for"
							+ " values aoAgencySettingsBean::" + aoAgencySettingsBean);

					// code modified as a part of release 3.8.0 enhancement 6534
					// - end

				}
				HhsAuditBean aoAudit = new HhsAuditBean();
				aoAudit.setEventName(HHSConstants.REVIEW_LEVEL_CHANGE);
				aoAudit.setEventType(HHSConstants.REVIEW_LEVEL_CHANGE);
				aoAudit.setData("Review Level Changed from " + aoAgencySettingsBean.getOldLevelOfReview() + " to "
						+ aoAgencySettingsBean.getLevelOfReview());
				aoAudit.setEntityType(HHSConstants.AGENCY);
				aoAudit.setEntityId(aoAgencySettingsBean.getAgencyId());
				aoAudit.setUserId(aoAgencySettingsBean.getModifiedByUserId());
				DAOUtil.masterDAO(aoMyBatisSession, aoAudit, HHSConstants.MAPPER_CLASS_HHS_AUDIT_MAPPER,
						HHSConstants.HHSAUDIT_AGENCY_INSERT, HHSConstants.HHS_AUDIT_BEAN_PATH);
			}

		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("aoAgencySettingsBean", CommonUtil.convertBeanToString(aoAgencySettingsBean));
			LOG_OBJECT.Error("Exception occured in AgencySettingService: saveReviewLevels method:: ", loAppEx);
			setMoState("Transaction Failed:: AgencySettingService: saveReviewLevels method - failed Exception occured"
					+ " while saving review levels with details aoAgencySettingsBean::" + aoAgencySettingsBean + "\n");
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "AgencySettingService: saveReviewLevels method:: ", loEx);
			loAppEx.addContextData("aoAgencySettingsBean", CommonUtil.convertBeanToString(aoAgencySettingsBean));
			LOG_OBJECT.Error("Exception occured in AgencySettingService: saveReviewLevels method:: ", loAppEx);
			setMoState("Transaction Failed:: AgencySettingService: saveReviewLevels method - failed Exception occured"
					+ " while saving review levels with details aoAgencySettingsBean::" + aoAgencySettingsBean + "\n");
			throw loAppEx;
		}
		return loSaveReviewLevRetrndMap;
	}

	// ***AGENCY USER METHODS***************** work in progress

	/**
	 * <p>
	 * This is AgencySettingService service class method and is called by agency
	 * users on page load. It fetches all review process for a particular
	 * agency.
	 * </p>
	 * <ul>
	 * <li>Called by Agency user, screen-S405</li>
	 * <li>Step executed are:</li>
	 * <li>1.Get all review process for a particular agency</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession SQLSession object
	 * @return AgencySettingsBean AgencySettingsBean - bean containing list of
	 *         all review process- List<ReviewProcessBean>
	 * @throws ApplicationException ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public AgencySettingsBean fetchAllReviewProcessData(SqlSession aoMyBatisSession) throws ApplicationException
	{
		AgencySettingsBean loAgencySettingsBean = null;
		List<ReviewProcessBean> loReviewProcessBeanList = null;
		try
		{
			// fetch all non configuration review process
			Map<String, Object> loQueryMap = new HashMap<String, Object>();
			loQueryMap.put(HHSConstants.AS_NON_CONFIG_TASK, false);
			loReviewProcessBeanList = (List<ReviewProcessBean>) DAOUtil.masterDAO(aoMyBatisSession, loQueryMap,
					HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS, HHSConstants.AS_FETCH_REVIEW_PROCESS,
					HHSConstants.JAVA_UTIL_MAP);

			loAgencySettingsBean = new AgencySettingsBean();
			loAgencySettingsBean.setAllReviewProcessBeanList(loReviewProcessBeanList);

			setMoState("AgencySettingService: fetchAllReviewProcessData - All review process data are "
					+ "fetched successfully.");
			return loAgencySettingsBean;

		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured in AgencySettingService:fetchAllReviewProcessData method:: ", loAppEx);
			setMoState("Transaction Failed:: AgencySettingService: fetchAllReviewProcessData method - failed Exception"
					+ " occurred while fetching all review process data \n");
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Exception occured in AgencySettingService: fetchAllReviewProcessData method:: ", loEx);
			setMoState("Transaction Failed:: AgencySettingService: fetchAllReviewProcessData method - failed Exception"
					+ " occurred while fetching all review process data \n");
			throw new ApplicationException(
					"Exception occured in AgencySettingService: fetchAllReviewProcessData method:: ", loEx);
		}
	}

	/**
	 * <p>
	 * This is AgencySettingService service class method and is called by agency
	 * users when user selects any review process type and hit on "Go" button.
	 * It fetches all level users, where number of levels are set by accelerator
	 * users. Agency user can further change/assign users across different
	 * levels for review.
	 * </p>
	 * <ul>
	 * <li>Called by Agency user, screen-S405</li>
	 * <li>Step executed are:</li>
	 * <li>1.Get review levels set by accelerator user for a review process and
	 * agency id</li>
	 * <li>2.Check if review levels are greater than 0</li>
	 * <li>2.1 fetch all unassigned user list and assign it to
	 * setAllAgencyUsersList</li>
	 * <li>2.2 fetch all assigned user list and call segregateLevel1To4Users()
	 * method with assigned user list as parameter</li>
	 * <li>segregateLevel1To4Users(): further segregates all assigned users into
	 * level 1 to level 4 user's list and assign it to AgencySettingBean</li>
	 * <li>3.Else if review level is 0 then set review level(0) to bean and send
	 * to controller where corresponding error message is shown stating
	 * Accelerator user has not set levels of review</li>
	 * </ul>
	 * 
	 * @param asUserId id of the user from session
	 * @param aiReviewProcessId Integer - ReviewProcessId of review process
	 * @param asAgencyId String - agency id of agency
	 * @param asConfigFlag - flag to distinguish between configuration and
	 *            non-config tasks
	 * @param aoMyBatisSession SQLSession object
	 * @return AgencySettingsBean AgencySettingsBean - bean containing list of
	 *         all level1 to level4 users and list of unassigned users
	 * @throws ApplicationException ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public AgencySettingsBean fetchAgencySetAssgndUsrData(String asUserId, Integer aiReviewProcessId,
			String asAgencyId, String asConfigFlag, SqlSession aoMyBatisSession) throws ApplicationException
	{
		AgencySettingsBean loAgencySettingsBean = new AgencySettingsBean();
		List<CityUserDetailsBean> loAllAgencyUsersList = null;
		List<CityUserDetailsBean> loAllAssgndUsersList = null;
		int liReviewlevels = HHSConstants.INT_ZERO;
		Map<String, Object> loQueryMap = null;
		try
		{
			loQueryMap = new HashMap<String, Object>();
			loQueryMap.put(HHSConstants.AGENCYID, asAgencyId);
			loQueryMap.put(HHSConstants.REVIEW_PROCESS_ID_KEY, aiReviewProcessId);
			loQueryMap.put(HHSConstants.USER_ID, asUserId);
			liReviewlevels = fetchReviewLevels(asAgencyId, aiReviewProcessId, aoMyBatisSession);
			if (liReviewlevels == HHSConstants.INT_ZERO && asConfigFlag.equalsIgnoreCase(HHSConstants.ONE))
			{
				setConfigReviewLevlTo1(loQueryMap, aoMyBatisSession);
				liReviewlevels = 1;
			}
			if (liReviewlevels > HHSConstants.INT_ZERO)
			{
				loAllAgencyUsersList = (List<CityUserDetailsBean>) DAOUtil.masterDAO(aoMyBatisSession, loQueryMap,
						HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS, HHSConstants.AS_FETCH_AGENCY_USER_NAMES,
						HHSConstants.JAVA_UTIL_MAP);

				loAllAssgndUsersList = (List<CityUserDetailsBean>) DAOUtil.masterDAO(aoMyBatisSession, loQueryMap,
						HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS, HHSConstants.AS_FETCH_ASSIGNED_USER_NAMES,
						HHSConstants.JAVA_UTIL_MAP);
				loAgencySettingsBean = segregateLevel1To4Users(loAllAssgndUsersList);
				loAgencySettingsBean.setAllAgencyUsersList(loAllAgencyUsersList);
			}
			loAgencySettingsBean.setLevelOfReview(liReviewlevels);
			loAgencySettingsBean.setAgencyId(asAgencyId);
			loAgencySettingsBean.setReviewProcessId(aiReviewProcessId);
			fetchLevel1UsersIfCoFTask(loAgencySettingsBean, aoMyBatisSession);
			setMoState("AgencySettingService: fetchAgencySetAssgndUsrDataAll fetched data successfully.");
			return loAgencySettingsBean;
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.AGENCYID, loQueryMap.get(HHSConstants.AGENCYID));
			loAppEx.addContextData(HHSConstants.REVIEW_PROCESS_ID_KEY,
					loQueryMap.get(HHSConstants.REVIEW_PROCESS_ID_KEY));
			LOG_OBJECT.Error("Exception occured in AgencySettingService: fetchAgencySetAssgndUsrData method:: ",
					loAppEx);
			setMoState("Transaction Failed:: AgencySettingService: fetchAgencySetAssgndUsrData method - "
					+ "failed Exception occured while fetching all assigned user names with agencyid::"
					+ loQueryMap.get(HHSConstants.AGENCYID) + " and review process id::"
					+ loQueryMap.get(HHSConstants.REVIEW_PROCESS_ID_KEY));
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "AgencySettingService: fetchAgencySetAssgndUsrData method:: ", loEx);
			if (null != loQueryMap)
			{
				loAppEx.addContextData(HHSConstants.AGENCYID, loQueryMap.get(HHSConstants.AGENCYID));
				loAppEx.addContextData(HHSConstants.REVIEW_PROCESS_ID_KEY,
						loQueryMap.get(HHSConstants.REVIEW_PROCESS_ID_KEY));
			}
			LOG_OBJECT.Error("Exception occured in AgencySettingService: fetchAgencySetAssgndUsrData method:: ",
					loAppEx);
			setMoState("Transaction Failed:: AgencySettingService: fetchAgencySetAssgndUsrData method - failed "
					+ "Exception occured while fetching all assigned user names with agencyid::"
					+ loQueryMap.get(HHSConstants.AGENCYID) + " and review process id::"
					+ loQueryMap.get(HHSConstants.REVIEW_PROCESS_ID_KEY));
			throw loAppEx;
		}
	}

	/**
	 * <p>
	 * Changes made for enhancement 6534 for Release 3.8.0 This is
	 * AgencySettingService service class method and is called by agency users
	 * when user has assigned/changed level users and has clicked on "Save"
	 * button. The main algorithm used is - for each level users get list of all
	 * users to be inserted and list of all users to be deleted, same process is
	 * done for all levels, level 1 to level 4
	 * </p>
	 * <ul>
	 * <li>Called by Agency user, screen-S405</li>
	 * <li>Step executed are:</li>
	 * <li>1.For review levels - level 1 to level 4 do as below:</li>
	 * <li>1.1 merge oldlist and newlist and create mergedlist with unique
	 * values</li>
	 * <li>1.1 now insert list = mergedlist - oldlist</li>
	 * <li>1.1 now delete list = mergedlist - newlist</li>
	 * <li>2.Call levelUsersInsert() and levelUsersDelete() which inserts and
	 * deletes list of user from database</li>
	 * </ul>
	 * 
	 * @param aiLevelOfReviewAssigned - levels of review set
	 * @param aoOldAgencySettingsBean AgencySettingsBean - Contains list of all
	 *            users on page load
	 * @param aoNewAgencySettingsBean AgencySettingsBean - Contains list of all
	 *            users with changes on click of save button
	 * @param aoMyBatisSession SQLSession object
	 * @param aoFilenetSession P8UserSession object
	 * @return Boolean status of whole method stating true when all
	 *         insert/delete calls are successful
	 * @throws ApplicationException ApplicationException object
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public Boolean saveAgencyLevelUsers(Integer aiLevelOfReviewAssigned, AgencySettingsBean aoOldAgencySettingsBean,
			AgencySettingsBean aoNewAgencySettingsBean, SqlSession aoMyBatisSession, P8UserSession aoFilenetSession)
			throws ApplicationException
	{
		String[] loListNames =
		{ HHSConstants.AS_GET_ALL_LEV1_USER_LIST, HHSConstants.AS_GET_ALL_LEV2_USER_LIST,
				HHSConstants.AS_GET_ALL_LEV3_USER_LIST, HHSConstants.AS_GET_ALL_LEV4_USER_LIST };
		boolean lbSaveStatus = false;
		AgencySettingsBean loBufferAgencySettingsBean = null;
		Map<String, Object> loAssgndToUnAssgndMap = new HashMap<String, Object>();
		List<CityUserDetailsBean> loAssgndToUnAssgndList = new ArrayList<CityUserDetailsBean>();
		// START || Changes made for enhancement 6534 for Release 3.8.0
		boolean lbErrorStatus = false;
		Map<String, Object> loQueryMap = new HashMap<String, Object>();
		try
		{
			loQueryMap.put(HHSConstants.REVIEW_PROCESS_ID_KEY, aoNewAgencySettingsBean.getReviewProcessId());
			loQueryMap.put(HHSConstants.AGENCYID, aoNewAgencySettingsBean.getAgencyId());
			Integer loTempValue = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loQueryMap,
					HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS, HHSConstants.AS_FETCH_REVIEW_LEVELS,
					HHSConstants.JAVA_UTIL_MAP);

			if (null != loTempValue && loTempValue != aiLevelOfReviewAssigned)
			{
				lbErrorStatus = Boolean.TRUE;
			}
			if (!lbErrorStatus)
			{
				// END || Changes made for enhancement 6534 for Release 3.8.0
				for (int liCounter = HHSConstants.INT_ZERO; liCounter < aiLevelOfReviewAssigned; liCounter++)
				{
					Map<String, Object> loMap = null;
					String lsMethodName = loListNames[liCounter];
					final Class loOldClass = aoOldAgencySettingsBean.getClass();
					Method loOldClassMethod;
					loOldClassMethod = loOldClass.getDeclaredMethod(lsMethodName, new Class[] {});
					List<CityUserDetailsBean> loOldList = (List<CityUserDetailsBean>) loOldClassMethod.invoke(
							aoOldAgencySettingsBean, new Object[] {});

					final Class loNewClass = aoNewAgencySettingsBean.getClass();
					Method loNewClassMethod;
					loNewClassMethod = loNewClass.getDeclaredMethod(lsMethodName, new Class[] {});

					List<CityUserDetailsBean> loNewList = (List<CityUserDetailsBean>) loNewClassMethod.invoke(
							aoNewAgencySettingsBean, new Object[] {});

					loMap = new HashMap<String, Object>();
					loMap = segregateIntoInsertDeleteList(loOldList, loNewList);
					loBufferAgencySettingsBean = new AgencySettingsBean();
					loBufferAgencySettingsBean.setAgencyId(aoNewAgencySettingsBean.getAgencyId());
					loBufferAgencySettingsBean.setReviewProcessId(aoNewAgencySettingsBean.getReviewProcessId());

					loBufferAgencySettingsBean.setLastUpdateDate(aoNewAgencySettingsBean.getLastUpdateDate());
					loBufferAgencySettingsBean.setCreatedDate(aoNewAgencySettingsBean.getCreatedDate());
					loBufferAgencySettingsBean.setCreatedByUserId(aoNewAgencySettingsBean.getCreatedByUserId());
					loBufferAgencySettingsBean.setModifiedDate(aoNewAgencySettingsBean.getModifiedDate());
					loBufferAgencySettingsBean.setModifiedByUserId(aoNewAgencySettingsBean.getModifiedByUserId());

					loBufferAgencySettingsBean.setAllAgencyUsersList((List<CityUserDetailsBean>) loMap
							.get(HHSConstants.AS_DELETE_LIST));
					loAssgndToUnAssgndList.addAll((List<CityUserDetailsBean>) loMap.get(HHSConstants.AS_DELETE_LIST));
					levelUsersDelete(loBufferAgencySettingsBean, aoMyBatisSession);

					loBufferAgencySettingsBean.setAllAgencyUsersList((List<CityUserDetailsBean>) loMap
							.get(HHSConstants.AS_INSERT_LIST));
					levelUsersInsert(loBufferAgencySettingsBean, aoMyBatisSession);
					loAssgndToUnAssgndMap.put(HHSConstants.AS_LEVEL + (liCounter + 1) + HHSConstants.AS_ASSIGNED_LIST,
							(List<CityUserDetailsBean>) loMap.get(HHSConstants.AS_DELETE_LIST));
				}
				setTaskToUnAssignedQueue(aoNewAgencySettingsBean.getAgencyId(),
						aoNewAgencySettingsBean.getReviewProcessId(), loAssgndToUnAssgndList, aoFilenetSession);
				lbSaveStatus = true;
				setMoState("AgencySettingService: saveAgencyLevelUsers done successfully.");
			}
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("aoAgencySettingsBean", CommonUtil.convertBeanToString(loBufferAgencySettingsBean));
			LOG_OBJECT.Error("Exception occured in AgencySettingService: saveAgencyLevelUsers method:: ", loAppEx);
			setMoState("Transaction Failed:: AgencySettingService: saveAgencyLevelUsers method - failed Exception"
					+ " occured while saving agency level users, loBufferAgencySettingsBean::"
					+ loBufferAgencySettingsBean + "\n");
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "AgencySettingService: saveReviewLevels method:: ", loEx);
			loAppEx.addContextData("aoAgencySettingsBean", CommonUtil.convertBeanToString(loBufferAgencySettingsBean));
			LOG_OBJECT.Error("Exception occured in AgencySettingService: saveAgencyLevelUsers method:: ", loAppEx);
			setMoState("Transaction Failed:: AgencySettingService: saveAgencyLevelUsers method - failed"
					+ " Exception occured while saving agency level users, loBufferAgencySettingsBean::"
					+ loBufferAgencySettingsBean + "\n");
			throw loAppEx;
		}
		return lbSaveStatus;
	}

	/**
	 * <p>
	 * This is a private method that is called every time for each level from
	 * level 1 to level 4. It receives two list of users old and new. It further
	 * compare two list and segregate into two lists insert list and delete list
	 * </p>
	 * <ul>
	 * <li>It receives two list of CityUserDetailsBean:</li>
	 * <li>OldList: containing details of all users that were on page load when
	 * review process is selected by agency</li>
	 * <li>NewList: containing details of all users that were changed/reassigned
	 * by Agency users</li>
	 * <li>Merge OldList and NewList into new MergedList that contains Unique
	 * entries</li>
	 * <li>To get list of users to be inserted at particular level do
	 * (MergedList-OldList)</li>
	 * <li>To get list of users to be deleted at particular level do
	 * (MergedList-NewList)</li>
	 * <li>NewList: containing details of all users that were changed/reassigned
	 * by Agency users</li>
	 * <li>NewList: containing details of all users that were changed/reassigned
	 * by Agency users</li>
	 * <li>Iterate till whole list is traversed</li>
	 * </ul>
	 * 
	 * @param aoOldList list of type CityUserDetailsBean with page on load users
	 * @param aoNewList list of type CityUserDetailsBean with page on submit
	 *            users
	 * @return Map<String, Object> with insert and delete user's list
	 * @throws Exception Exception thrown in case of any application code
	 *             failure.
	 */
	private Map<String, Object> segregateIntoInsertDeleteList(List<CityUserDetailsBean> aoOldList,
			List<CityUserDetailsBean> aoNewList) throws Exception
	{
		List<CityUserDetailsBean> loMergedAllUserList = new ArrayList<CityUserDetailsBean>();
		List<CityUserDetailsBean> loMergedAllUserListCopy = new ArrayList<CityUserDetailsBean>();
		Map<String, Object> loInsertDeleteListMap = new HashMap<String, Object>();

		loMergedAllUserList.addAll(aoOldList);
		loMergedAllUserList.addAll(aoNewList);

		Set<CityUserDetailsBean> loSet = new HashSet<CityUserDetailsBean>();

		loSet.addAll(loMergedAllUserList);
		List<CityUserDetailsBean> loMergedAllUserList1 = new ArrayList<CityUserDetailsBean>();
		loMergedAllUserList1.addAll(loSet);
		loMergedAllUserListCopy.addAll(loMergedAllUserList1);

		loMergedAllUserList.removeAll(aoOldList);
		loMergedAllUserListCopy.removeAll(aoNewList);
		loInsertDeleteListMap.put(HHSConstants.AS_INSERT_LIST, loMergedAllUserList);
		loInsertDeleteListMap.put(HHSConstants.AS_DELETE_LIST, loMergedAllUserListCopy);

		return loInsertDeleteListMap;
	}

	/**
	 * <p>
	 * This is a private method that is called every time for each level from
	 * level 1 to level 4. It Inserts user into database that are newly added to
	 * Levels, level 1 to level 4
	 * </p>
	 * <ul>
	 * <li>It receives level users allAgencyUsersList variable of
	 * AgencySettingsBean</li>
	 * <li>Associate iterator with list, allAgencyUsersList</li>
	 * <li>Call masterDao to insert user one by one into database</li>
	 * <li>Iterate till whole list is traversed</li>
	 * </ul>
	 * 
	 * @param aoInsertAgencySettingsBean AgencySettingsBean Object
	 * @param aoMyBatisSession SqlSession Object
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	private void levelUsersInsert(AgencySettingsBean aoInsertAgencySettingsBean, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		try
		{
			CityUserDetailsBean loCityUserDetailsBean = null;
			Iterator<CityUserDetailsBean> loInsertItr = aoInsertAgencySettingsBean.getAllAgencyUsersList().iterator();
			while (loInsertItr.hasNext())
			{
				loCityUserDetailsBean = (CityUserDetailsBean) loInsertItr.next();
				aoInsertAgencySettingsBean.setLevelId(loCityUserDetailsBean.getLevelId());
				aoInsertAgencySettingsBean.setLevelReviewerId(loCityUserDetailsBean.getUserId());

				DAOUtil.masterDAO(aoMyBatisSession, aoInsertAgencySettingsBean,
						HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS, HHSConstants.AS_INSERT_LEVEL_USERS,
						HHSConstants.AS_AGENCY_SETTING_BEAN_FILE_PATH);
				setMoState("AgencySettingService: levelUsersInsert method successful");
			}
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.INS_AGENCY_SETTING_BEAN,
					CommonUtil.convertBeanToString(aoInsertAgencySettingsBean));
			LOG_OBJECT.Error("Exception occured in AgencySettingService: levelUsersInsert method:: ", loAppEx);
			setMoState("Transaction Failed:: AgencySettingService: levelUsersInsert method - failed. Exception "
					+ "occured while inserting list of users at a particular level. \n");
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "AgencySettingService: levelUsersInsert method:: ", loEx);
			loAppEx.addContextData(HHSConstants.LO_INS_AGENCY_SETTING_BEAN,
					CommonUtil.convertBeanToString(aoInsertAgencySettingsBean));
			LOG_OBJECT.Error("Exception occured in AgencySettingService: levelUsersInsert method:: ", loAppEx);
			setMoState("Transaction Failed:: AgencySettingService: levelUsersInsert method - failed. Exception "
					+ "occured while inserting list of users at a particular level. \n");
			throw loAppEx;
		}
	}

	/**
	 * <p>
	 * This is a private method that is called every time for each level from
	 * level 1 to level 4. It deletes the user from database that are removed
	 * from levels
	 * </p>
	 * <ul>
	 * <li>It receives level users allAgencyUsersList variable of
	 * AgencySettingsBean</li>
	 * <li>Associate iterator with list, allAgencyUsersList</li>
	 * <li>Call masterDao to delete user one by one from database</li>
	 * <li>Iterate till whole list is traversed</li>
	 * </ul>
	 * 
	 * @param aoDeleteAgencySettingsBean AgencySettingsBean Object
	 * @param aoMyBatisSession SqlSession Object
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	private void levelUsersDelete(AgencySettingsBean aoDeleteAgencySettingsBean, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		try
		{
			CityUserDetailsBean loCityUserDetailsBean = null;
			Iterator<CityUserDetailsBean> loInsertItr = aoDeleteAgencySettingsBean.getAllAgencyUsersList().iterator();
			while (loInsertItr.hasNext())
			{
				loCityUserDetailsBean = (CityUserDetailsBean) loInsertItr.next();
				aoDeleteAgencySettingsBean.setLevelId(loCityUserDetailsBean.getLevelId());
				aoDeleteAgencySettingsBean.setLevelReviewerId(loCityUserDetailsBean.getUserId());

				DAOUtil.masterDAO(aoMyBatisSession, aoDeleteAgencySettingsBean,
						HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS, HHSConstants.AS_DELETE_LEVEL_USERS,
						HHSConstants.AS_AGENCY_SETTING_BEAN_FILE_PATH);
				setMoState("AgencySettingService: levelUsersDelete method successful");
			}
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("aoDeleteAgencySettingsBean",
					CommonUtil.convertBeanToString(aoDeleteAgencySettingsBean));
			LOG_OBJECT.Error("Exception occured in AgencySettingService: levelUsersDelete method:: ", loAppEx);
			setMoState("Transaction Failed:: AgencySettingService: levelUsersDelete method - failed. Exception "
					+ "occured while deleting list of users from particular level. \n");
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "AgencySettingService: levelUsersDelete method:: ", loEx);
			loAppEx.addContextData("aoDeleteAgencySettingsBean",
					CommonUtil.convertBeanToString(aoDeleteAgencySettingsBean));
			LOG_OBJECT.Error("Exception occured in AgencySettingService: levelUsersDelete method:: ", loAppEx);
			setMoState("Transaction Failed:: AgencySettingService: levelUsersDelete method - failed. Exception "
					+ "occured while deleting list of users from particular level. \n");
			throw loAppEx;
		}
	}

	/**
	 * <p>
	 * This is a private method used to segregate users into Level1 to Level4
	 * and set them into AgencySettingBean
	 * </p>
	 * <ul>
	 * <li>It receives all users from level1 to leve4 into single list</li>
	 * <li>Associate iterator with list and traverse whole list</li>
	 * <li>Based upon level id segregate user into level1 to level4 list</li>
	 * <li>After Iterating whole list set level1 to level4 users in
	 * AgencySettingsBean and return it</li>
	 * </ul>
	 * 
	 * @param aoAllAssgndUsersList list of type CityUserDetailsBean
	 * @return AgencySettingsBean AgencySettingsBean object
	 * @throws Exception Exception thrown in case of any application code
	 *             failure.
	 */
	private AgencySettingsBean segregateLevel1To4Users(List<CityUserDetailsBean> aoAllAssgndUsersList) throws Exception
	{

		AgencySettingsBean loAgencySettingsBean = new AgencySettingsBean();
		List<CityUserDetailsBean> loLevel1UsersList = new ArrayList<CityUserDetailsBean>();
		List<CityUserDetailsBean> loLevel2UsersList = new ArrayList<CityUserDetailsBean>();
		List<CityUserDetailsBean> loLevel3UsersList = new ArrayList<CityUserDetailsBean>();
		List<CityUserDetailsBean> loLevel4UsersList = new ArrayList<CityUserDetailsBean>();

		Iterator<CityUserDetailsBean> loItr = aoAllAssgndUsersList.iterator();
		while (loItr.hasNext())
		{
			CityUserDetailsBean loCityUserDetailsBean = loItr.next();
			switch (loCityUserDetailsBean.getLevelId())
			{
				case 1:
					loLevel1UsersList.add(loCityUserDetailsBean);
					break;
				case 2:
					loLevel2UsersList.add(loCityUserDetailsBean);
					break;
				case 3:
					loLevel3UsersList.add(loCityUserDetailsBean);
					break;
				case 4:
					loLevel4UsersList.add(loCityUserDetailsBean);
					break;
				default:
					break;
			}
		}
		loAgencySettingsBean.setAllLevel1UsersList(loLevel1UsersList);
		loAgencySettingsBean.setAllLevel2UsersList(loLevel2UsersList);
		loAgencySettingsBean.setAllLevel3UsersList(loLevel3UsersList);
		loAgencySettingsBean.setAllLevel4UsersList(loLevel4UsersList);
		return loAgencySettingsBean;
	}

	/**
	 * <p>
	 * This is a private method used to fetch all opened task for a particular
	 * agency and review process type. If any task are in open status then
	 * accelerator user is not allowed to save/change levels of review.
	 * </p>
	 * <ul>
	 * <li>Gets count of all task which are in open status</li>
	 * <li>for selected agency under particular review process type</li>
	 * <li>set count retrieved from filenet in loFileNetRetrndMap and return Map
	 * </li>
	 * </ul>
	 * 
	 * @param aoQueryMap contains agency id and review process id
	 * @param aoFilenetSession P8UserSession object
	 * @return Map<String, Object> contains count of all opened task
	 * @throws Exception Exception thrown in case of any application code
	 *             failure.
	 */
	private Map<String, Object> fetchOpenedTaskCount(Map<String, Object> aoQueryMap, P8UserSession aoFilenetSession)
			throws Exception
	{
		int liOpenCount = HHSConstants.INT_ZERO;
		HashMap<String, Object> loFileNetRetrndMap = new HashMap<String, Object>();
		HashMap<String, Object> loFileNetQueryMap = new HashMap<String, Object>();
		String lsProcessId = (String.valueOf((Integer) aoQueryMap.get(HHSConstants.REVIEW_PROCESS_ID_KEY)));
		loFileNetQueryMap.put(HHSConstants.PROPERTY_PE_AGENCY_ID, (String) aoQueryMap.get(HHSConstants.AGENCYID));
		loFileNetQueryMap.put(HHSConstants.PROPERTY_PE_TASK_TYPE,
				HHSConstants.FINANCIAL_TASK_ID_PROCESS_MAP.get(lsProcessId));
		liOpenCount = new P8ProcessServiceForSolicitationFinancials().getOpenTaskCount(aoFilenetSession,
				loFileNetQueryMap);
		loFileNetRetrndMap.put(HHSConstants.AS_OPEN_TASK_COUNT, liOpenCount);
		return loFileNetRetrndMap;
	}

	/**
	 * <p>
	 * This is a private method used to delete users from
	 * AGENCY_SETTING_LEVEL_USERS. It deletes all users that are allocated to
	 * higher levels than to currently changed maximum level of review
	 * </p>
	 * <ul>
	 * <li>It is called when accelerator user wants to decrease level of review
	 * for a particular agency for a particular review process</li>
	 * <li>Example Levels of review is changed from 4 to 2</li>
	 * <li>If above example is the case then all users at level3 and level4 that
	 * are assigned needs to be free and again available to be assigned to
	 * level1 to level2</li>
	 * </ul>
	 * 
	 * @param aoAgencySettingsBean AgencySettingsBean Object
	 * @param aoMyBatisSession SqlSession Object
	 * @throws Exception Exception thrown in case of any application code
	 *             failure.
	 */
	private void checkAndDeleteLevelUsers(AgencySettingsBean aoAgencySettingsBean, SqlSession aoMyBatisSession)
			throws Exception
	{
		int liOldLevelofReview = aoAgencySettingsBean.getOldLevelOfReview();
		int liNewLevelofReview = aoAgencySettingsBean.getLevelOfReview();

		if (liOldLevelofReview > liNewLevelofReview)
		{
			while (liOldLevelofReview - liNewLevelofReview != HHSConstants.INT_ZERO)
			{
				aoAgencySettingsBean.setLevelId(liOldLevelofReview);
				DAOUtil.masterDAO(aoMyBatisSession, aoAgencySettingsBean, HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS,
						HHSConstants.AS_DELETE_LEVEL_USERS_VIA_ACCELERATOR,
						HHSConstants.AS_AGENCY_SETTING_BEAN_FILE_PATH);
				liOldLevelofReview--;
			}
		}
	}

	/**
	 * <p>
	 * This is private method used to fetch level1 users when certification of
	 * funds task is selected Below business logic is used to populate level1
	 * users
	 * <p>
	 * <ul>
	 * <li>If <Review Process> is Amendment Certification of Funds then populate
	 * with <Level 1 Users> for <Review Process> = Contract Configuration
	 * Amendment.</li>
	 * <li>If <Review Process> is Contract Certification of Funds then populate
	 * with <Level 1 Users> for <Review Process> = Contract Configuration
	 * (Initial/Renewal/New Contract)</li>
	 * </ul>
	 * 
	 * @param aoAgencySettingsBean AgencySettingsBean Object
	 * @param aoMyBatisSession SqlSession Object
	 * @throws Exception Exception thrown in case of any application code
	 *             failure.
	 */
	@SuppressWarnings("unchecked")
	private void fetchLevel1UsersIfCoFTask(AgencySettingsBean aoAgencySettingsBean, SqlSession aoMyBatisSession)
			throws Exception
	{
		Map<String, Object> loQueryMap = new HashMap<String, Object>();
		loQueryMap.put(HHSConstants.AGENCYID, aoAgencySettingsBean.getAgencyId());

		List<CityUserDetailsBean> loAllLevelUsersList = null;
		List<CityUserDetailsBean> loLevel1UsersList = null;
		if (aoAgencySettingsBean.getLevelOfReview() > 0)
		{
			if (aoAgencySettingsBean.getReviewProcessId() == HHSConstants.AMENDMENT_CERTIFICATION_OF_FUNDS_ID)

			{
				loQueryMap.put(HHSConstants.REVIEW_PROCESS_ID_KEY, HHSConstants.CONTRACT_CONFIGURATION_AMENDMENT_ID);
				loLevel1UsersList = (List<CityUserDetailsBean>) DAOUtil.masterDAO(aoMyBatisSession, loQueryMap,
						HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS, HHSConstants.AS_FETCH_LEVEL1_USERS_COF_TASK,
						HHSConstants.JAVA_UTIL_MAP);
				aoAgencySettingsBean.setAllLevel1UsersList(loLevel1UsersList);
				aoAgencySettingsBean.getAllAgencyUsersList().removeAll(loLevel1UsersList);
			}
			else if (aoAgencySettingsBean.getReviewProcessId() == HHSConstants.CONTRACT_CERTIFICATION_OF_FUNDS_ID)
			{
				loQueryMap.put(HHSConstants.REVIEW_PROCESS_ID_KEY,
						HHSConstants.CONTRACT_CONFIGURATION_INITIAL_REN_NEW_ID);
				loLevel1UsersList = (List<CityUserDetailsBean>) DAOUtil.masterDAO(aoMyBatisSession, loQueryMap,
						HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS, HHSConstants.AS_FETCH_LEVEL1_USERS_COF_TASK,
						HHSConstants.JAVA_UTIL_MAP);
				aoAgencySettingsBean.setAllLevel1UsersList(loLevel1UsersList);
				aoAgencySettingsBean.getAllAgencyUsersList().removeAll(loLevel1UsersList);
			}
			else if (aoAgencySettingsBean.getReviewProcessId() == HHSConstants.CONTRACT_CONFIGURATION_INITIAL_REN_NEW_ID)
			{
				loQueryMap.put(HHSConstants.REVIEW_PROCESS_ID_KEY, HHSConstants.CONTRACT_CERTIFICATION_OF_FUNDS_ID);
				loAllLevelUsersList = (List<CityUserDetailsBean>) DAOUtil.masterDAO(aoMyBatisSession, loQueryMap,
						HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS, HHSConstants.AS_FETCH_ASSIGNED_USER_NAMES,
						HHSConstants.JAVA_UTIL_MAP);
				aoAgencySettingsBean.getAllAgencyUsersList().removeAll(loAllLevelUsersList);
			}
			else if (aoAgencySettingsBean.getReviewProcessId() == HHSConstants.CONTRACT_CONFIGURATION_AMENDMENT_ID)
			{
				loQueryMap.put(HHSConstants.REVIEW_PROCESS_ID_KEY, HHSConstants.AMENDMENT_CERTIFICATION_OF_FUNDS_ID);
				loAllLevelUsersList = (List<CityUserDetailsBean>) DAOUtil.masterDAO(aoMyBatisSession, loQueryMap,
						HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS, HHSConstants.AS_FETCH_ASSIGNED_USER_NAMES,
						HHSConstants.JAVA_UTIL_MAP);
				aoAgencySettingsBean.getAllAgencyUsersList().removeAll(loAllLevelUsersList);
			}
		}
	}

	/**
	 * <p>
	 * This is a private method used to set level of review to 1 for the
	 * configuration tasks. Configuration tasks are only level1 task only so
	 * accelerator user does not associate level of review for these tasks via
	 * S404 screen.
	 * </p>
	 * <ul>
	 * <li>Configuration tasks are:</li>
	 * <li>1. ID:9 Contract Configuration (Initial/Renewal/New Contract)</li>
	 * <li>2. ID:10 Contract Configuration Amendment</li>
	 * <li>1. ID:11 Contract Configuration Update</li>
	 * <li>1. ID:13 New Fiscal Year Configuration</li>
	 * <li>It is called when agency user selects any configuration task to fetch
	 * user details</li>
	 * <li>Insert call is made to set levels of review to 1 in case not already
	 * set to 1</li>
	 * </ul>
	 * 
	 * @param aoQueryMap Map Object contains agencyid, review process id, user
	 *            id
	 * @param aoMyBatisSession SqlSession Object
	 * @throws Exception Exception thrown in case of any application code
	 *             failure.
	 */
	private void setConfigReviewLevlTo1(Map<String, Object> aoQueryMap, SqlSession aoMyBatisSession) throws Exception
	{
		AgencySettingsBean loAgencySettingsBean = new AgencySettingsBean();
		loAgencySettingsBean.setAgencyId((String) (aoQueryMap.get(HHSConstants.AGENCYID)));
		loAgencySettingsBean.setReviewProcessId((Integer) (aoQueryMap.get(HHSConstants.REVIEW_PROCESS_ID_KEY)));

		loAgencySettingsBean.setLevelOfReview(1);
		loAgencySettingsBean.setLastUpdateDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
		loAgencySettingsBean.setCreatedDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
		loAgencySettingsBean.setCreatedByUserId((String) (aoQueryMap.get(HHSConstants.USER_ID)));
		loAgencySettingsBean.setModifiedDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
		loAgencySettingsBean.setModifiedByUserId((String) (aoQueryMap.get(HHSConstants.USER_ID)));
		// Start || Changes done for enhancement 6534 for Release 3.8.0
		loAgencySettingsBean.setReviewLevelChangeInProgress(HHSConstants.NO);
		// End || Changes done for enhancement 6534 for Release 3.8.0
		DAOUtil.masterDAO(aoMyBatisSession, loAgencySettingsBean, HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS,
				HHSConstants.AS_INSERT_REVIEW_LEVELS, HHSConstants.AS_AGENCY_SETTING_BEAN_FILE_PATH);

	}

	/**
	 * <p>
	 * This is a private method used to set task to unassigned queue When a
	 * level reviewer has been removed via S404 screen then Filenet needs to
	 * delete that user to take any future task and if any task is already
	 * assigned then to move that task to unassigned queue.
	 * </p>
	 * <ul>
	 * <li>Gets list of all users with their level ids' in
	 * List<CityUserDetailsBean></li>
	 * <li>remove user and put task to unassigned queue</li>
	 * </li>
	 * </ul>
	 * 
	 * @param aoAgencyId agency id
	 * @param aiReviewProcId review process id
	 * @param aoAssgndToUnAssgndList list of that needs to be removed from task
	 *            if any associated with them
	 * @param aoFilenetSession P8UserSession object
	 * @throws Exception Exception thrown in case of any application code
	 *             failure.
	 */
	private void setTaskToUnAssignedQueue(String aoAgencyId, int aiReviewProcId,
			List<CityUserDetailsBean> aoAssgndToUnAssgndList, P8UserSession aoFilenetSession) throws Exception
	{
		HashMap loHMWFRequiredProps = new HashMap();
		String lsProcessId = (String.valueOf(aiReviewProcId));
		String lsTaskType = (String) HHSConstants.FINANCIAL_TASK_ID_PROCESS_MAP.get(lsProcessId);
		int liCount = 0;
		String[] loUserIdArray = new String[aoAssgndToUnAssgndList.size()];
		for (CityUserDetailsBean loCityUserDetailsBean : aoAssgndToUnAssgndList)
		{
			loUserIdArray[liCount] = loCityUserDetailsBean.getUserId();
			liCount++;
		}
		if (null != loUserIdArray && loUserIdArray.length > HHSConstants.INT_ZERO)
		{
			loHMWFRequiredProps.put(HHSConstants.COMPONENT_ACTION, HHSConstants.AGENCY_USER_REMOVE_COMPONENT_ACTION);
			loHMWFRequiredProps.put(HHSConstants.PROPERTY_PE_TASK_TYPE, lsTaskType);
			loHMWFRequiredProps.put(HHSConstants.COMP_AGENCY_ID, aoAgencyId);
			loHMWFRequiredProps.put(HHSConstants.COMP_USERS, loUserIdArray);
			new P8ProcessServiceForSolicitationFinancials().launchWorkflow(aoFilenetSession,
					HHSConstants.WF_FINANCIAL_UTILITY, loHMWFRequiredProps);
		}
	}

	/**
	 * <p>
	 * This method added as a part of release 3.8.0 enhancement 6534 for
	 * updating review_level_chg_in_progress flag in agency_setting table
	 * </p>
	 * 
	 * @param aoMyBatisSession SQLSession object
	 * @param aoAgencyDetailsMap aoAgencyDetailsMap
	 * @return lbUpdateFlag lbUpdateFlag
	 * @throws ApplicationException ApplicationException object
	 */
	@SuppressWarnings("rawtypes")
	public boolean updateReviewInProgressFlag(SqlSession aoMybatisSession, Map aoAgencyDetailsMap)
			throws ApplicationException, Exception
	{
		boolean lbUpdateFlag = false;
		try
		{
			//[Start]R7.12.0 QC9311 Minimize Debug
			//LOG_OBJECT.Debug("updating REVIEW_LEVEL_CHG_IN_PROGRESS with parameters :: " + aoAgencyDetailsMap);
			//[End]R7.12.0 QC9311 Minimize Debug
			DAOUtil.masterDAO(aoMybatisSession, aoAgencyDetailsMap, HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS,
					HHSConstants.UPDATE_REVIEW_PROGRESS_FLAG, HHSConstants.JAVA_UTIL_MAP);

			lbUpdateFlag = true;
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error( "Exception occured while updating review level progress flag AgencySettingService: updateReviewInProgressFlag \n"
							+ " parameters:: "+ aoAgencyDetailsMap.toString(), loAppEx);
			setMoState("Transaction Failed:: AgencySettingService: updateReviewInProgressFlag method - failed Exception occured"
					+ " while updating review levels with details aoAgencyDetailsMap::" + aoAgencyDetailsMap + "\n");
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error(
					"Exception occured while updating review level progress flag AgencySettingService: updateReviewInProgressFlag"
							+ " parameters:: "+ aoAgencyDetailsMap.toString(), loEx);
			setMoState("Transaction Failed:: AgencySettingService: updateReviewInProgressFlag method - failed Exception occured"
					+ " while updating review levels with details aoAgencyDetailsMap::" + aoAgencyDetailsMap + "\n");
			throw loEx;
		}
		return lbUpdateFlag;
	}

	/**
	 * <p>
	 * This method added as a part of release 3.8.0 enhancement 6534 for
	 * fetching review_level_chg_in_progress flag in agency_setting table
	 * </p>
	 * 
	 * @param aoMyBatisSession SQLSession object
	 * @param aoAgencyDetailsMap aoAgencyDetailsMap
	 * @return lsReviewFlag String
	 * @throws ApplicationException ApplicationException object
	 */
	public String fetchReviewInProgressFlag(SqlSession aoMybatisSession, HashMap<String, String> aoAgencyDetailsMap)
			throws ApplicationException, Exception
	{
		//[Start]R7.12.0 QC9311 Minimize Debug
		//LOG_OBJECT.Debug("Entered AgencySettingService: fetchReviewInProgressFlag");
		//[End]R7.12.0 QC9311 Minimize Debug
		String lsReviewFlag = null;
		try
		{
			lsReviewFlag = (String) DAOUtil.masterDAO(aoMybatisSession, aoAgencyDetailsMap,
					HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS, HHSConstants.FETCH_REVIEW_PROGRESS_FLAG,
					HHSConstants.JAVA_UTIL_HASH_MAP);
		}
		catch (ApplicationException loAppEx)
		{
			//[Start]R7.12.0 QC9311 Minimize Debug
			LOG_OBJECT.Error(
					"Exception occured while fetching review level progress flag AgencySettingService: fetchReviewInProgressFlag"
							+ "\n Parameter:: " + aoAgencyDetailsMap.toString() , loAppEx);
			//[End]R7.12.0 QC9311 Minimize Debug
			setMoState("Transaction Failed:: AgencySettingService: fetchReviewInProgressFlag method - failed Exception occured"
					+ " while fetching review levels with details aoAgencyDetailsMap::" + aoAgencyDetailsMap + "\n");
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			//[Start]R7.12.0 QC9311 Minimize Debug
			LOG_OBJECT.Error(
					"Exception occured while fetching review level progress flag AgencySettingService: fetchReviewInProgressFlag"
							+ "\n Parameter:: " + aoAgencyDetailsMap.toString(), loEx);
			//[End]R7.12.0 QC9311 Minimize Debug
			setMoState("Transaction Failed:: AgencySettingService: fetchReviewInProgressFlag method - failed Exception occured"
					+ " while fetching review levels with details aoAgencyDetailsMap::" + aoAgencyDetailsMap + "\n");
			throw loEx;
		}
		return lsReviewFlag;
	}

	// R7 start :Modification Auto Approval Enhancement
	/**
	 * <p>
	 * This method is added as a part of R7 Modification Auto Approval Enhancement
	 * This is AgencySettingService service class method and is called by city
	 * users on page load to fetch all agency names 
	 * from database. City user can select an agency
	 * and can associate auto approval threshold value for budget modification.
	 * </p>
	 * <ul>
	 * <li>Called by City user, screen-S404</li>
	 * <li>Step executed are:</li>
	 * <li>1.Fetch all agency names</li>
	 * </ul>
	 * @param aoMyBatisSession SQLSession object
	 * @return loAgencySettingsBean AgencySettingsBean - bean containing list of
	 *         all agency names 
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	@SuppressWarnings("unchecked")
	public AgencySettingsBean fetchAgencyList(SqlSession aoMyBatisSession) throws ApplicationException
	{
		AgencySettingsBean loAgencySettingsBean = null;
		List<AgencyDetailsBean> loNYCAgencyList = null;
		try
		{
			// fetch all agency names (11 agencies for which document sharing
			// flag is set to 1)
			loNYCAgencyList = (List<AgencyDetailsBean>) DAOUtil.masterDAO(aoMyBatisSession, null,
					HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS, HHSConstants.FETCH_AGENCY_NAMES, null);

			
			loNYCAgencyList = formatAgencyNameList(loNYCAgencyList);
			loAgencySettingsBean = new AgencySettingsBean();
			loAgencySettingsBean.setAllAgencyDetailsBeanList(loNYCAgencyList);

			setMoState("AgencySettingService: fetchAgencyList() All agency names are fetched successfully.");
			return loAgencySettingsBean;
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured in AgencySettingService: fetchAgencyList method:: ",
					loAppEx);
			setMoState("Transaction Failed:: AgencySettingService: fetchAgencyList method - failed."
					+ " Exception occured while fetching all agency user names data. \n");
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Exception occured in AgencySettingService: fetchAgencyList method:: ",
					loEx);
			setMoState("Transaction Failed:: AgencySettingService: fetchAgencyList method - failed."
					+ " Exception occured while fetching all agency user names data. \n");
			throw new ApplicationException(
					"Error occured in AgencySettingService: fetchAgencyList method:: ", loEx);
		}
	}

	/**
	 * <p>
	 * This method added as a part of R7 Modification Auto Approval Enhancement
	 * This is AgencySettingService service class method and is called by city
	 * users when user selects an agency and
	 * hit 'Go' button. It retrieves threshold value that city user has set for
	 * a particular agency.
	 * </p>
	 * <ul>
	 * <li>Called by Accelerator user, screen-S404</li>
	 * <li>Step executed are:</li>
	 * <li>1.Get the threshold value if it exist in database for a particular
	 * agency </li>
	 * <li>2.If no record found the explicitly set threshold value to zero</li>
	 * <li>3.Else assign threshold value retrieved from database</li>
	 * </ul>
	 * 
	 * @param asAgencyId String - agency id of Agency
	 * @param aoMyBatisSession SQLSession object
	 * @return loDefaultThresholdBean AutoApprovalConfigBean - bean containing the threshold value and modified by, 
	 * modified date fields
	 * @throws ApplicationException ApplicationException object
	 */
	public AutoApprovalConfigBean fetchAutoApprovalThreshold(String asAgencyId, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		AutoApprovalConfigBean loDefaultThresholdBean = null;
		Map<String, Object> loQueryMap = new HashMap<String, Object>();
		try
		{
			loQueryMap.put(HHSConstants.AGENCYID, asAgencyId);
			loDefaultThresholdBean = (AutoApprovalConfigBean) DAOUtil.masterDAO(aoMyBatisSession, loQueryMap,
					HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS, HHSR5Constants.AS_FETCH_AUTO_APPROVE_THRESHOLD,
					HHSConstants.JAVA_UTIL_MAP);
			setMoState("AgencySettingService: fetchAutoApprovalThreshold - AutoApprovalThreshold is successfully retrieved for"
					+ " agency id ::"
					+ loQueryMap.get(HHSConstants.AGENCYID));
			
			return loDefaultThresholdBean;
		}
		// Application Exception is handled here
		catch (ApplicationException aoAppEx)
		{
			// Context Data is added into Exception object and Log is updated
			aoAppEx.addContextData(HHSConstants.AGENCYID, loQueryMap.get(HHSConstants.AGENCYID));
			LOG_OBJECT
					.Error("Exception occured in AgencySettingService: fetchAutoApprovalThreshold method:: ", aoAppEx);
			setMoState("Transaction Failed:: AgencySettingService: fetchAutoApprovalThreshold method - failed Exception "
					+ "occured while fetching deafult Auto Approval Threshold for agency id ::"
					+ loQueryMap.get(HHSConstants.AGENCYID));
			throw aoAppEx;
		}
		// Exception is handled here
		catch (Exception aoEx)
		{
			// Context Data is added into Exception object and Log is updated
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "AgencySettingService: fetchAutoApprovalThreshold method:: ", aoEx);
			loAppEx.addContextData(HHSConstants.AGENCYID, loQueryMap.get(HHSConstants.AGENCYID));
			LOG_OBJECT
					.Error("Exception occured in AgencySettingService: fetchAutoApprovalThreshold method:: ", loAppEx);
			setMoState("Transaction Failed:: AgencySettingService: fetchAutoApprovalThreshold method - failed "
					+ "Exception occured while fetching default Auto Approval Threshold for agency id ::"
					+ loQueryMap.get(HHSConstants.AGENCYID));
			throw loAppEx;
		}
	}

	/**
	 * <p>
	 * This method added as a part of R7 Modification Auto Approval Enhancement
	 * This is AgencySettingService service class method and is called by city
	 * users when user enters the threshold value for a particular agency 
	 * It saves the threshold value in database.
	 * </p>
	 * <ul>
	 * <li>Called by City user, screen-S404</li>
	 * <li>Step executed are:</li>
	 * 
	 * <li>2.Always update the threshold value for this agency as we have default 
	 * entry of 0 for all agencies in the DB</li>
	 * <li>1.Insert entry in the history table for all the previous existing threshold for the selected agency</li>
	 * <li>2.Increment the version id for the existing threshold for the selected agency</li>
	 * <li>3.Update the threshold value for this agency</li>
	 * </ul>
	 * @param asAgencyId String - agency id of Agency
	 * @param aiThresholdValue threshold value entered
	 * @param asUserId modified by/created by user id
	 * @param aoMyBatisSession SQLSession object
	 * @return boolean status of insert/update database call
	 * @throws ApplicationException ApplicationException object
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Boolean saveAutoApprovalThreshold(String asAgencyId, Integer aiThresholdValue,
			String asUserId, SqlSession aoMyBatisSession) throws ApplicationException
	{
		Map<String, Object> loQueryMap = new HashMap<String, Object>();
		Boolean lbSaveStatus = false;
		Boolean lbSaveStatusConfig;
		
		if (null==aiThresholdValue || aiThresholdValue < HHSConstants.INT_ZERO || aiThresholdValue > 100)
			return false;
		

		try
		{
			loQueryMap.put(HHSConstants.AGENCYID, asAgencyId);
			loQueryMap.put(HHSR5Constants.THRESHOLD_PERCENTAGE, aiThresholdValue);
			loQueryMap.put(HHSConstants.AS_USER_ID, asUserId);
			loQueryMap.put(HHSR5Constants.REVIEW_PROCESS_ID, 5);
			
				lbSaveStatus = (Boolean) DAOUtil.masterDAO(aoMyBatisSession, asAgencyId,
						HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS,
						HHSR5Constants.AB_INSERT_AUTO_APPROVE_THRESHOLD_HISTORY, HHSConstants.JAVA_LANG_STRING);
				DAOUtil.masterDAO(aoMyBatisSession, asAgencyId,
						HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS,
						HHSR5Constants.UPDATE_AUTO_APPROVAL_VERSION_ID, HHSConstants.JAVA_LANG_STRING);
				
					// Updating Threshold Value
						lbSaveStatusConfig = (Boolean) DAOUtil.masterDAO(aoMyBatisSession, loQueryMap,
								HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS,
								HHSR5Constants.AB_UPDATE_AUTO_APPROVE_CONFIG, HHSConstants.JAVA_UTIL_MAP);
				setMoState("AgencySettingService: saveAutoApprovalThreshold method -Threshold value is successfully saved "
						+ lbSaveStatus);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured in AgencySettingService: saveAutoApprovalThreshold method:: ", loAppEx);
			setMoState("Transaction Failed:: AgencySettingService: saveAutoApprovalThreshold method - failed Exception occured"
					+ " while saving threshold value::" + aiThresholdValue + "\n");
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "AgencySettingService: saveReviewLevels method:: ", loEx);
			LOG_OBJECT.Error("Exception occured in AgencySettingService: saveAutoApprovalThreshold method:: ", loAppEx);
			setMoState("Transaction Failed:: AgencySettingService: saveAutoApprovalThreshold method - failed Exception occured"
					+ " while saving threshold value::" + aiThresholdValue + "\n");
			throw loAppEx;
		}
		return lbSaveStatusConfig;
	}
	
	/**
	 * <p>
	 * This method added as a part of R7 Modification Auto Approval Enhancement
	 * This is AgencySettingService service class method and is called by city
	 * users. It fetch the auto approval threshold values configured for the providers  
	 * under an agency in th JQGrid.
	 * </p>
	 * <ul>
	 * <li>Called by City user, screen-S404</li>
	 * <li>Step executed are:</li>
	 * 
	 * <li>1.Fetch the threshold values configured for providers under the selected agency</li>
	 * </ul>
	 * @param asAgencyId String - agency id of Agency
	 * @param aoMyBatisSession SQLSession object
	 * @return loAutoApprovalConfigBeans - List<AutoApprovalConfigBean> list of bean
	 * containing the threshold configured for providers.
	 * @throws ApplicationException ApplicationException object
	 */
	@SuppressWarnings(
			{ "unchecked", "rawtypes" })
	public List<AutoApprovalConfigBean> fetchAutoApprovalDetailsList(String asAgencyId,SqlSession aoMyBatisSession) throws ApplicationException{
		List<AutoApprovalConfigBean> loAutoApprovalConfigBeans = new ArrayList<AutoApprovalConfigBean>() ;;
		List<AutoApprovalConfigBean> loAutoApprovalConfigBeansList = null;
		try
		{
			loAutoApprovalConfigBeansList =  (ArrayList<AutoApprovalConfigBean>) DAOUtil.masterDAO(aoMyBatisSession, asAgencyId,
					HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS, HHSR5Constants.FETCH_AUTO_APPROVAL_DETAILS_LIST,
					HHSConstants.JAVA_LANG_STRING);
			if(loAutoApprovalConfigBeansList != null){
				for (AutoApprovalConfigBean autoApprovalConfigBean : loAutoApprovalConfigBeansList)
				{
					if(autoApprovalConfigBean.getEmpPosition().contains("\"")){
						String orgLegalName = autoApprovalConfigBean.getEmpPosition().replace("\"", "\'");
						autoApprovalConfigBean.setEmpPosition(orgLegalName);
					}
					loAutoApprovalConfigBeans.add(autoApprovalConfigBean);
				}
			}
			
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured in AgencySettingService: fetchAgencyProviderDetailsList method:: ", loAppEx);
			setMoState("Transaction Failed:: AgencySettingService: fetchAgencyProviderDetailsList method - failed Exception occured"
					+ " while retrieving list of provider details::" + loAutoApprovalConfigBeans + "\n");
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "AgencySettingService: fetchAutoApprovalDetailsList method:: ", loEx);
			LOG_OBJECT.Error("Exception occured in AgencySettingService: fetchAgencyProviderDetailsList method:: ", loAppEx);
			setMoState("Transaction Failed:: AgencySettingService: fetchAgencyProviderDetailsList method - failed Exception occured"
					+ " while retrieving list of providers details::" + loAutoApprovalConfigBeans + "\n");
			throw loAppEx;
		}
		
		return loAutoApprovalConfigBeans;
	}
	
	/**
	 * <p>
	 * This method added as a part of R7 Modification Auto Approval Enhancement
	 * This is AgencySettingService service class method and is called by city
	 * users. It fetch the list of providers under an agency which need to be   
	 * populated in the provider drop down in JQGrid.
	 * </p>
	 * <ul>
	 * <li>Called by City user, screen-S404</li>
	 * <li>Step executed are:</li>
	 * <li>1.Fetch the list of providers under the selected agency</li>
	 * </ul>
	 * @param asAgencyId String - agency id of Agency
	 * @param aoMyBatisSession SQLSession object
	 * @return loAgencyProviders - List<String> list of providers
	 * under the selected agency.
	 * @throws ApplicationException ApplicationException object
	 */
	@SuppressWarnings({ "unchecked" })
	public List<OrganizationBean> fetchAgencyProviders(String asAgencyId, SqlSession aoMyBatisSession) throws ApplicationException {
		List<OrganizationBean> loAgencyProviders = null;
		try {
			//[Start]R7.12.0 QC9311 Minimize Debug
			LOG_OBJECT.Debug("Entering into fetchAgencyProviders with parameter agencyId=::: "+asAgencyId);
			//[End]R7.12.0 QC9311 Minimize Debug
			loAgencyProviders = (List<OrganizationBean>) DAOUtil.masterDAO(aoMyBatisSession, asAgencyId,
					HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS, HHSR5Constants.FETCH_AGENCY_PROVIDERS,
					HHSConstants.JAVA_LANG_STRING);
			//[Start]R7.12.0 QC9311 Minimize Debug
			LOG_OBJECT.Debug("fetchAgencyProviders query executed ");
			//[End]R7.12.0 QC9311 Minimize Debug
		}
		catch (ApplicationException loAppEx)
		{
			//[Start]R7.12.0 QC9311 Minimize Debug
			LOG_OBJECT.Error("Exception occured in AgencySettingService: fetchAgencyProviders " +
					"\n with AgencyId:: " + asAgencyId, loAppEx);
			//[End]R7.12.0 QC9311 Minimize Debug
			setMoState("Transaction Failed:: AgencySettingService: fetchAgencyProviders method - failed Exception occured"
					+ " while retrieving list of providers::" + loAgencyProviders + "\n");
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "AgencySettingService: fetchAgencyProviders method:: ", loEx);
			//[Start]R7.12.0 QC9311 Minimize Debug
			LOG_OBJECT.Error("Exception occured in AgencySettingService: fetchAgencyProviders \n with AgencyId:: " + asAgencyId, loAppEx);
			//[End]R7.12.0 QC9311 Minimize Debug
			setMoState("Transaction Failed:: AgencySettingService: fetchAgencyProviders method - failed Exception occured"
					+ " while retrieving list of providers::" + loAgencyProviders + "\n");
			throw loAppEx;
		}
		//[Start]R7.12.0 QC9311 Minimize Debug
		//LOG_OBJECT.Debug("Exiting fetchAgencyProviders ");
		//[End]R7.12.0 QC9311 Minimize Debug
		return loAgencyProviders;
	}
	
	/**
	 * <p>
	 * This method added as a part of R7 Modification Auto Approval Enhancement
	 * This is AgencySettingService service class method and is called by city
	 * users when they click on delete button in the JQGrid. It will delete the    
	 * threshold configured for the provider in JQGrid.
	 * </p>
	 * <ul>
	 * <li>Called by City user, screen-S404</li>
	 * <li>Step executed are:</li>
	 * <li>1.Add entry in the config history table for the provider which we are deleting</li>
	 * <li>2.Delete threshold configured for the selected provider</li>
	 * </ul>
	 * @param aoAutoApprovalConfigBean AutoApprovalConfigBean - bean containing the threshold details for 
	 * the provider.
	 * @param aoMyBatisSession SQLSession object
	 * @return delStatus - Boolean - delete status of auto approval threshold for provider
	 * under the selected agency.
	 * @throws ApplicationException ApplicationException object
	 */
	public Boolean delAutoApprovalDetails(AutoApprovalConfigBean aoAutoApprovalConfigBean, SqlSession aoMyBatisSession) throws ApplicationException {
		
		Boolean delStatus = false;
		String asAgencyId = aoAutoApprovalConfigBean.getAgencyId();
		try{ 
			DAOUtil.masterDAO(aoMyBatisSession, asAgencyId,
					HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS,
					HHSR5Constants.AB_INSERT_AUTO_APPROVE_THRESHOLD_HISTORY, HHSConstants.JAVA_LANG_STRING);
			DAOUtil.masterDAO(aoMyBatisSession, asAgencyId,
					HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS,
					HHSR5Constants.UPDATE_AUTO_APPROVAL_VERSION_ID, HHSConstants.JAVA_LANG_STRING);
			DAOUtil.masterDAO(aoMyBatisSession, aoAutoApprovalConfigBean,
					HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS, HHSR5Constants.DEL_PROVIDERS_AUTO_APPROVAL_DETAILS,
					HHSR5Constants.AUTO_APPROVAL_CONFIG_BEAN);
			delStatus = true;
		}
		catch (ApplicationException loExp) 
		{
			loExp.addContextData("ApplicationException occured while deleting Custom Provider Details",
					loExp);
			LOG_OBJECT.Error("ApplicationException occured while deleting Custom Provider Details : delAutoApprovalDetails ", loExp);
			setMoState("ApplicationException occured while deleting Custom Provider Details for agency id = "
					+ aoAutoApprovalConfigBean.getAgencyId() + " and organization Id= "
					+ aoAutoApprovalConfigBean.getOrganizationId());
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while deleting Custom Provider Details :  delAutoApprovalDetails ", loExp);
			loAppEx.addContextData("Exception occured while deleting Custom Provider Details", loExp);
			LOG_OBJECT.Error("ApplicationException occured while deleting Custom Provider Details : delAutoApprovalDetails ", loExp);
			setMoState("ApplicationException occured while deleting Custom Provider Details for agency id = "
					+ aoAutoApprovalConfigBean.getAgencyId() + " and organization Id= "
					+ aoAutoApprovalConfigBean.getOrganizationId());
			throw loAppEx;
		}
		return delStatus;
	}
	
	/**
	 * <p>
	 * This method added as a part of R7 Modification Auto Approval Enhancement
	 * This is AgencySettingService service class method and is called by city
	 * users when they click on add button in the JQGrid. It will add the    
	 * threshold configured for the provider in JQGrid.
	 * </p>
	 * <ul>
	 * <li>Called by City user, screen-S404</li>
	 * <li>Step executed are:</li>
	 * <li>1.Check if threshold is configured for the selected provider or not</li>
	 * <li>2.if threshold is not configured for the selected provider, then</li>
	 * <li>3.Add entry in the config history table for the agency under which provider is present</li>
	 * <li>4.Update version id for all the existing entries for the agency/provider under the selected provider</li>
	 * <li>5.Add entry in the config table for the selected provider.</li>
	 * </ul>
	 * @param aoAutoApprovalConfigBean AutoApprovalConfigBean - bean containing the threshold details for 
	 * the provider.
	 * @param aoMyBatisSession SQLSession object
	 * @return addStatus - Boolean - add status of auto approval threshold for provider
	 * under the selected agency.
	 * @throws ApplicationException ApplicationException object
	 */
	public Boolean addAutoApprovalDetails(AutoApprovalConfigBean aoAutoApprovalConfigBean, SqlSession aoMyBatisSession) throws ApplicationException {
		
		Boolean addStatus = false;
		String asAgencyId = aoAutoApprovalConfigBean.getAgencyId();
		LOG_OBJECT.Debug("Entering into addAutoApprovalDetails with parameter::: "+aoAutoApprovalConfigBean);
		try{
			if(aoAutoApprovalConfigBean.getOrganizationName().contains(HHSR5Constants.SELECT_PROVIDER)){
				LOG_OBJECT.Debug("No provider found for your agency");
				throw  new ApplicationException(HHSR5Constants.SELECT_PROVIDER, HHSConstants.GRID_ERROR_MESSAGE,
						HHSR5Constants.SELECT_PROVIDER);
			} else {
			
				AutoApprovalConfigBean loTempValue = (AutoApprovalConfigBean) DAOUtil.masterDAO(aoMyBatisSession, aoAutoApprovalConfigBean,
						HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS, HHSR5Constants.AS_FETCH_AUTO_APPROVE_PROVIDER,
						HHSR5Constants.AUTO_APPROVAL_CONFIG_BEAN);
				LOG_OBJECT.Debug("fetchAutoApproveProvider query returned:: "+loTempValue);
				if (loTempValue == null){
					LOG_OBJECT.Debug("If there is no value for that particular provider ");
						DAOUtil.masterDAO(aoMyBatisSession, asAgencyId,
								HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS,
								HHSR5Constants.AB_INSERT_AUTO_APPROVE_THRESHOLD_HISTORY, HHSConstants.JAVA_LANG_STRING);
						//[Start]R7.12.0 QC9311 Minimize Debug
						//LOG_OBJECT.Debug("insertAutoApprovalThresholdHistory query executed:: ");
						//[End]R7.12.0 QC9311 Minimize Debug
						DAOUtil.masterDAO(aoMyBatisSession, asAgencyId,
								HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS,
								HHSR5Constants.UPDATE_AUTO_APPROVAL_VERSION_ID, HHSConstants.JAVA_LANG_STRING);
						//[Start]R7.12.0 QC9311 Minimize Debug
						LOG_OBJECT.Debug("updateAutoApprovalVersionId query executed:: ");
						//[End]R7.12.0 QC9311 Minimize Debug
						DAOUtil.masterDAO(aoMyBatisSession, aoAutoApprovalConfigBean,
								HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS, HHSR5Constants.ADD_PROVIDERS_AUTO_APPROVAL_DETAILS,
								HHSR5Constants.AUTO_APPROVAL_CONFIG_BEAN);
						LOG_OBJECT.Debug("addAutoApprovalDetails query executed:: ");
						addStatus = true;
				}
				else{
					LOG_OBJECT.Debug("There is already threshold for that provider");
					throw  new ApplicationException("Provider already exists", HHSConstants.GRID_ERROR_MESSAGE,
							"Selected provider already exists in another row, please select a new provider.");
				}
			}
		}
		catch (ApplicationException loExp){
			loExp.addContextData("ApplicationException occured while adding Custom Provider Details",
					loExp);
			LOG_OBJECT.Error("ApplicationException occured while adding Custom Provider Details : addAutoApprovalDetails " +
					"\n Parameter", loExp);
			setMoState("ApplicationException occured while adding Custom Provider Details for agency id = "
					+ aoAutoApprovalConfigBean.getAgencyId() + " and organization Id= "
					+ aoAutoApprovalConfigBean.getOrganizationId());
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loExp){
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while adding Custom Provider Details :  addAutoApprovalDetails ", loExp);
			loAppEx.addContextData("Exception occured while adding Custom Provider Details", loExp);
			LOG_OBJECT.Error("ApplicationException occured while adding Custom Provider Details : addAutoApprovalDetails ", loExp);
			setMoState("ApplicationException occured while adding Custom Provider Details for agency id = "
					+ aoAutoApprovalConfigBean.getAgencyId() + " and organization Id= "
					+ aoAutoApprovalConfigBean.getOrganizationId());
			throw loAppEx;
		}
		LOG_OBJECT.Debug("Update status:::" +addStatus);
		return addStatus;
	}
	
	/**
	 * <p>
	 * This method added as a part of R7 Modification Auto Approval Enhancement
	 * This is AgencySettingService service class method and is called by city
	 * users when they click on edit/save button in the JQGrid. It will update the    
	 * threshold configured for the provider in JQGrid.
	 * </p>
	 * <ul>
	 * <li>Called by City user, screen-S404</li>
	 * <li>Step executed are:</li>
	 * <li>1.Add entry in the config history table for the agency under which provider is present</li>
	 * <li>4.Update version id for all the existing entries for the agency/provider under the selected provider</li>
	 * <li>5.Update entry in the config table for the selected provider.</li>
	 * </ul>
	 * @param aoAutoApprovalConfigBean AutoApprovalConfigBean - bean containing the threshold details for 
	 * the provider.
	 * @param aoMyBatisSession SQLSession object
	 * @return editStatus - Boolean - edit status of auto approval threshold for provider
	 * under the selected agency.
	 * @throws ApplicationException ApplicationException object
	 */
	public Boolean editAutoApprovalDetails(AutoApprovalConfigBean aoAutoApprovalConfigBean, SqlSession aoMyBatisSession) throws ApplicationException {
		
		Boolean editStatus = false;
		String asAgencyId = aoAutoApprovalConfigBean.getAgencyId();
		try{
				
			DAOUtil.masterDAO(aoMyBatisSession, asAgencyId, HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS,
					HHSR5Constants.AB_INSERT_AUTO_APPROVE_THRESHOLD_HISTORY, HHSConstants.JAVA_LANG_STRING);
			 DAOUtil.masterDAO(aoMyBatisSession, asAgencyId, HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS,
					 HHSR5Constants.UPDATE_AUTO_APPROVAL_VERSION_ID, HHSConstants.JAVA_LANG_STRING);
			DAOUtil.masterDAO(aoMyBatisSession, aoAutoApprovalConfigBean,
					HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS, HHSR5Constants.EDIT_PROVIDERS_AUTO_APPROVAL_DETAILS,
					HHSR5Constants.AUTO_APPROVAL_CONFIG_BEAN);
			editStatus = true;
		}
		catch (ApplicationException loExp)
		{
			loExp.addContextData("ApplicationException occured while editing Custom Provider Details",
					loExp);
			LOG_OBJECT.Error("ApplicationException occured while editing Custom Provider Details : editAutoApprovalDetails ", loExp);
			setMoState("ApplicationException occured while editing Custom Provider Details for agency id = "
					+ aoAutoApprovalConfigBean.getAgencyId() + " and organization Id= "
					+ aoAutoApprovalConfigBean.getOrganizationId());
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while editing Custom Provider Detail :  editAutoApprovalDetails ", loExp);
			loAppEx.addContextData("Exception occured while editing Custom Provider Detail", loExp);
			LOG_OBJECT.Error("ApplicationException occured while editing Custom Provider Detail : editAutoApprovalDetails ", loExp);
			setMoState("ApplicationException occured while editing Custom Provider Detail for agency id = "
					+ aoAutoApprovalConfigBean.getAgencyId() + " and organization Id= "
					+ aoAutoApprovalConfigBean.getOrganizationId());
			throw loAppEx;
		}
		return editStatus;
	}
	
	/**
	 * The method is added in R7 for fetching auto approver user list from city
	 * user details table.
	 * @param aoMyBatisSession
	 * @return loUserList
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<String> fetchAutoApproverUserList(SqlSession aoMyBatisSession) throws ApplicationException
	{
		List<String> loUserList = null;
		try
		{
			loUserList = (List<String>) DAOUtil.masterDAO(aoMyBatisSession, null,
					HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS, HHSR5Constants.FETCH_AUTO_APPROVAL_USER_LIST, null);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured in AgencySettingService: fetchAutoApproverUserList method:: ", loAppEx);
			setMoState("Transaction Failed:: AgencySettingService: fetchAutoApproverUserList method - failed Exception occured"
					+ " while saving threshold value::" + loUserList + "\n");
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "AgencySettingService: saveReviewLevels method:: ", loEx);
			LOG_OBJECT.Error("Exception occured in AgencySettingService: fetchAutoApproverUserList method:: ", loAppEx);
			setMoState("Transaction Failed:: AgencySettingService: fetchAutoApproverUserList method - failed Exception occured"
					+ " while saving threshold value::" + loUserList + "\n");
			throw loAppEx;
		}
		return loUserList;
	}
	
	/**
	 * The method is addedd in R7 for getting properties of budget
	 * @param aoMyBatisSession
	 * @param aoHashMap
	 * @param asBudgetId
	 * @return aoHashMap
	 * @throws ApplicationException
	 */
	public HashMap<String, Object> getRequiredProperties(SqlSession aoMyBatisSession, HashMap<String, Object> aoHashMap, String asBudgetId)
			throws ApplicationException
	{
		ContractBudgetBean loContractBean = null;
		try
		{
			loContractBean = (ContractBudgetBean) DAOUtil.masterDAO(aoMyBatisSession, asBudgetId,
					HHSConstants.MAPPER_CLASS_BUDGET_MAPPER, HHSR5Constants.FETCH_DETAILS_FOR_BUDGET, HHSConstants.JAVA_LANG_STRING);
			if (null != loContractBean)
			{
				aoHashMap.put(HHSConstants.AGENCY_ID_TABLE_COLUMN,
						loContractBean.getAgencyId());
				aoHashMap.put(HHSConstants.CONTRACT_ID1, loContractBean.getContractId());
			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured in AgencySettingService: saveAutoApprovalThreshold method:: ", loAppEx);
			setMoState("Transaction Failed:: AgencySettingService: saveAutoApprovalThreshold method - failed Exception occured"
					+ " while saving threshold value::" + aoHashMap + "\n");
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "AgencySettingService: saveReviewLevels method:: ", loEx);
			LOG_OBJECT.Error("Exception occured in AgencySettingService: saveAutoApprovalThreshold method:: ", loAppEx);
			setMoState("Transaction Failed:: AgencySettingService: saveReviewLevels method - failed Exception occured"
					+ " while saving threshold value::" + aoHashMap + "\n");
			throw loAppEx;
		}
		return aoHashMap;
	}
					
	// R7 end :Modification Auto Approval Enhancement
}
