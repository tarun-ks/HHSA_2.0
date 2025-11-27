package com.nyc.hhs.model;

import java.util.List;

public class LineItemMasterBean
{

	private String subbudgetId;
	private String indirectRatePercent;
	private PersonnelServicesData nonGridPSData;
	private ContractBudgetSummary loBudgetSummary;
	private CBOperationSupportBean nonGridOPSData;
	private ContractedServicesBean nonGridConServiceData;
	private List<RateBean> rateBeanList;
	private List<CBMileStoneBean> milestoneBeanList;
	private List<CBEquipmentBean> equipmentBeanList;
	private List<CBIndirectRateBean> indirectBeanList;
	private List<CBOperationSupportBean> opsBeanList;
	private List<CBProfessionalServicesBean> profserviceBeanList;
	private List<CBProgramIncomeBean> programincomeBeanList;
	private List<CBUtilities> utilityBeanList;
	private List<PersonnelServiceBudget> personnelserviceBeanList;
	private List<Rent> rentBeanList;
	private List<UnallocatedFunds> unallocatedBeanList;
	private List<ContractedServicesBean> contractedserviceBeanList;
	//Made changes for release 3.6.0 Enhancement id 6484
	private List<SiteDetailsBean> siteDetailsBeanList;
	//Start:Added in R7 for Cost-Center
	private List<CBServicesBean> servicesBeanList;
	private List<CBServicesBean> costCenterBeanList;
	//End: Added in R7 for Cost-Center
	//Start R7 : Program Income changes
	private String piIndirectRatePercent;

	public String getPiIndirectRatePercent()
	{
		return piIndirectRatePercent;
	}

	public void setPiIndirectRatePercent(String piIndirectRatePercent)
	{
		this.piIndirectRatePercent = piIndirectRatePercent;
	}
	//End R7: Program Income
	
	//Start:Added in R7 for Cost-Center
	public List<CBServicesBean> getServicesBeanList()
	{
		return servicesBeanList;
	}

	public void setServicesBeanList(List<CBServicesBean> servicesBeanList)
	{
		this.servicesBeanList = servicesBeanList;
	}

	public List<CBServicesBean> getCostCenterBeanList()
	{
		return costCenterBeanList;
	}

	public void setCostCenterBeanList(List<CBServicesBean> costCenterBeanList)
	{
		this.costCenterBeanList = costCenterBeanList;
	}
	//End: Added in R7 for Cost-Center
	
	public ContractBudgetSummary getLoBudgetSummary()
	{
		return loBudgetSummary;
	}

	public void setLoBudgetSummary(ContractBudgetSummary loBudgetSummary)
	{
		this.loBudgetSummary = loBudgetSummary;
	}

	public String getIndirectRatePercent()
	{
		return indirectRatePercent;
	}

	public void setIndirectRatePercent(String indirectRatePercent)
	{
		this.indirectRatePercent = indirectRatePercent;
	}

	public PersonnelServicesData getNonGridPSData()
	{
		return nonGridPSData;
	}

	public void setNonGridPSData(PersonnelServicesData nonGridPSData)
	{
		this.nonGridPSData = nonGridPSData;
	}

	public CBOperationSupportBean getNonGridOPSData()
	{
		return nonGridOPSData;
	}

	public void setNonGridOPSData(CBOperationSupportBean nonGridOPSData)
	{
		this.nonGridOPSData = nonGridOPSData;
	}

	public ContractedServicesBean getNonGridConServiceData()
	{
		return nonGridConServiceData;
	}

	public void setNonGridConServiceData(ContractedServicesBean nonGridConServiceData)
	{
		this.nonGridConServiceData = nonGridConServiceData;
	}

	public List<CBEquipmentBean> getEquipmentBeanList()
	{
		return equipmentBeanList;
	}

	public void setEquipmentBeanList(List<CBEquipmentBean> equipmentBeanList)
	{
		this.equipmentBeanList = equipmentBeanList;
	}

	public List<CBIndirectRateBean> getIndirectBeanList()
	{
		return indirectBeanList;
	}

	public void setIndirectBeanList(List<CBIndirectRateBean> indirectBeanList)
	{
		this.indirectBeanList = indirectBeanList;
	}

	public List<CBOperationSupportBean> getOpsBeanList()
	{
		return opsBeanList;
	}

	public void setOpsBeanList(List<CBOperationSupportBean> opsBeanList)
	{
		this.opsBeanList = opsBeanList;
	}

	public List<CBProfessionalServicesBean> getProfserviceBeanList()
	{
		return profserviceBeanList;
	}

	public void setProfserviceBeanList(List<CBProfessionalServicesBean> profserviceBeanList)
	{
		this.profserviceBeanList = profserviceBeanList;
	}

	public List<CBProgramIncomeBean> getProgramincomeBeanList()
	{
		return programincomeBeanList;
	}

	public void setProgramincomeBeanList(List<CBProgramIncomeBean> programincomeBeanList)
	{
		this.programincomeBeanList = programincomeBeanList;
	}

