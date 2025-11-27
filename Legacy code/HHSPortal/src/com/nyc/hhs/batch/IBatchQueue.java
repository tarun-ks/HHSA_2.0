package com.nyc.hhs.batch;

import java.util.List;
import java.util.Map;

import com.nyc.hhs.exception.ApplicationException;

/**
 * This interface provide methods that need to be implemented for Batch
 * processing
 * 
 */

public interface IBatchQueue
{
	public List getQueue(Map aoMParameters);

	public void executeQueue(List aoLQueue) throws ApplicationException;

}
