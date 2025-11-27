/**
 * 
 */
package com.nyc.hhs.contractsbatch.serviceTest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.contractsbatch.service.ContractsBatchService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.ContractBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.PropertyLoader;

/**
 * @author ramesh.kumar.jangra
 * 
 */
public class ContractsBatchServiceTest
{

	private static SqlSession moSession = null; // SQL Session
	private static P8UserSession session = null;
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

	/**
	 * @return
	 * @throws ApplicationException
	 */
	public P8UserSession getFileNetSession() throws ApplicationException
	{
		System.setProperty(P8Constants.PROP_FILE_JAVA_SECURITY_AUTH_LOGIN_CONFIG, PropertyLoader.getProperty(
				P8Constants.PROPERTY_FILE, P8Constants.PROP_FILE_JAVA_SECURITY_AUTH_LOGIN_CONFIG));
		System.setProperty(P8Constants.PROP_FILE_JAVA_NAMING_FACTORY_INITIAL, PropertyLoader.getProperty(
				P8Constants.PROPERTY_FILE, P8Constants.PROP_FILE_JAVA_NAMING_FACTORY_INITIAL));
		System.setProperty(P8Constants.PROP_FILE_FILENET_PE_BOOTSTRAP_CEURI,
				PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, HHSConstants.FILENET_URI));

