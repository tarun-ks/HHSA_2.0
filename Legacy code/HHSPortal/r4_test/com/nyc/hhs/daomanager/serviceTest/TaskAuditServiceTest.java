package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSP8Constants;
import com.nyc.hhs.daomanager.service.TaskAuditService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.cache.ICacheManager;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.util.XMLUtil;

public class TaskAuditServiceTest
{
	TaskAuditService loTaskAuditService = new TaskAuditService();
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
			ICacheManager loCacheManager = BaseCacheManagerWeb.getInstance();
			TaskAuditServiceTest loTaskAuditServiceTest = new TaskAuditServiceTest();
			Object loCacheTaskAuditObject = XMLUtil.getDomObj(loTaskAuditServiceTest.getClass().getResourceAsStream(
					HHSP8Constants.TASK_AUDIT_CONFIGURATION_XML));
			loCacheManager.putCacheObject(HHSP8Constants.TASK_AUDIT_CONFIGURATION, loCacheTaskAuditObject);
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
	 * Tests the method to insert audit for workflow launch
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertAuditForLaunchWorkflow() throws ApplicationException
	{
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		Boolean loInsertStatus = loTaskAuditService.insertAuditForLaunchWorkflow(moSession, loHhsAuditBean);
		assertFalse(loInsertStatus);
	}

