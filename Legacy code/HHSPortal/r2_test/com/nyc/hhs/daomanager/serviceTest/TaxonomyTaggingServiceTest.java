package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.AssertTrue;

import org.apache.ibatis.session.SqlSession;
import org.jdom.Document;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.daomanager.service.TaxonomyService;
import com.nyc.hhs.daomanager.service.TaxonomyTaggingService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.cache.ICacheManager;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.model.TaxonomyModifiersBean;
import com.nyc.hhs.model.TaxonomyTaggingBean;
import com.nyc.hhs.model.TaxonomyTaggingTree;
import com.nyc.hhs.model.TaxonomyTree;
import com.nyc.hhs.util.TaxonomyDOMUtil;

public class TaxonomyTaggingServiceTest
{
	TaxonomyTaggingService moTaxonomyTaggingService = new TaxonomyTaggingService();
	private static SqlSession moMyBatisSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
	private TaxonomyTaggingBean moTaxonomyTaggingBean= new TaxonomyTaggingBean() ;
	private List<TaxonomyTaggingBean> aoTaxonomyTaggingList= new ArrayList<TaxonomyTaggingBean>();
	private String msModifier="50,10143";
	private Map moMap= new HashMap();
	private String mscontractId="634";
	private String msproposalId="null";
	private String msprocurementId="null"; 
	static private ICacheManager loCacheManager;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		Boolean lbThrown = false;
		try
		{
			 loCacheManager = BaseCacheManagerWeb.getInstance();
			//moMyBatisSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
			Channel loChannelObj = new Channel();
			TaxonomyService loTaxonomyService = new TaxonomyService();
			List<TaxonomyTree> loTaxonomyList = loTaxonomyService.getTaxonomyMaster(moMyBatisSession);
			TaxonomyDOMUtil loTaxonomyDOM = new TaxonomyDOMUtil();
			Document loTaxonomyDom = loTaxonomyDOM.createTaxonomyDOMObj(loTaxonomyList);
			loCacheManager.putCacheObject(ApplicationConstants.TAXONOMY_ELEMENT, loTaxonomyDom);
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}
	
	@SuppressWarnings("unused")
	public void setTaxonomyInCache(ICacheManager aoCacheManager, String asKey) throws ApplicationException
	{
		try
		{
			if (asKey == null)
			{
				asKey = ApplicationConstants.TAXONOMY_ELEMENT;
			}
			Channel loChannelObj = new Channel();
			TaxonomyService loTaxonomyService = new TaxonomyService();
			List<TaxonomyTree> loTaxonomyList = loTaxonomyService.getTaxonomyMaster(moMyBatisSession);
			TaxonomyDOMUtil loTaxonomyDOM = new TaxonomyDOMUtil();
			Document loTaxonomyDom = loTaxonomyDOM.createTaxonomyDOMObj(loTaxonomyList);
			aoCacheManager.putCacheObject(asKey, loTaxonomyDom);
		}
		catch (ApplicationException aoError)
		{
			throw new ApplicationException("Error occured while creating Taxonomy DOM Object Cache", aoError);
		}
	}
	

	@Test
	public void testGetTaxonomyTaggingList0() throws ApplicationException
	{
		Map<String, Object> loMap = new HashMap<String, Object>();
		loMap.put("procurementId", "164");
		loMap.put("proposalId", "149");
		List<TaxonomyTaggingBean> loList = moTaxonomyTaggingService.getTaxonomyTaggingList(moMyBatisSession, loMap);
		assertNotNull(loList);
	}

	@Test(expected = ApplicationException.class)
	public void testGetTaxonomyTaggingList1() throws ApplicationException
	{
		Map<String, Object> loMap = new HashMap<String, Object>();
		loMap.put("procurementId", "797");
		loMap.put("proposalId", "306");
		moTaxonomyTaggingService.getTaxonomyTaggingList(null, loMap);
	}


	@Test(expected = Exception.class)
	public void testDeleteTaxonomyTags0() throws ApplicationException
	{
		TaxonomyTaggingBean loBean = new TaxonomyTaggingBean();
		loBean.setModifyByUserId("city_142");
		loBean.setTaxonomyTaggingId("409");
		moTaxonomyTaggingService.deleteTaxonomyTags(null, loBean);
	}

