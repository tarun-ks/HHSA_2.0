package com.nyc.hhs.frameworks.cache;

import com.nyc.hhs.exception.ApplicationException;

/**
 * Implementors define a caching algorithm. All implementors must be thread
 * safe.
 * 
 */

public interface ICache
{

	public Object get(Object aoKey) throws ApplicationException;

	public void put(Object aoKey, Object aoValue) throws ApplicationException;

	public void remove(Object aoKey) throws ApplicationException;

	public void clear() throws ApplicationException;

	public void destroy() throws ApplicationException;

	public void lock(Object aoKey) throws ApplicationException;

	public void unlock(Object aoKey) throws ApplicationException;

	public long nextTimestamp();

	public int getTimeout();

}
