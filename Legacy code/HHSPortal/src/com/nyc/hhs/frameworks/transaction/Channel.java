package com.nyc.hhs.frameworks.transaction;

import java.util.HashMap;

/**
 * This class will be used by every controller class to pass user inputs or any
 * other required information to transaction layer.
 * 
 */

public class Channel
{

	HashMap<String, Object> moHMData = new HashMap<String, Object>();

	public void setData(HashMap<String, Object> aoHashmap)
	{
		this.moHMData.putAll(aoHashmap);
	}

	public HashMap<String, Object> getData()
	{
		return moHMData;
	}

	public void setData(String asKey, Object asData)
	{
		this.moHMData.put(asKey, asData);
	}

	public Object getData(String asKey)
	{
		return moHMData.get(asKey);
	}

	public Channel()
	{
		StringBuffer aoTransactionLog = new StringBuffer();
		this.setData("aoTransactionLog", aoTransactionLog);

	}
	public String toString(){

        return "Channel :: " + moHMData.toString();
	}

}