	@Test(expected = ApplicationException.class)
	public void testDeleteTaxonomyTags1() throws ApplicationException
	{
		TaxonomyTaggingBean loBean = new TaxonomyTaggingBean();
		loBean.setModifyByUserId("city_142");
		loBean.setTaxonomyTaggingId("409");
		int liRows = moTaxonomyTaggingService.deleteTaxonomyTags(null, loBean);
		assertEquals(1, liRows);
		
	}


	
	@Test
	public void testDeleteTaxonomyTags2() throws ApplicationException
	{
		int i =moTaxonomyTaggingService.deleteTaxonomyTags(moMyBatisSession, null);
		assertEquals(i,0);
	}

	
	
	
	
	@SuppressWarnings("unused")
	@Test
	public void testGetProcurementProposalDetails0() throws ApplicationException
	{
		TaxonomyTaggingBean aoTaxonomyTaggingBean = new TaxonomyTaggingBean();
		aoTaxonomyTaggingBean.setProposalStatusId("20");
		aoTaxonomyTaggingBean.setContractStatusId("58");
		aoTaxonomyTaggingBean.setProposalId("7");
		aoTaxonomyTaggingBean.setProcurementContractTitle("fdhgdf");
		List<TaxonomyTaggingBean> loProcurementProposalList = moTaxonomyTaggingService.getProcurementProposalDetails(
				moMyBatisSession, aoTaxonomyTaggingBean);
	}

	@Test
	public void testGetProcurementProposalDetails1() throws ApplicationException
	{
		TaxonomyTaggingBean aoTaxonomyTaggingBean = new TaxonomyTaggingBean();
		aoTaxonomyTaggingBean.setProposalStatusId("20");
		aoTaxonomyTaggingBean.setContractStatusId("58");
		aoTaxonomyTaggingBean.setProposalId("7");
		aoTaxonomyTaggingBean.setProcurementContractTitle("");
		List<TaxonomyTaggingBean> loProcurementProposalList = moTaxonomyTaggingService.getProcurementProposalDetails(
				moMyBatisSession, aoTaxonomyTaggingBean);
		assertNotNull(loProcurementProposalList);
	}

	@Test
	public void testGetProcurementProposalDetails2() throws ApplicationException
	{
		TaxonomyTaggingBean aoTaxonomyTaggingBean = new TaxonomyTaggingBean();
		aoTaxonomyTaggingBean.setProposalStatusId("20");
		aoTaxonomyTaggingBean.setContractStatusId("58");
		aoTaxonomyTaggingBean.setProposalId("7");
		aoTaxonomyTaggingBean.setProcurementContractTitle(null);
		List<TaxonomyTaggingBean> loProcurementProposalList = moTaxonomyTaggingService.getProcurementProposalDetails(
				moMyBatisSession, aoTaxonomyTaggingBean);
		assertNotNull(loProcurementProposalList);
	}

	@Test
	public void testGetProcurementProposalDetails3() throws ApplicationException
	{
		TaxonomyTaggingBean aoTaxonomyTaggingBean = new TaxonomyTaggingBean();
		aoTaxonomyTaggingBean.setProposalStatusId("20");
		aoTaxonomyTaggingBean.setContractStatusId("58");
		aoTaxonomyTaggingBean.setProposalId("");
		aoTaxonomyTaggingBean.setProposalTitle("Bprop");
		aoTaxonomyTaggingBean.setProcurementContractTitle("fdhgdf");
		List<TaxonomyTaggingBean> loProcurementProposalList = moTaxonomyTaggingService.getProcurementProposalDetails(
				moMyBatisSession, aoTaxonomyTaggingBean);
		assertNotNull(loProcurementProposalList);
	}

	@Test
	public void testGetProcurementProposalDetails4() throws ApplicationException
	{
		TaxonomyTaggingBean aoTaxonomyTaggingBean = new TaxonomyTaggingBean();
		aoTaxonomyTaggingBean.setProposalStatusId("20");
		aoTaxonomyTaggingBean.setContractStatusId("58");
		aoTaxonomyTaggingBean.setProposalId(null);
		aoTaxonomyTaggingBean.setProcurementContractTitle("fdhgdf");
		List<TaxonomyTaggingBean> loProcurementProposalList = moTaxonomyTaggingService.getProcurementProposalDetails(
				moMyBatisSession, aoTaxonomyTaggingBean);
		assertNotNull(loProcurementProposalList);

	}

