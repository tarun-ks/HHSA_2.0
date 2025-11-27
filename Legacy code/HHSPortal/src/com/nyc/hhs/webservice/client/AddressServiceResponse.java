package com.nyc.hhs.webservice.client;

/** 
 * AddressServiceResponse encapsulates the response values returned 
 *  by the Web Service call. It provides all the getters method necessary 
 *  to retrieve the value of the return response fields. The class is to 
 *  be passed as an empty Address Service Response instance to the 
 *  WSClient_AddressService processRequest method. WSClient will populate 
 *  it with response values.  
 *  			
 */

import gov.niem.niem.niem_core._2.AddressType;
import gov.niem.niem.niem_core._2.DateType;
import gov.niem.niem.niem_core._2.IdentificationType;
import gov.niem.niem.niem_core._2.LocaleType;
import gov.niem.niem.niem_core._2.LocationType;
import gov.niem.niem.niem_core._2.ProperNameTextType;
import gov.niem.niem.niem_core._2.StreetType;
import gov.niem.niem.niem_core._2.StructuredAddressType;
import gov.niem.niem.niem_core._2.TextType;
import gov.niem.niem.proxy.xsd._2.NonNegativeInteger;
import gov.niem.niem.usps_states._2.USStateCodeType;
import gov.nyc.dhs.cares.wsdlframework.WSDLDataTypesObjectFactory;
import gov.nyc.hhsc.niem.domains.hhs._2.AreaPublicServiceType;
import gov.nyc.hhsc.niem.domains.hhs._2.ElectoralDistrictType;
import gov.nyc.hhsc.niem.domains.hhs._2.LocationBBLType;
import gov.nyc.hhsc.niem.domains.hhs._2.LocationCoordinatesType;
import gov.nyc.hhsc.niem.domains.hhs._2.StreetSegmentType;
import gov.nyc.hhsc.niem.exchange.addressservice._1.AddressServiceResponseType;
import gov.nyc.hhsc.niem.exchange.addressservice._1.CrossStreetType;
import gov.nyc.hhsc.wsdl.wsclientinterface.ApplicationFault;
import gov.nyc.hhsc.wsdl.wsclientinterface.SystemFault;
import gov.nyc.hhsc.wsdl.wsclientinterface.WSException;
import gov.nyc.hhsc.wsdl.wsclientinterface.WSResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.datatype.XMLGregorianCalendar;

public class AddressServiceResponse extends WSResponse<AddressServiceResponseType>
{
	
