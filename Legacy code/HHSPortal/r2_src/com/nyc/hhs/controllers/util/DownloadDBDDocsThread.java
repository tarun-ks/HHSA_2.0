package com.nyc.hhs.controllers.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import com.accenture.util.SaveFormOnLocalUtil;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.service.filenetmanager.p8services.P8ContentService;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;

/**
 * 
 * This is a thread class which implements runnable interface and used to down
 * load DBD documents into the zip file sequentially from the filenet
 */
public class DownloadDBDDocsThread implements Runnable
{
	/**
	 * This is a log object used to log any exception into log file.
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(DownloadDBDDocsThread.class);
	/**
	 * Private final CountDownLatch
	 */
	private final CountDownLatch moStartSignal;
	/**
	 * Private final CountDownLatch
	 */
	private final CountDownLatch moDoneSignal;
	/**
	 * Private final P8UserSession
	 */
	private final P8UserSession moUserSession;
	/**
	 * Private final Map<String, String>
	 */
	private final Map<String, String> moDBDDocDetails;
	/**
	 * Private final String
	 */
	private final String msPath;
	
	private final Boolean mbIsFinancial;
	
	/**
	 * Parameterized constructor for DownloadDBDDocThread class. This method is
	 * responsible for intializing various member variables of
	 * DownloadDBDDocThread class
	 * @param aoStartSignal - Thread Start Sequence
	 * @param aoDoneSignal - Thread End Sequence
	 * @param aoUserSession - Filenet Session
	 * @param aoDBDDocDetails - DBD doc details map
	 * @param asPath - Path at which file needs to be saved
	 */
	public DownloadDBDDocsThread(CountDownLatch aoStartSignal, CountDownLatch aoDoneSignal,
			P8UserSession aoUserSession, Map<String, String> aoDBDDocDetails, String asPath, Boolean abIsFinancial)
	{
		this.moStartSignal = aoStartSignal;
		this.moDoneSignal = aoDoneSignal;
		this.moUserSession = aoUserSession;
		this.moDBDDocDetails = aoDBDDocDetails;
		this.msPath = asPath;
		this.mbIsFinancial = abIsFinancial;
	}
	
	/**
	 * This method is run method for DownloadDBDDoc thread and will be
	 * responsible for downloading dbd docs from filenet and saving them in
	 * proper folder structure
	 */
	public void run()
	{
		try
		{
			moStartSignal.await();
			P8ContentService loP8ContentService = new P8ContentService();
			Map loDocumentMap = loP8ContentService.getDocumentContent(moUserSession,
					moDBDDocDetails.get(HHSConstants.DOCUMENT_IDENTIFIER_ID));
			createFile(msPath, loDocumentMap, moDBDDocDetails, mbIsFinancial);
			moDoneSignal.countDown();
		}
		catch (InterruptedException aoExp)
		{
			LOG_OBJECT.Error("Error while downloading dbd documents - run method", aoExp);
		}
		// Any Exception from DAO class will be thrown as Application
		// Exception
		// which will be handles over here. It throws Application Exception
		// back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error while downloading dbd documents - run method", aoExp);
		}
	}
	
	/**
	 * This method converts the input stream to file and writes it to the
	 * specified path
	 * <ul>
	 * <li>1. Create the file path where file will be saved</li>
	 * <li>2. Check if same file exists, if yes rename the current file else
	 * create new file</li>
	 * <li>3. Write the content from input stream to file</li>
	 * <li></li>
	 * @param asFilePath - File path at which file has to be saved
	 * @param aoDocumentMap - Document information map from Filenet
	 * @param moDBDDocDetails - Document related details from DB
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	private void createFile(String asFilePath, Map aoDocumentMap, Map<String, String> moDBDDocDetails,
			Boolean abFinancialDoc) throws ApplicationException
	{
		OutputStream loOutputStream = null;
		InputStream loContent = null;
		File loFile = null;
		try
		{
			String lsDocTitle = moDBDDocDetails.get(HHSConstants.FILE_DOCUMENT_TITLE);
			String lsOrgName = moDBDDocDetails.get(HHSConstants.ORGANIZATION_NAME);
			String lsProposalTitle = moDBDDocDetails.get(HHSConstants.PROP_TITLE);
			String lsDocExt = (String) aoDocumentMap.get(HHSConstants.FILE_TYPE);
			loContent = (InputStream) aoDocumentMap.get(HHSConstants.CONTENT_ELEMENT);
			SaveFormOnLocalUtil.createPath(asFilePath + HHSConstants.FORWARD_SLASH + lsOrgName
					+ HHSConstants.FORWARD_SLASH + lsProposalTitle);
			
			if (abFinancialDoc)
			{
				loFile = new File(asFilePath + HHSConstants.FORWARD_SLASH + lsDocTitle + HHSConstants.DOT
						+ lsDocExt.toLowerCase());
			}
			else
			{
				loFile = new File(asFilePath + HHSConstants.FORWARD_SLASH + lsOrgName + HHSConstants.FORWARD_SLASH
						+ lsProposalTitle + HHSConstants.FORWARD_SLASH + lsDocTitle + HHSConstants.DOT
						+ lsDocExt.toLowerCase());
			}
			int liCounter = HHSConstants.INT_ZERO;
			boolean lbFileCreated = false;
			do
			{
				if (!loFile.exists())
				{
					loFile.createNewFile();
					lbFileCreated = true;
				}
				else
				{
					loFile = new File(loFile.getParentFile(), MessageFormat.format(HHSConstants.MESSAGE_FORMAT,
							liCounter++, lsDocTitle) + HHSConstants.DOT + lsDocExt.toLowerCase());
					// renames the file in case same file name already
					// exists
				}
			}
			while (!lbFileCreated && loFile.exists());
			loOutputStream = new FileOutputStream(loFile);
			byte loBuffer[] = new byte[HHSConstants.INT_HUNDRED];
			int liLength;
			while ((liLength = loContent.read(loBuffer)) > HHSConstants.INT_ZERO)
			{
				loOutputStream.write(loBuffer, HHSConstants.INT_ZERO, liLength);
			}
			loOutputStream.close();
			loContent.close();
		}
		/**
		 * Any Exception from DAO class will be handles over here. It throws
		 * Application Exception back to Controllers calling method through
		 * Transaction framework
		 */
		catch (Exception aoExp)
		{
			ApplicationException aoAppExp = new ApplicationException("Error while saving dbd documents", aoExp);
			LOG_OBJECT.Error("Error while saving dbd documents :", aoAppExp);
			throw aoAppExp;
		}
		/**
		 * This block will be executed after completion of the execution of the
		 * method if any resource is not closed it will close
		 */
		finally
		{
			try
			{
				if (null != loOutputStream)
				{
					loOutputStream.close();
				}
				if (null != loContent)
				{
					loContent.close();
				}
			}
			// Handle and wrap IO exception if it is thrown while closing the
			// resources
			catch (IOException aoIoExp)
			{
				ApplicationException aoAppExp = new ApplicationException("Error while closing resources", aoIoExp);
				LOG_OBJECT.Error("Error while closing resources", aoAppExp);
				throw aoAppExp;
			}
		}
	}
}