	/**
	 * Tests the method to insert audit for workflow launch
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertAuditForLaunchWorkflow1() throws ApplicationException
	{
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		loHhsAuditBean.setEntityType("Contract Configuration");
		loHhsAuditBean.setUserId("city_43");
		loHhsAuditBean.setEntityId("428");
		loHhsAuditBean.setContractId("428");
		loHhsAuditBean.setWorkflowId("2B5C31B09DBACF418E5480F8FD2C0253");
		Boolean loInsertStatus = loTaskAuditService.insertAuditForLaunchWorkflow(moSession, loHhsAuditBean);
		assertTrue(loInsertStatus);
	}

	/**
	 * Tests the method to insert audit for workflow launch
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertAuditForLaunchWorkflow2() throws ApplicationException
	{
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		loHhsAuditBean.setEntityType("Contract Certification of Funds");
		loHhsAuditBean.setUserId("agency_10");
		loHhsAuditBean.setEntityId("428");
		loHhsAuditBean.setContractId("428");
		loHhsAuditBean.setWorkflowId("3C2B31B09DBACF418E5480F8FD212345");
		loHhsAuditBean.setTaskLevel("2");
		Boolean loInsertStatus = loTaskAuditService.insertAuditForLaunchWorkflow(moSession, loHhsAuditBean);
		assertTrue(loInsertStatus);
	}

	@Test
	public void testInsertAuditForLaunchWorkflow3() throws ApplicationException
	{
		Boolean loInsertStatus = loTaskAuditService.insertAuditForLaunchWorkflow(moSession, null);
		assertFalse(loInsertStatus);
	}

	@Test
	public void testInsertAuditForLaunchWorkflow4() throws ApplicationException
	{
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		Boolean loInsertStatus = loTaskAuditService.insertAuditForLaunchWorkflow(moSession, loHhsAuditBean);
		assertFalse(loInsertStatus);
	}

	@Test
	public void testInsertAuditForLaunchWorkflow5() throws ApplicationException
	{
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		loHhsAuditBean.setEntityType("Review Scores");
		Boolean loInsertStatus = loTaskAuditService.insertAuditForLaunchWorkflow(moSession, loHhsAuditBean);
		assertFalse(loInsertStatus);
	}

	@Test
	public void testInsertAuditForLaunchWorkflow6() throws ApplicationException
	{
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		loHhsAuditBean.setEntityType("Contract Configuration");
		loHhsAuditBean.setUserId("agency_14");
		loHhsAuditBean.setEntityId("428");
		loHhsAuditBean.setContractId("428");
		loHhsAuditBean.setWorkflowId("2B5C31B09DBACF418E5480F8FD2C0253");
		loHhsAuditBean.setTaskLevel("");
		Boolean loInsertStatus = loTaskAuditService.insertAuditForLaunchWorkflow(moSession, loHhsAuditBean);
		assertTrue(loInsertStatus);
	}

	@Test
	public void testInsertAuditForLaunchWorkflow7() throws ApplicationException
	{
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		loHhsAuditBean.setEntityType("Contract Configuration");
		loHhsAuditBean.setUserId("agency_14");
		loHhsAuditBean.setEntityId("428");
		loHhsAuditBean.setContractId("428");
		loHhsAuditBean.setWorkflowId("2B5C31B09DBACF418E5480F8FD2C0253");
		loHhsAuditBean.setTaskLevel("Level 2");
		Boolean loInsertStatus = loTaskAuditService.insertAuditForLaunchWorkflow(moSession, loHhsAuditBean);
		assertTrue(loInsertStatus);
	}

	@Test
	public void testInsertAuditForLaunchWorkflow8() throws ApplicationException
	{
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		loHhsAuditBean.setEntityType("Contract Configuration");
		loHhsAuditBean.setUserId("agency_14");
		loHhsAuditBean.setEntityId("428");
		loHhsAuditBean.setContractId("428");
		loHhsAuditBean.setWorkflowId("2B5C31B09DBACF418E5480F8FD2C0253");
		loHhsAuditBean.setTaskEvent("");
		Boolean loInsertStatus = loTaskAuditService.insertAuditForLaunchWorkflow(moSession, loHhsAuditBean);
		assertTrue(loInsertStatus);
	}

	@Test
	public void testInsertAuditForLaunchWorkflow9() throws ApplicationException
	{
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		loHhsAuditBean.setEntityType("Contract Configuration");
		loHhsAuditBean.setUserId("agency_14");
		loHhsAuditBean.setEntityId("428");
		loHhsAuditBean.setContractId("428");
		loHhsAuditBean.setWorkflowId("2B5C31B09DBACF418E5480F8FD2C0253");
		loHhsAuditBean.setTaskEvent("Approved and Launched");
		Boolean loInsertStatus = loTaskAuditService.insertAuditForLaunchWorkflow(moSession, loHhsAuditBean);
		assertTrue(loInsertStatus);
	}

	@Test
	public void testInsertAuditForLaunchWorkflow10() throws ApplicationException
	{
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		loHhsAuditBean.setEntityType("Contract Configuration");
		loHhsAuditBean.setUserId("agency_14");
		loHhsAuditBean.setEntityId("428");
		loHhsAuditBean.setContractId("428");
		loHhsAuditBean.setWorkflowId("2B5C31B09DBACF418E5480F8FD2C0253");
		Boolean loInsertStatus = loTaskAuditService.insertAuditForLaunchWorkflow(moSession, loHhsAuditBean);
		assertTrue(loInsertStatus);
	}

	@Test(expected = ApplicationException.class)
	public void testInsertAuditForLaunchWorkflow12() throws ApplicationException
	{
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		loHhsAuditBean.setEntityType("Contract Configuration");
		loHhsAuditBean.setEntityId("428");
		loHhsAuditBean.setContractId("428");
		loHhsAuditBean.setWorkflowId("2B5C31B09DBACF418E5480F8FD2C0253");
		loTaskAuditService.insertAuditForLaunchWorkflow(moSession, loHhsAuditBean);
	}

	/**
	 * Tests the method to insert audit for reassign and finish
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertAuditForReassignAndFinish() throws ApplicationException
	{
		Boolean loInsertStatus = loTaskAuditService.insertAuditForReassignAndFinish(moSession, null, null);
		assertFalse(loInsertStatus);
	}

	/**
	 * Tests the method to insert audit for reassign and finish
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertAuditForReassignAndFinish1() throws ApplicationException
	{
		Boolean loInsertStatus = loTaskAuditService.insertAuditForReassignAndFinish(moSession, null, true);
		assertFalse(loInsertStatus);
	}

	@Test
	public void testInsertAuditForReassignAndFinish2() throws ApplicationException
	{
		Boolean loInsertStatus = loTaskAuditService.insertAuditForReassignAndFinish(moSession, null, false);
		assertFalse(loInsertStatus);
	}

	@Test
	public void testInsertAuditForReassignAndFinish4() throws ApplicationException
	{
		moSession.rollback();
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		Boolean loInsertStatus = loTaskAuditService.insertAuditForReassignAndFinish(moSession, loTaskDetailsBean, true);
		assertFalse(loInsertStatus);
	}

	@Test
	public void testInsertAuditForReassignAndFinish5() throws ApplicationException
	{
		moSession.rollback();
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setEntityType("Evaluate Proposal Task");
		Boolean loInsertStatus = loTaskAuditService.insertAuditForReassignAndFinish(moSession, loTaskDetailsBean, true);
		assertFalse(loInsertStatus);
	}

	/**
	 * Tests the method to insert audit for reassign and finish
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertAuditForReassignAndFinish6() throws ApplicationException
	{
		moSession.rollback();
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setUserId("agency_14");
		loTaskDetailsBean.setWorkFlowId("2B5C31B09DBACF418E5480F8FD2C0253");
		loTaskDetailsBean.setEntityId("428");
		loTaskDetailsBean.setContractId("428");
		loTaskDetailsBean.setEntityType("Contract Configuration");
		loTaskDetailsBean.setLevel("1");
		loTaskDetailsBean.setReassignUserId("agency_16");
		Boolean loInsertStatus = loTaskAuditService.insertAuditForReassignAndFinish(moSession, loTaskDetailsBean, true);
		assertTrue(loInsertStatus);
	}

	@Test
	public void testInsertAuditForReassignAndFinish7() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setUserId("agency_16");
		loTaskDetailsBean.setWorkFlowId("3C2B31B09DBACF418E5480F8FD212345");
		loTaskDetailsBean.setEntityId("428");
		loTaskDetailsBean.setContractId("428");
		loTaskDetailsBean.setEntityType("Contract Certification of Funds");
		loTaskDetailsBean.setTaskStatus("Returned for Revision");
		loTaskDetailsBean.setLevel("2");
		loTaskDetailsBean.setTotalLevel("2");
		loTaskDetailsBean.setLinkedWobNum("2B5C31B09DBACF418E5480F8FD2C0253");
		Boolean loInsertStatus = loTaskAuditService.insertAuditForReassignAndFinish(moSession, loTaskDetailsBean, true);
		assertTrue(loInsertStatus);
	}

	@Test
	public void testInsertAuditForReassignAndFinish8() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setUserId("agency_16");
		loTaskDetailsBean.setWorkFlowId("3C2B31B09DBACF418E5480F8FD212345");
		loTaskDetailsBean.setEntityId("428");
		loTaskDetailsBean.setContractId("428");
		loTaskDetailsBean.setEntityType("Contract Certification of Funds");
		loTaskDetailsBean.setTaskStatus("Approved");
		loTaskDetailsBean.setLevel("2");
		loTaskDetailsBean.setTotalLevel("2");
		loTaskDetailsBean.setLinkedWobNum("2B5C31B09DBACF418E5480F8FD2C0253");
		Boolean loInsertStatus = loTaskAuditService.insertAuditForReassignAndFinish(moSession, loTaskDetailsBean, true);
		assertTrue(loInsertStatus);
	}

	/**
	 * Tests the method to insert audit for reassign and finish
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertAuditForReassignAndFinish9() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setUserId("agency_16");
		loTaskDetailsBean.setWorkFlowId("2B5C31B09DBACF418E5480F8FD2C0253");
		loTaskDetailsBean.setEntityId("428");
		loTaskDetailsBean.setContractId("428");
		loTaskDetailsBean.setEntityType("Contract Configuration");
		loTaskDetailsBean.setTaskStatus("Approved");
		loTaskDetailsBean.setLevel("1");
		loTaskDetailsBean.setTotalLevel("2");
		Boolean loInsertStatus = loTaskAuditService.insertAuditForReassignAndFinish(moSession, loTaskDetailsBean, true);
		assertFalse(loInsertStatus);
	}

	/**
	 * Tests the method to insert audit for reassign and finish
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertAuditForReassignAndFinish10() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setUserId("agency_16");
		loTaskDetailsBean.setWorkFlowId("2B5C31B09DBACF418E5480F8FD2C0253");
		loTaskDetailsBean.setEntityId("428");
		loTaskDetailsBean.setContractId("428");
		loTaskDetailsBean.setEntityType("Contract Configuration Amendment");
		loTaskDetailsBean.setTaskStatus("Approved");
		loTaskDetailsBean.setLevel("1");
		loTaskDetailsBean.setTotalLevel("2");
		Boolean loInsertStatus = loTaskAuditService.insertAuditForReassignAndFinish(moSession, loTaskDetailsBean, true);
		assertFalse(loInsertStatus);
	}

	/**
	 * Tests the method to insert audit for reassign and finish
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertAuditForReassignAndFinish11() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setUserId("agency_16");
		loTaskDetailsBean.setWorkFlowId("2B5C31B09DBACF418E5480F8FD2C0253");
		loTaskDetailsBean.setEntityId("428");
		loTaskDetailsBean.setContractId("428");
		loTaskDetailsBean.setEntityType("Contract Budget Review");
		loTaskDetailsBean.setTaskStatus("Approved");
		loTaskDetailsBean.setLevel("1");
		loTaskDetailsBean.setTotalLevel("2");
		Boolean loInsertStatus = loTaskAuditService.insertAuditForReassignAndFinish(moSession, loTaskDetailsBean, true);
		assertTrue(loInsertStatus);
	}

	/**
	 * Tests the method to insert audit for reassign and finish
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertAuditForReassignAndFinish12() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setUserId("agency_10");
		loTaskDetailsBean.setWorkFlowId("3C2B31B09DBACF418E5480F8FD212345");
		loTaskDetailsBean.setEntityId("428");
		loTaskDetailsBean.setContractId("428");
		loTaskDetailsBean.setEntityType("Contract Certification of Funds");
		loTaskDetailsBean.setLevel("1");
		loTaskDetailsBean.setTaskStatus("Returned for Revision");
		Boolean loInsertStatus = loTaskAuditService.insertAuditForReassignAndFinish(moSession, loTaskDetailsBean, true);
		assertTrue(loInsertStatus);
	}

	/**
	 * Tests the method to insert audit for reassign and finish
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertAuditForReassignAndFinish13() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setUserId("agency_10");
		loTaskDetailsBean.setWorkFlowId("3C2B31B09DBACF418E5480F8FD212345");
		loTaskDetailsBean.setEntityId("428");
		loTaskDetailsBean.setContractId("428");
		loTaskDetailsBean.setEntityType("Contract Certification of Funds");
		loTaskDetailsBean.setLevel("2");
		loTaskDetailsBean.setTaskStatus("Returned for Revision");
		loTaskDetailsBean.setLinkedWobNum("09DBACF418E5480F8FD212345");
		Boolean loInsertStatus = loTaskAuditService.insertAuditForReassignAndFinish(moSession, loTaskDetailsBean, true);
		assertTrue(loInsertStatus);
	}

	/**
	 * Tests the method to insert audit for reassign and finish
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertAuditForReassignAndFinish14() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setUserId("agency_10");
		loTaskDetailsBean.setWorkFlowId("3C2B31B09DBACF418E5480F8FD212345");
		loTaskDetailsBean.setEntityId("428");
		loTaskDetailsBean.setContractId("428");
		loTaskDetailsBean.setEntityType("Amendment Certification of Funds");
		loTaskDetailsBean.setLevel("2");
		loTaskDetailsBean.setTaskStatus("Returned for Revision");
		loTaskDetailsBean.setLinkedWobNum("09DBACF418E5480F8FD212345");
		Boolean loInsertStatus = loTaskAuditService.insertAuditForReassignAndFinish(moSession, loTaskDetailsBean, true);
		assertTrue(loInsertStatus);
	}

	@Test(expected = ApplicationException.class)
	public void testInsertAuditForReassignAndFinish15() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setWorkFlowId("3C2B31B09DBACF418E5480F8FD212345");
		loTaskDetailsBean.setEntityId("428");
		loTaskDetailsBean.setContractId("428");
		loTaskDetailsBean.setEntityType("Amendment Certification of Funds");
		loTaskDetailsBean.setLevel("2");
		loTaskDetailsBean.setTaskStatus("Returned for Revision");
		loTaskDetailsBean.setLinkedWobNum("09DBACF418E5480F8FD212345");
		loTaskAuditService.insertAuditForReassignAndFinish(moSession, loTaskDetailsBean, true);
	}

	/**
	 * Tests the method to insert audit for workflow launch
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertAuditForMultipleLaunchWorkflow() throws ApplicationException
	{
		List<HhsAuditBean> loHhsAuditBeanList = new ArrayList<HhsAuditBean>();
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		loHhsAuditBean.setEntityType("Contract Configuration");
		loHhsAuditBean.setUserId("agency_14");
		loHhsAuditBean.setEntityId("428");
		loHhsAuditBean.setContractId("428");
		loHhsAuditBean.setWorkflowId("2B5C31B09DBACF418E5480F8FD2C0253");
		loHhsAuditBean.setTaskEvent("");
		loHhsAuditBeanList.add(loHhsAuditBean);
		Boolean loInsertStatus = loTaskAuditService.insertAuditForMultipleLaunchWorkflow(moSession, loHhsAuditBeanList);
		assertTrue(loInsertStatus);
	}

	/**
	 * Tests the method to insert audit for workflow launch
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertAuditForMultipleLaunchWorkflow1() throws ApplicationException
	{
		Boolean loInsertStatus = loTaskAuditService.insertAuditForMultipleLaunchWorkflow(moSession, null);
		assertFalse(loInsertStatus);
	}

	/**
	 * Tests the method to insert audit for reassign and finish
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertAuditForMultiReassign() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setUserId("agency_10");
		loTaskDetailsBean.setWorkFlowId("3C2B31B09DBACF418E5480F8FD212345");
		loTaskDetailsBean.setEntityId("428");
		loTaskDetailsBean.setContractId("428");
		loTaskDetailsBean.setEntityType("Amendment Certification of Funds");
		loTaskDetailsBean.setLevel("2");
		loTaskDetailsBean.setTaskStatus("Returned for Revision");
		loTaskDetailsBean.setLinkedWobNum("09DBACF418E5480F8FD212345");
		List<TaskDetailsBean> loTaskDetailsBeanList = new ArrayList<TaskDetailsBean>();
		loTaskDetailsBeanList.add(loTaskDetailsBean);
		Boolean loInsertStatus = loTaskAuditService.insertAuditForMultiReassign(moSession, loTaskDetailsBeanList, true);
		assertTrue(loInsertStatus);
	}

	/**
	 * Tests the method to insert audit for reassign and finish
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertAuditForMultiReassign1() throws ApplicationException
	{
		Boolean loInsertStatus = loTaskAuditService.insertAuditForMultiReassign(moSession, null, true);
		assertFalse(loInsertStatus);
	}

	@Test(expected = ApplicationException.class)
	public void testInsertAuditForLaunchWorkflow11() throws ApplicationException
	{
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		loHhsAuditBean.setEntityType("Contract Configuration");
		Object loCacheTaskAuditObject = BaseCacheManagerWeb.getInstance().getCacheObject(
				HHSConstants.TASK_AUDIT_CONFIGURATION);
		BaseCacheManagerWeb.getInstance().putCacheObject(HHSConstants.TASK_AUDIT_CONFIGURATION, null);
		loTaskAuditService.insertAuditForLaunchWorkflow(moSession, loHhsAuditBean);
		BaseCacheManagerWeb.getInstance().putCacheObject(HHSConstants.TASK_AUDIT_CONFIGURATION, loCacheTaskAuditObject);
	}

	@Test(expected = ApplicationException.class)
	public void testInsertAuditForReassignAndFinish3() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		Object loCacheTaskAuditObject = BaseCacheManagerWeb.getInstance().getCacheObject(
				HHSConstants.TASK_AUDIT_CONFIGURATION);
		BaseCacheManagerWeb.getInstance().putCacheObject(HHSConstants.TASK_AUDIT_CONFIGURATION, null);
		loTaskAuditService.insertAuditForReassignAndFinish(moSession, loTaskDetailsBean, true);
		BaseCacheManagerWeb.getInstance().putCacheObject(HHSConstants.TASK_AUDIT_CONFIGURATION, loCacheTaskAuditObject);
	}
}
