package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

public class FiscalYear
{

	@RegExp(value ="^\\d{0,22}")
	private String fiscalYearId;
	@Length(max = 20)
	private String fiscalYear;

	public String getFiscalYearId()
	{
		return fiscalYearId;
	}

	public void setFiscalYearId(String fiscalYearId)
	{
		this.fiscalYearId = fiscalYearId;
	}

	public String getFiscalYear()
	{
		return fiscalYear;
	}

	public void setFiscalYear(String fiscalYear)
	{
		this.fiscalYear = fiscalYear;
	}

	@Override
	public String toString()
	{
		return "FiscalYear [fiscalYearId=" + fiscalYearId + ", fiscalYear=" + fiscalYear + "]";
	}

}
