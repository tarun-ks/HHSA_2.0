package com.nyc.hhs.handlersTest.ruleresulthandlersTest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.handlers.ruleresulthandlers.ArrayListFloatResultHandler;

/**
 * This is a test class for ArrayListFloatResultHandler class
 * 
 */
public class ArrayListFloatResultHandlerTest
{

	/**
	 * This method tests for Float type list return from String value splitting
	 * through a given separator.
	 * */
	@Test
	public void testformatRuleResultAllWellReturnTrue() throws ApplicationException
	{
		try
		{
			List<Float> laExpectedReturnList = new ArrayList<Float>();
			laExpectedReturnList.add(23.6F);
			laExpectedReturnList.add(3.6F);
			laExpectedReturnList.add(2.0F);

			String lsSeparator = ",";
			String loValue = "23.6, 3.6, 2.0";

			ArrayListFloatResultHandler loArrayListFloatResultHandler = new ArrayListFloatResultHandler();
			List<Float> loResult = loArrayListFloatResultHandler.formatRuleResult(lsSeparator, loValue);

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
	 * This method tests for an empty Float List if the value String gives a
	 * blank string after splitting through the separator provided
	 * */
	@Test
	public void testformatRuleResultValueNotSetReturnBlankList() throws ApplicationException
	{
		try
		{
			List<Float> laExpectedReturnList = new ArrayList<Float>();

			String lsSeparator = ",";
			String loValue = "";

			ArrayListFloatResultHandler loArrayListFloatResultHandler = new ArrayListFloatResultHandler();
			List<Float> loResult = loArrayListFloatResultHandler.formatRuleResult(lsSeparator, loValue);

			assertNotNull(loResult);
			assertTrue(loResult.equals(laExpectedReturnList));
		}
		catch (Exception aoEx)
		{
			throw new ApplicationException(
					"Exception occured while Unit Testing - testFormatReturnTypeMismatchThroughException().", aoEx);
		}
	}

	/**
	 * This method tests for an Exception expectation if the value String gives
	 * a non Float value after splitting through the separator provided
	 * */
	@Test
	public void testformatRuleResultTypeMismatchThrowsException() throws ApplicationException
	{
		try
		{
			List<Float> laExpectedReturnList = new ArrayList<Float>();
			laExpectedReturnList.add(23.6F);
			laExpectedReturnList.add(3.6F);
			laExpectedReturnList.add(2.0F);

			String lsSeparator = ",";
			String loValue = "Hello23.6, 3.6, 2.0";

			ArrayListFloatResultHandler loArrayListFloatResultHandler = new ArrayListFloatResultHandler();
			List<Float> loResult = loArrayListFloatResultHandler.formatRuleResult(lsSeparator, loValue);

			assertNotNull(loResult);
			assertTrue(loResult.equals(laExpectedReturnList));
		}
		catch (ApplicationException loAe)
		{
			assertTrue(true);
		}
		catch (Exception aoEx)
		{
			throw new ApplicationException(
					"Exception occured while Unit Testing - testFormatReturnTypeMismatchThroughException().", aoEx);
		}
	}
	/**
	 * This method tests for an Exception expectation if the value String gives
	 * a non Float value after splitting through the separator provided
	 * */
	@Test
	public void testformatRuleResultTypeMismatchThrowsExceptionCase2()
			throws ApplicationException {
		try {
			List<Float> laExpectedReturnList = new ArrayList<Float>();
			laExpectedReturnList.add(23.6F);
			laExpectedReturnList.add(3.6F);
			laExpectedReturnList.add(2.0F);

			String lsSeparator = ",";
			String loValue = "Hello23.6, 3.6, 0.0";

			ArrayListFloatResultHandler loArrayListFloatResultHandler = new ArrayListFloatResultHandler();
			List<Float> loResult = loArrayListFloatResultHandler
					.formatRuleResult(lsSeparator, loValue);

			assertNotNull(loResult);
			assertTrue(loResult.equals(laExpectedReturnList));
		} catch (ApplicationException loAe) {
			assertTrue(true);
		} catch (Exception e) {
			throw new ApplicationException(
					"Exception occured while Unit Testing - testFormatReturnTypeMismatchThroughException().",
					e);
		}
	}
	/**
	 * This method tests for an Exception expectation if the value String gives
	 * a non Float value after splitting through the separator provided
	 * */
	@Test
	public void testformatRuleResultTypeMismatchThrowsExceptionCase3()
			throws ApplicationException {
		try {
			List<Float> laExpectedReturnList = new ArrayList<Float>();
			laExpectedReturnList.add(23.6F);
			laExpectedReturnList.add(3.6F);
			laExpectedReturnList.add(2.0F);

			String lsSeparator = ",";
			String loValue = "Hello0.6, 3.0, 0.0";

			ArrayListFloatResultHandler loArrayListFloatResultHandler = new ArrayListFloatResultHandler();
			List<Float> loResult = loArrayListFloatResultHandler
					.formatRuleResult(lsSeparator, loValue);

			assertNotNull(loResult);
			assertTrue(loResult.equals(laExpectedReturnList));
		} catch (ApplicationException loAe) {
			assertTrue(true);
		} catch (Exception e) {
			throw new ApplicationException(
					"Exception occured while Unit Testing - testFormatReturnTypeMismatchThroughException().",
					e);
		}
	}

}
