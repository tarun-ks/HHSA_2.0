package com.nyc.hhs.controllers.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;

import org.jdom.Document;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.model.Population;
import com.nyc.hhs.model.TaxonomyTree;
import com.nyc.hhs.util.ApplicationSession;
import com.nyc.hhs.util.XMLUtil;

/**
 * This class is used to select, insert and update Population options
 * It sets the required values in the channel object, required to 
 * execute the transaction. Also sets the values, required in the in 
 * jsp, in the request object. 
 * 
 */

public class PopulationTaxonomy extends BusinessApplication {
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
	public Channel getChannelObject(String asSectionName, String asOrgId,
			String asAppId, String asAppStatus, String asAppDataForUpdate,
			String asAction, String asUserRole, ActionRequest aoRequest,
			String asTaxonomyName) throws ApplicationException {

		Channel loChannelObj = new Channel();
		String lsNumPopulation =aoRequest.getParameter("noPopulation");
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
		List<Population> loPopulationList = new ArrayList<Population>();
		//code of block to be executed if lsnoPopulation is null
		if(lsNumPopulation==null){
			String[] lsSelectedCheckBoxes =aoRequest.getParameterValues("populationCheckBoxex");
			//code of block to be executed if lsSelectedCheckBoxes is not null and has at least one element
			if(lsSelectedCheckBoxes!=null && lsSelectedCheckBoxes.length>0){
				//iterating the array of all the selected check boxes 
				for (int liConunt = 0; liConunt < lsSelectedCheckBoxes.length; liConunt++) {
					Population loPopulation = new Population();
					String lsElementId = lsSelectedCheckBoxes[liConunt];
					loPopulation.setMsAgeFrom(aoRequest.getParameter("ageFromInput".concat(lsElementId)));
					loPopulation.setMsAgeTo(aoRequest.getParameter("ageToInput".concat(lsElementId)));
					loPopulation.setMsElementid(lsElementId);
					loPopulation.setMsUserId(lsUserId);
					if(lsElementId.equalsIgnoreCase("-1")){
						loPopulation.setMsOther(aoRequest.getParameter("otherTextBox"));
					}else{
						loPopulation.setMsOther("");
					}
					loPopulation.setMsOrganizationid(asOrgId);
					loPopulationList.add(loPopulation);
				}
			}
		}else{
			Population loPopulation = new Population();
			loPopulation.setMsElementid("-2");
			loPopulation.setMsOrganizationid(asOrgId);
			loPopulation.setMsUserId(lsUserId);
			loPopulationList.add(loPopulation);
		}
		loChannelObj.setData(ApplicationConstants.POPULATION_DATA_LIST, loPopulationList);
		loChannelObj.setData(ApplicationConstants.ORGANIZATION_ID, asOrgId);
		loChannelObj.setData(ApplicationConstants.TAXONOMY_TYPE, ApplicationConstants.POPULATION);
		loChannelObj.setData(ApplicationConstants.FROM_CACHE, "true");

		return loChannelObj;
	}

