package com.nyc.hhs.controllers.utilTest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.cache.ICacheManager;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.model.Document;
import com.nyc.hhs.model.ExtendedDocument;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.RFPReleaseDocsUtil;
import com.nyc.hhs.util.XMLUtil;

public class RFPReleaseDocsUtilTest
{
	private static P8UserSession session = null;
	private final static String TEST_ORG = "test_org";
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
			session = getFileNetSession();
			ICacheManager loCacheManager = BaseCacheManagerWeb.getInstance();

			Object loCacheNotificationObject1 = XMLUtil.getDomObj((new RFPReleaseDocsUtilTest()).getClass()
					.getResourceAsStream("/com/nyc/hhs/config/ExtendedDocType.xml"));
			loCacheManager.putCacheObject(ApplicationConstants.FILENET_EXTENDED_DOC_TYPE, loCacheNotificationObject1);

			Object loCacheNotificationObject2 = XMLUtil.getDomObj((new RFPReleaseDocsUtilTest()).getClass()
					.getResourceAsStream("/com/nyc/hhs/config/DocType.xml"));
			loCacheManager.putCacheObject(ApplicationConstants.FILENETDOCTYPE, loCacheNotificationObject2);
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
	 * This method creates the Filenet session object.
	 * @return P8UserSession
	 * @throws java.lang.ApplicationException
	 */
	public static P8UserSession getFileNetSession() throws ApplicationException
	{
		System.setProperty(P8Constants.PROP_FILE_JAVA_SECURITY_AUTH_LOGIN_CONFIG, PropertyLoader.getProperty(
				P8Constants.PROPERTY_FILE, P8Constants.PROP_FILE_JAVA_SECURITY_AUTH_LOGIN_CONFIG));
		System.setProperty(P8Constants.PROP_FILE_JAVA_NAMING_FACTORY_INITIAL, PropertyLoader.getProperty(
				P8Constants.PROPERTY_FILE, P8Constants.PROP_FILE_JAVA_NAMING_FACTORY_INITIAL));
		System.setProperty(P8Constants.PROP_FILE_FILENET_PE_BOOTSTRAP_CEURI,
				PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "FILENET_URI"));

