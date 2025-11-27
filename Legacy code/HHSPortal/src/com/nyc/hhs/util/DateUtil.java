package com.nyc.hhs.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;

/**
 * This utility class has date operations to avoid boilerplate code. These
 * operation includes retrieving current System date, converting String date to
 * SQL date format, to parse in format like EE MMM dd HH:mm:ss zz yyyy and
 * adding a specific number of years to the given date.
 * 
 */

public class DateUtil
{
	private static final LogInfo LOG_OBJECT = new LogInfo(DateUtil.class);
	
	private static final String FILENET_DATE_TIME_FORMAT = "yyyyMMdd'T'HHmmss'Z'";

	
	/**
	 * Set the day begin time of the given Date
	 *
	 * @param date
	 *
	 * @return new instance of java.util.Date with the time set
	 */
	
	public static Date setBeginTimeOfDate( final Date date ) throws ApplicationException
	{
		return setTime(date,0,0,0);	  
	}
	
	/**
	 * Set the day end time of the given Date
	 *
	 * @param date
	 *
	 * @return new instance of java.util.Date with the time set
	 */
	
	public static Date setEndTimeOfDate( final Date date ) throws ApplicationException
	{
		return setTime(date,23,59,59);	  
	}
	
	
	/**
	 * Set the time of the given Date
	 *
	 * @param date
	 * @param hourOfDay
	 * @param minute
	 * @param second
	 *
	 * @return new instance of java.util.Date with the time set
	 */
	public static Date setTime( final Date date, final int hourOfDay, final int minute, final int second ) throws ApplicationException
	{
	    GregorianCalendar gc = new GregorianCalendar();
	    gc.setTime( date );
	    gc.set( Calendar.HOUR_OF_DAY, hourOfDay );
	    gc.set( Calendar.MINUTE, minute );
	    gc.set( Calendar.SECOND, second );	  
	    return gc.getTime();
	}
	/**
	 * This Method changes the inputed Date in FILENET_DATE_TIME_FORMAT("yyyyMMdd'T'HHmmss'Z'") format .
	 * 
	 * @param aoDate Date to be convert
	 * @return String
	 */
	public static String getFilenetUtcDateTimeFormat(Date aoDate) throws ApplicationException
	{

		if (null == aoDate)
		{
			return null;
		}

		SimpleDateFormat lsDateFormat = new SimpleDateFormat(FILENET_DATE_TIME_FORMAT);
		lsDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return lsDateFormat.format(aoDate);
	}

	
	/**
	 * This Method fetches the Current Date
	 * 
	 * @return String
	 */
	public static String getCurrentDate() throws ApplicationException
	{

		Calendar loCalendar = Calendar.getInstance();
		SimpleDateFormat loDateFormat = new SimpleDateFormat("MM/dd/yyyy");
		return loDateFormat.format(loCalendar.getTime());
	}
	
	/**
	 * This Method fetches the Current Date with time stamp
	 * @return String
	 * @throws ApplicationException
	 */
	public static String getCurrentDateWithTimeStamp() throws ApplicationException
	{

		Calendar loCalendar = Calendar.getInstance();
		SimpleDateFormat loDateFormat = new SimpleDateFormat(HHSR5Constants.NFCTH_TIMESTAMP_FORMAT);
		return loDateFormat.format(loCalendar.getTime());
	}

	/**
	 * This Method changes the inputed String in "MM/dd/yyyy" format .
	 * 
	 * @param aoDate Date to be changed
	 * @return Date sqlDate
	 */
	public static java.sql.Date getSqlDate(String aoDate) throws ApplicationException
	{

		SimpleDateFormat loDateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date loResult = null;
		java.sql.Date loSqlDate = null;
		try
		{
			if (null != aoDate)
			{
				loResult = loDateFormat.parse(aoDate);
				loSqlDate = new java.sql.Date(loResult.getTime());
			}

		}
		catch (ParseException aoExp)
		{
			LOG_OBJECT.Error("Application Exception in DateUtil", aoExp);
		}

		return loSqlDate;
	}

	/**
	 * This Method will fetch the Gregorian time for input String .
	 * 
	 * @param aoDate Date to be changed
	 * @return Date sqlDate
	 */
	public static java.sql.Date getSqlDate(java.util.Date aoDate) throws ApplicationException
	{
		java.sql.Date loCalculatedDate = null;

		if (aoDate != null)
		{
			final GregorianCalendar loCalendar = new GregorianCalendar();
			loCalendar.setTime(aoDate);
			loCalculatedDate = new java.sql.Date(loCalendar.getTime().getTime());
		}

		return loCalculatedDate;
	}

