package com.nyc.hhs.daomanager.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.jdom.Element;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.TaskAuditBean;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.XMLUtil;

/**
 * This service class will get the method calls from controller through
 * transaction layer. Execute queries by calling mapper and return query output
 * back to controller. If any error exists, wrap the exception into Application
 * Exception and throw it to controller.<br>
 * Service Class added in R4
 */
public class TaskAuditService extends ServiceState
{
	/**
	 * This is a log object which is used to log any error or exception into log
	 * file
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(TaskAuditService.class);
    private Object loTaskAuditBean;

	/**
	 * This method is used to insert audit details while launching R3 workflows
	 * 
	 * <ul>
	 * <li>1. Get HhsAudit Bean and check for the entity type</li>
	 * <li>2. Get task audit configuration from cache object and get document
	 * object</li>
	 * <li>3. Get element object for input task type</li>
	 * <li>4. Get entity name and user type from element object</li>
	 * <li>5. Get task audit bean object and call insertTaskAudit() to insert
	 * audit details</li>
	 * <li>6. Return insert status to the calling method</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession SQL Mybatis Session
	 * @param aoHhsAuditBean HhsAuditBean containing audit properties
	 * @return insert status
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Boolean insertAuditForLaunchWorkflow(SqlSession aoMyBatisSession, HhsAuditBean aoHhsAuditBean)
			throws ApplicationException
	{
		Boolean loInsertAuditStatus = false;
		Map<String, Object> loHmReqExceProp = new HashMap<String, Object>();
		loHmReqExceProp.put(HHSConstants.HHS_AUDIT_BEAN, aoHhsAuditBean);
		try
		{
			if (null != aoHhsAuditBean && null != aoHhsAuditBean.getEntityType())
			{
				// updated service for task audit
				org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
						HHSConstants.TASK_AUDIT_CONFIGURATION);
				if (loXMLDoc == null)
				{
					ApplicationException loAppex = new ApplicationException(
							"Task Audit Configuration has not been loaded into the memory.");
					loAppex.setContextData(loHmReqExceProp);
					throw loAppex;
				}
				String lsTaskType = aoHhsAuditBean.getEntityType();
				Element loTaskElt = XMLUtil.getElement("//taskType[@value=\"" + lsTaskType + "\"]", loXMLDoc);
				if (null != loTaskElt)
				{
					String lsEntityName = loTaskElt.getAttributeValue(HHSConstants.ENTITY_NAME);
					String lsUserType = HHSUtil.getUserTypeFromUserId(aoHhsAuditBean.getUserId());
					String lsTaskLevel = aoHhsAuditBean.getTaskLevel();
					String lsEventType = aoHhsAuditBean.getTaskEvent();
					if (null == lsTaskLevel || lsTaskLevel.isEmpty())
					{
						lsTaskLevel = HHSConstants.LEVEL_ONE;
					}
					if (null == lsEventType || lsEventType.isEmpty())
					{
						lsEventType = HHSConstants.WORKFLOW_LAUNCH;
					}
					TaskAuditBean loTaskAuditBean = HHSUtil.getTaskAuditBean(aoHhsAuditBean.getWorkflowId(),
							HHSConstants.ONE, lsEventType, lsTaskType, aoHhsAuditBean.getEntityId(), lsEntityName,
							lsTaskLevel, lsUserType, aoHhsAuditBean.getContractId(), aoHhsAuditBean.getUserId(),
							HHSConstants.WF_INITIAL_REVIEWER, null);
					updateEndDateForPreviousStep(aoMyBatisSession, loTaskAuditBean);
					loInsertAuditStatus = insertTaskAudit(aoMyBatisSession, loTaskAuditBean);
					setMoState("Task Audit Inserted successfully for workflow ID:" + aoHhsAuditBean.getWorkflowId());
				}
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		// handling ApplicationException in this block
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error(
					"Error occurred while inserting task audit for workflow ID:" + aoHhsAuditBean.getWorkflowId(),
					aoAppEx);
			setMoState("Error occurred while inserting task audit for workflow ID:" + aoHhsAuditBean.getWorkflowId());
			throw aoAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			LOG_OBJECT
					.Error("Error occurred while inserting task audit for workflow ID:"
							+ aoHhsAuditBean.getWorkflowId(), aoEx);
			setMoState("Error occurred while inserting task audit for workflow ID:" + aoHhsAuditBean.getWorkflowId());
			throw new ApplicationException("Error occurred while inserting task audit for workflow ID:"
					+ aoHhsAuditBean.getWorkflowId(), aoEx);
		}
		return loInsertAuditStatus;
	}

	/**
	 * This method is used to insert audit details while reassigning and
	 * finishing R3 workflows
	 * 
	 * <ul>
	 * <li>1. Get TaskDetailsBean and check for the task status</li>
	 * <li>2. If task status is null, set event type as Task Assignment else as
	 * Task Finished</li>
	 * <li>3. Get task audit configuration from cache object and get document
	 * object</li>
	 * <li>4. Get element object for input task type</li>
	 * <li>5. Get entity name and user type from element object</li>
	 * <li>6. Get task audit bean object and call insertTaskAudit() to insert
	 * audit details</li>
	 * <li>7. Return insert status to the calling method</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession SQL Mybatis Session
	 * @param aoTaskDetailsBean TaskDetailsBean containing task details
	 *            properties
	 * @param abExecuteFlag execution flag- Boolean Object
	 * @return insert status
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Boolean insertAuditForReassignAndFinish(SqlSession aoMyBatisSession, TaskDetailsBean aoTaskDetailsBean,
			Boolean abExecuteFlag) throws ApplicationException
	{
		//*** Start QC 9585 R 8.9 do not expose password for service account in logs
		String param = CommonUtil.maskPassword(aoTaskDetailsBean);
		LOG_OBJECT.Debug("Entering into insertAuditForReassignAndFinish" + param);
		//*** End QC 9585 R 8.9 do not expose password for service account in logs
		
		Boolean loInsertAuditStatus = false;
		HashMap<String, Object> loHmReqExceProp = new HashMap<String, Object>();
		loHmReqExceProp.put(HHSConstants.TASK_AUDIT_BEAN_KEY, aoTaskDetailsBean);
		try
		{
			if (null != abExecuteFlag && abExecuteFlag && null != aoTaskDetailsBean)
			{
				// updated service for task audit
				org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
						HHSConstants.TASK_AUDIT_CONFIGURATION);
				if (loXMLDoc == null)
				{
					ApplicationException loAppex = new ApplicationException(
							"Task Audit Configuration has not been loaded into the memory.");
					loAppex.setContextData(loHmReqExceProp);
					throw loAppex;
				}
				String lsTaskType = aoTaskDetailsBean.getEntityType();
				LOG_OBJECT.Debug("TaskType :: "+lsTaskType);
				if (null != lsTaskType)
				{
					Element loTaskElt = XMLUtil.getElement("//taskType[@value=\"" + lsTaskType + "\"]", loXMLDoc);
					if (null != loTaskElt)
					{
						loInsertAuditStatus = getAndUpdateTaskAudit(aoMyBatisSession, aoTaskDetailsBean,
								loInsertAuditStatus, lsTaskType, loTaskElt);
					}
				}
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		// handling ApplicationException in this block
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error(
					"Error occurred while inserting task audit for workflow ID:" + aoTaskDetailsBean.getWorkFlowId(),
					aoAppEx);
			setMoState("Error occurred while inserting task audit for workflow ID:" + aoTaskDetailsBean.getWorkFlowId());
			throw aoAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error(
					"Error occurred while inserting task audit for workflow ID:" + aoTaskDetailsBean.getWorkFlowId(),
					aoEx);
			setMoState("Error occurred while inserting task audit for workflow ID:" + aoTaskDetailsBean.getWorkFlowId());
			throw new ApplicationException("Error occurred while inserting task audit for workflow ID:"
					+ aoTaskDetailsBean.getWorkFlowId(), aoEx);
		}
		return loInsertAuditStatus;
	}

	/**
	 * This method is used to get task audit bean from task type, properties
	 * from task details bean and insert task audit entry by calling method
	 * "insertTaskAudit"
	 * 
	 * @param aoMyBatisSession SQL mybatis session
	 * @param aoTaskDetailsBean TaskDetailsBean object
	 * @param aoInsertAuditStatus boolean value of insert status
	 * @param asTaskType a string value of task type
	 * @param aoTaskElt Element object
	 * @return boolean value of update status
	 * @throws NumberFormatException
	 * @throws ApplicationException If an Application Exception occurs
	 */
	private Boolean getAndUpdateTaskAudit(SqlSession aoMyBatisSession, TaskDetailsBean aoTaskDetailsBean,
			Boolean aoInsertAuditStatus, String asTaskType, Element aoTaskElt) throws ApplicationException
	{
		//*** Start QC 9585 R 8.9 do not expose password for service account in logs
		String param = CommonUtil.maskPassword(aoTaskDetailsBean);
		LOG_OBJECT.Debug("Entering into :getAndUpdateTaskAudit" + param);
		//*** End QC 9585 R 8.9 do not expose password for service account in logs
		String lsEventType = null;
		String lsNextLevel = null;
		String lsAssignedTo = null;
		String lsCurrentLevel = aoTaskDetailsBean.getLevel();
		boolean lbIsTaskAudit = true;
		boolean lbIsLinkedWobNo = false;
		boolean lbInsertSecondAudit = false;
		if (null == aoTaskDetailsBean.getTaskStatus())
		{
			lsEventType = P8Constants.EVENT_NAME_ASSIGN;
			lsAssignedTo = aoTaskDetailsBean.getReassignUserId();
			lsNextLevel = lsCurrentLevel;
		}
		else
		{
			if (lsCurrentLevel.equalsIgnoreCase(aoTaskDetailsBean.getTotalLevel())
					&& !aoTaskDetailsBean.getTaskStatus().equalsIgnoreCase(HHSConstants.TASK_RFR))
			{
				lsEventType = HHSConstants.TASK_FINISHED_KEY;
			}
			else if (aoTaskDetailsBean.getTaskStatus().equalsIgnoreCase(HHSConstants.STR_BUDGET_APPROVED))
			{
				lsNextLevel = String.valueOf((Integer.parseInt(lsCurrentLevel) + 1));
				if (asTaskType.equalsIgnoreCase(HHSConstants.TASK_CONTRACT_CONFIGURATION)
						|| asTaskType.equalsIgnoreCase(HHSConstants.TASK_AMENDMENT_CONFIGURATION))
				{
					lsEventType = HHSConstants.APPROVED_AND_LAUNCHED;
					lbIsTaskAudit = false;
					if (null != aoTaskDetailsBean.getLinkedWobNum() && !aoTaskDetailsBean.getLinkedWobNum().isEmpty())
					{
						lbInsertSecondAudit = true;
					}
				}
				else
				{
					lsEventType = HHSConstants.APPROVED_AND_MOVED_TO_NEXT_LEVEL;
					lsAssignedTo = HHSConstants.WF_INITIAL_REVIEWER;
					lsCurrentLevel = lsNextLevel;
				}
			}
			else if (aoTaskDetailsBean.getTaskStatus().equalsIgnoreCase(HHSConstants.TASK_RFR)
					&& lsCurrentLevel.equalsIgnoreCase(HHSConstants.ONE))
			{
				lsEventType = HHSConstants.RETURNED_TO_PROVIDER;
				lsAssignedTo = HHSConstants.PROVIDER;
				lsCurrentLevel = HHSConstants.ZERO;
				if (asTaskType.equalsIgnoreCase(HHSConstants.TASK_PAYMENT_REVIEW)
						|| asTaskType.equalsIgnoreCase(HHSConstants.TASK_ADVANCE_PAYMENT_REVIEW))
				{
					lbInsertSecondAudit = true;
				}
			}
			else
			{
				lsEventType = HHSConstants.RETURNED_TO_PREVIOUS_LEVEL;
				lsAssignedTo = HHSConstants.WF_INITIAL_REVIEWER;
				if (!lsCurrentLevel.equalsIgnoreCase(aoTaskDetailsBean.getTotalLevel()))
				{
					lsNextLevel = String.valueOf((Integer.parseInt(lsCurrentLevel) + 1));
				}
				if ((asTaskType.equalsIgnoreCase(HHSConstants.TASK_CONTRACT_COF) || asTaskType
						.equalsIgnoreCase(HHSConstants.TASK_AMENDMENT_COF))
						&& lsCurrentLevel.equalsIgnoreCase(HHSConstants.TWO))
				{
					lbIsLinkedWobNo = true;
				}
				lsCurrentLevel = String.valueOf((Integer.parseInt(lsCurrentLevel) - 1));
			}
		}
		lsCurrentLevel = HHSConstants.LEVEL + lsCurrentLevel;
		if (null != lsNextLevel)
		{
			lsNextLevel = HHSConstants.LEVEL + lsNextLevel;
		}
		String lsUserType = HHSUtil.getUserTypeFromUserId(aoTaskDetailsBean.getUserId());
		setMoState("User Type:" + lsUserType);
		LOG_OBJECT.Debug("User Type:" + lsUserType);
		TaskAuditBean loTaskAuditBean = HHSUtil.getTaskAuditBean(aoTaskDetailsBean.getWorkFlowId(), null, lsEventType,
				null, null, null, lsCurrentLevel, lsUserType, null, aoTaskDetailsBean.getUserId(), lsAssignedTo,
				lsNextLevel);
		//*** Start QC 9585 R 8.9 do not expose password for service account in logs
		param = CommonUtil.maskPassword(loTaskAuditBean);
		LOG_OBJECT.Debug("TaskBean:: " + param);
		//*** End QC 9585 R 8.9 do not expose password for service account in logs
		
		updateEndDateForPreviousStep(aoMyBatisSession, loTaskAuditBean);
		if (lbIsLinkedWobNo)
		{
			loTaskAuditBean.setWorkflowId(aoTaskDetailsBean.getLinkedWobNum());
		}
		if (lbIsTaskAudit)
		{
			aoInsertAuditStatus = insertTaskAudit(aoMyBatisSession, loTaskAuditBean);
		}
		if (lbInsertSecondAudit)
		{
			TaskAuditBean loAuditBean = null;
			if (asTaskType.equalsIgnoreCase(HHSConstants.TASK_PAYMENT_REVIEW)
					|| asTaskType.equalsIgnoreCase(HHSConstants.TASK_ADVANCE_PAYMENT_REVIEW))
			{
				loAuditBean = HHSUtil.getTaskAuditBean(aoTaskDetailsBean.getLinkedWobNum(), null,
						P8Constants.EVENT_NAME_ASSIGN, null, null, null, null, lsUserType, null,
						aoTaskDetailsBean.getUserId(), HHSConstants.WF_INITIAL_REVIEWER, null);
			}
			else if (asTaskType.equalsIgnoreCase(HHSConstants.TASK_CONTRACT_CONFIGURATION)
					|| asTaskType.equalsIgnoreCase(HHSConstants.TASK_AMENDMENT_CONFIGURATION))
			{
				loAuditBean = HHSUtil.getTaskAuditBean(aoTaskDetailsBean.getLinkedWobNum(), null,
						HHSConstants.APPROVED_AND_LAUNCHED, null, null, null, HHSConstants.LEVEL + HHSConstants.TWO,
						lsUserType, null, aoTaskDetailsBean.getUserId(), HHSConstants.WF_INITIAL_REVIEWER, null);
				loAuditBean.setLinkedWobNo(aoTaskDetailsBean.getLinkedWobNum());
				loAuditBean.setNextLevel(HHSConstants.LEVEL + HHSConstants.TWO);
			}
			updateEndDateForPreviousStep(aoMyBatisSession, loAuditBean);
			aoInsertAuditStatus = insertTaskAudit(aoMyBatisSession, loAuditBean);
		}
		setMoState("Task Audit Inserted successfully for workflow ID:" + loTaskAuditBean.getWorkflowId());
		return aoInsertAuditStatus;
	}

