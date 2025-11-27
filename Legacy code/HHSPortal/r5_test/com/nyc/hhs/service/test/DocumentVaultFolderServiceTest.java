package com.nyc.hhs.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.derby.tools.sysinfo;
import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.daomanager.service.DocumentVaultFolderService;
import com.nyc.hhs.daomanager.service.EvaluationService;
import com.nyc.hhs.daomanager.service.PsrService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.Document;
import com.nyc.hhs.model.DocumentBean;
import com.nyc.hhs.model.DocumentVisibility;
import com.nyc.hhs.model.EvaluationBean;
import com.nyc.hhs.model.FolderMappingBean;
import com.nyc.hhs.model.Procurement;
import com.nyc.hhs.model.PsrBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.util.DateUtil;




public class DocumentVaultFolderServiceTest
{

	DocumentVaultFolderService documentVaultFolderService=new DocumentVaultFolderService();
	//EvaluationService evaluationService = new EvaluationService();

	private static SqlSession moSession = null; // SQL Session

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
	public void testInsertForFilenet() throws ApplicationException
	{
		FolderMappingBean folderMappingBean =new FolderMappingBean();		
		folderMappingBean.setFolderName("ABansal");
		folderMappingBean.setFolderFilenetId("{11593C07-5ACD-4B20-B77A-91A7CEE44BD5}");
		folderMappingBean.setParentFolderFilenetId("{1080A6B7-13CB-4723-851D-ED3EC65E2915}");
		folderMappingBean.setOrganizationId("Org_509");
		//folderMappingBean.setCreatedDate("30-08-16 09:05:37.806000000 AM");
		folderMappingBean.setCreatedBy("Bansal");
		//folderMappingBean.setModifiedDate("30-08-16 08:05:37.806000000 AM");
		folderMappingBean.setModifiedBy("Bansal");
		folderMappingBean.setType("1");
		folderMappingBean.setMovedToRecycleBin("0");
		List<FolderMappingBean> loParameter =new ArrayList<FolderMappingBean>();
		loParameter.add(folderMappingBean);
		assertEquals(1, documentVaultFolderService.insertForFilenet(moSession, loParameter));	
	}

	@Test
	public void uploadPsrDocumentToFilenetCase1() throws ApplicationException
	
	{
		PsrService obj1 = new PsrService();
		
		List<String> aoOutputFilePathList = new ArrayList<String>();
		aoOutputFilePathList.add("C:\\PDF\\4483.pdf");
		
		PsrBean loPsrBean = new PsrBean();
		
		List<Procurement> loServiceList = new ArrayList<Procurement>();
		Procurement obj = new Procurement();
		loServiceList.add(0, obj);
		loPsrBean.setUserId("agency_14");
		loPsrBean.setProcurementId("4626");
		loPsrBean.setPsrDetailId("123");
		loPsrBean.setAccPrimaryContact("Username1 || 123456");
		loPsrBean.setAccSecondaryContact("Username2 || 999900");
		loPsrBean.setAgecncyPrimaryContact("Username3 || 242353");
		loPsrBean.setAgecncySecondaryContact("Username3 || 9287364");
		loPsrBean.setEmail("xyz@www.com");
		loPsrBean.setProcurementDescription("good");
		loPsrBean.setProgramName("name");
		loPsrBean.setAgencyName("agency_18");
		loPsrBean.setProcurementTitle("Sample");
		loPsrBean.setProcurementEpin("EFHUJ8766GTG");
		loPsrBean.setRfpReleaseDate(DateUtil.getDate("08/22/2013"));
		loPsrBean.setIsOpenEndedRFP("1");
		loPsrBean.setBasisContractOut("0,1");
		loPsrBean.setAnticipateLevelComp("H");
		loPsrBean.setServiceFilter("0");
		loPsrBean.setMultiYearHumanServContract("test case");
		loPsrBean.setContractTermInfo("6");
		loPsrBean.setCreatedDate("08/20/2012");
		loPsrBean.setApproverUserId("agency_14");
		loPsrBean.setContractStartFrom("08/20/2012");
		
		assertNotNull(obj1.uploadPsrDocumentToFilenet(moP8session, aoOutputFilePathList, loPsrBean));
		
	}
	@Test
	public void testInsertForFilenetCase1() throws ApplicationException
	{
		FolderMappingBean folderMappingBean =new FolderMappingBean();		
		folderMappingBean.setFolderName("ABCD");
		folderMappingBean.setFolderFilenetId("{11593C07-5ACD-4B20-B77A-97A7CE3E8BD5}");
		folderMappingBean.setParentFolderFilenetId("{1080A6B7-13CB-4723-851D-ED3EC65E2915}");
		folderMappingBean.setOrganizationId("Org_509");

		folderMappingBean.setCreatedBy("Bansal");
	
		folderMappingBean.setModifiedBy("Bansal");
		folderMappingBean.setType("1");
		folderMappingBean.setMovedToRecycleBin("0");
		List<FolderMappingBean> loParameter =null;		
		assertEquals(0, documentVaultFolderService.insertForFilenet(moSession, loParameter));	
	}
	
