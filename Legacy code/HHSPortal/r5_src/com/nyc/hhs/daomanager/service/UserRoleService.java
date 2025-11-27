package com.nyc.hhs.daomanager.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.AssigneeList;
import com.nyc.hhs.model.DefaultAssignment;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.HHSUtil;

/**
 * This service class will get the method calls from controller through
 * transaction layer. Execute queries by calling mapper and return query output
 * back to controller. If any error exists, wrap the exception into Application
 * Exception and throw it to controller.
 */
public class UserRoleService extends ServiceState
{
	private static final LogInfo LOG_OBJECT = new LogInfo(AuditHistoryService.class);

	/**
	 * This method is added as a part of Release 5 for Manage Organization This
	 * method will fetch the askAgainFlag this flag will be use for
	 * defaultAssignee popUp should appear or not
	 * @param aoMybatisSession Sql session object
	 * @param asTaskType Task type string
	 * @param asTaskLevel Task level string
	 * @param asEntityId Entity id string
	 * @return Ask again flag string
	 * @throws ApplicationException
	 */
	public String fetchAskAgainFlag(SqlSession aoMybatisSession, String asTaskType, String asTaskLevel,
			String asEntityId) throws ApplicationException
	{
		String loAskAgainFlag = null;
		String lsQueryId = null;
		String lsContractId = null;
		HashMap<String, Object> loHmap = new HashMap<String, Object>();
		try
		{
			lsQueryId = HHSR5Constants.GET_QUERY_ID_DEFAULT_ASSIGNEE.get(asTaskType);
			if (null != lsQueryId)
			{
				lsContractId = (String) DAOUtil.masterDAO(aoMybatisSession, asEntityId,
						HHSR5Constants.MAPPER_CLASS_USERROLE_MAPPER, lsQueryId, HHSConstants.JAVA_LANG_STRING);
				loHmap.put(HHSConstants.CONTRACT_ID_WORKFLOW, lsContractId);
				loHmap.put(HHSConstants.TASK_TYPE, HHSUtil.setTaskType(asTaskType));
				loHmap.put(HHSConstants.TASK_LEVEL, HHSConstants.LEVEL + asTaskLevel);
				if (null != lsContractId)
				{
					loAskAgainFlag = (String) DAOUtil.masterDAO(aoMybatisSession, loHmap,
							HHSR5Constants.MAPPER_CLASS_USERROLE_MAPPER, HHSR5Constants.CHECK_ASKAGAIN_FLAG,
							HHSR5Constants.JAVA_UTIL_HASH_MAP);
				}
				LOG_OBJECT.Debug("Successfully fetched the askAgainflag");
			}
		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Error occured while fetching askAgain flag record", aoAppExp);
			throw aoAppExp;
		}
		return loAskAgainFlag;
	}

