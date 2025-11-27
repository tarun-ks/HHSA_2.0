package com.nyc.hhs.controllers.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import javax.security.auth.login.LoginException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import com.bea.p13n.security.Authentication;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.AccountsAllocationBean;
import com.nyc.hhs.model.BaseFilter;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.CommentsHistoryBean;
import com.nyc.hhs.model.Document;
import com.nyc.hhs.model.DocumentPropertiesBean;
import com.nyc.hhs.model.ExtendedDocument;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.MasterBean;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.taskhandlers.MainTaskHandler;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.XMLUtil;
import com.nyc.hhs.webservice.restful.NYCIDWebServices;

/**
 * This class has utility function for base controller
 * 
 */
public class BaseControllerUtil
{
	/**
	 * Constant for Logging
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(BaseControllerUtil.class);

	/**
	 * This method perform actions on database based on operation performed on
	 * Subgrid
	 * <ul>
	 * <li>If add operation perform on the grid insert row into database</li>
	 * <li>If delete operation perform on the grid delete row from database</li>
	 * <li>If edit operation perform on the grid update row into database</li>
	 * </ul>
	 * @param asOperation operation to Select
	 * @param asTransactionName Transaction name
	 * @param aoChannelObj Channel Object
	 * @param aoBeanObj Bean Object
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public static void executeStaticGridTransaction(String asOperation, String asTransactionName, Channel aoChannelObj,
			Object aoBeanObj) throws ApplicationException
	{
		try
		{
			if (asOperation != null && HHSConstants.OPERATION_ADD.equalsIgnoreCase(asOperation))
			{
				aoChannelObj.setData(HHSConstants.CB_GRID_BEAN_OBJ, aoBeanObj);
				HHSTransactionManager.executeTransaction(aoChannelObj, asTransactionName);
			}
			else if (asOperation != null && HHSConstants.OPERATION_DELETE.equalsIgnoreCase(asOperation))
			{
				aoChannelObj.setData(HHSConstants.CB_GRID_BEAN_OBJ, aoBeanObj);
				HHSTransactionManager.executeTransaction(aoChannelObj, asTransactionName);
			}
			else if (asOperation != null && HHSConstants.OPERATION_EDIT.equalsIgnoreCase(asOperation))
			{
				aoChannelObj.setData(HHSConstants.CB_GRID_BEAN_OBJ, aoBeanObj);
				HHSTransactionManager.executeTransaction(aoChannelObj, asTransactionName);
			}
		}
		// Catch (and throw as it is) ApplicationException thrown from
		// HHSTransactionManager.executeTransaction method
		catch (ApplicationException aoAppEx)
		{
			throw aoAppEx;
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error Occured While performing actions on "
					+ "database based on operation performed on Subgrid", aoEx);
			LOG_OBJECT.Error("Error Occured While performing actions on "
					+ "database based on operation performed on Subgrid", loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * This method print the error using print Writer object
	 * <ul>
	 * @param aoOut Printwriter Object
	 * @param asError Error
	 * @throws ApplicationException If an Application Exception occurs
	 *             </ul>
	 */
	public static void catchTaskError(PrintWriter aoOut, String asError) throws ApplicationException
	{
		try
		{
			if (aoOut != null)
			{
				aoOut.print(asError);
				aoOut.flush();
				aoOut.close();
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While setting Audit Data to Channel", aoEx);
			LOG_OBJECT.Error("Error Occured While setting Audit Data to Channel", loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * <ul>
	 * Below method will set the required parameter into the map
	 * </ul>
	 * @param aoParamMap HashMap<String, String>
	 */
	public static void setRequiredParam(HashMap<String, String> aoParamMap) throws ApplicationException
	{
		try
		{
			aoParamMap.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, ApplicationConstants.EMPTY_STRING);
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured while setting the required parameter into the map", aoEx);
			LOG_OBJECT.Error("Error Occured while setting the required parameter into the map", loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * Changed method - By: Siddharth Bhola Reason: Build: 2.6.0 Enhancement id:
	 * 5653 added application exception block
	 * 
	 * This method will return the error for the finish task Approve
	 * <ul>
	 * <li>execute fetchEpinList_db transaction</li>
	 * <li>Gets the respective Handler based on TaskType from 'taskhandlers'
	 * properties file</li>
	 * <li>Get Provider or Internal comment from request and set into
	 * TaskDetailsBean object</li>
	 * <li>Execute 'taskApprove' method in Handler get from above</li>
	 * </ul>
	 * 
	 * @param asTaskType Type of Task
	 * @param aoTaskDetailsBean task detail bean
	 * @param aoMethodName Method Name
	 * @return lsError
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public static String finishTaskApproveUtil(String asTaskType, TaskDetailsBean aoTaskDetailsBean, String aoMethodName)
			throws ApplicationException
	{
		Map loTaskParamMap = null;
		String lsError = HHSConstants.EMPTY_STRING;
		try
		{
			MainTaskHandler loMainTaskHandler = getTaskHandler(asTaskType);
			if (null == loMainTaskHandler)
			{
				// throwing ApplicationException
				throw new ApplicationException("Task Handler not found");
			}

			if (HHSConstants.FINISH_TASK_APPROVE.equals(aoMethodName))
			{
				aoTaskDetailsBean.setTaskStatus(ApplicationConstants.STATUS_APPROVED);
				loTaskParamMap = loMainTaskHandler.taskApprove(aoTaskDetailsBean);
			}
			else if (HHSConstants.FINISH_TASK_RETURN.equals(aoMethodName))
			{
				aoTaskDetailsBean.setTaskStatus(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS);
				loTaskParamMap = loMainTaskHandler.taskReturn(aoTaskDetailsBean);
			}

			String lsPageError = HHSConstants.EMPTY_STRING;
			if (null != loTaskParamMap && null != loTaskParamMap.get(HHSConstants.PAGE_ERROR))
			{
				lsPageError = (String) loTaskParamMap.get(HHSConstants.PAGE_ERROR);
			}
			if (!HHSConstants.EMPTY_STRING.equalsIgnoreCase(lsPageError))
			{
				lsError = HHSConstants.PAGE_ERROR + HHSConstants.COLON + lsPageError;
			}
			else
			{
				lsError = HHSConstants.TASK_ERROR + HHSConstants.COLON
						+ (String) loTaskParamMap.get(HHSConstants.TASK_ERROR);
			}
		}
		// build 2.6.0, added Application Exception block
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("ApplicationException Occurred in finishTaskApproveUtil method", aoAppEx);
			throw aoAppEx;
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error Occured in finishTaskApproveUtil method",
					aoEx);
			LOG_OBJECT.Error("Error Occured in finishTaskApproveUtil method", loAppEx);
			throw loAppEx;
		}
		return lsError;
	}

	/**
	 * This method decide which handler method will be called based on task Type
	 * <ul>
	 * <li>
	 * Gets the respective Handler from the property file and create an object
	 * of the handler</li>
	 * </ul>
	 * 
	 * @param asTaskType Type of Task
	 * @return loMainHandler Object of the handler class implement the method of
	 *         MainTaskHAndler
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings("rawtypes")
	private static MainTaskHandler getTaskHandler(String asTaskType) throws ApplicationException
	{
		MainTaskHandler loMainHandler = null;
		// block of code to be executed if asSubSectionName and asSectionName
		// both are not null
		try
		{
			String lsClass = PropertyLoader.getProperty(HHSConstants.PROPERTIES_TASKHANDLERS, asTaskType);
			Class loClass = Class.forName(lsClass);
			loMainHandler = (MainTaskHandler) loClass.newInstance();
		}
		// ApplicationException handled here
		catch (ApplicationException aoAe)
		{
			throw new ApplicationException(HHSConstants.BASE_ERROR_OCCURED_IN_GET_TASK_HANDLER_OF_BASE_CONTLOLLER, aoAe);
		}
		// ClassNotFoundException handled here
		catch (ClassNotFoundException aoCNFE)
		{
			throw new ApplicationException(HHSConstants.BASE_ERROR_OCCURED_IN_GET_TASK_HANDLER_OF_BASE_CONTLOLLER,
					aoCNFE);
		}
		// InstantiationException handled here
		catch (InstantiationException aoIE)
		{
			throw new ApplicationException(HHSConstants.BASE_ERROR_OCCURED_IN_GET_TASK_HANDLER_OF_BASE_CONTLOLLER, aoIE);
		}
		// IllegalAccessException handled here
		catch (IllegalAccessException aoIAE)
		{
			throw new ApplicationException(HHSConstants.BASE_ERROR_OCCURED_IN_GET_TASK_HANDLER_OF_BASE_CONTLOLLER,
					aoIAE);
		}
		return loMainHandler;
	}

	/**
	 * <ul>
	 * <li>Get provider or internal comment from request to save comments in
	 * Audit</li>
	 * <li>Execute 'reassignTask' transaction to perform reassign operation</li>
	 * </ul>
	 * This method is updated for R4
	 * @param aoHMReqdProp HashMap aoHMReqdProp
	 * @param aoTaskDetailsBean Task Detail Bean
	 * @param asUserId User Id
	 * @param aoChannel Channel Object
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings("rawtypes")
	public static void reAssignTaskUtil(HashMap aoHMReqdProp, TaskDetailsBean aoTaskDetailsBean, String asUserId,
			Channel aoChannel) throws ApplicationException
	{
		try
		{
			List<HhsAuditBean> loAuditList = new ArrayList<HhsAuditBean>();
			// Adding audit data in channel object
			loAuditList.add(HHSUtil.addAuditDataToChannel(P8Constants.EVENT_NAME_ASSIGN,
					P8Constants.EVENT_TYPE_WORKFLOW, HHSConstants.TASK_ASSIGNED_TO + HHSConstants.COLON
							+ HHSConstants.SPACE + aoHMReqdProp.get(HHSConstants.LS_REASSIGN_USER_NAME),
					aoTaskDetailsBean.getEntityType(), aoTaskDetailsBean.getEntityId(), asUserId,
					HHSConstants.AGENCY_AUDIT));

			if (!aoTaskDetailsBean.getIsTaskAssigned())
			{
				asUserId = aoTaskDetailsBean.getAssignedTo();
			}

			if (null != aoHMReqdProp.get(HHSConstants.LS_PUBLIC_COMMENT)
					&& !(HHSConstants.EMPTY_STRING.equalsIgnoreCase((String) aoHMReqdProp
							.get(HHSConstants.LS_PUBLIC_COMMENT))))
			{
				loAuditList.add(HHSUtil.addAuditDataToChannel(HHSConstants.AGENCY_COMMENTS_DATA,
						HHSConstants.AGENCY_COMMENTS_DATA, (String) aoHMReqdProp.get(HHSConstants.LS_PUBLIC_COMMENT),
						aoTaskDetailsBean.getEntityType(), aoTaskDetailsBean.getEntityId(), asUserId,
						HHSConstants.AGENCY_AUDIT));
			}
			if (null != aoHMReqdProp.get(HHSConstants.LS_INTERNAL_COMMENT)
					&& !(HHSConstants.EMPTY_STRING.equalsIgnoreCase((String) aoHMReqdProp
							.get(HHSConstants.LS_INTERNAL_COMMENT))))
			{
				loAuditList.add(HHSUtil.addAuditDataToChannel(HHSConstants.AUDIT_TASK_INTERNAL_COMMENTS,
						HHSConstants.AUDIT_TASK_INTERNAL_COMMENTS,
						(String) aoHMReqdProp.get(HHSConstants.LS_INTERNAL_COMMENT), aoTaskDetailsBean.getEntityType(),
						aoTaskDetailsBean.getEntityId(), asUserId, HHSConstants.AGENCY_AUDIT));
			}
			aoChannel.setData(HHSConstants.AO_TASK_DETAILS_BEAN, aoTaskDetailsBean);
			aoChannel.setData(HHSConstants.LO_AUDIT_LIST, loAuditList);
			HHSTransactionManager.executeTransaction(aoChannel, HHSConstants.REASSIGN_TASK);
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error Occured while getting provider or "
					+ "internal comment from request to save comments in Audit", aoEx);
			LOG_OBJECT.Error("Error:: BaseControllerUtil:" + "reAssignTaskUtil method - "
					+ "Error Occured while getting provider or "
					+ "internal comment from request to save comments in Audit", loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * This method fetches the Comments History from database
	 * 
	 * <ul>
	 * <li>Get Workflow Id from TaskDetailsBean object and set into Channel.</li>
	 * <li>Execute 'fetchAgencyTaskHistory' transcation to get history details</li>
	 * </ul>
	 * This method is updated for R4
	 * @param aoTaskDetailsBean TaskDetailsBean containing task Attributes
	 * @return loTaskDetailsBean TaskDetailsBean containing task Attributes
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public static TaskDetailsBean viewCommentsHistoryUtil(TaskDetailsBean aoTaskDetailsBean)
			throws ApplicationException
	{
		try
		{
			List<CommentsHistoryBean> loCommentsHistoryBeanList = null;
			HashMap loProps = new HashMap();
			Channel loChannel = new Channel();
			String lsEntityTypeProvider = null;
			loProps.put(HHSConstants.ENTITY_ID, aoTaskDetailsBean.getEntityId());
			loProps.put(HHSConstants.ENTITY_TYPE, aoTaskDetailsBean.getEntityType());
			loProps.put(HHSConstants.EVENT_NAME, HHSConstants.PROPERTY_TASK_CREATION_EVENT);

			if (aoTaskDetailsBean.getIsTaskScreen() && !aoTaskDetailsBean.getIsEntityTypeTabLevel())
			{
				lsEntityTypeProvider = HHSConstants.AGENCY_PROVIDER_ENTITY_TYPE_MAP.get(aoTaskDetailsBean
						.getEntityType());
				if (null != lsEntityTypeProvider)
				{
					loProps.put(HHSConstants.ENTITY_TYPE_PROVIDER, lsEntityTypeProvider);
				}
				loProps.put(HHSConstants.EVENT_NAME_COMMENT, HHSConstants.PROVIDER_COMMENTS_DATA);
				loChannel.setData(ApplicationConstants.REQUIRED_PROPS, loProps);
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_AGENCY_TASK_HISTORY);
			}
			// R4: Tab Level Comments
			else if (!aoTaskDetailsBean.getIsTaskScreen() && aoTaskDetailsBean.getIsEntityTypeTabLevel())
			{
				loProps.put(HHSConstants.ENTITY_TYPE_FOR_AGENCY, aoTaskDetailsBean.getEntityTypeForAgency());
				loProps.put(HHSConstants.EVENT_NAME_FOR_AGENCY, HHSConstants.AGENCY_COMMENTS_DATA);
				loProps.put(HHSConstants.EVENT_TYPE, aoTaskDetailsBean.getEventType());
				loChannel.setData(ApplicationConstants.REQUIRED_PROPS, loProps);
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_PROVIDER_TASK_HISTORY_TAB_LEVEL);
			}
			else if (aoTaskDetailsBean.getIsTaskScreen() && aoTaskDetailsBean.getIsEntityTypeTabLevel())
			{
				lsEntityTypeProvider = HHSConstants.AGENCY_PROVIDER_ENTITY_TYPE_MAP.get(aoTaskDetailsBean
						.getEntityType());
				if (null != lsEntityTypeProvider)
				{
					loProps.put(HHSConstants.ENTITY_TYPE_PROVIDER, lsEntityTypeProvider);
				}
				loProps.put(HHSConstants.EVENT_NAME_COMMENT, HHSConstants.PROVIDER_COMMENTS_DATA);
				loProps.put(HHSConstants.EVENT_TYPE, aoTaskDetailsBean.getEventType());
				loChannel.setData(ApplicationConstants.REQUIRED_PROPS, loProps);
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_AGENCY_TASK_HISTORY_TAB_LEVEL);
			}
			// R4: Tab Level Comments Ends
			else
			{
				loProps.put(HHSConstants.ENTITY_TYPE_FOR_AGENCY, aoTaskDetailsBean.getEntityTypeForAgency());
				loProps.put(HHSConstants.EVENT_NAME_FOR_AGENCY, HHSConstants.AGENCY_COMMENTS_DATA);
				//Changes made in R6 for getting task history
				if(HHSConstants.TASK_RETURN_PAYMENT_REVIEW.equalsIgnoreCase(aoTaskDetailsBean.getTaskName()))
					loProps.put(HHSConstants.EVENT_NAME_FOR_AGENCY, HHSConstants.AUDIT_TASK_INTERNAL_COMMENTS);
				//Changes made in R6 for getting task history
				loChannel.setData(ApplicationConstants.REQUIRED_PROPS, loProps);
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_PROVIDER_TASK_HISTORY);
			}
			loCommentsHistoryBeanList = (List<CommentsHistoryBean>) loChannel
					.getData(ApplicationConstants.TASK_HISTORY_LIST);
			aoTaskDetailsBean.setCommentsHistory(loCommentsHistoryBeanList);
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While fetching the Comments History from database", aoEx);
			LOG_OBJECT.Error("Error:: BaseControllerUtil:" + "viewCommentsHistoryUtil method - "
					+ "Error Occured While fetching the Comments History from database", loAppEx);
			throw loAppEx;
		}
		return aoTaskDetailsBean;
	}

	/**
	 * This method will get the Epin list from the cache when user types three
	 * characters in epin textbox
	 * <ul>
	 * <li>Based on the value of lsEpinCalling, get the epin list from cache</li>
	 * <li>if cache does not contain anything, call the service method to get
	 * the epin list from database.</li>
	 * </ul>
	 * @param aoEpinList Epin list
	 * @param asEpinCalling Epin Calling
	 * @param asQueryStringFromReq string to be searched
	 * @return aoEpinList
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public static List<String> getEpinListUtil(List<String> aoEpinList, String asEpinCalling,
			String asQueryStringFromReq) throws ApplicationException
	{
		try
		{
			Channel loChannel = new Channel();
			loChannel.setData(HHSConstants.TO_SEARCH, asQueryStringFromReq);
			if (null != asEpinCalling)
			{
				aoEpinList = executeEpinTransaction(asEpinCalling, loChannel);
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While getting the Epin list from the cache", aoEx);
			LOG_OBJECT.Error("Error Occured While getting the Epin list from the cache", loAppEx);
			throw loAppEx;
		}
		return aoEpinList;
	}

	/**
	 * This method will execute Epin transactions.
	 * <ul>
	 * <li>execute fetchEpinList_db transaction</li>
	 * </ul>
	 * @param asEpinCalling Epin Calling
	 * @param aoChannel Channel Object
	 * @return loEpinList Epin List
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings("unchecked")
	private static List<String> executeEpinTransaction(String asEpinCalling, Channel aoChannel)
			throws ApplicationException
	{
		List<String> loEpinList = null;
		try
		{
			aoChannel.setData(HHSConstants.QUERY_ID, asEpinCalling);
			HHSTransactionManager.executeTransaction(aoChannel, HHSConstants.FETCH_EPIN_LIST_DB);
			loEpinList = (List<String>) aoChannel.getData(HHSConstants.AO_EPIN_KEYS_LIST);
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While setting Audit Data to Channel", aoEx);
			LOG_OBJECT.Error("Error Occured While setting Audit Data to Channel", loAppEx);
			throw loAppEx;
		}
		return loEpinList;
	}

	/**
	 * This method returns the fiscal years based on contract start and end date
	 * <ul>
	 * <li>Get Fiscal Start Year</li>
	 * <li>Get Fiscal End Year</li>
	 * </ul>
	 * @param asContractStartDate Contract Start Date
	 * @param asContractEndDate Contract End Date
	 * @param aoContractMap Contract Map
	 * @throws ApplicationException Application Exception
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public static void getContractFiscalYearsUtil(String asContractStartDate, String asContractEndDate,
			Map aoContractMap) throws ApplicationException
	{
		try
		{
			String[] loStartDateArray = asContractStartDate.split(HHSConstants.FORWARD_SLASH);
			String[] loEndDateArray = asContractEndDate.split(HHSConstants.FORWARD_SLASH);
			int liStartMonth = Integer.parseInt(loStartDateArray[0]);
			int liStartYear = Integer.parseInt(loStartDateArray[2]);
			int liEndMonth = Integer.parseInt(loEndDateArray[0]);
			int liEndYear = Integer.parseInt(loEndDateArray[2]);
			int liYearCount = 0;
			if (liStartMonth > HHSConstants.INT_SIX)
			{
				liStartYear = liStartYear + 1;
			}
			if (liEndMonth > HHSConstants.INT_SIX)
			{
				liEndYear = liEndYear + 1;
			}
			liYearCount = (liEndYear - liStartYear) + 1;
			int liFYCounter = (Integer.parseInt(String.valueOf(liStartYear).substring(2)));
			aoContractMap.put(HHSConstants.LI_START_FY_COUNTER, liFYCounter);
			aoContractMap.put(HHSConstants.LI_FYCOUNT, liYearCount);
			aoContractMap.put(HHSConstants.LI_START_YEAR, liStartYear);
			aoContractMap.put(HHSConstants.LI_END_YEAR, liEndYear);
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While returning the fiscal years based on contract start and end date", aoEx);
			LOG_OBJECT.Error("Error:: BaseControllerUtil:" + "getContractFiscalYearsUtil method - "
					+ "Error Occured While returning the fiscal years based on contract start and end date", loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * <ul>
	 * <li>This method populates the Chart of Account Allocation parent grid
	 * data Here we perform the logical part.</li>
	 * </ul>
	 * @param asScreen Screen
	 * @param aoBuffer Buffer object
	 * @throws ApplicationException Application Exception
	 */
	public static void showAccountMainGridUtil(String asScreen, StringBuffer aoBuffer) throws ApplicationException
	{
		try
		{
			StringTokenizer lsToken = new StringTokenizer(HHSConstants.EMPTY_STRING);
			String lsRowId = lsToken.toString().substring(lsToken.toString().lastIndexOf(HHSConstants.DOT) + 1);
			if (lsRowId.indexOf('@') != -1)
			{
				lsRowId = lsRowId.replaceAll(HHSConstants.AT_THE_RATE, HHSConstants.EMPTY_STRING);
			}
			aoBuffer.append("{\"rows\":[{\"id\":\"").append(lsRowId).append("\",\"uobc\":\"");
			aoBuffer.append(HHSConstants.OVERALL);
			aoBuffer.append("\",\"subOC\":\"\",\"rc\":\"\",");
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While populating Chart of Account Allocation parent grid", aoEx);
			LOG_OBJECT.Error("Error Occured While populating Chart of Account Allocation parent grid", loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * This method fetch the sub grid data from database populates the Chart of
	 * Account Allocation Subgrid
	 * <ul>
	 * <li>Fetch the Sub grid data from DB in the form of List</li>
	 * <li>populate the data in the json format.</li>
	 * </ul>
	 * @param aoBuffer Buffer
	 * @param asErrorMsg Error message
	 * @param asRowsPerPage Rows per page
	 * @param asPage page
	 * @param aoMethodName Method Name
	 * @param aiScreenRecordCount Screen record count
	 * @return StringBuffer aoBuffer
	 * @throws ApplicationException Application Exception
	 */
	public static StringBuffer showAccountSubGridUtil(StringBuffer aoBuffer, String asErrorMsg, String asRowsPerPage,
			String asPage, int aiScreenRecordCount, String aoMethodName) throws ApplicationException
	{
		StringBuffer loBufferTotal;
		try
		{
			aoBuffer.append("]}");
			if (aoMethodName.equals(HHSConstants.SHOW_ACCOUNT_SUB_GRID) && aiScreenRecordCount != 0)
			{
				aoBuffer.deleteCharAt(aoBuffer.lastIndexOf(HHSConstants.COMMA));
			}
			else if (aoMethodName.equals(HHSConstants.SHOW_FUNDING_SUB_GRID))
			{
				aoBuffer.deleteCharAt(aoBuffer.lastIndexOf(HHSConstants.COMMA));
			}

			if (aoBuffer.indexOf(":[]") != -1)
			{
				aoBuffer = new StringBuffer();
				if (aoMethodName.equals(HHSConstants.SHOW_ACCOUNT_SUB_GRID))
				{
					aoBuffer.append("{\"total\":\"\",\"page\":\"0\",\"records\":\"\",\"error\":\"");
				}
				else if (aoMethodName.equals(HHSConstants.SHOW_FUNDING_SUB_GRID))
				{
					aoBuffer.append("{\"total\":\"\",\"page\":\"1\",\"records\":\"\",\"error\":\"");
				}

				aoBuffer.append(asErrorMsg);
				aoBuffer.append("\",\"rows\":\"\"}");
			}
			else
			{
				loBufferTotal = new StringBuffer();
				int liTotal = aiScreenRecordCount / Integer.parseInt(asRowsPerPage);
				if (aiScreenRecordCount == Integer.parseInt(asRowsPerPage))
				{
					liTotal = 0;
				}
				String lsTotalPage = String.valueOf(liTotal + 1);
				String lsScreenRecordCount = String.valueOf(aiScreenRecordCount);
				loBufferTotal.append("{\"total\":\"");
				loBufferTotal.append(lsTotalPage);
				loBufferTotal.append("\",\"page\":\"");
				loBufferTotal.append(asPage);
				loBufferTotal.append("\",\"records\":\"");
				loBufferTotal.append(lsScreenRecordCount);
				loBufferTotal.append("\",\"error\":\"");
				loBufferTotal.append(asErrorMsg);
				loBufferTotal.append("\",");
				aoBuffer = loBufferTotal.append(aoBuffer);
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While fetching the sub grid data from database", aoEx);
			LOG_OBJECT.Error("Error Occured While fetching the sub grid data from database", loAppEx);
			throw loAppEx;
		}
		return aoBuffer;
	}

	/**
	 * <ul>
	 * This method validate the chart Allocation FYI
	 * </ul>
	 * @param aoModifiedAllocationBean AccountsAllocationBean
	 * @param aoActualGridList Actual Grid List
	 * @param aoUpdateGridList Update Grid List
	 * @param aoFiscalYrMap Fiscal Year Map
	 * @return
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public static void validateChartAllocationFYIUtil(AccountsAllocationBean aoModifiedAllocationBean,
			List aoActualGridList, List aoUpdateGridList, Map<String, Object> aoFiscalYrMap)
			throws ApplicationException
	{
		try
		{
			AccountsAllocationBean loAccountsAllocationBean = null;
			int liFiscalStartYr = (Integer) aoFiscalYrMap.get(HHSConstants.LI_START_YEAR);
			int liFiscalEndYr = (Integer) aoFiscalYrMap.get(HHSConstants.LI_END_YEAR);
			if (aoActualGridList != null && aoUpdateGridList != null)
			{
				int liCounter = 1;
				int liBigDecimalInitializer = 0;
				BigDecimal loTotalActualAmt, loTotalUpdateAmt, loZeroDecimalValue = new BigDecimal(
						liBigDecimalInitializer);
				for (int liCount = liFiscalStartYr; liCount <= liFiscalEndYr; liCount++)
				{

					loTotalActualAmt = loZeroDecimalValue;
					loTotalUpdateAmt = loZeroDecimalValue;
					Iterator<AccountsAllocationBean> loIter = aoActualGridList.iterator();
					Iterator<AccountsAllocationBean> loUpdateiter = aoUpdateGridList.iterator();
					while (loIter.hasNext())
					{
						loAccountsAllocationBean = loIter.next();
						String lsBeanFY = HHSConstants.SMALL_FY + liCounter;
						String lsAmt = (String) BeanUtils.getProperty(loAccountsAllocationBean, lsBeanFY);
						if (lsAmt != null && !HHSConstants.EMPTY_STRING.equals(lsAmt))
						{
							loTotalActualAmt = loTotalActualAmt.add(new BigDecimal(lsAmt));
						}
					}

					while (loUpdateiter.hasNext())
					{
						String lsBeanFY = HHSConstants.SMALL_FY + liCounter;
						String lsAmt = null;
						loAccountsAllocationBean = loUpdateiter.next();
						if (aoModifiedAllocationBean.getId().equalsIgnoreCase(loAccountsAllocationBean.getId()))
						{
							lsAmt = (String) BeanUtils.getProperty(aoModifiedAllocationBean, lsBeanFY);
						}

						else
						{
							lsAmt = (String) BeanUtils.getProperty(loAccountsAllocationBean, lsBeanFY);
						}
						if (lsAmt != null && !HHSConstants.EMPTY_STRING.equals(lsAmt))
						{
							loTotalUpdateAmt = loTotalUpdateAmt.add(new BigDecimal(lsAmt));
						}
					}
					if (aoModifiedAllocationBean.getId().equals(HHSConstants.NEW_ROW_IDENTIFIER))
					{
						loTotalUpdateAmt = loTotalUpdateAmt.add(new BigDecimal((String) BeanUtils.getProperty(
								aoModifiedAllocationBean, (HHSConstants.SMALL_FY + liCounter))));
					}
					if (loTotalUpdateAmt.compareTo(loTotalActualAmt) < HHSConstants.INT_ZERO)
					{
						String lsMessage = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
								HHSConstants.NEGATIVE_AMENDMENT_VALIDATION_MESSAGE);

						ApplicationException loAppEx = new ApplicationException(HHSConstants.EMPTY_STRING);
						loAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, lsMessage);
						// throwing ApplicationException
						throw loAppEx;
					}
					liCounter++;
				}
			}
		}
		// ApplicationException are thrown while getting the property through
		// property loader and when validation failed
		// Exception are thrown while getting the property through property
		// loader and parsing the string to double
		catch (ApplicationException loEx)
		{
			LOG_OBJECT.Error("Application Exception occured in BaseController: validateChartAllocationFYI method.",
					loEx);
			throw loEx;
		}
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "BaseControllerUtil: validateChartAllocationFYIUtil:: ", loEx);
			loAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, "validation failed");
			LOG_OBJECT.Error("Exception occured in BaseController: validateChartAllocationFYI method:: ", loAppEx);

			throw loAppEx;
		}
	}

	/**
	 * <ul>
	 * <li>It will execute two different transactions depending upon the type of
	 * the organization of the user</li>
	 * <li>If the organization type is Provider the execute the transaction
	 * <b>insertRfpDocumentDetails_db</b></li>
	 * <li>If the organization type is City/Agency then execute the transaction
	 * <b>insertProposalDocumentDetails_db</b></li>
	 * </ul>
	 * 
	 * @param asUserOrgType User organization type
	 * @param aoChannel Channel Object
	 * @param aoCBGridBean Grid Bean
	 * @throws ApplicationException If an Application Exception occurs
	 */
	private static void insertDocumentDetailsInDBOnUploadUtil(String asUserOrgType, Channel aoChannel,
			CBGridBean aoCBGridBean) throws ApplicationException
	{
		try
		{
			if (asUserOrgType.equalsIgnoreCase(ApplicationConstants.AGENCY_ORG))
			{ // Added for R6- insertion of documents for returned payment
				if (aoChannel.getData().containsKey(HHSConstants.RETURN_PAYMENT_DETAIL_ID)
						&& null != aoChannel.getData(HHSConstants.RETURN_PAYMENT_DETAIL_ID)
						&& !aoChannel.getData(HHSConstants.RETURN_PAYMENT_DETAIL_ID).toString().isEmpty())
				{
					HHSTransactionManager.executeTransaction(aoChannel,
							HHSConstants.BASE_INSERT_RETURNED_PAYMENT_DOCUMENT_DETAILS_DB);
				}
				// Added for R6- insertion of documents for returned payment end
				else
				{
					HHSTransactionManager.executeTransaction(aoChannel,
							HHSConstants.BASE_INSERT_CONTRACT_DOCUMENT_DETAILS_DB);
				}
			}
			// if the user organization type is provider
			else if (asUserOrgType.equalsIgnoreCase(ApplicationConstants.PROVIDER_ORG)
					&& null != aoCBGridBean.getInvoiceId()
					&& !HHSConstants.EMPTY_STRING.equalsIgnoreCase(aoCBGridBean.getInvoiceId()))
			{
				HHSTransactionManager.executeTransaction(aoChannel,
						HHSConstants.BASE_INSERT_INVOICE_DOCUMENT_DETAILS_DB);
			}
			else
			{
				HHSTransactionManager
						.executeTransaction(aoChannel, HHSConstants.BASE_INSERT_BUDGET_DOCUMENT_DETAILS_DB);
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While executing two different transactions"
							+ " depending upon the type of the organization", aoEx);
			LOG_OBJECT.Error("Error Occured While executing two different transactions"
					+ " depending upon the type of the organization", loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * <ul>
	 * <li>Execute <b>insertFinanceDocumentDetails_db</b> transaction of
	 * contract budget Mapper <b>RFPReleaseDocsUtil</b> class</li>
	 * <li>The transaction used :insertInvoiceDocumentDetails_db</li>
	 * <li>The transaction used :insertBudgetDocumentDetails_db</li>
	 * <li>The transaction used :insertContractDocumentDetails_db</li>
	 * </ul>
	 * <br>
	 * This method is updated in R4.
	 * @param aoChannel channel Object
	 * @param aoCBGridBean Grid Bean
	 * @param String asOrgType
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public static void addDocumentFromVaultActionUtil(Channel aoChannel, CBGridBean aoCBGridBean, String asOrgType)
			throws ApplicationException
	{
		try
		{
			if (ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(asOrgType))
			{
				if (null != aoCBGridBean.getInvoiceId()
						&& !HHSConstants.EMPTY_STRING.equalsIgnoreCase(aoCBGridBean.getInvoiceId()))
				{
					HHSTransactionManager.executeTransaction(aoChannel,
							HHSConstants.BASE_INSERT_INVOICE_DOCUMENT_DETAILS_DB);
				}
				else
				{
					HHSTransactionManager.executeTransaction(aoChannel,
							HHSConstants.BASE_INSERT_BUDGET_DOCUMENT_DETAILS_DB);
				}
			}
			else
			{ // Added for R6- insertion of documents for returned payment
				if (aoChannel.getData().containsKey(HHSConstants.RETURN_PAYMENT_DETAIL_ID)
						&& null != aoChannel.getData(HHSConstants.RETURN_PAYMENT_DETAIL_ID)
						&& !aoChannel.getData(HHSConstants.RETURN_PAYMENT_DETAIL_ID).toString().isEmpty())
				{
					HHSTransactionManager.executeTransaction(aoChannel,
							HHSConstants.BASE_INSERT_RETURNED_PAYMENT_DOCUMENT_DETAILS_DB);
				}
				// Added for R6- insertion of documents for returned payment end
				else
				{
					HHSTransactionManager.executeTransaction(aoChannel,
							HHSConstants.BASE_INSERT_CONTRACT_DOCUMENT_DETAILS_DB);
				}

			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While executing insertFinanceDocumentDetails_db transaction", aoEx);
			LOG_OBJECT.Error("Error Occured While executing insertFinanceDocumentDetails_db transaction", loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * This method will set the values into the parameter map which will be
	 * later used to insert the details into the data base
	 * 
	 * @param aoParameterMap parameter map
	 * @param asUserId user id
	 * @param aoDefaultValue Default Value Map
	 * @param aoCBGridBean Grid Bean
	 * @param aoCreatedDate document created date
	 * @param aoDocModifiedDate document modified date
	 * @param asContractId ContractId
	 * @throws ApplicationException Application Exception
	 */
	public static void setParametersMapValue(Map<Object, Object> aoParameterMap, String asUserId,
			HashMap<String, String> aoDefaultValue, Date aoCreatedDate, Date aoDocModifiedDate,
			CBGridBean aoCBGridBean, String asContractId) throws ApplicationException
	{
		try
		{
			aoParameterMap.put(HHSConstants.PROCUREMENT_ID, aoDefaultValue.get(HHSConstants.PROCUREMENT_ID));
			aoParameterMap.put(HHSConstants.DOC_REF_NO, aoDefaultValue.get(HHSConstants.HIDDEN_DOC_REF_SEQ_NO));
			aoParameterMap.put(HHSConstants.PROPOSAL_ID, aoDefaultValue.get(HHSConstants.PROPOSAL_ID));
			aoParameterMap.put(HHSConstants.DOCUMENT_TITLE, aoDefaultValue.get(HHSConstants.DOC_TITLE));
			aoParameterMap.put(HHSConstants.DOC_TYPE, aoDefaultValue.get(HHSConstants.ADD_DOC_TYPE));
			aoParameterMap.put(HHSConstants.DOC_CATEGORY, aoDefaultValue.get(HHSConstants.DOC_CATEGORY_LOWERCASE));
			aoParameterMap.put(HHSConstants.DOC_ID, aoDefaultValue.get(HHSConstants.DOC_ID));
			aoParameterMap.put(HHSConstants.HHS_DOC_CREATED_BY_ID, aoDefaultValue.get(HHSConstants.SUBMISSION_BY));
			aoParameterMap.put(HHSConstants.DOC_CREATED_DATE, aoCreatedDate);
			aoParameterMap.put(HHSConstants.DOC_MODIFIED_DATE, aoDocModifiedDate);
			aoParameterMap.put(HHSConstants.DATE_LAST_MODIFIED, aoDocModifiedDate);
			aoParameterMap.put(HHSConstants.DOC_MODIFIED_BY, aoDefaultValue.get(HHSConstants.BASE_LAST_MODIFIED_BY));
			aoParameterMap.put(HHSConstants.USER_ID, asUserId);
			aoParameterMap.put(HHSConstants.MOD_BY_USER_ID, asUserId);
			aoParameterMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, asContractId);
			aoParameterMap.put(HHSConstants.BUDGET_ID_WORKFLOW, aoCBGridBean.getContractBudgetID());
			aoParameterMap.put(HHSConstants.INVOICE_ID, aoCBGridBean.getInvoiceId());
			// Added for R6- Adding Returned payment id in parameter map
			if (aoDefaultValue.containsKey(HHSConstants.RETURN_PAYMENT_DETAIL_ID))
			{
				aoParameterMap.put(HHSConstants.RETURN_PAYMENT_DETAIL_ID,
						aoDefaultValue.get(HHSConstants.RETURN_PAYMENT_DETAIL_ID));
			}
			// Added for R6- Adding Returned payment id in parameter map
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured while setting the values into the parameter map", aoEx);
			LOG_OBJECT.Error("Error Occured while setting the values into the parameter map", loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * This method will return a XML dom Element based on input parameters
	 * <ul>
	 * <li>Gets column name, user org, grid name and sort type as input
	 * parameters</li>
	 * <li>Reads SortConfoguration.xml from cache</li>
	 * <li>Gets column element object</li>
	 * <li>Populates SortOnColumnsBean bean object with sorting details</li>
	 * </ul>
	 * 
	 * @param asColumnName Column name
	 * @param asUserOrg User Organization
	 * @param asGridName Grid name
	 * @param aoBaseFilter Base Filter
	 * @param asSortType Sort Type
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public static void getSortDetailsFromXMLUtil(String asColumnName, String asUserOrg, String asGridName,
			BaseFilter aoBaseFilter, String asSortType) throws ApplicationException
	{
		try
		{
			org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
					HHSConstants.SORT_MASTER_DETAILS);
			if (null == loXMLDoc || loXMLDoc.toString().equals(HHSConstants.EMPTY_STRING))
			{
				throw new ApplicationException(
						"Sort Master Details XML hasnt been loaded into memory for transaction key: "
								+ HHSConstants.SORT_MASTER_DETAILS);
			}
			String lsXPath = "//sort-master//sort[(contains(@userType,\"" + asUserOrg + "\") and @gridName=\""
					+ asGridName + "\")]//column[(@first=\"" + asColumnName + "\")]";
			Element loEle = XMLUtil.getElement(lsXPath, loXMLDoc);
			String lsDefaultSortType = loEle.getAttributeValue(HHSConstants.DEFAULT);
			aoBaseFilter.setFirstSort(loEle.getAttributeValue(HHSConstants.FIRST_COLUMN_NAME));
			aoBaseFilter.setSortColumnName(asColumnName);
			if (loEle.getAttributeValue(HHSConstants.IS_FIRST_SORT_DATE) != null)
			{
				aoBaseFilter
						.setFirstSortDate(Boolean.valueOf(loEle.getAttributeValue(HHSConstants.IS_FIRST_SORT_DATE)));
			}
			else
			{
				aoBaseFilter.setFirstSortDate(false);
			}
			if (loEle.getAttributeValue(HHSConstants.SECOND_COLUMN_NAME) == null
					|| loEle.getAttributeValue(HHSConstants.SECOND_COLUMN_NAME).equals(HHSConstants.EMPTY_STRING))
			{
				aoBaseFilter.setSecondSort(null);
			}
			else
			{
				aoBaseFilter.setSecondSort(loEle.getAttributeValue(HHSConstants.SECOND_COLUMN_NAME));
				if (loEle.getAttributeValue(HHSConstants.IS_SECOND_SORT_DATE) != null)
				{
					aoBaseFilter.setSecondSortDate(Boolean.valueOf(loEle
							.getAttributeValue(HHSConstants.IS_SECOND_SORT_DATE)));
				}
				else
				{
					aoBaseFilter.setSecondSortDate(false);
				}
			}
			String lsSameSort = loEle.getAttributeValue(HHSConstants.SAME);
			if (asSortType == null || asSortType.isEmpty() || asSortType.equalsIgnoreCase(HHSConstants.DEFAULT))
			{
				asSortType = lsDefaultSortType;
			}
			aoBaseFilter.setFirstSortType(asSortType);

			if (lsSameSort.equalsIgnoreCase(HHSConstants.TRUE))
			{
				aoBaseFilter.setSecondSortType(lsDefaultSortType);
			}
			else
			{
				aoBaseFilter
						.setSecondSortType(lsDefaultSortType.equalsIgnoreCase(HHSConstants.ASCENDING) ? HHSConstants.DESCENDING
								: HHSConstants.ASCENDING);
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured while returning XML dom Element based on input parameters", aoEx);
			LOG_OBJECT.Error("Error Occured while returning XML dom Element based on input parameters", loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * This method save comments in comments table
	 * <ul>
	 * <li>Set All Audit bean property values .</li>
	 * <li>Call setHhsAudit Utility to save comments in Database.</li>
	 * </ul>
	 * This method is updated for R4
	 * @param aoTaskDetailsBean Task Detail Bean
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public static void saveCommentNonAuditUtil(TaskDetailsBean aoTaskDetailsBean) throws ApplicationException
	{
		try
		{
			String lsInternalComment = aoTaskDetailsBean.getInternalComment();
			String lsProviderComment = aoTaskDetailsBean.getProviderComment();
			boolean lbAuditFlag = false;
			HhsAuditBean loAudit = new HhsAuditBean();
			loAudit.setAuditTableIdentifier(HHSConstants.NON_AUDIT_COMMENTS);
			loAudit.setTaskId(aoTaskDetailsBean.getTaskId());
			loAudit.setWorkflowId(aoTaskDetailsBean.getWorkFlowId());
			loAudit.setEntityType(aoTaskDetailsBean.getEntityType());
			loAudit.setEntityId(aoTaskDetailsBean.getEntityId());
			loAudit.setUserId(aoTaskDetailsBean.getUserId());
			loAudit.setIsTaskScreen(aoTaskDetailsBean.getIsTaskScreen());
			// R4: Tab Level Comments
			if (aoTaskDetailsBean.getIsEntityTypeTabLevel()
					&& (null != aoTaskDetailsBean.getIsEntityTypeTabLevel() && aoTaskDetailsBean
							.getIsEntityTypeTabLevel()))
			{
				loAudit.setEntityType(aoTaskDetailsBean.getEntityTypeTabLevel());
			}
			// R4: Tab Level Comments Ends
			if (null != lsInternalComment && !lsInternalComment.isEmpty())
			{
				loAudit.setInternalComments(lsInternalComment);
				lbAuditFlag = true;
			}
			if (null != lsProviderComment && !lsProviderComment.isEmpty())
			{
				loAudit.setProviderComments(lsProviderComment);
				lbAuditFlag = true;
			}
			if (lbAuditFlag && !aoTaskDetailsBean.getIsEntityTypeTabLevel())
			{
				HHSUtil.setHhsAudit(loAudit);
			}
			else if (!lbAuditFlag && !aoTaskDetailsBean.getIsEntityTypeTabLevel())
			{
				HHSUtil.deleteUserCommentsIfEmptyCommentsSaved(loAudit);
			}
			else if (aoTaskDetailsBean.getIsEntityTypeTabLevel())
			{
				HHSUtil.setHhsAuditForTabLevel(loAudit, aoTaskDetailsBean);
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured while saving comments in comments table", aoEx);
			LOG_OBJECT.Error("Error Occured while saving comments in comments table", loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * <ul>
	 * <li>This is public method used to copy all values from CBGridBean to
	 * AccountAllocationBean</li>
	 * </ul>
	 * @param aoAccountsAllocationBean Account Allocation bean
	 * @param asId ID
	 * @param asUobc Uobc String
	 * @param asSubOC SubOc String
	 * @param asRc RC string
	 * @param asTotal Total
	 * @param aoCBGridBean Grid Bean
	 * @param aoFiscalYrMap Fiscal Year Map
	 * @throws ApplicationException ApplicationException object
	 */
	public static void copyCBGridBeanToAllocBeanUtil(AccountsAllocationBean aoAccountsAllocationBean, String asId,
			String asUobc, String asSubOC, String asRc, String asTotal, CBGridBean aoCBGridBean,
			Map<String, Object> aoFiscalYrMap) throws ApplicationException
	{
		try
		{
			aoAccountsAllocationBean.setProcurementID(aoCBGridBean.getProcurementID());
			aoAccountsAllocationBean.setContractID(aoCBGridBean.getContractID());
			aoAccountsAllocationBean.setAmendmentContractID(aoCBGridBean.getAmendmentContractID());
			aoAccountsAllocationBean.setFiscalYearID(aoCBGridBean.getFiscalYearID());
			aoAccountsAllocationBean.setContractBudgetID(aoCBGridBean.getContractBudgetID());
			aoAccountsAllocationBean.setSubBudgetID(aoCBGridBean.getSubBudgetID());
			aoAccountsAllocationBean.setCreatedByUserId(aoCBGridBean.getCreatedByUserId());
			aoAccountsAllocationBean.setModifiedByUserId(aoCBGridBean.getModifiedByUserId());
			aoAccountsAllocationBean.setModifyByProvider(aoCBGridBean.getModifyByProvider());
			aoAccountsAllocationBean.setModifyByAgency(aoCBGridBean.getModifyByAgency());
			aoAccountsAllocationBean.setContractTypeId(aoCBGridBean.getContractTypeId());
			aoAccountsAllocationBean.setAmendmentType(aoCBGridBean.getAmendmentType());
			// set start and end fiscal year of contract

			Integer loContractStartFY = (Integer) aoFiscalYrMap.get(HHSConstants.LI_START_YEAR);
			Integer loContractEndFY = (Integer) aoFiscalYrMap.get(HHSConstants.LI_END_YEAR);

			if (null != loContractStartFY && null != loContractEndFY)
			{
				aoAccountsAllocationBean.setContractStartFY((String.valueOf(loContractStartFY)));
				aoAccountsAllocationBean.setContractEndFY((String.valueOf(loContractEndFY)));
			}

			aoAccountsAllocationBean.setId(asId);
			if (null != asUobc)
			{
				aoAccountsAllocationBean.setChartOfAccount(asUobc);
				String[] loChartOfAccnt = asUobc.split(HHSConstants.HYPHEN);
				aoAccountsAllocationBean.setUnitOfAppropriation(loChartOfAccnt[0]);
				aoAccountsAllocationBean.setBudgetCode(loChartOfAccnt[1]);
				aoAccountsAllocationBean.setObjectCode(loChartOfAccnt[2]);
			}
			aoAccountsAllocationBean.setSubOc(asSubOC);
			aoAccountsAllocationBean.setRc(asRc);
			aoAccountsAllocationBean.setTotal(asTotal);
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured while copying all values from CBGridBean to AccountAllocationBean", aoEx);
			LOG_OBJECT.Error("Error:: BaseControllerUtil:" + "copyCBGridBeanToAllocBeanUtil method - "
					+ "Error Occured while copying all values from CBGridBean to AccountAllocationBean", loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * <ul>
	 * <li>This is for generic code for Fiscal Year Methods</li>
	 * </ul>
	 * @param aoContractMap Contract Map
	 * @param aiMethodName Method Name
	 * @return StringBuffer loStringBuffer
	 * @throws ApplicationException Application Exception
	 */
	@SuppressWarnings("rawtypes")
	public static StringBuffer getFiscalYearUtil(Map aoContractMap, int aiMethodName) throws ApplicationException
	{
		StringBuffer loStringBuffer = null;
		try
		{
			loStringBuffer = new StringBuffer();
			int liYearCount = (Integer) aoContractMap.get(HHSConstants.LI_FYCOUNT);
			int liStartFYCounter = (Integer) aoContractMap.get(HHSConstants.LI_START_FY_COUNTER);
			switch (aiMethodName)
			{
				case HHSConstants.GET_FISCAL_YEAR_HEADER_PROP:
				{
					for (int liCounter = 1, liFYCounter = liStartFYCounter; liCounter <= liYearCount; liFYCounter++, liCounter++)
					{
						loStringBuffer.append(HHSConstants.NAME_FY).append(HHSUtil.getFiscalYearCounter(liFYCounter))
								.append(HHSConstants.TEMPLATE_CURRENCY_TEMPLATE);
					}
					break;
				}
				case HHSConstants.GET_FISCAL_YEAR_SUB_GRID_PROP:
				{
					for (int liCounter = 1; liCounter <= liYearCount; liCounter++)
					{
						loStringBuffer.append(HHSConstants.EDITABLE_TRUE_EDITRULES_REQUIRED_TRUE_NUMBER_TRUE);
					}
					break;
				}
				case HHSConstants.GET_FISCAL_YEAR_GRID:
				{
					for (int liCounter = 1, liFYCounter = liStartFYCounter; liCounter <= liYearCount; liFYCounter++, liCounter++)
					{
						loStringBuffer.append(HHSConstants.FY2).append(HHSUtil.getFiscalYearCounter(liFYCounter))
								.append(HHSConstants._0);
					}
					break;
				}
				case HHSConstants.GET_FISCAL_YEAR_HEADER:
				{
					for (int liCounter = 1, liFYCounter = liStartFYCounter; liCounter <= liYearCount; liFYCounter++, liCounter++)
					{
						loStringBuffer.append(HHSConstants.FY).append(HHSUtil.getFiscalYearCounter(liFYCounter))
								.append(HHSConstants.STR);
					}
					break;
				}
				case HHSConstants.COLUMNS_FOR_TOTAL_COUNT:
				{
					for (int liCounter = 1, liFYCounter = liStartFYCounter; liCounter <= liYearCount; liFYCounter++, liCounter++)
					{
						loStringBuffer.append(HHSConstants.SMALL_FY).append(HHSUtil.getFiscalYearCounter(liFYCounter))
								.append(HHSConstants.COMMA);
					}
					loStringBuffer.deleteCharAt(loStringBuffer.lastIndexOf(HHSConstants.COMMA));
					break;
				}
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured for generic code for Fiscal Year Methods", aoEx);
			LOG_OBJECT.Error("Error occured in getFiscalYearUtil in basecontrollerutil", loAppEx);
			throw loAppEx;
		}
		return loStringBuffer;
	}

	/**
	 * <ul>
	 * <li>This is for generic code for Funding Main Header</li>
	 * </ul>
	 * @param aoFiscalYearVar Fiscal Year
	 * @param aiMethodName method name
	 * @return StringBuffer loBuffer
	 * @throws ApplicationException Application Exception
	 */
	public static StringBuffer getFundingMainHeaderUtil(String aoFiscalYearVar, int aiMethodName)
			throws ApplicationException
	{
		StringBuffer loBuffer = null;
		try
		{
			switch (aiMethodName)
			{
				case HHSConstants.GET_FUNDING_MAIN_HEADER:
				{
					loBuffer = new StringBuffer(HHSConstants.FUNDING_SOURCES);
					loBuffer.append(aoFiscalYearVar);
					loBuffer.append(HHSConstants.TOTAL_STRING);
					break;
				}
				case HHSConstants.GET_FUNDINGS_MAIN_HEADER_PROP:
				{
					loBuffer = new StringBuffer(HHSConstants.NAME_FUNDING_TYPE);
					loBuffer.append(aoFiscalYearVar);
					loBuffer.append(HHSConstants.NAME_TOTAL_FUNDING_TEMPLATE_CURRENCY_TEMPLATE);
					break;
				}
				case HHSConstants.GET_FUNDING_SUB_GRID_PROP:
				{
					loBuffer = new StringBuffer(HHSConstants.EDITABLE_FALSE_EDITRULES_REQUIRED_TRUE);
					loBuffer.append(aoFiscalYearVar);
					loBuffer.append(HHSConstants.EDITABLE_FALSE);
					break;
				}
				case HHSConstants.GET_ACCOUNTS_MAIN_HEADER_PROP:
				{
					loBuffer = new StringBuffer(HHSConstants.NAME_UOBC_NAME_SUB_OC_NAME_RC);
					loBuffer.append(aoFiscalYearVar);
					loBuffer.append(HHSConstants.NAME_TOTAL_TEMPLATE_CURRENCY_TEMPLATE);
					break;
				}
				case HHSConstants.GET_ACCOUNTS_MAIN_HEADER:
				{
					loBuffer = new StringBuffer(HHSConstants.UO_A_BC_OC_SUB_OC_RC);
					loBuffer.append(aoFiscalYearVar);
					loBuffer.append(HHSConstants.TOTAL_STRING);
					break;
				}
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured for generic code for Funding Main Header", aoEx);
			LOG_OBJECT.Error("Error occured in getFundingMainHeaderUtil in basecontrollerutil", loAppEx);
			throw loAppEx;
		}
		return loBuffer;
	}

	/**
	 * <ul>
	 * <li>This is setting aoHmDocReqProps for saveDocumentPropertiesAction</li>
	 * </ul>
	 * @param aoHmDocReqProps Hm Doc property
	 * @param aoNewPropertiesList New property List
	 * @param aoDocProps Document Property
	 * @param aoPropertyId property Id
	 * @throws ApplicationException Application Exception
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public static void saveDocumentPropertiesActionUtil(HashMap aoHmDocReqProps,
			List<DocumentPropertiesBean> aoNewPropertiesList, DocumentPropertiesBean aoDocProps, String aoPropertyId)
			throws ApplicationException
	{
		try
		{
			if (ApplicationConstants.PROPERTY_TYPE_BOOLEAN.equalsIgnoreCase(aoDocProps.getPropertyType()))
			{
				if (HHSConstants.ON.equalsIgnoreCase(aoPropertyId)
						|| HHSConstants.YES_LOWERCASE.equalsIgnoreCase(aoPropertyId))
				{
					aoHmDocReqProps.put(aoDocProps.getPropSymbolicName(), true);
					aoDocProps.setPropValue(true);
				}
				else
				{
					aoHmDocReqProps.put(aoDocProps.getPropSymbolicName(), false);
					aoDocProps.setPropValue(false);
				}
			}
			else
			{
				aoHmDocReqProps.put(aoDocProps.getPropSymbolicName(), aoPropertyId);
				aoDocProps.setPropValue(aoPropertyId);
			}
			aoNewPropertiesList.add(aoDocProps);
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While setting aoHmDocReqProps for saveDocumentPropertiesAction", aoEx);
			LOG_OBJECT.Error("Error Occured While setting aoHmDocReqProps for saveDocumentPropertiesAction", loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * <ul>
	 * <li>This method consist conditional logic for getFYRowData</li>
	 * </ul>
	 * @param aoBeanObj Bean Object
	 * @param aoStringBuffer String Buffer
	 * @param aoContractMap Contract Map
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings("rawtypes")
	public static void fYRowDataUtil(Object aoBeanObj, StringBuffer aoStringBuffer, Map aoContractMap)
			throws ApplicationException
	{
		try
		{
			int liYearCount = (Integer) aoContractMap.get(HHSConstants.LI_FYCOUNT);
			int liStartFYCounter = (Integer) aoContractMap.get(HHSConstants.LI_START_FY_COUNTER);

			for (int liCounter = 1, liFYCounter = liStartFYCounter; liCounter <= liYearCount; liFYCounter++, liCounter++)
			{
				String lsMethodName = HHSConstants.SMALL_FY + liCounter;
				String lsFiscalAmount = HHSConstants.EMPTY_STRING;
				lsFiscalAmount = (String) BeanUtils.getProperty(aoBeanObj, lsMethodName);
				aoStringBuffer.append(HHSConstants.FY3).append(HHSUtil.getFiscalYearCounter(liFYCounter))
						.append(HHSConstants.UNDERSCORESCORE).append(lsFiscalAmount);
			}
		}
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "BaseControllerUtil: fYRowDataUtil:: ", loEx);
			loAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, " failed");
			LOG_OBJECT.Error("Exception occured in BaseControllerUtil: fYRowDataUtil method:: ", loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * <ul>
	 * <li>This method consist conditional logic for populateBeanFromRequest</li>
	 * </ul>
	 * @param aoBeanObj Bean Object
	 * @param asParamName Parameter Name
	 * @param asParamValue Parameter Value
	 * @throws ApplicationException Application Exception
	 */
	public static void populateBeanFromRequestUtil(Object aoBeanObj, String asParamName, String asParamValue)
			throws ApplicationException
	{
		try
		{
			if (asParamValue.contains(HHSConstants.NEW_RECORD))
			{
				asParamValue = asParamValue.substring(0, asParamValue.indexOf(HHSConstants.NEW_RECORD));
			}
			if (!HHSConstants.GRID_OPERATION.equalsIgnoreCase(asParamName)
					&& !HHSConstants.SCREEN_NAME.equalsIgnoreCase(asParamName)
					&& !HHSConstants.GRID_LABEL.equalsIgnoreCase(asParamName))
			{
				try
				{
					BeanUtils.setProperty(aoBeanObj, asParamName, StringEscapeUtils.unescapeHtml(asParamValue));
				}
				// Exception is handled here.
				catch (Exception aoExe)
				{
					// Set the error log if any exception occurs
					ApplicationException loAppEx = new ApplicationException(
							"Exception occured in populateBeanFromRequest while performing operation on grid  ", aoExe);
					LOG_OBJECT
							.Error("Exception occured in populateBeanFromRequest while performing operation on grid  "
									+ loAppEx);
					throw loAppEx;
				}
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured while consisting conditional logic for populateBeanFromRequest", aoEx);
			LOG_OBJECT.Error("Error Occured while consisting conditional logic for populateBeanFromRequest", loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * <ul>
	 * <li>This method is used to read the data from file net when terms and
	 * condition comes</li>
	 * </ul>
	 * 
	 * @param aoChannel channel object
	 * @return file net string
	 * @throws ApplicationException Application exception
	 */
	@SuppressWarnings("unchecked")
	public static String getTermsAndCondition(Channel aoChannel) throws ApplicationException
	{
		String lsSystemTermsAndCond = HHSConstants.EMPTY_STRING;
		try
		{
			HashMap<String, InputStream> loIoMap = (HashMap<String, InputStream>) aoChannel
					.getData(HHSConstants.CONTENT_BY_TYPE);
			int liCount = 0;
			for (Entry<String, InputStream> loEntry : loIoMap.entrySet())
			{
				liCount++;
				Writer loWriter = new StringWriter();
				char[] loBuffer = new char[1024];
				Reader loReader = null;
				InputStream loKey = loEntry.getValue();
				try
				{
					loReader = new BufferedReader(new InputStreamReader(loKey));
					int liTempVar;
					while ((liTempVar = loReader.read(loBuffer)) != -1)
					{
						loWriter.write(loBuffer, 0, liTempVar);
					}
					lsSystemTermsAndCond = loWriter.toString();
				}
				catch (Exception loExp)
				{
					throw new ApplicationException("Exception in BaseControllerUtil:getTermsAndCondition method", loExp);
				}
				finally
				{
					loKey.close();
					try
					{
						if (loReader != null)
						{
							loReader.close();
						}
						if (loWriter != null)
						{
							loWriter.close();
						}
					}
					// IOException handled here
					catch (IOException loIOExp)
					{
						LOG_OBJECT.Error("Error occured while getting terms and conditions", loIOExp);

						throw new ApplicationException(
								"Not able to close stream in BaseControllerUtil:getTermsAndCondition method", loIOExp);
					}
				}
				if (liCount > 0)
				{
					break;
				}
			}
		}
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "BaseControllerUtil: getTermsAndCondition:: ", loEx);
			loAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, " failed");
			LOG_OBJECT.Error("Exception occured in BaseControllerUtil: getTermsAndCondition method:: ", loAppEx);
			throw loAppEx;
		}
		return lsSystemTermsAndCond;
	}

	/**
	 * <ul>
	 * <li>This method consist conditional logic for
	 * setCOFAccountHeaderDataInSession</li>
	 * </ul>
	 * @param aoContractMap Contract Map
	 * @return List loFiscalYears
	 * @throws ApplicationException Application Exception
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public static List setCOFAccountHeaderDataInSessionUtil(Map aoContractMap) throws ApplicationException
	{
		List loFiscalYears = null;
		try
		{
			loFiscalYears = new ArrayList();
			int liYearCount = (Integer) aoContractMap.get(HHSConstants.LI_FYCOUNT);
			int liStartFYCounter = (Integer) aoContractMap.get(HHSConstants.LI_START_FY_COUNTER);

			for (int liCounter = 1, liFYCounter = liStartFYCounter; liCounter <= liYearCount; liFYCounter++, liCounter++)
			{
				loFiscalYears.add(HHSConstants.BASE_FY + HHSUtil.getFiscalYearCounter(liFYCounter));
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error Occured while consisting conditional "
					+ "logic for setCOFAccountHeaderDataInSession", aoEx);
			LOG_OBJECT.Error("Error Occured while consisting conditional "
					+ "logic for setCOFAccountHeaderDataInSession", loAppEx);
			throw loAppEx;
		}
		return loFiscalYears;
	}

	/**
	 * This method will insert the details of the uploaded document to the
	 * corresponding table
	 * <ul>
	 * <li>It will execute two different transactions depending upon the type of
	 * the organization of the user</li>
	 * <li>If the organization type is Provider the execute the transaction
	 * <b>insertRfpDocumentDetails_db</b></li>
	 * <li>If the organization type is City/Agency then execute the transaction
	 * <b>insertProposalDocumentDetails_db</b></li>
	 * </ul>
	 * 
	 * @param asDocumentId Document Id
	 * @param asProcurementId Procurement Id
	 * @param asUserOrgType User Organization type
	 * @param asUserName user name
	 * @param aoParameterMap Parameter Map
	 * @param aoChannel Channel Object
	 * @param aoCreatedDate Created Date
	 * @param aoCBGridBean Grid Bean
	 * @param asContractId ContractId
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public static void insertDocumentDetailsInDBOnUploadUtil(String asDocumentId, String asProcurementId,
			String asUserOrgType, String asUserName, Map<String, Object> aoParameterMap, Channel aoChannel,
			Date aoCreatedDate, CBGridBean aoCBGridBean, String asContractId) throws ApplicationException
	{
		try
		{
			aoParameterMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, asContractId);
			aoParameterMap.put(HHSConstants.BUDGET_ID_WORKFLOW, aoCBGridBean.getContractBudgetID());
			aoParameterMap.put(HHSConstants.INVOICE_ID, aoCBGridBean.getInvoiceId());
			aoParameterMap.put(HHSConstants.BASE_DOCUMENT_UPLOAD_TO, HHSConstants.CONTRACT);
			if (ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(asUserOrgType))
			{
				aoParameterMap.put(HHSConstants.BASE_DOCUMENT_UPLOAD_TO, HHSConstants.EMPTY_STRING);
			}
			aoParameterMap.put(HHSConstants.DOC_ID, asDocumentId);
			aoParameterMap.put(HHSConstants.DOC_CREATED_DATE, aoCreatedDate);
			aoParameterMap.put(HHSConstants.USER_ID, asUserName);
			aoParameterMap.put(HHSConstants.MOD_BY_USER_ID, asUserName);
			aoParameterMap.put(HHSConstants.ORGANIZATION_TYPE, asUserOrgType);
			aoChannel.setData(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
			aoChannel.setData(HHSConstants.AO_PARAMETER_MAP, aoParameterMap);
			HashMap<String, Object> loHmDocReqProps = new HashMap<String, Object>();
			loHmDocReqProps.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, true);
			loHmDocReqProps.put(HHSConstants.IS_DOCUMENT_RFP_AWARD_TYPE, true);
			loHmDocReqProps.put(P8Constants.PROPERTY_PE_IS_FINANCIAL_DOC, true);
			aoChannel.setData(HHSConstants.DOCUMENT_TYPE, HHSConstants.EMPTY_STRING);
			aoChannel.setData(HHSConstants.DOC_ID, asDocumentId);
			aoChannel.setData(HHSConstants.LO_HM_DOC_REQ_PROPS, loHmDocReqProps);
			aoChannel.setData(HHSConstants.LO_LAST_MOD_HASHMAP, aoParameterMap);
			aoChannel.setData(HHSConstants.LB_SUCCESS_STATUS, true);
			// Added for R6- Adding Returned payment id in parameter map
			if (aoChannel.getData().containsKey(HHSConstants.RETURN_PAYMENT_DETAIL_ID)
					&& null != aoChannel.getData(HHSConstants.RETURN_PAYMENT_DETAIL_ID)
					&& !aoChannel.getData(HHSConstants.RETURN_PAYMENT_DETAIL_ID).toString().isEmpty())
			{
				aoParameterMap.put(HHSConstants.RETURN_PAYMENT_DETAIL_ID,
						aoChannel.getData(HHSConstants.RETURN_PAYMENT_DETAIL_ID));
			}
			// Added for R6- Adding Returned payment id in parameter map
			aoParameterMap.put(HHSConstants.STATUS_ID, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.DOCUMENT_COMPLETED));
			BaseControllerUtil.insertDocumentDetailsInDBOnUploadUtil(asUserOrgType, aoChannel, aoCBGridBean);
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured while inserting the details of the uploaded document", aoEx);
			LOG_OBJECT.Error("Error Occured while inserting the details of the uploaded document", loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * <ul>
	 * <li>This method will be deleting temp file</li>
	 * @throws ApplicationException Application Exception
	 *             </ul>
	 * 
	 * @param aoFilePath File object
	 */
	public static void deleteTempFile(File aoFilePath) throws ApplicationException
	{
		try
		{
			if (null != aoFilePath)
			{
				aoFilePath.delete();
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error Occured While deleting temp file", aoEx);
			LOG_OBJECT.Error("Error Occured While deleting temp file", loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * <ul>
	 * <li>This method consist conditional logic for
	 * getFiscalYearCustomSubGridProp</li>
	 * </ul>
	 * @param aoContractMap Contract Map
	 * @param asConfigurableFiscalYear Configurable Fiscal Year
	 * @return loStringBuffer String Buffer Object
	 * @throws ApplicationException Application Exception
	 */
	@SuppressWarnings("rawtypes")
	public static StringBuffer getFiscalYearCustomSubGridPropUtil(Map aoContractMap, String asConfigurableFiscalYear)
			throws ApplicationException
	{
		StringBuffer loStringBuffer = null;
		try
		{
			loStringBuffer = new StringBuffer();
			int liYearCount = (Integer) aoContractMap.get(HHSConstants.LI_FYCOUNT);
			int liStartFYCounter = (Integer) aoContractMap.get(HHSConstants.LI_START_FY_COUNTER);
			int liCurrentFY = Integer.parseInt(asConfigurableFiscalYear);
			for (int liCounter = 1, liFYCounter = liStartFYCounter; liCounter <= liYearCount; liFYCounter++, liCounter++)
			{
				if (liCurrentFY <= liFYCounter)
				{ // Makes Current and further FY editable
					loStringBuffer.append(HHSConstants.EDITABLE_TRUE_EDITRULES_REQUIRED_TRUE_NUMBER_TRUE);
				}
				else
				{
					loStringBuffer.append(",{editable:false, editrules:{required:true,number:true}}");
				}
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While setting Audit Data to Channel", aoEx);
			LOG_OBJECT.Error("Error occured in getFiscalYearCustomSubGridPropUtil in basecontrollerutil", loAppEx);
			throw loAppEx;
		}
		return loStringBuffer;
	}

	/**
	 * This method performs the userName and password validation
	 * <ul>
	 * <li>Validate entered user is logged in user</li>
	 * <li>Validate username and password is correct</li>
	 * </ul>
	 * @param asUserId User id
	 * @param asPassword Password
	 * @param asLoginUserEmail Login User Email
	 * @return loHashMap Hash Map Object
	 * @throws ApplicationException Application Exception
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public static Map validateUserUtil(String asUserId, String asPassword, String asLoginUserEmail, String asUserOrgType)
			throws ApplicationException
	{
		boolean lbAuthenticate = false;
		String lsMsg = HHSConstants.EMPTY_STRING;
		// Start Added in R5
		String lsMsgCode = HHSConstants.EMPTY_STRING;
		// End Added in R5
		Map loHashMap = null;
		try
		{
			loHashMap = new HashMap();
          
			try
			{
				if (!asLoginUserEmail.equalsIgnoreCase(asUserId))
				{
					lsMsg = HHSConstants.INVALID_USER_MSG;
					// Start Added in R5
					lsMsgCode = HHSR5Constants.INVALID_USER_NAME;
					// End Added in R5
				}
				else
				{
					String lsLoginEnvironment = PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
							HHSConstants.BASENEW_PROPERTY_LOGIN_ENVIRONMENT);
					if (!ApplicationConstants.LOCAL_ENVIRONMENT.equalsIgnoreCase(lsLoginEnvironment))
					{
						//***Start SAML R 7.8.0 QC 9165: invoke Web Service to Authenticate User - Providers only 
						
						if(!ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(asUserOrgType))
						{	
							Authentication.authenticate(asUserId, asPassword);
							lbAuthenticate = true;
						} 
						else
						{	
							LOG_OBJECT.Debug("Call WebService to Authenticate ");
							NYCIDWebServices sws = new NYCIDWebServices();
							String jsonResponse = sws.authenticateUser(asUserId, asPassword); 
							LOG_OBJECT.Debug("jsonRespons:: "+jsonResponse);
							if(jsonResponse!=null && jsonResponse.contains(":"))
						    {
								String[] temp = jsonResponse.split(":", 2);
								LOG_OBJECT.Debug("\nloStrBuffer[0] "+temp[0]);
								LOG_OBJECT.Debug("loStrBuffer[1] "+temp[1]);
								if (temp[0].indexOf("authenticated") != -1 && temp[1].indexOf("true") != -1)
								{
									lbAuthenticate = true;
								}	
								else
								{
									lbAuthenticate = false;
									lsMsg = HHSConstants.INVALID_USER_MSG;
									lsMsgCode = HHSR5Constants.INVALID_USER_PASSWORD;
									throw new LoginException(HHSConstants.BASE_FAILEDLOGINEXCEPTION); 
								}
						    }
							
						}	
						//***End SAML R 7.8.0 QC 9165: invoke Web Service to Authenticate User
						
					}
					//lbAuthenticate = true; 
				}
			}
			// Login Exception handled here
			
			catch (LoginException aoLogExcep)
			{
				lbAuthenticate = false;
				LOG_OBJECT.Debug("==========LoginException :: "+aoLogExcep);
				// Error message is logged and set into lsMsg in case there is
				// any
				// Logging Exception
				if (!aoLogExcep.toString().toLowerCase().contains(HHSConstants.BASE_FAILEDLOGINEXCEPTION))
				{
					lsMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
				}
				else
				{
					lsMsg = HHSConstants.INVALID_USER_MSG;
					// Start Added in R5
					lsMsgCode = HHSR5Constants.INVALID_USER_PASSWORD;
					// End Added in R5
				}
				LOG_OBJECT.Error("LoginException occured while authentication", aoLogExcep);
			}
			
			
			// Exception handled here
			catch (Exception aoExcep)
			{
				ApplicationException loAppExp = new ApplicationException("LoginException occured while authenticating",
						aoExcep);
				LOG_OBJECT.Error("LoginException occured while authenticating", loAppExp);
				throw loAppExp;
			}
			
			
			loHashMap.put(HHSConstants.IS_VALID_USER, lbAuthenticate);
			loHashMap.put(HHSConstants.ERROR_MESSAGE, lsMsg);
						
			// Start Added in R5
			loHashMap.put(HHSR5Constants.ERROR_MESSAGE_CODE, lsMsgCode);
			// End Added in R5
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While  performing the userName and password validation", aoEx);
			LOG_OBJECT.Error("Error Occured While  performing the userName and password validation", loAppEx);
			throw loAppEx;
		}
				
		return loHashMap;
	}

	/**
	 * <ul>
	 * <li>Set the default channel value for addDocumentFromVaultAction</li>
	 * </ul>
	 * @param aoDefaultValue Default Value
	 * @param aoMethodName method name
	 * @return Channel loChannel
	 * @throws ApplicationException Application Exception
	 */

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public static Channel settingDefaultChannel(HashMap aoDefaultValue, String aoMethodName)
			throws ApplicationException
	{
		Channel loChannel = null;
		try
		{
			loChannel = new Channel();
			if (HHSConstants.ADD_DOCUMENT_FROM_VAULT_ACTION.endsWith(aoMethodName))
			{
				loChannel.setData(HHSConstants.PROCUREMENT_ID_KEY, aoDefaultValue.get(HHSConstants.PROCUREMENT_ID));
				//Added for R6- Adding Returned payment id in channel
				if (aoDefaultValue.containsKey(HHSConstants.RETURN_PAYMENT_DETAIL_ID))
				{
				loChannel.setData(HHSConstants.RETURN_PAYMENT_DETAIL_ID, aoDefaultValue.get(HHSConstants.RETURN_PAYMENT_DETAIL_ID));
				}
				//Added for R6- Adding Returned payment id in channel end
				loChannel.setData(HHSConstants.AO_PARAMETER_MAP, aoDefaultValue.get(HHSConstants.AO_PARAMETER_MAP));
				HashMap loHmDocReqProps = new HashMap();
				loHmDocReqProps.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, true);
				loHmDocReqProps.put(HHSConstants.IS_DOCUMENT_RFP_AWARD_TYPE, true);
				loChannel.setData(HHSConstants.DOCUMENT_TYPE, HHSConstants.EMPTY_STRING);
				loChannel.setData(HHSConstants.DOC_ID, aoDefaultValue.get(HHSConstants.DOC_ID));
				loChannel.setData(HHSConstants.LO_HM_DOC_REQ_PROPS, loHmDocReqProps);
				loChannel.setData(HHSConstants.LO_LAST_MOD_HASHMAP, aoDefaultValue.get(HHSConstants.AO_PARAMETER_MAP));
				loChannel.setData(HHSConstants.LB_SUCCESS_STATUS, true);
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured while setting default channel value for " + "addDocumentFromVaultAction", aoEx);
			LOG_OBJECT.Error("Error Occured while setting default channel value for " + "addDocumentFromVaultAction",
					loAppEx);
			throw loAppEx;
		}
		return loChannel;
	}

	/**
	 * <ul>
	 * <li>Set the default DocHashMap value for saveDocumentPropertiesAction</li>
	 * </ul>
	 * @param aoHmDocReqProps Document required property
	 * @param aoDocument Document
	 * @param asCurrentDate Current date
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public static void saveDocHashMapUtil(HashMap aoHmDocReqProps, Document aoDocument, String asCurrentDate)
			throws ApplicationException
	{
		try
		{
			aoHmDocReqProps.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, aoHmDocReqProps.get(HHSConstants.DOC_NAME));
			aoHmDocReqProps.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY,
					aoHmDocReqProps.get(ApplicationConstants.KEY_SESSION_USER_NAME));
			aoHmDocReqProps.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY_ID,
					aoHmDocReqProps.get(ApplicationConstants.KEY_SESSION_USER_ID));
			aoHmDocReqProps.put(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE, asCurrentDate);
			aoHmDocReqProps.put(HHSConstants.PROC_DOC_ID, aoHmDocReqProps.get(HHSConstants.PROCUREMENT_ID));
			aoHmDocReqProps.put(HHSConstants.IS_ADDENDUM, aoHmDocReqProps.get(HHSConstants.IS_ADD_TYPE));
			aoHmDocReqProps.put(HHSConstants.PROCUREMENT_ID, aoHmDocReqProps.get(HHSConstants.PROCUREMENT_ID));
			aoHmDocReqProps
					.put(HHSConstants.MODIFIED_BY, aoHmDocReqProps.get(ApplicationConstants.KEY_SESSION_USER_ID));
			aoHmDocReqProps.put(HHSConstants.DOC_ID, aoDocument.getDocumentId());
			aoHmDocReqProps.put(HHSConstants.MODIFIED_DATE, DateUtil.getSqlDate(DateUtil.getCurrentDate()));
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While setting the default DocHashMap value for " + "saveDocumentPropertiesAction",
					aoEx);
			LOG_OBJECT.Error("Error Occured While setting the default DocHashMap value for "
					+ "saveDocumentPropertiesAction", loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * <ul>
	 * <li>Conditional method for displaySuccess</li>
	 * <li>The transaction used :getFinancialDocuments_db</li>
	 * </ul>
	 * @param asUserOrgType User Organization Type
	 * @param aoCBGridBean Grid Bean
	 * @param aoRequiredParamMap Required Parameter Map
	 * @param aoChannelObj channel object
	 * @param asContractId
	 * @return loFinancialDocumentList Financial Document List
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public static List<ExtendedDocument> displaySuccessUtil(String asUserOrgType, CBGridBean aoCBGridBean,
			HashMap<String, String> aoRequiredParamMap, Channel aoChannelObj, String asContractId)
			throws ApplicationException
	{
		List<ExtendedDocument> loFinancialDocumentList = null;
		try
		{
			aoChannelObj.setData(HHSConstants.HM_REQIRED_PROPERTY_MAP, aoRequiredParamMap);
			Map loMap = new HashMap();
			loMap.put(HHSConstants.ORGANIZATION_TYPE, asUserOrgType);
			loMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, asContractId);
			loMap.put(HHSConstants.BUDGET_ID_WORKFLOW, aoCBGridBean.getContractBudgetID());
			loMap.put(HHSConstants.INVOICE_ID, aoCBGridBean.getInvoiceId());
			//Added for R6- Adding Returned payment id in map
			if (aoChannelObj.getData().containsKey(HHSConstants.RETURN_PAYMENT_DETAIL_ID))
			{
			loMap.put(HHSConstants.RETURN_PAYMENT_DETAIL_ID, aoChannelObj.getData(HHSConstants.RETURN_PAYMENT_DETAIL_ID));
			}
			//Added for R6- Adding Returned payment id in map end
			aoChannelObj.setData(HHSConstants.AO_PARAMETER_MAP, loMap);
			HHSTransactionManager.executeTransaction(aoChannelObj, HHSConstants.GET_FINANCIAL_DOCUMENTS_DB);
			loFinancialDocumentList = (List<ExtendedDocument>) aoChannelObj
					.getData(HHSConstants.AO_FINANCIAL_DOCUMENT_LIST);
			if (loFinancialDocumentList != null && !loFinancialDocumentList.isEmpty())
			{
				Collections.sort(loFinancialDocumentList, new Comparator<ExtendedDocument>()
				{
					@Override
					public int compare(ExtendedDocument c1, ExtendedDocument c2)
					{
						Date aoCreatedDate1 = HHSUtil.ConvertStringToDate(c1.getCreatedDate());
						Date aoCreatedDate2 = HHSUtil.ConvertStringToDate(c2.getCreatedDate());
						int liResult = aoCreatedDate1.compareTo(aoCreatedDate2);
						if (liResult != 0 && liResult > 0)
						{
							liResult = -1;
						}
						else if (liResult != 0 && liResult < 0)
						{
							liResult = 1;
						}
						if (liResult == 0)
						{
							liResult = c1.getDocumentTitle().toLowerCase()
									.compareTo(c2.getDocumentTitle().toLowerCase());
						}
						return liResult;
					}
				});
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While Conditional method for displaySuccess", aoEx);
			LOG_OBJECT.Error("Error Occured While Conditional method for displaySuccess", loAppEx);
			throw loAppEx;
		}
		return loFinancialDocumentList;
	}

	/**
	 * <ul>
	 * <li>Setting bean for method gridOperation</li>
	 * </ul>
	 * @param asTransactionName Transaction Name
	 * @param asSubBudgetId Sub Budget Id
	 * @param asParentSubBudgetId Parent Sub budget Id
	 * @param aoBeanObj Bean Object
	 * @param aoCBGridBean Grid Bean
	 * @throws ApplicationException If an Application Exception occurs
	 * Modified method signature to include 'budgetId' for Tab Browsing QC8691, R7.1
	 */
	public static void settingGridBeanObj(String asTransactionName, String asSubBudgetId, String budgetId, String asParentSubBudgetId,
			Object aoBeanObj, CBGridBean aoCBGridBean) throws ApplicationException
	{
		try
		{
			BeanUtils.setProperty(aoBeanObj, HHSConstants.PROCUREMENT_ID, aoCBGridBean.getProcurementID());
			BeanUtils.setProperty(aoBeanObj, HHSConstants.CONTRACT_ID, aoCBGridBean.getContractID());
			BeanUtils.setProperty(aoBeanObj, HHSConstants.FISCAL_YEAR_ID, aoCBGridBean.getFiscalYearID());
			//BEGIN Modified for Tab Browsing QC8691, R7.1
			if (budgetId != null)
				BeanUtils.setProperty(aoBeanObj, HHSConstants.CONTRACT_BUDGET_ID, budgetId);
			else 
				BeanUtils.setProperty(aoBeanObj, HHSConstants.CONTRACT_BUDGET_ID, aoCBGridBean.getContractBudgetID());
			//END Modified for Tab Browsing QC8691, R7.1 
			BeanUtils.setProperty(aoBeanObj, HHSConstants.SUB_BUDGET_ID, asSubBudgetId);

			BeanUtils.setProperty(aoBeanObj, HHSConstants.INVOICE_ID, aoCBGridBean.getInvoiceId());
			BeanUtils.setProperty(aoBeanObj, HHSConstants.BUDGET_TYPE_ID, aoCBGridBean.getBudgetTypeId());
			BeanUtils.setProperty(aoBeanObj, HHSConstants.PARENT_BUDGET_ID, aoCBGridBean.getParentBudgetId());
			BeanUtils.setProperty(aoBeanObj, HHSConstants.PARENT_SUBBUDGET_ID, asParentSubBudgetId);
			BeanUtils.setProperty(aoBeanObj, HHSConstants.MODIFIED_BY_AGENCY, aoCBGridBean.getModifyByAgency());
			BeanUtils.setProperty(aoBeanObj, HHSConstants.MODIFIED_BY_PROVIDER, aoCBGridBean.getModifyByProvider());
			BeanUtils.setProperty(aoBeanObj, HHSConstants.TRANSACTION_NAME, asTransactionName);
			BeanUtils.setProperty(aoBeanObj, HHSConstants.AMENDMENT_TYPE, aoCBGridBean.getAmendmentType());
			BeanUtils.setProperty(aoBeanObj, HHSConstants.BUDGET_ADVANCE_ID, aoCBGridBean.getBudgetAdvanceId());
			// Start: Added for Defect-8478
			BeanUtils.setProperty(aoBeanObj, HHSR5Constants.NEW_RECORD, aoCBGridBean.getNewRecord());
			// End: Added for Defect-8478
		}
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "BaseControllerUtil: settingGridBeanObj:: ", loEx);
			loAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, "validation failed");
			LOG_OBJECT.Error("Exception occured in BaseControllerUtil: settingGridBeanObj method:: ", loAppEx);

			throw loAppEx;
		}
	}

	/**
	 * <ul>
	 * <li>This method populates the Funding Source Allocation parent grid data</li>
	 * </ul>
	 * @param asScreen Screen for selection
	 * @param aoBuffer StringBuffer Object
	 * @throws ApplicationException Application Exception
	 */
	public static void showFundingMainGridUtil(String asScreen, StringBuffer aoBuffer) throws ApplicationException
	{
		try
		{
			StringTokenizer lsToken = new StringTokenizer(HHSConstants.EMPTY_STRING);
			String lsRowId = lsToken.toString().substring(lsToken.toString().lastIndexOf(HHSConstants.DOT) + 1);
			if (lsRowId.indexOf('@') != -1)
			{
				lsRowId = lsRowId.replaceAll(HHSConstants.AT_THE_RATE, HHSConstants.EMPTY_STRING);
			}
			aoBuffer.append("{\"rows\":[{\"id\":\"").append(lsRowId).append("\",\"fundingType\":\"");
			aoBuffer.append(HHSConstants.OVERALL);
			aoBuffer.append("\",");
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While populating Funding Source Allocation parent grid data", aoEx);
			LOG_OBJECT.Error("Error Occured While populating Funding Source Allocation parent grid data", loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * <ul>
	 * <li>Setting the lsOperationUpperCase property</li>
	 * </ul>
	 * @param asOperation operation name
	 * @return lsOperationUpperCase
	 * @throws ApplicationException Application Exception
	 */
	public static String lsOperationUpperCaseUtil(String asOperation) throws ApplicationException
	{
		String lsOperationUpperCase = null;
		try
		{
			if (!StringUtils.isEmpty(asOperation))
			{
				lsOperationUpperCase = asOperation.substring(0, 1).toUpperCase()
						+ asOperation.substring(1, asOperation.length());
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While setting lsOperationUpperCase property", aoEx);
			LOG_OBJECT.Error("Error Occured While setting lsOperationUpperCase property", loAppEx);
			throw loAppEx;
		}
		return lsOperationUpperCase;
	}

	/**
	 * <ul>
	 * <li>Setting the default channel for BaseController method
	 * actionRemoveDocumentFromList and executing transaction</li>
	 * <li>Transaction used:removeFinancialDocs_db</li>
	 * <li>Transaction used: removeAgencyFinancialDocs_db</li>
	 * </ul>
	 * @param aoChannel Channel object
	 * @param aoHmDocReqProps Document required property
	 * @param asUserOrgType User Organization type
	 * @param asUserName user name
	 * @param asProcurementId procurement id
	 * @param asProcurementStatus procurement status
	 * @param asDeletedDocumentId Deleted Document Id
	 * @param asDocumentSequence Document sequence
	 * @param asHdnTableName Hidden Table name
	 * @return String lsErrorMsg
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public static String actionRemoveDocChannelUtil(Channel aoChannel, HashMap aoHmDocReqProps, String asUserOrgType,
			String asUserName, String asProcurementId, String asProcurementStatus, String asDeletedDocumentId,
			String asDocumentSequence, String asHdnTableName) throws ApplicationException
	{
		String lsErrorMsg;
		try
		{
			Map<String, String> loParamMap = new HashMap<String, String>();
			loParamMap.put(HHSConstants.BASE_DOCUMENT_UPLOAD_TO, HHSConstants.CONTRACT);
			String lsTransactionName = HHSConstants.BASE_REMOVE_FINANCIAL_DOCS_DB;
			if (ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(asUserOrgType))
			{
				loParamMap.put(HHSConstants.BASE_DOCUMENT_UPLOAD_TO, HHSConstants.EMPTY_STRING);
			}
			else
			{
				lsTransactionName = HHSConstants.BASE_REMOVE_AGENCY_FINANCIAL_DOCS_DB;
			}
			loParamMap.put(HHSConstants.USER_ID, asUserName);
			loParamMap.put(HHSConstants.MOD_BY_USER_ID, asUserName);
			loParamMap.put(HHSConstants.AS_DEL_DOC_ID, asDeletedDocumentId);
			loParamMap.put(HHSConstants.BASE_AS_DOCUMENT_SEQUENCE, asDocumentSequence);
			loParamMap.put(HHSConstants.HDN_TABLE_NAME, asHdnTableName);
			aoChannel.setData(HHSConstants.AO_PARAM_MAP, loParamMap);
			aoHmDocReqProps.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, false);
			aoChannel.setData(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
			aoChannel.setData(HHSConstants.DOC_TYPE, HHSConstants.EMPTY_STRING);
			aoChannel.setData(HHSConstants.DOC_ID, asDeletedDocumentId);
			aoChannel.setData(HHSConstants.LO_HM_DOC_REQ_PROPS, aoHmDocReqProps);
			aoChannel.setData(HHSConstants.LS_PROC_STATUS, asProcurementStatus);
			aoChannel.setData(HHSConstants.USER_ID, asUserName);
			aoChannel.setData(HHSConstants.DOCUMENT_TYPE, HHSConstants.EMPTY_STRING);
			aoChannel.setData(HHSConstants.LO_LAST_MOD_HASHMAP, loParamMap);
			aoChannel.setData(HHSConstants.LB_SUCCESS_STATUS, true);
			HHSTransactionManager.executeTransaction(aoChannel, lsTransactionName);
			lsErrorMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
					HHSConstants.BASE_FINANCE_DOC_REMOVED_SUCCESS);
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While Setting the default channel for BaseController method", aoEx);
			LOG_OBJECT.Error("Error Occured While Setting the default channel for BaseController method", loAppEx);
			throw loAppEx;
		}
		return lsErrorMsg;
	}

	/**
	 * <ul>
	 * <li>Setting the default channel for BaseController method
	 * actionRemoveDocNxtChannel and executing transaction</li>
	 * <li>Transaction used: getFinancialDocuments_db</li>
	 * </ul>
	 * @param aoChannel channel object
	 * @param asUserOrgType User Organization type
	 * @param aoCBGridBean Grid Bean
	 * @param asContractId ContractId
	 * @return List<ExtendedDocument> loFinancialDocumentList
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public static List<ExtendedDocument> actionRemoveDocNxtChannelUtil(Channel aoChannel, String asUserOrgType,
			CBGridBean aoCBGridBean, String asContractId) throws ApplicationException
	{
		List<ExtendedDocument> loFinancialDocumentList = null;
		try
		{
			loFinancialDocumentList = null;
			HashMap<String, String> loRequiredParamMap = new HashMap<String, String>();
			loRequiredParamMap.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, ApplicationConstants.EMPTY_STRING);
			aoChannel.setData(HHSConstants.HM_REQIRED_PROPERTY_MAP, loRequiredParamMap);
			Map loMap = new HashMap();
			loMap.put(HHSConstants.ORGANIZATION_TYPE, asUserOrgType);
			loMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, asContractId);
			loMap.put(HHSConstants.BUDGET_ID_WORKFLOW, aoCBGridBean.getContractBudgetID());
			loMap.put(HHSConstants.INVOICE_ID, aoCBGridBean.getInvoiceId());
			//Added for R6- Adding Returned payment id in map
			if (aoChannel.getData().containsKey(HHSConstants.RETURN_PAYMENT_DETAIL_ID))
			{
			loMap.put(HHSConstants.RETURN_PAYMENT_DETAIL_ID, aoChannel.getData(HHSConstants.RETURN_PAYMENT_DETAIL_ID));
			}
			//Added for R6- Adding Returned payment id in map end
			aoChannel.setData(HHSConstants.AO_PARAMETER_MAP, loMap);
			HHSTransactionManager.executeTransaction(aoChannel, HHSConstants.GET_FINANCIAL_DOCUMENTS_DB);
			loFinancialDocumentList = (List<ExtendedDocument>) aoChannel
					.getData(HHSConstants.AO_FINANCIAL_DOCUMENT_LIST);
			if (loFinancialDocumentList != null && !loFinancialDocumentList.isEmpty())
			{
				Collections.sort(loFinancialDocumentList, new Comparator<ExtendedDocument>()
				{
					@Override
					public int compare(ExtendedDocument c1, ExtendedDocument c2)
					{
						Date aoCreatedDate1 = HHSUtil.ConvertStringToDate(c1.getCreatedDate());
						Date aoCreatedDate2 = HHSUtil.ConvertStringToDate(c2.getCreatedDate());
						int liResult = aoCreatedDate1.compareTo(aoCreatedDate2);
						if (liResult != 0 && liResult > 0)
						{
							liResult = -1;
						}
						else if (liResult != 0 && liResult < 0)
						{
							liResult = 1;
						}
						if (liResult == 0)
						{
							liResult = c1.getDocumentTitle().toLowerCase()
									.compareTo(c2.getDocumentTitle().toLowerCase());
						}
						return liResult;
					}
				});
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While setting default channel for BaseController method", aoEx);
			LOG_OBJECT.Error("Error Occured While setting default channel for BaseController method", loAppEx);
			throw loAppEx;
		}
		return loFinancialDocumentList;
	}

	/**
	 * <ul>
	 * <li>Setting the default bean for BaseController method reAssignTask</li>
	 * </ul>
	 * @param asdefault default Value Map
	 * @param aoChannel channel object
	 * @param aoTaskDetailsBean task details bean
	 * @throws ApplicationException Application Exception
	 */
	@SuppressWarnings("rawtypes")
	public static void reAssignTaskDefPara(HashMap asdefault, Channel aoChannel, TaskDetailsBean aoTaskDetailsBean)
			throws ApplicationException
	{
		try
		{
			aoTaskDetailsBean.setProviderComment((String) asdefault.get(HHSConstants.LS_PUBLIC_COMMENT));
			aoTaskDetailsBean.setInternalComment((String) asdefault.get(HHSConstants.LS_INTERNAL_COMMENT));
			aoTaskDetailsBean.setReassignUserId((String) asdefault.get(HHSConstants.LS_REASSIGN_USER_ID));
			aoTaskDetailsBean.setTaskType((String) asdefault.get(HHSConstants.LS_TASK_TYPE));
			aoTaskDetailsBean.setWorkFlowId((String) asdefault.get(HHSConstants.LS_WOB_NUM));
			aoTaskDetailsBean.setReassignUserName((String) asdefault.get(HHSConstants.LS_REASSIGN_USER_NAME));

			aoChannel.setData(HHSConstants.AO_TASK_DETAILS_BEAN, aoTaskDetailsBean);
			aoChannel.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, Boolean.TRUE);
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While Setting the default bean for BaseController method reAssignTask", aoEx);
			LOG_OBJECT.Error("Error:: BaseControllerUtil:" + "reAssignTaskDefPara method - "
					+ "Error Occured While Setting the default bean for BaseController method reAssignTask", loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * This method fetched the taskDetailBeanfromFilenet Util <li>Transaction
	 * used: fetchTaskDetails</li>
	 * @param asWorkflowId Workflow id
	 * @param asUserId user Id
	 * @param aoTaskDetailsBean task details bean
	 * @param aoChannel channel object
	 * @return aoTaskDetailsBean task details bean
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public static TaskDetailsBean fetchTaskDetailsFromFilenetUtil(String asWorkflowId, String asUserId,
			TaskDetailsBean aoTaskDetailsBean, Channel aoChannel) throws ApplicationException
	{
		try
		{
			aoTaskDetailsBean.setWorkFlowId(asWorkflowId);
			aoTaskDetailsBean.setUserId(asUserId);

			aoChannel.setData(HHSConstants.AO_TASK_DETAILS_BEAN, aoTaskDetailsBean);
			HHSTransactionManager.executeTransaction(aoChannel, HHSConstants.FETCH_TASK_DETAILS);
			aoTaskDetailsBean = (TaskDetailsBean) aoChannel.getData(HHSConstants.TASK_DETAILS);
			if (null != aoTaskDetailsBean && aoTaskDetailsBean.getTaskId() == null)
			{
				aoTaskDetailsBean.setTaskId(String.valueOf((HHSConstants.INITIAL_TASK_ID)));
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error Occured While setting TaskDetailBean", aoEx);
			LOG_OBJECT.Error("Error Occured While setting TaskDetailBean", loAppEx);
			throw loAppEx;
		}
		return aoTaskDetailsBean;
	}

	/**
	 * Closing the Print Writer
	 * @param aoOut PrintWriter
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public static void closingPrintWriter(PrintWriter aoOut) throws ApplicationException
	{
		try
		{
			if (null != aoOut)
			{
				aoOut.flush();
				aoOut.close();
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error Occured While functioning on Print Writer",
					aoEx);
			LOG_OBJECT.Error("Error Occured While functioning on Print Writer", loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * This method perform actions on database based on operation performed on
	 * Subgrid
	 * <ul>
	 * <li>If add operation perform on the grid insert row into database</li>
	 * <li>If delete operation perform on the grid delete row from database</li>
	 * <li>If edit operation perform on the grid update row into database</li>
	 * <li>Transaction used: generateMasterBean</li>
	 * </ul>
	 * @param asOperation operation to Select
	 * @param asTransactionName Transaction name
	 * @param aoChannelObj Channel Object
	 * @param aoBeanObj Bean Object
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public static MasterBean generateMasterBean(String asBudgetId, P8UserSession aoUserSession)
			throws ApplicationException
	{
		MasterBean loMasterBean = null;
		try
		{
			Channel loChannelObj = new Channel();
			loChannelObj.setData(HHSConstants.BUDGET_ID_KEY, asBudgetId);
			loChannelObj.setData(ApplicationConstants.FILENET_SESSION, aoUserSession);

			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.GENERATE_MASTER_BEAN);
			loMasterBean = (MasterBean) loChannelObj.getData(HHSConstants.AO_RETURNED_MASTER_BEAN);

		}
		// Catch (and throw as it is) ApplicationException thrown from
		// HHSTransactionManager.executeTransaction method
		catch (ApplicationException aoAppEx)
		{
			throw aoAppEx;
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While generating MasterBean for Budget Id : " + asBudgetId, aoEx);
			LOG_OBJECT.Error("Error Occured While generating MasterBean for Budget Id : ", loAppEx);
			throw loAppEx;
		}
		return loMasterBean;
	}

	/**
	 * Returns the current fiscal year according to sysdate
	 * @return
	 */
	public static String getCurrentFiscalYear()
	{
		Calendar loCalendar = Calendar.getInstance();
		Integer loYear = loCalendar.get(Calendar.YEAR);
		Integer loMonth = loCalendar.get(Calendar.MONTH) + 1;

		loYear = (loMonth > HHSConstants.INT_SIX ? loYear + 1 : loYear);
		return loYear.toString();
	}
	
	public static boolean isDateValid(String dateStr){		
		IDateValidator iDateValidator = new DateValidatorImpl(HHSConstants.MMDDYYFORMAT);
		return iDateValidator.isValid(dateStr);		
	}
	
	public static boolean isDateOneBeforeDateTwo(String dateOneStr, String dateTwoStr){		
		IDateValidator iDateValidator = new DateValidatorImpl(HHSConstants.MMDDYYFORMAT);	
		return iDateValidator.isDateOneBeforeDateTwo(dateOneStr, dateTwoStr);
	}
	
	

}