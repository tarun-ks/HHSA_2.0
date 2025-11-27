package com.nyc.hhs.aop.log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.util.CommonUtil;

@Aspect
public abstract class LoggingAspect
{

	/**
	 * Log object to record all logs
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(LoggingAspect.class);

	@Pointcut
	abstract void serviceMethods();

	/**
	 * This method takes care of the time taken to execute a particular method
	 * while execution With the help of aspect @Around it will surround the
	 * matching joint point for the method invocation.
	 * 
	 * @param pjp
	 * @return
	 * @throws Throwable
	 */
	@Around("serviceMethods()")
	public Object logR1(ProceedingJoinPoint aoPrecedingJointPoint) throws Throwable
	{
		/*
		 * [Start]R7.12.0 QC9311 Minimize Debug
		 
		//if (LOG_OBJECT.isDebugEnabled())
		{
			StringBuffer lsBLog = new StringBuffer();
			lsBLog.append(HHSConstants.ENTERING).append(aoPrecedingJointPoint.getSignature().getDeclaringTypeName())
					.append(HHSConstants.COLON).append(aoPrecedingJointPoint.getSignature().getName())
					.append(HHSConstants.WITH);

			Object[] loArgs = aoPrecedingJointPoint.getArgs();
			if (loArgs != null && loArgs.length >= 1)
			{

				List<Object> loArgsList = Arrays.asList(loArgs);
				for (int liCount = 0; liCount <= loArgsList.size() - 1; liCount++)
				{
					Object loObj = loArgsList.get(liCount);

					lsBLog.append(HHSConstants.SPACE).append(loObj);
					if (liCount != loArgsList.size() - 1)
					{
						lsBLog.append(HHSConstants.COMMA);
					}
				}
			}
			lsBLog.append(HHSConstants.CLOSING_BRACKET);

			LOG_OBJECT.Debug(lsBLog.toString());
		}
*/
		if (LOG_OBJECT.isDebugEnabled())
		{
			LOG_OBJECT.Debug(HHSConstants.ENTERING + aoPrecedingJointPoint.getSignature().getDeclaringTypeName() );
		};		
		/*
		 * [End]R7.12.0 QC9311 Minimize Debug
		 */

		Object loRet = aoPrecedingJointPoint.proceed();

		if (LOG_OBJECT.isDebugEnabled())
		{
			LOG_OBJECT.Debug(HHSConstants.EXITING + aoPrecedingJointPoint.getSignature().getDeclaringTypeName()
					+ HHSConstants.COLON + aoPrecedingJointPoint.getSignature().getName() 
					// [Start]R7.12.0 QC9311 Minimize Debug
					//+ HHSConstants.WITH + loRet 
					// [End]R7.12.0 QC9311 Minimize Debug
					+ HHSConstants.CLOSING_BRACKET);
		}
		return loRet;
	}

	/**
	 * <ul>
	 * <li>The Method is to log the exception that are caused while execution in
	 * the point cut. We are using log.debug to get</li>
	 * the exception logged.
	 * <li>For all the exception in the point cut mentioned it will get the
	 * reason of the exception and the method throwing that exception.
	 * <li><code>logger.debug</code> is used to get the cause message
	 * interpreted and the methods throwing the exception.</li>
	 * </ul>
	 * 
	 * @param ex
	 */
	@AfterThrowing(pointcut = "execution(* com.nyc.hhs..*.*(..))", throwing = "ex")
	public void errorInterceptor(Exception ex)
	{
		Map loMap = new HashMap();
		if ((ex != null) && (ApplicationException.class.isInstance(ex)))
		{
			loMap = ((ApplicationException) ex).getContextData();
			
		}
        // QC 8998 R 8.8.0 do not expose password in logs
		//LOG_OBJECT.Error(HHSConstants.ERROR_TRACED + loMap + ExceptionUtils.getFullStackTrace(ex)
		//		+ HHSConstants.COLON_AOP);
		//*** Start QC 9585 R 8.9 do not expose password for service account in logs
		String param = CommonUtil.maskPassword(loMap);
		LOG_OBJECT.Error(HHSConstants.ERROR_TRACED + param + ExceptionUtils.getFullStackTrace(ex)
						+ HHSConstants.COLON_AOP);
		//*** End QC 9585 R 8.9 do not expose password for service account in logs
		
		LOG_OBJECT.Error(HHSConstants.ERROR_TRACED +  ExceptionUtils.getFullStackTrace(ex)
				+ HHSConstants.COLON_AOP);
	}
}
