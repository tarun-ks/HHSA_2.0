package com.nyc.hhs.batch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.xml.DOMConfigurator;
import org.jdom.Document;
import org.jdom.Element;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.cache.ICacheManager;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.XMLUtil;

/**
 * This BatchFactory is for the execution of batch processes
 * 
 */

public class BatchFactory

{
	private static final LogInfo LOG_OBJECT = new LogInfo(BatchFactory.class);

	public static final String BATCH_CONFIG = "/com/nyc/hhs/config/BatchConfig.xml";
	public static final String PROPERTY_FILE = "com.nyc.hhs.batch.batchConfig";
	public static final String TRANSACTION_CONFIG_FILE = "com.nyc.hhs.config.TransactionConfig";
	public static final String TRANSACTION_ELEMENT = "transaction";
	public static final String EVENT_TYPE = "/com/nyc/hhs/config/EventTypeTemplate.xml";

	/**
	 * This Main method is the execution point of the batch processes
	 * 
	 * @param args required while running this batch factory from script/command
	 *            prompt
	 */
	public static void main(String[] args)
	{
		try
		{
			Map<String, Object> loPropertyMap = new HashMap<String, Object>();
			String lsLogjPath = PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
					P8Constants.PROPERTY_PREDEFINED_LOG4J_PATH);
			DOMConfigurator.configure(lsLogjPath);

			// Set System properties for FileNet session
			System.setProperty(P8Constants.PROP_FILE_JAVA_SECURITY_AUTH_LOGIN_CONFIG, PropertyLoader.getProperty(
					P8Constants.PROPERTY_FILE, P8Constants.PROP_FILE_JAVA_SECURITY_AUTH_LOGIN_CONFIG));
			System.setProperty(P8Constants.PROP_FILE_JAVA_NAMING_FACTORY_INITIAL, PropertyLoader.getProperty(
					P8Constants.PROPERTY_FILE, P8Constants.PROP_FILE_JAVA_NAMING_FACTORY_INITIAL));
			System.setProperty(P8Constants.PROP_FILE_FILENET_PE_BOOTSTRAP_CEURI,
					PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, HHSConstants.FILENET_URI));
			long loStartTime = System.currentTimeMillis();
			LOG_OBJECT.Debug("Batch Build Number :: "
					+ PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, ApplicationConstants.PROPERTY_BUILD_NO));
			BatchFactory loBatchFactory = new BatchFactory();
			Document loBatchObject = XMLUtil.getDomObj(loBatchFactory.getClass().getResourceAsStream(BATCH_CONFIG));
			loPropertyMap.put("argumentsVal", args);
			
			if (null != args[0] && !args[0].equals(""))
			{
				
				Element loElt = XMLUtil.getElement("//batch[@name=\"" + args[0] + "\"]", loBatchObject);
				if (loElt != null)
				{
					for (Element loChildElt : (List<Element>) loElt.getChildren())
					{
						LOG_OBJECT.Debug("Stating Batch :: " + args[0] + "Started At :: " + loStartTime);
						loBatchFactory.executeBatch(loChildElt.getAttributeValue("class"),
								loChildElt.getAttributeValue("transaction"), loPropertyMap, loChildElt.getAttributeValue("key"));

					}
				}
			}
			long loEndTime = System.currentTimeMillis();
			LOG_OBJECT.Debug("Ending Batch :: " + args[0] +"End At :: " + loEndTime);
			LOG_OBJECT.Debug("Time Execution take for Batch " + args[0]+ " Run :: " + (loEndTime-loStartTime)/1000 +" secs");
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Application Exception occurred while executing batch factory:", aoExp);
		}
	}

	/**
	 * This method execute the batch process
	 * 
	 * @param asClassFile the class of batch process
	 * @param aoMParameters parameters required while executing batch
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public int executeBatch(String asClassFile, String asTransactionPath, Map aoMParameters, String asKeyName)
			throws ApplicationException
	{
		int liReturnCode = 0;
		try
		{

			ICacheManager loCacheManager = BaseCacheManagerWeb.getInstance();
			// putting Transaction config xml file into Cache
			Object loCacheObject = XMLUtil.getDomObj(this.getClass().getResourceAsStream(asTransactionPath));
			loCacheManager.putCacheObject(asKeyName, loCacheObject);
			//key name for executing r1 transaction or r2 transaction
			loCacheManager.putCacheObject(ApplicationConstants.APPLICATION_SETTING, CommonUtil.getApplicationSettingsForBatch(asKeyName));
			LOG_OBJECT.Debug("Transaction config file loaded");
			Object loCacheNotificationObject = XMLUtil.getDomObj(this.getClass().getResourceAsStream(EVENT_TYPE));
			loCacheManager.putCacheObject("notificationContent", loCacheNotificationObject);
			IBatchQueue loBatch = (IBatchQueue) Class.forName(asClassFile).newInstance();
			List loQueue = loBatch.getQueue(aoMParameters);
			loBatch.executeQueue(loQueue);
			liReturnCode = 1;
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Application Exception occurred while executing batch factory:", aoExp);
		}
		catch (InstantiationException aoExp)
		{
			LOG_OBJECT.Error("InstantiationException Exception occurred while executing batch factory:", aoExp);
		}
		catch (IllegalAccessException aoExp)
		{
			LOG_OBJECT.Error("IllegalAccessException Exception occurred while executing batch factory:", aoExp);
		}
		catch (ClassNotFoundException aoExp)
		{
			LOG_OBJECT.Error("ClassNotFoundException Exception occurred while executing batch factory:", aoExp);
		}
		return liReturnCode;
	}

	/**
	 * This method logs the error messages
	 * 
	 * @param aoEx is the instance of class Throwable
	 * @throws ApplicationException
	 */
	public static void printStackTrace(Exception aoEx)throws ApplicationException
	{
		if (aoEx instanceof ApplicationException)
		{
			LOG_OBJECT.Debug(((ApplicationException) aoEx).getStackTraceAsString());
		}
		else
		{
			LOG_OBJECT.Debug(ApplicationException.getStackTraceForThrowable(aoEx));
		}
	}

}
