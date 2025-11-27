package com.nyc.hhs.model;

import java.util.Date;
import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;
/**
 *This class is bean class that maintains details of bulk download
 */
public class BulkDownloadBean
{

	//@Digits(integer=22, fraction=0)
	@RegExp(value ="^\\d{0,22}")
	String msDownloadId;
	@Length(max = 20) 
	String lsOrgId;
	String msStatusId;
	String msFilterDocName;
	String msFilterDocType;
	public String getOrgId()
	{
		return lsOrgId;
	}
	public void setOrgId(String lsOrgId)
	{
		this.lsOrgId = lsOrgId;
	}
	Date msModifiedDateFrom;
	Date msModifiedDateTo;
	public String getMsModifiedTo()
	{
		return msModifiedTo;
	}
	public void setMsModifiedTo(String msModifiedTo)
	{
		this.msModifiedTo = msModifiedTo;
	}
	public String getMsModifiedFrom()
	{
		return msModifiedFrom;
	}
	public void setMsModifiedFrom(String msModifiedFrom)
	{
		this.msModifiedFrom = msModifiedFrom;
	}
	String msModifiedTo = null;
	String msModifiedFrom = null;
	String msModifiedBy;
	String msCreatedBy;
	Date msCreatedDate;
	Date msModifiedDate;
	String msAgentId;
	
	public String getMsAgentId()
	{
		return msAgentId;
	}
	public void setMsAgentId(String msAgentId)
	{
		this.msAgentId = msAgentId;
	}
	public String getMsDownloadId()
	{
		return msDownloadId;
	}
	public void setMsDownloadId(String msDownloadId)
	{
		this.msDownloadId = msDownloadId;
	}
	public String getMsStatusId()
	{
		return msStatusId;
	}
	public void setMsStatusId(String msStatusId)
	{
		this.msStatusId = msStatusId;
	}
	public String getMsFilterDocName()
	{
		return msFilterDocName;
	}
	public void setMsFilterDocName(String msFilterDocName)
	{
		this.msFilterDocName = msFilterDocName;
	}
	public String getMsFilterDocType()
	{
		return msFilterDocType;
	}
	public void setMsFilterDocType(String msFilterDocType)
	{
		this.msFilterDocType = msFilterDocType;
	}
	public Date getMsModifiedDateFrom()
	{
		return msModifiedDateFrom;
	}
	public void setMsModifiedDateFrom(Date msModifiedDateFrom)
	{
		this.msModifiedDateFrom = msModifiedDateFrom;
	}
	public Date getMsModifiedDateTo()
	{
		return msModifiedDateTo;
	}
	public void setMsModifiedDateTo(Date msModifiedDateTo)
	{
		this.msModifiedDateTo = msModifiedDateTo;
	}
	public String getMsModifiedBy()
	{
		return msModifiedBy;
	}
	public void setMsModofiedBy(String msModifiedBy)
	{
		this.msModifiedBy = msModifiedBy;
	}
	public String getMsCreatedBy()
	{
		return msCreatedBy;
	}
	public void setMsCreatedBy(String msCreatedBy)
	{
		this.msCreatedBy = msCreatedBy;
	}
	public Date getMsCreatedDate()
	{
		return msCreatedDate;
	}
	public void setMsCreatedDate(Date msCreatedDate)
	{
		this.msCreatedDate = msCreatedDate;
	}
	public Date getMsModifiedDate()
	{
		return msModifiedDate;
	}
	public void setMsModifiedDate(Date msModifiedDate)
	{
		this.msModifiedDate = msModifiedDate;
	}
	@Override
	public String toString()
	{
		return "BulkDownloadBean [msDownloadId=" + msDownloadId + ", msStatusId=" + msStatusId + ", msFilterDocName="
				+ msFilterDocName + ", msFilterDocType=" + msFilterDocType + ", msModifiedDateFrom="
				+ msModifiedFrom + ", msModifiedDateTo=" + msModifiedTo + ", msModifiedBy=" + msModifiedBy
				+ ", msCreatedBy=" + msCreatedBy + ", msCreatedDate=" + msCreatedDate + ", msModifiedDate="
				+ msModifiedDate + "]";
	}
	

	
}
