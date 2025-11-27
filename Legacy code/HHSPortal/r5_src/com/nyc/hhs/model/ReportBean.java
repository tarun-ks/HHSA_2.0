package com.nyc.hhs.model;
//This is a Bean class added for Reporting functionality
import java.io.Serializable;
import java.sql.Date;
import java.util.List;

import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

public class ReportBean extends BaseFilter implements Serializable
{
	private static final long serialVersionUID = 1L; 
	private String columnName;
	private String headingName;
	private String align;
	private String size;
	private Boolean dataGrid;
	private String reportParameterName;
	private Object reportParameterValue;
	private List<String> reportParameterNameList;
	private List<String> reportParameterValueList;
	private String reportVariableName;
	private String reportVariableValue;
	private List<String> reportVariableNameList;
	private List<String> reportVariableValueList;
	private String reportVariableScope;
	private String reportExportFormat;
	private String reportExportTitle;
	private String reportExportKey;
	private List<String> reportExportTitleList;
	private List<String> reportExportKeyList;
	private Integer reportHeight;
	private Integer reportWidth;
	private Integer ammendAmount;
	private Double projectedBudgetAmount;
	private String fyID;
	private String amountType;
	private String contractType;
	private Double paymentAmount;
	private Double invociedAmount;
	private String invoicedAmount;
	private String paymentAmountGrid;
	private String fyYear;
	private Double budgetAmount;
	@RegExp(value ="^\\d{0,22}")
	private String budgetId;
	@RegExp(value ="^\\d{0,22}")
	private String invoiceId;
	private Date invoiceStartDate;
	private String invoiceNumber;
	private String agencyInvoiceNumber;
	private String providerInvoiceNumber;
	private Integer monthRowNum;
	private Integer rowNumber;
	private Integer sortoder;
	private String monthName;
	private String currentMonth;
	private String subBudgetName;
	private String agencyId;
	private String ctNumber;
	private String contractTitle;
	private String fyYearAmount;
	private String pendingFyAmendmentAmount;
	private String orgId;
	private Double totalFYAmount;
	private String sqlId;
	private String fiscalYearSqlId;
	private Integer providerCount;
	private Integer agencyCount;
	private Integer programCount;
	private Integer registeredContractCount;
	private Double cityBudgetAmount;
	private Double pendingAmendAmount;
	private String budgetStartDate;
	private String budgetEndDate;
	private String dateApproved;
	private String contractStartDate;
	private String contractEndDate;
	private String programName;
	private String amendmentContractTitle;
	private String reportList;
	private String reportId;
	private String reportType;
	private String status;
	private String contractStatus;
	private String procurementStatus;
	private String providerStatus;
	private String proposalStatus;
	private String refReleaseDate;
	private String updProposalDueDate;
	private String plndProposalDueDate;
	private String ein;
	private String providerIdReport;
	private String providerId;
	private String provider;
	private String score;
	private String compitionPool;
	private String proposalTitle;
	@RegExp(value ="^\\d{0,22}")
	private String proposalId;
	private String procurementTitle;
	private String submitDate;
	private String submitDateFrom;
	private String submitDateTo;
	private String serviceStartDate;
	private String serviceEndDate;
	private String modifiedDate;
	private String modifiedByUserId;
	private String desc;
	private String advanceStatus;
	private String advanceAmount;
	private String totalAdvance;
	private String advanceRecouped;
	private String percentRecouped;
	private String percentUtilized;
	private String overUnder;
	private String invoiceEndDate;
	private String amount;
	private String agencyName;
	private String contractEPIN;
	private String amendmentEPIN;
	private String amendmentBudgetAmount;
	private String assignmentAmount;
	private String recoupedAmount;
	private String proposedPaymentToVendor;
	private String approvedInvoicedPendingDsiburesement;
	private String disbursmentNumber;
	private Date disbursmentDate;
	private String paymentVoucherNumber;
	private String voucherNumber;
	private String approvedAdvancePendingDsiburesement;
	private String statusId;
	private String organizationType;
	private String payeeVendorId;
	private String budgetAdvanceId;
	
	

