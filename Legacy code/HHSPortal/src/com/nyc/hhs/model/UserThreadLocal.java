package com.nyc.hhs.model;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.util.CommonUtil;

/**
 * This is a bean class for user thread local.
 */
public class UserThreadLocal
{

	private static final ThreadLocal<String> MO_USER_INFO = new ThreadLocal<String>();
	private static final ThreadLocal<String> MO_TIME_STAMP = new ThreadLocal<String>();
	private static final LogInfo LOG_OBJECT = new LogInfo(UserThreadLocal.class);

	public static void setUser(String asUser)
	{
		MO_USER_INFO.set(asUser + " transaction id [" + System.currentTimeMillis() + "]");
		MO_TIME_STAMP.set(CommonUtil.getCurrentTimeInMilliSec());
		try
		{
			LOG_OBJECT.Debug("Start Thread Local " + MO_TIME_STAMP.get());
		}
		catch (ApplicationException aoException)
		{
			LOG_OBJECT.Error("ApplicationException occurred in setUser ", aoException);
		}
	}

	public static String getUser()
	{
		return MO_USER_INFO.get();
	}

	public static void unSet()
	{
		String lsServiceEndTime = CommonUtil.getCurrentTimeInMilliSec();
		float liTimediff1 = CommonUtil.timeDiff(CommonUtil.getItemDateInMIlisec(MO_TIME_STAMP.get()),
				CommonUtil.getItemDateInMIlisec(lsServiceEndTime));
		try
		{
			LOG_OBJECT.Debug("Finished Thread Local " + liTimediff1);
		}
		catch (ApplicationException aoException)
		{
			LOG_OBJECT.Error("ApplicationException occurred in setUser ", aoException);
		}
		finally
		{
			MO_USER_INFO.remove();
			MO_TIME_STAMP.remove();
		}

	}

	@Override
	public String toString()
	{
		return "UserThreadLocal []";
	}
}
