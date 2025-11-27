package com.nyc.hhs.frameworks.cache;

import com.nyc.hhs.exception.ApplicationException;

/**
 * This interface defines the method to implement the data access logic
 * (preferably using a Data Access Object pattern) for a cached
 * object.CacheManager calls this to populate the cache when a cached object is
 * expired.
 * 
 */

public interface ICacheLoader
{
	public Object loadCacheObject(Object aoCacheKey, String asRegionName) throws ApplicationException;
}

