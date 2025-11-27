package com.nyc.hhs.controllers.utilTest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.controllers.util.ContractListUtils;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.junit.util.JUnitUtil;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.ContractList;
import com.nyc.hhs.model.ProgramNameInfo;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8SecurityOperations;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.PropertyLoader;

public class ContractListUtilsTest {
	/**
	 * SQL session created ONCE before the class
	 * 
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		Boolean lbThrown = false;
		try
		{
		       JUnitUtil.getTransactionManager();
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}
	
	/**
	 * This method get the default filenet session
	 * @return loUserSession filenet session as output
	 * @throws ApplicationException Application Exception thrown in case of queries failure.
	 */
	public P8UserSession getFileNetSession() throws ApplicationException
	{
		System.setProperty(P8Constants.PROP_FILE_JAVA_SECURITY_AUTH_LOGIN_CONFIG, PropertyLoader.getProperty(
				P8Constants.PROPERTY_FILE, P8Constants.PROP_FILE_JAVA_SECURITY_AUTH_LOGIN_CONFIG));
		System.setProperty(P8Constants.PROP_FILE_JAVA_NAMING_FACTORY_INITIAL, PropertyLoader.getProperty(
				P8Constants.PROPERTY_FILE, P8Constants.PROP_FILE_JAVA_NAMING_FACTORY_INITIAL));
		System.setProperty(P8Constants.PROP_FILE_FILENET_PE_BOOTSTRAP_CEURI,
				PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "FILENET_URI"));

