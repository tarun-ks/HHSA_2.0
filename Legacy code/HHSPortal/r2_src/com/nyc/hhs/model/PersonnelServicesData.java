package com.nyc.hhs.model;

import java.math.BigDecimal;

public class PersonnelServicesData
{

	private BigDecimal totalSalaryAndFringeAmount;
	private BigDecimal totalSalaryAmount;
	private BigDecimal totalFringeAmount;
	private BigDecimal totalYtdInvoicedAmount;
	private BigDecimal fringePercentage;
	// Added in R6
	private String totalPositions;
	private String totalCityFte;
	
	public String getTotalCityFte()
	{
		return totalCityFte;
	}

	public void setTotalCityFte(String totalCityFte)
	{
		this.totalCityFte = totalCityFte;
	}

	public String getTotalPositions()
	{
		return totalPositions;
	}

	public void setTotalPositions(String totalPositions)
	{
		this.totalPositions = totalPositions;
	}

	public BigDecimal getTotalSalaryAndFringeAmount()
	{
		return totalSalaryAndFringeAmount;
	}

	public void setTotalSalaryAndFringeAmount(BigDecimal totalSalaryAndFringeAmount)
	{
		this.totalSalaryAndFringeAmount = totalSalaryAndFringeAmount;
	}

	public BigDecimal getTotalSalaryAmount()
	{
		return totalSalaryAmount;
	}

	public void setTotalSalaryAmount(BigDecimal totalSalaryAmount)
	{
		this.totalSalaryAmount = totalSalaryAmount;
	}

	public BigDecimal getTotalFringeAmount()
	{
		return totalFringeAmount;
	}

	public void setTotalFringeAmount(BigDecimal totalFringeAmount)
	{
		this.totalFringeAmount = totalFringeAmount;
	}

	public BigDecimal getTotalYtdInvoicedAmount()
	{
		return totalYtdInvoicedAmount;
	}

	public void setTotalYtdInvoicedAmount(BigDecimal totalYtdInvoicedAmount)
	{
		this.totalYtdInvoicedAmount = totalYtdInvoicedAmount;
	}

	public BigDecimal getFringePercentage()
	{
		return fringePercentage;
	}

	public void setFringePercentage(BigDecimal fringePercentage)
	{
		this.fringePercentage = fringePercentage;
	}

	@Override
	public String toString()
	{
		return "PersonnelServicesData [totalSalaryAndFringeAmount=" + totalSalaryAndFringeAmount
				+ ", totalSalaryAmount=" + totalSalaryAmount + ", totalFringeAmount=" + totalFringeAmount
				+ ", totalYtdInvoicedAmount=" + totalYtdInvoicedAmount + ", fringePercentage=" + fringePercentage + "]";
	}

}
