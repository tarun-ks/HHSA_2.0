package com.nyc.hhs.service.filenetmanager.p8dataprovider;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSP8Constants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.AgencyTaskBean;
import com.nyc.hhs.model.DefaultAssignment;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.HHSUtil;

import filenet.vw.api.VWException;
import filenet.vw.api.VWFetchType;
import filenet.vw.api.VWQueue;
import filenet.vw.api.VWQueueElement;
import filenet.vw.api.VWQueueQuery;
import filenet.vw.api.VWSession;
import filenet.vw.api.VWStepElement;

/**
 * This class is generally used to perform all P8 process operations on FileNet.
 * The methods of this class will be executed through P8ProcessServices.
 * 
 * The class is updated in Release 7 to incorporate changes related to Auto Approved Modification.
 * 
 */

public class P8ProcessOperationForSolicitationFinancials extends P8ProcessOperations
{

	private static final LogInfo LOG_OBJECT = new LogInfo(P8ProcessOperationForSolicitationFinancials.class);

	/**
	 * This function is used to assigning or re-assigning work items for All
	 * Tasks
	 * 
	 * @param aoVWSession a VWSession object
	 * @param asWobNumber a string value of work flow numbers.
	 * @param asUserId a string value of user name.
	 * @param asSessionUserName a string value of session's user name
	 * @param asQueueName a string value of queue name
	 * @return HashMap loHmServiceCapacityWobNos a map containing service
	 *         capacity work flow numbers
	 * @throws ApplicationException
	 */
	public boolean assignTask(VWSession aoVWSession, String asWobNumber, String asUserId, String asSessionUserName,
			String asQueueName) throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put(HHSConstants.AS_WOB_NBR, asWobNumber);
		loHmReqExceProp.put(HHSConstants.AS_USER_NAME, asUserId);
		loHmReqExceProp.put(HHSConstants.AS_QUEUE_NAME, asQueueName);
		LOG_OBJECT.Debug("Entered HHSR2R3P8ProcessOperations.assignTaskR2R3() with parameters::"
				+ loHmReqExceProp.toString());
		try
		{
			VWStepElement loStepElement = getStepElementfromWobNo(aoVWSession, asWobNumber, asQueueName);
			if (loStepElement == null)
			{
				ApplicationException loAppex = new ApplicationException("Error in getting step element from wob no.");
				loAppex.setContextData(loHmReqExceProp);
				LOG_OBJECT.Error("Error in getting step element from wob no.::", loAppex);
				throw loAppex;
			}
			String lsTaskType = (String) loStepElement.getParameterValue(P8Constants.PROPERTY_PE_TASK_TYPE);
			loStepElement.setParameterValue(P8Constants.PROPERTY_PE_LAST_ASSIGNED, new Date(), false);
			loStepElement.setParameterValue(P8Constants.PROPERTY_PE_TASK_MODIFIED_DATE, new Date(), false);
			loStepElement.setParameterValue(P8Constants.PROPERTY_PE_ASSIGNED_TO, asUserId, false);
			loStepElement.setParameterValue(P8Constants.PROPERTY_PE_ASSIGNED_TO_NAME, asSessionUserName, false);
			loStepElement.doSave(true);
			LOG_OBJECT.Debug("Assigned task::" + asWobNumber + " of task-type " + lsTaskType + " to user::" + asUserId);
			LOG_OBJECT.Debug("Exited HHSR2R3P8ProcessOperations.assignTaskR2R3() ");
		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperationForSolicitationFinancials.assign()::", aoAppex);
			throw aoAppex;
		}
		catch (VWException aoVWEx)
		{
			ApplicationException loAppex = new ApplicationException("Error while assigning work item", aoVWEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperationForSolicitationFinancials.assign()::", loAppex);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error while assigning work item", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperationForSolicitationFinancials.assign()::", loAppex);
			throw loAppex;
		}
		return true;
	}

	/**
	 * This method is used for launching a workflow given by asWorkflowName and
	 * properties mentioned in aohmReqdWorkflowProperties
	 * 
	 * @param aoVWSession the active VWSession object
	 * @param asWorkflowName a string value of workflow name
	 * @param aohmReqdWorkflowProperties map containing workflow properties
	 * @return String workflow number
	 * @throws ApplicationException
	 */
	@Override
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public String launchWorkflow(VWSession aoVWSession, String asWorkflowName, HashMap aoHmReqdWorkflowProperties,
			String asSessionUserName) throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		String lsWobNo = HHSConstants.EMPTY_STRING;
		loHmReqExceProp.put(HHSConstants.REQWORKFLOW_PROP, aoHmReqdWorkflowProperties);
		loHmReqExceProp.put(HHSConstants.AS_WORK_FLOW_NAME, asWorkflowName);
		LOG_OBJECT.Debug("Entered P8ProcessOperationForSolicitationFinancials.launchWorkflow() with parameters::"
				+ loHmReqExceProp.toString());
		if (asWorkflowName == null || asWorkflowName.isEmpty())
		{
			throwApplicationException("Workflow Name is not found", loHmReqExceProp);
		}

