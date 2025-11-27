package com.nyc.hhs.controllers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.jdom.Document;
import org.jdom.Element;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.mvc.AbstractController;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.GeneralAuditBean;
import com.nyc.hhs.model.TaxonomyLinkageBean;
import com.nyc.hhs.model.TaxonomySynonymBean;
import com.nyc.hhs.model.TaxonomyTree;
import com.nyc.hhs.model.UserThreadLocal;
import com.nyc.hhs.util.ApplicationSession;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.PortalUtil;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.TaxonomyDOMUtil;
import com.nyc.hhs.util.XMLUtil;

/**
 * This controller is for the maintenance of taxonomy with major operations
 * like- add, update, delete and re-cache of Taxonomy
 * 
 */

public class MaintenanceTaxonomyController extends AbstractController
{
	private static final LogInfo LOG_OBJECT = new LogInfo(MaintenanceTaxonomyController.class);

	private static final String TAXONOMY_RE_CACHE_IS_IN_PROCESS_1 = "Unable to make the update as the Taxonomy Cache is being rebuilt, ";
	private static final String TAXONOMY_RE_CACHE_IS_IN_PROCESS_2 = "please re-submit further updates after 8 minutes.";
	private static final String TRANSACTION_PASSED_ADD = " was successfully added to the taxonomy";
	private static final String TRANSACTION_FAILED_ADD = " was not successfully added to the taxonomy. Please try again";

	private static final String TRANSACTION_SUCCESS_SAVE = "Your changes have been saved successfully.";
	private static final String TRANSACTION_FAILED_SAVE = "Your changes have not been saved successfully. Please try again";

	private static final String TRANSACTION_PASSED_REMOVE = " was successfully removed from the taxonomy.";
	private static final String TRANSACTION_FAILED_REMOVE = " was not successfully deleted. Please try again.";

	private static final String TRANSACTION_FAILED_DUPLICATE = "You cannot save the taxonomy because there are two items within ";
	private static final String TRANSACTION_FAILED_DUPLICATE_ADD = "You cannot add the taxonomy because there are two items within ";

	private static final String TRANS_FAIL_PARENT_NOT_ACTIVE = "You cannot save this taxonomy with active flag because its parent is inactive ";
	private static final String PROCUREMENT_IS_PLANNED = "Your change cannot be saved at this time. You cannot remove the evidence flag for a service "
			+ "that is currently in use on one or more active procurements in Planned status. Please remove the service from the procurement(s) before "
			+ "removing the evidence flag.";
	private static final String PROCUREMENT_IS_RELEASED = "Your change cannot be saved at this time. You cannot remove the evidence flag for "
			+ "a service that is in use on one or more active procurements in Released status.";

	/**
	 * This method is to render the next page depending on the action,
	 * maintenance taxonomy
	 * 
	 * @param aoRequest to get screen parameters and next page to be displayed
	 * @param aoResponse setting response parameter for JSP variables
	 * @throws ApplicationException
	 */
	@Override
	protected ModelAndView handleRenderRequestInternal(RenderRequest aoRequest, RenderResponse aoResponse)
			throws ApplicationException
	{
		long loStartTime = System.currentTimeMillis();
		PortletSession loPortletSessionThread = aoRequest.getPortletSession();
		String lsUserIdThreadLocal = (String) loPortletSessionThread.getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
		UserThreadLocal.setUser(lsUserIdThreadLocal);
		aoResponse.setContentType("text/html");
		String lsFormPath = null, lsTreeSource = "mainTree";
		ModelAndView loModelAndView = null;
		TaxonomyTree loTaxonomyTreeBean;
		Channel loChannelObj = new Channel();
		lsFormPath = ApplicationConstants.MAINTENANCE_TAXONOMY_ITEM_MAIN;
		try
		{
			if ("landing".equalsIgnoreCase(PortalUtil.parseQueryString(aoRequest, "navigatefrom"))
					|| (aoRequest.getParameter("landing") != null && !"null".equalsIgnoreCase(aoRequest
							.getParameter("landing"))))
			{
				loadTaxonomy(aoRequest, aoResponse, loChannelObj);
			}
			// This condition renders the selectTaxonomyStep1 screen while
			else if (aoRequest.getParameter("selectTaxonomyStep1") != null
					&& "selectTaxonomyStep1".equalsIgnoreCase(aoRequest.getParameter("selectTaxonomyStep1")))
			{
				lsFormPath = "selecttaxonomystep1";
			}
			// This condition renders the selectLocationstep2 screen
			else if (aoRequest.getParameter("selectLocationstep2") != null
					&& "selectLocationstep2".equalsIgnoreCase(aoRequest.getParameter("selectLocationstep2")))
			{
				lsFormPath = "selectLocationstep2";
				String lsTaxonomyType = aoRequest.getParameter("lsTaxonomyType");
				getLocationByElement(aoRequest, aoResponse, lsTaxonomyType, lsTreeSource);
			}
			// This condition renders the namenewitemstep3 screen
			else if (aoRequest.getParameter("namenewitemstep3") != null
					&& "namenewitemstep3".equalsIgnoreCase(aoRequest.getParameter("namenewitemstep3")))
			{
				lsFormPath = "namenewitemstep3";
				if (null != aoRequest.getParameter("duplicateElementName"))
				{
					aoRequest.setAttribute("duplicateElementName", aoRequest.getParameter("duplicateElementName"));
				}
			}
			// This condition renders the confirmselectionstep4 screen
			else if (aoRequest.getParameter("confirmselectionstep4") != null
					&& "confirmselectionstep4".equalsIgnoreCase(aoRequest.getParameter("confirmselectionstep4")))
			{
				lsFormPath = "confirmselectionstep4";
			}
			// This condition renders the taxonomy main page with success
			else if (aoRequest.getParameter("confirmselectionstep5") != null
					&& "confirmselectionstep5".equalsIgnoreCase(aoRequest.getParameter("confirmselectionstep5")))
			{
				lsFormPath = ApplicationConstants.MAINTENANCE_TAXONOMY_ITEM_MAIN;
				lsTreeSource = "mainTree";
				loTaxonomyTreeBean = (TaxonomyTree) ApplicationSession.getAttribute(aoRequest, false,
						"TaxonomyTreeBean");
				aoRequest.setAttribute("TaxonomyTreeBean", loTaxonomyTreeBean);
				aoRequest.setAttribute("newTaxonomyItemAdded", "newTaxonomyItemAdded");
				aoRequest.setAttribute("taxonomyItemId", loTaxonomyTreeBean.getMsElementid());
				aoRequest.setAttribute("taxonomyBranchId", loTaxonomyTreeBean.getMsBranchid());
				aoRequest.setAttribute("taxonomyElementType", loTaxonomyTreeBean.getMsElementType());
				navigateToTaxonomyMain(aoRequest, aoResponse);
			}
			// This condition renders the taxonomy main page with success
			else if (aoRequest.getParameter("deleteTaxonomy") != null)
			{
				lsFormPath = ApplicationConstants.MAINTENANCE_TAXONOMY_ITEM_MAIN;
				navigateToTaxonomyMain(aoRequest, aoResponse);
			}
			// This condition renders the taxonomy detail page for the taxonomy
			else if (aoRequest.getParameter("mainttaxonomyitemdetail") != null
					&& null == aoRequest.getAttribute("landingPage")
					&& ApplicationConstants.MAINTENANCE_TAXONOMY_ITEM_DETAILS.equalsIgnoreCase(aoRequest
							.getParameter("mainttaxonomyitemdetail")))
			{
				lsTreeSource = "linkageTree";
				loTaxonomyTreeBean = (TaxonomyTree) ApplicationSession.getAttribute(aoRequest, false,
						"TaxonomyTreeBean");
				aoRequest.setAttribute("TaxonomyTreeBean", loTaxonomyTreeBean);
				lsFormPath = ApplicationConstants.MAINTENANCE_TAXONOMY_ITEM_DETAILS;
			}
			if (!"".equalsIgnoreCase(aoRequest.getParameter("transactionMessage")))
			{
				aoRequest.setAttribute("transactionStatus", aoRequest.getParameter("transactionStatus"));
				aoRequest.setAttribute("transactionMessage", aoRequest.getParameter("transactionMessage"));
			}
		}
		catch (ApplicationException aoFbAppEx)
		{
			LOG_OBJECT.Error("Error occured in getting taxonomy  Data ", aoFbAppEx);
		}
		loModelAndView = new ModelAndView(lsFormPath);
		long loEndTimeTime = System.currentTimeMillis();
		try
		{
			LOG_OBJECT.Debug("TIME TAKEN for execution of render Method in MaintenanceTaxonomyController = "
					+ (loEndTimeTime - loStartTime));
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Error while execution of render Method in MaintenanceTaxonomyController ", aoEx);
		}
		UserThreadLocal.unSet();
		return loModelAndView;
	}

