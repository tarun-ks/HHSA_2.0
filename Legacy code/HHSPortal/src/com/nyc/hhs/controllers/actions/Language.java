package com.nyc.hhs.controllers.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.RenderRequest;

import org.jdom.Document;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.model.TaxonomyTree;
import com.nyc.hhs.util.XMLUtil;

/**
 * This class sets the required values in the Channel object, required to
 * execute the transaction to display all geography and to save the selected
 * language. Also it sets the values, required in the in jsp, in the request
 * object.
 * 
 */

public class Language extends BusinessApplication
{

	/**
	 * Gets the channel object for action
	 * @param asSectionName - current section name
	 * @param asOrgId - the organization id of the current organization
	 * @param asAppId - Business application id of the application
	 * @param asAppStatus - the current application status
	 * @param asAppDataForUpdate - data to be updated in application
	 * @param asAction - the action to be performed
	 * @param asUserRole - current user role
	 * @param aoRequest - Action request
	 * @param asTaxonomyName - taxonomy name to be used in factory
	 * @return the channel object to be used for further processing
	 * @throws ApplicationException
	 */
	@Override
	public Channel getChannelObject(String asSectionName, String asOrgId, String asAppId, String asAppStatus,
			String asAppDataForUpdate, String asAction, String asUserRole, ActionRequest aoRequest,
			String asTaxonomyName) throws ApplicationException
	{

		String lsOtherChecked = (String) aoRequest.getParameter("other_checked");
		String[] loSelectedLanguages = aoRequest.getParameterValues("language_checkbox");
		String[] loLanguagesInterpretation = aoRequest.getParameterValues("language_interpretation_services");
		String loLanguagesInterpretationFlag = "";
		if (loLanguagesInterpretation != null && loLanguagesInterpretation.length > 0)
		{
			loLanguagesInterpretationFlag = "true";
		}
		aoRequest.getPortletSession().setAttribute("languageOtherSelectedByUser", false);
		aoRequest.getPortletSession().setAttribute("languageCheckedSelectedByUser", true);
		ArrayList<String> loAllSelectedLanguages = new ArrayList<String>();
		ArrayList<String> loAllOtherSelectedLanguages = new ArrayList<String>();
		ArrayList<String> loAllCheckedSelectedLanguages = new ArrayList<String>();
		// Below section executes when at least one language is checked in the
		// check boxes.
		if (loSelectedLanguages != null && loSelectedLanguages.length > 0)
		{
			loAllSelectedLanguages.addAll(Arrays.asList(loSelectedLanguages));
			loAllCheckedSelectedLanguages.addAll(Arrays.asList(loSelectedLanguages));
			aoRequest.getPortletSession().setAttribute("languageCheckedSelectedByUser", false);
		}
		// Below section executes when at least one language is selected in the
		// select boxes when other is checked.
		if (lsOtherChecked.equalsIgnoreCase("other_checked"))
		{
			String[] loOtherSelectedLanguages = aoRequest.getParameterValues("language_listbox");
			if (loOtherSelectedLanguages != null)
			{
				loAllSelectedLanguages.addAll(Arrays.asList(loOtherSelectedLanguages));
				loAllOtherSelectedLanguages.addAll(Arrays.asList(loOtherSelectedLanguages));
			}
			else
			{
				aoRequest.getPortletSession().setAttribute("languageOtherSelectedByUser", true);
			}
		}
		Channel loChannel = new Channel();
		loChannel.setData(ApplicationConstants.ORG_ID, asOrgId);
		loChannel.setData("asElementType", ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_LANGUAGES);
		loChannel.setData("aoElementIdList", loAllSelectedLanguages);
		loChannel.setData("aoElementIdOtherList", loAllOtherSelectedLanguages);
		loChannel.setData("aoElementIdCheckedList", loAllCheckedSelectedLanguages);
		loChannel.setData("aoLanguagesInterpretationFlag", loLanguagesInterpretationFlag);
		aoRequest.getPortletSession().setAttribute("asOtherChecked", lsOtherChecked);
		aoRequest.getPortletSession().setAttribute("languageSelectedByUser", loAllSelectedLanguages);
		return loChannel;
	}

	/**
	 * This method fetches the map to be rendered Gets the channel object for
	 * render
	 * @param asSectionName - current section name
	 * @param asOrgId - the organization id of the current organization
	 * @param asAppId - Business application id of the application
	 * @param asAppStatus - the current application status
	 * @param asAppDataForUpdate - data to be updated in application
	 * @param asAction - the action to be performed
	 * @param asUserRole - current user role
	 * @param aoRequest - Render request
	 * @param asTaxonomyName - taxonomy name to be used in factory
	 * @return the channel object to be used for further processing
	 * @throws ApplicationException
	 */
	@Override
	public Channel getChannelObject(String asSectionName, String asOrgId, String asAppId, String asAppStatus,
			String asAppDataForUpdate, String asAction, String asUserRole, RenderRequest aoRequest,
			String asTaxonomyName) throws ApplicationException
	{
		Document loDoc = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.TAXONOMY_ELEMENT);
		String lsFromCache = ApplicationConstants.FALSE;
		if (loDoc != null)
		{
			String lsDocumentString = XMLUtil.getXMLAsString(loDoc);
			lsFromCache = (lsDocumentString.length() > 0) ? ApplicationConstants.TRUE : ApplicationConstants.FALSE;
		}