		P8UserSession loUserSession = new P8UserSession();
		loUserSession.setContentEngineUri(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
				HHSConstants.FILENET_URI));
		loUserSession.setObjectStoreName(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
				HHSConstants.OBJECT_STORE_NAME));
		loUserSession.setIsolatedRegionName(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
				HHSConstants.CONNECTION_POINT_NAME));
		loUserSession.setIsolatedRegionNumber(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
				HHSConstants.CONNECTION_POINT_NUMBER));
		loUserSession.setUserId(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, HHSConstants.CE_USER_ID));
		loUserSession.setPassword(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, HHSConstants.CE_PASSWORD));
		SqlSession loFilenetPEDBSession = HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory()
				.openSession();
		loUserSession.setFilenetPEDBSession(loFilenetPEDBSession);

		return loUserSession;

	}

	ContractsBatchService moContractsBatchService = new ContractsBatchService();

	@Test
	public void testFetchContractsForBatchProcess() throws ApplicationException
	{
		HashMap<Object, Object> loHMArgs = new HashMap<Object, Object>();
		HashMap<Object, Object> loHMCArgs = new HashMap<Object, Object>();
		loHMArgs.put(HHSConstants.STATUS_ID, PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.CONTRAT_STATUS_FOR_BATCH_PROCESS));
		loHMCArgs.put("aoHMArgs", loHMArgs);
		List<ContractBean> loContractBeansList = moContractsBatchService.fetchContractsForBatchProcess(moSession,
				loHMCArgs);
		assertNotNull(loContractBeansList);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchContractsForBatchProcessForAppExp() throws ApplicationException
	{
		HashMap<Object, Object> loHMArgs = new HashMap<Object, Object>();
		HashMap<Object, Object> loHMCArgs = new HashMap<Object, Object>();
		loHMCArgs.put("aoHMArgs", loHMArgs);
		List<ContractBean> loContractBeansList = moContractsBatchService.fetchContractsForBatchProcess(moSession,
				loHMCArgs);
		assertNotNull(loContractBeansList);
	}

	@Test(expected = Exception.class)
	public void testFetchContractsForBatchProcessForExp() throws ApplicationException
	{
		HashMap<Object, Object> loHMArgs = new HashMap<Object, Object>();
		List<ContractBean> loContractBeansList = moContractsBatchService.fetchContractsForBatchProcess(moSession,
				loHMArgs);
		assertNotNull(loContractBeansList);
	}

	@Test(expected = Exception.class)
	public void testFetchContractsForBatchProcessForExp2() throws ApplicationException
	{
		List<ContractBean> loContractBeansList = moContractsBatchService.fetchContractsForBatchProcess(moSession, null);
		assertNotNull(loContractBeansList);
	}

	@Test
	public void testFetchAmendmentContractsForBatchProcess() throws ApplicationException
	{
		HashMap<Object, Object> loHMArgs = new HashMap<Object, Object>();
		HashMap<Object, Object> loHMCArgs = new HashMap<Object, Object>();
		loHMArgs.put(HHSConstants.STATUS_ID, PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.CONTRAT_STATUS_FOR_BATCH_PROCESS));
		loHMCArgs.put("aoHMArgs", loHMArgs);
		List<ContractBean> loContractBeansList = moContractsBatchService.fetchAmendmentContractsForBatchProcess(
				moSession, loHMCArgs);
		assertNotNull(loContractBeansList);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchAmendmentContractsForBatchProcessForAppExp() throws ApplicationException
	{
		HashMap<Object, Object> loHMArgs = new HashMap<Object, Object>();
		HashMap<Object, Object> loHMCArgs = new HashMap<Object, Object>();
		loHMCArgs.put("aoHMArgs", loHMArgs);
		List<ContractBean> loContractBeansList = moContractsBatchService.fetchAmendmentContractsForBatchProcess(
				moSession, loHMCArgs);
		assertNotNull(loContractBeansList);
	}

	@Test(expected = Exception.class)
	public void testFetchAmendmentContractsForBatchProcessForExp() throws ApplicationException
	{
		HashMap<Object, Object> loHMArgs = new HashMap<Object, Object>();
		List<ContractBean> loContractBeansList = moContractsBatchService.fetchAmendmentContractsForBatchProcess(
				moSession, loHMArgs);
		assertNotNull(loContractBeansList);
	}

	@Test(expected = Exception.class)
	public void testFetchAmendmentContractsForBatchProcessForExp2() throws ApplicationException
	{
		List<ContractBean> loContractBeansList = moContractsBatchService.fetchAmendmentContractsForBatchProcess(
				moSession, null);
		assertNotNull(loContractBeansList);
	}

	private ContractBean getDummyContractBeanObject()
	{
		ContractBean loContractBean = new ContractBean();
		Date loConStDate = new Date();
		Date loConEndDate = new Date();
		Date loFmsConStDate = new Date();
		Date loFmsConEndDate = new Date();
		Date loParentConStDate = new Date();
		Date loParentConEndDate = new Date();
		loContractBean.setAgencyId("ACS123");
		loContractBean.setAwardEpin("123456780");
		loContractBean.setBatchRunNeeded(false);
		loContractBean.setContractAmount("1000");
		loContractBean.setContractStartDate(loConStDate);
		loContractBean.setContractEndDate(loConEndDate);
		loContractBean.setContractId("75");
		loContractBean.setContractEndFiscalYear(1);
		loContractBean.setContractStartFiscalYear(2);
		loContractBean.setContractTitle("JUnit Test");
		loContractBean.setContractType("Contract");
		loContractBean.setContractTypeId("2");
		loContractBean.setDiscrepancyFlag("0");
		loContractBean.setDiscrepancyInContractAmount(false);
		loContractBean.setDiscrepancyInStartDate(false);
		loContractBean.setDiscrepancyInEndDate(false);
		loContractBean.setExtCtNumber("CT1234567890");
		loContractBean.setFmsContractAmount("1000");
		loContractBean.setFmsContractAmountAfterMerge("1000");
		loContractBean.setFmsContractEndDate(loFmsConStDate);
		loContractBean.setFmsContractEndFiscalYear(1);
		loContractBean.setFmsContractStartDate(loFmsConEndDate);
		loContractBean.setFmsContractStartFiscalYear(2);
		loContractBean.setFmsTotalFiscalYearsCount(5);
		loContractBean.setOrganizationId("183");
		loContractBean.setStatusId("1");
		loContractBean.setTotalFiscalYearsCount(5);
		loContractBean.setParentAgencyId("ACS123");
		loContractBean.setParentContractAmount("10000");
		loContractBean.setParentContractStartDate(loParentConStDate);
		loContractBean.setParentContractEndDate(loParentConEndDate);
		loContractBean.setParentContractId("75");
		loContractBean.setParentContractTitle("JUnit Test");
		loContractBean.setParentExtCtNumber("CT1234567890");
		loContractBean.setParentOrganizationId("185");

		return loContractBean;
	}

	private ContractBean getDummyContractBeanObjectAmendment()
	{
		ContractBean loContractBean = new ContractBean();
		Date loConStDate = new Date();
		Date loConEndDate = new Date();
		Date loFmsConStDate = new Date();
		Date loFmsConEndDate = new Date();
		Date loParentConStDate = new Date();
		Date loParentConEndDate = new Date();
		loContractBean.setAgencyId("ACS123");
		loContractBean.setAwardEpin("223456780");
		loContractBean.setBatchRunNeeded(false);
		loContractBean.setContractAmount("1000");
		loContractBean.setContractStartDate(loConStDate);
		loContractBean.setContractEndDate(loConEndDate);
		loContractBean.setContractId("75");
		loContractBean.setContractEndFiscalYear(1);
		loContractBean.setContractStartFiscalYear(2);
		loContractBean.setContractTitle("JUnit Test");
		loContractBean.setContractType("Amendment");
		loContractBean.setContractTypeId("2");
		loContractBean.setDiscrepancyFlag("0");
		loContractBean.setDiscrepancyInContractAmount(false);
		loContractBean.setDiscrepancyInStartDate(false);
		loContractBean.setDiscrepancyInEndDate(false);
		loContractBean.setExtCtNumber("CT1234567890");
		loContractBean.setFmsContractAmount("1000");
		loContractBean.setFmsContractAmountAfterMerge("1000");
		loContractBean.setFmsContractEndDate(loFmsConStDate);
		loContractBean.setFmsContractEndFiscalYear(1);
		loContractBean.setFmsContractStartDate(loFmsConEndDate);
		loContractBean.setFmsContractStartFiscalYear(2);
		loContractBean.setFmsTotalFiscalYearsCount(5);
		loContractBean.setOrganizationId("183");
		loContractBean.setStatusId("118");
		loContractBean.setTotalFiscalYearsCount(5);
		loContractBean.setParentAgencyId("ACS123");
		loContractBean.setParentContractAmount("900");
		loContractBean.setParentContractStartDate(loParentConStDate);
		loContractBean.setParentContractEndDate(loParentConEndDate);
		loContractBean.setParentContractId("123456780");
		loContractBean.setParentContractTitle("JUnit Test");
		loContractBean.setParentExtCtNumber("CT1234567890");
		loContractBean.setParentOrganizationId("185");
		return loContractBean;
	}