	/**
	 * This method is added as a part of Release 5 for Manage Organization this
	 * method will update default assignee details
	 * @param aoMybatisSession Sql session object
	 * @param aoDefaultAssignmentBean DefaultAssignment object
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	public void defaultAssignmentInformation(SqlSession aoMybatisSession, DefaultAssignment aoDefaultAssignmentBean)
			throws ApplicationException
	{
		String lsQueryId = null;
		String lsContractId = null;
		try
		{
			if (aoDefaultAssignmentBean.getIsfinancials() != null
					&& aoDefaultAssignmentBean.getIsfinancials().equalsIgnoreCase(HHSR5Constants.TRUE))
			{
				lsQueryId = HHSR5Constants.GET_QUERY_ID_DEFAULT_ASSIGNEE.get(aoDefaultAssignmentBean.getTaskType());

				lsContractId = (String) DAOUtil.masterDAO(aoMybatisSession, aoDefaultAssignmentBean.getEntityId(),
						HHSR5Constants.MAPPER_CLASS_USERROLE_MAPPER, lsQueryId, HHSConstants.JAVA_LANG_STRING);
				LOG_OBJECT.Info("fetched ContractId: " + lsContractId);
				aoDefaultAssignmentBean.setContractId(lsContractId);
				if (aoDefaultAssignmentBean.getDefaultAssignments() != null
						&& aoDefaultAssignmentBean.getDefaultAssignments().equalsIgnoreCase(HHSConstants.YES))
				{
					DAOUtil.masterDAO(aoMybatisSession, aoDefaultAssignmentBean,
							HHSR5Constants.MAPPER_CLASS_USERROLE_MAPPER, HHSR5Constants.DELETE_DEFAULT_ASSIGNEE,
							HHSR5Constants.DEFAULT_ASSIGNMENT_BEAN);
					DAOUtil.masterDAO(aoMybatisSession, aoDefaultAssignmentBean,
							HHSR5Constants.MAPPER_CLASS_USERROLE_MAPPER, HHSR5Constants.INSERT_DEFAULT_ASSIGNEE,
							HHSR5Constants.DEFAULT_ASSIGNMENT_BEAN);
				}
				else
				{
					if (aoDefaultAssignmentBean.getKeepDefault() == null)
					{
						aoDefaultAssignmentBean.setAssigneeUserId(null);
						DAOUtil.masterDAO(aoMybatisSession, aoDefaultAssignmentBean,
								HHSR5Constants.MAPPER_CLASS_USERROLE_MAPPER, HHSR5Constants.UPDATE_DEFAULT_ASSIGNEE,
								HHSR5Constants.DEFAULT_ASSIGNMENT_BEAN);

					}
					if (aoDefaultAssignmentBean.getAskFlag() != null
							&& aoDefaultAssignmentBean.getAskFlag().equalsIgnoreCase(HHSConstants.YES_UPPERCASE))
					{
						Integer loCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoDefaultAssignmentBean,
								HHSR5Constants.MAPPER_CLASS_USERROLE_MAPPER, HHSR5Constants.UPDATE_DONOT_ASKAGAIN_FLAG,
								HHSR5Constants.DEFAULT_ASSIGNMENT_BEAN);
						if (loCount == 0)
						{
							aoDefaultAssignmentBean.setAssigneeUserId(null);
							DAOUtil.masterDAO(aoMybatisSession, aoDefaultAssignmentBean,
									HHSR5Constants.MAPPER_CLASS_USERROLE_MAPPER,
									HHSR5Constants.INSERT_DEFAULT_ASSIGNEE, HHSR5Constants.DEFAULT_ASSIGNMENT_BEAN);
						}
					}
				}
			}
		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Error occured while updating record", aoAppExp);
			throw aoAppExp;
		}
	}

	/**
	 * This method is added as a part of Release 5 for Manage Organization this
	 * method will update default assignee details
	 * @param aoMybatisSession sql session object
	 * @param aoTaskDetailsBean List of task details
	 * @param aoDefaultAssignmentBean  DefaultAssignmentBean object
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	public void defaultAssignmentInformationMultiAssign(SqlSession aoMybatisSession,
			List<TaskDetailsBean> aoTaskDetailsBean) throws ApplicationException
	{
		try
		{
			for (TaskDetailsBean aoDefaultAssignmentBean : aoTaskDetailsBean)
			{
				defaultAssignmentInformation(aoMybatisSession, aoDefaultAssignmentBean);
			}
		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Error occured while updating record", aoAppExp);
			throw aoAppExp;
		}
	}

	/**
	 * This method is added as a part of Release 5 for Manage Organization This
	 * method will fetch reassigned details for all level
	 * @param aoMybatisSession Sql session object
	 * @param aoHmap Hashmap object 
	 * @return loAssigneeList Assignee list
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	@SuppressWarnings("unchecked")
	public List<AssigneeList> getReassigneeList(SqlSession aoMybatisSession, HashMap<String, Object> aoHmap)
			throws ApplicationException
	{
		List<AssigneeList> loAssigneeList = null;
		String lsQueryId = null;
		String lsContractId = null;
		DefaultAssignment loDefaultAssignmentBean = new DefaultAssignment();
		String loTotalReviewLevels = null;
		try
		{
			loDefaultAssignmentBean.setTaskType((String) aoHmap.get(HHSConstants.TASK_TYPE));
			loDefaultAssignmentBean.setTaskLevel((String) aoHmap.get(HHSConstants.TASK_LEVEL));
			loDefaultAssignmentBean.setEntityId((String) aoHmap.get(HHSConstants.ENTITY_ID));
			lsQueryId = HHSR5Constants.GET_QUERY_ID_DEFAULT_ASSIGNEE.get(loDefaultAssignmentBean.getTaskType());
			lsContractId = (String) DAOUtil.masterDAO(aoMybatisSession, loDefaultAssignmentBean.getEntityId(),
					HHSR5Constants.MAPPER_CLASS_USERROLE_MAPPER, lsQueryId, HHSConstants.JAVA_LANG_STRING);
			LOG_OBJECT.Info("fetched ContractId: " + lsContractId);
			aoHmap.put(HHSConstants.CONTRACT_ID_WORKFLOW, lsContractId);
			loTotalReviewLevels = (String) DAOUtil.masterDAO(aoMybatisSession, aoHmap,
					HHSR5Constants.MAPPER_CLASS_USERROLE_MAPPER, HHSR5Constants.FETCH_REVIEW_LEVELS_QUERY,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			LOG_OBJECT.Info("Number of Levels: " + loTotalReviewLevels);
			aoHmap.put(HHSR5Constants.TOAL_REVIEW_LEVELS, loTotalReviewLevels);
			loAssigneeList = new ArrayList<AssigneeList>();
			loAssigneeList = (List<AssigneeList>) DAOUtil.masterDAO(aoMybatisSession, aoHmap,
					HHSR5Constants.MAPPER_CLASS_USERROLE_MAPPER, HHSR5Constants.GET_REASSIGNEE_LIST,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			LOG_OBJECT.Info("fetched Assignee List Details: " + loAssigneeList.toString());
		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Error occured while fetching record", aoAppExp);
			throw aoAppExp;
		}
		return loAssigneeList;

	}

	/**
	 * This method is added as a part of Release 5 for Manage Organization this
	 * method will update default assignee details
	 * @param aoMybatisSession Sql session object
	 * @param aoDefaultAssignmentBean List of Default Assignment
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	public void defaultAssignmentInformation(SqlSession aoMybatisSession,
			List<DefaultAssignment> aoDefaultAssignmentBean) throws ApplicationException
	{
		String lsQueryId = null;
		String lsContractId = null;
		try
		{
			if (aoDefaultAssignmentBean != null && !aoDefaultAssignmentBean.isEmpty())
			{
				lsQueryId = HHSR5Constants.GET_QUERY_ID_DEFAULT_ASSIGNEE.get(aoDefaultAssignmentBean.get(0)
						.getTaskType());

				lsContractId = (String) DAOUtil.masterDAO(aoMybatisSession, aoDefaultAssignmentBean.get(0)
						.getEntityId(), HHSR5Constants.MAPPER_CLASS_USERROLE_MAPPER, lsQueryId,
						HHSConstants.JAVA_LANG_STRING);
				LOG_OBJECT.Info("fetched ContractId: " + lsContractId);
				aoDefaultAssignmentBean.get(0).setContractId(lsContractId);
				DAOUtil.masterDAO(aoMybatisSession, aoDefaultAssignmentBean.get(0),
						HHSR5Constants.MAPPER_CLASS_USERROLE_MAPPER, HHSR5Constants.DELETE_DEFAULT_REASSIGNMENT,
						HHSR5Constants.DEFAULT_ASSIGNMENT_BEAN);
				for (DefaultAssignment defaultAssignment : aoDefaultAssignmentBean)
				{
					defaultAssignment.setContractId(lsContractId);
					DAOUtil.masterDAO(aoMybatisSession, defaultAssignment, HHSR5Constants.MAPPER_CLASS_USERROLE_MAPPER,
							HHSR5Constants.INSERT_DEFAULT_ASSIGNEE, HHSR5Constants.DEFAULT_ASSIGNMENT_BEAN);
				}
			}
		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Error occured while updating record", aoAppExp);
			throw aoAppExp;
		}
	}

	/**
	 * This method is added as a part of Release 5 for Manage Organization this
	 * method will fetch default assignee details
	 * @param aoMybatisSession Sql session object
	 * @param asEntityId Entity id string
	 * @param asTaskType Task type string
	 * @param asTaskLevel Task level string
	 * @param aoDefaultAssignmentBean DefaultAssignmentBean object
	 * @return TaskDetailsBean object
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 * @throws InterruptedException 
	 */
	public TaskDetailsBean fetchAssigneeListDetails(SqlSession aoMybatisSession, String asEntityId, String asTaskType,
			String asTaskLevel) throws ApplicationException, InterruptedException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		Map<String, Object> loHmap = new HashMap<String, Object>();
		String lsContractId = null;
		String lsQueryId = null;
		try
		{
			//Emergency Build 4.0.1 for advance request review stuck task
			Thread.sleep(5000);
			lsQueryId = HHSR5Constants.GET_QUERY_ID_DEFAULT_ASSIGNEE.get(asTaskType);
			if (null != lsQueryId)
			{
				lsContractId = (String) DAOUtil.masterDAO(aoMybatisSession, asEntityId,
						HHSR5Constants.MAPPER_CLASS_USERROLE_MAPPER, lsQueryId, HHSConstants.JAVA_LANG_STRING);
				loHmap.put(HHSConstants.CONTRACT_ID_WORKFLOW, lsContractId);
				loHmap.put(HHSConstants.TASK_TYPE, asTaskType);
				loHmap.put(HHSConstants.TASK_LEVEL, HHSConstants.LEVEL + asTaskLevel);
				loTaskDetailsBean = (TaskDetailsBean) DAOUtil.masterDAO(aoMybatisSession, loHmap,
						HHSR5Constants.MAPPER_CLASS_USERROLE_MAPPER, HHSR5Constants.GET_ASSIGNEE_DATA,
						HHSR5Constants.JAVA_UTIL_HASH_MAP);
			}
		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Error occured while updating record", aoAppExp);
			throw aoAppExp;
		}
		return loTaskDetailsBean;
	}

	/**
	 * <p>
	 * This method is added as a part of Release 5 for Manage Organization this
	 * method will update Review Level Details
	 * <ul>
	 * <li>liTotalRecords is calculated by using
	 * <b>updateReviewLevelsDetails</b></li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession - mybatis SQL session
	 * @return lbStatus - Boolean flag to indicate records updated
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	public Boolean updateReviewLevelsDetails(SqlSession aoMybatisSession) throws ApplicationException
	{
		Boolean lbStatus = false;
		try
		{
			Integer liTotalRecords = 0;
			liTotalRecords = (Integer) DAOUtil.masterDAO(aoMybatisSession, null,
					HHSR5Constants.MAPPER_CLASS_USERROLE_MAPPER, HHSR5Constants.UPDATE_REVIEW_LEVELS_DETAILS, null);
			if (null != liTotalRecords && liTotalRecords > 0)
			{
				lbStatus = true;
			}
		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Error occured while updating record for updateReviewLevelsDetails", aoAppExp);
			throw aoAppExp;
		}
		return lbStatus;
	}

	/**
	 * The method is added in Release 7 for getting assignee details in case of
	 * return for revision.
	 * @param aoMybatisSession
	 * @param asEntityId
	 * @param asQueryId
	 * @param lsReviewProcessId
	 * @return loDefaultAssignment
	 * @throws ApplicationException
	 */
	public DefaultAssignment getDefaultAssignmentForReturnForRevision(SqlSession aoMybatisSession, String asEntityId,
			String asQueryId, Integer lsReviewProcessId) throws ApplicationException
	{
		String lsContractId = null;
		Map<String, Integer> loMap = new HashMap<String, Integer>();
		DefaultAssignment loDefaultAssignment = null;
		try
		{
			lsContractId = (String) DAOUtil.masterDAO(aoMybatisSession, asEntityId,
					HHSR5Constants.MAPPER_CLASS_USERROLE_MAPPER, asQueryId, HHSConstants.JAVA_LANG_STRING);

			if (lsContractId != null)
			{
				loMap.put(HHSConstants.CONTRACT_ID1, Integer.parseInt(lsContractId));
				loMap.put(HHSR5Constants.TASK_REVIEW_PROCESS_ID, lsReviewProcessId);
				loDefaultAssignment = (DefaultAssignment) DAOUtil.masterDAO(aoMybatisSession, loMap,
						HHSR5Constants.MAPPER_CLASS_USERROLE_MAPPER, HHSR5Constants.GET_ASSIGNEE_FOR_RETURN_FOR_REVISION,
						HHSR5Constants.JAVA_UTIL_HASH_MAP);
			}
		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Error occured while fetching record for getDefaultAssignmentForReturnForRevision", aoAppExp);
			throw aoAppExp;
		}
		return loDefaultAssignment;
	}
}
