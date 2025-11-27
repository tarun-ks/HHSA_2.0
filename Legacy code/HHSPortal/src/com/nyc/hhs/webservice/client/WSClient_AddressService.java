package com.nyc.hhs.webservice.client;

/**
 * WSClient_AddressService extends abstract class WSClient<AddressServiceReques, 
 * AddressServiceRespons>. It is used to process Address Service web service 
 * request. Although it is possible to instantiate a WSClient_AddressService 
 * object, this is best archived by requesting an object instance from WSClientFactory.
 *		
 */

import gov.nyc.datashare.schema.enterpriseheader.EnterpriseHeaderType;
import gov.nyc.hhsc.wsdl.datatypes_v1.TransactionHeaderType;
import gov.nyc.hhsc.wsdl.wsclientinterface.WSClient;

import java.net.URL;

import javax.xml.namespace.QName;

public class WSClient_AddressService extends WSClient<AddressServiceRequest, AddressServiceResponse>
{

	public WSClient_AddressService(URL loWebserviceurl, URL loWsdlurl, String lsUsername, String lsPassword,
			TransactionHeaderType loTransactionheader, EnterpriseHeaderType loEnterpriseheader,
			Integer loRequestTimeoutInMillisecs) throws Exception
	{
		super(loWebserviceurl, loWsdlurl, lsUsername, lsPassword, loTransactionheader, loEnterpriseheader,
				loRequestTimeoutInMillisecs);
	};

	@Override
	protected String getServiceName()
	{
		return new String("addressService");

	}

	@Override
	protected Class<?> getWSStub()
	{
		return gov.nyc.hhsc.wsdl.addressservice_v1.AddressServiceV10.class;

	}

	@Override
	protected QName getWSPort()
	{
		return new QName("http://hhsc.nyc.gov/wsdl/AddressService_V1", "AddressService_V1_0");

	}

	@Override
	protected QName getWSService()
	{
		return new QName("http://hhsc.nyc.gov/wsdl/AddressService_V1", "AddressService_V1_0");
	}

}
