/**
 * This is a bean class for procurement
 * information
 */
package com.nyc.hhs.model;

import java.util.Date;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;

import com.nyc.hhs.constants.HHSConstants;

public class ProcurementInfo extends BaseFilter
{
	//@Length(max = 120, min = 5)
	@Length(max = 120)
	//@NotBlank
	private String procurementTitle = HHSConstants.EMPTY_STRING;
	private Integer status;
	private Integer previousStatus;
	private String procurementEpin;
	private String procurementId;
	private String procurementStatus;
	private String isOpenEndedRFP;
	private Date lastUpdatedDate;
	private String orgId;
	private String isRfpReleasedBefore;

	// changes for R5 starts
	private String psrFlag;
	private String generateTaskFlag;
	private String psrDetailsId;
	private String regeneratePDFFlag;
	
	//[Start] R6.3 QC6627 add ability to create new competition pool and delete during addendum.
	private Integer procurementAddendumFlag = 0; 
	//[End] R6.3 QC6627 add ability to create new competition pool and delete during addendum.

	

	/**
	 * @return the regeneratePDFFlag
	 */
	public String getRegeneratePDFFlag()
	{
		return regeneratePDFFlag;
	}
	/**
	 * @param regeneratePDFFlag the regeneratePDFFlag to set
	 */
	public void setRegeneratePDFFlag(String regeneratePDFFlag)
	{
		this.regeneratePDFFlag = regeneratePDFFlag;
	}
	/**
	 * @return the psrDetailsId
	 */
	public String getPsrDetailsId()
	{
		return psrDetailsId;
	}
	/**
	 * @param psrDetailsId the psrDetailsId to set
	 */
	public void setPsrDetailsId(String psrDetailsId)
	{
		this.psrDetailsId = psrDetailsId;
	}
	/**
	 * @return the generateTaskFlag
	 */
	public String getGenerateTaskFlag()
	{
		return generateTaskFlag;
	}

	/**
	 * @param generateTaskFlag the generateTaskFlag to set
	 */
	public void setGenerateTaskFlag(String generateTaskFlag)
	{
		this.generateTaskFlag = generateTaskFlag;
	}

	private int psrPcofVersionNumber;

	/**
	 * @return the psrFlag
	 */
	public String getPsrFlag()
	{
		return psrFlag;
	}

	/**
	 * @return the psrPcofVersionNumber
	 */
	public int getPsrPcofVersionNumber()
	{
		return psrPcofVersionNumber;
	}

	/**
	 * @param psrPcofVersionNumber the psrPcofVersionNumber to set
	 */
	public void setPsrPcofVersionNumber(int psrPcofVersionNumber)
	{
		this.psrPcofVersionNumber = psrPcofVersionNumber;
	}

	/**
	 * @param psrFlag the psrFlag to set
	 */
	public void setPsrFlag(String psrFlag)
	{
		this.psrFlag = psrFlag;
	}

	// changes for R5 ends
	/**
	 * @return the procurementId
	 */
	public String getProcurementId()
	{
		return procurementId;
	}

	/**
	 * @param procurementId the procurementId to set
	 */
	public void setProcurementId(String procurementId)
	{
		this.procurementId = procurementId;
	}

	/**
	 * @return the procurementTitle
	 */
	public String getProcurementTitle()
	{
		return procurementTitle;
	}

	/**
	 * @param procurementTitle the procurementTitle to set
	 */
	public void setProcurementTitle(String procurementTitle)
	{
		this.procurementTitle = procurementTitle;
	}

	/**
	 * @return the procurementEpin
	 */
	public String getProcurementEpin()
	{
		if (null != procurementEpin && !procurementEpin.equals(HHSConstants.EMPTY_STRING))
		{
			procurementEpin = procurementEpin.trim();
		}
		else
		{
			procurementEpin = null;
		}
		return procurementEpin;
	}

	/**
	 * @param procurementEpin the procurementEpin to set
	 */
	public void setProcurementEpin(String procurementEpin)
	{
		this.procurementEpin = procurementEpin;
	}

	/**
	 * @return the procurementStatus
	 */
	public String getProcurementStatus()
	{
		return procurementStatus;
	}

	/**
	 * @param procurementStatus the procurementStatus to set
	 */
	public void setProcurementStatus(String procurementStatus)
	{
		this.procurementStatus = procurementStatus;
	}

	/**
	 * @return the lastUpdatedDate
	 */
	public Date getLastUpdatedDate()
	{
		return lastUpdatedDate;
	}

	/**
	 * @param lastUpdatedDate the lastUpdatedDate to set
	 */
	public void setLastUpdatedDate(Date lastUpdatedDate)
	{
		this.lastUpdatedDate = lastUpdatedDate;
	}

	/**
	 * @return the status
	 */
	public Integer getStatus()
	{
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Integer status)
	{
		this.status = status;
	}

	/**
	 * @return the previousStatus
	 */
	public Integer getPreviousStatus()
	{
		return previousStatus;
	}

	/**
	 * @param previousStatus the previousStatus to set
	 */
	public void setPreviousStatus(Integer previousStatus)
	{
		this.previousStatus = previousStatus;
	}

	/**
	 * @return the orgId
	 */
	public String getOrgId()
	{
		return orgId;
	}

	/**
	 * @param orgId the orgId to set
	 */
	public void setOrgId(String orgId)
	{
		this.orgId = orgId;
	}

	/**
	 * @return the isOpenEndedRFP
	 */
	public String getIsOpenEndedRFP()
	{
		return isOpenEndedRFP;
	}

	/**
	 * @param isOpenEndedRFP the isOpenEndedRFP to set
	 */
	public void setIsOpenEndedRFP(String isOpenEndedRFP)
	{
		this.isOpenEndedRFP = isOpenEndedRFP;
	}

	/**
	 * @return the isRfpReleasedBefore
	 */
	public String getIsRfpReleasedBefore()
	{
		return isRfpReleasedBefore;
	}

	/**
	 * @param isRfpReleasedBefore the isRfpReleasedBefore to set
	 */
	public void setIsRfpReleasedBefore(String isRfpReleasedBefore)
	{
		this.isRfpReleasedBefore = isRfpReleasedBefore;
	}

	@Override
	public String toString()
	{
		return "ProcurementInfo [procurementTitle=" + procurementTitle + ", status=" + status + ", previousStatus="
				+ previousStatus + ", procurementEpin=" + procurementEpin + ", procurementId=" + procurementId
				+ ", procurementStatus=" + procurementStatus + ", isOpenEndedRFP=" + isOpenEndedRFP
				+ ", lastUpdatedDate=" + lastUpdatedDate + ", orgId=" + orgId + "]";
	}
	public Integer getProcurementAddendumFlag() {
		return procurementAddendumFlag;
	}
	public void setProcurementAddendumFlag(Integer procurementAddendumFlag) {
		this.procurementAddendumFlag = procurementAddendumFlag;
	}
}
