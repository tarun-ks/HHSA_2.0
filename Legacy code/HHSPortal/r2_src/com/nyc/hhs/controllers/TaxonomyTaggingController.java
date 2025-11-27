package com.nyc.hhs.controllers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.jdom.Document;
import org.jdom.Element;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.controllers.util.ContractListUtils;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.Procurement;
import com.nyc.hhs.model.TaxonomyTaggingBean;
import com.nyc.hhs.model.TaxonomyTaggingTree;
import com.nyc.hhs.util.ApplicationSession;
import com.nyc.hhs.util.HHSPortalUtil;
import com.nyc.hhs.util.PortalUtil;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.XMLUtil;

/**
 * This controller is for Taxonomy tagging, Accelerator users will be able to
 * set tags for Selected Proposals and released contracts using this controller
 * 
 */
@Controller(value = "taxonomyTagging")
@RequestMapping("view")
public class TaxonomyTaggingController extends BaseControllerSM
{
	/**
	 * This method will provide a LogInfo object for logging purposes
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(TaxonomyTaggingController.class);

	/**
	 * This method will initialize the TaxonomyTagging bean object
	 * 
	 * @return TaxonomyTagging Bean Object
	 */
	@ModelAttribute("TaxonomyTagging")
	public TaxonomyTaggingBean getCommandObject()
	{
		return new TaxonomyTaggingBean();
	}

	/**
	 * This method will initialize the procurement bean object
	 * 
	 * @return Procurement Bean Object
	 */
	@ModelAttribute("Procurement")
	public Procurement getCommandObj()
	{
		return new Procurement();
	}