	public ReportBean()
	{
		super();
	}

	public String getOrganizationType()
	{
		return organizationType;
	}

	public void setOrganizationType(String organizationType)
	{
		this.organizationType = organizationType;
	}

	public String getColumnName()
	{
		return columnName;
	}

	public void setColumnName(String columnName)
	{
		this.columnName = columnName;
	}

	public String getHeadingName()
	{
		return headingName;
	}

	public void setHeadingName(String headingName)
	{
		this.headingName = headingName;
	}

	public String getAlign()
	{
		return align;
	}

	public void setAlign(String align)
	{
		this.align = align;
	}

	public String getSize()
	{
		return size;
	}

	public void setSize(String size)
	{
		this.size = size;
	}

	public Boolean getDataGrid()
	{
		return dataGrid;
	}

	public void setDataGrid(Boolean dataGrid)
	{
		this.dataGrid = dataGrid;
	}

	public String getReportParameterName()
	{
		return reportParameterName;
	}

	public void setReportParameterName(String reportParameterName)
	{
		this.reportParameterName = reportParameterName;
	}

	public Object getReportParameterValue()
	{
		return reportParameterValue;
	}

	public void setReportParameterValue(Object reportParameterValue)
	{
		this.reportParameterValue = reportParameterValue;
	}

	public List<String> getReportParameterNameList()
	{
		return reportParameterNameList;
	}

	public void setReportParameterNameList(List<String> reportParameterNameList)
	{
		this.reportParameterNameList = reportParameterNameList;
	}

	public List<String> getReportParameterValueList()
	{
		return reportParameterValueList;
	}

	public void setReportParameterValueList(List<String> reportParameterValueList)
	{
		this.reportParameterValueList = reportParameterValueList;
	}

	public String getReportVariableName()
	{
		return reportVariableName;
	}

	public void setReportVariableName(String reportVariableName)
	{
		this.reportVariableName = reportVariableName;
	}

	public String getReportVariableValue()
	{
		return reportVariableValue;
	}

	public void setReportVariableValue(String reportVariableValue)
	{
		this.reportVariableValue = reportVariableValue;
	}

	public List<String> getReportVariableNameList()
	{
		return reportVariableNameList;
	}

	public void setReportVariableNameList(List<String> reportVariableNameList)
	{
		this.reportVariableNameList = reportVariableNameList;
	}

	public List<String> getReportVariableValueList()
	{
		return reportVariableValueList;
	}

	public void setReportVariableValueList(List<String> reportVariableValueList)
	{
		this.reportVariableValueList = reportVariableValueList;
	}

	public String getReportVariableScope()
	{
		return reportVariableScope;
	}

	public void setReportVariableScope(String reportVariableScope)
	{
		this.reportVariableScope = reportVariableScope;
	}

	public String getReportExportFormat()
	{
		return reportExportFormat;
	}

	public void setReportExportFormat(String reportExportFormat)
	{
		this.reportExportFormat = reportExportFormat;
	}

	public String getReportExportTitle()
	{
		return reportExportTitle;
	}

	public void setReportExportTitle(String reportExportTitle)
	{
		this.reportExportTitle = reportExportTitle;
	}

	public String getReportExportKey()
	{
		return reportExportKey;
	}

	public void setReportExportKey(String reportExportKey)
	{
		this.reportExportKey = reportExportKey;
	}

	public List<String> getReportExportTitleList()
	{
		return reportExportTitleList;
	}

	public void setReportExportTitleList(List<String> reportExportTitleList)
	{
		this.reportExportTitleList = reportExportTitleList;
	}

	public List<String> getReportExportKeyList()
	{
		return reportExportKeyList;
	}

	public void setReportExportKeyList(List<String> reportExportKeyList)
	{
		this.reportExportKeyList = reportExportKeyList;
	}

	public Integer getReportHeight()
	{
		return reportHeight;
	}

	public void setReportHeight(Integer reportHeight)
	{
		this.reportHeight = reportHeight;
	}

	public Integer getReportWidth()
	{
		return reportWidth;
	}

