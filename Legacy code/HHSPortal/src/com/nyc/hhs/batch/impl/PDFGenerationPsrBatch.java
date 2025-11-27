package com.nyc.hhs.batch.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.itextpdf.license.LicenseKey;
import com.nyc.hhs.batch.IBatchQueue;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8services.P8HelperServices;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This class is being used for Creating the PSR PDF files
 * and store them into Filenet
 */

public class PDFGenerationPsrBatch extends P8HelperServices implements IBatchQueue
{

	private static final LogInfo LOG_OBJECT = new LogInfo(PDFGenerationBatch.class);
	private static final String ENVIRONMENT = System.getProperty("hhs.env");
	/**
	 * Blank implementation of getQueue method of interface
	 * 
	 * @param aoMParameters HashMap of Parameter
	 * @return null
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public List getQueue(Map aoMParameters)
	{
		return null;
	}

	/**
	 * Implementation of the executeQueue method . This method will generate PSR
	 * PDF's and Save into Filenet.
	 * @param aoLQueue List of Queue
	 * @throws ApplicationException ApplicationException Object
	 */
	@Override
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public void executeQueue(List aoLQueue) throws ApplicationException
	{
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
		Channel loChannel = new Channel();
		LOG_OBJECT.Info("Executing PSR PDF Batch .. ");
		try
		{
			List<String> loEntityIdList = null;
			P8UserSession loFilenetSession = filenetConnection.setP8SessionVariables();
			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.FETCH_PSR_PDF_DETAILS,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			loEntityIdList = (ArrayList) loChannel.getData(HHSConstants.ENTITY_LIST);
			for (String lsPdfIndex : loEntityIdList)
			{
				HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
				HashMap<String, Object> loTempMap = new HashMap<String, Object>();
				loTempMap.put(HHSConstants.PROPERTY_PE_PROCURMENT_ID, lsPdfIndex);
				TaskDetailsBean loTaskDetailBean = new TaskDetailsBean();
				loTaskDetailBean.setWorkFlowId(HHSR5Constants.PDF_KEY);
				loTaskDetailBean.setEntityId(lsPdfIndex);
				loTaskDetailMap.put(HHSR5Constants.PDF_KEY, loTempMap);
				loChannel.setData(ApplicationConstants.TASK_DETAIL_MAP, loTaskDetailMap);
				loChannel.setData(ApplicationConstants.TASK_DETAILS_BEAN_KEY, loTaskDetailBean);
				loChannel.setData(HHSConstants.PROPERTY_PE_PROCURMENT_ID, lsPdfIndex);
				loChannel.setData(HHSR5Constants.PDF_FLAG, HHSConstants.TWO);
				loChannel.setData(HHSConstants.AO_FILENET_SESSION, loFilenetSession);
				HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.GENERATE_PSR_PDF,
						HHSR5Constants.TRANSACTION_ELEMENT_R5);

			}

		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in PSR pdf.executeQueue()", aoAppEx);
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error while executing PSR's pdf.executeQueue() ..", aoEx);
		}
	}
}

