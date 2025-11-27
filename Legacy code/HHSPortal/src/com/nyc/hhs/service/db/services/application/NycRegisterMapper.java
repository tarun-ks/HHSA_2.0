package com.nyc.hhs.service.db.services.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.nyc.hhs.model.EINBean;
import com.nyc.hhs.model.MissingNameBean;
import com.nyc.hhs.model.OrgStaffDetailBean;
import com.nyc.hhs.model.OrganizationBean;
import com.nyc.hhs.model.RegisterNycIdBean;
import com.nyc.hhs.model.SecurityQuestionBean;
import com.nyc.hhs.model.StaffDetails;
import com.nyc.hhs.model.UserBean;

/**
 *  NycRegisterMapper is an interface between the DAO and database layer 
 *  to map the methods for NYC register process.
 *  
 */

public interface NycRegisterMapper {

	ArrayList<SecurityQuestionBean> getSecurityQuestions();
	void insertNYCUserData(RegisterNycIdBean aoRegisterNycIdBean);
	EINBean searchEIN (String asEIN);
	Integer insertIntoOrgAccountDetails(OrganizationBean aoOrganizationBean);
	String getStaffIdSequence();
	Integer insertIntoStaffDetails(StaffDetails aoStaffDetails);
	Integer insertIntoStaffOrgMapping(StaffDetails aoStaffDetails);
	
	List<StaffDetails> getUserOrgDetails(StaffDetails aoStaffDetails);
	
	
	List<OrgStaffDetailBean> getMissingDetails(String  asOrgId);
	
	Integer updateStaffEmail(StaffDetails aoStaffDetails);
	StaffDetails searchUserDnInStaffDetails(MissingNameBean aoMissingNameBean);
	
	Integer insertIntoEinMaster(EINBean aoEINBean);
	
	StaffDetails ceoCheckInStaffDetails(StaffDetails aoStaffDetails);
	
	Integer updateTCinStaffDetails(StaffDetails aoStaffDetails);

	Integer updateUserProfileinStaffDetails(Map aoUserDetailsMap);
	
    Integer getCurrentSeqFromTable();
    Integer getNextSeqFromTable();
    
    Integer getCurrentSeqFromStaff();
    Integer getNextSeqFromStaff();
    OrganizationBean searchEinInOrg (String asEIN);
    
    Integer updateLastLoginDate(Map<String,Object> aoUserDetailsMap);
    Integer getNextSeqFromSiteMinderUserDetails();
    Integer insertIntoSiteMinderUserDetails(Map aoSiteMinderUserDetailMap);
    Map getSiteMinderUserDetails(String asUserDn);
    
	StaffDetails searchStaffIdInStaffDetails(StaffDetails aoStaffDetails);
	Map searchZipCode(String asZipCode);
	
	int updateRoleInCityUserDetails(Map aoCityUserDetailUpdateMap);
	StaffDetails checkUserDnInStaffDetails(UserBean aoUserBean);
	int getPendingRequestCount(StaffDetails aoStaffDetails);

	//R4 - Added New Queries for Multiple Account Access
	List<StaffDetails> getUserOrgDetailsMultiAccount(StaffDetails aoStaffDetails);
	
	List<StaffDetails> searchUserOnEmailId(StaffDetails aoStaffDetails);
	
	Integer insertSubmitAccessRequestProvider(StaffDetails aoStaffDetails);
	
	Integer updateStaffMappingForSubmitAccessRequest(StaffDetails aoStaffDetails);
	
	List<StaffDetails> getStaffDetailsFromId(String asStaffId);
	//R4 Queries for Multiple Account Access Ends
	
	String fetchStaffIdFromEmail (String asEmailId);
	
	int updateRoleInCityUserDetailsForLdapBatch(Map aoCityUserDetailUpdateMap);
	
	Map getSiteMinderUserDetailsForLdapBatch(String asUserDN);
	
	int deactivateInternalUser(Map loHashMap);
	
	/* [Start] R7.8.0 SAML check Email update  */
	Integer  checkProviderNycIdUpdate(StaffDetails aoStaffDetails);
    /* [End] R7.8.0 SAML check Email update  */
	
	/* [Start] R8.2.0 QC9531 cleaning Dup user_dn */
	List<StaffDetails>  fetchDupBothUserNOrg();
	List<StaffDetails>  fetchDupUserDNOnly();

	Integer removeDupUserDn( StaffDetails aoStaffDetails );
	Integer replaceStaffIdBaseOrg( StaffDetails aoStaffDetails );
    /* [End] R8.2.0 QC9531 cleaning Dup user_dn  */
	
	
	
}



