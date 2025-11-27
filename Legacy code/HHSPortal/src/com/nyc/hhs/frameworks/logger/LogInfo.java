package com.nyc.hhs.frameworks.logger;

//import org.apache.log4j.Appender;
//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//log4j2 - do not remove! waiting for Java 7 
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.apache.logging.log4j.core.Appender;
//import org.apache.logging.log4j.core.LoggerContext;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.model.UserThreadLocal;

/**
 * This class is a wrapper on Log4J. This class provide constructor to remove
 * database appender.
 * 
 */

public class LogInfo
{

	private Logger moLog = null;

	/**
	 * parameterized constructor for the loginfo class which will initialize the
	 * logger class
	 * 
	 * @param aoClass class for which logger to be initialized
	 * @param abRemoveDBAppender boolean value to remove DB appender
	 */
	public LogInfo(Class aoClass, boolean abRemoveDBAppender)
	{
		try
		{
			//addRemoveAppender(abRemoveDBAppender);
			//this.moLog = Logger.getLogger(aoClass);
			//this.moLog = LogManager.getLogger(aoClass); //log4j2
			this.moLog =LoggerFactory.getLogger(aoClass);
			
		}
		catch (Exception aoExp)
		{
			this.moLog = LoggerFactory.getLogger(aoClass);
			//this.moLog = LogManager.getLogger(aoClass); //log4j2
			moLog.error("Exception occurred while executing LogInfo:", aoExp);
		}
	}

	/**
	 * parameterized constructor for the loginfo class which will initialize the
	 * logger class
	 * 
	 * @param aoClass class for which logger to be initialized
	 */
	public LogInfo(Class aoClass)
	{
		this.moLog = LoggerFactory.getLogger(aoClass);
		//this.moLog = LogManager.getLogger(aoClass); //log4j2
	}

	/**
	 * This method is used to set the debug level for the logger
	 * 
	 * @param asDebugMessage message to be logged in log file
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	public final void Debug(String asDebugMessage) throws ApplicationException
	{
		try
		{
			if (UserThreadLocal.getUser() != null)
			{
				asDebugMessage = "UserId :: " + UserThreadLocal.getUser() + " --> " + asDebugMessage;
			}
			moLog.debug(asDebugMessage);
		}
		catch (Exception aoExp)
		{
			throw new ApplicationException(aoExp.getMessage(), aoExp);
		}
	}

	/**
	 * This is an overloaded method which is used to set the debug level for the
	 * logger
	 * 
	 * @param asDebugMessage message to be logged in log file
	 * @param aoDebug throwable object for which log to be appended
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	public final void Debug(String asDebugMessage, Throwable aoDebug) throws ApplicationException
	{
		try
		{
			asDebugMessage = "UserId :: " + UserThreadLocal.getUser() + " --> " + asDebugMessage;
			moLog.debug(asDebugMessage, aoDebug);
		}
		catch (Exception aoExp)
		{
			throw new ApplicationException(aoExp.getMessage(), aoExp);
		}
	}

	/**
	 * This method is used to set the debug level for the logger
	 * 
	 * @param asDebugMessage message to be logged in log file
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	public final void Debug(String asDebugMessage, String asTransactionId) throws ApplicationException
	{
		try
		{
			asDebugMessage = "UserId :: " + UserThreadLocal.getUser() + " --> TransactionId  :: " + asTransactionId
					+ " --> " + asDebugMessage;
			moLog.debug(asDebugMessage);
		}
		catch (Exception aoExp)
		{
			throw new ApplicationException(aoExp.getMessage(), aoExp);
		}
	}

	/**
	 * This is an overloaded method which is used to set the debug level for the
	 * logger
	 * 
	 * @param asDebugMessage message to be logged in log file
	 * @param aoDebug throwable object for which log to be appended
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	public final void Debug(String asDebugMessage, Throwable aoDebug, String asTransactionId)
			throws ApplicationException
	{
		try
		{
			asDebugMessage = "UserId :: " + UserThreadLocal.getUser() + " --> TransactionId  :: " + asTransactionId
					+ " --> " + asDebugMessage;
			moLog.debug(asDebugMessage, aoDebug);
		}
		catch (Exception aoExp)
		{
			throw new ApplicationException(aoExp.getMessage(), aoExp);
		}
	}

	/**
	 * This is an overloaded method which is used to set the info level for the
	 * logger
	 * 
	 * @param asInfoMessage message to be logged in log file
	 * @param abInfo boolean value to confirm the type
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	public final void Info(String asInfoMessage, boolean abInfo) throws ApplicationException
	{
		try
		{
			if (UserThreadLocal.getUser() != null)
			{
				asInfoMessage = "UserId :: " + UserThreadLocal.getUser() + " --> " + asInfoMessage;
			}
			moLog.info(asInfoMessage);
		}
		catch (Exception aoExp)
		{
			throw new ApplicationException(aoExp.getMessage(), aoExp);
		}
	}

	/**
	 * This is an overloaded method which is used to set the info level for the
	 * logger
	 * 
	 * @param asInfoMessage message to be logged in log file
	 */
	public final void Info(String asInfoMessage)
	{
		try
		{
			if (UserThreadLocal.getUser() != null)
			{
				asInfoMessage = "UserId :: " + UserThreadLocal.getUser() + " --> " + asInfoMessage;
			}
			moLog.info(asInfoMessage);
		}
		catch (Exception aoExp)
		{
			moLog.error("Exception occurred while executing LogInfo:", aoExp);
		}
	}

