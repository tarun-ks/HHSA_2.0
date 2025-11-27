package com.nyc.hhs.handlersTest.ruleresulthandlersTest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.handlers.ruleresulthandlers.ArrayListStringResultHandler;

/**
 * This is a test class for ArrayListStringResultHandler class
 * 
 */
public class ArrayListStringResultHandlerTest
{

	/**
	 * This method tests for String type list return from String value splitting
	 * through a given separator.
	 * */
	@Test
	public void testformatRuleResultAllWellReturnTrue() throws ApplicationException
	{
		try
		{
			List<String> laExpectedReturnList = new ArrayList<String>();
			laExpectedReturnList.add("Hello");
			laExpectedReturnList.add("How");
			laExpectedReturnList.add("Are");
			laExpectedReturnList.add("You?");

			String lsSeparator = ",";
			String loValue = "Hello,How,Are,You?";

			ArrayListStringResultHandler loArrayListStringResultHandler = new ArrayListStringResultHandler();
			List<String> loResult = loArrayListStringResultHandler.formatRuleResult(lsSeparator, loValue);

			assertNotNull(loResult);
			assertTrue(loResult.equals(laExpectedReturnList));

		}
		catch (Exception aoEx)
		{
			throw new ApplicationException(
					"Exception occured while Unit Testing - testFormatReturnAllWellReturnTrue().", aoEx);
		}
	}

	/**
	 * This method tests for String type list(with a blank String item) return
	 * from input String value splitting through a given separator.
	 * */
	@Test
	public void testformatRuleResultValueNotSetReturnsTrue() throws ApplicationException
	{
		try
		{
			List<String> laExpectedReturnList = new ArrayList<String>();
			laExpectedReturnList.add("");
			String lsSeparator = ",";
			String loValue = "";

			ArrayListStringResultHandler loArrayListStringResultHandler = new ArrayListStringResultHandler();
			List<String> loResult = loArrayListStringResultHandler.formatRuleResult(lsSeparator, loValue);

			assertNotNull(loResult);
			assertTrue(loResult.equals(laExpectedReturnList));
		}
		catch (Exception aoEx)
		{
			throw new ApplicationException(
					"Exception occured while Unit Testing - testFormatReturnTypeMismatchThroughException().", aoEx);
		}
	}

}
