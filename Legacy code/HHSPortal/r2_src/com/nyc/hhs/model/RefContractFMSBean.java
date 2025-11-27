package com.nyc.hhs.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.nyc.hhs.constants.HHSConstants;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;
/*
 * QC 9145 R 8.8 - keep contract record from REF_CONTRACTS_H table
 */
public class RefContractFMSBean 
{
	@RegExp(value ="^\\d{0,22}")
	private String contractId = HHSConstants.EMPTY_STRING;
	private String contractTitle = HHSConstants.EMPTY_STRING;
	private String contractTypeId = HHSConstants.EMPTY_STRING;
	private Date contractStartDate;
	private Date contractEndDate;
	private String contractAmount = HHSConstants.EMPTY_STRING;
	private String parentContractId = HHSConstants.EMPTY_STRING;
	private String discrepancyFlag = HHSConstants.STRING_ZERO;
	private String statusId = HHSConstants.EMPTY_STRING;
	private boolean discrepancyInContractAmount = HHSConstants.BOOLEAN_FALSE;
	private boolean discrepancyInStartDate = HHSConstants.BOOLEAN_FALSE;
	private boolean discrepancyInEndDate = HHSConstants.BOOLEAN_FALSE;
	private String extCtNumber = HHSConstants.EMPTY_STRING;
	private String organizationId = HHSConstants.EMPTY_STRING;
	private String agencyId = HHSConstants.EMPTY_STRING;
	private String awardAgencyId = HHSConstants.EMPTY_STRING;
	private String parentAgencyId = HHSConstants.EMPTY_STRING;
	private String awardEpin = HHSConstants.EMPTY_STRING;
	private String fmsContractAmount = HHSConstants.EMPTY_STRING;
	@Length(max = 30)
	//TRKG_NO
	private String fmsTrkgNo = HHSConstants.EMPTY_STRING; 
	//@RegExp(value ="^\\d{0,22}")
	//DOC_VERS_NO
	private String fmsDocVersNo = HHSConstants.EMPTY_STRING; 
	@Length(max = 8)
	//DOC_CD
	private String fmsDocCd = HHSConstants.EMPTY_STRING;
	@Length(max = 4)
	//DOC_DEPT_CD
	private String fmsDocDeptCd = HHSConstants.EMPTY_STRING;
	@Length(max = 20)
	//DOC_ID
	private String fmsDocId = HHSConstants.EMPTY_STRING;
	private String fmsExtCtNumber = HHSConstants.EMPTY_STRING;
	private Date fmsContractStartDate;
	private Date fmsContractEndDate;
	private String vendCustCd = HHSConstants.EMPTY_STRING;
	private String vendTin = HHSConstants.EMPTY_STRING;
	private String vendFmsId = HHSConstants.EMPTY_STRING;
	private String userId = HHSConstants.EMPTY_STRING;
	private String extCpmmodityCode= HHSConstants.EMPTY_STRING;
	private String extDocVersNo = HHSConstants.EMPTY_STRING; 
	 
	
	
