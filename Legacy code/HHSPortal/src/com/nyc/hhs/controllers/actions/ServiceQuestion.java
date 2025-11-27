package com.nyc.hhs.controllers.actions;

import java.sql.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.model.ContractDetails;
import com.nyc.hhs.model.NYCAgency;
import com.nyc.hhs.model.ServiceQuestions;
import com.nyc.hhs.model.StaffDetails;
import com.nyc.hhs.util.ApplicationSession;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.PortalUtil;

/**
 * This class sets the required values in the Channel object, required to
 * execute the transaction for the Adding/Updating/Displaying
 * Contract/Funder/Staff in the existing business application, to display the
 * existing services, or to save the services in the business application. Also
 * it sets the values, required in the in jsp, in the request object.
 * 
 */

public class ServiceQuestion extends BusinessApplication
{

	/**
	 * Gets the channel object for action
	 * 
	 * <li>This method was updated in R4</li>
	 * 
	 * @param asSectionName - current section name
	 * @param asOrgId - the organization id of the current organization
	 * @param asAppId - Business application id of the application
	 * @param asAppStatus - the current application status
	 * @param asAppDataForUpdate - data to be updated in application
	 * @param asAction - the action to be performed
	 * @param asUserRole - current user role
	 * @param aoRequest - Action request
	 * @param asTaxonomyName - taxonomy name to be used in factory
	 * @return the channel object to be used for further processing
	 * @throws ApplicationException
	 */
	@Override
	public Channel getChannelObject(String asSectionName, String asOrgId, String asAppId, String asAppStatus,
			String asAppDataForUpdate, String asAction, String asUserRole, ActionRequest aoRequest,
			String asTaxonomyName) throws ApplicationException
	{
		Channel loChannel = new Channel();
		PortletSession loSession = aoRequest.getPortletSession();
		String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		loChannel.setData("asUserId", lsUserId);
		// Checks if the action is selectValue then execute this block to
		// retrieve the details of the required contract/funder
		if (null != asAction && asAction.equalsIgnoreCase("selectValue"))
		{
			Map<String, String> loServiceInfoMap = new HashMap<String, String>();
			loServiceInfoMap.put("serviceAppId",
					PortalUtil.parseQueryString(aoRequest, ApplicationConstants.SERVICE_APPLICATION_ID));
			loServiceInfoMap.put("orgId", asOrgId);
			loChannel.setData("reqServiceInfo", loServiceInfoMap);
			String lsContractId = aoRequest.getParameter("selectBoxValue");
			Map<String, String> loContractInfo = new HashMap<String, String>();
			loContractInfo.put("asContractId", lsContractId);
			loContractInfo.put("orgId", asOrgId);
			loChannel.setData("aoContractId", lsContractId);
			loChannel.setData("aoContractInfo", loContractInfo);
			loChannel.setData("asOrgId", asOrgId);
			// Transactio'ired to retrieve the details of the required
			// contract/funder
			loChannel.setData("transaction_name", "contactDetailsById");
		}
		// Checks if the action is delSelectValue then execute this block to
		// delete the selected contract/funder
		if (null != asAction && asAction.equalsIgnoreCase("delSelectValue"))
		{
			String lsContractId = aoRequest.getParameter("selectBoxValue");
			loChannel.setData("contractId", lsContractId);
			loChannel.setData("asOrgId", asOrgId);
			loChannel.setData("asBusinessAppId", asAppId);
			loChannel.setData("asAppId", asAppId);
			loChannel.setData("asServiceAppId",
					PortalUtil.parseQueryString(aoRequest, ApplicationConstants.SERVICE_APPLICATION_ID));
			// Transaction required to delete the selected contract/funder
			loChannel.setData("transaction_name", "deleteSelectedContract");
			ApplicationSession.setAttribute("no", aoRequest, "typeNyc");
		}
		// block of code to be executed when user will select the existing staff
		// from the dropdown
		else if (null != asAction && asAction.equalsIgnoreCase("selectStaff"))
		{
			String lsStaffId = aoRequest.getParameter("selectBoxValue");
			loChannel.setData("msStaffId", lsStaffId);
			loChannel.setData("orgId", asOrgId);
			Map<String, String> loServiceInfoMap = new HashMap<String, String>();
			loServiceInfoMap.put("serviceAppId",
					PortalUtil.parseQueryString(aoRequest, ApplicationConstants.SERVICE_APPLICATION_ID));
			loServiceInfoMap.put("orgId", asOrgId);
			loChannel.setData("reqServiceInfo", loServiceInfoMap);
			// Transaction required to retrieve the details of the required
			// Staff
			// R4: Adding Map as parameter to provide additional data for
			// fetching data adding join to STAFF_ORGANIZATION_MAPPING table
			Map<String, String> loParamMap = new LinkedHashMap<String, String>();
			loParamMap.put("orgId", asOrgId);
			loParamMap.put(ApplicationConstants.MS_STAFF_ID, lsStaffId);
			loChannel.setData("loParamMap", loParamMap);
			loChannel.setData("transaction_name", "staffDetailsById");
		}// Checks if the action is delSelectStaff then execute this block to
			// delete the selected Staff
		else if (null != asAction && asAction.equalsIgnoreCase("delSelectStaff"))
		{
			String lsStaffId = aoRequest.getParameter("selectBoxValue");
			loChannel.setData("staffId", lsStaffId);
			loChannel.setData("asOrgId", asOrgId);
			loChannel.setData("asBusinessAppId", asAppId);
			loChannel.setData("asAppId", asAppId);
			loChannel.setData("staffId", lsStaffId);
			loChannel.setData("asServiceAppId",
					PortalUtil.parseQueryString(aoRequest, ApplicationConstants.SERVICE_APPLICATION_ID));
			// Transaction required to delete the selected Staff
			loChannel.setData("transaction_name", "deleteSelectedStaff");
		}
		// block of code to be executed when user will click on the save button
		// after filling up all the required fields of contracter/funder
		else if (asAction != null && asAction.equalsIgnoreCase("save"))
		{
			saveContractDetails(asOrgId, asAppId, asAppStatus, aoRequest, loChannel, asUserRole);
		}
		// block of code to be executed when user will click on the save button
		// after filling up all the required fields of staff
		else if (asAction != null && asAction.equalsIgnoreCase("saveStaff"))
		{
			saveStaffDetails(asSectionName, asOrgId, asAppId, asAppStatus, aoRequest, loChannel, asUserRole);

		}
		else if (asAction != null && asAction.equalsIgnoreCase("saveOrgMember"))
		{
			saveOrgMember(asOrgId, aoRequest, loChannel);
		}// Checks if the action is saveServiceQuestion then execute this block
		else if (null != asAction && asAction.equalsIgnoreCase("saveServiceQuestion"))
		{
			saveServiceQuestion(asSectionName, asOrgId, asAppId, aoRequest, loChannel);
		}
		return loChannel;
	}

