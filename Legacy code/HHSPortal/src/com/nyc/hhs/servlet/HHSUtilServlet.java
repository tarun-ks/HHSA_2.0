package com.nyc.hhs.servlet;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.record.cf.PatternFormatting;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
//import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jdom.Element;
import org.springframework.util.CollectionUtils;
import com.filenet.apiimpl.property.ClientInputStream;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.controllers.util.BaseControllerUtil;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.AgencyTaskBean;
import com.nyc.hhs.model.ContractDetails;
import com.nyc.hhs.model.Document;
import com.nyc.hhs.model.ExtendedDocument;
import com.nyc.hhs.model.NYCAgency;
import com.nyc.hhs.model.ProposalReportBean;
import com.nyc.hhs.model.ReportBean;
import com.nyc.hhs.model.StaffDetails;
import com.nyc.hhs.model.UserBean;
import com.nyc.hhs.model.UserThreadLocal;
import com.nyc.hhs.report.framework.utils.HHSReportUtil;
import com.nyc.hhs.report.framework.utils.ReportHandler;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.BusinessApplicationUtil;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.FileNetOperationsUtils;
import com.nyc.hhs.util.HHSPortalUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PortalUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * Servlet implementation class.<br>
 * This class will handle ajax call requested by user from jsp to populate data
 * for various fields on jsp.
 * 
 */
