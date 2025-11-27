package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.TransactionConstants;
import com.nyc.hhs.daomanager.service.ProposalService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.ExtendedDocument;
import com.nyc.hhs.model.NotificationDataBean;
import com.nyc.hhs.model.ProposalDetailsBean;
import com.nyc.hhs.model.ProposalQuestionAnswerBean;
import com.nyc.hhs.model.SiteDetailsBean;
import com.nyc.hhs.model.UserBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8SecurityOperations;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.PropertyLoader;

public class ProposalServiceTest
{

	private static SqlSession moMyBatisSession = null;
	private ProposalService moProposalService = new ProposalService();

	public P8UserSession getFileNetSession() throws ApplicationException
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
		P8SecurityOperations loP8SecurityService = new P8SecurityOperations();
		loP8SecurityService.getPESession(loUserSession);
		loP8SecurityService.getObjectStore(loUserSession);

		return loUserSession;
	}

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
			moMyBatisSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
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
			moMyBatisSession.rollback();
			moMyBatisSession.close();
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	/**
	 * This method tests cancelProposal method of Proposal Service class
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCancelProposal() throws ApplicationException
	{
		Boolean lbCancelProposalStatus = moProposalService.cancelProposal(moMyBatisSession, "180", true);
		assertFalse(lbCancelProposalStatus);
	}

	/**
	 * This method tests cancelProposal method of Proposal Service class
	 * 
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void testCancelProposalAppExc() throws ApplicationException
	{
		Boolean lbCancelProposalStatus = moProposalService.cancelProposal(null, null, true);
		assertEquals(Boolean.TRUE, lbCancelProposalStatus);
	}

	/**
	 * This method tests cancelProposal method of Proposal Service class
	 * 
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void testCancelProposalCase1() throws ApplicationException
	{
		moProposalService.cancelProposal(moMyBatisSession, "##", true);
	}

	/**
	 * This method tests cancelProposal method of Proposal Service class
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCancelProposalCase2() throws ApplicationException
	{
		Boolean lbCancelProposalStatus = moProposalService.cancelProposal(moMyBatisSession, "180", false);
		assertEquals(Boolean.FALSE, lbCancelProposalStatus);
	}

	/**
	 * This method tests retract Proposal functionality of Proposal Service
	 * class
	 * 
	 * @throws Exception
	 */
	/**
	 * This method tests if retractProposal in database
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testRetractProposal() throws ApplicationException
	{
		Boolean lbCancelProposalStatus = moProposalService.retractProposal(moMyBatisSession, "140");
		assertEquals(Boolean.TRUE, lbCancelProposalStatus);
	}

	@Test
	public void testRetractProposal2() throws ApplicationException
	{
		Boolean lbCancelProposalStatus = moProposalService.retractProposal(moMyBatisSession, "150");
		assertEquals(Boolean.TRUE, lbCancelProposalStatus);
	}

	@Test
	public void testRetractProposal3() throws ApplicationException
	{
		Boolean lbCancelProposalStatus = moProposalService.retractProposal(moMyBatisSession, "118");
		assertFalse(lbCancelProposalStatus);
	}

	/**
	 * This method tests if retractProposal in database
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testRetractProposalCase3() throws ApplicationException
	{
		moProposalService.retractProposal(moMyBatisSession, "##");
	}

	/**
	 * This method tests if retractProposal in database
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testRetractProposalCase4() throws ApplicationException
	{
		moProposalService.retractProposal(null, "4");
	}

	@Test
	public void testCheckAllRequiredFieldsCompleted1() throws ApplicationException
	{
		String lsProposalId = "102";
		String lsProcurementId = "115";
		String lsProviderStatus = "11";
		Boolean lbRequiredFieldsComplete = moProposalService.checkAllRequiredFieldsCompleted(moMyBatisSession,
				lsProposalId, lsProcurementId, lsProviderStatus);
		assertTrue(lbRequiredFieldsComplete);
	}

	@Test
	public void testCheckAllRequiredFieldsCompleted2() throws ApplicationException
	{
		String lsProposalId = "102";
		String lsProcurementId = "115";
		String lsProviderStatus = "9";
		Boolean lbRequiredFieldsComplete = moProposalService.checkAllRequiredFieldsCompleted(moMyBatisSession,
				lsProposalId, lsProcurementId, lsProviderStatus);
		assertTrue(lbRequiredFieldsComplete);
	}

	@Test
	public void testCheckAllRequiredFieldsCompleted3() throws ApplicationException
	{
		String lsProposalId = "102";
		String lsProcurementId = "115";
		String lsProviderStatus = "12";
		Boolean lbRequiredFieldsComplete = moProposalService.checkAllRequiredFieldsCompleted(moMyBatisSession,
				lsProposalId, lsProcurementId, lsProviderStatus);
		assertTrue(lbRequiredFieldsComplete);
	}

	@Test(expected = ApplicationException.class)
	public void testCheckAllRequiredFieldsThrowApplicationException() throws ApplicationException
	{
		moProposalService.checkAllRequiredFieldsCompleted(moMyBatisSession, "37", null, null);
	}

	@Test
	public void testSubmitProposal() throws ApplicationException
	{
		HashMap<String, String> loPropMap = new HashMap<String, String>();
		Boolean lbRequiredFieldsComplete = moProposalService.submitProposal(moMyBatisSession, "119", "283", true,
				loPropMap);
		assertTrue(lbRequiredFieldsComplete);
	}

	@Test(expected = ApplicationException.class)
	public void testSubmitProposalThrowsApplicationException() throws ApplicationException
	{
		Map<String, String> map = new HashMap<String, String>();
		moProposalService.submitProposal(null, null, "city_142", true, map);
	}

	@Test
	public void testSubmitProposalCase1() throws ApplicationException
	{
		Map<String, String> map = new HashMap<String, String>();
		Boolean lbRequiredFieldsComplete = moProposalService.submitProposal(moMyBatisSession, "183", "623", false, map);
		assertFalse(lbRequiredFieldsComplete);
	}

	@Test
	public void testSubmitProposalCase2() throws ApplicationException
	{
		Map<String, String> map = new HashMap<String, String>();
		Boolean lbRequiredFieldsComplete = moProposalService.submitProposal(moMyBatisSession, "183", "623", true, map);
		assertTrue(lbRequiredFieldsComplete);
	}

	@Test
	public void testSubmitProposalCase3() throws ApplicationException
	{
		Map<String, String> map = new HashMap<String, String>();
		Boolean lbRequiredFieldsComplete = moProposalService.submitProposal(moMyBatisSession, "183", "623", false, map);
		assertFalse(lbRequiredFieldsComplete);
	}

	// Arun
	/*
	 * @Test public void testGetDocumentContentForType() throws
	 * ApplicationException { HashMap loDocumentContentMap =
	 * moP8ContentService.getDocumentContentByType(getFileNetSession(),
	 * ApplicationConstants.DOCUMENT_TYPE_APPLICATION_TERMS_AND_CONDITIONS);
	 * assertNotNull(loDocumentContentMap); }
	 */

	// proposal details screen test cases start
	/*
	 * This method tests the exceution of fetchProposalDetails
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchProposalDetailsCase1() throws ApplicationException
	{
		ProposalDetailsBean loPropBean = moProposalService.fetchProposalDetails(moMyBatisSession, "6");
		assertNull(loPropBean);

	}

	/*
	 * This method tests the exceution of fetchProposalDetails
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchProposalDetailsCase2() throws ApplicationException
	{
		ProposalDetailsBean loPropBean = moProposalService.fetchProposalDetails(moMyBatisSession, "151");
		assertNotNull(loPropBean);

	}

	/*
	 * This method tests the exceution of fetchProposalDetails
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchProposalDetailsCase3() throws ApplicationException
	{
		moProposalService.fetchProposalDetails(moMyBatisSession, "##");

	}

	/*
	 * This method tests the exceution of fetchProposalDetails
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchProposalDetailsCase4() throws ApplicationException
	{
		ProposalDetailsBean loPropBean = moProposalService.fetchProposalDetails(moMyBatisSession, "");
		assertNull(loPropBean);

	}

	@Test
	public void testFetchProposalSiteDetails() throws ApplicationException
	{
		String lsProposalId = "141";
		List<SiteDetailsBean> loSiteDetails = moProposalService.fetchProposalSiteDetails(moMyBatisSession,
				lsProposalId, null);
		assertNotNull(loSiteDetails);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchProposalSiteDetailsCase3() throws ApplicationException
	{
		String lsUserType = "";
		moProposalService.fetchProposalSiteDetails(moMyBatisSession, null, lsUserType);
	}

	@Test
	public void testFetchAllOrganizationMembers() throws ApplicationException
	{
		List<Map<String, String>> loList = moProposalService.fetchAllOrganizationMembers(moMyBatisSession, "2404");
		assertNotNull(loList);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchAllOrganizationMembersCase1() throws ApplicationException
	{
		String lsUserId = "2404";
		moProposalService.fetchAllOrganizationMembers(null, lsUserId);
	}

	@Test
	public void testFetchAllOrganizationMembersCase2() throws ApplicationException
	{
		String lsUserId = "2404";
		List<Map<String, String>> loList = moProposalService.fetchAllOrganizationMembers(moMyBatisSession, lsUserId);
		assertNotNull(loList);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchMemberDetails() throws ApplicationException
	{
		Map<String, String> loMemberDetails = moProposalService.fetchMemberDetails(moMyBatisSession, "2404", null);
		assertNotNull(loMemberDetails);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchMemberDetailsCase1() throws ApplicationException
	{
		String lsUserId = "2404";
		Map<String, String> loMemberDetails = moProposalService.fetchMemberDetails(moMyBatisSession, lsUserId, null);
		assertNotNull(loMemberDetails);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchMemberDetailsCase2() throws ApplicationException
	{
		String lsUserId = "##";
		Map<String, String> loMemberDetails = moProposalService.fetchMemberDetails(moMyBatisSession, lsUserId, null);
		assertNull(loMemberDetails);
	}

	/*
	 * This method tests the execution of saveProposalDetails
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testSaveProposalDetails() throws ApplicationException
	{
		ProposalDetailsBean loPropBean = new ProposalDetailsBean();
		loPropBean.setCostPerUnit("5,555.00");
		loPropBean.setModifiedBy("283");
		loPropBean.setProcurementId("115");
		loPropBean.setProposalId("102");
		loPropBean.setProposalTitle("something new 6");
		loPropBean.setProviderContactId("283");
		loPropBean.setTotalFundingRequest("11,111.00");
		loPropBean.setTotalNumberOfService("2");
		loPropBean.setCompetitionPool("154");
		List<ProposalQuestionAnswerBean> loQuestionBean = new ArrayList<ProposalQuestionAnswerBean>();
		ProposalQuestionAnswerBean loQuesObj = new ProposalQuestionAnswerBean();
		loQuesObj.setAnswerText("121212");
		loQuesObj.setProcurementQnId("114");
		loQuesObj.setQuestionSeqNo("2");
		loQuestionBean.add(loQuesObj);
		loPropBean.setQuestionAnswerBeanList(loQuestionBean);
		List<SiteDetailsBean> loSiteBean = new ArrayList<SiteDetailsBean>();

		SiteDetailsBean loSite1 = new SiteDetailsBean();
		loSite1.setActionTaken("update");
		loSite1.setAddress1("asdasd");
		loSite1.setAddress2("dasda");
		loSite1.setCity("a");
		loSite1.setProposalSiteId("180");
		loSite1.setSiteName("update site");
		loSite1.setState("AL");
		loSite1.setZipCode("11111");
		loSite1.setAddressRelatedData(" k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0rasdk3yv@lu3S3p@r@t0rak3yv@lu3S3p@r@t0rALk3yv@lu3S3p@r@t0r13123k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r ");

		loSiteBean.add(loSite1);
		loPropBean.setSiteDetailsList(loSiteBean);
		boolean lbFlag = moProposalService.saveProposalDetails(moMyBatisSession, loPropBean);
		assertTrue(lbFlag);
	}

	/*
	 * This method tests the execution of saveProposalDetails
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testSaveProposalDetails2() throws ApplicationException
	{
		ProposalDetailsBean loPropBean = new ProposalDetailsBean();
		loPropBean.setCostPerUnit("5,555.00");
		loPropBean.setModifiedBy("283");
		loPropBean.setProcurementId("115");
		loPropBean.setProposalId("102");
		loPropBean.setProposalTitle("something new 6");
		loPropBean.setProviderContactId("283");
		loPropBean.setTotalFundingRequest("11,111.00");
		loPropBean.setTotalNumberOfService("2");
		loPropBean.setCompetitionPool("154");
		List<ProposalQuestionAnswerBean> loQuestionBean = new ArrayList<ProposalQuestionAnswerBean>();
		ProposalQuestionAnswerBean loQuesObj = new ProposalQuestionAnswerBean();
		loQuesObj.setAnswerText("121212");
		loQuesObj.setProcurementQnId("114");
		loQuesObj.setQuestionSeqNo("2");
		loQuestionBean.add(loQuesObj);
		loPropBean.setQuestionAnswerBeanList(loQuestionBean);
		List<SiteDetailsBean> loSiteBean = new ArrayList<SiteDetailsBean>();

		SiteDetailsBean loSite1 = new SiteDetailsBean();
		loSite1.setActionTaken("insert");
		loSite1.setAddress1("asdasd");
		loSite1.setAddress2("dasda");
		loSite1.setCity("a");
		loSite1.setProposalSiteId("180");
		loSite1.setSiteName("update site");
		loSite1.setState("AL");
		loSite1.setZipCode("11111");
		loSite1.setAddressRelatedData(" k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0rasdk3yv@lu3S3p@r@t0rak3yv@lu3S3p@r@t0rALk3yv@lu3S3p@r@t0r13123k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r ");

		loSiteBean.add(loSite1);
		loPropBean.setSiteDetailsList(loSiteBean);
		boolean lbFlag = moProposalService.saveProposalDetails(moMyBatisSession, loPropBean);
		assertTrue(lbFlag);
	}

	/*
	 * This method tests the execution of saveProposalDetails
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testSaveProposalDetails3() throws ApplicationException
	{
		ProposalDetailsBean loPropBean = new ProposalDetailsBean();
		loPropBean.setCostPerUnit("5,555.00");
		loPropBean.setModifiedBy("283");
		loPropBean.setProcurementId("115");
		loPropBean.setProposalId("102");
		loPropBean.setProposalTitle("something new 6");
		loPropBean.setProviderContactId("283");
		loPropBean.setTotalFundingRequest("11,111.00");
		loPropBean.setTotalNumberOfService("2");
		loPropBean.setCompetitionPool("154");
		List<ProposalQuestionAnswerBean> loQuestionBean = new ArrayList<ProposalQuestionAnswerBean>();
		ProposalQuestionAnswerBean loQuesObj = new ProposalQuestionAnswerBean();
		loQuesObj.setAnswerText("121212");
		loQuesObj.setProcurementQnId("114");
		loQuesObj.setQuestionSeqNo("2");
		loQuestionBean.add(loQuesObj);
		loPropBean.setQuestionAnswerBeanList(loQuestionBean);
		List<SiteDetailsBean> loSiteBean = new ArrayList<SiteDetailsBean>();

		SiteDetailsBean loSite1 = new SiteDetailsBean();
		loSite1.setActionTaken("delete");
		loSite1.setAddress1("asdasd");
		loSite1.setAddress2("dasda");
		loSite1.setCity("a");
		loSite1.setProposalSiteId("180");
		loSite1.setSiteName("update site");
		loSite1.setState("AL");
		loSite1.setZipCode("11111");
		loSite1.setAddressRelatedData(" k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0rasdk3yv@lu3S3p@r@t0rak3yv@lu3S3p@r@t0rALk3yv@lu3S3p@r@t0r13123k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r ");

		loSiteBean.add(loSite1);
		loPropBean.setSiteDetailsList(loSiteBean);
		boolean lbFlag = moProposalService.saveProposalDetails(moMyBatisSession, loPropBean);
		assertTrue(lbFlag);
	}

	/*
	 * This method tests the execution of saveProposalDetails
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testSaveProposalDetailsCase3() throws ApplicationException
	{
		ProposalDetailsBean loPropBean = new ProposalDetailsBean();
		loPropBean.setCostPerUnit("5,555.00");
		loPropBean.setModifiedBy("623");
		loPropBean.setProcurementId("601");
		loPropBean.setProposalId("180");
		loPropBean.setProposalTitle("something new 6");
		loPropBean.setProviderContactId("2404");
		loPropBean.setTotalFundingRequest("11,111.00");
		loPropBean.setTotalNumberOfService("2");
		List<ProposalQuestionAnswerBean> loQuestionBean = new ArrayList<ProposalQuestionAnswerBean>();
		ProposalQuestionAnswerBean loQuesObj = new ProposalQuestionAnswerBean();
		loQuesObj.setAnswerText("121212");
		loQuesObj.setProcurementQnId("499");
		loQuesObj.setQuestionSeqNo("2");
		loQuestionBean.add(loQuesObj);
		loPropBean.setQuestionAnswerBeanList(loQuestionBean);
		List<SiteDetailsBean> loSiteBean = new ArrayList<SiteDetailsBean>();

		SiteDetailsBean loSite1 = new SiteDetailsBean();
		loSite1.setActionTaken("insert");
		loSite1.setAddress1("asdasd");
		loSite1.setAddress2("dasda");
		loSite1.setCity("a");
		loSite1.setProposalSiteId("180");
		loSite1.setSiteName("update site");
		loSite1.setState("AL");
		loSite1.setZipCode("11111");
		loSite1.setAddressRelatedData(" k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0rasdk3yv@lu3S3p@r@t0rak3yv@lu3S3p@r@t0rALk3yv@lu3S3p@r@t0r13123k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r ");

		loSiteBean.add(loSite1);
		loPropBean.setSiteDetailsList(null);
		boolean lbFlag = moProposalService.saveProposalDetails(moMyBatisSession, loPropBean);
		assertTrue(lbFlag);
	}

	/*
	 * This method tests the execution of saveProposalDetails
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testSaveProposalDetailsCase4() throws ApplicationException
	{
		ProposalDetailsBean loPropBean = new ProposalDetailsBean();
		loPropBean.setCostPerUnit("5,555.00");
		loPropBean.setModifiedBy("623");
		loPropBean.setProcurementId("##");
		loPropBean.setProposalId("180");
		loPropBean.setProposalTitle("something new 6");
		loPropBean.setProviderContactId("2404");
		loPropBean.setTotalFundingRequest("11,111.00");
		loPropBean.setTotalNumberOfService("2");
		List<ProposalQuestionAnswerBean> loQuestionBean = new ArrayList<ProposalQuestionAnswerBean>();
		ProposalQuestionAnswerBean loQuesObj = new ProposalQuestionAnswerBean();
		loQuesObj.setAnswerText("121212");
		loQuesObj.setProcurementQnId("499");
		loQuesObj.setQuestionSeqNo("2");
		loQuestionBean.add(loQuesObj);
		loPropBean.setQuestionAnswerBeanList(loQuestionBean);
		List<SiteDetailsBean> loSiteBean = new ArrayList<SiteDetailsBean>();

		SiteDetailsBean loSite1 = new SiteDetailsBean();
		loSite1.setActionTaken("insert");
		loSite1.setAddress1("asdasd");
		loSite1.setAddress2("dasda");
		loSite1.setCity("a");
		loSite1.setProposalSiteId("180");
		loSite1.setSiteName("update site");
		loSite1.setState("AL");
		loSite1.setZipCode("11111");
		loSite1.setAddressRelatedData(" k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0rasdk3yv@lu3S3p@r@t0rak3yv@lu3S3p@r@t0rALk3yv@lu3S3p@r@t0r13123k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r ");

		loSiteBean.add(loSite1);
		loPropBean.setSiteDetailsList(null);
		boolean lbFlag = moProposalService.saveProposalDetails(moMyBatisSession, loPropBean);
		assertTrue(lbFlag);
	}

	/*
	 * This method tests the execution of saveProposalDetails
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testSaveProposalDetailsCase5() throws ApplicationException
	{
		ProposalDetailsBean loPropBean = new ProposalDetailsBean();
		loPropBean.setCostPerUnit("5,555.00");
		loPropBean.setModifiedBy("623");
		loPropBean.setProcurementId("601");
		loPropBean.setProposalId("##");
		loPropBean.setProposalTitle("something new 6");
		loPropBean.setProviderContactId("2404");
		loPropBean.setTotalFundingRequest("11,111.00");
		loPropBean.setTotalNumberOfService("2");
		List<ProposalQuestionAnswerBean> loQuestionBean = new ArrayList<ProposalQuestionAnswerBean>();
		ProposalQuestionAnswerBean loQuesObj = new ProposalQuestionAnswerBean();
		loQuesObj.setAnswerText("121212");
		loQuesObj.setProcurementQnId("499");
		loQuesObj.setQuestionSeqNo("2");
		loQuestionBean.add(loQuesObj);
		loPropBean.setQuestionAnswerBeanList(loQuestionBean);
		List<SiteDetailsBean> loSiteBean = new ArrayList<SiteDetailsBean>();

		SiteDetailsBean loSite1 = new SiteDetailsBean();
		loSite1.setActionTaken("insert");
		loSite1.setAddress1("asdasd");
		loSite1.setAddress2("dasda");
		loSite1.setCity("a");
		loSite1.setProposalSiteId("180");
		loSite1.setSiteName("update site");
		loSite1.setState("AL");
		loSite1.setZipCode("11111");
		loSite1.setAddressRelatedData(" k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0rasdk3yv@lu3S3p@r@t0rak3yv@lu3S3p@r@t0rALk3yv@lu3S3p@r@t0r13123k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r ");

		loSiteBean.add(loSite1);
		loPropBean.setSiteDetailsList(null);
		boolean lbFlag = moProposalService.saveProposalDetails(moMyBatisSession, loPropBean);
		assertTrue(lbFlag);
	}

	/*
	 * This method tests the execution of saveProposalDetails
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testSaveProposalDetailsCase6() throws ApplicationException
	{
		ProposalDetailsBean loPropBean = new ProposalDetailsBean();
		loPropBean.setCostPerUnit("5,555.00");
		loPropBean.setModifiedBy("623");
		loPropBean.setProcurementId("601");
		loPropBean.setProposalId("180");
		loPropBean.setProposalTitle("something new 6");
		loPropBean.setProviderContactId("##");
		loPropBean.setTotalFundingRequest("11,111.00");
		loPropBean.setTotalNumberOfService("2");
		List<ProposalQuestionAnswerBean> loQuestionBean = new ArrayList<ProposalQuestionAnswerBean>();
		ProposalQuestionAnswerBean loQuesObj = new ProposalQuestionAnswerBean();
		loQuesObj.setAnswerText("121212");
		loQuesObj.setProcurementQnId("499");
		loQuesObj.setQuestionSeqNo("2");
		loQuestionBean.add(loQuesObj);
		loPropBean.setQuestionAnswerBeanList(loQuestionBean);
		List<SiteDetailsBean> loSiteBean = new ArrayList<SiteDetailsBean>();

		SiteDetailsBean loSite1 = new SiteDetailsBean();
		loSite1.setActionTaken("insert");
		loSite1.setAddress1("asdasd");
		loSite1.setAddress2("dasda");
		loSite1.setCity("a");
		loSite1.setProposalSiteId("180");
		loSite1.setSiteName("update site");
		loSite1.setState("AL");
		loSite1.setZipCode("11111");
		loSite1.setAddressRelatedData(" k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0rasdk3yv@lu3S3p@r@t0rak3yv@lu3S3p@r@t0rALk3yv@lu3S3p@r@t0r13123k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r k3yv@lu3S3p@r@t0r ");

		loSiteBean.add(loSite1);
		loPropBean.setSiteDetailsList(null);
		boolean lbFlag = moProposalService.saveProposalDetails(moMyBatisSession, loPropBean);
		assertTrue(lbFlag);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchProposalDetailsExc() throws ApplicationException
	{
		moProposalService.fetchProposalDetails(moMyBatisSession, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchProposalSiteDetailsExc() throws ApplicationException
	{
		moProposalService.fetchProposalSiteDetails(moMyBatisSession, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchAllOrganizationMembersExc() throws ApplicationException
	{
		moProposalService.fetchAllOrganizationMembers(moMyBatisSession, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchMemberDetailsExc() throws ApplicationException
	{
		moProposalService.fetchMemberDetails(moMyBatisSession, null, null);
	}

	/*
	 * This method tests the execution of saveProposalDetails
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testSaveProposalDetailsExc() throws ApplicationException
	{
		ProposalDetailsBean loPropBean = new ProposalDetailsBean();
		moProposalService.saveProposalDetails(moMyBatisSession, loPropBean);
	}

	// proposal details screen test cases end

	@Test
	public void testGetProposalSummary1() throws ApplicationException
	{
		ProposalDetailsBean aoProposalDetailsBean = new ProposalDetailsBean();
		aoProposalDetailsBean.setProcurementId("114");
		aoProposalDetailsBean.setOrganizationId("test");
		List<ProposalDetailsBean> loProposalSummary = (List<ProposalDetailsBean>) moProposalService.getProposalSummary(
				moMyBatisSession, aoProposalDetailsBean);
		assertNotNull(loProposalSummary);
	}

	@Test
	public void testGetProposalSummary2() throws ApplicationException
	{
		ProposalDetailsBean aoProposalDetailsBean = new ProposalDetailsBean();
		aoProposalDetailsBean.setProcurementId("134");
		aoProposalDetailsBean.setOrganizationId("testing");
		List<ProposalDetailsBean> loProposalSummary = (List<ProposalDetailsBean>) moProposalService.getProposalSummary(
				moMyBatisSession, aoProposalDetailsBean);
		String lsActualResult = loProposalSummary.get(0).getProposalStatus();
		assertNull(lsActualResult);
	}

	@Test(expected = ApplicationException.class)
	public void testGetProposalSummaryAppException() throws ApplicationException
	{
		ProposalDetailsBean aoProposalDetailsBean = new ProposalDetailsBean();
		aoProposalDetailsBean.setProcurementId(null);
		List<ProposalDetailsBean> loProposalSummary = (List<ProposalDetailsBean>) moProposalService.getProposalSummary(
				moMyBatisSession, aoProposalDetailsBean);
		String lsActualResult = loProposalSummary.get(0).getProposalStatus();
		assertNotNull(lsActualResult);
	}

	@Test(expected = ApplicationException.class)
	public void testGetProposalSummaryException() throws ApplicationException
	{
		List<ProposalDetailsBean> loProposalSummary = (List<ProposalDetailsBean>) moProposalService.getProposalSummary(
				moMyBatisSession, null);
		String lsActualResult = loProposalSummary.get(0).getProposalStatus();
		assertNotNull(lsActualResult);
	}

	@Test
	public void testGetProposalCount1() throws ApplicationException
	{
		ProposalDetailsBean aoProposalDetailsBean = new ProposalDetailsBean();
		aoProposalDetailsBean.setProcurementId("114");
		aoProposalDetailsBean.setOrganizationId("test");
		Integer loProposalCount = (Integer) moProposalService.getProposalCount(moMyBatisSession, aoProposalDetailsBean);
		assertNotNull(loProposalCount);
	}

	@Test
	public void testGetProposalCount2() throws ApplicationException
	{
		ProposalDetailsBean aoProposalDetailsBean = new ProposalDetailsBean();
		aoProposalDetailsBean.setProcurementId("134");
		aoProposalDetailsBean.setOrganizationId("testing");
		Integer loProposalCount = (Integer) moProposalService.getProposalCount(moMyBatisSession, aoProposalDetailsBean);
		assertNotNull(loProposalCount);
	}

	@Test(expected = ApplicationException.class)
	public void testGetProposalCountAppException() throws ApplicationException
	{
		ProposalDetailsBean aoProposalDetailsBean = new ProposalDetailsBean();
		aoProposalDetailsBean.setProcurementId(null);
		Integer loProposalCount = (Integer) moProposalService.getProposalCount(moMyBatisSession, aoProposalDetailsBean);
		assertNotNull(loProposalCount);
	}

	@Test
	public void testGetProposalDocumentList() throws ApplicationException
	{
		Map<String, String> loParamMap = new HashMap<String, String>();
		loParamMap.put("procurementId", "77");
		loParamMap.put("proposalId", "37");
		List<ExtendedDocument> loProposalDocumentList = (List<ExtendedDocument>) moProposalService
				.getProposalDocumentList(moMyBatisSession, loParamMap);
		assertNotNull(loProposalDocumentList);
	}

	@Test(expected = ApplicationException.class)
	public void testGetProposalDocumentListExp() throws ApplicationException
	{
		Map<String, String> loParamMap = null;
		moProposalService.getProposalDocumentList(moMyBatisSession, loParamMap);
	}

	@Test(expected = ApplicationException.class)
	public void testGetProposalDocumentListCase1() throws ApplicationException
	{
		Map<String, String> loParamMap = new HashMap<String, String>();
		loParamMap.put("procurementId", "##");
		loParamMap.put("proposalId", "37");
		moProposalService.getProposalDocumentList(moMyBatisSession, loParamMap);
	}

	@Test(expected = ApplicationException.class)
	public void testGetProposalDocumentListCase2() throws ApplicationException
	{
		Map<String, String> loParamMap = new HashMap<String, String>();
		loParamMap.put("procurementId", "77");
		loParamMap.put("proposalId", "##");
		List<ExtendedDocument> loProposalDocumentList = (List<ExtendedDocument>) moProposalService
				.getProposalDocumentList(moMyBatisSession, loParamMap);
		assertNotNull(loProposalDocumentList);
	}

	/*
	 * This method test the execution of the insertProposalDocumentDetails
	 * method
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertProposalDocumentDetails() throws ApplicationException
	{
		Map<String, Object> loParameterMap = new HashMap<String, Object>();
		loParameterMap.put("procurementId", "114");
		loParameterMap.put("proposalId", "101");
		loParameterMap.put("procurementDocId", "113");
		loParameterMap.put("docTitle", "TestDoc");
		loParameterMap.put("docType", "TestDocType");
		loParameterMap.put("docReferenceNo", "113");
		loParameterMap.put("docCategory", "TestDocCategory");
		loParameterMap.put("documentId", "{729CFD7A-3212-448F-91C6-2C9F939E797B}");
		loParameterMap.put("docCreatedBy", "623");
		loParameterMap.put("docCreatedDate", new Date());
		loParameterMap.put("userId", "623");
		loParameterMap.put("docModifedDate", new Date());
		loParameterMap.put("docModifedBy", "623");
		loParameterMap.put("statusId", "28");
		Integer liRowsUpdated = (Integer) moProposalService.insertProposalDocumentDetails(moMyBatisSession,
				loParameterMap, "3", null);
		assertEquals(liRowsUpdated, Integer.valueOf(1));
	}

	/*
	 * This method test the execution of the insertProposalDocumentDetails
	 * method
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertProposalDocumentDetailsCase1() throws ApplicationException
	{
		Map<String, Object> loParameterMap = new HashMap<String, Object>();
		loParameterMap.put("procurementId", "111");
		loParameterMap.put("proposalId", "83");
		loParameterMap.put("procurementDocId", "109");
		loParameterMap.put("docTitle", "TestDoc");
		loParameterMap.put("docType", "TestDocType");
		loParameterMap.put("docReferenceNo", "109");
		loParameterMap.put("docCategory", "TestDocCategory");
		loParameterMap.put("documentId", "{729CFD7A-3212-448F-91C6-2C9F939E797B}");
		loParameterMap.put("docCreatedBy", "623");
		loParameterMap.put("docCreatedDate", new Date());
		loParameterMap.put("userId", "623");
		loParameterMap.put("docModifedDate", new Date());
		loParameterMap.put("docModifedBy", "623");
		loParameterMap.put("statusId", "28");
		Integer liRowsUpdated = (Integer) moProposalService.insertProposalDocumentDetails(moMyBatisSession,
				loParameterMap, "3", null);
		assertEquals(liRowsUpdated, Integer.valueOf(1));
	}

	/*
	 * This method test the execution of the insertProposalDocumentDetails
	 * method
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testInsertProposalDocumentDetailsExp() throws ApplicationException
	{
		Map<String, Object> loParamMap = null;
		moProposalService.insertProposalDocumentDetails(null, loParamMap, null, null);
	}

	// Arun
	@Test
	public void testRemoveProposalDocs() throws ApplicationException
	{
		Map<String, Object> loParamMap = new HashMap<String, Object>();
		loParamMap.put("procurementId", "114");
		loParamMap.put("proposalId", "101");
		loParamMap.put("procurementDocId", "113");
		loParamMap.put("documentId", "{SAD2DEB6-0F85-47A3-9390-F6AE8B351EE9}");
		loParamMap.put("userId", "283");
		loParamMap.put("statusId", "30");
		Integer liRowsDeleted = (Integer) moProposalService.removeProposalDocs(moMyBatisSession, loParamMap, true);
		assertNotNull(liRowsDeleted);
	}

	@Test(expected = ApplicationException.class)
	public void testRemoveProposalDocsException() throws ApplicationException
	{
		Map<String, Object> loParamMap = new HashMap<String, Object>();
		loParamMap.put("procurementId", "601");
		loParamMap.put("proposalId", "180");
		loParamMap.put("procurementDocId", "350");// 952
		moProposalService.removeProposalDocs(null, loParamMap, true);

	}

	@Test
	public void testRemoveProposalDocsCase1() throws ApplicationException
	{
		Map<String, Object> loParamMap = new HashMap<String, Object>();
		loParamMap.put("procurementId", "114");
		loParamMap.put("proposalId", "101");
		loParamMap.put("procurementDocId", "113");
		Integer liRowsDeleted = (Integer) moProposalService.removeProposalDocs(moMyBatisSession, loParamMap, false);
		assertNull(liRowsDeleted);
	}

	/*
	 * This method test the execution of the insertProposalDocumentDetails
	 * method
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testRemoveProposalDocsExp() throws ApplicationException
	{
		Map<String, Object> loParamMap = null;
		moProposalService.insertProposalDocumentDetails(null, loParamMap, null, null);

	}

	/*
	 * @Test public void testUpdateProposalDocumentProperties() throws
	 * ApplicationException { Map<String, Object> loParamMap = new
	 * HashMap<String, Object>(); loParamMap.put("procurementId", "77");
	 * loParamMap.put("proposalId", "37"); loParamMap.put("docTitle",
	 * "TestDocUpdated"); loParamMap.put("modifedBy", "623");
	 * loParamMap.put("procurementDocId", "350"); Boolean lbRowsUpdated =
	 * (Boolean)
	 * moProposalService.updateProposalDocumentProperties(moMyBatisSession,
	 * loParamMap, true); assertEquals(lbRowsUpdated, Boolean.TRUE); }
	 * 
	 * @Test(expected = ApplicationException.class) public void
	 * testUpdateProposalDocumentPropertiesExp() throws ApplicationException {
	 * Map<String, Object> loParamMap = null; Boolean lbRowsUpdated = (Boolean)
	 * moProposalService.updateProposalDocumentProperties(moMyBatisSession,
	 * loParamMap, true); }
	 */

	/**
	 * This method tests the execution of getProposalSiteDetails method and
	 * determines whether or not the Proposal Site Details List is getting
	 * generated.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testGetProposalSiteDetailsCase1() throws ApplicationException
	{
		String lsProposalId = "1";
		String lsUserType = "city_org";
		String lsSortSiteTable = "true";
		List<SiteDetailsBean> loSiteDetailsList = moProposalService.getProposalSiteDetails(moMyBatisSession,
				lsProposalId, lsUserType, lsSortSiteTable);
		assertNotNull(loSiteDetailsList);

	}

	/**
	 * This method tests the negative execution of getProposalSiteDetails method
	 * and determines whether or not the Proposal Site Details List is getting
	 * generated.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testGetProposalSiteDetailsCaseApplicationException() throws ApplicationException
	{
		String lsProposalId = null;
		String lsUserType = "city_org";
		String lsSortSiteTable = "true";
		moProposalService.getProposalSiteDetails(moMyBatisSession, lsProposalId, lsUserType, lsSortSiteTable);

	}

	/**
	 * This method tests the execution of getProposalDocuments method and
	 * determines whether or not the Proposal Documents List is getting
	 * generated.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testGetProposalDocumentsCase1() throws ApplicationException
	{
		String lsProposalId = "104";
		String lsProcurementID = "131";
		List<ExtendedDocument> loProposalDocumentList = moProposalService.getProposalDocuments(moMyBatisSession,
				lsProposalId, lsProcurementID);
		assertNotNull(loProposalDocumentList);

	}

	/**
	 * This method tests the negative execution of getProposalDocuments method
	 * and determines whether or not the Proposal Documents List is getting
	 * generated.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testGetProposalDocumentsCaseApplicationException() throws ApplicationException
	{
		String lsProposalId = "###";
		moProposalService.getProposalDocuments(moMyBatisSession, lsProposalId, null);

	}

	/**
	 * This method tests the execution of fetchProcurementTitle method and
	 * determines whether or not the Procurement Title is getting generated.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchProcurementTitleCase1() throws ApplicationException
	{
		String lsProcurementId = "624";
		String lsProcurementTitle = moProposalService.fetchProcurementTitle(moMyBatisSession, lsProcurementId);
		assertNull(lsProcurementTitle);

	}

	/**
	 * This method tests the execution of fetchProcurementTitle method and
	 * determines whether or not the Procurement Title is getting generated when
	 * procurement Id is null.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchProcurementTitleCase2() throws ApplicationException
	{
		String lsProcurementId = null;
		String lsProcurementTitle = moProposalService.fetchProcurementTitle(moMyBatisSession, lsProcurementId);
		assertNull(lsProcurementTitle);

	}

	/**
	 * This method tests the negative execution of fetchProcurementTitle method
	 * and determines whether or not the Procurement Title is getting generated.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchProcurementTitleCaseApplicationException() throws ApplicationException
	{
		String lsProcurementId = "###";
		moProposalService.fetchProcurementTitle(moMyBatisSession, lsProcurementId);

	}

	/**
	 * This method tests the execution of fetchProposalDocuments method and
	 * determines whether proposal documents exits for the given proposal Id
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchProposalDocuments() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskPropsMap = new HashMap<String, Object>();
		loTaskPropsMap.put(P8Constants.PE_WORKFLOW_PROPOSAL_ID, "180");
		loTaskDetailMap.put(lsWobNumber, loTaskPropsMap);
		List<ExtendedDocument> loExtendedDocList = moProposalService.fetchProposalDocuments(moMyBatisSession,
				loTaskDetailMap, lsWobNumber);
		assertNotNull(loExtendedDocList);

	}

	/**
	 * This method tests the execution of fetchProposalDocuments method and
	 * determines whether proposal documents exits for the given proposal Id
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchProposalDocuments1() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		List<ExtendedDocument> loExtendedDocList = moProposalService.fetchProposalDocuments(moMyBatisSession, null,
				lsWobNumber);
		assertNull(loExtendedDocList);

	}

	/**
	 * This method tests the execution of fetchProposalDocuments method and
	 * determines whether proposal documents exits for the given proposal Id
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchProposalDocuments2() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskPropsMap = new HashMap<String, Object>();
		loTaskPropsMap.put(P8Constants.PE_WORKFLOW_PROPOSAL_ID, "180");
		loTaskDetailMap.put("##", loTaskPropsMap);
		List<ExtendedDocument> loExtendedDocList = moProposalService.fetchProposalDocuments(moMyBatisSession,
				loTaskDetailMap, lsWobNumber);
		assertNull(loExtendedDocList);

	}

	/**
	 * This method tests the execution of fetchProposalDocuments method and
	 * determines whether proposal documents exits for the given proposal Id
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchProposalDocuments3() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskPropsMap = new HashMap<String, Object>();
		loTaskPropsMap.put(P8Constants.PE_WORKFLOW_PROPOSAL_ID, "###");
		loTaskDetailMap.put(lsWobNumber, loTaskPropsMap);
		moProposalService.fetchProposalDocuments(moMyBatisSession, loTaskDetailMap, lsWobNumber);

	}

	/**
	 * This method tests the execution of fetchProposalDocuments method and
	 * determines whether proposal documents exits for the given proposal Id
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchProposalDocuments4() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskPropsMap = new HashMap<String, Object>();
		loTaskPropsMap.put(P8Constants.PE_WORKFLOW_PROPOSAL_ID, "180");
		loTaskDetailMap.put(lsWobNumber, loTaskPropsMap);
		moProposalService.fetchProposalDocuments(null, loTaskDetailMap, lsWobNumber);
	}

	/**
	 * This method tests the execution of fetchProposalDocuments method and
	 * determines whether proposal documents exits for the given proposal Id
	 * @throws ApplicationException
	 */
	@Test(expected = Exception.class)
	public void testFetchProposalDocumentsNegative() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskPropsMap = new HashMap<String, Object>();
		loTaskPropsMap.put(P8Constants.PE_WORKFLOW_PROPOSAL_ID, "180");
		loTaskDetailMap.put(lsWobNumber, loTaskPropsMap);
		moProposalService.fetchProposalDocuments(moMyBatisSession, loTaskDetailMap, lsWobNumber);
	}

	/**
	 * This method tests the execution of fetchProposalDocuments method and
	 * determines whether proposal documents exits for the given proposal Id
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchProposalDocuments6() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskPropsMap = new HashMap<String, Object>();
		loTaskPropsMap.put(P8Constants.PE_WORKFLOW_PROPOSAL_ID, "180");
		loTaskDetailMap.put(lsWobNumber, loTaskPropsMap);
		List<ExtendedDocument> loExtendedDocList = moProposalService.fetchProposalDocuments(moMyBatisSession, null,
				lsWobNumber);
		assertNull(loExtendedDocList);

	}

	/**
	 * This method tests the execution of fetchProposalDocuments method and
	 * determines whether proposal documents exits for the given proposal Id
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchProposalDocuments7() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskPropsMap = new HashMap<String, Object>();
		loTaskPropsMap.put(P8Constants.PE_WORKFLOW_PROPOSAL_ID, "180");
		loTaskDetailMap.put(lsWobNumber, loTaskPropsMap);
		List<ExtendedDocument> loExtendedDocList = moProposalService.fetchProposalDocuments(moMyBatisSession,
				loTaskDetailMap, null);
		assertNull(loExtendedDocList);

	}

	/**
	 * This method tests the execution of fetchProposalTaskDetails method and
	 * determines whether proposal details exits for the given proposal Id
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchProposalDetailsForTask() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskPropsMap = new HashMap<String, Object>();
		loTaskPropsMap.put(P8Constants.PE_WORKFLOW_PROPOSAL_ID, "180");
		loTaskDetailMap.put(lsWobNumber, loTaskPropsMap);
		Map<String, String> loProposalMap = moProposalService.fetchProposalDetailsForTask(moMyBatisSession,
				loTaskDetailMap, lsWobNumber);
		assertNull(loProposalMap);
	}

	/**
	 * This method tests the execution of fetchProposalTaskDetails method and
	 * determines whether proposal details exits for the given proposal Id
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchProposalDetailsForTask1() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		Map<String, String> loProposalMap = moProposalService.fetchProposalDetailsForTask(moMyBatisSession, null,
				lsWobNumber);
		assertNull(loProposalMap);

	}

	/**
	 * This method tests the execution of fetchProposalTaskDetails method and
	 * determines whether proposal details exits for the given proposal Id
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchProposalDetailsForTask2() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskPropsMap = new HashMap<String, Object>();
		loTaskPropsMap.put(P8Constants.PE_WORKFLOW_PROPOSAL_ID, "180");
		loTaskDetailMap.put("##", loTaskPropsMap);
		Map<String, String> loProposalMap = moProposalService.fetchProposalDetailsForTask(moMyBatisSession,
				loTaskDetailMap, lsWobNumber);
		assertNull(loProposalMap);

	}

	/**
	 * This method tests the execution of fetchProposalTaskDetails method and
	 * determines whether proposal details exits for the given proposal Id
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchProposalDetailsForTask3() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskPropsMap = new HashMap<String, Object>();
		loTaskPropsMap.put(P8Constants.PE_WORKFLOW_PROPOSAL_ID, "###");
		loTaskDetailMap.put(lsWobNumber, loTaskPropsMap);
		moProposalService.fetchProposalDetailsForTask(moMyBatisSession, loTaskDetailMap, lsWobNumber);

	}

	/**
	 * This method tests the execution of fetchProposalTaskDetails method and
	 * determines whether proposal details exits for the given proposal Id
	 * @throws ApplicationException
	 */
	@Test(expected = Exception.class)
	public void testFetchProposalDetailsForTask7() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskPropsMap = new HashMap<String, Object>();
		loTaskPropsMap.put(P8Constants.PE_WORKFLOW_PROPOSAL_ID, "###");
		loTaskDetailMap.put(lsWobNumber, loTaskPropsMap);
		moProposalService.fetchProposalDetailsForTask(moMyBatisSession, loTaskDetailMap, lsWobNumber);

	}

	/**
	 * This method tests the execution of fetchProposalTaskDetails method and
	 * determines whether proposal details exits for the given proposal Id
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchProposalDetailsForTask4() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskPropsMap = new HashMap<String, Object>();
		loTaskPropsMap.put(P8Constants.PE_WORKFLOW_PROPOSAL_ID, "180");
		loTaskDetailMap.put(lsWobNumber, loTaskPropsMap);
		Map<String, String> loProposalMap = moProposalService.fetchProposalDetailsForTask(moMyBatisSession, null,
				lsWobNumber);
		assertNull(loProposalMap);

	}

	/**
	 * This method tests the execution of fetchProposalDetailsForTask method and
	 * determines whether proposal details exits for the given proposal Id
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchProposalDetailsForTask5() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskPropsMap = new HashMap<String, Object>();
		loTaskPropsMap.put(P8Constants.PE_WORKFLOW_PROPOSAL_ID, "180");
		loTaskDetailMap.put(lsWobNumber, loTaskPropsMap);
		Map<String, String> loProposalMap = moProposalService.fetchProposalDetailsForTask(moMyBatisSession,
				loTaskDetailMap, null);
		assertNull(loProposalMap);

	}

	/**
	 * This method tests the execution of fetchProposalDetailsForTask method and
	 * determines whether proposal details exits for the given proposal Id
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchProposalDetailsForTask6() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskPropsMap = new HashMap<String, Object>();
		loTaskPropsMap.put(P8Constants.PE_WORKFLOW_PROPOSAL_ID, "180");
		loTaskDetailMap.put(lsWobNumber, loTaskPropsMap);
		moProposalService.fetchProposalDetailsForTask(null, loTaskDetailMap, lsWobNumber);
	}

	/**
	 * This method tests the execution of fetchPermittedUsers method and
	 * determines whether users exits for the given agency Id
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchPermittedUsers() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskPropsMap = new HashMap<String, Object>();
		loTaskPropsMap.put(P8Constants.PE_WORKFLOW_AGENCY_ID, "DFTA");
		loTaskDetailMap.put(lsWobNumber, loTaskPropsMap);
		List<String> loUserRoleList = new ArrayList<String>();
		loUserRoleList.add(HHSConstants.ACCO_MANAGER_ROLE);
		List<UserBean> loUserList = moProposalService.fetchPermittedUsers(moMyBatisSession, loUserRoleList,
				loTaskDetailMap, lsWobNumber, null);
		assertNotNull(loUserList);
		assertNotNull(loUserList.size() > 0);

	}

	/**
	 * This method tests the execution of fetchPermittedUsers method and
	 * determines whether users exits for the given agency Id
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchPermittedUsersCase1() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		List<String> loUserRoleList = new ArrayList<String>();
		loUserRoleList.add(HHSConstants.ACCO_MANAGER_ROLE);
		List<UserBean> loUserList = moProposalService.fetchPermittedUsers(moMyBatisSession, loUserRoleList, null,
				lsWobNumber, null);
		assertNull(loUserList);

	}

	/**
	 * This method tests the execution of fetchPermittedUsers method and
	 * determines whether users exits for the given agency Id
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchPermittedUsersCase2() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskPropsMap = new HashMap<String, Object>();
		loTaskPropsMap.put(P8Constants.PE_WORKFLOW_AGENCY_ID, "DFTA");
		loTaskDetailMap.put(lsWobNumber, loTaskPropsMap);
		List<String> loUserRoleList = new ArrayList<String>();
		loUserRoleList.add(HHSConstants.ACCO_MANAGER_ROLE);
		moProposalService.fetchPermittedUsers(null, loUserRoleList, loTaskDetailMap, lsWobNumber, null);
	}

	/**
	 * This method tests the execution of fetchPermittedUsers method and
	 * determines whether users exits for the given agency Id
	 * @throws ApplicationException
	 */
	@Test(expected = Exception.class)
	public void testFetchPermittedUsersCase3() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskPropsMap = new HashMap<String, Object>();
		loTaskPropsMap.put(P8Constants.PE_WORKFLOW_AGENCY_ID, "DFTA");
		loTaskDetailMap.put(lsWobNumber, loTaskPropsMap);
		List<String> loUserRoleList = new ArrayList<String>();
		loUserRoleList.add(HHSConstants.ACCO_MANAGER_ROLE);
		moProposalService.fetchPermittedUsers(null, loUserRoleList, loTaskDetailMap, lsWobNumber, null);
	}

	/**
	 * This method tests the execution of fetchPermittedUsers method and
	 * determines whether users exits for the given agency Id
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchPermittedUsersCase4() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskPropsMap = new HashMap<String, Object>();
		loTaskPropsMap.put(P8Constants.PE_WORKFLOW_AGENCY_ID, "DFTA");
		loTaskDetailMap.put(lsWobNumber, loTaskPropsMap);
		List<String> loUserRoleList = new ArrayList<String>();
		loUserRoleList.add(HHSConstants.ACCO_MANAGER_ROLE);
		List<UserBean> loUserList = moProposalService.fetchPermittedUsers(moMyBatisSession, loUserRoleList, null,
				lsWobNumber, null);
		assertNull(loUserList);

	}

	/**
	 * This method tests the execution of fetchPermittedUsers method and
	 * determines whether users exits for the given agency Id
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchPermittedUsersCase5() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskPropsMap = new HashMap<String, Object>();
		loTaskPropsMap.put(P8Constants.PE_WORKFLOW_AGENCY_ID, "DFTA");
		loTaskDetailMap.put("", loTaskPropsMap);
		List<String> loUserRoleList = new ArrayList<String>();
		loUserRoleList.add(HHSConstants.ACCO_MANAGER_ROLE);
		List<UserBean> loUserList = moProposalService.fetchPermittedUsers(moMyBatisSession, loUserRoleList,
				loTaskDetailMap, lsWobNumber, null);
		assertNull(loUserList);

	}

	/**
	 * This method tests the execution of fetchPermittedUsers method and
	 * determines whether users exits for the given agency Id
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchPermittedUsersCase6() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskPropsMap = new HashMap<String, Object>();
		loTaskPropsMap.put(P8Constants.PE_WORKFLOW_AGENCY_ID, "DFTA");
		loTaskDetailMap.put("##", loTaskPropsMap);
		List<String> loUserRoleList = new ArrayList<String>();
		loUserRoleList.add(HHSConstants.ACCO_MANAGER_ROLE);
		List<UserBean> loUserList = moProposalService.fetchPermittedUsers(moMyBatisSession, loUserRoleList,
				loTaskDetailMap, lsWobNumber, null);
		assertNull(loUserList);

	}

	/**
	 * This method tests the execution of fetchPermittedUsers method and
	 * determines whether users exits for the given agency Id
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchPermittedUsersCase7() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskPropsMap = new HashMap<String, Object>();
		loTaskPropsMap.put(P8Constants.PE_WORKFLOW_AGENCY_ID, "DFTA");
		loTaskDetailMap.put(null, loTaskPropsMap);
		List<String> loUserRoleList = new ArrayList<String>();
		loUserRoleList.add(HHSConstants.ACCO_MANAGER_ROLE);
		List<UserBean> loUserList = moProposalService.fetchPermittedUsers(moMyBatisSession, loUserRoleList,
				loTaskDetailMap, lsWobNumber, null);
		assertNull(loUserList);

	}

	/**
	 * This method tests the execution of fetchPermittedUsers method and
	 * determines whether users exits for the given agency Id
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchPermittedUsersCase8() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskPropsMap = new HashMap<String, Object>();
		loTaskPropsMap.put("##", "DFTA");
		loTaskDetailMap.put(lsWobNumber, null);
		List<String> loUserRoleList = new ArrayList<String>();
		loUserRoleList.add(HHSConstants.ACCO_MANAGER_ROLE);
		List<UserBean> loUserList = moProposalService.fetchPermittedUsers(moMyBatisSession, loUserRoleList,
				loTaskDetailMap, lsWobNumber, null);
		assertNull(loUserList);

	}

	/**
	 * This method tests the execution of fetchRFPDocListForTask method and
	 * determines whether rfp documents for the given procurement Id
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchRFPDocListForTask() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskPropsMap = new HashMap<String, Object>();
		loTaskPropsMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, "623");
		loTaskDetailMap.put(lsWobNumber, loTaskPropsMap);
		ExtendedDocument loExDoc = new ExtendedDocument();
		loExDoc.setOrganizationType("city_org");
		List<ExtendedDocument> loDocList = moProposalService.fetchRFPDocListForTask(moMyBatisSession, loExDoc,
				loTaskDetailMap, lsWobNumber);
		assertNotNull(loDocList);

	}

	/**
	 * This method tests the execution of fetchRFPDocListForTask method and
	 * determines whether proposal documents exits for the given procurement Id
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchRFPDocListForTask1() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskPropsMap = new HashMap<String, Object>();
		loTaskPropsMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, "##");
		loTaskDetailMap.put(lsWobNumber, loTaskPropsMap);
		ExtendedDocument loExDoc = new ExtendedDocument();
		loExDoc.setOrganizationType("city_org");
		moProposalService.fetchRFPDocListForTask(moMyBatisSession, loExDoc, loTaskDetailMap, lsWobNumber);

	}

	/**
	 * This method tests the execution of fetchRFPDocListForTask method and
	 * determines whether proposal documents exits for the given procurement Id
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchRFPDocListForTask2() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskPropsMap = new HashMap<String, Object>();
		loTaskPropsMap.put("##", "623");
		loTaskDetailMap.put(lsWobNumber, loTaskPropsMap);
		ExtendedDocument loExDoc = new ExtendedDocument();
		loExDoc.setOrganizationType("city_org");
		moProposalService.fetchRFPDocListForTask(moMyBatisSession, loExDoc, loTaskDetailMap, lsWobNumber);

	}

	/**
	 * This method tests the execution of fetchRFPDocListForTask method and
	 * determines whether rfp documents for the given procurement Id
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchRFPDocListForTask4() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskPropsMap = new HashMap<String, Object>();
		loTaskPropsMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, "623");
		loTaskDetailMap.put(lsWobNumber, loTaskPropsMap);
		ExtendedDocument loExDoc = new ExtendedDocument();
		loExDoc.setOrganizationType("city_org");
		List<ExtendedDocument> loDocList = moProposalService.fetchRFPDocListForTask(moMyBatisSession, loExDoc,
				loTaskDetailMap, null);
		assertNull(loDocList);

	}

	/**
	 * This method tests the execution of fetchRFPDocListForTask method and
	 * determines whether rfp documents for the given procurement Id
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchRFPDocListForTask5() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskPropsMap = new HashMap<String, Object>();
		loTaskPropsMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, "623");
		loTaskDetailMap.put(lsWobNumber, loTaskPropsMap);
		ExtendedDocument loExDoc = new ExtendedDocument();
		loExDoc.setOrganizationType("city_org");
		moProposalService.fetchRFPDocListForTask(moMyBatisSession, null, loTaskDetailMap, lsWobNumber);

	}

	/**
	 * This method tests the execution of fetchRFPDocListForTask method and
	 * determines whether rfp documents for the given procurement Id
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchRFPDocListForTask6() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskPropsMap = new HashMap<String, Object>();
		loTaskPropsMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, "623");
		loTaskDetailMap.put(lsWobNumber, loTaskPropsMap);
		ExtendedDocument loExDoc = new ExtendedDocument();
		loExDoc.setOrganizationType("city_org");
		moProposalService.fetchRFPDocListForTask(null, loExDoc, loTaskDetailMap, lsWobNumber);
	}

	/**
	 * This method tests the execution of updateProposalDocumentAndDetailStatus
	 * method and determines whether proposal details and document status is
	 * updated successfully
	 * @throws ApplicationException
	 */
	@Test
	public void testUpdateProposalDocumentAndDetailStatus() throws ApplicationException
	{
		List<ExtendedDocument> loDocList = new ArrayList<ExtendedDocument>();
		ExtendedDocument loExtendedDocument = new ExtendedDocument();
		loExtendedDocument.setProposalTitle("Proposal Details");
		loExtendedDocument.setDocumentStatus(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.PROPOSAL_ACCEPTED_FOR_EVALUATION_KEY));
		loExtendedDocument.setProposalId("1");
		loDocList.add(loExtendedDocument);
		Boolean lbUpdateStatus = moProposalService.updateProposalDocumentAndDetailStatus(moMyBatisSession, loDocList);
		assertTrue(lbUpdateStatus);

	}

	/**
	 * This method tests the execution of updateProposalDocumentAndDetailStatus
	 * method and determines whether update proposal documents exits for the
	 * given proposal Id
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateProposalDocumentAndDetailStatusCase1() throws ApplicationException
	{
		List<ExtendedDocument> loDocList = new ArrayList<ExtendedDocument>();

		ExtendedDocument loExtendedDocument = new ExtendedDocument();
		loExtendedDocument.setProposalTitle("Proposal Details");
		loExtendedDocument.setDocumentStatus(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.PROPOSAL_ACCEPTED_FOR_EVALUATION_KEY));
		loExtendedDocument.setProposalId("##");
		loDocList.add(loExtendedDocument);

		ExtendedDocument loExtendedDocument1 = new ExtendedDocument();
		loExtendedDocument1.setDocumentStatus(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.DOCUMENT_VERIFIED_KEY));
		loExtendedDocument1.setProposalId("1");
		loExtendedDocument1.setDocumentId("{D3486709-3AA1-408B-B2EE-285CEDB5BE95}");
		loDocList.add(loExtendedDocument1);

		moProposalService.updateProposalDocumentAndDetailStatus(moMyBatisSession, loDocList);

	}

	/**
	 * This method tests the execution of updateProposalDocumentAndDetailStatus
	 * method and determines whether update proposal documents exits for the
	 * given Document Status
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateProposalDocumentAndDetailStatusCase2() throws ApplicationException
	{
		List<ExtendedDocument> loDocList = new ArrayList<ExtendedDocument>();

		ExtendedDocument loExtendedDocument = new ExtendedDocument();
		loExtendedDocument.setProposalTitle("Proposal Details");
		loExtendedDocument.setDocumentStatus(PropertyLoader.getProperty("##",
				HHSConstants.PROPOSAL_ACCEPTED_FOR_EVALUATION_KEY));
		loExtendedDocument.setProposalId("1");
		loDocList.add(loExtendedDocument);

		ExtendedDocument loExtendedDocument1 = new ExtendedDocument();
		loExtendedDocument1.setDocumentStatus(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.DOCUMENT_VERIFIED_KEY));
		loExtendedDocument1.setProposalId("1");
		loExtendedDocument1.setDocumentId("{D3486709-3AA1-408B-B2EE-285CEDB5BE95}");
		loDocList.add(loExtendedDocument1);

		moProposalService.updateProposalDocumentAndDetailStatus(moMyBatisSession, loDocList);

	}

	/**
	 * This method tests the execution of updateProposalDocumentAndDetailStatus
	 * method and determines whether proposal details and document status is
	 * updated successfully
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateProposalDocumentAndDetailStatusCase3() throws ApplicationException
	{
		List<ExtendedDocument> loDocList = new ArrayList<ExtendedDocument>();

		ExtendedDocument loExtendedDocument = new ExtendedDocument();
		loExtendedDocument.setProposalTitle("Proposal Details");
		loExtendedDocument.setDocumentStatus(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.PROPOSAL_ACCEPTED_FOR_EVALUATION_KEY));
		loExtendedDocument.setProposalId("1");
		loDocList.add(loExtendedDocument);

		ExtendedDocument loExtendedDocument1 = new ExtendedDocument();
		loExtendedDocument1.setDocumentStatus(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.DOCUMENT_VERIFIED_KEY));
		loExtendedDocument1.setProposalId("1");
		loExtendedDocument1.setDocumentId("{D3486709-3AA1-408B-B2EE-285CEDB5BE95}");
		loDocList.add(loExtendedDocument1);

		moProposalService.updateProposalDocumentAndDetailStatus(null, loDocList);
	}

	/**
	 * This method tests the execution of updateProposalDocumentAndDetailStatus
	 * method and determines whether proposal details and document status is
	 * updated successfully
	 * @throws ApplicationException
	 */
	@Test(expected = Exception.class)
	public void testUpdateProposalDocumentAndDetailStatusCase4() throws ApplicationException
	{
		List<ExtendedDocument> loDocList = new ArrayList<ExtendedDocument>();

		ExtendedDocument loExtendedDocument = new ExtendedDocument();
		loExtendedDocument.setProposalTitle("Proposal Details");
		loExtendedDocument.setDocumentStatus(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.PROPOSAL_ACCEPTED_FOR_EVALUATION_KEY));
		loExtendedDocument.setProposalId("1");
		loDocList.add(loExtendedDocument);

		ExtendedDocument loExtendedDocument1 = new ExtendedDocument();
		loExtendedDocument1.setDocumentStatus(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.DOCUMENT_VERIFIED_KEY));
		loExtendedDocument1.setProposalId("1");
		loExtendedDocument1.setDocumentId("{D3486709-3AA1-408B-B2EE-285CEDB5BE95}");
		loDocList.add(loExtendedDocument1);

		moProposalService.updateProposalDocumentAndDetailStatus(null, loDocList);
	}

	/**
	 * This method tests the execution of updateProposalDocumentAndDetailStatus
	 * method and determines whether proposal details and document status is
	 * updated successfully
	 * @throws ApplicationException
	 */
	@Test
	public void testUpdateProposalDocumentAndDetailStatusCase5() throws ApplicationException
	{
		Boolean lbUpdateStatus = moProposalService.updateProposalDocumentAndDetailStatus(moMyBatisSession, null);
		assertTrue(lbUpdateStatus);

	}

	/**
	 * This method tests the execution of updateProposalDocumentAndDetailStatus
	 * method and determines whether proposal details and document status is
	 * updated successfully
	 * @throws ApplicationException
	 */
	@Test
	public void testUpdateProposalDocumentAndDetailStatusCase6() throws ApplicationException
	{
		List<ExtendedDocument> loDocList = new ArrayList<ExtendedDocument>();
		Boolean lbUpdateStatus = moProposalService.updateProposalDocumentAndDetailStatus(moMyBatisSession, loDocList);
		assertTrue(lbUpdateStatus);

	}

	/**
	 * This method tests the execution of updateProposalDocumentAndDetailStatus
	 * method and determines whether proposal details and document status is
	 * updated successfully
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateProposalDocumentAndDetailStatusCase8() throws ApplicationException
	{
		List<ExtendedDocument> loDocList = new ArrayList<ExtendedDocument>();

		ExtendedDocument loExtendedDocument = new ExtendedDocument();
		loExtendedDocument.setProposalTitle("##");
		loExtendedDocument.setDocumentStatus(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.PROPOSAL_ACCEPTED_FOR_EVALUATION_KEY));
		loExtendedDocument.setProposalId("1");
		loDocList.add(loExtendedDocument);
		moProposalService.updateProposalDocumentAndDetailStatus(moMyBatisSession, loDocList);

	}

	/**
	 * This method tests the execution of updateProposalDocumentAndDetailStatus
	 * method and determines whether proposal details and document status is
	 * updated successfully
	 * @throws ApplicationException
	 */
	@Test(expected = Exception.class)
	public void testUpdateProposalDocumentAndDetailStatusCase9() throws ApplicationException
	{
		List<ExtendedDocument> loDocList = new ArrayList<ExtendedDocument>();

		ExtendedDocument loExtendedDocument = new ExtendedDocument();
		loExtendedDocument.setProposalTitle("##");
		loExtendedDocument.setDocumentStatus(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.PROPOSAL_ACCEPTED_FOR_EVALUATION_KEY));
		loExtendedDocument.setProposalId("1");
		loDocList.add(loExtendedDocument);
		moProposalService.updateProposalDocumentAndDetailStatus(moMyBatisSession, loDocList);

	}

	/**
	 * This method tests the execution of updateProposalDocumentStatusForTask
	 * method and determines whether proposal document status is updated
	 * successfully
	 * @throws ApplicationException
	 */
	@Test
	public void testUpdateProposalDocumentStatusForTask() throws ApplicationException
	{
		Boolean lbUpdateStatus = moProposalService.updateProposalDocumentStatusForTask(moMyBatisSession, true, "180");
		assertTrue(lbUpdateStatus);

	}

	/**
	 * This method tests the execution of updateProposalStatus method and
	 * determines whether update proposal status exits for the given Proposal Id
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateProposalDocumentStatusForTask1() throws ApplicationException
	{
		moProposalService.updateProposalDocumentStatusForTask(moMyBatisSession, true, "##");

	}

	/**
	 * This method tests the execution of updateProposalStatus method and
	 * determines whether update proposal status exits for the given Proposal
	 * Status
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateProposalDocumentStatusForTask2() throws ApplicationException
	{
		moProposalService.updateProposalDocumentStatusForTask(moMyBatisSession, null, "180");

	}

	/**
	 * This method tests the execution of updateProposalDocumentStatusForTask
	 * method and determines whether proposal document status is updated
	 * successfully
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateProposalDocumentStatusForTask3() throws ApplicationException
	{
		moProposalService.updateProposalDocumentStatusForTask(moMyBatisSession, true, null);

	}

	/**
	 * This method tests the execution of updateProposalDocumentStatusForTask
	 * method and determines whether proposal document status is updated
	 * successfully
	 * @throws ApplicationException
	 */
	@Test
	public void testUpdateProposalDocumentStatusForTask4() throws ApplicationException
	{
		Boolean lbUpdateStatus = moProposalService.updateProposalDocumentStatusForTask(moMyBatisSession, false, "180");
		assertFalse(lbUpdateStatus);

	}

	/**
	 * This method tests the execution of updateProposalDocumentStatusForTask
	 * method and determines whether proposal document status is updated
	 * successfully
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateProposalDocumentStatusForTask5() throws ApplicationException
	{
		moProposalService.updateProposalDocumentStatusForTask(null, true, "180");
	}

	/**
	 * This method tests the execution of fetchProcTitleAndOrgId method and
	 * determines whether organization Id and proposal title is fetched
	 * successfully
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchProcTitleAndOrgId() throws ApplicationException
	{
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		List<String> loAlertList = new ArrayList<String>();
		NotificationDataBean obj = new NotificationDataBean();
		loAlertList.add("AL201");
		loAlertList.add("AL202");
		loAlertList.add("NT201");
		loAlertList.add("NT202");
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		StringBuffer lsBfApplicationUrl = new StringBuffer(256);
		lsBfApplicationUrl
				.append("http://localhost:7001/HHSPortal/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_procurement&_nfls=false&app_menu_name=header_procurement&resetSessionProurement=true");
		loRequestMap.put("LINK", lsBfApplicationUrl.toString());
		loNotificationMap.put("AL201", obj);
		loNotificationMap.put("AL202", obj);
		loNotificationMap.put("NT201", obj);
		loNotificationMap.put("NT202", obj);
		loNotificationMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loAlertList);
		loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		loNotificationMap = moProposalService.fetchProcTitleAndOrgId(moMyBatisSession, true, "101", loNotificationMap);
		assertNotNull(loNotificationMap);
	}

	/**
	 * This method tests the execution of fetchProcTitleAndOrgId method and
	 * determines whether organization Id and proposal title is fetched
	 * successfully
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchProcTitleAndOrgIdCase1() throws ApplicationException
	{
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		loNotificationMap = moProposalService.fetchProcTitleAndOrgId(moMyBatisSession, false, "180", loNotificationMap);
		assertNull(loNotificationMap.get(TransactionConstants.PROVIDER_ID));

	}

	/**
	 * This method tests the execution of fetchProcTitleAndOrgId method and
	 * determines whether organization Id and proposal title is fetched
	 * successfully
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchProcTitleAndOrgIdCase2() throws ApplicationException
	{
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		loNotificationMap = moProposalService.fetchProcTitleAndOrgId(moMyBatisSession, true, "##", loNotificationMap);

	}

	/**
	 * This method tests the execution of fetchProcTitleAndOrgId method and
	 * determines whether organization Id and proposal title is fetched
	 * successfully
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchProcTitleAndOrgIdCase3() throws ApplicationException
	{
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		loNotificationMap = moProposalService.fetchProcTitleAndOrgId(null, true, "180", loNotificationMap);
	}

	/**
	 * This method tests the execution of fetchProcTitleAndOrgId method and
	 * determines whether organization Id and proposal title is fetched
	 * successfully
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchProcTitleAndOrgIdCase4() throws ApplicationException
	{
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		loNotificationMap = moProposalService.fetchProcTitleAndOrgId(moMyBatisSession, true, null, loNotificationMap);

	}

	/**
	 * This method tests the execution of fetchProcTitleAndOrgId method and
	 * determines whether organization Id and proposal title is fetched
	 * successfully
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchProcTitleAndOrgIdCase5() throws ApplicationException
	{
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		loNotificationMap = moProposalService.fetchProcTitleAndOrgId(moMyBatisSession, null, "180", loNotificationMap);

	}

	/**
	 * This method tests the execution of fetchProcTitleAndOrgId method and
	 * determines whether organization Id and proposal title is fetched
	 * successfully
	 * @throws ApplicationException
	 */
	@Test
	public void testCheckForDueDate() throws ApplicationException
	{
		Date loDate = moProposalService.checkForDueDate(moMyBatisSession, "601");
		assertNull(loDate);

	}

	@Test(expected = ApplicationException.class)
	public void testCheckForDueDateException() throws ApplicationException
	{
		Date loDate = moProposalService.checkForDueDate(null, "601");
		assertNotNull(loDate);

	}

	@Test
	public void testCheckProposalCancel() throws ApplicationException
	{
		Boolean loFetchStatus = moProposalService.checkProposalCancel(moMyBatisSession, "152");
		assertTrue(loFetchStatus);

	}

	@Test(expected = ApplicationException.class)
	public void testCheckProposalCancelException() throws ApplicationException
	{
		moProposalService.checkProposalCancel(null, "180");

	}

	@Test(expected = ApplicationException.class)
	public void testCheckProposalCancelCase1() throws ApplicationException
	{
		moProposalService.checkProposalCancel(moMyBatisSession, null);

	}

	@Test(expected = ApplicationException.class)
	public void testCheckProposalCancelCase2() throws ApplicationException
	{
		Boolean loFetchStatus = moProposalService.checkProposalCancel(moMyBatisSession, "##");
		assertFalse(loFetchStatus);

	}

	/**
	 * This method tests the execution of fetchRequiredOptionalDocuments method
	 * and determines whether or not the document list is getting generated when
	 * the award Id is null
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchRequiredOptionalDocumentsCase2() throws ApplicationException
	{
		String lsAwardId = "7";
		Map<String, String> loParamMap = new HashMap<String, String>();
		loParamMap.put("procurementId", "3");
		loParamMap.put("userOrgId", "accenture");
		loParamMap.put("userRole", "");
		String lsProcurementStatusId = "";
		List<ExtendedDocument> loDocumentList = moProposalService.fetchRequiredOptionalDocuments(moMyBatisSession,
				loParamMap, lsAwardId, lsProcurementStatusId);
		assertNotNull(loDocumentList);

	}

	/**
	 * This method tests the execution of negative
	 * fetchRequiredOptionalDocuments method and determines whether or not the
	 * document list is getting generated
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchRequiredOptionalDocumentsApplicationException() throws ApplicationException
	{
		String lsAwardId = "###";
		Map<String, String> loParamMap = new HashMap<String, String>();
		loParamMap.put("procurementId", "3");
		loParamMap.put("userOrgId", "accenture");
		moProposalService.fetchRequiredOptionalDocuments(moMyBatisSession, loParamMap, lsAwardId, null);

	}

	/*
	 * This method tests checkProposalEdit
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testCheckProposalEdit() throws ApplicationException
	{
		String lsUploadingDocType = "Proposal";
		String lsProposalId = "152";
		Boolean loFetchStatus = moProposalService.checkProposalEdit(moMyBatisSession, lsUploadingDocType, lsProposalId);
		assertTrue(loFetchStatus);

	}

	@Test
	public void testCheckProposalEdit2() throws ApplicationException
	{
		String lsUploadingDocType = "Proposal";
		String lsProposalId = "140";
		Boolean loFetchStatus = moProposalService.checkProposalEdit(moMyBatisSession, lsUploadingDocType, lsProposalId);
		assertTrue(loFetchStatus);

	}

	/*
	 * This method tests checkProposalEdit
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testCheckProposalEditException() throws ApplicationException
	{
		moProposalService.checkProposalEdit(null, "Proposal", "180");

	}

	/*
	 * This method tests checkProposalEdit
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testCheckProposalEditCase1() throws ApplicationException
	{
		moProposalService.checkProposalEdit(moMyBatisSession, "Proposal", "##");

	}

	@Test
	public void testFetchProposalTitle() throws ApplicationException
	{
		String loProposalTitle = moProposalService.fetchProposalTitle(moMyBatisSession, "201");
		assertNull(loProposalTitle);

	}

	@Test(expected = ApplicationException.class)
	public void testFetchProposalTitleException() throws ApplicationException
	{
		moProposalService.fetchProposalTitle(null, "180");

	}

	/*
	 * This method tests the execution of updateApprovedProviderStatus
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testUpdateApprovedProviderStatus() throws ApplicationException
	{
		Boolean loFetchStatus = moProposalService.updateApprovedProviderStatus(moMyBatisSession, "101", "114", "623",
				true);
		assertTrue(loFetchStatus);

	}

	/*
	 * This method tests the execution of updateApprovedProviderStatus
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateApprovedProviderStatusException() throws ApplicationException
	{
		moProposalService.updateApprovedProviderStatus(null, "180", "601", "623", true);

	}

	@Test
	public void testInsertNewProposalDetails() throws ApplicationException
	{
		String lsProcurementStatus = "3";
		String lsProviderStatus = "11";// 9 12
		Map<String, String> loProposalDetailMap = new HashMap<String, String>();
		loProposalDetailMap.put(HHSConstants.PROCUREMENT_ID, "114");
		loProposalDetailMap.put("providerId", "accenture");
		loProposalDetailMap.put("staffId", "");
		loProposalDetailMap.put("proposalTitle", HHSConstants.UNTITLED_PROPOSAL);
		loProposalDetailMap.put("proposalStatusId", HHSConstants.DEFAULT_PROPOSAL_STATUS);
		loProposalDetailMap.put("createdBy", "623");
		loProposalDetailMap.put("activeFlag", HHSConstants.ONE);
		String lsProposalId = moProposalService.insertNewProposalDetails(moMyBatisSession, loProposalDetailMap,
				lsProcurementStatus, lsProviderStatus);
		assertNotNull(lsProposalId);

	}

	@Test
	public void testInsertNewProposalDetails2() throws ApplicationException
	{
		String lsProcurementStatus = "3";
		String lsProviderStatus = "9";
		Map<String, String> loProposalDetailMap = new HashMap<String, String>();
		loProposalDetailMap.put(HHSConstants.PROCUREMENT_ID, "114");
		loProposalDetailMap.put("providerId", "accenture");
		loProposalDetailMap.put("staffId", "");
		loProposalDetailMap.put("proposalTitle", HHSConstants.UNTITLED_PROPOSAL);
		loProposalDetailMap.put("proposalStatusId", HHSConstants.DEFAULT_PROPOSAL_STATUS);
		loProposalDetailMap.put("createdBy", "623");
		loProposalDetailMap.put("activeFlag", HHSConstants.ONE);
		String lsProposalId = moProposalService.insertNewProposalDetails(moMyBatisSession, loProposalDetailMap,
				lsProcurementStatus, lsProviderStatus);
		assertNotNull(lsProposalId);

	}

	@Test
	public void testInsertNewProposalDetails3() throws ApplicationException
	{
		String lsProcurementStatus = "3";
		String lsProviderStatus = "12";
		Map<String, String> loProposalDetailMap = new HashMap<String, String>();
		loProposalDetailMap.put(HHSConstants.PROCUREMENT_ID, "114");
		loProposalDetailMap.put("providerId", "accenture");
		loProposalDetailMap.put("staffId", "");
		loProposalDetailMap.put("proposalTitle", HHSConstants.UNTITLED_PROPOSAL);
		loProposalDetailMap.put("proposalStatusId", HHSConstants.DEFAULT_PROPOSAL_STATUS);
		loProposalDetailMap.put("createdBy", "623");
		loProposalDetailMap.put("activeFlag", HHSConstants.ONE);
		String lsProposalId = moProposalService.insertNewProposalDetails(moMyBatisSession, loProposalDetailMap,
				lsProcurementStatus, lsProviderStatus);
		assertNotNull(lsProposalId);

	}

	@Test(expected = ApplicationException.class)
	public void testInsertNewProposalDetailsException() throws ApplicationException
	{
		Map<String, String> aoProposalDetailMap = new HashMap<String, String>();
		moProposalService.insertNewProposalDetails(null, aoProposalDetailMap, "3", null);

	}

	@Test
	public void testInsertNewProposalDetailsCase1() throws ApplicationException
	{
		Map<String, String> loProposalDetailMap = new HashMap<String, String>();
		loProposalDetailMap.put(HHSConstants.PROCUREMENT_ID, "624");
		loProposalDetailMap.put("providerId", "accenture");
		loProposalDetailMap.put("proposalTitle", HHSConstants.UNTITLED_PROPOSAL);
		loProposalDetailMap.put("proposalStatusId", HHSConstants.DEFAULT_PROPOSAL_STATUS);
		loProposalDetailMap.put("createdBy", "623");
		loProposalDetailMap.put("activeFlag", HHSConstants.ONE);
		String lsProposalId = moProposalService.insertNewProposalDetails(moMyBatisSession, loProposalDetailMap, null,
				null);
		assertNull(lsProposalId);

	}

	@Test
	public void testInsertNewProposalDetailsCase2() throws ApplicationException
	{
		Map<String, String> loProposalDetailMap = new HashMap<String, String>();
		loProposalDetailMap.put(HHSConstants.PROCUREMENT_ID, "624");
		loProposalDetailMap.put("providerId", "accenture");
		loProposalDetailMap.put("proposalTitle", HHSConstants.UNTITLED_PROPOSAL);
		loProposalDetailMap.put("proposalStatusId", HHSConstants.DEFAULT_PROPOSAL_STATUS);
		loProposalDetailMap.put("createdBy", "623");
		loProposalDetailMap.put("activeFlag", HHSConstants.ONE);
		String lsProposalId = moProposalService.insertNewProposalDetails(moMyBatisSession, loProposalDetailMap, "##",
				null);
		assertNull(lsProposalId);

	}

	@Test
	public void testGetDocumentIdList() throws ApplicationException
	{
		List<String> loDocumentIdList = moProposalService.getDocumentIdList(moMyBatisSession, "183");
		assertNotNull(loDocumentIdList);

	}

	@Test(expected = ApplicationException.class)
	public void testGetDocumentIdListException() throws ApplicationException
	{
		moProposalService.getDocumentIdList(null, "183");

	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testGetOrgIdsForSelectedProposals() throws ApplicationException
	{
		HashMap<String, String> loProposalDetailMap = new HashMap<String, String>();
		Boolean lbIsFirstLaunch = true;
		loProposalDetailMap.put("proposalStatusId", "23");
		loProposalDetailMap.put("modifiedFlag", "1");
		loProposalDetailMap.put("procurementId", "131");
		HashMap loTaskPropsMap = new HashMap();
		loTaskPropsMap = moProposalService.getOrgIdsForSelectedProposals(moMyBatisSession, loProposalDetailMap,
				loTaskPropsMap, lbIsFirstLaunch);
		assertNotNull(loTaskPropsMap);

	}

	@SuppressWarnings("rawtypes")
	@Test(expected = ApplicationException.class)
	public void testGetOrgIdsForSelectedProposalsCase2() throws ApplicationException
	{
		HashMap<String, String> loProposalDetailMap = new HashMap<String, String>();
		Boolean lbIsFirstLaunch = true;
		loProposalDetailMap.put("proposalStatusId", "23");
		loProposalDetailMap.put("modifiedFlag", "1");
		loProposalDetailMap.put("procurementId", "1");
		HashMap loTaskPropsMap = new HashMap();
		moProposalService.getOrgIdsForSelectedProposals(null, loProposalDetailMap, loTaskPropsMap, lbIsFirstLaunch);

	}

	@Test
	public void testUpdateProposalStatusFromTask() throws ApplicationException
	{
		String lsProcurementId = "2";
		String lsTaskStatus = "";
		Boolean loIsSecondFlag = true;
		String asEvaluationPoolMappingId = "230";
		Boolean lbUpdateStatus = moProposalService.updateProposalStatusFromTask(moMyBatisSession, lsProcurementId,
				loIsSecondFlag, false, lsTaskStatus, asEvaluationPoolMappingId);
		assertTrue(lbUpdateStatus);

	}

	@Test
	public void testUpdateProposalStatusFromTask4() throws ApplicationException
	{
		String lsProcurementId = "2";
		String lsTaskStatus = null;
		Boolean loIsSecondFlag = true;
		String asEvaluationPoolMappingId = "230";
		Boolean lbUpdateStatus = moProposalService.updateProposalStatusFromTask(moMyBatisSession, lsProcurementId,
				loIsSecondFlag, false, lsTaskStatus, asEvaluationPoolMappingId);
		assertFalse(lbUpdateStatus);

	}

	@Test
	public void testUpdateProposalStatusFromTaskCase1() throws ApplicationException
	{
		String lsProcurementId = "2";
		String lsTaskStatus = "";
		Boolean loIsProposalNotSelected = true;
		String asEvaluationPoolMappingId = "";
		Boolean lbUpdateStatus = moProposalService.updateProposalStatusFromTask(moMyBatisSession, lsProcurementId,
				false, loIsProposalNotSelected, lsTaskStatus, asEvaluationPoolMappingId);
		assertTrue(lbUpdateStatus);

	}

	@Test
	public void testUpdateProposalStatusFromTaskCase3() throws ApplicationException
	{
		String lsProcurementId = "2";
		String lsTaskStatus = "";
		Boolean loIsProposalNotSelected = true;
		String asEvaluationPoolMappingId = null;
		Boolean lbUpdateStatus = moProposalService.updateProposalStatusFromTask(moMyBatisSession, lsProcurementId,
				false, loIsProposalNotSelected, lsTaskStatus, asEvaluationPoolMappingId);
		assertTrue(lbUpdateStatus);

	}

	@Test
	public void testUpdateProposalPreviousStatus() throws ApplicationException
	{
		String lsProposalId = "7";
		String lsProposalStatus = "20";
		Boolean lbUpdateStatus = moProposalService.updateProposalPreviousStatus(moMyBatisSession, lsProposalId,
				lsProposalStatus);
		assertTrue(lbUpdateStatus);

	}

	@Test(expected = ApplicationException.class)
	public void testUpdateProposalPreviousStatusCase1() throws ApplicationException
	{
		String lsProposalId = "##";
		String lsProposalStatus = "20";
		moProposalService.updateProposalPreviousStatus(moMyBatisSession, lsProposalId, lsProposalStatus);

	}

	@Test(expected = ApplicationException.class)
	public void testUpdateProposalPreviousStatusCase3() throws ApplicationException
	{
		String lsProposalId = "7";
		String lsProposalStatus = "20";
		moProposalService.updateProposalPreviousStatus(null, lsProposalId, lsProposalStatus);

	}

	@Test
	public void testAddProposalAndProcurementStatus() throws ApplicationException
	{
		Map<String, String> loProposalMap = new HashMap<String, String>();
		loProposalMap.put(HHSConstants.PROPOSAL_STATUS_ID, "2");
		String lsProcurementStatus = "Test";
		ExtendedDocument loProposalExtendedDocument = new ExtendedDocument();
		List<ExtendedDocument> loProposalDocBeanList = new ArrayList<ExtendedDocument>();
		loProposalDocBeanList.add(loProposalExtendedDocument);
		loProposalDocBeanList = moProposalService.addProposalAndProcurementStatus(loProposalMap, lsProcurementStatus,
				loProposalDocBeanList);
		assertNotNull(loProposalDocBeanList);

	}

	@Test
	public void testAddProposalAndProcurementStatusCase1() throws ApplicationException
	{
		Map<String, String> loProposalMap = new HashMap<String, String>();
		String lsProcurementStatus = "Test";
		ExtendedDocument loProposalExtendedDocument = new ExtendedDocument();
		List<ExtendedDocument> loProposalDocBeanList = new ArrayList<ExtendedDocument>();
		loProposalDocBeanList.add(loProposalExtendedDocument);
		loProposalDocBeanList = moProposalService.addProposalAndProcurementStatus(loProposalMap, lsProcurementStatus,
				loProposalDocBeanList);
		assertNotNull(loProposalDocBeanList);

	}

	@Test
	public void testAddProposalAndProcurementStatusCase2() throws ApplicationException
	{
		Map<String, String> loProposalMap = new HashMap<String, String>();
		loProposalMap.put(HHSConstants.PROPOSAL_STATUS_ID, "2");
		String lsProcurementStatus = "Test";
		List<ExtendedDocument> loProposalDocBeanList = new ArrayList<ExtendedDocument>();
		loProposalDocBeanList = moProposalService.addProposalAndProcurementStatus(loProposalMap, lsProcurementStatus,
				loProposalDocBeanList);
		assertNotNull(loProposalDocBeanList);

	}

	@Test
	public void testAddProposalAndProcurementStatusCase3() throws ApplicationException
	{
		Map<String, String> loProposalMap = new HashMap<String, String>();
		loProposalMap.put(HHSConstants.PROPOSAL_STATUS_ID, "##");
		String lsProcurementStatus = "Test";
		ExtendedDocument loProposalExtendedDocument = new ExtendedDocument();
		List<ExtendedDocument> loProposalDocBeanList = new ArrayList<ExtendedDocument>();
		loProposalDocBeanList.add(loProposalExtendedDocument);
		loProposalDocBeanList = moProposalService.addProposalAndProcurementStatus(loProposalMap, lsProcurementStatus,
				loProposalDocBeanList);
		assertNotNull(loProposalDocBeanList);

	}

	@Test
	public void testAddProposalAndProcurementStatusCase4() throws ApplicationException
	{
		Map<String, String> loProposalMap = new HashMap<String, String>();
		loProposalMap.put(HHSConstants.PROPOSAL_STATUS_ID, "2");
		String lsProcurementStatus = null;
		ExtendedDocument loProposalExtendedDocument = new ExtendedDocument();
		List<ExtendedDocument> loProposalDocBeanList = new ArrayList<ExtendedDocument>();
		loProposalDocBeanList.add(loProposalExtendedDocument);
		loProposalDocBeanList = moProposalService.addProposalAndProcurementStatus(loProposalMap, lsProcurementStatus,
				loProposalDocBeanList);
		assertNotNull(loProposalDocBeanList);

	}

	@Test
	public void testUpdateProposalDocumentProperties() throws ApplicationException
	{
		String lsDocumentId = "{B6A2DEB6-0F85-47A3-9390-F6AE8B351EE9}";
		String lsProposalId = "101";
		Boolean lbDocStatusUpdated = moProposalService.updateProposalDocumentProperties(moMyBatisSession, lsDocumentId,
				lsProposalId);
		assertFalse(lbDocStatusUpdated);
	}

	@Test(expected = ApplicationException.class)
	public void testUpdateProposalDocumentPropertiesCase2() throws ApplicationException
	{
		String lsDocumentId = "";
		Boolean lbDocStatusUpdated = moProposalService.updateProposalDocumentProperties(moMyBatisSession, lsDocumentId,
				null);
		assertTrue(lbDocStatusUpdated);
	}

	@Test
	public void testShowProposalDetailsReadonly() throws ApplicationException
	{
		ProposalDetailsBean loProposalDetailBean = new ProposalDetailsBean();
		loProposalDetailBean.setProposalStatus("18");
		Boolean lbDocStatusUpdated = moProposalService.showProposalDetailsReadonly(loProposalDetailBean);
		assertTrue(lbDocStatusUpdated);
	}

	@Test
	public void testShowProposalDetailsReadonly2() throws ApplicationException
	{
		ProposalDetailsBean loProposalDetailBean = new ProposalDetailsBean();
		loProposalDetailBean.setProcReviewStatusId("20");
		Boolean lbDocStatusUpdated = moProposalService.showProposalDetailsReadonly(loProposalDetailBean);
		assertTrue(lbDocStatusUpdated);
	}

	@Test
	public void testShowProposalDetailsReadonlyCase1() throws ApplicationException
	{
		ProposalDetailsBean loProposalDetailBean = new ProposalDetailsBean();
		loProposalDetailBean.setProposalStatus("");
		Boolean lbDocStatusUpdated = moProposalService.showProposalDetailsReadonly(loProposalDetailBean);
		assertFalse(lbDocStatusUpdated);
	}

	@Test
	public void testShowProposalDetailsReadonlyCase2() throws ApplicationException
	{
		ProposalDetailsBean loProposalDetailBean = new ProposalDetailsBean();
		loProposalDetailBean.setProposalStatus("##");
		Boolean lbDocStatusUpdated = moProposalService.showProposalDetailsReadonly(loProposalDetailBean);
		assertFalse(lbDocStatusUpdated);
	}

	@Test
	public void testShowProposalDetailsReadonlyCase3() throws ApplicationException
	{
		ProposalDetailsBean loProposalDetailBean = new ProposalDetailsBean();
		loProposalDetailBean.setProposalStatus(null);
		Boolean lbDocStatusUpdated = moProposalService.showProposalDetailsReadonly(loProposalDetailBean);
		assertFalse(lbDocStatusUpdated);
	}

	@Test
	public void testShowProposalDetailsReadonlyCase4() throws ApplicationException
	{
		ProposalDetailsBean loProposalDetailBean = new ProposalDetailsBean();
		Boolean lbDocStatusUpdated = moProposalService.showProposalDetailsReadonly(loProposalDetailBean);
		assertFalse(lbDocStatusUpdated);
	}

	@Test
	public void testShowProposalDetailsReadonlyCase5() throws ApplicationException
	{
		ProposalDetailsBean loProposalDetailBean = new ProposalDetailsBean();
		Boolean lbDocStatusUpdated = moProposalService.showProposalDetailsReadonly(loProposalDetailBean);
		assertFalse(lbDocStatusUpdated);
	}

	@Test
	public void testGetProposalSiteDetails() throws ApplicationException
	{
		String lsProposalId = "3";
		String lsUserType = "";
		String lsSortSiteTable = "";
		List<SiteDetailsBean> loSiteDetailList = moProposalService.getProposalSiteDetails(moMyBatisSession,
				lsProposalId, lsUserType, lsSortSiteTable);
		assertNotNull(loSiteDetailList);
	}

	@Test(expected = ApplicationException.class)
	public void testGetProposalSiteDetailsCase3() throws ApplicationException
	{
		String lsProposalId = "##";
		String lsUserType = "";
		String lsSortSiteTable = "";
		moProposalService.getProposalSiteDetails(moMyBatisSession, lsProposalId, lsUserType, lsSortSiteTable);
	}

	@Test
	public void testGetNotificationMapForSubmitProposal1() throws ApplicationException
	{
		Map<String, String> aoStatusMap = new HashMap<String, String>();
		HashMap<String, Object> aoNotificationMap = new HashMap<String, Object>();
		Boolean aoProposalUpdateFlag = true;
		aoStatusMap.put("PROPOSAL_STATUS_ID", "17");
		aoStatusMap.put("PROC_REVIEW_STATUS_ID", "5");
		aoStatusMap.put("PROCUREMENT_TITLE", "Nitin");
		aoStatusMap.put("PROPOSAL_TITLE", "Untitled Prop");
		aoStatusMap.put("AGENCY_NAME", "ACS");
		aoStatusMap.put("MODIFIED_DATE", "21-MAY-13 11.48.32.000000000 AM");
		aoStatusMap.put("ORGANIZATION_LEGAL_NAME", "PROVIDER");
		aoStatusMap.put("ORGANIZATION_ID", "hhs");
		aoStatusMap.put("AGENCY_ID", "ACS");
		aoStatusMap.put("ORGANIZATION_LEGAL_NAME", "HHS");
		Map<String, String> aoRequestMap = new HashMap<String, String>();
		aoNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, aoRequestMap);
		aoNotificationMap = moProposalService.getNotificationMapForSubmitProposal(aoStatusMap, aoProposalUpdateFlag,
				aoNotificationMap);
		assertNotNull(aoNotificationMap);
	}

	@Test
	public void testGetNotificationMapForSubmitProposal2() throws ApplicationException
	{
		Map<String, String> aoStatusMap = new HashMap<String, String>();
		HashMap<String, Object> aoNotificationMap = new HashMap<String, Object>();
		Boolean aoProposalUpdateFlag = true;
		aoStatusMap.put("PROPOSAL_STATUS_ID", "17");
		aoStatusMap.put("PROC_REVIEW_STATUS_ID", null);
		aoStatusMap.put("PROC_STATUS_ID", "3");
		aoStatusMap.put("PROCUREMENT_TITLE", "Nitin");
		aoStatusMap.put("PROPOSAL_TITLE", "Untitled Prop");
		aoStatusMap.put("AGENCY_NAME", "ACS");
		aoStatusMap.put("MODIFIED_DATE", "21-MAY-13 11.48.32.000000000 AM");
		aoStatusMap.put("ORGANIZATION_LEGAL_NAME", "PROVIDER");
		aoStatusMap.put("ORGANIZATION_ID", "hhs");
		aoStatusMap.put("AGENCY_ID", "ACS");
		aoStatusMap.put("ORGANIZATION_LEGAL_NAME", "HHS");
		Map<String, String> aoRequestMap = new HashMap<String, String>();
		aoNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, aoRequestMap);
		aoNotificationMap = moProposalService.getNotificationMapForSubmitProposal(aoStatusMap, aoProposalUpdateFlag,
				aoNotificationMap);
		assertNotNull(aoNotificationMap);
	}

	@Test
	public void testGetNotificationMapForSubmitProposal3() throws ApplicationException
	{
		Map<String, String> aoStatusMap = new HashMap<String, String>();
		HashMap<String, Object> aoNotificationMap = new HashMap<String, Object>();
		Boolean aoProposalUpdateFlag = true;
		aoStatusMap.put("PROPOSAL_STATUS_ID", "19");
		aoStatusMap.put("PROC_REVIEW_STATUS_ID", "5");
		aoStatusMap.put("PROCUREMENT_TITLE", "Nitin");
		aoStatusMap.put("PROPOSAL_TITLE", "Untitled Prop");
		aoStatusMap.put("AGENCY_NAME", "ACS");
		aoStatusMap.put("MODIFIED_DATE", "21-MAY-13 11.48.32.000000000 AM");
		aoStatusMap.put("ORGANIZATION_LEGAL_NAME", "PROVIDER");
		aoStatusMap.put("ORGANIZATION_ID", "hhs");
		aoStatusMap.put("AGENCY_ID", "ACS");
		aoStatusMap.put("ORGANIZATION_LEGAL_NAME", "HHS");
		Map<String, String> aoRequestMap = new HashMap<String, String>();
		aoNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, aoRequestMap);
		aoNotificationMap = moProposalService.getNotificationMapForSubmitProposal(aoStatusMap, aoProposalUpdateFlag,
				aoNotificationMap);
		assertNotNull(aoNotificationMap);
	}

	@Test
	public void testFetchMemberDetails1() throws ApplicationException
	{
		String lsUserId = "111";
		String lsOrganizationId = "provider1";
		Map<String, String> loMemberDetails = moProposalService.fetchMemberDetails(moMyBatisSession, lsUserId,
				lsOrganizationId);
		assertNotNull(loMemberDetails);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchMemberDetails2() throws ApplicationException
	{
		String lsOrganizationId = "provider1";
		moProposalService.fetchMemberDetails(moMyBatisSession, null, lsOrganizationId);
	}

	@Test
	public void testGetEvaluationSentFlag1() throws Exception
	{
		String lsProposalId = "137";
		Boolean loValidateFlag = true;
		Boolean flag = moProposalService.getEvaluationSentFlag(moMyBatisSession, lsProposalId, loValidateFlag);
		assertTrue(flag);
	}

	@Test(expected = ApplicationException.class)
	public void testGetEvaluationSentFlag2() throws ApplicationException
	{
		Boolean loValidateFlag = true;
		moProposalService.getEvaluationSentFlag(moMyBatisSession, null, loValidateFlag);
	}

	@Test
	public void testGetProposalStatus1() throws Exception
	{
		Map<String, String> loStatusMap = new HashMap<String, String>();
		loStatusMap.put("proposalId", "137");
		loStatusMap = moProposalService.getProposalStatus(moMyBatisSession, loStatusMap);
		assertNotNull(loStatusMap);
	}

	@Test(expected = ApplicationException.class)
	public void testGetProposalStatus2() throws ApplicationException
	{
		Map<String, String> loStatusMap = new HashMap<String, String>();
		loStatusMap.put("proposalId", null);
		moProposalService.getProposalStatus(moMyBatisSession, loStatusMap);
	}

	@Test
	public void testFetchProviderIds1() throws Exception
	{
		Boolean loModifiedFlag = true;
		HashMap<String, String> loDataMap = new HashMap<String, String>();
		loDataMap.put("procurementId", "164");
		String lsTaskStatus = "";
		HashMap resultMap = moProposalService.fetchProviderIds(moMyBatisSession, loModifiedFlag, loDataMap,
				lsTaskStatus);
		assertNotNull(resultMap);
	}

	@Test
	public void testFetchProviderIds2() throws Exception
	{
		Boolean loModifiedFlag = false;
		HashMap<String, String> loDataMap = new HashMap<String, String>();
		loDataMap.put("procurementId", "163");
		String lsTaskStatus = "";
		HashMap resultMap = moProposalService.fetchProviderIds(moMyBatisSession, loModifiedFlag, loDataMap,
				lsTaskStatus);
		assertNotNull(resultMap);
	}

	@Test
	public void testUpdateModifiedFlagFromAward1() throws Exception
	{
		HashMap<String, String> loDataMap = new HashMap<String, String>();
		loDataMap.put("procurementId", "131");
		String lsTaskStatus = "";
		Boolean resultMap = moProposalService.updateModifiedFlagFromAward(moMyBatisSession, loDataMap, lsTaskStatus);
		assertTrue(resultMap);
	}

	@Test(expected = ApplicationException.class)
	public void testUpdateModifiedFlagFromAward2() throws ApplicationException
	{
		HashMap<String, String> loDataMap = new HashMap<String, String>();
		String lsTaskStatus = "";
		moProposalService.updateModifiedFlagFromAward(moMyBatisSession, loDataMap, lsTaskStatus);
	}

	@Test
	public void testFetchCountofSelectedProposals1() throws Exception
	{
		HashMap<String, String> loDataMap = new HashMap<String, String>();
		loDataMap.put("procurementId", "132");
		String lsTaskStatus = "";
		Boolean flag = moProposalService.fetchCountofSelectedProposals(moMyBatisSession, loDataMap, lsTaskStatus);
		assertTrue(flag);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchCountofSelectedProposals2() throws ApplicationException
	{
		HashMap<String, String> loDataMap = new HashMap<String, String>();
		String lsTaskStatus = "";
		moProposalService.fetchCountofSelectedProposals(moMyBatisSession, loDataMap, lsTaskStatus);
	}

	@Test
	public void testFetchPermittedUsersForAgencyList1() throws Exception
	{
		List<String> loUserRoleList = new ArrayList<String>();
		loUserRoleList.add(HHSConstants.ACCO_MANAGER_ROLE);
		loUserRoleList.add(HHSConstants.ACCO_STAFF_ROLE);
		loUserRoleList.add(HHSConstants.ACCO_ADMIN_STAFF_ROLE);
		String lsUserOrg = "test";
		List<UserBean> resultList = moProposalService.fetchPermittedUsersForAgencyList(moMyBatisSession,
				loUserRoleList, lsUserOrg);
		assertNotNull(resultList);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchPermittedUsersForAgencyList2() throws ApplicationException
	{
		List<String> loUserRoleList = new ArrayList<String>();
		String lsUserOrg = "";
		moProposalService.fetchPermittedUsersForAgencyList(moMyBatisSession, loUserRoleList, lsUserOrg);
	}

	@Test
	public void testCheckProposalEditSubmit1() throws Exception
	{
		Boolean loValidateStatus = true;
		String lsProposalId = "151";
		Boolean resultList = moProposalService
				.checkProposalEditSubmit(moMyBatisSession, loValidateStatus, lsProposalId);
		assertNotNull(resultList);
	}

	@Test(expected = ApplicationException.class)
	public void testCheckProposalEditSubmit2() throws ApplicationException
	{
		Boolean loValidateStatus = true;
		String lsProposalId = null;
		moProposalService.checkProposalEditSubmit(moMyBatisSession, loValidateStatus, lsProposalId);
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicefetchProposalSiteDetails0Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.fetchProposalSiteDetails(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicesaveProposalDetails1Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.saveProposalDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicefetchMemberDetails2Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.fetchMemberDetails(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicefetchAllOrganizationMembers3Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.fetchAllOrganizationMembers(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicecheckAllRequiredFieldsCompleted4Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.checkAllRequiredFieldsCompleted(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicecheckForDueDate5Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.checkForDueDate(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicesubmitProposal6Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.submitProposal(null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicegetProposalSummary7Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.getProposalSummary(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicegetProposalCount8Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.getProposalCount(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServiceretractProposal9Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.retractProposal(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicecancelProposal10Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.cancelProposal(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicecheckProposalEditSubmit11Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.checkProposalEditSubmit(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicecheckProposalEdit12Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.checkProposalEdit(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicecheckProposalCancel13Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.checkProposalCancel(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicegetDocumentIdList14Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.getDocumentIdList(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicefetchProposalTitle15Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.fetchProposalTitle(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicefetchProposalDetails16Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.fetchProposalDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServiceshowProposalDetailsReadonly17Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.showProposalDetailsReadonly(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicefetchPermittedUsers18Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.fetchPermittedUsers(null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicefetchProposalDocuments19Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.fetchProposalDocuments(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServiceupdateProposalDocumentAndDetailStatus20Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.updateProposalDocumentAndDetailStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicefetchRequiredOptionalDocuments21Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.fetchRequiredOptionalDocuments(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServiceinsertNewProposalDetails22Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.insertNewProposalDetails(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicegetProposalDocumentList24Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.getProposalDocumentList(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServiceinsertProposalDocumentDetails25Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.insertProposalDocumentDetails(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServiceremoveProposalDocs26Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.removeProposalDocs(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicefetchProposalDetailsForTask27Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.fetchProposalDetailsForTask(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicegetProposalSiteDetails28Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.getProposalSiteDetails(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicegetProposalDocuments29Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.getProposalDocuments(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicefetchProcurementTitle30Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.fetchProcurementTitle(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicefetchRFPDocListForTask31Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.fetchRFPDocListForTask(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServiceupdateProposalDocumentStatusForTask32Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.updateProposalDocumentStatusForTask(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicefetchProcTitleAndOrgId33Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.fetchProcTitleAndOrgId(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServiceaddProposalAndProcurementStatus34Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.addProposalAndProcurementStatus(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServiceupdateApprovedProviderStatus35Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.updateApprovedProviderStatus(null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServiceupdateProposalDocumentProperties36Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.updateProposalDocumentProperties(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicegetOrgIdsForSelectedProposals37Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.getOrgIdsForSelectedProposals(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServiceupdateProposalStatusFromTask38Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.updateProposalStatusFromTask(null, null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServiceupdateProposalPreviousStatus39Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.updateProposalPreviousStatus(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicefetchPermittedUsersForAgencyList40Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.fetchPermittedUsersForAgencyList(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicegetNotificationMapForSubmitProposal41Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.getNotificationMapForSubmitProposal(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicefetchCountofSelectedProposals42Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.fetchCountofSelectedProposals(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServiceupdateModifiedFlagFromAward43Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.updateModifiedFlagFromAward(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicefetchProviderIds44Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.fetchProviderIds(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicegetProposalStatus45Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.getProposalStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicegetEvaluationSentFlag46Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.getEvaluationSentFlag(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServicefetchProposalSiteDetails0Negative2() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.fetchProposalSiteDetails(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServicefetchMemberDetails2Negative2() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.fetchMemberDetails(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServicefetchAllOrganizationMembers3Negative2() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.fetchAllOrganizationMembers(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServicecheckAllRequiredFieldsCompleted4Negative2() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.checkAllRequiredFieldsCompleted(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServicecheckForDueDate5Negative2() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.checkForDueDate(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServicegetProposalSummary7Negative2() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.getProposalSummary(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServicegetProposalCount8Negative2() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.getProposalCount(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServiceretractProposal9Negative2() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.retractProposal(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServicecheckProposalCancel13Negative2() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.checkProposalCancel(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServicegetDocumentIdList14Negative2() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.getDocumentIdList(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServicefetchProposalTitle15Negative2() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.fetchProposalTitle(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServicefetchProposalDetails16Negative2() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.fetchProposalDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServiceshowProposalDetailsReadonly17Negative2() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.showProposalDetailsReadonly(null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServicegetProposalDocumentList24Negative2() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.getProposalDocumentList(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServiceinsertProposalDocumentDetails25Negative2() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.insertProposalDocumentDetails(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServicegetProposalSiteDetails28Negative2() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.getProposalSiteDetails(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServiceupdateProposalDocumentStatusForTask32Negative2() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.updateProposalDocumentStatusForTask(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServicefetchProcTitleAndOrgId33Negative2() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.fetchProcTitleAndOrgId(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServiceupdateApprovedProviderStatus35Negative2() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.updateApprovedProviderStatus(null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServiceupdateProposalDocumentProperties36Negative2() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.updateProposalDocumentProperties(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServiceupdateProposalPreviousStatus39Negative2() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.updateProposalPreviousStatus(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServicegetProposalStatus45Negative2() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.getProposalStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServicegetEvaluationSentFlag46Negative2() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.getEvaluationSentFlag(null, null, null);
	}

	/*
	 * This method test the execution of the insertProposalDocumentDetails
	 * method
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertBAFODocumentDetails() throws ApplicationException
	{
		Map<String, Object> loParameterMap = new HashMap<String, Object>();
		loParameterMap.put("procurementId", "162");
		loParameterMap.put("proposalId", "141");
		loParameterMap.put("documentId", "{729SAD7A-3212-448F-91C6-2C9F939E797B}");
		loParameterMap.put("docCreatedDate", new Date());
		loParameterMap.put("userId", "agency_14");
		loParameterMap.put("statusId", "28");
		Integer liRowsUpdated = (Integer) moProposalService.insertBAFODocumentDetails(moMyBatisSession, loParameterMap);
		assertEquals(liRowsUpdated, Integer.valueOf(1));
	}

	/*
	 * This method test the execution of the insertProposalDocumentDetails
	 * method
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testInsertBAFODocumentDetailsExp() throws ApplicationException
	{
		Map<String, Object> loParamMap = null;
		moProposalService.insertBAFODocumentDetails(null, loParamMap);
	}

}
