package com.nyc.hhs.model;

import java.io.Serializable;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;
/**
 *This is a bean class for getting proposal
 *status information
 */
public class ProposalStatusInfo implements Serializable
{

	private static final long serialVersionUID = 1L; 
	@RegExp(value ="^\\d{0,22}")
	//STATUS_ID
	private String proposalStatusId;
	@Length(max = 50)
	//STATUS
	private String proposalStatusName;
	
	public ProposalStatusInfo()
	{
		super();
	}
	public String getProposalStatusId()
	{
		return proposalStatusId;
	}
	public void setProposalStatusId(String proposalStatusId)
	{
		this.proposalStatusId = proposalStatusId;
	}
	public String getProposalStatusName()
	{
		return proposalStatusName;
	}
	public void setProposalStatusName(String proposalStatusName)
	{
		this.proposalStatusName = proposalStatusName;
	}

	
}