	/**
	 * <p>
	 * This method is default render method to display S267 screen for city
	 * users (Taxonomy Tagging - Proposals/Contracts List)
	 * </p>
	 * <ul>
	 * <li>For City Users it displays Taxonomy Tagging - Proposals/Contracts
	 * List, which shows - All Proposals in status "Selected" - All Contracts in
	 * status "Registered"</li>
	 * <li>This method was updated in R4</li>
	 * <li>The transaction used: fetchProcurementProposalDetails</li>
	 * </ul>
	 * 
	 * @param aoRequest RenderRequest object
	 * @param aoResponse RenderResponse object
	 * @return loModelAndView ModelAndView as return type with jsp name and data
	 *         to be displayed dynamically
	 */
	@SuppressWarnings("unchecked")
	@RenderMapping
	protected ModelAndView handleRenderRequestInternal(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		/** BEGIN QC 6523 Release 3.7.0   */
		PortletSession loPortletSession = aoRequest.getPortletSession();
		String lsOrgnizationType = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
		List<HashMap<String, String>> loAgencyDetails = (List<HashMap<String, String>>) loPortletSession.getAttribute(
				HHSConstants.AGENCY_DETAILS, PortletSession.APPLICATION_SCOPE);
		/** END QC 6523 Release 3.7.0   */
		
		ModelAndView loModelAndView = new ModelAndView(HHSConstants.TAXONOMY_TAGGING_JSP);
		Map<String, Object> loTaxonomyTaggingMap = new HashMap<String, Object>();
		TaxonomyTaggingBean loTaxonomyTaggingBean = null;
		Channel loChannelObj = new Channel();
		List<TaxonomyTaggingBean> loProcurementProposalList = null;
		String lsTransactionStatus = ApplicationConstants.MESSAGE_FAIL_TYPE;
		Integer loRowCount = HHSConstants.INT_ZERO;
		try
		{
			PortletSession loSession = aoRequest.getPortletSession();
			if (HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.RESET) != null)
			{
				loSession.removeAttribute(HHSConstants.PROCUREMENT_SESSION_BEAN);
			}
			if (HHSConstants.TAXONOMY_TAGGING_PAGE.equalsIgnoreCase(PortalUtil.parseQueryString(aoRequest,
					HHSConstants.NAVIGATE_FROM)))
			{
				loTaxonomyTaggingBean = (TaxonomyTaggingBean) ApplicationSession.getAttribute(aoRequest, true,
						HHSConstants.PROCUREMENT_SESSION_BEAN);
				if (aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE) != null)
				{
					aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
							aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE));
					aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
							aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE_TYPE));
				}
				if (null == loTaxonomyTaggingBean)
				{
					loTaxonomyTaggingBean = new TaxonomyTaggingBean();
				}
				loTaxonomyTaggingBean.setProposalStatusId(String.valueOf(PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_SELECTED)));
				loTaxonomyTaggingBean.setContractStatusId(String.valueOf(PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_CONTRACT_REGISTERED)));

				String lsNextPage = aoRequest.getParameter(HHSConstants.NEXT_PAGE_PARAM);
				// gets the paging/sorting related data in bean
				getPagingParamsTaxonomy(aoRequest.getPortletSession(), loTaxonomyTaggingBean, lsNextPage,
						HHSConstants.TAXONOMY_TAGGING_KEY, loTaxonomyTaggingBean.getSelectedTaxonomy());
				// set data in channel
				loChannelObj.setData(HHSConstants.AO_TAXONOMY_TAGGING_BEAN, loTaxonomyTaggingBean);
				HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.FETCH_PROC_PROP_DETAIL_TRANS);
				// get list of records from channel
				loProcurementProposalList = (List<TaxonomyTaggingBean>) loChannelObj
						.getData(HHSConstants.AO_PROC_PROP_DETAILS);
				// get row count from channel
				loRowCount = (Integer) loChannelObj.getData(HHSConstants.AI_ROW_COUNT);
				loSession.setAttribute(ApplicationConstants.ALERT_VIEW_PAGING_RECORDS,
						((loRowCount == null) ? HHSConstants.INT_ZERO : loRowCount), PortletSession.APPLICATION_SCOPE);
				lsTransactionStatus = ApplicationConstants.MESSAGE_PASS_TYPE;
				loSession.setAttribute(HHSConstants.SORT_TYPE, loTaxonomyTaggingBean.getFirstSortType(),
						PortletSession.APPLICATION_SCOPE);
				loSession.setAttribute(HHSConstants.SORT_BY, loTaxonomyTaggingBean.getSortColumnName(),
						PortletSession.APPLICATION_SCOPE);
				String lsFilteredValue = PortalUtil.parseQueryString(aoRequest, HHSConstants.FILTERED);
				aoRequest.setAttribute(HHSConstants.FILTERED, lsFilteredValue);
				
				/** BEGIN QC 6523 Release 3.7.0   */
				loChannelObj.setData(HHSConstants.ORGTYPE, lsOrgnizationType);
				loChannelObj.setData(HHSConstants.AS_USER_ID, lsUserId);
				
				if (loAgencyDetails == null || loAgencyDetails.isEmpty())
				{
					loAgencyDetails = ContractListUtils.getAgencyDetails(loChannelObj);
					loPortletSession.setAttribute(HHSConstants.AGENCY_DETAILS, loAgencyDetails,
							PortletSession.APPLICATION_SCOPE);
				}
				/** END QC 6523 Release 3.7.0   */
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			setGenericErrorMessage(aoRequest);
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_STATUS, ApplicationConstants.MESSAGE_FAIL_TYPE);
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_MSG, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST);
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occured while fetching proposal and contract list ", aoEx);
			setGenericErrorMessage(aoRequest);
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_STATUS, ApplicationConstants.MESSAGE_FAIL_TYPE);
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_MSG, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST);
		}
		aoRequest.setAttribute(HHSConstants.PROC_PROP_LIST, loProcurementProposalList);
		aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_STATUS, lsTransactionStatus);
		loTaxonomyTaggingMap.put(HHSConstants.TAXONOMY_TAGGING_BEAN, loTaxonomyTaggingBean);
		return loModelAndView.addAllObjects(loTaxonomyTaggingMap);
	}
	
	
	/** BEGIN QC 6523 Release 3.7.0   */
	/**
	 * This method will get the Epin list from the cache when user type three
	 * characters using getEpinList method defined in basecontroller.
	 * 
	 * @param aoRequest a ResourceRequest Object
	 * @param aoResponse a ResourceResponse Object
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             ApplicationException
	 */
	@ResourceMapping("getEpinListResourceUrl")
	public void getEpinListResourceRequest(ResourceRequest aoRequest, ResourceResponse aoResponse)
	{
		try
		{
			getEpinList(aoRequest, aoResponse);
		}
		catch (ApplicationException aoExe)
		{
			LOG_OBJECT.Error("ApplicationException occured in getEpinListResourceRequest method while fetching Epin ",
					aoExe);
		}
		catch (Exception aoExe)
		{
			LOG_OBJECT.Error("Error occured in getEpinListResourceRequest method while fetching Epin  ", aoExe);
		}
	}
	/** END QC 6523 Release 3.7.0   */

	/**
	 * This method will handle sort action from procurement roadmap screen.
	 * 
	 * <ul>
	 * <li>
	 * This method will check for next action parameter and call methods
	 * accordingly.</li>
	 * <li>Get sortType and columnName values by calling parseQueryString()
	 * method from class PortalUtil. Get sorting details by calling method
	 * getSortDetailsFromXML() from class BaseController.</li>
	 * <li>Set sorting and paging parameters in ProcurementFilter bean object by
	 * calling method getSortParams(), getPagingParams() from class
	 * ProcurementController and set this bean to ProcurementFilter Session
	 * object.</li>
	 * <li>This method was updated in R4</li>
	 * </ul>
	 * 
	 * @param aoTaxonomyTaggingBean a taxonomy tagging bean object
	 * @param aoRequest an Action Request object
	 * @param aoResponse an Action Response object
	 */
	@ActionMapping(params = "submit_action=sortTaxonomy")
	protected void actionSortTaxonomyTagging(
			@ModelAttribute("taxonomyTaggingBean") TaxonomyTaggingBean aoTaxonomyTaggingBean, ActionRequest aoRequest,
			ActionResponse aoResponse)
	{
		String lsSortType = null;
		String lsColumnName = null;
		String lsUserOrgType = null;
		try
		{
			lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			lsSortType = PortalUtil.parseQueryString(aoRequest, HHSConstants.SORT_TYPE);
			lsColumnName = PortalUtil.parseQueryString(aoRequest, HHSConstants.COLUMN_NAME);
			getSortDetailsFromXML(lsColumnName, lsUserOrgType,
					PortalUtil.parseQueryString(aoRequest, HHSConstants.SORT_GRID_NAME), aoTaxonomyTaggingBean,
					lsSortType);
			ApplicationSession.setAttribute(aoTaxonomyTaggingBean, aoRequest, HHSConstants.PROCUREMENT_SESSION_BEAN);
			aoResponse.setRenderParameter(HHSConstants.NAVIGATE_FROM, HHSConstants.TAXONOMY_TAGGING_PAGE);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			Map<String, String> loContextMap = new HashMap<String, String>();
			loContextMap.put(HHSConstants.LS_USER_ORG_TYPE, lsUserOrgType);
			loContextMap.put(HHSConstants.LS_SORT_TYPE, lsUserOrgType);
			String lsErrorMsg = "Error occured while sorting proposal and contract list ";
			aoAppEx.setContextData(loContextMap);
			LOG_OBJECT.Error(lsErrorMsg, aoAppEx);
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_STATUS, ApplicationConstants.MESSAGE_FAIL_TYPE);
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_MSG, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occured while sorting proposal and contract list ", aoEx);
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
	}

	/**
	 * This method will handle pagination action from procurement roadmap
	 * screen.
	 * 
	 * <ul>
	 * <li>
	 * This method will check for next action parameter and call methods
	 * accordingly.</li>
	 * <li>set filtered bean in ProcurementFilter Session object.</li>
	 * </ul>
	 * 
	 * @param aoTaxonomyTaggingBean a taxonomy tagging bean object
	 * @param aoRequest an Action Request object
	 * @param aoResponse an Action Response object
	 */
	@ActionMapping(params = "submit_action=fetchActiveProcurements")
	protected void actionPaginateTaggedProcurements(
			@ModelAttribute("TaxonomyTagging") TaxonomyTaggingBean aoTaxonomyTaggingBean, ActionRequest aoRequest,
			ActionResponse aoResponse)
	{
		try
		{
			aoResponse.setRenderParameter(HHSConstants.NEXT_PAGE_PARAM, aoRequest.getParameter(HHSConstants.NEXT_PAGE));
			ApplicationSession.setAttribute(aoTaxonomyTaggingBean, aoRequest, HHSConstants.PROCUREMENT_SESSION_BEAN);
			aoResponse.setRenderParameter(HHSConstants.NAVIGATE_FROM, HHSConstants.TAXONOMY_TAGGING_PAGE);
			if (null != aoRequest.getParameter(HHSConstants.FILTER_ITEM_KEY))
			{
				aoResponse.setRenderParameter(HHSConstants.FILTERED,
						aoRequest.getParameter(HHSConstants.FILTER_ITEM_KEY));
			}
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
			LOG_OBJECT.Error("Error occured while paginating proposal and contract list ", aoEx);
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_STATUS, ApplicationConstants.MESSAGE_FAIL_TYPE);
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_MSG, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST);
		}
	}

	/**
	 * This method will handle filter action from procurement road map screen.
	 * 
	 * <ul>
	 * <li>
	 * This method will check for next action parameter and call methods
	 * accordingly.</li>
	 * <li>Filter and sort values will be available in bean object.</li>
	 * <li>Set the procurement bean in session object</li>;
	 * </ul>
	 * 
	 * @param aoTaxonomyTaggingBean a taxonomy tagging bean object
	 * @param aoRequest an Action Request object
	 * @param aoResponse an Action Response object
	 */
	@ActionMapping(params = "submit_action=filterProcurement")
	protected void actionFilterTaggedProcurements(
			@ModelAttribute("TaxonomyTagging") TaxonomyTaggingBean aoTaxonomyTaggingBean, ActionRequest aoRequest,
			ActionResponse aoResponse)
	{
		try
		{
			// set sort/pagination related settings
			aoTaxonomyTaggingBean.setFirstSort(HHSConstants.TAXONOMY_TAGGING_ACTIVE_FLAG);
			aoTaxonomyTaggingBean.setSecondSort(HHSConstants.AWARD_APPROVAL_DATE);
			aoTaxonomyTaggingBean.setFirstSortType(HHSConstants.ASCENDING);
			aoTaxonomyTaggingBean.setSecondSortType(HHSConstants.DESCENDING);
			aoTaxonomyTaggingBean.setSortColumnName(HHSConstants.IS_TAGGED);
			aoTaxonomyTaggingBean.setFirstSortDate(false);
			aoTaxonomyTaggingBean.setSecondSortDate(true);
			aoResponse.setRenderParameter(HHSConstants.NAVIGATE_FROM, HHSConstants.TAXONOMY_TAGGING_PAGE);
			ApplicationSession.setAttribute(aoTaxonomyTaggingBean, aoRequest, HHSConstants.PROCUREMENT_SESSION_BEAN);
			aoResponse.setRenderParameter(HHSConstants.FILTERED, HHSConstants.FILTERED);
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
			LOG_OBJECT.Error("Error occured while filtering proposal and contract list ", aoEx);
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_STATUS, ApplicationConstants.MESSAGE_FAIL_TYPE);
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_MSG, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST);
		}
	}

	/**
	 * This method binds the date field on the jsp page with the date fields in
	 * the bean. also this method checks for the format and number of character
	 * to perform the server side validations. and if the format or number of
	 * character differs from the one specified in this method the errors will
	 * get stored in the resultBinder.
	 * 
	 * @param aoBinder - WebDataBinder
	 */
	@InitBinder
	public void initBinder(WebDataBinder aoBinder)
	{
		SimpleDateFormat loFormat = new SimpleDateFormat(HHSConstants.MMDDYYFORMAT);
		loFormat.setLenient(true);
		aoBinder.registerCustomEditor(Date.class, new CustomDateEditor(loFormat, false, HHSConstants.INT_TEN));
	}

	/**
	 * This method fetch all data from taxonomy master and transform this data
	 * to XML
	 * 
	 * <ul>
	 * <li>1. Fetch data from Taxonomy Master table.</li>
	 * <li>2. Transform data to xml.</li>
	 * <li>3. return formatted output to calling method</li>
	 * </ul>
	 * 
	 * @param aoRequest - a Resource Request object
	 * @param aoResponse - a Resource Response object
	 * @return lsFormattedOutput - formatted String
	 * @throws ApplicationException when exception occurs
	 */
	private String navigateToTaxonomyServiecFilter(ResourceRequest aoRequest, ResourceResponse aoResponse)
			throws ApplicationException
	{
		String lsFormattedOutput = HHSConstants.EMPTY_STRING;
		try
		{
			Document loTaxonomyDOM = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
					ApplicationConstants.TAXONOMY_ELEMENT);
			String lsXpath = HHSConstants.XPATH_NAME_SERVICE_FUNCTION;
			lsFormattedOutput = processTaxonomyDom(aoRequest, loTaxonomyDOM, lsXpath, false);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT
					.Error("Error occured while fetching taxonomy master data and tranforming that data into html : ",
							aoAppEx);
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error(
					"Error occured while fetching taxonomy master data and tranforming that data into html : ", aoEx);
			throw new ApplicationException(
					"Error occured while fetching taxonomy master data and tranforming that data into html ", aoEx);
		}
		return lsFormattedOutput;
	}

	/**
	 * This method processes taxonomy dom and fetch taxonomy data based on Xpath
	 * <ul>
	 * <li>1. fetch the required elements from dom based on xpath</li>
	 * <li>2. Convert the fetched element to dom</li>
	 * <li>3. Transform the bean to tree structure</li>
	 * </ul>
	 * 
	 * @param aoRequest - Resource Request
	 * @param aoTaxonomyDOM - Complete taxonomy dom from cache
	 * @param asXpath - xpath corresponding to which elements needs to be
	 *            fetched
	 * @param abIsService - flag depecting if invoked for fetching service
	 * @return Formatted output of tree structure - string
	 * @throws ApplicationException when exception occurs
	 */
	@SuppressWarnings("unchecked")
	private String processTaxonomyDom(ResourceRequest aoRequest, Document aoTaxonomyDOM, String asXpath,
			boolean abIsService) throws ApplicationException
	{
		String lsFormattedOutput = HHSConstants.EMPTY_STRING;
		ByteArrayOutputStream loMainTreeBaos = new ByteArrayOutputStream();
		try
		{
			List<Element> loElementList = XMLUtil.getElementList(asXpath, aoTaxonomyDOM);
			Element loRootElement = new Element(ApplicationConstants.TAXONOMY_ELEMENT);
			Document loDom = new Document(loRootElement);
			// convert list of jdom elements to a single dom based on
			// service/other data
			for (Element loElement : loElementList)
			{
				if (!abIsService && loElement.getChildren() != null)
				{
					for (Element loElementChild : (List<Element>) loElement.getChildren())
					{
						loRootElement.addContent(((Element) loElementChild.clone()).detach());
					}
				}
				else if (abIsService)
				{
					loRootElement.addContent(((Element) loElement.clone()).detach());
				}
			}
			String lsTree = (String) XMLUtil.getXMLAsString(loDom);
			TransformerFactory loFactory = TransformerFactory.newInstance();
			Transformer loMainTreeTransformer = null;
			PortletSession loPortletSession = aoRequest.getPortletSession();
			PortletContext loContext = loPortletSession.getPortletContext();
			// get taxonomy xsl and transform the xml
			String lsXsltRealPath = loContext.getRealPath(HHSConstants.TAXONOMY_XSL_PATH);
			loMainTreeTransformer = loFactory.newTransformer(new StreamSource(new File(lsXsltRealPath)));
			StreamSource loXmlSource = new StreamSource(new StringReader(lsTree));
			loMainTreeTransformer.transform(loXmlSource, new StreamResult(loMainTreeBaos));
			lsFormattedOutput = loMainTreeBaos.toString();
		}
		// handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			throw new ApplicationException("Error Occured while transforming the xml", aoExp);
		}
		finally
		{
			try
			{
				if (null != loMainTreeBaos)
				{
					loMainTreeBaos.close();
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
		return lsFormattedOutput;
	}

	/**
	 * This method fetch all data from taxonomy master and transform this data
	 * to XML for taxonomy tree generation
	 * 
	 * <ul>
	 * <li>1. Fetch data from Taxonomy Master table.</li>
	 * <li>2. Transform data to xml.</li>
	 * <li>3. return formatted output to calling method</li>
	 * </ul>
	 * 
	 * @param aoRequest - a Resource Request object
	 * @param aoResponse - a Resource Response object
	 * @return lsFormattedOutput - formatted String
	 * @throws ApplicationException when exception occurs
	 */
	private String navigateToTaxonomyExceptServiecFilter(ResourceRequest aoRequest, ResourceResponse aoResponse)
			throws ApplicationException
	{
		String lsFormattedOutput = HHSConstants.EMPTY_STRING;
		try
		{
			Document loTaxonomyDOM = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
					ApplicationConstants.TAXONOMY_ELEMENT);
			String lsXpath = HHSConstants.XPATH_NAME_NOT_SERVICE_FUNCTION;
			lsFormattedOutput = processTaxonomyDom(aoRequest, loTaxonomyDOM, lsXpath, true);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT
					.Error("Error occured while fetching taxonomy master data and tranforming that data into html : ",
							aoAppEx);
			throw aoAppEx;
		}
		return lsFormattedOutput;
	}

	/**
	 * This method used to edit taxonomy tags for the selected service/function
	 * 
	 * <ul>
	 * <li>1. Get proposalId, procurementId and serviceElementId id from the
	 * request parameter.</li>
	 * <li>2. Set proposalId, procurementId and serviceElementId in the channel
	 * object.</li>
	 * <li>3. Execute fetchTaxonomyTaggingList transaction to fetch taxonomy
	 * tags for the selected service/function.</li>
	 * <li>4. Get loTaxonomyTaggingList from the channel object for the
	 * remaining tags linked to the service/function.</li>
	 * <li>5. Set TaxonomyTaggingList in the session.</li>
	 * <li>6. render add newtag jsp</li>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * @param aoRequest - a Resource Request object
	 * @param aoResponse - a Resource Response object
	 * @return ModelAndView - render addnewtag jsp
	 */
	@SuppressWarnings("unchecked")
	@ResourceMapping("editTaxonomyTagsUrl")
	public ModelAndView editTaxonomyTags(ResourceRequest aoRequest, ResourceResponse aoResponse)
	{
		ModelAndView loModelAndView = new ModelAndView(HHSConstants.TAXONOMY_TAGGING_ADD_NEW_TAG);
		Channel loChannelObj = new Channel();

		String lsProcurementId = aoRequest.getParameter(HHSConstants.PROCUREMENT_ID);
		String lsContractId = aoRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW);
		String lsProposalId = aoRequest.getParameter(HHSConstants.PROPOSAL_ID);
		TaxonomyTaggingBean loTaxonomyTaggingBean = null;
		Map<String, Object> loTaxonomyTaggingMap = new HashMap<String, Object>();

		try
		{
			loTaxonomyTaggingMap.put(HHSConstants.PROPOSAL_ID, lsProposalId);
			loTaxonomyTaggingMap.put(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			loTaxonomyTaggingMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, lsContractId);
			loChannelObj.setData(HHSConstants.TAXONOMY_TAGGING_MAP, loTaxonomyTaggingMap);
			// fetch list of taxonomy tagged items
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.FETCH_TAXONOMYTAGGING_LIST);
			// fetch list of taxonomy tagged items from channel
			List<TaxonomyTaggingBean> loTaxonomyTaggingList = (List<TaxonomyTaggingBean>) loChannelObj
					.getData(HHSConstants.AO_TAXONOMY_TAGGING_LIST);
			loTaxonomyTaggingBean = new TaxonomyTaggingBean();
			loTaxonomyTaggingBean.setProcurementId(lsProcurementId);
			loTaxonomyTaggingBean.setProposalId(lsProposalId);
			loTaxonomyTaggingBean.setContractId(lsContractId);
			aoRequest.setAttribute(HHSConstants.TAXONOMY_TAGGING_LIST, loTaxonomyTaggingList);
			aoRequest.setAttribute(HHSConstants.TAXONOMY_TAGGING_BEAN, loTaxonomyTaggingBean);
			if (aoRequest.getParameter(HHSConstants.SUCCESS) != null)
			{
				String lsErrorMsg = HHSConstants.TAG_ADDED_SUCCESSFULLY;
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_PASS_TYPE);
			}
			else if (aoRequest.getParameter(HHSConstants.DELETE) != null)
			{
				String lsErrorMsg = HHSConstants.TAG_DELETED_SUCCESSFULLY;
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_PASS_TYPE);
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			setExceptionMessageInResponse(aoRequest);
			LOG_OBJECT.Error("Error occured while editing taxonomy tags : ", aoAppEx);
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_STATUS, ApplicationConstants.MESSAGE_FAIL_TYPE);
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_MSG, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST);
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			setExceptionMessageInResponse(aoRequest);
			LOG_OBJECT.Error("Error occured while editing taxonomy tags : ", aoEx);
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_STATUS, ApplicationConstants.MESSAGE_FAIL_TYPE);
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_MSG, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST);
		}
		return loModelAndView;
	}

	/**
	 * This method populate service/function drop down.
	 * 
	 * <ul>
	 * <li>1. Fetch data from taxonomy Cache method</li>
	 * <li>2. Return the service/Function list to the calling method.</li>
	 * <li>Updated method in R4</li>
	 * </ul>
	 * 
	 * @return loTaxonomyList - service/function list
	 * @throws ApplicationException when exception occurs
	 */
	private List<TaxonomyTaggingTree> selectedServiceFunction() throws ApplicationException
	{
		List<TaxonomyTaggingTree> loTaxonomyList = new ArrayList<TaxonomyTaggingTree>();
		try
		{
			Document loTaxonomyDom = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
					ApplicationConstants.TAXONOMY_ELEMENT);
			String lsXpath = HHSConstants.XPATH_NAME_SERVICE_OR_FUNCTION;
			// get list of service/function elements
			List<Element> loElementList = XMLUtil.getElementList(lsXpath, loTaxonomyDom);
			TaxonomyTaggingTree loTaxonomyTree = null;
			for (Element loChildDetails : loElementList)
			{
				String lsBranchId = loChildDetails.getAttributeValue(HHSConstants.BRANCH_ID);
				if (lsBranchId != null && loChildDetails.getChildren(HHSConstants.HHSUTIL_ELEMENT).size() == 0)
				{
					loTaxonomyTree = new TaxonomyTaggingTree();
					loTaxonomyTree.setMsElementid(loChildDetails.getAttributeValue(HHSConstants.ID));
					loTaxonomyTree.setMsElementName(loChildDetails.getAttributeValue(HHSConstants.NAME));
					loTaxonomyTree.setMsElementType(loChildDetails.getAttributeValue(HHSConstants.TYPE));
					loTaxonomyTree.setMsBranchid(loChildDetails.getAttributeValue(HHSConstants.BRANCH_ID));
					loTaxonomyTree.setMsParentid(loChildDetails.getAttributeValue(HHSConstants.PARENT_ID));
					loTaxonomyTree.setMsEvidenceReqd(loChildDetails.getAttributeValue(HHSConstants.EVE_REQ_FLAG));
					loTaxonomyTree.setMsActiveFlag(loChildDetails.getAttributeValue(HHSConstants.HHSUTIL_ACTIVEFLAG));
					loTaxonomyTree.setMsActiveFlag(loChildDetails.getAttributeValue(HHSConstants.SELECTION_FLAG));
					loTaxonomyTree.setMsLevel(loChildDetails.getAttributeValue(HHSConstants.AS_LEVEL));
					loTaxonomyList.add(loTaxonomyTree);
				}
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured while populating ServiceFunctionDropDown : ", aoAppEx);
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occured while populating ServiceFunctionDropDown : ", aoEx);
			throw new ApplicationException("Error occured while populating ServiceFunctionDropDown", aoEx);
		}
		return loTaxonomyList;
	}

	/**
	 * This method used to edit taxonomy tags for the selected service/function
	 * 
	 * <ul>
	 * <li>1. Get proposalId, procurementId and serviceElementId id from the
	 * request parameter.</li>
	 * <li>2. Set proposalId, procurementId and serviceElementId in the channel
	 * object.</li>
	 * <li>3. Execute fetchTaxonomyTaggingList transaction to fetch taxonomy
	 * tags for the selected service/function.</li>
	 * <li>4. Get loTaxonomyTaggingList from the channel object for the
	 * remaining tags linked to the service/function.</li>
	 * <li>5. Set TaxonomyTaggingList in the session.</li>
	 * <li>6. render add newtag jsp</li>
	 * <li>This method was updated in R4</li>
	 * </ul>
	 * 
	 * @param aoRequest - a Resource Request object
	 * @param aoResponse - a Resource Response object
	 * @return ModelAndView - render addnewtag jsp
	 */
	@ResourceMapping("editTaxonomyTagsInBulkUrl")
	public ModelAndView editTaxonomyTagsInBulk(ResourceRequest aoRequest, ResourceResponse aoResponse)
	{
		ModelAndView loModelAndView = new ModelAndView("taxonomytagging/addEditBulk");

		String lsFormattedOutput = HHSConstants.EMPTY_STRING;
		String lsFormattedHiddenOutput = HHSConstants.EMPTY_STRING;
		List<TaxonomyTaggingTree> loTaxonomyList = null;
		aoResponse.setContentType(HHSConstants.TEXT_HTML);
		try
		{
			// get taxonomy data other than service/function
			lsFormattedOutput = navigateToTaxonomyExceptServiecFilter(aoRequest, aoResponse);
			// get taxonomy data for service/function
			lsFormattedHiddenOutput = navigateToTaxonomyServiecFilter(aoRequest, aoResponse);
			loTaxonomyList = selectedServiceFunction();
			aoRequest.setAttribute(HHSConstants.TAXONOMY_TREE, lsFormattedOutput);
			aoRequest.setAttribute(HHSConstants.SERVICE_FUNCTION_LIST, loTaxonomyList);
			aoRequest.setAttribute(HHSConstants.TAXONOMY_HIDDEN_TREE, lsFormattedHiddenOutput);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			setExceptionMessageInResponse(aoRequest);
			LOG_OBJECT.Error("Error occured while editing taxonomy tags : ", aoAppEx);
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_STATUS, ApplicationConstants.MESSAGE_FAIL_TYPE);
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_MSG, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST);
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			setExceptionMessageInResponse(aoRequest);
			LOG_OBJECT.Error("Error occured while editing taxonomy tags : ", aoEx);
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_STATUS, ApplicationConstants.MESSAGE_FAIL_TYPE);
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_MSG, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST);
		}
		return loModelAndView;
	}

	/**
	 * This method used to delete taxonomy tags for the selected
	 * service/function
	 * 
	 * <ul>
	 * <li>1. Get proposalId, procurementId and serviceElementId id from the
	 * request parameter.</li>
	 * <li>2. Set proposalId, procurementId and serviceElementId in the channel
	 * object.</li>
	 * <li>3. Execute deleteTaxonomyTaggingDetails transaction to delete
	 * taxonomy tags for the selected service/function.</li>
	 * <li>4. Get loTaxonomyTaggingList from the channel object for the
	 * remaining tags linked to the service/function.</li>
	 * <li>5. render addnewtag jsp</li>
	 * <li>This method was updated in R4</li>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * @param aoRequest - a Resource Request object
	 * @param aoResponse - a Resource Response object
	 * @return ModelAndView Model And View
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@ResourceMapping("removeAllTaxonomyTagUrlPopUpInBulk")
	public ModelAndView removeAllTaxonomyTagUrlPopUpInBulk(ResourceRequest aoRequest, ResourceResponse aoResponse)
	{
		ModelAndView loModelAndView = new ModelAndView(HHSConstants.REMOVE_ALL_TAGGING_JSP_IN_BULK);
		String lsContractId = aoRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW);
		String lsProposalId = aoRequest.getParameter(HHSConstants.PROPOSAL_ID);
		String lsProcurementId = aoRequest.getParameter(HHSConstants.PROCUREMENT_ID);
		Map loTaxonomyTaggingMap = new HashMap();
		try
		{
			loTaxonomyTaggingMap.put(HHSConstants.PROPOSAL_ID, lsProposalId);
			loTaxonomyTaggingMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, lsContractId);
			loTaxonomyTaggingMap.put(HHSConstants.PROCUREMENT_ID, lsProcurementId);
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			setExceptionMessageInResponse(aoRequest);
			LOG_OBJECT.Error("Error occured while deleting taxonomy tags : ", aoEx);
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_STATUS, ApplicationConstants.MESSAGE_FAIL_TYPE);
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_MSG, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST);
		}
		return loModelAndView.addAllObjects(loTaxonomyTaggingMap);
	}

	/**
	 * This method used to delete taxonomy tags for the selected
	 * service/function
	 * 
	 * <ul>
	 * <li>1. Get proposalId, procurementId, ContractId and serviceElementId id
	 * from the request parameter.</li>
	 * <li>2. Set proposalId, procurementId and serviceElementId in the channel
	 * object.</li>
	 * <li>3. Execute removeAllTaxonomyTaggingDetailsInBulk transaction to
	 * delete taxonomy tags for the selected service/function.</li>
	 * <li>4. Render the success message</li>
	 * <li>This method was updated in R4</li>
	 * </ul>
	 * 
	 * @param aoRequest - a Resource Request object
	 * @param aoResponse - a Resource Response object
	 */
	@ActionMapping(params = "submit_action=removeAllTaxonomyTagUrlBulk")
	public void removeAllTaxonomyTagUrlBulk(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		Channel loChannelObj = null;
		String lsContractId = aoRequest.getParameter(HHSConstants.TT_HIDDENCONTRACTBULKID);
		String lsProposalId = aoRequest.getParameter(HHSConstants.TT_HIDDENPROPOSALBULKID);
		String lsProcurementId = aoRequest.getParameter(HHSConstants.TT_HIDDENPROCUREMENTBULKID);
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		TaxonomyTaggingBean loTaxonomyTaggingBean = new TaxonomyTaggingBean();
		try
		{
			loChannelObj = new Channel();
			if (lsProposalId != null && !lsProposalId.equalsIgnoreCase(HHSConstants.EMPTY_STRING)
					&& !lsProposalId.equalsIgnoreCase(HHSConstants.NULL))
			{
				loTaxonomyTaggingBean.setProposalIdList(new ArrayList<String>(Arrays.asList(lsProposalId
						.split(ApplicationConstants.COMMA))));
			}
			if (lsProcurementId != null && !lsProcurementId.equalsIgnoreCase(HHSConstants.EMPTY_STRING)
					&& !lsProcurementId.equalsIgnoreCase(HHSConstants.NULL))
			{
				loTaxonomyTaggingBean.setProcurementIdList(new ArrayList<String>(Arrays.asList(lsProcurementId
						.split(ApplicationConstants.COMMA))));
			}
			if (lsContractId != null && !lsContractId.equalsIgnoreCase(HHSConstants.EMPTY_STRING)
					&& !lsContractId.equalsIgnoreCase(HHSConstants.NULL))
			{
				loTaxonomyTaggingBean.setContractIdList(new ArrayList<String>(Arrays.asList(lsContractId
						.split(ApplicationConstants.COMMA))));
			}
			loTaxonomyTaggingBean.setModifyByUserId(lsUserId);
			loChannelObj.setData(HHSConstants.AO_TAXONOMY_TAGGING_BEAN, loTaxonomyTaggingBean);
			HHSTransactionManager.executeTransaction(loChannelObj,
					HHSConstants.REMOVE_ALL_TAXONOMY_TAGGING_DETAILS_IN_BULK);
			aoResponse.setRenderParameter(HHSConstants.NAVIGATE_FROM, HHSConstants.TAXONOMY_TAGGING_PAGE);
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_PASS_TYPE);
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.TAXONOMY_TAGS_REMOVE_MESSAGE));
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			setExceptionMessageInResponse(aoRequest);
			LOG_OBJECT.Error("Error occured while deleting taxonomy tags : ", aoAppEx);
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_STATUS, ApplicationConstants.MESSAGE_FAIL_TYPE);
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_MSG, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST);
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			setExceptionMessageInResponse(aoRequest);
			LOG_OBJECT.Error("Error occured while deleting taxonomy tags : ", aoEx);
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_STATUS, ApplicationConstants.MESSAGE_FAIL_TYPE);
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_MSG, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST);
		}
	}

	/**
	 * This method saves data in taxonomy tagging and taxonomy modifier table
	 * 
	 * <ul>
	 * <li>1. Get proposalId, procurementId, contractId modifiers and element id
	 * from the request parameter.</li>
	 * <li>2. Set all the parameters in the channel object.</li>
	 * <li>3. Insert data into taxonomyTagging table using
	 * saveAllSelectedProposalsInbulk transaction.</li>
	 * <li>4. Delete the data using "deleteTaxonomyTaggingDetailsInBulk"
	 * transaction.</li>
	 * <li>5. render selectedservicefunction JSP</li>
	 * <li>This method was updated in R4</li>
	 * </ul>
	 * 
	 * @param aoRequest - a Resource Request object
	 * @param aoResponse - a Resource Response object
	 */
	@ActionMapping(params = "submit_action=saveTaxonomyTagUrlInBulk")
	public void saveTaxonomyTagsInBulk(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		try
		{
			String lsContractIdBulk = aoRequest.getParameter(HHSConstants.TT_HIDDENCONTRACTBULKID);
			String lsProposalIdBulk = aoRequest.getParameter(HHSConstants.TT_HIDDENPROPOSALBULKID);
			String lsProcurementIdBulk = aoRequest.getParameter(HHSConstants.TT_HIDDENPROCUREMENTBULKID);
			String lsDeletedTaxonomy = aoRequest.getParameter(HHSConstants.TT_HIDDENDELETEDTAGS);
			String[] loServiceId = aoRequest.getParameterValues(HHSConstants.TT_SERVICEID);
			String[] loModifierIds = aoRequest.getParameterValues(HHSConstants.TT_MODIFIERID);
			String[] loTaxonomyTaggingId = aoRequest.getParameterValues(HHSConstants.TT_TAXONOMYTAGGINGID);
			List<String> loServiceIdArray = new ArrayList<String>();
			if (loServiceId != null)
			{
				loServiceIdArray = Arrays.asList(loServiceId);
			}
			List<String> loModifierIdsArray = new ArrayList<String>();
			if (loModifierIds != null)
			{
				loModifierIdsArray = Arrays.asList(loModifierIds);
			}
			List<String> loTaxonomyTaggingIdArray = new ArrayList<String>();
			if (loTaxonomyTaggingId != null)
			{
				loTaxonomyTaggingIdArray = Arrays.asList(loTaxonomyTaggingId);
			}
			List<String> loContractIdBulkArray = Arrays.asList(lsContractIdBulk.split(HHSConstants.COMMA));
			List<String> loProposalIdBulkArray = Arrays.asList(lsProposalIdBulk.split(HHSConstants.COMMA));
			List<String> loProcurementIdArray = Arrays.asList(lsProcurementIdBulk.split(HHSConstants.COMMA));
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			Channel loChannelObj = new Channel();
			loChannelObj.setData(HHSConstants.TT_SERVICEIDARRAY, loServiceIdArray);
			loChannelObj.setData(HHSConstants.TT_MODIFIERIDSARRAY, loModifierIdsArray);
			loChannelObj.setData(HHSConstants.TT_CONTRACTIDBULKARRAY, loContractIdBulkArray);
			loChannelObj.setData(HHSConstants.TT_PROPOSALIDBULKARRAY, loProposalIdBulkArray);
			loChannelObj.setData(HHSConstants.TT_PROCUREMENTIDBULKARRAY, loProcurementIdArray);
			loChannelObj.setData(HHSConstants.TT_USERID, lsUserId);
			loChannelObj.setData(HHSConstants.TT_DELETEDTAXONOMYTAGGINGID, lsDeletedTaxonomy);
			loChannelObj.setData(HHSConstants.TT_TAXONOMYTAGGINGIDS, loTaxonomyTaggingIdArray);
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.SAVE_ALL_SELECTED_PROPOSALS_IN_BULK);
			aoResponse.setRenderParameter(HHSConstants.NAVIGATE_FROM, HHSConstants.TAXONOMY_TAGGING_PAGE);
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_PASS_TYPE);
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.TAXONOMY_TAGS_SAVE_MESSAGE));
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			setExceptionMessageInResponse(aoRequest);
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_STATUS, ApplicationConstants.MESSAGE_FAIL_TYPE);
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_MSG, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST);
			LOG_OBJECT.Error("Error occured while saving new Taxonomy tags", aoAppEx);
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			setExceptionMessageInResponse(aoRequest);
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_STATUS, ApplicationConstants.MESSAGE_FAIL_TYPE);
			aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_MSG, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST);
			LOG_OBJECT.Error("Error occured while saving new Taxonomy tags", aoEx);
		}
	}
}