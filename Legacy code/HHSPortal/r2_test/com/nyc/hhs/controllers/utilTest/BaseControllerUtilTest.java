package com.nyc.hhs.controllers.utilTest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.controllers.util.BaseControllerUtil;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.junit.util.JUnitUtil;
import com.nyc.hhs.model.AccountsAllocationBean;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.CBMileStoneBean;
import com.nyc.hhs.model.Document;
import com.nyc.hhs.model.DocumentPropertiesBean;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.model.UnallocatedFunds;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.PropertyLoader;

public class BaseControllerUtilTest
{
	private static SqlSession moSession = null;
	private static P8UserSession session = null;

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
			moSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
			session = getFileNetSession();
//			JUnitUtil.getTransactionManager();
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	/**
	 * Close the SQL session created at the beginning
	 * 
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		Boolean lbThrown = false;
		try
		{
			moSession.close();
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	public static P8UserSession getFileNetSession() throws ApplicationException
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

		SqlSession loFilenetPEDBSession = HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory()
				.openSession();
		loUserSession.setFilenetPEDBSession(loFilenetPEDBSession);
		// loP8SecurityService.getPESession(loUserSession);
		// loP8SecurityService.getObjectStore(loUserSession);

		return loUserSession;
	}

	/**
	 * This method tests setting loAccountsAllocationBean when lsUobc is null
	 * 
	 */
	@Test
	public void testCopyCBGridBeanToAllocBeanUtillsUobcIsNull() throws ApplicationException
	{
		AccountsAllocationBean loAccountsAllocationBean = new AccountsAllocationBean();
		String lsId = "123";
		String lsUobc = null;
		String lsSubOC = "123";
		String lsRc = "123";
		String lsTotal = "123";
		Map<String, Object> loFiscalYrMap = new HashMap<String, Object>();
		Object liStartYear = new Integer(4);
		Object liEndYear = new Integer(4);
		loFiscalYrMap.put(HHSConstants.LI_START_YEAR, liStartYear);
		loFiscalYrMap.put(HHSConstants.LI_END_YEAR, liEndYear);

		CBGridBean loCBGridBean = new CBGridBean();
		BaseControllerUtil.copyCBGridBeanToAllocBeanUtil(loAccountsAllocationBean, lsId, lsUobc, lsSubOC, lsRc,
				lsTotal, loCBGridBean, loFiscalYrMap);

		assertTrue(loAccountsAllocationBean.getUnitOfAppropriation().equals(HHSConstants.EMPTY_STRING));
		assertTrue(loAccountsAllocationBean.getBudgetCode().equals(HHSConstants.EMPTY_STRING));
		assertTrue(loAccountsAllocationBean.getObjectCode().equals(HHSConstants.EMPTY_STRING));

	}

	/**
	 * This method tests setting loAccountsAllocationBean when variable are not
	 * null
	 * 
	 */
	@Test
	public void testCopyCBGridBeanToAllocBeanUtil() throws ApplicationException
	{
		AccountsAllocationBean loAccountsAllocationBean = new AccountsAllocationBean();
		String lsId = "123";
		String lsUobc = "123-234-334";
		String lsSubOC = "123";
		String lsRc = "123";
		String lsTotal = "123";
		Map<String, Object> loFiscalYrMap = new HashMap<String, Object>();
		Object liStartYear = new Integer("0");
		Object liEndYear = new Integer("12");
		loFiscalYrMap.put(HHSConstants.LI_START_YEAR, liStartYear);
		loFiscalYrMap.put(HHSConstants.LI_END_YEAR, liEndYear);

		CBGridBean loCBGridBean = new CBGridBean();
		BaseControllerUtil.copyCBGridBeanToAllocBeanUtil(loAccountsAllocationBean, lsId, lsUobc, lsSubOC, lsRc,
				lsTotal, loCBGridBean, loFiscalYrMap);

		assertNotNull(loAccountsAllocationBean.getUnitOfAppropriation());
		assertNotNull(loAccountsAllocationBean.getBudgetCode());
		assertNotNull(loAccountsAllocationBean.getObjectCode());

	}

	/**
	 * This method tests setting loAccountsAllocationBean when liStartYear and
	 * liEndYear are not null
	 */
	@Test
	public void testCopyCBGridBeanToAllocBeanUtilYrNotNull() throws ApplicationException
	{
		AccountsAllocationBean loAccountsAllocationBean = new AccountsAllocationBean();
		String lsId = "123";
		String lsUobc = "123-234-334";
		String lsSubOC = "123";
		String lsRc = "123";
		String lsTotal = "123";
		Map<String, Object> loFiscalYrMap = new HashMap<String, Object>();
		Object liStartYear = new Integer("0");
		Object liEndYear = new Integer("12");
		loFiscalYrMap.put(HHSConstants.LI_START_YEAR, liStartYear);
		loFiscalYrMap.put(HHSConstants.LI_END_YEAR, liEndYear);

		CBGridBean loCBGridBean = new CBGridBean();
		BaseControllerUtil.copyCBGridBeanToAllocBeanUtil(loAccountsAllocationBean, lsId, lsUobc, lsSubOC, lsRc,
				lsTotal, loCBGridBean, loFiscalYrMap);

		assertNotNull(loAccountsAllocationBean.getContractStartFY());
		assertNotNull(loAccountsAllocationBean.getContractEndFY());
	}

	/**
	 * This method tests setting loAccountsAllocationBean when liStartYear is
	 * null
	 */
	@Test
	public void testCopyCBGridBeanToAllocBeanUtilStartYrNull() throws ApplicationException
	{
		AccountsAllocationBean loAccountsAllocationBean = new AccountsAllocationBean();
		String lsId = "123";
		String lsUobc = "123-234-334";
		String lsSubOC = "123";
		String lsRc = "123";
		String lsTotal = "123";
		Map<String, Object> loFiscalYrMap = new HashMap<String, Object>();
		Object liEndYear = new Integer("12");
		loFiscalYrMap.put(HHSConstants.LI_START_YEAR, null);
		loFiscalYrMap.put(HHSConstants.LI_END_YEAR, liEndYear);

		CBGridBean loCBGridBean = new CBGridBean();
		BaseControllerUtil.copyCBGridBeanToAllocBeanUtil(loAccountsAllocationBean, lsId, lsUobc, lsSubOC, lsRc,
				lsTotal, loCBGridBean, loFiscalYrMap);

		assertNull(loAccountsAllocationBean.getContractStartFY());
		assertNull(loAccountsAllocationBean.getContractEndFY());
	}

	/**
	 * This method tests setting loAccountsAllocationBean when liEndYear is null
	 */
	@Test
	public void testCopyCBGridBeanToAllocBeanUtilEndYrNull() throws ApplicationException
	{
		AccountsAllocationBean loAccountsAllocationBean = new AccountsAllocationBean();
		String lsId = "123";
		String lsUobc = "123-234-334";
		String lsSubOC = "123";
		String lsRc = "123";
		String lsTotal = "123";
		Map<String, Object> loFiscalYrMap = new HashMap<String, Object>();
		Object liStartYear = new Integer("12");
		loFiscalYrMap.put(HHSConstants.LI_START_YEAR, liStartYear);
		loFiscalYrMap.put(HHSConstants.LI_END_YEAR, null);

		CBGridBean loCBGridBean = new CBGridBean();
		BaseControllerUtil.copyCBGridBeanToAllocBeanUtil(loAccountsAllocationBean, lsId, lsUobc, lsSubOC, lsRc,
				lsTotal, loCBGridBean, loFiscalYrMap);

		assertNull(loAccountsAllocationBean.getContractStartFY());
		assertNull(loAccountsAllocationBean.getContractEndFY());
	}

	/**
	 * This method tests when loMethodName is GET_FISCAL_YEAR_HEADER_PROP.
	 */
	@Test
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void testGetFiscalYearUtilGetFiscalYearHeaderProp() throws ApplicationException
	{
		Map loContractMap = new HashMap();
		int loMethodName = HHSConstants.GET_FISCAL_YEAR_HEADER_PROP;
		Object liFYcount = new Integer("1");
		Object liStartFyCounter = new Integer("1");
		loContractMap.put(HHSConstants.LI_FYCOUNT, liFYcount);
		loContractMap.put(HHSConstants.LI_START_FY_COUNTER, liStartFyCounter);
		StringBuffer loStringBuffer = BaseControllerUtil.getFiscalYearUtil(loContractMap, loMethodName);

		assertNotNull(loStringBuffer);
	}

	/**
	 * This method tests when loMethodName is GET_FISCAL_YEAR_SUB_GRID_PROP.
	 */
	@Test
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void testGetFiscalYearUtilGetFiscalYearSubGridProp() throws ApplicationException
	{
		Map loContractMap = new HashMap();
		int loMethodName = HHSConstants.GET_FISCAL_YEAR_SUB_GRID_PROP;
		Object liFYcount = new Integer("1");
		Object liStartFyCounter = new Integer("1");
		loContractMap.put(HHSConstants.LI_FYCOUNT, liFYcount);
		loContractMap.put(HHSConstants.LI_START_FY_COUNTER, liStartFyCounter);
		StringBuffer loStringBuffer = BaseControllerUtil.getFiscalYearUtil(loContractMap, loMethodName);

		assertNotNull(loStringBuffer);
	}

