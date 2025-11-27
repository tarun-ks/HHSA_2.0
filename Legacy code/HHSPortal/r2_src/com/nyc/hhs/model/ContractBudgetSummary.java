package com.nyc.hhs.model;

public class ContractBudgetSummary
{

	private BudgetDetails totalCityFundedBudget = new BudgetDetails();
	private BudgetDetails totalSalary;
	private BudgetDetails totalFringes;
	private BudgetDetails operationsAndSupportAmount = new BudgetDetails();
	private BudgetDetails operationsSupportAndEquipmentAmount = new BudgetDetails();
	private BudgetDetails totalOTPSAmount = new BudgetDetails();
	private BudgetDetails totalSalaryAndFringesAmount = new BudgetDetails();
	private BudgetDetails totalDirectsCosts = new BudgetDetails();
	private BudgetDetails equipmentAmount = new BudgetDetails();
	private BudgetDetails utilitiesAmount;
	private BudgetDetails professionalServicesAmount;
	private BudgetDetails rentAndOccupancyAmount;
	private BudgetDetails contractedServicesAmount;
	private BudgetDetails totalRateBasedAmount;
	private BudgetDetails totalMilestoneBasedAmount;
	private BudgetDetails unallocatedFunds;
	private BudgetDetails totalIndirectCosts;
	private BudgetDetails totalProgramIncome;
	private BudgetDetails totalProgramBudget = new BudgetDetails();

	/**
	 * @return the operationsSupportAndEquipmentAmount
	 */
	public BudgetDetails getOperationsSupportAndEquipmentAmount()
	{
		return operationsSupportAndEquipmentAmount;
	}

	/**
	 * @param operationsSupportAndEquipmentAmount the
	 *            operationsSupportAndEquipmentAmount to set
	 */
	public void setOperationsSupportAndEquipmentAmount(BudgetDetails operationsSupportAndEquipmentAmount)
	{
		this.operationsSupportAndEquipmentAmount = operationsSupportAndEquipmentAmount;
	}

	/**
	 * @return the equipmentAmount
	 */
	public BudgetDetails getEquipmentAmount()
	{
		return equipmentAmount;
	}

	/**
	 * @param equipmentAmount the equipmentAmount to set
	 */
	public void setEquipmentAmount(BudgetDetails equipmentAmount)
	{
		this.equipmentAmount = equipmentAmount;
	}

	/**
	 * @return the totalOTPSAmount
	 */
	public BudgetDetails getTotalOTPSAmount()
	{
		return totalOTPSAmount;
	}

	/**
	 * @param totalOTPSAmount the totalOTPSAmount to set
	 */
	public void setTotalOTPSAmount(BudgetDetails totalOTPSAmount)
	{
		this.totalOTPSAmount = totalOTPSAmount;
	}

	/**
	 * @return the totalSalaryAndFringesAmount
	 */
	public BudgetDetails getTotalSalaryAndFringesAmount()
	{
		return totalSalaryAndFringesAmount;
	}

	/**
	 * @param totalSalaryAndFringesAmount the totalSalaryAndFringesAmount to set
	 */
	public void setTotalSalaryAndFringesAmount(BudgetDetails totalSalaryAndFringesAmount)
	{
		this.totalSalaryAndFringesAmount = totalSalaryAndFringesAmount;
	}

	/**
	 * @return the totalDirectsCosts
	 */
	public BudgetDetails getTotalDirectsCosts()
	{
		return totalDirectsCosts;
	}

	/**
	 * @param totalDirectsCosts the totalDirectsCosts to set
	 */
	public void setTotalDirectsCosts(BudgetDetails totalDirectsCosts)
	{
		this.totalDirectsCosts = totalDirectsCosts;
	}

	public BudgetDetails getTotalCityFundedBudget()
	{
		return totalCityFundedBudget;
	}

	public void setTotalCityFundedBudget(BudgetDetails totalCityFundedBudget)
	{
		this.totalCityFundedBudget = totalCityFundedBudget;
	}

	public BudgetDetails getTotalSalary()
	{
		return totalSalary;
	}

	public void setTotalSalary(BudgetDetails totalSalary)
	{
		this.totalSalary = totalSalary;
	}

	public BudgetDetails getTotalFringes()
	{
		return totalFringes;
	}

	public void setTotalFringes(BudgetDetails totalFringe)
	{
		this.totalFringes = totalFringe;
	}

