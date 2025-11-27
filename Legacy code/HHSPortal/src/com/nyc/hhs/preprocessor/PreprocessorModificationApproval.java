package com.nyc.hhs.preprocessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

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
import com.nyc.hhs.util.HHSUtil;

/**
 * @author ritu.raaj
 * The Pre processing class is added in release 7.
 * It will decide whether a modification submitted by provider falls under auto approval threshold.
 *
 */
public class PreprocessorModificationApproval implements PreprocessorApproval
{
	private static final LogInfo LOG_OBJECT = new LogInfo(PreprocessorModificationApproval.class);

	public String[] isAutoApprovalApplicable(P8UserSession aoUserSession, String asBudgetId, String asWobnumber,
			TaskDetailsBean aoTaskBean, String asTaskType) throws ApplicationException
	{
		//LOG_OBJECT.Info("PreprocessorModificationApproval:::isAutoApprovalApplicable.taskBean:::" + aoTaskBean);
		LOG_OBJECT.Info("PreprocessorModificationApproval:::isAutoApprovalApplicable.taskType:::" + asTaskType);
		LOG_OBJECT.Info("PreprocessorModificationApproval:::isAutoApprovalApplicable.budgetId :::" + asBudgetId);
		LOG_OBJECT.Info("PreprocessorModificationApproval:::isAutoApprovalApplicable.wobnum :::" + asWobnumber);
		HashMap<String, Object> loHmWorkItemProps = new HashMap<String, Object>();
		Channel loChannelForModification = new Channel();
		Boolean lbIsAutoApproved = false;
		String[] loReturnData = null;
		try
		{
			loHmWorkItemProps.put(HHSConstants.BUDGET_ID_WORKFLOW, asBudgetId);
			loChannelForModification.setData(HHSConstants.AO_HASH_MAP, loHmWorkItemProps);
			loChannelForModification.setData(HHSConstants.BUDGET_ID_KEY, asBudgetId);
			
			//R8.5.0 QC 9465 
			LOG_OBJECT.Debug("Auto Approval Status :::   Trace debug   " );
			LOG_OBJECT.Info("Auto Approval Status :::   Trace Info   " );
			//R8.5.0 QC 9465 
			
			
			TransactionManager.executeTransaction(loChannelForModification, HHSR5Constants.CHECK_AUTO_APPROVAL_THRESHOLD);
			lbIsAutoApproved = (Boolean) loChannelForModification.getData(HHSR5Constants.FLAG_IS_AUTO_APPROVED);
			HashMap<String, Object> loResultantMap = (HashMap<String, Object>) loChannelForModification.getData(HHSConstants.AO_HASH_MAP);
			String lsAgencyId = (String) loResultantMap.get(HHSConstants.AGENCY_ID_TABLE_COLUMN);
			LOG_OBJECT.Info("Auto Approval Status :::" + lbIsAutoApproved);
			if (lbIsAutoApproved)
			{
				LOG_OBJECT.Info("Inside Auto Approval");
				Channel loChannelForAgencyUser = new Channel();
				loChannelForAgencyUser.setData(HHSR5Constants.AGENCY_ID_STRING, lsAgencyId);
				TransactionManager.executeTransaction(loChannelForAgencyUser, HHSR5Constants.GET_AUTO_APPROVAL_USERNAME);
				String lsUserNameForAutoApprover = (String) loChannelForAgencyUser.getData(HHSR5Constants.USER_NAME_APPROVER);
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
			LOG_OBJECT.Error("Unable to get assignment details 111" + aoAppEx);
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

}
