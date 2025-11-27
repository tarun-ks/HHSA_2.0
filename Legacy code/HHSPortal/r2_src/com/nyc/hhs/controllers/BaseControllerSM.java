package com.nyc.hhs.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.controllers.util.BaseControllerUtil;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.CommentsHistoryBean;
import com.nyc.hhs.model.DocumentPropertiesBean;
import com.nyc.hhs.model.EvaluationBean;
import com.nyc.hhs.model.Navigation;
import com.nyc.hhs.model.ProcurementInfo;
import com.nyc.hhs.model.ProposalDetailsBean;
import com.nyc.hhs.model.ProposalQuestionAnswerBean;
import com.nyc.hhs.model.SiteDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.ApplicationSession;
import com.nyc.hhs.util.BusinessApplicationUtil;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.FileNetOperationsUtils;
import com.nyc.hhs.util.HHSPortalUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PortalUtil;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.XMLUtil;

/**
 * This class has methods for navigation
 * 
 */
public class BaseControllerSM extends BaseController
{
	/**
	 * This is a log object which is used to log any exception into log file
	 */
	private final LogInfo LOG_OBJECT = new LogInfo(BaseControllerSM.class);
	/**
	 * View resolver object
	 */
	InternalResourceViewResolver viewResolver;

	/**
	 * <p>
	 * This method sets top level navigation related attributes in request.
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. Retrieve procurement Id</li>
	 * <li>2. Fetch "topLevelFromReqest" and "midLevelFromRequest" from
	 * 
	 * PortletRequest</li>
	 * <li>3. If retrieved procurement Id is not null then set the procurement
	 * Id in the Channel object and execute transaction
	 * <b>getProcurementSummaryForNav</b> corresponding to that procurement Id</li>
	 * <li>4. Retrieve current status and previous status of procurement from
	 * the executed transaction.</li>
	 * <li>5. Else set the procurement status Id as "1" i.e. procurement status
	 * as "Draft"</li>
	 * <li>6. If procurement status id equals "7" i.e. procurement status as
	 * "Canceled" then set procurement status Id equals to previous procurement
	 * Id and set "lbIsCancelled" as true</li>
	 * <li>7. Retrieve User Role Mappings in User Bean from PortletSession</li>
	 * <li>8. Get "navigationSM.xml" object from Cache and user type from user
	 * role mappings bean</li>
	 * <li>9. Fetch "Tab Id", "Screen Name" and "Procurement State"
	 * corresponding to the procurement Id and user type from "navigationSM.xml"
	 * </li>
	 * <li>10. If "lbIsCancelled" is true then invoke updateForCancel method
	 * corresponding to the retrieved element details</li>
	 * <li>11. Get tab list from retrieved element details and iterate over it
	 * to invoke getFinalizedNavigation method corresponding to a particular tab
	 * and retrieve the finalized navigation structure</li>
	 * <li>12. Invoke processNavigation method to further process the
	 * navigation.</li>
	 * <li>13. Mark the selected tabs using setNavigationInRequest method.</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoRequest - Portlet Request
	 * @param asProcurementId - Selected Procurement's Id
	 * @throws ApplicationException if any exception occurred
	 */
	@SuppressWarnings("unchecked")
	protected void getPageHeader(PortletRequest aoRequest, String asProcurementId) throws ApplicationException
	{
		String lsProcurementStatusId = null;
		String lsUserType = null;
		try
		{
			String lsProcurementStatusPrevId = null;
			boolean lbIsCancelled = false;
			if (aoRequest == null)
			{
				throw new ApplicationException("Request cannot be null in BaseControllerSM::getPageHeader method");
			}
			String lsIsExitVisible = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.HIDE_EXIT_PROCUREMENT);
			aoRequest.setAttribute(HHSConstants.HIDE_EXIT_PROCUREMENT, Boolean.valueOf(lsIsExitVisible));
			String lsTopTabId = aoRequest.getParameter(HHSConstants.TOP_LEVEL_FROM_REQ);
			String lsCompPoolId = aoRequest.getParameter(HHSConstants.COMPETITION_POOL_ID);
			String lsEvaluationGroupId = aoRequest.getParameter(HHSConstants.EVALUATION_GROUP_ID);
			String lsEvaluationPoolMappingId = aoRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID);
			String lsMidTabId = aoRequest.getParameter(HHSConstants.MID_LEVEL_FROM_REQ);
			String lsOpendFromStandAlon = HHSPortalUtil.parseQueryString(aoRequest, "overlay");
			String lsTakeValueFromAttribute = HHSPortalUtil.parseQueryString(aoRequest, "takeValueFromAttribute");
			if (lsTakeValueFromAttribute != null && lsTakeValueFromAttribute.equalsIgnoreCase("true"))
			{
				lsTopTabId = (String) aoRequest.getAttribute(HHSConstants.TOP_LEVEL_FROM_REQ);
				lsMidTabId = (String) aoRequest.getAttribute(HHSConstants.MID_LEVEL_FROM_REQ);
			}
			if ((null == lsTopTabId && null == lsMidTabId) || lsOpendFromStandAlon != null)
			{
				lsTopTabId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.TOP_LEVEL_FROM_REQ);
				lsMidTabId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.MID_LEVEL_FROM_REQ);
			}

			setRequestForPageHeader(aoRequest, lsTopTabId, lsCompPoolId, lsEvaluationGroupId,
					lsEvaluationPoolMappingId, lsMidTabId);

			if (asProcurementId != null && !asProcurementId.isEmpty())
			{
				Channel loChannel = new Channel();
				loChannel.setData(HHSConstants.PROCUREMENT_ID, asProcurementId);
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.GET_PROC_SUMMARY_FOR_NAV);
				ProcurementInfo loProcurement = (ProcurementInfo) loChannel.getData(HHSConstants.PROC_SUMMARY);
				lsProcurementStatusId = loProcurement.getStatus().toString();
				lsProcurementStatusPrevId = loProcurement.getPreviousStatus() == null ? null : loProcurement
						.getPreviousStatus().toString();
				aoRequest.setAttribute(HHSConstants.PROCUREMENT_BEAN, loProcurement);
			}
			else
			{
				lsProcurementStatusId = HHSConstants.STRING_ZERO;
			}
			if (lsProcurementStatusId.equalsIgnoreCase(PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_CANCELLED)))
			{
				lsProcurementStatusId = lsProcurementStatusPrevId;
				lbIsCancelled = true;
			}
			aoRequest.setAttribute(HHSConstants.PROCUREMENT_ID, asProcurementId);
			Set<String> loDynamicStates = new HashSet<String>();
			lsUserType = getPageHeaderFinal(aoRequest, asProcurementId, lsProcurementStatusId, lbIsCancelled,
					lsTopTabId, lsMidTabId, loDynamicStates);
		}
		catch (ApplicationException loApEx)
		{
			// Catch the exception thrown by transaction and pass the caught
			// exception with input params to the calling function
			loApEx.addContextData(HHSConstants.AS_PROCUREMENT_ID, asProcurementId);
			LOG_OBJECT.Error(
					"Some Error Occured while creating navigation :: BaseControllerSM :: getPageHeader() :: procurementId ::"
							+ asProcurementId + " :: status ::" + lsProcurementStatusId + " :: userType ::"
							+ lsUserType, loApEx);
			throw loApEx;
		}
		catch (Exception loEx)
		{
			// Catch the exception thrown by transaction and pass the caught
			// exception with input params to the calling function
			LOG_OBJECT.Error(
					"Some Error Occured while creating navigation :: BaseControllerSM :: getPageHeader() :: procurementId ::"
							+ asProcurementId + " :: status ::" + lsProcurementStatusId + " :: userType ::"
							+ lsUserType, loEx);
			throw new ApplicationException(
					"Some Error Occured while creating navigation :: BaseControllerSM :: getPageHeader()", loEx);
		}
	}

	/**
	 * <p>
	 * This method sets top level navigation related attributes in request.
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. If "lbIsCancelled" is true then invoke updateForCancel method
	 * corresponding to the retrieved element details</li>
	 * <li>2. Get tab list from retrieved element details and iterate over it to
	 * invoke getFinalizedNavigation method corresponding to a particular tab
	 * and retrieve the finalized navigation structure</li>
	 * <li>3. Invoke processNavigation method to further process the navigation.
	 * </li>
	 * <li>4. Mark the selected tabs using setNavigationInRequest method.</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoRequest Portlet Request
	 * @param asProcurementId procurement Id
	 * @param lsProcurementStatusId Proc status Id
	 * @param lbIsCancelled cancelled flag
	 * @param lsTopTabId top tab Id
	 * @param lsMidTabId mid tab Id
	 * @param loDynamicStates set of states
	 * @return user type string
	 * @throws ApplicationException
	 */
	private String getPageHeaderFinal(PortletRequest aoRequest, String asProcurementId, String lsProcurementStatusId,
			boolean lbIsCancelled, String lsTopTabId, String lsMidTabId, Set<String> loDynamicStates)
			throws ApplicationException
	{
		String lsUserType;
		PortletSession loSession = aoRequest.getPortletSession();
		lsUserType = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
				PortletSession.APPLICATION_SCOPE);
		Document loNavigationDom = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				HHSConstants.NAVIGATION_SM);
		String lsXPath = "//status[(contains(@user_type,\"" + lsUserType + "\") and @id=\"" + lsProcurementStatusId
				+ "\")]";
		Element loStatusElement = (Element) XMLUtil.getElement(lsXPath, loNavigationDom).clone();
		if (lbIsCancelled)
		{
			updateNavigationStateForCancelledStatus(loStatusElement);
		}
		List<Element> loTabElementList = loStatusElement.getChildren(HHSConstants.TAB);
		Navigation loTabNavigation = null;
		Map<String, Navigation> loNavigationMap = new HashMap<String, Navigation>();
		for (Element loElt : loTabElementList)
		{
			loTabNavigation = getFinalizedNavigation(aoRequest, loElt, lsTopTabId, lsMidTabId, true, loDynamicStates);
			loNavigationMap.put(loTabNavigation.getTabId(), loTabNavigation);
		}
		processNavigation(aoRequest, loNavigationMap, loDynamicStates, asProcurementId);
		updateNavigationMap(lsTopTabId, loNavigationMap);
		setNavigationInRequest(aoRequest, loNavigationMap);
		return lsUserType;
	}

	/**
	 * This method is used to set portlet request for getting page header
	 * 
	 * @param aoRequest Portlet Request
	 * @param lsTopTabId Top tab Id
	 * @param lsCompPoolId Competition Pool Id
	 * @param lsEvaluationGroupId Eval Group Id
	 * @param lsEvaluationPoolMappingId Eval Pool Mapping Id
	 * @param lsMidTabId Mid tab Id
	 */
	private void setRequestForPageHeader(PortletRequest aoRequest, String lsTopTabId, String lsCompPoolId,
			String lsEvaluationGroupId, String lsEvaluationPoolMappingId, String lsMidTabId)
	{
		if (lsTopTabId != null)
		{
			aoRequest.setAttribute(HHSConstants.TOP_LEVEL_FROM_REQ, lsTopTabId);
		}
		if (lsMidTabId != null)
		{
			aoRequest.setAttribute(HHSConstants.MID_LEVEL_FROM_REQ, lsMidTabId);
		}
		if (lsCompPoolId != null)
		{
			aoRequest.setAttribute(HHSConstants.COMPETITION_POOL_ID, lsCompPoolId);
		}
		if (lsEvaluationGroupId != null)
		{
			aoRequest.setAttribute(HHSConstants.EVALUATION_GROUP_ID, lsEvaluationGroupId);
		}
		if (lsEvaluationPoolMappingId != null)
		{
			aoRequest.setAttribute(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvaluationPoolMappingId);
		}
	}

	/**
	 * This method is used to update the navigation tab.
	 * 
	 * @param asTopTabId
	 * @param aoNavigationMap
	 */
	private void updateNavigationMap(String asTopTabId, Map<String, Navigation> aoNavigationMap)
	{
		Navigation loTabNavigation;
		String lsScreenId;
		List<Navigation> loChildNavigationList;
		for (Map.Entry<String, Navigation> loEntrySet : aoNavigationMap.entrySet())
		{
			loTabNavigation = loEntrySet.getValue();
			lsScreenId = loTabNavigation.getTabId();
			loChildNavigationList = loTabNavigation.getChildList();
			if (loChildNavigationList != null)
			{
				for (Navigation loChildNavigation : loChildNavigationList)
				{
					if (!(asTopTabId != null && (((asTopTabId
							.equalsIgnoreCase(HHSConstants.PROCUREMENT_ROADMAP_DETAILS) || asTopTabId
							.equalsIgnoreCase(HHSConstants.RFP_RELEASE_DETAILS)) && lsScreenId
							.equalsIgnoreCase(HHSConstants.PROCUREMENT_INFORMATION)) || asTopTabId
							.equalsIgnoreCase(lsScreenId))))
					{
						loChildNavigation.setTabState(HHSConstants.H);
						loChildNavigation.setSelected(false);
					}
				}
			}
		}
	}

	/**
	 * This method converts Publish Procurement, Release RFP, Release Addendum,
	 * Submit Proposal tabs to hidden state
	 * <ul>
	 * <li>1. Retrieve element status list and fetch its child elements</li>
	 * <li>2. Iterate over child elements to search for element with id in
	 * hidden tabs for canceled status list</li>
	 * <li>3. Change the tabs state to hidden</li>
	 * <li>4. Repeat the process for child tabs</li>
	 * </ul>
	 * 
	 * @param aoStatusElement - Cloned status element from XML
	 */
	@SuppressWarnings("unchecked")
	void updateNavigationStateForCancelledStatus(Element aoStatusElement)
	{
		List<Element> loTabElementList = aoStatusElement.getChildren();
		for (Element loElement : loTabElementList)
		{
			if (HHSConstants.HIDDEN_TABS_CANCEL_STATUS.contains(loElement.getAttributeValue(HHSConstants.ID)))
			{
				loElement.setAttribute(HHSConstants.ELEMENT_STATE, HHSConstants.H);
			}
			if (loElement.getChildren().size() > HHSConstants.INT_ZERO)
			{
				updateNavigationStateForCancelledStatus(loElement);
			}
		}
	}

	/**
	 * This method mark the selected tab and set tab details in request
	 * <ul>
	 * <li>1. Iterate over navigation map</li>
	 * <li>2. Check if child navigation tab is selected and accordingly mark it</li>
	 * <li>3. Check if Parent navigation tab is selected and accordingly mark it
	 * </li>
	 * <li>4. Set navigation in request</li>
	 * <li>5. Set navigation in session if request is action request</li>
	 * <ul>
	 * 
	 * @param aoRequest - Portlet request
	 * @param aoNavigationMap - finalized navigation map
	 */
	private void setNavigationInRequest(PortletRequest aoRequest, Map<String, Navigation> aoNavigationMap)
	{
		Navigation loNavigation = null;
		boolean lbIsSelected = false;
		for (Map.Entry<String, Navigation> loEntry : aoNavigationMap.entrySet())
		{
			loNavigation = loEntry.getValue();
			aoRequest.setAttribute(loNavigation.getTabId(), loNavigation);
			List<Navigation> loNavigationChildList = loNavigation.getChildList();
			lbIsSelected = false;
			if (loNavigationChildList != null)
			{
				for (Navigation loNavigationChild : loNavigationChildList)
				{
					aoRequest.setAttribute(loNavigationChild.getTabId(), loNavigationChild);
					if (loNavigationChild.isSelected() && loNavigation.isSelected())
					{
						lbIsSelected = true;
					}
				}
				for (Navigation loNavigationChild : loNavigationChildList)
				{
					if (!lbIsSelected
							&& loNavigationChild.getTabState().equalsIgnoreCase(HHSConstants.E)
							&& (aoRequest.getAttribute(HHSConstants.SELECTED_CHILD_TAB) == null || ((String) aoRequest
									.getAttribute(HHSConstants.SELECTED_CHILD_TAB)).contains(HHSConstants.COMMA)))
					{
						loNavigationChild.setSelected(true);
						aoRequest.setAttribute(HHSConstants.SELECTED_CHILD_TAB, loNavigationChild.getScreenNumber());
						break;
					}
				}
			}
		}
	}

	/**
	 * <p>
	 * This method gets the finalized version of navigation
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. Retrieve Screen Id, Screen Name and Screen Name from the Tab
	 * Element list
	 * <li>
	 * <li>2. If Screen State is not null and equals to "E" and If screen number
	 * is null and does not contains "," then invoke getConditionalRoleDisplay
	 * method corresponding to that Screen Number</li>
	 * <li>3. Else split the screen numbers to fetch individual screen number
	 * and invoke getConditionalRoleDisplay method corresponding to that Screen
	 * Number and fetch boolean flag</li>
	 * <li>4. If boolean flag is "false" and screen state starts with "E" then
	 * change screen state to "H"</li>
	 * <li>5. Else If boolean flag is "true" and screen state equals "E4", "E6"
	 * or "E7" then add screen state to the Set of Strings</li>
	 * <li>6. If screen Id equals Top Tab Id then set "setSelected" as true and
	 * set "selectedChildTab" in the PortletRequest</li>
	 * <li>7. Fetch "tab" from tab element details and set it in the list of
	 * elements</li>
	 * <li>8. If the size of list of elements is greater than 0 then iterate
	 * over the list to convert the xml structure to navigation bean structure.</li>
	 * <li>9. Mark selected tabs based on request params.</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoRequest - Portlet Request
	 * @param aoTabElt - Tab Element
	 * @param asTopTabId - Top level tab Id
	 * @param asMidTabId - Mid level Tab Id
	 * @param abIsTop - flag depecting -> is top most tab
	 * @param aoDynamicStates - set of dynamic enabled tabs
	 * @return loTabNav - an object of type Navigation bean
	 * @throws ApplicationException if any exception occurred
	 */
	@SuppressWarnings("unchecked")
	private Navigation getFinalizedNavigation(PortletRequest aoRequest, Element aoTabElt, String asTopTabId,
			String asMidTabId, boolean abIsTop, Set<String> aoDynamicStates) throws ApplicationException
	{
		Navigation loTabNav = new Navigation();
		try
		{
			String lsScreenId = aoTabElt.getAttributeValue(HHSConstants.ID);
			String lsScreenNumber = aoTabElt.getAttributeValue(HHSConstants.SCREEN);
			String lsScreenState = aoTabElt.getAttributeValue(HHSConstants.ELEMENT_STATE);
			boolean lbIsAvailable = false;
			if (lsScreenState != null
					&& (lsScreenState.charAt(HHSConstants.INT_ZERO) == HHSConstants.CHAR_E || lsScreenState
							.charAt(HHSConstants.INT_ZERO) == HHSConstants.CHAR_D))
			{
				if (lsScreenNumber != null)
				{
					if (!lsScreenNumber.contains(HHSConstants.COMMA))
					{
						lbIsAvailable = CommonUtil.getConditionalRoleWithoutCFODisplay(lsScreenNumber,
								aoRequest.getPortletSession());
					}
					else
					{
						String lsScreenNumberArray[] = lsScreenNumber.split(HHSConstants.COMMA);
						for (String lsScreenNum : lsScreenNumberArray)
						{
							lbIsAvailable = CommonUtil.getConditionalRoleWithoutCFODisplay(lsScreenNum,
									aoRequest.getPortletSession());
							if (lbIsAvailable)
							{
								break;
							}
						}
					}
				}
			}
			if (!lbIsAvailable
					&& (lsScreenState.charAt(HHSConstants.INT_ZERO) == HHSConstants.CHAR_E || lsScreenState
							.charAt(HHSConstants.INT_ZERO) == HHSConstants.CHAR_D))
			{
				lsScreenState = HHSConstants.H;
				loTabNav.setAuthorized(false);
			}
			else if (lbIsAvailable
					&& (lsScreenState.equalsIgnoreCase(HHSConstants.E4)
							|| lsScreenState.equalsIgnoreCase(HHSConstants.E5)
							|| lsScreenState.equalsIgnoreCase(HHSConstants.E6)
							|| lsScreenState.equalsIgnoreCase(HHSConstants.E7)
							|| lsScreenState.equalsIgnoreCase(HHSConstants.E8)
							|| lsScreenState.equalsIgnoreCase(HHSConstants.E10)
							|| lsScreenState.equalsIgnoreCase(HHSConstants.E11)
							|| lsScreenState.equalsIgnoreCase(HHSConstants.EG) || lsScreenState
								.equalsIgnoreCase(HHSConstants.ES)))
			{
				aoDynamicStates.add(lsScreenState);
			}
			loTabNav.setScreenNumber(lsScreenNumber);
			loTabNav.setTabId(lsScreenId);
			loTabNav.setTabState(lsScreenState);
			updateNavigationForAdditionalRule(aoRequest, aoTabElt, asTopTabId, asMidTabId, abIsTop, aoDynamicStates,
					loTabNav, lsScreenId, lsScreenNumber, lsScreenState);
		}
		catch (Exception loEx)
		{
			// Catch the all the exceptions thrown by the code and pass it to
			// the calling method
			LOG_OBJECT
					.Error("Some Error Occured while creating navigation :: BaseControllerSM :: getFinalizedNavigation()");
			throw new ApplicationException(
					"Some Error Occured while creating navigation :: BaseControllerSM :: getFinalizedNavigation()",
					loEx);
		}
		return loTabNav;
	}

	/**
	 * <ul>
	 * <li>First it will get the attribute value of "additionRuleName" from Tab
	 * Element</li>
	 * <li>If rule name is not null then it will set the rule name to the
	 * Navigation Bean</li>
	 * <li>If the screen state is "H" or screen state is "D" then it will set
	 * the Navigation Bean selected true</li>
	 * <li></li>
	 * </ul>
	 * 
	 * @param aoRequest - Portlet Request
	 * @param aoTabElt - Tab Element
	 * @param asTopTabId - Top level tab Id
	 * @param asMidTabId - Middle level tab Id
	 * @param abIsTop - flag depecting -> is top most tab
	 * @param aoDynamicStates - Dynamics States
	 * @param aoTabNav - an object of type Navigation bean
	 * @param asScreenId - Id of the screen
	 * @param asScreenNumber - Name of the screen
	 * @param asScreenState - state of the screen
	 * @throws ApplicationException
	 */
	private void updateNavigationForAdditionalRule(PortletRequest aoRequest, Element aoTabElt, String asTopTabId,
			String asMidTabId, boolean abIsTop, Set<String> aoDynamicStates, Navigation aoTabNav, String asScreenId,
			String asScreenNumber, String asScreenState) throws ApplicationException
	{
		String lsAdditionalRuleName = aoTabElt.getAttributeValue("additionRuleName");
		if (null != lsAdditionalRuleName)
		{
			aoTabNav.setAdditionRuleName(lsAdditionalRuleName);
		}
		if (!(asScreenState.equalsIgnoreCase(HHSConstants.H) || asScreenState.equalsIgnoreCase(HHSConstants.D))
				&& (asScreenId.equalsIgnoreCase(asTopTabId) || asScreenId.equalsIgnoreCase(asMidTabId)))
		{
			aoTabNav.setSelected(true);
			aoRequest.setAttribute(HHSConstants.SELECTED_CHILD_TAB, asScreenNumber);
		}
		else if (abIsTop
				&& (asTopTabId != null && ((asTopTabId.equalsIgnoreCase(HHSConstants.PROCUREMENT_ROADMAP_DETAILS) || asTopTabId
						.equalsIgnoreCase(HHSConstants.RFP_RELEASE_DETAILS)) && asScreenId
						.equalsIgnoreCase(HHSConstants.PROCUREMENT_INFORMATION))))
		{
			aoTabNav.setSelected(true);
		}
		List<Element> loTabEltList = aoTabElt.getChildren(HHSConstants.TAB);
		if (abIsTop && loTabEltList.size() > HHSConstants.INT_ZERO)
		{
			List<Navigation> loNavList = new ArrayList<Navigation>();
			Navigation loTabNavChild = null;
			for (Element loElt : loTabEltList)
			{
				loTabNavChild = getFinalizedNavigation(aoRequest, loElt, asTopTabId, asMidTabId, false, aoDynamicStates);
				loNavList.add(loTabNavChild);
			}
			aoTabNav.setChildList(loNavList);
		}
	}

	/**
	 * <p>
	 * This method processes the tab config
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. Retrieve user bean populated with user role mappings from the
	 * session</li>
	 * <li>2. Fetch user type and user Id from the retrieved bean</li>
	 * <li>3. Iterate the Set of Strings (Element State set) to process the
	 * dynamic rules E4, E6, E7, E12</li>
	 * <li>4. If element state value is "E4" then if the user type is
	 * "agency_org" then execute transaction <b>checkIfUserOfSameAgency</b> and
	 * retrieve boolean flag "lbIsSame"</li>
	 * <li>5. Call updateNavigationStatus on the basis of "lbIsSame"</li>
	 * <li>6. If element state value is not null and is equal to either "E6" or
	 * "E7" then call transaction <b>getProcurementDetailsForNav</b> and
	 * retrieve output Map</li>
	 * <li>7. Invoke updateNavigationStatus method to change navigation bean
	 * status on the basis of values of the retrieved output Map
	 * </ul>
	 * <b>Where: 4 These screens are the only screens Enabled for all Agency
	 * users if the user is not a member of the Procuring Agency (indicated in
	 * S203); all other screens are Hidden <br />
	 * 6 The "Selection Details" tab will only be Enabled for Providers whose
	 * Proposal was Selected; for all others, the tab will be Hidden <br />
	 * 7 These screens are the only screens Enabled for Provider users that with
	 * Providers status "Service App Required" or "Not Applicable"; all other
	 * screens will be Disabled </b>
	 * </p>
	 * 
	 * @param aoRequest - Portlet Request
	 * @param aoTabMap - complete navigation map
	 * @param aoDynamicStates - set of dynamic enabled tabs
	 * @param asProcurementId - Selected Procurement's Id
	 * @throws ApplicationException if any exception occured
	 */
	private void processNavigation(PortletRequest aoRequest, Map<String, Navigation> aoTabMap,
			Set<String> aoDynamicStates, String asProcurementId) throws ApplicationException
	{
		Channel loChannel = new Channel();
		String lsDynamicValue = null;
		PortletSession loSession = aoRequest.getPortletSession();
		String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsOrgId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
				PortletSession.APPLICATION_SCOPE);
		ProcurementInfo loProcurement = (ProcurementInfo) aoRequest.getAttribute(HHSConstants.PROCUREMENT_BEAN);
		try
		{
			loChannel.setData(HHSConstants.PROCUREMENT_ID, asProcurementId);
			String lsESValue = aoRequest.getParameter(HHSConstants.ES);
			if (aoDynamicStates != null && aoDynamicStates.contains(HHSConstants.EG))
			{
				if (loProcurement.getIsOpenEndedRFP() != null
						&& loProcurement.getIsOpenEndedRFP().equalsIgnoreCase("1") && lsESValue == null)
				{
					updateNavigationStatus(aoTabMap, HHSConstants.E4, HHSConstants.EG);
				}
				else
				{
					updateNavigationStatus(aoTabMap, HHSConstants.H, HHSConstants.EG);
				}
				aoDynamicStates.remove(HHSConstants.EG);
			}
			if (aoDynamicStates != null && aoDynamicStates.contains(HHSConstants.ES))
			{
				if (lsESValue == null || lsESValue.isEmpty() || lsESValue.equalsIgnoreCase("0"))
				{
					updateNavigationStatus(aoTabMap, HHSConstants.E4, HHSConstants.ES);
				}
				else
				{
					updateNavigationStatus(aoTabMap, HHSConstants.H, HHSConstants.ES);
				}
				aoDynamicStates.remove(HHSConstants.ES);
			}
			if (null != aoDynamicStates)
			{
				Iterator<String> loIterator = aoDynamicStates.iterator();
				while (loIterator.hasNext())
				{
					lsDynamicValue = loIterator.next();
					if (lsDynamicValue != null
							&& (lsDynamicValue.equalsIgnoreCase(HHSConstants.E4)
									|| lsDynamicValue.equalsIgnoreCase(HHSConstants.E10) || lsDynamicValue
										.equalsIgnoreCase(HHSConstants.E11)))
					{
						agencyCityRelatedRules(aoRequest, aoTabMap, loChannel, lsUserId, lsOrgId, aoDynamicStates);
						break;
					}
					else if (lsDynamicValue != null
							&& (lsDynamicValue.equalsIgnoreCase(HHSConstants.E5)
									|| lsDynamicValue.equalsIgnoreCase(HHSConstants.E6)
									|| lsDynamicValue.equalsIgnoreCase(HHSConstants.E7) || lsDynamicValue
										.equalsIgnoreCase(HHSConstants.E8)))
					{
						providerRelatedRules(aoRequest, aoTabMap, loChannel, lsUserId, lsOrgId);
						break;
					}
				}
			}
		}
		catch (ApplicationException loApEx)
		{
			// Catch the exception thrown by transaction and pass the caught
			// exception with input params to the calling function
			loApEx.addContextData(HHSConstants.AS_PROCUREMENT_ID, asProcurementId);
			loApEx.addContextData("lsUserId :: ", lsUserId);
			LOG_OBJECT.Error("Some Error Occured while creating navigation :: BaseControllerSM :: processNavigation()",
					loApEx);
			throw loApEx;
		}
	}

	/**
	 * This method is for Agency/City Related Navigation Rules
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Set the required parameters in channel</li>
	 * <li>Invoke Transaction <b>checkIfUserOfSameAgency</b> and fetch rules
	 * related details</li>
	 * <li>Check if there is any additional rule and process accordingle</li>
	 * <li>Based on channel output update navigation statues</li>
	 * </ul>
	 * 
	 * @param aoRequest Request
	 * @param aoTabMap Navigation map
	 * @param aoChannel Channel data
	 * @param asUserId User Id
	 * @param asOrgId Org Id
	 * @param aoDynamicStates Dynamics States
	 * @throws ApplicationException
	 */
	private void agencyCityRelatedRules(PortletRequest aoRequest, Map<String, Navigation> aoTabMap, Channel aoChannel,
			String asUserId, String asOrgId, Set<String> aoDynamicStates) throws ApplicationException
	{
		PortletSession loSession = aoRequest.getPortletSession();
		String lsUserType = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
				PortletSession.APPLICATION_SCOPE);
		String lsUserRole = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ROLE,
				PortletSession.APPLICATION_SCOPE);
		aoChannel.setData(HHSConstants.AGENCY_USER_ID, asUserId);
		aoChannel.setData(HHSConstants.EVALUATION_POOL_MAPPING_ID,
				HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.EVALUATION_POOL_MAPPING_ID));
		HHSTransactionManager.executeTransaction(aoChannel, HHSConstants.CHECK_IF_USER_OF_SAME_AGENCY);
		boolean lbIsSame = (Boolean) aoChannel.getData(HHSConstants.IS_SAME_AGENCY);
		boolean lbIsAwardApproved = (Boolean) aoChannel.getData(HHSConstants.IS_AWARD_APPROVED);
		aoRequest.setAttribute(HHSConstants.IS_PROC_AGENCY, lbIsSame);
		checkAdditionalRules(aoRequest, aoTabMap, aoChannel);
		if (lsUserType != null && lsUserType.equalsIgnoreCase(ApplicationConstants.AGENCY_ORG))
		{
			if (!lbIsSame)
			{
				updateNavigationStatus(aoTabMap, HHSConstants.H, HHSConstants.E, HHSConstants.D, HHSConstants.E10,
						HHSConstants.E11);
				updateNavigationStatus(aoTabMap, HHSConstants.E, HHSConstants.E4);
			}
			else
			{
				updateNavigationStatus(aoTabMap, HHSConstants.E, HHSConstants.E4);
				if (aoDynamicStates.contains(HHSConstants.E11))
				{
					if (HHSConstants.PROGRAM_USERS_ROLES.contains(lsUserRole)
							|| HHSConstants.FINANCE_USERS_ROLES.contains(lsUserRole)
							|| HHSConstants.CFO_ROLE.equals(lsUserRole))
					{
						if (lbIsAwardApproved)
						{
							updateNavigationStatus(aoTabMap, HHSConstants.E, HHSConstants.E11);
						}
						else
						{
							updateNavigationStatus(aoTabMap, HHSConstants.D, HHSConstants.E11);
						}
					}
					else
					{
						updateNavigationStatus(aoTabMap, HHSConstants.E, HHSConstants.E11);
					}
				}
				if (aoDynamicStates.contains(HHSConstants.E10))
				{
					if (lbIsAwardApproved)
					{
						updateNavigationStatus(aoTabMap, HHSConstants.E, HHSConstants.E10);
					}
					else
					{
						updateNavigationStatus(aoTabMap, HHSConstants.D, HHSConstants.E10);
					}
				}
			}
		}
		else
		{
			updateNavigationStatus(aoTabMap, HHSConstants.E, HHSConstants.E4, HHSConstants.E11);
			if (aoDynamicStates.contains(HHSConstants.E10))
			{
				if (lbIsAwardApproved)
				{
					updateNavigationStatus(aoTabMap, HHSConstants.E, HHSConstants.E10);
				}
				else
				{
					updateNavigationStatus(aoTabMap, HHSConstants.D, HHSConstants.E10);
				}
			}
		}
	}

	/**
	 * This method provides the rule related to the provider If the all the
	 * parameters are satisfactorily passed executes the Query
	 * <ul>
	 * <li>Set the required parameters in channel</li>
	 * <li>Invoke Transaction <b>getProcurementDetailsForNav</b> and fetch rules
	 * related details</li>
	 * <li>Based on channel output update navigation statues</li>
	 * </ul>
	 * 
	 * @param aoRequest - Portlet Request
	 * @param aoTabMap - complete navigation map
	 * @param aoChannel
	 * @param asUserId -selected UserId
	 * @param asOrgId -Org Id
	 * @return loOutputMap
	 * @throws ApplicationException if any exception occurs.
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Boolean> providerRelatedRules(PortletRequest aoRequest, Map<String, Navigation> aoTabMap,
			Channel aoChannel, String asUserId, String asOrgId) throws ApplicationException
	{
		String lsProposalId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROPOSAL_ID);
		aoChannel.setData(HHSConstants.PROPOSAL_ID, lsProposalId);
		aoChannel.setData(HHSConstants.PROVIDR_USER_ID, asUserId);
		aoChannel.setData(HHSConstants.ORGANIZATION_ID, asOrgId);
		HHSTransactionManager.executeTransaction(aoChannel, HHSConstants.GET_PROC_DETAIL_FOR_NAV);
		Map<String, Boolean> loOutputMap = (HashMap<String, Boolean>) aoChannel.getData(HHSConstants.OUTPUT_MAP);
		if (loOutputMap != null)
		{
			aoRequest.setAttribute(HHSConstants.RESTRICT_SUBMIT_FLAG,
					loOutputMap.get(HHSConstants.RESTRICT_SUBMIT_FLAG));
		}
		if (loOutputMap != null && loOutputMap.get(HHSConstants.E5))
		{
			updateNavigationStatus(aoTabMap, HHSConstants.E, HHSConstants.E5);
		}
		else if (loOutputMap != null && !loOutputMap.get(HHSConstants.E5))
		{
			updateNavigationStatus(aoTabMap, HHSConstants.H, HHSConstants.E5);
		}

		if (loOutputMap != null && loOutputMap.get(HHSConstants.E6))
		{
			updateNavigationStatus(aoTabMap, HHSConstants.E, HHSConstants.E6);
		}
		else if (loOutputMap != null && !loOutputMap.get(HHSConstants.E6))
		{
			updateNavigationStatus(aoTabMap, HHSConstants.H, HHSConstants.E6);
		}

		if (loOutputMap != null && loOutputMap.get(HHSConstants.E7))
		{
			updateNavigationStatus(aoTabMap, HHSConstants.E, HHSConstants.E7);
		}
		else if (loOutputMap != null && !loOutputMap.get(HHSConstants.E7))
		{
			updateNavigationStatus(aoTabMap, HHSConstants.D, HHSConstants.E6, HHSConstants.E);
			updateNavigationStatus(aoTabMap, HHSConstants.E, HHSConstants.E7);
		}
		if (loOutputMap != null && loOutputMap.get(HHSConstants.E8))
		{
			updateNavigationStatus(aoTabMap, HHSConstants.E, HHSConstants.E8);
		}
		else if (loOutputMap != null && !loOutputMap.get(HHSConstants.E8))
		{
			updateNavigationStatus(aoTabMap, HHSConstants.H, HHSConstants.E8);
		}
		return loOutputMap;
	}

	/**
	 * This method processes the navigation map and reset State to the specified
	 * states
	 * <ul>
	 * <li>1. Retrieve navigation map, array of "from" state and "to" String</li>
	 * <li>2. Retrieve keySet from the navigation map and iterate over it</li>
	 * <li>3. Iterate over the array of "from" String and set the value of the
	 * element state to specified state</li>
	 * <li>4. Repeat the process for the child elements</li>
	 * </ul>
	 * 
	 * @param aoTabMap - complete navigation Map
	 * @param asTo - change state to
	 * @param aoFrom - change state from array
	 * 
	 */
	private void updateNavigationStatus(Map<String, Navigation> aoTabMap, String asTo, String... aoFrom)
	{
		Navigation loNav = null;
		List<Navigation> loNavChildList = null;
		for (Map.Entry<String, Navigation> loEntry : aoTabMap.entrySet())
		{
			loNav = loEntry.getValue();
			for (String lsFrom : aoFrom)
			{
				if (loNav.getTabState() != null && loNav.getTabState().equalsIgnoreCase(lsFrom))
				{
					loNav.setTabState(asTo);
				}
				loNavChildList = loNav.getChildList();
				if (loNavChildList != null)
				{
					for (Navigation loNavChild : loNavChildList)
					{
						if (loNavChild.getTabState() != null && loNavChild.getTabState().equalsIgnoreCase(lsFrom))
						{
							loNavChild.setTabState(asTo);
						}
					}
				}
			}
		}
	}

	/**
	 * This method is for Additional Rules of agency/accelerator user
	 * <ul>
	 * <li>Iterate over navigation elements</li>
	 * <li>Check if any additional rule exists and process accordingly</li>
	 * </ul>
	 * 
	 * @param aoRequest Portlet Request
	 * @param aoTabMap Tab Map
	 * @param aoChannel Channel
	 * @throws ApplicationException
	 */
	private void checkAdditionalRules(PortletRequest aoRequest, Map<String, Navigation> aoTabMap, Channel aoChannel)
			throws ApplicationException
	{
		String lsStatus = (String) aoChannel.getData(HHSConstants.COMPETITION_POOL_STATUS);
		PortletSession loSession = aoRequest.getPortletSession();
		String lsUserRole = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ROLE,
				PortletSession.APPLICATION_SCOPE);
		Navigation loNav = null;
		List<Navigation> loNavChildList = null;
		for (Map.Entry<String, Navigation> loEntry : aoTabMap.entrySet())
		{
			loNav = loEntry.getValue();
			loNavChildList = loNav.getChildList();
			if (loNavChildList != null)
			{
				for (Navigation loNavChild : loNavChildList)
				{
					if (null != loNavChild.getAdditionRuleName() && lsStatus != null)
					{
						String lsXPath = "//add-rule[(@id=\"" + loNavChild.getAdditionRuleName()
								+ "\")]/status[(@id=\"" + lsStatus + "\")]";
						Document loNavigationDom = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
								HHSConstants.NAVIGATION_SM);
						Element loStatusElement = (Element) XMLUtil.getElement(lsXPath, loNavigationDom);
						String lsScreenState = loStatusElement.getAttributeValue(HHSConstants.ELEMENT_STATE);
						if (lsScreenState != null && lsScreenState.equalsIgnoreCase(HHSConstants.EA8))
						{
							if (HHSConstants.PROGRAM_USERS_ROLES.contains(lsUserRole)
									|| HHSConstants.FINANCE_USERS_ROLES.contains(lsUserRole)
									|| HHSConstants.CFO_ROLE.equals(lsUserRole))
							{
								if (((Boolean) aoChannel.getData(HHSConstants.IS_AWARD_APPROVED_FOR_EVAL_POOL)))
								{
									lsScreenState = HHSConstants.E;
								}
								else
								{
									lsScreenState = HHSConstants.H;
								}
							}
							else
							{
								lsScreenState = HHSConstants.E;
							}
						}
						loNavChild.setTabState(lsScreenState);
					}
					else if (null != loNavChild.getAdditionRuleName())
					{
						loNavChild.setTabState(HHSConstants.H);
					}
				}
			}
		}
	}

	/**
	 * This method sets the navigation specific parameters in request
	 * <ul>
	 * <li>Retrieve navigation specific parameters "topLevelFromRequest" and
	 * "midLevelFromRequest", "competitionPoolId", "evaluationGroupId",
	 * "evaluationPoolMappingId", "ES" and sets them in response if not null</li>
	 * </ul>
	 * 
	 * @param aoRequest - Action Request
	 * @param aoResponse - Action Response
	 */
	protected void setNavigationParamsInRender(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		String lsTopTabId = PortalUtil.parseQueryString(aoRequest, HHSConstants.TOP_LEVEL_FROM_REQ);
		String lsMidTabId = PortalUtil.parseQueryString(aoRequest, HHSConstants.MID_LEVEL_FROM_REQ);
		String lsCompetitionPoolId = PortalUtil.parseQueryString(aoRequest, HHSConstants.COMPETITION_POOL_ID);
		String lsEvaluationGroupId = PortalUtil.parseQueryString(aoRequest, HHSConstants.EVALUATION_GROUP_ID);
		String lsEvaluationPoolMappingId = PortalUtil.parseQueryString(aoRequest,
				HHSConstants.EVALUATION_POOL_MAPPING_ID);
		String lsESValue = PortalUtil.parseQueryString(aoRequest, HHSConstants.ES);
		if (lsTopTabId != null)
		{
			aoResponse.setRenderParameter(HHSConstants.TOP_LEVEL_FROM_REQ, lsTopTabId);
		}
		if (lsMidTabId != null)
		{
			aoResponse.setRenderParameter(HHSConstants.MID_LEVEL_FROM_REQ, lsMidTabId);
		}
		if (lsCompetitionPoolId != null && !lsCompetitionPoolId.isEmpty())
		{
			aoResponse.setRenderParameter(HHSConstants.COMPETITION_POOL_ID, lsCompetitionPoolId);
		}
		if (lsESValue != null && !lsESValue.isEmpty())
		{
			aoResponse.setRenderParameter(HHSConstants.ES, lsESValue);
		}
		if (lsEvaluationGroupId != null && !lsEvaluationGroupId.isEmpty())
		{
			aoResponse.setRenderParameter(HHSConstants.EVALUATION_GROUP_ID, lsEvaluationGroupId);
		}
		if (lsEvaluationPoolMappingId != null && !lsEvaluationPoolMappingId.isEmpty())
		{
			aoResponse.setRenderParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvaluationPoolMappingId);
		}
	}

	/**
	 * This method handles action from task screen when Proposal Details
	 * hyperlink is clicked from Proposal Documents tab
	 * 
	 * Changes done for enhancement QC : 5688 for Release 3.2.0
	 * <ul>
	 * <li>This method will get proposal Id ,jsp path and procurement Id from
	 * request</li>
	 * <li>Set in Response parameters and render to proposal summary screen</li>
	 * <ul>
	 * 
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse object
	 */
	@ActionMapping(params = "submit_action=viewProposalSummary")
	protected void actionViewProposalSummary(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		String lsControllerAction = aoRequest.getParameter(HHSConstants.CONTROLLER_ACTION);
		aoResponse.setRenderParameter(HHSConstants.PROPOSAL_ID, aoRequest.getParameter(HHSConstants.PROPOSAL_ID));
		aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
		aoResponse.setRenderParameter(HHSConstants.JSP_PATH, aoRequest.getParameter(HHSConstants.JSP_PATH));
		aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.VIEW_RESPONSE);
		if (aoRequest.getParameter(HHSConstants.IS_PROC_DOCS_VISIBLE) != null)
		{
			aoResponse.setRenderParameter(HHSConstants.IS_PROC_DOCS_VISIBLE,
					aoRequest.getParameter(HHSConstants.IS_PROC_DOCS_VISIBLE));
		}
		if (null != aoRequest.getParameter(HHSConstants.FROM_AWARD_TASK))
		{
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
		}
		// Start ||Changes done for enhancement QC : 5688 for Release 3.2.0
		if (null != lsControllerAction && !lsControllerAction.isEmpty())
		{
			aoResponse.setRenderParameter(HHSConstants.JSP_PATH, HHSConstants.JSP_PATH_EVALUATION);
			aoResponse.setRenderParameter(HHSConstants.CONTROLLER_ACTION, HHSConstants.AGENCY_WORKFLOW_FOR_CITY);
		}
		// End ||Changes done for enhancement QC : 5688 for Release 3.2.0
	}

	/**
	 * Changes done for enhancement QC : 5688 for Release 3.2.0
	 * 
	 * On selection of "View Proposal" option from Actions drop down of S215 and
	 * S218 screen. This method will be called and take user on page S220 -
	 * Accelerator/Agency "View Proposal" and allows the user to view read only
	 * response and site information for the Procurement
	 * <ul>
	 * <li>1. Fetch procurement Id, proposal Id and lbProcDocsVisible from the
	 * RenderRequest object</li>
	 * <li>2. Set navigation tab related information using getHeader</li>
	 * <li>3. Create a Channel object and set required parameters(procurement
	 * Id, proposal Id, userType and User Id) in it</li>
	 * <li>4. Execute transaction <b>viewResponse</b></li>
	 * <li>5. Fetch proposal details bean, question-answer list, proposal site
	 * details list, proposal documents list and procurement title from the
	 * Channel object</li>
	 * <li>6. Redirect all the fetched results to "viewResponse" screen and
	 * return the same</li>
	 * </ul>
	 * R4 change- Map is added in the channel object to hold input parameters
	 * for the review score workflow check
	 * 
	 * @param aoRequest - a ResourceRequest object
	 * @param aoResponse - a ResourceResponse object
	 * @return - a ModelAndView object
	 * @throws ApplicationException when any exception occurred wraps it into
	 *             ApplicationException Updated Method in R4
	 */
	@SuppressWarnings("unchecked")
	@RenderMapping(params = "render_action=viewResponse")
	protected ModelAndView viewResponse(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		ProposalDetailsBean loProposalDetailsBean = null;
		String lsProcurementId = aoRequest.getParameter(HHSConstants.PROCUREMENT_ID);
		String lsJspPath = aoRequest.getParameter(HHSConstants.JSP_PATH);
		String lsProposalId = aoRequest.getParameter(HHSConstants.PROPOSAL_ID);
		Map<String, String> loRSWFRequiredProps = new HashMap<String, String>();
		String lsControllerAction = aoRequest.getParameter(HHSConstants.CONTROLLER_ACTION);

		try
		{
			if (lsJspPath != null && !lsJspPath.isEmpty() && !lsJspPath.startsWith(HHSConstants.EVALUATION_LOWER))
			{
				aoRequest.setAttribute(HHSConstants.JSP_PATH, HHSConstants.PROC_PATH);
			}
			PortletSession loSession = aoRequest.getPortletSession();
			String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);
			String lsOrgType = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
					PortletSession.APPLICATION_SCOPE);
			HashMap<String, String> loRequiredParamMap = new HashMap<String, String>();
			loRequiredParamMap.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, ApplicationConstants.EMPTY_STRING);
			P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			Channel loChannelObj = new Channel();
			String lsProcDocsVisible = aoRequest.getParameter(HHSConstants.IS_PROC_DOCS_VISIBLE);
			loChannelObj.setData(HHSConstants.HM_REQIRED_PROPERTY_MAP, loRequiredParamMap);
			loChannelObj.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
			loChannelObj.setData(HHSConstants.AS_USER_TYPE, lsOrgType);
			loChannelObj.setData(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
			loChannelObj.setData(HHSConstants.PROPOSAL_ID_KEY, lsProposalId);
			loChannelObj.setData(HHSConstants.AS_USER_ID, lsUserId);
			loChannelObj.setData(HHSConstants.AS_SORT_SITE_TABLE, HHSConstants.TRUE);
			// start R5 Proposal Activity History
			HashMap loProposalHashMap = new HashMap();
			loProposalHashMap.put(HHSConstants.PROPOSAL_ID, lsProposalId);
			loProposalHashMap.put(HHSConstants.PARAM_USER_COMMENT_TABLE_ENTITY_ID, HHSConstants.ACCEPT_PROPOSAL);
			loChannelObj.setData(HHSConstants.REQUIRED_PROPS, loProposalHashMap);
			// end R5 Proposal Activity History
			// added to pass input to a service used to check review score task
			// for BAFO Upload Button visibility
			loRSWFRequiredProps.put(P8Constants.PE_WORKFLOW_PROPOSAL_ID, lsProposalId);
			loRSWFRequiredProps.put(HHSConstants.F_SUBJECT, P8Constants.TASK_REVIEW_SCORES);
			loChannelObj.setData(HHSConstants.AO_HMWF_REQUIRED_PROPS, loRSWFRequiredProps);
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.VIEW_RESPONSE);
			loProposalDetailsBean = (ProposalDetailsBean) loChannelObj.getData(HHSConstants.PROPOSAL_DETAILS_BEAN);
			List<ProposalQuestionAnswerBean> loQueAnsList = (List<ProposalQuestionAnswerBean>) loChannelObj
					.getData(HHSConstants.CUSTOM_QUE_LIST);
			List<SiteDetailsBean> loSiteDetails = (List<SiteDetailsBean>) loChannelObj
					.getData(HHSConstants.SITE_DETAIL_LIST);
			loProposalDetailsBean.setQuestionAnswerBeanList(loQueAnsList);
			loProposalDetailsBean.setSiteDetailsList(loSiteDetails);
			loProposalDetailsBean.setServiceUnitFlag((String) loChannelObj.getData(HHSConstants.SERVICE_UNIT_VALUE));
			// start R5 Proposal Activity History
			List<CommentsHistoryBean> loListBean = (List<CommentsHistoryBean>) loChannelObj
					.getData(HHSR5Constants.PROPOSAL_TASK_HISTORY_LIST);
			aoRequest.setAttribute(HHSR5Constants.PROPOSAL_AUDIT_LIST, loListBean);
			// end R5 Proposal Activity History
			aoRequest.setAttribute(HHSConstants.PROPOSAL_DETAILS_BEAN_UPPERCASE, loProposalDetailsBean);
			aoRequest.setAttribute(HHSConstants.PROPOSAL_DOCUMENT_LIST,
					loChannelObj.getData(HHSConstants.FINAL_PROPOSAL_DOC_LIST));
			
            // Start QC9665 R 9.3.2 Citywide cyber assessment program (CCAP) - vulnerabilities -- Users Can Access Other User's Uploaded Documents
			aoRequest.getPortletSession().setAttribute(ApplicationConstants.SESSION_EXTENDED_DOCUMENT_LIST, loChannelObj.getData(HHSConstants.FINAL_PROPOSAL_DOC_LIST), PortletSession.APPLICATION_SCOPE);
			LOG_OBJECT.Info("save Document List in Session on Application scope");
			//End QC9665 R 9.3.2 Citywide cyber assessment program (CCAP) - vulnerabilities -- Users Can Access Other User's Uploaded Documents

			aoRequest.setAttribute(HHSConstants.IS_PROC_DOCS_VISIBLE, lsProcDocsVisible);
			aoRequest.setAttribute(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			aoRequest.setAttribute(HHSConstants.PROCUREMENT_TITLE,
					(String) loChannelObj.getData(HHSConstants.PROCUREMENT_TITLE));
			aoRequest.setAttribute(HHSConstants.STATUS_FLAG, (Boolean) loChannelObj.getData(HHSConstants.STATUS_FLAG));
			aoRequest.setAttribute(HHSConstants.SHOW_BAFO_BUTTON,
					PortalUtil.parseQueryString(aoRequest, HHSConstants.SHOW_BAFO_BUTTON));
			// if the success attribute in the session is not null and equals to
			// success -- added for BAFO upload success
			if (null != PortalUtil.parseQueryString(aoRequest, HHSConstants.SUCCESS)
					&& PortalUtil.parseQueryString(aoRequest, HHSConstants.SUCCESS).equalsIgnoreCase(
							HHSConstants.UPLOAD))
			{
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
						ApplicationConstants.ERROR_MESSAGE_PROP_FILE, HHSConstants.FILE_UPLOAD_PASS_MESSAGE));
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_PASS_TYPE);
			}
		}
		// Handling Exception while rendering view response
		catch (ApplicationException aoExp)
		{
			setGenericErrorMessage(aoRequest);
			LOG_OBJECT.Error("Error occured while fetching proposal details", aoExp);
		}
		// Handling Exception other than ApplicationException
		catch (Exception aoExp)
		{
			setGenericErrorMessage(aoRequest);
			LOG_OBJECT.Error("Error occured while fetching proposal details", aoExp);
		}
		// Start || Changes done for enhancement QC : 5688 for Release 3.2.0
		if (null != lsControllerAction && !lsControllerAction.isEmpty())
		{
			return new ModelAndView(lsJspPath + HHSConstants.VIEW_RESPONSE_ACCELERATOR,
					HHSConstants.PROPOSAL_DETAILS_BEAN_UPPERCASE, loProposalDetailsBean);
		}
		else
		{
			return new ModelAndView(lsJspPath + HHSConstants.VIEW_RESPONSE,
					HHSConstants.PROPOSAL_DETAILS_BEAN_UPPERCASE, loProposalDetailsBean);
		}
		// End || Changes done for enhancement QC : 5688 for Release 3.2.0
	}

	/**
	 * This function opens the organization read only screen corresponding to an
	 * organization_id
	 * 
	 * Modified as a part of Enhancement #5688 for Release 3.2.0
	 * 
	 * <ul>
	 * <li>1. Fetch organization Id from request object</li>
	 * <li>2. Fetch organization basic data corresponding to the organization Id
	 * < Executes the the query <b>retrieve_questionanswer_withcomments</b></li>
	 * <li>3. Convert the fetched data into HTML format.</li>
	 * <li>4. Redirect the fetched data to organizationReadOnlyScreen</li>
	 * </ul>
	 * 
	 * @param aoRequest - a ResourceRequest object
	 * @param aoResponse - a ResourceResponse object
	 * @return loModelAndView - an ModelAndView object
	 * @throws ApplicationException if any exception occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	@ResourceMapping("organizationSummary")
	public ModelAndView openOrganization(ResourceRequest aoRequest, ResourceResponse aoResponse)
			throws ApplicationException
	{
		ModelAndView loModelAndView = null;
		// Modified as a part of Enhancement #5688 for Release 3.2.0
		String lsControllerAction = aoRequest.getParameter(HHSConstants.CONTROLLER_ACTION);
		String lsJspPath = null;
		try
		{
			aoResponse.setContentType(HHSConstants.TEXT_HTML);
			Channel loChannelObj = new Channel();
			String lsOrganizationId = aoRequest.getParameter(HHSConstants.ORGANIZATION_ID);
			// Start || Modified as a part of Enhancement #5688 for Release
			// 3.2.0
			if (null != lsControllerAction && !lsControllerAction.isEmpty())
			{
				lsJspPath = HHSConstants.JSP_PATH_EVALUATION;
			}
			else
			{
				lsJspPath = aoRequest.getParameter(HHSConstants.JSP_PATH);
			}
			// End || Modified as a part of Enhancement #5688 for Release 3.2.0
			loChannelObj.setData(HHSConstants.ORG_ID, lsOrganizationId);
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.RET_QUE_ANS_WITH_COMMENTS);
			HashMap loHMAnswerMap = (HashMap) loChannelObj.getData(HHSConstants.LO_FORM_INFO);
			String lsPathToReadQuestions = aoRequest.getPortletSession().getPortletContext()
					.getRealPath(ApplicationConstants.FORMS_FOLDER_NAME);
			if (lsPathToReadQuestions.lastIndexOf(HHSConstants.CHAR_FORWARDSLASH) > HHSConstants.INT_MINUS_ONE)
			{
				lsPathToReadQuestions = lsPathToReadQuestions.substring(HHSConstants.INT_ZERO,
						lsPathToReadQuestions.lastIndexOf(HHSConstants.CHAR_FORWARDSLASH));
			}
			if (lsPathToReadQuestions.lastIndexOf(HHSConstants.CHAR_BACKSLASH) > HHSConstants.INT_MINUS_ONE)
			{
				lsPathToReadQuestions = lsPathToReadQuestions.substring(HHSConstants.INT_ZERO,
						lsPathToReadQuestions.lastIndexOf(HHSConstants.CHAR_BACKSLASH));
			}
			if (loHMAnswerMap.containsKey(HHSConstants.FORM_NAME)
					&& loHMAnswerMap.get(HHSConstants.FORM_NAME).equals(HHSConstants.BASIC))
			{
				loHMAnswerMap.put(HHSConstants.FORM_NAME, HHSConstants.ORG_PROFILE);
			}
			StringBuffer loSbHTMLForPrintView = BusinessApplicationUtil.getFormHTML(loHMAnswerMap,
					lsPathToReadQuestions, false);
			aoRequest.setAttribute(HHSConstants.OPEN_NEW_WINDOW, loSbHTMLForPrintView.toString());
			if (lsJspPath == null)
			{
				lsJspPath = HHSConstants.EMPTY_STRING;
			}
			loModelAndView = new ModelAndView(lsJspPath + HHSConstants.ORG_READ_ONLY_SCREEN);
		}

		catch (ApplicationException loEx)
		{
			// Catch the exception thrown by transaction
			setExceptionMessageInResponse(aoRequest);
		}
		// handling exception other than Application Exception.
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Exception Occured while processing action on click of hyperlink", loEx);
			setExceptionMessageInResponse(aoRequest);
		}
		return loModelAndView;
	}

	/**
	 * This method will fetch the details of the selected document and display
	 * the properties to the end user This method will execute the method
	 * <b>actionViewDocumentInfo</b> method of <b>FileNetOperationsUtils</b> and
	 * the above mention method will execute one R1 transaction with the
	 * transaction id <b>displayDocProp_filenet</b> class and then set the
	 * render action for document view
	 * 
	 * @param aoRequest - ActionRequest Object
	 * @param aoResponse - ActionResponse Object
	 */
	@Override
	@ActionMapping(params = "submit_action=viewDocumentInfo")
	protected void viewDocumentInfoAction(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		String lsUserOrgType = null;
		try
		{
			lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			String lsViewDocInfoFromAgency = aoRequest.getParameter(HHSConstants.IS_VIEW_DOC_INFO_FROM_AGENCY);
			if (lsViewDocInfoFromAgency != null)
			{
				aoRequest.setAttribute(HHSConstants.ORGTYPE, ApplicationConstants.PROVIDER_ORG);
				lsUserOrgType = ApplicationConstants.PROVIDER_ORG;
			}
			String lsProcurementId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
			String lsProposalId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROPOSAL_ID);
			String lsProcurementDocId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.HIDDEN_DOC_REF_SEQ_NO);
			String lsIsAddendum = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.IS_ADD_TYPE);
			String lsEditingDocType = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.UPLOAD_DOC_TYPE);
			String lsPageReadOnly = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PAGE_READ_ONLY);
			setNavigationParamsInRender(aoRequest, aoResponse);
			FileNetOperationsUtils.actionViewDocumentInfo(aoRequest, aoResponse);
			if (aoRequest.getParameter(HHSConstants.JSP_PATH) != null)
			{
				aoResponse.setRenderParameter(HHSConstants.JSP_PATH, aoRequest.getParameter(HHSConstants.JSP_PATH));
			}
			// if the logged in user belongs to the provider organization type
			if (ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(lsUserOrgType))
			{
				setRenderParameterByUserType(aoRequest, aoResponse, lsProposalId, lsProcurementDocId, lsEditingDocType);
			}
			// if the user belongs to the accelerator organization type
			else if (ApplicationConstants.CITY_ORG.equalsIgnoreCase(lsUserOrgType))
			{
				if (null != PortalUtil.parseQueryString(aoRequest, HHSConstants.TOP_LEVEL_FROM_REQ))
				{
					aoResponse.setRenderParameter(HHSConstants.TOP_LEVEL_FROM_REQ,
							PortalUtil.parseQueryString(aoRequest, HHSConstants.TOP_LEVEL_FROM_REQ));
				}
				if (null != PortalUtil.parseQueryString(aoRequest, HHSConstants.MID_LEVEL_FROM_REQ))
				{
					aoResponse.setRenderParameter(HHSConstants.MID_LEVEL_FROM_REQ,
							PortalUtil.parseQueryString(aoRequest, HHSConstants.MID_LEVEL_FROM_REQ));
				}
				if (null != lsIsAddendum)
				{
					aoResponse.setRenderParameter(HHSConstants.IS_ADD_TYPE, lsIsAddendum);
				}
			}
			if (null != lsProcurementId)
			{
				aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			}
			if (null != lsPageReadOnly)
			{
				aoResponse.setRenderParameter(HHSConstants.PAGE_READ_ONLY, lsPageReadOnly);
			}
			if (null != aoRequest.getParameter(HHSConstants.FROM_AWARD_TASK))
			{
				aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.PROPOSAL_EVALUATION_ACTION);
			}
			else
			{
				aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
			}
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, ApplicationConstants.VIEW_DOCUMENT_INFO);
		}
		// Catch the application exception thrown by transaction and set the
		// error message
		// in request object and pass to jsp
		catch (ApplicationException aoAppExp)
		{
			String lsErrorMsg = aoAppExp.toString();
			lsErrorMsg = lsErrorMsg.substring(lsErrorMsg.lastIndexOf(HHSConstants.CHAR_COLON) + HHSConstants.INT_ONE,
					lsErrorMsg.length()).trim();
			if (lsErrorMsg.isEmpty())
			{
				lsErrorMsg = HHSConstants.INTERNAL_ERROR_OCCURED_WHILE_PROCESSING_YOUR_REQUEST;
			}
			LOG_OBJECT.Error(lsErrorMsg, aoAppExp);
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE);
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MAP, aoAppExp.getContextData().toString());
			aoRequest.getPortletSession().removeAttribute(ApplicationConstants.ERROR_MESSAGE);
			aoRequest.getPortletSession().removeAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE);
		}
	}

	/**
	 * This method is used to set render parameter on the basis of type of user
	 * 
	 * @param aoRequest
	 * @param aoResponse
	 * @param asProposalId
	 * @param lsProcurementDocId
	 * @param lsEditingDocType
	 */
	private void setRenderParameterByUserType(ActionRequest aoRequest, ActionResponse aoResponse, String asProposalId,
			String lsProcurementDocId, String lsEditingDocType)
	{
		if (null != PortalUtil.parseQueryString(aoRequest, HHSConstants.TOP_LEVEL_FROM_REQ))
		{
			aoResponse.setRenderParameter(HHSConstants.TOP_LEVEL_FROM_REQ,
					PortalUtil.parseQueryString(aoRequest, HHSConstants.TOP_LEVEL_FROM_REQ));
		}
		if (null != PortalUtil.parseQueryString(aoRequest, HHSConstants.MID_LEVEL_FROM_REQ))
		{
			aoResponse.setRenderParameter(HHSConstants.MID_LEVEL_FROM_REQ,
					PortalUtil.parseQueryString(aoRequest, HHSConstants.MID_LEVEL_FROM_REQ));
		}
		if (null != lsProcurementDocId)
		{
			aoResponse.setRenderParameter(HHSConstants.HIDDEN_DOC_REF_SEQ_NO, lsProcurementDocId);
		}
		if (null != asProposalId)
		{
			aoResponse.setRenderParameter(HHSConstants.PROPOSAL_ID, asProposalId);
		}
		if (null != lsEditingDocType)
		{
			aoResponse.setRenderParameter(HHSConstants.UPLOAD_DOC_TYPE, lsEditingDocType);
		}
	}

	/**
	 * This method will set the document object bean into request and render
	 * forward the user to the view document info screen with all the details
	 * which later displayed to the user.
	 * 
	 * @param aoRequest RenderRequest Object
	 * @param aoResponse RenderResponse Object
	 * @return ModelAndView Object with details where to navigate the user
	 */
	@Override
	@RenderMapping(params = "render_action=viewDocumentInfo")
	protected ModelAndView viewDocumentInfoRender(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		PortletSession loSession = null;
		String lsJspPath = aoRequest.getParameter(HHSConstants.JSP_PATH);
		if (lsJspPath == null)
		{
			lsJspPath = HHSConstants.EMPTY_STRING;
		}
		try
		{
			loSession = aoRequest.getPortletSession();
			String lsProcurementId = PortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
			String lsProposalId = PortalUtil.parseQueryString(aoRequest, HHSConstants.PROPOSAL_ID);
			String lsDocumentStatus = PortalUtil.parseQueryString(aoRequest, HHSConstants.DOC_STATUS);
			String lsProcurementDocId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.HIDDEN_DOC_REF_SEQ_NO);
			String lsEditingDocumenType = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.UPLOAD_DOC_TYPE);
			String lsPageReadOnly = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PAGE_READ_ONLY);
			getPageHeader(aoRequest, lsProcurementId);
			aoRequest.setAttribute(ApplicationConstants.EDIT_VERSION_PROP,
					aoRequest.getParameter(ApplicationConstants.EDIT_VERSION_PROP));
			aoRequest.setAttribute(ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_PARAMETER,
					(com.nyc.hhs.model.Document) ApplicationSession.getAttribute(aoRequest, true,
							ApplicationConstants.SESSION_DOCUMENT_OBJ));
			aoRequest.setAttribute(ApplicationConstants.IS_LOCKED_STATUS,
					aoRequest.getParameter(ApplicationConstants.IS_LOCKED_STATUS));
			if (null != lsProcurementDocId)
			{
				aoRequest.setAttribute(HHSConstants.HIDDEN_DOC_REF_SEQ_NO, lsProcurementDocId);
			}
			aoRequest.setAttribute(HHSConstants.DOC_STATUS, lsDocumentStatus);
			if (null != lsPageReadOnly)
			{
				aoRequest.setAttribute(HHSConstants.PAGE_READ_ONLY, lsPageReadOnly);
			}
			aoRequest.setAttribute(HHSConstants.PROPOSAL_ID, lsProposalId);
			aoRequest.setAttribute(HHSConstants.UPLOAD_DOC_TYPE, lsEditingDocumenType);
			aoRequest.setAttribute(HHSConstants.TOP_LEVEL_FROM_REQ,
					PortalUtil.parseQueryString(aoRequest, HHSConstants.TOP_LEVEL_FROM_REQ));
			aoRequest.setAttribute(HHSConstants.MID_LEVEL_FROM_REQ,
					PortalUtil.parseQueryString(aoRequest, HHSConstants.MID_LEVEL_FROM_REQ));
			setExceptionMessageInResponse(aoRequest);
		}
		// Catch the application exception thrown by transaction and set the
		// error message
		// in request object and pass to jsp
		catch (ApplicationException aoExp)
		{

			String lsErrorMsg = aoExp.toString();
			lsErrorMsg = lsErrorMsg.substring(lsErrorMsg.lastIndexOf(HHSConstants.CHAR_COLON) + HHSConstants.INT_ONE,
					lsErrorMsg.length()).trim();
			if (lsErrorMsg.isEmpty())
			{
				lsErrorMsg = HHSConstants.INTERNAL_ERROR_OCCURED_WHILE_PROCESSING_YOUR_REQUEST;
			}
			LOG_OBJECT.Error(lsErrorMsg, aoExp);
			loSession.setAttribute(ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
					ApplicationConstants.DOCUMENT_EXCEPTION, PortletSession.APPLICATION_SCOPE);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MAP, aoExp.getContextData());
		}
		// Catch the IO exception thrown by transaction and set the error
		// message
		// in request object and pass to jsp
		catch (Exception loException)
		{
			LOG_OBJECT.Error("Application Exception in Document Vault", loException);
			setGenericErrorMessage(aoRequest);
		}
		return new ModelAndView(lsJspPath + HHSConstants.VIEW_DOC_INFO);
	}

	/**
	 * This method will save the edited details of the document
	 * <ul>
	 * <li>Get document id from request.</li>
	 * <li>Get document properties from request.</li>
	 * <li>if the document is procurement type document execute
	 * <code>updateRfpDocumentProperties</code></li>
	 * <li>if the document is award type document execute
	 * <code>updateAwardDocumentProperties</code></li>
	 * <li>if the document is proposal type document execute
	 * <code>updateProposalDocumentProperties</code></li>
	 * </ul>
	 * 
	 * @param aoRequest - ActionRequest Object
	 * @param aoResponse - ActionResponse Object
	 */
	@Override
	@SuppressWarnings("unchecked")
	@ActionMapping(params = "submit_action=saveDocumentProperties")
	protected void saveDocumentPropertiesAction(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		PortletSession loSession = null;
		String lsIsAddendumType = HHSConstants.EMPTY_STRING;
		String lsProcurementId = HHSConstants.EMPTY_STRING;
		String lsProposalId = HHSConstants.EMPTY_STRING;
		String lsProcurementDocId = HHSConstants.EMPTY_STRING;
		@SuppressWarnings("rawtypes")
		HashMap loHmDocReqProps = new HashMap();
		com.nyc.hhs.model.Document loDocument = null;
		boolean lbSkipPropertySave = false;
		try
		{
			loSession = aoRequest.getPortletSession();
			String lsUserName = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_NAME,
					PortletSession.APPLICATION_SCOPE);
			String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);
			String lsUserOrgType = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
					PortletSession.APPLICATION_SCOPE);
			String lsOrgId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
					PortletSession.APPLICATION_SCOPE);
			P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			String lsDocumentName = aoRequest.getParameter(HHSConstants.DOC_NAME);
			lsIsAddendumType = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.IS_ADD_TYPE);
			lsProcurementId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
			lsProposalId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROPOSAL_ID);
			lsProcurementDocId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.HIDDEN_DOC_REF_SEQ_NO);
			String lsEditingDocumentType = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.UPLOAD_DOC_TYPE);
			List<DocumentPropertiesBean> loNewPropertiesList = new ArrayList<DocumentPropertiesBean>();
			loDocument = (com.nyc.hhs.model.Document) ApplicationSession.getAttribute(aoRequest, true,
					ApplicationConstants.SESSION_DOCUMENT_OBJ);
			if (ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(lsUserOrgType))
			{
				lbSkipPropertySave = FileNetOperationsUtils.isValidRenameEditProperties(lsDocumentName, loDocument,
						lsOrgId, lsUserOrgType, loUserSession);
			}
			else if (ApplicationConstants.CITY_ORG.equalsIgnoreCase(lsUserOrgType))
			{
				lbSkipPropertySave = FileNetOperationsUtils.isValidRenameEditProperties(lsDocumentName, loDocument,
						lsUserId, lsUserOrgType, loUserSession);
			}
			if (!lbSkipPropertySave)
			{
				setRenderParameterForValidProperties(aoRequest, aoResponse, lsIsAddendumType, lsProcurementId,
						lsProposalId, lsProcurementDocId, loHmDocReqProps, loDocument, lsUserName, lsUserId,
						lsUserOrgType, loUserSession, lsDocumentName, lsEditingDocumentType, loNewPropertiesList);
			}
			if (lbSkipPropertySave)
			{
				String lsErrorMsg = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE,
						"ERR_LINKED_TO_APP_RENAME");
				loSession.setAttribute(ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
						ApplicationConstants.DOCUMENT_EXCEPTION, PortletSession.APPLICATION_SCOPE);
				loSession
						.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg, PortletSession.APPLICATION_SCOPE);
				loSession.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE,
						PortletSession.APPLICATION_SCOPE);
				aoResponse.sendRedirect(aoRequest.getContextPath() + ApplicationConstants.ERROR_HANDLER);
			}
		}
		// Catch the application exception thrown by transaction and set the
		// error message
		// in request object and pass to jsp
		catch (ApplicationException aoExp)
		{
			ApplicationSession.setAttribute(loDocument, aoRequest, ApplicationConstants.SESSION_DOCUMENT_OBJ);
			try
			{
				String lsAjaxCall = aoRequest.getParameter(ApplicationConstants.IS_AJAX_CALL);
				if (null != lsAjaxCall && !lsAjaxCall.equalsIgnoreCase(HHSConstants.TRUE))
				{
					aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID,
							PortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID));
					aoResponse.setRenderParameter(HHSConstants.PROPOSAL_ID,
							PortalUtil.parseQueryString(aoRequest, HHSConstants.PROPOSAL_ID));
					aoResponse.setRenderParameter(HHSConstants.DOC_STATUS,
							PortalUtil.parseQueryString(aoRequest, HHSConstants.DOC_STATUS));
					aoResponse.setRenderParameter(HHSConstants.HIDDEN_DOC_REF_SEQ_NO,
							HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.HIDDEN_DOC_REF_SEQ_NO));
					aoResponse.setRenderParameter(HHSConstants.UPLOAD_DOC_TYPE,
							HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.UPLOAD_DOC_TYPE));
					aoResponse.setRenderParameter(HHSConstants.PAGE_READ_ONLY,
							HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PAGE_READ_ONLY));
				}
				setErrorMessageInResponse(aoRequest, aoResponse, aoExp, HHSConstants.VIEW_DOC_INFO_UPPERCASE);
			}
			catch (IOException aoIoEx)
			{
				LOG_OBJECT.Error("Io Exception in Document Vault", aoExp);
			}
			LOG_OBJECT.Error("Application Exception in Document Vault", aoExp);
		}
		catch (IOException aoIoEx)
		{
			LOG_OBJECT.Error("Io Exception in Document Vault", aoIoEx);
		}
	}

	/**
	 * 
	 * @param aoRequest -- ActionRequest Object
	 * @param aoResponse -- ActionResponse Object
	 * @param asIsAddendumType -- Addendum type
	 * @param asProcurementId -- Procurement Id
	 * @param asProposalId -- Proposal Id
	 * @param asProcurementDocId -- Procurement Document Id
	 * @param aoHmDocReqProps -- Document Map
	 * @param aoDocument -- Document Bean Object
	 * @param asUserName -- User Name
	 * @param asUserId -- User Id
	 * @param asUserOrgType -- Organization Type
	 * @param aoUserSession -- P8UserSession
	 * @param asDocumentName -- Document Name
	 * @param asEditingDocumentType -- Edited Document Type
	 * @param aoNewPropertiesList -- Properties List
	 * @throws ApplicationException
	 */
	private void setRenderParameterForValidProperties(ActionRequest aoRequest, ActionResponse aoResponse,
			String asIsAddendumType, String asProcurementId, String asProposalId, String asProcurementDocId,
			HashMap aoHmDocReqProps, com.nyc.hhs.model.Document aoDocument, String asUserName, String asUserId,
			String asUserOrgType, P8UserSession aoUserSession, String asDocumentName, String asEditingDocumentType,
			List<DocumentPropertiesBean> aoNewPropertiesList) throws ApplicationException
	{
		FileNetOperationsUtils.setPropertyBeanForFileUpload(aoRequest, aoDocument, aoHmDocReqProps);
		List<DocumentPropertiesBean> loDocumentPropsBeans = aoDocument.getDocumentProperties();
		Iterator<DocumentPropertiesBean> loDocPropsIt = loDocumentPropsBeans.iterator();
		//Updated in release 4.0.1- for removing mismatch in modified date
		String lsCurrentDate = DateUtil.getCurrentDateWithTimeStamp();
		//Updated in release 4.0.1- for removing mismatch in modified date end
		// iterate through the document properties
		if (null != loDocPropsIt)
		{
			prepareNewPropetiesList(aoRequest, aoHmDocReqProps, aoNewPropertiesList, loDocPropsIt);
		}
		aoHmDocReqProps.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, asDocumentName);
		aoHmDocReqProps.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY, asUserName);
		aoHmDocReqProps.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY_ID, asUserId);
		aoHmDocReqProps.put(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE, lsCurrentDate);
		aoHmDocReqProps.put(HHSConstants.PROC_DOC_ID, asProcurementDocId);
		updateDocumentProperties(aoRequest, aoResponse, asIsAddendumType, asProcurementId, asProposalId,
				aoHmDocReqProps, asUserId, aoUserSession, asDocumentName, aoNewPropertiesList, aoDocument,
				asUserOrgType, asEditingDocumentType);
		aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, asProcurementId);
		if (null != asProposalId)
		{
			aoResponse.setRenderParameter(HHSConstants.PROPOSAL_ID, asProposalId);
		}
		FileNetOperationsUtils.actionViewDocumentInfo(aoRequest, aoResponse);
		aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
		aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, ApplicationConstants.VIEW_DOCUMENT_INFO);
		if (null != PortalUtil.parseQueryString(aoRequest, HHSConstants.TOP_LEVEL_FROM_REQ))
		{
			aoResponse.setRenderParameter(HHSConstants.TOP_LEVEL_FROM_REQ,
					PortalUtil.parseQueryString(aoRequest, HHSConstants.TOP_LEVEL_FROM_REQ));
		}
		if (null != PortalUtil.parseQueryString(aoRequest, HHSConstants.MID_LEVEL_FROM_REQ))
		{
			aoResponse.setRenderParameter(HHSConstants.MID_LEVEL_FROM_REQ,
					PortalUtil.parseQueryString(aoRequest, HHSConstants.MID_LEVEL_FROM_REQ));
		}
	}

	/**
	 * This method will traverse through the document properties and set them
	 * into the final propeties list
	 * 
	 * @param aoRequest action request object
	 * @param aoHmDocReqProps document required properties list
	 * @param aoNewPropertiesList new document properties list
	 * @param aoDocPropsIt document properties iterator
	 * @throws ApplicationException if any exception occurred
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private void prepareNewPropetiesList(ActionRequest aoRequest, HashMap aoHmDocReqProps,
			List<DocumentPropertiesBean> aoNewPropertiesList, Iterator<DocumentPropertiesBean> aoDocPropsIt)
			throws ApplicationException
	{
		try
		{
			while (aoDocPropsIt.hasNext())
			{
				DocumentPropertiesBean loDocProps = aoDocPropsIt.next();
				// if the property type is boolean
				if (ApplicationConstants.PROPERTY_TYPE_BOOLEAN.equalsIgnoreCase(loDocProps.getPropertyType()))
				{
					if (HHSConstants.ON.equalsIgnoreCase(aoRequest.getParameter(loDocProps.getPropertyId()))
							|| HHSConstants.YES_LOWERCASE.equalsIgnoreCase(aoRequest.getParameter(loDocProps
									.getPropertyId())))
					{
						aoHmDocReqProps.put(loDocProps.getPropSymbolicName(), true);
						loDocProps.setPropValue(true);
					}
					else
					{
						aoHmDocReqProps.put(loDocProps.getPropSymbolicName(), false);
						loDocProps.setPropValue(false);
					}
				}
				else
				{
					aoHmDocReqProps.put(loDocProps.getPropSymbolicName(),
							aoRequest.getParameter(loDocProps.getPropertyId()));
					loDocProps.setPropValue(aoRequest.getParameter(loDocProps.getPropertyId()));
				}
				aoNewPropertiesList.add(loDocProps);
			}
		}
		catch (Exception aoExp)
		{
			// Catch the application exception thrown by transaction and set the
			// error message
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While Preparing final document properties list", aoExp);
			LOG_OBJECT.Error("Error Occured While Preparing final document properties list", loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * this method is used to update the document property in the filenet
	 * <ul>
	 * <li>Get all the details from the request object</li>
	 * <li>Execute transaction <code>updateRfpDocumentProperties</code></li>
	 * </ul>
	 * 
	 * @param aoRequest Action Request Request object
	 * @param aoResponse Action Response Object
	 * @param asIsAddendumType String document addendum type
	 * @param asProcurementId String procurement id
	 * @param asProposalId String ProposalId id
	 * @param aoHmDocReqProps HashMap document required properties
	 * @param asUserId String user id
	 * @param aoUserSession P8UserSession user session object
	 * @param asDocumentName String document name
	 * @param aoNewPropertiesList List<DocumentPropertiesBean> new properties
	 *            value changed
	 * @param aoDocument Document document bean object
	 * @param asUserOrgType String
	 * @param asEditingDocumentType String type of document editing
	 * @throws ApplicationException if any exception occurs.
	 */
	@SuppressWarnings("unchecked")
	private void updateDocumentProperties(ActionRequest aoRequest, ActionResponse aoResponse, String asIsAddendumType,
			String asProcurementId, String asProposalId, @SuppressWarnings("rawtypes") HashMap aoHmDocReqProps,
			String asUserId, P8UserSession aoUserSession, String asDocumentName,
			List<DocumentPropertiesBean> aoNewPropertiesList, com.nyc.hhs.model.Document aoDocument,
			String asUserOrgType, String asEditingDocumentType) throws ApplicationException
	{
		Channel loChannel = new Channel();
		PortletSession loSession = aoRequest.getPortletSession();

		loChannel.setData(HHSConstants.AO_FILENET_SESSION, aoUserSession);
		loChannel.setData(ApplicationConstants.DOCUMENT_ID, aoDocument.getDocumentId());
		loChannel.setData(HHSConstants.PROPOSAL_ID, asProposalId);
		loChannel.setData(ApplicationConstants.DOCS_TYPE, aoDocument.getDocType());
		aoHmDocReqProps.put(HHSConstants.MODIFIED_BY, asUserId);
		aoHmDocReqProps.put(HHSConstants.DOC_ID, aoDocument.getDocumentId());
		aoHmDocReqProps.put(HHSConstants.MODIFIED_DATE, DateUtil.getSqlDate(DateUtil.getCurrentDate()));
		aoHmDocReqProps.put(HHSConstants.IS_ADDENDUM, asIsAddendumType);
		aoHmDocReqProps.put(HHSConstants.PROCUREMENT_ID, asProcurementId);
		loChannel.setData(HHSConstants.AO_MODIFIED_INFO_MAP, aoHmDocReqProps);
		String lsTransactionname = HHSUtil.documentTypeTransactionName(asEditingDocumentType, asUserOrgType);
		HHSTransactionManager.executeTransaction(loChannel, lsTransactionname);
		boolean lbDocPropUpdated = (Boolean) loChannel.getData(HHSConstants.SAVE_STATUS);
		// if the document properties updated successfully
		if (lbDocPropUpdated)
		{
			String lsErrorMsg = HHSConstants.DOC_DETAILS_UPDATED_SUCCESFULLY;
			loSession.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg, PortletSession.APPLICATION_SCOPE);
			loSession.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_PASS_TYPE,
					PortletSession.APPLICATION_SCOPE);
		}
		else
		{
			String lsErrorMsg = HHSConstants.ERROR_OCCURED_SAVING_DOC_PROPERTIES;
			loSession.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg, PortletSession.APPLICATION_SCOPE);
			loSession.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_PASS_TYPE,
					PortletSession.APPLICATION_SCOPE);
		}
		aoDocument.setDocumentProperties(aoNewPropertiesList);
		aoDocument.setDocName(asDocumentName);

	}

	/**
	 * This resource request will set the lock id for the edit link once one
	 * user click on edit
	 * 
	 * @param aoRequest resource request object
	 * @param aoResponse resource response object
	 * @throws ApplicationException when any exception occurs
	 */
	@ResourceMapping("editDocumentProperties")
	protected String editDocumentPropertiesGetLock(ResourceRequest aoRequest, ResourceResponse aoResponse)
			throws ApplicationException
	{
		aoRequest.setAttribute(HHSConstants.DOC_ID, HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.DOC_ID));
		return "viewdocumentinfo";
	}

	/**
	 * <ul>
	 * <li>New method - Added By: Abhishek Saxena</li>
	 * <li>Reason: Enhancement id: 5415</li>
	 * <li>This method is added as part of Production Support release (2.5.0)</li>
	 * <li>Changes done for enhancement QC : 5688 for Release 3.2.0</li>
	 * </ul>
	 * <ul>
	 * <li>1. Add multiple input parameters to a Map</li>
	 * <li>2. Call the transaction: fetchEvaluationScoreDetailsForEvaluator to
	 * fetch the score details which includes criteria and comments too for
	 * requested evaluator based on evaluationStatusId.</li>
	 * </ul>
	 * 
	 * @param aoResourceRequest -- Portlet Resource Request Object -- to get
	 *            screen parameters and next action to be performed
	 * @param aoResourceResponse --Portlet Resource Reponse Object -- decides
	 *            the next execution flow
	 * @throws ApplicationException If an Application Exception occurs
	 * @throws Exception If an Exception occurs
	 */
	@SuppressWarnings(
	{ "unchecked" })
	@ResourceMapping("viewEvaluatorComments")
	protected ModelAndView viewEvaluatorComments(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		Channel loChannel = new Channel();
		// Start || Changes done for enhancement QC : 5688 for Release 3.2.0
		String lsControllerAction = aoResourceRequest.getParameter(HHSConstants.CONTROLLER_ACTION);
		String lsJspName = null;
		if (null != lsControllerAction && !lsControllerAction.isEmpty())
		{
			lsJspName = HHSConstants.CITY_WORKFLOW_SCOREDETAILS;
		}
		else
		{
			lsJspName = HHSConstants.AGENCY_WORKFLOW_SCOREDETAILS;
		}
		// End || Changes done for enhancement QC : 5688 for Release 3.2.0
		if (aoResourceRequest.getParameter(HHSConstants.BASE_HDN_TAB_NAME) != null)
		{
			lsJspName = HHSConstants.EVALUATION_LOWER + "/"
					+ aoResourceRequest.getParameter(HHSConstants.BASE_HDN_TAB_NAME);
		}
		ModelAndView loModelAndView = new ModelAndView(lsJspName);
		aoResourceResponse.setContentType(HHSConstants.TEXT_HTML);
		try
		{
			String lsEvaluatorName = aoResourceRequest.getParameter(HHSConstants.EVALUATOR_NAME);
			aoResourceRequest.setAttribute(HHSConstants.EVALUATOR_NAME, lsEvaluatorName);
			String lsEvaluationStatusId = aoResourceRequest.getParameter(HHSConstants.EVALUATION_STATUS_ID);
			Map<String, Object> loQueryMap = new HashMap<String, Object>();
			loQueryMap.put(HHSConstants.EVALUATION_STATUS_ID, lsEvaluationStatusId);

			// R5 starts Added versionNumber and proposalId
			String lsVersionNumber = aoResourceRequest.getParameter(HHSR5Constants.VERSION_NUMBER);
			String lsProposalId = aoResourceRequest.getParameter(HHSR5Constants.PROPOSAL_ID);
			loQueryMap.put(HHSR5Constants.VERSION_NUMBER, lsVersionNumber);
			loQueryMap.put(HHSConstants.PROPOSAL_ID, lsProposalId);
			// R5 ends Added versionNumber and proposalId

			loChannel.setData(HHSConstants.AO_QUERY_MAP, loQueryMap);
			// This transaction fetches the list of EvaluationBean having
			// details of Criteria, Scores and comments.
			HHSTransactionManager.executeTransaction(loChannel,
					HHSConstants.FETCH_EVALUATION_SCORE_DETAILS_FOR_EVALUATOR);
			List<EvaluationBean> loEvaluationScoreDetailsList = (List<EvaluationBean>) loChannel
					.getData(HHSConstants.AO_EVALUATION_BEAN_LIST);
			aoResourceRequest.setAttribute(HHSConstants.EVALUATION_SCORE_DETAILS_LIST, loEvaluationScoreDetailsList);
		}
		catch (ApplicationException aoExe)
		{
			LOG_OBJECT.Error("ApplicationException occured in viewEvaluatorComments", aoExe);
		}
		catch (Exception aoExe)
		{
			LOG_OBJECT.Error("Exception occured in viewEvaluatorComments", aoExe);
		}
		return loModelAndView;
	}

	/**
	 * This method fetches Name List <li>Execute transaction Id
	 * <b>fetchTypeAheadNameList</b></li>
	 * 
	 * @param aoRequest Request
	 * @param aoResponse Response
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	@ResourceMapping("fetchTypeAheadNameList")
	protected void fetchTypeAheadNameList(ResourceRequest aoRequest, ResourceResponse aoResponse)
			throws ApplicationException
	{
		PrintWriter loOut = null;
		try
		{
			Channel loChannel = new Channel();
			String lsInputParam = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.QUERY);
			loChannel.setData(HHSConstants.PROCUREMENT_TITLE,
					HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_TITLE));
			loChannel.setData(HHSConstants.INPUT_PARAM_MAP, lsInputParam);
			loChannel.setData(HHSConstants.QUERY_ID, HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.QUERY_ID));
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_TYPEAHEAD_NAME_LIST);
			aoResponse.setContentType(HHSConstants.APPLICATION_JSON);
			loOut = aoResponse.getWriter();
			loOut.print(HHSUtil.listMapToJSON((List<Map<String, String>>) loChannel.getData(HHSConstants.NAME_LIST),
					lsInputParam, HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.KEY),
					HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.FEILD_VALUE), 3));
		}
		// Catch the exception thrown by transaction and set the error message
		// in request object and pass to jsp
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception Occurred while displaying favorite list : " + loExp);
			setExceptionMessageInResponse(aoRequest);
		}
		finally
		{
			BaseControllerUtil.closingPrintWriter(loOut);
		}
	}

	/**
	 * <ul>
	 * <li>R5: Enhanced Evaluation</li>
	 * <li>This method is added as part of Release:5, ReviewScore Screen's popUp
	 * to view rounds in dropdown and score details for the latest round</li>
	 * </ul>
	 * <ul>
	 * <li>1. Add procurementId and evaluationStatusId input parameters to a Map
	 * </li>
	 * <li>2. Call the transaction: fetchEvaluatorScoreDetails to fetch the
	 * score details which includes criteria and comments too for requested
	 * evaluator based on evaluationStatusId.</li>
	 * </ul>
	 * 
	 * @param aoResourceRequest -- Portlet Resource Request Object -- to get
	 *            screen parameters and next action to be performed
	 * @param aoResourceResponse --Portlet Resource Reponse Object -- decides
	 *            the next execution flow
	 * @throws ApplicationException If an Application Exception occurs
	 * @throws Exception If an Exception occurs
	 */
	@SuppressWarnings(
	{ "unchecked" })
	@ResourceMapping("viewEvaluatorCommentsForReviewScore")
	protected ModelAndView viewEvaluatorCommentsForReviewScore(ResourceRequest aoResourceRequest,
			ResourceResponse aoResourceResponse)
	{
		Channel loChannel = new Channel();
		String lsJspName = HHSR5Constants.SCORE_DETAILS_ROUND_PATH;
		ModelAndView loModelAndView = new ModelAndView(lsJspName);
		aoResourceResponse.setContentType(HHSConstants.TEXT_HTML);
		Map<String, Object> loQueryMap = new HashMap<String, Object>();
		try
		{
			String lsEvaluatorName = aoResourceRequest.getParameter(HHSConstants.EVALUATOR_NAME);
			String lsEvaluationStatusId = aoResourceRequest.getParameter(HHSConstants.EVALUATION_STATUS_ID);
			aoResourceRequest.setAttribute(HHSConstants.EVALUATOR_NAME, lsEvaluatorName);
			aoResourceRequest.setAttribute(HHSConstants.EVALUATION_STATUS_ID, lsEvaluationStatusId);
			loQueryMap.put(HHSConstants.EVALUATION_STATUS_ID, lsEvaluationStatusId);
			loQueryMap.put(HHSConstants.PROCUREMENT_ID, aoResourceRequest.getParameter(HHSConstants.PROCUREMENT_ID));
			loQueryMap.put(HHSConstants.PROPOSAL_ID, aoResourceRequest.getParameter(HHSConstants.PROPOSAL_ID));
			loChannel.setData(HHSConstants.AO_QUERY_MAP, loQueryMap);
			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.FETCH_EVALUATOR_SCORE_DETAILS,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			aoResourceRequest.setAttribute(HHSConstants.EVALUATION_SCORE_DETAILS_LIST,
					(List<EvaluationBean>) loChannel.getData(HHSConstants.SCORE_LIST));
			List<EvaluationBean> loRound = (List<EvaluationBean>) loChannel.getData(HHSR5Constants.LO_ROUND_LIST);
			aoResourceRequest.setAttribute(HHSR5Constants.ITERATION, loRound);
			if (loRound.isEmpty())
			{
				aoResourceRequest.setAttribute(HHSR5Constants.LAST_ROUND, HHSR5Constants.ONE);
			}
			else
			{
				aoResourceRequest.setAttribute(HHSR5Constants.LAST_ROUND, loRound.get(HHSR5Constants.INT_ZERO)
						.getVersionNumber());
			}
		}
		// handling ApplicationException thrown from the transaction layer.
		catch (ApplicationException aoExe)
		{
			// populating context data map for exceptional handling
			aoExe.setContextData(loQueryMap);
			LOG_OBJECT.Error("ApplicationException occured in viewEvaluatorCommentsForReviewScore", aoExe);
			setGenericErrorMessage(aoResourceRequest);
		}
		// handling exception other than Application Exception.
		catch (Exception aoExe)
		{
			LOG_OBJECT.Error("Exception occured in viewEvaluatorCommentsForReviewScore", aoExe);
			setGenericErrorMessage(aoResourceRequest);
		}
		return loModelAndView;
	}

	/**
	 * The Method is used for displaying Evaluation Status Screen.The Evaluation
	 * Status screen lists all proposals that have been submitted by providers
	 * in response to an RFP
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1.Get the ProcurementId from Request</li>
	 * <li>2.Check if Evaluation Bean is present in Session i.e in case of
	 * sorting</li>
	 * <li>3.Set UserRole ,ProcurementId in EvaluationBean ,will be required in
	 * service call</li>
	 * <li>4.Call method "setEvaluationStatusResultInRequest" to execute the
	 * transaction fetchEvaluationStatus</li>
	 * <li>5.Catch exception if any and set the error message in the request.</li>
	 * </ul>
	 * @param aoRequest - a RenderRequest object
	 * @param aoResponse - a RenderResponse object
	 * @return jsp screen representation of proposalsEvaluationStatus screen
	 * 
	 */
	@RenderMapping(params = "render_action=getEvaluationStatus")
	protected String getEvaluationStatus(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		try
		{
			setRequestForEvalStatus(aoRequest);
			String lsProcurementId = aoRequest.getParameter(HHSConstants.PROCUREMENT_ID);
			/* Start: R5 Added */
			if (StringUtils.isBlank(lsProcurementId))
			{
				lsProcurementId = PortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
			}
			/* End: R5 Added */
			String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			String lsEvaluationGroupId = PortalUtil.parseQueryString(aoRequest, HHSConstants.EVALUATION_GROUP_ID);
			String lsEvaluationPoolMappingId = PortalUtil.parseQueryString(aoRequest,
					HHSConstants.EVALUATION_POOL_MAPPING_ID);
			String lsCompPoolId = PortalUtil.parseQueryString(aoRequest, HHSConstants.COMPETITION_POOL_ID);
			// Getting EvaluationBean from Session in case of Sorting
			EvaluationBean loEvaluationBean = (EvaluationBean) aoRequest.getPortletSession().getAttribute(
					HHSConstants.EVALUATION_SESSION_BEAN, PortletSession.PORTLET_SCOPE);
			if (null == loEvaluationBean)
			{
				loEvaluationBean = new EvaluationBean();
			}
			PortletSession loSession = aoRequest.getPortletSession();
			// Fetching userrole from session
			String lsUserRole = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ROLE,
					PortletSession.APPLICATION_SCOPE);
			loEvaluationBean.setUserRole(lsUserRole);
			loEvaluationBean.setProcurementId(lsProcurementId);
			loEvaluationBean.setPaginationEnable(HHSConstants.YES_UPPERCASE);
			getPageHeader(aoRequest, lsProcurementId);
			// get next page param for pagination
			String lsNextPage = aoRequest.getParameter(HHSConstants.NEXT_PAGE_PARAM);
			getPagingParams(loSession, loEvaluationBean, lsNextPage, HHSConstants.EVALUATION_LIST);
			Channel loChannel = new Channel();
			if (loEvaluationBean.getProposalTitle() != null
					&& loEvaluationBean.getProposalTitle().equals(HHSConstants.EMPTY_STRING))
			{
				loEvaluationBean.setProposalTitle(null);
			}
			if (loEvaluationBean.getOrganizationName() != null
					&& loEvaluationBean.getOrganizationName().equals(HHSConstants.EMPTY_STRING))
			{
				loEvaluationBean.setOrganizationName(null);
			}
			loEvaluationBean.setEvaluationPoolMappingId(lsEvaluationPoolMappingId);
			loChannel.setData(HHSConstants.EVALUATION_BEAN, loEvaluationBean);
			loChannel.setData(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
			loChannel.setData(HHSConstants.ORG_TYPE, lsUserOrgType);
			loChannel.setData(HHSConstants.USER_ROLE, lsUserRole);
			loChannel.setData(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvaluationPoolMappingId);
			loChannel.setData(HHSConstants.EVALUATION_GROUP_ID, lsEvaluationGroupId);
			// Change log 523
			HashMap<String, String> loProcMap = new HashMap<String, String>();
			loProcMap.put(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);
			loProcMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvaluationPoolMappingId);
			loProcMap.put(HHSConstants.EVALUATION_GROUP_ID, lsEvaluationGroupId);
			loProcMap.put(HHSConstants.COMPETITION_POOL_ID, lsCompPoolId);
			loChannel.setData(HHSConstants.PROC_MAP, loProcMap);
			setEvaluationStatusResultInRequest(aoRequest, loEvaluationBean, loSession, loChannel);
			aoRequest.getPortletSession().removeAttribute(HHSConstants.EVALUATION_TASK_SENT);
			aoRequest.getPortletSession().removeAttribute(HHSConstants.EVALUATION_SESSION_BEAN);
			aoRequest.getPortletSession().removeAttribute(HHSConstants.PROPOSAL_FILTERED);
			aoRequest.setAttribute(HHSConstants.CHANNEL_ACCESS, loChannel);
		}
		// handling ApplicationException that can be occured while
		// fetching evaluation status data for evaluation status screen
		catch (ApplicationException aoExp)
		{
			setGenericErrorMessage(aoRequest);
		}
		// handling Exception other than ApplicationException
		catch (Exception aoExp)
		{
			setGenericErrorMessage(aoRequest);
			LOG_OBJECT.Error("Exception Occured while rendering Evalution Status", aoExp);
		}
		return HHSConstants.PROPOSAL_EVALUATION_STATUS;
	}

	/**
	 * This method sets request for Evaluation Status
	 * @param aoRequest RenderRequest
	 */
	private void setRequestForEvalStatus(RenderRequest aoRequest)
	{
		if (ApplicationSession.getAttribute(aoRequest, true, ApplicationConstants.ERROR_MESSAGE_TYPE) != null)
		{
			if (((String) ApplicationSession.getAttribute(aoRequest, true, ApplicationConstants.ERROR_MESSAGE_TYPE))
					.equalsIgnoreCase(ApplicationConstants.MESSAGE_FAIL_TYPE))
			{
				if (ApplicationSession.getAttribute(aoRequest, true, HHSConstants.CANCEL_EVALUATION_TASK) == null
						|| !((String) ApplicationSession.getAttribute(aoRequest, true,
								HHSConstants.CANCEL_EVALUATION_TASK)).equalsIgnoreCase(HHSConstants.YES))
				{
					aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
							ApplicationSession.getAttribute(aoRequest, ApplicationConstants.ERROR_MESSAGE));
					aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
							ApplicationSession.getAttribute(aoRequest, ApplicationConstants.ERROR_MESSAGE_TYPE));
				}
			}
			else if (((String) ApplicationSession
					.getAttribute(aoRequest, true, ApplicationConstants.ERROR_MESSAGE_TYPE))
					.equalsIgnoreCase(ApplicationConstants.MESSAGE_PASS_TYPE))
			{
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
						ApplicationSession.getAttribute(aoRequest, ApplicationConstants.ERROR_MESSAGE));
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationSession.getAttribute(aoRequest, ApplicationConstants.ERROR_MESSAGE_TYPE));
			}
		}
		else if (PortalUtil.parseQueryString(aoRequest, HHSConstants.CLC_ERROR_MSG) != null
				&& PortalUtil.parseQueryString(aoRequest, HHSConstants.CLC_ERROR_MSG).equalsIgnoreCase(
						HHSConstants.YES_LOWERCASE))
		{
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
	}

	/**
	 * The Method is used for displaying Evaluation Status Screen.The Evaluation
	 * Status screen lists all proposals that have been submitted by providers
	 * in response to an RFP
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1.Execute the transaction <b>fetchEvaluationStatus</b></li>
	 * <li>2.Set the Filter Variable in the Request</li>
	 * <li>3.Set the Button Visibility Flag in Request</li>
	 * <li>4.Set the Total Evaluation Data in Request</li>
	 * <li>5.If we are coming from Cancel Evaluation Task.Set the Sucess Message
	 * </li>
	 * </ul>
	 * @param aoRequest - RenderRequest
	 * @param aoEvaluationBean - EvaluationBean
	 * @param aoSession - PortletSession
	 * @param aoChannel - Channel
	 * @throws ApplicationException - throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	private void setEvaluationStatusResultInRequest(RenderRequest aoRequest, EvaluationBean aoEvaluationBean,
			PortletSession aoSession, Channel aoChannel) throws ApplicationException
	{
		try
		{
			HHSTransactionManager.executeTransaction(aoChannel, HHSConstants.FETCH_EVALUATION_STATUS);
			Boolean lbTaskSent = (Boolean) aoSession.getAttribute(HHSConstants.EVALUATION_TASK_SENT);
			Integer loPropNotNonResCnt = (Integer) aoChannel.getData(HHSConstants.NOT_NOT_RESPONSIVE_COUNT);
			String lsEvaluationSent = (String) aoChannel.getData(HHSConstants.EVALUATION_SENT);
			aoRequest.setAttribute(HHSConstants.COMPETITION_POOL_LIST,
					aoChannel.getData(HHSConstants.COMPETITION_POOL_LIST));
			aoRequest.setAttribute(HHSConstants.EVALUATION_GROUP_ID,
					aoChannel.getData(HHSConstants.EVALUATION_GROUP_ID));
			aoRequest.setAttribute(HHSConstants.GROUP_TITLE_MAP, aoChannel.getData(HHSConstants.GROUP_TITLE_MAP));
			Integer loProposalCount = (Integer) aoChannel.getData(HHSConstants.PROPOSAL_COUNT);
			aoSession.setAttribute(ApplicationConstants.ALERT_VIEW_PAGING_RECORDS, loProposalCount,
					PortletSession.APPLICATION_SCOPE);
			// Setting Variable Filtered when coming from Filter Screen
			aoRequest.setAttribute(HHSConstants.FILTERED, aoRequest.getParameter(HHSConstants.FILTERED));
			aoRequest.setAttribute(
					HHSConstants.PROPOSAL_FILTERED,
					aoRequest.getPortletSession().getAttribute(HHSConstants.PROPOSAL_FILTERED,
							PortletSession.PORTLET_SCOPE));
			List<EvaluationBean> loEvaluationDetailList = (List<EvaluationBean>) aoChannel
					.getData(HHSConstants.EVALUATION_BEAN_LIST);
			String lsProcurementStatus = (String) aoChannel.getData(HHSConstants.PROCUREMENT_STATUS);
			// Setting procurement status in Evaluation Bean List
			for (EvaluationBean loEvaluationBean : loEvaluationDetailList)
			{
				loEvaluationBean.setProcurementStatus(lsProcurementStatus);
				// Change Log 523
				loEvaluationBean.setProposalCount(loPropNotNonResCnt);
				loEvaluationBean.setEvaluationSent(lsEvaluationSent);
			}
			// Setting Evaluation Detail List ,will populate date in UI
			aoRequest.setAttribute(HHSConstants.EVALUATION_DETAILS_LIST, loEvaluationDetailList);
			// Setting Button Visibility Flag i.e Send Evaluation,Download
			// DBD,Cancel Evaluation,Close Submission
			// procurement status check
			if (lsProcurementStatus.equals(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_PROCUREMENT_CLOSED))
					|| lsProcurementStatus.equals(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
							HHSConstants.STATUS_PROCUREMENT_CANCELLED)))
			{
				aoRequest.setAttribute(HHSConstants.IS_PROC_CLOSED_CANCELLED, true);
			}
			else
			{
				aoRequest.setAttribute(HHSConstants.IS_PROC_CLOSED_CANCELLED, false);
			}
			Map<String, Boolean> loSendEvalTaskButtonFlagMap = (Map<String, Boolean>) aoChannel
					.getData(HHSConstants.SEND_EVALUATION_VISIBILTY_FLAG);
			aoRequest.setAttribute(HHSConstants.ENABLE_SEND_EVAL_TASK_BUTTON,
					loSendEvalTaskButtonFlagMap.get(HHSConstants.ENABLE_SEND_EVAL_TASK_BUTTON));
			aoRequest.setAttribute(HHSConstants.SHOW_SEND_EVAL_TASK_BUTTON,
					loSendEvalTaskButtonFlagMap.get(HHSConstants.SHOW_SEND_EVAL_TASK_BUTTON));
			aoRequest.setAttribute(HHSConstants.DOWNLOAD_DBD_VISIBILTY_FLAG_KEY,
					aoChannel.getData(HHSConstants.DOWNLOAD_DBD_VISIBILTY_FLAG));
			aoRequest.setAttribute(HHSConstants.CANCEL_EV_TASK_VISIBILTY_FLAG_KEY,
					aoChannel.getData(HHSConstants.CANCEL_EV_TASK_VISIBILTY_FLAG));
			if (null != aoChannel.getData(HHSConstants.TOTAL_EVALUATION_DATA))
			{
				aoRequest.setAttribute(HHSConstants.TOTAL_EVALUATION_DATA,
						aoChannel.getData(HHSConstants.TOTAL_EVALUATION_DATA));
			}
			aoRequest.setAttribute(HHSConstants.NUMBER_OF_PROPOSAL, aoChannel.getData(HHSConstants.PROPOSAL_COUNT));
			aoRequest.getPortletSession().setAttribute(HHSConstants.SORT_TYPE, aoEvaluationBean.getFirstSortType(),
					PortletSession.APPLICATION_SCOPE);
			aoRequest.getPortletSession().setAttribute(HHSConstants.SORT_BY, aoEvaluationBean.getSortColumnName(),
					PortletSession.APPLICATION_SCOPE);
			setErrorMessageForEvaluationStatus(aoRequest, lbTaskSent, loPropNotNonResCnt);
		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Error was occurred while setting the evaluation request", aoAppExp);
			throw aoAppExp;
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error was occurred while setting the evaluation request", aoExp);
			throw new ApplicationException("Error was occurred while setting the evaluation request", aoExp);
		}
	}

	/**
	 * This method is used to set pass and fail messages for send evaluation
	 * task for evaluation status screen
	 * 
	 * @param aoRequest RenderRequest object
	 * @param abTaskSent boolean value of task sent flag
	 * @param aoPropNotNonResCnt integer value of non responsive proposal count
	 * @throws ApplicationException If an Application Exception occurs
	 */
	private void setErrorMessageForEvaluationStatus(RenderRequest aoRequest, Boolean abTaskSent,
			Integer aoPropNotNonResCnt) throws ApplicationException
	{
		if (aoRequest.getParameter(HHSConstants.PARAM_VALUE) != null
				&& aoRequest.getParameter(HHSConstants.PARAM_VALUE).equalsIgnoreCase(HHSConstants.SEND_EVALUATION_TASK))
		{
			// Change Log 523
			if (aoPropNotNonResCnt != null && aoPropNotNonResCnt == 0 && !abTaskSent)
			{
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.ALL_NON_RESPONSIVE_PROPOSALS_MESSAGE));
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
			}
			else
			{
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.SEND_EVALUATION_SUCCESSFUL));
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_PASS_TYPE);
			}
		}
	}

}