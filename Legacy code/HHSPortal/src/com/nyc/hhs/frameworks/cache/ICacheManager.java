package com.nyc.hhs.frameworks.cache;

/**
 * This is the main interface in Caching framework. It defines the methods
 * that all the client applications can depend on, to use caching in web portal
 * project. It hides the clients from any 3rd party caching API that is used
 * to implement the caching mechanism (Note: I am currently using Jakarta Turbine's
 * Java Caching System, also known as JCS, in the caching framework. We may want to switch to
 * JCache - Java Temporary Caching API (JSR-107) when it becomes available).
 *
 */

import com.nyc.hhs.exception.ApplicationException;

public interface ICacheManager
{

	public Object getCacheObject(Object aoCacheKey, ICacheLoader aoCacheLoader) throws ApplicationException;

	public Object getCacheObject(Object aoCacheKey, boolean abFromCache, ICacheLoader aoCacheLoader) throws ApplicationException;

	public Object getCacheObject(Object aoCacheKey, ICacheLoader aoCacheLoader, String asRegionName) throws ApplicationException;

	public Object getCacheObject(Object aoCacheKey, boolean abFromCache, boolean abSearchCoherence, ICacheLoader aoCacheLoader, String asRegionName)
			throws ApplicationException;

	public Object getCacheObject(Object aoCacheKey) throws ApplicationException;

	public void putCacheObject(Object aoCacheKey, Object aoCacheObject) throws ApplicationException;

	public void putCacheObject(Object aoCacheKey, Object aoCacheObject, String asRegionName) throws ApplicationException;

	public void putCacheObject(Object aoCacheKey, Object aoCacheObject, boolean abExpireSchedule) throws ApplicationException;

	public void putCacheObject(Object aoCacheKey, Object aoCacheObject, boolean abExpireSchedule, String aoRegionName) throws ApplicationException;

	public Object peek(Object aoCacheKey) throws ApplicationException;

	public Object peek(Object aoCacheKey, String aoRegionName) throws ApplicationException;

	public boolean isPresent(Object aoCacheKey) throws ApplicationException;

	public boolean isPresent(Object aoCacheKey, String asRegionName) throws ApplicationException;

	public void removeCacheObject(Object aoCacheKey) throws ApplicationException;

	public void removeCacheObject(Object aoCacheKey, String asRegionName) throws ApplicationException;

	public void clear() throws ApplicationException;

	public void clear(String asRegionName) throws ApplicationException;

	public void clearCache() throws ApplicationException;

	public void clearCache(String asRegionName) throws ApplicationException;

	public void userP13cache(boolean abUseP13Cache);
}