	/**
	 * This is an overloaded method which is used to set the warning level for
	 * the logger
	 * 
	 * @param asWarnMessage message to be logged in log file
	 * @param abWarning boolean value to confirm the type
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	public final void Warn(String asWarnMessage, boolean abWarning) throws ApplicationException
	{
		try
		{
			if (UserThreadLocal.getUser() != null)
			{
				asWarnMessage = "UserId :: " + UserThreadLocal.getUser() + " --> " + asWarnMessage;
			}
			moLog.warn(asWarnMessage);
		}
		catch (Exception aoExp)
		{
			throw new ApplicationException(aoExp.getMessage(), aoExp);
		}
	}

	/**
	 * This is an overloaded method which is used to set the warning level for
	 * the logger
	 * 
	 * @param asWarnMessage message to be logged in log file
	 * @param aoWarn throwable object for which warning to be appended in log
	 * @param abWarning boolean value to confirm the type
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	public final void Warn(String asWarnMessage, Throwable aoWarn, boolean abWarn) throws ApplicationException
	{
		try
		{
			if (UserThreadLocal.getUser() != null)
			{
				asWarnMessage = "UserId :: " + UserThreadLocal.getUser() + " --> " + asWarnMessage;
			}
			moLog.warn(asWarnMessage, aoWarn);
		}
		catch (Exception aoExp)
		{
			throw new ApplicationException(aoExp.getMessage(), aoExp);
		}
	}

	/**
	 * This is an overloaded method which is used to set the info Error for the
	 * logger
	 * 
	 * @param asErrorMessage message to be logged in log file
	 */
	public final void Error(String asErrorMessage)
	{
		try
		{
			asErrorMessage = "UserId :: " + UserThreadLocal.getUser() + " --> " + asErrorMessage;
			moLog.error(asErrorMessage);
		}
		catch (Exception aoExp)
		{
			moLog.error("Exception occurred while executing LogInfo:", aoExp);
		}
	}