	/**
	 * This method is called on first time page load to load the taxonomy tree
	 * @param aoRequest to get screen parameters and next action to be performed
	 * @param aoResponse setting response parameter for JSP variables
	 * @param aoChannelObj channel object to send and receive values across
	 *            transaction
	 * @throws ApplicationException
	 */
	private void loadTaxonomy(RenderRequest aoRequest, RenderResponse aoResponse, Channel aoChannelObj)
			throws ApplicationException
	{
		aoRequest.setAttribute("landingPage", "landingPage");
		navigateToTaxonomyMain(aoRequest, aoResponse);
		GeneralAuditBean loGeneralAuditBeanTaxonomy = null;
		loGeneralAuditBeanTaxonomy = getLastUpdatedTaxonomyDetails(aoRequest, aoResponse, aoChannelObj);
		String lsLastModifiedDateTaxonomy = "";
		DateFormat loDateFormatter = new SimpleDateFormat("MM/dd/yyyy");
		if (null == loGeneralAuditBeanTaxonomy)
		{
			loGeneralAuditBeanTaxonomy = new GeneralAuditBean();
			loGeneralAuditBeanTaxonomy.setMsUserid("");
		}
		else
		{
			lsLastModifiedDateTaxonomy = loDateFormatter.format(loGeneralAuditBeanTaxonomy.getMsAuditDate());
		}
		aoRequest.getPortletSession().setAttribute("lastModifiedDateTaxonomy", lsLastModifiedDateTaxonomy,
				PortletSession.APPLICATION_SCOPE);
		aoRequest.getPortletSession().setAttribute("lastModifiedByUserTaxonomy",
				loGeneralAuditBeanTaxonomy.getMsUserName(), PortletSession.APPLICATION_SCOPE);
		GeneralAuditBean loGeneralAuditBeanRecache = null;
		loGeneralAuditBeanRecache = getLastUpdatedRecacheDetails(aoRequest, aoResponse, aoChannelObj);
		String lsLastModifiedDateRecache = "";
		if (null == loGeneralAuditBeanRecache)
		{
			loGeneralAuditBeanRecache = new GeneralAuditBean();
			loGeneralAuditBeanRecache.setMsUserid("");
		}
		else
		{
			lsLastModifiedDateRecache = loDateFormatter.format(loGeneralAuditBeanRecache.getMsAuditDate());
		}
		aoRequest.getPortletSession().setAttribute("lastModifiedDateRecache", lsLastModifiedDateRecache,
				PortletSession.APPLICATION_SCOPE);
		aoRequest.getPortletSession().setAttribute("lastModifiedByUserRecache",
				loGeneralAuditBeanRecache.getMsUserName(), PortletSession.APPLICATION_SCOPE);
	}