	/**
	 * Gets the channel object for render
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
	public Channel getChannelObject(String asSectionName, String asOrgId,
			String asAppId, String asAppStatus, String asAppDataForUpdate,
			String asAction, String asUserRole, RenderRequest aoRequest,
			String asTaxonomyName) throws ApplicationException {

		Document loDoc = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(ApplicationConstants.TAXONOMY_ELEMENT);
		String lsFromCache = "false";
		//code of block to be executed if loDoc is not null
		if(loDoc != null){
			String lsDocumentString= XMLUtil.getXMLAsString(loDoc);
			lsFromCache = (lsDocumentString.length()>0)?ApplicationConstants.TRUE:ApplicationConstants.FALSE;
		}
		Channel loChannel = new Channel();
		loChannel.setData(ApplicationConstants.TAXONOMY_TYPE, ApplicationConstants.POPULATION);
		loChannel.setData(ApplicationConstants.FROM_CACHE, lsFromCache);
		loChannel.setData(ApplicationConstants.ORG_ID, asOrgId);

		return loChannel;
	}

	/**This method fetches the map to be rendered 
	 * @param asAction - the action to be performed
	 * @param asSectionName - current section name
	 * @param aoChannel - channel object with data, will be needed to be passed to MapForRender
	 * @param aoRequest - Render Request
	 * @return the map to be rendered on front end
	 * @throws ApplicationException
	 */
	@Override
	public Map<String, Object> getMapForRender(String asAction,String asSectionName, Channel aoChannel, RenderRequest aoRequest) throws ApplicationException {

		Map<String,Object> loMapForRender = new HashMap<String, Object>();
		loMapForRender.put(ApplicationConstants.FILE_TO_INCLUDE, "/WEB-INF/jsp/businessapplication/population.jsp");
		if(aoChannel != null){
			List<Population> loPopulationList = (List<Population>) aoChannel.getData("loPopulation");
			List<TaxonomyTree> loTaxonomyTreeList = (ArrayList<TaxonomyTree>) aoChannel.getData("loTaxonomyTreeList");
			String lsMenu = getSubMenu(asSectionName);
			loMapForRender.put(ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_MENU, lsMenu);
			loMapForRender.put("population_list", loPopulationList);
			if(loTaxonomyTreeList!=null && !loTaxonomyTreeList.isEmpty()){
			Collections.sort(loTaxonomyTreeList,new Comparator<TaxonomyTree>() {
				  @Override
				public int compare(TaxonomyTree aoObject1, TaxonomyTree aoObject2) {
				   return aoObject1.getMsElementName().toLowerCase().compareTo(aoObject2.getMsElementName().toLowerCase());
				  }
				 });
			}
			loMapForRender.put("taxonomy_tree_list", loTaxonomyTreeList);
			if(loPopulationList!=null &&  !loPopulationList.isEmpty() && loPopulationList.get(0).getMsElementid().equalsIgnoreCase("-2")){
				loMapForRender.put("otherCheckBoxSelected",true);
			}
			
			if(loPopulationList!=null &&  !loPopulationList.isEmpty()){
				for (Population loPopulation : loPopulationList) {
					if(loPopulation.getMsElementid().equalsIgnoreCase("-1")){
						loMapForRender.put("otherCheckBoxAtLast",true);
						loMapForRender.put("otherCheckBoxAtLastValue",loPopulation.getMsOther());
					}
				}
			}
			
			loTaxonomyTreeList = getCompletePopulationList(loTaxonomyTreeList,loPopulationList);
			if(aoRequest!=null){
				aoRequest.getPortletSession().setAttribute("taxonomy_tree_list", loTaxonomyTreeList);
			}
		}else{
			List<TaxonomyTree> loTaxonomyTreeList =(List<TaxonomyTree>)ApplicationSession.getAttribute(aoRequest, "taxonomyListAfterSave"); 
			List<Population> loPopulationList = (List<Population>)ApplicationSession.getAttribute(aoRequest, "populationListAfterSave");

			loTaxonomyTreeList = getCompletePopulationList(loTaxonomyTreeList,loPopulationList);
			if(loTaxonomyTreeList!=null && !loTaxonomyTreeList.isEmpty()){
				Collections.sort(loTaxonomyTreeList,new Comparator<TaxonomyTree>() {
					  @Override
					public int compare(TaxonomyTree aoObject1, TaxonomyTree aoOject2) {
					   return aoObject1.getMsElementName().toLowerCase().compareTo(aoOject2.getMsElementName().toLowerCase());
					  }
					 });
				}
			loMapForRender.put("taxonomy_tree_list", loTaxonomyTreeList);
			
			if(loPopulationList!=null &&  !loPopulationList.isEmpty() && loPopulationList.get(0).getMsElementid().equalsIgnoreCase("-2")){
				loMapForRender.put("otherCheckBoxSelected",true);
			}

			if(loPopulationList!=null &&  !loPopulationList.isEmpty()){
				for (Population loPopulation : loPopulationList) {
					if(loPopulation.getMsElementid().equalsIgnoreCase("-1")){
						loMapForRender.put("otherCheckBoxAtLast",true);
						loMapForRender.put("otherCheckBoxAtLastValue",loPopulation.getMsOther());
					}
				}
			}
		}
		return loMapForRender;
	}
	
	/**This method get the population list
	 * @param aoTaxonomyTreeList List of Taxonomy
	 * @param aoPopulationList List of Population
	 * @return aoTaxonomyTreeList List of Taxonomy
	 */
	private List<TaxonomyTree> getCompletePopulationList(List<TaxonomyTree> aoTaxonomyTreeList,List<Population> aoPopulationList){
		if(aoTaxonomyTreeList!=null && !aoTaxonomyTreeList.isEmpty()){
			for (TaxonomyTree loTaxonomyTree : aoTaxonomyTreeList) {
				if(aoPopulationList!=null && !aoPopulationList.isEmpty()){
					for (Population loSelectedPopulation : aoPopulationList) {
						if(loTaxonomyTree.getMsElementid().equalsIgnoreCase(loSelectedPopulation.getMsElementid())){
							loTaxonomyTree.setSelectedPopulation("true");
							loTaxonomyTree.setAgeFrom(loSelectedPopulation.getMsAgeFrom());
							loTaxonomyTree.setAgeTo(loSelectedPopulation.getMsAgeTo());
							loTaxonomyTree.setOtherData(loSelectedPopulation.getMsOther());
							break;
						}
					}
				}
			}
		}
		return aoTaxonomyTreeList;
	}
	/**This method fetches the map to be rendered 
	 * @param asAction - the action to be performed
	 * @param asSectionName - current section name
	 * @param aoChannel - channel object with data, will be needed to be passed to MapForRender
	 * @param aoRequest - Action Request
	 * @return the map to be rendered on front end
	 * @throws ApplicationException
	 */
	@Override
	public Map<String, Object> getMapForRender(String asAction,
			String asSectionName, Channel aoChannel, ActionRequest aoRequest)
			throws ApplicationException {
		
		return null;
	}
}
