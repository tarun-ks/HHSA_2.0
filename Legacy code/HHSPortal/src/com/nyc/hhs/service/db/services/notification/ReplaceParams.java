package com.nyc.hhs.service.db.services.notification;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;

/**
 * This class takes a template as input and replaces the values with the values
 * passed in Hashmap
 * 
 */

public class ReplaceParams
{
	private static final LogInfo LOG_OBJECT = new LogInfo(ReplaceParams.class);
	public static HashMap moReplaceHashMap = null;

	/**
	 * Method which takes a template as input and replaces the values with the
	 * values passed in Hashmap
	 * 
	 * @param asTemplate input email content templates
	 * @param aoHMParameters required email parameters
	 * @return String a string representation of templates
	 * @throws ApplicationException
	 */

	public static String replaceWithParams(String asTemplate, HashMap aoHMParameters) throws ApplicationException
	{
		LOG_OBJECT.Debug("In replaceWithParams replacing template content key with user values. ");

		if (aoHMParameters != null)
		{
			Iterator loIterator = aoHMParameters.entrySet().iterator();

			while (loIterator.hasNext())
			{
				Map.Entry loMapValue = (Map.Entry) loIterator.next();
				String lsPattern = "\\{#" + loMapValue.getKey() + "}";
				if (null != loMapValue.getValue())
				{
					String lsValue = loMapValue.getValue().toString();
					if (lsValue.contains("$"))
					{
						lsValue = lsValue.replace("$", "\\$");
					}
					asTemplate = asTemplate.replaceAll(lsPattern, lsValue);
				}
			}
		}
		return asTemplate;
	}
}
