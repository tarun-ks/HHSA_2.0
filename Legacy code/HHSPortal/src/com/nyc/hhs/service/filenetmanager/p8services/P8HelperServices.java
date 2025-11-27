package com.nyc.hhs.service.filenetmanager.p8services;

import com.nyc.hhs.daomanager.service.ServiceState;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8ContentOperations;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8ProcessOperations;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8SecurityOperations;

/**
 * This class is responsible for create objects of CE related classes. This
 * objects are used by CE related classes to avoiding creation of multiple
 * objects of same class.
 * 
 */

public class P8HelperServices extends ServiceState
{
	protected static P8ContentOperations contentOperationHelper = new P8ContentOperations();

	protected static P8SecurityOperations filenetConnection = new P8SecurityOperations();

	protected static P8ProcessOperations peOperationHelper = new P8ProcessOperations();
}