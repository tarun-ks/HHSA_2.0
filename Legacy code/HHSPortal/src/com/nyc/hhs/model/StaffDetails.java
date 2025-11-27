package com.nyc.hhs.model;

import java.sql.Date;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

import com.nyc.hhs.constants.HHSConstants;

/**
 * This class is a bean which maintains the Staff Details.
 * 
 */

public class StaffDetails
{

	private String msStaffFirstName = "";
	private String msStaffMidInitial = "";
	private String msStaffLastName = "";
	private String msStaffTitle = "";
	private String msStaffPhone = "";
	private String msStaffEmail = "";
	@Length(max = 20)
	private String msStaffId = "";
	private String msStaffActiveFlag = "";
	private String msStaffRole = "";
	private String msSystemUser = "";
	private String msMemberStatus = "";
	private String msOrgId = "";
	private String msActions = "";

	private String readOnly = "";
	private String operationType;
	private Boolean isAdminUser = false;
	private String msNYCUserId;
	private String msUserStatus;
	private Date msMemberInactiveDate = null;
	private String msPermissionLevel = "";
	private String msAdminPermission = "";

	private String msTermConditionStatus;

	private String msOrgActiveFlag;
	private String msUserDN = "";
	private Date msUserAcctCreationDate;

	private String msOrgProcStatus = "";

	private String msDuplicate = "";

	private String memberAsUser = null;

	private String deActivatedUser = null;
	private String msOrganisationName = "";
	private String msUserType = "";

	private String msName = "";
	private String msCreatedBy = "";

	private Date msModifiedDate;
	private Date msCreatedDate;

	private String msModifiedBy = "";
	private Boolean mbMutiAccount = false;
	private String msOfficeTitle = null;
	
	//R4
	private String msPermissionType = "";
	
	// release 3.1.0  defect 5650
	private String msUserAction = null;
	
	/* [Start] R8.9.0 QC9531 Add indicator for Access control */
	private String msIsBaseOrg = null;
	private String msStaffOrgId = null;
	
    public String getMsIsBaseOrg() {
		return msIsBaseOrg;
	}
	public void setMsIsBaseOrg(String msIsBaseOrg) {
		this.msIsBaseOrg = msIsBaseOrg;
	}
	public String getMsStaffOrgId() {
		return msStaffOrgId;
	}
	public void setMsStaffOrgId(String msStaffOrgId) {
		this.msStaffOrgId = msStaffOrgId;
	}

	public void setMsIsBaseOrgOn(){
		this.msIsBaseOrg = HHSConstants.ONE;
	}
	public void resetMsIsBaseOrg(){
		this.msIsBaseOrg = HHSConstants.ZERO;
	}
	
	public void resetMsStaffId(){
		this.msStaffId = null;
	}
	/* [End] R8.9.0 QC9531 Add indicator for Access control */



	/* [Start] R7.2.0 QC9055 Add indicator for Access control */
    private String msUserSubRole = "";
    
    public String getMsUserSubRole() {
        
        return msUserSubRole;
    }
    public void setUserSubRole(String msUserSubRole) {
        this.msUserSubRole = msUserSubRole;
    }
    /* [End] R7.2.0 QC8914 Add indicator for Access control */
    
	
	public String getMsPermissionType()
	{
		return msPermissionType;
	}

	public void setMsPermissionType(String msPermissionType)
	{
		this.msPermissionType = msPermissionType;
	}

	/**
	 * <li>This method was added in R4</li>
	 */
	public String getMsOfficeTitle()
	{
		return msOfficeTitle;
	}
	/**
	 * <li>This method was added in R4</li>
	 */
	public void setMsOfficeTitle(String msOfficeTitle)
	{
		this.msOfficeTitle = msOfficeTitle;
	}
	/**
	 * <li>This method was added in R4</li>
	 */
	public Boolean getMbMutiAccount()
	{
		return mbMutiAccount;
	}
	/**
	 * <li>This method was added in R4</li>
	 */
	public void setMbMutiAccount(Boolean mbMutiAccount)
	{
		this.mbMutiAccount = mbMutiAccount;
	}