	public List<CBUtilities> getUtilityBeanList()
	{
		return utilityBeanList;
	}

	public void setUtilityBeanList(List<CBUtilities> utilityBeanList)
	{
		this.utilityBeanList = utilityBeanList;
	}

	public List<PersonnelServiceBudget> getPersonnelserviceBeanList()
	{
		return personnelserviceBeanList;
	}

	public void setPersonnelserviceBeanList(List<PersonnelServiceBudget> personnelserviceBeanList)
	{
		this.personnelserviceBeanList = personnelserviceBeanList;
	}

	public List<Rent> getRentBeanList()
	{
		return rentBeanList;
	}

	public void setRentBeanList(List<Rent> rentBeanList)
	{
		this.rentBeanList = rentBeanList;
	}

	public List<UnallocatedFunds> getUnallocatedBeanList()
	{
		return unallocatedBeanList;
	}

	public void setUnallocatedBeanList(List<UnallocatedFunds> unallocatedBeanList)
	{
		this.unallocatedBeanList = unallocatedBeanList;
	}

	public List<ContractedServicesBean> getContractedserviceBeanList()
	{
		return contractedserviceBeanList;
	}

	public void setContractedserviceBeanList(List<ContractedServicesBean> contractedserviceBeanList)
	{
		this.contractedserviceBeanList = contractedserviceBeanList;
	}

	public void setRateBeanList(List<RateBean> rateBeanList)
	{
		this.rateBeanList = rateBeanList;
	}

	public List<RateBean> getRateBeanList()
	{
		return rateBeanList;
	}

	public void setSubbudgetId(String subbudgetId)
	{
		this.subbudgetId = subbudgetId;
	}

	public String getSubbudgetId()
	{
		return subbudgetId;
	}

	public void setMilestoneBeanList(List<CBMileStoneBean> milestoneBeanList)
	{
		this.milestoneBeanList = milestoneBeanList;
	}

	public List<CBMileStoneBean> getMilestoneBeanList()
	{
		return milestoneBeanList;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 * Updated in R7 : Program Income
	 */
	@Override
	public String toString()
	{
		return "LineItemMasterBean ["
				+ (subbudgetId != null ? "subbudgetId=" + subbudgetId + ", " : "")
				+ (indirectRatePercent != null ? "indirectRatePercent=" + indirectRatePercent + ", " : "")
				+ (nonGridPSData != null ? "nonGridPSData=" + nonGridPSData + ", " : "")
				+ (loBudgetSummary != null ? "loBudgetSummary=" + loBudgetSummary + ", " : "")
				+ (nonGridOPSData != null ? "nonGridOPSData=" + nonGridOPSData + ", " : "")
				+ (nonGridConServiceData != null ? "nonGridConServiceData=" + nonGridConServiceData + ", " : "")
				+ (rateBeanList != null ? "rateBeanList=" + rateBeanList + ", " : "")
				+ (milestoneBeanList != null ? "milestoneBeanList=" + milestoneBeanList + ", " : "")
				+ (equipmentBeanList != null ? "equipmentBeanList=" + equipmentBeanList + ", " : "")
				+ (indirectBeanList != null ? "indirectBeanList=" + indirectBeanList + ", " : "")
				+ (opsBeanList != null ? "opsBeanList=" + opsBeanList + ", " : "")
				+ (profserviceBeanList != null ? "profserviceBeanList=" + profserviceBeanList + ", " : "")
				+ (programincomeBeanList != null ? "programincomeBeanList=" + programincomeBeanList + ", " : "")
				+ (utilityBeanList != null ? "utilityBeanList=" + utilityBeanList + ", " : "")
				+ (personnelserviceBeanList != null ? "personnelserviceBeanList=" + personnelserviceBeanList + ", "
						: "")
				+ (rentBeanList != null ? "rentBeanList=" + rentBeanList + ", " : "")
				+ (unallocatedBeanList != null ? "unallocatedBeanList=" + unallocatedBeanList + ", " : "")
				+ (contractedserviceBeanList != null ? "contractedserviceBeanList=" + contractedserviceBeanList + ", "
						: "")
				+ (siteDetailsBeanList != null ? "siteDetailsBeanList=" + siteDetailsBeanList + ", " : "")
				+ (piIndirectRatePercent != null ? "piIndirectRatePercent=" + piIndirectRatePercent : "") 
				+ (costCenterBeanList != null ? "costCenterBeanList=" + costCenterBeanList : "")
				+ (servicesBeanList != null ? "servicesBeanList" + servicesBeanList : "") + "]";
	}

	//Made changes for release 3.6.0 Enhancement id 6484
	public void setSiteDetailsBeanList(List<SiteDetailsBean> siteDetailsBeanList) {
		this.siteDetailsBeanList = siteDetailsBeanList;
	}

	public List<SiteDetailsBean> getSiteDetailsBeanList() {
		return siteDetailsBeanList;
	}

}
