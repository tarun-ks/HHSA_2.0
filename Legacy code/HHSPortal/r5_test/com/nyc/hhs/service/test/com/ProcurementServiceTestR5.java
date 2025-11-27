package com.nyc.hhs.service.test.com;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.HashMap;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.daomanager.service.ProcurementService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.Procurement;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.PropertyLoader;

public class ProcurementServiceTestR5
{
	ProcurementService procurementService = new ProcurementService();
	
	private static SqlSession moSession = null; // SQL Session
	
	/**
	 * SQL session created ONCE before the class
	 * 
	 * @throws java.lang.Exception
	 */
	private static P8UserSession moP8session = null;
	private static TaskDetailsBean loTaskBean = null;
	
	/**
	 * SQL session created ONCE before the class
	 * 
	 * @throws java.lang.Exception
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
		loUserSession.setIsolatedRegionNumber("3");
		SqlSession loFilenetPEDBSession = HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory()
				.openSession();
		loUserSession.setFilenetPEDBSession(loFilenetPEDBSession);
		return loUserSession;
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		Boolean lbThrown = false;
		try
		{
			moSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
			moP8session = getFileNetSession();
			System.out.println("Before");
		}
		catch (Exception loEx)
		{
			/*
			 * lbThrown = true; assertTrue("Exception thrown", lbThrown);
			 */
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
		/*
		 * catch (Exception loEx) { lbThrown = true;
		 * assertTrue("Exception thrown", lbThrown); }
		 */
		finally
		{
			/*
			 * moSession.rollback(); moSession.close();
			 */
		}
	}
	
	@Test
	public void updatePcofPsrVersionNumberCase1() throws ApplicationException
	{
		String loprocurementId = "3925";
		assertTrue(procurementService.updatePcofPsrVersionNumber(moSession, loprocurementId));
	}
	
	@Test
	public void updatePcofPsrVersionNumberCase2() throws ApplicationException
	{
		String loprocurementId = "1111";
		
		assertFalse(procurementService.updatePcofPsrVersionNumber(moSession, loprocurementId));
	}
	
	@Test(expected = Exception.class)
	public void updatePcofPsrVersionNumberCase3() throws ApplicationException
	{
		String loprocurementId = "1111";
		
		procurementService.updatePcofPsrVersionNumber(null, loprocurementId);
	}
	
	@Test
	public void validatePCOFTaskApprovedCase1() throws ApplicationException
	{
		String loprocurementId = "3945";
		
		Procurement loProcurementBean = new Procurement();
		loProcurementBean.setIsOpenEndedRFP("1");
		
		assertFalse(procurementService.validatePCOFTaskApproved(moSession, loprocurementId, loProcurementBean));
	}
	
	@Test
	public void validatePCOFTaskApprovedCase2() throws ApplicationException
	{
		String loprocurementId = "3945";
		
		Procurement loProcurementBean = new Procurement();
		loProcurementBean.setIsOpenEndedRFP("0");
		loProcurementBean.setEstProcurementValue(new BigDecimal("1"));
		assertTrue(procurementService.validatePCOFTaskApproved(moSession, loprocurementId, loProcurementBean));
	}
	
	@Test
	public void validatePCOFTaskApprovedCase3() throws ApplicationException
	{
		
		String loprocurementId = "4437";
		
		Procurement loProcurementBean = new Procurement();
		loProcurementBean.setIsOpenEndedRFP("0");
		loProcurementBean.setEstProcurementValue(new BigDecimal("4"));
		assertTrue(procurementService.validatePCOFTaskApproved(moSession, loprocurementId, loProcurementBean));
	}
	
	@Test(expected = Exception.class)
	public void validatePCOFTaskApprovedCase4() throws ApplicationException
	{
		String loprocurementId = "1111";
		
		Procurement loProcurementBean = new Procurement();
		boolean loIsOpenEndedOrZeroValue = false;
		procurementService.validatePCOFTaskApproved(null, loprocurementId, loProcurementBean);
	}
	
	@Test
	public void validatePSRTaskApprovedCase1() throws ApplicationException
	{
		String loprocurementId = "4126";
		assertFalse(procurementService.validatePSRTaskApproved(moSession, loprocurementId));
	}
	
	@Test
	public void validatePSRTaskApprovedCase2() throws ApplicationException
	{
		String loprocurementId = "4112";
		assertTrue(procurementService.validatePSRTaskApproved(moSession, loprocurementId));
	}
	
	@Test(expected = Exception.class)
	public void validatePSRTaskApprovedCase3() throws ApplicationException
	{
		String loprocurementId = "4112";
		procurementService.validatePSRTaskApproved(null, loprocurementId);
	}
	
	@Test(expected = Exception.class)
	public void resetPSRTaskFlagCase1() throws ApplicationException
	{
		// String loprocurementId = "4112";
		HashMap<String, String> loHMWFRequiredProps = new HashMap<String, String>();
		loHMWFRequiredProps.put("loprocurementId", "4112");
		assertTrue(procurementService.resetPSRTaskFlag(moSession, loHMWFRequiredProps));
	}
	
	@Test
	public void resetPSRTaskFlagCase2() throws ApplicationException
	{
		// String loprocurementId = "4112";
		HashMap<String, String> loHMWFRequiredProps = new HashMap<String, String>();
		loHMWFRequiredProps.put("procurementId", "4150");
		assertTrue(procurementService.resetPSRTaskFlag(moSession, loHMWFRequiredProps));
	}
	
	@Test
	public void saveProcurementSummaryCase1() throws ApplicationException
	{
		// String loprocurementId = "4112";
		Integer loStatusId = 4122;
		Procurement loProcurementBean = new Procurement();
		
		assertTrue(procurementService.saveProcurementSummary(moSession, loProcurementBean, loStatusId));
	}
	
	@Test(expected = Exception.class)
	public void saveProcurementSummaryCase2() throws ApplicationException
	{
		// String loprocurementId = "4112";
		Integer loStatusId = null;
		Procurement loProcurementBean = new Procurement();
		
		assertTrue(procurementService.saveProcurementSummary(moSession, loProcurementBean, loStatusId));
	}
	
	@Test(expected = Exception.class)
	public void saveProcurementSummaryCase3() throws ApplicationException
	{
		// String loprocurementId = "4112";
		Integer loStatusId = null;
		Procurement loProcurementBean = null;
		// String ProcurementId = "4102";
		// loProcurementBean.setProcurementId("1212");
		// loProcurementBean.setPreProposalConferenceDateUpdated("getPreProposalConferenceDateUpdated");
		// loProcurementBean.setPreProposalConferenceDatePlanned("preProposalConferenceDatePlanned");
		// loProcurementBean.setProcurementTitle("Title");
		// loProcurementBean.setProcurementEpin("procurementEpin");
		// loProcurementBean.setStatus(2);
		// loProcurementBean.setAgencyId("Agency_14");
		// loProcurementBean.setProgramName("Name");
		// loProcurementBean.setAccPrimaryContact("accPrimaryContact");
		// loProcurementBean.setAccSecondaryContact("accSecondaryContact");
		// loProcurementBean.setAgecncyPrimaryContact("agecncyPrimaryContact");
		// loProcurementBean.setAgecncySecondaryContact("agecncySecondaryContact");
		// loProcurementBean.setPreProposalConferenceDatePlanned("setPreProposalCon");
		// loProcurementBean.setPreProposalConferenceDateUpdated("pre data");
		
		assertTrue(procurementService.saveProcurementSummary(moSession, loProcurementBean, loStatusId));
	}
	
	@Test(expected = Exception.class)
	public void saveProcurementSummaryCase4() throws ApplicationException
	{
		// String loprocurementId = "4112";
		Integer loStatusId = null;
		Procurement loProcurementBean = new Procurement();
		// String ProcurementId = "4102";
		loProcurementBean.setProcurementId("");
		// loProcurementBean.setPreProposalConferenceDateUpdated("getPreProposalConferenceDateUpdated");
		// loProcurementBean.setPreProposalConferenceDatePlanned("preProposalConferenceDatePlanned");
		// loProcurementBean.setProcurementTitle("Title");
		// loProcurementBean.setProcurementEpin("procurementEpin");
		// loProcurementBean.setStatus(2);
		// loProcurementBean.setAgencyId("Agency_14");
		// loProcurementBean.setProgramName("Name");
		// loProcurementBean.setAccPrimaryContact("accPrimaryContact");
		// loProcurementBean.setAccSecondaryContact("accSecondaryContact");
		// loProcurementBean.setAgecncyPrimaryContact("agecncyPrimaryContact");
		// loProcurementBean.setAgecncySecondaryContact("agecncySecondaryContact");
		loProcurementBean.setPreProposalConferenceDatePlanned("");
		loProcurementBean.setPreProposalConferenceDateUpdated("");
		
		assertTrue(procurementService.saveProcurementSummary(moSession, loProcurementBean, loStatusId));
	}
	
	/*@Test
	public void saveProcurementSummaryCase5() throws ApplicationException
	{
		String loprocurementId = "4112";
		Integer loStatusId = null;
		Procurement loProcurementBean = new Procurement();
		loProcurementBean.setProcurementId("null");
		loProcurementBean.setPreProposalConferenceDatePlanned("");
		loProcurementBean.setPreProposalConferenceDateUpdated("");		
		assertTrue(procurementService.saveProcurementSummary(moSession, loProcurementBean, loStatusId));
	}
	
	@Test
	public void saveProcurementSummaryCase6() throws ApplicationException
	{
		// String loprocurementId = "4112";
		Integer loStatusId = null;
		Procurement loProcurementBean = new Procurement();
		// String ProcurementId = "4102";
		loProcurementBean.setProcurementId("3925");
		loProcurementBean.setStatus(null);
		// loProcurementBean.setPreProposalConferenceDateUpdated("getPreProposalConferenceDateUpdated");
		// loProcurementBean.setPreProposalConferenceDatePlanned("preProposalConferenceDatePlanned");
		loProcurementBean.setProcurementTitle("Auto Test");
		loProcurementBean.setProcurementEpin("06810I0098");
		loProcurementBean.setProcurementDescription("Test");
		loProcurementBean.setStatus(2);
		loProcurementBean.setAgencyId("DOC");
		loProcurementBean.setProgramName("Name");
		loProcurementBean.setProgramId(152);
		loProcurementBean.setLinkToConceptReport("https://www.link.com");
		loProcurementBean.setRfpReleaseDatePlanned("31-JUL-16 12.00.00.000000000 AM");
		loProcurementBean.setRfpReleaseDateUpdated("31-JUL-16 12.00.00.000000000 AM");
		// loProcurementBean.setPreProposalConferenceDatePlanned("01-JUL-16 12.00.00.000000000 AM");
		loProcurementBean.setPreProposalConferenceDateUpdated("01-JUL-16 12.00.00.000000000 AM");
		loProcurementBean.setProposalDueDatePlanned("31-JUL-16 12.00.00.000000000 AM");
		loProcurementBean.setProposalDueDateUpdated("31-JUL-16 12.00.00.0cc00000000 AM");
		loProcurementBean.setFirstRFPEvalDatePlanned("01-JUL-16 12.00.00.000000000 AM");
		loProcurementBean.setFirstRFPEvalDateUpdated("01-JUL-16 12.00.00.000000000 AM");
		loProcurementBean.setFinalRFPEvalDatePlanned("01-JUL-16 12.00.00.000000000 AM");
		loProcurementBean.setEvaluatorTrainingDatePlanned("01-JUL-16 12.00.00.000000000 AM");
		loProcurementBean.setEvaluatorTrainingDateUpdated("01-JUL-16 12.00.00.000000000 AM");
		loProcurementBean.setFirstEvalCompletionDatePlanned("01-JUL-16 12.00.00.000000000 AM");
		loProcurementBean.setFirstEvalCompletionDateUpdated("01-JUL-16 12.00.00.000000000 AM");
		loProcurementBean.setIsOpenEndedRFP("1");
		loProcurementBean.setServiceUnitRequiredFlag("1");
		loProcurementBean.setFinalEvalCompletionDatePlanned("01-JUL-16 12.00.00.000000000 AM");
		loProcurementBean.setFinalEvalCompletionDateUpdated("01-JUL-16 12.00.00.000000000 AM");
		loProcurementBean.setAwardSelectionDatePlanned("01-JUL-16 12.00.00.000000000 AM");
		loProcurementBean.setAwardSelectionDateUpdated("01-JUL-16 12.00.00.000000000 AM");
		loProcurementBean.setContractStartDatePlanned("28-JUL-16 12.00.00.000000000 AM");
		loProcurementBean.setContractStartDateUpdated("28-JUL-16 12.00.00.000000000 AM");
		loProcurementBean.setContractEndDatePlanned("31-JUL-16 12.00.00.000000000 AM");
		loProcurementBean.setContractEndDateUpdated("31-JUL-16 12.00.00.000000000 AM");
		loProcurementBean.setServiceUnitRequiredFlag("1");
		loProcurementBean.setModifiedBy("city_459");
		loProcurementBean.setFinalRFPEvalDateUpdated("01-JUL-16 12.00.00.000000000 AM");
		loProcurementBean.setAccPrimaryContact("city_142");
		loProcurementBean.setEstNumberOfContracts(1);
		loProcurementBean.setEstProcurementValue(new java.math.BigDecimal(1));
		loProcurementBean.setAccSecondaryContact("accSecondaryContact");
		loProcurementBean.setAgecncyPrimaryContact("agecncyPrimaryContact");
		loProcurementBean.setAgecncySecondaryContact("agecncySecondaryContact");
		loProcurementBean.setAgencyPrimaryEmail("ghfjh@jikj.com");
		loProcurementBean.setPreProposalConferenceDatePlanned("setPreProposalCon");
		loProcurementBean.setPreProposalConferenceDateUpdated("pre data");
		
		assertTrue(procurementService.saveProcurementSummary(moSession, loProcurementBean, loStatusId));
	}*/
	
	@Test
	public void updatePCOFContractDatesCase1() throws ApplicationException
	{
		String loprocurementId = "4112";
		
		assertTrue(procurementService.updatePCOFContractDates(moSession, loprocurementId));
	}
	
	@Test(expected = ApplicationException.class)
	public void updatePCOFContractDatesCase2() throws ApplicationException
	{
		String loprocurementId = "4112";
		
		procurementService.updatePCOFContractDates(null, loprocurementId);
	}
	
	/*@Test
	public void updatePCOFContractDatesIfLaunchedCase1() throws ApplicationException
	{
		String loprocurementId = "4112";
		
		assertTrue(procurementService.updatePCOFContractDatesIfLaunched(moP8session, moSession, loprocurementId));
	}
	*/
	@Test(expected = ApplicationException.class)
	public void updatePCOFContractDatesIfLaunchedCase2() throws ApplicationException
	{
		String loprocurementId = "4112";
		
		procurementService.updatePCOFContractDatesIfLaunched(moP8session, null, loprocurementId);
	}
	
}