	public String getMsName()
	{
		return msName;
	}

	public void setMsName(String msName)
	{
		this.msName = msName;
	}

	public String getDeActivatedUser()
	{
		return deActivatedUser;
	}

	public void setDeActivatedUser(String deActivatedUser)
	{
		this.deActivatedUser = deActivatedUser;
	}

	public String getMemberAsUser()
	{
		return memberAsUser;
	}

	public void setMemberAsUser(String memberAsUser)
	{
		this.memberAsUser = memberAsUser;
	}

	public Date getMsMemberInactiveDate()
	{
		return msMemberInactiveDate;
	}

	public void setMsMemberInactiveDate(Date msMemberInactiveDate)
	{
		this.msMemberInactiveDate = msMemberInactiveDate;
	}

	public String getMsPermissionLevel()
	{
		return msPermissionLevel;
	}

	public void setMsPermissionLevel(String msPermissionLevel)
	{
		this.msPermissionLevel = msPermissionLevel;
	}

	public String getMsAdminPermission()
	{
		return msAdminPermission;
	}

	public void setMsAdminPermission(String msAdminPermission)
	{
		this.msAdminPermission = msAdminPermission;
	}

	public String getMsNYCUserId()
	{
		return msNYCUserId;
	}

	public void setMsNYCUserId(String msNYCUserId)
	{
		this.msNYCUserId = msNYCUserId;
	}

	public String getMsUserStatus()
	{
		return msUserStatus;
	}

	public void setMsUserStatus(String msUserStatus)
	{
		this.msUserStatus = msUserStatus;
	}

	public Boolean getIsAdminUser()
	{
		return isAdminUser;
	}

	public void setIsAdminUser(Boolean isAdminUser)
	{
		this.isAdminUser = isAdminUser;
	}

	public String getOperationType()
	{
		return operationType;
	}

	public void setOperationType(String operationType)
	{
		this.operationType = operationType;
	}

	public String getReadOnly()
	{
		return readOnly;
	}

	public void setReadOnly(String readOnly)
	{
		this.readOnly = readOnly;
	}

	public String getMsStaffRole()
	{
		return msStaffRole;
	}

	public void setMsStaffRole(String msStaffRole)
	{
		this.msStaffRole = msStaffRole;
	}

	public String getMsStaffId()
	{
		return msStaffId;
	}

	public void setMsStaffId(String msStaffId)
	{
		this.msStaffId = msStaffId;
	}

	public String getMsStaffActiveFlag()
	{
		return msStaffActiveFlag;
	}

	public void setMsStaffActiveFlag(String msStaffActiveFlag)
	{
		this.msStaffActiveFlag = msStaffActiveFlag;
	}

	public String getMsStaffFirstName()
	{
		return msStaffFirstName;
	}

	public void setMsStaffFirstName(String msStaffFirstName)
	{
		this.msStaffFirstName = msStaffFirstName;
	}

	public String getMsStaffMidInitial()
	{
		return msStaffMidInitial;
	}

	public void setMsStaffMidInitial(String msStaffMidInitial)
	{
        if( msStaffMidInitial != null && msStaffMidInitial.length() >= 1  ){  
            this.msStaffMidInitial = Character.toString(msStaffMidInitial.charAt(0)); 
        }else{ 
            this.msStaffMidInitial = null;
        }
	}

	public String getMsStaffLastName()
	{
		return msStaffLastName;
	}

	public void setMsStaffLastName(String msStaffLastName)
	{
		this.msStaffLastName = msStaffLastName;
	}

	public String getMsStaffTitle()
	{
		return msStaffTitle;
	}

	public void setMsStaffTitle(String msStaffTitle)
	{
		this.msStaffTitle = msStaffTitle;
	}

