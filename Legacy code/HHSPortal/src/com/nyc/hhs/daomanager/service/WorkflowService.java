package com.nyc.hhs.daomanager.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.model.DocumentBean;
import com.nyc.hhs.model.WithdrawalBean;
import com.nyc.hhs.model.WorkflowDetails;
import com.nyc.hhs.service.filenetmanager.p8services.P8ProcessService;
import com.nyc.hhs.service.filenetmanager.p8services.P8ProcessServiceForSolicitationFinancials;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.DAOUtil;

/**
 * WorkflowServiceService: Class used to hit different filenet services such as
 * assign , finish task and to fetch different subsection and document details
 * for city users
 * 
 */

@Service
public class WorkflowService extends ServiceState
{

	/**
	 * Getting the list of Documents from DB by providing AppID
	 * 
	 * @param asAppID Application Id
	 * @param asSectionId Section ID
	 * @param aoMyBatisSession SQL Session
	 * @return loAppDetails List of WorkflowDetails bean
	 * @throws ApplicationException
	 */

	public List<WorkflowDetails> getAssociatedDocs(String asAppID, String asSectionId, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		List<WorkflowDetails> loAppDetails = null;
		HashMap loHMWhereClause = new HashMap();
		loHMWhereClause.put("AppID", asAppID);
		loHMWhereClause.put("SectionID", asSectionId);
	
		loAppDetails = (List<WorkflowDetails>) DAOUtil.masterDAO(aoMyBatisSession, loHMWhereClause,
				"com.nyc.hhs.service.db.services.application.WorkflowDetailMapper", "documentDetails",
				"java.util.HashMap");
		return loAppDetails;
	}

	/**
	 * Getting the list of Documents for Service Application Wf from DB by
	 * providing AppID
	 * 
	 * @param asAppID Application Id
	 * @param asSectionId Section ID
	 * @param aoMyBatisSession SQL Session
	 * @return loAppDetails List of WorkflowDetails bean
	 * @throws ApplicationException
	 */
	public List<WorkflowDetails> getAssociatedServiceDocs(String asAppID, String asSectionId,
			SqlSession aoMyBatisSession) throws ApplicationException
	{
		List<WorkflowDetails> loAppDetails = null;

		loAppDetails = null;
		HashMap loWhereClause = new HashMap();
		loWhereClause.put("AppID", asAppID);
		loWhereClause.put("SectionID", asSectionId);
		loAppDetails = (List<WorkflowDetails>) DAOUtil.masterDAO(aoMyBatisSession, loWhereClause,
				"com.nyc.hhs.service.db.services.application.WorkflowDetailMapper", "documentServiceDetails",
				"java.util.HashMap");
		return loAppDetails;
	}

	/**
	 * Getting the list of Documents from DB by providing DocumentId
	 * 
	 * @param aoHMSection HashMap of Required props
	 * @param aoMybatisSession SQL Session
	 * @return loDocList List of DocumentBean
	 * @throws ApplicationException
	 */
	public List<DocumentBean> getDocList(HashMap aoHMSection, SqlSession aoMybatisSession) throws ApplicationException
	{

		List<DocumentBean> loDocList = (List<DocumentBean>) DAOUtil.masterDAO(aoMybatisSession, aoHMSection,
				"com.nyc.hhs.service.db.services.application.WorkflowDetailMapper", "getDocList", "java.util.HashMap");

		return loDocList;
	}

	/**
	 * Getting the Sub Section Details from db for BR application by providing
	 * the App ID
	 * 
	 * @param asAppID Application ID
	 * @param asSectionId Section ID
	 * @param aoMyBatisSession SQL Session
	 * @return loAppDetails List of WorkflowDetails bean
	 * @throws ApplicationException
	 */

	public List<WorkflowDetails> getSubSectionDetails(String asAppID, String asSectionId, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		List<WorkflowDetails> loAppDetails = null;

		loAppDetails = null;
		HashMap loWhereClauseMap = new HashMap();
		loWhereClauseMap.put("AppID", asAppID);
		loWhereClauseMap.put("SectionID", asSectionId);
		loAppDetails = (List<WorkflowDetails>) DAOUtil.masterDAO(aoMyBatisSession, loWhereClauseMap,
				"com.nyc.hhs.service.db.services.application.WorkflowDetailMapper", "subSectionDetails",
				"java.util.HashMap");
		return loAppDetails;
	}

	/**
	 * Getting the Sub Section Details from db for Serice application by
	 * providing the App ID
	 * 
	 * @param asAppID Application ID
	 * @param asSectionId Section ID
	 * @param aoMyBatisSession SQL Session
	 * @return loAppDetails List of WorkflowDetails bean
	 * @throws ApplicationException
	 */

