package com.nyc.hhs.controllers.actions;

import java.util.Iterator;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.RenderRequest;

import org.jdom.Document;
import org.jdom.Element;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.util.XMLUtil;

/**
 * This class is extended by each business application's sections.
 *
 */

public abstract class  BusinessApplication {

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
	public abstract Channel getChannelObject(String asSectionName, String asOrgId, String asAppId, String asAppStatus, String asAppDataForUpdate, String asAction,String asUserRole, ActionRequest aoRequest, String asTaxonomyName) throws ApplicationException; 
	
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
	public abstract Channel getChannelObject(String asSectionName, String asOrgId, String asAppId, String asAppStatus, String asAppDataForUpdate, String asAction,String asUserRole, RenderRequest aoRequest, String asTaxonomyName) throws ApplicationException;
	
	/**
	 * This method gets the map to be rendered.
	 * 
	 * @param asAction - the action to be performed
	 * @param asSectionName - current section name
	 * @param aoChannel - channel object with data, will be needed to be passed to MapForRender
	 * @param aoRequest - Render Request
	 * @return the map to be rendered on front end
	 * @throws ApplicationException
	 */
	public abstract Map<String,Object> getMapForRender(String asAction,String asSectionName, Channel aoChannel, RenderRequest aoRequest) throws ApplicationException;
	
	/**
	 * This method gets the map to be rendered.
	 *  
	 * @param asAction - the action to be performed
	 * @param asSectionName - current section name
	 * @param aoChannel - channel object with data, will be needed to be passed to MapForRender
	 * @param aoRequest - Action Request
	 * @return the map to be rendered on front end
	 * @throws ApplicationException
	 */
	public abstract Map<String,Object> getMapForRender(String asAction,String asSectionName, Channel aoChannel, ActionRequest aoRequest) throws ApplicationException;
	
	/**
	 * This method fetches the submenu for the current section name
	 * 
	 * @param asSectionName - current section name
	 * @return the submenu for the current section name
	 * @throws ApplicationException
	 */
	public static String getSubMenu(String asSectionName) throws ApplicationException{
		StringBuffer loSbMenu = new StringBuffer();
		Document loNavigationDom = XMLUtil.getDomObj(BusinessApplication.class.getResourceAsStream(ApplicationConstants.NAVIGATION_FILE_PATH));
		String lsXPath = "//menu[(@name=\"" + asSectionName + "\")]";
		Element loMainMenu = XMLUtil.getElement(lsXPath, loNavigationDom);
		//block of code to be executed if loMainMenu is not null and having at least one child
		if(null != loMainMenu && loMainMenu.getChildren().size()>0){
			Iterator<Element> loItrElements = loMainMenu.getChildren().iterator();
			//Iterating all the elements
			while(loItrElements.hasNext()){
				Element loMenu = loItrElements.next();
				String lsMenuName = loMenu.getAttributeValue("name");
				String lsMenuDisplayName = loMenu.getAttributeValue("displayname");
				loSbMenu.append("<li><a id='subsection_"+lsMenuName+"'");
				loSbMenu.append(" href='#' onclick=\"javascript: submitForm(this.id);\">");
				loSbMenu.append(lsMenuDisplayName).append("</a></li>");
			}
		}
		return loSbMenu.toString();
	}
}
