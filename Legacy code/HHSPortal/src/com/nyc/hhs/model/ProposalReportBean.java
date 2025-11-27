package com.nyc.hhs.model;


import com.nyc.hhs.constants.ApplicationConstants;
import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

/**
 * 
 * 
 */
public class ProposalReportBean {
    private String procurementTitle         = ApplicationConstants.EMPTY_STRING;
    private String providerName             = ApplicationConstants.EMPTY_STRING;
    @Length(max = 50) 
    private String providerEIN              = ApplicationConstants.EMPTY_STRING;
    private String proposalTitle            = ApplicationConstants.EMPTY_STRING;
    @RegExp(value ="^\\d{0,22}")
    private String proposalId               = ApplicationConstants.EMPTY_STRING;
    private String proposalStatus           = ApplicationConstants.EMPTY_STRING;
    private String competitionPoolId        = ApplicationConstants.EMPTY_STRING;
    private String competitionPool          = ApplicationConstants.EMPTY_STRING;
    private String totalFundingRequest      = ApplicationConstants.EMPTY_STRING;
    private String totalNumberOfService     = ApplicationConstants.EMPTY_STRING;
    private String costPerUnit              = ApplicationConstants.EMPTY_STRING;

    private String createdDate              = ApplicationConstants.EMPTY_STRING;
    private String lastModifiedDate         = ApplicationConstants.EMPTY_STRING;
    private String lastModifiedByName       = ApplicationConstants.EMPTY_STRING;
    private String lastModifiedByEmail      = ApplicationConstants.EMPTY_STRING;
    private String lastModifiedByPhone      = ApplicationConstants.EMPTY_STRING;

    private String question1Answer          = ApplicationConstants.EMPTY_STRING;
    private String question2Answer          = ApplicationConstants.EMPTY_STRING;
    private String question3Answer          = ApplicationConstants.EMPTY_STRING;
    private String question4Answer          = ApplicationConstants.EMPTY_STRING;
    private String question5Answer          = ApplicationConstants.EMPTY_STRING;
    private String question6Answer          = ApplicationConstants.EMPTY_STRING;
    private String question7Answer          = ApplicationConstants.EMPTY_STRING;
    private String question8Answer          = ApplicationConstants.EMPTY_STRING;
    private String question9Answer          = ApplicationConstants.EMPTY_STRING;
    private String question10Answer         = ApplicationConstants.EMPTY_STRING;
    private String question11Answer         = ApplicationConstants.EMPTY_STRING;
    private String question12Answer         = ApplicationConstants.EMPTY_STRING;
    private String question13Answer         = ApplicationConstants.EMPTY_STRING;
    private String question14Answer         = ApplicationConstants.EMPTY_STRING;
    private String question15Answer         = ApplicationConstants.EMPTY_STRING;
    // add questions R 8.5 QC 9401
    private String question16Answer         = ApplicationConstants.EMPTY_STRING;
    private String question17Answer         = ApplicationConstants.EMPTY_STRING;
   
	private String question18Answer         = ApplicationConstants.EMPTY_STRING;
                                            
    //From PROPOSAL_SITE                    
    private String schoolDistrictName       = ApplicationConstants.EMPTY_STRING;
    private String addressValidation        = ApplicationConstants.EMPTY_STRING;
    private String communityDistinct        = ApplicationConstants.EMPTY_STRING;
                                            
    private String numberOfSites            = ApplicationConstants.EMPTY_STRING;
    private String siteName                 = ApplicationConstants.EMPTY_STRING;
    private String siteAddress1             = ApplicationConstants.EMPTY_STRING;
    private String siteAddress2             = ApplicationConstants.EMPTY_STRING;
    private String siteCity                 = ApplicationConstants.EMPTY_STRING;
    private String siteState                = ApplicationConstants.EMPTY_STRING;
    private String siteZip                  = ApplicationConstants.EMPTY_STRING;
    
    private String agencyId                 = ApplicationConstants.EMPTY_STRING;
    
    // Start QC 9401 R 8.5
    private String providerContactPhone     = ApplicationConstants.EMPTY_STRING;
    private String providerContactEmail     = ApplicationConstants.EMPTY_STRING;
    private String providerContactName      = ApplicationConstants.EMPTY_STRING;
    private String spare                    = ApplicationConstants.EMPTY_STRING;
    private String epin      				= ApplicationConstants.EMPTY_STRING;
    
