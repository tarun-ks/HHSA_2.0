package com.nyc.hhs.frameworks.cache;

import java.util.HashMap;

import com.nyc.hhs.exception.ApplicationException;

/**
 * This class is a hash map based cache implementation and provides maintenance
 * of cache data.
 * 
 */

public class CacheManager implements ICache
{
	HashMap<String, Object> moHMCacheStore = new HashMap<String, Object>();

	/**
	 * Constructor of the class
	 */
	public CacheManager()
	{
		//No Action Required
	}
	
	/**
	 * This Method get the object from cache
	 * @param Object 
	 * 				Key as object
	 * @return Object
	 * @throws ApplicationException
	 */
	public Object get(Object aoKey) throws ApplicationException
	{
		Object loValue = moHMCacheStore.get(aoKey.toString());
		return loValue;
	}
	
	/**
	 * This Method put the value in Map
	 * @param aoKey
	 * 				Key of Map
	 * @param aoValue
	 * 				Value against the key.
	 * @throws Application Exception
	 */
	public void put(Object aoKey, Object aoValue) throws ApplicationException
	{
		moHMCacheStore.put(aoKey.toString(), aoValue);
	}
	
	/**
	 * This Method removes the key from the Map.
	 * 
	 * @param Object 
	 * 				Key to be removed
	 * @throws Application Exception
	 */
	@Override
	public void remove(Object aoKey) throws ApplicationException
	{
		moHMCacheStore.remove(aoKey);

	}
	
	/**
	 * This Method Clear the Map
	 * @throws Application Exception
	 */
	@Override
	public void clear() throws ApplicationException
	{
		moHMCacheStore.clear();

	}
	/**
	 * This Method destroys the Map.
	 * @throws Application Exception
	 */
	@Override
	public void destroy() throws ApplicationException
	{
		moHMCacheStore = null;

	}
	/**
	 * This Method Locks the Object
	 * @param aoKey
	 * @throws Application Exception 
	 */
	@Override
	public void lock(Object aoKey) throws ApplicationException
	{
		// Lock doesn't work on hashmap based cache.
	}
	/**
	 * This Method Unlocks the Object
	 * @param  aoKey
	 * @throws Application Exception
	 */
	@Override
	public void unlock(Object aoKey) throws ApplicationException
	{
		// Unlock doesn't work on hashmap based cache.
	}
	/**
	 * This Method Returns the TimeStamp.
	 * @return TimeStamp
	 * 	
	 */
	@Override
	public long nextTimestamp()
	{
		return System.currentTimeMillis();
	}
	/**
	 * This Method fetches the timeout.
	 * @return int
	 */
	@Override
	public int getTimeout()
	{

		return 0;
	}

}
