package com.nyc.hhs.batch.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.filenet.api.collection.ContentElementList;
import com.filenet.api.collection.RepositoryRowSet;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.AutoUniqueName;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.DefineSecurityParentage;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.filenet.api.query.RepositoryRow;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.nyc.hhs.batch.IBatchQueue;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.PrintViewGenerationBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8SecurityOperations;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This class is being used for generating reports
 */

public class ReportsBatch implements IBatchQueue
{

	private static final LogInfo LOG_OBJECT = new LogInfo(ReportsBatch.class);

	public static Domain moDomain = null;

	/**
	 * Blank implementation of getQueue method of interface
	 * 
	 * @param aoMParameters HashMap of Parameter
	 * @return null
	 */
	@Override
	public List<PrintViewGenerationBean> getQueue(Map aoMParameters)
	{
		return null;
	}

	/**
	 * Implementation of the executeQueue method . This method will call all the
	 * other methods for executing the batch operations
	 * 
	 * @param aoLQueue List of Queue
	 * @throws ApplicationException
	 */
	@Override
	public void executeQueue(List aoLQueue) throws ApplicationException
	{

		LOG_OBJECT.Info("Executing Reports Batch");

		try
		{
			// get object store
			ObjectStore loObjectStore = getP8Objectstore();
			LOG_OBJECT.Info("ObjectStore Instance in generatePrintVersion" + loObjectStore.get_Name());

			// call method for generating report 3.3.2 - Shared Document Summary
			generateDocCategoryTypeReport(loObjectStore);

			// call method for generating report 3.3.6 - Providers Sharing
			// Documents
			generateProviderSharingReport(loObjectStore);

		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in PrintViewBatch.executeQueue()", aoAppEx);
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception in PrintViewBatch.executeQueue()", aoEx);
		}

		LOG_OBJECT.Info("Finished Reports Batch");

	} // end function executeQueue

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
		P8UserSession loUserSession = null;
		P8SecurityOperations loFilenetConnection = new P8SecurityOperations();
		loUserSession = loFilenetConnection.getFileNetConnection(loFilenetConnection.setP8SessionVariables());

