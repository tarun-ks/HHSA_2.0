package com.nyc.hhs.junit.util;

import java.io.File;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.cache.ICacheManager;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.util.XMLUtil;

public class JUnitUtil
{
	private static final LogInfo LOG_OBJECT = new LogInfo(JUnitUtil.class);
	
	/**
	 * This method loads the TransactionConfigR2.xml into cache object.
	 * It is used while writing JUnits for Util classes as there is no option
	 * to pass MyBatis session to Util class methods through parameter.
	 * 
	 * @throws ApplicationException
	 */
	@SuppressWarnings({ "unchecked" })
	public static void getTransactionManager() throws ApplicationException
	{
		try
		{
			ICacheManager loCacheManager = BaseCacheManagerWeb.getInstance();
			String lsFilePath = new File(HHSConstants.EMPTY_STRING).getAbsolutePath(); 
			/*String lsFilePath = Thread.currentThread().getContextClassLoader().getResource("com/nyc/hhs/config/TransactionConfigR2.xml")
					.getPath();*/
			Document loDocument = XMLUtil.getDomObj(lsFilePath + HHSConstants.TRANSACTION_CONFIGR2_PATH);
			List<Element> loElementChildList = (List<Element>) loDocument.getRootElement().getChildren();
			for (Element element : loElementChildList)
			{
				if(element.getAttributeValue(HHSConstants.USESDBCONNECTION)!=null)
				element.setAttribute(HHSConstants.USESLOCALDBCONNECTION, element.getAttributeValue(HHSConstants.USESDBCONNECTION));
				element.removeAttribute(HHSConstants.USESDBCONNECTION);
			}
			loCacheManager.putCacheObject("transactionR2", loDocument);
		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("JUnitUtil :: getTransactionManager --> Error in getting transactionR2.xml into Session.");
			aoAppExp = new ApplicationException("Error in getting transactionR2.xml into Session.");
			throw aoAppExp;
		}
		
	}
}
