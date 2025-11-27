package com.nyc.hhs.servlet;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.HHSUtil;

public class DownloadAllBatch extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
	private static final LogInfo LOG_OBJECT = new LogInfo(DownloadAllBatch.class);
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DownloadAllBatch()
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
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		try
		{
			HashMap<String, Object> loChanelOutputMap = null;
			String lsUserOrgType = (String) request.getSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE);
			if (null != lsUserOrgType && lsUserOrgType.equalsIgnoreCase(HHSR5Constants.CITY_ORG))
			{
				String lsRequestId = request.getParameter(HHSR5Constants.REQUEST_ID);
				String lsZipId = request.getParameter(HHSR5Constants.REQUESTED_ZIP_ID);
				Channel loChannel = new Channel();
				loChannel.setData(HHSR5Constants.REQUEST_ID, lsRequestId);
				loChannel.setData(HHSR5Constants.ZIP_ID, lsZipId);
				HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.TRANSACTION_GET_ZIP_PATH_FROM_DB,
						HHSR5Constants.TRANSACTION_ELEMENT_R5);
				loChanelOutputMap = (HashMap<String, Object>) loChannel.getData(HHSR5Constants.ZIP_DETAILS);
				if (null != loChanelOutputMap && !loChanelOutputMap.isEmpty())
				{
					Date loCreatedDate = null;
					loCreatedDate = DateUtil.getDate((String)loChanelOutputMap.get("CREATED_DATE"));
					double ldDiffDays = DateUtil.getDateDifference(loCreatedDate, new Date(System.currentTimeMillis()));
					String lsZipPath = (String) loChanelOutputMap.get("ZIP_PATH");
					if ((null != lsZipPath && !lsZipPath.isEmpty()) && ldDiffDays<=30 )
					{						
						String lspattern = Pattern.quote(File.separator);
						String[] losplittedFileName = lsZipPath.split(lspattern);
						File lofile = new File(lsZipPath);
						response.setContentType(HHSR5Constants.CONTENT_TYPE_ZIP);
						/** [Start] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
						/*response.setHeader(HHSR5Constants.BUSINESS_APPLICATION_SUB_SECTION_CONTENT_DISPOSITION,
								"attachment; filename=" + losplittedFileName[losplittedFileName.length - 1]
										+ HHSR5Constants.DELIMITER_SEMICOLON);*/
						response.setHeader(HHSR5Constants.BUSINESS_APPLICATION_SUB_SECTION_CONTENT_DISPOSITION,
								"attachment; filename=" + HHSUtil.sanitizeCarriageReturns(losplittedFileName[losplittedFileName.length - 1])
										+ HHSR5Constants.DELIMITER_SEMICOLON);
						/** [Start] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
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
						request.getSession().setAttribute(ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
								ApplicationConstants.DOCUMENT_EXCEPTION);
						request.getSession().setAttribute(ApplicationConstants.ERROR_MESSAGE,
								"This zip file is no longer available. Please regenerate the request for download.");
						request.getSession().setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
								ApplicationConstants.MESSAGE_FAIL_TYPE);
						String lsPath = request.getContextPath() +"/portal/hhsweb.portal?_nfpb=true&_pageLabel=enhanced_document_vault_page&_nfls=false&app_menu_name=header_document_vault&removeNavigator=true&headerClick=true";
						response.sendRedirect(lsPath);
					}
				}		
			}
			else
			{
				setInvalidAuthorizationMsg(request, response);
			}
		}
		catch (ApplicationException loex)
		{
			LOG_OBJECT.Error("Error Occured while ftching zip file detils",loex);
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error Occured while ftching zip file detils",aoExp);
		}
	}

	/** This method is called from doPost() of Servlet.
	 *  It sets the InValid Authorization Message to Servlet Request.
	 *  
	 * @param request - HttpServletRequest Object
	 * @param response - HttpServletResponse Object
	 * @throws IOException
	 */
	private void setInvalidAuthorizationMsg(HttpServletRequest request, HttpServletResponse response)
			throws ApplicationException
	{
		try
		{
		request.getSession().setAttribute(ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
				ApplicationConstants.DOCUMENT_EXCEPTION);
		request.getSession()
				.setAttribute(
						ApplicationConstants.ERROR_MESSAGE,
						"You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.");
		request.getSession().setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
				ApplicationConstants.MESSAGE_FAIL_TYPE);
		String lsPath = request.getContextPath() +"/portal/hhsweb.portal?_nfpb=true&_pageLabel=enhanced_document_vault_page&_nfls=false&app_menu_name=header_document_vault&removeNavigator=true&headerClick=true";
		response.sendRedirect(lsPath);
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error Occured while DownloadAllBatch.setInvalidAuthorizationMsg()",aoExp);
			throw new ApplicationException("Error Occured while DownloadAllBatch.setInvalidAuthorizationMsg()",aoExp);
		}
	}
}
