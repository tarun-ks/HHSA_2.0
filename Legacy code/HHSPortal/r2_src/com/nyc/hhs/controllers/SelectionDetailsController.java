package com.nyc.hhs.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.AwardBean;
import com.nyc.hhs.model.ExtendedDocument;
import com.nyc.hhs.model.ProcurementCOF;
import com.nyc.hhs.model.SelectionDetailsSummaryBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.HHSPortalUtil;
import com.nyc.hhs.util.PortalUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This controller class will handle all the request made by user from the
 * Selection details tab like displaying award details for proposal, displaying
 * award , required and optional documents, uploading new document or selecting
 * document from vault, removing document, opening document, viewing document
 * properties. Also it will handle the navigation through different pages.
 * 
 * This controller will be executed from S241 screen
 */
@Controller(value = "selectionDetailHandler")
@RequestMapping("view")
public class SelectionDetailsController extends BaseControllerSM
{
	/**
	 * This the logger object which is used to log any exception into the log
	 * file
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(RFPReleaseController.class);

	/**
	 * Changed method - Build 3.1.0, Enhancement id: 6025
	 * 
	 * This method is modified to fetch procurement documents details.
	 * 
	 * This method will be executed when the page will be loaded first time and
	 * will display provider's awards documents
	 * 
	 * <ul>
	 * <li>1. Create P8UserSession and Channel object</li>
	 * <li>2. Fetch the value of top level header</li>
	 * <li>3. If the fetched value of top level header is "selection details"
	 * then fetch the value of user organization Id from P8UserSession and the
	 * value of procurement Id from parse query String</li>
	 * <li>4. If the fetched value of top level header is not
	 * "selection details" then fetch the value of user organization Id and
	 * procurement Id from the request object</li>
	 * <li>5. Populate channel object with procurement Id, User Organization Id
	 * and filenet user session</li>
	 * <li>6. Execute transaction <b>fetchProposalSelectionDetails</b></li>
	 * <li>7. Retrieve the value of award Id, Award Document List, Award Config
	 * Document List and Award Bean from the channel object from the executed
	 * transaction</li>
	 * <li>8. If Award Config Document List contains records then iterate the
	 * list and segregate required and optional documents</li>
	 * <li>9. Set all the fetched data in the Request object</li>
	 * <li>10. If the value of top level header is "selection details" then set
	 * "providerselectiondetails" in ModelAndView object else set
	 * "viewAwardDocuments"</li>
	 * </ul>
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * Changes done for Enhancement #6429 for Release 3.4.0
	 * 
	 * @param aoRequest - a RenderRequest object
	 * @param aoResponse - a RenderResponse object
	 * @return ModelAndView containing details of the page to be displayed to
	 *         the end user
	 */
	@RenderMapping(params = "render_action=viewSelectionDetails")
	protected ModelAndView handleRenderRequestInternal(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		ModelAndView loModelAndView = new ModelAndView(HHSConstants.SELECTION_DETAILS_SUMMARY_JSP);
		Channel loChannelObj = new Channel();
		new ArrayList<ExtendedDocument>();
		new ArrayList<ExtendedDocument>();
		List<ExtendedDocument> loReqAwardConfigDocList = new ArrayList<ExtendedDocument>();
		List<ExtendedDocument> loOptAwardConfigDocList = new ArrayList<ExtendedDocument>();
		new AwardBean();
		Map<String, String> loParamMap = new HashMap<String, String>();
		HashMap<String, String> loRequiredParamMap = new HashMap<String, String>();
		String lsUserRole = null;
		String lsProcurementId = null;
		try
		{
			PortletSession loSession = aoRequest.getPortletSession();
			P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			lsUserRole = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ROLE,
					PortletSession.APPLICATION_SCOPE);
			String lsOrgId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);
			String lsEvalPoolMappingId = aoRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID);
			if (null != lsEvalPoolMappingId
					&& (lsEvalPoolMappingId == HHSConstants.NULL || lsEvalPoolMappingId.isEmpty()))
			{
				lsEvalPoolMappingId = null;
			}
			// checking if the value of top level header is equal to
			// "selection details"
			if (HHSConstants.SELECTION_DETAILS.equalsIgnoreCase(PortalUtil.parseQueryString(aoRequest,
					HHSConstants.TOP_LEVEL_FROM_REQ)))
			{

				lsProcurementId = PortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
				// Start || Changes done for Enhancement #6429 for Release 3.4.0
				if (null != aoRequest.getParameter(HHSConstants.UPLOAD_DOC_TYPE)
						&& aoRequest.getParameter(HHSConstants.UPLOAD_DOC_TYPE).equalsIgnoreCase(
								HHSConstants.STRING_AWARD_DOC))
				{
					lsOrgId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.ORGANIZATION_ID);
				}
				// End || Changes done for Enhancement #6429 for Release 3.4.0
			}
			else
			{
				lsOrgId = aoRequest.getParameter(HHSConstants.ORGANIZATION_ID);
				lsProcurementId = aoRequest.getParameter(HHSConstants.PROCUREMENT_ID);
			}
			getPageHeader(aoRequest, lsProcurementId);
			String lsSelectChildScreen = (String) aoRequest.getAttribute(HHSConstants.SELECTED_CHILD_TAB);
			if (lsSelectChildScreen != null
					&& lsSelectChildScreen.equalsIgnoreCase(HHSConstants.SELECTION_DETAILS_SUMMARY_KEY))
			{
				loModelAndView = renderSelectionDetailSummary(aoRequest);
			}
			else
			{
				loModelAndView = hadleRenderInternalForViewAwardDoc(aoRequest, loChannelObj, loReqAwardConfigDocList,
						loOptAwardConfigDocList, loParamMap, loRequiredParamMap, lsUserRole, lsProcurementId,
						loSession, loUserSession, lsOrgId, lsEvalPoolMappingId);
			}
		}
		// Handling Exception while rendering evaluation result and selection
		catch (ApplicationException aoAppEx)
		{
			setGenericErrorMessage(aoRequest);
			LOG_OBJECT.Error("Error occurred while rendering selection details", aoAppEx);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
		// handling exception other than Application Exception
		catch (Exception aoEx)
		{
			setGenericErrorMessage(aoRequest);
			LOG_OBJECT.Error("Error occurred while rendering selection details", aoEx);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
		return loModelAndView;
	}

	/**
	 * This method is used to render the View Award document screen if the Child
	 * screen is View Award documents
	 * <ul>
	 * <li>Execute Transaction <b>fetchProposalSelectionDetails</b> to fetch all
	 * required and optional documents</li>
	 * <li>Sel all documents in request to display it to user</li>
	 * </ul>
	 * <ul>
	 * <li>New method in R4</li>
	 * </ul>
	 * 
	 * Changes done for Enhancement #6429 for Release 3.4.0
	 * 
	 * @param aoRequest Render Request Object
	 * @param aoChannelObj Channel Object to
	 * @param aoReqAwardConfigDocList Required Award document list.
	 * @param aoOptAwardConfigDocList Optional Award document list
	 * @param aoParamMap Required parameters map
	 * @param aoRequiredParamMap Required parameters map
	 * @param asUserRole user role
	 * @param asProcurementId procurement id
	 * @param aoSession portlet session object
	 * @param aoUserSession p8 user session bean object
	 * @param asOrgId organization id
	 * @param asEvalPoolMappingId evaluation pool mapping id
	 * @return model and view of award document screen
	 * @throws ApplicationException if any exception occurred
	 */
	@SuppressWarnings("unchecked")
	private ModelAndView hadleRenderInternalForViewAwardDoc(RenderRequest aoRequest, Channel aoChannelObj,
			List<ExtendedDocument> aoReqAwardConfigDocList, List<ExtendedDocument> aoOptAwardConfigDocList,
			Map<String, String> aoParamMap, HashMap<String, String> aoRequiredParamMap, String asUserRole,
			String asProcurementId, PortletSession aoSession, P8UserSession aoUserSession, String asOrgId,
			String asEvalPoolMappingId) throws ApplicationException
	{
		ModelAndView loModelAndView;
		List<ExtendedDocument> loAwardDocumentList;
		List<ExtendedDocument> loAwardConfigDocList;
		// Start || Changes done for Enhancement #6429 for Release 3.4.0
		List<ExtendedDocument> loAgencyAwardDocumentList;
		// End || Changes done for Enhancement #6429 for Release 3.4.0
		ExtendedDocument loAwardDocDetails;
		Map<String, String> orgDetailsMap = new HashMap<String, String>();
		AwardBean loAwardBean;
		String lsAwardId;
		aoParamMap.put(HHSConstants.PROCUREMENT_ID, asProcurementId);
		aoParamMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvalPoolMappingId);
		aoParamMap.put(HHSConstants.ACCELERATOR_USER_ROLE, asUserRole);
		aoParamMap.put(HHSConstants.USER_ORG_ID, asOrgId);
		aoChannelObj.setData(HHSConstants.PARAMETERS_MAP_LOWERCASE, aoParamMap);
		aoChannelObj.setData(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		aoChannelObj.setData(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvalPoolMappingId);
		setRequiredParam(aoRequiredParamMap);
		aoChannelObj.setData(HHSConstants.HM_REQIRED_PROPERTY_MAP, aoRequiredParamMap);
		aoChannelObj.setData(HHSConstants.AO_FILENET_SESSION, aoUserSession);
		// Start || Changes done for Enhancement #6429 for Release 3.4.0
		if (null != HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.HIDDEN_HDNCONTRACTID)
				&& !HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.HIDDEN_HDNCONTRACTID).equals(
						HHSConstants.EMPTY_STRING))
		{
			aoChannelObj.setData(HHSConstants.CONTRACT_ID_WORKFLOW,
					HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.HIDDEN_HDNCONTRACTID));
		}
		else
		{
			aoChannelObj.setData(HHSConstants.CONTRACT_ID_WORKFLOW,
					aoRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW));
		}
		// End || Changes done for Enhancement #6429 for Release 3.4.0
		// checking if the value of "isFinancials" is null or not
		if (aoRequest.getParameter(HHSConstants.IS_FINANCIAL) == null
				|| HHSConstants.EMPTY_STRING.equalsIgnoreCase(aoRequest.getParameter(HHSConstants.IS_FINANCIAL)))
		{
			aoChannelObj.setData(HHSConstants.IS_REQUESTING_FROM_FINANCE_SCREEN,
					Boolean.valueOf(String.valueOf(aoRequest.getParameter(HHSConstants.IS_FINANCIAL))));
		}
		else
		{
			aoChannelObj.setData(HHSConstants.IS_REQUESTING_FROM_FINANCE_SCREEN, Boolean.TRUE);
		}
		HHSTransactionManager.executeTransaction(aoChannelObj, HHSConstants.FETCH_PROPOSALS_DETAILS);
		loAwardDocumentList = (List<ExtendedDocument>) aoChannelObj.getData(HHSConstants.FINAL_AWARD_DOC_LIST);
		loAwardDocDetails = (ExtendedDocument) aoChannelObj.getData(HHSConstants.AWARD_DOC_DETAILS);
		// Start || Changes done for Enhancement #6429 for Release 3.4.0
		loAgencyAwardDocumentList = (List<ExtendedDocument>) aoChannelObj
				.getData(HHSConstants.FINAL_AGENCY_AWARD_DOC_LIST);

		// End || Changes done for Enhancement #6429 for Release 3.4.0
		loAwardConfigDocList = (List<ExtendedDocument>) aoChannelObj.getData(HHSConstants.FINAL_AWARD_CONFIG_DOC_LIST);
		
        // Start QC9665 R 9.3.2 Citywide cyber assessment program (CCAP) - vulnerabilities -- Users Can Access Other User's Uploaded Documents
		List<ExtendedDocument> loFinalDocLst = ListUtils.union( ListUtils.union(loAwardDocumentList,loAgencyAwardDocumentList  )  ,  loAwardConfigDocList  );
		aoRequest.getPortletSession().setAttribute(ApplicationConstants.SESSION_EXTENDED_DOCUMENT_LIST,loFinalDocLst , PortletSession.APPLICATION_SCOPE);
		LOG_OBJECT.Info("save Document List in Session on Application scope");
		//End QC9665 R 9.3.2 Citywide cyber assessment program (CCAP) - vulnerabilities -- Users Can Access Other User's Uploaded Documents
		
		
		lsAwardId = (String) aoChannelObj.getData(HHSConstants.AWARD_ID);
		loAwardBean = (AwardBean) aoChannelObj.getData(HHSConstants.AWARD_BEAN_DETAIL);
		orgDetailsMap = (Map<String, String>) aoChannelObj.getData(HHSConstants.ORG_DETAILS_MAP);
		if (null != orgDetailsMap && !orgDetailsMap.isEmpty())
		{
			aoRequest.setAttribute(HHSConstants.ORG_NAME, orgDetailsMap.get(HHSR5Constants.ORGANIZATION_NAME_CAPS));
		}
		if (null != loAwardDocDetails)
		{
			aoRequest.setAttribute(HHSConstants.AWARD_DOC_DETAILS, loAwardDocDetails);
		}
		// aoRequest.setAttribute(HHSConstants.ORG_NAME,
		// aoChannelObj.getData(HHSConstants.ORGANIZATN_NAME));
		aoRequest.setAttribute(HHSConstants.GROUP_TITLE_MAP, aoChannelObj.getData(HHSConstants.GROUP_TITLE_MAP));
		aoRequest.setAttribute(HHSConstants.COMPETITION_POOL_STATUS,
				aoChannelObj.getData(HHSConstants.COMPETITION_POOL_STATUS));
		aoRequest.setAttribute(HHSConstants.CONTRACT_COF_APPROVED,
				aoChannelObj.getData(HHSConstants.CONTRACT_COF_APPROVED));
		aoRequest.setAttribute(HHSConstants.CONTRACT_BUDGET_APPROVED,
				aoChannelObj.getData(HHSConstants.CONTRACT_BUDGET_APPROVED));
		aoRequest.setAttribute(HHSConstants.PROVIDER_ORG_ID, asOrgId);

		// evaluation pool mapping added in request for enhancement 3.1.0 : 6025
		aoRequest.setAttribute(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvalPoolMappingId);
		// checking if award config document list contains records
		if (null != loAwardConfigDocList && !loAwardConfigDocList.isEmpty())
		{
			for (Iterator<ExtendedDocument> loIterator = loAwardConfigDocList.iterator(); loIterator.hasNext();)
			{
				ExtendedDocument loExtendedDocumentBean = (ExtendedDocument) loIterator.next();
				// checking if the Is_required doc flag value is one
				if (loExtendedDocumentBean.getIsRequiredDoc().equalsIgnoreCase(HHSConstants.ONE))
				{
					aoReqAwardConfigDocList.add(loExtendedDocumentBean);
				}
				else
				{
					aoOptAwardConfigDocList.add(loExtendedDocumentBean);
				}
				if (StringUtils.isNotBlank(loExtendedDocumentBean.getDocumentId())
						&& StringUtils.isBlank((String) aoRequest
								.getAttribute(HHSConstants.DISPLAY_DOWNLOAD_ALL_AWARD_DOCS_BUTTON)))
				{
					aoRequest.setAttribute(HHSConstants.DISPLAY_DOWNLOAD_ALL_AWARD_DOCS_BUTTON, HHSR5Constants.TRUE);
				}
			}
		}
		// Start || Changes done for Enhancement #6429 for Release 3.4.0
		loModelAndView = setRenderRequestAttributes(aoRequest, loAwardDocumentList, aoReqAwardConfigDocList,
				aoOptAwardConfigDocList, loAwardBean, lsAwardId, aoSession, loAgencyAwardDocumentList);
		// End || Changes done for Enhancement #6429 for Release 3.4.0
		return loModelAndView;
	}

	/**
	 * This method is used to set the render request parameters into the request
	 * object
	 * <ul>
	 * <li>Set all the attributed in request object obtained by executing the
	 * transaction</li>
	 * <li>Depending upon the requesting page it will render the user to the
	 * desired page</li>
	 * <li>If upload document is success then it will display message
	 * <code>M03</code></li>
	 * </ul>
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * Changes done for Enhancement #6429 for Release 3.4.0
	 * 
	 * @param aoRequest Render Request Object
	 * @param aoAwardDocumentList Award Document List
	 * @param aoReqAwardConfigDocList Award Config Required document list
	 * @param aoOptAwardConfigDocList Award Config Optional document list
	 * @param aoAwardBean award details bean
	 * @param asAwardId award id
	 * @param aoSession portlet session object
	 * @return ModelAndView
	 * @throws ApplicationException if any exception occurred
	 */
	private ModelAndView setRenderRequestAttributes(RenderRequest aoRequest,
			List<ExtendedDocument> aoAwardDocumentList, List<ExtendedDocument> aoReqAwardConfigDocList,
			List<ExtendedDocument> aoOptAwardConfigDocList, AwardBean aoAwardBean, String asAwardId,
			PortletSession aoSession, List<ExtendedDocument> aoAgencyAwardDocumentList) throws ApplicationException
	{
		ModelAndView loModelAndView = null;
		Boolean loIsPageReadOnly = Boolean.FALSE;
		try
		{
			/*
			 * ExtendedDocument dummyObj = new ExtendedDocument();
			 * dummyObj.setModifiedDate("modifiedDate");
			 * dummyObj.setStatusId(PropertyLoader.getProperty(
			 * HHSConstants.PROPERTIES_STATUS_CONSTANT,
			 * HHSConstants.DOCUMENTS_NOT_REQUESTED));
			 * dummyObj.setStatus(HHSConstants.DOCUMENTS_NOT_REQUESTED);
			 * aoRequest.setAttribute("dummyObj", dummyObj);
			 */
			// Release 5 User Notification
			String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			String lsPermissionType = (String) aoRequest.getPortletSession().getAttribute(HHSConstants.PERMISSION_TYPE,
					PortletSession.APPLICATION_SCOPE);
			if (lsPermissionType != null
					&& (lsPermissionType.equalsIgnoreCase(ApplicationConstants.ROLE_READ_ONLY) || lsPermissionType
							.equalsIgnoreCase(ApplicationConstants.ROLE_FINANCIAL))
					&& lsUserOrgType.equalsIgnoreCase(HHSConstants.PROVIDER_ORG))
			{
				if (aoReqAwardConfigDocList != null)
				{
					for (ExtendedDocument loExtendedDocument : aoReqAwardConfigDocList)
					{
						loExtendedDocument.setUserAccess(false);
					}
				}
				if (aoOptAwardConfigDocList != null)
				{
					for (ExtendedDocument loExtendedDocument : aoOptAwardConfigDocList)
					{
						loExtendedDocument.setUserAccess(false);
					}
				}
			}
			// Release 5
			aoRequest.setAttribute(HHSConstants.AWARD_BEAN_VALUE_OBJECT, aoAwardBean);
			aoRequest.setAttribute(HHSConstants.AWARD_DOC_LIST, aoAwardDocumentList);
			// Start || Changes done for Enhancement #6429 for Release 3.4.0
			aoRequest.setAttribute(HHSConstants.AGENCY_AWARD_DOC_LIST, aoAgencyAwardDocumentList);
			// End || Changes done for Enhancement #6429 for Release 3.4.0
			aoRequest.setAttribute(HHSConstants.AWARD_CONFIG_OPT_DOC, aoOptAwardConfigDocList);
			aoRequest.setAttribute(HHSConstants.AWARD_CONFIG_REQ_DOC, aoReqAwardConfigDocList);
			aoRequest.setAttribute(HHSConstants.AWARD_ID, asAwardId);
			// checking if the value is success or not
			if (null != PortalUtil.parseQueryString(aoRequest, HHSConstants.SUCCESS)
					&& PortalUtil.parseQueryString(aoRequest, HHSConstants.SUCCESS).equalsIgnoreCase(
							HHSConstants.UPLOAD))
			{
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
						ApplicationConstants.ERROR_MESSAGE_PROP_FILE, HHSConstants.FILE_UPLOAD_PASS_MESSAGE));
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_PASS_TYPE);
			}
			// Start || Changes done for Enhancement #6429 for Release 3.4.0
			if (null != aoRequest.getParameter(HHSConstants.UPLOAD_DOC_TYPE)
					&& aoRequest.getParameter(HHSConstants.UPLOAD_DOC_TYPE).equalsIgnoreCase(
							HHSConstants.STRING_AWARD_DOC))
			{
				loIsPageReadOnly = Boolean.TRUE;
				loModelAndView = new ModelAndView(HHSConstants.VIEW_AWARD_DOCUMENTS);
				aoRequest.setAttribute(HHSConstants.PAGE_READ_ONLY, loIsPageReadOnly);
				aoRequest.setAttribute(HHSConstants.IS_FINANCIAL, aoRequest.getParameter(HHSConstants.IS_FINANCIAL));
				aoRequest
						.setAttribute(HHSConstants.AS_PROC_STATUS, aoRequest.getParameter(HHSConstants.AS_PROC_STATUS));
			}
			// End || Changes done for Enhancement #6429 for Release 3.4.0
			// checking if the value of top level header is equal to
			// "selection details"
			else if (HHSConstants.SELECTION_DETAILS.equalsIgnoreCase(PortalUtil.parseQueryString(aoRequest,
					HHSConstants.TOP_LEVEL_FROM_REQ)))
			{
				loModelAndView = new ModelAndView(HHSConstants.PROVIDERS_SELECTION_DETAILS);
			}
			else
			{
				loIsPageReadOnly = Boolean.TRUE;
				loModelAndView = new ModelAndView(HHSConstants.VIEW_AWARD_DOCUMENTS);
				aoRequest.setAttribute(HHSConstants.PAGE_READ_ONLY, loIsPageReadOnly);
				aoRequest.setAttribute(HHSConstants.IS_FINANCIAL, aoRequest.getParameter(HHSConstants.IS_FINANCIAL));
				aoRequest
						.setAttribute(HHSConstants.AS_PROC_STATUS, aoRequest.getParameter(HHSConstants.AS_PROC_STATUS));
			}
			// Start || Changes done for Enhancement #6429 for Release 3.4.0
			if (null != aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE))
			{
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
						aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE));
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
						aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE_TYPE));
			}
			// End || Changes done for Enhancement #6429 for Release 3.4.0
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error Occurred while setting render parameter in base controller.", aoExp);
			throw aoExp;
		}
		catch (Exception aoExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error OCccured while setting render parameter in base controller.", aoExp);
			LOG_OBJECT.Error("Error OCccured while setting render parameter in base controller.", loAppEx);
			throw loAppEx;
		}
		return loModelAndView;
	}

	/**
	 * Below method will set the required parameter into the map
	 * @param aoParamMap param map
	 */
	private void setRequiredParam(HashMap<String, String> aoParamMap)
	{
		aoParamMap.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, ApplicationConstants.EMPTY_STRING);
		aoParamMap.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY_ID, ApplicationConstants.EMPTY_STRING);
		aoParamMap.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY, ApplicationConstants.EMPTY_STRING);
		aoParamMap.put(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY, ApplicationConstants.EMPTY_STRING);
		aoParamMap.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY_ID, ApplicationConstants.EMPTY_STRING);
		aoParamMap.put(P8Constants.PROPERTY_CE_LAST_MODIFIED_DATE, ApplicationConstants.EMPTY_STRING);
	}

	/**
	 * This method is used to render Selection Details Summary for the given
	 * procurement Id
	 * 
	 * <ul>
	 * <li>1.Get the procurement Id from request</li>
	 * <li>2.Get SelectionDetailsSummaryBean from Session object</li>
	 * <li>3.Set pagination parameters in session object by calling
	 * getPagingParams() from BaseController</li>
	 * <li>4.Set channel parameters and execute transaction
	 * <b>fetchGroupSelectionDetails</b></li>
	 * <li>5.Set transaction output in request and render it to
	 * selectionDetailsSummary jsp</li>
	 * </ul>
	 * <ul>
	 * <li>New method added in R4</li>
	 * </ul>
	 * @param aoRequest RenderRequest object
	 * @return ModelAndView containing jsp name
	 * @throws ApplicationException If an Application Exception occurs New
	 * 
	 */
	private ModelAndView renderSelectionDetailSummary(RenderRequest aoRequest) throws ApplicationException
	{
		String lsProcurementId = PortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
		String lsOrgId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_ORG_ID,
				PortletSession.APPLICATION_SCOPE);
		SelectionDetailsSummaryBean loSelectionDetailsSummaryBean = null;
		loSelectionDetailsSummaryBean = (SelectionDetailsSummaryBean) aoRequest.getPortletSession().getAttribute(
				HHSConstants.SESSION_SELECTION_DETAILS_SUMMARY_BEAN, PortletSession.PORTLET_SCOPE);
		if (null == loSelectionDetailsSummaryBean)
		{
			loSelectionDetailsSummaryBean = new SelectionDetailsSummaryBean();
		}
		loSelectionDetailsSummaryBean.setProcurementId(lsProcurementId);
		loSelectionDetailsSummaryBean.setOrganizationId(lsOrgId);
		loSelectionDetailsSummaryBean.setContractTypeId(HHSConstants.INT_SIX);
		loSelectionDetailsSummaryBean.setContractSourceId(HHSConstants.INT_ONE);
		String lsNextPage = aoRequest.getParameter(ApplicationConstants.NEXT_PAGE_PARAM);
		getPagingParams(aoRequest.getPortletSession(), loSelectionDetailsSummaryBean, lsNextPage,
				HHSConstants.SELECTION_DETAILS_SUMMARY_KEY);
		Channel loChannel = new Channel();
		loChannel.setData(HHSConstants.SELECTION_DETAILS_SUMMARY_BEAN, loSelectionDetailsSummaryBean);
		loChannel.setData(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_GROUP_SELECTION_DETAILS);
		Integer loGroupSelectionDetailsCount = (Integer) loChannel.getData(HHSConstants.GROUP_SELECTION_DETAILS_COUNT);
		aoRequest.setAttribute(HHSConstants.SELECTION_DETAILS_SUMMARY_LIST,
				loChannel.getData(HHSConstants.SELECTION_DETAILS_SUMMARY_LIST));
		aoRequest.getPortletSession().setAttribute(ApplicationConstants.ALERT_VIEW_PAGING_RECORDS,
				((loGroupSelectionDetailsCount == null) ? 0 : loGroupSelectionDetailsCount),
				PortletSession.APPLICATION_SCOPE);
		aoRequest.getPortletSession().setAttribute(HHSConstants.SORT_TYPE,
				loSelectionDetailsSummaryBean.getFirstSortType(), PortletSession.APPLICATION_SCOPE);
		aoRequest.getPortletSession().setAttribute(HHSConstants.SORT_BY,
				loSelectionDetailsSummaryBean.getSortColumnName(), PortletSession.APPLICATION_SCOPE);
		return new ModelAndView(HHSConstants.SELECTION_DETAILS_SUMMARY_JSP);
	}

	/**
	 * This method is used to Download the Contract Budget Summary.pdf and
	 * Contract Certification of Funds.Pdf from Filenet to WebContent\dbdDoc
	 * folder.
	 * <ul>
	 * <li>Get the lsContractId, lsbudgetId, lsProcurementId, lsAwardId,
	 * lsProcurementTitle from request</li>
	 * <li>Get SelectionDetailsSummaryBean from Session object</li>
	 * <li>Set lsRealpath,lsHeader, lsAwardId, lsbudgetId ,lsContractId ,
	 * lsProcurementId , lsProcurementTitle, loUserSession, lsOrgName,lsUserId,
	 * loProcCofBean</li>
	 * <li>Set channel parameters and execute transaction
	 * <b>DownloadPOFandBudgetSummaryDetails</b></li>
	 * </ul>
	 * <ul>
	 * <li>Method in R4</li>
	 * </ul>
	 * @param aoRequest RenderRequest object
	 * @throws ApplicationException If an Application Exception occurs New
	 * @param aoResourceRequestForProc Portal resource request object
	 * @param aoResourceResponseForProc Portal resource Response Object
	 */

	@ResourceMapping("downloadAllDocuments")
	protected void getDownloadProcurementDocument(ResourceRequest aoResourceRequestForProc,
			ResourceResponse aoResourceResponseForProc)
	{
		String lsBudgetId = null;
		String lsProcurementId = null;
		String lsProcurementTitle = null;
		String lsHeader = HHSConstants.EMPTY_STRING;
		String lsAwardId = null;
		ProcurementCOF loProcCofBean = new ProcurementCOF();
		HashMap<String, String> loRequiredParamMap = new HashMap<String, String>();
		String lsUserId = (String) aoResourceRequestForProc.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
		String lsMsg = null;
		PrintWriter loOutForProc = null;
		String lsZipFilePath = null;
		Channel loChannelForProc = new Channel();
		Boolean lsIsFinacialDocRequired = Boolean.FALSE;
		String lsProviderOrgId = null;
		try
		{
			lsProviderOrgId = (String) aoResourceRequestForProc.getParameter(HHSConstants.PROVIDER_ORG_ID);
			lsIsFinacialDocRequired = Boolean.valueOf((String) aoResourceRequestForProc
					.getParameter(HHSConstants.IS_FINANCIAL));
			String lsContractId = (String) aoResourceRequestForProc.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW);
			String lsOrgName = aoResourceRequestForProc.getParameter(HHSConstants.ORG_NAME);
			String lsRealpath = aoResourceRequestForProc.getPortletSession().getPortletContext()
					.getRealPath(HHSR5Constants.DBD_DOC_REAL_PATH);
			loRequiredParamMap.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, ApplicationConstants.EMPTY_STRING);
			loRequiredParamMap.put(P8Constants.PROPERTY_CE_MIME_TYPE, ApplicationConstants.EMPTY_STRING);
			loRequiredParamMap.put(HHSConstants.FILE_TYPE, ApplicationConstants.EMPTY_STRING);
			loRequiredParamMap.put(P8Constants.PROPERTY_CE_CONTRACT_ID, ApplicationConstants.EMPTY_STRING);
			P8UserSession loUserSession = (P8UserSession) aoResourceRequestForProc.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			lsContractId = aoResourceRequestForProc.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW);
			lsBudgetId = aoResourceRequestForProc.getParameter(HHSConstants.BUDGET_ID_WORKFLOW);
			lsProcurementId = aoResourceRequestForProc.getParameter(HHSConstants.PROCUREMENT_ID);
			lsProcurementTitle = aoResourceRequestForProc.getParameter(HHSConstants.PROCUREMENT_TITLE);
			lsAwardId = aoResourceRequestForProc.getParameter(HHSConstants.AWARD_ID);
			loChannelForProc.setData(HHSConstants.AO_OUTPUT_PATH, lsRealpath);
			loChannelForProc.setData(HHSConstants.HEADER_LABEL, lsHeader);
			loChannelForProc.setData(HHSConstants.AWARD_ID, lsAwardId);
			loChannelForProc.setData(HHSConstants.BUDGET_ID_KEY, lsBudgetId);
			loChannelForProc.setData(HHSConstants.CONTRACT_ID_KEY, lsContractId);
			loChannelForProc.setData(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
			loChannelForProc.setData(HHSConstants.PROCUREMENT_TITLE, lsProcurementTitle);
			loChannelForProc.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
			loChannelForProc.setData(HHSConstants.ORG_NAME, lsOrgName);
			loChannelForProc.setData(HHSConstants.USER_ID_2, lsUserId);
			loChannelForProc.setData(HHSConstants.FOLDER_NAME, HHSConstants.FORWARD_SLASH + lsOrgName
					+ HHSConstants.AWARD_DOCUMENT_PDF);
			loChannelForProc.setData(HHSConstants.PROCUREMENT_COF_BEAN, loProcCofBean);
			loChannelForProc.setData(HHSConstants.IS_FINANCIAL, lsIsFinacialDocRequired);
			loChannelForProc.setData(HHSConstants.USER_ORG_ID, (String) aoResourceRequestForProc.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE));
			loRequiredParamMap.put(P8Constants.PROPERTY_CE_CONTRACT_TITLE, ApplicationConstants.EMPTY_STRING);
			loChannelForProc.setData(
					HHSConstants.FINANCIAL_PDF_DOC_PATH,
					PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
							P8Constants.PREDEFINED_FOLDER_PATH_FINANCIAL_DOC)
							+ HHSConstants.FORWARD_SLASH
							+ lsContractId);
			loChannelForProc.setData(HHSConstants.PROVIDER_ORG_ID, lsProviderOrgId);
			HHSTransactionManager
					.executeTransaction(loChannelForProc, HHSConstants.DOWNLOAD_POF_BUDGET_SUMMARY_DETAILS);
			lsZipFilePath = (String) loChannelForProc.getData(HHSConstants.ZIP_PATH);
			if (lsZipFilePath == null)
			{
				lsMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			}
			loOutForProc = aoResourceResponseForProc.getWriter();
		}
		// Handle the Application Exception and log it into logger
		catch (ApplicationException aoExe)
		{
			try
			{
				LOG_OBJECT.Error("ApplicationException occured in downloading pdf from getDownloadProcurementDocument",
						aoExe);
				loOutForProc = aoResourceResponseForProc.getWriter();
				lsMsg = aoExe.getRootCause().getMessage();
			}
			catch (IOException aoIoEx)
			{
				LOG_OBJECT
						.Error("IO Exception occured while downloading award pdf from getDownloadProcurementDocument.",
								aoIoEx);

			}
		}
		// Handle the Exception and log it into logger
		catch (Exception aoExe)
		{
			LOG_OBJECT.Error("ApplicationException occured in downloading pdf from getDownloadProcurementDocument",
					aoExe);

		}
		// Finally block to be executed after creating a document in temporary
		// folder
		finally
		{
			showDownloadDialogOrErrorForZip(aoResourceResponseForProc, lsMsg, lsZipFilePath, loOutForProc);
		}
	}

	/**
	 * This method will handle pagination action from Selection Details Summary
	 * screen.
	 * 
	 * <ul>
	 * <li>1.Set navigation parameters in Response by calling method
	 * setNavigationParamsInRender from BaseControllerSM</li>
	 * <li>2.This method will check for next action parameter and call methods
	 * accordingly.</li>
	 * <li>3.Set required attributes in Response</li>
	 * </ul>
	 * <ul>
	 * </li>New Method in R4</li>
	 * </ul>
	 * 
	 * @param aoRequestForProc an Action Request object
	 * @param aoResponseForProc an Action Response object
	 */
	@ActionMapping(params = "submit_action=paginateSelectionDetailsSummary")
	protected void actionPaginateSelectionDetailsSummary(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
		setNavigationParamsInRender(aoRequest, aoResponse);
		aoResponse.setRenderParameter(ApplicationConstants.NEXT_PAGE_PARAM,
				aoRequest.getParameter(HHSConstants.NEXT_PAGE));
		aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.VIEW_SEL_DETAILS);
		aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.ACTION_SELECTION_DETAILS);
	}

	/***
	 * This method will handle sort action from Selection Details Summary screen
	 * by clicking any of the column headers
	 * 
	 * <ul>
	 * <li>1.Setting Navigation parameters in Response by calling method
	 * setNavigationParamsInRender from BaseControllerSM</li>
	 * <li>2.Get sortType and columnName values by calling parseQueryString()
	 * method from class PortalUtil.</li>
	 * <li>3.Get sorting details by calling method getSortDetailsFromXML() from
	 * class BaseController.</li>
	 * <li>4.Setting Selection Details Summary Bean and other required parameter
	 * in Response for successful rendering of Selection Details Summary</li>
	 * <ul>
	 * <ul>
	 * </li>New Method in R4</li>
	 * </ul>
	 * 
	 * @param aoSelectionDetailsSummaryBean SelectionDetailsSummaryBean
	 * @param aoRequest ActionRequest
	 * @param aoResponse ActionResponse
	 */
	@ActionMapping(params = "submit_action=sortSelectionDetailsSummary")
	protected void actionSortSelectionDetailsSummary(
			@ModelAttribute("SelectionDetailsSummaryBean") SelectionDetailsSummaryBean aoSelectionDetailsSummaryBean,
			ActionRequest aoRequest, ActionResponse aoResponse)
	{
		Map<String, String> loHmReqExceProp = new HashMap<String, String>();
		try
		{
			LOG_OBJECT.Debug("Entered into Selection Details Summary Sort Action::");
			setNavigationParamsInRender(aoRequest, aoResponse);
			String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			String lsSortType = PortalUtil.parseQueryString(aoRequest, HHSConstants.SORT_TYPE);
			String lsColumnName = PortalUtil.parseQueryString(aoRequest, HHSConstants.COLUMN_NAME);
			getSortDetailsFromXML(lsColumnName, lsUserOrgType,
					PortalUtil.parseQueryString(aoRequest, HHSConstants.SORT_GRID_NAME), aoSelectionDetailsSummaryBean,
					lsSortType);
			// Setting param for Rendering getEvaluationStatus Render Method
			aoRequest.getPortletSession().setAttribute(HHSConstants.SESSION_SELECTION_DETAILS_SUMMARY_BEAN,
					aoSelectionDetailsSummaryBean, PortletSession.PORTLET_SCOPE);
			aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID,
					aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			loHmReqExceProp.put(HHSConstants.PROCUREMENT_ID, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.VIEW_SEL_DETAILS);
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.ACTION_SELECTION_DETAILS);
		}
		// handling Application Exception while sorting evaluation result.
		catch (ApplicationException aoAppExp)
		{
			String lsErrorMsg = HHSConstants.ERROR_WHILE_PROCESSING_REQUEST;
			setExceptionMessageFromAction(aoResponse, lsErrorMsg, ApplicationConstants.MESSAGE_FAIL_TYPE,
					loHmReqExceProp, aoAppExp);
		}
		// Handling Exception other than ApplicationException
		catch (Exception aoExp)
		{
			String lsErrorMsg = HHSConstants.ERROR_WHILE_PROCESSING_REQUEST;
			LOG_OBJECT.Error("Exception Occured while sorting Award Contracts summary: ", aoExp);
			setExceptionMessageFromAction(aoResponse, lsErrorMsg, ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
	}
}
