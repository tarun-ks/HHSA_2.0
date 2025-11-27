package com.nyc.hhs.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.io.IOUtils;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.cache.CacheList;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.XMLUtil;

/**
 * It gets executed at server startup Reads elements to be cached from
 * cache.properties If taxonomy, it builds taxonomy DOM from database else
 * caches as DOM object in cache
 * 
 */

public class HHSContextListener implements ServletContextListener
{

	private static final LogInfo LOG_OBJECT = new LogInfo(HHSContextListener.class);

	public void contextDestroyed(ServletContextEvent aoContext)
	{
		// No Action Required
	}

	/**
	 * This method is used to initialize the context parameters while deploying
	 * the application for the first time into the application server
	 * 
	 * @param aoContext context event type object
	 */
	public void contextInitialized(ServletContextEvent aoContext)
	{
		contextInitializedSM(aoContext.getServletContext());
	}
	/**
	 * This method is used to initialize context SM
	 * @param aoServletContext
	 */
	public static void contextInitializedSM(ServletContext aoServletContext)
	{
		CacheList loCacheList = CacheList.getInstance();
		try
		{
			ResourceBundle loRB = PropertyLoader.getProperties(HHSConstants.CACHE_FILES);
			Object loCacheObject = XMLUtil.getDomObj(CachingListener.class
					.getResourceAsStream(HHSConstants.TRANSACTION_ELEMENT_PATH));
			loCacheList.putCacheLoader(loCacheObject);
			BaseCacheManagerWeb.getInstance().putCacheObject(HHSConstants.TRANSACTION_ELEMENT, loCacheObject);
			Iterator loITer = loRB.keySet().iterator();
			while (loITer.hasNext())
			{
				String lsKey = (String) loITer.next();
				String lsFilePath = loRB.getString(lsKey);

				if (HHSConstants.STATUS_LIST.equalsIgnoreCase(lsKey))
				{
					synchronized (HHSContextListener.class)
					{
						BaseCacheManagerWeb.getInstance().putCacheObject(lsKey, HHSUtil.setMasterStatus());
						LOG_OBJECT.Debug(":::: cache:::: " + BaseCacheManagerWeb.getInstance().getCacheObject(lsKey));
					}
				}//added for release 5
				else if (HHSR5Constants.DICTIONARY_ALGO.equalsIgnoreCase(lsKey))
				{
					try
					{
						InputStream stream = aoServletContext.getResourceAsStream(HHSR5Constants.DICTIONARY_ALGO_PATH);
						BaseCacheManagerWeb.getInstance().putCacheObject(lsKey,
								IOUtils.toString(stream, HHSR5Constants.UTF_8));
					}
					catch (IOException loIOEx)
					{
						LOG_OBJECT.Error("Error occured while setting in dictionaryAlgo cache object", loIOEx);
					}
				}//added for release 5
				else
				{
					loCacheObject = XMLUtil.getDomObj(CachingListener.class.getResourceAsStream(lsFilePath));
					loCacheList.putCacheLoader(loCacheObject);
					BaseCacheManagerWeb.getInstance().putCacheObject(lsKey, loCacheObject);
				}
			}
		}
		catch (ApplicationException loError)
		{
			LOG_OBJECT.Error("Error occured while getting cache object.", loError);
		}
	}
}
