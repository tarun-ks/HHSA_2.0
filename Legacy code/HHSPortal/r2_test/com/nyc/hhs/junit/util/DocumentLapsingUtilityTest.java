package com.nyc.hhs.junit.util;

import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.util.DocumentLapsingUtility;

public class DocumentLapsingUtilityTest {

	/**
	 * This method tests RollbackDueDateOnRejection method for data inputs and
	 * an ApplicationException is expected
	 * 
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 */
	@SuppressWarnings({ "deprecation", "rawtypes" })
	@Test
	public void testRollbackDueDateOnRejection() throws ApplicationException {

		Map lsMsg = null;
		Date aoDCurrentDueDate = new Date("01/01/2013");
		String asPeriodCoveredStartMonth = "jul";
		int aiPeriodCoveredStartYear = 1;
		String asPeriodCoveredEndMonth = "jun";
		int aiPeriodCoveredEndYear = 1;
		boolean abafterShortFiling = true;
		boolean abIsShortFiling = false;
		// First Scenario
		lsMsg = DocumentLapsingUtility.rollbackDueDateOnRejection(
				P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE, aoDCurrentDueDate,
				asPeriodCoveredStartMonth, aiPeriodCoveredStartYear,
				asPeriodCoveredEndMonth, aiPeriodCoveredEndYear,
				P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE,
				P8Constants.PROPERTY_PE_LAW_TYPE, abIsShortFiling,
				abafterShortFiling);
		assertTrue(!lsMsg.isEmpty());
		
		// Second Scenario
		lsMsg = DocumentLapsingUtility.rollbackDueDateOnRejection(
				P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE, aoDCurrentDueDate,
				asPeriodCoveredStartMonth, aiPeriodCoveredStartYear,
				asPeriodCoveredEndMonth, aiPeriodCoveredEndYear,
				P8Constants.PROPERTY_CE_CHAR500_EXT1_DOC_TYPE,
				P8Constants.PROPERTY_PE_LAW_TYPE, abIsShortFiling,
				abafterShortFiling);
		assertTrue(!lsMsg.isEmpty());
		

		// Third Scenario
		lsMsg = DocumentLapsingUtility.rollbackDueDateOnRejection(
				P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE, aoDCurrentDueDate,
				asPeriodCoveredStartMonth, aiPeriodCoveredStartYear,
				asPeriodCoveredEndMonth, aiPeriodCoveredEndYear,
				P8Constants.PROPERTY_CE_CHAR500_EXT2_DOC_TYPE,
				P8Constants.PROPERTY_PE_LAW_TYPE, abIsShortFiling,
				abafterShortFiling);
		assertTrue(!lsMsg.isEmpty());
		

		// Fourth Scenario
		lsMsg = DocumentLapsingUtility.rollbackDueDateOnRejection(
				P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE, aoDCurrentDueDate,
				asPeriodCoveredStartMonth, aiPeriodCoveredStartYear,
				asPeriodCoveredEndMonth, aiPeriodCoveredEndYear,
				P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE, null,
				abIsShortFiling, abafterShortFiling);
		assertTrue(!lsMsg.isEmpty());
		

		// Fifth Scenario
		lsMsg = DocumentLapsingUtility.rollbackDueDateOnRejection(
				P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE, aoDCurrentDueDate,
				asPeriodCoveredStartMonth, aiPeriodCoveredStartYear,
				asPeriodCoveredEndMonth, aiPeriodCoveredEndYear,
				P8Constants.PROPERTY_CE_CHAR500_EXT1_DOC_TYPE, null,
				abIsShortFiling, abafterShortFiling);
		assertTrue(!lsMsg.isEmpty());
		

		// Sixth Scenario
		lsMsg = DocumentLapsingUtility.rollbackDueDateOnRejection(
				P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE, aoDCurrentDueDate,
				asPeriodCoveredStartMonth, aiPeriodCoveredStartYear,
				asPeriodCoveredEndMonth, aiPeriodCoveredEndYear,
				P8Constants.PROPERTY_CE_CHAR500_EXT2_DOC_TYPE, null,
				abIsShortFiling, abafterShortFiling);
		assertTrue(!lsMsg.isEmpty());
		

		// Seventh Scenario
		lsMsg = DocumentLapsingUtility.rollbackDueDateOnRejection(
				P8Constants.PROPERTY_CE_CHAR500_EXT1_DOC_TYPE,
				aoDCurrentDueDate, asPeriodCoveredStartMonth,
				aiPeriodCoveredStartYear, asPeriodCoveredEndMonth,
				aiPeriodCoveredEndYear,
				P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE,
				P8Constants.PROPERTY_PE_LAW_TYPE, abIsShortFiling,
				abafterShortFiling);
		assertTrue(!lsMsg.isEmpty());
		

		// Eighth Scenario
		lsMsg = DocumentLapsingUtility.rollbackDueDateOnRejection(
				P8Constants.PROPERTY_CE_CHAR500_EXT1_DOC_TYPE,
				aoDCurrentDueDate, asPeriodCoveredStartMonth,
				aiPeriodCoveredStartYear, asPeriodCoveredEndMonth,
				aiPeriodCoveredEndYear,
				P8Constants.PROPERTY_CE_CHAR500_EXT1_DOC_TYPE,
				P8Constants.PROPERTY_PE_LAW_TYPE, abIsShortFiling,
				abafterShortFiling);
		assertTrue(!lsMsg.isEmpty());
		

		// Ninth Scenario
		lsMsg = DocumentLapsingUtility.rollbackDueDateOnRejection(
				P8Constants.PROPERTY_CE_CHAR500_EXT1_DOC_TYPE,
				aoDCurrentDueDate, asPeriodCoveredStartMonth,
				aiPeriodCoveredStartYear, asPeriodCoveredEndMonth,
				aiPeriodCoveredEndYear,
				P8Constants.PROPERTY_CE_CHAR500_EXT2_DOC_TYPE,
				P8Constants.PROPERTY_PE_LAW_TYPE, abIsShortFiling,
				abafterShortFiling);
		assertTrue(!lsMsg.isEmpty());
		

		// Tenth Scenario
		lsMsg = DocumentLapsingUtility.rollbackDueDateOnRejection(
				P8Constants.PROPERTY_CE_CHAR500_EXT1_DOC_TYPE,
				aoDCurrentDueDate, asPeriodCoveredStartMonth,
				aiPeriodCoveredStartYear, asPeriodCoveredEndMonth,
				aiPeriodCoveredEndYear,
				P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE, null,
				abIsShortFiling, abafterShortFiling);
		assertTrue(!lsMsg.isEmpty());
		

		// Eleventh Scenario
		lsMsg = DocumentLapsingUtility.rollbackDueDateOnRejection(
				P8Constants.PROPERTY_CE_CHAR500_EXT1_DOC_TYPE,
				aoDCurrentDueDate, asPeriodCoveredStartMonth,
				aiPeriodCoveredStartYear, asPeriodCoveredEndMonth,
				aiPeriodCoveredEndYear,
				P8Constants.PROPERTY_CE_CHAR500_EXT1_DOC_TYPE, null,
				abIsShortFiling, abafterShortFiling);
		assertTrue(!lsMsg.isEmpty());
		

		// Twelth Scenario
		lsMsg = DocumentLapsingUtility.rollbackDueDateOnRejection(
				P8Constants.PROPERTY_CE_CHAR500_EXT1_DOC_TYPE,
				aoDCurrentDueDate, asPeriodCoveredStartMonth,
				aiPeriodCoveredStartYear, asPeriodCoveredEndMonth,
				aiPeriodCoveredEndYear,
				P8Constants.PROPERTY_CE_CHAR500_EXT2_DOC_TYPE, null,
				abIsShortFiling, abafterShortFiling);
		assertTrue(!lsMsg.isEmpty());
		

		// Thirteenth Scenario
		lsMsg = DocumentLapsingUtility.rollbackDueDateOnRejection(
				P8Constants.PROPERTY_CE_CHAR500_EXT2_DOC_TYPE,
				aoDCurrentDueDate, asPeriodCoveredStartMonth,
				aiPeriodCoveredStartYear, asPeriodCoveredEndMonth,
				aiPeriodCoveredEndYear,
				P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE,
				P8Constants.PROPERTY_PE_LAW_TYPE, abIsShortFiling,
				abafterShortFiling);
		assertTrue(!lsMsg.isEmpty());
		

		// Fourthteenth Scenario
		lsMsg = DocumentLapsingUtility.rollbackDueDateOnRejection(
				P8Constants.PROPERTY_CE_CHAR500_EXT2_DOC_TYPE,
				aoDCurrentDueDate, asPeriodCoveredStartMonth,
				aiPeriodCoveredStartYear, asPeriodCoveredEndMonth,
				aiPeriodCoveredEndYear,
				P8Constants.PROPERTY_CE_CHAR500_EXT1_DOC_TYPE,
				P8Constants.PROPERTY_PE_LAW_TYPE, abIsShortFiling,
				abafterShortFiling);
		assertTrue(!lsMsg.isEmpty());
		

		// Fifteenth Scenario
		lsMsg = DocumentLapsingUtility.rollbackDueDateOnRejection(
				P8Constants.PROPERTY_CE_CHAR500_EXT2_DOC_TYPE,
				aoDCurrentDueDate, asPeriodCoveredStartMonth,
				aiPeriodCoveredStartYear, asPeriodCoveredEndMonth,
				aiPeriodCoveredEndYear,
				P8Constants.PROPERTY_CE_CHAR500_EXT2_DOC_TYPE,
				P8Constants.PROPERTY_PE_LAW_TYPE, abIsShortFiling,
				abafterShortFiling);
		assertTrue(!lsMsg.isEmpty());
		

		// SixTeenth Scenario
		lsMsg = DocumentLapsingUtility.rollbackDueDateOnRejection(
				P8Constants.PROPERTY_CE_CHAR500_EXT2_DOC_TYPE,
				aoDCurrentDueDate, asPeriodCoveredStartMonth,
				aiPeriodCoveredStartYear, asPeriodCoveredEndMonth,
				aiPeriodCoveredEndYear,
				P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE, null,
				abIsShortFiling, abafterShortFiling);
		assertTrue(!lsMsg.isEmpty());
		

		// Seventeenth Scenario
		lsMsg = DocumentLapsingUtility.rollbackDueDateOnRejection(
				P8Constants.PROPERTY_CE_CHAR500_EXT2_DOC_TYPE,
				aoDCurrentDueDate, asPeriodCoveredStartMonth,
				aiPeriodCoveredStartYear, asPeriodCoveredEndMonth,
				aiPeriodCoveredEndYear,
				P8Constants.PROPERTY_CE_CHAR500_EXT1_DOC_TYPE, null,
				abIsShortFiling, abafterShortFiling);
		assertTrue(!lsMsg.isEmpty());
		

		// Eighteenth Scenario
		lsMsg = DocumentLapsingUtility.rollbackDueDateOnRejection(
				P8Constants.PROPERTY_CE_CHAR500_EXT2_DOC_TYPE,
				aoDCurrentDueDate, asPeriodCoveredStartMonth,
				aiPeriodCoveredStartYear, asPeriodCoveredEndMonth,
				aiPeriodCoveredEndYear,
				P8Constants.PROPERTY_CE_CHAR500_EXT2_DOC_TYPE, null,
				abIsShortFiling, abafterShortFiling);
		assertTrue(!lsMsg.isEmpty());
		
		abIsShortFiling = true;
		// Nineteenth Scenario
		lsMsg = DocumentLapsingUtility.rollbackDueDateOnRejection(
				P8Constants.PROPERTY_CE_CHAR500_EXT2_DOC_TYPE,
				aoDCurrentDueDate, asPeriodCoveredStartMonth,
				aiPeriodCoveredStartYear, asPeriodCoveredEndMonth,
				aiPeriodCoveredEndYear,
				P8Constants.PROPERTY_CE_CHAR500_EXT2_DOC_TYPE, null,
				abIsShortFiling, abafterShortFiling);
		assertTrue(!lsMsg.isEmpty());
		

	}

