package com.nyc.hhs.frameworks.grid;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import com.nyc.hhs.frameworks.logger.LogInfo;

/**
 * This class performs the sorting operation on the Java Collection objects on
 * the basis of their properties.
 * 
 */

public class SortComparator implements Comparator
{
	private static final LogInfo LOG_OBJECT = new LogInfo(SortComparator.class);
	private String mspropertyName;
	private String msSortType;

	/**
	 * This constructor initializes the property of the object
	 * 
	 * @param aoPropertyName
	 *            is string representation of the property name
	 */
	public SortComparator(String aoPropertyName)
	{
		super();
		this.mspropertyName = aoPropertyName;
	}

	@Override
	/**
	 * This method compares the two objects on the basis of their properties
	 * 
	 * @param aoObj1 is the first input object to be compared.
	 * @param aoObj2 is the second input object to be compared.
	 * @returns the int value on the basis of property comparison. 
	 */
	public int compare(Object aoObj1, Object aoObj2)
	{

		Integer liProperty1 = null;
		Integer liProperty2 = null;
		try
		{
			liProperty1 = (Integer) new PropertyDescriptor(this.mspropertyName, aoObj1.getClass()).getReadMethod().invoke(aoObj1);
			liProperty2 = (Integer) new PropertyDescriptor(this.mspropertyName, aoObj2.getClass()).getReadMethod().invoke(aoObj2);
		}
		catch (IllegalArgumentException aoExp)
		{
			LOG_OBJECT.Error("Error during sort comparison" , aoExp);
		}
		catch (IllegalAccessException aoExp)
		{
			LOG_OBJECT.Error("Error during sort comparison" , aoExp);
		}
		catch (InvocationTargetException aoExp)
		{
			LOG_OBJECT.Error("Error during sort comparison" , aoExp);
		}
		catch (IntrospectionException aoExp)
		{
			LOG_OBJECT.Error("Error during sort comparison" , aoExp);
		}
		if (liProperty1.equals(liProperty2))
		{
			return 0;
		}
		else if (liProperty1 > liProperty2)
		{
			return 1;
		}
		else
		{
			return -1;
		}

	}

	/**
	 * This constructor initializes the property of the object and the
	 * comparison sorting type.
	 * 
	 * @param aspropertyName
	 *            is string representation of the property name
	 * @param asclassName
	 *            is the string representation of the class name
	 * @param asSortType
	 *            is string representation of the sorting algorithm
	 */
	public SortComparator(String aspropertyName, String asclassName, String asSortType)
	{
		super();
		this.mspropertyName = aspropertyName;
		this.msSortType = asSortType;
	}

	/**
	 * This method compares the two input Strings
	 * 
	 * @param asData1
	 *            is the first input String to be compared
	 * @param asData2
	 *            is the second input String to be compared
	 * @return the int value on the basis of comparison of two String
	 */
	public int compareString(String asData1, String asData2)
	{
		if (msSortType.equalsIgnoreCase("ASC"))
		{
			return asData1.compareTo(asData2);
		}
		else
		{
			return asData2.compareTo(asData1);
		}
	}

	/**
	 * This method compares the two string representations of date
	 * 
	 * @param asData1
	 *            is the string representation of the first input date
	 * @param asData2
	 *            is the string representation of the second input date
	 * @return the int value on the basis of property comparison.
	 */
	public int compareDate(String asData1, String asData2)
	{
		try
		{
			Date loDate1 = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse(asData1);
			Date loDate2 = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse(asData2);
			if (msSortType.equalsIgnoreCase("ASC"))
			{
				return loDate1.compareTo(loDate2);
			}
			else
			{
				return loDate2.compareTo(loDate1);
			}
		}
		catch (ParseException aoParseExp)
		{
			LOG_OBJECT.Error("Error occured while comparing date in Sort Comparator", aoParseExp);
			return 0;
		}
	}

	/**
	 * This method determines whether the entered date is valid or not
	 * 
	 * @param asinDate
	 *            is the string representation of the date
	 * @return is the boolean value which determines whether the entered date is
	 *         valid or not
	 */
	public boolean isValidDate(String asinDate)
	{

		if (asinDate == null)
		{
			return false;
		}

		// set the format to use as a constructor argument
		SimpleDateFormat loDateFormat = new SimpleDateFormat("yyyy-MM-dd");

		if (asinDate.trim().length() != loDateFormat.toPattern().length())
		{
			return false;
		}

		loDateFormat.setLenient(false);

		try
		{
			// parse the inDate parameter
			loDateFormat.parse(asinDate.trim());
		}
		catch (ParseException aoParseExp)
		{
			return false;
		}
		return true;
	}

}
