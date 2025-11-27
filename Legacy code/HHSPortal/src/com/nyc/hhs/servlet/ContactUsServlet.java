package com.nyc.hhs.servlet;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jdom.Element;
import org.springframework.util.CollectionUtils;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.ContactUsBean;
import com.nyc.hhs.model.Document;
import com.nyc.hhs.model.UserThreadLocal;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.PortalUtil;
import com.nyc.hhs.util.XMLUtil;

/**
 * This controller is for the ContactUs Link in which user can submit a question
 * against a topic. The question is submitted an launches a FileNetWorkflow.
 */

public class ContactUsServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
	private static final LogInfo LOG_OBJECT = new LogInfo(ContactUsServlet.class);
	private static final String WF_NAME = "WFName";
	private static final String CONTACTLIST_FILE_NET = "contactlistFileNet";
	private static final String WORKFLOWFAIL = "workflowfail";
	private static final String RETURN_CONTACT_US_NUMBER = "ReturnContactUsNumber";
	private static final String HELP_LIST = "helpList";
	private static final String HELP_PAGE = "helpPage";
	private static final String LO_TOPIC_LIST = "loTopicList";
	private static final String INSERT = "insert";
	private static final String PORTLET_FAQHELP_HELPPAGE_JSP = "/portlet/faqhelp/helppage.jsp";
	private static final String PORTLET_CONTACTUS_HELP_FA_QNOTIFICATION_JSP = "/portlet/contactus/helpFAQnotification.jsp";
	private static final String PORTLET_CONTACTUS_CONTACTUS_JSP = "/portlet/contactus/contactus.jsp";
	private static final long serialVersionUID = 1L;
	private static final String THIS_REQUEST_COULD_NOT_BE_COMPLETED = "This request could not be completed. Please try again in a few minutes.";
	private String msTransactionStatus = "";
	private String msTransactionStatusMsg = "";
	private static final String HELP_CATEGORY = "helpCategory";

	/**
	 * This is the non-parameterized constructor
	 */
	public ContactUsServlet()
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
	 * transactions to the end.
	 * 
	 * @param aoRequest HttpServlet request object
	 * @param aoResponse HttpServlet response object
	 * @throws ServletException If an Servlet Exception occurs
	 * @throws IOException If an Input Output Exception occurs
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest aoRequest, HttpServletResponse aoResponse) throws ServletException,
			IOException
	{
		List<ContactUsBean> loTopicList = null;
		String loForwardMapping = ContactUsServlet.PORTLET_CONTACTUS_CONTACTUS_JSP;
		String lsAction = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.ACTION);
		String lsUserId = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID);
		String lsOrganisationId = (String) aoRequest.getSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ORG);
		String lsUserOrgType = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE);
		HttpSession loSession = aoRequest.getSession();
		UserThreadLocal.setUser(lsUserId);
		String lsHelpCategory = null;
		String lsScreenName = null;
		try
		{
			lsHelpCategory = PortalUtil.parseQueryString(aoRequest, HELP_CATEGORY);
			lsScreenName = aoRequest.getParameter("screenName");
			// This condition evaluates if no submit action then topic list will
			// be populated in the topic dropdown combo box.
			if (lsAction == null)
			{
				Channel loChannelobj = new Channel();
				TransactionManager.executeTransaction(loChannelobj, ApplicationConstants.TOPIC_LIST);
				loTopicList = (ArrayList<ContactUsBean>) loChannelobj.getData(ContactUsServlet.LO_TOPIC_LIST);
				aoRequest.setAttribute(ContactUsServlet.LO_TOPIC_LIST, loTopicList);
				if (null != lsHelpCategory && !lsHelpCategory.isEmpty())
				{
					aoRequest.setAttribute(HELP_CATEGORY, lsHelpCategory);
				}
			}
			// This condition evaluates if submit button is clicked then
			// question details entered by the user will be inserted in the
			// database.
			else if (INSERT.equals(lsAction))
			{
				loForwardMapping = launchWorkflowInFilenet(aoRequest, aoResponse, loForwardMapping, lsUserId,
						lsOrganisationId);
			}
			// This condition evalutes if the page specific help page is
			// rendered.
			if (HELP_PAGE.equals(lsAction))
			{
				if (null != lsScreenName && !lsScreenName.isEmpty() && !lsScreenName.equalsIgnoreCase("undefined"))
				{
					lsHelpCategory = getHelpCategoryForScreen(lsScreenName, aoRequest);
				}
				else
				{
					lsHelpCategory = aoRequest.getParameter("helpCategory");
				}
				loForwardMapping = ContactUsServlet.PORTLET_FAQHELP_HELPPAGE_JSP;
				P8UserSession loUserSession = (P8UserSession) loSession
						.getAttribute(ApplicationConstants.FILENET_SESSION_OBJECT);
				reInitializePageIterator(loSession, loUserSession);

				/*[Start]  R9.6.2  QC9696*/
				List<Document> loHelpDocumentList = getHelpDocumentList(loUserSession, lsHelpCategory, lsUserOrgType);

				aoRequest.getSession().setAttribute(ApplicationConstants.HELP_DOCUMENT_LIST, loHelpDocumentList);
		    	LOG_OBJECT.Info("#####  DOC_List1234 !!! :: "+loHelpDocumentList);

                aoRequest.setAttribute(ContactUsServlet.HELP_LIST, loHelpDocumentList );
				/*[End]  R9.6.2  QC9696*/

				if (null != lsHelpCategory && !lsHelpCategory.isEmpty())
				{
					aoRequest.setAttribute(HELP_CATEGORY, lsHelpCategory);
				}
			}
		}
		catch (ApplicationException loAppEx)
		{
			String lsErrorMsg = loAppEx.toString();
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
			LOG_OBJECT.Error("Error occured while executing transaction in doPost of ContactUsServlet ", loAppEx);
		}
		UserThreadLocal.unSet();
		aoResponse.setContentType("text/html");
		RequestDispatcher loDisp = getServletContext().getRequestDispatcher(loForwardMapping);
		loDisp.include(aoRequest, aoResponse);
	}

	/**
	 * This method is used to launch Workflow In Filenet
	 * @param aoRequest
	 * @param aoResponse
	 * @param loforwardMApping
	 * @param asUserId
	 * @param asOrganisationId
	 * @return
	 * @throws ApplicationException
	 */
	private String launchWorkflowInFilenet(HttpServletRequest aoRequest, HttpServletResponse aoResponse,
			String loforwardMApping, String asUserId, String asOrganisationId) throws ApplicationException
	{
		ContactUsBean loContactUsBean = new ContactUsBean();
		String lsTopicName = aoRequest.getParameter("topicName");
		String lsSelectedInputfromquestion = aoRequest.getParameter("selectedInputfromquestion");
		String lsSelectedContact = aoRequest.getParameter("selectedContact");
		String lsTopicTextId = aoRequest.getParameter("topicId");
		loContactUsBean.setMsTopic(lsTopicName);
		loContactUsBean.setMsTopicID(Integer.valueOf(lsTopicTextId));
		loContactUsBean.setMsQuestion(lsSelectedInputfromquestion);
		loContactUsBean.setMsContactMedium(lsSelectedContact);
		loContactUsBean.setMsCreationDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
		loContactUsBean.setMsCreationUser(asUserId);
		loContactUsBean.setMsStatus("open");
		loContactUsBean.setMsOrganisationId(asOrganisationId);
		Channel loChannelobj = new Channel();
		loChannelobj.setData("aoContactUsBean", loContactUsBean);
		createAuditHashMap(aoRequest, aoResponse, loChannelobj);
		TransactionManager.executeTransaction(loChannelobj, ApplicationConstants.CONTACT_US);
		// This method is to launch the workflow in Filenet
		try
		{
			fetchContactIDToFileNet(loChannelobj, aoRequest);
			loforwardMApping = ContactUsServlet.PORTLET_CONTACTUS_HELP_FA_QNOTIFICATION_JSP;
		}
		catch (ApplicationException loAppEx)
		{
			if ("failed".equalsIgnoreCase(msTransactionStatus))
			{
				aoRequest.setAttribute("transactionStatus", msTransactionStatus);
				aoRequest.setAttribute("transactionMessage", msTransactionStatusMsg);
			}
			LOG_OBJECT.Error("Error occured in fetchContactIDToFileNet() in ContactUsServlet  ", loAppEx);
		}
		return loforwardMApping;
	}

	/**
	 * This method will get list of help documents depending upon help category
	 * 
	 * @param aoUserSession a P8UserSession object
	 * @param asHelpCategory a string value of help category
	 * @return a list of help documents based on help category
	 * @throws ApplicationException If an application exception occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public List<Document> getHelpDocumentList(P8UserSession aoUserSession, String asHelpCategory, String asUserOrgType)
			throws ApplicationException
	{

		HashMap loHmReqProps = new HashMap();
		String lsVisibleTo = null;
		// changes in R5
		loHmReqProps.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, "DOC");
		loHmReqProps.put(P8Constants.PROPERTY_CE_HELP_CATEGORY, "DOC");
		loHmReqProps.put(P8Constants.PROPERTY_CE_DOCUMENT_ID, "DOC");
		loHmReqProps.put(P8Constants.PROPERTY_CE_DOCUMENT_DESCRIPTION, "DOC");
		// ends R5 changes
		HashMap loFilterProps = new HashMap();
		loFilterProps.put(P8Constants.PROPERTY_CE_DISPLAY_HELP_ON_APP, true);
		loFilterProps.put(P8Constants.PROPERTY_CE_HELP_CATEGORY, asHelpCategory);
		loFilterProps.put(P8Constants.PROPERTY_CE_IS_CURRENT_VERSION, Boolean.TRUE);
		// Release 5- added check for deleted file
		loFilterProps.put(HHSR5Constants.DELETE_FLAG, 0);
		// Release 5 end
		Channel loChannel = new Channel();
		loChannel.setData("aoFilenetSession", aoUserSession);
		loChannel.setData("docType", ApplicationConstants.DOCUMENT_TYPE_HELP);
		org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENET_EXTENDED_DOC_TYPE);
		String lsXPath = "//" + P8Constants.XML_DOC_ORG_ID_PROPERTY + "[@name=\"" + asUserOrgType + "\"] //"
				+ P8Constants.XML_DOC_CATEGORY_PROPERTY + "[@name=\"" + ApplicationConstants.DOCUMENT_TYPE_HELP
				+ "\"] //" + "HelpCategory[@name=\"" + asHelpCategory + "\"]";
		Element loHelpElement = XMLUtil.getElement(lsXPath, loXMLDoc);
		if (null != loHelpElement)
		{
			lsVisibleTo = loHelpElement.getAttributeValue(ApplicationConstants.VISIBLE_TO);
		}
		if (null != lsVisibleTo && lsVisibleTo.equals(ApplicationConstants.PROVIDER))
		{
			loFilterProps.put(P8Constants.PROPERTY_CE_IS_PROVIDER_HELP_DOC, Boolean.TRUE);
		}
		else if (null != lsVisibleTo && lsVisibleTo.equals(ApplicationConstants.BOTH_AGENCY_PROVIDER))
		{
			loFilterProps.put(P8Constants.PROPERTY_CE_IS_PROVIDER_HELP_DOC, Boolean.TRUE);
			loFilterProps.put(P8Constants.PROPERTY_CE_IS_AGENCY_HELP_DOC, Boolean.TRUE);
		}
		else if (null != lsVisibleTo && lsVisibleTo.equals(ApplicationConstants.AGENCY))
		{
			loFilterProps.put(P8Constants.PROPERTY_CE_IS_AGENCY_HELP_DOC, Boolean.TRUE);
		}
		loChannel.setData("hmReqProps", loHmReqProps);
		loChannel.setData("filterMap", loFilterProps);
		loChannel.setData("orderByMap", null);
		loChannel.setData("includeFilter", true);

		TransactionManager.executeTransaction(loChannel, "displayHelpDocList_filenet");

		List loDocumentList = (List) loChannel.getData(ApplicationConstants.SESSION_DOCUMENT_LIST);
		ArrayList<Document> loHelpDocumentList = new ArrayList<Document>();

		// This condition evaluates if document list fetched from filenet is
		// null or not
		if (!CollectionUtils.isEmpty(loDocumentList))
		{
			Iterator loIter = loDocumentList.iterator();

			while (loIter.hasNext())
			{
				Document loDocument = new Document();
				HashMap loDocProps = (HashMap) loIter.next();
				loDocument.setDocName((String) loDocProps.get(P8Constants.PROPERTY_CE_DOCUMENT_TITLE));
				loDocument
						.setDocumentDescription((String) loDocProps.get(P8Constants.PROPERTY_CE_DOCUMENT_DESCRIPTION));
				loDocument.setDocCategory((String) loDocProps.get(P8Constants.PROPERTY_CE_HELP_CATEGORY));
				// changes in R5
				if (null != loDocProps.get(P8Constants.PROPERTY_CE_DOCUMENT_ID))
				{
					loDocument.setDocumentId(loDocProps.get(P8Constants.PROPERTY_CE_DOCUMENT_ID).toString());
				}
				// Ends r5 changes

				loHelpDocumentList.add(loDocument);
			}
		}

		return loHelpDocumentList;
	}

	/**
	 * This method will re - initialize page iterators
	 * 
	 * @param aoPortletSession a aoPortlet Session object
	 * @param aoUserSession a P8UserSession object
	 */
	private void reInitializePageIterator(HttpSession aoPortletSession, P8UserSession aoUserSession)
	{
		aoUserSession.setPageIterator(null);
		aoUserSession.setPageIteratorForTotal(null);
		aoUserSession.setAllPageMark(null);
		aoUserSession.setNextPageIndex(0);
		aoPortletSession.setAttribute(ApplicationConstants.FILENET_SESSION_OBJECT, aoUserSession);
	}

	/**
	 * This method is to fetch contact us id into file net attribute to launch
	 * work-flow
	 * 
	 * @param aoChannelObj channel object to execute transaction
	 * @param aoRequest a HttpServletRequest request object to get session
	 *            attribute values
	 */
	@SuppressWarnings(
	{ "rawtypes" })
	public void fetchContactIDToFileNet(Channel aoChannelObj, HttpServletRequest aoRequest) throws ApplicationException
	{
		Map<String, String> loContactlistFileNet = new HashMap<String, String>();
		String lsOrgName = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_ORG_NAME);
		String lsOrgId = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG);
		String lsUserId = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID);
		TransactionManager.executeTransaction(aoChannelObj, ApplicationConstants.GET_CONTACT_US_ID_FILENET);
		List loContactUsList = (ArrayList) aoChannelObj.getData("loGetContactUsID");
		Iterator loItr = loContactUsList.iterator();
		while (loItr.hasNext())
		{
			ContactUsBean loValue = (ContactUsBean) loItr.next();
			loContactlistFileNet.put(P8Constants.PROPERTY_PE_PROVIDER_ID, lsOrgId);
			loContactlistFileNet.put(P8Constants.PROPERTY_PE_PROVIDER_NAME, lsOrgName);
			loContactlistFileNet.put(P8Constants.PROPERTY_PE_CONTACT_US_TOPIC, loValue.getMsTopic());
			loContactlistFileNet.put(P8Constants.PROPERTY_PE_LAUNCH_BY, lsUserId);
			loContactlistFileNet.put(P8Constants.PROPERTY_PE_APPLICTION_ID, loValue.getMsSequenceID().toString());
			loContactlistFileNet.put(P8Constants.PROPERTY_PE_TASK_NAME, "Contact Us - " + loValue.getMsTopic());
			aoChannelObj.setData(ContactUsServlet.CONTACTLIST_FILE_NET, loContactlistFileNet);
			aoChannelObj.setData(ContactUsServlet.WF_NAME, P8Constants.PROPERTY_CONTACT_US_WORKFLOW_NAME);
		}
		HttpSession loSession = aoRequest.getSession();
		P8UserSession loUserSession = (P8UserSession) loSession
				.getAttribute(ApplicationConstants.FILENET_SESSION_OBJECT);
		aoChannelObj.setData("aoFilenetSession", loUserSession);
		msTransactionStatus = "failed";
		msTransactionStatusMsg = ContactUsServlet.THIS_REQUEST_COULD_NOT_BE_COMPLETED;

		TransactionManager.executeTransaction(aoChannelObj, ApplicationConstants.LAUNCH_WORKFLOW_CONTACT_US);
		msTransactionStatus = "passed";
		String lsStatusFromFileNet = (String) aoChannelObj.getData(ContactUsServlet.RETURN_CONTACT_US_NUMBER);
		// This condition evaluates whether the WOB number received from filenet
		// is valid.
		if (null == lsStatusFromFileNet || "".equalsIgnoreCase(lsStatusFromFileNet)
				|| lsStatusFromFileNet.length() != 32)
		{
			aoRequest.setAttribute(ContactUsServlet.WORKFLOWFAIL, ContactUsServlet.WORKFLOWFAIL);
		}
	}

	/**
	 * This method create audit hash map to make entry in Audit table
	 * 
	 * @param aoRequest a HttpServletRequest request object to get session
	 *            attribute values
	 * @param aoResponse a HttpServletRequest response object
	 * @param aoChannelObj channel object to execute transaction
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private void createAuditHashMap(HttpServletRequest aoRequest, HttpServletResponse aoResponse, Channel aoChannelObj)
	{
		Map loAuditHashMap = new HashMap();
		DateFormat loDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Date loDate = new Date();
		String lsLastModifiedDateTaxonomy = loDateFormat.format(loDate);

		String lsUserId = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_EMAIL_ID);
		String lsOrgId = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG);
		loAuditHashMap.put("orgId", lsOrgId);
		loAuditHashMap.put("eventName", "submit Contact Us");
		loAuditHashMap.put("eventType", "Contact Us");
		loAuditHashMap.put("auditDate", lsLastModifiedDateTaxonomy);
		loAuditHashMap.put("userId", lsUserId);
		loAuditHashMap.put("data", "Contact Us");
		loAuditHashMap.put("entityType", "Contact Us");
		loAuditHashMap.put("EntityIdentifier", "Contact Us");
		loAuditHashMap.put("entityId", "1");
		loAuditHashMap.put("providerFlag", "N");
		aoChannelObj.setData("aoAuditDetailMap", loAuditHashMap);
	}

	/**
	 * 
	 * @param asScreenName
	 * @param aoRequest
	 * @return
	 * @throws ApplicationException
	 */
	private String getHelpCategoryForScreen(String asScreenName, HttpServletRequest aoRequest)
			throws ApplicationException
	{
		String lsHelpCategory = null;
		org.jdom.Document loXMLDoc = null;
		String lsOrgId = null;
		try
		{
			lsOrgId = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE);
			loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
					ApplicationConstants.HELP_CATEGORY_SCREEN_MAPPING_CONFIG);
			Element loElt = XMLUtil.getElement("//" + P8Constants.XML_DOC_ORG_ID_PROPERTY + "[@name=\"" + lsOrgId
					+ "\"] //HelpCategory[@screenName=\"" + asScreenName + "\"]", loXMLDoc);
			if (null != loElt)
			{
				lsHelpCategory = loElt.getAttributeValue("HelpCategory");
			}
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error Occured While getting help category for screen name", aoExp);
			throw new ApplicationException("Error Occured While getting help category for screen name", aoExp);
		}
		return lsHelpCategory;
	}
}