	/**
	 * This method decide the execution flow for the maintenance taxonomy
	 * process
	 * 
	 * @param aoRequest to get screen parameters and next action to be performed
	 * @param aoResponse decides the next execution flow
	 * @throws ApplicationException
	 */
	@Override
	protected void handleActionRequestInternal(ActionRequest aoRequest, ActionResponse aoResponse)
			throws ApplicationException
	{
		long loStartTime = System.currentTimeMillis();
		PortletSession loPortletSessionThread = aoRequest.getPortletSession();
		String lsUserIdThreadLocal = (String) loPortletSessionThread.getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
		UserThreadLocal.setUser(lsUserIdThreadLocal);
		String lsTransactionStatusMsg = "", lsTransactionStatus = "", lsDetailPageElementId = aoRequest
				.getParameter("elementId");
		String lsNextAction = aoRequest.getParameter("next_action"), lsTaxonomyType = aoRequest
				.getParameter("hdnTaxonomyType");
		String lsNewItemValue = aoRequest.getParameter("hdnItemName"), lsBranchID = aoRequest
				.getParameter("hdnBranchId");
		String lsElementId = aoRequest.getParameter("locationValue"), lsDeleteBranchId = aoRequest
				.getParameter("deleteBranchId");
		boolean lbDuplicate = false, lbActiveflagValStatus = false, lbParentInactive = false, lbRecacheInProgress = false;
		boolean lbActiveProcurementStatus = false;
		boolean lbPlannedProcurement = false, lbReleasedProcurement = false;
		String lsOldEvidenceValue = "", lsNewEvidenceValue = "";

		try
		{
			String lsEnvironmentType = (String) BaseCacheManagerWeb.getInstance().getCacheObject(
					ApplicationConstants.ENVIROMENT_TYPE);
			if (null != lsEnvironmentType
					&& null != lsNextAction
					&& (lsNextAction.equals("savePage") || lsNextAction.equals("removeTaxonomyItem") || lsNextAction
							.equals("confirmselectionstep5")))
			{
				lbRecacheInProgress = checkRecacheInProgress();
			}
			if (null != lsNextAction && lsNextAction.equals("detailPage"))
			{
				navigateToDetailPage(aoRequest, aoResponse);
			}
			else if (null != lsNextAction && lsNextAction.equals("savePage"))
			{
				// transaction to find active procurement
				lsOldEvidenceValue = aoRequest.getParameter("hiddenChkEvidanceOld");
				lsNewEvidenceValue = aoRequest.getParameter("hiddenChkEvidance");
				if (lsOldEvidenceValue.equalsIgnoreCase("1") && lsNewEvidenceValue.equalsIgnoreCase("0"))
				{
					lsTransactionStatusMsg = MaintenanceTaxonomyController.TRANSACTION_FAILED_SAVE;
					lbReleasedProcurement = fetchReleasedProcurementStatus(lsDetailPageElementId);
					if (!lbReleasedProcurement)
					{
						lbPlannedProcurement = fetchPlannedProcurementStatus(lsDetailPageElementId);
					}
				}
				//
				if (!lbPlannedProcurement && !lbReleasedProcurement)
				{
					String lsHiddenElementName = aoRequest.getParameter("hiddenElementName");
					String lsSaveTaxonomyType = aoRequest.getParameter("elementType");
					if ("service area".equalsIgnoreCase(lsSaveTaxonomyType.toLowerCase())
							|| "function".equalsIgnoreCase(lsSaveTaxonomyType.toLowerCase()))
					{
						lsSaveTaxonomyType = "Service Areas/Functions";
					}
					String lsTempCurrentTaxonomyName = aoRequest.getParameter("hiddenElementName");
					String lsTempOldTaxonomyName = aoRequest.getParameter("hiddenOldElementName");
					lsTransactionStatusMsg = MaintenanceTaxonomyController.TRANSACTION_FAILED_SAVE;
					TaxonomyTree loTaxonomyTreeBeanObj = (TaxonomyTree) checkDuplicate(aoRequest, aoResponse);
					// This condition checks for the duplicate names
					if (Integer.valueOf(loTaxonomyTreeBeanObj.getMsIsDuplicate()) > 1
							|| (Integer.valueOf(loTaxonomyTreeBeanObj.getMsIsDuplicate()) == 1 && !lsTempOldTaxonomyName
									.trim().equalsIgnoreCase(lsTempCurrentTaxonomyName.trim())))
					{
						lbDuplicate = true;
						lsTransactionStatusMsg = MaintenanceTaxonomyController.TRANSACTION_FAILED_DUPLICATE + " "
								+ lsSaveTaxonomyType + " named " + lsHiddenElementName;
					}
					else if (!lbRecacheInProgress)
					{
						lbActiveflagValStatus = checkActiveflagStatus(aoRequest, aoResponse);
						if (lbActiveflagValStatus)
						{
							lbParentInactive = false;
							saveTaxonomyDetailPage(aoRequest, aoResponse, lbRecacheInProgress);
							aoRequest.getPortletSession().setAttribute("lsOldElementName", lsTempCurrentTaxonomyName,
									PortletSession.APPLICATION_SCOPE);
							if (lbRecacheInProgress)
							{
								lsTransactionStatusMsg = TAXONOMY_RE_CACHE_IS_IN_PROCESS_1
										+ TAXONOMY_RE_CACHE_IS_IN_PROCESS_2;
							}
							else
							{
								lsTransactionStatusMsg = MaintenanceTaxonomyController.TRANSACTION_SUCCESS_SAVE;
							}
						}
						else
						{
							lsTransactionStatusMsg = MaintenanceTaxonomyController.TRANS_FAIL_PARENT_NOT_ACTIVE;
							lbParentInactive = true;
						}
					}
					else
					{
						lsTransactionStatusMsg = TAXONOMY_RE_CACHE_IS_IN_PROCESS_1 + TAXONOMY_RE_CACHE_IS_IN_PROCESS_2;
					}
				}
				else
				{
					lbActiveProcurementStatus = true;
					if (lbReleasedProcurement)
					{
						lsTransactionStatusMsg = PROCUREMENT_IS_RELEASED;
					}
					else if (lbPlannedProcurement)
					{
						lsTransactionStatusMsg = PROCUREMENT_IS_PLANNED;
					}
				}
			}
			// This condition removes/deletes all the details of selected
			else if (null != lsNextAction && lsNextAction.equals("removeTaxonomyItem"))
			{
				lsTransactionStatusMsg = aoRequest.getParameter("deleteTaxonomyName")
						+ MaintenanceTaxonomyController.TRANSACTION_FAILED_REMOVE;
				lsTransactionStatusMsg = removeTaxonomy(aoRequest, aoResponse, lsDetailPageElementId, lsDeleteBranchId,
						lbRecacheInProgress);
			}
			// This condition renders and persist the selected taxonomy
			else if (null != lsNextAction && lsNextAction.equals("selectTaxonomy"))
			{
				aoResponse.setRenderParameter("selectTaxonomyStep1", "selectTaxonomyStep1");
			}
			else if (null != lsNextAction && lsNextAction.equals("selectLocationstep2"))
			{
				String lsTaxonomyTypeRadio = (aoRequest.getParameter("taxonomyTypeRadio"));
				aoResponse.setRenderParameter("lsTaxonomyType", lsTaxonomyTypeRadio);
				aoResponse.setRenderParameter("selectLocationstep2", "selectLocationstep2");
			}
			// This condition renders and persist the new taxonomy name page
			else if (null != lsNextAction && lsNextAction.equals("namenewitemstep3"))
			{
				aoResponse.setRenderParameter("namenewitemstep3", "namenewitemstep3");

			}
			// This condition renders and persist data to confirm adding
			else if (null != lsNextAction && lsNextAction.equals("confirmselectionstep4"))
			{
				duplicateCheck(aoRequest, aoResponse);
			}
			// This condition finally inserts all the details of new taxonomy
			else if (null != lsNextAction && lsNextAction.equals("confirmselectionstep5"))
			{
				lsTransactionStatusMsg = lsNewItemValue + MaintenanceTaxonomyController.TRANSACTION_FAILED_ADD;
				aoResponse.setRenderParameter("landing", "landing");
				TaxonomyTree loTaxonomyTree = addNewTaxonomyItem(aoRequest, aoResponse, lsTaxonomyType, lsNewItemValue,
						lsBranchID, lsElementId, lbRecacheInProgress);
				aoRequest.getPortletSession().setAttribute("lsOldElementName", lsNewItemValue,
						PortletSession.APPLICATION_SCOPE);
				if (!lbRecacheInProgress)
				{
					lsTransactionStatusMsg = loTaxonomyTree.getMsElementName()
							+ MaintenanceTaxonomyController.TRANSACTION_PASSED_ADD;
				}
				else
				{
					lsTransactionStatusMsg = TAXONOMY_RE_CACHE_IS_IN_PROCESS_1 + TAXONOMY_RE_CACHE_IS_IN_PROCESS_2;
				}
			}
			if (lbDuplicate || lbParentInactive || lbRecacheInProgress || lbActiveProcurementStatus)
			{
				lsTransactionStatus = "failed";
			}
			else
			{
				lsTransactionStatus = "passed";
			}
			setTransactionStatus(aoResponse, lsTransactionStatusMsg, lsTransactionStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lsTransactionStatus = "failed";
			setTransactionStatus(aoResponse, lsTransactionStatusMsg, lsTransactionStatus);
		}
		catch (Exception loEx)
		{
			lsTransactionStatus = "failed";
			setTransactionStatus(aoResponse, lsTransactionStatusMsg, lsTransactionStatus);
		}
		long loEndTimeTime = System.currentTimeMillis();
		try
		{
			LOG_OBJECT.Debug("TIME TAKEN for execution of action Method in MaintenanceTaxonomyController = "
					+ (loEndTimeTime - loStartTime));
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Error while execution of action Method in MaintenanceTaxonomyController ", aoEx);
		}
		UserThreadLocal.unSet();
	}

	/**
	 * This method fetches the no. of procurement in planned status
	 * 
	 * @param asElementId - service element Id
	 * @return lbProcurementPlanned - boolean status flag
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	private boolean fetchPlannedProcurementStatus(String asElementId) throws ApplicationException
	{
		Channel loChannelObj = new Channel();
		boolean lbProcurementPlanned = false;
		Map<String, String> aoProcurementMap = new HashMap<String, String>();
		aoProcurementMap.put(HHSConstants.PROPOSAL_STATUS_ID_KEY, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_PLANNED));
		aoProcurementMap.put("serviceElementId", asElementId);
		loChannelObj.setData("aoProcurementMap", aoProcurementMap);
		HHSTransactionManager.executeTransaction(loChannelObj, "fetchPlannedProcurementCount");
		int liPlannedProcCount = (Integer) loChannelObj.getData("plannedProcurementCount");
		if (liPlannedProcCount > 0)
		{
			lbProcurementPlanned = true;
		}
		return lbProcurementPlanned;
	}

	/**
	 * This method fetches the no. of procurement in released status
	 * 
	 * @param asElementId - service element Id
	 * @return lbProcurementPlanned - boolean status flag
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	private boolean fetchReleasedProcurementStatus(String asElementId) throws ApplicationException
	{
		Channel loChannelObj = new Channel();
		boolean lbProcurementPlanned = false;
		Map<String, String> aoProcurementMap = new HashMap<String, String>();
		aoProcurementMap.put(HHSConstants.PROPOSAL_STATUS_ID_KEY, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_RELEASED));
		aoProcurementMap.put("serviceElementId", asElementId);
		loChannelObj.setData("aoProcurementMap", aoProcurementMap);
		HHSTransactionManager.executeTransaction(loChannelObj, "fetchReleasedProcurementCount");
		int liPlannedProcCount = (Integer) loChannelObj.getData("releasedProcurementCount");
		if (liPlannedProcCount > 0)
		{
			lbProcurementPlanned = true;
		}
		return lbProcurementPlanned;
	}

	/**
	 * This method set the transaction message and status based on transaction
	 * pass or fail
	 * @param aoResponse decides the next execution flow
	 * @param asTransactionStatusMsg this contains the message bases on
	 *            transaction pass or fail
	 * @param asTransactionStatus this contains the status bases on transaction
	 *            pass or fail
	 */
	private void setTransactionStatus(ActionResponse aoResponse, String asTransactionStatusMsg,
			String asTransactionStatus)
	{
		aoResponse.setRenderParameter("transactionStatus", asTransactionStatus);
		aoResponse.setRenderParameter("transactionMessage", asTransactionStatusMsg);
	}

	/**
	 * This method checks the duplicates in service Areas and Function
	 * @param aoRequest to get screen parameters and next action to be performed
	 * @param aoResponse decides the next execution flow
	 * @throws ApplicationException
	 * @throws NumberFormatException
	 */
	private void duplicateCheck(ActionRequest aoRequest, ActionResponse aoResponse) throws ApplicationException,
			NumberFormatException
	{
		TaxonomyTree loTaxonomyTreeBean;
		Channel loChannelObj;
		String lsTaxonomyType;
		String lsNewItemValue;
		TaxonomyTree loTaxonomyTree = new TaxonomyTree();
		loChannelObj = new Channel();
		lsNewItemValue = aoRequest.getParameter("newItem");
		lsTaxonomyType = aoRequest.getParameter("taxonomyTypeRadio");
		loTaxonomyTree.setMsElementName(lsNewItemValue.toLowerCase());
		loTaxonomyTree.setMsElementType(lsTaxonomyType.toLowerCase());
		if ("service area".equalsIgnoreCase(lsTaxonomyType.toLowerCase())
				|| "function".equalsIgnoreCase(lsTaxonomyType.toLowerCase()))
		{
			lsTaxonomyType = "Service Areas/Functions";
		}
		loChannelObj.setData("aoTaxonomyTree", loTaxonomyTree);
		TransactionManager.executeTransaction(loChannelObj, "isDuplicateTaxonomyElementName");
		loTaxonomyTreeBean = (TaxonomyTree) loChannelObj.getData("aoTaxonomyTree");
		if (Integer.valueOf(loTaxonomyTreeBean.getMsIsDuplicate()) >= 1)
		{
			String lsDupErrorMsg = MaintenanceTaxonomyController.TRANSACTION_FAILED_DUPLICATE_ADD + " "
					+ lsTaxonomyType + " named " + lsNewItemValue;
			aoResponse.setRenderParameter("duplicateElementName", lsDupErrorMsg);
			aoResponse.setRenderParameter("namenewitemstep3", "namenewitemstep3");
		}
		else
		{
			aoResponse.setRenderParameter("confirmselectionstep4", "confirmselectionstep4");
		}
	}

	/**
	 * This method removes the taxonomy item
	 * @param aoRequest to get screen parameters and next action to be performed
	 * @param aoResponse decides the next execution flow
	 * @param asDetailPageElementId is the element id to be deleted
	 * 
	 * @param asDeleteBranchId is the branch id of the item selected
	 * @param lbRecacheInProgress
	 * @return lsTransactionStatusMsg lsTransactionStatusMsg is the message
	 *         return based on transaction pass or fail
	 * @throws ApplicationException
	 */
	private String removeTaxonomy(ActionRequest aoRequest, ActionResponse aoResponse, String asDetailPageElementId,
			String asDeleteBranchId, boolean lbRecacheInProgress) throws ApplicationException
	{
		String lsTransactionStatusMsg;
		TaxonomyTree loTaxonomyTreeBean;
		Channel loChannelObj;
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		lsTransactionStatusMsg = MaintenanceTaxonomyController.TRANSACTION_FAILED_REMOVE;
		loChannelObj = new Channel();
		createAuditHashMap(aoRequest, aoResponse, loChannelObj);
		String lsHiddenElementName = aoRequest.getParameter("deleteTaxonomyName");
		loTaxonomyTreeBean = new TaxonomyTree();
		loTaxonomyTreeBean.setMsElementid(asDetailPageElementId);
		loTaxonomyTreeBean.setMsBranchid(asDeleteBranchId);
		loTaxonomyTreeBean.setMsTransactionEvent("delete");
		loTaxonomyTreeBean.setMsStatus("1");
		loTaxonomyTreeBean.setMoModifiedDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));

