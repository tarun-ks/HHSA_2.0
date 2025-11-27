package com.nyc.hhs.aop.trace;

import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.frameworks.logger.LogInfo;

@Aspect
public abstract class TraceAspect
{
	/**
	 * Log object to record all logs
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(TraceAspect.class);

	/**
	 * Method logR1Transactions() with the execution pointcut
	 */
	@Pointcut
	abstract void logR1Transactions();

	/**
	 * Method logR2Transactions() with the execution pointcut
	 */
	@Pointcut
	abstract void logR2Transactions();

	/**
	 * Method logServices() with the execution pointcut
	 */
	@Pointcut
	abstract void logServices();

	/**
	 * This method takes care of the time taken to execute a particular method
	 * while execution With the help of aspect @Around it will surround the
	 * matching joint point for the method invocation.
	 * 
	 * @param aoProceedingJoinPoint
	 * @return
	 * @throws Throwable
	 */
	@Around("logR1Transactions()")
	public Object logR1(ProceedingJoinPoint aoProceedingJoinPoint) throws Throwable
	{
		return logTransaction(aoProceedingJoinPoint);
	}

	@Around("logServices()")
	public Object logServices(ProceedingJoinPoint aoProceedingJoinPoint) throws Throwable
	{

		String lsServiceId = HHSConstants.EMPTY_STRING;
		long llStartTime = 0;

		if (LOG_OBJECT.isInfoEnabled())
		{
			lsServiceId = aoProceedingJoinPoint.getSignature().toLongString();
			llStartTime = (new Date()).getTime();
		}

		Object loReturnVal = aoProceedingJoinPoint.proceed();

		if (LOG_OBJECT.isInfoEnabled())
		{
			long llEndTime = (new Date()).getTime();
			long llExecutionTime = llEndTime - llStartTime;
			LOG_OBJECT.Info(HHSConstants.SERVICE + lsServiceId + HHSConstants.TOOK + llExecutionTime
					+ HHSConstants.MILLISECONDS);
		}
		return loReturnVal;
	}

	@Around("logR2Transactions()")
	public Object logR2(ProceedingJoinPoint aoProceedingJoinPoint) throws Throwable
	{
		return logTransaction(aoProceedingJoinPoint);
	}

	private Object logTransaction(ProceedingJoinPoint aoProceedingJoinPoint) throws Throwable
	{

		String lsTransactionId = HHSConstants.EMPTY_STRING;
		long mlStartTime = 0;

		if (LOG_OBJECT.isInfoEnabled())
		{
			Object[] loArgs = aoProceedingJoinPoint.getArgs();
			lsTransactionId = HHSConstants.EMPTY_STRING + loArgs[1];
			mlStartTime = (new Date()).getTime();
		}

		Object loReturnVal = aoProceedingJoinPoint.proceed();

		if (LOG_OBJECT.isInfoEnabled())
		{
			long llEndTime = (new Date()).getTime();
			long llExecutionTime = llEndTime - mlStartTime;
			LOG_OBJECT.Info(HHSConstants.TRANSACTION + lsTransactionId + HHSConstants.TOOK + llExecutionTime
					+ HHSConstants.MILLISECONDS);
		}
		return loReturnVal;
	}

}
