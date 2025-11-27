package com.nyc.hhs.component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.ibatis.session.SqlSession;

import com.filenet.api.collection.ContentElementList;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.AutoUniqueName;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.ClassNames;
import com.filenet.api.constants.DefineSecurityParentage;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.filenet.api.exception.EngineRuntimeException;
//import com.filenet.wcm.toolkit.util.WcmException;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.daomanager.service.ServiceState;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.cache.ICacheManager;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.TaxonomyTree;
import com.nyc.hhs.model.WorkflowIDServiceBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8SecurityOperations;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.BusinessApplicationUtil;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.TaxonomyDOMUtil;
import com.nyc.hhs.util.XMLUtil;

/**
 * This class is used to perform all P8 process component operations on FileNet
 * for uploading printable version in the FileNet repository. This will be
 * executed through work-flow component step. The main method being called is
 * uploadPrintVersionInDV(), which is invoked through a component step in the BR
 * and SC workflows
 * 
 */

public class HHSComponentService extends ServiceState
{

	private static final LogInfo LOG_OBJECT = new LogInfo(HHSComponentService.class);
	public static Domain moDomain = null;

	// Cashing parameters
	public static final String BATCH_PROVIDER_DUE_DATE_CHECK = "provider_due_date_check";
	public static final String BATCH_INSERT_ALERTS = "insert_alerts";
	public static final String BATCH_NOTIFICATIONS = "notifications";
	public static final String BATCH_APPLICATION_EXPIRATION = "application_expiration";

	public static final String PROVIDER_NAME = "provider_name";

	public static final String PROPERTY_FILE = "com.nyc.hhs.batch.batchConfig";
	public static final String TRANSACTION_CONFIG_FILE = "com.nyc.hhs.config.TransactionConfig";
	public static final String TRANSACTION_ELEMENT = "transaction";

	// static block for loading P8 config files
	static
	{
		try
		{
			System.setProperty(P8Constants.PROP_FILE_JAVA_SECURITY_AUTH_LOGIN_CONFIG, PropertyLoader.getProperty(
					P8Constants.PROPERTY_FILE, P8Constants.PROP_FILE_JAVA_SECURITY_AUTH_LOGIN_CONFIG));

		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error while setting system property", aoEx);
		}
	} // end static

	/**
	 * 
	 * @param aoMybatisSession
	 * @param aoHashMap
	 * @return
	 * @throws Exception
	 */
	/**
	 * This method is being called from the PrintView batch
	 */
	public boolean uploadPrintVersionInDVForBatch(SqlSession aoMybatisSession, HashMap aoHashMap) throws Exception
	{

		boolean lbDocCreated = false;

		HashMap loHMap = (HashMap) aoHashMap.get("loHMap");

		LOG_OBJECT.Debug("***************Inside  uploadPrintVersionInDVForBatch ************************");

		String lsPrintViewId = (String) loHMap.get("lsPrintViewId");
		String lsProviderId = (String) loHMap.get("lsProviderId");
		String lsTaskType = (String) loHMap.get("lsTaskType");

		lbDocCreated = uploadPrintVersionInDV(lsPrintViewId, lsProviderId, lsTaskType);

		return lbDocCreated;

	}

