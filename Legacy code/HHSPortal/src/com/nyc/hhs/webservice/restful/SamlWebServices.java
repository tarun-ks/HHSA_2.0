package com.nyc.hhs.webservice.restful;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;
/*
 * SAML REST Web Services R 7.8.0 QC 9165
 */

public class SamlWebServices {
	
	private final LogInfo LOG_OBJECT = new LogInfo(SamlWebServices.class);
	private String NYCIDdomain = "";
	private String NYCIDpassword = "";
	private String NYCIDuser = "";
	private String CHAR_SET="UTF-8";
		
	public SamlWebServices() {
		
		try 
		{
			NYCIDdomain = PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE,
					ApplicationConstants.SAML_NYCID_WEB_SERVICE_URI);
			NYCIDpassword = PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE,
					ApplicationConstants.SAML_NYCID_WEB_SERVICE_PASSWORD);
			NYCIDuser = PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE,
					ApplicationConstants.SAML_NYCID_WEB_SERVICE_USER);
			LOG_OBJECT.Info("NYCIDdomain :: "+ NYCIDdomain);
			LOG_OBJECT.Info("NYCIDuser :: "+ NYCIDuser);
		}
		catch (Exception e)
		{
			LOG_OBJECT.Error("Exception occurred while initialization", e);
		}
	}
	/**
	 * The Authenticate Web Service provides the application with the ability to authenticate a user without using SAML
	 * @param email - required
	 * @param password - required
	 * @param date Time (MM/dd/yyyy HH:mm or M/d/yy HH:mm) - optional
	 * @param NYC.ID userName - required
	 * @param signature  required
	 * @return response in json format
	 * @return String lsReturnMessage returns corresponding message whether user is authenticated {"authenticated":true}
	 * @returns http status code 200 - ok, 400 - bed request, 401 - unauthorized 
	 * @throws Exception
	 */ 	
	public String authenticateUser(String asEmail, String asPassword) 
	{	
		final String forSignature = "GET/account/api/authenticate.htm";
	
		try
		{	LOG_OBJECT.Debug("Begin /GET Authenticate WS request!");
			
		    RestTemplate restTemplate = new RestTemplate();
		    
		    StringBuilder stringToSign = new StringBuilder();
		    stringToSign.append(forSignature);
		    stringToSign.append(asEmail);
		    stringToSign.append(asPassword);
		    stringToSign.append(NYCIDuser);
		    String signature  = HHSUtil.getSignature(stringToSign.toString(), NYCIDpassword);
		    LOG_OBJECT.Debug("signature :: "+ signature);
		    // construct url
		    String url = NYCIDdomain+"authenticate.htm?email="+asEmail+"&password="+asPassword+"&userName="+NYCIDuser+"&signature="+signature;
		    
		    LOG_OBJECT.Debug("url :: "+ url);
		    HttpHeaders headers = new HttpHeaders();
		    headers.setContentType(MediaType.APPLICATION_JSON);
		    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	        HttpEntity<String> entity = new HttpEntity<String>("", headers);
	        
	        ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
	        LOG_OBJECT.Debug("\nRESULT:: \n"+result);
	        Thread.sleep(1000);       	    
	        return result.getBody();
		    
		}
		catch (HttpClientErrorException ec)
		{
			LOG_OBJECT.Error("HttpClientErrorException occurred while autthenticateUser " + ec);
			LOG_OBJECT.Error("      Response body: " + ec.getResponseBodyAsString());
			return  ec.getResponseBodyAsString();
		}
		catch (Exception ex)
		{
			LOG_OBJECT.Error("Exception occurred while authentication", ex);
			return ex.getMessage();
		}
		
	}
	
	
	
	
	/**
	 * The Web Service Email validation is the process of confirming that a user owns the email address he or she registered with NYC.ID.
	 * @param guid- required
	 * @param date Time (MM/dd/yyyy HH:mm or M/d/yy HH:mm) - optional
	 * @param NYC.ID Account userName - required
	 * @param signature  required
	 * @return response in json format
	 * @return returns corresponding message whether users email is registered {"validate":true}
	 * @returns http status code 200 - ok, 400 - bed request, 401 - unauthorized 
	 * @throws Exception
	 */ 	
	public  String validateEmail(String asGuid) 
	{		
		// test environment
	    final String forSignature = "GET/account/api/isEmailValidated.htm";
	    try
		{	        
			System.out.println("Begin /GET Emai Validated request!");
			
		    RestTemplate restTemplate = new RestTemplate();
		   		  
		    StringBuilder stringToSign = new StringBuilder();
		    stringToSign.append(forSignature);
		    stringToSign.append(asGuid);
		    stringToSign.append(NYCIDuser);
		    LOG_OBJECT.Debug("stringToSign :: "+ stringToSign);
		    String signature  = HHSUtil.getSignature(stringToSign.toString(), NYCIDpassword);
		    LOG_OBJECT.Debug("signature :: "+ signature);
		    // Construct url
		    final String url = NYCIDdomain+"isEmailValidated.htm?guid="+asGuid+"&userName="+NYCIDuser+"&signature="+signature;
		    LOG_OBJECT.Debug("url :: "+ url);
		    
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	        HttpEntity<String> entity = new HttpEntity<String>("", headers);
	         
	        ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
	        LOG_OBJECT.Debug("\nRESULT:: \n"+result);
	        
	        return result.getBody();
		    
		}
	    catch (HttpClientErrorException ec)
		{
			LOG_OBJECT.Error("HttpClientErrorException occurred while validateEmail " + ec);
			LOG_OBJECT.Error("      Response body: " + ec.getResponseBodyAsString());
			return  ec.getResponseBodyAsString();
		}
		catch (Exception ex)
		{
			LOG_OBJECT.Error("Exception occurred while validateEmail :: ", ex);
			return ex.getMessage();
			
		}
			
	}
	
	/**
	 * The User Web Service provides the application with user's latest profile information
	 * @param guid - required
	 * @param email - required
	 * @param date Time (MM/dd/yyyy HH:mm or M/d/yy HH:mm) - optional
	 * @param NYC.ID userName - required
	 * @param signature  required
	 * @return response in json format
	 * @return String lsReturnMessage returns user profile in JSON format
	 * @returns http status code 200 - ok, 400 - bed request, 401 - unauthorized 
	 * @throws Exception
	 */ 
	public  String getUser(String asGuid, String asEmail) 
	{	
		final String forSignature = "GET/account/api/user.htm";	
		try
		{	
			
			LOG_OBJECT.Debug("Begin /GET User WS request!");
			
			RestTemplate restTemplate = new RestTemplate();
		    
		    StringBuilder stringToSign = new StringBuilder();
		    stringToSign.append(forSignature);
		    stringToSign.append(asEmail);
		    stringToSign.append(asGuid);
		    stringToSign.append(NYCIDuser);
		    // test environment
		      
		    String signature  = HHSUtil.getSignature(stringToSign.toString(), NYCIDpassword);
		    LOG_OBJECT.Debug("signature :: "+ signature);
		    // construct url
		    String url = NYCIDdomain+"user.htm?guid="+asGuid+"&email="+asEmail+"&userName="+NYCIDuser+"&signature="+signature;
		    LOG_OBJECT.Debug("url :: "+ url);
		    
		    HttpHeaders headers = new HttpHeaders();
		    headers.setContentType(MediaType.APPLICATION_JSON);
	        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	        HttpEntity<String> entity = new HttpEntity<String>("", headers);
	               
	        ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
	        LOG_OBJECT.Debug("\nRESULT:: \n"+result);
	        	        	    
	        return result.getBody();
		    
		}
		catch (HttpClientErrorException ec)
		{
			LOG_OBJECT.Error("HttpClientErrorException occurred while getUser " + ec);
			LOG_OBJECT.Error("      Response body: " + ec.getResponseBodyAsString());
			return  ec.getResponseBodyAsString();
		}
		catch (Exception ex)
		{
			LOG_OBJECT.Error("Exception occurred while getUser", ex);
			return ex.getMessage();
		}
	
	}

	
	/**
	 * The User Web Service provides the application a list of users
	 * whose profile has been modified between a specified start date and optional end date
	 * @param startDate - dateTime (MM/dd/yyyy HH:mm or M/d/yy HH:mm) - Required, if "guids" is not specified
	 * @param guid - Required, if "startDate" is not specified
	 * @param endDate - dateTime (MM/dd/yyyy HH:mm or M/d/yy HH:mm) - Optional, defaults to the current date
	 * @param date Time (MM/dd/yyyy HH:mm or M/d/yy HH:mm) - optional
	 * @param NYC.ID userName - required
	 * @param signature  required
	 * @return response in json format
	 * @return String JSon array returns 
	 * In the event that the list contains more than 1,000 users, an error will be returned. You application may need to make multiple requests with a smaller date range to get the complete list of users that have been modified between the original start date and end date
	 * @returns http status code 200 - ok, 400 - bed request, 401 - unauthorized 
	 * @throws Exception
	 */ 
	public String getUsersByDateModified(String beginDate, String stopDate, Integer numberOfDays) 
	{	
		final String forSignature = "GET/account/api/getUsers.htm";	
		int days = 1;
		DateFormat startDateFormat = new SimpleDateFormat("MM/dd/yyyy 00:00");
		DateFormat currentDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		Date tmpDate = new Date();
		if(numberOfDays!= null && numberOfDays > 0)
		{
			days = numberOfDays;
		}
		String startDate = currentDateFormat.format(tmpDate);
		String endDate = currentDateFormat.format(tmpDate);
		try
		{	
			LOG_OBJECT.Info("Begin /GET getUsersByDateModified WS request! startDate :: "+beginDate +" :: EndDate :: "+stopDate);
			if (null==beginDate || null==stopDate || beginDate.isEmpty() || stopDate.isEmpty()
		    		|| currentDateFormat.parse(beginDate).after(currentDateFormat.parse(endDate))
		    		|| currentDateFormat.parse(stopDate).before(currentDateFormat.parse(beginDate)) 
		    		|| currentDateFormat.parse(stopDate).after(currentDateFormat.parse(endDate))    )
		    {		    	
		    	Calendar cal = Calendar.getInstance();
		    	cal.add(Calendar.DATE, -days);
		       	startDate = startDateFormat.format(cal.getTime());
		    	beginDate = null;
		    }
		    else
		    {
		    	startDate = beginDate;
		    }
		    
			if (null!=beginDate && null!=stopDate && !stopDate.isEmpty() 
		    		&& (currentDateFormat.parse(startDate)).before(currentDateFormat.parse(stopDate)) 
		    		&& (currentDateFormat.parse(stopDate)).before(currentDateFormat.parse(endDate)) )
		    {
		    	endDate = stopDate;
		    }
			RestTemplate restTemplate = new RestTemplate();
		    
		    StringBuilder stringToSign = new StringBuilder();
		    stringToSign.append(forSignature);
		    stringToSign.append(endDate);
		    stringToSign.append(startDate);
		    stringToSign.append(NYCIDuser);
		     
		    String signature  = HHSUtil.getSignature(stringToSign.toString(), NYCIDpassword);
		    LOG_OBJECT.Info("signature :: "+ signature);
		    // construct url
		    String url = NYCIDdomain+"getUsers.htm?startDate="+startDate+"&endDate="+endDate+"&userName="+URLEncoder.encode(NYCIDuser,CHAR_SET)+"&signature="+URLEncoder.encode(signature,CHAR_SET);
		    //String url = NYCIDdomain+"getUsers.htm?startDate="+URLEncoder.encode(startDate,CHAR_SET)+"&endDate="+URLEncoder.encode(endDate,CHAR_SET)+"&userName="+URLEncoder.encode(NYCIDuser,CHAR_SET)+"&signature="+URLEncoder.encode(signature,CHAR_SET);
		    LOG_OBJECT.Info("url :: "+ url);
		    HttpHeaders headers = new HttpHeaders();
		    headers.setContentType(MediaType.APPLICATION_JSON);
	        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	        HttpEntity<String> entity = new HttpEntity<String>("", headers);
	          
	        ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
	        System.out.println("\nSTATUS:: \n"+result.getStatusCode());
	       	
	        return result.getBody();
		    
		}
		catch (HttpClientErrorException ec)
		{
			LOG_OBJECT.Error("HttpClientErrorException occurred while getUsersByDateModified " + ec);
			LOG_OBJECT.Error("      Response body: " + ec.getResponseBodyAsString());
			return  ec.getResponseBodyAsString();
		}
		catch (Exception ex)
		{
			LOG_OBJECT.Error("Exception occurred while getUsersByDateModified", ex);
			return ex.getMessage();
		}
	
	}
	
	/**
	 * The User Web Service provides the application a list of users based on UserDN = guids
	 * @param guids - list of UserDN - Required
	 * @param endDate - dateTime (MM/dd/yyyy HH:mm or M/d/yy HH:mm) - Optional, defaults to the current date
	 * @param date Time (MM/dd/yyyy HH:mm or M/d/yy HH:mm) - optional
	 * @param NYC.ID userName - required
	 * @param signature  required
	 * @return response in json format
	 * @return String JSon array returns 
	 * In the event that the list contains more than 1,000 users, an error will be returned. You application may need to make multiple requests with a smaller date range to get the complete list of users that have been modified between the original start date and end date
	 * @returns http status code 200 - ok, 400 - bed request, 401 - unauthorized 
	 * @throws Exception
	 */ 
	public String getUsersByGuids(String guids_param) 
	{	
		final String forSignature = "GET/account/api/getUsers.htm";	
		try
		{	
			LOG_OBJECT.Info("/nBegin /GET getUsersByGuids WS request! guids :: "+guids_param);
			String guids_sign =  guids_param.replace(",",  "");
			LOG_OBJECT.Info("guids_sign :: "+guids_sign);
					    
			String guids =  guids_param.replace(",",  "&guids=");
			LOG_OBJECT.Info("guids :: "+guids);
					    			
			RestTemplate restTemplate = new RestTemplate();
		    
		    StringBuilder stringToSign = new StringBuilder();
		    stringToSign.append(forSignature);
		    stringToSign.append(guids_sign);
		    stringToSign.append(NYCIDuser);
		    		      
		    String signature  = HHSUtil.getSignature(stringToSign.toString(), NYCIDpassword);
		    LOG_OBJECT.Info("signature :: "+ signature);
		    // construct url
		    //String url = NYCIDdomain+"getUsers.htm?guids="+guids+"&userName="+NYCIDuser+"&signature="+signature;
		   // String url = NYCIDdomain+"getUsers.htm?guids="+URLEncoder.encode(guids,CHAR_SET)+"&userName="+URLEncoder.encode(NYCIDuser,CHAR_SET)+"&signature="+URLEncoder.encode(signature,CHAR_SET);
		    String url = NYCIDdomain+"getUsers.htm?guids="+guids+"&userName="+URLEncoder.encode(NYCIDuser,CHAR_SET)+"&signature="+URLEncoder.encode(signature,CHAR_SET);
		    
		    LOG_OBJECT.Info("url :: "+ url);
		    
		    HttpHeaders headers = new HttpHeaders();
		    headers.setContentType(MediaType.APPLICATION_JSON);
	        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	        HttpEntity<String> entity = new HttpEntity<String>("", headers);
	               
	        ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
	        LOG_OBJECT.Info("\nSTATUS:: \n"+result.getStatusCode());
	        	    
	        return result.getBody();
		    
		}
		catch (HttpClientErrorException ec)
		{
			LOG_OBJECT.Error("HttpClientErrorException occurred while getUsersByGuids " + ec);
			LOG_OBJECT.Error("      Response body: " + ec.getResponseBodyAsString());
			return  ec.getResponseBodyAsString();
		}
		catch (Exception ex)
		{
			LOG_OBJECT.Error("Exception occurred while getUsersByGuids", ex);
			return ex.getMessage();
		}
	
	}
}	


	