		try
		{
			// Fetching PE Session
			VWSession loVWSession = aoVWSession;
			if (loVWSession == null)
			{
				throwApplicationException("VWSession is not found", loHmReqExceProp);
			}

			LOG_OBJECT.Debug("Obtained lsWobNo = " + lsWobNo + " from isTaskExist");
			// create the workflow
			VWStepElement loStepElement = loVWSession.createWorkflow(asWorkflowName);
			if (loStepElement == null)
			{
				throwApplicationException("Error in launching worklow", loHmReqExceProp);
			}

			// set the workflow properties
			loStepElement = peOperationHelper.setWorkFlowProperties(loStepElement, aoHmReqdWorkflowProperties);
			// dispatch the work item
			loStepElement.doDispatch();
			lsWobNo = loStepElement.getWorkflowNumber();
			// Do auditing for the launched workflow item

			LOG_OBJECT
					.Debug("Exited P8ProcessOperationForSolicitationFinancials.launchWorkflow() . Returned lsWobNo ::"
							+ lsWobNo);
			return lsWobNo;

		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperationForSolicitationFinancials.launchWorkflow()::", aoAppex);
			throw aoAppex;

		}
		catch (VWException aoVWEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in Launching Workflow:: " + asWorkflowName,
					aoVWEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperationForSolicitationFinancials.launchWorkflow()::", loAppex);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in launching workflow:: " + asWorkflowName,
					aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperationForSolicitationFinancials.launchWorkflow()::", loAppex);
			throw loAppex;
		}
	}

	/**
	 * This function is used for saving all parameters and move task to next
	 * level.
	 * @param aoStepElement StepElement object
	 * @return
	 * @throws ApplicationException
	 */
	public boolean finishTask(VWStepElement aoStepElement) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered P8ProcessOperationForSolicitationFinancials.finishTask() with parameters::"
				+ aoStepElement);
		try
		{   
			aoStepElement.doSave(false); 
			aoStepElement.doDispatch();
		}
		catch (VWException aoVWEx)
		{
			ApplicationException loAppex = new ApplicationException("Error while finishing a task ", aoVWEx);
			LOG_OBJECT.Error("Exception in P8ProcessOperationForSolicitationFinancials.finishTask()::", loAppex);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error while finishing a task", aoEx);
			LOG_OBJECT.Error("Exception in P8ProcessOperationForSolicitationFinancials.finishTask()::", loAppex);
			throw loAppex;
		}
		LOG_OBJECT.Debug("Exited P8ProcessOperationForSolicitationFinancials.finishTask(). ");
		return true;
	}

	/**
	 * This method get all the open task exist in Filenet PE on the basis of
	 * Condition passed
	 * 
	 * @param aoDBSession Filenet DB session
	 * @param asViewName Filenet PE view name
	 * @param asWhereClause Where condition
	 * @return
	 * @throws ApplicationException
	 */
	public int getOpenTaskCount(SqlSession aoDBSession, String asViewName, String asWhereClause)
			throws ApplicationException
	{
		int liCount = HHSConstants.INT_ZERO;
		ResultSet loResultSet = null;
		Statement loStatement = null;
		java.sql.Connection loConnection = aoDBSession.getConnection();
		StringBuffer lsQuery = new StringBuffer(HHSConstants.EMPTY_STRING);
		if (null == asWhereClause || asWhereClause.isEmpty())
		{
			throw new ApplicationException(
					"Exception in P8ProcessOperationForSolicitationFinancials.getOpenTaskCount() :Where Condition is not set");
		}
		try
		{
			loStatement = loConnection.createStatement();
			lsQuery.append("select count(1) as count from ");
			lsQuery.append(asViewName);
			lsQuery.append(" where ");
			lsQuery.append(asWhereClause);
			loResultSet = executeViewQuery(aoDBSession, lsQuery.toString(), loStatement);

			if (loResultSet.next())
			{
				liCount = loResultSet.getInt(HHSConstants.COUNT);
			}
		}
		catch (SQLException aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in getOpenTaskCount:: " + loResultSet, aoEx);
			LOG_OBJECT.Error("Exception in P8ProcessOperationForSolicitationFinancials.getOpenTaskCount()::", loAppex);
			throw loAppex;
		}
		finally
		{
			closeFilenetDBResources(loResultSet, loStatement);
		}

		return liCount;
	}

	/**
	 * This method fetch the TaskStatus from Filenet PE on the basis of
	 * Condition passed
	 * 
	 * @param aoDBSession Filenet DB session
	 * @param asViewName Filenet PE view name
	 * @param asWhereClause Where condition
	 * @return
	 * @throws ApplicationException
	 */
	public String fetchTaskStatusFromView(SqlSession aoDBSession, String asViewName, String asWhereClause)
			throws ApplicationException
	{
		String lsTaskStatus = HHSConstants.EMPTY_STRING;
		ResultSet loResultSet = null;
		Statement loStatement = null;
		java.sql.Connection loConnection = aoDBSession.getConnection();
		StringBuffer lsQuery = new StringBuffer(HHSConstants.EMPTY_STRING);
		if (null == asWhereClause || asWhereClause.isEmpty())
		{
			throw new ApplicationException(
					"Exception in P8ProcessOperationForSolicitationFinancials.fetchTaskStatus() :Where Condition is not set");
		}
		try
		{
			loStatement = loConnection.createStatement();
			lsQuery.append("select \"TaskStatus\" from ");
			lsQuery.append(asViewName);
			lsQuery.append(" where ");
			lsQuery.append(asWhereClause);
			lsQuery.append(" order by \"F_CreateTime\" desc");
			loResultSet = executeViewQuery(aoDBSession, lsQuery.toString(), loStatement);

			if (loResultSet.next())
			{
				lsTaskStatus = loResultSet.getString(HHSConstants.TASK_STATUS);
			}
		}
		catch (SQLException aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in fetchTaskStatus:: " + loResultSet, aoEx);
			LOG_OBJECT.Error("Exception in P8ProcessOperationForSolicitationFinancials.fetchTaskStatus()::", loAppex);
			throw loAppex;
		}
		finally
		{
			closeFilenetDBResources(loResultSet, loStatement);
		}

		return lsTaskStatus;
	}

	/**
	 * This method fetches the Last TaskStatus from Filenet PE on the basis of
	 * Condition passed
	 * 
	 * @param aoDBSession Filenet DB session
	 * @param asViewName Filenet PE view name
	 * @param asWhereClause Where condition
	 * @return
	 * @throws ApplicationException
	 */
	public String fetchLastTaskStatusFromView(SqlSession aoDBSession, String asViewName, String asWhereClause)
			throws ApplicationException
	{
		String lsTaskStatus = HHSConstants.EMPTY_STRING;
		ResultSet loResultSet = null;
		Statement loStatement = null;
		java.sql.Connection loConnection = aoDBSession.getConnection();
		StringBuffer lsQuery = new StringBuffer(HHSConstants.EMPTY_STRING);
		if (null == asWhereClause || asWhereClause.isEmpty())
		{
			throw new ApplicationException(
					"Exception in P8ProcessOperationForSolicitationFinancials.fetchLastTaskStatusFromView() :Where Condition is not set");
		}
		try
		{
			loStatement = loConnection.createStatement();
			lsQuery.append("select \"LastTaskStatus\" from ");
			lsQuery.append(asViewName);
			lsQuery.append(" where ");
			lsQuery.append(asWhereClause);
			lsQuery.append(" order by \"F_CreateTime\" desc");
			loResultSet = executeViewQuery(aoDBSession, lsQuery.toString(), loStatement);

			if (loResultSet.next())
			{
				lsTaskStatus = loResultSet.getString(HHSConstants.LAST_TASK_STATUS);
			}
		}
		catch (SQLException aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in fetchLastTaskStatusFromView:: "
					+ loResultSet, aoEx);
			LOG_OBJECT
					.Error("Exception in P8ProcessOperationForSolicitationFinancials.fetchLastTaskStatusFromView()::",
							loAppex);
			throw loAppex;
		}
		finally
		{
			closeFilenetDBResources(loResultSet, loStatement);
		}

		return lsTaskStatus;
	}

	/**
	 * This method fetch workflow Id of task in Filenet PE on the basis of
	 * Condition passed
	 * 
	 * @param aoDBSession Filenet DB session
	 * @param asViewName Filenet PE view name
	 * @param asWhereClause Where condition
	 * @return
	 * @throws ApplicationException
	 */
	public List<String> fetchALLWorkflowIdFromView(SqlSession aoDBSession, String asViewName, String asWhereClause)
			throws ApplicationException
	{
		String lsWobNum = HHSConstants.EMPTY_STRING;
		ResultSet loResultSet = null;
		Statement loStatement = null;
		java.sql.Connection loConnection = null;
		List<String> loWorkflowIDList = new ArrayList<String>();
		StringBuffer lsQuery = new StringBuffer(HHSConstants.EMPTY_STRING);
		if (null == asWhereClause || asWhereClause.isEmpty())
		{
			throw new ApplicationException(
					"Exception in P8ProcessOperationForSolicitationFinancials.fetchWorkflowIdFromView() :Where Condition is not set");
		}
		try
		{
			loConnection = aoDBSession.getConnection();
			loStatement = loConnection.createStatement();
			lsQuery.append("select \"F_WobNum\" from ");
			lsQuery.append(asViewName);
			lsQuery.append(" where ");
			lsQuery.append(asWhereClause);
			loResultSet = executeViewQuery(aoDBSession, lsQuery.toString(), loStatement);

			while (loResultSet.next())
			{
				lsWobNum = loResultSet.getString(HHSConstants.F_WOB_NUM);
				loWorkflowIDList.add(lsWobNum);
			}
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Exception in P8ProcessOperationForSolicitationFinancials.fetchWorkflowIdFromView()::",
					aoEx);
			throw aoEx;
		}
		catch (SQLException aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Error in fetchWorkflowIdFromView:: " + loResultSet, aoEx);
			LOG_OBJECT.Error("Exception in P8ProcessOperationForSolicitationFinancials.fetchWorkflowIdFromView()::",
					loAppex);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Error in fetchWorkflowIdFromView:: " + loResultSet, aoEx);
			LOG_OBJECT.Error("Exception in P8ProcessOperationForSolicitationFinancials.fetchWorkflowIdFromView()::",
					loAppex);
			throw loAppex;
		}
		finally
		{
			closeFilenetDBResources(loResultSet, loStatement);
		}
		LOG_OBJECT.Debug("Exiting::::P8ProcessOperationForSolicitationFinancials.fetchWorkflowIdFromView()::",
				loWorkflowIDList.toString());
		return loWorkflowIDList;
	}

	/**
	 * This method fetch workflow Id of task in Filenet PE on the basis of
	 * Condition passed
	 * 
	 * @param aoDBSession Filenet DB session
	 * @param asViewName Filenet PE view name
	 * @param asWhereClause Where condition
	 * @return
	 * @throws ApplicationException
	 */
	public String fetchWorkflowIdFromView(SqlSession aoDBSession, String asViewName, String asWhereClause)
			throws ApplicationException
	{
		String lsWobNum = HHSConstants.EMPTY_STRING;
		ResultSet loResultSet = null;
		Statement loStatement = null;
		java.sql.Connection loConnection = aoDBSession.getConnection();

		StringBuffer lsQuery = new StringBuffer(HHSConstants.EMPTY_STRING);
		if (null == asWhereClause || asWhereClause.isEmpty())
		{
			throw new ApplicationException(
					"Exception in P8ProcessOperationForSolicitationFinancials.fetchWorkflowIdFromView() :Where Condition is not set");
		}
		try
		{
			loStatement = loConnection.createStatement();
			lsQuery.append("select \"F_WobNum\" from ");
			lsQuery.append(asViewName);
			lsQuery.append(" where ");
			lsQuery.append(asWhereClause);
			lsQuery.append(" order by \"F_CreateTime\" desc");
			loResultSet = executeViewQuery(aoDBSession, lsQuery.toString(), loStatement);

			if (loResultSet != null && loResultSet.next())
			{
				lsWobNum = loResultSet.getString(HHSConstants.F_WOB_NUM);
			}
		}
		catch (SQLException aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Error in fetchWorkflowIdFromView:: " + loResultSet, aoEx);
			LOG_OBJECT.Error("Exception in P8ProcessOperationForSolicitationFinancials.fetchWorkflowIdFromView()::",
					loAppex);
			throw loAppex;
		}
		finally
		{
			closeFilenetDBResources(loResultSet, loStatement);
		}

		return lsWobNum;
	}

	/**
	 * This method execute View query and return result set
	 * @param aoDBSession Filenet DB session
	 * @param asQuery View Query
	 * @return
	 * @throws ApplicationException
	 */
	public ResultSet executeViewQuery(SqlSession aoDBSession, String asQuery, Statement aoStatement)
			throws ApplicationException
	{
		ResultSet loResultSet = null;
		// Execute the SQL statement and get the results in a Resultset
		try
		{
			loResultSet = aoStatement.executeQuery(asQuery);
		}
		catch (SQLException aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in executeViewQuery:: " + asQuery, aoEx);
			LOG_OBJECT.Error("Exception in P8ProcessOperationForSolicitationFinancials.executeViewQuery()::", aoEx);
			throw loAppex;
		}

		return loResultSet;
	}

	/**
	 * This Method set Task to unassigned level if assigned user is removed from
	 * that level by agency
	 * 
	 * @param aoUserSession Filenet User Session bean object
	 * @param asViewName View Name
	 * @param asWhereClause Where condition
	 * @return
	 * @throws ApplicationException
	 */
	public boolean setTaskUnassignedForAgencyUsers(P8UserSession aoUserSession, String asViewName, String asWhereClause)
			throws ApplicationException
	{
		String lsWobNum = HHSConstants.EMPTY_STRING;
		String lsCurrentLevel = HHSConstants.EMPTY_STRING;
		String lsCurrentUnassigned = HHSConstants.EMPTY_STRING;
		SqlSession loSession = aoUserSession.getFilenetPEDBSession();
		Statement loStatement = null;
		java.sql.Connection loConnection = loSession.getConnection();
		HashMap loHMreqd = new HashMap();
		ResultSet loResultSet = null;
		StringBuffer lsQuery = new StringBuffer(HHSConstants.EMPTY_STRING);
		if (null == asWhereClause || asWhereClause.isEmpty())
		{
			throw new ApplicationException(
					"Exception in P8ProcessOperationForSolicitationFinancials.setTaskUnassignedForAgencyUsers() :Where Condition is not set");
		}
		try
		{
			loStatement = loConnection.createStatement();
			lsQuery.append("select \"F_WobNum\",\"CurrentLevel\" from ");
			lsQuery.append(asViewName);
			lsQuery.append(" where ");
			lsQuery.append(asWhereClause);
			loResultSet = executeViewQuery(loSession, lsQuery.toString(), loStatement);

			while (loResultSet.next())
			{
				lsWobNum = loResultSet.getString(HHSConstants.F_WOB_NUM);
				lsCurrentLevel = loResultSet.getString(HHSConstants.CURR_LEVEL);
				if (null != lsCurrentLevel)
				{
					lsCurrentUnassigned = HHSConstants.TASK_UNASSIGNED + lsCurrentLevel;
				}
				else
				{
					lsCurrentUnassigned = HHSConstants.UNASSIGNED_LEVEL1;
				}
				loHMreqd.put(HHSConstants.PROPERTY_PE_ASSIGNED_TO, lsCurrentUnassigned);
				loHMreqd.put(HHSConstants.PROPERTY_PE_ASSIGNED_TO_NAME, lsCurrentUnassigned);
				setWFProperty(aoUserSession, lsWobNum, loHMreqd);
			}
		}
		catch (SQLException aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in setTaskUnassignedForAgencyUsers:: "
					+ loResultSet, aoEx);
			LOG_OBJECT.Error(
					"Exception in P8ProcessOperationForSolicitationFinancials.setTaskUnassignedForAgencyUsers::",
					loAppex);
			throw loAppex;
		}
		finally
		{
			closeFilenetDBResources(loResultSet, loStatement);
		}

		return true;
	}

	/**
	 * This method is used for Setting WF Property in open Task
	 * 
	 * @param aoUserSession a user bean having information about user
	 * @param asWobNo a string value of child work flow no
	 * @param asFinishStatus a string value of child status
	 * @return HashMap a map containing finished child tasks
	 * @throws ApplicationException If an application exception occurred
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public boolean setWFProperty(P8UserSession aoUserSession, String asWobNo, HashMap aoHmWFProperties)
			throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put(HHSConstants.FILE_NET_SESSION, aoUserSession.getObjectStoreName());
		loHmReqExceProp.put(HHSConstants.AS_WOB_NO, asWobNo);
		LOG_OBJECT.Debug("Entered P8ProcessService.finishTask() with parameters::" + loHmReqExceProp.toString());
		// Get FileNet PE session
		VWSession loVWSession = filenetConnection.getPESession(aoUserSession);
		if (loVWSession == null)
		{
			ApplicationException loAppex = new ApplicationException("VWSession is not found");
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("VWSession is not found", loAppex);
			throw loAppex;
		}
		try
		{
			// Call the data-provider class
			VWStepElement loStepElement = getStepElementfromWobNo(loVWSession, asWobNo, P8Constants.HSS_QUEUE_NAME);
			if (loStepElement == null)
			{
				throwApplicationException("Error in P8ProcessService.finishTask while fetching step element",
						loHmReqExceProp);
			}
			// set the workflow properties required at a time of finish
			if (null != aoHmWFProperties && !aoHmWFProperties.isEmpty())
			{
				loStepElement = peOperationHelper.setWorkFlowProperties(loStepElement, aoHmWFProperties);
			}
			loStepElement.doSave(true);
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while setting WF property in task.");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessService.finishTask()::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error while setting WF property in task.");
			ApplicationException loAppex = new ApplicationException("Error while  finishing a child task.", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessService.finishTask()::", loAppex);
			throw loAppex;
		}
		LOG_OBJECT.Debug("Exited P8ProcessService.finishTask().");
		return true;
	}

	/**
	 * This method is used for throwing exceptions
	 * 
	 * @param asMessage A string containing the exception message
	 * @param loHmReqExceProp A hashmap containing all the parameters for the
	 *            calling function which will be added to the exception context
	 */
	private void throwApplicationException(String asMessage, HashMap loHmReqExceProp) throws ApplicationException
	{
		ApplicationException loAppex = new ApplicationException(asMessage);
		loAppex.setContextData(loHmReqExceProp);
		LOG_OBJECT.Error(asMessage + "::", loAppex);
		throw loAppex;
	}

	/**
	 * This method closes all Filenet PE database resources
	 * 
	 * @param aoResultSet Result Set object
	 * @param aoStatement Statement object
	 */
	public static void closeFilenetDBResources(ResultSet aoResultSet, Statement aoStatement)
	{
		try
		{
			if (null != aoResultSet)
			{

				aoResultSet.close();
			}
			if (null != aoStatement)
			{
				aoStatement.close();
			}
		}
		catch (SQLException aoEx)
		{
			LOG_OBJECT.Error("Exception while closing Result Set()::", aoEx);
		}
	}

	/**
	 * Changed Method in Release 7 - to remove task which are assigned to 
	 * auto-approver user 
	 * Changed method - Reason: Removed the like clause with "task owner" in
	 * city & agency task filter with release 2.6.0 for defect 5670 This method
	 * get all the open task exist in Filenet PE on the basis of Condition
	 * passed
	 * <ul>
	 * <li>1. This method takes inputs the task types & Owner Name</li>
	 * <li>2. Then it generates a query at run time to fetch count of tasks from
	 * filenet DB</li>
	 * <li>3. Then it hits the db to fetch the count.</li>
	 * </ul>
	 * @param aoDBSession Filenet DB session
	 * @param asViewName Filenet PE view name
	 * @param asWhereClause Where condition
	 * @return a hashmap containing counts against task types
	 * @throws ApplicationException
	 */
	public HashMap<String, Integer> getHomePageTaskCount(SqlSession aoDBSession, String asViewName,
			HashMap<String, Integer> aoTaskTypeMap, String asTaskOwnerName, Boolean aoIncludeNotFlag, String asAgencyId)
			throws ApplicationException
	{
		ResultSet loResultSet = null;
		Statement loStatement = null;
		java.sql.Connection loConnection = aoDBSession.getConnection();
		StringBuffer lsQuery = new StringBuffer(HHSConstants.EMPTY_STRING);
		HashMap<String, Integer> loResultCountMap = null;
		if (null == aoTaskTypeMap)
		{
			throw new ApplicationException(
					"Exception in P8ProcessOperationForSolicitationFinancials.getHomePageTaskCount() :Task Types not found");
		}
		try
		{
			loStatement = loConnection.createStatement();
			if (aoTaskTypeMap.containsKey(P8Constants.PROPERTY_PE_TASK_TYPE_AWARD_APPROVAL)
					&& ApplicationConstants.UNASSIGNED.equalsIgnoreCase(asTaskOwnerName) && !aoIncludeNotFlag)
			{
				lsQuery.append("select \"TaskType\", count(*),\"TaskOwner\" from ");
			}
			else
			{
				lsQuery.append("select \"TaskType\", count(1) from ");
			}
			lsQuery.append(asViewName);
			lsQuery.append(" where ");
			lsQuery.append("\"TaskOwner\" ");
			// Filter Start. Modified below conditions as part of release 2.6.0
			// (defect 5670) for filter key
			if (null != aoIncludeNotFlag && aoIncludeNotFlag)
			{
				lsQuery.append("not like '%");
				lsQuery.append(asTaskOwnerName);
				lsQuery.append("%' and \"TaskType\" in ('");
			}
			else if ((null != aoIncludeNotFlag && !aoIncludeNotFlag)
					&& (asTaskOwnerName != null && !asTaskOwnerName.contains(ApplicationConstants.UNASSIGNED)))
			{
				lsQuery.append("= '");
				lsQuery.append(asTaskOwnerName);
				lsQuery.append("' and \"TaskType\" in ('");
			}
			else
			{
				lsQuery.append("like '%");
				lsQuery.append(asTaskOwnerName);
				lsQuery.append("%' and \"TaskType\" in ('");
			}
			// Filter end for changes of release 2.6.0
			Set<String> loMapKeySet = aoTaskTypeMap.keySet();
			Iterator<String> loKeyIter = loMapKeySet.iterator();
			while (loKeyIter.hasNext())
			{
				lsQuery.append(loKeyIter.next());
				lsQuery.append("'");
				if (loKeyIter.hasNext())
				{
					lsQuery.append(",'");
				}
			}
			lsQuery.append(") and \"taskVisibility\" = 1");
			//R7 changes start - Added to filter out task assigned to Auto approver user
			lsQuery.append(" AND \"TaskOwner\" ");
			lsQuery.append(" not like '%");
			lsQuery.append(HHSR5Constants.AUTO_APPROVER_NAME);
			lsQuery.append("%' ");
			//R7 end
			if (null != asAgencyId)
			{
				lsQuery.append(" and \"AgencyID\" = '");
				lsQuery.append(asAgencyId);
				lsQuery.append("'");
			}
			if (aoTaskTypeMap.containsKey(P8Constants.PROPERTY_PE_TASK_TYPE_AWARD_APPROVAL)
					&& ApplicationConstants.UNASSIGNED.equalsIgnoreCase(asTaskOwnerName) && !aoIncludeNotFlag)
			{
				lsQuery.append(" group by \"TaskType\", \"TaskOwner\"");
			}
			else
			{
				lsQuery.append(" group by \"TaskType\"");
			}
			loResultSet = executeViewQuery(aoDBSession, lsQuery.toString(), loStatement);
			loResultCountMap = new HashMap<String, Integer>();
			while (loResultSet.next())
			{
				String lsTaskTypeColumn = (String) loResultSet.getObject(1);
				Integer loTaskCount = (Integer) loResultSet.getInt(2);
				if (aoTaskTypeMap.containsKey(P8Constants.PROPERTY_PE_TASK_TYPE_AWARD_APPROVAL)
						&& ApplicationConstants.UNASSIGNED.equalsIgnoreCase(asTaskOwnerName) && !aoIncludeNotFlag)
				{
					putData(loResultSet, loResultCountMap, lsTaskTypeColumn, loTaskCount);
				}
				else
				{
					loResultCountMap.put(lsTaskTypeColumn, loTaskCount);
				}
			}
		}
		catch (SQLException aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in getHomePageTaskCount:: " + loResultSet,
					aoEx);
			LOG_OBJECT
					.Error("Exception in P8ProcessOperationForSolicitationFinancials.getHomePageTaskCount::", loAppex);
			throw loAppex;
		}
		finally
		{
			closeFilenetDBResources(loResultSet, loStatement);
		}
		return loResultCountMap;
	}

	/**
	 * This method is used to put data
	 * @param aoResultSet
	 * @param aoResultCountMap
	 * @param asTaskTypeColumn
	 * @param aoTaskCount
	 * @return
	 * @throws SQLException
	 */
	private String putData(ResultSet aoResultSet, HashMap<String, Integer> aoResultCountMap, String asTaskTypeColumn,
			Integer aoTaskCount) throws SQLException
	{
		String lsTaskOwnerColumn = (String) aoResultSet.getObject(3);
		if ("Unassigned - Manager".equals(lsTaskOwnerColumn)
				&& asTaskTypeColumn.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_BR_APPLICATION))
		{
			aoResultCountMap.put(ApplicationConstants.BUSINESS_APP_MANAGER, aoTaskCount);
		}
		else if ("Unassigned - Manager".equals(lsTaskOwnerColumn)
				&& asTaskTypeColumn.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SERVICE_APPLICATION))
		{
			aoResultCountMap.put(ApplicationConstants.SERVICE_APP_MANAGER, aoTaskCount);
		}
		else
		{
			aoResultCountMap.put(asTaskTypeColumn, aoTaskCount);
		}
		return lsTaskOwnerColumn;
	}

	/**
	 * This method query the filenet DB for fetching the list of agency task on
	 * the baisis filter values set from application end.
	 * @param aoDBSession Filenet PE DB session
	 * @param asViewName View name
	 * @param aoFilterProp Filter Properties
	 * @param asOrderBy Order by clause
	 * @param aiPageNum page index
	 * @return List of Agency Task Bean
	 * @throws ApplicationException
	 */
	public List<AgencyTaskBean> fetchAgencyTask(SqlSession aoDBSession, String asViewName, HashMap aoFilterProp,
			String asOrderBy, int aiPageNum) throws ApplicationException
	{
		List<AgencyTaskBean> loTaskList = new ArrayList<AgencyTaskBean>();
		AgencyTaskBean loAgencyTaskBean = null;
		ResultSet loResultSet = null;
		Statement loStatement = null;
		String lsWhereClause = null;
		java.sql.Connection loConnection = aoDBSession.getConnection();
		StringBuffer lsQuery = new StringBuffer("");
		String lsCurrentLevel = null;
		String lsTaskName = null;
		String lsAssignedTo = null;

		try
		{
			lsWhereClause = createAgencyTaskFilter(aoFilterProp);
			loStatement = loConnection.createStatement();
			createAgencyTaskQuery(asViewName, lsWhereClause, asOrderBy, lsQuery, aiPageNum);
			LOG_OBJECT.Debug("Entered P8ProcessOperationForSolicitationFinancials.fetchAgencyTask()");
			loResultSet = executeViewQuery(aoDBSession, lsQuery.toString(), loStatement);
			LOG_OBJECT.Debug("Exited P8ProcessOperationForSolicitationFinancials.fetchAgencyTask()");
			while (loResultSet.next())
			{
				loAgencyTaskBean = new AgencyTaskBean();
				loAgencyTaskBean.setWobNumber(loResultSet.getString(HHSConstants.F_WOB_NUM));
				loAgencyTaskBean.setAgencyId(loResultSet.getString(HHSConstants.PROPERTY_PE_AGENCY_ID));
				lsTaskName = loResultSet.getString(HHSConstants.PROPERTY_PE_TASK_TYPE);
				loAgencyTaskBean.setTaskName(lsTaskName);
				lsCurrentLevel = loResultSet.getString(HHSConstants.CURR_LEVEL);
				loAgencyTaskBean.setTaskLevel(lsCurrentLevel);
				loAgencyTaskBean.setTaskId(loResultSet.getString(HHSConstants.PROPERTY_PE_TASK_ID));
				loAgencyTaskBean.setProcurementTitle(loResultSet.getString(HHSConstants.PROPERTY_PE_PROCUREMENT_TITLE));
				// Start || Changes done for enhancement 6636 for Release 3.12.0
				if (null != loResultSet.getString(HHSConstants.PROPERTY_PE_PROPOSAL_ID)
						&& !loResultSet.getString(HHSConstants.PROPERTY_PE_PROPOSAL_ID).isEmpty()
						&& !loResultSet.getString(HHSConstants.PROPERTY_PE_PROPOSAL_ID).equalsIgnoreCase(
								HHSConstants.NULL))
				{
					loAgencyTaskBean.setProposalId(Integer.parseInt(loResultSet
							.getString(HHSConstants.PROPERTY_PE_PROPOSAL_ID)));
				}
				// End || Changes done for enhancement 6636 for Release 3.12.0
				loAgencyTaskBean.setProviderName(loResultSet.getString(HHSConstants.PROPERTY_PE_PROVIDER_NAME));
				loAgencyTaskBean.setDateCreated(loResultSet.getString(HHSConstants.PROPERTY_PE_SUBMITTED_DATE));
				lsAssignedTo = loResultSet.getString(HHSConstants.PROPERTY_PE_ASSIGNED_TO_NAME);
				if (!lsAssignedTo.contains(HHSConstants.UNASSIGNED_LEVEL) && null != lsCurrentLevel
						&& HHSConstants.FINANCIAL_TASK_PROCESS_ID_MAP.containsKey(lsTaskName))
				{
					// R3Task
					loAgencyTaskBean.setAssignedTo(lsCurrentLevel + HHSConstants.SPACE + HHSConstants.HYPHEN
							+ HHSConstants.SPACE + lsAssignedTo);
				}
				else
				{
					loAgencyTaskBean.setAssignedTo(lsAssignedTo);
				}

				loAgencyTaskBean.setLastAssigned(loResultSet.getString(HHSConstants.PROPERTY_PE_ASSIGNED_DATE));

				loAgencyTaskBean.setStatus(loResultSet.getString(HHSConstants.TASK_STATUS));
				loAgencyTaskBean.setEntityId(loResultSet.getString(P8Constants.PROPERTY_PE_ENTITY_ID));

				// [Start] Extract InvoiceID from Result set R3.7.0
				loAgencyTaskBean.setInvoiceNumber(loResultSet.getString(HHSConstants.PROPERTY_PE_INVOICE_ID));
				// [End] Extract InvoiceID from Result set R3.7.0

				loTaskList.add(loAgencyTaskBean);
			}
		}
		catch (SQLException aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in getOpenTaskCount:: " + loResultSet, aoEx);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.getOpenTaskCount()::", loAppex);
			throw loAppex;
		}
		finally
		{
			closeFilenetDBResources(loResultSet, loStatement);
		}

		return loTaskList;
	}

	/**
	 * This method query the filenet DB for fetching the list of agency task on
	 * the baisis filter values set from application end.
	 * @param aoDBSession Filenet PE DB session
	 * @param asViewName View name
	 * @param aoFilterProp Filter Properties
	 * @param asOrderBy Order by clause
	 * @param aiPageNum page index
	 * @return List of Agency Task Bean
	 * @throws ApplicationException
	 */
	public HashMap fetchAcceleratorTask(SqlSession aoDBSession, String asViewName, HashMap aoFilterProp,
			HashMap aoRequiredProprs) throws ApplicationException
	{
		ResultSet loResultSet = null;
		Statement loStatement = null;
		String lsWhereClause = null;
		HashMap loHMOutput = new HashMap();
		HashMap loHMResultOutput = null;
		java.sql.Connection loConnection = aoDBSession.getConnection();
		StringBuffer lsQuery = new StringBuffer("");
		try
		{
			lsWhereClause = createAgencyTaskFilter(aoFilterProp);
			loStatement = loConnection.createStatement();
			createAcceleratorTaskQuery(asViewName, lsWhereClause, lsQuery, aoRequiredProprs);
			loResultSet = executeViewQuery(aoDBSession, lsQuery.toString(), loStatement);
			String lsValue = "";

			while (loResultSet.next())
			{
				loHMResultOutput = new HashMap();
				if (aoRequiredProprs != null)
				{
					Iterator loIt = aoRequiredProprs.keySet().iterator();
					while (loIt.hasNext())
					{
						String lsKey = (String) loIt.next();
						if (lsKey.equalsIgnoreCase(HHSConstants.PROPERTY_PE_SUBMITTED_DATE)
								|| lsKey.equalsIgnoreCase(HHSConstants.PROPERTY_PE_ASSIGNED_DATE))
						{
							loHMResultOutput.put(lsKey, HHSUtil.getDateFromEpochTime(loResultSet.getString(lsKey)));
						}
						else
						{
							lsValue = loResultSet.getString(lsKey);
							if (null == lsValue)
							{
								lsValue = "";
							}
							loHMResultOutput.put(lsKey, lsValue);
						}
					}
				}

				loHMOutput.put(loResultSet.getString(HHSConstants.F_WOB_NUM), loHMResultOutput);
			}
		}
		catch (SQLException aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in getOpenTaskCount:: " + loResultSet, aoEx);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.getOpenTaskCount()::", loAppex);
			throw loAppex;
		}
		finally
		{
			closeFilenetDBResources(loResultSet, loStatement);
		}

		return loHMOutput;
	}

	/**
	 * This method create the task query to fetch agency task
	 * @param asViewName View name
	 * @param asWhereClause where clause
	 * @param asOrderBy order by
	 * @param lsQuery SQL query statement
	 * @param aiPageNum page num
	 */
	private void createAgencyTaskQuery(String asViewName, String asWhereClause, String asOrderBy, StringBuffer lsQuery,
			int aiPageNum)
	{
		lsQuery.append("select * from ( select rownum as rn, a.*  from (");
		lsQuery.append("select ");
		lsQuery.append("\"F_WobNum\",");
		lsQuery.append("\"");
		lsQuery.append(HHSConstants.PROPERTY_PE_TASK_TYPE);
		lsQuery.append("\",");
		lsQuery.append("\"");
		lsQuery.append(HHSConstants.PROPERTY_PE_PROCUREMENT_TITLE);
		lsQuery.append("\",");
		lsQuery.append("\"");
		lsQuery.append(HHSConstants.PROPERTY_PE_PROPOSAL_ID);
		lsQuery.append("\",");
		lsQuery.append("\"");
		lsQuery.append(HHSConstants.PROPERTY_PE_PROVIDER_NAME);
		lsQuery.append("\",");
		lsQuery.append("\"");
		lsQuery.append(HHSConstants.TASK_STATUS);
		lsQuery.append("\",");
		lsQuery.append("\"");
		lsQuery.append(HHSConstants.PROPERTY_PE_SUBMITTED_DATE);
		lsQuery.append("\",");
		lsQuery.append("\"");
		lsQuery.append(HHSConstants.PROPERTY_PE_ASSIGNED_TO_NAME);
		lsQuery.append("\",");
		lsQuery.append("\"");
		lsQuery.append(HHSConstants.PROPERTY_PE_ASSIGNED_DATE);
		lsQuery.append("\",");
		lsQuery.append("\"");
		lsQuery.append(P8Constants.PROPERTY_PE_ENTITY_ID);
		lsQuery.append("\",");

		/*
		 * [Start]Add InvoiceNumber for R3.7.0 enhancement #6361 to get Service
		 * date from portal DB
		 */
		lsQuery.append("\"");
		lsQuery.append(HHSConstants.PROPERTY_PE_INVOICE_ID);
		lsQuery.append("\",");
		/*
		 * [End]Add InvoiceNumber for R3.7.9 to enhancement #6361 get Service
		 * date from portal DB
		 */

		lsQuery.append("\"");
		lsQuery.append(HHSConstants.PROPERTY_PE_TASK_ID);
		lsQuery.append("\",");
		lsQuery.append("\"");
		lsQuery.append(HHSConstants.CURR_LEVEL);
		lsQuery.append("\",");
		lsQuery.append("\"");
		lsQuery.append(HHSConstants.PROPERTY_PE_AGENCY_ID);
		lsQuery.append("\"");
		lsQuery.append("  from ");
		lsQuery.append(asViewName);
		lsQuery.append(" where ");
		lsQuery.append(asWhereClause);
		lsQuery.append(" order by ");
		lsQuery.append(asOrderBy);
		lsQuery.append(" )a ) where rownum <= ");
		lsQuery.append(HHSConstants.PE_GRID_PAGE_SIZE);
		lsQuery.append(" and rn >  (");
		lsQuery.append(aiPageNum);
		lsQuery.append("-1)*");
		lsQuery.append(HHSConstants.PE_GRID_PAGE_SIZE);
	}

	/**
	 * This method create the task query to fetch agency task
	 * @param asViewName View name
	 * @param asWhereClause where clause
	 * @param asOrderBy order by
	 * @param lsQuery SQL query statement
	 * @param aiPageNum page num
	 */
	private void createAcceleratorTaskQuery(String asViewName, String asWhereClause, StringBuffer lsQuery,
			HashMap aoRequiredProprs)
	{
		lsQuery.append("select ");
		lsQuery.append("\"F_WobNum\",");

		if (aoRequiredProprs != null)
		{
			Iterator loIt = aoRequiredProprs.keySet().iterator();
			while (loIt.hasNext())
			{
				lsQuery.append("\"");
				lsQuery.append((String) loIt.next());
				lsQuery.append("\"");

				if (loIt.hasNext())
				{
					lsQuery.append(",");
				}
			}
		}
		lsQuery.append("  from ");
		lsQuery.append(asViewName);
		lsQuery.append(" where ");
		lsQuery.append(asWhereClause);
	}

	/**
	 * Changed Method in Release 7 - to remove task which are assigned to 
	 * auto-approver user 
	 * This method create where clause on the basis of values selected in
	 * filter from application.
	 * @param aoHmFilter HashMap object
	 * @return Query's where clause
	 * @throws ApplicationException
	 */
	public String createAgencyTaskFilter(HashMap aoHmFilter) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered P8ProcessOperations.createAgencyTaskFilter()");
		String lsQueueFilter = "";
		StringBuffer loStrBufferQueueFilter = new StringBuffer("");
		try
		{
			if (aoHmFilter != null)
			{
				Iterator loIt = aoHmFilter.keySet().iterator();
				while (loIt.hasNext())
				{
					loStrBufferQueueFilter = setValues(aoHmFilter, loStrBufferQueueFilter, loIt);
				}// while ends
			}
			loStrBufferQueueFilter.append(" AND \"taskVisibility\" = 1 ");
			//R7 changes start - Added to filter out task assigned to Auto approver user
			loStrBufferQueueFilter.append(" AND \"TaskOwner\" ");
			loStrBufferQueueFilter.append(" not like '%");
			loStrBufferQueueFilter.append(HHSR5Constants.AUTO_APPROVER_NAME);
			loStrBufferQueueFilter.append("%' ");
			//R7 end
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error While Creating queue Filter::", aoEx);
			loAppex.setContextData(aoHmFilter);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.createAgencyTaskFilter()::", loAppex);
			throw loAppex;
		}
		lsQueueFilter = loStrBufferQueueFilter.toString();
		LOG_OBJECT.Debug("Exited P8ProcessOperations.createAgencyTaskFilter(). Returned lsQueueFilter::"
				+ lsQueueFilter);
		// Return the created queue filter
		return lsQueueFilter;
	}

	/**
	 * This method is used to set the Values.
	 * @param aoHmFilter
	 * @param aoStrBufferQueueFilter
	 * @param aoIt
	 * @return
	 * @throws ApplicationException
	 */
	private StringBuffer setValues(HashMap aoHmFilter, StringBuffer aoStrBufferQueueFilter, Iterator aoIt)
			throws ApplicationException
	{
		aoStrBufferQueueFilter = aoStrBufferQueueFilter.append(" ");
		String lsFilterKey = (String) aoIt.next();
		String lsFilterValue = (String) aoHmFilter.get(lsFilterKey);
		if (lsFilterValue.contains("'"))
		{
			lsFilterValue = lsFilterValue.replace("'", "''");
		}
		if (lsFilterValue.equalsIgnoreCase(P8Constants.PROPERTY_PE_VALUE_ALL_STAFF))
		{
			checkAllStaffValues(aoStrBufferQueueFilter, lsFilterKey);
		}
		else if (lsFilterValue.equalsIgnoreCase(P8Constants.UNASSIGNED_ALL_LEVELS))
		{
			checkUnassignedAllLevels(aoStrBufferQueueFilter, lsFilterKey);
		}

		else if (lsFilterKey.equalsIgnoreCase(P8Constants.PROPERTY_PE_ASSIGNED_TO)
				&& lsFilterValue.equalsIgnoreCase(P8Constants.PROPERTY_PE_VALUE_UNASSIGN))
		{ // Check for "Unassign" in AssignedTo
			checkTaskOwner(aoStrBufferQueueFilter);
		}
		else if (lsFilterKey.equalsIgnoreCase(HHSConstants.PROPERTY_HMP_SUBMITTED_FROM))
		{
			checkSubmittedFrom(aoHmFilter, aoStrBufferQueueFilter);
		}
		else if (lsFilterKey.equalsIgnoreCase(HHSConstants.PROPERTY_HMP_SUBMITTED_TO))
		{
			checkSubmittedTo(aoHmFilter, aoStrBufferQueueFilter);
		}
		else if (lsFilterKey.equalsIgnoreCase(HHSConstants.PROPERTY_HMP_ASSIGNED_FROM))
		{
			checkAssignedFrom(aoHmFilter, aoStrBufferQueueFilter);
		}
		else if (lsFilterKey.equalsIgnoreCase(HHSConstants.PROPERTY_HMP_ASSIGNED_TO))
		{
			checkAssignedTo(aoHmFilter, aoStrBufferQueueFilter);
		}
		else if (lsFilterKey.equalsIgnoreCase(HHSConstants.PROPERTY_PE_TASK_TYPE)
				&& !lsFilterValue.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_ALL_APPLICATIONS))
		{
			checkTaskType(aoStrBufferQueueFilter, lsFilterKey, lsFilterValue);
		}
		else if ((lsFilterKey.equalsIgnoreCase(HHSConstants.PROPERTY_PE_TASK_TYPE) && lsFilterValue
				.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_ALL_APPLICATIONS)))
		{
			allApplicationQueueFilter(aoStrBufferQueueFilter);
		}
		// Filter Start. Added this filter with release 2.6.0 for defect 5670
		// filter key "Task owner" & "Provider Name"
		else if (((lsFilterKey != null && lsFilterKey.equalsIgnoreCase(HHSConstants.PROPERTY_PE_ASSIGNED_TO)) && (lsFilterValue
				.startsWith(ApplicationConstants.CITY) || lsFilterValue.startsWith(ApplicationConstants.AGENCY) || lsFilterValue
					.equalsIgnoreCase(P8Constants.PE_TASK_UNASSIGNED)))
				|| (lsFilterKey != null && (lsFilterKey.equalsIgnoreCase(HHSConstants.PROPERTY_PE_PROVIDER_NAME) || lsFilterKey
						.equalsIgnoreCase(HHSConstants.PROPERTY_PE_AGENCY_ID))))
		{
			aoStrBufferQueueFilter.append("lower(\"");
			aoStrBufferQueueFilter.append(lsFilterKey);
			aoStrBufferQueueFilter.append("\")");
			aoStrBufferQueueFilter.append(" =lower('");
			aoStrBufferQueueFilter.append(lsFilterValue);
			aoStrBufferQueueFilter.append("')");
		}
		// Filter ends for defect defect 5670
		else
		{
			aoStrBufferQueueFilter.append("lower(\"");
			aoStrBufferQueueFilter.append(lsFilterKey);
			aoStrBufferQueueFilter.append("\")");
			aoStrBufferQueueFilter.append(" like lower('%");
			aoStrBufferQueueFilter.append(lsFilterValue);
			aoStrBufferQueueFilter.append("%')");
		}
		// append AND if multiple filters are present
		if (aoIt.hasNext())
		{
			aoStrBufferQueueFilter.append(" AND ");
		}
		return aoStrBufferQueueFilter;
	}

	/**
	 * This method is used to perform check TaskOwner
	 * @param aoStrBufferQueueFilter
	 */
	private void checkTaskOwner(StringBuffer aoStrBufferQueueFilter)
	{
		aoStrBufferQueueFilter.append("\"");
		aoStrBufferQueueFilter.append(P8Constants.PROPERTY_PE_ASSIGNED_TO);
		aoStrBufferQueueFilter.append("\"");
		aoStrBufferQueueFilter.append(" like 'Unassign%' ");
	}

	/**
	 * This method is used to perform check TaskType
	 * @param aoStrBufferQueueFilter
	 * @param asFilterKey
	 * @param asFilterValue
	 */
	private void checkTaskType(StringBuffer aoStrBufferQueueFilter, String asFilterKey, String asFilterValue)
	{
		aoStrBufferQueueFilter.append("\"");
		aoStrBufferQueueFilter.append(asFilterKey);
		aoStrBufferQueueFilter.append("\"");
		aoStrBufferQueueFilter.append(" = '");
		aoStrBufferQueueFilter.append(asFilterValue);
		aoStrBufferQueueFilter.append("'");
	}

	/**
	 * This method is used to perform check AssignedTo
	 * @param aoHmFilter
	 * @param aoStrBufferQueueFilter
	 */
	private void checkAssignedTo(HashMap aoHmFilter, StringBuffer aoStrBufferQueueFilter)
	{
		aoStrBufferQueueFilter.append("\"");
		aoStrBufferQueueFilter.append(HHSConstants.PROPERTY_PE_ASSIGNED_DATE);
		aoStrBufferQueueFilter.append("\"");
		aoStrBufferQueueFilter.append("<='");
		aoStrBufferQueueFilter.append(aoHmFilter.get(HHSConstants.PROPERTY_HMP_ASSIGNED_TO));
		aoStrBufferQueueFilter.append("'");
	}

	/**
	 * This method is used to perform check AssignedFrom
	 * @param aoHmFilter
	 * @param aoStrBufferQueueFilter
	 */
	private void checkAssignedFrom(HashMap aoHmFilter, StringBuffer aoStrBufferQueueFilter)
	{
		aoStrBufferQueueFilter.append("\"");
		aoStrBufferQueueFilter.append(HHSConstants.PROPERTY_PE_ASSIGNED_DATE);
		aoStrBufferQueueFilter.append("\"");
		aoStrBufferQueueFilter.append(">='");
		aoStrBufferQueueFilter.append(aoHmFilter.get(HHSConstants.PROPERTY_HMP_ASSIGNED_FROM));
		aoStrBufferQueueFilter.append("'");
	}

	/**
	 * This method is used to perform check SubmittedTo
	 * @param aoHmFilter
	 * @param aoStrBufferQueueFilter
	 */
	private void checkSubmittedTo(HashMap aoHmFilter, StringBuffer aoStrBufferQueueFilter)
	{
		aoStrBufferQueueFilter.append("\"");
		aoStrBufferQueueFilter.append(HHSConstants.PROPERTY_PE_SUBMITTED_DATE);
		aoStrBufferQueueFilter.append("\"");
		aoStrBufferQueueFilter.append("<='");
		aoStrBufferQueueFilter.append(aoHmFilter.get(HHSConstants.PROPERTY_HMP_SUBMITTED_TO));
		aoStrBufferQueueFilter.append("'");
	}

	/**
	 * This method is used to perform check SubmittedFrom
	 * @param aoHmFilter
	 * @param aoStrBufferQueueFilter
	 */
	private void checkSubmittedFrom(HashMap aoHmFilter, StringBuffer aoStrBufferQueueFilter)
	{
		aoStrBufferQueueFilter.append("\"");
		aoStrBufferQueueFilter.append(HHSConstants.PROPERTY_PE_SUBMITTED_DATE);
		aoStrBufferQueueFilter.append("\"");
		aoStrBufferQueueFilter.append(">='");
		aoStrBufferQueueFilter.append(aoHmFilter.get(HHSConstants.PROPERTY_HMP_SUBMITTED_FROM));
		aoStrBufferQueueFilter.append("'");
	}

	/**
	 * This method is used to perform check UnassignedAllLevels
	 * @param aoStrBufferQueueFilter
	 * @param asFilterKey
	 */
	private void checkUnassignedAllLevels(StringBuffer aoStrBufferQueueFilter, String asFilterKey)
	{
		aoStrBufferQueueFilter.append("\"");
		aoStrBufferQueueFilter.append(asFilterKey);
		aoStrBufferQueueFilter.append("\"");
		aoStrBufferQueueFilter.append(" like '%Unassigned%'");
	}

	/**
	 * This method is used to perform check AllStaffValues
	 * @param aoStrBufferQueueFilter
	 * @param asFilterKey
	 */
	private void checkAllStaffValues(StringBuffer aoStrBufferQueueFilter, String asFilterKey)
	{
		aoStrBufferQueueFilter.append("\"");
		aoStrBufferQueueFilter.append(asFilterKey);
		aoStrBufferQueueFilter.append("\"");
		aoStrBufferQueueFilter.append(" not like '%Unassigned%'");
	}

	/**
	 * This method is used for Creating Queue filter for all application task
	 * hash map
	 * 
	 * @param aoStrBufferQueueFilter String buffer containing the queue filter
	 * @throws ApplicationException
	 */
	private StringBuffer allApplicationQueueFilter(StringBuffer aoStrBufferQueueFilter) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered P8ProcessOperations.allApplicationQueueFilter()");
		try
		{
			aoStrBufferQueueFilter.append("\"");
			aoStrBufferQueueFilter.append(P8Constants.PROPERTY_PE_TASK_TYPE);
			aoStrBufferQueueFilter.append("\" in ('");
			aoStrBufferQueueFilter.append(P8Constants.PROPERTY_PE_TASK_TYPE_BR_APPLICATION);
			aoStrBufferQueueFilter.append("' , '");
			aoStrBufferQueueFilter.append(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_BASIC);
			aoStrBufferQueueFilter.append("' , '");
			aoStrBufferQueueFilter.append(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_BOARD);
			aoStrBufferQueueFilter.append("' , '");
			aoStrBufferQueueFilter.append(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_FILINGS);
			aoStrBufferQueueFilter.append("' , '");
			aoStrBufferQueueFilter.append(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_POLICIES);
			aoStrBufferQueueFilter.append("' , '");
			aoStrBufferQueueFilter.append(P8Constants.PROPERTY_PE_TASK_TYPE_SERVICE_APPLICATION);
			aoStrBufferQueueFilter.append("' )");
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Error while Creating Queue Filter in allApplicationQueueFilter ", aoEx);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.allApplicationQueueFilter()::", loAppex);
			throw loAppex;
		}
		LOG_OBJECT.Debug("Exited P8ProcessOperations.allApplicationQueueFilter()");

		return aoStrBufferQueueFilter;
	}

	/**
	 * This method is used to fetch the work items present in the mentioned
	 * Queue since the time period mentioned in the argument.It is added as part
	 * of enhancement number 6508 in build 3.6.0
	 * @param aoVWSession PE Session object
	 * @param asQueueName Queue name
	 * @param aiStuckTime Time since the items stuck in the queue
	 * @return count of stuck items in the specified queue
	 * @throws ApplicationException
	 */
	public static HashMap<String, Integer> getQueueWorkitems(VWSession aoVWSession, String asQueueName, int aiStuckTime)
			throws ApplicationException
	{

		VWQueueQuery loVWQueueQuery = null;
		VWQueue loVWQueue = null;
		String lsQueueFilter = "";
		int liWorkitemsCount = 0;
		String lsTaskType = "";
		HashMap<String, Integer> loHmWorkItemsDetails = new HashMap<String, Integer>();
		Integer liTaskCount = 0;
		try
		{

			if (aiStuckTime > 0)
			{
				lsQueueFilter = "F_EnqueueTime <=" + DateUtil.getTime(aiStuckTime);
			}
			if (aoVWSession == null)
				LOG_OBJECT.Error("ERROR while fetching VWSession Object");

			// fetch the queue object
			loVWQueue = aoVWSession.getQueue(asQueueName);
			if (loVWQueue == null)
				LOG_OBJECT.Error("Error While Fetching Queue");

			int liFlag = loVWQueue.QUERY_READ_LOCKED;
			int liType = VWFetchType.FETCH_TYPE_QUEUE_ELEMENT;
			// create query
			loVWQueueQuery = loVWQueue.createQuery(null, null, null, liFlag, lsQueueFilter, null, liType);
			liWorkitemsCount = loVWQueueQuery.fetchCount();

			while (loVWQueueQuery.hasNext())
			{
				VWQueueElement loVWQueueElement = (VWQueueElement) loVWQueueQuery.next();
				// VWStepElement loStepElement =
				// loVWQueueElement.fetchStepElement(false, false);
				// loVWQueueElement.fetchWorkObject(false, false);

				lsTaskType = loVWQueueElement.getDataField("F_Subject").getStringValue();
				if (loHmWorkItemsDetails.containsKey(lsTaskType))
				{
					liTaskCount = (Integer) loHmWorkItemsDetails.get(lsTaskType);
					loHmWorkItemsDetails.put(lsTaskType, liTaskCount + 1);
				}
				else
				{
					loHmWorkItemsDetails.put(lsTaskType, 1);
				}
			}

			LOG_OBJECT.Error("Total Workflow Count " + liWorkitemsCount);
		}
		/**
		 * catch the application exception thrown from the service layer log it
		 * into the console
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while fetching QueWorkItem from Queue" + asQueueName, aoAppEx);
			throw aoAppEx;
		}
		/**
		 * catch any exception thrown from the code and wrap it into application
		 * exception and propagate forward
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetching QueWorkItem from Queue" + asQueueName, aoExp);
			throw new ApplicationException("Error while fetching QueWorkItem from Queue" + asQueueName, aoExp);
		}
		return loHmWorkItemsDetails;
	}

	/**
	 * Release 5 This method is used to fetch the work items present in the
	 * mentioned Queue since the time period mentioned in the argument.It is
	 * added as part of enhancement number 6508 in build 3.6.0
	 * @param aoVWSession PE Session object
	 * @param asQueueName Queue name
	 * @param aiStuckTime Time since the items stuck in the queue
	 * @return count of stuck items in the specified queue
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public static List<DefaultAssignment> getQueueWorkitemsDailyDigest(VWSession aoVWSession, String asQueueName,
			SqlSession aoMyBatisSession) throws ApplicationException
	{

		VWQueueQuery loVWQueueQuery = null;
		VWQueue loVWQueue = null;
		StringBuffer loQueueFilter = new StringBuffer();
		int liWorkitemsCount = 0;
		List<DefaultAssignment> loDefaultAssignmentList = new ArrayList<DefaultAssignment>();
		DefaultAssignment loDefaultAssignment = new DefaultAssignment();
		try
		{
			//Updated in R6: Fix for Defect 8573
			//Changes made in R6 for FindBug
			loQueueFilter.append("TaskType  in (");
			 Iterator loItr = HHSConstants.FINANCIAL_TASK_PROCESS_ID_MAP.entrySet().iterator();
			 while(loItr.hasNext())
			 {
				 Map.Entry loEntry = (Map.Entry)loItr.next();
				 String loTaskName = (String) loEntry.getKey();
	                if(null != loTaskName && !loTaskName.isEmpty())
	                {
	                	loQueueFilter.append(HHSConstants.STR + loTaskName + HHSConstants.STR);
	                      if (loItr.hasNext())
	                      {
	                    	  loQueueFilter.append(HHSConstants.COMMA);
	                      }
	                }
				 
			 }
			 loQueueFilter.append(")  and taskVisibility=1");
			//Updated in R6: end
			if (aoVWSession == null)
				LOG_OBJECT.Error("ERROR while fetching VWSession Object");

			// fetch the queue object
			loVWQueue = aoVWSession.getQueue(asQueueName);
			if (loVWQueue == null)
				LOG_OBJECT.Error("Error While Fetching Queue");

			int liFlag = loVWQueue.QUERY_READ_LOCKED;
			int liType = VWFetchType.FETCH_TYPE_QUEUE_ELEMENT;
			loVWQueueQuery = loVWQueue.createQuery(null, null, null, liFlag, loQueueFilter.toString(), null, liType);
			liWorkitemsCount = loVWQueueQuery.fetchCount();

			while (loVWQueueQuery.hasNext())
			{
				VWQueueElement loVWQueueElement = (VWQueueElement) loVWQueueQuery.next();
				loDefaultAssignment = new DefaultAssignment();
				loDefaultAssignment.setTaskType(loVWQueueElement.getDataField("TaskType").getStringValue());
				loDefaultAssignment.setAssigneeUserId(loVWQueueElement.getDataField("TaskOwner").getStringValue());
				loDefaultAssignment.setCreatedByUserId((String) DAOUtil.masterDAO(aoMyBatisSession, loVWQueueElement
						.getDataField("TaskOwner").getStringValue(),
						HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER, HHSP8Constants.FETCH_EMAIL_ID,
						HHSP8Constants.JAVA_LANG_STRING));

				loDefaultAssignmentList.add(loDefaultAssignment);

			}

			LOG_OBJECT.Error("Total Workflow Count " + liWorkitemsCount);
		}
		/**
		 * catch any exception thrown from the code and wrap it into application
		 * exception and propagate forward
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetching QueWorkItem from Queue" + asQueueName, aoExp);
			throw new ApplicationException("Error while fetching QueWorkItem from Queue" + asQueueName, aoExp);
		}
		return loDefaultAssignmentList;
	}

	/**
	 * Release 5 This method is used to fetch the work items present in the
	 * mentioned Queue since the time period mentioned in the argument.It is
	 * added as part of enhancement number 6508 in build 3.6.0
	 * @param aoVWSession PE Session object
	 * @param asQueueName Queue name
	 * @param aiStuckTime Time since the items stuck in the queue
	 * @return count of stuck items in the specified queue
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public static List<TaskDetailsBean> getQueueWorkitemsExport(VWSession aoVWSession) throws ApplicationException
	{

		VWQueueQuery loVWQueueQuery = null;
		VWQueue loVWQueue = null;
		StringBuffer loQueueFilter = new StringBuffer();
		int liWorkitemsCount = 0;
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		List<TaskDetailsBean> loTaskDetailsBeanList = new ArrayList<TaskDetailsBean>();
		try
		{
			
			//Updated in R6: Fix for Defect 8573
			//Changes made in R6 for FindBug
			loQueueFilter.append("TaskType  in (");
			 Iterator loItr = HHSConstants.FINANCIAL_TASK_PROCESS_ID_MAP.entrySet().iterator();
			 while(loItr.hasNext())
			 {
				 Map.Entry loEntry = (Map.Entry)loItr.next();
				 String loTaskName = (String) loEntry.getKey();
	                if(null != loTaskName && !loTaskName.isEmpty())
	                {
	                	loQueueFilter.append(HHSConstants.STR + loTaskName + HHSConstants.STR);
	                      if (loItr.hasNext())
	                      {
	                    	  loQueueFilter.append(HHSConstants.COMMA);
	                      }
	                }
				 
			 }
			 loQueueFilter.append(")");
			//Updated in R6: end
			if (aoVWSession == null)
				LOG_OBJECT.Error("ERROR while fetching VWSession Object");

			// fetch the queue object
			loVWQueue = aoVWSession.getQueue("HHSAcceleratorProcessQueue");
			if (loVWQueue == null)
				LOG_OBJECT.Error("Error While Fetching Queue");

			int liFlag = loVWQueue.QUERY_READ_LOCKED;
			int liType = VWFetchType.FETCH_TYPE_QUEUE_ELEMENT;
			loVWQueueQuery = loVWQueue.createQuery(null, null, null, liFlag, loQueueFilter.toString(), null, liType);
			liWorkitemsCount = loVWQueueQuery.fetchCount();

			while (loVWQueueQuery.hasNext())
			{
				VWQueueElement loVWQueueElement = (VWQueueElement) loVWQueueQuery.next();
				loTaskDetailsBean = new TaskDetailsBean();
				loTaskDetailsBean.setAgencyId(loVWQueueElement.getDataField("AgencyID").getStringValue());
			}

			LOG_OBJECT.Error("Total Workflow Count " + liWorkitemsCount);
		}
		/**
		 * catch any exception thrown from the code and wrap it into application
		 * exception and propagate forward
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetching QueWorkItem from Queue", aoExp);
			throw new ApplicationException("Error while fetching QueWorkItem from Queue", aoExp);
		}
		return loTaskDetailsBeanList;
	}

	/**
	 * This method id added in release 3.10.0 enhancement 6576 This method gets
	 * pending approval invoice review tasks which are approved at Level 1
	 * 
	 * @param aoDBSession Filenet DB session
	 * @param asViewName Filenet PE view name
	 * @param asWhereClause Where condition
	 * @return
	 * @throws ApplicationException
	 */
	public List<String> fetchInvoiceNumbersForInvoicesApprovedAtLevel1(SqlSession aoDBSession, String asViewName,
			String asWhereClause) throws ApplicationException
	{
		LOG_OBJECT
				.Debug("Entered P8ProcessServiceForSolicitationFinancials.fetchInvoiceNumbersForInvoicesApprovedAtLevel1() with parameters::"
						+ aoDBSession + " :: " + asViewName + " :: " + asWhereClause);
		String lsInvoiceNum = HHSConstants.EMPTY_STRING;
		ResultSet loResultSet = null;
		Statement loStatement = null;
		java.sql.Connection loConnection = null;
		List<String> loWorkflowIDList = new ArrayList<String>();
		StringBuffer lsQuery = new StringBuffer(HHSConstants.EMPTY_STRING);
		asWhereClause = asWhereClause.replace(HHSConstants.SQUARE_BRAC_BEGIN, HHSConstants.EMPTY_STRING);
		asWhereClause = asWhereClause.replace(HHSConstants.SQUARE_BRAC_END, HHSConstants.EMPTY_STRING);
		if (null == asWhereClause || asWhereClause.isEmpty())
		{
			throw new ApplicationException(
					"Exception in P8ProcessOperationForSolicitationFinancials.fetchInvoiceNumbersForInvoicesApprovedAtLevel1() :Where Condition is not set");
		}
		try
		{
			loConnection = aoDBSession.getConnection();
			loStatement = loConnection.createStatement();
			lsQuery.append("select \"InvoiceNumber\" from ");
			lsQuery.append(asViewName);
			lsQuery.append(" where ");
			lsQuery.append(asWhereClause);
			loResultSet = executeViewQuery(aoDBSession, lsQuery.toString(), loStatement);

			while (loResultSet.next())
			{
				lsInvoiceNum = loResultSet.getString(HHSConstants.PROPERTY_PE_INVOICE_ID);
				loWorkflowIDList.add(lsInvoiceNum);
			}
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT
					.Error("Exception in P8ProcessOperationForSolicitationFinancials.fetchInvoiceNumbersForInvoicesApprovedAtLevel1::",
							aoEx);
			throw aoEx;
		}
		catch (SQLException aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Error in fetchWorkflowIdFromView:: " + loResultSet, aoEx);
			LOG_OBJECT
					.Error("Exception in P8ProcessOperationForSolicitationFinancials.fetchInvoiceNumbersForInvoicesApprovedAtLevel1::",
							loAppex);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Error in fetchWorkflowIdFromView:: " + loResultSet, aoEx);
			LOG_OBJECT
					.Error("Exception in P8ProcessOperationForSolicitationFinancials.fetchInvoiceNumbersForInvoicesApprovedAtLevel1::",
							loAppex);
			throw loAppex;
		}
		finally
		{
			closeFilenetDBResources(loResultSet, loStatement);
		}
		LOG_OBJECT
				.Debug("Exiting::::P8ProcessOperationForSolicitationFinancials.fetchInvoiceNumbersForInvoicesApprovedAtLevel1::",
						loWorkflowIDList.toString());
		return loWorkflowIDList;
	}

	/**
	 * This method id added in release 3.12.0 enhancement 6578 This method gets
	 * pending approval invoice review tasks which are approved at Level 1
	 * 
	 * @param aoDBSession Filenet DB session
	 * @param asViewName Filenet PE view name
	 * @param asWhereClause Where condition
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public HashMap fetchInvoiceNumbersForPaymentsAtLevel1(SqlSession aoDBSession, String asViewName,
			String asWhereClause) throws ApplicationException
	{
		LOG_OBJECT
				.Debug("Entered P8ProcessServiceForSolicitationFinancials.fetchInvoiceNumbersForPaymentsAtLevel1() with parameters::"
						+ aoDBSession + " :: " + asViewName + " :: " + asWhereClause);
		String lsInvoiceNum = HHSConstants.EMPTY_STRING;
		String lsEntityID = HHSConstants.EMPTY_STRING;
		;
		HashMap loInvoiceIDAdvanceIDHashMap = new HashMap();
		ResultSet loResultSet = null;
		Statement loStatement = null;
		java.sql.Connection loConnection = null;
		List<String> loInvoiceIDList = new ArrayList<String>();
		List<String> loBudgetAdvanceIDList = new ArrayList<String>();
		StringBuffer lsQuery = new StringBuffer(HHSConstants.EMPTY_STRING);
		asWhereClause = asWhereClause.replace(HHSConstants.SQUARE_BRAC_BEGIN, HHSConstants.EMPTY_STRING);
		asWhereClause = asWhereClause.replace(HHSConstants.SQUARE_BRAC_END, HHSConstants.EMPTY_STRING);
		if (null == asWhereClause || asWhereClause.isEmpty())
		{
			throw new ApplicationException(
					"Exception in P8ProcessOperationForSolicitationFinancials.fetchInvoiceNumbersForInvoicesApprovedAtLevel1() :Where Condition is not set");
		}
		try
		{
			loConnection = aoDBSession.getConnection();
			loStatement = loConnection.createStatement();
			lsQuery.append("select \"InvoiceNumber\", \"EntityID\" from ");
			lsQuery.append(asViewName);
			lsQuery.append(" where ");
			lsQuery.append(asWhereClause);
			loResultSet = executeViewQuery(aoDBSession, lsQuery.toString(), loStatement);

			while (loResultSet.next())
			{
				lsInvoiceNum = loResultSet.getString(HHSConstants.PROPERTY_PE_INVOICE_ID);
				if (null == lsInvoiceNum)
				{
					lsEntityID = loResultSet.getString(HHSConstants.PROPERTY_PE_ENTITY_ID);
					loBudgetAdvanceIDList.add(lsEntityID);
				}
				else
				{
					loInvoiceIDList.add(lsInvoiceNum);
				}

			}
			loInvoiceIDAdvanceIDHashMap.put(HHSConstants.INVOICE, loInvoiceIDList);
			loInvoiceIDAdvanceIDHashMap.put(HHSConstants.BUDGET_ADVANCE_ID, loBudgetAdvanceIDList);
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT
					.Error("Exception in P8ProcessOperationForSolicitationFinancials.fetchInvoiceNumbersForPaymentsAtLevel1::",
							aoEx);
			throw aoEx;
		}
		catch (SQLException aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Error in fetchWorkflowIdFromView:: " + loResultSet, aoEx);
			LOG_OBJECT
					.Error("Exception in P8ProcessOperationForSolicitationFinancials.fetchInvoiceNumbersForPaymentsAtLevel1::",
							loAppex);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Error in fetchWorkflowIdFromView:: " + loResultSet, aoEx);
			LOG_OBJECT
					.Error("Exception in P8ProcessOperationForSolicitationFinancials.fetchInvoiceNumbersForPaymentsAtLevel1::",
							loAppex);
			throw loAppex;
		}
		finally
		{
			closeFilenetDBResources(loResultSet, loStatement);
		}
		LOG_OBJECT.Debug(
				"Exiting::::P8ProcessOperationForSolicitationFinancials.fetchInvoiceNumbersForPaymentsAtLevel1::",
				loInvoiceIDAdvanceIDHashMap.toString());
		return loInvoiceIDAdvanceIDHashMap;
	}

	// Added for Release 5
	/**
	 * This method is used to fetch agency task report details
	 * @param aoDBSession
	 * @param asViewName
	 * @param aoFilterProp
	 * @param asOrderBy
	 * @param aoSqlSession
	 * @return loTaskList
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<AgencyTaskBean> fetchAgencyTaskExport(SqlSession aoDBSession, String asViewName, HashMap aoFilterProp,
			String asOrderBy, SqlSession aoSqlSession) throws ApplicationException
	{
		List<AgencyTaskBean> loTaskList = new ArrayList<AgencyTaskBean>();
		ResultSet loResultSet = null;
		Statement loStatement = null;
		String lsWhereClause = null;
		java.sql.Connection loConnection = aoDBSession.getConnection();
		StringBuffer lsQuery = new StringBuffer("");
		try
		{
			lsWhereClause = createAgencyTaskFilterExport(aoFilterProp);
			loStatement = loConnection.createStatement();
			createAgencyTaskQueryForExport(asViewName, lsWhereClause, asOrderBy, lsQuery);
			LOG_OBJECT.Debug("Entered P8ProcessOperationForSolicitationFinancials.fetchAgencyTask()");
			loResultSet = executeViewQuery(aoDBSession, lsQuery.toString(), loStatement);
			LOG_OBJECT.Debug("Exited P8ProcessOperationForSolicitationFinancials.fetchAgencyTask()");
			List<AgencyTaskBean> loInvoiceAgencyTaskBeanList = (List<AgencyTaskBean>) DAOUtil.masterDAO(aoSqlSession,
					null, HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
					HHSP8Constants.FETCH_SERVICE_START_END_DATE, null);
			List<AgencyTaskBean> loNameAgencyTaskBeanList = (List<AgencyTaskBean>) DAOUtil.masterDAO(aoSqlSession,
					null, HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
					HHSP8Constants.FETCH_AGENCY_PROVIDER_NAME, null);
			setTaskList(loTaskList, loResultSet, loInvoiceAgencyTaskBeanList, loNameAgencyTaskBeanList);
		}
		catch (SQLException aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in getOpenTaskCount:: " + loResultSet, aoEx);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.getOpenTaskCount()::", loAppex);
			throw loAppex;
		}
		finally
		{
			closeFilenetDBResources(loResultSet, loStatement);
		}
		return loTaskList;
	}
	
	/*
	 * [Start]: Added for defect QC 9555 R 8.7.0
	 * */
	@SuppressWarnings("unchecked")
	public List<AgencyTaskBean> fetchAgencyTaskForExport(SqlSession aoDBSession, String asViewName, HashMap aoFilterProp,
			String asOrderBy, SqlSession aoSqlSession, Map<String,AgencyTaskBean> aoUserNameLst , Map<String,AgencyTaskBean>  aoInvSrtEndDateLst) throws ApplicationException
	{
		List<AgencyTaskBean> loTaskList = new ArrayList<AgencyTaskBean>();
		ResultSet loResultSet = null;
		Statement loStatement = null;
		String lsWhereClause = null;
		java.sql.Connection loConnection = aoDBSession.getConnection();
		StringBuffer lsQuery = new StringBuffer("");
		try
		{
			lsWhereClause = createAgencyTaskFilterExport(aoFilterProp);
			loStatement = loConnection.createStatement();
			createAgencyTaskQueryForExport(asViewName, lsWhereClause, asOrderBy, lsQuery);
			LOG_OBJECT.Debug("Entered P8ProcessOperationForSolicitationFinancials.fetchAgencyTaskForExport():");
			System.out.println("---------------[Entered P8ProcessOperationForSolicitationFinancials.fetchAgencyTaskForExport() ]: " + lsQuery);
			loResultSet = executeViewQuery(aoDBSession, lsQuery.toString(), loStatement);
			System.out.println("---------------[Entered P8ProcessOperationForSolicitationFinancials.fetchAgencyTaskForExport() ]: " );
			LOG_OBJECT.Debug("Exited P8ProcessOperationForSolicitationFinancials.fetchAgencyTaskForExport()");
			setTaskList(loTaskList, loResultSet, aoInvSrtEndDateLst, aoUserNameLst);
		}
		catch (SQLException aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in getOpenTaskCount:: " + loResultSet, aoEx);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.getOpenTaskCount()::", loAppex);
			throw loAppex;
		}
		finally
		{
			closeFilenetDBResources(loResultSet, loStatement);
		}
		return loTaskList;
	}
	
	private void setTaskList(List<AgencyTaskBean> loTaskList, ResultSet loResultSet,
			Map<String,AgencyTaskBean> loInvoiceAgencyTaskBeanMap, Map<String, AgencyTaskBean> loNameAgencyTaskBeanMap)
			throws SQLException, ApplicationException
	{
		AgencyTaskBean loAgencyTaskBean;
		String lsCurrentLevel, lsTaskName, lsAssignedTo;
		while (loResultSet.next())
		{
			loAgencyTaskBean = new AgencyTaskBean();
			loAgencyTaskBean.setWobNumber(checkNullVal(loResultSet.getString(HHSConstants.F_WOB_NUM)));
			loAgencyTaskBean.setAgencyId(checkNullVal(loResultSet.getString(HHSConstants.PROPERTY_PE_AGENCY_ID)));
			loAgencyTaskBean.setAwardEpin(checkNullVal(loResultSet.getString(HHSConstants.PROPERTY_PE_AWARD_EPIN)));
			loAgencyTaskBean.setCtNumber(checkNullVal(loResultSet.getString(HHSConstants.PROPERTY_PE_CT)));
			if (loNameAgencyTaskBeanMap != null)
			{
				String lsSubmittedById = checkNullVal(loResultSet.getString(HHSConstants.PROPERTY_PE_SUBMITTED_BY))  ;
				if( loNameAgencyTaskBeanMap.containsKey( lsSubmittedById ) ){
					loAgencyTaskBean.setSubmittedBy(  loNameAgencyTaskBeanMap.get(lsSubmittedById).getSubmittedBy() );
				}
			}
			loAgencyTaskBean.setSubmittedDate(HHSUtil.getDateFromEpochTime(checkNullVal(loResultSet
					.getString(HHSConstants.PROPERTY_PE_SUBMITTED_DATE))));
			loAgencyTaskBean.setTaskAssignDate(HHSUtil.getDateFromEpochTime(checkNullVal(loResultSet
					.getString(HHSConstants.PROPERTY_PE_ASSIGNED_DATE))));
			lsTaskName = loResultSet.getString(HHSConstants.PROPERTY_PE_TASK_TYPE);
			loAgencyTaskBean.setTaskName(checkNullVal(lsTaskName));
			if (checkNullVal(lsTaskName).equalsIgnoreCase(HHSConstants.TASK_INVOICE_REVIEW) || checkNullVal(lsTaskName).equalsIgnoreCase(HHSConstants.TASK_PAYMENT_REVIEW))
			{
				if (loInvoiceAgencyTaskBeanMap != null)
				{
					/*[Start] R8.7.0 QC9555 Performance Turning */
					String lsInvNum = checkNullVal(loResultSet.getString(HHSConstants.PROPERTY_PE_INVOICE_ID))  ;
					if( loInvoiceAgencyTaskBeanMap.containsKey(lsInvNum) ){
						loAgencyTaskBean.setServiceStartDate( loInvoiceAgencyTaskBeanMap.get(lsInvNum).getServiceStartDate()  );
						loAgencyTaskBean.setServiceEndDate(  loInvoiceAgencyTaskBeanMap.get(lsInvNum).getServiceEndDate() );
					}
					/*[End] R8.7.0 QC9555 Performance Turning */
				}
			}
			loAgencyTaskBean.setTaskName(checkNullVal(lsTaskName));
			lsCurrentLevel = loResultSet.getString(HHSConstants.CURR_LEVEL);
			loAgencyTaskBean.setTaskLevel(checkNullVal(lsCurrentLevel));
			loAgencyTaskBean.setTaskId(checkNullVal(loResultSet.getString(HHSConstants.PROPERTY_PE_TASK_ID)));
			loAgencyTaskBean.setProcurementTitle(checkNullVal(loResultSet
					.getString(HHSConstants.PROPERTY_PE_PROCUREMENT_TITLE)));
			// Start || Changes done for enhancement 6636 for Release 3.12.0
			if (null != loResultSet.getString(HHSConstants.PROPERTY_PE_PROPOSAL_ID)
					&& !loResultSet.getString(HHSConstants.PROPERTY_PE_PROPOSAL_ID).isEmpty()
					&& !loResultSet.getString(HHSConstants.PROPERTY_PE_PROPOSAL_ID).equalsIgnoreCase(HHSConstants.NULL))
			{
				loAgencyTaskBean.setProposalIdExport(loResultSet.getString(HHSConstants.PROPERTY_PE_PROPOSAL_ID)
						.toString());
			}
			// End || Changes done for enhancement 6636 for Release 3.12.0
			loAgencyTaskBean
					.setProviderName(checkNullVal(loResultSet.getString(HHSConstants.PROPERTY_PE_PROVIDER_NAME)));
			loAgencyTaskBean
					.setDateCreated(checkNullVal(loResultSet.getString(HHSConstants.PROPERTY_PE_SUBMITTED_DATE)));
			lsAssignedTo = loResultSet.getString(HHSConstants.PROPERTY_PE_ASSIGNED_TO_NAME);
			if (lsAssignedTo !=null && !lsAssignedTo.contains(HHSConstants.UNASSIGNED_LEVEL) && null != lsCurrentLevel
					&& HHSConstants.FINANCIAL_TASK_PROCESS_ID_MAP.containsKey(lsTaskName))
			{
				// R3Task
				loAgencyTaskBean.setAssignedTo(lsCurrentLevel + HHSConstants.SPACE + HHSConstants.HYPHEN
						+ HHSConstants.SPACE + checkNullVal(lsAssignedTo));
			}
			else
			{
				loAgencyTaskBean.setAssignedTo(checkNullVal(lsAssignedTo));
			}
			loAgencyTaskBean
					.setLastAssigned(checkNullVal(loResultSet.getString(HHSConstants.PROPERTY_PE_ASSIGNED_DATE)));

			loAgencyTaskBean.setStatus(checkNullVal(loResultSet.getString(HHSConstants.TASK_STATUS)));
			loAgencyTaskBean.setEntityId(checkNullVal(loResultSet.getString(P8Constants.PROPERTY_PE_ENTITY_ID)));
			// [Start] Extract InvoiceID from Result set R3.7.0
			loAgencyTaskBean.setInvoiceNumber(checkNullVal(loResultSet.getString(HHSConstants.PROPERTY_PE_INVOICE_ID)));
			loAgencyTaskBean.setProcurementmentEpin(checkNullVal(loResultSet
					.getString(HHSR5Constants.PROPERTY_PE_PROCUREMENT_EPIN)));
			loAgencyTaskBean.setCompetitionPoolTitle(checkNullVal(loResultSet
					.getString(P8Constants.PE_WORKFLOW_COMPETITION_POOL_TITLE)));
			if (null != loResultSet.getString(P8Constants.PE_WORKFLOW_PROPOSAL_ID))
			{
				loAgencyTaskBean.setProposalIdExport(loResultSet.getString(HHSConstants.PROPERTY_PE_PROPOSAL_ID)
						.toString());
			}
			// [End] Extract InvoiceID from Result set R3.7.0
			loTaskList.add(loAgencyTaskBean);
		}
	}

	
	
	/*
	 * [End]: Added for defect QC 9555 R 8.7.0
	 * */


	/**
	 * @param loTaskList
	 * @param loResultSet
	 * @param loInvoiceAgencyTaskBeanList
	 * @param loNameAgencyTaskBeanList
	 * @throws SQLException
	 * @throws ApplicationException
	 */
	private void setTaskList(List<AgencyTaskBean> loTaskList, ResultSet loResultSet,
			List<AgencyTaskBean> loInvoiceAgencyTaskBeanList, List<AgencyTaskBean> loNameAgencyTaskBeanList)
			throws SQLException, ApplicationException
	{
		AgencyTaskBean loAgencyTaskBean;
		String lsCurrentLevel, lsTaskName, lsAssignedTo;
		while (loResultSet.next())
		{
			loAgencyTaskBean = new AgencyTaskBean();
			loAgencyTaskBean.setWobNumber(checkNullVal(loResultSet.getString(HHSConstants.F_WOB_NUM)));
			loAgencyTaskBean.setAgencyId(checkNullVal(loResultSet.getString(HHSConstants.PROPERTY_PE_AGENCY_ID)));
			loAgencyTaskBean.setAwardEpin(checkNullVal(loResultSet.getString(HHSConstants.PROPERTY_PE_AWARD_EPIN)));
			loAgencyTaskBean.setCtNumber(checkNullVal(loResultSet.getString(HHSConstants.PROPERTY_PE_CT)));
			if (loNameAgencyTaskBeanList != null)
			{
				for (AgencyTaskBean loNameAgencyTaskBean : loNameAgencyTaskBeanList)
				{
					if (loNameAgencyTaskBean.getSubmittedById() != null
							&& loNameAgencyTaskBean.getSubmittedById().equalsIgnoreCase(
									checkNullVal(loResultSet.getString(HHSConstants.PROPERTY_PE_SUBMITTED_BY))))
					{
						//R5 Start:commit for Export Task: Emergency build 4.0.1 defectId 8354
						loAgencyTaskBean.setSubmittedBy(checkNullVal(loNameAgencyTaskBean.getSubmittedBy()));
						//R5: End
					}
				}
			}
			loAgencyTaskBean.setSubmittedDate(HHSUtil.getDateFromEpochTime(checkNullVal(loResultSet
					.getString(HHSConstants.PROPERTY_PE_SUBMITTED_DATE))));
			loAgencyTaskBean.setTaskAssignDate(HHSUtil.getDateFromEpochTime(checkNullVal(loResultSet
					.getString(HHSConstants.PROPERTY_PE_ASSIGNED_DATE))));
			lsTaskName = loResultSet.getString(HHSConstants.PROPERTY_PE_TASK_TYPE);
			loAgencyTaskBean.setTaskName(checkNullVal(lsTaskName));
			//R5 Start:commit for Export Task: Emergency build 4.0.1 defectId 8354
			if (checkNullVal(lsTaskName).equalsIgnoreCase(HHSConstants.TASK_INVOICE_REVIEW) || checkNullVal(lsTaskName).equalsIgnoreCase(HHSConstants.TASK_PAYMENT_REVIEW))
			//R5 End:commit for Export Task: Emergency build 4.0.1 defectId 8354
			{
				if (loInvoiceAgencyTaskBeanList != null)
				{
					for (AgencyTaskBean loInvoiceAgencyTaskBean : loInvoiceAgencyTaskBeanList)
					{
						if (loInvoiceAgencyTaskBean.getInvoiceNumber() != null
								&& loInvoiceAgencyTaskBean.getInvoiceNumber().equalsIgnoreCase(
										checkNullVal(loResultSet.getString(HHSConstants.PROPERTY_PE_INVOICE_ID))))
						{
							loAgencyTaskBean.setServiceStartDate(loInvoiceAgencyTaskBean.getServiceStartDate());
							loAgencyTaskBean.setServiceEndDate(loInvoiceAgencyTaskBean.getServiceEndDate());
						}
					}
				}
			}
			loAgencyTaskBean.setTaskName(checkNullVal(lsTaskName));
			lsCurrentLevel = loResultSet.getString(HHSConstants.CURR_LEVEL);
			loAgencyTaskBean.setTaskLevel(checkNullVal(lsCurrentLevel));
			loAgencyTaskBean.setTaskId(checkNullVal(loResultSet.getString(HHSConstants.PROPERTY_PE_TASK_ID)));
			loAgencyTaskBean.setProcurementTitle(checkNullVal(loResultSet
					.getString(HHSConstants.PROPERTY_PE_PROCUREMENT_TITLE)));
			// Start || Changes done for enhancement 6636 for Release 3.12.0
			if (null != loResultSet.getString(HHSConstants.PROPERTY_PE_PROPOSAL_ID)
					&& !loResultSet.getString(HHSConstants.PROPERTY_PE_PROPOSAL_ID).isEmpty()
					&& !loResultSet.getString(HHSConstants.PROPERTY_PE_PROPOSAL_ID).equalsIgnoreCase(HHSConstants.NULL))
			{
				loAgencyTaskBean.setProposalIdExport(loResultSet.getString(HHSConstants.PROPERTY_PE_PROPOSAL_ID)
						.toString());
			}
			// End || Changes done for enhancement 6636 for Release 3.12.0
			loAgencyTaskBean
					.setProviderName(checkNullVal(loResultSet.getString(HHSConstants.PROPERTY_PE_PROVIDER_NAME)));
			loAgencyTaskBean
					.setDateCreated(checkNullVal(loResultSet.getString(HHSConstants.PROPERTY_PE_SUBMITTED_DATE)));
			lsAssignedTo = loResultSet.getString(HHSConstants.PROPERTY_PE_ASSIGNED_TO_NAME);
			if (lsAssignedTo !=null && !lsAssignedTo.contains(HHSConstants.UNASSIGNED_LEVEL) && null != lsCurrentLevel
					&& HHSConstants.FINANCIAL_TASK_PROCESS_ID_MAP.containsKey(lsTaskName))
			{
				// R3Task
				loAgencyTaskBean.setAssignedTo(lsCurrentLevel + HHSConstants.SPACE + HHSConstants.HYPHEN
						+ HHSConstants.SPACE + checkNullVal(lsAssignedTo));
			}
			else
			{
				loAgencyTaskBean.setAssignedTo(checkNullVal(lsAssignedTo));
			}
			loAgencyTaskBean
					.setLastAssigned(checkNullVal(loResultSet.getString(HHSConstants.PROPERTY_PE_ASSIGNED_DATE)));

			loAgencyTaskBean.setStatus(checkNullVal(loResultSet.getString(HHSConstants.TASK_STATUS)));
			loAgencyTaskBean.setEntityId(checkNullVal(loResultSet.getString(P8Constants.PROPERTY_PE_ENTITY_ID)));
			// [Start] Extract InvoiceID from Result set R3.7.0
			loAgencyTaskBean.setInvoiceNumber(checkNullVal(loResultSet.getString(HHSConstants.PROPERTY_PE_INVOICE_ID)));
			loAgencyTaskBean.setProcurementmentEpin(checkNullVal(loResultSet
					.getString(HHSR5Constants.PROPERTY_PE_PROCUREMENT_EPIN)));
			loAgencyTaskBean.setCompetitionPoolTitle(checkNullVal(loResultSet
					.getString(P8Constants.PE_WORKFLOW_COMPETITION_POOL_TITLE)));
			if (null != loResultSet.getString(P8Constants.PE_WORKFLOW_PROPOSAL_ID))
			{
				loAgencyTaskBean.setProposalIdExport(loResultSet.getString(HHSConstants.PROPERTY_PE_PROPOSAL_ID)
						.toString());
			}
			// [End] Extract InvoiceID from Result set R3.7.0
			loTaskList.add(loAgencyTaskBean);
		}
	}

	/**
	 * Changed Method in Release 7 - to remove task which are assigned to 
	 * auto-approver user from export list
	 * This method is used to create agency task filter export details
	 * @param aoHmFilter
	 * @return lsQueueFilter
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public String createAgencyTaskFilterExport(HashMap<String, Object> aoHmFilter) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered P8ProcessOperations.createAgencyTaskFilter()");
		String lsQueueFilter = "";
		StringBuffer loStrBufferQueueFilter = new StringBuffer("");
		try
		{
			if (aoHmFilter != null)
			{
				Iterator loIt = aoHmFilter.keySet().iterator();
				while (loIt.hasNext())
				{
					String lsFilterKey = (String) loIt.next();

					if (null != lsFilterKey && !lsFilterKey.isEmpty()
							&& !lsFilterKey.equalsIgnoreCase(HHSR5Constants.PROPERTY_PE_AGENCY_ID))
					{
						if (!loStrBufferQueueFilter.toString().isEmpty())
						{
							loStrBufferQueueFilter.append(" AND");
						}
						List<String> loFilterList = (List<String>) aoHmFilter.get(lsFilterKey);
						loStrBufferQueueFilter.append("\"");
						loStrBufferQueueFilter.append(lsFilterKey);
						loStrBufferQueueFilter.append("\" IN (");
						Iterator<String> loItr = loFilterList.iterator();
						while (loItr.hasNext())
						{
							loStrBufferQueueFilter.append("'");
							loStrBufferQueueFilter.append(loItr.next());
							loStrBufferQueueFilter.append("'");
							if (loItr.hasNext())
							{
								loStrBufferQueueFilter.append(",");
							}
						}
						loStrBufferQueueFilter.append(")");
					}
					else if (null != lsFilterKey && !lsFilterKey.isEmpty()
							&& lsFilterKey.equalsIgnoreCase(HHSR5Constants.PROPERTY_PE_AGENCY_ID))
					{
						if (!aoHmFilter.get(lsFilterKey).toString().contains(HHSConstants.USER_CITY))
						{
							if (!loStrBufferQueueFilter.toString().isEmpty())
							{
								loStrBufferQueueFilter.append(" AND");
							}
							loStrBufferQueueFilter.append(" lower(\"");
							loStrBufferQueueFilter.append(lsFilterKey);
							loStrBufferQueueFilter.append("\")");
							loStrBufferQueueFilter.append(" =lower('");
							loStrBufferQueueFilter.append((String) aoHmFilter.get(lsFilterKey));
							loStrBufferQueueFilter.append("')");
						}
					}
				}// while ends
			}
			loStrBufferQueueFilter.append(" AND \"taskVisibility\" = 1 ");
			//R7 changes start - Added to filter out task assigned to Auto approver user
			loStrBufferQueueFilter.append(" AND \"TaskOwner\" ");
			loStrBufferQueueFilter.append(" not like '%");
			loStrBufferQueueFilter.append(HHSR5Constants.AUTO_APPROVER_NAME);
			loStrBufferQueueFilter.append("%' ");
			//R7 end
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error While Creating queue Filter::", aoEx);
			loAppex.setContextData(aoHmFilter);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.createAgencyTaskFilter()::", loAppex);
			throw loAppex;
		}
		lsQueueFilter = loStrBufferQueueFilter.toString();
		LOG_OBJECT.Debug("Exited P8ProcessOperations.createAgencyTaskFilter(). Returned lsQueueFilter::"
				+ lsQueueFilter);
		// Return the created queue filter
		return lsQueueFilter;
	}

	/**
	 * This method is used to create agency task query for export
	 * @param asViewName
	 * @param asWhereClause
	 * @param asOrderBy
	 * @param lsQuery
	 */
	private void createAgencyTaskQueryForExport(String asViewName, String asWhereClause, String asOrderBy,
			StringBuffer lsQuery)
	{
		lsQuery.append("select ");
		lsQuery.append("\"F_WobNum\",");
		//R5 Start:commit for Export Task: Emergency build 4.0.1 defectId 8354
		lsQuery.append("decode(\"");
		lsQuery.append("TaskType\",'Service Application',\"TaskName\",'Contact Us',\"TaskName\",'Withdrawal Request - Service Application',\"TaskName\",\"TaskType\") as ");
		//R5:End
		lsQuery.append("\"");
		lsQuery.append(HHSConstants.PROPERTY_PE_TASK_TYPE);
		lsQuery.append("\",");
		lsQuery.append("replace(\"ProcurementTitle\", ',','')  as ");
		lsQuery.append("\"");
		lsQuery.append(HHSConstants.PROPERTY_PE_PROCUREMENT_TITLE);
		lsQuery.append("\",");
		lsQuery.append("nvl(\"ProposalID\",' ') as ");
		lsQuery.append("\"");
		lsQuery.append(HHSConstants.PROPERTY_PE_PROPOSAL_ID);
		lsQuery.append("\",");
		lsQuery.append("replace(\"ProviderName\", ',','') as ");
		lsQuery.append("\"");
		lsQuery.append(HHSConstants.PROPERTY_PE_PROVIDER_NAME);
		lsQuery.append("\",");
		lsQuery.append("\"");
		lsQuery.append(HHSConstants.TASK_STATUS);
		lsQuery.append("\",");
		lsQuery.append("\"");
		lsQuery.append("ProcurementEPin");
		lsQuery.append("\",");
		lsQuery.append("\"");
		lsQuery.append("AwardEPin");
		lsQuery.append("\",");
		lsQuery.append("\"");
		lsQuery.append("TaskAssignDate");
		lsQuery.append("\",");
		lsQuery.append("\"");
		lsQuery.append("LaunchBy");
		lsQuery.append("\",");
		lsQuery.append("\"");
		lsQuery.append("ContractNumber");
		lsQuery.append("\",");
		lsQuery.append("\"");
		lsQuery.append("CompetitionPoolTitle");
		lsQuery.append("\",");
		lsQuery.append("\"");
		lsQuery.append("ProposalID");
		lsQuery.append("\",");
		lsQuery.append("\"");
		lsQuery.append(HHSConstants.PROPERTY_PE_SUBMITTED_DATE);
		lsQuery.append("\",");
		lsQuery.append("TRANSLATE (\"TaskOwnerName\" , 'x'||CHR(10)||CHR(13), 'x') as ");
		lsQuery.append("\"");
		lsQuery.append(HHSConstants.PROPERTY_PE_ASSIGNED_TO_NAME);
		lsQuery.append("\",");
		lsQuery.append("\"");
		lsQuery.append(HHSConstants.PROPERTY_PE_ASSIGNED_DATE);
		lsQuery.append("\",");
		lsQuery.append("\"");
		lsQuery.append(P8Constants.PROPERTY_PE_ENTITY_ID);
		lsQuery.append("\",");
		/*
		 * [Start]Add InvoiceNumber for R3.7.0 enhancement #6361 to get Service
		 * date from portal DB
		 */
		lsQuery.append("\"");
		lsQuery.append(HHSConstants.PROPERTY_PE_INVOICE_ID);
		lsQuery.append("\",");
		/*
		 * [End]Add InvoiceNumber for R3.7.9 to enhancement #6361 get Service
		 * date from portal DB
		 */
		lsQuery.append("\"");
		lsQuery.append(HHSConstants.PROPERTY_PE_TASK_ID);
		lsQuery.append("\",");
		lsQuery.append("\"");
		lsQuery.append(HHSConstants.CURR_LEVEL);
		lsQuery.append("\",");
		lsQuery.append("\"");
		lsQuery.append(HHSConstants.PROPERTY_PE_AGENCY_ID);
		lsQuery.append("\"");
		lsQuery.append("  from ");
		lsQuery.append(asViewName);
		lsQuery.append(" where ");
		lsQuery.append(asWhereClause);
		lsQuery.append(" order by ");
		lsQuery.append(asOrderBy);
	}

	/**
	 * This method is used to check null values
	 * @param aoVal
	 * @return aoVal or "" accordingly
	 */
	private String checkNullVal(String aoVal)
	{
		if (null != aoVal && !aoVal.isEmpty() && !aoVal.equalsIgnoreCase("null"))
		{
			//R5 Start:commit for Export Task: Emergency build 4.0.1 defectId 8354
			return aoVal.replace(HHSConstants.COMMA, HHSConstants.EMPTY_STRING); 
			//R5 End
		}
		else
		{
			return "";
		}
	}

	/**
	 * This method fetches the IsNewLaunchStatus from Filenet PE on the basis of
	 * Condition passed
	 * 
	 * @param aoDBSession Filenet DB session
	 * @param asViewName Filenet PE view name
	 * @param asWhereClause Where condition
	 * @return
	 * @throws ApplicationException
	 */
	public String fetchIsNewLaunchStatusFromView(SqlSession aoDBSession, String asViewName, String asWhereClause)
			throws ApplicationException
	{
		String lsTaskStatus = HHSConstants.EMPTY_STRING;
		ResultSet loResultSet = null;
		Statement loStatement = null;
		java.sql.Connection loConnection = aoDBSession.getConnection();
		StringBuffer lsQuery = new StringBuffer(HHSConstants.EMPTY_STRING);
		if (null == asWhereClause || asWhereClause.isEmpty())
		{
			throw new ApplicationException(
					"Exception in P8ProcessOperationForSolicitationFinancials.fetchIsNewLaunchStatusFromView() :Where Condition is not set");
		}
		try
		{
			loStatement = loConnection.createStatement();
			lsQuery.append("select \"IsNewLaunch\" from ");
			lsQuery.append(asViewName);
			lsQuery.append(" where ");
			lsQuery.append(asWhereClause);
			lsQuery.append(" order by \"F_CreateTime\" desc");
			loResultSet = executeViewQuery(aoDBSession, lsQuery.toString(), loStatement);

			if (loResultSet.next())
			{
				lsTaskStatus = loResultSet.getString("IsNewLaunch");
			}
		}
		catch (SQLException aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in fetchLastTaskStatusFromView:: "
					+ loResultSet, aoEx);
			LOG_OBJECT.Error(
					"Exception in P8ProcessOperationForSolicitationFinancials.fetchIsNewLaunchStatusFromView()::",
					loAppex);
			throw loAppex;
		}
		finally
		{
			closeFilenetDBResources(loResultSet, loStatement);
		}

		return lsTaskStatus;
	}
	// End

}