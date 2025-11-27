package com.nyc.hhs.service.filenetmanager.p8services;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSP8Constants;
import com.nyc.hhs.daomanager.service.FinancialsListService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.AgencyTaskBean;
import com.nyc.hhs.model.DefaultAssignment;
import com.nyc.hhs.model.EPinDetailBean;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8ProcessOperationForSolicitationFinancials;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;

import filenet.vw.api.VWException;
import filenet.vw.api.VWFetchType;
import filenet.vw.api.VWQueue;
import filenet.vw.api.VWQueueElement;
import filenet.vw.api.VWQueueQuery;
import filenet.vw.api.VWSession;
import filenet.vw.api.VWStepElement;

/**
 * This class is used to execute the p8 PE operations and this will be called
 * from controller class and execute the respective operations to complete the
 * transactions
 * 
 */

public class P8ProcessServiceForSolicitationFinancials extends P8HelperServices
{
	private static final LogInfo LOG_OBJECT = new LogInfo(P8ProcessServiceForSolicitationFinancials.class);
	protected static P8ProcessOperationForSolicitationFinancials peOperationHelperForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();

	/**
	 * This function assigns the work item to user asUserName
	 * 
	 * @param aoUserSession a user bean having information about user
	 * @param aoWobNumber an arraylist of work flow numbers
	 * @param asUserId a string value of user name
	 * @param asSessionUserName string
	 * @return boolean
	 * @throws ApplicationException If an application exception occurred
	 */

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public boolean assign(P8UserSession aoUserSession, String aoWobNumber, String asUserId, String asSessionUserName)
			throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put(HHSConstants.FILE_NET_SESSION, aoUserSession.getObjectStoreName());
		loHmReqExceProp.put(HHSConstants.AS_WOB_NBRS, aoWobNumber);
		loHmReqExceProp.put(HHSConstants.AS_USER_NAME, asUserId);
		loHmReqExceProp.put(HHSConstants.AS_SESSION_USER_NAME, asSessionUserName);
		LOG_OBJECT.Debug("Entered P8ProcessServiceForSolicitationFinancials.assign() with parameters::"
				+ loHmReqExceProp.toString());
		if (aoWobNumber.isEmpty())
		{
			ApplicationException loAppex = new ApplicationException("aoWobNumbers is empty");
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("aoWobNumbers is empty::", loAppex);
			throw loAppex;
		}
		if (asUserId == null || asUserId.isEmpty())
		{
			ApplicationException loAppex = new ApplicationException("asUserName is null");
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("asUserName is empty::", loAppex);
			throw loAppex;
		}
		try
		{
			// Fetching PE Session
			VWSession loVWSession = filenetConnection.getPESession(aoUserSession);
			if (loVWSession == null)
			{
				ApplicationException loAppex = new ApplicationException("VWSession is not found");
				loAppex.setContextData(loHmReqExceProp);
				LOG_OBJECT.Error("VWSession is not found::", loAppex);
				throw loAppex;
			}
			peOperationHelperForSolicitationFinancials.assignTask(loVWSession, aoWobNumber, asUserId,
					asSessionUserName, P8Constants.HSS_QUEUE_NAME);
			LOG_OBJECT.Debug("Work items::" + aoWobNumber + " assigned to user::" + asUserId);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while assigning the work items to user: " + asUserId);
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessServiceForSolicitationFinancials.assign()::", aoAppex);
			throw aoAppex;
		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			setMoState("Error while assigning the work item to user: " + asUserId);
			ApplicationException loAppex = new ApplicationException("Error while assigning the work item to user: "
					+ asUserId, aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessServiceForSolicitationFinancials.assign()::", loAppex);
			throw loAppex;
		}
		return true;
	}

