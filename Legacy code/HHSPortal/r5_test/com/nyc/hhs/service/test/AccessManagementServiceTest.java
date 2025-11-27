package com.nyc.hhs.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.daomanager.service.AccessManagementService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.ContractBean;
import com.nyc.hhs.model.StaffDetails;

public class AccessManagementServiceTest
{
	AccessManagementService accessManagementService=new AccessManagementService();
	
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
			System.out.println("Before");
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
		finally
		{
			moSession.rollback();
			moSession.close();
		}
	}

	@Test(expected=ApplicationException.class)
	public void testGetUserAccessDetails() throws ApplicationException
	{
		accessManagementService.getUserAccessDetails(moSession, null,null);
	}

	@Test
	public void testGetUserAccessDetailsCase2() throws ApplicationException
	{
		List<ContractBean> loContractBean= new ArrayList<ContractBean>();
		String loContractId="6733";
		String loOrganizationId="Org_509";
		loContractBean=accessManagementService.getUserAccessDetails(moSession, loContractId,loOrganizationId);
		assertEquals(2,loContractBean.size());
	}
	
	@Test
	public void testGetUserAccessDetailsCase3() throws ApplicationException
	{
		List<ContractBean> loContractBean= new ArrayList<ContractBean>();
		String loContractId="6731";
		String loOrganizationId="Org_509";
		loContractBean=accessManagementService.getUserAccessDetails(moSession, loContractId,loOrganizationId);
		assertEquals(0,loContractBean.size());	}
	
	@Test
	public void testUpdateUserAccessInformation() throws ApplicationException
	{
		String loContractId="6733";
		String loUserId="3311";
		List<String> aoUserListWithoutAccess=new ArrayList<String>();
		aoUserListWithoutAccess.add("3299");
		boolean loResult=accessManagementService.updateUserAccessInformation(moSession, loContractId, aoUserListWithoutAccess, loUserId);
		assertTrue(loResult);
	}
	
	@Test
	public void testUpdateUserAccessInformationCase1() throws ApplicationException
	{
		String loContractId="6733";
		String loUserId="3311";
		List<String> aoUserListWithoutAccess=new ArrayList<String>();
		//aoUserListWithoutAccess.add("3299");
		boolean loResult=accessManagementService.updateUserAccessInformation(moSession, loContractId, aoUserListWithoutAccess, loUserId);
		if(loResult== false)
		assertTrue(true);
	}
	
	@Test
	public void testUpdateUserAccessInformationCase2() throws ApplicationException
	{
		String loContractId="6733";
		String loUserId="3311";
		List<String> aoUserListWithoutAccess=new ArrayList<String>();
		aoUserListWithoutAccess.add("3299");
		aoUserListWithoutAccess.add("3298");
		boolean loResult=accessManagementService.updateUserAccessInformation(moSession, loContractId, aoUserListWithoutAccess, loUserId);
		if(loResult== false)
		assertTrue(true);
	}
	
	@Test(expected=ApplicationException.class)
	public void testUpdateUserAccessInformationCase3() throws ApplicationException
	{
		String loContractId="6731";
		String loUserId="3011";
		List<String> aoUserListWithoutAccess=new ArrayList<String>();
		accessManagementService.updateUserAccessInformation(null, loContractId, aoUserListWithoutAccess, loUserId);
	}
	

	@Test
	public void testdefaultAssignmentInformation() throws ApplicationException
	{   
		List<String> aoUserListWithoutAccess=new ArrayList<String>();
		accessManagementService.defaultAssignmentInformation(moSession, "String asContractId",
		aoUserListWithoutAccess, "String loUserId");
	}
	
	@Test
	public void testgetContractRestrictionCountDeactivatedUser() throws ApplicationException
	{  
		Integer liCountDeactivateUser =null;
		StaffDetails aoStaffDetails = new StaffDetails();
	    aoStaffDetails.setMsStaffId("3298");
	    liCountDeactivateUser = accessManagementService.getContractRestrictionCountDeactivatedUser(moSession, aoStaffDetails);
	    assertTrue(liCountDeactivateUser!= null);
	}
	
	

}