	public List<WorkflowDetails> getSubSectionServiceDetails(String asAppID, String asSectionId,
			SqlSession aoMyBatisSession) throws ApplicationException
	{
		List<WorkflowDetails> loDetails = null;

		loDetails = null;
		HashMap loHMClause = new HashMap();
		loHMClause.put("AppID", asAppID);
		loHMClause.put("SectionID", asSectionId);
		loDetails = (List<WorkflowDetails>) DAOUtil.masterDAO(aoMyBatisSession, loHMClause,
				"com.nyc.hhs.service.db.services.application.WorkflowDetailMapper", "subSectionServiceDetails",
				"java.util.HashMap");
		return loDetails;
	}

	/**
	 * Getting the provider details on task details page from DB by providing
	 * the provider name from filenet
	 * Updated for R4
	 * @param asUserId Staff ID
	 * @param asOrgId Organization Id
	 * @param aoMyBatisSession SQL Session
	 * @return loAppDetails List of WorkflowDetails bean
	 * @throws ApplicationException
	 */
	public List<WorkflowDetails> getProviderDetails(String asUserId ,String asOrgId , SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		List<WorkflowDetails> loAppDetails = null;
		HashMap loWhereClauseHM = new HashMap();

		loWhereClauseHM.put("userId", asUserId);
		loWhereClauseHM.put("orgId", asOrgId);
		loAppDetails = (List<WorkflowDetails>) DAOUtil.masterDAO(aoMyBatisSession, loWhereClauseHM,
				"com.nyc.hhs.service.db.services.application.WorkflowDetailMapper", "providerDetails",
				"java.util.HashMap");

		return loAppDetails;

	}

	/**
	 * Getting the questions from db by providing the App ID
	 * 
	 * @param asAppID Application ID
	 * @param asSectionId Section ID
	 * @param aoMyBatisSession SQL Session
	 * @return loAppDetails List of WorkflowDetails bean
	 * @throws ApplicationException
	 */

	public List<WorkflowDetails> getQuestionsDB(String asAppID, String asSectionId, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		HashMap loWhereClauseHash = new HashMap();

		loWhereClauseHash.put("AppID", asAppID);
		loWhereClauseHash.put("sectionId", asSectionId);
		List<WorkflowDetails> loAppDetails = (List<WorkflowDetails>) DAOUtil.masterDAO(aoMyBatisSession,
				loWhereClauseHash, "com.nyc.hhs.service.db.services.application.WorkflowDetailMapper", "questionDB",
				"java.util.HashMap");

		return loAppDetails;

	}

	/**
	 * Calling the getQueueWorkItems filenet method
	 * 
	 * @param aoRequiredProprs HashMap of Required props
	 * @param aoUserSessionInfo Filenet Session
	 * @return loWorkItemDetails HashMap Of Work Items
	 * @throws ApplicationException
	 */
	public HashMap getWorkItemList(HashMap aoRequiredProprs, P8UserSession aoUserSessionInfo)
			throws ApplicationException
	{

		HashMap loWorkItemDetails = new P8ProcessService().getQueueWorkItems(aoUserSessionInfo, aoRequiredProprs);

		return loWorkItemDetails;
	}

	/**
	 * Getting details of the Task from filenet by providing the wobNo &
	 * required parameters
	 * 
	 * @param asWOBNUM WorkFlowID
	 * @param aoRequiredProprs HashMap of Required props
	 * @param aoUserSessionInfo Filenet Session
	 * @return loWorkItemDetails HashMap Of Work Items
	 * @throws ApplicationException
	 */

	public HashMap getFilenetWorkItemDetails(String asWOBNUM, HashMap aoRequiredProprs, P8UserSession aoUserSessionInfo)
			throws ApplicationException
	{

		HashMap loWorkItemDetails = new P8ProcessService().getWorkItemDetails(aoUserSessionInfo, asWOBNUM,
				aoRequiredProprs);

		return loWorkItemDetails;
	}

	/**
	 * Getting child task from the filenet while providing the parent wobno.
	 * 
	 * @param asWOBNUM WorkFlow ID
	 * @param aoRequiredProprs HashMap of Required props
	 * @param aoUserSessionInfo Filenet Session
	 * @return loWorkItemDetails HashMap Of Work Items
	 * @throws ApplicationException
	 */

	public HashMap getFilenetWorkItemChildDetails(String asWOBNUM, HashMap aoRequiredProprs,
			P8UserSession aoUserSessionInfo) throws ApplicationException
	{

		HashMap loWorkItemDetails = new P8ProcessService().getChildWorkItems(aoUserSessionInfo, aoRequiredProprs,
				asWOBNUM);

		return loWorkItemDetails;
	}

