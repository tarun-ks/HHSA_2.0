package com.nyc.hhs.exception;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import com.nyc.hhs.frameworks.logger.LogInfo;

/**
 * This class will be used to wrap all Exceptions. Service class will create an
 * instance of this class and will provider error message.
 * 
 */

public class ApplicationException extends Exception
{

	private static final long serialVersionUID = 1L;
	private static final LogInfo LOG_OBJECT = new LogInfo(ApplicationException.class);
	public static final int DEFAULT = -1;
	private String msMessage = null;
	private Throwable moThrowable = null;
	private Map moHMContextData = new HashMap();

	/**
	 * This is a method for string exception message.
	 * 
	 * @param asMessage message string
	 */
	public ApplicationException(String asMessage)
	{
		msMessage = asMessage;
	}

	/**
	 * This is a method for Throwable root cause
	 * 
	 * @return moThrowable Throwable Object
	 */
	public Throwable getRootCause()
	{
		if (moThrowable == null)
		{
			return this;
		}
		else
		{
			return moThrowable;
		}
	}

	/**
	 * This is a method for string exception message and throwable exception
	 * 
	 * @param asMessage message string
	 * @param aoThrowable Throwable Object
	 */
	public ApplicationException(String asMessage, Throwable aoThrowable)
	{
		msMessage = asMessage;
		moThrowable = aoThrowable;
	}

	/**
	 * This is a method for string exception message ,throwable exception and
	 * error codes
	 * 
	 * @param aiErrorCode error code
	 * @param asMessage message string
	 * @param aoThrowable Throwable Object
	 */
	public ApplicationException(int aiErrorCode, String asMessage, Throwable aoThrowable)
	{
		msMessage = asMessage;
		moThrowable = aoThrowable;
	}

	/**
	 * This is a method for string exception message and add context data
	 * 
	 * @param asMessage message string
	 * @param asDataKey string data key
	 * @param asDataValue string data value
	 */
	public ApplicationException(String asMessage, String asDataKey, String asDataValue)
	{
		msMessage = asMessage;
		addContextData(asDataKey, asDataValue);
	}

	/**
	 * This is a method for string exception message ,throwable exception, error
	 * codes and context data
	 * 
	 * @param aiErrorCode error code
	 * @param asMessage message string
	 * @param aoThrowable Throwable Object
	 * @param asDataKey string data key
	 * @param asDataValue string data value
	 */
	public ApplicationException(int aiErrorCode, String asMessage, Throwable aoThrowable, String asDataKey,
			String asDataValue)
	{
		msMessage = asMessage;
		moThrowable = aoThrowable;
		addContextData(asDataKey, asDataValue);
	}

	/**
	 * This is a method for string exception message and add Context Data
	 * 
	 * @param asMessage message string
	 * @param aoThrowable Throwable Object
	 * @param asDataKey string data key
	 * @param asDataValue string data value
	 */
	public ApplicationException(String asMessage, Throwable aoThrowable, String asDataKey, String asDataValue)
	{
		msMessage = asMessage;
		moThrowable = aoThrowable;
		addContextData(asDataKey, asDataValue);
	}

	/**
	 * This is a method to set context data.
	 * 
	 * @param aoHMContextData map of context data
	 */
	public void setContextData(Map aoHMContextData)
	{
		if (aoHMContextData != null)
		{
			moHMContextData.putAll(aoHMContextData);
		}
	}

	/**
	 * This is a method to add context data.
	 * 
	 * @param asDataKey string data key
	 * @param aoDataValue string data value
	 */
	public void addContextData(String asDataKey, Object aoDataValue)
	{
		if (asDataKey != null)
		{
			moHMContextData.put(asDataKey, aoDataValue);
		}
	}

	/**
	 * Method to get context data
	 * 
	 * @return moHMContextData map of context data
	 */
	public Map getContextData()
	{
		return moHMContextData;
	}

	/**
	 * This is a method to create message.
	 * 
	 * @return lsBfMessage message string
	 */
	private String createMessage()
	{
		StringBuffer lsBfMessage = new StringBuffer(msMessage);
		if (moThrowable != null)
		{
			lsBfMessage.append("\nInternal Exception Message: ").append(moThrowable.getMessage());
		}
		return lsBfMessage.toString();
	}

	/**
	 * This is a get message.
	 * 
	 * @return msMessage string messga
	 */
	public String getMessage()
	{
		return msMessage;
	}

	public String toString()
	{
		return createMessage();
	}

	/**
	 * Method to get Stack Trace as String.
	 * 
	 * @return getStackTraceAsString string stack trace
	 */
	public String getStackTraceAsString()
	{
		ByteArrayOutputStream loBAOS = new ByteArrayOutputStream();
		PrintStream loPS = new PrintStream(loBAOS);
		if (moThrowable != null)
		{
			moThrowable.printStackTrace(loPS);
			LOG_OBJECT.Info("moThrowable is not null" + moThrowable);
		}
		else
		{
			this.printStackTrace(loPS);
		}
		String lsStackTrace = loBAOS.toString();
		try
		{
			loBAOS.close();
			loPS.close();
		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Error occured in ApplicationException method:getStackTraceAsString()", loEx);
		}
		return lsStackTrace;
	}

	/**
	 * Method to get Stack Trace for Throwable.
	 * 
	 * @param aoTh Throwable Object
	 * @return lsStackTrace stack trace string
	 */
	public static String getStackTraceForThrowable(Throwable aoTh)
	{
		ByteArrayOutputStream loBAOS = new ByteArrayOutputStream();
		PrintStream loPS = new PrintStream(loBAOS);
		if (aoTh != null)
		{
			aoTh.printStackTrace(loPS);
			LOG_OBJECT.Info("aoTh is not null" + aoTh);
		}
		String lsStackTrace = loBAOS.toString();
		try
		{
			loBAOS.close();
			loPS.close();
		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Error occured in ApplicationException method:getStackTraceForThrowable()", loEx);
		}
		return lsStackTrace;
	}

}
