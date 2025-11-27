package com.nyc.hhs.service.test.com;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.daomanager.service.UserRoleService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.AssigneeList;
import com.nyc.hhs.model.DefaultAssignment;
import com.nyc.hhs.model.TaskDetailsBean;

public class UserRoleServiceTestR5
{
	
	UserRoleService userRoleService = new UserRoleService();
	
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
	
	@Test(expected = ApplicationException.class)
	public void defaultAssignmentInformationCase1() throws ApplicationException
	{
		DefaultAssignment aoDefaultAssignmentBean = new DefaultAssignment();
		aoDefaultAssignmentBean.setIsfinancials("true");
		userRoleService.defaultAssignmentInformation(null, aoDefaultAssignmentBean);
	}
	
	@Test
	public void testDefaultAssignmentInformationCase2() throws ApplicationException
	{
		DefaultAssignment aoDefaultAssignmentBean = new DefaultAssignment();
		aoDefaultAssignmentBean.setIsfinancials("true");
		aoDefaultAssignmentBean.setEntityId("6752");
		aoDefaultAssignmentBean.setTaskLevel("Level 1");
		aoDefaultAssignmentBean.setDefaultAssignments("yes");
		aoDefaultAssignmentBean.setAssigneeUserId("agency_138");
		aoDefaultAssignmentBean.setCreatedByUserId("agency_21");
		aoDefaultAssignmentBean.setModifiedByUserId("agency_21");
		aoDefaultAssignmentBean.setAskFlag("y");
		aoDefaultAssignmentBean.setTaskType("Contract Configuration (Initial/Renewal/New Contract)");
		userRoleService.defaultAssignmentInformation(moSession, aoDefaultAssignmentBean);
	}
	
	@Test
	public void testDefaultAssignmentInformationCase3() throws ApplicationException
	{
		DefaultAssignment aoDefaultAssignmentBean = new DefaultAssignment();
		aoDefaultAssignmentBean.setIsfinancials("true");
		aoDefaultAssignmentBean.setEntityId("6752");
		aoDefaultAssignmentBean.setTaskLevel("Level 1");
		aoDefaultAssignmentBean.setAssigneeUserId("agency_138");
		aoDefaultAssignmentBean.setCreatedByUserId("agency_21");
		aoDefaultAssignmentBean.setModifiedByUserId("agency_21");
		aoDefaultAssignmentBean.setAskFlag("y");
		aoDefaultAssignmentBean.setTaskType("Contract Configuration (Initial/Renewal/New Contract)");
		userRoleService.defaultAssignmentInformation(moSession, aoDefaultAssignmentBean);
	}
	
	@Test
	public void testDefaultAssignmentInformationCase4() throws ApplicationException
	{
		DefaultAssignment aoDefaultAssignmentBean = new DefaultAssignment();
		aoDefaultAssignmentBean.setIsfinancials("true");
		aoDefaultAssignmentBean.setEntityId("6752");
		aoDefaultAssignmentBean.setTaskLevel("Level 1");
		aoDefaultAssignmentBean.setAssigneeUserId("agency_138");
		aoDefaultAssignmentBean.setCreatedByUserId("agency_21");
		aoDefaultAssignmentBean.setModifiedByUserId("agency_21");
		aoDefaultAssignmentBean.setTaskType("Contract Configuration (Initial/Renewal/New Contract)");
		userRoleService.defaultAssignmentInformation(moSession, aoDefaultAssignmentBean);
	}
	
	@Test
	public void testDefaultAssignmentInformationCase5() throws ApplicationException
	{
		DefaultAssignment aoDefaultAssignmentBean = new DefaultAssignment();
		aoDefaultAssignmentBean.setIsfinancials("true");
		aoDefaultAssignmentBean.setEntityId("6752");
		aoDefaultAssignmentBean.setTaskLevel("Level 1");
		aoDefaultAssignmentBean.setAssigneeUserId("agency_138");
		aoDefaultAssignmentBean.setCreatedByUserId("agency_21");
		aoDefaultAssignmentBean.setModifiedByUserId("agency_21");
		aoDefaultAssignmentBean.setTaskType("Contract Configuration (Initial/Renewal/New Contract)");
		aoDefaultAssignmentBean.setKeepDefault("default");
		userRoleService.defaultAssignmentInformation(moSession, aoDefaultAssignmentBean);
	}
	
