/**
 * 
 */
package com.nyc.hhs.model;

public class AutoCompleteBean
{

	private String displayName;

	private String hiddenId;

	/**
	 * @return the displayName
	 */
	public String getDisplayName()
	{
		return displayName;
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	/**
	 * @return the hiddenId
	 */
	public String getHiddenId()
	{
		return hiddenId;
	}

	/**
	 * @param hiddenId the hiddenId to set
	 */
	public void setHiddenId(String hiddenId)
	{
		this.hiddenId = hiddenId;
	}

	@Override
	public String toString()
	{
		return "AutoCompleteBean [displayName=" + displayName + ", hiddenId=" + hiddenId + "]";
	}

}
