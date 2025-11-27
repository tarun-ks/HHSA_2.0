/**
 * 
 */
package com.batch.bulkupload;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.util.PropertyLoader;

/**
 *This class will work as a factory to return Bulk upload batch process implementing object
 */
public class TemplateProcessFactory
{
	private static final LogInfo LOG_OBJECT = new LogInfo(TemplateProcessFactory.class);
	/**
	 * This method return object of implemention of Bulk upload contract Service interface for the version provided.
	 * @param asVersion
	 * @return ProcessBulkUploadContracts bean object
	 * @throws ApplicationException if any exception occurred
	 */
	public static ProcessBulkUploadContracts getTemplateProcessObj(String asVersion) throws ApplicationException
	{

		ProcessBulkUploadContracts loTemplateObj = null;

		if (asVersion != null)
		{

			String lsClass;
			try
			{
				lsClass = PropertyLoader.getProperty("com.batch.bulkupload.templatefactory", HHSConstants.BULK_UPLOAD_TEMPLATE_NAME + asVersion);
				Class loClass = Class.forName(lsClass);
				loTemplateObj = (ProcessBulkUploadContracts) loClass.newInstance();
			}
			catch (ApplicationException loAppExp)
			{
				LOG_OBJECT.Error("Application Exception occurred while executing TemplateProcessFactory", loAppExp);
				throw loAppExp;
			}
			catch (ClassNotFoundException loClsExp)
			{
				LOG_OBJECT.Error("ClassNotFoundException Exception occurred while executing TemplateProcessFactory", loClsExp);
				ApplicationException loAppEx=new ApplicationException("ClassNotFoundException Exception occurred while executing TemplateProcessFactory", loClsExp);
				throw loAppEx;
			}
			catch (InstantiationException loInstExp)
			{
				LOG_OBJECT.Error("InstantiationException Exception occurred while executing TemplateProcessFactory", loInstExp);
				ApplicationException loAppEx=new ApplicationException("InstantiationException Exception occurred while executing TemplateProcessFactory", loInstExp);
				throw loAppEx;
			}
			catch (IllegalAccessException loIllegalExp)
			{
				LOG_OBJECT.Error("IllegalAccessException Exception occurred while executing TemplateProcessFactory", loIllegalExp);
				ApplicationException loAppEx=new ApplicationException("IllegalAccessException Exception occurred while executing TemplateProcessFactory", loIllegalExp);
				throw loAppEx;
			}

		}
		return loTemplateObj;
	}

}
