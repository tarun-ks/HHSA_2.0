package com.nyc.hhs.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;

/**
 * This utility class is used to calculate the due date and next fiscal start
 * and end month for different scenarios like task approval,task rejection and
 * document upload for CHAR500,CHAR500-Ext1,CHAR500-Ext2 document types
 * 
 */

public class DocumentLapsingUtility
{
	/**
	 * Modified as per 3.1.0 . Enhancement 6021 - Added checks for char 500 extension
	 * This method calculates and revert back the due date when a task
	 * containing any char 500 type document is rejected
	 * <ul>
	 * <li> Set Next End Fiscal Year Calendar</li>
	 * <li> Execute getRollbackDoctype Method </li>
	 * <li> Fill Due date Details Map</li>
	 * <li> Method Updated in R4 </li>
	 * </ul>
	 * @param asLastUploadedDocumentType
	 *            last uploaded document type
	 * @param aoDCurrentDueDate
	 *            current due date
	 * @param asPeriodCoveredStartMonth
	 *            current financial period start month
	 * @param aiPeriodCoveredStartYear
	 *            current financial period start year
	 * @param asPeriodCoveredEndMonth
	 *            current financial period end month
	 * @param aiPeriodCoveredEndYear
	 *            current financial period end year
	 * @param asSecondLastUploadedDocType
	 *            document type uploaded before
	 * @param asApplicableLaw
	 *            applicable law for the organization
	 * @param abIsShortFiling
	 *            represent whether it is a short filing or not
	 * @param abafterShortFiling
	 *            represent whether it is after short filing
	 * @return loDuedateDetailsMap a map containing all the data
	 * @throws ApplicationException
	 */
	
