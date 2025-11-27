package com.nyc.hhs.daomanager.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.AgencyTaskBean;
import com.nyc.hhs.model.CommentsHistoryBean;
import com.nyc.hhs.model.DefaultAssignment;
import com.nyc.hhs.model.FinancialWFBean;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.StaffDetails;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8services.P8ProcessService;
import com.nyc.hhs.service.filenetmanager.p8services.P8ProcessServiceForSolicitationFinancials;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * TaskService: Class used to hit different filenet services such as assign ,
 * finish task and to fetch Workflow details
 * 
 */

@Service
public class TaskService extends ServiceState
{

	/**
	 * This is a log object used to log any exception into log file.
	 */

	private static final LogInfo LOG_OBJECT = new LogInfo(TaskService.class);

	/**
	 * <p>
	 * This method reassign The workflow to user and save comments in audit
	 * table(If any)
	 * <ul>
	 * <li>Call saveCommentsInHistory method to save audit comments</li>
	 * <li>Call filenet reassign service for workflow reassignment</li>
	 * </ul>
	 * <ul>
	 * Method updated in R4
	 * </p>
	 * 
	 * @param aoTaskDetailsBean Bean object contains all task details
	 * @param aoUserSession Filenet UserSession
	 * @return lbStatus reassign status
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public boolean reassignTask(TaskDetailsBean aoTaskDetailsBean, P8UserSession aoUserSession)
			throws ApplicationException
	{
		boolean lbStatus = true;
		HashMap loHmWFProperties = new HashMap();
		String lsTaskId = aoTaskDetailsBean.getTaskId();
		P8ProcessServiceForSolicitationFinancials loSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
		try
		{
			if (null != lsTaskId)
			{
				loHmWFProperties.put(HHSConstants.PROPERTY_PE_TASK_ID, Integer.valueOf(aoTaskDetailsBean.getTaskId())
						+ HHSConstants.INT_ONE);
			}
			else
			{
				loHmWFProperties.put(HHSConstants.PROPERTY_PE_TASK_ID, HHSConstants.INITIAL_TASK_ID
						+ HHSConstants.INT_ONE);
			}
			loSolicitationFinancials.setWFProperty(aoUserSession, aoTaskDetailsBean.getWorkFlowId(), loHmWFProperties);
			loSolicitationFinancials.assign(aoUserSession, aoTaskDetailsBean.getWorkFlowId(),
					aoTaskDetailsBean.getReassignUserId(), aoTaskDetailsBean.getReassignUserName());

		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			setMoState("Error in reassignTask");
			aoAppEx.addContextData(HHSConstants.LEVEL_ERROR_MESSAGE, aoAppEx.toString());
			LOG_OBJECT.Error("Error in reassignTask :", aoAppEx);
			throw aoAppEx;
		}
		return lbStatus;
	}
	
	// Release 6 apt changes start

		/**
		 * This method is used to launch a Certification of funds task with
		 * visibility set to true. This is used by the Contract configure batch
		 * which creates a COF task for the off line APT contracts saved into our
		 * application with status Etl registered(192) via ETL inbound interface.
		 * @param aoUserSession: P8UserSession object
		 * @param aoMybatisSession : SqlSession object 
		 * @param aoHMWFRequiredProps : Hashmap containing values required for launching COF task
		 * @return lsWobNum : String containing Wob number
		 * @throws ApplicationException
		 */
		@SuppressWarnings(
		{ "unchecked", "rawtypes" })
		public String launchCertificationOfFundsWorkflowFromBatch(P8UserSession aoUserSession, SqlSession aoMybatisSession,
				HashMap aoHMWFRequiredProps) throws ApplicationException
		{   
		String lsWobNum = null;
		try
		{   P8ProcessServiceForSolicitationFinancials loSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			String lsWorkflowName = HHSConstants.WF_CONTRACT_CERTIFICATION_FUND;
			String lsEntityId = (String) aoHMWFRequiredProps.get(HHSConstants.ENTITY_ID);
			HashMap loHMWFReqProp = new HashMap();

			// Check if Task is already exist.
			loHMWFReqProp.put(HHSConstants.PROPERTY_PE_ENTITY_ID, lsEntityId);
			loHMWFReqProp.put(HHSConstants.PROPERTY_PE_TASK_TYPE,
					HHSConstants.FINANCIAL_WF_NAME_TYPE_MAP.get(lsWorkflowName));
			lsWobNum = loSolicitationFinancials.fetchWorkflowIdFromView(aoUserSession,
					loHMWFReqProp);
			AgencySettingService loAgencySettingService = new AgencySettingService();
			int liReviewLevel = HHSConstants.INT_ONE;
			liReviewLevel = loAgencySettingService.fetchReviewLevels(
					(String) aoHMWFRequiredProps.get(HHSConstants.PROPERTY_PE_AGENCY_ID),
					HHSConstants.FINANCIAL_WF_ID_MAP.get(lsWorkflowName), aoMybatisSession);

			if (liReviewLevel == HHSConstants.INT_ZERO)
			{
				// Levels are not set for workflow. Throw Error message
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.CB_REVIEW_LEVEL_ERROR));
			}
			aoHMWFRequiredProps.put(HHSConstants.PROPERTY_PE_TASK_TOTAL_LEVEL, liReviewLevel);

