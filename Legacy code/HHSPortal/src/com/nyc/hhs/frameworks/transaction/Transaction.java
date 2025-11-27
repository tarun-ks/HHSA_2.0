package com.nyc.hhs.frameworks.transaction;

import java.util.ArrayList;

/**
 * This class will be used to get transaction information and all its service
 * classes information for a give transaction.
 * 
 */

public class Transaction
{

	public String msTransId;
	public String msIsPreAudit;
	public String msIsPostAudit;
	public boolean mbUseDBConnection;
	public boolean mbUseFilenetConnection;
	public boolean mbUseLocalDBConnection;
	public ArrayList<Service> moServices;

	public String getTransId()
	{
		return msTransId;
	}

	public void setTransId(String asTransId)
	{
		this.msTransId = asTransId;
	}

	public String getIsPreAudit()
	{
		return msIsPreAudit;
	}

	public void setIsPreAudit(String asIsPreAudit)
	{
		this.msIsPreAudit = asIsPreAudit;
	}

	public String getIsPostAudit()
	{
		return msIsPostAudit;
	}

	public void setIsPostAudit(String asIsPostAudit)
	{
		this.msIsPostAudit = asIsPostAudit;
	}

	public ArrayList<Service> getServices()
	{
		return moServices;
	}

	public void setServices(ArrayList<Service> aoServices)
	{
		this.moServices = aoServices;
	}

	public boolean isMbUseDBConnection()
	{
		return mbUseDBConnection;
	}

	public void setMbUseDBConnection(boolean mbUseDBConnection)
	{
		this.mbUseDBConnection = mbUseDBConnection;
	}

	public boolean isMbUseFilenetConnection()
	{
		return mbUseFilenetConnection;
	}

	public void setMbUseFilenetConnection(boolean mbUseFilenetConnection)
	{
		this.mbUseFilenetConnection = mbUseFilenetConnection;
	}

	public boolean isMbUseLocalDBConnection()
	{
		return mbUseLocalDBConnection;
	}

	public void setMbUseLocalDBConnection(boolean abUseLocalDBConnection)
	{
		this.mbUseLocalDBConnection = abUseLocalDBConnection;
	}

}
