package com.nyc.hhs.batch.impl;

import java.util.List;
import java.util.Map;

import com.nyc.hhs.batch.IBatchQueue;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.CityUserDetailsBeanForBatch;
import com.nyc.hhs.service.filenetmanager.p8services.P8HelperServices;

/**
 * This class added for 3.1.0 enhancement: 6025 will be used to perform all
 * batch process for deleting the Zipped file for which the procurement has been closed or cancelled.
 */

public class ProcurementDocsZipDeleteBatch extends P8HelperServices implements IBatchQueue
{

	private static final LogInfo LOG_OBJECT = new LogInfo(ProcurementDocsZipDeleteBatch.class);

	public ProcurementDocsZipDeleteBatch()
	{
		//Log4j initialization commented for incident INC000000265040
		// Load log4j property file
		//String lsLog4jPath = null;
		try
		{
			LOG_OBJECT.Debug("Constructor initialized");
			// Load property file for log4j
			//DOMConfigurator.configure(lsLog4jPath);

			LOG_OBJECT.Debug("Creating Filenet Connection");
			// Load transaction xmlsl

			String lsClassName = this.getClass().getName();
			int liIndex = lsClassName.lastIndexOf(HHSConstants.DOT);
			if (liIndex > -1)
			{
				lsClassName = lsClassName.substring(liIndex + 1);
			}
			lsClassName = lsClassName + HHSConstants.DOT_CLASS;
			String lsCastorPath = (this.getClass().getResource(lsClassName) + HHSConstants.EMPTY_STRING).replace(
					HHSConstants.PDF_CLASS, HHSConstants.CASTOR_MAPPING);

			BaseCacheManagerWeb.getInstance().putCacheObject(HHSConstants.CASTER_CONFIGURATION_PATH, lsCastorPath);

		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured during initializing the ProcurementDocsZipBatch constructor", aoAppEx);

		}
	}

	/**
	 * Blank implementation of getQueue method of interface
	 * 
	 * @param aoMParameters HashMap of Parameter
	 * @return null
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public List<CityUserDetailsBeanForBatch> getQueue(Map aoMParameters)
	{
		return null;
	}

	/**
	 * Implementation of the executeQueue method . This method will delete all
	 * those zip files of the documents for which procurement has been closed or
	 * cancelled.
	 * @throws ApplicationException ApplicationException Object
	 */
	@Override
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public void executeQueue(List aoLQueue) throws ApplicationException
	{

		Channel loChannelObj = null;
		try
		{

			loChannelObj = new Channel();

			// Fetch The Procurement Documents for which Zip Batch needs to be
			// run
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.DELETE_ZIP_PROCUREMENT_DOCUMENTS);
		}
		// Handle the Application Exception and log it into logger
		catch (ApplicationException aoAppEx)
		{
				LOG_OBJECT.Error("ApplicationException in ProcurementDocsZipDeleteBatch.executeQueue()",
						aoAppEx);
				throw aoAppEx;
		}
		// Handle the Exception and log it into logger
		catch (Exception aoExe)
		{
			LOG_OBJECT.Error("Exception in ProcurementDocsZipDeleteBatch.executeQueue()",
					aoExe);
			throw new ApplicationException("Exception in ProcurementDocsZipDeleteBatch.executeQueue()", aoExe);

		}
		// Finally block to be executed after creating a document in temporary
		// folder

	}
}