	/**
	 * This method creates the channel object to save the service question in
	 * the database.
	 * 
	 * @param asSectionName - current section name
	 * @param asOrgId - the organization id of the current organization
	 * @param asAppId - Business application id of the application
	 * @param aoRequest - Action request
	 * @param loChannel - the channel object to be used for further processing
	 * @throws ApplicationException
	 */
	private void saveServiceQuestion(String asSectionName, String asOrgId, String asAppId, ActionRequest aoRequest,
			Channel aoChannel) throws ApplicationException
	{
		List<ServiceQuestions> loQuestionDetails = (List<ServiceQuestions>) ApplicationSession.getAttribute(
				aoRequest, true, "allQuestionDetails");
		String lsQues1 = aoRequest.getParameter("ques1");
		String lsQues2 = aoRequest.getParameter("ques2");
		String lsQues3 = aoRequest.getParameter("ques3");
		String lsSubSectionName = PortalUtil.parseQueryString(aoRequest,
				ApplicationConstants.BUZ_APP_PARAMETER_SUB_SECTION);
		Map<String, String> loQuesDocMap = new HashMap<String, String>();
		if (!(null != lsQues1 && lsQues1.equalsIgnoreCase("no") && null != lsQues2 && lsQues2.equalsIgnoreCase("no")
				&& null != lsQues3 && lsQues3.equalsIgnoreCase("no")))
		{
			String lsTypeNyc = "no";
			lsTypeNyc = (String) ApplicationSession.getAttribute(aoRequest, true, "typeNyc");

			if (lsQues1 != null && (lsTypeNyc == null || lsTypeNyc.equalsIgnoreCase("no"))
					&& lsQues1.equalsIgnoreCase("yes"))
			{
				loQuesDocMap.put("asDocType", ApplicationConstants.CONTRACT_GRANT_DOC_TYPE);
				loQuesDocMap.put("asDocCat", ApplicationConstants.SERVICES_DOC_CATEGORY);
				ApplicationSession.setAttribute("yes", aoRequest, "contractSel");
			}
			if (lsQues1 != null && lsTypeNyc != null && lsTypeNyc.equalsIgnoreCase("yes")
					&& lsQues1.equalsIgnoreCase("yes"))
			{
				loQuesDocMap.put("asDocType", ApplicationConstants.CONTRACT_GRANT_DOC_TYPE);
				loQuesDocMap.put("asDocCat", "");
			}
			if (lsQues2 != null && lsQues2.equalsIgnoreCase("yes"))
			{
				loQuesDocMap.put("asDocType", ApplicationConstants.KEY_STAFF_DOC_TYPE);
				loQuesDocMap.put("asDocCat", ApplicationConstants.ORG_BASICS_DOC_CATEGORY);
				ApplicationSession.setAttribute("yes", aoRequest, "staffSel");
			}
			if (lsQues3 != null && lsQues3.equalsIgnoreCase("yes"))
			{
				loQuesDocMap.put("asDocType", ApplicationConstants.CAPABILITY_STATEMENT_DOC_TYPE);
				loQuesDocMap.put("asDocCat", ApplicationConstants.SERVICES_DOC_CATEGORY);
				ApplicationSession.setAttribute("no", aoRequest, "noFunderStaff");
			}
			String lsNoFunderStaff = (String) ApplicationSession.getAttribute(aoRequest, true, "noFunderStaff");
			PortletSession loSession = aoRequest.getPortletSession();
			String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);
			String lsCurrentDate = DateUtil.getSqlDateddmmmyyyyFormat(DateUtil.getCurrentDate());
			aoChannel.setData("asQues1", lsQues1);
			aoChannel.setData("asQues2", lsQues2);
			aoChannel.setData("asQues3", lsQues3);
			aoChannel.setData("asOrgId", asOrgId);
			aoChannel.setData("asAppId", asAppId);
			aoChannel.setData("asServiceStatus", ApplicationConstants.COMPLETED_STATE);
			aoChannel.setData("asModifiedBy", lsUserId);
			aoChannel.setData("asModifiedDate", lsCurrentDate);
			aoChannel.setData("asBusinessAppId", asAppId);
			aoChannel.setData("asSectionId", asSectionName);
			aoChannel.setData("asSubSectionId", lsSubSectionName);
			aoChannel.setData("asUserId", lsUserId);
			aoChannel.setData("asSubSectionIdNextTab", asSectionName);
			aoChannel.setData("asServiceAppId",
					PortalUtil.parseQueryString(aoRequest, ApplicationConstants.SERVICE_APPLICATION_ID));
			aoChannel.setData("asDocStatus", ApplicationConstants.NOT_STARTED_STATE);
			aoChannel.setData("aoQuesDocMap", loQuesDocMap);
			if (lsNoFunderStaff != null && !lsNoFunderStaff.equalsIgnoreCase("yes"))
			{
				if (loQuestionDetails.isEmpty())
				{
					// Transaction required to insert the service question info
					// in the database
					aoChannel.setData("transaction_name", "insertServiceQuesInfo");
				}
				else
				{
					// Transaction required to update the service question info
					// in the database
					aoChannel.setData("transaction_name", "updateServiceQuesInfo");
				}
			}
			else if (lsNoFunderStaff != null && !lsNoFunderStaff.equalsIgnoreCase("no"))
			{
				ApplicationSession.setAttribute("You must add at least one Funder.", aoRequest, "message");
				ApplicationSession.setAttribute("yes", aoRequest, "messageType");
				// Transaction required to delete the service info
				// in the database when no funder/staff is deleted
				aoChannel.setData("transaction_name", "deleteServiceInfo");
			}
		}
	}

	/**
	 * This method creates the channel object to save the org member details in
	 * the database.
	 * 
	 * @param asOrgId - the organization id of the current organization
	 * @param aoRequest - Action request
	 * @param loChannel - the channel object to be used for further processing
	 */
	private void saveOrgMember(String asOrgId, ActionRequest aoRequest, Channel aoChannel)
	{
		// Fetching all the required data from the JSP
		String lsStaffFirstName = aoRequest.getParameter("staffFirstName");
		String lsStaffMidInitial = aoRequest.getParameter("staffMidInitial");
		String lsStaffLastName = aoRequest.getParameter("staffLastName");
		String lsStaffTitle = aoRequest.getParameter("staffTitle");
		String lsStaffPhone = aoRequest.getParameter("staffPhone");
		String lsStaffEmail = aoRequest.getParameter("staffEmail");

		// bean object to be set in the channel object
		StaffDetails loStaffDetails = new StaffDetails();
		loStaffDetails.setMsStaffFirstName(lsStaffFirstName);
		loStaffDetails.setMsStaffMidInitial(lsStaffMidInitial);
		loStaffDetails.setMsStaffLastName(lsStaffLastName);
		loStaffDetails.setMsStaffTitle(lsStaffTitle);
		loStaffDetails.setMsStaffPhone(lsStaffPhone);
		loStaffDetails.setMsStaffEmail(lsStaffEmail);
		loStaffDetails.setMsStaffActiveFlag("Yes");
		loStaffDetails.setMsOrgId(asOrgId);
		loStaffDetails.setMsSystemUser("No");
		loStaffDetails.setMsMemberStatus("InActive");
		// setting up the transaction name
		aoChannel.setData("newStaff", loStaffDetails);
		aoChannel.setData("orgId", asOrgId);
		// Transaction required to insert the organization member details
		aoChannel.setData("transaction_name", "insertOrgMemberDetails");
	}

	/**
	 * This method creates the channel object to save the staff details in the
	 * database.
	 * 
	 * @param asSectionName - current section name
	 * @param asOrgId - the organization id of the current organization
	 * @param asAppId - Business application id of the application
	 * @param asAppStatus - the current application status
	 * @param aoRequest - Action request
	 * @param loChannel - the channel object to be used for further processing
	 * @throws ApplicationException
	 */
	private void saveStaffDetails(String asSectionName, String asOrgId, String asAppId, String asAppStatus,
			ActionRequest aoRequest, Channel aoChannel, String asUserRole) throws ApplicationException
	{
		StaffDetails loStaffDetails = setStaffInfoInBean(asOrgId, aoRequest);
		loStaffDetails.setMsCreatedBy(asUserRole);
		// setting up the transaction name
		String lsTransactionName = "insertStaffDetails";
		String lsExistingStaffId = aoRequest.getParameter("existingStaff");
		String lsStaffId = "";
		Boolean lbIsExisingStaff = false;
		if (lsExistingStaffId != null && !(lsExistingStaffId.equalsIgnoreCase("-1")))
		{
			lbIsExisingStaff = true;
			// setting up the transaction name
			lsTransactionName = "updateServiceStaffDetails";
			loStaffDetails.setMsStaffId(lsExistingStaffId);
			loStaffDetails.setMsCreatedBy(asUserRole);
			aoChannel.setData("existingStaff", loStaffDetails);
			lsStaffId = lsExistingStaffId;
		}
		else
		{
			aoChannel.setData("newStaff", loStaffDetails);
		}
		Map<String, Object> loStaffMapping = new HashMap<String, Object>();
		loStaffMapping.put("msStaffId", lsStaffId);
		loStaffMapping.put("oldTitle", aoRequest.getParameter("oldTitle"));
		loStaffMapping.put("msOrgId", asOrgId);
		loStaffMapping.put("msServiceAppId", asAppStatus);
		loStaffMapping.put("msAppId", asAppId);
		loStaffMapping.put("msCreatedBy", asUserRole);
		loStaffMapping.put("msStaffAddedOn", new Date(System.currentTimeMillis()));
		aoChannel.setData("newStaffMapping", loStaffMapping);
		// Transaction required to insert/update details of the staff in the
		// database.
		aoChannel.setData("transaction_name", lsTransactionName);
		if (lsTransactionName.equalsIgnoreCase("insertStaffDetails"))
		{
			// using dummy section name just to set the transaction name and
			// channel data
			if ("getRecentlyAddedStaff".equalsIgnoreCase(asSectionName))
			{
				// setting up the transaction name
				lsTransactionName = "getRecentlyAddedStaffId";
				// Transaction required to retrieve the details of the
				// recently added staff
				aoChannel.setData("transaction_name", lsTransactionName);
			}
			// using dummy section name just to set the transaction name and
			// channel data
			if (asSectionName != null && !"getRecentlyAddedStaff".equalsIgnoreCase(asSectionName))
			{
				loStaffMapping.put("msStaffId", asSectionName);
				aoChannel.setData("newStaffMapping", loStaffMapping);
				// setting up the transaction name
				lsTransactionName = "insertStaffMapping";
				// Transaction required to insert the staff mapping in the
				// database
				aoChannel.setData("transaction_name", lsTransactionName);
			}
		}

		String lsAppSettingMapKey = ApplicationConstants.APPLICATION_STAFF_VIEW_COMPONENT + "_"
				+ ApplicationConstants.APPLICATION_STAFF_VIEW_PER_PAGE;
		HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb.getInstance()
				.getCacheObject(ApplicationConstants.APPLICATION_SETTING);
		aoRequest.getPortletSession().setAttribute("allowedObjectCount",
				Integer.valueOf(loApplicationSettingMap.get(lsAppSettingMapKey)), PortletSession.APPLICATION_SCOPE);
		aoChannel.setData("abisExisingStaff", lbIsExisingStaff);
	}

	/**
	 * This method sets the staff details in the staffdetails bean object
	 * 
	 * @param asOrgId - the organization id of the current organization
	 * @param aoRequest - Action request
	 * @return loStaffDetails - the StaffDetails bean object
	 */
	private StaffDetails setStaffInfoInBean(String asOrgId, ActionRequest aoRequest)
	{
		String lsStaffFirstName = aoRequest.getParameter("staffFirstName");
		String lsStaffMidInitial = aoRequest.getParameter("staffMidInitial");
		String lsStaffLastName = aoRequest.getParameter("staffLastName");
		String lsStaffTitle = aoRequest.getParameter("staffTitle");
		String lsStaffPhone = aoRequest.getParameter("staffPhone");
		String lsStaffEmail = aoRequest.getParameter("staffEmail");
		StaffDetails loStaffDetails = new StaffDetails();
		loStaffDetails.setMsStaffFirstName(lsStaffFirstName);
		loStaffDetails.setMsStaffMidInitial(lsStaffMidInitial);
		loStaffDetails.setMsStaffLastName(lsStaffLastName);
		loStaffDetails.setMsStaffTitle(lsStaffTitle);
		loStaffDetails.setMsStaffPhone(lsStaffPhone);
		loStaffDetails.setMsStaffEmail(lsStaffEmail);
		loStaffDetails.setMsStaffActiveFlag("No");
		loStaffDetails.setMsOrgId(asOrgId);
		loStaffDetails.setMsSystemUser("No");
		loStaffDetails.setMsMemberStatus("Active");
		loStaffDetails.setMsUserStatus("No");
		loStaffDetails.setOperationType("insertStaff");
		loStaffDetails.setMsUserAcctCreationDate(new Date(System.currentTimeMillis()));
		return loStaffDetails;
	}

	/**
	 * This method creates the channel object to save the contract details in
	 * the database.
	 * 
	 * @param asOrgId - the organization id of the current organization
	 * @param asAppId - Business application id of the application
	 * @param asAppStatus - the current application status
	 * @param aoRequest - Action request
	 * @param loChannel - the channel object to be used for further processing
	 * @throws ApplicationException
	 */
	private void saveContractDetails(String asOrgId, String asAppId, String asAppStatus, ActionRequest aoRequest,
			Channel aoChannel, String asUserRole) throws ApplicationException
	{

		String lsExistingContractId = aoRequest.getParameter("selectBoxValue");
		String lsCheckForId = (String) aoRequest.getAttribute("checkForId");
		String lsCheckId = aoRequest.getParameter("checkId");
		String lsTransactionName = "";
		if ((lsExistingContractId == null || lsExistingContractId.equalsIgnoreCase("-1") || (lsCheckId != null && lsCheckId
				.equalsIgnoreCase("check"))) && lsCheckForId.equalsIgnoreCase(""))
		{
			String lsContractId = aoRequest.getParameter("msContractId");
			String lsFunderName = aoRequest.getParameter("contractFunderName");
			if (lsFunderName == null || lsFunderName.equalsIgnoreCase("null"))
			{
				lsFunderName = "";
			}
			String lsNYCAgency = aoRequest.getParameter("contractNYCAgency");
			if (lsNYCAgency == null || lsNYCAgency.equalsIgnoreCase("null"))
			{
				lsNYCAgency = "";
			}
			String lsProgramName = aoRequest.getParameter("contractDescription");
			Map<String, String> loContractInfo = new HashMap<String, String>();
			Map<String, String> loServiceInfoMap = new HashMap<String, String>();
			loServiceInfoMap.put("serviceAppId",
					PortalUtil.parseQueryString(aoRequest, ApplicationConstants.SERVICE_APPLICATION_ID));
			loServiceInfoMap.put("orgId", asOrgId);
			aoChannel.setData("reqServiceInfo", loServiceInfoMap);
			loContractInfo.put("asContractId", lsContractId);
			loContractInfo.put("orgId", asOrgId);
			loContractInfo.put("contractFunderName", lsFunderName);
			loContractInfo.put("contractNYCAgency", lsNYCAgency);
			loContractInfo.put("contractDescription", lsProgramName);
			aoChannel.setData("aoContractId", lsContractId);
			aoChannel.setData("aoContractInfo", loContractInfo);
			aoChannel.setData("asOrgId", asOrgId);
			// Transaction required to delete the selected Staff
			aoChannel.setData("transaction_name", "checkExistingContractId");
		}
		else
		{
			String lsFunderName = aoRequest.getParameter("contractFunderName");

			if (lsFunderName == null || lsFunderName.equalsIgnoreCase("null"))
			{
				lsFunderName = "";
			}
			String lsNYCAgency = aoRequest.getParameter("contractNYCAgency");
			if (lsNYCAgency == null || lsNYCAgency.equalsIgnoreCase("null"))
			{
				lsNYCAgency = "";
			}
			String lsProgramName = aoRequest.getParameter("contractDescription");

			Date loContractStartDate = DateUtil.getSqlDate(aoRequest.getParameter("contractStartDate"));
			Date loContractEndDate = DateUtil.getSqlDate(aoRequest.getParameter("contractEndDate"));

			Boolean lbValidPeriod = true;
			lbValidPeriod = (loContractEndDate.after(loContractStartDate) || loContractEndDate
					.equals(loContractStartDate));
			if (lbValidPeriod)
			{
				lsTransactionName = "insertContractDetails";
				Boolean lbContractCheckFlag = true;
				if (lsExistingContractId != null && !lsExistingContractId.equalsIgnoreCase("-1")
						&& !lsExistingContractId.equalsIgnoreCase("null"))
				{
					String lsOldContractId = aoRequest.getParameter("oldContractId");
					String lsOldFunderName = aoRequest.getParameter("oldFunderName");
					String lsOldNYCAgency = aoRequest.getParameter("oldNYCAgency");
					String lsOldProgramName = aoRequest.getParameter("oldProgramName");
					String lsContractId = aoRequest.getParameter("msContractId");
					if ((lsOldContractId != null && lsOldContractId.equalsIgnoreCase(lsContractId))
							&& ((lsOldFunderName != null && lsOldFunderName.equalsIgnoreCase(lsFunderName)) || (lsOldNYCAgency != null && lsOldNYCAgency
									.equalsIgnoreCase(lsNYCAgency)))
							&& (lsOldProgramName != null && lsOldProgramName.equalsIgnoreCase(lsProgramName)))
					{
						lbContractCheckFlag = false;
					}
					lsTransactionName = "updateContractDetails";
				}
				// Fetching all the required data from the JSP
				String lsContractId = aoRequest.getParameter("msContractId");
				String lsContractType = aoRequest.getParameter("contractType");
				ContractDetails loContractDetails = setContractInfoInBean(asOrgId, aoRequest, lsExistingContractId,
						lsContractId, lsContractType, asUserRole);
				loContractDetails.setMsContractDescription(lsProgramName);
				loContractDetails.setMsContractFunderName(lsFunderName);
				loContractDetails.setMsContractNYCAgency(lsNYCAgency);
				Map<String, Object> loContractMapping = new HashMap<String, Object>();
				loContractMapping.put("msContractID", lsContractId);
				loContractMapping.put("msOrgId", asOrgId);
				loContractMapping.put("msAppId", asAppId);
				loContractMapping.put("msServiceAppId", asAppStatus);
				loContractMapping.put("msContractAddedOn", new Date(System.currentTimeMillis()));
				loContractDetails.setMsContractDetailsId(lsExistingContractId);
				loContractDetails.setMsOldContractID(lsExistingContractId);
				loContractMapping.put("msOldContractID", lsExistingContractId);
				loContractMapping.put("msContractDetailsId", lsExistingContractId);
				loContractMapping.put("msCreatedBy", asUserRole);
				loContractMapping.put("contractCheckFlag", lbContractCheckFlag);
				if ((lsExistingContractId != null && (lsExistingContractId.equalsIgnoreCase("-1") || lsExistingContractId
						.equalsIgnoreCase("null"))))
				{
					aoChannel.setData("newContract", loContractDetails);
				}
				else
				{
					aoChannel.setData("existingContract", loContractDetails);
				}
				aoChannel.setData("newContractMapping", loContractMapping);
				// Transaction required to insert/update the details
				// contract/funder in the database.
				aoChannel.setData("transaction_name", lsTransactionName);
				String lsAppSettingMapKey = ApplicationConstants.APPLICATION_CONTRACT_VIEW_COMPONENT + "_"
						+ ApplicationConstants.APPLICATION_CONTRACT_VIEW_PER_PAGE;
				HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb
						.getInstance().getCacheObject(ApplicationConstants.APPLICATION_SETTING);
				aoRequest.getPortletSession().setAttribute("allowedObjectCount",
						Integer.valueOf(loApplicationSettingMap.get(lsAppSettingMapKey)),
						PortletSession.APPLICATION_SCOPE);
			}
			else
			{
				ApplicationSession.setAttribute("true", aoRequest, "lbValidPeriod");
			}
		}
	}

	/**
	 * This method sets the contract details in the contractDetails bean object
	 * 
	 * @param asOrgId - the organization id of the current organization
	 * @param aoRequest - Action request
	 * @param lsExistingContractId - existing contract id
	 * @param msContractId - contract id
	 * @param msContractType - contract type
	 * @return aoContractDetails the ContractDetails bean object
	 * @throws ApplicationException
	 */
	private ContractDetails setContractInfoInBean(String asOrgId, ActionRequest aoRequest, String asExistingContractId,
			String asContractId, String asContractType, String asUserRole) throws ApplicationException
	{
		String lsContractNYCAgency = aoRequest.getParameter("contractNYCAgency");
		String lsContractFunderName = aoRequest.getParameter("contractFunderName");
		String lsContractRefFirstName = aoRequest.getParameter("contractRefFirstName");
		String lsContractRefMidName = aoRequest.getParameter("contractRefMidName");
		String lsContractRefLastName = aoRequest.getParameter("contractRefLastName");
		String lsContractRefTitle = aoRequest.getParameter("contractRefTitle");
		String lsContractRefPhone = aoRequest.getParameter("contractRefPhone");
		String lsContractRefEmail = aoRequest.getParameter("contractRefEmail");
		String lsContractDescription = aoRequest.getParameter("contractDescription");
		Date loContractStartDate = DateUtil.getSqlDate(aoRequest.getParameter("contractStartDate"));
		Date loContractEndDate = DateUtil.getSqlDate(aoRequest.getParameter("contractEndDate"));
		String lsContractBudget = aoRequest.getParameter("contractBudget");
		String lsOrgId = asOrgId;
		// bean object to be set in the channel object
		ContractDetails loContractDetails = new ContractDetails();
		loContractDetails.setMsContractID(asContractId);
		loContractDetails.setMsContractType(asContractType);
		loContractDetails.setMsContractNYCAgency(lsContractNYCAgency);
		loContractDetails.setMsContractFunderName(lsContractFunderName);
		loContractDetails.setMsContractRefFirstName(lsContractRefFirstName);
		loContractDetails.setMsContractRefMidName(lsContractRefMidName);
		loContractDetails.setMsContractRefLastName(lsContractRefLastName);
		loContractDetails.setMsContractRefTitle(lsContractRefTitle);
		loContractDetails.setMsContractRefPhone(lsContractRefPhone);
		loContractDetails.setMsContractRefEmail(lsContractRefEmail);
		loContractDetails.setMsContractDescription(lsContractDescription);
		loContractDetails.setMsContractBudget(lsContractBudget);
		loContractDetails.setMsOrgId(lsOrgId);
		loContractDetails.setMsContractStartDate(loContractStartDate);
		loContractDetails.setMsContractEndDate(loContractEndDate);
		loContractDetails.setMsCreatedBy(asUserRole);
		return loContractDetails;
	}

	/**
	 * Gets the channel object for render
	 * 
	 * @param asSectionName - current section name
	 * @param asOrgId - the organization id of the current organization
	 * @param asAppId - Business application id of the application
	 * @param asAppStatus - the current application status
	 * @param asAppDataForUpdate - data to be updated in application
	 * @param asAction - the action to be performed
	 * @param asUserRole - current user role
	 * @param aoRequest - Render request
	 * @param asTaxonomyName - taxonomy name to be used in factory
	 * @return the channel object to be used for further processing
	 * @throws ApplicationException
	 */
	@Override
	public Channel getChannelObject(String asSectionName, String asOrgId, String asAppId, String asAppStatus,
			String asAppDataForUpdate, String asAction, String asUserRole, RenderRequest aoRequest,
			String asTaxonomyName) throws ApplicationException
	{
		Channel loChannel = new Channel();
		// Checks if the action is addservice then execute this block
		if (asAction != null && asAction.equalsIgnoreCase("addservice"))
		{

			loChannel.setData(ApplicationConstants.BUSINESS_APPLICATION_ID, asAppId);
			loChannel.setData(ApplicationConstants.NEW_ORGANIZATION_ID, asOrgId);
			loChannel.setData(ApplicationConstants.ELEMENT_TYPE, ApplicationConstants.SERVICE_AREA);
			loChannel.setData("abFromCache", ApplicationConstants.FALSE);

		}// Checks if the action is showServiceQuestion then execute this block
		else if (asAction != null && asAction.equalsIgnoreCase("showServiceQuestion"))
		{
			Map<String, String> loQuestionMap = new HashMap<String, String>();
			Map<String, String> loServiceInfoMap = new HashMap<String, String>();
			loServiceInfoMap.put("serviceAppId",
					PortalUtil.parseQueryString(aoRequest, ApplicationConstants.SERVICE_APPLICATION_ID));
			loServiceInfoMap.put("orgId", asOrgId);
			loChannel.setData("reqServiceInfo", loServiceInfoMap);
			loQuestionMap.put("asOrgId", asOrgId);
			loQuestionMap.put("asAppId", asAppId);
			loQuestionMap.put("asBusinessAppid", asAppId);
			loQuestionMap.put("asServiceAppId",
					PortalUtil.parseQueryString(aoRequest, ApplicationConstants.SERVICE_APPLICATION_ID));
			loChannel.setData("aoQuestionDetails", loQuestionMap);
			// Transaction required to display the question page of the service.
			loChannel.setData("transaction_name", "serviceQuestionView");
		}// Checks if the action is addContract then execute this block
		else if (null != asAction && asAction.equalsIgnoreCase("addContract"))
		{
			Map<String, String> loServiceInfoMap = new HashMap<String, String>();
			loServiceInfoMap.put("serviceAppId",
					PortalUtil.parseQueryString(aoRequest, ApplicationConstants.SERVICE_APPLICATION_ID));
			loServiceInfoMap.put("orgId", asOrgId);
			loChannel.setData("reqServiceInfo", loServiceInfoMap);
			loChannel.setData("asOrgId", asOrgId);
			// Transaction required to retrieve the details of all the
			// contract/funder
			loChannel.setData("transaction_name", "allContractDetails");
		}// Checks if the action is addStaff then execute this block
		else if (null != asAction && asAction.equalsIgnoreCase("addStaff"))
		{
			Map<String, String> loServiceInfoMap = new HashMap<String, String>();
			loServiceInfoMap.put("serviceAppId",
					PortalUtil.parseQueryString(aoRequest, ApplicationConstants.SERVICE_APPLICATION_ID));
			loServiceInfoMap.put("orgId", asOrgId);
			loChannel.setData("reqServiceInfo", loServiceInfoMap);
			// Transaction required to retrieve the details of all the Staff
			loChannel.setData("orgId", asOrgId);
			loChannel.setData("transaction_name", "allStaffDetails");
			// Checks if the action is displayOrgMember then execute this block
		}
		else if (asAction != null && asAction.equalsIgnoreCase("displayOrgMember"))
		{
			loChannel.setData("orgId", asOrgId);
			// Transaction required to retrieve the details of all the Staff to
			// be displayed on the grid
			loChannel.setData("transaction_name", "getOrgMemberListForGrid");
		}
		// Checks if the action is editOrgMember then execute this block
		else if (asAction != null && asAction.equalsIgnoreCase("editOrgMember"))
		{
			// Adding value to map and passing it to transaction to Pass
			// additional Org ID for new table join - STAFF_ORGANIZATION_MAPPING
			Map<String, String> loParamMap = new HashMap<String, String>();
			loParamMap.put("msStaffId", aoRequest.getParameter("editOrgMemberId"));
			loParamMap.put("orgId", asOrgId);
			loChannel.setData("loParamMap", loParamMap);
			// Transaction required to retrieve the details of the required
			// Organization member
			loChannel.setData("transaction_name", "getOrgMemberById");
		}
		return loChannel;
	}

	/**
	 * This method fetches the map to be rendered
	 * 
	 * @param asAction - the action to be performed
	 * @param asSectionName - current section name
	 * @param aoChannel - channel object with data, will be needed to be passed
	 *            to MapForRender
	 * @param aoRequest - Render Request
	 * @return the map to be rendered on front end
	 * @throws ApplicationException
	 */
	@Override
	public Map<String, Object> getMapForRender(String asAction, String asSectionName, Channel aoChannel,
			RenderRequest aoRequest) throws ApplicationException
	{

		Map<String, Object> loMapForRender = new HashMap<String, Object>();
		// Checks if the action is addContract then execute this block
		if (asAction != null && asAction.equalsIgnoreCase("addContract"))
		{
			List<ContractDetails> loContractList = (List<ContractDetails>) aoChannel
					.getData("allContractDetailsOutput");
			List<NYCAgency> loAgencyList = (List<NYCAgency>) aoChannel.getData("allNYCAgencyDetailsOutput");
			loMapForRender.put("getValue", loContractList);
			loMapForRender.put("getNYCAgency", loAgencyList);
		}// Checks if the action is addStaff then execute this block
		else if (asAction != null && asAction.equalsIgnoreCase("addStaff"))
		{
			List<StaffDetails> loStaffList = (List<StaffDetails>) aoChannel.getData("allStaffDetailsOutput");
			loMapForRender.put("getValue", loStaffList);
		}// Checks if the action is showServiceQuestion then execute this block
		else if (asAction != null && asAction.equalsIgnoreCase("showServiceQuestion"))
		{
			List<ContractDetails> loContractList = (List<ContractDetails>) aoChannel
					.getData("allContractDetailsForGrid");
			if (null != loContractList)
			{
				for (ContractDetails loContractDetails : loContractList)
				{
					if (loContractDetails.getMsContractFunderName() == null
							|| loContractDetails.getMsContractFunderName().equalsIgnoreCase("")
							|| loContractDetails.getMsContractFunderName().equalsIgnoreCase("null"))
					{
						loContractDetails.setMsContractFunderName(loContractDetails.getMsContractNYCAgency());
					}
				}
			}
			List<StaffDetails> loStaffDetailsList = (List<StaffDetails>) aoChannel.getData("allStaffDetailsForGrid");
			List<ServiceQuestions> loQuestionDetails = (List<ServiceQuestions>) aoChannel
					.getData("allQuestionDetails");
			String lsTempSaveStaff = (String) aoRequest.getAttribute("saveStaff");
			if ((lsTempSaveStaff != null && lsTempSaveStaff.equalsIgnoreCase("saveStaff")))
			{
				if (loQuestionDetails == null || !loQuestionDetails.isEmpty())
				{
					aoRequest.setAttribute("contractSel", null);
					aoRequest.setAttribute("staffSel", null);
				}
			}

			ApplicationSession.setAttribute(loQuestionDetails, aoRequest, "allQuestionDetails");
			loMapForRender.put("aoContractList", loContractList);

			/*
			 * bug fix defect 1289
			 */
			if (loStaffDetailsList != null)
			{
				for (StaffDetails loStaffDetails : loStaffDetailsList)
				{

					String lsFullName = loStaffDetails.getMsStaffFirstName();
					String lsMiddleInitial = loStaffDetails.getMsStaffMidInitial();
					String lsLastName = loStaffDetails.getMsStaffLastName();
					if (lsFullName != null)
					{
						if (lsMiddleInitial != null)
						{
							lsFullName = new StringBuffer(lsFullName).append(" ").append(lsMiddleInitial).toString();
						}
						if (lsLastName != null)
						{
							lsFullName = new StringBuffer(lsFullName).append(" ").append(lsLastName).toString();
						}

					}
					loStaffDetails.setMsStaffFirstName(lsFullName);
				}
			}

			loMapForRender.put("aoStaffDetailsList", loStaffDetailsList);
			if (loContractList != null && loContractList.isEmpty() && loStaffDetailsList != null
					&& loStaffDetailsList.isEmpty())
			{
				ApplicationSession.setAttribute("yes", aoRequest, "noFunderStaff");
			}
			else
			{
				ApplicationSession.setAttribute("no", aoRequest, "noFunderStaff");
			}
			ApplicationSession.setAttribute(loStaffDetailsList, aoRequest, "staff_list");
			loMapForRender.put("aoallQuestionDetails", loQuestionDetails);
			// Checks if the action is displayOrgMember then execute this block
		}
		else if (asAction != null && asAction.equalsIgnoreCase("displayOrgMember"))
		{
			List<StaffDetails> loOrgMemberList = (List<StaffDetails>) aoChannel.getData("allOrgMemberListForGrid");
			loMapForRender.put("loOrgMemberList", loOrgMemberList);
			// Checks if the action is saveAndDisplayStaff then execute this
			// block
		}
		else if (asAction != null && asAction.equalsIgnoreCase("saveAndDisplayStaff"))
		{
			loMapForRender.put("loOrgMemberList", aoRequest.getAttribute("displayListAfterSave"));
		}// Checks if the action is editOrgMember then execute this block
		else if (asAction != null && asAction.equalsIgnoreCase("editOrgMember"))
		{
			loMapForRender.put("displayEditOrgMember", aoRequest.getAttribute("displayEditOrgMember"));
		}
		return loMapForRender;
	}

	/**
	 * This method fetches the map to be rendered
	 * 
	 * @param asAction - the action to be performed
	 * @param asSectionName - current section name
	 * @param aoChannel - channel object with data, will be needed to be passed
	 *            to MapForRender
	 * @param aoRequest - Action Request
	 * @return the map to be rendered on front end
	 * @throws ApplicationException
	 */
	@Override
	public Map<String, Object> getMapForRender(String asAction, String asSectionName, Channel aoChannel,
			ActionRequest aoRequest) throws ApplicationException
	{
		return null;
	}
}
