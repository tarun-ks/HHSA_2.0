package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.daomanager.service.MultiAccountService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.StaffDetails;

public class MultiAccountServiceTest
{
	MultiAccountService moMultiAccountService = new MultiAccountService();
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
	 * Tests the method to fetch user data on search of email id from database
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testSearchUserOnEmailId1() throws ApplicationException
	{
		StaffDetails loStaffDetailsBean = new StaffDetails();
		loStaffDetailsBean.setMsStaffEmail("%man%");
		List<StaffDetails> loStaffDetailsList = moMultiAccountService
				.searchUserOnEmailId(moSession, loStaffDetailsBean);
		assertNotNull(loStaffDetailsList);

	}

	/**
	 * This method tests occurring of application exception while searching
	 * email id from the database.
	 * 
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void testSearchUserOnEmailIdApplicationException() throws Exception
	{
		moMultiAccountService.searchUserOnEmailId(moSession, null);
	}

	
	@Test
	public void testGetUserOrgDetailsMultiAccountSuccess() throws ApplicationException
	{
		StaffDetails loStaffDetails = new StaffDetails();
		loStaffDetails.setMsUserDN("cn=YQASRX8F,ou=ExtUsers74,o=External");
		List<StaffDetails> loStaffDetailsList = moMultiAccountService.getUserOrgDetailsMultiAccount(moSession, loStaffDetails);
		assertNotNull(loStaffDetailsList);

	}


	@Test(expected = ApplicationException.class)
	public void testGetUserOrgDetailsMultiAccountApplicationException() throws Exception
	{
		moMultiAccountService.getUserOrgDetailsMultiAccount(moSession, null);
	}
	 

	@Test
	public void testSubmitAccessRequestProvider1() throws ApplicationException
	{
		String[] lsOrgIds =
		{ "test" };
		HashMap<String, Object> loParamMap = new HashMap<String, Object>();
		loParamMap.put("asCreatorUser", "city_43");
		loParamMap.put("asStaffId", "111");
		loParamMap.put("asOrgId", lsOrgIds);
		Integer liIntCount = moMultiAccountService.submitAccessRequestProvider(moSession, loParamMap);
		assertTrue(liIntCount > 0);

	}

	@Test
	public void testSubmitAccessRequestProvider2() throws ApplicationException
	{
		Integer liIntCount = moMultiAccountService.submitAccessRequestProvider(moSession, null);
		assertTrue(liIntCount == 0);

	}

	@Test
	public void testSubmitAccessRequestProvider3() throws ApplicationException
	{
		String[] lsOrgIds =
		{ "test" };
		HashMap<String, Object> loParamMap = new HashMap<String, Object>();
		loParamMap.put("asCreatorUser", "city_43");
		loParamMap.put("asStaffId", "111");
		loParamMap.put("asOrgId", null);
		Integer liIntCount = moMultiAccountService.submitAccessRequestProvider(moSession, loParamMap);
		assertTrue(liIntCount == 0);

	}
	
	@Test
	public void testSubmitAccessRequestProvider4() throws ApplicationException
	{
		String[] lsOrgIds =
		{ "r3_org" };
		HashMap<String, Object> loParamMap = new HashMap<String, Object>();
		loParamMap.put("asCreatorUser", "city_43");
		loParamMap.put("asStaffId", "170");
		loParamMap.put("asOrgId", lsOrgIds);
		Integer liIntCount = moMultiAccountService.submitAccessRequestProvider(moSession, loParamMap);
		assertTrue(liIntCount > 0);

	}

	@Test(expected = ApplicationException.class)
	public void testSubmitAccessRequestProviderApplicationException() throws Exception
	{
		String[] lsOrgIds =
		{ "test" };
		HashMap<String, Object> loParamMap = new HashMap<String, Object>();
		loParamMap.put("asCreatorUser", "city_43");
		loParamMap.put("asStaffId", "111");
		loParamMap.put("asOrgId", lsOrgIds);
		moMultiAccountService.submitAccessRequestProvider(null, loParamMap);
	}

	@Test
	public void testGetStaffDetailsFromId1() throws ApplicationException
	{

		List<StaffDetails> loStaffDetailsList = moMultiAccountService.getStaffDetailsFromId(moSession, "111");
		assertNotNull(loStaffDetailsList);

	}

	@Test(expected = ApplicationException.class)
	public void testGetStaffDetailsFromIdApplicationException() throws Exception
	{
		moMultiAccountService.getStaffDetailsFromId(null, null);
	}

}
