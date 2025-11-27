package com.nyc.hhs.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.TaxonomyTree;
import com.nyc.hhs.model.UserThreadLocal;
import com.nyc.hhs.util.CommonUtil;

/**
 * This Servlet is for taxonomy re-cache. It read data from database and create
 * JDOM and puts in cache.
 * 
 */
@SuppressWarnings("serial")
public class TaxonomyServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
	private static final LogInfo LOG_OBJECT = new LogInfo(TaxonomyServlet.class);
	private static final String TAXONOMY_RE_CACHE_IS_IN_PROCESS_1 = "Unable to make the update as the Taxonomy Cache is being rebuilt, ";
	private static final String TAXONOMY_RE_CACHE_IS_IN_PROCESS_2 = "please re-submit further updates after 8 minutes.";
	private static final String BRANCH_HAS_MULTIPLE_EVIDENCE = " branch has multiple evidence levels.";
	private static final String BRANCH_HAS_MULTIPLE_EVIDENCE_PLURAL = " branches have multiple evidence levels.";
	private static final String TAXONOMY = "Taxonomy";
	private static final String TAXONOMY_RECACHE = "Taxonomy Recache";
	private static final String RECACHE_PASSED_STATUS = "The entire taxonomy has been successfully re-cached";
	private static final String RECACHE_FAILED_STATUS = "The re-caching of the taxonomy was not successful. Please try again.";
	private static final String RECACHE_EVIDENCE_FAILED_1 = "You cannot re-cache the taxonomy because the ";
	private static final String RECACHE_EVIDENCE_FAILED_2_1 = "branch is active but does not require evidence. The evidence flag must be added to ";
	private static final String RECACHE_EVIDENCE_FAILED_2_2 = "an item on the branch or the parent must be made inactive before continuing.";
	private static final String RECACHE_EVI_FAILED_2_PLURAL_1 = "branches are active but does not require evidence. The evidence flag must be added";
	private static final String RECACHE_EVI_FAILED_2_PLURAL_2 = " to an item on the branch or the parent must be made inactive before continuing.";

	/**
	 * This is the constructor method
	 * 
	 */
	public TaxonomyServlet()
	{
		super();
	}

	/**
	 * This method is to call do-post method for re-cache processing
	 * 
	 * @param aoRequest a HttpServletRequest request object to get session
	 *            attribute values
	 * @param aoResponse a HttpServletRequest response object
	 */
	protected void doGet(HttpServletRequest aoRequest, HttpServletResponse aoResponse) throws ServletException,
			IOException
	{
		this.doPost(aoRequest, aoResponse);
	}

	/**
	 * This method performs : 1. evidence validation check 2. taxonomy batch
	 * processing for evidence to non evidence and for approval to non approval
	 * 3. emptying taxonomy transaction record 4. put taxonomy DOM into cache
	 * 
	 * @param aoRequest a HttpServletRequest request object to get session
	 *            attribute values
	 * @param aoResponse a HttpServletRequest response object
	 * @throws ServletException,IOException
	 * 
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	protected void doPost(HttpServletRequest aoRequest, HttpServletResponse aoResponse) throws ServletException,
			IOException
	{
		String lsTransactionStatusMsg = "", lsNextAction = aoRequest.getParameter("next_action");
		aoResponse.setContentType("text/html");
		final StringBuffer loOutputBuffer = new StringBuffer();
		final PrintWriter loOut = aoResponse.getWriter();
		String lsUserId = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID);
		UserThreadLocal.setUser(lsUserId);
		try
		{
			boolean lbEvidenceValidationCheck = false;
			String lsEnvironmentType = (String) BaseCacheManagerWeb.getInstance().getCacheObject(
					ApplicationConstants.ENVIROMENT_TYPE);
			if (null != lsNextAction && lsNextAction.equalsIgnoreCase("recache"))
			{
				if (null != lsEnvironmentType && checkRecacheInProgress())
				{
					lsTransactionStatusMsg = "Failure#" + TaxonomyServlet.TAXONOMY_RE_CACHE_IS_IN_PROCESS_1
							+ TAXONOMY_RE_CACHE_IS_IN_PROCESS_2;
				}
				else
				{
					List<String> loErroneousElementId = new ArrayList();
					List<String> loEvidenceCountList = new ArrayList();
					evidenceValidationCheck(loErroneousElementId, loEvidenceCountList);
					if (loErroneousElementId.isEmpty())
					{
						lbEvidenceValidationCheck = true;
					}
					else
					{
						lbEvidenceValidationCheck = false;
						int liZeroCounter = 0;
						int liMoreCounter = 0;
						for (int liCntr = 0; liCntr < loEvidenceCountList.size(); liCntr++)
						{
							if ("zero".equalsIgnoreCase(loEvidenceCountList.get(liCntr).trim()))
							{
								liZeroCounter++;
							}
							else if ("more".equalsIgnoreCase(loEvidenceCountList.get(liCntr).trim()))
							{
								liMoreCounter++;
							}
						}
						String lsMsg1 = TaxonomyServlet.RECACHE_EVIDENCE_FAILED_2_1
								+ TaxonomyServlet.RECACHE_EVIDENCE_FAILED_2_2;
						String lsMsg2 = TaxonomyServlet.BRANCH_HAS_MULTIPLE_EVIDENCE;
						if (liZeroCounter > 1)
						{
							lsMsg1 = TaxonomyServlet.RECACHE_EVI_FAILED_2_PLURAL_1
									+ TaxonomyServlet.RECACHE_EVI_FAILED_2_PLURAL_2;
						}
						if (liMoreCounter > 1)
						{
							lsMsg2 = TaxonomyServlet.BRANCH_HAS_MULTIPLE_EVIDENCE_PLURAL;
						}
						lsTransactionStatusMsg = "Failure#" + TaxonomyServlet.RECACHE_EVIDENCE_FAILED_1 + "|"
								+ loErroneousElementId + "|" + lsMsg1 + "|" + loEvidenceCountList + "|" + lsMsg2;
					}
					if (lbEvidenceValidationCheck)
					{
						lsTransactionStatusMsg = getLogAuditInfo(aoRequest);
					}
				}
			}
			loOutputBuffer.append(lsTransactionStatusMsg);
			loOut.print(loOutputBuffer.toString());
		}
		catch (ApplicationException loError)
		{
			exceptionMessage(loOutputBuffer, loOut);
			LOG_OBJECT.Error("Error occurred while re-caching taxonomy", loError);
		}
		finally
		{
			if (null != loOut)
			{
				loOut.flush();
				loOut.close();
			}
		}
		UserThreadLocal.unSet();
	}

	/**
	 * @param aoRequest
	 * @return
	 * @throws ApplicationException
	 */
	private String getLogAuditInfo(HttpServletRequest aoRequest) throws ApplicationException
	{
		String lsTransactionStatusMsg;
		String lsUserId;
		Channel loChannelObj;
		lsUserId = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID);
		String lsEmailId = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_EMAIL_ID);
		String lsOrgId = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG);
		String lsUserName = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_NAME);
		Map loMap = new HashMap<String, String>();
		loChannelObj = new Channel();
		loMap.put("userId", lsUserId);
		loMap.put("orgId", lsOrgId);
		loChannelObj.setData("aoUserIdMap", loMap);
		TransactionManager.executeTransaction(loChannelObj, "executeTaxonomyBatchAndReCache");
		loChannelObj = new Channel();
		Map loHM = new HashMap();
		DateFormat loDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Date loDateObj = new Date();
		String lslastModifiedDate = loDateFormat.format(loDateObj);
		loHM.put("orgId", lsOrgId);
		loHM.put("eventName", TaxonomyServlet.TAXONOMY_RECACHE);
		loHM.put("eventType", TaxonomyServlet.TAXONOMY);
		loHM.put("auditDate", lslastModifiedDate);
		loHM.put("userId", lsEmailId);
		loHM.put("data", TaxonomyServlet.TAXONOMY);
		loHM.put("entityType", TaxonomyServlet.TAXONOMY);
		loHM.put("EntityIdentifier", TaxonomyServlet.TAXONOMY);
		loHM.put("entityId", "1");
		loHM.put("providerFlag", "N");
		loChannelObj.setData("aoAuditDetailMap", loHM);
		lsTransactionStatusMsg = "Failure#" + TaxonomyServlet.RECACHE_FAILED_STATUS;
		TransactionManager.executeTransaction(loChannelObj, "insertLogAuditInfo");
		loDateFormat = new SimpleDateFormat("MM/dd/yyyy");
		loDateObj = new Date();
		lslastModifiedDate = loDateFormat.format(loDateObj);
		aoRequest.getSession().setAttribute("lastModifiedDate", lslastModifiedDate);
		aoRequest.getSession().setAttribute("lastModifiedByUser", lsUserName);
		lsTransactionStatusMsg = "success#" + TaxonomyServlet.RECACHE_PASSED_STATUS + "|" + lslastModifiedDate + "|"
				+ lsUserName;
		return lsTransactionStatusMsg;
	}

	/**
	 * @param aoOutputBuffer
	 * @param aoOut
	 */
	private void exceptionMessage(final StringBuffer aoOutputBuffer, final PrintWriter aoOut)
	{
		String lsTransactionStatusMsg;
		lsTransactionStatusMsg = "Failure#" + TaxonomyServlet.RECACHE_FAILED_STATUS;
		aoOutputBuffer.append(lsTransactionStatusMsg);
		aoOut.print(aoOutputBuffer.toString());
	}

	/**
	 * This method performs evidence flag validation and returns Element Id of
	 * erroneous Taxonomy items where evidence flag needs to be changed
	 * @param aoErroneousElementId a ElementID lIst
	 * @param aoEvidenceCountList Evidence count flag list
	 * @return list of String for which evidence validation is failing
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private void evidenceValidationCheck(List aoErroneousElementId, List aoEvidenceCountList)
			throws ApplicationException
	{
		Channel loChannelObj = new Channel();
		TransactionManager.executeTransaction(loChannelObj, "reCacheEvidenceValidation");
		List<TaxonomyTree> loList = (List<TaxonomyTree>) loChannelObj.getData("aoCacheEvidenceValidation");

		Iterator<TaxonomyTree> loIterator = loList.iterator();

		while (loIterator.hasNext())
		{
			TaxonomyTree loTaxonomyTree = loIterator.next();
			aoErroneousElementId.add(loTaxonomyTree.getMsElementid());
			aoEvidenceCountList.add(loTaxonomyTree.getMsEvidenceCount());
		}
	}

	/**
	 * This method is used to check whether Recache In Progress
	 * @return lbRecacheInProgress boolean value for Recache in progress
	 */
	@SuppressWarnings("rawtypes")
	private boolean checkRecacheInProgress()
	{
		boolean lbRecacheInProgress = false;
		String lsCurrentdate = CommonUtil.getCurrentTime();
		String lsLastRechacheTime = "";
		try
		{

			Channel aoChannelObj = new Channel();
			TransactionManager.executeTransaction(aoChannelObj, "getLastRecacheTime");
			lsLastRechacheTime = (String) aoChannelObj.getData("lsLastRecacheTime");

			if (lsLastRechacheTime != null)
			{
				int liTimediff = CommonUtil.minutesDiff(CommonUtil.getItemDate(lsLastRechacheTime),
						CommonUtil.getItemDate(lsCurrentdate));
				if (liTimediff <= 8)
				{
					lbRecacheInProgress = true;
				}
			}
		}
		catch (ApplicationException loAppExc)
		{
			LOG_OBJECT.Error("Error occured in checkRecacheInProgress method", loAppExc);
		}
		catch (Exception loThrExc)
		{
			LOG_OBJECT.Error("Error occured in checkRecacheInProgress method", loThrExc);
		}

		return lbRecacheInProgress;
	}

}