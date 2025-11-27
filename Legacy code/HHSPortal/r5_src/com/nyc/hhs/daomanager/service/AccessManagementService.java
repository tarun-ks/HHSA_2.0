package com.nyc.hhs.daomanager.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.ContractBean;
import com.nyc.hhs.model.StaffDetails;
import com.nyc.hhs.util.DAOUtil;

/**
 * This class is added for release 5
 * This service class will get the method calls from controller through
 * transaction layer. Execute queries by calling mapper and return query output
 * back to controller. If any error exists, wrap the exception into Application
 * Exception and throw it to controller.
 */

public class AccessManagementService extends ServiceState
{
	/**
	 * This is a log object used to log any exception into log file.
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(AuditHistoryService.class);
	
	/**
	 * <p>
	 * This method is added as a part of Release 5 for user access screen This
	 * method will get the list of users to which contract is shared
	 * <ul>
	 * <li>put contract id in map</li>
	 * <li>put user id in map</li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asContractId String
	 * @param asUserId String
	 * @return loContractBean - list of type ContractBean
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	
	@SuppressWarnings("unchecked")
	public List<ContractBean> getUserAccessDetails(SqlSession aoMybatisSession, String asContractId, String asUserId)
			throws ApplicationException
	{
		List<ContractBean> loContractBean = new ArrayList<ContractBean>();
		loContractBean = null;
		Map<String, Object> lohmap = new HashMap<String, Object>();
		lohmap.put(HHSR5Constants.CONTRACT_ID_KEY, asContractId);
		lohmap.put(HHSR5Constants.USER_ID, asUserId);
		try
		{
			loContractBean = (List<ContractBean>) DAOUtil.masterDAO(aoMybatisSession, lohmap,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSR5Constants.FETCH_USER_ACCESS_DETAILS,
					HHSR5Constants.JAVA_UTIL_HASH_MAP);
			
			LOG_OBJECT.Debug("Successfully fetched the organizationm information");
		}
		
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Organization's filing audit information for the accelerator user", aoAppExp);
			throw aoAppExp;
		}
		return loContractBean;
	}
	
	/**
	 * <p>
	 * This method is added as a part of Release 5 for user access screen This
	 * is a log object which is used to log any error or exception into log file
	 * <ul>
	 * <li>put contract id in map</li>
	 * <li>put user id in map</li>
	 * <li>put userListwithoutAccess in map</li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asContractId String
	 * @param aoUserListWithoutAccess contains list of String
	 * @param loUserId String
	 * @return loOutput a boolean flag indicating any error or exception
	 *         successfully added into log
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public boolean updateUserAccessInformation(SqlSession aoMybatisSession, String asContractId,
			List<String> aoUserListWithoutAccess, String loUserId) throws ApplicationException
	{
		Boolean loOutput = false;
		ContractBean loContractListBean = new ContractBean();
		loContractListBean.setContractId(asContractId);
		loContractListBean.setUserListWithoutAccess(aoUserListWithoutAccess);
		loContractListBean.setUserId(loUserId);
		
		HashMap<String, Object> lohmap = new HashMap<String, Object>();
		lohmap.put(HHSConstants.CONTRACT_ID, asContractId);
		lohmap.put(HHSR5Constants.USER_LIST_WITHOUT_ACCESS, aoUserListWithoutAccess);
		lohmap.put(HHSConstants.USER_ID, loUserId);
		Integer loUserCount = 0;
		try
		{
			loUserCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loContractListBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSR5Constants.FETCH_L2_USER_COUNT,
					HHSR5Constants.CONTRACT_BEAN_PATH);
			if (loUserCount > 0)
			{
				DAOUtil.masterDAO(aoMybatisSession, lohmap, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
						HHSR5Constants.DELETE_CONTRACT_RESTRICTION, HHSR5Constants.JAVA_UTIL_HASH_MAP);
				for (String staffId : aoUserListWithoutAccess)
				{
					lohmap.put("staffID", staffId);
					DAOUtil.masterDAO(aoMybatisSession, lohmap, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
							HHSR5Constants.INSERT_CONTRACT_RESTRICTION_DETAILS, HHSR5Constants.JAVA_UTIL_HASH_MAP);
				}
				loOutput = true;
			}
		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Organization's filing audit information for the accelerator user", aoAppExp);
			throw aoAppExp;
		}
		return loOutput;
	}
	
	/**
	 * <p>
	 * This method is added as a part of Release 5 for user access screen This
	 * is added to get Contract Restriction Count of Deactivated User
	 * <ul>
	 * <li>put contract id in map</li>
	 * <li>put user id in map</li>
	 * <li>put userListwithoutAccess in map</li>
	 * </ul>
	 * </p>
	 * @param aoMyBatisSession as Session Input
	 * @param aoStaffDetails as Input Bean
	 * @return liCountDeactivateUser of integer type to give count of
	 *         Deactivated User
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	public int getContractRestrictionCountDeactivatedUser(SqlSession aoMyBatisSession, StaffDetails aoStaffDetails)
			throws ApplicationException
	{
		Integer liCountDeactivateUser = null;
		try
		{
			liCountDeactivateUser = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoStaffDetails,
					ApplicationConstants.MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER,
					HHSR5Constants.GET_CONTRACT_RESTRICTION_COUNT, HHSR5Constants.STAFF_DETAILS);
			
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while getServiceAppExpiringDate", aoAppEx);
			setMoState("Error while getServiceAppExpiringDate");
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while getServiceAppExpiringDate", aoExp);
			setMoState("Error while getServiceAppExpiringDate");
			throw new ApplicationException(
					"Error while getServiceAppExpiringDate", aoExp);
		}
		
		return liCountDeactivateUser;
	}
}