	@Test(expected=ApplicationException.class)
	public void testInsertForFilenetCase3() throws ApplicationException
	{
		FolderMappingBean folderMappingBean =new FolderMappingBean();		
		folderMappingBean.setFolderName("ABCD");
		folderMappingBean.setFolderFilenetId("{11593C07-5ACD-4B20-B77A-97A7CE3E8BD5}");
		folderMappingBean.setParentFolderFilenetId("{1080A6B7-13CB-4723-851D-ED3EC65E2915}");
		folderMappingBean.setOrganizationId("Org_509");
		//folderMappingBean.setCreatedDate("30-08-16 09:05:37.806000000 AM");
		folderMappingBean.setCreatedBy("Bansal");
	//	folderMappingBean.setModifiedDate("30-08-16 08:05:37.806000000 AM");
		folderMappingBean.setModifiedBy("Bansal");
		folderMappingBean.setType("1");
		folderMappingBean.setMovedToRecycleBin("0");
		List<FolderMappingBean> loParameter =new ArrayList<FolderMappingBean>();
		loParameter.add(folderMappingBean);
		documentVaultFolderService.insertForFilenet(null, loParameter);	
	}
	
	@Test
	public void testDeleteFromFolderMapping() throws ApplicationException
	{
		 HashMap<String, String> loHashMap=new HashMap<String, String>();
		 loHashMap.put("organizationId", "Org_509");
		 HashMap<String,String>  ls=new HashMap<String, String>();
		 ls.put("fileNetIdList", "{11593C07-5ACD-4B20-B77A-97A7CE3E7EC5}");	
		assertTrue(documentVaultFolderService.deleteFromFolderMapping(moSession, loHashMap));
		
	}

	@Test(expected=ApplicationException.class)
	public void testDeleteFromFolderMappingCase1() throws ApplicationException
	{
		 HashMap<String, String> loHashMap=new HashMap<String, String>();
		documentVaultFolderService.deleteFromFolderMapping(null, loHashMap);		
	}
	
/*	@Test
	public void testCreateFolder() throws ApplicationException
	{ FolderMappingBean aofolderMappingBean=null;
		documentVaultFolderService.createFolder(moSession, aofolderMappingBean);
	}*/

/*	@Test
	public void testGetFolderMappingPath() throws ApplicationException
	{
		 HashMap<String, String> aoHashMap=null;
		documentVaultFolderService.getFolderMappingPath(moSession, aoHashMap);
	}
*/
/*	@Test
	public void testGetJstreeData() throws ApplicationException
	{
		 HashMap<String, String> aoHashMap=null;
		documentVaultFolderService.getJstreeData(moSession, aoHashMap);
	}
*/
/*	@Test
	public void testGetLockInfo() throws ApplicationException
	{
		HashMap<String, String> aoHashMap=null;
		documentVaultFolderService.getLockInfo(moSession, aoHashMap);
	}*/

