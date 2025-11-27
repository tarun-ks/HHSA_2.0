package com.nyc.hhs.model;

import java.util.Map;

import com.nyc.hhs.constants.ApplicationConstants;
import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
/**
 * This class is a bean which maintains the Status information.
 * 
 */

public class StatusBean
{
	public Map<String, String> getMoHMSubSectionDetails()
	{
		return moHMSubSectionDetails;
	}

	public void setMoHMSubSectionDetails(Map<String, String> moHMSubSectionDetails)
	{
		this.moHMSubSectionDetails = moHMSubSectionDetails;
	}

	public String getMsSectionStatus()
	{
		return msSectionStatus;
	}

	public void setMsSectionStatus(String msSectionStatus)
	{
		this.msSectionStatus = msSectionStatus;
	}

	public Map<String, String> getMoHMSubSectionDetailsToDisplay()
	{
		return moHMSubSectionDetailsToDisplay;
	}

	public void setMoHMSubSectionDetailsToDisplay(Map<String, String> moHMSubSectionDetailsToDisplay)
	{
		this.moHMSubSectionDetailsToDisplay = moHMSubSectionDetailsToDisplay;
	}

	public String getMsSectionStatusToDisplay()
	{
		return msSectionStatusToDisplay;
	}

	public void setMsSectionStatusToDisplay(String msSectionStatusToDisplay)
	{
		this.msSectionStatusToDisplay = msSectionStatusToDisplay;
	}

	public String getMsSectionStatusOnInnerSummary()
	{
		return msSectionStatusOnInnerSummary;
	}

	public void setMsSectionStatusOnInnerSummary(String msSectionStatusOnInnerSummary)
	{
		this.msSectionStatusOnInnerSummary = msSectionStatusOnInnerSummary;
	}

	private Map<String, String> moHMSubSectionDetails;
	@Length(max = 50)
	private String msSectionStatus;
	private Map<String, String> moHMSubSectionDetailsToDisplay;
	private String msSectionStatusOnInnerSummary = ApplicationConstants.NOT_STARTED_STATE;
	private String msSectionStatusToDisplay = ApplicationConstants.NOT_STARTED_STATE;

	public String toString()
	{
		StringBuffer lsData = new StringBuffer();
		lsData.append("moHMSubSectionDetails ");
		lsData.append(moHMSubSectionDetails);
		lsData.append("\n");
		lsData.append("msSectionStatus ");
		lsData.append(msSectionStatus);
		lsData.append("\n");
		lsData.append("moHMSubSectionDetailsToDisplay ");
		lsData.append(moHMSubSectionDetailsToDisplay);
		lsData.append("\n");
		lsData.append("msSectionStatusToDisplay ");
		lsData.append(msSectionStatusToDisplay);
		lsData.append("\n");
		return lsData.toString();
	}
}