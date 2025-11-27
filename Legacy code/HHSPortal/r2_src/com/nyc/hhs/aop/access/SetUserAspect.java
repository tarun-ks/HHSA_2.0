package com.nyc.hhs.aop.access;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.UserThreadLocal;

@Aspect
public abstract class SetUserAspect
{
	/**
	 * Log object to record all logs
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(SetUserAspect.class);

	@Pointcut
	abstract void setUnsetUser();

	/**
	 * This method is point cut to all render method of all controllers and will
	 * be responsible for removing all locks taken by user
	 * <ul>
	 * <li>1. get the object returned by render method</li>
	 * <li>2. get request from render method arguments</li>
	 * <li>3. invoke removeUser method to remove all locks taken by user</li>
	 * </ul>
	 * @param aoPrecedingJointPoint - Proceeding join point object
	 * @return object been returned by method on which point cut is applied
	 * @throws Throwable - in case of any exception occurs
	 */
	@Around("setUnsetUser()")
	public Object setUnsetUser(ProceedingJoinPoint aoPrecedingJointPoint) throws Throwable
	{
		LOG_OBJECT.Info("Start setUnsetUser method of SetUserAspect Class");

		Object[] loArgumentArray = aoPrecedingJointPoint.getArgs();
		PortletRequest loRequest = null;
		Object loRet = null;
		if (loArgumentArray != null)
		{
			for (Object loArgObject : loArgumentArray)
			{
				if (loArgObject instanceof PortletRequest)
				{
					loRequest = (PortletRequest) loArgObject;
				}
			}
		}
		if (loRequest == null)
		{
			loRet = aoPrecedingJointPoint.proceed();
		}
		else
		{
			HttpSession loHttpSession = ((HttpServletRequest) loRequest
					.getAttribute(HHSConstants.JAVAX_SERVLET_REQUEST)).getSession();
			String lsUserIdThreadLocal = (String) loHttpSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID);
			UserThreadLocal.setUser(lsUserIdThreadLocal);
			loRet = aoPrecedingJointPoint.proceed();
			UserThreadLocal.unSet();
		}
		LOG_OBJECT.Info("End setUnsetUser method of SetUserAspect Class");
		return loRet;
	}

}