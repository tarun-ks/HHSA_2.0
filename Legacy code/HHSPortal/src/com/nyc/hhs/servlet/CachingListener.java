package com.nyc.hhs.servlet;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.cache.CacheList;
import com.nyc.hhs.frameworks.cache.ICacheManager;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.TaxonomyTree;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.FileNetOperationsUtils;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.PropertyUtil;
import com.nyc.hhs.util.XMLUtil;
import com.tangosol.net.CacheFactory;

/**
 * It gets executed at server startup Reads elements to be cached from
 * cache.properties If taxonomy, it builds taxonomy DOM from database else
 * caches as DOM object in cache
 * 
 */

public class CachingListener implements ServletContextListener
{

	private static final LogInfo LOG_OBJECT = new LogInfo(CachingListener.class);

	public void contextDestroyed(ServletContextEvent aoContext)
	{
		try
		{
			// Shutdown all clustered services.
			CacheFactory.shutdown();
		}
		catch (Exception aoExc)
		{
			LOG_OBJECT.Error("Error occured while getting cache object.", aoExc);
		}
	}

	/**
	 * This method is used to initialize the context parameters while deploying
	 * the application for the first time into the application server
	 * 
	 * @param aoContext context event type object
	 */
	public void contextInitialized(ServletContextEvent aoContext)
	{
		boolean lbUsesCoherenceCache = true;
		String lsCacheType = System.getProperty(ApplicationConstants.CACHE_TYPE);
		if (lsCacheType != null && lsCacheType.equalsIgnoreCase(ApplicationConstants.LOCAL_CACHE))
		{
			lbUsesCoherenceCache = false;
		}
		initializeLog4j();
		ICacheManager loCacheManager = BaseCacheManagerWeb.getInstance();
		loCacheManager.userP13cache(lbUsesCoherenceCache);
		Object loCacheObject = null;
		CacheList loCacheList = CacheList.getInstance();
		try
		{
			LOG_OBJECT.Debug(logProperties("System Properties", System.getProperties()));
			LOG_OBJECT.Debug("Component Build Number ::: "
					+ PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, ApplicationConstants.PROPERTY_BUILD_NO));
			// Join the Coherence cluster, but only in clustered environments.
			if (System.getProperty("tangosol.coherence.cluster") != null && lbUsesCoherenceCache)
			{
				CacheFactory.ensureCluster();
			}
			ResourceBundle loRB = PropertyLoader.getProperties(ApplicationConstants.CACHE_FILES);
			loCacheObject = XMLUtil.getDomObj(CachingListener.class.getResourceAsStream(loRB.getString("transaction")));
			loCacheList.putCacheLoader(loCacheObject);
			loCacheManager.putCacheObject("transaction", loCacheObject);

			Iterator loITer = loRB.keySet().iterator();
			while (loITer.hasNext())
			{
				String lsKey = (String) loITer.next();
				String lsFilePath = loRB.getString(lsKey);

				if (ApplicationConstants.TAXONOMY_ELEMENT.equalsIgnoreCase(lsKey))
				{
					// taxonomy validation and batch
					PropertyUtil loTaxonomyUtil = new PropertyUtil();
					loTaxonomyUtil.setTaxonomyInCache(loCacheManager, ApplicationConstants.TAXONOMY_ELEMENT);
					LOG_OBJECT.Debug("Sucessfuly added Taxonomy DOM in Cache");
				}
				else if (ApplicationConstants.PROV_LIST.equalsIgnoreCase(lsKey))
				{
					synchronized (this)
					{
						loCacheManager.putCacheObject(lsKey, FileNetOperationsUtils.getProviderList(true));
						LOG_OBJECT.Debug(":::: cache:::: " + loCacheManager.getCacheObject(lsKey));
					}
				}
				else if (ApplicationConstants.AGENCY_LIST.equalsIgnoreCase(lsKey))
				{
					synchronized (this)
					{
						loCacheManager.putCacheObject(lsKey, FileNetOperationsUtils.getNYCAgencyListFromDB());
						LOG_OBJECT.Debug(":::: cache:::: " + loCacheManager.getCacheObject(lsKey));
					}
				}
				else if (ApplicationConstants.APPLICATION_SETTING.equalsIgnoreCase(lsKey))
				{
					synchronized (this)
					{
						loCacheManager.putCacheObject(lsKey, CommonUtil.getApplicationSettings());
						LOG_OBJECT.Debug(":::: cache:::: " + loCacheManager.getCacheObject(lsKey));
					}
				}
				else if (!("transaction".equalsIgnoreCase(lsKey) || "recacheTimer".equalsIgnoreCase(lsKey) || "recacheTimerInterval"
						.equalsIgnoreCase(lsKey)))
				{
					// updated in R5
					LOG_OBJECT.Debug(lsFilePath);
					// Ends in R5
					loCacheObject = XMLUtil.getDomObj(CachingListener.class.getResourceAsStream(lsFilePath));
					loCacheList.putCacheLoader(loCacheObject);
					loCacheManager.putCacheObject(lsKey, loCacheObject);
				}
			}
			// initializeCacheTimer(loRB);
			LOG_OBJECT.Debug(":::: Server OS :::: " + System.getProperty("os.name"));
			LOG_OBJECT.Debug(":::: JAVA Version:::: " + System.getProperty("java.version"));
		}
		catch (ApplicationException aoError)
		{
			LOG_OBJECT.Error("Error occured while getting cache object.", aoError);
		}
	}

	/**
	 * This method performs evidence flag validation and returns Element Id of
	 * erroneous Taxonomy items where evidence flag needs to be changed
	 * 
	 * @return list of String for which evidence validation is failing
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	private List<String> evidenceValidationCheck() throws ApplicationException
	{
		Channel loChannelObj = new Channel();
		List<String> loErroneousElementId = new ArrayList<String>();
		TransactionManager.executeTransaction(loChannelObj, "reCacheEvidenceValidation");
		List<TaxonomyTree> loList = (List<TaxonomyTree>) loChannelObj.getData("aoCacheEvidenceValidation");

		Iterator<TaxonomyTree> loIterator = loList.iterator();

		while (loIterator.hasNext())
		{
			TaxonomyTree loTaxonomyTree = loIterator.next();
			loErroneousElementId.add(loTaxonomyTree.getMsElementid());
		}
		return loErroneousElementId;
	}
	/**
	 * This method is used to initialize Log4j
	 */
	private void initializeLog4j()
	{
		try
		{
			String lsLog4jConfigFileName = PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE,
					"log4j.config.location");
			File loLog4jConfigFile = new File(lsLog4jConfigFileName);
			if (loLog4jConfigFile.exists())
			{
				LOG_OBJECT.Debug("Initializing log4j with: " + lsLog4jConfigFileName);
				PropertyConfigurator.configure(lsLog4jConfigFileName);
			}
			else
			{
				LOG_OBJECT.Debug("\n\n\n\n " + lsLog4jConfigFileName
						+ " file not found, so initializing log4j with BasicConfigurator");
				BasicConfigurator.configure();
			}
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error in initializeLog4j ", aoExp);
		}
	}
	/**
	 * This method is used to log in the properties
	 * @param name
	 * @param props
	 * @return name + ":\n" + sb
	 */
	private static String logProperties(final String name, final Properties props)
	{
		final StringBuilder sb = new StringBuilder();
		if (props != null && !props.isEmpty())
		{
			for (final Map.Entry<Object, Object> entry : props.entrySet())
			{
				sb.append(entry.getKey());
				sb.append("=");
				sb.append(entry.getValue());
				sb.append("\n");
			}
		}
		return name + ":\n" + sb;
	}

}