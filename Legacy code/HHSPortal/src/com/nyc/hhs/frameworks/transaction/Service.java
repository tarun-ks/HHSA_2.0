package com.nyc.hhs.frameworks.transaction;

import java.util.ArrayList;

/**
 * This class will keep service class information from transaction configuration
 * file.
 * 
 */

public class Service
{

	public String msClassname;
	public String msMethodname;
	public String msMethodOutputName;
	public String msMethodOutputType;
	public boolean mbSystemServiceFlag;
	public ArrayList<MethodParam> moMethodParam;

	public ArrayList<MethodParam> getMethodParam()
	{
		return moMethodParam;
	}

	public void setMethodParam(ArrayList<MethodParam> aoMethodParam)
	{
		this.moMethodParam = aoMethodParam;
	}

	public String getClassname()
	{
		return msClassname;
	}

	public void setClassname(String asClassname)
	{
		this.msClassname = asClassname;
	}

	public String getMethodname()
	{
		return msMethodname;
	}

	public void setMethodname(String asMethodname)
	{
		this.msMethodname = asMethodname;
	}

	public String getMethodOutputName()
	{
		return msMethodOutputName;
	}

	public void setMethodOutputName(String asMethodOutputName)
	{
		this.msMethodOutputName = asMethodOutputName;
	}

	public String getMethodOutputType()
	{
		return msMethodOutputType;
	}

	public void setMethodOutputType(String asMethodOutputType)
	{
		this.msMethodOutputType = asMethodOutputType;
	}

	public boolean getSystemServiceFlag()
	{
		return mbSystemServiceFlag;
	}

	public void setSystemServiceFlag(boolean abSystemServiceFlag)
	{
		this.mbSystemServiceFlag = abSystemServiceFlag;
	}

	// Inner class
	@SuppressWarnings("unused")
	public class MethodParam
	{

		String msParamName;
		String msParamType;

		public String getParamName()
		{
			return msParamName;
		}

		public void setParamName(String asParamName)
		{
			this.msParamName = asParamName;
		}

		public String getParamType()
		{
			return msParamType;
		}

		public void setParamType(String asParamType)
		{
			this.msParamType = asParamType;
		}

	}
}
