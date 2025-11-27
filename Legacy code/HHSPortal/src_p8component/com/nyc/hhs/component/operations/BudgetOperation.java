package com.nyc.hhs.component.operations;

import java.lang.reflect.Field;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.ReflectionUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nyc.hhs.component.HHSComponentOperations;
import com.nyc.hhs.constants.HHSP8Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.TaskDetailsBean;


public class BudgetOperation
{

	private static final LogInfo LOG_OBJECT = new LogInfo(HHSComponentOperations.class);

	/**
	 * This method is used to build the TaskDetailsBean
	 * <ul>
	 * <li>This method was updated in R4.</li>
	 * </ul>
	 * 
	 * @param String str Data used to build the TaskDetailsBean
	* @return loTaskDetailsBean - TaskDetailsBean Object
	 * @throws ApplicationException - ApplicationException Object
	 */
	public TaskDetailsBean buildTaskDetailsBean(String asStr) throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		try
		{
			if (null != asStr && !asStr.isEmpty())
			{
				ObjectMapper loMapper = new ObjectMapper();
				loMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				loTaskDetailsBean = loMapper.readValue(asStr, TaskDetailsBean.class);
			}
		}
		catch (Exception aoITExp)
		{
			LOG_OBJECT.Error("Error while building the TaskDetailsBean ", aoITExp);
			throw new ApplicationException("Error occured during executing the buildTaskDetailsBean method", aoITExp);
		}
		return loTaskDetailsBean;
	}

	/**
	 * 
	 * This method converts the String array to TaskDetailbean.
	 * @param aoStrArray
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unused")
	private TaskDetailsBean convertToBean(String[] aoStrArray) throws ApplicationException
	{

		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		String lsKey = null;
		String lsValue = null;
		if (aoStrArray != null)
		{
			for (String lsStr : aoStrArray)
			{

				String[] lsStrArray2 = lsStr.split("=");
				lsKey = lsStrArray2[0];
				if (!StringUtils.isEmpty(lsKey))
				{
					lsKey = lsKey.trim();
				}

				try
				{
					lsValue = lsStrArray2[1];
				}
				catch (Exception aoExc)
				{
					LOG_OBJECT.Error("Error while building the TaskDetailsBean ", aoExc);
					throw new ApplicationException("Error occured during executing the convertToBean method", aoExc);	
				}
				if (!StringUtils.isEmpty(lsValue))
				{
					lsValue = lsValue.trim();
				}

				try
				{
					if (!HHSP8Constants.CLASS.equalsIgnoreCase(lsKey))
					{
						Field loField = TaskDetailsBean.class.getDeclaredField(lsKey);

						if (loField.getType().equals(java.lang.String.class))
						{
							ReflectionUtils.makeAccessible(loField);
							ReflectionUtils.setField(loField, loTaskDetailsBean, lsValue);
						}
					}
				}
				catch (SecurityException aoSExp)
				{
					LOG_OBJECT.Error("Error while building the TaskDetailsBean ", aoSExp);
				}
				catch (NoSuchFieldException aoNSExp)
				{
					LOG_OBJECT.Error("Error while building the TaskDetailsBean ", aoNSExp);
				}

			}
		}
		return loTaskDetailsBean;
	}

}
