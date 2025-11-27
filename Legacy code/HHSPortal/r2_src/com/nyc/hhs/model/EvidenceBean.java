package com.nyc.hhs.model;

/**
 * @author sunny.aggarwal
 * 
 */
public class EvidenceBean
{
	private String elementId;
	private String elementName;
	private String evidenceFlag;
	private String activeFlag;

	public String getElementId()
	{
		return elementId;
	}

	public void setElementId(String elementId)
	{
		this.elementId = elementId;
	}

	public String getElementName()
	{
		return elementName;
	}

	public void setElementName(String elementName)
	{
		this.elementName = elementName;
	}

	public String getEvidenceFlag()
	{
		return evidenceFlag;
	}

	public void setEvidenceFlag(String evidenceFlag)
	{
		this.evidenceFlag = evidenceFlag;
	}

	public String getActiveFlag()
	{
		return activeFlag;
	}

	public void setActiveFlag(String activeFlag)
	{
		this.activeFlag = activeFlag;
	}

	@Override
	public String toString()
	{
		return "EvidenceBean [elementId=" + elementId + ", elementName=" + elementName + ", evidenceFlag="
				+ evidenceFlag + ", activeFlag=" + activeFlag + "]";
	}
}
