package com.nyc.hhs.model;

/**
 * This class is a bean which maintains agency settings module details.
 * 
 */

public class ProgramNameBean
{

	private int programID;
	private String programName;

	/**
	 * @return the programID
	 */
	public int getProgramID()
	{
		return programID;
	}

	/**
	 * @param programID the programID to set
	 */
	public void setProgramID(int programID)
	{
		this.programID = programID;
	}

	/**
	 * @return the programName
	 */
	public String getProgramName()
	{
		return programName;
	}

	/**
	 * @param programName the programName to set
	 */
	public void setProgramName(String programName)
	{
		this.programName = programName;
	}

	@Override
	public String toString()
	{
		return "ProgramNameBean [programID=" + programID + ", programName=" + programName + "]";
	}

}