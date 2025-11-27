package com.nyc.hhs.preprocessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.HHSUtil;

/**
 * @author ritu.raaj Added in Release 7 The preprocessor class will check if the
 *         Update configuration satisfies auto approval condition or not. It
 *         will also assign the contract budget update review task to auto
 *         approver user if it satisfies the condition.
 * 
 */
public class PreprocessorUpdateApproval implements PreprocessorApproval
{

	private static final LogInfo LOG_OBJECT = new LogInfo(PreprocessorModificationApproval.class);

	/*
	 * The method is added in Release 7. It will assign the contract budget
	 * review task to auto approver user if satisfies the condition. In case, it
	 * does not, it will assigned to default user or unassigned.
	 * 
	 * @param aoUserSession
	 * 
	 * @param asBudgetId
	 * 
	 * @param asWobnumber
	 * 
	 * @param aoTaskBean
	 * 
	 * @param asTaskType
	 * 
	 * @return loReturnData
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Override
	public String[] isAutoApprovalApplicable(P8UserSession aoUserSession, String asBudgetId, String asWobnumber,
			TaskDetailsBean aoTaskBean, String asTaskType) throws ApplicationException
	{
		LOG_OBJECT.Info("PreprocessorUpdateApproval:::isAutoApprovalApplicable.taskBean:::" + aoTaskBean);
		LOG_OBJECT.Info("PreprocessorUpdateApproval:::isAutoApprovalApplicable.taskType:::" + asTaskType);
		LOG_OBJECT.Info("PreprocessorUpdateApproval:::isAutoApprovalApplicable.budgetId :::" + asBudgetId);
		LOG_OBJECT.Info("PreprocessorUpdateApproval:::isAutoApprovalApplicable.wobnum :::" + asWobnumber);
		HashMap<String, Object> loHmWorkItemProps = new HashMap<String, Object>();
		Channel loChannelForModification = new Channel();
		Boolean lbIsAutoApproved = false;
		String[] loReturnData = null;
		try
		{
			loHmWorkItemProps.put(HHSConstants.BUDGET_ID_WORKFLOW, asBudgetId);
			loChannelForModification.setData(HHSConstants.AO_HASH_MAP, loHmWorkItemProps);
			loChannelForModification.setData(HHSConstants.BUDGET_ID_KEY, asBudgetId);
			TransactionManager.executeTransaction(loChannelForModification, HHSR5Constants.CHECK_AUTO_APPROVAL_FOR_ASSIGNMENT);
			lbIsAutoApproved = (Boolean) loChannelForModification.getData(HHSR5Constants.FLAG_IS_AUTO_APPROVED);
			HashMap<String, Object> loResultantMap = (HashMap<String, Object>) loChannelForModification
					.getData(HHSConstants.AO_HASH_MAP);
			String lsAgencyId = (String) loResultantMap.get(HHSConstants.AGENCY_ID_TABLE_COLUMN);
			LOG_OBJECT.Info("Auto Approval Status :::" + lbIsAutoApproved);
			if (lbIsAutoApproved)
			{
				LOG_OBJECT.Info("Inside Auto Approval");
				Channel loChannelForAgencyUser = new Channel();
				loChannelForAgencyUser.setData(HHSR5Constants.AGENCY_ID_STRING, lsAgencyId);
				TransactionManager
						.executeTransaction(loChannelForAgencyUser, HHSR5Constants.GET_AUTO_APPROVAL_USERNAME);
				String lsUserNameForAutoApprover = (String) loChannelForAgencyUser
						.getData(HHSR5Constants.USER_NAME_APPROVER);
				LOG_OBJECT.Info("User who is going to approve the modification" + lsUserNameForAutoApprover);
				loReturnData = new String[2];
				loReturnData[0] = lsUserNameForAutoApprover;
				loReturnData[1] = lsUserNameForAutoApprover;
				if(null == aoTaskBean){
					aoTaskBean = new TaskDetailsBean();
				}
				Channel loChannelForAudit = new Channel();
				aoTaskBean.setUserId(HHSR5Constants.AUTO_APPROVER_ID);
				aoTaskBean.setReassignUserId(HHSR5Constants.AUTO_APPROVER_ID);
				aoTaskBean.setEntityType(asTaskType);
				aoTaskBean.setWorkFlowId(asWobnumber);
				loChannelForAudit.setData(HHSConstants.AO_TASK_DETAILS_BEAN, aoTaskBean);
				loChannelForAudit.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
				TransactionManager.executeTransaction(loChannelForAudit, HHSR5Constants.SET_AUDIT_AUTO_APPROVAL);
			}
			else
			{
				if (null == aoTaskBean)
				{
					LOG_OBJECT.Info("In  null taskBean");
					loReturnData = new String[1];
					loReturnData[0] = "";
				}
				else if (StringUtils.isNotBlank(aoTaskBean.getReassignUserId()))
				{
					LOG_OBJECT.Info("In not null taskBean");
					loReturnData = new String[2];
					loReturnData[0] = aoTaskBean.getReassignUserId();
					loReturnData[1] = aoTaskBean.getReassignUserName();
					List<HhsAuditBean> loAuditBeanList = new ArrayList<HhsAuditBean>();
					loAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSR5Constants.TASK_ASSIGNMENT, asTaskType,
							HHSConstants.TASK_ASSIGNED_TO + HHSR5Constants.COLON_AOP + loReturnData[1], asTaskType,
							asBudgetId, HHSConstants.SYSTEM_USER, HHSR5Constants.AGENCY_AUDIT));
					Channel loChannelObj = new Channel();
					loChannelObj.setData(ApplicationConstants.AUDIT_BEAN_LIST, loAuditBeanList);
					loChannelObj.setData(HHSConstants.UPDATE_FLAG, HHSConstants.BOOLEAN_TRUE);
					LOG_OBJECT.Debug("Creating AutoAssign Audit:" + loAuditBeanList.toString());
					TransactionManager.executeTransaction(loChannelObj, "insertPsrTaskAudit");
				}
			}
		}
		catch (ApplicationException aoAppEx)
		{
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Unable to get assignment details" + aoAppEx);
			throw new ApplicationException("Error occured while fetching default assignment details", aoAppEx);
		}
		LOG_OBJECT.Debug("Exit:: HHSComponentOperations.setAssigneeDetails");
		return loReturnData;
	}

	/**
	 * The method is added in Release 7. It will check if the contract update
	 * configuration task falls under auto approval condition or not.
	 * @param aoUserSession
	 * @param aoMybatisSession
	 * @param aoTaskBean
	 * @param asUpdateCOntractId
	 * @return lbAutoUpdate
	 * @throws ApplicationException
	 */
	public boolean checkAutoApprovalForUpdateBudgetTask(P8UserSession aoUserSession, SqlSession aoMybatisSession,
			TaskDetailsBean aoTaskBean, String asUpdateCOntractId) throws ApplicationException
	{

		LOG_OBJECT.Debug("HHSComponentOperations.checkAutoApprovalForUpdateBudgetTask:::: TaskBean" + aoTaskBean);
		LOG_OBJECT.Debug("HHSComponentOperations.checkAutoApprovalForUpdateBudgetTask:::: Update COntract ID"
				+ asUpdateCOntractId);
		boolean lbAutoUpdate = false;
		HashMap<String, Object> loHmRequiredProps = new HashMap<String, Object>();
		HashMap<String, Object> loHashMapForBudgetUpdate = new HashMap<String, Object>();
		Channel loChannel = new Channel();
		HashMap<String, Object> loHmRequiredPropsForUpdate = new HashMap<String, Object>();
		try
		{
			List<String> loActiveFiscalYearList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, asUpdateCOntractId,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, "getActiveFiscalYear",
					HHSConstants.JAVA_LANG_STRING);
			LOG_OBJECT.Debug("Active Fiscal Year List:::" + loActiveFiscalYearList);
			Iterator loItr = loActiveFiscalYearList.iterator();
			while (loItr.hasNext())
			{
				String lsFiscalYearId = (String) loItr.next();
				LOG_OBJECT.Debug("Active Fiscal Year :::" + lsFiscalYearId);
				loHmRequiredPropsForUpdate.put(HHSConstants.CONTRACT_ID_WORKFLOW, asUpdateCOntractId);
				loHmRequiredPropsForUpdate.put(HHSConstants.FISCAL_YEAR, lsFiscalYearId);
				String lsBudgetId = (String) DAOUtil.masterDAO(aoMybatisSession, loHmRequiredPropsForUpdate,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSR5Constants.FETCH_UPDATE_BUDGET_ID, HHSConstants.JAVA_UTIL_HASH_MAP);
				LOG_OBJECT.Debug("Budget Id :::" + lsBudgetId);
				loHmRequiredPropsForUpdate.put(HHSConstants.BUDGET_ID_WORKFLOW, lsBudgetId);
				LOG_OBJECT.Debug("HashMap Used :::" + loHmRequiredPropsForUpdate);
				Integer liCategoryAdded = (Integer) DAOUtil.masterDAO(aoMybatisSession, loHmRequiredPropsForUpdate,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, HHSR5Constants.FETCH_COUNT_CCU,
						HHSConstants.JAVA_UTIL_HASH_MAP);
				LOG_OBJECT.Debug("Category Added in Update:: " + liCategoryAdded);
				Integer liFiscalYearAmountChange = (Integer) DAOUtil.masterDAO(aoMybatisSession, asUpdateCOntractId,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSR5Constants.FETCH_COUNT_CCU_FISCAL_AMOUNT, HHSConstants.JAVA_LANG_STRING);
				LOG_OBJECT.Debug("Amount change in current fiscal year:: " + liFiscalYearAmountChange);
				if ((null != liCategoryAdded && liCategoryAdded > 0)
						|| (null != liFiscalYearAmountChange && liFiscalYearAmountChange > 0))
				{
					LOG_OBJECT.Debug("Manual Approval, Do nothing");
				}
				else
				{
					LOG_OBJECT.Debug("Auto Approval state");
					loHmRequiredProps.put(HHSConstants.CONTRACT_ID_WORKFLOW, asUpdateCOntractId);
					loHmRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW, lsBudgetId);
					loHmRequiredProps.put(HHSConstants.PROPERTY_PE_CT, null);
					loHmRequiredProps.put(HHSConstants.SUBMITTED_BY, HHSConstants.SYSTEM_USER);
					loHmRequiredProps.put(HHSConstants.MOD_BY_USER_ID, HHSConstants.EMPTY_STRING);
					loHmRequiredProps.put(HHSConstants.AUDIT_PROVIDER_INITIATED_TASK, true);
					loHmRequiredProps.put(HHSConstants.WORKFLOW_NAME, HHSConstants.WF_CONTRACT_BUDGET_UPDATE_REVIEW);
					setAuditDataInChannel(loChannel, lsBudgetId, HHSConstants.SYSTEM_USER,
							HHSConstants.STATUS_CHANGED_FROM, HHSConstants.AUDIT_CONTRACT_BUDGET_UPDATE);
					loHmRequiredProps.put(HHSConstants.AS_STATUS_ID, HHSConstants.BUDGET_APPROVED_STATUS_ID);
					loChannel.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
					loChannel.setData(HHSConstants.LB_AUTH_STATUS_FLAG, true);
					loChannel.setData(HHSConstants.AO_HMWF_REQUIRED_PROPS, loHmRequiredProps);
					loChannel.setData(HHSConstants.CHANNEL_PARAM_TAB_LEVEL_COMMENTS_MAP_PROVIDER, null);
					loChannel.setData(ApplicationConstants.FILENET_SESSION, aoUserSession);
					CommonUtil.setChannelForAutoSaveData(loChannel, lsBudgetId,
							HHSConstants.AUDIT_CONTRACT_BUDGET_UPDATE);
					
					// Start QC 9585 R 9.3.0  remove password from logs
					String param = CommonUtil.maskPassword(loChannel);
					//LOG_OBJECT.Info("Channel Data::" + loChannel);
					LOG_OBJECT.Info("Channel Data::" + param);
					// End QC 9585 R 9.3.0  remove password from logs
	
					TransactionManager.executeTransaction(loChannel, HHSR5Constants.LAUCH_WFUPDATE_AUTO);
					loHashMapForBudgetUpdate.put(HHSR5Constants.IS_ELIGIBLE_AUTO_APPROVAL, true);
					loHashMapForBudgetUpdate.put(HHSConstants.BUDGET_ID_WORKFLOW, lsBudgetId);
					LOG_OBJECT.Debug("Update budget as auto approval flag true");
					DAOUtil.masterDAO(aoMybatisSession, loHashMapForBudgetUpdate,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
							HHSR5Constants.UPDATE_APPROVAL_DETAILS_IN_BUDGET, HHSConstants.JAVA_UTIL_HASH_MAP);
				}
			}
		}
		catch (ApplicationException aoAppEx)
		{
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Unable to get assignment details" + aoAppEx);
			throw new ApplicationException("Error occured while fetching default assignment details", aoAppEx);
		}
		return lbAutoUpdate;
	}

	/**
	 * The method is added in Release 7 for auditing in case of auto approval of update.
	 * @param aoChannel
	 * @param asBudgetId
	 * @param asUserId
	 * @param asStatusChange
	 * @param asEntityType
	 * @throws ApplicationException
	 */
	private void setAuditDataInChannel(Channel aoChannel, String asBudgetId, String asUserId, String asStatusChange,
			String asEntityType) throws ApplicationException
	{
		List<HhsAuditBean> loAuditList = new ArrayList<HhsAuditBean>();
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		StringBuilder loStatusChange = new StringBuilder();
		loStatusChange.append(asStatusChange);
		loStatusChange.append(HHSConstants.SPACE);
		loStatusChange.append(HHSConstants.STR);
		loStatusChange.append(HHSConstants.STATUS_PENDING_SUBMISSION);
		loStatusChange.append(HHSConstants.STR);
		loStatusChange.append(HHSConstants._TO_);
		loStatusChange.append(HHSConstants.STR);
		loStatusChange.append(HHSConstants.STATUS_PENDING_APPROVAL);
		loStatusChange.append(HHSConstants.STR);
		loAuditList.add(HHSUtil.addAuditDataToChannel(HHSConstants.STATUS_CHANGE, HHSConstants.STATUS_CHANGE,
				loStatusChange.toString(), asEntityType, asBudgetId, asUserId, HHSConstants.PROVIDER_AUDIT));
		loHhsAuditBean.setEntityId(asBudgetId);
		loHhsAuditBean.setEntityType(asEntityType);
		aoChannel.setData(HHSConstants.LO_AUDIT_LIST, loAuditList);
		aoChannel.setData(HHSConstants.AUDIT_BEAN, loHhsAuditBean);
		LOG_OBJECT.Debug("Audit data List:::" + loAuditList);
		LOG_OBJECT.Debug("Audit Bean:::" + loHhsAuditBean);
	}
}