	public String getEpin() {
		return epin;
	}
	public void setEpin(String epin) {
		this.epin = epin;
	}
	public String getQuestion16Answer() {
		return question16Answer;
	}
	public void setQuestion16Answer(String question16Answer) {
		this.question16Answer = question16Answer;
	}
	public String getQuestion17Answer() {
		return question17Answer;
	}
	public void setQuestion17Answer(String question17Answer) {
		this.question17Answer = question17Answer;
	}
	public String getQuestion18Answer() {
		return question18Answer;
	}
	public void setQuestion18Answer(String question18Answer) {
		this.question18Answer = question18Answer;
	}
	public String getProviderContactPhone() {
		return providerContactPhone;
	}
	public void setProviderContactPhone(String providerContactPhone) {
		this.providerContactPhone = providerContactPhone;
	}
	public String getProviderContactEmail() {
		return providerContactEmail;
	}
	public void setProviderContactEmail(String providerContactEmail) {
		this.providerContactEmail = providerContactEmail;
	}
	public String getProviderContactName() {
		return providerContactName;
	}
	public void setProviderContactName(String providerContactName) {
		this.providerContactName = providerContactName;
	}
	public String getSpare() {
		return spare;
	}
	public void setSpare(String spare) {
		this.spare = spare;
	}

    // End QC 9401 R 8.5
   

