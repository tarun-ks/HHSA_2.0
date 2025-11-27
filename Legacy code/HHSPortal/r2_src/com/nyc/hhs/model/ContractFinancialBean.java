package com.nyc.hhs.model;

import java.util.Date;

import com.nyc.hhs.constants.HHSConstants;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

public class ContractFinancialBean
{

	private String id;
	@RegExp(value ="^\\d{0,22}")
	private String contractId;
	@RegExp(value ="^\\d{0,22}")
	private String contractTypeId;
	@Length(max = 20)
	private String agencyId;
	@RegExp(value ="^\\d{0,22}")
	private String parentContractId;
	private String contractAmount;
	private Date contractStart;
	private Date contractEnd;
	private String contractValue = HHSConstants.EMPTY_STRING;
	private String contractSourceId = HHSConstants.EMPTY_STRING;
	private String programId = HHSConstants.EMPTY_STRING;
	private String statusId = HHSConstants.EMPTY_STRING;
	private String registrationFlag = HHSConstants.EMPTY_STRING;
	private String updateFlag = HHSConstants.EMPTY_STRING;
	private String createByUserId = HHSConstants.EMPTY_STRING;
	private String modifyByUserId = HHSConstants.EMPTY_STRING;
	private String procMethod = HHSConstants.EMPTY_STRING;
	private String contractTitle = HHSConstants.EMPTY_STRING;
	private String orgnaziationId = HHSConstants.EMPTY_STRING;

	private String unitOfAppropriation = HHSConstants.EMPTY_STRING;
	private String budgetCode = HHSConstants.EMPTY_STRING;

	private String objectCode = HHSConstants.EMPTY_STRING;
	private String fiscalYear = HHSConstants.EMPTY_STRING;
	private String contractFinancialId = HHSConstants.EMPTY_STRING;
	private String procurementId = HHSConstants.EMPTY_STRING;
	private String activeFlag = HHSConstants.EMPTY_STRING;
	private String total = HHSConstants.EMPTY_STRING;
	private String delStatus = HHSConstants.EMPTY_STRING;
	private Date modDate;
	private String parentId;
	private String subOc = HHSConstants.EMPTY_STRING;
	private String rc = HHSConstants.EMPTY_STRING;
	private String ammount = HHSConstants.EMPTY_STRING;
	private String deleteFlag = HHSConstants.EMPTY_STRING;
	private String discrepancyFlag = HHSConstants.EMPTY_STRING;
	private String extEpin = HHSConstants.EMPTY_STRING;
	private String extCTNumber = HHSConstants.EMPTY_STRING;

	//R6: Extra field for non apt epin change
	private String refAptEpinId = HHSConstants.EMPTY_STRING;

	public String getRefAptEpinId()
	{
		return refAptEpinId;
	}

	public void setRefAptEpinId(String refAptEpinId)
	{
		this.refAptEpinId = refAptEpinId;
	}

	public Date getModDate()
	{
		return modDate;
	}

	public void setModDate(Date modDate)
	{
		this.modDate = modDate;
	}

	public String getExtEpin()
	{
		return extEpin;
	}

	public void setExtEpin(String extEpin)
	{
		this.extEpin = extEpin;
	}

	public String getDeleteFlag()
	{
		return deleteFlag;
	}

	public void setDeleteFlag(String deleteFlag)
	{
		this.deleteFlag = deleteFlag;
	}

	public String getDiscrepancyFlag()
	{
		return discrepancyFlag;
	}

	public void setDiscrepancyFlag(String discrepancyFlag)
	{
		this.discrepancyFlag = discrepancyFlag;
	}

	public String getAmmount()
	{
		return ammount;
	}

	public void setAmmount(String ammount)
	{
		this.ammount = ammount;
	}

	public String getOrgnaziationId()
	{
		return orgnaziationId;
	}

	public void setOrgnaziationId(String orgnaziationId)
	{
		this.orgnaziationId = orgnaziationId;
	}

	public String getContractValue()
	{
		return contractValue;
	}

	public void setContractValue(String contractValue)
	{
		this.contractValue = contractValue;
	}

	public Date getContractStart()
	{
		return contractStart;
	}

	public void setContractStart(Date contractStart)
	{
		this.contractStart = contractStart;
	}

	public Date getContractEnd()
	{
		return contractEnd;
	}

	public void setContractEnd(Date contractEnd)
	{
		this.contractEnd = contractEnd;
	}

	public String getContractSourceId()
	{
		return contractSourceId;
	}

	public void setContractSourceId(String contractSourceId)
	{
		this.contractSourceId = contractSourceId;
	}

	public String getProgramId()
	{
		return programId;
	}

	public void setProgramId(String programId)
	{
		this.programId = programId;
	}

	public String getStatusId()
	{
		return statusId;
	}

	public void setStatusId(String statusId)
	{
		this.statusId = statusId;
	}

	public String getRegistrationFlag()
	{
		return registrationFlag;
	}

	public void setRegistrationFlag(String registrationFlag)
	{
		this.registrationFlag = registrationFlag;
	}

	public String getUpdateFlag()
	{
		return updateFlag;
	}

	public void setUpdateFlag(String updateFlag)
	{
		this.updateFlag = updateFlag;
	}

	public String getCreateByUserId()
	{
		return createByUserId;
	}

	public void setCreateByUserId(String createByUserId)
	{
		this.createByUserId = createByUserId;
	}

