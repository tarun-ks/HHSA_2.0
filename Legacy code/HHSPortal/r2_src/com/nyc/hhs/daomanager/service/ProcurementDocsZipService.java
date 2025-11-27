package com.nyc.hhs.daomanager.service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.ibatis.session.SqlSession;

import com.accenture.util.SaveFormOnLocalUtil;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.contractsbatch.service.ContractsBatchService;
import com.nyc.hhs.controllers.util.DownloadDBDDocsThread;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.AwardBean;
import com.nyc.hhs.model.ExtendedDocument;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This service class is added for 3.1.0 enhancement: 6025 which processes all
 * operations and Batch for requesting, downloading and deleting zip file for
 * the Award Documents of a specific provider of a procurement.
 */

public class ProcurementDocsZipService extends ServiceState
{

	private static final LogInfo LOG_OBJECT = new LogInfo(ContractsBatchService.class);

	/**
	 * <p>
	 * This method fetches The Procurement Documents for which Zip Batch needs
	 * to be run.
	 * </p>
	 * 
	 * @param aoMybatisSession SqlSession object
	 * @param aoHMArgs Parameters Map
	 * @return loDocumentDetailsList List<ExtendedDocument> object
	 * @throws ApplicationException if ApplicationException is thrown
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public List<ExtendedDocument> fetchDocumentsDetailsforZipBatchProcess(SqlSession aoMybatisSession, HashMap aoHMArgs)
			throws ApplicationException
	{
		List<ExtendedDocument> loDocumentDetailsList = null;
		try
		{
			loDocumentDetailsList = (List<ExtendedDocument>) DAOUtil.masterDAO(aoMybatisSession, aoHMArgs,
					HHSConstants.MAPPER_CLASS_AWARDS_MAPPER, HHSConstants.FETCH_DOCUMENT_DETAILS_FOR_ZIP_BATCH_PROCESS,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			for (ExtendedDocument loExtendedDocument : loDocumentDetailsList)
			{
				Map<String, String> aoParamMap = new HashMap<String, String>();
				String lsAwardId = null;
				List<String> lsPropsalId = null;
				Integer loIsFinancial = 0;
				aoParamMap.put(HHSConstants.PROCUREMENT_ID, loExtendedDocument.getProcurementId());
				aoParamMap
						.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, loExtendedDocument.getEvaluationPoolMappingId());
				aoParamMap.put(HHSConstants.USER_ORG_ID, loExtendedDocument.getOrganizationId());
				lsAwardId = (String) DAOUtil.masterDAO(aoMybatisSession, aoParamMap,
						HHSConstants.MAPPER_CLASS_AWARDS_MAPPER, HHSConstants.FETCH_AWARD_ID,
						HHSConstants.JAVA_UTIL_MAP);
				lsPropsalId = (List<String>) DAOUtil.masterDAO(aoMybatisSession, aoParamMap,
						HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.FETCH_PROPOSAL_ID,
						HHSConstants.JAVA_UTIL_MAP);
				loExtendedDocument.setAwardId(lsAwardId);
				loExtendedDocument.setDocProposalIds(lsPropsalId);
				loIsFinancial = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoParamMap,
						HHSConstants.MAPPER_CLASS_AWARDS_MAPPER, HHSConstants.IS_FINANCIAL_CHECK,
						HHSConstants.JAVA_UTIL_MAP);
				if (loIsFinancial != 0)
				{
					AwardBean loAwardBean = null;
					loAwardBean = (AwardBean) DAOUtil.masterDAO(aoMybatisSession, aoParamMap,
							HHSConstants.MAPPER_CLASS_AWARDS_MAPPER, HHSConstants.FETCH_AWARD_DETAILS_FOR_FINANCE,
							HHSConstants.JAVA_UTIL_MAP);
					loExtendedDocument.setContractId(loAwardBean.getContractId());
					loExtendedDocument.setBudgetId(loAwardBean.getBudgetId());
					loExtendedDocument.setProcurementTitle(loAwardBean.getProcurementTitle());
				}

			}
			setMoState("Document Details for Batch Process fetched successfully.\n");
		}
		catch (ApplicationException aoAppEx)
		{
			// Handle the ApplicationException type Exception and set moState
			// and context data
			setMoState("Error occured in ProcurementDocsZipService: fetchDocumentsDetailsforZipBatchProcess() method\n");
			LOG_OBJECT.Error(
					"Error occured in ProcurementDocsZipService :fetchDocumentsDetailsforZipBatchProcess() method",
					aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoExp)
		{
			// Handle the Exception type exception and set moState
			// and context data
			ApplicationException loAppEx = new ApplicationException(
					"Error occured in ProcurementDocsZipService: fetchDocumentsDetailsforZipBatchProcess() method\n",
					aoExp);
			setMoState("Error occured in ProcurementDocsZipService: fetchDocumentsDetailsforZipBatchProcess() method\n");
			LOG_OBJECT.Error(
					"Error occured in ProcurementDocsZipService: fetchDocumentsDetailsforZipBatchProcess() method\n",
					loAppEx);
			throw loAppEx;
		}
		return loDocumentDetailsList;
	}

	/**
	 * <p>
	 * This method updated document status in DB to 'Ready to Download' once the
	 * batch successfully runs.
	 * <ul>
	 * <li>Execute query updateDocumentDownloadRequest to update status in DB.</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatisSession : SqlSession Object
	 * @param aoDocumentTitle : String containing DocumentTitle
	 * @param asProcurementId : String containing ProcurementId
	 * @return loRowsModified : int value containing modified rows
	 * @throws ApplicationException : If ApplicationException occurs
	 */
	public Integer updateDocumentStatus(SqlSession aoMybatisSession, String asProcurementId, String asProviderOrgID,
			String asEvaluationPoolMappingId) throws ApplicationException
	{
		Integer loRowsModified = HHSConstants.INT_ZERO;
		try
		{
			Map<String, String> aoParamMap = new HashMap<String, String>();
			aoParamMap.put(HHSConstants.AS_PROVIDER_ORG_ID, asProviderOrgID);
			aoParamMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
			aoParamMap.put(HHSConstants.AS_EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
			aoParamMap.put(HHSConstants.AS_STATUS_ID, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.DOCUMENTS_READY_TO_DOWNLOAD));

			loRowsModified = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoParamMap,
					HHSConstants.MAPPER_CLASS_AWARDS_MAPPER, HHSConstants.UPDATE_DOC_DOWNLOAD_REQUEST_FOR_BATCH,
					HHSConstants.JAVA_UTIL_MAP);
			setMoState("Document Status updated successfully.\n");
		}
		catch (ApplicationException aoAppEx)
		{
			// Handle the ApplicationException type Exception and set moState
			// and context data
			setMoState("Error occured in ProcurementDocsZipService: updateDocumentStatus() method");
			LOG_OBJECT.Error("Error occured in ProcurementDocsZipService: updateDocumentStatus() method", aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoExp)
		{
			// Handle the Exception type exception and set moState
			// and context data
			ApplicationException loAppEx = new ApplicationException(
					"Error occured in ProcurementDocsZipService: updateDocumentStatus() method", aoExp);
			setMoState("Error occured in ProcurementDocsZipService: updateDocumentStatus() method");
			LOG_OBJECT.Error("Error occured in ProcurementDocsZipService: updateDocumentStatus() method", loAppEx);
			throw loAppEx;
		}
		return loRowsModified;
	}