	public void setReportWidth(Integer reportWidth)
	{
		this.reportWidth = reportWidth;
	}

	public Integer getAmmendAmount()
	{
		return ammendAmount;
	}

	public void setAmmendAmount(Integer ammendAmount)
	{
		this.ammendAmount = ammendAmount;
	}

	public Double getProjectedBudgetAmount()
	{
		return projectedBudgetAmount;
	}

	public void setProjectedBudgetAmount(Double projectedBudgetAmount)
	{
		this.projectedBudgetAmount = projectedBudgetAmount;
	}

	public String getFyID()
	{
		return fyID;
	}

	public void setFyID(String fyID)
	{
		this.fyID = fyID;
	}

	public String getAmountType()
	{
		return amountType;
	}

	public void setAmountType(String amountType)
	{
		this.amountType = amountType;
	}

	public String getContractType()
	{
		return contractType;
	}

	public void setContractType(String contractType)
	{
		this.contractType = contractType;
	}

	public Double getPaymentAmount()
	{
		return paymentAmount;
	}

	public void setPaymentAmount(Double paymentAmount)
	{
		this.paymentAmount = paymentAmount;
	}

	public Double getInvociedAmount()
	{
		return invociedAmount;
	}

	public void setInvociedAmount(Double invociedAmount)
	{
		this.invociedAmount = invociedAmount;
	}

	public String getFyYear()
	{
		return fyYear;
	}

	public void setFyYear(String fyYear)
	{
		this.fyYear = fyYear;
	}

	public Double getBudgetAmount()
	{
		return budgetAmount;
	}

	public void setBudgetAmount(Double budgetAmount)
	{
		this.budgetAmount = budgetAmount;
	}

	public Date getInvoiceStartDate()
	{
		return invoiceStartDate;
	}

	public void setInvoiceStartDate(Date invoiceStartDate)
	{
		this.invoiceStartDate = invoiceStartDate;
	}

	public String getInvoiceNumber()
	{
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber)
	{
		this.invoiceNumber = invoiceNumber;
	}

	public String getAgencyInvoiceNumber()
	{
		return agencyInvoiceNumber;
	}

	public void setAgencyInvoiceNumber(String agencyInvoiceNumber)
	{
		this.agencyInvoiceNumber = agencyInvoiceNumber;
	}

	public String getProviderInvoiceNumber()
	{
		return providerInvoiceNumber;
	}

	public void setProviderInvoiceNumber(String providerInvoiceNumber)
	{
		this.providerInvoiceNumber = providerInvoiceNumber;
	}

	public Integer getMonthRowNum()
	{
		return monthRowNum;
	}

	public void setMonthRowNum(Integer monthRowNum)
	{
		this.monthRowNum = monthRowNum;
	}

	public Integer getRowNumber()
	{
		return rowNumber;
	}

	public void setRowNumber(Integer rowNumber)
	{
		this.rowNumber = rowNumber;
	}

	public Integer getSortoder()
	{
		return sortoder;
	}

	public void setSortoder(Integer sortoder)
	{
		this.sortoder = sortoder;
	}

	public String getMonthName()
	{
		return monthName;
	}

	public void setMonthName(String monthName)
	{
		this.monthName = monthName;
	}

	public String getSubBudgetName()
	{
		return subBudgetName;
	}

	public void setSubBudgetName(String subBudgetName)
	{
		this.subBudgetName = subBudgetName;
	}

	public String getAgencyId()
	{
		return agencyId;
	}

	public void setAgencyId(String agencyId)
	{
		this.agencyId = agencyId;
	}

	public String getCtNumber()
	{
		return ctNumber;
	}

	public void setCtNumber(String ctNumber)
	{
		this.ctNumber = ctNumber;
	}

	public String getContractTitle()
	{
		return contractTitle;
	}

	public void setContractTitle(String contractTitle)
	{
		this.contractTitle = contractTitle;
	}

	public String getFyYearAmount()
	{
		return fyYearAmount;
	}

	public void setFyYearAmount(String fyYearAmount)
	{
		this.fyYearAmount = fyYearAmount;
	}