	/**
	 * This method is used for launching a workflow given by asWorkflowName and
	 * 
	 * @param aoUserSession a user bean having information about user
	 * @param asWorkflowName a string value of workflow name
	 * @param aoHmReqdWorkflowProperties map containing workflow properties
	 * @return String workflow number
	 * @throws ApplicationException If an application exception occurred
	 */

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public String launchWorkflow(P8UserSession aoUserSession, String asWorkflowName, HashMap aoHmReqdWorkflowProperties)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entering into launchWorkflow::"+ aoHmReqdWorkflowProperties.toString()+ "   WF name::  " +asWorkflowName);
		String lsStartTime = CommonUtil.getCurrentTimeInMilliSec();
		HashMap loHmReqExceProp = new HashMap();
		String lsWFWobNo = null;
		VWSession loVWSession = filenetConnection.getPESession(aoUserSession);
		loHmReqExceProp.put(HHSConstants.FILE_NET_SESSION, aoUserSession.getObjectStoreName());
		loHmReqExceProp.put(HHSConstants.AS_WORK_FLOW_NAME, asWorkflowName);
		loHmReqExceProp.put(HHSConstants.AO_HM_REQ_WK_FLW_PROP, aoHmReqdWorkflowProperties);
		LOG_OBJECT.Debug("Entered P8ProcessServiceForSolicitationFinancials.launchWorkflow() with parameters::"
				+ loHmReqExceProp.toString());
		if (loVWSession == null)
		{
			ApplicationException loAppex = new ApplicationException("VWSession is not found");
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("VWSession is not found::", loAppex);
			throw loAppex;
		}
		try
		{
			LOG_OBJECT.Debug("Launch workflow parameters::"+ aoHmReqdWorkflowProperties.toString());
			lsWFWobNo = peOperationHelperForSolicitationFinancials.launchWorkflow(loVWSession, asWorkflowName,
					aoHmReqdWorkflowProperties, P8Constants.PE_TASK_UNASSIGNED);
			setMoState("Workflow launched::" + lsWFWobNo);
			LOG_OBJECT.Debug("Worklow launched with WobNo::" + lsWFWobNo);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while launching workflow");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessServiceForSolicitationFinancials.launchWorkflow()::", aoAppex);
			throw aoAppex;
		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			setMoState("Error while launching workflow");
			ApplicationException loAppex = new ApplicationException("Error while launching workflow : ", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessServiceForSolicitationFinancials.launchWorkflow()::", loAppex);
			throw loAppex;
		}
		String lsEndTime = CommonUtil.getCurrentTimeInMilliSec();
		float liTimediff = CommonUtil.timeDiff(CommonUtil.getItemDateInMIlisec(lsStartTime),
				CommonUtil.getItemDateInMIlisec(lsEndTime));
		LOG_OBJECT.Debug("P8ProcessServiceForSolicitationFinancials: method: launchWorkflow. Time Taken(seconds):: "
				+ liTimediff);
		LOG_OBJECT.Debug("Exited P8ProcessServiceForSolicitationFinancials.launchWorkflow(). Returned lsWFWobNo::"
				+ lsWFWobNo);
		return lsWFWobNo;
	}

	/**
	 * This method is used for finishing a child task. This method is only
	 * called on finishing all tasks except BR task <li>This method will be
	 * changed as per finishStatus in Release 5 flow</li>
	 * @param aoUserSession a user bean having information about user
	 * @param asWobNo a string value of child work flow no
	 * @param asFinishStatus a string value of child status
	 * @param aoHmFinishWFProperties HmFinish WFProperties
	 * @return boolean
	 * @throws ApplicationException If an application exception occurred
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public boolean finishTask(P8UserSession aoUserSession, String asWobNo, String asFinishStatus,
			HashMap aoHmFinishWFProperties) throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		boolean lbFinishStatus = false;
		loHmReqExceProp.put(HHSConstants.FILE_NET_SESSION, aoUserSession.getObjectStoreName());
		loHmReqExceProp.put(HHSConstants.AS_WOB_NO, asWobNo);
		loHmReqExceProp.put(HHSConstants.AS_FINISH_STATUS, asFinishStatus);
		LOG_OBJECT.Debug("Entered P8ProcessServiceForSolicitationFinancials.finishTask() with parameters::"
				+ loHmReqExceProp.toString());
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
				throwApplicationException(
						"Error in P8ProcessServiceForSolicitationFinancials.finishTask while fetching step element",
						loHmReqExceProp);
			}
			// update the task status only if we are receiving status from the
			// UI
			if (asFinishStatus != null && !asFinishStatus.equals(HHSConstants.EMPTY_STRING))
			{
				loStepElement.setParameterValue(P8Constants.PROPERTY_PE_TASK_STATUS, asFinishStatus, false);
			}
			loStepElement.setParameterValue(P8Constants.PROPERTY_PE_TASK_MODIFIED_DATE, new Date(), false);
			// set the workflow properties required at a time of finish
			if (null != aoHmFinishWFProperties && !aoHmFinishWFProperties.isEmpty())
			{
				loStepElement = peOperationHelper.setWorkFlowProperties(loStepElement, aoHmFinishWFProperties);
			}
			//----
			lbFinishStatus = peOperationHelperForSolicitationFinancials.finishTask(loStepElement);
			//------
			setMoState("task finished" + lbFinishStatus);
			LOG_OBJECT.Debug("task with WOB No ::" + asWobNo + "finished.");
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while  finishing a  task.");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessServiceForSolicitationFinancials.finishTask()::", aoAppex);
			throw aoAppex;
		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			setMoState("Error while  finishing a task.");
			ApplicationException loAppex = new ApplicationException("Error while  finishing a child task.", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessServiceForSolicitationFinancials.finishTask()::", loAppex);
			throw loAppex;
		}
		/*[Start] R7.3.0 QC9017 */
		finally{
		    if (loVWSession != null ){
		        try {
                    loVWSession.logoff();
                } catch (VWException e) {
                    setMoState("Error while logging off session after finishing a task.");
                    ApplicationException loAppex = new ApplicationException("Error while  finishing a child task.", e);
                    LOG_OBJECT.Error("Exception in P8ProcessServiceForSolicitationFinancials.finishTask()::session log off ", loAppex);
                    throw loAppex;                }
		    }
		} 
	    /*[End] R7.3.0 QC9017 */
		
		
		LOG_OBJECT.Debug("Exited P8ProcessServiceForSolicitationFinancials.finishTask(). " + lbFinishStatus);
		return lbFinishStatus;
	}

	/**
	 * This method is used for Setting WF Property in open Task
	 * 
	 * @param aoUserSession a user bean having information about user
	 * @param asWobNo a string value of child work flow no
	 * @param asFinishStatus a string value of child status
	 * @param aoHmWFProperties HmWF Properties
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
		LOG_OBJECT.Debug("Entered P8ProcessServiceForSolicitationFinancials.setWFProperty() with parameters::"
				+ loHmReqExceProp.toString());
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
				throwApplicationException(
						"Error in P8ProcessServiceForSolicitationFinancials.setWFProperty while fetching step element",
						loHmReqExceProp);
			}
			// set the workflow properties required at a time of finish
			if (null != aoHmWFProperties && !aoHmWFProperties.isEmpty())
			{
				loStepElement = peOperationHelper.setWorkFlowProperties(loStepElement, aoHmWFProperties);
			}
			loStepElement.doSave(true);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while setting WF property in task.");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessServiceForSolicitationFinancials.setWFProperty()::", aoAppex);
			throw aoAppex;
		}

		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			setMoState("Error while setting WF property in task.");
			ApplicationException loAppex = new ApplicationException("Error while  setting WF property.", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessServiceForSolicitationFinancials.setWFProperty()::", loAppex);
			throw loAppex;
		}
		LOG_OBJECT.Debug("Exited P8ProcessServiceForSolicitationFinancials.setWFProperty().");
		return true;
	}

	/**
	 * This function is used for fetching the step element based on asWobNumber
	 * 
	 * @param aoVWSession a VWSession Object
	 * @param asWobNumber a string value of work flow number
	 * @param asQueueName a string value of Queue Name
	 * @return VWStepElement a VWStepElement object
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public VWStepElement getStepElementfromWobNo(VWSession aoVWSession, String asWobNumber, String asQueueName)
			throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put(HHSConstants.AS_QUEUE_NAME, asQueueName);
		loHmReqExceProp.put(HHSConstants.AS_WOB_NBR, asWobNumber);
		LOG_OBJECT.Debug("Entered P8ProcessOperations.getStepElementfromWobNo() with parameters::"
				+ loHmReqExceProp.toString());

		VWQueueQuery loVWQueueQuery = null;
		VWQueue loVWQueue = null;
		VWStepElement loStepElement = null;

		String lsQueueFilter = P8Constants.PROPERTY_PE_WOBNUMBER + HHSConstants.STRING_EQUAL + asWobNumber
				+ HHSConstants.STR;

		try
		{
			loVWQueue = aoVWSession.getQueue(asQueueName);
			if (loVWQueue == null)
			{
				throwApplicationException(
						"Exception in P8ProcessOperations.getStepElementfromWobNo() : Error While Fetching Queue",
						loHmReqExceProp);
			}

			int liFlag = VWQueue.QUERY_READ_LOCKED; // read the locked work
													// items as well
			int liType = VWFetchType.FETCH_TYPE_QUEUE_ELEMENT;
			loVWQueueQuery = loVWQueue.createQuery(null, null, null, liFlag, lsQueueFilter, null, liType);
			while (loVWQueueQuery.hasNext())
			{
				VWQueueElement loVWQueueElement = (VWQueueElement) loVWQueueQuery.next();
				loStepElement = loVWQueueElement.fetchStepElement(true, true);
				if (loStepElement == null)
				{
					throwApplicationException(
							"Exception in P8ProcessOperations.getStepElementfromWobNo() : Error While Fetching StepElement from QueueElement fro wob no"
									+ asWobNumber, loHmReqExceProp);
				}
			}
			LOG_OBJECT.Debug("Exited P8ProcessOperations.getStepElementfromWobNo(). Returned the step element");
			return loStepElement;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.getStepElementfromWobNo()::", aoAppex);
			throw aoAppex;
		}
		// handling exception other than ApplicationException
		catch (VWException aoVWEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Error While Fetching StepElement from QueueElement", aoVWEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.getStepElementfromWobNo()::", loAppex);
			throw loAppex;
		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Error While Fetching StepElement from QueueElement", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.getStepElementfromWobNo()::", loAppex);
			throw loAppex;
		}
	}

	/**
	 * This method gets the total count of existing task
	 * 
	 * @param aoUserSession Filenet session object
	 * @param aoHmReqdWFProperties All required properties in HashMap object
	 * @return int
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public int getOpenTaskCount(P8UserSession aoUserSession, HashMap aoHmReqdWFProperties) throws ApplicationException
	{
		int liCount = HHSConstants.INT_ZERO;
		String lsViewName = null;
		String lsWhereClause = null;
		//[Start]R7.12.0 QC9311 Minimize Debug
		LOG_OBJECT.Debug("Entered P8ProcessServiceForSolicitationFinancials.getOpenTaskCount() with parameters::"
				+ aoHmReqdWFProperties.toString());
		//[End]R7.12.0 QC9311 Minimize Debug

		SqlSession loSession = aoUserSession.getFilenetPEDBSession();
		if (loSession == null)
		{
			ApplicationException loAppex = new ApplicationException("Filenet PE DB Session is not found");
			loAppex.setContextData(aoHmReqdWFProperties);
			LOG_OBJECT.Error("Filenet PE DB Session is not found", loAppex);
			throw loAppex;
		}
		try
		{
			lsViewName = getPEViewName(P8Constants.HSS_QUEUE_NAME,
					Integer.valueOf(aoUserSession.getIsolatedRegionNumber()));
			lsWhereClause = createWhereClause(aoHmReqdWFProperties);
			liCount = peOperationHelperForSolicitationFinancials.getOpenTaskCount(loSession, lsViewName, lsWhereClause);

		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			setMoState("Error while getting open task count.");
			ApplicationException loAppex = new ApplicationException("Error while  finishing a child task.", aoEx);
			loAppex.setContextData(aoHmReqdWFProperties);
			LOG_OBJECT.Error("Exception in P8ProcessServiceForSolicitationFinancials..getTaskOpenCount(()::", loAppex);
			throw loAppex;
		}
		LOG_OBJECT.Debug("Exited P8ProcessServiceForSolicitationFinancials.getOpenTaskCount(). " + liCount);
		return liCount;
	}

	/**
	 * This method gets Task Status on based of conditions
	 * 
	 * @param aoUserSession Filenet session object
	 * @param aoHmReqdWFProperties All required properties in HashMap object
	 * @return String
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public String fetchTaskStatusFromView(P8UserSession aoUserSession, HashMap aoHmReqdWFProperties)
			throws ApplicationException
	{
		String lsTaskStatus = HHSConstants.EMPTY_STRING;
		String lsViewName = null;
		String lsWhereClause = null;
		LOG_OBJECT
				.Debug("Entered P8ProcessServiceForSolicitationFinancials.fetchTaskStatusFromView() with parameters::"
						+ aoHmReqdWFProperties.toString());

		SqlSession loSession = aoUserSession.getFilenetPEDBSession();
		if (loSession == null)
		{
			ApplicationException loAppex = new ApplicationException("Filenet PE DB Session is not found");
			loAppex.setContextData(aoHmReqdWFProperties);
			LOG_OBJECT.Error("Filenet PE DB Session is not found", loAppex);
			throw loAppex;
		}

		try
		{
			lsViewName = getPEViewName(P8Constants.HSS_QUEUE_NAME,
					Integer.valueOf(aoUserSession.getIsolatedRegionNumber()));
			lsWhereClause = createWhereClause(aoHmReqdWFProperties);
			lsTaskStatus = peOperationHelperForSolicitationFinancials.fetchTaskStatusFromView(loSession, lsViewName,
					lsWhereClause);

		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			setMoState("Error while fetching task status from view.");
			ApplicationException loAppex = new ApplicationException("Error while  finishing a child task.", aoEx);
			loAppex.setContextData(aoHmReqdWFProperties);
			LOG_OBJECT.Error("Exception in P8ProcessServiceForSolicitationFinancials.fetchTaskStatusFromView()::",
					loAppex);
			throw loAppex;
		}
		LOG_OBJECT.Debug("Exited P8ProcessServiceForSolicitationFinancials.fetchTaskStatusFromView(). " + lsTaskStatus);
		return taskStatusId(lsTaskStatus);
	}

	/**
	 * This method gets Task Status on based of conditions
	 * 
	 * <ul>
	 * <li>This method is added in R4</li>
	 * </ul>
	 * 
	 * @param aoUserSession Filenet session object
	 * @param aoHmReqdWFProperties All required properties in HashMap object
	 * @return String
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public String fetchWorkflowIdFromView(P8UserSession aoUserSession, HashMap aoHmReqdWFProperties)
			throws ApplicationException
	{
		String lsWorkflowId = HHSConstants.EMPTY_STRING;
		String lsViewName = null;
		String lsWhereClause = null;
		LOG_OBJECT
				.Debug("Entered P8ProcessServiceForSolicitationFinancials.fetchWorkflowIdFromView() with parameters::"
						+ aoHmReqdWFProperties.toString());

		SqlSession loSession = aoUserSession.getFilenetPEDBSession();
		if (loSession == null)
		{
			ApplicationException loAppex = new ApplicationException("Filenet PE DB Session is not found");
			loAppex.setContextData(aoHmReqdWFProperties);
			LOG_OBJECT.Error("Filenet PE DB Session is not found", loAppex);
			throw loAppex;
		}

		try
		{
			lsViewName = getPEViewName(P8Constants.HSS_QUEUE_NAME,
					Integer.valueOf(aoUserSession.getIsolatedRegionNumber()));
			lsWhereClause = createWhereClause(aoHmReqdWFProperties);
			lsWorkflowId = peOperationHelperForSolicitationFinancials.fetchWorkflowIdFromView(loSession, lsViewName,
					lsWhereClause);

		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			setMoState("Error while fetching workflow id from view.");
			ApplicationException loAppex = new ApplicationException("Error while  fetchWorkflowIdFromView.", aoEx);
			loAppex.setContextData(aoHmReqdWFProperties);
			LOG_OBJECT.Error("Exception in P8ProcessServiceForSolicitationFinancials.fetchWorkflowIdFromView()::",
					loAppex);
			throw loAppex;
		}
		LOG_OBJECT.Debug("Exited P8ProcessServiceForSolicitationFinancials.fetchWorkflowIdFromView(). " + lsWorkflowId);
		return lsWorkflowId;
	}

	/**
	 * This method gets Task Status on based of conditions
	 * 
	 * @param aoUserSession Filenet session object
	 * @param aoHmReqdWFProperties All required properties in HashMap object
	 * @return List
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public List<String> fetchALLWorkflowIdFromView(P8UserSession aoUserSession, HashMap aoHmReqdWFProperties)
			throws ApplicationException
	{
		String lsViewName = null;
		String lsWhereClause = null;
		List<String> loWorkflowIDList = null;
		LOG_OBJECT
				.Debug("Entered P8ProcessServiceForSolicitationFinancials.fetchALLWorkflowIdFromView() with parameters::"
						+ aoHmReqdWFProperties.toString());

		SqlSession loSession = aoUserSession.getFilenetPEDBSession();
		if (loSession == null)
		{
			ApplicationException loAppex = new ApplicationException("Filenet PE DB Session is not found");
			loAppex.setContextData(aoHmReqdWFProperties);
			LOG_OBJECT.Error("Filenet PE DB Session is not found", loAppex);
			throw loAppex;
		}

		try
		{
			lsViewName = getPEViewName(P8Constants.HSS_QUEUE_NAME,
					Integer.valueOf(aoUserSession.getIsolatedRegionNumber()));
			lsWhereClause = createWhereClause(aoHmReqdWFProperties);
			loWorkflowIDList = peOperationHelperForSolicitationFinancials.fetchALLWorkflowIdFromView(loSession,
					lsViewName, lsWhereClause);

		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			setMoState("Error while fetching workflow ids from view.");
			ApplicationException loAppex = new ApplicationException("Error while  fetchALLWorkflowIdFromView.", aoEx);
			loAppex.setContextData(aoHmReqdWFProperties);
			LOG_OBJECT.Error("Exception in P8ProcessServiceForSolicitationFinancials.fetchALLWorkflowIdFromView()::",
					loAppex);
			throw loAppex;
		}
		return loWorkflowIDList;
	}

	/**
	 * This function is used for creating a queue filter based on the task types
	 * hash-map
	 * 
	 * @param aoHmTaskTypes a map containing task types information
	 * @return a string value of queue filter
	 * @throws ApplicationException If an application exception occurred
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String createWhereClause(HashMap aoHmReqdProps) throws ApplicationException
	{
		String lsQueueWhereFilter = HHSConstants.EMPTY_STRING;
		String lsPropKey = null;
		StringBuffer lsQueueFilterBuffer = new StringBuffer(HHSConstants.EMPTY_STRING);
		if (null != aoHmReqdProps)
		{
			LOG_OBJECT.Debug("Entered P8ProcessServiceForSolicitationFinancials.createWhereClause() with parameters : "
					+ aoHmReqdProps.toString());
			try
			{
				Iterator loItPropNames = aoHmReqdProps.keySet().iterator();
				while (loItPropNames.hasNext())
				{
					lsPropKey = (String) loItPropNames.next();
					if (null != lsPropKey && !lsPropKey.isEmpty())
					{
						if (aoHmReqdProps.get(lsPropKey) instanceof List)
						{
							lsQueueFilterBuffer.append("\"");
							lsQueueFilterBuffer.append(lsPropKey);
							lsQueueFilterBuffer.append("\"");
							lsQueueFilterBuffer.append(" IN(");
							// Start: Changes for Defect 8572 
							if(null != aoHmReqdProps.get(lsPropKey))
							{
								List<String> loPropList = (List<String>)aoHmReqdProps.get(lsPropKey);
								if(loPropList.size() > 0)
								{
									for (Iterator loiterator = loPropList.iterator(); loiterator.hasNext();)
									{
										String loPropName = (String) loiterator.next();
										if(null != loPropName && !loPropName.isEmpty())
										{
											lsQueueFilterBuffer.append(HHSConstants.STR + loPropName + HHSConstants.STR);
											if (loiterator.hasNext())
											{
												lsQueueFilterBuffer.append(HHSConstants.COMMA);
											}
										}
									}
								}
							}
							// End: Changes for Defect 8572 
							lsQueueFilterBuffer.append(")");
						}
						else
						{
							lsQueueFilterBuffer.append("\"");
							lsQueueFilterBuffer.append(lsPropKey);
							lsQueueFilterBuffer.append("\"");
							lsQueueFilterBuffer.append(" = '");
							lsQueueFilterBuffer.append(aoHmReqdProps.get(lsPropKey));
							lsQueueFilterBuffer.append("'");
						}
					}

					if (loItPropNames.hasNext())
					{
						lsQueueFilterBuffer.append(" AND ");
					}
				}
				lsQueueWhereFilter = lsQueueFilterBuffer.toString();
			}
			// handling exception other than ApplicationException
			catch (Exception aoEx)
			{
				setMoState("Error while creating createFilter ");
				ApplicationException loAppex = new ApplicationException(
						"Error while fetching all work items from queue  : ", aoEx);
				loAppex.setContextData(aoHmReqdProps);
				LOG_OBJECT.Error("Exception in P8ProcessServiceForSolicitationFinancials.createWhereClause()", loAppex);
				throw loAppex;
			}
			LOG_OBJECT
					.Debug("Exited P8ProcessServiceForSolicitationFinancials.createWhereClause(). Returned lsQueueFilter :- "
							+ lsQueueWhereFilter);
		}

		return lsQueueWhereFilter;
	}

	/**
	 * This method create Filenet PE DB Queue View name at runtime
	 * 
	 * @param asQueueName Filenet PE queue name
	 * @param aiPERegionId int
	 * @return return filenet view name
	 * @throws ApplicationException
	 */
	public String getPEViewName(String asQueueName, int aiPERegionId) throws ApplicationException
	{
		String lsPEViewName = null;
		StringBuffer loViewNameBuffer = new StringBuffer(HHSConstants.EMPTY_STRING);
		LOG_OBJECT.Debug("Entered P8ProcessServiceForSolicitationFinancials.getPEViewName() with parameters::"
				+ asQueueName);
		try
		{
			loViewNameBuffer.append(HHSConstants.VW_VQ);
			loViewNameBuffer.append(aiPERegionId);
			loViewNameBuffer.append(HHSConstants.UNDERSCORE);
			loViewNameBuffer.append(asQueueName);
			lsPEViewName = loViewNameBuffer.toString();
			if (lsPEViewName.length() > HHSConstants.DB_MAX_VIEW_LEN)
			{
				lsPEViewName = lsPEViewName.substring(HHSConstants.INT_ZERO, HHSConstants.DB_MAX_VIEW_LEN
						- HHSConstants.INT_ONE);
			}
		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			setMoState("Error while getting view name.");
			ApplicationException loAppex = new ApplicationException("Error while  getPEViewName.", aoEx);
			LOG_OBJECT.Error("Exception in P8ProcessServiceForSolicitationFinancials.getPEViewName()::", loAppex);
			throw loAppex;
		}
		LOG_OBJECT.Debug("Exited P8ProcessServiceForSolicitationFinancials.getPEViewName(). " + lsPEViewName);
		return lsPEViewName;
	}

	/**
	 * <ul>
	 * <li>This method is used for finishing a child task. This method is only
	 * called on finishing all tasks except BR task</li>
	 * <li>New Method in R4</li>
	 * </ul>
	 * 
	 * @param aoUserSession a user bean having information about user
	 * @param aoHmWFProperties ao HmWF Properties
	 * @return List
	 * @throws ApplicationException If an application exception occurred Updated
	 * 
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public List<HhsAuditBean> suspendALLFinancialTask(P8UserSession aoUserSession, HashMap aoHmWFProperties)
			throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		HashMap loHMProp = new HashMap();
		List<String> loWorkflowIDList = null;
		String lsCurrentTaskStatus = null;
		String lsContractId = null;
		String lsUserId = null;
		String lsEntityId = null;
		String lsEntityType = null;
		String lsNewStatus = null;
		List<HhsAuditBean> loAuditList = new ArrayList<HhsAuditBean>();
		loHmReqExceProp.put(HHSConstants.FILE_NET_SESSION, aoUserSession.getObjectStoreName());
		LOG_OBJECT
				.Debug("Entered P8ProcessServiceForSolicitationFinancials.suspendALLFinancialTask() with parameters::"
						+ loHmReqExceProp.toString());
		try
		{
			SqlSession loSession = aoUserSession.getFilenetPEDBSession();
			if (loSession == null)
			{
				ApplicationException loAppex = new ApplicationException("Filenet PE DB Session is not found");
				LOG_OBJECT.Error("Filenet PE DB Session is not found", loAppex);
				throw loAppex;
			}
			// Get FileNet PE session
			VWSession loVWSession = filenetConnection.getPESession(aoUserSession);
			if (loVWSession == null)
			{
				ApplicationException loAppex = new ApplicationException("VWSession is not found");
				loAppex.setContextData(loHmReqExceProp);
				LOG_OBJECT.Error("VWSession is not found::", loAppex);
				throw loAppex;
			}
			lsContractId = (String) aoHmWFProperties.get(HHSConstants.PROPERTY_PE_CONTRACT_ID);
			lsUserId = (String) aoHmWFProperties.get(HHSConstants.USER_ID);

			loHMProp.put(HHSConstants.PROPERTY_PE_CONTRACT_ID, lsContractId);
			// Start: Changes for Release 6(Returned Payment)- Suspend COntract
			ArrayList<String> loTaskList = new ArrayList();
			FinancialsListService.getTaskTypesForContract(loTaskList);
			loHMProp.put(HHSConstants.PROPERTY_PE_TASK_TYPE, loTaskList);
			// End: Changes for Release 6(Returned Payment)- Suspend COntract
			loWorkflowIDList = fetchALLWorkflowIdFromView(aoUserSession, loHMProp);
			for (String loWobNum : loWorkflowIDList)
			{
				loHMProp = new HashMap();

				loHMProp.put(HHSConstants.PROPERTY_PE_TASK_TYPE, HHSConstants.EMPTY_STRING);
				loHMProp.put(HHSConstants.PROPERTY_PE_ENTITY_ID, HHSConstants.EMPTY_STRING);
				loHMProp.put(HHSConstants.TASK_STATUS, HHSConstants.EMPTY_STRING);
				loHMProp = (HashMap) (peOperationHelper.getWorkItemDetails(loVWSession, P8Constants.HSS_QUEUE_NAME,
						P8Constants.PROPERTY_PE_WOBNUMBER, loWobNum, loHMProp)).get(loWobNum);
				lsEntityType = (String) loHMProp.get(HHSConstants.PROPERTY_PE_TASK_TYPE);
				lsEntityId = (String) loHMProp.get(HHSConstants.PROPERTY_PE_ENTITY_ID);
				lsCurrentTaskStatus = (String) loHMProp.get(HHSConstants.TASK_STATUS);
				loHMProp = new HashMap();
				loHMProp.put(HHSConstants.LAST_TASK_STATUS, lsCurrentTaskStatus);
				loHMProp.put(HHSConstants.TASK_STATUS, HHSConstants.STATUS_SUSPENDED);
				setWFProperty(aoUserSession, loWobNum, loHMProp);
				lsNewStatus = HHSConstants.STATUS_SUSPENDED;
				addAuditForSuspend(loAuditList, lsEntityId, lsEntityType, lsCurrentTaskStatus, lsNewStatus, lsUserId);
				LOG_OBJECT
						.Debug("Entered P8ProcessServiceForSolicitationFinancials.suspendALLFinancialTask() Suspended WF::"
								+ loWobNum);
			}
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while  suspend tasks.");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessServiceForSolicitationFinancials.suspendALLFinancialTask()::",
					aoAppex);
			throw aoAppex;
		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			setMoState("Error while suspend task.");
			ApplicationException loAppex = new ApplicationException("Error while  suspendALLFinancialTask.", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessServiceForSolicitationFinancials.suspendALLFinancialTask()::",
					loAppex);
			throw loAppex;
		}
		LOG_OBJECT.Debug("Exited P8ProcessServiceForSolicitationFinancials.suspendALLFinancialTask(). ");
		return loAuditList;
	}

	/**
	 * @param aoAuditList Audit List
	 * @param asEntityId Entity Id
	 * @param asEntityType Entity Type
	 * @param asCurrentStatus Current Status
	 * @param asNewStatus New Status
	 * @param asUserId User Id
	 */
	private void addAuditForSuspend(List<HhsAuditBean> aoAuditList, String asEntityId, String asEntityType,
			String asCurrentStatus, String asNewStatus, String asUserId)
	{

		aoAuditList.add(HHSUtil.addAuditDataToChannel(HHSConstants.STATUS_CHANGE, HHSConstants.STATUS_CHANGE,
				ApplicationConstants.STATUS_CHANGED_FROM + HHSConstants.SPACE + HHSConstants.STR + asCurrentStatus
						+ HHSConstants.STR + HHSConstants.TO + HHSConstants.STR + asNewStatus + HHSConstants.STR,
				asEntityType, asEntityId, asUserId, HHSConstants.AGENCY_AUDIT));

	}

	/**
	 * This method is used for finishing a child task. This method is only
	 * called on finishing all tasks except BR task
	 * 
	 * @param aoUserSession a user bean having information about user
	 * @param aoHmWFProperties HmWF Properties
	 * @return List
	 * @throws ApplicationException If an application exception occurred
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public List<HhsAuditBean> unSuspendALLFinancialTask(P8UserSession aoUserSession, HashMap aoHmWFProperties)
			throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		HashMap loHmPrevStatusProp = new HashMap();
		String lsUserId = null;
		String lsContractId = null;
		String lsEntityId = null;
		String lsEntityType = null;
		String lsLastTaskStatus = null;
		List<String> loWorkflowIDList = null;
		List<HhsAuditBean> loAuditList = new ArrayList<HhsAuditBean>();
		loHmReqExceProp.put(HHSConstants.FILE_NET_SESSION, aoUserSession.getObjectStoreName());
		LOG_OBJECT
				.Debug("Entered P8ProcessServiceForSolicitationFinancials.unSuspendALLFinancialTask() with parameters::"
						+ loHmReqExceProp.toString());
		try
		{
			SqlSession loSession = aoUserSession.getFilenetPEDBSession();
			if (loSession == null)
			{
				ApplicationException loAppex = new ApplicationException("Filenet PE DB Session is not found");
				LOG_OBJECT.Error("Filenet PE DB Session is not found", loAppex);
				throw loAppex;
			}
			// Get FileNet PE session
			VWSession loVWSession = filenetConnection.getPESession(aoUserSession);
			if (loVWSession == null)
			{
				ApplicationException loAppex = new ApplicationException("VWSession is not found");
				loAppex.setContextData(loHmReqExceProp);
				LOG_OBJECT.Error("VWSession is not found::", loAppex);
				throw loAppex;
			}
			lsContractId = (String) aoHmWFProperties.get(HHSConstants.PROPERTY_PE_CONTRACT_ID);
			lsUserId = (String) aoHmWFProperties.get(HHSConstants.USER_ID);
			aoHmWFProperties = new HashMap();
			aoHmWFProperties.put(HHSConstants.PROPERTY_PE_CONTRACT_ID, lsContractId);
			aoHmWFProperties.put(HHSConstants.TASK_STATUS, HHSConstants.STATUS_SUSPENDED);
			loWorkflowIDList = fetchALLWorkflowIdFromView(aoUserSession, aoHmWFProperties);
			for (String loWobNum : loWorkflowIDList)
			{
				loHmReqExceProp = new HashMap();
				loHmReqExceProp.put(HHSConstants.PROPERTY_PE_TASK_TYPE, HHSConstants.EMPTY_STRING);
				loHmReqExceProp.put(HHSConstants.PROPERTY_PE_ENTITY_ID, HHSConstants.EMPTY_STRING);
				loHmReqExceProp.put(HHSConstants.LAST_TASK_STATUS, HHSConstants.EMPTY_STRING);
				loHmReqExceProp = (HashMap) (peOperationHelper.getWorkItemDetails(loVWSession,
						P8Constants.HSS_QUEUE_NAME, P8Constants.PROPERTY_PE_WOBNUMBER, loWobNum, loHmReqExceProp))
						.get(loWobNum);
				lsEntityType = (String) loHmReqExceProp.get(HHSConstants.PROPERTY_PE_TASK_TYPE);
				lsEntityId = (String) loHmReqExceProp.get(HHSConstants.PROPERTY_PE_ENTITY_ID);
				lsLastTaskStatus = (String) loHmReqExceProp.get(HHSConstants.LAST_TASK_STATUS);
				if (null == lsLastTaskStatus || lsLastTaskStatus.isEmpty())
				{
					lsLastTaskStatus = HHSConstants.TASK_IN_REVIEW;
				}
				loHmPrevStatusProp.put(HHSConstants.TASK_STATUS, lsLastTaskStatus);
				setWFProperty(aoUserSession, loWobNum, loHmPrevStatusProp);
				addAuditForSuspend(loAuditList, lsEntityId, lsEntityType, HHSConstants.STATUS_SUSPENDED,
						lsLastTaskStatus, lsUserId);
				LOG_OBJECT
						.Debug("Entered P8ProcessServiceForSolicitationFinancials.suspendALLFinancialTask() Suspended WF::"
								+ loWobNum);
			}

		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while unSuspendALLFinancialTask");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessServiceForSolicitationFinancials.suspendALLFinancialTask()::",
					aoAppex);
			throw aoAppex;
		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			setMoState("Error while unsuspend task.");
			ApplicationException loAppex = new ApplicationException("Error while unSuspendALLFinancialTask.", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessServiceForSolicitationFinancials.unSuspendALLFinancialTask()::",
					loAppex);
			throw loAppex;
		}
		LOG_OBJECT.Debug("Exited P8ProcessServiceForSolicitationFinancials.unSuspendALLFinancialTask(). ");
		return loAuditList;
	}

	/**
	 * This method fetches all workflows list from filenet view and put these
	 * all task to unassigned queue of current level
	 * 
	 * @param aoUserSession User Filenet session
	 * @param asUserIdList User Id list
	 * @param asAgencyId agency Id
	 * @param asTaskType Task type
	 * @return true if success
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public boolean setTaskUnassignedForAgencyUsers(P8UserSession aoUserSession, List<String> asUserIdList,
			String asAgencyId, String asTaskType) throws ApplicationException
	{
		HashMap aoHmReqdWFProperties = new HashMap();
		HashMap loWFProperties = new HashMap();
		String lsViewName = null;
		String lsWhereClause = null;
		
		//[Start]R7.12.0 QC9311 Minimize Debug
		/*LOG_OBJECT.Debug("Entered P8ProcessServiceForSolicitationFinancials.setTaskUnassignedForAgencyUsers() with parameters::"
						+ aoHmReqdWFProperties.toString());*/
		LOG_OBJECT.Debug("Entered P8ProcessServiceForSolicitationFinancials.setTaskUnassignedForAgencyUsers() with parameters::");
		//[End]R7.12.0 QC9311 Minimize Debug

		SqlSession loSession = aoUserSession.getFilenetPEDBSession();
		if (loSession == null)
		{
			ApplicationException loAppex = new ApplicationException("Filenet PE DB Session is not found");
			loAppex.setContextData(aoHmReqdWFProperties);
			LOG_OBJECT.Error("Filenet PE DB Session is not found", loAppex);
			throw loAppex;
		}

		aoHmReqdWFProperties.put(HHSConstants.FILE_NET_SESSION, aoUserSession.getObjectStoreName());
		
		//[Start]R7.12.0 QC9311 Minimize Debug
		/*LOG_OBJECT
				.Debug("Entered P8ProcessServiceForSolicitationFinancials.setTaskUnassignedForAgencyUsers() with parameters::"
						+ aoHmReqdWFProperties.toString());*/
		LOG_OBJECT.Debug("Entered P8ProcessServiceForSolicitationFinancials.setTaskUnassignedForAgencyUsers() with parameters::");
		//[End]R7.12.0 QC9311 Minimize Debug
		
		try
		{
			lsViewName = getPEViewName(P8Constants.HSS_QUEUE_NAME,
					Integer.valueOf(aoUserSession.getIsolatedRegionNumber()));
			loWFProperties.put(HHSConstants.PROPERTY_PE_TASK_TYPE, asTaskType);
			loWFProperties.put(HHSConstants.PROPERTY_PE_AGENCY_ID, asAgencyId);
			for (String lsUserID : asUserIdList)
			{
				loWFProperties.put(HHSConstants.PROPERTY_PE_ASSIGNED_TO, lsUserID);
				lsWhereClause = createWhereClause(loWFProperties);
				peOperationHelperForSolicitationFinancials.setTaskUnassignedForAgencyUsers(aoUserSession, lsViewName,
						lsWhereClause);
				loWFProperties.remove(HHSConstants.PROPERTY_PE_ASSIGNED_TO);
			}

		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while  setTaskUnassignedForAgencyUsers");
			aoAppex.setContextData(aoHmReqdWFProperties);
			LOG_OBJECT.Error(
					"Exception in P8ProcessServiceForSolicitationFinancials.setTaskUnassignedForAgencyUsers()::",
					aoAppex);
			throw aoAppex;
		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			setMoState("Error while setTaskUnassignedForAgencyUsers");
			ApplicationException loAppex = new ApplicationException("Error while  setTaskUnassignedForAgencyUsers.",
					aoEx);
			loAppex.setContextData(aoHmReqdWFProperties);
			LOG_OBJECT.Error(
					"Exception in P8ProcessServiceForSolicitationFinancials.setTaskUnassignedForAgencyUsers()::",
					loAppex);
			throw loAppex;
		}
		LOG_OBJECT.Debug("Exited P8ProcessServiceForSolicitationFinancials.setTaskUnassignedForAgencyUsers(). ");
		return true;
	}

	/**
	 * This method return Task Status Id
	 * 
	 * @param asTaskStatus Task Status
	 * @return string
	 */
	private String taskStatusId(String asTaskStatus)
	{
		String lsStatusId = HHSConstants.EMPTY_STRING;
		lsStatusId = HHSConstants.TASK_STATUS_ID_MAP.get(asTaskStatus);
		if (null == lsStatusId)
		{
			return HHSConstants.EMPTY_STRING;
		}

		return lsStatusId;
	}

	/**
	 * This method is used for throwing exceptions
	 * 
	 * @param asMessage A string containing the exception message
	 * @param loHmReqExceProp A hashmap containing all the parameters for the
	 *            calling function which will be added to the exception context
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	private void throwApplicationException(String asMessage, HashMap loHmReqExceProp) throws ApplicationException
	{
		ApplicationException loAppex = new ApplicationException(asMessage);
		loAppex.setContextData(loHmReqExceProp);
		LOG_OBJECT.Error(asMessage + "::", loAppex);
		throw loAppex;
	}

	/**
	 * This method is used to terminate all the evaluation task launched for the
	 * evaluators we are removing or editing
	 * <ul>
	 * <li>Get the list of the evaluators passed in the arguments</li>
	 * <li>Get the list of work flow ids launched for the evaluators</li>
	 * <li>Iterate over the work flow ids list and terminate one by one</li>
	 * <li>If all the work flows terminated successfully return true else retuen
	 * false</li>
	 * </ul>
	 * 
	 * @param aoFilenetSession filenet session bean object
	 * @param aoEvaluationTaskDetailsMap evaluators detail map
	 * @return boolean if all work flows terminated successfully
	 * @throws ApplicationException
	 */
	public boolean cancelEvaluationTask(P8UserSession aoFilenetSession,
			HashMap<String, Object> aoEvaluationTaskDetailsMap) throws ApplicationException
	{
		List<String> loListOfWobNum = null;
		int liPERegionId = HHSConstants.INT_ZERO;
		String lsViewName = null;
		String lsWhereClause = null;
		SqlSession loSession = aoFilenetSession.getFilenetPEDBSession();
		String lsWobNum = null;
		int liTerminatedWorkflowCount = HHSConstants.INT_ZERO;
		boolean lbAllWorkflowTerminated = false;
		try
		{
			if (loSession == null)
			{
				ApplicationException loAppex = new ApplicationException("Filenet PE DB Session is not found");
				loAppex.setContextData(aoEvaluationTaskDetailsMap);
				LOG_OBJECT.Error("Filenet PE DB Session is not found", loAppex);
				throw loAppex;
			}
			// Get FileNet PE session
			VWSession loVWSession = filenetConnection.getPESession(aoFilenetSession);
			if (loVWSession == null)
			{
				ApplicationException loAppex = new ApplicationException("VWSession is not found");
				loAppex.setContextData(aoEvaluationTaskDetailsMap);
				LOG_OBJECT.Error("VWSession is not found", loAppex);
				throw loAppex;
			}
			liPERegionId = loVWSession.getIsolatedRegion();
			lsViewName = getPEViewName(P8Constants.HSS_QUEUE_NAME, liPERegionId);
			lsWhereClause = createWhereClause(aoEvaluationTaskDetailsMap);
			loListOfWobNum = peOperationHelperForSolicitationFinancials.fetchALLWorkflowIdFromView(loSession,
					lsViewName, lsWhereClause);
			if (null != loListOfWobNum && !loListOfWobNum.isEmpty())
			{
				for (Iterator<String> loWobNumIterator = loListOfWobNum.iterator(); loWobNumIterator.hasNext();)
				{
					lsWobNum = loWobNumIterator.next();
					peOperationHelper.terminateWorkItem(loVWSession, lsWobNum, P8Constants.HSS_QUEUE_NAME);
					liTerminatedWorkflowCount++;
				}
			}
			if (liTerminatedWorkflowCount == loListOfWobNum.size())
			{
				lbAllWorkflowTerminated = true;
			}
		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			setMoState("Error while  terminating evaluation task");
			ApplicationException loAppex = new ApplicationException("Error while  terminating evaluation task", aoEx);
			loAppex.setContextData(aoEvaluationTaskDetailsMap);
			LOG_OBJECT.Error("Exception in P8ProcessServiceForSolicitationFinancials.finishTask()::", loAppex);
			throw loAppex;
		}
		LOG_OBJECT.Debug("Exited P8ProcessServiceForSolicitationFinancials.cancelEvaluationTask. ");

		return lbAllWorkflowTerminated;
	}

	/**
	 * This method is used to cancel the award work flow launched for the
	 * procurement id mentioned in parameter map
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * @param aoFilenetSession
	 * @param aoHmFinishWFProperties
	 * @param asAwardAmount Award Amount
	 * @param asRfpReleaseBeforeR4Flag Rfp Release Before R4Flag
	 * @throws ApplicationException
	 */
	public Boolean reActivateAwardWF(P8UserSession aoFilenetSession, HashMap<String, Object> aoHmFinishWFProperties,
			String asAwardAmount, String asRfpReleaseBeforeR4Flag) throws ApplicationException
	{
		String lsViewName = null;
		String lsWhereClause = null;
		SqlSession loSession = null;
		String lsWobNum = null;
		Boolean lbWokflowCancwelled = false;
		HashMap<String, Object> loWorkflowPropMap = new HashMap<String, Object>();
		try
		{
			loSession = aoFilenetSession.getFilenetPEDBSession();
			if (loSession == null)
			{
				ApplicationException loAppex = new ApplicationException("Filenet PE DB Session is not found");
				loAppex.setContextData(aoHmFinishWFProperties);
				LOG_OBJECT.Error("Filenet PE DB Session is not found", loAppex);
				throw loAppex;
			}
			lsViewName = getPEViewName(P8Constants.HSS_QUEUE_NAME,
					Integer.valueOf(aoFilenetSession.getIsolatedRegionNumber()));

			loWorkflowPropMap.put(HHSConstants.F_SUBJECT, P8Constants.PE_AWARD_WORK_FLOW_SUBJECT);
			loWorkflowPropMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID,
					aoHmFinishWFProperties.get(P8Constants.PE_WORKFLOW_PROCUREMENT_ID));
			if (null == asRfpReleaseBeforeR4Flag)
			{
				loWorkflowPropMap.put(P8Constants.EVALUATION_POOL_MAPPING_ID,
						aoHmFinishWFProperties.get(P8Constants.EVALUATION_POOL_MAPPING_ID));
			}
			lsWhereClause = createWhereClause(loWorkflowPropMap);
			lsWobNum = peOperationHelperForSolicitationFinancials.fetchWorkflowIdFromView(loSession, lsViewName,
					lsWhereClause);
			if (null != lsWobNum && !lsWobNum.isEmpty())
			{
				aoHmFinishWFProperties.put(P8Constants.PROPERTY_PE_IS_TASK_VISSIBLE, Boolean.TRUE);
				aoHmFinishWFProperties.put(P8Constants.PE_WORKFLOW_DATE_ASSIGNED, new Date());
				aoHmFinishWFProperties.put(HHSConstants.PROPERTY_PE_SUBMITTED_DATE, new Date());
				aoHmFinishWFProperties.put(P8Constants.PE_WORKFLOW_PROCUREMENT_AWARD_AMOUNT, asAwardAmount);
				peOperationHelperForSolicitationFinancials.setWFProperty(aoFilenetSession, lsWobNum,
						aoHmFinishWFProperties);
				lbWokflowCancwelled = true;
			}
			else
			{
				ApplicationException loAppex = new ApplicationException(
						"No Work flow found for the requested work flow.");
				throw loAppex;
			}
		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			setMoState("Error while  terminating evaluation task");
			ApplicationException loAppex = new ApplicationException("Error while  terminating evaluation task", aoEx);
			loAppex.setContextData(aoHmFinishWFProperties);
			LOG_OBJECT.Error("Exception in P8ProcessServiceForSolicitationFinancials.finishTask()::", loAppex);
			throw loAppex;
		}
		return lbWokflowCancwelled;
	}

	/**
	 * This method is used to launch evaluate score work flow for all the
	 * proposals in accepted status
	 * <ul>
	 * <li>Get the list of all proposals details which are in accepted for
	 * proposal status</li>
	 * <li>while traversing through above lists execute
	 * <code>launchWorkflow</code> of
	 * <code>peOperationHelperForSolicitationFinancials</code> class</li>
	 * <li>if the number of work flows launched is equal to the size of the
	 * proposal list then return true else return false</li>
	 * </ul>
	 * 
	 * @param aoUserSession filenet session bean object
	 * @param aoEvalProposalMap proposal map list
	 * @return boolean if all the work flow launched successfully
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public Boolean launchEvaluateScoreWF(P8UserSession aoUserSession, List<HashMap<String, Object>> aoEvalProposalMap)
			throws ApplicationException
	{
		String lsStartTime = CommonUtil.getCurrentTimeInMilliSec();
		HashMap loHmReqExceProp = new HashMap();
		String lsWFWobNo = null;
		int liWorkflowlaunchCount = HHSConstants.INT_ZERO;
		Boolean lbWorkflowLaunchedSuccess = false;
		String lsWorkFlowName = P8Constants.PE_EVALUATE_PROPOSAL_TASK_NAME;
		VWSession loVWSession = filenetConnection.getPESession(aoUserSession);
		loHmReqExceProp.put(HHSConstants.FILE_NET_SESSION, aoUserSession.getObjectStoreName());
		loHmReqExceProp.put(HHSConstants.AO_HM_REQ_WK_FLW_PROP, aoEvalProposalMap);
		LOG_OBJECT.Debug("Entered P8ProcessServiceForSolicitationFinancials.launchWorkflow() with parameters::"
				+ loHmReqExceProp.toString());
		if (loVWSession == null)
		{
			ApplicationException loAppex = new ApplicationException("VWSession is not found");
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("VWSession is not found::", loAppex);
			throw loAppex;
		}
		try
		{
			for (Iterator<HashMap<String, Object>> loAcceptProposal = aoEvalProposalMap.iterator(); loAcceptProposal
					.hasNext();)
			{
				HashMap<String, Object> loPropertyMap = loAcceptProposal.next();
				lsWFWobNo = peOperationHelperForSolicitationFinancials.launchWorkflow(loVWSession, lsWorkFlowName,
						loPropertyMap, P8Constants.PE_TASK_UNASSIGNED);
				liWorkflowlaunchCount++;
			}
			if (liWorkflowlaunchCount == aoEvalProposalMap.size())
			{
				lbWorkflowLaunchedSuccess = Boolean.TRUE;
				LOG_OBJECT.Debug("Evaluate Score Worklow launched for all proposal and evaluator:::");
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while launching Evaluate Score");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessServiceForSolicitationFinancials.launchEvaluationWF()::", aoAppex);
			throw aoAppex;
		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			setMoState("Error while launching Evaluation workflow");
			ApplicationException loAppex = new ApplicationException("Error while launching workflow : ", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessServiceForSolicitationFinancials.launchEvaluateScoreWF()::",
					loAppex);
			throw loAppex;
		}
		String lsEndTime = CommonUtil.getCurrentTimeInMilliSec();
		float liTimediff = CommonUtil.timeDiff(CommonUtil.getItemDateInMIlisec(lsStartTime),
				CommonUtil.getItemDateInMIlisec(lsEndTime));
		LOG_OBJECT
				.Debug("P8ProcessServiceForSolicitationFinancials: method: launchEvaluateScoreWF(). Time Taken(seconds):: "
						+ liTimediff);
		LOG_OBJECT
				.Debug("Exited P8ProcessServiceForSolicitationFinancials.launchEvaluateScoreWF(). Returned lsWFWobNo::"
						+ lsWFWobNo);
		return lbWorkflowLaunchedSuccess;
	}

	/**
	 * Below method is used to re launch the evaluate proposal task
	 * <ul>
	 * <li>Get the proposal status and proposal id from argument</li>
	 * <li>If the proposal status is returned for revision then set the task
	 * visibility property of work flow to true</li>
	 * <li>Updated Method in R4
	 * <li>
	 * </ul>
	 * 
	 * @param aoUserSession filenet session bean object
	 * @param aoProposalDetailsMap Proposal Details Map
	 * @param aoUpdateStatus Update Status
	 * @param asProposalId proposal id
	 * @return boolean if the relaunched successfully
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Boolean relaunchAcceptProposalTask(P8UserSession aoUserSession, Map<String, String> aoProposalDetailsMap,
			String asProposalId, Boolean aoUpdateStatus) throws ApplicationException
	{
		Boolean lbWorkflowReLaunchedSuccess = false;
		HashMap loHmReqExceProp = new HashMap();
		String lsProposalStatus = null;
		String lsEvaluateProposalTaskName = null;
		HashMap<String, Object> loWorkFlowPropMapToFetchWobNum = new HashMap<String, Object>();
		HashMap<String, Object> loWorkFlowPropMap = new HashMap<String, Object>();
		String lsViewName = null;
		String lsWhereClause = null;
		String lsWobNum = null;
		SqlSession loSession = null;
		try
		{
			if (null != aoUpdateStatus && aoUpdateStatus)
			{
				loHmReqExceProp.put(HHSConstants.FILE_NET_SESSION, aoUserSession.getObjectStoreName());
				loHmReqExceProp.put(HHSConstants.PROPOSAL_STATUS, aoProposalDetailsMap.toString());
				loHmReqExceProp.put(HHSConstants.PROPOSAL_ID, asProposalId);
				if (null != aoProposalDetailsMap)
				{
					lsProposalStatus = String.valueOf(aoProposalDetailsMap.get(HHSConstants.PROPOSAL_STATUS_ID));
				}
				lsEvaluateProposalTaskName = P8Constants.PE_ACCEPT_PROPOSAL_TASK_NAME;
				loWorkFlowPropMapToFetchWobNum.put(P8Constants.PE_WORKFLOW_PROPOSAL_ID, asProposalId);
				loWorkFlowPropMapToFetchWobNum.put(P8Constants.PROPERTY_PE_TASK_NAME, lsEvaluateProposalTaskName);
				if (lsProposalStatus.equalsIgnoreCase(PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_RETURNED_FOR_REVISION))
						|| lsProposalStatus.equalsIgnoreCase(PropertyLoader.getProperty(
								HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.PROPOSAL_ACCEPTED_FOR_EVALUATION_KEY))
						|| lsProposalStatus.equalsIgnoreCase(PropertyLoader.getProperty(
								HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_NON_RESPONSIVE)))
				{
					// get database session
					loSession = aoUserSession.getFilenetPEDBSession();
					if (loSession == null)
					{
						ApplicationException loAppex = new ApplicationException("Filenet PE DB Session is not found");
						loAppex.setContextData(loHmReqExceProp);
						LOG_OBJECT.Error("Filenet PE DB Session is not found", loAppex);
						throw loAppex;
					}
					lsViewName = getPEViewName(P8Constants.HSS_QUEUE_NAME,
							Integer.valueOf(aoUserSession.getIsolatedRegionNumber()));
					lsWhereClause = createWhereClause(loWorkFlowPropMapToFetchWobNum);
					lsWobNum = peOperationHelperForSolicitationFinancials.fetchWorkflowIdFromView(loSession,
							lsViewName, lsWhereClause);
					if (null != lsWobNum && !lsWobNum.isEmpty())
					{
						loWorkFlowPropMap.put(P8Constants.PROPERTY_PE_IS_TASK_VISSIBLE, Boolean.TRUE);
						loWorkFlowPropMap.put(P8Constants.PE_WORKFLOW_DATE_ASSIGNED, new Date());
						loWorkFlowPropMap.put(HHSConstants.PROPERTY_PE_SUBMITTED_DATE, new Date());
						if (null != aoProposalDetailsMap.get(HHSConstants.COMP_POOL_TITLE))
						{
							loWorkFlowPropMap.put(P8Constants.PE_WORKFLOW_COMPETITION_POOL_TITLE,
									aoProposalDetailsMap.get(HHSConstants.COMP_POOL_TITLE));
						}
						peOperationHelperForSolicitationFinancials.setWFProperty(aoUserSession, lsWobNum,
								loWorkFlowPropMap);
						lbWorkflowReLaunchedSuccess = Boolean.TRUE;
					}
					else
					{
						ApplicationException loAppex = new ApplicationException(
								"work flow id not found for requested proposal id:" + asProposalId);
						loAppex.setContextData(loWorkFlowPropMapToFetchWobNum);
						LOG_OBJECT.Error("work flow id not found for requested proposal id:", loAppex);
						throw loAppex;
					}
				}
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while launching Evaluate Score");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessServiceForSolicitationFinancials.launchEvaluationWF()::", aoAppex);
			throw aoAppex;
		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			setMoState("Error while launching Evaluation workflow");
			ApplicationException loAppex = new ApplicationException("Error while launching workflow : ", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessServiceForSolicitationFinancials.launchEvaluateScoreWF()::",
					loAppex);
			throw loAppex;
		}
		return lbWorkflowReLaunchedSuccess;
	}

	/**
	 * 
	 * This method is used to relaunch the evaluate proposal task for all the
	 * task for which result is returned for amendment
	 * <ul>
	 * <li>Get the list of the returned evaluation id</li>
	 * <li>For all evaluation id get the work flow id</li>
	 * <li>For all work flow ids set task visibility true</li>
	 * </ul>
	 * 
	 * @param aoUserSession filenet session
	 * @param aoEvaluationId
	 * @param asProposalId
	 * @param asWobNum
	 * @param aoWorkFlowPropMap
	 * @param asFinishStatus
	 * @return boolean
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Boolean finishReviewScoreWF(P8UserSession aoUserSession, List<String> aoEvaluationId, String asProposalId,
			String asWobNum, HashMap<String, Object> aoWorkFlowPropMap, String asFinishStatus)
			throws ApplicationException
	{
		Boolean lbWorkflowReLaunchedSuccess = false;
		HashMap loHmReqExceProp = new HashMap();
		HashMap<String, Object> loWorkFlowPropMapToFetchWobNum = new HashMap<String, Object>();
		List<String> loWobNumList = new ArrayList<String>();
		String[] loWobNumberArr = null;
		String lsViewName = null;
		String lsWhereClause = null;
        SqlSession loSession = null;
/* [Start] R7.3.0 QC9017
 		String lsWobNum = null;
		String lsEvaluationId = null;
*///[End] R7.3.0 QC9017		
		try
		{
			loHmReqExceProp.put(HHSConstants.FILE_NET_SESSION, aoUserSession.getObjectStoreName());
			loHmReqExceProp.put(HHSConstants.EVAL_IDS, aoEvaluationId.toString());
			loHmReqExceProp.put(HHSConstants.PROPOSAL_ID, asProposalId);
			
			if (null != aoEvaluationId  )
			{
				// get database session
				loSession = aoUserSession.getFilenetPEDBSession();
				if (loSession == null)
				{
					ApplicationException loAppex = new ApplicationException("Filenet PE DB Session is not found");
					loAppex.setContextData(loHmReqExceProp);
					LOG_OBJECT.Error("Filenet PE DB Session is not found", loAppex);
					throw loAppex;
				}
				// Get FileNet PE session
				lsViewName = getPEViewName(P8Constants.HSS_QUEUE_NAME,
						Integer.valueOf(aoUserSession.getIsolatedRegionNumber()));
				
/* [Start] R7.3.0 QC 9017   */
                loWorkFlowPropMapToFetchWobNum.put(P8Constants.PE_WORKFLOW_PROPOSAL_ID, asProposalId);
                loWorkFlowPropMapToFetchWobNum.put(P8Constants.PE_EVALUATE_PROPOSAL_EVALUATION_ID,  aoEvaluationId);
                if (null != aoEvaluationId && aoEvaluationId.size()  > 0 ) 
                {
                    lsWhereClause = createWhereClause(loWorkFlowPropMapToFetchWobNum);
                    loWobNumList = peOperationHelperForSolicitationFinancials.fetchALLWorkflowIdFromView(loSession,lsViewName, lsWhereClause);
                    
                }

/*				for (int liCount = HHSConstants.INT_ZERO; liCount < aoEvaluationId.size(); liCount++)
				{
					lsEvaluationId = aoEvaluationId.get(liCount);
					if (null != lsEvaluationId)
					{
						loWorkFlowPropMapToFetchWobNum.put(P8Constants.PE_WORKFLOW_PROPOSAL_ID, asProposalId);
						loWorkFlowPropMapToFetchWobNum.put(P8Constants.PE_EVALUATE_PROPOSAL_EVALUATION_ID,
								lsEvaluationId);
						lsWhereClause = createWhereClause(loWorkFlowPropMapToFetchWobNum);
						lsWobNum = peOperationHelperForSolicitationFinancials.fetchWorkflowIdFromView(loSession,lsViewName, lsWhereClause);

						if (null != lsWobNum && !lsWobNum.isEmpty())
						{
							loWobNumList.add(lsWobNum);
						}
					}
				}
*/
/* [End] R7.3.0 QC 9017   */
				if (null != loWobNumList && loWobNumList.size() > HHSConstants.INT_ZERO)
				{
					loWobNumberArr = new String[loWobNumList.size()];
					loWobNumberArr = loWobNumList.toArray(loWobNumberArr);
					aoWorkFlowPropMap.put(HHSConstants.EVALUATE_WORKFLOW_NUMBERS, loWobNumberArr);
				}
				
				finishTask(aoUserSession, asWobNum, asFinishStatus, aoWorkFlowPropMap);
			}
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while launching Evaluate Score");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessServiceForSolicitationFinancials.reLaunchEvaluateProposalWF()::",
					aoAppex);
			throw aoAppex;
		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			setMoState("Error while launching Evaluation workflow");
			ApplicationException loAppex = new ApplicationException("Error while launching workflow : ", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessServiceForSolicitationFinancials.reLaunchEvaluateProposalWF()::",
					loAppex);
			throw loAppex;
		}
		return lbWorkflowReLaunchedSuccess;
	}

	/**
	 * This method is used to set task visibility flag true for review task
	 * requested for score amendment
	 * <ul>
	 * <li>Get the procurement id and proposal id from request parameters</li>
	 * <li>Get the wob number for the review score launched for the requested
	 * procurement id and proposal id</li>
	 * <li>Set visibility flag true for the fetched wob number</li>
	 * </ul>
	 * 
	 * @param aoUserSession filenet session bean object
	 * @param asProcurementId procurement id
	 * @param asProposalId proposal id
	 * @return boolean
	 * @throws ApplicationException
	 */
	public Boolean requestScoreAmendement(P8UserSession aoUserSession, String asProcurementId, String asProposalId)
			throws ApplicationException
	{
		HashMap<String, Object> loHmReqExceProp = new HashMap<String, Object>();
		HashMap<String, Object> loWorkFlowPropMapToFetchWobNum = new HashMap<String, Object>();
		HashMap<String, Object> loWorkFlowPropToSet = new HashMap<String, Object>();
		Boolean loTaskVisibiltyReset = Boolean.FALSE;
		SqlSession loSession = null;
		String lsViewName = null;
		String lsWhereClause = null;
		String lsWobNum = null;
		String lsWorkFlowName = null;
		try
		{
			loHmReqExceProp.put(HHSConstants.PROCUMENET_ID, asProcurementId);
			loHmReqExceProp.put(HHSConstants.PROPOSAL_ID, asProposalId);
			lsWorkFlowName = P8Constants.TASK_REVIEW_SCORES;
			loWorkFlowPropMapToFetchWobNum.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, asProcurementId);
			loWorkFlowPropMapToFetchWobNum.put(P8Constants.PE_WORKFLOW_PROPOSAL_ID, asProposalId);
			loWorkFlowPropMapToFetchWobNum.put(P8Constants.PROPERTY_PE_TASK_NAME, lsWorkFlowName);
			// get database session
			loSession = aoUserSession.getFilenetPEDBSession();
			if (loSession == null)
			{
				ApplicationException loAppex = new ApplicationException("Filenet PE DB Session is not found");
				loAppex.setContextData(loHmReqExceProp);
				LOG_OBJECT.Error("Filenet PE DB Session is not found", loAppex);
				throw loAppex;
			}
			lsViewName = getPEViewName(P8Constants.HSS_QUEUE_NAME,
					Integer.valueOf(aoUserSession.getIsolatedRegionNumber()));
			lsWhereClause = createWhereClause(loWorkFlowPropMapToFetchWobNum);
			lsWobNum = peOperationHelperForSolicitationFinancials.fetchWorkflowIdFromView(loSession, lsViewName,
					lsWhereClause);
			if (null != lsWobNum && !lsWobNum.isEmpty())
			{
				loWorkFlowPropToSet.put(P8Constants.PROPERTY_PE_IS_TASK_VISSIBLE, Boolean.TRUE);
				loWorkFlowPropToSet.put(P8Constants.PE_WORKFLOW_DATE_ASSIGNED, new Date());
				loWorkFlowPropToSet.put(P8Constants.PROPERTY_PE_TASK_STATUS, HHSConstants.TASK_IN_REVIEW);
				loWorkFlowPropToSet.put(HHSConstants.PROPERTY_PE_SUBMITTED_DATE, new Date());
				peOperationHelperForSolicitationFinancials.setWFProperty(aoUserSession, lsWobNum, loWorkFlowPropToSet);
				loTaskVisibiltyReset = Boolean.TRUE;
			}
		}
		// catch application exception and forward it to the service layer
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while launching Evaluate Score");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessServiceForSolicitationFinancials.reLaunchEvaluateProposalWF()::",
					aoAppex);
			throw aoAppex;
		}
		// catch the exception thrown wrap it into application exception and
		// forward it to the service layer
		catch (Exception aoEx)
		{
			setMoState("Error while launching Evaluation workflow");
			ApplicationException loAppex = new ApplicationException("Error while launching workflow : ", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessServiceForSolicitationFinancials.reLaunchEvaluateProposalWF()::",
					loAppex);
			throw loAppex;
		}
		return loTaskVisibiltyReset;
	}

	/**
	 * This method is used to finish the Evaluate proposal work flow
	 * <ul>
	 * <li>If the task status is equal to score returned get the review score
	 * work flow id for the proposal</li>
	 * <li>Set the work flow id in workflowid attribute</li>
	 * <li>Finish the task and return true if the task finished success fully</li>
	 * </ul>
	 * 
	 * @param aoUserSession filenet user session bean
	 * @param aoTaskDetailsBean task deails bean object
	 * @return lbTaskVisibiltyReset flag
	 * @throws ApplicationException
	 */
	public Boolean finishEvaluateProposalWF(P8UserSession aoUserSession, TaskDetailsBean aoTaskDetailsBean)
			throws ApplicationException
	{
		HashMap<String, Object> loHmReqExceProp = new HashMap<String, Object>();
		HashMap<String, Object> loWorkFlowPropToSet = new HashMap<String, Object>();
		Boolean lbTaskVisibiltyReset = Boolean.FALSE;
		SqlSession loSession = null;
		try
		{
			loHmReqExceProp.put(HHSConstants.WORK_FLOW_ID, aoTaskDetailsBean.getWorkFlowId());
			loHmReqExceProp.put(HHSConstants.PROPOSAL_ID, aoTaskDetailsBean.getProposalId());
			if (null != aoTaskDetailsBean.getTaskStatus()
					&& P8Constants.PE_WORKFLOW_SOCRE_RETURNED_STATUS
							.equalsIgnoreCase(aoTaskDetailsBean.getTaskStatus()))
			{
				loSession = aoUserSession.getFilenetPEDBSession();
				if (loSession == null)
				{
					ApplicationException loAppex = new ApplicationException("Filenet PE DB Session is not found");
					loAppex.setContextData(loHmReqExceProp);
					LOG_OBJECT.Error("Filenet PE DB Session is not found", loAppex);
					throw loAppex;
				}
				// Get FileNet PE session
				VWSession loVWSession = filenetConnection.getPESession(aoUserSession);
				if (loVWSession == null)
				{
					ApplicationException loAppex = new ApplicationException("VWSession is not found");
					loAppex.setContextData(loHmReqExceProp);
					LOG_OBJECT.Error("VWSession is not found", loAppex);
					throw loAppex;
				}
				loWorkFlowPropToSet.put(P8Constants.PE_WORKFLOW_TASK_VISIBILITY, Boolean.FALSE);
			}
			finishTask(aoUserSession, aoTaskDetailsBean.getWorkFlowId(), HHSConstants.TASK_FINISHED,
					loWorkFlowPropToSet);
		}
		// catch application exception and forward it to the service layer
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while launching Evaluate Score");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessServiceForSolicitationFinancials.reLaunchEvaluateProposalWF()::",
					aoAppex);
			throw aoAppex;
		}
		// catch the exception thrown wrap it into application exception and
		// forward it to the service layer
		catch (Exception aoEx)
		{
			setMoState("Error while launching Evaluation workflow");
			ApplicationException loAppex = new ApplicationException("Error while launching workflow : ", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessServiceForSolicitationFinancials.reLaunchEvaluateProposalWF()::",
					loAppex);
			throw loAppex;
		}
		return lbTaskVisibiltyReset;
	}

	/**
	 * This method will get workflow Id for accept proposal task having status
	 * non-responsive This method is used to get the work flow ids for the
	 * Accept proposal work flow marked as non responsive
	 * <ul>
	 * <li>Fetch the work flow id for the task Accept Proposal Task with the
	 * required paramters</li>
	 * <li>Return work flow id back to transaction layer</li>
	 * </ul>
	 * 
	 * @param aoUserSession filenet session bean object
	 * @param asProcurementId procurement id
	 * @param asProposalId proposal id
	 * @return String work flow id
	 * @throws ApplicationException if any exception occurs
	 */
	public String getNonResponsiveAcceptProposalWobNum(P8UserSession aoUserSession, String asProcurementId,
			String asProposalId) throws ApplicationException
	{
		HashMap<String, Object> loHmReqExceProp = new HashMap<String, Object>();
		HashMap<String, Object> loWorkFlowPropToFetchTaskStatus = new HashMap<String, Object>();
		SqlSession loSession = null;
		String lsViewName = null;
		String lsWhereClause = null;
		String lsAcceptProposalWobNum = null;
		String lsWorkFlowName = null;
		try
		{
			loHmReqExceProp.put(HHSConstants.PROPOSAL_ID, asProposalId);
			lsWorkFlowName = P8Constants.PE_ACCEPT_PROPOSAL_TASK_NAME;
			loWorkFlowPropToFetchTaskStatus.clear();
			loWorkFlowPropToFetchTaskStatus.put(P8Constants.PE_WORKFLOW_PROPOSAL_ID, asProposalId);
			loWorkFlowPropToFetchTaskStatus.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, asProcurementId);
			loWorkFlowPropToFetchTaskStatus.put(P8Constants.PROPERTY_PE_TASK_NAME, lsWorkFlowName);
			loSession = aoUserSession.getFilenetPEDBSession();
			if (loSession == null)
			{
				ApplicationException loAppex = new ApplicationException("Filenet PE DB Session is not found");
				loAppex.setContextData(loHmReqExceProp);
				LOG_OBJECT.Error("Filenet PE DB Session is not found", loAppex);
				throw loAppex;
			}
			lsViewName = getPEViewName(P8Constants.HSS_QUEUE_NAME,
					Integer.valueOf(aoUserSession.getIsolatedRegionNumber()));
			lsWhereClause = createWhereClause(loWorkFlowPropToFetchTaskStatus);
			lsAcceptProposalWobNum = peOperationHelperForSolicitationFinancials.fetchWorkflowIdFromView(loSession,
					lsViewName, lsWhereClause);
		}
		// catch application exception and forward it to the service layer
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while fetching work flow id");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error(
					"Exception in P8ProcessServiceForSolicitationFinancials.getNonResponsiveAcceptProposalWobNum()::",
					aoAppex);
			throw aoAppex;
		}
		// catch the exception thrown wrap it into application exception and
		// forward it to the service layer
		catch (Exception aoEx)
		{
			setMoState("Error while fetching work flow id");
			ApplicationException loAppex = new ApplicationException("Error while launching workflow : ", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error(
					"Exception in P8ProcessServiceForSolicitationFinancials.getNonResponsiveAcceptProposalWobNum()::",
					loAppex);
			throw loAppex;
		}
		return lsAcceptProposalWobNum;
	}

	/**
	 * This method will get tasks count for agency homepages when refresh button
	 * is clicked from task portlets
	 * 
	 * @param aoUserSession P8UserSession object
	 * @param aoHmReqdWFProperties a hashmap containing task types
	 * @param aoIncludeNotFlag Include Not Flag
	 * @param asAgencyId Agency Id
	 * @return a hashmap containing counts against task types
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public HashMap<String, Integer> getHomePageTaskCount(P8UserSession aoUserSession,
			HashMap<String, Integer> aoHmReqdWFProperties, String asTaskOwnerName, Boolean aoIncludeNotFlag,
			String asAgencyId) throws ApplicationException
	{
		HashMap<String, Integer> loTaskCountMap = null;
		String lsViewName = null;
		LOG_OBJECT.Debug("Entered P8ProcessServiceForSolicitationFinancials.getHomePageTaskCount() with parameters::"
				+ aoHmReqdWFProperties.toString());

		SqlSession loSession = aoUserSession.getFilenetPEDBSession();
		if (loSession == null)
		{
			ApplicationException loAppex = new ApplicationException("Filenet PE DB Session is not found");
			loAppex.setContextData(aoHmReqdWFProperties);
			LOG_OBJECT.Error("Filenet PE DB Session is not found", loAppex);
			throw loAppex;
		}
		try
		{
			lsViewName = getPEViewName(P8Constants.HSS_QUEUE_NAME,
					Integer.valueOf(aoUserSession.getIsolatedRegionNumber()));
			loTaskCountMap = peOperationHelperForSolicitationFinancials.getHomePageTaskCount(loSession, lsViewName,
					aoHmReqdWFProperties, asTaskOwnerName, aoIncludeNotFlag, asAgencyId);

		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (Exception aoEx)
		{
			setMoState("Error while getting home page count.");
			ApplicationException loAppex = new ApplicationException("Error while getHomePageTaskCount.", aoEx);
			loAppex.setContextData(aoHmReqdWFProperties);
			LOG_OBJECT
					.Error("Exception in P8ProcessServiceForSolicitationFinancials.getHomePageTaskCount()::", loAppex);
			throw loAppex;
		}
		//[Start]R7.12.0 QC9311 Minimize Debug
		LOG_OBJECT.Debug("Exited P8ProcessServiceForSolicitationFinancials.getHomePageTaskCount(). "
				+ aoHmReqdWFProperties);
		//[End]R7.12.0 QC9311 Minimize Debug
		return loTaskCountMap;
	}

	/**
	 * This method fetches all agency task list to show in task in box and
	 * management
	 * 
	 * @param aoUserSession Filenet session
	 * @param aoAgencyTaskQueryParam Agency task bean object
	 * @return return list of task
	 * @throws ApplicationException
	 */
	public List<AgencyTaskBean> fetchAgencyTask(P8UserSession aoUserSession, AgencyTaskBean aoAgencyTaskQueryParam)
			throws ApplicationException
	{
		List<AgencyTaskBean> loAgencyTask = null;
		String lsViewName = null;
		LOG_OBJECT.Debug("Entered P8ProcessServiceForSolicitationFinancials.fetchAgencyTask() with parameters::"
				+ aoAgencyTaskQueryParam.toString());

		SqlSession loSession = aoUserSession.getFilenetPEDBSession();
		if (loSession == null)
		{
			ApplicationException loAppex = new ApplicationException("Filenet PE DB Session is not found");
			loAppex.setContextData(aoAgencyTaskQueryParam.getFilterProp());
			LOG_OBJECT.Error("Filenet PE DB Session is not found", loAppex);
			throw loAppex;
		}

		try
		{
			lsViewName = getPEViewName(P8Constants.HSS_QUEUE_NAME,
					Integer.valueOf(aoUserSession.getIsolatedRegionNumber()));
			loAgencyTask = peOperationHelperForSolicitationFinancials.fetchAgencyTask(loSession, lsViewName,
					aoAgencyTaskQueryParam.getFilterProp(), aoAgencyTaskQueryParam.getOrderBy(),
					aoAgencyTaskQueryParam.getPaginationNum());

		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (Exception aoEx)
		{
			setMoState("Error while fetch Agency task.");
			ApplicationException loAppex = new ApplicationException("Error while  finishing a child task.", aoEx);
			loAppex.setContextData(aoAgencyTaskQueryParam.getFilterProp());
			LOG_OBJECT.Error("Exception in P8ProcessServiceForSolicitationFinancials.fetchAgencyTask()::", loAppex);
			throw loAppex;
		}
		
		//[Start]R7.12.0 QC9311 Minimize Debug
		//LOG_OBJECT.Debug("Exited P8ProcessServiceForSolicitationFinancials.fetchAgencyTask(). " + loAgencyTask);
		LOG_OBJECT.Debug("Exited P8ProcessServiceForSolicitationFinancials.fetchAgencyTask(). " );
		//[End]R7.12.0 QC9311 Minimize Debug
		return loAgencyTask;
	}

	/**
	 * This method fetches all agency task list to show in task in box and
	 * management
	 * 
	 * @param aoUserSession Filenet session
	 * @param aoRequiredProprs Required Proprs
	 * @param aoFilter Filter
	 * @return return list of task
	 * @throws ApplicationException
	 */
	public HashMap fetchAcceleratorTask(P8UserSession aoUserSession, HashMap aoRequiredProprs, HashMap aoFilter)
			throws ApplicationException
	{
		List<AgencyTaskBean> loAgencyTask = null;
		String lsViewName = null;
		HashMap loHMOutput = null;
		LOG_OBJECT.Debug("Entered P8ProcessServiceForSolicitationFinancials.fetchAcceleratorTask() with parameters::"
				+ aoRequiredProprs.toString());

		SqlSession loSession = aoUserSession.getFilenetPEDBSession();
		if (loSession == null)
		{
			ApplicationException loAppex = new ApplicationException("Filenet PE DB Session is not found");
			loAppex.setContextData(aoRequiredProprs);
			LOG_OBJECT.Error("Filenet PE DB Session is not found", loAppex);
			throw loAppex;
		}

		try
		{
			lsViewName = getPEViewName(P8Constants.HSS_QUEUE_NAME,
					Integer.valueOf(aoUserSession.getIsolatedRegionNumber()));
			loHMOutput = peOperationHelperForSolicitationFinancials.fetchAcceleratorTask(loSession, lsViewName,
					aoFilter, aoRequiredProprs);

		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (Exception aoEx)
		{
			setMoState("Error while fetch Agency task.");
			ApplicationException loAppex = new ApplicationException("Error while  finishing a child task.", aoEx);
			loAppex.setContextData(aoRequiredProprs);
			LOG_OBJECT.Error("Exception in P8ProcessServiceForSolicitationFinancials.fetchAgencyTask()::", loAppex);
			throw loAppex;
		}
		//[Start]R7.12.0 QC9311 Minimize Debug
		//LOG_OBJECT.Debug("Exited P8ProcessServiceForSolicitationFinancials.fetchAgencyTask(). " + loAgencyTask);
		LOG_OBJECT.Debug("Exited P8ProcessServiceForSolicitationFinancials.fetchAgencyTask(). " );
		//[End]R7.12.0 QC9311 Minimize Debug
		return loHMOutput;
	}

	/**
	 * This method fetches the total no of task count
	 * 
	 * @param aoUserSession Filenet session
	 * @param aoAgencyTaskQueryParam Agency Task param bean object
	 * @return int
	 * @throws ApplicationException
	 */
	public int fetchAgencyTaskCount(P8UserSession aoUserSession, AgencyTaskBean aoAgencyTaskQueryParam)
			throws ApplicationException
	{
		int liTaskCount = 0;
		String lsViewName = null;
		String lsWhereClause = null;
		LOG_OBJECT.Debug("Entered P8ProcessServiceForSolicitationFinancials.fetchAgencyTaskCount() with parameters::"
				+ aoAgencyTaskQueryParam.toString());

		SqlSession loSession = aoUserSession.getFilenetPEDBSession();
		if (loSession == null)
		{
			ApplicationException loAppex = new ApplicationException("Filenet PE DB Session is not found");
			loAppex.setContextData(aoAgencyTaskQueryParam.getFilterProp());
			LOG_OBJECT.Error("Filenet PE DB Session is not found", loAppex);
			throw loAppex;
		}

		try
		{
			lsViewName = getPEViewName(P8Constants.HSS_QUEUE_NAME,
					Integer.valueOf(aoUserSession.getIsolatedRegionNumber()));
			lsWhereClause = peOperationHelperForSolicitationFinancials.createAgencyTaskFilter(aoAgencyTaskQueryParam
					.getFilterProp());
			liTaskCount = peOperationHelperForSolicitationFinancials.getOpenTaskCount(loSession, lsViewName,
					lsWhereClause);

		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			setMoState("Error while fetching agency task count.");
			ApplicationException loAppex = new ApplicationException("Error while fetchAgencyTaskCount.", aoEx);
			loAppex.setContextData(aoAgencyTaskQueryParam.getFilterProp());
			LOG_OBJECT
					.Error("Exception in P8ProcessServiceForSolicitationFinancials.fetchAgencyTaskCount()::", loAppex);
			throw loAppex;
		}
		LOG_OBJECT.Debug("Exited P8ProcessServiceForSolicitationFinancials.fetchAgencyTaskCount(). " + liTaskCount);
		return liTaskCount;
	}

	/**
	 * This method assign multiple tasks to user
	 * 
	 * @param aoUserSession Filenet session
	 * @param aoWobNumbers List of wob numbers
	 * @param asUserId User Id
	 * @param asSessionUserName Session user name
	 * @return boolean
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public boolean assignMultiTask(P8UserSession aoUserSession, List<String> aoWobNumbers, String asUserId,
			String asSessionUserName) throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put(HHSConstants.FILE_NET_SESSION, aoUserSession.getObjectStoreName());
		loHmReqExceProp.put(HHSConstants.AS_WOB_NBRS, aoWobNumbers);
		loHmReqExceProp.put(HHSConstants.AS_USER_NAME, asUserId);
		loHmReqExceProp.put(HHSConstants.AS_SESSION_USER_NAME, asSessionUserName);
		LOG_OBJECT.Debug("Entered P8ProcessServiceForSolicitationFinancials.assign() with parameters::"
				+ loHmReqExceProp.toString());
		if (null == aoWobNumbers)
		{
			ApplicationException loAppex = new ApplicationException("aoWobNumbers is empty");
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("aoWobNumbers have null value::", loAppex);
			throw loAppex;
		}
		if (asUserId == null || asUserId.isEmpty())
		{
			ApplicationException loAppex = new ApplicationException("asUserName is null");
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("asUserName is empty::", loAppex);
			throw loAppex;
		}
		try
		{
			// Fetching PE Session
			VWSession loVWSession = filenetConnection.getPESession(aoUserSession);
			if (loVWSession == null)
			{
				ApplicationException loAppex = new ApplicationException("VWSession is not found");
				loAppex.setContextData(loHmReqExceProp);
				LOG_OBJECT.Error("VWSession is not found::", loAppex);
				throw loAppex;
			}
			if (null != aoWobNumbers)
			{
				for (String lsWobNum : aoWobNumbers)
				{
					peOperationHelperForSolicitationFinancials.assignTask(loVWSession, lsWobNum, asUserId,
							asSessionUserName, P8Constants.HSS_QUEUE_NAME);
					setTaskIDMultipleAssign(loVWSession, lsWobNum);
					LOG_OBJECT.Debug("Work items::" + lsWobNum + " assigned to user::" + asUserId);
				}
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while assigning the work items to user: " + asUserId);
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessServiceForSolicitationFinancials.assign()::", aoAppex);
			throw aoAppex;
		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			setMoState("Error while assigning the work item to user: " + asUserId);
			ApplicationException loAppex = new ApplicationException("Error while assigning the work item to user: "
					+ asUserId, aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessServiceForSolicitationFinancials.assignMultiTask()::", loAppex);
			throw loAppex;
		}
		return true;
	}

	/**
	 * This method fetch the Task Id from task and set its value again
	 * incrementing by 1
	 * @param aoVWSession Filenet VW session object
	 * @param asWobNum Workflow id
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private void setTaskIDMultipleAssign(VWSession aoVWSession, String asWobNum) throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		Integer loTaskId = null;
		loHmReqExceProp.put(HHSConstants.PROPERTY_PE_TASK_ID, "");
		try
		{
			loHmReqExceProp = peOperationHelper.getWorkItemDetails(aoVWSession, P8Constants.HSS_QUEUE_NAME,
					P8Constants.PROPERTY_PE_WOBNUMBER, asWobNum, loHmReqExceProp);
			loTaskId = (Integer) (((HashMap) loHmReqExceProp.get(asWobNum)).get(HHSConstants.PROPERTY_PE_TASK_ID));
			if (null != loTaskId)
			{
				loHmReqExceProp.put(HHSConstants.PROPERTY_PE_TASK_ID, loTaskId + HHSConstants.INT_ONE);
			}
			else
			{
				loHmReqExceProp.put(HHSConstants.PROPERTY_PE_TASK_ID, HHSConstants.INITIAL_TASK_ID
						+ HHSConstants.INT_ONE);
			}

			VWStepElement loStepElement = getStepElementfromWobNo(aoVWSession, asWobNum, P8Constants.HSS_QUEUE_NAME);
			if (loStepElement == null)
			{
				throwApplicationException(
						"Error in P8ProcessServiceForSolicitationFinancials.assignMultiTask while fetching step element",
						loHmReqExceProp);
			}
			// set the workflow properties
			loStepElement = peOperationHelper.setWorkFlowProperties(loStepElement, loHmReqExceProp);

			loStepElement.doSave(true);
		}
		catch (VWException aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error while setting task id to work item: " + aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessServiceForSolicitationFinancials.setTaskIDMultipleAssign()::",
					loAppex);
			throw loAppex;
		}

	}

	/**
	 * This function is used for fetching current task owner of a workflow. If
	 * workflow does not exist it throw exception
	 * 
	 * @param aoUserSession Filenet session
	 * @param asWobNo Wobnum
	 * @param Task Count
	 * @return Current Task Owner
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public HashMap fetchCurrentTaskOwner(P8UserSession aoUserSession, String asWobNo, Integer aoTaskCount)
			throws ApplicationException
	{
		String lsTaskOwner = null;
		String lsTaskStatus = null;
		HashMap loHmReqExceProp = new HashMap();
		HashMap loHmOutputProp = new HashMap();
		loHmReqExceProp.put(HHSConstants.FILE_NET_SESSION, aoUserSession.getObjectStoreName());
		loHmReqExceProp.put(HHSConstants.AS_WOB_NO, asWobNo);
		if (aoTaskCount > 0)
		{
			LOG_OBJECT
					.Debug("Entered P8ProcessServiceForSolicitationFinancials.fetchCurrentTaskOwner() with parameters : "
							+ loHmReqExceProp.toString());
			if (asWobNo.isEmpty())
			{
				ApplicationException loAppex = new ApplicationException("asWobNo is empty");
				loAppex.setContextData(loHmReqExceProp);
				LOG_OBJECT.Error("asWobNo is empty::", loAppex);
				throw loAppex;
			}
			try
			{
				// Fetching PE Session
				VWSession loVWSession = filenetConnection.getPESession(aoUserSession);
				if (loVWSession == null)
				{
					ApplicationException loAppex = new ApplicationException("VWSession is not found");
					loAppex.setContextData(loHmReqExceProp);
					LOG_OBJECT.Error("VWSession is not found::", loAppex);
					throw loAppex;
				}
				VWStepElement loStepElement = getStepElementfromWobNo(loVWSession, asWobNo, P8Constants.HSS_QUEUE_NAME);
				if (loStepElement == null)
				{
					throwApplicationException(
							"Exception in P8ProcessServiceForSolicitationFinancials.fetchCurrentTaskOwner():Error in getting step element from wob no "
									+ asWobNo, loHmReqExceProp);
				}

				lsTaskOwner = (String) loStepElement.getParameterValue(P8Constants.PROPERTY_PE_ASSIGNED_TO);
				lsTaskStatus = (String) loStepElement.getParameterValue(HHSConstants.TASK_STATUS);
				loHmOutputProp.put(P8Constants.PROPERTY_PE_ASSIGNED_TO, lsTaskOwner);
				loHmOutputProp.put(HHSConstants.TASK_STATUS, lsTaskStatus);
			}
			catch (ApplicationException aoAppex)
			{
				setMoState("Error while checking if the currently logged-in user is same as the current assigned user");
				aoAppex.setContextData(loHmReqExceProp);
				LOG_OBJECT.Error("Exception in P8ProcessServiceForSolicitationFinancials.fetchCurrentTaskOwner()::",
						aoAppex);
				throw aoAppex;
			}
			catch (Exception aoEx)
			{
				setMoState("Error while checking if the currently logged-in user is same as the current assigned user");
				ApplicationException loAppex = new ApplicationException(
						"Error while checking if the currently logged-in user is same as the current assigned user",
						aoEx);
				loAppex.setContextData(loHmReqExceProp);
				LOG_OBJECT.Error("Exception in P8ProcessService.fetchCurrentTaskOwner()::", loAppex);
				throw loAppex;
			}
			LOG_OBJECT
					.Debug("Exited P8ProcessServiceForSolicitationFinancials.fetchCurrentTaskOwner(). Returned lsTaskOwner::"
							+ lsTaskOwner);
		}
		return loHmOutputProp;
	}

	/**
	 * This method is used for launching a workflow given by asWorkflowName and
	 * boolean status flag
	 * 
	 * @param aoUserSession a user bean having information about user
	 * @param asWorkflowName a string value of workflow name
	 * @param aoHmReqdWorkflowProperties map containing workflow properties
	 * @param loLaunchWorkflow - boolean status flag
	 * @return Boolean loLaunchSuccessful - boolean status flag
	 * @throws ApplicationException If an application exception occurred
	 */
	public Boolean launchWorkflow(P8UserSession aoUserSession, String asWorkflowName,
			HashMap aoHmReqdWorkflowProperties, Boolean loLaunchWorkflow) throws ApplicationException
	{
		Boolean loLaunchSuccessful = false;
		// checking if the value of boolean status flag is true
		if (null != loLaunchWorkflow && loLaunchWorkflow)
		{
			launchWorkflow(aoUserSession, asWorkflowName, aoHmReqdWorkflowProperties);
			loLaunchSuccessful = true;
		}
		return loLaunchSuccessful;

	}

	/**
	 * This method is used to fetch the work item id from the file net view for
	 * the step name
	 * @param aoUserSession file net session bean
	 * @param aoHmReqdWFProperties required work flow properties to create where
	 *            clause
	 * @return boolean
	 * @throws ApplicationException if any exception occurred
	 */
	public Boolean fetchWorkflowIdFromViewForStepName(P8UserSession aoUserSession, HashMap aoHmReqdWFProperties)
			throws ApplicationException
	{
		String lsWorkflowId = HHSConstants.EMPTY_STRING;
		String lsViewName = null;
		String lsWhereClause = null;
		Boolean loWorkItempExist = Boolean.FALSE;
		LOG_OBJECT
				.Debug("Entered P8ProcessServiceForSolicitationFinancials.fetchWorkflowIdFromViewForStepName() with parameters::"
						+ aoHmReqdWFProperties.toString());

		SqlSession loSession = aoUserSession.getFilenetPEDBSession();
		if (loSession == null)
		{
			ApplicationException loAppex = new ApplicationException("Filenet PE DB Session is not found");
			loAppex.setContextData(aoHmReqdWFProperties);
			LOG_OBJECT.Error("Filenet PE DB Session is not found", loAppex);
			throw loAppex;
		}

		try
		{
			lsViewName = getPEViewName(P8Constants.HSS_QUEUE_NAME,
					Integer.valueOf(aoUserSession.getIsolatedRegionNumber()));
			lsWhereClause = createWhereClause(aoHmReqdWFProperties);
			lsWorkflowId = peOperationHelperForSolicitationFinancials.fetchWorkflowIdFromView(loSession, lsViewName,
					lsWhereClause);
			if (null != lsWorkflowId && !lsWorkflowId.isEmpty())
			{
				loWorkItempExist = Boolean.TRUE;
			}

		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			setMoState("Error while fetching workflow id from view.");
			ApplicationException loAppex = new ApplicationException("Error while  fetchWorkflowIdFromView.", aoEx);
			loAppex.setContextData(aoHmReqdWFProperties);
			LOG_OBJECT.Error(
					"Exception in P8ProcessServiceForSolicitationFinancials.fetchWorkflowIdFromViewForStepName()::",
					loAppex);
			throw loAppex;
		}
		LOG_OBJECT.Debug("Exited P8ProcessServiceForSolicitationFinancials.fetchWorkflowIdFromView(). " + lsWorkflowId);
		return loWorkItempExist;
	}

	/**
	 * This method is used for Setting WF Property in open Task and dispatch to
	 * next step
	 * 
	 * @param aoUserSession a user bean having information about user
	 * @param asWobNo a string value of child work flow no
	 * @param asFinishStatus a string value of child status
	 * @return HashMap a map containing finished child tasks
	 * @throws ApplicationException If an application exception occurred
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public boolean setWFPropertyAndDispatch(P8UserSession aoUserSession, String asWobNo, HashMap aoHmWFPropForDispatch)
			throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put(HHSConstants.FILE_NET_SESSION, aoUserSession.getObjectStoreName());
		loHmReqExceProp.put(HHSConstants.AS_WOB_NO, asWobNo);
		LOG_OBJECT.Debug("Entered P8ProcessService.setWFPropertyAndDispatch with parameters::"
				+ loHmReqExceProp.toString());
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
			VWStepElement loStepElementForDispatch = getStepElementfromWobNo(loVWSession, asWobNo,
					P8Constants.HSS_QUEUE_NAME);
			if (loStepElementForDispatch == null)
			{
				throwApplicationException("Error in P8ProcessService.finishTask while fetching step element",
						loHmReqExceProp);
			}
			// set the workflow properties required at a time of finish
			if (null != aoHmWFPropForDispatch && !aoHmWFPropForDispatch.isEmpty())
			{
				loStepElementForDispatch = peOperationHelper.setWorkFlowProperties(loStepElementForDispatch,
						aoHmWFPropForDispatch);
			}
			loStepElementForDispatch.doSave(false);
			loStepElementForDispatch.doDispatch();
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while setting WF property in task.");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessService.setWFPropertyAndDispatch::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error while setting WF property in task.");
			ApplicationException loAppex = new ApplicationException("Error while  finishing a child task.", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessService.setWFPropertyAndDispatch::", loAppex);
			throw loAppex;
		}
		LOG_OBJECT.Debug("Exited P8ProcessService.setWFPropertyAndDispatch.");
		return true;
	}

	/**
	 * This method is used to fetch the work items present in the mentioned
	 * Queue since the time period mentioned in the argument. it was added as a
	 * part of enhancement number 6508 in build 3.6.0
	 * @param aoUserSession P8UserSession Bean Object
	 * @param asQueueName Queue name
	 * @param aiTimeSince Time since the items stuck in the queue
	 * @return count of the work items present in the queue
	 * @throws ApplicationException
	 */
	public HashMap<String, Integer> getWorkItemsCountFromQueue(P8UserSession aoUserSession, String asQueueName,
			Integer aiTimeSince) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered P8ProcessServiceForSolicitationFinancials.getWorkItemsCountFromQueue()");
		VWSession loVWSession = null;
		HashMap<String, Integer> loHmWorkItemsDetails = null;
		try
		{
			loVWSession = filenetConnection.getPESession(aoUserSession);
			loHmWorkItemsDetails = peOperationHelperForSolicitationFinancials.getQueueWorkitems(loVWSession,
					asQueueName, aiTimeSince);
		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			setMoState("Error while fetching QueWorkItem from Queue.");
			ApplicationException loAppex = new ApplicationException("Error while fetching QueWorkItem from Queue.",
					aoEx);
			LOG_OBJECT.Error("Error while fetching QueWorkItem from Queue.", loAppex);
			throw loAppex;
		}
		LOG_OBJECT.Debug("Exited P8ProcessServiceForSolicitationFinancials.getWorkItemsCountFromQueue(). "
				+ loHmWorkItemsDetails);
		return loHmWorkItemsDetails;
	}

	/**
	 * Release 5 changes done daily digest mailer
	 * @param aoUserSession P8UserSession Bean Object
	 * @param asQueueName Queue name
	 * @param aiTimeSince Time since the items stuck in the queue
	 * @return count of the work items present in the queue
	 * @throws ApplicationException
	 */
	public List<DefaultAssignment> getWorkItemsCountFromQueueDailyDigest(P8UserSession aoUserSession,
			String asQueueName, SqlSession aoMyBatisSession) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered P8ProcessServiceForSolicitationFinancials.getWorkItemsCountFromQueueDailyDigest()");
		VWSession loVWSession = null;
		List<DefaultAssignment> loDefaultAssignment = null;
		try
		{
			loVWSession = filenetConnection.getPESession(aoUserSession);
			loDefaultAssignment = peOperationHelperForSolicitationFinancials.getQueueWorkitemsDailyDigest(loVWSession,
					asQueueName, aoMyBatisSession);
		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			setMoState("Error while fetching QueWorkItem from Queue.");
			ApplicationException loAppex = new ApplicationException("Error while fetching QueWorkItem from Queue.",
					aoEx);
			LOG_OBJECT.Error("Error while fetching QueWorkItem from Queue.", loAppex);
			throw loAppex;
		}
		LOG_OBJECT.Debug("Exited P8ProcessServiceForSolicitationFinancials.getWorkItemsCountFromQueueDailyDigest(). ");
		return loDefaultAssignment;
	}

	/**
	 * Release 5 changes done daily digest mailer
	 * @param aoUserSession P8UserSession Bean Object
	 * @param asQueueName Queue name
	 * @param aiTimeSince Time since the items stuck in the queue
	 * @return count of the work items present in the queue
	 * @throws ApplicationException
	 */
	public List<TaskDetailsBean> getQueueWorkitemsExport(P8UserSession aoUserSession) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered P8ProcessServiceForSolicitationFinancials.getQueueWorkitemsExport()");
		VWSession loVWSession = null;
		List<TaskDetailsBean> loTaskDetailsBean = null;
		try
		{
			loVWSession = filenetConnection.getPESession(aoUserSession);
			loTaskDetailsBean = peOperationHelperForSolicitationFinancials.getQueueWorkitemsExport(loVWSession);
		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			setMoState("Error while fetching QueWorkItem from Queue.");
			ApplicationException loAppex = new ApplicationException("Error while fetching QueWorkItem from Queue.",
					aoEx);
			LOG_OBJECT.Error("Error while fetching QueWorkItem from Queue.", loAppex);
			throw loAppex;
		}
		LOG_OBJECT.Debug("Exited P8ProcessServiceForSolicitationFinancials.getQueueWorkitemsExport(). ");
		return loTaskDetailsBean;
	}

	/**
	 * This method added as a part of release 3.8.0 enhancement 6534
	 * 
	 * <ul>
	 * This method Enable Change of Task Levels of Approvals for all Tasks
	 * Regardless of Whether there are Tasks Inflight
	 * </ul>
	 * 
	 * @param aoUserSession P8UserSession
	 * @param asWorkflowName taskType
	 * @param asAgencyID agencyID
	 * @param aiOldDBReviewLevel aiOldDBReviewLevel
	 * @param aiNewDBReviewLevel aiNewDBReviewLevel
	 * @throws Exception
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void changeReviewLevels(P8UserSession aoUserSession, String asWorkflowName, String asAgencyID,
			int aiOldDBReviewLevel, int aiNewDBReviewLevel) throws ApplicationException
	{
		LOG_OBJECT
				.Debug("Entering changeReviewLevels method of P8ProcessServiceForSolicitationFinancials class with parameters:::"
						+ asWorkflowName
						+ " :: "
						+ asAgencyID
						+ " :: "
						+ aiOldDBReviewLevel
						+ " :: "
						+ aiNewDBReviewLevel);
		String lsWobNum = HHSConstants.EMPTY_STRING;
		String lsCurrentLevel = HHSConstants.EMPTY_STRING;
		String lsTaskId = null;
		String lsTaskStatus = null;
		String lsTaskVisibility = HHSConstants.EMPTY_STRING;
		int liCurrentLevel = HHSConstants.INT_ZERO;
		SqlSession loSession = null;
		Statement loStatement = null;
		java.sql.Connection loConnection = null;
		// HashMap loHMreqd = new HashMap();
		ResultSet loResultSet = null;
		StringBuffer lsQuery = new StringBuffer(HHSConstants.EMPTY_STRING);
		String lsViewName = null;
		HashMap loWFProperties = new HashMap();
		String lsWhereClause = null;
		HashMap loHmReqExceProp = null;

		try
		{
			// Start QC 9585 R 9.3.0  remove password from logs
			String param = CommonUtil.maskPassword(aoUserSession);
			//LOG_OBJECT.Debug("aoUserSession: " + aoUserSession);
			LOG_OBJECT.Info("aoUserSession: " + param);
			// End QC 9585 R 9.3.0  remove password from logs
			
			aoUserSession.setFilenetPEDBSession(HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory()
					.openSession());
			loSession = aoUserSession.getFilenetPEDBSession();
			
			// Start QC 9585 R 9.3.0  remove password from logs
			param = CommonUtil.maskPassword(loSession);
			//LOG_OBJECT.Debug("loSession: " + loSession);
			LOG_OBJECT.Info("loSession: " + param);
			// End QC 9585 R 9.3.0  remove password from logs
			
			if (loSession == null)
			{
				ApplicationException loAppex = new ApplicationException("Filenet PE DB Session is not found");
				LOG_OBJECT.Error("Filenet PE DB Session is not found", loAppex);
				throw loAppex;
			}
			loConnection = loSession.getConnection();
			LOG_OBJECT.Debug("loConnection: " + loConnection);
			lsViewName = getPEViewName(P8Constants.HSS_QUEUE_NAME,
					Integer.valueOf(aoUserSession.getIsolatedRegionNumber()));
			LOG_OBJECT.Debug("lsViewName: " + lsViewName);
			loWFProperties.put(HHSConstants.PROPERTY_PE_TASK_TYPE, asWorkflowName);
			loWFProperties.put(HHSConstants.PROPERTY_PE_AGENCY_ID, asAgencyID);
			lsWhereClause = createWhereClause(loWFProperties);
			LOG_OBJECT.Debug("lsWhereClause: " + lsWhereClause);
			if (null == lsWhereClause || lsWhereClause.isEmpty())
			{
				throw new ApplicationException(
						"Exception in P8ProcessServiceForSolicitationFinancials.changeReviewLevels() :Where Condition is not set");
			}

			loStatement = loConnection.createStatement();
			lsQuery.append("select \"F_WobNum\",\"CurrentLevel\",\"taskVisibility\",\"TaskID\",\"TaskStatus\" from ");
			lsQuery.append(lsViewName);
			lsQuery.append(" where ");
			lsQuery.append(lsWhereClause);
			loResultSet = peOperationHelperForSolicitationFinancials.executeViewQuery(loSession, lsQuery.toString(),
					loStatement);
			
			LOG_OBJECT.Debug("Got Resultset: " + loResultSet.toString());
			while (loResultSet.next())
			{
				loHmReqExceProp = new HashMap();
				lsWobNum = loResultSet.getString(HHSConstants.F_WOB_NUM);
				lsCurrentLevel = loResultSet.getString(HHSConstants.CURR_LEVEL);
				lsTaskVisibility = loResultSet.getString(HHSConstants.PROPERTY_PE_TASK_VISIBILITY);
				lsTaskId = loResultSet.getString(HHSConstants.PROPERTY_PE_TASK_ID);
				lsTaskStatus = loResultSet.getString(HHSConstants.TASK_STATUS);
				LOG_OBJECT.Debug("Got Values from resultset: lsWobNum- " + lsWobNum + " lsCurrentLevel- "
						+ lsCurrentLevel + " lsTaskVisibility- " + lsTaskVisibility + " lsTaskId- " + lsTaskId
						+ " lsTaskStatus- " + lsTaskStatus);
				if (null != lsCurrentLevel)
				{
					liCurrentLevel = Integer.valueOf(lsCurrentLevel);
				}
				if (null != lsCurrentLevel && null != lsWobNum)
				{
					LOG_OBJECT.Debug("Inside While loop Task Id :: TaskStatus:: " + lsTaskId + " :: " + lsTaskStatus);
					if (aiNewDBReviewLevel != aiOldDBReviewLevel)
					{
						if (aiNewDBReviewLevel > aiOldDBReviewLevel)
						{
							if (null != lsTaskVisibility && lsTaskVisibility.equalsIgnoreCase(HHSConstants.FALSE))
							{
								loHmReqExceProp.put(HHSConstants.PROPERTY_PE_TASK_VISIBILITY, false);
							}
							if (null != lsTaskStatus && lsTaskStatus.equalsIgnoreCase(HHSConstants.STATUS_SUSPENDED))
							{
								loHmReqExceProp.put(HHSConstants.TASK_STATUS, HHSConstants.STATUS_SUSPENDED);
							}
							loHmReqExceProp.put(HHSConstants.PROPERTY_PE_TASK_TOTAL_LEVEL, aiNewDBReviewLevel);
							LOG_OBJECT.Debug("New Review level :" + aiNewDBReviewLevel
									+ " greater than old review level :" + aiOldDBReviewLevel);
							LOG_OBJECT.Debug("Setting workflow properties for Wob No :: " + lsWobNum
									+ " :: with parameters " + loHmReqExceProp);
							setWFProperty(aoUserSession, lsWobNum, loHmReqExceProp);
						}
						else if (aiNewDBReviewLevel < aiOldDBReviewLevel)
						{
							LOG_OBJECT.Debug("New Review level :" + aiNewDBReviewLevel
									+ " less than old review level :" + aiOldDBReviewLevel);
							//[Start] QC 9343 R 8.6 - Preventing Double task: Don't activate task already Approved at Old Level & Finished
							boolean finishedTask = false;
							
							if (liCurrentLevel == aiOldDBReviewLevel 
									&& HHSConstants.STR_BUDGET_APPROVED.equalsIgnoreCase(lsTaskStatus)
									&& HHSConstants.ZERO.equalsIgnoreCase(lsTaskVisibility))
							{
									finishedTask = true;
							}	
							
							//if (liCurrentLevel > aiNewDBReviewLevel)
							if (liCurrentLevel > aiNewDBReviewLevel	&& 	!finishedTask )
							//[End] QC 9343 R 8.6 - Preventing Double task: Don't activate already Approved at Old Level & Finished task - add condition	
							{
								LOG_OBJECT.Debug("Current Review level :" + liCurrentLevel
										+ " greater than new review level :" + aiNewDBReviewLevel);
								int liLevelDifference = aiOldDBReviewLevel - aiNewDBReviewLevel;
								int liLevelToBeReturned = liCurrentLevel - aiNewDBReviewLevel;
								LOG_OBJECT.Debug("Level Difference :" + liLevelDifference + "  and LevelToBeReturned :"
										+ liLevelToBeReturned);
								while (liLevelToBeReturned > 0)
								{
									//Loop updated in R5
									loHmReqExceProp.put(HHSConstants.TASK_STATUS, HHSConstants.TASK_RFR);
									loHmReqExceProp.put(HHSConstants.PROPERTY_PE_TASK_TOTAL_LEVEL, aiNewDBReviewLevel);
									loHmReqExceProp.put(HHSConstants.CURR_LEVEL, aiNewDBReviewLevel);
									if (null != lsTaskId && !lsTaskId.isEmpty())
									{
										loHmReqExceProp.put(HHSConstants.PROPERTY_PE_TASK_ID, Integer.valueOf(lsTaskId)
												+ HHSConstants.INT_ONE);
									}
									else
									{
										loHmReqExceProp.put(HHSConstants.PROPERTY_PE_TASK_ID,
												HHSConstants.INITIAL_TASK_ID + HHSConstants.INT_TWO);
									}
									if (null != lsTaskVisibility
											&& lsTaskVisibility.equalsIgnoreCase(HHSConstants.FALSE))
									{
										loHmReqExceProp.put(HHSConstants.PROPERTY_PE_TASK_VISIBILITY, false);
									}
									LOG_OBJECT.Debug("Finishing task with WobNum :" + lsWobNum + "  and properties :"
											+ loHmReqExceProp);
									new P8ProcessServiceForSolicitationFinancials().finishTask(aoUserSession, lsWobNum,
											lsTaskStatus, loHmReqExceProp);
									
									if(liLevelToBeReturned >= 2 || (null != lsTaskStatus
											&& lsTaskStatus.equalsIgnoreCase(HHSConstants.STATUS_SUSPENDED))){
										Thread.sleep(5000);// added delay for processing on same task(Added in R5)
										if (null != lsTaskStatus
												&& lsTaskStatus.equalsIgnoreCase(HHSConstants.STATUS_SUSPENDED))
										{
											loHmReqExceProp.put(HHSConstants.TASK_STATUS, HHSConstants.STATUS_SUSPENDED);
											setWFProperty(aoUserSession, lsWobNum, loHmReqExceProp);
										}
									}
									liLevelToBeReturned--;
								}
							}
							else
							{   
								if (null != lsTaskVisibility && lsTaskVisibility.equalsIgnoreCase(HHSConstants.FALSE))
								{
									loHmReqExceProp.put(HHSConstants.PROPERTY_PE_TASK_VISIBILITY, false);
								}
								if (null != lsTaskStatus
										&& lsTaskStatus.equalsIgnoreCase(HHSConstants.STATUS_SUSPENDED))
								{
									loHmReqExceProp.put(HHSConstants.TASK_STATUS, HHSConstants.STATUS_SUSPENDED);
								}
								loHmReqExceProp.put(HHSConstants.PROPERTY_PE_TASK_TOTAL_LEVEL, aiNewDBReviewLevel);
								LOG_OBJECT.Debug("Setting WF property  with WobNum :" + lsWobNum + "  and properties :"
										+ loHmReqExceProp);
								setWFProperty(aoUserSession, lsWobNum, loHmReqExceProp);
							}
						}
					}
				}
			}
			LOG_OBJECT
					.Debug("Exited P8ProcessServiceForSolicitationFinancials.changeReviewLevels(). Review level changes successful");
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while changing review levels.");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error while changing review levels ::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error while changing review levels.");
			ApplicationException loAppex = new ApplicationException("Error while changing review levels.", aoEx);
			LOG_OBJECT.Error("Error while changing review levels.", loAppex);
			throw loAppex;
		}
	}

	/**
	 * This method added as a part of release 3.8.0 enhancement 6482
	 * 
	 * <ul>
	 * This method updates the Contract Title and program name in all Filenet
	 * Tasks for Update Contract Information screen.
	 * @param aoUserSession Filenet session
	 * @param aoEPinDetailBean EPinDetailBean bean
	 * @throws Exception
	 */
	@SuppressWarnings(
	{ "static-access", "rawtypes", "unchecked" })
	public void updateContractInformationInFilenet(P8UserSession aoUserSession, EPinDetailBean aoEPinDetailBean,
			Boolean aoUpdateInfo) throws Exception
	{
		LOG_OBJECT
				.Debug("Entering updateContractInformationInFilenet method of P8ProcessServiceForSolicitationFinancials class with parameters:::"
						+ aoEPinDetailBean);
		HashMap loHmReqExceProp = null;
		VWQueueQuery loVWQueueQuery = null;
		VWQueue loVWQueue = null;
		String lsWobNum = null;
		String lsProgramName = null;
		String lsTaskVisibility = HHSConstants.EMPTY_STRING;
		String lsContractTitle = HHSConstants.EMPTY_STRING;
		String lsQueueFilter = HHSConstants.EMPTY_STRING;
		StringBuffer loStrBufferQueueFilter = new StringBuffer(HHSConstants.EMPTY_STRING);
		loStrBufferQueueFilter.append("( ");
		loStrBufferQueueFilter.append(HHSConstants.PROPERTY_PE_CONTRACT_ID);
		loStrBufferQueueFilter.append(" = '");
		loStrBufferQueueFilter.append(aoEPinDetailBean.getContractId());
		loStrBufferQueueFilter.append("'");
		loStrBufferQueueFilter.append(" )");
		lsQueueFilter = loStrBufferQueueFilter.toString();
		LOG_OBJECT.Debug("Query Filter ::: " + lsQueueFilter);
		try
		{
			if (aoUpdateInfo)
			{
				VWSession loVWSession = filenetConnection.getPESession(aoUserSession);
				LOG_OBJECT.Debug("Established PE session :: ");
				loVWQueue = loVWSession.getQueue(P8Constants.HSS_QUEUE_NAME);
				if (loVWQueue == null)
				{
					LOG_OBJECT.Error("Error While Fetching Queue");
				}
				int liFlag = loVWQueue.QUERY_READ_LOCKED;
				int liType = VWFetchType.FETCH_TYPE_QUEUE_ELEMENT;
				loVWQueueQuery = loVWQueue.createQuery(null, null, null, liFlag, lsQueueFilter, null, liType);
				while (loVWQueueQuery.hasNext())
				{
					loHmReqExceProp = new HashMap();
					VWQueueElement loVWQueueElement = (VWQueueElement) loVWQueueQuery.next();
					lsWobNum = loVWQueueElement.getDataField(HHSConstants.F_WOB_NUM).getStringValue();
					if (null != loVWQueueElement.getDataField(HHSConstants.PROPERTY_PE_PROGRAM_NAME))
						lsProgramName = loVWQueueElement.getDataField(HHSConstants.PROPERTY_PE_PROGRAM_NAME)
								.getStringValue();
					lsTaskVisibility = loVWQueueElement.getDataField(HHSConstants.PROPERTY_PE_TASK_VISIBILITY)
							.getStringValue();
					if (null != loVWQueueElement.getDataField(HHSConstants.PROPERTY_PE_PROCUREMENT_TITLE))
						lsContractTitle = loVWQueueElement.getDataField(HHSConstants.PROPERTY_PE_PROCUREMENT_TITLE)
								.getStringValue();
					LOG_OBJECT.Debug("Inside While loop :: " + lsWobNum + " :: " + lsProgramName + " :: "
							+ lsContractTitle);
					if (null != aoEPinDetailBean.getProgramName() && !aoEPinDetailBean.getProgramName().isEmpty())
					{
						if (null != lsProgramName && !lsProgramName.isEmpty())
						{
							loHmReqExceProp.put(HHSConstants.PROPERTY_PE_PROGRAM_NAME,
									aoEPinDetailBean.getProgramName());
						}
					}
					if (null != aoEPinDetailBean.getContractTitle() && !aoEPinDetailBean.getContractTitle().isEmpty())
					{
						if (null != lsContractTitle && !lsContractTitle.isEmpty())
						{
							loHmReqExceProp.put(HHSConstants.PROPERTY_PE_PROCUREMENT_TITLE,
									aoEPinDetailBean.getContractTitle());
						}
					}
					if (null != lsTaskVisibility && lsTaskVisibility.equalsIgnoreCase(HHSConstants.FALSE))
					{
						loHmReqExceProp.put(HHSConstants.PROPERTY_PE_TASK_VISIBILITY, false);
					}
					LOG_OBJECT.Debug("updating workflow properties for Wob No :: " + lsWobNum + " :: with parameters "
							+ loHmReqExceProp);
					setWFProperty(aoUserSession, lsWobNum, loHmReqExceProp);
					LOG_OBJECT.Debug("Updated workflow properties for Wob No :: " + lsWobNum + " :: with parameters "
							+ loHmReqExceProp);
				}
			}
			LOG_OBJECT
					.Debug("Exited P8ProcessServiceForSolicitationFinancials.updateContractInformationInFilenet(). contract information updated"
							+ " successfully");
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while updating contract information in filenet. ");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error while updating contract information in filenet::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error while updating contract information in filenet.");
			ApplicationException loAppex = new ApplicationException(
					"Error while updating contract information in filenet.", aoEx);
			LOG_OBJECT.Error("Error while updating contract information in filenet.", loAppex);
			throw loAppex;
		}
	}

	/**
	 * This method id added in release 3.10.0 enhancement 6576 This method gets
	 * pending approval invoice review tasks which are approved at Level 1
	 * 
	 * @param aoUserSession Filenet session object
	 * @param aoHmReqdWFProperties All required properties in HashMap object
	 * @return List
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public List<String> fetchInvoiceNumbersForPAInvoicesApprovedAtLevel1(P8UserSession aoUserSession,
			HashMap aoHmReqdWFProperties) throws ApplicationException
	{
		String lsViewName = null;
		String lsWhereClause = null;
		List<String> loWorkflowIDList = null;
		LOG_OBJECT
				.Debug("Entered P8ProcessServiceForSolicitationFinancials.fetchInvoiceNumbersForPAInvoicesApprovedAtLevel1() with parameters::"
						+ aoHmReqdWFProperties.toString());

		SqlSession loSession = aoUserSession.getFilenetPEDBSession();
		if (loSession == null)
		{
			ApplicationException loAppex = new ApplicationException("Filenet PE DB Session is not found");
			loAppex.setContextData(aoHmReqdWFProperties);
			LOG_OBJECT.Error("Filenet PE DB Session is not found", loAppex);
			throw loAppex;
		}

		try
		{
			lsViewName = getPEViewName(P8Constants.HSS_QUEUE_NAME,
					Integer.valueOf(aoUserSession.getIsolatedRegionNumber()));
			lsWhereClause = createWhereClause(aoHmReqdWFProperties);
			loWorkflowIDList = peOperationHelperForSolicitationFinancials
					.fetchInvoiceNumbersForInvoicesApprovedAtLevel1(loSession, lsViewName, lsWhereClause);

		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			setMoState("Error while fetching workflow ids from view.");
			ApplicationException loAppex = new ApplicationException(
					"Error while  fetchInvoiceNumbersForInvoicesApprovedAtLevel1.", aoEx);
			loAppex.setContextData(aoHmReqdWFProperties);
			LOG_OBJECT
					.Error("Exception in P8ProcessServiceForSolicitationFinancials.fetchInvoiceNumbersForInvoicesApprovedAtLevel1()::",
							loAppex);
			throw loAppex;
		}
		return loWorkflowIDList;
	}

	/**
	 * This method id added in release 3.12.0 enhancement 6578 This method fetch
	 * pending approval payment review/ Advance Payment Review tasks at Level 1
	 * 
	 * @param aoUserSession Filenet session object
	 * @param aoHmReqdWFProperties All required properties in HashMap object
	 * @return List
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public HashMap fetchPaymentIDsForPaymentsAtLevel1(P8UserSession aoUserSession, HashMap aoHmReqdWFProperties)
			throws ApplicationException
	{
		String lsViewName = null;
		String lsWhereClause = null;
		HashMap loInvoiceIDAdvanceIDHashMap = new HashMap();
		LOG_OBJECT
				.Debug("Entered P8ProcessServiceForSolicitationFinancials.fetchPaymentIDsForPaymentsAtLevel1() with parameters::"
						+ aoHmReqdWFProperties.toString());

		SqlSession loSession = aoUserSession.getFilenetPEDBSession();
		if (loSession == null)
		{
			ApplicationException loAppex = new ApplicationException("Filenet PE DB Session is not found");
			loAppex.setContextData(aoHmReqdWFProperties);
			LOG_OBJECT.Error("Filenet PE DB Session is not found", loAppex);
			throw loAppex;
		}

		try
		{
			lsViewName = getPEViewName(P8Constants.HSS_QUEUE_NAME,
					Integer.valueOf(aoUserSession.getIsolatedRegionNumber()));
			lsWhereClause = createWhereClause(aoHmReqdWFProperties);
			LOG_OBJECT.Info("where clause to fetch Invoice ids and budget advance ids map ::: " + lsWhereClause);
			loInvoiceIDAdvanceIDHashMap = peOperationHelperForSolicitationFinancials
					.fetchInvoiceNumbersForPaymentsAtLevel1(loSession, lsViewName, lsWhereClause);
			LOG_OBJECT.Info("Invoice ids and budget advance ids map fetched successfully ::: "
					+ loInvoiceIDAdvanceIDHashMap);

		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while fetching invoice ids and budget advance ids map from view. ");
			aoAppex.setContextData(aoHmReqdWFProperties);
			LOG_OBJECT.Error("Error while fetching invoice ids and budget advance ids map from view :: ", aoAppex);
			throw aoAppex;
		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			setMoState("Error while fetching invoice ids and budget advance ids map from view.");
			ApplicationException loAppex = new ApplicationException(
					"Error in method  fetchPaymentIDsForPaymentsAtLevel1()", aoEx);
			loAppex.setContextData(aoHmReqdWFProperties);
			LOG_OBJECT.Error(
					"Exception in P8ProcessServiceForSolicitationFinancials.fetchPaymentIDsForPaymentsAtLevel1()::",
					loAppex);
			throw loAppex;
		}
		return loInvoiceIDAdvanceIDHashMap;
	}

	/**
	 * This method id added in release 5.This method fetch the export data for
	 * Agency User.
	 * 
	 * @param aoUserSession Filenet session object
	 * @param aoSqlSession Sql session object
	 * @param aoAgencyTaskQueryParam AgencyTaskBean Object
	 * @return List of AgencyTaskBean Object
	 * @throws ApplicationException
	 */
	public List<AgencyTaskBean> fetchAgencyTaskExport(P8UserSession aoUserSession, SqlSession aoSqlSession,
			AgencyTaskBean aoAgencyTaskQueryParam) throws ApplicationException
	{
		List<AgencyTaskBean> loAgencyTask = null;
		String lsViewName = null;
		LOG_OBJECT.Debug("Entered P8ProcessServiceForSolicitationFinancials.fetchAgencyTaskEcport() with parameters::"
				+ aoAgencyTaskQueryParam.toString());

		SqlSession loSession = aoUserSession.getFilenetPEDBSession();
		if (loSession == null)
		{
			ApplicationException loAppex = new ApplicationException("Filenet PE DB Session is not found");
			loAppex.setContextData(aoAgencyTaskQueryParam.getFilterProp());
			LOG_OBJECT.Error("Filenet PE DB Session is not found", loAppex);
			throw loAppex;
		}

		try
		{
			lsViewName = getPEViewName(P8Constants.HSS_QUEUE_NAME,
					Integer.valueOf(aoUserSession.getIsolatedRegionNumber()));
			loAgencyTask = peOperationHelperForSolicitationFinancials.fetchAgencyTaskExport(loSession, lsViewName,
					aoAgencyTaskQueryParam.getFilterProp(), aoAgencyTaskQueryParam.getOrderBy(), aoSqlSession);

		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (Exception aoEx)
		{
			setMoState("Error while fetch Agency task.");
			ApplicationException loAppex = new ApplicationException("Error while  finishing a child task.", aoEx);
			loAppex.setContextData(aoAgencyTaskQueryParam.getFilterProp());
			LOG_OBJECT.Error("Exception in P8ProcessServiceForSolicitationFinancials.fetchAgencyTask()::", loAppex);
			throw loAppex;
		}
		LOG_OBJECT.Debug("Exited P8ProcessServiceForSolicitationFinancials.fetchAgencyTask(). " + loAgencyTask);
		return loAgencyTask;
	}



	/*
	 * [Start]: Added for defect QC 9555 R 8.7.0
	 * */
	@SuppressWarnings("unchecked")
	public Map<String, List<AgencyTaskBean>> fetchInvUsrForTaskExport(SqlSession aoSqlSession) throws ApplicationException {
		try
		{
			LOG_OBJECT.Debug("Start P8ProcessOperationForSolicitationFinancials.fetchInvUsrForTaskExport()");
			List<AgencyTaskBean> loInvoiceAgencyTaskBeanList = (List<AgencyTaskBean>) DAOUtil.masterDAO(aoSqlSession,
					null, HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
					HHSP8Constants.FETCH_SERVICE_START_END_DATE, null);
			List<AgencyTaskBean> loNameAgencyTaskBeanList = (List<AgencyTaskBean>) DAOUtil.masterDAO(aoSqlSession,
					null, HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
					HHSP8Constants.FETCH_AGENCY_PROVIDER_NAME, null);

			HashMap <String, List<AgencyTaskBean>> paramListForTaskExport = new HashMap <String, List<AgencyTaskBean>> ();
			paramListForTaskExport.put(HHSP8Constants.PARAM_USER_NAME_LIST, loNameAgencyTaskBeanList);
			paramListForTaskExport.put(HHSP8Constants.PARAM_INVOICE_SRT_END_DATE_LIST, loInvoiceAgencyTaskBeanList);
			
			return paramListForTaskExport;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in fetchInvUsrForTaskExport:: " , aoEx);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.fetchInvUsrForTaskExport()::", loAppex);
			throw loAppex;
		}
	}

	
	public List<AgencyTaskBean> fetchAgencyTaskForExport(P8UserSession aoUserSession, SqlSession aoSqlSession,
			AgencyTaskBean aoAgencyTaskQueryParam, Map<String,AgencyTaskBean> aoUserNameLst  , Map<String,AgencyTaskBean> aoInvSrtEndDateLst) throws ApplicationException
	{
		List<AgencyTaskBean> loAgencyTask = null;
		String lsViewName = null;
		LOG_OBJECT.Debug("Entered P8ProcessServiceForSolicitationFinancials.fetchAgencyTaskEcport() with parameters::"
				+ aoAgencyTaskQueryParam.toString());

		SqlSession loSession = aoUserSession.getFilenetPEDBSession();
		if (loSession == null)
		{
			ApplicationException loAppex = new ApplicationException("Filenet PE DB Session is not found");
			loAppex.setContextData(aoAgencyTaskQueryParam.getFilterProp());
			LOG_OBJECT.Error("Filenet PE DB Session is not found", loAppex);
			throw loAppex;
		}

		try
		{
			lsViewName = getPEViewName(P8Constants.HSS_QUEUE_NAME,
					Integer.valueOf(aoUserSession.getIsolatedRegionNumber()));
			
			System.out.println("---------------[After getting all Task from file net   ]: com.nyc.hhs.service.filenetmanager.p8services.P8ProcessServiceForSolicitationFinancials  -- fetchAgencyTaskForExport " );
			loAgencyTask = peOperationHelperForSolicitationFinancials.fetchAgencyTaskForExport(loSession, lsViewName,
					aoAgencyTaskQueryParam.getFilterProp(), aoAgencyTaskQueryParam.getOrderBy(), aoSqlSession , aoUserNameLst  ,  aoInvSrtEndDateLst);
			System.out.println("---------------[All Done  ]: -- loAgencyTask:"+ loAgencyTask.size() );
			
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (Exception aoEx)
		{
			setMoState("Error while fetch Agency task.");
			ApplicationException loAppex = new ApplicationException("Error while  finishing a child task.", aoEx);
			loAppex.setContextData(aoAgencyTaskQueryParam.getFilterProp());
			LOG_OBJECT.Error("Exception in P8ProcessServiceForSolicitationFinancials.fetchAgencyTask()::", loAppex);
			throw loAppex;
		}
		LOG_OBJECT.Debug("Exited P8ProcessServiceForSolicitationFinancials.fetchAgencyTask(). " + loAgencyTask);
		return loAgencyTask;
	}
	
	
	
	/*
	 * [End]: Added for defect QC 9555 R 8.7.0
	 * */
















	/**
	 * This method gets IsNewLaunchFlag on based of wobnumber
	 * 
	 * <ul>
	 * <li>This method is added in R4</li>
	 * </ul>
	 * 
	 * @param aoUserSession Filenet session object
	 * @param aoHmReqdWFProperties All required properties in HashMap object
	 * @return String
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public String fetchIsNewLaunchFlagFromView(P8UserSession aoUserSession, HashMap aoHmReqdWFProperties)
			throws ApplicationException
	{
		String lsLaunchFlag = HHSConstants.EMPTY_STRING;
		if (aoHmReqdWFProperties == null)
		{
			lsLaunchFlag = null;
		}
		else
		{
			String lsViewName = null;
			String lsWhereClause = null;
			LOG_OBJECT
					.Debug("Entered P8ProcessServiceForSolicitationFinancials.fetchIsNewLaunchFlagFromView() with parameters::"
							+ aoHmReqdWFProperties.toString());
			SqlSession loSession = aoUserSession.getFilenetPEDBSession();
			if (loSession == null)
			{
				ApplicationException loAppex = new ApplicationException("Filenet PE DB Session is not found");
				loAppex.setContextData(aoHmReqdWFProperties);
				LOG_OBJECT.Error("Filenet PE DB Session is not found", loAppex);
				throw loAppex;
			}
			try
			{
				lsViewName = getPEViewName(P8Constants.HSS_QUEUE_NAME,
						Integer.valueOf(aoUserSession.getIsolatedRegionNumber()));
				lsWhereClause = createWhereClause(aoHmReqdWFProperties);
				lsLaunchFlag = peOperationHelperForSolicitationFinancials.fetchIsNewLaunchStatusFromView(loSession,
						lsViewName, lsWhereClause);

			}
			// handling exception other than ApplicationException
			catch (Exception aoEx)
			{
				setMoState("Error while fetching workflow id from view.");
				ApplicationException loAppex = new ApplicationException("Error while  fetchIsNewLaunchFlagFromView.",
						aoEx);
				loAppex.setContextData(aoHmReqdWFProperties);
				LOG_OBJECT.Error(
						"Exception in P8ProcessServiceForSolicitationFinancials.fetchIsNewLaunchFlagFromView()::",
						loAppex);
				throw loAppex;
			}
			LOG_OBJECT.Debug("Exited P8ProcessServiceForSolicitationFinancials.fetchIsNewLaunchFlagFromView(). "
					+ lsLaunchFlag);
		}
		return lsLaunchFlag;
	}
	// End
}