		// new Columns
		loTaxonomyTreeBean.setMoCreatedDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
		loTaxonomyTreeBean.setMsCreatedBy(lsUserId);
		loTaxonomyTreeBean.setMoTMModifiedDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
		loTaxonomyTreeBean.setMsModifyBy(lsUserId);
		// new Columns

		loChannelObj.setData("aoTaxonomyTree", loTaxonomyTreeBean);
		if (!lbRecacheInProgress)
		{
			TransactionManager.executeTransaction(loChannelObj, "deleteTaxonomyItemDetails");
			lsTransactionStatusMsg = lsHiddenElementName + " "
					+ MaintenanceTaxonomyController.TRANSACTION_PASSED_REMOVE;
		}
		else
		{
			lsTransactionStatusMsg = TAXONOMY_RE_CACHE_IS_IN_PROCESS_1 + TAXONOMY_RE_CACHE_IS_IN_PROCESS_2;
		}
		aoResponse.setRenderParameter("deleteTaxonomy", ApplicationConstants.MAINTENANCE_TAXONOMY_ITEM_DETAILS);
		return lsTransactionStatusMsg;
	}

	/**
	 * This method is called on click of item from left menu to show the detail
	 * page
	 * @param aoRequest to get screen parameters and next action to be performed
	 * @param aoResponse decides the next execution flow
	 * @throws ApplicationException
	 */
	private void navigateToDetailPage(ActionRequest aoRequest, ActionResponse aoResponse) throws ApplicationException
	{
		TaxonomyTree loTaxonomyTreeBean;
		Channel loChannelObj;
		String lsElementId;
		loTaxonomyTreeBean = null;
		GeneralAuditBean loGeneralAuditBeanRecache = new GeneralAuditBean();
		loChannelObj = new Channel();
		lsElementId = aoRequest.getParameter("elementId");
		loChannelObj.setData("aoElementId", lsElementId);
		loGeneralAuditBeanRecache.setMsEventname("taxonomy recache");
		loChannelObj.setData("aoGeneralAuditBeanRecache", loGeneralAuditBeanRecache);
		TransactionManager.executeTransaction(loChannelObj, "getTaxonomyDetailsLeftMenu");
		loTaxonomyTreeBean = (TaxonomyTree) loChannelObj.getData("aoTaxonomyTreeBean");
		aoRequest.getPortletSession().setAttribute("lsOldElementName", loTaxonomyTreeBean.getMsElementName(),
				PortletSession.APPLICATION_SCOPE);
		loGeneralAuditBeanRecache = null;
		ApplicationSession.setAttribute(loTaxonomyTreeBean, aoRequest, "TaxonomyTreeBean");
		aoResponse
				.setRenderParameter("mainttaxonomyitemdetail", ApplicationConstants.MAINTENANCE_TAXONOMY_ITEM_DETAILS);
	}

	/**
	 * This method adds new taxonomy item to the existing taxonomy and persist
	 * the values to database
	 * 
	 * @param aoRequest to get screen parameters and next action to be performed
	 * @param aoResponse decides the next execution flow
	 * @param asTaxonomyType under which taxonomy type we are adding the new
	 *            taxonomy
	 * @param asNewItemValue name of the new taxonomy
	 * @param asBranchID branch id of the newly added taxonomy(separated by
	 *            delimiter ',')
	 * @param asElementId taxonomy id of the newly added taxonomy
	 * @param lbRecacheInProgress
	 * @return TaxonomyTree taxonomy tree bean with taxonomy values
	 * @throws ApplicationException
	 */
	private TaxonomyTree addNewTaxonomyItem(ActionRequest aoRequest, ActionResponse aoResponse, String asTaxonomyType,
			String asNewItemValue, String asBranchID, String asElementId, boolean lbRecacheInProgress)
			throws ApplicationException
	{
		Channel loChannelObj;
		loChannelObj = new Channel();
		createAuditHashMap(aoRequest, aoResponse, loChannelObj);
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		TaxonomyTree loTaxonomyTreeBean = new TaxonomyTree();
		loTaxonomyTreeBean.setMsBranchid(asBranchID);
		loTaxonomyTreeBean.setMsElementName(asNewItemValue);
		loTaxonomyTreeBean.setMsElementDescription("");
		loTaxonomyTreeBean.setMsElementType(asTaxonomyType);
		loTaxonomyTreeBean.setMsActiveFlag("1");
		loTaxonomyTreeBean.setMsParentid(asElementId);
		loTaxonomyTreeBean.setMsSelectionFlag("0");
		loTaxonomyTreeBean.setMsEvidenceReqd("0");
		loTaxonomyTreeBean.setMsDeleteStatus("N");
		loTaxonomyTreeBean.setMsTransactionEvent("Add");
		loTaxonomyTreeBean.setMoModifiedDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));

		// new Columns
		loTaxonomyTreeBean.setMoCreatedDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
		loTaxonomyTreeBean.setMsCreatedBy(lsUserId);
		loTaxonomyTreeBean.setMoTMModifiedDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
		loTaxonomyTreeBean.setMsModifyBy(lsUserId);
		// new Columns

		loChannelObj.setData("aoTaxonomyTree", loTaxonomyTreeBean);
		ApplicationSession.setAttribute(loTaxonomyTreeBean, aoRequest, "TaxonomyTreeBean");
		if (!lbRecacheInProgress)
		{
			TransactionManager.executeTransaction(loChannelObj, "addTaxonomyConfirmationComplete");
			aoResponse.setRenderParameter("landing", "null");
			loTaxonomyTreeBean = (TaxonomyTree) loChannelObj.getData("aoTaxonomyTreeSelected");
			if (null == loTaxonomyTreeBean.getMsElementDescription())
			{
				loTaxonomyTreeBean.setMsElementDescription("");
			}
			aoResponse.setRenderParameter("confirmselectionstep5", "confirmselectionstep5");
			ApplicationSession.setAttribute(loTaxonomyTreeBean, aoRequest, "TaxonomyTreeBean");
		}
		else
		{
			aoResponse.setRenderParameter("landing", "landing");
		}
		return loTaxonomyTreeBean;
	}

	/**
	 * This method adds new taxonomy item to the existing taxonomy and persist
	 * the values to database
	 * 
	 * @param aoRequest to get screen parameters and next action to be performed
	 * @param aoResponse decides the next execution flow
	 * @param lbRecacheInProgress
	 * @throws ApplicationException
	 */
	private void saveTaxonomyDetailPage(ActionRequest aoRequest, ActionResponse aoResponse, boolean lbRecacheInProgress)
			throws ApplicationException
	{
		Channel loChannelObj = new Channel();
		createAuditHashMap(aoRequest, aoResponse, loChannelObj);

		TaxonomyTree loTaxonomyTreeBean = new TaxonomyTree();
		loTaxonomyTreeBean = populateBean(aoRequest, loTaxonomyTreeBean);
		ApplicationSession.setAttribute(loTaxonomyTreeBean, aoRequest, "TaxonomyTreeBean");
		aoResponse.setRenderParameter("confirmselectionstep5", "confirmselectionstep5");
		loChannelObj.setData("aoTaxonomyTree", loTaxonomyTreeBean);
		if (!lbRecacheInProgress)
		{
			TransactionManager.executeTransaction(loChannelObj, "taxonomyItemDetailsSave");
		}
		ApplicationSession.setAttribute(loTaxonomyTreeBean, aoRequest, "TaxonomyTreeBean");
	}

	/**
	 * This method creates hashmap for audit transaction which captures details
	 * like (event name, type, date, etc..)
	 * 
	 * @param aoRequest to get screen parameters and next action to be performed
	 * @param aoResponse decides the next execution flow
	 * @param aoChannelObj Channel Object that needs to be set for to and fro of
	 *            values
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private void createAuditHashMap(ActionRequest aoRequest, ActionResponse aoResponse, Channel aoChannelObj)
	{
		Map loAuditHashMap = new HashMap();
		DateFormat loDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Date loDate = new Date();
		String lsLastModifiedDateTaxonomy = loDateFormat.format(loDate);
		String lsUserName = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_NAME, PortletSession.APPLICATION_SCOPE);
	
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_EMAIL_ID, PortletSession.APPLICATION_SCOPE);
		String lsOrgId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
				PortletSession.APPLICATION_SCOPE);
		loAuditHashMap.put("orgId", lsOrgId);
		loAuditHashMap.put("eventName", "Taxonomy Update");
		loAuditHashMap.put("eventType", "Taxonomy");
		loAuditHashMap.put("auditDate", lsLastModifiedDateTaxonomy);
		loAuditHashMap.put("userId", lsUserId);
		loAuditHashMap.put("data", "Taxonomy");
		loAuditHashMap.put("entityType", "Taxonomy");
		loAuditHashMap.put("EntityIdentifier", "Taxonomy");
		loAuditHashMap.put("entityId", "1");
		loAuditHashMap.put("providerFlag", "N");
		aoChannelObj.setData("aoAuditDetailMap", loAuditHashMap);
		loDateFormat = new SimpleDateFormat("MM/dd/yyyy");
		lsLastModifiedDateTaxonomy = loDateFormat.format(loDate);
		aoRequest.getPortletSession().setAttribute("lastModifiedDateTaxonomy", lsLastModifiedDateTaxonomy,
				PortletSession.APPLICATION_SCOPE);
		aoRequest.getPortletSession().setAttribute("lastModifiedByUserTaxonomy", lsUserName,
				PortletSession.APPLICATION_SCOPE);

	}

	/**
	 * This method populates loTaxonomyTreeBean, loTaxonomySynonymBean,
	 * loTaxonomyLinkageBean
	 * 
	 * @param aoRequest to get screen parameters
	 * @param aoTaxonomyTreeBean populate taxonomy tree bean to persist data
	 *            till database
	 * @return TaxonomyTree taxonomy tree bean with taxonomy values
	 * @throws ApplicationException
	 */
	private TaxonomyTree populateBean(ActionRequest aoRequest, TaxonomyTree aoTaxonomyTreeBean)
			throws ApplicationException
	{
		String lsDetailPageElementId = aoRequest.getParameter("elementId");
		TaxonomySynonymBean loTaxonomySynonymBean = null;
		TaxonomyLinkageBean loTaxonomyLinkageBean = null;
		List<TaxonomySynonymBean> loSynonymBeanList = new ArrayList<TaxonomySynonymBean>();
		List<TaxonomyLinkageBean> loLinkageBeanList = new ArrayList<TaxonomyLinkageBean>();
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsHiddenChkEvidance = aoRequest.getParameter("hiddenChkEvidance");
		String lsHiddenChkApproval = aoRequest.getParameter("hiddenChkApproval");
		String lsHiddenChkTaxonomy = aoRequest.getParameter("hiddenChkTaxonomy");
		String lsHiddenDescription = aoRequest.getParameter("hiddenDescription");
		String lsSynonymValues = aoRequest.getParameter("allSynonyms");
		String lsLinkageIdValues = aoRequest.getParameter("allLinkages");
		String lsHiddenElementName = aoRequest.getParameter("hiddenElementName");
		String lsSaveBranchId = aoRequest.getParameter("saveBranchId");
		String lsElementType = aoRequest.getParameter("elementType");

		// get all the onload values of flags as hidden parameters
		String lsHiddenChkEvidanceOld = aoRequest.getParameter("hiddenChkEvidanceOld");
		String lsHiddenChkApprovalOld = aoRequest.getParameter("hiddenChkApprovalOld");
		String lsHiddenChkTaxonomyOld = aoRequest.getParameter("hiddenChkTaxonomyOld");

		String lsSynonymArr[] = lsSynonymValues.split("\\|");
		if (!(lsSynonymArr[0].equalsIgnoreCase("")))
		{
			for (int liCount = 0; liCount < lsSynonymArr.length; liCount++)
			{
				loTaxonomySynonymBean = new TaxonomySynonymBean();
				loTaxonomySynonymBean.setMsDeleteStatus("N");
				loTaxonomySynonymBean.setMsServiceElementIdFk(lsDetailPageElementId);
				loTaxonomySynonymBean.setMsTaxonomySyn(lsSynonymArr[liCount]);
				// new Columns
				loTaxonomySynonymBean.setMoCreatedDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
				loTaxonomySynonymBean.setMsCreatedBy(lsUserId);
				loTaxonomySynonymBean.setMoTMModifiedDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
				loTaxonomySynonymBean.setMsModifyBy(lsUserId);
				// new Columns
				loSynonymBeanList.add(loTaxonomySynonymBean);
			}
		}
		// setting list of LinkageBean
		String lsLinkageArr[] = lsLinkageIdValues.split("\\|");
		if (!(lsLinkageArr[0].equalsIgnoreCase("")))
		{
			for (int liCount = 0; liCount < lsLinkageArr.length; liCount++)
			{
				loTaxonomyLinkageBean = new TaxonomyLinkageBean();
				loTaxonomyLinkageBean.setMsDeleteStatus("N");
				loTaxonomyLinkageBean.setMsTaxonomyId(lsDetailPageElementId);
				loTaxonomyLinkageBean.setMsTaxonomyLinkageId(lsLinkageArr[liCount]);

				// new Columns
				loTaxonomyLinkageBean.setMoCreatedDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
				loTaxonomyLinkageBean.setMsCreatedBy(lsUserId);
				loTaxonomyLinkageBean.setMoTMModifiedDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
				loTaxonomyLinkageBean.setMsModifyBy(lsUserId);
				// new Columns
				loLinkageBeanList.add(loTaxonomyLinkageBean);
			}
		}

		// Below 3 condition sets the whether the Evidence, Approval and Active
		// Flags are changed or not while doing save operation
		if (null != lsHiddenChkEvidanceOld && (!lsHiddenChkEvidanceOld.equalsIgnoreCase(lsHiddenChkEvidance)))
		{
			aoTaxonomyTreeBean.setMbEvidenceChanged(true);
		}
		if (null != lsHiddenChkApprovalOld && (!lsHiddenChkApprovalOld.equalsIgnoreCase(lsHiddenChkApproval)))
		{
			aoTaxonomyTreeBean.setMbApprovalChanged(true);
		}
		if (null != lsHiddenChkTaxonomyOld && (!lsHiddenChkTaxonomyOld.equalsIgnoreCase(lsHiddenChkTaxonomy)))
		{
			aoTaxonomyTreeBean.setMbTaxonomyChanged(true);
		}

		aoTaxonomyTreeBean.setMsTransactionEvent("Save");
		aoTaxonomyTreeBean.setMsElementid(lsDetailPageElementId);
		aoTaxonomyTreeBean.setMoModifiedDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
		aoTaxonomyTreeBean.setMsEvidenceReqd(lsHiddenChkEvidance);
		aoTaxonomyTreeBean.setMsActiveFlag(lsHiddenChkTaxonomy);
		aoTaxonomyTreeBean.setMsSelectionFlag(lsHiddenChkApproval);
		aoTaxonomyTreeBean.setMsElementDescription(lsHiddenDescription);
		aoTaxonomyTreeBean.setMsSynonymList(loSynonymBeanList);
		aoTaxonomyTreeBean.setMsLinkageList(loLinkageBeanList);
		aoTaxonomyTreeBean.setMsElementName(lsHiddenElementName);
		aoTaxonomyTreeBean.setMsBranchid(lsSaveBranchId);
		aoTaxonomyTreeBean.setMsElementType(lsElementType);
		aoTaxonomyTreeBean.setMoCreatedDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
		aoTaxonomyTreeBean.setMsCreatedBy(lsUserId);
		aoTaxonomyTreeBean.setMoTMModifiedDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
		aoTaxonomyTreeBean.setMsModifyBy(lsUserId);
		return aoTaxonomyTreeBean;
	}

	/**
	 * This method checks if any duplicate taxonomy is already present
	 * 
	 * @param aoRequest to get screen parameters and next action to be performed
	 * @param aoResponse decides the next execution flow
	 * @return TaxonomyTree taxonomy tree bean with taxonomy values
	 * @throws ApplicationException
	 */
	private TaxonomyTree checkDuplicate(ActionRequest aoRequest, ActionResponse aoResponse) throws ApplicationException
	{
		Channel loChannelObj = new Channel();
		TaxonomyTree loTaxonomyTreeBean = new TaxonomyTree();
		loTaxonomyTreeBean = populateBean(aoRequest, loTaxonomyTreeBean);
		ApplicationSession.setAttribute(loTaxonomyTreeBean, aoRequest, "TaxonomyTreeBean");
		aoResponse.setRenderParameter("confirmselectionstep5", "confirmselectionstep5");
		loChannelObj.setData("aoTaxonomyTree", loTaxonomyTreeBean);
		TransactionManager.executeTransaction(loChannelObj, "isDuplicateTaxonomyElementName");
		TaxonomyTree loTaxonomyTreeBeanObj = (TaxonomyTree) loChannelObj.getData("aoTaxonomyTree");
		ApplicationSession.setAttribute(loTaxonomyTreeBean, aoRequest, "TaxonomyTreeBean");
		return loTaxonomyTreeBeanObj;
	}

	/**
	 * This method checks status of active flag
	 * 
	 * @param aoRequest to get screen parameters and next action to be performed
	 * @param aoResponse decides the next execution flow
	 * @return Boolean status of active flag
	 * @throws ApplicationException
	 */
	private Boolean checkActiveflagStatus(ActionRequest aoRequest, ActionResponse aoResponse)
			throws ApplicationException
	{
		Channel loChannelObj = new Channel();
		boolean lbActiveFlagVal = false;
		TaxonomyTree loTaxonomyTreeBean = new TaxonomyTree();
		loTaxonomyTreeBean = populateBean(aoRequest, loTaxonomyTreeBean);
		ApplicationSession.setAttribute(loTaxonomyTreeBean, aoRequest, "TaxonomyTreeBean");
		loChannelObj.setData("aoTaxonomyTree", loTaxonomyTreeBean);
		TransactionManager.executeTransaction(loChannelObj, "activeFlagValidation");
		lbActiveFlagVal = (Boolean) loChannelObj.getData("abActiveflagValStatus");
		ApplicationSession.setAttribute(loTaxonomyTreeBean, aoRequest, "TaxonomyTreeBean");
		return lbActiveFlagVal;
	}

	/**
	 * This method is used for fetching Taxonomy tree
	 * 
	 * @param aoRequest to get screen parameters
	 * @param aoResponse decides the next execution flow
	 * @param aoChannelObj Channel Object that needs to be set for to and fro of
	 *            values
	 * @throws ApplicationException
	 */
	private void navigateToTaxonomyMain(RenderRequest aoRequest, RenderResponse aoResponse) throws ApplicationException
	{
		Document loTaxonomyDOM = getTaxonomyFromDB();
		try
		{
			String lsTree = (String) XMLUtil.getXMLAsString(loTaxonomyDOM);
			transformXML(aoRequest, aoResponse, lsTree);
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error occured while getting application data", aoExp);
		}
	}

	/**
	 * This method is used to get Taxonomy from DB
	 * 
	 * @return Document returns an object of HTML DOM type
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	private Document getTaxonomyFromDB() throws ApplicationException
	{

		try
		{
			Channel loChannelObj = new Channel();
			// Fetch Taxonomy data from DB
			TransactionManager.executeTransaction(loChannelObj, "retrieve_taxonomy_from_DB");
			List<TaxonomyTree> loTaxonomyList = (List<TaxonomyTree>) loChannelObj.getData("aoTaxonomyList");
			// Instantiating TaxonomyDOM to generate DOM Tree for Taxonomy
			TaxonomyDOMUtil loTaxonomyDOM = new TaxonomyDOMUtil();
			Document loTaxonomyDom = loTaxonomyDOM.createTaxonomyDOMObj(loTaxonomyList);
			return loTaxonomyDom;
		}
		catch (ApplicationException loEx)
		{
			throw loEx;
		}
	}

	/**
	 * This method is used transform XML format Taxonomy String in HTML format
	 * 
	 * @param aoRequest to get screen parameters
	 * @param aoResponse decides the next execution flow
	 * @param asTree XML as String
	 */
	private void transformXML(RenderRequest aoRequest, RenderResponse aoResponse, String asTree)
	{
		try
		{
			TransformerFactory loFactory = TransformerFactory.newInstance();
			Transformer loMainTreeTransformer = null;
			Transformer loLinkageTreeTransformer = null;
			PortletSession loPortletSession = aoRequest.getPortletSession();
			PortletContext loContext = loPortletSession.getPortletContext();

			String lsXsltRealPath = loContext.getRealPath("/portlet/maintenance/taxonomymaintenance/taxonomy.xsl");
			String lsLinkageXsltRealPath = loContext
					.getRealPath("/portlet/maintenance/taxonomymaintenance/taxonomyLinkage.xsl");

			loMainTreeTransformer = loFactory.newTransformer(new StreamSource(new File(lsXsltRealPath)));
			loLinkageTreeTransformer = loFactory.newTransformer(new StreamSource(new File(lsLinkageXsltRealPath)));
			StreamSource loXmlSource1 = new StreamSource(new StringReader(asTree));
			StreamSource loXmlSource2 = new StreamSource(new StringReader(asTree));

			ByteArrayOutputStream loMainTreeBaos = new ByteArrayOutputStream();
			ByteArrayOutputStream loLinkageTreeBaos = new ByteArrayOutputStream();

			loMainTreeTransformer.transform(loXmlSource1, new StreamResult(loMainTreeBaos));
			String lsFormattedOutput = loMainTreeBaos.toString();
			ApplicationSession.setAttribute(lsFormattedOutput, aoRequest, "lsMainTree");

			loLinkageTreeTransformer.transform(loXmlSource2, new StreamResult(loLinkageTreeBaos));
			lsFormattedOutput = loLinkageTreeBaos.toString();
			ApplicationSession.setAttribute(lsFormattedOutput, aoRequest, "lsLinkageTree");

		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error occured while transforming XML", aoExp);
		}
	}

	/**
	 * This method is used transform XML format Taxonomy String in HTML format
	 * for Location String
	 * 
	 * @param aoRequest to get screen parameters
	 * @param asTree XML as String
	 * @return String formatted output(tree as String)
	 */
	private String transformLocationXML(RenderRequest aoRequest, String asTree)
	{
		String lsFormattedOutput = "";
		try
		{

			TransformerFactory loFactory = TransformerFactory.newInstance();
			Transformer loLocationTreeTransformer = null;
			PortletSession loPortletSession = aoRequest.getPortletSession();
			PortletContext loContext = loPortletSession.getPortletContext();
			String lsXsltRealPath = loContext.getRealPath("/portlet/maintenance/taxonomymaintenance/taxonomy.xsl");
			loLocationTreeTransformer = loFactory.newTransformer(new StreamSource(new File(lsXsltRealPath)));
			StreamSource loXmlSource = new StreamSource(new StringReader(asTree));
			ByteArrayOutputStream loLocationTreeBaos = new ByteArrayOutputStream();
			loLocationTreeTransformer.transform(loXmlSource, new StreamResult(loLocationTreeBaos));
			lsFormattedOutput = loLocationTreeBaos.toString();

		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error occured while transforming form location XML", aoExp);
		}
		return lsFormattedOutput;
	}

	/**
	 * This method is used fetch Taxonomy XML based on taxonomy Type and
	 * transform the XML string in HTML format for Location String
	 * 
	 * @param aoRequest to get screen parameters
	 * @param aoResponse decides the next execution flow
	 * @param asTaxonomyType type of taxonomy amongst top predefined 6 types
	 * @param asTreeSource as main tree
	 * @throws ApplicationException
	 */
	private void getLocationByElement(RenderRequest aoRequest, RenderResponse aoResponse, String asTaxonomyType,
			String asTreeSource) throws ApplicationException
	{
		Document loTaxonomyDOM = (Document) getTaxonomyFromDB();
		try
		{
			String lsXpath = "//element[(@name='" + asTaxonomyType + "')]";
			Element lsElement = XMLUtil.getElement(lsXpath, loTaxonomyDOM);
			String lsTree = XMLUtil.getXMLAsString(lsElement);
			String lsHtmlTree = transformLocationXML(aoRequest, lsTree);
			aoRequest.setAttribute("lsTaxonomyTypeTree", lsHtmlTree);
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error occured while getting location by element", aoExp);
		}
	}

	/**
	 * This method is used to get Last Updated Re-cache Details(last modified
	 * date and user who has done re-cache) to display on screen
	 * 
	 * @param aoRequest to get screen parameters
	 * @param aoResponse decides the next execution flow
	 * @param aoChannelObj Channel Object that needs to be set for to and fro of
	 *            values (giving 'taxonomy recache' as event name to fetch
	 *            details)
	 * @return GeneralAuditBean for field mapping of variables (user name, last
	 *         modified date, etc..)
	 * @throws ApplicationException
	 */
	private GeneralAuditBean getLastUpdatedRecacheDetails(RenderRequest aoRequest, RenderResponse aoResponse,
			Channel aoChannelObj) throws ApplicationException
	{
		aoChannelObj = new Channel();
		GeneralAuditBean loGeneralAuditBeanRecache = new GeneralAuditBean();

		loGeneralAuditBeanRecache.setMsEventname("taxonomy recache");
		aoChannelObj.setData("aoGeneralAuditBeanRecache", loGeneralAuditBeanRecache); // for
																						// recache
																						// audit
																						// table

		TransactionManager.executeTransaction(aoChannelObj, "taxonomyGeneralAuditDetails");
		loGeneralAuditBeanRecache = (GeneralAuditBean) aoChannelObj.getData("aoGeneralAuditBeanRecache");
		return loGeneralAuditBeanRecache;
	}

	/**
	 * This method is used to get Last Updated Taxonomy Details(last modified
	 * date and user) to display on screen
	 * 
	 * @param aoRequest to get screen parameters
	 * @param aoResponse decides the next execution flow
	 * @param aoChannelObj Channel Object that needs to be set for to and fro of
	 *            values (giving 'taxonomy update' as event name to fetch
	 *            details)
	 * @return GeneralAuditBean for field mapping of variables (user name, last
	 *         modified date, etc..)
	 * @throws ApplicationException
	 */
	private GeneralAuditBean getLastUpdatedTaxonomyDetails(RenderRequest aoRequest, RenderResponse aoResponse,
			Channel aoChannelObj) throws ApplicationException
	{
		aoChannelObj = new Channel();
		GeneralAuditBean loGeneralAuditBean = new GeneralAuditBean();
		loGeneralAuditBean.setMsEventname("taxonomy update");
		aoChannelObj.setData("aoGeneralAuditBean", loGeneralAuditBean); // for
																		// recache
																		// audit
																		// table

		TransactionManager.executeTransaction(aoChannelObj, "taxonomyAuditDetails");
		loGeneralAuditBean = (GeneralAuditBean) aoChannelObj.getData("aoGeneralAuditBean");

		return loGeneralAuditBean;
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
