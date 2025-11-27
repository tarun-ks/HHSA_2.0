package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

/**
 * This class is a bean which maintains city user details
 * 
 */

public class CityUserDetailsBean
{

	// city_user_details table mapping
	@Length(max = 64)
	private String firstName;
	@Length(max = 64)
	private String lastName;
	@Length(max = 20)
	private String userId;
	private String emailId;
	@Length(max = 50)
	private String userRole;
	private String userDN;
	private String userType;
	private String organizationName;

	private int levelId;

	/**
	 * @return the firstName
	 */
	public String getFirstName()
	{
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName()
	{
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	/**
	 * @return the userId
	 */
	public String getUserId()
	{
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	/**
	 * @return the emailId
	 */
	public String getEmailId()
	{
		return emailId;
	}

	/**
	 * @param emailId the emailId to set
	 */
	public void setEmailId(String emailId)
	{
		this.emailId = emailId;
	}

	/**
	 * @return the userRole
	 */
	public String getUserRole()
	{
		return userRole;
	}

	/**
	 * @param userRole the userRole to set
	 */
	public void setUserRole(String userRole)
	{
		this.userRole = userRole;
	}

	/**
	 * @return the userDN
	 */
	public String getUserDN()
	{
		return userDN;
	}

	/**
	 * @param userDN the userDN to set
	 */
	public void setUserDN(String userDN)
	{
		this.userDN = userDN;
	}

	/**
	 * @return the userType
	 */
	public String getUserType()
	{
		return userType;
	}

	/**
	 * @param userType the userType to set
	 */
	public void setUserType(String userType)
	{
		this.userType = userType;
	}

	/**
	 * @return the organizationName
	 */
	public String getOrganizationName()
	{
		return organizationName;
	}

	/**
	 * @param organizationName the organizationName to set
	 */
	public void setOrganizationName(String organizationName)
	{
		this.organizationName = organizationName;
	}

	/**
	 * @return the levelId
	 */
	public int getLevelId()
	{
		return levelId;
	}

	/**
	 * @param levelId the levelId to set
	 */
	public void setLevelId(int levelId)
	{
		this.levelId = levelId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		CityUserDetailsBean other = (CityUserDetailsBean) obj;
		if (userId == null)
		{
			if (other.userId != null)
			{
				return false;
			}
		}
		else if (!userId.equals(other.userId))
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return "CityUserDetailsBean [firstName=" + firstName + ", lastName=" + lastName + ", userId=" + userId
				+ ", emailId=" + emailId + ", userRole=" + userRole + ", userDN=" + userDN + ", userType=" + userType
				+ ", organizationName=" + organizationName + ", levelId=" + levelId + "]";
	}

}