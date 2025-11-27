package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

/**
 * This class is a bean which maintains the user Address information which
 * includes Address, City, State, Zip code and Address type.
 * 
 */

public class AddressBean
{
	private String msAddress1 = "";
	private String msCity = "";
	private String msState = "";	
	@RegExp(value ="^\\d{5}(?:[-\\s]\\d{4})?|$")
	private String msZipcode = "";
	private String msAddressType = "";
	private String msStatusDescriptionText = "";
	private String msStatusReason = "";
	private String msStreetNumberText = "";
	private String msCongressionalDistrictName = "";
	private String msLatitude = "";
	private String msLongitude = "";
	private String msXCoordinate = "";
	private String msYCoordinate = "";
	private String msCommunityDistrict = "";
	private String msCivilCourtDistrict = "";
	private String msSchoolDistrictName = "";
	private String msHealthArea = "";
	private String msBuildingIdNumber = "";
	private String msTaxBlock = "";
	private String msTaxLot = "";
	private String msSenatorialDistrict = "";
	private String msAssemblyDistrict = "";
	private String msCouncilDistrict = "";
	private String msLowEndStreetNumber = "";
	private String msHighEndStreetNumber = "";
	private String msLowEndStreetName = "";
	private String msHighEndStreetName = "";
	private String msNycBorough = "";

	/**
	 * @return the msAddress1
	 */
	public String getMsAddress1()
	{
		return msAddress1;
	}

	/**
	 * @param msAddress1
	 *            the msAddress1 to set
	 */
	public void setMsAddress1(String msAddress1)
	{
		this.msAddress1 = msAddress1;
	}

	/**
	 * @return the msCity
	 */
	public String getMsCity()
	{
		return msCity;
	}

	/**
	 * @param msCity
	 *            the msCity to set
	 */
	public void setMsCity(String msCity)
	{
		this.msCity = msCity;
	}

	/**
	 * @return the msState
	 */
	public String getMsState()
	{
		return msState;
	}

	/**
	 * @param msState
	 *            the msState to set
	 */
	public void setMsState(String msState)
	{
		this.msState = msState;
	}

	/**
	 * @return the msZipcode
	 */
	public String getMsZipcode()
	{
		return msZipcode;
	}

	/**
	 * @param msZipcode
	 *            the msZipcode to set
	 */
	public void setMsZipcode(String msZipcode)
	{
		this.msZipcode = msZipcode;
	}

	/**
	 * @return the msAddressType
	 */
	public String getMsAddressType()
	{
		return msAddressType;
	}

	/**
	 * @param msAddressType
	 *            the msAddressType to set
	 */
	public void setMsAddressType(String msAddressType)
	{
		this.msAddressType = msAddressType;
	}

	/**
	 * @return the msStatusDescriptionText
	 */
	public String getMsStatusDescriptionText()
	{
		return msStatusDescriptionText;
	}

	/**
	 * @param msStatusDescriptionText
	 *            the msStatusDescriptionText to set
	 */
	public void setMsStatusDescriptionText(String msStatusDescriptionText)
	{
		this.msStatusDescriptionText = msStatusDescriptionText;
	}

	/**
	 * @return the msStatusReason
	 */
	public String getMsStatusReason()
	{
		return msStatusReason;
	}

	/**
	 * @param msStatusReason
	 *            the msStatusReason to set
	 */
	public void setMsStatusReason(String msStatusReason)
	{
		this.msStatusReason = msStatusReason;
	}

	/**
	 * @return the msStreetNumberText
	 */
	public String getMsStreetNumberText()
	{
		return msStreetNumberText;
	}

	/**
	 * @param msStreetNumberText
	 *            the msStreetNumberText to set
	 */
	public void setMsStreetNumberText(String msStreetNumberText)
	{
		this.msStreetNumberText = msStreetNumberText;
	}

	/**
	 * @return the msCongressionalDistrictName
	 */
	public String getMsCongressionalDistrictName()
	{
		return msCongressionalDistrictName;
	}

	/**
	 * @param msCongressionalDistrictName
	 *            the msCongressionalDistrictName to set
	 */
	public void setMsCongressionalDistrictName(String msCongressionalDistrictName)
	{
		this.msCongressionalDistrictName = msCongressionalDistrictName;
	}

	/**
	 * @return the msLatitude
	 */
	public String getMsLatitude()
	{
		return msLatitude;
	}

