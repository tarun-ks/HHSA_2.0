package com.nyc.hhs.batch.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.batch.IBatchQueue;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.CityUserDetailsBeanForBatch;
import com.nyc.hhs.model.ExtendedDocument;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8services.P8HelperServices;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This class added for 3.1.0 enhancement: 6025 will be used to perform all
 * batch process for zipping all the procurement documents in one file and
 * saving the zipped file in a specified path for all those procurements whose
 * zip request has been made by the user.
 */

public class ProcurementDocsZipBatch extends P8HelperServices implements IBatchQueue
{

	private static final LogInfo LOG_OBJECT = new LogInfo(ProcurementDocsZipBatch.class);

	public ProcurementDocsZipBatch()
	{
		//Log4j initialization commented for incident INC000000265040 
		// Load log4j property file
		//String lsLog4jPath = null;
		try
		{
			// Load property file for log4j

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
	 * Implementation of the executeQueue method . This method will fetch all
	 * those procurements for a specific provider from DB whose document zip
	 * request has been made by the user and then zip all the documents in one
	 * folder and save them on a specified location.
	 * @throws ApplicationException ApplicationException Object
	 */
	@Override
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public void executeQueue(List aoLQueue) throws ApplicationException
	{
		String lsBudgetId = null;
		String lsProcurementId = null;
		String lsProcurementTitle = null;
		String lsAwardId = null;
		List<String> loProposalIdList = null;
		Channel loChannelForProc = new Channel();
		String lsProviderOrgId = null;
		Channel loChannelObj = null;
		HashMap loHMArgs = new HashMap();
		SqlSession loFilenetPEDBSession = null;
		List<ExtendedDocument> loDocumentList = new ArrayList<ExtendedDocument>();
		try
		{

			loChannelObj = new Channel();
			P8UserSession loFilenetSession = filenetConnection.setP8SessionVariables();
			loFilenetPEDBSession = HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory().openSession();
			loFilenetSession.setFilenetPEDBSession(loFilenetPEDBSession);

			// Fetch The Procurement Documents for which Zip Batch needs to be
			// run
			loHMArgs.put(HHSConstants.STATUS_ID, PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.DOCUMENTS_REQUESTED));
			loChannelObj.setData(HHSConstants.AO_HM_ARGS, loHMArgs);
			HHSTransactionManager.executeTransaction(loChannelObj,
					HHSConstants.FETCH_DOCUMENT_DETAILS_FOR_ZIP_BATCH_PROCESS);
			loDocumentList = (List<ExtendedDocument>) loChannelObj.getData(HHSConstants.AO_DOCUMENTS_DETAILS_LIST);
			for (ExtendedDocument loDocumentDetails : loDocumentList)
			{
				lsProviderOrgId = loDocumentDetails.getOrganizationId();
				String lsContractId = loDocumentDetails.getContractId();
				String lsEvaluationPoolMappingId = loDocumentDetails.getEvaluationPoolMappingId();
				String lsOrgName = loDocumentDetails.getOrganizationName().replaceAll(HHSConstants.REMOVE_SPCL_CHAR,
						HHSConstants.EMPTY_STRING);
				String lsRealpath = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
						HHSConstants.ZIP_DOCUMENTS_PATH);
				if (null != loDocumentDetails.getBudgetId())
				{
					lsBudgetId = loDocumentDetails.getBudgetId();
					loChannelForProc.setData(HHSConstants.BUDGET_ID_KEY, lsBudgetId);
				}
				lsProcurementId = loDocumentDetails.getProcurementId();
				lsProcurementTitle = loDocumentDetails.getProcurementTitle();
				lsAwardId = loDocumentDetails.getAwardId();
				loProposalIdList = loDocumentDetails.getDocProposalIds();
				loChannelForProc.setData(HHSConstants.CONTRACT_ID_KEY, lsContractId);
				loChannelForProc.setData(HHSConstants.AO_OUTPUT_PATH, lsRealpath);
				loChannelForProc.setData(HHSConstants.AS_AWARD_ID, lsAwardId);
				loChannelForProc.setData(HHSConstants.PROPOSAL_ID_KEY, loProposalIdList);
				loChannelForProc.setData(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
				loChannelForProc.setData(HHSConstants.AS_EVALUATION_POOL_MAPPING_ID, lsEvaluationPoolMappingId);
				loChannelForProc.setData(HHSConstants.PROCUREMENT_TITLE, lsProcurementTitle);
				loChannelForProc.setData(HHSConstants.AO_FILENET_SESSION, loFilenetSession);
				loChannelForProc.setData(HHSConstants.AO_DOCUMENT_TITLE, loDocumentDetails.getDocumentTitle());
				loChannelForProc.setData(HHSConstants.AS_FOLDER_NAME_AWARD_DOC, loDocumentDetails.getDocumentTitle()
						+ File.separator + lsOrgName + HHSConstants.UNDERSCORE_AWARD_DOCUMENTS);
				loChannelForProc.setData(HHSConstants.AS_FOLDER_NAME_RFP_DOC, loDocumentDetails.getDocumentTitle()
						+ File.separator + HHSConstants.RFP_DOCUMENT);
				loChannelForProc.setData(HHSConstants.AS_FOLDER_NAME_PROPOSAL_DOC, loDocumentDetails.getDocumentTitle()
						+ File.separator + lsOrgName + HHSConstants.UNDERSCORE_PROPOSAL_DOCUMENTS);
				loChannelForProc.setData(
						HHSConstants.FINANCIAL_PDF_DOC_PATH,
						PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
								P8Constants.PREDEFINED_FOLDER_PATH_FINANCIAL_DOC)
								+ HHSConstants.FORWARD_SLASH
								+ lsContractId);
				loChannelForProc.setData(HHSConstants.AS_PROVIDER_ORG_ID, lsProviderOrgId);
				try
				{
					HHSTransactionManager.executeTransaction(loChannelForProc, HHSConstants.ZIP_PROCUREMENT_DOCUMENTS);
				}
				catch (ApplicationException aoAppEx)
				{
					LOG_OBJECT.Error(
							"ApplicationException in ProcurementDocsZipBatch.executeQueue() for procurement id:"
									+ lsProcurementId, aoAppEx);
					// throw aoAppEx;
				}
			}
		}
		// Handle the Application Exception and log it into logger
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("ApplicationException in ProcurementDocsZipBatch.executeQueue()", aoAppEx);
			throw aoAppEx;
		}
		// Handle the Exception and log it into logger
		catch (Exception aoExe)
		{
			LOG_OBJECT.Error("Exception in ProcurementDocsZipBatch.executeQueue()", aoExe);
			throw new ApplicationException("Exception in ProcurementDocsZipBatch.executeQueue()", aoExe);

		}
		// Finally block to be executed after creating a document in temporary
		// folder
		finally
		{
		}

	}
}
