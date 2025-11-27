package com.nyc.hhs.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.model.ProposalQuestionAnswerBean;

public class ProposalAnswerValidator implements Validator
{
	/**
	 * This Validator validates *just* ProposalDetails instances
	 */
	public boolean supports(Class aoClass)
	{
		return ProposalQuestionAnswerBean.class.equals(aoClass);
	}

	public void validate(Object aoObj, Errors aoError)
	{
		ProposalQuestionAnswerBean loQueAnsBean = (ProposalQuestionAnswerBean) aoObj;
		if (loQueAnsBean != null)
		{
			ValidationUtils.rejectIfEmpty(aoError, HHSConstants.ANSWER_TEXT, HHSConstants.FIELD_REQUIRED);
		}
	}
}