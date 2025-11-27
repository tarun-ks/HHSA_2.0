package com.nyc.hhs.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.model.EvaluationCriteriaBean;

public class EvaluationCriteriaScoreValidator implements Validator
{
	/**
	 * This Validator validates *just* ProposalDetails instances
	 */
	public boolean supports(Class aoClass)
	{
		return EvaluationCriteriaBean.class.equals(aoClass);
	}

	public void validate(Object aoObj, Errors aoError)
	{
		EvaluationCriteriaBean loEvaluationCriteriaBean = (EvaluationCriteriaBean) aoObj;
		if (loEvaluationCriteriaBean != null)
		{
			if (loEvaluationCriteriaBean.getScoreFlag() != null
					&& loEvaluationCriteriaBean.getScoreFlag().equalsIgnoreCase(HHSConstants.YES_UPPERCASE))
			{
				ValidationUtils.rejectIfEmptyOrWhitespace(aoError, HHSConstants.SCORE_CRITERIA,
						"required.scoreCriteria", "! This field is required.");
				ValidationUtils.rejectIfEmptyOrWhitespace(aoError, HHSConstants.MAX_SCORE, "required.maximumScore",
						"! This field is required.");
				checkScoreCriteriaLength(loEvaluationCriteriaBean, aoError);
				checkScoreLength(loEvaluationCriteriaBean, aoError);
			}
		}
	}

	private void checkScoreCriteriaLength(EvaluationCriteriaBean aoEvaluationCriteriaBean, Errors aoError)
	{
		if (aoEvaluationCriteriaBean.getScoreCriteria() != null
				&& aoEvaluationCriteriaBean.getScoreCriteria().trim().length() > 90)
		{
			aoError.rejectValue(HHSConstants.SCORE_CRITERIA, "field.max.length", new Object[]
			{ Integer.valueOf(90) }, "! You can not enter more than " + 90 + " characters.");
		}
	}

	private void checkScoreLength(EvaluationCriteriaBean aoEvaluationCriteriaBean, Errors aoError)
	{
		if (aoEvaluationCriteriaBean != null && aoEvaluationCriteriaBean.getMaximumScore() != null
				&& aoEvaluationCriteriaBean.getMaximumScore().toString().length() > 3)
		{
			aoError.rejectValue(HHSConstants.MAX_SCORE, "field.max.length", new Object[]
			{ Integer.valueOf(3) }, "! You can not enter more than " + 3 + " characters.");
		}
	}
}