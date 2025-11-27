package com.nyc.hhs.daomanager.service;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.model.ApplicationAuditBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.util.DAOUtil;

/**
 * TaskHistoryService: Service class used to fetch the task history and comments
 *                     for city users
 * 
 */

public class TaskHistoryService extends ServiceState
{

	/**
	 * This method make entry in APPLICATION_AUDIT Table
	 * 
	 * @param aoApplicationAudit
	 *            ApplicationAuditBean
	 * @param aoMybatiSession
	 *            SQl Session
	 * @return boolean Updated Status
	 * @throws ApplicationException
	 */
	public boolean updateTaskHistory(ApplicationAuditBean aoApplicationAudit, SqlSession aoMybatiSession) throws ApplicationException
	{
		DAOUtil.masterDAO(aoMybatiSession, aoApplicationAudit, ApplicationConstants.MAPPER_CLASS_HISTORY_MAPPER, "updateTaskHistoryAudit",
				"com.nyc.hhs.model.ApplicationAuditBean");

		return true;
	}

	/**
	 * This method fetch all section entries from APPLICATION_AUDIT Table
	 * 
	 * @param aoHMApplicationAudit
	 *            HashMap of Required props
	 * @param aoMybatisSession
	 *            SQl Session
	 * @return loResultList List of ApplicationAuditBean
	 * @throws ApplicationException
	 */
	public List<ApplicationAuditBean> fetchTaskHistory(HashMap aoHMApplicationAudit, SqlSession aoMybatisSession) throws ApplicationException
	{

		List<ApplicationAuditBean> loResultList = null;
		if (aoHMApplicationAudit.containsKey("appid") && ((String) aoHMApplicationAudit.get("appid")) != null)
		{
			loResultList = (List<ApplicationAuditBean>) DAOUtil.masterDAO(aoMybatisSession, aoHMApplicationAudit,
					ApplicationConstants.MAPPER_CLASS_HISTORY_MAPPER, "fetchTaskHistoryAudit", "java.util.HashMap");
		}
		return loResultList;
	}
	/**
	 * This method fetch all section entries from APPLICATION_AUDIT Table
	 * 
	 * @param aoHMApplicationAudit
	 *            HashMap of Required props
	 * @param aoMybatisSession
	 *            SQl Session
	 * @return loResultList List of ApplicationAuditBean
	 * @throws ApplicationException
	 */
	public List<ApplicationAuditBean> fetchTaskHistoryWithdrawal(HashMap aoHMApplicationAudit, SqlSession aoMybatisSession) throws ApplicationException
	{

		List<ApplicationAuditBean> loResultList = null;
			loResultList = (List<ApplicationAuditBean>) DAOUtil.masterDAO(aoMybatisSession, aoHMApplicationAudit,
					ApplicationConstants.MAPPER_CLASS_HISTORY_MAPPER, "fetchTaskHistoryWithdrawalAudit", "java.util.HashMap");
		return loResultList;
	}

	/**
	 * This method fetch all Application level entries from APPLICATION_AUDIT
	 * Table
	 * 
	 * @param aoHMApplicationAudit
	 *            HashMap of Required props
	 * @param aoMybatisSession
	 *            SQl Session
	 * @return loResultList List of ApplicationAuditBean
	 * @throws ApplicationException
	 */
	public List<ApplicationAuditBean> fetchAllAppTaskHistoryAudit(HashMap aoHMApplicationAudit, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		List<ApplicationAuditBean> loResultList = null;
		loResultList = (List<ApplicationAuditBean>) DAOUtil.masterDAO(aoMybatisSession, aoHMApplicationAudit,
				ApplicationConstants.MAPPER_CLASS_HISTORY_MAPPER, "fetchAllAppTaskHistoryAudit", "java.util.HashMap");
		return loResultList;
	}

	/**
	 * This method fetch all provider comments entry from APPLICATION_AUDIT
	 * Table
	 * 
	 * @param aoHMApplicationAudit
	 *            HashMap of Required props
	 * @param aoMybatisSession
	 *            SQl Session
	 * @return loResultList List of ApplicationAuditBean
	 * @throws ApplicationException
	 */
	public List<ApplicationAuditBean> fetchLastProviderComments(HashMap aoHMApplicationAudit, SqlSession aoMybatisSession)
			throws ApplicationException
	{

		List<ApplicationAuditBean> loResultList = null;
		loResultList = (List<ApplicationAuditBean>) DAOUtil.masterDAO(aoMybatisSession, aoHMApplicationAudit,
				ApplicationConstants.MAPPER_CLASS_HISTORY_MAPPER, "fetchLastProviderComments", "java.util.HashMap");

		return loResultList;
	}
	/**
	 * This method fetch all provider comments entry from APPLICATION_AUDIT
	 * Table
	 * 
	 * @param aoHMApplicationAudit
	 *            HashMap of Required props
	 * @param aoMybatisSession
	 *            SQl Session
	 * @return loResultList List of ApplicationAuditBean
	 * @throws ApplicationException
	 */
	public List<ApplicationAuditBean> fetchLastProviderCommentsWithdrawal(HashMap aoHMApplicationAudit, SqlSession aoMybatisSession)
			throws ApplicationException
	{

		List<ApplicationAuditBean> loResultList = null;
		loResultList = (List<ApplicationAuditBean>) DAOUtil.masterDAO(aoMybatisSession, aoHMApplicationAudit,
				ApplicationConstants.MAPPER_CLASS_HISTORY_MAPPER, "fetchLastProviderCommentsWithdrawal", "java.util.HashMap");

		return loResultList;
	}
	/**
	 * This method fetch all Bapp comments entry from APPLICATION_AUDIT
	 * Table
	 * 
	 * @param aoHMApplicationAudit
	 *            HashMap of Required props
	 * @param aoMybatisSession
	 *            SQl Session
	 * @return loResultList List of ApplicationAuditBean
	 * @throws ApplicationException
	 */
	public List<ApplicationAuditBean> fetchBappComments(HashMap aoHMApplicationAudit, SqlSession aoMybatisSession)
			throws ApplicationException
	{

		List<ApplicationAuditBean> loResultList = null;
		loResultList = (List<ApplicationAuditBean>) DAOUtil.masterDAO(aoMybatisSession, aoHMApplicationAudit,
				ApplicationConstants.MAPPER_CLASS_HISTORY_MAPPER, "fetchBappComments", "java.util.HashMap");

		return loResultList;
	}

	
	
