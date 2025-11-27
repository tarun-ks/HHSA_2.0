package com.nyc.hhs.db.cache;

import java.util.HashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.CacheException;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;

/**
 * Cache adapter for Coherencecache.
 * 
 */
public final class CoherenceMyBatisCache implements Cache
{
	private static final LogInfo LOG_OBJECT = new LogInfo(CoherenceMyBatisCache.class);
	/**
	 * The {@code ReadWriteLock}.
	 */
	private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

	/**
	 * The cache id.
	 */
	private String id;
	private Long recacheTime;

	public CoherenceMyBatisCache()
	{

	}

	/**
	 * 
	 * 
	 * @param id
	 */
	public CoherenceMyBatisCache(final String id)
	{
		if (id == null)
		{
			throw new IllegalArgumentException("Cache instances require an ID");
		}
		this.id = id;
		if (CacheFactory.getCache(id) == null)
		{
			throw new IllegalStateException("Unable to get cache: " + id);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void clear()
	{
		this.getCache().clear();
	}

	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getId()
	{
		return this.id;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getObject(Object key)
	{
		try
		{
			LOG_OBJECT.Debug("::Report Cache Key get::" + key);
			return this.getCache().get(key);
		}
		catch (Throwable t)
		{
			throw new CacheException(t);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public ReadWriteLock getReadWriteLock()
	{
		return this.readWriteLock;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getSize()
	{
		try
		{
			return this.getCache().size();
		}
		catch (Throwable t)
		{
			throw new CacheException(t);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void putObject(Object key, Object value)
	{
		try
		{
			LOG_OBJECT.Debug("::Report Cache Key put::" + key);
			if (recacheTime == null)
			{
				HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb
						.getInstance().getCacheObject(ApplicationConstants.APPLICATION_SETTING);
				String loRecacheTime = (String) loApplicationSettingMap.get(HHSR5Constants.REPORTING_CACHE_TIME);
				recacheTime = Long.parseLong(loRecacheTime, 10);
			}
			this.getCache().put(key, value, recacheTime);
		}
		catch (Throwable t)
		{
			throw new CacheException(t);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Object removeObject(Object key)
	{
		try
		{
			LOG_OBJECT.Debug("::Report Cache Key remove::" + key);
			Object obj = this.getObject(key);
			this.getCache().remove(key);
			return obj;
		}
		catch (Throwable t)
		{
			throw new CacheException(t);
		}
	}

	/**
	 * 
	 * @return the named cached
	 */
	private NamedCache getCache()
	{
		return CacheFactory.getCache(this.id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (!(obj instanceof Cache))
		{
			return false;
		}

		Cache otherCache = (Cache) obj;
		return this.id.equals(otherCache.getId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode()
	{
		return this.id.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return "Coherence {" + this.id + "}";
	}
}