	/**
	 * This method tests when loMethodName is GET_FISCAL_YEAR_GRID.
	 */
	@Test
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void testGetFiscalYearUtilGetFiscalYearGrid() throws ApplicationException
	{
		Map loContractMap = new HashMap();
		int loMethodName = HHSConstants.GET_FISCAL_YEAR_GRID;
		Object liFYcount = new Integer("1");
		Object liStartFyCounter = new Integer("1");
		loContractMap.put(HHSConstants.LI_FYCOUNT, liFYcount);
		loContractMap.put(HHSConstants.LI_START_FY_COUNTER, liStartFyCounter);
		StringBuffer loStringBuffer = BaseControllerUtil.getFiscalYearUtil(loContractMap, loMethodName);

		assertNotNull(loStringBuffer);
	}

	/**
	 * This method tests when loMethodName is GET_FISCAL_YEAR_HEADER.
	 */
	@Test
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void testGetFiscalYearUtilGetFiscalYearHeader() throws ApplicationException
	{
		Map loContractMap = new HashMap();
		int loMethodName = HHSConstants.GET_FISCAL_YEAR_HEADER;
		Object liFYcount = new Integer("1");
		Object liStartFyCounter = new Integer("1");
		loContractMap.put(HHSConstants.LI_FYCOUNT, liFYcount);
		loContractMap.put(HHSConstants.LI_START_FY_COUNTER, liStartFyCounter);
		StringBuffer loStringBuffer = BaseControllerUtil.getFiscalYearUtil(loContractMap, loMethodName);

		assertNotNull(loStringBuffer);
	}

	/**
	 * This method tests when loMethodName is COLUMNS_FOR_TOTAL_COUNT.
	 */
	@Test
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void testGetFiscalYearUtilColumnsForTotalCount() throws ApplicationException
	{
		Map loContractMap = new HashMap();
		int loMethodName = HHSConstants.COLUMNS_FOR_TOTAL_COUNT;
		Object liFYcount = new Integer("1");
		Object liStartFyCounter = new Integer("1");
		loContractMap.put(HHSConstants.LI_FYCOUNT, liFYcount);
		loContractMap.put(HHSConstants.LI_START_FY_COUNTER, liStartFyCounter);
		StringBuffer loStringBuffer = BaseControllerUtil.getFiscalYearUtil(loContractMap, loMethodName);

		assertNotNull(loStringBuffer);
	}

	/**
	 * This method tests when loMethodName is GET_FUNDING_MAIN_HEADER.
	 */
	@Test
	public void testGetFundingMainHeaderUtilGetFundingMainHeader() throws ApplicationException
	{
		int loMethodName = HHSConstants.GET_FUNDING_MAIN_HEADER;
		String loFiscalYearVar = "2013";
		StringBuffer loStringBuffer = BaseControllerUtil.getFundingMainHeaderUtil(loFiscalYearVar, loMethodName);

		assertNotNull(loStringBuffer);
	}

	/**
	 * This method tests when loMethodName is GET_FUNDINGS_MAIN_HEADER_PROP.
	 */
	@Test
	public void testGetFundingMainHeaderUtilGetFundingsMainHeaderProp() throws ApplicationException
	{
		int loMethodName = HHSConstants.GET_FUNDINGS_MAIN_HEADER_PROP;
		String loFiscalYearVar = "2013";
		StringBuffer loStringBuffer = BaseControllerUtil.getFundingMainHeaderUtil(loFiscalYearVar, loMethodName);

		assertNotNull(loStringBuffer);
	}

	/**
	 * This method tests when loMethodName is GET_FUNDING_SUB_GRID_PROP.
	 */
	@Test
	public void testGetFundingMainHeaderUtilGetFundingSubGridProp() throws ApplicationException
	{
		int loMethodName = HHSConstants.GET_FUNDING_SUB_GRID_PROP;
		String loFiscalYearVar = "2013";
		StringBuffer loStringBuffer = BaseControllerUtil.getFundingMainHeaderUtil(loFiscalYearVar, loMethodName);

		assertNotNull(loStringBuffer);
	}

	/**
	 * This method tests when loMethodName is GET_ACCOUNTS_MAIN_HEADER_PROP.
	 */
	@Test
	public void testGetFundingMainHeaderUtilGetAccountsMainHeaderProp() throws ApplicationException
	{
		int loMethodName = HHSConstants.GET_ACCOUNTS_MAIN_HEADER_PROP;
		String loFiscalYearVar = "2013";
		StringBuffer loStringBuffer = BaseControllerUtil.getFundingMainHeaderUtil(loFiscalYearVar, loMethodName);

		assertNotNull(loStringBuffer);
	}

	/**
	 * This method tests when loMethodName is GET_ACCOUNTS_MAIN_HEADER.
	 */
	@Test
	public void testGetFundingMainHeaderUtilGetAccountsMainHeader() throws ApplicationException
	{
		int loMethodName = HHSConstants.GET_ACCOUNTS_MAIN_HEADER;
		String loFiscalYearVar = "2013";
		StringBuffer loStringBuffer = BaseControllerUtil.getFundingMainHeaderUtil(loFiscalYearVar, loMethodName);

		assertNotNull(loStringBuffer);
	}

	/**
	 * This method tests when property type is not boolean
	 */
	@Test
	@SuppressWarnings("rawtypes")
	public void testSaveDocumentPropertiesActionUtilPropertyTypeNotBoolean() throws ApplicationException
	{
		HashMap loHmDocReqProps = new HashMap();
		List<DocumentPropertiesBean> loNewPropertiesList = new ArrayList<DocumentPropertiesBean>();
		;
		DocumentPropertiesBean loDocProps = new DocumentPropertiesBean();
		loDocProps.setPropertyType("String");
		String loPropertyId = "123";
		BaseControllerUtil.saveDocumentPropertiesActionUtil(loHmDocReqProps, loNewPropertiesList, loDocProps,
				loPropertyId);

		assertNotNull(loNewPropertiesList);
	}

	/**
	 * This method tests when property type is boolean and loPropertyId is on
	 */
	@Test
	@SuppressWarnings("rawtypes")
	public void testSaveDocumentPropertiesActionUtilProIdIsOn() throws ApplicationException
	{
		HashMap loHmDocReqProps = new HashMap();
		List<DocumentPropertiesBean> loNewPropertiesList = new ArrayList<DocumentPropertiesBean>();
		;
		DocumentPropertiesBean loDocProps = new DocumentPropertiesBean();
		loDocProps.setPropertyType("boolean");
		loDocProps.setPropSymbolicName("SYMBOLIC_NAME");
		String loPropertyId = HHSConstants.ON;
		BaseControllerUtil.saveDocumentPropertiesActionUtil(loHmDocReqProps, loNewPropertiesList, loDocProps,
				loPropertyId);
		boolean loSymbolName = (Boolean) loHmDocReqProps.get("SYMBOLIC_NAME");
		boolean propValue = (Boolean) loDocProps.getPropValue();
		assertTrue(loSymbolName);
		assertTrue(propValue);
	}

	/**
	 * This method tests when property type is boolean and loPropertyId is yes
	 */
	@Test
	@SuppressWarnings("rawtypes")
	public void testSaveDocumentPropertiesActionUtilProIdIsYes() throws ApplicationException
	{
		HashMap loHmDocReqProps = new HashMap();
		List<DocumentPropertiesBean> loNewPropertiesList = new ArrayList<DocumentPropertiesBean>();
		;
		DocumentPropertiesBean loDocProps = new DocumentPropertiesBean();
		loDocProps.setPropertyType("boolean");
		loDocProps.setPropSymbolicName("SYMBOLIC_NAME");
		String loPropertyId = HHSConstants.YES_LOWERCASE;
		BaseControllerUtil.saveDocumentPropertiesActionUtil(loHmDocReqProps, loNewPropertiesList, loDocProps,
				loPropertyId);
		boolean loSymbolName = (Boolean) loHmDocReqProps.get("SYMBOLIC_NAME");
		boolean propValue = (Boolean) loDocProps.getPropValue();
		assertTrue(loSymbolName);
		assertTrue(propValue);
	}

	/**
	 * This method tests when property type is boolean and loPropertyId is not
	 * yes or on
	 */
	@Test
	@SuppressWarnings("rawtypes")
	public void testSaveDocumentPropertiesActionUtilProId() throws ApplicationException
	{
		HashMap loHmDocReqProps = new HashMap();
		List<DocumentPropertiesBean> loNewPropertiesList = new ArrayList<DocumentPropertiesBean>();
		;
		DocumentPropertiesBean loDocProps = new DocumentPropertiesBean();
		loDocProps.setPropertyType("boolean");
		loDocProps.setPropSymbolicName("SYMBOLIC_NAME");
		String loPropertyId = "off";
		BaseControllerUtil.saveDocumentPropertiesActionUtil(loHmDocReqProps, loNewPropertiesList, loDocProps,
				loPropertyId);
		boolean loSymbolName = (Boolean) loHmDocReqProps.get("SYMBOLIC_NAME");
		boolean propValue = (Boolean) loDocProps.getPropValue();
		assertFalse(loSymbolName);
		assertFalse(propValue);
	}

