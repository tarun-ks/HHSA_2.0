package com.nyc.hhs.batch.impl;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.xml.DOMConfigurator;

import com.itextpdf.license.LicenseKey;
import com.nyc.hhs.batch.IBatchQueue;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.CityUserDetailsBeanForBatch;
import com.nyc.hhs.model.PDFBatch;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8services.P8HelperServices;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This class is being used for synchronizing LDAP and database records for
 * internal users
 */

public class PDFGenerationBatch extends P8HelperServices implements IBatchQueue
{

	private static final LogInfo LOG_OBJECT = new LogInfo(PDFGenerationBatch.class);
	private static final String ENVIRONMENT = System.getProperty("hhs.env");

	public PDFGenerationBatch()
	{
		//Log4j initialization commented for incident INC000000265040 
		// Load log4j property file
		//String lsLog4jPath = null;
		try
		{
			//LOG_OBJECT.Debug("Constructor initialized");
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
			LOG_OBJECT.Error("Error occured during initializing the PDFGenerationBatch constructor", aoAppEx);

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
	 * Implementation of the executeQueue method . This method will generate All
	 * the PDF's and Save into Filenet.
	 * @param aoLQueue List of Queue
	 * @throws ApplicationException ApplicationException Object
	 */
	@Override
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public void executeQueue(List aoLQueue) throws ApplicationException
	{
		String lsLog4jPath = PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
				P8Constants.PROPERTY_PREDEFINED_LOG4J_PATH);
		/*** Code added to read iText license file in staging and prod env***/
		String lsEnvironment = ENVIRONMENT;
		if (lsEnvironment != null)
		{
				String lsLicenseFilePath = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
						HHSConstants.ITEXT_LICENSE_FILE_PATH);
				if(!lsLicenseFilePath.equalsIgnoreCase(HHSConstants.NA))
				{
				LicenseKey.loadLicenseFile(lsLicenseFilePath);
				}
		}
		/**********************************************/
		String lsPDFFilePath = PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, HHSConstants.PDF_FILE_PATH);
		File loTempDir = new File(lsPDFFilePath);
		if (!loTempDir.exists())
		{
			loTempDir.mkdirs();
		}
		DOMConfigurator.configure(lsLog4jPath);
		Channel loChannel = null;
		LOG_OBJECT.Info("Executing PDF Batch .. ");
		SqlSession loFilenetPEDBSession = null;
		try
		{
			P8UserSession loFilenetSession = filenetConnection.setP8SessionVariables();
			loFilenetPEDBSession = HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory().openSession();
			loFilenetSession.setFilenetPEDBSession(loFilenetPEDBSession);
			loChannel = new Channel();
			HashMap loReqMap = new HashMap();
			loReqMap.put(HHSConstants.NEW_ENTITY_TYPE, HHSConstants.CONTRACT);
			loReqMap.put(HHSConstants.STATUS_COLUMN, PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.PDF_NOT_STARTED));
			loReqMap.put(HHSConstants.STATUS_ID, PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.PDF_IN_PROGRESS));
			loChannel.setData(HHSConstants.AO_PARAM_MAP, loReqMap);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_ENTITY_ID_UPDATE_STATUS_FOR_PDF);
			List<PDFBatch> loEntityIdList = (List<PDFBatch>) loChannel.getData(HHSConstants.ENTITY_LIST);
			loChannel = new Channel();
			for (PDFBatch loPdfDetails : loEntityIdList)
			{
				try
				{
					loChannel.setData(HHSConstants.AO_FILENET_SESSION, loFilenetSession);
					loChannel.setData(HHSConstants.AO_OUTPUT_PATH, lsPDFFilePath);

					loReqMap.put(HHSConstants.ENTITY_ID, loPdfDetails.getEntityId());
					loReqMap.put(HHSConstants.SUB_ENTITY_ID, loPdfDetails.getSubEntityId());
					loChannel.setData(HHSConstants.AO_PARAM_MAP, loReqMap);
					loChannel.setData(HHSConstants.PDF_BATCH, loPdfDetails);
					loChannel.setData(HHSConstants.HEADER_LABEL, HHSConstants.EMPTY_STRING);
					HHSTransactionManager.executeTransaction(loChannel, HHSConstants.UPLOAD_AMENDMET_DOC_IN_FILENET);
				}
				catch (ApplicationException aoAppEx)
				{
					LOG_OBJECT.Error("Exception in pdf.executeQueue()", aoAppEx);
				}
				catch (Exception aoEx)
				{
					LOG_OBJECT.Error("Error while executing pdf.executeQueue() ..", aoEx);
				}
			}
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in pdf.executeQueue()", aoAppEx);
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error while executing pdf.executeQueue() ..", aoEx);
		}
	}
}
