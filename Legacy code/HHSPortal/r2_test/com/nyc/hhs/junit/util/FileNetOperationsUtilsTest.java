package com.nyc.hhs.junit.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.util.CollectionUtils;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.cache.ICacheManager;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.Document;
import com.nyc.hhs.model.DocumentPropertiesBean;
import com.nyc.hhs.model.ProviderBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.FileNetOperationsUtils;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.XMLUtil;

public class FileNetOperationsUtilsTest
{

	private static P8UserSession session = null;

	private static SqlSession moSession = null; // SQL Session
	
	
	

	/**
	 * SQL session created ONCE before the class
	 * 
	 * @throws Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		Boolean lbThrown = false;
		try
		{
			session = getFileNetSession();
			ICacheManager loCacheManager = BaseCacheManagerWeb.getInstance();

			Object loCacheNotificationObject1 = XMLUtil.getDomObj((new FileNetOperationsUtilsTest()).getClass()
					.getResourceAsStream("/com/nyc/hhs/config/ExtendedDocType.xml"));
			loCacheManager.putCacheObject(ApplicationConstants.FILENET_EXTENDED_DOC_TYPE, loCacheNotificationObject1);

			Object loCacheNotificationObject2 = XMLUtil.getDomObj((new FileNetOperationsUtilsTest()).getClass()
					.getResourceAsStream("/com/nyc/hhs/config/DocType.xml"));
			loCacheManager.putCacheObject(ApplicationConstants.FILENETDOCTYPE, loCacheNotificationObject2);

			Object loCacheNotificationObject3 = XMLUtil.getDomObj((new FileNetOperationsUtilsTest()).getClass()
					.getResourceAsStream("/com/nyc/hhs/config/TransactionConfig.xml"));
			loCacheManager.putCacheObject(ApplicationConstants.TRANSACTION_ELEMENT, loCacheNotificationObject3);
			
			loCacheManager.putCacheObject(ApplicationConstants.PROV_LIST, getProviderListForCache());
			
			loCacheManager.putCacheObject(ApplicationConstants.AGENCY_LIST, getAgencyListForCache());

			moSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	/**
	 * SQL session created after the class
	 * 
	 * @throws Exception
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

	/**
	 * This method creates the Filenet session object.
	 * 
	 * @return P8UserSession
	 * @throws ApplicationException
	 */
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

		return loUserSession;
	}
	
	