    public String getProcurementTitle() {
        return procurementTitle;
    }
    public void setProcurementTitle(String procurementTitle) {
        this.procurementTitle = procurementTitle;
    }
    public String getProviderName() {
        return providerName;
    }
    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }
    public String getProviderEIN() {
        return providerEIN;
    }
    public void setProviderEIN(String providerEIN) {
        this.providerEIN = providerEIN;
    }
    public String getProposalTitle() {
        return proposalTitle;
    }
    public void setProposalTitle(String proposalTitle) {
        this.proposalTitle = proposalTitle;
    }
    public String getProposalId() {
        return proposalId;
    }
    public void setProposalId(String proposalId) {
        this.proposalId = proposalId;
    }
    public String getProposalStatus() {
        return proposalStatus;
    }
    public void setProposalStatus(String proposalStatus) {
        this.proposalStatus = proposalStatus;
    }
    public String getCompetitionPool() {
        return competitionPool;
    }
    public void setCompetitionPool(String competitionPool) {
        this.competitionPool = competitionPool;
    }
    public String getTotalFundingRequest() {
        return totalFundingRequest;
    }
    public void setTotalFundingRequest(String totalFundingRequest) {
        this.totalFundingRequest = totalFundingRequest;
    }
    public String getTotalNumberOfService() {
        return totalNumberOfService;
    }
    public void setTotalNumberOfService(String totalNumberOfService) {
        this.totalNumberOfService = totalNumberOfService;
    }
    public String getCostPerUnit() {
        return costPerUnit;
    }
    public void setCostPerUnit(String costPerUnit) {
        this.costPerUnit = costPerUnit;
    }
    public String getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
    public String getLastModifiedDate() {
        return lastModifiedDate;
    }
    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
    public String getLastModifiedByName() {
        return lastModifiedByName;
    }
    public void setLastModifiedByName(String lastModifiedByName) {
        this.lastModifiedByName = lastModifiedByName;
    }
    public String getLastModifiedByEmail() {
        return lastModifiedByEmail;
    }
    public void setLastModifiedByEmail(String lastModifiedByEmail) {
        this.lastModifiedByEmail = lastModifiedByEmail;
    }
    public String getLastModifiedByPhone() {
        return lastModifiedByPhone;
    }
    public void setLastModifiedByPhone(String lastModifiedByPhone) {
        this.lastModifiedByPhone = lastModifiedByPhone;
    }
    public String getQuestion1Answer() {
        return question1Answer;
    }
    public void setQuestion1Answer(String question1Answer) {
        this.question1Answer = question1Answer;
    }
    public String getQuestion2Answer() {
        return question2Answer;
    }
    public void setQuestion2Answer(String question2Answer) {
        this.question2Answer = question2Answer;
    }
    public String getQuestion3Answer() {
        return question3Answer;
    }
    public void setQuestion3Answer(String question3Answer) {
        this.question3Answer = question3Answer;
    }
    public String getQuestion4Answer() {
        return question4Answer;
    }
    public void setQuestion4Answer(String question4Answer) {
        this.question4Answer = question4Answer;
    }
    public String getQuestion5Answer() {
        return question5Answer;
    }
    public void setQuestion5Answer(String question5Answer) {
        this.question5Answer = question5Answer;
    }
    public String getQuestion6Answer() {
        return question6Answer;
    }
    public void setQuestion6Answer(String question6Answer) {
        this.question6Answer = question6Answer;
    }
    public String getQuestion7Answer() {
        return question7Answer;
    }
    public void setQuestion7Answer(String question7Answer) {
        this.question7Answer = question7Answer;
    }
    public String getQuestion8Answer() {
        return question8Answer;
    }
    public void setQuestion8Answer(String question8Answer) {
        this.question8Answer = question8Answer;
    }
    public String getQuestion9Answer() {
        return question9Answer;
    }
    public void setQuestion9Answer(String question9Answer) {
        this.question9Answer = question9Answer;
    }
    public String getQuestion10Answer() {
        return question10Answer;
    }
    public void setQuestion10Answer(String question10Answer) {
        this.question10Answer = question10Answer;
    }
    public String getQuestion11Answer() {
        return question11Answer;
    }
    public void setQuestion11Answer(String question11Answer) {
        this.question11Answer = question11Answer;
    }
    public String getQuestion12Answer() {
        return question12Answer;
    }
    public void setQuestion12Answer(String question12Answer) {
        this.question12Answer = question12Answer;
    }
    public String getQuestion13Answer() {
        return question13Answer;
    }
    public void setQuestion13Answer(String question13Answer) {
        this.question13Answer = question13Answer;
    }
    public String getQuestion14Answer() {
        return question14Answer;
    }
    public void setQuestion14Answer(String question14Answer) {
        this.question14Answer = question14Answer;
    }
    public String getQuestion15Answer() {
        return question15Answer;
    }
    public void setQuestion15Answer(String question15Answer) {
        this.question15Answer = question15Answer;
    }
    public String getSchoolDistrictName() {
        return schoolDistrictName;
    }
    public void setSchoolDistrictName(String schoolDistrictName) {
        this.schoolDistrictName = schoolDistrictName;
    }
    public String getAddressValidation() {
        return addressValidation;
    }
    public void setAddressValidation(String addressValidation) {
        this.addressValidation = addressValidation;
    }
    public String getCommunityDistinct() {
        return communityDistinct;
    }
    public void setCommunityDistinct(String communityDistinct) {
        this.communityDistinct = communityDistinct;
    }
    public String getNumberOfSites() {
        return numberOfSites;
    }
    public void setNumberOfSites(String numberOfSites) {
        this.numberOfSites = numberOfSites;
    }
    public String getSiteName() {
        return siteName;
    }
    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }
    public String getSiteAddress1() {
        return siteAddress1;
    }
    public void setSiteAddress1(String siteAddress1) {
        this.siteAddress1 = siteAddress1;
    }
    public String getSiteAddress2() {
        return siteAddress2;
    }
    public void setSiteAddress2(String siteAddress2) {
        this.siteAddress2 = siteAddress2;
    }
    public String getSiteCity() {
        return siteCity;
    }
    public void setSiteCity(String siteCity) {
        this.siteCity = siteCity;
    }
    public String getSiteState() {
        return siteState;
    }
    public void setSiteState(String siteState) {
        this.siteState = siteState;
    }
    public String getSiteZip() {
        return siteZip;
    }
    public void setSiteZip(String siteZip) {
        this.siteZip = siteZip;
    }
    public String getAgencyId() {
        return agencyId;
    }
    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    public String getCompetitionPoolId() {
        return competitionPoolId;
    }
    public void setCompetitionPoolId(String competitionPoolId) {
        this.competitionPoolId = competitionPoolId;
    }

    private String commaEscape(String lsStr){
        return "\"" + lsStr + "\"";
    }

    public String toCommarDelimertedString(){
        return commaEscape(procurementTitle) +","    
        + commaEscape(competitionPool      )  +","
        + commaEscape(providerName         )  +","
        + commaEscape(providerEIN          )  +","
        + commaEscape(proposalStatus       )  +","
        + commaEscape(proposalId           )  +","
        + commaEscape(proposalTitle        )  +","
        + commaEscape(totalFundingRequest  )  +","
        + commaEscape(totalNumberOfService )  +","
        + commaEscape(costPerUnit          )  +","
        + commaEscape(question1Answer      ) + ","
        + commaEscape(question2Answer      ) + ","
        + commaEscape(question3Answer      ) + ","
        + commaEscape(question4Answer      ) + ","
        + commaEscape(question5Answer      ) + ","
        + commaEscape(question6Answer      ) + ","
        + commaEscape(question7Answer      ) + ","
        + commaEscape(question8Answer      ) + ","
        + commaEscape(question9Answer      ) + ","
        + commaEscape(question10Answer     ) + ","
        + commaEscape(question11Answer     ) + ","
        + commaEscape(question12Answer     ) + ","
        + commaEscape(question13Answer     ) + ","
        + commaEscape(question14Answer     ) + ","
        + commaEscape(question15Answer     ) + ","
        + commaEscape(question16Answer     ) + ","
        + commaEscape(question17Answer     ) + ","
        + commaEscape(question18Answer     ) + ","    

        + commaEscape(schoolDistrictName   ) + ","
        + commaEscape(addressValidation    ) + ","

        + commaEscape(numberOfSites        ) + ","
        + commaEscape(siteName             ) + ","
        + commaEscape(siteAddress1         ) + ","
        + commaEscape(siteAddress2         ) + ","
        + commaEscape(siteCity             ) + ","
        + commaEscape(siteState            ) + ","
        + commaEscape(siteZip              ) + ","
        + commaEscape(communityDistinct    ) + ","
        
        + commaEscape(providerContactEmail ) + ","
        + commaEscape(providerContactPhone ) + ","
        + commaEscape(providerContactName  ) + ","

        + commaEscape(createdDate          ) + ","
        + commaEscape(lastModifiedDate     ) + ","
        + commaEscape(lastModifiedByName   ) + ","
        + commaEscape(lastModifiedByEmail  ) + ","
        + commaEscape(lastModifiedByPhone  ) 
        ;
        
        
        
    }
    

}