	/**
	 * This method is used to insert task audit details for R3 workflows
	 * 
	 * <ul>
	 * <li>1. Get event type from task audit bean</li>
	 * <li>2. Check for the value of event type</li>
	 * <li>3. If event type equals WorkflowLaunch, execute query with Id
	 * "insertTaskAuditForLaunch" from TaskAuditMapper</li>
	 * <li>4. Else execute query with Id "insertTaskAudit" from TaskAuditMapper</li>
	 * <li>5. Return insert status to the calling method</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession SQL Mybatis Session
	 * @param aoTaskAuditBean TaskAuditBean containing audit properties
	 * @return insert status
	 * @throws ApplicationException If an Application Exception occurs
	 */
	private Boolean insertTaskAudit(SqlSession aoMyBatisSession, TaskAuditBean aoTaskAuditBean)
			throws ApplicationException
	{
		//*** Start QC 9585 R 8.9 do not expose password for service account in logs
		String param = CommonUtil.maskPassword(aoTaskAuditBean);
		LOG_OBJECT.Debug("Entering into insertTaskAudit: " + param);
		//*** End QC 9585 R 8.9 do not expose password for service account in logs

		Boolean loInsertAuditStatus = false;
		try
		{
			if (aoTaskAuditBean.getEventType().equalsIgnoreCase(HHSConstants.WORKFLOW_LAUNCH)
					|| (aoTaskAuditBean.getEventType().equalsIgnoreCase(HHSConstants.APPROVED_AND_LAUNCHED) && (null == aoTaskAuditBean
							.getLinkedWobNo() || aoTaskAuditBean.getLinkedWobNo().isEmpty())))
			{
				DAOUtil.masterDAO(aoMyBatisSession, aoTaskAuditBean, HHSConstants.MAPPER_CLASS_TASK_AUDIT_MAPPER,
						HHSConstants.INSERT_TASK_AUDIT_FOR_LAUNCH, HHSConstants.TASK_AUDIT_BEAN);
			}
			else
			{
				DAOUtil.masterDAO(aoMyBatisSession, aoTaskAuditBean, HHSConstants.MAPPER_CLASS_TASK_AUDIT_MAPPER,
						HHSConstants.INSERT_TASK_AUDIT, HHSConstants.TASK_AUDIT_BEAN);
			}
			loInsertAuditStatus = true;
			setMoState("Task Audit Inserted successfully for workflow ID:" + aoTaskAuditBean.getWorkflowId());
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		// handling ApplicationException in this block
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData("Task Audit Bean ", aoTaskAuditBean);
			LOG_OBJECT.Error(
					"Error occurred while inserting task audit for workflow ID:" + aoTaskAuditBean.getWorkflowId(),
					aoAppEx);
			setMoState("Error occurred while inserting task audit for workflow ID:" + aoTaskAuditBean.getWorkflowId());
			throw aoAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error(
					"Error occurred while inserting task audit for workflow ID:" + aoTaskAuditBean.getWorkflowId(),
					aoEx);
			setMoState("Error occurred while inserting task audit for workflow ID:" + aoTaskAuditBean.getWorkflowId());
			throw new ApplicationException("Error occurred while inserting task audit for workflow ID:"
					+ aoTaskAuditBean.getWorkflowId(), aoEx);
		}
		return loInsertAuditStatus;
	}

