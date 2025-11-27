package com.nyc.hhs.controllers.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.RenderRequest;

import org.jdom.Document;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.model.TaxonomyServiceBean;
import com.nyc.hhs.util.ApplicationSession;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.PortalUtil;
import com.nyc.hhs.util.XMLUtil;

/**
 * This class sets the required values in the channel object, required to execute the 
 * transaction for the Adding services in the existing business application, to display
 * the existing services, or to save the services in the business application. Also it 
 * sets the values, required in the in jsp, in the request object.  
 * 
 */

public class AddService extends BusinessApplication {
	
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
	public Channel getChannelObject(String asSectionName, String asOrgId,
			String asAppId, String asAppStatus, String asAppDataForUpdate,
			String asAction, String asUserRole, ActionRequest aoRequest,
			String asTaxonomyName) throws ApplicationException {

		Channel loChannel = new Channel();
		//Checks if the action is showServices then execute this block
		if(asAction != null && asAction.equalsIgnoreCase("showServices")){

			Document loDoc = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(ApplicationConstants.TAXONOMY_ELEMENT);
			String lsFromCache = "false";
			if(loDoc != null){
				String lsDocumentString= XMLUtil.getXMLAsString(loDoc);
				lsFromCache = (lsDocumentString.length()>0)?ApplicationConstants.TRUE:ApplicationConstants.FALSE;
			}

			loChannel.setData(ApplicationConstants.BUSINESS_APPLICATION_ID,asAppId);
			loChannel.setData(ApplicationConstants.NEW_ORGANIZATION_ID,asOrgId);
			loChannel.setData(ApplicationConstants.ELEMENT_TYPE,ApplicationConstants.SERVICE_AREA);
			loChannel.setData("abFromCache",lsFromCache);

			//Checks if the action is saveRelatedServices then execute this block to save the related services
		}else if(asAction != null && asAction.equalsIgnoreCase("saveRelatedServices")){

			String lsSelectedService = aoRequest.getParameter("selectedService");
			List<TaxonomyServiceBean> loSelectedServiceListDB = (List<TaxonomyServiceBean>)ApplicationSession.getAttribute(aoRequest, "loSelectedServiceListDB");
			Map<String,List<TaxonomyServiceBean>> loInsertUpdateDeleteMap = saveTaxonomyService(lsSelectedService, asUserRole, asOrgId, loSelectedServiceListDB,asAppId);
			loChannel.setData(ApplicationConstants.INSERT_UPDATE_DELETE_MAP,loInsertUpdateDeleteMap);
			//Checks if the action is saveServices then execute this block to save the services
		}else if(asAction != null && asAction.equalsIgnoreCase("saveServices")){

			String lsSelectedService = aoRequest.getParameter("selectedService");
			ApplicationSession.setAttribute(lsSelectedService, aoRequest, "selectedService");
			List<TaxonomyServiceBean> loSelectedServiceListDB = (List<TaxonomyServiceBean>)ApplicationSession.getAttribute(aoRequest, "loSelectedServiceListDB");
			Map<String,List<TaxonomyServiceBean>> loInsertUpdateDeleteMap = saveTaxonomyService(lsSelectedService, asUserRole, asOrgId, loSelectedServiceListDB,asAppId);
			loChannel.setData(ApplicationConstants.INSERT_UPDATE_DELETE_MAP,loInsertUpdateDeleteMap);
			//Checks if the action is showsimilarServices then execute this block to show the related services
		}else if(asAction != null && asAction.equalsIgnoreCase("showsimilarServices")){

			Document loDoc = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(ApplicationConstants.TAXONOMY_ELEMENT);
			String lsFromCache = "false";
			if(loDoc != null){
				String lsDocumentString= XMLUtil.getXMLAsString(loDoc);
				lsFromCache = (lsDocumentString.length()>0)?ApplicationConstants.TRUE:ApplicationConstants.FALSE;
			}

			loChannel.setData(ApplicationConstants.APPID,asAppId);
			loChannel.setData(ApplicationConstants.NEW_ORGANIZATION_ID,asOrgId);
			loChannel.setData(ApplicationConstants.ELEMENT_TYPE,ApplicationConstants.SERVICE_AREA);
			loChannel.setData("abFromCache",lsFromCache);
			//Transaction required to show the related services
			loChannel.setData("transaction_name","RelatedServices");
		}else if(asAction != null && asAction.equalsIgnoreCase("showsimilarServicesAll")){

			Document loDoc = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(ApplicationConstants.TAXONOMY_ELEMENT);
			String lsFromCache = "false";
			if(loDoc != null){
				String lsDocumentString= XMLUtil.getXMLAsString(loDoc);
				lsFromCache = (lsDocumentString.length()>0)?ApplicationConstants.TRUE:ApplicationConstants.FALSE;
			}

			loChannel.setData(ApplicationConstants.APPID,asAppId);
			loChannel.setData(ApplicationConstants.NEW_ORGANIZATION_ID,asOrgId);
			loChannel.setData(ApplicationConstants.ELEMENT_TYPE,ApplicationConstants.SERVICE_AREA);
			loChannel.setData("abFromCache",lsFromCache);
			//Transaction required to show the related services
			loChannel.setData("transaction_name","RelatedServicesAll");
		}
		//block of code to be executed when user will select the existing contract from the dropdown
		return loChannel;
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
	public Channel getChannelObject(String asSectionName, String asOrgId,
			String asAppId, String asAppStatus, String asAppDataForUpdate,
			String asAction, String asUserRole, RenderRequest aoRequest,
			String asTaxonomyName) throws ApplicationException {
		Channel loChannel = new Channel();
		//Checks if the action is addservice then execute this block to add the service
		if(PortalUtil.parseQueryString(aoRequest, "action") != null){
			Document loDoc = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(ApplicationConstants.TAXONOMY_ELEMENT);
			String lsFromCache = "false";
			if(loDoc != null){
				String lsDocumentString = XMLUtil.getXMLAsString(loDoc);
				lsFromCache = (lsDocumentString.length()>0)?ApplicationConstants.TRUE:ApplicationConstants.FALSE;
			}

			loChannel.setData(ApplicationConstants.BUSINESS_APPLICATION_ID,asAppId);
			loChannel.setData(ApplicationConstants.NEW_ORGANIZATION_ID,asOrgId);
			loChannel.setData(ApplicationConstants.ELEMENT_TYPE,ApplicationConstants.SERVICE_AREA);
			loChannel.setData("abFromCache",lsFromCache);
			//Checks if the action is saveRelatedServices then execute this block to save the related services
		}else if(asAction != null && asAction.equalsIgnoreCase("addservice")){

			loChannel.setData(ApplicationConstants.BUSINESS_APPLICATION_ID,asAppId);
			loChannel.setData(ApplicationConstants.NEW_ORGANIZATION_ID,asOrgId);
			loChannel.setData(ApplicationConstants.ELEMENT_TYPE,ApplicationConstants.SERVICE_AREA);
			loChannel.setData("abFromCache",ApplicationConstants.FALSE);
			//Checks if the action is showsimilarServices then execute this block to save the similar services
		}else if(asAction != null && asAction.equalsIgnoreCase("showsimilarServices")){

			Document loDoc = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(ApplicationConstants.TAXONOMY_ELEMENT);
			String lsFromCache = "false";
			if(loDoc != null){
				String lsDocumentString= XMLUtil.getXMLAsString(loDoc);
				lsFromCache = (lsDocumentString.length()>0)?ApplicationConstants.TRUE:ApplicationConstants.FALSE;
			}

			loChannel.setData(ApplicationConstants.APPID,asAppId);
			loChannel.setData(ApplicationConstants.NEW_ORGANIZATION_ID,asOrgId);
			loChannel.setData(ApplicationConstants.ELEMENT_TYPE,ApplicationConstants.SERVICE_AREA);
			loChannel.setData("abFromCache",lsFromCache);
			//Transaction required to show the related services
			loChannel.setData("transaction_name","RelatedServices");
		}
		else if(asAction != null && asAction.equalsIgnoreCase("showsimilarServicesAll")){

			Document loDoc = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(ApplicationConstants.TAXONOMY_ELEMENT);
			String lsFromCache = "false";
			if(loDoc != null){
				String lsDocumentString= XMLUtil.getXMLAsString(loDoc);
				lsFromCache = (lsDocumentString.length()>0)?ApplicationConstants.TRUE:ApplicationConstants.FALSE;
			}

			loChannel.setData(ApplicationConstants.APPID,asAppId);
			loChannel.setData(ApplicationConstants.NEW_ORGANIZATION_ID,asOrgId);
			loChannel.setData(ApplicationConstants.ELEMENT_TYPE,ApplicationConstants.SERVICE_AREA);
			loChannel.setData("abFromCache",lsFromCache);
			//Transaction required to show the related services
			loChannel.setData("transaction_name","RelatedServicesAll");
		}else if(asAction != null && asAction.equalsIgnoreCase("showServices")){

			Document loDoc = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(ApplicationConstants.TAXONOMY_ELEMENT);
			String lsFromCache = "false";
			if(loDoc != null){
				String lsDocumentString= XMLUtil.getXMLAsString(loDoc);
				lsFromCache = (lsDocumentString.length()>0)?ApplicationConstants.TRUE:ApplicationConstants.FALSE;
			}

			loChannel.setData(ApplicationConstants.BUSINESS_APPLICATION_ID,asAppId);
			loChannel.setData(ApplicationConstants.NEW_ORGANIZATION_ID,asOrgId);
			loChannel.setData(ApplicationConstants.ELEMENT_TYPE,ApplicationConstants.SERVICE_AREA);
			loChannel.setData("abFromCache",lsFromCache);

			//Checks if the action is saveRelatedServices then execute this block to save the related services
		}
		return loChannel;
	}
	
	/**
	 * This method gets map to be rendered
	 *  
	 * @param asAction - the action to be performed
	 * @param asSectionName - current section name
	 * @param aoChannel - channel object with data, will be needed to be passed to MapForRender
	 * @param aoRequest - Render Request
	 * @return the map to be rendered on front end
	 * @throws ApplicationException
	 */
	@Override
	public Map<String, Object> getMapForRender(String asAction,
			String asSectionName, Channel aoChannel, RenderRequest aoRequest)
			throws ApplicationException {

		Map<String,Object> loMapForRender  = new HashMap<String, Object>();
		
		//Checks if the action is showServices then execute this block to show the services
		if(asAction != null && asAction.equalsIgnoreCase("showServices")){
			String loTaxonomyTree = (String)ApplicationSession.getAttribute(aoRequest,"finalTreeAsString");
			List<TaxonomyServiceBean> loSelectedServiceListDB = (List<TaxonomyServiceBean>)ApplicationSession.getAttribute(aoRequest,"loSelectedServiceListDB");
			loMapForRender.put("selectedServiceList", loSelectedServiceListDB);
			List <String> loErrorMsg = null;
			if(aoRequest.getParameter("error_msg") != null){
				loErrorMsg = new ArrayList<String>();
				loErrorMsg.add(aoRequest.getParameter("error_msg"));
			}
			ApplicationSession.setAttribute(loTaxonomyTree, aoRequest, "finalTreeAsString");
			ApplicationSession.setAttribute(loSelectedServiceListDB, aoRequest, "selectedServiceList");
			loMapForRender.put("errorToDisplay", loErrorMsg);
			loMapForRender.put("finalTreeAsString", loTaxonomyTree);
			ApplicationSession.setAttribute(loTaxonomyTree, aoRequest, "finalTreeAsString");
			ApplicationSession.setAttribute(loSelectedServiceListDB, aoRequest, "selectedServiceList");
			loMapForRender.put("errorToDisplay", loErrorMsg);
			loMapForRender.put("finalTreeAsString", loTaxonomyTree);
		}

		return loMapForRender;
	}

	/**
	 * This method is used to insert update and delete the selected taxonomy services
	 * 
	 * @param aoSelectedService aoSelectedService comma separated string
	 * @param aoUserId user id
	 * @param asOrgId organization id
	 * @param aoSelectedServiceListDB selected list from database
	 * @return boolean true false
	 * @throws ApplicationException exception
	 * 
	 */
	private Map<String,List<TaxonomyServiceBean>> saveTaxonomyService(final String aoSelectedService,final String aoUserId,final String asOrgId,
			final List<TaxonomyServiceBean> aoSelectedServiceListDB,final String asAppId) throws ApplicationException{
		Integer loCounter = 1;
		List<TaxonomyServiceBean> loInsertList = new ArrayList<TaxonomyServiceBean>();
		List<TaxonomyServiceBean> loUpdateList = new ArrayList<TaxonomyServiceBean>();
		List<TaxonomyServiceBean> loDeleteList = new ArrayList<TaxonomyServiceBean>();
		List<TaxonomyServiceBean> loUpdateDeleteList = new ArrayList<TaxonomyServiceBean>();
		Map<String,List<TaxonomyServiceBean>> loInsertUpdateDeleteMap = new LinkedHashMap<String, List<TaxonomyServiceBean>>();
		List<TaxonomyServiceBean> loTaxonomyList = new ArrayList<TaxonomyServiceBean>();

		// add the db list object into the delete list
		if(aoSelectedServiceListDB!=null && !aoSelectedServiceListDB.isEmpty()){
			for (TaxonomyServiceBean loTaxonomyServiceBean : aoSelectedServiceListDB) {
				loDeleteList.add(loTaxonomyServiceBean);
			}
		}
		// check the condition whether selected service coming from jsp is empty or not
		if(aoSelectedService!=null && !aoSelectedService.equals("")){
			ArrayList<String> lsSelectedServiceList =  new  ArrayList<String>(Arrays.asList(aoSelectedService.split(",")));
			// code to separate the update ids and delete ids 
			if(aoSelectedServiceListDB!=null && !aoSelectedServiceListDB.isEmpty()){
				for (TaxonomyServiceBean loTaxonomyServiceBean : aoSelectedServiceListDB) {
					for (String lsElementId : lsSelectedServiceList) {
						if(loTaxonomyServiceBean.getServiceElementId().equalsIgnoreCase(lsElementId)){
							loTaxonomyServiceBean.setModifiedBy("modified");
							loTaxonomyServiceBean.setModifiedDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
							loUpdateList.add(loTaxonomyServiceBean);
							loDeleteList.remove(loTaxonomyServiceBean);
							// add update list into the map 
							loInsertUpdateDeleteMap.put("update", loUpdateList);
							break;
						}
					}
				}
			}
			// add the delete list into the map
			loInsertUpdateDeleteMap.put("delete", loDeleteList);
			// create one list from update and delete list
			loUpdateDeleteList.addAll(loUpdateList);
			loUpdateDeleteList.addAll(loDeleteList);
			// code to create the insert list
			long llTimeStamp = System.currentTimeMillis();
			for (String lsElementId : lsSelectedServiceList) {
				TaxonomyServiceBean loTaxonomyServiceBean = new TaxonomyServiceBean(); 
				loTaxonomyServiceBean.setServiceApplicationId("sr_"+llTimeStamp+loCounter);
				loTaxonomyServiceBean.setBusinessApplicationId(asAppId);
				loTaxonomyServiceBean.setServiceElementId(lsElementId);
				loTaxonomyServiceBean.setOrganizationId(asOrgId);
				loTaxonomyServiceBean.setUserId(aoUserId);
				loTaxonomyServiceBean.setSubmittedBy(aoUserId);
				loTaxonomyServiceBean.setSubmittionDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
				loTaxonomyServiceBean.setCreatedBy(aoUserId);
				loTaxonomyServiceBean.setCreatedDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
				loTaxonomyServiceBean.setModifiedBy(aoUserId);
				loTaxonomyServiceBean.setModifiedDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
				loTaxonomyServiceBean.setStartDate(null);
				loTaxonomyServiceBean.setExpirationDate(null);
				loTaxonomyServiceBean.setRemovedFlag("0");
				loTaxonomyServiceBean.setInActiveFlag("0");
				loTaxonomyServiceBean.setStatusId("Active");
				loTaxonomyServiceBean.setServiceStatus(ApplicationConstants.NOT_STARTED_STATE);
				loTaxonomyServiceBean.setProcessStatus(ApplicationConstants.START_STATUS);
				loTaxonomyList.add(loTaxonomyServiceBean);
				loCounter++; 
			}
			// create insert list 
			for (TaxonomyServiceBean loTaxonomyServiceBean : loTaxonomyList) {
				loInsertList.add(loTaxonomyServiceBean);
			}

			// now create a final insert list after update and delete list
			if(loUpdateDeleteList!=null && !loUpdateDeleteList.isEmpty()){
				for (TaxonomyServiceBean loServiceBean : loUpdateDeleteList) {
					for (TaxonomyServiceBean loTaxonomyServiceBean : loTaxonomyList) {
						if(loServiceBean.getServiceElementId().equalsIgnoreCase(loTaxonomyServiceBean.getServiceElementId())){
							loInsertList.remove(loTaxonomyServiceBean);
						}
					}
				}
			}
			// add insert list into the map
			loInsertUpdateDeleteMap.put("insert", loInsertList);
		}
		// execute the transaction manager to validate the request 
		return loInsertUpdateDeleteMap;
	}
	/**
	 * This method gets map to be rendered
	 * 
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