	/**
	 * Getting task list from filenet based on the given parameters
	 * 
	 * @param aoUserSessionInfo Filenet Session
	 * @param aoRequiredProprs HashMap of Required props
	 * @param aoFilter HashMap Of Filter Details
	 * @return loWorkItemDetails HashMap Of Filtered Work Items
	 * @throws ApplicationException
	 */

	public HashMap getFilteredList(P8UserSession aoUserSessionInfo, HashMap aoRequiredProprs, HashMap aoFilter)
			throws ApplicationException
	{


		HashMap loWorkItemDetails = new P8ProcessServiceForSolicitationFinancials().fetchAcceleratorTask(
				aoUserSessionInfo, aoRequiredProprs, aoFilter);

		return loWorkItemDetails;
	}

	/**
	 * Reassigning Tasks to different users in Filenet
	 * 
	 * @param aoUserSession Filenet Session
	 * @param aoWobNumbers List Of Workflow ID's
	 * @param asUserName User Name
	 * @param asSessionUserName Session User Name
	 * @return lbAssigned Updated Status
	 * @throws ApplicationException
	 */

	public HashMap assign(P8UserSession aoUserSession, ArrayList<String> aoWobNumbers, String asUserName,
			String asSessionUserName, String asUserForAudit) throws ApplicationException
	{

		HashMap lbAssigned = new P8ProcessService().assign(aoUserSession, aoWobNumbers, asUserName, asSessionUserName,
				asUserForAudit);

		return lbAssigned;
	}

	/**
	 * Finish Child task and updating Filenet
	 * 
	 * @param aoUserSession Filenet Session
	 * @param asChildWobNumber WorkFlow ID
	 * @param asChildTaskStatus Selected Finished Status for WorkFlow
	 * @return lbStatusChanged Updated Status
	 * @throws ApplicationException
	 */

	public HashMap finishChildTask(P8UserSession aoUserSession, String asChildWobNumber, String asChildTaskStatus)
			throws ApplicationException
	{
		HashMap lbStatusChanged = null;
		lbStatusChanged = new P8ProcessService().finishChildTask(aoUserSession, asChildWobNumber, asChildTaskStatus);
		return lbStatusChanged;
	}

	/**
	 * Finish parent task and updating Filenet
	 * 
	 * @param aoUserSession Filenet Session
	 * @param asParentWobNumber WorkFlow ID
	 * @return String Updated Status
	 * @throws ApplicationException
	 */
	public String finishParentTask(P8UserSession aoUserSession, String asParentWobNumber) throws ApplicationException
	{
		return new P8ProcessService().finishParentTask(aoUserSession, asParentWobNumber);

	}

	/**
	 * Getting the No of Services related to a particular BR Application
	 * 
	 * @param asAppID Application ID
	 * @param aoMyBatisSession SQL Session
	 * @return loAppDetails List of WorkflowDetails bean to get number of
	 *         Services
	 * @throws ApplicationException
	 */
	public List<WorkflowDetails> getNoOfservices(String asAppID, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		List<WorkflowDetails> loAppDetails = null;
		HashMap loWhereClause = new HashMap();
		loWhereClause.put("AppId", asAppID);
		loAppDetails = (List<WorkflowDetails>) DAOUtil
				.masterDAO(aoMyBatisSession, loWhereClause,
						"com.nyc.hhs.service.db.services.application.WorkflowDetailMapper", "noOfServices",
						"java.util.HashMap");

		return loAppDetails;
	}

	/**
	 * Check User valid or not for Filenet Operations
	 * 
	 * @param aoUserSession Filenet Session
	 * @param asWobNo WorkFlow ID
	 * @param asCurrentUserName USer Name
	 * @return lbValidUser Status
	 * @throws ApplicationException
	 */

	public boolean isValidUser(P8UserSession aoUserSession, String asWobNo, String asCurrentUserName)
			throws ApplicationException
	{
		boolean lbValidUser = false;
		lbValidUser = new P8ProcessService().isValidUser(aoUserSession, asWobNo, asCurrentUserName);

		return lbValidUser;
	}