	/**
	 * This method tests CalculateDueDateonDocumentUpload method for data inputs
	 * and an ApplicationException is expected
	 * 
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testCalculateDueDateonDocumentUpload()
			throws ApplicationException {
		Map lsMsg = null;
		String asPeriodCoveredStartMonth = "jul";
		int aiPeriodCoveredStartYear = 1;
		String asNewPeriodCoveredEndMonth = "jun";
		int aiNewPeriodCoveredEndYear = 1;
		String asApplicableLaw = P8Constants.PROPERTY_PE_LAW_TYPE;
		boolean abafterShortFiling = false;

		// First Scenario
		lsMsg = DocumentLapsingUtility.calculateDueDateonDocumentUpload(
				P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE,
				asPeriodCoveredStartMonth, aiPeriodCoveredStartYear,
				asNewPeriodCoveredEndMonth, aiNewPeriodCoveredEndYear,
				asApplicableLaw, abafterShortFiling);
		assertTrue(!lsMsg.isEmpty());

		// Second Scenario
		lsMsg = DocumentLapsingUtility.calculateDueDateonDocumentUpload(
				P8Constants.PROPERTY_CE_CHAR500_EXT1_DOC_TYPE,
				asPeriodCoveredStartMonth, aiPeriodCoveredStartYear,
				asNewPeriodCoveredEndMonth, aiNewPeriodCoveredEndYear,
				asApplicableLaw, abafterShortFiling);
		assertTrue(!lsMsg.isEmpty());

		// Third Scenario
		lsMsg = DocumentLapsingUtility.calculateDueDateonDocumentUpload(
				P8Constants.PROPERTY_CE_CHAR500_EXT2_DOC_TYPE,
				asPeriodCoveredStartMonth, aiPeriodCoveredStartYear,
				asNewPeriodCoveredEndMonth, aiNewPeriodCoveredEndYear,
				asApplicableLaw, abafterShortFiling);
		assertTrue(!lsMsg.isEmpty());

		// Fourth Scenario
		lsMsg = DocumentLapsingUtility.calculateDueDateonDocumentUpload(
				P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE,
				asPeriodCoveredStartMonth, aiPeriodCoveredStartYear,
				asNewPeriodCoveredEndMonth, aiNewPeriodCoveredEndYear,
				asApplicableLaw, abafterShortFiling);
		assertTrue(!lsMsg.isEmpty());

		// Sixth Scenario
		lsMsg = DocumentLapsingUtility.calculateDueDateonDocumentUpload(
				P8Constants.PROPERTY_CE_CHAR500_EXT1_DOC_TYPE,
				asPeriodCoveredStartMonth, aiPeriodCoveredStartYear,
				asNewPeriodCoveredEndMonth, aiNewPeriodCoveredEndYear,
				asApplicableLaw, abafterShortFiling);
		assertTrue(!lsMsg.isEmpty());

		// Seventh Scenario
		lsMsg = DocumentLapsingUtility.calculateDueDateonDocumentUpload(
				P8Constants.PROPERTY_CE_CHAR500_EXT2_DOC_TYPE,
				asPeriodCoveredStartMonth, aiPeriodCoveredStartYear,
				asNewPeriodCoveredEndMonth, aiNewPeriodCoveredEndYear,
				asApplicableLaw, abafterShortFiling);
		assertTrue(!lsMsg.isEmpty());

		asApplicableLaw = null;

		// Eighth Scenario
		lsMsg = DocumentLapsingUtility.calculateDueDateonDocumentUpload(
				P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE,
				asPeriodCoveredStartMonth, aiPeriodCoveredStartYear,
				asNewPeriodCoveredEndMonth, aiNewPeriodCoveredEndYear,
				asApplicableLaw, abafterShortFiling);
		assertTrue(!lsMsg.isEmpty());

		// Ninth Scenario
		lsMsg = DocumentLapsingUtility.calculateDueDateonDocumentUpload(
				P8Constants.PROPERTY_CE_CHAR500_EXT1_DOC_TYPE,
				asPeriodCoveredStartMonth, aiPeriodCoveredStartYear,
				asNewPeriodCoveredEndMonth, aiNewPeriodCoveredEndYear,
				asApplicableLaw, abafterShortFiling);
		assertTrue(!lsMsg.isEmpty());

		// eleventh Scenario
		lsMsg = DocumentLapsingUtility.calculateDueDateonDocumentUpload(
				P8Constants.PROPERTY_CE_CHAR500_EXT2_DOC_TYPE,
				asPeriodCoveredStartMonth, aiPeriodCoveredStartYear,
				asNewPeriodCoveredEndMonth, aiNewPeriodCoveredEndYear,
				asApplicableLaw, abafterShortFiling);
		assertTrue(!lsMsg.isEmpty());

		abafterShortFiling = true;
		asApplicableLaw = P8Constants.PROPERTY_PE_LAW_TYPE;
		// Twelth Scenario
		lsMsg = DocumentLapsingUtility.calculateDueDateonDocumentUpload(
				P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE,
				asPeriodCoveredStartMonth, aiPeriodCoveredStartYear,
				asNewPeriodCoveredEndMonth, aiNewPeriodCoveredEndYear,
				asApplicableLaw, abafterShortFiling);
		assertTrue(!lsMsg.isEmpty());

		// Thirteenth Scenario
		lsMsg = DocumentLapsingUtility.calculateDueDateonDocumentUpload(
				P8Constants.PROPERTY_CE_CHAR500_EXT1_DOC_TYPE,
				asPeriodCoveredStartMonth, aiPeriodCoveredStartYear,
				asNewPeriodCoveredEndMonth, aiNewPeriodCoveredEndYear,
				asApplicableLaw, abafterShortFiling);
		assertTrue(!lsMsg.isEmpty());

		// Fourteenth Scenario
		lsMsg = DocumentLapsingUtility.calculateDueDateonDocumentUpload(
				P8Constants.PROPERTY_CE_CHAR500_EXT2_DOC_TYPE,
				asPeriodCoveredStartMonth, aiPeriodCoveredStartYear,
				asNewPeriodCoveredEndMonth, aiNewPeriodCoveredEndYear,
				asApplicableLaw, abafterShortFiling);
		assertTrue(!lsMsg.isEmpty());

		// Fiveteenth Scenario
		lsMsg = DocumentLapsingUtility.calculateDueDateonDocumentUpload(
				P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE,
				asPeriodCoveredStartMonth, aiPeriodCoveredStartYear,
				asNewPeriodCoveredEndMonth, aiNewPeriodCoveredEndYear,
				asApplicableLaw, abafterShortFiling);
		assertTrue(!lsMsg.isEmpty());

		// Sixteenth Scenario
		lsMsg = DocumentLapsingUtility.calculateDueDateonDocumentUpload(
				P8Constants.PROPERTY_CE_CHAR500_EXT1_DOC_TYPE,
				asPeriodCoveredStartMonth, aiPeriodCoveredStartYear,
				asNewPeriodCoveredEndMonth, aiNewPeriodCoveredEndYear,
				asApplicableLaw, abafterShortFiling);
		assertTrue(!lsMsg.isEmpty());

		// Seventeenth Scenario
		lsMsg = DocumentLapsingUtility.calculateDueDateonDocumentUpload(
				P8Constants.PROPERTY_CE_CHAR500_EXT2_DOC_TYPE,
				asPeriodCoveredStartMonth, aiPeriodCoveredStartYear,
				asNewPeriodCoveredEndMonth, aiNewPeriodCoveredEndYear,
				asApplicableLaw, abafterShortFiling);
		assertTrue(!lsMsg.isEmpty());

		asApplicableLaw = null;

		// Eighteenth Scenario
		lsMsg = DocumentLapsingUtility.calculateDueDateonDocumentUpload(
				P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE,
				asPeriodCoveredStartMonth, aiPeriodCoveredStartYear,
				asNewPeriodCoveredEndMonth, aiNewPeriodCoveredEndYear,
				asApplicableLaw, abafterShortFiling);
		assertTrue(!lsMsg.isEmpty());

		// Ninteenth Scenario
		lsMsg = DocumentLapsingUtility.calculateDueDateonDocumentUpload(
				P8Constants.PROPERTY_CE_CHAR500_EXT1_DOC_TYPE,
				asPeriodCoveredStartMonth, aiPeriodCoveredStartYear,
				asNewPeriodCoveredEndMonth, aiNewPeriodCoveredEndYear,
				asApplicableLaw, abafterShortFiling);
		assertTrue(!lsMsg.isEmpty());

		// Twentyth Scenario
		lsMsg = DocumentLapsingUtility.calculateDueDateonDocumentUpload(
				P8Constants.PROPERTY_CE_CHAR500_EXT2_DOC_TYPE,
				asPeriodCoveredStartMonth, aiPeriodCoveredStartYear,
				asNewPeriodCoveredEndMonth, aiNewPeriodCoveredEndYear,
				asApplicableLaw, abafterShortFiling);
		assertTrue(!lsMsg.isEmpty());

	}

	/**
	 * This method tests CalculateDueDateOnFiscalYearChange method for data
	 * inputs and an ApplicationException is expected
	 * 
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testCalculateDueDateOnFiscalYearChange()
			throws ApplicationException {
		HashMap loDuedateDetailsMap = new HashMap();
		int aiChangedPeriodEffectiveYear = 1;
		String asCurrentPeriodCoveredEndMonth = "jun";
		String asCurrentPeriodCoveredStartMonth = "jul";
		String asApplicableLaw = P8Constants.PROPERTY_PE_LAW_TYPE;
		String asNewPeriodCoveredEndMonth = "dec";
		String asNewPeriodCoveredStartMonth = "jan";

		// First Scenario
		loDuedateDetailsMap = DocumentLapsingUtility
				.calculateDueDateOnFiscalYearChange(
						asCurrentPeriodCoveredStartMonth,
						asCurrentPeriodCoveredEndMonth,
						asNewPeriodCoveredStartMonth,
						asNewPeriodCoveredEndMonth,
						aiChangedPeriodEffectiveYear, asApplicableLaw);
		assertTrue(!loDuedateDetailsMap.isEmpty());

		asApplicableLaw = P8Constants.PROPERTY_PE_AFTER_SHORT_FILING;

		// Second Scenario
		loDuedateDetailsMap = DocumentLapsingUtility
				.calculateDueDateOnFiscalYearChange(
						asCurrentPeriodCoveredStartMonth,
						asCurrentPeriodCoveredEndMonth,
						asNewPeriodCoveredStartMonth,
						asNewPeriodCoveredEndMonth,
						aiChangedPeriodEffectiveYear, asApplicableLaw);
		assertTrue(!loDuedateDetailsMap.isEmpty());

	}

	/**
	 * This method tests GetShortFilingDetails method for data inputs and an
	 * ApplicationException is expected
	 * 
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testGetShortFilingDetails() throws ApplicationException {
		int aiChangedPeriodEffectiveYear = 1;
		HashMap loShortFilingMap = new HashMap();
		// First Scenario
		loShortFilingMap = DocumentLapsingUtility.getShortFilingDetails("jan",
				"dec", "mar", "feb", aiChangedPeriodEffectiveYear);
		assertTrue(!loShortFilingMap.isEmpty());

		// Second Scenario
		loShortFilingMap = DocumentLapsingUtility.getShortFilingDetails("feb",
				"jan", "mar", "feb", aiChangedPeriodEffectiveYear);
		assertTrue(!loShortFilingMap.isEmpty());
	}

	/**
	 * This method tests getNextFiscalYearStartDateOnRejection method for data
	 * inputs and an ApplicationException is expected
	 * 
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 */
	@Test
	public void testGetNextFiscalYearStartDateOnRejection()
			throws ApplicationException {
		Calendar localendar = new GregorianCalendar(2013, 01, 01);
		Date lodate = DocumentLapsingUtility
				.getNextFiscalYearStartDateOnRejection(localendar);
		assertTrue(lodate != null);
	}