		return loFilenetConnection.getObjectStore(loUserSession);

	}

	/**
	 * This method is used for generating a report for document count based on
	 * doc-category an doc-type
	 * @param aoObjectStore - the object store
	 * @throws ApplicationException - if any application related exception is
	 *             thrown
	 * @throws WcmException - if any filenet related exception is thrown
	 */
	public void generateDocCategoryTypeReport(ObjectStore aoObjectStore) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered ReportsBatch.generateDocCategoryTypeReport()");

		try
		{
			// Step1 : Run query to fetch all documents ie. DocCategory and
			// DocType count
			TreeMap<String, String> loAllDocCategoryType = fetchAllDocCategoryType(aoObjectStore);

			// Step2 : Run query to fetch all shared documents count based on
			// doc category and type
			TreeMap<String, int[]> loDocCategoryTypeShared = fetchSharedDocCategoryType(aoObjectStore);

			// Step3 : Create a new hashmap(treemap) consolidating the above 2
			// hashmaps
			TreeMap<String, int[]> loConsolidatedDocCategoryType = new TreeMap<String, int[]>();

			// first add the hashmap for all doc types

			for (Entry<String, String> loEntrySet : loAllDocCategoryType.entrySet())
			{

				int[] liArr = new int[3];
				liArr[0] = Integer.parseInt(loEntrySet.getValue()); 
				liArr[1] = 0;
				liArr[2] = 0;
				loConsolidatedDocCategoryType.put(loEntrySet.getKey(), liArr);
			}

			// now add hashmap for shared documents
			for (Entry<String, int[]> loEntrySet : loDocCategoryTypeShared.entrySet())
			{
				String lsKey = loEntrySet.getKey();
				int[] liSharedCountArr = loEntrySet.getValue();

				int[] liTotCountArr = loConsolidatedDocCategoryType.get(lsKey);
				liTotCountArr[1] = liSharedCountArr[1]; // no. of documents
														// shared with provider
				liTotCountArr[2] = liSharedCountArr[2]; // no. of documents
														// shared with city
				loConsolidatedDocCategoryType.put(lsKey, liTotCountArr);
			}

			// Step 4 : Write output to csv
			writeToCSVDocCategoryType(loConsolidatedDocCategoryType);

		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in generateDocCategoryTypeReport() :", aoEx);
			LOG_OBJECT.Error("Error in generateDocCategoryTypeReport() :", loAppex);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited ReportsBatch.generateDocCategoryTypeReport()");
	}

	/**
	 * 
	 * @param aoObjectStore
	 * @return loDocCategoryTypeShared A Tree-map containing the count of all
	 *         shared documents for provider and agency
	 * @throws ApplicationException
	 */
	/**
	 * This method is used for fetching the count of total no. of documents in
	 * the system based on DocCategory and DocType It returns a tree-map, where
	 * the key is a combination of DocCategory###DocType , and the value is an
	 * int containing the count for no. of documents of each category and type
	 */
	private TreeMap<String, String> fetchAllDocCategoryType(ObjectStore aoObjectStore) throws ApplicationException
	{

		LOG_OBJECT.Info("Entered ReportsBatch.fetchAllDocCategoryType()");
		// treemap is used for sorting on doc category and type
		TreeMap<String, String> loAllDocCategoryType = new TreeMap<String, String>();
		SearchSQL loSqlObject = new SearchSQL();

		// Creating sql query for fetching provider id from custom object
		// corresponding to given document id

		String lsAllDocSQLQuery = "SELECT [DOC_CATEGORY] , [DOC_TYPE] FROM HHS_ACCELERATOR WHERE ([DOC_CATEGORY] is not null AND [DOC_TYPE] is not null)";

		try
		{

			loSqlObject.setQueryString(lsAllDocSQLQuery);

			// Executes the search for fetching custom object.
			SearchScope loSearchScope = new SearchScope(aoObjectStore);

			RepositoryRowSet loMyRows = loSearchScope.fetchRows(loSqlObject, null, null, Boolean.TRUE);
			Iterator loIt = loMyRows.iterator();

			while (loIt.hasNext())
			{

				RepositoryRow loRow = (RepositoryRow) loIt.next();

				String lsDocCategory = loRow.getProperties().get(P8Constants.PROPERTY_CE_DOC_CATEGORY).getStringValue();
				String lsDocType = loRow.getProperties().get(P8Constants.PROPERTY_CE_DOC_TYPE).getStringValue();

				// adding provider id to array list
				if (null == lsDocCategory || lsDocCategory.equalsIgnoreCase(""))
				{
					LOG_OBJECT.Debug("Error in generateDocCategoryTypeReport():lsDocCategory is null or blank");
				}
				else if (null == lsDocType || lsDocType.equalsIgnoreCase(""))
				{
					LOG_OBJECT.Debug("Error in generateDocCategoryTypeReport():lsDocType is null or blank");
				}
				else
				{
					/*
					 * Here we are counting the no. of documents for each
					 * DocCategory-DocType combination
					 */

					// use combination of DocCategory and DocType as the key
					String lsKey = lsDocCategory + "###" + lsDocType;
					// check if there already exists an entry for the obtained
					// DocCategory and DocType
					if (!loAllDocCategoryType.containsKey(lsKey))
					{
						// hashmap doesn't contain this doctype|doccategory
						// entry, so add with a count of 1
						loAllDocCategoryType.put(lsKey, "1");
					}
					else
					{
						// hashmap already contains an entry for this
						// doctype|doccategory, so increment the count by 1
						int liCount = Integer.parseInt(loAllDocCategoryType.get(lsKey));
						liCount++;
						loAllDocCategoryType.put(lsKey, String.valueOf(liCount));
					}
				}// end if
			}// wend

		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in fetchAllDocCategoryType() :", aoEx);
			LOG_OBJECT.Error("Error in fetchAllDocCategoryType() :", loAppex);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited ReportsBatch.fetchAllDocCategoryType()");

		return loAllDocCategoryType;
	}

	/**
	 * 
	 * @param aoObjectStore
	 * @return loDocCategoryTypeShared A Tree-map containing the count of all
	 *         shared documents for provider and agency
	 * @throws ApplicationException
	 */
	/**
	 * This method is used for fetching the count of total shared documents
	 * based on DocCategory and DocType for provider and agency It returns a
	 * tree-map, where the key is a combination of DocCategory###DocType , and
	 * the value is an int array such that liArr[0] contains total no. of share
	 * documents, liArr[1] contains no. of docs shared with provider and
	 * liArr[2] contains no. of docs shared with agency
	 */
	private TreeMap<String, int[]> fetchSharedDocCategoryType(ObjectStore aoObjectStore) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered ReportsBatch.fetchSharedDocCategoryType()");
		// treemap is used for sorting on doc category and type
		TreeMap<String, int[]> loDocCategoryTypeShared = new TreeMap<String, int[]>();
		SearchSQL loSqlObject = new SearchSQL();
		ArrayList<String> loProviderSharedDocIds = new ArrayList<String>();
		ArrayList<String> loAgencySharedDocIds = new ArrayList<String>();
		// Creating sql query for fetching provider id from custom object
		// corresponding to given document id
		String lsSharedObjSQLQuery = "SELECT Doc.DOC_CATEGORY , Doc.DOC_TYPE , ShObj.HHS_AGENCY_ID , ShObj.HHSProviderID , ShObj.SHARED_DOCUMENT_ID"
				+ " FROM HHSSharedDocument ShObj inner join HHS_ACCELERATOR Doc"
				+ " on Doc.ID = ShObj.SHARED_DOCUMENT_ID ORDER BY Doc.DOC_CATEGORY , Doc.DOC_TYPE";
		try
		{
			loSqlObject.setQueryString(lsSharedObjSQLQuery);
			// Executes the search for fetching custom object.
			SearchScope loSearchScope = new SearchScope(aoObjectStore);
			RepositoryRowSet loRows = loSearchScope.fetchRows(loSqlObject, null, null, Boolean.TRUE);
			Iterator loIt = loRows.iterator();
			while (loIt.hasNext())
			{
				getDocCategoryType(loDocCategoryTypeShared, loProviderSharedDocIds, loAgencySharedDocIds, loIt);
			}// wend
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in fetchSharedDocCategoryType() :", aoEx);
			LOG_OBJECT.Error("Error in fetchSharedDocCategoryType() :", loAppex);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exited ReportsBatch.fetchSharedDocCategoryType()");
		return loDocCategoryTypeShared;
	}

	/**
	 * This method get the Document Category Type
	 * 
	 * @param loDocCategoryTypeShared
	 * @param loProviderSharedDocIds
	 * @param loAgencySharedDocIds
	 * @param loIt
	 */
	private void getDocCategoryType(TreeMap<String, int[]> loDocCategoryTypeShared,
			ArrayList<String> loProviderSharedDocIds, ArrayList<String> loAgencySharedDocIds, Iterator loIt) throws ApplicationException
	{
		RepositoryRow loRow = (RepositoryRow) loIt.next();
		String lsDocCategory = loRow.getProperties().get(P8Constants.PROPERTY_CE_DOC_CATEGORY).getStringValue();
		String lsDocType = loRow.getProperties().get(P8Constants.PROPERTY_CE_DOC_TYPE).getStringValue();
		String lsProviderId = loRow.getProperties().get(P8Constants.PROPERTY_CE_SHARED_PROVIDER_ID).getStringValue();
		String lsAgencyId = loRow.getProperties().get(P8Constants.PROPERTY_CE_SHARED_AGENCY_ID).getStringValue();
		String lsSharedDocId = loRow.getProperties().get(P8Constants.PROPERTY_CE_SHARED_DOC_ID).getIdValue().toString();
		// adding provider id to array list
		if (null == lsDocCategory || lsDocCategory.equalsIgnoreCase(""))
		{
			LOG_OBJECT.Debug("Error in generateDocCategoryTypeReport():lsDocCategory is null or blank");
		}
		else if (null == lsDocType || lsDocType.equalsIgnoreCase(""))
		{
			LOG_OBJECT.Debug("Error in generateDocCategoryTypeReport():lsDocType is null or blank");
		}
		else if (null == lsSharedDocId || lsSharedDocId.equalsIgnoreCase(""))
		{
			LOG_OBJECT.Debug("Error in generateDocCategoryTypeReport():lsDocType is null or blank");
		}
		else
		{
			/*
			 * Here we are counting the no. of custom objects for each
			 * DocCategory-DocType combination
			 */
			// use combination of DocCategory and DocType as the key
			String lsKey = lsDocCategory + "###" + lsDocType;
			// check if there already exists an entry for the obtained
			// DocCategory and DocType
			if (!loDocCategoryTypeShared.containsKey(lsKey))
			{
				// hashmap doesn't contain this doctype|doccategory
				// entry, so add with a count of 1
				int[] liArr = new int[3];
				liArr[0] = 1; // total no. of shared docs
				if (null != lsProviderId)
				{
					liArr[1] = 1; // no. of docs shared with provider
					liArr[2] = 0;
					loProviderSharedDocIds.add(lsSharedDocId);
				}
				if (null != lsAgencyId)
				{
					liArr[1] = 0;
					liArr[2] = 1; // no. of docs shared with agency
					loAgencySharedDocIds.add(lsSharedDocId);
				}
				loDocCategoryTypeShared.put(lsKey, liArr);
			}
			else
			{
				// hashmap already contains an entry for this
				// doctype|doccategory, so increment the count by 1
				int[] liCountArr = loDocCategoryTypeShared.get(lsKey);
				int liTotSharedCount = liCountArr[0];
				int liProviderCount = liCountArr[1];
				int liAgencyCount = liCountArr[2];
				liTotSharedCount++;
				if (null != lsProviderId && (!loProviderSharedDocIds.contains(lsSharedDocId)))
				{
					liProviderCount++;
					loProviderSharedDocIds.add(lsSharedDocId);
				}
				if (null != lsAgencyId && (!loAgencySharedDocIds.contains(lsSharedDocId)))
				{
					liAgencyCount++;
					loAgencySharedDocIds.add(lsSharedDocId);
				}
				liCountArr[0] = liTotSharedCount;
				liCountArr[1] = liProviderCount;
				liCountArr[2] = liAgencyCount;
				loDocCategoryTypeShared.put(lsKey, liCountArr);
			}
		}// end if
	}

	/**
	 * 
	 * @param aoDocCategoryType : A hashmap containing the docCategory and
	 *            DocType as key (seperated by ###) and an int[] as value for
	 *            storing all the counts
	 */
	/**
	 * This method is used to read data from the hashmap aoDocCategoryType and
	 * write it into a csv file *
	 */
	public void writeToCSVDocCategoryType(TreeMap<String, int[]> aoDocCategoryType)
	{
		LOG_OBJECT.Info("Enterted ReportsBatch.writeToCSVDocCategoryType()");
		FileOutputStream loFileOut = null;
		FileInputStream loFileIn = null;
		try
		{
			// initialize the excel sheet
			String lsReportFilePath = PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "REPORT_FILEPATH");
			String lsReportFileName = PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "REPORT_FILENAME");
			String lsFilename = lsReportFilePath + lsReportFileName;
			HSSFWorkbook loHwb = new HSSFWorkbook();
			HSSFSheet loSheet = loHwb.createSheet("Report Doc Category Type");
			HSSFRow loRowhead = loSheet.createRow((short) 0);
			
			loRowhead.createCell(0).setCellValue("DOCUMENT CATEGORY");
			loRowhead.createCell(1).setCellValue("DOCUMENT TYPE");
			loRowhead.createCell(2).setCellValue("No.OF DOCUMENTS");
			loRowhead.createCell(3).setCellValue("No. OF DOCUMENTS SHARED WITH PROVIDERS");
			loRowhead.createCell(4).setCellValue("No. OF DOCUMENTS SHARED WITH CITY");
			setCellValue(aoDocCategoryType, loSheet);
			loFileOut = new FileOutputStream(lsFilename);
			loHwb.write(loFileOut);
			loFileIn = new FileInputStream(lsFilename);
			Calendar loCalendar = Calendar.getInstance();
			java.util.Date loCurrentDate = loCalendar.getTime();
			java.sql.Timestamp loOrgTimestamp = new java.sql.Timestamp(loCurrentDate.getTime());
			// call method to upload in filenet
			ObjectStore loObjectStore = getP8Objectstore();
			LOG_OBJECT.Info("ObjectStore Instance:" + loObjectStore.get_Name());
			// making filenet connection
			// creating document property hash map;
			HashMap loHMPropertyMap = new HashMap();
			loHMPropertyMap.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE,
					"Shared Document Summary_" + loOrgTimestamp.toString());
			loHMPropertyMap.put(P8Constants.PROPERTY_CE_REPORT_TYPE, "3.3.2");
			String lsDocId = createFileNetDocument(loObjectStore, loFileIn,
					PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "REPORT_P8_DOC_CLASS"), loHMPropertyMap,
					PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "REPORT_P8_FOLDER_PATH"));
			LOG_OBJECT.Info("Your excel file has been generated for report 3.3.2 with Doc id " + lsDocId);
		}
		catch (FileNotFoundException aoFnEx)
		{
			LOG_OBJECT.Error("Error in writeToCSVDocCategoryType() :", aoFnEx);
		}
		catch (IOException aoIoEx)
		{
			LOG_OBJECT.Error("Error in writeToCSVDocCategoryType() :", aoIoEx);
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error in writeToCSVDocCategoryType() :", aoEx);
		}
		finally
		{
			try
			{
				loFileOut.close();
				loFileIn.close();
			}
			catch (FileNotFoundException aoFnEx)
			{
				LOG_OBJECT.Error("Error in writeToCSVDocCategoryType() in finally block :", aoFnEx);
			}
			catch (IOException aoIoEx)
			{
				LOG_OBJECT.Error("Error in writeToCSVDocCategoryType() in finally block :", aoIoEx);
			}
			catch (Exception aoEx)
			{
				LOG_OBJECT.Error("Error in writeToCSVDocCategoryType() in finally block :", aoEx);
			}

			LOG_OBJECT.Info("Exited ReportsBatch.writeToCSVDocCategoryType()");
		}
	}

	/**
	 * This method setValue for rows.
	 * 
	 * @param aoDocCategoryType
	 * @param loSheet
	 */
	private void setCellValue(TreeMap<String, int[]> aoDocCategoryType, HSSFSheet loSheet)
	{
		int liRowCount = 1;
		for (Entry<String, int[]> loEntrySet : aoDocCategoryType.entrySet())
		{
			String lsKey = loEntrySet.getKey();
			int[] liCountArr = loEntrySet.getValue();
			int liTotCount = liCountArr[0];
			int liProviderCount = liCountArr[1];
			int liAgencyCount = liCountArr[2];
			String[] lsTemp = lsKey.split("###");
			String lsDocCategory = lsTemp[0];
			String lsDocType = lsTemp[1];
			HSSFRow loRow = loSheet.createRow(liRowCount);
			loRow.createCell(0).setCellValue(lsDocCategory);
			loRow.createCell(1).setCellValue(lsDocType);
			loRow.createCell(2).setCellValue(liTotCount);
			loRow.createCell(3).setCellValue(liProviderCount);
			loRow.createCell(4).setCellValue(liAgencyCount);
			liRowCount++;
		}
	}

	/**
	 * The method to get the Filenet object store session
	 * 
	 * @param aoObjectStore - the object store
	 * @throws ApplicationException - if any application related exception is
	 *             thrown
	 * @throws WcmException - if any filenet related exception is thrown
	 */

	public void generateProviderSharingReport(ObjectStore aoObjectStore) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered ReportsBatch.generateProviderSharingReport()");
		String lsKey = null;
		HashMap<String, String> loProviderSharing = new HashMap<String, String>();
		HashMap<String, String> loSharedTo = new HashMap<String, String>();
		ArrayList<String> loSharedDocIds = new ArrayList<String>();
		// TreeMap used for sorting
		TreeMap<String, String[]> loConsolidatedSharing = new TreeMap<String, String[]>();
		SearchSQL loSqlObject = new SearchSQL();
		// Creating sql query for fetching provider id from custom object
		// corresponding to given document id
		String lsSQLQuery = "SELECT HHS_SHARED_BY , HHS_AGENCY_ID , HHSProviderID , SHARED_DOCUMENT_ID FROM [HHSSharedDocument] ORDER BY HHS_SHARED_BY";
		try
		{
			loSqlObject.setQueryString(lsSQLQuery);
			// Executes the search for fetching custom object.
			SearchScope loSearchScope = new SearchScope(aoObjectStore);
			RepositoryRowSet loMyRow = loSearchScope.fetchRows(loSqlObject, null, null, Boolean.TRUE);
			setSharedTo(loProviderSharing, loSharedTo, loSharedDocIds, loMyRow);
			LOG_OBJECT.Info("Obtained hashmap loProviderSharing = " + loProviderSharing.toString());
			LOG_OBJECT.Info("Obtained hashmap loSharedTo = " + loSharedTo.toString());
			// 3. Now create a consolidated hashmap for both the hashmaps for
			// documents shared-by and shared-to
			// The array loArrCount will hold the counts for a provider or
			// agency loCountArr[0] will hold the no. of documents shared by the
			// provider/agency
			// loCountArr[1] will hold the no. of documents shared with the
			// provider/agency
			String[] loCountArr = null;
			// 3.1. Add all providers sharing documents
			for (Entry<String, String> loEntrySet : loProviderSharing.entrySet())
			{
				String lsProviderName;
				String lsProvId = loEntrySet.getKey();
				String lsSharedByProviderCount = loEntrySet.getValue();
				loCountArr = new String[2];
				loCountArr[0] = lsSharedByProviderCount;
				loCountArr[1] = "0";
				// if the user id is city_org then set the predefined name for
				// the accelerator user
				if (ApplicationConstants.CITY_ORG.equalsIgnoreCase(lsProvId))
				{
					lsProviderName = ApplicationConstants.CITY_USER_NAME;
				}
				else
				{
					lsProviderName = getProviderName(lsProvId);
					// if the name does not exists in staff details then find it
					// in agency details as in release 4 agency can share a
					// document also
					if (null == lsProviderName || lsProviderName.isEmpty())
					{
						lsProviderName = getAgencyName(lsProvId);
					}
				}
				lsKey = (null != lsProviderName) ? lsProviderName : lsProvId;
				loConsolidatedSharing.put(lsKey, loCountArr);
			}
			// 3.2. Add all providers and agencies with whom documents have been
			// shared
			setToSharedTo(loSharedTo, loConsolidatedSharing);
			// write output to csv
			writeToCSVProviderSharing(loConsolidatedSharing);
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in generateProviderSharingReport() :", aoEx);
			LOG_OBJECT.Error("Error in generateProviderSharingReport() :", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exited ReportsBatch.generateProviderSharingReport()");
	}

	/**
	 * This method set loConsolidatedSharing TreeMap
	 * 
	 * @param loSharedTo
	 * @param loConsolidatedSharing
	 * @throws ApplicationException
	 */
	private void setToSharedTo(HashMap<String, String> loSharedTo, TreeMap<String, String[]> loConsolidatedSharing)
			throws ApplicationException
	{
		String lsKey;
		String[] loCountArr;
		for (Entry<String, String> loEntrySet : loSharedTo.entrySet())
		{
			String lsSharedWithId = loEntrySet.getKey();
			String lsSharedWithCount = loEntrySet.getValue();
			// Check if there provider name for the lsSharedWithId. In case
			// no name is found, then check if a name exists for an agency.
			String lsProviderName = getProviderName(lsSharedWithId);
			if (null != lsProviderName)
			{
				lsKey = lsProviderName;
			}
			else
			{
				// search for agency name
				String lsAgencyName = getAgencyName(lsSharedWithId);
				if (null != lsAgencyName)
				{
					lsKey = lsAgencyName;
				}
				else
				{
					lsKey = lsSharedWithId;
				}// this condition should never happen, as lsSharedWithId is
					// always either a provider or agency
			}
			if (!loConsolidatedSharing.containsKey(lsKey))
			{
				loCountArr = new String[2];
				loCountArr[0] = "0";
				loCountArr[1] = lsSharedWithCount;
				loConsolidatedSharing.put(lsKey, loCountArr);
			}
			else
			{
				loCountArr = loConsolidatedSharing.get(lsKey);
				loCountArr[1] = lsSharedWithCount;
				loConsolidatedSharing.put(lsKey, loCountArr);
			}
		}
	}

	/**
	 * This method set loSharedTo HashMap
	 * 
	 * @param loProviderSharing
	 * @param loSharedTo
	 * @param loSharedDocIds
	 * @param myRows
	 */
	private void setSharedTo(HashMap<String, String> loProviderSharing, HashMap<String, String> loSharedTo,
			ArrayList<String> loSharedDocIds, RepositoryRowSet myRows)throws ApplicationException
	{
		String lsKey;
		Iterator loIt = myRows.iterator();
		while (loIt.hasNext())
		{
			RepositoryRow loRow = (RepositoryRow) loIt.next();
			String lsSharedById = loRow.getProperties().get(P8Constants.PROPERTY_CE_SHARED_BY_ID).getStringValue();
			String lsSharedDocId = loRow.getProperties().get(P8Constants.PROPERTY_CE_SHARED_DOC_ID).getIdValue()
					.toString();
			// adding provider id to array list
			if (null == lsSharedById || lsSharedById.equalsIgnoreCase(""))
			{
				LOG_OBJECT.Debug("Error in generateProviderSharingReport():lsSharedById is null or blank");
			}
			else
			{
				// 1. Prepare a hashmap for count of documents shared by
				// each provider
				lsKey = lsSharedById;
				// check if there already exists an entry for the provider
				if (!loProviderSharing.containsKey(lsSharedById))
				{
					/**
					 * hashmap doesn't contain this provider entry, so add with
					 * a count of 1
					 */
					loProviderSharing.put(lsKey, "1");
					loSharedDocIds.add(lsSharedDocId);
				}
				else
				{
					/**
					 * hashmap already contains an entry for this provider,so
					 * increment the count by 1
					 */
					// check if an entry is already made for this doc id, to
					// avoid duplicate entries for a same doc
					if (!loSharedDocIds.contains(lsSharedDocId))
					{
						int liCount = Integer.parseInt(loProviderSharing.get(lsKey));
						liCount++;
						loProviderSharing.put(lsKey, String.valueOf(liCount));
						loSharedDocIds.add(lsSharedDocId);
					}
				}
				// 2. Prepare a hashmap of documents shared to each
				// provider/agency
				String lsSharedToProviderId = loRow.getProperties().get(P8Constants.PROPERTY_CE_SHARED_PROVIDER_ID)
						.getStringValue();
				String lsSharedToAgencyId = loRow.getProperties().get(P8Constants.PROPERTY_CE_SHARED_AGENCY_ID)
						.getStringValue();
				if (null != lsSharedToProviderId)
				{
					// document is shared with a provider
					lsKey = lsSharedToProviderId;
				}
				else if (null != lsSharedToAgencyId)
				{
					// document is shared with an agency
					lsKey = lsSharedToAgencyId;
				}
				// check if there already exists an entry in the hashmap for
				// the provider or agency
				if (!loSharedTo.containsKey(lsKey))
				{
					// hashmap doesn't contain this provider/agency entry,
					// so add with a count of 1
					loSharedTo.put(lsKey, "1");
				}
				else
				{
					// hashmap already contains an entry for this
					// provider/agency ,so increment the count by 1
					int liCount = Integer.parseInt(loSharedTo.get(lsKey));
					liCount++;
					loSharedTo.put(lsKey, String.valueOf(liCount));
				}
			}// end if
		}// wend
	}

	/**
	 * @param aoDocCategoryType : A hashmap containing the docCategory and
	 *            DocType as key (seperated by ###) and the count as value
	 */
	/**
	 * This method is used to read data from the hashmap aoDocCategoryType and
	 * write it into a csv file *
	 */
	public void writeToCSVProviderSharing(TreeMap<String, String[]> aoConsolidatedSharing)
	{
		LOG_OBJECT.Info("Entered ReportsBatch.writeToCSVProviderSharing()");
		FileOutputStream loFileOut = null;
		FileInputStream loFileIn = null;
		String[] loCountArr = null;
		try
		{
			// initialize the excel sheet
			String lsReportFilePath = PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "REPORT_FILEPATH");
			String lsReportFileName = PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "REPORT_FILENAME");
			String lsFilename = lsReportFilePath + lsReportFileName;
			HSSFWorkbook loHwb = new HSSFWorkbook();
			HSSFSheet loSheet = loHwb.createSheet("ReportProviderSharing");
			HSSFRow loRowhead = loSheet.createRow((short) 0);
			loRowhead.createCell(0).setCellValue("ORGANIZATION NAME");
			loRowhead.createCell(1).setCellValue("No. OF SHARED DOCUMENTS");
			loRowhead.createCell(2).setCellValue("No. OF RECEIVED SHARED DOCUMENTS");
			int liRowCount = 1;

			for (Entry<String, String[]> loEntrySet : aoConsolidatedSharing.entrySet())
			{
				String lsKey = loEntrySet.getKey();
				loCountArr = loEntrySet.getValue();
				HSSFRow loRow = loSheet.createRow(liRowCount);
				// provider/agency
				loRow.createCell(0).setCellValue(lsKey);
				// no. of documents shared by. For agencies it will be 0
				loRow.createCell(1).setCellValue(loCountArr[0]);
				// no. of documents shared to
				loRow.createCell(2).setCellValue(loCountArr[1]);
				liRowCount++;
			}
			loFileOut = new FileOutputStream(lsFilename);
			loHwb.write(loFileOut);
			loFileOut.close();
			Calendar loCalendar = Calendar.getInstance();
			java.util.Date loNow = loCalendar.getTime();
			java.sql.Timestamp loTimeStamp = new java.sql.Timestamp(loNow.getTime());
			// call method to upload in filenet
			ObjectStore loObjectStore = getP8Objectstore();
			LOG_OBJECT.Info("ObjectStore Instance:" + loObjectStore.get_Name());
			loFileIn = new FileInputStream(lsFilename);
			// creating document property hash map;
			HashMap<String, String> loHMPropertyMap = new HashMap<String, String>();
			loHMPropertyMap.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE,
					"Providers Sharing Documents_" + loTimeStamp.toString());
			loHMPropertyMap.put(P8Constants.PROPERTY_CE_REPORT_TYPE, "3.3.6");
			String lsDocId = createFileNetDocument(loObjectStore, loFileIn,
					PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "REPORT_P8_DOC_CLASS"), loHMPropertyMap,
					PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "REPORT_P8_FOLDER_PATH"));
			LOG_OBJECT.Info("Your excel file for 3.3.6 has been generated with Doc id " + lsDocId);

		}
		catch (FileNotFoundException aoFnEx)
		{
			LOG_OBJECT.Error("Error in writeToCSVDocCategoryType() :", aoFnEx);
		}
		catch (IOException aoIoEx)
		{
			LOG_OBJECT.Error("Error in writeToCSVDocCategoryType() :", aoIoEx);
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error in ReportsBatch.writeToCSVProviderSharing()", aoEx);
		}
		finally
		{
			try
			{
				loFileOut.close();
				loFileIn.close();
			}
			catch (FileNotFoundException aoFnEx)
			{
				LOG_OBJECT.Error("Error in writeToCSVDocCategoryType() in finally block :", aoFnEx);
			}
			catch (IOException aoIoEx)
			{
				LOG_OBJECT.Error("Error in writeToCSVDocCategoryType() in finally block :", aoIoEx);
			}
			catch (Exception aoEx)
			{
				LOG_OBJECT.Error("Error in writeToCSVDocCategoryType() in finally block :", aoEx);
			}
			LOG_OBJECT.Info("Exited ReportsBatch.writeToCSVProviderSharing()");
		}
	}

	/**
	 * 
	 * @param aoObjStr
	 * @param aoIS
	 * @param asClassName
	 * @param aoPropertyMap
	 * @param asFolderpath
	 * @return a string as the doc id
	 * @throws ApplicationException
	 */
	/**
	 * This method is used for creating a filenet document with the given input
	 * stream
	 */
	private String createFileNetDocument(ObjectStore aoObjStr, FileInputStream aoIS, String asClassName,
			HashMap<String, String> aoPropertyMap, String asFolderpath) throws ApplicationException
	{

		LOG_OBJECT.Info("Entered createFileNetDocument");

		Document loNewDoc = null;

		try
		{

			// Creating new instance of FileNet Document Objects
			loNewDoc = Factory.Document.createInstance(aoObjStr, asClassName);

			// Setting Content of Document Objects
			loNewDoc = setDocContent(loNewDoc, aoIS);

			// Setting Properties of Document Objects
			for (Entry<String, String> loEntrySet : aoPropertyMap.entrySet())
			{
				String lsPropName = loEntrySet.getKey();
				String lsPropValue = loEntrySet.getValue();
				loNewDoc.getProperties().putObjectValue(lsPropName, lsPropValue);
			}

			loNewDoc.save(RefreshMode.REFRESH);

			// fetching folder for filing custom object
			LOG_OBJECT.Info("asFolderpath" + asFolderpath);
			Folder loFldr = Factory.Folder.fetchInstance(aoObjStr, asFolderpath, null);
			if (null == loFldr)
			{
				LOG_OBJECT.Debug("Error in fetching folder " + asFolderpath);
				throw new ApplicationException("Error in fetching folder " + asFolderpath);
			}
			else
			{
				LOG_OBJECT.Info("loFldr::" + loFldr.get_FolderName());
				LOG_OBJECT.Info("loFldr::" + loFldr.get_PathName());

				// Setting referential Containment relationship
				ReferentialContainmentRelationship loRcr = loFldr.file(loNewDoc, AutoUniqueName.AUTO_UNIQUE,
						asClassName, DefineSecurityParentage.DEFINE_SECURITY_PARENTAGE);
				loRcr.save(RefreshMode.REFRESH);

				LOG_OBJECT.Info("New doc created and filed");
			}

		}
		catch (ApplicationException loAE)
		{
			LOG_OBJECT.Error("Error in createFileNetDocument:: ", loAE);
			throw loAE;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in createFileNetDocument:: ", aoEx);
			LOG_OBJECT.Error("Error in createFileNetDocument:: ", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited createFileNetDocument");
		return loNewDoc.get_Id().toString();
	}

	/**
	 * 
	 * @param aoDoc
	 * @param aoIS
	 * @return
	 * @throws ApplicationException
	 */
	/**
	 * This method is used for setting the content element for the given doc
	 * object
	 */
	private Document setDocContent(Document aoDoc, InputStream aoIS) throws ApplicationException
	{

		LOG_OBJECT.Info("Entered setDocContent()");

		try
		{
			// Creating ContentElementList object for setting content.
			ContentElementList loCEL = Factory.ContentElement.createList();
			ContentTransfer loCT = Factory.ContentTransfer.createInstance();
			loCT.setCaptureSource(aoIS);
			loCT.set_RetrievalName(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "REPORT_FILENAME"));
			loCT.set_ContentType(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "REPORT_MIME_TYPE"));

			loCEL.add(loCT);
			aoDoc.set_ContentElements(loCEL);

			// Check-in document back into FileNet Repository
			aoDoc.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);

			LOG_OBJECT.Info("Exited setDocContent()");
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
	 * This Method Returns Provider Name of the Org ID
	 * 
	 * @param asOrgId Organization Id
	 * @return Provider Name
	 * @throws ApplicationException
	 */
	private String getProviderName(String asOrgId) throws ApplicationException
	{
		Channel aoChannel = new Channel();
		Map loHMap = new HashMap();
		loHMap.put("OrgID", asOrgId);
		aoChannel.setData("loHMap", loHMap);
		TransactionManager.executeTransaction(aoChannel, "fetchProviderNameBatch");
		return (String) aoChannel.getData("fetchProviderNameBatchResult");
	}

	/**
	 * This Method Returns Provider Name of the Agency ID
	 * 
	 * @param asAgencyId Agency Id
	 * @return Agency Name
	 * @throws ApplicationException
	 */
	private String getAgencyName(String asAgencyId) throws ApplicationException
	{
		Channel aoChannel = new Channel();
		Map loHMap = new HashMap();
		loHMap.put("asAgencyId", asAgencyId);
		aoChannel.setData("loHMap", loHMap);
		TransactionManager.executeTransaction(aoChannel, "fetchAgencyNameBatch");
		return (String) aoChannel.getData("fetchAgencyNameBatchResult");
	}
}// end class