	/**
	 * This method is used to set the debug level for the logger
	 * 
	 * @param asDebugMessage message to be logged in log file
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	public final void Error(String asDebugMessage, String asTransactionId) throws ApplicationException
	{
		try
		{
			asDebugMessage = "UserId :: " + UserThreadLocal.getUser() + " --> TransactionId  :: " + asTransactionId
					+ " --> " + asDebugMessage;
			moLog.error(asDebugMessage);
		}
		catch (Exception aoExp)
		{
			throw new ApplicationException(aoExp.getMessage(), aoExp);
		}
	}

	/**
	 * This is an overloaded method which is used to set the Error level for the
	 * logger
	 * 
	 * @param asErrorMessage message to be logged in log file
	 * @param abError boolean value to confirm the type
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	public final void Error(String asErrorMessage, boolean abError) throws ApplicationException
	{
		try
		{
			asErrorMessage = "UserId :: " + UserThreadLocal.getUser() + " --> " + asErrorMessage;
			moLog.error(asErrorMessage);
		}
		catch (Exception aoExp)
		{
			throw new ApplicationException(aoExp.getMessage(), aoExp);
		}
	}

	/**
	 * This is an overloaded method which is used to set the Error level for the
	 * logger
	 * 
	 * @param asErrorMessage message to be logged in log file
	 * @param aoError throwable object for which error to be appended in the log
	 * @param abError boolean value to confirm the type
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	public final void Error(String asErrorMessage, Throwable aoError, boolean abError) throws ApplicationException
	{
		try
		{
			asErrorMessage = "UserId :: " + UserThreadLocal.getUser() + " --> " + asErrorMessage;
			moLog.error(asErrorMessage, aoError);
		}
		catch (Exception aoExp)
		{
			throw new ApplicationException(aoExp.getMessage(), aoExp);
		}
	}

	/**
	 * This is an overloaded method which is used to set the Error level for the
	 * logger
	 * 
	 * @param asErrorMessage message to be logged in log file
	 * @param aoError throwable object for which error to be appended in the log
	 * 
	 */
	public final void Error(String asErrorMessage, Throwable aoError)
	{
		try
		{
			asErrorMessage = "UserId :: " + UserThreadLocal.getUser() + " --> " + asErrorMessage;
			moLog.error(asErrorMessage, aoError);
		}
		catch (Exception aoExp)
		{
			moLog.error("Exception occurred while executing LogInfo:", aoExp);
		}
	}

	/**
	 * This is an overloaded method which is used to set the Fatal level for the
	 * logger
	 * 
	 * @param asFatalMessage message to be logged in log file
	 * @param abFatal boolean value to confirm the type
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	public final void Fatal(String asFatalMessage, boolean abFatal) throws ApplicationException
	{
		try
		{
			if (UserThreadLocal.getUser() != null)
			{
				asFatalMessage = "UserId :: " + UserThreadLocal.getUser() + " --> " + asFatalMessage;
			}
			//moLog.fatal(asFatalMessage);
			moLog.error(asFatalMessage);
		}
		catch (Exception aoExp)
		{
			throw new ApplicationException(aoExp.getMessage(), aoExp);
		}
	}

	/**
	 * This is an overloaded method which is used to set the Fatal level for the
	 * logger
	 * 
	 * @param asFatalMessage message to be logged in log file * @param aoFatal
	 *            throwable object for which fatal to be appended in the log
	 * @param abFatal boolean value to confirm the type
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	public final void Fatal(String asFatalMessage, Throwable aoFatal, boolean abFatal) throws ApplicationException
	{
		try
		{
			if (UserThreadLocal.getUser() != null)
			{
				asFatalMessage = "UserId :: " + UserThreadLocal.getUser() + " --> " + asFatalMessage;
			}
			//moLog.fatal(asFatalMessage, aoFatal);
			moLog.error(asFatalMessage, aoFatal);
		}
		catch (Exception aoExp)
		{
			throw new ApplicationException(aoExp.getMessage(), aoExp);
		}
	}

	/**
	 * This method is used to remove the appender from the logger
	 * 
	 * @param abRemoveDBAppender boolean value to append the jdbc logger
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	/*public void addRemoveAppender(boolean abRemoveDBAppender) throws ApplicationException
	{
		try
		{
			if (abRemoveDBAppender)
			{ 				
				Logger loLogger = Logger.getRootLogger();
				Appender loAppender = loLogger.getAppender("JDBC");
				loLogger.removeAppender(loAppender);
				 log4j2
				final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
				final Configuration config = ctx.getConfiguration();
				config.getRootLogger().removeAppender("JDBC");
				ctx.updateLoggers();
				
				
			}
		}
		catch (Exception aoExp)
		{
			throw new ApplicationException(aoExp.getMessage(), aoExp);
		}
	}
*/
	/**
	 * This method is used to check if debug option is enabled
	 */
	public boolean isDebugEnabled()
	{
		return this.moLog.isDebugEnabled();
	}

	/**
	 * This method is used to check if info option is enabled
	 */
	public boolean isInfoEnabled()
	{
		return this.moLog.isInfoEnabled();
	}

	/**
	 * This method is used to check if trace option is enabled
	 */
	public boolean isTraceEnabled()
	{
		return this.moLog.isTraceEnabled();
	}

}
