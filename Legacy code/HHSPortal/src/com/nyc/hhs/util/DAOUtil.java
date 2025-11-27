package com.nyc.hhs.util;

import java.lang.reflect.Method;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;

/**
 *  This utility class has standard DAO method that act as fascade for all 
 *  services performing DB operations. This class uses java reflection to 
 *  fetch required mappers and to invoke its operations.
 *  
 */

public class DAOUtil
{
	private static final LogInfo LOG_OBJECT = new LogInfo(DAOUtil.class);
	/** 
	 *  This method  act as fascade for all 
	 *  services performing DB operations. This class uses java reflection to 
	 *  fetch required mappers and to invoke its operations.
	 *  
	 * @param aoSession
	 * 				SQL SESSION
	 * @param aoParameter
	 * 				This is the required parameter that is passed to mapper method for query execution
	 * @param asClassName
	 * 				This the qualified class name of the Mapper Interface containing mapper method definitions
	 * @param asMethodName
	 * 				This is the name of mapper method that needs to be called for making DB query
	 * @param asParamaterType
	 * 				This is the type of required Parameter that we pass in ‘aoRequiredParameters’
	 * @return Object
	 * 				The result of this can be type cast to any object
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public static Object masterDAO(SqlSession aoSession, Object aoParameter, String asClassName, String asMethodName, String asParamaterType)
			throws ApplicationException
	{
		// Start QC 8998 R 8.8  remove password from logs
		String param = CommonUtil.maskPassword(aoParameter);
		
		//LOG_OBJECT.Debug("Start Method:: masterDAO params:" + aoParameter + " asClassName :: "+ asClassName+ " asMethodName :: "+ asMethodName);
		LOG_OBJECT.Debug("Start Method:: masterDAO params:" + param + " asClassName :: "+ asClassName+ " asMethodName :: "+ asMethodName);
		// End QC 8998 R 8.8  remove password from logs
		
		Object loResultObject = null;
		try
		{
			String lsTransactionStartTime = CommonUtil.getCurrentTimeInMilliSec();
		
			Class loClassName = Class.forName(asClassName);
			Class[] loCInputParamType = new Class[1];
			if (null != asParamaterType)
			{
				loCInputParamType[0] = Class.forName(asParamaterType);
				Method loClassMethod = loClassName.getMethod(asMethodName, loCInputParamType);
				Object loObj = aoSession.getMapper(Class.forName(asClassName));
				LOG_OBJECT.Debug("End Method:: masterDAO");
				loResultObject =  loClassMethod.invoke(loObj, aoParameter);
			}
			else
			{
				Method loClassMethod = loClassName.getMethod(asMethodName);
				Object loObj = aoSession.getMapper(Class.forName(asClassName));
				LOG_OBJECT.Debug("End Method:: masterDAO");
				loResultObject =  loClassMethod.invoke(loObj);
			}
			
			String lsTransactionEndTime = CommonUtil.getCurrentTimeInMilliSec();
			float liTimediff = CommonUtil.timeDiff(CommonUtil.getItemDateInMIlisec(lsTransactionStartTime),
					CommonUtil.getItemDateInMIlisec(lsTransactionEndTime));
			if (liTimediff > 1)
			{
				LOG_OBJECT.Error("!!!!!Ending DAO UTIL, TIME LAPSED," + liTimediff + ", Mapper Name,"
						+ asClassName + ", Method Name," + asMethodName);
			}
		}
		catch (Exception loTh)
		{
			if (!(loTh.getCause() instanceof ApplicationException))
			{
				LOG_OBJECT.Error("Error occured executing transaction in master DAO",loTh);
				ApplicationException loEx = new ApplicationException("Error occured while executing service: ", loTh);
				throw loEx;
			}
			else
			{
				LOG_OBJECT.Error("End Method:: masterDAO error");
				ApplicationException loEx = new ApplicationException("Error occured while executing service: ", loTh);
				throw loEx;
			}
		}
		LOG_OBJECT.Debug("End Method:: masterDAO params:" + param + " asClassName :: "+ asClassName+ " asMethodName :: "+ asMethodName  );
		
		return loResultObject;
	}

}
