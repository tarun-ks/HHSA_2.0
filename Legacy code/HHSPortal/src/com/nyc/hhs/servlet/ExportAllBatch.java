package com.nyc.hhs.servlet;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;

public class ExportAllBatch extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
	private static final LogInfo LOG_OBJECT = new LogInfo(DownloadAllBatch.class);
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ExportAllBatch()
	{
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		this.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 *      This method is updated in R6 for Export Notifications. 
	 *      This method will also handle the Export Notifications Request.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException
	{

		try
		{
			LOG_OBJECT.Debug("Entered try block of doPost");
			HashMap<String, Object> loChanelOutputMap = null;
			String lsUserOrgType = (String) request.getSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE);
			String lsActionId = request.getParameter(HHSR5Constants.ACTION_ID);
			// R6 Change Starts: Added for Export Notifications
			if (null != lsActionId && lsActionId.equalsIgnoreCase(HHSR5Constants.EXPORT_NOTIFICATION))
			{
				String lsDirId = request.getParameter(HHSR5Constants.REQUEST_ID);
				Channel loChannel = new Channel();
				loChannel.setData(HHSR5Constants.REQUEST_ID, lsDirId);
				HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.FETCH_CREATED_DATE_FOR_EXPORT_TASK,
						HHSR5Constants.TRANSACTION_ELEMENT_R5);
				TaskDetailsBean loDetailsBean = (TaskDetailsBean) loChannel.getData(HHSR5Constants.FETCH_DATE);
				long ldDiffDays = DateUtil.getDateDifference((Date) loDetailsBean.getTaskRequestedDate(), new Date(
						System.currentTimeMillis()));
				if (ldDiffDays <= 30)
				{
					String lsFilePath = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
							HHSConstants.BULK_UPLOAD_ABSOLUTE_PATH);
					String lsFileName = request.getParameter(HHSConstants.BULK_UPLOAD_FILE_NAME);
					String lsPath = lsFilePath + lsFileName;
					if (loDetailsBean.getAgencyId().equalsIgnoreCase(
							(String) request.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID)))
					{
						downloadFile(response, lsPath, lsFileName);
					}
					else
					{
						LOG_OBJECT.Debug("Inside Nested Else block");
						request.getSession().setAttribute(ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
								ApplicationConstants.DOCUMENT_EXCEPTION);
						//R6: Message referenced from constants file now
						request.getSession().setAttribute(
								ApplicationConstants.ERROR_MESSAGE,
								PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGE_PROP_FILE,
										HHSR5Constants.UNAUTHORIZED_ACCESS));
						request.getSession().setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
								ApplicationConstants.MESSAGE_FAIL_TYPE);
						LOG_OBJECT.Debug("Inside Else block");

						String lsResponsePath = request.getContextPath()
								+ "/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_maintenanceagencysettings&agencysettingTab=bulkNotifications";
						response.sendRedirect(lsResponsePath);
					}
				}
				else
				{
					LOG_OBJECT.Debug("Inside Else block");
					request.getSession().setAttribute(ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
							ApplicationConstants.DOCUMENT_EXCEPTION);
					//R6: Message referenced from constants file now
					request.getSession().setAttribute(
							ApplicationConstants.ERROR_MESSAGE,
							PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGE_PROP_FILE,
									HHSR5Constants.DOWNLOAD_CSV_EXPIRED));
					request.getSession().setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
							ApplicationConstants.MESSAGE_FAIL_TYPE);
					String lsResponsePath = request.getContextPath()
							+ "/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_maintenanceagencysettings&agencysettingTab=bulkNotifications";
					response.sendRedirect(lsResponsePath);
				}
			}
			// R6 Change Ends:
			// Export Task
			else if (StringUtils.isNotBlank(lsUserOrgType)
					&& (lsUserOrgType.equalsIgnoreCase(HHSR5Constants.CITY_ORG) || lsUserOrgType
							.equalsIgnoreCase(ApplicationConstants.AGENCY_ORG)))
			{
				LOG_OBJECT.Debug("Entered if block of doPost method");
				String lsRequestId = request.getParameter(HHSR5Constants.REQUEST_ID);
				Channel loChannel = new Channel();
				loChannel.setData(HHSR5Constants.REQUEST_ID, lsRequestId);
				HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.TRANSACTION_GET_EXPORT_INFO_FROM_DB,
						HHSR5Constants.TRANSACTION_ELEMENT_R5);
				LOG_OBJECT.Debug("After executing transaction of doPost method");
				loChanelOutputMap = (HashMap<String, Object>) loChannel.getData(HHSR5Constants.ZIP_DETAILS);

				if (null != loChanelOutputMap && !loChanelOutputMap.isEmpty())
				{
					LOG_OBJECT.Debug("Inside IF block");
					Date loCreatedDate = DateUtil.getDate(loChanelOutputMap.get("CREATED_DATE").toString());

					SimpleDateFormat loDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
					loCreatedDate = loDateFormat.parse(loChanelOutputMap.get("CREATED_DATE").toString());
					String lsSubFileName = request.getParameter("lsSubFileName");
					DateFormat dateFormat2 = new SimpleDateFormat("MMddyyyy-hhmmss");
					String lsFileName = HHSR5Constants.TASK_EXPORT_FILE_NAME
							+ HHSR5Constants.UNDERSCORE
							+ lsSubFileName
							+ HHSR5Constants.UNDERSCORE
							+ dateFormat2
									.format(loDateFormat.parse((loChanelOutputMap.get("CREATED_DATE").toString())))
							+ HHSR5Constants.CSV_CONSTANT;
					String lsFilePath = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
							"TASK_EXPORT_FILE_PATH") + File.separator + lsRequestId + File.separator + lsFileName;

					long ldDiffDays = DateUtil.getDateDifference(loCreatedDate, new Date(System.currentTimeMillis()));

					if ((null != lsFilePath && !lsFilePath.isEmpty()) || ldDiffDays <= 30)
					{
						LOG_OBJECT.Debug("Inside Nested IF block");
						String lspattern = Pattern.quote(File.separator);
						String[] losplittedFileName = lsFilePath.split(lspattern);
						File lofile = new File(lsFilePath);
						response.setContentType(HHSR5Constants.CONTENT_TYPE_ZIP);
						/** [Start] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
						/*response.setHeader(HHSR5Constants.BUSINESS_APPLICATION_SUB_SECTION_CONTENT_DISPOSITION,
								"attachment; filename=" + losplittedFileName[losplittedFileName.length - 1]
										+ HHSR5Constants.DELIMITER_SEMICOLON);*/
						response.setHeader(HHSR5Constants.BUSINESS_APPLICATION_SUB_SECTION_CONTENT_DISPOSITION,
								"attachment; filename=" + HHSUtil.sanitizeCarriageReturns(losplittedFileName[losplittedFileName.length - 1])
										+ HHSR5Constants.DELIMITER_SEMICOLON);
						/** [End] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
						response.setHeader(HHSR5Constants.CACHE_CONTROL, HHSR5Constants.NO_CACHE);
						byte[] lobuffer = new byte[response.getBufferSize()];
						response.setContentLength((int) lofile.length());
						int liLength;
						BufferedInputStream loFileInBuf = null;
						loFileInBuf = new BufferedInputStream(new FileInputStream(lofile));
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						while ((liLength = loFileInBuf.read(lobuffer)) > 0)
						{
							baos.write(lobuffer, 0, liLength);
						}
						response.getOutputStream().write(baos.toByteArray());
						response.getOutputStream().flush();
						response.getOutputStream().close();
					}
					else
					{
						LOG_OBJECT.Debug("Inside Nested Else block");
						request.getSession().setAttribute(ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
								ApplicationConstants.DOCUMENT_EXCEPTION);
						//R6: Message referenced from constants file now
						request.getSession().setAttribute(
								ApplicationConstants.ERROR_MESSAGE,
								PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGE_PROP_FILE,
										HHSR5Constants.ZIP_NOT_AVAILABLE));
						request.getSession().setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
								ApplicationConstants.MESSAGE_FAIL_TYPE);
						response.setContentType("text/html");
						response.setCharacterEncoding("UTF-8");
						PrintWriter outer = response.getWriter();
						outer.println("<span class='error'>This zip file is no longer available. Please regenerate the request for download.</span>");
						outer.close();
						LOG_OBJECT.Debug("End if else block");
					}
				}
				else
				{
					LOG_OBJECT.Debug("Inside Else block");
					request.getSession().setAttribute(ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
							ApplicationConstants.DOCUMENT_EXCEPTION);
					//R6: Message referenced from constants file now
					request.getSession().setAttribute(
							ApplicationConstants.ERROR_MESSAGE,
							PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGE_PROP_FILE,
									HHSR5Constants.UNAUTHORIZED_ACCESS));
					request.getSession().setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
							ApplicationConstants.MESSAGE_FAIL_TYPE);
					LOG_OBJECT.Debug("Inside Else block");
					response.setContentType("text/html");
					response.setCharacterEncoding("UTF-8");
					PrintWriter out = response.getWriter();
					out.println("<span class='error'>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</span>");
					out.close();
				}
			}
		}
		catch (ApplicationException loex)
		{
			LOG_OBJECT.Error("Error Occured while ftching zip file detils", loex);
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error Occured while ftching zip file detils", aoExp);
		}
	}

	/**
	 * This method is added in R6. This method will download file from the path
	 * specified.
	 * @param response
	 * @param asFilePath
	 * @param asFileName
	 * @throws ServletException
	 * @throws IOException
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	private void downloadFile(HttpServletResponse asResponse, String asFilePath, String asFileName)
			throws ServletException, IOException, Exception
	{
		BufferedInputStream loFileInBuf = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try
		{
			StringBuffer loStringBuffer = new StringBuffer(asFileName);
			asResponse.setContentType(HHSR5Constants.TEXT_CSV);
			String disposition = "attachment; fileName="
					+ loStringBuffer.toString().split(HHSR5Constants.DELIMETER_SIGN)[1].trim();
			/** [Start] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
			//asResponse.setHeader(HHSR5Constants.BUSINESS_APPLICATION_SUB_SECTION_CONTENT_DISPOSITION, disposition);
			asResponse.setHeader(HHSR5Constants.BUSINESS_APPLICATION_SUB_SECTION_CONTENT_DISPOSITION, HHSUtil.sanitizeCarriageReturns(disposition));
			/** [End] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
			File lofile = new File(asFilePath);
			byte[] lobuffer = new byte[asResponse.getBufferSize()];
			asResponse.setContentLength((int) lofile.length());
			int liLength;
			loFileInBuf = new BufferedInputStream(new FileInputStream(lofile));
			while ((liLength = loFileInBuf.read(lobuffer)) > 0)
			{
				baos.write(lobuffer, 0, liLength);
			}
			asResponse.getOutputStream().write(baos.toByteArray());
		}
		catch (IOException loInputEx)
		{
			LOG_OBJECT.Error("Error Occured while ftching CSV file detils", loInputEx);
			throw loInputEx;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error Occured while ftching CSV file detils", loExp);
			throw loExp;
		}
		finally
		{
			asResponse.getOutputStream().flush();
			asResponse.getOutputStream().close();
		}
	}
}
