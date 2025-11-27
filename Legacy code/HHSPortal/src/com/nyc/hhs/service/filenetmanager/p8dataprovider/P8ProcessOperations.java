package com.nyc.hhs.service.filenetmanager.p8dataprovider;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8services.P8HelperServices;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DateUtil;

import filenet.vw.api.VWException;
import filenet.vw.api.VWFetchType;
import filenet.vw.api.VWFieldType;
import filenet.vw.api.VWParameter;
import filenet.vw.api.VWQueue;
import filenet.vw.api.VWQueueElement;
import filenet.vw.api.VWQueueQuery;
import filenet.vw.api.VWSession;
import filenet.vw.api.VWStepElement;
import filenet.vw.api.VWWorkObject;

/**
 * This class is generally used to perform all P8 process operations on FileNet.
 * The methods of this class will be executed through P8ProcessServices.
 * 
 * Updated in Release 7- Methods Added(checkAutoAprrovalCompleteAndMerge,
 * setAuditForAutoApprovedStatus,getModificationWFAutoApproval,
 * dispatchModificationWF, createFilterForTask) for modification auto approval
 */

public class P8ProcessOperations extends P8HelperServices
{

	// LogInfo LogInfo = new LogInfo(P8ProcessOperations.class);
	private static final LogInfo LOG_OBJECT = new LogInfo(P8ProcessOperations.class);

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
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public String launchWorkflow(VWSession aoVWSession, String asWorkflowName, HashMap aoHmReqdWorkflowProperties,
			String asSessionUserName) throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put(HHSR5Constants.REQWORKFLOW_PROP, aoHmReqdWorkflowProperties);
		loHmReqExceProp.put(HHSR5Constants.AS_WORK_FLOW_NAME, asWorkflowName);
		LOG_OBJECT.Debug("Entered P8ProcessOperations.launchWorkflow() with parameters::" + loHmReqExceProp.toString());
		if (asWorkflowName == null || asWorkflowName.equalsIgnoreCase(""))
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

			String lsWobNo = isTaskExist(aoVWSession, asWorkflowName, aoHmReqdWorkflowProperties);
			LOG_OBJECT.Debug("Obtained lsWobNo = " + lsWobNo + " from isTaskExist");

			if (lsWobNo == null || lsWobNo.equalsIgnoreCase(""))
			{

				// create the workflow
				VWStepElement loStepElement = loVWSession.createWorkflow(asWorkflowName);
				if (loStepElement == null)
				{
					throwApplicationException("Error in launching worklow", loHmReqExceProp);
				}

				// ***** Added for the case where SC application is launched
				// directly
				// When workflow is launched through BR application on assigning
				// ,
				// the properties collection will contain the
				// PROPERTY_PE_ASSIGNED_TO
				// When it is launched directly through external application,
				// the
				// properties collection wont contain the
				// PROPERTY_PE_ASSIGNED_TO
				if (!aoHmReqdWorkflowProperties.containsKey(P8Constants.PROPERTY_PE_ASSIGNED_TO))
				{
					aoHmReqdWorkflowProperties.put(P8Constants.PROPERTY_PE_ASSIGNED_TO, P8Constants.PE_TASK_UNASSIGNED);
					aoHmReqdWorkflowProperties.put(P8Constants.PROPERTY_PE_ASSIGNED_TO_NAME,
							P8Constants.PE_TASK_UNASSIGNED);
				}
				// set the workflow properties
				loStepElement = peOperationHelper.setWorkFlowProperties(loStepElement, aoHmReqdWorkflowProperties);
				// dispatch the work item
				loStepElement.doDispatch();
				lsWobNo = loStepElement.getWorkflowNumber();
				// Do auditing for the launched workflow item
				updateAuditInfoDuringWorkFlowLaunch(asWorkflowName, aoHmReqdWorkflowProperties, asSessionUserName);

			}

