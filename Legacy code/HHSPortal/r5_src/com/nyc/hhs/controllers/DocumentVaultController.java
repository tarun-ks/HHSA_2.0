package com.nyc.hhs.controllers;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;
import org.springframework.web.portlet.mvc.ResourceAwareController;

import com.filenet.api.util.Id;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.ComponentMappingConstant;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.Document;
import com.nyc.hhs.model.DocumentBean;
import com.nyc.hhs.model.DocumentPropertiesBean;
import com.nyc.hhs.model.DocumentsSelFromDocVault;
import com.nyc.hhs.model.ExtendedDocument;
import com.nyc.hhs.model.FolderMappingBean;
import com.nyc.hhs.model.ProviderBean;
import com.nyc.hhs.model.UserThreadLocal;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.thread.DocumentVaultParallelProcessHandler;
import com.nyc.hhs.thread.DocumentVaultParallelProcessor;
import com.nyc.hhs.util.ApplicationSession;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.FileNetOperationsUtils;
import com.nyc.hhs.util.HHSPortalUtil;
import com.nyc.hhs.util.MultipartActionRequestParser;
import com.nyc.hhs.util.PortalUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This class is basically used to handle all the actions requested by the user
 * from the document vault screen. It process all the request by executing
 * certain transactions and redirect user to the required page as per the
 * result. When user performs any action handleActionRequestInternal method will
 * execute the transactions and redirect to handleRenderRequestInternal method
 * which will render UI to show the required result to the user. Removing
 * extended class AbstractController and adding annotations in Release 5
 * 
 */
@Controller(value = "enhanceddocumentVaultController")
@RequestMapping("view")
public class DocumentVaultController implements ResourceAwareController {
	private static final LogInfo LOG_OBJECT = new LogInfo(
			DocumentVaultController.class);