	/**
	 * This Method changes the inputed Date in "MM/dd/yyyy" format .
	 * 
	 * @param aoDate Date to be changed
	 * @return String
	 */
	public static String getDateMMddYYYYFormat(Date aoDate) throws ApplicationException
	{

		if (null == aoDate)
		{
			return null;
		}

		SimpleDateFormat lsDateFormat = new SimpleDateFormat("MM/dd/yyyy");
		return lsDateFormat.format(aoDate);
	}

	/**
	 * This Method changes the inputed Date in "MM/dd/yyyy" format .
	 * 
	 * @param aoDate Date to be changed
	 * @return Date
	 */
	public static Date getDate(String aoDate) throws ApplicationException
	{

		SimpleDateFormat loDateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date loResult = null;
		if (aoDate != null && !("").equalsIgnoreCase(aoDate))
		{
			try
			{
				loResult = loDateFormat.parse(aoDate);
			}
			catch (ParseException aoParseException)
			{
				LOG_OBJECT.Error("Application Exception in DateUtil", aoParseException);
			}
		}

		return loResult;
	}

	/**
	 * This Method changes the inputed Date in "yyyyMMdd" format
	 * 
	 * @param aoDate Date to be changed
	 * @return String
	 */
	public static String getDateyyyyMMddFormat(String aoDate) throws ApplicationException
	{

		java.sql.Date loDate = getSqlDate(aoDate);

		SimpleDateFormat loDateFormat = new SimpleDateFormat("yyyyMMdd");

		return loDateFormat.format(loDate);
	}

	/**
	 * This Method changes the inputed Date in "EE MMM dd HH:mm:ss zz yyyy"
	 * format
	 * 
	 * @param aoDate Date to be changed
	 * @return Date
	 */
	public static java.sql.Date getUnParseDateFormat(final String aoDate) throws ApplicationException
	{
		String loFormat = "EE MMM dd HH:mm:ss zz yyyy";
		Date loUtilDate = null;
		try
		{
			loUtilDate = new SimpleDateFormat(loFormat, Locale.ENGLISH).parse(aoDate);
		}
		catch (ParseException aoParseException)
		{
			LOG_OBJECT.Error("Application Exception in DateUtil", aoParseException);
		}
		final java.sql.Date loSqlDate = new java.sql.Date(loUtilDate.getTime());
		return loSqlDate;
	}

	/**
	 * This Method changed the date format from asInputFormat to asOutPutFormat.
	 * 
	 * @param asInputFormat Input Date Format
	 * @param asOutPutFormat OutPut Date Format
	 * @param asDate Date To be changed
	 * @return
	 */
	public static String getDateByFormat(String asInputFormat, String asOutPutFormat, String asDate)
			throws ApplicationException
	{

		SimpleDateFormat loFromUser = new SimpleDateFormat(asInputFormat);
		SimpleDateFormat loMyFormat = new SimpleDateFormat(asOutPutFormat);
		String loReformattedStr = "";
		try
		{
			loReformattedStr = loMyFormat.format(loFromUser.parse(asDate));
		}
		catch (ParseException aoParseException)
		{
			LOG_OBJECT.Error("Application Exception in DateUtil", aoParseException);
		}

		return loReformattedStr;
	}

	/**
	 * This Method changes the inputed Date in "dd-MMM-yyyy" format
	 * 
	 * @param aoDate Date To be changed
	 * @return Date
	 */
	public static Date getDatewithMMMFormat(String aoDate) throws ApplicationException
	{

		SimpleDateFormat loDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
		Date loResult = null;

		if (aoDate != null && !("").equalsIgnoreCase(aoDate))
		{
			try
			{
				loResult = loDateFormat.parse(aoDate);
			}
			catch (ParseException aoExp)
			{
				LOG_OBJECT.Error("Application Exception in DateUtil", aoExp);
			}
		}

		return loResult;
	}

	// added for release 5
	/**
	 * This Method changes the inputed Date in "yyyy-MM-dd HH:mm:ss" format
	 * 
	 * @param aoDate String To be formatted
	 * @return Date
	 */
	public static Date getDatewithMMMFormatWithTimeStamp(String aoDate) throws ApplicationException
	{

		SimpleDateFormat loDateFormat = new SimpleDateFormat(HHSConstants.NFCTH_DATE_FORMAT);
		Date loResult = null;

		if (aoDate != null && !("").equalsIgnoreCase(aoDate))
		{
			try
			{
				loResult = loDateFormat.parse(aoDate);
			}
			catch (ParseException aoExp)
			{
				LOG_OBJECT.Error("Application Exception in DateUtil", aoExp);
			}
		}

		return loResult;
	}

