package com.nyc.hhs.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.model.EvaluationCriteriaBean;
import com.nyc.hhs.model.RFPReleaseBean;

public class EvaluationCriteriaDetailsValidator implements Validator
{
	private Validator moEvalCriteriaValidator;

	/**
	 * This Validator validates *just* ProposalDetails instances
	 */
	public boolean supports(Class aoClass)
	{
		return RFPReleaseBean.class.equals(aoClass);
	}

	/**
	 * @param moCustomQuestionValidator the moCustomQuestionValidator to set
	 */
	public EvaluationCriteriaDetailsValidator(Validator moCustomQuestionValidator)
	{
		this.moEvalCriteriaValidator = moCustomQuestionValidator;
	}

	public void validate(Object aoObj, Errors aoError)
	{
		RFPReleaseBean loRFPReleaseBean = (RFPReleaseBean) aoObj;
		if (loRFPReleaseBean != null)
		{
			Integer liCounter = Integer.valueOf(0);
			for (EvaluationCriteriaBean loEvaluationCriteriaBean : loRFPReleaseBean.getLoEvaluationCriteriaBeanList())
			{
				aoError.pushNestedPath(HHSConstants.EVAL_CRIT_BEAN_LIST_BRACKET + liCounter
						+ HHSConstants.SQUARE_BRAC_END);
				ValidationUtils.invokeValidator(this.moEvalCriteriaValidator, loEvaluationCriteriaBean, aoError);
				aoError.popNestedPath();
				liCounter++;
			}
		}
	}
}