public class HHSUtilServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
	private static final LogInfo LOG_OBJECT = new LogInfo(HHSUtilServlet.class);
	private static final long serialVersionUID = 1L;
	private static final String SINGLE_QUOTE = "\'";
	private static final String DQUOTES_COMMA = "\",";
	private static final String DOUBLE_QUOTES = "\"";
	private static final String SQUARE_BRAC_BEGIN = "[";
	private static final String COMMA = ",";
	private String msDocName = "";

	public HHSUtilServlet()
	{
		super();
	}

	/**
	 * This method handle the get request of a servlet. This will internally
	 * call doPost method to process the servlet request and return the
	 * response.
	 * 
	 * @param aoRequest HttpServlet request object
	 * @param aoResponse HttpServlet response object
	 * @throws ServletException If an Servlet Exception occurs
	 * @throws IOException If an Input Output Exception occurs
	 */
	@Override
	protected void doGet(HttpServletRequest aoRequest, HttpServletResponse aoResponse) throws ServletException,
			IOException
	{
		this.doPost(aoRequest, aoResponse);
	}

	/**
	 * This method handle the post request of a servlet. When a action is
	 * initiated from a jsp, it process the action by calling multiple
	 * transactions to the end. Updated for R4: Adding Organization ID to add
	 * join on STAFF_ORGANIZATION table
	 * @param aoRequest HttpServlet request object
	 * @param aoResponse HttpServlet response object
	 * @throws ServletException If an Servlet Exception occurs
	 * @throws IOException If an Input Output Exception occurs
	 */
	@SuppressWarnings({ "unchecked", "resource" })
	@Override
	protected void doPost(HttpServletRequest aoRequest, HttpServletResponse aoResponse) throws ServletException,
			IOException
	{
		InputStream loInpOut = null;
		Channel loChannel = new Channel();
		String lsUserId = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID);
		UserThreadLocal.setUser(lsUserId);
		
		// Added in R5
		HttpSession loSession = aoRequest.getSession();
		P8UserSession loUserSession = (P8UserSession) loSession
				.getAttribute(ApplicationConstants.FILENET_SESSION_OBJECT);
		
		/**[Start]R9.3.2 QC9664  get user info in HTTP session    */
		String lsOrganizType = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE);
		String lsOrganizId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG);
		
		String lsPermissionLevel = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_PERMISSION_LEVEL);
		String lsPermissionType = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_PERMISSION_TYPE);

		LOG_OBJECT.Info( "######[Teace] User info : lsUserId-" +   lsUserId +
				"       lsOrganizId:" +  lsOrganizId +  "     lsOrganizType" +  lsOrganizType + "\tlsPermissionLevel" + lsPermissionLevel
				+ "\tlsPermissionType" + lsPermissionType);
		/**[End]R9.3.2 QC9664  get user info in HTTP session    */
		try
		{   			
			String lsLockingFlag = aoRequest.getParameter(HHSR5Constants.LOCKING_FLAG);
			String lsFilter = aoRequest.getParameter(HHSR5Constants.IS_FILTER);
			String lsProcurementTitle = aoRequest.getParameter(HHSR5Constants.IS_PROCUREMENT_TITLE);
			String lsAwardePinTitle = aoRequest.getParameter(HHSR5Constants.IS_AWARD_PIN_TITLE);
			String lsContractAwardEPinTitle = aoRequest.getParameter(HHSR5Constants.CONTRACT_AWARD_E_PIN_TITLE);
			String lsOrganizationId = aoRequest.getParameter(HHSConstants.ORGANIZATION_ID);
			String lsSelectedCategory = aoRequest.getParameter(HHSR5Constants.SELECTED_INPUT);
			String lsSharedWith = aoRequest.getParameter(HHSR5Constants.IS_SHARED_WITH);
			String lsAmendEpinSerach = aoRequest.getParameter(HHSR5Constants.GET_AMENDMENT_EPIN);
			String lsInvoiceNumber = aoRequest.getParameter(HHSConstants.GET_INVOICE_DETAILS);
			
			/**[Start]R9.3.2 QC9664  get user info in HTTP session    */
			LOG_OBJECT.Info( "######[Teace]0 User info : Doc Id-" +   aoRequest.getParameter(HHSR5Constants.GET_DOCUMENT) +
					"       lsLockingFlag:" +  lsLockingFlag +  "     lsFilter" +  lsFilter  +
					"       lsProcurementTitle:" +  lsProcurementTitle +  "     lsAwardePinTitle" +  lsAwardePinTitle  +
					"       lsContractAwardEPinTitle:" +  lsContractAwardEPinTitle +  "     lsOrganizationId" +  lsOrganizationId  +
					"       lsSelectedCategory:" +  lsSelectedCategory +  "     lsSharedWith" +  lsSharedWith  +
					"       lsAmendEpinSerach:" +  lsAmendEpinSerach +  "     lsInvoiceNumber" +  lsInvoiceNumber
					);
			/**[End]R9.3.2 QC9664  get user info in HTTP session    */
			// Ends R5
			if (null != lsSelectedCategory && !lsSelectedCategory.equals(HHSR5Constants.EMPTY_STRING))
			{
				LOG_OBJECT.Debug( "######[Teace]1 User info :   "     );
				getDocTypeForSelectedCategory(aoRequest, aoResponse, lsOrganizationId, lsSelectedCategory);
			}
			if (null != aoRequest.getParameter(HHSConstants.ORGANIZATION_LEGAL_NAME))
			{
				LOG_OBJECT.Debug( "######[Teace]2 User info :   "  );
				getDocumentDetailsByProviderName(loUserSession, loSession, aoRequest, aoResponse);
			}
			if ("samplecategory".equalsIgnoreCase(aoRequest.getParameter(ApplicationConstants.CATEGORY_NODE)))
			{
				LOG_OBJECT.Debug( "######[Teace]3 User info :   "  );
				getSampleCategoryAndType(aoRequest, aoResponse);
			}
			if (null != lsFilter && !lsFilter.equals(HHSR5Constants.EMPTY_STRING))
			{
				LOG_OBJECT.Debug( "######[Teace]4 User info :   "  );
				getDocType(aoRequest, aoResponse);
			}
			if (null != lsProcurementTitle && !lsProcurementTitle.equals(HHSR5Constants.EMPTY_STRING))
			{
				LOG_OBJECT.Debug( "######[Teace]5 User info :   "  );
				getProcurementTitle(aoRequest, aoResponse);
			}
			if (null != lsInvoiceNumber && !lsInvoiceNumber.equals(HHSR5Constants.EMPTY_STRING))
			{
				LOG_OBJECT.Debug( "######[Teace]6 User info :   "  );
				getInvoiceNumber(aoRequest, aoResponse);
			}
			if (null != lsAwardePinTitle && !lsAwardePinTitle.equals(HHSR5Constants.EMPTY_STRING))
			{
				LOG_OBJECT.Debug( "######[Teace]7 User info :   "  );
				getAwardPin(aoRequest, aoResponse);
			}
			if (null != lsContractAwardEPinTitle && !lsContractAwardEPinTitle.equals(HHSR5Constants.EMPTY_STRING))
			{
				LOG_OBJECT.Debug( "######[Teace]8 User info :   "  );
				getContractAwardEPin(aoRequest, aoResponse);
			}
			if (null != lsSharedWith && !lsSharedWith.equals(HHSR5Constants.EMPTY_STRING))
			{
				LOG_OBJECT.Debug( "######[Teace]9 User info :   "  );
				getSharedWith(aoRequest, aoResponse);
			}
			if (null != lsAmendEpinSerach && !lsAmendEpinSerach.isEmpty())
			{
				LOG_OBJECT.Debug( "######[Teace]10 User info :   "  );
				getAmendmentEpin(aoRequest, aoResponse);
			}
			if (null != aoRequest.getParameter(HHSR5Constants.GET_DOCUMENT))
			{   
				LOG_OBJECT.Debug( "######[Teace]11 User info :   "  );
				String lsDocumentId = aoRequest.getParameter(HHSR5Constants.GET_DOCUMENT);
				
				/*[Start] R9.3.2  QC9665*/
				PrintWriter loPrintWriter = null;
				if( lsDocumentId != null     ){
					if( !checkPermission(aoRequest, aoResponse, lsDocumentId)  ){
						loPrintWriter = aoResponse.getWriter();
						loPrintWriter.write("The File cannot be accessble");
						loPrintWriter.flush();
						loPrintWriter.close();
						
						return;
					}
					//LOG_OBJECT.Debug( "######[Teace]13 sub---- User info :  checkPermission   " + checkPermission(aoRequest, aoResponse, lsDocumentId) );
				}
				/*[End] R9.3.2 QC9665 */

				HashMap loOutputHashMap = actionDisplayDocument(aoRequest, aoResponse, lsDocumentId);
				String lsMimeType = (String) loOutputHashMap.get(HHSR5Constants.MIME_TYPE1);
				loInpOut = (InputStream) loOutputHashMap.get(HHSR5Constants.CONTENT_ELEMENT);
				/** [Start] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
				//aoResponse.setContentType(lsMimeType);
				aoResponse.setContentType(HHSUtil.sanitizeCarriageReturns(lsMimeType));
				String lsFileType = (String) loOutputHashMap.get(HHSConstants.FILE_TYPE);
				aoResponse.setHeader("Content-Disposition",
						"attachment;FileName=" +HHSUtil.sanitizeCarriageReturns( msDocName + "." + lsFileType.toLowerCase() ));
				/** [End] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
				
				byte[] lbByteArray = new byte[100];
				int liBytesRead = 0;

				while ((liBytesRead = loInpOut.read(lbByteArray)) != -1)
				{
					aoResponse.getOutputStream().write(lbByteArray, 0, liBytesRead);
				}
				aoResponse.getOutputStream().close();

				return;
			}
			// R5 change starts
			if ("checkDocExits".equalsIgnoreCase(aoRequest.getParameter(ApplicationConstants.RENDER_ACTION)))
			{   
				LOG_OBJECT.Debug( "######[Teace]12 User info :   "  );
				String lsDocumentId = aoRequest.getParameter(HHSR5Constants.DOC_ID);
				/*[Start] R9.3.2  QC9665*/

				PrintWriter loPrintWriter = null;
				if( lsDocumentId != null     ){
					if( !checkPermission(aoRequest, aoResponse, lsDocumentId)  ){
						LOG_OBJECT.Info( "######[Teace]12 User info : The File cannot be accessble  "  );
						loPrintWriter = aoResponse.getWriter();
						loPrintWriter.write("The File cannot be accessble");
						loPrintWriter.flush();
						loPrintWriter.close();
					}
					//LOG_OBJECT.Debug( "######[Teace]13 sub---- User info :  checkPermission   " + checkPermission(aoRequest, aoResponse, lsDocumentId) );
				}

				checkDocumentExists(aoRequest, aoResponse);
			}
			if ("displayDocument".equalsIgnoreCase(aoRequest.getParameter(ApplicationConstants.RENDER_ACTION))
					|| (null != aoRequest.getParameter(HHSR5Constants.DOWNLOAD_PDF) && HHSR5Constants.DOWNLOAD_PDF
							.equalsIgnoreCase(aoRequest.getParameter(HHSR5Constants.DOWNLOAD_PDF))))
			{
				LOG_OBJECT.Debug( "######[Teace]13 User info :   "  );

				/*[Start] R9.3.2  QC9665*/
				PrintWriter loPrintWriter = null;
				String lsDocumentId = aoRequest.getParameter(HHSR5Constants.DOC_ID);
				if( lsDocumentId != null     ){
					if( checkPermission(aoRequest, aoResponse, lsDocumentId)  ){
						loInpOut = getDocumentContent(aoRequest, aoResponse);
					} else {
						LOG_OBJECT.Info( "######[Teace]13 User info : The File cannot be accessble  "  );
						loPrintWriter = aoResponse.getWriter();
						loPrintWriter.write("The File cannot be accessble");
						loPrintWriter.flush();
						loPrintWriter.close();
					}
					//LOG_OBJECT.Debug( "######[Teace]13 sub---- User info :  checkPermission   " + checkPermission(aoRequest, aoResponse, lsDocumentId) );
				}
				/*[End] R9.3.2 QC9665 */
			}

			if (null != lsLockingFlag && !lsLockingFlag.isEmpty() && lsLockingFlag.equalsIgnoreCase("true"))
			{  
				LOG_OBJECT.Debug( "######[Teace]14 lsLockingFlag   :   "  );
				PrintWriter loPrntWrter = aoResponse.getWriter();
				HashMap<String, String> loLockIdMap = new HashMap<String, String>();
				Boolean lbLockStatus = false;
				if (null != aoRequest.getParameter("checkedDocumentId")
						&& !aoRequest.getParameter("checkedDocumentId").isEmpty())
				{
                  	loLockIdMap.put(aoRequest.getParameter("checkedDocumentId"),
					aoRequest.getParameter("checkedDocumentType"));
    				LOG_OBJECT.Debug( "######[Teace]14 signle file   :   "  );
					/*[Start] R9.3.2  QC9665*/
					if( !checkPermission(aoRequest, aoResponse, aoRequest.getParameter("checkedDocumentId"))  ){
						loPrntWrter = aoResponse.getWriter();
						loPrntWrter.write("File cannot be accessable!");
						loPrntWrter.flush();
						loPrntWrter.close();
						return;
					}
					/*[End] R9.3.2 QC9665 */

				}
				else
				{
    				LOG_OBJECT.Debug( "######[Teace]14 multi file   :   "  );
					Map<String, String[]> loMap = (Map<String, String[]>) aoRequest.getParameterMap();
					String[] loChekedItems = loMap.get(ApplicationConstants.CHECKED_OBJECT);
					if (null != loChekedItems && loChekedItems.length > 0)
					{
						for (int i = 0; i < loChekedItems.length; i++)
						{
							Document loDocument = new Document();
							String lsCheckValue = loChekedItems[i];
							String[] loTempArray = lsCheckValue.split(HHSR5Constants.COMMA);
							loDocument.setDocumentId(loTempArray[0]);
							loDocument.setDocType(loTempArray[1]);
							loLockIdMap.put(loDocument.getDocumentId(), loDocument.getDocType());
							
							/*[Start] R9.3.2  QC9665*/
							if( !checkPermission(aoRequest, aoResponse, loDocument.getDocumentId())  ){
								loPrntWrter = aoResponse.getWriter();
								loPrntWrter.write("File cannot be accessable!");
								loPrntWrter.flush();
								loPrntWrter.close();
								return;
							}
							/*[End] R9.3.2 QC9665 */
							
							LOG_OBJECT.Info("====loDocument.getDocType() :: "+loDocument.getDocType());
						}
					}
				}
				Channel loChannelLock = new Channel();
				String lsAction = aoRequest.getParameter("nextAction");
				loChannelLock.setData("documentIdMap", loLockIdMap);
				List<String> loLockIdList = new ArrayList<String>();
				loChannelLock.setData("aoFilenetSession", loUserSession);
				loChannelLock.setData("next_action", lsAction);
				HHSTransactionManager.executeTransaction(loChannelLock, "getFolderPathFromFilenet",
						HHSR5Constants.TRANSACTION_ELEMENT_R5);
				HashMap<String, List<String>> loLockingMap = (HashMap) loChannelLock.getData("loPathMap");
				for (Map.Entry<String, List<String>> entry : loLockingMap.entrySet())
				{
					List<String> loList = entry.getValue();
					for (Iterator iterator = loList.iterator(); iterator.hasNext();)
					{
						String lsLockPath = (String) iterator.next();
						loLockIdList.add(lsLockPath);
					}
				}
				String lsUserOrg = (String) aoRequest.getSession().getAttribute(
						ApplicationConstants.KEY_SESSION_USER_ORG);
				String lsUserOrgType = (String) aoRequest.getSession().getAttribute(
						ApplicationConstants.KEY_SESSION_ORG_TYPE);
				if (null != lsUserOrgType && lsUserOrgType.equalsIgnoreCase(HHSR5Constants.USER_CITY))
				{
					lsUserOrg = lsUserOrgType;
				}
				if (loLockIdList.isEmpty() && null != aoRequest.getParameter("checkedDocumentId")
						&& !aoRequest.getParameter("checkedDocumentId").isEmpty()
						&& aoRequest.getParameter("checkedDocumentId").equalsIgnoreCase(HHSR5Constants.RECYCLE_BIN_ID))
				{
					loLockIdList.add(FileNetOperationsUtils.setFolderPath(lsUserOrgType, lsUserOrg,
							HHSR5Constants.RECYCLE_BIN));
				}

				if (null != aoRequest.getParameter("restoreFlag")
						&& !aoRequest.getParameter("restoreFlag").equalsIgnoreCase(HHSR5Constants.EMPTY_STRING)
						&& !aoRequest.getParameter("restoreFlag").equalsIgnoreCase("null")
						&& aoRequest.getParameter("restoreFlag").equalsIgnoreCase("true"))
				{
					String lsRestoreMessageFlag = "true";
					List<String> loLockedByList = FileNetOperationsUtils.getLockDetails(loLockIdList, lsUserOrg);
					aoResponse.setContentType(HHSR5Constants.APPLICATION_JSON);

					if (loLockedByList.size() > 0)
					{
						if (FileNetOperationsUtils.checkIfLockedByRecycleBin(loLockedByList))
						{
							lsRestoreMessageFlag = "false";
						}
						String lsJson = "{\"lockingFlag\": \"true\",\"RecyclelockingFlag\": \"" + lsRestoreMessageFlag
								+ "\"}";
						loPrntWrter.print(lsJson);
					}
					else
					{
						String lsJson = "{\"lockingFlag\": \"false\",\"RecyclelockingFlag\": \"" + lsRestoreMessageFlag
								+ "\"}";
						loPrntWrter.print(lsJson);
					}

				}
				else
				{
					lbLockStatus = FileNetOperationsUtils.checkLock(loLockIdList, lsUserOrg);
					loPrntWrter.write(lbLockStatus.toString());
				}

				loPrntWrter.flush();
			}
			// R5 change ends
			if ("displayAppendix".equalsIgnoreCase(aoRequest.getParameter("action")))
			{
				LOG_OBJECT.Debug( "######[Teace]15 displayAppendix   :   "  );
				displayAppendixDocContent(aoRequest, aoResponse, loChannel, loUserSession);
			}

			// Added below if condition for Export Task List for Agency Users

			if (null != aoRequest.getParameter(HHSR5Constants.AGENCY_EXPORT_LIST)
					&& !aoRequest.getParameter(HHSR5Constants.AGENCY_EXPORT_LIST).isEmpty())
			{
				LOG_OBJECT.Debug( "######[Teace]16 displayAppendix   :   "  );
				String lsUserRole = (String) aoRequest.getSession().getAttribute(
						ApplicationConstants.KEY_SESSION_USER_ROLE);
				String lsUserOrg = (String) aoRequest.getSession().getAttribute(
						ApplicationConstants.KEY_SESSION_USER_ORG);
				String lsUserOrgType = (String) aoRequest.getSession().getAttribute(
						ApplicationConstants.KEY_SESSION_ORG_TYPE);
				HashMap loDefaultMap = new HashMap();
				// setting default parameters in default map
				loDefaultMap.put("statusId", PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSR5Constants.EXPORT_ALL_TASK_REQUESTED));
				loDefaultMap.put("orgType", lsUserOrgType);
				loDefaultMap.put("userRRole", lsUserRole);
				loDefaultMap.put("userId", lsUserId);
				loDefaultMap.put("userOrgType", lsUserOrg);
				Channel loChannelObj = new Channel();
				loChannelObj.setData("loDefaultMap", loDefaultMap);
				TransactionManager.executeTransaction(loChannelObj, HHSR5Constants.EXPORT_ALL_TASK,
						HHSR5Constants.TRANSACTION_ELEMENT_R5);
			}

			// End R5 changes
			else if (null != aoRequest.getParameter("documentID"))
			{   
				LOG_OBJECT.Debug( "######[Teace]17 displayAppendix   :   "  );
				getRequestDispatcher(aoRequest, aoResponse, loUserSession);
			}
			if (null != aoRequest.getParameter("iFrameClick")
					&& aoRequest.getParameter("iFrameClick").equalsIgnoreCase("iFrameClickContract"))
			{
				LOG_OBJECT.Debug( "######[Teace]18 displayAppendix   :   "  );
				getAddedServiceAndDeletedServiceNameForContract(aoRequest, aoResponse, loChannel, lsOrganizationId);
			}
			if (null != aoRequest.getParameter("iFrameClick")
					&& aoRequest.getParameter("iFrameClick").equalsIgnoreCase("iFrameClickStaff"))
			{
				LOG_OBJECT.Debug( "######[Teace]19 displayAppendix   :   "  );
				getAddedServiceAndDeletedServiceNameForStaff(aoRequest, aoResponse, loChannel, lsOrganizationId);
			}
			if (null != aoRequest.getParameter("downloadZip")
					&& "downloadZip".equalsIgnoreCase(aoRequest.getParameter("downloadZip")))
			{   
				LOG_OBJECT.Debug( "######[Teace]20 displayAppendix   :   "  );
				loInpOut = getZipDocumentContent(aoRequest, aoResponse);
			}
			// Added in R5
			if (null != aoRequest.getParameter("actionParam")
					&& "exportDetailReport".equalsIgnoreCase(aoRequest.getParameter("actionParam")))
			{    
				LOG_OBJECT.Debug( "######[Teace]21 displayAppendix   :   "  );
				exportData(aoRequest, aoResponse);
			}
			// R5 changes Ends
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception occurred while displaying document:", aoExp);
			String lsErrorMsg = aoExp.toString();
			lsErrorMsg = lsErrorMsg.substring(lsErrorMsg.lastIndexOf(":") + 1, lsErrorMsg.length()).trim();
			aoRequest.getSession().setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoRequest.getSession().setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
		finally
		{
			if (null != loInpOut)
			{   
				loInpOut.close();
			}
		}
		// Start : R5 Added
		if (StringUtils.isNotBlank(aoRequest.getParameter(HHSR5Constants.ALERT_ACTION))
				&& HHSR5Constants.ALERT_DATA.equalsIgnoreCase(aoRequest.getParameter(HHSR5Constants.ALERT_ACTION)))
		{
			try
			{   
				String lsOrgType = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE);
				String lsOrgId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG);
				HashMap<String, String> loParamHashMap = new HashMap<String, String>();
				loParamHashMap.put(HHSR5Constants.AS_ORG_TYPE, lsOrgType);
				loParamHashMap.put(HHSR5Constants.AS_ORG_ID, lsOrgId);
				loParamHashMap.put(HHSR5Constants.AS_USER_ID, lsUserId);
				loChannel.setData(HHSR5Constants.AO_INPUT_PARAMS, loParamHashMap);
				TransactionManager.executeTransaction(loChannel, HHSR5Constants.GET_ALERT_BOX_UN_READ_DATA,
						HHSR5Constants.TRANSACTION_ELEMENT_R5);
				loSession.setAttribute(HHSR5Constants.ALERT_UPDATE_TIME, new Timestamp(new Date().getTime()));
				String lsAlertCount = (String) loChannel.getData(HHSR5Constants.ALERT_COUNT);
				loSession.setAttribute(HHSR5Constants.ALERT_BOX_UN_READ_DATA, lsAlertCount);
				PrintWriter loPrntWrter = aoResponse.getWriter();
				loPrntWrter.write(lsAlertCount);
				loPrntWrter.flush();
			}
			catch (ApplicationException loExp)
			{
				LOG_OBJECT.Error("Exception occurred while displaying alert count:", loExp);
			}
		}
		if (StringUtils.isNotBlank(HHSR5Constants.ALERT_ACTION)
				&& HHSR5Constants.DICTIONARY_DATA.equalsIgnoreCase(aoRequest.getParameter(HHSR5Constants.ALERT_ACTION)))
		{    
			String lsWordToDictionary = aoRequest.getParameter(HHSR5Constants.ADD_WORD_TO_DICTIONARY);
			PrintWriter loPrntWrter = aoResponse.getWriter();
			if (StringUtils.isNotBlank(lsWordToDictionary))
			{   
				Channel loChannelObj = new Channel();
				String lsUpdatedDicstionaryData;
				try
				{   
					lsUpdatedDicstionaryData = updateDictionaryData((String) BaseCacheManagerWeb.getInstance()
							.getCacheObject(HHSR5Constants.DICTIONARY_DATA), lsWordToDictionary);
					loChannelObj.setData(HHSConstants.AO_USER_SESSION, lsUserId);
					loChannelObj.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
					loChannelObj.setData(HHSConstants.BULK_UPLOAD_INPUT_STREAM,
							HHSUtil.convertXmlToStream(lsUpdatedDicstionaryData));
					loChannelObj.setData(HHSConstants.AS_DOC_TYPE, HHSR5Constants.DICTIONARY);
					HHSTransactionManager.executeTransaction(loChannelObj, HHSR5Constants.UPDATE_DICTIONARY_DATA,
							HHSR5Constants.TRANSACTION_ELEMENT_R5);
					BaseCacheManagerWeb.getInstance().putCacheObject(HHSR5Constants.DICTIONARY_DATA,
							lsUpdatedDicstionaryData);
					loPrntWrter.write((String) lsUpdatedDicstionaryData);
				}
				catch (ApplicationException aoExp)
				{
					LOG_OBJECT.Error("ApplicationException occured while Add word to Dictionary.", aoExp);
				}
			}
			else
			{
				try
				{   
					StringWriter loWriter = new StringWriter();
					Channel loChannelObj = new Channel();
					loChannelObj.setData(HHSConstants.AO_USER_SESSION, loUserSession);
					loChannelObj.setData(HHSConstants.AS_DOC_TYPE, HHSR5Constants.DICTIONARY);
					HHSTransactionManager.executeTransaction(loChannelObj, HHSR5Constants.GET_DICTIONARY_DATA,
							HHSR5Constants.TRANSACTION_ELEMENT_R5);
					@SuppressWarnings("rawtypes")
					HashMap lsData = (HashMap) loChannelObj.getData(HHSConstants.CONTENT_BY_TYPE);
					IOUtils.copy((InputStream) lsData.get(lsData.keySet().iterator().next()), loWriter,
							HHSR5Constants.UTF_8);
					BaseCacheManagerWeb.getInstance().putCacheObject(HHSR5Constants.DICTIONARY_DATA,
							loWriter.toString());
					loPrntWrter.write((String) BaseCacheManagerWeb.getInstance().getCacheObject(
							HHSR5Constants.DICTIONARY_DATA));
				}
				catch (IOException loexp)
				{
					LOG_OBJECT.Error("IOException occured while getting DictionaryData.", loexp);
				}
				catch (ApplicationException aoExp)
				{
					LOG_OBJECT.Error("ApplicationException occured while getting DictionaryData.", aoExp);
				}
				
			}
			loPrntWrter.flush();
		}

		// Start : Added in Release 6
		if (StringUtils.isNotBlank(HHSR5Constants.ALERT_ACTION)
				&& HHSR5Constants.DOWNLOAD_CSV.equalsIgnoreCase(aoRequest.getParameter(HHSR5Constants.ALERT_ACTION)))
		{   
			OutputStream lsOutputStream = aoResponse.getOutputStream();
			try
			{   
				aoResponse.setContentType("text/csv");
				/** [Start] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
				/*aoResponse.setHeader("Content-Disposition",
						"attachment; filename=\""
								+ getCsvFileName(aoRequest.getParameter(HHSR5Constants.CSV_FILE_NAME)) + "\"");*/
				String csvFileName=HHSUtil.sanitizeCarriageReturns(aoRequest.getParameter(HHSR5Constants.CSV_FILE_NAME));
				aoResponse.setHeader("Content-Disposition",
						"attachment; filename=\""
								+ HHSUtil.sanitizeCarriageReturns(getCsvFileName(csvFileName)) + "\"");
				/** [End] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
				String outputResult = aoRequest.getParameter(HHSR5Constants.CSV_ROW_DATA);
				lsOutputStream.write(outputResult.getBytes());
			}
			catch (Exception aoExp)
			{
				LOG_OBJECT.Error("ApplicationException occured while getting downloading CSV.", aoExp);
			}
			finally
			{
				if (null != lsOutputStream)
				{
					lsOutputStream.flush();
					lsOutputStream.close();
				}
			}
		}
		// End : Added in Release 6
		
		//Start QC 9401 R 8.5.0 generate RGP Report XML file
		
		if ("getRfpReportXsls".equalsIgnoreCase(aoRequest.getParameter(ApplicationConstants.RENDER_ACTION)))
		{  
			try {
				getRfpReport(aoRequest, aoResponse);
				
			}catch (IOException loexp){
					LOG_OBJECT.Error("IOException occured while getting getRfpReportXsls.", loexp);
			} catch (ApplicationException aoExp){
					LOG_OBJECT.Error("ApplicationException occured while getting getRfpReportXsls.", aoExp);
				}
			
		}
		//End QC 9401 R 8.5.0 generate RGP Report XML file
		
		UserThreadLocal.unSet();
	}

	/**
	 * This method will return for Export filename for PS Enhancement. Added in
	 * Release 6
	 * @param aoFileName
	 * @return loCsvName
	 */
	private String getCsvFileName(String aoFileName)
	{
		StringBuffer loCsvName = new StringBuffer(aoFileName);
		SimpleDateFormat moDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		loCsvName.append(moDateFormat.format(new java.util.Date())).append(".csv");
		return loCsvName.toString();
	}

	// End : R5 Added
	/**
	 * This method returns RequestDispatcher VAlue.
	 * @param aoRequest
	 * @param aoResponse
	 * @param aoUserSession
	 * @throws ApplicationException
	 * @throws ServletException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private void getRequestDispatcher(HttpServletRequest aoRequest, HttpServletResponse aoResponse,
			P8UserSession aoUserSession) throws ApplicationException, ServletException, IOException
	{   
		Document loDocument = null;
		// Added extra parameters in Release 5
		String lsUserOrg = null;
		String lsDocType = null;
		String lsDocCategory = null;
		HashMap<String, String> loMap = new HashMap<String, String>();
		P8UserSession loUserSession = (P8UserSession) aoRequest.getSession().getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT);
		Channel loChannel = new Channel();
		loChannel.setData(HHSR5Constants.P8USER_SESSION, loUserSession);
		loChannel.setData(HHSConstants.DOC_ID, aoRequest.getParameter("documentID"));
		HHSTransactionManager.executeTransaction(loChannel, "getDocTypeAndCategory",
				HHSR5Constants.TRANSACTION_ELEMENT_R5);
		loMap = (HashMap<String, String>) loChannel.getData("loDataMap");
		lsDocType = loMap.get(HHSR5Constants.DOCTYPE);
		lsDocCategory = loMap.get(HHSR5Constants.DOCUMENT_CATEGORY);
		lsUserOrg = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG);
		loDocument = FileNetOperationsUtils.viewDocumentInfo(aoUserSession, ApplicationConstants.PROVIDER_ORG,
				aoRequest.getParameter("documentID"), lsDocType, lsUserOrg, lsDocCategory, null);
		// R5 changes ends
		aoRequest.setAttribute("document", loDocument);
		RequestDispatcher loDisp = getServletContext().getRequestDispatcher(
				"/portlet/application/taskdetails/documentinfo.jsp");
		loDisp.include(aoRequest, aoResponse);
	}

	/**
	 * @param aoRequest
	 * @param aoResponse
	 * @param aoChannel
	 * @param asOrganizationId
	 * @throws ApplicationException
	 * @throws ServletException
	 * @throws IOException
	 */
	private void getAddedServiceAndDeletedServiceNameForStaff(HttpServletRequest aoRequest,
			HttpServletResponse aoResponse, Channel aoChannel, String asOrganizationId) throws ApplicationException,
			ServletException, IOException
	{
		String lsStaffId = aoRequest.getParameter("selectBoxValue");
		aoChannel.setData("msStaffId", lsStaffId);
		aoChannel.setData("orgId", asOrganizationId);
		Map<String, String> loServiceInfoMap = new HashMap<String, String>();
		loServiceInfoMap.put("serviceAppId",
				PortalUtil.parseQueryString(aoRequest, ApplicationConstants.SERVICE_APPLICATION_ID));
		loServiceInfoMap.put("orgId", asOrganizationId);
		aoChannel.setData("reqServiceInfo", loServiceInfoMap);
		// R4: Adding Organization ID to add join on STAFF_ORGANIZATION
		// table
		Map<String, String> loParamMap = new LinkedHashMap<String, String>();
		loParamMap.put("orgId", asOrganizationId);
		loParamMap.put(ApplicationConstants.MS_STAFF_ID, lsStaffId);
		aoChannel.setData("loParamMap", loParamMap);
		TransactionManager.executeTransaction(aoChannel, "staffDetailsById");

		List<Map<String, Object>> loServiceCommentsList = (List<Map<String, Object>>) aoChannel
				.getData("serviceCommentsInfo");
		aoRequest.setAttribute("serviceComments", loServiceCommentsList);
		List<StaffDetails> loStaffList = (List<StaffDetails>) aoChannel.getData("allStaffDetailsOutput");
		StaffDetails loReqStaff = (StaffDetails) aoChannel.getData("staffDetailsOutput");
		aoRequest.setAttribute("getValue", loStaffList);
		aoRequest.setAttribute("reqStaff", loReqStaff);
		aoRequest.setAttribute("selectedStaff", lsStaffId);
		aoRequest.setAttribute("staffTitle", getStaffTitle());
		String lsElementId = aoRequest.getParameter("elementId");
		aoRequest.setAttribute(ApplicationConstants.ELEMENT_ID, lsElementId);
		org.jdom.Document loDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.TAXONOMY_ELEMENT);
		if (loDoc != null)
		{
			String lsServiceName = BusinessApplicationUtil.getTaxonomyName(lsElementId, loDoc);
			// code added for deleted service name
			if (null == lsServiceName || lsServiceName.equalsIgnoreCase(""))
			{
				Map<String, String> loActionMap = new HashMap<String, String>();
				loActionMap.put("lsElementId", lsElementId);
				aoChannel.setData("loActionMap", loActionMap);
				TransactionManager.executeTransaction(aoChannel, "getDeletedServiceName");
				lsServiceName = (String) aoChannel.getData("serviceName");
			}
			// code added for deleted service name
			aoRequest.setAttribute("serviceName", lsServiceName);
		}
		RequestDispatcher loDisp = getServletContext().getRequestDispatcher(
				"/portlet/application/taskdetails/addStaff.jsp");
		loDisp.include(aoRequest, aoResponse);
	}

	/**
	 * This method is used to Execute Transcation ID: "contactDetailsById"
	 * @param aoRequest
	 * @param aoResponse
	 * @param aoChannel
	 * @param asOrganizationId
	 * @throws ApplicationException
	 * @throws ServletException
	 * @throws IOException
	 */
	private void getAddedServiceAndDeletedServiceNameForContract(HttpServletRequest aoRequest,
			HttpServletResponse aoResponse, Channel aoChannel, String asOrganizationId) throws ApplicationException,
			ServletException, IOException
	{
		Map<String, String> loServiceInfoMap = new HashMap<String, String>();
		loServiceInfoMap.put("serviceAppId",
				PortalUtil.parseQueryString(aoRequest, ApplicationConstants.SERVICE_APPLICATION_ID));
		loServiceInfoMap.put("orgId", asOrganizationId);
		aoChannel.setData("reqServiceInfo", loServiceInfoMap);
		String lsContractId = aoRequest.getParameter("selectBoxValue");
		String lsElementId = aoRequest.getParameter("elementId");
		Map<String, String> loContractInfo = new HashMap<String, String>();
		loContractInfo.put("asContractId", lsContractId);
		loContractInfo.put("orgId", asOrganizationId);
		aoChannel.setData("aoContractId", lsContractId);
		aoChannel.setData("aoContractInfo", loContractInfo);
		aoChannel.setData("asOrgId", asOrganizationId);

		TransactionManager.executeTransaction(aoChannel, "contactDetailsById");
		List<Map<String, Object>> loServiceCommentsList = (List<Map<String, Object>>) aoChannel
				.getData("serviceCommentsInfo");
		aoRequest.setAttribute("serviceComments", loServiceCommentsList);
		List<ContractDetails> loContractList = (List<ContractDetails>) aoChannel.getData("allContractDetailsOutput");
		List<NYCAgency> loAgencyList = (List<NYCAgency>) aoChannel.getData("allNYCAgencyDetailsOutput");

		ContractDetails loReqContract = (ContractDetails) aoChannel.getData("contractDetailsOutput");
		String lsContractId1 = aoRequest.getParameter("selectBoxValue");
		aoRequest.setAttribute("selectedContract", lsContractId1);
		aoRequest.setAttribute("getValue", loContractList);
		aoRequest.setAttribute("getNYCAgency", loAgencyList);
		aoRequest.setAttribute("reqContract", loReqContract);

		aoRequest.setAttribute("staffTitle", getStaffTitle());
		aoRequest.setAttribute("contractType", CommonUtil.getContractType());
		aoRequest.setAttribute("NYCAgency", getNYCAgency());

		aoRequest.setAttribute(ApplicationConstants.ELEMENT_ID, lsElementId);
		org.jdom.Document loDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.TAXONOMY_ELEMENT);
		if (loDoc != null)
		{
			String lsServiceName = BusinessApplicationUtil.getTaxonomyName(lsElementId, loDoc);
			// code added for deleted service name
			if (null == lsServiceName || lsServiceName.equalsIgnoreCase(""))
			{
				Map<String, String> loActionMap = new HashMap<String, String>();
				loActionMap.put("lsElementId", lsElementId);
				aoChannel.setData("loActionMap", loActionMap);
				TransactionManager.executeTransaction(aoChannel, "getDeletedServiceName");
				lsServiceName = (String) aoChannel.getData("serviceName");
			}
			// code added for deleted service name
			aoRequest.setAttribute("serviceName", lsServiceName);
		}

		RequestDispatcher loDisp = getServletContext().getRequestDispatcher(
				"/portlet/application/taskdetails/addContract.jsp");
		loDisp.include(aoRequest, aoResponse);
	}

	/**
	 * This method set all the member title into the map
	 * 
	 * @return map for member title
	 */
	private Map<String, String> getStaffTitle()
	{
		Channel loChannel = new Channel();
		try
		{
			TransactionManager.executeTransaction(loChannel, "getMemberTitles");
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("exception while fetching getMemberInfoTitle", aoExp);
		}
		return (Map<String, String>) loChannel.getData("getMemberTitles");
	}

	/**
	 * This method set all the contract type into the map
	 * 
	 * @return Map contract type map
	 */
	private Map<String, String> getNYCAgency() throws ApplicationException
	{
		List<Map<String, String>> loProcuringNonProcuringAgencies = null;
		Channel loChannelObj = new Channel();
		Map<String, String> loNYCAgency = new LinkedHashMap<String, String>();
		TransactionManager.executeTransaction(loChannelObj, ApplicationConstants.FETCH_PROCURING_NONPROCURING_AGENCIES);
		loProcuringNonProcuringAgencies = (List<Map<String, String>>) loChannelObj
				.getData(ApplicationConstants.PROCURING_NONPROCURING_AGENCIES);
		for (int liCount = 1; liCount <= loProcuringNonProcuringAgencies.size(); liCount++)
		{
			Map<String, String> loAgenciesMap = (Map<String, String>) loProcuringNonProcuringAgencies.get(liCount - 1);
			loNYCAgency.put(String.valueOf(liCount), loAgenciesMap.get("AGENCY_NAME")
					+ ApplicationConstants.BEGINNING_BRACKET + loAgenciesMap.get("AGENCY_ID")
					+ ApplicationConstants.CLOSING_BRACKET);

		}
		return loNYCAgency;
	}

	/**
	 * This method will get the content of the Appendix type document and
	 * display it to the end user <li>This method was updated in R4</li>
	 * @param aoRequest http servlet request object
	 * @param aoResponse http servlet response object
	 * @param loChannel channel object to execute transaction
	 * @param loUserSession user session object
	 * @throws ApplicationException application exception object
	 * @throws ServletException servlet exception object
	 * @throws IOException IO exception object
	 */
	private void displayAppendixDocContent(HttpServletRequest aoRequest, HttpServletResponse aoResponse,
			Channel loChannel, P8UserSession loUserSession) throws ApplicationException, ServletException, IOException
	{
		InputStream loInpOut = null;
		try
		{
			String lsDocumentType = aoRequest.getParameter("documentType");
			loChannel.setData("aoFilenetSession", loUserSession);
			loChannel.setData(P8Constants.AS_DOC_TITLE, null);
			loChannel.setData("asProviderId", null);
			loChannel.setData(HHSR5Constants.AS_DOC_TYPE, lsDocumentType);
			loChannel.setData("asDocCategory", null);
			loChannel.setData("aoOrgId", ApplicationConstants.CITY_ORG);
			TransactionManager.executeTransaction(loChannel, "checkDocumentExist_filenet");
			String lsDocumentId = (String) loChannel.getData(HHSR5Constants.DOC_ID);
			HashMap loOutputHashMap = actionDisplayDocument(aoRequest, aoResponse, lsDocumentId);
			String lsMimeType = (String) loOutputHashMap.get("MimeType");
			String lsFileType = (String) loOutputHashMap.get("FileType");
			loInpOut = (InputStream) loOutputHashMap.get("ContentElements");
			int loLength = 0;
			/** [Start] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
			/*aoResponse.setContentType(lsMimeType);
			aoResponse.setHeader("Content-Disposition", "attachment;FileName=" + aoRequest.getParameter("documentName")
					+ "." + lsFileType.toLowerCase());*/
			aoResponse.setContentType(HHSUtil.sanitizeCarriageReturns(lsMimeType));
			aoResponse.setHeader("Content-Disposition", "attachment;FileName=" + HHSUtil.sanitizeCarriageReturns(aoRequest.getParameter("documentName"))
					+ "." + HHSUtil.sanitizeCarriageReturns(lsFileType.toLowerCase()));
			/** [End] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
			ClientInputStream loClientInputStream = (ClientInputStream) loInpOut;
			int liContentLen = loClientInputStream.getTotalSize().intValue();
			aoResponse.setContentLength(liContentLen);
			byte[] lbByteArray = new byte[1024];
			DataInputStream loDataIn = new DataInputStream(loInpOut);
			ServletOutputStream loServletOP = aoResponse.getOutputStream();
			while ((loDataIn != null) && ((loLength = loDataIn.read(lbByteArray)) != -1))
			{
				loServletOP.write(lbByteArray, 0, loLength);
			}

			loDataIn.close();
			loServletOP.flush();
			loServletOP.close();
			loClientInputStream.close();
		}
		finally
		{
			if (null != loInpOut)
			{
				loInpOut.close();
			}
		}
	}

	/**
	 * Method added in R5 This method is used to get the content and mime type
	 * of the selected document
	 * 
	 * @param aoRequest http servlet request object
	 * @param aoResponse http servlet response object
	 * @return Input Stream object
	 * @throws ApplicationException application exception object
	 * @throws IOException IO exception object
	 * @throws ServletException Servlet Exception Object
	 */
	private InputStream getDocumentContent(HttpServletRequest aoRequest, HttpServletResponse aoResponse)
			throws ApplicationException, IOException, ServletException
	{   
		// R5 changes ends
		String lsDocumentId = null;
		if (aoRequest.getParameter(HHSR5Constants.DOC_ID) != null)
		{
			lsDocumentId = aoRequest.getParameter(HHSR5Constants.DOC_ID);
		}
		else
		// Changes for PSR
		{   
			String lsProcurementId = aoRequest.getParameter(HHSConstants.PROCUREMENT_ID);
			String lsPsrDetailsId = aoRequest.getParameter(HHSR5Constants.PSR_DETAILS_ID);
			String lsDocTitle = HHSConstants.STR + HHSR5Constants.PSR_PDF + HHSConstants.UNDERSCORE + lsProcurementId
					+ HHSConstants.STR;
			String lsFolderpath = PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
					P8Constants.PREDEFINED_FOLDER_PATH_FINANCIAL_DOC)
					+ HHSConstants.FORWARD_SLASH
					+ lsProcurementId
					+ HHSConstants.UNDERSCORE + lsPsrDetailsId;
			P8UserSession loUserSession = (P8UserSession) aoRequest.getSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT);
			Channel loChannel = new Channel();
			loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
			loChannel.setData(HHSConstants.DOCUMENT_TITLE, lsDocTitle);
			loChannel.setData(HHSConstants.CONTRACT_ID_KEY, HHSR5Constants.PSR_PDF);
			loChannel.setData(HHSConstants.FINANCIAL_PDF_DOC_PATH, lsFolderpath);
			TransactionManager.executeTransaction(loChannel, "getPdfDocumentId", HHSR5Constants.TRANSACTION_ELEMENT_R5);
			List<String> loDocumentId = (List<String>) loChannel.getData("loListOfDocId");
			lsDocumentId = loDocumentId.get(0);
		}
		
		return getDocumentContent(aoRequest, aoResponse, lsDocumentId);
	}

	/**
	 * The method is added in R5. It will check if the document exists in the
	 * user's document vault or not.
	 * @param aoRequest
	 * @param aoResponse
	 * @return
	 * @throws ApplicationException
	 * @throws IOException
	 * @throws ServletException
	 */
	private boolean checkDocumentExists(HttpServletRequest aoRequest, HttpServletResponse aoResponse)
			throws ApplicationException, IOException, ServletException
	{   
		boolean llStat = true;
		PrintWriter loPrintWriter = null;
		String lsDocumentId = aoRequest.getParameter(HHSR5Constants.DOCUMENT_DATA);
		LOG_OBJECT.Info("====DocumentId :: " + lsDocumentId);
		String lsType = aoRequest.getParameter("documentType");
		String lsCheckedItems = aoRequest.getParameter("checkedItem");
		Map<String, String> loCheckedItemMap = new HashMap<String, String>();
		String lsJspName = aoRequest.getParameter("jspName");
		if (lsCheckedItems != null && !StringUtils.isEmpty(lsCheckedItems))
		{
			String loItems[] = lsCheckedItems.split("~");
			String lsSubDetails[];
			for (String lsItem : loItems)
			{
				if (!StringUtils.isEmpty(lsItem))
				{
					lsSubDetails = lsItem.split(",");
					loCheckedItemMap.put(lsSubDetails[0], lsSubDetails[1]);
				}
			}
		}

		try
		{
			Channel loChannel = new Channel();
			P8UserSession loUserSession = (P8UserSession) aoRequest.getSession().getAttribute(ApplicationConstants.FILENET_SESSION_OBJECT);
			loChannel.setData(HHSR5Constants.P8USER_SESSION, loUserSession);
			aoResponse.setContentType(HHSConstants.TEXT_HTML);
			loPrintWriter = aoResponse.getWriter();
			if (null != lsDocumentId && !lsDocumentId.isEmpty()
					&& !lsDocumentId.contains(HHSR5Constants.RECYCLE_BIN_ID)
					&& !lsDocumentId.equals(HHSR5Constants.DOCUMENT_VAULT_ID))
			{
				loChannel.setData(ApplicationConstants.DOCUMENT_ID, lsDocumentId);
				loCheckedItemMap.put(lsDocumentId, lsType);
			}
			loChannel.setData("screenName", lsJspName);
			loChannel.setData("documentIdMap", loCheckedItemMap);
			TransactionManager.executeTransaction(loChannel, "checkFolderAndDocumentExist",
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			Boolean loFlag = (Boolean) loChannel.getData("loSelectedItemsExist");
			if (loFlag)
			{
				LOG_OBJECT.Info( "######[Teace]checkDocumentExists : FileExists  "  );
				loPrintWriter.write("FileExists");
			}
			else
			{
				LOG_OBJECT.Info( "######[Teace]checkDocumentExists : FileExists  "  );
				loPrintWriter.write("FilenotFound");
			}
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Info( "######[Teace] Exception at checkDocumentExists()  "  );
			loPrintWriter.write("FilenotFound");
		}
		finally
		{
			if (null != loPrintWriter)
			{
				loPrintWriter.flush();
				loPrintWriter.close();
			}
		}
		return llStat;
	}

	// Changes R5 Ends
	/**
	 * Method changed in R5 This method is used to get the content and mime type
	 * of the selected document and redirect it to daeja viewer
	 * 
	 * <li>This method was updated in R4</li>
	 * 
	 * @param aoRequest http servlet request object
	 * @param aoResponse http servlet response object
	 * @return Input Stream object
	 * @throws ApplicationException application exception object
	 * @throws IOException IO exception object
	 * @throws ServletException Servlet Exception Object
	 */
	private InputStream getDocumentContent(HttpServletRequest aoRequest, HttpServletResponse aoResponse,
			String asDocumentId) throws ApplicationException, IOException, ServletException
	{
		InputStream loInpOut = null;
		try
		{
			String lsFileType = getFileTypeForDocId(aoRequest, asDocumentId);
			int loLength = 0;
			HashMap loOutputHashMap = actionDisplayDocument(aoRequest, aoResponse, asDocumentId);
			String lsMimeType = (String) loOutputHashMap.get("MimeType");
			// Start Emergency Build 4.0.1 defect 8365
			String lsDocumentTitle = (String) loOutputHashMap.get(P8Constants.PROPERTY_CE_DOCUMENT_TITLE);
			// End Emergency Build 4.0.1 defect 8365
			loInpOut = (InputStream) loOutputHashMap.get("ContentElements");
			/** [Start] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
			/*aoResponse.setContentType(lsMimeType); 
			// Start Emergency Build 4.0.1 defect 8365
			aoResponse.setHeader("Content-Disposition",
					"attachment;FileName=\"" + lsDocumentTitle + "." + lsFileType.toLowerCase() + "\"");*/
			aoResponse.setContentType(HHSUtil.sanitizeCarriageReturns(lsMimeType)); 
			// Start Emergency Build 4.0.1 defect 8365
			aoResponse.setHeader("Content-Disposition",
					"attachment;FileName=\"" + HHSUtil.sanitizeCarriageReturns(lsDocumentTitle) + "." + HHSUtil.sanitizeCarriageReturns(lsFileType.toLowerCase()) + "\"");
			/** [End] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
			// End Emergency Build 4.0.1 defect 8365
			ClientInputStream loClientInputStream = (ClientInputStream) loInpOut;
			int liContentLen = loClientInputStream.getTotalSize().intValue();
			aoResponse.setContentLength(liContentLen);
			byte[] lbByteArray = new byte[1024];
			DataInputStream loDataIn = new DataInputStream(loInpOut);
			ServletOutputStream loServletOP = aoResponse.getOutputStream();
			while ((loDataIn != null) && ((loLength = loDataIn.read(lbByteArray)) != -1))
			{
				loServletOP.write(lbByteArray, 0, loLength);
			}
			loDataIn.close();
			loServletOP.flush();
			loServletOP.close();
			loClientInputStream.close();
		}
		catch (ApplicationException aoAppEx)
		{
			throw aoAppEx;
		}
		finally
		{
			if (null != loInpOut)
			{
				loInpOut.close();
			}
		}
		
		return loInpOut;
	}

	/**
	 * This method will get list of document types for selected document
	 * category <li>This method was updated in R4</li>
	 * @param aoRequest a servlet request object
	 * @param aoResponse a servlet response object
	 * @param asOrganizationId a string value of organization Id
	 * @param asSelectedCategory a string value of selected category
	 * @throws ApplicationException If an Application Exception occurs
	 * @throws ServletException If a Servlet Exception occurs
	 * @throws IOException If an Input Output Exception occurs
	 */
	private void getDocTypeForSelectedCategory(HttpServletRequest aoRequest, HttpServletResponse aoResponse,
			String asOrganizationId, String asSelectedCategory) throws ApplicationException, ServletException,
			IOException
	{
		String lsRequestingOrganization = (String) aoRequest.getSession().getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_TYPE);
		// R4 Document Vault changes: fetching request parameter
		// 'sharedTypeRequest' which would be true of the login is from city
		String lsSharedTypeRequest = aoRequest.getParameter("sharedTypeRequest");
		if (null != lsSharedTypeRequest && lsSharedTypeRequest.equalsIgnoreCase("true"))
		{

			lsRequestingOrganization = ApplicationConstants.PROVIDER_ORG;
		}
		if (asSelectedCategory.indexOf("$") >= 0)
		{
			asSelectedCategory = asSelectedCategory.replace("$", "&");
		}
		List<String> loDocTypeList = FileNetOperationsUtils.getDocTypeForDocCategory(asSelectedCategory,
				asOrganizationId, lsRequestingOrganization);
		StringBuffer lsBfDocType = new StringBuffer();
		if (!CollectionUtils.isEmpty(loDocTypeList))
		{
			Iterator<String> loIter = loDocTypeList.iterator();
			while (loIter.hasNext())
			{
				lsBfDocType.append(loIter.next());
				lsBfDocType.append("|");
			}
		}
		aoRequest.setAttribute("test", lsBfDocType.toString());
		RequestDispatcher loDisp = getServletContext().getRequestDispatcher(
				"/portlet/application/documentvault/fragment1.jsp");
		loDisp.include(aoRequest, aoResponse);
	}

	/**
	 * This method will get list of Sample document category and Sample Document
	 * Type
	 * 
	 * @param aoRequest a servlet request object
	 * @param aoResponse a servlet response object
	 * @throws ApplicationException If an Application Exception occurs
	 * @throws ServletException If a Servlet Exception occurs
	 * @throws IOException If an Input Output Exception occurs
	 */
	private void getSampleCategoryAndType(HttpServletRequest aoRequest, HttpServletResponse aoResponse)
			throws ApplicationException, ServletException, IOException
	{
		List<String> loSampleCategoryList = getSampleCategoryList();
		StringBuffer lsBfSampleType = new StringBuffer();

		if (!CollectionUtils.isEmpty(loSampleCategoryList))
		{
			Iterator<String> loIter = loSampleCategoryList.iterator();
			while (loIter.hasNext())
			{
				lsBfSampleType.append(loIter.next());
				lsBfSampleType.append("|");
			}
			lsBfSampleType = new StringBuffer(lsBfSampleType.substring(0, lsBfSampleType.length() - 1));
		}
		aoRequest.setAttribute("test", lsBfSampleType.toString());
		RequestDispatcher loDisp = getServletContext().getRequestDispatcher(
				"/portlet/application/documentvault/fragment1.jsp");
		loDisp.include(aoRequest, aoResponse);
	}

	/**
	 * This method will create map for required document properties
	 * 
	 * @return a map containing required document properties
	 */
	private HashMap<String, String> requiredDocsProps()
	{
		HashMap<String, String> loReqPropMaps = new HashMap<String, String>();

		loReqPropMaps.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, "");
		loReqPropMaps.put(P8Constants.PROPERTY_CE_DOC_TYPE, "");
		return loReqPropMaps;
	}

	/**
	 * This method will get a list of Sample document Category
	 * 
	 * @return a list of Sample document Category
	 * @throws ApplicationException If an Application Exception occurs
	 */
	private ArrayList<String> getSampleCategoryList() throws ApplicationException
	{
		org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENETDOCTYPE);
		ArrayList<String> loSampleCategoryList = null;
		loSampleCategoryList = (ArrayList<String>) FileNetOperationsUtils.getDocCategoryFromXML(loXMLDoc, null,
				ApplicationConstants.PROVIDER_ORG);
		return loSampleCategoryList;
	}

	/**
	 * This method will get Input Stream for Document viewer based on document
	 * Id
	 * 
	 * @param aoRequest a servlet request object
	 * @param aoResponse a servlet response object
	 * @return an Input Stream for document viewer
	 * @throws ApplicationException If an Application Exception occurs
	 * @throws IOException If an Input Output Exception occurs
	 */
	private HashMap actionDisplayDocument(HttpServletRequest aoRequest, HttpServletResponse aoResponse,
			String asDocumentId) throws ApplicationException, IOException
	{   
		HashMap loOutputHashMap = null;
		try
		{
			PortalUtil.parseQueryString(aoRequest, ApplicationConstants.FILENET_SESSION_OBJECT);
			P8UserSession loUserSession = (P8UserSession) aoRequest.getSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT);
			Channel loChannel = new Channel();
			loChannel.setData("aoFilenetSession", loUserSession);
			loChannel.setData(ApplicationConstants.DOCUMENT_ID, asDocumentId);
			TransactionManager.executeTransaction(loChannel, "documentcontent_filenet");
			loOutputHashMap = (HashMap) loChannel.getData("loOutputHashMap");
		}
		catch (ApplicationException aoAppEx)
		{
			throw aoAppEx;
		}
		return loOutputHashMap;
	}

	/**
	 * This method will get the document details based on Provider name
	 * 
	 * @param loUserSession a P8UserSession object
	 * @param loSession a HttpSession object
	 * @param aoRequest a servlet request object
	 * @param aoResponse a servlet response object
	 * @throws ApplicationException If an Application Exception occurs
	 * @throws ServletException If a Servlet Exception occurs
	 * @throws IOException If an Input Output Exception occurs
	 */
	@SuppressWarnings("unchecked")
	private void getDocumentDetailsByProviderName(P8UserSession aoUserSession, HttpSession aoSession,
			HttpServletRequest aoRequest, HttpServletResponse aoResponse) throws ApplicationException,
			ServletException, IOException
	{
		String lsUserOrg = (String) aoSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG);
		String lsOrgType = (String) aoSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE);
		if (null != lsOrgType && lsOrgType.equalsIgnoreCase(HHSConstants.CITY_ORG))
		{
			lsUserOrg = lsOrgType;
		}
		HashMap<String, Object> loFilterProps = new HashMap<String, Object>();
		HashMap<Integer, String> loOrderByMap = new HashMap<Integer, String>();
		loFilterProps.put(P8Constants.PROPERTY_CE_IS_CURRENT_VERSION, true);
		String lsProviderAgencyId = aoRequest.getParameter("providerName");
		TreeSet<String> loProviderList = getSharedAgencyProviderList(aoUserSession,
				P8Constants.PROPERTY_CE_SHARED_PROVIDER_ID, lsUserOrg);
		TreeSet<String> loAgencyList = getSharedAgencyProviderList(aoUserSession,
				P8Constants.PROPERTY_CE_SHARED_AGENCY_ID, lsUserOrg);
		if (loProviderList != null && loProviderList.contains(lsProviderAgencyId))
		{
			loFilterProps.put(P8Constants.PROPERTY_CE_FILTER_PROVIDER_ID, lsProviderAgencyId.trim());

		}
		else if (loAgencyList != null && loAgencyList.contains(lsProviderAgencyId))
		{
			loFilterProps.put(P8Constants.PROPERTY_CE_FILTER_NYC_ORG, lsProviderAgencyId.trim());
		}
		loFilterProps.put(P8Constants.PROPERTY_CE_SHARED_BY_ID, lsUserOrg);
		Channel loChannel = new Channel();
		loChannel.setData("aoFilenetSession", aoUserSession);
		loOrderByMap.put(1, ApplicationConstants.DOC_TYPE + " " + "ASC");
		loChannel.setData("orderByMap", loOrderByMap);
		// Added extra Parameter for Release 5
		List<HashMap<String, Object>> loDocumentList = FileNetOperationsUtils.getDocumentList(loChannel, null,
				requiredDocsProps(), loFilterProps, true, "");
		StringBuffer lsBfDocDetail = new StringBuffer();
		if (loDocumentList != null)
		{
			Iterator<HashMap<String, Object>> loIterator = loDocumentList.iterator();
			while (loIterator.hasNext())
			{
				HashMap<String, Object> loDocProps = (HashMap<String, Object>) loIterator.next();

				lsBfDocDetail.append((String) loDocProps.get(P8Constants.PROPERTY_CE_DOCUMENT_TITLE));
				lsBfDocDetail.append("!");
				lsBfDocDetail.append((String) loDocProps.get(P8Constants.PROPERTY_CE_DOC_TYPE));
				lsBfDocDetail.append("|");
			}
		}
		aoRequest.setAttribute("test", lsBfDocDetail.toString());
		RequestDispatcher loDisp = getServletContext().getRequestDispatcher(
				"/portlet/application/documentvault/fragment1.jsp");
		loDisp.include(aoRequest, aoResponse);
	}

	/**
	 * This method will get list of providers and agencies based on provider or
	 * agency Id
	 * 
	 * @param aoUserSession a P8UserSession object
	 * @param asAgencyType a string value for agency type
	 * @param asProviderId a string value for provider Id
	 * @return a set containing providers and agencies
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings("unchecked")
	private TreeSet<String> getSharedAgencyProviderList(P8UserSession aoUserSession, String asAgencyType,
			String asProviderId) throws ApplicationException
	{
		Channel loChannel = new Channel();
		loChannel.setData("aoFilenetSession", aoUserSession);
		loChannel.setData("asAgencyType", asAgencyType);
		loChannel.setData("asProviderId", asProviderId);
		TransactionManager.executeTransaction(loChannel, "getSharedAgencyProviderList_filenet");
		TreeSet<String> loProviderSet = (TreeSet<String>) loChannel.getData("providerList");
		return loProviderSet;
	}

	/**
	 * This method will get Input Stream for Document viewer based on document
	 * Id
	 * 
	 * @param aoRequest a servlet request object
	 * @param aoResponse a servlet response object
	 * @return an Input Stream for document viewer
	 * @throws ApplicationException If an Application Exception occurs
	 * @throws IOException If an Input Output Exception occurs
	 */
	private String getFileTypeForDocId(HttpServletRequest aoRequest, String asDocumentId) throws ApplicationException,
			IOException
	{
		String lsFileType = "";
		try
		{
			PortalUtil.parseQueryString(aoRequest, ApplicationConstants.FILENET_SESSION_OBJECT);
			P8UserSession loUserSession = (P8UserSession) aoRequest.getSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT);
			List<String> loDocIdList = new ArrayList<String>();
			loDocIdList.add(asDocumentId);
			HashMap loReqPropsMap = new HashMap();
			loReqPropsMap.put(P8Constants.PROPERTY_CE_FILE_TYPE, "");
			Channel loChannel = new Channel();
			loChannel.setData("aoFilenetSession", loUserSession);
			loChannel.setData("hmReqProps", loReqPropsMap);
			loChannel.setData("aoDocIdList", loDocIdList);
			TransactionManager.executeTransaction(loChannel, "displayDocProp_filenet");
			HashMap loOutputHashMap = (HashMap) loChannel.getData("documentPropHM");
			if (null != loOutputHashMap)
			{
				HashMap loPropsMap = (HashMap) loOutputHashMap.get(asDocumentId);
				if (null != loPropsMap)
				{
					lsFileType = (String) loPropsMap.get(P8Constants.PROPERTY_CE_FILE_TYPE);
				}
			}
		}
		catch (ApplicationException aoAppEx)
		{
			throw aoAppEx;
		}
		return lsFileType;
	}

	/**
	 * This method is added for enhancement 3.1.0 : 6025
	 * <p>
	 * This method reads the zip file and deletes the file from the location.
	 * </p>
	 * @param aoRequest HttpServletRequest object
	 * @param aoResponse HttpServletResponse object
	 * @return
	 * @throws ApplicationException if ApplicationException occurs
	 * @throws IOException if IOException occurs
	 * @throws ServletException if ServletException occurs
	 */
	private InputStream getZipDocumentContent(HttpServletRequest aoRequest, HttpServletResponse aoResponse)
			throws ApplicationException, IOException, ServletException
	{

		String lsFileName = aoRequest.getParameter(HHSConstants.FILE_NAME_PARAMETER);
		String lsProviderOrgID = aoRequest.getParameter(HHSConstants.PROVIDER_ORG_ID);
		String lsProcurementId = aoRequest.getParameter(HHSConstants.PROCUREMENT_ID);
		String lsEvaluationPoolMappingId = aoRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID);

		String lsParentFolderName = lsProcurementId + HHSConstants.UNDERSCORE + lsEvaluationPoolMappingId
				+ HHSConstants.UNDERSCORE + lsProviderOrgID;
		String lsZipFilePath = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
				HHSConstants.ZIP_DOCUMENTS_PATH)
				+ File.separator
				+ lsParentFolderName
				+ File.separator
				+ lsFileName
				+ HHSConstants.ZIP;
		InputStream loInpOut = null;
		try
		{
			String lsFileType = HHSConstants.ZIP;
			int loLength = 0;
			loInpOut = new FileInputStream(lsZipFilePath);
			aoResponse.setContentType(HHSConstants.CONTENT_TYPE_ZIP);
			/** [Start] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
			/*aoResponse.setHeader("Content-Disposition",
					"attachment;FileName=\"" + lsFileName + lsFileType.toLowerCase() + "\"");*/
			aoResponse.setHeader("Content-Disposition",
					"attachment;FileName=\"" + HHSUtil.sanitizeCarriageReturns(lsFileName + lsFileType.toLowerCase()) + "\"");
			/** [End] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
			byte[] lbByteArray = new byte[1024];
			DataInputStream loDataIn = new DataInputStream(loInpOut);
			ServletOutputStream loServletOP = aoResponse.getOutputStream();
			while ((loDataIn != null) && ((loLength = loDataIn.read(lbByteArray)) != -1))
			{
				loServletOP.write(lbByteArray, 0, loLength);
			}
			loDataIn.close();
			loServletOP.flush();
			loServletOP.close();
		}
		finally
		{
			if (null != loInpOut)
			{
				loInpOut.close();
			}
		}
		return loInpOut;
	}

	/**
	 * This method is added as a part of Release 5 to Export Data. It will be
	 * executed when a user click on the Export report button on the detail
	 * report screen. It will read the report configurations then. It will
	 * invoke the exportReport method from ReportHandler Class and return
	 * required parameters o download a file
	 * @param aoRequest HttpServletRequest
	 * @param aoResponse HttpServletResponse
	 * @throws ApplicationException
	 */
	public void exportData(HttpServletRequest aoRequest, HttpServletResponse aoResponse) throws ApplicationException
	{   
		Channel loChannel = new Channel();
		Element loNodeChart = null;
		String lsReportTemplateName = null;
		String lsDataSource = null;
		String lsQueryId = null;
		List<ReportBean> loReportData = null;
		String lsExportFormat = null;
		String lsExportTableName = null;
		Map<String, Object> loReportRenderMap = null;
		String lsEmitterClassName = null;
		Map<String, Map<String, Object>> loReportConfigDetails = null;
		Map<String, Object> loVariableDetails = null;
		String lsTabName = HHSConstants.EMPTY_STRING;
		SimpleDateFormat moDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		try

		{
			String lsReportId = HHSPortalUtil.parseQueryString(aoRequest, HHSR5Constants.REPORT_ID);
			String lsUserOrgType = (String) aoRequest.getSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE);
			ReportBean loReportBean = (ReportBean) aoRequest.getSession().getAttribute(HHSR5Constants.REPORT_BEAN_LIST);
			lsTabName = aoRequest.getParameter(HHSR5Constants.TAB_NAME);
						
			loChannel.setData(HHSR5Constants.REPORT_BEAN, loReportBean);
			loReportConfigDetails = HHSReportUtil.getReportConfigDetails(lsUserOrgType, lsReportId, lsTabName);
			lsReportTemplateName = (String) loReportConfigDetails.get(HHSR5Constants.RENDER_DETAILS).get(
					HHSR5Constants.EXPORT_DESIGN_ID);
			
			if (null == lsReportTemplateName || lsReportTemplateName.isEmpty())
				lsReportTemplateName = (String) loReportConfigDetails.get(HHSR5Constants.RENDER_DETAILS).get(
						HHSR5Constants.CHART_DESIGN_ID);
			lsDataSource = (String) loReportConfigDetails.get(HHSR5Constants.RENDER_DETAILS).get(
					HHSR5Constants.DATA_SOURCE);
			lsExportFormat = (String) loReportConfigDetails.get(HHSR5Constants.RENDER_DETAILS).get(
					HHSR5Constants.EXPORT_FORMAT);
			lsExportTableName = (String) loReportConfigDetails.get(HHSR5Constants.RENDER_DETAILS).get(
					HHSR5Constants.EXPORT_TABLE_NAME);
			
			if (null != lsDataSource && lsDataSource.equalsIgnoreCase(HHSR5Constants.SCRIPTED))
			{
				lsQueryId = (String) (String) loReportConfigDetails.get(HHSR5Constants.RENDER_DETAILS).get(
						HHSR5Constants.EXPORT_SQL_ID);
				
				if (StringUtils.isBlank(lsQueryId))
				{
					lsQueryId = (String) (String) loReportConfigDetails.get(HHSR5Constants.RENDER_DETAILS).get(
							HHSR5Constants.SQL_ID);
				}
				
				loReportBean.setSqlId(lsQueryId);
				loChannel.setData(HHSR5Constants.REPORT_BEAN, loReportBean);
				TransactionManager.executeTransaction(loChannel, HHSR5Constants.GET_REPORT_DATA,
						HHSR5Constants.TRANSACTION_ELEMENT_R5);
				loReportData = (List<ReportBean>) loChannel.getData(HHSR5Constants.LO_REPORT_LIST);
				loVariableDetails = loReportConfigDetails.get(HHSR5Constants.VARIABLE);
				for (Map.Entry<String, Object> entrySet : loVariableDetails.entrySet())
				{
					aoRequest.getSession().setAttribute(entrySet.getKey(), loReportData);
				}
			}
			lsEmitterClassName = (String) loReportConfigDetails.get(HHSR5Constants.RENDER_DETAILS).get(
					HHSR5Constants.EMITTER_CLASS);
			loReportRenderMap = ReportHandler.exportReport(aoRequest, lsEmitterClassName, lsReportTemplateName);
			aoResponse.setContentType((String) loReportRenderMap.get(HHSR5Constants.MIME_TYPE));
			if (lsReportId.equalsIgnoreCase(HHSR5Constants.BUDGET_UTILIZATION))
			{
				if (lsTabName.equalsIgnoreCase(HHSR5Constants.INVOICE))
				{
					/** [Start] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
					/*aoResponse.setHeader(
							"Content-Disposition",
							"attachment;FileName=" + lsReportId + "_" + lsTabName.toLowerCase() + "_"
									+ loReportBean.getFyYear() + "_" + moDateFormat.format(new java.util.Date()) + "."
									+ lsExportFormat);*/
					
					aoResponse.setHeader(
							"Content-Disposition",
							"attachment;FileName=" + HHSUtil.sanitizeCarriageReturns(lsReportId + "_" + lsTabName.toLowerCase() + "_"
									+ loReportBean.getFyYear() + "_" + moDateFormat.format(new java.util.Date()) + "."
									+ lsExportFormat));
					/** [End] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
				}
				else
				{
					/** [Start] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
					/*aoResponse.setHeader("Content-Disposition", "attachment;FileName=" + lsReportId + "_"
							+ HHSR5Constants.CONTRACT.toLowerCase() + "_" + loReportBean.getFyYear() + "_"
							+ moDateFormat.format(new java.util.Date()) + "." + lsExportFormat);*/
					aoResponse.setHeader("Content-Disposition", "attachment;FileName=" + HHSUtil.sanitizeCarriageReturns(lsReportId + "_"
							+ HHSR5Constants.CONTRACT.toLowerCase() + "_" + loReportBean.getFyYear() + "_"
							+ moDateFormat.format(new java.util.Date()) + "." + lsExportFormat));
					/** [Start] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
				}
			}
			else
			{
				/** [Start] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
				// R5 Start: Updated for defect 7861
				/*aoResponse.setHeader(
						"Content-Disposition",
						"attachment;FileName=" + lsReportId + "_" + loReportBean.getFyYear() + "_"
								+ moDateFormat.format(new java.util.Date()) + "." + lsExportFormat);*/
				aoResponse.setHeader(
						"Content-Disposition",
						"attachment;FileName=" + HHSUtil.sanitizeCarriageReturns(lsReportId + "_" + loReportBean.getFyYear() + "_"
								+ moDateFormat.format(new java.util.Date()) + "." + lsExportFormat));
				// R5 End: Updated for defect 7861
				/** [End] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
			}
			
			PrintWriter loPrntWrter = aoResponse.getWriter();
			loPrntWrter.write(loReportRenderMap.get(HHSR5Constants.EXPORT_CONTENT).toString());
			loPrntWrter.flush();

		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Exception occurred while exporting reports", aoAppExp);
			throw aoAppExp;
		}
		catch (Exception aoExp)
		{
			ApplicationException loAppEx = new ApplicationException("Exception occurred while exporting reports", aoExp);
			throw loAppEx;
		}
	}

	/**
	 * This method will be executed when a user type anything in the typeahead
	 * text box. it will return the list of document types
	 * @param aoRequest Servlet request object
	 * @param aoResponse Servlet response object
	 * @throws ApplicationException
	 * @throws ServletException
	 * @throws IOException
	 */
	private void getDocType(HttpServletRequest aoRequest, HttpServletResponse aoResponse) throws ApplicationException,
			ServletException, IOException
	{
		final int liMinLength = 3;
		List<String> loArrayList = new ArrayList<String>();
		List<String> loArrayListDistinct = new ArrayList<String>();
		String lsPartialUoDenom = aoRequest.getParameter("query");
		String lsRequestingDocumentType = aoRequest.getParameter("requestingtype");
		String lsUserOrgType = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE);
		String lsUserManageOrg = (String) aoRequest.getSession().getAttribute("cityUserSearchProviderId");
		String lsHeaderFlag = aoRequest.getParameter("headerClick");
		if (null != lsUserManageOrg && !lsUserManageOrg.isEmpty()
				&& !lsUserManageOrg.equalsIgnoreCase(HHSR5Constants.EMPTY_STRING) && null != lsHeaderFlag
				&& !lsHeaderFlag.isEmpty() && null != lsHeaderFlag && !lsHeaderFlag.equalsIgnoreCase("true"))
		{
			lsUserOrgType = lsUserManageOrg.substring(lsUserManageOrg.indexOf("~") + 1);
		}
		try
		{
			loArrayList = FileNetOperationsUtils.getDocType(lsUserOrgType, lsRequestingDocumentType, lsPartialUoDenom);
			TreeSet<String> loTreeSet = new TreeSet<String>();
			loTreeSet.addAll(loArrayList);
			Iterator loItrDistinct = loTreeSet.iterator();
			while (loItrDistinct.hasNext())
			{
				loArrayListDistinct.add((String) loItrDistinct.next());
			}
			generateJSONForTypeHead(aoResponse, liMinLength, loArrayListDistinct, lsPartialUoDenom);
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error Occured while searching document types", aoExp);
			throw new ApplicationException("Error Occured while searching document types", aoExp);
		}
	}

	/**
	 * This method will be executed when a user type anything in the typeahead
	 * text box. it will return the list of ProcurementTitle
	 * @param aoRequest Servlet request object
	 * @param aoResponse Servlet response object
	 * @throws ApplicationException
	 * @throws ServletException
	 * @throws IOException
	 */
	private void getProcurementTitle(HttpServletRequest aoRequest, HttpServletResponse aoResponse)
			throws ApplicationException, ServletException, IOException
	{
		final int liMinLength = 3;
		List<ExtendedDocument> loArrayList = new ArrayList<ExtendedDocument>();
		String lsPartialUoDenom = aoRequest.getParameter("query");
		String lsSelectedOrgType = aoRequest.getParameter(HHSConstants.ORGTYPE);
		String lsUserOrgType = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE);
		// Changes for Defect 7570 starts
		if (null != lsSelectedOrgType && !lsSelectedOrgType.equalsIgnoreCase(HHSConstants.EMPTY_STRING))
		{
			lsUserOrgType = (lsUserOrgType.equalsIgnoreCase(lsSelectedOrgType) ? lsUserOrgType : lsSelectedOrgType);
		}
		// Changes for Defect 7570 ends
		loArrayList = FileNetOperationsUtils.getProcurementTitle(lsUserOrgType, lsPartialUoDenom);

		if ((lsPartialUoDenom != null) && (lsPartialUoDenom.length() >= liMinLength))
		{
			PrintWriter loOut = aoResponse.getWriter();
			try
			{
				aoResponse.setContentType("application/json");
				final String lsOutputJSONaoResponse = this
						.generateDelimitedResponseForProcurement(loArrayList, lsPartialUoDenom, liMinLength).toString()
						.trim();
				loOut.print(lsOutputJSONaoResponse);
			}
			catch (final Exception aoExp)
			{
				LOG_OBJECT.Error("Exception occurred while searching procurement:", aoExp);
			}
			finally
			{
				BaseControllerUtil.closingPrintWriter(loOut);
			}
		}
	}

	/**
	 * This method will be executed when a user type anything in the typeahead
	 * text box. it will return the list of InvoiceNumber
	 * @param aoRequest Servlet request object
	 * @param aoResponse Servlet response object
	 * @throws ApplicationException
	 * @throws ServletException
	 * @throws IOException
	 */
	private void getInvoiceNumber(HttpServletRequest aoRequest, HttpServletResponse aoResponse)
			throws ApplicationException, ServletException, IOException
	{
		final int liMinLength = 3;
		List<ExtendedDocument> loArrayList = new ArrayList<ExtendedDocument>();
		String lsPartialUoDenom = aoRequest.getParameter("query");
		String lsSelectedOrgType = aoRequest.getParameter(HHSConstants.USER_ORG_ID);
		String lsUserOrgType = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG);
		if (null != lsSelectedOrgType && !lsSelectedOrgType.equalsIgnoreCase(HHSConstants.EMPTY_STRING))
		{
			lsUserOrgType = (lsUserOrgType.equalsIgnoreCase(lsSelectedOrgType) ? lsUserOrgType : lsSelectedOrgType);
		}
		loArrayList = FileNetOperationsUtils.getInvoiceNumber(lsUserOrgType, lsPartialUoDenom);

		if ((lsPartialUoDenom != null) && (lsPartialUoDenom.length() >= liMinLength))
		{
			PrintWriter loOut = aoResponse.getWriter();
			try
			{
				aoResponse.setContentType("application/json");
				final String lsOutputJSONaoResponse = this
						.generateDelimitedResponse(loArrayList, lsPartialUoDenom, liMinLength).toString().trim();
				loOut.print(lsOutputJSONaoResponse);
			}
			catch (final Exception aoExp)
			{
				LOG_OBJECT.Error("Exception occurred while searching procurement:", aoExp);
			}
			finally
			{
				BaseControllerUtil.closingPrintWriter(loOut);
			}
		}
	}

	/**
	 * This method will be executed when a user type anything in the typeahead
	 * text box. it will return the list of AwardPin
	 * @param aoRequest Servlet request object
	 * @param aoResponse Servlet response object
	 * @throws ApplicationException
	 * @throws ServletException
	 * @throws IOException
	 */
	private void getAwardPin(HttpServletRequest aoRequest, HttpServletResponse aoResponse) throws ApplicationException,
			ServletException, IOException
	{
		final int liMinLength = 3;
		List<String> loArrayList = new ArrayList<String>();
		String lsPartialUoDenom = aoRequest.getParameter("query");
		String lsUserOrg = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG);
		String lsUserOrgType = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE);
		loArrayList = FileNetOperationsUtils.getAwardEPin(lsUserOrgType, lsUserOrg, lsPartialUoDenom);
		generateJSONForTypeHead(aoResponse, liMinLength, loArrayList, lsPartialUoDenom);
	}

	/**
	 * This method will be executed when a user type anything in the typeahead
	 * text box. it will return the list of ContractAwardEPin
	 * @param aoRequest Servlet request object
	 * @param aoResponse Servlet response object
	 * @throws ApplicationException
	 * @throws ServletException
	 * @throws IOException
	 */
	private void getContractAwardEPin(HttpServletRequest aoRequest, HttpServletResponse aoResponse)
			throws ApplicationException, ServletException, IOException
	{
		final int liMinLength = 3;
		List<String> loArrayList = new ArrayList<String>();
		String lsPartialUoDenom = aoRequest.getParameter("query");
		String lsUserOrg = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG);
		String lsUserOrgType = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE);
		loArrayList = FileNetOperationsUtils.getContractAwardEPin(lsUserOrgType, lsUserOrg, lsPartialUoDenom);
		generateJSONForTypeHead(aoResponse, liMinLength, loArrayList, lsPartialUoDenom);
	}

	/**
	 * This method will be executed when a user type anything in the typeahead
	 * text box. it will return the list of SharedWith
	 * @param aoRequest Servlet request object
	 * @param aoResponse Servlet response object
	 * @throws ApplicationException
	 * @throws ServletException
	 * @throws IOException
	 */
	private void getSharedWith(HttpServletRequest aoRequest, HttpServletResponse aoResponse) throws Exception
	{
		final int liMinLength = 3;
		List<String> loArrayList = new ArrayList<String>();
		List<String> loSharedArrayList = new ArrayList<String>();
		String lsPartialUoDenom = aoRequest.getParameter("query");
		String lsUserOrg = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG);
		String lsUserOrgType = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE);
		if (null != lsUserOrgType && !lsUserOrgType.isEmpty()
				&& lsUserOrgType.equalsIgnoreCase(HHSR5Constants.USER_CITY))
		{
			lsUserOrg = lsUserOrgType;
		}
		P8UserSession loUserSession = (P8UserSession) aoRequest.getSession().getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT);
		loArrayList = FileNetOperationsUtils.getSharedWith(lsPartialUoDenom, loUserSession, aoRequest);
		TreeSet<String> loTreeSet = new TreeSet<String>();
		loTreeSet.addAll(loArrayList);
		Iterator<String> loItr = loTreeSet.iterator();
		while (loItr.hasNext())
		{
			String lsValue = loItr.next();
			if (lsValue.toLowerCase().contains(lsPartialUoDenom.toLowerCase()))
			{
				loSharedArrayList.add(lsValue);
			}
		}
		generateJSONForTypeHead(aoResponse, liMinLength, loSharedArrayList, lsPartialUoDenom);
	}

	/**
	 * @param aoResponse
	 * @param aiMinLength
	 * @param aoArrayList
	 * @param lsData
	 * @throws IOException
	 * @throws ApplicationException
	 */
	private void generateJSONForTypeHead(HttpServletResponse aoResponse, final int aiMinLength,
			List<String> aoArrayList, String lsData) throws IOException, ApplicationException
	{
		if ((lsData != null) && (lsData.length() >= aiMinLength))
		{
			final PrintWriter loOut = aoResponse.getWriter();
			try
			{
				aoResponse.setContentType(HHSConstants.APPLICATION_JSON);
				final String lsOutputJSONaoResponse = this.generateDelimitedResponse(aoArrayList, lsData, aiMinLength)
						.toString().trim();
				loOut.print(lsOutputJSONaoResponse);
			}
			catch (final Exception aoExp)
			{
				LOG_OBJECT.Error("Exception occurred while searching shared with:", aoExp);
			}
			finally
			{
				BaseControllerUtil.closingPrintWriter(loOut);
			}
		}
	}

	/**
	 * method for generate Delimited Response
	 * @param aoInputList
	 * @param aoPartialSearchTerms
	 * @param aiMinLength
	 * @param lsData
	 * @return StringBuffer loOutputBuffer
	 */
	final StringBuffer generateDelimitedResponse(final List aoInputList, String aoPartialSearchTerms,
			final int aiMinLength)
	{
		final StringBuffer loOutputBuffer = new StringBuffer();
		aoPartialSearchTerms = StringEscapeUtils.escapeJavaScript(aoPartialSearchTerms);
		loOutputBuffer.append("{");
		loOutputBuffer.append("\"query\":\"");
		loOutputBuffer.append(aoPartialSearchTerms);
		loOutputBuffer.append("\",");
		loOutputBuffer.append("\"suggestions\":");
		loOutputBuffer.append(HHSUtilServlet.SQUARE_BRAC_BEGIN);
		for (int liCount = 0; liCount < aoInputList.size(); liCount++)
		{
			String loDocTypeiterator = (String) aoInputList.get(liCount);
			if (null != loDocTypeiterator)
			{
				if ((loDocTypeiterator.length() >= aiMinLength))
				{
					loOutputBuffer.append(HHSUtilServlet.DOUBLE_QUOTES);
					loOutputBuffer.append(loDocTypeiterator.replace("\"", HHSUtilServlet.SINGLE_QUOTE));
					loOutputBuffer.append(HHSUtilServlet.DQUOTES_COMMA);
				}
			}
		}
		if (loOutputBuffer.lastIndexOf(HHSUtilServlet.COMMA) == loOutputBuffer.length() - 1)
		{
			loOutputBuffer.deleteCharAt(loOutputBuffer.lastIndexOf(HHSUtilServlet.COMMA));
		}
		loOutputBuffer.append("],");
		loOutputBuffer.append("\"data\":");
		loOutputBuffer.append(HHSUtilServlet.SQUARE_BRAC_BEGIN);
		for (int liCount = 0; liCount < aoInputList.size(); liCount++)
		{
			String loDocTypeiterator = (String) aoInputList.get(liCount);
			if (null != loDocTypeiterator)
			{
				loOutputBuffer.append(HHSUtilServlet.DOUBLE_QUOTES);
				loOutputBuffer.append(loDocTypeiterator.replace("\"", HHSUtilServlet.SINGLE_QUOTE));
				loOutputBuffer.append(HHSUtilServlet.DQUOTES_COMMA);
			}
		}
		if (loOutputBuffer.lastIndexOf(HHSUtilServlet.COMMA) == loOutputBuffer.length() - 1)
		{
			loOutputBuffer.deleteCharAt(loOutputBuffer.lastIndexOf(HHSUtilServlet.COMMA));
		}
		loOutputBuffer.append("]");
		loOutputBuffer.append("}");
		return loOutputBuffer;
	}

	/**
	 * method for generate Delimited Response For Procurement
	 * @param aoInputList
	 * @param aoPartialSearchTerms
	 * @param aiMinLength
	 * @return StringBuffer loOutputBuffer
	 */
	final StringBuffer generateDelimitedResponseForProcurement(final List<ExtendedDocument> aoInputList,
			String aoPartialSearchTerms, final int aiMinLength)
	{
		final StringBuffer loOutputBuffer = new StringBuffer();
		aoPartialSearchTerms = StringEscapeUtils.escapeJavaScript(aoPartialSearchTerms);
		loOutputBuffer.append("{");
		loOutputBuffer.append("\"query\":\"");
		loOutputBuffer.append(aoPartialSearchTerms);
		loOutputBuffer.append("\",");
		loOutputBuffer.append("\"suggestions\":");
		loOutputBuffer.append(HHSUtilServlet.SQUARE_BRAC_BEGIN);
		for (int liCount = 0; liCount < aoInputList.size(); liCount++)
		{
			ExtendedDocument loDocTypeiterator = (ExtendedDocument) aoInputList.get(liCount);
			if ((loDocTypeiterator.getProcurementTitle().length() >= aiMinLength))
			{
				loOutputBuffer.append(HHSUtilServlet.DOUBLE_QUOTES);
				loOutputBuffer.append(loDocTypeiterator.getProcurementTitle()
						.replace("\"", HHSUtilServlet.SINGLE_QUOTE));
				loOutputBuffer.append(HHSUtilServlet.DQUOTES_COMMA);
			}
		}
		if (loOutputBuffer.lastIndexOf(HHSUtilServlet.COMMA) == loOutputBuffer.length() - 1)
		{
			loOutputBuffer.deleteCharAt(loOutputBuffer.lastIndexOf(HHSUtilServlet.COMMA));
		}
		loOutputBuffer.append("],");
		loOutputBuffer.append("\"data\":");
		loOutputBuffer.append(HHSUtilServlet.SQUARE_BRAC_BEGIN);
		for (int liCount = 0; liCount < aoInputList.size(); liCount++)
		{
			ExtendedDocument loDocTypeiterator = (ExtendedDocument) aoInputList.get(liCount);
			loOutputBuffer.append(HHSUtilServlet.DOUBLE_QUOTES);
			loOutputBuffer.append(loDocTypeiterator.getProcurementId().replace("\"", HHSUtilServlet.SINGLE_QUOTE));
			loOutputBuffer.append(HHSUtilServlet.DQUOTES_COMMA);
		}
		if (loOutputBuffer.lastIndexOf(HHSUtilServlet.COMMA) == loOutputBuffer.length() - 1)
		{
			loOutputBuffer.deleteCharAt(loOutputBuffer.lastIndexOf(HHSUtilServlet.COMMA));
		}
		loOutputBuffer.append("]");
		loOutputBuffer.append("}");
		return loOutputBuffer;
	}

	/**
	 * This method will be executed when a user type anything and update
	 * Dictionary Data
	 * @param updateDictionaryData String
	 * @param loWordToDictionary String
	 * @return String
	 */
	private String updateDictionaryData(String loCacheObject, String loWordToDictionary)
	{
		int liLineIndex = loCacheObject.indexOf("\n");
		int liTotalWord = Integer.parseInt(loCacheObject.substring(0, liLineIndex - 1));
		loCacheObject = loCacheObject.replace(String.valueOf(liTotalWord), String.valueOf(liTotalWord + 1));
		return loCacheObject.concat(loWordToDictionary + "\n");
	}

	/**
	 * Added in R5 This static method provides the File net session.
	 * 
	 * @return P8Usersession bean object with all details to create P8Session
	 * @throws ApplicationException if any exception occurred
	 */

	@SuppressWarnings("unchecked")
	public void exportTaskListForAgency(HttpServletRequest aoRequest, HttpServletResponse aoResponse,
			Channel aoChannel, P8UserSession aoUserSession, List<String> aoTaskTypeList, String asUserOrg)
	{

		try
		{
			List<AgencyTaskBean> loAgencyTaskBeanList;
			StringBuffer loHeader = new StringBuffer();
			AgencyTaskBean aoAgencyTaskBean = new AgencyTaskBean();
			HashMap<String, Object> loFilterProp = new HashMap<String, Object>();
			Iterator loHeaderListItr = HHSR5Constants.HEADER_FOR_TASK_LIST_EXPORT_ACCO.iterator();
			while (loHeaderListItr.hasNext())
			{
				loHeader.append(loHeaderListItr.next());
				if (loHeaderListItr.hasNext())
				{
					loHeader.append(HHSConstants.COMMA);
				}
			}
			loFilterProp.put(HHSR5Constants.PROPERTY_PE_AGENCY_ID, asUserOrg);
			loFilterProp.put(HHSR5Constants.PROPERTY_PE_TASK_TYPE, aoTaskTypeList);
			aoAgencyTaskBean.setAgencyId(asUserOrg);
			aoAgencyTaskBean.setFilterProp(loFilterProp);
			aoAgencyTaskBean.setOrderBy("\"LaunchDate\" DESC, lower(\"TaskType\") ASC");
			aoAgencyTaskBean.setTaskNameList(aoTaskTypeList);
			aoChannel.setData(HHSConstants.AO_FILENET_SESSION, aoUserSession);
			aoChannel.setData(HHSConstants.AGENCY_TASK_BEAN, aoAgencyTaskBean);
			HHSTransactionManager.executeTransaction(aoChannel, HHSR5Constants.FETCH_AGENCY_TASK_LIST_EXPORT,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			loAgencyTaskBeanList = (List<AgencyTaskBean>) aoChannel.getData(HHSConstants.AGENCY_TASK_LIST);
			Iterator<AgencyTaskBean> loListItr = loAgencyTaskBeanList.iterator();
			StringBuffer loStringBuff = new StringBuffer();
			while (loListItr.hasNext())
			{
				AgencyTaskBean loBean = (AgencyTaskBean) loListItr.next();
				loStringBuff.append(loBean.toStringForExport());
				if (loListItr.hasNext())
				{
					loStringBuff.append(HHSR5Constants.LINE_SEPRATOR);
				}
			}
			aoResponse.setContentType(HHSR5Constants.TEXT_CSV);
			DateFormat dateFormat = new SimpleDateFormat("MMddyyyy-HHmmss");
			Date date = new Date();
			aoResponse.setHeader(HHSR5Constants.BUSINESS_APPLICATION_SUB_SECTION_CONTENT_DISPOSITION,
					"attachment; FileName=" + HHSR5Constants.TASK_EXPORT_FILE_NAME + HHSR5Constants.UNDERSCORE
							+ asUserOrg + HHSR5Constants.UNDERSCORE + dateFormat.format(date)
							+ HHSR5Constants.CSV_CONSTANT);
			loHeader.append(HHSR5Constants.LINE_SEPRATOR);
			loHeader.append(loStringBuff);
			String outputResult = loHeader.toString();
			PrintWriter loPrntWrter = aoResponse.getWriter();
			loPrntWrter.write(outputResult);
			loPrntWrter.flush();
			loPrntWrter.close();
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("IOException occurred while docTypes:", aoExp);
		}
		catch (final Exception aoExp)
		{
			LOG_OBJECT.Error("Exception occurred while searching docTypes:", aoExp);
		}
	}

	/**
	 * This method will be executed when a user type anything in the typeahead
	 * text box. it will return the list of AmendmentEpin
	 * @param aoRequest Servlet request object
	 * @param aoResponse Servlet response object
	 * @throws ApplicationException
	 * @throws ServletException
	 * @throws IOException
	 */
	private void getAmendmentEpin(HttpServletRequest aoRequest, HttpServletResponse aoResponse)
			throws ApplicationException, ServletException, IOException
	{
		final int liMinLength = 3;
		List<String> loArrayList = new ArrayList<String>();
		String lsPartialUoDenom = aoRequest.getParameter("query");
		String lsUserOrg = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG);
		String lsUserOrgType = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE);
		loArrayList = FileNetOperationsUtils.getAmendmentEpin(lsUserOrgType, lsUserOrg, lsPartialUoDenom);
		generateJSONForTypeHead(aoResponse, liMinLength, loArrayList, lsPartialUoDenom);
	}
	
	// Start QC9401 R 8.5.0 - generates Excel RFP Report file and download it for user
	/**
	 * This method is used to create XSLS RFP report
	 *  and redirect it to daeja viewer
	 * @param aoRequest http servlet request object
	 * @param aoResponse http servlet response object
	 * @return 
	 * @return Input Stream object
	 * @throws ApplicationException application exception object
	 * @throws IOException IO exception object
	 * @throws ServletException Servlet Exception Object
	 */
	private void getRfpReport(HttpServletRequest aoRequest, HttpServletResponse aoResponse)
				throws ApplicationException, IOException, ServletException
	{
		LOG_OBJECT.Debug("getRfpReport method ");
		String lsProcurementId = aoRequest.getParameter(HHSConstants.PROCUREMENT_ID);
		String xslsFileName = aoRequest.getParameter(HHSR5Constants.XSLS_FILE_NAME);
		LOG_OBJECT.Debug("XSLS_FILE_NAME :: "+xslsFileName);
		LOG_OBJECT.Debug("ProcurementId :: "+lsProcurementId);
		OutputStream lsOutputStream = null;
		PrintWriter loPrntWrter = null;
		
		try
		{
			int loLength = 0;
			Channel loChannelObj = new Channel();
			loChannelObj.setData("asProcurementId", lsProcurementId);
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.GET_RFP_REPORT_DATA);
			
			List<ProposalReportBean> rfpReportDataLst = (List<ProposalReportBean>) loChannelObj.getData("loProcurementList");
						
			if(rfpReportDataLst!=null && !rfpReportDataLst.isEmpty())
			{   
				lsOutputStream = aoResponse.getOutputStream();
				XSSFWorkbook workbook = createXlslFile(rfpReportDataLst); 
				aoRequest.setAttribute("workbook", workbook);
							
				aoResponse.setContentType("application/vnd.ms-excel"); 
				/** [Start] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
				/*aoResponse.setHeader("Content-Disposition",
						"attachment;FileName=\""  + xslsFileName + "\"");*/
				aoResponse.setHeader("Content-Disposition",
						"attachment;FileName=\""  +  HHSUtil.sanitizeCarriageReturns(xslsFileName) + "\"");
				/** [End] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
				workbook.write(lsOutputStream);
				lsOutputStream.flush();
				lsOutputStream.close();
			}
			else
			{
				loPrntWrter = aoResponse.getWriter();
				loPrntWrter.write("FilenotFound");
				loPrntWrter.flush();
				loPrntWrter.close();
			}
		}
		catch (Exception aoExp)
		{   LOG_OBJECT.Error("ApplicationException occured while getting downloading XSLS.", aoExp);
			//throw aoExp;
		}
		finally
		{   
			if (null != lsOutputStream)
			{
				lsOutputStream.close();
			}
			if (null != loPrntWrter)
			{
				loPrntWrter.close();
			}
		}
		
	}
	
	
	   private XSSFWorkbook  createXlslFile( List<ProposalReportBean> rfpReportDataLst ) 
	    throws ApplicationException 
	    {    	      
	    	XSSFWorkbook workbook = new XSSFWorkbook();
	    	XSSFSheet sheet = workbook.createSheet("RFP Report");
	    	
	    	int rowCount = 0;
	    	List<String> columns = new ArrayList<String>();
	    	columns.add("Procurement Name");
	    	columns.add("Competition Pool");
	    	columns.add("Provider Name");
	    	columns.add("Site Name");
	    	columns.add("Site Address 1");
	    	columns.add("Site Address 2");
	    	columns.add("Site City");
	    	columns.add("Site State");
	    	columns.add("Site Zip");
	    	columns.add("Provider EIN");
	    	columns.add("Proposal Status");
	    	columns.add("Proposal ID");
	    	columns.add("Proposal Title");
	    	columns.add("Total Funding Request");
	    	columns.add("Question 1 Answer");
	    	columns.add("Question 2 Answer");
	    	columns.add("Question 3 Answer");
	    	columns.add("Question 4 Answer");
	    	columns.add("Question 5 Answer");
	    	columns.add("Question 6 Answer");
	    	columns.add("Question 7 Answer");
	    	columns.add("Question 8 Answer");
	    	columns.add("Question 9 Answer");
	    	columns.add("Question 10 Answer");
	    	columns.add("Question 11 Answer");
	    	columns.add("Question 12 Answer");
	    	columns.add("Question 13 Answer");
	    	columns.add("Question 14 Answer");
	    	columns.add("Question 15 Answer");
	    	columns.add("Question 16 Answer");
	    	columns.add("Question 17 Answer");
	    	columns.add("Question 18 Answer");
	    	columns.add("Provider Contact Name");
	    	columns.add("Provider Contact Email");
	    	columns.add("Provider Contact Phone");
	    	columns.add("Number of Site");
	    	columns.add("Community District of Proposal");
	    	columns.add("Created Date : time");
	    	columns.add("Last Modified Date : time");
	    	columns.add("Last Modified By Name");
	    	columns.add("Last Modified By Email");
	    	columns.add("Last Modified By Phone");
	    	columns.add("Spare");
	    	    	
	    	createHeaderRow(columns, sheet);
	    	
	    	for (ProposalReportBean loBin : rfpReportDataLst) 
	    	{
	            Row row = sheet.createRow(++rowCount);
	            writeBook(loBin, row);
	        }
	    	
	    	return workbook;
		}
	    
	    private void writeBook(ProposalReportBean aBin, Row row) 
	    {
	        Cell cell = row.createCell(0);
	        cell.setCellValue(aBin.getProcurementTitle());
	     
	        cell = row.createCell(1);
	        cell.setCellValue(aBin.getCompetitionPool());
	     
	        cell = row.createCell(2);
	        cell.setCellValue(aBin.getProviderName());
	        
	        cell = row.createCell(3);
	        cell.setCellValue(aBin.getSiteName());
	     
	        cell = row.createCell(4);
	        cell.setCellValue(aBin.getSiteAddress1());
	     
	        cell = row.createCell(5);
	        cell.setCellValue(aBin.getSiteAddress2());

	        cell = row.createCell(6);
	        cell.setCellValue(aBin.getSiteCity());
	        
	        cell = row.createCell(7);
	        cell.setCellValue(aBin.getSiteState());
	          
	        cell = row.createCell(8);
	        cell.setCellValue(aBin.getSiteZip());
	     
	        cell = row.createCell(9);
	        cell.setCellValue(aBin.getEpin());

	        cell = row.createCell(10);
	        cell.setCellValue(aBin.getProposalStatus());
	     
	        cell = row.createCell(11);
	        cell.setCellValue(aBin.getProposalId());
	     
	        cell = row.createCell(12);
	        cell.setCellValue(aBin.getProposalTitle());

	        cell = row.createCell(13);
	        cell.setCellValue(aBin.getTotalFundingRequest());
	        
	        cell = row.createCell(14);
	        cell.setCellValue(aBin.getQuestion1Answer());
	        
	        cell = row.createCell(15);
	        cell.setCellValue(aBin.getQuestion2Answer());
	        
	        cell = row.createCell(16);
	        cell.setCellValue(aBin.getQuestion3Answer());
	        
	        cell = row.createCell(17);
	        cell.setCellValue(aBin.getQuestion4Answer());
	        
	        cell = row.createCell(18);
	        cell.setCellValue(aBin.getQuestion5Answer());
	        
	        cell = row.createCell(19);
	        cell.setCellValue(aBin.getQuestion6Answer());
	        
	        cell = row.createCell(20);
	        cell.setCellValue(aBin.getQuestion7Answer());
	        
	        cell = row.createCell(21);
	        cell.setCellValue(aBin.getQuestion8Answer());
	        
	        cell = row.createCell(22);
	        cell.setCellValue(aBin.getQuestion9Answer());
	        
	        cell = row.createCell(23);
	        cell.setCellValue(aBin.getQuestion10Answer());
	        
	        cell = row.createCell(24);
	        cell.setCellValue(aBin.getQuestion11Answer());
	        
	        cell = row.createCell(25);
	        cell.setCellValue(aBin.getQuestion12Answer());
	        
	        cell = row.createCell(26);
	        cell.setCellValue(aBin.getQuestion13Answer());
	        
	        cell = row.createCell(27);
	        cell.setCellValue(aBin.getQuestion14Answer());
	        
	        cell = row.createCell(28);
	        cell.setCellValue(aBin.getQuestion15Answer());
	        
	        cell = row.createCell(29);
	        cell.setCellValue(aBin.getQuestion16Answer());
	        
	        cell = row.createCell(30);
	        cell.setCellValue(aBin.getQuestion17Answer());
	        
	        cell = row.createCell(31);
	        cell.setCellValue(aBin.getQuestion18Answer());

	        cell = row.createCell(32);
	        cell.setCellValue(aBin.getProviderContactName());
	        
	        cell = row.createCell(33);
	        cell.setCellValue(aBin.getProviderContactEmail());
	        
	        cell = row.createCell(34);
	        cell.setCellValue(aBin.getProviderContactPhone());

	        cell = row.createCell(35);
	        cell.setCellValue(aBin.getNumberOfSites());

	        cell = row.createCell(36);
	        cell.setCellValue(aBin.getCommunityDistinct());
	        
	        cell = row.createCell(37);
	        cell.setCellValue(aBin.getCreatedDate());
	        
	        cell = row.createCell(38);
	        cell.setCellValue(aBin.getLastModifiedDate());
	        
	        cell = row.createCell(39);
	        cell.setCellValue(aBin.getLastModifiedByName());
	        
	        cell = row.createCell(40);
	        cell.setCellValue(aBin.getLastModifiedByEmail());
	        
	        cell = row.createCell(41);
	        cell.setCellValue(aBin.getLastModifiedByPhone());
	        
	        cell = row.createCell(42);
	        cell.setCellValue(aBin.getSpare());
	        
	    }
	    
	     
	    private void createHeaderRow(List<String> columns, XSSFSheet sheet )
	    {
	        XSSFFont font = sheet.getWorkbook().createFont();
	        font.setFontName("Calibri");
	        font.setBold(true);
	        CellStyle style = sheet.getWorkbook().createCellStyle();
	        style.setFont(font);
	        //style.setAlignment(CellStyle.ALIGN_CENTER);
	        style.setFillForegroundColor ( HSSFColor.GREY_25_PERCENT.index );
	        style.setFillPattern ( PatternFormatting.SOLID_FOREGROUND );
	       
	        Row header = sheet.createRow(0);
	        for ( int i = 0; i < columns.size (); i++ )
	        {
	        	Cell cell = header.createCell(i);
	        	cell.setCellValue (columns.get(i));
	            cell.setCellStyle (style);
	        }
	    }
	
	// End QC9401 R 8.5.0 - generates XMl RFP Report file and download it for user

		/*[Start] R9.3.2 QC9665*/
	    public boolean checkPermission(HttpServletRequest aoRequest, HttpServletResponse aoResponse, String aoDocumentId)  
	    {
	    	
			String  loOrgId = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG);
			String lsUserId = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID);
			String lsOrganizType = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE);
			
			//String lsPermissionLevel = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_PERMISSION_LEVEL);
			//String lsPermissionType = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_PERMISSION_TYPE);

			LOG_OBJECT.Info("Entered HHSUtilServlet.checkPermission() with Dic Id::"
					+ aoDocumentId + "    lsOrganizType:" + lsOrganizType + "    loOrgId:" + loOrgId  +  "     User ID:" + lsUserId  
					);

	    	if( lsOrganizType.equalsIgnoreCase(HHSConstants.USER_CITY)
	    			|| lookupDocList( aoRequest, aoResponse , aoDocumentId )
	    			|| helpPageDocList( aoRequest, aoResponse , aoDocumentId )
		    		|| lookupExtendedDocList( aoRequest, aoResponse , aoDocumentId ) 
		    		){
		    		return true;
		    	}
			P8UserSession loUserSession = (P8UserSession) aoRequest.getSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT);
			
			Channel loChannel = new Channel();
			loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
			loChannel.setData(HHSConstants.DOC_ID, aoDocumentId);
			loChannel.setData(HHSConstants.USER_ORG_ID, loOrgId);
			loChannel.setData(HHSConstants.ORGTYPE, lsOrganizType);
			
			Boolean loDocumentPermission = null;
			try {
//				LOG_OBJECT.Debug( "######[Teace]1000----before  :  checkPermission "  );
				TransactionManager.executeTransaction(loChannel, ApplicationConstants.CHECK_PERMISION_FOR_DOC, HHSR5Constants.TRANSACTION_ELEMENT_R5);
				 loDocumentPermission = (Boolean) loChannel.getData("hasPermision");
//				LOG_OBJECT.Debug( "######[Teace]1000----After  :   "  + loDocumentPermission );
			} catch (ApplicationException e) {
				// TODO Auto-generated catch block
				LOG_OBJECT.Error( "######[Teace]1000----ERROR at checkPermission :   "  + loDocumentPermission );
				e.printStackTrace();
				return false;
			}
			if(loDocumentPermission == null){
				return false;
			}
			return loDocumentPermission.booleanValue();
	    }
	    
	    
	    public boolean lookupDocList(HttpServletRequest aoRequest, HttpServletResponse aoResponse, String aoDocumentId)  {
			boolean doc_in_list = false; 

			/*[Start]  R9.6.2  QC9696  added for exception.*/
	    	List<Object> loDocBeanList = (List<Object>) aoRequest.getSession().getAttribute(ApplicationConstants.SESSION_DOCUMENT_LIST);
	    	if(null == loDocBeanList ){
    			return false;
	    	} else {
	    		if(loDocBeanList.isEmpty() && !(loDocBeanList.get(0) instanceof  Document) ){
	    			return false;
	    		}
	    	}
	    	/*[End]  R9.6.2  QC9696*/
			List<Document> aoDocumentBeanList = (List<Document>) aoRequest.getSession().getAttribute(ApplicationConstants.SESSION_DOCUMENT_LIST);

			if (null != aoDocumentBeanList && !aoDocumentBeanList.isEmpty())
			{
				for (Iterator<Document> loDocumentItr = aoDocumentBeanList.iterator(); loDocumentItr.hasNext();)
				{
					Document loDocumentBean = (Document) loDocumentItr.next();

					if (null != loDocumentBean.getDocumentId() && !loDocumentBean.getDocumentId().isEmpty())
					{
						if(loDocumentBean.getDocumentId().equalsIgnoreCase(aoDocumentId))
						{
							LOG_OBJECT.Info("DOC EXISTS in DOC List!!! :: "+aoDocumentId);
							doc_in_list = true;
							return true;
						}
					}
				}
			}
			
			return doc_in_list;
	    }

	    /*[Start]  R9.6.2  QC9696*/
	    public boolean helpPageDocList(HttpServletRequest aoRequest, HttpServletResponse aoResponse, String aoDocumentId)  {
			boolean doc_in_list = false; 
			List<Document> loDocLookupList = (List<Document>) aoRequest.getSession().getAttribute(ApplicationConstants.HELP_DOCUMENT_LIST);
			if (null != loDocLookupList && !loDocLookupList.isEmpty())
			{
				for (Iterator<Document> loDocItr = loDocLookupList.iterator(); loDocItr.hasNext();)
				{
					Document loDocBean = (Document) loDocItr.next();

					if (null != loDocBean.getDocumentId() && !loDocBean.getDocumentId().isEmpty())
					{
						if(loDocBean.getDocumentId().equalsIgnoreCase(aoDocumentId))
						{
							doc_in_list = true;
							return true;
						}
					}
				}
			}
			
			/*[Start]  R9.7.1  QC9716*/
			loDocLookupList = (List<Document>) aoRequest.getSession().getAttribute(ApplicationConstants.SAMPLE_DOCUMENTS_LIST);
	    	LOG_OBJECT.Info("#####  DOC_List1234 !!! :: "+loDocLookupList);
			if (null != loDocLookupList && !loDocLookupList.isEmpty())
			{
				for (Iterator<Document> loDocItr = loDocLookupList.iterator(); loDocItr.hasNext();)
				{
					Document loDocBean = (Document) loDocItr.next();

					if (null != loDocBean.getDocumentId() && !loDocBean.getDocumentId().isEmpty())
					{
						if(loDocBean.getDocumentId().equalsIgnoreCase(aoDocumentId))
						{
							doc_in_list = true;
							return true;
						}
					}
				}
			}
			/*[End]  R9.7.1  QC9716*/
			
			return doc_in_list;
	    }
	    /*[End]  R9.6.2  QC9696*/
	    public boolean lookupExtendedDocList(HttpServletRequest aoRequest, HttpServletResponse aoResponse, String aoDocumentId)  {

			List<ExtendedDocument> aoDocumentBeanList = (List<ExtendedDocument>) aoRequest.getSession().getAttribute(ApplicationConstants.SESSION_EXTENDED_DOCUMENT_LIST);
			boolean doc_in_list = false; 
			//LOG_OBJECT.Info("#####  DOC List!!! :: "+aoDocumentBeanList);

			if (null != aoDocumentBeanList && !aoDocumentBeanList.isEmpty())
			{
				for (Iterator<ExtendedDocument> loDocumentItr = aoDocumentBeanList.iterator(); loDocumentItr.hasNext();)
				{
					ExtendedDocument loDocumentBean = (ExtendedDocument) loDocumentItr.next();

					if (null != loDocumentBean.getDocumentId() && !loDocumentBean.getDocumentId().isEmpty())
					{
						if(loDocumentBean.getDocumentId().equalsIgnoreCase(aoDocumentId))
						{
							doc_in_list = true;
							return true;
						}
					}
				}
			}

			return doc_in_list;
	    }

		/*[End] R9.3.2 QC9665*/
}