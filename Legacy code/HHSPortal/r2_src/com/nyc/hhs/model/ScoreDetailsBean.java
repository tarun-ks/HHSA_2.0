package com.nyc.hhs.model;

import java.util.List;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

public class ScoreDetailsBean
{
	@RegExp(value ="^\\d{0,22}")
	private String miProposalId;
	private String miEvaluatorId;
	private String createdBy;
	private String modifiedBy;
	private String internalComments;
	private String action;

	private List<EvaluationBean> miEvaluationBeanList;

	/**
	 * @return the miProposalId
	 */
	public String getMiProposalId()
	{
		return miProposalId;
	}

	/**
	 * @param miProposalId the miProposalId to set
	 */
	public void setMiProposalId(String miProposalId)
	{
		this.miProposalId = miProposalId;
	}

	/**
	 * @return the miEvaluatorId
	 */
	public String getMiEvaluatorId()
	{
		return miEvaluatorId;
	}

	/**
	 * @param miEvaluatorId the miEvaluatorId to set
	 */
	public void setMiEvaluatorId(String miEvaluatorId)
	{
		this.miEvaluatorId = miEvaluatorId;
	}

	/**
	 * @return the createdBy
	 */
	public String getCreatedBy()
	{
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy)
	{
		this.createdBy = createdBy;
	}

	/**
	 * @return the modifiedBy
	 */
	public String getModifiedBy()
	{
		return modifiedBy;
	}

	/**
	 * @param modifiedBy the modifiedBy to set
	 */
	public void setModifiedBy(String modifiedBy)
	{
		this.modifiedBy = modifiedBy;
	}

	/**
	 * @return the miEvaluationBeanList
	 */
	public List<EvaluationBean> getMiEvaluationBeanList()
	{
		return miEvaluationBeanList;
	}

	/**
	 * @param miEvaluationBeanList the miEvaluationBeanList to set
	 */
	public void setMiEvaluationBeanList(List<EvaluationBean> miEvaluationBeanList)
	{
		this.miEvaluationBeanList = miEvaluationBeanList;
	}

	/**
	 * @return the internalComments
	 */
	public String getInternalComments()
	{
		return internalComments;
	}

	/**
	 * @param internalComments the internalComments to set
	 */
	public void setInternalComments(String internalComments)
	{
		this.internalComments = internalComments;
	}

	/**
	 * @return the action
	 */
	public String getAction()
	{
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(String action)
	{
		this.action = action;
	}

	@Override
	public String toString()
	{
		return "ScoreDetailsBean [miProposalId=" + miProposalId + ", miEvaluatorId=" + miEvaluatorId + ", createdBy="
				+ createdBy + ", modifiedBy=" + modifiedBy + ", miEvaluationBeanList=" + miEvaluationBeanList + "]";
	}
}
