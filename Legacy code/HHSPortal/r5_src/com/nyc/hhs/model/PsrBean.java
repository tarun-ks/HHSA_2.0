package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

//This is a Bean class added for PSR functionality
public class PsrBean extends Procurement
{
	@RegExp(value ="^\\d{0,22}")
	//PSR_DETAILS_ID
	private String psrDetailId;
	@Length(max = 11)
	//BASIS_CONTRACTING_OUT
	private String basisContractOut;
	private String anticipateLevelComp;
	private String considerationPrice;
	private String conceptReportReleaseDt;
	private String renewalOption;
	private String multiYearHumanServContract;
	private String contractTermInfo;
	private String createdBy;
	private String createdDate;
	private String modifiedBy;
	private String modifiedDate;
	private String statusId;
	private String multiYearHumanServOpt;
	
	public PsrBean()
	{
		super();
	}
	
	/**
	 * @return the psrDetailId
	 */
	public String getPsrDetailId()
	{
		return psrDetailId;
	}

	/**
	 * @param psrDetailId the psrDetailId to set
	 */
	public void setPsrDetailId(String psrDetailId)
	{
		this.psrDetailId = psrDetailId;
	}

	/**
	 * @return the basisContractOut
	 */
	public String getBasisContractOut()
	{
		return basisContractOut;
	}

	/**
	 * @param basisContractOut the basisContractOut to set
	 */
	public void setBasisContractOut(String basisContractOut)
	{
		this.basisContractOut = basisContractOut;
	}

	/**
	 * @return the anticipateLevelComp
	 */
	public String getAnticipateLevelComp()
	{
		return anticipateLevelComp;
	}

	/**
	 * @param anticipateLevelComp the anticipateLevelComp to set
	 */
	public void setAnticipateLevelComp(String anticipateLevelComp)
	{
		this.anticipateLevelComp = anticipateLevelComp;
	}

	/**
	 * @return the considerationPrice
	 */
	public String getConsiderationPrice()
	{
		return considerationPrice;
	}

	/**
	 * @param considerationPrice the considerationPrice to set
	 */
	public void setConsiderationPrice(String considerationPrice)
	{
		this.considerationPrice = considerationPrice;
	}

	/**
	 * @return the conceptReportReleaseDt
	 */
	public String getConceptReportReleaseDt()
	{
		return conceptReportReleaseDt;
	}

	/**
	 * @param conceptReportReleaseDt the conceptReportReleaseDt to set
	 */
	public void setConceptReportReleaseDt(String conceptReportReleaseDt)
	{
		this.conceptReportReleaseDt = conceptReportReleaseDt;
	}

	/**
	 * @return the renewalOption
	 */
	public String getRenewalOption()
	{
		return renewalOption;
	}

	/**
	 * @param renewalOption the renewalOption to set
	 */
	public void setRenewalOption(String renewalOption)
	{
		this.renewalOption = renewalOption;
	}

	/**
	 * @return the multiYearHumanServContract
	 */
	public String getMultiYearHumanServContract()
	{
		return multiYearHumanServContract;
	}

	/**
	 * @param multiYearHumanServContract the multiYearHumanServContract to set
	 */
	public void setMultiYearHumanServContract(String multiYearHumanServContract)
	{
		this.multiYearHumanServContract = multiYearHumanServContract;
	}

	/**
	 * @return the contractTermInfo
	 */
	public String getContractTermInfo()
	{
		return contractTermInfo;
	}

	/**
	 * @param contractTermInfo the contractTermInfo to set
	 */
	public void setContractTermInfo(String contractTermInfo)
	{
		this.contractTermInfo = contractTermInfo;
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
	 * @return the createdDate
	 */
	public String getCreatedDate()
	{
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(String createdDate)
	{
		this.createdDate = createdDate;
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
	 * @return the modifiedDate
	 */
	public String getModifiedDate()
	{
		return modifiedDate;
	}

	/**
	 * @param modifiedDate the modifiedDate to set
	 */
	public void setModifiedDate(String modifiedDate)
	{
		this.modifiedDate = modifiedDate;
	}

	/**
	 * @return the statusId
	 */
	public String getStatusId()
	{
		return statusId;
	}

	/**
	 * @param statusId the statusId to set
	 */
	public void setStatusId(String statusId)
	{
		this.statusId = statusId;
	}

	/**
	 * @return the multiYearHumanServOpt
	 */
	public String getMultiYearHumanServOpt()
	{
		return multiYearHumanServOpt;
	}

	/**
	 * @param multiYearHumanServOpt the multiYearHumanServOpt to set
	 */
	public void setMultiYearHumanServOpt(String multiYearHumanServOpt)
	{
		this.multiYearHumanServOpt = multiYearHumanServOpt;
	}
}
