package com.nyc.hhs.frameworks.cache;

import java.io.Serializable;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.util.CommonUtil;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;

/**
 * This is the base implementation of BaseCacheManager abstract class. It
 * implements all the methods defined in BaseCacheManager.
 * 
 */

public class BaseCacheManagerWeb extends BaseCacheManager
{

	private static final LogInfo LOG_OBJECT = new LogInfo(BaseCacheManagerWeb.class);
	private static Object moSyncObj = new Object();

	/**
	 * This method is used to get the instance of BaseCacheManagerWeb
	 * 
	 * @return moBaseCacheInstance
	 */
	public static ICacheManager getInstance()
	{
		if (moBaseCacheInstance == null)
		{
			synchronized (moSyncObj)
			{
				if (moBaseCacheInstance == null)
				{
					moBaseCacheInstance = new BaseCacheManagerWeb();
				}
			}
		}
		return moBaseCacheInstance;
	}

	/**
	 * This method is used to get the cache object for the region name passed to
	 * it
	 * 
	 * @param asRegionName region name from where cache is to be picked
	 * @return cache object
	 * @throws ApplicationException
	 */
	private ICache getCacheForRegion(String asRegionName) throws ApplicationException
	{

		if (moCacheList.getCache(asRegionName) == null)
		{
			LOG_OBJECT.Debug("Cache does not exist for region " + asRegionName + ". So create a new cache object.");
			ICache loCache = new CacheManager();

			LOG_OBJECT.Debug("getCacheForRegion() - cache 1: " + loCache);
			moCacheList.putCache(asRegionName, loCache);
			LOG_OBJECT.Debug("getCacheForRegion() - cache 2: " + loCache);
			return loCache;
		}
		else
		{
			LOG_OBJECT.Debug("Cache exists for region " + asRegionName);
			return (ICache) moCacheList.getCache(asRegionName);
		}
	}