	/**
	 * This method is used for creating a printable version of a document and
	 * uploading it into FileNet. and is called directly from the workflow
	 * component step
	 * 
	 * @param lsPrintViewId is the print view id attribute which is to be set
	 *            for the document
	 * @param aoProviderId is the provider Id attribute for the document
	 * @param lsTaskType specifies the task type i.e. BR or Service application
	 * @return boolean to indicate whether the document has been successfully
	 *         uploaded
	 * @throws Exception if any exception is thrown
	 */
	public boolean uploadPrintVersionInDV(String asPrintViewId, String asProviderId, String asTaskType)
			throws Exception
	{
		boolean lbDocumentCreated = false;

		try
		{

			LOG_OBJECT.Debug("***************Inside  uploadPrintVersionInDV************************");
			LOG_OBJECT.Debug("The Pass values to the function are:>>asPrintViewId>>:" + asPrintViewId
					+ "<<asProviderId>>:" + asProviderId + "<<asTaskType:>>" + asTaskType);
			// putting Transaction config xml file into Cache
			ICacheManager loCacheManager = BaseCacheManagerWeb.getInstance();
			Object loCacheObject = XMLUtil.getDomObj(this.getClass().getResourceAsStream(
					"/com/nyc/hhs/config/TransactionConfig.xml"));
			LOG_OBJECT.Debug("Level One_01");
			loCacheManager.putCacheObject("transaction", loCacheObject);
			LOG_OBJECT.Debug("Level One_02");

			Channel loChannelObj = new Channel();
			TransactionManager.executeTransaction(loChannelObj, "reCacheEvidenceValidationComponent");
			List<TaxonomyTree> loList = (List<TaxonomyTree>) loChannelObj.getData("aoCacheEvidenceValidation");

			if (!loList.isEmpty())
			{
				LOG_OBJECT
						.Error("reCacheEvidenceValidation transaction returned non empty collection. There is a mismatch in cache data and DB data");
			}
			else
			{
				// reCacheEvidenceValidation ok
				if (asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_BR_APPLICATION)
						|| asTaskType.equalsIgnoreCase(ApplicationConstants.ENTITY_TYPE_BUSINESS_APPLICATION)
						|| asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SERVICE_APPLICATION)
						|| asTaskType
								.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_SERVICE_APPLICATION))
				{

					LOG_OBJECT.Debug("Generating Print View For Single asPrintViewId");
					lbDocumentCreated = generatePrintVersion(asPrintViewId, asProviderId, asTaskType);
				}