	public String getExtCpmmodityCode() {
		return extCpmmodityCode;
	}
	public void setExtCpmmodityCode(String extCpmmodityCode) {
		this.extCpmmodityCode = extCpmmodityCode;
	}
	public String getExtDocVersNo() {
		return extDocVersNo;
	}
	public void setExtDocVersNo(String extDocVersNo) {
		this.extDocVersNo = extDocVersNo;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getVendCustCd() {
		return vendCustCd;
	}
	public void setVendCustCd(String vendCustCd) {
		this.vendCustCd = vendCustCd;
	}
	public String getVendTin() {
		return vendTin;
	}
	public void setVendTin(String vendTin) {
		this.vendTin = vendTin;
	}
	public String getVendFmsId() {
		return vendFmsId;
	}
	public void setVendFmsId(String vendFmsId) {
		this.vendFmsId = vendFmsId;
	}
	
	public String getContractId() {
		return contractId;
	}
	public void setContractId(String contractId) {
		this.contractId = contractId;
	}
	public String getContractTitle() {
		return contractTitle;
	}
	public void setContractTitle(String contractTitle) {
		this.contractTitle = contractTitle;
	}
	public String getContractTypeId() {
		return contractTypeId;
	}
	public void setContractTypeId(String contractTypeId) {
		this.contractTypeId = contractTypeId;
	}
	public Date getContractStartDate() {
		return contractStartDate;
	}
	public void setContractStartDate(Date contractStartDate) {
		this.contractStartDate = contractStartDate;
	}
	public Date getContractEndDate() {
		return contractEndDate;
	}
	public void setContractEndDate(Date contractEndDate) {
		this.contractEndDate = contractEndDate;
	}
	public String getContractAmount() {
		return contractAmount;
	}
	public void setContractAmount(String contractAmount) {
		this.contractAmount = contractAmount;
	}
	public String getFmsContractAmount() {
		return fmsContractAmount;
	}
	public void setFmsContractAmount(String fmsContractAmount) {
		this.fmsContractAmount = fmsContractAmount;
	}
	public String getParentContractId() {
		return parentContractId;
	}
	public void setParentContractId(String parentContractId) {
		this.parentContractId = parentContractId;
	}
	public String getDiscrepancyFlag() {
		return discrepancyFlag;
	}
	public void setDiscrepancyFlag(String discrepancyFlag) {
		this.discrepancyFlag = discrepancyFlag;
	}
	public String getStatusId() {
		return statusId;
	}
	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}
	public boolean isDiscrepancyInContractAmount() {
		return discrepancyInContractAmount;
	}
	public void setDiscrepancyInContractAmount(boolean discrepancyInContractAmount) {
		this.discrepancyInContractAmount = discrepancyInContractAmount;
	}
	public boolean isDiscrepancyInStartDate() {
		return discrepancyInStartDate;
	}
	public void setDiscrepancyInStartDate(boolean discrepancyInStartDate) {
		this.discrepancyInStartDate = discrepancyInStartDate;
	}
	public boolean isDiscrepancyInEndDate() {
		return discrepancyInEndDate;
	}
	public void setDiscrepancyInEndDate(boolean discrepancyInEndDate) {
		this.discrepancyInEndDate = discrepancyInEndDate;
	}
	public String getExtCtNumber() {
		return extCtNumber;
	}
	public void setExtCtNumber(String extCtNumber) {
		this.extCtNumber = extCtNumber;
	}
	public String getOrganizationId() {
		return organizationId;
	}
	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}
	public String getAgencyId() {
		return agencyId;
	}
	public void setAgencyId(String agencyId) {
		this.agencyId = agencyId;
	}
	public String getAwardAgencyId() {
		return awardAgencyId;
	}
	public void setAwardAgencyId(String awardAgencyId) {
		this.awardAgencyId = awardAgencyId;
	}
	public String getParentAgencyId() {
		return parentAgencyId;
	}
	public void setParentAgencyId(String parentAgencyId) {
		this.parentAgencyId = parentAgencyId;
	}
	public String getAwardEpin() {
		return awardEpin;
	}
	public void setAwardEpin(String awardEpin) {
		this.awardEpin = awardEpin;
	}
	public String getFmsTrkgNo() {
		return fmsTrkgNo;
	}
	public void setFmsTrkgNo(String fmsTrkgNo) {
		this.fmsTrkgNo = fmsTrkgNo;
	}
	public String getFmsDocVersNo() {
		return fmsDocVersNo;
	}
	public void setFmsDocVersNo(String fmsDocVersNo) {
		this.fmsDocVersNo = fmsDocVersNo;
	}
	public String getFmsDocCd() {
		return fmsDocCd;
	}
	public void setFmsDocCd(String fmsDocCd) {
		this.fmsDocCd = fmsDocCd;
	}
	public String getFmsDocDeptCd() {
		return fmsDocDeptCd;
	}
	public void setFmsDocDeptCd(String fmsDocDeptCd) {
		this.fmsDocDeptCd = fmsDocDeptCd;
	}
	public String getFmsDocId() {
		return fmsDocId;
	}
	public void setFmsDocId(String fmsDocId) {
		this.fmsDocId = fmsDocId;
	}
	public String getFmsExtCtNumber() {
		return fmsExtCtNumber;
	}
	public void setFmsExtCtNumber(String fmsExtCtNumber) {
		this.fmsExtCtNumber = fmsExtCtNumber;
	}
	public Date getFmsContractStartDate() {
		return fmsContractStartDate;
	}
	public void setFmsContractStartDate(Date fmsContractStartDate) {
		this.fmsContractStartDate = fmsContractStartDate;
	}
	public Date getFmsContractEndDate() {
		return fmsContractEndDate;
	}
	public void setFmsContractEndDate(Date fmsContractEndDate) {
		this.fmsContractEndDate = fmsContractEndDate;
	}
	
	@Override
	public String toString() {
		return "RefContractFMSBean [contractId=" + contractId
				+ ", contractTitle=" + contractTitle + ", contractTypeId="
				+ contractTypeId + ", contractStartDate=" + contractStartDate
				+ ", contractEndDate=" + contractEndDate + ", contractAmount="
				+ contractAmount + ", parentContractId=" + parentContractId
				+ ", discrepancyFlag=" + discrepancyFlag + ", statusId="
				+ statusId + ", discrepancyInContractAmount="
				+ discrepancyInContractAmount + ", discrepancyInStartDate="
				+ discrepancyInStartDate + ", discrepancyInEndDate="
				+ discrepancyInEndDate + ", extCtNumber=" + extCtNumber
				+ ", organizationId=" + organizationId + ", agencyId="
				+ agencyId + ", awardAgencyId=" + awardAgencyId
				+ ", parentAgencyId=" + parentAgencyId + ", awardEpin="
				+ awardEpin + ", fmsContractAmount=" + fmsContractAmount
				+ ", fmsTrkgNo=" + fmsTrkgNo + ", fmsDocVersNo=" + fmsDocVersNo
				+ ", fmsDocCd=" + fmsDocCd + ", fmsDocDeptCd=" + fmsDocDeptCd
				+ ", fmsDocId=" + fmsDocId + ", fmsExtCtNumber="
				+ fmsExtCtNumber + ", fmsContractStartDate="
				+ fmsContractStartDate + ", fmsContractEndDate="
				+ fmsContractEndDate + ", vendCustCd=" + vendCustCd
				+ ", vendTin=" + vendTin + ", vendFmsId=" + vendFmsId
				+ ", userId=" + userId + ", extCpmmodityCode="
				+ extCpmmodityCode + ", extDocVersNo=" + extDocVersNo + "]";
	}
	
}