	public static Map rollbackDueDateOnRejection(String asLastUploadedDocumentType, Date aoDCurrentDueDate, String asPeriodCoveredStartMonth,
			int aiPeriodCoveredStartYear, String asPeriodCoveredEndMonth, int aiPeriodCoveredEndYear, String asSecondLastUploadedDocType,
			String asApplicableLaw, boolean abIsShortFiling, boolean abafterShortFiling) throws ApplicationException
	{
		Date loDueDate = null;
		Date loDStartNextFiscalYear = null;
		Date loDEndNextFiscalYear = null;
		String lsNextExpectedDocType = null;
		Map loDuedateDetailsMap = new HashMap();
		Calendar loCurrentDueDateCalendar = new GregorianCalendar();
		loCurrentDueDateCalendar.setTime(aoDCurrentDueDate);
		
		Calendar loNextEndFiscalYearCalendar = new GregorianCalendar();
		loNextEndFiscalYearCalendar.set(aiPeriodCoveredEndYear, getMonth(asPeriodCoveredEndMonth), 1);
		loNextEndFiscalYearCalendar.set(Calendar.DATE,loNextEndFiscalYearCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		if (!abIsShortFiling)
		{
			if (P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE.equalsIgnoreCase(asLastUploadedDocumentType))
			{
				rollbackDueDateOnRejectionForChar500(aiPeriodCoveredEndYear, asSecondLastUploadedDocType, asApplicableLaw,
						loCurrentDueDateCalendar, loNextEndFiscalYearCalendar);
			}
			else if (P8Constants.PROPERTY_CE_CHAR500_EXT1_DOC_TYPE.equalsIgnoreCase(asLastUploadedDocumentType))
			{
				 rollbackDueDateOnRejectionForChar500Ext1(aiPeriodCoveredEndYear, asSecondLastUploadedDocType, asApplicableLaw,
						loCurrentDueDateCalendar);
			}
			else if (P8Constants.PROPERTY_CE_CHAR500_EXT2_DOC_TYPE.equalsIgnoreCase(asLastUploadedDocumentType))
			{
				 rollbackDueDateOnRejectionForChar500Ext2(aiPeriodCoveredEndYear, asSecondLastUploadedDocType, asApplicableLaw,
						loCurrentDueDateCalendar);
			}
			//Modified as per 3.1.0 . Enhancement 6021 - Added checks for char 500 - extension
			else if (P8Constants.PROPERTY_CE_CHAR500_EXTENSION.equalsIgnoreCase(asLastUploadedDocumentType))
			{
				rollbackDueDateOnRejectionForChar500Ext(aiPeriodCoveredEndYear, asSecondLastUploadedDocType, asApplicableLaw,
						loCurrentDueDateCalendar);
			}
			else
			{
				throw new ApplicationException("Unknown Document type: " + asLastUploadedDocumentType);
			}
			loDEndNextFiscalYear = loNextEndFiscalYearCalendar.getTime();
			loDStartNextFiscalYear = getNextFiscalYearStartDateOnRejection(loNextEndFiscalYearCalendar);
			loDueDate = loCurrentDueDateCalendar.getTime();
			lsNextExpectedDocType =  getRollbackDoctype(asSecondLastUploadedDocType);
		}
		else if (abIsShortFiling)
		{

			loDueDate = aoDCurrentDueDate;
			loDStartNextFiscalYear = getCalendar(asPeriodCoveredStartMonth, aiPeriodCoveredStartYear).getTime();
			loDEndNextFiscalYear = getCalendar(asPeriodCoveredEndMonth, aiPeriodCoveredEndYear).getTime();
			lsNextExpectedDocType = P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE;
		}
		else if (abafterShortFiling)
		{
			loDueDate = aoDCurrentDueDate;
			loDStartNextFiscalYear = getCalendar(asPeriodCoveredStartMonth, aiPeriodCoveredStartYear).getTime();
			loDEndNextFiscalYear = getCalendar(asPeriodCoveredEndMonth, aiPeriodCoveredEndYear).getTime();
			lsNextExpectedDocType = P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE;
		}
		java.sql.Date loDate = null;
		if(loDueDate != null)
		{
			loDate = new java.sql.Date(loDueDate.getTime());
		}
		loDuedateDetailsMap.put(P8Constants.PROPERTY_PE_CURRENT_DUE_DATE, loDate);
		Calendar loCal = new GregorianCalendar();
		if(null != loDStartNextFiscalYear)
		{
			loCal.setTime(loDStartNextFiscalYear);
		}
		loDuedateDetailsMap.put(P8Constants.PROPERTY_PE_NEXT_PERIOD_COVERED_START_YEAR, loCal.get(Calendar.YEAR));
		loDuedateDetailsMap.put(P8Constants.PROPERTY_PE_NEXT_PERIOD_COVERED_START_MONTH, getMonth(loCal.get(Calendar.MONTH)));
		if(null != loDEndNextFiscalYear)
		{
			loCal.setTime(loDEndNextFiscalYear);
		}
		loDuedateDetailsMap.put(P8Constants.PROPERTY_PE_NEXT_PERIOD_COVERED_END_YEAR, loCal.get(Calendar.YEAR));
		loDuedateDetailsMap.put(P8Constants.PROPERTY_PE_NEXT_PERIOD_COVERED_END_MONTH, getMonth(loCal.get(Calendar.MONTH)));
		loDuedateDetailsMap.put(P8Constants.PROPERTY_PE_NEXT_EXPECTED_DOC_TYPE, lsNextExpectedDocType);
		return loDuedateDetailsMap;
	}

	/**
	 * This method calculates and roll back the due date for char500 Extn 2 type
	 * document when a task is rejected
	 * <ul>
	 * <li>Set Calendar object </li>
	 * <li> Method Updated in R4 </li>
	 * </ul>
	 * @param aiPeriodCoveredEndYear
	 *            current financial period end year
	 * @param asSecondLastUploadedDocType
	 *            last uploaded document type
	 * @param asApplicableLaw
	 *            applicable law for organization
	 * @param aoCalendar
	 *            calendar object
	 * @return liFiscalYearEnd new financial period end year
	 * @throws ApplicationException
	 */
	private static int rollbackDueDateOnRejectionForChar500Ext2(int aiPeriodCoveredEndYear, String asSecondLastUploadedDocType,
			String asApplicableLaw, Calendar aoCalendar) throws ApplicationException
	{
		int liFiscalYearEnd;
		if (P8Constants.PROPERTY_PE_LAW_TYPE.equalsIgnoreCase(asApplicableLaw))
		{
			if (P8Constants.PROPERTY_CE_CHAR500_EXT1_DOC_TYPE.equalsIgnoreCase(asSecondLastUploadedDocType))
			{
				addMonths(aoCalendar, -3, 0);
				aoCalendar.set(Calendar.DAY_OF_MONTH, aoCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			}
		}
		else
		{
			if (P8Constants.PROPERTY_CE_CHAR500_EXT1_DOC_TYPE.equalsIgnoreCase(asSecondLastUploadedDocType))
			{
				addMonths(aoCalendar, -3, 0);
				aoCalendar.set(Calendar.DAY_OF_MONTH, 15);
			}
		}
		liFiscalYearEnd = aiPeriodCoveredEndYear;
		return liFiscalYearEnd;
	}

	/**
	 * This method calculates and roll back the due date for char500 Extn 1 type
	 * document when a task is rejected
	 * 
	 * @param aiPeriodCoveredEndYear
	 *            current financial period end year
	 * @param asSecondLastUploadedDocType
	 *            last uploaded document type
	 * @param asApplicableLaw
	 *            applicable law for organization
	 * @param aoCalendar
	 *            calendar object
	 * @return liFiscalYearEnd new financial period end year
	 * @throws ApplicationException
	 */
	private static int rollbackDueDateOnRejectionForChar500Ext1(int aiPeriodCoveredEndYear, String asSecondLastUploadedDocType,
			String asApplicableLaw, Calendar aoCalendar) throws ApplicationException
	{
		int liFiscalYearEnd;
		if (P8Constants.PROPERTY_PE_LAW_TYPE.equalsIgnoreCase(asApplicableLaw))
		{
			if (P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE.equalsIgnoreCase(asSecondLastUploadedDocType))
			{
				addMonths(aoCalendar, -3, 0);
				aoCalendar.set(Calendar.DAY_OF_MONTH, aoCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			}
		}
		else
		{
			if (P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE.equalsIgnoreCase(asSecondLastUploadedDocType))
			{
				addMonths(aoCalendar, -3, 0);
				aoCalendar.set(Calendar.DAY_OF_MONTH, 15);
			}
		}
		liFiscalYearEnd = aiPeriodCoveredEndYear;
		return liFiscalYearEnd;
	}
	
	/**
	 * Created as per 3.1.0 . Enhancement 6021 - Added checks for char 500 extension
	 * This method calculates and roll back the due date for char500 Extn  type
	 * document when a task is rejected
	 * 
	 * @param aiPeriodCoveredEndYear
	 *            current financial period end year
	 * @param asSecondLastUploadedDocType
	 *            last uploaded document type
	 * @param asApplicableLaw
	 *            applicable law for organization
	 * @param aoCalendar
	 *            calendar object
	 * @return liFiscalYearEnd new financial period end year
	 * @throws ApplicationException
	 */
	private static int rollbackDueDateOnRejectionForChar500Ext(int aiPeriodCoveredEndYear, String asSecondLastUploadedDocType,
			String asApplicableLaw, Calendar aoCalendar) throws ApplicationException
	{
		int liFiscalYearEnd;
		if (P8Constants.PROPERTY_PE_LAW_TYPE.equalsIgnoreCase(asApplicableLaw))
		{
			addMonths(aoCalendar, -6, 0);
			aoCalendar.set(Calendar.DAY_OF_MONTH, aoCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		}
		else
		{
			addMonths(aoCalendar, -6, 0);
			aoCalendar.set(Calendar.DAY_OF_MONTH, 15);
		}
		liFiscalYearEnd = aiPeriodCoveredEndYear;
		return liFiscalYearEnd;
	}

	/**
	 * Modified as per 3.1.0 . Enhancement 6021 - Added checks for char 500 extension
	 * Modified by - Tanuj
	 * This Method has been changed in release 2.6.1 for defect 5735
	 * This change will set the date to maximum of month
	 * 
	 * <ul>
	 * <li>1.This method roll back due date when a filling task is being rejected </li>
	 * <li>2. it will calculate and revert back the due date for the document uploaded based on different document doc types</li>
	 * </ul>
	 * it
	 * will calculate and revert back the due date for the document uploaded
	 * 
	 * @param aiPeriodCoveredEndYear
	 *            current financial period end year
	 * @param asSecondLastUploadedDocType
	 *            last uploaded document type
	 * @param asApplicableLaw
	 *            applicable law for organization
	 * @param aoCalendar
	 *            calendar object
	 * @param aiFiscalYearEnd
	 *            current financial period end year
	 * @return Calendar
	 * @throws ApplicationException
	 */
	private static Calendar rollbackDueDateOnRejectionForChar500(int aiPeriodCoveredEndYear, String asSecondLastUploadedDocType, String asApplicableLaw,
			Calendar aoCalendar, Calendar aoNextEndFiscalYearCalendar)throws ApplicationException
	{
		if (P8Constants.PROPERTY_PE_LAW_TYPE.equalsIgnoreCase(asApplicableLaw))
		{
			if (P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE.equalsIgnoreCase(asSecondLastUploadedDocType))
			{
				addMonths(aoCalendar, -12, 0);
				aoCalendar.set(Calendar.DAY_OF_MONTH, aoCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
				
			}
			else if (P8Constants.PROPERTY_CE_CHAR500_EXT1_DOC_TYPE.equalsIgnoreCase(asSecondLastUploadedDocType))
			{
				addMonths(aoCalendar, -9, 0);
				aoCalendar.set(Calendar.DAY_OF_MONTH, aoCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
				
			}
			else if (P8Constants.PROPERTY_CE_CHAR500_EXT2_DOC_TYPE.equalsIgnoreCase(asSecondLastUploadedDocType))
			{
				addMonths(aoCalendar, -6, 0);
				aoCalendar.set(Calendar.DAY_OF_MONTH, aoCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			}
			//Modified as per 3.1.0 . Enhancement 6021 - Added checks for char 500 - extension
			else if (P8Constants.PROPERTY_CE_CHAR500_EXTENSION.equalsIgnoreCase(asSecondLastUploadedDocType))
			{
				addMonths(aoCalendar, -6, 0);
				aoCalendar.set(Calendar.DAY_OF_MONTH, aoCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			}
		}
		else
		{
			if (P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE.equalsIgnoreCase(asSecondLastUploadedDocType))
			{
				addMonths(aoCalendar, -12, 0);
				aoCalendar.set(Calendar.DAY_OF_MONTH, 15);
			}
			else if (P8Constants.PROPERTY_CE_CHAR500_EXT1_DOC_TYPE.equalsIgnoreCase(asSecondLastUploadedDocType))
			{
				addMonths(aoCalendar, -9, 0);
				aoCalendar.set(Calendar.DAY_OF_MONTH, 15);
			}
			else if (P8Constants.PROPERTY_CE_CHAR500_EXT2_DOC_TYPE.equalsIgnoreCase(asSecondLastUploadedDocType))
			{
				addMonths(aoCalendar, -6, 0);
				aoCalendar.set(Calendar.DAY_OF_MONTH, 15);
			}
			//Modified as per 3.1.0 . Enhancement 6021 - Added checks for char 500 - extension
			else if (P8Constants.PROPERTY_CE_CHAR500_EXTENSION.equalsIgnoreCase(asSecondLastUploadedDocType))
			{
				addMonths(aoCalendar, -6, 0);
				aoCalendar.set(Calendar.DAY_OF_MONTH, 15);
			}
		}
		aoNextEndFiscalYearCalendar.add(Calendar.YEAR, -1);
		
		//2.6.1 changes start for defect 5735
		aoNextEndFiscalYearCalendar.set(Calendar.DATE, aoNextEndFiscalYearCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		//2.6.1 changes end for defect 5735
		return aoNextEndFiscalYearCalendar;
	}

	/**
	 * Modified as per 3.1.0 . Enhancement 6021 - Added checks for char 500 extension
	 * this method Calculates the due date on changing of the financial period
	 * 
	 * @param asCurrentPeriodCoveredStartMonth
	 *            current financial period start month
	 * @param asCurrentPeriodCoveredEndMonth
	 *            current financial period end month
	 * @param asNewPeriodCoveredStartMonth
	 *            new financial period start month
	 * @param asNewPeriodCoveredEndMonth
	 *            new financial period end month
	 * @param aiChangedPeriodEffectiveYear
	 *            new financial period effective from year
	 * @param asApplicableLaw
	 *            applicable law for the organization
	 * @return HashMap
	 * @throws ApplicationException
	 */
	public static HashMap calculateDueDateOnFiscalYearChange(String asCurrentPeriodCoveredStartMonth, String asCurrentPeriodCoveredEndMonth,
			String asNewPeriodCoveredStartMonth, String asNewPeriodCoveredEndMonth, int aiChangedPeriodEffectiveYear, String asApplicableLaw)
			throws ApplicationException
	{
		int liMonths = 0;
		int liDays = 0;
		Date loDueDate = null;
		if (P8Constants.PROPERTY_PE_LAW_TYPE.equalsIgnoreCase(asApplicableLaw))
		{
			liMonths = getNumberofMonthsForPTLaw(P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE);
			liDays = 0;
		}
		else
		{
			StringBuilder loSBDays = new StringBuilder();
			liMonths = getNumberofMonthsForExecutiveLaw(P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE, loSBDays);
			liDays = Integer.parseInt(loSBDays.toString());
		}
		HashMap loDuedateDetailsMap = new HashMap();

		loDueDate = addMonths(getCalendar(asNewPeriodCoveredStartMonth, aiChangedPeriodEffectiveYear), liMonths - 1, liDays);
		if (!P8Constants.PROPERTY_PE_LAW_TYPE.equalsIgnoreCase(asApplicableLaw))
		{
			GregorianCalendar loCalender = new GregorianCalendar();
			loCalender.setTime(loDueDate);
			loCalender.set(Calendar.DAY_OF_MONTH, 15);
			loDueDate = loCalender.getTime();
		}
		else
		{
			GregorianCalendar loCalender = new GregorianCalendar();
			loCalender.setTime(loDueDate);
			loCalender.set(Calendar.DAY_OF_MONTH, loCalender.getActualMaximum(Calendar.DAY_OF_MONTH));
			loDueDate = loCalender.getTime();
		}
		
		Calendar loCal = new GregorianCalendar();
		loCal.set(aiChangedPeriodEffectiveYear, getMonth(asNewPeriodCoveredStartMonth), 1);
		Date loDStartNextFiscalYear = loCal.getTime();
		
		loCal.add(Calendar.YEAR, 1);
		loCal.add(Calendar.DATE, -1);
		Date loDEndNextFiscalYear = loCal.getTime();
		
		java.sql.Date loDate = new java.sql.Date(loDueDate.getTime());
		loDuedateDetailsMap.put("DUE_DATE", loDate);
		loCal = new GregorianCalendar();
		loCal.setTime(loDStartNextFiscalYear);
		loDuedateDetailsMap.put("START_YEAR", loCal.get(Calendar.YEAR));
		loDuedateDetailsMap.put("START_MONTH", getMonth(loCal.get(Calendar.MONTH)));
		loCal.setTime(loDEndNextFiscalYear);
		loDuedateDetailsMap.put("END_YEAR", loCal.get(Calendar.YEAR));
		loDuedateDetailsMap.put("END_MONTH", getMonth(loCal.get(Calendar.MONTH)));

		//Modified as per 3.1.0 . Enhancement 6021 - Updated for char 500 - extension
		loDuedateDetailsMap.put("DOCUMENT_TYPE", P8Constants.PROPERTY_CE_CHAR500_EXTENSION + HHSConstants.HHSUTIL_DELIM_PIPE + P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE);
		HashMap loShortFiling = getShortFilingDetails(asCurrentPeriodCoveredStartMonth, asCurrentPeriodCoveredEndMonth, asNewPeriodCoveredStartMonth,
				asNewPeriodCoveredEndMonth, aiChangedPeriodEffectiveYear);
		HashMap loHashMap = new HashMap();
		loHashMap.put("loDuedateDetailsMap", loDuedateDetailsMap);
		loHashMap.put("loShortFiling", loShortFiling);

		return loHashMap;

	}

	/**
	 * This method is used to calculate the due date for document expiration
	 * when a Char 500 type document is uploaded
	 * <ul>
	 * <li> Method Updated in R4 </li>
	 * </ul>
	 * @param asNewUploadedDocType
	 *            document Type user is trying to upload
	 * @param asPeriodCoveredStartMonth
	 *            Current financial year start month
	 * @param aiPeriodCoveredStartYear
	 *            current financial year start year
	 * @param asNewPeriodCoveredEndMonth
	 *            Current financial year End month
	 * @param aiNewPeriodCoveredEndYear
	 *            Current financial year End year
	 * @param asApplicableLaw
	 *            Applicable law for that char500 type document
	 * @param abafterShortFiling
	 *            to indicate whether the filing is after short filling or not
	 * @return Map
	 * @throws ApplicationException
	 */
	public static Map calculateDueDateonDocumentUpload(String asNewUploadedDocType, String asPeriodCoveredStartMonth, int aiPeriodCoveredStartYear,
			String asNewPeriodCoveredEndMonth, int aiNewPeriodCoveredEndYear, String asApplicableLaw, boolean abafterShortFiling)
			throws ApplicationException
	{
		return calculateDueDateOnApplicationApproval(asPeriodCoveredStartMonth, aiPeriodCoveredStartYear, asNewPeriodCoveredEndMonth,
				aiNewPeriodCoveredEndYear, asNewUploadedDocType, asApplicableLaw, abafterShortFiling);
	}

	/**
	 * Modified as per 3.1.0 . Enhancement 6021 - Added checks for char 500 extension
	 * This method is used to calculate the due date for document expiration
	 * when a task for e-filling is approved
	 * 
	 * @param asPeriodCoveredStartMonth
	 *            Current financial year start month
	 * @param aiPeriodCoveredStartYear
	 *            current financial year start year
	 * @param asPeriodCoveredEndMonth
	 *            Current financial year End month
	 * @param aiPeriodCoveredEndYear
	 *            Current financial year End year
	 * @param asDocumentType
	 *            document Type user is trying to upload
	 * @param asApplicableLaw
	 *            Applicable law for that char500 type document
	 * @param abafterShortFiling
	 *            to indicate whether the filing is after short filling or not
	 * @return loDuedateDetailsMap map containing all details regarding
	 * @throws ApplicationException
	 */
	public static Map calculateDueDateOnApplicationApproval(String asPeriodCoveredStartMonth, int aiPeriodCoveredStartYear,
			String asPeriodCoveredEndMonth, int aiPeriodCoveredEndYear, String asDocumentType, String asApplicableLaw, boolean abafterShortFiling)
			throws ApplicationException
	{

		Date loDueDate = null;
		Date loDStartNextFiscalYear = null;
		Date loDEndNextFiscalYear = null;
		String lsNextExpectedDocType = null;
		Map loDuedateDetailsMap = new HashMap();
		int liMonths;
		int liDays;
		if (P8Constants.PROPERTY_PE_LAW_TYPE.equalsIgnoreCase(asApplicableLaw))
		{
			liMonths = getNumberofMonthsForPTLaw(asDocumentType);
			liDays = 0;
		}
		else
		{
			StringBuilder loSBDays = new StringBuilder();
			liMonths = getNumberofMonthsForExecutiveLaw(asDocumentType, loSBDays);
			liDays = Integer.parseInt(loSBDays.toString());
		}
		loDueDate = addMonths(getCalendar(asPeriodCoveredEndMonth, aiPeriodCoveredEndYear), liMonths, liDays);
		if (!P8Constants.PROPERTY_PE_LAW_TYPE.equalsIgnoreCase(asApplicableLaw))
		{
			GregorianCalendar loCalender = new GregorianCalendar();
			loCalender.setTime(loDueDate);
			loCalender.set(Calendar.DAY_OF_MONTH, 15);
			loDueDate = loCalender.getTime();
		}
		else
		{
			GregorianCalendar loCalender = new GregorianCalendar();
			loCalender.setTime(loDueDate);
			loCalender.set(Calendar.DAY_OF_MONTH, loCalender.getActualMaximum(Calendar.DAY_OF_MONTH));
			loDueDate = loCalender.getTime();
		}
		if (!abafterShortFiling)
		{
			if (P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE.equalsIgnoreCase(asDocumentType))
			{
				loDStartNextFiscalYear = getNextFiscalYearStartDateOnApproval(getCalendar(asPeriodCoveredEndMonth, aiPeriodCoveredStartYear));
				loDEndNextFiscalYear = getNextFiscalYearEndDate(getCalendar(asPeriodCoveredEndMonth, aiPeriodCoveredEndYear));
				//Modified as per 3.1.0 . Enhancement 6021 - Updated for char 500 - extension
				//Start of changes for Release 3.10.0 . Enhancement 6573 - Remove CHAR500 extension logic
				lsNextExpectedDocType = P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE;
				//End of changes for Release 3.10.0 . Enhancement 6573 - Remove CHAR500 extension logic
			}
			else
			{
				loDStartNextFiscalYear = getCalendar(asPeriodCoveredStartMonth, aiPeriodCoveredStartYear).getTime();
				loDEndNextFiscalYear = getCalendar(asPeriodCoveredEndMonth, aiPeriodCoveredEndYear).getTime();
				//Modified as per 3.1.0 . Enhancement 6021 - Removing check for 1st/2nd Extension
				lsNextExpectedDocType = P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE;
			}
		}
		else
		{
			loDStartNextFiscalYear = getNextFiscalYearStartEndDateOnApprovalShortFilling(getCalendar(asPeriodCoveredEndMonth, aiPeriodCoveredStartYear));
			loDEndNextFiscalYear = getCalendar(asPeriodCoveredEndMonth, aiPeriodCoveredEndYear).getTime();
			//Modified as per 3.1.0 . Enhancement 6021 - Updated for char 500 - extension
			//Start of changes for Release 3.10.0 . Enhancement 6573 - Remove CHAR500 extension logic
			lsNextExpectedDocType = P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE;
			//End of changes for Release 3.10.0 . Enhancement 6573 - Remove CHAR500 extension logic
		}
		loDuedateDetailsMap.put(P8Constants.PROPERTY_PE_CURRENT_DUE_DATE, DateUtil.getDateMMddYYYYFormat(loDueDate));
		java.sql.Date loDate = new java.sql.Date(loDueDate.getTime());
		loDuedateDetailsMap.put(P8Constants.PROPERTY_PE_UPLOADED_DOC_TYPE, asDocumentType);
		loDuedateDetailsMap.put("sqlDueDate", loDate);
		loDuedateDetailsMap.put(P8Constants.PROPERTY_PE_APPLICABLE_LAW, asApplicableLaw);
		loDuedateDetailsMap.put(P8Constants.PROPERTY_PE_NEXT_EXPECTED_DOC_TYPE, lsNextExpectedDocType);
		loDuedateDetailsMap.put(P8Constants.PROPERTY_PE_PRESENT_PERIOD_COVERED_START_YEAR, aiPeriodCoveredStartYear);
		loDuedateDetailsMap.put(P8Constants.PROPERTY_PE_PRESENT_PERIOD_COVERED_START_MONTH, asPeriodCoveredStartMonth);
		if (!abafterShortFiling)
		{
			Calendar loCal = new GregorianCalendar();
			loCal.setTime(loDStartNextFiscalYear);
			loDuedateDetailsMap.put(P8Constants.PROPERTY_PE_NEXT_PERIOD_COVERED_START_YEAR, loCal.get(Calendar.YEAR));
			loDuedateDetailsMap.put(P8Constants.PROPERTY_PE_NEXT_PERIOD_COVERED_START_MONTH, getMonth(loCal.get(Calendar.MONTH)));
			loCal.setTime(loDEndNextFiscalYear);
			loDuedateDetailsMap.put(P8Constants.PROPERTY_PE_NEXT_PERIOD_COVERED_END_YEAR, loCal.get(Calendar.YEAR));
			loDuedateDetailsMap.put(P8Constants.PROPERTY_PE_NEXT_PERIOD_COVERED_END_MONTH, getMonth(loCal.get(Calendar.MONTH)));
		}
		else
		{
			loDuedateDetailsMap.put(P8Constants.PROPERTY_PE_NEXT_PERIOD_COVERED_START_YEAR, aiPeriodCoveredStartYear);
			loDuedateDetailsMap.put(P8Constants.PROPERTY_PE_NEXT_PERIOD_COVERED_START_MONTH, asPeriodCoveredStartMonth);
			loDuedateDetailsMap.put(P8Constants.PROPERTY_PE_NEXT_PERIOD_COVERED_END_YEAR, aiPeriodCoveredEndYear);
			loDuedateDetailsMap.put(P8Constants.PROPERTY_PE_NEXT_PERIOD_COVERED_END_MONTH, asPeriodCoveredEndMonth);
		}
		return loDuedateDetailsMap;
	}

	/**
	 * Modified as per 3.1.0 . Enhancement 6021 - Added checks for char 500 extension
	 * This method is used to get the next allowed document type when a
	 * application is rolled back or rejected
	 * 
	 * @param asSecondLastUploadedDocType
	 *            Document type uploaded before.
	 * @return lsNextExpectedDocType Document type expected next to be uploaded
	 */
	public static String getRollbackDoctype(String asSecondLastUploadedDocType)
	{
		String lsNextExpectedDocType = null;

		if (P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE.equalsIgnoreCase(asSecondLastUploadedDocType))
		{
			//Modified as per 3.1.0 . Enhancement 6021 - Updated for char 500 - extension
			lsNextExpectedDocType = P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE + HHSConstants.HHSUTIL_DELIM_PIPE + P8Constants.PROPERTY_CE_CHAR500_EXTENSION;
		}
		else if (P8Constants.PROPERTY_CE_CHAR500_EXT1_DOC_TYPE.equalsIgnoreCase(asSecondLastUploadedDocType))
		{
			lsNextExpectedDocType = P8Constants.PROPERTY_CE_CHAR500_EXT2_DOC_TYPE + HHSConstants.HHSUTIL_DELIM_PIPE + P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE;
		}
		else
		{
			lsNextExpectedDocType = P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE;
		}

		return lsNextExpectedDocType;
	}

	/**
	 * Modified as per 3.1.0 . Enhancement 6021 - Added checks for char 500 extension
	 * This method is used to get the number of months for calculating the due
	 * date for a document uploaded basically it will calculate the number of
	 * month for Power trust law type
	 * 
	 * @param asDocumentType
	 *            Document type
	 * @return liReturnMonths Number of months return to calculate due date
	 * @throws ApplicationException
	 */
	public static int getNumberofMonthsForPTLaw(String asDocumentType) throws ApplicationException
	{
		int liReturnMonths;
		if (P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE.equalsIgnoreCase(asDocumentType))
		{
			//Start of changes for Release 3.10.0 . Enhancement 6573 - Remove CHAR500 extension logic
			// Months changed from 12 to 24 for Release 3.10.2
			liReturnMonths = 24;
			//End of changes for Release 3.10.0 . Enhancement 6573 - Remove CHAR500 extension logic
		}
		else if (P8Constants.PROPERTY_CE_CHAR500_EXT1_DOC_TYPE.equalsIgnoreCase(asDocumentType))
		{
			liReturnMonths = 9;
		}
		else if (P8Constants.PROPERTY_CE_CHAR500_EXT2_DOC_TYPE.equalsIgnoreCase(asDocumentType))
		{
			liReturnMonths = 12;
		}
		//Modified as per 3.1.0 . Enhancement 6021 - Added checks for char 500 - extension
		else if (P8Constants.PROPERTY_CE_CHAR500_EXTENSION.equalsIgnoreCase(asDocumentType))
		{
			liReturnMonths =12;
		}
		else
		{
			throw new ApplicationException("Unknown Document type: " + asDocumentType);
		}
		return liReturnMonths;
	}

	/**
	 * Modified as per 3.1.0 . Enhancement 6021 - Added checks for char 500 extension
	 * This method is used to get the number of months for calculating the due
	 * date for a document uploaded basically it will calculate the number of
	 * month for Executive law type
	 * 
	 * @param asDocumentType
	 *            Document type
	 * @param loSBNoOfDays
	 *            String buffer containing number of days and month
	 * @return liReturnMonths number of months return to calculate due date
	 * @throws ApplicationException
	 */
	public static int getNumberofMonthsForExecutiveLawShort(String asDocumentType, StringBuilder aoSBNoOfDays) throws ApplicationException
	{
		int liReturnMonths;
		if (P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE.equalsIgnoreCase(asDocumentType))
		{
			liReturnMonths = 16;
			aoSBNoOfDays.append("15");
		}
		else if (P8Constants.PROPERTY_CE_CHAR500_EXT1_DOC_TYPE.equalsIgnoreCase(asDocumentType))
		{
			liReturnMonths = 7;
			aoSBNoOfDays.append("15");
		}
		else if (P8Constants.PROPERTY_CE_CHAR500_EXT2_DOC_TYPE.equalsIgnoreCase(asDocumentType))
		{
			liReturnMonths = 10;
			aoSBNoOfDays.append("15");
		}
		//Modified as per 3.1.0 . Enhancement 6021 - Added checks for char 500 - extension
		else if (P8Constants.PROPERTY_CE_CHAR500_EXTENSION.equalsIgnoreCase(asDocumentType))
		{
			liReturnMonths = 10;
			aoSBNoOfDays.append("15");
		}
		else
		{
			throw new ApplicationException("Unknown Document type: " + asDocumentType);
		}
		return liReturnMonths;
	}

	/**
	 * Modified as per 3.1.0 . Enhancement 6021 - Added checks for char 500 extension
	 * This method is used to get the number of months for calculating the due
	 * date for a document uploaded basically it will calculate the number of
	 * month for Power trust law type
	 * 
	 * @param asDocumentType
	 *            Document type
	 * @return liReturnMonths Number of months return to calculate due date
	 * @throws ApplicationException
	 */
	public static int getNumberofMonthsForPTLawShort(String asDocumentType) throws ApplicationException
	{
		int liReturnMonths;
		if (P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE.equalsIgnoreCase(asDocumentType))
		{
			liReturnMonths = 6;
		}
		else if (P8Constants.PROPERTY_CE_CHAR500_EXT1_DOC_TYPE.equalsIgnoreCase(asDocumentType))
		{
			liReturnMonths = 9;
		}
		else if (P8Constants.PROPERTY_CE_CHAR500_EXT2_DOC_TYPE.equalsIgnoreCase(asDocumentType))
		{
			liReturnMonths = 12;
		}
		//Modified as per 3.1.0 . Enhancement 6021 - Added checks for char 500 - extension
		else if (P8Constants.PROPERTY_CE_CHAR500_EXTENSION.equalsIgnoreCase(asDocumentType))
		{
			liReturnMonths = 12;
		}
		else
		{
			throw new ApplicationException("Unknown Document type: " + asDocumentType);
		}
		return liReturnMonths;
	}

	/**
	 * Modified as per 3.1.0 . Enhancement 6021 - Added checks for char 500 extension
	 * This method is used to get the number of months for calculating the due
	 * date for a document uploaded basically it will calculate the number of
	 * month for Executive law type
	 * 
	 * @param asDocumentType
	 *            Document type
	 * @param loSBNoOfDays
	 *            String buffer containing number of days and month
	 * @return liReturnMonths number of months return to calculate due date
	 * @throws ApplicationException
	 */
	public static int getNumberofMonthsForExecutiveLaw(String asDocumentType, StringBuilder aoSBNoOfDays) throws ApplicationException
	{
		int liReturnMonths;
		if (P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE.equalsIgnoreCase(asDocumentType))
		{
			//Start of changes for Release 3.10.0 . Enhancement 6573 - Remove CHAR500 extension logic
			// Months changed from 10 to 22.5 for Release 3.10.2
			liReturnMonths = 22;
			//End of changes for Release 3.10.0 . Enhancement 6573 - Remove CHAR500 extension logic
			aoSBNoOfDays.append("15");
		}
		else if (P8Constants.PROPERTY_CE_CHAR500_EXT1_DOC_TYPE.equalsIgnoreCase(asDocumentType))
		{
			liReturnMonths = 7;
			aoSBNoOfDays.append("15");
		}
		else if (P8Constants.PROPERTY_CE_CHAR500_EXT2_DOC_TYPE.equalsIgnoreCase(asDocumentType))
		{
			liReturnMonths = 10;
			aoSBNoOfDays.append("15");
		}
		//Modified as per 3.1.0 . Enhancement 6021 - Added checks for char 500 - extension
		else if (P8Constants.PROPERTY_CE_CHAR500_EXTENSION.equalsIgnoreCase(asDocumentType))
		{
			liReturnMonths = 10;
			aoSBNoOfDays.append("15");
		}
		else
		{
			throw new ApplicationException("Unknown Document type: " + asDocumentType);
		}
		return liReturnMonths;
	}
	public static Calendar getCalendar(String asMonth, int aiYear) throws ApplicationException
	{
		Calendar loCal = new GregorianCalendar();
		loCal.set(aiYear, getMonth(asMonth), 1);

		loCal.set(Calendar.DATE, loCal.getActualMaximum(Calendar.DATE));
		return loCal;
	}

	/**
	 * This method will create one calendar object of the date passed to it
	 * 
	 * @param aoDate
	 *            date to be added to calendar object
	 * @return loCal calendar object containing all details of the date object
	 * @throws ApplicationException
	 */
	public static Calendar getCalendar(Date aoDate) throws ApplicationException
	{
		Calendar loCal = new GregorianCalendar();
		loCal.setTime(aoDate);
		return loCal;
	}

	/**
	 * This method will add the months to the calendar and return the final date
	 * 
	 * @param aoCalendar
	 *            calendar object contains the date details
	 * @param aiNoOfMonths
	 *            no of months to be added to calculate due date
	 * @param aiNoOfDays
	 *            no of days to be added to calculate due date
	 * @return due date calculated
	 * @throws ApplicationException
	 */
	public static Date addMonths(Calendar aoCalendar, int aiNoOfMonths, int aiNoOfDays) throws ApplicationException
	{
		aoCalendar.add(Calendar.MONTH, aiNoOfMonths);
		aoCalendar.add(Calendar.DATE, aiNoOfDays);
		return aoCalendar.getTime();
	}

	/**
	 * Modified By- Tanuj
	 * 
	 * This method has been modified in release 2.6.0 for defect 5589.
	 * This method will return start year after rejection of new filling WF.
	 * <ul>
	 * <li>1. Passes calendar year to method</li>
	 * <li>2. It calculates the calendar year based on calculation.</li>
	 * </ul>
	 * This method will get the Start date of the next financial year
	 * <ul>
	 * <li> Method Updated in R4 </li>
	 * </ul>
	 * @param aoCalendar
	 *            a calendar object containing the details of the Start date of
	 *            the next financial year
	 * @return start date of the next financial year

	 */
	public static Date getNextFiscalYearStartDateOnRejection(Calendar aoCalendar)
	{
		//5589 fix start
		aoCalendar.add(Calendar.MONTH, -12);
		aoCalendar.set(Calendar.DAY_OF_MONTH, aoCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		aoCalendar.add(Calendar.DATE,1);
		//5589 fix end
		return aoCalendar.getTime();
	}
	
	/**
	 * This method will get the Start date of the next financial year
	 * <ul>
	 * <li> Method Updated in R4 </li>
	 * </ul>
	 * @param aoCalendar
	 *            a calendar object containing the details of the Start date of
	 *            the next financial year
	 * @return start date of the next financial year
	 */
	public static Date getNextFiscalYearStartDateOnApproval(Calendar aoCalendar)
	{
		aoCalendar.add(Calendar.YEAR, 1);
		if(aoCalendar.getActualMaximum(Calendar.DAY_OF_YEAR)>365)
		{
			if (aoCalendar.get(Calendar.MONTH) == 1)
			{
				aoCalendar.add(Calendar.DATE,2);
			}else
			{
				aoCalendar.add(Calendar.DATE, 1);
				if (aoCalendar.get(Calendar.MONTH) == 0)
				{
					aoCalendar.add(Calendar.YEAR, -1);
				}
			}
		}else{
			aoCalendar.add(Calendar.DATE, 1);
			if (aoCalendar.get(Calendar.MONTH) == 0)
			{
				aoCalendar.add(Calendar.YEAR, -1);
			}
		}
		return aoCalendar.getTime();
	}
	/**
	 * This method will get the Start date of the next financial year
	 * <ul>
	 * <li> Method Updated in R4 </li>
	 * </ul>
	 * @param aoCalendar
	 *            a calendar object containing the details of the Start date of
	 *            the next financial year
	 * @return start date of the next financial year
	 */
	public static Date getNextFiscalYearStartEndDateOnApprovalShortFilling(Calendar aoCalendar)
	{
		if(aoCalendar.getActualMaximum(Calendar.DAY_OF_YEAR)>365)
		{
			if (aoCalendar.get(Calendar.MONTH) == 1)
			{
				aoCalendar.add(Calendar.DATE,2);
			}else
			{
				aoCalendar.add(Calendar.DATE, 1);
				if (aoCalendar.get(Calendar.MONTH) == 0)
				{
					aoCalendar.add(Calendar.YEAR, -1);
				}
			}
		}else{
			aoCalendar.add(Calendar.DATE, 1);
			if (aoCalendar.get(Calendar.MONTH) == 0)
			{
				aoCalendar.add(Calendar.YEAR, -1);
			}
		}
		return aoCalendar.getTime();
	}
	/**
	 * This method will get the End date of the next financial year
	 * 
	 * @param aoCalendar
	 *            a calendar object containing the details of the end date of
	 *            the next financial year
	 * @return End date of the next financial year
	 */
	public static Date getNextFiscalYearEndDate(Calendar aoCalendar)
	{
		aoCalendar.add(Calendar.YEAR, 1);
		return aoCalendar.getTime();
	}

	/**
	 * This method will get the Start month of the short filling financial year
	 * 
	 * @param aoCalendar
	 *            a calendar object containing the details of the Start date of
	 *            the short filling financial year
	 * @return Start date of the short filling financial year
	 */
	public static Date getStartOfShortFilingFiscalYear(Calendar aoCalendar)
	{
		aoCalendar.add(Calendar.MONTH, 1);
		return aoCalendar.getTime();
	}

	/**
	 * This method will get the end month of the short filling financial year
	 * 
	 * @param aoCalendar
	 *            a calendar object containing the details of the end date of
	 *            the short filling financial year
	 * @return Last date of the short filling financial year
	 */
	public static Date getEndOfShortFilingFiscalYear(Calendar aoCalendar)
	{
		aoCalendar.add(Calendar.MONTH, -1);
		aoCalendar.add(Calendar.DATE, aoCalendar.getLeastMaximum(Calendar.DAY_OF_MONTH));
		return aoCalendar.getTime();
	}

	/**
	 * This method will return the number of the month when the name of the
	 * month is passed to it
	 * 
	 * @param asMonth
	 *            name of month
	 * @return liReturn number of month
	 * @throws ApplicationException
	 */
	public static int getMonth(String asMonth) throws ApplicationException
	{
		int liReturn;
		if ("JAN".equalsIgnoreCase(asMonth))
		{
			liReturn = Calendar.JANUARY;
		}
		else if ("FEB".equalsIgnoreCase(asMonth))
		{
			liReturn = Calendar.FEBRUARY;
		}
		else if ("MAR".equalsIgnoreCase(asMonth))
		{
			liReturn = Calendar.MARCH;
		}
		else if ("APR".equalsIgnoreCase(asMonth))
		{
			liReturn = Calendar.APRIL;
		}
		else if ("MAY".equalsIgnoreCase(asMonth))
		{
			liReturn = Calendar.MAY;
		}
		else if ("JUN".equalsIgnoreCase(asMonth))
		{
			liReturn = Calendar.JUNE;
		}
		else if ("JUL".equalsIgnoreCase(asMonth))
		{
			liReturn = Calendar.JULY;
		}
		else if ("AUG".equalsIgnoreCase(asMonth))
		{
			liReturn = Calendar.AUGUST;
		}
		else if ("SEP".equalsIgnoreCase(asMonth))
		{
			liReturn = Calendar.SEPTEMBER;
		}
		else if ("OCT".equalsIgnoreCase(asMonth))
		{
			liReturn = Calendar.OCTOBER;
		}
		else if ("NOV".equalsIgnoreCase(asMonth))
		{
			liReturn = Calendar.NOVEMBER;
		}
		else if ("DEC".equalsIgnoreCase(asMonth))
		{
			liReturn = Calendar.DECEMBER;
		}
		else
		{
			throw new ApplicationException("Incorrect Month for parsing: " + asMonth);
		}
		return liReturn;
	}

	/**
	 * This method will return the name of month taking the number of the month
	 * 
	 * @param aiMonth
	 *            number of month
	 * @return name the month
	 * @throws ApplicationException
	 */
	public static String getMonth(int aiMonth) throws ApplicationException
	{
		String lsArrMonths[] =
		{ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
		return lsArrMonths[aiMonth];
	}

	/**
	 * This method will get the details for the short filling documents
	 * 
	 * @param asCurrentPeriodCoveredStartMonth
	 *            current financial year start month.
	 * @param asCurrentPeriodCoveredEndMonth
	 *            current financial year end month
	 * @param asNewPeriodCoveredStartMonth
	 *            new financial year start month
	 * @param asNewPeriodCoveredEndMonth
	 *            new financial year end month
	 * @param aiChangedPeriodEffectiveYear
	 *            change of financial year effect from the year
	 * @return loShortFilingMap map containing all details
	 * @throws ApplicationException
	 */
	public static HashMap getShortFilingDetails(String asCurrentPeriodCoveredStartMonth, String asCurrentPeriodCoveredEndMonth,
			String asNewPeriodCoveredStartMonth, String asNewPeriodCoveredEndMonth, int aiChangedPeriodEffectiveYear) throws ApplicationException
	{
		Calendar loStartShortFilingCal = new GregorianCalendar();
		Calendar loEndShortFilingCal = new GregorianCalendar();
		if(getMonth(asCurrentPeriodCoveredEndMonth)>=getMonth(asNewPeriodCoveredStartMonth))
		{
			loStartShortFilingCal.set(aiChangedPeriodEffectiveYear - 1, getMonth(asCurrentPeriodCoveredEndMonth), 1);
			loEndShortFilingCal.set(aiChangedPeriodEffectiveYear, getMonth(asNewPeriodCoveredStartMonth), 1);
		}
		else
		{
			loStartShortFilingCal.set(aiChangedPeriodEffectiveYear, getMonth(asCurrentPeriodCoveredEndMonth), 1);
			loEndShortFilingCal.set(aiChangedPeriodEffectiveYear, getMonth(asNewPeriodCoveredStartMonth), 1);
		}
		
		Date loStartShortFilngDate = getStartOfShortFilingFiscalYear(loStartShortFilingCal);
		Date loEndShortFilngDate = getEndOfShortFilingFiscalYear(loEndShortFilingCal);
		loStartShortFilingCal.setTime(loStartShortFilngDate);
		loEndShortFilingCal.setTime(loEndShortFilngDate);
		HashMap loShortFilingMap = new HashMap();
		loShortFilingMap.put("START_MONTH", getMonth(loStartShortFilingCal.get(Calendar.MONTH)));
		loShortFilingMap.put("START_YEAR", loStartShortFilingCal.get(Calendar.YEAR));
		loShortFilingMap.put("END_MONTH", getMonth(loEndShortFilingCal.get(Calendar.MONTH)));
		loShortFilingMap.put("END_YEAR", loEndShortFilingCal.get(Calendar.YEAR));
		loShortFilingMap.put("DOCUMENT_TYPE", P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE);
		return loShortFilingMap;
	}
	
}
