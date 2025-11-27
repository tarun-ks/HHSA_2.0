package com.nyc.hhs.model;

import java.util.HashMap;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

/**
 * This bean class is added as a part of enhancement number 6508. it will hold the values for Stuck Queue items which are referred while sending the notification
 */
public class QueueItemsDetailsBean
{
	
	private   String queueName;
	@Length(max = 50)
	//COMPONENT_NAME
	private 	String queueType;
	private   int stuckSince;
	private   int stuckCount;
	private HashMap<String,Integer> stuckItemDetails = new HashMap<String,Integer>();
	/**
	 * @return the queueName
	 */
	public String getQueueName()
	{
		return queueName;
	}
	/**
	 * @param queueName the queueName to set
	 */
	public void setQueueName(String queueName)
	{
		this.queueName = queueName;
	}
	/**
	 * @return the stuckSince
	 */
	public int getStuckSince()
	{
		return stuckSince;
	}
	/**
	 * @param stuckSince the stuckSince to set
	 */
	public void setStuckSince(int stuckSince)
	{
		this.stuckSince = stuckSince;
	}
	/**
	 * @return the stuckCount
	 */
	public int getStuckCount()
	{
		return stuckCount;
	}
	/**
	 * @param stuckCount the stuckCount to set
	 */
	public void setStuckCount(int stuckCount)
	{
		this.stuckCount = stuckCount;
	}
	/**
	 * @return the queueType
	 */
	public String getQueueType()
	{
		return queueType;
	}
	/**
	 * @param queueType the queueType to set
	 */
	public void setQueueType(String queueType)
	{
		this.queueType = queueType;
	}
	public void setStuckItemDetails(HashMap stuckItemDetails) {
		this.stuckItemDetails = stuckItemDetails;
	}
	public HashMap getStuckItemDetails() {
		return stuckItemDetails;
	}
	
	

	
	
}