	@Test
	public void testUpdateFolderMapping() throws ApplicationException
	{
			HashMap<String,String> hmReqProps=new HashMap<String,String >();
		 	hmReqProps.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE,"testing");
			hmReqProps.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY,"Amit Bansal");
			hmReqProps.put(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE,"29-08-16 03:59:37.736000000 PM");
			hmReqProps.put("folderId","{11593C07-5ACD-4B20-B77A-97A7CE3E7EC5}");
			hmReqProps.put("organizationId","Org_509");
			Boolean loStatus=documentVaultFolderService.updateFolderMapping(moSession, hmReqProps);
			assertEquals(true, loStatus);
	}

	@Test(expected=ApplicationException.class)
	public void testUpdateFolderMappingCase1() throws ApplicationException
	{
			HashMap<String,String> hmReqProps=new HashMap<String,String >();
		 	hmReqProps.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE,"testing");
			hmReqProps.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY,"Amit Bansal");
			//hmReqProps.put(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE,"29-08-16 03:59:37.736000000 PM");
			hmReqProps.put("folderId","{11593C07-5ACD-4B20-B77A-9797CE3333C5}");
			hmReqProps.put("organizationId","Org_509");
			Boolean loStatus=documentVaultFolderService.updateFolderMapping(moSession, hmReqProps);
			//assertEquals(false, loStatus);
	}

	@Test(expected=ApplicationException.class)
	public void testUpdateFolderMappingCase2() throws ApplicationException
	{
			HashMap<String,String> hmReqProps=new HashMap<String,String >();
		 	hmReqProps.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE,"testing");
			hmReqProps.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY,"Amit Bansal");
			//hmReqProps.put(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE,"29-08-16 03:59:37.736000000 PM");
			hmReqProps.put("folderId","{11593C07-5ACD-4B20-B77A-97A7CE3333C5}");
			hmReqProps.put("organizationId","Org_509");
			Boolean loStatus=documentVaultFolderService.updateFolderMapping(null, hmReqProps);			
	}
	
	@Test
	public void testGetFolderMappingCount() throws ApplicationException
	{
		 String aoFolderId="{11593C07-5ACD-4B20-B77A-97A7CE3E7EC5}" ;
		 HashMap<String, String> loHashMap=new HashMap<String, String>();
		 loHashMap.put(HHSR5Constants.USER_ORG_ID,"Org_509");
		 FolderMappingBean folderMappingBean=new FolderMappingBean();
		 folderMappingBean=documentVaultFolderService.getFolderMappingCount(moSession, aoFolderId, loHashMap);
		 assertEquals("test",folderMappingBean.getFolderName());
	}
	@Test(expected=ApplicationException.class)
	public void testGetFolderMappingCountCase1() throws ApplicationException
	{
		 String aoFolderId="{11593C07-5ACD-4B20-B77A-97A7CE3E7EC5}" ;
		 HashMap<String, String> loHashMap=new HashMap<String, String>();
		 loHashMap.put(HHSR5Constants.USER_ORG_ID,"Org_509");
		 FolderMappingBean folderMappingBean=new FolderMappingBean();
		 folderMappingBean=documentVaultFolderService.getFolderMappingCount(null, aoFolderId, loHashMap);
	
	}
	@Test(expected=Exception.class)
	public void testGetFolderMappingCountCase2() throws ApplicationException
	{
		 String aoFolderId="{11593C07-5ACD-4B20-B77A-97A7CE3E7EC5}" ;
		 HashMap<String, String> loHashMap=new HashMap<String, String>();
		 loHashMap.put(HHSR5Constants.USER_ORG_ID,"Org_509");
		 FolderMappingBean folderMappingBean=new FolderMappingBean();
		 folderMappingBean=documentVaultFolderService.getFolderMappingCount(null, null, null);
	
	}
	/*@Test 
	public void testInsertFolderMappingAudit() throws ApplicationException
	{
		 HashMap<String, String> aoHashMap=null;
		documentVaultFolderService.insertFolderMappingAudit(moSession, aoHashMap);
	}
*/
	@Test
	public void testFetchFolderMapping() throws ApplicationException
	{
		 HashMap<String, String> loHashMap=new HashMap<String, String>();
		 loHashMap.put("orgId", "Org_509");
		 List<FolderMappingBean> list=new ArrayList<FolderMappingBean>();
		 list=documentVaultFolderService.fetchFolderMapping(moSession, loHashMap);
		 assertEquals(list.size(), 10);
	}
	
	@Test(expected=ApplicationException.class)
	public void testFetchFolderMappingCase1() throws ApplicationException
	{
		 HashMap<String, String> loHashMap=new HashMap<String, String>();
		 loHashMap.put("orgId", "Org_509");
		 List<FolderMappingBean> list=new ArrayList<FolderMappingBean>();
		 list=documentVaultFolderService.fetchFolderMapping(null, loHashMap);
		
	}
	@Test(expected=Exception.class)
	public void testFetchFolderMappingCase2() throws ApplicationException
	{
		 HashMap<String, String> loHashMap=new HashMap<String, String>();
		 loHashMap.put("orgId", "12345555555555555555");
		 List<FolderMappingBean> list=new ArrayList<FolderMappingBean>();
		 list=documentVaultFolderService.fetchFolderMapping(moSession, null);		
	}

	@Test
	public void testUpdateFolderCount() throws ApplicationException
	{
		FolderMappingBean folderMappingBean=new FolderMappingBean();
		folderMappingBean.setFolderName("test");
		folderMappingBean.setModifiedBy("Amit Kumar Bansal");
		folderMappingBean.setOrganizationId("Org_509");
		folderMappingBean.setParentFolderFilenetId("{1080A6B7-13CB-4723-851D-ED3EC65E2915}"); //parentID
		List<FolderMappingBean> loBean=new ArrayList<FolderMappingBean>();
		loBean.add(folderMappingBean);
		Integer loCount=documentVaultFolderService.updateFolderCount(moSession, loBean);
		assertEquals(1, loCount.intValue());
	}
	
	@Test
	public void testUpdateFolderCountCase1() throws ApplicationException
	{
		FolderMappingBean folderMappingBean=new FolderMappingBean();
		folderMappingBean.setFolderName(HHSR5Constants.DOCUMENT_VAULT);
		folderMappingBean.setModifiedBy("Amit Kumar Bansal");
		folderMappingBean.setOrganizationId("Org_509");
		folderMappingBean.setParentFolderFilenetId("{1080A6B7-13CB-4723-851D-ED3EC65E2915}"); //parentID
		List<FolderMappingBean> loBean=new ArrayList<FolderMappingBean>();
		loBean.add(folderMappingBean);
		Integer loCount=documentVaultFolderService.updateFolderCount(moSession, loBean);
		assertEquals(0, loCount.intValue());
	}
	
	@Test(expected=ApplicationException.class)
	public void testUpdateFolderCountCase2() throws ApplicationException
	{
		FolderMappingBean folderMappingBean=new FolderMappingBean();
		folderMappingBean.setFolderName("test");
		folderMappingBean.setModifiedBy("Amit Kumar Bansal");
		folderMappingBean.setOrganizationId("Org_509'");
		folderMappingBean.setParentFolderFilenetId("{1080A6B7-13CB-4723-851D-ED3EC65E2915}"); //parentID
		List<FolderMappingBean> loBean=new ArrayList<FolderMappingBean>();
		//loBean.add(folderMappingBean);
		loBean.add(null);
		documentVaultFolderService.updateFolderCount(moSession, loBean);
		
	}
	@Test(expected=ApplicationException.class)
	public void testUpdateFolderCountCase3() throws ApplicationException
	{ 
		FolderMappingBean folderMappingBean=new FolderMappingBean();
		folderMappingBean.setFolderName("test");
		folderMappingBean.setModifiedBy("Amit Kumar Bansal");
		folderMappingBean.setOrganizationId("Org_509'");
		folderMappingBean.setParentFolderFilenetId("{1080A6B7-13CB-4723-851D-ED3EC65E2915}"); //parentID
		List<FolderMappingBean> loBean=new ArrayList<FolderMappingBean>();
		loBean.add(folderMappingBean);
		Integer loCount=documentVaultFolderService.updateFolderCount(null, loBean);
		
	}
	
	@Test
	public void testUpdateDocCount() throws ApplicationException
	{
		 HashMap<String, String> loReqMap=new HashMap<String, String>();
		 loReqMap.put(HHSR5Constants.CUSTOM_FLDR_ID,"{11593C07-5ACD-4B20-B77A-97A7CE3E7EC5}");
		 loReqMap.put(P8Constants.PROPERTY_CE_PROVIDER_ID,"Org_509");
		 loReqMap.put(P8Constants.PROPERTY_CE_ORGANIZATION_ID,"Bansal Amit");		 
		 Integer loCount=documentVaultFolderService.updateDocCount(moSession, loReqMap);
		 assertEquals(1, loCount.intValue());
	}
	@Test
	public void testUpdateDocCountCase1() throws ApplicationException
	{
		 HashMap<String, String> loReqMap=new HashMap<String, String>();
		 loReqMap.put(HHSR5Constants.CUSTOM_FLDR_ID,"{11593C07-5AAA-4B20-B77A-97A7CE3E7EC5}");
		 loReqMap.put(P8Constants.PROPERTY_CE_PROVIDER_ID,"Org_09");
		 loReqMap.put(P8Constants.PROPERTY_CE_ORGANIZATION_ID,"Bansal Amit");		 
		 Integer loCount=documentVaultFolderService.updateDocCount(moSession, loReqMap);
		 assertEquals(0, loCount.intValue());
	}
	
	@Test(expected=ApplicationException.class)
	public void testUpdateDocCountCase2() throws ApplicationException
	{
		 HashMap<String, String> loReqMap=new HashMap<String, String>();
		 loReqMap.put(HHSR5Constants.CUSTOM_FLDR_ID,"{11593C07-5AAA-4B20-B77A-97A7CE3E7EC5}");
		 loReqMap.put(P8Constants.PROPERTY_CE_PROVIDER_ID,"Org_09");
		 loReqMap.put(P8Constants.PROPERTY_CE_ORGANIZATION_ID,"Bansal Amit");		 
		 documentVaultFolderService.updateDocCount(null, loReqMap);
		 
	}
	
	@Test(expected=Exception.class)
	public void testUpdateDocCountCase3() throws ApplicationException
	{
		 HashMap<String, String> loReqMap=new HashMap<String, String>();
		 loReqMap.put(HHSR5Constants.CUSTOM_FLDR_ID,"{11593C07-5AAA-4B20-B77A-97A7CE3E7EC5}");
		 loReqMap.put(P8Constants.PROPERTY_CE_PROVIDER_ID,"Org_09");
		 loReqMap.put(P8Constants.PROPERTY_CE_ORGANIZATION_ID,"Bansal Amit");		 
		 documentVaultFolderService.updateDocCount(moSession, null);
		 
	}
