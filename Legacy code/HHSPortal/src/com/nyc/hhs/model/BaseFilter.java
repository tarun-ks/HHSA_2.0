package com.nyc.hhs.model;

/*
 * Class created to contain basic filter fields
 * required by any filter bean
 */
public class BaseFilter
{
	private String pageIndex;
	private int startNode;
	private int endNode;
	private String firstSortType;
	private String secondSortType;
	private String firstSort;
	private String secondSort;
	private String sortColumnName;
	private boolean firstSortDate;
	private boolean secondSortDate;
	// Release 5
	private boolean contractAccess = true;
	private boolean userAccess = true;
	private boolean viewUserAccessDropdown = true;
	private String userIdContractRestriction;

	/**
	 * @return the pageIndex
	 */
	public String getPageIndex()
	{
		return pageIndex;
	}

	/**
	 * @param pageIndex the pageIndex to set
	 */
	public void setPageIndex(String pageIndex)
	{
		this.pageIndex = pageIndex;
	}

	/**
	 * @return the startNode
	 */
	public int getStartNode()
	{
		return startNode;
	}

	/**
	 * @param startNode the startNode to set
	 */
	public void setStartNode(int startNode)
	{
		this.startNode = startNode;
	}

	/**
	 * @return the endNode
	 */
	public int getEndNode()
	{
		return endNode;
	}

	/**
	 * @param endNode the endNode to set
	 */
	public void setEndNode(int endNode)
	{
		this.endNode = endNode;
	}

	/**
	 * @return the firstSortType
	 */
	public String getFirstSortType()
	{
		return firstSortType;
	}

	/**
	 * @param firstSortType the firstSortType to set
	 */
	public void setFirstSortType(String firstSortType)
	{
		this.firstSortType = firstSortType;
	}

	/**
	 * @return the secondSortType
	 */
	public String getSecondSortType()
	{
		return secondSortType;
	}

	/**
	 * @param secondSortType the secondSortType to set
	 */
	public void setSecondSortType(String secondSortType)
	{
		this.secondSortType = secondSortType;
	}

	/**
	 * @return the firstSort
	 */
	public String getFirstSort()
	{
		return firstSort;
	}

	/**
	 * @param firstSort the firstSort to set
	 */
	public void setFirstSort(String firstSort)
	{
		this.firstSort = firstSort;
	}

	/**
	 * @return the secondSort
	 */
	public String getSecondSort()
	{
		return secondSort;
	}

	/**
	 * @param secondSort the secondSort to set
	 */
	public void setSecondSort(String secondSort)
	{
		this.secondSort = secondSort;
	}

	/**
	 * @return the sortColumnName
	 */
	public String getSortColumnName()
	{
		return sortColumnName;
	}

	/**
	 * @param sortColumnName the sortColumnName to set
	 */
	public void setSortColumnName(String sortColumnName)
	{
		this.sortColumnName = sortColumnName;
	}

	/**
	 * @return the firstSortDate
	 */
	public boolean isFirstSortDate()
	{
		return firstSortDate;
	}

	/**
	 * @param firstSortDate the firstSortDate to set
	 */
	public void setFirstSortDate(boolean firstSortDate)
	{
		this.firstSortDate = firstSortDate;
	}

	/**
	 * @return the secondSortDate
	 */
	public boolean isSecondSortDate()
	{
		return secondSortDate;
	}

	/**
	 * @param secondSortDate the secondSortDate to set
	 */
	public void setSecondSortDate(boolean secondSortDate)
	{
		this.secondSortDate = secondSortDate;
	}

	@Override
	public String toString()
	{
		return "BaseFilter [pageIndex=" + pageIndex + ", startNode=" + startNode + ", endNode=" + endNode
				+ ", firstSortType=" + firstSortType + ", secondSortType=" + secondSortType + ", firstSort="
				+ firstSort + ", secondSort=" + secondSort + ", sortColumnName=" + sortColumnName + ", firstSortDate="
				+ firstSortDate + ", secondSortDate=" + secondSortDate + "]";
	}

	// added in R5
	/**
	 * @return the contractAccess
	 */
	public boolean isContractAccess()
	{
		return contractAccess;
	}

	/**
	 * @param contractAccess the contractAccess to set
	 */
	public void setContractAccess(boolean contractAccess)
	{
		this.contractAccess = contractAccess;
	}

	/**
	 * @return the userAccess
	 */
	public boolean isUserAccess()
	{
		return userAccess;
	}

	/**
	 * @param userAccess the userAccess to set
	 */
	public void setUserAccess(boolean userAccess)
	{
		this.userAccess = userAccess;
	}

	/**
	 * @return the userIdContractRestriction
	 */
	public String getUserIdContractRestriction()
	{
		return userIdContractRestriction;
	}

	/**
	 * @param userIdContractRestriction the userIdContractRestriction to set
	 */
	public void setUserIdContractRestriction(String userIdContractRestriction)
	{
		this.userIdContractRestriction = userIdContractRestriction;
	}

	public boolean isViewUserAccessDropdown()
	{
		return viewUserAccessDropdown;
	}

	public void setViewUserAccessDropdown(boolean viewUserAccessDropdown)
	{
		this.viewUserAccessDropdown = viewUserAccessDropdown;
	}
	// R5 changes ends
}