				else if (asTaskType
						.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_BUSINESS_REVIEW_APPLICATION))
				{
					LOG_OBJECT.Debug("Generating Print View For BR WithDrawl scenario for BR ID" + asPrintViewId);
					lbDocumentCreated = generatePrintVersion(asPrintViewId, asProviderId, asTaskType);
					LOG_OBJECT.Debug("After Generating Print View For BR WithDrawl In uploadPrintVersionInDV ");
					// Execute Query and populate string[] and get all service
					// capacity to be withdrawl
					// runing the loop n times
					HashMap loReqdProp = new HashMap();
					// Incase of BR Withdrawl we are passing application Id
					String lsBRAppID = asPrintViewId;
					loReqdProp.put("applicationId", lsBRAppID);
					LOG_OBJECT.Debug("Fetching All Withdrawl SCs_1 for BR ID::" + lsBRAppID + "::& Provider::"
							+ asProviderId);
					loChannelObj = new Channel();
					LOG_OBJECT.Debug("Setting lsBRAppID In Channel");
					loChannelObj.setData("aoHMSection", loReqdProp);
					LOG_OBJECT.Debug("Fetching All Withdrawl SCs_2 from Transaction");
					TransactionManager.executeTransaction(loChannelObj, "fetchServiceCapacityIds_DB");
					LOG_OBJECT.Debug("Fetching All Withdrawl SCs_3");
					List loServiceCapacityWOBNums = (List<WorkflowIDServiceBean>) loChannelObj.getData("wobNums");
					WorkflowIDServiceBean loWorkflowIDServiceBean = null;
					LOG_OBJECT.Debug("Fetching All Withdrawl SCs_4");
					Iterator loIter = loServiceCapacityWOBNums.iterator();

					// Setting the task type for Service capacity print versio
					// in case of BR withdrawl
					LOG_OBJECT.Debug("Generating Print View For ALL WithDrawl SC in BR With drawn scenario");
					while (loIter.hasNext())
					{
						loWorkflowIDServiceBean = (WorkflowIDServiceBean) loIter.next();
						String lsSCWorkFlowId = loWorkflowIDServiceBean.getMsWorkFlowId();
						if (!(lsSCWorkFlowId == null || lsSCWorkFlowId.equalsIgnoreCase("")))
						{
							String lsPrintViewSCId = loWorkflowIDServiceBean.getMsServiceApplicationId();
							LOG_OBJECT.Debug("Generating Print View For SC WithDrawl scenario for SC ID"
									+ lsPrintViewSCId);
							lbDocumentCreated = generatePrintVersion(lsPrintViewSCId, asProviderId,
									P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_SERVICE_APPLICATION);
						}
					}
				}
			}
		} // end try

		catch (ApplicationException loAE)
		{
			LOG_OBJECT.Error("Error in uploadPrintVersionInDV:: ", loAE);
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error in uploadPrintVersionInDV:: ", aoEx);
		}

		LOG_OBJECT.Debug("******************Exited uploadPrintVersionInDV**********************");

		return lbDocumentCreated;

	}

	/**
	 * This method is used to generate the Print Version and upload it in
	 * FileNet OS as a document content
	 * 
	 * 
	 * @param lsPrintViewId is the print view id attribute which is to be set
	 *            for the document
	 * @param aoProviderId is the provider Id attribute for the document
	 * @param lsTaskType specifies the task type i.e. BR or Service application
	 * @return boolean to indicate whether the document has been successfully
	 *         uploaded
	 * @throws Exception if any exception is thrown
	 */

	public boolean generatePrintVersion(String asPrintViewId, String asProviderId, String asTaskType) throws Exception
	{
		boolean lbDocuCreated = false;
		String lsContent = "";
		InputStream loPrintdocStream = null;

		LOG_OBJECT.Debug("Entered generatePrintVersion");

		try
		{
			if (asProviderId == null || asProviderId.equals("") || asPrintViewId == null || asPrintViewId.equals("")
					|| asTaskType == null || asTaskType.equals(""))
			{
				LOG_OBJECT.Debug("Error in generatePrintVersion :: WF parameters are null or empty string");
			}
			else
			{
				// call the method callPrinterFriendlyTransaction for fetching
				// the printable content
				lsContent = callPrinterFriendlyTransaction(asPrintViewId, asProviderId, asTaskType);

				LOG_OBJECT.Debug("Obtained content from callPrinterFriendlyTransaction = " + lsContent);

				if (lsContent == null || lsContent.equals(""))
				{
					LOG_OBJECT.Debug("Component comming as blank from transaction");
				}
				else
				{
					loPrintdocStream = new ByteArrayInputStream(lsContent.getBytes("UTF-8"));

					ObjectStore moObjectStore = getP8Objectstore();
					LOG_OBJECT.Debug("ObjectStore Instance in generatePrintVersion" + moObjectStore.get_Name());
					// making filenet connection

					// creating document property hash map;
					HashMap<String, String> loHMPropertyMap = new HashMap<String, String>();
					loHMPropertyMap.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, asPrintViewId);
					loHMPropertyMap.put("Print_View_ID", asPrintViewId);
					loHMPropertyMap.put("ORGANIZATION_ID", asProviderId);
					loHMPropertyMap.put("HHS_DOC_CREATED_BY", "System");
					loHMPropertyMap.put("HHS_DOC_MODIFIED_BY", "System");

					LOG_OBJECT.Debug("PrintViewId set In Map::" + asPrintViewId);

					// Fetching the folder path for the given provider
					String lsFolderPath = getFolderPath(asProviderId);

					LOG_OBJECT.Debug("Got folderpath = " + lsFolderPath + "for provier id " + asProviderId);

					// call the function uploading the printable content in
					// filenet
					String lsId = createPrintableDocument(moObjectStore, loPrintdocStream,
							P8Constants.PRINTER_FRINDLY_VERSI_DOCUMENT_CLASS, loHMPropertyMap, lsFolderPath);

					LOG_OBJECT.Debug("Created print view document with id :" + lsId);

					lbDocuCreated = true;
				}

			}

		}
		catch (ApplicationException loAE)
		{
			LOG_OBJECT.Error("Error in uploadPrintVersionInDV:: ", loAE);
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error in uploadPrintVersionInDV:: ", aoEx);
		}
		finally
		{
			if (null != loPrintdocStream)
			{
				loPrintdocStream.close();
			}
		}

		LOG_OBJECT.Debug("Exited uploadPrintVersionInDV");
		return lbDocuCreated;
	}

	/**
	 * This method will call the database print functionality and will return an
	 * print view string format. The transaction being called is
	 * printerFriendlyComponent for BR application and
	 * printerFriendlyComponentService for Service application
	 * 
	 * @param lsAppId specifies the application id for the document
	 * @param lsOrgId specifies the provider id for the document
	 * @param asTaskType specifies the task type i.e. BR or Service application
	 * @return the content element String
	 * @throws ApplicationException
	 */

	private String callPrinterFriendlyTransaction(String asAppId, String asOrgId, String asTaskType)
			throws ApplicationException
	{
		String lsContent = "";
		try
		{
			LOG_OBJECT.Debug("Entered callPrinterFriendlyTransaction");
			// putting Transaction config xml file into Cache
			ICacheManager loCacheManager = BaseCacheManagerWeb.getInstance();
			Object loCacheObject = XMLUtil.getDomObj(this.getClass().getResourceAsStream(
					"/com/nyc/hhs/config/TransactionConfig.xml"));
			LOG_OBJECT.Debug("Level One_1");
			loCacheManager.putCacheObject("transaction", loCacheObject);
			LOG_OBJECT.Debug("Level One_2");

			String lsKey = ApplicationConstants.TAXONOMY_ELEMENT;
			Channel loChannelObj = new Channel();
			TransactionManager.executeTransaction(loChannelObj, ApplicationConstants.RETRIEVE_FROM_TAXONOMY
					+ "_component");
			List<TaxonomyTree> loTaxonomyList = (List<TaxonomyTree>) loChannelObj.getData("loTaxonomyList");

			// Instantiating TaxonomyDOM to generate DOM Tree for Taxonomy
			TaxonomyDOMUtil loTaxonomyDOM = new TaxonomyDOMUtil();
			org.jdom.Document loTaxonomyDom = loTaxonomyDOM.createTaxonomyDOMObj(loTaxonomyList);
		
			// Caching Taxonomy DOM
			loCacheManager.putCacheObject(lsKey, loTaxonomyDom);

			// transaction code - call the function through transaction
			loChannelObj = new Channel();
			loChannelObj.setData("asAppId", asAppId);
			loChannelObj.setData("asOrgId", asOrgId);
			loChannelObj.setData("asWebContentPath",
					PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "PROPERTY_WEBCONTENTPATH"));
			loChannelObj.setData("abIsFinalView", Boolean.TRUE);
			// Will Generate BR Print View
			if (asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_BR_APPLICATION)
					|| asTaskType.equalsIgnoreCase(ApplicationConstants.ENTITY_TYPE_BUSINESS_APPLICATION)
					|| asTaskType
							.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_BUSINESS_REVIEW_APPLICATION))
			{
				LOG_OBJECT.Debug("Level One_4::asTaskType" + asTaskType);
				LOG_OBJECT.Debug("Calling Transaction printerFriendlyComponent");
				TransactionManager.executeTransaction(loChannelObj, "printerFriendlyComponent");
			}
			// Will Generate SC Print View
			else if ((asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SERVICE_APPLICATION) || asTaskType
					.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_SERVICE_APPLICATION)))
			{
				LOG_OBJECT.Debug("Level One_5 ::asTaskType" + asTaskType);
				LOG_OBJECT.Debug("Calling Transaction printerFriendlyComponentService");
				TransactionManager.executeTransaction(loChannelObj, "printerFriendlyComponentService");
			}

			LOG_OBJECT.Debug("Transaction executed");
			Map<String, Object> loMap = (Map<String, Object>) loChannelObj.getData("loPrinterFriendlyContent");
			lsContent = BusinessApplicationUtil.convertMapToString(loMap);
			if (lsContent == null || lsContent.equals(""))
			{
				LOG_OBJECT.Debug("Component comming as blank from transaction");
				throw new ApplicationException("Component comming as blank from transaction");
			}
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Error in callPrinterFriendlyTransaction:: ", aoEx);
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error in callPrinterFriendlyTransaction:: ", aoEx);
		}
		LOG_OBJECT.Debug("Exited callPrinterFriendlyTransaction");
		return lsContent;
	}

	/**
	 * The method to get the Filenet object store session
	 * 
	 * @return moObjectStore - the object store fetched
	 * @throws ApplicationException - if any application related exception is
	 *             thrown
	 * @throws WcmException - if any filenet related exception is thrown
	 */

	private ObjectStore getP8Objectstore() throws ApplicationException
	{
		LOG_OBJECT.Info("Entered getP8Objectstore()");

		LOG_OBJECT.Info(":::: PROP_FILE_JAVA_SECURITY_AUTH_LOGIN_CONFIG  Value :::: "
				+ System.getProperty(P8Constants.PROP_FILE_JAVA_SECURITY_AUTH_LOGIN_CONFIG));
		LOG_OBJECT.Info(":::: PROP_FILE_JAVA_NAMING_FACTORY_INITIAL  Value :::: "
				+ System.getProperty(P8Constants.PROP_FILE_JAVA_NAMING_FACTORY_INITIAL));
		LOG_OBJECT.Info(":::: PROPERTY_PE_BOOTSTRAP_CE_URI Value :::: "
				+ System.getProperty(P8Constants.PROPERTY_PE_BOOTSTRAP_CE_URI));

		P8UserSession loUserSession = null;
		P8SecurityOperations loFilenetConnection = new P8SecurityOperations();
		loUserSession = loFilenetConnection.getFileNetConnection(loFilenetConnection.setP8SessionVariables());

		return loFilenetConnection.getObjectStore(loUserSession);

	}

	/**
	 * This method is used for creating a new document and filing the document
	 * in P8 It returns the id of the newly created document
	 * 
	 * @param aoObjStr - the object store where the document is to be created
	 * @param aoIS - the content element input stream
	 * @param asClassName - the document class for which the document will be
	 *            created in P8
	 * @param aoPropertyMap - a hashmap containing the properties to be set for
	 *            the document
	 * @param asFolderpath - the folder path where the document would be filed
	 * @return - the document id of the created document
	 * @throws ApplicationException - if any exception is thrown
	 */

	private String createPrintableDocument(ObjectStore aoObjStr, InputStream aoIS, String asClassName,
			HashMap<String, String> aoPropertyMap, String asFolderpath) throws ApplicationException
	{

		LOG_OBJECT.Debug("Entered createPrintableDocument");

		Document loNewDoc = null;

		try
		{

			// Creating new instance of FileNet Document Objects
			loNewDoc = Factory.Document.createInstance(aoObjStr, asClassName);

			// Setting Content of Document Objects
			loNewDoc = setDocContent(loNewDoc, aoIS);

			// Setting Properties of Document Objects
			for (Entry<String, String> loEntry : aoPropertyMap.entrySet())
			{
				String lsPropName = loEntry.getKey();
				String lsPropValue = loEntry.getValue();
				loNewDoc.getProperties().putObjectValue(lsPropName, lsPropValue);
			}
			loNewDoc.save(RefreshMode.REFRESH);

			// fetching folder for filing custom object
			LOG_OBJECT.Debug("asFolderpath" + asFolderpath);
			Folder loFldr = getFolderByName(aoObjStr, asFolderpath);
			LOG_OBJECT.Debug("loFldr::" + loFldr.get_FolderName());
			LOG_OBJECT.Debug("loFldr::" + loFldr.get_PathName());

			// Setting referential Containment relationship
			ReferentialContainmentRelationship loRcr = loFldr.file(loNewDoc, AutoUniqueName.AUTO_UNIQUE, asClassName,
					DefineSecurityParentage.DEFINE_SECURITY_PARENTAGE);
			loRcr.save(RefreshMode.REFRESH);

			LOG_OBJECT.Debug("New doc created and filed");

		}

		catch (ApplicationException loAE)
		{
			LOG_OBJECT.Error("Error in createPrintableDocument:: ", loAE);
			throw loAE;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in createPrintableDocument:: ", aoEx);
			LOG_OBJECT.Error("Error in createPrintableDocument:: ", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Debug("Exited createPrintableDocument");
		return loNewDoc.get_Id().toString();
	}

	/**
	 * This method is used for setting the content of a document object It
	 * returns the document object along with the content element set
	 * 
	 * @param aoDoc - FileNet document object
	 * @param aoIS - The input stream object representing the content element of
	 *            the document
	 * @return - the document object with the content added
	 * @throws ApplicationException if any exception occured
	 */

	private Document setDocContent(Document aoDoc, InputStream aoIS) throws ApplicationException
	{

		LOG_OBJECT.Debug("Entered setDocContent()");

		try
		{
			// Creating ContentElementList object for setting content.
			ContentElementList loCEL = Factory.ContentElement.createList();
			ContentTransfer loCT = Factory.ContentTransfer.createInstance();
			loCT.setCaptureSource(aoIS);

			// Setting MimeType of the content which will help in viewing
			// document
			loCEL.add(loCT);
			aoDoc.set_ContentElements(loCEL);
			aoDoc.set_MimeType("UTF-8");

			// Check-in document back into FileNet Repository
			aoDoc.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);

			LOG_OBJECT.Debug("Exited setDocContent()");
			return aoDoc;

		}

		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in setDocContent:: ", aoEx);
			LOG_OBJECT.Error("Error in setDocContent():: ", aoEx);
			throw loAppex;
		}
	}

	/**
	 * This method fetches the folder path for a given provider in filenet
	 * 
	 * @param asProviderID - the provider id corresponding to which the provider
	 *            folder is to be fetched
	 * @return the full folder path as a string
	 * @throws ApplicationException in case any exception is thrown
	 */

	private String getFolderPath(String asOrgId) throws ApplicationException
	{

		LOG_OBJECT.Debug("Entered getFolderPath()");

		String lsFullFolderPath = PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
				P8Constants.PROP_FILE_PREDEFINED_FOLDER_PATH_PROVIDER);

		lsFullFolderPath = lsFullFolderPath.concat(P8Constants.STRING_SINGLE_SLASH).concat(asOrgId);

		/* will generate static folder path from constants */
		try
		{
			String lsSubFolderPath = PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
					P8Constants.PROPERTY_PREDEFINED_PRINT_VIEW_SUB_FOLDER_PATH);

			lsFullFolderPath = lsFullFolderPath.concat(lsSubFolderPath);
			LOG_OBJECT.Debug("Full Folder Path::" + lsFullFolderPath);
		}

		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in getFolderPath:: ", aoEx);
			LOG_OBJECT.Error("Error in getFolderPath:: ", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Debug("Exited getFolderPath()");

		return lsFullFolderPath;

	}

	/**
	 * This Method is for checking whether any specific folder is exist in the
	 * FILENET or not
	 * 
	 * @param aoObjectStore - The object-store object where the folder is to be
	 *            checked
	 * @param asPath - the folder path which has to be checked for existence
	 * @return - boolean value denoting whether a folder exists or not
	 * @throws ApplicationException in case any exception is thrown
	 */

	private boolean checkFolderExists(ObjectStore aoObjectStore, String asPath) throws ApplicationException
	{

		LOG_OBJECT.Debug("Entered checkFolderExists()");

		boolean lbFlag = false;

		HashMap<String, String> loHmRequiredProp = new HashMap<String, String>();
		loHmRequiredProp.put("asPath", asPath);

		try
		{
			if (asPath.equalsIgnoreCase(""))
			{
				ApplicationException loAppex = new ApplicationException(
						"Error in checkFolderExists Method.Required Parameters are missing.");
				throw loAppex;
			}

			// Checking whether folder instance exists or not
			Factory.Folder.fetchInstance(aoObjectStore, asPath, null);
			lbFlag = true;

		}
		catch (ApplicationException aoAppex)
		{
			LOG_OBJECT.Error("Error in checkFolderExists:: ", aoAppex);
			throw aoAppex;
		}
		catch (EngineRuntimeException aoEc)
		{
			// Will throws EngineRuntimeException if folder does not exist,which
			// will return "False"
			LOG_OBJECT.Error("folder not found. Returned false", aoEc);
			return lbFlag;

		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Error in checkFolderExists Method.Required Parameters are missing.", aoEx);
			LOG_OBJECT.Error("Error in checkFolderExists:: ", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Debug("Exited checkFolderExists()");
		return lbFlag;
	}

	/**
	 * This Method is used for fetching folder from the FileNet Repository. if
	 * Folder does not exist, it will create the folder.
	 * 
	 * @param aoObjectStore - The object store object from which the folder has
	 *            to be fetched
	 * @param asPath - the folder path which has to be fetched
	 * @return - the folder object
	 * @throws ApplicationException in case any exception is thrown
	 */

	private Folder getFolderByName(ObjectStore aoObjectStore, String asPath) throws Exception
	{

		LOG_OBJECT.Debug("Entered getFolderByName()");

		Folder loFldr = null;

		HashMap<String, String> loHmRequiredProp = new HashMap<String, String>();
		loHmRequiredProp.put("asPath", asPath);

		try
		{

			// checking whether folder exists or not. if no then creating new
			// folder instance
			if (checkFolderExists(aoObjectStore, asPath))
			{

				// if folder path exist, then returning the existing folder
				loFldr = Factory.Folder.fetchInstance(aoObjectStore, asPath, null);
			}
			else
			{
				// if folder path doesn't exist then creating new folder. sample
				// folder path expected /TestRun/P1/f1/f2

				// creating folder path array to check whether all parents are
				// exist or not.
				String[] loSubFolders = asPath.split(P8Constants.STRING_SINGLE_SLASH);

				int liLength = 1;
				String lsParentFolderPath = "";
				String lsChildFolderPath = "";

				// doing looping to check whether all parents folder are exist
				// or not.
				while (liLength < loSubFolders.length)
				{
					lsChildFolderPath = lsParentFolderPath.concat(P8Constants.STRING_SINGLE_SLASH).concat(
							loSubFolders[liLength]);

					// checking child folder exist or not. if no then creating
					// new folder

					if (!checkFolderExists(aoObjectStore, lsChildFolderPath))
					{
						Folder loParent;
						if (lsParentFolderPath.equalsIgnoreCase(""))
						{
							loParent = aoObjectStore.get_RootFolder();
						}
						else
						{
							loParent = Factory.Folder.fetchInstance(aoObjectStore, lsParentFolderPath, null);
						}

						loFldr = Factory.Folder.createInstance(aoObjectStore, ClassNames.FOLDER);
						loFldr.set_Parent(loParent);
						loFldr.set_FolderName(loSubFolders[liLength]);
						loFldr.save(RefreshMode.REFRESH);
					}

					// setting parent folder path value to child folder path
					// value so that check all levels of folder structure
					lsParentFolderPath = lsChildFolderPath;
					liLength++;
				}
			}
		}
		catch (EngineRuntimeException aoRuntimeEx)
		{
			LOG_OBJECT.Error("Error in getFolderPath:: ", aoRuntimeEx);
		}
		catch (Exception aoExp)
		{
			ApplicationException loAppex = new ApplicationException("Error in getFolderPath:: ", aoExp);
			LOG_OBJECT.Error("Error in getFolderPath:: ", aoExp);
			throw loAppex;
		}

		LOG_OBJECT.Debug("Exited getFolderByName()");
		return loFldr;
	}

	/****
	 * methods added for testing component on unix
	 * @throws ApplicationException
	 ***/

	public void writeLog() throws ApplicationException
	{

		LOG_OBJECT.Debug("Entered writeLog");
	}

	public void writeSysout() throws ApplicationException
	{

		LOG_OBJECT.Debug("Entered writeSysout");
	}


}
