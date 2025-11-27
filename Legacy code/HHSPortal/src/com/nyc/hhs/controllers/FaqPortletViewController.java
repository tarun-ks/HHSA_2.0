package com.nyc.hhs.controllers;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.util.CollectionUtils;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.Document;
import com.nyc.hhs.model.FaqFormBean;
import com.nyc.hhs.model.UserThreadLocal;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.FileNetOperationsUtils;
import com.nyc.hhs.util.PortalUtil;

/**
 * This controller is for the maintenance of faq with major operations like
 * publish, preview of faq
 * 
 */

public class FaqPortletViewController extends GenericPortlet
{
	private static final LogInfo LOG_OBJECT = new LogInfo(FaqPortletViewController.class);

	/**
	 * This method is to render the next page depending on the action, FAQ
	 * preview and publish process
	 * 
	 * @param aoRequest to get screen parameters and next page to be displayed
	 * @param aoResponse setting response parameter for JSP variables
	 * @throws PortletException
	 * @throws IOException
	 */
	@Override
	@SuppressWarnings(
	{ "unchecked" })
	public void doView(RenderRequest aoRequest, RenderResponse aoResponse) throws PortletException, IOException
	{
		long loStartTime = System.currentTimeMillis();
		PortletSession loPortletSessionThread = null;
		String lsUserInSession = null;
		String lsUserIdThreadLocal = null;
		try
		{
			loPortletSessionThread = aoRequest.getPortletSession();
			if (null != loPortletSessionThread)
			{
				lsUserIdThreadLocal = (String) loPortletSessionThread.getAttribute(
						ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			}
			UserThreadLocal.setUser(lsUserIdThreadLocal);
			String lsAction = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.ACTION);
			String lsPageNumber = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.NEXT_PAGE_PARAM);
			String lsUserType = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.MAINTENANCE_USER_TYPE);
			if (null == lsUserType
					&& null != loPortletSessionThread
					&& loPortletSessionThread.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
							PortletSession.APPLICATION_SCOPE) != null)
			{
				if (null != loPortletSessionThread)
				{
					lsUserInSession = (String) loPortletSessionThread.getAttribute(
							ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
				}
				if (lsUserInSession.equalsIgnoreCase(ApplicationConstants.AGENCY_ORG))
				{
					lsUserType = ApplicationConstants.AGENCY;
				}
				else if ((lsUserInSession.equalsIgnoreCase(ApplicationConstants.PROVIDER_ORG))
						|| lsUserInSession.equalsIgnoreCase(ApplicationConstants.CITY_ORG))
				{
					lsUserType = ApplicationConstants.PROVIDER;
				}

			}

			if (null == aoRequest.getParameter(ApplicationConstants.PUBLISH) && null != lsAction
					&& ApplicationConstants.FAQ.equalsIgnoreCase(lsAction)
					|| ApplicationConstants.PREVIEW.equalsIgnoreCase(lsAction))
			{
				Map<Integer, List<FaqFormBean>> loQuestionListMap = null;
				Channel loChannelobj = new Channel();
				if (null != lsUserType)
				{
					FaqFormBean loFaqFormBean = new FaqFormBean();
					loFaqFormBean.setMsType(lsUserType);
					loChannelobj.setData(ApplicationConstants.FAQ_FORM_BEAN_DATA, loFaqFormBean);
				}
				if (ApplicationConstants.PREVIEW.equalsIgnoreCase(lsAction))
				{
					TransactionManager.executeTransaction(loChannelobj, ApplicationConstants.FAQ_PREVIEW_DISPLAY);
					loQuestionListMap = (HashMap<Integer, List<FaqFormBean>>) loChannelobj
							.getData(ApplicationConstants.FAQ_SUMMARY_MAP);
					aoRequest.setAttribute(ApplicationConstants.PREVIEW_PAGE, ApplicationConstants.PREVIEW_PAGE);
				}
				else
				{
					TransactionManager.executeTransaction(loChannelobj, ApplicationConstants.FAQ_HELP_DISPLAY);
					loQuestionListMap = (HashMap<Integer, List<FaqFormBean>>) loChannelobj
							.getData(ApplicationConstants.FAQ_SUMMARY_MAPFOR_HELP);
				}
				aoRequest
						.setAttribute(ApplicationConstants.INCLUDE_FAQ, ApplicationConstants.PORTLET_FAQHELP_FA_QS_JSP);
				aoRequest.setAttribute(ApplicationConstants.USER_TYPE, lsUserType);
				aoRequest.setAttribute(ApplicationConstants.QUESTION_LIST_MAP1, loQuestionListMap);

			}

			if (!ApplicationConstants.PREVIEW.equalsIgnoreCase(lsAction)
					&& aoRequest.getParameter(ApplicationConstants.PUBLISH) != null)
			{
				String lsPublishMsg = (String) aoRequest.getParameter(ApplicationConstants.PUBLISH);
				loPortletSessionThread.setAttribute(ApplicationConstants.PUBLISH, lsPublishMsg,
						PortletSession.APPLICATION_SCOPE);
			}

			if (ApplicationConstants.HELP_DOCUMENTS_PARAM.equalsIgnoreCase(lsAction))
			{
				List<Document> loHelpDocs = getHelpDocumentList(aoRequest, lsPageNumber,
						ApplicationConstants.PROVIDER_ORG);
				/*[Start]  R9.6.2  QC9696*/
				aoRequest.getPortletSession().setAttribute(ApplicationConstants.HELP_DOCUMENT_LIST, loHelpDocs, PortletSession.APPLICATION_SCOPE);
				LOG_OBJECT.Info("save Document List in Session on Application scope at displaySuccess"+loHelpDocs);
				/*[End]  R9.6.2  QC9696*/
				aoRequest.setAttribute(ApplicationConstants.HELP_DOCUMENT_LIST, loHelpDocs);
				aoRequest.setAttribute(ApplicationConstants.INCLUDE_HELP_PAGE, ApplicationConstants.HELP_DOCUMENTS);
			}
			else if (ApplicationConstants.SAMPLE_DOCUMENT_PARAM.equalsIgnoreCase(lsAction))
			{
				List<Document> loSampleDocs = fetchDocumentDetails(aoRequest, lsPageNumber);
				/*[Start]  R9.7.1  QC9716*/
				aoRequest.getPortletSession().setAttribute(ApplicationConstants.SAMPLE_DOCUMENTS_LIST, loSampleDocs, PortletSession.APPLICATION_SCOPE);
				LOG_OBJECT.Info("save Document List in Session on Application scope at displaySuccess"+loSampleDocs);
				/*[End]  R9.7.1  QC9716*/
				
				aoRequest.setAttribute(ApplicationConstants.SAMPLE_DOCUMENTS_LIST, loSampleDocs);
				aoRequest.setAttribute(ApplicationConstants.INCLUDE_SAMPLE_HELP, ApplicationConstants.SAMPLE_DOCUMENTS);
			}
			else if (ApplicationConstants.HELP_DOCUMENTS_AGENCY_PARAM.equalsIgnoreCase(lsAction))
			{
				List<Document> loHelpDocs = getHelpDocumentList(aoRequest, lsPageNumber,
						ApplicationConstants.AGENCY_ORG);
				/*[Start]  R9.6.2  QC9696*/
				aoRequest.getPortletSession().setAttribute(ApplicationConstants.HELP_DOCUMENT_LIST, loHelpDocs, PortletSession.APPLICATION_SCOPE);
				LOG_OBJECT.Info("save Document List in Session on Application scope at displaySuccess"+loHelpDocs);
				/*[End]  R9.6.2  QC9696*/
				aoRequest.setAttribute(ApplicationConstants.HELP_DOCUMENT_LIST, loHelpDocs);
				aoRequest.setAttribute(ApplicationConstants.INCLUDE_AGENCY_HELP, ApplicationConstants.HELP_DOCUMENTS_AGENCY);
			}
			long loEndTimeTime = System.currentTimeMillis();
			LOG_OBJECT.Debug("TIME TAKEN for execution of render Method in FaqPortletViewController = "
					+ (loEndTimeTime - loStartTime));
			UserThreadLocal.unSet();
			PortletRequestDispatcher loPortletRequestDispatcher = getPortletContext().getRequestDispatcher(
					ApplicationConstants.HELP_FAQ);
			loPortletRequestDispatcher.include(aoRequest, aoResponse);
		}
		// Catch any application exception thrown from service layer and set the
		// appropriate message to display on jsp
		catch (ApplicationException aoAppEx)
		{
			String lsErrorMessage = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.ERROR_MESSAGE);
			String lsErrorMessageType = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.ERROR_MESSAGE_TYPE);
			if (lsErrorMessage != null && !lsErrorMessage.isEmpty())
			{
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMessage);
			}
			else
			{
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
						"Error while execution of render Method in FaqPortletViewController");
			}
			if (lsErrorMessageType != null && !lsErrorMessageType.isEmpty())
			{
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, lsErrorMessageType);
			}
			else
			{
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
			}

			LOG_OBJECT.Error("Error while execution of render Method in FaqPortletViewController", aoAppEx);
		}

	}

	/**
	 * This method decide the execution flow for FAQ preview and publish process
	 * <ul>
	 * <li>If the requested action is sampledocument then fetch the sample
	 * document and redirect user to the sample document screen</li>
	 * <li>If the action parameter is filterDocuments then filter the documents
	 * according to the filter criteria selected by the user</li>
	 * <li>If the requested action is helpdocument then fetch the help document
	 * and redirect user to the sample document screen</li>
	 * </ul>
	 * @param aoRequest to get screen parameters and next action to be performed
	 * @param aoResponse decides the next execution flow
	 */
	@Override
	public void processAction(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		String lsUserIdThreadLocal = null;
		Channel loChannelObj = new Channel();
		String lsPageNumber = null;
		String lsRequestingOrgType = null;
		String lsAction = null;
		String lsUserType = null;
		try
		{
			lsPageNumber = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.NEXT_PAGE_PARAM);
			lsRequestingOrgType = PortalUtil.parseQueryString(aoRequest,
					ApplicationConstants.ORGANIZATION_TYPE_REQUESTING_FOR);
			UserThreadLocal.setUser(lsUserIdThreadLocal);
			lsAction = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.ACTION);
			lsUserType = aoRequest.getParameter(ApplicationConstants.PUBLISH_ACTION_ATTRIBUTE);
			/**
			 * This condition evaluates the user type (provider or agency) for
			 * the publish event on the screen
			 */
			if (lsUserType != null && !lsUserType.isEmpty())
			{
				FaqFormBean loFaqFormBean = new FaqFormBean();
				loFaqFormBean.setMsType(lsUserType);
				loChannelObj.setData(ApplicationConstants.ARGUMENT_FAQ_FORM_BEAN_DATA, loFaqFormBean);
				createAuditHashMap(aoRequest, aoResponse, loChannelObj, lsUserType);
				TransactionManager.executeTransaction(loChannelObj, ApplicationConstants.FAQ_PUBLISH);
				aoResponse.setRenderParameter(ApplicationConstants.PUBLISH,
						ApplicationConstants.SUCCESS_STRING_WITH_COLON + lsUserType);
			}
			// if the action parameter is equal to sample documents
			else if (ApplicationConstants.SAMPLE_DOCUMENT_PARAM.equalsIgnoreCase(lsAction))
			{
				aoResponse.setRenderParameter(ApplicationConstants.NEXT_PAGE_PARAM, lsPageNumber);
				aoResponse.setRenderParameter(ApplicationConstants.ACTION, lsAction);
			}
			// if the action parameter is equal to help documents
			else if (ApplicationConstants.HELP_DOCUMENTS_PARAM.equalsIgnoreCase(lsAction))
			{
				aoResponse.setRenderParameter(ApplicationConstants.NEXT_PAGE_PARAM, lsPageNumber);
				aoResponse.setRenderParameter(ApplicationConstants.ORGANIZATION_TYPE_REQUESTING_FOR,
						lsRequestingOrgType);
				aoResponse.setRenderParameter(ApplicationConstants.ACTION, lsAction);
			}

		}
		// Catch the application exception thrown by transaction and set the
		// error message
		// in request object and pass to jsp
		catch (ApplicationException aoAppEx)
		{
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE,
					"Error occured while fetching the help documents");
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE);
			LOG_OBJECT.Error("ApplicationException occured in FaqPortletViewController process action", aoAppEx);
		}

	}

	/**
	 * This method is used to get the help documents and display it to the user
	 * <ul>
	 * <li>Set the required parameters name we need to display</li>
	 * <li>If user has selected any filter criteria the n add it into the filter
	 * map</li>
	 * <li>Set the parameter name in the sorted order to sort documents
	 * accordingly</li>
	 * <li>Execute transaction to get the document details from the filenet</li>
	 * <li>Set the fetched document details list into request parameter and
	 * redirect user to the desired page</li>
	 * </ul>
	 * @param aoRequest render request object
	 * @param asNextPage next desired page
	 * @param asRequestingOrgType The organization type requesting for
	 * @return list of document beans
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public List<Document> getHelpDocumentList(RenderRequest aoRequest, String asNextPage, String asRequestingOrgType)
	{
		HashMap loHMReqProps = new HashMap();
		List<Document> loHelpDocumentList = new ArrayList<Document>();
		ArrayList<String> loHelpCategoryList = null;
		Document loDocumentBean = new Document();
		PortletSession loPortletSession = null;
		P8UserSession loUserSession = null;
		String lsHelpCategory = null;
		HashMap loFilterProps = new HashMap();
		String lsUserOrgType = null;
		try
		{
			loPortletSession = aoRequest.getPortletSession();
			loUserSession = (P8UserSession) loPortletSession.getAttribute(ApplicationConstants.FILENET_SESSION_OBJECT,
					PortletSession.APPLICATION_SCOPE);
			lsUserOrgType = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
					PortletSession.APPLICATION_SCOPE);
			loHMReqProps.put(P8Constants.PROPERTY_CE_DOCUMENT_ID, HHSR5Constants.DOCUMENT_CLASS_ALIAS);
			loHMReqProps.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, HHSR5Constants.DOCUMENT_CLASS_ALIAS);
			loHMReqProps.put(P8Constants.PROPERTY_CE_DOCUMENT_DESCRIPTION, HHSR5Constants.DOCUMENT_CLASS_ALIAS);
			loHMReqProps.put(P8Constants.PROPERTY_CE_HELP_CATEGORY, HHSR5Constants.DOCUMENT_CLASS_ALIAS);
			lsHelpCategory = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.FILTER_DOC_CATEGORY);
			// if the user has select something in the filter dropdown
			if (null != lsHelpCategory && !lsHelpCategory.isEmpty())
			{
				loDocumentBean.setHelpCategory(lsHelpCategory);
				loFilterProps.put(P8Constants.PROPERTY_CE_HELP_CATEGORY, lsHelpCategory);
				aoRequest.setAttribute(ApplicationConstants.FILTER_LABEL, ApplicationConstants.FILTERED);
			}
			else
			{
				aoRequest.setAttribute(ApplicationConstants.FILTER_LABEL, ApplicationConstants.FILTER_ITEMS);
			}
			if (null != asRequestingOrgType && asRequestingOrgType.equalsIgnoreCase(ApplicationConstants.PROVIDER_ORG))
			{
				loHelpCategoryList = FileNetOperationsUtils.getHelpCategory(lsUserOrgType,
						ApplicationConstants.DOCUMENT_TYPE_HELP, ApplicationConstants.DOCUMENT_TYPE_PROVIDER_HELP);

				loFilterProps.put(P8Constants.PROPERTY_CE_IS_PROVIDER_HELP_DOC, Boolean.TRUE);
			}
			else if (null != asRequestingOrgType
					&& asRequestingOrgType.equalsIgnoreCase(ApplicationConstants.AGENCY_ORG))
			{
				loHelpCategoryList = FileNetOperationsUtils.getHelpCategory(lsUserOrgType,
						ApplicationConstants.DOCUMENT_TYPE_HELP, ApplicationConstants.DOCUMENT_TYPE_AGENCY_HELP);
				loFilterProps.put(P8Constants.PROPERTY_CE_IS_AGENCY_HELP_DOC, Boolean.TRUE);
			}
			loFilterProps.put(P8Constants.PROPERTY_CE_IS_CURRENT_VERSION, Boolean.TRUE);
			loFilterProps.put(P8Constants.PROPERTY_CE_IS_CURRENT_VERSION, Boolean.TRUE);
			HashMap loOrderByMap = new HashMap();
			loOrderByMap.put(1, P8Constants.PROPERTY_CE_HELP_CATEGORY + " " + ApplicationConstants.SORT_ASCENDING + ","
					+ HHSR5Constants.DOCUMENT_CLASS_ALIAS);
			loOrderByMap.put(2, P8Constants.PROPERTY_CE_DOCUMENT_TITLE + " " + ApplicationConstants.SORT_ASCENDING
					+ "," + HHSR5Constants.DOCUMENT_CLASS_ALIAS);
			Channel loChannel = new Channel();
			loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
			loChannel.setData(ApplicationConstants.ORDER_BY_MAP, loOrderByMap);
			// if the user clicked on the page number or next
			if (asNextPage == null || asNextPage.isEmpty())
			{
				FileNetOperationsUtils.reInitializePageIterator(loPortletSession, loUserSession);
			}
			
			// Added extra Parameter for Release 5
			List loDocumentList = FileNetOperationsUtils.getDocumentList(loChannel,
					ApplicationConstants.DOCUMENT_TYPE_HELP, loHMReqProps, loFilterProps, true, "");
			FileNetOperationsUtils.setReqRequestParameter(loPortletSession, aoRequest, loUserSession, asNextPage, null,
					null, null);
			if (!CollectionUtils.isEmpty(loDocumentList))
			{
				Iterator loIter = loDocumentList.iterator();
				while (loIter.hasNext())
				{
					Document loDocument = new Document();
					Map loDocProps = (HashMap) loIter.next();
					loDocument.setDocName((String) loDocProps.get(P8Constants.PROPERTY_CE_DOCUMENT_TITLE));
					loDocument.setDocumentDescription((String) loDocProps
							.get(P8Constants.PROPERTY_CE_DOCUMENT_DESCRIPTION));
					loDocument.setHelpCategory((String) loDocProps.get(P8Constants.PROPERTY_CE_HELP_CATEGORY));
					loDocument.setDocumentId(loDocProps.get(P8Constants.PROPERTY_CE_DOCUMENT_ID).toString());
					loHelpDocumentList.add(loDocument);
				}
			}
			loDocumentBean.setHelpCategoryList(loHelpCategoryList);
			loPortletSession.setAttribute(ApplicationConstants.ALERT_VIEW_PAGING_PAGE_INDEX, asNextPage,
					PortletSession.APPLICATION_SCOPE);
			loPortletSession.setAttribute(
					ApplicationConstants.ALERT_VIEW_PAGING_RECORDS,
					loUserSession.getTotalPageCount()
							* Integer.valueOf(String.valueOf(loPortletSession.getAttribute(
									ApplicationConstants.ALLOWED_OBJECT_COUNT, PortletSession.APPLICATION_SCOPE))),
					PortletSession.APPLICATION_SCOPE);
			aoRequest.setAttribute(ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_PARAMETER, loDocumentBean);
		}

		// Catch the application exception thrown by transaction and set the
		// error message
		// in request object and pass to jsp
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured while fetching the help documents", aoAppEx);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
					"Error occured while fetching the help documents");
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
		// Catch the exception thrown by transaction and set the
		// error message
		// in request object and pass to jsp
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error occured while fetching the help documents", aoExp);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
					"Error occured while fetching the help documents");
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
		return loHelpDocumentList;
	}

	/**
	 * this method is used to fetch the sample type document to display it to
	 * the user
	 * <ul>
	 * <li>Set the required parameters name we need to display</li>
	 * <li>Set the parameter name in the sorted order to sort documents
	 * accordingly</li>
	 * <li>Execute transaction to get the document details from the filenet</li>
	 * <li>Set the fetched document details list into request parameter and
	 * redirect user to the desired page</li>
	 * </ul>
	 * @param aoRequest render request object
	 * @param asNextPage next page value
	 * @return list of sample documents detail
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private List<Document> fetchDocumentDetails(RenderRequest aoRequest, String asNextPage)
	{
		Document loDocument = null;
		Document loDocumentBean = new Document();
		HashMap loReqProps = new HashMap();
		HashMap loOrderByMap = new HashMap();
		HashMap loFilterProps = new HashMap();
		List<Document> loSampleDocumentList = new ArrayList<Document>();
		try
		{
			PortletSession loPortletSession = aoRequest.getPortletSession();
			P8UserSession loUserSession = (P8UserSession) loPortletSession.getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			String lsDocCategory = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.FILTER_DOC_CATEGORY);
			String lsDocType = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.FILTER_DOC_TYPE);
			loReqProps.put(P8Constants.PROPERTY_CE_SAMPLE_CATEGORY, HHSR5Constants.DOCUMENT_CLASS_ALIAS);
			loReqProps.put(P8Constants.PROPERTY_CE_SAMPLE_TYPE, HHSR5Constants.DOCUMENT_CLASS_ALIAS);
			loReqProps.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, HHSR5Constants.DOCUMENT_CLASS_ALIAS);
			//Release 5 starts
			loReqProps.put(P8Constants.PROPERTY_CE_DOCUMENT_ID, HHSR5Constants.DOCUMENT_CLASS_ALIAS);
			//Release 5 end
			if (null != lsDocCategory && !lsDocCategory.isEmpty())
			{
				loFilterProps.put(P8Constants.PROPERTY_CE_SAMPLE_CATEGORY, lsDocCategory);
				loDocumentBean.setDocCategory(lsDocCategory);
				aoRequest.setAttribute(ApplicationConstants.FILTER_LABEL, ApplicationConstants.FILTERED);
			}
			if (null != lsDocType && !lsDocType.isEmpty())
			{
				loFilterProps.put(P8Constants.PROPERTY_CE_SAMPLE_TYPE, lsDocType);
				loDocumentBean.setDocType(lsDocType);
				aoRequest.setAttribute(ApplicationConstants.FILTER_LABEL, ApplicationConstants.FILTERED);

			}
			if ((null == lsDocCategory || lsDocCategory.isEmpty()) && (null == lsDocType || lsDocType.isEmpty()))
			{
				aoRequest.setAttribute(ApplicationConstants.FILTER_LABEL, ApplicationConstants.FILTER_ITEMS);
			}	
			//Release 5 starts
			loFilterProps.put(HHSConstants.SELECT_ALL_FLAG, HHSR5Constants.TRUE);
			loFilterProps.put(HHSR5Constants.SELECT_VAULT, HHSR5Constants.TRUE);
			loFilterProps.put(HHSR5Constants.FOLDER_PATH, "/City/Document Vault");
			//Release 5 end
			loFilterProps.put(P8Constants.PROPERTY_CE_IS_CURRENT_VERSION, Boolean.TRUE);
			loOrderByMap.put(1, P8Constants.PROPERTY_CE_SAMPLE_CATEGORY + " " + ApplicationConstants.SORT_ASCENDING
					+ "," + HHSR5Constants.DOCUMENT_CLASS_ALIAS);

			loOrderByMap.put(2, P8Constants.PROPERTY_CE_DOCUMENT_TITLE + " " + ApplicationConstants.SORT_ASCENDING
					+ "," + HHSR5Constants.DOCUMENT_CLASS_ALIAS);
			Channel loChannel = new Channel();
			loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
			loChannel.setData(ApplicationConstants.ORDER_BY_MAP, loOrderByMap);
			if ((null == asNextPage || asNextPage.isEmpty()) || (null == lsDocCategory || lsDocCategory.isEmpty()))
			{
				FileNetOperationsUtils.reInitializePageIterator(loPortletSession, loUserSession);
			}
			
			// Added extra Parameter for Release 5
			List loDocumentList = FileNetOperationsUtils.getDocumentList(loChannel, ApplicationConstants.DOC_SAMPLE,
					loReqProps, loFilterProps, true, "");
			FileNetOperationsUtils.setReqRequestParameter(loPortletSession, aoRequest, loUserSession, asNextPage, null,
					null, null);
			
			if (!CollectionUtils.isEmpty(loDocumentList))
			{
				Iterator loIter = loDocumentList.iterator();
				while (loIter.hasNext())
				{
					loDocument = new Document();
					Map loDocProps = (HashMap) loIter.next();
					loDocument.setSampleCategory((String) loDocProps.get(P8Constants.PROPERTY_CE_SAMPLE_CATEGORY));
					loDocument.setSampleType((String) loDocProps.get(P8Constants.PROPERTY_CE_SAMPLE_TYPE));
					//Release 5 starts
					loDocument.setDocumentId(loDocProps.get(P8Constants.PROPERTY_CE_DOCUMENT_ID).toString());
					//Release 5 end
					loDocument.setDocName((String) loDocProps.get(P8Constants.PROPERTY_CE_DOCUMENT_TITLE));
					loSampleDocumentList.add(loDocument);
				}
			}
			FileNetOperationsUtils.setDocCategorynDocType(loDocumentBean, lsDocCategory,
					ApplicationConstants.PROVIDER_ORG);
			loPortletSession.setAttribute(ApplicationConstants.ALERT_VIEW_PAGING_PAGE_INDEX, asNextPage,
					PortletSession.APPLICATION_SCOPE);
			loPortletSession.setAttribute(
					ApplicationConstants.ALERT_VIEW_PAGING_RECORDS,
					loUserSession.getTotalPageCount()
							* Integer.valueOf(String.valueOf(loPortletSession.getAttribute(
									ApplicationConstants.ALLOWED_OBJECT_COUNT, PortletSession.APPLICATION_SCOPE))),
					PortletSession.APPLICATION_SCOPE);
			aoRequest.setAttribute(ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_PARAMETER, loDocumentBean);
		}
		// Catch the application exception thrown by transaction and set the
		// error message
		// in request object and pass to jsp
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured while fetching the sample documents", aoAppEx);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
					"Error occured while fetching the sample documents");
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
		// Catch the exception thrown by transaction and set the
		// error message
		// in request object and pass to jsp
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error occured while fetching the sample documents", aoExp);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
					"Error occured while fetching the sample documents");
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
		return loSampleDocumentList;
	}

	/**
	 * This method creates Audit HashMap that logs type of event, name, userid,
	 * data, etc..
	 * 
	 * @param aoRequest to get the session and screen parameters
	 * @param aoResponse to set response parameter that is used in jsp
	 * @param aoChannelObj channel object required to execute transaction
	 * @param asUserType states usertype(provider or agency..)
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private void createAuditHashMap(ActionRequest aoRequest, ActionResponse aoResponse, Channel aoChannelObj,
			String asUserType)
	{
		Map loAuditHashMap = new HashMap();
		DateFormat loDteFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Date loDate = new Date();
		String lsLastModifiedDateTaxonomy = loDteFormat.format(loDate);
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_EMAIL_ID, PortletSession.APPLICATION_SCOPE);
		String lsOrgId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
				PortletSession.APPLICATION_SCOPE);
		loAuditHashMap.put("orgId", lsOrgId);
		loAuditHashMap.put("eventName", "FAQ Publish");
		loAuditHashMap.put("eventType", "FAQ");
		loAuditHashMap.put("auditDate", lsLastModifiedDateTaxonomy);
		loAuditHashMap.put("userId", lsUserId);
		loAuditHashMap.put("data", "FAQ published for " + asUserType);
		loAuditHashMap.put("entityType", "FAQ");
		loAuditHashMap.put("EntityIdentifier", "FAQ");
		loAuditHashMap.put("entityId", "1");
		loAuditHashMap.put("providerFlag", "N");
		aoChannelObj.setData("aoAuditDetailMap", loAuditHashMap);
	}

}
