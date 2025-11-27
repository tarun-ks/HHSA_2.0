package com.nyc.hhs.frameworks.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;

/**
 * This class has operations for cache maintenance.
 * 
 */

public class CacheList
{
	private static final LogInfo LOG_OBJECT = new LogInfo(CacheList.class);

	private static CacheList moCacheListInstance = null;
	private static Object moMutex= new Object();
	private Map moMCacheMap = new HashMap();
	private Map moMCacheLoaderMap = new HashMap();
	private int miScheduleExpiryItemCount = 0;

	/**
	 * This Method gets the instance of the cache list
	 * 
	 * @return CacheList moCacheListInstance
	 */
	public static CacheList getInstance()
	{
		if (moCacheListInstance == null)
		{
			synchronized (moMutex)
			{
				if (moCacheListInstance == null)
				{
					moCacheListInstance = new CacheList();
				}
			}
		}
		return moCacheListInstance;
	}

	/**
	 * This Method is used to get the cache
	 * @param asRegionName Name of the region
	 * @return Object
	 * @throws ApplicationException
	 */
	@SuppressWarnings("static-access")
	public Object getCache(String asRegionName) throws ApplicationException
	{
		CacheTable loCacheTable = (CacheTable) moMCacheMap.get(asRegionName);
		if (loCacheTable == null || loCacheTable.getCache() == null)
		{
			return null;
		}
		LOG_OBJECT.Debug("Cache for " + asRegionName + " : " + loCacheTable.getCache().toString());
		return loCacheTable.getCache();
	}

	/**
	 * This Method put the cache in the Map
	 * @param asRegionName Name Of the Region
	 * @param aoCache Object of the team
	 */
	@SuppressWarnings("unchecked")
	public void putCache(String asRegionName, Object aoCache)
	{
		CacheTable loCacheTable = (CacheTable) moMCacheMap.get(asRegionName);
		if (loCacheTable != null)
		{
			loCacheTable.setCache(aoCache);
		}
		else
		{
			loCacheTable = new CacheTable();
			loCacheTable.setCache(aoCache);
			moMCacheMap.put(asRegionName, loCacheTable);
		}
	}

	/**
	 * This Method gets the cache loader.
	 * @param asCacheLoaderClassName Cache Loader Name
	 * @return loCacheLoader
	 */
	public Object getCacheLoader(String asCacheLoaderClassName)
	{
		Object loCacheLoader = moMCacheLoaderMap.get(asCacheLoaderClassName);
		return loCacheLoader;
	}

	/**
	 * This Methods puts the cache loader in moMCacheLoaderMap Map.
	 * @param moCacheLoader Object of the cache loader
	 */
	@SuppressWarnings("unchecked")
	public void putCacheLoader(Object moCacheLoader)
	{
		String lsCacheLoaderClassName = (moCacheLoader).getClass().getName();
		Object loCacheLoaderObject = moMCacheLoaderMap.get(lsCacheLoaderClassName);
		if (!(loCacheLoaderObject != null))
		{
			moMCacheLoaderMap.put(lsCacheLoaderClassName, moCacheLoader);
		}
	}

	/**
	 * This Method fetches the Schedule of the expiry
	 * @param asRegionName Name Of the Region
	 * @return loCacheTable
	 */
	public boolean getScheduleExpiry(String asRegionName)
	{
		CacheTable loCacheTable = (CacheTable) moMCacheMap.get(asRegionName);
		return loCacheTable.getScheduleExpiry();
	}

	/**
	 * This Method Puts the Region Name in the Cache Table
	 * @param asRegionName Name Of Region
	 */
	public void putScheduleExpiry(String asRegionName)
	{
		CacheTable loCacheTable = (CacheTable) moMCacheMap.get(asRegionName);
		if (loCacheTable != null)
		{
			loCacheTable.setScheduleExpiry(true);
			miScheduleExpiryItemCount++;
		}
		else
		{
			loCacheTable = new CacheTable();
			loCacheTable.setScheduleExpiry(true);
			miScheduleExpiryItemCount++;
			moMCacheMap.put(asRegionName, loCacheTable);
		}
	}

	/**
	 * This Method Returns the Expiry Count
	 * 
	 * @return int miScheduleExpiryItemCount
	 */
	public int getScheduleExpiryListCount()
	{
		return miScheduleExpiryItemCount;
	}

	/**
	 * This Method fetches the schedule expiry list.
	 * 
	 * @return loLCacheList
	 */
	public List getScheduleExpiryList()
	{
		List loLCacheList = new ArrayList();

		Collection loCollection = moMCacheMap.values();
		Iterator loCollectionItr = loCollection.iterator();
		while (loCollectionItr.hasNext())
		{
			CacheTable loItem = (CacheTable) loCollectionItr.next();
			if (loItem.getScheduleExpiry())
			{
				loLCacheList.add(loItem);
			}
		}
		return loLCacheList;
	}

	/**
	 * This methods sets the cache in region
	 */
	public void setCacheForRegion()
	{
		String lsCache = "CACHE VALUE";
		CacheTable loCacheTable = new CacheTable();
		loCacheTable.setCache(lsCache);

	}

	/**
	 * This inner class provides data structure to maintain cache properties
	 * 
	 */

	private class CacheTable
	{

		String msRegionName;
		Object moCache;
		Object moCacheLoader;
		boolean mbScheduleExpiry;

		/**
		 * Constructor
		 */
		public CacheTable()
		{
			// No Action Required
		}

		/**
		 * Paramterised constructor
		 * 
		 * @param asRegionName Name Of the Region
		 * @param aoCache Object of the Cache
		 * @param aoCacheLoader Object Of Cache Loader
		 * @param abScheduleExpiry Weather on expiry schedule or not
		 */
		public CacheTable(String asRegionName, Object aoCache, Object aoCacheLoader, boolean abScheduleExpiry)
		{
			this.msRegionName = asRegionName;
			this.moCache = aoCache;
			this.moCacheLoader = aoCacheLoader;
			this.mbScheduleExpiry = abScheduleExpiry;
		}

		public Object getCache()
		{
			return this.moCache;
		}

		public void setCache(Object aoCache)
		{
			this.moCache = aoCache;
		}

		public Object getCacheLoader()
		{
			return this.moCacheLoader;
		}

		public void setCacheLoader(Object aoCacheLoader)
		{
			this.moCacheLoader = aoCacheLoader;
		}

		public boolean getScheduleExpiry()
		{
			return this.mbScheduleExpiry;
		}

		public void setScheduleExpiry(boolean abScheduleExpiry)
		{
			this.mbScheduleExpiry = abScheduleExpiry;
		}

	}
}
