package com.nyc.hhs.frameworks.sessiongrid;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.util.DateUtil;

/**
 * This class performs the sorting operation on the Java Collection objects on
 * the basis of their properties.
 * 
 */

public class SortComparator implements Comparator
{
	private static final LogInfo LOG_OBJECT = new LogInfo(SortComparator.class);
	private String msPropertyName;
	private String msSortType;
	private String msPropertyName2 = null;

	/**
	 * This constructor initializes the property of the object and the
	 * comparison sorting type.
	 * 
	 * @param asPropertyName
	 *            is string representation of the property name
	 * @param asClassName
	 *            is the string representation of the class name
	 * @param asSortType
	 *            is string representation of the sorting algorithm
	 */
	public SortComparator(String asPropertyName, String asClassName, String asSortType)
	{
		super();
		this.msPropertyName = asPropertyName;
		this.msSortType = asSortType;
	}

	/**
	 * This constructor initializes the property of the object and the
	 * comparison sorting type.
	 * 
	 * @param asPropertyName
	 *            is string representation of the property name
	 * @param asClassName
	 *            is the string representation of the class name
	 * @param asSortType
	 *            is string representation of the sorting algorithm
	 * @param asPropertyName2
	 *            is string representation of the second property
	 */
	public SortComparator(String asPropertyName, String asClassName, String asSortType, String asPropertyName2)
	{
		super();
		this.msPropertyName = asPropertyName;
		this.msPropertyName2 = asPropertyName2;
		this.msSortType = asSortType;
	}

	/**
	 * This method compares the two objects on the basis of their properties
	 * 
	 * @param aoObj1
	 *            is the first input object to be compared.
	 * @param aoObj2
	 *            is the second input object to be compared.
	 * @returns the int value on the basis of property comparison.
	 */
	@Override
	public int compare(Object ao1, Object ao2)
	{

		Object loProperty1 = null;
		Object loProperty2 = null;
		Object loProperty21 = null;
		Object loProperty22 = null;
		try
		{
			loProperty1 = new PropertyDescriptor(this.msPropertyName, ao1.getClass()).getReadMethod().invoke(ao1);
			loProperty2 = new PropertyDescriptor(this.msPropertyName, ao2.getClass()).getReadMethod().invoke(ao2);
			if (msPropertyName2 != null)
			{
				loProperty21 = new PropertyDescriptor(this.msPropertyName2, ao1.getClass()).getReadMethod().invoke(ao1);
				loProperty22 = new PropertyDescriptor(this.msPropertyName2, ao2.getClass()).getReadMethod().invoke(ao2);
			}

		}
		catch (IllegalArgumentException aoIllegalArgExp)
		{
			LOG_OBJECT.Error("Error occured while comparing", aoIllegalArgExp);

		}
		catch (IllegalAccessException aoIllegalAccsExp)
		{
			LOG_OBJECT.Error("Error occured while comparing", aoIllegalAccsExp);

		}
		catch (InvocationTargetException aoInvocationTrgtExp)
		{
			LOG_OBJECT.Error("Error occured while comparing", aoInvocationTrgtExp);

		}
		catch (IntrospectionException aoIntoseptionExp)
		{
			LOG_OBJECT.Error("Error occured while comparing", aoIntoseptionExp);
		}

		if (msPropertyName2 != null)
		{
			return compareString((java.lang.String) loProperty1, (java.lang.String) loProperty2, (java.lang.String) loProperty21,
						(java.lang.String) loProperty22);
		}
		else
		{
				return compareString((java.lang.String) loProperty1, (java.lang.String) loProperty2);
		}
	}

	/**
	 * This method compares the two input Strings
	 * 
	 * @param asData1
	 *            is the first input String to be compared
	 * @param asData2
	 *            is the second input String to be compared
	 * @return the int value on the basis of comparison of two String
	 * @throws ApplicationException 
	 */
	public int compareString(String asData1, String asData2)
	{
		if (msSortType.equalsIgnoreCase("ASC") )
		{
		//updated in R5
		if(null != asData1 && null!=asData2){
			//R5 Changes ends
			if(asData1.length()>=6 && asData1.charAt(2)=='/' && asData1.charAt(5)=='/' ){
				return DateUtil.getSortedDate(asData1).compareTo(DateUtil.getSortedDate(asData2));
			}
			else
			{
			return asData1.compareTo(asData2);
			}
		
		}
		return 0;
		}
		else
		{
		//Changes in R5
			if(null != asData1 && null!=asData2){
			if(asData1.length()>=6 && asData1.charAt(2)=='/' && asData1.charAt(5)=='/' && null != asData1 && null!=asData2){
			//R5 changes ends
				return DateUtil.getSortedDate(asData2).compareTo(DateUtil.getSortedDate(asData1));
			}
			else
			{
			return asData2.compareTo(asData1);
			}
		}
		//added in R5
		}
		return 0;
		//Ends in R5
		
	}