	public String getMsStaffPhone()
	{
		return msStaffPhone;
	}

	public void setMsStaffPhone(String msStaffPhone)
	{
		this.msStaffPhone = msStaffPhone;
	}

	public String getMsStaffEmail()
	{
		return msStaffEmail;
	}

	public void setMsStaffEmail(String msStaffEmail)
	{
		this.msStaffEmail = msStaffEmail;
	}

	public String getMsActions()
	{
		return msActions;
	}

	public void setMsActions(String msActions)
	{
		this.msActions = msActions;
	}

	public String getMsSystemUser()
	{
		return msSystemUser;
	}

	public void setMsSystemUser(String msSystemUser)
	{
		this.msSystemUser = msSystemUser;
	}

	public String getMsMemberStatus()
	{
		return msMemberStatus;
	}

	public void setMsMemberStatus(String msMemberStatus)
	{
		this.msMemberStatus = msMemberStatus;
	}

	public String getMsOrgId()
	{
		return msOrgId;
	}

	public void setMsOrgId(String msOrgId)
	{
		this.msOrgId = msOrgId;
	}

	public String getMsTermConditionStatus()
	{
		return msTermConditionStatus;
	}

	public void setMsTermConditionStatus(String msTermConditionStatus)
	{
		this.msTermConditionStatus = msTermConditionStatus;
	}

	public String getMsOrgActiveFlag()
	{
		return msOrgActiveFlag;
	}

	public void setMsOrgActiveFlag(String msOrgActiveFlag)
	{
		this.msOrgActiveFlag = msOrgActiveFlag;
	}

	public String getMsUserDN()
	{
		return msUserDN;
	}

	public void setMsUserDN(String msUserDN)
	{
		this.msUserDN = msUserDN;
	}

	public String getMsOrgProcStatus()
	{
		return msOrgProcStatus;
	}

	public void setMsOrgProcStatus(String msOrgProcStatus)
	{
		this.msOrgProcStatus = msOrgProcStatus;
	}

	public String getMsDuplicate()
	{
		return msDuplicate;
	}

	public void setMsDuplicate(String msDuplicate)
	{
		this.msDuplicate = msDuplicate;
	}

	public String getMsOrganisationName()
	{
		return msOrganisationName;
	}

	public void setMsOrganisationName(String msOrganisationName)
	{
		this.msOrganisationName = msOrganisationName;
	}

	/**
	 * @return the msUserAcctCreationDate
	 */
	public Date getMsUserAcctCreationDate()
	{
		return msUserAcctCreationDate;
	}

	/**
	 * @param msUserAcctCreationDate the msUserAcctCreationDate to set
	 */
	public void setMsUserAcctCreationDate(Date msUserAcctCreationDate)
	{
		this.msUserAcctCreationDate = msUserAcctCreationDate;
	}

	/**
	 * @return the msUserType
	 */
	public String getMsUserType()
	{
		return msUserType;
	}

	/**
	 * @param msUserType the msUserType to set
	 */
	public void setMsUserType(String msUserType)
	{
		this.msUserType = msUserType;
	}

	public String getMsCreatedBy()
	{
		return msCreatedBy;
	}

	public void setMsCreatedBy(String msCreatedBy)
	{
		this.msCreatedBy = msCreatedBy;
	}

	/**
	 * @return the msModifiedDate
	 */
	public Date getMsModifiedDate()
	{
		return msModifiedDate;
	}

	/**
	 * @param msModifiedDate the msModifiedDate to set
	 */
	public void setMsModifiedDate(Date msModifiedDate)
	{
		this.msModifiedDate = msModifiedDate;
	}

	/**
	 * @return the msCreatedDate
	 */
	public Date getMsCreatedDate()
	{
		return msCreatedDate;
	}

	/**
	 * @param msCreatedDate the msCreatedDate to set
	 */
	public void setMsCreatedDate(Date msCreatedDate)
	{
		this.msCreatedDate = msCreatedDate;
	}