	/**
	 * <ul>
	 * <li>1. Get HhsAuditBean List and check for null value</li>
	 * <li>2. Iterate through the list and call insertAuditForLaunchWorkflow()
	 * to insert audit details</li>
	 * <li>3. Return insert status to the calling method</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession SQL Mybatis Session
	 * @param aoHhsAuditBeanList HhsAuditBean List
	 * @return insert status
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Boolean insertAuditForMultipleLaunchWorkflow(SqlSession aoMyBatisSession,
			List<HhsAuditBean> aoHhsAuditBeanList) throws ApplicationException
	{
		Boolean loInsertStatus = false;
		if (null != aoHhsAuditBeanList)
		{
			for (HhsAuditBean loHhsAuditBean : aoHhsAuditBeanList)
			{
				loInsertStatus = insertAuditForLaunchWorkflow(aoMyBatisSession, loHhsAuditBean);
			}
		}
		return loInsertStatus;
	}

	/**
	 * <ul>
	 * <li>1. Get TaskDetailsBean List and check for null value</li>
	 * <li>2. Iterate through the list and call
	 * insertAuditForReassignAndFinish() to insert audit details</li>
	 * <li>3. Return insert status to the calling method</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession -SQL Mybatis Session
	 * @param aoHhsAuditBeanList- TaskDetailsBean List
	 * @param aoTaskDetailsBeanList- List<TaskDetailsBean> Object
	 * @return loInsertAuditStatus -Boolean Object
	 * @throws ApplicationException If an Application Exception occurs
	 */

