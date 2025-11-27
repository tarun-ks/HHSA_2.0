package com.nyc.hhs.model;

//import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

/**
 * 
 * EvaluationReassignDetailsBean class
 * 
 */
public class EvaluationReassignDetailsBean
{

	// Initialize the required variables
	//@RegExp(value ="^\\d{0,22}")
	private String evalSettingsIntExtId = "";
	private String addDeleteFlag = "";

	/*
	 * For each variable a getter and a setter method is created.
	 */
	public String getEvalSettingsIntExtId()
	{
		return evalSettingsIntExtId;
	}

	public void setEvalSettingsIntExtId(String evalSettingsIntExtId)
	{
		this.evalSettingsIntExtId = evalSettingsIntExtId;
	}

	public String getAddDeleteFlag()
	{
		return addDeleteFlag;
	}

	public void setAddDeleteFlag(String addDeleteFlag)
	{
		this.addDeleteFlag = addDeleteFlag;
	}

}
