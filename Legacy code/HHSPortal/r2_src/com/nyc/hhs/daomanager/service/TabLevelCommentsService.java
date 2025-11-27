package com.nyc.hhs.daomanager.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.CommentsHistoryBean;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.HHSUtil;

/**
 * TabLevelCommentsService: Class used to hit different database services such
 * as fetch, insert, update Tab level comments. This also captures services to
 * highlight and remove highlight of a tab.
 * <ul>
 * <li>Service Class Added For R4</li>
 * </ul>
 * 
 */

public class TabLevelCommentsService extends ServiceState
{

	/**
	 * This is a log object used to log any exception into log file.
	 */

	private static final LogInfo LOG_OBJECT = new LogInfo(TabLevelCommentsService.class);

	/**
	 * This method fetches the Tab Level History From the Agency_audit Table by
	 * providing the required where clause fields.
	 * 
	 * <ul>
	 * <li>Get the workflow Id from input map</li>
	 * <li>Execute query with Id "fetchAgencyTaskHistoryTabLevel" from task
	 * service mapper</li>
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
	public List<CommentsHistoryBean> fetchAgencyTaskHistoryTabLevel(HashMap aoHMApplicationAudit,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		List<CommentsHistoryBean> loResultList = null;
		LOG_OBJECT.Debug("Entered into fetchAgencyTaskHistoryTabLevel");
		try
		{
			loResultList = (List<CommentsHistoryBean>) DAOUtil.masterDAO(aoMybatisSession, aoHMApplicationAudit,
					HHSConstants.MAPPER_CLASS_TASK_SERVICE_MAPPER, HHSConstants.FETCH_AGENCY_TASK_HISTORY_TAB_LEVEL,
					HHSConstants.JAVA_UTIL_HASH_MAP);
		}
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
	 * This method fetches the Tab Level History (Provider) From the
	 * Agency_audit and provider_audit Table by providing the required where
	 * clause fields. Created for R4
	 * @param aoHMApplicationAudit - HashMap Of parameters
	 * @param aoMybatisSession - SQL Session
	 * @return - List Of CommentsHistoryBean bean.
	 * @throws ApplicationException when any error condition occurred
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public List<CommentsHistoryBean> fetchProviderTaskHistoryTabLevel(HashMap aoHMApplicationAudit,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		List<CommentsHistoryBean> loResultList = null;
		LOG_OBJECT.Debug("Entered into fetchProviderTaskHistoryTabLevel");
		try
		{
			loResultList = (List<CommentsHistoryBean>) DAOUtil.masterDAO(aoMybatisSession, aoHMApplicationAudit,
					HHSConstants.MAPPER_CLASS_TASK_SERVICE_MAPPER, HHSConstants.FETCH_PROVIDER_TASK_HISTORY_TAB_LEVEL,
					HHSConstants.JAVA_UTIL_HASH_MAP);
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting Agency Task History Tab Level Details");
			loExp.setContextData(aoHMApplicationAudit);
			LOG_OBJECT.Error("Error while getting Agency Task History Tab Level Details :", loExp);
			throw loExp;
		}
		LOG_OBJECT.Debug("Exited fetchProviderTaskHistoryTabLevel");
		return loResultList;
	}

	/**
	 * This method fetches the list of tab for a sub-buddget corresponding to
	 * which Agency Comments exists. Created for R4
	 * @param aoMybatisSession - SQL Session
	 * @param aoCBGridBean - CBGridBEan
	 * @return - List of Tabs (Integer Values) that needs to be highlighted
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> fetchTabsToHighlihtProviderFromAgencyAudit(SqlSession aoMybatisSession, CBGridBean aoCBGridBean)
			throws ApplicationException
	{
		HashMap<String, String> loParamMap = new HashMap<String, String>();
		loParamMap.put("contractId", aoCBGridBean.getContractID());
		loParamMap.put("subBudgetId", aoCBGridBean.getSubBudgetID());
		loParamMap.put("budgetId", aoCBGridBean.getContractBudgetID());
		loParamMap.put("invoiceId", aoCBGridBean.getInvoiceId());
		loParamMap.put("entityType", "");
		List<String> loAuditResultList = null;
		loAuditResultList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, loParamMap,
				HHSConstants.MAPPER_CLASS_TASK_SERVICE_MAPPER,
				HHSConstants.FETCH_PROVIDER_TAB_HIGHLIGHT_FROM_AGRNCY_AUDIT, HHSConstants.JAVA_UTIL_HASH_MAP);
		return HHSUtil.generateLineItemTabsToHighlightMapProvider(loAuditResultList);
	}

	/**
	 * This method updates/inserts and deletes entries made to highlight tab on
	 * insertion of Comments in Tab Level COmeents box Created for R4
	 * @param aoMybatisSession - SQL Session
	 * @param aoTaskDetailsBean - Task Details Bean
	 * @param aoAudit - HHSAudit Bean
	 * @param aoExecuteAudit - Boolean Flag
	 * @return - Status (0/1) Successful Insertion/Update
	 * @throws ApplicationException
	 */
	public int highlightTabInsertUpdate(SqlSession aoMybatisSession, TaskDetailsBean aoTaskDetailsBean,
			HhsAuditBean aoAudit, Boolean aoExecuteAudit) throws ApplicationException
	{
		Integer liInsertUpdateCount = 0;
		try
		{
			HhsAuditService loHhsAuditService = new HhsAuditService();
			if ((null != aoTaskDetailsBean.getProviderComment() && !aoTaskDetailsBean.getProviderComment().isEmpty()))
			{
				liInsertUpdateCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoTaskDetailsBean,
						HHSConstants.MAPPER_CLASS_TASK_SERVICE_MAPPER, HHSConstants.HIGHLIGHT_TAB_UPDATE,
						HHSConstants.CS_TASK_DETAILS_BEAN);
				if (!(liInsertUpdateCount > 0))
				{
					liInsertUpdateCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoTaskDetailsBean,
							HHSConstants.MAPPER_CLASS_TASK_SERVICE_MAPPER, HHSConstants.HIGHLIGHT_TAB_INSERT,
							HHSConstants.CS_TASK_DETAILS_BEAN);
				}
				loHhsAuditService.hhsauditInsert(aoMybatisSession, aoAudit, aoExecuteAudit);
			}
			else if ((null != aoTaskDetailsBean.getInternalComment() && !aoTaskDetailsBean.getInternalComment()
					.isEmpty()))
			{
				loHhsAuditService.hhsauditInsert(aoMybatisSession, aoAudit, aoExecuteAudit);
			}
			else
			{
				liInsertUpdateCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoTaskDetailsBean,
						HHSConstants.MAPPER_CLASS_TASK_SERVICE_MAPPER, HHSConstants.HIGHLIGHT_TAB_DELETE,
						HHSConstants.CS_TASK_DETAILS_BEAN);
				loHhsAuditService.deleteFromUserComment(aoMybatisSession, aoAudit, aoExecuteAudit);
			}
		}
		catch (Exception loExp)
		{
			setMoState("Error while inserting for highlightTabInsertUpdate");
			LOG_OBJECT.Error("Error while inserting for highlightTabInsertUpdate :", loExp);
			throw new ApplicationException("Error while inserting for highlightTabInsertUpdate :", loExp);
		}
		return liInsertUpdateCount;
	}

	/**
	 * This method fetches Tab Level Comments - Provider from User Comments
	 * table and creates the Multi Insert Audit List Created for R4 <li>This
	 * query used: fetchUserCommentsForTabLevelAuditProvider</li> <li>This query
	 * used: deleteFromUserCommentForTabLevelProvider</li> <li>This query used:
	 * deleteTabHighlightTabLevelFromAgencyAuditOnSubmit</li>
	 * @param aoMyBatisSession SQL Session
	 * @param aoAuditList Audit List
	 * @param aoHhsAuditBean HHSAudiBean
	 * @param aoTabLevelCommentsMap Tab Level Comments Map
	 * @param aoHMWFRequiredProps Workflow properties Hashmap
	 * @return List of HHSAudit Bean for Insertion
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public List<HhsAuditBean> fetchUserCommentsForTabLevelAuditProvider(SqlSession aoMyBatisSession,
			List<HhsAuditBean> aoAuditList, HhsAuditBean aoHhsAuditBean, Map<String, String> aoTabLevelCommentsMap,
			HashMap aoHMWFRequiredProps) throws ApplicationException
	{
		Map<String, String> loUserCommentsMapDB = null;
		String asEntityType = aoHhsAuditBean.getEntityType();
		List<Map<String, String>> loUserCommentsMapDBList = (List<Map<String, String>>) DAOUtil.masterDAO(
				aoMyBatisSession, aoHhsAuditBean, HHSConstants.MAPPER_CLASS_HHS_AUDIT_MAPPER,
				HHSConstants.FETCH_FROM_USER_COMMENTS_PROVIDER, HHSConstants.HHS_AUDIT_BEAN_PATH);
		loUserCommentsMapDB = HHSUtil.populateProviderCommentsMapFromDB(loUserCommentsMapDBList);
		HHSUtil.generateFinalAuditListForTabLevelCommentsProvider(loUserCommentsMapDB, aoAuditList,
				aoHMWFRequiredProps, asEntityType);
		DAOUtil.masterDAO(aoMyBatisSession, aoHhsAuditBean, HHSConstants.MAPPER_CLASS_HHS_AUDIT_MAPPER,
				HHSConstants.DELETE_FROM_USER_COMMENTS_PROVIDER, HHSConstants.HHS_AUDIT_BEAN_PATH);
		DAOUtil.masterDAO(aoMyBatisSession, aoHMWFRequiredProps, HHSConstants.MAPPER_CLASS_HHS_AUDIT_MAPPER,
				HHSConstants.DELETE_FROM_AGENCY_AUDIT_TAB_HIGHLGHT_ENTRIES_ON_SUBMIT, HHSConstants.JAVA_UTIL_HASH_MAP);
		return aoAuditList;
	}

	/**
	 * This method fetches Tab Level Comments - Agency from User Comments table
	 * and creates the Multi Insert Audit List Created for R4 <li>This query
	 * used: fetchUserCommentsForTabLevelAuditAgency</li> <li>This query used:
	 * deleteFromUserCommentForTabLevelAgency</li> <li>This query used:
	 * highlightTabUpdateOnAgencyTaskFinish</li>
	 * @param aoMyBatisSession SQL Session
	 * @param aoAuditList Audit List
	 * @param aoTaskDetailsBean Task Details Bean
	 * @return List of HHSAudit Bean for Insertion
	 */
	@SuppressWarnings("unchecked")
	public List<HhsAuditBean> fetchUserCommentsForTabLevelAuditAgency(SqlSession aoMyBatisSession,
			List<HhsAuditBean> aoAuditList, TaskDetailsBean aoTaskDetailsBean) throws ApplicationException
	{
		// Add check in case of Reassign/Approve task to not change the comment
		// highlight flag to '1'
		List<Map<String, String>> loUserCommentsMapDBList = (List<Map<String, String>>) DAOUtil.masterDAO(
				aoMyBatisSession, aoTaskDetailsBean, HHSConstants.MAPPER_CLASS_HHS_AUDIT_MAPPER,
				HHSConstants.FETCH_FROM_USER_COMMENTS_AGENCY, HHSConstants.CS_TASK_DETAILS_BEAN);
		HHSUtil.populateAgencyCommentsAuditList(loUserCommentsMapDBList, aoAuditList, aoTaskDetailsBean);
		DAOUtil.masterDAO(aoMyBatisSession, aoTaskDetailsBean, HHSConstants.MAPPER_CLASS_HHS_AUDIT_MAPPER,
				HHSConstants.DELETE_FROM_USER_COMMENTS_AGENCY, HHSConstants.CS_TASK_DETAILS_BEAN);
		DAOUtil.masterDAO(aoMyBatisSession, aoTaskDetailsBean, HHSConstants.MAPPER_CLASS_TASK_SERVICE_MAPPER,
				HHSConstants.HIGHLIGHT_TAB_UPDATE_ON_AGENCY_TASK_FINISH, HHSConstants.CS_TASK_DETAILS_BEAN);
		return aoAuditList;
	}
}
