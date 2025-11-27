package com.nyc.hhs.batch.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.xml.DOMConfigurator;
import org.json.JSONArray;
import org.json.JSONException;

import com.nyc.hhs.batch.IBatchQueue;
import com.nyc.hhs.batch.ldap.LDAPConstants;
import com.nyc.hhs.batch.ldap.LDAPOperations;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.cache.ICacheManager;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.CityUserDetailsBeanForBatch;
import com.nyc.hhs.model.StaffDetails;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.XMLUtil;
import com.nyc.hhs.webservice.restful.SamlWebServices;

/**
 * This class is being used for synchronizing LDAP and database records for
 * internal users
 */

public class LdapBatch implements IBatchQueue
{
	private static final LogInfo LOG_OBJECT = new LogInfo(LdapBatch.class);

	/**
	 * Blank implementation of getQueue method of interface
	 * 
	 * @param aoMParameters HashMap of Parameter
	 * @return null
	 */
	@SuppressWarnings("rawtypes")
	public List<CityUserDetailsBeanForBatch> getQueue(Map aoMParameters)
	{
		return null;
	}

	/**
	 * Implementation of the executeQueue method . This method will call all the
	 * other methods for executing the batch operations
	 * <ul>
	 * <li> Method Updated in R4 </li>
	 * </ul>

	 * @param aoLQueue List of Queue
	 * @throws ApplicationException 
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public void executeQueue(List aoLQueue) throws ApplicationException
	{
		// Start SAML QC 9165 R 7.8.0 - put in separate methods synchronization city users with LDAP and provider members with NYCID user profiles 
		LOG_OBJECT.Info("Executing LDAP Batch .. ");
		String lsLog4jPath = PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
				P8Constants.PROPERTY_PREDEFINED_LOG4J_PATH);
		DOMConfigurator.configure(lsLog4jPath);
		DateFormat currentDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		Date tmpDate = new Date();
		String endDate = currentDateFormat.format(tmpDate);
		
		//HashMap<String, CityUserDetailsBeanForBatch> loUserLDAPHashMap = null;
		//HashMap<String, CityUserDetailsBeanForBatch> loUserDetailsFromDB = null;
        String startDate = getStartDate();
        LOG_OBJECT.Info("Start Date Time :: " + startDate);	
        LOG_OBJECT.Info("End Date Time   :: " + endDate);		
		
        
		try
		{			
			ICacheManager loCacheManager = BaseCacheManagerWeb.getInstance();
			Object loCacheObject = XMLUtil.getDomObj(this.getClass().getResourceAsStream(
					"/" + ApplicationConstants.TRANSACTION_CONFIG));
			loCacheManager.putCacheObject(ApplicationConstants.TRANSACTION_ELEMENT, loCacheObject);
			
			//synchronized all city & agency users with LDAP
			agencyCitiUsersSynchronizationWithLdap();  

			
			// Start qc 9594 --Enhance the NYCID sync-up web service call (LDPA batch job), as the NYCID web service limitation may occur while the query result exceeds size limit.
			//synchronized providers with NYCID LDAP
			
			// use the date range to limit to one day per webservice call 
			//error message from webservice response :  {"ERRORS":{"cpui.sizeLimit":"Number of users returned exceeds size limit."}}
			Date starteParsedDate = currentDateFormat.parse(startDate);
			Date endParsedDate = currentDateFormat.parse(endDate);			
			
			Calendar startCalendar = new GregorianCalendar();
		    Calendar endCalendar = new GregorianCalendar();
			startCalendar.setTime(starteParsedDate);
			endCalendar.setTime(endParsedDate);
			
			while (startCalendar.before(endCalendar)) 
			{	     
				Date startDateTemp = startCalendar.getTime();
				startCalendar.add(Calendar.DATE, 1);	// increase one day per request       
			    
				Date endDateTemp=startCalendar.getTime(); //after increase one day, use it as an end date
		       
			    if(endDateTemp.after(endParsedDate)) {
		        	endDateTemp = endParsedDate;
		        }
		        String startDateStr=currentDateFormat.format(startDateTemp);
		        String endDateStr=currentDateFormat.format(endDateTemp);
		        LOG_OBJECT.Info("start date string:"+ startDateStr  +",end date string:" +  endDateStr);
		      
				providerUsersSynchronizationWithNYCID(startDateStr, endDateStr);  
			}			
			// End qc 9594 --Enhance the NYCID sync-up web service call (LDPA batch job), as the NYCID web service limitation may occur while the query result exceeds size limit.
			
			/* code moved to agencyCitiUsersSynchronizationWithLdap
			/*
			// fetching list of groups from NYC Agency Details Table
			List loList = generateAgencyCityGroupList();
			if (!loList.isEmpty())
			{
				LOG_OBJECT.Info("List of All the Groups : " + loList.toString());
				// fetching data from LDAP
				LDAPOperations loLdapOper = new LDAPOperations();
				loUserLDAPHashMap = loLdapOper.getMemberListForLDAPGroup(loList);
				LOG_OBJECT.Info(" fetched valid internal users from Ldap.. ");
				// fetching active user details from DB
				loUserDetailsFromDB = getCityAgencyUserDetailsFromDB();
				LOG_OBJECT.Info(" fetched active users from database.. ");
				// identifying the list of users which has to be inserted,
				// updated in database
				if (loUserLDAPHashMap != null && !loUserLDAPHashMap.isEmpty())
				{
					loLdapOper.identifyingListOfUsersToBeUpdated(loUserLDAPHashMap, loUserDetailsFromDB);
					loLdapOper.deactivateRemainingUsersFromDatabase(loUserLDAPHashMap, loUserDetailsFromDB);
				}
				else
				{
					LOG_OBJECT.Debug("Member list fetched from LDAP is null ..  ");
				}
				LOG_OBJECT.Info("Finished LDAP Batch Execution .. ");
			}
			else
			{
				LOG_OBJECT.Debug("Agency and City users group list fetched from Database is null .. ");
			}
            */ 
			// End SAML QC 9165 R 7.8.0 put in separate methods synchronization city users with LDAP and provider members with NYCID user profiles 
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in LdapBatch.executeQueue()", aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error while executing LdapBatch.executeQueue() ..", aoEx);
			new ApplicationException("Exception in LdapBatch.executeQueue() : " + aoEx.getMessage());
		}
	}

	
	/**
	 * This Method returns the list of all city & agency groups in LDAP
	 * <ul>
	 * <li> Method Updated in R4 </li>
	 * </ul>
	 * This Method returns the list of all city & agency groups in LDAP
	 * 
	 * @return loOutputList List of groups fetched from database
	 * @throws ApplicationException
	 */
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List generateAgencyCityGroupList() throws ApplicationException{
		List loOutputList = new ArrayList();
		Channel loChannel = new Channel();
		loOutputList.add(LDAPConstants.CITY_HHSA_I_MANAGER);
		loOutputList.add(LDAPConstants.CITY_HHSA_I_EXEC_ADMIN);
		loOutputList.add(LDAPConstants.CITY_HHSA_I_STAFF);
		TransactionManager.executeTransaction(loChannel, LDAPConstants.FETCH_AGENCY_ID_FOR_LDAP_BATCH);
		List loAgencyIdList = (List) loChannel.getData(LDAPConstants.AGENCY_CITY_LDAP_GROUPS);
		if (loAgencyIdList != null)
		{
			for (int liAgencyCount = 0; liAgencyCount < loAgencyIdList.size(); liAgencyCount++)
			{
				loOutputList.add(LDAPConstants.AGENCY_NAME_PREFIX + loAgencyIdList.get(liAgencyCount));

				Iterator loIt = ApplicationConstants.ROLE_AGENCY.keySet().iterator();
				while (loIt.hasNext())
				{
					loOutputList.add((LDAPConstants.AGENCY_NAME_PREFIX + loAgencyIdList.get(liAgencyCount)) + "_"
							+ loIt.next());
				}
			}
		}
		else
		{
			LOG_OBJECT.Debug("Agency records list fetched from Agency user details is null ..");
		}
		return loOutputList;
	}

	/**
	 * This Method Fetches all the details of city & agency users and it returns
	 * a hashmap with key as User Dn & values as city user bean
	 * <ul>
	 * <li>Execute transaction id <b> fetchCityUserDetailsForBatch</b></li>
	 * <li> Method Updated in R4 </li>
	 * <li>Execute transaction id <b> fetchCityUserDetailsForBatch</b></li>
	 * </ul>
	 * @return loOutput hashmap of userdn and city user detail bean
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	private HashMap<String, CityUserDetailsBeanForBatch> getCityAgencyUserDetailsFromDB() throws ApplicationException
	{
		HashMap<String, CityUserDetailsBeanForBatch> loOutput = new HashMap<String, CityUserDetailsBeanForBatch>();
		Map<String, String> loHMap = new HashMap<String, String>();
		loHMap.put(LDAPConstants.ACTIVE_FLAG, LDAPConstants.STRING_ONE);

		Channel loChannelObj = new Channel();
		loChannelObj.setData(LDAPConstants.HASH_MAP_VAR, loHMap);
		TransactionManager.executeTransaction(loChannelObj, LDAPConstants.FETCH_CITY_USERS_DETAILS_FOR_BATCH);

		LOG_OBJECT.Info("successfully fetched details of Active users from CITY_USER_DETAILS");
		List<CityUserDetailsBeanForBatch> loCityUserDetailsBeanList = (List<CityUserDetailsBeanForBatch>) loChannelObj
				.getData(LDAPConstants.CITY_USER_DETAILS);
		if (null != loCityUserDetailsBeanList)
		{
			for (int liCount = 0; liCount < loCityUserDetailsBeanList.size(); liCount++)
			{
				loCityUserDetailsBeanList.get(liCount).setMsUserId(null);
				String lsMapKey = loCityUserDetailsBeanList.get(liCount).getMsUserDn();
				loOutput.put(lsMapKey, loCityUserDetailsBeanList.get(liCount));
			}
		}
		else
		{
			LOG_OBJECT.Info("User details fetched from database is null .. ");
		}
		return loOutput;
	}
	
	/** 
	 * R 7.8.0 QC 9165 - refactoring code: put code in separate method outside of executeQueue() method
	 * This Method synchronized all city & agency users with LDAP
	 * @throws ApplicationException
	 */
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void agencyCitiUsersSynchronizationWithLdap() throws ApplicationException{
		
		HashMap<String, CityUserDetailsBeanForBatch> loUserLDAPHashMap = null;
		HashMap<String, CityUserDetailsBeanForBatch> loUserDetailsFromDB = null;

		LOG_OBJECT.Info("Executing LDAP Batch .. ");
		try
		{			
			// fetching list of groups from NYC Agency Details Table
			List loList = generateAgencyCityGroupList();
			if (!loList.isEmpty())
			{
				LOG_OBJECT.Info("List of All the Groups : " + loList.toString());
				// fetching data from LDAP
				LDAPOperations loLdapOper = new LDAPOperations();
				loUserLDAPHashMap = loLdapOper.getMemberListForLDAPGroup(loList);
				LOG_OBJECT.Info(" fetched valid internal users from Ldap.. ");
				// fetching active user details from DB
				loUserDetailsFromDB = getCityAgencyUserDetailsFromDB();
				LOG_OBJECT.Info(" fetched active users from database.. ");
				// identifying the list of users which has to be inserted,
				// updated in database
				if (loUserLDAPHashMap != null && !loUserLDAPHashMap.isEmpty())
				{
					loLdapOper.identifyingListOfUsersToBeUpdated(loUserLDAPHashMap, loUserDetailsFromDB);
					loLdapOper.deactivateRemainingUsersFromDatabase(loUserLDAPHashMap, loUserDetailsFromDB);
				}
				else
				{
					LOG_OBJECT.Debug("Member list fetched from LDAP is null ..  ");
				}
				LOG_OBJECT.Info("Finished LDAP Batch Execution .. ");
			}
			else
			{
				LOG_OBJECT.Debug("Agency and City users group list fetched from Database is null .. ");
			}

		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in LdapBatch.executeQueue()", aoAppEx);
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error while executing LdapBatch.executeQueue() ..", aoEx);
		}
	}
	
	/** 
	 * Start SAML R 7.8.0 QC 9165 
	 * This Method synchronize all active provider users with NYCID 
	 * @throws ApplicationException
	 */
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void providerUsersSynchronizationWithNYCID(String startDate, String endDate) throws ApplicationException{
		
		LOG_OBJECT.Info("Executing NYCD Profiles synchronization with Accelerator db :: LDAP Batch .. ");
		List<StaffDetails> allStaffDetailsList  = null;
		HashMap<String, StaffDetails> allStaffDetailsMap = new HashMap<String, StaffDetails>();
		List<String> userDNList = new ArrayList<String>();
		boolean status = false;
		String jsonResponse = null;		
		try
		{			
			// fetching list of groups from NYC Agency Details Table
			allStaffDetailsList = getAllActiveOrgStaffList();
			LOG_OBJECT.Info("Finished getAllActiveOrgStaffList .. ");
			
			if (allStaffDetailsList != null && !allStaffDetailsList.isEmpty())
			{   
				LOG_OBJECT.Info("getAllActiveOrgStaffList .. list size:: "+allStaffDetailsList.size());
				for (StaffDetails loStaffDetailsBean : allStaffDetailsList)
				{
					LOG_OBJECT.Info("current userdn :: "+loStaffDetailsBean.getMsUserDN());
					
					if(null!=loStaffDetailsBean.getMsUserDN() && !loStaffDetailsBean.getMsUserDN().contains(",") && !loStaffDetailsBean.getMsUserDN().contains("=")) //for test only!!!!
					{   
						allStaffDetailsMap.put(loStaffDetailsBean.getMsUserDN(), loStaffDetailsBean);
						if(userDNList!=null && !userDNList.contains(loStaffDetailsBean.getMsUserDN()))
						{	
							userDNList.add(loStaffDetailsBean.getMsUserDN());
						}	
					}	
			
				}
				LOG_OBJECT.Info("============load MOCS Providers into MAP: " +allStaffDetailsMap.size());
				//LOG_OBJECT.Info("============Unsorted userDNList: " +userDNList);
				Collections.sort(userDNList);   
				LOG_OBJECT.Info("============Sorted userDNList zize: " +userDNList.size());
												
				SamlWebServices nycidws = new SamlWebServices();
				LOG_OBJECT.Info("============call   web service: nycidws.getUsersByDateModified(startDate, endDate, null)");
				
				//------------------------------------------------------------------------------
				//remove after test
				//jsonResponse = nycidws.getUsersByDateModified("07/15/2021 14:00", "07/15/2021 15:15", null);
				//--------------------------------------------------------------------
				jsonResponse = nycidws.getUsersByDateModified(startDate, endDate, null);
				LOG_OBJECT.Info("==========got resopnse length:: "+jsonResponse.length());
				LOG_OBJECT.Info("==========got resopnse :: "+jsonResponse);
				status = processNYCProfilesResponse(jsonResponse, allStaffDetailsMap);
				LOG_OBJECT.Info("\n=========status after processing result of getUsersByDateModified :: "+status);
				
				//status = false;
				
				if(!status)
				{ 
					providerUsersSynchronizationWithNYCIDbyUserId(userDNList, allStaffDetailsMap, startDate, endDate);
				}
				else
				{
					updateLastSeccessfulUpdateDate(endDate); 
					LOG_OBJECT.Info("Finished NYCID synchronization :: NYCID Web Service getUsersByDateModified has been call succsefully. ");
				}
			
				LOG_OBJECT.Info("Finished NYCID synchronization .. ");
			}	
			else
				LOG_OBJECT.Info("Provider Members list fetched from Database is null .. ");
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in LdapBatch.providerUsersSynchronizationWithNYCID", aoAppEx);
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error while executing LdapBatch.providerUsersSynchronizationWithNYCID ..", aoEx);
		}
	
	}
	
	
	/** 
	 * Start SAML R 7.8.0 QC 9165 
	 * This Method synchronize all active provider users with NYCID 
	 * @throws ApplicationException
	 */
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void providerUsersSynchronizationWithNYCIDbyUserId(List<String> userDNList, HashMap<String, StaffDetails> allStaffDetailsMap, String startDate, String endDate) throws ApplicationException
	{
		LOG_OBJECT.Info("Executing providerUsersSynchronizationWithNYCIDbyUserId Profiles synchronization with Accelerator db :: LDAP Batch .. ");
		try
		{	
			if (userDNList != null && !userDNList.isEmpty())
			{   
				int max = getMaximunCounter();
				
				SamlWebServices nycidws = new SamlWebServices();
				int cnt = 0;
				boolean status = false;
				boolean finalStatus = true;
				String jsonResponse = null;
				
				StringBuilder sb = new StringBuilder();
				for (String loUserDNList : userDNList)
				{ // process first 100
					if(cnt == max)
					{  
						LOG_OBJECT.Info("=====Process .. "+max);
						LOG_OBJECT.Info("=====run Web Service getUsersByGuid====");
						jsonResponse = nycidws.getUsersByGuids(sb.toString());
						LOG_OBJECT.Info("=====Web Service response lenght :: "+jsonResponse.length());
						status = processNYCProfilesResponse(jsonResponse, allStaffDetailsMap);
						sb = new StringBuilder();
					    cnt = 0;
					}
					sb.append(loUserDNList);
					cnt++;
					if(cnt < max)
					{
						sb.append(",");
					}
				}
				// process last portion of users
				if(cnt > 0 && cnt < max && sb.length() > 0)
				{	//remove last , sb.deleteCharAt(sb.length() - 1) 
					LOG_OBJECT.Info("=====Process the last  .. ");
					sb.setLength(sb.length() - 1);
					// NYCID WS call
					jsonResponse = nycidws.getUsersByGuids(sb.toString()); 
					status = processNYCProfilesResponse(jsonResponse, allStaffDetailsMap);
					if(!status)
						finalStatus = false;
				}
				
				if(finalStatus)
				{
					//update Application_settings with last successful update date
					updateLastSeccessfulUpdateDate(endDate); 
					LOG_OBJECT.Info("Finished NYCID synchronization :: NYCID Web Service getUsersByDateModified has been call succsefully. ");
					
				}
				LOG_OBJECT.Info("Finished NYCID synchronization .. ");
			}
			else
			{
				LOG_OBJECT.Debug("Providers Member list fetched from Database is null .. ");
			}

		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in LdapBatch.providerUsersSynchronizationWithNYCIDbyUserId", aoAppEx);
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error while executing LdapBatch.providerUsersSynchronizationWithNYCIDbyUserId ..", aoEx);
		}
	
	}

	
	private boolean processNYCProfilesResponse(String jsonResponse, HashMap<String, StaffDetails> allStaffDetailsMap) {
		// TODO Auto-generated method stub
	try{	
		
		//{"ERRORS":{"cpui.sizeLimit":"Number of users returned exceeds size limit."}}
		if(jsonResponse!=null && jsonResponse.contains("ERRORS"))
		{
			String[] ea = jsonResponse.split(":", 2);
			LOG_OBJECT.Info(ea[0]);
			LOG_OBJECT.Info(ea[1]);
			LOG_OBJECT.Error("Error happened durin NYCID WS call :: ", ea[1]);
			return false;
		}
		else if("[]".equals(jsonResponse))
		{
				LOG_OBJECT.Info("NYCID WS call :: No user has been modified: ");
				return true;
		}
		else
		{				
			JSONArray ja = new JSONArray(jsonResponse);
			System.out.println("=======processNYCProfilesResponse: get jsonarr: "+ja);
			
			for(int i=0; i < ja.length(); i++)
			{
				
				LOG_OBJECT.Info("\nget ja.getJSONObject("+i+"): "+ja.getJSONObject(i));
			    String firstName = ja.getJSONObject(i).optString("firstName");
			    String lastName = ja.getJSONObject(i).optString("lastName");
			    String middleInitial = ja.getJSONObject(i).optString("middleInitial").trim();
			    if (("NA").equalsIgnoreCase(middleInitial))
			    {
			    	middleInitial="";
			    }
			    if (middleInitial!= null && !middleInitial.isEmpty() && middleInitial.length()> 1 )
			    {
			    	middleInitial = Character.toString(middleInitial.charAt(0));
			    }
			   
			    boolean termsOfUse = ja.getJSONObject(i).optBoolean("termsOfUse");
			    boolean hasNYCAccount = ja.getJSONObject(i).optBoolean("hasNYCAccount");
			    boolean validated = ja.getJSONObject(i).optBoolean("validated");
			    boolean active = ja.getJSONObject(i).optBoolean("active");
			    boolean nycEmployee = ja.getJSONObject(i).optBoolean("nycEmployee");
			    String guid = ja.getJSONObject(i).optString("id");
			    String email = ja.getJSONObject(i).optString("email");
			    LOG_OBJECT.Info("get guid: "+guid);
			    LOG_OBJECT.Info("get firstName: "+firstName);
			    LOG_OBJECT.Info("get lastName: "+lastName);
			    LOG_OBJECT.Info("get middleInitial: "+middleInitial);
			    LOG_OBJECT.Info("middleInitial.isEmpty(): "+middleInitial.isEmpty());
			    LOG_OBJECT.Info("get active: "+active);
			    LOG_OBJECT.Info("get email: "+email);
			    
			    StaffDetails sd = allStaffDetailsMap.get(guid);
			    if(sd == null)
			    {
			     LOG_OBJECT.Info("db StaffDetails for guid  "+guid + " not found in our StuffDetails table");
			    }			    
			    if(sd!=null)
			    {	
			    	LOG_OBJECT.Info("db dn: "+sd.getMsUserDN());
				    LOG_OBJECT.Info("db firstName: "+sd.getMsStaffFirstName());
				    LOG_OBJECT.Info("db lastName: "+sd.getMsStaffLastName());
				    LOG_OBJECT.Info("db middleInitial: "+sd.getMsStaffMidInitial());
				    LOG_OBJECT.Info("db userStatus: "+sd.getMsUserStatus());
				    LOG_OBJECT.Info("db email: "+sd.getMsStaffEmail());
				    LOG_OBJECT.Info("db NYCUserId: "+sd.getMsNYCUserId());
			    	boolean updateFlag = false;
				    if(!sd.getMsStaffEmail().equalsIgnoreCase(email) 
				    		|| !sd.getMsStaffFirstName().equals(firstName) 
				    		|| !sd.getMsStaffMidInitial().equals(middleInitial) 
				    		|| !sd.getMsStaffLastName().equals(lastName)
				    		|| !sd.getMsStaffEmail().equals(email)
				    		|| !sd.getMsNYCUserId().equals(email) )
				    {
		              
					      // synchronize with MOCS account
					      if(email!=null && !email.isEmpty())	
					      {	  
					    	  sd.setMsStaffEmail(email);
					      	  sd.setMsNYCUserId(email);
					      	  updateFlag = true;
					      }
					      if(!firstName.isEmpty() && firstName!=null)
					      {	  
					      	  sd.setMsStaffFirstName(firstName);
					      	  updateFlag = true;
					      }	  
					      if(!lastName.isEmpty() && lastName!=null)
					      {	  
					    	  sd.setMsStaffLastName(lastName);
					    	  updateFlag = true;
					      }
					      if(!middleInitial.isEmpty() && middleInitial!=null)
					      {	  LOG_OBJECT.Info("===!middleInitial.isEmpty() && middleInitial!=null"); 
					    	  sd.setMsStaffMidInitial(middleInitial); 
					    	  updateFlag = true;
					      }
					     LOG_OBJECT.Info("===updateFlag : "+updateFlag); 
					     if(updateFlag)
					     {	 
					      sd.setMsModifiedBy("system");
					      LOG_OBJECT.Info("===updateStaffdetailsPrividerProfile===== "); 
					      updateStaffdetailsPrividerProfile(sd);
					     }
				    }
				    
				    LOG_OBJECT.Info(" sd.getMsUserStatus() : "+sd.getMsUserStatus()); //msUserStatus
				    /* commented out incativation of user n Accelerator even thou user vas inactivated on NYCID
				    if(active==false && sd.getMsUserStatus().equalsIgnoreCase("Yes"))
				    {  	//in-activate user_status and active_flag in staff organization 'No'
				    	LOG_OBJECT.Info(" Inactivate User Status for guid: "+guid); 
				    	sd.setMsUserStatus("No");
				    	sd.setMsStaffActiveFlag("No");
				    	sd.setMsModifiedBy("system");
				    	updateStaffOrganisationUserStatus(sd);
				    }
				    */
				    LOG_OBJECT.Info("updateFlag : "+updateFlag); 
			    }
			}
			return true;
		} 
	}
	catch (JSONException e) {
	// TODO Auto-generated catch block
	System.out.println(e.getMessage());
	}
	catch(Exception ex) {
		System.out.println(ex.getMessage());
	}
	
	return false;
	
	}
    /*
     * Method in-activate User Status (set 'No') for Provider Member since account has been inactivated on NYCID site
     */
	private void updateStaffOrganisationUserStatus(StaffDetails sd) {
		// TODO Auto-generated method stub
		Channel loChannelObj = new Channel();
		loChannelObj.setData("aoStaffDetails", sd);
		try {
			TransactionManager.executeTransaction(loChannelObj, HHSConstants.UPDATE_STAFF_ORGANIZATION_USER_STATUS);
		} catch (ApplicationException aoExp) {
			LOG_OBJECT.Error("Error occurred while running LDAP batch: updateStaffOrganisation", aoExp);
		}
	
	}
	/*
     * Method updated Provider Member with NYCID Profile changes
     */
	private void updateStaffdetailsPrividerProfile(StaffDetails sd) {
		// TODO Auto-generated method stub
		
			try {
				Channel loChannel = new Channel();
			    loChannel.setData("aoStaffDetails", sd);
				TransactionManager.executeTransaction(loChannel, HHSConstants.CHECK_PROVIDER_NYC_ID_UPDATE);
			} catch (ApplicationException aoExp) 
			{
				// TODO Auto-generated catch block
				LOG_OBJECT.Error("Error occurred while running LDAP batch: email Notification batch:", aoExp);
			}
		
	}
	
	/* R.7.8 SAMLQC9165
	 * This method retrieves the last successful date when Provider members from StuffDetails table were synch with NYCID profiles
	 */
	private int getMaximunCounter() {
		// TODO Auto-generated method stub
		int maxCnt=0;
		try {
				Channel loChannelObj = new Channel();
				loChannelObj.setData("componentName", HHSConstants.MAXIMUM_COUNTER);
				TransactionManager.executeTransaction(loChannelObj, HHSConstants.GET_SETTINGS_VALUE);
				maxCnt = Integer.valueOf((String) loChannelObj.getData("settingsValue"));
			} 
		catch (ApplicationException aoExp) {
				LOG_OBJECT.Error("Error occurred while running LDAP batch: getMaximunCounter", aoExp);
			}
		return maxCnt;
	}
	/* R.7.8 SAMLQC9165
	 * This method retrieves the last successful date when Provider members from StuffDetails table were synch with NYCID profiles
	 */
	private String getStartDate() {
		// TODO Auto-generated method stub
		String startDate="";
		try {
				Channel loChannelObj = new Channel();
				loChannelObj.setData("componentName", HHSConstants.NYCID_UPDATE_DATE);
				TransactionManager.executeTransaction(loChannelObj, HHSConstants.GET_SETTINGS_VALUE);
				startDate = (String) loChannelObj.getData("settingsValue");
			} 
		catch (ApplicationException aoExp) {
				LOG_OBJECT.Error("Error occurred while running LDAP batch: getStartDate", aoExp);
			}
		return startDate;
	}
	
	/*
	 * This method retrieves the last successful date when Provider members from StuffDetails table were synch with NYCID profiles
	 */
	private String updateLastSeccessfulUpdateDate(String endDate) {
		// TODO Auto-generated method stub
		HashMap<String, String> loParam = new HashMap<String, String>();
		loParam.put("lastSuccessfulUpdateDate", endDate);
		loParam.put("componentName", HHSConstants.NYCID_UPDATE_DATE);
		Channel loChannelObj = new Channel();
		loChannelObj.setData("loParam", loParam);
		try {
			TransactionManager.executeTransaction(loChannelObj, HHSConstants.UPDATE_SETTINGS_VALUE);
		} catch (ApplicationException aoExp) {
			LOG_OBJECT.Error("Error occurred while running LDAP batch: updateLastSeccessfulUpdateDate", aoExp);
		}
		return null;
	}
	
	
	/**
	 * This method fetch all entry from STAFF_DETAILS Table
	 * for Active Provider Members
	 * @param aoMybatisSession Mybatis Sql Session
	 * @return loResultList List of all the staff details
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<StaffDetails> getAllActiveOrgStaffList() throws ApplicationException
	{
		List<StaffDetails> loResultList = null;
		try
		{			
			Channel loChannelObj = new Channel();
			TransactionManager.executeTransaction(loChannelObj, ApplicationConstants.GET_ALL_ACTIVE_ORG_STAFF);
			loResultList = (List<StaffDetails>) loChannelObj
					.getData(ApplicationConstants.ALL_STAFF_DETAILS_LIST);
	    }
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error occurred while running email Notification batch:", aoExp);
		}
		
		return loResultList;
	}
	
	// End SAML QC 9165 R 7.8.0
	
	
	// SAML R 7.8.0 QC 9165 test 
	/*
	public static void main(String args[]) throws ApplicationException
	{
		(new LdapBatch()).executeQueue(null);

	}
    
    */
}
