package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

/**
 * This class is a bean which maintains the Service Summary information.
 * 
 */

public class ServiceSummary implements Comparable
{

	@Length(max = 20)
	String msServiceAppId;
	@Length(max = 20)
	String msBusinessAppId;
	String msServiceName;
	@Length(max = 20)
	String msOrgId;
	String msUserId;
	String msSubmittedBy;
	String msSubmissionDate;
	String msModifiedBy;
	String msModifiedDate;
	String msStartDate;
	String msExpirationDate;
	String msRemovedFlag;
	String msInactiveFlag;
	String msStatusId;
	String msServiceStatus;
	String msProcessStatus;
	String msServiceElementId;
	ServiceSummaryStatus moServiceSubSectionStatus;
	String msServiceType;

	public String getMsServiceAppId()
	{
		return msServiceAppId;
	}

	public void setMsServiceAppId(String msServiceAppId)
	{
		this.msServiceAppId = msServiceAppId;
	}

	public String getMsBusinessAppId()
	{
		return msBusinessAppId;
	}

	public void setMsBusinessAppId(String msBusinessAppId)
	{
		this.msBusinessAppId = msBusinessAppId;
	}

	public String getMsServiceName()
	{
		return msServiceName;
	}

	public void setMsServiceName(String msServiceName)
	{
		this.msServiceName = msServiceName;
	}

	public String getMsOrgId()
	{
		return msOrgId;
	}

	public void setMsOrgId(String msOrgId)
	{
		this.msOrgId = msOrgId;
	}

	public String getMsUserId()
	{
		return msUserId;
	}

	public void setMsUserId(String msUserId)
	{
		this.msUserId = msUserId;
	}

	public String getMsSubmittedBy()
	{
		return msSubmittedBy;
	}

	public void setMsSubmittedBy(String msSubmittedBy)
	{
		this.msSubmittedBy = msSubmittedBy;
	}

	public String getMsSubmissionDate()
	{
		return msSubmissionDate;
	}

	public void setMsSubmissionDate(String msSubmissionDate)
	{
		this.msSubmissionDate = msSubmissionDate;
	}

	public String getMsModifiedBy()
	{
		return msModifiedBy;
	}

	public void setMsModifiedBy(String msModifiedBy)
	{
		this.msModifiedBy = msModifiedBy;
	}

	public String getMsModifiedDate()
	{
		return msModifiedDate;
	}

	public void setMsModifiedDate(String msModifiedDate)
	{
		this.msModifiedDate = msModifiedDate;
	}

	public String getMsStartDate()
	{
		return msStartDate;
	}

	public void setMsStartDate(String msStartDate)
	{
		this.msStartDate = msStartDate;
	}

	public String getMsExpirationDate()
	{
		return msExpirationDate;
	}

	public void setMsExpirationDate(String msExpirationDate)
	{
		this.msExpirationDate = msExpirationDate;
	}

	public String getMsRemovedFlag()
	{
		return msRemovedFlag;
	}

	public void setMsRemovedFlag(String msRemovedFlag)
	{
		this.msRemovedFlag = msRemovedFlag;
	}

	public String getMsInactiveFlag()
	{
		return msInactiveFlag;
	}

	public void setMsInactiveFlag(String msInactiveFlag)
	{
		this.msInactiveFlag = msInactiveFlag;
	}

	public String getMsStatusId()
	{
		return msStatusId;
	}

	public void setMsStatusId(String msStatusId)
	{
		this.msStatusId = msStatusId;
	}

	public String getMsServiceStatus()
	{
		return msServiceStatus;
	}

	public void setMsServiceStatus(String msServiceStatus)
	{
		this.msServiceStatus = msServiceStatus;
	}

	public String getMsProcessStatus()
	{
		return msProcessStatus;
	}

	public void setMsProcessStatus(String msProcessStatus)
	{
		this.msProcessStatus = msProcessStatus;
	}

