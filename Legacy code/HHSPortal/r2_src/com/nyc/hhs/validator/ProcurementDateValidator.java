package com.nyc.hhs.validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.nyc.hhs.model.Procurement;

public class ProcurementDateValidator implements Validator
{
	/**
	 * This Validator validates *just* ProposalDetails instances
	 */
	public boolean supports(Class aoClass)
	{
		return Procurement.class.equals(aoClass);
	}
	
	/**
	 * This method validates 
	 * <ul>
	 * <li>
	 * If the 'if' condition is satisfied then further processing is done
	 * </li>
	 * <li>Updated Methods in R4</li>
	 * </ul>
	 * @param aoObj Object
	 * @param aoError Error
	 *
	 */
	public void validate(Object aoObj, Errors aoError)
	{
		Procurement loProcurementBean = (Procurement) aoObj;
		if (loProcurementBean != null)
		{
			validateRequiredDateFormat(loProcurementBean.getRfpReleaseDatePlanned(), aoError, "rfpReleaseDatePlanned");
			validateRequiredDateFormat(loProcurementBean.getRfpReleaseDateUpdated(), aoError, "rfpReleaseDateUpdated");
			if (loProcurementBean.getIsOpenEndedRFP() != null
					&& loProcurementBean.getIsOpenEndedRFP().equalsIgnoreCase("No"))
			{
				validateRequiredDateFormat(loProcurementBean.getProposalDueDatePlanned(), aoError,
						"proposalDueDatePlanned");
				validateRequiredDateFormat(loProcurementBean.getProposalDueDateUpdated(), aoError,
						"proposalDueDateUpdated");
				validateRequiredDateFormat(loProcurementBean.getContractStartDatePlanned(), aoError,
						"contractStartDatePlanned");
				validateRequiredDateFormat(loProcurementBean.getContractStartDateUpdated(), aoError,
						"contractStartDateUpdated");
				validateRequiredDateFormat(loProcurementBean.getFirstRFPEvalDatePlanned(), aoError,
						"firstRFPEvalDatePlanned");
				validateRequiredDateFormat(loProcurementBean.getFirstRFPEvalDateUpdated(), aoError,
						"firstRFPEvalDateUpdated");
				validateRequiredDateFormat(loProcurementBean.getFinalRFPEvalDatePlanned(), aoError,
						"finalRFPEvalDatePlanned");
				validateRequiredDateFormat(loProcurementBean.getFinalRFPEvalDateUpdated(), aoError,
						"finalRFPEvalDateUpdated");
				validateDateFormat(loProcurementBean.getPreProposalConferenceDatePlanned(), aoError,
						"preProposalConferenceDatePlanned");
				validateDateFormat(loProcurementBean.getPreProposalConferenceDateUpdated(), aoError,
						"preProposalConferenceDateUpdated");
				validateRequiredDateFormat(loProcurementBean.getContractEndDatePlanned(), aoError,
						"contractEndDatePlanned");
				validateRequiredDateFormat(loProcurementBean.getContractEndDateUpdated(), aoError,
						"contractEndDateUpdated");
				validateRequiredDateFormat(loProcurementBean.getEvaluatorTrainingDatePlanned(), aoError,
						"evaluatorTrainingDatePlanned");
				validateRequiredDateFormat(loProcurementBean.getEvaluatorTrainingDateUpdated(), aoError,
						"evaluatorTrainingDateUpdated");
				validateRequiredDateFormat(loProcurementBean.getFirstEvalCompletionDatePlanned(), aoError,
						"firstEvalCompletionDatePlanned");
				validateRequiredDateFormat(loProcurementBean.getFirstEvalCompletionDateUpdated(), aoError,
						"firstEvalCompletionDateUpdated");
				validateRequiredDateFormat(loProcurementBean.getFinalEvalCompletionDatePlanned(), aoError,
						"finalEvalCompletionDatePlanned");
				validateRequiredDateFormat(loProcurementBean.getFinalEvalCompletionDateUpdated(), aoError,
						"finalEvalCompletionDateUpdated");
				validateRequiredDateFormat(loProcurementBean.getAwardSelectionDatePlanned(), aoError,
						"awardSelectionDatePlanned");
				validateRequiredDateFormat(loProcurementBean.getAwardSelectionDateUpdated(), aoError,
						"awardSelectionDateUpdated");
			}
		}
	}

	/**
	 * This method validates Date Format
	 * @param aoDate Date
	 * @param aoError Error
	 * @param asPropertyName PropertyName
	 */
	private void validateDateFormat(String aoDate, Errors aoError, String asPropertyName)
	{
		if (aoDate != null && !aoDate.equalsIgnoreCase(""))
		{
			if (!isDateValid(aoDate, "MM/dd/yyyy"))
			{
				aoError.rejectValue(asPropertyName, "dateInvalidFormat");
			}
			else
			{
				if (!isDateValidYear(aoDate, "MM/dd/yyyy"))
				{
					aoError.rejectValue(asPropertyName, "dateInvalid");
				}
			}
		}
	}

	/**
	 * This method validates required data format
	 * @param aoDate Date
	 * @param aoError Error
	 * @param asPropertyName Property Name
	 */
	private void validateRequiredDateFormat(String aoDate, Errors aoError, String asPropertyName)
	{
		if (aoDate != null && !aoDate.equalsIgnoreCase(""))
		{
			if (!isDateValid(aoDate, "MM/dd/yyyy"))
			{
				aoError.rejectValue(asPropertyName, "dateInvalidFormat");
			}
			else
			{
				if (!isDateValidYear(aoDate, "MM/dd/yyyy"))
				{
					aoError.rejectValue(asPropertyName, "dateInvalid");
				}
			}
		}
		else
		{
			aoError.rejectValue(asPropertyName, "dateRequired");
		}
	}

	/**
	 * This method validates Date
	 * @param aoDate Date
	 * @param asDateFormat Date Format
	 * @return boolean
	 */
	public static boolean isDateValid(String aoDate, String asDateFormat)
	{
		try
		{
			SimpleDateFormat loDateFormatter = new SimpleDateFormat(asDateFormat);
			loDateFormatter.setLenient(false);
			loDateFormatter.parse(aoDate);
			return true;
		}
		catch (ParseException aoExp)
		{
			return false;
		}
	}

	/**
	 * This method validates date year
	 * 
	 * @param aoDate Date
	 * @param asDateFormat DateFormat
	 * @return boolean
	 */
	public static boolean isDateValidYear(String aoDate, String asDateFormat)
	{
		boolean lbIsValidYear = false;
		try
		{
			SimpleDateFormat loDateFormatter = new SimpleDateFormat(asDateFormat);
			loDateFormatter.setLenient(false);
			if (loDateFormatter.parse(aoDate).before(loDateFormatter.parse("01/01/1800")))
			{
				lbIsValidYear= false;
			}
			else if (loDateFormatter.parse(aoDate).after(loDateFormatter.parse("31/31/2050")))
			{
				lbIsValidYear= false;
			}
			else
			{
				lbIsValidYear = true;
			}
		}
		catch (ParseException aoExp)
		{
			lbIsValidYear= false;
		}
		return lbIsValidYear;
	}
}