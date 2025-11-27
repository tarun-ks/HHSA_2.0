package com.nyc.hhs.webservice.helper;

import gov.nyc.dhs.cares.niemframework.HHSCDomainObjectFactory;
import gov.nyc.dhs.cares.niemframework.NIEMExtendedObjectFactory2;
import gov.nyc.hhsc.niem.exchange.addressservice._1.AddressParameterType;
import gov.nyc.hhsc.niem.exchange.addressservice._1.AddressServiceRequestType;
import gov.nyc.hhsc.niem.exchange.addressservice._1.DirectionCodeSimpleType;
import gov.nyc.hhsc.niem.exchange.addressservice._1.DirectionCodeType;
import gov.nyc.hhsc.niem.exchange.addressservice._1.NYCAddressParameterType;
import gov.nyc.hhsc.niem.exchange.addressservice._1.NYCCrossStreetParameterType;
import gov.nyc.hhsc.niem.exchange.addressservice._1.NYCSearchStreetParameterType;
import gov.nyc.hhsc.niem.exchange.addressservice._1.ObjectFactory;

/**
 * This class is for Address validation
 * 
 */

public class AddressServiceObjectFactory extends ObjectFactory
{

	public AddressServiceRequestType createAddressServiceRequestType(String lsStreetname, String lsStreetnumber, String lsLocationcityname,
			String lsPostalcode, String lsUsstatecode, String lsNycborough, String lsFirststreetname, String lsSecondstreetname, String lsRelativelocation)
	{

		AddressServiceRequestType request = createAddressServiceRequestType();

		AddressParameterType loAddress = createAddressParameterType(lsStreetname, lsStreetnumber, lsLocationcityname, lsPostalcode, lsUsstatecode);
		NYCAddressParameterType loNycaddress = createNYCAddressParameterType(lsStreetname, lsStreetnumber, lsNycborough, lsLocationcityname, lsPostalcode,
				lsUsstatecode);
		NYCCrossStreetParameterType loNyccrossstreet = createCrossStreetParameterType(lsFirststreetname, lsSecondstreetname, lsStreetname, lsNycborough,
				lsRelativelocation);

		NYCSearchStreetParameterType loSearchstreet = createNYCSearchStreetParameterType(lsStreetname, lsNycborough);

		request.setAddressParameter(loAddress);
		request.setNYCAddressParameter(loNycaddress);
		request.setNYCCrossStreetParameter(loNyccrossstreet);
		request.setNYCSearchStreetParameter(loSearchstreet);

		return request;

	}

	public AddressParameterType createAddressParameterType(String lsStreetname, String lsStreetnumber, String lsCity, String lsPostalcode,
			String lsStateuspostalcode)
	{
		AddressParameterType loAddress = createAddressParameterType();
		loAddress.setLocationCityName(NIEMExtendedObjectFactory2.NIEMCoreObjectFactory().createProperNameTextType(lsCity));
		loAddress.setLocationPostalCode(NIEMExtendedObjectFactory2.ProxyXSDObjectFactory().createString(lsPostalcode));
		if (lsStateuspostalcode != null)
		{
			loAddress.setLocationStateUSPostalServiceCode(NIEMExtendedObjectFactory2.USPSStatesObjectFactory().createUSStateCodeType(lsStateuspostalcode));
		}
		loAddress.setStreetName(NIEMExtendedObjectFactory2.NIEMCoreObjectFactory().createProperNameTextType(lsStreetname));
		loAddress.setStreetNumberText(NIEMExtendedObjectFactory2.NIEMCoreObjectFactory().createTextType(lsStreetnumber));
		return loAddress;
	}

	public NYCAddressParameterType createNYCAddressParameterType(String lsStreetname, String lsStreetnumber, String lsNycborough, String lsLocationcityname,
			String lsPostalcode, String lsUsstatecode)
	{

		HHSCDomainObjectFactory hhscdomain = NIEMExtendedObjectFactory2.HHSCDomainObjectFactory();
		NYCAddressParameterType nycaddress = createNYCAddressParameterType();
		if (lsNycborough != null)
		{
			nycaddress.setNYCBorough(hhscdomain.createNYCBorough(hhscdomain.createNYCBoroughType(HHSCDomainObjectFactory.NYCBorough
					.fromValue(lsNycborough))));
		}
		nycaddress.setStreetName(NIEMExtendedObjectFactory2.NIEMCoreObjectFactory().createProperNameTextType(lsStreetname));
		nycaddress.setStreetNumberText(NIEMExtendedObjectFactory2.NIEMCoreObjectFactory().createProperNameTextType(lsStreetnumber));
		nycaddress.setLocationCityName(NIEMExtendedObjectFactory2.NIEMCoreObjectFactory().createProperNameTextType(lsLocationcityname));
		if (lsUsstatecode != null)
		{
			nycaddress.setLocationStateUSPostalServiceCode(NIEMExtendedObjectFactory2.USPSStatesObjectFactory().createUSStateCodeType(lsUsstatecode));
		}
		nycaddress.setLocationPostalCode(NIEMExtendedObjectFactory2.ProxyXSDObjectFactory().createString(lsPostalcode));

		return nycaddress;
	}

	public NYCCrossStreetParameterType createCrossStreetParameterType(String lsFirststreetname, String lsSecondstreetname, String lsCrossstreetname,
			String lsNycborough, String lsRelativelocation)
	{

		NYCCrossStreetParameterType loCrossstreet = createNYCCrossStreetParameterType();
		loCrossstreet.setStreetName(NIEMExtendedObjectFactory2.NIEMCoreObjectFactory().createProperNameTextType(lsCrossstreetname));
		loCrossstreet.setFirstStreetName(NIEMExtendedObjectFactory2.NIEMCoreObjectFactory().createTextType(lsFirststreetname));
		HHSCDomainObjectFactory loHhscdomain = NIEMExtendedObjectFactory2.HHSCDomainObjectFactory();
		if (lsNycborough != null)
		{
			loCrossstreet.setNYCBorough(loHhscdomain.createNYCBorough(loHhscdomain.createNYCBoroughType(HHSCDomainObjectFactory.NYCBorough
					.fromValue(lsNycborough))));
		}
		if (lsRelativelocation != null)
		{
			DirectionCodeType loDirectioncode = createDirectionCodeType();
			loDirectioncode.setValue(DirectionCodeSimpleType.fromValue(lsRelativelocation));
			loCrossstreet.setRelativeLocationDirection(createRelativeLocationDirectionCode(loDirectioncode));
		}
		loCrossstreet.setSecondStreetName(NIEMExtendedObjectFactory2.NIEMCoreObjectFactory().createTextType(lsSecondstreetname));
		return loCrossstreet;
	}

	public NYCSearchStreetParameterType createNYCSearchStreetParameterType(String lsSearchstreetname, String lsNycborought)
	{

		NYCSearchStreetParameterType loSearchstreet = createNYCSearchStreetParameterType();
		loSearchstreet.setStreetName(NIEMExtendedObjectFactory2.NIEMCoreObjectFactory().createProperNameTextType(lsSearchstreetname));

		HHSCDomainObjectFactory loHhscdomain = NIEMExtendedObjectFactory2.HHSCDomainObjectFactory();

		if (lsNycborought != null)
		{
			loSearchstreet.setNYCBorough(loHhscdomain.createNYCBorough(loHhscdomain.createNYCBoroughType(HHSCDomainObjectFactory.NYCBorough
					.fromValue(lsNycborought))));
		}

		return loSearchstreet;
	}
}