	/**
	 * @return the msModifiedBy
	 */
	public String getMsModifiedBy()
	{
		return msModifiedBy;
	}

	/**
	 * @param msModifiedBy the msModifiedBy to set
	 */
	public void setMsModifiedBy(String msModifiedBy)
	{
		this.msModifiedBy = msModifiedBy;
	}
 
	
	@Override
	public String toString()
	{
		return "StaffDetails [msStaffFirstName=" + msStaffFirstName + ", msStaffMidInitial=" + msStaffMidInitial
				+ ", msStaffLastName=" + msStaffLastName + ", msIsBaseOrg=" +  msIsBaseOrg  + ", msStaffOrgId=" +  msStaffOrgId  
				+ ", msStaffTitle=" + msStaffTitle + ", msStaffPhone="
				+ msStaffPhone + ", msStaffEmail=" + msStaffEmail + ", msStaffId=" + msStaffId + ", msStaffActiveFlag="
				+ msStaffActiveFlag + ", msStaffRole=" + msStaffRole + ", msSystemUser=" + msSystemUser
				+ ", msMemberStatus=" + msMemberStatus + ", msOrgId=" + msOrgId + ", msActions=" + msActions
				+ ", readOnly=" + readOnly + ", operationType=" + operationType + ", isAdminUser=" + isAdminUser
				+ ", msNYCUserId=" + msNYCUserId + ", msUserStatus=" + msUserStatus + ", msMemberInactiveDate="
				+ msMemberInactiveDate + ", msPermissionLevel=" + msPermissionLevel + ", msAdminPermission="
				+ msAdminPermission + ", msTermConditionStatus=" + msTermConditionStatus + ", msOrgActiveFlag="
				+ msOrgActiveFlag + ", msUserDN=" + msUserDN + ", msUserAcctCreationDate=" + msUserAcctCreationDate
				+ ", msOrgProcStatus=" + msOrgProcStatus + ", msDuplicate=" + msDuplicate + ", memberAsUser="
				+ memberAsUser + ", deActivatedUser=" + deActivatedUser + ", msOrganisationName=" + msOrganisationName
				+ ", msUserType=" + msUserType + ", msName=" + msName + ", msCreatedBy=" + msCreatedBy
				+ ", msModifiedDate=" + msModifiedDate + ", msCreatedDate=" + msCreatedDate + ", msModifiedBy="
				+ msModifiedBy + "]";
	}

	public String getMsUserAction() {
		return msUserAction;
	}

	public void setMsUserAction(String msUserAction) {
		this.msUserAction = msUserAction;
	}

    // Create a class constructor for the StaffDetails class
    public StaffDetails(String asUserDN) { 
        this.msUserDN = asUserDN;
    }
    // a default class constructor for the StaffDetails class
    public StaffDetails() { }


    /** [Start] R7.8.0 add two constructors for SAML **/    
    // Create a class constructor for the StaffDetails class
    public StaffDetails(String asUserDN, String asNYCUserId ) { 
        this.msUserDN = asUserDN;
        this.msNYCUserId = asNYCUserId;
        this.msStaffEmail = asNYCUserId;
    }
    
    public StaffDetails(String asUserDN, String asNYCUserId ,  String asFname, String asMiddleName, String asLastName    ) { 
        this.msUserDN = asUserDN;
        this.msNYCUserId = asNYCUserId;
        this.msStaffEmail = asNYCUserId;
        if( asFname != null ){      this.msStaffFirstName = asFname; }
        if( asMiddleName != null && asMiddleName.length() == 1  ){  this.msStaffMidInitial = Character.toString(asMiddleName.charAt(0)); }
        if( asLastName != null ) {         this.msStaffLastName = asLastName;         } 
    }
    
    /** [Start] R7.8.0 add two constructor for SAML **/    


}
