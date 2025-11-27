package com.nyc.hhs.frameworks.cache;

import com.nyc.hhs.exception.ApplicationException;

/**
 * This interface defines the method to implement the logic to create a unique
 * key for a cached object.
 * 
 */

public interface ICacheKey
{
	public Object getCacheKey() throws ApplicationException;
}