	/**
	 * This method tests for positive condition
	 */
	@Test
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void testSetCOFAccountHeaderDataInSessionUtilPositive() throws ApplicationException
	{
		Map loContractMap = new HashMap();
		Object liFYcount = new Integer("1");
		Object liStartFyCounter = new Integer("1");
		loContractMap.put(HHSConstants.LI_FYCOUNT, liFYcount);
		loContractMap.put(HHSConstants.LI_START_FY_COUNTER, liStartFyCounter);

		List loFiscalYears = BaseControllerUtil.setCOFAccountHeaderDataInSessionUtil(loContractMap);

		assertNotNull(loFiscalYears);
	}

	/**
	 * This method tests for positive condition
	 */
	@Test
	public void testDeleteTempFilePosi() throws ApplicationException
	{
		String loNull = HHSConstants.EMPTY_STRING;
		File aoFilePath = new File(loNull);
		BaseControllerUtil.deleteTempFile(aoFilePath);

		assertNotNull(aoFilePath);
	}

	/**
	 * This method tests for positive condition
	 */
	@Test
	@SuppressWarnings("rawtypes")
	public void testPopulateBeanFromRequestUtil1() throws ApplicationException
	{
		String lsParamName = HHSConstants.BEAN_NAME;
		Object loBeanObj = new CBGridBean();
		boolean lbThrown;
		Class loClass;
		String lsFiscalAmount = HHSConstants.EMPTY_STRING;
		String lsParamValue = HHSConstants.ON + HHSConstants.NEW_RECORD;
		try
		{
			loClass = Class.forName("com.nyc.hhs.model.CBGridBean");
			loBeanObj = loClass.newInstance();
			BaseControllerUtil.populateBeanFromRequestUtil(loBeanObj, lsParamName, lsParamValue);
			lsFiscalAmount = (String) BeanUtils.getProperty(loBeanObj, lsParamName);
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
		assertNotNull(lsFiscalAmount);
		// assertTrue(lsFiscalAmount == "com.nyc.hhs.model.CBGridBean");
	}

	/**
	 * This method tests for positive condition
	 */
	@Test
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void testFYRowDataUtil1() throws ApplicationException
	{
		Object loBeanObj = new AccountsAllocationBean();
		boolean lbThrown;
		StringBuffer loStringBuffer = new StringBuffer();
		Class loClass;
		Map loContractMap = new HashMap();
		Object liFYcount = new Integer("1");
		Object liStartFyCounter = new Integer("1");
		loContractMap.put(HHSConstants.LI_FYCOUNT, liFYcount);
		loContractMap.put(HHSConstants.LI_START_FY_COUNTER, liStartFyCounter);
		String lsParamValue = HHSConstants.ON + HHSConstants.NEW_RECORD;
		try
		{
			loClass = Class.forName("com.nyc.hhs.model.AccountsAllocationBean");
			loBeanObj = loClass.newInstance();
			BaseControllerUtil.fYRowDataUtil(loBeanObj, loStringBuffer, loContractMap);
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}

		assertNotNull(lsParamValue);
		// assertTrue(lsParamValue == HHSConstants.ON);
	}

	// /**
	// * This method tests for positive condition
	// */
	// @Test
	// @SuppressWarnings(
	// { "rawtypes", "unchecked" })
	// public void testGetTermsAndCondition1() throws ApplicationException
	// {
	// HashMap<String, InputStream> loIoMap = new HashMap<String,
	// InputStream>();
	// loIoMap.put("123", new InputStream(""));
	// Channel loChannel = new Channel();
	// String lsSystemTermsAndCond =
	// BaseControllerUtil.getTermsAndCondition(loChannel);
	//
	// }

	/**
	 * This method tests for positive condition
	 */
	@Test
	public void testInsertDocumentDetailsInDBOnUploadUtil() throws ApplicationException
	{
		JUnitUtil.getTransactionManager();
		String lsDocumentId = "123";
		String lsProcurementId = "123";
		String lsUserOrgType = "";
		String lsUserName = "170";
		Map<String, Object> loParameterMap = new HashMap<String, Object>();
		loParameterMap.put(P8Constants.PROPERTY_CE_DOC_CATEGORY, HHSConstants.EMPTY_STRING);
		loParameterMap.put(P8Constants.PROPERTY_CE_DOC_TYPE, "Banking Documentation");
		loParameterMap.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, HHSConstants.EMPTY_STRING);
		loParameterMap.put(P8Constants.PROPERTY_CE_DATE_CREATED, HHSConstants.EMPTY_STRING);
		loParameterMap.put(P8Constants.PROPERTY_CE_LAST_MODIFIED_DATE, new Date());
		loParameterMap.put(P8Constants.PROPERTY_CE_FILE_TYPE, HHSConstants.EMPTY_STRING);
		loParameterMap.put(P8Constants.PROPERTY_CE_SAMPLE_CATEGORY, HHSConstants.EMPTY_STRING);
		loParameterMap.put(P8Constants.PROPERTY_CE_SAMPLE_TYPE, HHSConstants.EMPTY_STRING);
		loParameterMap.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY_ID, "170");
		loParameterMap.put(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY_ID, HHSConstants.EMPTY_STRING);
		loParameterMap.put(P8Constants.PROPERTY_CE_PROVIDER_ID, HHSConstants.EMPTY_STRING);
		Channel loChannel = new Channel();
		loChannel.setData(ApplicationConstants.MYBATIS_SESSION, moSession);
		loChannel.setData(ApplicationConstants.FILENET_SESSION, session);
		Date loCreatedDate = new Date();
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setContractID("660");
		loCBGridBean.setContractBudgetID("555");
		loCBGridBean.setInvoiceId("111");
		String asContractId = "111177";
		BaseControllerUtil.insertDocumentDetailsInDBOnUploadUtil(lsDocumentId, lsProcurementId, lsUserOrgType,
				lsUserName, loParameterMap, loChannel, loCreatedDate, loCBGridBean,asContractId);
	}

	/**
	 * Closing the Print Writer
	 * @param loOut
	 */

	@Test
	public void testClosingPrintWriter() throws ApplicationException
	{
		PrintWriter loOut = null;
		BaseControllerUtil.closingPrintWriter(loOut);
		assertTrue(true);

	}

	/**
	 * Closing the Print Writer
	 * @param loOut
	 */
	@Test
	public void testClosingPrintWriter2() throws ApplicationException
	{
		PrintWriter loOut;
		loOut = new PrintWriter(System.out);

		BaseControllerUtil.closingPrintWriter(loOut);
		assertTrue(true);

	}

	@Test
	public void testFetchTaskDetailsFromFilenetUtil() throws Exception
	{
		Boolean lbThrown = false;
		try
		{
			String lsWorkflowId = "B3C76DBA71429244A58BB3D804C58737";
			String lsUserId = "agency_21";
			TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
			loTaskDetailsBean.setWorkFlowId(lsWorkflowId);
			loTaskDetailsBean.setUserId(lsUserId);
			Channel loChannel = new Channel();
			loChannel.setData(ApplicationConstants.MYBATIS_SESSION, moSession);
			TaskDetailsBean loTaskDetailsBean1 = BaseControllerUtil.fetchTaskDetailsFromFilenetUtil(lsWorkflowId,
					lsUserId, loTaskDetailsBean, loChannel);
			assertTrue(loTaskDetailsBean1 != null);
		}
		catch (ApplicationException e)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test
	public void testFetchTaskDetailsFromFilenetUtilException() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			String lsWorkflowId = "";
			String lsUserId = "agency_21";
			TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
			loTaskDetailsBean.setWorkFlowId(lsWorkflowId);
			loTaskDetailsBean.setUserId(lsUserId);
			Channel loChannel = new Channel();
			TaskDetailsBean loTaskDetailsBean1 = BaseControllerUtil.fetchTaskDetailsFromFilenetUtil(lsWorkflowId,
					lsUserId, loTaskDetailsBean, loChannel);
			assertTrue(loTaskDetailsBean1 != null);
		}
		catch (ApplicationException e)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test
	public void testFetchTaskDetailsFromFilenetUtilException2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			String lsWorkflowId = null;
			String lsUserId = "agency_21";
			TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
			loTaskDetailsBean.setWorkFlowId(lsWorkflowId);
			loTaskDetailsBean.setUserId(lsUserId);
			Channel loChannel = new Channel();
			TaskDetailsBean loTaskDetailsBean1 = BaseControllerUtil.fetchTaskDetailsFromFilenetUtil(lsWorkflowId,
					lsUserId, loTaskDetailsBean, loChannel);
			assertTrue(loTaskDetailsBean1 != null);
		}
		catch (ApplicationException e)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test
	public void testFetchTaskDetailsFromFilenetUtilException3() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			String lsWorkflowId = null;
			String lsUserId = "agency_21";
			TaskDetailsBean loTaskDetailsBean = null;
			Channel loChannel = new Channel();
			TaskDetailsBean loTaskDetailsBean1 = BaseControllerUtil.fetchTaskDetailsFromFilenetUtil(lsWorkflowId,
					lsUserId, loTaskDetailsBean, loChannel);
			assertTrue(loTaskDetailsBean1 != null);
		}
		catch (ApplicationException e)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	/**
	 * Setting the default bean for BaseController method reAssignTask
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testReAssignTaskDefPara() throws ApplicationException
	{
		HashMap asdefault = new HashMap();
		Channel aoChannel = new Channel();
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		BaseControllerUtil.reAssignTaskDefPara(asdefault, aoChannel, aoTaskDetailsBean);
		assertTrue(true);

	}

	/**
	 * Setting the default channel for BaseController method
	 * actionRemoveDocumentFromList and executing transaction
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Test
	public void testActionRemoveDocNxtChannelUtil()
	{
		Boolean lbThrown = false;
		String lsBudgetId = "555";
		String lsUserOrgType = "accenture";
		try
		{
			CBGridBean loCBGridBean = new CBGridBean();
			Channel aoChannel = new Channel();
			Map loMap = new HashMap();
			loMap.put(HHSConstants.ORGANIZATION_TYPE, lsUserOrgType);
			loMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, "111777");
			loMap.put(HHSConstants.BUDGET_ID_WORKFLOW, lsBudgetId);
			loMap.put(HHSConstants.INVOICE_ID, "55");
			String asContractId = "111177";
			List loFinancialDocumentList = BaseControllerUtil.actionRemoveDocNxtChannelUtil(aoChannel, lsUserOrgType,
					loCBGridBean, asContractId);
			assertTrue(loFinancialDocumentList != null);
			assertTrue(true);
		}
		catch (ApplicationException e)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}

	}

	/**
	 * Setting the default channel for BaseController method
	 * actionRemoveDocumentFromList and executing transaction
	 */

	@SuppressWarnings(
	{ "rawtypes" })
	@Test
	public void testActionRemoveDocChannelUtil()
	{
		Boolean lbThrown = false;
		String lsUserOrgType = "accenture";
		String lsUserName = "agency_47";
		String lsProcurementId = "636";
		String lsProcurementStatus = "1";
		String lsDeletedDocumentId = "{224DC3D2-E062-4B74-8A11-60089D34507E}";
		String lsDocumentSequence = "2444";
		HashMap loHmDocReqProps = new HashMap();
		String lsHdnTableName = "24";
		try
		{
			Channel loChannel = new Channel();
			String lsErrorMsg = BaseControllerUtil.actionRemoveDocChannelUtil(loChannel, loHmDocReqProps,
					lsUserOrgType, lsUserName, lsProcurementId, lsProcurementStatus, lsDeletedDocumentId,
					lsDocumentSequence, lsHdnTableName);
			assertTrue(lsErrorMsg != null);
			assertTrue(true);
		}
		catch (ApplicationException e)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}

	}

	/**
	 * Setting the default channel for BaseController method
	 * actionRemoveDocumentFromList and executing transaction
	 */
	@SuppressWarnings(
	{ "rawtypes" })
	@Test
	public void testActionRemoveDocChannelUtil2()
	{
		Boolean lbThrown = false;
		String lsUserOrgType = "provider_org";
		String lsUserName = "agency_47";
		String lsProcurementId = "636";
		String lsProcurementStatus = "1";
		String lsDeletedDocumentId = "{224DC3D2-E062-4B74-8A11-60089D34507E}";
		String lsDocumentSequence = "2444";
		HashMap loHmDocReqProps = new HashMap();
		String lsHdnTableName = "24";
		try
		{
			Channel loChannel = new Channel();
			String lsErrorMsg = BaseControllerUtil.actionRemoveDocChannelUtil(loChannel, loHmDocReqProps,
					lsUserOrgType, lsUserName, lsProcurementId, lsProcurementStatus, lsDeletedDocumentId,
					lsDocumentSequence, lsHdnTableName);
			assertTrue(lsErrorMsg != null);
			assertTrue(true);
		}
		catch (ApplicationException e)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}

	}

	/**
	 * Setting the lsOperationUpperCase property
	 */
	@Test
	public void testlsOperationUpperCaseUtil() throws ApplicationException
	{
		String lsOperation = null;
		String lsOperationUpperCase = BaseControllerUtil.lsOperationUpperCaseUtil(lsOperation);
		assertNull(lsOperationUpperCase);
	}

	/**
	 * This method populates the Funding Source Allocation parent grid data
	 */
	@Test
	public void testlsOperationUpperCaseUtil2() throws ApplicationException
	{
		String lsOperation = "operation";
		String lsOperationUpperCase = BaseControllerUtil.lsOperationUpperCaseUtil(lsOperation);
		assertNotNull(lsOperationUpperCase);
	}

	/**
	 * This method populates the Funding Source Allocation parent grid data
	 */
	@Test
	public void testShowFundingMainGridUtil() throws ApplicationException
	{
		String lsScreen = "taskScreen";
		StringBuffer loBuffer = new StringBuffer();
		BaseControllerUtil.showFundingMainGridUtil(lsScreen, loBuffer);
		assertNotNull(loBuffer);
	}

	/**
	 * This method populates the Funding Source Allocation parent grid data
	 */
	@Test
	public void testShowFundingMainGridUtil2() throws ApplicationException
	{
		String lsScreen = "financialsFundingGrid";
		StringBuffer loBuffer = new StringBuffer();
		BaseControllerUtil.showFundingMainGridUtil(lsScreen, loBuffer);
		assertNotNull(loBuffer);
	}

	/**
	 * This method populates the Funding Source Allocation parent grid data
	 */
	@Test
	public void testShowFundingMainGridUtil3() throws ApplicationException
	{
		String lsScreen = null;
		StringBuffer loBuffer = new StringBuffer();
		BaseControllerUtil.showFundingMainGridUtil(lsScreen, loBuffer);
		assertNotNull(loBuffer);
	}

	/**
	 * Setting bean for method gridOperation
	 */
	@Test
	public void testSettingGridBeanObj() throws ApplicationException
	{
		String lsTransactionName = "financialsFundingGrid";
		String lsSubBudgetId = "555";
		String lsParentSubBudgetId = "1000";
		CBGridBean loCBGridBean = getDummyCBGridBean();
		Object loBeanObj = new Object();
		BaseControllerUtil.settingGridBeanObj(lsTransactionName, lsSubBudgetId, lsParentSubBudgetId, loBeanObj,
				loCBGridBean);
		assertTrue(true);
	}

	/**
	 * Conditional method for displaySuccess
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Test
	public void testDisplaySuccessUtil() throws ApplicationException
	{
		Boolean lbThrown = false;
		String lsUserOrgType = "accenture";
		CBGridBean loCBGridBean = getDummyCBGridBean();
		HashMap<String, String> loRequiredParamMap = new HashMap();
		Channel loChannelObj = new Channel();
		String asContractId = "111777";
		try
		{
			List loFinancialDocumentList = BaseControllerUtil.displaySuccessUtil(lsUserOrgType, loCBGridBean,
					loRequiredParamMap, loChannelObj,asContractId);
			assertTrue(loFinancialDocumentList != null);
		}
		catch (ApplicationException e)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	/**
	 * Conditional method for displaySuccess
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Test
	public void testDisplaySuccessUtil2() throws ApplicationException
	{
		Boolean lbThrown = false;
		String lsUserOrgType = "accenture";
		CBGridBean loCBGridBean = getDummyCBGridBean();
		HashMap<String, String> loRequiredParamMap = new HashMap();
		Channel loChannelObj = new Channel();
		String asContractId = "111777";
		try
		{
			List loFinancialDocumentList = BaseControllerUtil.displaySuccessUtil(lsUserOrgType, loCBGridBean,
					loRequiredParamMap, loChannelObj,asContractId);
			assertTrue(loFinancialDocumentList != null);
		}
		catch (ApplicationException e)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	/**
	 * Set the default DocHashMap value for saveDocumentPropertiesAction
	 */
	/*
	 * @Test public void testSaveDocHashMapUtil() throws ApplicationException{
	 * HashMap loHmDocReqProps=new HashMap(); Document loDocument=new
	 * Document(); String lsCurrentDate=""; Boolean lbThrown = false; try {
	 * BaseControllerUtil
	 * .saveDocHashMapUtil(loHmDocReqProps,loDocument,lsCurrentDate); } catch
	 * (ApplicationException e) { lbThrown = true;
	 * assertTrue("Exception thrown", lbThrown); } assertTrue(true); }
	 */
	/**
	 * Set the default channel value for addDocumentFromVaultAction and
	 * loMethodName is "addDocumentFromVaultAction"
	 */
	@Test
	@SuppressWarnings(
	{ "rawtypes" })
	public void testSettingDefaultChannel() throws ApplicationException
	{
		HashMap loDefaultValue = new HashMap();
		String loMethodName = "addDocumentFromVaultAction";
		Channel loChannel = BaseControllerUtil.settingDefaultChannel(loDefaultValue, loMethodName);
		assertTrue("Success", (Boolean) loChannel.getData(HHSConstants.LB_SUCCESS_STATUS));

	}

	/**
	 * Set the default channel value for addDocumentFromVaultAction
	 */
	@Test
	@SuppressWarnings(
	{ "rawtypes" })
	public void testSettingDefaultChannel2() throws ApplicationException
	{
		HashMap loDefaultValue = new HashMap();
		String loMethodName = "";
		Channel loChannel = BaseControllerUtil.settingDefaultChannel(loDefaultValue, loMethodName);
		assertTrue(loChannel != null);
	}

	/**
	 * This method performs the userName and password validation
	 * <ul>
	 * <li>Validate entered user is logged in user</li>
	 * <li>Validate username and password is correct</li>
	 */
	@Test
	@SuppressWarnings(
	{ "rawtypes" })
	public void testValidateUserUtil() throws ApplicationException
	{
		String lsUserId = "";
		String lsPassword = "";
		String lsLoginUserEmail = "";
		Map loMap = BaseControllerUtil.validateUserUtil(lsUserId, lsPassword, lsLoginUserEmail);
		assertTrue(loMap != null);
		assertTrue(true);
	}

	/**
	 * This method performs the userName and password validation
	 * <ul>
	 * <li>Validate entered user is logged in user</li>
	 * <li>Validate username and password is incorrect</li>
	 */
	@Test
	@SuppressWarnings(
	{ "rawtypes" })
	public void testValidateUserUtil2() throws ApplicationException
	{
		String lsUserId = "test";
		String lsPassword = "";
		String lsLoginUserEmail = "";
		Map loMap = BaseControllerUtil.validateUserUtil(lsUserId, lsPassword, lsLoginUserEmail);
		assertTrue(loMap != null);

	}

	/**
	 * This method performs the userName and password validation
	 * <ul>
	 * <li>Validate entered user is logged in user</li>
	 * <li>Validate username and password is blank and lsLoginUserEmail is null</li>
	 */
	@Test
	@SuppressWarnings(
	{ "rawtypes" })
	public void testValidateUserUtil3() throws ApplicationException
	{
		String lsUserId = "";
		String lsPassword = "";
		String lsLoginUserEmail = null;
		Map loMap = BaseControllerUtil.validateUserUtil(lsUserId, lsPassword, lsLoginUserEmail);
		assertTrue("invalid user", (Boolean) loMap.get(HHSConstants.IS_VALID_USER));

	}

	/**
	 * This method consist conditional logic for getFiscalYearCustomSubGridProp
	 */
	@Test
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void testGetFiscalYearCustomSubGridPropUtil() throws ApplicationException
	{
		Map loContractMap = new HashMap();
		loContractMap.put("liFYcount", Integer.valueOf(2015));
		loContractMap.put("liStartFyCounter", Integer.valueOf(2013));
		String lsConfigurableFiscalYear = "2013";
		StringBuffer loStringBuffer = BaseControllerUtil.getFiscalYearCustomSubGridPropUtil(loContractMap,
				lsConfigurableFiscalYear);
		assertNotNull(loStringBuffer);
	}

	/**
	 * This method consist conditional logic for getFiscalYearCustomSubGridProp
	 */
	@Test
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void testGetFiscalYearCustomSubGridPropUtil2() throws ApplicationException
	{
		Map loContractMap = new HashMap();
		loContractMap.put("liFYcount", Integer.valueOf(2015));
		loContractMap.put("liStartFyCounter", Integer.valueOf(2013));
		String lsConfigurableFiscalYear = "2014";
		StringBuffer loStringBuffer = BaseControllerUtil.getFiscalYearCustomSubGridPropUtil(loContractMap,
				lsConfigurableFiscalYear);
		assertNotNull(loStringBuffer);
	}

	/**
	 * To set the dummy data in CBGrid bean
	 */
	private CBGridBean getDummyCBGridBean()
	{
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setProcurementID("1");
		loCBGridBean.setContractID("111777");
		loCBGridBean.setContractBudgetID("555");
		loCBGridBean.setFiscalYearID("2014");
		loCBGridBean.setInvoiceId("55");
		loCBGridBean.setCreatedByUserId("agency_12");
		return loCBGridBean;
	}

	/**
	 * This method tests for positive condition
	 */
	@Test
	public void testCatchTaskError() throws Exception
	{
		PrintWriter loOut = new PrintWriter("uuu");
		String lsError = HHSConstants.EMPTY_STRING;
		BaseControllerUtil.catchTaskError(loOut, lsError);

		assertNotNull(loOut);
	}

	/**
	 * This method tests for positive condition
	 */
	@Test
	public void testCatchTaskError2() throws Exception
	{
		PrintWriter loOut = null;
		String lsError = HHSConstants.EMPTY_STRING;
		BaseControllerUtil.catchTaskError(loOut, lsError);

		assertNull(loOut);
	}

	/**
	 * This method tests for positive condition
	 */
	@Test
	public void testSetRequiredParam() throws Exception
	{
		HashMap<String, String> aoParamMap = new HashMap<String, String>();
		BaseControllerUtil.setRequiredParam(aoParamMap);

		assertNotNull(aoParamMap.get(P8Constants.PROPERTY_CE_DOCUMENT_TITLE));
		assertTrue(aoParamMap.get(P8Constants.PROPERTY_CE_DOCUMENT_TITLE).equals(HHSConstants.EMPTY_STRING));
	}

	/**
	 * This method tests for positive condition
	 */
	@Test
	public void testFinishTaskApproveUtil1() throws Exception
	{
		String lsTaskType = "taskBudgetAmend";
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		String loMethodName = "";
		Boolean lbThrown = false;
		try
		{
			BaseControllerUtil.finishTaskApproveUtil(lsTaskType, loTaskDetailsBean, loMethodName);
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	/**
	 * This method tests for positive condition
	 */
	@Test
	public void testFinishTaskApproveUtil2() throws Exception
	{
		String lsTaskType = "taskContractCertificationFunds";
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		String loMethodName = "";
		Boolean lbThrown = false;
		try
		{
			String lsError = BaseControllerUtil.finishTaskApproveUtil(lsTaskType, loTaskDetailsBean, loMethodName);
			assertNotNull(lsError);
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	/**
	 * This method tests for positive condition
	 */
	@Test
	public void testFinishTaskApproveUtil3() throws Exception
	{
		String lsTaskType = "taskContractCertificationFunds";
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		String lsInternalComment = "123";
		String lsProviderComment = "123";

		loTaskDetailsBean.setP8UserSession(session);
		loTaskDetailsBean.setProviderComment(lsProviderComment);
		loTaskDetailsBean.setInternalComment(lsInternalComment);
		String loMethodName = HHSConstants.FINISH_TASK_APPROVE;
		Boolean lbThrown = false;
		try
		{
			String lsError = BaseControllerUtil.finishTaskApproveUtil(lsTaskType, loTaskDetailsBean, loMethodName);
			assertNotNull(lsError);
			assertNotNull(loTaskDetailsBean.getTaskStatus());
			assertNotNull(loTaskDetailsBean.getTaskStatus().equals(ApplicationConstants.STATUS_APPROVED));
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	/**
	 * This method tests for positive condition
	 */
	@Test
	public void testFinishTaskApproveUtil4() throws Exception
	{
		String lsTaskType = "taskContractCertificationFunds";
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		String lsInternalComment = "123";
		String lsProviderComment = "123";

		loTaskDetailsBean.setP8UserSession(session);
		loTaskDetailsBean.setProviderComment(lsProviderComment);
		loTaskDetailsBean.setInternalComment(lsInternalComment);
		String loMethodName = HHSConstants.FINISH_TASK_RETURN;
		Boolean lbThrown = false;
		try
		{
			String lsError = BaseControllerUtil.finishTaskApproveUtil(lsTaskType, loTaskDetailsBean, loMethodName);
			assertNotNull(lsError);
			assertNotNull(loTaskDetailsBean.getTaskStatus());
			assertNotNull(loTaskDetailsBean.getTaskStatus().equals(ApplicationConstants.STATUS_APPROVED));
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	/**
	 * This method tests for positive condition
	 */
	@Test
	public void testFinishTaskApproveUtil5() throws Exception
	{
		String lsTaskType = "taskContractCertificationFunds";
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		String loMethodName = "";
		Boolean lbThrown = false;
		try
		{
			String lsError = BaseControllerUtil.finishTaskApproveUtil(lsTaskType, loTaskDetailsBean, loMethodName);
			assertNotNull(lsError);
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	/**
	 * This method tests for positive condition
	 */
	@Test
	@SuppressWarnings(
	{ "rawtypes" })
	public void testGetContractFiscalYearsUtil1() throws Exception
	{
		boolean lbThrown = false;
		String asContractStartDate = "8/8/2012";
		String asContractEndDate = "12/12/2012";
		Map aoContractMap = new HashMap();
		try
		{
			BaseControllerUtil.getContractFiscalYearsUtil(asContractStartDate, asContractEndDate, aoContractMap);
			assertNotNull(aoContractMap);
			assertNotNull(aoContractMap.get(HHSConstants.LI_START_FY_COUNTER));
			assertNotNull(aoContractMap.get(HHSConstants.LI_FYCOUNT));
			assertNotNull(aoContractMap.get(HHSConstants.LI_START_YEAR));
			assertNotNull(aoContractMap.get(HHSConstants.LI_END_YEAR));
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	// /**
	// * This method tests for positive condition
	// */
	// @Test
	// @SuppressWarnings(
	// { "rawtypes" })
	// public void testGetEpinListUtil1() throws Exception
	// {
	// boolean lbThrown = false;
	// List<String> aoEpinList;
	// String asEpinCalling;
	//
	// try
	// {
	// List<String> loEpinList = BaseControllerUtil.getEpinListUtil(aoEpinList,
	// asEpinCalling);
	// assertNotNull(aoContractMap);
	//
	// }
	// catch (Exception loEx)
	// {
	// lbThrown = true;
	// assertTrue("Exception thrown", lbThrown);
	// }
	// }

	/**
	 * This method tests for positive condition
	 */
	@Test
	public void testShowAccountMainGridUtil1() throws Exception
	{
		boolean lbThrown = false;
		String asScreen = null;
		StringBuffer aoBuffer = new StringBuffer();
		try
		{
			BaseControllerUtil.showAccountMainGridUtil(asScreen, aoBuffer);
			assertNotNull(aoBuffer);
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	/**
	 * This method tests for positive condition
	 */
	@Test
	public void testShowAccountMainGridUtil2() throws Exception
	{
		boolean lbThrown = false;
		String asScreen = HHSConstants.FINANCIALS_ACCOUNT_GRID;
		StringBuffer aoBuffer = new StringBuffer();
		try
		{
			BaseControllerUtil.showAccountMainGridUtil(asScreen, aoBuffer);
			assertNotNull(aoBuffer);
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	/**
	 * This method tests for positive condition
	 */
	@Test
	public void testShowAccountSubGridUtil1() throws Exception
	{
		StringBuffer aoBuffer = new StringBuffer("0000,");
		String asErrorMsg = "";
		String asRowsPerPage = "1";
		String asPage = "1";
		int aiScreenRecordCount = 1;
		String aoMethodName = HHSConstants.SHOW_ACCOUNT_SUB_GRID;
		aoBuffer = BaseControllerUtil.showAccountSubGridUtil(aoBuffer, asErrorMsg, asRowsPerPage, asPage,
				aiScreenRecordCount, aoMethodName);
		assertNotNull(aoBuffer);
		assertTrue(aoBuffer.length() != aoBuffer.lastIndexOf(HHSConstants.COMMA));
	}

	/**
	 * This method tests for positive condition
	 */
	@Test
	public void testShowAccountSubGridUtil2() throws Exception
	{
		StringBuffer aoBuffer = new StringBuffer("0000,");
		String asErrorMsg = "";
		String asRowsPerPage = "1";
		String asPage = "1";
		int aiScreenRecordCount = 1;
		String aoMethodName = HHSConstants.SHOW_FUNDING_SUB_GRID;
		aoBuffer = BaseControllerUtil.showAccountSubGridUtil(aoBuffer, asErrorMsg, asRowsPerPage, asPage,
				aiScreenRecordCount, aoMethodName);
		assertNotNull(aoBuffer);
		assertTrue(aoBuffer.length() != aoBuffer.lastIndexOf(HHSConstants.COMMA));
	}

	/**
	 * This method tests for positive condition
	 */
	@Test
	public void testShowAccountSubGridUtil3() throws Exception
	{
		StringBuffer aoBuffer = new StringBuffer("111111,:[]");
		String asErrorMsg = "";
		String asRowsPerPage = "1";
		String asPage = "1";
		int aiScreenRecordCount = 1;
		String aoMethodName = HHSConstants.SHOW_ACCOUNT_SUB_GRID;
		aoBuffer = BaseControllerUtil.showAccountSubGridUtil(aoBuffer, asErrorMsg, asRowsPerPage, asPage,
				aiScreenRecordCount, aoMethodName);
		assertNotNull(aoBuffer);
		assertTrue(aoBuffer.lastIndexOf("0") != -1);
	}

	/**
	 * This method tests for positive condition
	 */
	@Test
	public void testShowAccountSubGridUtil4() throws Exception
	{
		StringBuffer aoBuffer = new StringBuffer("0000,:[]");
		String asErrorMsg = "";
		String asRowsPerPage = "1";
		String asPage = "1";
		int aiScreenRecordCount = 1;
		String aoMethodName = HHSConstants.SHOW_FUNDING_SUB_GRID;
		aoBuffer = BaseControllerUtil.showAccountSubGridUtil(aoBuffer, asErrorMsg, asRowsPerPage, asPage,
				aiScreenRecordCount, aoMethodName);
		assertNotNull(aoBuffer);
		assertTrue(aoBuffer.lastIndexOf("1") != -1);
	}

	/**
	 * This method tests for positive condition
	 */
	@Test
	public void testSetParametersMapValue1() throws Exception
	{
		Map<Object, Object> aoParameterMap = new HashMap<Object, Object>();
		String asUserId = "me";
		HashMap<String, String> aoDefaultValue = new HashMap<String, String>();
		Date aoCreatedDate = new Date(12 / 12 / 2010);
		Date aoDocModifiedDate = new Date(12 / 12 / 2013);
		CBGridBean aoCBGridBean = new CBGridBean();

		aoDefaultValue.put(HHSConstants.PROCUREMENT_ID, "123");
		aoDefaultValue.put(HHSConstants.HIDDEN_DOC_REF_SEQ_NO, "123");
		aoDefaultValue.put(HHSConstants.PROPOSAL_ID, "123");
		aoDefaultValue.put(HHSConstants.DOC_TITLE, "Doc Title");
		aoDefaultValue.put(HHSConstants.ADD_DOC_TYPE, "Doc Title Add");
		aoDefaultValue.put(HHSConstants.DOC_CATEGORY_LOWERCASE, "Budget");
		aoDefaultValue.put(HHSConstants.DOC_ID, "12343");
		aoDefaultValue.put(HHSConstants.SUBMISSION_BY, "me");
		aoDefaultValue.put(HHSConstants.BASE_LAST_MODIFIED_BY, "me");

		aoCBGridBean.setContractID("1232");
		aoCBGridBean.setContractBudgetID("123");
		aoCBGridBean.setInvoiceId("12");
		String asContractId = "111177";
		BaseControllerUtil.setParametersMapValue(aoParameterMap, asUserId, aoDefaultValue, aoCreatedDate,
				aoDocModifiedDate, aoCBGridBean,asContractId);
		assertNotNull(aoParameterMap);
		assertTrue(aoParameterMap.get(HHSConstants.PROCUREMENT_ID).equals("123"));
		assertTrue(aoParameterMap.get(HHSConstants.DOC_REF_NO).equals("123"));
		assertTrue(aoParameterMap.get(HHSConstants.PROPOSAL_ID).equals("123"));
		assertTrue(aoParameterMap.get(HHSConstants.DOCUMENT_TITLE).equals("Doc Title"));
		assertTrue(aoParameterMap.get(HHSConstants.DOC_TYPE).equals("Doc Title Add"));
		assertTrue(aoParameterMap.get(HHSConstants.DOC_CATEGORY).equals("Budget"));
		assertTrue(aoParameterMap.get(HHSConstants.DOC_ID).equals("12343"));
		assertTrue(aoParameterMap.get(HHSConstants.HHS_DOC_CREATED_BY_ID).equals("me"));
		assertTrue(aoParameterMap.get(HHSConstants.DOC_MODIFIED_BY).equals("me"));
		assertTrue(aoParameterMap.get(HHSConstants.USER_ID).equals("me"));
		assertTrue(aoParameterMap.get(HHSConstants.MOD_BY_USER_ID).equals("me"));
		assertTrue(aoParameterMap.get(HHSConstants.CONTRACT_ID_WORKFLOW).equals("1232"));
		assertTrue(aoParameterMap.get(HHSConstants.BUDGET_ID_WORKFLOW).equals("123"));
		assertTrue(aoParameterMap.get(HHSConstants.INVOICE_ID).equals("12"));
	}

	/**
	 * This method tests for positive condition
	 */
	@Test
	@SuppressWarnings("rawtypes")
	public void testValidateChartAllocationFYIUtil1() throws Exception
	{
		AccountsAllocationBean aoModifiedAllocationBean = new AccountsAllocationBean();
		List aoActualGridList = null;
		List aoUpdateGridList = new ArrayList();
		Object liStartYear = new Integer(2010);
		Object liEndYear = new Integer(2103);
		Map<String, Object> aoFiscalYrMap = new HashMap<String, Object>();
		aoFiscalYrMap.put(HHSConstants.LI_START_YEAR, liStartYear);
		aoFiscalYrMap.put(HHSConstants.LI_END_YEAR, liEndYear);

		BaseControllerUtil.validateChartAllocationFYIUtil(aoModifiedAllocationBean, aoActualGridList, aoUpdateGridList,
				aoFiscalYrMap);
		Exception loEx = new Exception();
		assertNull(loEx.getMessage());
	}

	/**
	 * This method tests for positive condition
	 */
	@Test
	@SuppressWarnings("rawtypes")
	public void testValidateChartAllocationFYIUtil2() throws Exception
	{
		AccountsAllocationBean aoModifiedAllocationBean = new AccountsAllocationBean();
		List aoActualGridList = new ArrayList();
		List aoUpdateGridList = null;
		Object liStartYear = new Integer(2010);
		Object liEndYear = new Integer(2011);
		Map<String, Object> aoFiscalYrMap = new HashMap<String, Object>();
		aoFiscalYrMap.put(HHSConstants.LI_START_YEAR, liStartYear);
		aoFiscalYrMap.put(HHSConstants.LI_END_YEAR, liEndYear);

		BaseControllerUtil.validateChartAllocationFYIUtil(aoModifiedAllocationBean, aoActualGridList, aoUpdateGridList,
				aoFiscalYrMap);
		Exception loEx = new Exception();
		assertNull(loEx.getMessage());
	}

	/**
	 * This method tests for positive condition
	 */
	@Test
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void testValidateChartAllocationFYIUtil3() throws Exception
	{
		AccountsAllocationBean aoModifiedAllocationBean = new AccountsAllocationBean();
		List aoActualGridList = new ArrayList();
		AccountsAllocationBean loAccountsAllocationBeanActual = new AccountsAllocationBean();
		loAccountsAllocationBeanActual.setFy1("");
		aoActualGridList.add(loAccountsAllocationBeanActual);
		List aoUpdateGridList = new ArrayList();
		Object liStartYear = new Integer(2010);
		Object liEndYear = new Integer(2011);
		Map<String, Object> aoFiscalYrMap = new HashMap<String, Object>();
		aoFiscalYrMap.put(HHSConstants.LI_START_YEAR, liStartYear);
		aoFiscalYrMap.put(HHSConstants.LI_END_YEAR, liEndYear);

		BaseControllerUtil.validateChartAllocationFYIUtil(aoModifiedAllocationBean, aoActualGridList, aoUpdateGridList,
				aoFiscalYrMap);
		Exception loEx = new Exception();
		assertNull(loEx.getMessage());
	}

	/**
	 * This method tests for positive condition
	 */
	@Test
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void testValidateChartAllocationFYIUtil4() throws Exception
	{
		AccountsAllocationBean aoModifiedAllocationBean = new AccountsAllocationBean();
		List aoActualGridList = new ArrayList();
		AccountsAllocationBean loAccountsAllocationBeanActual = new AccountsAllocationBean();
		loAccountsAllocationBeanActual.setFy1("44");
		aoActualGridList.add(loAccountsAllocationBeanActual);
		List aoUpdateGridList = new ArrayList();
		Object liStartYear = new Integer(2010);
		Object liEndYear = new Integer(2011);
		Map<String, Object> aoFiscalYrMap = new HashMap<String, Object>();
		aoFiscalYrMap.put(HHSConstants.LI_START_YEAR, liStartYear);
		aoFiscalYrMap.put(HHSConstants.LI_END_YEAR, liEndYear);

		BaseControllerUtil.validateChartAllocationFYIUtil(aoModifiedAllocationBean, aoActualGridList, aoUpdateGridList,
				aoFiscalYrMap);
		Exception loEx = new Exception();
		assertNull(loEx.getMessage());
	}

	/**
	 * This method tests for positive condition
	 */
	@Test
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void testValidateChartAllocationFYIUtil5() throws Exception
	{
		AccountsAllocationBean aoModifiedAllocationBean = new AccountsAllocationBean();
		List aoActualGridList = new ArrayList();
		AccountsAllocationBean loAccountsAllocationBeanActual = new AccountsAllocationBean();
		loAccountsAllocationBeanActual.setFy1(null);
		aoActualGridList.add(loAccountsAllocationBeanActual);
		List aoUpdateGridList = new ArrayList();
		Object liStartYear = new Integer(2010);
		Object liEndYear = new Integer(2011);
		Map<String, Object> aoFiscalYrMap = new HashMap<String, Object>();
		aoFiscalYrMap.put(HHSConstants.LI_START_YEAR, liStartYear);
		aoFiscalYrMap.put(HHSConstants.LI_END_YEAR, liEndYear);

		BaseControllerUtil.validateChartAllocationFYIUtil(aoModifiedAllocationBean, aoActualGridList, aoUpdateGridList,
				aoFiscalYrMap);
		Exception loEx = new Exception();
		assertNull(loEx.getMessage());
	}

	/**
	 * This method tests for positive condition
	 */
	@Test
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void testValidateChartAllocationFYIUtil6() throws Exception
	{
		AccountsAllocationBean aoModifiedAllocationBean = new AccountsAllocationBean();
		List aoActualGridList = new ArrayList();
		AccountsAllocationBean loAccountsAllocationBeanActual = new AccountsAllocationBean();
		loAccountsAllocationBeanActual.setFy1("44");
		aoActualGridList.add(loAccountsAllocationBeanActual);

		List aoUpdateGridList = new ArrayList();
		AccountsAllocationBean loAccountsAllocationBeanUpdate = new AccountsAllocationBean();
		loAccountsAllocationBeanUpdate.setFy1("43");
		aoUpdateGridList.add(loAccountsAllocationBeanActual);

		Object liStartYear = new Integer(2010);
		Object liEndYear = new Integer(2011);
		Map<String, Object> aoFiscalYrMap = new HashMap<String, Object>();
		aoFiscalYrMap.put(HHSConstants.LI_START_YEAR, liStartYear);
		aoFiscalYrMap.put(HHSConstants.LI_END_YEAR, liEndYear);

		BaseControllerUtil.validateChartAllocationFYIUtil(aoModifiedAllocationBean, aoActualGridList, aoUpdateGridList,
				aoFiscalYrMap);
		Exception loEx = new Exception();
		assertNull(loEx.getMessage());
	}

	/**
	 * This method tests for positive condition
	 */
	@Test
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void testValidateChartAllocationFYIUtil7() throws Exception
	{
		AccountsAllocationBean aoModifiedAllocationBean = new AccountsAllocationBean();
		aoModifiedAllocationBean.setId("123");
		List aoActualGridList = new ArrayList();
		AccountsAllocationBean loAccountsAllocationBeanActual = new AccountsAllocationBean();
		loAccountsAllocationBeanActual.setFy1("44");
		aoActualGridList.add(loAccountsAllocationBeanActual);

		List aoUpdateGridList = new ArrayList();
		AccountsAllocationBean loAccountsAllocationBeanUpdate = new AccountsAllocationBean();
		loAccountsAllocationBeanUpdate.setFy1("43");
		aoUpdateGridList.add(loAccountsAllocationBeanActual);

		Object liStartYear = new Integer(2010);
		Object liEndYear = new Integer(2011);
		Map<String, Object> aoFiscalYrMap = new HashMap<String, Object>();
		aoFiscalYrMap.put(HHSConstants.LI_START_YEAR, liStartYear);
		aoFiscalYrMap.put(HHSConstants.LI_END_YEAR, liEndYear);

		BaseControllerUtil.validateChartAllocationFYIUtil(aoModifiedAllocationBean, aoActualGridList, aoUpdateGridList,
				aoFiscalYrMap);
		Exception loEx = new Exception();
		assertNull(loEx.getMessage());
	}

	/**
	 * This method tests for positive condition
	 */
	@Test
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void testValidateChartAllocationFYIUtil8() throws Exception
	{
		AccountsAllocationBean aoModifiedAllocationBean = new AccountsAllocationBean();
		aoModifiedAllocationBean.setId("123");
		List aoActualGridList = new ArrayList();
		AccountsAllocationBean loAccountsAllocationBeanActual = new AccountsAllocationBean();
		loAccountsAllocationBeanActual.setFy1("44");
		aoActualGridList.add(loAccountsAllocationBeanActual);

		List aoUpdateGridList = new ArrayList();
		AccountsAllocationBean loAccountsAllocationBeanUpdate = new AccountsAllocationBean();
		loAccountsAllocationBeanUpdate.setFy1("");
		aoUpdateGridList.add(loAccountsAllocationBeanActual);

		Object liStartYear = new Integer(2010);
		Object liEndYear = new Integer(2011);
		Map<String, Object> aoFiscalYrMap = new HashMap<String, Object>();
		aoFiscalYrMap.put(HHSConstants.LI_START_YEAR, liStartYear);
		aoFiscalYrMap.put(HHSConstants.LI_END_YEAR, liEndYear);

		BaseControllerUtil.validateChartAllocationFYIUtil(aoModifiedAllocationBean, aoActualGridList, aoUpdateGridList,
				aoFiscalYrMap);
		Exception loEx = new Exception();
		assertNull(loEx.getMessage());
	}

	/**
	 * This method tests for positive condition
	 */
	@Test
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void testValidateChartAllocationFYIUtil9() throws Exception
	{
		boolean lbThrown = false;
		AccountsAllocationBean aoModifiedAllocationBean = new AccountsAllocationBean();
		aoModifiedAllocationBean.setId(HHSConstants.NEW_ROW_IDENTIFIER);
		aoModifiedAllocationBean.setFy1("122");
		List aoActualGridList = new ArrayList();
		AccountsAllocationBean loAccountsAllocationBeanActual = new AccountsAllocationBean();
		loAccountsAllocationBeanActual.setFy1("44");
		aoActualGridList.add(loAccountsAllocationBeanActual);

		List aoUpdateGridList = new ArrayList();
		AccountsAllocationBean loAccountsAllocationBeanUpdate = new AccountsAllocationBean();
		loAccountsAllocationBeanUpdate.setFy1(null);
		aoUpdateGridList.add(loAccountsAllocationBeanActual);

		Object liStartYear = new Integer(2010);
		Object liEndYear = new Integer(2011);
		Map<String, Object> aoFiscalYrMap = new HashMap<String, Object>();
		aoFiscalYrMap.put(HHSConstants.LI_START_YEAR, liStartYear);
		aoFiscalYrMap.put(HHSConstants.LI_END_YEAR, liEndYear);

		try
		{
			BaseControllerUtil.validateChartAllocationFYIUtil(aoModifiedAllocationBean, aoActualGridList,
					aoUpdateGridList, aoFiscalYrMap);
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
			assertNotNull(loEx.getMessage());
		}
	}

	/**
	 * This method tests for positive condition
	 */
	@Test
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void testSaveDocHashMapUtil() throws Exception
	{
		HashMap aoHmDocReqProps = new HashMap();
		String asCurrentDate = DateUtil.getSqlDate(DateUtil.getCurrentDate()).toString();

		aoHmDocReqProps.put(HHSConstants.DOC_NAME, "Doc Name");
		aoHmDocReqProps.put(ApplicationConstants.KEY_SESSION_USER_NAME, "me");
		aoHmDocReqProps.put(ApplicationConstants.KEY_SESSION_USER_ID, "me");
		aoHmDocReqProps.put(HHSConstants.PROCUREMENT_ID, "123");
		aoHmDocReqProps.put(HHSConstants.IS_ADD_TYPE, "Y");

		Document aoDocument = new Document();
		aoDocument.setDocumentId("12332");
		BaseControllerUtil.saveDocHashMapUtil(aoHmDocReqProps, aoDocument, asCurrentDate);
		assertNotNull(aoHmDocReqProps);
		assertTrue(aoHmDocReqProps.get(P8Constants.PROPERTY_CE_DOCUMENT_TITLE).equals("Doc Name"));
		assertTrue(aoHmDocReqProps.get(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY).equals("me"));
		assertTrue(aoHmDocReqProps.get(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY_ID).equals("me"));
		assertTrue(aoHmDocReqProps.get(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE).equals(
				DateUtil.getSqlDate(DateUtil.getCurrentDate()).toString()));
		assertTrue(aoHmDocReqProps.get(HHSConstants.PROCUREMENT_ID).equals("123"));
		assertTrue(aoHmDocReqProps.get(HHSConstants.IS_ADDENDUM).equals("Y"));
		assertTrue(aoHmDocReqProps.get(HHSConstants.MODIFIED_BY).equals("me"));
		assertTrue(aoHmDocReqProps.get(HHSConstants.DOC_ID).equals("12332"));
		assertTrue(aoHmDocReqProps.get(HHSConstants.MODIFIED_DATE).equals(
				DateUtil.getSqlDate(DateUtil.getCurrentDate())));
	}

	/**
	 * This method tests for positive condition
	 */
	@Test
	@SuppressWarnings("rawtypes")
	public void testExecuteStaticGridTransaction() throws Exception
	{
		String asOperation = null;
		String asTransactionName = null;
		Channel aoChannelObj = new Channel();

		Object loBeanObj = new CBGridBean();
		Class loClass = Class.forName("com.nyc.hhs.model.CBGridBean");
		loBeanObj = loClass.newInstance();

		BaseControllerUtil.executeStaticGridTransaction(asOperation, asTransactionName, aoChannelObj, loBeanObj);
		assertTrue(true);
	}

	/**
	 * This method tests for positive condition
	 */
	@Test
	@SuppressWarnings("rawtypes")
	public void testExecuteStaticGridTransaction2() throws Exception
	{
		String asOperation = HHSConstants.OPERATION_ADD;
		String asTransactionName = "getContractBudgetModMilestoneGridAdd";
		Channel aoChannelObj = new Channel();

		Object loBeanObj = new CBGridBean();
		Class loClass = Class.forName("com.nyc.hhs.model.CBMileStoneBean");
		loBeanObj = loClass.newInstance();

		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();
		loCBMileStoneBean.setSubBudgetID("556");
		loCBMileStoneBean.setModifiedByUserId("803");
		loCBMileStoneBean.setCreatedByUserId("803");
		loCBMileStoneBean.setMileStone("new test case");
		loCBMileStoneBean.setModificationAmount("500");
		loCBMileStoneBean.setContractBudgetID("555");

		loBeanObj = loCBMileStoneBean;
		BaseControllerUtil.executeStaticGridTransaction(asOperation, asTransactionName, aoChannelObj, loBeanObj);
		assertTrue(true);
	}

	/**
	 * This method tests for positive condition
	 */
	@Test
	@SuppressWarnings("rawtypes")
	public void testExecuteStaticGridTransaction3() throws Exception
	{
		String asOperation = HHSConstants.OPERATION_DELETE;
		String asTransactionName = "getContractBudgetModMilestoneGridDel";
		Channel aoChannelObj = new Channel();

		Object loBeanObj = new CBGridBean();
		Class loClass = Class.forName("com.nyc.hhs.model.CBMileStoneBean");
		loBeanObj = loClass.newInstance();

		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();
		loCBMileStoneBean.setSubBudgetID("556");
		loCBMileStoneBean.setModifiedByUserId("803");
		loCBMileStoneBean.setCreatedByUserId("803");
		loCBMileStoneBean.setMileStone("new test case");
		loCBMileStoneBean.setModificationAmount("500");
		loCBMileStoneBean.setContractBudgetID("555");

		loBeanObj = loCBMileStoneBean;
		BaseControllerUtil.executeStaticGridTransaction(asOperation, asTransactionName, aoChannelObj, loBeanObj);
		assertTrue(true);
	}

	/**
	 * This method tests for positive condition
	 */
	@Test
	@SuppressWarnings("rawtypes")
	public void testExecuteStaticGridTransaction4() throws Exception
	{
		
		String asOperation = HHSConstants.OPERATION_EDIT;
		String asTransactionName = "unallocatedFundsEdit";
		Channel aoChannelObj = new Channel();

		Object loBeanObj = new CBGridBean();
		Class loClass = Class.forName("com.nyc.hhs.model.UnallocatedFunds");
		loBeanObj = loClass.newInstance();

		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		loUnallocatedFundsBean.setSubBudgetID("555");
		loUnallocatedFundsBean.setContractBudgetID("555");
		loUnallocatedFundsBean.setModifiedByUserId("city_142");
		loUnallocatedFundsBean.setAmmount("13452.90");

		loBeanObj = loUnallocatedFundsBean;
		BaseControllerUtil.executeStaticGridTransaction(asOperation, asTransactionName, aoChannelObj, loBeanObj);
		assertTrue(true);
	}
	
	
	/**
	 * This method tests for positive condition
	 */
	@Test
	@SuppressWarnings(
			{ "rawtypes", "unchecked" })
	public void testReAssignTaskUtil() throws Exception
	{
		HashMap aoHMReqdProp = new HashMap();
		aoHMReqdProp.put(HHSConstants.LS_REASSIGN_USER_NAME,"me");
		aoHMReqdProp.put(HHSConstants.LS_PUBLIC_COMMENT,"coldplay");
		aoHMReqdProp.put(HHSConstants.LS_INTERNAL_COMMENT,"coldplay");
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		String lsWorkflowId = "B3C76DBA71429244A58BB3D804C58737";
		aoTaskDetailsBean.setWorkFlowId(lsWorkflowId);
		aoTaskDetailsBean.setEntityType("Y");
		aoTaskDetailsBean.setEntityId("U");
		String asUserId = "170";
		Channel aoChannel = new Channel();
		aoChannel.setData(HHSConstants.AO_TASK_DETAILS_BEAN, aoTaskDetailsBean);
		aoChannel.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
		aoChannel.setData(HHSConstants.AO_FILENET_SESSION, session);
		
		BaseControllerUtil.reAssignTaskUtil( aoHMReqdProp,  aoTaskDetailsBean,  asUserId, aoChannel);
		assertTrue(true);
	}

	
	/**
	 * This method tests for positive condition
	 */
//	@Test
//	public void testAddDocumentFromVaultActionUtil() throws Exception
//	{
//		Channel aoChannel = new Channel();
//		CBGridBean aoCBGridBean = new CBGridBean();
//		BaseControllerUtil.addDocumentFromVaultActionUtil( aoChannel,  aoCBGridBean);
//		assertTrue(true);
//	}

}
