package com.nyc.hhs.daomanager.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.ibatis.session.SqlSession;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.BaseFont;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSP8Constants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.AccountsAllocationBean;
import com.nyc.hhs.model.FundingAllocationBean;
import com.nyc.hhs.model.Procurement;
import com.nyc.hhs.model.ProcurementCOF;
import com.nyc.hhs.model.PsrBean;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8services.P8ContentService;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.PsrPdfGenerationUtil;

/**
 * This class is added for release 5 This service is used for calling methods
 * part of Procurement PSR module. This service class will get the method calls
 * from controller through transaction layer. Execute queries by calling mapper
 * and return query output back to controller. If any error exists, wrap the
 * exception into Application Exception and throw it to controller.
 */
/**
 * @author manav.rajora
 *
 */
public class PsrService extends ServiceState
{

	/**
	 * This is a log object used to log any exception into log file.
	 */
	public static final LogInfo LOG_OBJECT = new LogInfo(PsrService.class);

	/**
	 * This method generates PSR PDF file.This method will create pdf for a
	 * particular procurementId and return the output path, which is used by
	 * Filenet to upload the document.
	 * @param aoProcurementID - ProcurementID
	 * @param aoPsrBean - PsrBean Object
	 * @param aoServiceList - Service element List for procurement
	 * @param aoPCOFBean - PCOF details for procurement
	 * @param aoReturnedGridList - Financial grid details for procurement
	 * @param aoFinanceGridList - Finance grid list
	 * @param aoShowFinanceGridFlag - boolean finance grid flag
	 * @param aoFinanceGridList- Finance grid list
	 * @param aoShowFinanceGridFlag- boolean finance grid flag
	 * @return aoOutputFilePathList - Output path File for Filenet Upload
	 * @throws ApplicationException - when any exception occurs wrap it into
	 *             application exception.
	 */
	public List generatePsrPdf(String aoProcurementID, PsrBean aoPsrBean, List aoServiceList,
			ProcurementCOF aoPCOFBean, List aoReturnedGridList, List aoFinanceGridList, Boolean aoShowFinanceGridFlag)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered PsrService.generatePsrPdf()");
		NumberFormat aoFormatter = NumberFormat.getCurrencyInstance(Locale.US);
		List<String> loOutputFilePathList = new ArrayList<String>();
		try
		{
			Document aoDocument = new Document(PageSize.A4, 36, 36, 36, 40);
			PsrPdfGenerationUtil loPsrGenerationUtil = new PsrPdfGenerationUtil();
			FileOutputStream loFileOutputStream = null;
			String lsVerdanaFontPath = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
					HHSR5Constants.VERDANA_BOLD_FONT_PATH);
			BaseFont loBaseFont = BaseFont.createFont(loPsrGenerationUtil.getJarPath() + lsVerdanaFontPath,
					BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
			Font aoDocFont = new Font(loBaseFont, 13, Font.NORMAL, new BaseColor(0, 123, 164));
			String lsPDFFilePath = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
					HHSConstants.PDF_FILE_PATH);
			String asOutputPath = lsPDFFilePath + aoProcurementID + HHSR5Constants.PDF_FILE_EXTENSION;
			loFileOutputStream = loPsrGenerationUtil.subGeneratePsrPdf(aoPsrBean, aoServiceList, aoPCOFBean,
					aoReturnedGridList, aoFinanceGridList, aoFormatter, aoDocument, aoDocFont, asOutputPath,
					aoShowFinanceGridFlag);
			aoDocument.close();
			loFileOutputStream.close();
			loOutputFilePathList.add(asOutputPath);
			LOG_OBJECT.Info("Exited PsrService.generatePsrPdf()");
			return loOutputFilePathList;

		}
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("Error while Generating PDF File", loExp);
			throw loExp;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured  at Generating PDF File", loAppEx);
			throw new ApplicationException("Exception occured  at Generating PDF File", loAppEx);
		}
	}

	/**
	 * This method uploads document to FileNet and return the List of uploaded
	 * files.
	 * <ul>
	 * <li>Steps of execution are -
	 * <ul>
	 * <li>On the basis of aoUserSession, aoOutputFilePathList AND PsrBean
	 * Object would be display.</li>
	 * </ul>
	 * 
	 * @param aoUserSession - P8UserSession Object
	 * @param aoOutputFilePathList - List<String> Object
	 * @param aoPDFBatch - PsrBean Object
	 * @return loListOfDocId - an object of List<String>
	 * @throws ApplicationException - when any exception occurs wrap it into
	 *             application exception.
	 */
	public List<String> uploadPsrDocumentToFilenet(P8UserSession aoUserSession, List<String> aoOutputFilePathList,
			PsrBean aoPDFBatch) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered PsrService.uploadPsrDocumentToFilenet()");
		String lsDocId = null;
		String lsStartTime = CommonUtil.getCurrentTimeInMilliSec();
		HashMap loHmReqExceProp = new HashMap();
		HashMap<String, Object> loParamMap = new HashMap<String, Object>();
		String lsFileName = null;
		FileInputStream loFileInputStreamObj = null;
		String lsFullFolderPath = null;
		List<String> loListOfDocId = new ArrayList<String>();
		try
		{
			lsFullFolderPath = PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
					P8Constants.PREDEFINED_FOLDER_PATH_FINANCIAL_DOC)
					+ HHSConstants.FORWARD_SLASH
					+ aoPDFBatch.getProcurementId() + HHSConstants.UNDERSCORE + aoPDFBatch.getPsrDetailId();
			for (Iterator<String> loOutputFilePathItr = aoOutputFilePathList.iterator(); loOutputFilePathItr.hasNext();)
			{
				loParamMap.clear();
				String lsOuputFilePath = (String) loOutputFilePathItr.next();
				lsFileName = lsOuputFilePath.substring(lsOuputFilePath.lastIndexOf(HHSConstants.FORWARD_SLASH) + 1,
						lsOuputFilePath.length());
				loFileInputStreamObj = new FileInputStream(new File(lsOuputFilePath));
				loParamMap.put(P8Constants.PROPERTY_CE_MIME_TYPE, P8Constants.APPLICATION_PDF);
				loParamMap.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, HHSR5Constants.PSR_PDF + HHSConstants.UNDERSCORE
						+ aoPDFBatch.getProcurementId());
				loParamMap.put(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY, HHSConstants.SYSTEM_USER);
				loParamMap.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY, HHSConstants.SYSTEM_USER);
				loParamMap.put(P8Constants.PROPERTY_CE_PSR_ID, aoPDFBatch.getPsrDetailId());
				loParamMap.put(P8Constants.PROPERTY_CE_PROCUREMENT_ID, aoPDFBatch.getProcurementId());
				loParamMap.put(P8Constants.PROPERTY_CE_DOCUMENT_TYPE, lsFileName);
				loParamMap.put(P8Constants.PROPERTY_CE_FILE_TYPE, HHSConstants.PDF_FILE_TYPE);
				// Adding for Emergency Release 4.0.2 for PDf-creation Batch
				loParamMap.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, false);
				lsDocId = new P8ContentService().financialPDFDocumentCreation(aoUserSession, loFileInputStreamObj,
						loParamMap, lsFullFolderPath);
				loFileInputStreamObj.close();
				loListOfDocId.add(lsDocId);
			}
		}
		catch (ApplicationException aoEx)
		{
			String lsMessage = aoEx.getMessage();
			if (lsMessage.startsWith("Runtime Error in Fetching Filenet"))
			{
				lsMessage = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE, HHSConstants.FILE_UPLOAD_FAIL_MESSAGE);
				throw new ApplicationException(lsMessage, aoEx);
			}
			aoEx.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.createDVdocument()::", aoEx);
			throw aoEx;
		}
		catch (Exception aoEx)
		{
			setMoState("Error While creating document");
			ApplicationException loAppex = new ApplicationException(PropertyLoader.getProperty(
					P8Constants.ERROR_PROPERTY_FILE, HHSConstants.FILE_UPLOAD_FAIL_MESSAGE), aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error While creating document", aoEx);
			throw loAppex;
		}
		finally
		{
			if (null != loFileInputStreamObj)
			{
				try
				{
					loFileInputStreamObj.close();
				}
				catch (IOException aoIoExp)
				{
					throw new ApplicationException("Error Occured While Closing Input Stream::", aoIoExp);
				}
			}
		}
		String lsEndTime = CommonUtil.getCurrentTimeInMilliSec();
		float liTimediff = CommonUtil.timeDiff(CommonUtil.getItemDateInMIlisec(lsStartTime),
				CommonUtil.getItemDateInMIlisec(lsEndTime));
		LOG_OBJECT.Info("P8ContentService: document uploaded. uploadPsrDocumentToFilenet. Time Taken(seconds):: "
				+ liTimediff);
		LOG_OBJECT.Info("Exiting PsrService.uploadPsrDocumentToFilenet()");
		return loListOfDocId;
	}

	/**
	 * This method is for Release 5 to fetch details for chart of account grid
	 * in procurement financials for PSR Workflow
	 * 
	 * @param aoMybatisSession - SQLSession
	 * @param aoProcurementId - ProcurmentId
	 * @return - List of AccountsAllocationBean
	 * @throws ApplicationException- when any exception occurs wrap it into
	 *             application exception.
	 */
	@SuppressWarnings("unchecked")
	public List<AccountsAllocationBean> fetchPCOFFinanceDetails(SqlSession aoMybatisSession, String aoProcurementId)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered PsrService.fetchPCOFFinanceDetails()");
		List<AccountsAllocationBean> loAccountsAllocationBeanList = null;
		try
		{
			loAccountsAllocationBeanList = (List<AccountsAllocationBean>) DAOUtil.masterDAO(aoMybatisSession,
					aoProcurementId, HHSR5Constants.MAPPER_CLASS_PROCUREMENT_MAPPER,
					HHSR5Constants.FETCH_PSR_FINANCE_DETAILS, HHSR5Constants.JAVA_LANG_STRING);

			setMoState("Account Allocation Details for PSR fetched successfully:");
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured in ProcurementService: fetchPCOFFinanceDetails method:: ", loAppEx);
			setMoState("Transaction Failed:: ProcurementService: fetchPCOFFinanceDetails method :");

			throw loAppEx;
		}
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured in ProcurementService: fetchPCOFFinanceDetails method::", loEx);
			LOG_OBJECT.Error("Exception occured in ConfigurationService: fetchPCOFCoADetails method:: ", loAppEx);
			setMoState("Transaction Failed:: ProcurementService: fetchPCOFFinanceDetails method :");
			throw loAppEx;
		}
		LOG_OBJECT.Info("Exited PsrService.fetchPCOFFinanceDetails()");
		return loAccountsAllocationBeanList;
	}

	/**
	 * This method returns the list of procurmentIds where PDF Flag is one in
	 * PSR details table database.
	 * 
	 * @param aoMybatisSession - SQL Session
	 * @return fetchPdfFlagList - List of procurement Ids
	 * @throws ApplicationException - when any exception occurs wrap it into
	 *             application exception.
	 */
	public List<String> fetchPdfFlagList(SqlSession aoMybatisSession) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered PsrService.fetchPdfFlagList()");
		List<String> loProcurementId = null;
		try
		{
			loProcurementId = (List<String>) DAOUtil.masterDAO(aoMybatisSession, HHSR5Constants.EMPTY_STRING,
					HHSR5Constants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSR5Constants.FETCH_PDF_FLAG_LIST,
					HHSR5Constants.JAVA_LANG_STRING);

		}

		// catch any application exception thrown from the code and throw it
		// forward
		catch (ApplicationException loExp)
		{
			setMoState("Error occurred while fetchPdfFlagList");
			LOG_OBJECT.Error("Error occurred fetchPdfFlagList", loExp);
			throw loExp;
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception loAppEx)
		{
			setMoState("Error occurred while fetchPdfFlagList");
			LOG_OBJECT.Error("Error occurred while fetchPdfFlagList", loAppEx);
			throw new ApplicationException("Error occurred while fetchPdfFlagList", loAppEx);
		}
		LOG_OBJECT.Info("Exited PsrService.fetchPdfFlagList()");
		return loProcurementId;
	}

	/**
	 * This method inserts the PDF generation Flag data in PSR table in the
	 * database.
	 * 
	 * <ul>
	 * <li>Update Psr Details using query <b>updatePsrPdfStatusFlag</b></li>
	 * </ul>
	 * 
	 * Return insert status.
	 * 
	 * @param aoMybatisSession - SQL Session
	 * @param asProcurementId - Procurement id string
	 * @param asPdfFlag - Pdf flag list
	 * @param finishTaskStatus - Filenet Return Workflow Status
	 * @param statusId - Updated StatusId
	 * @return InsertStatus - Return Status
	 * @throws ApplicationException - when any exception occurs wrap it into
	 *             application exception.
	 */
	public Boolean updatePsrPdfStatusFlag(SqlSession aoMybatisSession, String asProcurementId, String asPdfFlag)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered PsrService.updatePsrPdfStatusFlag()");
		Boolean loSuccessStatus = Boolean.FALSE;
		HashMap<String, String> loContextDataMap = new HashMap<String, String>();
		loContextDataMap.put(ApplicationConstants.PROCUREMENT_ID, asProcurementId);
		loContextDataMap.put(HHSR5Constants.PDF_FLAG, asPdfFlag);
		try
		{
			DAOUtil.masterDAO(aoMybatisSession, loContextDataMap, HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
					HHSR5Constants.UPDATE_PSR_PDF_FLAG, ApplicationConstants.JAVA_UTIL_HASHMAP);

			loSuccessStatus = Boolean.TRUE;
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Error while updating Psr Pdf Flag detail :", loAppEx);
			setMoState("Error while saving PSR details");
			throw loAppEx;
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Error while updating Psr Pdf Flag detail :", loAppEx);
			setMoState("Error while updating Psr statusId details :");
			throw new ApplicationException("Error occured while updating Psr Pdf Flag detail", loAppEx);
		}
		LOG_OBJECT.Info("Exited PsrService.updatePsrPdfStatusFlag()");
		return loSuccessStatus;
	}

	/**
	 * This method inserts the Updated StatusId in PSR data in the database.
	 * 
	 * <ul>
	 * <li>Update Psr Details using query <b>updatePsrStatusFlag</b></li>
	 * </ul>
	 * 
	 * Return insert status.
	 * 
	 * @param aoMybatisSession - SQL Session
	 * @param asProcurementId Procurement id string
	 * @param asStatusId - Status id string
	 * @param aoTaskDetailsBean - TaskDetailsBean object
	 * @param finishTaskStatus - Filenet Return Workflow Status
	 * @param statusId - Updated StatusId
	 * @return InsertStatus - Return Status
	 * @throws ApplicationException - when any exception occurs wrap it into
	 *             application exception.
	 */
	public Boolean updatePsrStatusFlag(SqlSession aoMybatisSession, String asProcurementId, String asStatusId,
			TaskDetailsBean aoTaskDetailsBean) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered PsrService.updatePsrStatusFlag()");
		Boolean loSuccessStatus = Boolean.FALSE;
		String lsUserId = aoTaskDetailsBean.getUserId();
		HashMap<String, String> loContextDataMap = new HashMap<String, String>();
		loContextDataMap.put(ApplicationConstants.PROCUREMENT_ID, asProcurementId);
		loContextDataMap.put(HHSConstants.STATUS_ID, asStatusId);
		loContextDataMap.put(HHSConstants.USER_ID, lsUserId);
		try
		{
			if (aoTaskDetailsBean.getTaskName().equalsIgnoreCase(HHSR5Constants.TASK_APPROVE_PSR))
			{

				DAOUtil.masterDAO(aoMybatisSession, loContextDataMap, HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
						HHSR5Constants.UPDATE_PSR_STATUS_ID, ApplicationConstants.JAVA_UTIL_HASHMAP);

				loSuccessStatus = Boolean.TRUE;
			}
			else if (aoTaskDetailsBean.getTaskName().equalsIgnoreCase(HHSR5Constants.TASK_COMPLETE_PSR))
			{

				DAOUtil.masterDAO(aoMybatisSession, loContextDataMap, HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
						HHSR5Constants.UPDATE_PSR_STATUS_BY_AGENCY, ApplicationConstants.JAVA_UTIL_HASHMAP);

				loSuccessStatus = Boolean.TRUE;
			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Error while updating Psr statusId detail :", loAppEx);
			setMoState("Error while saving PSR details");
			throw loAppEx;
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Error while updating Psr statusId detail :", loAppEx);
			setMoState("Error while updating Psr statusId details :");
			throw new ApplicationException("Error occured while updating Psr statusId detail", loAppEx);
		}
		LOG_OBJECT.Info("Exited PsrService.updatePsrStatusFlag()");
		return loSuccessStatus;

	}

	/**
	 * This method is part of Release 5 PSR module
	 * 
	 * <ul>
	 * This method fetch the ProcurmentCOF details for PSR non-open ended
	 * Procurements
	 * </ul>
	 * @param aoMybatisSession SqlSession object
	 * @param aoPsrBean PsrBean object
	 * @return ProcurementCOF object
	 * @throws ApplicationException - when any exception occurs wrap it into
	 *             application exception.
	 */
	public ProcurementCOF getPcofPsrdetails(SqlSession aoMybatisSession, PsrBean aoPsrBean) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered PsrService.getPcofPsrdetails()");
		ProcurementCOF loProcurementPcofBean = null;
		if (aoPsrBean.getIsOpenEndedRFP().equals(HHSConstants.ZERO)
				&& aoPsrBean.getEstProcurementValue().compareTo(java.math.BigDecimal.ZERO) > 0)
		{
			String lsProcurementId = aoPsrBean.getProcurementId();
			try
			{
				loProcurementPcofBean = (ProcurementCOF) DAOUtil.masterDAO(aoMybatisSession, lsProcurementId,
						HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSR5Constants.FETCH_PSR_PCOF_DETAILS,
						HHSConstants.JAVA_LANG_STRING);

				setMoState("Procurement Element List fetched successfully for Procurement Id:" + lsProcurementId);
			}

			// Any Exception from DAO class will be thrown as Application
			// Exception
			// which will be handles over here. It throws Application Exception
			// back
			// to Controllers calling method through Transaction framework
			catch (ApplicationException loExp)
			{
				LOG_OBJECT.Error("Error while getting Procurement Details", loExp);
				setMoState("Error while getting Procurement Details");
				throw loExp;
			}
			// handling exception other than Application Exception.
			catch (Exception loEx)
			{
				LOG_OBJECT.Error("Error while getting Procurement Details", loEx);
				setMoState("Error while getting Procurement Details");
				throw new ApplicationException("Error while getting Procurement Details", loEx);
			}
		}
		LOG_OBJECT.Info("Exited PsrService.getPcofPsrdetails()");
		return loProcurementPcofBean;
	}

	/**
	 * This method gets the Element Services for a procurement Id
	 * 
	 * <ul>
	 * <li>1. Retrieve procurement Id</li>
	 * <li>2. Set procurement id in a map for context data to be logged in case
	 * of exception.</li>
	 * <li>3. Retrieve Procurement Element Services
	 * <b>getProcurementServicesList</b> to fetch the required Element Lidt</li>
	 * <li>4. Return the Services List.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - SqlSession
	 * @param aoTaskDetailBean - TaskDetailsBean object
	 * @param asProcurementId - Procurement Id
	 * @return Services List 
	 * @throws ApplicationException - when any exception occurs wrap it into
	 *             application exception.
	 */
	public List getPsrServicesList(SqlSession aoMybatisSession, TaskDetailsBean aoTaskDetailBean)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered PsrService.getPsrServicesList()");
		List<Procurement> loServiceList = null;
		String lsProcurementId = aoTaskDetailBean.getEntityId();
		try
		{
			loServiceList = (List<Procurement>) DAOUtil.masterDAO(aoMybatisSession, lsProcurementId,
					HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSR5Constants.GET_PSR_SERVICES_LIST,
					HHSConstants.JAVA_LANG_STRING);

			setMoState("Procurement Element List fetched successfully for Procurement Id:" + lsProcurementId);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("Error while getting Procurement Details", loExp);
			setMoState("Error while getting Procurement Details");
			throw loExp;
		}
		// handling exception other than Application Exception.
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Error while getting Procurement Details", loEx);
			setMoState("Error while getting Procurement Details");
			throw new ApplicationException("Error while getting Procurement Details", loEx);
		}
		LOG_OBJECT.Info("Exited PsrService.getPsrServicesList()");
		return loServiceList;
	}

	/**
	 * This method gets the PSR Summary corresponding to a procurement Id
	 * 
	 * <ul>
	 * <li>1. Retrieve procurement Id</li>
	 * <li>2. Set procurement id in a map for context data to be logged in case
	 * of exception.</li>
	 * <li>3. If retrieved PSR summary is null then execute query
	 * <b>getProcurementSummary</b> to fetch the required procurement summary</li>
	 * <li>5. Return the PSR Summary.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - SqlSession
	 * @param aoTaskDetailMap - Task detail hashmap
	 * @param aoTaskDetailBean - TaskDetailsBean object
	 * @return PsrBean object
	 * @throws ApplicationException - when any exception occurs wrap it into
	 *             application exception.
	 */
	public PsrBean getPsrSummary(SqlSession aoMybatisSession, HashMap aoTaskDetailMap, TaskDetailsBean aoTaskDetailBean)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered PsrService.getPsrSummary()");
		PsrBean loPsrBean = null;
		HashMap<String, String> loContextDataMap = new HashMap<String, String>();
		HashMap loTempMap = (HashMap) aoTaskDetailMap.get(aoTaskDetailBean.getWorkFlowId());
		String lsProcurementId = (String) loTempMap.get(P8Constants.PE_WORKFLOW_PROCUREMENT_ID);
		aoTaskDetailBean.setEntityId(lsProcurementId);
		try
		{
			loPsrBean = (PsrBean) DAOUtil.masterDAO(aoMybatisSession, lsProcurementId,
					HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSR5Constants.GET_PSR_SUMMARY,
					HHSConstants.JAVA_LANG_STRING);

			setMoState("Procurement details fetched successfully for Procurement Id:" + lsProcurementId);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException loExp)
		{
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while getting Procurement Details", loExp);
			setMoState("Error while getting Procurement Details");
			throw loExp;
		}
		// handling exception other than Application Exception.
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Error while getting Procurement Details", loEx);
			setMoState("Error while getting Procurement Details");
			throw new ApplicationException("Error while getting Procurement Details", loEx);
		}
		LOG_OBJECT.Info("Exited PsrService.getPsrSummary()");
		return loPsrBean;
	}

	/**
	 * This method inserts the PSR data in the database.
	 * 
	 * <ul>
	 * <li>1.Update Psr Details.</li>
	 * <li>2.If update row count is less than 1 using query
	 * <b>updatePsrDetails</b> .</li>
	 * <l1>3.Insert new Psr Details.</li>
	 * </ul>
	 * 
	 * Return insert status.
	 * 
	 * @param aoMybatisSession - SqlSession
	 * @param aoPsrBean - PsrBean object
	 * @return boolean value loSuccessStatus
	 * @throws ApplicationException - when any exception occurs wrap it into
	 *             application exception.
	 */
	public Boolean savePsrDetails(SqlSession aoMybatisSession, PsrBean aoPsrBean) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered PsrService.savePsrDetails()");
		Boolean loSuccessStatus = Boolean.FALSE;
		int liUpdateCount = HHSConstants.INT_ZERO;
		try
		{
			liUpdateCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoPsrBean,
					HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSR5Constants.UPDATE_PSR_DETAILS,
					HHSR5Constants.COM_NYC_HHS_MODEL_PSR);

			if (liUpdateCount < HHSConstants.INT_ONE)
			{
				DAOUtil.masterDAO(aoMybatisSession, aoPsrBean, HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
						HHSR5Constants.INS_NEW_PSR_DETAILS, HHSR5Constants.COM_NYC_HHS_MODEL_PSR);
			}
			loSuccessStatus = Boolean.TRUE;
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Error while saving Psr details :", loAppEx);
			setMoState("Error while saving PSR details");
			throw loAppEx;
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Error while saving Psr details :", loAppEx);
			setMoState("Error while saving PSR details");
			throw new ApplicationException("Error occured while saving PSR details", loAppEx);
		}
		LOG_OBJECT.Info("Exited PsrService.savePsrDetails()");
		return loSuccessStatus;
	}

	/**
	 * This method checks for Audit Insert for Agency in Complete PSR workflow.
	 * <ul>
	 * <li>Select AgencyAudit details using query <b>checkAuditInsert</b></li>
	 * </ul>
	 * 
	 * Return insert status.
	 * 
	 * @param aoMybatisSession - Sql session object
	 * @param asProcurementId - Procurement id string
	 * @return loSuccessStatus Boolean value of success status
	 * @throws ApplicationException - when any exception occurs wrap it into
	 *             application exception.
	 */
	public Boolean checkAuditInsert(SqlSession aoMybatisSession, String asProcurementId) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered PsrService.checkAuditInsert()");
		Boolean loSuccessStatus = Boolean.TRUE;
		int liSelectCount = HHSConstants.INT_ZERO;
		HashMap loMap = new HashMap();
		loMap.put(HHSConstants.ENTITY_ID, asProcurementId);
		try
		{
			liSelectCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loMap,
					HHSConstants.MAPPER_CLASS_TASK_SERVICE_MAPPER, HHSR5Constants.CHECK_AUDIT_INSERT,
					HHSConstants.JAVA_UTIL_HASH_MAP);

			if (liSelectCount > HHSConstants.INT_ZERO)
			{
				loSuccessStatus = Boolean.FALSE;
			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Error while fetching checkAuditInsert details :", loAppEx);
			setMoState("Error while fetching checkAuditInsert details");
			throw loAppEx;
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Error while fetching checkAuditInsert details :", loAppEx);
			setMoState("Error while fetching checkAuditInsert details");
			throw new ApplicationException("Error while fetching checkAuditInsert details", loAppEx);
		}
		LOG_OBJECT.Info("Exited PsrService.checkAuditInsert()");
		return loSuccessStatus;
	}

	/**
	 * This method inputs flag for Audit Insert for Agency in Complete PSR
	 * workflow. This method returns flag for Audit Insert in Approve PSR
	 * Returned workflow.
	 * 
	 * Return insert status.
	 * 
	 * @param aoTaskCompleted - Boolean Flag for new Audit Insert
	 * @return aoTaskCompleted Boolean flag for Audit Insert in Approve PSR returned workflow
	 * @throws ApplicationException - when any exception occurs wrap it into
	 *             application exception.
	 */
	public Boolean checkAuditInsertOnReturn(Boolean aoTaskCompleted)
	{
		LOG_OBJECT.Info("Entered PsrService.checkAuditInsertOnReturn()");
		LOG_OBJECT.Info("Exited PsrService.checkAuditInsertOnReturn()");
		return !aoTaskCompleted;
	}

	/**
	 * This method feches the FundingSourceDetails details for a procurement
	 * <ul>
	 * <li>The fields values returned from this method are -
	 * <ul>
	 * <li>Federal Amount</li>
	 * <li>City Amount</li>
	 * <li>State Amount</li>
	 * <li>Other Amount</li>
	 * 
	 * These amounts are returned for all the fiscal years of contract duration.
	 * </ul>
	 * </li>
	 * </ul>
	 * <ul>
	 * <li>Steps of execution are -
	 * <ul>
	 * <li>On the basis of procurementId, the above mentioned values are
	 * received from the DataBase by executing the
	 * <code>fetchPsrConfFundingDetails</code> query in the ProcurementMapper</li>
	 * <li>It returns the values as List of FundingAllocationBean object</li>
	 * <li>List is iterated to check if it contains the data for all fiscal
	 * years.</li>
	 * <li>If there is no data for any fiscal year, then
	 * addContractConfFundingDetails query will be executed to insert a row in
	 * table with all values as 0.</li> so that it can be used by JQGrid to
	 * display the required information.</li>
	 * </ul>
	 * </li> </ul>
	 * 
	 * @param aoMybatisSession SqlSession object
	 * @param asProcurmentId Procurement Id string
	 * @param aoGridBean input bean on basis of which funding details will be
	 *            fetched
	 * @return List<FundingAllocationBean> list of type FundingAllocationBean
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	@SuppressWarnings("unchecked")
	public List<FundingAllocationBean> fetchPsrConfFundingDetails(SqlSession aoMybatisSession, String asProcurmentId)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered PsrService.fetchPsrConfFundingDetails()");
		List<FundingAllocationBean> loFundingSourceDetails = null;
		try
		{
			loFundingSourceDetails = (List<FundingAllocationBean>) DAOUtil.masterDAO(aoMybatisSession, asProcurmentId,
					HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSR5Constants.FETCH_PSR_FUND_DETAILS,
					HHSConstants.JAVA_LANG_STRING);

			setMoState("Funding Source Allocation Details for procurement fetched successfully:");
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error in PSR Service while getting procurement configuration funding source details :");
			throw loExp;
		}
		catch (Exception aoAppExp)
		{
			setMoState("Error in PSR Service while procurement configuration funding source details");
			LOG_OBJECT.Error("Error in PSR Service while getting procurement configuration funding source details",
					aoAppExp);
			throw new ApplicationException(
					"Error in PSR Service while getting procurement configuration funding source details", aoAppExp);
		}
		LOG_OBJECT.Info("Exited PsrService.fetchPsrConfFundingDetails()");
		return loFundingSourceDetails;
	}

	/**
	 * This method checks for display grid field in PSR workflow.
	 * <ul>
	 * <li>Select count value using query <b>showPsrFundingSubGrid</b></li>
	 * </ul>
	 * 
	 * Return display status.
	 * 
	 * @param aoMybatisSession Sql session object
	 * @param aoPsrBean PsrBean object
	 * @param aoProcurementCOF ProcurementCOF object
	 * @return loDisplayStatus Boolean value of display status
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	public Boolean showPsrFundingGrid(SqlSession aoMybatisSession, PsrBean aoPsrBean, ProcurementCOF aoProcurementCOF)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered PsrService.showPsrFundingGrid()");
		Boolean loDisplayStatus = Boolean.FALSE;
		int liSelectCount = HHSConstants.INT_ZERO;
		if (null != aoProcurementCOF)
		{
			String lsProcurementId = aoPsrBean.getProcurementId();
			try
			{
				liSelectCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, lsProcurementId,
						HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSR5Constants.SHOW_PSR_FUNDING_SUBGRID,
						HHSConstants.JAVA_LANG_STRING);

				if (liSelectCount > HHSConstants.INT_ZERO)
				{
					loDisplayStatus = Boolean.TRUE;
				}
			}
			catch (ApplicationException loAppEx)
			{
				LOG_OBJECT.Error("Error while fetching showPsrFundingGrid details :", loAppEx);
				setMoState("Error while fetching showPsrFundingGrid details");
				throw loAppEx;
			}
			// Catch any exception thrown from the code and wrap it into
			// application
			// Exception and throw it forward
			catch (Exception loAppEx)
			{
				LOG_OBJECT.Error("Error while fetching showPsrFundingGrid details :", loAppEx);
				setMoState("Error while fetching showPsrFundingGrid details");
				throw new ApplicationException("Error while fetching showPsrFundingGrid details", loAppEx);
			}
		}
		LOG_OBJECT.Info("Exited PsrService.showPsrFundingGrid()");
		return loDisplayStatus;
	}
	
	/** This method is added for Defect 7970. It puts the
	 *  the updated procurement title into Required props for 
	 *  procurement status panned.
	 *  
	 * @param asProcurementStatus - String ProcurementStatus
	 * @param aoHmRequiredMap - Hash RequiredProps
	 * @return task update details
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	public HashMap getTaskUpdateDetails(String asProcurementStatus, HashMap aoHmRequiredMap) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered PsrService.getTaskUpdateDetails()");
		HashMap loWorkflowMap = null;
		if (asProcurementStatus.equalsIgnoreCase(HHSConstants.TWO))
		{		
			loWorkflowMap = new HashMap();
			HashMap loUdatedMap = (HashMap) aoHmRequiredMap.get(HHSP8Constants.REQUEST_MAP_PARAMETER_NAME);
			String lsProcurementTitle = (String) loUdatedMap.get(HHSConstants.PROC_TITLE);
			if (null != lsProcurementTitle)
			{
				loWorkflowMap.put(HHSConstants.PROPERTY_PE_PROCUREMENT_TITLE, lsProcurementTitle);
			}
		}
		LOG_OBJECT.Info("Exited PsrService.getTaskUpdateDetails()");
		return loWorkflowMap;
	}

}
