package com.nyc.hhs.webservice.restful;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpClientError;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;

public class NYCIDWebServices {

	private final static LogInfo LOG_OBJECT = new LogInfo(NYCIDWebServices.class);
	private String NYCIDdomain = "";
	private String NYCIDpassword = "";
	private String NYCIDuser = "";
	private String CHAR_SET = "UTF-8";
	private static String PROXY_HOST = "";
	private static String PROXY_PORT = "";

	public NYCIDWebServices() {

		try {
			NYCIDdomain = PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE, ApplicationConstants.SAML_NYCID_WEB_SERVICE_URI);
			NYCIDpassword = PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE, ApplicationConstants.SAML_NYCID_WEB_SERVICE_PASSWORD);
			NYCIDuser = PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE, ApplicationConstants.SAML_NYCID_WEB_SERVICE_USER);
			LOG_OBJECT.Info("NYCIDdomain :: " + NYCIDdomain);
			LOG_OBJECT.Info("NYCIDuser :: " + NYCIDuser);
			// LOG_OBJECT.Info("NYCIDpassword :: "+ NYCIDpassword); QC 8998 R 8.8 do not show password

		} catch (Exception e) {
			LOG_OBJECT.Error("Exception occurred while initialization", e);
		}

	}

	static {
		try {
			PROXY_HOST = PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE, "PROXY_HOST");
			PROXY_PORT = PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE, "PROXY_PORT");
			LOG_OBJECT.Info("PROXY_HOST :: " + PROXY_HOST);
			LOG_OBJECT.Info("PROXY_PORT :: " + PROXY_PORT);
		} catch (Exception e) {
			LOG_OBJECT.Error("Exception occurred while static initialization", e);
		}
	}

	/**
	 * The Authenticate Web Service provides the application with the ability to authenticate a user without using SAML
	 * @param email - required
	 * @param password
	 *            - required
	 * @param date
	 *            Time (MM/dd/yyyy HH:mm or M/d/yy HH:mm) - optional
	 * @param NYC
	 *            .ID userName - required
	 * @param signature
	 *            required
	 * @return response in json format
	 * @return String lsReturnMessage returns corresponding message whether user
	 *         is authenticated {"authenticated":true}
	 * @returns http status code 200 - ok, 400 - bed request, 401 - unauthorized
	 * @throws Exception
	 */

	public String authenticateUser(String asEmail, String asPassword) {
		
		//[Start] R9.6.6 qc 9711 --NYCID WebServices need to be updated to Post Method
		//final String forSignature = "GET/account/api/authenticate.htm";
		
		final String forSignature = "POST/account/api/authenticate.htm";

		try {
			LOG_OBJECT.Info("Begin post Authenticate WS request!");
			String result = "";
			// HttpClient loClient = new HttpClient();
			HttpClient loClient = getHttpClient();

			StringBuilder stringToSign = new StringBuilder();
			stringToSign.append(forSignature);
			stringToSign.append(asEmail);
			stringToSign.append(asPassword);
			stringToSign.append(NYCIDuser);
			String signature = HHSUtil.getSignature(stringToSign.toString(), NYCIDpassword);
			LOG_OBJECT.Info("signature :: " + signature);
			// construct url
			
			/* code before fix for QC9711		
			String url = NYCIDdomain + "authenticate.htm?email=" + URLEncoder.encode(asEmail, CHAR_SET) + "&password=" + URLEncoder.encode(asPassword, CHAR_SET) + "&userName=" + URLEncoder.encode(NYCIDuser, CHAR_SET) + "&signature=" + URLEncoder.encode(signature, CHAR_SET);
			String logurl = NYCIDdomain + "authenticate.htm?email=" + URLEncoder.encode(asEmail, CHAR_SET) + "&password=********" + "&userName=" + URLEncoder.encode(NYCIDuser, CHAR_SET) + "&signature=" + URLEncoder.encode(signature, CHAR_SET);
			LOG_OBJECT.Info("logurl :: " + logurl);
			HttpMethod method = new GetMethod(url);
			*/
			
			PostMethod method = new PostMethod(NYCIDdomain + "authenticate.htm");	
			
			NameValuePair[] data = {
			          new NameValuePair("email", asEmail),
			          new NameValuePair("password", asPassword),
			          new NameValuePair("userName", NYCIDuser),
			          new NameValuePair("signature", signature)
			        };
			
			method.setRequestBody(data);	
			//method.setRequestHeader("Content-Type", "application/vnd.nyc.v2");//remove it
			method.setRequestHeader("Accept", "application/vnd.nyc.v2");
			//[End] R9.6.6 qc 9711 --NYCID WebServices need to be updated to Post Method
			
			int returnCode = loClient.executeMethod(method);
			LOG_OBJECT.Info("After executing authentication method :: returnCode :: " + returnCode);
			LOG_OBJECT.Info("After executing authentication method :: Response   :: " + method.getResponseBodyAsString());

			result = method.getResponseBodyAsString();
			LOG_OBJECT.Info("\nRESULT:: \n" + result);
			// Thread.sleep(1000);
			return result;

		} catch (HttpException ec) {
			LOG_OBJECT.Error("HttpClientErrorException occurred while autthenticateUser " + ec);
			LOG_OBJECT.Error("Response body: " + ec.getMessage());
			return ec.getMessage();
		} catch (Exception ex) {
			LOG_OBJECT.Error("Exception occurred while authentication", ex);
			return ex.getMessage();
		}

	}

	/**
	 * The Web Service Email validation is the process of confirming that a user
	 * owns the email address he or she registered with NYC.ID.
	 * 
	 * @param guid
	 *            - required
	 * @param date
	 *            Time (MM/dd/yyyy HH:mm or M/d/yy HH:mm) - optional
	 * @param NYC
	 *            .ID Account userName - required
	 * @param signature
	 *            required
	 * @return response in json format
	 * @return returns corresponding message whether users email is registered
	 *         {"validate":true}
	 * @returns http status code 200 - ok, 400 - bed request, 401 - unauthorized
	 * @throws Exception
	 */
	public String validateEmail(String asGuid) {
		// test environment
		final String forSignature = "GET/account/api/isEmailValidated.htm";
		try {
			System.out.println("Begin /GET Emai Validated request!");

			String result = "";
			// HttpClient loClient = new HttpClient();
			HttpClient loClient = getHttpClient();

			StringBuilder stringToSign = new StringBuilder();
			stringToSign.append(forSignature);
			stringToSign.append(asGuid);
			stringToSign.append(NYCIDuser);
			LOG_OBJECT.Info("stringToSign :: " + stringToSign);
			String signature = HHSUtil.getSignature(stringToSign.toString(), NYCIDpassword);
			LOG_OBJECT.Info("signature :: " + signature);
			// Construct url
			final String url = NYCIDdomain + "isEmailValidated.htm?guid=" + URLEncoder.encode(asGuid, CHAR_SET) + "&userName=" + URLEncoder.encode(NYCIDuser, CHAR_SET) + "&signature=" + URLEncoder.encode(signature, CHAR_SET);
			LOG_OBJECT.Info("url :: " + url);

			HttpMethod method = new GetMethod(url);
			method.setRequestHeader("Content-Type", "application/json"); // "text/json; charset=ISO-8859-1");
			int returnCode = loClient.executeMethod(method);
			LOG_OBJECT.Info("After executing validateEmail method :: returnCode :: " + returnCode);
			LOG_OBJECT.Info("After executing validateEmail method :: Response   :: " + method.getResponseBodyAsString());

			result = "ERRORS:code " + returnCode;
			if (returnCode == 200) {
				result = method.getResponseBodyAsString();
			}
			LOG_OBJECT.Info("\nRESULT:: \n" + result);
			// Thread.sleep(1000);
			return result;

		} catch (HttpClientError ec) {
			LOG_OBJECT.Error("HttpClientErrorException occurred while validateEmail " + ec);
			LOG_OBJECT.Error("      Response body: " + ec.getMessage());
			return ec.getMessage();
		} catch (Exception ex) {
			LOG_OBJECT.Error("Exception occurred while validateEmail :: ", ex);
			return ex.getMessage();

		}

	}

	/**
	 * The User Web Service provides the application with user's latest profile
	 * information
	 * 
	 * @param guid
	 *            - required
	 * @param email
	 *            - required
	 * @param date
	 *            Time (MM/dd/yyyy HH:mm or M/d/yy HH:mm) - optional
	 * @param NYC
	 *            .ID userName - required
	 * @param signature
	 *            required
	 * @return response in json format
	 * @return String lsReturnMessage returns user profile in JSON format
	 * @returns http status code 200 - ok, 400 - bed request, 401 - unauthorized
	 * @throws Exception
	 */
	public String getUser(String asGuid, String asEmail) {
		final String forSignature = "GET/account/api/user.htm";
		try {
			LOG_OBJECT.Info("Begin /GET User WS request!");

			String result = "";
			// HttpClient loClient = new HttpClient();
			HttpClient loClient = getHttpClient();

			StringBuilder stringToSign = new StringBuilder();
			stringToSign.append(forSignature);
			stringToSign.append(asEmail);
			stringToSign.append(asGuid);
			stringToSign.append(NYCIDuser);
			// test environment

			String signature = HHSUtil.getSignature(stringToSign.toString(), NYCIDpassword);
			LOG_OBJECT.Info("signature :: " + signature);
			// construct url
			String url = NYCIDdomain + "user.htm?guid=" + URLEncoder.encode(asGuid, CHAR_SET) + "&email=" + URLEncoder.encode(asEmail, CHAR_SET) + "&userName=" + URLEncoder.encode(NYCIDuser, CHAR_SET) + "&signature=" + URLEncoder.encode(signature, CHAR_SET);
			LOG_OBJECT.Info("url :: " + url);
			HttpMethod method = new GetMethod(url);
			method.setRequestHeader("Content-Type", "application/json"); // "text/json; charset=ISO-8859-1");
			int returnCode = loClient.executeMethod(method);
			LOG_OBJECT.Info("After executing getUser method :: returnCode :: " + returnCode);
			LOG_OBJECT.Info("After executing getUser method :: Response   :: " + method.getResponseBodyAsString());

			result = "ERRORS:code " + returnCode;
			if (returnCode == 200) {
				result = method.getResponseBodyAsString();
			}
			LOG_OBJECT.Info("\nRESULT:: \n" + result);
			// Thread.sleep(1000);
			return result;

		} catch (HttpClientError ec) {
			LOG_OBJECT.Error("HttpClientErrorException occurred while getUser " + ec);
			LOG_OBJECT.Error("      Response body: " + ec.getMessage());
			return ec.getMessage();
		} catch (Exception ex) {
			LOG_OBJECT.Error("Exception occurred while getUser", ex);
			return ex.getMessage();
		}

	}

	/**
	 * The User Web Service provides the application a list of users whose
	 * profile has been modified between a specified start date and optional end
	 * date
	 * 
	 * @param startDate
	 *            - dateTime (MM/dd/yyyy HH:mm or M/d/yy HH:mm) - Required, if
	 *            "guids" is not specified
	 * @param guid
	 *            - Required, if "startDate" is not specified
	 * @param endDate
	 *            - dateTime (MM/dd/yyyy HH:mm or M/d/yy HH:mm) - Optional,
	 *            defaults to the current date
	 * @param date
	 *            Time (MM/dd/yyyy HH:mm or M/d/yy HH:mm) - optional
	 * @param NYC
	 *            .ID userName - required
	 * @param signature
	 *            required
	 * @return response in json format
	 * @return String JSon array returns In the event that the list contains
	 *         more than 1,000 users, an error will be returned. You application
	 *         may need to make multiple requests with a smaller date range to
	 *         get the complete list of users that have been modified between
	 *         the original start date and end date
	 * @returns http status code 200 - ok, 400 - bed request, 401 - unauthorized
	 * @throws Exception
	 */
	public String getUsersByDateModified(String beginDate, String stopDate, Integer numberOfDays) {
		final String forSignature = "GET/account/api/getUsers.htm";
		int days = 1;
		DateFormat startDateFormat = new SimpleDateFormat("MM/dd/yyyy 00:00");
		DateFormat currentDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		Date tmpDate = new Date();
		if (numberOfDays != null && numberOfDays > 0) {
			days = numberOfDays;
		}
		String startDate = currentDateFormat.format(tmpDate);
		String endDate = currentDateFormat.format(tmpDate);
		try {
			LOG_OBJECT.Info("Begin /GET getUsersByDateModified WS request! startDate :: " + beginDate + " :: EndDate :: " + stopDate);
			if (null == beginDate || null == stopDate || beginDate.isEmpty() || stopDate.isEmpty() || currentDateFormat.parse(beginDate).after(currentDateFormat.parse(endDate)) || currentDateFormat.parse(stopDate).before(currentDateFormat.parse(beginDate)) || currentDateFormat.parse(stopDate).after(currentDateFormat.parse(endDate))) {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DATE, -days);
				startDate = startDateFormat.format(cal.getTime());
				beginDate = null;
			} else {
				startDate = beginDate;
			}

			if (null != beginDate && null != stopDate && !stopDate.isEmpty() && (currentDateFormat.parse(startDate)).before(currentDateFormat.parse(stopDate)) && (currentDateFormat.parse(stopDate)).before(currentDateFormat.parse(endDate))) {
				endDate = stopDate;
			}
			String result = "";
			// HttpClient loClient = new HttpClient();
			HttpClient loClient = getHttpClient();

			StringBuilder stringToSign = new StringBuilder();
			stringToSign.append(forSignature);
			stringToSign.append(endDate);
			stringToSign.append(startDate);
			stringToSign.append(NYCIDuser);

			String signature = HHSUtil.getSignature(stringToSign.toString(), NYCIDpassword);
			LOG_OBJECT.Info("signature :: " + signature);
			// construct url
			String url = NYCIDdomain + "getUsers.htm?startDate=" + URLEncoder.encode(startDate, CHAR_SET) + "&endDate=" + URLEncoder.encode(endDate, CHAR_SET) + "&userName=" + URLEncoder.encode(NYCIDuser, CHAR_SET) + "&signature=" + URLEncoder.encode(signature, CHAR_SET);
			LOG_OBJECT.Info("url :: " + url);
			HttpMethod method = new GetMethod(url);
			method.setRequestHeader("Content-Type", "application/json");
			int returnCode = loClient.executeMethod(method);
			LOG_OBJECT.Info("After executing getUsersByDateModified method :: returnCode :: " + returnCode);
			LOG_OBJECT.Info("After executing getUsersByDateModified method :: Response   :: " + method.getResponseBodyAsString());
			result = "ERRORS:code " + returnCode;
			if (returnCode == 200) {
				result = method.getResponseBodyAsString();
			}
			LOG_OBJECT.Info("\nRESULT:: \n" + result);
			// Thread.sleep(1000);
			return result;

		} catch (HttpClientError ec) {
			LOG_OBJECT.Error("HttpClientErrorException occurred while getUsersByDateModified " + ec);
			LOG_OBJECT.Error("      Response body: " + ec.getMessage());
			return ec.getMessage();
		} catch (Exception ex) {
			LOG_OBJECT.Error("Exception occurred while getUsersByDateModified", ex);
			return ex.getMessage();
		}

	}

	/**
	 * The User Web Service provides the application a list of users whose
	 * profile has been modified between a specified start date and optional end
	 * date
	 * 
	 * @param startDate
	 *            - dateTime (MM/dd/yyyy HH:mm or M/d/yy HH:mm) - Required, if
	 *            "guids" is not specified
	 * @param guid
	 *            - Required, if "startDate" is not specified
	 * @param endDate
	 *            - dateTime (MM/dd/yyyy HH:mm or M/d/yy HH:mm) - Optional,
	 *            defaults to the current date
	 * @param date
	 *            Time (MM/dd/yyyy HH:mm or M/d/yy HH:mm) - optional
	 * @param NYC
	 *            .ID userName - required
	 * @param signature
	 *            required
	 * @return response in json format
	 * @return String JSon array returns In the event that the list contains
	 *         more than 1,000 users, an error will be returned. You application
	 *         may need to make multiple requests with a smaller date range to
	 *         get the complete list of users that have been modified between
	 *         the original start date and end date
	 * @returns http status code 200 - ok, 400 - bed request, 401 - unauthorized
	 * @throws Exception
	 */
	public String getUsersByGuids(String guids) {
		final String forSignature = "GET/account/api/getUsers.htm";
		try {
			LOG_OBJECT.Info("Begin /GET getUsers WS request! guids :: " + guids);

			String result = "";
			// HttpClient loClient = new HttpClient();
			HttpClient loClient = getHttpClient();

			StringBuilder stringToSign = new StringBuilder();
			stringToSign.append(forSignature);
			stringToSign.append(guids);
			stringToSign.append(NYCIDuser);
			// test environment

			String signature = HHSUtil.getSignature(stringToSign.toString(), NYCIDpassword);
			LOG_OBJECT.Info("signature :: " + signature);
			// construct url
			String url = NYCIDdomain + "getUsers.htm?guids=" + URLEncoder.encode(guids, CHAR_SET) + "&userName=" + URLEncoder.encode(NYCIDuser, CHAR_SET) + "&signature=" + URLEncoder.encode(signature, CHAR_SET);
			LOG_OBJECT.Info("url :: " + url);
			HttpMethod method = new GetMethod(url);
			method.setRequestHeader("Content-Type", "application/json"); // "text/json; charset=ISO-8859-1");
			int returnCode = loClient.executeMethod(method);
			LOG_OBJECT.Info("After executing getUsersByGuids method :: returnCode :: " + returnCode);
			LOG_OBJECT.Info("After executing getUsersByGuids method :: Response   :: " + method.getResponseBodyAsString());

			result = "ERRORS:code " + returnCode;
			if (returnCode == 200) {
				result = method.getResponseBodyAsString();
			}
			LOG_OBJECT.Info("\nRESULT:: \n" + result);
			// Thread.sleep(1000);
			return result;

		} catch (HttpClientError ec) {
			LOG_OBJECT.Error("HttpClientErrorException occurred while getUsersByGuids " + ec);
			LOG_OBJECT.Error("      Response body: " + ec.getMessage());
			return ec.getMessage();
		} catch (Exception ex) {
			LOG_OBJECT.Error("Exception occurred while getUsersByGuids", ex);
			return ex.getMessage();
		}

	}

	public HttpClient getHttpClient() {

		HttpClient loClient = new HttpClient();
		if (StringUtils.isNotBlank(PROXY_HOST) && StringUtils.isNotBlank(PROXY_PORT)) {
			loClient.getHostConfiguration().setProxy(PROXY_HOST, Integer.parseInt(PROXY_PORT));
		}
		return loClient;
	}

}
