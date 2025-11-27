package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.daomanager.service.PersonalizedRoadmapService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;

public class PersonalizedRoadmapServiceTest
{
	PersonalizedRoadmapService moPersonalizedRoadmapService = new PersonalizedRoadmapService();
	private static SqlSession moSession = null; // SQL Session

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
			moSession.rollback();
			moSession.close();
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	/**
	 * Tests the method to save favorites on road map
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testSaveFavorites1() throws ApplicationException
	{
		Boolean lbSuccess = moPersonalizedRoadmapService.saveFavorites(moSession, "111", "623", "accenture");
		assertTrue(lbSuccess);
	}

	/**
	 * Tests the method to save favorites on road map
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testSaveFavorites2() throws ApplicationException
	{
		Boolean lbSuccess = moPersonalizedRoadmapService.saveFavorites(moSession, "113", "623", "accenture");
		assertTrue(lbSuccess);
	}

	/**
	 * Tests the method to save favorites on road map
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testSaveFavorites3() throws ApplicationException
	{
		Boolean lbSuccess = moPersonalizedRoadmapService.saveFavorites(moSession, "", "623", "accenture");
		assertFalse(lbSuccess);
	}

	/**
	 * This method tests occurring of application exception while saving
	 * favorite procurements into the database.
	 * 
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void testSaveFavoritesApplicationException() throws Exception
	{
		moPersonalizedRoadmapService.saveFavorites(moSession, "111", "623", null);
	}

	/**
	 * Tests the method to delete favorites on road map
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testdeleteFavorites1() throws ApplicationException
	{
		Boolean lbSuccess = moPersonalizedRoadmapService.deleteFavorites(moSession, "113", "623", "accenture", true);
		assertTrue(lbSuccess);
	}

	/**
	 * Tests the method to delete favorites on road map
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testdeleteFavorites2() throws ApplicationException
	{
		Boolean lbSuccess = moPersonalizedRoadmapService.deleteFavorites(moSession, "111", "623", "accenture", true);
		assertTrue(lbSuccess);
	}

	/**
	 * Tests the method to delete favorites on road map
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testdeleteFavorites3() throws ApplicationException
	{
		Boolean lbSuccess = moPersonalizedRoadmapService.deleteFavorites(moSession, "", "623", "accenture", false);
		assertFalse(lbSuccess);
	}

	/**
	 * This method tests occurring of application exception while deleting
	 * favorite procurements into the database.
	 * 
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void testdeleteFavoritesApplicationException() throws Exception
	{
		moPersonalizedRoadmapService.deleteFavorites(moSession, "111", null, null, true);
	}
	
	@Test(expected = java.lang.Exception.class)
	public void testPersonalizedRoadmapServicesaveFavorites0Negative()
	{
		PersonalizedRoadmapService loPersonalizedRoadmapService = new PersonalizedRoadmapService();
		try
		{
			loPersonalizedRoadmapService.saveFavorites(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testPersonalizedRoadmapServicedeleteFavorites1Negative()
	{
		PersonalizedRoadmapService loPersonalizedRoadmapService = new PersonalizedRoadmapService();
		try
		{
			loPersonalizedRoadmapService.deleteFavorites(null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

}