	/**
	 * This method tests getNextFiscalYearStartDateOnApproval method for data
	 * inputs and an ApplicationException is expected
	 * 
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 */
	@Test
	public void testGetNextFiscalYearStartDateOnApproval()
			throws ApplicationException {
		Calendar localendar = new GregorianCalendar(2013, 01, 01);
		Date lodate = DocumentLapsingUtility
				.getNextFiscalYearStartDateOnApproval(localendar);
		assertTrue(lodate != null);
	}

	/**
	 * This method tests getNextFiscalYearStartEndDateOnApprovalShortFilling
	 * method for data inputs and an ApplicationException is expected
	 * 
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 */
	@Test
	public void testGetNextFiscalYearStartEndDateOnApprovalShortFilling()
			throws ApplicationException {
		Calendar localendar = new GregorianCalendar(2013, 01, 01);
		Date lodate = DocumentLapsingUtility
				.getNextFiscalYearStartEndDateOnApprovalShortFilling(localendar);
		assertTrue(lodate != null);
	}
	
	
	/**
	 * This method tests getNumberofMonthsForExecutiveLawShort
	 * method for data inputs and an ApplicationException is expected
	 * 
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 */
	@Test
	public void testGetNumberofMonthsForExecutiveLawShort()
			throws ApplicationException {
		// First Scenario
		int liReturnMonths = DocumentLapsingUtility
				.getNumberofMonthsForExecutiveLawShort(
						P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE, new StringBuilder());
		assertTrue(liReturnMonths > 0);
		// Second Scenario
		liReturnMonths = DocumentLapsingUtility
				.getNumberofMonthsForExecutiveLawShort(
						P8Constants.PROPERTY_CE_CHAR500_EXT1_DOC_TYPE, new StringBuilder());
		assertTrue(liReturnMonths > 0);
		// Third Scenario
		liReturnMonths = DocumentLapsingUtility
				.getNumberofMonthsForExecutiveLawShort(
						P8Constants.PROPERTY_CE_CHAR500_EXT2_DOC_TYPE, new StringBuilder());
		assertTrue(liReturnMonths > 0);
	}
	
