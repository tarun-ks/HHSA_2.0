package com.nyc.hhs.service.filenetmanager.p8services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.CommonUtil;

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
 * Updated in Release 7- Methods Added(checkAutoAprrovalCompleteAndMerge,
 * setAuditForAutoApprovedStatus,getModificationWFAutoApproval,
 * dispatchModificationWF) for auto approval
 * 
 */

public class P8ProcessService extends P8HelperServices
{
	private static final LogInfo LOG_OBJECT = new LogInfo(P8ProcessService.class);

	/**
	 * This method is used for launching a workflow given by asWorkflowName and
	 * properties mentioned in aohmReqdWorkflowProperties
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
		String lsStartTime = CommonUtil.getCurrentTimeInMilliSec();
		HashMap loHmReqExceProp = new HashMap();
		String lsWFWobNo = null;

		VWSession loVWSession = filenetConnection.getPESession(aoUserSession);
		loHmReqExceProp.put("filenetSession", aoUserSession.getObjectStoreName());
		loHmReqExceProp.put("asWorkflowName", asWorkflowName);
		loHmReqExceProp.put("aoHmReqdWorkflowProperties", aoHmReqdWorkflowProperties);

		LOG_OBJECT.Debug("Entered P8ProcessService.launchWorkflow() with parameters::" + loHmReqExceProp.toString());

		if (null == loVWSession)
		{
			ApplicationException loAppex = new ApplicationException("VWSession is not found");
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Debug("VWSession is not found::", loAppex);
			throw loAppex;
		}

		try
		{
			lsWFWobNo = peOperationHelper.launchWorkflow(loVWSession, asWorkflowName, aoHmReqdWorkflowProperties,
					P8Constants.PE_TASK_UNASSIGNED);
			setMoState("Workflow launched::" + lsWFWobNo);
			LOG_OBJECT.Debug("Worklow launched with WobNo::" + lsWFWobNo);

			String lsEndTime = CommonUtil.getCurrentTimeInMilliSec();
			float liTimediff = CommonUtil.timeDiff(CommonUtil.getItemDateInMIlisec(lsStartTime),
					CommonUtil.getItemDateInMIlisec(lsEndTime));
			LOG_OBJECT.Debug("P8ProcessService: method: launchWorkflow. Time Taken(seconds):: " + liTimediff);
			LOG_OBJECT.Debug("Exited P8ProcessService.launchWorkflow(). Returned lsWFWobNo::" + lsWFWobNo);
			return lsWFWobNo;
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while launching workflow");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessService.launchWorkflow()::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error while launching workflow");
			ApplicationException loAppex = new ApplicationException("Error while launching workflow : ", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessService.launchWorkflow()::", loAppex);
			throw loAppex;
		}
	}

	/**
	 * This function fetches all work items from queue and returns a hashmap of
	 * all properties specified in ahmRequiredProps
	 * 
	 * @param aoUserSession a user bean having information about user
	 * @param aoHmRequiredProps map containing required properties information
	 * @return HashMap map containing queue work items
	 * @throws ApplicationException If an application exception occurred
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public HashMap getQueueWorkItems(P8UserSession aoUserSession, HashMap aoHmRequiredProps)
			throws ApplicationException
	{

		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("filenetSession", aoUserSession.getObjectStoreName());
		loHmReqExceProp.put("aoHmRequiredProps", aoHmRequiredProps);

		HashMap loHmWorkItemList = null;

		LOG_OBJECT.Debug("Entered P8ProcessService.getQueueWorkItems() with parameters::" + loHmReqExceProp.toString());

		// Get FileNet PE session
		VWSession loVWSession = filenetConnection.getPESession(aoUserSession);

		if (null == loVWSession)
		{
			ApplicationException loAppex = new ApplicationException("VWSession is not found");
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Debug("VWSession is not found::", loAppex);
			throw loAppex;
		}

		try
		{
			// Call the data-provider class
			loHmWorkItemList = peOperationHelper.getQueueWorkitems(loVWSession, P8Constants.HSS_QUEUE_NAME, "",
					aoHmRequiredProps, null);
			setMoState("All work items from queue " + loHmWorkItemList.toString());
			LOG_OBJECT.Debug("Exited P8ProcessService.getQueueWorkItems(). Returned loHmWorkItemList::"
					+ loHmWorkItemList.toString());
			loVWSession.logoff();
			loVWSession = null;
			return loHmWorkItemList;
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while fetching all work items from queue ");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessService.getQueueWorkItems()::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error while fetching all work items from queue ");
			ApplicationException loAppex = new ApplicationException(
					"Error while fetching all work items from queue  : ", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessService.getQueueWorkItems()::", loAppex);
			throw loAppex;
		}
	}

	/**
	 * This function fetches all work items from queue and returns a hashmap of
	 * all properties specified in ahmRequiredProps based on the filter
	 * ahmFilter
	 * 
	 * @param aoUserSession a user bean having information about user
	 * @param aoHmRequiredProps map containing required properties
	 * @param aoHmFilter map containing filter properties
	 * @return HashMap map containing filtered queue work items
	 * @throws ApplicationException If an application exception occurred
	 */

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public HashMap getFilteredWorkItemsQueue(P8UserSession aoUserSession, HashMap aoHmRequiredProps, HashMap aoHmFilter)
			throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		HashMap loHmWorkItemList = null;
		loHmReqExceProp.put("filenetSession", aoUserSession.getObjectStoreName());
		loHmReqExceProp.put("aoHmRequiredProps", aoHmRequiredProps);
		loHmReqExceProp.put("aoHmFilter", aoHmFilter);

		LOG_OBJECT.Debug("Entered P8ProcessService.getFilteredWorkItemsQueue() with parameters::"
				+ loHmReqExceProp.toString());
		// Get FileNet PE session
		VWSession loVWSession = filenetConnection.getPESession(aoUserSession);
		if (null == loVWSession)
		{
			ApplicationException loAppex = new ApplicationException("VWSession is not found");
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Debug("VWSession is not found::", loAppex);
			throw loAppex;
		}

