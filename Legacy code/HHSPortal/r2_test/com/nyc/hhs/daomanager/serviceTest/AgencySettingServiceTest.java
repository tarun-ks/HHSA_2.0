package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.daomanager.service.AgencySettingService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.model.AgencySettingsBean;
import com.nyc.hhs.model.CityUserDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8SecurityOperations;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.PropertyLoader;

/**
 * 
 *
 */
public class AgencySettingServiceTest
{
	private static SqlSession moSession = null; // SQL Session
	private static P8UserSession session = null; // FileNet session

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
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}
	
	private static P8UserSession getFileNetSession() throws ApplicationException
	{
		System.setProperty(P8Constants.PROP_FILE_JAVA_SECURITY_AUTH_LOGIN_CONFIG,
				PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, P8Constants.PROP_FILE_JAVA_SECURITY_AUTH_LOGIN_CONFIG));
		System.setProperty(P8Constants.PROP_FILE_JAVA_NAMING_FACTORY_INITIAL,
				PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, P8Constants.PROP_FILE_JAVA_NAMING_FACTORY_INITIAL));
		System.setProperty(P8Constants.PROP_FILE_FILENET_PE_BOOTSTRAP_CEURI, PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "FILENET_URI"));
		
		P8UserSession loUserSession = new P8UserSession();
		loUserSession.setContentEngineUri(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "FILENET_URI"));
		loUserSession.setObjectStoreName(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "OBJECT_STORE_NAME"));
		loUserSession.setIsolatedRegionName(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "CONNECTION_POINT_NAME"));
		loUserSession.setUserId("ceadmin");
		loUserSession.setPassword("Filenet1");
		P8SecurityOperations loP8SecurityService = new P8SecurityOperations();
		SqlSession loFilenetPEDBSession= HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory().openSession();
		loUserSession.setFilenetPEDBSession(loFilenetPEDBSession);
		//loP8SecurityService.getPESession(loUserSession);
		//loP8SecurityService.getObjectStore(loUserSession);
		
		return loUserSession;
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

	/**
	 * Tests the method to fetch the Agency List and the Review Process data,
	 * i.e. a static list in the DB.
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchAgencyAndReviewProcessData() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			AgencySettingService loAgencySettingService = new AgencySettingService();

			AgencySettingsBean loASBean = loAgencySettingService.fetchAgencyAndReviewProcessData(moSession);
			assertNotNull(loASBean);

			// Negative scenario -- By passing NULL session
			loASBean = loAgencySettingService.fetchAgencyAndReviewProcessData(null);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Application Exception thrown", lbThrown);
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}

	}

	/**
	 * ----------Require the parameters for query-----
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchReviewLevels() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			AgencySettingService loAgencySettingService = new AgencySettingService();

			// Case 1 = List is returned
			int lireviewLevel = loAgencySettingService.fetchReviewLevels("ACS", 14, moSession);
			assertNotNull(lireviewLevel);

			// Case 2 = Null, Since no value for Review Process Id = 15
			lireviewLevel = loAgencySettingService.fetchReviewLevels("ACS", 15, moSession);
			assertNotNull(lireviewLevel);

			// Negative scenario --NULL session
			lireviewLevel = loAgencySettingService.fetchReviewLevels("ACS", 14, null);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}

	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testSaveReviewLevels() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			AgencySettingService loAgencySettingService = new AgencySettingService();
			AgencySettingsBean adb = new AgencySettingsBean();
			adb.setAgencyId("DHS");
			adb.setReviewProcessId(14);
			adb.setOldLevelOfReview(0);
			adb.setLastUpdateDate(new Date());
			adb.setCreatedDate(new Date());
			adb.setModifiedDate(new Date());
			adb.setCreatedByUserId("agency_17");
			adb.setModifiedByUserId("agency_17");

			Map<String, Object> map = loAgencySettingService.saveReviewLevels(adb, moSession, session);
			assertNotNull(map);

			adb.setOldLevelOfReview(1);
			map = loAgencySettingService.saveReviewLevels(adb, moSession, session);
			assertNotNull(map);

			// Negative scenario --NULL session
			map = loAgencySettingService.saveReviewLevels(adb, null, session);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}

	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchAllReviewProcessData() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			AgencySettingService loAgencySettingService = new AgencySettingService();
			AgencySettingsBean adb = loAgencySettingService.fetchAllReviewProcessData(moSession);
			assertNotNull(adb);

			// Negative scenario --NULL session
			adb = loAgencySettingService.fetchAllReviewProcessData(null);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}

	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchAgencySetAssgndUsrData() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			AgencySettingService loAgencySettingService = new AgencySettingService();
			AgencySettingsBean adb = loAgencySettingService.fetchAgencySetAssgndUsrData("agency_17", 14, "ACS", "1",
					moSession);
			assertNotNull(adb);

			adb = loAgencySettingService.fetchAgencySetAssgndUsrData("agency_17", 15, "ACS", "1", moSession);
			assertNotNull(adb);

			// Negative scenario --NULL session
			adb = loAgencySettingService.fetchAgencySetAssgndUsrData("agency_17", 14, "ACS", "Y", null);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}

	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testSaveAgencyLevelUsers() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			AgencySettingService loAgencySettingService = new AgencySettingService();

			List<CityUserDetailsBean> loCbListOld = new ArrayList<CityUserDetailsBean>();
			List<CityUserDetailsBean> loCbListNew = new ArrayList<CityUserDetailsBean>();

			CityUserDetailsBean cb1 = new CityUserDetailsBean();
			cb1.setLevelId(1);
			cb1.setUserId("agency_17");

			CityUserDetailsBean cb2 = new CityUserDetailsBean();
			cb2.setLevelId(1);
			cb2.setUserId("agency_18");

			CityUserDetailsBean cb3 = new CityUserDetailsBean();
			cb3.setLevelId(1);
			cb3.setUserId("agency_19");

			CityUserDetailsBean cb4 = new CityUserDetailsBean();
			cb4.setLevelId(1);
			cb4.setUserId("agency_20");

			loCbListOld.add(cb1);
			loCbListOld.add(cb2);
			loCbListNew.add(cb3);
			loCbListNew.add(cb4);

			AgencySettingsBean adbOld = setAgencySettingBean();
			adbOld.setAllLevel1UsersList(loCbListOld);
			AgencySettingsBean adbNew = setAgencySettingBean();
			adbNew.setAllLevel1UsersList(loCbListNew);

			Boolean b = loAgencySettingService.saveAgencyLevelUsers(1, adbOld, adbNew, moSession, session);
			assertTrue(b);

			// Negative scenario --NULL session
			b = loAgencySettingService.saveAgencyLevelUsers(1, adbOld, adbNew, null, session);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}

	}

	/**
	 * @param adbOld
	 */
	private AgencySettingsBean setAgencySettingBean()
	{
		AgencySettingsBean loAgencySettingBean = new AgencySettingsBean();

		loAgencySettingBean.setLevelId(3);
		loAgencySettingBean.setAgencyId("ACS");
		loAgencySettingBean.setReviewProcessId(14);
		loAgencySettingBean.setLevelReviewerId("agency_17");
		loAgencySettingBean.setCreatedByUserId("agency_17");
		loAgencySettingBean.setModifiedByUserId("agency_17");
		loAgencySettingBean.setCreatedDate(new Date());
		loAgencySettingBean.setModifiedDate(new Date());
		loAgencySettingBean.setLastUpdateDate(new Date());

		return loAgencySettingBean;
	}

	@Test(expected = java.lang.Exception.class)
	public void testAgencySettingServicefetchAgencyAndReviewProcessData0Negative()
	{
		AgencySettingService loAgencySettingService = new AgencySettingService();
		try
		{
			loAgencySettingService.fetchAgencyAndReviewProcessData(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAgencySettingServicefetchReviewLevels1Negative()
	{
		AgencySettingService loAgencySettingService = new AgencySettingService();
		try
		{
			loAgencySettingService.fetchReviewLevels(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAgencySettingServicesaveReviewLevels2Negative()
	{
		AgencySettingService loAgencySettingService = new AgencySettingService();
		try
		{
			loAgencySettingService.saveReviewLevels(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAgencySettingServicefetchAllReviewProcessData3Negative()
	{
		AgencySettingService loAgencySettingService = new AgencySettingService();
		try
		{
			loAgencySettingService.fetchAllReviewProcessData(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAgencySettingServicefetchAgencySetAssgndUsrData4Negative()
	{
		AgencySettingService loAgencySettingService = new AgencySettingService();
		try
		{
			loAgencySettingService.fetchAgencySetAssgndUsrData(null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAgencySettingServicesaveAgencyLevelUsers5Negative()
	{
		AgencySettingService loAgencySettingService = new AgencySettingService();
		try
		{
			loAgencySettingService.saveAgencyLevelUsers(null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = ApplicationException.class)
	public void testAgencySettingServicefetchAgencyAndReviewProcessData0NegativeApp() throws ApplicationException
	{
		AgencySettingService loAgencySettingService = new AgencySettingService();
		loAgencySettingService.fetchAgencyAndReviewProcessData(null);
	}

	@Test(expected = ApplicationException.class)
	public void testAgencySettingServicefetchReviewLevels1NegativeApp() throws ApplicationException
	{
		AgencySettingService loAgencySettingService = new AgencySettingService();
		loAgencySettingService.fetchReviewLevels(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAgencySettingServicesaveReviewLevels2NegativeApp() throws ApplicationException
	{
		AgencySettingService loAgencySettingService = new AgencySettingService();
		loAgencySettingService.saveReviewLevels(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAgencySettingServicefetchAllReviewProcessData3NegativeApp() throws ApplicationException
	{
		AgencySettingService loAgencySettingService = new AgencySettingService();
		loAgencySettingService.fetchAllReviewProcessData(null);
	}

	@Test(expected = ApplicationException.class)
	public void testAgencySettingServicefetchAgencySetAssgndUsrData4NegativeApp() throws ApplicationException
	{
		AgencySettingService loAgencySettingService = new AgencySettingService();
		loAgencySettingService.fetchAgencySetAssgndUsrData(null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAgencySettingServicesaveAgencyLevelUsers5NegativeApp() throws ApplicationException
	{
		AgencySettingService loAgencySettingService = new AgencySettingService();
		loAgencySettingService.saveAgencyLevelUsers(null, null, null, null, null);
	}

}
