package com.nyc.hhs.daomanager.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.EINBean;
import com.nyc.hhs.model.MissingNameBean;
import com.nyc.hhs.model.OrgStaffDetailBean;
import com.nyc.hhs.model.OrganizationBean;
import com.nyc.hhs.model.ProviderBean;
import com.nyc.hhs.model.RegisterNycIdBean;
import com.nyc.hhs.model.SecurityQuestionBean;
import com.nyc.hhs.model.StaffDetails;
import com.nyc.hhs.model.UserBean;
import com.nyc.hhs.service.db.dao.NycRegisterDAO;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8services.P8ProcessService;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.PropertyUtil;

/**
 * @NycRegisterService This Service is for NYC Registration, Missing profile
 *                     information and password reset email functionalities
 * 
 */
public class NycRegisterService extends ServiceState
{
	private static final LogInfo LOG_OBJECT = new LogInfo(NycRegisterService.class);

	/**
	 * This method fetch all security questions from Security Question table
	 * @param aoMyBatisSession to connect to database
	 * @return List SecurityQuestionBean list of security question beans
	 * @throws ApplicationException
	 * 
	 */
	public List<SecurityQuestionBean> getSecurityQuestions(SqlSession aoMyBatisSession) throws ApplicationException
	{
		NycRegisterDAO loNycRegisterDAO = new NycRegisterDAO();
		List<SecurityQuestionBean> loQuestionList = null;
		try
		{
			loQuestionList = loNycRegisterDAO.getSecurityQuestions(aoMyBatisSession);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while retreiving security Questions in NycRegisterService ", loAppEx);
			setMoState("Transaction Failed:: NycRegisterService:getSecurityQuestions method - failed to retrieve security Questions \n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: NycRegisterService:getSecurityQuestions method - retrieved all  security Questions successfully \n + ");
		return loQuestionList;
	}

	/**
	 * This method inserts a new record for the user that is going to be
	 * registered into user details table.
	 * @param aoRegisterNycIdBean
	 * @param aoMyBatisSession to connect to database
	 * @return Boolean lbSuccessStatus
	 * @throws ApplicationException
	 */
	public Boolean insertNYCUserData(RegisterNycIdBean aoRegisterNycIdBean, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		Boolean lbSuccessStatus = false;
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoRegisterNycIdBean, ApplicationConstants.MAPPER_CLASS_NYC_REGISTER,
					"insertNYCUserData", "com.nyc.hhs.model.RegisterNycIdBean");
			lbSuccessStatus = true;
		}
		catch (ApplicationException loAppEx)
		{
			lbSuccessStatus = false;
			loAppEx.addContextData("RegisterNycIdBean", CommonUtil.convertBeanToString(aoRegisterNycIdBean));
			LOG_OBJECT.Error("Exception occured while inserting new user data into USER_DETAILS", loAppEx);
			setMoState("Transaction Failed:: NycRegisterService: insertNYCUserData method -Exception occured while inserting record into "
					+ "USER_DETAILS for registering a new user: /n");
			throw loAppEx;
		}
		lbSuccessStatus = true;
		setMoState("Transaction Success:: NycRegisterService: insertNYCUserData method - Successfully registered a new user and inserted record "
				+ "in USER_DETAILS /n");
		return lbSuccessStatus;
	}

	/**
	 * This method is used to insert organization account details in
	 * Organization table while creating new organization account
	 * @param aoMyBatisSession to connect to database
	 * @param aoOrganizationBean contains required details to create new
	 *            organization account
	 * @return Integer liInsertRowCount insert row count
	 * @throws ApplicationException
	 */
	public Integer insertIntoOrgAccountDetails(SqlSession aoMyBatisSession, OrganizationBean aoOrganizationBean)
			throws ApplicationException
	{
		int liInsertRowCount = 0;
		try
		{
			HashMap<String, Object> loFormNameMap = PropertyUtil.getFormNameVersionMap("basics");
			String lsFormName = (String) loFormNameMap.get(ApplicationConstants.FORM_NAME);
			String lsVersion = (String) loFormNameMap.get(ApplicationConstants.FORMVERSION_STRING);
			String lsFormId = lsFormName + "_" + lsVersion;
			aoOrganizationBean.setMsFormName(lsFormName);
			aoOrganizationBean.setMsFormVersion(lsVersion);
			aoOrganizationBean.setMsFormId(lsFormId);
			liInsertRowCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoOrganizationBean,
					ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, "insertIntoOrgAccountDetails",
					"com.nyc.hhs.model.OrganizationBean");
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while inserting Data in NycRegisterService ", loAppEx);
			setMoState("Transaction Failed:: NycRegisterService:insertOrgAccountDetails method - failed to insert Data \n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: NycRegisterService:insertOrgAccountDetails method - Successfully inserted data in Organization \n + ");
		return liInsertRowCount;
	}

	/**
	 * This method is used to search EIN number in EIN Master Table
	 * @param aoMyBatisSession to connect to database
	 * @param asEIN user EIN number
	 * @return EINBean record returned based on user EIN
	 * @throws ApplicationException
	 */
	public EINBean searchEIN(SqlSession aoMyBatisSession, String asEIN) throws ApplicationException
	{
		EINBean loEINBean = null;
		try
		{
			loEINBean = (EINBean) DAOUtil.masterDAO(aoMyBatisSession, asEIN,
					ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, "searchEIN", "java.lang.String");
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while searching EIN in EIN Master table ", loAppEx);
			setMoState("Transaction Failed:: NycRegisterService:searchEIN method - failed to search EIN \n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: NycRegisterService:searchEIN method - Successfully searched EIN \n + ");
		return loEINBean;
	}

	/**
	 * This method is used to search EIN in Organization table
	 * @param aoMyBatisSession to connect to database
	 * @param asEIN user EIN number
	 * @return loOrganizationBean record returned based on user EIN
	 * @throws ApplicationException
	 */
	public OrganizationBean searchEinInOrg(SqlSession aoMyBatisSession, String asEIN) throws ApplicationException
	{
		OrganizationBean loOrganizationBean = null;
		try
		{
			loOrganizationBean = (OrganizationBean) DAOUtil.masterDAO(aoMyBatisSession, asEIN,
					ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, "searchEinInOrg", "java.lang.String");
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while searching EIN in EIN Master table ", loAppEx);
			setMoState("Transaction Failed:: NycRegisterService:searchEIN method - failed to search EIN \n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: NycRegisterService:searchEIN method - Successfully searched EIN \n + ");
		return loOrganizationBean;
	}

	/**
	 * This method is used to insert user details in Staff Details and
	 * Organization table while creating new Organization account <li>This
	 * method was updated in R4</li>
	 * @param aoMyBatisSession to connect to database
	 * @param aoMissingNameBean details required to create organization account
	 * @param aoUserSession contains user related session data
	 * @param asWorkflowName details related to workflow for provider
	 * @param aoHmReqdWorkflowProperties workflow properties
	 * @return Integer liInsertRowCount insert row count
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public String insertIntoStaffDetails(SqlSession aoMyBatisSession, MissingNameBean aoMissingNameBean,
			P8UserSession aoUserSession, String asWorkflowName, HashMap aoHmReqdWorkflowProperties)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Inside Serivce 'insertIntoStaffDetails' to insert details in Staff Details for Organization Creation");
		StaffDetails loStaffDetails = new StaffDetails();
		OrganizationBean loOrganizationBean = new OrganizationBean();
		int liInsertRowCount = 0;
		String lsWorkflowNumber = null;
		try
		{
			loStaffDetails.setMsOrgId(aoMissingNameBean.getMsOrgId());
			loStaffDetails.setMsStaffTitle("1");
			loStaffDetails = (StaffDetails) DAOUtil.masterDAO(aoMyBatisSession, loStaffDetails,
					ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, "ceoCheckInStaffDetails",
					"com.nyc.hhs.model.StaffDetails");
			if (loStaffDetails != null && Integer.valueOf(loStaffDetails.getMsDuplicate()) < 1)
			{
				loStaffDetails = new StaffDetails();
				// populating staff detail bean for CEO
				populateBeanForCeo(aoMissingNameBean, loStaffDetails);
				String lsStaffIdToInsert = (String) DAOUtil.masterDAO(aoMyBatisSession, null,
						ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, "getStaffIdSequence", null);
				loStaffDetails.setMsStaffId(lsStaffIdToInsert);
				liInsertRowCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loStaffDetails,
						ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, "insertIntoStaffDetails",
						"com.nyc.hhs.model.StaffDetails");
				DAOUtil.masterDAO(aoMyBatisSession, loStaffDetails, ApplicationConstants.MAPPER_CLASS_NYC_REGISTER,
						"insertIntoStaffOrgMapping", "com.nyc.hhs.model.StaffDetails");
			}
			loStaffDetails = new StaffDetails();
			// populating staff detail bean for CFO and President
			populateBeanForCfoAndPresident(aoMissingNameBean, loStaffDetails);
			if (null != aoMissingNameBean.getMsCfoFirstName()
					&& !aoMissingNameBean.getMsCfoFirstName().equalsIgnoreCase(""))
			{
				loStaffDetails.setMsStaffFirstName(aoMissingNameBean.getMsCfoFirstName());
				loStaffDetails.setMsStaffLastName(aoMissingNameBean.getMsCfoLastName());
				loStaffDetails.setMsStaffMidInitial(aoMissingNameBean.getMsCfoMiddleInitial());
				loStaffDetails.setMsStaffPhone(aoMissingNameBean.getMsCfoPhoneNo());
				loStaffDetails.setMsStaffEmail(aoMissingNameBean.getMsCfoEmailAdd());
				loStaffDetails.setMsStaffTitle("2");
				String lsStaffIdToInsert = (String) DAOUtil.masterDAO(aoMyBatisSession, null,
						ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, "getStaffIdSequence", null);
				loStaffDetails.setMsStaffId(lsStaffIdToInsert);
				liInsertRowCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loStaffDetails,
						ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, "insertIntoStaffDetails",
						"com.nyc.hhs.model.StaffDetails");
				DAOUtil.masterDAO(aoMyBatisSession, loStaffDetails, ApplicationConstants.MAPPER_CLASS_NYC_REGISTER,
						"insertIntoStaffOrgMapping", "com.nyc.hhs.model.StaffDetails");
			}
			// populating staff detail bean for President
			loStaffDetails.setMsStaffFirstName(aoMissingNameBean.getMsPresFirstName());
			loStaffDetails.setMsStaffLastName(aoMissingNameBean.getMsPresLastName());
			loStaffDetails.setMsStaffMidInitial(aoMissingNameBean.getMsPresMiddleInitial());
			loStaffDetails.setMsStaffPhone(aoMissingNameBean.getMsPresPhoneNo());
			loStaffDetails.setMsStaffEmail(aoMissingNameBean.getMsPresEmailAdd());
			loStaffDetails.setMsStaffTitle("6");
			String lsStaffIdToInsert = (String) DAOUtil.masterDAO(aoMyBatisSession, null,
					ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, "getStaffIdSequence", null);
			loStaffDetails.setMsStaffId(lsStaffIdToInsert);
			liInsertRowCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loStaffDetails,
					ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, "insertIntoStaffDetails",
					"com.nyc.hhs.model.StaffDetails");
			DAOUtil.masterDAO(aoMyBatisSession, loStaffDetails, ApplicationConstants.MAPPER_CLASS_NYC_REGISTER,
					"insertIntoStaffOrgMapping", "com.nyc.hhs.model.StaffDetails");
			// populating staff detail bean for Admin
			forProfitNonProfitCheck(aoMissingNameBean, loStaffDetails);
			populateBeanForAdmin(aoMissingNameBean, loStaffDetails);
			lsStaffIdToInsert = (String) DAOUtil.masterDAO(aoMyBatisSession, null,
					ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, "getStaffIdSequence", null);
			loStaffDetails.setMsStaffId(lsStaffIdToInsert);
			liInsertRowCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loStaffDetails,
					ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, "insertIntoStaffDetails",
					"com.nyc.hhs.model.StaffDetails");
			DAOUtil.masterDAO(aoMyBatisSession, loStaffDetails, ApplicationConstants.MAPPER_CLASS_NYC_REGISTER,
					"insertIntoStaffOrgMapping", "com.nyc.hhs.model.StaffDetails");
			// insert into organization
			loOrganizationBean = populateOrganizationBean(aoMissingNameBean, loOrganizationBean, aoMyBatisSession);
			HashMap<String, Object> loFormNameMap = PropertyUtil.getFormNameVersionMap("basics");
			String lsFormName = (String) loFormNameMap.get(ApplicationConstants.FORM_NAME);
			String lsVersion = (String) loFormNameMap.get(ApplicationConstants.FORMVERSION_STRING);
			String lsFormId = lsFormName + "_" + lsVersion;
			loOrganizationBean.setMsFormName(lsFormName);
			loOrganizationBean.setMsFormVersion(lsVersion);
			loOrganizationBean.setMsFormId(lsFormId);
			liInsertRowCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loOrganizationBean,
					ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, "insertIntoOrgAccountDetails",
					"com.nyc.hhs.model.OrganizationBean");
			StaffDetails loStaffDetails2 = new StaffDetails();
			String lsStaffId = null;
			loStaffDetails2.setMsUserDN(aoMissingNameBean.getMsUserDN());
			loStaffDetails2.setMsStaffEmail(aoMissingNameBean.getMsAdminEmailAdd());
			loStaffDetails2 = (StaffDetails) DAOUtil.masterDAO(aoMyBatisSession, loStaffDetails2,
					ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, "searchStaffIdInStaffDetails",
					"com.nyc.hhs.model.StaffDetails");
			if (null != loStaffDetails2)
			{
				lsStaffId = loStaffDetails2.getMsStaffId();
			}
			P8ProcessService loP8ProcessService = new P8ProcessService();
			aoHmReqdWorkflowProperties.put(P8Constants.PROPERTY_PE_LAUNCH_BY, lsStaffId);
			if (liInsertRowCount > 0)
			{
				if (aoMissingNameBean.getMsOrgCorpStructure().equalsIgnoreCase(
						ApplicationConstants.ORG_CORPORATE_FOR_PROFIT))
				{
					lsWorkflowNumber = loP8ProcessService.launchWorkflow(aoUserSession, asWorkflowName,
							aoHmReqdWorkflowProperties);
				}
				else if (aoMissingNameBean.getMsOrgCorpStructure().equalsIgnoreCase(
						ApplicationConstants.ORG_CORPORATE_NON_PROFIT))
				{
					if (aoMissingNameBean.getMsWorkFlowRequired())
					{
						lsWorkflowNumber = loP8ProcessService.launchWorkflow(aoUserSession, asWorkflowName,
								aoHmReqdWorkflowProperties);
					}
				}
			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while inserting Data in Staff Details ", loAppEx);
			setMoState("Transaction Failed:: NycRegisterService:insertIntoStaffDetails method - failed to insert Data in Staff Details \n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: NycRegisterService:insertIntoStaffDetails method - Successfully inserted data in Staff Details \n + ");
		return null != lsWorkflowNumber ? lsWorkflowNumber : " ";
	}

	/**
	 * This method fill the staff detail bean based on for profit or Non profit
	 * @param aoMissingNameBean details required to fill staff details
	 * 
	 * @param aoStaffDetails
	 */
	private void forProfitNonProfitCheck(MissingNameBean aoMissingNameBean, StaffDetails aoStaffDetails)
	{
		if (aoMissingNameBean.getMsOrgCorpStructure().equalsIgnoreCase(ApplicationConstants.ORG_CORPORATE_FOR_PROFIT))
		{
			aoStaffDetails.setMsStaffActiveFlag("No");
			aoStaffDetails.setMsMemberStatus("Pending");
			aoStaffDetails.setMsUserStatus("Pending");
		}
		else if (aoMissingNameBean.getMsOrgCorpStructure().equalsIgnoreCase(
				ApplicationConstants.ORG_CORPORATE_NON_PROFIT))
		{
			if (aoMissingNameBean.getMsWorkFlowRequired())
			{
				aoStaffDetails.setMsStaffActiveFlag("No");
				aoStaffDetails.setMsMemberStatus("Pending");
				aoStaffDetails.setMsUserStatus("Pending");
			}
			else
			{
				aoStaffDetails.setMsStaffActiveFlag("Yes");
				aoStaffDetails.setMsMemberStatus("Active");
				aoStaffDetails.setMsUserStatus("Yes");
			}
		}
	}

	/**
	 * This method is used to populate staff detail bean for admin while
	 * creating new Organization account. This method is updated for R4 - Added permission type at insertion.
	 * @param aoMissingNameBean details required to create organization account
	 * @param aoStaffDetails staff detail bean to insert data into DB
	 * @throws ApplicationException
	 */
	private void populateBeanForAdmin(MissingNameBean aoMissingNameBean, StaffDetails aoStaffDetails)
			throws ApplicationException
	{
		aoStaffDetails.setMsStaffFirstName(aoMissingNameBean.getMsAdminFirstName());
		aoStaffDetails.setMsStaffLastName(aoMissingNameBean.getMsAdminLastName());
		aoStaffDetails.setMsStaffMidInitial(aoMissingNameBean.getMsAdminMiddleInitial());
		aoStaffDetails.setMsStaffPhone(aoMissingNameBean.getMsAdminPhoneNo());
		aoStaffDetails.setMsStaffEmail(aoMissingNameBean.getMsAdminEmailAdd());
		aoStaffDetails.setMsNYCUserId(aoMissingNameBean.getMsUserId());
		aoStaffDetails.setMsTermConditionStatus("Yes");
		aoStaffDetails.setMsPermissionLevel("Level 2");
		aoStaffDetails.setMsPermissionType(ApplicationConstants.ROLE_FINANCIALPROCUREMENT);
		aoStaffDetails.setMsAdminPermission("Yes");
		aoStaffDetails.setMsSystemUser("");
		aoStaffDetails.setMsStaffEmail(aoMissingNameBean.getMsAdminEmailAdd());
		aoStaffDetails.setMsUserDN(aoMissingNameBean.getMsUserDN());
		aoStaffDetails.setMsStaffTitle(aoMissingNameBean.getMsAdminOfficeTitle());
		aoStaffDetails.setMsUserAcctCreationDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
	}

	/**
	 * This method is used to populate staff detail bean for CFO while creating
	 * new Organization account
	 * @param aoMissingNameBean details required to create organization account
	 * @param aoStaffDetails staff detail bean to insert data into DB
	 * @throws ApplicationException
	 */
	private void populateBeanForCfoAndPresident(MissingNameBean aoMissingNameBean, StaffDetails aoStaffDetails)
			throws ApplicationException
	{
		aoStaffDetails.setMsMemberInactiveDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
		aoStaffDetails.setMsMemberStatus("Active");
		aoStaffDetails.setMsNYCUserId("");
		aoStaffDetails.setMsOrgId(aoMissingNameBean.getMsOrgId());
		aoStaffDetails.setMsPermissionLevel("");
		aoStaffDetails.setMsStaffActiveFlag("No");
		aoStaffDetails.setMsAdminPermission("");
		aoStaffDetails.setMsSystemUser("");
		aoStaffDetails.setMsUserStatus("No");
		aoStaffDetails.setOperationType("");
		aoStaffDetails.setMsUserAcctCreationDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
	}

	/**
	 * This method is used to populate staff detail bean for CEO while creating
	 * new Organization account
	 * @param aoMissingNameBean details required to create organization account
	 * @param aoStaffDetails staff detail bean to insert data into DB
	 * @throws ApplicationException
	 */
	private void populateBeanForCeo(MissingNameBean aoMissingNameBean, StaffDetails aoStaffDetails)
			throws ApplicationException
	{
		aoStaffDetails.setMsMemberInactiveDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
		aoStaffDetails.setMsMemberStatus("Active");
		aoStaffDetails.setMsNYCUserId("");
		aoStaffDetails.setMsOrgId(aoMissingNameBean.getMsOrgId());
		aoStaffDetails.setMsPermissionLevel("");
		aoStaffDetails.setMsStaffActiveFlag("No");
		aoStaffDetails.setMsAdminPermission("");
		aoStaffDetails.setMsSystemUser("");
		aoStaffDetails.setMsUserStatus("No");
		aoStaffDetails.setOperationType("");
		aoStaffDetails.setMsStaffFirstName(aoMissingNameBean.getMsCeoFirstName());
		aoStaffDetails.setMsStaffLastName(aoMissingNameBean.getMsCeoLastName());
		aoStaffDetails.setMsStaffMidInitial(aoMissingNameBean.getMsCeoMiddleInitial());
		aoStaffDetails.setMsStaffPhone(aoMissingNameBean.getMsCeoPhoneNo());
		aoStaffDetails.setMsStaffEmail(aoMissingNameBean.getMsCeoEmailAdd());
		aoStaffDetails.setMsStaffTitle("1");
		aoStaffDetails.setMsUserAcctCreationDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
	}

	/**
	 * This method is used to insert user details in Staff Details creating new
	 * Organization account.
	 * <li>This method is updated for R4.</li>
	 * @param aoMyBatisSession to connect to database
	 * @param aoStaffDetails details required to create organization account
	 * @return Integer liInsertRowCount insert row count
	 * @throws ApplicationException
	 */
	public Integer insertIntoStaffDetail(SqlSession aoMyBatisSession, StaffDetails aoStaffDetails)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Inside Serivce 'insertIntoStaffDetails' to insert details in Staff Details for Account Request");
		int liInsertRowCount = 0;
		int liPendingRequestCount = 0;
		String lsStaffIdToInsert = "0";
		try
		{

			liPendingRequestCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoStaffDetails,
					ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, "getPendingRequestCount",
					"com.nyc.hhs.model.StaffDetails");
			
			if (liPendingRequestCount == 0)
			{
				lsStaffIdToInsert = (String) DAOUtil.masterDAO(aoMyBatisSession, null,
						ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, "getStaffIdSequence", null);
				aoStaffDetails.setMsStaffId(lsStaffIdToInsert);
				liInsertRowCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoStaffDetails,
						ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, "insertIntoStaffDetails",
						"com.nyc.hhs.model.StaffDetails");
				DAOUtil.masterDAO(aoMyBatisSession, aoStaffDetails, ApplicationConstants.MAPPER_CLASS_NYC_REGISTER,
						"insertIntoStaffOrgMapping", "com.nyc.hhs.model.StaffDetails");

			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while inserting Data in Staff Details ", loAppEx);
			setMoState("Transaction Failed:: NycRegisterService:insertIntoStaffDetail method - failed to insert Data in Staff Details \n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: NycRegisterService:insertIntoStaffDetail method - Successfully inserted data in Staff Details \n + ");
		return Integer.parseInt(lsStaffIdToInsert);

	}

	/**
	 * This method is used to get user details , organization Details from Staff
	 * details and organization table Updated for R4: Changed the return type of
	 * method. This method will now return the list of StaffDetails
	 * corresponding to mapped Organizations.
	 * @param aoMyBatisSession to connect to database
	 * @param aoStaffDetails organization id and email attribute of bean fetch
	 *            user organization details
	 * @return loStaffDetails contains user and their organization details
	 * @throws ApplicationException
	 */
	public List<StaffDetails> getUserOrgDetails(SqlSession aoMyBatisSession, StaffDetails aoStaffDetails)
			throws ApplicationException
	{
		List<StaffDetails> loStaffDetails = null;
		try
		{

			loStaffDetails = (List<StaffDetails>) DAOUtil.masterDAO(aoMyBatisSession, aoStaffDetails,
					ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, "getUserOrgDetails",
					HHSConstants.INPUT_PARAM_CLASS_STAFF_DETAILS_BEAN);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while getting Data from Staff and Organization ", loAppEx);
			setMoState("Transaction Failed:: NycRegisterService:getUserOrgDetails method - failed to get Data from Staff and Organization \n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: NycRegisterService:getUserOrgDetails method - Successfully reterived Data from Staff and Organization \n + ");
		return loStaffDetails;
	}

	/*[Start] R 7.8.0      */
	/**
     * This method is used to get user details , organization Details from Staff
     * details and organization table Updated for R4: Changed the return type of
     * method. This method will now return the list of StaffDetails
     * corresponding to mapped Organizations.
     * @param aoMyBatisSession to connect to database
     * @param aoStaffDetails organization id and email attribute of bean fetch
     *            user organization details
     * @return loStaffDetails contains user and their organization details
     * @throws ApplicationException
     */
	public Integer checkNycIdNUpdate(SqlSession aoMyBatisSession, StaffDetails aoStaffDetails) throws ApplicationException{
		Integer liUpdateRowCount = 0;
		try
		{
	      return (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoStaffDetails,
                ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, HHSConstants.CHECK_PROVIDER_NYC_ID_UPDATE,
                HHSConstants.INPUT_PARAM_CLASS_STAFF_DETAILS_BEAN);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while getting Data from Staff and Organization ", loAppEx);
			setMoState("Transaction Failed:: checkNycIdNUpdate method - failed to update Data in Staff Details \n");
			throw loAppEx;
		}
		
	}
    /*[End] R 7.8.0      */
	
	/**
	 * This method is used to get user details and organization details from
	 * Staff_details and Organization table
	 * @param aoMyBatisSession to connect to database
	 * @param asNycUserId NYC id of user
	 * @return List loOrgStaffDetailBeanList list of beans returned based on
	 *         user nyc id
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<OrgStaffDetailBean> getMissingDetails(SqlSession aoMyBatisSession, String asNycUserId)
			throws ApplicationException
	{
		List<OrgStaffDetailBean> loOrgStaffDetailBeanList = null;
		try
		{
			loOrgStaffDetailBeanList = (List<OrgStaffDetailBean>) DAOUtil.masterDAO(aoMyBatisSession, asNycUserId,
					ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, "getMissingDetails", "java.lang.String");
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while getting Data from Staff and Organization ", loAppEx);
			setMoState("Transaction Failed:: NycRegisterService:getMissingDetails method - failed to get Data from Staff and Organization \n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: NycRegisterService:getMissingDetails method - Successfully reterived Data from Staff and Organization \n + ");
		return loOrgStaffDetailBeanList;
	}

	/**
	 * This method is used to update email address in Staff Details where the
	 * LDAP email is different from database email
	 * @param aoMyBatisSession to connect to database
	 * @param aoStaffDetails contains user specific details
	 * @return Integer liUpdateRowCount update row count
	 * @throws ApplicationException
	 */

	public Integer updateStaffEmail(SqlSession aoMyBatisSession, StaffDetails aoStaffDetails)
			throws ApplicationException
	{
		int liUpdateRowCount = 0;
		try
		{
			liUpdateRowCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoStaffDetails,
					ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, "updateStaffEmail",
					"com.nyc.hhs.model.StaffDetails");
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while updating Email Address in Staff Details ", loAppEx);
			setMoState("Transaction Failed:: NycRegisterService:updateStaffEmail method - failed to update Email Address in Staff Details \n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: NycRegisterService:updateStaffEmail method - Successfully updated Email Address in Staff Details \n + ");
		return liUpdateRowCount;
	}

	/**
	 * This method is used to search user DN in staff details table
	 * @param aoMyBatisSession to connect to database
	 * @param asUserDN used to search record
	 * @return loStaffDetails record based on userDN
	 * @throws ApplicationException
	 */

	public StaffDetails searchUserDnInStaffDetails(SqlSession aoMyBatisSession, MissingNameBean aoMissingNameBean)
			throws ApplicationException
	{
		StaffDetails loStaffDetails = null;
		try
		{
			loStaffDetails = (StaffDetails) DAOUtil.masterDAO(aoMyBatisSession, aoMissingNameBean,
					ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, "searchUserDnInStaffDetails",
					"com.nyc.hhs.model.MissingNameBean");
			if (null != loStaffDetails)
			{
				Map<String, Object> loLastLoginMap = new HashMap<String, Object>();
				Date loLastLoginDate = DateUtil.getSqlDate(DateUtil.getCurrentDate());
				loLastLoginMap.put("LastLoginDate", loLastLoginDate);
				loLastLoginMap.put("UserDN", aoMissingNameBean.getMsUserDN());
				loLastLoginMap.put("ModifyBy", loStaffDetails.getMsStaffId());
				DAOUtil.masterDAO(aoMyBatisSession, loLastLoginMap, ApplicationConstants.MAPPER_CLASS_NYC_REGISTER,
						"updateLastLoginDate", "java.util.Map");
			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while updating Email Address in Staff Details ", loAppEx);
			setMoState("Transaction Failed:: NycRegisterService:searchUserDnInStaffDetails method - failed to search data based on UserDN in "
					+ "Staff Details \n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: NycRegisterService:searchUserDnInStaffDetails method - Successfully fetched data based on UserDN from "
				+ "Staff Details \n + ");
		return loStaffDetails;
	}

	/**
	 * This method is used to search user DN in staff details table
	 * @param aoMyBatisSession to connect to database
	 * @param asUserDN used to search record
	 * @return loStaffDetails record based on userDN
	 * @throws ApplicationException
	 */

	public StaffDetails checkUserDnInStaffDetails(SqlSession aoMyBatisSession, UserBean aoUserBean)
			throws ApplicationException
	{
		StaffDetails loStaffDetails = null;
		try
		{
			loStaffDetails = (StaffDetails) DAOUtil.masterDAO(aoMyBatisSession, aoUserBean,
					ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, "checkUserDnInStaffDetails",
					"com.nyc.hhs.model.UserBean");
			if (null != loStaffDetails)
			{
				Map<String, Object> loLastLoginMap = new HashMap<String, Object>();
				Date loLastLoginDate = DateUtil.getSqlDate(DateUtil.getCurrentDate());
				loLastLoginMap.put("LastLoginDate", loLastLoginDate);
				loLastLoginMap.put("UserDN", aoUserBean.getMsUserDN());
				loLastLoginMap.put("ModifyBy", loStaffDetails.getMsStaffId());
				DAOUtil.masterDAO(aoMyBatisSession, loLastLoginMap, ApplicationConstants.MAPPER_CLASS_NYC_REGISTER,
						"updateLastLoginDate", "java.util.Map");
			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while updating Email Address in Staff Details ", loAppEx);
			setMoState("Transaction Failed:: NycRegisterService:searchUserDnInStaffDetails method - failed to search data based on UserDN in "
					+ "Staff Details \n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: NycRegisterService:searchUserDnInStaffDetails method - Successfully fetched data based on UserDN from "
				+ "Staff Details \n + ");
		return loStaffDetails;
	}

	/**
	 * This method is used to check duplicate CEO entry in staff details table
	 * @param aoMyBatisSession to connect to database
	 * @param aoStaffDetails contains title field that determines CEO entry in
	 *            staff details
	 * @return StaffDetails loStaffDetails, isDuplicate variable of bean decides
	 *         whether CEO already exists or not
	 * @throws ApplicationException
	 */
	public StaffDetails ceoCheckInStaffDetails(SqlSession aoMyBatisSession, StaffDetails aoStaffDetails)
			throws ApplicationException
	{
		StaffDetails loStaffDetails = null;
		try
		{
			loStaffDetails = (StaffDetails) DAOUtil.masterDAO(aoMyBatisSession, aoStaffDetails,
					ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, "ceoCheckInStaffDetails",
					"com.nyc.hhs.model.StaffDetails");
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while inserting Data in Staff Details ", loAppEx);
			setMoState("Transaction Failed:: NycRegisterService:ceoCheckInStaffDetails method - failed to perform duplicate check in "
					+ "Staff Details \n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: NycRegisterService:ceoCheckInStaffDetailsmethod - Successfully performed duplicate check in "
				+ "Staff Details \n + ");
		return loStaffDetails;
	}

	/**
	 * This method is used to update terms and condition flag in staff details
	 * table
	 * @param aoMyBatisSession to connect to database
	 * @param aoStaffDetails contains terms and conditions that needs to be
	 *            updated
	 * @return Integer liUpdateRowCount update row count
	 * @throws ApplicationException
	 */
	public Integer updateTCinStaffDetails(SqlSession aoMyBatisSession, StaffDetails aoStaffDetails)
			throws ApplicationException
	{
		int liUpdateRowCount = 0;
		try
		{
			liUpdateRowCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoStaffDetails,
					ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, "updateTCinStaffDetails",
					"com.nyc.hhs.model.StaffDetails");
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while inserting Data in Staff Details ", loAppEx);
			setMoState("Transaction Failed:: NycRegisterService:updateTCinStaffDetails method - failed to update T&C in Staff Details \n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: NycRegisterService:updateTCinStaffDetails method - Successfully updated data in Staff Details \n + ");
		return liUpdateRowCount;
	}

	/**
	 * This method is used to update user profile details in staff details table
	 * based on their user DN.
	 * @param aoMyBatisSession to connect to database
	 * @param aoUserDetailsMap contains user first name, last name , Middle name
	 *            and user DN
	 * @return lbUpdateRowCount no of rows updated
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public Integer updateUserProfileinStaffDetails(SqlSession aoMyBatisSession, Map aoUserDetailsMap)
			throws ApplicationException
	{
		int lbUpdateRowCount = 0;
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoUserDetailsMap, ApplicationConstants.MAPPER_CLASS_NYC_REGISTER,
					"updateUserProfileinStaffDetails", "java.util.Map");
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while inserting Data in Staff Details ", loAppEx);
			setMoState("Transaction Failed:: NycRegisterService:updateUserProfileinStaffDetails method - failed to update Data in Staff Details \n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: NycRegisterService:updateUserProfileinStaffDetails method - Successfully updated data in Staff "
				+ "Details \n + ");
		return lbUpdateRowCount;
	}

	/**
	 * This method retrieves Current Sequence from Organization table
	 * @param aoMyBatisSession to connect to database
	 * @return Integer liCurrentSeq current sequence from the organization table
	 * @throws ApplicationException
	 */
	public Integer getCurrentSeqFromTable(SqlSession aoMyBatisSession) throws ApplicationException
	{
		int liCurrentSeq = 0;
		try
		{
			liCurrentSeq = (Integer) DAOUtil.masterDAO(aoMyBatisSession, null,
					ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, "getCurrentSeqFromTable", null);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while retreiving Current Sequence from Table", loAppEx);
			setMoState("Transaction Failed::  NycRegisterService:getCurrentSeqFromTable method -Exception occured while while retreiving Current "
					+ "Sequence from Table: /n");
			throw loAppEx;
		}
		setMoState("Transaction Success::  NycRegisterService:getCurrentSeqFromTable method - Current Sequence from Table have been retreived "
				+ "successfully: /n");
		return liCurrentSeq;
	}

	/**
	 * This method retrieves Next Sequence from Organization table
	 * @param aoMyBatisSession to connect to database
	 * @return Integer liNextSeq next sequence from the organization table
	 * @throws ApplicationException if an application exception occurs
	 */
	public Integer getNextSeqFromTable(SqlSession aoMyBatisSession) throws ApplicationException
	{
		int liNextSeq;
		try
		{
			liNextSeq = (Integer) DAOUtil.masterDAO(aoMyBatisSession, null,
					ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, "getNextSeqFromTable", null);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while retreiving Current Sequence from Table", loAppEx);
			setMoState("Transaction Failed:: NycRegisterService:getNextSeqFromTable method -Exception occured while while retreiving Current "
					+ "Sequence from Table: /n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: NycRegisterService:getNextSeqFromTable method - Next Sequence from Table have been retreived "
				+ "successfully: /n");
		return liNextSeq;
	}

	/**
	 * This method retrieves Current Sequence from Staff Details table
	 * @param aoMyBatisSession to connect to database
	 * @return Integer liCurrentSeq current sequence from the staff details
	 *         table
	 * @throws ApplicationException if an application exception occurs
	 */
	public Integer getCurrentSeqFromStaff(SqlSession aoMyBatisSession) throws ApplicationException
	{
		int liCurrentSeq = 0;
		try
		{
			liCurrentSeq = (Integer) DAOUtil.masterDAO(aoMyBatisSession, null,
					ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, "getCurrentSeqFromStaff", null);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while retreiving Current Sequence from Table", loAppEx);
			setMoState("Transaction Failed:: NycRegisterService:getCurrentSeqFromTable method -Exception occured while while retreiving Current "
					+ "Sequence from Table: /n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: NycRegisterService:getCurrentSeqFromStaff method - Current Sequence from Table have been retreived "
				+ "successfully: /n");
		return liCurrentSeq;
	}

	/**
	 * This method retrieves Next Sequence from Staff Details table
	 * @param aoMyBatisSession to connect to database
	 * @return Integer liCurrentSeq next sequence from the staff details table
	 * @throws ApplicationException if an application exception occurs
	 */
	public Integer getNextSeqFromStaff(SqlSession aoMyBatisSession) throws ApplicationException
	{
		int liNextSeq = 0;
		try
		{
			liNextSeq = (Integer) DAOUtil.masterDAO(aoMyBatisSession, null,
					ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, "getNextSeqFromStaff", null);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while retreiving Current Sequence from Table", loAppEx);
			setMoState("Transaction Failed:: NycRegisterService:getCurrentSeqFromTable method -Exception occured while while retreiving Current "
					+ "Sequence from Table: /n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: NycRegisterService:getNextSeqFromStaff method - Next Sequence from Table have been retreived "
				+ "successfully: /n");
		return liNextSeq;
	}

	/**
	 * Changed method - By: Harish Kumar Reason: Organization Creation date is
	 * set to null before approving "Provider Account Request" task and after
	 * rejecting "Provider Account Request" task, Organization Creation date is
	 * current date after approving "Provider Account Request" task and for
	 * Non-Profit where EIN_ID_NN is present in EIN MASTER
	 * 
	 * This private method is used to populate organizationBean from missing
	 * name bean while creating organization account
	 * @param aoMissingNameBean required to populate organization bean to insert
	 *            record in Organization table
	 * @param aoOrganizationBean containing organization details
	 * @param aoMyBatisSession mybatis session
	 * @return aoOrganizationBean containing details required to insert record
	 *         in Organization table
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private OrganizationBean populateOrganizationBean(MissingNameBean aoMissingNameBean,
			OrganizationBean aoOrganizationBean, SqlSession aoMyBatisSession) throws ApplicationException
	{
		aoOrganizationBean.setMsAcctPeriodStrtMonth(aoMissingNameBean.getMsOrgAcctPeriodStart());
		aoOrganizationBean.setMsAlternateName(aoMissingNameBean.getMsOrgDoingBusAs());
		aoOrganizationBean.setMsCorpStrucId(aoMissingNameBean.getMsOrgCorpStructure());
		aoOrganizationBean.setMsDunsId(aoMissingNameBean.getMsOrgDunsNumber());
		aoOrganizationBean.setMsEinId(aoMissingNameBean.getMsOrgEinTinNumber());
		aoOrganizationBean.setMsEmailAddress(aoMissingNameBean.getMsAdminEmailAdd());
		aoOrganizationBean.setMsEntityTypeId(aoMissingNameBean.getMsOrgEntityType());
		aoOrganizationBean.setMsEntityTypeOthers(aoMissingNameBean.getMsOrgEntityTypeOther());
		aoOrganizationBean.setMsMissionStatement(" ");
		aoOrganizationBean.setMsAcctPeriodEndMonth(aoMissingNameBean.getMsOrgAcctPeriodEnd());
		aoOrganizationBean.setMsOrgActiveFlag("true");
		HashMap loCacheAppSettings = (HashMap) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.APPLICATION_SETTING);
		String lsExpiryDate = (String) loCacheAppSettings.get("ProviderExpiration" + "_" + "BR001");
		int lsYearsToBeAdded = Integer.valueOf(lsExpiryDate);

		Calendar loDate = Calendar.getInstance();
		loDate.setTime(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
		loDate.add(Calendar.YEAR, lsYearsToBeAdded);

		aoOrganizationBean.setMsOrgExpirationDate(loDate.getTime());
		aoOrganizationBean.setMsOrgId(aoMissingNameBean.getMsOrgId());
		aoOrganizationBean.setMsOrgLegalName(aoMissingNameBean.getMsOrgLegalName());
		aoOrganizationBean.setMsOrgStrtDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
		aoOrganizationBean.setMsOrgType("provider_org");
		aoOrganizationBean.setMsOverridingFlag("0");
		aoOrganizationBean.setMsOverridingReason("ok");
		aoOrganizationBean.setMsPhoneNo(aoMissingNameBean.getMsAdminPhoneNo());
		aoOrganizationBean.setMsSubmissionDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
		aoOrganizationBean.setMsSubmittedBy(aoMissingNameBean.getMsUserId());
		aoOrganizationBean.setMsAddress1(aoMissingNameBean.getMsExecAddrLine1());
		aoOrganizationBean.setMsAddress2(aoMissingNameBean.getMsExecAddrLine2());
		aoOrganizationBean.setMsCity(aoMissingNameBean.getMsExecCity());
		aoOrganizationBean.setMsState(aoMissingNameBean.getMsExecState());
		aoOrganizationBean.setMsFaxNumber(aoMissingNameBean.getMsExecFaxNo());
		aoOrganizationBean.setMsWebSite(aoMissingNameBean.getMsExecWebSite());
		aoOrganizationBean.setMsZipCode(aoMissingNameBean.getMsExecZipCode());
		aoOrganizationBean.setMsBuildIdNumber(aoMissingNameBean.getMsBuildIdNumber());
		aoOrganizationBean.setMsCivilCourtDistt(aoMissingNameBean.getMsCivilCourtDistt());
		aoOrganizationBean.setMsCommDistt(aoMissingNameBean.getMsCommDistt());
		aoOrganizationBean.setMsCongressionalDistrictName(aoMissingNameBean.getMsCongressionalDistrictName());
		aoOrganizationBean.setMsCouncilDistt(aoMissingNameBean.getMsCouncilDistt());
		aoOrganizationBean.setMsHealthArea(aoMissingNameBean.getMsHealthArea());
		aoOrganizationBean.setMsHighEndCrossStreetName(aoMissingNameBean.getMsHighEndCrossStreetName());
		aoOrganizationBean.setMsLowEndCrossStreetName(aoMissingNameBean.getMsLowEndCrossStreetName());
		aoOrganizationBean.setMsLowEndCrossStreetNo(aoMissingNameBean.getMsLowEndCrossStreetNo());
		aoOrganizationBean.setMsHighEndCrossStreetNo(aoMissingNameBean.getMsHighEndCrossStreetNo());
		aoOrganizationBean.setMsNormHouseNumber(aoMissingNameBean.getMsNormHouseNumber());
		aoOrganizationBean.setMsValZipCode(aoMissingNameBean.getMsValZipCode());
		aoOrganizationBean.setMsValYCoordinate(aoMissingNameBean.getMsValYCoordinate());
		aoOrganizationBean.setMsValXCoordinate(aoMissingNameBean.getMsValXCoordinate());
		aoOrganizationBean.setMsValCity(aoMissingNameBean.getMsValCity());
		aoOrganizationBean.setMsValBorough(aoMissingNameBean.getMsValBorough());
		aoOrganizationBean.setMsTaxLot(aoMissingNameBean.getMsTaxLot());
		aoOrganizationBean.setMsTaxBlock(aoMissingNameBean.getMsTaxBlock());
		aoOrganizationBean.setMsStreetNumberText(aoMissingNameBean.getMsStreetNumberText());
		aoOrganizationBean.setMsStatusReason(aoMissingNameBean.getMsStatusReason());
		aoOrganizationBean.setMsStatusDescriptionText(aoMissingNameBean.getMsStatusDescriptionText());
		aoOrganizationBean.setMsSenatorDistt(aoMissingNameBean.getMsSenatorDistt());
		aoOrganizationBean.setMsSchoolDisttName(aoMissingNameBean.getMsSchoolDisttName());
		aoOrganizationBean.setMsAssemblyDistt(aoMissingNameBean.getMsAssemblyDistt());
		aoOrganizationBean.setMsValState(aoMissingNameBean.getMsValState());
		aoOrganizationBean.setMsLatitude(aoMissingNameBean.getMsLatitude());
		aoOrganizationBean.setMsLongitude(aoMissingNameBean.getMsLongitude());

		if (aoMissingNameBean.getMsOrgCorpStructure().equalsIgnoreCase(ApplicationConstants.ORG_CORPORATE_FOR_PROFIT))
		{
			aoOrganizationBean.setMsProcStatus("In Review");
			aoOrganizationBean.setMsOrgStatus("Not Applied");
			aoOrganizationBean.setMsOrgActiveFlag("false");
		}
		else if (aoMissingNameBean.getMsOrgCorpStructure().equalsIgnoreCase(
				ApplicationConstants.ORG_CORPORATE_NON_PROFIT))
		{
			if (aoMissingNameBean.getMsWorkFlowRequired())
			{
				aoOrganizationBean.setMsProcStatus("In Review");
				aoOrganizationBean.setMsOrgStatus("Not Applied");
				aoOrganizationBean.setMsOrgActiveFlag("false");
			}
			else
			{
				aoOrganizationBean.setMsProcStatus("Approved");
				aoOrganizationBean.setMsOrgStatus("Not Applied");
				aoOrganizationBean.setMsOrgActiveFlag("true");
				// Organization Creation Date change implemented.
				aoOrganizationBean.setMsOrgCreationDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
				List<ProviderBean> loProviderList = (List<ProviderBean>) BaseCacheManagerWeb.getInstance()
						.getCacheObject(ApplicationConstants.PROV_LIST);
				ProviderBean loProviderBean = new ProviderBean();
				loProviderBean.setHiddenValue(aoOrganizationBean.getMsOrgId());
				loProviderBean.setDisplayValue(StringEscapeUtils.escapeJavaScript(aoOrganizationBean
						.getMsOrgLegalName()));
				loProviderList.add(loProviderBean);
				synchronized (this)
				{
					BaseCacheManagerWeb.getInstance().putCacheObject(ApplicationConstants.PROV_LIST, loProviderList);
				}
			}
		}
		else
		{
			aoOrganizationBean.setMsOrgStatus("In Review");
		}
		return aoOrganizationBean;
	}

	/**
	 * This method is used to search user DN in siteMinder details table
	 * @param aoMyBatisSession to connect to database
	 * @param asUserDN used to search record
	 * @return loSiteMinderUserDetailMap contains user details(accelerator)
	 * @throws ApplicationException
	 */

	@SuppressWarnings("rawtypes")
	public Map getSiteMinderUserDetails(SqlSession aoMyBatisSession, String asUserDN) throws ApplicationException
	{
		Map loSiteMinderUserDetailMap = null;
		try
		{
			loSiteMinderUserDetailMap = (Map) DAOUtil.masterDAO(aoMyBatisSession, asUserDN,
					ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, "getSiteMinderUserDetails", "java.lang.String");
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while updating Email Address in Staff Details ", loAppEx);
			setMoState("Transaction Failed:: NycRegisterService:getSiteMinderUserDetails method - failed to search data based on UserDN in "
					+ "SiteMinder Details \n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: NycRegisterService:getSiteMinderUserDetails method - Successfully fetched data based on UserDN from "
				+ "SiteMinder Details \n + ");
		return loSiteMinderUserDetailMap;
	}

	/**
	 * This method is used to insert user details in Site Minder User Details
	 * creating new Organization account
	 * @param aoMyBatisSession to connect to database
	 * @param aoSiteMinderUserDetails
	 * @return Integer liInsertRowCount insert row count
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Integer insertIntoSiteMinderUserDetails(Integer aiNextSeq, Map aoSiteMinderUserDetailMap,
			SqlSession aoMyBatisSession) throws ApplicationException
	{
		int liInsertRowCount = 0;
		String lsCityUserId = "";

		String lsUserType = (String) aoSiteMinderUserDetailMap.get("UserType");
		if (lsUserType.toLowerCase().contains("agency"))
		{
			lsCityUserId = "agency_" + String.valueOf(aiNextSeq);
		}
		else
		{
			lsCityUserId = "city_" + String.valueOf(aiNextSeq);
		}
		try
		{
			aoSiteMinderUserDetailMap.put("SequenceId", lsCityUserId);
			liInsertRowCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoSiteMinderUserDetailMap,
					ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, "insertIntoSiteMinderUserDetails", "java.util.Map");
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while inserting Data in Site Minder User Details ", loAppEx);
			setMoState("Transaction Failed:: NycRegisterService:insertIntoSiteMinderUserDetails method - failed to insert Data in Site Minder"
					+ " User Details \n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: NycRegisterService:insertIntoSiteMinderUserDetails method - Successfully inserted data in Site Minder"
				+ " User Details \n + ");
		return liInsertRowCount;
	}

	/**
	 * This method getNextSeq FromSiteMinderUserDetails
	 * 
	 * @param aoMyBatisSession to connect to database
	 * @return Integer liNextSeq Sequence
	 * @throws ApplicationException
	 */
	public Integer getNextSeqFromSiteMinderUserDetails(SqlSession aoMyBatisSession) throws ApplicationException
	{
		int liNextSeq;
		try
		{
			liNextSeq = (Integer) DAOUtil.masterDAO(aoMyBatisSession, null,
					ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, "getNextSeqFromSiteMinderUserDetails", null);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while retreiving Next Sequence from Table", loAppEx);
			setMoState("Transaction Failed:: NycRegisterService:getNextSeqFromSiteMinderUserDetails method -Exception occured while while"
					+ " retreiving Next Sequence from Table: /n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: NycRegisterService:getNextSeqFromSiteMinderUserDetails method - Next Sequence from Table"
				+ " have been retreived successfully: /n");
		return liNextSeq;
	}

	/**
	 * This method searchZipCode
	 * 
	 * @param aoMyBatisSession to connect to database
	 * @param asZipCode ZipCode to search borough
	 * @return Map loMap
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public Map searchZipCode(SqlSession aoMyBatisSession, String asZipCode) throws ApplicationException
	{
		Map loMap = null;
		try
		{
			loMap = (Map) DAOUtil.masterDAO(aoMyBatisSession, asZipCode,
					ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, "searchZipCode", "java.lang.String");
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while retreiving Next Sequence from Table", loAppEx);
			setMoState("Transaction Failed:: NycRegisterService:getNextSeqFromSiteMinderUserDetails method -Exception occured while while "
					+ "retreiving Next Sequence from Table: /n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: NycRegisterService:getNextSeqFromSiteMinderUserDetails method - Next Sequence from Table have been "
				+ "retreived successfully: /n");
		return loMap;
	}

	/**
	 * This method addressValidation
	 * 
	 * @param asURL URL to hit address validation web service
	 * @return loBReader input stream from web service
	 * @throws ApplicationException
	 * @throws IOException
	 */
	public BufferedReader addressValidation(URL asURL) throws ApplicationException, IOException
	{
		LOG_OBJECT.Debug("Address Validation URL :::: " + asURL);
		URLConnection loUrlConnection = asURL.openConnection();
		BufferedReader loBReader = new BufferedReader(new InputStreamReader(loUrlConnection.getInputStream()));
		return loBReader;
	}

	/**
	 * This method addressValidation
	 * 
	 * @param aoMyBatisSession to connect to database
	 * @param aoCityUserDetailUpdateMap map to update City User Details
	 * @return liUpdateRowCount update row count
	 * @throws ApplicationException
	 */
	public Integer updateRoleInCityUserDetails(SqlSession aoMyBatisSession, Map aoCityUserDetailUpdateMap)
			throws ApplicationException
	{
		int liUpdateRowCount = 0;
		try
		{
			liUpdateRowCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoCityUserDetailUpdateMap,
					ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, "updateRoleInCityUserDetails", "java.util.Map");
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while updating role in city user details", loAppEx);
			setMoState("Transaction Failed:: NycRegisterService:updateRoleInCityUserDetails method -Exception occured while "
					+ "updating role in city user details: /n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: NycRegisterService:updateRoleInCityUserDetails method - role has been updated "
				+ "successfully in city user details : /n");
		return liUpdateRowCount;
	}

	/**
	 * This method update role in city user details
	 * 
	 * @param aoMyBatisSession to connect to database
	 * @param aoCityUserDetailUpdateMap map to update City User Details
	 * @return liUpdateRowCount update row count
	 * @throws ApplicationException
	 */
	public Integer updateRoleInCityUserDetailsForLdapBatch(SqlSession aoMyBatisSession,
			HashMap aoCityUserDetailUpdateMap) throws ApplicationException
	{
		int liUpdateRowCount = 0;
		HashMap loUpdateHashMap = (HashMap) aoCityUserDetailUpdateMap.get("aoCmUserDetailMap");
		try
		{
			liUpdateRowCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loUpdateHashMap,
					ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, "updateRoleInCityUserDetailsForLdapBatch",
					"java.util.Map");
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while updating role in city user details", loAppEx);
			setMoState("Transaction Failed:: NycRegisterService:updateRoleInCityUserDetails method -Exception occured while "
					+ "updating role in city user details: /n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: NycRegisterService:updateRoleInCityUserDetails method - role has been updated "
				+ "successfully in city user details : /n");
		return liUpdateRowCount;
	}

	/**
	 * This method is used to search user DN in siteMinder details table
	 * @param aoMyBatisSession to connect to database
	 * @param asUserDN used to search record
	 * @return loSiteMinderUserDetailMap contains user details(accelerator)
	 * @throws ApplicationException
	 */

	@SuppressWarnings("rawtypes")
	public Map getSiteMinderUserDetailsForLdapBatch(SqlSession aoMyBatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		Map loSiteMinderUserDetailMap = null;
		HashMap loHashMap = (HashMap) aoHashMap.get("aoCmUserDetailMap");
		String lsUserDN = (String) loHashMap.get("asCmUserDN");
		try
		{
			loSiteMinderUserDetailMap = (Map) DAOUtil.masterDAO(aoMyBatisSession, lsUserDN,
					ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, "getSiteMinderUserDetails", "java.lang.String");
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while updating Email Address in Staff Details ", loAppEx);
			setMoState("Transaction Failed:: NycRegisterService:getSiteMinderUserDetails method - failed to search data based on UserDN in "
					+ "SiteMinder Details \n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: NycRegisterService:getSiteMinderUserDetails method - Successfully fetched data based on UserDN from "
				+ "SiteMinder Details \n + ");
		return loSiteMinderUserDetailMap;
	}

	/**
	 * This method is used to insert user details in Site Minder User Details
	 * creating new Organization account
	 * @param aoMyBatisSession to connect to database
	 * @param aoSiteMinderUserDetails
	 * @return Integer liInsertRowCount insert row count
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Integer insertIntoSiteMinderUserDetailsForLdapBatch(SqlSession aoMyBatisSession, Integer aiNextSeq,
			Map aoSiteMinderUserDetailMap) throws ApplicationException
	{
		int liInsertRowCount = 0;
		String lsCityUserId = "";

		String lsUserType = (String) aoSiteMinderUserDetailMap.get("UserType");
		if (lsUserType.toLowerCase().contains("agency"))
		{
			lsCityUserId = "agency_" + String.valueOf(aiNextSeq);
		}
		else
		{
			lsCityUserId = "city_" + String.valueOf(aiNextSeq);
		}
		try
		{
			aoSiteMinderUserDetailMap.put("SequenceId", lsCityUserId);
			liInsertRowCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoSiteMinderUserDetailMap,
					ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, "insertIntoSiteMinderUserDetails", "java.util.Map");
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while inserting Data in Site Minder User Details ", loAppEx);
			setMoState("Transaction Failed:: NycRegisterService:insertIntoSiteMinderUserDetails method - failed to insert Data in Site Minder"
					+ " User Details \n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: NycRegisterService:insertIntoSiteMinderUserDetails method - Successfully inserted data in Site Minder"
				+ " User Details \n + ");
		return liInsertRowCount;
	}

	/**
	 * This method getNextSeq FromSiteMinderUserDetails
	 * 
	 * @param aoMyBatisSession to connect to database
	 * @return Integer liNextSeq Sequence
	 * @throws ApplicationException
	 */
	public Integer getNextSeqFromSiteMinderUserDetailsForLdapBatch(SqlSession aoMyBatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		int liNextSeq;
		try
		{
			liNextSeq = (Integer) DAOUtil.masterDAO(aoMyBatisSession, null,
					ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, "getNextSeqFromSiteMinderUserDetails", null);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while retreiving Next Sequence from Table", loAppEx);
			setMoState("Transaction Failed:: NycRegisterService:getNextSeqFromSiteMinderUserDetailsForLdapBatch method -Exception occured while while"
					+ " retreiving Next Sequence from Table: /n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: NycRegisterService:getNextSeqFromSiteMinderUserDetailsForLdapBatch method - Next Sequence from Table"
				+ " have been retreived successfully: /n");
		return liNextSeq;
	}

	/**
	 * This method de-activate internal users
	 * 
	 * @param aoMyBatisSession to connect to database
	 * @return Integer liNextSeq Sequence
	 * @throws ApplicationException
	 */
	public Integer deactivateInternalUser(SqlSession aoMyBatisSession, HashMap aoHashMap) throws ApplicationException
	{
		HashMap loHashMap = (HashMap) aoHashMap.get("aoCmUserDetailMap");
		int liNextSeq = 0;
		try
		{
			liNextSeq = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
					ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, "deactivateInternalUser", "java.util.Map");
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while deactivating internal users", loAppEx);
			setMoState("Transaction Failed:: NycRegisterService:deactivateInternalUser method -Exception occured while while"
					+ " deactivating internal users: /n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: NycRegisterService:deactivateInternalUser method - deactivating internal users"
				+ " have been retreived successfully: /n");
		return liNextSeq;
	}

	/**
	 * This method is created for R4 This method fetches Staff ID corresponding
	 * to the Input Email Address. Staff Is is retrieved corresponding to the
	 * user with the input Email address having NOT NULL USER_DN in Database.
	 * @param aoMyBatisSession - SQL Session
	 * @param asEmail - Input Email Address
	 * @return StafF ID
	 * @throws ApplicationException
	 */
	public String fetchStaffIdFromEmail(SqlSession aoMyBatisSession, String asEmail) throws ApplicationException
	{
		String lsStaffId = ApplicationConstants.EMPTY_STRING;
		try
		{
			lsStaffId = (String) DAOUtil.masterDAO(aoMyBatisSession, asEmail,
					ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, HHSConstants.QUERY_FETCH_STAFF_ID_FOR_EMAIL,
					HHSConstants.JAVA_LANG_STRING);
		}
		catch (ApplicationException loAppex)
		{
			LOG_OBJECT.Error("Exception occured while fetching Staff Id from Email Address", loAppex);
			setMoState("Transaction Failed:: NycRegisterService:fetchStaffIdFromEmail method"
					+ " - Exception occured while fetching Staff Id from Email Address: /n");
			throw loAppex;
		}
		return lsStaffId;
	}
         
}