	public String getPendingFyAmendmentAmount()
	{
		return pendingFyAmendmentAmount;
	}

	public void setPendingFyAmendmentAmount(String pendingFyAmendmentAmount)
	{
		this.pendingFyAmendmentAmount = pendingFyAmendmentAmount;
	}

	public String getOrgId()
	{
		return orgId;
	}

	public void setOrgId(String orgId)
	{
		this.orgId = orgId;
	}

	public Double getTotalFYAmount()
	{
		return totalFYAmount;
	}

	public void setTotalFYAmount(Double totalFYAmount)
	{
		this.totalFYAmount = totalFYAmount;
	}

	public String getSqlId()
	{
		return sqlId;
	}

	public void setSqlId(String sqlId)
	{
		this.sqlId = sqlId;
	}

	public Integer getProviderCount()
	{
		return providerCount;
	}

	public void setProviderCount(Integer providerCount)
	{
		this.providerCount = providerCount;
	}

	public Integer getAgencyCount()
	{
		return agencyCount;
	}

	public void setAgencyCount(Integer agencyCount)
	{
		this.agencyCount = agencyCount;
	}

	public Integer getProgramCount()
	{
		return programCount;
	}

	public void setProgramCount(Integer programCount)
	{
		this.programCount = programCount;
	}

	public Integer getRegisteredContractCount()
	{
		return registeredContractCount;
	}

	public void setRegisteredContractCount(Integer registeredContractCount)
	{
		this.registeredContractCount = registeredContractCount;
	}

	public Double getCityBudgetAmount()
	{
		return cityBudgetAmount;
	}

	public void setCityBudgetAmount(Double cityBudgetAmount)
	{
		this.cityBudgetAmount = cityBudgetAmount;
	}

	
	public String getBudgetStartDate()
	{
		return budgetStartDate;
	}

	public void setBudgetStartDate(String budgetStartDate)
	{
		this.budgetStartDate = budgetStartDate;
	}

	public String getBudgetEndDate()
	{
		return budgetEndDate;
	}

	public void setBudgetEndDate(String budgetEndDate)
	{
		this.budgetEndDate = budgetEndDate;
	}

	public String getDateApproved()
	{
		return dateApproved;
	}

	public void setDateApproved(String dateApproved)
	{
		this.dateApproved = dateApproved;
	}

	public String getContractStartDate()
	{
		return contractStartDate;
	}

	public void setContractStartDate(String contractStartDate)
	{
		this.contractStartDate = contractStartDate;
	}

	public String getContractEndDate()
	{
		return contractEndDate;
	}

	public void setContractEndDate(String contractEndDate)
	{
		this.contractEndDate = contractEndDate;
	}

	public String getProgramName()
	{
		return programName;
	}

	public void setProgramName(String programName)
	{
		this.programName = programName;
	}

	public String getAmendmentContractTitle()
	{
		return amendmentContractTitle;
	}

	public void setAmendmentContractTitle(String amendmentContractTitle)
	{
		this.amendmentContractTitle = amendmentContractTitle;
	}

	public String getReportList()
	{
		return reportList;
	}

	public void setReportList(String reportList)
	{
		this.reportList = reportList;
	}

	public String getReportId()
	{
		return reportId;
	}

	public void setReportId(String reportId)
	{
		this.reportId = reportId;
	}

	public String getReportType()
	{
		return reportType;
	}