//	@Test
//	public void testCloseAppropriateTasks() throws ApplicationException
//	{
//		HashMap<Object, Object> loHMArgs = new HashMap<Object, Object>();
//
//		ContractBean loContractBean = getDummyContractBeanObject();
//		P8UserSession loFilenetSession = getFileNetSession();
//
//		loHMArgs.put(HHSConstants.AO_CONTRACT_BEAN, loContractBean);
//		loHMArgs.put(HHSConstants.AO_FILENET_SESSION, loFilenetSession);
//
//		Boolean loResult = moContractsBatchService.closeAppropriateTasks(moSession, loHMArgs);
//		assertTrue(loResult);
//	}

	@Test
	public void testCloseAppropriateTasksForAppExp() throws ApplicationException
	{
		HashMap<Object, Object> loHMArgs = new HashMap<Object, Object>();

		ContractBean loContractBean = getDummyContractBeanObject();
		P8UserSession loFilenetSession = new P8UserSession();

		loHMArgs.put(HHSConstants.AO_CONTRACT_BEAN, loContractBean);
		loHMArgs.put(HHSConstants.AO_FILENET_SESSION, session);

		moContractsBatchService.closeAppropriateTasks(moSession, loHMArgs);
	}

	@Test(expected = Exception.class)
	public void testCloseAppropriateTasksForExp() throws ApplicationException
	{
		HashMap<Object, Object> loHMArgs = new HashMap<Object, Object>();
		Boolean loResult = moContractsBatchService.closeAppropriateTasks(moSession, loHMArgs);
		assertTrue(loResult);
	}

	@Test(expected = Exception.class)
	public void testCloseAppropriateTasksForExp2() throws ApplicationException
	{
		Boolean loResult = moContractsBatchService.closeAppropriateTasks(moSession, null);
		assertTrue(loResult);
	}

	@Test
	public void testUpdateBudgetModificationAndUpdateStatus() throws ApplicationException
	{
		HashMap<Object, Object> loHMArgs = new HashMap<Object, Object>();

		ContractBean loContractBean = getDummyContractBeanObject();
		P8UserSession loFilenetSession = getFileNetSession();

		loHMArgs.put(HHSConstants.AO_CONTRACT_BEAN, loContractBean);
		loHMArgs.put(HHSConstants.AO_FILENET_SESSION, loFilenetSession);

		Boolean loResult = moContractsBatchService.updateBudgetModificationAndUpdateStatus(moSession, loHMArgs);
		assertTrue(loResult);
	}

	@Test(expected = ApplicationException.class)
	public void testUpdateBudgetModificationAndUpdateStatusForAppExp() throws ApplicationException
	{
		HashMap<Object, Object> loHMArgs = new HashMap<Object, Object>();

		ContractBean loContractBean = getDummyContractBeanObject();
		loContractBean.setContractId("");
		loHMArgs.put(HHSConstants.AO_CONTRACT_BEAN, loContractBean);

		Boolean loResult = moContractsBatchService.updateBudgetModificationAndUpdateStatus(null, loHMArgs);
		assertTrue(loResult);
	}

	@Test(expected = Exception.class)
	public void testUpdateBudgetModificationAndUpdateStatusForExp() throws ApplicationException
	{
		HashMap<Object, Object> loHMArgs = new HashMap<Object, Object>();
		Boolean loResult = moContractsBatchService.updateBudgetModificationAndUpdateStatus(moSession, loHMArgs);
		assertTrue(loResult);
	}

	@Test(expected = Exception.class)
	public void testUpdateBudgetModificationAndUpdateStatusForExp2() throws ApplicationException
	{
		Boolean loResult = moContractsBatchService.updateBudgetModificationAndUpdateStatus(moSession, null);
		assertTrue(loResult);
	}

	@Test
	public void testrResetDiscrepancyStatus() throws ApplicationException
	{
		HashMap<Object, Object> loHMArgs = new HashMap<Object, Object>();

		ContractBean loContractBean = getDummyContractBeanObject();
//		P8UserSession loFilenetSession = getFileNetSession();

		loHMArgs.put(HHSConstants.AO_CONTRACT_BEAN, loContractBean);
//		loHMArgs.put(HHSConstants.AO_FILENET_SESSION, loFilenetSession);

		Boolean loResult = moContractsBatchService.resetDiscrepancyStatus(moSession, loHMArgs);
		assertTrue(loResult);
	}

	@Test(expected = ApplicationException.class)
	public void testResetDiscrepancyStatusForAppExp() throws ApplicationException
	{
		HashMap<Object, Object> loHMArgs = new HashMap<Object, Object>();

		ContractBean loContractBean = getDummyContractBeanObject();
		loHMArgs.put(HHSConstants.AO_CONTRACT_BEAN, loContractBean);

		Boolean loResult = moContractsBatchService.resetDiscrepancyStatus(null, loHMArgs);
		assertTrue(loResult);
	}

	@Test
	public void testrRemoveContractDiscrepancies() throws ApplicationException
	{
		HashMap<Object, Object> loHMArgs = new HashMap<Object, Object>();

		ContractBean loContractBean = getDummyContractBeanObject();
//		P8UserSession loFilenetSession = getFileNetSession();

		loHMArgs.put(HHSConstants.AO_CONTRACT_BEAN, loContractBean);
//		loHMArgs.put(HHSConstants.AO_FILENET_SESSION, loFilenetSession);

		Boolean loResult = moContractsBatchService.removeContractDiscrepancies(moSession, loHMArgs);
		assertTrue(loResult);
	}

	@Test(expected = ApplicationException.class)
	public void testRemoveContractDiscrepanciesForAppExp() throws ApplicationException
	{
		HashMap<Object, Object> loHMArgs = new HashMap<Object, Object>();

		ContractBean loContractBean = getDummyContractBeanObject();
		loHMArgs.put(HHSConstants.AO_CONTRACT_BEAN, loContractBean);

		Boolean loResult = moContractsBatchService.removeContractDiscrepancies(null, loHMArgs);
		assertTrue(loResult);
	}

	@Test
	public void testSetContractStatusAsRegistered() throws ApplicationException
	{
		HashMap<Object, Object> loHMArgs = new HashMap<Object, Object>();

		ContractBean loContractBean = getDummyContractBeanObject();
//		P8UserSession loFilenetSession = getFileNetSession();

		loHMArgs.put(HHSConstants.AO_CONTRACT_BEAN, loContractBean);
//		loHMArgs.put(HHSConstants.AO_FILENET_SESSION, loFilenetSession);

		Boolean loResult = moContractsBatchService.setContractStatusAsRegistered(moSession, loHMArgs);
		assertTrue(loResult);
	}

	@Test
	public void testSetContractStatusAsRegisteredForAmendment() throws ApplicationException
	{
		HashMap<Object, Object> loHMArgs = new HashMap<Object, Object>();

		ContractBean loContractBean = getDummyContractBeanObjectAmendment();
//		P8UserSession loFilenetSession = getFileNetSession();
		loContractBean.setContractTypeId("1");
		loHMArgs.put(HHSConstants.AO_CONTRACT_BEAN, loContractBean);
//		loHMArgs.put(HHSConstants.AO_FILENET_SESSION, loFilenetSession);

		Boolean loResult = moContractsBatchService.setContractStatusAsRegistered(moSession, loHMArgs);
		assertTrue(loResult);
	}

	@Test(expected = ApplicationException.class)
	public void testSetContractStatusAsRegisteredForAppExp() throws ApplicationException
	{
		HashMap<Object, Object> loHMArgs = new HashMap<Object, Object>();

		ContractBean loContractBean = getDummyContractBeanObject();
		loContractBean.setContractId("");
		loHMArgs.put(HHSConstants.AO_CONTRACT_BEAN, loContractBean);

		Boolean loResult = moContractsBatchService.setContractStatusAsRegistered(null, loHMArgs);
		assertTrue(loResult);
	}

	@Test(expected = ApplicationException.class)
	public void testSetContractStatusAsRegisteredForAppExp2() throws ApplicationException
	{
		HashMap<Object, Object> loHMArgs = new HashMap<Object, Object>();

		ContractBean loContractBean = getDummyContractBeanObject();
		loContractBean.setExtCtNumber(null);
		P8UserSession loFilenetSession = getFileNetSession();

		loHMArgs.put(HHSConstants.AO_CONTRACT_BEAN, loContractBean);
		loHMArgs.put(HHSConstants.AO_FILENET_SESSION, loFilenetSession);

		Boolean loResult = moContractsBatchService.setContractStatusAsRegistered(moSession, loHMArgs);
		assertTrue(loResult);
	}

	@Test(expected = Exception.class)
	public void testSetContractStatusAsRegisteredForExp() throws ApplicationException
	{
		HashMap<Object, Object> loHMArgs = new HashMap<Object, Object>();
		Boolean loResult = moContractsBatchService.setContractStatusAsRegistered(moSession, loHMArgs);
		assertTrue(loResult);
	}

	@Test(expected = Exception.class)
	public void testSetContractStatusAsRegisteredForExp2() throws ApplicationException
	{
		Boolean loResult = moContractsBatchService.setContractStatusAsRegistered(moSession, null);
		assertTrue(loResult);
	}

	@Test
	public void testMergeAmendmentInBaseContract() throws ApplicationException
	{
		HashMap<Object, Object> loHMArgs = new HashMap<Object, Object>();

		ContractBean loContractBean = getDummyContractBeanObject();
		loContractBean.setContractId("75");
//		P8UserSession loFilenetSession = getFileNetSession();

		loHMArgs.put(HHSConstants.AO_CONTRACT_BEAN, loContractBean);
//		loHMArgs.put(HHSConstants.AO_FILENET_SESSION, loFilenetSession);

		Boolean loResult = moContractsBatchService.mergeAmendmentInBaseContract(moSession, loHMArgs);
		assertTrue(loResult);
	}

	@Test
	public void testMergeAmendmentInBaseContractForZeroAmendment() throws ApplicationException
	{
		HashMap<Object, Object> loHMArgs = new HashMap<Object, Object>();

		ContractBean loContractBean = getDummyContractBeanObject();
		loContractBean.setContractId("75");
		loContractBean.setContractAmount("0");
//		P8UserSession loFilenetSession = getFileNetSession();

		loHMArgs.put(HHSConstants.AO_CONTRACT_BEAN, loContractBean);
//		loHMArgs.put(HHSConstants.AO_FILENET_SESSION, loFilenetSession);

		Boolean loResult = moContractsBatchService.mergeAmendmentInBaseContract(moSession, loHMArgs);
		assertTrue(loResult);
	}

	@Test(expected = ApplicationException.class)
	public void testMergeAmendmentInBaseContractForAppExp() throws ApplicationException
	{
		HashMap<Object, Object> loHMArgs = new HashMap<Object, Object>();

		ContractBean loContractBean = getDummyContractBeanObject();
		loContractBean.setContractId("");
		loHMArgs.put(HHSConstants.AO_CONTRACT_BEAN, loContractBean);

		Boolean loResult = moContractsBatchService.mergeAmendmentInBaseContract(null, loHMArgs);
		assertTrue(loResult);
	}

	@Test(expected = Exception.class)
	public void testMergeAmendmentInBaseContractForExp() throws ApplicationException
	{
		HashMap<Object, Object> loHMArgs = new HashMap<Object, Object>();
		Boolean loResult = moContractsBatchService.mergeAmendmentInBaseContract(moSession, loHMArgs);
		assertTrue(loResult);
	}

	@Test(expected = Exception.class)
	public void testMergeAmendmentInBaseContractForExp2() throws ApplicationException
	{
		Boolean loResult = moContractsBatchService.mergeAmendmentInBaseContract(moSession, null);
		assertTrue(loResult);
	}

	@Test
	public void testSetWFPropForContConfUpdateTask() throws ApplicationException
	{
		HashMap<Object, Object> loHMArgs = new HashMap<Object, Object>();

		ContractBean loContractBean = getDummyContractBeanObject();
//		P8UserSession loFilenetSession = getFileNetSession();

		loHMArgs.put(HHSConstants.AO_CONTRACT_BEAN, loContractBean);
//		loHMArgs.put(HHSConstants.AO_FILENET_SESSION, loFilenetSession);

		HashMap loHMWFArgs = moContractsBatchService.setWFPropForContConfUpdateTask(moSession, loHMArgs);
		assertNotNull(loHMWFArgs);
	}

	@Test(expected = ApplicationException.class)
	public void testSetWFPropForContConfUpdateTaskForAppExp() throws ApplicationException
	{
		HashMap<Object, Object> loHMArgs = new HashMap<Object, Object>();

		ContractBean loContractBean = getDummyContractBeanObject();
		loContractBean.setContractId("");
		loHMArgs.put(HHSConstants.AO_CONTRACT_BEAN, loContractBean);

		HashMap loHMWFArgs = moContractsBatchService.setWFPropForContConfUpdateTask(null, loHMArgs);
		assertNotNull(loHMWFArgs);
	}

	@Test(expected = Exception.class)
	public void testSetWFPropForContConfUpdateTaskForExp() throws ApplicationException
	{
		HashMap<Object, Object> loHMArgs = new HashMap<Object, Object>();
		HashMap loHMWFArgs = moContractsBatchService.setWFPropForContConfUpdateTask(moSession, loHMArgs);
		assertNotNull(loHMWFArgs);
	}

	@Test(expected = Exception.class)
	public void testSetWFPropForContConfUpdateTaskForExp2() throws ApplicationException
	{
		HashMap loHMWFArgs = moContractsBatchService.setWFPropForContConfUpdateTask(moSession, null);
		assertNotNull(loHMWFArgs);
	}

	@Test
	public void testCreateContractUpdateRecordAndFYs() throws ApplicationException
	{
		ContractBean loContractBean = getDummyContractBeanObject();
		moContractsBatchService.createContractUpdateRecordAndFYs(moSession, loContractBean);

	}

	@Test
	public void testCreateContractUpdateRecordAndFYsForAmendment() throws ApplicationException
	{
		ContractBean loContractBean = getDummyContractBeanObjectAmendment();
		moContractsBatchService.createContractUpdateRecordAndFYs(moSession, loContractBean);

	}

	@Test(expected = ApplicationException.class)
	public void testcCreateContractUpdateRecordAndFYsForAppExp() throws ApplicationException
	{
		ContractBean loContractBean = getDummyContractBeanObject();
		loContractBean.setContractId("");
		moContractsBatchService.createContractUpdateRecordAndFYs(null, loContractBean);
	}

	@Test(expected = Exception.class)
	public void testCreateContractUpdateRecordAndFYsForExp() throws ApplicationException
	{
		moContractsBatchService.createContractUpdateRecordAndFYs(null, null);
	}

	@Test(expected = Exception.class)
	public void testCreateContractUpdateRecordAndFYsForExp2() throws ApplicationException
	{
		moContractsBatchService.createContractUpdateRecordAndFYs(moSession, null);
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testUpdateInvoiceIdForBatch1() throws ApplicationException
	{
		HashMap loHashmap = new HashMap();
		loHashmap.put("InvoiceNumber", "53");
		Boolean loStatus = moContractsBatchService.updateInvoiceIdForBatch(moSession, loHashmap);
		assertNotNull(loStatus);
	}
	
	@Test(expected = Exception.class)
	public void testUpdateInvoiceIdForBatch2() throws ApplicationException
	{
		HashMap loHashmap = new HashMap();
		loHashmap.put("InvoiceNumber", "53");
		Boolean loStatus = moContractsBatchService.updateInvoiceIdForBatch(null, loHashmap);
		assertNotNull(loStatus);
	}
	
	@Test
	public void testFetchApproveInvoiceFromETL1() throws ApplicationException
	{
		List<HashMap> loInvoiceDetails = moContractsBatchService.fetchApproveInvoiceFromETL(moSession, new HashMap());
		assertNotNull(loInvoiceDetails);
	}
	
	@Test(expected = Exception.class)
	public void testFetchApproveInvoiceFromETL2() throws ApplicationException
	{
		moContractsBatchService.fetchApproveInvoiceFromETL(null, new HashMap());
	}
	
	
	@Test
	public void testFetchBudgetAdvanceFromETL1() throws ApplicationException
	{
		List<HashMap> loResultDetails = moContractsBatchService.fetchBudgetAdvanceFromETL(moSession);
		assertNotNull(loResultDetails);
	}
	
	@Test(expected = Exception.class)
	public void testFetchBudgetAdvanceFromETL2() throws ApplicationException
	{
		moContractsBatchService.fetchBudgetAdvanceFromETL(null);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testUpdateBudgetAdvanceIdForBatch1() throws ApplicationException
	{
		HashMap loHashMap = new HashMap();
		loHashMap.put("BudgetAdvanceId", "4");
		Boolean loStatus = moContractsBatchService.updateBudgetAdvanceIdForBatch(moSession, loHashMap);
		assertNotNull(loStatus);
	}
	
	@Test(expected = ApplicationException.class)
	public void testUpdateBudgetAdvanceIdForBatch2() throws ApplicationException
	{
		moContractsBatchService.updateBudgetAdvanceIdForBatch(null, new HashMap());
	}
	
	@Test
	public void testSetContractStatusAsRegisteredForCOF() throws ApplicationException
	{
		HashMap<Object, Object> loHMArgs = new HashMap<Object, Object>();
		ContractBean loContractBean = getDummyContractBeanObject();
		loHMArgs.put(HHSConstants.AO_CONTRACT_BEAN, loContractBean);
		Boolean loResult = moContractsBatchService.setContractStatusAsRegisteredForCOF(moSession, loHMArgs);
		assertTrue(loResult);
	}
	
	@Test(expected = ApplicationException.class)
	public void testSetContractStatusAsRegisteredForCOFAppExp() throws ApplicationException
	{
		HashMap<Object, Object> loHMArgs = new HashMap<Object, Object>();
		ContractBean loContractBean = getDummyContractBeanObject();
		loHMArgs.put(HHSConstants.AO_CONTRACT_BEAN, loContractBean);
		Boolean loResult = moContractsBatchService.setContractStatusAsRegisteredForCOF(null, loHMArgs);
		assertTrue(loResult);
	}
	@Test(expected = Exception.class)
	public void testSetContractStatusAsRegisteredForCOFAppExp2() throws ApplicationException
	{
		HashMap<Object, Object> loHMArgs = new HashMap<Object, Object>();
		ContractBean loContractBean = getDummyContractBeanObject();
		loContractBean.setContractId("");
		loHMArgs.put(HHSConstants.AO_CONTRACT_BEAN, null);
		Boolean loResult = moContractsBatchService.setContractStatusAsRegisteredForCOF(null, loHMArgs);
		assertTrue(loResult);
	}
	
	@Test
	public void testSetContractStatusAsRegisteredForAmendmentCOF() throws ApplicationException
	{
		//HashMap<Object, Object> loHMArgs = new HashMap<Object, Object>();
		ContractBean loContractBean = getDummyContractBeanObject();
		loContractBean.setContractTypeId("1");
		//loHMArgs.put(HHSConstants.AO_CONTRACT_BEAN, loContractBean);
	    Boolean loResult= moContractsBatchService.setContractStatusAsRegisteredForAmendmentCOF(moSession, loContractBean);
		assertTrue(loResult);
	}

	@Test(expected = ApplicationException.class)
	public void testSetContractStatusAsRegisteredForAmendmentCOFForAppExp() throws ApplicationException
	{
		HashMap<Object, Object> loHMArgs = new HashMap<Object, Object>();

		ContractBean loContractBean = getDummyContractBeanObject();
		loContractBean.setContractId("");
		Boolean loResult = moContractsBatchService.setContractStatusAsRegisteredForAmendmentCOF(null, loContractBean);
		assertTrue(loResult);
	}
	@Test(expected = Exception.class)
	public void testSetContractStatusAsRegisteredForAmendmentCOFForAppExp2() throws ApplicationException
	{
		ContractBean loContractBean = getDummyContractBeanObject();
		loContractBean.setContractId("");
		Boolean loResult = moContractsBatchService.setContractStatusAsRegisteredForAmendmentCOF(null, null);
		assertTrue(loResult);
	}
	
}