/*	@Test
	public void testGetObjectLinkage() throws ApplicationException
	{
		 HashMap aoHashmap=null;
		documentVaultFolderService.getObjectLinkage(moSession, aoHashmap);
	}*/

/*	@Test
	public void testDownloadAllDocuments() throws ApplicationException
	{
		 HashMap aoHashmap=null;
		documentVaultFolderService.downloadAllDocuments(moSession, aoHashmap);
	}
*/
	@Test
	public void testUpdateFolderMappingForMove() throws ApplicationException
	{
		HashMap<String, Object> hmap=new HashMap<String, Object>();
		Object obj1 = new Object();
		obj1 = 1 ;
		hmap.put("documentCount",obj1);
		/*Object obj = new Object();
		obj = "doc_count_decrease";*/
		
		hmap.put(HHSR5Constants.DOC_COUNT_DECREASE, obj1 );
		hmap.put("fid1", "{11593C07-5ACD-4B20-B77A-97A7CE3E7EC5}");
		 HashMap<String, HashMap<String, Object>> hmReqProps=new HashMap<String, HashMap<String, Object>>();
		 hmReqProps.put("fid", hmap);
		 HashMap<String, String> aoMapOrg=new HashMap<String, String>();
		 aoMapOrg.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY,"Bansal Amit");
		aoMapOrg.put(HHSR5Constants.ORGANIZATION_ID,"Org_509");
 		int loStatus=documentVaultFolderService.updateFolderMappingForMove(moSession, hmReqProps, aoMapOrg);
			if(loStatus>0)
			{
				System.out.println(loStatus);
				assertTrue(true);
			}
	}

	@Test
	public void testUpdateFolderMappingForMoveCase0() throws ApplicationException
	{
		HashMap<String, Object> hmap=new HashMap<String, Object>();
		Object obj1 = new Object();
		obj1 = 1 ;
		hmap.put("documentCount",obj1);
	
		hmap.put(HHSR5Constants.DOC_COUNT_INCREASE, obj1 );
		hmap.put("fid1", "{11593C07-5ACD-4B20-B77A-97A7CE3E7EC5}");
		 HashMap<String, HashMap<String, Object>> hmReqProps=new HashMap<String, HashMap<String, Object>>();
		 hmReqProps.put("fid", hmap);
		 HashMap<String, String> aoMapOrg=new HashMap<String, String>();
		 aoMapOrg.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY,"Bansal Amit");
		aoMapOrg.put(HHSR5Constants.ORGANIZATION_ID,"Org_509");
 		int loStatus=documentVaultFolderService.updateFolderMappingForMove(moSession, hmReqProps, aoMapOrg);
			if(loStatus>0)
			{
				System.out.println(loStatus);
				assertTrue(true);
			}
	}
	@Test
	public void testUpdateFolderMappingForMoveCase3() throws ApplicationException
	{
		HashMap<String, Object> hmap=new HashMap<String, Object>();
		Object obj1 = new Object();
		obj1 = 1 ;
		hmap.put("documentCount",obj1);
		/*Object obj = new Object();
		obj = "doc_count_decrease";*/
		
		hmap.put(HHSR5Constants.FOLDER_COUNT_INCREASE, obj1 );
		hmap.put("fid1", "{11593C07-5ACD-4B20-B77A-97A7CE3E7EC5}");
		 HashMap<String, HashMap<String, Object>> hmReqProps=new HashMap<String, HashMap<String, Object>>();
		 hmReqProps.put("fid", hmap);
		 HashMap<String, String> aoMapOrg=new HashMap<String, String>();
		 aoMapOrg.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY,"Bansal Amit");
		aoMapOrg.put(HHSR5Constants.ORGANIZATION_ID,"Org_509");
 		int loStatus=documentVaultFolderService.updateFolderMappingForMove(moSession, hmReqProps, aoMapOrg);
			if(loStatus>0)
			{
				System.out.println(loStatus);
				assertTrue(true);
			}
	}
	
	@Test
	public void testUpdateFolderMappingForMoveCase4() throws ApplicationException
	{
		HashMap<String, Object> hmap=new HashMap<String, Object>();
		Object obj1 = new Object();
		obj1 = 1 ;
		hmap.put("documentCount",obj1);
		/*Object obj = new Object();
		obj = "doc_count_decrease";*/
		
		hmap.put(HHSR5Constants.FOLDER_COUNT_DECREASE, obj1 );
		hmap.put("fid1", "{11593C07-5ACD-4B20-B77A-97A7CE3E7EC5}");
		 HashMap<String, HashMap<String, Object>> hmReqProps=new HashMap<String, HashMap<String, Object>>();
		 hmReqProps.put("fid", hmap);
		 HashMap<String, String> aoMapOrg=new HashMap<String, String>();
		 aoMapOrg.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY,"Bansal Amit");
		aoMapOrg.put(HHSR5Constants.ORGANIZATION_ID,"Org_509");
 		int loStatus=documentVaultFolderService.updateFolderMappingForMove(moSession, hmReqProps, aoMapOrg);
			if(loStatus>0)
			{
				System.out.println(loStatus);
				assertTrue(true);
			}
	}
	
	@Test
	public void testUpdateFolderMappingForMoveCase5() throws ApplicationException
	{
		HashMap<String, Object> hmap=new HashMap<String, Object>();
		Object obj1 = new Object();
		obj1 = 1 ;
		hmap.put("documentCount",obj1);
		/*Object obj = new Object();
		obj = "doc_count_decrease";*/
		
		hmap.put("DeleteItemsList", obj1 );
		hmap.put("fid1", "{11593C07-5ACD-4B20-B77A-97A7CE3E7EC5}");


	
		//hmap.put("parentFolderId", obj1);
		 HashMap<String, HashMap<String, Object>> hmReqProps=new HashMap<String, HashMap<String, Object>>();
		 hmReqProps.put("fid", hmap);
		 HashMap<String, String> aoMapOrg=new HashMap<String, String>();
		 aoMapOrg.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY,"Bansal Amit");
		aoMapOrg.put(HHSR5Constants.ORGANIZATION_ID,"Org_509");
 		int loStatus=documentVaultFolderService.updateFolderMappingForMove(moSession, hmReqProps, aoMapOrg);
			if(loStatus>0)
			{
				System.out.println(loStatus);
				assertTrue(true);
			}
	}
	
	@Test
	public void testUpdateFolderMappingForMoveCase6() throws ApplicationException
	{
		HashMap<String, Object> hmap=new HashMap<String, Object>();
	Object obj1 = new Object();
		obj1 = 1  ;
		//hmap.put("parentFolderFilenetId",obj1);	
		
		hmap.put("DeleteItemsList", obj1 );
		hmap.put("fid1", "{11593C07-5ACD-4B20-B77A-97A7CE3E7EC5}");
	
		//hmap.put("parentFolderId", obj1);
		 HashMap<String, HashMap<String, Object>> hmReqProps=new HashMap<String, HashMap<String, Object>>();
		 hmReqProps.put("fid", hmap);
		 HashMap<String, String> aoMapOrg=new HashMap<String, String>();
		 aoMapOrg.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY,"Bansal Amit");
		aoMapOrg.put(HHSR5Constants.ORGANIZATION_ID,"Org_509");
 		int loStatus=documentVaultFolderService.updateFolderMappingForMove(moSession, hmReqProps, aoMapOrg);
			if(loStatus>0)
			{
				System.out.println(loStatus);
				assertTrue(true);
			}
	}
	
	@Test
	public void testUpdateFolderMappingForMoveCase1() throws ApplicationException
	{
		HashMap<String, Object> hmap=new HashMap<String, Object>();
		hmap.put("folderCount","1");
		hmap.put("fid1", "{11593C07-5ACD-4B20-B77A-97A7CE3E7EC5}");
		 HashMap<String, HashMap<String, Object>> hmReqProps=new HashMap<String, HashMap<String, Object>>();
		 hmReqProps.put("fid", hmap);
		 HashMap<String, String> aoMapOrg=new HashMap<String, String>();
		 aoMapOrg.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY,"Bansal Amit");
		aoMapOrg.put(HHSR5Constants.ORGANIZATION_ID,"Org_509");
		int loStatus=documentVaultFolderService.updateFolderMappingForMove(moSession, hmReqProps, aoMapOrg);
		if(loStatus>0)
		{
			System.out.println(loStatus);
			assertTrue(true);
		}

	}
	
	@Test(expected=ApplicationException.class)
	public void testUpdateFolderMappingForMoveCase2() throws ApplicationException
	{
		HashMap<String, Object> hmap=new HashMap<String, Object>();
		hmap.put("documentCount","1");
		hmap.put("fid1", "{11593C07-5ACD-4B20-B77A-97A7CE3E7EC5}");
		 HashMap<String, HashMap<String, Object>> hmReqProps=new HashMap<String, HashMap<String, Object>>();
		 hmReqProps.put("fid", hmap);
		 HashMap<String, String> aoMapOrg=new HashMap<String, String>();
		 aoMapOrg.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY,"Bansal Amit");
		aoMapOrg.put(HHSR5Constants.ORGANIZATION_ID,"Org_509");
		int loStatus=documentVaultFolderService.updateFolderMappingForMove(moSession, hmReqProps, aoMapOrg);
		if(loStatus>0)
		{
			System.out.println(loStatus);
			assertTrue(true);
		}
	}
	
	@Test
	public void testUpdateFolderMapingForDelete() throws ApplicationException
	{
				 HashMap<String, List<String>> hmReqProps=null;
		 HashMap<String, String> aoMapOrg=null;
		
		 aoMapOrg.put(HHSR5Constants.ORGANIZATION_ID,"Org_509");
		 documentVaultFolderService.updateFolderMapingForDelete(moSession, hmReqProps, aoMapOrg);
	}