	public Boolean insertAuditForMultiReassign(SqlSession aoMyBatisSession,
			List<TaskDetailsBean> aoTaskDetailsBeanList, Boolean abExecuteFlag) throws ApplicationException
	{
		Boolean loInsertAuditStatus = false;
		if (null != aoTaskDetailsBeanList)
		{
			for (TaskDetailsBean loTaskDetailsBean : aoTaskDetailsBeanList)
			{
				loInsertAuditStatus = insertAuditForReassignAndFinish(aoMyBatisSession, loTaskDetailsBean,
						abExecuteFlag);
			}
		}
		return loInsertAuditStatus;
	}

	/**
	 * 
	 * This method is used to update end date for last step corresponding to
	 * workflow Id
	 * <ul>
	 * <li>1. Execute query with id <b>updateEndDateForPreviousStep</b> from
	 * task audit mapper</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession SQL Mybatis Session
	 * @param aoTaskAuditBean TaskAuditBean object
	 * @throws ApplicationException If an Application Exception occurs
	 */
	private void updateEndDateForPreviousStep(SqlSession aoMyBatisSession, TaskAuditBean aoTaskAuditBean)
			throws ApplicationException
	{
		DAOUtil.masterDAO(aoMyBatisSession, aoTaskAuditBean, HHSConstants.MAPPER_CLASS_TASK_AUDIT_MAPPER,
				HHSConstants.UPDATE_END_DATE_FOR_PREVIOUS_STEP, HHSConstants.TASK_AUDIT_BEAN);
	}
	
	
    /**
     * 
     * This method is used to fetch max budget update audit data by contract id in order to obtain  WORKFLOW_ID and audit log info
     * <ul>
     * 
     * @param aoMyBatisSession SQL Mybatis Session
     * @param aoTaskAuditBean  aoContractId String
     * @throws ApplicationException If an Application Exception occurs
     */
	