		P8UserSession loUserSession = new P8UserSession();
		loUserSession.setContentEngineUri(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "FILENET_URI"));
		loUserSession.setObjectStoreName(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "OBJECT_STORE_NAME"));
		loUserSession.setIsolatedRegionName(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
				"CONNECTION_POINT_NAME"));
		loUserSession.setUserId("ceadmin");
		loUserSession.setPassword("Filenet1");

		SqlSession loFilenetPEDBSession = HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory()
				.openSession();
		loUserSession.setFilenetPEDBSession(loFilenetPEDBSession);

		return loUserSession;
	}

	/**
	 * This method tests getSelectDocFromVaultChannel and checks that the
	 * channel object is populated with data.
	 */
	@Test
	public void testGetSelectDocFromVaultChannel() throws ApplicationException
	{
		String lsUserOrgType = ApplicationConstants.CITY_ORG;
		Channel loChannel = new Channel();

		RFPReleaseDocsUtil.getSelectDocFromVaultChannel(lsUserOrgType, session, loChannel);
		assertTrue(!loChannel.getData().isEmpty());
	}

	/**
	 * This method tests getProposalDocsFromVaultChannel and checks that the
	 * channel object is populated with data.
	 */
	@Test
	public void testGetProposalDocsFromVaultChannel() throws ApplicationException
	{
		String lsUserOrgType = ApplicationConstants.CITY_ORG;
		String asDocType = HHSConstants.ADDENDA;

		Channel loChannel = RFPReleaseDocsUtil.getProposalDocsFromVaultChannel(lsUserOrgType, asDocType, session);
		assertNotNull(loChannel);
		assertTrue(!loChannel.getData().isEmpty());
	}

	/**
	 * This method tests getRFPDocCategoryList and checks that the category list
	 * is not null.
	 */
	@Test
	public void testGetRFPDocCategoryList() throws ApplicationException
	{
		String lsUserOrg = ApplicationConstants.CITY_ORG;
		String lsFinancial = HHSConstants.BASE_FINANCIALS;

		List<String> loList = RFPReleaseDocsUtil.getRFPDocCategoryList(lsUserOrg, lsFinancial);
		assertNotNull(loList);
	}

	/**
	 * This method tests getRFPDocTypeForDocCategory and checks that the doc
	 * type list is not null or empty.
	 */
	@Test
	public void testGtRFPDocTypeForDocCategory() throws ApplicationException
	{
		String lsUserOrg = ApplicationConstants.CITY_ORG;
		String lsDocCategory = HHSConstants.SOLICITATION_CATEGORY;

		List<String> loList = RFPReleaseDocsUtil.getRFPDocTypeForDocCategory(lsDocCategory, lsUserOrg);
		assertNotNull(loList);
		assertTrue(!loList.isEmpty());
	}

	/**
	 * This method tests setSelectedDocumentBean when the data is not null or
	 * empty and checks that the extended document list is not null or empty.
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testSetSelectedDocumentBean() throws ApplicationException
	{
		Channel loChannel = new Channel();
		List<HashMap> documentList = new ArrayList<HashMap>();
		HashMap<String, Object> loMap = new HashMap<String, Object>();

		loMap.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, "My Document");

		loMap.put(P8Constants.PROPERTY_CE_DOC_TYPE, HHSConstants.ADDENDA);
		loMap.put(P8Constants.PROPERTY_CE_DOC_CATEGORY, HHSConstants.SOLICITATION_CATEGORY);
		loMap.put(P8Constants.PROPERTY_CE_LAST_MODIFIED_DATE, new Date());
		loMap.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY_ID, HHSConstants.CBL_CITY_142);
		loMap.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY, HHSConstants.CBL_CITY_142);

		loMap.put(P8Constants.PROPERTY_CE_DOCUMENT_ID, "1234");
		loMap.put(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY_ID, "1256");
		loMap.put(P8Constants.PROPERTY_CE_DATE_CREATED, new Date());

		documentList.add(loMap);

		loChannel.setData(ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_LIST_PARAMETER, documentList);

		List<ExtendedDocument> loList = RFPReleaseDocsUtil.setSelectedDocumentBean(loChannel);
		assertNotNull(loList);
		assertTrue(!loList.isEmpty());
	}

	/**
	 * This method tests setSelectedDocumentBean when the data is empty and
	 * checks that the extended document list is not null or empty.
	 */
	@Test
	@SuppressWarnings("rawtypes")
	public void testSetSelectedDocumentBeanWithAllDataEmpty() throws ApplicationException
	{
		Channel loChannel = new Channel();
		List<HashMap> documentList = new ArrayList<HashMap>();
		HashMap<String, Object> loMap = new HashMap<String, Object>();

		loMap.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, HHSConstants.EMPTY_STRING);

		loMap.put(P8Constants.PROPERTY_CE_DOC_TYPE, HHSConstants.EMPTY_STRING);
		loMap.put(P8Constants.PROPERTY_CE_DOC_CATEGORY, HHSConstants.EMPTY_STRING);
		loMap.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY_ID, HHSConstants.EMPTY_STRING);
		loMap.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY, HHSConstants.EMPTY_STRING);

		loMap.put(P8Constants.PROPERTY_CE_DOCUMENT_ID, HHSConstants.EMPTY_STRING);
		loMap.put(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY_ID, HHSConstants.EMPTY_STRING);

		documentList.add(loMap);

		loChannel.setData(ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_LIST_PARAMETER, documentList);

		List<ExtendedDocument> loList = RFPReleaseDocsUtil.setSelectedDocumentBean(loChannel);
		assertNotNull(loList);
		assertTrue(!loList.isEmpty());
	}

	/**
	 * This method tests setSelectedDocumentBean when the data is null and
	 * checks that the extended document list is not null or empty.
	 */
	@Test
	@SuppressWarnings("rawtypes")
	public void testSetSelectedDocumentBeanWithAllDataNull() throws ApplicationException
	{
		Channel loChannel = new Channel();
		List<HashMap> documentList = new ArrayList<HashMap>();
		HashMap<String, Object> loMap = new HashMap<String, Object>();

		loMap.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, null);

		loMap.put(P8Constants.PROPERTY_CE_DOC_TYPE, null);
		loMap.put(P8Constants.PROPERTY_CE_DOC_CATEGORY, null);
		loMap.put(P8Constants.PROPERTY_CE_LAST_MODIFIED_DATE, null);
		loMap.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY, null);
		loMap.put(P8Constants.PROPERTY_CE_LAST_MODIFIED_DATE, null);
		loMap.put(P8Constants.PROPERTY_CE_DOCUMENT_ID, null);
		loMap.put(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY_ID, null);
		loMap.put(P8Constants.PROPERTY_CE_DATE_CREATED, null);

		documentList.add(loMap);

		loChannel.setData(ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_LIST_PARAMETER, documentList);

		List<ExtendedDocument> loList = RFPReleaseDocsUtil.setSelectedDocumentBean(loChannel);
		assertNotNull(loList);
		assertTrue(!loList.isEmpty());
	}

	/**
	 * This method tests setSelectedDocumentBean when document list is empty and
	 * checks that the extended document list is empty.
	 */
	@Test
	@SuppressWarnings("rawtypes")
	public void testSetSelectedDocumentBeanDocListNull() throws ApplicationException
	{
		Channel loChannel = new Channel();
		List<HashMap> documentList = new ArrayList<HashMap>();

		loChannel.setData(ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_LIST_PARAMETER, documentList);

		List<ExtendedDocument> loList = RFPReleaseDocsUtil.setSelectedDocumentBean(loChannel);
		assertNotNull(loList);
		assertTrue(loList.isEmpty());
	}

	/**
	 * This method tests getDocCategoryForDocType and checks that the document
	 * category is correctly fetched for the given doc type and user
	 * organization id.
	 */
	@Test
	public void testGetDocCategoryForDocType() throws ApplicationException
	{
		String lsDocType = HHSConstants.ADDENDA;
		String lsUserOrg = ApplicationConstants.CITY_ORG;

		String lsDocCategory = RFPReleaseDocsUtil.getDocCategoryForDocType(lsDocType, lsUserOrg);
		assertTrue(lsDocCategory.equals(HHSConstants.SOLICITATION_CATEGORY));
	}

	/**
	 * The Method will test getDocCategoryForDocType method when lsUserOrg is
	 * passed as empty. It will throw Application Exception
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testGetDocCategoryForDocTypeOrgTypeEmpty() throws ApplicationException
	{
		String lsDocType = HHSConstants.ADDENDA;
		String lsUserOrg = HHSConstants.EMPTY_STRING;

		String lsDocCategory = RFPReleaseDocsUtil.getDocCategoryForDocType(lsDocType, lsUserOrg);
		assertNull(lsDocCategory);
	}

	/**
	 * The Method will test getDocCategoryForDocType method when lsUserOrg is
	 * passed as null. It will throw Application Exception
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testGetDocCategoryForDocTypeOrgTypeNull() throws ApplicationException
	{
		String lsDocType = HHSConstants.ADDENDA;
		String lsUserOrg = null;

		String lsDocCategory = RFPReleaseDocsUtil.getDocCategoryForDocType(lsDocType, lsUserOrg);
		assertNull(lsDocCategory);
	}

	/**
	 * The Method will test getDocCategoryForDocType method when lsDocType is
	 * passed as empty. It will throw Application Exception
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testGetDocCategoryForDocTypeDocTypeEmpty() throws ApplicationException
	{
		String lsDocType = HHSConstants.EMPTY_STRING;
		String lsUserOrg = ApplicationConstants.CITY_ORG;

		String lsDocCategory = RFPReleaseDocsUtil.getDocCategoryForDocType(lsDocType, lsUserOrg);
		assertNull(lsDocCategory);
	}

	/**
	 * The Method will test getDocCategoryForDocType method when lsDocType is
	 * passed as null. It will throw Application Exception
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testGetDocCategoryForDocTypeDocTypeNull() throws ApplicationException
	{
		String lsDocType = null;
		String lsUserOrg = ApplicationConstants.CITY_ORG;

		String lsDocCategory = RFPReleaseDocsUtil.getDocCategoryForDocType(lsDocType, lsUserOrg);
		assertNull(lsDocCategory);
	}

	/**
	 * This method tests setRFPDocCategorynDocType and checks that for the given
	 * data the document category list and document type list is not empty.
	 */
	@Test
	public void testSetRFPDocCategorynDocType() throws ApplicationException
	{
		Document loDoc = new Document();
		String lsDocCategory = HHSConstants.SOLICITATION_CATEGORY;
		String lsUserOrg = ApplicationConstants.CITY_ORG;
		String lsFinancial = HHSConstants.BASE_FINANCIALS;

		RFPReleaseDocsUtil.setRFPDocCategorynDocType(loDoc, lsDocCategory, lsUserOrg, lsFinancial);
		assertNotNull(loDoc.getCategoryList());
		assertTrue(loDoc.getCategoryList().size() > 0);
		assertNotNull(loDoc.getTypeList());
		assertTrue(loDoc.getTypeList().size() > 0);

	}

	/**
	 * This method tests setRFPDocCategorynDocType when lsFinancial is null and
	 * checks that the document category list and document type list is not
	 * empty.
	 */
	@Test
	public void testSetRFPDocCategorynDocTypeFinancialNull() throws ApplicationException
	{
		Document loDoc = new Document();
		String lsDocCategory = HHSConstants.SOLICITATION_CATEGORY;
		String lsUserOrg = ApplicationConstants.CITY_ORG;
		String lsFinancial = null;

		RFPReleaseDocsUtil.setRFPDocCategorynDocType(loDoc, lsDocCategory, lsUserOrg, lsFinancial);
		assertNotNull(loDoc.getCategoryList());
		assertTrue(loDoc.getCategoryList().size() > 0);
		assertNotNull(loDoc.getTypeList());
		assertTrue(loDoc.getTypeList().size() > 0);
	}

	/**
	 * This method tests setRFPDocCategorynDocType when lsDocCategory is null
	 * and checks that the document category list is not empty and document type
	 * list is null.
	 */
	@Test
	public void testSetRFPDocCategorynDocTypeDocCatNull() throws ApplicationException
	{
		Document loDoc = new Document();
		;
		String lsDocCategory = null;
		String lsUserOrg = ApplicationConstants.CITY_ORG;
		String lsFinancial = HHSConstants.BASE_FINANCIALS;

		RFPReleaseDocsUtil.setRFPDocCategorynDocType(loDoc, lsDocCategory, lsUserOrg, lsFinancial);
		assertNotNull(loDoc.getCategoryList());
		assertTrue(loDoc.getCategoryList().size() > 0);
		assertNull(loDoc.getTypeList());
	}

	/**
	 * This method tests setRFPDocCategorynDocType when lsDocCategory is empty
	 * and checks that the document category list is not empty and document type
	 * list is null.
	 */
	@Test
	public void testSetRFPDocCategorynDocTypeDocCatEmpty() throws ApplicationException
	{
		Document loDoc = new Document();
		String lsDocCategory = HHSConstants.EMPTY_STRING;
		String lsUserOrg = ApplicationConstants.CITY_ORG;
		String lsFinancial = HHSConstants.BASE_FINANCIALS;

		RFPReleaseDocsUtil.setRFPDocCategorynDocType(loDoc, lsDocCategory, lsUserOrg, lsFinancial);
		assertNotNull(loDoc.getCategoryList());
		assertTrue(loDoc.getCategoryList().size() > 0);
		assertNull(loDoc.getTypeList());
	}

	/**
	 * The Method will test setRFPDocCategorynDocType method when lsUserOrg is
	 * passed as null. It will throw Application Exception
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testSetRFPDocCategorynDocTypeUserOrgNull() throws ApplicationException
	{
		Document loDoc = new Document();
		;
		String lsDocCategory = HHSConstants.SOLICITATION_CATEGORY;
		String lsUserOrg = null;
		String lsFinancial = HHSConstants.BASE_FINANCIALS;

		RFPReleaseDocsUtil.setRFPDocCategorynDocType(loDoc, lsDocCategory, lsUserOrg, lsFinancial);

	}

	/**
	 * The Method will test setRFPDocCategorynDocType method when lsUserOrg is
	 * passed as empty. It will throw Application Exception
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testSetRFPDocCategorynDocTypeUserOrgEmpty() throws ApplicationException
	{
		Document loDoc = new Document();
		;
		String lsDocCategory = HHSConstants.SOLICITATION_CATEGORY;
		String lsUserOrg = HHSConstants.EMPTY_STRING;
		String lsFinancial = HHSConstants.BASE_FINANCIALS;

		RFPReleaseDocsUtil.setRFPDocCategorynDocType(loDoc, lsDocCategory, lsUserOrg, lsFinancial);

	}

	/**
	 * This method tests getDoctypesFromXML when lsOrgId is provider_org and
	 * checks that the document type list is not empty.
	 */
	@Test
	public void testGetDoctypesFromXMLProviderOrg() throws ApplicationException
	{
		org.jdom.Document loDocTypeXML = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENET_EXTENDED_DOC_TYPE);
		String lsOrgId = ApplicationConstants.PROVIDER_ORG;

		List<String> loDocTypeList = RFPReleaseDocsUtil.getDoctypesFromXML(loDocTypeXML, lsOrgId);
		assertTrue(loDocTypeList.size() > 0);
	}

	/**
	 * This method tests getDoctypesFromXML when lsOrgId is empty and checks
	 * that the document type list is empty.
	 */
	@Test
	public void testGetDoctypesFromXMLOrgIdXMLEmpty() throws ApplicationException
	{
		org.jdom.Document loDocTypeXML = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENET_EXTENDED_DOC_TYPE);
		String lsOrgId = HHSConstants.EMPTY_STRING;

		List<String> loDocTypeList = RFPReleaseDocsUtil.getDoctypesFromXML(loDocTypeXML, lsOrgId);
		assertTrue(loDocTypeList.isEmpty());
	}

	/**
	 * This method tests getDoctypesFromXML when lsOrgId is null and checks that
	 * the document type list is empty.
	 */
	@Test
	public void testGetDoctypesFromXMLOrgIdNull() throws ApplicationException
	{
		org.jdom.Document loDocTypeXML = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENET_EXTENDED_DOC_TYPE);
		String lsOrgId = null;

		List<String> loDocTypeList = RFPReleaseDocsUtil.getDoctypesFromXML(loDocTypeXML, lsOrgId);
		assertTrue(loDocTypeList.isEmpty());
	}

	/**
	 * The Method will test getDoctypesFromXML method when loDocTypeXML is
	 * passed as null. It will throw Application Exception
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testGetDoctypesFromXMLDoctTypeXMLNull() throws ApplicationException
	{
		Object loDocTypeXML = null;
		String lsOrgId = ApplicationConstants.PROVIDER_ORG;

		List<String> loDocTypeList = RFPReleaseDocsUtil.getDoctypesFromXML(loDocTypeXML, lsOrgId);
		assertTrue(loDocTypeList.isEmpty());
	}

	/**
	 * The Method will test getDoctypesFromXML method when loDocTypeXML is
	 * passed as empty. It will throw Application Exception
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testGetDoctypesFromXMLDoctTypeXMLEmpty() throws ApplicationException
	{
		Object loDocTypeXML = HHSConstants.EMPTY_STRING;
		String lsOrgId = ApplicationConstants.PROVIDER_ORG;

		List<String> loDocTypeList = RFPReleaseDocsUtil.getDoctypesFromXML(loDocTypeXML, lsOrgId);
		assertTrue(loDocTypeList.isEmpty());
	}

	/**
	 * This method tests getDoctypesFromXML when lsOrgId is city_org and checks
	 * that the document type list is empty.
	 */
	@Test
	public void testGetDoctypesFromXMLCityOrg() throws ApplicationException
	{
		org.jdom.Document loDocTypeXML = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENET_EXTENDED_DOC_TYPE);
		String lsOrgId = ApplicationConstants.CITY_ORG;

		List<String> loDocTypeList = RFPReleaseDocsUtil.getDoctypesFromXML(loDocTypeXML, lsOrgId);
		assertTrue(loDocTypeList.isEmpty());
	}

	/**
	 * This method tests getDoctypesFromXML when lsOrgId is agency_org and
	 * checks that the document type list is empty.
	 */
	@Test
	public void testGetDoctypesFromXMLAgencyOrg() throws ApplicationException
	{
		org.jdom.Document loDocTypeXML = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENET_EXTENDED_DOC_TYPE);
		String lsOrgId = ApplicationConstants.AGENCY_ORG;

		List<String> loDocTypeList = RFPReleaseDocsUtil.getDoctypesFromXML(loDocTypeXML, lsOrgId);
		assertTrue(loDocTypeList.isEmpty());
	}

	/**
	 * This method tests getDoctypesFromXML when lsOrgId is some value which is
	 * not present in the xml and checks that the document type list is empty.
	 */
	@Test
	public void testGetDoctypesFromXMLTestOrg() throws ApplicationException
	{
		org.jdom.Document loDocTypeXML = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENET_EXTENDED_DOC_TYPE);
		String lsOrgId = TEST_ORG;

		List<String> loDocTypeList = RFPReleaseDocsUtil.getDoctypesFromXML(loDocTypeXML, lsOrgId);
		assertTrue(loDocTypeList.isEmpty());
	}

	/**
	 * This method tests getRFPDocCategoryFromXML when lsOrgId is city_org and
	 * checks that the document category list is empty.
	 */
	@Test
	public void testGetRFPDocCategoryFromXMLCity() throws ApplicationException
	{
		org.jdom.Document aoDocTypeXML = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENETDOCTYPE);
		String lsDocTypes = HHSConstants.ADDENDA;
		String lsOrgId = ApplicationConstants.CITY_ORG;
		String lsFinancial = HHSConstants.BASE_FINANCIALS;

		List<String> loList = RFPReleaseDocsUtil.getRFPDocCategoryFromXML(aoDocTypeXML, lsDocTypes, lsOrgId,
				lsFinancial);
		assertNotNull(loList);
		assertTrue(loList.isEmpty());
	}

	/**
	 * This method tests getRFPDocCategoryFromXML when lsOrgId is agency_org and
	 * checks that the document category list is empty.
	 */
	@Test
	public void testGetRFPDocCategoryFromXMLAgency() throws ApplicationException
	{
		org.jdom.Document aoDocTypeXML = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENETDOCTYPE);
		String lsDocTypes = HHSConstants.AGENCY_DOCUMENT;
		String lsOrgId = ApplicationConstants.AGENCY_ORG;
		String lsFinancial = HHSConstants.BASE_FINANCIALS;

		List<String> loList = RFPReleaseDocsUtil.getRFPDocCategoryFromXML(aoDocTypeXML, lsDocTypes, lsOrgId,
				lsFinancial);
		assertNotNull(loList);
		assertTrue(loList.isEmpty());
	}

	/**
	 * This method tests getRFPDocCategoryFromXML when lsOrgId is provider_org
	 * and checks that the document category list is empty.
	 */
	@Test
	public void testGetRFPDocCategoryFromXMLProvider() throws ApplicationException
	{
		org.jdom.Document aoDocTypeXML = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENETDOCTYPE);
		String lsDocTypes = "Paystubs";
		String lsOrgId = ApplicationConstants.PROVIDER_ORG;
		String lsFinancial = HHSConstants.BASE_FINANCIALS;

		List<String> loList = RFPReleaseDocsUtil.getRFPDocCategoryFromXML(aoDocTypeXML, lsDocTypes, lsOrgId,
				lsFinancial);
		assertNotNull(loList);
		assertTrue(loList.isEmpty());
	}

	/**
	 * This method tests getRFPDocCategoryFromXML when lsFinancial is null and
	 * checks that the document category list is not empty.
	 */
	@Test
	public void testGetRFPDocCategoryFromXMLFinancialNull() throws ApplicationException
	{
		org.jdom.Document aoDocTypeXML = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENETDOCTYPE);
		String lsDocTypes = HHSConstants.ADDENDA;
		String lsOrgId = ApplicationConstants.CITY_ORG;
		String lsFinancial = null;

		List<String> loList = RFPReleaseDocsUtil.getRFPDocCategoryFromXML(aoDocTypeXML, lsDocTypes, lsOrgId,
				lsFinancial);
		assertNotNull(loList);
		assertTrue(!loList.isEmpty());
	}

	/**
	 * This method tests getRFPDocCategoryFromXML when lsDocTypes is empty and
	 * checks that the document category list is not empty.
	 */
	@Test
	public void testGetRFPDocCategoryFromXMLDocTypeEmpty() throws ApplicationException
	{
		org.jdom.Document aoDocTypeXML = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENETDOCTYPE);
		String lsDocTypes = HHSConstants.EMPTY_STRING;
		String lsOrgId = ApplicationConstants.PROVIDER_ORG;
		String lsFinancial = HHSConstants.BASE_FINANCIALS;

		List<String> loList = RFPReleaseDocsUtil.getRFPDocCategoryFromXML(aoDocTypeXML, lsDocTypes, lsOrgId,
				lsFinancial);
		assertNotNull(loList);
		assertTrue(!loList.isEmpty());
	}

	/**
	 * This method tests getRFPDocCategoryFromXML when lsDocTypes is null and
	 * lsFinancial is not null and checks that the document category list is not
	 * empty.
	 */
	@Test
	public void testGetRFPDocCategoryFromXMLDocTypeNullFinancial() throws ApplicationException
	{
		org.jdom.Document aoDocTypeXML = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENETDOCTYPE);
		String lsDocTypes = null;
		String lsOrgId = ApplicationConstants.PROVIDER_ORG;
		String lsFinancial = HHSConstants.BASE_FINANCIALS;

		List<String> loList = RFPReleaseDocsUtil.getRFPDocCategoryFromXML(aoDocTypeXML, lsDocTypes, lsOrgId,
				lsFinancial);
		assertNotNull(loList);
		assertTrue(!loList.isEmpty());
	}

	/**
	 * This method tests getRFPDocCategoryFromXML when lsOrgId is some value not
	 * present in xml and checks that the document category list is empty.
	 */
	@Test
	public void testGetRFPDocCategoryFromXMLTestOrg() throws ApplicationException
	{
		org.jdom.Document aoDocTypeXML = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENETDOCTYPE);
		String lsDocTypes = HHSConstants.ADDENDA;
		String lsOrgId = TEST_ORG;
		String lsFinancial = HHSConstants.BASE_FINANCIALS;

		List<String> loList = RFPReleaseDocsUtil.getRFPDocCategoryFromXML(aoDocTypeXML, lsDocTypes, lsOrgId,
				lsFinancial);
		assertNotNull(loList);
		assertTrue(loList.isEmpty());
	}

	/**
	 * This method tests getRFPDocCategoryFromXML when lsDocTypes is null and
	 * lsFinancial is null and checks that the document category list is not
	 * empty.
	 */
	@Test
	public void testGetRFPDocCategoryFromXMLDocTypeNullNoFinancial() throws ApplicationException
	{
		org.jdom.Document aoDocTypeXML = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENETDOCTYPE);
		String lsDocTypes = null;
		String lsOrgId = ApplicationConstants.PROVIDER_ORG;
		String lsFinancial = null;

		List<String> loList = RFPReleaseDocsUtil.getRFPDocCategoryFromXML(aoDocTypeXML, lsDocTypes, lsOrgId,
				lsFinancial);
		assertNotNull(loList);
		assertTrue(!loList.isEmpty());
	}

	/**
	 * The Method will test getRFPDocCategoryFromXML method when lsOrgId is
	 * passed as empty. It will throw Application Exception
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testGetRFPDocCategoryFromXMLOrgEmpty() throws ApplicationException
	{
		org.jdom.Document aoDocTypeXML = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENETDOCTYPE);
		String lsDocTypes = HHSConstants.ADDENDA;
		String lsOrgId = HHSConstants.EMPTY_STRING;
		String lsFinancial = null;

		List<String> loList = RFPReleaseDocsUtil.getRFPDocCategoryFromXML(aoDocTypeXML, lsDocTypes, lsOrgId,
				lsFinancial);
		assertNotNull(loList);
		assertTrue(loList.isEmpty());
	}

	/**
	 * The Method will test getRFPDocCategoryFromXML method when lsOrgId is
	 * passed as null. It will throw Application Exception
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testGetRFPDocCategoryFromXMLOrgNull() throws ApplicationException
	{
		org.jdom.Document aoDocTypeXML = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENETDOCTYPE);
		String lsDocTypes = HHSConstants.ADDENDA;
		String lsOrgId = null;
		String lsFinancial = null;

		List<String> loList = RFPReleaseDocsUtil.getRFPDocCategoryFromXML(aoDocTypeXML, lsDocTypes, lsOrgId,
				lsFinancial);
		assertNotNull(loList);
		assertTrue(loList.isEmpty());
	}

	/**
	 * The Method will test getRFPDocCategoryFromXML method when aoDocTypeXML is
	 * passed as null. It will throw Application Exception
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testGetRFPDocCategoryFromXMLDocTypeXmlNull() throws ApplicationException
	{
		Object aoDocTypeXML = null;
		String lsDocTypes = HHSConstants.ADDENDA;
		String lsOrgId = ApplicationConstants.CITY_ORG;
		String lsFinancial = HHSConstants.BASE_FINANCIALS;

		List<String> loList = RFPReleaseDocsUtil.getRFPDocCategoryFromXML(aoDocTypeXML, lsDocTypes, lsOrgId,
				lsFinancial);
		assertNotNull(loList);
		assertTrue(!loList.isEmpty());
	}

	/**
	 * The Method will test getRFPDocCategoryFromXML method when aoDocTypeXML is
	 * passed as empty. It will throw Application Exception
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testGetRFPDocCategoryFromXMLDocTypeXmlEmpty() throws ApplicationException
	{
		Object aoDocTypeXML = HHSConstants.EMPTY_STRING;
		String lsDocTypes = HHSConstants.ADDENDA;
		String lsOrgId = ApplicationConstants.CITY_ORG;
		String lsFinancial = HHSConstants.BASE_FINANCIALS;

		List<String> loList = RFPReleaseDocsUtil.getRFPDocCategoryFromXML(aoDocTypeXML, lsDocTypes, lsOrgId,
				lsFinancial);
		assertNotNull(loList);
		assertTrue(!loList.isEmpty());
	}

	@Test
	public void getDocumentInfo() throws ApplicationException
	{
		HashMap<String, Object> loDocumentMap = RFPReleaseDocsUtil.getDocumentInfo(session, "city_org",
				"{B1C06A99-9468-482B-A60A-94CE0B5CB4B1}");
		assertNotNull(loDocumentMap);
	}

	@Test
	public void getDocCategoryForRfpOtherDocTypeTest1() throws ApplicationException
	{
		String lsCategory = RFPReleaseDocsUtil.getDocCategoryForRfpOtherDocType("Substitute W9", "provider_org");
		assertNotNull(lsCategory);
	}

	@Test(expected = ApplicationException.class)
	public void getDocCategoryForRfpOtherDocTypeTest2() throws ApplicationException
	{
		RFPReleaseDocsUtil.getDocCategoryForRfpOtherDocType("Annual Financial Statement", "provider_orgasdasd");
//		assertNotNull(lsCategory);
	}

	@Test(expected = ApplicationException.class)
	public void getDocCategoryForRfpOtherDocTypeTest3() throws ApplicationException
	{
		String lsCategory = RFPReleaseDocsUtil.getDocCategoryForRfpOtherDocType("Annual Financial Statement", null);
		assertNotNull(lsCategory);
	}

	@Test(expected = ApplicationException.class)
	public void getDocCategoryForRfpOtherDocTypeTest4() throws ApplicationException
	{
		String lsCategory = RFPReleaseDocsUtil.getDocCategoryForRfpOtherDocType(null,  "provider_org");
		assertNotNull(lsCategory);
	}

	@Test(expected = ApplicationException.class)
	public void getDocCategoryForRfpOtherDocTypeTest5() throws ApplicationException
	{
		Object loFromCache = BaseCacheManagerWeb.getInstance().getCacheObject(ApplicationConstants.FILENETDOCTYPE);
		BaseCacheManagerWeb.getInstance().putCacheObject(ApplicationConstants.FILENETDOCTYPE, null);
		String lsCategory = RFPReleaseDocsUtil.getDocCategoryForRfpOtherDocType("", "");
		BaseCacheManagerWeb.getInstance().putCacheObject(ApplicationConstants.FILENETDOCTYPE, loFromCache);
	}

}