	// added for release 5
	/**
	 * This Method changes the inputed Date in "yyyy-MM-dd hh:mm:ss" format
	 * 
	 * @param aoDate Date to be changed
	 * @return String
	 */
	public static String getDateddmmmyyyyFormat(String aoDate) throws ApplicationException
	{
		SimpleDateFormat loFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date loUtilDate = null;

		try
		{
			loUtilDate = loFormatter.parse(aoDate);
		}
		catch (ParseException aoExp)
		{
			LOG_OBJECT.Error("Application Exception in DateUtil", aoExp);
		}

		SimpleDateFormat loDateFormat = new SimpleDateFormat("dd-MMM-yyyy");

		return loDateFormat.format(loUtilDate);
	}

	/**
	 * This Method changes the inputed Date in "dd-MMM-yyyy" format
	 * 
	 * @param aoDdate Date to be changed
	 * @return String
	 */
	public static String getSqlDateddmmmyyyyFormat(String aoDdate) throws ApplicationException
	{

		SimpleDateFormat loDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
		java.sql.Date loDate = getSqlDate(aoDdate);
		return loDateFormat.format(loDate);
	}

	/**
	 * This Method changes the inputed Date in "MM/dd/yyyy HH:mm" format
	 * 
	 * @param aoDate Date to be changed
	 * @return String
	 */
	public static String getDateMMddYYYYHHMMFormat(Date aoDate) throws ApplicationException
	{

		if (null == aoDate)
		{
			return null;
		}

		SimpleDateFormat lsDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm zz");
		return lsDateFormat.format(aoDate);
	}

	/**
	 * This Method changes the return type of Date to String
	 * 
	 * @param aoDate Date to be changed
	 * @return String
	 */
	public static String getDateMMddYYYYHHMMSSFormat(Date aoDate) throws ApplicationException
	{

		if (null == aoDate)
		{
			return null;
		}

		SimpleDateFormat lsDateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
		return lsDateFormat.format(aoDate);
	}

	/**
	 * This Method changes the inputed Date in "MM/dd/yyyy HH:mm" format
	 * 
	 * @param aoDate Date to be changed
	 * @return String
	 */
	public static String getDateMMDDYYYYFormat(Date aoDate) throws ApplicationException
	{

		if (null == aoDate)
		{
			return null;
		}

		SimpleDateFormat lsDateFormat = new SimpleDateFormat("MM/dd/yyyy");
		return lsDateFormat.format(aoDate);
	}

	/**
	 * This Method changes the format of the inputed date to the inputed format
	 * 
	 * @param asFormat Format of the date to be changed
	 * @param aoDate Date to be changed
	 * @return String
	 */
	public static String getFormattedDated(String asFormat, Date aoDate) throws ApplicationException
	{
		SimpleDateFormat loDateFormat = new SimpleDateFormat(asFormat);
		return loDateFormat.format(aoDate);
	}

	/**
	 * This Method adds the inputed years to the inputed date & returned the
	 * future date
	 * 
	 * @param aoDate Date to be changed
	 * @param aiYears No of years to be added in the date
	 * @return Date
	 */
	public static java.sql.Date addYears(java.util.Date aoDate, int aiYears) throws ApplicationException
	{
		java.sql.Date loCalculatedDate = null;

		if (aoDate != null)
		{
			final GregorianCalendar loCalendar = new GregorianCalendar();
			loCalendar.setTime(aoDate);
			loCalendar.add(Calendar.YEAR, aiYears);
			loCalculatedDate = new java.sql.Date(loCalendar.getTime().getTime());
		}

		return loCalculatedDate;
	}

	/**
	 * This Method changes the inputed Date in "MM-dd-yyyy" format
	 * 
	 * @param aoDate Date to be changed
	 * @return String
	 */
	public static String getDateyyyymmddFormat(String aoDate) throws ApplicationException
	{
		SimpleDateFormat loFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date loUtilDate = null;

		try
		{
			loUtilDate = loFormatter.parse(aoDate);
		}
		catch (ParseException aoExp)
		{
			LOG_OBJECT.Error("Application Exception in DateUtil", aoExp);
		}

		SimpleDateFormat loDateFormat = new SimpleDateFormat("MM-dd-yyyy");

		return loDateFormat.format(loUtilDate);
	}

	/**
	 * This method will calculate no of days between start date and end date
	 * 
	 * @param aoStartDate start date object
	 * @param aoEndDate end date object
	 * @return along value of days between 2 dates
	 * @throws ApplicationException If an Application exception occurs
	 */
	public static long getDateDifference(Date aoStartDate, Date aoEndDate) throws ApplicationException
	{
		long llDaysDiff = 0;
		long llStartTime = aoStartDate.getTime();
		long llEndTime = aoEndDate.getTime();
		long llDiffTime = llEndTime - llStartTime;
		llDaysDiff = llDiffTime / (1000 * 60 * 60 * 24);
		return llDaysDiff;
	}