	/**
	 * This method tests getNumberofMonthsForPTLawShort
	 * method for data inputs and an ApplicationException is expected
	 * 
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 */
	@Test
	public void testGetNumberofMonthsForPTLawShort()
			throws ApplicationException {
		// First Scenario
		int liReturnMonths = DocumentLapsingUtility
				.getNumberofMonthsForPTLawShort(
						P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE);
		assertTrue(liReturnMonths > 0);
		// Second Scenario
		liReturnMonths = DocumentLapsingUtility
				.getNumberofMonthsForPTLawShort(
						P8Constants.PROPERTY_CE_CHAR500_EXT1_DOC_TYPE);
		assertTrue(liReturnMonths > 0);
		// Third Scenario
		liReturnMonths = DocumentLapsingUtility
				.getNumberofMonthsForPTLawShort(
						P8Constants.PROPERTY_CE_CHAR500_EXT2_DOC_TYPE);
		assertTrue(liReturnMonths > 0);
	}
	
	/**
	 * This method tests getCalendar
	 * method for data inputs and an ApplicationException is expected
	 * 
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testGetCalendar()
			throws ApplicationException {
		Calendar loCal = DocumentLapsingUtility
				.getCalendar(new Date("01/01/2014"));
		assertTrue(loCal != null);
	}

}
