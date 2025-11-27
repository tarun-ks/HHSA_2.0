package com.nyc.hhs.util;

import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.cache.ICacheManager;
import com.nyc.hhs.frameworks.logger.LogInfo;

/**
 * This utility class has utilities related to properties class (resource
 * bundle) which includes methods to get property on the basis of input property
 * file name.
 * 
 */

public class PropertyLoader
{
	private static final LogInfo LOG_OBJECT = new LogInfo(PropertyLoader.class);
	private static HashMap<String, ResourceBundle> moHMProp = new HashMap<String, ResourceBundle>();
	private static Object moMonitor = new Object();
	private static final String ENVIRONMENT = System.getProperty("hhs.env");

	/**
	 * This method loads Properties File.
	 * 
	 * @param asPropBundle - properties bundle.
	 */
	public static synchronized void loadPropertiesFile(String asPropBundle)
	{
		synchronized (moMonitor)
		{
			try
			{
				ResourceBundle loRB = null;
				if (moHMProp.get(asPropBundle) == null)
				{
					String lsEnvironment = ENVIRONMENT;
					if (lsEnvironment != null)
					{
						if (lsEnvironment.indexOf("_") > 0)
						{
							String lsEnvArray[] = lsEnvironment.split("_");
							lsEnvironment = lsEnvArray[0];
							ICacheManager loICacheManager = BaseCacheManagerWeb.getInstance();
							loICacheManager.putCacheObject(ApplicationConstants.ENVIROMENT_TYPE, lsEnvArray[1]);
						}
						try
						{
							loRB = ResourceBundle.getBundle(asPropBundle + "-" + lsEnvironment.toLowerCase());
							LOG_OBJECT.Error("Enviornment Type ::: " + lsEnvironment.toLowerCase());
						}
						catch (MissingResourceException aoEx)
						{
							loRB = ResourceBundle.getBundle(asPropBundle);
						}
					}
					else
					{
						loRB = ResourceBundle.getBundle(asPropBundle);
					}
					moHMProp.put(asPropBundle, loRB);
				}
			}
			catch (Exception loEx)
			{
				LOG_OBJECT.Error("Exception occured while putting data into Cache", loEx);
			}
		}
	}

	/**
	 * This method gets Property.
	 * 
	 * @param asPropBundle - properties bundle.
	 * @param asPropName - properties name.
	 * @return - property name.
	 * @throws ApplicationException
	 */
	public static String getProperty(String asPropBundle, String asPropName) throws ApplicationException
	{
		if (moHMProp.get(asPropBundle) == null)
		{
			loadPropertiesFile(asPropBundle);
		}

		ResourceBundle loRB = (ResourceBundle) moHMProp.get(asPropBundle);
		if (loRB == null)
		{
			throw new ApplicationException("No property bundle loaded with name: " + asPropBundle);
		}
		return loRB.getString(asPropName);
	}

	/**
	 * This method gets Properties.
	 * 
	 * @param asPropBundle - properties bundle.
	 * @return - loRB
	 * @throws ApplicationException
	 */
	public static ResourceBundle getProperties(String asPropBundle) throws ApplicationException
	{
		if (moHMProp.get(asPropBundle) == null)
		{
			loadPropertiesFile(asPropBundle);
		}
		ResourceBundle loRB = (ResourceBundle) moHMProp.get(asPropBundle);
		if (loRB == null)
		{
			throw new ApplicationException("No property bundle loaded with name: " + asPropBundle);
		}
		return loRB;
	}

}
