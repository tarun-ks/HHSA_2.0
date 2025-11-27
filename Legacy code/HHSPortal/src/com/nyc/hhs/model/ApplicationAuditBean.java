package com.nyc.hhs.model;

import java.util.Date;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.util.DateUtil;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;
/**
 * This class is a bean which maintains the Application Audit information which
 * includes Application Id, Event Name, Event Type, Audit Date, User Id, Data,
 * Entity Type, Entity Id, Section Id, Status, Provider Flag, Audit Type and
 * Organization Id. Modified this class in R5 Filing History module.
 */

public class ApplicationAuditBean extends BaseFilter
{
	@Length(max = 20) 
	private String msAppid;
	private String msEventname;
	private String msEventtype;
	private Date msAuditDate;
	private String msDate;
	private String msUserid;
	private String msData;
	private String msEntityType;
	private String msEntityId;
	private String msSectionId;
	private String msStatus;
	private String msProviderFlag;
	private String msAuditType;
	@Length(max = 20) 
	private String msOrgId;
	private String msEntityIdentifier;
	private String msFilingPeriod;
	private Date activefilingssFrom;
	private Date activefilingssTo;

	private String fiscalYearFilterToMonth;
	private String fiscalYearFilterFromMonth;
	private String filingPeriodToMonth;
	private String filingPeriodFromMonth;
	private Date auditDateFilings;

	// added in R5
	public String getFiscalYearFilterToMonth()
	{
		return fiscalYearFilterToMonth;
	}

	public void setFiscalYearFilterToMonth(String fiscalYearFilterToMonth)
	{
		this.fiscalYearFilterToMonth = fiscalYearFilterToMonth;
	}

	public String getFiscalYearFilterFromMonth()
	{
		return fiscalYearFilterFromMonth;
	}

	public void setFiscalYearFilterFromMonth(String fiscalYearFilterFromMonth)
	{
		this.fiscalYearFilterFromMonth = fiscalYearFilterFromMonth;
	}

	public String getFilingPeriodToMonth()
	{
		return filingPeriodToMonth;
	}

	public void setFilingPeriodToMonth(String filingPeriodToMonth)
	{
		this.filingPeriodToMonth = filingPeriodToMonth;
	}

	public String getFilingPeriodFromMonth()
	{
		return filingPeriodFromMonth;
	}

	public void setFilingPeriodFromMonth(String filingPeriodFromMonth)
	{
		this.filingPeriodFromMonth = filingPeriodFromMonth;
	}

	// r5 ends
	public String getMsDate()
	{
		return msDate;
	}

	public void setMsDate(String msDate)
	{
		this.msDate = msDate;
	}

	public String getMsEntityIdentifier()
	{
		return msEntityIdentifier;
	}

	public void setMsEntityIdentifier(String msEntityIdentifier)
	{
		this.msEntityIdentifier = msEntityIdentifier;
	}

	public String getMsOrgId()
	{
		return msOrgId;
	}

	public void setMsOrgId(String msOrgId)
	{
		this.msOrgId = msOrgId;
	}

	public String getMsAppid()
	{
		return msAppid;
	}

	public void setMsAppid(String msAppid)
	{
		this.msAppid = msAppid;
	}

	public String getMsEventname()
	{
		return msEventname;
	}

	public void setMsEventname(String msEventname)
	{
		this.msEventname = msEventname;
	}

	public String getMsEventtype()
	{
		return msEventtype;
	}

	public void setMsEventtype(String msEventtype)
	{
		this.msEventtype = msEventtype;
	}

	public Date getMsAuditDate()
	{
		return msAuditDate;
	}

	public void setMsAuditDate(Date msAuditDate) throws ApplicationException
	{
		setMsDate(DateUtil.getDateMMddYYYYHHMMFormat((msAuditDate)));

		this.msAuditDate = msAuditDate;
	}

	public String getMsUserid()
	{
		return msUserid;
	}

	public void setMsUserid(String msUserid)
	{
		this.msUserid = msUserid;
	}

	public String getMsData()
	{
		return msData;
	}

	public void setMsData(String msData)
	{
		this.msData = msData;
	}

	public String getMsEntityType()
	{
		return msEntityType;
	}

	public void setMsEntityType(String msEntityType)
	{
		this.msEntityType = msEntityType;
	}

	public String getMsEntityId()
	{
		return msEntityId;
	}

	public void setMsEntityId(String msEntityId)
	{
		this.msEntityId = msEntityId;
	}

	public String getMsSectionId()
	{
		return msSectionId;
	}

	public void setMsSectionId(String msSectionId)
	{
		this.msSectionId = msSectionId;
	}

	public String getMsStatus()
	{
		return msStatus;
	}

	public void setMsStatus(String msStatus)
	{
		this.msStatus = msStatus;
	}

	public String getMsProviderFlag()
	{
		return msProviderFlag;
	}

	public void setMsProviderFlag(String msProviderFlag)
	{
		this.msProviderFlag = msProviderFlag;
	}

	public String getMsAuditType()
	{
		return msAuditType;
	}

	public void setMsAuditType(String msAuditType)
	{
		this.msAuditType = msAuditType;
	}

	@Override
	public String toString()
	{
		return "ApplicationAuditBean [msAppid=" + msAppid + ", msEventname=" + msEventname + ", msEventtype="
				+ msEventtype + ", msAuditDate=" + msAuditDate + ", msDate=" + msDate + ", msUserid=" + msUserid
				+ ", msData=" + msData + ", msEntityType=" + msEntityType + ", msEntityId=" + msEntityId
				+ ", msSectionId=" + msSectionId + ", msStatus=" + msStatus + ", msProviderFlag=" + msProviderFlag
				+ ", msAuditType=" + msAuditType + ", msOrgId=" + msOrgId + ", msEntityIdentifier="
				+ msEntityIdentifier + "]";
	}

	// added in r5
	/**
	 * @return the activefilingssFrom
	 */
	public Date getActivefilingssFrom()
	{
		return activefilingssFrom;
	}

	/**
	 * @param activefilingssFrom the activefilingssFrom to set
	 */
	public void setActivefilingssFrom(Date activefilingssFrom)
	{
		this.activefilingssFrom = activefilingssFrom;
	}

	/**
	 * @return the activefilingssTo
	 */
	public Date getActivefilingssTo()
	{
		return activefilingssTo;
	}

	/**
	 * @param activefilingssTo the activefilingssTo to set
	 */
	public void setActivefilingssTo(Date activefilingssTo)
	{
		this.activefilingssTo = activefilingssTo;
	}

	/**
	 * @return the msFilingPeriod
	 */
	public String getMsFilingPeriod()
	{
		return msFilingPeriod;
	}

	/**
	 * @param msFilingPeriod the msFilingPeriod to set
	 */
	public void setMsFilingPeriod(String msFilingPeriod)
	{
		this.msFilingPeriod = msFilingPeriod;
	}

	/**
	 * @return the auditDateFilings
	 */
	public Date getAuditDateFilings()
	{
		return auditDateFilings;
	}

	/**
	 * @param auditDateFilings the auditDateFilings to set
	 */
	public void setAuditDateFilings(Date auditDateFilings)
	{
		this.auditDateFilings = auditDateFilings;
	}
	// r5 ends

}
