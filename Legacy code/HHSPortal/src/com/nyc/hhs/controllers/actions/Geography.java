package com.nyc.hhs.controllers.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.portlet.ActionRequest;
import javax.portlet.RenderRequest;

import org.jdom.Document;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.model.TaxonomyTree;
import com.nyc.hhs.util.ApplicationSession;
import com.nyc.hhs.util.XMLUtil;

/**
 * This class sets the required values in the Channel object, required to
 * execute the transaction to display all geography and to save the selected
 * geography. Also it sets the values, required in the in jsp, in the request
 * object.
 * 
 */

public class Geography extends BusinessApplication
{
	/**
	 * Gets the channel object for action
	 * 
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

		String[] loSelectedGeography = aoRequest.getParameterValues("geography");
		String loBottomCheckBox = aoRequest.getParameter("bottomCheckBox");
		ArrayList<String> loALSelectedGography = new ArrayList<String>();
		// code of block to be executed if loSelectedGeography is not null
		if (loSelectedGeography != null)
		{
			loALSelectedGography = new ArrayList<String>(Arrays.asList(loSelectedGeography));
			aoRequest.getPortletSession().setAttribute("geographySelectedByUser", loALSelectedGography);
		}
		Channel loChanneL = new Channel();
		loChanneL.setData(ApplicationConstants.ORG_ID, asOrgId);
		loChanneL.setData("asElementType", ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_GEOGRAPHY);
		loChanneL.setData("aoElementIdList", loALSelectedGography);
		loChanneL.setData("asBottomCheckBox", loBottomCheckBox);
		// save_transaction
		return loChanneL;
	}

	/**
	 * Gets the channel object for render
	 * 
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
		// code of block to be executed if loDoc is not null
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
	 * 
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
		String lsMenu = getSubMenu(asSectionName);
		loMapForRender.put(ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_MENU, lsMenu);
		loMapForRender.put(ApplicationConstants.FILE_TO_INCLUDE, "/WEB-INF/jsp/businessapplication/geography.jsp");
		List loGeographySelectedByUser = null;
		// Checks if the action is open then execute this block
		if (null == asAction || asAction.equals(ApplicationConstants.BUSINESS_APPLICATION_ACTION_OPEN))
		{
			if (null != aoRequest)
			{
				loGeographySelectedByUser = (List) aoChannel.getData("loTaxonomyIdList");
			}
			Map<String, List<TaxonomyTree>> loHMTaxonomyMap = (TreeMap<String, List<TaxonomyTree>>) aoChannel
					.getData("lohTaxonomyMap");
			if (null != aoRequest)
			{
				aoRequest.getPortletSession().setAttribute(
						ApplicationConstants.BUSINESS_APPLICATION_SESSION_PARAMETER_TAXONOMY, loHMTaxonomyMap);
			}
			loMapForRender.put("TaxonomyTypeMap", getFinalMap(loHMTaxonomyMap, loGeographySelectedByUser));
			loMapForRender.put("loTaxonomyIdList", loGeographySelectedByUser);
			loMapForRender.put("loReadOnly", false); // need to pass variable on
														// basis of application
														// status to make page
														// readonly
			// Checks if the action is save then execute this block
		}
		else if (asAction.equals(ApplicationConstants.BUSINESS_APPLICATION_ACTION_SAVE))
		{
			loGeographySelectedByUser = (List<String>) ApplicationSession
					.getAttribute(aoRequest, "returnGeographyList");
			Map<String, List<TaxonomyTree>> loHMTaxonomyMap = (TreeMap<String, List<TaxonomyTree>>) aoRequest
					.getPortletSession().getAttribute(
							ApplicationConstants.BUSINESS_APPLICATION_SESSION_PARAMETER_TAXONOMY);
			loMapForRender.put("loReadOnly", false);
			loMapForRender.put("TaxonomyTypeMap", getFinalMap(loHMTaxonomyMap, loGeographySelectedByUser));
			loMapForRender.put("loTaxonomyIdList", loGeographySelectedByUser);
		}
		return loMapForRender;
	}

	/**
	 * This method get the final map for geography
	 * @param aoGeographyMap Map of list of taxonomy tree as value
	 * @param aoGeographySelectedByUser id of geography selected by user
	 * @return aoGeographyMap final geography map
	 */
	private Map<String, List<TaxonomyTree>> getFinalMap(Map<String, List<TaxonomyTree>> aoGeographyMap,
			List<String> aoGeographySelectedByUser)
	{
		if (aoGeographyMap != null && !aoGeographyMap.isEmpty())
		{
			for (Map.Entry<String, List<TaxonomyTree>> loEntry : aoGeographyMap.entrySet())
			{
				List<TaxonomyTree> loHeaderValue = loEntry.getValue();
				for (TaxonomyTree loTaxonomyTree : loHeaderValue)
				{
					if (aoGeographySelectedByUser != null && !aoGeographySelectedByUser.isEmpty())
					{
						if (aoGeographySelectedByUser.contains(loTaxonomyTree.getMsElementid()))
						{
							loTaxonomyTree.setMsServiceStatus("selected");
						}
						else
						{
							loTaxonomyTree.setMsServiceStatus("");
						}
					}
				}
			}
		}
		return aoGeographyMap;
	}

	/**
	 * This method fetches the map to be rendered
	 * 
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