	/**
	 * This method is used to render the document vault screen for provider,city
	 * and agency
	 * <ul>
	 * <li>This method will fetch the document list by calling
	 * <i>getDocumentList</i> method of the class <b>FileNetOperationsUtils</b></li>
	 * <li>Depending upon the organization type this method will return the
	 * specific jsp path to render</li>
	 * <li>Method updated in Release 5</li>
	 * </ul>
	 * 
	 * @param aoRequest
	 *            RenderRequest Object
	 * @param aoResponse
	 *            RenderResponse Object
	 * @return String value of the jsp path
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@SuppressWarnings("unchecked")
	@RenderMapping
	protected ModelAndView handleRenderRequestInternal(RenderRequest aoRequest,
			RenderResponse aoResponse) throws ApplicationException {
		String lsFormPath = null;
		ModelAndView loModelAndView = null;
		String lsFolderPath = null;
		String lsFolderId = null;
		PortletSession loSession = aoRequest.getPortletSession();
		String lsSectionName = aoRequest.getParameter(HHSConstants.SECTION);
		String lsSubSectionName = aoRequest
				.getParameter(HHSConstants.SUB_SECTION);
		String lsCityUserSearchProviderId = (String) aoRequest
				.getPortletSession().getAttribute(
						HHSR5Constants.CITY_USER_SEARCH_PROVIDER_ID_NEW,
						PortletSession.APPLICATION_SCOPE);
		//**  start QC 8914 R7.2 read only role **/
		String role_current = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_ROLE_CURRENT, PortletSession.APPLICATION_SCOPE);
		
		// Adding if condition in Emergency Release 4.0.2
		if (null == lsCityUserSearchProviderId
				|| lsCityUserSearchProviderId.isEmpty()) {
			String lsManageOrgType = HHSPortalUtil.parseQueryString(aoRequest,
					"documentOriginator");
			String lsManageOrgId = HHSPortalUtil.parseQueryString(aoRequest,
					"cityUserSearchProviderId");
			if (null != lsManageOrgId
					&& lsManageOrgId.contains(HHSR5Constants.TILD)) {
				lsCityUserSearchProviderId = lsManageOrgId;
			} else {
				lsCityUserSearchProviderId = lsManageOrgId
						+ HHSR5Constants.TILD + lsManageOrgType;
			}

		}
		String lsAgencyName = (String) aoRequest.getPortletSession()
				.getAttribute(ApplicationConstants.AGENCY_NAME,
						PortletSession.APPLICATION_SCOPE);
		if (StringUtils.isBlank(lsAgencyName)) {
			lsAgencyName = (String) ApplicationSession.getAttribute(aoRequest,
					true, HHSR5Constants.PROV_NAME_SHARED_DOC);
		}
		HashMap<String, Object> loFilterProps = new HashMap<String, Object>();
		String lsProviderName = null;
		List<String> loDocTypeList = new ArrayList<String>();
		try {

			String lsOrgType = (String) loSession.getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE,
					PortletSession.APPLICATION_SCOPE);

			//**** Start QC 8719 R7.0.0 Move Shared Documents FileNet call from Home page to DocumentVault
			if (ApplicationConstants.AGENCY_ORG.equalsIgnoreCase(lsOrgType))
			{					
				String loOrgId = (String) loSession.getAttribute(
						ApplicationConstants.KEY_SESSION_USER_ORG,	PortletSession.APPLICATION_SCOPE);
				LOG_OBJECT.Info("DocumentVaultController:: Start loading Shared Documents for Agency :: "+loOrgId);
				P8UserSession lsUserSession = (P8UserSession) loSession.getAttribute(
						ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
				Map<String, String> loMProvider = FileNetOperationsUtils.getSharedAgencyProviderList( lsUserSession,
						P8Constants.PROPERTY_CE_SHARED_AGENCY_ID, loOrgId, aoRequest);
				
				ApplicationSession.setAttribute(loMProvider, aoRequest, "sharedDocForProvider");
				
				aoRequest.getPortletSession().setAttribute("sharedOrgDetailsForAgency", loMProvider,
							PortletSession.APPLICATION_SCOPE);
				LOG_OBJECT.Info("DocumentVaultController:: Finish loading Shared Documents for Agency :: "+loOrgId);
			}
			//***** End QC 8719 R7.0.0

			String lsNextPageReqParam = HHSPortalUtil
					.parseQueryString(
							aoRequest,
							ApplicationConstants.DOCUMENT_VAULT_NEXT_PAGE_VALUE_PARAMETER);
			// Added for Release 5- selectVault
			String lsSelectVaultFlag = aoRequest
					.getParameter(HHSR5Constants.CONTROLLER_PARAM);
			// End of release 5
			String lsHeaderFlag = HHSPortalUtil.parseQueryString(aoRequest,
					HHSR5Constants.HEADER_CLICK);
			aoRequest.setAttribute(HHSR5Constants.HEADER_CLICK, lsHeaderFlag);
			if (StringUtils.isNotBlank(lsHeaderFlag)
					|| StringUtils.isNotBlank(lsSelectVaultFlag)) {
				aoRequest.getPortletSession().removeAttribute(
						HHSR5Constants.CURRENT_FOLDER_ID);
				aoRequest.getPortletSession().removeAttribute(
						HHSConstants.FILENET_FILTER_MAP,
						PortletSession.PORTLET_SCOPE);
				// Added for select vault to remove folderId
				aoRequest.getPortletSession().removeAttribute(
						HHSR5Constants.FOLDER_ID);
				// end
			} else {
				loFilterProps = (HashMap<String, Object>) aoRequest
						.getPortletSession().getAttribute(
								HHSConstants.FILENET_FILTER_MAP,
								PortletSession.PORTLET_SCOPE);
				if (null == loFilterProps) {
					loFilterProps = new HashMap<String, Object>();
				}
			}
			loFilterProps.put(HHSR5Constants.PRESENT_ORG_ID, lsOrgType);
			// Adding two parameters below in filtermap for Defect # 8150
			loFilterProps.put("presentOrg", (String) loSession.getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ORG,
					PortletSession.APPLICATION_SCOPE));
			loFilterProps.put("parentId",
					HHSPortalUtil.parseQueryString(aoRequest, "parentId"));
			setAgencyListInSession(aoRequest);
			// Added ternary operation to get value of error message and error
			// type from either request or session
			String lsMessage = (null == aoRequest
					.getParameter(ApplicationConstants.ERROR_MESSAGE)) ? (String) aoRequest
					.getPortletSession().getAttribute(
							ApplicationConstants.ERROR_MESSAGE,
							PortletSession.APPLICATION_SCOPE) : aoRequest
					.getParameter(ApplicationConstants.ERROR_MESSAGE);

			String lsMessageType = (null == aoRequest
					.getParameter(ApplicationConstants.ERROR_MESSAGE_TYPE)) ? (String) aoRequest
					.getPortletSession().getAttribute(
							ApplicationConstants.ERROR_MESSAGE_TYPE,
							PortletSession.APPLICATION_SCOPE) : aoRequest
					.getParameter(ApplicationConstants.ERROR_MESSAGE_TYPE);
			String lsNewFolderId = aoRequest
					.getParameter(HHSR5Constants.NEW_FLD_ID);
			// End
			String lsParentNodeReqParam = (String) loSession.getAttribute(
					ApplicationConstants.DOCUMENT_VAULT_PARENT_NODE_PARAMETER,
					PortletSession.APPLICATION_SCOPE);

			String lsSortByReqParam = HHSPortalUtil.parseQueryString(aoRequest,
					ApplicationConstants.DOCUMENT_VAULT_SORT_BY_PARAMETER);
			String lsSortTypeReqParam = HHSPortalUtil.parseQueryString(
					aoRequest,
					ApplicationConstants.DOCUMENT_VAULT_SORT_TYPE_PARAMETER);
			P8UserSession loUserSession = (P8UserSession) loSession
					.getAttribute(ApplicationConstants.FILENET_SESSION_OBJECT,
							PortletSession.APPLICATION_SCOPE);
			String lsJspName = HHSPortalUtil.parseQueryString(aoRequest,
					"jspName");
			String lsUserOrg = aoRequest.getParameter(HHSR5Constants.ORG_NAME);
			String lsUserOrgType = aoRequest
					.getParameter(HHSConstants.LS_USER_ORG_TYPE);

			// code to get list of doctype for combobox
			if (StringUtils.isNotBlank(lsCityUserSearchProviderId)
					&& StringUtils.isNotBlank(lsHeaderFlag)
					&& lsHeaderFlag.equalsIgnoreCase(HHSR5Constants.FALSE)) {
				String lsUserManageOrgType = lsCityUserSearchProviderId
						.substring(lsCityUserSearchProviderId
								.indexOf(HHSConstants.DELIMETER_SIGN) + 1);
				loDocTypeList = FileNetOperationsUtils.getDocType(
						lsUserManageOrgType, null,
						HHSR5Constants.DOC_TYPE_LISTING);
			} else {
				loDocTypeList = FileNetOperationsUtils.getDocType(lsOrgType,
						null, HHSR5Constants.DOC_TYPE_LISTING);
			}
			ApplicationSession.setAttribute(loDocTypeList, aoRequest,
					HHSR5Constants.DROPDOWN_DOC_TYPE1);
			aoRequest.setAttribute(HHSR5Constants.DROPDOWN_DOC_TYPE1,
					loDocTypeList);
			List<String> loDocTypeListOrg = FileNetOperationsUtils.getDocType(
					lsOrgType, HHSR5Constants.DOC_TYPE_FOR_OTHER_ORG, null);
			ApplicationSession.setAttribute(loDocTypeListOrg, aoRequest,
					HHSR5Constants.DOC_TYPE_DROP_DOWN_LIST_OTHER_ORG);
			aoRequest.setAttribute(
					HHSR5Constants.DOC_TYPE_DROP_DOWN_LIST_OTHER_ORG,
					loDocTypeListOrg);
			// addd below code for populating document type for combo box
			setDocTypesForComboBox(aoRequest, lsOrgType);
			if (null == lsMessageType && null == lsMessage) {
				aoRequest.setAttribute(
						ApplicationConstants.ERROR_MESSAGE,
						aoRequest.getPortletSession().getAttribute(
								ApplicationConstants.MESSAGE));
				aoRequest
						.setAttribute(
								ApplicationConstants.ERROR_MESSAGE_TYPE,
								aoRequest
										.getPortletSession()
										.getAttribute(
												ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE));
			} else {
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
						lsMessage);
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
						lsMessageType);
				aoRequest
						.setAttribute(
								HHSR5Constants.SELECTED_FOLDER_ID,
								aoRequest
										.getParameter(HHSR5Constants.STRING_SELETED_FLDR));
				aoRequest
						.setAttribute(HHSR5Constants.NEW_FLD_ID, lsNewFolderId);
			}
			if (lsUserOrgType == null && lsUserOrg == null) {
				lsUserOrgType = (String) loSession.getAttribute(
						ApplicationConstants.KEY_SESSION_ORG_TYPE,
						PortletSession.APPLICATION_SCOPE);
				if (lsUserOrgType
						.equalsIgnoreCase(ApplicationConstants.CITY_ORG)) {
					lsUserOrg = lsUserOrgType;

				} else {
					lsUserOrg = (String) loSession.getAttribute(
							ApplicationConstants.KEY_SESSION_USER_ORG,
							PortletSession.APPLICATION_SCOPE);
				}
			}
			if (null != lsCityUserSearchProviderId
					&& StringUtils.isNotBlank(lsHeaderFlag)
					&& !lsHeaderFlag.equalsIgnoreCase(HHSR5Constants.TRUE)) {
				aoRequest.setAttribute(HHSR5Constants.MANAGE_ORGANIZATION,
						HHSR5Constants.MANAGE_ORG);
				String[] loTemp = lsCityUserSearchProviderId
						.split(ApplicationConstants.TILD);
				lsUserOrgType = loTemp[1];
				lsUserOrg = loTemp[0];
				aoRequest.setAttribute("selectedOrgTypeForLinkage",
						lsUserOrgType);
				loFilterProps.put("parentId", "root");
				LOG_OBJECT.Debug("++++++handleRenderRequestInternal++++getDocumentListForCitySearchProvider++++++++++++++++++");
				getDocumentListForCitySearchProvider(aoRequest,
						lsCityUserSearchProviderId, lsAgencyName,
						loFilterProps, lsUserOrg, lsUserOrgType);

			}
			if (null != lsJspName
					&& !lsJspName.isEmpty()
					&& lsJspName
							.equalsIgnoreCase(HHSR5Constants.RECYCLE_BIN_ID)) {
				lsFolderPath = FileNetOperationsUtils.setFolderPath(
						lsUserOrgType, lsUserOrg, HHSR5Constants.RECYCLE_BIN);
				aoRequest.getPortletSession().setAttribute(
						HHSR5Constants.FOLDER_PATH, lsFolderPath);
			} else {
				if (null == aoRequest.getPortletSession().getAttribute(
						HHSR5Constants.CURRENT_FOLDER_ID)) {
					String loSessionFolderId = (String) aoRequest
							.getPortletSession().getAttribute(
									HHSR5Constants.FOLDER_ID);
					String loParamFolderId = (String) aoRequest
							.getParameter(HHSR5Constants.FOLDER_ID);
					if (StringUtils.isNotBlank(loSessionFolderId)) {
						lsFolderId = loSessionFolderId;
					} else if (StringUtils.isNotBlank(loParamFolderId)) {
						lsFolderId = loParamFolderId;
					} else {
						lsFolderPath = FileNetOperationsUtils.setFolderPath(
								lsUserOrgType, lsUserOrg,
								HHSR5Constants.DOCUMENT_VAULT);
						aoRequest.getPortletSession().setAttribute(
								HHSR5Constants.FOLDER_PATH, lsFolderPath);
					}
				} else {
					lsFolderId = (String) aoRequest.getPortletSession()
							.getAttribute(HHSR5Constants.CURRENT_FOLDER_ID);
				}

			}
			loSession.removeAttribute(HHSR5Constants.DUE_DATE,
					PortletSession.APPLICATION_SCOPE);

			Document loDocument = new Document();
						
			List<Document> loDocumentList = new ArrayList<Document>();
			FileNetOperationsUtils.getOrgTypeFilter(loFilterProps, lsUserOrg,
					lsUserOrgType);
			FileNetOperationsUtils.setPropFilter(loFilterProps, lsFolderPath,
					lsFolderId, lsUserOrgType, lsUserOrg);
			aoRequest.setAttribute(HHSR5Constants.UPDATED_FOLDER_NAME,
					aoRequest.getParameter(HHSR5Constants.UPDATED_FOLDER_NAME));
			// Added for Release 5- selectVault
			if (null != lsSelectVaultFlag
					&& !lsSelectVaultFlag.isEmpty()
					&& lsSelectVaultFlag
							.equalsIgnoreCase(HHSConstants.ADD_DOC_FROM_VAULT)) {
				loFilterProps.put(P8Constants.PROPERTY_CE_PROVIDER_ID,
						lsUserOrg);
				loFilterProps.put(
						P8Constants.PROPERTY_CE_IS_SYSTEM_DOC_CATEGORY,
						Boolean.FALSE);
				loFilterProps.put(HHSR5Constants.IS_FILTER, aoRequest
						.getParameter(HHSR5Constants.SELECT_VAULT_FLAG));
				aoRequest
						.setAttribute(
								P8Constants.PROPERTY_PE_UPLOADED_DOC_TYPE,
								aoRequest
										.getParameter(P8Constants.PROPERTY_PE_UPLOADED_DOC_TYPE));
				aoRequest.setAttribute(HHSR5Constants.STRING_SELECT_VAULT_FLAG,
						lsSelectVaultFlag);
				aoRequest
						.setAttribute(
								HHSR5Constants.SELECT_DOC_RELEASE,
								aoRequest
										.getParameter(HHSR5Constants.SELECT_DOC_RELEASE));
				// Added for defect# 7368
				aoRequest.setAttribute(HHSR5Constants.DOC_TYPE,
						aoRequest.getParameter(ApplicationConstants.DOCS_TYPE));
				// End
			}
			// End of release 5
			if (CommonUtil.getConditionalRoleDisplay(
					ComponentMappingConstant.DV_S030_PAGE, loSession)
					|| CommonUtil.getConditionalRoleDisplay(
							ComponentMappingConstant.DV_S031_PAGE, loSession)) {
				if (null != aoRequest
						.getParameter(HHSR5Constants.CONTROLLER_NAME)) {
					loFilterProps.put(HHSR5Constants.CONTROLLER_NAME, aoRequest
							.getParameter(HHSR5Constants.CONTROLLER_NAME));
					loFilterProps.put(
							HHSR5Constants.CUSTOM_ORGANIZATION,
							aoRequest.getPortletSession().getAttribute(
									HHSR5Constants.CUSTOM_ORGANIZATION));

				}

				if (null != aoRequest.getParameter(HHSR5Constants.NEXT_PAGE)
						|| lsNextPageReqParam != null
						&& !lsNextPageReqParam.isEmpty()) {
					lsFormPath = getDocumentListForPagingAndSorting(aoRequest,
							lsParentNodeReqParam, lsNextPageReqParam,
							lsSortByReqParam, lsSortTypeReqParam,
							loUserSession, lsUserOrg, lsUserOrgType,
							loDocument, loDocumentList, loFilterProps);
					

				} else {
					// Adding below code For Release 4.0.2
					String lsNextAction = null;
					lsNextAction = HHSPortalUtil.parseQueryString(aoRequest,
							"headerJSPName");
					if (null != lsNextAction && !lsNextAction.isEmpty()
							&& lsNextAction.equalsIgnoreCase("shareDocheader")) {
						String lsOrganizationType = (String) aoRequest
								.getPortletSession()
								.getAttribute(
										ApplicationConstants.KEY_SESSION_ORG_TYPE,
										PortletSession.APPLICATION_SCOPE);
						String lsOrgId = (String) aoRequest
								.getPortletSession()
								.getAttribute(
										ApplicationConstants.KEY_SESSION_USER_ORG,
										PortletSession.APPLICATION_SCOPE);
						Map<String, String> loMProvider = null;
						if (lsOrganizationType
								.equalsIgnoreCase(ApplicationConstants.AGENCY_ORG)) {
							loMProvider = FileNetOperationsUtils
									.getSharedAgencyProviderList(
											loUserSession,
											P8Constants.PROPERTY_CE_SHARED_AGENCY_ID,
											lsOrgId, aoRequest);
						} else {
							loMProvider = FileNetOperationsUtils
									.getSharedAgencyProviderList(
											loUserSession,
											P8Constants.PROPERTY_CE_SHARED_PROVIDER_ID,
											lsOrgId, aoRequest);
						}
						ApplicationSession.setAttribute(loMProvider, aoRequest,
								"sharedDocForProvider");
						FileNetOperationsUtils.createFilterForNotificationLink(
								loFilterProps, aoRequest);
						aoRequest.getPortletSession().setAttribute(
								HHSR5Constants.HOME_PAGE_MANAGE_ORG_FLAG, true,
								PortletSession.APPLICATION_SCOPE);
						lsSectionName = HHSR5Constants.SHARED_DOC;
						lsSubSectionName = ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_LIST_PARAMETER;
						lsCityUserSearchProviderId = HHSPortalUtil
								.parseQueryString(aoRequest,
										"documentOriginator");
						aoRequest.setAttribute(
								HHSR5Constants.MANAGE_ORGANIZATION,
								HHSR5Constants.MANAGE_ORG);

						aoRequest.getPortletSession().setAttribute(
								HHSR5Constants.TREE_MANAGE_ORG_ID,
								HHSPortalUtil.parseQueryString(aoRequest,
										"cityUserSearchProviderId"));
						aoRequest.setAttribute(
								HHSR5Constants.TREE_MANAGE_ORG_ID,
								HHSPortalUtil.parseQueryString(aoRequest,
										"cityUserSearchProviderId"));

						String lsAgencyNameForManage = FileNetOperationsUtils
								.getAgencyNameForManage(
										(TreeSet) BaseCacheManagerWeb
												.getInstance()
												.getCacheObject(
														ApplicationConstants.AGENCY_LIST),
										HHSPortalUtil.parseQueryString(
												aoRequest,
												"cityUserSearchProviderId"));
						if (null == lsAgencyNameForManage
								|| lsAgencyNameForManage.isEmpty()) {
							List<ProviderBean> loProvList = (List<ProviderBean>) BaseCacheManagerWeb
									.getInstance().getCacheObject(
											HHSR5Constants.PROV_LIST);
							lsAgencyNameForManage = FileNetOperationsUtils
									.getProviderName(loProvList, HHSPortalUtil
											.parseQueryString(aoRequest,
													"cityUserSearchProviderId"));
						}
						if (null == lsAgencyNameForManage
								|| lsAgencyNameForManage.isEmpty()
								|| lsAgencyNameForManage
										.equalsIgnoreCase("city_org")) {
							lsAgencyNameForManage = "HHS Accelerator";
						}
						ApplicationSession.setAttribute(lsAgencyNameForManage,
								aoRequest, HHSR5Constants.PROV_NAME_SHARED_DOC);
						aoRequest.getPortletSession().setAttribute(
								ApplicationConstants.KEY_SESSION_ORG_NAME_CITY,
								lsAgencyNameForManage,
								PortletSession.APPLICATION_SCOPE);

						Set<String> loSharedOrgSessionList = new HashSet<String>();
						/*
						 * if (null !=
						 * aoRequest.getPortletSession().getAttribute
						 * (HHSR5Constants.SHARED_ORG_SESSION_LIST)) {
						 * loSharedOrgSessionList = (Set<String>)
						 * aoRequest.getPortletSession().getAttribute(
						 * HHSR5Constants.SHARED_ORG_SESSION_LIST); }
						 */
						String asCityUserSearchProviderIdReverse = HHSPortalUtil
								.parseQueryString(aoRequest,
										"cityUserSearchProviderId")
								+ HHSR5Constants.TILD
								+ HHSPortalUtil.parseQueryString(aoRequest,
										"documentOriginator");
						loSharedOrgSessionList.add(asCityUserSearchProviderIdReverse
								+ HHSConstants.HYPHEN + lsAgencyNameForManage);
						aoRequest.getPortletSession().setAttribute(
								HHSR5Constants.SHARED_ORG_SESSION_LIST,
								loSharedOrgSessionList);
						aoRequest.getPortletSession().setAttribute(
								"cityUserSearchProviderId",
								asCityUserSearchProviderIdReverse,
								PortletSession.APPLICATION_SCOPE);
						

					}
					// End code For Release 4.0.2
					FileNetOperationsUtils.reInitializePageIterator(loSession,
							loUserSession);
					aoRequest.setAttribute(HHSR5Constants.CLICKED_SEARCH,
							loSession.getAttribute(
									HHSR5Constants.CLICKED_SEARCH,
									PortletSession.PORTLET_SCOPE));
					
					LOG_OBJECT.Debug("560+++handleRenderRequestInternal+++getDocumentList+++++++++++");
					lsFormPath = FileNetOperationsUtils
							.getDocumentList(aoRequest, loUserSession,
									loDocumentList, loDocument, lsUserOrgType,
									lsUserOrg, loFilterProps, lsSortByReqParam,
									lsSortTypeReqParam);
					}
				
				/*[Start] R7.2.0 QC8914	Set indicator for Access control	 */
				if( ApplicationConstants.ROLE_OBSERVER.equalsIgnoreCase((String)loSession.getAttribute(ApplicationConstants.KEY_SESSION_ROLE_CURRENT, PortletSession.APPLICATION_SCOPE)))  
				{
				    setIndecatorForReadOnlyRole(loDocumentList, ApplicationConstants.ROLE_OBSERVER);
				    
				}
				/*[End] R7.2.0 QC8914 Set indicator for Access control     */ 

				aoRequest.setAttribute(
						ApplicationConstants.SESSION_DOCUMENT_LIST,
						loDocumentList);
				aoRequest.setAttribute(
						ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_PARAMETER,
						loDocument);

				ApplicationSession.setAttribute(loDocumentList, aoRequest,
						ApplicationConstants.SESSION_DOCUMENT_LIST);
				FileNetOperationsUtils.setReqRequestParameter(loSession,
						aoRequest, loUserSession, lsNextPageReqParam,
						lsSortByReqParam, lsSortTypeReqParam,
						lsParentNodeReqParam);
			}
			if (null != lsJspName && !lsJspName.isEmpty()
					&& lsJspName.equalsIgnoreCase(HHSR5Constants.RECYCLE_BIN)) {
				loModelAndView = new ModelAndView(lsJspName);
			} else if (null != lsSectionName
					&& null != lsSubSectionName
					&& lsSectionName
							.equalsIgnoreCase(HHSR5Constants.SHARED_DOC)
					&& lsSubSectionName
							.equalsIgnoreCase(ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_LIST_PARAMETER)
					&& null != lsCityUserSearchProviderId
					&& !lsCityUserSearchProviderId
							.contains(ApplicationConstants.AGENCY_ORG)
					&& !lsCityUserSearchProviderId
							.contains(ApplicationConstants.CITY_ORG)) {
				lsFormPath = HHSR5Constants.SHARE_DOC_HEADER;
				aoRequest.setAttribute(ApplicationConstants.FILE_TO_INCLUDE,
								"/portlet/application/enhanceddocumentvault/provdocumentlist.jsp");
				ApplicationSession.setAttribute(
						PortalUtil.parseQueryString(aoRequest,
								HHSR5Constants.CITY_USER_SEARCH_PROVIDER_ID),
						aoRequest,
						HHSR5Constants.CITY_USER_SEARCH_PROVIDER_ID_NEW);
				ApplicationSession.setAttribute(lsProviderName, aoRequest,
						HHSR5Constants.PROV_NAME_SHARED_DOC);
				loModelAndView = new ModelAndView(lsFormPath);
			} else {

				if (StringUtils.isNotBlank(lsJspName)
						&& !"documentlocation".equalsIgnoreCase(lsJspName)) {
					loModelAndView = new ModelAndView(lsJspName);
				} else {
					loModelAndView = new ModelAndView(lsFormPath);
				}
				lsProviderName = FileNetOperationsUtils.getAgencyNameForManage(
						(TreeSet) BaseCacheManagerWeb.getInstance()
								.getCacheObject(
										ApplicationConstants.AGENCY_LIST),
						PortalUtil.parseQueryString(aoRequest,
								HHSR5Constants.CITY_USER_SEARCH_PROVIDER_ID));
				if (StringUtils.isNotBlank(lsHeaderFlag)
						&& !lsHeaderFlag.equalsIgnoreCase("true")) {
					ApplicationSession.setAttribute(lsAgencyName, aoRequest,
							HHSR5Constants.PROV_NAME_SHARED_DOC);
				}
				ApplicationSession.setAttribute(
						PortalUtil.parseQueryString(aoRequest,
								HHSR5Constants.CITY_USER_SEARCH_PROVIDER_ID),
						aoRequest,
						HHSR5Constants.CITY_USER_SEARCH_PROVIDER_ID_NEW);
			}
			
			String lsFlag = PortalUtil.parseQueryString(aoRequest,
					HHSR5Constants.HEADER_CLICK);
			if (null != lsFlag && !lsFlag.isEmpty()
					&& lsFlag.equalsIgnoreCase(HHSR5Constants.TRUE)) {
				aoRequest.getPortletSession().setAttribute(
						HHSR5Constants.HOME_PAGE_MANAGE_ORG_FLAG, false,
						PortletSession.APPLICATION_SCOPE);
			} else if (null != lsFlag && !lsFlag.isEmpty()
					&& lsFlag.equalsIgnoreCase(HHSR5Constants.FALSE)) {
				aoRequest.getPortletSession().setAttribute(
						HHSR5Constants.HOME_PAGE_MANAGE_ORG_FLAG, true,
						PortletSession.APPLICATION_SCOPE);
			}
            // Start QC9665 R 9.3.2 Citywide cyber assessment program (CCAP) - vulnerabilities -- Users Can Access Other User's Uploaded Documents
			loSession.setAttribute(ApplicationConstants.SESSION_DOCUMENT_LIST, loDocumentList, PortletSession.APPLICATION_SCOPE);
			LOG_OBJECT.Info("save Document List in Session on Application scope");
			//End QC9665 R 9.3.2 Citywide cyber assessment program (CCAP) - vulnerabilities -- Users Can Access Other User's Uploaded Documents
			
			// Added for SelectVault
			if (null != aoRequest.getParameter(HHSR5Constants.CONTROLLER_PARAM)) {
				lsFormPath = addDocumentBeanPropsForSelectDoc(aoRequest);
				
				return new ModelAndView(lsFormPath);
			}
			// end
		} catch (ApplicationException aoExp) {
			String lsErrorMsg = aoExp.toString();
			lsErrorMsg = lsErrorMsg.substring(
					lsErrorMsg.lastIndexOf(HHSR5Constants.COLON) + 1,
					lsErrorMsg.length()).trim();
			if (lsErrorMsg.equalsIgnoreCase(HHSR5Constants.EMPTY_STRING)) {
				lsErrorMsg = "Internal Error Occured While Processing Your Request";
			}
			LOG_OBJECT.Error("Application Exception in Document Vault", aoExp);
			loSession.setAttribute(
					ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
					ApplicationConstants.DOCUMENT_EXCEPTION,
					PortletSession.PORTLET_SCOPE);
			loSession.setAttribute(ApplicationConstants.ERROR_MESSAGE,
					lsErrorMsg, PortletSession.PORTLET_SCOPE);
			loSession.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE,
					PortletSession.PORTLET_SCOPE);
			loSession.setAttribute(ApplicationConstants.ERROR_MAP,
					aoExp.getContextData(), PortletSession.PORTLET_SCOPE);
			
			loModelAndView = new ModelAndView(
					ApplicationConstants.ERROR_HANDLER1);
		}
		return loModelAndView;

	}

	
	/**
	 * This method is extracted in release 5 from hadleRenderRequestInternal
	 * method, this method will set the required filter parameters when an
	 * accelerator user will search for other organizations documents.
	 * 
	 * @param aoRequest
	 *            - RenderRequest object
	 * @param asCityUserSearchProviderId
	 *            - id that user is searching for
	 * @param asAgencyName
	 *            - searching agency name
	 * @param aoFilterProps
	 *            - filter criteria to create query
	 * @param asUserOrg
	 *            -user organization id
	 * @param asUserOrgType
	 *            - user organization type
	 */
	private void getDocumentListForCitySearchProvider(RenderRequest aoRequest,
			String asCityUserSearchProviderId, String asAgencyName,
			HashMap<String, Object> aoFilterProps, String asUserOrg,
			String asUserOrgType) {
	
		aoRequest.getPortletSession().setAttribute(
				HHSR5Constants.TREE_MANAGE_ORG_ID, asUserOrg);
		aoRequest.setAttribute(HHSR5Constants.TREE_MANAGE_ORG_ID, asUserOrg);
		aoRequest.setAttribute(HHSR5Constants.TREE_MANAGER_ORG_TYPE,
				asUserOrgType);
		aoFilterProps.put(HHSR5Constants.CONTROLLER_NAME,
				HHSR5Constants.SELECT_ORGANIZATION);
		aoFilterProps.put(HHSR5Constants.CUSTOM_ORGANIZATION, asUserOrg);
		aoRequest.getPortletSession().removeAttribute(
				HHSR5Constants.SHARED_ORG_SESSION_LIST);
		Set<String> loSharedOrgSessionList = new HashSet<String>();
		if (null != aoRequest.getPortletSession().getAttribute(
				HHSR5Constants.SHARED_ORG_SESSION_LIST)) {
			loSharedOrgSessionList = (Set<String>) aoRequest
					.getPortletSession().getAttribute(
							HHSR5Constants.SHARED_ORG_SESSION_LIST);
		}
		loSharedOrgSessionList.add(asCityUserSearchProviderId
				+ HHSConstants.HYPHEN + asAgencyName);
		aoRequest.getPortletSession().setAttribute(
				HHSR5Constants.SHARED_ORG_SESSION_LIST, loSharedOrgSessionList);
	}

	/**
	 * This method is added as a part of release 5 , this method will be used to
	 * fetch and set the document types in session It will read the document
	 * type configuration file from cache and get the document types.
	 * 
	 * @param aoRequest
	 *            RenderRequest Object
	 * @param lsOrgType
	 *            Organization type
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	private void setDocTypesForComboBox(RenderRequest aoRequest,
			String lsOrgType) throws ApplicationException {
		// Added for agency shared search to get all doctypes regardless of
		// usertype
		List<String> loDocTypeListSharedAgencyList = FileNetOperationsUtils
				.getDocType(lsOrgType, HHSR5Constants.AGENCY_SHARED_DOC_TYPE,
						null);
		ApplicationSession.setAttribute(loDocTypeListSharedAgencyList,
				aoRequest, HHSR5Constants.AGENCY_SHARED_DOC_TYPE_DROPDOWN);
		aoRequest.setAttribute(HHSR5Constants.AGENCY_SHARED_DOC_TYPE_DROPDOWN,
				loDocTypeListSharedAgencyList);
		// End
		// Agency DocType list
		List<String> loDocTypeListAgency = FileNetOperationsUtils
				.getDocType(HHSConstants.USER_AGENCY, null,
						HHSR5Constants.DOC_TYPE_LISTING);
		ApplicationSession.setAttribute(loDocTypeListAgency, aoRequest,
				HHSR5Constants.AGENCY_DOC_TYPE);
		aoRequest.setAttribute(HHSR5Constants.AGENCY_DOC_TYPE,
				loDocTypeListAgency);
		// End
		// Provider DocType list
		List<String> loDocTypeListProvider = FileNetOperationsUtils.getDocType(
				HHSConstants.PROVIDER_ORG, null,
				HHSR5Constants.DOC_TYPE_LISTING);
		ApplicationSession.setAttribute(loDocTypeListProvider, aoRequest,
				HHSR5Constants.PROVIDER_DOC_TYPE);
		aoRequest.setAttribute(HHSR5Constants.PROVIDER_DOC_TYPE,
				loDocTypeListProvider);
		// End
		// City DocType list
		List<String> loDocTypeListCity = FileNetOperationsUtils.getDocType(
				HHSConstants.USER_CITY, null, HHSR5Constants.DOC_TYPE_LISTING);
		ApplicationSession.setAttribute(loDocTypeListCity, aoRequest,
				HHSR5Constants.CITY_DOC_TYPE);
		aoRequest.setAttribute(HHSR5Constants.CITY_DOC_TYPE, loDocTypeListCity);
		// End
	}

	/**
	 * This method is extracted from default render in release5, this will be
	 * executed when sorting and paging on the document listing screen will be
	 * clicked.
	 * 
	 * @param aoRequest
	 *            RenderRequest object
	 * @param lsParentNodeReqParam
	 *            Parent node parameter
	 * @param lsNextPageReqParam
	 *            next page parameter
	 * @param lsSortByReqParam
	 *            sort by required parameter
	 * @param lsSortTypeReqParam
	 *            sort type parameter string
	 * @param loUserSession
	 *            P8usersession bean
	 * @param lsUserOrg
	 *            user organization string
	 * @param lsUserOrgType
	 *            user organization type string
	 * @param loDocument
	 *            document bean
	 * @param loDocumentList
	 *            document list
	 * @param aoFilterMap
	 *            filtermap
	 * @return list for paging and sorting
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	private String getDocumentListForPagingAndSorting(RenderRequest aoRequest,
			String lsParentNodeReqParam, String lsNextPageReqParam,
			String lsSortByReqParam, String lsSortTypeReqParam,
			P8UserSession loUserSession, String lsUserOrg,
			String lsUserOrgType, Document loDocument,
			List<Document> loDocumentList, HashMap<String, Object> aoFilterMap)
			throws ApplicationException {
		String lsSearchClicked = (String) aoRequest.getPortletSession()
				.getAttribute(HHSR5Constants.CLICKED_SEARCH);
		String lsFormPath;
		FileNetOperationsUtils.setReqRequestParameter(
				aoRequest.getPortletSession(), aoRequest, loUserSession,
				lsNextPageReqParam, lsSortByReqParam, lsSortTypeReqParam,
				lsParentNodeReqParam);
		// Adding extra check of pagination in below if for Defect # 8150
		if (null != lsSearchClicked
				&& lsSearchClicked.equalsIgnoreCase(HHSConstants.TRUE)
				&& (null == lsNextPageReqParam || lsNextPageReqParam.isEmpty()))
			aoFilterMap.put(HHSR5Constants.IS_FILTER, HHSConstants.TRUE);
		lsFormPath = FileNetOperationsUtils.getDocumentList(aoRequest,
				loUserSession, loDocumentList, loDocument, lsUserOrgType,
				lsUserOrg, aoFilterMap, lsSortByReqParam, lsSortTypeReqParam);
		if ((null != loDocument.getFilterDocCategory() && !loDocument
				.getFilterDocCategory().equals(HHSR5Constants.EMPTY_STRING))
				|| (null != loDocument.getFilterDocType() && !loDocument
						.getFilterDocType().equals(HHSR5Constants.EMPTY_STRING))
				|| (null != loDocument.getFilterModifiedFrom() && !loDocument
						.getFilterModifiedFrom().equals(
								HHSR5Constants.EMPTY_STRING))
				|| (null != loDocument.getFilterModifiedTo() && !loDocument
						.getFilterModifiedTo().equals(
								HHSR5Constants.EMPTY_STRING))
				|| (null != loDocument.getFilterNYCAgency() && !loDocument
						.getFilterNYCAgency().equals(
								HHSR5Constants.EMPTY_STRING))
				|| (null != loDocument.getFilterProviderId()
						&& !loDocument.getFilterProviderId().equals(
								HHSR5Constants.EMPTY_STRING) || (null != loDocument
						.getDocSharedStatus()
						&& !loDocument.getDocSharedStatus().equals(
								HHSR5Constants.EMPTY_STRING) && !loDocument
						.getDocSharedStatus().equalsIgnoreCase(
								ApplicationConstants.BOTH_AGENCY_PROVIDER)))) {
			aoRequest.setAttribute(HHSR5Constants.FILTER_STATUS,
					HHSConstants.FILTERED);
			if (aoRequest.getParameter(HHSR5Constants.FILTER_TRUE) != null
					&& aoRequest.getParameter(HHSR5Constants.FILTER_TRUE)
							.equalsIgnoreCase(ApplicationConstants.FALSE)) {
				aoRequest.setAttribute(HHSR5Constants.FILTER_STATUS,
						HHSR5Constants.NOT_FILTER);
			}
		}
		aoRequest.setAttribute(ApplicationConstants.CATEGORY_NODE,
				loDocument.getFilterDocCategory());
		if (ApplicationConstants.DOC_SAMPLE.equalsIgnoreCase(loDocument
				.getFilterDocCategory())) {
			ArrayList<String> loSampleCategoryList = null;
			loSampleCategoryList = FileNetOperationsUtils
					.getSampleCategoryList();
			loDocument.setSampleCategoryList(loSampleCategoryList);
		}
		return lsFormPath;
	}

	/**
	 * The method is used to make list of either DocumentsSelFromDocVault bean
	 * or ExtendedDocument bean from list of Document bean
	 * 
	 * @param aoRequest
	 *            RenderRequest object
	 * @return the name of view as String.
	 * @throws ApplicationException
	 */
	private String addDocumentBeanPropsForSelectDoc(RenderRequest aoRequest)
			throws ApplicationException {
		String lsFormPath;
		lsFormPath = HHSConstants.ADD_DOC_FROM_VAULT;
		boolean lbDocPresent;
		String lsSelectDocforRelease = aoRequest
				.getParameter(HHSR5Constants.SELECT_DOC_RELEASE);
		List<Document> loDocumentList = (List<Document>) ApplicationSession
				.getAttribute(aoRequest, true,
						ApplicationConstants.SESSION_DOCUMENT_LIST);
		if (loDocumentList.size() == 0) {
			PortletSession loSession = aoRequest.getPortletSession();
			P8UserSession loUserSession = (P8UserSession) loSession
					.getAttribute(ApplicationConstants.FILENET_SESSION_OBJECT,
							PortletSession.APPLICATION_SCOPE);
			Channel loChannel = new Channel();
			loChannel.setData(HHSR5Constants.AO_FILENET_SESSION, loUserSession);
			String lsUserOrg = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
							PortletSession.APPLICATION_SCOPE);
			String lsUserOrgType = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
							PortletSession.APPLICATION_SCOPE);
			if (null != lsUserOrgType
					&& lsUserOrgType.equalsIgnoreCase(HHSR5Constants.USER_CITY)) {
				lsUserOrg = lsUserOrgType;
			}
			String lsFolderPath = FileNetOperationsUtils.setFolderPath(
					lsUserOrgType, lsUserOrg, HHSR5Constants.DOCUMENT_VAULT);
			loChannel.setData(HHSR5Constants.FOLDER_PATH, lsFolderPath);
			HHSTransactionManager.executeTransaction(loChannel,
					"checkDocPresent", HHSR5Constants.TRANSACTION_ELEMENT_R5);
			lbDocPresent = (Boolean) loChannel
					.getData(HHSR5Constants.DOC_PRESENT);
			aoRequest.setAttribute(HHSR5Constants.DOC_PRESENT, lbDocPresent);
		}

		if (null != lsSelectDocforRelease
				&& lsSelectDocforRelease
						.equalsIgnoreCase(HHSR5Constants.BUSINESS_APP)) {
			List<DocumentsSelFromDocVault> loDocumentListBaap = new ArrayList<DocumentsSelFromDocVault>();
			for (Iterator iterator = loDocumentList.iterator(); iterator
					.hasNext();) {
				Document loDocExisting = (Document) iterator.next();
				DocumentsSelFromDocVault loExtDoc = new DocumentsSelFromDocVault();
				loExtDoc.setMsDocumentName(loDocExisting.getDocName());
				loExtDoc.setMsLastModifiedDate(loDocExisting.getDate());
				loExtDoc.setMsDocumnetType(loDocExisting.getDocType());
				loExtDoc.setMsDocumentId(new Id(loDocExisting.getDocumentId()));
				loDocumentListBaap.add(loExtDoc);
			}
			aoRequest.setAttribute(ApplicationConstants.SESSION_DOCUMENT_LIST,
					loDocumentListBaap);
			lsFormPath = HHSR5Constants.SELECT_DOC_BUSINESS_APP;
		} else {
			List<ExtendedDocument> loExtendedDocList = new ArrayList<ExtendedDocument>();
			for (Iterator iterator = loDocumentList.iterator(); iterator
					.hasNext();) {
				Document loDocExisting = (Document) iterator.next();
				ExtendedDocument loExtDoc = new ExtendedDocument();
				loExtDoc.setDocumentTitle(loDocExisting.getDocName());
				loExtDoc.setModifiedDate(loDocExisting.getDate());
				loExtDoc.setDocumentType(loDocExisting.getDocType());
				loExtDoc.setDocumentId(loDocExisting.getDocumentId());
				loExtDoc.setDocumentCategory(loDocExisting.getDocCategory());
				loExtDoc.setLastModifiedById(loDocExisting.getLastModifiedBy());
				loExtDoc.setCreatedBy(loDocExisting.getCreatedBy());
				loExtDoc.setCreatedDate(loDocExisting.getDate());
				loExtendedDocList.add(loExtDoc);
			}
			aoRequest.setAttribute(ApplicationConstants.SESSION_DOCUMENT_LIST,
					loExtendedDocList);
		}
		return lsFormPath;
	}

	// End of Release 5

	/**
	 * This method is added in release 5, which will set the agency list in the
	 * session. It will get the agency name from the cache and set them in
	 * session.
	 * 
	 * @param aoRequest
	 *            render Request Object
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@SuppressWarnings("unchecked")
	private void setAgencyListInSession(RenderRequest aoRequest)
			throws ApplicationException {
		TreeSet<String> loAgencyList = null;
		List<ProviderBean> loAgencyDataList = new ArrayList<ProviderBean>();

		loAgencyList = (TreeSet<String>) BaseCacheManagerWeb.getInstance()
				.getCacheObject(ApplicationConstants.AGENCY_LIST);
		Iterator loItr = loAgencyList.iterator();
		while (loItr.hasNext()) {
			String lsTemp = (String) loItr.next();
			String[] loTemp = lsTemp.split(ApplicationConstants.TILD);
			ProviderBean loBean = new ProviderBean();
			loBean.setHiddenValue(loTemp[0]);
			loBean.setDisplayValue(StringEscapeUtils
					.unescapeJavaScript(loTemp[1]));
			loAgencyDataList.add(loBean);
		}
		aoRequest.getPortletSession().setAttribute(
				ApplicationConstants.AGENCY_LIST, loAgencyDataList);
	}

	/**
	 * This method handle the action of a portlet page. when a action is
	 * initiated from a jsp, it process the action by calling multiple
	 * transactions and forward it to render method for further processing this
	 * is an out of box method from portlet frame work which we override to
	 * achieve further functionalities
	 * <ul>
	 * <li>Adding annotations for Release 5</li>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * 
	 * @param aoRequest
	 *            Action request object
	 * @param aoResponse
	 *            Action response object
	 * @return loModelAndView model view object of the required JSP
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@ActionMapping
	protected void handleActionRequestInternal(ActionRequest aoRequest,
			ActionResponse aoResponse) throws ApplicationException {
		long loStartTime = System.currentTimeMillis();
		PortletSession loPortletSessionThread = aoRequest.getPortletSession();
		String lsUserIdThreadLocal = (String) loPortletSessionThread
				.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
						PortletSession.APPLICATION_SCOPE);
		UserThreadLocal.setUser(lsUserIdThreadLocal);
		PortletSession loSession = aoRequest.getPortletSession();
		String lsAjaxCall = aoRequest
				.getParameter(ApplicationConstants.IS_AJAX_CALL);
		String lsNextAction = aoRequest
				.getParameter(ApplicationConstants.DOCUMENT_VAULT_NEXT_ACTION_PARAMETER);
		try {
			String lsUserOrgType = null;

			lsUserOrgType = (String) loSession.getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE,
					PortletSession.APPLICATION_SCOPE);
			String lsUserOrg = null;
			if (lsUserOrgType.equals(ApplicationConstants.CITY_ORG)) {
				lsUserOrg = lsUserOrgType;

			} else {
				lsUserOrg = (String) loSession.getAttribute(
						ApplicationConstants.KEY_SESSION_USER_ORG,
						PortletSession.APPLICATION_SCOPE);
			}
			if (null != lsNextAction && !lsNextAction.isEmpty()
					&& lsNextAction.equalsIgnoreCase("returnToHome")) {
				FileNetOperationsUtils.redirectToHomePage(aoRequest,
						aoResponse, new StringBuilder(lsUserOrg),
						new StringBuilder(lsUserOrgType));
			} else {
				aoResponse.setRenderParameter(HHSConstants.ACTION,
						HHSR5Constants.VALUT_KEY);
				String lsNextPageReqParam = aoRequest
						.getParameter(ApplicationConstants.DOCUMENT_VAULT_NEXT_PAGE_VALUE_PARAMETER);
				String lsSortByReqParam = aoRequest
						.getParameter(ApplicationConstants.DOCUMENT_VAULT_SORT_BY_PARAMETER);
				String lsSortTypeReqParam = aoRequest
						.getParameter(ApplicationConstants.DOCUMENT_VAULT_SORT_TYPE_PARAMETER);
				String lsParentNodeReqParam = aoRequest
						.getParameter(ApplicationConstants.DOCUMENT_VAULT_PARENT_NODE_PARAMETER);
				P8UserSession loUserSession = (P8UserSession) loSession
						.getAttribute(
								ApplicationConstants.FILENET_SESSION_OBJECT,
								PortletSession.APPLICATION_SCOPE);
				// Fixed Defect 1809 , 1810. If clicked on pagination make
				// lsNextAction as blank.
				if (lsNextPageReqParam != null
						&& Integer.parseInt(lsNextPageReqParam) >= 1) {
					lsNextAction = HHSR5Constants.EMPTY_STRING;
				}
				// This executes when next page parameter is not present in
				// request
				if (StringUtils.isNotBlank(lsNextPageReqParam)) {
					Document loDocument = new Document();
					FileNetOperationsUtils.setFilterHiddenParams(aoRequest,
							loDocument, null);
					FileNetOperationsUtils.setReqSessionParameter(loSession,
							lsNextPageReqParam, lsSortByReqParam,
							lsSortTypeReqParam, lsParentNodeReqParam);
					aoResponse.setRenderParameter(HHSR5Constants.NEXT_PAGE,
							lsNextPageReqParam);
					if (null != aoRequest
							.getParameter(HHSR5Constants.FOLDER_ID)
							&& !aoRequest
									.getParameter(HHSR5Constants.FOLDER_ID)
									.isEmpty()
							&& aoRequest.getParameter(HHSR5Constants.FOLDER_ID)
									.contains(HHSR5Constants.RECYCLE_BIN_ID)) {
						aoResponse.setRenderParameter(HHSConstants.JSP_NAME,
								HHSR5Constants.RECYCLE_BIN_JSP_NAME);
					}
					ApplicationSession
							.setAttribute(
									loDocument,
									aoRequest,
									ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_PARAMETER);
				} else if (StringUtils.isNotBlank(lsSortByReqParam)
						&& !lsSortByReqParam
								.equalsIgnoreCase(P8Constants.STRING_SINGLE_SLASH)) {
					FileNetOperationsUtils.setReqSessionParameter(loSession,
							lsNextPageReqParam, lsSortByReqParam,
							lsSortTypeReqParam, lsParentNodeReqParam);
					FileNetOperationsUtils.reInitializePageIterator(loSession,
							loUserSession);
				}
				// Added for selectVault
				String lsRenderJspName = (String) aoRequest
						.getParameter(HHSR5Constants.RENDER_JSP_NAME);
				if (StringUtils.isNotBlank(lsRenderJspName)) {
					aoResponse.setRenderParameter(
							HHSR5Constants.CONTROLLER_PARAM, lsRenderJspName);
				}
				String lsIsFilter = (String) aoRequest
						.getParameter(HHSR5Constants.IS_FILTER);
				if (StringUtils.isNotBlank(lsIsFilter)) {
					aoResponse.setRenderParameter(
							HHSR5Constants.SELECT_VAULT_FLAG, lsIsFilter);
				}
				String lsUploadDocType = (String) aoRequest
						.getParameter(ApplicationConstants.UPLOAD_DOC_TYPE);
				if (StringUtils.isNotBlank(lsUploadDocType)) {
					aoResponse.setRenderParameter(
							ApplicationConstants.UPLOAD_DOC_TYPE,
							lsUploadDocType);
				}
				String lsDocType = (String) aoRequest
						.getParameter(ApplicationConstants.DOC_TYPE);
				if (StringUtils.isNotBlank(lsDocType)) {
					aoResponse.setRenderParameter(
							P8Constants.PROPERTY_PE_UPLOADED_DOC_TYPE,
							lsDocType);
				}
				String lsDocRelease = (String) aoRequest
						.getParameter(HHSR5Constants.SELECT_DOC_RELEASE);
				if (StringUtils.isNotBlank(lsDocRelease)) {
					aoResponse.setRenderParameter(HHSConstants.ACTION,
							HHSR5Constants.DOCUMENT_VALUT_KEY);
					aoResponse.setRenderParameter(
							HHSR5Constants.SELECT_DOC_RELEASE, lsDocRelease);
				}
			}

		} catch (ApplicationException aoExp) {
			try {
				setErrorMessageInResponse(aoRequest, aoResponse, loSession,
						lsAjaxCall, lsNextAction, aoExp);
			} catch (IOException aoIoExp) {
				LOG_OBJECT.Error("IOException during file upload", aoIoExp);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION,
						ApplicationConstants.ERROR);
			}
		}
		long loEndTimeTime = System.currentTimeMillis();
		try {
			LOG_OBJECT
					.Debug("TIME TAKEN for execution of action Method in DocumentVaultController = "
							+ (loEndTimeTime - loStartTime));
		} catch (ApplicationException aoEx) {
			LOG_OBJECT
					.Error("Error while execution of action Method in DocumentVaultController ",
							aoEx);
		}
		UserThreadLocal.unSet();
	}

	/**
	 * This action is performed when a user cancel from the edit properties page
	 * 
	 * @param aoRequest
	 *            ActionRequest object
	 * @param aoResponse
	 *            ActionResponse object
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@SuppressWarnings("unchecked")
	public void cancelEditDocumentProperties(ActionRequest aoRequest,
			ActionResponse aoResponse) throws ApplicationException {
		PortletSession loSession = aoRequest.getPortletSession();
		String lsSessionId = loSession.getId();
		Map<String, String> loEditDocumentMap = (Map<String, String>) BaseCacheManagerWeb
				.getInstance().getCacheObject(
						ApplicationConstants.EDIT_DOC_LIST_MAP);
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsLockedDocumentsKey = lsSessionId
				+ ApplicationConstants.UNDERSCORE + lsUserId;
		if (null != loEditDocumentMap
				&& loEditDocumentMap.containsKey(lsLockedDocumentsKey)) {
			loEditDocumentMap.remove(lsLockedDocumentsKey);
		}
		synchronized (this) {
			BaseCacheManagerWeb.getInstance().putCacheObject(
					ApplicationConstants.EDIT_DOC_LIST_MAP, loEditDocumentMap);
		}
		FileNetOperationsUtils.actionViewDocumentInfo(aoRequest, aoResponse);
		
		//[Start]R7.12.0 QC9311 Minimize Debug
		//LOG_OBJECT.Debug("====line 1206===cancelEditDocumentProperties ::   VIEW_DOCUMENT_INFO :: viewDocumentInfo ");
		//[End]R7.12.0 QC9311 Minimize Debug
		aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION,
				ApplicationConstants.VIEW_DOCUMENT_INFO);
		
	}

	/**
	 * This method executes when a user click on the edit document properties
	 * action
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action response object
	 * @throws ApplicationException
	 *             when any exception oocurred we wrap it into this custom
	 *             exception
	 */
	public void editDocumentPropertiesAction(ActionRequest aoRequest,
			ActionResponse aoResponse) throws ApplicationException {
		Map<String, String> loEditDocsMap = new HashMap<String, String>();
		PortletSession loSession = aoRequest.getPortletSession();
		String lsSessionId = loSession.getId();
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsLockedDocumentsKey = lsSessionId
				+ ApplicationConstants.UNDERSCORE + lsUserId;
		loEditDocsMap.put(lsLockedDocumentsKey,
				aoRequest.getParameter(ApplicationConstants.DOCUMENT_ID)
						.toString());
		synchronized (this) {
			BaseCacheManagerWeb.getInstance().putCacheObject(
					ApplicationConstants.EDIT_DOC_LIST_MAP, loEditDocsMap);
		}
		FileNetOperationsUtils.actionViewDocumentInfo(aoRequest, aoResponse);
		aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION,
				ApplicationConstants.EDIT_DOCUMENT_PROPS);
	}

	/**
	 * 
	 * @param aoRequest
	 *            RenderRequest object
	 * @param aoResponse
	 *            RenderRequest object
	 * @return ModelAndView object
	 */
	@RenderMapping(params = "render_action=editDocumentProps")
	protected ModelAndView handleRenderForEditDocumentProps(
			RenderRequest aoRequest, RenderResponse aoResponse) {
		String lsFormPath = null;
		try {
			lsFormPath = getRenderEditDocument(aoRequest);
		} catch (Exception loAppEx) {
			LOG_OBJECT.Error("Exception occured in documentUploadAction",
					loAppEx);
		}
		return new ModelAndView(lsFormPath);
	}

	/**
	 * Updated in 3.1.0. Added check for Error message in case of invalid start
	 * year and month while editing document propeties.
	 * 
	 * This method executes when a user click on the save button while editing
	 * the properties of the document
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action response object
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	public void saveDocPropertiesAction(ActionRequest aoRequest,
			ActionResponse aoResponse) throws ApplicationException {
		try {
			actionSaveProperty(aoRequest, aoResponse);
		} catch (ApplicationException loAppEx) {
			LOG_OBJECT.Error("Exception occured   in handleResourceForTree",
					loAppEx);
			throw loAppEx;

		} catch (Exception loAppEx) {
			LOG_OBJECT.Error("Exception occured   in handleResourceForTree",
					loAppEx);

		}
	}

	/**
	 * This method executes when a user click on next button from the share
	 * document 2nd screen
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action response object
	 */
	public void shareDocumentStepThreeAction(ActionRequest aoRequest,
			ActionResponse aoResponse) {
		String lsProvName = aoRequest
				.getParameter(ApplicationConstants.PROVIDER_NAME);
		aoResponse.setRenderParameter(
				ApplicationConstants.PROVIDER_NAMES_RETURNED,
				StringEscapeUtils.escapeHtml(StringEscapeUtils.escapeJavaScript(lsProvName)));
		aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION,
				ApplicationConstants.SHARE_DOCUMENT_STEP3);
	}

	/**
	 * This method executed when user click on back button from 4th screen of
	 * document sharing
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action response object
	 */
	public void shareDocumentBackToStepThreeAction(ActionRequest aoRequest,
			ActionResponse aoResponse) {
		String lsProvName = aoRequest
				.getParameter(ApplicationConstants.PROV_AGENCY_LIST);
		if (null != lsProvName) {
			aoResponse.setRenderParameter(
					ApplicationConstants.PROVIDER_NAMES_RETURNED, lsProvName);
		}
		aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION,
				ApplicationConstants.SHARE_DOCUMENT_STEP3);
	}

	/**
	 * This method executed when user click on the the next button from the
	 * third screen of document sharing
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action response object
	 */
	public void shareDocumentStepFourAction(ActionRequest aoRequest,
			ActionResponse aoResponse) {
		String lsProvName = aoRequest
				.getParameter(ApplicationConstants.PROVIDER_NAME);
		if (null != lsProvName) {
			aoResponse.setRenderParameter(
					ApplicationConstants.PROVIDER_NAMES_RETURNED,
					StringEscapeUtils.escapeJavaScript(lsProvName));
		}
		aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION,
				ApplicationConstants.SHARE_DOCUMENT_STEP4);
	}

	/**
	 * This method executed when a user click on the back button from the third
	 * screen of document sharing
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action response object
	 */
	public void shareDocumentBackToStepTwoAction(ActionRequest aoRequest,
			ActionResponse aoResponse) {
		String lsProvName = aoRequest
				.getParameter(ApplicationConstants.PROVIDER_NAME);
		if (null != lsProvName) {
			aoResponse.setRenderParameter(
					ApplicationConstants.PROVIDER_NAMES_RETURNED,
					StringEscapeUtils.escapeJavaScript(lsProvName));
		}
		aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION,
				ApplicationConstants.SHARE_DOCUMENT_STEP2);
	}

	/**
	 * This method executed when a user click on the back button from the second
	 * screen of document sharing
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action response object
	 */
	public void shareDocumentBackToStepOneAction(ResourceRequest aoRequest,
			ResourceRequest aoResponse) {
		String lsProvName = aoRequest
				.getParameter(ApplicationConstants.PROVIDER_NAME);
		if (null != lsProvName) {
			aoRequest.setAttribute(ApplicationConstants.PROVIDER_NAME,
					StringEscapeUtils.escapeJavaScript(lsProvName));
		}
	}

	/**
	 * This method executed when user click on the next button from the first
	 * screen of document sharing
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action response object
	 */
	public void shareDocumentStepTwoAction(ResourceRequest aoRequest,
			ResourceResponse aoResponse) {
		String lsProvName = aoRequest
				.getParameter(ApplicationConstants.PROVIDER_NAME);
		if (null != lsProvName) {
			aoRequest.setAttribute(ApplicationConstants.PROVIDER_NAME,
					StringEscapeUtils.escapeJavaScript(lsProvName));
		}
	}

	/**
	 * This method executed when user click on upload document link from the
	 * document vault home screen
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action response object
	 * @param asUserOrgType
	 *            user organization type
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	private void documentUploadAction(ActionRequest aoRequest, ActionResponse aoResponse, String asUserOrgType)
			throws ApplicationException 
	{
		LOG_OBJECT.Info("====documentUploadAction===");
		Document loDocument = new Document();
		try {
			FileNetOperationsUtils.setFilterHiddenParams(aoRequest, loDocument,
					null);
			// Added for combo box of doctype
			List<String> loDocTypeList = FileNetOperationsUtils.getDocType(
					asUserOrgType, null, HHSR5Constants.UPLOAD_DOC_TYPE);
			ApplicationSession.setAttribute(loDocTypeList, aoRequest,
					HHSR5Constants.DROPDOWN_DOC_TYPE);

			// end
		} catch (ApplicationException loAppEx) {
			LOG_OBJECT.Error("Exception occured   in documentUploadAction",
					loAppEx);
			throw loAppEx;
		} catch (Exception loAppEx) {
			LOG_OBJECT.Error("Exception occured in documentUploadAction",
					loAppEx);
			throw new ApplicationException(
					"Internal Error Occured While Processing Your Request",
					loAppEx);
		}
		ApplicationSession.setAttribute(loDocument, aoRequest,
				ApplicationConstants.SESSION_DOCUMENT_OBJ);
	}

	/**
	 * This method executed when a user tried to share or unshare a document
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action response object
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 * @throws IOException
	 *             when any IOException occured
	 */
	@ActionMapping(params = "submit_action=finalUnShareDocument")
	public void removeAccessWithDocumentsAction(ActionRequest aoRequest,
			ActionResponse aoResponse) throws ApplicationException, IOException {
		HashMap<String, String> loLockingMapId = new HashMap<String, String>();
		List<String> loLockIdList = new ArrayList<String>();
		Boolean lbLockFlag = true;
		try {
			FileNetOperationsUtils
					.removeMessageFromSessionAndRequest(aoRequest);
			String lsDocId = aoRequest
					.getParameter(ApplicationConstants.DOCUMENT_ID);

			// loLockingMapId.put(lsDocId,"");
			// Add parent Id of document
			String lsParentFolderId = aoRequest
					.getParameter("parentFolderIdUnshare");
			if (null != lsParentFolderId && !lsParentFolderId.isEmpty()
					&& !lsParentFolderId.equalsIgnoreCase(HHSR5Constants.NULL)) {
				aoResponse.setRenderParameter(HHSR5Constants.FOLDER_ID,
						lsParentFolderId);
			}
			StringBuffer lsBfMessage = new StringBuffer();
			StringBuffer lsBfProvidersName = new StringBuffer();
			StringBuffer lsBfDocumentName = new StringBuffer();
			String lsMessageFromProp = HHSR5Constants.EMPTY_STRING;
			HashMap<String, String> loReqMap = new HashMap<String, String>();
			PortletSession loSession = aoRequest.getPortletSession();
			P8UserSession loUserSession = (P8UserSession) loSession
					.getAttribute(ApplicationConstants.FILENET_SESSION_OBJECT,
							PortletSession.APPLICATION_SCOPE);
			String lsUserOrg = null;
			String lsOrgType = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
							PortletSession.APPLICATION_SCOPE);
			String lsUserId = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
							PortletSession.APPLICATION_SCOPE);
			HashMap<String, Object> loUserProps = new HashMap<String, Object>();
			loUserProps.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY,
					lsUserId);

			if (lsOrgType.equalsIgnoreCase(ApplicationConstants.CITY_ORG)) {
				lsUserOrg = lsOrgType;
			} else {
				lsUserOrg = (String) aoRequest.getPortletSession()
						.getAttribute(
								ApplicationConstants.KEY_SESSION_USER_ORG,
								PortletSession.APPLICATION_SCOPE);
			}
			String lsUnshareBy = aoRequest
					.getParameter(ApplicationConstants.UNSHARE_BY);
			HashMap loReqProps = new HashMap();
			/*
			 * Below section executes when user click on remove all button on
			 * shared details screen on success it will show one message on the
			 * home screen.
			 */
			if (null != lsUnshareBy
					&& lsUnshareBy.equals(ApplicationConstants.UNSHARE_BY_ALL)) {
				removeShareWithAllProviders(aoRequest, lsBfMessage,
						lsBfDocumentName, loReqProps, loLockingMapId);

			} else if (null != lsUnshareBy
					&& lsUnshareBy.equals(HHSR5Constants.REMOVE_ALL)) {
				String lsDocType = aoRequest
						.getParameter(ApplicationConstants.DOC_TYPE);
				if (null == lsDocType || lsDocType.isEmpty()
						|| lsDocType.equalsIgnoreCase(HHSConstants.NULL)) {
					lsDocId = lsDocId.concat(HHSR5Constants.COMMA_WITH_FOLDER);

				}
				loReqProps.put(lsDocId, lsDocType);
				lsBfDocumentName.append(aoRequest
						.getParameter(ApplicationConstants.DOCUMENT_NAME));
				lsMessageFromProp = PropertyLoader.getProperty(
						P8Constants.ERROR_PROPERTY_FILE,
						HHSR5Constants.MESSAGE_M07);
				lsBfMessage.append(HHSConstants.SPACE);
				lsBfMessage.append(lsMessageFromProp);
			}
			/*
			 * Below section executes when user select specific
			 * provider/organization and click on remove button on success it
			 * will show one message on the home screen.
			 */
			else if (null != aoRequest
					.getParameter(ApplicationConstants.REMOVE_SELECTED)) {
				removeShareWithSelectedProvider(aoRequest, lsBfMessage,
						lsBfProvidersName, lsBfDocumentName, loReqProps,
						loLockingMapId);
			} else {
				lsBfDocumentName.append(aoRequest
						.getParameter(ApplicationConstants.DOCUMENT_NAME));
				lsMessageFromProp = PropertyLoader.getProperty(
						P8Constants.ERROR_PROPERTY_FILE,
						HHSR5Constants.MESSAGE_M07);
				lsBfMessage.append(lsMessageFromProp);
				lsBfMessage.append(HHSConstants.SPACE);
				lsBfMessage.append(lsBfDocumentName);
				loReqProps.put(lsDocId, null);
			}
			Channel loChannelLock = new Channel();
			loChannelLock.setData("documentIdMap", loLockingMapId);
			loChannelLock.setData("aoFilenetSession", loUserSession);
			loChannelLock.setData("next_action", "unShare");
			HHSTransactionManager.executeTransaction(loChannelLock,
					"getFolderPathFromFilenet",
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			HashMap<String, List<String>> loLockingMap = (HashMap) loChannelLock
					.getData("loPathMap");
			for (Map.Entry<String, List<String>> entry : loLockingMap
					.entrySet()) {
				List<String> loList = entry.getValue();
				for (Iterator iterator = loList.iterator(); iterator.hasNext();) {
					String lsLockPath = (String) iterator.next();
					loLockIdList.add(lsLockPath);
				}
			}

			if (FileNetOperationsUtils.checkLock(loLockIdList, lsUserOrg)) {
				aoResponse.setRenderParameter(
						ApplicationConstants.ERROR_MESSAGE, PropertyLoader
								.getProperty(P8Constants.ERROR_PROPERTY_FILE,
										HHSR5Constants.LOCK_MESSAGE_UNSHARING));
				aoResponse.setRenderParameter(
						ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE);
				lbLockFlag = false;
			} else {
				FileNetOperationsUtils.addLock(loLockIdList, aoRequest);
				loReqMap.put(HHSR5Constants.ORG_ID, lsUserOrg);
				loReqMap.put(HHSConstants.LS_USER_ORG_TYPE, lsOrgType);
				List<String> lsUnShareOpsList = new ArrayList<String>();
				lsUnShareOpsList.add(HHSR5Constants.UN_SHARE_FOLDER);
				lsUnShareOpsList.add(HHSR5Constants.UN_SHARE_FILE);
				Channel loChannel = new Channel();
				loChannel.setData(HHSR5Constants.REQUIRED_MAP, loUserProps);
				loChannel.setData(HHSR5Constants.AUDIT_ENTITY_NAME,
						lsUnShareOpsList);
				loChannel.setData(HHSR5Constants.AO_FILENET_SESSION,
						loUserSession);
				loChannel.setData(HHSR5Constants.FILTER_PROPS, loReqProps);
				loChannel.setData(HHSR5Constants.REQ_MAP, loReqMap);
				TransactionManager.executeTransaction(loChannel,
						HHSR5Constants.UNSHARE_DOCUMENT_FILENET,
						HHSR5Constants.TRANSACTION_ELEMENT_R5);
				boolean lbShareStatus = (Boolean) loChannel
						.getData(HHSR5Constants.SHARED_STATUS);
				if (lbShareStatus) {
					loSession
							.setAttribute(
									ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
									ApplicationConstants.DOCUMET_VAULT_CONTROLLER_SUCCESS,
									PortletSession.APPLICATION_SCOPE);
					aoResponse.setRenderParameter(ApplicationConstants.MESSAGE,
							lsBfMessage.toString());
					aoResponse.setRenderParameter(
							ApplicationConstants.ERROR_MESSAGE_TYPE,
							ApplicationConstants.MESSAGE_PASS_TYPE);
				} else {
					aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION,
							ApplicationConstants.ERROR);
				}
				FileNetOperationsUtils.reInitializePageIterator(loSession,
						loUserSession);
				aoResponse.setRenderParameter(HHSR5Constants.HEADER_CLICK,
						"true");
				// Emergency Build- 4.0.1- changes for share with list
				HashMap<String, String> loSharedList = ProviderAgencyHomeController
						.getShareWithOrgDetails(loUserSession, lsUserOrg);
				aoRequest.getPortletSession().setAttribute(
						HHSR5Constants.SHARED_WITH_DETAILS, loSharedList,
						PortletSession.APPLICATION_SCOPE);
				// Emergency Build- 4.0.1- changes for share with list end

			}

		} catch (ApplicationException loAppEx) {
			LOG_OBJECT.Error("Exception occured in documentShareAction",
					loAppEx);
			throw loAppEx;
		} catch (Exception loAppEx) {
			LOG_OBJECT.Error("Exception occured in documentShareAction",
					loAppEx);
			throw new ApplicationException(
					"Internal Error Occured While Processing Your Request",
					loAppEx);
		} finally {
			if (lbLockFlag) {
				FileNetOperationsUtils.removeLock(loLockIdList, aoRequest);
			}

		}

	}

	/**
	 * This method executed when a user click on shared link corresponding to
	 * each document it executes displaySharedDocuments_filenet transaction and
	 * display all the providers and agencies with whom document is shared
	 * 
	 * @param aoRequest
	 *            ResourceRequest object
	 * @param aoResponse
	 *            ResourceResponse object
	 * @param aoChannel
	 *            channel object to execute the transaction
	 * @param aoUserSession
	 *            P8 session object
	 * @return all shared document list
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<String> showAllSharedWithDocumentAction(
			ResourceRequest aoRequest, ResourceResponse aoResponse,
			Channel aoChannel, P8UserSession aoUserSession)
			throws ApplicationException {
		TreeSet<String> loAgencyList = null;
		String lsDocumentId = aoRequest
				.getParameter(ApplicationConstants.DOCUMENT_ID);
		String lsDocType = aoRequest
				.getParameter(ApplicationConstants.DOC_TYPE);
		lsDocumentId = lsDocumentId + HHSR5Constants.COMMA + lsDocType;

		String lsProvAgencyName = null;
		aoChannel.setData(HHSConstants.AO_FILENET_SESSION, aoUserSession);
		aoChannel.setData(ApplicationConstants.DOCUMENT_ID, lsDocumentId);
		
		
		TransactionManager.executeTransaction(aoChannel,
				HHSR5Constants.DISPLAY_SHARED_DOCUMENTS_FILENET,
				HHSR5Constants.TRANSACTION_ELEMENT_R5);
		List loProviderList = (List) aoChannel
				.getData(HHSConstants.PROVIDER_ID_LIST);
		List loPList = new ArrayList();

		List<ProviderBean> loProviderBeanList = (List<ProviderBean>) BaseCacheManagerWeb
				.getInstance().getCacheObject(ApplicationConstants.PROV_LIST);
		loAgencyList = (TreeSet<String>) BaseCacheManagerWeb.getInstance()
				.getCacheObject(ApplicationConstants.AGENCY_LIST);
		for (Iterator liIt = loProviderList.iterator(); liIt.hasNext();) {
			String lsName = (String) liIt.next();
			if (lsName.startsWith(ApplicationConstants.PROVIDER.toUpperCase())) {
				lsProvAgencyName = FileNetOperationsUtils.getProviderName(
						loProviderBeanList, lsName.substring(
								lsName.indexOf(HHSConstants.COLON) + 1,
								lsName.length()));
				lsProvAgencyName = StringEscapeUtils
						.unescapeJavaScript(lsProvAgencyName);
				loPList.add(lsProvAgencyName);
			} else if (lsName.startsWith(ApplicationConstants.AGENCY
					.toUpperCase())) {
				lsProvAgencyName = FileNetOperationsUtils.getAgencyName(
						loAgencyList, lsName.substring(
								lsName.indexOf(HHSConstants.COLON) + 1,
								lsName.length()));
				lsProvAgencyName = StringEscapeUtils
						.unescapeJavaScript(lsProvAgencyName);
				loPList.add(lsProvAgencyName);
			}

		}
		Collections.sort(loPList);
		return loPList;

	}

	/**
	 * This method executed when user browse to the document vault home screen
	 * this will fetch all the documents from the filenet
	 * 
	 * @param aoRequest
	 *            ActionRequest object
	 * @param aoResponse
	 *            ActionResponse object
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 * @throws PortletException
	 *             when any portlet exception occurred
	 */
	public void showDocumentListAction(ActionRequest aoRequest,
			ActionResponse aoResponse) throws ApplicationException,
			PortletException {
		Document loDocument = new Document();
		MultipartActionRequestParser loFormdata = new MultipartActionRequestParser(
				aoRequest);
		FileNetOperationsUtils.setFilterHiddenParams(aoRequest, loDocument,
				FileNetOperationsUtils.getHiddenFilterMap(loFormdata));
		aoResponse.setRenderParameter(ApplicationConstants.MESSAGE,
				StringEscapeUtils.escapeJavaScript((String) aoRequest
						.getParameter(ApplicationConstants.MESSAGE)));
		aoResponse.setRenderParameter(ApplicationConstants.MESSAGE_TYPE,
				(String) aoRequest
						.getParameter(ApplicationConstants.MESSAGE_TYPE));
		aoResponse.setRenderParameter(HHSConstants.SUBMIT_ACTION,
				ApplicationConstants.SHOW_DOCUMENT_LIST);
		ApplicationSession.setAttribute(loDocument, aoRequest,
				ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_PARAMETER);
		String lsFilterTrue = (String) aoRequest
				.getParameter(HHSR5Constants.STRING_FILTER_TRUE);
		if (lsFilterTrue != null
				&& lsFilterTrue.equalsIgnoreCase(ApplicationConstants.FALSE)) {
			aoResponse.setRenderParameter(HHSR5Constants.FILTER_TRUE,
					ApplicationConstants.FALSE);
		}
	}

	/**
	 * This method executed when user unshare the document with all the
	 * providers and agencies
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action response object
	 */
	@ActionMapping(params = "submit_action=unsharedocumentall")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void unshareAllAction(ActionRequest aoRequest,
			ActionResponse aoResponse) {
		try {
			FileNetOperationsUtils
					.removeMessageFromSessionAndRequest(aoRequest);
			String lsMenuOptionsFlag = aoRequest.getParameter("menuFlag");
			Map<String, String[]> loMap = aoRequest.getParameterMap();
			List<String> lsTempListForId = new ArrayList<String>();
			List<String> lsTempListForStatus = new ArrayList<String>();
			String[] loCheckedObject = loMap
					.get(ApplicationConstants.CHECKED_OBJECT);
			List<Document> loShareDocumentList = null;
			if (null != lsMenuOptionsFlag
					&& lsMenuOptionsFlag.equalsIgnoreCase("true")) {
				for (int i = 0; i < loCheckedObject.length; i++) {
					String[] loTemp = loCheckedObject[i]
							.split(HHSConstants.COMMA);
					lsTempListForId.add(loTemp[0]);
					lsTempListForStatus.add(loTemp[3]);
				}
				loShareDocumentList = new ArrayList();
				Iterator<Document> loIter = ((List<Document>) ApplicationSession
						.getAttribute(aoRequest, true,
								ApplicationConstants.SESSION_DOCUMENT_LIST))
						.iterator();
				while (loIter.hasNext()) {
					Document loDocObject = loIter.next();
					if (lsTempListForId.contains(loDocObject.getDocumentId()
							.toString())
							&& lsTempListForStatus.contains(loDocObject
									.getShareStatus())) {
						loShareDocumentList.add(loDocObject);
					}
				}

			} else {

				lsTempListForId.add(HHSPortalUtil.parseQueryString(aoRequest,
						HHSR5Constants.CHECKED_DOC_ID));
				loShareDocumentList = new ArrayList();
				Iterator<Document> loIter = ((List<Document>) ApplicationSession
						.getAttribute(aoRequest, true,
								ApplicationConstants.SESSION_DOCUMENT_LIST))
						.iterator();
				while (loIter.hasNext()) {
					Document loDocObject = loIter.next();
					if (lsTempListForId.contains(loDocObject.getDocumentId()
							.toString())) {
						loShareDocumentList.add(loDocObject);
					}
				}

			}
			if (null != loShareDocumentList) {
				FileNetOperationsUtils.sortDocTypeList(loShareDocumentList,
						ApplicationConstants.DOC_TYPE);
				ApplicationSession.setAttribute(loShareDocumentList, aoRequest,
						ApplicationConstants.SHARED_DOCUMENTS);
			}
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION,
					ApplicationConstants.UNSHARE_DOCUMENT_ALL);
			aoResponse.setRenderParameter(HHSR5Constants.HEADER_CLICK, "true");
		} catch (Exception loAppEx) {
			LOG_OBJECT.Error("Exception occured in documentShareAction",
					loAppEx);
		}

	}

	/**
	 * 
	 * @param aoRequest
	 *            RenderRequest object
	 * @param aoResponse
	 *            RenderRequest object
	 * @return ModelAndView object
	 */
	@RenderMapping(params = "render_action=unsharedocumentall")
	protected ModelAndView handleRenderForUnshareDocumentAll(
			RenderRequest aoRequest, RenderResponse aoResponse) {
		String lsFormPath = null;
		try {
			aoRequest.setAttribute(ApplicationConstants.SHARE_DOCUMENT_LIST,
					ApplicationSession.getAttribute(aoRequest, true,
							ApplicationConstants.SHARED_DOCUMENTS));
			lsFormPath = ApplicationConstants.UNSHARE_DOCUMENT;
			String lsHeaderFlag = HHSPortalUtil.parseQueryString(aoRequest,
					HHSR5Constants.HEADER_CLICK);
			aoRequest.setAttribute(HHSR5Constants.HEADER_CLICK, lsHeaderFlag);
		} catch (Exception loAppEx) {
			LOG_OBJECT.Error("Exception occured in documentUploadAction",
					loAppEx);
		}
		return new ModelAndView(lsFormPath);
	}

	/**
	 * This method executed when user click on share link after selecting
	 * documents
	 * 
	 * @param aoRequest
	 *            ActionRequest object
	 * @param aoResponse
	 *            ActionResponse object
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void shareDocumentStepOneAction(ActionRequest aoRequest,
			ActionResponse aoResponse) throws ApplicationException {
		FileNetOperationsUtils.removeMessageFromSessionAndRequest(aoRequest);
		String lsOrgId = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ORG,
				PortletSession.APPLICATION_SCOPE);
		String lsMenuOptionsFlag = aoRequest.getParameter("menuFlag");
		String lsOrgType = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_TYPE,
				PortletSession.APPLICATION_SCOPE);
		if (null != lsOrgType
				&& lsOrgType.equalsIgnoreCase(HHSConstants.CITY_ORG)) {
			lsOrgId = lsOrgType;
		}
		Map<String, String[]> loMap = aoRequest.getParameterMap();
		List<String> lsTempList = new ArrayList<String>();
		String[] loCheckedObject = loMap
				.get(ApplicationConstants.CHECKED_OBJECT);
		List<Document> loShareDocumentList = null;
		List<Document> loShareUpdatedDocumentList = null;
		try {
			if (null != lsMenuOptionsFlag
					&& lsMenuOptionsFlag.equalsIgnoreCase("true")) {

				for (int i = 0; i < loCheckedObject.length; i++) {
					String[] loTemp = loCheckedObject[i]
							.split(HHSConstants.COMMA);
					lsTempList.add(loTemp[0]);

				}

				loShareDocumentList = new ArrayList();
				Iterator<Document> loIter = ((List<Document>) ApplicationSession
						.getAttribute(aoRequest, true,
								ApplicationConstants.SESSION_DOCUMENT_LIST))
						.iterator();
				while (loIter.hasNext()) {
					Document loDocObject = loIter.next();
					if (lsTempList.contains(loDocObject.getDocumentId()
							.toString())) {
						loShareDocumentList.add(loDocObject);
					}
				}
			} else {

				lsTempList.add(HHSPortalUtil.parseQueryString(aoRequest,
						HHSR5Constants.CHECKED_DOC_ID));
				loShareDocumentList = new ArrayList();
				Iterator<Document> loIter = ((List<Document>) ApplicationSession
						.getAttribute(aoRequest, true,
								ApplicationConstants.SESSION_DOCUMENT_LIST))
						.iterator();
				while (loIter.hasNext()) {
					Document loDocObject = loIter.next();
					if (lsTempList.contains(loDocObject.getDocumentId()
							.toString())) {
						loShareDocumentList.add(loDocObject);
					}
				}

			}
			// Added transaction for getting folder count
			Channel loChannel = new Channel();
			P8UserSession loUserSession = (P8UserSession) aoRequest
					.getPortletSession().getAttribute(
							ApplicationConstants.FILENET_SESSION_OBJECT,
							PortletSession.APPLICATION_SCOPE);
			loChannel.setData(HHSR5Constants.P8USER_SESSION, loUserSession);
			loChannel.setData(ApplicationConstants.SHARE_DOCUMENT_LIST,
					loShareDocumentList);
			loChannel.setData(HHSR5Constants.ORG_ID, lsOrgId);

			TransactionManager.executeTransaction(loChannel,
					HHSR5Constants.GET_FOLDER_COUNT_FILENET,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);

			loShareUpdatedDocumentList = (List<Document>) loChannel
					.getData(HHSR5Constants.OUTPUT_DOC_LIST);
			if (null != loShareUpdatedDocumentList) {
				FileNetOperationsUtils.sortDocTypeList(
						loShareUpdatedDocumentList,
						ApplicationConstants.DOC_TYPE);

				ApplicationSession.setAttribute(loShareUpdatedDocumentList,
						aoRequest, ApplicationConstants.SHARE_DOCUMENTS_LIST);
			}
		} catch (ApplicationException loAppEx) {
			LOG_OBJECT.Error("Exception occured in documentShareAction",
					loAppEx);
			throw loAppEx;
		} catch (Exception loAppEx) {
			LOG_OBJECT.Error("Exception occured in documentShareAction",
					loAppEx);
			throw new ApplicationException(
					"Internal Error Occured While Processing Your Request",
					loAppEx);
		}
	}

	/**
	 * This method executed when user click on back button from any overlay page
	 * it will take user to document vault home screen
	 * <ul>
	 * <li>Method updated in Release 5</li>
	 * </ul>
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action response object
	 * @param asUserOrgType
	 *            user organization type
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void goBackAction(ActionRequest aoRequest,
			ActionResponse aoResponse, String asUserOrgType)
			throws ApplicationException {
		String lsbackReqFromLocationScreen = aoRequest
				.getParameter(HHSR5Constants.BACK_INSTANCE);
		if (null == lsbackReqFromLocationScreen
				|| lsbackReqFromLocationScreen.isEmpty()) {
			deleteTempFile(aoRequest);
		}
		Document loDocument = new Document();
		loDocument = (Document) ApplicationSession.getAttribute(aoRequest,
				true, ApplicationConstants.SESSION_DOCUMENT_OBJ);
		// Removing below code(Setting properties for sample document) for R5
		// Removing end
		if (ApplicationConstants.FROM_UPLOAD_VERSION.equals(aoRequest
				.getParameter(ApplicationConstants.CALL_FROM_UPLOAD_VERSION))) {
			aoResponse.setRenderParameter(
					ApplicationConstants.FROM_UPLOAD_VERSION,
					ApplicationConstants.TRUE);
		}
		// Added if condition for Release 5
		if (null != aoRequest.getParameter(HHSR5Constants.JSP_NAME)
				&& aoRequest.getParameter(HHSR5Constants.JSP_NAME)
						.equalsIgnoreCase(HHSR5Constants.DOC_LOC)) {
			aoResponse.setRenderParameter(HHSR5Constants.JSP_NAME,
					ApplicationConstants.DISPLAY_FILE);
		}
		// End Release 5
		ApplicationSession.setAttribute(loDocument, aoRequest,
				ApplicationConstants.SESSION_DOCUMENT_OBJ);
		aoResponse.setRenderParameter(ApplicationConstants.DOC_CATEGORY,
				aoRequest.getParameter(ApplicationConstants.DOCS_CATEGORY));
		if (null != aoRequest.getParameter(ApplicationConstants.DOCS_TYPE)) {
			aoResponse.setRenderParameter(ApplicationConstants.DOCS_TYPE,
					aoRequest.getParameter(ApplicationConstants.DOCS_TYPE));
		}
	}

	/**
	 * This method executed when user click on the unshare by provider link
	 * after selecting document from document vault home screen
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action response object
	 * @param aoUserSession
	 *            portlet session object
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void unshareByProviderAction(ActionRequest aoRequest,
			ActionResponse aoResponse, P8UserSession aoUserSession)
			throws ApplicationException {
		String lsUserOrg = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ORG,
				PortletSession.APPLICATION_SCOPE);
		String lsOrgType = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_TYPE,
				PortletSession.APPLICATION_SCOPE);
		if (null != lsOrgType
				&& lsOrgType.equalsIgnoreCase(HHSConstants.CITY_ORG)) {
			lsUserOrg = lsOrgType;
		}
		TreeSet<ProviderBean> loProviderList = FileNetOperationsUtils
				.getSharedAgencyProviderListForUnshareScreen(
						(List<ProviderBean>) BaseCacheManagerWeb.getInstance()
								.getCacheObject(ApplicationConstants.PROV_LIST),
						aoUserSession,
						P8Constants.PROPERTY_CE_SHARED_PROVIDER_ID, lsUserOrg);
		TreeSet<ProviderBean> loAgencyList = FileNetOperationsUtils
				.getSharedAgencyProviderListForUnshareScreen(
						(List<ProviderBean>) BaseCacheManagerWeb.getInstance()
								.getCacheObject(ApplicationConstants.PROV_LIST),
						aoUserSession,
						P8Constants.PROPERTY_CE_SHARED_AGENCY_ID, lsUserOrg);
		TreeSet loProviderSet = new TreeSet<ProviderBean>();
		TreeSet loAgencySet = new TreeSet<ProviderBean>();
		TreeSet loAgencyProviderSet = new TreeSet<ProviderBean>();
		for (Iterator loProvIter = loProviderList.iterator(); loProvIter
				.hasNext();) {
			ProviderBean loProviderBean = (ProviderBean) loProvIter.next();
			loProviderSet.add(loProviderBean);
		}
		for (Iterator loAgencyIter = loAgencyList.iterator(); loAgencyIter
				.hasNext();) {
			ProviderBean loProviderBean = (ProviderBean) loAgencyIter.next();
			loAgencySet.add(loProviderBean);
		}
		loAgencyProviderSet.addAll(loProviderSet);
		loAgencyProviderSet.addAll(loAgencySet);
		aoRequest.getPortletSession().setAttribute(
				HHSR5Constants.PROVIDER_AGENCY_SET, loAgencyProviderSet);
		aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION,
				ApplicationConstants.UNSHARED_DOCUMENT_BY_PROVIDER);
	}

	/**
	 * 
	 * @param aoRequest
	 *            RenderRequest object
	 * @param aoResponse
	 *            RenderRequest object
	 * @return ModelAndView object
	 */
	@RenderMapping(params = "render_action=unsharedocumentbyprovider")
	protected ModelAndView handleRenderForUnshareDocumentByProvider(
			RenderRequest aoRequest, RenderResponse aoResponse) {
		String lsFormPath = null;
		try {

			aoRequest.setAttribute(
					ApplicationConstants.PROVIDER_SET,
					aoRequest.getPortletSession().getAttribute(
							HHSR5Constants.PROVIDER_AGENCY_SET));
			lsFormPath = ApplicationConstants.UNSHARE_DOCUMENT_BY_PROVIDER;

		} catch (Exception loAppEx) {
			LOG_OBJECT.Error("Exception occured in documentUploadAction",
					loAppEx);
		}
		return new ModelAndView(lsFormPath);
	}

	/**
	 * This method executed when user want to unshare the document with all the
	 * providers and agencies with whom document is shared before
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param asBfMessage
	 *            message to be displayed on the home screen
	 * @param asBfDocumentName
	 *            name of the documents selected
	 * @param aoReqProps
	 *            document properties need to be displayed
	 * @param aoLockMap
	 *            this puts a lock on the elements that are being unshared
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void removeShareWithAllProviders(ActionRequest aoRequest,
			StringBuffer asBfMessage, StringBuffer asBfDocumentName,
			HashMap aoReqProps, HashMap<String, String> aoLockMap)
			throws ApplicationException {
		String lsMessageFromProp;
		Iterator<Document> loIter;
		loIter = ((List<Document>) ApplicationSession.getAttribute(aoRequest,
				ApplicationConstants.SHARED_DOCUMENTS)).iterator();

		while (loIter.hasNext()) {
			Document loDocObj = loIter.next();
			if (null == loDocObj.getDocType()
					|| loDocObj.getDocType().isEmpty()) {
				aoReqProps.put(loDocObj.getDocumentId(), HHSR5Constants.FOLDER);
				aoLockMap.put(loDocObj.getDocumentId(), loDocObj.getDocType());
				asBfDocumentName.append(loDocObj.getDocName());
				if (loIter.hasNext()) {
					asBfDocumentName.append(HHSR5Constants.COMMA_WITH_SPACE);
				}
			} else {
				aoReqProps.put(loDocObj.getDocumentId(), loDocObj.getDocType());
				// Added parent Id of document
				asBfDocumentName.append(loDocObj.getDocName());
				if (loIter.hasNext()) {
					asBfDocumentName.append(HHSR5Constants.COMMA_WITH_SPACE);
				}
			}
		}
		lsMessageFromProp = PropertyLoader.getProperty(
				P8Constants.ERROR_PROPERTY_FILE, HHSR5Constants.MESSAGE_M07);
		asBfMessage.append(HHSConstants.SPACE);
		asBfMessage.append(lsMessageFromProp);
	}

	/**
	 * This method executed when a user want to remove the access on the
	 * document for the selected providers and agencies
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param asBfMessage
	 *            message to be displayed to the user
	 * @param asBfProvidersName
	 *            selected provider and agency name
	 * @param asBfDocumentName
	 *            name of the document selected
	 * @param aoReqProps
	 *            properties of the document to be fetched
	 * @param aoLockMap
	 *            this puts a lock on the elements that are being unshared
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ActionMapping(params = "submit_action=UnShareDocumentFromCheck")
	public void removeShareWithSelectedProvider(ActionRequest aoRequest,
			StringBuffer asBfMessage, StringBuffer asBfProvidersName,
			StringBuffer asBfDocumentName, HashMap aoReqProps,
			HashMap<String, String> aoLockMap) throws ApplicationException {
		FileNetOperationsUtils.removeMessageFromSessionAndRequest(aoRequest);
		String lsMessageFromProp;
		asBfDocumentName.append(aoRequest
				.getParameter(ApplicationConstants.DOCUMENT_NAME));
		String lsDocId = aoRequest
				.getParameter(ApplicationConstants.DOCUMENT_ID);
		String lsDocType = aoRequest.getParameter("docTypeHidden");
		aoLockMap.put(lsDocId, lsDocType);
		if (null == lsDocType || lsDocType.isEmpty()
				|| lsDocType.equalsIgnoreCase(HHSR5Constants.NULL)) {
			lsDocId = lsDocId.concat(HHSR5Constants.COMMA_WITH_FOLDER);
		} else {
			lsDocId = lsDocId.concat("," + lsDocType);
		}
		Map<String, String[]> loMap = aoRequest.getParameterMap();
		String[] loCheckedObject = loMap
				.get(ApplicationConstants.PROVIDER_CHECK);
		if (null != loCheckedObject) {
			List<String> loProvIdList = Arrays.asList(loCheckedObject);
			List loPList = new ArrayList();

			List<ProviderBean> loProviderBeanList = (List<ProviderBean>) BaseCacheManagerWeb
					.getInstance().getCacheObject(
							ApplicationConstants.PROV_LIST);
			TreeSet<String> loAgencyList = (TreeSet<String>) BaseCacheManagerWeb
					.getInstance().getCacheObject(
							ApplicationConstants.AGENCY_LIST);
			for (Iterator liIt = loProvIdList.iterator(); liIt.hasNext();) {
				String lsName = (String) liIt.next();
				String lsId = null;
				boolean lbProvFound = false;
				for (Iterator loProvIter = loProviderBeanList.iterator(); loProvIter
						.hasNext();) {
					ProviderBean loProvBean = (ProviderBean) loProvIter.next();
					String lsProviderName = StringEscapeUtils
							.unescapeJavaScript(loProvBean.getDisplayValue());
					if (lsProviderName.equalsIgnoreCase(lsName)) {
						lbProvFound = true;
						lsId = FileNetOperationsUtils.getProviderId(
								loProviderBeanList, lsName);
						loPList.add(lsId);
						break;
					}
				}
				if (!lbProvFound) {
					for (Iterator loAgencyIter = loAgencyList.iterator(); loAgencyIter
							.hasNext();) {
						String lsAgencyString = (String) loAgencyIter.next();
						String lsAgencyName = lsAgencyString
								.substring(
										lsAgencyString
												.indexOf(ApplicationConstants.TILD) + 1,
										lsAgencyString.length());
						lsAgencyName = StringEscapeUtils
								.unescapeJavaScript(lsAgencyName);
						if (lsAgencyName.equalsIgnoreCase(lsName)) {
							lsId = FileNetOperationsUtils.getAgencyId(
									loAgencyList, lsAgencyName);
							loPList.add(lsId);
							break;
						}
					}
				}

			}

			aoReqProps.put(lsDocId, loPList);

			// Add parent Id of document

			for (Iterator loProvItr = loProvIdList.iterator(); loProvItr
					.hasNext();) {
				String lsProvider = (String) loProvItr.next();
				asBfProvidersName.append(lsProvider);
				if (loProvItr.hasNext()) {
					asBfProvidersName.append(HHSR5Constants.AND_SMALL_CAPS);
				}
			}
		}
		lsMessageFromProp = PropertyLoader.getProperty(
				P8Constants.ERROR_PROPERTY_FILE, HHSR5Constants.MESSAGE_M09);
		asBfMessage.append(asBfProvidersName);
		asBfMessage.append(HHSConstants.SPACE);
		asBfMessage.append(lsMessageFromProp);
		asBfMessage.append(HHSConstants.SPACE);
		asBfMessage.append(asBfDocumentName);
	}

	/**
	 * This method is used to get the error message from the exception object if
	 * there is some exceptional case occurred which later displayed to user for
	 * information
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action response object
	 * @param aoSession
	 *            portlet session object
	 * @param asAjaxCall
	 *            whether the action is called on overlay or not
	 * @param asNextAction
	 *            next action attribute
	 * @param aoExp
	 *            exception object
	 * @throws IOException
	 *             when any IOException occurred
	 */
	public void setErrorMessageInResponse(ActionRequest aoRequest,
			ActionResponse aoResponse, PortletSession aoSession,
			String asAjaxCall, String asNextAction, ApplicationException aoExp)
			throws IOException {
		String lsErrorMsg = aoExp.toString();
		lsErrorMsg = lsErrorMsg.substring(
				lsErrorMsg.lastIndexOf(HHSConstants.COLON) + 1,
				lsErrorMsg.length()).trim();
		if (lsErrorMsg.equalsIgnoreCase(HHSR5Constants.EMPTY_STRING)) {
			lsErrorMsg = "Internal Error Occured While Processing Your Request";
		}
		if (lsErrorMsg.contains("~")) {
			lsErrorMsg = lsErrorMsg.replace("~", ":");
		}
		LOG_OBJECT.Error("Application Exception in Document Vault", aoExp);
		/*
		 * This section executes when any exception occurred while executing any
		 * operation on a overlay
		 */
		if (null != asAjaxCall
				&& asAjaxCall.equalsIgnoreCase(ApplicationConstants.TRUE)) {
			aoSession.setAttribute(
					ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
					ApplicationConstants.DOCUMENT_EXCEPTION,
					PortletSession.APPLICATION_SCOPE);
			aoSession.setAttribute(ApplicationConstants.ERROR_MESSAGE,
					lsErrorMsg, PortletSession.APPLICATION_SCOPE);
			aoSession.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE,
					PortletSession.APPLICATION_SCOPE);
			aoResponse.sendRedirect(aoRequest.getContextPath()
					+ ApplicationConstants.ERROR_HANDLER);
		} else if ((ApplicationConstants.FALSE).equalsIgnoreCase(asAjaxCall)) {
			aoSession.setAttribute(
					ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
					ApplicationConstants.BUSINESS_ERROR,
					PortletSession.APPLICATION_SCOPE);
			aoSession.setAttribute(ApplicationConstants.ERROR_MESSAGE,
					lsErrorMsg, PortletSession.APPLICATION_SCOPE);
			aoSession.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE,
					PortletSession.APPLICATION_SCOPE);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION,
					asNextAction);
		} else {
			aoSession.setAttribute(
					ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
					ApplicationConstants.DOCUMENT_EXCEPTION,
					PortletSession.APPLICATION_SCOPE);
			aoSession.setAttribute(ApplicationConstants.ERROR_MESSAGE,
					lsErrorMsg, PortletSession.APPLICATION_SCOPE);
			aoSession.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE,
					PortletSession.APPLICATION_SCOPE);
			aoSession.setAttribute(ApplicationConstants.ERROR_MAP,
					aoExp.getContextData(), PortletSession.APPLICATION_SCOPE);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION,
					ApplicationConstants.DOCUMENT_EXCEPTION);
		}
		// added for 7666
		if (null != aoRequest.getParameter(HHSConstants.IS_RENDER_ACTION)
				&& aoRequest.getParameter(HHSConstants.IS_RENDER_ACTION)
						.equalsIgnoreCase(HHSConstants.NO)) {
			aoSession.setAttribute(
					ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
					"CloseOverlay", PortletSession.APPLICATION_SCOPE);
		}
		// added for 7666
	}

	/**
	 * This method executed when we want to create one document type bean with
	 * specific attributes
	 * 
	 * @param aoDocument
	 *            document bean object
	 * @param asDocumentId
	 *            id of the document for which bean need to be created
	 * @param asUserOrg
	 *            user organization type
	 * @param aoPropsMap
	 *            required properties map
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@SuppressWarnings("rawtypes")
	public void getBeanObjectForId(Document aoDocument, String asDocumentId,
			String asUserOrg, HashMap aoPropsMap) throws ApplicationException {
		if (null != asDocumentId) {
			HashMap loDocProps = (HashMap) aoPropsMap.get(asDocumentId);
			aoDocument.setDocumentId(asDocumentId);
			aoDocument.setDocCategory((String) loDocProps
					.get(P8Constants.PROPERTY_CE_DOC_CATEGORY));
			aoDocument.setDocType((String) loDocProps
					.get(P8Constants.PROPERTY_CE_DOC_TYPE));
			aoDocument.setDocName((String) loDocProps
					.get(P8Constants.PROPERTY_CE_DOCUMENT_TITLE));
			aoDocument.setDate(DateUtil.getDateMMddYYYYFormat((Date) loDocProps
					.get(P8Constants.PROPERTY_CE_LAST_MODIFIED_DATE)));
			aoDocument.setFileType((String) loDocProps
					.get(P8Constants.PROPERTY_CE_FILE_TYPE));
			aoDocument.setDocumentProperties(getDocumentProperties(
					aoDocument.getDocCategory(), aoDocument.getDocType(),
					asUserOrg));

			if (!(aoDocument.getDocType().equalsIgnoreCase(
					ApplicationConstants.DOCUMENT_TYPE_HELP)
					|| aoDocument.getDocType().equalsIgnoreCase(
							ApplicationConstants.DOC_SAMPLE)
					|| aoDocument
							.getDocType()
							.equalsIgnoreCase(
									ApplicationConstants.DOCUMENT_TYPE_SYSTEM_TERMS_AND_CONDITIONS) || aoDocument
					.getDocType()
					.equalsIgnoreCase(
							ApplicationConstants.DOCUMENT_TYPE_APPLICATION_TERMS_AND_CONDITIONS))) {
				aoDocument.setDocumentProperties(getDocumentProperties(
						aoDocument.getDocCategory(), aoDocument.getDocType(),
						asUserOrg));
			}
			if (ApplicationConstants.DOC_SAMPLE.equalsIgnoreCase(aoDocument
					.getDocCategory())) {
				aoDocument.setSampleCategory((String) loDocProps
						.get(P8Constants.PROPERTY_CE_SAMPLE_CATEGORY));
				aoDocument.setSampleType((String) loDocProps
						.get(P8Constants.PROPERTY_CE_SAMPLE_TYPE));
			}

		}
	}

	/**
	 * This method will be executes to get document properties for a specific
	 * document type and document category
	 * 
	 * @param asDocCategory
	 *            document category
	 * @param asDocType
	 *            document type
	 * @param asUserOrg
	 *            user organization type
	 * @return list of document properties
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@SuppressWarnings("unchecked")
	public List<DocumentPropertiesBean> getDocumentProperties(
			String asDocCategory, String asDocType, String asUserOrg)
			throws ApplicationException {
		org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb
				.getInstance().getCacheObject(
						ApplicationConstants.FILENETDOCTYPE);
		return FileNetOperationsUtils.getDocPropertiesFromXML(loXMLDoc,
				asDocCategory, asDocType, asUserOrg);
	}

	/**
	 * This method will be executed when a user first navigate to document vault
	 * screen it will decide which page to display according to the organization
	 * type of the logged in user
	 * <ul>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * 
	 * @param asUserOrg
	 *            user organization type
	 * @return lsFormPath to which page user need to redirected
	 */
	private String getDisplayOrgPath(String asUserOrg) {
		String lsFormPath = HHSR5Constants.EMPTY_STRING;
		if (ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(asUserOrg)) {
			lsFormPath = ApplicationConstants.PROV_DOCUMENT_LIST_PAGE;
		} else if (ApplicationConstants.CITY_ORG.equalsIgnoreCase(asUserOrg)) {
			lsFormPath = ApplicationConstants.PROV_DOCUMENT_LIST_PAGE;
		}
		// R4 Document Vault changes: condition added for setting form path for
		// Agency Document Vault Jsp.
		else if (ApplicationConstants.AGENCY_ORG.equalsIgnoreCase(asUserOrg)) {
			lsFormPath = ApplicationConstants.PROV_DOCUMENT_LIST_PAGE;
		}
		return lsFormPath;
	}

	/**
	 * This method will be executed when user click on save button after editing
	 * the existing property of the document
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action response object
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void actionSaveProperty(ActionRequest aoRequest,
			ActionResponse aoResponse) throws ApplicationException {

		try {
			PortletSession loSession = aoRequest.getPortletSession();
			String lsUserName = (String) loSession.getAttribute(
					ApplicationConstants.KEY_SESSION_USER_NAME,
					PortletSession.APPLICATION_SCOPE);
			String lsUserId = (String) loSession.getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);
			P8UserSession loUserSession = (P8UserSession) loSession
					.getAttribute(ApplicationConstants.FILENET_SESSION_OBJECT,
							PortletSession.APPLICATION_SCOPE);
			String lsOrgId = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
							PortletSession.APPLICATION_SCOPE);
			String lsOrgType = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
							PortletSession.APPLICATION_SCOPE);
			String lsDocumentName = aoRequest
					.getParameter(HHSR5Constants.EDIT_FOLDER_NAME_TEXT);
			if (null == lsDocumentName || lsDocumentName.isEmpty()) {
				lsDocumentName = aoRequest
						.getParameter(HHSR5Constants.EDIT_DOC_NAME_TEXT);
			}
			HashMap loHmDocReqProps = new HashMap();
			List<DocumentPropertiesBean> loNewPropertiesList = new ArrayList<DocumentPropertiesBean>();
			Document loDocument = (Document) ApplicationSession.getAttribute(
					aoRequest, true, ApplicationConstants.SESSION_DOCUMENT_OBJ);
			// Release 5: change starts
			boolean lbSkipPropertySave = false;
			boolean lbIsFile = false;
			String lsReturnedDocIdonCheckExisting = null;
			// R5- for editing document properties, if edit is required or not
			if (null != loDocument
					&& null != loDocument.getDocType()
					&& !loDocument.getDocType().equalsIgnoreCase(
							HHSConstants.NULL)
					&& !loDocument.getDocType().equalsIgnoreCase(
							HHSR5Constants.EMPTY_STRING)) {
				lbIsFile = true;
				if ((null != lsDocumentName && null != loDocument && null != loDocument
						.getDocName())
						&& !(lsDocumentName.equals(loDocument.getDocName()))) {
					lsReturnedDocIdonCheckExisting = FileNetOperationsUtils
							.checkDocExist(loUserSession, lsOrgId,
									lsDocumentName, loDocument.getDocType(),
									loDocument.getDocCategory(), lsOrgType);
					if (StringUtils.isNotBlank(lsReturnedDocIdonCheckExisting)) {
						lbSkipPropertySave = true;
						// Doc Exists with same title already exists for this
						// Provider.
						// Stop Save Properties and Give Error Message
						String lsMessage = PropertyLoader.getProperty(
								P8Constants.ERROR_PROPERTY_FILE,
								HHSR5Constants.ERR_LINKED_TO_APP_RENAME);
						ApplicationSession.setAttribute(lsMessage, aoRequest,
								ApplicationConstants.ERROR_MESSAGE);
						ApplicationSession.setAttribute(loDocument, aoRequest,
								ApplicationConstants.SESSION_DOCUMENT_OBJ);
						try {
							throw new ApplicationException(lsMessage);
						} catch (ApplicationException aoExp) {
							try {
								setErrorMessageInResponse(aoRequest,
										aoResponse, loSession,
										HHSR5Constants.TRUE,
										HHSR5Constants.EMPTY_STRING, aoExp);
							} catch (IOException aoIoExp) {
								LOG_OBJECT.Error(
										"IOException during checking folder",
										aoIoExp);
								aoResponse.setRenderParameter(
										HHSConstants.RENDER_ACTION,
										ApplicationConstants.ERROR);
							}
						}
					}
				}
			}
			// R5 end- for editing document properties, if edit is required or
			// not
			// R5- for editing folder properties, if edit is required or not
			else {
				lbIsFile = false;
				if ((null != lsDocumentName && null != loDocument && null != loDocument
						.getDocName())
						&& !(lsDocumentName.equals(loDocument.getDocName()))) {
					if (checkFolderExists(loUserSession,
							loDocument.getFolderLocation()
									+ HHSConstants.FORWARD_SLASH
									+ lsDocumentName)) {

						lbSkipPropertySave = true;
						// Folder Exists with same title already exists for this
						// Provider.
						// Stop Save Properties and Give Error Message
						String lsMessage = PropertyLoader.getProperty(
								P8Constants.ERROR_PROPERTY_FILE,
								HHSR5Constants.ERR_LINKED_TO_APP_RENAME);
						ApplicationSession.setAttribute(lsMessage, aoRequest,
								ApplicationConstants.ERROR_MESSAGE);
						ApplicationSession.setAttribute(loDocument, aoRequest,
								ApplicationConstants.SESSION_DOCUMENT_OBJ);
						try {
							throw new ApplicationException(lsMessage);
						} catch (ApplicationException aoExp) {
							try {
								setErrorMessageInResponse(aoRequest,
										aoResponse, loSession,
										HHSR5Constants.TRUE,
										HHSR5Constants.EMPTY_STRING, aoExp);
							} catch (IOException aoIoExp) {
								LOG_OBJECT.Error(
										"IOException during checking folder",
										aoIoExp);
								aoResponse.setRenderParameter(
										HHSConstants.RENDER_ACTION,
										ApplicationConstants.ERROR);
							}
						}

					}
				}
			}
			// R5 end- for checking folder properties, if edit is required or
			// not
			// R5 starts- updating the new property of document/Folder
			if (!lbSkipPropertySave) {
				if (lbIsFile) {
					skipEditDocumentPropetiesAction(aoRequest, aoResponse,
							lsUserName, lsUserId, loUserSession,
							lsDocumentName, loHmDocReqProps,
							loNewPropertiesList, loDocument);
				} else {
					saveFolderProperties(aoRequest, aoResponse, lsUserName,
							lsUserId, loUserSession, lsDocumentName,
							loHmDocReqProps, loNewPropertiesList, loDocument);
				}
			}
			// R5 end- updating the new property of document/Folder
			// Release 5: change ends
		} catch (ApplicationException loAppEx) {
			LOG_OBJECT
					.Error("Exception occured   in editing document/folder properties",
							loAppEx);
			throw loAppEx;

		} catch (Exception loAppEx) {
			LOG_OBJECT
					.Error("Exception occured   in editing document/folder properties",
							loAppEx);

		}
	}

	/**
	 * This method is executed when <b>skip property save is false</b>
	 * <ul>
	 * <li>This method will execute <i>saveProperties_filenet</i> transaction to
	 * update the document property in filenet</li>
	 * <li>It will execute <i>UpdateModifiedInformation_DB</i> transaction to
	 * update the document property in data base</li>
	 * </ul>
	 * 
	 * @param aoRequest
	 *            Action Request Object
	 * @param aoResponse
	 *            Action Response Object
	 * @param asUserName
	 *            String value of user name
	 * @param asUserId
	 *            String value of user id
	 * @param aoUserSession
	 *            p8UserSession object
	 * @param asDocumentName
	 *            String value of document name
	 * @param aoHmDocReqProps
	 *            Map containing properties to be updated in Filenet and
	 *            database
	 * @param aoNewPropertiesList
	 *            list of new document properties bean
	 * @param aoDocument
	 *            document bean object
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void skipEditDocumentPropetiesAction(ActionRequest aoRequest,
			ActionResponse aoResponse, String asUserName, String asUserId,
			P8UserSession aoUserSession, String asDocumentName,
			HashMap aoHmDocReqProps,
			List<DocumentPropertiesBean> aoNewPropertiesList,
			Document aoDocument) throws ApplicationException {
		FileNetOperationsUtils.setPropertyBeanForFileUpload(aoRequest,
				aoDocument, aoHmDocReqProps);
		List<DocumentPropertiesBean> loDocumentPropsBeans = aoDocument
				.getDocumentProperties();
		Iterator<DocumentPropertiesBean> loDocPropsIt = loDocumentPropsBeans
				.iterator();
		// Updated in release 4.0.1- for removing mismatch in modified date
		String lsCurrentDate = DateUtil.getCurrentDateWithTimeStamp();
		// Updated in release 4.0.1- for removing mismatch in modified date end
		String lsNewSampleDocType = null;
		String lsSampleType = null;
		while (loDocPropsIt.hasNext()) {
			DocumentPropertiesBean loDocProps = loDocPropsIt.next();
			if (ApplicationConstants.PROPERTY_TYPE_BOOLEAN
					.equalsIgnoreCase(loDocProps.getPropertyType())) {
				if (HHSConstants.ON.equalsIgnoreCase(aoRequest
						.getParameter(loDocProps.getPropertyId()))
						|| HHSConstants.YES_LOWERCASE
								.equalsIgnoreCase(aoRequest
										.getParameter(loDocProps
												.getPropertyId()))) {
					aoHmDocReqProps.put(loDocProps.getPropSymbolicName(), true);
					loDocProps.setPropValue(true);
				} else {
					aoHmDocReqProps
							.put(loDocProps.getPropSymbolicName(), false);
					loDocProps.setPropValue(false);
				}
			} else {
				aoHmDocReqProps.put(loDocProps.getPropSymbolicName(),
						aoRequest.getParameter(loDocProps.getPropertyId()));
				loDocProps.setPropValue(aoRequest.getParameter(loDocProps
						.getPropertyId()));
			}
			if (null != loDocProps.getPropertyId()
					&& loDocProps.getPropertyId()
							.equalsIgnoreCase("sampletype")) {
				lsSampleType = aoRequest.getParameter((String) loDocProps
						.getPropertyId());
			}
			aoNewPropertiesList.add(loDocProps);
		}
		if (aoDocument.getDocType() != null
				&& aoDocument.getDocType().startsWith(
						ApplicationConstants.DOC_SAMPLE)) {
			lsNewSampleDocType = ApplicationConstants.DOC_SAMPLE + " - "
					+ lsSampleType;
			aoDocument.setDocType(lsNewSampleDocType);
			aoHmDocReqProps.put(P8Constants.PROPERTY_CE_DOC_TYPE,
					lsNewSampleDocType);
		}
		aoHmDocReqProps.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE,
				asDocumentName);
		aoHmDocReqProps.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY,
				asUserName);
		aoHmDocReqProps.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY_ID,
				asUserId);
		aoHmDocReqProps.put(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE,
				lsCurrentDate);
		ApplicationSession.setAttribute(aoDocument, aoRequest,
				ApplicationConstants.SESSION_DOCUMENT_OBJ);
		Channel loChannel = new Channel();
		loChannel.setData(HHSConstants.AO_FILENET_SESSION, aoUserSession);
		loChannel.setData(ApplicationConstants.DOCUMENT_ID,
				aoDocument.getDocumentId());
		loChannel.setData(ApplicationConstants.DOCS_TYPE,
				aoDocument.getDocType());
		loChannel
				.setData(HHSConstants.HM_REQIRED_PROPERTY_MAP, aoHmDocReqProps);
		TransactionManager.executeTransaction(loChannel,
				HHSR5Constants.SAVE_PROPERTIES_FILENET,
				HHSR5Constants.TRANSACTION_ELEMENT_R5);
		boolean lbSaveStatus = (Boolean) loChannel
				.getData(HHSConstants.SAVE_STATUS);
		if (lbSaveStatus) {
			// Defect 1786: added not condition to the existing condition
			if (!FileNetOperationsUtils.checkLinkToApplication(aoRequest,
					aoDocument.getDocumentId())) {
				HashMap loModifiedInfoMap = new HashMap();
				loModifiedInfoMap.put(HHSConstants.MODIFIED_BY, asUserId);
				loModifiedInfoMap.put(HHSConstants.DOC_ID,
						aoDocument.getDocumentId());
				loModifiedInfoMap.put(HHSConstants.MODIFIED_DATE,
						DateUtil.getSqlDate(DateUtil.getCurrentDate()));
				// Defect 1786: added the below line and updated the same in
				// FileUploadMapper
				loModifiedInfoMap.put(HHSConstants.DOCUMENT_TITLE,
						asDocumentName);
				loChannel = new Channel();
				loChannel.setData(HHSConstants.AO_MODIFIED_INFO_MAP,
						loModifiedInfoMap);
				TransactionManager.executeTransaction(loChannel,
						HHSR5Constants.UPDATE_MODIFIED_INFORMATION_DB,
						HHSR5Constants.TRANSACTION_ELEMENT_R5);
				boolean lsApplicationStatus = (Boolean) loChannel
						.getData(HHSConstants.LO_DOC_UPPDATE_STATUS);
				if (!lsApplicationStatus) {
					aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION,
							ApplicationConstants.ERROR);
				}
			}
			aoDocument.setDocumentProperties(aoNewPropertiesList);
			aoDocument.setDocName(asDocumentName);
			ApplicationSession.setAttribute(aoDocument, aoRequest,
					ApplicationConstants.SESSION_DOCUMENT_OBJ);
		} else {
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION,
					ApplicationConstants.ERROR);
		}
	}

	/**
	 * This method will be executed after the document has been uploaded
	 * successfully or the upload document process gives any exception
	 * 
	 * @param aoRequest
	 *            action request object
	 */
	private void deleteTempFile(ActionRequest aoRequest) {
		File loFilePath = new File(
				aoRequest.getParameter(ApplicationConstants.FILE_PATH));
		if (null != loFilePath) {
			loFilePath.delete();
		}
	}

	/**
	 * This message will be executed when a user successfully shared the
	 * documents with selected providers and users it will set the message tobe
	 * displayed to the user
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action response object
	 * @param aoUserSession
	 *            P8 session object
	 * @param asSharedByName
	 *            name of the user sharing the document
	 * @param aoShareWithProvList
	 *            list of the providers user selected to share with
	 * @param aoShareWithAgencyList
	 *            list of the agencies user selected to share with
	 * @param aoProvAgencyList
	 *            combine list of provider and agencies
	 * @param aoShareDocumentList
	 *            list of all the selected documents
	 * @param aoChannel
	 *            channel object to execute transaction
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 * @throws IOException
	 *             when IOException occurred
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void createShareMessageAndSendNotification(ActionRequest aoRequest,
			ActionResponse aoResponse, P8UserSession aoUserSession,
			String asSharedByName, List aoShareWithProvList,
			List aoShareWithAgencyList, List aoProvAgencyList,
			List<Document> aoShareDocumentList, Channel aoChannel)
			throws ApplicationException, IOException {
		StringBuffer lsBfMessage = new StringBuffer();
		String lsMessageFromProp = HHSR5Constants.EMPTY_STRING;
		if (aoProvAgencyList.size() > 1) {
			StringBuffer lsBfOrgName = new StringBuffer();
			// StringBuffer lsBfDocName = new StringBuffer();
			List<ProviderBean> loProviderBeanList = (List<ProviderBean>) BaseCacheManagerWeb
					.getInstance().getCacheObject(
							ApplicationConstants.PROV_LIST);
			TreeSet<String> loAgenciesList = (TreeSet<String>) BaseCacheManagerWeb
					.getInstance().getCacheObject(
							ApplicationConstants.AGENCY_LIST);
			lsMessageFromProp = PropertyLoader
					.getProperty(P8Constants.ERROR_PROPERTY_FILE,
							HHSR5Constants.MESSAGE_M06);
			Iterator<String> loProvIter = aoShareWithProvList.iterator();
			Iterator<String> loAgencyIter = aoShareWithAgencyList.iterator();
			// Iterator<Document> loDocIter = aoShareDocumentList.iterator();
			while (loProvIter.hasNext()) {
				lsBfOrgName.append(StringEscapeUtils
						.unescapeJavaScript(WordUtils
								.capitalize(FileNetOperationsUtils
										.getProviderName(loProviderBeanList,
												loProvIter.next()))));
				if (loProvIter.hasNext()) {
					lsBfOrgName.append(HHSR5Constants.AND_SMALL_CAPS);
				}
			}
			if (!lsBfOrgName.toString().isEmpty()) {
				lsBfOrgName.append(HHSR5Constants.AND_SMALL_CAPS);
			}
			while (loAgencyIter.hasNext()) {
				lsBfOrgName.append(StringEscapeUtils
						.unescapeJavaScript(WordUtils
								.capitalize(FileNetOperationsUtils
										.getAgencyName(loAgenciesList,
												loAgencyIter.next()))));
				if (loAgencyIter.hasNext()) {
					lsBfOrgName.append(HHSR5Constants.AND_SMALL_CAPS);
				}
			}
			lsBfMessage.append(lsBfOrgName);
			lsBfMessage.append(HHSConstants.SPACE);
			lsBfMessage.append(lsMessageFromProp);
			// lsBfMessage.append(lsBfDocName);
		} else if (aoProvAgencyList.size() == 1) {
			List<ProviderBean> loProviderBeanList = (List<ProviderBean>) BaseCacheManagerWeb
					.getInstance().getCacheObject(
							ApplicationConstants.PROV_LIST);
			TreeSet<String> loAgenciesList = (TreeSet<String>) BaseCacheManagerWeb
					.getInstance().getCacheObject(
							ApplicationConstants.AGENCY_LIST);
			Iterator<String> loProvIter = aoShareWithProvList.iterator();
			Iterator<String> loAgencyIter = aoShareWithAgencyList.iterator();
			lsMessageFromProp = PropertyLoader
					.getProperty(P8Constants.ERROR_PROPERTY_FILE,
							HHSR5Constants.MESSAGE_M05);
			Iterator<Document> loDocIter = aoShareDocumentList.iterator();
			StringBuffer lsBfDocName = new StringBuffer();
			while (loDocIter.hasNext()) {
				Document loDocObj = loDocIter.next();
				lsBfDocName.append(loDocObj.getDocName());
				if (loDocIter.hasNext()) {
					lsBfDocName.append(HHSConstants.COMMA);
				}
			}
			if (loProvIter.hasNext()) {
				lsBfMessage.append(FileNetOperationsUtils.getProviderName(
						loProviderBeanList, loProvIter.next()));
			}
			if (loAgencyIter.hasNext()) {
				lsBfMessage.append(StringEscapeUtils
						.unescapeJavaScript(FileNetOperationsUtils
								.getAgencyName(loAgenciesList,
										loAgencyIter.next())));
			}
			lsBfMessage.append(HHSConstants.SPACE);
			lsBfMessage.append(lsMessageFromProp);
		}
		FileNetOperationsUtils.reInitializePageIterator(
				aoRequest.getPortletSession(), aoUserSession);
		sendShareNotifications(aoRequest, asSharedByName, aoShareWithProvList,
				aoShareWithAgencyList, aoChannel);
		aoRequest.getPortletSession().setAttribute(
				ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
				ApplicationConstants.DOCUMET_VAULT_CONTROLLER_SUCCESS,
				PortletSession.APPLICATION_SCOPE);
		aoResponse.setRenderParameter(ApplicationConstants.MESSAGE,
				lsBfMessage.toString());
		aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
				ApplicationConstants.MESSAGE_PASS_TYPE);
		aoRequest.getPortletSession().removeAttribute(
				ApplicationConstants.PROVIDER_AGENCY_LIST);
	}

	/**
	 * This method will be executed when a user successfully share the document
	 * with selected providers and agencies it will call
	 * sendEmailNotificationToOrgs method to send notification to the selcted
	 * providers and agencies
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param asSharedByName
	 *            name of the user sharing the document
	 * @param asSharedBy
	 *            action response object
	 * @param aoShareWithProvList
	 *            list of selected providers
	 * @param aoShareWithAgencyList
	 *            list of selected agencies
	 * @param aoChannel
	 *            channel object to execute transaction
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@SuppressWarnings("rawtypes")
	public void sendShareNotifications(ActionRequest aoRequest,
			String asSharedByName, List aoShareWithProvList,
			List aoShareWithAgencyList, Channel aoChannel)
			throws ApplicationException {
		String lsOrgId = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ORG,
				PortletSession.APPLICATION_SCOPE);
		String lsOrgType = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_TYPE,
				PortletSession.APPLICATION_SCOPE);
		if (null != lsOrgType
				&& lsOrgType.equalsIgnoreCase(HHSConstants.CITY_ORG)) {
			lsOrgId = lsOrgType;
		}
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		StringBuffer lsBfApplicationUrl = new StringBuffer();

		String lsServerName = PropertyLoader.getProperty(
				ApplicationConstants.PROPERTY_FILE,
				HHSConstants.SERVER_NAME_FOR_PROVIDER_BATCH);
		String lsServerPort = PropertyLoader.getProperty(
				ApplicationConstants.PROPERTY_FILE,
				HHSConstants.SERVER_PORT_FOR_PROVIDER_BATCH);
		String lsContextPath = PropertyLoader.getProperty(
				ApplicationConstants.PROPERTY_FILE,
				HHSConstants.CONTEXT_PATH_FOR_PROVIDER_BATCH);
		String lsAppProtocol = PropertyLoader.getProperty(
				ApplicationConstants.PROPERTY_FILE,
				HHSConstants.SERVER_PROTOCOL_FOR_PROVIDER_BATCH);

		lsBfApplicationUrl.append(lsAppProtocol)
				.append(HHSConstants.NOTIFICATION_HREF_1).append(lsServerName)
				.append(HHSConstants.COLON).append(lsServerPort)
				.append(HHSConstants.FORWARD_SLASH).append(lsContextPath);
		lsBfApplicationUrl
				.append(ApplicationConstants.ORGANIZATION_DOCUMENTS_ALERT_NOTIFICATION_LINK);
		lsBfApplicationUrl.append("&provider=");
		lsBfApplicationUrl.append(lsOrgId);
		lsBfApplicationUrl.append("&cityUserSearchProviderId=");
		lsBfApplicationUrl.append(lsOrgId);
		lsBfApplicationUrl.append("&documentOriginator=");
		lsBfApplicationUrl.append(lsOrgType);
		FileNetOperationsUtils.sendEmailNotificationToOrgs(aoChannel,
				aoShareWithProvList, aoShareWithAgencyList, asSharedByName,
				lsBfApplicationUrl.toString(), lsOrgId, lsUserId, lsOrgType);
	}

	/**
	 * this method will be executed when a user click on return to vault link it
	 * will retain all the filter parameters and as well the filtered documents
	 * if any
	 * 
	 * @param aoUserSession
	 *            p8 session object0
	 * @param aoSession
	 *            portlet session object
	 * @param aoRequest
	 *            render request object
	 * @param asUserOrgType
	 *            user organization type
	 * @param asUserOrg
	 *            user organization name
	 * @param asSortByReqParam
	 *            sort by required parameters
	 * @param asSortTypeReqParam
	 *            sort type required parameters
	 * @return lsFormPath path to be rendered
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@SuppressWarnings("rawtypes")
	private String getRenderFilterForReturnToVault(P8UserSession aoUserSession,
			PortletSession aoSession, RenderRequest aoRequest,
			String asUserOrgType, String asUserOrg, String asSortByReqParam,
			String asSortTypeReqParam) throws ApplicationException {
		String lsFormPath;
		List<Document> loDocumentList = new ArrayList<Document>();
		Document loFilterDocumentObject = (Document) ApplicationSession
				.getAttribute(aoRequest,
						ApplicationConstants.FILTER_DOCUMENT_OBJECT);
		Document loDocument = new Document();
		HashMap<String, Object> loFilterProps = new HashMap<String, Object>();
		if (null != loFilterDocumentObject) {
			loDocument.setFilterDocCategory(loFilterDocumentObject
					.getFilterDocCategory());
			loDocument.setFilterDocType(loFilterDocumentObject
					.getFilterDocType());
			loDocument.setFilterModifiedFrom(loFilterDocumentObject
					.getFilterModifiedFrom());
			loDocument.setFilterModifiedTo(loFilterDocumentObject
					.getFilterModifiedTo());
			loDocument.setFilterProviderId(loFilterDocumentObject
					.getFilterProviderId());
			loDocument.setFilterNYCAgency(loFilterDocumentObject
					.getFilterNYCAgency());
			loDocument.setDocSharedStatus(loFilterDocumentObject
					.getDocSharedStatus());

			if ((null != loDocument.getFilterDocCategory() && !loDocument
					.getFilterDocCategory().equals(HHSR5Constants.EMPTY_STRING))
					|| (null != loDocument.getFilterDocType() && !loDocument
							.getFilterDocType().equals(
									HHSR5Constants.EMPTY_STRING))
					|| (null != loDocument.getFilterModifiedFrom() && !loDocument
							.getFilterModifiedFrom().equals(
									HHSR5Constants.EMPTY_STRING))
					|| (null != loDocument.getFilterModifiedTo() && !loDocument
							.getFilterModifiedTo().equals(
									HHSR5Constants.EMPTY_STRING))
					|| (null != loDocument.getFilterNYCAgency() && !loDocument
							.getFilterNYCAgency().equals(
									HHSR5Constants.EMPTY_STRING))
					|| (null != loDocument.getFilterProviderId()
							&& !loDocument.getFilterProviderId().equals(
									HHSR5Constants.EMPTY_STRING) || (null != loDocument
							.getDocSharedStatus()
							&& !loDocument.getDocSharedStatus().equals(
									HHSR5Constants.EMPTY_STRING) && !loDocument
							.getDocSharedStatus().equalsIgnoreCase(
									ApplicationConstants.BOTH_AGENCY_PROVIDER)))) {
				aoRequest.setAttribute(HHSR5Constants.FILTER_STATUS,
						HHSConstants.FILTERED);
			}
			aoRequest.setAttribute(HHSConstants.CATEGORY_NODE,
					loDocument.getFilterDocCategory());
			if (ApplicationConstants.DOC_SAMPLE
					.equalsIgnoreCase(loFilterDocumentObject
							.getFilterDocCategory())) {
				ArrayList<String> loSampleCategoryList = null;
				loSampleCategoryList = FileNetOperationsUtils
						.getSampleCategoryList();
				loSampleCategoryList.add(0, HHSR5Constants.EMPTY_STRING);
				loDocument.setSampleCategoryList(loSampleCategoryList);
				if (null != loFilterDocumentObject.getSampleCategory()
						&& !loFilterDocumentObject.getSampleCategory()
								.isEmpty()) {
					ArrayList<String> loSampleTypeList = new ArrayList<String>();
					loSampleTypeList = FileNetOperationsUtils
							.getSampleTypeList(loFilterDocumentObject
									.getSampleCategory());
					loSampleTypeList.add(0, HHSR5Constants.EMPTY_STRING);
					loDocument.setSampleTypeList(loSampleTypeList);
				}
				loDocument.setSampleCategory(loFilterDocumentObject
						.getSampleCategory());
				loDocument
						.setSampleType(loFilterDocumentObject.getSampleType());
			}
		}
		FileNetOperationsUtils.createAndSetFilteredProperties(
				loDocument.getFilterDocCategory(),
				loDocument.getFilterDocType(),
				loDocument.getFilterModifiedFrom(),
				loDocument.getFilterModifiedTo(),
				loDocument.getSampleCategory(), loDocument.getSampleType(),
				loDocument.getDocName(), loDocument.getSharedWith(),
				HHSR5Constants.IS_FILTER, null, null, null, null, false,
				loFilterProps, null, null);
		if (null != loFilterDocumentObject
				&& (null != loFilterDocumentObject.getFilterProviderId() || null != loFilterDocumentObject
						.getFilterNYCAgency())) {
			FileNetOperationsUtils.setFilteredPropsForProviderAndAgency(
					loDocument.getFilterDocCategory(),
					loDocument.getFilterProviderId(),
					loDocument.getFilterNYCAgency(), asUserOrgType, asUserOrg,
					loDocument, loFilterProps, HHSR5Constants.EMPTY_STRING);
		}
		if (null != loDocument.getDocSharedStatus()
				&& !"".equals(loDocument.getDocSharedStatus())) {
			String[] loRadioArray = { loDocument.getDocSharedStatus() };
			FileNetOperationsUtils.setFilteredPropertiesForSharedStatus(
					loRadioArray, asUserOrgType, asUserOrg, loDocument,
					loFilterProps);
		}
		FileNetOperationsUtils.getOrgTypeFilter(loFilterProps, asUserOrg,
				asUserOrgType);
		FileNetOperationsUtils.setReqSessionParameter(aoSession, null,
				asSortByReqParam, asSortTypeReqParam, null);

		Channel loChannel = new Channel();
		loChannel.setData(HHSConstants.AO_FILENET_SESSION, aoUserSession);
		loChannel.setData("findOrgFlag", false);

		FileNetOperationsUtils.setOrderByParameter(loChannel, asSortByReqParam,
				asSortTypeReqParam);
		FileNetOperationsUtils.reInitializePageIterator(aoSession,
				aoUserSession);
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);

		List loListDocumentProps = FileNetOperationsUtils.getDocumentList(
				loChannel, loDocument.getFilterDocType(),
				FileNetOperationsUtils.requiredDocsProps(asUserOrgType,
						loDocument.getFilterDocCategory()), loFilterProps,
				true, lsUserId);
		// Release 5 Contract Restriction
		List<Document> loDocumentContractRestriction = (List<Document>) loChannel
				.getData(HHSR5Constants.LO_DOCUMENT_CONTRACT_RESTRICTION);
		// Release 5 Contract Restriction
		String lsShareStatus = FileNetOperationsUtils.generateDocumentBean(
				loListDocumentProps, loDocumentList, asUserOrgType,
				aoUserSession, loDocumentContractRestriction, asUserOrg);

		if (null != lsShareStatus) {
			aoRequest.setAttribute(ApplicationConstants.SHARED_FLAG,
					lsShareStatus);
			FileNetOperationsUtils.setSharedWithAgencyAndProvider(asUserOrg,
					aoRequest, aoUserSession);
		}
		FileNetOperationsUtils.setDocCategorynDocType(loDocument,
				loDocument.getFilterDocCategory(), asUserOrgType);
		aoRequest.setAttribute(ApplicationConstants.SESSION_DOCUMENT_LIST,
				loDocumentList);
		aoRequest.setAttribute(
				ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_PARAMETER,
				loDocument);
		ApplicationSession.setAttribute(loDocumentList, aoRequest,
				ApplicationConstants.SESSION_DOCUMENT_LIST);

		lsFormPath = getDisplayOrgPath(asUserOrgType);
		return lsFormPath;
	}

	/**
	 * This method will be executed when a user clicks on the filter button
	 * after selecting the desired filter attributes
	 * 
	 * @param aoRequest
	 *            render request object
	 * @return lsFormPath name of the jsp to be rendered
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private String getFilterRenderResponse(RenderRequest aoRequest)
			throws ApplicationException {
		String lsFormPath = null;
		// Added for selectVault
		String lsRenderJSP = aoRequest.getParameter(HHSR5Constants.RENDER_JSP);
		// Added for defect# 7368
		String lsDoc_TYPE = aoRequest.getParameter(HHSR5Constants.DOC_TYPE);
		// end
		P8UserSession loUserSession = (P8UserSession) aoRequest
				.getPortletSession().getAttribute(
						ApplicationConstants.FILENET_SESSION_OBJECT,
						PortletSession.APPLICATION_SCOPE);
		String lsUserOrg = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_TYPE,
				PortletSession.APPLICATION_SCOPE);
		TreeSet loProviderShareSet = new TreeSet();
		if (null != aoRequest.getParameter(ApplicationConstants.SHARED_FLAG)) {
			aoRequest.setAttribute(ApplicationConstants.SHARED_FLAG,
					aoRequest.getParameter(ApplicationConstants.SHARED_FLAG));
		}
		Document loDocument = (Document) ApplicationSession.getAttribute(
				aoRequest, true, ApplicationConstants.SESSION_DOCUMENT_OBJ);
		if (loDocument != null) {
			aoRequest.setAttribute(HHSConstants.CATEGORY_NODE,
					loDocument.getFilterDocCategory());
			if (HHSR5Constants.SAMPLE.equalsIgnoreCase(loDocument
					.getFilterDocCategory())) {
				ArrayList<String> loSampleCategoryList = null;
				loSampleCategoryList = FileNetOperationsUtils
						.getSampleCategoryList();
				loSampleCategoryList.add(0, HHSR5Constants.EMPTY_STRING);
				loDocument.setSampleCategoryList(loSampleCategoryList);
			}
			if ((null != loDocument.getFilterDocCategory() && !loDocument
					.getFilterDocCategory().equals(HHSR5Constants.EMPTY_STRING))
					|| (null != loDocument.getFilterDocType() && !loDocument
							.getFilterDocType().equals(
									HHSR5Constants.EMPTY_STRING))
					|| (null != loDocument.getFilterModifiedFrom() && !loDocument
							.getFilterModifiedFrom().equals(
									HHSR5Constants.EMPTY_STRING))
					|| (null != loDocument.getFilterModifiedTo() && !loDocument
							.getFilterModifiedTo().equals(
									HHSR5Constants.EMPTY_STRING))
					|| (null != loDocument.getFilterNYCAgency() && !loDocument
							.getFilterNYCAgency().equals(
									HHSR5Constants.EMPTY_STRING))
					|| (null != loDocument.getFilterProviderId() && !loDocument
							.getFilterProviderId().equals(
									HHSR5Constants.EMPTY_STRING))
					|| (null != loDocument.getDocSharedStatus()
							&& !loDocument.getDocSharedStatus().equals(
									HHSR5Constants.EMPTY_STRING) && !loDocument
							.getDocSharedStatus().equalsIgnoreCase(
									ApplicationConstants.BOTH_AGENCY_PROVIDER))) {
				aoRequest.setAttribute(HHSR5Constants.FILTER_STATUS,
						HHSConstants.FILTERED);
			}
		}
		aoRequest.setAttribute(
				ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_PARAMETER,
				loDocument);

		aoRequest.setAttribute(
				ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_LIST_PARAMETER,
				ApplicationSession.getAttribute(aoRequest, true,
						ApplicationConstants.SESSION_DOCUMENT_LIST));
		loProviderShareSet = FileNetOperationsUtils
				.getSharedAgencyProviderList(
						(List<ProviderBean>) BaseCacheManagerWeb.getInstance()
								.getCacheObject(ApplicationConstants.PROV_LIST),
						loUserSession,
						P8Constants.PROPERTY_CE_SHARED_PROVIDER_ID, lsUserOrg);
		aoRequest.setAttribute(ApplicationConstants.PROVIDER_SET, StringUtils
				.join(loProviderShareSet.iterator(),
						ApplicationConstants.KEY_SEPARATOR));
		aoRequest
				.setAttribute(
						ApplicationConstants.AGENCY_SET,
						FileNetOperationsUtils
								.getSharedAgencyProviderList(
										(List<ProviderBean>) BaseCacheManagerWeb
												.getInstance()
												.getCacheObject(
														ApplicationConstants.PROV_LIST),
										loUserSession,
										P8Constants.PROPERTY_CE_SHARED_AGENCY_ID,
										lsUserOrg));
		String lsJspName = aoRequest.getParameter(HHSR5Constants.JSP_NAME);
		if (null != lsJspName
				&& !lsJspName.isEmpty()
				&& lsJspName
						.equalsIgnoreCase(HHSR5Constants.RECYCLE_BIN_JSP_NAME)) {
			lsFormPath = lsJspName;
		}
		// Added for Release 5- selectVault- added another case for getting
		// formPath for selectDocFromVault call
		else if (null != lsRenderJSP
				&& !lsRenderJSP.isEmpty()
				&& lsRenderJSP
						.equalsIgnoreCase(HHSConstants.ADD_DOC_FROM_VAULT)) {
			lsFormPath = addDocumentBeanPropsForSelectDoc(aoRequest);
			// Added for defect# 7368
			aoRequest.setAttribute(HHSR5Constants.DOC_TYPE, lsDoc_TYPE);
			// end
		}
		// End of release 5
		else {
			lsFormPath = ApplicationConstants.PROV_DOCUMENT_LIST_PAGE;
		}
		aoRequest.getPortletSession().setAttribute(HHSR5Constants.JSP_NAME,
				lsFormPath);
		return lsFormPath;
	}

	/**
	 * This method will be executed when a user select view document option from
	 * the drop down it will redirect user to the view document jsp page
	 * 
	 * @param aoRequest
	 *            render request object
	 * @return lsFormPath name of the jsp to render
	 */
	private String getRenderViewDocument(RenderRequest aoRequest) {
		String lsFormPath = null;
		aoRequest.setAttribute(ApplicationConstants.EDIT_VERSION_PROP,
				aoRequest.getParameter(ApplicationConstants.EDIT_VERSION_PROP));
		aoRequest.setAttribute(
				ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_PARAMETER,
				(Document) ApplicationSession.getAttribute(aoRequest, true,
						ApplicationConstants.SESSION_DOCUMENT_OBJ));
		aoRequest.setAttribute(ApplicationConstants.IS_LOCKED_STATUS,
				aoRequest.getParameter(ApplicationConstants.IS_LOCKED_STATUS));
		// Added for Release 5
		lsFormPath = ApplicationConstants.PROV_DOCUMENT_LIST_PAGE;
		return lsFormPath;
	}

	/**
	 * This method will be executed when a user moves from the document share
	 * screen third screen to fourth screen
	 * 
	 * @param aoRequest
	 *            render request object
	 * @return lsoFormPath name of the jsp to render
	 */
	private String getRenderShareDocumentStep4(ResourceRequest aoRequest) {
		String lsFormPath = null;
		String lsProvName = aoRequest
				.getParameter(ApplicationConstants.PROVIDER_NAME);

		if (null != lsProvName) {
			aoRequest.setAttribute(ApplicationConstants.PROVIDER_AGENCY_LIST,
					StringEscapeUtils.escapeJavaScript(lsProvName));
		}
		aoRequest.setAttribute(ApplicationConstants.SHARE_DOCUMENT_LIST,
				ApplicationSession.getAttribute(aoRequest, true,
						ApplicationConstants.SHARE_DOCUMENTS_LIST));
		lsFormPath = ApplicationConstants.SHARE_DOCUMENT_STEP4_PAGE;
		return lsFormPath;
	}

	/**
	 * This method will be executed when a user click on the shared link
	 * correspond to the documents this method will redirect user to the screen
	 * where the list of providers and agencies are displayed
	 * 
	 * @param aoRequest
	 *            render request object
	 * @return lsFormPath name of the jsp to render
	 */
	private String getRenderDisplayShared(RenderRequest aoRequest) {
		String lsFormPath = null;
		aoRequest.setAttribute(
				ApplicationConstants.PROVIDER_ARRAY,
				aoRequest.getPortletSession().getAttribute(
						ApplicationConstants.PROVIDER_LIST));
		aoRequest.setAttribute(ApplicationConstants.DOCUMENT_ID,
				aoRequest.getParameter(ApplicationConstants.DOCUMENT_ID));
		aoRequest.setAttribute(ApplicationConstants.DOCUMENT_NAME,
				aoRequest.getParameter(ApplicationConstants.DOCUMENT_NAME));
		lsFormPath = ApplicationConstants.DISPLAY_SHARED_DOCUMENTS;
		return lsFormPath;
	}

	/**
	 * This method will be executed when a user click on share document link
	 * after selecting documents from document vault screen it will redirect
	 * user to document sharing first page
	 * 
	 * @param aoRequest
	 *            render request object
	 * @return lsFormPath name of the jsp to render
	 */
	private String getRenderShareDocumentStep1(RenderRequest aoRequest) {
		String lsFormPath = null;
		String lsProvName = aoRequest
				.getParameter(ApplicationConstants.PROVIDER_NAME);
		aoRequest.setAttribute(ApplicationConstants.PROVIDER_NAMES, lsProvName);
		aoRequest.setAttribute(ApplicationConstants.SHARE_DOCUMENT_LIST,
				ApplicationSession.getAttribute(aoRequest, true,
						ApplicationConstants.SHARE_DOCUMENTS_LIST));
		lsFormPath = ApplicationConstants.SHARE_DOCUMENT_STEP1_PAGE;
		return lsFormPath;
	}

	/**
	 * This method will be executed when user navigate from document sharing
	 * second page to third page
	 * 
	 * @param aoRequest
	 *            render request object
	 * @param asSharedString
	 *            string
	 * @return lsFormPath name of jsp to render
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@SuppressWarnings("unchecked")
	private String getRenderShareDocumentStep3(ResourceRequest aoRequest,
			String asSharedString) throws ApplicationException {
		String lsFormPath = null;
		aoRequest.setAttribute(ApplicationConstants.PROVIDER_NAME,
				asSharedString);
		TreeSet<String> loAgencyList = (TreeSet<String>) BaseCacheManagerWeb
				.getInstance().getCacheObject(ApplicationConstants.AGENCY_LIST);
		if (null == loAgencyList || loAgencyList.isEmpty()) {
			loAgencyList = FileNetOperationsUtils.getNYCAgencyListFromDB();
		}
		aoRequest.setAttribute(ApplicationConstants.AGENCY_SET, loAgencyList);
		lsFormPath = ApplicationConstants.SHARE_DOCUMENT_STEP3_PAGE;
		return lsFormPath;
	}

	/**
	 * This method will be executed when a user click on the edit link, it will
	 * redirect user to edit document properties page
	 * 
	 * @param aoRequest
	 *            render request object
	 * @return lsFormPath name of the jsp to render
	 */
	private String getRenderEditDocument(RenderRequest aoRequest) {
		String lsFormPath = null;
		aoRequest.getPortletSession().setAttribute(
				ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_PARAMETER,
				ApplicationSession.getAttribute(aoRequest, true,
						ApplicationConstants.SESSION_DOCUMENT_OBJ));
		aoRequest.getPortletSession().setAttribute(
				ApplicationConstants.ERROR_MESSAGE,
				aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE));
		aoRequest
				.getPortletSession()
				.setAttribute(
						ApplicationConstants.ERROR_MESSAGE_TYPE,
						aoRequest
								.getParameter(ApplicationConstants.ERROR_MESSAGE_TYPE));
		lsFormPath = ApplicationConstants.PROV_DOCUMENT_LIST_PAGE;
		return lsFormPath;
	}

	/**
	 * This is a Default resource handler for controller
	 * 
	 * @param aoRequest
	 *            ResourceRequest object
	 * @param aoResponse
	 *            ResourceResponse object
	 * @return ModelAndView object with lsRenderJspPath as parameter
	 * @throws Exception
	 *             when any exception occurs
	 * 
	 */
	@ResourceMapping
	public ModelAndView handleResourceRequest(ResourceRequest aoRequest,
			ResourceResponse aoResponse) throws Exception {
		String lsNextActionParam = aoRequest
				.getParameter(ApplicationConstants.DOCUMENT_VAULT_NEXT_ACTION_PARAMETER);
		PortletSession loPortletSession = aoRequest.getPortletSession();
		String lsRenderJspPath = HHSR5Constants.EMPTY_STRING;
		// R4 Document Vault changes: Component role mapping added for Document
		// Vault agency.
		if (lsNextActionParam.equals(ApplicationConstants.SHARE_DOCUMENT_STEP2)
				&& (CommonUtil
						.getConditionalRoleDisplay(
								ComponentMappingConstant.DV_S040_PAGE,
								loPortletSession) || CommonUtil
						.getConditionalRoleDisplay(
								ComponentMappingConstant.DV_S033_AGENCY_PAGE,
								loPortletSession))) {
			String lsProvName = (String) aoRequest
					.getParameter(ApplicationConstants.PROVIDER_NAME);
			aoRequest.setAttribute(ApplicationConstants.PROVIDER_NAME,
					StringEscapeUtils.escapeJavaScript(lsProvName));
			lsRenderJspPath = ApplicationConstants.SHARE_DOCUMENT_STEP2_PAGE;
		} else if (lsNextActionParam
				.equals(ApplicationConstants.SHARE_DOCUMENT_STEP3)) {
			String lsProvName = aoRequest
					.getParameter(ApplicationConstants.PROVIDER_NAME);
			lsRenderJspPath = getRenderShareDocumentStep3(aoRequest, lsProvName);
		} else if (lsNextActionParam
				.equals(ApplicationConstants.SHARE_DOCUMENT_STEP4)) {
			lsRenderJspPath = getRenderShareDocumentStep4(aoRequest);
		} else if (lsNextActionParam
				.equals(ApplicationConstants.BACK_TO_SHARED_STEP3)) {
			String lsProvName = aoRequest
					.getParameter(ApplicationConstants.PROV_AGENCY_LIST);
			lsRenderJspPath = getRenderShareDocumentStep3(aoRequest,
					StringEscapeUtils.unescapeJavaScript(lsProvName));
		} else if (lsNextActionParam
				.equals(ApplicationConstants.BACK_TO_SHARED_STEP2)) {
			String lsProvName = aoRequest
					.getParameter(ApplicationConstants.PROVIDER_NAME);
			aoRequest.setAttribute(ApplicationConstants.PROVIDER_NAME,
					StringEscapeUtils.escapeJavaScript(lsProvName));
			lsRenderJspPath = ApplicationConstants.SHARE_DOCUMENT_STEP2_PAGE;
		} else if (lsNextActionParam
				.equals(ApplicationConstants.BACK_TO_SHARED_STEP1)) {
			String lsProvName = aoRequest
					.getParameter(ApplicationConstants.PROVIDER_NAME);
			aoRequest.setAttribute(ApplicationConstants.PROVIDER_NAME,
					StringEscapeUtils.escapeJavaScript(lsProvName));
			aoRequest.setAttribute(ApplicationConstants.SHARE_DOCUMENT_LIST,
					ApplicationSession.getAttribute(aoRequest, true,
							ApplicationConstants.SHARE_DOCUMENTS_LIST));
			lsRenderJspPath = ApplicationConstants.SHARE_DOCUMENT_STEP1_PAGE;
		}
		return new ModelAndView(lsRenderJspPath);
	}

	// Method Added for Release 5
	/**
	 * This method will handle resource request for generating json for tree
	 * 
	 * @param aoResourceRequest
	 *            ResourceRequest object
	 * @param aoResourceResponse
	 *            ResourceResponse object
	 * @throws IOException
	 *             may throw an IO Exception
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@SuppressWarnings("unchecked")
	@ResourceMapping("openTreeAjax")
	protected void handleResourceForTree(ResourceRequest aoResourceRequest,
			ResourceResponse aoResourceResponse) throws IOException,
			ApplicationException {
		//String role_current = (String) aoResourceRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_ROLE_CURRENT, PortletSession.APPLICATION_SCOPE);
		LOG_OBJECT.Debug("in handleResourceForTree");
		PrintWriter loOut = null;
		// Adding P8Session for Defect # 8150
		P8UserSession loUserSession = (P8UserSession) aoResourceRequest
				.getPortletSession().getAttribute(
						ApplicationConstants.FILENET_SESSION_OBJECT,
						PortletSession.APPLICATION_SCOPE);
		loOut = aoResourceResponse.getWriter();
		String loJson = null;
		String lsCustmOrgId = null;
		aoResourceResponse.setContentType(HHSConstants.APPLICATION_JSON);
		String lsUserOrg = (String) aoResourceRequest.getPortletSession()
				.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
						PortletSession.APPLICATION_SCOPE);
		String lsUserOrgType = (String) aoResourceRequest.getPortletSession()
				.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
						PortletSession.APPLICATION_SCOPE);
		Boolean lbManageOrgFlag = (Boolean) aoResourceRequest
				.getPortletSession().getAttribute(
						HHSR5Constants.HOME_PAGE_MANAGE_ORG_FLAG,
						PortletSession.APPLICATION_SCOPE);
		Set<String> loList = (Set<String>) aoResourceRequest
				.getPortletSession().getAttribute(
						HHSR5Constants.SHARED_ORG_SESSION_LIST);
		if (null != aoResourceRequest.getPortletSession().getAttribute(
				HHSR5Constants.TREE_MANAGE_ORG_ID)) {
			lsUserOrg = (String) aoResourceRequest.getPortletSession()
					.getAttribute(HHSR5Constants.TREE_MANAGE_ORG_ID);
			aoResourceRequest.getPortletSession().removeAttribute(
					HHSR5Constants.TREE_MANAGE_ORG_ID);
		}
		String lsDivId = null;
		try {
			if (null != lsUserOrgType
					&& lsUserOrgType.equalsIgnoreCase(HHSR5Constants.CITY_ORG)) {
				lsUserOrg = lsUserOrgType;
			}
			LOG_OBJECT.Debug("in handleResourceForTree Organization " 
					+ lsCustmOrgId);
			lsDivId = aoResourceRequest.getParameter(HHSR5Constants.DIV_ID);
			lsCustmOrgId = aoResourceRequest
					.getParameter(HHSR5Constants.ORG_ID);

            //[Start]R7.12.0 QC9311 Minimize Debug
/*            LOG_OBJECT.Debug("[Start]------------handleResourceForTree lsCustmOrgId:"+lsCustmOrgId);
            if( loList != null ){
	            for( Iterator<String>  ll = loList.iterator(); ll.hasNext(); ){
	                LOG_OBJECT.Debug("Str lst" + ll.next());
	            }
		    } else  LOG_OBJECT.Debug("Str lst NULLLLL");  
            LOG_OBJECT.Debug("lsUserOrgType:" + lsUserOrgType +
            "     lsUserOrg:" + lsUserOrg + "    lbManageOrgFlag:" + lbManageOrgFlag);
            LOG_OBJECT.Debug("[End]------------handleResourceForTree \n ");             
*/            //[End]R7.12.0 QC9311 Minimize Debug
            
			if (StringUtils.isNotBlank(lsCustmOrgId)) {
				if (lbManageOrgFlag) { // Adding userOrg for Defect # 8150
					lsUserOrg = (String) aoResourceRequest.getPortletSession()
							.getAttribute(
									ApplicationConstants.KEY_SESSION_USER_ORG,
									PortletSession.APPLICATION_SCOPE);
					loJson = FileNetOperationsUtils
							.getManageOrgFolderStructure(loList, lsUserOrgType,
									loUserSession, lsUserOrg);
				} else {
					loJson = FileNetOperationsUtils.getOtherOrgFolderStructure(
							loList, lsUserOrgType, loUserSession, lsUserOrg);
				}
			} else {
				loJson = FileNetOperationsUtils.getOrgFolderStructure(
						lsUserOrg, lsDivId, lsUserOrgType);
			}
			//LOG_OBJECT.Debug("Json   in handleResourceForTree", loJson);
			loOut.print(loJson);
		} catch (ApplicationException loAppEx) {
			LOG_OBJECT.Error("Exception occured   in handleResourceForTree",
					loAppEx);
			throw loAppEx;
		} catch (Exception loAppEx) {
			LOG_OBJECT.Error("Exception occured   in handleResourceForTree",
					loAppEx);
			throw new ApplicationException(
					"Internal Error Occured While Processing Your Request",
					loAppEx);
		}

	}

	/**
	 * This method will check if folder exists on path or not
	 * 
	 * @param aoUserSession
	 *            P8UserSession object
	 * @param asPath
	 *            path string
	 * @return boolean lbFlag
	 */
	public boolean checkFolderExists(P8UserSession aoUserSession, String asPath) {
		Channel loChannel = new Channel();
		boolean lbFlag = false;
		loChannel.setData(HHSConstants.AO_FILENET_SESSION, aoUserSession);
		loChannel.setData(HHSR5Constants.AS_PATH, asPath);
		try {
			TransactionManager.executeTransaction(loChannel,
					HHSR5Constants.CHECK_FOLDER_EXIST_FILENET,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			lbFlag = (Boolean) loChannel
					.getData(HHSR5Constants.BOOLEAN_FOLDER_FLAG);
		} catch (ApplicationException loAppEx) {
			LOG_OBJECT.Error("Exception occured   in handleResourceForTree",
					loAppEx);

		} catch (Exception loAppEx) {
			LOG_OBJECT.Error("Exception occured   in handleResourceForTree",
					loAppEx);

		}

		return lbFlag;
	}

	/**
	 * This method is used to save folder properties while editing from overlay
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action response object
	 * @param asUserName
	 *            User name string
	 * @param asUserId
	 *            User Id string
	 * @param aoUserSession
	 *            P8UserSession object
	 * @param asDocumentName
	 *            Document name string
	 * @param aoHmDocReqProps
	 *            Map containing properties to be updated in Filenet and
	 *            database
	 * @param aoNewPropertiesList
	 *            List containing values of DocumentPropertiesBean
	 * @param aoDocument
	 *            Document object
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	private void saveFolderProperties(ActionRequest aoRequest,
			ActionResponse aoResponse, String asUserName, String asUserId,
			P8UserSession aoUserSession, String asDocumentName,
			HashMap<String, String> aoHmDocReqProps,
			List<DocumentPropertiesBean> aoNewPropertiesList,
			Document aoDocument) throws ApplicationException {

		// Updated in release 4.0.1- for removing mismatch in modified date
		String lsCurrentDate = DateUtil.getCurrentDateWithTimeStamp();
		// Updated in release 4.0.1- for removing mismatch in modified date end
		String lsUserOrg = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ORG,
				PortletSession.APPLICATION_SCOPE);
		String lsUserOrgType = (String) aoRequest.getPortletSession()
				.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
						PortletSession.APPLICATION_SCOPE);
		if (null != lsUserOrgType
				&& lsUserOrgType.equalsIgnoreCase(HHSR5Constants.CITY_ORG)) {
			lsUserOrg = lsUserOrgType;
		}
		aoHmDocReqProps.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE,
				asDocumentName);
		// change in R5
		aoHmDocReqProps.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY,
				asUserName);
		// change in R5
		aoHmDocReqProps.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY_ID,
				asUserId);
		aoHmDocReqProps.put(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE,
				lsCurrentDate);
		aoHmDocReqProps.put(HHSR5Constants.FOLDER_ID,
				aoDocument.getDocumentId());
		aoHmDocReqProps.put(HHSConstants.ORGANIZATION_ID, lsUserOrg);

		try {
			Channel loChannel = new Channel();
			loChannel.setData(HHSConstants.AO_FILENET_SESSION, aoUserSession);
			loChannel.setData(HHSConstants.HM_REQIRED_PROPERTY_MAP,
					aoHmDocReqProps);
			TransactionManager.executeTransaction(loChannel,
					HHSR5Constants.SAVE_PROPERTIES_OF_FOLDER,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			aoResponse.setRenderParameter(HHSR5Constants.UPDATED_FOLDER_NAME,
					asDocumentName);
		} catch (ApplicationException loAppEx) {
			LOG_OBJECT.Error("Exception occured   in handleResourceForTree",
					loAppEx);
			throw loAppEx;

		} catch (Exception loAppEx) {
			LOG_OBJECT.Error("Exception occured   in handleResourceForTree",
					loAppEx);

		}
	}

	/**
	 * This method is mapped to Action Requests coming with submit_action
	 * createFolder to create New Folder in filenet
	 * 
	 * @param aoRequest
	 *            a action request object
	 * @param aoResponse
	 *            a action response object
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ActionMapping(params = "submit_action=createFolder")
	protected void createFolder(ActionRequest aoRequest,
			ActionResponse aoResponse) throws ApplicationException {
		String lsAjaxCall = null;
		String lsSelectedFolderId = null;
		Channel loChannel = null;
		List<FolderMappingBean> loMappingBean = new ArrayList<FolderMappingBean>();
		String lsFolderId = null;
		List<String> loLockList = new ArrayList<String>();
		try {
			FileNetOperationsUtils
					.removeMessageFromSessionAndRequest(aoRequest);
			lsAjaxCall = aoRequest
					.getParameter(ApplicationConstants.IS_AJAX_CALL);
			lsSelectedFolderId = aoRequest
					.getParameter(HHSR5Constants.SELECTED_FLDR_ID);
			if (StringUtils.isNotBlank(lsSelectedFolderId)) {

				P8UserSession loUserSession = (P8UserSession) aoRequest
						.getPortletSession().getAttribute(
								ApplicationConstants.FILENET_SESSION_OBJECT,
								PortletSession.APPLICATION_SCOPE);
				Channel loChannelLock = new Channel();
				HashMap<String, String> loDocIdMap = new HashMap<String, String>();
				loDocIdMap.put(lsSelectedFolderId, "");
				loChannelLock.setData("documentIdMap", loDocIdMap);
				loChannelLock.setData("aoFilenetSession", loUserSession);
				loChannelLock.setData("next_action", "createFolder");
				HHSTransactionManager.executeTransaction(loChannelLock,
						"getFolderPathFromFilenet",
						HHSR5Constants.TRANSACTION_ELEMENT_R5);
				HashMap<String, List<String>> loMap = (HashMap) loChannelLock
						.getData("loPathMap");

				for (Map.Entry<String, List<String>> entry : loMap.entrySet()) {
					List<String> loList = entry.getValue();
					for (Iterator iterator = loList.iterator(); iterator
							.hasNext();) {
						String lsLockPath = (String) iterator.next();
						loLockList.add(lsLockPath);
					}
				}
				String lsUserOrg = (String) aoRequest.getPortletSession()
						.getAttribute(
								ApplicationConstants.KEY_SESSION_USER_ORG,
								PortletSession.APPLICATION_SCOPE);
				String lsUserOrgType = (String) aoRequest.getPortletSession()
						.getAttribute(
								ApplicationConstants.KEY_SESSION_ORG_TYPE,
								PortletSession.APPLICATION_SCOPE);
				if (null != lsUserOrgType
						&& lsUserOrgType
								.equalsIgnoreCase(HHSR5Constants.USER_CITY)) {
					lsUserOrg = lsUserOrgType;
				}
				if (FileNetOperationsUtils.checkLock(loLockList, lsUserOrg)) {
					ApplicationException loAppex = new ApplicationException(
							"Folder could not be created. "
									+ "The selected destination folder has a current transaction in progress. "
									+ "Please try again after the transaction is complete.");
					lsAjaxCall = HHSR5Constants.TRUE;
					throw loAppex;
				} else {
					loChannel = FileNetOperationsUtils.createFolder(aoRequest,
							lsSelectedFolderId);
					if (null != loChannel) {
						HHSTransactionManager.executeTransaction(loChannel,
								HHSR5Constants.CREATE_FOLDER_IN_FILENET_AND_DB,
								HHSR5Constants.TRANSACTION_ELEMENT_R5);
						loMappingBean = (List<FolderMappingBean>) loChannel
								.getData(HHSR5Constants.RETURN_BEAN);
						for (Iterator iterator = loMappingBean.iterator(); iterator
								.hasNext();) {
							FolderMappingBean folderMappingBean = (FolderMappingBean) iterator
									.next();
							if (null != folderMappingBean
									&& !folderMappingBean
											.getFolderName()
											.equalsIgnoreCase(
													HHSR5Constants.DOCUMENT_VAULT)) {
								lsFolderId = folderMappingBean
										.getFolderFilenetId();
							}
						}

						aoResponse.setRenderParameter(
								HHSR5Constants.NEW_FLD_ID, lsFolderId);
						aoResponse.setRenderParameter(
								ApplicationConstants.ERROR_MESSAGE,
								PropertyLoader.getProperty(
										P8Constants.ERROR_PROPERTY_FILE,
										HHSR5Constants.MESSAGE_M111));
						aoResponse.setRenderParameter(
								ApplicationConstants.ERROR_MESSAGE_TYPE,
								ApplicationConstants.MESSAGE_PASS_TYPE);
						aoResponse.setRenderParameter(
								HHSR5Constants.HEADER_CLICK, "true");
						aoResponse.setRenderParameter(HHSR5Constants.FOLDER_ID,
								lsSelectedFolderId);

					}
				}

			} else {
				ApplicationException loAppex = new ApplicationException(
						"Please select a folder Name");
				throw loAppex;

			}

		} catch (ApplicationException loAppEx) {
			try {
				setErrorMessageInResponse(aoRequest, aoResponse,
						aoRequest.getPortletSession(), lsAjaxCall,
						HHSR5Constants.EMPTY_STRING, loAppEx);
			} catch (Exception aoIoExp) {
				LOG_OBJECT.Error("Exception during creating folder", aoIoExp);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION,
						ApplicationConstants.ERROR);
			}
		} catch (Exception aoExp) {
			LOG_OBJECT.Error("Exception in Creating folder", aoExp);
			throw new ApplicationException(
					"Internal Error Occured While Processing Your Request",
					aoExp);
		}
	}

	/**
	 * This method will handle render requets folder
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action response object
	 * @return jspname as lsformpath
	 */
	@RenderMapping(params = "render_action=createFolder")
	protected ModelAndView handleRenderforCreateFolder(RenderRequest aoRequest,
			RenderResponse aoResponse) {
		
		String lsFormPath = null;
		try {
			
			lsFormPath = getFilterRenderResponse(aoRequest);
			String lsNewFolderId = aoRequest
					.getParameter(HHSR5Constants.NEW_FLD_ID);
			String lsHeaderFlag = HHSPortalUtil.parseQueryString(aoRequest,
					HHSR5Constants.HEADER_CLICK);
			String lsMessage = (null == aoRequest
					.getParameter(ApplicationConstants.ERROR_MESSAGE)) ? (String) aoRequest
					.getPortletSession().getAttribute(
							ApplicationConstants.ERROR_MESSAGE,
							PortletSession.PORTLET_SCOPE) : aoRequest
					.getParameter(ApplicationConstants.ERROR_MESSAGE);

			String lsMessageType = (null == aoRequest
					.getParameter(ApplicationConstants.ERROR_MESSAGE_TYPE)) ? (String) aoRequest
					.getPortletSession().getAttribute(
							ApplicationConstants.ERROR_MESSAGE_TYPE,
							PortletSession.PORTLET_SCOPE) : aoRequest
					.getParameter(ApplicationConstants.ERROR_MESSAGE_TYPE);

			aoRequest.setAttribute(HHSR5Constants.NEW_FLD_ID, lsNewFolderId);
			aoRequest.setAttribute(HHSR5Constants.HEADER_CLICK, lsHeaderFlag);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
					lsMessage);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					lsMessageType);

		} catch (ApplicationException loAppEx) {
			LOG_OBJECT.Error("Exception occured   in documentUploadAction",
					loAppEx);
		} catch (Exception loAppEx) {
			LOG_OBJECT.Error("Exception occured in documentUploadAction",
					loAppEx);
		}
		return new ModelAndView(lsFormPath);
	}

	/**
	 * This method will open folder
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action response object
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@ActionMapping(params = "submit_action=openFolder")
	protected void openFolder(ActionRequest aoRequest, ActionResponse aoResponse)
			throws ApplicationException {
		try {
			FileNetOperationsUtils
					.removeMessageFromSessionAndRequest(aoRequest);
			aoRequest.getPortletSession().removeAttribute(
					HHSConstants.FILENET_FILTER_MAP,
					PortletSession.PORTLET_SCOPE);
			aoRequest.getPortletSession()
					.removeAttribute(HHSR5Constants.CLICKED_SEARCH,
							PortletSession.PORTLET_SCOPE);
			aoRequest.getPortletSession().removeAttribute(
					HHSR5Constants.JSP_NAME);
			aoRequest.getPortletSession().removeAttribute(
					HHSR5Constants.FOLDER_PATH);
			aoRequest.getPortletSession().removeAttribute(
					ApplicationConstants.ERROR_MESSAGE,
					PortletSession.PORTLET_SCOPE);
			aoRequest.setAttribute("openFlag", true);
			aoRequest.getPortletSession().removeAttribute(
					ApplicationConstants.ERROR_MESSAGE_TYPE,
					PortletSession.PORTLET_SCOPE);
			FileNetOperationsUtils.actionFilterDocument(aoRequest, aoResponse);
			aoResponse.setRenderParameter(HHSR5Constants.RENDER_ACTION,
					HHSR5Constants.OPEN_FOLDER);
			if (null != aoRequest.getParameter("ManageOrgFlag")
					&& !aoRequest.getParameter("ManageOrgFlag").isEmpty()) {
				aoResponse.setRenderParameter("ManageOrgFlag",
						aoRequest.getParameter("ManageOrgFlag"));
			}

			// Added for Release 5- flag to identify call from
			// selectDocFromVault
			String lsRenderJSP = aoRequest
					.getParameter(HHSR5Constants.RENDER_JSP_NAME);
			String lsselectDocForRelease = aoRequest
					.getParameter(HHSR5Constants.SELECT_DOC_RELEASE);
			if (StringUtils.isNotBlank(lsRenderJSP)) {
				aoResponse.setRenderParameter(HHSR5Constants.RENDER_JSP,
						lsRenderJSP);
				aoResponse.setRenderParameter(ApplicationConstants.ACTION,
						HHSR5Constants.DOCUMENT_VALUT_KEY);
			} else {
				aoResponse.setRenderParameter(ApplicationConstants.ACTION,
						HHSR5Constants.VALUT_KEY);
			}
			if (null != lsselectDocForRelease
					&& !lsselectDocForRelease.isEmpty()
					&& !lsselectDocForRelease
							.equalsIgnoreCase(HHSR5Constants.EMPTY_STRING)) {
				aoResponse.setRenderParameter(
						HHSR5Constants.SELECT_DOC_RELEASE,
						lsselectDocForRelease);
			}
			// End of release 5
		} catch (ApplicationException loAppEx) {
			LOG_OBJECT.Error("Exception in Document Vault", loAppEx);
			try {
				setErrorMessageInResponse(aoRequest, aoResponse,
						aoRequest.getPortletSession(), "false", "", loAppEx);
			} catch (IOException loIoExp) {
				LOG_OBJECT.Error(
						"Exception in opening folder in Document Vault",
						loIoExp);
			}
			// throw loAppEx;
		} catch (Exception aoExp) {
			LOG_OBJECT.Error("Exception in opening folder in Document Vault",
					aoExp);
		}
	}

	/**
	 * This method will open folder
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action response object
	 * @return jspname as lsformpath
	 */
	@RenderMapping(params = "render_action=openFolder")
	protected ModelAndView handleRenderforOpenFolder(RenderRequest aoRequest,
			RenderResponse aoResponse) {
		String lsFormPath = null;
		try {

			lsFormPath = getFilterRenderResponse(aoRequest);
			String lsManageOrgFlag = aoRequest.getParameter("ManageOrgFlag");
			if (StringUtils.isNotBlank(lsManageOrgFlag)) {
				aoRequest.getPortletSession().setAttribute(
						HHSR5Constants.HOME_PAGE_MANAGE_ORG_FLAG, true,
						PortletSession.APPLICATION_SCOPE);
				aoRequest.setAttribute(HHSR5Constants.HEADER_CLICK, false);
			} else {
				aoRequest.getPortletSession().setAttribute(
						HHSR5Constants.HOME_PAGE_MANAGE_ORG_FLAG, false,
						PortletSession.APPLICATION_SCOPE);
				aoRequest.setAttribute(HHSR5Constants.HEADER_CLICK, true);
			}

		} catch (ApplicationException loAppEx) {
			LOG_OBJECT.Error("Exception occured   in documentUploadAction",
					loAppEx);
		} catch (Exception loAppEx) {
			LOG_OBJECT.Error("Exception occured in documentUploadAction",
					loAppEx);
		}
		return new ModelAndView(lsFormPath);
	}

	/**
	 * This method will handle ActionRequest coming for documentUpload screen
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action response object
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@ActionMapping(params = "submit_action=documentupload")
	protected void uploadRequest(ActionRequest aoRequest,
			ActionResponse aoResponse) throws ApplicationException {
		try {
			LOG_OBJECT.Info("====uploadRequest===");
			FileNetOperationsUtils
					.removeMessageFromSessionAndRequest(aoRequest);
			String lsUserOrgType = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
							PortletSession.APPLICATION_SCOPE);
			aoRequest.getPortletSession().removeAttribute(
					HHSR5Constants.UPLOAD_FORM_DATA);
			if (CommonUtil.getConditionalRoleDisplay(
					ComponentMappingConstant.DV_S032_PAGE,
					aoRequest.getPortletSession())) {
				documentUploadAction(aoRequest, aoResponse, lsUserOrgType);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION,
						ApplicationConstants.DOCUMENT_UPLOAD);
			}
		} catch (ApplicationException loAppEx) {
			LOG_OBJECT.Error("Exception in upload screen of Document Vault",
					loAppEx);
			throw loAppEx;
		} catch (Exception aoExp) {
			LOG_OBJECT.Error(
					"Exception in document upload screen in Document Vault",
					aoExp);
			throw new ApplicationException(
					"Internal Error Occured While Processing Your Request",
					aoExp);
		}
	}

	/**
	 * This method will handle renderRequest for documentUplaod Action
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action response object
	 * @return jspname as jsformpath
	 */
	@RenderMapping(params = "render_action=documentupload")
	protected ModelAndView handleRenderforDocumentUplaodScreen(
			RenderRequest aoRequest, RenderResponse aoResponse) {
		String lsFormPath = null;
		try {
			aoRequest.setAttribute(ApplicationConstants.SAMPLE,
					aoRequest.getParameter(ApplicationConstants.SAMPLE));
			aoRequest.setAttribute(ApplicationConstants.DOC_CATEGORY,
					aoRequest.getParameter(ApplicationConstants.DOC_CATEGORY));
			aoRequest.setAttribute(ApplicationConstants.DOCS_TYPE,
					aoRequest.getParameter(ApplicationConstants.DOCS_TYPE));
			// Added for combo box
			aoRequest.setAttribute(HHSR5Constants.DROPDOWN_DOC_TYPE,
					ApplicationSession.getAttribute(aoRequest, true,
							HHSR5Constants.DROPDOWN_DOC_TYPE));
			// end
			// below if will be executed when a new version of the
			// document is uploaded successfully
			if (StringUtils.isNotBlank(aoRequest
					.getParameter(ApplicationConstants.FROM_UPLOAD_VERSION))) {
				Document loDocObj = (Document) ApplicationSession.getAttribute(
						aoRequest, true,
						ApplicationConstants.SESSION_DOCUMENT_OBJ);
				String lsHelpCategory = loDocObj.getDocCategory();
				aoRequest.setAttribute(ApplicationConstants.DOCUMENT_CATEGORY,
						lsHelpCategory);
				aoRequest.setAttribute(
						ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_PARAMETER,
						loDocObj);
				lsFormPath = ApplicationConstants.UPLOAD_NEW_FILE_VERSION;
			}
			// Added else if condition for Release 5
			else if (StringUtils.isNotBlank(aoRequest
					.getParameter(HHSR5Constants.JSP_NAME))) {
				aoRequest.setAttribute(
						ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_PARAMETER,
						ApplicationSession.getAttribute(aoRequest,
								ApplicationConstants.SESSION_DOCUMENT_OBJ));
				Document loDoc = new Document();
				loDoc = (Document) aoRequest.getPortletSession().getAttribute(
						"DocumentSessionBean");
				aoRequest.setAttribute(
						ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_PARAMETER,
						loDoc);
				lsFormPath = aoRequest.getParameter(HHSR5Constants.JSP_NAME);
				aoRequest.setAttribute(ApplicationConstants.DOCUMENT_CATEGORY,
						loDoc.getDocCategory());
			}
			// End Release 5
			else {
				Document loDocument = (Document) ApplicationSession
						.getAttribute(aoRequest,
								ApplicationConstants.SESSION_DOCUMENT_OBJ);
				if (aoRequest.getParameter("isNotFromVault") != null
						&& ((String) aoRequest
								.getParameter(HHSR5Constants.IS_NOT_FROM_VAULT))
								.equalsIgnoreCase(HHSR5Constants.TRUE)) {
					Map<String, Object> loDataMap = (Map<String, Object>) aoRequest
							.getPortletSession().getAttribute(
									HHSR5Constants.UPLOAD_FORM_DATA);
					if (loDocument == null)
						loDocument = new Document();
					if (loDataMap != null) {
						loDocument.setDocType((String) loDataMap
								.get(HHSR5Constants.DOC_TYPE));
						loDocument.setDocCategory((String) loDataMap
								.get(HHSR5Constants.DOC_CATEGORY));
						loDocument.setSectionId((String) loDataMap
								.get(HHSR5Constants.SECTION_ID));
					}
				}
				aoRequest.setAttribute(
						ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_PARAMETER,
						loDocument);
				lsFormPath = ApplicationConstants.UPLOAD_FILE;
			}
			aoRequest.getPortletSession().setAttribute(
					HHSR5Constants.HOME_PAGE_MANAGE_ORG_FLAG, false,
					PortletSession.APPLICATION_SCOPE);
		} catch (Exception loAppEx) {
			LOG_OBJECT.Error("Exception occured in documentUploadAction",
					loAppEx);
		}
		return new ModelAndView(lsFormPath);
	}

	/**
	 * This method will handle action Request for showing document metadata on
	 * screen .It will invoke when clicked on next button of Upload Screen
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action response object
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@ActionMapping(params = "submit_action=fileinformation")
	protected void fileInformation(ActionRequest aoRequest,
			ActionResponse aoResponse) throws ApplicationException {
		try {
			FileNetOperationsUtils
					.removeMessageFromSessionAndRequest(aoRequest);
			PortletSession aoSession = aoRequest.getPortletSession();
			if ((CommonUtil.getConditionalRoleDisplay(
					ComponentMappingConstant.DV_S033_PROVIDER_PAGE, aoSession))
					|| (CommonUtil.getConditionalRoleDisplay(
							ComponentMappingConstant.DV_S033_CITY_PAGE,
							aoSession))
					|| (CommonUtil.getConditionalRoleDisplay(
							ComponentMappingConstant.HE_N01_SECTION_5,
							aoSession))) {
				// R5: removing message from session
				aoSession.removeAttribute(ApplicationConstants.ERROR_MESSAGE);
				aoSession.removeAttribute(ApplicationConstants.ERROR_MESSAGE,
						PortletSession.APPLICATION_SCOPE);
				// R5 end
				FileNetOperationsUtils.actionFileInformation(aoRequest,
						aoResponse);
				aoResponse.setRenderParameter(ApplicationConstants.ACTION,
						HHSR5Constants.DOCUMENT_VALUT_KEY);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION,
						HHSR5Constants.FILE_INFORMATION);
			}
		} catch (ApplicationException loAppEx) {
			try {
				setErrorMessageInResponse(aoRequest, aoResponse,
						aoRequest.getPortletSession(), HHSR5Constants.TRUE,
						HHSR5Constants.EMPTY_STRING, loAppEx);
			} catch (IOException aoIoExp) {
				LOG_OBJECT.Error("IOException during checking folder", aoIoExp);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION,
						ApplicationConstants.ERROR);
			}
		} catch (Exception aoExp) {
			LOG_OBJECT.Error(
					"Exception in action file info screen in Document Vault",
					aoExp);
			throw new ApplicationException(
					"Internal Error Occured While Processing Your Request",
					aoExp);
		}
	}

	/**
	 * This method will view document information
	 * 
	 * @param aoRequest
	 *            render request object
	 * @param aoResponse
	 *            render request object
	 * @return ModelAndView object with lsFormPath string as parameter
	 */
	@RenderMapping(params = "render_action=fileinformation")
	protected ModelAndView handleRenderforDocumentInformationScreen(
			RenderRequest aoRequest, RenderResponse aoResponse) {
		String lsFormPath = null;
		try {
			String lsErrorMessage = null;
			String lsMessageType = null;
			lsErrorMessage = (String) ApplicationSession.getAttribute(
					aoRequest, false, ApplicationConstants.ERROR_MESSAGE);
			if (null == lsErrorMessage || lsErrorMessage.isEmpty()) {
				lsErrorMessage = (String) aoRequest.getPortletSession()
						.getAttribute(ApplicationConstants.MESSAGE,
								PortletSession.PORTLET_SCOPE);
				aoRequest.getPortletSession().removeAttribute(
						ApplicationConstants.MESSAGE,
						PortletSession.PORTLET_SCOPE);
			}
			// Fix defect 5661 starts. Setting "OldDocumentIdReq" attribtue in
			// request
			if (lsErrorMessage != null
					&& (lsErrorMessage
							.equalsIgnoreCase(PropertyLoader.getProperty(
									P8Constants.ERROR_PROPERTY_FILE, "M49")) || lsErrorMessage
							.equalsIgnoreCase(PropertyLoader.getProperty(
									P8Constants.ERROR_PROPERTY_FILE,
									HHSR5Constants.M08)))) {
				aoRequest.setAttribute(HHSConstants.OLD_DOCUMENT_ID_REQ,
						ApplicationSession.getAttribute(aoRequest,
								HHSConstants.OLD_DOCUMENT_ID_REQ));
			}
			// Fix defect 5661 ends
			lsMessageType = (String) ApplicationSession.getAttribute(aoRequest,
					false, ApplicationConstants.ERROR_MESSAGE_TYPE);
			if (null == lsMessageType || lsMessageType.isEmpty()) {
				lsMessageType = (String) aoRequest.getPortletSession()
						.getAttribute(ApplicationConstants.MESSAGE_TYPE,
								PortletSession.PORTLET_SCOPE);
				aoRequest.getPortletSession().removeAttribute(
						ApplicationConstants.MESSAGE_TYPE,
						PortletSession.PORTLET_SCOPE);
			}
			if (null == lsMessageType || lsMessageType.isEmpty()) {
				lsMessageType = aoRequest
						.getParameter(ApplicationConstants.ERROR_MESSAGE_TYPE);
				lsErrorMessage = aoRequest
						.getParameter(ApplicationConstants.ERROR_MESSAGE);

			}
			aoRequest.setAttribute(ApplicationConstants.LINKED_TO_APP_FLAG,
					ApplicationSession.getAttribute(aoRequest,
							ApplicationConstants.LINKED_TO_APP));
			aoRequest.setAttribute(
					ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_PARAMETER,
					ApplicationSession.getAttribute(aoRequest, true,
							ApplicationConstants.SESSION_DOCUMENT_OBJ));
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
					lsErrorMessage);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					lsMessageType);
			Document loDocObj = (Document) ApplicationSession.getAttribute(
					aoRequest, true, ApplicationConstants.SESSION_DOCUMENT_OBJ);
			String lsHelpCategory = loDocObj.getDocCategory();
			aoRequest.setAttribute(ApplicationConstants.DOCUMENT_CATEGORY,
					lsHelpCategory);
			aoRequest
					.setAttribute(
							ApplicationConstants.FROM_UPLOAD_VERSION,
							aoRequest
									.getParameter(ApplicationConstants.FROM_UPLOAD_VERSION));
			lsFormPath = ApplicationConstants.DISPLAY_FILE;
		} catch (Exception loAppEx) {
			LOG_OBJECT.Error("Exception occured in documentUploadAction",
					loAppEx);
		}
		return new ModelAndView(lsFormPath);
	}

	/**
	 * This method will upload file
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action response object
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@ActionMapping(params = "submit_action=fileupload")
	protected void fileUpload(ActionRequest aoRequest, ActionResponse aoResponse)
			throws ApplicationException {
		PortletSession loSession = aoRequest.getPortletSession();
		List<String> loLockList = new ArrayList<String>();
		try {
			FileNetOperationsUtils
					.removeMessageFromSessionAndRequest(aoRequest);
			HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb
					.getInstance().getCacheObject(
							HHSR5Constants.APPLICATION_SETTING);
			String lsLockingFlag = (String) loApplicationSettingMap
					.get(HHSR5Constants.DOCUMENT_VAULT_LOCKING_FLAG);
			String lsUserOrg = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
							PortletSession.APPLICATION_SCOPE);
			String lsUserOrgType = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
							PortletSession.APPLICATION_SCOPE);
			if (null != lsUserOrgType
					&& lsUserOrgType.equalsIgnoreCase(HHSR5Constants.USER_CITY)) {
				lsUserOrg = lsUserOrgType;
			}
			if (StringUtils.isNotBlank(aoRequest
					.getParameter(HHSR5Constants.CUSTOM_FLDR_ID))) {
				P8UserSession loUserSession = (P8UserSession) aoRequest
						.getPortletSession().getAttribute(
								ApplicationConstants.FILENET_SESSION_OBJECT,
								PortletSession.APPLICATION_SCOPE);
				Channel loChannelLock = new Channel();
				HashMap<String, String> loDocIdMap = new HashMap<String, String>();
				loDocIdMap.put(
						aoRequest.getParameter(HHSR5Constants.CUSTOM_FLDR_ID),
						"");
				loChannelLock.setData("documentIdMap", loDocIdMap);
				loChannelLock.setData("aoFilenetSession", loUserSession);
				loChannelLock.setData("next_action", "upload");
				HHSTransactionManager.executeTransaction(loChannelLock,
						"getFolderPathFromFilenet",
						HHSR5Constants.TRANSACTION_ELEMENT_R5);
				HashMap<String, List<String>> loMap = (HashMap<String, List<String>>) loChannelLock
						.getData("loPathMap");
				for (Map.Entry<String, List<String>> entry : loMap.entrySet()) {
					List<String> loList = entry.getValue();
					for (Iterator iterator = loList.iterator(); iterator
							.hasNext();) {
						String lsLockPath = (String) iterator.next();
						loLockList.add(lsLockPath);
					}
				}
			} else {
				ApplicationException loAppex = new ApplicationException(
						"Please Select a Folder");
				throw loAppex;
			}
			if (Boolean.valueOf(lsLockingFlag)
					&& FileNetOperationsUtils.checkLock(loLockList, lsUserOrg)) {
				ApplicationException loAppex = new ApplicationException(
						"Document could not be uploaded. "
								+ "A transaction is in progress in the selected folder. "
								+ "Please try again after the transaction is complete.");
				throw loAppex;
			} else {
				Document loDocument = (Document) aoRequest.getPortletSession()
						.getAttribute("DocumentSessionBean");
				ApplicationSession.setAttribute(loDocument, aoRequest,
						ApplicationConstants.SESSION_DOCUMENT_OBJ);
				FileNetOperationsUtils.actionFileUpload(aoRequest, aoResponse);
				String lsParentFolderId = aoRequest
						.getParameter(HHSR5Constants.CUSTOM_FLDR_ID);
				if (StringUtils.isNotBlank(lsParentFolderId)) {
					aoResponse.setRenderParameter(HHSR5Constants.FOLDER_ID,
							lsParentFolderId);
				}

			}
			aoResponse.setRenderParameter(HHSR5Constants.HEADER_CLICK, "true");
		} catch (ApplicationException loAppEx) {
			LOG_OBJECT.Error("Exception in Document Vault", loAppEx);
			// Fix for Defect #7869
			String lsMessage = PropertyLoader.getProperty(
					P8Constants.ERROR_PROPERTY_FILE,
					HHSConstants.FILE_UPLOAD_FAIL_MESSAGE);
			loAppEx = new ApplicationException(lsMessage);
			// Fix for Defect #7869 end
			try {
				setErrorMessageInResponse(aoRequest, aoResponse, loSession,
						HHSR5Constants.TRUE, HHSR5Constants.EMPTY_STRING,
						loAppEx);
			} catch (IOException aoIoExp) {
				LOG_OBJECT.Error("IOException during file upload", aoIoExp);
				aoResponse.setRenderParameter(
						ApplicationConstants.RENDER_ACTION,
						ApplicationConstants.ERROR);
			}
		} catch (Exception aoExp) {
			LOG_OBJECT.Error(
					"Exception in action file upload screen in Document Vault",
					aoExp);
			throw new ApplicationException(
					"Internal Error Occured While Processing Your Request",
					aoExp);
		}

	}

	/**
	 * This method will share
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action response object
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@ActionMapping(params = "submit_action=shareStep1")
	protected void share(ActionRequest aoRequest, ActionResponse aoResponse)
			throws ApplicationException {
		try {
			shareDocumentStepOneAction(aoRequest, aoResponse);
		} catch (Exception aoExp) {
			LOG_OBJECT.Error(
					"Exception in action file upload screen in Document Vault",
					aoExp);
			throw new ApplicationException(
					"Internal Error Occured While Processing Your Request",
					aoExp);
		}
	}

	/**
	 * This method will save properties
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action response object
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@SuppressWarnings("unchecked")
	@ActionMapping(params = "submit_action=saveProperties")
	protected void saveProperties(ActionRequest aoRequest,
			ActionResponse aoResponse) throws ApplicationException {
		try {
			FileNetOperationsUtils
					.removeMessageFromSessionAndRequest(aoRequest);
			Map<String, String> loEditDocumentMap = (Map<String, String>) BaseCacheManagerWeb
					.getInstance().getCacheObject(
							ApplicationConstants.EDIT_DOC_LIST_MAP);
			String lsUserId = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
							PortletSession.APPLICATION_SCOPE);
			if (null != loEditDocumentMap) {
				loEditDocumentMap.remove(lsUserId);
			}
			saveDocPropertiesAction(aoRequest, aoResponse);

		} catch (ApplicationException loAppEx) {
			try {
				setErrorMessageInResponse(aoRequest, aoResponse,
						aoRequest.getPortletSession(), HHSR5Constants.TRUE,
						HHSR5Constants.EMPTY_STRING, loAppEx);
			} catch (Exception aoIoExp) {
				LOG_OBJECT.Error("Exception during creating folder", aoIoExp);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION,
						ApplicationConstants.ERROR);
			}
		} catch (Exception aoExp) {
			LOG_OBJECT.Error(
					"Exception in action file upload screen in Document Vault",
					aoExp);
			throw new ApplicationException(
					"Internal Error Occured While Processing Your Request",
					aoExp);
		}
	}

	/**
	 * This method will cancel delete
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action response object
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@ActionMapping(params = "submit_action=canceldelete")
	protected void canceldelete(ActionRequest aoRequest,
			ActionResponse aoResponse) throws ApplicationException {
		try {

			FileNetOperationsUtils
					.removeMessageFromSessionAndRequest(aoRequest);
			aoRequest.getPortletSession().removeAttribute(
					HHSR5Constants.OLD_DOC_ID);
			aoRequest.getPortletSession()
					.removeAttribute(HHSR5Constants.OLD_DOC_ID,
							PortletSession.APPLICATION_SCOPE);
			// Redirecting since on click of 'No' button on delete document
			// overlay an ajax call is made rather than form submit.
			aoResponse.sendRedirect(aoRequest.getContextPath()
					+ ApplicationConstants.ERROR_HANDLER);
		} catch (Exception aoExp) {
			LOG_OBJECT.Error(
					"Exception in action file upload screen in Document Vault",
					aoExp);
			throw new ApplicationException(
					"Internal Error Occured While Processing Your Request",
					aoExp);
		}
	}

	/**
	 * This method will back request
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action response object
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@ActionMapping(params = "submit_action=backrequest")
	protected void backRequest(ActionRequest aoRequest,
			ActionResponse aoResponse) throws ApplicationException {
		try {
			FileNetOperationsUtils
					.removeMessageFromSessionAndRequest(aoRequest);
			String lsUserOrgType = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
							PortletSession.APPLICATION_SCOPE);
			goBackAction(aoRequest, aoResponse, lsUserOrgType);
			Document loDoc = (Document) aoRequest.getPortletSession()
					.getAttribute(HHSR5Constants.DOC_SESSION_BEAN);
			// Added for combo box of doctype
			List<String> loDocTypeList = (List<String>) ApplicationSession
					.getAttribute(aoRequest, true,
							HHSR5Constants.DROPDOWN_DOC_TYPE);
			ApplicationSession.setAttribute(loDocTypeList, aoRequest,
					HHSR5Constants.DROPDOWN_DOC_TYPE);
			// end
			loDoc.setHelpCategory(aoRequest
					.getParameter(ApplicationConstants.HELP_CATEGORY));
			loDoc.setHelpRadioButton(aoRequest
					.getParameter(ApplicationConstants.HELP));
			loDoc.setHelpDocDesc(aoRequest
					.getParameter(ApplicationConstants.DOCUMENT_DESCRIPTION));
			aoRequest.getPortletSession().setAttribute(
					HHSR5Constants.DOC_SESSION_BEAN, loDoc);
			ApplicationSession.setAttribute(loDoc, aoRequest,
					ApplicationConstants.SESSION_DOCUMENT_OBJ);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION,
					HHSR5Constants.DOCUMENT_UPLOAD);

		} catch (ApplicationException loAppEx) {
			LOG_OBJECT.Error("Exception in Document Vault", loAppEx);
			throw loAppEx;
		} catch (Exception aoExp) {
			LOG_OBJECT.Error(
					"Exception in action file upload screen in Document Vault",
					aoExp);
			throw new ApplicationException(
					"Internal Error Occured While Processing Your Request",
					aoExp);
		}
	}

	/**
	 * This method will cancel request
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action request object
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@ActionMapping(params = "submit_action=cancelrequest")
	protected void cancelRequest(ActionRequest aoRequest,
			ActionResponse aoResponse) throws ApplicationException {
		try {
			FileNetOperationsUtils
					.removeMessageFromSessionAndRequest(aoRequest);
			deleteTempFile(aoRequest);
		}

		catch (Exception aoExp) {
			LOG_OBJECT.Error(
					"Exception in action file upload screen in Document Vault",
					aoExp);
			throw new ApplicationException(
					"Internal Error Occured While Processing Your Request",
					aoExp);
		}
	}

	/**
	 * This method will check filters
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action response object
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@SuppressWarnings("unchecked")
	@ActionMapping(params = "submit_action=checkFilterParams")
	protected void checkFilterParams(ActionRequest aoRequest,
			ActionResponse aoResponse) throws ApplicationException {
		try {
			FileNetOperationsUtils
					.removeMessageFromSessionAndRequest(aoRequest);
			Map<String, String> loEditDocumentMap = (Map<String, String>) BaseCacheManagerWeb
					.getInstance().getCacheObject(
							ApplicationConstants.EDIT_DOC_LIST_MAP);
			String lsSessionId = aoRequest.getPortletSession().getId();
			String lsUserId = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
							PortletSession.APPLICATION_SCOPE);
			String lsLockedDocumentKey = lsSessionId
					+ ApplicationConstants.UNDERSCORE + lsUserId;
			if (null != loEditDocumentMap
					&& loEditDocumentMap.containsKey(lsLockedDocumentKey)) {
				loEditDocumentMap.remove(lsLockedDocumentKey);
			}
			synchronized (this) {
				BaseCacheManagerWeb.getInstance().putCacheObject(
						ApplicationConstants.EDIT_DOC_LIST_MAP,
						loEditDocumentMap);
			}
			Document loDocument = new Document();
			FileNetOperationsUtils.setFilterHiddenParams(aoRequest, loDocument,
					null);
			ApplicationSession.setAttribute(loDocument, aoRequest,
					ApplicationConstants.FILTER_DOCUMENT_OBJECT);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION,
					ApplicationConstants.CHECK_FILTER_FOR_RETURN_TO_VAULT);
		} catch (ApplicationException loAppEx) {
			LOG_OBJECT.Error("Exception in Document Vault", loAppEx);
			throw loAppEx;
		} catch (Exception aoExp) {
			LOG_OBJECT.Error(
					"Exception in action file upload screen in Document Vault",
					aoExp);
			throw new ApplicationException(
					"Internal Error Occured While Processing Your Request",
					aoExp);
		}
	}

	/**
	 * This method will check filters
	 * 
	 * @param aoRequest
	 *            render request object
	 * @param aoResponse
	 *            render response object
	 * @return lsformpath as jspname
	 */
	@RenderMapping(params = "render_action=checkFilterParams")
	protected ModelAndView handleRenderForCheckFilterParams(
			RenderRequest aoRequest, RenderResponse aoResponse) {
		String lsFormPath = null;
		try {
			P8UserSession loUserSession = (P8UserSession) aoRequest
					.getPortletSession().getAttribute(
							ApplicationConstants.FILENET_SESSION_OBJECT,
							PortletSession.APPLICATION_SCOPE);
			String lsUserOrg = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
							PortletSession.APPLICATION_SCOPE);
			String lsUserOrgType = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
							PortletSession.APPLICATION_SCOPE);
			String lsSortByReqParam = HHSPortalUtil.parseQueryString(aoRequest,
					ApplicationConstants.DOCUMENT_VAULT_SORT_BY_PARAMETER);

			String lsSortTypeReqParam = HHSPortalUtil.parseQueryString(
					aoRequest,
					ApplicationConstants.DOCUMENT_VAULT_SORT_TYPE_PARAMETER);
			lsFormPath = getRenderFilterForReturnToVault(loUserSession,
					aoRequest.getPortletSession(), aoRequest, lsUserOrgType,
					lsUserOrg, lsSortByReqParam, lsSortTypeReqParam);

		} catch (Exception loAppEx) {
			LOG_OBJECT.Error("Exception occured in documentUploadAction",
					loAppEx);
		}
		return new ModelAndView(lsFormPath);
	}

	/**
	 * This method will view document information
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action response object
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@ActionMapping(params = "submit_action=viewDocumentInfo")
	protected void viewDocumentInfo(ActionRequest aoRequest,
			ActionResponse aoResponse) throws ApplicationException {
		HashMap<String, String> loLockListMap = new HashMap<String, String>();
		List<String> loLockIdList = new ArrayList<String>();
		try {
			//**  start QC 8914 R7.2 read only role **/
			//[Start]R7.12.0 QC9311 Minimize Debug
			//LOG_OBJECT.Debug("in viewDocumentInfo ");
			String role_current = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_ROLE_CURRENT, PortletSession.APPLICATION_SCOPE);
			LOG_OBJECT.Debug("in viewDocumentInfo : role_current :: "+role_current);
			//[End]R7.12.0 QC9311 Minimize Debug
			//**  end QC 8914 R7.2 read only role **/
			FileNetOperationsUtils
					.removeMessageFromSessionAndRequest(aoRequest);
			String lsUserOrg = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
							PortletSession.APPLICATION_SCOPE);
			String lsDocTpye = aoRequest
					.getParameter(HHSR5Constants.DOC_TYPE_HIDDEN);
			String lsUserOrgType = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
							PortletSession.APPLICATION_SCOPE);
			if (null != lsUserOrgType && !lsUserOrgType.isEmpty()
					&& lsUserOrgType.equalsIgnoreCase(HHSR5Constants.USER_CITY)) {
				lsUserOrg = lsUserOrgType;
			}
			P8UserSession loUserSession = (P8UserSession) aoRequest
					.getPortletSession().getAttribute(
							ApplicationConstants.FILENET_SESSION_OBJECT,
							PortletSession.APPLICATION_SCOPE);
			if (CommonUtil.getConditionalRoleDisplay(
					ComponentMappingConstant.DV_S112_PAGE,
					aoRequest.getPortletSession())) {
				String lsDocumentId = aoRequest
						.getParameter(ApplicationConstants.DOCUMENT_ID);

				Channel loChannelLock = new Channel();
				loLockListMap.put(lsDocumentId, lsDocTpye);
				loChannelLock.setData("documentIdMap", loLockListMap);
				loChannelLock.setData("aoFilenetSession", loUserSession);
				loChannelLock.setData("next_action", "viewInfo");
				HHSTransactionManager.executeTransaction(loChannelLock,
						"getFolderPathFromFilenet",
						HHSR5Constants.TRANSACTION_ELEMENT_R5);
				HashMap<String, List<String>> loLockingMap = (HashMap) loChannelLock
						.getData("loPathMap");

				for (Map.Entry<String, List<String>> entry : loLockingMap
						.entrySet()) {
					List<String> loList = entry.getValue();
					
					for (Iterator iterator = loList.iterator(); iterator
							.hasNext();) {
						String lsLockPath = (String) iterator.next();
						loLockIdList.add(lsLockPath);
					}
				}
                								
				if (FileNetOperationsUtils.checkLock(loLockIdList, lsUserOrg) && !ApplicationConstants.ROLE_OBSERVER.equalsIgnoreCase(role_current)) {
					aoResponse.setRenderParameter(
							HHSR5Constants.VIEW_INFO_LOCKING_FLAG, "true");
					
				} else {
					aoResponse.setRenderParameter(
							HHSR5Constants.VIEW_INFO_LOCKING_FLAG, "false");
					
				}
				

				FileNetOperationsUtils.actionViewDocumentInfo(aoRequest,
						aoResponse);
											
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION,	ApplicationConstants.VIEW_DOCUMENT_INFO);
				
				aoResponse.setRenderParameter(ApplicationConstants.ACTION,	HHSR5Constants.VALUT_KEY);
				
				aoResponse.setRenderParameter(HHSR5Constants.EDIT_PROP_CHECK,	(String) aoRequest.getParameter(HHSR5Constants.SHARED_PAGE_ORG));
			}
		} catch (ApplicationException loAppEx) {
			try {
				setErrorMessageInResponse(aoRequest, aoResponse,
						aoRequest.getPortletSession(),
						HHSR5Constants.EMPTY_STRING,
						HHSR5Constants.EMPTY_STRING, loAppEx);
				aoResponse.setRenderParameter(HHSR5Constants.HEADER_CLICK,
						"true");
			} catch (IOException aoIoExp) {
				LOG_OBJECT
						.Error("Exception Occured while viewing document information.",
								aoIoExp);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION,
						ApplicationConstants.ERROR);
			}
		} catch (Exception aoExp) {
			LOG_OBJECT.Error(
					"Exception Occured while viewing document information.",
					aoExp);
			throw new ApplicationException(
					"Internal Error Occured While Processing Your Request",
					aoExp);
		}
	}

	/**
	 * This method will view document information
	 * 
	 * @param aoRequest
	 *            render request object
	 * @param aoResponse
	 *            render response object
	 * @return ls formpath as jspname
	 */
	@RenderMapping(params = "render_action=viewDocumentInfo")
	protected ModelAndView handleRenderForViewDocumentInfo(
			RenderRequest aoRequest, RenderResponse aoResponse) {
		String lsFormPath = null;
		try {
			//[Start]R7.12.0 QC9311 Minimize Debug
			//LOG_OBJECT.Debug("in handleRenderForViewDocumentInfo");
			//[End]R7.12.0 QC9311 Minimize Debug
			
			lsFormPath = getRenderViewDocument(aoRequest);
			aoRequest.setAttribute(HHSR5Constants.EDIT_PROP_CHECK,	aoRequest.getParameter(HHSR5Constants.EDIT_PROP_CHECK));
			aoRequest.setAttribute(HHSR5Constants.VIEW_INFO_LOCKING_FLAG, aoRequest.getParameter(HHSR5Constants.VIEW_INFO_LOCKING_FLAG));
			aoRequest.setAttribute(ApplicationConstants.EDIT_VERSION_PROP, aoRequest.getParameter(ApplicationConstants.EDIT_VERSION_PROP));
		
		} catch (Exception loAppEx) {
			LOG_OBJECT.Error(
					"Exception Occured while viewing document information.",
					loAppEx);
		}
		return new ModelAndView(lsFormPath);
	}

	/**
	 * This method will get folder location
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action response object
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@ActionMapping(params = "submit_action=getFolderLocation")
	protected void getFolderLocation(ActionRequest aoRequest,
			ActionResponse aoResponse) throws ApplicationException {
		Document loDocument = new Document();
		Map<String, Object> loPropertyMapInfo = new HashMap<String, Object>();
		try {
			FileNetOperationsUtils
					.removeMessageFromSessionAndRequest(aoRequest);
			loDocument = (Document) ApplicationSession.getAttribute(aoRequest,
					ApplicationConstants.SESSION_DOCUMENT_OBJ);
			if (null == loDocument) {
				loDocument = (Document) aoRequest.getPortletSession()
						.getAttribute(HHSR5Constants.DOC_SESSION_BEAN);
			}
			loDocument.setHelpCategory(aoRequest
					.getParameter(ApplicationConstants.HELP_CATEGORY));
			loDocument.setHelpRadioButton(aoRequest
					.getParameter(ApplicationConstants.HELP));
			loDocument.setHelpDocDesc(aoRequest
					.getParameter(ApplicationConstants.DOCUMENT_DESCRIPTION));
			List<DocumentPropertiesBean> loDocumentPropsBeans = loDocument
					.getDocumentProperties();
			Iterator<DocumentPropertiesBean> loDocPropsIt = loDocumentPropsBeans
					.iterator();
			while (loDocPropsIt.hasNext()) {
				DocumentPropertiesBean loDocProps = loDocPropsIt.next();
				if (ApplicationConstants.PROPERTY_TYPE_BOOLEAN
						.equalsIgnoreCase(loDocProps.getPropertyType())) {
					if (HHSR5Constants.ON.equalsIgnoreCase(aoRequest
							.getParameter(loDocProps.getPropertyId()))) // request.getParameter("accidentalCover").equals("checked"))
					{
						loPropertyMapInfo.put(loDocProps.getPropertyId(), true);
					} else {
						loPropertyMapInfo
								.put(loDocProps.getPropertyId(), false);
					}
				} else {
					loPropertyMapInfo.put(loDocProps.getPropertyId(),
							aoRequest.getParameter(loDocProps.getPropertyId()));
					loDocProps.setPropValue(aoRequest.getParameter(loDocProps
							.getPropertyId()));
				}
			}
			FileNetOperationsUtils.validatorForUpload(
					(HashMap) loPropertyMapInfo, loDocument.getDocType());
			aoRequest.getPortletSession().setAttribute(
					HHSR5Constants.DOC_SESSION_BEAN, loDocument);
			aoResponse.setRenderParameter(ApplicationConstants.ACTION,
					HHSR5Constants.DOCUMENT_VALUT_KEY);
			ApplicationSession.setAttribute(loDocument, aoRequest,
					ApplicationConstants.SESSION_DOCUMENT_OBJ);
			aoRequest.getPortletSession().setAttribute(
					HHSR5Constants.PROPERTY_MAP_INFO, loPropertyMapInfo,
					PortletSession.APPLICATION_SCOPE);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION,
					HHSR5Constants.CREATE_TREE);
		} catch (ApplicationException aoExp) {
			try {
				setErrorMessageInResponse(aoRequest, aoResponse,
						aoRequest.getPortletSession(), HHSR5Constants.TRUE,
						HHSR5Constants.EMPTY_STRING, aoExp);
			} catch (IOException aoIoExp) {
				LOG_OBJECT.Error("IOException during file upload", aoIoExp);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION,
						ApplicationConstants.ERROR);
			}
		}
	}

	/**
	 * This method will upload document into vault
	 * 
	 * @param aoRequest
	 *            render request object
	 * @param aoResponse
	 *            render response object
	 * @return formpath as document location
	 */
	@RenderMapping(params = "render_action=treeCreation")
	protected String documentupload(RenderRequest aoRequest,
			RenderResponse aoResponse) {
		String lsFormPath = null;
		Document loDocument = (Document) aoRequest.getPortletSession()
				.getAttribute(HHSR5Constants.DOC_SESSION_BEAN);
		ApplicationSession.setAttribute(loDocument, aoRequest,
				ApplicationConstants.SESSION_DOCUMENT_OBJ);
		aoRequest.setAttribute(
				ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_PARAMETER,
				loDocument);
		lsFormPath = HHSR5Constants.DOC_LOC;
		return lsFormPath;
	}

	/**
	 * This method will restore documents and folders from recycle bin
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action response object
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@ActionMapping(params = "submit_action=restore")
	protected void restoreFromRecycleBin(ActionRequest aoRequest,
			ActionResponse aoResponse) throws ApplicationException {
		HashMap<String, String> loRequiredMap = new HashMap<String, String>();
		List<String> loLockList = new ArrayList<String>();
		List<Document> loEntityList = new ArrayList<Document>();
		Channel loChannel = new Channel();
		HashMap<String, String> loLockListMap = new HashMap<String, String>();
		Boolean lbLockFlag = true;
		try {
			FileNetOperationsUtils
					.removeMessageFromSessionAndRequest(aoRequest);
			String lsMenuOptionsFlag = aoRequest.getParameter("menuFlag");
			Map<String, String[]> loMap = aoRequest.getParameterMap();
			String[] loChekedItems = loMap
					.get(ApplicationConstants.CHECKED_OBJECT);
			P8UserSession loUserSession = (P8UserSession) aoRequest
					.getPortletSession().getAttribute(
							ApplicationConstants.FILENET_SESSION_OBJECT,
							PortletSession.APPLICATION_SCOPE);
			String lsUserOrg = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
							PortletSession.APPLICATION_SCOPE);
			String lsUserOrgType = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
							PortletSession.APPLICATION_SCOPE);
			if (null != lsUserOrgType
					&& lsUserOrgType.equalsIgnoreCase(HHSR5Constants.USER_CITY)) {
				lsUserOrg = lsUserOrgType;
			}
			String lsUserId = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
							PortletSession.APPLICATION_SCOPE);
			loRequiredMap.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY,
					lsUserId);
			loRequiredMap.put(HHSR5Constants.ORGANIZATION_ID, lsUserOrg);
			loRequiredMap.put(HHSR5Constants.ORGANIZATION_TYPE, lsUserOrgType);

			if (null != lsMenuOptionsFlag
					&& lsMenuOptionsFlag.equalsIgnoreCase("true")) {
				for (int i = 0; i < loChekedItems.length; i++) {
					Document loDocument = new Document();
					String lsCheckValue = loChekedItems[i];
					String[] loTempArray = lsCheckValue
							.split(HHSR5Constants.COMMA);
					loDocument.setDocumentId(loTempArray[0]);
					loDocument.setDocType(loTempArray[1]);
					loEntityList.add(loDocument);
					loLockListMap.put(loDocument.getDocumentId(),
							loDocument.getDocType());

				}
			} else {

				Document loDocument = new Document();
				loDocument.setDocumentId((String) aoRequest
						.getParameter(HHSR5Constants.CHECKED_DOC_ID));
				loDocument.setDocType((String) aoRequest
						.getParameter(HHSR5Constants.CHECKED_DOC_TYPE));
				loEntityList.add(loDocument);
				loLockListMap.put(loDocument.getDocumentId(),
						loDocument.getDocType());

			}

			String lsRecycleBinId = aoRequest
					.getParameter(HHSR5Constants.RECYCLE_ID);
			loLockListMap.put(lsRecycleBinId, HHSR5Constants.EMPTY_STRING);

			Channel loChannelLock = new Channel();

			loChannelLock.setData("documentIdMap", loLockListMap);
			loChannelLock.setData("aoFilenetSession", loUserSession);
			loChannelLock.setData("next_action", "restore");
			HHSTransactionManager.executeTransaction(loChannelLock,
					"getFolderPathFromFilenet",
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			HashMap<String, List<String>> loLockingMap = (HashMap) loChannelLock
					.getData("loPathMap");

			for (Map.Entry<String, List<String>> entry : loLockingMap
					.entrySet()) {
				List<String> loList = entry.getValue();
				for (Iterator iterator = loList.iterator(); iterator.hasNext();) {
					String lsLockPath = (String) iterator.next();
					loLockList.add(lsLockPath);
				}
			}
			List<String> loLockedByList = FileNetOperationsUtils
					.getLockDetails(loLockList, lsUserOrg);
			if (loLockedByList.size() > 0) {
				lbLockFlag = false;
				if (FileNetOperationsUtils
						.checkIfLockedByRecycleBin(loLockedByList)) {
					aoResponse.setRenderParameter(
							ApplicationConstants.ERROR_MESSAGE,
							PropertyLoader.getProperty(
									P8Constants.ERROR_PROPERTY_FILE,
									HHSR5Constants.LOCK_MESSAGE_RESTORE));
					aoResponse.setRenderParameter(
							ApplicationConstants.ERROR_MESSAGE_TYPE,
							ApplicationConstants.MESSAGE_FAIL_TYPE);
				} else {
					aoResponse
							.setRenderParameter(
									ApplicationConstants.ERROR_MESSAGE,
									PropertyLoader
											.getProperty(
													P8Constants.ERROR_PROPERTY_FILE,
													HHSR5Constants.LOCK_MESSAGE_RESTORE_CUSTOM_SOURCE));
					aoResponse.setRenderParameter(
							ApplicationConstants.ERROR_MESSAGE_TYPE,
							ApplicationConstants.MESSAGE_FAIL_TYPE);
				}
			} else {
				FileNetOperationsUtils.addLock(loLockList, aoRequest);
				List<String> lsRestoreList = new ArrayList<String>();
				lsRestoreList.add("Restore Folder");
				lsRestoreList.add("Restore File");
				loChannel.setData(HHSR5Constants.AUDIT_ENTITY_NAME,
						lsRestoreList);
				loChannel
						.setData(HHSR5Constants.AO_USER_SESSION, loUserSession);
				loChannel.setData(HHSR5Constants.REQUIRED_MAP, loRequiredMap);
				loChannel.setData(HHSR5Constants.DELETE_LIST_ITEMS,
						loEntityList);
				DocumentVaultParallelProcessor loDVPP = new DocumentVaultParallelProcessor(
						loChannel, loLockList, lsUserOrg, lsUserOrgType,
						HHSR5Constants.RESTORE_DATA_FILENET);
				Thread loThread = new Thread(loDVPP);
				loThread.start();
				aoRequest.getPortletSession().setAttribute(
						"currentProcessThreadObject", loDVPP);
			}
			aoResponse.setRenderParameter(HHSR5Constants.JSP_NAME,
					HHSR5Constants.RECYCLE_BIN_JSP_NAME);
		} catch (ApplicationException loAppEx) {
			try {
				setErrorMessageInResponse(aoRequest, aoResponse,
						aoRequest.getPortletSession(), HHSR5Constants.TRUE,
						HHSR5Constants.EMPTY_STRING, loAppEx);
			} catch (IOException aoIoExp) {
				LOG_OBJECT.Error("IOException during file upload", aoIoExp);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION,
						ApplicationConstants.ERROR);
			}
		} catch (Exception aoExp) {
			LOG_OBJECT.Error(
					"Exception in action file upload screen in Document Vault",
					aoExp);
		}
	}

	/**
	 * This method will move document or folders
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action response object
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@ActionMapping(params = "submit_action=move")
	protected void move(ActionRequest aoRequest, ActionResponse aoResponse)
			throws ApplicationException {
		HashMap<String, String> loRequiredMap = new HashMap<String, String>();
		HashMap<String, String> loLockListMap = new HashMap<String, String>();
		String lsMenuOptionsFlag = aoRequest.getParameter("menuFlag");
		List<Document> loEntityList = new ArrayList<Document>();
		String lsSelectedFolderId = aoRequest
				.getParameter(HHSR5Constants.SELECTED_FOLDER_ID_FOR_MOVE);
		Map<String, String[]> loMap = aoRequest.getParameterMap();
		String[] loChekedItems = loMap.get(ApplicationConstants.CHECKED_OBJECT);
		P8UserSession loUserSession = (P8UserSession) aoRequest
				.getPortletSession().getAttribute(
						ApplicationConstants.FILENET_SESSION_OBJECT,
						PortletSession.APPLICATION_SCOPE);
		String lsPresentFolderId = (String) aoRequest
				.getParameter(HHSR5Constants.PRESENT_FOLDER_ID);
		List<String> loLockIdList = new ArrayList<String>();
		try {
			FileNetOperationsUtils
					.removeMessageFromSessionAndRequest(aoRequest);
			String lsUserOrg = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
							PortletSession.APPLICATION_SCOPE);
			String lsUserOrgType = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
							PortletSession.APPLICATION_SCOPE);
			if (null != lsUserOrgType
					&& lsUserOrgType.equalsIgnoreCase(HHSR5Constants.USER_CITY)) {
				lsUserOrg = lsUserOrgType;
			}
			String lsUserId = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
							PortletSession.APPLICATION_SCOPE);
			loRequiredMap.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY,
					lsUserId);
			loRequiredMap.put(HHSR5Constants.ORGANIZATION_ID, lsUserOrg);
			loRequiredMap.put(HHSConstants.LS_USER_ORG_TYPE, lsUserOrgType);
			loRequiredMap.put(HHSR5Constants.ORG_ID, lsUserOrg);

			if (StringUtils.isNotBlank(lsPresentFolderId)) {
				if (null != lsMenuOptionsFlag
						&& lsMenuOptionsFlag.equalsIgnoreCase("true")) {
					for (int i = 0; i < loChekedItems.length; i++) {
						Document loDocument = new Document();
						String lsCheckValue = loChekedItems[i];
						String[] loTempArray = lsCheckValue
								.split(HHSR5Constants.COMMA);
						loDocument.setDocumentId(loTempArray[0]);
						loDocument.setDocType(loTempArray[1]);
						// Add parent id of document

						loLockListMap.put(loDocument.getDocumentId(),
								loDocument.getDocType());

						loEntityList.add(loDocument);

					}
				} else {
					Document loDocument = new Document();
					loDocument.setDocumentId((String) aoRequest
							.getParameter(HHSR5Constants.CHECKED_DOC_ID));
					loDocument.setDocType((String) aoRequest
							.getParameter(HHSR5Constants.CHECKED_DOC_TYPE));
					// Add parent id of document
					loLockListMap.put(loDocument.getDocumentId(),
							loDocument.getDocType());

					loEntityList.add(loDocument);
				}

				if (StringUtils.isNotBlank(lsSelectedFolderId)) {
					loLockListMap.put(lsSelectedFolderId, "");
					LOG_OBJECT.Info("Channel Object in move::::::::::"
							+ loLockListMap);
					Channel loChannelLock = new Channel();
					loChannelLock.setData("documentIdMap", loLockListMap);
					loChannelLock.setData("aoFilenetSession", loUserSession);
					loChannelLock.setData("next_action", "move");
					HHSTransactionManager.executeTransaction(loChannelLock,
							"getFolderPathFromFilenet",
							HHSR5Constants.TRANSACTION_ELEMENT_R5);
					HashMap<String, List<String>> loLockingMap = (HashMap) loChannelLock
							.getData("loPathMap");

					for (Map.Entry<String, List<String>> entry : loLockingMap
							.entrySet()) {
						List<String> loList = entry.getValue();
						for (Iterator iterator = loList.iterator(); iterator
								.hasNext();) {
							String lsLockPath = (String) iterator.next();
							loLockIdList.add(lsLockPath);
						}
					}

					if (FileNetOperationsUtils.checkLock(loLockIdList,
							lsUserOrg)) {
						aoResponse.setRenderParameter(
								ApplicationConstants.ERROR_MESSAGE,
								PropertyLoader.getProperty(
										P8Constants.ERROR_PROPERTY_FILE,
										HHSR5Constants.LOCK_MESSAGE_MOVE));
						aoResponse.setRenderParameter(
								ApplicationConstants.ERROR_MESSAGE_TYPE,
								ApplicationConstants.MESSAGE_FAIL_TYPE);
					} else {
						FileNetOperationsUtils.addLock(loLockIdList, aoRequest);
						Channel loChannel = new Channel();
						List<String> lsMoveOpsList = new ArrayList<String>();
						lsMoveOpsList.add(HHSR5Constants.MOVE_FOLDER);
						lsMoveOpsList.add(HHSR5Constants.MOVE_FILE);
						loChannel.setData(HHSR5Constants.AO_USER_SESSION,
								loUserSession);
						loChannel.setData(HHSR5Constants.MOVE_FROM,
								lsPresentFolderId);
						loChannel.setData(HHSR5Constants.MOVE_TO,
								lsSelectedFolderId);
						loChannel.setData(HHSR5Constants.MOVE_LIST_ITEMS,
								loEntityList);
						loChannel.setData(HHSR5Constants.REQUIRED_MAP,
								loRequiredMap);
						loChannel.setData(HHSR5Constants.AUDIT_ENTITY_NAME,
								lsMoveOpsList);
						DocumentVaultParallelProcessor loDVPP = new DocumentVaultParallelProcessor(
								loChannel, loLockIdList, lsUserOrg,
								lsUserOrgType,
								HHSR5Constants.MOVE_FOLDER_FILENET);
						Thread loThread = new Thread(loDVPP);
						loThread.start();
						aoRequest.getPortletSession().setAttribute(
								"currentProcessThreadObject", loDVPP);
					}
				} else {
					throw new ApplicationException(
							"Internal Error Occured While Processing Your Request");
				}
			} else {
				throw new ApplicationException(
						"Internal Error Occured While Processing Your Request");
			}

		} catch (ApplicationException loAppEx) {
			try {
				setErrorMessageInResponse(aoRequest, aoResponse,
						aoRequest.getPortletSession(), HHSR5Constants.TRUE,
						HHSR5Constants.EMPTY_STRING, loAppEx);
			} catch (IOException aoIoExp) {
				LOG_OBJECT.Error("IOException during file upload", aoIoExp);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION,
						ApplicationConstants.ERROR);
			}
		} catch (Exception aoExp) {
			LOG_OBJECT.Error("Exception in Creating folder Document Vault",
					aoExp);
			throw new ApplicationException(
					"Internal Error Occured While Processing Your Request",
					aoExp);
		}

	}

	/**
	 * This method will handle action request for deletion of entry in From
	 * Document Vault screen to RecycleBin
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action response object
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@ActionMapping(params = "submit_action=delete")
	protected void deleteToRecycleBin(ActionRequest aoRequest,
			ActionResponse aoResponse) throws ApplicationException {
		FileNetOperationsUtils.removeMessageFromSessionAndRequest(aoRequest);
		HashMap<String, String> loRequiredMap = new HashMap<String, String>();
		HashMap<String, String> loLockListMap = new HashMap<String, String>();
		List<Document> loEntityList = new ArrayList<Document>();
		Channel loChannel = new Channel();
		Map<String, String[]> loMap = aoRequest.getParameterMap();
		String lsMenuOptionsFlag = aoRequest.getParameter("menuFlag");
		String[] loChekedItems = loMap.get(ApplicationConstants.CHECKED_OBJECT);
		P8UserSession loUserSession = (P8UserSession) aoRequest
				.getPortletSession().getAttribute(
						ApplicationConstants.FILENET_SESSION_OBJECT,
						PortletSession.APPLICATION_SCOPE);
		List<String> loLockIdList = new ArrayList<String>();
		try {
			String lsPresentFolderId = aoRequest
					.getParameter("presentFolderId");
			loChannel.setData("presentFolderId", lsPresentFolderId);
			String lsUserOrg = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
							PortletSession.APPLICATION_SCOPE);
			String lsUserOrgType = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
							PortletSession.APPLICATION_SCOPE);
			if (null != lsUserOrgType
					&& lsUserOrgType.equalsIgnoreCase(HHSR5Constants.USER_CITY)) {
				lsUserOrg = lsUserOrgType;
			}
			String lsUserId = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
							PortletSession.APPLICATION_SCOPE);
			loRequiredMap.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY,
					lsUserId);
			loRequiredMap.put(HHSR5Constants.ORGANIZATION_ID, lsUserOrg);
			loRequiredMap.put(HHSR5Constants.ORGANIZATION_TYPE, lsUserOrgType);

			if (null != lsMenuOptionsFlag
					&& lsMenuOptionsFlag.equalsIgnoreCase("true")) {
				for (int i = 0; i < loChekedItems.length; i++) {
					Document loDocument = new Document();
					String lsCheckValue = loChekedItems[i];
					String[] loTempArray = lsCheckValue
							.split(HHSR5Constants.COMMA);
					loDocument.setDocumentId(loTempArray[0]);
					loDocument.setDocType(loTempArray[1]);
					loLockListMap.put(loDocument.getDocumentId(),
							loDocument.getDocType());
					loEntityList.add(loDocument);
				}
			} else {
				Document loDocument = new Document();
				loDocument.setDocumentId((String) aoRequest
						.getParameter(HHSR5Constants.CHECKED_DOC_ID));
				loDocument.setDocType((String) aoRequest
						.getParameter(HHSR5Constants.CHECKED_DOC_TYPE));
				loLockListMap.put(loDocument.getDocumentId(),
						loDocument.getDocType());
				loEntityList.add(loDocument);
			}
			Channel loChannelLock = new Channel();
			loChannelLock.setData("documentIdMap", loLockListMap);
			loChannelLock.setData("aoFilenetSession", loUserSession);
			loChannelLock.setData("next_action", "delete");
			HHSTransactionManager.executeTransaction(loChannelLock,
					"getFolderPathFromFilenet",
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			HashMap<String, List<String>> loLockingMap = (HashMap) loChannelLock
					.getData("loPathMap");

			for (Map.Entry<String, List<String>> entry : loLockingMap
					.entrySet()) {
				List<String> loList = entry.getValue();
				for (Iterator iterator = loList.iterator(); iterator.hasNext();) {
					String lsLockPath = (String) iterator.next();
					loLockIdList.add(lsLockPath);
				}
			}

			if (FileNetOperationsUtils.checkLock(loLockIdList, lsUserOrg)) {
				aoResponse.setRenderParameter(
						ApplicationConstants.ERROR_MESSAGE, PropertyLoader
								.getProperty(P8Constants.ERROR_PROPERTY_FILE,
										HHSR5Constants.DELETE_TO_RECYCLE_BIN));
				aoResponse.setRenderParameter(
						ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE);
			} else {
				FileNetOperationsUtils.addLock(loLockIdList, aoRequest);
				List<String> lsDeleteOpsList = new ArrayList<String>();
				lsDeleteOpsList.add(HHSR5Constants.DELETE_FOL);
				lsDeleteOpsList.add(HHSR5Constants.DELETE_FILE);
				loChannel
						.setData(HHSR5Constants.AO_USER_SESSION, loUserSession);
				loChannel.setData(HHSR5Constants.REQUIRED_MAP, loRequiredMap);
				// paramter added for multi-select delete 4.0.2.0
				loChannel.setData(HHSR5Constants.ACTION, HHSR5Constants.DELETE);
				// paramter added for multi-select delete 4.0.2.0
				loChannel.setData(HHSR5Constants.AUDIT_ENTITY_NAME,
						lsDeleteOpsList);
				loChannel.setData(HHSR5Constants.DELETE_LIST_ITEMS,
						loEntityList);
				DocumentVaultParallelProcessor loDVPP = new DocumentVaultParallelProcessor(
						loChannel, loLockIdList, lsUserOrg, lsUserOrgType,
						HHSR5Constants.DELETE_FOLDER_FILENET);
				Thread loThread = new Thread(loDVPP);
				loThread.start();
				aoRequest.getPortletSession().setAttribute(
						"currentProcessThreadObject", loDVPP);
			}
		} catch (ApplicationException loAppEx) {
			try {
				setErrorMessageInResponse(aoRequest, aoResponse,
						aoRequest.getPortletSession(), HHSR5Constants.TRUE,
						HHSR5Constants.EMPTY_STRING, loAppEx);
			} catch (IOException aoIoExp) {
				LOG_OBJECT.Error("IOException during File Deletion", aoIoExp);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION,
						ApplicationConstants.ERROR);
			}
		} catch (Exception aoExp) {
			LOG_OBJECT.Error("Exception during File Deletion", aoExp);
			throw new ApplicationException(
					"Internal Error Occured While Processing Your Request",
					aoExp);
		}
	}

	/**
	 * This method will be executed when a user click on search button of
	 * enhanced document vault
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action response object
	 * @throws IOException
	 *             when IO exception occurs
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@ActionMapping(params = "submit_action=filterdocuments")
	protected void searchDoc(ActionRequest aoRequest, ActionResponse aoResponse)
			throws ApplicationException {

		try {
			FileNetOperationsUtils
					.removeMessageFromSessionAndRequest(aoRequest);
			String lsSearchButtonCliked = aoRequest
					.getParameter("clickedSearch");
			if (null != aoRequest
					.getParameter(HHSR5Constants.SEARCH_MESSAGE_FLAG)
					&& !aoRequest.getParameter(
							HHSR5Constants.SEARCH_MESSAGE_FLAG).isEmpty()) {
				aoResponse.setRenderParameter(
						HHSR5Constants.SEARCH_MESSAGE_FLAG, "true");
			}
			aoRequest.getPortletSession().setAttribute(
					HHSR5Constants.CLICKED_SEARCH, lsSearchButtonCliked,
					PortletSession.PORTLET_SCOPE);
			FileNetOperationsUtils.actionFilterDocument(aoRequest, aoResponse);
			aoRequest.getPortletSession().removeAttribute(
					HHSR5Constants.CITY_USER_SEARCH_PROVIDER_ID_NEW);
			String lsJspName = aoRequest.getParameter(HHSR5Constants.JSP_NAME);
			String lsSharedFlag = aoRequest
					.getParameter(ApplicationConstants.SHARED_FLAG);
			StringBuffer loAgencyName = new StringBuffer();
			if (null != HHSPortalUtil
					.parseQueryString(aoRequest, "treeOrgType")
					&& !HHSPortalUtil
							.parseQueryString(aoRequest, "treeOrgType")
							.isEmpty()) {
				aoResponse.setRenderParameter("treeOrgType", HHSPortalUtil
						.parseQueryString(aoRequest, "treeOrgType"));
			}

			if (null != lsSharedFlag && !lsSharedFlag.isEmpty()) {
				aoResponse.setRenderParameter(ApplicationConstants.SHARED_FLAG,
						lsSharedFlag);
				String[] loAgencyList = aoRequest
						.getParameterValues(HHSR5Constants.AGENCY_CHECK_BOX);
				if (null != loAgencyList && loAgencyList.length > 0) {
					for (int i = 0; i < loAgencyList.length; i++) {
						loAgencyName.append(loAgencyList[i]);
						loAgencyName.append(ApplicationConstants.TILD);
					}
				}
				if (null != aoRequest.getParameter(HHSR5Constants.DOC_NAME)) {
					aoResponse.setRenderParameter(HHSR5Constants.DOCUMENT_NAME,
							aoRequest.getParameter(HHSR5Constants.DOC_NAME));
				}

				if (null != aoRequest
						.getParameter(HHSR5Constants.DOC_TYPE_CITY)) {
					aoResponse
							.setRenderParameter(
									P8Constants.PROPERTY_PE_UPLOADED_DOC_TYPE,
									aoRequest
											.getParameter(HHSR5Constants.DOC_TYPE_CITY));
				}
				if (null != aoRequest
						.getParameter(HHSR5Constants.MODIFIED_FROM_5)) {
					aoResponse
							.setRenderParameter(
									HHSR5Constants.MODIFIED_FROM,
									aoRequest
											.getParameter(HHSR5Constants.MODIFIED_FROM_5));
				}
				if (null != aoRequest
						.getParameter(HHSR5Constants.MODIFIED_TO_5)) {
					aoResponse
							.setRenderParameter(
									HHSR5Constants.MODIFIED_DATE_TO,
									aoRequest
											.getParameter(HHSR5Constants.MODIFIED_TO_5));
				}

				aoResponse.setRenderParameter(HHSR5Constants.SHARED_WITH,
						loAgencyName.toString());
			}
			if (null != lsJspName && !lsJspName.isEmpty()
					&& !lsJspName.equalsIgnoreCase(HHSConstants.UNDEFINED)) {
				aoResponse.setRenderParameter(HHSR5Constants.JSP_NAME,
						lsJspName);
			} else {
				aoResponse.setRenderParameter(HHSR5Constants.JSP_NAME,
						HHSR5Constants.PROV_DOCUMENT_LIST_PAGE);
			}
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION,
					ApplicationConstants.FILTER_DOCUMENTS);
			aoResponse.setRenderParameter(ApplicationConstants.RENDER_ACTION,
					HHSR5Constants.VALUT_KEY);

		} catch (ApplicationException loAppEx) {
			LOG_OBJECT.Error("Exception in Document Vault", loAppEx);
			try {
				setErrorMessageInResponse(aoRequest, aoResponse,
						aoRequest.getPortletSession(),
						ApplicationConstants.FALSE,
						ApplicationConstants.DOCUMENT_EXCEPTION, loAppEx);
			} catch (IOException aoIoExp) {
				LOG_OBJECT.Error("IOException in searchig documents", aoIoExp);
			}
		} catch (Exception aoExp) {
			try {
				LOG_OBJECT.Error(
						"Exception in searching file in Document Vault", aoExp);
				ApplicationException loAppEx = new ApplicationException(
						aoExp.getMessage(), aoExp);
				setErrorMessageInResponse(aoRequest, aoResponse,
						aoRequest.getPortletSession(),
						ApplicationConstants.FALSE,
						ApplicationConstants.DOCUMENT_EXCEPTION, loAppEx);
			} catch (IOException aoIoExp) {
				LOG_OBJECT.Error("IOException in searchig documents", aoIoExp);
			}
		}
	}

	/**
	 * This method will filter documents
	 * 
	 * @param aoRequest
	 *            a render requets object
	 * @param aoResponse
	 *            a render response object
	 * @return jspname as lsformpath
	 */
	@RenderMapping(params = "render_action=filterDocuments")
	protected ModelAndView filterDocuments(RenderRequest aoRequest,
			RenderResponse aoResponse) {
		String lsFormPath = null;
		lsFormPath = aoRequest.getParameter(HHSR5Constants.JSP_NAME);
		aoRequest.setAttribute(ApplicationConstants.SHARED_FLAG,
				aoRequest.getParameter(ApplicationConstants.SHARED_FLAG));
		aoRequest.setAttribute(HHSR5Constants.CLICKED_SEARCH,
				aoRequest.getParameter(HHSR5Constants.CLICKED_SEARCH));
		aoRequest.setAttribute(HHSR5Constants.SEARCH_MESSAGE_FLAG,
				aoRequest.getParameter(HHSR5Constants.SEARCH_MESSAGE_FLAG));
		if (null != lsFormPath
				&& !lsFormPath.isEmpty()
				&& lsFormPath
						.equalsIgnoreCase(HHSR5Constants.SHARED_SEARCH_AGENCY)) {
			aoRequest.setAttribute(HHSConstants.DOCUMENT_NAME,
					aoRequest.getParameter(HHSConstants.DOCUMENT_NAME));
			aoRequest.setAttribute(HHSR5Constants.DOC_TYPE_CITY, aoRequest
					.getParameter(P8Constants.PROPERTY_PE_UPLOADED_DOC_TYPE));
			aoRequest.setAttribute(HHSR5Constants.MODIFIED_DATE_FROM,
					aoRequest.getParameter(HHSR5Constants.MODIFIED_DATE_FROM));
			aoRequest.setAttribute(HHSR5Constants.MODIFIED_DATE_TO,
					aoRequest.getParameter(HHSR5Constants.MODIFIED_DATE_TO));
			aoRequest.setAttribute(HHSR5Constants.SHARED_WITH,
					aoRequest.getParameter(HHSR5Constants.SHARED_WITH));
			aoRequest.setAttribute(HHSR5Constants.CLICKED_SEARCH,
					aoRequest.getParameter(HHSR5Constants.CLICKED_SEARCH));

		}
		String lsOrgType = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_TYPE,
				PortletSession.APPLICATION_SCOPE);
		String lstreeOrgType = aoRequest.getParameter("treeOrgType");
		if (null != lstreeOrgType && null != lsOrgType
				&& !lsOrgType.equalsIgnoreCase(lstreeOrgType)
				&& !lsOrgType.equalsIgnoreCase(HHSR5Constants.USER_CITY)) {
			aoRequest.getPortletSession().setAttribute(
					HHSR5Constants.HOME_PAGE_MANAGE_ORG_FLAG, true,
					PortletSession.APPLICATION_SCOPE);
		} else {
			if (null != aoRequest
					.getParameter(ApplicationConstants.SHARED_FLAG)
					&& !aoRequest
							.getParameter(ApplicationConstants.SHARED_FLAG)
							.isEmpty()
					&& aoRequest.getParameter(ApplicationConstants.SHARED_FLAG)
							.equalsIgnoreCase("true")) {
				aoRequest.getPortletSession().setAttribute(
						HHSR5Constants.HOME_PAGE_MANAGE_ORG_FLAG, true,
						PortletSession.APPLICATION_SCOPE);
			} else {
				aoRequest.getPortletSession().setAttribute(
						HHSR5Constants.HOME_PAGE_MANAGE_ORG_FLAG, false,
						PortletSession.APPLICATION_SCOPE);
			}

		}
		return new ModelAndView(lsFormPath);
	}

	/**
	 * This method will be executed when a user click on share button present on
	 * file option button after selection documents/folders from the main screen
	 * of enhanced document vault
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action response object
	 * @throws IOException
	 *             when IO exception occurs
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@ActionMapping(params = "submit_action=shareDocumentStep1")
	protected void shareDocumentStep1(ActionRequest aoRequest,
			ActionResponse aoResponse) throws ApplicationException {

		try {
			FileNetOperationsUtils
					.removeMessageFromSessionAndRequest(aoRequest);
			aoRequest.getPortletSession().removeAttribute(
					ApplicationConstants.PROVIDER_AGENCY_LIST);
			if (CommonUtil.getConditionalRoleDisplay(
					ComponentMappingConstant.DV_S112_PAGE,
					aoRequest.getPortletSession())) {

				shareDocumentStepOneAction(aoRequest, aoResponse);
			}
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION,
					ApplicationConstants.SHARE_DOCUMENT_STEP1);
		} catch (ApplicationException aoExp) {
			LOG_OBJECT.Error(
					"Exception in Step 1 sharing screen in Document Vault",
					aoExp);
			try {
				setErrorMessageInResponse(aoRequest, aoResponse,
						aoRequest.getPortletSession(), HHSR5Constants.TRUE,
						HHSR5Constants.EMPTY_STRING, aoExp);
			} catch (IOException e) {
				LOG_OBJECT.Error(
						"Exception in Step 1 sharing screen in Document Vault",
						e);
			}
		} catch (Exception aoExp) {
			LOG_OBJECT.Error(
					"Exception in Step 1 sharing screen in Document Vault",
					aoExp);
		}

	}

	/**
	 * This method will share document
	 * 
	 * @param aoRequest
	 *            a render request object
	 * @param aoResponse
	 *            a render response object
	 * @return jspname as lsformpath
	 */
	@RenderMapping(params = "render_action=shareDocumentStep1")
	protected ModelAndView handleRenderForShareDocumentStep1(
			RenderRequest aoRequest, RenderResponse aoResponse) {
		String lsFormPath = null;
		try {
			lsFormPath = getRenderShareDocumentStep1(aoRequest);
		} catch (Exception loAppEx) {
			LOG_OBJECT.Error("Exception occured in documentUploadAction",
					loAppEx);
		}
		return new ModelAndView(lsFormPath);
	}

	/**
	 * This method will be executed when a user click on next button from the
	 * first screen of document/folder sharing
	 * 
	 * @param aoResourceRequest
	 *            resource request object
	 * @param aoResourceResponse
	 *            resource response object
	 * @return ModelAndView object
	 * @throws IOException
	 *             when IO exception occurs
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@ResourceMapping("fromStep1Sharing")
	protected ModelAndView handleResourceForStep1Sharing(
			ResourceRequest aoResourceRequest,
			ResourceResponse aoResourceResponse) throws IOException,
			ApplicationException {
		String lsRenderJspPath = HHSR5Constants.EMPTY_STRING;
		String lsProvName = null;
		try {

			lsProvName = aoResourceRequest
					.getParameter(ApplicationConstants.PROVIDER_NAME);

			if (lsProvName.isEmpty()) {
				lsProvName = (String) aoResourceRequest
						.getPortletSession()
						.getAttribute(ApplicationConstants.PROVIDER_AGENCY_LIST);
				aoResourceRequest.setAttribute(
						ApplicationConstants.PROVIDER_AGENCY_LIST,
						StringEscapeUtils.escapeJavaScript(lsProvName));
			}
			lsRenderJspPath = getRenderShareDocumentStep3(aoResourceRequest,
					lsProvName);
		} catch (Exception aoExp) {
			LOG_OBJECT.Error(
					"Exception in Step 2 sharing screen in Document Vault",
					aoExp);
			throw new ApplicationException(
					"Internal Error Occured While Processing Your Request",
					aoExp);
		}

		return new ModelAndView(lsRenderJspPath);

	}

	/**
	 * This method will be executed when a user click on next button from the
	 * second screen of document/folder sharing
	 * 
	 * @param aoResourceRequest
	 *            resource request object
	 * @param aoResourceResponse
	 *            resource response object
	 * @return ModelAndView object
	 * @throws IOException
	 *             when IO exception occurs
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@ResourceMapping("fromStep2Sharing")
	protected ModelAndView handleResourceForStep2Sharing(
			ResourceRequest aoResourceRequest,
			ResourceResponse aoResourceResponse) throws IOException,
			ApplicationException {
		String lsRenderJspPath = HHSR5Constants.EMPTY_STRING;
		try {
			String lsProvName = (String) aoResourceRequest
					.getParameter(ApplicationConstants.PROVIDER_NAME);
			aoResourceRequest.setAttribute(ApplicationConstants.PROVIDER_NAME,
					StringEscapeUtils.escapeJavaScript(lsProvName));
			if (null != lsProvName) {
				aoResourceRequest.setAttribute(
						ApplicationConstants.PROVIDER_AGENCY_LIST,
						StringEscapeUtils.escapeJavaScript(lsProvName));
				aoResourceRequest.getPortletSession().setAttribute(
						ApplicationConstants.PROVIDER_AGENCY_LIST,
						StringEscapeUtils.escapeJavaScript(lsProvName));
			}
			lsRenderJspPath = ApplicationConstants.SHARE_DOCUMENT_STEP2_PAGE;

		} catch (Exception aoExp) {
			LOG_OBJECT.Error(
					"Exception in Step 3 sharing screen in Document Vault",
					aoExp);
			throw new ApplicationException(
					"Internal Error Occured While Processing Your Request",
					aoExp);
		}

		return new ModelAndView(lsRenderJspPath);

	}

	/**
	 * This method will be executed when a user click on next button from the
	 * third screen of document/folder sharing
	 * 
	 * @param aoResourceRequest
	 *            resource request object
	 * @param aoResourceResponse
	 *            resource response object
	 * @return ModelAndView object
	 * @throws IOException
	 *             when IO exception occurs
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@ResourceMapping("fromStep3Sharing")
	protected ModelAndView handleResourceForStep3Sharing(
			ResourceRequest aoResourceRequest,
			ResourceResponse aoResourceResponse) throws IOException,
			ApplicationException {
		String lsRenderJspPath = HHSR5Constants.EMPTY_STRING;
		try {
			String lsProvName = aoResourceRequest
					.getParameter(ApplicationConstants.PROVIDER_NAME);

			if (null != lsProvName) {
				aoResourceRequest.setAttribute(
						ApplicationConstants.PROVIDER_AGENCY_LIST, lsProvName);
				aoResourceRequest.getPortletSession().setAttribute(
						ApplicationConstants.PROVIDER_AGENCY_LIST, lsProvName);
			}
			aoResourceRequest.setAttribute(
					ApplicationConstants.SHARE_DOCUMENT_LIST,
					ApplicationSession.getAttribute(aoResourceRequest, true,
							ApplicationConstants.SHARE_DOCUMENTS_LIST));
			lsRenderJspPath = ApplicationConstants.SHARE_DOCUMENT_STEP4_PAGE;

		} catch (Exception aoExp) {
			LOG_OBJECT.Error(
					"Exception in Step 4 sharing screen in Document Vault",
					aoExp);
			throw new ApplicationException(
					"Internal Error Occured While Processing Your Request",
					aoExp);
		}
		return new ModelAndView(lsRenderJspPath);

	}

	/**
	 * This method will be executed when a user click on back button from the
	 * second screen of document/folder sharing
	 * 
	 * @param aoResourceRequest
	 *            resource request object
	 * @param aoResourceResponse
	 *            resource response object
	 * @return ModelAndView object
	 * @throws IOException
	 *             when IO exception occurs
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@ResourceMapping("backtoStep1Sharing")
	protected ModelAndView handleResourceForBacktoStep1Sharing(
			ResourceRequest aoResourceRequest,
			ResourceResponse aoResourceResponse) throws IOException,
			ApplicationException {
		String lsRenderJspPath = HHSR5Constants.EMPTY_STRING;
		try {
			String lsProvName = aoResourceRequest
					.getParameter(ApplicationConstants.PROVIDER_NAME);
			aoResourceRequest.setAttribute(ApplicationConstants.PROVIDER_NAME,
					StringEscapeUtils.escapeJavaScript(lsProvName));
			aoResourceRequest.setAttribute(
					ApplicationConstants.SHARE_DOCUMENT_LIST,
					ApplicationSession.getAttribute(aoResourceRequest, true,
							ApplicationConstants.SHARE_DOCUMENTS_LIST));
			aoResourceRequest.getPortletSession().setAttribute(
					ApplicationConstants.PROVIDER_AGENCY_LIST,
					StringEscapeUtils.escapeJavaScript(lsProvName));
			lsRenderJspPath = ApplicationConstants.SHARE_DOCUMENT_STEP1_PAGE;

		} catch (Exception aoExp) {
			LOG_OBJECT.Error(
					"Exception in Step 2 sharing screen in Document Vault",
					aoExp);
			throw new ApplicationException(
					"Internal Error Occured While Processing Your Request",
					aoExp);
		}

		return new ModelAndView(lsRenderJspPath);

	}

	/**
	 * This method will be executed when a user click on back button from the
	 * third screen of document/folder sharing
	 * 
	 * @param aoResourceRequest
	 *            resource request object
	 * @param aoResourceResponse
	 *            resource response object
	 * @return ModelAndView object
	 * @throws IOException
	 *             when IO exception occurs
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@ResourceMapping("backtoStep2Sharing")
	protected ModelAndView handleResourceForBacktoStep2Sharing(
			ResourceRequest aoResourceRequest,
			ResourceResponse aoResourceResponse) throws IOException,
			ApplicationException {
		String lsRenderJspPath = HHSR5Constants.EMPTY_STRING;
		try {

			String lsProvName = aoResourceRequest
					.getParameter(ApplicationConstants.PROVIDER_NAME);
			aoResourceRequest.getPortletSession().setAttribute(
					ApplicationConstants.PROVIDER_AGENCY_LIST,
					StringEscapeUtils.escapeJavaScript(lsProvName));

			if (null == lsProvName || lsProvName.isEmpty()) {
				lsProvName = (String) aoResourceRequest
						.getPortletSession()
						.getAttribute(ApplicationConstants.PROVIDER_AGENCY_LIST);
			}
			lsRenderJspPath = getRenderShareDocumentStep3(aoResourceRequest,
					StringEscapeUtils.unescapeJavaScript(lsProvName));

		} catch (Exception aoExp) {
			LOG_OBJECT.Error(
					"Exception in Step 3 sharing screen in Document Vault",
					aoExp);
			throw new ApplicationException(
					"Internal Error Occured While Processing Your Request",
					aoExp);
		}

		return new ModelAndView(lsRenderJspPath);

	}

	/**
	 * This method will be executed when a user click on back button from the
	 * fourth screen of document/folder sharing
	 * 
	 * @param aoResourceRequest
	 *            resource request object
	 * @param aoResourceResponse
	 *            resource response object
	 * @return ModelAndView object
	 * @throws IOException
	 *             when IO exception occurs
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@ResourceMapping("backtoStep3Sharing")
	protected ModelAndView handleResourceForBacktoStep3Sharing(
			ResourceRequest aoResourceRequest,
			ResourceResponse aoResourceResponse) throws IOException,
			ApplicationException {
		String lsRenderJspPath = HHSR5Constants.EMPTY_STRING;
		try {
			String lsProvName = aoResourceRequest
					.getParameter(ApplicationConstants.PROVIDER_NAME);

			if (null == lsProvName || lsProvName.isEmpty()) {
				lsProvName = (String) aoResourceRequest
						.getPortletSession()
						.getAttribute(ApplicationConstants.PROVIDER_AGENCY_LIST);
			}
			aoResourceRequest.setAttribute(ApplicationConstants.PROVIDER_NAME,
					StringEscapeUtils.escapeJavaScript(lsProvName));
			lsRenderJspPath = ApplicationConstants.SHARE_DOCUMENT_STEP2_PAGE;

		} catch (Exception aoExp) {
			LOG_OBJECT.Error("Exception in Share Step3 in Document Vault",
					aoExp);
			throw new ApplicationException(
					"Internal Error Occured While Processing Your Request",
					aoExp);
		}

		return new ModelAndView(lsRenderJspPath);

	}

	/**
	 * This method will be executed when a user click on share button from the
	 * fourth screen of document/folder sharing
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action response object
	 * @throws IOException
	 *             when IO exception occurs
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@ActionMapping(params = "submit_action=finalShareDocument")
	protected void handleActionForStep4Sharing(ActionRequest aoRequest,
			ActionResponse aoResponse) throws IOException, ApplicationException {

		try {
			FileNetOperationsUtils
					.removeMessageFromSessionAndRequest(aoRequest);
			actionShareDocuments(aoRequest, aoResponse);
			// Emergency Build- 4.0.1- changes for share with list
			P8UserSession loUserSession = (P8UserSession) aoRequest
					.getPortletSession().getAttribute(
							ApplicationConstants.FILENET_SESSION_OBJECT,
							PortletSession.APPLICATION_SCOPE);
			String lsUserId = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
							PortletSession.APPLICATION_SCOPE);
			HashMap<String, String> loSharedList = ProviderAgencyHomeController
					.getShareWithOrgDetails(loUserSession, lsUserId);
			aoRequest.getPortletSession().setAttribute(
					HHSR5Constants.SHARED_WITH_DETAILS, loSharedList,
					PortletSession.APPLICATION_SCOPE);
			// Emergency Build- 4.0.0.61- changes for share with list end
		} catch (ApplicationException loAppEx) {
			LOG_OBJECT.Error("Exception in Document Vault", loAppEx);
			throw loAppEx;
		} catch (Exception aoExp) {
			LOG_OBJECT.Error(
					"Exception in step 4- Share file in Document Vault", aoExp);
			throw new ApplicationException(
					"Internal Error Occured While Processing Your Request",
					aoExp);
		}

	}

	/**
	 * This method will be executed when a user click on share button from the
	 * fourth screen of document sharing
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action response object
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 * @throws IOException
	 *             when any IOException occurred
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void actionShareDocuments(ActionRequest aoRequest,
			ActionResponse aoResponse) throws ApplicationException, IOException {
		HashMap<String, String> loLockingMapId = new HashMap<String, String>();
		List<String> loLockIdList = new ArrayList<String>();
		Boolean lbLockFlag = true;
		try {
			String lsParentFolderId = aoRequest.getParameter("parentFolderId");
			if (null != lsParentFolderId && !lsParentFolderId.isEmpty()
					&& !lsParentFolderId.equalsIgnoreCase(HHSR5Constants.NULL)) {
				aoResponse.setRenderParameter(HHSR5Constants.FOLDER_ID,
						lsParentFolderId);
			}
			P8UserSession loUserSession = (P8UserSession) aoRequest
					.getPortletSession().getAttribute(
							ApplicationConstants.FILENET_SESSION_OBJECT,
							PortletSession.APPLICATION_SCOPE);
			String lsSharedById = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
							PortletSession.APPLICATION_SCOPE);
			String lsSharedByName = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_ORG_NAME,
							PortletSession.APPLICATION_SCOPE);
			String lsUserOrg = null;
			String lsOrgType = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
							PortletSession.APPLICATION_SCOPE);
			String lsUserId = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
							PortletSession.APPLICATION_SCOPE);
			HashMap<String, Object> loUserProps = new HashMap<String, Object>();
			loUserProps.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY,
					lsUserId);
			if (null != lsOrgType
					&& lsOrgType.equalsIgnoreCase(HHSConstants.CITY_ORG)) {
				lsSharedById = lsOrgType;
			}

			if (lsOrgType.equalsIgnoreCase(ApplicationConstants.CITY_ORG)) {
				lsUserOrg = lsOrgType;
			} else {
				lsUserOrg = (String) aoRequest.getPortletSession()
						.getAttribute(
								ApplicationConstants.KEY_SESSION_USER_ORG,
								PortletSession.APPLICATION_SCOPE);
			}

			List<Document> loDocTypeMap = new ArrayList<Document>();
			HashMap<String, List> loShareWithProvioderAgencyMap = new HashMap<String, List>();
			List loShareWithProvList = new ArrayList();
			List loShareWithAgencyList = new ArrayList();
			HashMap<String, String> loReqMap = new HashMap<String, String>();
			String loProvAgency = aoRequest
					.getParameter(ApplicationConstants.PROV_AGENCY_LIST);
			String[] loProvAgencyArray = loProvAgency
					.split(HHSConstants.DOUBLE_HHSUTIL_DELIM_PIPE);
			List loProvAgencyList = new ArrayList<String>();
			String lsAgencyId = HHSR5Constants.EMPTY_STRING;
			for (int liCount = 0; liCount < loProvAgencyArray.length; liCount++) {
				String lsAgencyName = loProvAgencyArray[liCount];
				if (loProvAgencyArray[liCount]
						.contains(ApplicationConstants.DOCUMENT_VAULT_PROVIDER)) {
					lsAgencyName = lsAgencyName.substring(0,
							lsAgencyName.indexOf(HHSR5Constants.CARET_SIGN))
							.trim();
					lsAgencyName = StringEscapeUtils
							.unescapeJavaScript(lsAgencyName);
					lsAgencyId = FileNetOperationsUtils.getProviderId(
							(List) BaseCacheManagerWeb.getInstance()
									.getCacheObject(
											ApplicationConstants.PROV_LIST),
							lsAgencyName);
					loShareWithProvList.add(lsAgencyId);

				}
				if (loProvAgencyArray[liCount]
						.contains(ApplicationConstants.DOCUMENT_VAULT_AGENCY)) {
					lsAgencyName = lsAgencyName.substring(0,
							lsAgencyName.indexOf(HHSR5Constants.CARET_SIGN))
							.trim();
					lsAgencyName = StringEscapeUtils
							.unescapeJavaScript(lsAgencyName);
					lsAgencyId = FileNetOperationsUtils.getAgencyId(
							(TreeSet<String>) BaseCacheManagerWeb.getInstance()
									.getCacheObject(
											ApplicationConstants.AGENCY_LIST),
							lsAgencyName);
					loShareWithAgencyList.add(lsAgencyId);
				}
			}
			loProvAgencyList.addAll(0, loShareWithAgencyList);
			loProvAgencyList.addAll(loShareWithProvList);
			loShareWithProvioderAgencyMap.put(
					ApplicationConstants.DOCUMENT_VAULT_PROVIDER,
					loShareWithProvList);
			loShareWithProvioderAgencyMap.put(
					ApplicationConstants.DOCUMENT_VAULT_AGENCY,
					loShareWithAgencyList);
			List<Document> loShareDocumentList = (ArrayList<Document>) ApplicationSession
					.getAttribute(aoRequest,
							ApplicationConstants.SHARE_DOCUMENTS_LIST);
			if (!CollectionUtils.isEmpty(loShareDocumentList)) {
				Iterator<Document> loIter = loShareDocumentList.iterator();
				while (loIter.hasNext()) {
					Document loDocObj = loIter.next();
					loLockingMapId.put(loDocObj.getDocumentId(),
							loDocObj.getDocType());
				}
			}
			// Adding lock status for folders and documents
			Channel loChannelLock = new Channel();
			loChannelLock.setData("documentIdMap", loLockingMapId);
			loChannelLock.setData("aoFilenetSession", loUserSession);
			loChannelLock.setData("next_action", "share");
			HHSTransactionManager.executeTransaction(loChannelLock,
					"getFolderPathFromFilenet",
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			HashMap<String, List<String>> loLockingMap = (HashMap) loChannelLock
					.getData("loPathMap");
			for (Map.Entry<String, List<String>> entry : loLockingMap
					.entrySet()) {
				List<String> loList = entry.getValue();
				for (Iterator iterator = loList.iterator(); iterator.hasNext();) {
					String lsLockPath = (String) iterator.next();
					loLockIdList.add(lsLockPath);
				}
			}
			if (FileNetOperationsUtils.checkLock(loLockIdList, lsUserOrg)) {
				aoResponse.setRenderParameter(
						ApplicationConstants.ERROR_MESSAGE, PropertyLoader
								.getProperty(P8Constants.ERROR_PROPERTY_FILE,
										HHSR5Constants.LOCK_MESSAGE_SHARE));
				aoResponse.setRenderParameter(
						ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE);
				lbLockFlag = false;
			} else {
				List<String> loShareOpsList = new ArrayList<String>();
				loShareOpsList.add(HHSR5Constants.SHARE_FOLDER_OPS);
				loShareOpsList.add(HHSR5Constants.SHARE_FILE_OPS);
				FileNetOperationsUtils.addLock(loLockIdList, aoRequest);
				loReqMap.put(HHSR5Constants.ORG_ID, lsUserOrg);
				loReqMap.put(HHSConstants.LS_USER_ORG_TYPE, lsOrgType);

				Channel loChannel = new Channel();
				loChannel.setData(HHSConstants.AO_FILENET_SESSION,
						loUserSession);
				loChannel.setData(HHSR5Constants.AUDIT_ENTITY_NAME,
						loShareOpsList);
				loChannel
						.setData(
								ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_LIST_PARAMETER,
								loShareDocumentList);
				loChannel.setData(HHSR5Constants.PROV_AGENCY_MAP,
						loShareWithProvioderAgencyMap);
				loChannel.setData(HHSR5Constants.SHARED_BY, lsSharedById);
				// added for R5
				loChannel.setData(HHSR5Constants.REQ_MAP, loReqMap);
				loChannel.setData(HHSR5Constants.DOC_TYPE_MAP, loDocTypeMap);
				loChannel.setData(HHSR5Constants.REQUIRED_MAP, loUserProps);
				TransactionManager.executeTransaction(loChannel,
						HHSR5Constants.SHARE_DOCUMENT_FILENET,
						HHSR5Constants.TRANSACTION_ELEMENT_R5);

				boolean lbShareStatus = (Boolean) loChannel
						.getData(HHSR5Constants.SHARED_STATUS);
				if (lbShareStatus) {
					createShareMessageAndSendNotification(aoRequest,
							aoResponse, loUserSession, lsSharedByName,
							loShareWithProvList, loShareWithAgencyList,
							loProvAgencyList, loShareDocumentList, loChannel);
				} else {
					aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION,
							ApplicationConstants.ERROR);
				}
				aoResponse.setRenderParameter(HHSR5Constants.HEADER_CLICK,
						"true");
			}

		} catch (ApplicationException loAppEx) {
			LOG_OBJECT.Error("Exception during fetching document data linking",
					loAppEx);
		} catch (Exception aoExp) {
			LOG_OBJECT.Error("Exception during fetching document data linking",
					aoExp);
		} finally {
			if (lbLockFlag) {
				FileNetOperationsUtils.removeLock(loLockIdList, aoRequest);
			}

		}

	}

	/**
	 * This method will execute when Link button of any document is clicked. It
	 * will get the history of modified dates and entity type of the document
	 * and display it. It calls the "linkInfo" transaction and renders
	 * overlay.jsp. Added for release 5
	 * 
	 * @param aoRequest
	 *            a Action Request object
	 * @param aoResponse
	 *            a Action Response object
	 * @return ModelAndViewObject
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 * @throws IOException
	 *             If an Input Output Exception occurs Added for release 5
	 * 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ResourceMapping("checkLinkage")
	protected ModelAndView displayInfo(ResourceRequest aoRequest,
			ResourceResponse aoResponse) throws ApplicationException {
		Channel loChannel = new Channel();
		String lsOverName = null;
		ModelAndView loModel = new ModelAndView();
		try {
			PortletSession loSession = aoRequest.getPortletSession();
			String lsOrgType = (String) loSession.getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE,
					PortletSession.APPLICATION_SCOPE);
			String lsDocType = (String) aoRequest
					.getParameter(HHSConstants.DOCTYPE);
			String lsDocData = (String) aoRequest
					.getParameter(HHSConstants.DOC_ID);
			//**  start QC 8914 R7.2 read only role **/
			String role_current = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_ROLE_CURRENT, PortletSession.APPLICATION_SCOPE);
			//**  end QC 8914 R7.2 read only role **/
			List<DocumentBean> loResult = new ArrayList<DocumentBean>();
			HashMap<String, String> loReqProps = new HashMap<String, String>();
			loReqProps.put(HHSR5Constants.USER_ORG_TYPE, lsOrgType);
			loReqProps.put(HHSR5Constants.DOC_DATA, lsDocData);
			if (lsDocType.contains(HHSR5Constants.CHAR_500)) {
				P8UserSession loUserSession = (P8UserSession) loSession
						.getAttribute(
								ApplicationConstants.FILENET_SESSION_OBJECT,
								PortletSession.APPLICATION_SCOPE);
				List loDocIdList = new ArrayList();
				loDocIdList.add(lsDocData);
				loChannel.setData(HHSConstants.AO_FILENET_SESSION,
						loUserSession);

				loReqProps.put(ApplicationConstants.PERIOD_COVER_FROM_MONTH,
						HHSConstants.EMPTY_STRING);
				loReqProps.put(ApplicationConstants.PERIOD_COVER_FROM_YEAR,
						HHSConstants.EMPTY_STRING);
				loReqProps.put(ApplicationConstants.PERIOD_COVER_TO_MONTH,
						HHSConstants.EMPTY_STRING);
				loReqProps.put(ApplicationConstants.PERIOD_COVER_TO_YEAR,
						HHSConstants.EMPTY_STRING);
				loChannel.setData(HHSConstants.HM_REQIRED_PROPERTY_MAP,
						loReqProps);
				loChannel.setData(HHSConstants.AO_DOC_ID_LIST, loDocIdList);
				HHSTransactionManager.executeTransaction(loChannel,
						HHSR5Constants.GET_CHAR_500_LINKAGE_DETAIL,
						HHSR5Constants.TRANSACTION_ELEMENT_R5);
			} else {
				loChannel.setData(HHSR5Constants.LINKAGE_DATA, loReqProps);
				HHSTransactionManager.executeTransaction(loChannel,
						HHSR5Constants.LINK_INFO,
						HHSR5Constants.TRANSACTION_ELEMENT_R5);
			}
			loResult = (List<DocumentBean>) loChannel
					.getData(HHSR5Constants.INFO_DISPLAYED);
			String lsPermissionType = (String) loSession.getAttribute(
					HHSConstants.PERMISSION_TYPE,
					PortletSession.APPLICATION_SCOPE);
			String lsRole = (String) loSession.getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ROLE,
					PortletSession.APPLICATION_SCOPE);
			
			for (Iterator iterator = loResult.iterator(); iterator.hasNext();) {
				DocumentBean loDocumentBean = (DocumentBean) iterator.next();
				loDocumentBean.setOrgID(lsOrgType);
				loDocumentBean.setPermissionType(lsPermissionType);
				loDocumentBean.setUserRole(lsRole);
				loDocumentBean.setContractAccess((String) aoRequest
						.getParameter("contractAccess"));
				/*start  QC 8914 read only role R 7.2*/
				if( ApplicationConstants.ROLE_OBSERVER.equalsIgnoreCase(role_current))  
				{
					loDocumentBean.setUserSubRole(ApplicationConstants.ROLE_OBSERVER);
				}	
				/*end  QC 8914 read only role R 7.2*/
				
			}
			loSession.setAttribute(HHSR5Constants.SESSION_VAR, loResult,
					PortletSession.APPLICATION_SCOPE);
			lsOverName = HHSR5Constants.LINK_STATUS;
			loModel.setView(HHSR5Constants.DOCUMENT_VAULT_OVERLAY);
			loModel.addObject(HHSR5Constants.OVERLAY_NAME, lsOverName);

		} catch (ApplicationException loAppEx) {
			LOG_OBJECT.Error("Exception during fetching document data linking",
					loAppEx);
		} catch (Exception aoExp) {
			LOG_OBJECT.Error("Exception during fetching document data linking",
					aoExp);
		}
		return loModel;
	}

	/**
	 * This method will execute when shared button is clicked for a document.It
	 * will get the list of Providers/NYC Government Agencies with which the
	 * organization is sharing the file. It calls the bbbb transaction and
	 * displays bbbb overlay. Added for release 5.
	 * 
	 * @param aoRequest
	 *            a Resource Request object
	 * @param aoResponse
	 *            a Resource Response object
	 * @return displaysharedocuments.jsp
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ResourceMapping("linkage")
	protected ModelAndView displaySharedInfo(ResourceRequest aoRequest,
			ResourceResponse aoResponse) throws ApplicationException {
		List<String> loDataList = new ArrayList<String>();
		Channel loChannel = new Channel();
		String lsShareStatus = aoRequest
				.getParameter(HHSR5Constants.SHARE_STATUS);
		P8UserSession loUserSession = (P8UserSession) aoRequest
				.getPortletSession().getAttribute(
						ApplicationConstants.FILENET_SESSION_OBJECT,
						PortletSession.APPLICATION_SCOPE);

		loDataList = (List) showAllSharedWithDocumentAction(aoRequest,
				aoResponse, loChannel, loUserSession);
		aoRequest.setAttribute(ApplicationConstants.PROVIDER_ARRAY, loDataList);
		aoRequest.setAttribute(HHSConstants.DOC_ID,
				aoRequest.getParameter(HHSR5Constants.DOCUMENT_ID));
		aoRequest.setAttribute(ApplicationConstants.DOCUMENT_NAME,
				aoRequest.getParameter(ApplicationConstants.DOCUMENT_NAME));
		aoRequest.setAttribute(HHSConstants.DOCTYPE,
				aoRequest.getParameter(HHSConstants.DOCTYPE));
		aoRequest.setAttribute(HHSR5Constants.SHARE_STATUS, lsShareStatus);
		return new ModelAndView(ApplicationConstants.DISPLAY_SHARED_DOCUMENTS);

	}

	/**
	 * The method will delete entity from RecycleBin
	 * 
	 * @param aoRequest
	 *            a action request object
	 * @param aoResponse
	 *            a action request object
	 * @throws IOException
	 *             when IO exception occurs
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@ActionMapping(params = "submit_action=deleteForever")
	protected void handleActionForDeleteForever(ActionRequest aoRequest,
			ActionResponse aoResponse) throws IOException, ApplicationException {
		FileNetOperationsUtils.removeMessageFromSessionAndRequest(aoRequest);
		actionDeleteDocument(aoRequest, aoResponse);
	}

	/**
	 * 
	 * @param aoRequest
	 *            a action request object
	 * @param aoResponse
	 *            a action response object
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	private void actionDeleteDocument(ActionRequest aoRequest,
			ActionResponse aoResponse) throws ApplicationException {
		List<String> loLockIdList = new ArrayList<String>();
		HashMap<String, String> loLockListMap = new HashMap<String, String>();
		List<Document> loEntityList = new ArrayList<Document>();

		Boolean lbLockFlag = true;
		try {
			String lsMenuOptionsFlag = aoRequest.getParameter("menuFlag");
			String lsUserOrg = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
							PortletSession.APPLICATION_SCOPE);
			String lsUserOrgType = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
							PortletSession.APPLICATION_SCOPE);
			String lsUserId = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
							PortletSession.APPLICATION_SCOPE);
			if (null != lsUserOrgType
					&& lsUserOrgType.equalsIgnoreCase(HHSR5Constants.USER_CITY)) {
				lsUserOrg = lsUserOrgType;
			}
			P8UserSession loUserSession = (P8UserSession) aoRequest
					.getPortletSession().getAttribute(
							ApplicationConstants.FILENET_SESSION_OBJECT,
							PortletSession.APPLICATION_SCOPE);
			PortletSession loSession = aoRequest.getPortletSession();
			Map<String, String[]> loMap = aoRequest.getParameterMap();
			HashMap<String, Object> loReqProps = new HashMap<String, Object>();
			HashMap<String, Object> loUserProps = new HashMap<String, Object>();
			loUserProps.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY,
					lsUserId);
			String[] loCheckedObject = loMap
					.get(ApplicationConstants.CHECKED_OBJECT);

			if (null != lsMenuOptionsFlag
					&& lsMenuOptionsFlag.equalsIgnoreCase("true")) {
				for (int i = 0; i < loCheckedObject.length; i++) {
					Document loDocument = new Document();
					String[] loTemp = loCheckedObject[i]
							.split(HHSConstants.COMMA);
					loReqProps.put(loTemp[0], loTemp[1]);
					// Added for defect 7678
					loDocument.setDocumentId(loTemp[0]);
					loDocument.setDocType(loTemp[1]);
					loEntityList.add(loDocument);
				}

			} else {
				Document loDocument = new Document();
				String lsDocId = aoRequest
						.getParameter(HHSR5Constants.DELETED_ID);
				loReqProps
						.put(lsDocId, aoRequest
								.getParameter(HHSR5Constants.RECYCLE_DOC_TYPE));
				// Added for defect 7678
				loDocument.setDocumentId(lsDocId);
				loDocument.setDocType((String) aoRequest
						.getParameter(HHSR5Constants.RECYCLE_DOC_TYPE));
				loEntityList.add(loDocument);

			}
			String lsRecycleBinId = aoRequest
					.getParameter(HHSR5Constants.RECYCLE_ID);

			loLockListMap.put(lsRecycleBinId, HHSR5Constants.EMPTY_STRING);

			Channel loChannelLock = new Channel();

			loChannelLock.setData("documentIdMap", loLockListMap);
			loChannelLock.setData("aoFilenetSession", loUserSession);
			loChannelLock.setData("next_action", "deleteForever");
			HHSTransactionManager.executeTransaction(loChannelLock,
					"getFolderPathFromFilenet",
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			HashMap<String, List<String>> loLockingMap = (HashMap) loChannelLock
					.getData("loPathMap");
			for (Map.Entry<String, List<String>> entry : loLockingMap
					.entrySet()) {
				List<String> loList = entry.getValue();
				for (Iterator iterator = loList.iterator(); iterator.hasNext();) {
					String lsLockPath = (String) iterator.next();
					loLockIdList.add(lsLockPath);
				}
			}

			if (FileNetOperationsUtils.checkLock(loLockIdList, lsUserOrg)) {
				aoResponse.setRenderParameter(
						ApplicationConstants.ERROR_MESSAGE,
						PropertyLoader.getProperty(
								P8Constants.ERROR_PROPERTY_FILE,
								HHSR5Constants.LOCK_MESSAGE_DELETE_FOREVER));
				aoResponse.setRenderParameter(
						ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE);
				lbLockFlag = false;

			} else {
				FileNetOperationsUtils.addLock(loLockIdList, aoRequest);
				Channel loChannel = new Channel();
				List<String> lsDeleteOpsList = new ArrayList<String>();
				lsDeleteOpsList.add(HHSR5Constants.DELETE_FOREVR_FOL);
				lsDeleteOpsList.add(HHSR5Constants.DELETE_FOREVR_FILE);
				loChannel.setData(HHSR5Constants.AO_FILENET_SESSION,
						loUserSession);
				loChannel.setData(HHSR5Constants.REQUIRED_MAP, loUserProps);
				loChannel.setData(HHSR5Constants.FILTER_PROPS, loReqProps);
				loChannel.setData(HHSR5Constants.AUDIT_ENTITY_NAME,
						lsDeleteOpsList);
				loChannel.setData(HHSR5Constants.DELETE_LIST_ITEMS,
						loEntityList);
				// Adding below line for Release 4.0.1
				loChannel.setData(HHSR5Constants.ACTION_NAME_R5,
						HHSR5Constants.DELETE_DOCUMENT_FILENET);
				TransactionManager.executeTransaction(loChannel,
						HHSR5Constants.DELETE_DOCUMENT_FILENET,
						HHSR5Constants.TRANSACTION_ELEMENT_R5);
				boolean lbDeleteStatus = (Boolean) loChannel
						.getData(HHSR5Constants.DELETE_STATUS);
				boolean lbFolderDeleteStatus = (Boolean) loChannel
						.getData(HHSR5Constants.DELETE_FOLDER_STATUS);
				// Changes for defect 7678 starts
				if ((Boolean) loChannel
						.getData(HHSR5Constants.ENTITY_FLAG_FOR_MAX_COUNT)
						&& lbDeleteStatus && lbFolderDeleteStatus) {
					FileNetOperationsUtils.reInitializePageIterator(loSession,
							loUserSession);
					String lsMessage = PropertyLoader.getProperty(
							P8Constants.ERROR_PROPERTY_FILE,
							HHSR5Constants.RECYCLE_BIN_DELETE_FOREVER);
					aoResponse.setRenderParameter(
							ApplicationConstants.ERROR_MESSAGE, lsMessage);
					aoResponse.setRenderParameter(
							ApplicationConstants.ERROR_MESSAGE_TYPE,
							ApplicationConstants.MESSAGE_PASS_TYPE);
				}
				// Changes for defect 7678 ends
				else {
					if (!(Boolean) loChannel
							.getData(HHSR5Constants.ENTITY_FLAG_FOR_MAX_COUNT)) {
						aoResponse
								.setRenderParameter(
										ApplicationConstants.ERROR_MESSAGE,
										PropertyLoader
												.getProperty(
														P8Constants.ERROR_PROPERTY_FILE,
														HHSR5Constants.DELETE_TRANSACTION_MESSAGE_MAX_COUNT));
						aoResponse.setRenderParameter(
								ApplicationConstants.ERROR_MESSAGE_TYPE,
								ApplicationConstants.MESSAGE_FAIL_TYPE);
					} else if (!lbDeleteStatus && !lbFolderDeleteStatus) {
						String lsMessage = PropertyLoader.getProperty(
								P8Constants.ERROR_PROPERTY_FILE,
								HHSR5Constants.RECYCLE_BIN_ERROR_MESSAGE);
						aoResponse.setRenderParameter(
								ApplicationConstants.ERROR_MESSAGE, lsMessage);
						aoResponse.setRenderParameter(
								ApplicationConstants.ERROR_MESSAGE_TYPE,
								ApplicationConstants.MESSAGE_FAIL_TYPE);
					} else {
						String lsMessage = PropertyLoader.getProperty(
								P8Constants.ERROR_PROPERTY_FILE,
								HHSR5Constants.RECYCLE_BIN_DELETE_FOREVER);
						aoResponse.setRenderParameter(
								ApplicationConstants.ERROR_MESSAGE, lsMessage);
						aoResponse.setRenderParameter(
								ApplicationConstants.ERROR_MESSAGE_TYPE,
								ApplicationConstants.MESSAGE_PASS_TYPE);
					}
				}
			}
			aoResponse.setRenderParameter(HHSR5Constants.JSP_NAME,
					HHSR5Constants.RECYCLE_BIN_JSP_NAME);
		} catch (ApplicationException loAppEx) {
			try {
				setErrorMessageInResponse(aoRequest, aoResponse,
						aoRequest.getPortletSession(), HHSR5Constants.TRUE,
						HHSR5Constants.EMPTY_STRING, loAppEx);
			} catch (IOException aoIoExp) {
				LOG_OBJECT.Error("IOException during file upload", aoIoExp);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION,
						ApplicationConstants.ERROR);
			}
		} catch (Exception aoExp) {
			LOG_OBJECT.Error("Exception in Creating folder Document Vault",
					aoExp);
			throw new ApplicationException(
					"Internal Error Occured While Processing Your Request",
					aoExp);
		} finally {
			if (lbLockFlag) {
				FileNetOperationsUtils.removeLock(loLockIdList, aoRequest);
			}

		}

	}

	/**
	 * This method will empty recycle bin
	 * 
	 * @param aoRequest
	 *            a action request object
	 * @param aoResponse
	 *            a action response object
	 * @throws IOException
	 *             when IO exception occurs
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@ActionMapping(params = "submit_action=emptyRecycleBin")
	protected void handleActionForEmptyRecycleBin(ActionRequest aoRequest,
			ActionResponse aoResponse) throws IOException, ApplicationException {
		List<String> loLockIdList = new ArrayList<String>();
		HashMap<String, String> loLockListMap = new HashMap<String, String>();
		Boolean lbLockFlag = true;
		try {
			FileNetOperationsUtils
					.removeMessageFromSessionAndRequest(aoRequest);
			P8UserSession loUserSession = (P8UserSession) aoRequest
					.getPortletSession().getAttribute(
							ApplicationConstants.FILENET_SESSION_OBJECT,
							PortletSession.APPLICATION_SCOPE);
			String lsUserOrg = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
							PortletSession.APPLICATION_SCOPE);
			String lsUserOrgType = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
							PortletSession.APPLICATION_SCOPE);
			String lsUserId = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
							PortletSession.APPLICATION_SCOPE);
			HashMap<String, Object> loUserProps = new HashMap<String, Object>();
			loUserProps.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY,
					lsUserId);
			if (null != lsUserOrgType
					&& lsUserOrgType.equalsIgnoreCase(HHSR5Constants.USER_CITY)) {
				lsUserOrg = lsUserOrgType;
			}
			String lsRecycleBinId = aoRequest
					.getParameter(HHSR5Constants.RECYCLE_ID);
			loLockListMap.put(lsRecycleBinId, HHSR5Constants.EMPTY_STRING);

			Channel loChannelLock = new Channel();

			loChannelLock.setData("documentIdMap", loLockListMap);
			loChannelLock.setData("aoFilenetSession", loUserSession);
			loChannelLock.setData("next_action", "emptyRecycleBin");
			HHSTransactionManager.executeTransaction(loChannelLock,
					"getFolderPathFromFilenet",
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			HashMap<String, List<String>> loLockingMap = (HashMap) loChannelLock
					.getData("loPathMap");
			for (Map.Entry<String, List<String>> entry : loLockingMap
					.entrySet()) {
				List<String> loList = entry.getValue();
				for (Iterator iterator = loList.iterator(); iterator.hasNext();) {
					String lsLockPath = (String) iterator.next();
					loLockIdList.add(lsLockPath);
				}
			}

			if (FileNetOperationsUtils.checkLock(loLockIdList, lsUserOrg)) {

				aoResponse.setRenderParameter(
						ApplicationConstants.ERROR_MESSAGE,
						PropertyLoader.getProperty(
								P8Constants.ERROR_PROPERTY_FILE,
								HHSR5Constants.LOCK_MESSAGE_EMPTY_RECYCLEBIN));
				aoResponse.setRenderParameter(
						ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE);
				lbLockFlag = false;

			} else {
				FileNetOperationsUtils.addLock(loLockIdList, aoRequest);
				PortletSession loSession = aoRequest.getPortletSession();
				if (lsUserOrgType
						.equalsIgnoreCase(ApplicationConstants.CITY_ORG)) {
					lsUserOrg = lsUserOrgType;
				} else {
					lsUserOrg = (String) loSession.getAttribute(
							ApplicationConstants.KEY_SESSION_USER_ORG,
							PortletSession.APPLICATION_SCOPE);
				}

				String lsFolderPath = FileNetOperationsUtils.setFolderPath(
						lsUserOrgType, lsUserOrg, HHSR5Constants.RECYCLE_BIN);
				List<String> lsEmptyBinOpsList = new ArrayList<String>();
				lsEmptyBinOpsList.add("EmptyBin Folder");
				lsEmptyBinOpsList.add("EmptyBin File");
				Channel loChannel = new Channel();
				loChannel.setData(HHSR5Constants.REQUIRED_MAP, loUserProps);
				loChannel.setData(HHSR5Constants.AUDIT_ENTITY_NAME,
						lsEmptyBinOpsList);
				loChannel.setData(HHSConstants.AO_FILENET_SESSION,
						loUserSession);
				loChannel.setData(HHSR5Constants.FOLDER_PATH, lsFolderPath);
				loChannel.setData(HHSR5Constants.Org_Id, lsUserOrg);
				// Adding below line for Release 4.0.1
				loChannel.setData(HHSR5Constants.ACTION_NAME_R5,
						HHSR5Constants.EMPTY_RECYCLE_BIN);
				loChannel.setData(HHSConstants.UPDATE_FLAG,
						HHSConstants.BOOLEAN_TRUE);

				TransactionManager.executeTransaction(loChannel,
						HHSR5Constants.EMPTY_RECYCLE_BIN,
						HHSR5Constants.TRANSACTION_ELEMENT_R5);
				boolean lbDeleteStatus = (Boolean) loChannel
						.getData(HHSR5Constants.DELETE_STATUS);
				if (lbDeleteStatus) {
					FileNetOperationsUtils.reInitializePageIterator(loSession,
							loUserSession);
					String lsMessage = PropertyLoader.getProperty(
							P8Constants.ERROR_PROPERTY_FILE,
							HHSR5Constants.RECYCLE_BIN_SUCCESS_MESSAGE);
					aoResponse.setRenderParameter(
							ApplicationConstants.ERROR_MESSAGE, lsMessage);
					aoResponse.setRenderParameter(
							ApplicationConstants.ERROR_MESSAGE_TYPE,
							ApplicationConstants.MESSAGE_PASS_TYPE);
				} else {
					String lsMessage = PropertyLoader.getProperty(
							P8Constants.ERROR_PROPERTY_FILE,
							HHSR5Constants.RECYCLE_BIN_ERROR_MESSAGE);
					aoResponse.setRenderParameter(
							ApplicationConstants.ERROR_MESSAGE, lsMessage);
					aoResponse.setRenderParameter(
							ApplicationConstants.ERROR_MESSAGE_TYPE,
							ApplicationConstants.MESSAGE_FAIL_TYPE);
				}

			}
			aoResponse.setRenderParameter(HHSR5Constants.JSP_NAME,
					HHSR5Constants.RECYCLE_BIN_JSP_NAME);
		} catch (ApplicationException loAppEx) {
			try {
				setErrorMessageInResponse(aoRequest, aoResponse,
						aoRequest.getPortletSession(), HHSR5Constants.TRUE,
						HHSR5Constants.EMPTY_STRING, loAppEx);
			} catch (IOException aoIoExp) {
				LOG_OBJECT.Error("IOException during file upload", aoIoExp);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION,
						ApplicationConstants.ERROR);
			}
		} catch (Exception aoExp) {
			LOG_OBJECT.Error("Exception in Creating folder Document Vault",
					aoExp);
			throw new ApplicationException(
					"Internal Error Occured While Processing Your Request",
					aoExp);
		} finally {
			if (lbLockFlag) {
				FileNetOperationsUtils.removeLock(loLockIdList, aoRequest);
			}

		}
	}

	/**
	 * The method will select organization and display its list
	 * 
	 * @param aoRequest
	 *            a action request object
	 * @param aoResponse
	 *            a action response object
	 * @throws IOException
	 *             when IO excption occurs
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@SuppressWarnings("unchecked")
	@ActionMapping(params = "submit_action=manageOrganization")
	protected void handleActionForManageOrg(ActionRequest aoRequest,
			ActionResponse aoResponse) throws IOException, ApplicationException {
		FileNetOperationsUtils.removeMessageFromSessionAndRequest(aoRequest);
		String lsUserOrgTypeOrg = (String) aoRequest.getPortletSession()
				.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
						PortletSession.APPLICATION_SCOPE);

		String lsUserOrg = null;

		String lsSelectedOrg = (String) aoRequest
				.getParameter(HHSConstants.PROVIDER_ID);
		String lsProvName = (String) aoRequest
				.getParameter(HHSR5Constants.PROV_NAME);

		String lsUserOrgType = lsSelectedOrg.substring(lsSelectedOrg
				.lastIndexOf(ApplicationConstants.TILD) + 1);
		if (lsUserOrgType.equalsIgnoreCase(ApplicationConstants.CITY_ORG)) {
			lsUserOrg = lsUserOrgType;
		} else {
			// Start QC 9283 R 7.10.0 error StringIndexOutOfBoundsException
			//lsUserOrg = lsSelectedOrg.substring(0, lsSelectedOrg.lastIndexOf(ApplicationConstants.TILD));
			if(lsSelectedOrg.contains(ApplicationConstants.TILD)){
				lsUserOrg = lsSelectedOrg.substring(0,
						lsSelectedOrg.lastIndexOf(ApplicationConstants.TILD));
			}
			else{
				lsUserOrg = lsSelectedOrg;
			}
			//End QC 9283 R 7.10.0 error StringIndexOutOfBoundsException	
		}
		Set<String> loSharedOrgSessionList = new HashSet<String>();
		if (null != aoRequest.getPortletSession().getAttribute(
				HHSR5Constants.SHARED_ORG_SESSION_LIST)) {
			loSharedOrgSessionList = (Set<String>) aoRequest
					.getPortletSession().getAttribute(
							HHSR5Constants.SHARED_ORG_SESSION_LIST);
		}
		loSharedOrgSessionList.add(lsSelectedOrg + HHSConstants.TILD
				+ lsProvName);
		aoRequest.getPortletSession().setAttribute(
				HHSR5Constants.SHARED_ORG_SESSION_LIST, loSharedOrgSessionList);
        LOG_OBJECT.Info("SHARED_ORG_SESSION_LIST");
        
        
		aoResponse.setRenderParameter(HHSR5Constants.ORG_NAME, lsUserOrg);
		if (null != lsUserOrgTypeOrg
				&& lsUserOrgTypeOrg.contains(HHSConstants.HHSUTIL_AGENCY)) {
			aoResponse.setRenderParameter(HHSR5Constants.CONTROLLER_NAME,
					HHSR5Constants.SELECT_ORGANIZATION);
		}

		aoResponse.setRenderParameter(HHSConstants.LS_USER_ORG_TYPE,
				lsUserOrgType);
		aoRequest.getPortletSession().setAttribute(
				HHSR5Constants.CUSTOM_ORGANIZATION, lsUserOrg);
		aoRequest.getPortletSession().setAttribute(HHSR5Constants.JSP_NAME,
				HHSR5Constants.SHARE_ORG);
		aoRequest.getPortletSession().setAttribute(
				HHSR5Constants.HOME_PAGE_MANAGE_ORG, false,
				PortletSession.APPLICATION_SCOPE);
		aoResponse.setRenderParameter(HHSR5Constants.HEADER_CLICK,
				ApplicationConstants.TRUE);

	}

	/**
	 * This method will remove session
	 * 
	 * @param aoResourceRequest
	 *            a resource request object
	 * @param aoResourceResponse
	 *            a resource response object
	 * @throws IOException
	 *             when IO Exception occurs
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@ResourceMapping("removeSession")
	protected void handleResourceForRemoveSession(
			ResourceRequest aoResourceRequest,
			ResourceResponse aoResourceResponse) throws IOException,
			ApplicationException {

		try {

			aoResourceRequest.getPortletSession().removeAttribute(
					HHSConstants.FILENET_FILTER_MAP,
					PortletSession.PORTLET_SCOPE);
			aoResourceRequest.getPortletSession()
					.removeAttribute(HHSR5Constants.CLICKED_SEARCH,
							PortletSession.PORTLET_SCOPE);
			aoResourceRequest.getPortletSession().removeAttribute(
					HHSR5Constants.JSP_NAME);
			aoResourceRequest.getPortletSession().removeAttribute(
					HHSR5Constants.FOLDER_PATH);
			aoResourceRequest.getPortletSession().removeAttribute(
					ApplicationConstants.ERROR_MESSAGE,
					PortletSession.PORTLET_SCOPE);
			aoResourceRequest.getPortletSession().removeAttribute(
					ApplicationConstants.ERROR_MESSAGE_TYPE,
					PortletSession.PORTLET_SCOPE);
			String lsUserOrg = HHSPortalUtil.parseQueryString(
					aoResourceRequest, HHSConstants.USER_ORG);
			HashSet<String> lsUserList = (HashSet) aoResourceRequest
					.getPortletSession().getAttribute(
							HHSR5Constants.SHARED_ORG_SESSION_LIST);
			Iterator loIterator = lsUserList.iterator();
			String lsMatchUserOrg = null;
			while (loIterator.hasNext()) {
				lsMatchUserOrg = (String) loIterator.next();
				if (lsMatchUserOrg.contains(lsUserOrg)) {
					loIterator.remove();
					break;
				}
			}
			aoResourceRequest.getPortletSession().setAttribute(
					HHSR5Constants.SHARED_ORG_SESSION_LIST, lsUserList);
		} catch (Exception aoExp) {
			LOG_OBJECT.Error("Exception in removing Session", aoExp);
		}

	}

	/**
	 * This method will find shared documents
	 * 
	 * @param aoResourceRequest
	 *            a resource request object
	 * @param aoResourceResponse
	 *            a resource response object
	 * @return jspname sharedSearchAgency
	 * @throws IOException
	 *             when IO Exception occurs
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@ResourceMapping("findSharedDocs")
	protected ModelAndView handleResourceForFindSharedDocs(
			ResourceRequest aoResourceRequest,
			ResourceResponse aoResourceResponse) throws IOException,
			ApplicationException {
		aoResourceRequest.getPortletSession().removeAttribute(
				HHSR5Constants.JSP_NAME, PortletSession.PORTLET_SCOPE);
		aoResourceRequest.getPortletSession().removeAttribute(
				HHSR5Constants.JSP_NAME, PortletSession.APPLICATION_SCOPE);
		aoResourceRequest.getPortletSession().removeAttribute(
				HHSR5Constants.FOLDER_ID, PortletSession.PORTLET_SCOPE);
		aoResourceRequest.getPortletSession().removeAttribute(
				HHSR5Constants.FOLDER_ID, PortletSession.APPLICATION_SCOPE);
		aoResourceRequest.getPortletSession().setAttribute(
				HHSConstants.ALLOWED_OBJECT_COUNT, 0,
				PortletSession.APPLICATION_SCOPE);
		aoResourceRequest.getPortletSession().setAttribute(
				ApplicationConstants.DOCUMENT_VAULT_SORT_BY_PARAMETER,
				HHSR5Constants.ORGANIZATION_NAME,
				PortletSession.APPLICATION_SCOPE);
		aoResourceRequest.getPortletSession().removeAttribute(
				ApplicationConstants.SESSION_DOCUMENT_LIST);
		aoResourceRequest.getPortletSession().removeAttribute(
				ApplicationConstants.SESSION_DOCUMENT_LIST,
				PortletSession.APPLICATION_SCOPE);
		return new ModelAndView(HHSR5Constants.SHARED_SEARCH_AGENCY);
	}

	// End Release 5
	/**
	 * This method will download all documents
	 * 
	 * @param aoRequest
	 *            a action request object
	 * @param aoResponse
	 *            a action response object
	 * @throws IOException
	 *             when IO exception occurs
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@ActionMapping(params = "submit_action=downloadAll")
	protected void handleActionFordownloadAll(ActionRequest aoRequest,
			ActionResponse aoResponse) throws IOException, ApplicationException {
		HashMap<String, Object> loDowloadMap = new HashMap<String, Object>();
		Boolean lbFlag = false;
		String lsModifiedFrom = aoRequest.getParameter("modifiedfrom5");
		String lsModifiedTo = aoRequest.getParameter("modifiedto5");
		try {
			FileNetOperationsUtils
					.removeMessageFromSessionAndRequest(aoRequest);
			String lsUserId = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
							PortletSession.APPLICATION_SCOPE);
			Channel loChannel = new Channel();
			loDowloadMap.put(ApplicationConstants.DOCUMENT_NAME,
					aoRequest.getParameter("docName"));
			loDowloadMap.put(P8Constants.PROPERTY_PE_UPLOADED_DOC_TYPE,
					aoRequest.getParameter(HHSR5Constants.DOC_TYPE_CITY));
			if (StringUtils.isNotBlank(lsModifiedFrom)) {
				loDowloadMap.put(HHSR5Constants.MODIFIED_DATE_FROM,
						DateUtil.getSqlDate(lsModifiedFrom));
			} else {
				loDowloadMap.put(HHSR5Constants.MODIFIED_DATE_FROM, null);
			}
			if (StringUtils.isNotBlank(lsModifiedTo)) {
				loDowloadMap.put(HHSR5Constants.MODIFIED_DATE_TO,
						DateUtil.getSqlDate(lsModifiedTo));
			} else {
				loDowloadMap.put(HHSR5Constants.MODIFIED_DATE_TO, null);
			}
			Map<String, String[]> loMap = aoRequest.getParameterMap();
			String[] loCheckedItem = (String[]) loMap.get("agencyCheckBox");
			StringBuffer lsAgencyList = new StringBuffer();
			if (null != loCheckedItem) {
				for (int i = 0; i < loCheckedItem.length; i++) {
					lsAgencyList.append(loCheckedItem[i]);
					if (i != loCheckedItem.length - 1) {
						lsAgencyList.append(HHSR5Constants.COMMA);
					}
				}
			}
			loDowloadMap.put(HHSR5Constants.SHARED_WITH,
					lsAgencyList.toString());
			loDowloadMap.put(HHSConstants.BULK_UPLOAD_FILE_STATUS_ID, 2);
			loDowloadMap.put(ApplicationConstants.MODIFIED_BY, lsUserId);
			loDowloadMap.put(HHSConstants.CREATED_BY, lsUserId);
			loChannel.setData(HHSR5Constants.DOWNLOAD_MAP, loDowloadMap);
			HHSTransactionManager.executeTransaction(loChannel,
					HHSR5Constants.DOWNLOAD_ALL_DB,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			lbFlag = (Boolean) loChannel
					.getData(HHSConstants.INVOICE_LB_STATUS);
			aoResponse.setRenderParameter(HHSR5Constants.DB_FLAG,
					lbFlag.toString());
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION,
					HHSR5Constants.DOWNLOAD);
		} catch (ApplicationException loAppEx) {
			try {
				setErrorMessageInResponse(aoRequest, aoResponse,
						aoRequest.getPortletSession(), HHSR5Constants.TRUE,
						HHSR5Constants.EMPTY_STRING, loAppEx);
			} catch (IOException aoIoExp) {
				LOG_OBJECT.Error("IOException during file upload", aoIoExp);
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION,
						ApplicationConstants.ERROR);
			}
		} catch (Exception aoExp) {
			LOG_OBJECT.Error("Exception in Creating folder Document Vault",
					aoExp);
			throw new ApplicationException(
					"Internal Error Occured While Processing Your Request",
					aoExp);
		}
	}

	/**
	 * This method will download all documents
	 * 
	 * @param aoRequest
	 *            a render request object
	 * @param aoResponse
	 *            a render response object
	 * @return jsp name sharedSearchAgency
	 */
	@RenderMapping(params = "render_action=downloadAll")
	protected ModelAndView handleRenderForDownloadAll(RenderRequest aoRequest,
			RenderResponse aoResponse) {
		try {
			String lsDBFlag = aoRequest.getParameter(HHSR5Constants.DB_FLAG);
			if (null != lsDBFlag && !lsDBFlag.isEmpty()
					&& lsDBFlag.equalsIgnoreCase(HHSR5Constants.TRUE)) {
				aoRequest
						.setAttribute(
								ApplicationConstants.ERROR_MESSAGE,
								"Your download request is in progress. You will receive an email when the files are ready for download");
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
						HHSR5Constants.AS_PASSED);
			}
		} catch (Exception loAppEx) {
			LOG_OBJECT.Error("Exception occured in documentUploadAction",
					loAppEx);
		}
		return new ModelAndView(HHSR5Constants.SHARED_SEARCH_AGENCY);
	}

	/**
	 * This method checks if delete is in progress and returns different
	 * statuses based upon the current status of delete process
	 * 
	 * @param aoRequest
	 *            - Action request
	 * @param aoResponse
	 *            - Action response
	 * @throws ApplicationException
	 *             when any exception occurred we wrap it into this custom
	 *             exception
	 */
	@ActionMapping(params = "submit_action=checkParallelProcessProgress")
	protected void checkParallelProcessProgress(ActionRequest aoRequest,
			ActionResponse aoResponse) throws ApplicationException {
		try {
			FileNetOperationsUtils
					.removeMessageFromSessionAndRequest(aoRequest);
			DocumentVaultParallelProcessor loDVPP = (DocumentVaultParallelProcessor) aoRequest
					.getPortletSession().getAttribute(
							"currentProcessThreadObject");
			String lsAction = HHSPortalUtil.parseQueryString(aoRequest,
					HHSConstants.ACTION);
			String lsClass = PropertyLoader.getProperty(
					HHSR5Constants.DV_PARALLEL_FACTORY_CONFIG, lsAction);
			Class loClass = Class.forName(lsClass);
			DocumentVaultParallelProcessHandler loDocumentVaultParallelProcessHandler = (DocumentVaultParallelProcessHandler) loClass
					.newInstance();
			loDocumentVaultParallelProcessHandler.postProcessHandler(loDVPP,
					aoRequest, aoResponse);
		} catch (Exception loAppEx) {
			LOG_OBJECT.Error(
					"Exception occured in checking Parallel Process progress",
					loAppEx);
		}
	}
	
	/*[Start] R7.2.0 QC8914	Set indicator for Access control	 */
	 /**
     * This method will handle setting indicator setIndecatorForReadOnlyRole ApplicationConstants.ROLE_OBSERVER 
     * for access control.
     * @param List<Document> loContractList
     * @throws ApplicationException
     */ 
	private void setIndecatorForReadOnlyRole(List<Document> loDocumentList, String asUserRole) {
	    if( loDocumentList == null || loDocumentList.isEmpty() == true)  return;

	    for( Document  docObj :   loDocumentList){
	        docObj.setUserSubRole(asUserRole);
	        
	    }
	} 
	  /*[End] R7.2.0 QC8914 Set indicator for Access control     */ 
}