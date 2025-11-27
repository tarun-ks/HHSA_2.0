package com.nyc.hhs.model;
//This is a Bean class added for ReportMapping functionality
public class ReportMapping
{

	private String reportId;
	private String dashBoradReportId;
	private String reportValue;
	private String configFile;
	private String reportType;
	
	public ReportMapping()
	{
		super();
	}

	public String getReportId()
	{
		return reportId;
	}

	public void setReportId(String reportId)
	{
		this.reportId = reportId;
	}

	public String getReportValue()
	{
		return reportValue;
	}

	public void setReportValue(String reportValue)
	{
		this.reportValue = reportValue;
	}

	/**
	 * @return the dashBoradReportId
	 */
	public String getDashBoradReportId()
	{
		return dashBoradReportId;
	}

	/**
	 * @param dashBoradReportId the dashBoradReportId to set
	 */
	public void setDashBoradReportId(String dashBoradReportId)
	{
		this.dashBoradReportId = dashBoradReportId;
	}

	/**
	 * @return the configFile
	 */
	public String getConfigFile()
	{
		return configFile;
	}

	/**
	 * @param configFile the configFile to set
	 */
	public void setConfigFile(String configFile)
	{
		this.configFile = configFile;
	}

	/**
	 * @return the reportType
	 */
	public String getReportType()
	{
		return reportType;
	}

	/**
	 * @param reportType the reportType to set
	 */
	public void setReportType(String reportType)
	{
		this.reportType = reportType;
	}

}
