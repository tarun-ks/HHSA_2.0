package com.nyc.hhs.controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.mvc.ResourceAwareController;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.cache.CacheList;
import com.nyc.hhs.frameworks.cache.ICacheManager;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.FileNetOperationsUtils;
import com.nyc.hhs.util.HHSPortalUtil;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.PropertyUtil;
import com.nyc.hhs.util.XMLUtil;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;

public class CachingController extends BaseController implements ResourceAwareController
{
	private static final LogInfo LOG_OBJECT = new LogInfo(CachingController.class);
	
	/**
	 * This method will Handle 
	 * <li>All Locks Details</li>
	 *  <li>Specific Locks Details</li>
	 * <li>Update Cache</li>
	 * <li>Clean Specific locks</li>
	 *<li>Re-initialize Log4j</li> 
	 *<li>Cache Clean</li> 
	 *<li>Recache</li> 
	 * @param aoRequest
	 * @param aoResponse
	 * @return
	 * @throws ApplicationException
	 */
	@RenderMapping
	protected ModelAndView handleRenderRequestInternal(RenderRequest aoRequest, RenderResponse aoResponse)
			throws ApplicationException
	{
		CacheList loCacheList = CacheList.getInstance();
		String lsIsAdminUser = null;
		try
		{
			String lsPrintCache = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PRINT_CACHE);
			String lsCleanSpecificLock = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.CLEAN_SPECIFIC_LOCK);
			String lsPrintSpecificLock = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PRINT_SPECIFIC_LOCK);
			String lsCleanCache = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.CLEAN_CACHE);
			String lsReinitializeLog4j = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.REINITIALIZE_LOG4J);
			String lsUpdateCache = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.UPDATE_CACHE);
			String lsRecache = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.RECACHE);
			lsIsAdminUser = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.IS_ADMIN_USER);
			List<String> loKeysList = new ArrayList<String>();
			// Print All Locks Details
			if (lsPrintCache != null)
			{
				NamedCache loCache = CacheFactory.getCache(ApplicationConstants.HHS_CONFIG);
				loCache = CacheFactory.getCache(ApplicationConstants.HHS_CONFIG_SYNC);
				for (Object loKey : loCache.keySet())
				{
					if (null != loKey && loKey.toString().startsWith(ApplicationConstants.LOCK_ID_START))
						loKeysList.add((loKey.toString()));
				}
				aoRequest.setAttribute(HHSConstants.KEY_IDS, loKeysList);
			}
			// Print Specific Lock Details
			else if (null != lsPrintSpecificLock && !lsPrintSpecificLock.trim().isEmpty())
			{
				NamedCache loCache = CacheFactory.getCache(ApplicationConstants.HHS_CONFIG_SYNC);
				aoResponse.setContentType(HHSConstants.TEXT_HTML);
				if (!loCache.containsKey(lsPrintSpecificLock))
				{
					LOG_OBJECT.Debug(HHSConstants.LOCK_ID_TEXT + lsPrintSpecificLock + HHSConstants.DOESNT_EXIST_TEXT);
				}
				else
				{
					aoRequest.setAttribute(HHSConstants.PRINT_SPECIFIC_LOCK, true);
					aoRequest.setAttribute(HHSConstants.LOCK_ID, lsPrintSpecificLock);
					aoRequest.setAttribute(HHSConstants.LOCK_DESCRIBE, loCache.get(lsPrintSpecificLock));
				}
			}
			// Clean Specific Lock
			else if (null != lsCleanSpecificLock && !lsCleanSpecificLock.trim().isEmpty())
			{
				NamedCache loCache = CacheFactory.getCache(ApplicationConstants.HHS_CONFIG_SYNC);
				if (!loCache.containsKey(lsCleanSpecificLock))
				{
					LOG_OBJECT.Debug(HHSConstants.LOCK_ID_TEXT + lsCleanSpecificLock + HHSConstants.DOESNT_EXIST_TEXT);
				}
				else
				{
					LOG_OBJECT.Debug(HHSConstants.REMOVING_FROM_CACHE_TEXT + lsCleanSpecificLock
							+ HHSConstants.WITH_VALUE_TEXT + loCache.get(lsCleanSpecificLock));
					loCache.remove(lsCleanSpecificLock);
					aoRequest.setAttribute(HHSConstants.CBL_MESSAGE, true);
					LOG_OBJECT.Debug(HHSConstants.SUCCESS_REMOVAL_TEXT);
				}
			}
			// Update Cache
			else if (lsUpdateCache != null)
			{
				
				ICacheManager loCacheManager = BaseCacheManagerWeb.getInstance();
				if (lsUpdateCache.equalsIgnoreCase(HHSConstants.COHERENCE_ELEMENT))
				{
					loCacheManager.userP13cache(true);
				}
				else
				{
					loCacheManager.userP13cache(false);
				}
				aoRequest.setAttribute(HHSConstants.CBL_MESSAGE, true);
			}
			// Clean Cache
			else if (lsCleanCache != null)
			{
				NamedCache loCache = CacheFactory.getCache(ApplicationConstants.HHS_LOCK_IDS);
				loCache.clear();
				aoRequest.setAttribute(HHSConstants.CBL_MESSAGE, true);
			}
			// Reinitialize Log4j
			else if (lsReinitializeLog4j != null)
			{
				String lsLog4jConfigName = PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE,
						"log4j.config.location");
				File loFileLog4jConfig = new File(lsLog4jConfigName);
				if (loFileLog4jConfig.exists())
				{
					LOG_OBJECT.Debug("Initializing log4j with: " + lsLog4jConfigName);
					PropertyConfigurator.configure(lsLog4jConfigName);
				}
				else
				{
					LOG_OBJECT.Error("\n\n\n\n " + lsLog4jConfigName
							+ " file not found, so initializing log4j with BasicConfigurator");
					BasicConfigurator.configure();
				}
				aoRequest.setAttribute(HHSConstants.CBL_MESSAGE, true);
			}
			// ReCache
			else if (null != lsRecache)
			{
				putCacheData(loCacheList);
				aoRequest.setAttribute(HHSConstants.CBL_MESSAGE, true);
			}
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Exception in CachingController", aoExp);
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception in CachingController", aoExp);
		}
		aoRequest.setAttribute(HHSConstants.IS_ADMIN_USER, lsIsAdminUser);
		return new ModelAndView(HHSConstants.ADMIN_SETTINGS_VIEW);
	}
	
	/**
	 * Method is used to put Cache data.
	 * @param aoCacheList
	 * @throws ApplicationException
	 */
	private void putCacheData(CacheList aoCacheList) throws ApplicationException
	{
		// R1 cache reload
		ResourceBundle loRB = PropertyLoader.getProperties(ApplicationConstants.CACHE_FILES);
		Object loCacheObject = XMLUtil.getDomObj(CachingController.class.getResourceAsStream(loRB
				.getString(HHSConstants.TRANSACTION_LOWESCASE)));
		aoCacheList.putCacheLoader(loCacheObject);
		ICacheManager loCacheManager = BaseCacheManagerWeb.getInstance();
		loCacheManager.putCacheObject(HHSConstants.TRANSACTION_LOWESCASE, loCacheObject);
		Iterator loITer = loRB.keySet().iterator();
		loITer = loRB.keySet().iterator();
		while (loITer.hasNext())
		{
			String lsKey = (String) loITer.next();
			String lsFilePath = loRB.getString(lsKey);
			
			if (ApplicationConstants.TAXONOMY_ELEMENT.equalsIgnoreCase(lsKey))
			{
				PropertyUtil loTaxonomyUtil = new PropertyUtil();
				loTaxonomyUtil.setTaxonomyInCache(loCacheManager, ApplicationConstants.TAXONOMY_ELEMENT);
			}
			else if (ApplicationConstants.PROV_LIST.equalsIgnoreCase(lsKey))
			{
				synchronized (this)
				{
					loCacheManager.putCacheObject(lsKey, FileNetOperationsUtils.getProviderList(true));
				}
			}
			else if (ApplicationConstants.AGENCY_LIST.equalsIgnoreCase(lsKey))
			{
				synchronized (this)
				{
					loCacheManager.putCacheObject(lsKey, FileNetOperationsUtils.getNYCAgencyListFromDB());
				}
			}
			else if (ApplicationConstants.APPLICATION_SETTING.equalsIgnoreCase(lsKey))
			{
				synchronized (this)
				{
					loCacheManager.putCacheObject(lsKey, CommonUtil.getApplicationSettings());
				}
			}
			else if (HHSConstants.TRANSACTION_LOWESCASE.equalsIgnoreCase(lsKey)
					|| HHSConstants.RECACHE_TIMER.equalsIgnoreCase(lsKey)
					|| HHSConstants.RECACHE_TIME_INTERVAL.equalsIgnoreCase(lsKey))
			{
				LOG_OBJECT.Error("Exception in CachingTransactionConfigServlet.doPost()");
			}
			else
			{
				loCacheObject = XMLUtil.getDomObj(CachingController.class.getResourceAsStream(lsFilePath));
				aoCacheList.putCacheLoader(loCacheObject);
				loCacheManager.putCacheObject(lsKey, loCacheObject);
			}
		}
	}
	
}
