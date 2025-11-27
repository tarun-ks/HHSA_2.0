package com.nyc.hhs.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.cache.CacheList;
import com.nyc.hhs.frameworks.cache.ICacheManager;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.FileNetOperationsUtils;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.PropertyUtil;
import com.nyc.hhs.util.XMLUtil;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;

/**
 * It gets executed at server startup Reads elements to be cached from
 * cache.properties If taxonomy, it builds taxonomy DOM from database else
 * caches as DOM object in cache
 * 
 */
public class CachingTransactionConfigServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{

	private static final long serialVersionUID = 1L;
	private static final LogInfo LOG_OBJECT = new LogInfo(CachingTransactionConfigServlet.class);

	/**
	 * This is the non-parameterized constructor
	 * 
	 */
	public CachingTransactionConfigServlet()
	{
		super();
	}

	/**
	 * This method handle the get request of a servlet. This will internally
	 * call doPost method to process the servlet request and return the
	 * response.
	 * 
	 * @param aoRequest HttpServlet request object
	 * @param aoResponse HttpServlet response object
	 * @throws ServletException If an Servlet Exception occurs
	 * @throws IOException If an Input Output Exception occurs
	 */
	@Override
	protected void doGet(HttpServletRequest aoRequest, HttpServletResponse aoResponse) throws ServletException,
			IOException
	{

		this.doPost(aoRequest, aoResponse);
	}