	public AddressServiceResponse()
	{
		super();

	}
	/**
	 * This method is used to get structured address
	 * @return loStructuredaddress
	 */
	private final StructuredAddressType getStructuredAddress()
	{
		LocationType loAddresslocation = responseobject.getStandardizedAddress();
		StructuredAddressType loStructuredaddress = null;
		if ((loAddresslocation != null) && (loAddresslocation.getLocationAddress().size() > 0))
		{
			AddressType loAddressrepresentation = loAddresslocation.getLocationAddress().get(0);
			loStructuredaddress = (StructuredAddressType) loAddressrepresentation.getAddressRepresentation().getValue();
		}
		return loStructuredaddress;
	}
	/**
	 * This method is used to get street type
	 * @return loStreet
	 */
	private StreetType getStreetType()
	{
		StructuredAddressType loStructuredaddress = getStructuredAddress();
		StreetType loStreet = null;
		if (loStructuredaddress != null)
		{
			loStreet = (StreetType) loStructuredaddress.getAddressDeliveryPoint().get(0).getValue();
		}
		return loStreet;
	}
	/**
	 * This method is used to get street name
	 * @return lsStreetname
	 */
	public final String getStreetName()
	{
		String lsStreetname = "";
		StreetType lsStreet = getStreetType();
		if (lsStreet != null)
		{
			lsStreetname = lsStreet.getStreetName().getValue();
		}

		return lsStreetname;
	}
	/**
	 * This method is used to get street number text
	 * @return lsStreetnumbertext
	 */
	public final String getStreetNumberText()
	{
		String lsStreetnumbertext = "";
		StreetType lsStreet = getStreetType();
		if (lsStreet != null)
		{
			lsStreetnumbertext = (lsStreet.getStreetNumberText() == null) ? "" : lsStreet.getStreetNumberText().getValue();
		}
		return lsStreetnumbertext;
	}
	/**
	 * This method is used to get location postal code
	 * @return locationpostalcode
	 */
	public final String getLocationPostalcode()
	{
		String locationpostalcode = "";
		StructuredAddressType loStructuredaddress = getStructuredAddress();
		if (loStructuredaddress != null)
		{
			locationpostalcode = loStructuredaddress.getLocationPostalCode().getValue();
		}
		return locationpostalcode;
	}
	/**
	 * This method is used to get location city name
	 * @return lscationcityname
	 */
	public final String getLocationCityName()
	{
		StructuredAddressType loStructuredaddress = getStructuredAddress();
		ProperNameTextType lcn = (loStructuredaddress == null) ? null : loStructuredaddress.getLocationCityName();
		String lscationcityname = "";
		if (lcn != null)
		{
			lscationcityname = lcn.getValue();
		}
		return lscationcityname;
	}
	/**
	 * This method is used to get location state of US postal code
	 * @return lscationstateuspostalservicecode
	 */
	public final String getLocationStateUSPostalServiceCode()
	{
		StructuredAddressType loStructuredaddress = getStructuredAddress();
		USStateCodeType lsusc = (loStructuredaddress == null) ? null : (USStateCodeType) loStructuredaddress.getLocationState().getValue();
		String lscationstateuspostalservicecode = (lsusc == null) ? "" : lsusc.getValue().value();
		return lscationstateuspostalservicecode;
	}
	/**
	 * This method is used to get building id
	 * @return lsBuildingid 
	 */
	public final String getBuildingID()
	{
		LocationBBLType locationbbl = (responseobject.getStandardizedAddress() == null) ? null : responseobject.getStandardizedAddress()
				.getLocationBBL();
		List<IdentificationType> building = (locationbbl != null) ? locationbbl.getBuildingID() : new ArrayList<IdentificationType>();
		String lsBuildingid = (building.size() > 0) ? building.get(0).getIdentificationID().getValue() : "";
		return lsBuildingid;
	}
	/**
	 * This method is used to get X coordinate
	 * @return lsXcoordinate
	 */
	public final String getXCoordinate()
	{
		LocationCoordinatesType locationcoordinates = (responseobject.getStandardizedAddress() == null) ? null : responseobject
				.getStandardizedAddress().getLocationCoordinates();
		TextType coordinate = (locationcoordinates == null) ? null : locationcoordinates.getXCoordinate();
		String lsXcoordinate = (coordinate == null) ? "" : coordinate.getValue();
		return lsXcoordinate;
	}
	/**
	 * This method is used to get Y coordinate
	 * @return lsYcoordinate
	 */
	public final String getYCoordinate()
	{
		LocationCoordinatesType locationcoordinates = (responseobject.getStandardizedAddress() == null) ? null : responseobject
				.getStandardizedAddress().getLocationCoordinates();
		TextType coordinate = (locationcoordinates == null) ? null : locationcoordinates.getYCoordinate();
		String lsYcoordinate = (coordinate == null) ? "" : coordinate.getValue();
		return lsYcoordinate;
	}
	/**
	 * This method is used to get coordinate category text
	 * @return lsCoordinatecategory
	 */
	public final String getCoordinateCategoryText()
	{
		LocationCoordinatesType locationcoordinates = (responseobject.getStandardizedAddress() == null) ? null : responseobject
				.getStandardizedAddress().getLocationCoordinates();
		TextType coordinatecategorytext = (locationcoordinates == null) ? null : locationcoordinates.getCoordinateCategoryText();
		String lsCoordinatecategory = (coordinatecategorytext == null) ? "" : coordinatecategorytext.getValue();
		return lsCoordinatecategory;
	}
	/**
	 * This method is used to get local community name
	 * @return localecommunityname
	 */
	public final String getLocaleCommunityName()
	{
		List<LocaleType> loLocales = (responseobject.getStandardizedAddress() == null) ? new ArrayList<LocaleType>() : responseobject
				.getStandardizedAddress().getLocationLocale();
		String localecommunityname = (loLocales.size() <= 0) ? null : loLocales.get(0).getLocaleCommunityName().getValue();
		localecommunityname = (localecommunityname == null) ? "" : localecommunityname;
		return localecommunityname;
	}
	/**
	 * This method is used to get school district name
	 * @return lsSchooldistrictname
	 */
	public final String getSchoolDistrictName()
	{
		AreaPublicServiceType loAreapublicservice = (responseobject.getStandardizedAddress() == null) ? null : responseobject.getStandardizedAddress()
				.getAreaPublicService();
		TextType schooldistrict = (loAreapublicservice == null) ? null : loAreapublicservice.getSchoolDistrictName();
		String lsSchooldistrictname = (schooldistrict == null) ? null : schooldistrict.getValue();
		lsSchooldistrictname = (lsSchooldistrictname == null) ? "" : lsSchooldistrictname;
		return lsSchooldistrictname;
	}
	/**
	 * This method is used to get health area name
	 * @return lsHealthareaname
	 */
	public final String getHealthAreaName()
	{
		AreaPublicServiceType loAreapublicservice = (responseobject.getStandardizedAddress() == null) ? null : responseobject.getStandardizedAddress()
				.getAreaPublicService();
		TextType healtharea = (loAreapublicservice == null) ? null : loAreapublicservice.getHealthAreaName();
		String lsHealthareaname = (healtharea == null) ? "" : healtharea.getValue();
		return lsHealthareaname;
	}
	/**
	 * This method is used to get NYC borough
	 * @return lsNycb
	 */
	public final String getNYCBorough()
	{
		LocationBBLType locationbbl = (responseobject.getStandardizedAddress() == null) ? null : responseobject.getStandardizedAddress()
				.getLocationBBL();
		String lsNycb = (locationbbl == null) ? "" : locationbbl.getNYCBoroughName().getValue();
		return lsNycb;
	}
	/**
	 * This method is used to get tax block
	 * @return lsTaxblockstring
	 */
	public final String getTaxBlock()
	{
		LocationBBLType locationbbl = (responseobject.getStandardizedAddress() == null) ? null : responseobject.getStandardizedAddress()
				.getLocationBBL();
		List<TextType> taxblock = (locationbbl == null) ? new ArrayList<TextType>() : locationbbl.getTaxBlock();
		String lsTaxblockstring = (taxblock.size() <= 0) ? "" : taxblock.get(0).getValue();

		return lsTaxblockstring;
	}
	/**
	 * This method is used to get tax lot
	 * @return lsTaxlotstring
	 */
	public final String getTaxLot()
	{
		LocationBBLType locationbbl = (responseobject.getStandardizedAddress() == null) ? null : responseobject.getStandardizedAddress()
				.getLocationBBL();
		List<TextType> loTaxlot = (locationbbl == null) ? new ArrayList<TextType>() : locationbbl.getTaxLot();
		String lsTaxlotstring = (loTaxlot.size() <= 0) ? "" : loTaxlot.get(0).getValue();
		return lsTaxlotstring;
	}
	/**
	 * This method is used to get congressional district name
	 * @return lsCongressionaldistrictname
	 */
	public final String getCongressionalDistrictName()
	{
		ElectoralDistrictType loElectoraldistrict = (responseobject.getStandardizedAddress() == null) ? null : responseobject.getStandardizedAddress()
				.getElectoralDistrict();
		String lsCongressionaldistrictname = (loElectoraldistrict == null) ? "" : loElectoraldistrict.getCongressionalDistrictName().getValue();
		return lsCongressionaldistrictname;
	}
	/**
	 * This method is used to get senatorial district name
	 * @return senatorialdistrictname
	 */
	public final String getSenatorialDistrictName()
	{
		ElectoralDistrictType loElectoraldistrict = (responseobject.getStandardizedAddress() == null) ? null : responseobject.getStandardizedAddress()
				.getElectoralDistrict();
		String senatorialdistrictname = (loElectoraldistrict == null) ? "" : (loElectoraldistrict.getStateSenatorialDistrictName() == null) ? ""
				: loElectoraldistrict.getStateSenatorialDistrictName().getValue();
		return senatorialdistrictname;
	}
	/**
	 * This method is used to get local judicial district name
	 * @return localejudicialdistrictname
	 */
	public final String getLocaleJudicialDistrictName()
	{
		List<LocaleType> loLocales = (responseobject.getStandardizedAddress() == null) ? new ArrayList<LocaleType>() : responseobject
				.getStandardizedAddress().getLocationLocale();
		TextType ljddn = (loLocales.size() <= 0) ? null : loLocales.get(0).getLocaleJudicialDistrictName().get(0);
		String localejudicialdistrictname = (ljddn == null) ? "" : ljddn.getValue();

		return localejudicialdistrictname;
	}
	/**
	 * This method is used to get assembly district name
	 * @return lsAssemblydistrictname
	 */
	public final String getAssemblyDistrictName()
	{
		ElectoralDistrictType loElectoraldistrict = (responseobject.getStandardizedAddress() == null) ? null : responseobject.getStandardizedAddress()
				.getElectoralDistrict();
		TextType adn = (loElectoraldistrict == null) ? null : loElectoraldistrict.getStateAssemblyDistrictName();
		String lsAssemblydistrictname = (adn == null) ? "" : adn.getValue();
		return lsAssemblydistrictname;
	}
	/**
	 * This method is used to get address service return code
	 * @return lsAddressservicereturncode
	 */
	public final String getAddressServiceReturnCode()
	{
		Object loStatuscode = (responseobject.getAddressServiceRequestStatus() == null) ? null
				: (responseobject.getAddressServiceRequestStatus() == null) ? null : responseobject.getAddressServiceRequestStatus().getStatusCode();
		String lsAddressservicereturncode = (loStatuscode == null) ? "" : (String) responseobject.getAddressServiceRequestStatus().getStatusCode()
				.getValue();
		return lsAddressservicereturncode;

	}
	/**
	 * 	This method is used to get status description text
	 * @return status as true or false
	 */
	public final String getStatusDescriptionText()
	{
		TextType loStatusdescriptiontext = responseobject.getAddressServiceRequestStatus().getStatusDescriptionText();
		return (loStatusdescriptiontext == null) ? "" : loStatusdescriptiontext.getValue();
	}
	/**
	 * This method is used to get low end cross street name
	 * @return lowendcrossstreetnumber
	 */
	public final String getLowEndCrossStreetName()
	{

		List<StreetSegmentType> loStreetsegments = (responseobject.getStandardizedAddress() == null) ? new ArrayList<StreetSegmentType>()
				: responseobject.getStandardizedAddress().getStreetSegment();
		gov.niem.niem.proxy.xsd._2.String lecsn = (loStreetsegments.size() <= 0) ? null : loStreetsegments.get(0).getLowEndCrossStreetName();
		String lowendcrossstreetnumber = (lecsn == null) ? "" : lecsn.getValue().toString();
		return lowendcrossstreetnumber;
	}
	/**
	 * This method is used to high end cross street name
	 * @return lsHighendcrossstreenumber
	 */
	public final String getHighEndCrossStreetName()
	{
		List<StreetSegmentType> loStreetsegments = (responseobject.getStandardizedAddress() == null) ? new ArrayList<StreetSegmentType>()
				: responseobject.getStandardizedAddress().getStreetSegment();
		gov.niem.niem.proxy.xsd._2.String hecsn = (loStreetsegments.size() <= 0) ? null : loStreetsegments.get(0).getHighEndCrossStreetName();
		String lsHighendcrossstreenumber = (hecsn == null) ? "" : hecsn.getValue().toString();
		return lsHighendcrossstreenumber;
	}
	/**
	 * This method is used to get low end cross street number
	 * @return lowendcrossstreetnumber
	 */
	public final String getLowEndCrossStreetNumber()
	{
		List<StreetSegmentType> loStreetsegments = (responseobject.getStandardizedAddress() == null) ? new ArrayList<StreetSegmentType>()
				: responseobject.getStandardizedAddress().getStreetSegment();
		NonNegativeInteger lecsn = (loStreetsegments.size() <= 0) ? null : loStreetsegments.get(0).getLowEndCrossStreetNumber();
		String lowendcrossstreetnumber = (lecsn == null) ? "" : lecsn.getValue().toString();
		return lowendcrossstreetnumber;

	}
	/**
	 * This method is used to get high end cross street number
	 * @return lsHighendcrossstreenumber
	 */
	public final String getHighEndCrossStreetNumber()
	{
		List<StreetSegmentType> loStreetsegments = (responseobject.getStandardizedAddress() == null) ? new ArrayList<StreetSegmentType>()
				: responseobject.getStandardizedAddress().getStreetSegment();
		NonNegativeInteger hecsn = (loStreetsegments.size() <= 0) ? null : loStreetsegments.get(0).getHighEndCrossStreetNumber();
		String lsHighendcrossstreenumber = (hecsn == null) ? "" : hecsn.getValue().toString();
		return lsHighendcrossstreenumber;

	}
	/**
	 * This method is used to get city council district name
	 * @return lsCitycouncildistrictname
	 */
	public final String getCityCouncilDistrictName()
	{
		ElectoralDistrictType loElectoraldistrict = (responseobject.getStandardizedAddress() == null) ? null : responseobject.getStandardizedAddress()
				.getElectoralDistrict();
		TextType ccdn = (loElectoraldistrict == null) ? null : loElectoraldistrict.getCouncilDistrictName();
		String lsCitycouncildistrictname = (ccdn == null) ? "" : ccdn.getValue();
		return lsCitycouncildistrictname;
	}
	/**
	 * This method is used get status text
	 * @return lsStatustext
	 */
	public final String getStatusText()
	{
		TextType loStxt = responseobject.getAddressServiceRequestStatus().getStatusText();
		String lsStatustext = (loStxt == null) ? "" : loStxt.getValue();
		return lsStatustext;
	}
	/**
	 * This method is used to get status reason
	 * @return lsStatusreason
	 */
	public final String getStatusReason()
	{
		TextType loSreason = null;
		if (responseobject.getAddressServiceRequestStatus().getStatusReason() != null)
		{
			loSreason = (TextType) responseobject.getAddressServiceRequestStatus().getStatusReason().getValue();
		}
		String lsStatusreason = (loSreason == null) ? "" : loSreason.getValue();
		return lsStatusreason;

	}
	/**
	 * This method is used to get status date
	 * @return status
	 */
	public final String getStatusDate()
	{
		DateType loSdate = responseobject.getAddressServiceRequestStatus().getStatusDate();
		XMLGregorianCalendar statusdate = (loSdate == null) ? null : (XMLGregorianCalendar) loSdate.getDateRepresentation().getValue();
		String status = (statusdate != null) ? statusdate.toGregorianCalendar().toString() : "";
		return status;
	}
	/**
	 * This method is used to get possible street name
	 * @return loPossiblestreetnames
	 */
	public final List<String> getPossibleStreetName()
	{
		List<String> loPossiblestreetnames = new ArrayList<String>();

		if (responseobject.getPossibleStreet() != null)
		{
			Iterator<StreetType> loI = responseobject.getPossibleStreet().iterator();
			while (loI.hasNext())
			{
				loPossiblestreetnames.add(loI.next().getStreetName().getValue());
			}
		}
		return loPossiblestreetnames;
	}
	/**
	 * This method is used to get cross streets
	 * @return loCrossStreetNames
	 */
	public Set<Entry<List<String>, String>> getCrossStreets()
	{
		Map<List<String>, String> loCrossStreetNames = new HashMap<List<String>, String>();
		if (responseobject.getCrossStreet() != null)
		{
			Iterator<CrossStreetType> loI = responseobject.getCrossStreet().iterator();
			while (loI.hasNext())
			{
				CrossStreetType crossstreet = loI.next();
				String nodenumber = crossstreet.getNodeNumber().getValue();
				List<String> crossStreetList = new ArrayList<String>();
				Iterator<TextType> streetnameit = crossstreet.getIntersectingStreetName().iterator();
				while (streetnameit.hasNext())
				{
					crossStreetList.add(streetnameit.next().getValue());
				}
				loCrossStreetNames.put(crossStreetList, nodenumber);
			}
		}
		return loCrossStreetNames.entrySet();
	}
	/**
	 * This method is used to throw exception fault
	 */
	@Override
	public final void throwExceptionFault(WSException ex) throws ApplicationFault, SystemFault
	{
		if (ex.getException() instanceof gov.nyc.hhsc.wsdl.addressservice_v1.ApplicationFault)
		{
			gov.nyc.hhsc.wsdl.addressservice_v1.ApplicationFault loF = (gov.nyc.hhsc.wsdl.addressservice_v1.ApplicationFault) ex.getException();
			throw this
					.applicationFault(
							loF.getFaultInfo().getApplicationError().getFaultName(),
							loF.getMessage(),
							WSDLDataTypesObjectFactory.ApplicationErrorCode.fromValue((loF.getFaultInfo().getApplicationError().getErrorCode()) == null ? "HHSC006"
									: loF.getFaultInfo().getApplicationError().getErrorCode().value()), loF.getFaultInfo().getEnterpriseHeader(), loF
									.getFaultInfo().getTransactionHeader());
		}
		else if (ex.getException() instanceof gov.nyc.hhsc.wsdl.addressservice_v1.SystemFault)
		{
			gov.nyc.hhsc.wsdl.addressservice_v1.SystemFault loF = (gov.nyc.hhsc.wsdl.addressservice_v1.SystemFault) ex.getException();
			throw this.systemFault(loF.getFaultInfo().getSystemError().getFaultName(), loF.getFaultInfo().getSystemError().getMessage(), loF.getFaultInfo()
					.getSystemError().getOriginatingError(), loF.getFaultInfo().getSystemError().getTrace(), WSDLDataTypesObjectFactory.SystemErrorCode
					.fromValue(loF.getFaultInfo().getSystemError().getErrorCode().value()), loF.getFaultInfo().getEnterpriseHeader(), loF.getFaultInfo()
					.getTransactionHeader());
		}

	}
}