	/**
	 * This method will get date difference in months based on inputs
	 * 
	 * @param asStartMonth a string value of start month
	 * @param asStartYear a string value of start year
	 * @param asEndMonth a string value of end month
	 * @param asEndYear a string value of end year
	 * @return a double value of months difference
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public static double calculateDateDifference(Date aoStartDate, Date aoEndDate) throws ApplicationException
	{
		long llDateDiff = aoEndDate.getTime() - aoStartDate.getTime();
		double ldDiffMonths = Math.ceil(llDateDiff / (365.24 * 24 * 60 * 60 * 1000 / 12));
		return ldDiffMonths;
	}

	/**
	 * This Method changes the inputed Date in "MM/dd/yyyy" format .
	 * 
	 * @param aoDate Date to be changed
	 * @return Date
	 */
	public static Date getSortedDate(String aoDate)
	{
		SimpleDateFormat loDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
		Date loResult = null;
		if (aoDate != null && !("").equalsIgnoreCase(aoDate))
		{
			try
			{
				loResult = loDateFormat.parse(aoDate);
			}
			catch (ParseException aoParseException)
			{
				LOG_OBJECT.Error("Application Exception in DateUtil", aoParseException);
			}
		}

		return loResult;
	}

	/**
	 * This Method validates the format of input Date as "MM/dd/yyyy" or not.
	 * 
	 * @param aoDate Date to be changed
	 * @return Date
	 */
	public static boolean validateDate(String aoDate)
	{

		boolean loValidDate = false;

		SimpleDateFormat loDateFormat = new SimpleDateFormat("MM/dd/yyyy");
		if (aoDate != null && !("").equalsIgnoreCase(aoDate))
		{
			try
			{
				loDateFormat.setLenient(false);
				loDateFormat.parse(aoDate);
				loValidDate = true;
			}
			catch (ParseException aoParseException)
			{
				LOG_OBJECT.Error("Application Exception in DateUtil", aoParseException);
			}
		}

		return loValidDate;

	}

	/**
	 * This Method find the next weekday.
	 * 
	 * 
	 * @return Date that the next weekday
	 */

	public static String nextWeekday() throws ApplicationException
	{
		Date d = new Date();

		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.DATE, 1);

		for (;;)
		{
			switch (cal.get(Calendar.DAY_OF_WEEK))
			{
				case 1:// Sunday
				case 7:// Sat
					cal.add(Calendar.DATE, 1);
					break;
				default:
					return getDateMMddYYYYFormat(cal.getTime());
			}
		}

	}

	/**
	 * This Method find the next weekday.
	 * 
	 * @param d Date to be search
	 * @param days String
	 * @return Date that the next weekday
	 */
	public static String nextWeekday(Date d, int days) throws ApplicationException
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.DATE, days);

		for (;;)
		{
			switch (cal.get(Calendar.DAY_OF_WEEK))
			{
				case 1:// Sunday
				case 7:// Sat
					cal.add(Calendar.DATE, 1);
					break;
				default:
					return getDateMMddYYYYFormat(cal.getTime());
			}
		}
	}

	/**
	 * This method is added as a part of release 3.6.0 and this will add the
	 * specified minutes in parameter to current time stamp
	 * 
	 * @param aiTimeInMin time to be added in current time stamp
	 * @return long updated time
	 * @throws Exception
	 */
	public static long getTime(int aiTimeInMin) throws Exception
	{
		Date loDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(loDate);
		cal.add(Calendar.MINUTE, -aiTimeInMin);
		Date loDateBeforeMins = cal.getTime();
		// MM/DD/YYYY HH:MM AM/PM
		cal.clear();
		cal.setTime(loDateBeforeMins);
		long time = cal.getTimeInMillis();
		return time / 1000;
	}

	/**
	 * This Method changes the inputed Date in required format
	 * 
	 * @param aoDate Date to be changed
	 * @return Date
	 */
	// added for release 5
	public static Date getDateWithTimeStamp(final String aoDate) throws ApplicationException
	{
		Date loUtilDate = null;
		SimpleDateFormat loFormat = new SimpleDateFormat(HHSR5Constants.NFCTH_TIMESTAMP_FORMAT);
		try
		{
			loUtilDate = loFormat.parse(aoDate);
		}
		catch (ParseException aoParseException)
		{
			LOG_OBJECT.Error("Application Exception in DateUtil", aoParseException);
		}
		final java.sql.Date loSqlDate = new java.sql.Date(loUtilDate.getTime());
		return loSqlDate;
	}

	/**
	 * This Method changes the inputed Date in "MM/dd/yyyy hh:mm:ss zz" format
	 * 
	 * @param aoDate Date to be changed
	 * @return String
	 */
	public static String getDateMMDDYYYYZZFormat(Date aoDate) throws ApplicationException
	{

		if (null == aoDate)
		{
			return null;
		}

		SimpleDateFormat lsDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
		return lsDateFormat.format(aoDate);
	}
}
// added for release 5