		try
		{
			// Call the data-provider class
			loHmWorkItemList = peOperationHelper.getQueueWorkitems(loVWSession, P8Constants.HSS_QUEUE_NAME, "",
					aoHmRequiredProps, aoHmFilter);
			setMoState("Work items from queue after applying filter" + loHmWorkItemList);
			LOG_OBJECT.Debug("Exited P8ProcessService.getFilteredWorkItemsQueue(). Returned loHmWorkItemList::"
					+ loHmWorkItemList.toString());
			loVWSession.logoff();
			loVWSession = null;
			return loHmWorkItemList;
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error in getting work items from queue after applying filter ");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessService.getFilteredWorkItemsQueue()::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error in getting work items from queue after applying filter ");
			ApplicationException loAppex = new ApplicationException(
					"Error while fetching work items from queue after applying filter : ", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessService.getFilteredWorkItemsQueue()::", loAppex);
			throw loAppex;
		}
	}

	/**
	 * This function gets the work item details for the asWobNum and returns the
	 * properties mentioned in ahmRequiredProps
	 * 
	 * @param aoUserSession a user bean having information about user
	 * @param asWobNum a string value of work flow Id
	 * @param aoHmRequiredProps map containing required properties
	 * @return HashMap map containing work item details
	 * @throws ApplicationException If an application exception occurred
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public HashMap getWorkItemDetails(P8UserSession aoUserSession, String asWobNum, HashMap aoHmRequiredProps)
			throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		HashMap loHmWorkItemList = null;
		loHmReqExceProp.put("filenetSession", aoUserSession.getObjectStoreName());
		loHmReqExceProp.put("asWobNum", asWobNum);
		loHmReqExceProp.put("aoHmRequiredProps", aoHmRequiredProps);
		LOG_OBJECT
				.Debug("Entered P8ProcessService.getWorkItemDetails() with parameters::" + loHmReqExceProp.toString());

		// Get FileNet PE session
		VWSession loVWSession = filenetConnection.getPESession(aoUserSession);
		if (null == loVWSession)
		{
			ApplicationException loAppex = new ApplicationException("VWSession is not found");
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Debug("VWSession is not found::", loAppex);
			throw loAppex;
		}

		try
		{
			// Call the data-provider class
			loHmWorkItemList = peOperationHelper.getWorkItemDetails(loVWSession, P8Constants.HSS_QUEUE_NAME,
					P8Constants.PROPERTY_PE_WOBNUMBER, asWobNum, aoHmRequiredProps);
			setMoState("Work item details" + loHmWorkItemList);
			LOG_OBJECT.Debug("Exited P8ProcessService.getWorkItemDetails(). Returned loHmWorkItemList::"
					+ loHmWorkItemList.toString());
			loVWSession.logoff();
			loVWSession = null;
			return loHmWorkItemList;
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while getting the work item details for the Wob Number:" + asWobNum);
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessService.getWorkItemDetails()::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error while getting the work item details for the Wob Number:" + asWobNum);
			ApplicationException loAppex = new ApplicationException(
					"Error while getting the work item details for the Wob Num : " + asWobNum, aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessService.getWorkItemDetails()::", loAppex);
			throw loAppex;
		}
	}

	/**
	 * This function gets all the work item details for the asWobNum
	 * 
	 * @param aoUserSession a user bean having information about user
	 * @param asWobNum a string value of work flow Id
	 * @return HashMap a map containing work item details
	 * @throws ApplicationException If an application exception occurred
	 */
	@SuppressWarnings(
	{ "rawtypes" })
	public HashMap getWorkItemDetails(P8UserSession aoUserSession, String asWobNum) throws ApplicationException
	{
		// Call the above method getWorkItemDetails with the required properties
		// as null. It returns all the properties
		return getWorkItemDetails(aoUserSession, asWobNum, null);

	}

	/**
	 * This function assigns the work item to user asUserName
	 * 
	 * @param aoUserSession a user bean having information about user
	 * @param aoWobNumbers an arraylist of work flow numbers
	 * @param asUserName a string value of user name
	 * @return HashMap a map containing information about work items assigned to
	 *         user
	 * @throws ApplicationException If an application exception occurred
	 */

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public HashMap assign(P8UserSession aoUserSession, ArrayList<String> aoWobNumbers, String asUserName,
			String asSessionUserName, String asUserForAudit) throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		HashMap loAssignResultList = new HashMap();
		loHmReqExceProp.put("filenetSession", aoUserSession.getObjectStoreName());
		loHmReqExceProp.put("aoWobNumbers", aoWobNumbers);
		loHmReqExceProp.put("asUserName", asUserName);
		loHmReqExceProp.put("asSessionUserName", asSessionUserName);
		LOG_OBJECT.Debug("Entered P8ProcessService.assign() with parameters::" + loHmReqExceProp.toString());

		if (aoWobNumbers.isEmpty())
		{
			ApplicationException loAppex = new ApplicationException("aoWobNumbers is empty");
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Debug("aoWobNumbers is empty::", loAppex);
			throw loAppex;
		}
		if (asUserName == null || asUserName.equalsIgnoreCase(""))
		{
			ApplicationException loAppex = new ApplicationException("asUserName is null");
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Debug("asUserName is empty::", loAppex);
			throw loAppex;
		}

		try
		{
			// Fetching PE Session
			VWSession loVWSession = filenetConnection.getPESession(aoUserSession);
			if (null == loVWSession)
			{
				ApplicationException loAppex = new ApplicationException("VWSession is not found");
				loAppex.setContextData(loHmReqExceProp);
				LOG_OBJECT.Debug("VWSession is not found::", loAppex);
				throw loAppex;
			}

			// For each WOB no. provided, call the assign method of the data
			// provider layer
			Iterator loItr = aoWobNumbers.iterator();
			while (loItr.hasNext())
			{
				String lsWobNum = (String) loItr.next();
				// assign the task and get the hashmap of wob numbers
				HashMap loHmWobNos = peOperationHelper.assign(loVWSession, lsWobNum, asUserName, asSessionUserName,
						P8Constants.HSS_QUEUE_NAME, asUserForAudit);
				// hashmap of hasmap containing the wob no. as the key, and the
				// hashmap returned from the assign function as value
				loAssignResultList.put(lsWobNum, loHmWobNos);
			}
			setMoState("Work items assigned to user" + loAssignResultList);
			LOG_OBJECT.Debug("Work items::" + aoWobNumbers.toString() + " assigned to user::" + asUserName);
			LOG_OBJECT.Debug("Exited P8ProcessService.assign(). Returned loAssignResultList::"
					+ loAssignResultList.toString());
			loVWSession.logoff();
			loVWSession = null;
			return loAssignResultList;
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while assigning the work items to user: " + asUserName);
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessService.assign()::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error while assigning the work item to user: " + asUserName);
			ApplicationException loAppex = new ApplicationException("Error while assigning the work item to user: "
					+ asUserName, aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessService.assign()::", loAppex);
			throw loAppex;
		}
	}

	/**
	 * This function returns all the child work items of the work item
	 * asParentWobNo The parameter asParentWobNo refers to the Wob no. of the BR
	 * application It returns all the properties of the child section work items
	 * of the given BR application in the form of an hashmap
	 * 
	 * @param aoUserSession a user bean having information about user
	 * @param aoHmRequiredProps a map containing required properties information
	 * @param asParentWobNo a string value of parent work flow number
	 * @return HashMap a map containing child work items
	 * @throws ApplicationException If an application exception occurred
	 */

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public HashMap getChildWorkItems(P8UserSession aoUserSession, HashMap aoHmRequiredProps, String asParentWobNo)
			throws ApplicationException
	{

		HashMap loHmReqExceProp = new HashMap();
		HashMap loHmWorkItemList = null;
		loHmReqExceProp.put("filenetSession", aoUserSession.getObjectStoreName());
		loHmReqExceProp.put("aoHmRequiredProps", aoHmRequiredProps);
		loHmReqExceProp.put("asParentWobNo", asParentWobNo);
		LOG_OBJECT.Debug("Entered P8ProcessService.getChildWorkItems() with parameters::" + loHmReqExceProp.toString());

		// Get FileNet PE session
		VWSession loVWSession = filenetConnection.getPESession(aoUserSession);
		if (null == loVWSession)
		{
			ApplicationException loAppex = new ApplicationException("VWSession is not found");
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Debug("VWSession is not found::", loAppex);
			throw loAppex;
		}

		try
		{
			HashMap loHmFilter = new HashMap();
			// The parent wob no. is also put in the hashmap
			loHmFilter.put(P8Constants.PROPERTY_PE_PARENT_APP_WOB_NO, asParentWobNo);
			// Call the data-provider class
			loHmWorkItemList = peOperationHelper.getSectionWorkItems(loVWSession, P8Constants.HSS_QUEUE_NAME, "",
					aoHmRequiredProps, loHmFilter);
			setMoState("List of All the child work items of the work item" + loHmWorkItemList);

			LOG_OBJECT.Debug("Exited P8ProcessService.getChildWorkItems(). Returned loHmWorkItemList::"
					+ loHmWorkItemList.toString());
			loVWSession.logoff();
			loVWSession = null;
			return loHmWorkItemList;
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while returning all the child work items of the work item as given in the Parent Wob Number "
					+ asParentWobNo);
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessService.getChildWorkItems()::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error while returning all the child work items of the work item as given in the Parent Wob Number "
					+ asParentWobNo);
			ApplicationException loAppex = new ApplicationException(
					"Error while returning all the child work items of the work item : ", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessService.getChildWorkItems()::", loAppex);
			throw loAppex;
		}
	}

	/**
	 * This function returns the properties of a given parent work item as given
	 * in asParentWobNo
	 * 
	 * @param aoUserSession a user bean having information about user
	 * @param aoHmRequiredProps a map containing required properties information
	 * @param asParentWobNo a string value of parent work flow number
	 * @return HashMap a map containing parent work items
	 * @throws ApplicationException If an application exception occurred
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public HashMap getParentWorkItem(P8UserSession aoUserSession, HashMap aoHmRequiredProps, String asParentWobNo)
			throws ApplicationException
	{

		HashMap loHmReqExceProp = new HashMap();
		HashMap loHmWorkItemList = null;

		// Get FileNet PE session
		VWSession loVWSession = filenetConnection.getPESession(aoUserSession);
		loHmReqExceProp.put("filenetSession", aoUserSession.getObjectStoreName());
		loHmReqExceProp.put("aoHmRequiredProps", aoHmRequiredProps);
		loHmReqExceProp.put("asParentWobNo", asParentWobNo);
		LOG_OBJECT.Debug("Entered P8ProcessService.getParentWorkItem() with parameters::" + loHmReqExceProp.toString());
		if (null == loVWSession)
		{
			ApplicationException loAppex = new ApplicationException("VWSession is not found");
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Debug("VWSession is not found::", loAppex);
			throw loAppex;
		}

		try
		{
			HashMap loHmFilter = new HashMap();
			loHmFilter.put(P8Constants.PROPERTY_PE_WOBNUMBER, asParentWobNo);
			// Call the data-provider class
			loHmWorkItemList = peOperationHelper.getQueueWorkitems(loVWSession, P8Constants.HSS_QUEUE_NAME, "",
					aoHmRequiredProps, loHmFilter);
			setMoState("Properties of a given parent work item" + loHmWorkItemList);
			LOG_OBJECT.Debug("Exited P8ProcessService.getParentWorkItem(). Returned loHmWorkItemList::"
					+ loHmWorkItemList.toString());
			loVWSession.logoff();
			loVWSession = null;
			return loHmWorkItemList;
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while returning the properties of a parent work item: " + asParentWobNo);
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessService.getParentWorkItem()::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error while returning the properties of a parent work item: " + asParentWobNo);
			ApplicationException loAppex = new ApplicationException(
					"Error while returning the properties of a parent work item : " + asParentWobNo, aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessService.getParentWorkItem()::", loAppex);
			throw loAppex;
		}
	}

	/**
	 * This method is used for finishing a child task. This method is only
	 * called on finishing all tasks except BR task
	 * 
	 * @param aoUserSession a user bean having information about user
	 * @param asChildWobNo a string value of child work flow no
	 * @param asChildStatus a string value of child status
	 * @return HashMap a map containing finished child tasks
	 * @throws ApplicationException If an application exception occurred
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public HashMap finishChildTask(P8UserSession aoUserSession, String asChildWobNo, String asChildStatus)
			throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		HashMap loHmFinishResult = null;
		loHmReqExceProp.put("filenetSession", aoUserSession.getObjectStoreName());
		loHmReqExceProp.put("asChildWobNo", asChildWobNo);
		loHmReqExceProp.put("asChildStatus", asChildStatus);
		LOG_OBJECT.Debug("Entered P8ProcessService.finishChildTask() with parameters::" + loHmReqExceProp.toString());

		// Get FileNet PE session
		VWSession loVWSession = filenetConnection.getPESession(aoUserSession);
		if (null == loVWSession)
		{
			ApplicationException loAppex = new ApplicationException("VWSession is not found");
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Debug("VWSession is not found", loAppex);
			throw loAppex;
		}

		try
		{
			// Call the data-provider class
			loHmFinishResult = peOperationHelper.finishChildTask(loVWSession, asChildWobNo, asChildStatus,
					P8Constants.HSS_QUEUE_NAME);
			setMoState("Child task finished" + loHmFinishResult);
			LOG_OBJECT.Debug("Child task with WOB No ::" + asChildWobNo + "finished.");
			LOG_OBJECT.Debug("Exited P8ProcessService.finishChildTask(). Returned loHmFinishResult::"
					+ loHmFinishResult.toString());
			loVWSession.logoff();
			loVWSession = null;
			return loHmFinishResult;
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while  finishing a child task.");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessService.finishChildTask()::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error while  finishing a child task.");
			ApplicationException loAppex = new ApplicationException("Error while  finishing a child task.", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessService.finishChildTask()::", loAppex);
			throw loAppex;
		}
	}

	/**
	 * This method is used for finishing a parent BR task
	 * 
	 * @param aoUserSession a user bean having information about user
	 * @param asParentWobNo a string value of parent work flow no
	 * @return String a string value of parent task status
	 * @throws ApplicationException If an application exception occurred
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public String finishParentTask(P8UserSession aoUserSession, String asParentWobNo) throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		String lsParentStatus = null;
		loHmReqExceProp.put("filenetSession", aoUserSession.getObjectStoreName());
		loHmReqExceProp.put("asParentWobNo", asParentWobNo);
		LOG_OBJECT.Debug("Entered P8ProcessService.finishParentTask() with parameters::" + loHmReqExceProp.toString());

		// Get FileNet PE session
		VWSession loVWSession = filenetConnection.getPESession(aoUserSession);
		if (null == loVWSession)
		{
			ApplicationException loAppex = new ApplicationException("VWSession is not found");
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Debug("VWSession is not found", loAppex);
			throw loAppex;
		}

		try
		{
			// Call the data-provider class
			lsParentStatus = peOperationHelper.finishParentTask(loVWSession, asParentWobNo, P8Constants.HSS_QUEUE_NAME);
			setMoState("Parent BR task finished with status:" + lsParentStatus);
			LOG_OBJECT.Debug("Parent BR task finished wih status::" + lsParentStatus);
			LOG_OBJECT.Debug("Exited P8ProcessService.finishParentTask(). Returned lsParentStatus::" + lsParentStatus);
			loVWSession.logoff();
			loVWSession = null;
			return lsParentStatus;
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while   finishing a parent BR task.");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessService.finishParentTask()::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error while   finishing a parent BR task.");
			ApplicationException loAppex = new ApplicationException("Error while finishing a parent BR task.", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessService.finishParentTask()::", loAppex);
			throw loAppex;
		}
	}

	/**
	 * This method is used for finishing a child task. This method is only
	 * called on finishing all SC tasks
	 * 
	 * @param aoUserSession a user bean having information about user
	 * @param asChildWobNo a string value of child work flow no
	 * @return HashMap a map containing finished SC withdrawl task status
	 * @throws ApplicationException If an application exception occurred
	 */

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public HashMap finishSCWithDrawlTask(P8UserSession aoUserSession, String asChildWobNo) throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		HashMap loHmFinishResult = null;
		loHmReqExceProp.put("filenetSession", aoUserSession.getObjectStoreName());
		loHmReqExceProp.put("asChildWobNo", asChildWobNo);
		LOG_OBJECT.Debug("Entered P8ProcessService.finishSCWithDrawlTask() with parameters::"
				+ loHmReqExceProp.toString());

		// Get FileNet PE session
		VWSession loVWSession = filenetConnection.getPESession(aoUserSession);
		if (null == loVWSession)
		{
			ApplicationException loAppex = new ApplicationException("VWSession is not found");
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Debug("VWSession is not found", loAppex);
			throw loAppex;
		}
		try
		{
			// Call the data-provider class
			loHmFinishResult = peOperationHelper.finishSCWithDrawlTask(loVWSession, asChildWobNo,
					P8Constants.HSS_QUEUE_NAME);
			setMoState("Child task finished" + loHmFinishResult);
			LOG_OBJECT.Debug("Child task with WOB No::" + asChildWobNo + "finished.");

			LOG_OBJECT.Debug("Exited P8ProcessService.finishSCWithDrawlTask(). Returned loHmFinishResult::"
					+ loHmFinishResult.toString());
			// The HashMap loHmFinishResult consists of 2 entries. The 1st entry
			// is
			// "Terminated", which has the value true or false
			// The 2nd entry is "ServiceIDs" , which contains an arraylist of
			// all
			// terminated SC ids
			return loHmFinishResult;
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while  finishing a child task.");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessService.finishChildTask()::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error while  finishing a child task.");
			ApplicationException loAppex = new ApplicationException("Error while  finishing a child task.", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessService.finishChildTask()::", loAppex);
			throw loAppex;
		}
	}

	/**
	 * This method is used for finishing a child task. This method is only
	 * called on finishing all SC tasks
	 * 
	 * @param aoUserSession a user bean having information about user
	 * @param asChildWobNo a string value of child work flow no
	 * @return a map containing finished BR withdrawl task status
	 * @throws ApplicationException If an application exception occurred
	 */

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public HashMap finishBRWithDrawlTask(P8UserSession aoUserSession, String asBRWobNo) throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		HashMap loHmFinishResult = null;
		loHmReqExceProp.put("filenetSession", aoUserSession.getObjectStoreName());
		loHmReqExceProp.put("asChildWobNo", asBRWobNo);
		LOG_OBJECT.Debug("Entered P8ProcessService.finishBRWithDrawlTask() with parameters::"
				+ loHmReqExceProp.toString());

		// Get FileNet PE session
		VWSession loVWSession = filenetConnection.getPESession(aoUserSession);
		if (null == loVWSession)
		{
			ApplicationException loAppex = new ApplicationException("VWSession is not found");
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Debug("VWSession is not found::", loAppex);
			throw loAppex;
		}

		try
		{
			// Call the data-provider class
			loHmFinishResult = peOperationHelper.finishBRWithDrawlTask(loVWSession, asBRWobNo,
					P8Constants.HSS_QUEUE_NAME);
			setMoState("BR task finished" + loHmFinishResult);
			LOG_OBJECT.Debug("BR Task with WOB No::" + asBRWobNo + "finished.");
			LOG_OBJECT.Debug("Exited P8ProcessService.finishBRWithDrawlTask(). Returned loHmFinishResult::"
					+ loHmFinishResult.toString());
			loVWSession.logoff();
			loVWSession = null;
			// The HashMap loHmFinishResult consists of 2 entries. The 1st entry
			// is
			// "Terminated", which has the value true or false
			// The 2nd entry is "SectionIDs" , which contains an arraylist of
			// all
			// terminated section ids
			return loHmFinishResult;
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while  finishing a child task.");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessService.finishChildTask()::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error while  finishing a child task.");
			ApplicationException loAppex = new ApplicationException("Error while  finishing a child task.", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessService.finishChildTask()::", loAppex);
			throw loAppex;
		}
	}

	/**
	 * This method is used for unlocking an work item. Its called for Return to
	 * Inbox
	 * 
	 * @param aoUserSession a user bean having information about user
	 * @param asWobNo a string value of workflow no
	 * @return a boolean value of unlock work Item
	 * @throws ApplicationException If an application exception occurred
	 */

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public boolean unlockWorkItem(P8UserSession aoUserSession, String asWobNo) throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		boolean lbResult;
		loHmReqExceProp.put("filenetSession", aoUserSession.getObjectStoreName());
		loHmReqExceProp.put("asWobNo", asWobNo);
		LOG_OBJECT.Debug("Entered P8ProcessService.unlockWorkItem() with parameters::" + loHmReqExceProp.toString());

		// Get FileNet PE session
		VWSession loVWSession = filenetConnection.getPESession(aoUserSession);
		if (null == loVWSession)
		{
			ApplicationException loAppex = new ApplicationException("VWSession is not found");
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Debug("VWSession is not found", loAppex);
			throw loAppex;
		}

		try
		{
			// Call the data-provider class
			lbResult = peOperationHelper.unlockWorkItem(loVWSession, asWobNo, P8Constants.HSS_QUEUE_NAME);
			setMoState("Unlocked an work item" + lbResult);
			LOG_OBJECT.Debug("Unlocked an work item with wob no::" + asWobNo);

			LOG_OBJECT.Debug("Exited P8ProcessService.unlockWorkItem(). Returned lbResult::" + lbResult);
			LOG_OBJECT.Debug("Exited P8ProcessService.unlockWorkItem(). Returned lbResult::" + lbResult);
			loVWSession.logoff();
			loVWSession = null;
			return lbResult;
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while unlocking an work item");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessService.unlockWorkItem()", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error while unlocking an work item");
			ApplicationException loAppex = new ApplicationException("Error while unlocking an work item", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessService.unlockWorkItem()::", loAppex);
			throw loAppex;
		}
	}

	/**
	 * This method is used for unlocking a parent work item when the child task
	 * is opened
	 * 
	 * @param aoUserSession a user bean having information about user
	 * @param asChildWobNo a string value of child work flow no
	 * @return a boolean value of unlock parent work Item
	 * @throws ApplicationException If an application exception occurred
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public boolean unlockParentWorkItem(P8UserSession aoUserSession, String asChildWobNo) throws ApplicationException
	{
		boolean lbResult;
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("filenetSession", aoUserSession.getObjectStoreName());
		loHmReqExceProp.put("asChildWobNo", asChildWobNo);
		LOG_OBJECT.Debug("Entered P8ProcessService.unlockParentWorkItem() with parameters::"
				+ loHmReqExceProp.toString());

		// Get FileNet PE session
		VWSession loVWSession = filenetConnection.getPESession(aoUserSession);
		if (null == loVWSession)
		{
			ApplicationException loAppex = new ApplicationException("VWSession is not found");
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Debug("VWSession is not found::", loAppex);
			throw loAppex;
		}

		try
		{
			// Call the data-provider class
			lbResult = peOperationHelper.unlockParentWorkItem(loVWSession, asChildWobNo, P8Constants.HSS_QUEUE_NAME);
			setMoState("When the child task is opened, parent work item is unlocked" + lbResult);
			LOG_OBJECT.Debug("Exited P8ProcessService.unlockParentWorkItem(). Returned lbResult::" + lbResult);
			loVWSession.logoff();
			loVWSession = null;
			return lbResult;
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while unlocking a parent work item when the child task is opened");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessService.unlockParentWorkItem()::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error while unlocking a parent work item when the child task is opened");
			ApplicationException loAppex = new ApplicationException(
					"Error while unlocking a parent work item when the child task is opened", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessService.unlockParentWorkItem()::", loAppex);
			throw loAppex;
		}
	}

	/**
	 * This method is used for UI unlocking of BR work items
	 * 
	 * @param aoUserSession a user bean having information about user
	 * @param asParentWobNo a string value of parent wob no
	 * @param aosSectionIds an arraylist of section IDs
	 * @return a boolean value of unlock UIBR work Item
	 * @throws ApplicationException If an application exception occurred
	 */
	public boolean unlockUIBRWorkItems(P8UserSession aoUserSession, String asParentWobNo,
			ArrayList<String> aoSectionIds, String asUpdatedById) throws ApplicationException
	{
		String lsStartTime = CommonUtil.getCurrentTimeInMilliSec();
		HashMap loHmReqExceProp = new HashMap();
		boolean lbUnlockedUI = false;
		loHmReqExceProp.put("filenetSession", aoUserSession.getObjectStoreName());
		loHmReqExceProp.put("asParentWobNo", asParentWobNo);
		loHmReqExceProp.put("aoSectionIds", aoSectionIds);
		LOG_OBJECT.Debug("Entered P8ProcessService.unlockUIBRWorkItems() with parameters::"
				+ loHmReqExceProp.toString());

		// Get FileNet PE session
		VWSession loVWSession = filenetConnection.getPESession(aoUserSession);
		if (null == loVWSession)
		{
			ApplicationException loAppex = new ApplicationException("VWSession is not found");
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Debug("VWSession is not found::", loAppex);
			throw loAppex;
		}

		try
		{
			// Convert array-list to array
			String[] loSecIDsArray = new String[aoSectionIds.size()];
			loSecIDsArray = (String[]) aoSectionIds.toArray(loSecIDsArray);
			// Call the data-provider class
			lbUnlockedUI = peOperationHelper.unlockUIBRWorkItems(loVWSession, asParentWobNo, loSecIDsArray,
					asUpdatedById, P8Constants.HSS_QUEUE_NAME);
			setMoState("UI unlocking of BR work items:" + lbUnlockedUI);
			String lsEndTime = CommonUtil.getCurrentTimeInMilliSec();
			float liTimediff = CommonUtil.timeDiff(CommonUtil.getItemDateInMIlisec(lsStartTime),
					CommonUtil.getItemDateInMIlisec(lsEndTime));
			LOG_OBJECT.Debug("P8ProcessService: method: unlockUIBRWorkItems. Time Taken(seconds):: " + liTimediff);
			LOG_OBJECT.Debug("Exited P8ProcessService.unlockUIBRWorkItems(). Returned lbUnlockedUI::" + lbUnlockedUI);
			loVWSession.logoff();
			loVWSession = null;
			return lbUnlockedUI;
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while UI unlocking of BR work items");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessService.unlockUIBRWorkItems::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error while UI unlocking of BR work items");
			ApplicationException loAppex = new ApplicationException("Error while UI unlocking of BR work items", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessService.unlockUIBRWorkItems::", loAppex);
			throw loAppex;
		}
	}

	/******************* Code begins for Service Capacity and other workflows ***********************/

	/**
	 * This method is used for UI unlocking of SC work items
	 * 
	 * @param aoUserSession a user bean having information about user
	 * @param asParentWobNo a string value of parent wob no
	 * @param aosSectionIds an arraylist of section IDs
	 * @return a boolean value of unlock UISC work Item
	 * @throws ApplicationException If an application exception occurred
	 */

	public boolean unlockUISCWorkItems(P8UserSession aoUserSession, String asSCWobNo, String asUpdatedById)
			throws ApplicationException
	{
		String lsStartTime = CommonUtil.getCurrentTimeInMilliSec();
		HashMap loHmReqExceProp = new HashMap();
		boolean lbUnlockedUI = false;
		loHmReqExceProp.put("filenetSession", aoUserSession.getObjectStoreName());
		loHmReqExceProp.put("asParentWobNo", asSCWobNo);
		LOG_OBJECT.Debug("Entered P8ProcessService.unlockSCBRWorkItems() with parameters::"
				+ loHmReqExceProp.toString());

		// Get FileNet PE session
		VWSession loVWSession = filenetConnection.getPESession(aoUserSession);
		if (null == loVWSession)
		{
			ApplicationException loAppex = new ApplicationException("VWSession is not found");
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Debug("VWSession is not found::", loAppex);
			throw loAppex;
		}

		try
		{
			// Call the data-provider class
			lbUnlockedUI = peOperationHelper.unlockUISCWorkItems(loVWSession, asSCWobNo, asUpdatedById,
					P8Constants.HSS_QUEUE_NAME);
			setMoState("UI unlocking of BR work items:" + lbUnlockedUI);

			String lsEndTime = CommonUtil.getCurrentTimeInMilliSec();
			float liTimediff = CommonUtil.timeDiff(CommonUtil.getItemDateInMIlisec(lsStartTime),
					CommonUtil.getItemDateInMIlisec(lsEndTime));
			LOG_OBJECT.Debug("P8ProcessService: method: unlockUISCWorkItems. Time Taken(seconds):: " + liTimediff);
			LOG_OBJECT.Debug("Exited P8ProcessService.unlockSCBRWorkItems(). Returned lbUnlockedUI::" + lbUnlockedUI);
			loVWSession.logoff();
			loVWSession = null;
			return lbUnlockedUI;
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while UI unlocking of BR work items");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessService.unlockSCBRWorkItems::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error while UI unlocking of BR work items");
			ApplicationException loAppex = new ApplicationException("Error while UI unlocking of BR work items", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessService.unlockSCBRWorkItems::", loAppex);
			throw loAppex;
		}
	}

	/**
	 * This function is used for checking if the currently logged-in user is
	 * same as the current assigned user Returns true if the currently logged in
	 * user is same as the assigned user for the work item, else returns false
	 * 
	 * @param aoUserSession a user bean having information about user
	 * @param asWobNo a string value of wob no
	 * @param asCurrentUserName a string value of user name
	 * @return a boolean value of validate user
	 * @throws ApplicationException If an application exception occurred
	 */
	public boolean isValidUser(P8UserSession aoUserSession, String asWobNo, String asCurrentUserName)
			throws ApplicationException
	{
		boolean lbIsValid = false;
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("filenetSession", aoUserSession.getObjectStoreName());
		loHmReqExceProp.put("asWobNo", asWobNo);
		loHmReqExceProp.put("asCurrentUserName", asCurrentUserName);
		LOG_OBJECT.Debug("Entered P8ProcessService.isValidUser() with parameters : " + loHmReqExceProp.toString());
		if (asWobNo.isEmpty())
		{
			ApplicationException loAppex = new ApplicationException("asWobNo is empty");
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Debug("asWobNo is empty::", loAppex);
			throw loAppex;
		}
		if (asCurrentUserName == null || asCurrentUserName.equalsIgnoreCase(""))
		{
			ApplicationException loAppex = new ApplicationException("asCurrentUserName is null");
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Debug("asCurrentUserName is null::", loAppex);
			throw loAppex;
		}

		try
		{
			// Fetching PE Session
			VWSession loVWSession = filenetConnection.getPESession(aoUserSession);
			if (null == loVWSession)
			{
				ApplicationException loAppex = new ApplicationException("VWSession is not found");
				loAppex.setContextData(loHmReqExceProp);
				LOG_OBJECT.Debug("VWSession is not found::", loAppex);
				throw loAppex;
			}
			// Call the data-provider class to check for the validity of the
			// user
			lbIsValid = peOperationHelper.isValidUser(loVWSession, asWobNo, asCurrentUserName,
					P8Constants.HSS_QUEUE_NAME);
			setMoState("Currently logged-in user is same as the current assigned user:" + lbIsValid);
			LOG_OBJECT.Debug("Currently logged-in user is same as the current assigned user::" + lbIsValid);

			LOG_OBJECT.Debug("Exited P8ProcessService.isValidUser(). Returned lbIsValid::" + lbIsValid);
			loVWSession.logoff();
			loVWSession = null;
			return lbIsValid;
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while checking if the currently logged-in user is same as the current assigned user");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessService.isValidUser()::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error while checking if the currently logged-in user is same as the current assigned user");
			ApplicationException loAppex = new ApplicationException(
					"Error while checking if the currently logged-in user is same as the current assigned user", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessService.isValidUser()::", loAppex);
			throw loAppex;
		}
	}

	/**
	 * This function is used for terminating a work item
	 * 
	 * @param aoUserSession a user bean having information about user
	 * @param asWobNo a string value of wob no
	 * @return a boolean value of terminate work item
	 * @throws ApplicationException If an application exception occurred
	 */
	public boolean terminateWorkItem(P8UserSession aoUserSession, String asWobNo) throws ApplicationException
	{
		HashMap loReqExceProp = new HashMap();
		boolean lbTerminated = false;
		loReqExceProp.put("filenetSession", aoUserSession.getObjectStoreName());
		loReqExceProp.put("asWobNo", asWobNo);
		LOG_OBJECT.Debug("Entered P8ProcessService.terminateWorkItem() with parameters::" + loReqExceProp.toString());
		// Get FileNet PE session
		VWSession loVWSession = filenetConnection.getPESession(aoUserSession);
		if (null == loVWSession)
		{
			ApplicationException loAppex = new ApplicationException("VWSession is not found");
			loAppex.setContextData(loReqExceProp);
			LOG_OBJECT.Debug("VWSession is not found", loAppex);
			throw loAppex;
		}
		try
		{
			// Call the data-provider class
			lbTerminated = peOperationHelper.terminateWorkItem(loVWSession, asWobNo, P8Constants.HSS_QUEUE_NAME);

			LOG_OBJECT.Debug("Exited P8ProcessService.terminateWorkItem(). Returned lbTerminated::" + lbTerminated);
			loVWSession.logoff();
			loVWSession = null;
			return lbTerminated;
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while terminating work item");
			aoAppex.setContextData(loReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessService.terminateWorkItem()::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error while terminating work item");
			ApplicationException loAppex = new ApplicationException("Error while terminating work item", aoEx);
			loAppex.setContextData(loReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessService.terminateWorkItem()::", loAppex);
			throw loAppex;
		}
	}

	/**
	 * This function is used for creating a queue filter based on the task types
	 * hash-map
	 * 
	 * @param aoHmTaskTypes a map containing task types information
	 * @return a string value of queue filter
	 * @throws ApplicationException If an application exception occurred
	 */

	public String createQueueFilterFromHashMap(HashMap aoHmTaskTypes) throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("aoHmTaskTypes", aoHmTaskTypes);
		String lsQueueFilter = "";
		StringBuffer lsQueueFilterBuffer = new StringBuffer("");
		LOG_OBJECT.Debug("Entered P8ProcessService.createQueueFilterFromHashMap() with parameters : "
				+ loHmReqExceProp.toString());
		try
		{
			// Iterate for all the task types obtained in hashmap aoHmTaskTypes
			// and create queue filter
			Iterator loItTaskypes = aoHmTaskTypes.keySet().iterator();
			while (loItTaskypes.hasNext())
			{
				String lsTaskype = (String) loItTaskypes.next();
				// create the queue filter based on the used name and task type
				// received in aoHmTaskTypes
				lsQueueFilterBuffer.append(P8Constants.PROPERTY_PE_TASK_TYPE);
				lsQueueFilterBuffer.append(" = '");
				lsQueueFilterBuffer.append(lsTaskype);
				lsQueueFilterBuffer.append("'");
				if (loItTaskypes.hasNext())
				{
					lsQueueFilterBuffer.append(" OR ");
				}
			}// wend
			lsQueueFilter = lsQueueFilterBuffer.toString();
		}
		catch (Exception aoEx)
		{
			setMoState("Error while fetching all work items from queue ");
			ApplicationException loAppex = new ApplicationException(
					"Error while fetching all work items from queue  : ", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessService.getQueueWorkItems()", loAppex);
			throw loAppex;
		}
		LOG_OBJECT.Debug("Exited P8ProcessService.createQueueFilterFromHashMap(). Returned lsQueueFilter :- "
				+ lsQueueFilter);
		return lsQueueFilter;
	}

	/**
	 * This function is used for Force Full Suspension of task hash-map
	 * 
	 * @param aoUserSession a user bean having information about user
	 * @asWobNo a string value of task WF Number
	 * @asTaskType a string value of Process task Type
	 * @throws ApplicationException If an application exception occurred
	 */

	public boolean forcefullySuspendTaskItem(P8UserSession aoUserSession, String asWobNo, String asTaskType)
			throws ApplicationException
	{
		HashMap loReqProp = new HashMap();
		boolean lbTerminated = false;
		loReqProp.put("filenetSession", aoUserSession.getObjectStoreName());
		loReqProp.put("asWobNo", asWobNo);
		loReqProp.put("asTaskType", asTaskType);
		LOG_OBJECT.Debug("Entered P8ProcessService.forcefullySuspendTaskItem() with parameters::"
				+ loReqProp.toString());
		// Get FileNet PE session
		VWSession loVWSession = filenetConnection.getPESession(aoUserSession);
		if (null == loVWSession)
		{
			ApplicationException loAppex = new ApplicationException("VWSession is not found");
			loAppex.setContextData(loReqProp);
			LOG_OBJECT.Debug("VWSession is not found", loAppex);
			throw loAppex;
		}
		try
		{
			// Call the data-provider class
			lbTerminated = peOperationHelper.forcefullySuspendTaskItem(loVWSession, asWobNo, asTaskType,
					P8Constants.HSS_QUEUE_NAME);

			LOG_OBJECT.Debug("Exited P8ProcessService.terminateWorkItem(). Returned lbTerminated::" + lbTerminated);
			return lbTerminated;
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while terminating work item");
			aoAppex.setContextData(loReqProp);
			LOG_OBJECT.Error("Exception in P8ProcessService.terminateWorkItem()::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error while terminating work item");
			ApplicationException loAppex = new ApplicationException("Error while terminating work item", aoEx);
			loAppex.setContextData(loReqProp);
			LOG_OBJECT.Error("Exception in P8ProcessService.terminateWorkItem()::", loAppex);
			throw loAppex;
		}
	}
	/**
	 * This method is used to update default assignee
	 * @param aoUserSession
	 * @param asWFWobNo
	 * @param aoHmReqdWorkflowProperties
	 * @return loStatus
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public Boolean updateDefaultAssignee(P8UserSession aoUserSession, String asWFWobNo, HashMap aoHmReqdWorkflowProperties)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered P8ProcessService.updateDefaultAssignee() with parameters::"
				+ aoHmReqdWorkflowProperties.toString());
		LOG_OBJECT.Debug("Entered P8ProcessService.updateDefaultAssignee() with WobNum::"
				+ asWFWobNo);
		Boolean loStatus=false;
		VWSession loVWSession = filenetConnection.getPESession(aoUserSession);
		try
		{
			peOperationHelper.updateDefaultAssigneeRole(loVWSession, asWFWobNo, aoHmReqdWorkflowProperties,
					P8Constants.PE_TASK_UNASSIGNED);
			LOG_OBJECT.Debug("Exited P8ProcessService.updateDefaultAssignee(). Returned lsWFWobNo::" + asWFWobNo);
		}
		catch (Exception aoAppex)
		{
			setMoState("Error while updateDefaultAssignee");
		}
		return loStatus;
	}
	
	/** This method is added for Defect 7970. It updates the new 
	 *  procurement title into PSR/PCOF Tasks for procurement
	 *  in Planned status.
	 *  
	 * @param aoUserSession - Filenet Session Object
	 * @param asProcurementId - String ProcurementId
	 * @param aoHmReqdWorkflowProperties - HashMap 
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public Boolean updateTaskDetails(P8UserSession aoUserSession, String asProcurementId,
			HashMap aoHmReqdWorkflowProperties) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered P8ProcessService.updateTaskDetails() with ProcurmentId::" + asProcurementId);
		Boolean loStatus = false;
		VWSession loVWSession = filenetConnection.getPESession(aoUserSession);
		try
		{
			VWQueue loVWQueue = null;
			VWQueueQuery loVWQueueQuery = null;
			VWStepElement loStepElement = null;
			String lsQueueFilter = null;
			if (null != aoHmReqdWorkflowProperties)
			{
				lsQueueFilter = P8Constants.PE_WORKFLOW_PROCUREMENT_ID + "='" + asProcurementId + "'";
				loVWQueue = loVWSession.getQueue(P8Constants.HSS_QUEUE_NAME);
				int liFlag = loVWQueue.QUERY_READ_LOCKED;
				int liType = VWFetchType.FETCH_TYPE_QUEUE_ELEMENT;
				loVWQueueQuery = loVWQueue.createQuery(null, null, null, liFlag, lsQueueFilter, null, liType);
				VWQueueElement loVWQueueElement = (VWQueueElement) loVWQueueQuery.next(); // Query
				loStepElement = loVWQueueElement.fetchStepElement(true, true);
				loStepElement.setParameterValue(HHSConstants.PROPERTY_PE_PROCUREMENT_TITLE,
						(String) aoHmReqdWorkflowProperties.get(HHSConstants.PROPERTY_PE_PROCUREMENT_TITLE), false);
				loStepElement.doSave(true);
			}
			LOG_OBJECT.Debug("Exited P8ProcessService.updateTaskDetails()");
		}
		catch (Exception aoAppex)
		{
			setMoState("Error while P8ProcessService.updateTaskDetails()");
		}
		return loStatus;
	}
	
	/** This method is added in Release 6. It fetches the
	 * Work flow ID with respective to returnedPaymentId
	 * and terminated the work flow.
	 *  
	 * @param aoUserSession - Filenet Session Object
	 * @param asReturnedPaymentId - String ReturnedPaymentId
	 * @return loStatus - Boolean status
	 * @throws ApplicationException
	 */
	public Boolean terminateReturnedPaymentWF(P8UserSession aoUserSession, String asReturnedPaymentId,
			String asReturnedPaymentStatus) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered P8ProcessService.terminateReturnedPaymentWF() with parameters::"
				+ asReturnedPaymentId);
		Boolean loStatus = false;
		try
		{
			loStatus = peOperationHelper.terminateReturnedPaymentWF(aoUserSession, asReturnedPaymentId, asReturnedPaymentStatus);
			LOG_OBJECT.Debug("Exited P8ProcessService.terminateReturnedPaymentWF(). Returned Payment ID::" + asReturnedPaymentId);
		}
		catch (Exception aoAppex)
		{
			setMoState("Error while terminateReturnedPaymentWF");
		}
		return loStatus;
	}
	
	//Release 7 Start
	/**
	 * The method is added in Release 7. The method will dispatch automatically
	 * the work flows to next level.
	 * @param aoUserSession
	 * @param aoTaskDetailBean
	 * @param abAuditflag
	 * @return loStatus
	 * @throws ApplicationException
	 */
	public Boolean dispatchWF(P8UserSession aoUserSession,
			TaskDetailsBean aoTaskDetailBean, Boolean abAuditflag) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered P8Service.dispatchWF():::");
		Boolean loStatus = HHSConstants.BOOLEAN_FALSE;
		try
		{
			if (abAuditflag)
			{
				loStatus = peOperationHelper.dispatchWF(aoUserSession, aoTaskDetailBean);
			}
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while P8Service.dispatchWF()");
			throw aoAppex;
		}
		LOG_OBJECT.Debug("Exited P8Service.dispatchWF()");
		return loStatus;
	}
		//Release 7 End	
}
