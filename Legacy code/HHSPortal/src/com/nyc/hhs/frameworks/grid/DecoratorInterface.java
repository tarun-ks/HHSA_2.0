package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.exception.ApplicationException;

/**
 * This interface basically acts as a mark-up interface and is implemented 
 * by the Extension classes to generate custom elements in the Grid.
 * 
 */

public interface DecoratorInterface {
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aoSeqNo) throws ApplicationException;

}