	/**
	 * Method to terminate the the Service capacity
	 * 
	 * @param aoUserSession Filenet Session
	 * @param aoWithdrawalBean WithdrawalBean
	 * @return loHmFinishResult HashMap of terminated SC Wob
	 * @throws ApplicationException
	 */
	public HashMap terminateSCParentWob(P8UserSession aoUserSession, WithdrawalBean aoWithdrawalBean)
			throws ApplicationException
	{

		HashMap loHmFinishResult = new HashMap();
		if (aoWithdrawalBean.getMsWithdrawStatus().equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED))
		{
			loHmFinishResult = new P8ProcessService().finishSCWithDrawlTask(aoUserSession,
					aoWithdrawalBean.getMsPWOBNumber());
		}
		else
		{
			loHmFinishResult.put("Terminated", "false");
		}
		return loHmFinishResult;

	}

	/**
	 * Method to terminate the SC withdrawl task
	 * 
	 * @param aoUserSession Filenet Session
	 * @param aoWithdrawalBean WithdrawalBean
	 * @return loHmFinishResult HashMap OF Terminated SC
	 * @throws ApplicationException
	 */
	public HashMap terminateSCWithdrawalWob(P8UserSession aoUserSession, WithdrawalBean aoWithdrawalBean)
			throws ApplicationException
	{
		HashMap loHmFinishResult = new HashMap();
		if (aoWithdrawalBean.isMbToBeTerminate())
		{
			loHmFinishResult = new P8ProcessService().finishChildTask(aoUserSession,
					aoWithdrawalBean.getMsWorkFlowId(), aoWithdrawalBean.getMsWithdrawStatus());
		}

		return loHmFinishResult;
	}

	/**
	 * Method to terminate the BR Parent task
	 * 
	 * @param aoUserSession Filenet Session
	 * @param aoWithdrawalBean WithdrawalBean
	 * @return loHmFinishResult HashMap Of Terminated BR
	 * @throws ApplicationException
	 */
	public HashMap terminateBRParentWob(P8UserSession aoUserSession, WithdrawalBean aoWithdrawalBean)
			throws ApplicationException
	{

		HashMap loHmFinishResult = new HashMap();
		if (aoWithdrawalBean.getMsWithdrawStatus().equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED))
		{
			loHmFinishResult = new P8ProcessService().finishBRWithDrawlTask(aoUserSession,
					aoWithdrawalBean.getMsPWOBNumber());
		}
		else
		{
			loHmFinishResult.put("Terminated", "false");
		}
		return loHmFinishResult;
	}

	/**
	 * Method calling finishChildTask() filenet method to finish the task
	 * 
	 * @param aoUserSession Filenet Session
	 * @param aoWithdrawalBean WithdrawalBean
	 * @return HashMap Terminated of Terminated BR Withdrawl
	 * @throws ApplicationException
	 */

	public HashMap terminateBRWithdrawalWob(P8UserSession aoUserSession, WithdrawalBean aoWithdrawalBean)
			throws ApplicationException
	{

		return new P8ProcessService().finishChildTask(aoUserSession, aoWithdrawalBean.getMsWorkFlowId(),
				aoWithdrawalBean.getMsWithdrawStatus());
	}



	/**
	 * Method to terminate the BR application , Service Applications & Sections
	 * 
	 * @param aoUserSession Filenet Session
	 * @param asBRAppWOBNo WorkFlow ID
	 * @param asOrgId Provider ID
	 * @param asBussAppID Business Application ID
	 * @param asUserID User ID
	 * @param aoMybatisSession SQL Session
	 * @return loHmFinishResult HashMap Of Terminated BR WF
	 * @throws ApplicationException
	 */
	public HashMap terminateBRApplicationWorkflow(P8UserSession aoUserSession, String asBRAppWOBNo, String asOrgId,
			String asBussAppID, String asUserID, SqlSession aoMybatisSession) throws ApplicationException
	{
		HashMap loHmFinishResult = new HashMap();
		if (null != asBRAppWOBNo && !"".equalsIgnoreCase(asBRAppWOBNo))
		{
			loHmFinishResult = new P8ProcessService().finishBRWithDrawlTask(aoUserSession, asBRAppWOBNo);

		}
		else
		{
			loHmFinishResult.put("Terminated", "false");

		}
		return loHmFinishResult;

	}

	/**
	 * Method to terminate the Service Application WorkFlow
	 * 
	 * @param aoUserSession Filenet Session
	 * @param asSRAppWOBNo WorkFlow ID
	 * @return HashMap Map Of Status
	 * @throws ApplicationException
	 */
	public HashMap terminateSRApplicationWorkflow(P8UserSession aoUserSession, String asSRAppWOBNo)
			throws ApplicationException
	{

		return new P8ProcessService().finishSCWithDrawlTask(aoUserSession, asSRAppWOBNo);

	}

	/**
	 * Finish Suspend task and updating Filenet
	 * 
	 * @param aoUserSession Filenet Session
	 * @param asParentWobNumber WorkFlow ID
	 * @return String Updated Status
	 * @throws ApplicationException
	 */
	public boolean suspendTask(P8UserSession aoUserSession, String asParentWobNumber, String asTaskType)
			throws ApplicationException
	{
		return new P8ProcessService().forcefullySuspendTaskItem(aoUserSession, asParentWobNumber, asTaskType);

	}

}
