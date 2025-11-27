package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

/**
 * This class is a bean which maintains the User information for sending emails.
 * 
 */

public class UserEmailIdBean
{
	@Length(max = 128)
	private String userEmailId;
	@Length(max = 20)
	private String staffId;
	@Length(max = 20)
	private String userLevel;
	@Length(max = 20)
	private String userOrgId;
	private String orgLegalName;
	//START || Changes for enhancement 5978 for Release 3.11.0
	private String bccFlag;
	private String skipUserInNotification;


	public String getSkipUserInNotification() {
		return skipUserInNotification;
	}

	public void setSkipUserInNotification(String skipUserInNotification) {
		this.skipUserInNotification = skipUserInNotification;
	}

	public String getBccFlag() {
		return bccFlag;
	}

	public void setBccFlag(String bccFlag) {
		this.bccFlag = bccFlag;
	}
	//END || Changes for enhancement 5978 for Release 3.11.0
	/**
	 * @return the userEmailId
	 */
	public String getUserEmailId()
	{
		return userEmailId;
	}

	/**
	 * @param userEmailId
	 *            the userEmailId to set
	 */
	public void setUserEmailId(String userEmailId)
	{
		this.userEmailId = userEmailId;
	}

	/**
	 * @return the staffId
	 */
	public String getStaffId()
	{
		return staffId;
	}

	/**
	 * @param staffId
	 *            the staffId to set
	 */
	public void setStaffId(String staffId)
	{
		this.staffId = staffId;
	}
	

	/**
	 * @return the userLevel
	 */
	public String getUserLevel()
	{
		return userLevel;
	}

	/**
	 * @param userLevel the userLevel to set
	 */
	public void setUserLevel(String userLevel)
	{
		this.userLevel = userLevel;
	}
	

	/**
	 * @return the userOrgId
	 */
	public String getUserOrgId()
	{
		return userOrgId;
	}

	/**
	 * @param userOrgId the userOrgId to set
	 */
	public void setUserOrgId(String userOrgId)
	{
		this.userOrgId = userOrgId;
	}
	

	/**
	 * @return the orgLegalName
	 */
	public String getOrgLegalName()
	{
		return orgLegalName;
	}

	/**
	 * @param orgLegalName the orgLegalName to set
	 */
	public void setOrgLegalName(String orgLegalName)
	{
		this.orgLegalName = orgLegalName;
	}

	@Override
	public String toString() {
		return "UserEmailIdBean [userEmailId=" + userEmailId + ", staffId="
				+ staffId + "]";
	}
}
