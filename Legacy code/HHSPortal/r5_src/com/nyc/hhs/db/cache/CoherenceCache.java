package com.nyc.hhs.db.cache;

import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.decorators.LoggingCache;

import com.nyc.hhs.constants.ApplicationConstants;

/**
 * This class is added for release 5.
 */
public final class CoherenceCache extends LoggingCache
{
	public static Cache cache;

	public CoherenceCache(String asId)
	{
		super(getCache(ApplicationConstants.HHS_DB_CACHE));
	}

	private static Cache getCache(String asId)
	{
		cache = new CoherenceMyBatisCache(asId);
		return cache;
	}
}