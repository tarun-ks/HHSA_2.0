package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.daomanager.service.TabLevelCommentsService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.CommentsHistoryBean;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.TaskDetailsBean;

public class TabLevelCommentsServiceTest
{

	TabLevelCommentsService moTabLevelCommentsService = new TabLevelCommentsService();
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
	 * Tests the method to fetch user user comments history for tab level audit
	 * agency from database
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void fetchAgencyTaskHistoryTabLevelTest1() throws ApplicationException
	{
		moTabLevelCommentsService.fetchAgencyTaskHistoryTabLevel(null, null);
	}

	/**
	 * Tests the method to fetch user user comments history for tab level audit
	 * agency from database
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void fetchAgencyTaskHistoryTabLevelTest2() throws ApplicationException
	{
		moTabLevelCommentsService.fetchAgencyTaskHistoryTabLevel(null, moSession);
	}

	/**
	 * Tests the method to fetch user user comments history for tab level audit
	 * agency from database
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void fetchAgencyTaskHistoryTabLevelTest3() throws ApplicationException
	{
		HashMap<String, String> aoHMApplicationAudit = new HashMap<String, String>();
		moTabLevelCommentsService.fetchAgencyTaskHistoryTabLevel(aoHMApplicationAudit, null);
	}

	/**
	 * Tests the method to fetch user user comments history for tab level audit
	 * agency from database
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void fetchAgencyTaskHistoryTabLevelTest4() throws ApplicationException
	{
		HashMap<String, String> aoHMApplicationAudit = new HashMap<String, String>();
		aoHMApplicationAudit.put("entityId", "158");
		aoHMApplicationAudit.put("eventNameComment", "Provider Comments");
		aoHMApplicationAudit.put("eventName", "Task Creation");
		aoHMApplicationAudit.put("eventType", "TLC_personnelServices275");
		aoHMApplicationAudit.put("entityType", "Contract Budget");
		List<CommentsHistoryBean> loCommentsHistoryList = moTabLevelCommentsService.fetchAgencyTaskHistoryTabLevel(
				aoHMApplicationAudit, moSession);
		assertTrue(loCommentsHistoryList.size() >= 0);
	}

	/**
	 * Tests the method to fetch user user comments history for tab level audit
	 * provider from database
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void fetchProviderTaskHistoryTabLevelTest1() throws ApplicationException
	{
		moTabLevelCommentsService.fetchProviderTaskHistoryTabLevel(null, null);
	}

	/**
	 * Tests the method to fetch user user comments history for tab level audit
	 * provider from database
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void fetchProviderTaskHistoryTabLevelTest2() throws ApplicationException
	{
		moTabLevelCommentsService.fetchProviderTaskHistoryTabLevel(null, moSession);
	}

	/**
	 * Tests the method to fetch user user comments history for tab level audit
	 * provider from database
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void fetchProviderTaskHistoryTabLevelTest3() throws ApplicationException
	{
		HashMap<String, String> aoHMApplicationAudit = new HashMap<String, String>();
		moTabLevelCommentsService.fetchProviderTaskHistoryTabLevel(aoHMApplicationAudit, null);
	}

	/**
	 * Tests the method to fetch user user comments history for tab level audit
	 * provider from database
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void fetchProviderTaskHistoryTabLevelTest4() throws ApplicationException
	{
		HashMap<String, String> aoHMApplicationAudit = new HashMap<String, String>();
		aoHMApplicationAudit.put("eventNameForAgency", "Agency Comments");
		aoHMApplicationAudit.put("entityId", "158");
		aoHMApplicationAudit.put("eventName", "Task Creation");
		aoHMApplicationAudit.put("eventType", "TLC_personnelServices275");
		aoHMApplicationAudit.put("entityType", "Contract Budget");
		aoHMApplicationAudit.put("entityTypeForAgency", "Contract Budget Review");
		List<CommentsHistoryBean> loCommentsHistoryList = moTabLevelCommentsService.fetchProviderTaskHistoryTabLevel(
				aoHMApplicationAudit, moSession);
		assertTrue(loCommentsHistoryList.size() >= 0);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchTabsToHighlihtProviderFromAgencyAudit1() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		moTabLevelCommentsService.fetchTabsToHighlihtProviderFromAgencyAudit(null, loCBGridBean);
	}

	/**
	 * This method tests positive case for
	 * fetchTabsToHighlihtProviderFromAgencyAudit method
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchTabsToHighlihtProviderFromAgencyAudit2() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setContractID("166");
		loCBGridBean.setSubBudgetID("145");
		loCBGridBean.setContractBudgetID("96");
		loCBGridBean.setInvoiceId("6");
		List<Integer> loAuditResultList = moTabLevelCommentsService.fetchTabsToHighlihtProviderFromAgencyAudit(moSession,
				loCBGridBean);
		assertNull(loAuditResultList);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testHighlightTabInsertUpdate1() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		moTabLevelCommentsService.highlightTabInsertUpdate(null, loTaskDetailsBean, loHhsAuditBean, true);
	}

	@Test
	public void testHighlightTabInsertUpdate2() throws ApplicationException
	{
		int liInsertUpdateDeleteCount = 0;
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setProviderComment(null);
		loTaskDetailsBean.setBudgetId("96");
		loTaskDetailsBean.setInvoiceId("6");
		loTaskDetailsBean.setContractId("166");
		loTaskDetailsBean.setSubBudgetId("145");
		loTaskDetailsBean.setEntityTypeTabLevel("TLC_unallocatedFunds_145");
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		loHhsAuditBean.setEntityId("6");
		loHhsAuditBean.setEntityType("TLC_unallocatedFunds_145");
		loHhsAuditBean.setAuditTableIdentifier(HHSConstants.NON_AUDIT_COMMENTS);
		loHhsAuditBean.setIsTaskScreen(false);
		loHhsAuditBean.setUserId("909");
		loHhsAuditBean.setAgencyId("DOC");
		liInsertUpdateDeleteCount = moTabLevelCommentsService.highlightTabInsertUpdate(moSession, loTaskDetailsBean,
				loHhsAuditBean, true);
		assertTrue(liInsertUpdateDeleteCount >= 0);
	}

	@Test
	public void testHighlightTabInsertUpdate3() throws ApplicationException
	{
		int liInsertUpdateDeleteCount = 0;
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setProviderComment("");
		loTaskDetailsBean.setBudgetId("96");
		loTaskDetailsBean.setInvoiceId("6");
		loTaskDetailsBean.setContractId("166");
		loTaskDetailsBean.setSubBudgetId("145");
		loTaskDetailsBean.setEntityTypeTabLevel("TLC_unallocatedFunds_145");
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		loHhsAuditBean.setEntityId("6");
		loHhsAuditBean.setEntityType("TLC_unallocatedFunds_145");
		loHhsAuditBean.setAuditTableIdentifier(HHSConstants.NON_AUDIT_COMMENTS);
		loHhsAuditBean.setIsTaskScreen(false);
		loHhsAuditBean.setUserId("909");
		loHhsAuditBean.setAgencyId("DOC");
		liInsertUpdateDeleteCount = moTabLevelCommentsService.highlightTabInsertUpdate(moSession, loTaskDetailsBean,
				loHhsAuditBean, true);
		assertTrue(liInsertUpdateDeleteCount >= 0);
	}

	@Test
	public void testHighlightTabInsertUpdate4() throws ApplicationException
	{
		int liInsertUpdateDeleteCount = 0;
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setProviderComment("");
		loTaskDetailsBean.setInternalComment("");
		loTaskDetailsBean.setBudgetId("96");
		loTaskDetailsBean.setInvoiceId("6");
		loTaskDetailsBean.setContractId("166");
		loTaskDetailsBean.setSubBudgetId("145");
		loTaskDetailsBean.setEntityTypeTabLevel("TLC_unallocatedFunds_145");
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		loHhsAuditBean.setEntityId("6");
		loHhsAuditBean.setEntityType("TLC_unallocatedFunds_145");
		loHhsAuditBean.setAuditTableIdentifier(HHSConstants.NON_AUDIT_COMMENTS);
		loHhsAuditBean.setIsTaskScreen(false);
		loHhsAuditBean.setUserId("909");
		loHhsAuditBean.setAgencyId("DOC");
		liInsertUpdateDeleteCount = moTabLevelCommentsService.highlightTabInsertUpdate(moSession, loTaskDetailsBean,
				loHhsAuditBean, true);
		assertTrue(liInsertUpdateDeleteCount >= 0);
	}

	@Test
	public void testHighlightTabInsertUpdate5() throws ApplicationException
	{
		int liInsertUpdateDeleteCount = 0;
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setProviderComment("Temp");
		loTaskDetailsBean.setBudgetId("96");
		loTaskDetailsBean.setInvoiceId("6");
		loTaskDetailsBean.setContractId("166");
		loTaskDetailsBean.setSubBudgetId("145");
		loTaskDetailsBean.setEntityTypeTabLevel("TLC_unallocatedFunds_145");
		loTaskDetailsBean.setUserId("909");
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		loHhsAuditBean.setEntityId("6");
		loHhsAuditBean.setEntityType("TLC_unallocatedFunds_145");
		loHhsAuditBean.setAuditTableIdentifier(HHSConstants.NON_AUDIT_COMMENTS);
		loHhsAuditBean.setIsTaskScreen(false);
		loHhsAuditBean.setUserId("909");
		loHhsAuditBean.setAgencyId("DOC");
		moSession.rollback();
		liInsertUpdateDeleteCount = moTabLevelCommentsService.highlightTabInsertUpdate(moSession, loTaskDetailsBean,
				loHhsAuditBean, true);
		assertNotNull(liInsertUpdateDeleteCount);
	}

	@Test
	public void testHighlightTabInsertUpdate6() throws ApplicationException
	{
		int liInsertUpdateDeleteCount = 0;
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setInternalComment("Temp");
		loTaskDetailsBean.setBudgetId("96");
		loTaskDetailsBean.setInvoiceId("6");
		loTaskDetailsBean.setContractId("166");
		loTaskDetailsBean.setSubBudgetId("145");
		loTaskDetailsBean.setUserId("909");
		loTaskDetailsBean.setEntityTypeTabLevel("TLC_unallocatedFunds_145");
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		loHhsAuditBean.setEntityId("6");
		loHhsAuditBean.setEntityType("TLC_unallocatedFunds_145");
		loHhsAuditBean.setAuditTableIdentifier(HHSConstants.NON_AUDIT_COMMENTS);
		loHhsAuditBean.setIsTaskScreen(false);
		loHhsAuditBean.setUserId("909");
		loHhsAuditBean.setAgencyId("DOC");
		moSession.rollback();
		liInsertUpdateDeleteCount = moTabLevelCommentsService.highlightTabInsertUpdate(moSession, loTaskDetailsBean,
				loHhsAuditBean, true);
		assertNotNull(liInsertUpdateDeleteCount);
	}

	@Test
	public void testHighlightTabInsertUpdate7() throws ApplicationException
	{
		int liInsertUpdateDeleteCount = 0;
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setInternalComment("");
		loTaskDetailsBean.setBudgetId("96");
		loTaskDetailsBean.setInvoiceId("6");
		loTaskDetailsBean.setContractId("166");
		loTaskDetailsBean.setSubBudgetId("145");
		loTaskDetailsBean.setUserId("909");
		loTaskDetailsBean.setEntityTypeTabLevel("TLC_unallocatedFunds_145");
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		loHhsAuditBean.setEntityId("6");
		loHhsAuditBean.setEntityType("TLC_unallocatedFunds_145");
		loHhsAuditBean.setAuditTableIdentifier(HHSConstants.NON_AUDIT_COMMENTS);
		loHhsAuditBean.setIsTaskScreen(false);
		loHhsAuditBean.setUserId("909");
		loHhsAuditBean.setAgencyId("DOC");
		liInsertUpdateDeleteCount = moTabLevelCommentsService.highlightTabInsertUpdate(moSession, loTaskDetailsBean,
				loHhsAuditBean, true);
		assertNotNull(liInsertUpdateDeleteCount);
	}

	@Test
	public void testHighlightTabInsertUpdate8() throws ApplicationException
	{
		int liInsertUpdateDeleteCount = 0;
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setInternalComment(null);
		loTaskDetailsBean.setBudgetId("96");
		loTaskDetailsBean.setInvoiceId("6");
		loTaskDetailsBean.setContractId("166");
		loTaskDetailsBean.setSubBudgetId("145");
		loTaskDetailsBean.setUserId("909");
		loTaskDetailsBean.setEntityTypeTabLevel("TLC_unallocatedFunds_145");
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		loHhsAuditBean.setEntityId("6");
		loHhsAuditBean.setEntityType("TLC_unallocatedFunds_145");
		loHhsAuditBean.setAuditTableIdentifier(HHSConstants.NON_AUDIT_COMMENTS);
		loHhsAuditBean.setIsTaskScreen(false);
		loHhsAuditBean.setUserId("909");
		loHhsAuditBean.setAgencyId("DOC");
		liInsertUpdateDeleteCount = moTabLevelCommentsService.highlightTabInsertUpdate(moSession, loTaskDetailsBean,
				loHhsAuditBean, true);
		assertNotNull(liInsertUpdateDeleteCount);
	}

	@Test
	public void testHighlightTabInsertUpdate9() throws ApplicationException
	{
		int liInsertUpdateDeleteCount = 0;
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setProviderComment("Temp");
		loTaskDetailsBean.setInternalComment("Temp");
		loTaskDetailsBean.setBudgetId("96");
		loTaskDetailsBean.setEntityTypeTabLevel("TLC_unallocatedFunds_145");
		loTaskDetailsBean.setInvoiceId("6");
		loTaskDetailsBean.setContractId("166");
		loTaskDetailsBean.setSubBudgetId("145");
		loTaskDetailsBean.setUserId("909");
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		loHhsAuditBean.setEntityId("6");
		loHhsAuditBean.setEntityType("TLC_unallocatedFunds_145");
		loHhsAuditBean.setAuditTableIdentifier(HHSConstants.NON_AUDIT_COMMENTS);
		loHhsAuditBean.setIsTaskScreen(false);
		loHhsAuditBean.setUserId("909");
		loHhsAuditBean.setAgencyId("DOC");
		liInsertUpdateDeleteCount = moTabLevelCommentsService.highlightTabInsertUpdate(moSession, loTaskDetailsBean,
				loHhsAuditBean, true);
		assertNotNull(liInsertUpdateDeleteCount);
	}
	
	
	/**
	 * Tests the method to fetch user user comments for tab level audit provider from database
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void fetchUserCommentsForTabLevelAuditProviderTest1() throws ApplicationException
	{
		HhsAuditBean aoHhsAuditBean = new HhsAuditBean();
		aoHhsAuditBean.setEntityType("");
		List<HhsAuditBean> loAuditList = moTabLevelCommentsService.fetchUserCommentsForTabLevelAuditProvider(null, null, aoHhsAuditBean, null, null);
	}
	
	/**
	 * Tests the method to fetch user user comments for tab level audit provider from database
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void fetchUserCommentsForTabLevelAuditProviderTest2() throws ApplicationException
	{
		HhsAuditBean aoHhsAuditBean = new HhsAuditBean();
		aoHhsAuditBean.setEntityType("Temp");
		aoHhsAuditBean.setEntityId("6");
		List<HhsAuditBean> aoAuditList = new ArrayList<HhsAuditBean>();
		HashMap loHMWFRequiredProps = new HashMap<String, String>();
		loHMWFRequiredProps.put("contractId", "1");
		loHMWFRequiredProps.put("budgetId", "1");
		List<HhsAuditBean> loAuditList = moTabLevelCommentsService.fetchUserCommentsForTabLevelAuditProvider(moSession, aoAuditList, aoHhsAuditBean, null, loHMWFRequiredProps);
		assertNotNull(loAuditList);
	}
	
	
	/**
	 * Tests the method to fetch user user comments for tab level audit agency from database
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void fetchUserCommentsForTabLevelAuditAgencyTest1() throws ApplicationException
	{
		HhsAuditBean aoHhsAuditBean = new HhsAuditBean();
		aoHhsAuditBean.setEntityType("");
		List<HhsAuditBean> loAuditList = moTabLevelCommentsService.fetchUserCommentsForTabLevelAuditAgency(null, null, null);
	}
	
	/**
	 * Tests the method to fetch user user comments for tab level audit provider from database
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void fetchUserCommentsForTabLevelAuditAgencyTest2() throws ApplicationException
	{
		HhsAuditBean aoHhsAuditBean = new HhsAuditBean();
		aoHhsAuditBean.setEntityType("Temp");
		aoHhsAuditBean.setEntityId("6");
		List<HhsAuditBean> aoAuditList = new ArrayList<HhsAuditBean>();
		List<HhsAuditBean> loAuditList = moTabLevelCommentsService.fetchUserCommentsForTabLevelAuditAgency(moSession, null, null);
		assertNull(loAuditList);
	}
}