//	  private static void loadTransactionXml() throws ApplicationException
//      {
// 
//            try
//            {
//                  ICacheManager loCacheManager = BaseCacheManagerWeb.getInstance();
//                  Object loCacheNotificationObject3 = XMLUtil.getDomObj((new FileNetOperationsUtilsTest()).getClass()
//                              .getResourceAsStream("/com/nyc/hhs/config/TransactionConfig.xml"));
//                  loCacheManager.putCacheObject(HHSConstants.TRANSACTION_ELEMENT, loCacheNotificationObject3);
//                  System.out.println(loCacheManager.getCacheObject(HHSConstants.TRANSACTION_ELEMENT));
//            }
//            catch (Exception loEx)
//            {
//                  loEx.printStackTrace();
//                  throw new ApplicationException("Error Occured while loading Transaction Xml",loEx);
//            }
//      
//      }
	
	/**
	 * This method fetches provider list from the DB.
	 * 
	 * @return P8UserSession
	 * @throws ApplicationException
	 */
	public static List<ProviderBean> getProviderListForCache() throws ApplicationException
	{
		List<ProviderBean> loProviderList = new ArrayList<ProviderBean>();
		Channel loChannel = new Channel();
			TransactionManager.executeTransaction(loChannel, "getProviderListForCache_DB");
			List<ProviderBean> loProviderTempList = (List<ProviderBean>) loChannel.getData("providerList");
			if (!CollectionUtils.isEmpty(loProviderTempList))
			{
				Iterator<ProviderBean> loIter = loProviderTempList.iterator();
				while (loIter.hasNext())
				{
					ProviderBean loProviderBean = loIter.next();
					String lsDisplayValue = loProviderBean.getDisplayValue();
					lsDisplayValue = StringEscapeUtils.escapeJavaScript(lsDisplayValue);
					loProviderBean.setDisplayValue(lsDisplayValue);
					loProviderList.add(loProviderBean);
				}
			}
		
		return loProviderList;

	}
	
	/**
	 * This method fetches agency list from the DB.
	 * 
	 * @return P8UserSession
	 * @throws ApplicationException
	 */
	public static TreeSet getAgencyListForCache() throws ApplicationException
	{
		Channel loChannel = new Channel();
		TransactionManager.executeTransaction(loChannel, "getAgencyListForCache_DB");
		List<ProviderBean> loAgencyList = (List) loChannel.getData("agencyList");
		TreeSet loAgencySet = new TreeSet<String>();
		if (!CollectionUtils.isEmpty(loAgencyList))
		{
			Iterator<ProviderBean> loIter = loAgencyList.iterator();
			while (loIter.hasNext())
			{
				ProviderBean loProviderBean = loIter.next();
				String lsDisplayValue = loProviderBean.getDisplayValue();
				lsDisplayValue = StringEscapeUtils.escapeJavaScript(lsDisplayValue);
				loAgencySet.add(loProviderBean.getHiddenValue() + "~" + lsDisplayValue);
			}
		}
		return loAgencySet;

	}
	
	

	/**
	 * This method tests getSharedAgencyProviderList method for data inputs and
	 * an ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Test
	public void testGetSharedAgencyProviderList() throws ApplicationException
	{
		List<ProviderBean> aoProviderList = new ArrayList();

		ProviderBean loproviderBean = new ProviderBean();
		// loproviderBean.se
		aoProviderList.add(loproviderBean);
		TreeSet loresult = FileNetOperationsUtils.getSharedAgencyProviderList(aoProviderList, session, "HHS_AGENCY_ID",
				"provider");
		assertTrue(!loresult.isEmpty());
	}

	/**
	 * This method tests getNYCAgencyListFromDB method for data inputs and an
	 * ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testGetNYCAgencyListFromDB() throws ApplicationException
	{
		TreeSet loresult = FileNetOperationsUtils.getNYCAgencyListFromDB();
		assertTrue(!loresult.isEmpty());
	}

	/**
	 * This method tests getOrgTypeFilter method for data inputs and an
	 * ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testGetOrgTypeFilter() throws ApplicationException
	{
		HashMap loresult = FileNetOperationsUtils.getOrgTypeFilter(new HashMap(), "PROVIDER_ID", "provider_org");
		assertTrue(!loresult.isEmpty());
	}
	
	/**
	 * This method tests getOrgTypeFilter method for data inputs and an
	 * ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testGetOrgTypeFilter1() throws ApplicationException
	{
		HashMap loresult = FileNetOperationsUtils.getOrgTypeFilter(new HashMap(), "PROVIDER_ID", "agency_org");
		assertTrue(!loresult.isEmpty());
	}
	
	/**
	 * This method tests getOrgTypeFilter method for data inputs and an
	 * ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testGetOrgTypeFilter2() throws ApplicationException
	{
		HashMap loresult = FileNetOperationsUtils.getOrgTypeFilter(new HashMap(), "PROVIDER_ID", "city_org");
		assertTrue(!loresult.isEmpty());
	}
	
	/**
	 * This method tests createAndSetFilteredProperties method for data inputs
	 * and an ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testCreateAndSetFilteredProperties() throws ApplicationException
	{
		HashMap loresult = FileNetOperationsUtils.createAndSetFilteredProperties("test", "test", "test", "test",
				"test", "test");
		assertTrue(!loresult.isEmpty());
	}


	/**
	 * This method tests getDocumentProperties method for data inputs and an
	 * ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testGetDocumentProperties() throws ApplicationException
	{
		String asUserOrg = "city_org", asDocType = "Addenda";
		String asDocCategory = "Solicitation";
		List<DocumentPropertiesBean> loList = FileNetOperationsUtils.getDocumentProperties(asDocCategory, asDocType,
				asUserOrg);
		assertTrue(loList.isEmpty());
	}

	/**
	 * This method tests viewDocumentInfo method for data inputs and an
	 * ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testViewDocumentInfo() throws ApplicationException
	{
		Document lodoc = FileNetOperationsUtils.viewDocumentInfo(session, "city_org",
				"{4D53FC89-220B-4F78-9A6D-3F71EB49C1E6}");
		assertTrue(lodoc != null);
	}

	/**
	 * This method tests checkDocExist method for data inputs and an
	 * ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testCheckDocExist() throws ApplicationException
	{
		String lsresult = FileNetOperationsUtils.checkDocExist(session, "provider", "testDoc1", "Addenda","Solicitation", "city_org");
		assertTrue(lsresult == null);
	}

	/**
	 * This method tests validatePeriodCoveredDateChar500 method for data inputs
	 * and an ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Test
	public void testValidatePeriodCoveredDateChar500ForDraftApp() throws ApplicationException
	{
		HashMap aoInsertPropMap = new HashMap();
		aoInsertPropMap.put(ApplicationConstants.PERIOD_COVER_FROM_YEAR, "1990");
		aoInsertPropMap.put(ApplicationConstants.PERIOD_COVER_FROM_MONTH, "jun");
		aoInsertPropMap.put(ApplicationConstants.PERIOD_COVER_TO_MONTH, "may");
		aoInsertPropMap.put(ApplicationConstants.PERIOD_COVER_TO_YEAR, "1995");
		String asNextExpectedDocType = "test";
		double loresult = FileNetOperationsUtils.validatePeriodCoveredDateChar500(aoInsertPropMap,
				asNextExpectedDocType);
		assertTrue(loresult > 0.0);
	}
	
	/**
	 * This method tests validatePeriodCoveredDateChar500 method for data inputs
	 * and an ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Test
	public void testValidatePeriodCoveredDateChar500() throws ApplicationException
	{
		HashMap aoInsertPropMap = new HashMap();
		aoInsertPropMap.put(ApplicationConstants.PERIOD_COVER_FROM_YEAR, null);
		aoInsertPropMap.put(ApplicationConstants.PERIOD_COVER_TO_YEAR, null);
		String asNextExpectedDocType = "test";
		double loresult = FileNetOperationsUtils.validatePeriodCoveredDateChar500(aoInsertPropMap,
				asNextExpectedDocType);
		assertTrue(loresult == 0.0);
	}
	
	/**
	 * This method tests validatePeriodCoveredDateChar500 method for data inputs
	 * and an ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Test
	public void testValidatePeriodCoveredDateChar5001() throws ApplicationException
	{
		HashMap aoInsertPropMap = new HashMap();
		aoInsertPropMap.put(ApplicationConstants.PERIOD_COVER_FROM_YEAR, "");
		aoInsertPropMap.put(ApplicationConstants.PERIOD_COVER_TO_YEAR, "");
		String asNextExpectedDocType = "test";
		double loresult = FileNetOperationsUtils.validatePeriodCoveredDateChar500(aoInsertPropMap,
				asNextExpectedDocType);
		assertTrue(loresult == 0.0);
	}
	
	/**
	 * This method tests validatePeriodCoveredDateChar500 method for data inputs
	 * and an ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Test
	public void testValidatePeriodCoveredDateChar5002() throws ApplicationException
	{
		HashMap aoInsertPropMap = new HashMap();
		aoInsertPropMap.put(ApplicationConstants.PERIOD_COVER_FROM_YEAR, "1990");
		aoInsertPropMap.put(ApplicationConstants.PERIOD_COVER_TO_YEAR, "");
		String asNextExpectedDocType = "test";
		double loresult = FileNetOperationsUtils.validatePeriodCoveredDateChar500(aoInsertPropMap,
				asNextExpectedDocType);
		assertTrue(loresult == 0.0);
	}
	
	/**
	 * This method tests validatePeriodCoveredDateChar500 method for data inputs
	 * and an ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Test
	public void testValidatePeriodCoveredDateChar5003() throws ApplicationException
	{
		HashMap aoInsertPropMap = new HashMap();
		aoInsertPropMap.put(ApplicationConstants.PERIOD_COVER_FROM_YEAR, "1990");
		aoInsertPropMap.put(ApplicationConstants.PERIOD_COVER_TO_YEAR, null);
		String asNextExpectedDocType = "test";
		double loresult = FileNetOperationsUtils.validatePeriodCoveredDateChar500(aoInsertPropMap,
				asNextExpectedDocType);
		assertTrue(loresult == 0.0);
	}
	
	/**
	 * This method tests validatePeriodCoveredDateChar500 method for data inputs
	 * and an ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Test(expected = ApplicationException.class)
	public void testValidatePeriodCoveredDateChar5004() throws ApplicationException
	{
		HashMap aoInsertPropMap = new HashMap();
		aoInsertPropMap.put(ApplicationConstants.PERIOD_COVER_FROM_YEAR, "2015");
		aoInsertPropMap.put(ApplicationConstants.PERIOD_COVER_FROM_MONTH, "jun");
		aoInsertPropMap.put(ApplicationConstants.PERIOD_COVER_TO_MONTH, "may");
		aoInsertPropMap.put(ApplicationConstants.PERIOD_COVER_TO_YEAR, "2016");
		String asNextExpectedDocType = null;
		FileNetOperationsUtils.validatePeriodCoveredDateChar500(aoInsertPropMap,
				asNextExpectedDocType);

	}
	
	
	/**
	 * This method tests validatePeriodCoveredDateChar500 method for data inputs
	 * and an ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Test(expected = ApplicationException.class)
	public void testValidatePeriodCoveredDateChar5005() throws ApplicationException
	{
		HashMap aoInsertPropMap = new HashMap();
		aoInsertPropMap.put(ApplicationConstants.PERIOD_COVER_FROM_YEAR, "2015");
		aoInsertPropMap.put(ApplicationConstants.PERIOD_COVER_FROM_MONTH, "jun");
		aoInsertPropMap.put(ApplicationConstants.PERIOD_COVER_TO_MONTH, "may");
		aoInsertPropMap.put(ApplicationConstants.PERIOD_COVER_TO_YEAR, "2016");
		String asNextExpectedDocType = "";
		FileNetOperationsUtils.validatePeriodCoveredDateChar500(aoInsertPropMap,
				asNextExpectedDocType);

	}
	
	/**
	 * This method tests validatePeriodCoveredDateChar500 method for data inputs
	 * and an ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Test(expected = ApplicationException.class)
	public void testValidatePeriodCoveredDateChar5006() throws ApplicationException
	{
		HashMap aoInsertPropMap = new HashMap();
		aoInsertPropMap.put(ApplicationConstants.PERIOD_COVER_FROM_YEAR, "2015");
		aoInsertPropMap.put(ApplicationConstants.PERIOD_COVER_FROM_MONTH, "jun");
		aoInsertPropMap.put(ApplicationConstants.PERIOD_COVER_TO_MONTH, "may");
		aoInsertPropMap.put(ApplicationConstants.PERIOD_COVER_TO_YEAR, "2016");
		String asNextExpectedDocType = "";
		FileNetOperationsUtils.validatePeriodCoveredDateChar500(aoInsertPropMap,
				asNextExpectedDocType);

	}
	
	/**
	 * This method tests validatePeriodCoveredDateChar500 method for data inputs
	 * and an ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Test(expected = ApplicationException.class)
	public void testValidatePeriodCoveredDateChar5007() throws ApplicationException
	{
		HashMap aoInsertPropMap = new HashMap();
		aoInsertPropMap.put(ApplicationConstants.PERIOD_COVER_FROM_YEAR, "2013");
		aoInsertPropMap.put(ApplicationConstants.PERIOD_COVER_FROM_MONTH, "jun");
		aoInsertPropMap.put(ApplicationConstants.PERIOD_COVER_TO_MONTH, "may");
		aoInsertPropMap.put(ApplicationConstants.PERIOD_COVER_TO_YEAR, "2016");
		String asNextExpectedDocType = "";
		FileNetOperationsUtils.validatePeriodCoveredDateChar500(aoInsertPropMap,
				asNextExpectedDocType);

	}
	
	/**
	 * This method tests validatePeriodCoveredDateChar500 method for data inputs
	 * and an ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Test(expected = ApplicationException.class)
	public void testValidatePeriodCoveredDateChar5008() throws ApplicationException
	{
		HashMap aoInsertPropMap = new HashMap();
		aoInsertPropMap.put(ApplicationConstants.PERIOD_COVER_FROM_YEAR, "2013");
		aoInsertPropMap.put(ApplicationConstants.PERIOD_COVER_FROM_MONTH, "jun");
		aoInsertPropMap.put(ApplicationConstants.PERIOD_COVER_TO_MONTH, "may");
		aoInsertPropMap.put(ApplicationConstants.PERIOD_COVER_TO_YEAR, "2013");
		String asNextExpectedDocType = null;
		FileNetOperationsUtils.validatePeriodCoveredDateChar500(aoInsertPropMap,
				asNextExpectedDocType);

	}
	
	/**
	 * This method tests validatePeriodCoveredDateChar500 method for data inputs
	 * and an ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Test(expected = ApplicationException.class)
	public void testValidatePeriodCoveredDateChar5009() throws ApplicationException
	{
		HashMap aoInsertPropMap = new HashMap();
		aoInsertPropMap.put(ApplicationConstants.PERIOD_COVER_FROM_YEAR, "2012");
		aoInsertPropMap.put(ApplicationConstants.PERIOD_COVER_FROM_MONTH, "jun");
		aoInsertPropMap.put(ApplicationConstants.PERIOD_COVER_TO_MONTH, "nov");
		aoInsertPropMap.put(ApplicationConstants.PERIOD_COVER_TO_YEAR, "2013");
		String asNextExpectedDocType = "";
		FileNetOperationsUtils.validatePeriodCoveredDateChar500(aoInsertPropMap,
				asNextExpectedDocType);

	}
	
	
	
	
	/**
	 * This method tests getSampleTypeList method for data inputs and an
	 * ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testGetSampleTypeList() throws ApplicationException
	{
		ArrayList<String> loresult = FileNetOperationsUtils.getSampleTypeList("Financials");
		assertTrue(!loresult.isEmpty());
	}

	/**
	 * This method tests getSampleCategoryList method for data inputs and an
	 * ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testGetSampleCategoryList() throws ApplicationException
	{
		ArrayList<String> loresult = FileNetOperationsUtils.getSampleCategoryList();
		assertTrue(!loresult.isEmpty());
	}
	
	/**
	 * This method tests getProviderList method for data inputs and an
	 * ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testGetProviderList() throws ApplicationException
	{
		List<ProviderBean> loresult = FileNetOperationsUtils.getProviderList();
		assertTrue(!loresult.isEmpty());
	}
	
	/**
	 * This method tests generateDocumentBean method for data inputs and an
	 * ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testGenerateDocumentBean() throws ApplicationException
	{
		HashMap loHashmap = new HashMap();
		loHashmap.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY, "hhs mgr1");
		loHashmap.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, "filenetutil");
		loHashmap.put(P8Constants.PROPERTY_CE_DOC_TYPE, "Appendix A");
		loHashmap.put(P8Constants.PROPERTY_CE_DOC_CATEGORY, "Appendix A");
		loHashmap.put(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE, new Date());
		loHashmap.put(P8Constants.PROPERTY_CE_DOCUMENT_ID, "40C20EB4-F614-40F7-AEEE-E61C5F86DE0A");
		loHashmap.put(P8Constants.PROPERTY_CE_IS_DOCUMENT_SHARED, "false");
		loHashmap.put(P8Constants.PROPERTY_CE_SAMPLE_CATEGORY, null);
		loHashmap.put(P8Constants.PROPERTY_CE_SAMPLE_TYPE, null);
		List aoDocumentList = new ArrayList();
		List aoDocuments = new ArrayList<Document>();
		aoDocumentList.add(loHashmap);
		String lsShareStatusString = FileNetOperationsUtils.generateDocumentBean(aoDocumentList,aoDocuments,"city_org",session);
		Assert.assertEquals("", lsShareStatusString);
	}
	
	/**
	 * This method tests generateDocumentBean method for data inputs and an
	 * ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testGenerateDocumentBean1() throws ApplicationException
	{
		HashMap loHashmap = new HashMap();
		loHashmap.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY, "hhs mgr1");
		loHashmap.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, "filenetutil");
		loHashmap.put(P8Constants.PROPERTY_CE_DOC_TYPE, "Appendix A");
		loHashmap.put(P8Constants.PROPERTY_CE_DOC_CATEGORY, "Appendix A");
		loHashmap.put(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE, new Date());
		loHashmap.put(P8Constants.PROPERTY_CE_DOCUMENT_ID, "40C20EB4-F614-40F7-AEEE-E61C5F86DE0A");
		loHashmap.put(P8Constants.PROPERTY_CE_IS_DOCUMENT_SHARED, "true");
		loHashmap.put(P8Constants.PROPERTY_CE_SAMPLE_CATEGORY, null);
		loHashmap.put(P8Constants.PROPERTY_CE_SAMPLE_TYPE, null);
		List aoDocumentList = new ArrayList();
		List aoDocuments = new ArrayList<Document>();
		aoDocumentList.add(loHashmap);
		String lsShareStatusString = FileNetOperationsUtils.generateDocumentBean(aoDocumentList,aoDocuments,"provider_org",session);
		Assert.assertEquals("true", lsShareStatusString);
	}
	
	/**
	 * This method tests generateDocumentBean method for data inputs and an
	 * ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testGenerateDocumentBean2() throws ApplicationException
	{
		List aoDocumentList = null;
		List aoDocuments = new ArrayList<Document>();
		String lsShareStatusString = FileNetOperationsUtils.generateDocumentBean(aoDocumentList,aoDocuments,"provider_org",session);
		Assert.assertEquals("", lsShareStatusString);
	}
	
	/**
	 * This method tests setOrderByParameter method for data inputs and an
	 * ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testSetOrderByParameter() throws ApplicationException
	{
		Channel loChannel = new Channel();
		FileNetOperationsUtils.setOrderByParameter(loChannel, null, null);
		assertNotNull(loChannel.getData("orderByMap"));
	}
	
	/**
	 * This method tests setOrderByParameter method for data inputs and an
	 * ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testSetOrderByParameterDate() throws ApplicationException
	{
		Channel loChannel = new Channel();
		FileNetOperationsUtils.setOrderByParameter(loChannel, ApplicationConstants.PROPERTY_TYPE_DATE, "DESC");
		assertNotNull(loChannel.getData("orderByMap"));
	}
	
	/**
	 * This method tests setOrderByParameter method for data inputs and an
	 * ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testSetOrderByParameterDocName() throws ApplicationException
	{
		Channel loChannel = new Channel();
		FileNetOperationsUtils.setOrderByParameter(loChannel, ApplicationConstants.DOC_NAME, "DESC");
		assertNotNull(loChannel.getData("orderByMap"));
	}
	
	/**
	 * This method tests setOrderByParameter method for data inputs and an
	 * ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testSetOrderByParameterDocType() throws ApplicationException
	{
		Channel loChannel = new Channel();
		FileNetOperationsUtils.setOrderByParameter(loChannel, ApplicationConstants.DOC_TYPE, "DESC");
		assertNotNull(loChannel.getData("orderByMap"));
	}
	
	/**
	 * This method tests setOrderByParameter method for data inputs and an
	 * ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testSetOrderByParameterDocCategory() throws ApplicationException
	{
		Channel loChannel = new Channel();
		FileNetOperationsUtils.setOrderByParameter(loChannel, ApplicationConstants.DOC_CATEGORY, "DESC");
		assertNotNull(loChannel.getData("orderByMap"));
	}
	
	/**
	 * This method tests setOrderByParameter method for data inputs and an
	 * ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testSetOrderByParameterLastModifiedBy() throws ApplicationException
	{
		Channel loChannel = new Channel();
		FileNetOperationsUtils.setOrderByParameter(loChannel, ApplicationConstants.LAST_MODIFIED_BY, "DESC");
		assertNotNull(loChannel.getData("orderByMap"));
	}
	
	/**
	 * This method tests setOrderByParameter method for data inputs and an
	 * ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testSetOrderByParameterShareStatus() throws ApplicationException
	{
		Channel loChannel = new Channel();
		FileNetOperationsUtils.setOrderByParameter(loChannel, ApplicationConstants.SHARE_DOCUMENT_STATUS, "DESC");
		assertNotNull(loChannel.getData("orderByMap"));
	}
	
	/**
	 * This method tests setOrderByParameter method for data inputs and an
	 * ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testSetOrderByParameterSampleCategory() throws ApplicationException
	{
		Channel loChannel = new Channel();
		FileNetOperationsUtils.setOrderByParameter(loChannel, ApplicationConstants.SAMPLE_CATEGORY, "DESC");
		assertNotNull(loChannel.getData("orderByMap"));
	}

	/**
	 * This method tests setOrderByParameter method for data inputs and an
	 * ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testSetOrderByParameterSampleType() throws ApplicationException
	{
		Channel loChannel = new Channel();
		FileNetOperationsUtils.setOrderByParameter(loChannel, ApplicationConstants.SAMPLE_TYPE, "DESC");
		assertNotNull(loChannel.getData("orderByMap"));
	}
	
	/**
	 * This method tests setOrderByParameter method for data inputs and an
	 * ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testSetOrderByParameter1() throws ApplicationException
	{
		Channel loChannel = new Channel();
		FileNetOperationsUtils.setOrderByParameter(loChannel, "/", "DESC");
		assertNotNull(loChannel.getData("orderByMap"));
	}
	
	/**
	 * This method tests getProviderName method for data inputs and an
	 * ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testGetProviderName() throws ApplicationException
	{
		List<ProviderBean> loProviderList = new ArrayList<ProviderBean>();
		ProviderBean loProviderBean = new ProviderBean();
		loProviderBean.setDisplayValue("R3 dev team\'s organization");
		loProviderBean.setHiddenValue("r3_org");
		ProviderBean loProviderBean1 = new ProviderBean();
		loProviderBean1.setDisplayValue("test_manager");
		loProviderBean1.setHiddenValue("test");
		loProviderList.add(loProviderBean);
		loProviderList.add(loProviderBean1);
		String lsAgencyId = FileNetOperationsUtils.getProviderName(loProviderList, "r3_org");
		Assert.assertEquals(lsAgencyId, "R3 dev team\'s organization");
	}
	
	/**
	 * This method tests getProviderName method for data inputs and an
	 * ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testGetProviderName1() throws ApplicationException
	{
		List<ProviderBean> loProviderList = new ArrayList<ProviderBean>();
		String lsAgencyId = FileNetOperationsUtils.getProviderName(loProviderList, "r3_org");
		Assert.assertEquals(lsAgencyId, "r3_org");
	}
	
	/**
	 * This method tests getProviderName method for data inputs and an
	 * ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testGetProviderName2() throws ApplicationException
	{
		List<ProviderBean> loProviderList = new ArrayList<ProviderBean>();
		ProviderBean loProviderBean = new ProviderBean();
		loProviderBean.setDisplayValue("R3 dev team\'s organization");
		loProviderBean.setHiddenValue("r3_org");
		ProviderBean loProviderBean1 = new ProviderBean();
		loProviderBean1.setDisplayValue("test_manager");
		loProviderBean1.setHiddenValue("test");
		loProviderList.add(loProviderBean);
		loProviderList.add(loProviderBean1);
		String lsAgencyId = FileNetOperationsUtils.getProviderName(loProviderList, null);
		Assert.assertEquals(lsAgencyId, null);
	}
	
	/**
	 * This method tests getProviderName method for data inputs and an
	 * ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testGetProviderName3() throws ApplicationException
	{
		List<ProviderBean> loProviderList = new ArrayList<ProviderBean>();
		ProviderBean loProviderBean = new ProviderBean();
		loProviderBean.setDisplayValue("R3 dev team\'s organization");
		loProviderBean.setHiddenValue("r3_org");
		ProviderBean loProviderBean1 = new ProviderBean();
		loProviderBean1.setDisplayValue("test_manager");
		loProviderBean1.setHiddenValue("test");
		loProviderList.add(loProviderBean);
		loProviderList.add(loProviderBean1);
		String lsAgencyId = FileNetOperationsUtils.getProviderName(loProviderList, "provider");
		Assert.assertEquals(lsAgencyId, "provider");
	}
	
	/**
	 * This method tests getProviderId method for data inputs and an
	 * ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testGetProviderId() throws ApplicationException
	{
		List<ProviderBean> loProviderList = new ArrayList<ProviderBean>();
		ProviderBean loProviderBean = new ProviderBean();
		loProviderBean.setDisplayValue("R3 dev team\'s organization");
		loProviderBean.setHiddenValue("r3_org");
		ProviderBean loProviderBean1 = new ProviderBean();
		loProviderBean1.setDisplayValue("test_manager");
		loProviderBean1.setHiddenValue("test");
		loProviderList.add(loProviderBean);
		loProviderList.add(loProviderBean1);
		String lsAgencyId = FileNetOperationsUtils.getProviderId(loProviderList, "org_provider2");
		Assert.assertEquals(lsAgencyId, "org_provider2");
	}
	
	/**
	 * This method tests getProviderId method for data inputs and an
	 * ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testGetProviderId1() throws ApplicationException
	{
		List<ProviderBean> loProviderList = new ArrayList<ProviderBean>();
		ProviderBean loProviderBean = new ProviderBean();
		loProviderBean.setDisplayValue("R3 dev team\'s organization");
		loProviderBean.setHiddenValue("r3_org");
		ProviderBean loProviderBean1 = new ProviderBean();
		loProviderBean1.setDisplayValue("test_manager");
		loProviderBean1.setHiddenValue("test");
		loProviderList.add(loProviderBean);
		loProviderList.add(loProviderBean1);
		String lsAgencyId = FileNetOperationsUtils.getProviderId(loProviderList, "test_manager");
		Assert.assertEquals(lsAgencyId, "test");
	}
	
	/**
	 * This method tests getProviderId method for data inputs and an
	 * ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testGetProviderId2() throws ApplicationException
	{
		List<ProviderBean> loProviderList = new ArrayList<ProviderBean>();
		String lsAgencyId = FileNetOperationsUtils.getProviderId(loProviderList, "test_manager");
		Assert.assertEquals(lsAgencyId, "test_manager");
	}
	
	/**
	 * This method tests getProviderId method for data inputs and an
	 * ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testGetProviderId3() throws ApplicationException
	{
		List<ProviderBean> loProviderList = new ArrayList<ProviderBean>();
		ProviderBean loProviderBean = new ProviderBean();
		loProviderBean.setDisplayValue("R3 dev team\'s organization");
		loProviderBean.setHiddenValue("r3_org");
		ProviderBean loProviderBean1 = new ProviderBean();
		loProviderBean1.setDisplayValue("test_manager");
		loProviderBean1.setHiddenValue("test");
		loProviderList.add(loProviderBean);
		loProviderList.add(loProviderBean1);
		String lsAgencyId = FileNetOperationsUtils.getProviderId(loProviderList, null);
		Assert.assertEquals(lsAgencyId, null);
	}
	
	/**
	 * This method tests setDocCategorynDocType method for data inputs and an
	 * ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testSetDocCategorynDocType() throws ApplicationException
	{
		Document loDoc = new Document();
		FileNetOperationsUtils.setDocCategorynDocType(loDoc, null, "provider_org");
		assertNotNull(loDoc.getCategoryList());
	}
	
	/**
	 * This method tests setDocCategorynDocType method for data inputs and an
	 * ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testSetDocCategorynDocType1() throws ApplicationException
	{
		Document loDoc = new Document();
		FileNetOperationsUtils.setDocCategorynDocType(loDoc, "Audit", "provider_org");
		assertNotNull(loDoc.getCategoryList());
		assertNotNull(loDoc.getTypeList());
	}
	
	/**
	 * This method tests setDocCategorynDocType method for data inputs and an
	 * ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testSetDocCategorynDocType2() throws ApplicationException
	{
		Document loDoc = new Document();
		FileNetOperationsUtils.setDocCategorynDocType(loDoc, "", "provider_org");
		assertNotNull(loDoc.getCategoryList());
	}
	
	/**
	 * This method tests setDocCategorynDocType method for data inputs and an
	 * ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testSetDocCategorynDocType3() throws ApplicationException
	{
		Document loDoc = new Document();
		ArrayList<String> loCategoryList = new ArrayList<String>();
		loCategoryList.add("Audit");
		loDoc.setCategoryList(loCategoryList);
		FileNetOperationsUtils.setDocCategorynDocType(loDoc, "Audit", "provider_org");
		assertNotNull(loDoc.getCategoryList());
		assertNotNull(loDoc.getTypeList());
	}
	
	/**
	 * This method tests setDocCategorynDocType method for data inputs and an
	 * ApplicationException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testSetDocCategorynDocType4() throws ApplicationException
	{
		FileNetOperationsUtils.setDocCategorynDocType(null, null, null);
	}
	
	
	/**
	 * This method tests requiredDocsProps method for data inputs
	 **/
	@Test
	public void testRequiredDocsProps() throws ApplicationException
	{
		HashMap<String, String> loHmReqProps = FileNetOperationsUtils.requiredDocsProps("city_org", "Audit");
		assertNotNull(loHmReqProps);
		assertEquals(loHmReqProps.get("SAMPLE_CATEGORY"), "");
		assertEquals(loHmReqProps.get("SAMPLE_TYPE"), "");
	}
	
	
	/**
	 * This method tests requiredDocsProps method for data inputs
	 **/
	@Test
	public void testRequiredDocsProps1() throws ApplicationException
	{
		HashMap<String, String> loHmReqProps = FileNetOperationsUtils.requiredDocsProps("provider_org", "Audit");
		assertNotNull(loHmReqProps);
	}
	
	/**
	 * This method tests requiredDocsProps method for data inputs
	 **/
	@Test
	public void testRequiredDocsProps2() throws ApplicationException
	{
		HashMap<String, String> loHmReqProps = FileNetOperationsUtils.requiredDocsProps("city_org", null);
		assertNotNull(loHmReqProps);
	}
	
	/**
	 * This method tests requiredDocsProps method for data inputs
	 **/
	@Test
	public void testRequiredDocsProps3() throws ApplicationException
	{
		HashMap<String, String> loHmReqProps = FileNetOperationsUtils.requiredDocsProps("city_org", "Solicitation");
		assertNotNull(loHmReqProps);
	}
	
	/**
	 * This method tests requiredDocsProps method for data inputs
	 **/
	@Test
	public void testRequiredDocsProps4() throws ApplicationException
	{
		HashMap<String, String> loHmReqProps = FileNetOperationsUtils.requiredDocsProps("city_org", "");
		assertNotNull(loHmReqProps);
	}
	
	/**
	 * This method tests setFilteredPropertiesForSharedStatus method for data inputs
	 **/
	@Test
	public void testSetFilteredPropertiesForSharedStatus1()
	{
		String[] loRadioArray = {"shared"};
		Document loDocument = new Document();
		HashMap loFilterProps = new HashMap();
		assertNotNull(loFilterProps);
		FileNetOperationsUtils.setFilteredPropertiesForSharedStatus(loRadioArray, "provider_org", "r3_org", loDocument, loFilterProps);
		assertEquals(loFilterProps.get("IS_SHARED"), true);
	}
	
	/**
	 * This method tests setFilteredPropertiesForSharedStatus method for data inputs
	 **/
	@Test
	public void testSetFilteredPropertiesForSharedStatus2()
	{
		String[] loRadioArray = {"unshared"};
		Document loDocument = new Document();
		HashMap loFilterProps = new HashMap();
		assertNotNull(loFilterProps);
		FileNetOperationsUtils.setFilteredPropertiesForSharedStatus(loRadioArray, "provider_org", "r3_org", loDocument, loFilterProps);
		assertEquals(loFilterProps.get("IS_SHARED"), false);
	}
	
	/**
	 * This method tests setFilteredPropsForProviderAndAgency method for data inputs
	 * @throws ApplicationException 
	 **/
	@Test
	public void testSetFilteredPropsForProviderAndAgency() throws ApplicationException
	{
		Document loDocument = new Document();
		HashMap loFilterProps = new HashMap();
		FileNetOperationsUtils.setFilteredPropsForProviderAndAgency( "Audit",  "R3 dev team\'s organization", "DOC - Department of Corrections",  "provider_org",  "r3_org",  loDocument,  
				loFilterProps, "Agency");
		assertEquals(loFilterProps.get("FILTER_NYC_ORG_ID"), "DOC");
	}
	
	/**
	 * This method tests setFilteredPropsForProviderAndAgency method for data inputs
	 * @throws ApplicationException 
	 **/
	@Test
	public void testSetFilteredPropsForProviderAndAgency1() throws ApplicationException
	{
		Document loDocument = new Document();
		HashMap loFilterProps = new HashMap();
		FileNetOperationsUtils.setFilteredPropsForProviderAndAgency( "Audit",  "R3 dev team\'s organization", "DOC - Department of Corrections",  "provider_org",  "r3_org",  loDocument,  
				loFilterProps, "Provider");
		assertEquals(loFilterProps.get("FILTER_NYC_ORG_ID"), "DOC");
		assertEquals(loFilterProps.get("FILTER_PROVIDER_ID"), "r3_org");
	}
	
	/**
	 * This method tests setFilteredPropsForProviderAndAgency method for data inputs
	 * @throws ApplicationException 
	 **/
	@Test
	public void testSetFilteredPropsForProviderAndAgency2() throws ApplicationException
	{
		Document loDocument = new Document();
		HashMap loFilterProps = new HashMap();
		FileNetOperationsUtils.setFilteredPropsForProviderAndAgency( "Audit",  "R3 dev team\'s organization", "DOC - Department of Corrections",  "provider_org",  "r3_org",  loDocument,  
				loFilterProps, null);
		assertEquals(loFilterProps.get("FILTER_NYC_ORG_ID"), "DOC");
		assertEquals(loFilterProps.get("FILTER_PROVIDER_ID"), "r3_org");
	}
	
	/**
	 * This method tests getDoctypesFromXML method for data inputs
	 * @throws ApplicationException 
	 **/
	@Test
	public void testGetDoctypesFromXML() throws ApplicationException
	{
		org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENETDOCTYPE);
		List<String> loDoctypesList = FileNetOperationsUtils.getDoctypesFromXML(loXMLDoc, "Solicitation", "agency_org", "r3_org");
		assertTrue(loDoctypesList.size()>0);
		
	}
	
	/**
	 * This method tests getDoctypesFromXML method for data inputs
	 * @throws ApplicationException 
	 **/
	@Test
	public void testGetDoctypesFromXML1() throws ApplicationException
	{
		org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENETDOCTYPE);
		List<String> loDoctypesList = FileNetOperationsUtils.getDoctypesFromXML(loXMLDoc, "Solicitation", "agency_org", "city_org");
		assertTrue(loDoctypesList.size()>0);
		
	}
	
	/**
	 * This method tests getDoctypesFromXML method for data inputs
	 * @throws ApplicationException 
	 **/
	@Test(expected = ApplicationException.class)
	public void testGetDoctypesFromXMLException() throws ApplicationException
	{
		org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENETDOCTYPE);
		FileNetOperationsUtils.getDoctypesFromXML(loXMLDoc, "Solicitation", null, "city_org");
		
	}
	
	/**
	 * This method tests getDoctypesFromXML method for data inputs
	 * @throws ApplicationException 
	 **/
	@Test(expected = ApplicationException.class)
	public void testGetDoctypesFromXMLException1() throws ApplicationException
	{
		org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENETDOCTYPE);
		FileNetOperationsUtils.getDoctypesFromXML(null, "Solicitation", "agency_org", "city_org");
		
	}
	
	/**
	 * This method tests getDoctypesFromXML method for data inputs
	 * @throws ApplicationException 
	 **/
	@Test(expected = ApplicationException.class)
	public void testGetDoctypesFromXMLException2() throws ApplicationException
	{
		org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENETDOCTYPE);
		FileNetOperationsUtils.getDoctypesFromXML("", "Solicitation", "agency_org", "city_org");
		
	}
	
	/**
	 * This method tests getDoctypesFromXML method for data inputs
	 * @throws ApplicationException 
	 **/
	@Test(expected = ApplicationException.class)
	public void testGetDoctypesFromXMLException3() throws ApplicationException
	{
		org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENETDOCTYPE);
		FileNetOperationsUtils.getDoctypesFromXML(loXMLDoc, "Solicitation", "", "city_org");
		
	}
	
	/**
	 * This method tests getDoctypesFromXML method for data inputs
	 * @throws ApplicationException 
	 **/
	@Test
	public void testGetDoctypesFromXML2() throws ApplicationException
	{
		org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENETDOCTYPE);
		List<String> loDoctypesList = FileNetOperationsUtils.getDoctypesFromXML(loXMLDoc, "Financials", "provider_org", "city_org");
		assertTrue(loDoctypesList.size()>0);
		
	}
	
	
	/**
	 * This method tests getDocCategoryFromXML method for data inputs
	 * @throws ApplicationException 
	 **/
	@Test
	public void testGetDocCategoryFromXML() throws ApplicationException
	{
		org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENETDOCTYPE);
		List<String> loDoctypesList = FileNetOperationsUtils.getDocCategoryFromXML(loXMLDoc, "BudgetAmedmentTemplate", "agency_org");
		assertTrue(loDoctypesList.size()>0);
	}
	

	/**
	 * This method tests testGetDocCategoryFromXML method for data inputs
	 * @throws ApplicationException 
	 **/
	@Test(expected = ApplicationException.class)
	public void testGetDocCategoryFromXML2() throws ApplicationException
	{
		org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENETDOCTYPE);
		List<String> loDoctypesList = FileNetOperationsUtils.getDocCategoryFromXML(loXMLDoc, "BudgetAmedmentTemplate", "povider_org");
	}
	
	/**
	 * This method tests testGetDocCategoryFromXML2 method for data inputs
	 * @throws ApplicationException 
	 **/
	@Test(expected = ApplicationException.class)
	public void testGetDocCategoryFromXMLException() throws ApplicationException
	{
		org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENETDOCTYPE);
		FileNetOperationsUtils.getDocCategoryFromXML("zxnj", "DOC", "abc");
	}
	
	/**
	 * This method tests testGetDocCategoryFromXML2 method for data inputs
	 * @throws ApplicationException 
	 **/
	@Test(expected = ApplicationException.class)
	public void testGetDocCategoryFromXMLException1() throws ApplicationException
	{
		org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENETDOCTYPE);
		FileNetOperationsUtils.getDocCategoryFromXML(loXMLDoc, "BudgetAmedmentTemplate", null);
	}
	
	/**
	 * This method tests getDocPropertiesFromXML method for data inputs
	 * @throws ApplicationException 
	 **/
	@Test
	public void testGetDocPropertiesFromXML() throws ApplicationException
	{
		org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENETDOCTYPE);
		List<String> loDoctypesList = FileNetOperationsUtils.getDocPropertiesFromXML(loXMLDoc, "Audit", "A-133", "provider_org");
		assertTrue(loDoctypesList.size()>0);
		
	}
	
	/**
	 * This method tests GetDocPropertiesFromXML method for data inputs
	 * @throws ApplicationException 
	 **/
	@Test(expected = ApplicationException.class)
	public void testGetDocPropertiesFromXMLException() throws ApplicationException
	{
		FileNetOperationsUtils.getDocPropertiesFromXML(null, "Audit", "A-133", "provider_org");
	}
	
	
	/**
	 * This method tests GetDocPropertiesFromXML method for data inputs
	 * @throws ApplicationException 
	 **/
	@Test(expected = ApplicationException.class)
	public void testGetDocPropertiesFromXMLException1() throws ApplicationException
	{
		org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENETDOCTYPE);
		FileNetOperationsUtils.getDocPropertiesFromXML(loXMLDoc, "Audit", "A-133", null);
	}
	
	/**
	 * This method tests GetDocPropertiesFromXML method for data inputs
	 * @throws ApplicationException 
	 **/
	@Test(expected = ApplicationException.class)
	public void testGetDocPropertiesFromXMLException2() throws ApplicationException
	{
		org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENETDOCTYPE);
		FileNetOperationsUtils.getDocPropertiesFromXML(loXMLDoc, "Audit", "A-133", "");
	}
	
	/**
	 * This method tests GetDocPropertiesFromXML method for data inputs
	 * @throws ApplicationException 
	 **/
	@Test(expected = ApplicationException.class)
	public void testGetDocPropertiesFromXMLException3() throws ApplicationException
	{
		org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENETDOCTYPE);
		FileNetOperationsUtils.getDocPropertiesFromXML(loXMLDoc, "Audit", "", "provider_org");
	}
	
	/**
	 * This method tests GetDocPropertiesFromXML method for data inputs
	 * @throws ApplicationException 
	 **/
	@Test(expected = ApplicationException.class)
	public void testGetDocPropertiesFromXMLException4() throws ApplicationException
	{
		org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENETDOCTYPE);
		FileNetOperationsUtils.getDocPropertiesFromXML(loXMLDoc, "Audit", null, "provider_org");
	}
	
	
	/**
	 * This method tests GetDocPropertiesFromXML method for data inputs
	 * @throws ApplicationException 
	 **/
	@Test(expected = ApplicationException.class)
	public void testGetDocPropertiesFromXMLException5() throws ApplicationException
	{
		org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENETDOCTYPE);
		FileNetOperationsUtils.getDocPropertiesFromXML(loXMLDoc, "", "A-133", "provider_org");
	}
	
	
	/**
	 * This method tests GetDocPropertiesFromXML method for data inputs
	 * @throws ApplicationException 
	 **/
	@Test(expected = ApplicationException.class)
	public void testGetDocPropertiesFromXMLException6() throws ApplicationException
	{
		org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENETDOCTYPE);
		FileNetOperationsUtils.getDocPropertiesFromXML(loXMLDoc, null, "A-133", "provider_org");
	}
	
	/**
	 * This method tests GetDocPropertiesFromXML method for data inputs
	 * @throws ApplicationException 
	 **/
	@Test(expected = ApplicationException.class)
	public void testGetDocPropertiesFromXMLException7() throws ApplicationException
	{
		org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENETDOCTYPE);
		FileNetOperationsUtils.getDocPropertiesFromXML(loXMLDoc, "Audit", "A-133", "agency_org");
	}
	
	
	/**
	 * This method tests convert method for data inputs
	 * @throws ApplicationException 
	 * @throws Exception 
	 **/
	@Test
	public void testConvert() throws ApplicationException, IOException
	{
		InputStream inputstream = new FileInputStream("C:\\Users\\d.a.malik\\Desktop\\test.txt");
		byte[] lbOutput = FileNetOperationsUtils.convert(inputstream);
		assertTrue(lbOutput.length>0);
		
	}
	
	
}