		P8UserSession loUserSession = new P8UserSession();
		loUserSession.setContentEngineUri(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "FILENET_URI"));
		loUserSession.setObjectStoreName(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "OBJECT_STORE_NAME"));
		loUserSession.setIsolatedRegionName(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
				"CONNECTION_POINT_NAME"));
		loUserSession.setUserId("ceadmin");
		loUserSession.setPassword("Filenet1");
		P8SecurityOperations loP8SecurityService = new P8SecurityOperations();
		loP8SecurityService.getPESession(loUserSession);
		loP8SecurityService.getObjectStore(loUserSession);

		return loUserSession;
	}
	
	/**This method set the default param for contract list.
	 * @return loContractList ContractList bean as output.
	 */
	private ContractList setContractListFilterParams()
	{
		ContractList loContractList = new ContractList();
		loContractList.setStartNode(1);
		loContractList.setEndNode(5);
		loContractList.setFirstSort(HHSConstants.STATUS);
		loContractList.setSecondSort(HHSConstants.LAST_UPDATE_DATE);
		loContractList.setFirstSortType(HHSConstants.ASCENDING);
		loContractList.setSecondSortType(HHSConstants.DESCENDING);
		loContractList.setOrgName("accenture");
		loContractList.setContractId("1");
		loContractList.setContractTypeId("1");
		return loContractList;
	}
	
	/**
	 * This method is unit test case for getProgramNameList method in ContractListUtils.
	 * @throws ApplicationException Application Exception thrown in case of queries failure.
	 */
	@Test
	public void getProgramNameListTest() throws ApplicationException
	{
		Channel loChannelObj = new Channel();
		ContractList loContractFilterBean  = setContractListFilterParams();
		loChannelObj.setData(HHSConstants.ORGTYPE, HHSConstants.USER_CITY);
		loChannelObj.setData(HHSConstants.CONTRACT_FILTER_BEAN, loContractFilterBean);
		List<ProgramNameInfo> loProgramNameList = (List<ProgramNameInfo>) ContractListUtils.getProgramNameList(loChannelObj);
		assertTrue(loProgramNameList.size() > 0);
	}
	
	/**
	 * This method is unit test case for getAgencyDetails method in ContractListUtils.
	 * @throws ApplicationException Application Exception thrown in case of queries failure.
	 */
	@Test
	public void getAgencyDetailsTest() throws ApplicationException
	{
		Channel loChannelObj = new Channel();
		ContractList loContractFilterBean  = setContractListFilterParams();
		loChannelObj.setData(HHSConstants.ORGTYPE, HHSConstants.USER_CITY);
		loChannelObj.setData(HHSConstants.CONTRACT_FILTER_BEAN, loContractFilterBean);
		List<HashMap<String, String>> loAgencyDetails = (List<HashMap<String, String>>) ContractListUtils.getAgencyDetails(loChannelObj);
		assertTrue(loAgencyDetails.size() > 0);
	}
	
	/**
	 * This method is unit test case for getRenewalRecordExist method in ContractListUtils.
	 * @throws ApplicationException Application Exception thrown in case of queries failure.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void getRenewalRecordExistTest() throws ApplicationException
	{
		Channel loChannelObj = new Channel();
		HashMap aoContractStatus = new HashMap();
		aoContractStatus.put("contract_type_Id", "1");
		aoContractStatus.put("contract_Id", "12312");
		String[] aoStatusList = { "72", "71", "73", "74", "75" };
		aoContractStatus.put("status_Id", aoStatusList);
		loChannelObj.setData(HHSConstants.AO_CONTRACT_STATUS, aoContractStatus);
		Boolean lbIsRenewalRecordNotExist = (Boolean) ContractListUtils.getRenewalRecordExist(loChannelObj);
		assertTrue(lbIsRenewalRecordNotExist);
	}
	
	/**
	 * This method is unit test case for getRenewalRecordExist method in ContractListUtils.
	 * @throws ApplicationException Application Exception thrown in case of queries failure.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void getRenewalRecordExistBlankContractIdTest() throws ApplicationException
	{
		Channel loChannelObj = new Channel();
		HashMap aoContractStatus = new HashMap();
		aoContractStatus.put("contract_type_Id", "1");
		aoContractStatus.put("contract_Id", "");
		String[] aoStatusList = { "72", "71", "73", "74", "75" };
		aoContractStatus.put("status_Id", aoStatusList);
		loChannelObj.setData(HHSConstants.AO_CONTRACT_STATUS, aoContractStatus);
		Boolean lbIsRenewalRecordNotExist = (Boolean) ContractListUtils.getRenewalRecordExist(loChannelObj);
		assertTrue(lbIsRenewalRecordNotExist);
	}
	
	/**
	 * This method is unit test case for getRenewalRecordExist method in ContractListUtils.
	 * @throws ApplicationException Application Exception thrown in case of queries failure.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void getRenewalRecordExistBlankContractTypeIdTest() throws ApplicationException
	{
		Channel loChannelObj = new Channel();
		HashMap aoContractStatus = new HashMap();
		aoContractStatus.put("contract_type_Id", "");
		aoContractStatus.put("contract_Id", "12312");
		String[] aoStatusList = { "72", "71", "73", "74", "75" };
		aoContractStatus.put("status_Id", aoStatusList);
		loChannelObj.setData(HHSConstants.AO_CONTRACT_STATUS, aoContractStatus);
		Boolean lbIsRenewalRecordNotExist = (Boolean) ContractListUtils.getRenewalRecordExist(loChannelObj);
		assertTrue(lbIsRenewalRecordNotExist);
	}
	
	/**
	 * This method is unit test case for setModifiedBy method in ContractListUtils.
	 * @throws ApplicationException Application Exception thrown in case of queries failure.
	 */
	@Test
	public void setModifiedByCityOrgTest() throws ApplicationException
	{
		CBGridBean loGridBean = new CBGridBean();
		String lsUserOrgType = "city_org";
		String lsUserId = "623";
		ContractListUtils.setModifiedBy(loGridBean,lsUserOrgType,lsUserId);
	}
	
	/**
	 * This method is unit test case for setModifiedBy method in ContractListUtils.
	 * @throws ApplicationException Application Exception thrown in case of queries failure.
	 */
	@Test
	public void setModifiedByProviderOrgTest() throws ApplicationException
	{
		CBGridBean loGridBean = new CBGridBean();
		String lsUserOrgType = "provider_org";
		String lsUserId = "623";
		ContractListUtils.setModifiedBy(loGridBean,lsUserOrgType,lsUserId);
	}
	
	/**
	 * This method is unit test case for getAmendErrorCheck method in ContractListUtils.
	 * @throws ApplicationException Application Exception thrown in case of queries failure.
	 */
	@SuppressWarnings({ "rawtypes", "unused" })
	@Test
	public void getAmendErrorCheckTest() throws ApplicationException
	{
		Channel loChannelObj = new Channel();
		loChannelObj.setData("asContractAmount", "100");
		loChannelObj.setData("asAmendmentAmount", "122");
		loChannelObj.setData("asContractId", "11177");
		loChannelObj.setData("aoFilenetSession", getFileNetSession());
		HashMap loErrorCheckRule = (HashMap) ContractListUtils.getAmendErrorCheck(loChannelObj);
	}
	
	/**
	 * This method is unit test case for selectContractAmendmentId method in ContractListUtils.
	 * @throws ApplicationException Application Exception thrown in case of queries failure.
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void selectContractAmendmentIdTest() throws ApplicationException
	{
		Channel loChannelObj = new Channel();
		ContractList loContractFilterBean  = setContractListFilterParams();
		loChannelObj.setData("aoContractBean", loContractFilterBean);
		HashMap loErrorCheckRule = (HashMap) ContractListUtils.selectContractAmendmentId(loChannelObj);
		assertTrue((loErrorCheckRule.get("CONTRACT_ID")) !=null);
	}
	

	/**
	 * This method is unit test case for validateEpin method in ContractListUtils.
	 * @throws ApplicationException Application Exception thrown in case of queries failure.
	 */
	@Test
	public void validateEpinTest() throws ApplicationException
	{
		Channel loChannelObj = new Channel();
		loChannelObj.setData("asEPin", "12312");
		boolean lbIsEpinValid = (boolean) ContractListUtils.validateEpin(loChannelObj);
		assertTrue(lbIsEpinValid);
	}

	/**
	 * This method is unit test case for validateProviderAccelerator method in ContractListUtils.
	 * @throws ApplicationException Application Exception thrown in case of queries failure.
	 */
	@Test
	public void validateProviderAcceleratorTest() throws ApplicationException
	{
		Channel loChannelObj = new Channel();
		loChannelObj.setData("asVendorFmsId", "12312");
		boolean lbIsEpinValid = (boolean) ContractListUtils.validateProviderAccelerator(loChannelObj);
		assertFalse(lbIsEpinValid);
	}


}