	@Test
	public void testDefaultAssignmentInformationCase6() throws ApplicationException
	{
		DefaultAssignment aoDefaultAssignmentBean = new DefaultAssignment();
		aoDefaultAssignmentBean.setIsfinancials("true");
		aoDefaultAssignmentBean.setEntityId("6752");
		aoDefaultAssignmentBean.setTaskLevel("Level 1");
		aoDefaultAssignmentBean.setAssigneeUserId("agency_138");
		aoDefaultAssignmentBean.setCreatedByUserId("agency_21");
		aoDefaultAssignmentBean.setModifiedByUserId("agency_21");
		aoDefaultAssignmentBean.setAskFlag("y");
		aoDefaultAssignmentBean.setTaskType("Contract Configuration (Initial/Renewal/New Contract)");
		aoDefaultAssignmentBean.setKeepDefault("default");
		userRoleService.defaultAssignmentInformation(moSession, aoDefaultAssignmentBean);
	}
	
	@Test
	public void testDefaultAssignmentInformationCase7() throws ApplicationException
	{
		DefaultAssignment aoDefaultAssignmentBean = new DefaultAssignment();
		aoDefaultAssignmentBean.setIsfinancials("true");
		aoDefaultAssignmentBean.setEntityId("6752");
		aoDefaultAssignmentBean.setTaskLevel("Level 1");
		aoDefaultAssignmentBean.setAssigneeUserId("agency_138");
		aoDefaultAssignmentBean.setCreatedByUserId("agency_21");
		aoDefaultAssignmentBean.setModifiedByUserId("agency_21");
		aoDefaultAssignmentBean.setAskFlag("y");
		aoDefaultAssignmentBean.setTaskType("Contract Configuration (Initial/Renewal/New Contract)");
		aoDefaultAssignmentBean.setKeepDefault("default");
		userRoleService.defaultAssignmentInformation(moSession, aoDefaultAssignmentBean);
	}
	
	@Test
	public void testDefaultAssignmentInformationCase8() throws ApplicationException
	{
		DefaultAssignment aoDefaultAssignmentBean = new DefaultAssignment();
		aoDefaultAssignmentBean.setIsfinancials(null);
		aoDefaultAssignmentBean.setEntityId("6752");
		aoDefaultAssignmentBean.setTaskLevel("Level 1");
		aoDefaultAssignmentBean.setAssigneeUserId("agency_138");
		aoDefaultAssignmentBean.setCreatedByUserId("agency_21");
		aoDefaultAssignmentBean.setModifiedByUserId("agency_21");
		aoDefaultAssignmentBean.setAskFlag("y");
		aoDefaultAssignmentBean.setTaskType("Contract Configuration (Initial/Renewal/New Contract)");
		aoDefaultAssignmentBean.setKeepDefault("default");
		userRoleService.defaultAssignmentInformation(moSession, aoDefaultAssignmentBean);
	}
	
	@Test(expected = ApplicationException.class)
	public void testFetchAskAgainFlagCase1() throws ApplicationException
	{
		String asEntityId = null;
		String asTaskLevel = "1";
		String asTaskType = "Contract Configuration (Initial/Renewal/New Contract)";
		userRoleService.fetchAskAgainFlag(null, asTaskType, asTaskLevel, asEntityId);
	}
	
	@Test
	public void testFetchAskAgainFlagCase2() throws ApplicationException
	{
		String asEntityId = "6752";
		String asTaskLevel = "Level 1";
		String asTaskType = "Contract Configuration (Initial/Renewal/New Contract)";
		userRoleService.fetchAskAgainFlag(moSession, asTaskType, asTaskLevel, asEntityId);
	}
	
	@Test
	public void testFetchAskAgainFlagCase3() throws ApplicationException
	{
		String asEntityId = "6752";
		String asTaskLevel = "Level 1";
		String asTaskType = "";
		userRoleService.fetchAskAgainFlag(moSession, asTaskType, asTaskLevel, asEntityId);
	}
	
	@Test
	public void TestdefaultAssignmentInformationCase1() throws ApplicationException
	{
		List<DefaultAssignment> lodefaultAssignmentInformation = new ArrayList<DefaultAssignment>();
		DefaultAssignment loDefaultAssignmentBean = new DefaultAssignment();
		loDefaultAssignmentBean.setEntityId("6752");
		loDefaultAssignmentBean.setTaskLevel("Level 1");
		loDefaultAssignmentBean.setDefaultAssignments("yes");
		loDefaultAssignmentBean.setAssigneeUserId("agency_138");
		loDefaultAssignmentBean.setCreatedByUserId("agency_21");
		loDefaultAssignmentBean.setModifiedByUserId("agency_21");
		loDefaultAssignmentBean.setAskFlag("y");
		loDefaultAssignmentBean.setTaskType("Contract Configuration (Initial/Renewal/New Contract)");
		loDefaultAssignmentBean.setKeepDefault("default");
		lodefaultAssignmentInformation.add(loDefaultAssignmentBean);
		userRoleService.defaultAssignmentInformation(moSession, lodefaultAssignmentInformation);
	}
	