		Channel loChannel = new Channel();
		loChannel.setData(ApplicationConstants.TAXONOMY_TYPE, asTaxonomyName);
		loChannel.setData(ApplicationConstants.FROM_CACHE, lsFromCache);
		loChannel.setData(ApplicationConstants.ORGANIZATION_ID, asOrgId);

		return loChannel;
	}

	/**
	 * This method fetches the map to be rendered
	 * @param asAction - the action to be performed
	 * @param asSectionName - current section name
	 * @param aoChannel - channel object with data, will be needed to be passed
	 *            to MapForRender
	 * @param aoRequest - Render Request
	 * @return the map to be rendered on front end
	 * @throws ApplicationException
	 */
	@Override
	public Map<String, Object> getMapForRender(String asAction, String asSectionName, Channel aoChannel,
			RenderRequest aoRequest) throws ApplicationException
	{

		Map<String, Object> loMapForRender = new HashMap<String, Object>();
		loMapForRender.put(ApplicationConstants.FILE_TO_INCLUDE, "/WEB-INF/jsp/businessapplication/language.jsp");
		String lsMenu = getSubMenu(asSectionName);
		loMapForRender.put(ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_MENU, lsMenu);
		if (null == asAction || asAction.equals(ApplicationConstants.BUSINESS_APPLICATION_ACTION_OPEN))
		{
			ArrayList loLanguageIdSelectList = (ArrayList) aoChannel.getData("moLanguageIdSelectList");
			ArrayList loLanguageinterpretationList = (ArrayList) aoChannel.getData("loLanguageinterpretationList");
			List<TaxonomyTree> loTaxonomyTree = (ArrayList<TaxonomyTree>) aoChannel.getData("loTaxonomyTreeList");
			if (loTaxonomyTree != null && !loTaxonomyTree.isEmpty())
			{
				Collections.sort(loTaxonomyTree, new Comparator<TaxonomyTree>()
				{
					public int compare(TaxonomyTree aoObject1, TaxonomyTree aoObject2)
					{
						return aoObject1.getMsElementName().toLowerCase()
								.compareTo(aoObject2.getMsElementName().toLowerCase());
					}
				});
			}
			loMapForRender.put("TaxonomyList", loTaxonomyTree);
			loMapForRender.put("moLanguageIdList", loLanguageIdSelectList);
			loMapForRender.put("aoLanguageinterpretationList", loLanguageinterpretationList);
			loMapForRender.put("errorToDisplay", null);
			if (aoRequest != null)
			{
				aoRequest.getPortletSession().setAttribute(
						ApplicationConstants.BUSINESS_APPLICATION_SESSION_PARAMETER_TAXONOMY, loTaxonomyTree);
			}
		}
		// Below section executes when we save languages.
		else if (asAction.equals(ApplicationConstants.BUSINESS_APPLICATION_ACTION_SAVE))
		{
			ArrayList loTaxonomyTree = (ArrayList) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.BUSINESS_APPLICATION_SESSION_PARAMETER_TAXONOMY);
			ArrayList loLanguageSelectedByUser = (ArrayList) aoRequest.getPortletSession().getAttribute(
					"languageSelectedByUser");
			Boolean loLanguageOtherSelectedByUser = (Boolean) aoRequest.getPortletSession().getAttribute(
					"languageOtherSelectedByUser");
			Boolean loLanguageCheckedSelectedByUser = (Boolean) aoRequest.getPortletSession().getAttribute(
					"languageCheckedSelectedByUser");
			String lsOtherChecked = (String) aoRequest.getPortletSession().getAttribute("asOtherChecked");
			ArrayList lsErrorMsg = new ArrayList();
			// Below section executes when no language is checked in the check
			// boxes.
			if ((loLanguageSelectedByUser.size() <= 0 || loLanguageCheckedSelectedByUser)
					&& !lsOtherChecked.equalsIgnoreCase("other_checked"))
			{
				lsErrorMsg.add(aoRequest.getParameter("errorMsg"));
			}
			// Below section executes when no language is selected in the select
			// boxes when other is checked.
			if (loLanguageOtherSelectedByUser)
			{
				lsErrorMsg.add(aoRequest.getParameter("errorOtherMsg"));
			}
			String lsOtherSelected = aoRequest.getParameter("isOtherSelected");
			String lsLanguageInterpretation = aoRequest.getParameter("isLanguageInterpretation");
			loMapForRender.put("TaxonomyList", loTaxonomyTree);
			loMapForRender.put("moLanguageIdList", loLanguageSelectedByUser);
			loMapForRender.put("errorToDisplay", lsErrorMsg);
			loMapForRender.put("other_checked", lsOtherSelected);
			loMapForRender.put("language_interpretation_services", lsLanguageInterpretation);

		}
		return loMapForRender;
	}

	/**
	 * This method fetches the map to be rendered
	 * @param asAction - the action to be performed
	 * @param asSectionName - current section name
	 * @param aoChannel - channel object with data, will be needed to be passed
	 *            to MapForRender
	 * @param aoRequest - Action Request
	 * @return the map to be rendered on front end
	 * @throws ApplicationException
	 */
	@Override
	public Map<String, Object> getMapForRender(String asAction, String asSectionName, Channel aoChannel,
			ActionRequest aoRequest) throws ApplicationException
	{
		return null;
	}

}