	   @SuppressWarnings("unchecked")
    public List<TaskAuditBean> fetchMaxBudgetUpdateAuditDataByContractId(SqlSession aoMyBatisSession, String aoContractId)
	            throws ApplicationException
	    {
	        return (List<TaskAuditBean>) DAOUtil.masterDAO(aoMyBatisSession, aoContractId, HHSConstants.MAPPER_CLASS_TASK_AUDIT_MAPPER,
	                HHSConstants.MAX_BUDGET_UPDATE_AUDIT_BY_CONTRACT_ID, HHSConstants.JAVA_LANG_STRING);
	    }
	   
	   //[Start] R8.4.0 QC_9468 Delete Update Budget not working for multiple years
	   /*[Start] R7.4.0 QC9008 Ability to delete Budget Update Task*/
	    /**
	     * 
	     * This method is to add audit data from max budget update audit data by contract id 
	     * <ul>
	     * 
	     * @param aoMyBatisSession SQL Mybatis Session
	     * @param aoTaskAuditBean  aoContractId String
	     * @param List  asDelResultWFList
	     * @param String  asUserId
	     * @throws ApplicationException If an Application Exception occurs
	     */
	      
	       @SuppressWarnings("unchecked")
	       public boolean insertTaskDeleteAudit(SqlSession aoMyBatisSession, String asContractId, List<String> asDelResultWFList, String asUserId)
	                throws ApplicationException
	        {
	         	           
	           if(asDelResultWFList!=null) {
	             
	        	   if(asDelResultWFList.size()==0){
	            	 //Nothing to insert
	            	 LOG_OBJECT.Info(
	     					"Nothing to insert -- task audit for contract ID:" + asContractId+",asDelResultWFList:" +asDelResultWFList +",asUserId:" +asUserId);	     			
	            	 return true;
	             }
	        	   
	             for (String asDelResultWF : asDelResultWFList){
	            	 TaskAuditBean loTaskAuditBean = new TaskAuditBean();
	            	 loTaskAuditBean.setWorkflowId(asDelResultWF);
	            	 loTaskAuditBean.setContractId(asContractId);
	            	 loTaskAuditBean.setUserType(HHSConstants.CITY);
	            	 loTaskAuditBean.setEventType(HHSConstants.TASK_REMOVAL);
	            	 loTaskAuditBean.setCreatedBy(asUserId);

	            	 DAOUtil.masterDAO(aoMyBatisSession, loTaskAuditBean, HHSConstants.MAPPER_CLASS_TASK_AUDIT_MAPPER,
	                    HHSConstants.AUDIT_DELETING_BUDGET_UPDATE_TASK, HHSConstants.TASK_AUDIT_BEAN);
	             }
	             return true;
	           }else{
	        	   LOG_OBJECT.Error(
	   					"Error occurred while inserting task audit for task audit for contract ID:" + asContractId+",asDelResultWFList is null:" +asDelResultWFList +",asUserId:" +asUserId);
	   			
	        	   return false; 
	           }

	        }
	       /*[End] R7.4.0 QC9008 Ability to delete Budget Update Task*/
	       //[End] R8.4.0 QC_9468 Delete Update Budget not working for multiple years

}


