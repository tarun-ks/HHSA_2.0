package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

public class ProgramNameInfo
{

	@RegExp(value ="^\\d{0,22}")
	private String programId;
	@Length(max = 250)
	private String programName;

	public String getProgramId()
	{
		return programId;
	}

	public void setProgramId(String programId)
	{
		this.programId = programId;
	}

	public String getProgramName()
	{
		return programName;
	}

	public void setProgramName(String programName)
	{
		this.programName = programName;
	}

	@Override
	public String toString()
	{
		return "ProgramNameInfo [programId=" + programId + ", programName=" + programName + "]";
	}

}