			LOG_OBJECT.Debug("Exited P8ProcessOperations.launchWorkflow() . Returned lsWobNo ::" + lsWobNo);
			return lsWobNo;

		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.launchWorkflow()::", aoAppex);
			throw aoAppex;

		}
		catch (VWException aoVWEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in Launching Workflow:: " + asWorkflowName,
					aoVWEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.launchWorkflow()::", loAppex);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in launching workflow:: " + asWorkflowName,
					aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.launchWorkflow()::", loAppex);
			throw loAppex;
		}
	}

	/**
	 * This methods sets workflow properties
	 * 
	 * @param aoVWStepElem the VWStepElement object
	 * @param aoHmReqdOutputProps map containing required output properties
	 * @return VWStepElement VWStepElement object as output
	 * @throws ApplicationException
	 */
	public VWStepElement setWorkFlowProperties(VWStepElement aoVWStepElem, HashMap aoHmReqdOutputProps)
			throws ApplicationException
	{
		VWParameter[] loVWParams = null;
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("aoHmReqdOutputProps", aoHmReqdOutputProps.toString());
		LOG_OBJECT.Debug("Entered P8ProcessOperations.setWorkFlowProperties() with parameters ::"
				+ loHmReqExceProp.toString());
		try
		{
			if (aoVWStepElem == null)
			{
				throwApplicationException("VWStep Element is null", loHmReqExceProp);
			}

			// Get all the parameters
			loVWParams = aoVWStepElem.getParameters(VWFieldType.ALL_FIELD_TYPES,
					VWStepElement.FIELD_USER_AND_SYSTEM_DEFINED);
			// Iterate through all the fetched parameters and check if these
			// parameters are present in the collection aohmReqdOutputProps
			// Set the values for only the properties present in
			// aohmReqdOutputProps
			for (int liCount = 0; liCount < loVWParams.length; liCount++)
			{
				if (aoHmReqdOutputProps.containsKey(loVWParams[liCount].getName()))
				{
					// set the value of the parameter contained in the
					// aoHmReqdOutputProps hashmap
					aoVWStepElem = setVWParametersValue(aoVWStepElem, loVWParams[liCount], aoHmReqdOutputProps, liCount);
				}
			}
		}
		catch (ApplicationException aoAppex)
		{
			loHmReqExceProp.put("aohmReqdOutputProps", aoHmReqdOutputProps);
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.setWorkFlowProperties()::", aoAppex);
			throw aoAppex;

		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Cannot perform PE operation on FileNet", aoEx);
			loHmReqExceProp.put("aohmReqdOutputProps", aoHmReqdOutputProps);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.setWorkFlowProperties()::", loAppex);
			throw loAppex;
		}
		LOG_OBJECT.Debug("Exited P8ProcessOperations.setWorkFlowProperties(). Returned::" + aoVWStepElem.toString());
		// return the updated step element
		return aoVWStepElem;
	}

	/**
	 * This is the internal method for setting work item properties.
	 * 
	 * @param aoVwStepElement is the VWStepElement object
	 * @param aoParameter a VWParameter object
	 * @param aoHmReqdOutputProps map containing required output properties
	 * @param aiValue an integer value input
	 * @return VWStepElement Step Element VWStepElement object
	 * @throws ApplicationException
	 */

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private VWStepElement setVWParametersValue(VWStepElement aoVwStepElement, VWParameter aoParameter,
			HashMap aoHmReqdOutputProps, int aiValue) throws ApplicationException
	{
		String lsValue = "";
		Object loTemp = aoHmReqdOutputProps.get(aoParameter.getName());
		if (null != loTemp)
		{
			lsValue = loTemp.toString();
		}
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("aoHmReqdOutputProps", aoHmReqdOutputProps);
		loHmReqExceProp.put("aiValue", aiValue);
		loHmReqExceProp.put(aoParameter.getName(), lsValue);
		LOG_OBJECT.Debug("Entered P8ProcessOperations.setVWParametersValue() with VW parameter name::"
				+ aoParameter.getName() + " and value::" + lsValue);
		try
		{
			// Check for the data type of the parameter and set the values
			// accordingly
			switch (aoParameter.getFieldType())
			{
				case VWFieldType.FIELD_TYPE_INT:
					if (aoParameter.isArray())
					{
						Integer[] loTempArray = (Integer[]) (aoHmReqdOutputProps.get(aoParameter.getName()));
						aoVwStepElement.setParameterValue(aoParameter.getName(), loTempArray, true);
					}
					else
					{
						Long loLongValue = Long.valueOf(lsValue);
						int liValue = loLongValue.intValue();
						aoVwStepElement.setParameterValue(aoParameter.getName(), Integer.valueOf(liValue), true);
					}
					break;
				case VWFieldType.FIELD_TYPE_BOOLEAN:
					if (aoParameter.isArray())
					{
						Boolean[] loTempArray = (Boolean[]) (aoHmReqdOutputProps.get(aoParameter.getName()));
						aoVwStepElement.setParameterValue(aoParameter.getName(), loTempArray, true);
					}
					else
					{
						aoVwStepElement.setParameterValue(aoParameter.getName(), (Boolean.valueOf(lsValue)), true);
					}
					break;
				case VWFieldType.FIELD_TYPE_FLOAT:
					if (aoParameter.isArray())
					{
						Float[] loTempArray = (Float[]) (aoHmReqdOutputProps.get(aoParameter.getName()));
						aoVwStepElement.setParameterValue(aoParameter.getName(), loTempArray, true);
					}
					else
					{
						aoVwStepElement.setParameterValue(aoParameter.getName(), lsValue, true);
					}
					break;
				case VWFieldType.FIELD_TYPE_STRING:
					// Handle string arrays as well for Section Ids, Section
					// names and Status arrays
					if (aoParameter.isArray())
					{
						String[] loTempArray = (String[]) (aoHmReqdOutputProps.get(aoParameter.getName()));
						aoVwStepElement.setParameterValue(aoParameter.getName(), loTempArray, true);
					}
					else
					{
						aoVwStepElement.setParameterValue(aoParameter.getName(), lsValue, true);
					}
					break;
				case VWFieldType.FIELD_TYPE_TIME:
					try
					{
						DateFormat loDateFormat = new SimpleDateFormat(P8Constants.DATE_FORMAT);
						aoVwStepElement.setParameterValue(aoParameter.getName(), loDateFormat.parse(lsValue), true);
					}
					catch (ParseException aoEx)
					{
						aoVwStepElement.setParameterValue(aoParameter.getName(),
								aoHmReqdOutputProps.get(aoParameter.getName()), true);
					}
					break;
			}
		}
		catch (VWException aoVWEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"FileNet VWException While Setting Parameter Values", aoVWEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.setVWParametersValue()::", loAppex);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error While Setting Parameter Values", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.setVWParametersValue()::", loAppex);
			throw loAppex;
		}
		LOG_OBJECT.Debug("Value " + lsValue + " set for VW parameter::" + aoParameter.getName());
		LOG_OBJECT.Debug("Exited P8ProcessOperations.setVWParametersValue()");
		// return the updated step element
		return aoVwStepElement;
	}

	/**
	 * This is the method for fetching list of work items which are present in a
	 * particular queue. This method will also accept a HashMap based on which
	 * it will generate queue filter at runtime.
	 * 
	 * @param aoVWSession aoVWSession object
	 * @param asQueueName a string value of Queue Name
	 * @param asIndexName a string value of Index Name
	 * @param aoHmReqdOutputProps map containing required output properties
	 * @param aoHmFilter map containing filter properties
	 * @return HashMap map containing filtered queue work items
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public HashMap getQueueWorkitems(VWSession aoVWSession, String asQueueName, String asIndexName,
			HashMap aoHmReqdOutputProps, HashMap aoHmFilter) throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("asQueueName", asQueueName);
		loHmReqExceProp.put("asIndexName", asIndexName);
		loHmReqExceProp.put("aoHmReqdOutputProps", aoHmReqdOutputProps);
		loHmReqExceProp.put("aoHmFilter", aoHmFilter);
		LOG_OBJECT.Debug("Entered P8ProcessOperations.getQueueWorkitems() with parameters::"
				+ loHmReqExceProp.toString());

		VWQueueQuery loVWQueueQuery = null;
		VWQueue loVWQueue = null;
		HashMap loHmWorkItems = new HashMap();
		String lsQueueFilter = "";
		try
		{
			if (aoVWSession == null)
			{
				throwApplicationException("ERROR while fetching VWSession Object", loHmReqExceProp);
			}

			// fetch the queue object
			loVWQueue = aoVWSession.getQueue(asQueueName);
			if (loVWQueue == null)
			{
				throwApplicationException("Error While Fetching Queue", loHmReqExceProp);
			}

			int liFlag = loVWQueue.QUERY_READ_LOCKED;
			int liType = VWFetchType.FETCH_TYPE_QUEUE_ELEMENT;
			// create the queue filter based on the items received in aoHmFilter
			lsQueueFilter = createQueueFilter(aoHmFilter);
			// get only visible work items
			if ("".equals(lsQueueFilter))
			{
				lsQueueFilter = " taskVisibility = 1";
			}
			else
			{
				StringBuffer lsQueueFilterBuffer = new StringBuffer(lsQueueFilter);
				lsQueueFilterBuffer.append(" AND taskVisibility = 1");
				lsQueueFilter = lsQueueFilterBuffer.toString();
			}
			LOG_OBJECT.Debug("Creating query with queue filter::" + lsQueueFilter);

			// create query
			loVWQueueQuery = loVWQueue.createQuery(null, null, null, liFlag, lsQueueFilter, null, liType);
			// Iterate through the query results and put the values in the
			// hashmap lhmWorkItems
			while (loVWQueueQuery.hasNext())
			{
				VWQueueElement loVWQueueElement = (VWQueueElement) loVWQueueQuery.next();
				VWStepElement loStepElement = loVWQueueElement.fetchStepElement(false, true);
				if (loStepElement == null)
				{
					throwApplicationException("Error While Fetching StepElement from QueueElement", loHmReqExceProp);
				}
				// get the parameters for a step element in a hashmap
				HashMap loHmWorkItemProps = getVWParamtersValues(loStepElement, aoHmReqdOutputProps);
				// put the wob no and its corresponding properties in another
				// hashmap
				loHmWorkItems.put(loStepElement.getWorkflowNumber(), loHmWorkItemProps);
				LOG_OBJECT.Debug("Fetched the following properties for Wob no::" + loStepElement.getWorkflowNumber()
						+ " ::" + loHmWorkItemProps.toString());
			}
		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.getQueueWorkitems()::", aoAppex);
			throw aoAppex;
		}
		catch (VWException aoVWEx)
		{
			ApplicationException loAppex = new ApplicationException("Error While fetching Queue Elements", aoVWEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.getQueueWorkitems()::", loAppex);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error While fetching Queue Elements", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.getQueueWorkitems()::", loAppex);
			throw loAppex;
		}
		// return the hashmap of properties
		LOG_OBJECT.Debug("Exited P8ProcessOperations.getQueueWorkitems(). Returned loHmWorkItems::"
				+ loHmWorkItems.toString());
		return loHmWorkItems;
	}

	/**
	 * This is the method for fetching list of work items which are present in a
	 * particular queue. This method will also accept a HashMap based on which
	 * it will generate queue filter at runtime.
	 * 
	 * @param aoVWSession a VWSession object
	 * @param asQueueName a string value of Queue Name
	 * @param asIndexName a string value of Index Name
	 * @param aoHmReqdOutputProps map containing required output properties
	 * @param aoHmFilter map containing filter properties
	 * @return HashMap map containing Section Work Items
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public HashMap getSectionWorkItems(VWSession aoVWSession, String asQueueName, String asIndexName,
			HashMap aoHmReqdOutputProps, HashMap aoHmFilter) throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("asQueueName", asQueueName);
		loHmReqExceProp.put("asIndexName", asIndexName);
		loHmReqExceProp.put("aoHmReqdOutputProps", aoHmReqdOutputProps);
		loHmReqExceProp.put("aoHmFilter", aoHmFilter);

		VWQueueQuery loVWQueueQuery = null;
		VWQueue loVWQueue = null;
		HashMap loHmWorkItems = new HashMap();
		String lsQueueFilter = "";
		LOG_OBJECT.Debug("Entered P8ProcessOperations.getSectionWorkItems() with parameters::"
				+ loHmReqExceProp.toString());
		try
		{
			if (aoVWSession == null)
			{
				throwApplicationException("ERROR while fetching VWSession Object", loHmReqExceProp);
			}

			// fetch the queue object
			loVWQueue = aoVWSession.getQueue(asQueueName);
			if (loVWQueue == null)
			{
				throwApplicationException("Error While Fetching Queue", loHmReqExceProp);
			}

			// create the queue filter based on the items received in aoHmFilter
			if (null != aoHmFilter)
			{
				lsQueueFilter = createQueueFilter(aoHmFilter);
			}

			int liFlag = loVWQueue.QUERY_READ_LOCKED;
			int liType = VWFetchType.FETCH_TYPE_QUEUE_ELEMENT;
			loVWQueueQuery = loVWQueue.createQuery(null, null, null, liFlag, lsQueueFilter, null, liType);
			// Iterate through the query results and put the values in the
			// hashmap lhmWorkItems
			while (loVWQueueQuery.hasNext())
			{
				VWQueueElement loVWQueueElement = (VWQueueElement) loVWQueueQuery.next();
				VWStepElement loStepElement = loVWQueueElement.fetchStepElement(false, true);
				if (loStepElement == null)
				{
					throwApplicationException("Error While Fetching StepElement from QueueElement", loHmReqExceProp);
				}
				// get the parameters for a step element in a hashmap
				HashMap loHmWorkItemProps = getVWParamtersValues(loStepElement, aoHmReqdOutputProps);
				// put the wob no and its corresponding properties in another
				// hashmap
				loHmWorkItems.put(loStepElement.getWorkflowNumber(), loHmWorkItemProps);
				LOG_OBJECT.Debug("Fetched the following properties for Wob no::" + loStepElement.getWorkflowNumber()
						+ "::" + loHmWorkItemProps.toString());
			}
		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.getSectionWorkItems()::", aoAppex);
			throw aoAppex;
		}
		catch (VWException aoVWEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in getting section work items", aoVWEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.getSectionWorkItems()::", loAppex);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in getting section work items", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.getSectionWorkItems()::", loAppex);
			throw loAppex;
		}
		// return the hashmap of properties
		LOG_OBJECT.Debug("Exited P8ProcessOperations.getSectionWorkItems(). Returned loHmWorkItems::"
				+ loHmWorkItems.toString());
		return loHmWorkItems;
	}

	/**
	 * This is the method for fetching list of work items which are present in a
	 * particular queue. This method will also accept a HashMap based on which
	 * it will generate queue filter at runtime. This method is used for
	 * fetching the work items of the queue as per the filter mentioned in
	 * aohmFilter
	 * 
	 * @param aoVWSession a VWSession object
	 * @param asQueueName a string value of Queue Name
	 * @param asIndexName a string value of Index Name
	 * @param asIndexValue a string representation of Index Value
	 * @param aohmReqdOutputProps map containing required output properties
	 * @return HashMap map containing work item details
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public HashMap getWorkItemDetails(VWSession aoVWSession, String asQueueName, String asIndexName,
			String asIndexValue, HashMap aoHmReqdOutputProps) throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("asQueueName", asQueueName);
		loHmReqExceProp.put("asIndexName", asIndexName);
		loHmReqExceProp.put("asIndexValue", asIndexValue);
		loHmReqExceProp.put("aoHmReqdOutputProps", aoHmReqdOutputProps);
		LOG_OBJECT.Debug("Entered P8ProcessOperations.getWorkItemDetails() with parameters::"
				+ loHmReqExceProp.toString());

		VWQueueQuery loVWQueueQuery = null;
		VWQueue loVWQueue = null;
		HashMap loHmWorkItems = new HashMap();
		Object[] loMinVal = new Object[1];
		Object[] loMaxVal = new Object[1];
		HashMap loHmWorkItemProps = null;
		String lsQFilter = null;
		try
		{
			if (aoVWSession == null)
			{
				throwApplicationException("ERROR while fetching VWSession Object", loHmReqExceProp);
			}

			// fetch the queue object
			loVWQueue = aoVWSession.getQueue(asQueueName);
			if (loVWQueue == null)
			{
				throwApplicationException("Error While Fetching Queue", loHmReqExceProp);
			}

			int liFlag = loVWQueue.QUERY_MIN_VALUES_INCLUSIVE + loVWQueue.QUERY_MAX_VALUES_INCLUSIVE
					+ loVWQueue.QUERY_READ_LOCKED;
			int liType = VWFetchType.FETCH_TYPE_QUEUE_ELEMENT;
			loMinVal[0] = asIndexValue;
			loMaxVal[0] = asIndexValue;
			// create and execute the queue-query
			loVWQueueQuery = loVWQueue.createQuery(asIndexName, loMinVal, loMaxVal, liFlag, lsQFilter, null, liType);
			// Iterate through the query results and put the values in the
			// hashmap lhmWorkItems
			if (loVWQueueQuery.hasNext())
			{
				VWQueueElement loVWQueueElement = (VWQueueElement) loVWQueueQuery.next();
				VWStepElement loStepElement = loVWQueueElement.fetchStepElement(false, true);
				if (loStepElement == null)
				{
					throwApplicationException("Error While Fetching StepElement from QueueElement", loHmReqExceProp);
				}

				if (aoHmReqdOutputProps != null)
				{
					// fetch values of only those parameters specified in
					// aoHmReqdOutputProps
					loHmWorkItemProps = getVWParamtersValues(loStepElement, aoHmReqdOutputProps);
				}
				else
				{
					// fetch values of all the parameters for the step element
					loHmWorkItemProps = getVWParamtersValues(loStepElement);
				}
				// put the wob no and its corresponding fetched properties in a
				// hashmap
				loHmWorkItems.put(loStepElement.getWorkflowNumber(), loHmWorkItemProps);
			}
		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.getQueueWorkitems()::", aoAppex);
			throw aoAppex;
		}
		catch (VWException aoVWEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Error while fetching list of work items which are present in a particular queue::", aoVWEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.getQueueWorkitems()::", loAppex);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Error while fetching list of work items which are present in a particular queue::", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.getQueueWorkitems()::", loAppex);
			throw loAppex;
		}
		// return the hashmap of properties
		LOG_OBJECT.Debug("Exited P8ProcessOperations.getSectionWorkItems(). Returned loHmWorkItems::"
				+ loHmWorkItems.toString());
		return loHmWorkItems;
	}

	/**
	 * This method is used for fetching the properties of a given step element
	 * aoVWStepElement It fetches only the properties mentioned in the hashmap
	 * aohmRequiredProps
	 * 
	 * @param aoVWStepElement VW Step Element object
	 * @param aohmRequiredProps Required Props map
	 * @return HashMap VW Parameters Values map
	 * @throws ApplicationException
	 */

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public HashMap getVWParamtersValues(VWStepElement aoVWStepElement, HashMap aoHmRequiredProps)
			throws ApplicationException
	{

		HashMap loHmWorkItemDetails = new HashMap();
		String[] loAllParam = aoVWStepElement.getParameterNames();
		List<String> loAllParamList = Arrays.asList(loAllParam);

		try
		{
			Iterator loIt = aoHmRequiredProps.keySet().iterator();
			// iterate through all the properties in the hashmap
			// aoHmRequiredProps, fetch the property value and add it to
			// loHmWorkItemDetails
			while (loIt.hasNext())
			{
				String lsPropName = (String) loIt.next();
				if (loAllParamList.contains(lsPropName))
				{
					setVWParamValuesInHashMap(loHmWorkItemDetails, aoVWStepElement, lsPropName);
				}
			}

		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmWorkItemDetails);
			aoAppex.setContextData(aoHmRequiredProps);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.getVWParamtersValues()::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Error while fetching the properties of a given step element aoVWStepElement::" + aoVWStepElement,
					aoEx);
			loAppex.setContextData(loHmWorkItemDetails);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.getVWParamtersValues()::", loAppex);
			throw loAppex;
		}

		// Return the hashmap loHmWorkItemDetails which contains all the
		// properties of the given step element
		LOG_OBJECT.Debug("Exited P8ProcessOperations.getVWParamtersValues(). Returned loHmWorkItemDetails::"
				+ loHmWorkItemDetails.toString());
		return loHmWorkItemDetails;
	}

	/**
	 * This method is used for fetching all the properties of a given step
	 * element aoVWStepElement
	 * 
	 * @param aoVWStepElement an object of VWStepElement
	 * @return HashMap a map containing VW Parameters Values
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public HashMap getVWParamtersValues(VWStepElement aoVWStepElement) throws ApplicationException
	{

		HashMap loHmWorkItemDetails = new HashMap();
		VWParameter[] loVWParams = null;

		try
		{
			// get all the parameters of the step element
			loVWParams = aoVWStepElement.getParameters(VWFieldType.ALL_FIELD_TYPES,
					VWStepElement.FIELD_USER_AND_SYSTEM_DEFINED);

			// iterate through the VWParameters array and set their values in
			// the hashmap loHmWorkItemDetails
			for (int liCount = 0; liCount < loVWParams.length; liCount++)
			{
				String lsPropName = loVWParams[liCount].getName();
				setVWParamValuesInHashMap(loHmWorkItemDetails, aoVWStepElement, lsPropName);
			}
		}
		catch (VWException aoVWEx)
		{
			ApplicationException loAppex = new ApplicationException("Error while retrieving property values", aoVWEx);
			loAppex.setContextData(loHmWorkItemDetails);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.getVWParamtersValues()::", loAppex);
			throw loAppex;

		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmWorkItemDetails);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.getVWParamtersValues()::", aoAppex);
			throw new ApplicationException("Exception in P8ProcessOperations.getVWParamtersValues()::", aoAppex);
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Error while fetching fetching the properties of a given step element aoVWStepElement::"
							+ aoVWStepElement, aoEx);
			loAppex.setContextData(loHmWorkItemDetails);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.getVWParamtersValues()::", loAppex);
			throw loAppex;
		}

		// return the hashmap containing all the property values
		LOG_OBJECT.Debug("Exited P8ProcessOperations.getVWParamtersValues(). Returned loHmWorkItemDetails::"
				+ loHmWorkItemDetails.toString());
		return loHmWorkItemDetails;
	}

	/**
	 * This method is used for creating a queue filer for PE queries
	 * 
	 * @param aohmFilter map containing filter properties
	 * @return String a string value of queue filter
	 * @throws ApplicationException
	 */

	private String createQueueFilter(HashMap aoHmFilter) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered P8ProcessOperations.createQueueFilter()");
		String lsQueueFilter = "";
		StringBuffer loStrBufferQueueFilter = new StringBuffer("");
		try
		{
			if (aoHmFilter != null)
			{
				Iterator loIt = aoHmFilter.keySet().iterator();
				while (loIt.hasNext())
				{
					loStrBufferQueueFilter = loStrBufferQueueFilter.append(" ");
					String lsFilterKey = (String) loIt.next();
					String lsFilterValue = (String) aoHmFilter.get(lsFilterKey);
					// Check for date values From/To in the hashmap
					if (lsFilterKey.equalsIgnoreCase(P8Constants.PROPERTY_HMP_SUBMITTED_FROM))
					{
						loStrBufferQueueFilter.append(P8Constants.PROPERTY_PE_LAUNCH_DATE);
						loStrBufferQueueFilter.append(">=");
						loStrBufferQueueFilter.append(aoHmFilter.get(P8Constants.PROPERTY_HMP_SUBMITTED_FROM));
					}
					else if (lsFilterKey.equalsIgnoreCase(P8Constants.PROPERTY_HMP_SUBMITTED_TO))
					{
						loStrBufferQueueFilter.append(P8Constants.PROPERTY_PE_LAUNCH_DATE);
						loStrBufferQueueFilter.append("<=");
						loStrBufferQueueFilter.append(aoHmFilter.get(P8Constants.PROPERTY_HMP_SUBMITTED_TO));
					}
					else if (lsFilterKey.equalsIgnoreCase(P8Constants.PROPERTY_HMP_ASSIGNED_FROM))
					{
						loStrBufferQueueFilter.append(P8Constants.PROPERTY_PE_LAST_ASSIGNED);
						loStrBufferQueueFilter.append(">=");
						loStrBufferQueueFilter.append(aoHmFilter.get(P8Constants.PROPERTY_HMP_ASSIGNED_FROM));
					}
					else if (lsFilterKey.equalsIgnoreCase(P8Constants.PROPERTY_HMP_ASSIGNED_TO))
					{
						loStrBufferQueueFilter.append(P8Constants.PROPERTY_PE_LAST_ASSIGNED);
						loStrBufferQueueFilter.append("<=");
						loStrBufferQueueFilter.append(aoHmFilter.get(P8Constants.PROPERTY_HMP_ASSIGNED_TO));
					}
					else if (lsFilterKey.equalsIgnoreCase(P8Constants.PROPERTY_PE_ASSIGNED_TO)
							&& lsFilterValue.equalsIgnoreCase(P8Constants.PROPERTY_PE_VALUE_ALL_STAFF))
					{
						// Check for "All Staff" in AssignedTo
						loStrBufferQueueFilter.append(P8Constants.PROPERTY_PE_ASSIGNED_TO);
						loStrBufferQueueFilter.append(" not like 'Unassign%' ");
					}
					else if (lsFilterKey.equalsIgnoreCase(P8Constants.PROPERTY_PE_ASSIGNED_TO)
							&& lsFilterValue.equalsIgnoreCase(P8Constants.PROPERTY_PE_VALUE_UNASSIGN))
					{ // Check for "Unassign" in AssignedTo
						loStrBufferQueueFilter.append(P8Constants.PROPERTY_PE_ASSIGNED_TO);
						loStrBufferQueueFilter.append(" like 'Unassign%' ");
					}
					else if (lsFilterKey.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE)
							&& lsFilterValue.equalsIgnoreCase(P8Constants.PROPERTY_PE_VALUE_WITHDRAWL_REQUEST))
					{// Fetch both Withdrawal Request - Business Application and
						// Withdrawal Request - Service Application
						loStrBufferQueueFilter.append(P8Constants.PROPERTY_PE_TASK_TYPE);
						loStrBufferQueueFilter.append(" like '");
						loStrBufferQueueFilter.append(P8Constants.PROPERTY_PE_VALUE_WITHDRAWL_REQUEST);
						loStrBufferQueueFilter.append("%'");
					}
					/* Defect id - 955 All applications */
					else if (lsFilterKey.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE)
							&& lsFilterValue.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_ALL_APPLICATIONS))
					{ // If task type comes as "All Applications" , then display
						// BR Applications, Section tasks and Service
						// Application
						loStrBufferQueueFilter = (StringBuffer) allApplicationQueueFilter(loStrBufferQueueFilter);
						// have change function to reduce the length of function
						// upto 100

					}
					else
					{ // For all other values,use normal equals clause
						if (lsFilterValue.contains("'"))
						{
							lsFilterValue = lsFilterValue.replace("'", "''");
						}
						loStrBufferQueueFilter.append(lsFilterKey);
						loStrBufferQueueFilter.append("= '");
						loStrBufferQueueFilter.append(lsFilterValue);
						loStrBufferQueueFilter.append("'");
					}
					// append AND if multiple filters are present
					if (loIt.hasNext())
					{
						loStrBufferQueueFilter.append(" AND ");
					}
				}// while ends
			}
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error While Creating queue Filter::", aoEx);
			loAppex.setContextData(aoHmFilter);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.getQueueWorkitems()::", loAppex);
			throw loAppex;
		}
		lsQueueFilter = loStrBufferQueueFilter.toString();
		LOG_OBJECT.Debug("Exited P8ProcessOperations.createQueueFilter(). Returned lsQueueFilter::" + lsQueueFilter);
		// Return the created queue filter
		return lsQueueFilter;
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
			aoStrBufferQueueFilter.append("( ");
			aoStrBufferQueueFilter.append(P8Constants.PROPERTY_PE_TASK_TYPE);
			aoStrBufferQueueFilter.append(" = '");
			aoStrBufferQueueFilter.append(P8Constants.PROPERTY_PE_TASK_TYPE_BR_APPLICATION);
			aoStrBufferQueueFilter.append("'");
			aoStrBufferQueueFilter.append(" OR ");
			aoStrBufferQueueFilter.append(P8Constants.PROPERTY_PE_TASK_TYPE);
			aoStrBufferQueueFilter.append(" = '");
			aoStrBufferQueueFilter.append(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_BASIC);
			aoStrBufferQueueFilter.append("'");
			aoStrBufferQueueFilter.append(" OR ");
			aoStrBufferQueueFilter.append(P8Constants.PROPERTY_PE_TASK_TYPE);
			aoStrBufferQueueFilter.append(" = '");
			aoStrBufferQueueFilter.append(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_BOARD);
			aoStrBufferQueueFilter.append("'");
			aoStrBufferQueueFilter.append(" OR ");
			aoStrBufferQueueFilter.append(P8Constants.PROPERTY_PE_TASK_TYPE);
			aoStrBufferQueueFilter.append(" = '");
			aoStrBufferQueueFilter.append(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_FILINGS);
			aoStrBufferQueueFilter.append("'");
			aoStrBufferQueueFilter.append(" OR ");
			aoStrBufferQueueFilter.append(P8Constants.PROPERTY_PE_TASK_TYPE);
			aoStrBufferQueueFilter.append(" = '");
			aoStrBufferQueueFilter.append(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_POLICIES);
			aoStrBufferQueueFilter.append("'");
			aoStrBufferQueueFilter.append(" OR ");
			aoStrBufferQueueFilter.append(P8Constants.PROPERTY_PE_TASK_TYPE);
			aoStrBufferQueueFilter.append(" = '");
			aoStrBufferQueueFilter.append(P8Constants.PROPERTY_PE_TASK_TYPE_SERVICE_APPLICATION);
			aoStrBufferQueueFilter.append("'");
			aoStrBufferQueueFilter.append(" )");
			aoStrBufferQueueFilter.append(" AND taskVisibility = 1 ");
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
	 * This method is used for setting the values of a parameter in the given
	 * hash map
	 * 
	 * @param aohmWorkItemDetails a map containing work item details
	 * @param aoVWStepElem an object of VWStepElement
	 * @param asPropName a string value of property name
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private void setVWParamValuesInHashMap(HashMap aoHmWorkItemDetails, VWStepElement aoVWStepElem, String asPropName)
			throws ApplicationException
	{

		try
		{
			// if property name is Subject then fetch the value of Subject from
			// step element, else fetch the value based on property name
			if (asPropName.equalsIgnoreCase(P8Constants.PROPERTY_PE_SUBJECT))
			{
				aoHmWorkItemDetails.put(asPropName, aoVWStepElem.getSubject());
			}
			else
			{
				aoHmWorkItemDetails.put(asPropName, aoVWStepElem.getParameterValue(asPropName));
			}

		}
		catch (VWException aoVWEx)
		{
			ApplicationException loAppex = new ApplicationException("Error while setting property values", aoVWEx);
			loAppex.setContextData(aoHmWorkItemDetails);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.setVWParamValuesInHashMap()::", loAppex);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Error while setting the values of a parameter in the given hash map::", aoEx);
			loAppex.setContextData(aoHmWorkItemDetails);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.setVWParamValuesInHashMap()::", loAppex);
			throw loAppex;
		}
		// LOG_OBJECT.Debug("Exited P8ProcessOperations.setVWParamValuesInHashMap()");
	}

	/**
	 * This function is used to assigning or re-assigning work items for BR
	 * Tasks and Section tasks In case of Section task, only task owner is
	 * changed In case of BR task :- if it is a not a manager task then update
	 * BROwner also for both parent and child. if it is a manager task, then
	 * don't change BROwner for parent or child. Only change task owner
	 * 
	 * @param aoVWSession a VWSession object
	 * @param asWobNumber a string value of work flow numbers.
	 * @param asUserName a string value of user name.
	 * @param asSessionUserName a string value of session's user name
	 * @param asQueueName a string value of queue name
	 * @return HashMap loHmServiceCapacityWobNos a map containing service
	 *         capacity work flow numbers
	 * @throws ApplicationException
	 */
	public HashMap assign(VWSession aoVWSession, String asWobNumber, String asUserName, String asSessionUserName,
			String asQueueName, String asUserForAudit) throws ApplicationException
	{
		HashMap loHmServiceCapacityWobNos = new HashMap();
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("asWobNumber", asWobNumber);
		loHmReqExceProp.put("asUserName", asUserName);
		loHmReqExceProp.put("asQueueName", asQueueName);
		LOG_OBJECT.Debug("Entered P8ProcessOperations.assign() with parameters::" + loHmReqExceProp.toString());
		String lsSectionId = "";
		String lsEntityId = "";
		String lsTaskDisplayName = "";
		String lsEntityTypeDisplayName = "";
		String lsEntityIdentifier = "";
		try
		{
			VWStepElement loStepElement = getStepElementfromWobNo(aoVWSession, asWobNumber, asQueueName);
			if (loStepElement == null)
			{
				throwApplicationException("Error in getting step element from wob no.", loHmReqExceProp);
			}
			loStepElement.setParameterValue(P8Constants.PROPERTY_PE_LAST_ASSIGNED, new Date(), false);
			loStepElement.setParameterValue(P8Constants.PROPERTY_PE_TASK_MODIFIED_DATE, new Date(), false);
			String lsTaskType = (String) loStepElement.getParameterValue(P8Constants.PROPERTY_PE_TASK_TYPE);
			String lsAppId = null;
			String lsLaunchBy = null;
			String lsProviderId = null;
			String lsProviderName = null;
			if (null != lsTaskType)
			{
				// changes for r5 starts
				if (lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_AWARD_APPROVAL)
						|| lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_APPROVE_PSR)
						|| lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_AWARD_APPROVAL_AMOUNT))
				{
					assignTask(loStepElement, asUserName, asSessionUserName, loHmServiceCapacityWobNos);
					if (lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_APPROVE_PSR))
					{
						String lsProcurementId = (String) loStepElement
								.getParameterValue(P8Constants.PE_WORKFLOW_PROCUREMENT_ID);
						lsEntityId = lsProcurementId;
					}
					else if (lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_AWARD_APPROVAL))
					{
						String lsEvaluationPoolMappingId = (String) loStepElement
								.getParameterValue(P8Constants.EVALUATION_POOL_MAPPING_ID);
						if (null != lsEvaluationPoolMappingId)
						{
							lsEntityId = lsEvaluationPoolMappingId;
						}
					}
					else if (lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_AWARD_APPROVAL_AMOUNT))
					{
						String lsEntityForApproveAmount = (String) loStepElement
								.getParameterValue(P8Constants.PROPERTY_PE_ENTITY_ID);
						lsEntityId = lsEntityForApproveAmount;
					}
					HhsAuditBean loAuditBean = CommonUtil.addAuditDataToChannel(ApplicationConstants.TASK_ASSIGNMENT,
							lsTaskType, ApplicationConstants.TASK_ASSIGNED_TO + ApplicationConstants.COLON_AOP
									+ asSessionUserName, lsTaskType, lsEntityId, asUserName,
							ApplicationConstants.ACCELERATOR_AUDIT);
					// changes for r5 ends
					Channel loChannel = new Channel();
					loChannel.setData("auditBean", loAuditBean);
					loChannel.setData("statusFlag", true);
					TransactionManager.executeTransaction(loChannel, "acceleratorAuditForAwardTask");
				}
				else
				{
					lsAppId = (String) loStepElement.getParameterValue(P8Constants.PROPERTY_PE_APPLICTION_ID);
					lsLaunchBy = (String) loStepElement.getParameterValue(P8Constants.PROPERTY_PE_LAUNCH_BY);
					lsProviderId = (String) loStepElement.getParameterValue(P8Constants.PROPERTY_PE_PROVIDER_ID);
					lsProviderName = (String) loStepElement.getParameterValue(P8Constants.PROPERTY_PE_PROVIDER_NAME);
					if (lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_BR_APPLICATION))
					{
						lsSectionId = "";
						lsEntityId = lsAppId;
						lsTaskDisplayName = lsTaskType;
						lsEntityTypeDisplayName = lsTaskType;
						lsEntityIdentifier = lsTaskType; // modified as per
															// defect
															// 789
						assignBRAplication(aoVWSession, loStepElement, asQueueName, asWobNumber, asUserName,
								asSessionUserName, lsAppId, lsProviderId, lsProviderName, lsLaunchBy,
								loHmServiceCapacityWobNos);
					}
					else if (lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SERVICE_APPLICATION))
					{
						lsSectionId = (String) loStepElement.getParameterValue(P8Constants.PROPERTY_PE_SECTION_ID);
						lsEntityId = lsSectionId;
						lsTaskDisplayName = lsTaskType;
						lsEntityTypeDisplayName = lsTaskType;
						lsEntityIdentifier = lsTaskType; // modified as per
															// defect
															// 789
						assignServiceApplication(aoVWSession, asWobNumber, asUserName, asSessionUserName, asQueueName,
								loHmServiceCapacityWobNos);
					}
					else if (lsTaskType
							.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_BUSINESS_REVIEW_APPLICATION))
					{
						lsSectionId = "";
						lsTaskDisplayName = lsTaskType;
						lsEntityTypeDisplayName = lsTaskType;
						lsEntityId = (String) loStepElement.getParameterValue(P8Constants.PROPERTY_PE_ENTITY_ID);
						lsEntityIdentifier = lsTaskType; // modified as per
															// defect
															// 789
						assignTask(loStepElement, asUserName, asSessionUserName, loHmServiceCapacityWobNos);
					}
					else if (lsTaskType
							.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_SERVICE_APPLICATION))
					{
						lsSectionId = (String) loStepElement.getParameterValue(P8Constants.PROPERTY_PE_SECTION_ID);
						lsEntityId = (String) loStepElement.getParameterValue(P8Constants.PROPERTY_PE_ENTITY_ID);
						lsTaskDisplayName = lsTaskType;
						lsEntityTypeDisplayName = lsTaskType;
						lsEntityIdentifier = lsTaskType; // modified as per
															// defect
															// 789
						assignTask(loStepElement, asUserName, asSessionUserName, loHmServiceCapacityWobNos);
					}
					else if (lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_BASIC)
							|| lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_BOARD)
							|| lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_FILINGS)
							|| lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_POLICIES))
					{
						lsSectionId = (String) loStepElement.getParameterValue(P8Constants.PROPERTY_PE_SECTION_ID);
						lsEntityId = lsSectionId;
						String[] lsEventTypeArray = lsTaskType.split(":");
						lsTaskDisplayName = lsEventTypeArray[lsEventTypeArray.length - 1];
						lsEntityTypeDisplayName = "Section";
						// lsEntityTypeDisplayName = lsTaskType; // updated as
						// per
						// defect 789
						lsEntityIdentifier = lsTaskType; // modified as per
															// defect
															// 789
						assignTask(loStepElement, asUserName, asSessionUserName, loHmServiceCapacityWobNos);
					}
					else
					{
						lsEntityId = lsAppId;
						lsTaskDisplayName = lsTaskType;
						lsEntityTypeDisplayName = lsTaskType;
						lsEntityIdentifier = lsTaskType;
						assignTask(loStepElement, asUserName, asSessionUserName, loHmServiceCapacityWobNos);
					}
					updateAuditDuringTaskAssign(lsProviderId, lsTaskDisplayName, lsLaunchBy, lsEntityTypeDisplayName,
							lsEntityId, lsAppId, lsSectionId, asSessionUserName, lsTaskType, lsEntityIdentifier,
							asUserForAudit);
				}
				LOG_OBJECT.Debug("Assigned task::" + asWobNumber + " of task-type " + lsTaskType + " to user::"
						+ asUserName);
				LOG_OBJECT.Debug("Exited P8ProcessOperations.assign(). Returned loHmServiceCapacityWobNos::"
						+ loHmServiceCapacityWobNos.toString());
			}
		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.assign()::", aoAppex);
			throw aoAppex;
		}
		catch (VWException aoVWEx)
		{
			catchExceptionOnAssign(loHmReqExceProp, aoVWEx);
		}
		catch (Exception aoEx)
		{
			catchExceptionOnAssign(loHmReqExceProp, aoEx);
		}
		return loHmServiceCapacityWobNos;
	}

	/**
	 * This Method catches Exception on assign method
	 * @param loHmReqExceProp
	 * @param aoEx
	 * @throws ApplicationException
	 */
	private void catchExceptionOnAssign(HashMap loHmReqExceProp, Exception aoEx) throws ApplicationException
	{
		ApplicationException loAppex = new ApplicationException("Error while assigning work item", aoEx);
		loAppex.setContextData(loHmReqExceProp);
		LOG_OBJECT.Error("Exception in P8ProcessOperations.assign()::", loAppex);
		throw loAppex;
	}

	/**
	 * This method is used for assigning a BR application
	 * 
	 * @param aoVWSession a VWSession object
	 * @param aoStepElement VWStepElement object
	 * @param asQueueName a string value of queue name
	 * @param asWobNumber a string value of work flow number.
	 * @param asUserName a string value of user name.
	 * @param asSessionUserName a string value of session's user name
	 * @param asAppId a string value of application id
	 * @param asProviderId a string value of provider id
	 * @param asProviderName a string value of provider name
	 * @param asLaunchBy a string value of launch by user
	 * @param aoHmServiceCapacityWobNos a hashmap containing the service
	 *            capacity wob nos
	 * @throws ApplicationException
	 */
	private void assignBRAplication(VWSession aoVWSession, VWStepElement aoStepElement, String asQueueName,
			String asWobNumber, String asUserName, String asSessionUserName, String asAppId, String asProviderId,
			String asProviderName, String asLaunchBy, HashMap aoHmServiceCapacityWobNos) throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("asWobNumber", asWobNumber);
		loHmReqExceProp.put("asUserName", asUserName);
		loHmReqExceProp.put("asQueueName", asQueueName);
		loHmReqExceProp.put("asSessionUserName", asSessionUserName);
		loHmReqExceProp.put("asAppId", asAppId);
		loHmReqExceProp.put("asProviderId", asProviderId);
		loHmReqExceProp.put("asProviderName", asProviderName);
		loHmReqExceProp.put("asLaunchBy", asLaunchBy);
		LOG_OBJECT.Debug("Entered assignBRAplication with parameters " + loHmReqExceProp.toString());

		try
		{
			// set task owner to new user
			aoStepElement.setParameterValue(P8Constants.PROPERTY_PE_ASSIGNED_TO, asUserName, false);
			aoStepElement.setParameterValue(P8Constants.PROPERTY_PE_ASSIGNED_TO_NAME, asSessionUserName, false);

			// Check if it is a manager step
			boolean lbIsManagerReserveStep = Boolean.parseBoolean(aoStepElement.getParameterValue(
					P8Constants.PROPERTY_PE_IS_MANAGER_REVIEW_STEP).toString());
			LOG_OBJECT.Debug("Obtained value of lbIsManagerReserveStep::" + lbIsManagerReserveStep);
			if (lbIsManagerReserveStep == false)
			{
				// Change BR owner only if it is a non manager step. For manager
				// step BR owner shouldn't change
				// as it to be remain same for Return for Revision to remain
				// same.
				aoStepElement.setParameterValue(P8Constants.PROPERTY_PE_BR_APP_OWNER, asUserName, false);
				aoStepElement.setParameterValue(P8Constants.PROPERTY_PE_BR_APP_OWNER_NAME, asSessionUserName, false);
				LOG_OBJECT.Debug("Set the value of BRAppOwner::" + asUserName + " for work item::" + asWobNumber);
			}

			boolean lbIsChildTaskLaunched = Boolean.parseBoolean(aoStepElement.getParameterValue(
					P8Constants.PROPERTY_IS_CHILD_TASK_LAUNCHED).toString());
			LOG_OBJECT.Debug("Got the value of lbIsChildTaskLaunched::" + lbIsChildTaskLaunched);
			if (lbIsChildTaskLaunched == false)
			{
				// add an entry in the hashmap for isChildTaskLaunched=true to
				// indicate that service capacity child tasks will be launched
				aoHmServiceCapacityWobNos.put("isChildTaskLaunched", "true");
				String lsParentAppId = (String) aoStepElement
						.getParameterValue(P8Constants.PROPERTY_PE_PARENT_APPLICATION_ID);
				HashMap loHmCommonProps = new HashMap();
				loHmCommonProps.put(P8Constants.PROPERTY_PE_APPLICTION_ID, asAppId);
				loHmCommonProps.put(P8Constants.PROPERTY_PE_PROVIDER_ID, asProviderId);
				loHmCommonProps.put(P8Constants.PROPERTY_PE_PROVIDER_NAME, asProviderName);
				loHmCommonProps.put(P8Constants.PROPERTY_PE_BR_APP_OWNER, asUserName);
				loHmCommonProps.put(P8Constants.PROPERTY_PE_ASSIGNED_TO, asUserName);
				loHmCommonProps.put(P8Constants.PROPERTY_PE_ASSIGNED_TO_NAME, asSessionUserName);
				loHmCommonProps.put(P8Constants.PROPERTY_PE_BR_APP_OWNER_NAME, asSessionUserName);
				loHmCommonProps.put(P8Constants.PROPERTY_PE_TASK_MODIFIED_DATE, new Date());
				loHmCommonProps.put(P8Constants.PROPERTY_PE_TASK_ASSIGN_DATE, new Date());
				loHmCommonProps.put(P8Constants.PROPERTY_PE_PARENT_APPLICATION_ID, lsParentAppId);
				loHmCommonProps.put(P8Constants.PROPERTY_PE_LAUNCH_BY, asLaunchBy);
				launchChildSectionTasks(aoVWSession, aoStepElement, asWobNumber, loHmCommonProps,
						aoHmServiceCapacityWobNos, asSessionUserName);
			}
			else
			{
				// **** Child tasks already launched ***
				LOG_OBJECT.Debug("Child tasks aleady launched");
				aoHmServiceCapacityWobNos.put("isChildTaskLaunched", "false");
				// fetch child tasks and assign owner
				String[] loChildTaskItems = (String[]) aoStepElement
						.getParameterValue(P8Constants.PROPERTY_PE_SECTION_WF_NOS);
				for (int liCount = 0; liCount < loChildTaskItems.length; liCount++)
				{
					String lsChildWobNo = loChildTaskItems[liCount];
					assignChild(aoVWSession, lsChildWobNo, asQueueName, asUserName, asSessionUserName,
							lbIsManagerReserveStep);
					LOG_OBJECT.Debug("Reassigned child::" + lsChildWobNo);
				}
			}
			// Save the parent step element
			aoStepElement.doSave(true);
		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.assignBRAplication()::", aoAppex);
			throw aoAppex;
		}
		catch (VWException aoVWEx)
		{
			ApplicationException loAppex = new ApplicationException("Error while assigning work item", aoVWEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.assignBRAplication()::", loAppex);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error while assigning work item", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.assignBRAplication()::", loAppex);
			throw loAppex;
		}
		LOG_OBJECT.Debug("Exited assignBRAplication with parameters " + loHmReqExceProp.toString());
	}

	/**
	 * This method is used for assigning a BR application task
	 * 
	 * @param aoVWSession a VWSession object
	 * @param aoStepElement a VWStepElement object
	 * @param asParentWobNumber a string value of parent wob no
	 * @param aoHmCommonProps a hashmap of common workflow properties.
	 * @param aoHmServiceCapacityWobNos a hashmap of common service capacity wob
	 *            nos.
	 * 
	 * @throws ApplicationException
	 */
	private void launchChildSectionTasks(VWSession aoVWSession, VWStepElement aoStepElement, String asParentWobNumber,
			HashMap aoHmCommonProps, HashMap aoHmServiceCapacityWobNos, String asSessionUserName)
			throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("asParentWobNumber", asParentWobNumber);
		loHmReqExceProp.put("aoHmCommonProps", aoHmCommonProps);
		loHmReqExceProp.put("aoHmServiceCapacityWobNos", aoHmServiceCapacityWobNos);
		LOG_OBJECT.Debug("Entered launchChildSectionTasks with parameters ::" + loHmReqExceProp.toString());
		String lsWobNo = null;

		try
		{
			String[] loSectionIDs = (String[]) aoStepElement.getParameterValue(P8Constants.PROPERTY_PE_SECTION_IDS);
			String[] loSectionTaskNames = (String[]) aoStepElement
					.getParameterValue(P8Constants.PROPERTY_PE_SECTION_TASK_NAMES);
			String[] loSectionTaskStatus = (String[]) aoStepElement
					.getParameterValue(P8Constants.PROPERTY_PE_SECTION_TASK_STATUS);
			String[] loSectionWFNos = new String[4];

			// Launch 1st Section - Basic
			lsWobNo = launchSectionTask(aoVWSession, loSectionIDs[0], loSectionTaskNames[0],
					P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_BASIC, asParentWobNumber, loSectionTaskStatus[0],
					aoHmCommonProps, asSessionUserName);
			LOG_OBJECT.Debug("Launched Basic section task with wob no::" + lsWobNo);
			loSectionWFNos[0] = lsWobNo;
			// Launch 2nd Section - Board
			lsWobNo = launchSectionTask(aoVWSession, loSectionIDs[1], loSectionTaskNames[1],
					P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_BOARD, asParentWobNumber, loSectionTaskStatus[1],
					aoHmCommonProps, asSessionUserName);
			LOG_OBJECT.Debug("Launched Board section task with wob no::" + lsWobNo);
			loSectionWFNos[1] = lsWobNo;
			// Launch 3rd Section - Filings
			lsWobNo = launchSectionTask(aoVWSession, loSectionIDs[2], loSectionTaskNames[2],
					P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_FILINGS, asParentWobNumber, loSectionTaskStatus[2],
					aoHmCommonProps, asSessionUserName);
			LOG_OBJECT.Debug("Launched Filings section task with wob no::" + lsWobNo);
			loSectionWFNos[2] = lsWobNo;
			// Launch 4th Section - Policies
			lsWobNo = launchSectionTask(aoVWSession, loSectionIDs[3], loSectionTaskNames[3],
					P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_POLICIES, asParentWobNumber, loSectionTaskStatus[3],
					aoHmCommonProps, asSessionUserName);
			LOG_OBJECT.Debug("Launched Policies section task with wob no.::" + lsWobNo);
			loSectionWFNos[3] = lsWobNo;

			// set values in parent for section WOB nos. array,
			// isChildTaskLaunched and status
			aoStepElement.setParameterValue(P8Constants.PROPERTY_PE_SECTION_WF_NOS, loSectionWFNos, false);
			aoStepElement.setParameterValue(P8Constants.PROPERTY_IS_CHILD_TASK_LAUNCHED, Boolean.TRUE, false);

			// ************** Launch Service Capacity Workflows
			// **********************
			// Launch service capacity tasks and get a hashmap containing the
			// service capacity ids and corresponding wobnos
			LOG_OBJECT
					.Debug("Calling the function launchServiceCapacityTasks to launch the Service Application tasks associated with the parent BR task ");
			HashMap loHmTempSCWobNos = launchServiceCapacityTasks(aoVWSession, aoStepElement, aoHmCommonProps);
			LOG_OBJECT
					.Debug("Launched Service Application tasks associated with the parent BR task. SC Wob nos. obtained::"
							+ loHmTempSCWobNos.toString());
			// Iterate through the hashmap and add the service capacity ids and
			// corresponding wobnos to the hashmap lhmServiceCapacityWobNos
			Iterator loIt = loHmTempSCWobNos.keySet().iterator();
			while (loIt.hasNext())
			{
				String lsSecId = (String) loIt.next();
				String lsSCWobNo = (String) loHmTempSCWobNos.get(lsSecId);
				aoHmServiceCapacityWobNos.put(lsSecId, lsSCWobNo);
			}
		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.launchChildSectionTasks()::", aoAppex);
			throw aoAppex;
		}
		catch (VWException aoVWEx)
		{
			ApplicationException loAppex = new ApplicationException("Error while assigning work item", aoVWEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.launchChildSectionTasks()::", loAppex);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error while assigning work item", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.launchChildSectionTasks()::", loAppex);
			throw loAppex;
		}
		LOG_OBJECT.Debug("Exited launchChildSectionTasks");
	}

	/**
	 * This method is used for launching a section task item
	 * 
	 * @param aoVWSession a VWSession object
	 * @param asSectionID a String denoting the section id
	 * @param asSectionTaskName a String denoting the section task name
	 * @param asTaskType a String denoting the section task type
	 * @param asParentWobNumber a String denoting the parent wob no
	 * @param asSectionTaskStatus a String denoting the section task status
	 * @param aoHmCommonProps a hashmap denoting the common workflow properties
	 * @return a String containing the wob no of the launches workflow
	 * @throws ApplicationException
	 */
	private String launchSectionTask(VWSession aoVWSession, String asSectionID, String asSectionTaskName,
			String asTaskType, String asParentWobNumber, String asSectionTaskStatus, HashMap aoHmCommonProps,
			String asSessionUserName) throws ApplicationException
	{

		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("asWobNumber", asParentWobNumber);
		loHmReqExceProp.put("aoHmCommonProps", aoHmCommonProps);

		LOG_OBJECT.Debug("Entered launchSectionTask with parameters ::" + loHmReqExceProp.toString());

		String lsWobNo = null;

		try
		{
			HashMap loHmPropsSection = null;
			loHmPropsSection = aoHmCommonProps;
			loHmPropsSection.put(P8Constants.PROPERTY_PE_SECTION_ID, asSectionID);
			loHmPropsSection.put(P8Constants.PROPERTY_PE_TASK_NAME, asSectionTaskName);
			loHmPropsSection.put(P8Constants.PROPERTY_PE_TASK_TYPE, asTaskType);
			loHmPropsSection.put(P8Constants.PROPERTY_PE_TASK_STATUS, asSectionTaskStatus);
			loHmPropsSection.put(P8Constants.PROPERTY_PE_PARENT_APP_WOB_NO, asParentWobNumber);

			lsWobNo = launchWorkflow(aoVWSession, P8Constants.PROPERTY_SECTION_WORKFLOW_NAME, loHmPropsSection,
					asSessionUserName);
			LOG_OBJECT.Debug("Launched " + asTaskType + " section task with wob no::" + lsWobNo);
		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.launchSectionTask()::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error while assigning work item", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.launchSectionTask()::", loAppex);
			throw loAppex;
		}
		LOG_OBJECT.Debug("Exited launchSectionTask with parameters ");
		return lsWobNo;
	}

	/**
	 * This function is used for assigning child items
	 * 
	 * @param aoVWSession a VWSession object
	 * @param asWobNumber a string value of work flow number
	 * @param asQueueName a string value of queue name
	 * @param asUserName a string value of User Name
	 * @param asSessionUserName a string value of session's user name
	 * @param abIsManagerReserveStep a booleab representing whether or not the
	 *            step is reserved for manager
	 * @return boolean variable true if child is assigned
	 * @throws ApplicationException
	 */

	public boolean assignChild(VWSession aoVWSession, String asWobNumber, String asQueueName, String asUserName,
			String asSessionUserName, boolean abIsManagerReserveStep) throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("asWobNumber", asWobNumber);
		loHmReqExceProp.put("asQueueName", asWobNumber);
		loHmReqExceProp.put("asUserName", asWobNumber);
		loHmReqExceProp.put("abIsManagerReserveStep", asWobNumber);
		LOG_OBJECT.Debug("Entered P8ProcessOperations.assignChild() with parameters : " + loHmReqExceProp.toString());

		HashMap loHmReqdOutputProps = new HashMap();
		loHmReqdOutputProps.put(P8Constants.PROPERTY_PE_BR_APP_OWNER, "");
		loHmReqdOutputProps.put(P8Constants.PROPERTY_PE_ASSIGNED_TO, "");
		HashMap loHmResultList = getWorkItemDetails(aoVWSession, P8Constants.HSS_QUEUE_NAME,
				P8Constants.PROPERTY_PE_WOBNUMBER, asWobNumber, loHmReqdOutputProps);
		HashMap loHmWorkItem = (HashMap) loHmResultList.get(asWobNumber);
		String lsBRAppOwner = loHmWorkItem.get(P8Constants.PROPERTY_PE_BR_APP_OWNER).toString();
		String lsTaskOwner = loHmWorkItem.get(P8Constants.PROPERTY_PE_ASSIGNED_TO).toString();

		try
		{
			VWStepElement loChildStepElement = getStepElementfromWobNo(aoVWSession, asWobNumber, asQueueName);
			if (loChildStepElement == null)
			{
				throwApplicationException(
						"Exception in P8ProcessOperations.assignChild():Error in getting child step element from wob no",
						loHmReqExceProp);
			}

			if (abIsManagerReserveStep == true)
			{
				loChildStepElement.setParameterValue(P8Constants.PROPERTY_PE_ASSIGNED_TO, asUserName, false);
				loChildStepElement
						.setParameterValue(P8Constants.PROPERTY_PE_ASSIGNED_TO_NAME, asSessionUserName, false);
			}
			else
			{
				// non manager step
				if (lsTaskOwner.equalsIgnoreCase(lsBRAppOwner))
				{
					loChildStepElement.setParameterValue(P8Constants.PROPERTY_PE_BR_APP_OWNER, asUserName, false);
					loChildStepElement.setParameterValue(P8Constants.PROPERTY_PE_BR_APP_OWNER_NAME, asSessionUserName,
							false);
					loChildStepElement.setParameterValue(P8Constants.PROPERTY_PE_ASSIGNED_TO_NAME, asSessionUserName,
							false);
					loChildStepElement.setParameterValue(P8Constants.PROPERTY_PE_ASSIGNED_TO, asUserName, false);
				}
				else
				{
					loChildStepElement.setParameterValue(P8Constants.PROPERTY_PE_BR_APP_OWNER, asUserName, false);
					loChildStepElement.setParameterValue(P8Constants.PROPERTY_PE_BR_APP_OWNER_NAME, asSessionUserName,
							false);
				}
			}
			loChildStepElement.doSave(true);
		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.assign()::", aoAppex);
			throw aoAppex;
		}
		catch (VWException aoVWEx)
		{
			ApplicationException loAppex = new ApplicationException("Error While assigning child items", aoVWEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.assign()::", loAppex);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error While assigning child items", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.assign()::", loAppex);
			throw loAppex;
		}
		LOG_OBJECT.Debug("Exited P8ProcessOperations.assignChild(). Returned true");
		return true;
	}

	/**
	 * This function is used for assign all other task items except BR and SC
	 * tasks
	 * 
	 * @param aoStepElement The Step element to be assigned
	 * @param asUserName The user id to which the task will be assigned
	 * @param asSessionUserName The user name to which the task will be assigned
	 * @throws ApplicationException
	 */
	private void assignTask(VWStepElement aoStepElement, String asUserName, String asSessionUserName,
			HashMap aoHmServiceCapacityWobNos) throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("asUserName", asUserName);
		loHmReqExceProp.put("asSessionUserName", asSessionUserName);

		LOG_OBJECT.Debug("Entered P8ProcessOperations.assignTask() with parameters::" + loHmReqExceProp.toString());

		try
		{
			// add an entry in the hashmap for isChildTaskLaunched=false
			// to indicate that no service capacity child tasks will be launched
			aoHmServiceCapacityWobNos.put("isChildTaskLaunched", "false");

			aoStepElement.setParameterValue(P8Constants.PROPERTY_PE_ASSIGNED_TO, asUserName, false);
			aoStepElement.setParameterValue(P8Constants.PROPERTY_PE_ASSIGNED_TO_NAME, asSessionUserName, false);
			aoStepElement.doSave(true);
		}
		catch (VWException aoVWEx)
		{
			ApplicationException loAppex = new ApplicationException("Error while assigning work item", aoVWEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.assignTask()::", loAppex);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error while assigning work item", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.assignTask()::", loAppex);
			throw loAppex;
		}
		LOG_OBJECT.Debug("Exited P8ProcessOperations.assignTask()");
	}

	/**
	 * This function is used to launch service capacity workflows
	 * 
	 * @param aoVWSession a VWSession object
	 * @param aoStepElement a VWStepElement object
	 * @param aoHmCommonProps a map containing common properties
	 * @return HashMap a map containing launch Service Capacity Tasks
	 * @throws ApplicationException
	 */
	public HashMap launchServiceCapacityTasks(VWSession aoVWSession, VWStepElement aoStepElement,
			HashMap aoHmCommonProps) throws ApplicationException
	{
		HashMap loHmWobNos = new HashMap();
		String lsWobNo;
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("aoHmCommonProps", aoHmCommonProps);

		LOG_OBJECT.Debug("Entered P8ProcessOperations.launchServiceCapacityTasks() with parameters::"
				+ loHmReqExceProp.toString());

		try
		{

			// Fetch Service Capacity IDs and Names from parent, delimited by
			// ###
			String lsServiceCapacityIDs = (String) aoStepElement
					.getParameterValue(P8Constants.PROPERTY_PE_SERVICE_CAPACITY_IDS);
			String lsServiceCapacityNames = (String) aoStepElement
					.getParameterValue(P8Constants.PROPERTY_PE_SERVICE_CAPACITY_NAMES);

			// Split on the basis of ### to get individual items
			String[] loArrServiceCapacityIDs = lsServiceCapacityIDs.split(P8Constants.PROPERTY_WF_STRINGSEPERATOR);
			LOG_OBJECT.Debug("Obtained Service Application IDs array::" + Arrays.toString(loArrServiceCapacityIDs));

			String[] loArrServiceCapacityNames = lsServiceCapacityNames.split(P8Constants.PROPERTY_WF_STRINGSEPERATOR);
			LOG_OBJECT.Debug("Obtained Service Application Names array::" + Arrays.toString(loArrServiceCapacityNames));

			// Launch all Service Capacity Workflows
			for (int liCount = 0; liCount < loArrServiceCapacityIDs.length; liCount++)
			{
				HashMap loHmServiceProps = null;
				loHmServiceProps = aoHmCommonProps;
				loHmServiceProps.put(P8Constants.PROPERTY_PE_SECTION_ID, loArrServiceCapacityIDs[liCount]);
				loHmServiceProps.put(P8Constants.PROPERTY_PE_TASK_NAME, loArrServiceCapacityNames[liCount]);

				lsWobNo = launchWorkflow(aoVWSession, P8Constants.PROPERTY_SERVICE_CAPACITY_WORKFLOW_NAME,
						loHmServiceProps, P8Constants.PE_TASK_UNASSIGNED);
				LOG_OBJECT.Debug("Launched Service Application Workflow::" + lsWobNo);

				if (lsWobNo != null)
				{
					// workflow launched successfully, put all the section-ids
					// and corresponding wob nos. in a hashmap
					loHmWobNos.put(loArrServiceCapacityIDs[liCount], lsWobNo);
				}
			}

		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.launchServiceCapacityTasks()::", aoAppex);
			throw aoAppex;
		}
		catch (VWException aoVWEx)
		{
			ApplicationException loAppex = new ApplicationException("Error while launching service capacity workflows",
					aoVWEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.launchServiceCapacityTasks()::", loAppex);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error while launching service capacity workflows",
					aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.launchServiceCapacityTasks()::", loAppex);
			throw loAppex;
		}

		LOG_OBJECT.Debug("Exited P8ProcessOperations.launchServiceCapacityTasks(). Returned loHmWobNos::"
				+ loHmWobNos.toString());
		return loHmWobNos;

	}

	/**
	 * This function is used for finishing a section child task and all other
	 * tasks except BR task. In case of section tasks, it also makes a call to
	 * the updateParentTaskStatus function to update the status arrays of the
	 * parent BR task
	 * 
	 * @param aoVWSession VWSession object
	 * @param asChildWobNumber a string form of child work flow number
	 * @param asChildStatus a string form of Child Status
	 * @param asQueueName a string form of Queue Name
	 * @return HashMap a map containing finished child tasks
	 * @throws ApplicationException
	 */
	public HashMap finishChildTask(VWSession aoVWSession, String asChildWobNumber, String asChildStatus,
			String asQueueName) throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("asChildWobNumber", asChildWobNumber);
		loHmReqExceProp.put("asChildStatus", asChildStatus);
		loHmReqExceProp.put("asQueueName", asQueueName);
		LOG_OBJECT
				.Debug("Entered P8ProcessOperations.finishChildTask() with parameters::" + loHmReqExceProp.toString());
		HashMap loHmFinishResult = new HashMap();
		int liSectionPointer = 0;
		try
		{
			VWStepElement loStepElement = getStepElementfromWobNo(aoVWSession, asChildWobNumber, asQueueName);
			if (loStepElement == null)
			{
				throwApplicationException("Error in P8ProcessOperations.finishChildTask while fetching step element",
						loHmReqExceProp);
			}
			// update the child task status only if we are receiving status from
			// the UI
			if (asChildStatus != null && !asChildStatus.equalsIgnoreCase(""))
			{
				loStepElement.setParameterValue(P8Constants.PROPERTY_PE_TASK_STATUS, asChildStatus, false);
			}
			loStepElement.setParameterValue(P8Constants.PROPERTY_PE_TASK_MODIFIED_DATE, new Date(), false);
			loStepElement.doSave(false); // save,but don't unlock yet
			String lsTaskType = (String) loStepElement.getParameterValue(P8Constants.PROPERTY_PE_TASK_TYPE);
			loHmFinishResult.put("TaskType", lsTaskType);
			if (lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_BASIC)
					|| lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_BOARD)
					|| lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_FILINGS)
					|| lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_POLICIES))
			{
				String lsParenWobNo = (String) loStepElement
						.getParameterValue(P8Constants.PROPERTY_PE_PARENT_APP_WOB_NO);// fetch
																						// parent
																						// wob
																						// no.
				// Find out the task type and based on that determine the value
				// of liSectionPointer.
				// This section pointer is passed to the updateParentTaskStatus
				// function, to determine which element of
				// the arrays lasSectionTaskStatus and
				// lasIsSectionTaskStatusUpdated have to be updated in the
				// parent task.
				// The 0th element of the arrays refer to the basic section, 1st
				// to the board, 2nd to the filings and 3rd to the policies
				// section.
				if (lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_BASIC))
				{
					liSectionPointer = 0;
				}
				else if (lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_BOARD))
				{
					liSectionPointer = 1;
				}
				else if (lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_FILINGS))
				{
					liSectionPointer = 2;
				}
				else if (lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_POLICIES))
				{
					liSectionPointer = 3;
				}
				// call the function updateParentTaskStatus to update the parent
				// task
				updateParentTaskStatus(aoVWSession, lsParenWobNo, asChildWobNumber, asChildStatus, liSectionPointer,
						asQueueName);
				// complete the child task
				loStepElement.doDispatch();
			}
			else if (lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SERVICE_APPLICATION))
			{
				finishServiceApplicationTask(aoVWSession, asChildWobNumber, asChildStatus, asQueueName);
			}
			else if (lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_NEW_FILING))
			{
				HashMap loHmReturnedItems = finishNewFilingTaskItem(aoVWSession, asChildWobNumber, asChildStatus,
						asQueueName);
				loHmFinishResult.put("ReturnedItems", loHmReturnedItems);
			}
			else if (lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_CONTACT_US))
			{
				HashMap loHmReturnedItems = finishContactUsTaskItem(aoVWSession, asChildWobNumber, asQueueName);
				loHmFinishResult.put("ReturnedItems", loHmReturnedItems);
			}
			else if (lsTaskType
					.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_ORGANIZATION_LEGAL_NAME_UPDATE_REQUEST))
			{
				String lsProviderId = (String) loStepElement.getParameterValue(P8Constants.PROPERTY_PE_PROVIDER_ID);
				String lsNewProviderName = (String) loStepElement
						.getParameterValue(P8Constants.PROPERTY_PE_PROVIDER_NEW_NAME);
				HashMap loHmReturnedItems = finishTaskItem(aoVWSession, asChildWobNumber, asChildStatus, asQueueName);
				loHmFinishResult.put("ReturnedItems", loHmReturnedItems);
				if (asChildStatus.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED))
				{
					updateProviderNames(lsProviderId, lsNewProviderName, aoVWSession); // update
																						// all
																						// provider
																						// names
				}
			}
			else
			{
				HashMap loHmReturnedItems = finishTaskItem(aoVWSession, asChildWobNumber, asChildStatus, asQueueName);
				loHmFinishResult.put("ReturnedItems", loHmReturnedItems);
			}
		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.finishChildTask()::", aoAppex);
			throw aoAppex;
		}
		catch (VWException aoVWEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Error while finishing a section child task and all other tasks except BR task", aoVWEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.finishChildTask()::", loAppex);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Error while finishing a section child task and all other tasks except BR task", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.finishChildTask()::", loAppex);
			throw loAppex;
		}
		// return the hash-map containing the task-type as the first key and the
		// terminated wob nos(in case of withdrawal tasks) as the 2nd key
		LOG_OBJECT.Debug("Exited P8ProcessOperations.finishChildTask(). Returned lhmFinishResult::"
				+ loHmFinishResult.toString());
		return loHmFinishResult;
	}

	/**
	 * This function is used for updating the arrays lasSectionTaskStatus and
	 * lasIsSectionTaskStatusUpdated have to be updated in the parent task. In
	 * the parent BR task, the string array lasSectionTaskStatus contains the
	 * current status of each section. The boolean array
	 * lasIsSectionTaskStatusUpdated contains info about whether the status of
	 * the section task has been set or not. The 0th element of the arrays refer
	 * to the basic section, 1st to the board, 2nd to the filings and 3rd to the
	 * policies section. Once the status of all the 4 sections have been set,
	 * then only the status of the parent BR application is set, based on given
	 * business logic
	 * 
	 * @param aoVWSession a VWSession object
	 * @param asParentWobNo a string value of Parent work flow number
	 * @param asChildWobNo a string value of child work flow number
	 * @param asChildStatus a string value of Child Status
	 * @param aiArrayPointer int array pointer
	 * @param asQueueName name of queue
	 * @return boolean variable update parent task is success if its true
	 * @throws ApplicationException
	 */
	public boolean updateParentTaskStatus(VWSession aoVWSession, String asParentWobNo, String asChildWobNo,
			String asChildStatus, int aiArrayPointer, String asQueueName) throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("asQueueName", asQueueName);
		loHmReqExceProp.put("asParentWobNo", asParentWobNo);
		loHmReqExceProp.put("asChildWobNo", asChildWobNo);
		loHmReqExceProp.put("asChildStatus", asChildStatus);
		loHmReqExceProp.put("aiArrayPointer", aiArrayPointer);
		LOG_OBJECT.Debug("Entered P8ProcessOperations.updateParentTaskStatus() with parameters::"
				+ loHmReqExceProp.toString());

		VWQueueQuery loVWQueueQuery = null;
		VWQueue loVWQueue = null;
		VWStepElement loStepElement = null;
		boolean lbIsStatusChanged = false;
		String lsSectionStatus0 = "";
		String lsSectionStatus1 = "";
		String lsSectionStatus2 = "";
		String lsSectionStatus3 = "";
		int liTrueCount = 0;
		// create queue filter for fetching the work item based on wob no.
		String lsQueueFilter = P8Constants.PROPERTY_PE_WOBNUMBER + "='" + asParentWobNo + "'";
		try
		{
			// LOG_OBJECT.Debug("Fetcing parent BR task item for updation");
			loVWQueue = aoVWSession.getQueue(asQueueName);
			if (loVWQueue == null)
			{
				throwApplicationException(
						"Exception in P8ProcessOperations.updateParentTaskStatus():Error While Fetching Queue::"
								+ asQueueName, loHmReqExceProp);
			}

			int liFlag = loVWQueue.QUERY_READ_LOCKED;
			int liType = VWFetchType.FETCH_TYPE_QUEUE_ELEMENT;
			loVWQueueQuery = loVWQueue.createQuery(null, null, null, liFlag, lsQueueFilter, null, liType);
			VWQueueElement loVWQueueElement = (VWQueueElement) loVWQueueQuery.next(); // Query
																						// will
																						// return
																						// only
																						// 1
																						// item
			loStepElement = loVWQueueElement.fetchStepElement(true, true);
			if (loStepElement == null)
			{
				throwApplicationException(
						"Exception in P8ProcessOperations.updateParentTaskStatus():Error While Fetching StepElement from QueueElement",
						loHmReqExceProp);
			}

			// ***************************** Retrieve Parent WF Properties and
			// then do calculation *******************************
			String[] loSectionTaskStatus = (String[]) loStepElement
					.getParameterValue(P8Constants.PROPERTY_PE_SECTION_TASK_STATUS);
			LOG_OBJECT.Debug("Got array SectionTaskStatus from parent " + Arrays.toString(loSectionTaskStatus));
			Boolean[] loIsSectionTaskStatusUpdated = (Boolean[]) loStepElement
					.getParameterValue(P8Constants.PROPERTY_PE_IS_SECTIONS_TASK_UPDATED);
			LOG_OBJECT.Debug("Got array IsSectionsTaskUpdated from parent "
					+ Arrays.toString(loIsSectionTaskStatusUpdated));
			// Update the Status and Section task Flag array.
			loSectionTaskStatus[aiArrayPointer] = asChildStatus.trim();
			LOG_OBJECT.Debug("Updated loSectionTaskStatus[" + aiArrayPointer + " ]: " + asChildStatus.trim());
			loIsSectionTaskStatusUpdated[aiArrayPointer] = true;
			LOG_OBJECT.Debug("Updated loIsSectionTaskStatusUpdated[" + aiArrayPointer + "]: true ");
			loStepElement.setParameterValue(P8Constants.PROPERTY_PE_SECTION_TASK_STATUS, loSectionTaskStatus, false);
			loStepElement.setParameterValue(P8Constants.PROPERTY_PE_IS_SECTIONS_TASK_UPDATED,
					loIsSectionTaskStatusUpdated, false);

			for (int liPointer = 0; liPointer <= 3; liPointer++)
			{
				boolean lbUpdatedStatus = false;
				lbUpdatedStatus = loIsSectionTaskStatusUpdated[liPointer];
				switch (liPointer)
				{
					case 0:
						lsSectionStatus0 = loSectionTaskStatus[liPointer];
						break;
					case 1:
						lsSectionStatus1 = loSectionTaskStatus[liPointer];
						break;
					case 2:
						lsSectionStatus2 = loSectionTaskStatus[liPointer];
						break;
					case 3:
						lsSectionStatus3 = loSectionTaskStatus[liPointer];
						break;
				}
				if (lbUpdatedStatus == false)
				{
					break;
				}
				else
				{
					liTrueCount++;
				}

				// if labTrueCount = 4 update the parent Status on rules
				// combination
				if (liTrueCount == 4)
				{
					String lsCalculatedStatus = calculateStatus(lsSectionStatus0, lsSectionStatus1, lsSectionStatus2,
							lsSectionStatus3);
					loStepElement.setParameterValue(P8Constants.PROPERTY_PE_TASK_STATUS, lsCalculatedStatus, false);
					LOG_OBJECT.Debug("Updates status of parent task item to " + lsCalculatedStatus);
					lbIsStatusChanged = true;
				} // end if
			} // end for
			loStepElement.doSave(true);
			LOG_OBJECT.Debug("Exited P8ProcessOperations.updateParentTaskStatus(). Returnd  lbIsStatusChanged::"
					+ lbIsStatusChanged);
			return lbIsStatusChanged;
		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.updateParentTaskStatus()::", aoAppex);
			throw aoAppex;
		}
		catch (VWException aoVWEx)
		{
			ApplicationException loAppex = new ApplicationException("Error: Multiple Parent Tasks Exist", aoVWEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.updateParentTaskStatus()::", loAppex);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error: Multiple Parent Tasks Exist", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.updateParentTaskStatus()::", loAppex);
			throw loAppex;
		}
	}

	/**
	 * This method calculate Business Application status based on all child task
	 * status
	 * @param asSectionStatus0 String denoting the section status0
	 * @param asSectionStatus1 String denoting the section status1
	 * @param asSectionStatus2 String denoting the section status2
	 * @param asSectionStatus3 String denoting the section status3
	 * @return String lsCalculatedStatus
	 */
	private String calculateStatus(String asSectionStatus0, String asSectionStatus1, String asSectionStatus2,
			String asSectionStatus3)
	{
		String lsCalculatedStatus = null;
		// Approve Rule:
		if (asSectionStatus0.equals(ApplicationConstants.STATUS_APPROVED)
				&& asSectionStatus1.equals(ApplicationConstants.STATUS_APPROVED)
				&& asSectionStatus2.equals(ApplicationConstants.STATUS_APPROVED)
				&& asSectionStatus3.equals(ApplicationConstants.STATUS_APPROVED))
		{
			lsCalculatedStatus = ApplicationConstants.STATUS_APPROVED;
		}
		// Reject Rule:
		if (asSectionStatus0.equals(ApplicationConstants.STATUS_REJECTED)
				|| asSectionStatus1.equals(ApplicationConstants.STATUS_REJECTED)
				|| asSectionStatus2.equals(ApplicationConstants.STATUS_REJECTED)
				|| asSectionStatus3.equals(ApplicationConstants.STATUS_REJECTED))
		{
			lsCalculatedStatus = ApplicationConstants.STATUS_REJECTED;
		}
		// Suspended Rule:
		if (asSectionStatus0.equals(ApplicationConstants.STATUS_SUSPEND)
				|| asSectionStatus1.equals(ApplicationConstants.STATUS_SUSPEND)
				|| asSectionStatus2.equals(ApplicationConstants.STATUS_SUSPEND)
				|| asSectionStatus3.equals(ApplicationConstants.STATUS_SUSPEND)
				&& (!asSectionStatus0.equals(ApplicationConstants.STATUS_REJECTED)
						&& !asSectionStatus1.equals(ApplicationConstants.STATUS_REJECTED)
						&& !asSectionStatus2.equals(ApplicationConstants.STATUS_REJECTED) && !asSectionStatus3
							.equals(ApplicationConstants.STATUS_REJECTED)))
		{
			lsCalculatedStatus = ApplicationConstants.STATUS_SUSPEND;
		}
		// Deferred Rule:
		if ((asSectionStatus0.equals(ApplicationConstants.STATUS_DEFFERED)
				|| asSectionStatus1.equals(ApplicationConstants.STATUS_DEFFERED)
				|| asSectionStatus2.equals(ApplicationConstants.STATUS_DEFFERED) || asSectionStatus3
					.equals(ApplicationConstants.STATUS_DEFFERED))
				&& ((!asSectionStatus0.equals(ApplicationConstants.STATUS_SUSPEND) && !asSectionStatus0
						.equals(ApplicationConstants.STATUS_REJECTED))
						&& (!asSectionStatus1.equals(ApplicationConstants.STATUS_SUSPEND) && !asSectionStatus1
								.equals(ApplicationConstants.STATUS_REJECTED))
						&& (!asSectionStatus2.equals(ApplicationConstants.STATUS_SUSPEND) && !asSectionStatus2
								.equals(ApplicationConstants.STATUS_REJECTED)) && (!asSectionStatus3
						.equals(ApplicationConstants.STATUS_SUSPEND) && !asSectionStatus3
						.equals(ApplicationConstants.STATUS_REJECTED))))
		{
			lsCalculatedStatus = ApplicationConstants.STATUS_DEFFERED;
		}
		// Returned for Revisions Rule:
		if ((asSectionStatus0.equals(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS)
				|| asSectionStatus1.equals(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS)
				|| asSectionStatus2.equals(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS) || asSectionStatus3
					.equals(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS))
				&& ((!asSectionStatus0.equals(ApplicationConstants.STATUS_DEFFERED)
						&& !asSectionStatus0.equals(ApplicationConstants.STATUS_SUSPEND) && !asSectionStatus0
							.equals(ApplicationConstants.STATUS_REJECTED))
						&& (!asSectionStatus1.equals(ApplicationConstants.STATUS_DEFFERED)
								&& !asSectionStatus1.equals(ApplicationConstants.STATUS_SUSPEND) && !asSectionStatus1
									.equals(ApplicationConstants.STATUS_REJECTED))
						&& (!asSectionStatus2.equals(ApplicationConstants.STATUS_DEFFERED)
								&& !asSectionStatus2.equals(ApplicationConstants.STATUS_SUSPEND) && !asSectionStatus2
									.equals(ApplicationConstants.STATUS_REJECTED)) && (!asSectionStatus3
						.equals(ApplicationConstants.STATUS_DEFFERED)
						&& !asSectionStatus3.equals(ApplicationConstants.STATUS_SUSPEND) && !asSectionStatus3
							.equals(ApplicationConstants.STATUS_REJECTED))))
		{
			lsCalculatedStatus = ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS;
		}

		return lsCalculatedStatus;
	}

	/**
	 * This function is used for finishing a parent task (BR Application)
	 * 
	 * @param aoVWSession a VWSession Object
	 * @param asParentWobNo a string value of parent work flow number
	 * @param asQueueName Name of queue
	 * @return String a string value of finished Parent Task
	 * @throws ApplicationException
	 */

	public String finishParentTask(VWSession aoVWSession, String asParentWobNo, String asQueueName)
			throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("asParentWobNo", asParentWobNo);
		loHmReqExceProp.put("asQueueName", asQueueName);
		LOG_OBJECT.Debug("Entered P8ProcessOperations.finishParentTask() with parameters::"
				+ loHmReqExceProp.toString());
		try
		{
			VWStepElement loStepElement = getStepElementfromWobNo(aoVWSession, asParentWobNo, asQueueName);
			if (loStepElement == null)
			{
				throwApplicationException(
						"Exception in P8ProcessOperations.finishParentTask():Error in getting parent step element from wob no. "
								+ asParentWobNo, loHmReqExceProp);
			}

			// fetch the status of the parent which has already been updated by
			// the function updateParentTaskStatus
			String lsParentStatus = (String) loStepElement.getParameterValue(P8Constants.PROPERTY_PE_TASK_STATUS);
			LOG_OBJECT.Debug("Obtained parent status as::" + lsParentStatus);
			// check if it the task is now with a manager
			boolean lbIsManagerReserveStep = Boolean.parseBoolean(loStepElement.getParameterValue(
					P8Constants.PROPERTY_PE_IS_MANAGER_REVIEW_STEP).toString());
			LOG_OBJECT.Debug("Obtained lbIsManagerReserveStep::" + lbIsManagerReserveStep);
			// fetch the wob nos. of the child
			String[] loChildTasksWobNos = (String[]) loStepElement
					.getParameterValue(P8Constants.PROPERTY_PE_SECTION_WF_NOS);
			String lsBRFinishAction;
			String lsParentBRStatus;

			if (lbIsManagerReserveStep == false)
			{
				// if its not a manager step update only the BRFinishAction in
				// the child
				lsBRFinishAction = P8Constants.PROPERTY_PE_FINISH_BR_OWNER_TASK_VALUE;
			}
			else
			{
				// if its a manager step update the ParentBRStatus as well,along
				// with BRFinishAction
				lsBRFinishAction = P8Constants.PROPERTY_PE_FINISH_MANAGER_TASK_VALUE;
			}

			lsParentBRStatus = lsParentStatus; // Used to set the Status in the
												// section to terminate them
												// finally

			for (int liCount = 0; liCount < loChildTasksWobNos.length; liCount++)
			{
				String lsChildWobNo = loChildTasksWobNos[liCount];
				LOG_OBJECT.Debug("Finishing child task item::" + lsChildWobNo);
				VWStepElement loChildStepElement = getStepElementfromWobNo(aoVWSession, lsChildWobNo, asQueueName);
				if (loChildStepElement == null)
				{
					throwApplicationException(
							"Exception in P8ProcessOperations.finishParentTask():Error in getting parent step element from wob no",
							loHmReqExceProp);
				}

				// update the parameter ParentBRStatus in child tasks and
				// dispatch them
				// The value of ParentBRStatus is used to determine if the child
				// tasks can be terminated on finishing parent
				loChildStepElement.setParameterValue(P8Constants.PROPERTY_PE_FINISH_ACTION, lsBRFinishAction, false);
				LOG_OBJECT.Debug("Updated property BRFinishAction for child task item::" + lsChildWobNo + " to "
						+ lsBRFinishAction);
				loChildStepElement.setParameterValue(P8Constants.PROPERTY_PE_PARENT_BR_STATUS, lsParentBRStatus, false);
				LOG_OBJECT.Debug("Updated property ParentBRStatus for child task item::" + lsChildWobNo + " to "
						+ lsParentBRStatus);
				loChildStepElement.setParameterValue(P8Constants.PROPERTY_PE_TASK_MODIFIED_DATE, new Date(), false);
				LOG_OBJECT.Debug("Updated Last Modified Date for child task item::" + lsChildWobNo + " to "
						+ new Date());
				loChildStepElement.doSave(false);
				loChildStepElement.doDispatch(); // dispatch child
				LOG_OBJECT.Debug("Finished child task item::" + lsChildWobNo);
			}
			LOG_OBJECT.Debug("Updated Last Modified Date for parent task item::" + asParentWobNo + " to " + new Date());
			loStepElement.setParameterValue(P8Constants.PROPERTY_PE_TASK_MODIFIED_DATE, new Date(), false);
			loStepElement.doDispatch(); // dispatch parent
			LOG_OBJECT.Debug("Exited P8ProcessOperations.finishParentTask(). Returned lsParentStatus::"
					+ lsParentStatus);
			return lsParentStatus;
		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.finishParentTask()::", aoAppex);
			throw aoAppex;
		}
		catch (VWException aoVWEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Error in finishParentTask while fetching step element::", aoVWEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.finishParentTask()::", loAppex);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Error in finishParentTask while fetching step element::", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.finishParentTask()::", loAppex);
			throw loAppex;
		}
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

	public VWStepElement getStepElementfromWobNo(VWSession aoVWSession, String asWobNumber, String asQueueName)
			throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("asQueueName", asQueueName);
		loHmReqExceProp.put("asWobNumber", asWobNumber);
		LOG_OBJECT.Debug("Entered P8ProcessOperations.getStepElementfromWobNo() with parameters::"
				+ loHmReqExceProp.toString());

		VWQueueQuery loVWQueueQuery = null;
		VWQueue loVWQueue = null;
		VWStepElement loStepElement = null;

		String lsQueueFilter = P8Constants.PROPERTY_PE_WOBNUMBER + "='" + asWobNumber + "'";

		try
		{
			loVWQueue = aoVWSession.getQueue(asQueueName);
			if (loVWQueue == null)
			{
				throwApplicationException(
						"Exception in P8ProcessOperations.getStepElementfromWobNo() : Error While Fetching Queue",
						loHmReqExceProp);
			}

			int liFlag = loVWQueue.QUERY_READ_LOCKED; // read the locked work
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
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.getStepElementfromWobNo()::", aoAppex);
			throw aoAppex;
		}
		catch (VWException aoVWEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Error While Fetching StepElement from QueueElement", aoVWEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.getStepElementfromWobNo()::", loAppex);
			throw loAppex;
		}
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
	 * This method is used for unlocking an work item. Its called for Return to
	 * Inbox
	 * 
	 * @param aoVWSessiona a VWSession object
	 * @param asWobNo a string value of work flow number
	 * @param asQueueName a string value of queue name
	 * @return boolean variable successful unlock if its sucess
	 * @throws ApplicationException
	 */
	public boolean unlockWorkItem(VWSession aoVWSession, String asWobNo, String asQueueName)
			throws ApplicationException
	{
		boolean lbResult = false;
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("asQueueName", asQueueName);
		loHmReqExceProp.put("asWobNo", asWobNo);
		LOG_OBJECT
				.Debug("Entered P8ProcessOperations.unlockWorkItem() with parameters : " + loHmReqExceProp.toString());

		try
		{
			VWStepElement loStepElement = getStepElementfromWobNo(aoVWSession, asWobNo, asQueueName);
			if (loStepElement == null)
			{
				throwApplicationException("Error in P8ProcessOperations.unlockWorkItem() while fetching step element ",
						loHmReqExceProp);
			}

			loStepElement.doAbort();
			lbResult = true;
			return lbResult;
		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.unlockWorkItem()::", aoAppex);
			throw aoAppex;
		}
		catch (VWException aoVWEx)
		{
			ApplicationException loAppex = new ApplicationException("Error while unlocking work item", aoVWEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.unlockWorkItem()::", loAppex);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error while unlocking work itemt", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.unlockWorkItem()::", loAppex);
			throw loAppex;
		}

	}

	/**
	 * This method is used for unlocking a parent work item when the child task
	 * is opened
	 * 
	 * @param aoVWSessiona a VWSession object
	 * @param asChildWobNo a string value of Child work flow number
	 * @param asQueueName a string value of Queue Name
	 * @return boolean variable unlock is success if its true
	 * @throws ApplicationException
	 */
	public boolean unlockParentWorkItem(VWSession aoVWSession, String asChildWobNo, String asQueueName)
			throws ApplicationException
	{
		boolean lbResult = false;
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("asQueueName", asQueueName);
		loHmReqExceProp.put("asChildWobNo", asChildWobNo);

		LOG_OBJECT.Debug("Entered P8ProcessOperations.unlockParentWorkItem() with parameters::"
				+ loHmReqExceProp.toString());

		try
		{
			VWStepElement loStepElement = getStepElementfromWobNo(aoVWSession, asChildWobNo, asQueueName);
			if (loStepElement == null)
			{
				throwApplicationException(
						"Exception in P8ProcessOperations.unlockParentWorkItem():Error in getting step element from wob no. "
								+ asChildWobNo, loHmReqExceProp);
			}

			String lsParentWobNo = (String) loStepElement.getParameterValue(P8Constants.PROPERTY_PE_PARENT_APP_WOB_NO);
			LOG_OBJECT.Debug("Parent work item obtained : " + lsParentWobNo);
			lbResult = unlockWorkItem(aoVWSession, lsParentWobNo, asQueueName);
			return lbResult;
		}
		catch (ApplicationException aoAppex)
		{
			ApplicationException loAppex = new ApplicationException(
					"Error while unlocking parent work item when the child task is opened", aoAppex);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.unlockParentWorkItem()::", loAppex);
			throw loAppex;
		}
		catch (VWException aoVWEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Error while unlocking parent work item when the child task is opened", aoVWEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.unlockParentWorkItem()::", loAppex);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Error while unlocking parent work item when the child task is opened", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.unlockParentWorkItem()::", loAppex);
			throw loAppex;
		}
	}

	/**
	 * This method is used for UI unlocking of BR work items
	 * 
	 * @param aoVWSession a VWSession object
	 * @param asParentWobNo a string value of Parent work flow number
	 * @param aoSectionIds a string array of section Ids
	 * @param asQueueName a string value of Queue Name
	 * @return boolean variable unlock is success if its true
	 * @throws ApplicationException
	 */
	public boolean unlockUIBRWorkItems(VWSession aoVWSession, String asParentWobNo, String[] aoSectionIds,
			String asUpdatedById, String asQueueName) throws ApplicationException
	{
		boolean lbResult = false;
		String lsChildWobNo = null;
		VWStepElement loChildStepElement = null;
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("asQueueName", asQueueName);
		loHmReqExceProp.put("asParentWobNo", asParentWobNo);
		loHmReqExceProp.put("aoSectionIds", aoSectionIds);
		LOG_OBJECT.Debug("Entered P8ProcessOperations.unlockUIBRWorkItems() with parameters::"
				+ loHmReqExceProp.toString());

		try
		{
			VWStepElement loParentStepElement = getStepElementfromWobNo(aoVWSession, asParentWobNo, asQueueName);
			if (loParentStepElement == null)
			{
				throwApplicationException(
						"Exception in P8ProcessOperations.unlockUIBRWorkItems():Error in getting step element from wob no. "
								+ asParentWobNo, loHmReqExceProp);
			}

			// unlock parent
			String[] loSectionIDs = (String[]) loParentStepElement
					.getParameterValue(P8Constants.PROPERTY_PE_SECTION_IDS);
			String[] loSectionTaskStatus = (String[]) loParentStepElement
					.getParameterValue(P8Constants.PROPERTY_PE_SECTION_TASK_STATUS);
			String[] loChildTasksWobNos = (String[]) loParentStepElement
					.getParameterValue(P8Constants.PROPERTY_PE_SECTION_WF_NOS);
			Boolean[] loIsSectionTaskStatusUpdated = (Boolean[]) loParentStepElement
					.getParameterValue(P8Constants.PROPERTY_PE_IS_SECTIONS_TASK_UPDATED);

			// convert parent's section id array to array-list to find index
			ArrayList loParentSectionIds = new ArrayList(Arrays.asList(loSectionIDs));

			loParentStepElement.setParameterValue(P8Constants.PROPERTY_PE_TASK_STATUS,
					ApplicationConstants.STATUS_IN_REVIEW, false);
			loParentStepElement.setParameterValue(P8Constants.PROPERTY_PE_IS_TASK_LOCKED, false, false);
			loParentStepElement.setParameterValue(P8Constants.PROPERTY_PE_LAUNCH_BY, asUpdatedById, false); // updated
																											// for
																											// returned-for-revision

			// Browse through each section ids got
			for (int liCount = 0; liCount < aoSectionIds.length; liCount++)
			{
				int liIndex = loParentSectionIds.indexOf(aoSectionIds[liCount]);
				if (liIndex != -1)
				{
					loSectionTaskStatus[liIndex] = ApplicationConstants.STATUS_IN_REVIEW;
					loIsSectionTaskStatusUpdated[liIndex] = false;

					// unlock child tasks
					lsChildWobNo = loChildTasksWobNos[liIndex];
					loChildStepElement = getStepElementfromWobNo(aoVWSession, lsChildWobNo, asQueueName);
					loChildStepElement.setParameterValue(P8Constants.PROPERTY_PE_TASK_STATUS,
							ApplicationConstants.STATUS_IN_REVIEW, false);
					// child tasks will always be vissible now
					loChildStepElement.setParameterValue(P8Constants.PROPERTY_PE_IS_TASK_VISSIBLE, true, false);
					loChildStepElement.setParameterValue(P8Constants.PROPERTY_PE_TASK_MODIFIED_DATE, new Date(), false);
					loChildStepElement.doSave(true);
					LOG_OBJECT.Debug("Unlocked child work item::" + lsChildWobNo);
				}

			} // end for

			// added for unlocking all child task items as per defect 725
			for (int liChildCount = 0; liChildCount < loChildTasksWobNos.length; liChildCount++)
			{
				String lsSingleChildWobNo = loChildTasksWobNos[liChildCount];

				VWStepElement loSingleChildStepElement = getStepElementfromWobNo(aoVWSession, lsSingleChildWobNo,
						asQueueName);
				loSingleChildStepElement.setParameterValue(P8Constants.PROPERTY_PE_IS_TASK_LOCKED, false, false);
				loSingleChildStepElement.setParameterValue(P8Constants.PROPERTY_PE_LAUNCH_BY, asUpdatedById, false); // updated
																														// for
																														// returned-for-revision
				loSingleChildStepElement.doSave(true);
			}
			loParentStepElement.setParameterValue(P8Constants.PROPERTY_PE_SECTION_TASK_STATUS, loSectionTaskStatus,
					false);
			loParentStepElement.setParameterValue(P8Constants.PROPERTY_PE_IS_SECTIONS_TASK_UPDATED,
					loIsSectionTaskStatusUpdated, false);
			loParentStepElement.setParameterValue(P8Constants.PROPERTY_PE_TASK_MODIFIED_DATE, new Date(), false);
			loParentStepElement.doSave(true);

			lbResult = true;
			LOG_OBJECT.Debug("Exited P8ProcessOperations.unlockUIBRWorkItems(). Returned lbResult::" + lbResult);
			return lbResult;
		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.unlockUIBRWorkItems()::", aoAppex);
			throw aoAppex;
		}
		catch (VWException aoVWEx)
		{
			ApplicationException loAppex = new ApplicationException("Error While UI unlocking of BR work items", aoVWEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.unlockUIBRWorkItems()::", loAppex);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error While UI unlocking of BR work items", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.unlockUIBRWorkItems()::", loAppex);
			throw loAppex;
		}
	}

	/******************* Code begins for Service Capacity and other workflows ***********************/

	/**
	 * This method is used for UI unlocking of SC work items
	 * 
	 * @param aoVWSession a VWSession object
	 * @param asSCWobNo a string value of SC work flow number
	 * @param asQueueName a string value of Queue Name
	 * @return boolean variable unlock is success if its true
	 * @throws ApplicationException
	 */
	public boolean unlockUISCWorkItems(VWSession aoVWSession, String asSCWobNo, String asUpdatedById, String asQueueName)
			throws ApplicationException
	{
		boolean lbResult = false;
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("asQueueName", asQueueName);
		loHmReqExceProp.put("asParentWobNo", asSCWobNo);
		LOG_OBJECT.Debug("Entered P8ProcessOperations.unlockUISCWorkItems() with parameters::"
				+ loHmReqExceProp.toString());

		try
		{
			VWStepElement loStepElement = getStepElementfromWobNo(aoVWSession, asSCWobNo, asQueueName);
			if (loStepElement == null)
			{
				throwApplicationException(
						"Exception in P8ProcessOperations.unlockUISCWorkItems():Error in getting step element from wob no. "
								+ asSCWobNo, loHmReqExceProp);
			}

			// unlock task
			loStepElement.setParameterValue(P8Constants.PROPERTY_PE_TASK_STATUS, "In Review", false);
			loStepElement.setParameterValue(P8Constants.PROPERTY_PE_IS_TASK_LOCKED, false, false);
			loStepElement.setParameterValue(P8Constants.PROPERTY_PE_IS_TASK_VISSIBLE, true, false);
			loStepElement.setParameterValue(P8Constants.PROPERTY_PE_TASK_MODIFIED_DATE, new Date(), false);
			loStepElement.setParameterValue(P8Constants.PROPERTY_PE_LAUNCH_BY, asUpdatedById, false); // updated
																										// for
																										// returned-for-revision
			loStepElement.doSave(true);

			lbResult = true;
			return lbResult;
		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.unlockUISCWorkItems()::", aoAppex);
			throw aoAppex;
		}
		catch (VWException aoVWEx)
		{
			ApplicationException loAppex = new ApplicationException("Error While UI unlocking of BR work items", aoVWEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.unlockUISCWorkItems()::", loAppex);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error While UI unlocking of BR work items", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.unlockUISCWorkItems()::", loAppex);
			throw loAppex;
		}
	}

	/**
	 * Used for reassigning service application
	 * 
	 * @param aoVWSession a VWSession object
	 * @param asWobNumber a string value of Work flow Number
	 * @param asUserName a string value of User Name
	 * @param asSessionUserName a string value of session's user name
	 * @param asQueueName a string value of Queue Name
	 * @return boolean variable assign is success if its true
	 * @throws ApplicationException
	 */
	public boolean assignServiceApplication(VWSession aoVWSession, String asWobNumber, String asUserName,
			String asSessionUserName, String asQueueName, HashMap aoHmServiceCapacityWobNos)
			throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("asWobNumber", asWobNumber);
		loHmReqExceProp.put("asUserName", asUserName);
		loHmReqExceProp.put("asQueueName", asQueueName);
		LOG_OBJECT.Debug("Entered P8ProcessOperations.assignServiceApplication() with parameters::"
				+ loHmReqExceProp.toString());

		boolean lbAssigned = false;

		if (asUserName.equalsIgnoreCase(""))
		{
			throwApplicationException(
					"Exception in P8ProcessOperations.assignServiceApplication():Username Name is not found",
					loHmReqExceProp);
		}
		if (asWobNumber.equalsIgnoreCase(""))
		{
			throwApplicationException(
					"Exception in P8ProcessOperations.assignServiceApplication():asWobNumber is not found::",
					loHmReqExceProp);
		}

		try
		{
			VWStepElement loStepElement = getStepElementfromWobNo(aoVWSession, asWobNumber, asQueueName);
			if (loStepElement == null)
			{
				throwApplicationException("Error in getting step element from wob no." + asWobNumber, loHmReqExceProp);
			}

			// add entry in the hashmap for isChildTaskLaunched=false to
			// indicate that no new service capacity child tasks will be
			// launched
			aoHmServiceCapacityWobNos.put("isChildTaskLaunched", "false");

			boolean lbIsManagerReserveStep = Boolean.parseBoolean(loStepElement.getParameterValue(
					P8Constants.PROPERTY_PE_IS_MANAGER_REVIEW_STEP).toString());
			LOG_OBJECT.Debug("Obtained lbIsManagerReserveStep::" + lbIsManagerReserveStep);
			if (lbIsManagerReserveStep == false)
			{
				loStepElement.setParameterValue(P8Constants.PROPERTY_PE_ASSIGNED_TO, asUserName, false);
				loStepElement.setParameterValue(P8Constants.PROPERTY_PE_ASSIGNED_TO_NAME, asSessionUserName, false);
				LOG_OBJECT.Debug("Set Task owner::" + asUserName);
				LOG_OBJECT.Debug("Set AppTaskOwner::" + asUserName);
			}
			else
			{
				loStepElement.setParameterValue(P8Constants.PROPERTY_PE_ASSIGNED_TO, asUserName, false);
				loStepElement.setParameterValue(P8Constants.PROPERTY_PE_ASSIGNED_TO_NAME, asSessionUserName, false);
				LOG_OBJECT.Debug("Set Task owner::" + asUserName);
			}

			loStepElement.setParameterValue(P8Constants.PROPERTY_PE_TASK_ASSIGN_DATE, new Date(), false);
			loStepElement.setParameterValue(P8Constants.PROPERTY_PE_TASK_MODIFIED_DATE, new Date(), false);
			loStepElement.doSave(true);
		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.assign()::", aoAppex);
			throw aoAppex;
		}
		catch (VWException aoVWEx)
		{
			ApplicationException loAppex = new ApplicationException("Error while assigning Service Application", aoVWEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.assign()::", loAppex);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error while assigning Service Application", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.assign()::", loAppex);
			throw loAppex;
		}
		lbAssigned = true;
		return lbAssigned;
	}

	/**
	 * This function is used for finishing Service Application Task
	 * 
	 * @param aoVWSession a VWSession object
	 * @param asWobNumber a string value of Work flow Number
	 * @param asStatus a string value of Status
	 * @param asQueueName a string value of Queue Name
	 * @return boolean variable finish is success if its true
	 * @throws ApplicationException
	 */
	public boolean finishServiceApplicationTask(VWSession aoVWSession, String asWobNumber, String asStatus,
			String asQueueName) throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("asWobNumber", asWobNumber);
		loHmReqExceProp.put("asStatus", asStatus);
		loHmReqExceProp.put("asQueueName", asQueueName);
		LOG_OBJECT.Debug("Entered P8ProcessOperations.finishServiceApplicationTask() with parameters::"
				+ loHmReqExceProp.toString());

		boolean lbParentStatus = false;
		try
		{
			// fetch the step element of the given wob no.
			VWStepElement loStepElement = getStepElementfromWobNo(aoVWSession, asWobNumber, asQueueName);
			if (loStepElement == null)
			{
				throwApplicationException(
						"Exception in P8ProcessOperations.finishServiceApplicationTask():Error in getting step element from wob no "
								+ asWobNumber, loHmReqExceProp);
			}

			// update the child task status
			loStepElement.setParameterValue(P8Constants.PROPERTY_PE_TASK_STATUS, asStatus, false);
			LOG_OBJECT.Debug("Updated status::" + asStatus + " for SC task Wob no::" + asWobNumber);

			// set the modified date every time
			loStepElement.setParameterValue(P8Constants.PROPERTY_PE_TASK_MODIFIED_DATE, new Date(), false);
			loStepElement.doSave(false); // save, but don't unlock yet
			// complete the task
			loStepElement.doDispatch();
		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.finishServiceApplicationTask()::", aoAppex);
			throw aoAppex;
		}
		catch (VWException aoVWEx)
		{
			ApplicationException loAppex = new ApplicationException("Error while finish ServiceApplication Task",
					aoVWEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.finishServiceApplicationTask()::", loAppex);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error while finish ServiceApplication Task", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.finishServiceApplicationTask()::", loAppex);
			throw loAppex;
		}

		return lbParentStatus;
	}

	/**
	 * This function is used for checking if the currently logged-in user is
	 * same as the current assigned user
	 * 
	 * @param aoVWSession a VWSession Object
	 * @param asWobNo a string value of work flow number
	 * @param asCurrentUserName a string value of Current User Name
	 * @param asQueueName a string value of queue name
	 * @return boolean variable true if valid
	 * @throws ApplicationException
	 */
	public boolean isValidUser(VWSession aoVWSession, String asWobNo, String asCurrentUserName, String asQueueName)
			throws ApplicationException
	{

		boolean lbIsValid = false;
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("asWobNo", asWobNo);
		loHmReqExceProp.put("asCurrentUserName", asCurrentUserName);
		loHmReqExceProp.put("asQueueName", asQueueName);
		LOG_OBJECT.Debug("Entered P8ProcessOperations.isValidUser() with parameters::" + loHmReqExceProp.toString());

		try
		{

			VWStepElement loStepElement = getStepElementfromWobNo(aoVWSession, asWobNo, asQueueName);
			if (loStepElement == null)
			{
				throwApplicationException(
						"Exception in P8ProcessOperations.isValidUser():Error in getting step element from wob no "
								+ asWobNo, loHmReqExceProp);
			}

			String lsAssignedUser = (String) loStepElement.getParameterValue(P8Constants.PROPERTY_PE_ASSIGNED_TO);

			if (asCurrentUserName.equalsIgnoreCase(lsAssignedUser))
			{
				lbIsValid = true;
			}
			loStepElement.doAbort();
			LOG_OBJECT.Debug("Exited P8ProcessOperations.isValidUser(). Returned lbIsValid::" + lbIsValid);
			return lbIsValid;
		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.isValidUser()::", aoAppex);
			throw aoAppex;
		}
		catch (VWException aoVWEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Error in checking if the currently logged-in user is same as the current assigned user", aoVWEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.isValidUser()::", loAppex);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Error in checking if the currently logged-in user is same as the current assigned user", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.isValidUser()::", loAppex);
			throw loAppex;
		}
	}

	/**
	 * This function is used for finishing a BR WithDrawl Task
	 * 
	 * @param aoVWSession a VWSession object
	 * @param asParentWobNo a string value of Parent work flow number
	 * @param asQueueName a string value of Queue Name
	 * @return HashMap a map containing finished BRWithDrawl Task
	 * @throws ApplicationException
	 */

	public HashMap finishBRWithDrawlTask(VWSession aoVWSession, String asParentWobNo, String asQueueName)
			throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("asQueueName", asQueueName);
		loHmReqExceProp.put("asParentWobNo", asParentWobNo);
		LOG_OBJECT.Debug("Entered P8ProcessOperations.finishBRWithDrawlTask() with parameters : "
				+ loHmReqExceProp.toString());

		HashMap loHmFinishResult = new HashMap();

		// The array list loTerminatedSecIDs used to maintain a list of all
		// section ids which have been terminated
		ArrayList loTerminatedSecIDs = new ArrayList();
		String lsSectionId = "";

		try
		{

			VWStepElement loStepElement = getStepElementfromWobNo(aoVWSession, asParentWobNo, asQueueName);
			if (loStepElement == null)
			{
				loHmFinishResult.put("Terminated", "true");
				return loHmFinishResult;
			}

			String[] loChildTasksWobNos = (String[]) loStepElement
					.getParameterValue(P8Constants.PROPERTY_PE_SECTION_WF_NOS);

			if (loChildTasksWobNos.length == 4)
			{
				for (int liChildCount = 0; liChildCount < loChildTasksWobNos.length; liChildCount++)
				{
					// Terminate all child tasks
					String lsChildWobNo = loChildTasksWobNos[liChildCount];

					VWStepElement loChildStepElement = getStepElementfromWobNo(aoVWSession, lsChildWobNo, asQueueName);
					if (loChildStepElement == null)
					{
						throwApplicationException(
								"Exception in finishBRWithDrawlTask():Error in getting child step element from wob no. "
										+ lsChildWobNo, loHmReqExceProp);
					}

					lsSectionId = (String) loStepElement.getParameterValue(P8Constants.PROPERTY_PE_SECTION_ID);
					loTerminatedSecIDs.add(lsSectionId);
					loChildStepElement.fetchWorkObject(true, true).doTerminate();
					LOG_OBJECT.Debug("Terminated child task item with wob no::" + lsChildWobNo + " and Section Id::"
							+ lsSectionId);
				}
			}

			// Now terminate the parent
			loStepElement.fetchWorkObject(true, true).doTerminate();
			LOG_OBJECT.Debug("Terminated parent task item with wob no::" + asParentWobNo + " and Section Id::"
					+ lsSectionId);

			loHmFinishResult.put("Terminated", "true");
			loHmFinishResult.put("SectionIDs", loTerminatedSecIDs);
		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.finishBRWithDrawlTask()::", aoAppex);
			throw aoAppex;
		}
		catch (VWException aoVWEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in finishing BR WithDrawl Task", aoVWEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.finishBRWithDrawlTask()::", loAppex);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in finishing BR WithDrawl Task", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.finishBRWithDrawlTask()::", loAppex);
			throw loAppex;
		}

		LOG_OBJECT.Debug("Exited P8ProcessOperations.finishBRWithDrawlTask(). Returned  loHmFinishResult::"
				+ loHmFinishResult);
		return loHmFinishResult;
	}

	/**
	 * This function is used for finishing a Service Application WithDrawl Task
	 * 
	 * @param aoVWSession a VWSession object
	 * @param asSCWobNo a string value of SC work flow number
	 * @param asQueueName a string value of Queue Name
	 * @return HashMap a map containing finished SCWithDrawl Tasks
	 * @throws ApplicationException
	 */

	public HashMap finishSCWithDrawlTask(VWSession aoVWSession, String asSCWobNo, String asQueueName)
			throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("asQueueName", asQueueName);
		loHmReqExceProp.put("asSCWobNo", asSCWobNo);
		LOG_OBJECT.Debug("Entered P8ProcessOperations.finishSCWithDrawlTask() with parameters : "
				+ loHmReqExceProp.toString());

		HashMap loHmFinishResult = new HashMap();
		// The array list loTerminatedSecIDs used to maintain a list of all
		// section ids which have been terminated
		ArrayList loTerminatedSecIDs = new ArrayList();
		String lsSectionId = "";

		try
		{

			VWStepElement loStepElement = getStepElementfromWobNo(aoVWSession, asSCWobNo, asQueueName);
			if (loStepElement == null)
			{
				loHmFinishResult.put("Terminated", "true");
				return loHmFinishResult;
			}

			// Terminate parent
			lsSectionId = (String) loStepElement.getParameterValue(P8Constants.PROPERTY_PE_SECTION_ID);
			loTerminatedSecIDs.add(lsSectionId);
			loStepElement.fetchWorkObject(true, true).doTerminate();
			LOG_OBJECT.Debug("Terminated SC task item with wob no::" + asSCWobNo + " and Section Id::" + lsSectionId);

			loHmFinishResult.put("Terminated", "true");
			loHmFinishResult.put("ServiceIDs", loTerminatedSecIDs);

		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.finishSCWithDrawlTask()::", aoAppex);
			throw aoAppex;
		}
		catch (VWException aoVWEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in finish SC WithDrawl Task", aoVWEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.finishSCWithDrawlTask()::", loAppex);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in finish SC WithDrawl Task", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.finishSCWithDrawlTask()::", loAppex);
			throw loAppex;
		}

		LOG_OBJECT.Debug("Exited P8ProcessOperations.finishSCWithDrawlTask(). Returned loHmFinishResult::"
				+ loHmFinishResult.toString());
		return loHmFinishResult;
	}

	/**
	 * This function is used for finishing a New Filing task item
	 * 
	 * @param aoVWSession a VWSession Object
	 * @param asWobNo a string value of parent work flow number
	 * @param asFinishStatus a string value of finished status
	 * @param asQueueName a string value of queue name
	 * @return HashMap a map containing finished NewFiling Task items
	 * @throws ApplicationException
	 */

	public HashMap finishNewFilingTaskItem(VWSession aoVWSession, String asWobNo, String asFinishStatus,
			String asQueueName) throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("asQueueName", asQueueName);
		loHmReqExceProp.put("asWobNo", asWobNo);
		loHmReqExceProp.put("asFinishStatus", asFinishStatus);
		LOG_OBJECT.Debug("Entered P8ProcessOperations.finishNewFilingTaskItem() with parameters::"
				+ loHmReqExceProp.toString());

		HashMap loHmReturnedItems = null;
		try
		{

			VWStepElement loStepElement = getStepElementfromWobNo(aoVWSession, asWobNo, asQueueName);
			if (loStepElement == null)
			{
				throwApplicationException(
						"Exception in P8ProcessOperations.finishNewFilingTaskItem():Error in getting step element from wob no::"
								+ asWobNo, loHmReqExceProp);
			}

			// Return the required values
			HashMap loHmRequiredProps = new HashMap();
			loHmRequiredProps.put(P8Constants.PROPERTY_PE_PROVIDER_ID, "");
			loHmRequiredProps.put(P8Constants.PROPERTY_PE_APPLICTION_ID, "");
			loHmRequiredProps.put(P8Constants.PROPERTY_PE_TASK_STATUS, "");
			loHmRequiredProps.put(P8Constants.PROPERTY_PE_LAST_UPLOADED_DOC_TYPE, "");
			loHmRequiredProps.put(P8Constants.PROPERTY_PE_UPLOADED_DOC_TYPE, "");
			loHmRequiredProps.put(P8Constants.PROPERTY_PE_NEXT_PERIOD_COVERED_END_MONTH, "");
			loHmRequiredProps.put(P8Constants.PROPERTY_PE_NEXT_PERIOD_COVERED_END_YEAR, "");
			loHmRequiredProps.put(P8Constants.PROPERTY_PE_NEXT_PERIOD_COVERED_START_MONTH, "");
			loHmRequiredProps.put(P8Constants.PROPERTY_PE_NEXT_PERIOD_COVERED_START_YEAR, "");
			loHmRequiredProps.put(P8Constants.PROPERTY_PE_CURRENT_DUE_DATE, "");
			loHmRequiredProps.put(P8Constants.PROPERTY_PE_IS_SHORT_FILING, "");
			loHmRequiredProps.put(P8Constants.PROPERTY_PE_APPLICABLE_LAW, "");

			// Fetch the required values to be returned for New Filing task item
			loHmReturnedItems = getVWParamtersValues(loStepElement, loHmRequiredProps);

			// Complete the child task
			loStepElement.doDispatch();
		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.finishNewFilingTaskItem()::", aoAppex);
			throw aoAppex;
		}
		catch (VWException aoVWEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in finish SC WithDrawl Task", aoVWEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.finishNewFilingTaskItem()::", loAppex);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in finish SC WithDrawl Task", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.finishNewFilingTaskItem()::", loAppex);
			throw loAppex;
		}

		// Return the values
		LOG_OBJECT.Debug("Exited P8ProcessOperations.finishNewFilingTaskItem(). Returned loHmReturnedItems::"
				+ loHmReturnedItems.toString());
		return loHmReturnedItems;
	}

	/**
	 * This function is used for finishing a Contact Us task item
	 * 
	 * @param aoVWSession a VWSession Object
	 * @param asWobNumber a string value of work flow number
	 * @param asQueueName a string value of parent work flow number Queue Name
	 * @return loHmReturnedItems a map containing Contact Us Task Item list
	 */

	private HashMap finishContactUsTaskItem(VWSession aoVWSession, String asWobNumber, String asQueueName)
			throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("asQueueName", asQueueName);
		loHmReqExceProp.put("asWobNumber", asWobNumber);
		LOG_OBJECT.Debug("Entered P8ProcessOperations.finishContactUsTaskItem() with parameters::"
				+ loHmReqExceProp.toString());

		HashMap loHmReturnedItems = null;

		try
		{
			VWStepElement loStepElement = getStepElementfromWobNo(aoVWSession, asWobNumber, asQueueName);
			if (loStepElement == null)
			{
				throwApplicationException(
						"Exception in P8ProcessOperations.finishContactUsTaskItem():Error in getting step element from wob no::"
								+ asWobNumber, loHmReqExceProp);
			}

			loStepElement.setParameterValue(P8Constants.PROPERTY_PE_TASK_STATUS,
					P8Constants.PROPERTY_CONTACT_US_STATUS_VALUE, false);
			loStepElement.doSave(false);

			// Return the required values
			HashMap loHmRequiredProps = new HashMap();
			loHmRequiredProps.put(P8Constants.PROPERTY_PE_APPLICTION_ID, "");
			loHmRequiredProps.put(P8Constants.PROPERTY_PE_TASK_STATUS, "");

			// Fetch the required values to be returned for the Contact Us task
			// item
			loHmReturnedItems = getVWParamtersValues(loStepElement, loHmRequiredProps);

			// complete the child task
			loStepElement.doDispatch();
		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.finishContactUsTaskItem()::", aoAppex);
			throw aoAppex;
		}
		catch (VWException aoVWEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in finish SC WithDrawl Task", aoVWEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.finishContactUsTaskItem()::", loAppex);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in finish SC WithDrawl Task", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.finishContactUsTaskItem()::", loAppex);
			throw loAppex;
		}

		// Return the required values
		LOG_OBJECT.Debug("Exited P8ProcessOperations.finishContactUsTaskItem(). Returned loHmReturnedItems::"
				+ loHmReturnedItems.toString());
		return loHmReturnedItems;
	}

	/**
	 * This function is used for finishing a task item
	 * 
	 * @param aoVWSession a VWSession object
	 * @param asWobNo a string value of work flow number
	 * @param a string value of Finished Status
	 * @param asQueueName a string value of Queue Name
	 * @return a map containing finished Task Item
	 * @throws ApplicationException
	 */

	public HashMap finishTaskItem(VWSession aoVWSession, String asWobNo, String asFinishStatus, String asQueueName)
			throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("asQueueName", asQueueName);
		loHmReqExceProp.put("asWobNo", asWobNo);
		loHmReqExceProp.put("asFinishStatus", asFinishStatus);
		LOG_OBJECT.Debug("Entered P8ProcessOperations.finishTaskItem() with parameters::" + loHmReqExceProp.toString());

		HashMap loHmReturnedItems = null;

		try
		{

			VWStepElement loStepElement = getStepElementfromWobNo(aoVWSession, asWobNo, asQueueName);
			if (loStepElement == null)
			{
				throwApplicationException(
						"Exception in P8ProcessOperations.finishTaskItem():Error in gettingstep element from wob no::"
								+ asWobNo, loHmReqExceProp);
			}

			// update the child task status
			if (null != asFinishStatus)
			{
				loStepElement.setParameterValue(P8Constants.PROPERTY_PE_TASK_STATUS, asFinishStatus, false);
			}

			// set the modified date every time
			loStepElement.setParameterValue(P8Constants.PROPERTY_PE_TASK_MODIFIED_DATE, new Date(), false);
			loStepElement.doSave(false); // save, but don't unlock yet

			// Return the required values
			HashMap loHmRequiredProps = new HashMap();
			loHmRequiredProps.put(P8Constants.PROPERTY_PE_APPLICTION_ID, "");
			loHmRequiredProps.put(P8Constants.PROPERTY_PE_TASK_STATUS, "");

			// Fetch the required values to be returned
			loHmReturnedItems = getVWParamtersValues(loStepElement, loHmRequiredProps);

			// complete the child task
			loStepElement.doDispatch();

		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.finishTaskItem()::", aoAppex);
			throw aoAppex;
		}
		catch (VWException aoVWEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in finish SC WithDrawl Task", aoVWEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.finishTaskItem()::", loAppex);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in finish SC WithDrawl Task", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.finishTaskItem()::", loAppex);
			throw loAppex;
		}

		LOG_OBJECT.Debug("Exited P8ProcessOperations.finishTaskItem(). Returned lohmReturnedItems::"
				+ loHmReturnedItems.toString());
		return loHmReturnedItems;

	}

	/**
	 * This function is used for terminating a work item
	 * 
	 * @param aoVWSession a VWSession object
	 * @param asWobNo a string value of work flow number
	 * @param asQueueName a string value of queue name
	 * @return terminate is succes if true
	 * @throws ApplicationException
	 */

	public boolean terminateWorkItem(VWSession aoVWSession, String asWobNo, String asQueueName)
			throws ApplicationException
	{
		boolean lbTerminated = false;

		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("asQueueName", asQueueName);
		loHmReqExceProp.put("asWobNo", asWobNo);

		LOG_OBJECT.Debug("Entered P8ProcessOperations.terminateWorkItem() with parameters::"
				+ loHmReqExceProp.toString());

		try
		{
			VWStepElement loStepElement = getStepElementfromWobNo(aoVWSession, asWobNo, asQueueName);

			if (loStepElement == null)
			{
				// Step element not found for the given wob no.
				LOG_OBJECT.Debug("Step element not found for the given wob no::" + asWobNo);
				return true;
			}

			loStepElement.fetchWorkObject(true, true).doTerminate();

			lbTerminated = true;

		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.terminateWorkItem()::", aoAppex);
			throw aoAppex;
		}
		catch (VWException aoVWEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in terminateWorkItem Task", aoVWEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.terminateWorkItem()::", loAppex);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in terminateWorkItem Task", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.terminateWorkItem()::", loAppex);
			throw loAppex;
		}

		LOG_OBJECT.Debug("Exited P8ProcessOperations.terminateWorkItem(). Returned lbTerminated::" + lbTerminated);
		return lbTerminated;

	}

	/**
	 * This method is used for updating the audit tables on launching of
	 * workflows
	 * 
	 * @param asWorkflowName a string value of Workflow Name
	 * @param aoHmReqdWorkflowProperties a map containing Workflow Properties
	 * @throws ApplicationException
	 */

	private void updateAuditInfoDuringWorkFlowLaunch(String asWorkflowName, HashMap aoHmReqdWorkflowProperties,
			String asSessionUserName) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered P8ProcessOperations.updateAuditInfoDuringWorkFlowLaunch() with worklow name::"
				+ asWorkflowName);
		// Fetch values from HashMap
		String lsAppId = (String) aoHmReqdWorkflowProperties.get(P8Constants.PROPERTY_PE_APPLICTION_ID);
		String lsProviderId = (String) aoHmReqdWorkflowProperties.get(P8Constants.PROPERTY_PE_PROVIDER_ID);
		String lsLaunchedBy = (String) aoHmReqdWorkflowProperties.get(P8Constants.PROPERTY_PE_LAUNCH_BY);
		String lsTaskDisplayName = "";
		String lsEntityTypeDisplayName = "";
		String lsTaskType = "";
		String lsSectionId = "";
		String lsEntityId = "";
		String lsEntityIdentifier = "";

		// For Section and Service Application tasks, we will be getting the
		// section id from the hash-map
		// For all other workflows, section id would be the same as application
		// id, which is being set in the workflow itself

		if (asWorkflowName.equalsIgnoreCase(P8Constants.PROPERTY_BR_APPLICATION_WORKFLOW_NAME)) // BR
																								// application
		{
			lsTaskType = P8Constants.PROPERTY_PE_TASK_TYPE_BR_APPLICATION;
			lsSectionId = "";
			lsEntityId = lsAppId;
			lsTaskDisplayName = lsTaskType;
			lsEntityTypeDisplayName = lsTaskType;
			lsEntityIdentifier = lsTaskType;
		}
		else if (asWorkflowName.equalsIgnoreCase(P8Constants.PROPERTY_SERVICE_CAPACITY_WORKFLOW_NAME)) // Service
																										// application
		{
			lsSectionId = (String) aoHmReqdWorkflowProperties.get(P8Constants.PROPERTY_PE_SECTION_ID);
			lsTaskType = P8Constants.PROPERTY_PE_TASK_TYPE_SERVICE_APPLICATION;
			lsEntityId = lsSectionId;
			lsTaskDisplayName = lsTaskType;
			lsEntityTypeDisplayName = lsTaskType;
			lsEntityIdentifier = lsTaskType;
		}
		else if (asWorkflowName.equalsIgnoreCase(P8Constants.PROPERTY_BUSINESS_APPLICATION_WITHDRAWL_WORKFLOW_NAME)) // BR
																														// withdrawl
		{
			lsSectionId = "";
			lsTaskType = P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_BUSINESS_REVIEW_APPLICATION;
			lsEntityId = (String) aoHmReqdWorkflowProperties.get(P8Constants.PROPERTY_PE_ENTITY_ID); // Here
																										// Entity
																										// Id
																										// is
																										// BR
																										// Withdrawl
																										// Id
			lsTaskDisplayName = lsTaskType;
			lsEntityTypeDisplayName = lsTaskType;
			lsEntityIdentifier = lsTaskType;
		}
		else if (asWorkflowName.equalsIgnoreCase(P8Constants.PROPERTY_SERVICE_APPLICATION_WITHDRAWL_WORKFLOW_NAME)) // Service
																													// withdrawl
		{
			lsSectionId = (String) aoHmReqdWorkflowProperties.get(P8Constants.PROPERTY_PE_SECTION_ID);
			lsTaskType = P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_SERVICE_APPLICATION;
			lsEntityId = (String) aoHmReqdWorkflowProperties.get(P8Constants.PROPERTY_PE_ENTITY_ID); // Here
																										// EntityId
																										// is
																										// SC_Withdrawl_ID
			lsTaskDisplayName = lsTaskType;
			lsEntityTypeDisplayName = lsTaskType;
			lsEntityIdentifier = lsTaskType;
		}
		else if (asWorkflowName.equalsIgnoreCase(P8Constants.PROPERTY_CONTACT_US_WORKFLOW_NAME)) // Contact
																									// Us
		{
			lsEntityId = lsAppId; // here AppId is the contact us id
			lsTaskType = P8Constants.PROPERTY_PE_TASK_TYPE_CONTACT_US;
			lsTaskDisplayName = lsTaskType;
			lsEntityTypeDisplayName = lsTaskType;
			lsEntityIdentifier = lsTaskType;
		}
		else if (asWorkflowName.equalsIgnoreCase(P8Constants.PROPERTY_NEW_FILING_WORKFLOW_NAME)) // New
																									// filing
		{
			lsEntityId = lsAppId; // here AppId is the documentID
			lsTaskType = P8Constants.PROPERTY_PE_TASK_TYPE_NEW_FILING;
			lsTaskDisplayName = lsTaskType;
			lsEntityTypeDisplayName = lsTaskType;
			lsEntityIdentifier = lsTaskType;
		}
		else if (asWorkflowName.equalsIgnoreCase(P8Constants.PROPERTY_ORG_ACC_REQ_WORKFLOW_NAME)) // Org
																									// account
																									// request
		{
			lsEntityId = lsAppId; // here AppId is the documentID
			lsTaskType = P8Constants.PROPERTY_PE_TASK_TYPE_PROVIDER_ACCOUNT_REQUEST;
			lsTaskDisplayName = lsTaskType;
			lsEntityTypeDisplayName = lsTaskType;
			lsEntityIdentifier = lsTaskType;
		}
		else if (asWorkflowName.equalsIgnoreCase(P8Constants.PROPERTY_ORG_LEGAL_NAME_UPDATE_WORKFLOW_NAME)) // Org
																											// legal
																											// name
																											// change
		{
			lsEntityId = lsAppId; // here AppId is the documentID
			lsTaskType = P8Constants.PROPERTY_PE_TASK_TYPE_ORGANIZATION_LEGAL_NAME_UPDATE_REQUEST;
			lsTaskDisplayName = lsTaskType;
			lsEntityTypeDisplayName = lsTaskType;
			lsEntityIdentifier = lsTaskType;
		}
		else
		{ // All section tasks - ie. basic,board,filings, policies
			lsSectionId = (String) aoHmReqdWorkflowProperties.get(P8Constants.PROPERTY_PE_SECTION_ID);
			lsTaskType = (String) aoHmReqdWorkflowProperties.get(P8Constants.PROPERTY_PE_TASK_TYPE);
			lsEntityId = lsSectionId;
			String[] lsEventTypeArray = lsTaskType.split(":");
			lsTaskDisplayName = lsEventTypeArray[lsEventTypeArray.length - 1].trim();
			lsEntityTypeDisplayName = "Section";
			lsEntityIdentifier = lsTaskType; // modified as per defect 789
		}
		// call the audit services
		callAudit(lsProviderId, lsTaskDisplayName, lsLaunchedBy, lsTaskType, lsEntityTypeDisplayName, lsEntityId,
				lsAppId, lsSectionId, lsEntityIdentifier, asSessionUserName);
		LOG_OBJECT.Debug("Exited P8ProcessOperations.updateAuditInfoDuringWorkFlowLaunch() ");
	}

	/**
	 * This functon is used for calling the audit transaction
	 * 
	 * @param aoChannel The channel object
	 * @param asTaskType String task type
	 * @throws ApplicationException
	 */
	public void callAudit(String asProviderId, String asTaskDisplayName, String asLaunchedBy, String asTaskType,
			String asEntityTypeDisplayName, String asEntityId, String asAppId, String asSectionId,
			String asEntityIdentifier, String asSessionUserName) throws ApplicationException
	{
		String lsData = "";
		try
		{

			LOG_OBJECT.Debug("Entered P8ProcessOperations.callAudit() ");
			Channel aoChannel = new Channel();
			HashMap loProps = new HashMap();
			loProps.put("launchBy", asLaunchedBy);
			aoChannel.setData("loprops", loProps);
			TransactionManager.executeTransaction(aoChannel, "GetEmail");
			String lsEmailID = (String) aoChannel.getData("email_id");
			if (lsEmailID != null && !lsEmailID.isEmpty())
			{
				asLaunchedBy = lsEmailID;
			}
			if (asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_BASIC)
					|| asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_BOARD)
					|| asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_FILINGS)
					|| asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_POLICIES))
			{
				lsData = "Task Assigned To : " + asSessionUserName;
			}
			else
			{
				lsData = "Task Assigned To : Unassigned ";
			}

			Channel loChannel = new Channel();
			loChannel.setData("orgId", asProviderId);
			loChannel.setData("eventName", P8Constants.PROPERTY_PE_TH_TASK_CREATION);
			loChannel.setData("eventType", P8Constants.EVENT_TYPE_WORKFLOW);
			loChannel.setData("auditDate", DateUtil.getFormattedDated("dd/MM/yyyy HH:mm:ss", new Date()));
			loChannel.setData("userId", asLaunchedBy);
			loChannel.setData("data", lsData);
			loChannel.setData("entityType", asEntityTypeDisplayName);
			loChannel.setData("entityId", asEntityId);
			loChannel.setData("providerFlag", "false");
			loChannel.setData("appId", asAppId);
			loChannel.setData("sectionId", asSectionId);
			loChannel.setData("EntityIdentifier", asEntityIdentifier);

			if (asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_BASIC)
					|| asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_FILINGS)
					|| asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_BOARD)
					|| asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_POLICIES)
					|| asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_BR_APPLICATION)
					|| asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SERVICE_APPLICATION)
					|| asTaskType
							.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_BUSINESS_REVIEW_APPLICATION)
					|| asTaskType
							.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_SERVICE_APPLICATION))
			{
				loChannel.setData("asAuditType", "application");
			}
			else
			{
				loChannel.setData("asAuditType", "general");
			}

			TransactionManager.executeTransaction(loChannel, "AuditInformation");
		}
		catch (ApplicationException aoAppex)
		{
			LOG_OBJECT.Error("Exception in P8ProcessOperations.terminateWorkItem()::", aoAppex);
			throw aoAppex;
		}

		LOG_OBJECT.Debug("Exited P8ProcessOperations.callAudit() ");

	}

	/**
	 * This method is used for updating the audit tables on launching of
	 * workflows
	 * 
	 * @param asWorkflowName a string value of Workflow Name
	 * @param aoHmReqdWorkflowProperties a map containing Workflow Properties
	 * @throws ApplicationException
	 */
	private void updateAuditDuringTaskAssign(String asProviderId, String asTaskDisplayName, String asLaunchBy,
			String asEntityTypeDisplayName, String asEntityId, String asAppId, String asSectionId,
			String asSessionUserName, String asTaskType, String asEntityIdentifier, String asUserForAudit)
			throws ApplicationException
	{

		LOG_OBJECT.Debug("Entered P8ProcessOperations.updateAuditDuringTaskAssign() ");

		// /*******************************************************
		// Do auditing
		// ********************************************************
		Channel loChannel = new Channel();
		loChannel.setData("orgId", asProviderId);
		loChannel.setData("eventName", P8Constants.EVENT_NAME_ASSIGN);
		loChannel.setData("eventType", P8Constants.EVENT_TYPE_ASSIGN);
		loChannel.setData("auditDate", DateUtil.getFormattedDated("dd/MM/yyyy HH:mm:ss", new Date()));
		loChannel.setData("userId", asUserForAudit);
		loChannel.setData("data", P8Constants.PROPERTY_PE_TH_TASK_ASSIGNED_TO + " : " + asSessionUserName);
		loChannel.setData("entityType", asEntityTypeDisplayName);
		loChannel.setData("entityId", asEntityId);
		loChannel.setData("providerFlag", "false");
		loChannel.setData("appId", asAppId);
		loChannel.setData("sectionId", asSectionId);
		loChannel.setData("EntityIdentifier", asEntityIdentifier);

		if (asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_BASIC)
				|| asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_FILINGS)
				|| asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_BOARD)
				|| asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_POLICIES)
				|| asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_BR_APPLICATION)
				|| asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SERVICE_APPLICATION)
				|| asTaskType
						.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_BUSINESS_REVIEW_APPLICATION)
				|| asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_SERVICE_APPLICATION))
		{
			loChannel.setData("asAuditType", "application");
		}
		else
		{
			loChannel.setData("asAuditType", "general");
		}

		TransactionManager.executeTransaction(loChannel, "AuditInformation");

		LOG_OBJECT.Debug("Exited P8ProcessOperations.updateAuditDuringTaskAssign() ");
	}

	/**
	 * This function is used for updating the provider names to
	 * asNewProviderName for all providers in all tasks
	 * 
	 * @param asProviderId a string value of provider id
	 * @param asNewProviderName a string value of New Provider Name
	 * @param aoVWSession a VWSession object
	 * @return boolean update is success if its true
	 * @throws Exception
	 */
	public boolean updateProviderNames(String asProviderId, String asNewProviderName, VWSession aoVWSession)
			throws Exception
	{
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("asProviderId", asProviderId);
		loHmReqExceProp.put("asNewProviderName", asNewProviderName);
		LOG_OBJECT.Debug("Entered P8ProcessOperations.updateProviderNames() with parameters::"
				+ loHmReqExceProp.toString());

		boolean lbUpdated = false;

		VWQueueQuery loVWQueueQuery = null;
		VWQueue loVWQueue = null;
		VWStepElement loStepElement = null;

		String lsQueueFilter = P8Constants.PROPERTY_PE_PROVIDER_ID + " = '" + asProviderId + "'";

		LOG_OBJECT.Debug("Obtained lsQueueFilter::" + lsQueueFilter);

		try
		{

			loVWQueue = aoVWSession.getQueue(P8Constants.HSS_QUEUE_NAME);
			if (loVWQueue == null)
			{
				throwApplicationException("Exception in updateProviderNames() : Error While Fetching Queue",
						loHmReqExceProp);
			}

			int liFlag = loVWQueue.QUERY_READ_LOCKED; // read the locked work
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
							"Exception in updateProviderNames() : Error While Fetching StepElement from QueueElement",
							loHmReqExceProp);
				}

				loStepElement.setParameterValue(P8Constants.PROVIDER_NAME, asNewProviderName, false);
				loStepElement.doSave(true);

				LOG_OBJECT.Debug("Updated ProviderName=" + asNewProviderName + " for WobNo "
						+ loStepElement.getWorkflowNumber());
			}

		}
		catch (VWException aoVWEx)
		{
			LOG_OBJECT.Error("Exception in updateProviderNames", aoVWEx);
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception in updateProviderNames", aoEx);
		}

		return lbUpdated;
	}

	/**
	 * This function is used for Force Full Suspension of task
	 * 
	 * @param aoVWSession Is use to establesh a PE session
	 * @asWobNo a string value of task WF Number
	 * @asTaskType a string value of Process task Type
	 * @throws ApplicationException If an application exception occurred
	 */

	public boolean forcefullySuspendTaskItem(VWSession aoVWSession, String asWobNo, String asTaskType,
			String asQueueName) throws ApplicationException
	{

		boolean lbSuspended = false;
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("asQueueName", asQueueName);
		loHmReqExceProp.put("asWobNo", asWobNo);
		loHmReqExceProp.put("asTaskType", asTaskType);

		LOG_OBJECT.Debug("Entered forcefullySuspendTaskItem() with parameters::" + loHmReqExceProp.toString());

		try
		{
			VWStepElement loStepElement = getStepElementfromWobNo(aoVWSession, asWobNo, asQueueName);

			String lsOrgId = (String) loStepElement.getParameterValue(P8Constants.PROPERTY_PE_PROVIDER_ID);
			String lsEntityId = (String) loStepElement.getParameterValue(P8Constants.PROPERTY_PE_ENTITY_ID);

			if (asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_BR_APPLICATION))
			{
				String[] loChildTasksWobNos = (String[]) loStepElement
						.getParameterValue(P8Constants.PROPERTY_PE_SECTION_WF_NOS);
				for (int liCount = 0; liCount < loChildTasksWobNos.length; liCount++)
				{
					String lsChildWobNo = loChildTasksWobNos[liCount];
					terminateWorkItem(aoVWSession, lsChildWobNo, asQueueName);
					LOG_OBJECT.Debug("Terminated task item with wob no::" + lsChildWobNo);
				}
			}

			loStepElement.setParameterValue(P8Constants.PROPERTY_PE_TASK_STATUS, ApplicationConstants.STATUS_SUSPEND,
					false);
			loStepElement.doSave(false);
			LOG_OBJECT.Debug("Updated status::" + ApplicationConstants.STATUS_SUSPEND + " for task Wob no::" + asWobNo
					+ " having org id = " + lsOrgId + " and entity id = " + lsEntityId);

			loStepElement.doDispatch();

			lbSuspended = true;
		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.forcefullySuspendTaskItem()::", aoAppex);
			throw aoAppex;
		}
		catch (VWException aoVWEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in forcefullySuspendTaskItem", aoVWEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.forcefullySuspendTaskItem()::", loAppex);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in forcefullySuspendTaskItem", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.forcefullySuspendTaskItem()::", loAppex);
			throw loAppex;
		}
		return lbSuspended;
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
	 * 
	 * @param aoVWSession : Process session
	 * @param aoHmReqdWorkflowProperties: Hash map set by the application before
	 *            process launch
	 * @return : WNo. if exist Or blank string if do not exist
	 */
	/**
	 * This method is used for checking if there already exists any other BR or
	 * SC workflows in the system of the given App Id or SectionId
	 */
	public String isTaskExist(VWSession aoVWSession, String asWorkflowName, HashMap aoHmReqdWorkflowProperties)
			throws ApplicationException
	{
		String lsWbNumber = "";
		LOG_OBJECT.Debug("Entered P8ProcessOperations.isTaskExist()");
		String lsQueueFilter = "";
		StringBuffer loStrBufferQueueFilter = new StringBuffer("");
		VWQueueQuery loVWQueueQuery = null;
		VWQueue loVWQueue = null;
		VWStepElement loStepElement = null;
		try
		{
			LOG_OBJECT.Debug("Creating loStrBufferQueueFilter");
			loStrBufferQueueFilter.append("( ");
			LOG_OBJECT.Debug("Checking condition for various task types ");

			if (asWorkflowName.equalsIgnoreCase(P8Constants.PROPERTY_BR_APPLICATION_WORKFLOW_NAME))
			{

				loStrBufferQueueFilter.append(P8Constants.PROPERTY_PE_TASK_TYPE);
				loStrBufferQueueFilter.append(" = '");
				loStrBufferQueueFilter.append(P8Constants.PROPERTY_PE_TASK_TYPE_BR_APPLICATION);
				loStrBufferQueueFilter.append("'");
				loStrBufferQueueFilter.append("  AND ");
				String lsApplicationID = (String) aoHmReqdWorkflowProperties.get(P8Constants.PROPERTY_PE_APPLICTION_ID);
				loStrBufferQueueFilter.append(P8Constants.PROPERTY_PE_APPLICTION_ID);
				loStrBufferQueueFilter.append(" = '");
				loStrBufferQueueFilter.append(lsApplicationID.trim());
				loStrBufferQueueFilter.append("'");
				loStrBufferQueueFilter.append(" )");
			}
			else if (asWorkflowName.equalsIgnoreCase(P8Constants.PROPERTY_SERVICE_CAPACITY_WORKFLOW_NAME))
			{

				loStrBufferQueueFilter.append(P8Constants.PROPERTY_PE_TASK_TYPE);
				loStrBufferQueueFilter.append(" = '");
				loStrBufferQueueFilter.append(P8Constants.PROPERTY_PE_TASK_TYPE_SERVICE_APPLICATION);
				loStrBufferQueueFilter.append("'");
				loStrBufferQueueFilter.append("  AND ");

				String lsSectionID = (String) aoHmReqdWorkflowProperties.get(P8Constants.PROPERTY_PE_SECTION_ID);

				loStrBufferQueueFilter.append(P8Constants.PROPERTY_PE_SECTION_ID);
				loStrBufferQueueFilter.append(" = '");
				loStrBufferQueueFilter.append(lsSectionID.trim());
				loStrBufferQueueFilter.append("'");
				loStrBufferQueueFilter.append(" )");
			}
			else
			{
				// all other workflows except business and service applications
				// do nothing. return wbNumber as blank
				return lsWbNumber;
			}
			lsQueueFilter = loStrBufferQueueFilter.toString();
			LOG_OBJECT.Debug("Final Query String::" + lsQueueFilter);

			// fetch the queue object
			loVWQueue = aoVWSession.getQueue(P8Constants.HSS_QUEUE_NAME);
			if (loVWQueue == null)
			{
				throwApplicationException("Error While Fetching Queue::" + P8Constants.HSS_QUEUE_NAME,
						aoHmReqdWorkflowProperties);
			}

			int liFlag = loVWQueue.QUERY_READ_LOCKED;// read even the locked
														// items
			int liType = VWFetchType.FETCH_TYPE_QUEUE_ELEMENT;
			LOG_OBJECT.Debug("Creating query with queue filter::" + lsQueueFilter);
			// create query
			LOG_OBJECT.Debug("Executing Query::");
			loVWQueueQuery = loVWQueue.createQuery(null, null, null, liFlag, lsQueueFilter, null, liType);

			if (loVWQueueQuery.hasNext())
			{
				if (loVWQueueQuery.fetchCount() > 1)
				{
					LOG_OBJECT.Debug("Multiple workitems found :: loVWQueueQuery.fetchCount()>1"
							+ loVWQueueQuery.fetchCount());
				}
				else
				{
					VWQueueElement loVWQueueElement = (VWQueueElement) loVWQueueQuery.next();
					loStepElement = loVWQueueElement.fetchStepElement(false, true);
					if (loStepElement == null)
					{
						throwApplicationException("Error While Fetching StepElement from QueueElement",
								aoHmReqdWorkflowProperties);
					}
					lsWbNumber = (String) loStepElement.getWorkflowNumber();
				}
			}
			else
			{
				lsWbNumber = "";
			}

		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(aoHmReqdWorkflowProperties);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.isTaskExist()::", aoAppex);
			throw aoAppex;
		}
		catch (VWException aoVWEx)
		{
			ApplicationException loAppex = new ApplicationException("Exception in P8ProcessOperations.isTaskExist()",
					aoVWEx);
			loAppex.setContextData(aoHmReqdWorkflowProperties);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.isTaskExist()::", loAppex);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in P8ProcessOperations.isTaskExist()", aoEx);
			loAppex.setContextData(aoHmReqdWorkflowProperties);
			LOG_OBJECT.Error("Exception in P8ProcessOperations.isTaskExist()::", loAppex);
			throw loAppex;
		}
		LOG_OBJECT.Debug("Exited P8ProcessOperations.isTaskExist() with Returned ::" + lsWbNumber);

		return lsWbNumber;
	}
	/**
	 * This methos is used to update default assignee role
	 * @param aoVWSession
	 * @param asWobNo
	 * @param aoHmReqdWorkflowProperties
	 * @param asSessionUserName
	 * @return true
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Boolean updateDefaultAssigneeRole(VWSession aoVWSession, String asWobNo, HashMap aoHmReqdWorkflowProperties,
			String asSessionUserName) throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put(HHSR5Constants.REQWORKFLOW_PROP, aoHmReqdWorkflowProperties);
		LOG_OBJECT.Debug("Entered P8ProcessOperations.updateDefaultAssigneeRole() with parameters::"
				+ loHmReqExceProp.toString());
		VWQueue loVWQueue = null;
		VWQueueQuery loVWQueueQuery = null;
		VWStepElement loStepElement = null;
		String lsWobNo = null;
		String lsQueueFilter = null;
		try
		{
			VWSession loVWSession = aoVWSession;
			lsQueueFilter = P8Constants.PROPERTY_PE_WOBNUMBER + "='" + asWobNo + "'";
			loVWQueue = aoVWSession.getQueue(P8Constants.HSS_QUEUE_NAME);

			int liFlag = loVWQueue.QUERY_READ_LOCKED;
			int liType = VWFetchType.FETCH_TYPE_QUEUE_ELEMENT;
			loVWQueueQuery = loVWQueue.createQuery(null, null, null, liFlag, lsQueueFilter, null, liType);
			VWQueueElement loVWQueueElement = (VWQueueElement) loVWQueueQuery.next(); // Query
			loStepElement = loVWQueueElement.fetchStepElement(true, true);

			if (!aoHmReqdWorkflowProperties.containsKey(P8Constants.PROPERTY_PE_ASSIGNED_TO))
			{
				aoHmReqdWorkflowProperties.put(P8Constants.PROPERTY_PE_ASSIGNED_TO, P8Constants.PE_TASK_UNASSIGNED);
				aoHmReqdWorkflowProperties
						.put(P8Constants.PROPERTY_PE_ASSIGNED_TO_NAME, P8Constants.PE_TASK_UNASSIGNED);
			}
			// set the workflow properties
			loStepElement = peOperationHelper.setWorkFlowProperties(loStepElement, aoHmReqdWorkflowProperties);
			// dispatch the work item
			loStepElement.doDispatch();
			lsWobNo = loStepElement.getWorkflowNumber();
			LOG_OBJECT.Debug("Exited P8ProcessOperations.updateDefaultAssigneeRole() . Returned lsWobNo ::" + asWobNo);

		}
		catch (Exception aoAppex)
		{
			LOG_OBJECT.Error("Exception in P8ProcessOperations.updateDefaultAssigneeRole()::", aoAppex);
		}
		return true;
	}

	/**
	 * This method is added in Release 6. It fetches the Work flow ID with
	 * respective to returnedPaymentId and terminated the work flow.
	 * 
	 * @param aoUserSession - Filenet Session Object
	 * @param asReturnedPaymentId - String ReturnedPaymentId
	 * @return loStatus - Boolean status
	 * @throws ApplicationException
	 */
	public Boolean terminateReturnedPaymentWF(P8UserSession aoUserSession, String asReturnedPaymentId,
			String asReturnedPaymentStatus) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered P8ProcessService.terminateReturnedPaymentWF() with ReturnedPaymentId::"
				+ asReturnedPaymentId);
		Boolean loStatus = HHSConstants.BOOLEAN_FALSE;
		if (HHSR5Constants.STATUS_ONE_EIGHT_SIX.equalsIgnoreCase(asReturnedPaymentStatus))
		{
			VWSession loVWSession = filenetConnection.getPESession(aoUserSession);
			try
			{
				VWQueue loVWQueue = null;
				VWQueueQuery loVWQueueQuery = null;
				VWStepElement loStepElement = null;
				String lsQueueFilter = null;
				lsQueueFilter = HHSConstants.RETURN_PE_PAYMENT_DETAIL_ID + HHSConstants.STRING_EQUAL
						+ asReturnedPaymentId + HHSConstants.STR;
				loVWQueue = loVWSession.getQueue(P8Constants.HSS_QUEUE_NAME);
				loVWQueueQuery = loVWQueue.createQuery(null, null, null, VWQueue.QUERY_READ_LOCKED, lsQueueFilter,
						null, VWFetchType.FETCH_TYPE_QUEUE_ELEMENT);
				VWQueueElement loVWQueueElement = (VWQueueElement) loVWQueueQuery.next();
				if (null != loVWQueueElement)
				{
					loStepElement = loVWQueueElement.fetchStepElement(true, true);
					VWWorkObject loReturnedWorkObject = loStepElement.fetchWorkObject(true, true);
					loReturnedWorkObject.doTerminate();
					loStatus = HHSConstants.BOOLEAN_TRUE;
					LOG_OBJECT.Debug("Exited P8ProcessService.terminateReturnedPaymentWF()");
				}
				else
				{
					setMoState("Error while P8ProcessService.terminateReturnedPaymentWF()");
					ApplicationException loAppex = new ApplicationException("Null Result from VWWork Queue");
					LOG_OBJECT.Error("Exception in P8ProcessService.terminateReturnedPaymentWF()::", loAppex);
					throw loAppex;
				}
			}
			catch (Exception aoAppex)
			{
				setMoState("Error while P8ProcessService.terminateReturnedPaymentWF()");
				ApplicationException loAppex = new ApplicationException(aoAppex.getMessage(), aoAppex);
				LOG_OBJECT.Error("Exception in P8ProcessService.terminateReturnedPaymentWF()::", loAppex);
				throw loAppex;
			}
		}
		return loStatus;
	}

	// Release 7 Start
	/**
	 * The method is added in Release 7. The method will dispatch automatically
	 * the modification work flows to next level.
	 * @param aoUserSession
	 * @param aoTaskDetailBean
	 * @return loStatus
	 * @throws ApplicationException
	 */
	public Boolean dispatchWF(P8UserSession aoUserSession, TaskDetailsBean aoTaskDetailBean)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered P8ProcessOperation.dispatchWF()");
		Boolean loStatus = HHSConstants.BOOLEAN_FALSE;
		VWSession loVWSession = filenetConnection.getPESession(aoUserSession);
		VWQueue loVWQueue = null;
		VWQueueQuery loVWQueueQuery = null;
		VWStepElement loStepElement = null;
		String lsQueueFilter = null;
		try
		{
			lsQueueFilter = HHSConstants.F_WOB_NUM;
			StringBuilder lsQueueFilterBuffer = new StringBuilder(lsQueueFilter);
			lsQueueFilterBuffer.append(HHSR5Constants.EQUAL_QUOTES);
			lsQueueFilterBuffer.append(aoTaskDetailBean.getWorkFlowId());
			lsQueueFilterBuffer.append(HHSConstants.STR);
			lsQueueFilter = lsQueueFilterBuffer.toString();
			loVWQueue = loVWSession.getQueue(P8Constants.HSS_QUEUE_NAME);
			loVWQueueQuery = loVWQueue.createQuery(null, null, null, VWQueue.QUERY_READ_LOCKED, lsQueueFilter, null,
					VWFetchType.FETCH_TYPE_QUEUE_ELEMENT);
			VWQueueElement loVWQueueElement = (VWQueueElement) loVWQueueQuery.next();
			if (null != loVWQueueElement)
			{
				LOG_OBJECT.Info("Element present");
				loStepElement = loVWQueueElement.fetchStepElement(true, true);
				VWWorkObject loModifiedWorkObject = loStepElement.fetchWorkObject(true, true);
				loModifiedWorkObject.doDispatch();
				loStatus = HHSConstants.BOOLEAN_TRUE;
				LOG_OBJECT.Info("Current level:: " + aoTaskDetailBean.getLevel());
				LOG_OBJECT.Info("Exited P8ProcessOperation.dispatchWF()");
			}
			else
			{
				LOG_OBJECT.Info("No Review workflow found in VWWork Queue");
			}
		}
		catch (Exception aoAppex)
		{
			setMoState("Error while P8ProcessOperation.dispatchWF()");
			ApplicationException loAppex = new ApplicationException(aoAppex.getMessage(), aoAppex);
			LOG_OBJECT.Error("Exception in P8ProcessOperation.dispatchWF()::", loAppex);
			throw loAppex;
		}
		return loStatus;
	}
	
	 //[Start] R8.4.0 QC_9468 Delete Update Budget not working for multiple years
    // [Start] R7.4.0 QC9008 Add abilities to delete budget update task
	/**
     * The method is added in Release 7.4.0 . 
     * 
     * @param aoUserSession
     * @param aoTaskDetailBean
     * @return loDelResultWFList workflow number List
     * @throws ApplicationException
     */
	   public List<String> terminateContractBudgetUpdateWF(P8UserSession aoUserSession, String asContractId,
	            String asBudgetTypeId) throws ApplicationException
	  {
	        LOG_OBJECT.Debug("Entered P8ProcessService.terminateContractBudgetUpdateWF() with contract Id and BudgetId ::" + asContractId);
	        //Boolean loStatus = HHSConstants.BOOLEAN_FALSE;	      
	        List<String> loDelResultWFList = new ArrayList<String>();
	        //String  loDelResultWF = null;
	        VWSession loVWSession  = null;
	            try
	            {
	                VWQueue loVWQueue = null;
	                VWQueueQuery loVWQueueQuery = null;
	                VWStepElement loStepElement = null;
	                HashMap<String, String> loParamMap = new HashMap<String, String>();
	                
	                loParamMap.put(P8Constants.PROPERTY_PE_TASK_TYPE, HHSConstants.TASK_BUDGET_UPDATE);
	                loParamMap.put(P8Constants.PE_WORKFLOW_CONTRACT_ID, asContractId);
                    //loParamMap.put(P8Constants.PROPERTY_PE_ENTITY_ID, asBudgetId);
                    //loParamMap.put(P8Constants.PE_WORKFLOW_CONTRACT_ID, HHSConstants.BUDGET_UPDATE_ID);
	                
	                String lsQueueFilter = createQueueFilter(loParamMap);
	                
	                loVWSession = filenetConnection.getPESession(aoUserSession);

	                loVWQueue = loVWSession.getQueue(P8Constants.HSS_QUEUE_NAME);
	                loVWQueueQuery = loVWQueue.createQuery(null, null, null, VWQueue.QUERY_READ_LOCKED, lsQueueFilter,
	                        null, VWFetchType.FETCH_TYPE_QUEUE_ELEMENT);
	                
	                // the result of the Query is more then one record
                    if( !loVWQueueQuery.hasNext()   )  {
                         LOG_OBJECT.Info("[Deleting Contract Budget Update Task] task doesn't exist or miltiple tasks exists for "+ lsQueueFilter + "  at P8ProcessService.terminateReturnedPaymentWF() " );

                         return  null;
                    }
                	while (loVWQueueQuery.hasNext())
        			{
		                VWQueueElement loVWQueueElement = (VWQueueElement) loVWQueueQuery.next();
	
		                //loVWQueueElement.getWorkFlowNumber();
		                if (null != loVWQueueElement)
		                {
		                    loStepElement = loVWQueueElement.fetchStepElement(true, true);
		                    VWWorkObject loReturnedWorkObject = loStepElement.fetchWorkObject(true, true);		                  
		                    String loDelResultWF = loStepElement.getWorkflowNumber();
		                    loDelResultWFList.add(loDelResultWF);
	
	                        LOG_OBJECT.Debug("terminateContractBudgetUpdateWF()    " + loReturnedWorkObject.getAuthoredStepName() +",loDelResultWF Number:" +loDelResultWF);                       
		                    
	                        loReturnedWorkObject.doLock(true);
	                        loReturnedWorkObject.doTerminate();
	                        
		                    LOG_OBJECT.Debug("Exited P8ProcessService.terminateContractBudgetUpdateWF()");
		                }
		                else
		                {
		                    setMoState("Error while P8ProcessService.terminateReturnedPaymentWF()");
		                    ApplicationException loAppex = new ApplicationException("Null Result from VWWork Queue");		                 
		                    LOG_OBJECT.Error("[Deleting Contract Budget Update Task]   "+ lsQueueFilter + " at P8ProcessService.terminateReturnedPaymentWF() " , loAppex);
		                    throw loAppex;
		                }
        			}
	            }
	            catch (Exception aoAppex)
	            {
	                setMoState("Error while P8ProcessService.terminateReturnedPaymentWF()");
	                ApplicationException loAppex = new ApplicationException(aoAppex.getMessage(), aoAppex);
	                LOG_OBJECT.Error("Exception in P8ProcessService.terminateContractBudgetUpdateWF()::", loAppex);
	                throw loAppex;
	            }
	            finally{
	                if( loVWSession != null ){
	                    try {
                            loVWSession.logoff();
                        } catch (VWException e) {
                            setMoState("Error while logging off Filenet connection");
                            ApplicationException loAppex = new ApplicationException("Error while logging off Filenet connection");
                            LOG_OBJECT.Error("Error while logging off Filenet connection in P8ProcessService.terminateContractBudgetUpdateWF()::", loAppex);
                            throw loAppex;
                        }
	                }
	            }
	        return loDelResultWFList;
	    }
	    // [End] R7.4.0 QC9008 Add abilities to delete budget update task
	   //[End] R8.4.0 QC_9468 Delete Update Budget not working for multiple years
}