	/**
	 * @param msLatitude
	 *            the msLatitude to set
	 */
	public void setMsLatitude(String msLatitude)
	{
		this.msLatitude = msLatitude;
	}

	/**
	 * @return the msLongitude
	 */
	public String getMsLongitude()
	{
		return msLongitude;
	}

	/**
	 * @param msLongitude
	 *            the msLongitude to set
	 */
	public void setMsLongitude(String msLongitude)
	{
		this.msLongitude = msLongitude;
	}

	/**
	 * @return the msXCoordinate
	 */
	public String getMsXCoordinate()
	{
		return msXCoordinate;
	}

	/**
	 * @param msXCoordinate
	 *            the msXCoordinate to set
	 */
	public void setMsXCoordinate(String msXCoordinate)
	{
		this.msXCoordinate = msXCoordinate;
	}

	/**
	 * @return the msYCoordinate
	 */
	public String getMsYCoordinate()
	{
		return msYCoordinate;
	}

	/**
	 * @param msYCoordinate
	 *            the msYCoordinate to set
	 */
	public void setMsYCoordinate(String msYCoordinate)
	{
		this.msYCoordinate = msYCoordinate;
	}

	/**
	 * @return the msCommunityDistrict
	 */
	public String getMsCommunityDistrict()
	{
		return msCommunityDistrict;
	}

	/**
	 * @param msCommunityDistrict
	 *            the msCommunityDistrict to set
	 */
	public void setMsCommunityDistrict(String msCommunityDistrict)
	{
		this.msCommunityDistrict = msCommunityDistrict;
	}

	/**
	 * @return the msCivilCourtDistrict
	 */
	public String getMsCivilCourtDistrict()
	{
		return msCivilCourtDistrict;
	}

	/**
	 * @param msCivilCourtDistrict
	 *            the msCivilCourtDistrict to set
	 */
	public void setMsCivilCourtDistrict(String msCivilCourtDistrict)
	{
		this.msCivilCourtDistrict = msCivilCourtDistrict;
	}

	/**
	 * @return the msSchoolDistrictName
	 */
	public String getMsSchoolDistrictName()
	{
		return msSchoolDistrictName;
	}

	/**
	 * @param msSchoolDistrictName
	 *            the msSchoolDistrictName to set
	 */
	public void setMsSchoolDistrictName(String msSchoolDistrictName)
	{
		this.msSchoolDistrictName = msSchoolDistrictName;
	}

	/**
	 * @return the msHealthArea
	 */
	public String getMsHealthArea()
	{
		return msHealthArea;
	}

	/**
	 * @param msHealthArea
	 *            the msHealthArea to set
	 */
	public void setMsHealthArea(String msHealthArea)
	{
		this.msHealthArea = msHealthArea;
	}

	/**
	 * @return the msBuildingIdNumber
	 */
	public String getMsBuildingIdNumber()
	{
		return msBuildingIdNumber;
	}

	/**
	 * @param msBuildingIdNumber
	 *            the msBuildingIdNumber to set
	 */
	public void setMsBuildingIdNumber(String msBuildingIdNumber)
	{
		this.msBuildingIdNumber = msBuildingIdNumber;
	}

	/**
	 * @return the msTaxBlock
	 */
	public String getMsTaxBlock()
	{
		return msTaxBlock;
	}

	/**
	 * @param msTaxBlock
	 *            the msTaxBlock to set
	 */
	public void setMsTaxBlock(String msTaxBlock)
	{
		this.msTaxBlock = msTaxBlock;
	}

	/**
	 * @return the msTaxLot
	 */
	public String getMsTaxLot()
	{
		return msTaxLot;
	}

	/**
	 * @param msTaxLot
	 *            the msTaxLot to set
	 */
	public void setMsTaxLot(String msTaxLot)
	{
		this.msTaxLot = msTaxLot;
	}

	/**
	 * @return the msSenatorialDistrict
	 */
	public String getMsSenatorialDistrict()
	{
		return msSenatorialDistrict;
	}

	/**
	 * @param msSenatorialDistrict
	 *            the msSenatorialDistrict to set
	 */
	public void setMsSenatorialDistrict(String msSenatorialDistrict)
	{
		this.msSenatorialDistrict = msSenatorialDistrict;
	}

	/**
	 * @return the msAssemblyDistrict
	 */
	public String getMsAssemblyDistrict()
	{
		return msAssemblyDistrict;
	}

