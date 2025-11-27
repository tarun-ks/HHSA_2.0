package com.nyc.hhs.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This utility class provides the functionality like checking the specified
 * pattern of the value, checking if the value contains special character,
 * checking value's max length matches to specified length, checking value's min
 * length is reached, creating a regular expression for pattern and special
 * chars.
 * 
 */

public class ValidationUtil
{

	/**
	 * This method checks if a form element is mandatory or not and validates it
	 * 
	 * @param asNoData - Data
	 * @param asValue - Value to be validated
	 * @return boolean flag
	 */
	public static Boolean checkMandatory(String asNoData, String asValue)
	{
		Boolean lbFlag = false;
		if (asValue != null && asValue.length() > 0)
		{
			lbFlag = true;
		}
		return lbFlag;
	}

	/**
	 * This method checks if a form element matches a pattern or not
	 * 
	 * @param asRulePattern - Pattern against which value to be checked
	 * @param asValue - Value to be validated
	 * @return boolean flag
	 */
	public static Boolean checkPattern(String asRulePattern, String asValue)
	{
		Boolean lbFlag = false;
		asRulePattern = createRegexPattern(asRulePattern);
		lbFlag = asValue.matches(asRulePattern);
		return lbFlag;
	}

	/**
	 * This method checks if a form element contains any special character other
	 * than permitted
	 * 
	 * @param asRuleValue - Characters against which value to be checked
	 * @param asValue - Value to be validated
	 * @return boolean flag
	 */
	public static Boolean checkSpecialChar(String asRuleValue, String asValue)
	{
		Boolean lbFlag = false;
		asRuleValue = createRegexSpecialChar(asRuleValue);
		Pattern loClassPattern = Pattern.compile(asRuleValue, Pattern.CASE_INSENSITIVE);
		Matcher loMatcher = loClassPattern.matcher(asValue);
		if (!loMatcher.find())
		{
			lbFlag = true;
		}
		return lbFlag;
	}

	/**
	 * This method checks if a form element contains character more than
	 * permitted limit
	 * 
	 * @param asRuleValue - Max length to be checekd
	 * @param asValue - Value to be validated
	 * @return boolean flag
	 */
	public static Boolean checkMaxLength(String asRuleValue, String asValue)
	{
		Boolean lbFlag = false;
		int liLength = Integer.parseInt(asRuleValue);
		if (asValue != null && (asValue.length() <= liLength))
		{

			lbFlag = true;

		}
		return lbFlag;
	}

	/**
	 * This method checks if a form element contains character less than
	 * permitted limit
	 * 
	 * @param asRuleValue - Min length to be checekd
	 * @param asValue - Value to be validated
	 * @return boolean flag
	 */
	public static Boolean checkMinLength(String asRuleValue, String asValue)
	{
		Boolean lbFlag = false;
		int liLength = Integer.parseInt(asRuleValue);
		if (asValue != null && (asValue.length() >= liLength))
		{

			lbFlag = true;

		}
		return lbFlag;
	}

	/**
	 * Creates regular expression for specified pattern
	 * 
	 * @param asPattern - Pattern to be converted to regular expression
	 * @return regular expression for specified pattern
	 */
	private static String createRegexPattern(String asPattern)
	{
		StringBuffer loSbResult = new StringBuffer();
		String lsResult = asPattern.replaceAll("[^ANT]+", "\\\\Q$0\\\\E");
		lsResult = lsResult.replaceAll("A", "[0-9a-zA-Z]");
		lsResult = lsResult.replaceAll("T", "[a-zA-Z]");
		lsResult = lsResult.replaceAll("N", "[0-9]");
		loSbResult.append("^");
		loSbResult.append(lsResult);
		loSbResult.append("$");
		return loSbResult.toString();
	}

	/**
	 * Creates regular expression for specified Special characters
	 * 
	 * @param asPattern - Special characters to be converted to regular
	 *            expression
	 * @return regular expression for specified Special characters
	 */
	private static String createRegexSpecialChar(String asPattern)
	{
		StringBuffer loSbResult = new StringBuffer();
		String lsResult = asPattern.replaceAll("A", "0-9a-zA-Z");
		lsResult = lsResult.replaceAll("T", "a-zA-Z");
		lsResult = lsResult.replaceAll("N", "0-9");
		loSbResult.append("[^");
		loSbResult.append(lsResult);
		loSbResult.append("]");
		return loSbResult.toString();
	}
}
