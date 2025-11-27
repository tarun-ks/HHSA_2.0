package com.nyc.hhs.service.test.com.nyc.hhs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.daomanager.service.AutoSaveService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.AutoSaveBean;
import com.nyc.hhs.model.TempBean;

public class AutoSaveServiceTestR5
{
	AutoSaveService autoSaveService = new AutoSaveService();
	
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
	
	@Test
	public void fetchAutoSaveCase1() throws ApplicationException
	{
		AutoSaveBean loAutoSaveBean = new AutoSaveBean();
		loAutoSaveBean.setUserId("city_459");
		loAutoSaveBean.setEntityId("4522");
		loAutoSaveBean.setJspName("addprocurement");
		autoSaveService.fetchAutoSave(moSession, loAutoSaveBean);
	}
	
	@Test(expected = ApplicationException.class)
	public void fetchAutoSaveCase2() throws ApplicationException
	{
		AutoSaveBean loAutoSaveBean = new AutoSaveBean();
		loAutoSaveBean.setUserId("city_459");
		loAutoSaveBean.setEntityId("4522");
		loAutoSaveBean.setJspName("addprocurement");
		autoSaveService.fetchAutoSave(null, null);
	}
	
	@Test
	public void updateAutoSaveCase1() throws ApplicationException
	{
		AutoSaveBean loAutoSaveBean = new AutoSaveBean();
		loAutoSaveBean.setTextareaValue("test ok program progream ");
		loAutoSaveBean.setUserId("city_459");
		loAutoSaveBean.setEntityName("Procurement");
		loAutoSaveBean.setEntityId("4522");
		loAutoSaveBean.setJspName("addprocurement");
		loAutoSaveBean.setTextareaName("procurementDescription");
		loAutoSaveBean.setUserId("city_459");
		List<TempBean> loTemp = new ArrayList<TempBean>();
		loAutoSaveBean.setTempBean(loTemp);
		
		Integer loResult = autoSaveService.updateAutoSave(moSession, loAutoSaveBean);
		assertEquals(null, loResult);
	}
	
	@Test
	public void updateAutoSaveCase2() throws ApplicationException
	{
		AutoSaveBean loAutoSaveBean = new AutoSaveBean();
		loAutoSaveBean.setTextareaValue("consideration price");
		loAutoSaveBean.setUserId("agency_14");
		loAutoSaveBean.setEntityName("tasks");
		loAutoSaveBean.setEntityId("9A4DDFCCA69ED7478E20CA7E14D7EAB4");
		loAutoSaveBean.setJspName("completePsr");
		loAutoSaveBean.setTextareaName("considerationPrice");
		loAutoSaveBean.setUserId("agency_14");
		List<TempBean> loTempList = new ArrayList<TempBean>();
		TempBean loTempBean = new TempBean();
		loTempBean.setName("publicCommentArea");
		loTempBean.setValue("this is comments");
		loTempList.add(loTempBean);
		loAutoSaveBean.setTempBean(loTempList);
		Integer loResult = autoSaveService.updateAutoSave(moSession, loAutoSaveBean);
		assertTrue(loResult == 1);
	}
	
	@Test(expected = ApplicationException.class)
	public void updateAutoSaveCase3() throws ApplicationException
	{
		AutoSaveBean loAutoSaveBean = new AutoSaveBean();
		loAutoSaveBean.setTextareaValue("dfb4g45fdbfd./.,/");
		loAutoSaveBean.setUserId("dfb4g45fdbfd./.,/");
		loAutoSaveBean.setEntityName("dfb4g45fdbfd./.,/");
		loAutoSaveBean.setEntityId("dfb4g45fdbfd./.,/");
		loAutoSaveBean.setJspName("dfb4g45fdbfd./.,/");
		loAutoSaveBean.setTextareaName("dfb4g45fdbfd./.,/");
		loAutoSaveBean.setUserId("dfb4g45fdbfd./.,/");
		List<TempBean> loTempList = new ArrayList<TempBean>();
		TempBean loTempBean = new TempBean();
		loTempBean.setName("dfb4g45fdbfd./.,/");
		loTempBean.setValue("thdfb4g45fdbfd./.,/");
		loTempList.add(loTempBean);
		loAutoSaveBean.setTempBean(loTempList);
		autoSaveService.updateAutoSave(null, loAutoSaveBean);
		
	}
	
	@Test
	public void updateAutoSaveCase4() throws ApplicationException
	{
		AutoSaveBean loAutoSaveBean = new AutoSaveBean();
		loAutoSaveBean.setTextareaValue("this is first criteria ");
		loAutoSaveBean.setUserId("3311");
		loAutoSaveBean.setEntityName("Contract Budget");
		loAutoSaveBean.setEntityId("656D991EAB7C4F4783A71DF1AC596847");
		loAutoSaveBean.setJspName("contractInvoiceReviewTask");
		loAutoSaveBean.setTextareaName("internalCommentArea");
		loAutoSaveBean.setUserId("3311");
		List<TempBean> loTempList = new ArrayList<TempBean>();
		TempBean loTempBean = new TempBean();
		loTempBean.setName("internalCommentArea");
		loTempBean.setValue("this is comments");
		loTempList.add(loTempBean);
		loAutoSaveBean.setTempBean(loTempList);
		Integer loResult = autoSaveService.updateAutoSave(moSession, loAutoSaveBean);
		assertTrue(loResult == 1);
	}
	
	@Test
	public void deleteFromAutoSaveCase1() throws ApplicationException
	{
		String lsEntityId = "9A4DDFCCA69ED7478E20CA7E14D7EAB4";
		String lsEntityName = "tasks";
		Integer loResult = autoSaveService.deleteFromAutoSave(moSession, lsEntityId, lsEntityName);
		assertNotNull(loResult);
	}
	
	@Test
	public void deleteFromAutoSaveCase2() throws ApplicationException
	{
		String lsEntityId = null;
		String lsEntityName = "Contract Budget";
		Integer loResult = autoSaveService.deleteFromAutoSave(moSession, lsEntityId, lsEntityName);
		assertEquals(null, loResult);
	}
	
	@Test(expected = ApplicationException.class)
	public void deleteFromAutoSaveCase3() throws ApplicationException
	{
		String lsEntityId = "9A4DDFCCA69ED7478E20CA7E14D7EAB4";
		autoSaveService.deleteFromAutoSave(null, lsEntityId, null);
	}
	
}