	/**
	 * @param msAssemblyDistrict
	 *            the msAssemblyDistrict to set
	 */
	public void setMsAssemblyDistrict(String msAssemblyDistrict)
	{
		this.msAssemblyDistrict = msAssemblyDistrict;
	}

	/**
	 * @return the msCouncilDistrict
	 */
	public String getMsCouncilDistrict()
	{
		return msCouncilDistrict;
	}

	/**
	 * @param msCouncilDistrict
	 *            the msCouncilDistrict to set
	 */
	public void setMsCouncilDistrict(String msCouncilDistrict)
	{
		this.msCouncilDistrict = msCouncilDistrict;
	}

	/**
	 * @return the msLowEndStreetNumber
	 */
	public String getMsLowEndStreetNumber()
	{
		return msLowEndStreetNumber;
	}

	/**
	 * @param msLowEndStreetNumber
	 *            the msLowEndStreetNumber to set
	 */
	public void setMsLowEndStreetNumber(String msLowEndStreetNumber)
	{
		this.msLowEndStreetNumber = msLowEndStreetNumber;
	}

	/**
	 * @return the msHighEndStreetNumber
	 */
	public String getMsHighEndStreetNumber()
	{
		return msHighEndStreetNumber;
	}

	/**
	 * @param msHighEndStreetNumber
	 *            the msHighEndStreetNumber to set
	 */
	public void setMsHighEndStreetNumber(String msHighEndStreetNumber)
	{
		this.msHighEndStreetNumber = msHighEndStreetNumber;
	}

	/**
	 * @return the msLowEndStreetName
	 */
	public String getMsLowEndStreetName()
	{
		return msLowEndStreetName;
	}

	/**
	 * @param msLowEndStreetName
	 *            the msLowEndStreetName to set
	 */
	public void setMsLowEndStreetName(String msLowEndStreetName)
	{
		this.msLowEndStreetName = msLowEndStreetName;
	}

	/**
	 * @return the msHighEndStreetName
	 */
	public String getMsHighEndStreetName()
	{
		return msHighEndStreetName;
	}

	/**
	 * @param msHighEndStreetName
	 *            the msHighEndStreetName to set
	 */
	public void setMsHighEndStreetName(String msHighEndStreetName)
	{
		this.msHighEndStreetName = msHighEndStreetName;
	}

	/**
	 * @return the msNycBorough
	 */
	public String getMsNycBorough()
	{
		return msNycBorough;
	}

	/**
	 * @param msNycBorough
	 *            the msNycBorough to set
	 */
	public void setMsNycBorough(String msNycBorough)
	{
		this.msNycBorough = msNycBorough;
	}

	@Override
	public String toString() {
		return "AddressBean [msAddress1=" + msAddress1 + ", msCity=" + msCity
				+ ", msState=" + msState + ", msZipcode=" + msZipcode
				+ ", msAddressType=" + msAddressType
				+ ", msStatusDescriptionText=" + msStatusDescriptionText
				+ ", msStatusReason=" + msStatusReason
				+ ", msStreetNumberText=" + msStreetNumberText
				+ ", msCongressionalDistrictName="
				+ msCongressionalDistrictName + ", msLatitude=" + msLatitude
				+ ", msLongitude=" + msLongitude + ", msXCoordinate="
				+ msXCoordinate + ", msYCoordinate=" + msYCoordinate
				+ ", msCommunityDistrict=" + msCommunityDistrict
				+ ", msCivilCourtDistrict=" + msCivilCourtDistrict
				+ ", msSchoolDistrictName=" + msSchoolDistrictName
				+ ", msHealthArea=" + msHealthArea + ", msBuildingIdNumber="
				+ msBuildingIdNumber + ", msTaxBlock=" + msTaxBlock
				+ ", msTaxLot=" + msTaxLot + ", msSenatorialDistrict="
				+ msSenatorialDistrict + ", msAssemblyDistrict="
				+ msAssemblyDistrict + ", msCouncilDistrict="
				+ msCouncilDistrict + ", msLowEndStreetNumber="
				+ msLowEndStreetNumber + ", msHighEndStreetNumber="
				+ msHighEndStreetNumber + ", msLowEndStreetName="
				+ msLowEndStreetName + ", msHighEndStreetName="
				+ msHighEndStreetName + ", msNycBorough=" + msNycBorough + "]";
	}
}
