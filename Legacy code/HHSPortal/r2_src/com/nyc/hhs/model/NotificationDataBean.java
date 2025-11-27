/**
 * 
 */
package com.nyc.hhs.model;

import java.util.HashMap;
import java.util.List;

/**
 * This is bean will be used to carry provider/ Agency/ Accelerator information
 * for notification.
 */
public class NotificationDataBean
{
	private List<String> providerList;
	private List<String> agencyList;
	private HashMap<String, String> linkMap;
	private HashMap<String, String> agencyLinkMap;
	private HashMap<String, String> additionalParameterMap;

	/**
	 * @return
	 */
	public HashMap<String, String> getAdditionalParameterMap()
	{
		return additionalParameterMap;
	}

	/**
	 * @param additionalParameterMap
	 */
	public void setAdditionalParameterMap(HashMap<String, String> additionalParameterMap)
	{
		this.additionalParameterMap = additionalParameterMap;
	}

	/**
	 * @return
	 */
	public HashMap<String, String> getAgencyLinkMap()
	{
		return agencyLinkMap;
	}

	/**
	 * @param agencyLinkMap
	 */
	public void setAgencyLinkMap(HashMap<String, String> agencyLinkMap)
	{
		this.agencyLinkMap = agencyLinkMap;
	}

	/**
	 * @return the providerList
	 */
	public List<String> getProviderList()
	{
		return providerList;
	}

	/**
	 * @param providerList the providerList to set
	 */
	public void setProviderList(List<String> providerList)
	{
		this.providerList = providerList;
	}

	/**
	 * @return the agencyList
	 */
	public List<String> getAgencyList()
	{
		return agencyList;
	}

	/**
	 * @param agencyList the agencyList to set
	 */
	public void setAgencyList(List<String> agencyList)
	{
		this.agencyList = agencyList;
	}

	/**
	 * @return the linkMap
	 */
	public HashMap<String, String> getLinkMap()
	{
		return linkMap;
	}

	/**
	 * @param linkMap the linkMap to set
	 */
	public void setLinkMap(HashMap<String, String> linkMap)
	{
		this.linkMap = linkMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "NotificationDataBean [providerList=" + providerList + ", agencyList=" + agencyList + ", linkMap="
				+ linkMap + ", agencyLinkMap=" + agencyLinkMap + ", additionalParameterMap=" + additionalParameterMap
				+ "]";
	}
}