	/**
	 * This method fetch all task history entry from General_audit Table
	 * 
	 * @param aoHMApplicationAudit
	 *            HashMap of Required props
	 * @param aoMybatisSession
	 *            SQl Session
	 * @return loResultList List of ApplicationAuditBean
	 * @throws ApplicationException
	 */
	public List<ApplicationAuditBean> fetchTaskHistoryAuditGeneral(HashMap aoHMApplicationAudit, SqlSession aoMybatisSession)
			throws ApplicationException
	{

		List<ApplicationAuditBean> loResultList = null;
		if (aoHMApplicationAudit.containsKey("appid") && ((String) aoHMApplicationAudit.get("appid")) != null)
		{
			loResultList = (List<ApplicationAuditBean>) DAOUtil.masterDAO(aoMybatisSession, aoHMApplicationAudit,
					ApplicationConstants.MAPPER_CLASS_HISTORY_MAPPER, "fetchTaskHistoryAuditGeneral", "java.util.HashMap");
		}
		return loResultList;
	}

	/**
	 * This method fetch all provider comments entry from General_audit Table
	 * 
	 * @param aoHMApplicationAudit
	 *            HashMap of Required props
	 * @param aoMybatisSession
	 *            SQl Session
	 * @return loResultList List of ApplicationAuditBean
	 * @throws ApplicationException
	 */
	public List<ApplicationAuditBean> fetchLastProviderCommentsGeneral(HashMap aoHMApplicationAudit, SqlSession aoMybatisSession)
			throws ApplicationException
	{

		List<ApplicationAuditBean> loResultList = null;
		loResultList = (List<ApplicationAuditBean>) DAOUtil.masterDAO(aoMybatisSession, aoHMApplicationAudit,
				ApplicationConstants.MAPPER_CLASS_HISTORY_MAPPER, "fetchLastProviderCommentsGeneral", "java.util.HashMap");

		return loResultList;
	}

	/**
	 * This method fetches STATUS from SUPERSEDING_STATUS table
	 * 
	 * @param asSectionId
	 *            Section ID
	 * @param aoMybatisSession
	 *            SQl Session
	 * @return lsResult Updated Status
	 * @throws ApplicationException
	 */
	public String checkServiceTaskStatus(String asSectionId, SqlSession aoMybatisSession) throws ApplicationException
	{
		String lsResult = "";
		lsResult = (String) DAOUtil.masterDAO(aoMybatisSession, asSectionId, ApplicationConstants.MAPPER_CLASS_HISTORY_MAPPER,
				"checkServiceTaskStatus", "java.lang.String");
		return lsResult;
	}
	/**
	 * This Method Updates the Flag in the Application Audit Table 
	 * @param aoMybatiSession SQL Session
	 * @param aoProps  HashMap of Required props
	 * @return true
	 * @throws ApplicationException
	 */
	public Boolean updateAuditStatus(SqlSession aoMybatiSession, HashMap aoProps) throws ApplicationException
	{
		String lsTaskType=(String)aoProps.get("taskType");
		if(lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST)){
		DAOUtil.masterDAO(aoMybatiSession, aoProps, ApplicationConstants.MAPPER_CLASS_HISTORY_MAPPER,
				"updateAuditStatusWithdrawal", "java.util.HashMap");
		}
		else
		{
		DAOUtil.masterDAO(aoMybatiSession, aoProps, ApplicationConstants.MAPPER_CLASS_HISTORY_MAPPER,
					"updateAuditStatus", "java.util.HashMap");
		}

		return true;
	}
	
	/**
	 * This Method Updates the Flag in the Application Audit Table 
	 * @param aoMybatiSession SQL Session
	 * @param aoProps  HashMap of Required props
	 * @return true
	 * @throws ApplicationException
	 */
	public Boolean updateAuditforDoc(SqlSession aoMybatiSession, HashMap aoProps) throws ApplicationException
	{
		
		DAOUtil.masterDAO(aoMybatiSession, aoProps, ApplicationConstants.MAPPER_CLASS_HISTORY_MAPPER,
				"updateAuditforDoc", "java.util.HashMap");
	

		return true;
	}
}
