package com.nyc.hhs.validator;

import java.util.List;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.model.ProposalDetailsBean;
import com.nyc.hhs.model.ProposalQuestionAnswerBean;
import com.nyc.hhs.model.SiteDetailsBean;

public class ProposalDetailsValidator implements Validator
{
	private final Validator moQuesAnsValidator;

	/**
	 * This Validator validates *just* ProposalDetails instances
	 */
	@Override
	public boolean supports(Class aoClass)
	{
		return ProposalDetailsBean.class.equals(aoClass);
	}

	/**
	 * This method validates Proposal Details
	 * @param aoQuesAnsValidator
	 */
	public ProposalDetailsValidator(Validator aoQuesAnsValidator)
	{
		this.moQuesAnsValidator = aoQuesAnsValidator;
	}

	/**
	 * This method is used to validate
	 * <ul><li>Updated Method in R4</li></ul>
	 * @param aoObj Object
	 * @param aoError Error 
	 */
	@Override
	public void validate(Object aoObj, Errors aoError)
	{
		ProposalDetailsBean loProposalDetailsBean = (ProposalDetailsBean) aoObj;
		if (loProposalDetailsBean != null)
		{
			ValidationUtils.rejectIfEmpty(aoError, HHSConstants.PROPOSAL_TITLE, HHSConstants.FIELD_REQUIRED);
			ValidationUtils.rejectIfEmpty(aoError, HHSConstants.PROVIDER_CONTRACT_ID, HHSConstants.FIELD_REQUIRED);
			ValidationUtils.rejectIfEmpty(aoError, HHSConstants.COMPETITION_POOL, HHSConstants.FIELD_REQUIRED);
			if (null != loProposalDetailsBean.getServiceUnitFlag()
					&& loProposalDetailsBean.getServiceUnitFlag().equalsIgnoreCase(HHSConstants.ONE))
			{
				ValidationUtils.rejectIfEmpty(aoError, HHSConstants.TOTAL_SERVICE, HHSConstants.FIELD_REQUIRED);
			}
			ValidationUtils.rejectIfEmpty(aoError, HHSConstants.TOTAL_FUNDING_REQUEST, HHSConstants.FIELD_REQUIRED);
			int liCount = 0;
			if (loProposalDetailsBean.getQuestionAnswerBeanList() != null)
			{
				for (ProposalQuestionAnswerBean loQueAns : loProposalDetailsBean.getQuestionAnswerBeanList())
				{
					aoError.pushNestedPath(HHSConstants.QUES_ANS_BEAN_LIST_BRACKET + liCount
							+ HHSConstants.SQUARE_BRAC_END);
					ValidationUtils.invokeValidator(this.moQuesAnsValidator, loQueAns, aoError);
					aoError.popNestedPath();
					liCount++;
				}
			}
			List<SiteDetailsBean> loSiteDetailsList = loProposalDetailsBean.getSiteDetailsList();
			if (loSiteDetailsList.isEmpty())
			{
				aoError.rejectValue(HHSConstants.SITE_DETAILS_LIST, "proposalDetails.minSiteSize");
			}
		}
	}
}