	public void setReportType(String reportType)
	{
		this.reportType = reportType;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getProcurementStatus()
	{
		return procurementStatus;
	}

	public void setProcurementStatus(String procurementStatus)
	{
		this.procurementStatus = procurementStatus;
	}

	public String getProviderStatus()
	{
		return providerStatus;
	}

	public void setProviderStatus(String providerStatus)
	{
		this.providerStatus = providerStatus;
	}

	public String getProposalStatus()
	{
		return proposalStatus;
	}

	public void setProposalStatus(String proposalStatus)
	{
		this.proposalStatus = proposalStatus;
	}

	public String getRefReleaseDate()
	{
		return refReleaseDate;
	}

	public void setRefReleaseDate(String refReleaseDate)
	{
		this.refReleaseDate = refReleaseDate;
	}

	public String getUpdProposalDueDate()
	{
		return updProposalDueDate;
	}

	public void setUpdProposalDueDate(String updProposalDueDate)
	{
		this.updProposalDueDate = updProposalDueDate;
	}

	public String getEin()
	{
		return ein;
	}

	public void setEin(String ein)
	{
		this.ein = ein;
	}



	public String getProvider()
	{
		return provider;
	}

	public void setProvider(String provider)
	{
		this.provider = provider;
	}

	public String getScore()
	{
		return score;
	}

	public void setScore(String score)
	{
		this.score = score;
	}

	public String getCompitionPool()
	{
		return compitionPool;
	}

	public void setCompitionPool(String compitionPool)
	{
		this.compitionPool = compitionPool;
	}

	public String getProposalTitle()
	{
		return proposalTitle;
	}

	public void setProposalTitle(String proposalTitle)
	{
		this.proposalTitle = proposalTitle;
	}

	public String getProposalId()
	{
		return proposalId;
	}

	public void setProposalId(String proposalId)
	{
		this.proposalId = proposalId;
	}

	public String getProcurementTitle()
	{
		return procurementTitle;
	}

	public void setProcurementTitle(String procurementTitle)
	{
		this.procurementTitle = procurementTitle;
	}

	public String getSubmitDate()
	{
		return submitDate;
	}

	public void setSubmitDate(String submitDate)
	{
		this.submitDate = submitDate;
	}

	public String getSubmitDateFrom()
	{
		return submitDateFrom;
	}

	public void setSubmitDateFrom(String submitDateFrom)
	{
		this.submitDateFrom = submitDateFrom;
	}

	public String getSubmitDateTo()
	{
		return submitDateTo;
	}

	public void setSubmitDateTo(String submitDateTo)
	{
		this.submitDateTo = submitDateTo;
	}

	public String getServiceStartDate()
	{
		return serviceStartDate;
	}

	public void setServiceStartDate(String serviceStartDate)
	{
		this.serviceStartDate = serviceStartDate;
	}

	public String getServiceEndDate()
	{
		return serviceEndDate;
	}

	public void setServiceEndDate(String serviceEndDate)
	{
		this.serviceEndDate = serviceEndDate;
	}

	public String getModifiedDate()
	{
		return modifiedDate;
	}

	public void setModifiedDate(String modifiedDate)
	{
		this.modifiedDate = modifiedDate;
	}

	public String getModifiedByUserId()
	{
		return modifiedByUserId;
	}

	public void setModifiedByUserId(String modifiedByUserId)
	{
		this.modifiedByUserId = modifiedByUserId;
	}

	public String getDesc()
	{
		return desc;
	}

	public void setDesc(String desc)
	{
		this.desc = desc;
	}

	public String getAdvanceStatus()
	{
		return advanceStatus;
	}

	public void setAdvanceStatus(String advanceStatus)
	{
		this.advanceStatus = advanceStatus;
	}

	public String getAdvanceAmount()
	{
		return advanceAmount;
	}

	public void setAdvanceAmount(String advanceAmount)
	{
		this.advanceAmount = advanceAmount;
	}

	public String getAdvanceRecouped()
	{
		return advanceRecouped;
	}

	public void setAdvanceRecouped(String advanceRecouped)
	{
		this.advanceRecouped = advanceRecouped;
	}

	public String getPercentRecouped()
	{
		return percentRecouped;
	}

	public void setPercentRecouped(String percentRecouped)
	{
		this.percentRecouped = percentRecouped;
	}

	public String getPercentUtilized()
	{
		return percentUtilized;
	}

	public void setPercentUtilized(String percentUtilized)
	{
		this.percentUtilized = percentUtilized;
	}

	public String getOverUnder()
	{
		return overUnder;
	}

	public void setOverUnder(String overUnder)
	{
		this.overUnder = overUnder;
	}

	public String getInvoiceEndDate()
	{
		return invoiceEndDate;
	}

	public void setInvoiceEndDate(String invoiceEndDate)
	{
		this.invoiceEndDate = invoiceEndDate;
	}

	public String getAmount()
	{
		return amount;
	}

	public void setAmount(String amount)
	{
		this.amount = amount;
	}

	public String getAgencyName()
	{
		return agencyName;
	}

	public void setAgencyName(String agencyName)
	{
		this.agencyName = agencyName;
	}

	public String getContractEPIN()
	{
		return contractEPIN;
	}

	public void setContractEPIN(String contractEPIN)
	{
		this.contractEPIN = contractEPIN;
	}

	public String getAmendmentEPIN()
	{
		return amendmentEPIN;
	}

	public void setAmendmentEPIN(String amendmentEPIN)
	{
		this.amendmentEPIN = amendmentEPIN;
	}

	public String getAmendmentBudgetAmount()
	{
		return amendmentBudgetAmount;
	}

	public void setAmendmentBudgetAmount(String amendmentBudgetAmount)
	{
		this.amendmentBudgetAmount = amendmentBudgetAmount;
	}

	public String getAssignmentAmount()
	{
		return assignmentAmount;
	}

	public void setAssignmentAmount(String assignmentAmount)
	{
		this.assignmentAmount = assignmentAmount;
	}

	public String getRecoupedAmount()
	{
		return recoupedAmount;
	}

	public void setRecoupedAmount(String recoupedAmount)
	{
		this.recoupedAmount = recoupedAmount;
	}

	public String getProposedPaymentToVendor()
	{
		return proposedPaymentToVendor;
	}

	public void setProposedPaymentToVendor(String proposedPaymentToVendor)
	{
		this.proposedPaymentToVendor = proposedPaymentToVendor;
	}

	public String getApprovedInvoicedPendingDsiburesement()
	{
		return approvedInvoicedPendingDsiburesement;
	}

	public void setApprovedInvoicedPendingDsiburesement(String approvedInvoicedPendingDsiburesement)
	{
		this.approvedInvoicedPendingDsiburesement = approvedInvoicedPendingDsiburesement;
	}

	public String getDisbursmentNumber()
	{
		return disbursmentNumber;
	}

	public void setDisbursmentNumber(String disbursmentNumber)
	{
		this.disbursmentNumber = disbursmentNumber;
	}

	public Date getDisbursmentDate()
	{
		return disbursmentDate;
	}

	public void setDisbursmentDate(Date disbursmentDate)
	{
		this.disbursmentDate = disbursmentDate;
	}

	public String getPaymentVoucherNumber()
	{
		return paymentVoucherNumber;
	}

	public void setPaymentVoucherNumber(String paymentVoucherNumber)
	{
		this.paymentVoucherNumber = paymentVoucherNumber;
	}

	public String getApprovedAdvancePendingDsiburesement()
	{
		return approvedAdvancePendingDsiburesement;
	}

	public void setApprovedAdvancePendingDsiburesement(String approvedAdvancePendingDsiburesement)
	{
		this.approvedAdvancePendingDsiburesement = approvedAdvancePendingDsiburesement;
	}

	public String getStatusId()
	{
		return statusId;
	}

	public void setStatusId(String statusId)
	{
		this.statusId = statusId;
	}

	/**
	 * @return the budgetId
	 */
	public String getBudgetId()
	{
		return budgetId;
	}

	/**
	 * @param budgetId the budgetId to set
	 */
	public void setBudgetId(String budgetId)
	{
		this.budgetId = budgetId;
	}

	/**
	 * @return the invoiceId
	 */
	public String getInvoiceId()
	{
		return invoiceId;
	}

	/**
	 * @param invoiceId the invoiceId to set
	 */
	public void setInvoiceId(String invoiceId)
	{
		this.invoiceId = invoiceId;
	}

	/**
	 * @return the totalAdvance
	 */
	public String getTotalAdvance()
	{
		return totalAdvance;
	}

	/**
	 * @param totalAdvance the totalAdvance to set
	 */
	public void setTotalAdvance(String totalAdvance)
	{
		this.totalAdvance = totalAdvance;
	}

	/**
	 * @return the currentMonth
	 */
	public String getCurrentMonth()
	{
		return currentMonth;
	}

	/**
	 * @param currentMonth the currentMonth to set
	 */
	public void setCurrentMonth(String currentMonth)
	{
		this.currentMonth = currentMonth;
	}

	/**
	 * @return the plndProposalDueDate
	 */
	public String getPlndProposalDueDate()
	{
		return plndProposalDueDate;
	}

	/**
	 * @param plndProposalDueDate the plndProposalDueDate to set
	 */
	public void setPlndProposalDueDate(String plndProposalDueDate)
	{
		this.plndProposalDueDate = plndProposalDueDate;
	}

	/**
	 * @return the payeeVendorId
	 */
	public String getPayeeVendorId()
	{
		return payeeVendorId;
	}

	/**
	 * @param payeeVendorId the payeeVendorId to set
	 */
	public void setPayeeVendorId(String payeeVendorId)
	{
		this.payeeVendorId = payeeVendorId;
	}

	/**
	 * @return the budgetAdvanceId
	 */
	public String getBudgetAdvanceId()
	{
		return budgetAdvanceId;
	}

	/**
	 * @param budgetAdvanceId the budgetAdvanceId to set
	 */
	public void setBudgetAdvanceId(String budgetAdvanceId)
	{
		this.budgetAdvanceId = budgetAdvanceId;
	}

	/**
	 * @return the contractStatus
	 */
	public String getContractStatus()
	{
		return contractStatus;
	}

	/**
	 * @param contractStatus the contractStatus to set
	 */
	public void setContractStatus(String contractStatus)
	{
		this.contractStatus = contractStatus;
	}

	/**
	 * @return the pendingAmendAmount
	 */
	public Double getPendingAmendAmount()
	{
		return pendingAmendAmount;
	}

	/**
	 * @param pendingAmendAmount the pendingAmendAmount to set
	 */
	public void setPendingAmendAmount(Double pendingAmendAmount)
	{
		this.pendingAmendAmount = pendingAmendAmount;
	}

	/**
	 * @return the providerIdReport
	 */
	public String getProviderIdReport()
	{
		return providerIdReport;
	}

	/**
	 * @param providerIdReport the providerIdReport to set
	 */
	public void setProviderIdReport(String providerIdReport)
	{
		this.providerIdReport = providerIdReport;
	}

	/**
	 * @return the providerId
	 */
	public String getProviderId()
	{
		return providerId;
	}

	/**
	 * @param providerId the providerId to set
	 */
	public void setProviderId(String providerId)
	{
		this.providerId = providerId;
	}

	/**
	 * @return the invoicedAmount
	 */
	public String getInvoicedAmount()
	{
		return invoicedAmount;
	}

	/**
	 * @param invoicedAmount the invoicedAmount to set
	 */
	public void setInvoicedAmount(String invoicedAmount)
	{
		this.invoicedAmount = invoicedAmount;
	}

	/**
	 * @return the paymentAmountGrid
	 */
	public String getPaymentAmountGrid()
	{
		return paymentAmountGrid;
	}

	/**
	 * @param paymentAmountGrid the paymentAmountGrid to set
	 */
	public void setPaymentAmountGrid(String paymentAmountGrid)
	{
		this.paymentAmountGrid = paymentAmountGrid;
	}

	/**
	 * @return the fiscalYearSqlId
	 */
	public String getFiscalYearSqlId()
	{
		return fiscalYearSqlId;
	}

	/**
	 * @param fiscalYearSqlId the fiscalYearSqlId to set
	 */
	public void setFiscalYearSqlId(String fiscalYearSqlId)
	{
		this.fiscalYearSqlId = fiscalYearSqlId;
	}

	/**
	 * @return the voucherNumber
	 */
	public String getVoucherNumber()
	{
		return voucherNumber;
	}

	/**
	 * @param voucherNumber the voucherNumber to set
	 */
	public void setVoucherNumber(String voucherNumber)
	{
		this.voucherNumber = voucherNumber;
	}

}