	/**
	 * <p>
	 * This method updated procurement status in DB when the procurement is
	 * closed or cancelled.
	 * <ul>
	 * <li>Execute query updateProcStatusForDocs to update status in DB.</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatisSession : SqlSession object
	 * @param asProcurementId : String containing ProcurementId
	 * @param asUserId : String containing UserId
	 * @param asStatusId : String containing StatusId
	 * @return loRowsModified : int value containing modified rows
	 * @throws ApplicationException
	 */
	public Integer updateProcStatusForDocs(SqlSession aoMybatisSession, String asProcurementId, String asUserId,
			String asStatusId) throws ApplicationException
	{
		Integer loRowsModified = HHSConstants.INT_ZERO;
		try
		{
			Map<String, String> aoParamMap = new HashMap<String, String>();
			aoParamMap.put(HHSConstants.AS_USER_ID, asUserId);
			aoParamMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
			aoParamMap.put(HHSConstants.AS_STATUS_ID, asStatusId);

			loRowsModified = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoParamMap,
					HHSConstants.MAPPER_CLASS_AWARDS_MAPPER, HHSConstants.UPDATE_PROC_STATUS_FOR_DOCS,
					HHSConstants.JAVA_UTIL_MAP);
			setMoState("Procurement Status updated successfully.\n");
		}
		catch (ApplicationException aoAppEx)
		{
			// Handle the ApplicationException type Exception and set moState
			// and context data
			setMoState("Error occured in ProcurementDocsZipService: updateProcStatusForDocs() method");
			LOG_OBJECT.Error("Error occured in ProcurementDocsZipService: updateProcStatusForDocs() method", aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoExp)
		{
			// Handle the Exception type exception and set moState
			// and context data
			ApplicationException loAppEx = new ApplicationException(
					"Error occured in ProcurementDocsZipService: updateProcStatusForDocs() method", aoExp);
			setMoState("Error occured in ProcurementDocsZipService: updateProcStatusForDocs() method");
			LOG_OBJECT.Error("Error occured in ProcurementDocsZipService: updateProcStatusForDocs() method", loAppEx);
			throw loAppEx;
		}
		return loRowsModified;
	}