	@Test(expected = ApplicationException.class)
	public void testGetProcurementProposalDetails5() throws ApplicationException
	{
		TaxonomyTaggingBean aoTaxonomyTaggingBean = new TaxonomyTaggingBean();
		aoTaxonomyTaggingBean.setProposalStatusId("20");
		aoTaxonomyTaggingBean.setContractStatusId("58");
		aoTaxonomyTaggingBean.setProposalId("7");
		aoTaxonomyTaggingBean.setProcurementContractTitle("fdhgdf");
		moTaxonomyTaggingService.getProcurementProposalDetails(null, aoTaxonomyTaggingBean);
	}

	@Test(expected = NullPointerException.class)
	public void testGetProcurementProposalDetails6() throws ApplicationException
	{
		TaxonomyTaggingBean aoTaxonomyTaggingBean = new TaxonomyTaggingBean();
		aoTaxonomyTaggingBean.setProposalStatusId("20");
		aoTaxonomyTaggingBean.setContractStatusId("58");
		aoTaxonomyTaggingBean.setProposalId("7");
		aoTaxonomyTaggingBean.setProcurementContractTitle("fdhgdf");
		moTaxonomyTaggingService.getProcurementProposalDetails(moMyBatisSession, null);

	}

	@Test(expected = ApplicationException.class)
	public void testGetProcurementProposalDetails7() throws ApplicationException
	{
		TaxonomyTaggingBean aoTaxonomyTaggingBean = new TaxonomyTaggingBean();
		aoTaxonomyTaggingBean.setProposalStatusId(null);
		aoTaxonomyTaggingBean.setContractStatusId("58");
		aoTaxonomyTaggingBean.setProposalId("");
		aoTaxonomyTaggingBean.setProcurementContractTitle("fdhgdf");
		moTaxonomyTaggingService.getProcurementProposalDetails(moMyBatisSession, aoTaxonomyTaggingBean);
	}

	@Test(expected = ApplicationException.class)
	public void testGetProcurementProposalDetails8() throws ApplicationException
	{
		TaxonomyTaggingBean aoTaxonomyTaggingBean = new TaxonomyTaggingBean();
		aoTaxonomyTaggingBean.setProposalStatusId("20");
		aoTaxonomyTaggingBean.setContractStatusId(null);
		aoTaxonomyTaggingBean.setProposalId("7");
		aoTaxonomyTaggingBean.setProcurementContractTitle("fdhgdf");
		aoTaxonomyTaggingBean.setProposalTitle(null);
		moTaxonomyTaggingService.getProcurementProposalDetails(moMyBatisSession, aoTaxonomyTaggingBean);

	}

	@Test(expected = ApplicationException.class)
	public void testGetProcurementProposalDetails9() throws ApplicationException
	{
		TaxonomyTaggingBean aoTaxonomyTaggingBean = new TaxonomyTaggingBean();
		aoTaxonomyTaggingBean.setProposalStatusId("20");
		aoTaxonomyTaggingBean.setContractStatusId(null);
		aoTaxonomyTaggingBean.setProposalId("7");
		aoTaxonomyTaggingBean.setProcurementContractTitle("fdhgdf");
		aoTaxonomyTaggingBean.setProposalTitle("");
		moTaxonomyTaggingService.getProcurementProposalDetails(moMyBatisSession, aoTaxonomyTaggingBean);

	}

	@Test
	public void testSselectProcurementRecordCount0() throws ApplicationException
	{
		int liRowCount = 0;
		TaxonomyTaggingBean aoTaxonomyTaggingBean = new TaxonomyTaggingBean();
		aoTaxonomyTaggingBean.setProposalTitle("sdgsd");
		aoTaxonomyTaggingBean.setProcurementContractTitle("fdhgdf");
		liRowCount = moTaxonomyTaggingService.selectProcurementRecordCount(moMyBatisSession, aoTaxonomyTaggingBean);
		assertNotNull(liRowCount);

	}

