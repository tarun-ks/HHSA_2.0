package com.nyc.hhs.webservice.client;

import gov.nyc.datashare.schema.enterpriseheader.EnterpriseHeaderType;
import gov.nyc.dhs.cares.wsdlframework.WSDLDataTypesObjectFactory;
import gov.nyc.dhs.cares.wsdlframework.WSDLDataTypesObjectFactory.ApplicationErrorCode;
import gov.nyc.hhsc.niem.exchange.addressservice._1.AddressServiceRequestType;
import gov.nyc.hhsc.wsdl.addressservice_v1.ApplicationFault;
import gov.nyc.hhsc.wsdl.datatypes_v1.ApplicationErrorType;
import gov.nyc.hhsc.wsdl.datatypes_v1.ApplicationFaultMessageType;
import gov.nyc.hhsc.wsdl.datatypes_v1.TransactionHeaderType;
import gov.nyc.hhsc.wsdl.wsclientinterface.WSRequest;

import java.util.Set;

import com.nyc.hhs.webservice.helper.AddressServiceObjectFactory;

/**
 * AddressServiceRequest extends from generic type WSRequest&lt;AddressServiceRequestType&gt; and provides
 * all the setters required to populate the request. This request object is used WSClient_AddressService to 
 * make web services call to the Address Service web service. All setter methods are final as defined by the 
 * contract specified in WSRequest. AddressServiceRequest provides with the enum types:
 * NYCBorough, USStateCode, DirectionCode
 * 
 */

public class AddressServiceRequest extends WSRequest<AddressServiceRequestType>
{
	
	private TransactionHeaderAction transactionheaderaction;

	public AddressServiceRequest()
	{
		super();
	};

	@Override
	protected final void setRequiredRequestParameters()
	{
		requiredparameters.add("StreetName");

	}

	@Override
	protected final void setAllRequestParameters()
	{
		allparameters.add("StreetName");
		allparameters.add("NYCBorough");
		allparameters.add("StreetNumberText");
		allparameters.add("LocationCityName");
		allparameters.add("LocationStateUSPostalServiceCode");
		allparameters.add("LocationPostalCode");
		allparameters.add("FirstStreetName");
		allparameters.add("SecondStreetName");
		allparameters.add("RelativelocationDirectionCode");

	}

	@Override
	protected final AddressServiceRequestType getRequestMapping() throws ApplicationFault
	{

		AddressServiceObjectFactory aserviceobjectfactory = new AddressServiceObjectFactory();

		if (((transactionheaderaction.equals(TransactionHeaderAction.NYCADDRESS)
				|| transactionheaderaction.equals(TransactionHeaderAction.CROSSSTREET) || transactionheaderaction
				.equals(TransactionHeaderAction.SEARCHSTREET)) && (request.get("NYCBorough") == null))
				|| (transactionheaderaction.equals(TransactionHeaderAction.NON_NYCADDRESS) && (request.get("LocationPostalCode") == null)))
		{
			throw applicationFault("Schema validation error found", "Missing required field(s)",
					WSDLDataTypesObjectFactory.ApplicationErrorCode.HHSC_003, null, null);
		}
		AddressServiceRequestType servicerequest = aserviceobjectfactory.createAddressServiceRequestType((String) request.get("StreetName"),
				(String) request.get("StreetNumberText"), (String) request.get("LocationCityName"), (String) request.get("LocationPostalCode"),
				(String) request.get("LocationStateUSPostalServiceCode"), (String) request.get("NYCBorough"),
				(String) request.get("FirstStreetName"), (String) request.get("SecondStreetName"),
				(String) request.get("RelativelocationDirectionCodestreetname"));

		return servicerequest;
	}

	public final void setStreetName(String asValue)
	{
		request.put("StreetName", asValue);
	}

	public final void setNYCBorough(NYCBorough asValue)
	{
		request.put("NYCBorough", asValue.value());
	}

	public final void setStreetNumberText(String asValue)
	{
		request.put("StreetNumberText", asValue);
	}

	public final void setLocationCityName(String asValue)
	{
		request.put("LocationCityName", asValue);
	}

	public final void setLocationStateUSPostalServiceCode(USStateCode value)
	{
		request.put("LocationStateUSPostalServiceCode", value.value());
	}

	public final void setLocationPostalCode(String asValue)
	{
		request.put("LocationPostalCode", asValue);
	}

	public final void setFirstStreetName(String asValue)
	{
		request.put("FirstStreetName", asValue);
	}

	public final void setSecondStreetName(String asValue)
	{
		request.put("SecondStreetName", asValue);
	}