	/**
	 * This method handle the post request of a servlet. When a action is
	 * initiated from a jsp, it process the action by calling multiple
	 * transactions to the end. Updated as part of Release 3.4.0 for
	 * Locking/Unlocking
	 * @param aoRequest HttpServlet request object
	 * @param aoResponse HttpServlet response object
	 * @throws ServletException If an Servlet Exception occurs
	 * @throws IOException If an Input Output Exception occurs
	 */
	@Override
	protected void doPost(HttpServletRequest aoRequest, HttpServletResponse aoResponse) throws ServletException,
			IOException
	{
		CacheList loCacheList = CacheList.getInstance();
		PrintWriter loOut = null;
		try
		{
			String lsPrintCache = aoRequest.getParameter(HHSConstants.PRINT_CACHE);
			String lsCleanSpecificLock = aoRequest.getParameter(HHSConstants.CLEAN_SPECIFIC_LOCK);
			// Added as part of release 3.4.0 for unlocking with Lock Id;
			String lsPrintSpecificLock = aoRequest.getParameter(HHSConstants.PRINT_SPECIFIC_LOCK);
			// Added as part of release 3.4.0 for unlocking with Lock Id;
			String lsCleanCache = aoRequest.getParameter(HHSConstants.CLEAN_CACHE);
			String lsCleanDBCache = aoRequest.getParameter(HHSConstants.CLEAN_DB_CACHE);
			String lsReinitializeLog4j = aoRequest.getParameter(HHSConstants.REINITIALIZE_LOG4J);
			String lsUpdateCache = aoRequest.getParameter(HHSConstants.UPDATE_CACHE);
			String lsDocumentVaultCache = aoRequest.getParameter(HHSR5Constants.DOCUMENT_VAULTS_CACHE);
			if (lsUpdateCache != null)
			{
				ICacheManager loCacheManager = BaseCacheManagerWeb.getInstance();
				if (lsUpdateCache.equalsIgnoreCase("coherence"))
				{
					loCacheManager.userP13cache(true);
				}
				else
				{
					loCacheManager.userP13cache(false);
				}
			}
			if (lsCleanCache != null)
			{
				NamedCache loCache = CacheFactory.getCache(ApplicationConstants.HHS_LOCK_IDS);
				loCache.clear();
			}
			else if (lsCleanDBCache != null)
			{
				NamedCache loCache = CacheFactory.getCache(ApplicationConstants.HHS_DB_CACHE);
				loCache.clear();
			}
			else if (lsPrintCache != null)
			{
				loOut = aoResponse.getWriter();
				aoResponse.setContentType(HHSConstants.TEXT_HTML);
				NamedCache loCache = CacheFactory.getCache(ApplicationConstants.HHS_CONFIG);
				loOut.print(ApplicationConstants.HHS_CONFIG + HHSConstants.CACHE_PRINT_BREAK
						+ loCache.keySet().toString());
				// Updated as part of release 3.4.0 for unlocking with Lock Id -
				// Starts
				loCache = CacheFactory.getCache(ApplicationConstants.HHS_CONFIG_SYNC);
				loOut.print(HHSConstants.PRINT_NEW_LINE + ApplicationConstants.HHS_CONFIG_SYNC
						+ HHSConstants.CACHE_PRINT_BREAK + loCache.keySet().toString());
				loCache = CacheFactory.getCache(ApplicationConstants.HHS_DB_CACHE);
				loOut.print(HHSConstants.PRINT_NEW_LINE + ApplicationConstants.HHS_DB_CACHE
						+ HHSConstants.CACHE_PRINT_BREAK + loCache.keySet().toString());
				loCache = CacheFactory.getCache(ApplicationConstants.HHS_LOCK_IDS);
				loOut.print(HHSConstants.PRINT_NEW_LINE + ApplicationConstants.HHS_LOCK_IDS
						+ HHSConstants.CACHE_PRINT_BREAK);
				// System.out.println("::::"+loCache.keySet());
				for (Object loKey : loCache.keySet())
				{
					loOut.print(HHSConstants.PRINT_HREF_P1 + loKey.toString() + HHSConstants.PRINT_HREF_P2
							+ loKey.toString() + HHSConstants.PRINT_HREF_END);
				}
				// Updated as part of release 3.4.0 for unlocking with Lock Id -
				// Ends
			}
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
			}
			// Added as part of release 3.4.0 for unlocking with Lock Id -
			// Starts
			else if (null != lsPrintSpecificLock && !lsPrintSpecificLock.trim().isEmpty())
			{
				NamedCache loCache = CacheFactory.getCache(ApplicationConstants.HHS_LOCK_IDS);
				loOut = aoResponse.getWriter();
				aoResponse.setContentType(HHSConstants.TEXT_HTML);
				if (!loCache.containsKey(lsPrintSpecificLock))
				{
					loOut.print(HHSConstants.LOCK_ID_TEXT + lsPrintSpecificLock + HHSConstants.DOESNT_EXIST_TEXT);
					LOG_OBJECT.Debug(HHSConstants.LOCK_ID_TEXT + lsPrintSpecificLock + HHSConstants.DOESNT_EXIST_TEXT);
				}
				else
				{
					loOut.print(HHSConstants.LOCK_DETAILS_TEXT + HHSConstants.CACHE_PRINT_BREAK
							+ HHSConstants.LOCK_ID_TEXT + lsPrintSpecificLock + HHSConstants.DETAILS_TEXT
							+ loCache.get(lsPrintSpecificLock));
					loOut.print(HHSConstants.REMOVE_HREF_P1 + lsPrintSpecificLock + HHSConstants.REMOVE_HREF_P2);
				}
			}
			else if (null != lsCleanSpecificLock && !lsCleanSpecificLock.trim().isEmpty())
			{
				NamedCache loCache = CacheFactory.getCache(ApplicationConstants.HHS_LOCK_IDS);
				loOut = aoResponse.getWriter();
				if (!loCache.containsKey(lsCleanSpecificLock))
				{
					loOut.print(HHSConstants.LOCK_ID_TEXT + lsCleanSpecificLock + HHSConstants.DOESNT_EXIST_TEXT);
					LOG_OBJECT.Debug(HHSConstants.LOCK_ID_TEXT + lsCleanSpecificLock + HHSConstants.DOESNT_EXIST_TEXT);
				}
				else
				{
					LOG_OBJECT.Debug(HHSConstants.REMOVING_FROM_CACHE_TEXT + lsCleanSpecificLock
							+ HHSConstants.WITH_VALUE_TEXT + loCache.get(lsCleanSpecificLock));
					loCache.remove(lsCleanSpecificLock);
					LOG_OBJECT.Debug(HHSConstants.SUCCESS_REMOVAL_TEXT);
					loOut.print(HHSConstants.SUCCESS_REMOVAL_TEXT);
				}
			}
			else if (lsDocumentVaultCache != null)
			{
				String lsAction = aoRequest.getParameter("action");
				String lsFolderPath = aoRequest.getParameter("folderPath");

				String lsOrgId = (String) aoRequest.getSession()
						.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG);
				String lsUserOrgType = (String) aoRequest.getSession().getAttribute(
						ApplicationConstants.KEY_SESSION_ORG_TYPE);
				if (null != lsUserOrgType && lsUserOrgType.equalsIgnoreCase(HHSR5Constants.USER_CITY))
				{
					lsOrgId = lsUserOrgType;
				}

				try
				{
					if (lsAction != null && lsAction.equals("add"))
						addLock(lsOrgId, lsFolderPath);
					else if (lsAction != null && lsAction.equals("remove"))
						removeLock(lsOrgId, lsFolderPath);
					else if (lsAction != null && lsAction.equals("print"))
					{
						//Change after Release 4.0.2 added constant
						Map<String, List<String>> loLockMap = (Map<String, List<String>>) BaseCacheManagerWeb
								.getInstance().getCacheObject(HHSR5Constants.DOCUMENT_VAULT_LOCKS);
						loOut = aoResponse.getWriter();
						aoResponse.setContentType(HHSConstants.TEXT_HTML);
						if (loLockMap != null)
							loOut.print(loLockMap.toString());
					}
					else
						cleanAll();
				}
				catch (ApplicationException e)
				{
					e.printStackTrace();
				}
			}
			// Added as part of release 3.4.0 for unlocking with Lock Id - Ends
			else
			{
				putCacheData(loCacheList, aoRequest);
			}
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Exception in CachingTransactionConfigServlet.doPost()", aoExp);
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception in CachingTransactionConfigServlet.doPost()", aoExp);
		}
		finally
		{
			if (null != loOut)
			{
				loOut.flush();
				loOut.close();
			}
		}
	}

	/**
	 * Method is used to put Cache data.
	 * @param aoCacheList
	 * @param aoRequest
	 * @throws ApplicationException
	 */
	private void putCacheData(CacheList aoCacheList, HttpServletRequest aoRequest) throws ApplicationException
	{
		// R1 cache reload
		ResourceBundle loRB = PropertyLoader.getProperties(ApplicationConstants.CACHE_FILES);
		Object loCacheObject = XMLUtil.getDomObj(CachingListener.class.getResourceAsStream(loRB
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
				loCacheObject = XMLUtil.getDomObj(CachingListener.class.getResourceAsStream(lsFilePath));
				aoCacheList.putCacheLoader(loCacheObject);
				loCacheManager.putCacheObject(lsKey, loCacheObject);
			}
		}
		// R1 cache reloaded

		// R2 cache reload
		HHSContextListener.contextInitializedSM(aoRequest.getSession().getServletContext());
		// R2 cache reloaded

	}
	/**
	 * This method is used to add lock
	 * @param lsOrgId
	 * @param lsFolderPath
	 * @throws ApplicationException
	 */
	private void addLock(String lsOrgId, String lsFolderPath) throws ApplicationException
	{
	    //Change after Release 4.0.2 added constant
		Map<String, List<String>> loLockMap = (Map<String, List<String>>) BaseCacheManagerWeb.getInstance()
				.getCacheObject(HHSR5Constants.DOCUMENT_VAULT_LOCKS);
		List<String> loLockList = null;
		if (null != loLockMap)
		{
			loLockList = loLockMap.get(lsOrgId);
			if (loLockList == null)
			{
				loLockList = new ArrayList<String>();
			}
		}
		else
		{
			loLockMap = new HashMap<String, List<String>>();
			loLockList = new ArrayList<String>();
		}
		loLockList.add(lsFolderPath);
		loLockMap.put(lsOrgId, loLockList);
		//Change after Release 4.0.2 added constant
		BaseCacheManagerWeb.getInstance().putCacheObject(HHSR5Constants.DOCUMENT_VAULT_LOCKS, loLockMap);
	}
	/**
	 * This method is used to remove lock
	 * @param lsOrgId
	 * @param lsFolderPath
	 * @throws ApplicationException
	 */
	private void removeLock(String lsOrgId, String lsFolderPath) throws ApplicationException
	{
	    //Change after Release 4.0.2 added constant
		Map<String, List<String>> loLockMap = (Map<String, List<String>>) BaseCacheManagerWeb.getInstance()
				.getCacheObject(HHSR5Constants.DOCUMENT_VAULT_LOCKS);
		List<String> loLockList = null;
		if (null != loLockMap)
		{
			loLockList = loLockMap.get(lsOrgId);
			if (loLockList != null)
			{
				loLockList.remove(lsFolderPath);
				loLockMap.put(lsOrgId, loLockList);
				//Change after Release 4.0.2 added constant
				BaseCacheManagerWeb.getInstance().putCacheObject(HHSR5Constants.DOCUMENT_VAULT_LOCKS, loLockMap);
			}
		}
	}
	/**
	 * This method is used to clean all locks
	 * @throws ApplicationException
	 */
	private void cleanAll() throws ApplicationException
	{
	    //Change after Release 4.0.2 added constant
		BaseCacheManagerWeb.getInstance().removeCacheObject(HHSR5Constants.DOCUMENT_VAULT_LOCKS);

	}
}