	public String getModifyByUserId()
	{
		return modifyByUserId;
	}

	public void setModifyByUserId(String modifyByUserId)
	{
		this.modifyByUserId = modifyByUserId;
	}

	public String getProcMethod()
	{
		return procMethod;
	}

	public void setProcMethod(String procMethod)
	{
		this.procMethod = procMethod;
	}

	public String getContractTitle()
	{
		return contractTitle;
	}

	public void setContractTitle(String contractTitle)
	{
		this.contractTitle = contractTitle;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getContractId()
	{
		return contractId;
	}

	public void setContractId(String contractId)
	{
		this.contractId = contractId;
	}

	public String getContractTypeId()
	{
		return contractTypeId;
	}

	public void setContractTypeId(String contractTypeId)
	{
		this.contractTypeId = contractTypeId;
	}

	public String getAgencyId()
	{
		return agencyId;
	}

	public void setAgencyId(String agencyId)
	{
		this.agencyId = agencyId;
	}

	public String getParentContractId()
	{
		return parentContractId;
	}

	public void setParentContractId(String parentContractId)
	{
		this.parentContractId = parentContractId;
	}

	public String getContractAmount()
	{
		return contractAmount;
	}

	public void setContractAmount(String contractAmount)
	{
		this.contractAmount = contractAmount;
	}

	public String getUnitOfAppropriation()
	{
		return unitOfAppropriation;
	}

	public void setUnitOfAppropriation(String unitOfAppropriation)
	{
		this.unitOfAppropriation = unitOfAppropriation;
	}

	public String getBudgetCode()
	{
		return budgetCode;
	}

	public void setBudgetCode(String budgetCode)
	{
		this.budgetCode = budgetCode;
	}

	public String getObjectCode()
	{
		return objectCode;
	}

	public void setObjectCode(String objectCode)
	{
		this.objectCode = objectCode;
	}

	public String getFiscalYear()
	{
		return fiscalYear;
	}

	public void setFiscalYear(String fiscalYear)
	{
		this.fiscalYear = fiscalYear;
	}

	public String getContractFinancialId()
	{
		return contractFinancialId;
	}

	public void setContractFinancialId(String contractFinancialId)
	{
		this.contractFinancialId = contractFinancialId;
	}

	public String getProcurementId()
	{
		return procurementId;
	}

	public void setProcurementId(String procurementId)
	{
		this.procurementId = procurementId;
	}

	public String getActiveFlag()
	{
		return activeFlag;
	}

	public void setActiveFlag(String activeFlag)
	{
		this.activeFlag = activeFlag;
	}

	public String getTotal()
	{
		return total;
	}

	public void setTotal(String total)
	{
		this.total = total;
	}

	public String getDelStatus()
	{
		return delStatus;
	}

	public void setDelStatus(String delStatus)
	{
		this.delStatus = delStatus;
	}

	public String getParentId()
	{
		return parentId;
	}

	public void setParentId(String parentId)
	{
		this.parentId = parentId;
	}

	public String getSubOc()
	{
		return subOc;
	}

	public void setSubOc(String subOc)
	{
		this.subOc = subOc;
	}

	public String getRc()
	{
		return rc;
	}

	public void setRc(String rc)
	{
		this.rc = rc;
	}

	/**
	 * @return the extCTNumber
	 */
	public String getExtCTNumber()
	{
		return extCTNumber;
	}

	/**
	 * @param extCTNumber the extCTNumber to set
	 */
	public void setExtCTNumber(String extCTNumber)
	{
		this.extCTNumber = extCTNumber;
	}

	@Override
	public String toString()
	{
		return "ContractFinancialBean [id=" + id + ", contractId=" + contractId + ", contractTypeId=" + contractTypeId
				+ ", agencyId=" + agencyId + ", parentContractId=" + parentContractId + ", contractAmount="
				+ contractAmount + ", contractStart=" + contractStart + ", contractEnd=" + contractEnd
				+ ", contractValue=" + contractValue + ", contractSourceId=" + contractSourceId + ", programId="
				+ programId + ", statusId=" + statusId + ", registrationFlag=" + registrationFlag + ", updateFlag="
				+ updateFlag + ", createByUserId=" + createByUserId + ", modifyByUserId=" + modifyByUserId
				+ ", procMethod=" + procMethod + ", contractTitle=" + contractTitle + ", orgnaziationId="
				+ orgnaziationId + ", unitOfAppropriation=" + unitOfAppropriation + ", budgetCode=" + budgetCode
				+ ", objectCode=" + objectCode + ", fiscalYear=" + fiscalYear + ", contractFinancialId="
				+ contractFinancialId + ", procurementId=" + procurementId + ", activeFlag=" + activeFlag + ", total="
				+ total + ", delStatus=" + delStatus + ", modDate=" + modDate + ", parentId=" + parentId + ", subOc="
				+ subOc + ", rc=" + rc + ", ammount=" + ammount + ", deleteFlag=" + deleteFlag + ", discrepancyFlag="
				+ discrepancyFlag + ", extEpin=" + extEpin + ", extCTNumber=" + extCTNumber + ", refAptEpinId="
				+ refAptEpinId + " contractMessage="+ contractMessage +"]";
	}
	//added in R7
	private String contractMessage = HHSConstants.EMPTY_STRING;

	public String getContractMessage()
	{
		return contractMessage;
	}

	public void setContractMessage(String contractMessage)
	{
		this.contractMessage = contractMessage;
	}
	//R7 End
}