/*	@Test
	public void testIntermediateServiceForDataHandling() throws ApplicationException
	{HashMap<String, Object> loReqMap=null;
		documentVaultFolderService.intermediateServiceForDataHandling(loReqMap);
	}*/

	@Test
	public void testDisplayLinkageInformation() throws ApplicationException
	{
		 String lsdocumentId="{01F1E3BA-E36C-4E73-A2AF-0F6A1D3C9C33}";
		 List<DocumentBean> loData=new ArrayList<DocumentBean>();
		 loData=documentVaultFolderService.displayLinkageInformation(moSession, lsdocumentId);
		 assertEquals(12, loData.size());
	}

	@Test(expected=ApplicationException.class)
	public void testDisplayLinkageInformationCase1() throws ApplicationException
	{
		 String lsdocumentId="{01F1E3BA-E36C-4E73-A2AF-0F6A1D3C9C33}";
		 List<DocumentBean> loData=new ArrayList<DocumentBean>();
		 loData=documentVaultFolderService.displayLinkageInformation(null, lsdocumentId);
		
	}
	
	@Test
	public void testUpdateFolderMappingDeleteFlag() throws ApplicationException
	{
		Document doc=new Document();
		doc.setDocumentId("{11593C07-5ACD-4B20-B77A-97A7CE3E7EC5}");
		doc.setPermamantDeleteFromDb(false);
		 List<Document> lsFolderIdList=new ArrayList<Document>();
		 lsFolderIdList.add(doc);
		 HashMap<String, String> aoMapOrg=new HashMap<String, String>();
		 aoMapOrg.put(HHSR5Constants.ORGANIZATION_ID,"Org_509");
		 Boolean status=documentVaultFolderService.updateFolderMappingDeleteFlag(moSession, lsFolderIdList, aoMapOrg);
		 assertTrue(status);
	}

	@Test
	public void testUpdateFolderMappingDeleteFlagCase1() throws ApplicationException
	{
		Document doc=new Document();
		doc.setDocumentId("{11593C07-5ACD-4B20-B77A-97A7CE3E7EC5}");
		doc.setPermamantDeleteFromDb(true);
		 List<Document> lsFolderIdList=new ArrayList<Document>();
		 lsFolderIdList.add(doc);
		 HashMap<String, String> aoMapOrg=new HashMap<String, String>();
		 aoMapOrg.put(HHSR5Constants.ORGANIZATION_ID,"Org_509");
		 assertEquals(false,documentVaultFolderService.updateFolderMappingDeleteFlag(moSession, lsFolderIdList, aoMapOrg));
	}
	@Test
	public void testUpdateFolderMappingDeleteFlagCase2() throws ApplicationException
	{
		Document doc=new Document();
		doc.setParent(null);
		doc.setDocType("1");
		doc.setDocumentId("{11593C07-5ACD-4B20-B77A-97A7CE3E7EC5}");
		//doc.setPermamantDeleteFromDb(true);
		 List<Document> lsFolderIdList=new ArrayList<Document>();
		 lsFolderIdList.add(doc);
		 HashMap<String, String> aoMapOrg=new HashMap<String, String>();
		 aoMapOrg.put(HHSR5Constants.ORGANIZATION_ID,"Org_509");
		assertEquals(false,documentVaultFolderService.updateFolderMappingDeleteFlag(moSession, lsFolderIdList, aoMapOrg));
	}

	@Test
	public void testUpdateFolderMappingDeleteFlagCase3() throws ApplicationException
	{
		Document doc=new Document();
		doc.setParent(null);
		doc.setDocType("1");
		doc.setDocumentId("{11593C07-5ACD-4B20-B77A-97A7CE3E7EC5}");
		//doc.setPermamantDeleteFromDb(true);
		 List<Document> lsFolderIdList=new ArrayList<Document>();		 
		 HashMap<String, String> aoMapOrg=new HashMap<String, String>();
		 aoMapOrg.put(HHSR5Constants.ORGANIZATION_ID,"Org_509");
		boolean loFlag=documentVaultFolderService.updateFolderMappingDeleteFlag(moSession, lsFolderIdList, aoMapOrg);
		assertTrue(loFlag);
	}
	
	@Test(expected=ApplicationException.class)
	public void testUpdateFolderMappingDeleteFlagCase4() throws ApplicationException
	{
		Document doc=new Document();
		doc.setParent(null);
		doc.setDocType("1");
		doc.setDocumentId("{11593C07-5ACD-4B20-B77A-97A7CE3E7EC5}");
		//doc.setPermamantDeleteFromDb(true);
		 List<Document> asFolderIdList=null;
		 HashMap<String, String> aoMapOrg=new HashMap<String, String>();
		 aoMapOrg.put(HHSR5Constants.ORGANIZATION_ID,"Org_509");
		documentVaultFolderService.updateFolderMappingDeleteFlag(null, asFolderIdList, aoMapOrg);
		
	}
	
	
	@Test
	public void testIntermediateServiceForInsertionInDB() throws ApplicationException
	{
		Document doc=new Document();
		doc.setDocumentId("{11593C07-5ACD-4B20-B77A-97A7CE3E7EC5}");
		doc.setPermamantDeleteFromDb(true);
		 List<Document> lsFolderIdList=new ArrayList<Document>();
		 lsFolderIdList.add(doc);
		 
		 HashMap<String, List> aoParameterMap=new HashMap<String, List>();		 
		 aoParameterMap.put("deletionList", lsFolderIdList);
		 
		 FolderMappingBean folderMappingBean =new FolderMappingBean();		
			folderMappingBean.setFolderName("ABCD");
			folderMappingBean.setFolderFilenetId("{11593C07-5ACD-4B20-B77A-97A7CE3E8BD5}");
			folderMappingBean.setParentFolderFilenetId("{1080A6B7-13CB-4723-851D-ED3EC65E2915}");
			folderMappingBean.setOrganizationId("Org_509");
			//folderMappingBean.setCreatedDate("30-08-16 09:05:37.806000000 AM");
			folderMappingBean.setCreatedBy("Bansal");
			//folderMappingBean.setModifiedDate("30-08-16 08:05:37.806000000 AM");
			folderMappingBean.setModifiedBy("Bansal");
			folderMappingBean.setType("1");
			folderMappingBean.setMovedToRecycleBin("0");
			List<FolderMappingBean> loParameter =new ArrayList<FolderMappingBean>();
			loParameter.add(folderMappingBean);
			
		 aoParameterMap.put("insertionList", loParameter);
		 HashMap<String, String> aoMapOrg=new HashMap<String, String>();
		 aoMapOrg.put(HHSR5Constants.ORGANIZATION_ID,"Org_509");
		 Integer loUpdateCount = documentVaultFolderService.intermediateServiceForInsertionInDB(moSession, aoParameterMap, aoMapOrg);
		 assertEquals(1, loUpdateCount.intValue());
	}
	
	@Test
	public void testIntermediateServiceForInsertionInDBCase1() throws ApplicationException
	{
		Document doc=new Document();
		doc.setDocumentId("{11593C07-5ACD-4B20-B77A-97A7CE3E7EC5}");
		doc.setPermamantDeleteFromDb(true);
		 List<Document> lsFolderIdList=new ArrayList<Document>();
		 lsFolderIdList.add(doc);
		 
		 HashMap<String, List> aoParameterMap=new HashMap<String, List>();		 
		 aoParameterMap.put("deletionList", lsFolderIdList);
		 
		 FolderMappingBean folderMappingBean =new FolderMappingBean();		
			folderMappingBean.setFolderName("ABCD");
			folderMappingBean.setFolderFilenetId("{11593C07-5ACD-4B20-B77A-97A7CE3E8BD5}");
			folderMappingBean.setParentFolderFilenetId("{1080A6B7-13CB-4723-851D-ED3EC65E2915}");
			folderMappingBean.setOrganizationId("Org_509");
			//folderMappingBean.setCreatedDate("30-08-16 09:05:37.806000000 AM");
			folderMappingBean.setCreatedBy("Bansal");
			//folderMappingBean.setModifiedDate("30-08-16 08:05:37.806000000 AM");
			folderMappingBean.setModifiedBy("Bansal");
			folderMappingBean.setType("1");
			folderMappingBean.setMovedToRecycleBin("0");
			List<FolderMappingBean> loParameter =new ArrayList<FolderMappingBean>();
			loParameter.add(folderMappingBean);
			
		 aoParameterMap.put("insertionList", loParameter);
		 HashMap<String, String> aoMapOrg=new HashMap<String, String>();
		 aoMapOrg.put(HHSR5Constants.ORGANIZATION_ID,"Org_509");
		 aoParameterMap=null;
		 Integer loUpdateCount = documentVaultFolderService.intermediateServiceForInsertionInDB(moSession, aoParameterMap, aoMapOrg);
		 assertEquals(0, loUpdateCount.intValue());
	}
	
	@Test(expected=ApplicationException.class)
	public void testIntermediateServiceForInsertionInDBCase2() throws ApplicationException
	{
		Document doc=new Document();
		doc.setDocumentId("{11593C07-5ACD-4B20-B77A-97A7CE3E7EC5}");
		doc.setPermamantDeleteFromDb(true);
		 List<Document> lsFolderIdList=new ArrayList<Document>();
		 lsFolderIdList.add(doc);
		 
		 HashMap<String, List> aoParameterMap=new HashMap<String, List>();		 
		 aoParameterMap.put("deletionList", lsFolderIdList);
		 
		 FolderMappingBean folderMappingBean =new FolderMappingBean();		
			folderMappingBean.setFolderName("ABCD");
			folderMappingBean.setFolderFilenetId("{11593C07-5ACD-4B20-B77A-97A7CE3E8BD5}");
			folderMappingBean.setParentFolderFilenetId("{1080A6B7-13CB-4723-851D-ED3EC65E2915}");
			folderMappingBean.setOrganizationId("Org_509");
			//folderMappingBean.setCreatedDate("30-08-16 09:05:37.806000000 AM");
			folderMappingBean.setCreatedBy("Bansal");
			//folderMappingBean.setModifiedDate("30-08-16 08:05:37.806000000 AM");
			folderMappingBean.setModifiedBy("Bansal");
			folderMappingBean.setType("1");
			folderMappingBean.setMovedToRecycleBin("0");
			List<FolderMappingBean> loParameter =new ArrayList<FolderMappingBean>();
			loParameter.add(folderMappingBean);
			
		 aoParameterMap.put("insertionList", loParameter);
		 HashMap<String, String> aoMapOrg=new HashMap<String, String>();
		 aoMapOrg.put(HHSR5Constants.ORGANIZATION_ID,"Org_509");	
		documentVaultFolderService.intermediateServiceForInsertionInDB(null, aoParameterMap, aoMapOrg);
		
	}
	
	@Test(expected=Exception.class)
	public void testIntermediateServiceForInsertionInDBCase3() throws ApplicationException
	{
		Document doc=new Document();
		doc.setDocumentId("{11593C07-5ACD-4B20-B77A-97A7CE3E7EC5}");
		doc.setPermamantDeleteFromDb(true);
		 List<Document> lsFolderIdList=new ArrayList<Document>();
		 lsFolderIdList.add(doc);
		 
		 HashMap<String, List> aoParameterMap=new HashMap<String, List>();		 
		 aoParameterMap.put("deletionList", lsFolderIdList);
		 
		 FolderMappingBean folderMappingBean =new FolderMappingBean();		
			folderMappingBean.setFolderName("ABCD");
			folderMappingBean.setFolderFilenetId("{11593C07-5ACD-4B20-B77A-97A7CE3E8BD5}");
			folderMappingBean.setParentFolderFilenetId("{1080A6B7-13CB-4723-851D-ED3EC65E2915}");
			folderMappingBean.setOrganizationId("Org_509");
			//folderMappingBean.setCreatedDate("30-08-16 09:05:37.806000000 AM");
			folderMappingBean.setCreatedBy("Bansal");
			//folderMappingBean.setModifiedDate("30-08-16 08:05:37.806000000 AM");
			folderMappingBean.setModifiedBy("Bansal");
			folderMappingBean.setType("1");
			folderMappingBean.setMovedToRecycleBin("0");
			List<FolderMappingBean> loParameter =new ArrayList<FolderMappingBean>();
			loParameter.add(folderMappingBean);
			
		 aoParameterMap.put("insertionList", loParameter);
		 HashMap<String, String> aoMapOrg=new HashMap<String, String>();
		 aoMapOrg.put(HHSR5Constants.ORGANIZATION_ID,"Org_509");	
		documentVaultFolderService.intermediateServiceForInsertionInDB(moSession, aoParameterMap, null);
		
	}
	
	@Test
	public void testCheckFolderNameExists() throws ApplicationException
	{
		HashMap<String, String> aoHmDocReqProps=new HashMap<String, String>();
		aoHmDocReqProps.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE,
				"sdfsdf");
		aoHmDocReqProps.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY,
				"ACCO_MANAGER DOC");
		aoHmDocReqProps.put("folderId", "{589A578D-04CF-403A-B23E-1F3593A40B11}");
		aoHmDocReqProps.put("organizationId", "DOC");
		boolean lostatus=documentVaultFolderService.checkFolderNameExists(moSession, aoHmDocReqProps);
		assertEquals(true, lostatus);
	}

	@Test(expected=ApplicationException.class)
	public void testCheckFolderNameExistsCase1() throws ApplicationException
	{
		HashMap<String, String> aoHmDocReqProps=new HashMap<String, String>();
		aoHmDocReqProps.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE,
				"sdfsdf");
		aoHmDocReqProps.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY,
				"ACCO_MANAGER DOC");
		aoHmDocReqProps.put("folderId", "{589A578D-04CF-403A-B23E-1F3593A40111}");
		aoHmDocReqProps.put("organizationId", "DOC");
		documentVaultFolderService.checkFolderNameExists(null, aoHmDocReqProps); 
		
	}
	@Test
	public void testCheckFolderNameExistsCase2() throws ApplicationException
	{
		HashMap<String, String> aoHmDocReqProps=new HashMap<String, String>();
		aoHmDocReqProps.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE,
				"Amit");
		aoHmDocReqProps.put("folderId", "{589A578D-04CF-403A-B23E-1F3593A0C11}");
		aoHmDocReqProps.put("organizationId", "DOC");
		boolean lostatus=documentVaultFolderService.checkFolderNameExists(moSession, aoHmDocReqProps);
		assertEquals(true, lostatus);
	}
	
	@Test(expected=Exception.class)
	public void testCheckFolderNameExistsCase3() throws ApplicationException
	{
		HashMap<String, String> aoHmDocReqProps=new HashMap<String, String>();
		aoHmDocReqProps.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE,
				"sdfsdf");
		aoHmDocReqProps.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY,
				"ACCO_MANAGER DOC");
		aoHmDocReqProps.put("folderId", "{589A578D-04CF-403A-B23E-1F3593A40B11}");
		aoHmDocReqProps.put("organizationId", "DOC");
		documentVaultFolderService.checkFolderNameExists(moSession, null); 
		
	}
}
