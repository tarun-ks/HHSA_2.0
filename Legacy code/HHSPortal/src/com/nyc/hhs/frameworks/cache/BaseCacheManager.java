package com.nyc.hhs.frameworks.cache;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.util.CommonUtil;

/**
 * This is the abstract class implementation of CachingManager interface. It
 * provides default functionality of methods defined in CachingManager.
 * 
 */

public abstract class BaseCacheManager implements ICacheManager
{

	protected CacheList moCacheList = CacheList.getInstance();

	protected static BaseCacheManager moBaseCacheInstance;

	protected double mdReadFromCache = 0;
	protected double mdReadFromDataSource = 0;

	protected boolean mbUserP13Cache = false;

	/**
	 * This is the overridden method to get the cached object from the cache
	 * 
	 * @param aoCacheKey key corresponding to which value is stored in cache
	 * @param aoCacheLoader collection of cache object
	 * @return object from the cache
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	public Object getCacheObject(Object aoCacheKey, ICacheLoader aoCacheLoader) throws ApplicationException
	{
		return getCacheObject(aoCacheKey, true, aoCacheLoader);
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
	public Object getCacheObject(Object aoCacheKey, boolean abFromCache, boolean abSearchCoherence,
			ICacheLoader aoCacheLoader) throws ApplicationException
	{
		return getCacheObject(aoCacheKey, abFromCache, abSearchCoherence, aoCacheLoader,
				CommonUtil.getRegionName(aoCacheKey));
	}

	/**
	 * This is the overridden method to get the cached object from the cache
	 * 
	 * @param aoCacheKey key corresponding to which value is stored in cache
	 * @param aoCacheLoader collection of cache object
	 * @param asRegionName region name indicates from which cache to fetch the
	 *            value
	 * @return object fetched from cache
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	public Object getCacheObject(Object aoCacheKey, ICacheLoader aoCacheLoader, String asRegionName)
			throws ApplicationException
	{
		return getCacheObject(aoCacheKey, true, false, aoCacheLoader, CommonUtil.getRegionName(aoCacheKey));
	}

	/**
	 * This is the overridden method to get the cached object from the cache
	 * 
	 * @param aoCacheKey key corresponding to which value is stored in cache
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	public Object getCacheObject(Object aoCacheKey) throws ApplicationException
	{

		return getCacheObject(aoCacheKey, true, false, null, CommonUtil.getRegionName(aoCacheKey));

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
	public void putCacheObject(Object aoCacheKey, Object aoCacheObject) throws ApplicationException
	{
		putCacheObject(aoCacheKey, aoCacheObject, CommonUtil.getRegionName(aoCacheKey));
	}

	/**
	 * This method is used to put the object in the cache correspond to the key
	 * passed
	 * 
	 * @param aoCacheKey key against which to store object in cache
	 * @param aoCacheObject object to store in the cache object
	 * @param abExpireSchedule boolean value to indicate whether the cache
	 *            object is scheduled for expiration or not
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	public void putCacheObject(Object aoCacheKey, Object aoCacheObject, boolean abExpireSchedule)
			throws ApplicationException
	{
		putCacheObject(aoCacheKey, aoCacheObject, abExpireSchedule, CommonUtil.getRegionName(aoCacheKey));
	}

	/**
	 * This method is used to peek the object in the cache correspond to the key
	 * passed
	 * 
	 * @param aoCacheKey key against which to store object in cache
	 * @return object peeked from cache
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	public Object peek(Object aoCacheKey) throws ApplicationException
	{
		return peek(aoCacheKey, CommonUtil.getRegionName(aoCacheKey));
	}

	/**
	 * This method is used to check whether object present in cache against the
	 * key passed
	 * 
	 * @param aoCacheKey key against which to check in cache
	 * @return boolean value representing whether object is present in cache or
	 *         not
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	public boolean isPresent(Object aoCacheKey) throws ApplicationException
	{
		return isPresent(aoCacheKey, CommonUtil.getRegionName(aoCacheKey));
	}

	/**
	 * This is an overridden method to check whether object present in cache
	 * against the key passed
	 * 
	 * @param aoCacheKey key against which to check in cache
	 * @param asRegionName name from which cache to be picked
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	public boolean isPresent(Object aoCacheKey, String asRegionName) throws ApplicationException
	{
		boolean lbIsPresent = false;
		if (peek(aoCacheKey, asRegionName) != null)
		{
			lbIsPresent = true;
		}
		return lbIsPresent;
	}

	/**
	 * This method is used to remove the object from the cache which is stored
	 * against the key passed to it
	 * 
	 * @param aoCacheKey key against which to remove from cache
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	public void removeCacheObject(Object aoCacheKey) throws ApplicationException
	{
		removeCacheObject(aoCacheKey, CommonUtil.getRegionName(aoCacheKey));
	}

	/**
	 * This method is used to clear the cache from the region
	 * 
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	public void clear() throws ApplicationException
	{
		clear(ApplicationConstants.HHS_CONFIG);
		clear(ApplicationConstants.HHS_CONFIG_SYNC);
		clear(ApplicationConstants.HHS_LOCK_IDS);
	}

	/**
	 * This method is used to assign P13 cache.
	 */
	public void userP13cache(boolean abUseP13Cache)
	{
		this.mbUserP13Cache = abUseP13Cache;
	}
}
