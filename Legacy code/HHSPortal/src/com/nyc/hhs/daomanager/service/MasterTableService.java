package com.nyc.hhs.daomanager.service;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.model.ActionStatusBean;
import com.nyc.hhs.model.StaffDetails;
import com.nyc.hhs.util.DAOUtil;

/**
 * MasterTableService: Service Class Used to fetch the Master dropdown users and
 *                     status list for city users
 */

public class MasterTableService extends ServiceState
{

	/**
	 * This method fetch all Master Data from TASK_TYPE_MASTER table
	 * 
	 * @param aoMybatisSession
	 *            SQl Session
	 * @param asMethodName
	 *            Master Status
	 * @return loResultList List of Master Data
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<String> fetchMasterData(SqlSession aoMybatisSession, String asMethodName) throws ApplicationException
	{
		String lsStatusType = "";
		List<String> loResultList = null;
		if ("fetchTaskTypeData".equalsIgnoreCase(asMethodName))
		{
			lsStatusType = ApplicationConstants.MASTER_TASK_TYPE;
		}
		else if ("fetchBRStatus".equalsIgnoreCase(asMethodName))
		{
			lsStatusType = ApplicationConstants.MASTER_SUBSECTION_STATUS;
		}
		else if ("fetchWithdrawalStatus".equalsIgnoreCase(asMethodName))
		{
			lsStatusType = ApplicationConstants.MASTER_WITHDRAWAL_STATUS;
		}
		else if ("fetchBRFinishStatus".equalsIgnoreCase(asMethodName))
		{
			lsStatusType = ApplicationConstants.MASTER_APPLICATION_TASK_STATUS;
		}
		else if ("fetchInboxFilterStatus".equalsIgnoreCase(asMethodName))
		{
			lsStatusType = ApplicationConstants.MASTER_TASK_FILTER_STATUS;
		}

		loResultList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, lsStatusType, ApplicationConstants.MAPPER_CLASS_MASTER_TABLE_MAPPER,
				"fetchStatusMasterData", "java.lang.String");
		
		
		//Start QC 9587 R 8.10.0- remove Contact Us task from list
		if(loResultList.contains("Contact Us"))
		{
			loResultList.remove("Contact Us");
		}
		//End QC 9587 R 8.10.0- remove Contact Us task from list

		return loResultList;
	}

	/**
	 * Method to fetch city user details
	 * 
	 * @param aoMybatisSession
	 *            SQl Session
	 * @return List<StaffDetails> List Of city User Details
	 * @throws ApplicationException
	 */
	public List<StaffDetails> fetchCityUserDetails(SqlSession aoMybatisSession) throws ApplicationException
	{
		return (List<StaffDetails>) DAOUtil.masterDAO(aoMybatisSession, null, ApplicationConstants.MAPPER_CLASS_MASTER_TABLE_MAPPER,
				"fetchCityUserDetails", null);
	}

	/**
	 * Method to fetch ActionStatus
	 * 
	 * @param aoMybatisSession
	 *            SQl Session
	 * @return oversight_flag from city_user_details
	 * @throws ApplicationException
	 */
	public String fetchCityAgencyUserOversightFlag(SqlSession aoMybatisSession, String asUserId) throws ApplicationException
	{
		return (String) DAOUtil.masterDAO(aoMybatisSession, asUserId, ApplicationConstants.MAPPER_CLASS_MASTER_TABLE_MAPPER,
				"fetchCityAgencyUserOversightFlag", "java.lang.String");
	}


 	//<!-- [Start] R9.6.4 QC9701 -->
	/**
	 * List<ActionStatusBean> getActionStatusAll()
	 */
	public List<ActionStatusBean> fetchActionStatusMap( SqlSession aoMybatisSession ) throws ApplicationException 
	{
		List<ActionStatusBean>   loActionLst = (List<ActionStatusBean>) DAOUtil.masterDAO(aoMybatisSession, null, ApplicationConstants.MAPPER_CLASS_MASTER_TABLE_MAPPER,
				"getActionStatusAll", null);
		
		return loActionLst;
	}
	/**
	 * Integer updateActionStatus()
	 */
	public Integer updateActionStatus( SqlSession aoMybatisSession, ActionStatusBean aoActionStat ) throws ApplicationException 
	{
		Integer   loActionCnt = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoActionStat, ApplicationConstants.MAPPER_CLASS_MASTER_TABLE_MAPPER,
				"updateActionStatus", "com.nyc.hhs.model.ActionStatusBean");
		
		return loActionCnt;
	}
	
	/**
	 * ActionStatusBean getActionStatusByAgency()
	 */
	public ActionStatusBean getActionStatusByAgency( SqlSession aoMybatisSession, String aoAgencyId ) throws ApplicationException 
	{
		ActionStatusBean  loActionBean = (ActionStatusBean) DAOUtil.masterDAO(aoMybatisSession, aoAgencyId, ApplicationConstants.MAPPER_CLASS_MASTER_TABLE_MAPPER,
				"getActionStatusByAgency", "java.lang.String");
		
		return loActionBean;
	}
 	//<!-- [End] R9.6.4 QC9701 -->


}