	public BudgetDetails getOperationsAndSupportAmount()
	{
		return operationsAndSupportAmount;
	}

	public void setOperationsAndSupportAmount(BudgetDetails operationsAndSupportAmount)
	{
		this.operationsAndSupportAmount = operationsAndSupportAmount;
	}

	public BudgetDetails getUtilitiesAmount()
	{
		return utilitiesAmount;
	}

	public void setUtilitiesAmount(BudgetDetails utilitiesAmount)
	{
		this.utilitiesAmount = utilitiesAmount;
	}

	public BudgetDetails getProfessionalServicesAmount()
	{
		return professionalServicesAmount;
	}

	public void setProfessionalServicesAmount(BudgetDetails professionalServicesAmount)
	{
		this.professionalServicesAmount = professionalServicesAmount;
	}

	public BudgetDetails getRentAndOccupancyAmount()
	{
		return rentAndOccupancyAmount;
	}

	public void setRentAndOccupancyAmount(BudgetDetails rentAndOccupancyAmount)
	{
		this.rentAndOccupancyAmount = rentAndOccupancyAmount;
	}

	public BudgetDetails getContractedServicesAmount()
	{
		return contractedServicesAmount;
	}

	public void setContractedServicesAmount(BudgetDetails contractedServicesAmount)
	{
		this.contractedServicesAmount = contractedServicesAmount;
	}

	public BudgetDetails getTotalRateBasedAmount()
	{
		return totalRateBasedAmount;
	}

	public void setTotalRateBasedAmount(BudgetDetails totalRateBasedAmount)
	{
		this.totalRateBasedAmount = totalRateBasedAmount;
	}

	public BudgetDetails getTotalMilestoneBasedAmount()
	{
		return totalMilestoneBasedAmount;
	}

	public void setTotalMilestoneBasedAmount(BudgetDetails totalMilestoneBasedAmount)
	{
		this.totalMilestoneBasedAmount = totalMilestoneBasedAmount;
	}

	public BudgetDetails getUnallocatedFunds()
	{
		return unallocatedFunds;
	}

	public void setUnallocatedFunds(BudgetDetails unallocatedFunds)
	{
		this.unallocatedFunds = unallocatedFunds;
	}

	public BudgetDetails getTotalIndirectCosts()
	{
		return totalIndirectCosts;
	}

	public void setTotalIndirectCosts(BudgetDetails totalIndirectCosts)
	{
		this.totalIndirectCosts = totalIndirectCosts;
	}

	public BudgetDetails getTotalProgramIncome()
	{
		return totalProgramIncome;
	}

	public void setTotalProgramIncome(BudgetDetails totalProgramIncome)
	{
		this.totalProgramIncome = totalProgramIncome;
	}

	public BudgetDetails getTotalProgramBudget()
	{
		return totalProgramBudget;
	}

	public void setTotalProgramBudget(BudgetDetails totalProgramBudget)
	{
		this.totalProgramBudget = totalProgramBudget;
	}

	@Override
	public String toString()
	{
		return "ContractBudgetSummary [totalCityFundedBudget=" + totalCityFundedBudget + ", totalSalary=" + totalSalary
				+ ", totalFringes=" + totalFringes + ", operationsAndSupportAmount=" + operationsAndSupportAmount
				+ ", operationsSupportAndEquipmentAmount=" + operationsSupportAndEquipmentAmount + ", totalOTPSAmount="
				+ totalOTPSAmount + ", totalSalaryAndFringesAmount=" + totalSalaryAndFringesAmount
				+ ", totalDirectsCosts=" + totalDirectsCosts + ", equipmentAmount=" + equipmentAmount
				+ ", utilitiesAmount=" + utilitiesAmount + ", professionalServicesAmount=" + professionalServicesAmount
				+ ", rentAndOccupancyAmount=" + rentAndOccupancyAmount + ", contractedServicesAmount="
				+ contractedServicesAmount + ", totalRateBasedAmount=" + totalRateBasedAmount
				+ ", totalMilestoneBasedAmount=" + totalMilestoneBasedAmount + ", unallocatedFunds=" + unallocatedFunds
				+ ", totalIndirectCosts=" + totalIndirectCosts + ", totalProgramIncome=" + totalProgramIncome
				+ ", totalProgramBudget=" + totalProgramBudget + "]";
	}

}
