package com.nyc.hhs.model;

import java.util.List;

/**
 * This class is used to keep logged in user details.
 *
 */

public class UserInformation {

	private static final long serialVersionUID = 1L;

	private String msUserId;
	private String msFirstName;
	private String msLastName;
	private String msMiddleInitial;// One character only
	private String msEmailAddress;
	private List moLRoles;
	private String msSecurityAns1;
	private String msSecurityAns2;
	private String msSecurityAns3;

	public String getFirstName()
	{
		return msFirstName;
	}

	public void setFirstName(String asFirstName)
	{
		this.msFirstName = asFirstName;
	}

	public String getLastName()
	{
		return msLastName;
	}

	public void setLastName(String asLastName)
	{
		this.msLastName = asLastName;
	}

	public String getMiddleInitial()
	{
		return msMiddleInitial;
	}

	public void setMiddleInitial(String asMiddleInitial)
	{
		this.msMiddleInitial = asMiddleInitial;
	}

	public String getEmailAddress()
	{
		return msEmailAddress;
	}

	public void setEmailAddress(String asEmailAddress)
	{
		this.msEmailAddress = asEmailAddress;
	}

	public List getRoles()
	{
		return moLRoles;
	}

	public void setRoles(List aoLRoles)
	{
		this.moLRoles = aoLRoles;
	}

	public String getSecurityAns1()
	{
		return msSecurityAns1;
	}

	public void setSecurityAns1(String asSecurityAns1)
	{
		this.msSecurityAns1 = asSecurityAns1;
	}

	public String getSecurityAns2()
	{
		return msSecurityAns2;
	}

	public void setSecurityAns2(String asSecurityAns2)
	{
		this.msSecurityAns2 = asSecurityAns2;
	}

	public String getSecurityAns3()
	{
		return msSecurityAns3;
	}

	public void setSecurityAns3(String asSecurityAns3)
	{
		this.msSecurityAns3 = asSecurityAns3;
	}

	public String getUserId()
	{
		return msUserId;
	}

	public void setUserId(String asUserId)
	{
		this.msUserId = asUserId;
	}

	@Override
	public String toString() {
		return "UserInformation [msUserId=" + msUserId + ", msFirstName="
				+ msFirstName + ", msLastName=" + msLastName
				+ ", msMiddleInitial=" + msMiddleInitial + ", msEmailAddress="
				+ msEmailAddress + ", moLRoles=" + moLRoles
				+ ", msSecurityAns1=" + msSecurityAns1 + ", msSecurityAns2="
				+ msSecurityAns2 + ", msSecurityAns3=" + msSecurityAns3 + "]";
	}
}
