package com.nyc.hhs.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.nyc.hhs.model.ProposalDetailsBean;
import com.nyc.hhs.model.ProposalQuestionAnswerBean;

public class ProposalConfigurationValidator implements Validator
{
	private Validator moCustomQuestionValidator;

	/**
	 * This Validator validates *just* ProposalDetails instances
	 */
	public boolean supports(Class aoClass)
	{
		return ProposalDetailsBean.class.equals(aoClass);
	}

	/**
	 * @param moCustomQuestionValidator the moCustomQuestionValidator to set
	 */
	public ProposalConfigurationValidator(Validator moCustomQuestionValidator)
	{
		this.moCustomQuestionValidator = moCustomQuestionValidator;
	}

	public void validate(Object aoObj, Errors aoError)
	{
		ProposalDetailsBean loProposalDetailsBean = (ProposalDetailsBean) aoObj;
		if (loProposalDetailsBean != null)
		{
			int liCount = 0;
			for (ProposalQuestionAnswerBean loQueAns : loProposalDetailsBean.getQuestionAnswerBeanList())
			{
				aoError.pushNestedPath("questionAnswerBeanList[" + liCount + "]");
				ValidationUtils.invokeValidator(this.moCustomQuestionValidator, loQueAns, aoError);
				aoError.popNestedPath();
				liCount++;
			}
		}
	}
}