	@Test
	public void testSelectProcurementRecordCount1() throws ApplicationException
	{
		int liRowCount = 0;
		TaxonomyTaggingBean aoTaxonomyTaggingBean = new TaxonomyTaggingBean();
		aoTaxonomyTaggingBean.setProposalTitle(null);
		aoTaxonomyTaggingBean.setProcurementContractTitle(null);
		moTaxonomyTaggingService.selectProcurementRecordCount(moMyBatisSession, aoTaxonomyTaggingBean);
		assertNotNull(liRowCount);

	}

	@Test
	public void testSelectProcurementRecordCount2() throws ApplicationException
	{
		int liRowCount = 0;
		TaxonomyTaggingBean aoTaxonomyTaggingBean = new TaxonomyTaggingBean();
		aoTaxonomyTaggingBean.setProposalTitle("");
		aoTaxonomyTaggingBean.setProcurementContractTitle(null);
		moTaxonomyTaggingService.selectProcurementRecordCount(moMyBatisSession, aoTaxonomyTaggingBean);
		assertNotNull(liRowCount);

	}

	@Test
	public void testSelectProcurementRecordCount3() throws ApplicationException
	{
		int liRowCount = 0;
		TaxonomyTaggingBean aoTaxonomyTaggingBean = new TaxonomyTaggingBean();
		aoTaxonomyTaggingBean.setProposalTitle("sdgsd");
		aoTaxonomyTaggingBean.setProcurementContractTitle(null);
		liRowCount = moTaxonomyTaggingService.selectProcurementRecordCount(moMyBatisSession, aoTaxonomyTaggingBean);
		assertNotNull(liRowCount);
	}

	@Test
	public void testSelectProcurementRecordCount4() throws ApplicationException
	{
		int liRowCount = 0;
		TaxonomyTaggingBean aoTaxonomyTaggingBean = new TaxonomyTaggingBean();
		aoTaxonomyTaggingBean.setProposalTitle("sdgsd");
		aoTaxonomyTaggingBean.setProcurementContractTitle("");
		liRowCount = moTaxonomyTaggingService.selectProcurementRecordCount(moMyBatisSession, aoTaxonomyTaggingBean);
		assertNotNull(liRowCount);

	}

	@Test(expected = NullPointerException.class)
	public void testSelectProcurementRecordCount5() throws ApplicationException
	{
		TaxonomyTaggingBean aoTaxonomyTaggingBean = new TaxonomyTaggingBean();
		aoTaxonomyTaggingBean.setProposalTitle("sdgsd");
		aoTaxonomyTaggingBean.setProcurementContractTitle("fdhgdf");
		moTaxonomyTaggingService.selectProcurementRecordCount(moMyBatisSession, null);
	}

	@Test(expected = ApplicationException.class)
	public void testSelectProcurementRecordCount6() throws ApplicationException
	{
		TaxonomyTaggingBean aoTaxonomyTaggingBean = new TaxonomyTaggingBean();
		aoTaxonomyTaggingBean.setProposalTitle("sdgsd");
		aoTaxonomyTaggingBean.setProcurementContractTitle("fdhgdf");
		moTaxonomyTaggingService.selectProcurementRecordCount(null, aoTaxonomyTaggingBean);
	}

	

	@Test
	public void rollback() throws ApplicationException
	{
		if (moMyBatisSession != null)
			moMyBatisSession.rollback();
	}
	@Test(expected = ApplicationException.class)
	public void testTaxonomyTaggingServicegetTaxonomyTaggingList1Negative() throws ApplicationException
	{
		TaxonomyTaggingService loTaxonomyTaggingService = new TaxonomyTaggingService();
		loTaxonomyTaggingService.getTaxonomyTaggingList(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaxonomyTaggingServicedeleteTaxonomyTags4Negative() throws ApplicationException
	{
		TaxonomyTaggingService loTaxonomyTaggingService = new TaxonomyTaggingService();
		loTaxonomyTaggingService.deleteTaxonomyTags(null, null);
	}

	
	@Test
	public void testGetTaxonomyTaggingList() throws ApplicationException 
	{
		moMap.put("contractId", mscontractId);
		moMap.put("proposalId", msproposalId);
		moMap.put("procurementId", msprocurementId);
		List<TaxonomyTaggingBean> loProcurementProposalList = moTaxonomyTaggingService.getTaxonomyTaggingList(moMyBatisSession, moMap);
		assertTrue(loProcurementProposalList.size()>0);
	}	
	
	


}