	/**
	 * This is the overridden method to get the cached object from the cache
	 * 
	 * @param aoCacheKey key corresponding to which value is stored in cache
	 * @param aoCacheLoader collection of cache object
	 * @return object from the cache
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	@Override
	public Object getCacheObject(Object aoCacheKey) throws ApplicationException
	{
		return getCacheObject(aoCacheKey, true, false, null, CommonUtil.getRegionName(aoCacheKey));
	}

	/**
	 * This is the overridden method to get the cached object from the cache
	 * 
	 * @param aoCacheKey key corresponding to which value is stored in cache
	 * @param abFromCache boolean value to indicate to fetch value from Cache
	 * @param abSearchCoherence boolean value to indicate to fetch value from
	 *            Coherence
	 * @param aoCacheLoader collection of cache object
	 * @return object fetched from cache
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	@Override
	public Object getCacheObject(Object aoCacheKey, boolean abFromCache, boolean abSearchCoherence,
			ICacheLoader aoCacheLoader, String asRegionName) throws ApplicationException
	{
		Object loCacheObject = null;
		try
		{
			if (mbUserP13Cache)
			{
				final NamedCache loCache = CacheFactory.getCache(asRegionName);
				loCacheObject = loCache.get(aoCacheKey);
			}
			else
			{
				Object loCacheObjectKey = null;
				ICache loCache = getCacheForRegion(CommonUtil.getRegionName(aoCacheKey));

				long llStartTime = System.currentTimeMillis();
				// Check if cacheKey is an instance of CacheKey interface
				if (aoCacheKey instanceof ICacheKey)
				{
					loCacheObjectKey = ((ICacheKey) aoCacheKey).getCacheKey();
				}
				else
				{
					loCacheObjectKey = aoCacheKey;
				}

				try
				{
					// First, if requested, attempt to load from cache
					if (abFromCache)
					{
						loCacheObject = loCache.get(loCacheObjectKey);
						// readFromCache++;
					}

					long llEndTime = System.currentTimeMillis();
					LOG_OBJECT.Debug("Time taken to get cached object is : " + (llEndTime - llStartTime)
							+ " milli-seconds.");

				}
				catch (Exception loException)
				{
					LOG_OBJECT.Error("Exception in getCacheObject()" + loException);
					throw new ApplicationException("Exception in getCacheObject()", loException);
				}
			}
		}
		catch (final Exception loException)
		{
			// Handle failure in removing object or putting object to cache.
			LOG_OBJECT.Error("Error retrieving object with key[" + aoCacheKey + "] from the " + asRegionName
					+ " cache.");
			throw new ApplicationException("Error retrieving object with key[" + aoCacheKey + "] from the "
					+ asRegionName + " cache.", loException);
		}
		return loCacheObject;
	}



	/**
	 * This method is used to put the object in the cache correspond to the key
	 * passed
	 * 
	 * @param aoCacheKey key against which to store object in cache
	 * @param aoCacheObject object to store in the cache object
	 * @param asRegionName region name from where to cache object to be picked
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	@Override
	public void putCacheObject(Object aoCacheKey, Object aoCacheObject, String asRegionName)
			throws ApplicationException
	{
		Object loCacheObjectKey = null;
		try
		{
			if (mbUserP13Cache)
			{
				NamedCache loCache = CacheFactory.getCache(asRegionName);
				loCache.remove(aoCacheKey);
				loCache.put(aoCacheKey, aoCacheObject, -1);
			}
			else
			{
				ICache loCache = getCacheForRegion(asRegionName);

				// since any cached data is no longer valid, we should
				// remove the item from the cache if it's an update.
				loCache.remove(aoCacheObject);

				// Check if cacheKey is an instance of CacheKey interface
				if (aoCacheKey instanceof ICacheKey)
				{
					loCacheObjectKey = ((ICacheKey) aoCacheKey).getCacheKey();
				}
				else
				{
					loCacheObjectKey = aoCacheKey;
				}
				// put the new object in the cache
				loCache.put(loCacheObjectKey, aoCacheObject);
			}
		}
		catch (Exception loException)
		{
			// Handle failure in removing object or putting object to cache.
			LOG_OBJECT.Error("Error in putting object into cache" + loException.toString());
			throw new ApplicationException("Error in putting object into cache. ", loException);
		}
	}

	/**
	 * This method is used to put the object in the cache correspond to the key
	 * passed
	 * 
	 * @param aoCacheKey key against which to store object in cache
	 * @param aoCacheObject object to store in the cache object
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	@Override
	public void putCacheObject(Object aoCacheKey, Object aoCacheObject) throws ApplicationException
	{
		putCacheObject(aoCacheKey, aoCacheObject, false, CommonUtil.getRegionName(aoCacheKey));

	}

	/**
	 * This method is used to put the object in the cache correspond to the key
	 * passed
	 * 
	 * @param aoCacheKey key against which to store object in cache
	 * @param aoCacheObject object to store in the cache object
	 * @param abExpireSchedule boolean value to indicate whether the cache
	 *            object is scheduled for expiration or not
	 * @param asRegionName region name from where to cache object to be picked
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	@Override
	public void putCacheObject(Object aoCacheKey, Object aoCacheObject, boolean abExpireSchedule, String asRegionName)
			throws ApplicationException
	{
		try
		{
			if (mbUserP13Cache)
			{
				NamedCache loCache = CacheFactory.getCache(asRegionName);
				loCache.remove(aoCacheKey);
				loCache.put(aoCacheKey, aoCacheObject, -1);
			}
			else
			{
				Object loCacheObjectKey = null;
				ICache loCache = getCacheForRegion(asRegionName);
				// since any cached data is no longer valid, we should
				// remove the item from the cache if it's an update.
				loCache.remove(aoCacheObject);
				// Check if cacheKey is an instance of CacheKey interface
				if (aoCacheKey instanceof ICacheKey)
				{
					loCacheObjectKey = ((ICacheKey) aoCacheKey).getCacheKey();
				}
				else
				{
					loCacheObjectKey = aoCacheKey;
				}
				// put the new object in the cache
				loCache.put(loCacheObjectKey, aoCacheObject);
				// cacheList.putScheduleExpiry(regionName);
			}
		}
		catch (Exception loException)
		{
			// Handle failure in removing object or putting object to cache.
			LOG_OBJECT.Error("Error in removing object or putting object to cache");
			throw new ApplicationException("Error in removing object or putting object to cache", loException);
		}
	}

	/**
	 * This method is used to peek the object in the cache correspond to the key
	 * passed
	 * 
	 * @param aoCacheKey key against which to store object in cache
	 * @param asRegionName region name from where to cache object to be picked
	 * @return object peeked from cache
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	@Override
	public Object peek(Object aoCacheKey, String asRegionName) throws ApplicationException
	{
		ICache loCache = getCacheForRegion(asRegionName);
		Object loCacheObjectKey = null;
		// Check if cacheKey is an instance of CacheKey interface
		if (aoCacheKey instanceof ICacheKey)
		{
			loCacheObjectKey = ((ICacheKey) aoCacheKey).getCacheKey();
		}
		else
		{
			loCacheObjectKey = aoCacheKey;
		}

		if (loCache.get(loCacheObjectKey) == null)
		{
			LOG_OBJECT.Debug("Object does not exist in " + asRegionName + " region.");
		}
		else
		{
			LOG_OBJECT.Debug("Object exists in the cache in " + asRegionName + " region.");
		}
		return loCache.get(loCacheObjectKey);
	}

	/**
	 * This method is used to remove the object from the cache which is stored
	 * against the key passed to it
	 * 
	 * @param aoCacheKey key against which to remove from cache
	 * @param asRegionName region name from where to cache object to be picked
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	@Override
	public void removeCacheObject(Object aoCacheKey, String asRegionName) throws ApplicationException
	{
		try
		{
			if (mbUserP13Cache)
			{
				if (aoCacheKey != null && asRegionName != null)
				{
					NamedCache loCache = CacheFactory.getCache(asRegionName);
					loCache.remove(aoCacheKey);
				}
			}
			else
			{
				ICache loCache = getCacheForRegion(asRegionName);
				loCache.remove(aoCacheKey);
			}
		}
		catch (Exception loException)
		{
			LOG_OBJECT.Error("Error in removing CacheObject from region : " + asRegionName + loException);
			throw new ApplicationException("Error in removing CacheObject : " + asRegionName, loException);
		}
	}

	/**
	 * This method is used to clear the cache from the region
	 * 
	 * @param asRegionName region name from where to cache object to be picked
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	@Override
	public void clear(String asRegionName) throws ApplicationException
	{
		LOG_OBJECT.Debug("Removing all elements from cache region : " + asRegionName);
		try
		{
			if (mbUserP13Cache)
			{
				NamedCache loCache = CacheFactory.getCache(asRegionName);
				loCache.clear();
			}
			else
			{
				ICache loCache = getCacheForRegion(asRegionName);
				loCache.clear();
				mdReadFromCache = 0;
				mdReadFromDataSource = 0;
			}
		}
		catch (Exception loException)
		{
			LOG_OBJECT.Error("Error in clearing the cache." + loException);
			throw new ApplicationException("Error in clearing the cache.", loException);
		}
	}

	/**
	 * This method is used to clear the cache from the region
	 * 
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	@Override
	public void clearCache() throws ApplicationException
	{
		clearCache(ApplicationConstants.HHS_CONFIG);
		clearCache(ApplicationConstants.HHS_CONFIG_SYNC);
		clearCache(ApplicationConstants.HHS_LOCK_IDS);
	}

	/**
	 * This method is used to clear the cache from the region
	 * 
	 * @param asRegionName region name from where to cache object to be picked
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	@Override
	public void clearCache(String asRegionName) throws ApplicationException
	{
		ICache loCache = getCacheForRegion(asRegionName);
		loCache.clear();

	}

	/**
	 * This is the overridden method to get the cached object from the cache
	 * 
	 * @param aoCacheKey key corresponding to which value is stored in cache
	 * @param abFromCache boolean value to indicate to fetch value from Cache
	 * @param aoCacheLoader collection of cache object
	 * @return object fetched from cache
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	@Override
	public Object getCacheObject(Object aoCacheKey, boolean abFromCache, ICacheLoader aoCacheLoader)
			throws ApplicationException
	{
		return null;
	}

	/**
	 * This method is used to put the object in the cache correspond to the key
	 * passed
	 * 
	 * @param aoCacheKey key against which to store object in cache
	 * @param aoCacheObject object to store in the cache object
	 * @param abLockAndUpdate - flag depicting if key has to be locked for
	 *            writing
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	public void putCacheObject(final Serializable aoCacheKey, final Serializable aoCacheObject,
			final boolean abLockAndUpdate) throws ApplicationException
	{
		NamedCache loCache = null;
		String lsRegionName = CommonUtil.getRegionName(aoCacheKey);
		try
		{
			loCache = CacheFactory.getCache(lsRegionName);
			if (abLockAndUpdate)
			{
				loCache.lock(aoCacheKey);
			}
			loCache.put(aoCacheKey, aoCacheObject);
		}
		catch (final Exception loException)
		{
			// Handle failure in removing object or putting object to cache.
			LOG_OBJECT
					.Error("Error inserting object with key[" + aoCacheKey + "] into the " + lsRegionName + " cache.");
			throw new ApplicationException("Error inserting object with key[" + aoCacheKey + "] into the "
					+ lsRegionName + " cache.", loException);
		}
		finally
		{
			if (loCache != null && abLockAndUpdate)
			{
				loCache.unlock(aoCacheKey);
			}
		}
	}
}