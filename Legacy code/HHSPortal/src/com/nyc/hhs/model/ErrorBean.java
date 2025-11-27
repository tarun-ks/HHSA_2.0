package com.nyc.hhs.model;

/**
 * This class is a bean which maintains the Error information.
 *
 */

public class ErrorBean
{
	
	private static String errorMessage;
	private static String errorCode;
	private static String errorCause;
	private static String errorCausedForProperty;
	
	
	
	public static String getErrorMessage()
	{
		return errorMessage;
	}
	public static void setErrorMessage(String errorMessage)
	{
		ErrorBean.errorMessage = errorMessage;
	}
	public static String getErrorCode()
	{
		return errorCode;
	}
	public static void setErrorCode(String errorCode)
	{
		ErrorBean.errorCode = errorCode;
	}
	public static String getErrorCause()
	{
		return errorCause;
	}
	public static void setErrorCause(String errorCause)
	{
		ErrorBean.errorCause = errorCause;
	}
	public static String getErrorCausedForProperty()
	{
		return errorCausedForProperty;
	}
	public static void setErrorCausedForProperty(String errorCausedForProperty)
	{
		ErrorBean.errorCausedForProperty = errorCausedForProperty;
	}
	@Override
	public String toString() {
		return "ErrorBean []";
	}

}