			// If the work flow does not exist, then we launch it
			if (lsWobNum == null || lsWobNum.isEmpty())
			{
				aoHMWFRequiredProps.put(HHSConstants.WORKFLOW_NAME, lsWorkflowName);
				aoHMWFRequiredProps.put(HHSConstants.PROPERTY_PE_TASK_VISIBILITY, true);
				lsWobNum = loSolicitationFinancials.launchWorkflow(aoUserSession,
						lsWorkflowName, aoHMWFRequiredProps);
			}
		}
		catch (ApplicationException aoAppExp)
		{
			setMoState("Error in launchCertificationOfFundsWorkflowFromBatch");
			aoAppExp.addContextData(HHSConstants.EXCEPTION_OCCURED_WHILE_UPDATING_UTILITIES, aoHMWFRequiredProps);
			LOG_OBJECT.Error("Error in launchCertificationOfFundsWorkflowFromBatch :", aoAppExp);
			throw aoAppExp;
		}
			return lsWobNum;
		}

		/**
		 * This method is used to launch a Amendment Certification of funds task with
		 * visibility set to true. This is used by the Contract configure batch
		 * which creates a COF task for the off line APT contract amendments saved into our
		 * application with status Etl registered(192) via ETL inbound interface.
		 * @param aoUserSession : P8UserSession object
		 * @param aoMybatisSession : SqlSession object 
		 * @param aoHMWFRequiredProps : Hashmap containing values required for launching COF task
		 * @return lsWobNum : String containing Wob number
		 * @throws ApplicationException
		 */
		@SuppressWarnings(
		{ "unchecked", "rawtypes" })
		public String launchAmendmentCOFWorkflowFromBatch(P8UserSession aoUserSession, SqlSession aoMybatisSession,
				HashMap aoHMWFRequiredProps) throws ApplicationException
		{
		String lsWobNum = null;
		try
		{
			P8ProcessServiceForSolicitationFinancials loSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			String lsWorkflowName = HHSConstants.WF_AMENDMENT_CERTIFICATION_FUND;
			String lsEntityId = (String) aoHMWFRequiredProps.get(HHSConstants.ENTITY_ID);

			HashMap loHMWFReqProp = new HashMap();
			// Check if Task already exists .If Yes make task visibility true

			loHMWFReqProp.put(HHSConstants.PROPERTY_PE_ENTITY_ID, lsEntityId);
			loHMWFReqProp.put(HHSConstants.PROPERTY_PE_TASK_TYPE,
					HHSConstants.FINANCIAL_WF_NAME_TYPE_MAP.get(lsWorkflowName));
			lsWobNum = loSolicitationFinancials.fetchWorkflowIdFromView(aoUserSession,
					loHMWFReqProp);
			AgencySettingService loAgencySettingService = new AgencySettingService();
			int liReviewLevel = HHSConstants.INT_ONE;
			liReviewLevel = loAgencySettingService.fetchReviewLevels(
					(String) aoHMWFRequiredProps.get(HHSConstants.PROPERTY_PE_AGENCY_ID),
					HHSConstants.FINANCIAL_WF_ID_MAP.get(lsWorkflowName), aoMybatisSession);

			if (liReviewLevel == HHSConstants.INT_ZERO)
			{
				// Levels are not set for workflow. Throw Error message
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.CB_REVIEW_LEVEL_ERROR));
			}
			aoHMWFRequiredProps.put(HHSConstants.PROPERTY_PE_TASK_TOTAL_LEVEL, liReviewLevel);

			// If the work flow does not exist, then we launch it
			if (lsWobNum == null || lsWobNum.isEmpty())
			{
				aoHMWFRequiredProps.put(HHSConstants.WORKFLOW_NAME, lsWorkflowName);
				aoHMWFRequiredProps.put(HHSConstants.PROPERTY_PE_TASK_VISIBILITY, true);
				lsWobNum = loSolicitationFinancials.launchWorkflow(aoUserSession,
						lsWorkflowName, aoHMWFRequiredProps);
			}
		}
		catch (ApplicationException aoAppExp)
		{
			setMoState("Error in launchAmendmentCOFWorkflowFromBatch");
			aoAppExp.addContextData(HHSConstants.EXCEPTION_OCCURED_WHILE_UPDATING_UTILITIES, aoHMWFRequiredProps);
			LOG_OBJECT.Error("Error in launchAmendmentCOFWorkflowFromBatch :", aoAppExp);
			throw aoAppExp;
		}
			return lsWobNum;
		}

		// Release 6 changes end
	/**
	 * This method launch all the financial workflows by calling filenet
	 * services and return workflow Id
	 * <ul>
	 * <li>Method updated in R4</li>
	 * </ul>
	 * @param aoUserSession : contains the Filenet userSession
	 * @param aoMybatisSession : passes the MyBatis SQL Session
	 * @param aoHMWFRequiredProps : map containing the required info
	 * @param aoAuthFlag : flag to continue transaction
	 * @return loHhsAuditBean : returns the updated HHSAuditBean
	 * @throws ApplicationException when any error condition occurred
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public HhsAuditBean launchFinancialWorkflow(P8UserSession aoUserSession, SqlSession aoMybatisSession,
			HashMap aoHMWFRequiredProps, Boolean aoAuthFlag) throws ApplicationException, InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		String lsWorkflowName = (String) aoHMWFRequiredProps.get(HHSConstants.PROPERTY_PE_TASK_TYPE);
		String lsEntityId = (String) aoHMWFRequiredProps.get(HHSConstants.ENTITY_ID);
		Boolean loProviderInitiated = (Boolean) aoHMWFRequiredProps.get(HHSConstants.AUDIT_PROVIDER_INITIATED_TASK);
		Boolean loLaunchCOF = (Boolean) aoHMWFRequiredProps.get(HHSConstants.PROPERTY_PE_LAUNCH_COF);
		String lsAuditType = HHSConstants.AGENCY_AUDIT;
		boolean lbError = false;
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		String lsWobNum = null;
		String lsLaunchByOrgType = HHSConstants.CITY;
		HashMap loHMWFReqProp = new HashMap();
		try
		{
			if (null != loProviderInitiated && loProviderInitiated)
			{
				lsAuditType = HHSConstants.PROVIDER_AUDIT;
				lsLaunchByOrgType = HHSConstants.PROVIDER;
			}
			if (null == loLaunchCOF)
			{
				loLaunchCOF = true;
				aoHMWFRequiredProps.put(HHSConstants.PROPERTY_PE_LAUNCH_COF, true);
			}
			AgencySettingService loAgencySettingService = new AgencySettingService();
			int liReviewLevel = HHSConstants.INT_ONE;
			if (aoAuthFlag && (!aoHMWFRequiredProps.isEmpty()))
			{
				aoHMWFRequiredProps.put(HHSConstants.PROPERTY_PE_LAUNCH_ORG_TYPE, lsLaunchByOrgType);
				if (!lsWorkflowName.equalsIgnoreCase(HHSConstants.WF_CONTRACT_CONFIGURATION_UPDATE)
						&& !lsWorkflowName.equalsIgnoreCase(HHSConstants.WF_NEW_FY_CONFIGURATION) && loLaunchCOF)
				{
					liReviewLevel = loAgencySettingService.fetchReviewLevels(
							(String) aoHMWFRequiredProps.get(HHSConstants.PROPERTY_PE_AGENCY_ID),
							HHSConstants.FINANCIAL_WF_ID_MAP.get(lsWorkflowName), aoMybatisSession);
				}
				if (liReviewLevel == HHSConstants.INT_ZERO)
				{
					// Levels are not set for workflow. Throw Error message
					lbError = true;
					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.CB_REVIEW_LEVEL_ERROR));
				}

				// Release 5 changes
				String lsContractId = null;
				String lsQueryId = HHSR5Constants.GET_QUERY_ID_DEFAULT_ASSIGNEE_FROM_WORKFLOW_NAME.get(lsWorkflowName);
				Map<String, Integer> loMap = new HashMap<String, Integer>();
				DefaultAssignment loDefaultAssignment = null;
				if (null != lsQueryId)
				{
					lsContractId = (String) DAOUtil.masterDAO(aoMybatisSession, lsEntityId,
							HHSR5Constants.MAPPER_CLASS_USERROLE_MAPPER, lsQueryId, HHSConstants.JAVA_LANG_STRING);
					loMap.put("contractId", Integer.parseInt(lsContractId));
					loMap.put("taskReviewProcessId",
							HHSR5Constants.GET_QUERY_ID_DEFAULT_ASSIGNEE_FROM_REVIEW_PROCESS_ID.get(lsWorkflowName));
					if (lsContractId != null)
					{
						loDefaultAssignment = (DefaultAssignment) DAOUtil.masterDAO(aoMybatisSession, loMap,
								HHSR5Constants.MAPPER_CLASS_USERROLE_MAPPER, "getAssigneeReturnForRevision",
								HHSR5Constants.JAVA_UTIL_HASH_MAP);
					}
				}

				aoHMWFRequiredProps.put(HHSConstants.PROPERTY_PE_TASK_TOTAL_LEVEL, liReviewLevel);
				aoHMWFRequiredProps.put(HHSConstants.PROPERTY_PE_TASK_ID, HHSConstants.INITIAL_TASK_ID);
				// Check if Task is already exist .if Yes make task visibility
				// true
				loHMWFReqProp.put(HHSConstants.PROPERTY_PE_ENTITY_ID, lsEntityId);
				loHMWFReqProp.put(HHSConstants.PROPERTY_PE_TASK_TYPE,
						HHSConstants.FINANCIAL_WF_NAME_TYPE_MAP.get(lsWorkflowName));
				lsWobNum = new P8ProcessServiceForSolicitationFinancials().fetchWorkflowIdFromView(aoUserSession,
						loHMWFReqProp);
				loHhsAuditBean = launckWorkflowAndGetAuditBean(aoUserSession, aoMybatisSession, aoHMWFRequiredProps,
						lsWorkflowName, lsEntityId, lsAuditType, loHhsAuditBean, lsWobNum, loHMWFReqProp,
						loDefaultAssignment);
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppExp)
		{
			if (lbError)
			{
				aoAppExp.addContextData(HHSConstants.LEVEL_ERROR_MESSAGE, aoAppExp.toString());
			}
			setMoState("Error in launchFinancialWorkflow");
			aoAppExp.addContextData(HHSConstants.EXCEPTION_OCCURED_WHILE_UPDATING_UTILITIES, aoHMWFRequiredProps);
			LOG_OBJECT.Error("Error in launchFinancialWorkflow :", aoAppExp);
			throw aoAppExp;
		}

		return loHhsAuditBean;
	}

	/**
	 * This method is used to launch workflow or set workflow properties if wob
	 * number exists and get audit bean based on wob number and other parameters
	 * to insert task audit details
	 * 
	 * <ul>
	 * <li>Method added in R4</li>
	 * </ul>
	 * @param aoUserSession P8UserSession object
	 * @param aoMybatisSession SQL mybatis session
	 * @param aoHMWFRequiredProps workflow properties hashmap
	 * @param asWorkflowName a string value of workflow name
	 * @param asEntityId a string value of entity Id
	 * @param asAuditType a string value of audit type
	 * @param aoHhsAuditBean HhsAuditBean object
	 * @param asWobNum a string value of wob number
	 * @param aoHMWFReqProp required properties hashmap
	 * @return HhsAuditBean object
	 * @throws ApplicationException If an Application Exception occurs
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	@SuppressWarnings(
	{ "rawtypes" })
	private HhsAuditBean launckWorkflowAndGetAuditBean(P8UserSession aoUserSession, SqlSession aoMybatisSession,
			HashMap aoHMWFRequiredProps, String asWorkflowName, String asEntityId, String asAuditType,
			HhsAuditBean aoHhsAuditBean, String asWobNum, HashMap aoHMWFReqProp, DefaultAssignment aoDefaultAssignment)
			throws ApplicationException, InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		LOG_OBJECT.Debug("Entering into launckWorkflowAndGetAuditBean with parameters::: "+ aoHMWFRequiredProps+ asWorkflowName+asEntityId);
		LOG_OBJECT.Info("Entering into launckWorkflowAndGetAuditBean with parameters::: "+ aoHMWFRequiredProps+ asWorkflowName+asEntityId);
		setMoState("Entering into launckWorkflowAndGetAuditBean with parameters::: "+ aoHMWFRequiredProps+ asWorkflowName+asEntityId);
		// added for 7839
		Boolean lbUpdateFlag = true;
		String lsWobNum = new String();
		HashMap<String, String> loHmWFComponentForBudgetModification = new HashMap<String, String>();
		if (null != asWorkflowName && asWorkflowName.equalsIgnoreCase(HHSConstants.WF_PROCUREMENT_CERTIFICATION_FUND))
		{
			lbUpdateFlag = false;
		}
		//R7 Start::: Updated below block for return for revision
		if (null != asWobNum && !asWobNum.isEmpty() && lbUpdateFlag)
		{
			LOG_OBJECT.Debug("1. Return for Revision case ");
			LOG_OBJECT.Info("2. Return for Revision case ");
			setMoState("3. Return for Revision case ");
			loHmWFComponentForBudgetModification.put(HHSConstants.COMPONENT_ACTION,HHSR5Constants.COMPONENT_FOR_RETURN_FOR_REVISION);
			loHmWFComponentForBudgetModification.put(HHSR5Constants.WOB_NUMBER_MOD,asWobNum);
			loHmWFComponentForBudgetModification.put(HHSConstants.PROPERTY_PE_BUDGET_ID,asEntityId );
			loHmWFComponentForBudgetModification.put(HHSR5Constants.WorkFLOW_NAME,asWorkflowName );
			lsWobNum = new P8ProcessServiceForSolicitationFinancials().launchWorkflow(aoUserSession,
					HHSConstants.WF_FINANCIAL_UTILITY, loHmWFComponentForBudgetModification);
			LOG_OBJECT.Debug("WorkFlow number "+lsWobNum);
			LOG_OBJECT.Info("WorkFlow number "+lsWobNum);
			setMoState("WorkFlow number "+lsWobNum);
			// Make multiple audit entry on re-submit
			multipleAuditEntryOnReSubmitTask(aoMybatisSession, asWorkflowName, asEntityId, asAuditType,
					(String) aoHMWFRequiredProps.get(HHSConstants.PROPERTY_PE_SUBMITTED_BY),aoDefaultAssignment);
			aoHhsAuditBean.setWorkflowId(asWobNum);
			aoHhsAuditBean.setEntityId(asEntityId);
			aoHhsAuditBean.setUserId((String) aoHMWFRequiredProps.get(HHSConstants.PROPERTY_PE_SUBMITTED_BY));
			aoHhsAuditBean.setContractId((String) aoHMWFRequiredProps.get(HHSConstants.CONTRACT_ID_WORKFLOW));
			aoHhsAuditBean.setEntityType(HHSConstants.FINANCIAL_WF_NAME_TYPE_MAP.get(asWorkflowName));
			aoHhsAuditBean.setTaskEvent(HHSConstants.TASK_RELAUNCHED);
		}
		//R7 End
		// Call Filenet Service for Launch Workflow
		else
		{
			asWobNum = new P8ProcessServiceForSolicitationFinancials().launchWorkflow(aoUserSession, asWorkflowName,
					aoHMWFRequiredProps);
			// Set Audit data in channel for Workflow Launch event
			aoHhsAuditBean = HHSUtil.addAuditDataToChannel(HHSConstants.PROPERTY_TASK_CREATION_EVENT,
					HHSConstants.PROPERTY_TASK_CREATION_EVENT, HHSConstants.PROPERTY_TASK_CREATION_DATA,
					HHSConstants.FINANCIAL_WF_NAME_TYPE_MAP.get(asWorkflowName), asEntityId,
					(String) aoHMWFRequiredProps.get(HHSConstants.PROPERTY_PE_SUBMITTED_BY), asAuditType);
			aoHhsAuditBean.setWorkflowId(asWobNum);
			aoHhsAuditBean.setContractId((String) aoHMWFRequiredProps.get(HHSConstants.CONTRACT_ID_WORKFLOW));
		}
		return aoHhsAuditBean;
	}

	/**
	 * This method make multiple audit entry when provider re-submit any budget
	 * or invoice tasks.
	 * @param aoMybatisSession SQL session
	 * @param asWorkflowName workflow name
	 * @param asEntityId Entity Id
	 * @param asAuditType Audit Type
	 * @param asUserId user Id
	 * @throws ApplicationException
	 */
	private void multipleAuditEntryOnReSubmitTask(SqlSession aoMybatisSession, String asWorkflowName,
			String asEntityId, String asAuditType, String asUserId,DefaultAssignment aoDefaultAssignment) throws ApplicationException
	{
		List<HhsAuditBean> loAuditList = new ArrayList<HhsAuditBean>();
		HhsAuditService loHhsAuditService = new HhsAuditService();
		StringBuilder loStatusChange = new StringBuilder();
		loStatusChange.append(HHSConstants.STATUS_CHANGED_FROM);
		loStatusChange.append(HHSConstants.SPACE + HHSConstants.STR + HHSConstants.TASK_RFR + HHSConstants.STR);
		loStatusChange.append(HHSConstants._TO_);
		loStatusChange.append(HHSConstants.STR + HHSConstants.TASK_IN_REVIEW + HHSConstants.STR);

		loAuditList.add(HHSUtil.addAuditDataToChannel(HHSConstants.STATUS_CHANGE, HHSConstants.STATUS_CHANGE,
				loStatusChange.toString(), HHSConstants.FINANCIAL_WF_NAME_TYPE_MAP.get(asWorkflowName), asEntityId,
				asUserId, asAuditType));
		loAuditList.add(HHSUtil.addAuditDataToChannel(HHSConstants.TASK_ASSIGNMENT, HHSConstants.TASK_ASSIGNMENT,
				HHSConstants.PROPERTY_TASK_CREATION_DATA, HHSConstants.FINANCIAL_WF_NAME_TYPE_MAP.get(asWorkflowName),
				asEntityId, asUserId, asAuditType));
		//R5 Start: defect 8312
		if (aoDefaultAssignment != null && aoDefaultAssignment.getDefaultAssignmentId() != null 
				&& HHSConstants.FINANCIAL_WF_NAME_TYPE_MAP_DEFAULT_ASSIGNMENT.contains(asWorkflowName))
		{
					loAuditList.add(HHSUtil.addAuditDataToChannel(HHSConstants.TASK_ASSIGNMENT,
							HHSConstants.TASK_ASSIGNMENT, HHSConstants.TASK_ASSIGNED_TO + HHSR5Constants.COLON_AUDIT
									+ aoDefaultAssignment.getOrgName(),
							HHSConstants.FINANCIAL_WF_NAME_TYPE_MAP.get(asWorkflowName), asEntityId,
							HHSConstants.SYSTEM_USER, asAuditType));
		}
		//R5 End: defect 8312
		loHhsAuditService.hhsMultiAuditInsert(aoMybatisSession, loAuditList, true);
	}

	/**
	 * This method terminate the workflow on the basis of Workflow Id
	 * @param aoUserSession Filenet session
	 * @param asWorkflowId Workflow Id
	 * @return lbStatus return true for success
	 * @throws ApplicationException when any error condition occurred
	 */
	private boolean terminateWorkflow(P8UserSession aoUserSession, String asWorkflowId) throws ApplicationException
	{
		boolean lbStatus = true;
		try
		{
			if (null != asWorkflowId)
			{
				new P8ProcessService().terminateWorkItem(aoUserSession, asWorkflowId);
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error closeAllOpenTask");
			LOG_OBJECT.Error("Error closeAllOpenTask :", loExp);
			throw loExp;
		}
		return lbStatus;
	}

	/**
	 * This method terminate workflow by passing workflow Id to filenet service
	 * @param aoUserSession filenet session
	 * @param asWorkflowId Workflow Id
	 * @param aoFinalFinish final finish
	 * @return lbStatus return true if success
	 * @throws ApplicationException when any error condition occurred
	 */
	public boolean terminateWorkflow(P8UserSession aoUserSession, String asWorkflowId, Boolean aoFinalFinish)
			throws ApplicationException
	{
		boolean lbStatus = true;
		try
		{
			if (null != asWorkflowId && aoFinalFinish)
			{
				new P8ProcessService().terminateWorkItem(aoUserSession, asWorkflowId);
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error closeAllOpenTask");
			LOG_OBJECT.Error("Error closeAllOpenTask :", loExp);
			throw loExp;
		}

		return lbStatus;
	}

	/**
	 * This method get all workflow Id from Filenet view on the basis of
	 * required properties and terminate all workflows by calling method
	 * 'closeAllOpenTask'
	 * @param aoUserSession Filenet session
	 * @param aoHmWFProperties WF propoerties
	 * @return lbStatus return true if success
	 * @throws ApplicationException when any error condition occurred
	 */
	@SuppressWarnings("rawtypes")
	public boolean closeAllOpenTask(P8UserSession aoUserSession, HashMap aoHmWFProperties) throws ApplicationException
	{
		List<String> loWorkflowIdList = null;
		boolean lbStatus = true;

        LOG_OBJECT.Debug("[Tracce 2.1]Entered into deleteContract::lbStatus=" + lbStatus);
		try
		{
			loWorkflowIdList = new P8ProcessServiceForSolicitationFinancials().fetchALLWorkflowIdFromView(
					aoUserSession, aoHmWFProperties);
	        LOG_OBJECT.Debug("[Tracce 2.1]Entered into deleteContract::lbStatus=" + lbStatus);
			if (null != loWorkflowIdList)
			{
				for (String lsWorkflowId : loWorkflowIdList)
				{
					terminateWorkflow(aoUserSession, lsWorkflowId);
				}
			}
			
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error closeAllOpenTask");
			loExp.setContextData(aoHmWFProperties);
			LOG_OBJECT.Error("Error closeAllOpenTask :", loExp);
			throw loExp;
		}
        LOG_OBJECT.Debug("[Tracce 2.1]Entered into deleteContract::lbStatus=" + lbStatus);

		return lbStatus;
	}

	/**
	 * This method get all workflow Id from Filenet view on the basis of
	 * required properties and set all workflows status to close by calling
	 * method 'setWFProperty'
	 * <ul>
	 * <li>Method added in R4</li>
	 * </ul>
	 * @param aoUserSession Filenet session
	 * @param aoHmWFProperties WF propoerties
	 * @return lbStatus return true if success
	 * @throws ApplicationException when any error condition occurred
	 */
	@SuppressWarnings(
	{ "rawtypes" })
	public boolean taskStatusForClosedProcurment(P8UserSession aoUserSession, HashMap aoHmWFProperties)
			throws ApplicationException
	{
		List<String> loWorkflowIdList = null;
		boolean lbStatus = false;
		P8ProcessServiceForSolicitationFinancials loSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
		try
		{
			loWorkflowIdList = loSolicitationFinancials.fetchALLWorkflowIdFromView(aoUserSession, aoHmWFProperties);
			if (null != loWorkflowIdList && !loWorkflowIdList.isEmpty())
			{
				lbStatus = true;
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error taskStatusForClosedProcurment");
			loExp.setContextData(aoHmWFProperties);
			LOG_OBJECT.Error("Error taskStatusForClosedProcurment :", loExp);
			throw loExp;
		}
		return lbStatus;
	}

	/**
	 * This method close all task related to original contract. aoMyBatisSession
	 * SqlSession object. Query Id 'fetchUpdateContractId'
	 * @param aoUserSession filenet session
	 * @param aoHmWFProperties Required properties in hash map object
	 * @param aoAuthFlag Authentication flag
	 * @return boolean value
	 * @throws ApplicationException
	 * 
	 * 
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public boolean closeOriginalContractTask(SqlSession aoMyBatisSession, P8UserSession aoUserSession,
			HashMap aoHmWFProperties, Boolean aoAuthFlag) throws ApplicationException
	{
		boolean lbStatus = true;
		try
		{
			if (aoAuthFlag)
			{
				aoHmWFProperties.put(HHSConstants.PROPERTY_PE_TASK_TYPE, HHSConstants.TASK_BUDGET_MODIFICATION);
				closeAllOpenTask(aoUserSession, aoHmWFProperties);
				aoHmWFProperties.put(HHSConstants.PROPERTY_PE_TASK_TYPE, HHSConstants.TASK_CONTRACT_UPDATE);
				closeAllOpenTask(aoUserSession, aoHmWFProperties);
				aoHmWFProperties.put(HHSConstants.PROPERTY_PE_TASK_TYPE, HHSConstants.TASK_NEW_FY_CONFIGURATION);
				closeAllOpenTask(aoUserSession, aoHmWFProperties);
				Map loContractInfo = null;
				loContractInfo = (HashMap) DAOUtil.masterDAO(aoMyBatisSession,
						String.valueOf(aoHmWFProperties.get(HHSConstants.PROPERTY_PE_CONTRACT_ID)),
						HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.BMC_FETCH_UPDATE_CONTRACT_ID,
						HHSConstants.JAVA_LANG_STRING);
				if (loContractInfo != null && !loContractInfo.isEmpty()
						&& loContractInfo.get(HHSConstants.CONTRACT_ID_UNDERSCORE) != null)
				{
					aoHmWFProperties.put(HHSConstants.PROPERTY_PE_TASK_TYPE, HHSConstants.TASK_BUDGET_UPDATE);
					aoHmWFProperties.put(HHSConstants.PROPERTY_PE_CONTRACT_ID,
							String.valueOf(loContractInfo.get(HHSConstants.CONTRACT_ID_UNDERSCORE)));
					closeAllOpenTask(aoUserSession, aoHmWFProperties);
				}
			}

		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error closeOriginalContractTask");
			loExp.setContextData(aoHmWFProperties);
			LOG_OBJECT.Error("Error closeOriginalContractTask :", loExp);
			throw loExp;
		}
		return lbStatus;
	}

	/**
	 * This method deleted not need contract and budget..
	 * 
	 * Query Id 'deleteModificationBudget' Query Id 'deleteUpdateBudget' Query
	 * Id 'deleteUddateContract'
	 * @param aoMyBatisSession SqlSession
	 * @param aoUserSession filenet session
	 * @param aoHmWFProperties Required properties in hash map object
	 * @param aoUserHashMap UserId
	 * @param aoAuthFlag Authentication flag
	 * @return boolean value
	 * @throws ApplicationException
	 * 
	 * 
	 */
	public boolean deleteNotNeededBudgetAndContract(SqlSession aoMyBatisSession, Map aoUserHashMap, Boolean aoAuthFlag)
			throws ApplicationException
	{
		boolean lbStatus = true;
		try
		{
			if (aoAuthFlag)
			{
				DAOUtil.masterDAO(aoMyBatisSession, aoUserHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
						HHSConstants.DELETE_MODIFICATION_BUDGET, HHSConstants.JAVA_UTIL_MAP);
				DAOUtil.masterDAO(aoMyBatisSession, aoUserHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
						HHSConstants.DELETE_UPDATE_BUDGET, HHSConstants.JAVA_UTIL_MAP);
				DAOUtil.masterDAO(aoMyBatisSession, aoUserHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
						HHSConstants.DELETE_UDDATE_CONTRACT, HHSConstants.JAVA_UTIL_MAP);
				setMoState("Transaction Success:: TaskService:deleteNotNeededBudgetAndContract"
						+ " method - success to update record " + " \n");
			}

		}
		// catch any application exception thrown from the code due to UPDATE
		// statement and throw it
		// forward
		catch (ApplicationException loAppEx)
		{
			setMoState("Transaction Failed:: TaskService:deleteNotNeededBudgetAndContract"
					+ " method - failed to update record " + " \n");
			loAppEx.addContextData("Map passed: ", aoUserHashMap);
			LOG_OBJECT.Error("ApplicationException occured while executing query deleteNotNeededBudgetAndContract ",
					loAppEx);
			throw loAppEx;
		}
		// catch any null exception thrown from the code due to UPDATE
		// statement and throw it
		// forward
		catch (Exception loEx)
		{
			setMoState("Transaction Failed:: TaskService:deleteNotNeededBudgetAndContract method"
					+ " - failed to update record " + " \n");
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while executing query in deleteNotNeededBudgetAndContract", loEx);
			LOG_OBJECT.Error("Exception occured while executing query in deleteNotNeededBudgetAndContract ", loEx);
			throw loAppEx;
		}
		return lbStatus;
	}

	/**
	 * This method deleted not need base contract and budget.. Query Id
	 * 'deleteBaseBudget' Query Id 'deleteBaseContract'
	 * @param aoMyBatisSession SqlSession
	 * @param aoUserHashMap UserId
	 * @param aoAuthFlag Authentication flag
	 * @return boolean value
	 * @throws ApplicationException
	 * 
	 * 
	 * 
	 */
	public boolean deleteBaseBudgetAndContract(SqlSession aoMyBatisSession, Map aoUserHashMap, Boolean aoAuthFlag)
			throws ApplicationException
	{
		boolean lbStatus = true;
		try
		{
			if (aoAuthFlag)
			{
				DAOUtil.masterDAO(aoMyBatisSession, aoUserHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
						HHSConstants.DELETE_BASE_BUDGET, HHSConstants.JAVA_UTIL_MAP);
				DAOUtil.masterDAO(aoMyBatisSession, aoUserHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
						HHSConstants.DELETE_BASE_CONTRACT, HHSConstants.JAVA_UTIL_MAP);
				setMoState("Transaction Success:: TaskService:deleteBaseBudgetAndContract"
						+ " method - success to update record " + " \n");
			}

		}
		// catch any application exception thrown from the code due to UPDATE
		// statement and throw it
		// forward
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT
					.Error("ApplicationException occured while executing query deleteBaseBudgetAndContract ", loAppEx);
			setMoState("Transaction Failed:: TaskService:deleteBaseBudgetAndContract"
					+ " method - failed to update record " + " \n");
			loAppEx.addContextData("Map passed: ", aoUserHashMap);
			throw loAppEx;
		}
		// catch any null exception thrown from the code due to UPDATE
		// statement and throw it
		// forward
		catch (Exception loEx)
		{
			setMoState("Transaction Failed:: TaskService:deleteBaseBudgetAndContract method"
					+ " - failed to update record " + " \n");
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while executing query in deleteBaseBudgetAndContract", loEx);
			LOG_OBJECT.Error("Exception occured while executing query in deleteBaseBudgetAndContract ", loEx);
			throw loAppEx;
		}
		return lbStatus;
	}

	/**
	 * This method set workflow property value in workflow Task on the basis of
	 * Workflow Id
	 * 
	 * @param aoUserSession Filenet session
	 * @param asWobNo Workflow Id
	 * @param aoHmWFProperties HashMap object
	 * @param aoFlag Flag object
	 * @return lbStatus return true if success
	 * @throws ApplicationException when any error condition occurred
	 */
	public boolean setPropertyInWF(P8UserSession aoUserSession, String asWobNo,
			@SuppressWarnings("rawtypes") HashMap aoHmWFProperties, Boolean aoFlag) throws ApplicationException
	{
		boolean lbStatus = true;
		try
		{
			if (aoFlag && null != asWobNo)
			{
				new P8ProcessServiceForSolicitationFinancials().setWFProperty(aoUserSession, asWobNo, aoHmWFProperties);
				return true;
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error setPropertyInWF");
			loExp.setContextData(aoHmWFProperties);
			LOG_OBJECT.Error("Error setPropertyInWF :", loExp);
			throw loExp;
		}
		return lbStatus;
	}

	/**
	 * This method finish the task in FileNet and save comments in audit
	 * table(If any)
	 * <ul>
	 * <li>Call saveCommentsInHistory method to save audit comments</li>
	 * <li>Call filenet service to finish workflow</li>
	 * </ul>
	 * 
	 * @param aoTaskDetailsBean TaskDetailsBean
	 * @param aoHmFinishWFProperties Finish WFP properties
	 * @param aoUserSession P8UserSession
	 * @return lbStatus
	 * @throws ApplicationException when any error condition occurred
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public boolean finishTask(TaskDetailsBean aoTaskDetailsBean, P8UserSession aoUserSession,
			HashMap aoHmFinishWFProperties) throws ApplicationException
	{
		boolean lbStatus = true;
		HashMap loHmWFProperties = new HashMap();
		String lsTaskId = aoTaskDetailsBean.getTaskId();
		LOG_OBJECT.Debug("Entered finishTask");
		try
		{
			if (null != lsTaskId && !lsTaskId.isEmpty())
			{
				loHmWFProperties.put(HHSConstants.PROPERTY_PE_TASK_ID, Integer.valueOf(aoTaskDetailsBean.getTaskId())
						+ HHSConstants.INT_ONE);
			}
			else
			{
				loHmWFProperties.put(HHSConstants.PROPERTY_PE_TASK_ID, HHSConstants.INITIAL_TASK_ID
						+ HHSConstants.INT_TWO);
			}
			new P8ProcessServiceForSolicitationFinancials().setWFProperty(aoUserSession,
					aoTaskDetailsBean.getWorkFlowId(), loHmWFProperties);
			new P8ProcessServiceForSolicitationFinancials().finishTask(aoUserSession,
					aoTaskDetailsBean.getWorkFlowId(), aoTaskDetailsBean.getTaskStatus(), aoHmFinishWFProperties);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			setMoState("Error finishTask");
			aoAppEx.setContextData(aoHmFinishWFProperties);
			LOG_OBJECT.Error("Error finishTask :", aoAppEx);
			throw aoAppEx;
		}
		LOG_OBJECT.Debug("Exited finishTask");

		return lbStatus;
	}

	/**
	 * This method fetches the task details from FileNet by calling Filenet
	 * process engine services.
	 * <ul>
	 * <li>Call setPropertiesForTask method to set parameters need to fetch from
	 * filenet</li>
	 * <li>Call filenet service to fetch workflow details</li>
	 * <li>Call setTaskDetailsInBean method to set all returned values from
	 * filenet in Bean</li>
	 * </ul>
	 * 
	 * @param aoTaskDetailsBean Bean object contains all task details
	 * @param aoUserSession Filenet user session
	 * @return aoTaskDetailsBean task details bean
	 * @throws ApplicationException when any error condition occurred
	 */
	@SuppressWarnings("rawtypes")
	public TaskDetailsBean fetchTaskDetails(TaskDetailsBean aoTaskDetailsBean, P8UserSession aoUserSession)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered fetchTaskDetails");
		HashMap loHmRequiredProps = setPropertiesForTask();
		try
		{
			HashMap loWorkItemDetails = new P8ProcessService().getWorkItemDetails(aoUserSession,
					aoTaskDetailsBean.getWorkFlowId(), loHmRequiredProps);
			if (!loWorkItemDetails.isEmpty())
			{
				setTaskDetailsInBean((HashMap) loWorkItemDetails.get(aoTaskDetailsBean.getWorkFlowId()),
						aoTaskDetailsBean);
			}
			else
			{
				throw new ApplicationException("This Task is no longer Exist.");
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			setMoState("Error fetchTaskDetails");
			aoAppEx.setContextData(loHmRequiredProps);
			LOG_OBJECT.Error("Error fetchTaskDetails :", aoAppEx);
			throw aoAppEx;
		}
		LOG_OBJECT.Debug("Exited fetchTaskDetails");
		return aoTaskDetailsBean;
	}

	/**
	 * This method set all required properties need to fetch from filenet in
	 * HashMap object
	 * 
	 * @return Hash Map
	 * 
	 *         This method was updated in R4.
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private HashMap setPropertiesForTask()
	{
		HashMap loHmRequiredProps = new HashMap();
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_PROCUREMENT_TITLE, HHSConstants.EMPTY_STRING);
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_PROCUREMENT_EPIN, HHSConstants.EMPTY_STRING);
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_PROVIDER_ID, HHSConstants.EMPTY_STRING);
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_PROVIDER_NAME, HHSConstants.EMPTY_STRING);
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_AWARD_EPIN, HHSConstants.EMPTY_STRING);
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_CT, HHSConstants.EMPTY_STRING);
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_TASK_NAME, HHSConstants.EMPTY_STRING);
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_SUBMITTED_BY, HHSConstants.EMPTY_STRING);
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_SUBMITTED_DATE, HHSConstants.EMPTY_STRING);
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_ASSIGNED_TO, HHSConstants.EMPTY_STRING);
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_ASSIGNED_TO_NAME, HHSConstants.EMPTY_STRING);
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_ASSIGNED_DATE, HHSConstants.EMPTY_STRING);
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_TASK_ID, HHSConstants.EMPTY_STRING);
		loHmRequiredProps.put(HHSConstants.CURR_LEVEL, HHSConstants.EMPTY_STRING);
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_TASK_TOTAL_LEVEL, HHSConstants.EMPTY_STRING);
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_AGENCY_ID, HHSConstants.EMPTY_STRING);
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_PROCURMENT_ID, HHSConstants.EMPTY_STRING);
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_CONTRACT_ID, HHSConstants.EMPTY_STRING);
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_TASK_TYPE, HHSConstants.EMPTY_STRING);
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_BUDGET_ID, HHSConstants.EMPTY_STRING);
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_INVOICE_ID, HHSConstants.EMPTY_STRING);
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_NEW_FISCAL_YEAR_ID, HHSConstants.EMPTY_STRING);
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_CONTRACT_CONF_WOB, HHSConstants.EMPTY_STRING);
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_LAUNCH_COF, HHSConstants.EMPTY_STRING);
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_LAUNCH_ORG_TYPE, HHSConstants.EMPTY_STRING);
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_LINKED_WOBNO, HHSConstants.EMPTY_STRING);
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_ADVANCE_NUMBER, HHSConstants.EMPTY_STRING);
		loHmRequiredProps.put(HHSConstants.TASK_STATUS, HHSConstants.EMPTY_STRING);
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_TASK_SOURCE, HHSConstants.EMPTY_STRING);
		loHmRequiredProps.put(P8Constants.PE_WORKFLOW_COMPETITION_POOL_TITLE, HHSConstants.EMPTY_STRING);
		loHmRequiredProps.put("IsNewLaunch", HHSConstants.EMPTY_STRING);
		//Added for R6: return payment review task
		loHmRequiredProps.put(HHSConstants.RETURN_PE_PAYMENT_DETAIL_ID, HHSConstants.EMPTY_STRING);
		//Added for R6: return payment review task
		return loHmRequiredProps;
	}

	/**
	 * This method set TaskDetailsBean object from aoWorkItemDetails HashMap
	 * object
	 * 
	 * @param aoWorkItemDetails WorkItemDetails HashMap object
	 * @param aoTaskDetailsBean Bean object contains all task details
	 * @throws ApplicationException when any error condition occurred
	 * 
	 *             This method was updated in R4.
	 */
	private void setTaskDetailsInBean(@SuppressWarnings("rawtypes") HashMap aoWorkItemDetails,
			TaskDetailsBean aoTaskDetailsBean) throws ApplicationException
	{
		Integer loCurrentLevel = 0;
		String lsTaskName = null;
		String lsAssignedToName = null;
		lsAssignedToName = (String) aoWorkItemDetails.get(HHSConstants.PROPERTY_PE_ASSIGNED_TO_NAME);
		loCurrentLevel = (Integer) aoWorkItemDetails.get(HHSConstants.CURR_LEVEL);
		lsTaskName = (String) aoWorkItemDetails.get(HHSConstants.PROPERTY_PE_TASK_TYPE);
		aoTaskDetailsBean.setProcurementTitle((String) aoWorkItemDetails
				.get(HHSConstants.PROPERTY_PE_PROCUREMENT_TITLE));
		aoTaskDetailsBean.setProcurementEpin((String) aoWorkItemDetails.get(HHSConstants.PROPERTY_PE_PROCUREMENT_EPIN));
		aoTaskDetailsBean.setProvider((String) aoWorkItemDetails.get(HHSConstants.PROPERTY_PE_PROVIDER_NAME));
		aoTaskDetailsBean.setOrganizationId((String) aoWorkItemDetails.get(HHSConstants.PROPERTY_PE_PROVIDER_ID));
		aoTaskDetailsBean.setAwardEpin((String) aoWorkItemDetails.get(HHSConstants.PROPERTY_PE_AWARD_EPIN));
		aoTaskDetailsBean.setCt((String) aoWorkItemDetails.get(HHSConstants.PROPERTY_PE_CT));
		aoTaskDetailsBean.setTaskName(lsTaskName);
		aoTaskDetailsBean.setSubmittedBy((String) aoWorkItemDetails.get(HHSConstants.PROPERTY_PE_SUBMITTED_BY));
		aoTaskDetailsBean.setSubmittedDate(DateUtil.getDateMMDDYYYYFormat((Date) aoWorkItemDetails
				.get(HHSConstants.PROPERTY_PE_SUBMITTED_DATE)));
		//Added for R6: return payment review task
		aoTaskDetailsBean.setReturnPaymentDetailId((String)aoWorkItemDetails.get(HHSConstants.RETURN_PE_PAYMENT_DETAIL_ID));
		//Added for R6: return payment review task
		aoTaskDetailsBean.setAssignedTo((String) aoWorkItemDetails.get(HHSConstants.PROPERTY_PE_ASSIGNED_TO));

		if (!lsAssignedToName.contains(HHSConstants.UNASSIGNED_LEVEL)
				&& HHSConstants.FINANCIAL_TASK_PROCESS_ID_MAP.containsKey(lsTaskName))
		{
			aoTaskDetailsBean.setAssignedToUserName(loCurrentLevel.toString() + HHSConstants.SPACE
					+ HHSConstants.HYPHEN + HHSConstants.SPACE + lsAssignedToName);
		}
		else
		{
			aoTaskDetailsBean.setAssignedToUserName(lsAssignedToName);
		}

		aoTaskDetailsBean.setAssignedDate(DateUtil.getDateMMDDYYYYFormat((Date) aoWorkItemDetails
				.get(HHSConstants.PROPERTY_PE_ASSIGNED_DATE)));
		aoTaskDetailsBean.setTaskId(String.valueOf(aoWorkItemDetails.get(HHSConstants.PROPERTY_PE_TASK_ID)));
		if (null != aoWorkItemDetails.get(HHSConstants.CURR_LEVEL))
		{
			aoTaskDetailsBean.setLevel(((Integer) aoWorkItemDetails.get(HHSConstants.CURR_LEVEL)).toString());
		}
		if (null != aoWorkItemDetails.get(HHSConstants.PROPERTY_PE_TASK_TOTAL_LEVEL))
		{
			aoTaskDetailsBean
					.setTotalLevel(((Integer) aoWorkItemDetails.get(HHSConstants.PROPERTY_PE_TASK_TOTAL_LEVEL))
							.toString());
		}
		aoTaskDetailsBean.setAgencyId((String) aoWorkItemDetails.get(HHSConstants.PROPERTY_PE_AGENCY_ID));
		aoTaskDetailsBean.setProcurementId((String) aoWorkItemDetails.get(HHSConstants.PROPERTY_PE_PROCURMENT_ID));
		aoTaskDetailsBean.setContractId((String) aoWorkItemDetails.get(HHSConstants.PROPERTY_PE_CONTRACT_ID));
		aoTaskDetailsBean.setBudgetId((String) aoWorkItemDetails.get(HHSConstants.PROPERTY_PE_BUDGET_ID));
		aoTaskDetailsBean.setInvoiceId((String) aoWorkItemDetails.get(HHSConstants.PROPERTY_PE_INVOICE_ID));
		aoTaskDetailsBean.setNewFYId((String) aoWorkItemDetails.get(HHSConstants.PROPERTY_PE_NEW_FISCAL_YEAR_ID));
		aoTaskDetailsBean
				.setContractConfWob((String) aoWorkItemDetails.get(HHSConstants.PROPERTY_PE_CONTRACT_CONF_WOB));
		aoTaskDetailsBean.setLaunchCOF((Boolean) aoWorkItemDetails.get(HHSConstants.PROPERTY_PE_LAUNCH_COF));
		aoTaskDetailsBean.setLaunchOrgType((String) aoWorkItemDetails.get(HHSConstants.PROPERTY_PE_LAUNCH_ORG_TYPE));
		aoTaskDetailsBean.setLinkedWobNum((String) aoWorkItemDetails.get(HHSConstants.PROPERTY_PE_LINKED_WOBNO));
		aoTaskDetailsBean.setBudgetAdvanceId((String) aoWorkItemDetails.get(HHSConstants.PROPERTY_PE_ADVANCE_NUMBER));
		aoTaskDetailsBean.setCurrentTaskStatus((String) aoWorkItemDetails.get(HHSConstants.TASK_STATUS));
		aoTaskDetailsBean.setTaskSource((String) aoWorkItemDetails.get(HHSConstants.PROPERTY_PE_TASK_SOURCE));
		aoTaskDetailsBean.setCompetitionPoolTitle((String) aoWorkItemDetails
				.get(P8Constants.PE_WORKFLOW_COMPETITION_POOL_TITLE));
		aoTaskDetailsBean.setIsNewLaunch((Boolean) aoWorkItemDetails.get("IsNewLaunch"));
		setMoState("TaskService: set details fetch from filenet into Bean object");

	}

	/**
	 * This method fetches the History From the Agency_audit Table by providing
	 * the required where clause fields.
	 * 
	 * <ul>
	 * <li>Get the workflow Id from input map</li>
	 * <li>Execute query with Id "fetchAgencyTaskHistory" from task service
	 * mapper</li>
	 * <li>Returen the results to controller</li>
	 * </ul>
	 * 
	 * @param aoHMApplicationAudit HashMap Of parameters
	 * @param aoMybatisSession SQL Session
	 * @return List Of CommentsHistoryBean bean.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public List<CommentsHistoryBean> fetchAgencyTaskHistory(HashMap aoHMApplicationAudit, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		List<CommentsHistoryBean> loResultList = null;
		LOG_OBJECT.Debug("Entered into fetchAgencyTaskHistory");
		try
		{
			loResultList = (List<CommentsHistoryBean>) DAOUtil.masterDAO(aoMybatisSession, aoHMApplicationAudit,
					HHSConstants.MAPPER_CLASS_TASK_SERVICE_MAPPER, HHSConstants.FETCH_AGENCY_TASK_HISTORY,
					HHSConstants.JAVA_UTIL_HASH_MAP);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting Agency Task History Details");
			loExp.setContextData(aoHMApplicationAudit);
			LOG_OBJECT.Error("Error while getting Agency Task History Details :", loExp);
			throw loExp;
		}
		LOG_OBJECT.Debug("Exited fetchAgencyTaskHistory");
		return loResultList;
	}

	/**
	 * This method fetches the History From the Agency_audit and provider_audit
	 * Table by providing the required where clause fields. Query Id
	 * 'fetchProviderTaskHistory'
	 * @param aoHMApplicationAudit HashMap Of parameters
	 * @param aoMybatisSession SQL Session
	 * @return List Of CommentsHistoryBean bean.
	 * @throws ApplicationException when any error condition occurred
	 * 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<CommentsHistoryBean> fetchProviderTaskHistory(
			@SuppressWarnings("rawtypes") HashMap aoHMApplicationAudit, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		List<CommentsHistoryBean> loResultList = null;
		LOG_OBJECT.Debug("Entered into fetchProviderTaskHistory");
		try
		{
			loResultList = (List<CommentsHistoryBean>) DAOUtil.masterDAO(aoMybatisSession, aoHMApplicationAudit,
					HHSConstants.MAPPER_CLASS_TASK_SERVICE_MAPPER, HHSConstants.FETCH_PROVIDER_TASK_HISTORY,
					HHSConstants.JAVA_UTIL_HASH_MAP);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting Agency Task History Details");
			loExp.setContextData(aoHMApplicationAudit);
			LOG_OBJECT.Error("Error while getting Agency Task History Details :", loExp);
			throw loExp;
		}
		LOG_OBJECT.Debug("Exited fetchProviderTaskHistory");
		return loResultList;
	}

	/**
	 * This method fetches all Agency User Details on the basis of agency Id and
	 * Level of task assigned to that user by calling fetchAgencyDetails query
	 * in Database .It returns the value in List of StaffDetails bean.
	 * 
	 * @param aoMybatisSession SQL session
	 * @param asAgencyId Agency Id
	 * @param asProcessId Process Id
	 * @param asTaskLevel Current Level of Task
	 * @return List
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings("unchecked")
	public List<StaffDetails> fetchAgencyDetails(SqlSession aoMybatisSession, String asAgencyId, String asTaskLevel,
			String asProcessId) throws ApplicationException
	{
		List<StaffDetails> loStaffDetailsList = null;
		@SuppressWarnings("rawtypes")
		HashMap loHMReqdProp = new HashMap();
		LOG_OBJECT.Debug("Entered into fetchAgencyDetails");
		try
		{
			loHMReqdProp.put(HHSConstants.AS_AGENCY_ID, asAgencyId);
			loHMReqdProp.put(HHSConstants.AS_TASK_LEVEL, asTaskLevel);
			loHMReqdProp.put(HHSConstants.AS_PROCESS_ID, asProcessId);
			loStaffDetailsList = (List<StaffDetails>) DAOUtil.masterDAO(aoMybatisSession, loHMReqdProp,
					HHSConstants.MAPPER_CLASS_TASK_SERVICE_MAPPER, HHSConstants.FETCH_AGENCY_DETAILS,
					HHSConstants.JAVA_UTIL_HASH_MAP);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error in fetchAgencyDetails");
			loExp.setContextData(loHMReqdProp);
			LOG_OBJECT.Error("Error in fetchAgencyDetails :", loExp);
			throw loExp;
		}
		LOG_OBJECT.Debug("Exited fetchAgencyDetails");

		return loStaffDetailsList;
	}

	/**
	 * This method fetches all Agency User Details on the basis of agency Id and
	 * Level of task assigned to that user by calling fetchAgencyDetailsForInbox
	 * query in Database .It returns the value in List of StaffDetails bean.
	 * 
	 * @param aoMybatisSession SQL session
	 * @param asAgencyId Agency Id
	 * @param asProcessId Process Id
	 * @param asUserId Current User Id
	 * @return List
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings("unchecked")
	public List<StaffDetails> fetchAgencyDetailsForInbox(SqlSession aoMybatisSession, String asAgencyId,
			String asUserId, String asProcessId) throws ApplicationException
	{
		List<StaffDetails> loStaffDetailsList = null;
		@SuppressWarnings("rawtypes")
		HashMap loHMReqdProp = new HashMap();
		LOG_OBJECT.Debug("Entered into fetchAgencyDetails");
		try
		{
			loHMReqdProp.put(HHSConstants.AS_AGENCY_ID, asAgencyId);
			loHMReqdProp.put(HHSConstants.AS_USER_ID, asUserId);
			loHMReqdProp.put(HHSConstants.AS_PROCESS_ID, asProcessId);
			loStaffDetailsList = (List<StaffDetails>) DAOUtil.masterDAO(aoMybatisSession, loHMReqdProp,
					HHSConstants.MAPPER_CLASS_TASK_SERVICE_MAPPER, HHSConstants.FETCH_AGENCY_DETAILS_INBOX,
					HHSConstants.JAVA_UTIL_HASH_MAP);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error in fetchAgencyDetails");
			loExp.setContextData(loHMReqdProp);
			LOG_OBJECT.Error("Error in fetchAgencyDetails :", loExp);
			throw loExp;
		}
		LOG_OBJECT.Debug("Exited fetchAgencyDetails");

		return loStaffDetailsList;
	}

	/**
	 * This method fetches all Agency User Details on the basis of agency Id and
	 * Level of task assigned to that user by calling fetchAgencyUserDetails
	 * query in Database .It returns the value in List of StaffDetails bean.
	 * 
	 * @param aoMybatisSession SQL session
	 * @param asAgencyId Agency Id
	 * @return List
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings("unchecked")
	public List<StaffDetails> fetchAgencyUserDetails(SqlSession aoMybatisSession, String asAgencyId)
			throws ApplicationException
	{
		List<StaffDetails> loStaffDetailsList = null;
		@SuppressWarnings("rawtypes")
		HashMap loHMReqdProp = new HashMap();
		LOG_OBJECT.Debug("Entered into fetchAgencyDetails");
		try
		{
			loHMReqdProp.put(HHSConstants.AS_AGENCY_ID, asAgencyId);
			loStaffDetailsList = (List<StaffDetails>) DAOUtil.masterDAO(aoMybatisSession, loHMReqdProp,
					HHSConstants.MAPPER_CLASS_TASK_SERVICE_MAPPER, HHSConstants.FETCH_AGENCY_USER_DETAILS,
					HHSConstants.JAVA_UTIL_HASH_MAP);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error in fetchAgencyDetails");
			loExp.setContextData(loHMReqdProp);
			LOG_OBJECT.Error("Error in fetchAgencyDetails :", loExp);
			throw loExp;
		}
		LOG_OBJECT.Debug("Exited fetchAgencyDetails");

		return loStaffDetailsList;
	}

	/**
	 * This method fetches last provider and internal comment from user_comment
	 * table for all task screens on the basis of workflow Id and Task Id If Any
	 * last comment comes null set empty in Bean object Query Id
	 * 'fetchLastTaskComment' Query Id 'fetchLastComment' Updated for R4 - Added
	 * check to populate Entity ID with Tab Level Entity ID and restore it back
	 * once DB operation is complete
	 * <ul>
	 * <li>Method updated in R4</li>
	 * </ul>
	 * @param aoMybatisSession SQL session
	 * @param aoTaskDetailsBean TaskDetailsBean
	 * @return task details bean
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public TaskDetailsBean fetchLastComment(SqlSession aoMybatisSession, TaskDetailsBean aoTaskDetailsBean)
			throws ApplicationException
	{
		String lsInternalComment = HHSConstants.EMPTY_STRING;
		String lsProviderComment = HHSConstants.EMPTY_STRING;
		String lsUserId = null;
		// R4: Tab Level Comments
		String lsActualEntityId = null;
		HashMap loHashMap = null;
		Map loHMApplicationAudit = new HashMap();
		LOG_OBJECT.Debug("Entered into fetchLastComment");
		try
		{
			// R4: Tab Level Comments
			if (null != aoTaskDetailsBean.getIsEntityTypeTabLevel() && aoTaskDetailsBean.getIsEntityTypeTabLevel())
			{
				lsActualEntityId = aoTaskDetailsBean.getEntityType();
				aoTaskDetailsBean.setEntityType(aoTaskDetailsBean.getEntityTypeTabLevel());
			}
			// R4: Tab Level Comments Ends
			loHMApplicationAudit.put(ApplicationConstants.TASK_DETAILS_BEAN, aoTaskDetailsBean);
			if (aoTaskDetailsBean.getIsTaskScreen())
			{
				loHashMap = (HashMap) DAOUtil.masterDAO(aoMybatisSession, aoTaskDetailsBean,
						HHSConstants.MAPPER_CLASS_TASK_SERVICE_MAPPER, HHSConstants.FETCH_LAST_TASK_COMMENT,
						HHSConstants.CS_TASK_DETAILS_BEAN);
			}
			else
			{
				loHashMap = (HashMap) DAOUtil.masterDAO(aoMybatisSession, aoTaskDetailsBean,
						HHSConstants.MAPPER_CLASS_TASK_SERVICE_MAPPER, HHSConstants.FETCH_LAST_COMMENT,
						HHSConstants.CS_TASK_DETAILS_BEAN);
			}
			if (null != loHashMap)
			{
				lsInternalComment = (String) loHashMap.get(HHSConstants.USER_INTERNAL_COMMENT);
				lsProviderComment = (String) loHashMap.get(HHSConstants.USER_PROVIDER_COMMENT);
				lsUserId = (String) loHashMap.get(HHSConstants.CREATED_BY_USERID);
				if (null == lsInternalComment)
				{
					lsInternalComment = HHSConstants.EMPTY_STRING;
				}
				if (null == lsProviderComment)
				{
					lsProviderComment = HHSConstants.EMPTY_STRING;
				}
			}
			// R4: Tab Level Comments
			if (null != lsActualEntityId && !lsActualEntityId.isEmpty())
			{
				aoTaskDetailsBean.setEntityType(lsActualEntityId);
			}
			// R4: Tab Level Comments Ends
			aoTaskDetailsBean.setAuditUserId(lsUserId);
			aoTaskDetailsBean.setProviderComment(lsProviderComment);
			aoTaskDetailsBean.setInternalComment(lsInternalComment);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error in fetchLastComment");
			loExp.setContextData(loHMApplicationAudit);
			LOG_OBJECT.Error("Error in fetchLastComment :", loExp);
			throw loExp;
		}
		LOG_OBJECT.Debug("Exited fetchLastComment");

		return aoTaskDetailsBean;
	}

	/**
	 * This method fetches last provider and internal comment from user_comment
	 * table for all task screens on the basis of workflow Id and Task Id If Any
	 * last comment comes null set empty in Bean object Query Id
	 * 'fetchLastComment'
	 * <ul>
	 * <li>Get the workflowId and taskId from input task map</li>
	 * <li>Execute query with Id "fetchLastTaskComment" from task service mapper
	 * </li>
	 * <li>Return the results to controller</li>
	 * </ul>
	 * <ul>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * @param aoMybatisSession SQL session
	 * @param aoTaskMap a map containing task details
	 * @param asWobNumber a string value of wob number
	 * @param aoTaskDetailsBean task details bean
	 * @return task detail bean object containing last provider and internal
	 *         comments
	 * @throws ApplicationException If an Application Exception occurs
	 * 
	 * 
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public TaskDetailsBean fetchUserLastComment(SqlSession aoMybatisSession, HashMap<String, Object> aoTaskMap,
			String asWobNumber, TaskDetailsBean aoTaskDetailsBean) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into fetchUserLastComment for wob number:" + asWobNumber);
		try
		{
			if (null != aoTaskMap)
			{
				HashMap<String, Object> loTaskDetailMap = (HashMap<String, Object>) aoTaskMap.get(asWobNumber);
				if (null != loTaskDetailMap)
				{
					aoTaskDetailsBean.setProposalId(String.valueOf(loTaskDetailMap
							.get(P8Constants.PE_WORKFLOW_PROPOSAL_ID)));
					aoTaskDetailsBean.setProcurementId(String.valueOf(loTaskDetailMap
							.get(P8Constants.PE_WORKFLOW_PROCUREMENT_ID)));
					aoTaskDetailsBean.setEvaluationPoolMappingId(String.valueOf(loTaskDetailMap
							.get(P8Constants.EVALUATION_POOL_MAPPING_ID)));
					setEntityId(aoTaskDetailsBean);
					String lsInternalComment = HHSConstants.EMPTY_STRING;
					String lsProviderComment = HHSConstants.EMPTY_STRING;
					aoTaskDetailsBean.setWorkFlowId(asWobNumber);
					HashMap loHashMap = (HashMap) DAOUtil.masterDAO(aoMybatisSession, aoTaskDetailsBean,
							HHSConstants.MAPPER_CLASS_TASK_SERVICE_MAPPER, HHSConstants.FETCH_LAST_COMMENT,
							HHSConstants.CS_TASK_DETAILS_BEAN);
					if (null != loHashMap)
					{
						lsInternalComment = (String) loHashMap.get(HHSConstants.USER_INTERNAL_COMMENT);
						lsProviderComment = (String) loHashMap.get(HHSConstants.USER_PROVIDER_COMMENT);
						if (null == lsInternalComment)
						{
							lsInternalComment = HHSConstants.EMPTY_STRING;
						}
						if (null == lsProviderComment)
						{
							lsProviderComment = HHSConstants.EMPTY_STRING;
						}
					}
					aoTaskDetailsBean.setProviderComment(lsProviderComment);
					aoTaskDetailsBean.setInternalComment(lsInternalComment);
					setMoState("Users Last comments fetched successfully for workflow Id:" + asWobNumber);
				}
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occurred while user last comments for wob number:", asWobNumber);
			setMoState("Error occurred while user last comments for wob number:" + asWobNumber);
			throw aoAppEx;
		}
		LOG_OBJECT.Debug("Exited fetchUserLastComment for wob number:", asWobNumber);
		return aoTaskDetailsBean;
	}

	/**
	 * This method fetches the History From the Agency_audit Table by providing
	 * the required where clause fields.
	 * 
	 * <ul>
	 * <li>Get the workflow Id from input map</li>
	 * <li>Execute query with Id "fetchAgencyTaskHistory" from task service
	 * mapper</li>
	 * <li>Return the results to controller</li>
	 * </ul>
	 * 
	 * @param aoHMApplicationAudit HashMap Of parameters
	 * @param aoMybatisSession SQL Session
	 * @param aoTaskMap Task Map
	 * @param asWobNumber Wob Number
	 * @return List Of CommentsHistoryBean bean.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public List<CommentsHistoryBean> fetchAgencyTaskHistory(SqlSession aoMybatisSession, HashMap aoHMApplicationAudit,
			HashMap<String, Object> aoTaskMap, String asWobNumber) throws ApplicationException
	{
		List<CommentsHistoryBean> loResultList = new ArrayList<CommentsHistoryBean>();
		LOG_OBJECT.Debug("Entered into fetchAgencyTaskHistory");
		try
		{
			if (null != aoTaskMap)
			{
				HashMap<String, Object> loTaskDetailMap = (HashMap<String, Object>) aoTaskMap.get(asWobNumber);
				if (null != loTaskDetailMap)
				{
					String lsProposalId = (String) loTaskDetailMap.get(P8Constants.PE_WORKFLOW_PROPOSAL_ID);
					aoHMApplicationAudit.put(HHSConstants.ENTITY_ID, lsProposalId);
					loResultList = (List<CommentsHistoryBean>) DAOUtil.masterDAO(aoMybatisSession,
							aoHMApplicationAudit, HHSConstants.MAPPER_CLASS_TASK_SERVICE_MAPPER,
							HHSConstants.FETCH_AGENCY_TASK_HISTORY, HHSConstants.JAVA_UTIL_HASH_MAP);
				}
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			setMoState("Error while getting Agency Task History Details");
			aoAppEx.setContextData(aoHMApplicationAudit);
			LOG_OBJECT.Error("Error while getting Agency Task History Details :", aoAppEx);
			throw aoAppEx;
		}
		LOG_OBJECT.Debug("Exited fetchAgencyTaskHistory");
		return loResultList;
	}

	/**
	 * This method fetches the History From the Agency_audit Table by providing
	 * the required where clause fields.
	 * 
	 * <ul>
	 * <li>Get the workflow Id from input map</li>
	 * <li>Execute query with Id "fetchAgencyTaskHistory" from task service
	 * mapper</li>
	 * <li>Return the results to controller</li>
	 * </ul>
	 * <ul>
	 * <li>Method updated in R4</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SQL Session
	 * @param aoHMApplicationAudit HashMap Of parameters
	 * @param aoTaskMap a map containing task and procurement details
	 * @param asWobNumber a string value of wob number
	 * @return List Of CommentsHistoryBean bean.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public List<CommentsHistoryBean> fetchAcceleratorTaskHistory(SqlSession aoMybatisSession,
			HashMap aoHMApplicationAudit, HashMap<String, Object> aoTaskMap, String asWobNumber)
			throws ApplicationException
	{
		List<CommentsHistoryBean> loResultList = new ArrayList<CommentsHistoryBean>();
		LOG_OBJECT.Debug("Entered into fetchAcceleratorTaskHistory");
		try
		{
			if (null != aoTaskMap)
			{
				HashMap<String, Object> loTaskDetailMap = (HashMap<String, Object>) aoTaskMap.get(asWobNumber);
				if (null != loTaskDetailMap)
				{
					String lsProcurementId = (String) loTaskDetailMap.get(P8Constants.PE_WORKFLOW_PROCUREMENT_ID);
					String lsEvaluationPoolMappingId = (String) loTaskDetailMap
							.get(P8Constants.EVALUATION_POOL_MAPPING_ID);
					String lsEntityId = null;
					if (null != lsEvaluationPoolMappingId
							&& !lsEvaluationPoolMappingId.equalsIgnoreCase(HHSConstants.EMPTY_STRING))
					{
						lsEntityId = lsEvaluationPoolMappingId;
					}
					else
					{
						lsEntityId = lsProcurementId;
					}
					aoHMApplicationAudit.put(HHSConstants.ENTITY_ID, lsEntityId);
					loResultList = (List<CommentsHistoryBean>) DAOUtil.masterDAO(aoMybatisSession,
							aoHMApplicationAudit, HHSConstants.MAPPER_CLASS_TASK_SERVICE_MAPPER,
							HHSConstants.FETCH_AGENCY_TASK_HISTORY, HHSConstants.JAVA_UTIL_HASH_MAP);
				}
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			setMoState("Error while getting Accelerator Task History Details");
			aoAppEx.setContextData(aoHMApplicationAudit);
			LOG_OBJECT.Error("Error while getting Accelerator Task History Details :", aoAppEx);
			throw aoAppEx;
		}
		LOG_OBJECT.Debug("Exited fetchAcceleratorTaskHistory");
		return loResultList;
	}

	/**
	 * This method set the entity ID based ob ProposalID or Evaluation Status ID
	 * Query Id 'fetchLastComment'
	 * <ul>
	 * <li>Method updated in R4</li>
	 * </ul>
	 * @param TaskDetailsBean aoTaskDetailsBean
	 * 
	 */
	private void setEntityId(TaskDetailsBean aoTaskDetailsBean)
	{
		String lsEntityId = HHSConstants.EMPTY_STRING;

		if (null != aoTaskDetailsBean.getEvaluationStatusId()
				&& !aoTaskDetailsBean.getEvaluationStatusId().equals(HHSConstants.NULL)
				&& !(aoTaskDetailsBean.getEvaluationStatusId()).isEmpty())
		{
			lsEntityId = aoTaskDetailsBean.getEvaluationStatusId();
		}
		else if (null != aoTaskDetailsBean.getProposalId()
				&& !aoTaskDetailsBean.getProposalId().equals(HHSConstants.NULL)
				&& !(aoTaskDetailsBean.getProposalId()).isEmpty())
		{
			lsEntityId = aoTaskDetailsBean.getProposalId();
		}
		else if (null != aoTaskDetailsBean.getEvaluationPoolMappingId()
				&& !aoTaskDetailsBean.getEvaluationPoolMappingId().equals(HHSConstants.NULL)
				&& !(aoTaskDetailsBean.getEvaluationPoolMappingId()).isEmpty())
		{
			lsEntityId = aoTaskDetailsBean.getEvaluationPoolMappingId();
		}
		else if (null != aoTaskDetailsBean.getProcurementId()
				&& !aoTaskDetailsBean.getProcurementId().equals(HHSConstants.NULL)
				&& !(aoTaskDetailsBean.getProcurementId()).isEmpty())
		{
			lsEntityId = aoTaskDetailsBean.getProcurementId();
		}
		aoTaskDetailsBean.setEntityId(lsEntityId);
	}

	/**
	 * This method returns the contract info by calling query 'getContractInfo'
	 * @param aoMybatisSession
	 * @param aoUserSession
	 * @param aoTaskDetailsBean
	 * @return aoTaskDetailsBean
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public TaskDetailsBean getContractInfo(SqlSession aoMybatisSession, P8UserSession aoUserSession,
			TaskDetailsBean aoTaskDetailsBean) throws ApplicationException
	{
		HashMap loContractInfo = null;
		String lsContractId = aoTaskDetailsBean.getContractId();
		String lsCT = aoTaskDetailsBean.getCt();
		try
		{
			if (null != lsContractId && lsCT.equalsIgnoreCase(ApplicationConstants.NA_KEY))
			{
				loContractInfo = (HashMap) DAOUtil.masterDAO(aoMybatisSession, lsContractId,
						HHSConstants.MAPPER_CLASS_TASK_SERVICE_MAPPER, HHSConstants.GET_CONTRACT_INFO,
						HHSConstants.JAVA_LANG_STRING);
				if (null != loContractInfo)
				{
					lsCT = (String) loContractInfo.get(HHSConstants.CT_NUMBER);
					loContractInfo = new HashMap();
					if (null != lsCT)
					{
						loContractInfo.put(HHSConstants.PROPERTY_PE_CT, lsCT);
						aoTaskDetailsBean.setCt(lsCT);
						new P8ProcessServiceForSolicitationFinancials().setWFProperty(aoUserSession,
								aoTaskDetailsBean.getWorkFlowId(), loContractInfo);
					}
				}
			}
		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Error occured while getting contract info", aoAppExp);
			throw aoAppExp;
		}
		return aoTaskDetailsBean;

	}

	/**
	 * This method fetch the User name for city/agency and provider name
	 * depending upon org type which launch the workflows. Query id
	 * 'fetchProviderUserName' Query id 'fetchCityAgencyUserName' Query id and
	 * findProcurementDetailsForWF'
	 * @param aoMybatisSession SQL session
	 * @param aoTaskDetailsBean Bean object
	 * @return TaskDetailsBean object
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public TaskDetailsBean fetchCityProviderName(SqlSession aoMybatisSession, TaskDetailsBean aoTaskDetailsBean)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered fetchCityProviderName");
		String lsUserName = null;
		String lsTaskType = null;
		String lsProcurementId = null;
		FinancialWFBean loFinancialWFBean = null;
		HashMap loHMWFRequiredProps = new HashMap();

		String lsUserId = aoTaskDetailsBean.getSubmittedBy();
		try
		{
			if (null != aoTaskDetailsBean.getLaunchOrgType())
			{
				if (aoTaskDetailsBean.getLaunchOrgType().equalsIgnoreCase(HHSConstants.PROVIDER))
				{

					lsUserName = (String) DAOUtil.masterDAO(aoMybatisSession, lsUserId,
							HHSConstants.MAPPER_CLASS_TASK_SERVICE_MAPPER, HHSConstants.FETCH_PROVIDER_USER_NAME,
							HHSConstants.JAVA_LANG_STRING);
				}
				else
				{
					lsUserName = (String) DAOUtil.masterDAO(aoMybatisSession, lsUserId,
							HHSConstants.MAPPER_CLASS_TASK_SERVICE_MAPPER, HHSConstants.FETCH_CITY_AGENCY_USER_NAME,
							HHSConstants.JAVA_LANG_STRING);
				}
			}
			else
			{
				lsUserName = (String) DAOUtil.masterDAO(aoMybatisSession, lsUserId,
						HHSConstants.MAPPER_CLASS_TASK_SERVICE_MAPPER, HHSConstants.FETCH_CITY_AGENCY_USER_NAME,
						HHSConstants.JAVA_LANG_STRING);
			}
			aoTaskDetailsBean.setSubmittedByName(lsUserName);

			lsTaskType = aoTaskDetailsBean.getTaskName();
			if (null != lsTaskType && lsTaskType.equalsIgnoreCase(HHSConstants.TASK_PROCUREMENT_COF))
			{
				lsProcurementId = aoTaskDetailsBean.getProcurementId();
				if (null != lsProcurementId)
				{
					loHMWFRequiredProps.put(HHSConstants.PROCUREMENT_ID, lsProcurementId);
					loFinancialWFBean = (FinancialWFBean) DAOUtil.masterDAO(aoMybatisSession, loHMWFRequiredProps,
							HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FIND_PROCUREMENT_DETAILS_FOR_WF,
							HHSConstants.JAVA_UTIL_HASH_MAP);
					aoTaskDetailsBean.setProcurementEpin(loFinancialWFBean.getProcEpin());

				}

			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			setMoState("Error fetchCityProviderName");
			LOG_OBJECT.Error("Error fetchCityProviderName :", aoAppEx);
			throw aoAppEx;
		}
		LOG_OBJECT.Debug("Exited fetchCityProviderName");
		return aoTaskDetailsBean;
	}

	/**
	 * This method Update Award Epin when Award epin assign after task launches
	 * @param aoUserSession Filenet session object
	 * @param asContractId contract Id
	 * @param asAwardEpin Award Epin
	 * @return return true if success
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public boolean updateAwardEPinInTasks(P8UserSession aoUserSession, String asContractId, String asAwardEpin)
			throws ApplicationException
	{
		List<String> loWorkflowIdList = null;
		HashMap loHmWFProperties = new HashMap();
		boolean lbStatus = true;
		try
		{
			loHmWFProperties.put(HHSConstants.PROPERTY_PE_CONTRACT_ID, asContractId);
			loWorkflowIdList = new P8ProcessServiceForSolicitationFinancials().fetchALLWorkflowIdFromView(
					aoUserSession, loHmWFProperties);
			if (null != loWorkflowIdList)
			{
				loHmWFProperties = new HashMap();
				loHmWFProperties.put(HHSConstants.PROPERTY_PE_AWARD_EPIN, asAwardEpin);
				for (String lsWorkflowId : loWorkflowIdList)
				{
					new P8ProcessServiceForSolicitationFinancials().setWFProperty(aoUserSession, lsWorkflowId,
							loHmWFProperties);
				}
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error updateAwardEPinInTasks");
			loExp.setContextData(loHmWFProperties);
			LOG_OBJECT.Error("Error updateAwardEPinInTasks :", loExp);
			throw loExp;
		}
		return lbStatus;
	}

	/**
	 * This method fetches last provider and internal comment from user_comment
	 * table for all task screens on the basis of workflow Id and Task Id If Any
	 * last comment comes null set empty in Bean object
	 * 
	 * <ul>
	 * <li>Get the workflowId and taskId from input task map</li>
	 * <li>Execute query with Id "fetchEvaluationLastComment" from task service
	 * mapper</li>
	 * <li>Return the results to controller</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SQL session
	 * @param aoTaskMap a map containing task details
	 * @param asWobNumber a string value of wob number
	 * @param aoTaskDetailsBean task details bean
	 * @return task detail bean object containing last provider and internal
	 *         comments
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public TaskDetailsBean fetchEvaluationLastComment(SqlSession aoMybatisSession, HashMap<String, Object> aoTaskMap,
			String asWobNumber, TaskDetailsBean aoTaskDetailsBean) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into fetchEvaluationLastComment for wob number:" + asWobNumber);
		try
		{
			if (null != aoTaskMap)
			{
				HashMap<String, Object> loTaskDetailMap = (HashMap<String, Object>) aoTaskMap.get(asWobNumber);
				if (null != loTaskDetailMap)
				{
					aoTaskDetailsBean.setEvaluationStatusId(String.valueOf(loTaskDetailMap
							.get(P8Constants.PE_WORKFLOW_EVALUATION_STATUS_ID)));
					aoTaskDetailsBean.setProposalId(String.valueOf(loTaskDetailMap
							.get(P8Constants.PE_WORKFLOW_PROPOSAL_ID)));
					aoTaskDetailsBean.setProcurementId(String.valueOf(loTaskDetailMap
							.get(P8Constants.PE_WORKFLOW_PROCUREMENT_ID)));
					setEntityId(aoTaskDetailsBean);
					String lsInternalComment = HHSConstants.EMPTY_STRING;
					String lsProviderComment = HHSConstants.EMPTY_STRING;
					HashMap loHashMap = (HashMap) DAOUtil.masterDAO(aoMybatisSession, aoTaskDetailsBean,
							HHSConstants.MAPPER_CLASS_TASK_SERVICE_MAPPER, HHSConstants.FETCH_EVALUATION_LAST_COMMENT,
							HHSConstants.CS_TASK_DETAILS_BEAN);
					if (null != loHashMap)
					{
						lsInternalComment = (String) loHashMap.get(HHSConstants.USER_INTERNAL_COMMENT);
						lsProviderComment = (String) loHashMap.get(HHSConstants.USER_PROVIDER_COMMENT);
						if (null == lsInternalComment)
						{
							lsInternalComment = HHSConstants.EMPTY_STRING;
						}
						if (null == lsProviderComment)
						{
							lsProviderComment = HHSConstants.EMPTY_STRING;
						}
					}
					aoTaskDetailsBean.setProviderComment(lsProviderComment);
					aoTaskDetailsBean.setInternalComment(lsInternalComment);
					setMoState("Users Last comments fetched successfully for workflow Id:" + asWobNumber);
				}
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occurred while user last comments for wob number:", asWobNumber);
			setMoState("Error occurred while user last comments for wob number:" + asWobNumber);
			throw aoAppEx;
		}
		LOG_OBJECT.Debug("Exited fetchUserLastComment for wob number:", asWobNumber);
		return aoTaskDetailsBean;
	}
	/**
	 * This method is used to fetch Task Level For Audit.
	 * @param aoUserSession Filenet session object
	 * @param aoTaskDetailsBeanList List
	 * @param asWobNum String
	 * @param aoHmRequiredProps HashMap
	 * @return aoTaskDetailsBeanList List<TaskDetailsBean>
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public List<TaskDetailsBean> fetchTaskLevelForAudit(P8UserSession aoUserSession,
			List<TaskDetailsBean> aoTaskDetailsBeanList, String asWobNum, HashMap aoHmRequiredProps)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered getchTaskLevelForAudit");
		try
		{
			HashMap loWorkItemDetails = new P8ProcessService().getWorkItemDetails(aoUserSession, asWobNum,
					aoHmRequiredProps);
			if (!loWorkItemDetails.isEmpty())
			{
				HashMap loTaskMap = (HashMap) loWorkItemDetails.get(asWobNum);
				if (null != loTaskMap && null != loTaskMap.get(HHSConstants.CURR_LEVEL))
				{
					String lsTaskLevel = String.valueOf((Integer) loTaskMap.get(HHSConstants.CURR_LEVEL));
					if (null != aoTaskDetailsBeanList && !aoTaskDetailsBeanList.isEmpty())
					{
						for (TaskDetailsBean loTaskDetailsBean : aoTaskDetailsBeanList)
						{
							loTaskDetailsBean.setLevel(lsTaskLevel);
						}
					}
				}
			}
			else
			{
				throw new ApplicationException("This Task is no longer Exist.");
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			setMoState("Error getchTaskLevelForAudit");
			aoAppEx.setContextData(aoHmRequiredProps);
			LOG_OBJECT.Error("Error getchTaskLevelForAudit :", aoAppEx);
			throw aoAppEx;
		}
		LOG_OBJECT.Debug("Exited getchTaskLevelForAudit");
		return aoTaskDetailsBeanList;
	}

	/**
	 * <ul>
	 * This method is added in 3.2.0 release for enhancement #6361
	 * <li>This method will fetch the user list from the assigned list for the
	 * selected level.</li>
	 * <li>It execute the query <b>fetchSelectedUserAssignedLevel</b> to fetch
	 * the level of the selected user</li>
	 * <li>It execute the query <b>fetchAgencyDetails</b> to fetch all the users
	 * details based upon the level returned from previous query</li>
	 * </ul>
	 * @param aoMybatisSession SQL session to connect database
	 * @param aoFilterMap Filter map with all options selected by user
	 * @param asAgencyId Agency ID
	 * @param asProcessId Process ID for the selected task
	 * @return list of user details based upon the selected level
	 * @throws ApplicationException if any exception occurred.
	 */
	public List<StaffDetails> fetchSelectedUserAssignedLevel(SqlSession aoMybatisSession,
			HashMap<String, String> aoFilterMap, String asAgencyId, String asProcessId) throws ApplicationException
	{
		String lsSelecetedAssignedTo = null;
		String lsAssignedLevel = null;
		HashMap<String, String> loHMReqdProp = new HashMap<String, String>();
		List<StaffDetails> loStaffDetailsList = null;
		try
		{
			lsSelecetedAssignedTo = aoFilterMap.get(HHSConstants.PROPERTY_PE_ASSIGNED_TO);
			loHMReqdProp.put(HHSConstants.AS_AGENCY_ID, asAgencyId);
			loHMReqdProp.put(HHSConstants.AS_PROCESS_ID, asProcessId);
			loHMReqdProp.put(HHSConstants.TASK_OWNER_NAME, aoFilterMap.get(HHSConstants.PROPERTY_PE_ASSIGNED_TO));
			if (null != lsSelecetedAssignedTo && lsSelecetedAssignedTo.contains(HHSConstants.UNASSIGNED_LEVEL))
			{
				lsAssignedLevel = lsSelecetedAssignedTo.substring(lsSelecetedAssignedTo.length() - 1,
						lsSelecetedAssignedTo.length());
			}
			else if (!(null == lsSelecetedAssignedTo || lsSelecetedAssignedTo.isEmpty() || lsSelecetedAssignedTo
					.equalsIgnoreCase(HHSConstants.ALL_STAFF)))
			{
				lsAssignedLevel = (String) DAOUtil.masterDAO(aoMybatisSession, loHMReqdProp,
						HHSConstants.MAPPER_CLASS_TASK_SERVICE_MAPPER, HHSConstants.FETCH_SELECTED_USER_ASSIGNED_LEVEL,
						HHSConstants.JAVA_UTIL_HASH_MAP);
			}
			if (null != lsAssignedLevel && !lsAssignedLevel.isEmpty())
			{
				loHMReqdProp.put(HHSConstants.AS_TASK_LEVEL, lsAssignedLevel);
				loStaffDetailsList = (List<StaffDetails>) DAOUtil.masterDAO(aoMybatisSession, loHMReqdProp,
						HHSConstants.MAPPER_CLASS_TASK_SERVICE_MAPPER, HHSConstants.FETCH_AGENCY_DETAILS,
						HHSConstants.JAVA_UTIL_HASH_MAP);
			}
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error Fetching User Assigned Level");
			loExp.setContextData(aoFilterMap);
			LOG_OBJECT.Error("Error Fetching User Assigned Level", loExp);
			throw loExp;
		}
		return loStaffDetailsList;

	}

	// R5 change starts
	/**
	 * This method is added for Release 5. This method fetches the History From
	 * the Agency_audit Table by providing the required where clause fields.
	 * 
	 * <ul>
	 * <li>Get the workflow Id from input map</li>
	 * <li>Execute query with Id "fetchAgencyTaskHistory" from task service
	 * mapper</li>
	 * <li>Return the results to controller</li>
	 * </ul>
	 * 
	 * @param aoHMApplicationAudit HashMap Of parameters
	 * @param aoMybatisSession SQL Session
	 * @return List Of CommentsHistoryBean bean.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public List<CommentsHistoryBean> fetchPsrTaskHistoryDetails(HashMap aoTaskDetailMap, SqlSession aoMybatisSession,
			String asWobNumber) throws ApplicationException
	{
		List<CommentsHistoryBean> loResultList = null;
		HashMap loMap = new HashMap();
		loMap = (HashMap) aoTaskDetailMap.get(asWobNumber);
		String lsEntityId = (String) loMap.get(HHSConstants.PROPERTY_PE_ENTITY_ID);
		List lsPsrTaskList = new ArrayList<String>();
		lsPsrTaskList.add(HHSR5Constants.TASK_COMPLETE_PSR);
		lsPsrTaskList.add(HHSR5Constants.TASK_APPROVE_PSR);
		loMap.put(HHSConstants.ENTITY_ID, lsEntityId);
		loMap.put(HHSConstants.ENTITY_LIST, lsPsrTaskList);
		LOG_OBJECT.Debug("Entered into fetchPsrTaskHistory");
		try
		{
			loResultList = (List<CommentsHistoryBean>) DAOUtil.masterDAO(aoMybatisSession, loMap,
					HHSConstants.MAPPER_CLASS_TASK_SERVICE_MAPPER, HHSConstants.FETCH_AGENCY_TASK_HISTORY,
					HHSConstants.JAVA_UTIL_HASH_MAP);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting PSR Task History Details");
			loExp.setContextData(loMap);
			LOG_OBJECT.Error("Error while getting PSR Task History Details :", loExp);
			throw loExp;
		}
		LOG_OBJECT.Debug("Exited fetchPsrTaskHistory");
		return loResultList;
	}

	/**
	 * This method is added in R5 for Negotiation workflow. This method fetches
	 * last provider and internal comment from user_comment table for all task
	 * screens on the basis of workflow Id and Task Id If Any last comment comes
	 * null set empty in Bean object Query Id 'fetchLastComment'
	 * <ul>
	 * <li>Get the workflowId and taskId from input task map</li>
	 * <li>Execute query with Id "fetchLastTaskComment" from task service mapper
	 * </li>
	 * <li>Return the results to controller</li>
	 * </ul>
	 * @param aoMybatisSession SQL session
	 * @param aoTaskMap a map containing task details
	 * @param asWobNumber a string value of wob number
	 * @param aoTaskDetailsBean task details bean
	 * @return task detail bean object containing last provider and internal
	 *         comments
	 * @throws ApplicationException If an Application Exception occurs
	 * 
	 * 
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public TaskDetailsBean fetchFinalizeAwardLastComment(SqlSession aoMybatisSession, Map<String, Object> aoTaskMap,
			String asWobNumber, TaskDetailsBean aoTaskDetailsBean) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into fetchFinalizeAwardLastComment for wob number:" + asWobNumber);
		try
		{
			if (null != aoTaskMap)
			{
				Map<String, Object> loTaskDetailMap = (HashMap<String, Object>) aoTaskMap.get(asWobNumber);
				if (null != loTaskDetailMap)
				{
					String lsInternalComment = HHSConstants.EMPTY_STRING;
					String lsProviderComment = HHSConstants.EMPTY_STRING;
					aoTaskDetailsBean.setWorkFlowId(asWobNumber);
					HashMap loHashMap = (HashMap) DAOUtil.masterDAO(aoMybatisSession, aoTaskDetailsBean,
							HHSConstants.MAPPER_CLASS_TASK_SERVICE_MAPPER, HHSR5Constants.FETCH_FINALIZE_AWARD_COMMENT,
							HHSConstants.CS_TASK_DETAILS_BEAN);
					if (null != loHashMap)
					{
						lsInternalComment = (String) loHashMap.get(HHSConstants.USER_INTERNAL_COMMENT);
						lsProviderComment = (String) loHashMap.get(HHSConstants.USER_PROVIDER_COMMENT);
						if (null == lsInternalComment)
						{
							lsInternalComment = HHSConstants.EMPTY_STRING;
						}
						if (null == lsProviderComment)
						{
							lsProviderComment = HHSConstants.EMPTY_STRING;
						}
					}
					aoTaskDetailsBean.setProviderComment(lsProviderComment);
					aoTaskDetailsBean.setInternalComment(lsInternalComment);
					setMoState("Users Last comments fetched successfully for workflow Id:" + asWobNumber);
				}
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occurred while user last comments for wob number:", asWobNumber);
			setMoState("Error occurred while user last comments for wob number:" + asWobNumber);
			throw aoAppEx;
		}
		LOG_OBJECT.Debug("Exited fetchUserLastComment for wob number:", asWobNumber);
		return aoTaskDetailsBean;
	}

	/**
	 * This method is added for Release 5. This method fetches the History From
	 * the Agency_audit Table by providing the required where clause fields.
	 * 
	 * <ul>
	 * <li>Get the workflow Id from input map</li>
	 * <li>Execute query with Id "fetchAgencyTaskHistory" from task service
	 * mapper</li>
	 * <li>Return the results to controller</li>
	 * </ul>
	 * 
	 * @param aoHMApplicationAudit HashMap Of parameters
	 * @param aoMybatisSession SQL Session
	 * @return List Of CommentsHistoryBean bean.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public List<CommentsHistoryBean> fetchFinalizeTaskHistoryDetails(Map aoTaskDetailMap, SqlSession aoMybatisSession,
			String asWobNumber) throws ApplicationException
	{
		List<CommentsHistoryBean> loResultList = null;
		Map loMap = new HashMap();
		loMap = (HashMap) aoTaskDetailMap.get(asWobNumber);
		String lsEntityId = (String) loMap.get(HHSConstants.PROPERTY_PE_ENTITY_ID);
		List lsPsrTaskList = new ArrayList<String>();
		lsPsrTaskList.add(HHSR5Constants.APPROVE_AWARD_AMOUNT);
		lsPsrTaskList.add(HHSR5Constants.FINALIZE_AWARD_AMOUNT);
		loMap.put(HHSConstants.ENTITY_ID, lsEntityId);
		loMap.put(HHSConstants.ENTITY_LIST, lsPsrTaskList);
		LOG_OBJECT.Debug("Entered into fetchPsrTaskHistory");
		try
		{
			loResultList = (List<CommentsHistoryBean>) DAOUtil.masterDAO(aoMybatisSession, loMap,
					HHSConstants.MAPPER_CLASS_TASK_SERVICE_MAPPER, HHSConstants.FETCH_AGENCY_TASK_HISTORY,
					HHSConstants.JAVA_UTIL_HASH_MAP);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting PSR Task History Details");
			loExp.setContextData(loMap);
			LOG_OBJECT.Error("Error while getting PSR Task History Details :", loExp);
			throw loExp;
		}
		LOG_OBJECT.Debug("Exited fetchPsrTaskHistory");
		return loResultList;
	}
	/**
	 * This method is used to fetch Export Task Details from DB 
	 * 
	 * <ul>
	 * <li>Execute query with Id "fetchInvoiceDetailForTaskExport" from task service
	 * mapper</li>
	 * <li>Return the results to controller</li>
	 * </ul>
	 * 
	 * @param aoAgencyTaskBeanList List<AgencyTaskBean>
	 * @param aoMybatisSession SQL Session
	 * @return List Of AgencyTaskBeanList bean.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public List<AgencyTaskBean> fetchTaskExportDBDetails(SqlSession aoMybatisSession,
			List<AgencyTaskBean> aoAgencyTaskBeanList) throws ApplicationException
	{
		String lsInvoiceStartDate=null;
		String lsInvoiceEndDate=null;
		String lsSubmittedByName=null;
		
		Iterator<AgencyTaskBean> loListItr = aoAgencyTaskBeanList.iterator();
		while (loListItr.hasNext())
		{
			AgencyTaskBean loAgencyTaskBean = (AgencyTaskBean) loListItr.next();
			if (null != loAgencyTaskBean.getTaskName()
					&& loAgencyTaskBean.getTaskName().equalsIgnoreCase(HHSConstants.TASK_INVOICE_REVIEW))
			{
				HashMap loHashMap = (HashMap) DAOUtil.masterDAO(aoMybatisSession, loAgencyTaskBean,
						HHSConstants.MAPPER_CLASS_TASK_SERVICE_MAPPER,"fetchInvoiceDetailForTaskExport",
						"com.nyc.hhs.model.AgencyTaskBean");
				if (null != loHashMap)
				{
					lsInvoiceStartDate = (String) loHashMap.get("INVOICE_START_DATE");
					lsInvoiceEndDate = (String) loHashMap.get("INVOICE_END_DATE");
					loAgencyTaskBean.setServiceStartDate(lsInvoiceStartDate);
					loAgencyTaskBean.setServiceEndDate(lsInvoiceEndDate);

				}			
			}
			lsSubmittedByName= (String) DAOUtil.masterDAO(aoMybatisSession, loAgencyTaskBean,
					HHSConstants.MAPPER_CLASS_TASK_SERVICE_MAPPER,"fetchUserNameForTaskExport",
					"com.nyc.hhs.model.AgencyTaskBean");
			if (null != lsSubmittedByName)
			{
				loAgencyTaskBean.setSubmittedBy(lsSubmittedByName);
			}

		}

		return aoAgencyTaskBeanList;
	}
	/**
	 * This method is used to get Export Task List
	 * 
	 * <ul>
	 * <li>Execute query with Id "getExportTaskList" from task service
	 * mapper</li>
	 * <li>Return the results to controller</li>
	 * </ul>
	 * 
	 * @param asStatus String
	 * @param aoMybatisSession SQL Session
	 * @return List Of loHMExportList
	 * @throws ApplicationException If an Application Exception occurs
	 */
	
	public List<HashMap> getExportTaskList(SqlSession aoMybatisSession, String asStatus) throws ApplicationException
	{
		List<HashMap> loHMExportList = (List<HashMap>) DAOUtil.masterDAO(aoMybatisSession, asStatus,
				HHSConstants.MAPPER_CLASS_TASK_SERVICE_MAPPER, "getExportTaskList", HHSConstants.JAVA_LANG_STRING);
		return loHMExportList;
	}
	
}