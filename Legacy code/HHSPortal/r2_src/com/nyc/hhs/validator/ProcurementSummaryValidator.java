package com.nyc.hhs.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.nyc.hhs.model.Procurement;

public class ProcurementSummaryValidator implements Validator
{
	private Validator moProcurementDateValidator;

	/**
	 * This Validator validates *just* ProposalDetails instances
	 */
	public boolean supports(Class aoClass)
	{
		return Procurement.class.equals(aoClass);
	}

	/**
	 * @param moCustomQuestionValidator the moCustomQuestionValidator to set
	 */
	public ProcurementSummaryValidator(Validator aoProcurementDateValidator)
	{
		this.moProcurementDateValidator = aoProcurementDateValidator;
	}

	public void validate(Object aoObj, Errors aoError)
	{
		Procurement loProcurementBean = (Procurement) aoObj;
		if (loProcurementBean != null)
		{
			ValidationUtils.invokeValidator(this.moProcurementDateValidator, loProcurementBean, aoError);
			aoError.popNestedPath();
		}
	}
}