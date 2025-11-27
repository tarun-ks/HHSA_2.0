package com.nyc.hhs.handlersTest.ruleresulthandlersTest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.handlers.ruleresulthandlers.ArrayListIntegerResultHandler;

/**
 * This is a test class for ArrayListIntegerResultHandler class
 * 
 */
public class ArrayListIntegerResultHandlerTest {

	/**
	 * This method tests for Integer type list return from String value splitting
	 * through a given separator.
	 * */
	@Test
	public void testformatRuleResultAllWellReturnTrue()
			throws ApplicationException {
		try {
			List<Integer> laExpectedReturnList = new ArrayList<Integer>();
			laExpectedReturnList.add(23);
			laExpectedReturnList.add(3);
			laExpectedReturnList.add(2);

			String lsSeparator = ",";
			String loValue = "23, 3, 2";

			ArrayListIntegerResultHandler loArrayListIntegerResultHandler = new ArrayListIntegerResultHandler();
			List<Integer> loResult = loArrayListIntegerResultHandler
					.formatRuleResult(lsSeparator, loValue);

			assertNotNull(loResult);
			assertTrue(loResult.equals(laExpectedReturnList));

		} catch (Exception aoEx) {
			throw new ApplicationException(
					"Exception occured while Unit Testing - testFormatReturnAllWellReturnTrue().",
					aoEx);
		}
	}

	/**
	 * This method tests for an empty Integer List if the value String gives a
	 * blank string after splitting through the separator provided
	 * */
	@Test
	public void testformatRuleResultValueNotSetReturnBlankList()
			throws ApplicationException {
		try {
			List<Integer> laExpectedReturnList = new ArrayList<Integer>();

			String lsSeparator = ",";
			String loValue = "";

			ArrayListIntegerResultHandler loArrayListIntegerResultHandler = new ArrayListIntegerResultHandler();
			List<Integer> loResult = loArrayListIntegerResultHandler
					.formatRuleResult(lsSeparator, loValue);

			assertNotNull(loResult);
			assertTrue(loResult.equals(laExpectedReturnList));
		} catch (Exception aoEx) {
			throw new ApplicationException(
					"Exception occured while Unit Testing - testFormatReturnTypeMismatchThroughException().",
					aoEx);
		}
	}

	/**
	 * This method tests for an Exception expectation if the value String gives
	 * a non Integer value after splitting through the separator provided
	 * */
	@Test
	public void testformatRuleResultTypeMismatchThrowsException()
			throws ApplicationException {
		try {
			List<Integer> laExpectedReturnList = new ArrayList<Integer>();
			laExpectedReturnList.add(23);
			laExpectedReturnList.add(3);
			laExpectedReturnList.add(2);

			String lsSeparator = ",";
			String loValue = "Hello23, 3, 2";

			ArrayListIntegerResultHandler loArrayListIntegerResultHandler = new ArrayListIntegerResultHandler();
			List<Integer> loResult = loArrayListIntegerResultHandler
					.formatRuleResult(lsSeparator, loValue);

			assertNotNull(loResult);
			assertTrue(loResult.equals(laExpectedReturnList));
		} catch (ApplicationException loAe) {
			assertTrue(true);
		} catch (Exception aoEx) {
			throw new ApplicationException(
					"Exception occured while Unit Testing - testFormatReturnTypeMismatchThroughException().",
					aoEx);
		}
	}
	/**
	 * This method tests for an Exception expectation if the value String gives
	 * a non Integer value after splitting through the separator provided
	 * */
	@Test
	public void testformatRuleResultTypeMismatchThrowsExceptionCase2()
			throws ApplicationException {
		try {
			List<Integer> laExpectedReturnList = new ArrayList<Integer>();
			laExpectedReturnList.add(23);
			laExpectedReturnList.add(3);
			laExpectedReturnList.add(2);

			String lsSeparator = "@";
			String loValue = "Hello23, 3, 2";

			ArrayListIntegerResultHandler loArrayListIntegerResultHandler = new ArrayListIntegerResultHandler();
			List<Integer> loResult = loArrayListIntegerResultHandler
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
	 * This method tests for an empty Integer List if the value String gives a
	 * blank string after splitting through the separator provided
	 * */
	@Test
	public void testformatRuleResultValueNotSetReturnBlankListCase2()
			throws ApplicationException {
		try {
			List<Integer> laExpectedReturnList = new ArrayList<Integer>();

			String lsSeparator = ".";
			String loValue = "";

			ArrayListIntegerResultHandler loArrayListIntegerResultHandler = new ArrayListIntegerResultHandler();
			List<Integer> loResult = loArrayListIntegerResultHandler
					.formatRuleResult(lsSeparator, loValue);

			assertNotNull(loResult);
			assertTrue(loResult.equals(laExpectedReturnList));
		} catch (Exception e) {
			throw new ApplicationException(
					"Exception occured while Unit Testing - testFormatReturnTypeMismatchThroughException().",
					e);
		}
	}
}
