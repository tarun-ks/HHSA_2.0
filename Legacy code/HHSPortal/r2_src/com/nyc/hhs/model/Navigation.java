/**
 * Navigation Bean
 */
package com.nyc.hhs.model;

import java.util.List;

public class Navigation
{
	private boolean isSelected = false;
	private String tabId;
	private String tabState;
	private boolean isAuthorized = true;
	private String screenNumber;
	private String additionRuleName;
	private List<Navigation> childList;

	/**
	 * @return the tabId
	 */
	public String getTabId()
	{
		return tabId;
	}

	/**
	 * @param tabId the tabId to set
	 */
	public void setTabId(String tabId)
	{
		this.tabId = tabId;
	}

	/**
	 * @return the tabState
	 */
	public String getTabState()
	{
		return tabState;
	}

	/**
	 * @param tabState the tabState to set
	 */
	public void setTabState(String tabState)
	{
		this.tabState = tabState;
	}

	/**
	 * @return the screenNumber
	 */
	public String getScreenNumber()
	{
		return screenNumber;
	}

	/**
	 * @param screenNumber the screenNumber to set
	 */
	public void setScreenNumber(String screenNumber)
	{
		this.screenNumber = screenNumber;
	}

	/**
	 * @param isAuthorized the isAuthorized to set
	 */
	public void setAuthorized(boolean isAuthorized)
	{
		this.isAuthorized = isAuthorized;
	}

	/**
	 * @return the isAuthorized
	 */
	public boolean isAuthorized()
	{
		return isAuthorized;
	}

	/**
	 * @param isSelected the isSelected to set
	 */
	public void setSelected(boolean isSelected)
	{
		this.isSelected = isSelected;
	}

	/**
	 * @return the isSelected
	 */
	public boolean isSelected()
	{
		return isSelected;
	}

	/**
	 * @param childList the childList to set
	 */
	public void setChildList(List<Navigation> childList)
	{
		this.childList = childList;
	}

	/**
	 * @return the childList
	 */
	public List<Navigation> getChildList()
	{
		return childList;
	}

	/**
	 * @return the additionRuleName
	 */
	public String getAdditionRuleName()
	{
		return additionRuleName;
	}

	/**
	 * @param additionRuleName the additionRuleName to set
	 */
	public void setAdditionRuleName(String additionRuleName)
	{
		this.additionRuleName = additionRuleName;
	}

	@Override
	public String toString()
	{
		return "Navigation [isSelected=" + isSelected + ", tabId=" + tabId + ", tabState=" + tabState
				+ ", isAuthorized=" + isAuthorized + ", screenNumber=" + screenNumber + ", additionRuleName="
				+ additionRuleName + ", childList=" + childList + "]";
	}
}