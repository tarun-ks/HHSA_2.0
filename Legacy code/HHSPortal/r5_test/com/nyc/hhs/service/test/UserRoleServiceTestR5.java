package com.nyc.hhs.service.test;

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


public class UserRoleServiceTestR5 {
	
		
		UserRoleService userRoleService=new UserRoleService();
		
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
		public void testDefaultAssignmentInformationCase5() throws ApplicationException
		{   
			DefaultAssignment aoDefaultAssignmentBean = new DefaultAssignment();
			userRoleService.defaultAssignmentInformation(null,aoDefaultAssignmentBean );
		}

		
		
		@Test(expected=ApplicationException.class)
		public void testFetchAskAgainFlag() throws ApplicationException
		{
			userRoleService.fetchAskAgainFlag(moSession, null,null,null);
		}

		@Test
		public void testFetchAskAgainFlagCase1() throws ApplicationException
		{
			
			String asEntityId=null;
			String asTaskLevel="1";
			String asTaskType= "Invoice Review";
			String flag = userRoleService.fetchAskAgainFlag(moSession, asTaskType, asTaskLevel, asEntityId);
			assertTrue(flag != "");
	    }
		
		
		
		
		
		
		@Test
		public void testDefaultAssignmentInformationCase2() throws ApplicationException
		{
			
			DefaultAssignment aoDefaultAssignmentBean = new DefaultAssignment();
			userRoleService.defaultAssignmentInformation(moSession, aoDefaultAssignmentBean);
		}
		
		
		
		
		
		
		@Test
		public void testDefaultAssignmentInformationCase4() throws ApplicationException
		{
			
			List<DefaultAssignment> aoDefaultAssignmentBean = new ArrayList<DefaultAssignment>();
			userRoleService.defaultAssignmentInformation(moSession,aoDefaultAssignmentBean);
		}
		

		
		
		
		
		@Test(expected=ApplicationException.class)
		public void testGetReassigneeList() throws ApplicationException
		{
			userRoleService.getReassigneeList(moSession, null);
		}
		@Test
	    public void testGetReassigneeListCase1() throws ApplicationException
		{
		HashMap<String, Object> aoHmap = new HashMap<String, Object>();
		aoHmap.put("taskType", "Invoice Review");
		aoHmap.put("contractId",6494);
		List<AssigneeList> loAssigneeList = null;
		loAssigneeList = new ArrayList<AssigneeList>();
		loAssigneeList = userRoleService.getReassigneeList(moSession,aoHmap);
		assertTrue(loAssigneeList.size()>0);					
		}
}