	public final void setRelativelocationDirectionCode(DirectionCode asValue)
	{
		request.put("RelativelocationDirectionCode", asValue.value());

	}

	public static enum TransactionHeaderAction
	{

		NYCADDRESS("HHSCCARES_AddrSearchInNYC"), NON_NYCADDRESS("HHSCCARES_AddrSearchOutNYC"), CROSSSTREET("HHSCCARES_NYCCrossStSearch"), SEARCHSTREET(
				"HHSCCARES_NYCPossibleStSearch");

		private final String value;

		TransactionHeaderAction(String asV)
		{
			value = asV;
		}

		public String value()
		{
			return value;
		}

		public static TransactionHeaderAction fromValue(String asV)
		{
			for (TransactionHeaderAction a : TransactionHeaderAction.values())
			{
				if (a.value.equals(asV))
				{
					return a;
				}
			}
			throw new IllegalArgumentException(asV);
		}
	}

	
	public static enum NYCBorough
	{
		BRONX("Bronx"), BROOKLYN("Brooklyn"), MANHATTAN("Manhattan"), QUEENS("Queens"), STATEN_ISLAND("Staten Island");

		private final String value;

		NYCBorough(String v)
		{
			value = v;
		}

		public String value()
		{
			return value;
		}

		public static NYCBorough fromValue(String asV)
		{
			for (NYCBorough c : NYCBorough.values())
			{
				if (c.value.equals(asV))
				{
					return c;
				}
			}
			throw new IllegalArgumentException(asV);
		}

	}


	public static enum USStateCode
	{
		AA, AE, AK, AL, AP, AR, AS, AZ, CA, CO, CT, DC, DE, FL, FM, GA, GU, HI, IA, ID, IL, IN, KS, KY, LA, MA, MD, ME, MH, MI, MN, MO, MP, MS, MT, NC, ND, NE, NH, NJ, NM, NV, NY, OH, OK, OR, PA, PR, PW, RI, SC, SD, TN, TX, UT, VA, VI, VT, WA, WI, WV, WY;

		public String value()
		{
			return name();
		}

		public static USStateCode fromValue(String v)
		{
			return valueOf(v);
		}

	}


	public static enum DirectionCode
	{
		EAST("East"), NORTH("North"), SOUTH("South"), WEST("West");
		private final String value;

		DirectionCode(String v)
		{
			value = v;
		}

		public String value()
		{
			return value;
		}

		public static DirectionCode fromValue(String asV)
		{
			for (DirectionCode c : DirectionCode.values())
			{
				if (c.value.equals(asV))
				{
					return c;
				}
			}
			throw new IllegalArgumentException(asV);
		}

	}

	@Override
	public void setTransactionHeaderAction(String lsAction)
	{
		transactionheaderaction = TransactionHeaderAction.fromValue(lsAction);

	}

	@Override
	public void setTransactionHeaderDestination(String lsDestination)
	{

	}

	@Override
	public String getTransactionHeaderAction()
	{

		return transactionheaderaction.value;
	}

	@Override
	public String getTransactionHeaderDestination()
	{
		return null;
	}

	@Override
	public void setTransactionHeaderRequesterAgencyCode(String lsCode)
	{

	}

	@Override
	public void setTransactionHeaderUserGroup(Set<String> loGroup)
	{

	}

	@Override
	public void setTransactionHeaderSourceSystemName(String lsSource)
	{

	}

	@Override
	public String getTransactionHeaderRequesterAgencyCode()
	{
		return null;
	}

	@Override
	public Set<String> getTransactionHeaderUserGroup()
	{
		return null;
	}

	@Override
	public String getTransactionHeaderSourceSystemName()
	{
		return null;
	}

	private ApplicationFault applicationFault(String lsFaultheader, String lsFaultmsg, ApplicationErrorCode lsErrorcode,
			EnterpriseHeaderType lsEnterpriseheader, TransactionHeaderType lsTransactionheader)
	{
		WSDLDataTypesObjectFactory objectfactory = new WSDLDataTypesObjectFactory();
		ApplicationErrorType applicationerror = objectfactory.createApplicationError(lsFaultheader, lsFaultmsg, lsErrorcode).getValue();
		ApplicationFaultMessageType appfaultmsg = objectfactory.createApplicationFaultMessage(applicationerror, lsEnterpriseheader, lsTransactionheader)
				.getValue();
		ApplicationFault appfault = new ApplicationFault(lsFaultheader, appfaultmsg);

		return appfault;
	}
}
