package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.daomanager.service.ConfigurationService;
import com.nyc.hhs.daomanager.service.SolicitationFinancialsGeneralService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.junit.util.JUnitUtil;
import com.nyc.hhs.model.QueueItemsDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8SecurityOperations;
import com.nyc.hhs.service.filenetmanager.p8services.P8ProcessServiceForSolicitationFinancials;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.PropertyLoader;

public class Release360Test
{
	private static SqlSession moSession = null; // SQL Session
	private SolicitationFinancialsGeneralService moSolicitationFinancialsGeneralService;
	private P8ProcessServiceForSolicitationFinancials moP8ProcessServiceForSolicitationFinancials;
	private ConfigurationService moConfigurationService;
	
	
	@Before
	public void setUp() throws Exception
	{

		moSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		moP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
		moConfigurationService = new ConfigurationService();
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		moSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
		JUnitUtil.getTransactionManager();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		Boolean lbThrown = false;
		try
		{
			moSession.rollback();
			moSession.close();
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

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
		loUserSession.setIsolatedRegionNumber("3");
		SqlSession loFilenetPEDBSession = HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory()
				.openSession();
		loUserSession.setFilenetPEDBSession(loFilenetPEDBSession);

		return loUserSession;
	}


	/**
	 * This method tests getQueueItemDelayConfig method
	 * @throws ApplicationException
	 */
	@Test
	public void testGetQueueItemDelayConfigSuccess() throws ApplicationException
	{
			List<QueueItemsDetailsBean> loTimeConfigMap = moSolicitationFinancialsGeneralService.getQueueItemDelayConfig(moSession);
			assertNotNull(loTimeConfigMap);
	}
	
	/**
	 * This method tests getQueueItemDelayConfig method
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testGetQueueItemDelayConfigException() throws ApplicationException
	{
		moSolicitationFinancialsGeneralService.getQueueItemDelayConfig(null);
	}
	
	/**
	 * This method tests getWorkItemsCountFromQueue method
	 * @throws ApplicationException
	 */
	@Test
	public void testGetWorkItemsCountFromQueueSuccess() throws ApplicationException
	{
		int liWorkItemsCount = moP8ProcessServiceForSolicitationFinancials.getWorkItemsCountFromQueue(getFileNetSession(),"HHSMaintenanceQueue", 5);
		assertNotNull(liWorkItemsCount);
	}
	
	/**
	 * This method tests getWorkItemsCountFromQueue method
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testGetWorkItemsCountFromQueueException() throws ApplicationException
	{
		moP8ProcessServiceForSolicitationFinancials.getWorkItemsCountFromQueue(null,"HHSMaintenanceQueue", 5);
	}
	
	/**
	 * This method tests getQueueItemDelayConfig method
	 * @throws ApplicationException
	 */
	@Test
	public void testFtchBudgetDetailsForNT403Success() throws ApplicationException
	{
		HashMap loHashMap = moConfigurationService.fetchBudgetDetailsForNT403(moSession, "3731");
		assertNotNull(loHashMap);
	}
	
	/**
	 * This method tests getQueueItemDelayConfig method
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFtchBudgetDetailsForNT403Exception() throws ApplicationException
	{
		moConfigurationService.fetchBudgetDetailsForNT403(null, "1035");
	}
}