	public ServiceSummaryStatus getServiceSubSectionStatus()
	{
		return moServiceSubSectionStatus;
	}

	public void setServiceSubSectionStatus(ServiceSummaryStatus aoServiceSubSectionStatus)
	{
		this.moServiceSubSectionStatus = aoServiceSubSectionStatus;
	}

	public String getMsServiceType()
	{
		return msServiceType;
	}

	public void setMsServiceType(String msServiceType)
	{
		this.msServiceType = msServiceType;
	}

	public String getMsServiceElementId()
	{
		return msServiceElementId;
	}

	public void setMsServiceElementId(String msServiceElementId)
	{
		this.msServiceElementId = msServiceElementId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((moServiceSubSectionStatus == null) ? 0 : moServiceSubSectionStatus.hashCode());
		result = prime * result + ((msBusinessAppId == null) ? 0 : msBusinessAppId.hashCode());
		result = prime * result + ((msExpirationDate == null) ? 0 : msExpirationDate.hashCode());
		result = prime * result + ((msInactiveFlag == null) ? 0 : msInactiveFlag.hashCode());
		result = prime * result + ((msModifiedBy == null) ? 0 : msModifiedBy.hashCode());
		result = prime * result + ((msModifiedDate == null) ? 0 : msModifiedDate.hashCode());
		result = prime * result + ((msOrgId == null) ? 0 : msOrgId.hashCode());
		result = prime * result + ((msProcessStatus == null) ? 0 : msProcessStatus.hashCode());
		result = prime * result + ((msRemovedFlag == null) ? 0 : msRemovedFlag.hashCode());
		result = prime * result + ((msServiceAppId == null) ? 0 : msServiceAppId.hashCode());
		result = prime * result + ((msServiceElementId == null) ? 0 : msServiceElementId.hashCode());
		result = prime * result + ((msServiceName == null) ? 0 : msServiceName.hashCode());
		result = prime * result + ((msServiceStatus == null) ? 0 : msServiceStatus.hashCode());
		result = prime * result + ((msServiceType == null) ? 0 : msServiceType.hashCode());
		result = prime * result + ((msStartDate == null) ? 0 : msStartDate.hashCode());
		result = prime * result + ((msStatusId == null) ? 0 : msStatusId.hashCode());
		result = prime * result + ((msSubmissionDate == null) ? 0 : msSubmissionDate.hashCode());
		result = prime * result + ((msSubmittedBy == null) ? 0 : msSubmittedBy.hashCode());
		result = prime * result + ((msUserId == null) ? 0 : msUserId.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (!(obj instanceof ServiceSummary))
		{
			return false;
		}
		ServiceSummary other = (ServiceSummary) obj;
		if (moServiceSubSectionStatus == null)
		{
			if (other.moServiceSubSectionStatus != null)
			{
				return false;
			}
		}
		else if (!moServiceSubSectionStatus.equals(other.moServiceSubSectionStatus))
		{
			return false;
		}
		if (msBusinessAppId == null)
		{
			if (other.msBusinessAppId != null)
			{
				return false;
			}
		}
		else if (!msBusinessAppId.equals(other.msBusinessAppId))
		{
			return false;
		}
		if (msExpirationDate == null)
		{
			if (other.msExpirationDate != null)
			{
				return false;
			}
		}
		else if (!msExpirationDate.equals(other.msExpirationDate))
		{
			return false;
		}
		if (msInactiveFlag == null)
		{
			if (other.msInactiveFlag != null)
			{
				return false;
			}
		}
		else if (!msInactiveFlag.equals(other.msInactiveFlag))
		{
			return false;
		}
		if (msModifiedBy == null)
		{
			if (other.msModifiedBy != null)
			{
				return false;
			}
		}
		else if (!msModifiedBy.equals(other.msModifiedBy))
		{
			return false;
		}
		if (msModifiedDate == null)
		{
			if (other.msModifiedDate != null)
			{
				return false;
			}
		}
		else if (!msModifiedDate.equals(other.msModifiedDate))
		{
			return false;
		}
		if (msOrgId == null)
		{
			if (other.msOrgId != null)
			{
				return false;
			}
		}
		else if (!msOrgId.equals(other.msOrgId))
		{
			return false;
		}
		if (msProcessStatus == null)
		{
			if (other.msProcessStatus != null)
			{
				return false;
			}
		}
		else if (!msProcessStatus.equals(other.msProcessStatus))
		{
			return false;
		}
		if (msRemovedFlag == null)
		{
			if (other.msRemovedFlag != null)
			{
				return false;
			}
		}
		else if (!msRemovedFlag.equals(other.msRemovedFlag))
		{
			return false;
		}
		if (msServiceAppId == null)
		{
			if (other.msServiceAppId != null)
			{
				return false;
			}
		}
		else if (!msServiceAppId.equals(other.msServiceAppId))
		{
			return false;
		}
		if (msServiceElementId == null)
		{
			if (other.msServiceElementId != null)
			{
				return false;
			}
		}
		else if (!msServiceElementId.equals(other.msServiceElementId))
		{
			return false;
		}
		if (msServiceName == null)
		{
			if (other.msServiceName != null)
			{
				return false;
			}
		}
		else if (!msServiceName.equals(other.msServiceName))
		{
			return false;
		}
		if (msServiceStatus == null)
		{
			if (other.msServiceStatus != null)
			{
				return false;
			}
		}
		else if (!msServiceStatus.equals(other.msServiceStatus))
		{
			return false;
		}
		if (msServiceType == null)
		{
			if (other.msServiceType != null)
			{
				return false;
			}
		}
		else if (!msServiceType.equals(other.msServiceType))
		{
			return false;
		}
		if (msStartDate == null)
		{
			if (other.msStartDate != null)
			{
				return false;
			}
		}
		else if (!msStartDate.equals(other.msStartDate))
		{
			return false;
		}
		if (msStatusId == null)
		{
			if (other.msStatusId != null)
			{
				return false;
			}
		}
		else if (!msStatusId.equals(other.msStatusId))
		{
			return false;
		}
		if (msSubmissionDate == null)
		{
			if (other.msSubmissionDate != null)
			{
				return false;
			}
		}
		else if (!msSubmissionDate.equals(other.msSubmissionDate))
		{
			return false;
		}
		if (msSubmittedBy == null)
		{
			if (other.msSubmittedBy != null)
			{
				return false;
			}
		}
		else if (!msSubmittedBy.equals(other.msSubmittedBy))
		{
			return false;
		}
		if (msUserId == null)
		{
			if (other.msUserId != null)
			{
				return false;
			}
		}
		else if (!msUserId.equals(other.msUserId))
		{
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(Object loObject)
	{
		return this.msServiceName.compareTo(((ServiceSummary) loObject).msServiceName);
	}

	@Override
	public String toString()
	{
		return "ServiceSummary [msServiceAppId=" + msServiceAppId + ", msBusinessAppId=" + msBusinessAppId
				+ ", msServiceName=" + msServiceName + ", msOrgId=" + msOrgId + ", msUserId=" + msUserId
				+ ", msSubmittedBy=" + msSubmittedBy + ", msSubmissionDate=" + msSubmissionDate + ", msModifiedBy="
				+ msModifiedBy + ", msModifiedDate=" + msModifiedDate + ", msStartDate=" + msStartDate
				+ ", msExpirationDate=" + msExpirationDate + ", msRemovedFlag=" + msRemovedFlag + ", msInactiveFlag="
				+ msInactiveFlag + ", msStatusId=" + msStatusId + ", msServiceStatus=" + msServiceStatus
				+ ", msProcessStatus=" + msProcessStatus + ", msServiceElementId=" + msServiceElementId
				+ ", moServiceSubSectionStatus=" + moServiceSubSectionStatus + ", msServiceType=" + msServiceType + "]";
	}

}