	/**
	 * This method compares the two input Strings
	 * 
	 * @param asData1
	 *            is the first input String to be compared
	 * @param asData2
	 *            is the second input String to be compared
	 * @param asData21
	 *            is the first input String to be compared
	 * @param asData22
	 *            is the second input String to be compared
	 * @return the int value on the basis of comparison of two String
	 * @throws ApplicationException 
	 */
	public int compareString(String asData1, String asData2, String asData21, String asData22)
	{
		int liReturnValue;
		if (msSortType.equalsIgnoreCase("ASC"))
			{
				if(asData1.length()>=6 && asData1.charAt(2)=='/' && asData1.charAt(5)=='/'){
					liReturnValue= DateUtil.getSortedDate(asData1).compareTo(DateUtil.getSortedDate(asData2));
				}
				else
				{
					liReturnValue = asData1.toLowerCase().compareTo(asData2.toLowerCase());
				}
				if (liReturnValue == 0)
				{
					if(asData22.length()>=6 && asData22.charAt(2)=='/' && asData22.charAt(5)=='/'){
						liReturnValue= DateUtil.getSortedDate(asData22).compareTo(DateUtil.getSortedDate(asData21));
					}
					else
					{
						liReturnValue = asData22.toLowerCase().compareTo(asData21.toLowerCase());
					}
				}
			}
			else
			{
				if(asData1.length()>=6 && asData1.charAt(2)=='/' && asData1.charAt(5)=='/'){
					liReturnValue= DateUtil.getSortedDate(asData2).compareTo(DateUtil.getSortedDate(asData1));
				}
				else
				{
				liReturnValue = asData2.toLowerCase().compareTo(asData1.toLowerCase());
				}
				if (liReturnValue == 0)
				{
					if(asData22.length()>=6 && asData22.charAt(2)=='/' && asData22.charAt(5)=='/'){
						liReturnValue= DateUtil.getSortedDate(asData22).compareTo(DateUtil.getSortedDate(asData21));
					}
					else
					{
					liReturnValue = asData22.toLowerCase().compareTo(asData21.toLowerCase());
					}
				}
			}	
			return liReturnValue;
	}

	/**
	 * This method compares the two string representations of date
	 * 
	 * @param asDate1
	 *            is the string representation of the first input date
	 * @param asDate2
	 *            is the string representation of the second input date
	 * @return the int value on the basis of property comparison.
	 */
	public int compareDate(String asDate1, String asDate2)
	{
		try
		{
			Date loDate = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse(asDate1);
			Date loDateToComp = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse(asDate2);
			if (msSortType.equalsIgnoreCase("ASC"))
			{
				return loDate.compareTo(loDateToComp);
			}
			else
			{
				return loDateToComp.compareTo(loDate);
			}
		}
		catch (ParseException aoExp)
		{
			LOG_OBJECT.Error("Error occured while comparing date", aoExp);
			return 0;
		}
	}

	/**
	 * This method determines whether the entered date is valid or not
	 * 
	 * @param ainDate
	 *            is the string representation of the date
	 * @return is the boolean value which determines whether the entered date is
	 *         valid or not
	 */
	public boolean isValidDate(String ainDate)
	{

		if (ainDate == null)
		{
			return false;
		}

		// set the format to use as a constructor argument
		SimpleDateFormat loFormater = new SimpleDateFormat("yyyy-MM-dd");

		if (ainDate.trim().length() != loFormater.toPattern().length())
		{
			return false;
		}

		loFormater.setLenient(false);

		try
		{
			// parse the inDate parameter
			loFormater.parse(ainDate.trim());
		}
		catch (ParseException aoPeExp)
		{
			return false;
		}
		return true;
	}


}
