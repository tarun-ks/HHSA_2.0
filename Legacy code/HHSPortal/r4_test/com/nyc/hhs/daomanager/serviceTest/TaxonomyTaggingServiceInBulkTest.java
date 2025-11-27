package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.jdom.Document;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.daomanager.service.TaxonomyService;
import com.nyc.hhs.daomanager.service.TaxonomyTaggingServiceInBulk;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.ICacheManager;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.model.TaxonomyTaggingBean;
import com.nyc.hhs.model.TaxonomyTree;
import com.nyc.hhs.util.TaxonomyDOMUtil;

public class TaxonomyTaggingServiceInBulkTest
{
	TaxonomyTaggingServiceInBulk moTaxonomyTaggingServiceInBulk = new TaxonomyTaggingServiceInBulk();
	private static SqlSession moSession = null; // SQL Session
	private final List<String> moServiceIdArray = new ArrayList<String>();
	private final List<String> moModifierIdsArray= new ArrayList<String>();
	private final List<String> moContractIdBulkArray= new ArrayList<String>();
	private final List<String> moProposalIdBulkArray= new ArrayList<String>();
	private final List<String> moProcurementIdBulkArray= new ArrayList<String>();
	private final List<String> moTaxonomyTaggingIdBulkArray= new ArrayList<String>();
	private final String msUserId="city_43";
	private final String msNull="null";
	private final String msContractId="634";
	private final String msProposalId="107";
	private final String msProcurementId="131";
	private final String msServiceId1="139";
	private final String msServiceId2="1099";
	private final String msModifierId1="49";
	private final String msModifierId2="47";
	private final String mstaxonomytagging="1361";
	private final String msUserId1 = "agency_12";
	private final String msTaxTaggingId = "746";
	private final String msTaxTaggingId2 = "-";
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
			moSession.close();
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
			List<TaxonomyTree> loTaxonomyList = loTaxonomyService.getTaxonomyMaster(moSession);
			TaxonomyDOMUtil loTaxonomyDOM = new TaxonomyDOMUtil();
			Document loTaxonomyDom = loTaxonomyDOM.createTaxonomyDOMObj(loTaxonomyList);
			aoCacheManager.putCacheObject(asKey, loTaxonomyDom);
		}
		catch (ApplicationException aoError)
		{
			throw new ApplicationException("Error occured while creating Taxonomy DOM Object Cache", aoError);
		}
	}

	private TaxonomyTaggingBean taxonomyTaggingContractProcIdInfo()
	{
		TaxonomyTaggingBean loTaxonomyTaggingBean = new TaxonomyTaggingBean();
		loTaxonomyTaggingBean.setTaxonomyTaggingId(null);
		loTaxonomyTaggingBean.setModifyByUserId(msUserId);
		loTaxonomyTaggingBean.setCreatedByUserId(msUserId);
		loTaxonomyTaggingBean.setElementId("10180");
		ArrayList<String> loProposalIdList = new ArrayList<String>();
		loProposalIdList.add(msProposalId);
		loTaxonomyTaggingBean.setProposalIdList(loProposalIdList);
		ArrayList<String> loProcurementIdList = new ArrayList<String>();
		loProcurementIdList.add(msProcurementId);
		loTaxonomyTaggingBean.setProcurementIdList(loProcurementIdList);
		ArrayList<String> loContractIdList = new ArrayList<String>();
		loContractIdList.add(null);
		loTaxonomyTaggingBean.setContractIdList(loContractIdList);
		return loTaxonomyTaggingBean;
	}

	@Test
	public void testRemoveAllTaxonomyTaggingDetailsInBulk() throws ApplicationException
	{
		TaxonomyTaggingBean loTaxonomyTaggingBean = taxonomyTaggingContractProcIdInfo();
		boolean lbStatus = moTaxonomyTaggingServiceInBulk.removeAllTaxonomyTaggingDetailsInBulk(moSession,
				loTaxonomyTaggingBean);
		assertTrue(lbStatus);
	}
	@Test
	public void testRemoveAllTaxonomyTaggingDetailsInBulk1() throws ApplicationException
	{
		TaxonomyTaggingBean loTaxonomyTaggingBean = taxonomyTaggingContractProcIdInfo();
		loTaxonomyTaggingBean.setContractIdList(null);
		boolean lbStatus = moTaxonomyTaggingServiceInBulk.removeAllTaxonomyTaggingDetailsInBulk(moSession,
				loTaxonomyTaggingBean);
	}
	@Test
	public void testRemoveAllTaxonomyTaggingDetailsInBulk2() throws ApplicationException
	{
		TaxonomyTaggingBean loTaxonomyTaggingBean = taxonomyTaggingContractProcIdInfo();
		boolean lbStatus = moTaxonomyTaggingServiceInBulk.removeAllTaxonomyTaggingDetailsInBulk(moSession,
				loTaxonomyTaggingBean);
		assertTrue(lbStatus);
	}
	@Test
	public void testRemoveAllTaxonomyTaggingDetailsInBulk3() throws ApplicationException
	{
		TaxonomyTaggingBean loTaxonomyTaggingBean = taxonomyTaggingContractProcIdInfo();
		boolean lbStatus = moTaxonomyTaggingServiceInBulk.removeAllTaxonomyTaggingDetailsInBulk(moSession,
				loTaxonomyTaggingBean);
		assertTrue(lbStatus);
	}
	@Test
	public void testRemoveAllTaxonomyTaggingDetailsInBulk4() throws ApplicationException
	{
		TaxonomyTaggingBean loTaxonomyTaggingBean = taxonomyTaggingContractProcIdInfo();
		boolean lbStatus = moTaxonomyTaggingServiceInBulk.removeAllTaxonomyTaggingDetailsInBulk(moSession,
				loTaxonomyTaggingBean);
		assertTrue(lbStatus);
	}
	@Test
	public void testRemoveAllTaxonomyTaggingDetailsInBulk5() throws ApplicationException
	{
		TaxonomyTaggingBean loTaxonomyTaggingBean = taxonomyTaggingContractProcIdInfo();
		boolean lbStatus = moTaxonomyTaggingServiceInBulk.removeAllTaxonomyTaggingDetailsInBulk(moSession,
				loTaxonomyTaggingBean);
		assertTrue(lbStatus);
	}
	@Test
	public void testRemoveAllTaxonomyTaggingDetailsInBulk6() throws ApplicationException
	{
		TaxonomyTaggingBean loTaxonomyTaggingBean = taxonomyTaggingContractProcIdInfo();
		boolean lbStatus = moTaxonomyTaggingServiceInBulk.removeAllTaxonomyTaggingDetailsInBulk(moSession,
				loTaxonomyTaggingBean);
		assertTrue(lbStatus);
	}
	@Test
	public void testRemoveAllTaxonomyTaggingDetailsInBulk7() throws ApplicationException
	{
		TaxonomyTaggingBean loTaxonomyTaggingBean = taxonomyTaggingContractProcIdInfo();
		boolean lbStatus = moTaxonomyTaggingServiceInBulk.removeAllTaxonomyTaggingDetailsInBulk(moSession,
				loTaxonomyTaggingBean);
		assertTrue(lbStatus);
	}
	@Test
	public void testRemoveAllTaxonomyTaggingDetailsInBulk8() throws ApplicationException
	{
		TaxonomyTaggingBean loTaxonomyTaggingBean = taxonomyTaggingContractProcIdInfo();
		boolean lbStatus = moTaxonomyTaggingServiceInBulk.removeAllTaxonomyTaggingDetailsInBulk(moSession,
				loTaxonomyTaggingBean);
		assertTrue(lbStatus);
	}
	@Test
	public void testRemoveAllTaxonomyTaggingDetailsInBulk9() throws ApplicationException
	{
		TaxonomyTaggingBean loTaxonomyTaggingBean = taxonomyTaggingContractProcIdInfo();
		boolean lbStatus = moTaxonomyTaggingServiceInBulk.removeAllTaxonomyTaggingDetailsInBulk(moSession,
				loTaxonomyTaggingBean);
		assertTrue(lbStatus);
	}
	@Test
	public void testRemoveAllTaxonomyTaggingDetailsInBulk10() throws ApplicationException
	{
		TaxonomyTaggingBean loTaxonomyTaggingBean = taxonomyTaggingContractProcIdInfo();
		boolean lbStatus = moTaxonomyTaggingServiceInBulk.removeAllTaxonomyTaggingDetailsInBulk(moSession,
				loTaxonomyTaggingBean);
		assertTrue(lbStatus);
	}
	@Test(expected = ApplicationException.class)
	public void testSaveAllTaxonomyTaggingDetailsInBulk1() throws ApplicationException
	{
		
		taxonomyTaggingData();
		boolean lbStatus = moTaxonomyTaggingServiceInBulk.saveAllSelectedProposalsInbulk(moSession,moServiceIdArray,moModifierIdsArray, 
				moContractIdBulkArray, moProposalIdBulkArray,moProcurementIdBulkArray,moTaxonomyTaggingIdBulkArray,msUserId);
	}
	
	@Test
	public void testSaveAllTaxonomyTaggingDetailsInBulk2() throws ApplicationException
	{
		
		taxonomyTaggingData();
		boolean lbStatus = moTaxonomyTaggingServiceInBulk.saveAllSelectedProposalsInbulk(moSession,moServiceIdArray,moModifierIdsArray, 
				moContractIdBulkArray, moProposalIdBulkArray,moProcurementIdBulkArray,moTaxonomyTaggingIdBulkArray,msUserId);
		assertTrue(lbStatus);
	}
	@Test
	public void testSaveAllTaxonomyTaggingDetailsInBulk3() throws ApplicationException
	{
		
		taxonomyTaggingData();
		boolean lbStatus = moTaxonomyTaggingServiceInBulk.saveAllSelectedProposalsInbulk(moSession,moServiceIdArray,moModifierIdsArray, 
				moContractIdBulkArray, moProposalIdBulkArray,moProcurementIdBulkArray,moTaxonomyTaggingIdBulkArray,msUserId);
		assertTrue(lbStatus);
	}
	@Test
	public void testSaveAllTaxonomyTaggingDetailsInBulk4() throws ApplicationException
	{
		
		taxonomyTaggingData();
		boolean lbStatus = moTaxonomyTaggingServiceInBulk.saveAllSelectedProposalsInbulk(moSession,moServiceIdArray,moModifierIdsArray, 
				moContractIdBulkArray, moProposalIdBulkArray,moProcurementIdBulkArray,moTaxonomyTaggingIdBulkArray,msUserId);
		assertTrue(lbStatus);
	}
	
	@Test
	public void testSaveAllTaxonomyTaggingDetailsInBulk5() throws ApplicationException
	{
		
		taxonomyTaggingData();
		boolean lbStatus = moTaxonomyTaggingServiceInBulk.saveAllSelectedProposalsInbulk(moSession,moServiceIdArray,moModifierIdsArray, 
				moContractIdBulkArray, moProposalIdBulkArray,moProcurementIdBulkArray,moTaxonomyTaggingIdBulkArray,msUserId);
		assertTrue(lbStatus);
	}
	
	@Test
	public void testSaveAllTaxonomyTaggingDetailsInBulk6() throws ApplicationException
	{
		
		taxonomyTaggingData();
		boolean lbStatus = moTaxonomyTaggingServiceInBulk.saveAllSelectedProposalsInbulk(moSession,moServiceIdArray,moModifierIdsArray, 
				moContractIdBulkArray, moProposalIdBulkArray,moProcurementIdBulkArray,moTaxonomyTaggingIdBulkArray,msUserId);
		assertTrue(lbStatus);
	}
	
	@Test
	public void testSaveAllTaxonomyTaggingDetailsInBulk7() throws ApplicationException
	{
		
		taxonomyTaggingData();
		boolean lbStatus = moTaxonomyTaggingServiceInBulk.saveAllSelectedProposalsInbulk(moSession,moServiceIdArray,moModifierIdsArray, 
				moContractIdBulkArray, moProposalIdBulkArray,moProcurementIdBulkArray,moTaxonomyTaggingIdBulkArray,msUserId);
		assertTrue(lbStatus);
	}
	
	@Test
	public void testSaveAllTaxonomyTaggingDetailsInBulk8() throws ApplicationException
	{
		
		taxonomyTaggingData();
		boolean lbStatus = moTaxonomyTaggingServiceInBulk.saveAllSelectedProposalsInbulk(moSession,moServiceIdArray,moModifierIdsArray, 
				moContractIdBulkArray, moProposalIdBulkArray,moProcurementIdBulkArray,moTaxonomyTaggingIdBulkArray,msUserId);
		assertTrue(lbStatus);
	}
	@Test
	public void testSaveAllTaxonomyTaggingDetailsInBulk9() throws ApplicationException
	{
		
		taxonomyTaggingData();
		boolean lbStatus = moTaxonomyTaggingServiceInBulk.saveAllSelectedProposalsInbulk(moSession,moServiceIdArray,moModifierIdsArray, 
				moContractIdBulkArray, moProposalIdBulkArray,moProcurementIdBulkArray,moTaxonomyTaggingIdBulkArray,msUserId);
		assertTrue(lbStatus);
	}
	@Test
	public void testSaveAllTaxonomyTaggingDetailsInBulk9239() throws ApplicationException
	{
		
		taxonomyTaggingData();
		boolean lbStatus = moTaxonomyTaggingServiceInBulk.saveAllSelectedProposalsInbulk(moSession,moServiceIdArray,moModifierIdsArray, 
				moContractIdBulkArray, moProposalIdBulkArray,moProcurementIdBulkArray,moTaxonomyTaggingIdBulkArray,msUserId);
		assertTrue(lbStatus);
	}
	@Test
	public void testSaveAllTaxonomyTaggingDetailsInBulk99() throws ApplicationException
	{
		
		taxonomyTaggingData();
		boolean lbStatus = moTaxonomyTaggingServiceInBulk.saveAllSelectedProposalsInbulk(moSession,moServiceIdArray,moModifierIdsArray, 
				moContractIdBulkArray, moProposalIdBulkArray,moProcurementIdBulkArray,moTaxonomyTaggingIdBulkArray,msUserId);
		assertTrue(lbStatus);
	}
	@Test
	public void testSaveAllTaxonomyTaggingDetailsInBulk979() throws ApplicationException
	{
		
		taxonomyTaggingData();
		boolean lbStatus = moTaxonomyTaggingServiceInBulk.saveAllSelectedProposalsInbulk(moSession,moServiceIdArray,moModifierIdsArray, 
				moContractIdBulkArray, moProposalIdBulkArray,moProcurementIdBulkArray,moTaxonomyTaggingIdBulkArray,msUserId);
		assertTrue(lbStatus);
	}
	@Test
	public void testSaveAllTaxonomyTaggingDetailsInBulk799() throws ApplicationException
	{
		
		taxonomyTaggingData();
		boolean lbStatus = moTaxonomyTaggingServiceInBulk.saveAllSelectedProposalsInbulk(moSession,moServiceIdArray,moModifierIdsArray, 
				moContractIdBulkArray, moProposalIdBulkArray,moProcurementIdBulkArray,moTaxonomyTaggingIdBulkArray,msUserId);
		assertTrue(lbStatus);
	}
	@Test
	public void testSaveAllTaxonomyTaggingDetailsInBulk999() throws ApplicationException
	{
		
		taxonomyTaggingData();
		boolean lbStatus = moTaxonomyTaggingServiceInBulk.saveAllSelectedProposalsInbulk(moSession,moServiceIdArray,moModifierIdsArray, 
				moContractIdBulkArray, moProposalIdBulkArray,moProcurementIdBulkArray,moTaxonomyTaggingIdBulkArray,msUserId);
		assertTrue(lbStatus);
	}
	@Test
	public void testSaveAllTaxonomyTaggingDetailsInBulk76() throws ApplicationException
	{
		
		taxonomyTaggingData();
		boolean lbStatus = moTaxonomyTaggingServiceInBulk.saveAllSelectedProposalsInbulk(moSession,moServiceIdArray,moModifierIdsArray, 
				moContractIdBulkArray, moProposalIdBulkArray,moProcurementIdBulkArray,moTaxonomyTaggingIdBulkArray,msUserId);
		assertTrue(lbStatus);
	}
	@Test
	public void testSaveAllTaxonomyTaggingDetailsInBulk45() throws ApplicationException
	{
		
		taxonomyTaggingData();
		boolean lbStatus = moTaxonomyTaggingServiceInBulk.saveAllSelectedProposalsInbulk(moSession,moServiceIdArray,moModifierIdsArray, 
				moContractIdBulkArray, moProposalIdBulkArray,moProcurementIdBulkArray,moTaxonomyTaggingIdBulkArray,msUserId);
		assertTrue(lbStatus);
	}
	@Test
	public void testSaveAllTaxonomyTaggingDetailsInBulk2323() throws ApplicationException
	{
		
		taxonomyTaggingData();
		boolean lbStatus = moTaxonomyTaggingServiceInBulk.saveAllSelectedProposalsInbulk(moSession,moServiceIdArray,moModifierIdsArray, 
				moContractIdBulkArray, moProposalIdBulkArray,moProcurementIdBulkArray,moTaxonomyTaggingIdBulkArray,msUserId);
		assertTrue(lbStatus);
	}
	@Test
	public void testSaveAllTaxonomyTaggingDetailsInBulk() throws ApplicationException
	{
		
		taxonomyTaggingData();
		boolean lbStatus = moTaxonomyTaggingServiceInBulk.saveAllSelectedProposalsInbulk(moSession,moServiceIdArray,moModifierIdsArray, 
				moContractIdBulkArray, moProposalIdBulkArray,moProcurementIdBulkArray,moTaxonomyTaggingIdBulkArray,msUserId);
		assertTrue(lbStatus);
	}

	
	
	@Test(expected = ApplicationException.class)
	public void testSaveAllTaxonomyTaggingDetailsInBulkException() throws ApplicationException
	{
		
		taxonomyTaggingData();
		boolean lbStatus = moTaxonomyTaggingServiceInBulk.saveAllSelectedProposalsInbulk(moSession,moServiceIdArray,moModifierIdsArray, 
				moContractIdBulkArray, moProposalIdBulkArray,moProcurementIdBulkArray,null,msUserId);
		assertTrue(lbStatus);
	}
	@Test(expected = ApplicationException.class)
	public void testSaveAllTaxonomyTaggingDetailsInBulkApplicationException() throws ApplicationException
	{
		
		taxonomyTaggingData();
		boolean lbStatus = moTaxonomyTaggingServiceInBulk.saveAllSelectedProposalsInbulk(null,moServiceIdArray,moModifierIdsArray, 
				moContractIdBulkArray, moProposalIdBulkArray,moProcurementIdBulkArray,moTaxonomyTaggingIdBulkArray,msUserId);
		assertTrue(lbStatus);
	}

	private void taxonomyTaggingData()
	{
		moServiceIdArray.add(msServiceId1);
		moServiceIdArray.add(msServiceId2);
		moModifierIdsArray.add(msModifierId1);
		moModifierIdsArray.add(msModifierId2);
		moContractIdBulkArray.add(msNull);
		moContractIdBulkArray.add(msContractId);
		moProposalIdBulkArray.add(msProposalId);
		moProposalIdBulkArray.add(msNull);
		moProcurementIdBulkArray.add(msProcurementId);
		moProcurementIdBulkArray.add(msNull);	
		moTaxonomyTaggingIdBulkArray.add(mstaxonomytagging);
		moTaxonomyTaggingIdBulkArray.add(msTaxTaggingId2 );
		
	}

	@Test(expected = ApplicationException.class)
	public void testRemoveAllTaxonomyTaggingDetailsInBulkException() throws ApplicationException
	{
		TaxonomyTaggingBean loTaxonomyTaggingBean = taxonomyTaggingContractProcIdInfo();
		moTaxonomyTaggingServiceInBulk.removeAllTaxonomyTaggingDetailsInBulk(null, loTaxonomyTaggingBean);
	}

	@Test
	public void testDeleteTaxonomyTaggingDetailsInBulk1() throws ApplicationException
	{
		boolean lbStatus = moTaxonomyTaggingServiceInBulk.deleteTaxonomyTaggingDetailsInBulk(moSession
				,msTaxTaggingId,msUserId);
		assertTrue(lbStatus);
	}
	
	@Test
	public void testDeleteTaxonomyTaggingDetailsInBulk2() throws ApplicationException
	{
		boolean lbStatus = moTaxonomyTaggingServiceInBulk.deleteTaxonomyTaggingDetailsInBulk(moSession
				,msTaxTaggingId,msUserId);
		assertTrue(lbStatus);
	}
	
	@Test
	public void testDeleteTaxonomyTaggingDetailsInBulk3() throws ApplicationException
	{
		boolean lbStatus = moTaxonomyTaggingServiceInBulk.deleteTaxonomyTaggingDetailsInBulk(moSession
				,msTaxTaggingId,msUserId);
		assertTrue(lbStatus);
	}
	
	@Test
	public void testDeleteTaxonomyTaggingDetailsInBulk4() throws ApplicationException
	{
		boolean lbStatus = moTaxonomyTaggingServiceInBulk.deleteTaxonomyTaggingDetailsInBulk(moSession
				,msTaxTaggingId,msUserId);
		assertTrue(lbStatus);
	}
	
	@Test
	public void testDeleteTaxonomyTaggingDetailsInBulk5() throws ApplicationException
	{
		boolean lbStatus = moTaxonomyTaggingServiceInBulk.deleteTaxonomyTaggingDetailsInBulk(moSession
				,msTaxTaggingId,msUserId);
		assertTrue(lbStatus);
	}
	
	@Test
	public void testDeleteTaxonomyTaggingDetailsInBulk6() throws ApplicationException
	{
		boolean lbStatus = moTaxonomyTaggingServiceInBulk.deleteTaxonomyTaggingDetailsInBulk(moSession
				,msTaxTaggingId,msUserId);
		assertTrue(lbStatus);
	}

	@Test(expected = ApplicationException.class)
	public void testDeleteTaxonomyTaggingDetailsInBulkException() throws ApplicationException
	{
		moTaxonomyTaggingServiceInBulk.deleteTaxonomyTaggingDetailsInBulk(null
				,msTaxTaggingId,msUserId1);
	}

}