	/**
	 * <p>
	 * This method fetches all those records for which procurement status is
	 * closed and cancelled and delete the zip folder for the same.
	 * <ul>
	 * <li>Execute query fetchDocumentDetailsforZipDeleteBatchProcess for
	 * fetching all those records whose procurement status is closed and
	 * cancelled</li>
	 * <li>For the fetched records the zipped folder is deleted</li>
	 * <li>Execute query updateDocumentDownloadRequest to update the records
	 * status in DB</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatisSession : SqlSession object
	 * @return loRowsModified : int value containing modified rows
	 * @throws ApplicationException : if ApplicationException is launched
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Integer deleteZipProcurementDocuments(SqlSession aoMybatisSession) throws ApplicationException
	{
		List<ExtendedDocument> loDocumentDetailsList = null;
		String lsFolderPath = null;
		String lsParentFolderName = null;
		Integer loRowsModified = HHSConstants.INT_ZERO;
		Map<String, String> loParamMap = new HashMap<String, String>();
		try
		{
			loDocumentDetailsList = (List<ExtendedDocument>) DAOUtil.masterDAO(aoMybatisSession, null,
					HHSConstants.MAPPER_CLASS_AWARDS_MAPPER,
					HHSConstants.FETCH_DOCUMENT_DETAILS_FOR_ZIP_DELETE_BATCH_PROCESS, null);

			for (ExtendedDocument loDocumentDetails : loDocumentDetailsList)
			{
				try
				{
					lsParentFolderName = loDocumentDetails.getProcurementId() + HHSConstants.UNDERSCORE
							+ loDocumentDetails.getEvaluationPoolMappingId() + HHSConstants.UNDERSCORE
							+ loDocumentDetails.getOrganizationId();
					lsFolderPath = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
							HHSConstants.ZIP_DOCUMENTS_PATH) + HHSConstants.FORWARD_SLASH + lsParentFolderName;

					SaveFormOnLocalUtil.deleteFolder(new File(lsFolderPath));

					loParamMap.put(HHSConstants.AS_EVALUATION_POOL_MAPPING_ID,
							loDocumentDetails.getEvaluationPoolMappingId());
					loParamMap.put(HHSConstants.AS_PROVIDER_ORG_ID, loDocumentDetails.getOrganizationId());
					loParamMap.put(HHSConstants.PROCUREMENT_ID_KEY, loDocumentDetails.getProcurementId());
					loParamMap.put(HHSConstants.AS_STATUS_ID, PropertyLoader.getProperty(
							HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.DOCUMENTS_EXPIRED));
					loRowsModified = (Integer) DAOUtil.masterDAO(aoMybatisSession, loParamMap,
							HHSConstants.MAPPER_CLASS_AWARDS_MAPPER,
							HHSConstants.UPDATE_DOC_DOWNLOAD_DELETE_REQUEST_FOR_BATCH, HHSConstants.JAVA_UTIL_MAP);
					setMoState("Procurement Documents Zip folder deleted successfully");
				}
				catch (ApplicationException aoAppEx)
				{
					// Handle the ApplicationException type Exception and set
					// moState
					// and context data
					setMoState("Error occured in ProcurementDocsZipService:fetchDocumentDetailsforZipDeleteBatchProcess() method");
					LOG_OBJECT
							.Error("ApplicationException occured in ProcurementDocsZipService:fetchDocumentDetailsforZipDeleteBatchProcess() method for the procurement id:"
									+ loDocumentDetails.getProcurementId(), aoAppEx);
				}

			}
		}
		catch (ApplicationException aoAppEx)
		{
			// Handle the ApplicationException type Exception and set moState
			// and context data
			setMoState("Error occured in ProcurementDocsZipService:fetchDocumentDetailsforZipDeleteBatchProcess() method");
			LOG_OBJECT
					.Error("ApplicationException occured in ProcurementDocsZipService:fetchDocumentDetailsforZipDeleteBatchProcess() method",
							aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoExp)
		{
			// Handle the Exception type exception and set moState
			// and context data
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured in ProcurementDocsZipService:fetchDocumentDetailsforZipDeleteBatchProcess() method",
					aoExp);
			setMoState("Error occured in ProcurementDocsZipService:fetchDocumentDetailsforZipDeleteBatchProcess() method");
			LOG_OBJECT.Error(
					"Error occured in ProcurementDocsZipService:fetchDocumentDetailsforZipDeleteBatchProcess() method",
					loAppEx);
			throw loAppEx;
		}
		return loRowsModified;
	}

	/**
	 * <p>
	 * This method is used to insert/update the document details in DB when the
	 * user requests 'New Zip File' from the view Award Documents Screen.
	 * <ul>
	 * <li>Get the document details map from the argument</li>
	 * <li>If the document status is 'Not Requested' then new entry is instered
	 * in the DB by executing <code>insertDocumentDownloadRequest</code> query
	 * <li>Else If the document status is 'Ready to Download', then the current
	 * DB entry is updated for the status and status is again set to 'Generating
	 * file' by executing <code>updateDocumentDownloadRequest</code> query
	 * <li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatisSession Sql Session Object
	 * @param aoParamMap Document details parameter map
	 * @return loAwardDocDetails ExtendedDocument object
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public ExtendedDocument requestNewZipFileAction(SqlSession aoMybatisSession, Map<String, String> aoParamMap)
			throws ApplicationException
	{
		Integer loRowsModified = HHSConstants.INT_ZERO;
		ExtendedDocument loAwardDocDetails = null;
		try
		{
			if (null != aoParamMap && null != aoParamMap.get(HHSConstants.AS_DOC_STATUS)
					&& !aoParamMap.get(HHSConstants.AS_DOC_STATUS).isEmpty())
			{
				if (aoParamMap.get(HHSConstants.AS_DOC_STATUS).equalsIgnoreCase(
						PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.DOCUMENTS_NOT_REQUESTED)))
				{
					loRowsModified = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoParamMap,
							HHSConstants.MAPPER_CLASS_AWARDS_MAPPER, HHSConstants.INSERT_DOC_DOWNLOAD_REQUEST,
							HHSConstants.JAVA_UTIL_MAP);

				}
				else if (aoParamMap.get(HHSConstants.AS_DOC_STATUS).equalsIgnoreCase(
						PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.DOCUMENTS_READY_TO_DOWNLOAD)))
				{
					aoParamMap.put(HHSConstants.AS_STATUS_ID, PropertyLoader.getProperty(
							HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.DOCUMENTS_REQUESTED));
					loRowsModified = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoParamMap,
							HHSConstants.MAPPER_CLASS_AWARDS_MAPPER, HHSConstants.UPDATE_DOC_DOWNLOAD_REQUEST,
							HHSConstants.JAVA_UTIL_MAP);
				}
				if (loRowsModified == HHSConstants.INT_ZERO)
				{
					throw new ApplicationException(
							"Error occured in ProcurementDocsZipService : requestNewZipFileAction() method for the Procurement Id:"
									+ aoParamMap.get(HHSConstants.PROCUREMENT_ID_KEY) + "and the Organization Id: "
									+ aoParamMap.get(HHSConstants.AS_PROVIDER_ORG_ID));
				}
				else
				{

					String lsStatusId = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
							HHSConstants.DOCUMENTS_REQUESTED);
					String lsStatus = (String) DAOUtil.masterDAO(aoMybatisSession, lsStatusId,
							HHSConstants.MAPPER_CLASS_AWARDS_MAPPER, HHSConstants.FETCH_DOCUMENT_STATUS,
							HHSConstants.JAVA_LANG_STRING);
					loAwardDocDetails = new ExtendedDocument();
					loAwardDocDetails.setStatus(lsStatus);
					loAwardDocDetails.setStatusId(lsStatusId);
					loAwardDocDetails.setModifiedDate(HHSConstants.BLANK_SYMBOL);
					loAwardDocDetails.setDocumentTitle(aoParamMap.get(HHSConstants.AS_FILE_NAME_PARAMETER));
				}
				setMoState("Successfully requested new zip file for the Procurement Id:"
						+ aoParamMap.get(HHSConstants.PROCUREMENT_ID_KEY) + "and the Organization Id: "
						+ aoParamMap.get(HHSConstants.AS_PROVIDER_ORG_ID));
			}
		}
		// Catch the application exception thrown from dao layer and log it
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error(
					"Error occured in ProcurementDocsZipService:requestNewZipFileAction() method for the Procurement Id:"
							+ aoParamMap.get(HHSConstants.PROCUREMENT_ID_KEY) + "and the Organization Id: "
							+ aoParamMap.get(HHSConstants.AS_PROVIDER_ORG_ID), aoAppExp);
			setMoState("Error occured in ProcurementDocsZipService:requestNewZipFileAction() method");
			throw aoAppExp;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error(
					"Error occured in ProcurementDocsZipService:requestNewZipFileAction() method for the Procurement Id:"
							+ aoParamMap.get(HHSConstants.PROCUREMENT_ID_KEY) + "and the Organization Id: "
							+ aoParamMap.get(HHSConstants.AS_PROVIDER_ORG_ID), loExp);
			setMoState("Error occured in ProcurementDocsZipService:requestNewZipFileAction() method");
			throw new ApplicationException(
					"Error occured in ProcurementDocsZipService:requestNewZipFileAction() method", loExp);
		}
		return loAwardDocDetails;
	}

	/**
	 * <p>
	 * This method fetches awardDocDetails corresponding to file Name
	 * <ul>
	 * <li>1. Fetches awardDocDetails for corresponding file name using
	 * <b>fetchAwardDocDetails</b> from proposal mapper</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asFileName - file name
	 * @return Map of awardDocDetails
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public ExtendedDocument fetchAwardDocDetails(SqlSession aoMybatisSession, Map<String, String> asOrgDetailsMap,
			Map<String, String> asParametersMap) throws ApplicationException
	{
		ExtendedDocument loAwardDocDetails = null;
		Map<String, String> loInputParam = new HashMap<String, String>();
		String lsOrgName = null;
		try
		{
			if (null != asOrgDetailsMap && null != asParametersMap)
			{
				lsOrgName = asOrgDetailsMap.get(HHSConstants.ORGANIZATION_NAME_CAPS);
				lsOrgName = lsOrgName.replaceAll(HHSConstants.REMOVE_SPCL_CHAR, HHSConstants.EMPTY_STRING);
				String lsFileName = lsOrgName + HHSConstants.UNDERSCORE_AWARD_DOCUMENTS;

				loInputParam.put(HHSConstants.PROCUREMENT_ID_KEY, asParametersMap.get(HHSConstants.PROCUREMENT_ID));
				loInputParam.put(HHSConstants.AS_PROVIDER_ORG_ID, asParametersMap.get(HHSConstants.USER_ORG_ID));
				loInputParam.put(HHSConstants.AS_EVALUATION_POOL_MAPPING_ID,
						asParametersMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID));

				loAwardDocDetails = (ExtendedDocument) DAOUtil.masterDAO(aoMybatisSession, loInputParam,
						HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.FETCH_AWARD_DOC_DETAILS,
						HHSConstants.JAVA_UTIL_MAP);
				if (null == loAwardDocDetails)
				{
					String lsStatusId = null;
					Integer loProcStatusId = 0;
					loProcStatusId = (Integer) DAOUtil.masterDAO(aoMybatisSession,
							asParametersMap.get(HHSConstants.PROCUREMENT_ID),
							HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.FETCH_PROCUREMENT_STATUS_ID,
							HHSConstants.JAVA_LANG_STRING);
					if (loProcStatusId.toString().equalsIgnoreCase(
							PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
									HHSConstants.STATUS_PROCUREMENT_CANCELLED))
							|| loProcStatusId.toString().equalsIgnoreCase(
									PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
											HHSConstants.STATUS_PROCUREMENT_CLOSED)))
					{
						lsStatusId = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.DOCUMENTS_EXPIRED);
					}
					else
					{
						lsStatusId = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.DOCUMENTS_NOT_REQUESTED);
					}
					String lsStatus = (String) DAOUtil.masterDAO(aoMybatisSession, lsStatusId,
							HHSConstants.MAPPER_CLASS_AWARDS_MAPPER, HHSConstants.FETCH_DOCUMENT_STATUS,
							HHSConstants.JAVA_LANG_STRING);
					loAwardDocDetails = new ExtendedDocument();
					loAwardDocDetails.setDocumentTitle(lsFileName);
					loAwardDocDetails.setStatus(lsStatus);
					loAwardDocDetails.setModifiedDate(HHSConstants.BLANK_SYMBOL);
					loAwardDocDetails.setStatusId(lsStatusId);
				}
				setMoState("Successfully fetched awardDocDetails for file:" + lsFileName);
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error occurred in ProcurementDocsZipService:fetchAwardDocDetails method", aoExp);
			setMoState("Error occurred in ProcurementDocsZipService:fetchAwardDocDetails method");
			throw aoExp;
		}
		/**
		 * Any Exception from DAO class will be handles over here. It throws
		 * Application Exception back to Controllers calling method through
		 * Transaction framework
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error occurred in ProcurementDocsZipService:fetchAwardDocDetails method", aoExp);
			setMoState("Error occurred in ProcurementDocsZipService:fetchAwardDocDetails method");
			throw new ApplicationException("Error occurred in ProcurementDocsZipService:fetchAwardDocDetails method",
					aoExp);
		}
		return loAwardDocDetails;
	}

	/**
	 * <p>
	 * This method is used to download the documents on a specified path.
	 * </p>
	 * 
	 * @param aoUserSession
	 * @param aoDBDDocList
	 * @param asProcurementId
	 * @param asProviderOrgID
	 * @param asEvaluationPoolMappingId
	 * @param asContextPath
	 * @param asFolderName
	 * @param lsIsFinacialDocRequired
	 * @return
	 * @throws ApplicationException if ApplicationException occurs
	 */
	public String downloadProcurementDocuments(P8UserSession aoUserSession, List<Map<String, String>> aoDBDDocList,
			String asProcurementId, String asProviderOrgID, String asEvaluationPoolMappingId, String asContextPath,
			String asFolderName) throws ApplicationException
	{
		String lsPath = null;
		String lsParentFolderName = null;
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		try
		{
			loContextDataMap.put(HHSConstants.PROCUREMENT_ID, asProcurementId);
			loContextDataMap.put(HHSConstants.CONTEXT_PATH, asContextPath);
			loContextDataMap.put(HHSConstants.AO_DB_DOC_LIST, aoDBDDocList);
			if (aoDBDDocList != null && aoDBDDocList.size() > HHSConstants.INT_ZERO)
			{

				CountDownLatch loStartSignal = new CountDownLatch(1);
				CountDownLatch loDoneSignal = new CountDownLatch(aoDBDDocList.size());
				lsParentFolderName = asProcurementId + HHSConstants.UNDERSCORE + asEvaluationPoolMappingId
						+ HHSConstants.UNDERSCORE + asProviderOrgID;
				lsPath = asContextPath + File.separator + lsParentFolderName + File.separator + asFolderName;
				File loFolder = new File(lsPath);
				if (loFolder.exists())
				{
					SaveFormOnLocalUtil.deleteFolder(loFolder);
				}
				for (Map<String, String> loDBDDocDetails : aoDBDDocList)
				{
					new Thread(new DownloadDBDDocsThread(loStartSignal, loDoneSignal, aoUserSession, loDBDDocDetails,
							lsPath, true)).start();

				}
				loStartSignal.countDown(); // let all threads proceed
				loDoneSignal.await();
				setMoState("Successfully downloaded procurement documents in the folder name :" + asFolderName);
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (InterruptedException aoExp)
		{
			setMoState("Error while downloading procurement documents :");
			ApplicationException aoAppExp = new ApplicationException(
					"Error occurred in ProcurementDocsZipService:downloadProcurementDocuments() method", aoExp);
			LOG_OBJECT
					.Error("Error occurred in ProcurementDocsZipService:downloadProcurementDocuments() method while downloading procurement documents in the folder name :"
							+ asFolderName, aoExp);
			throw aoAppExp;
		}
		catch (Exception loExp)
		{
			setMoState("Error occurred in ProcurementDocsZipService:downloadProcurementDocuments() method");
			LOG_OBJECT
					.Error("Error occurred in ProcurementDocsZipService:downloadProcurementDocuments() method while downloading procurement documents in the folder name :"
							+ asFolderName, loExp);
			throw new ApplicationException(
					"Error occurred in ProcurementDocsZipService:downloadProcurementDocuments() method", loExp);
		}
		return lsParentFolderName;
	}

	/**
	 * <p>
	 * This method is used to zip the downloaded document folder containing all
	 * the Award, Financial, Proposal and RFP Documents.
	 * </p>
	 * 
	 * @param aoDocumentTitle String value containing Document Title
	 * @param asParentFolderName String value containing Parent Folder Name
	 * @return String object
	 * @throws ApplicationException if ApplicationException occurs
	 */
	public Boolean finalZipProcess(String aoDocumentTitle, String asParentFolderName) throws ApplicationException
	{
		try
		{

			String lsFolderPath = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
					HHSConstants.ZIP_DOCUMENTS_PATH) + File.separator + asParentFolderName;
			String lsPath = lsFolderPath + File.separator + aoDocumentTitle;
			File loFolder = new File(lsPath);
			if (loFolder.exists())
			{
				HHSUtil.zipFolder(lsPath, lsPath + HHSConstants.ZIP);
				SaveFormOnLocalUtil.deleteFolder(loFolder);
			}
			setMoState("Successfully zipped the folder :" + loFolder);

		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error(
					"ApplicationException occured in ProcurementDocsZipService:finalZipProcess() method while Zipping the folder :"
							+ aoDocumentTitle, aoAppEx);
			setMoState("ApplicationException Exception occured while Zipping Procurement Documents for the Document Name :"
					+ aoDocumentTitle);
			throw aoAppEx;
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (Exception loExp)
		{
			LOG_OBJECT.Error(
					"Exception occured in ProcurementDocsZipService:finalZipProcess() method while Zipping the folder :"
							+ aoDocumentTitle, loExp);
			setMoState("Exception occured while Zipping Procurement Documentsfor the Document Name :" + aoDocumentTitle);
			throw new ApplicationException("Exception occured in ProcurementDocsZipService : finalZipProcess method",
					loExp);
		}
		return true;
	}

	/**
	 * This method is used to fetch document status.
	 * 
	 * @param aoMybatisSession SqlSession object
	 * @param asDocStatus String containing document status Id
	 * @return lsStatus String containing document status
	 * @throws ApplicationException
	 */
	public String fetchDocumentStatus(SqlSession aoMybatisSession, String asDocStatus) throws ApplicationException
	{
		String lsStatus = null;
		try
		{

			lsStatus = (String) DAOUtil.masterDAO(aoMybatisSession, asDocStatus,
					HHSConstants.MAPPER_CLASS_AWARDS_MAPPER, HHSConstants.FETCH_DOCUMENT_STATUS,
					HHSConstants.JAVA_LANG_STRING);
			setMoState("Successfully fetched document status :" + lsStatus);

		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error(
					"ApplicationException occured in ProcurementDocsZipService:fetchDocumentStatus() method while fetching status :"
							+ lsStatus, aoAppEx);
			setMoState("ApplicationException Exception occured while fetching status :" + lsStatus);
			throw aoAppEx;
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (Exception loExp)
		{
			LOG_OBJECT.Error(
					"Exception occured in ProcurementDocsZipService:fetchDocumentStatus() method while fetching status :"
							+ lsStatus, loExp);
			setMoState("Exception occured while fetching status :" + lsStatus);
			throw new ApplicationException(
					"Exception occured in ProcurementDocsZipService : fetchDocumentStatus() method", loExp);
		}
		return lsStatus;
	}

}
