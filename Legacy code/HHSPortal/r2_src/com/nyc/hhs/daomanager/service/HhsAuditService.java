package com.nyc.hhs.daomanager.service;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.util.DAOUtil;

/**
 * This service class will get the method calls from controller through
 * transaction layer. Execute queries by calling mapper and return query output
 * back to controller. If any error exists, wrap the exception into Application
 * Exception and throw it to controller.
 */
public class HhsAuditService extends ServiceState
{
	/**
	 * This is a log object which is used to log any error or exception into log
	 * file
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(HhsAuditService.class);

	/**
	 * This Method Inserts the Data using HhsAuditBean bean in the Audit's
	 * table. <li>This query used: hhsauditProviderInsert</li> <li>This query
	 * used: hhsauditAgencyInsert</li> <li>This query used:
	 * hhsauditAcceleratorInsert</li> <li>This query used:
	 * updateCommentNonAuditForProvider</li> <li>This query used:
	 * updateCommentNonAudit</li> <li>This query used: saveCommentNonAudit</li>
	 * @param aoMyBatisSession : passes the MyBatis SQL Session
	 * @param aoAudit : HhsAuditBean containing the required Info for
	 *            transaction
	 * @param aoExecuteAudit : aoExecuteAudit as input.
	 * @return lbStatus : Boolean : returns the insert status as TRUE if
	 *         transaction is successful or FALSE in case no matching branch is
	 *         found in the method
	 * @throws ApplicationException ApplicationException thrown in case any
	 *             query fails.
	 */
	public Boolean hhsauditInsert(SqlSession aoMyBatisSession, HhsAuditBean aoAudit, Boolean aoExecuteAudit)
			throws ApplicationException
	{
		boolean lbStatus = true;
		int liUpdateCount = HHSConstants.INT_ZERO;
        LOG_OBJECT.Debug("[Tracce 3]Entered into hhsauditInsert::aoExecuteAudit=" + aoExecuteAudit + 
        		"      \n***aoAudit " + aoAudit);

		if (aoExecuteAudit)
		{
			if (aoAudit.getAuditTableIdentifier() != null
					&& aoAudit.getAuditTableIdentifier().equalsIgnoreCase(HHSConstants.PROVIDER_AUDIT))
			{
				DAOUtil.masterDAO(aoMyBatisSession, aoAudit, HHSConstants.MAPPER_CLASS_HHS_AUDIT_MAPPER,
						HHSConstants.HHSAUDIT_PROVIDER_INSERT, HHSConstants.HHS_AUDIT_BEAN_PATH);
				return lbStatus;
			}
			else if (aoAudit.getAuditTableIdentifier() != null
					&& aoAudit.getAuditTableIdentifier().equalsIgnoreCase(HHSConstants.AGENCY_AUDIT))
			{
				DAOUtil.masterDAO(aoMyBatisSession, aoAudit, HHSConstants.MAPPER_CLASS_HHS_AUDIT_MAPPER,
						HHSConstants.HHSAUDIT_AGENCY_INSERT, HHSConstants.HHS_AUDIT_BEAN_PATH);
				return lbStatus;
			}
			else if (aoAudit.getAuditTableIdentifier() != null
					&& aoAudit.getAuditTableIdentifier().equalsIgnoreCase(HHSConstants.ACCELERATOR_AUDIT))
			{
				DAOUtil.masterDAO(aoMyBatisSession, aoAudit, HHSConstants.MAPPER_CLASS_HHS_AUDIT_MAPPER,
						HHSConstants.HHSAUDIT_ACCELERATOR_INSERT, HHSConstants.HHS_AUDIT_BEAN_PATH);
				return lbStatus;
			}
			else if (aoAudit.getAuditTableIdentifier() != null
					&& aoAudit.getAuditTableIdentifier().equalsIgnoreCase(HHSConstants.NON_AUDIT_COMMENTS))
			{
				if (null != aoAudit.getIsTaskScreen() && (!aoAudit.getIsTaskScreen()))
				{
					liUpdateCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoAudit,
							HHSConstants.MAPPER_CLASS_HHS_AUDIT_MAPPER,
							HHSConstants.UPDATE_COMMENT_NON_AUDIT_FOR_PROVIDER, HHSConstants.HHS_AUDIT_BEAN_PATH);
				}
				else
				{
					liUpdateCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoAudit,
							HHSConstants.MAPPER_CLASS_HHS_AUDIT_MAPPER, HHSConstants.UPDATE_COMMENT_NON_AUDIT,
							HHSConstants.HHS_AUDIT_BEAN_PATH);
				}
				if (liUpdateCount < HHSConstants.INT_ONE)
				{
					DAOUtil.masterDAO(aoMyBatisSession, aoAudit, HHSConstants.MAPPER_CLASS_HHS_AUDIT_MAPPER,
							HHSConstants.SAVE_COMMENT_NON_AUDIT, HHSConstants.HHS_AUDIT_BEAN_PATH);
				}
				return lbStatus;
			}
		}
		
		lbStatus = false;
		return lbStatus;
	}

	/**
	 * This Method Inserts the Data using HhsAuditBean bean in the Audit's
	 * table. 
	 * query used: hhsauditAcceleratorInsert
	 * This method insert entry for accelerator user.
	 * @param aoMyBatisSession
	 * @param aoAudit
	 * @param budgetList
	 * @param aoExecuteAudit
	 * @return
	 * @throws ApplicationException
	 */
	//Added for enhancement 6000 in Release 3.8.0
	public Boolean hhsauditInsert(SqlSession aoMyBatisSession, HhsAuditBean aoAudit, List<String> budgetList,
			Boolean aoExecuteAudit) throws ApplicationException
	{
		boolean lbStatus = true;
        LOG_OBJECT.Debug("[Tracce 3]Entered into hhsauditInsert::aoExecuteAudit=" + aoExecuteAudit + 
        		"      \n***aoAudit " + aoAudit);

		if (aoExecuteAudit)
		{
			if (aoAudit.getAuditTableIdentifier() != null
					&& aoAudit.getAuditTableIdentifier().equalsIgnoreCase(HHSConstants.ACCELERATOR_AUDIT)  )
			{
				/***[Start] R8.8.0 QC9534 **/
					StringBuilder loBudgets = new StringBuilder();
					if(null != budgetList && !budgetList.isEmpty()){
						for (String budget : budgetList){
							loBudgets.append("- " + budget);
						}
						if(null != loBudgets && !loBudgets.equals(HHSConstants.EMPTY_STRING)){
							aoAudit.setData(aoAudit.getData() + " with budgets " + loBudgets);
						}
					}
					DAOUtil.masterDAO(aoMyBatisSession, aoAudit, HHSConstants.MAPPER_CLASS_HHS_AUDIT_MAPPER,
							HHSConstants.HHSAUDIT_ACCELERATOR_INSERT, HHSConstants.HHS_AUDIT_BEAN_PATH);
					return lbStatus;
					/***[End] R8.8.0 QC9534**/
			}
		}
		lbStatus = false;
		return lbStatus;
	}

	/**
	 * This method updates audit table with multiple audit bean objects
	 * 
	 * <ul>
	 * <li>Get the input audit bean list and iterate through it</li>
	 * <li>Call method hhsauditInsert() with audit bean object</li>
	 * <li>Return insert status to controller</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession - Sql Session
	 * @param aoAuditList a list of audit bean object
	 * @param aoExecuteAudit a boolean value indicating whether to execute audit
	 *            query or not
	 * @return insert status
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Boolean hhsMultiAuditInsert(SqlSession aoMyBatisSession, List<HhsAuditBean> aoAuditList,
			Boolean aoExecuteAudit) throws ApplicationException
	{
		Boolean loInsertStatus = false;
		LOG_OBJECT.Debug("Entered into hhsMultiAuditInsert");
		try
		{
			if (null != aoAuditList && aoAuditList.size() > HHSConstants.INT_ZERO)
			{
				for (HhsAuditBean loHhsAuditBean : aoAuditList)
				{
					loInsertStatus = hhsauditInsert(aoMyBatisSession, loHhsAuditBean, aoExecuteAudit);
				}
				setMoState("Audit table updated successfully with multiple audit bean objects");
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
			aoAppEx.addContextData("BudgetId passed: ", aoAuditList);
			LOG_OBJECT.Error("Error occurred while updating Audit table with multiple audit bean objects", aoAppEx);
			setMoState("Error occurred while updating Audit table with multiple audit bean objects");
			throw aoAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occurred while updating Audit table with multiple audit bean objects", aoEx);
			setMoState("Error occurred while updating Audit table with multiple audit bean objects");
			throw new ApplicationException(
					"Error occurred while updating Audit table with multiple audit bean objects", aoEx);
		}
		LOG_OBJECT.Debug("Exited hhsMultiAuditInsert");
		return loInsertStatus;
	}

	/**
	 * This method delete comment entry from User_comment table
	 * 
	 * <ul>
	 * <li>Get the required properties from bean object</li>
	 * <li>Call transaction 'deleteFromUserComment'</li>
	 * <li>Return delete status to controller</li>
	 * <li>This query used: deleteFromUserComment</li>
	 * </ul>
	 * @param aoMyBatisSession sql session
	 * @param aoHhsAuditBean audit bean object
	 * @param aoExecuteAudit a boolean value indicating whether to execute audit
	 *            query or not
	 * @return lbStatus
	 * @throws ApplicationException if any exception occurs
	 */
	public Boolean deleteFromUserComment(SqlSession aoMyBatisSession, HhsAuditBean aoHhsAuditBean,
			Boolean aoExecuteAudit) throws ApplicationException
	{
		boolean lbStatus = true;
		try
		{
			if (aoExecuteAudit)
			{

				DAOUtil.masterDAO(aoMyBatisSession, aoHhsAuditBean, HHSConstants.MAPPER_CLASS_HHS_AUDIT_MAPPER,
						HHSConstants.DELETE_FROM_USER_COMMENT, HHSConstants.HHS_AUDIT_BEAN_PATH);
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
			LOG_OBJECT.Error("Error occurred whiledeleting user comment", aoAppEx);
			setMoState("Error occurred whiledeleting user comment");
			throw aoAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occurred whiledeleting user comment", aoEx);
			setMoState("EError occurred whiledeleting user comment");
			throw new ApplicationException("Error occurred whiledeleting user comment", aoEx);
		}
		return lbStatus;
	}

	/**
	 * This method delete comment entry from User_comment table
	 * 
	 * <ul>
	 * <li>Get the required properties from bean object</li>
	 * <li>Call transaction 'deleteFromUserComment'</li>
	 * <li>Return delete status to controller</li>
	 * <li>This query used: copyAgencyTaskCommentHistory</li>
	 * </ul>
	 * @param aoMyBatisSession sql session
	 * @param aoHMReqdProp hashmap object
	 * @param aoExecuteAudit a boolean value indicating whether to execute audit
	 *            query or not
	 * @return lbStatus
	 * @throws ApplicationException if any exception occurs
	 */
	@SuppressWarnings("rawtypes")
	public Boolean copyCommentHistory(SqlSession aoMyBatisSession, HashMap aoHMReqdProp, Boolean aoExecuteAudit)
			throws ApplicationException
	{
		boolean lbStatus = true;
		try
		{
			if (aoExecuteAudit)
			{

				DAOUtil.masterDAO(aoMyBatisSession, aoHMReqdProp, HHSConstants.MAPPER_CLASS_HHS_AUDIT_MAPPER,
						HHSConstants.COPY_AGENCY_TASK_COMMENT_HISTORY, HHSConstants.JAVA_UTIL_HASH_MAP);

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
			LOG_OBJECT.Error("Error occurred while copying comment history", aoAppEx);
			setMoState("Error occurred while copying comment history");
			throw aoAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occurred while copying comment history", aoEx);
			setMoState("Error occurred while copying comment history");
			throw new ApplicationException("Error occurred while copying comment history", aoEx);
		}
		return lbStatus;
	}

	/**
	 * This method modified as a part of release 3.1.0 for enhancement request
	 * 6024 This Method Inserts the Data using HhsAuditBean bean in the Release
	 * Addendum Audit's table. <li>The query used: releaseAddendumAuditInsert</li>
	 * @param aoMyBatisSession : passes the MyBatis SQL Session
	 * @param aoAudit : HhsAuditBean containing the required Info for
	 *            transaction
	 * @param aoExecuteAudit : aoExecuteAudit as input.
	 * @return lbStatus : Boolean : returns the insert status as TRUE if
	 *         transaction is successful or FALSE in case no matching branch is
	 *         found in the method
	 * @throws ApplicationException ApplicationException thrown in case any
	 *             query fails.
	 */
	public Boolean releaseAddendumAudit(SqlSession aoMyBatisSession, HhsAuditBean aoAudit, Boolean aoExecuteAudit)
			throws ApplicationException
	{
		boolean lbStatus = Boolean.FALSE;
		try
		{
			if (aoExecuteAudit)
			{
				DAOUtil.masterDAO(aoMyBatisSession, aoAudit, HHSConstants.MAPPER_CLASS_HHS_AUDIT_MAPPER,
						HHSConstants.RELEASE_ADDENDUM_AUDIT_INSERT, HHSConstants.HHS_AUDIT_BEAN_PATH);
				lbStatus = Boolean.TRUE;
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
			LOG_OBJECT.Error("Error occurred while inserting Addendum Audit", aoAppEx);
			setMoState("Error occurred while inserting Addendum Audit");
			throw aoAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occurred while inserting Addendum Audit", aoEx);
			setMoState("Error occurred while inserting Addendum Audit");
			throw new ApplicationException("Error occurred while inserting Addendum Audit", aoEx);
		}
		return lbStatus;
	}
	
	/**
	 * This method will insert the entry into Audit table 
	 * when contract is cancelled or updated.
	 * query: hhsauditAgencyInsert
	 * 
	 * @param aoMyBatisSession
	 * @param aoAudit
	 * @param budgetList
	 * @param aoExecuteAudit
	 * @return
	 * @throws ApplicationException
	 */
	//Added for enhancement 6483 in Release 3.8.0
	public Boolean hhsauditInsertForCancleContractUpdate(SqlSession aoMyBatisSession, HhsAuditBean aoAudit, List<String> budgetList,
			Boolean aoExecuteAudit) throws ApplicationException
	{
		boolean lbStatus = true;
		if (aoExecuteAudit)
		{
			if (aoAudit.getAuditTableIdentifier() != null
					&& aoAudit.getAuditTableIdentifier().equalsIgnoreCase(HHSConstants.AGENCY_AUDIT))
			{
				
				if(null != budgetList && !budgetList.isEmpty()){
					StringBuilder loBudgets = new StringBuilder();
					for (String budget : budgetList){
						loBudgets.append("- " + budget);
						
					}
					aoAudit.setData(aoAudit.getData() + " with budgets " + loBudgets);
				}
					else{
					aoAudit.setData(aoAudit.getData());	
					}
				}
			DAOUtil.masterDAO(aoMyBatisSession, aoAudit, HHSConstants.MAPPER_CLASS_HHS_AUDIT_MAPPER,
					HHSConstants.HHSAUDIT_AGENCY_INSERT, HHSConstants.HHS_AUDIT_BEAN_PATH);
			return lbStatus;
		}
		lbStatus = false;
		return lbStatus;
	}

}