	@Test
	public void TestdefaultAssignmentInformationCase2() throws ApplicationException
	{
		List<DefaultAssignment> lodefaultAssignmentInformation = null;
		userRoleService.defaultAssignmentInformation(moSession, lodefaultAssignmentInformation);
	}
	
	@Test(expected = ApplicationException.class)
	public void TestdefaultAssignmentInformationCase3() throws ApplicationException
	{
		List<DefaultAssignment> lodefaultAssignmentInformation = new ArrayList<DefaultAssignment>();
		DefaultAssignment loDefaultAssignmentBean = new DefaultAssignment();
		loDefaultAssignmentBean.setEntityId("6752");
		loDefaultAssignmentBean.setTaskLevel("Level 1");
		loDefaultAssignmentBean.setDefaultAssignments("yes");
		loDefaultAssignmentBean.setAssigneeUserId("agency_138");
		loDefaultAssignmentBean.setCreatedByUserId("agency_21");
		loDefaultAssignmentBean.setModifiedByUserId("agency_21");
		loDefaultAssignmentBean.setAskFlag("y");
		loDefaultAssignmentBean.setTaskType("Contract Configuration (Initial/Renewal/New Contract)");
		loDefaultAssignmentBean.setKeepDefault("default");
		lodefaultAssignmentInformation.add(loDefaultAssignmentBean);
		userRoleService.defaultAssignmentInformation(null, lodefaultAssignmentInformation);
	}
	
	@Test(expected = ApplicationException.class)
	public void testGetReassigneeList() throws ApplicationException
	{
		HashMap<String, Object> aoHmap = new HashMap<String, Object>();
		aoHmap.put("taskType", "Contract Configuration (Initial/Renewal/New Contract)");
		aoHmap.put("contractId", 6494);
		aoHmap.put("taskLevel", "Level 1");
		aoHmap.put("entityId", "6752");
		userRoleService.getReassigneeList(null, aoHmap);
	}
	
	@Test
	public void testGetReassigneeListCase1() throws ApplicationException
	{
		HashMap<String, Object> aoHmap = new HashMap<String, Object>();
		aoHmap.put("taskType", "Contract Configuration (Initial/Renewal/New Contract)");
		aoHmap.put("contractId", 6494);
		aoHmap.put("taskLevel", "Level 1");
		aoHmap.put("entityId", "6752");
		List<AssigneeList> loAssigneeList = null;
		loAssigneeList = new ArrayList<AssigneeList>();
		loAssigneeList = userRoleService.getReassigneeList(moSession, aoHmap);
		assertTrue(loAssigneeList.size() > 0);
	}
	
	@Test
	public void fetchAssigneeListDetailsCase1() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		String asEntityId = "6752";
		String asTaskLevel = "1";
		String asTaskType = "Contract Configuration (Initial/Renewal/New Contract)";
		loTaskDetailsBean = userRoleService.fetchAssigneeListDetails(moSession, asEntityId, asTaskType, asTaskLevel);
		assertTrue(loTaskDetailsBean != null);
	}
	
	@Test
	public void fetchAssigneeListDetailsCase2() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		String asEntityId = "6752";
		String asTaskLevel = "1";
		String asTaskType = null;
		loTaskDetailsBean = userRoleService.fetchAssigneeListDetails(moSession, asEntityId, asTaskType, asTaskLevel);
		assertTrue(loTaskDetailsBean != null);
	}
	
	@Test(expected = ApplicationException.class)
	public void fetchAssigneeListDetailsCase3() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		String asEntityId = "6752";
		String asTaskLevel = "1";
		String asTaskType = "Contract Configuration (Initial/Renewal/New Contract)";
		loTaskDetailsBean = userRoleService.fetchAssigneeListDetails(null, asEntityId, asTaskType, asTaskLevel);
	}

	
			@Test
			public void updateReviewLevelsDetailsCase1() throws ApplicationException
			{
				Boolean lbResult = userRoleService.updateReviewLevelsDetails(moSession);
				assertTrue(!lbResult);
			}
	
			@Test(expected = ApplicationException.class)
			public void updateReviewLevelsDetailsCase2() throws ApplicationException
			{
				Boolean lbResult = userRoleService.updateReviewLevelsDetails(null);
			}
}
