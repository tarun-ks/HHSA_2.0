package com.nyc.hhs.webservice.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This class is used to call web services for the manage user Profile.
 * 
 */

public class ManageUserClient
{	
	private static final LogInfo LOG_OBJECT = new LogInfo(ManageUserClient.class);
	public static final int LOOP_COUNTER = 30;
	public static final int SLEEP_TIME = 2000;

	/**
	 * This method is used to change user password, when opted via update user profile  
	 * @param asUserDN UserDN of the User
	 * @param asCurrentpass current password
	 * @param asNewpass new password
	 * @return String lsReturnMessage returns corresponding message whether password is successfully changed or not
	 * @throws ApplicationException
	 * @throws IOException
	 */ 	
	public static String executeChangePassword(String asUserDN, String asCurrentpass, String asNewpass) throws ApplicationException,IOException
	{
		String lsText = "";
		String lsReturnMessage = "";
		OutputStream loPostBody =null;
		InputStream loInpStream = null;
		HttpURLConnection loPasswordURL = null;
		BufferedReader loReader = null;
		try
		{
			loPasswordURL = (HttpURLConnection) new URL((PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE,
					ApplicationConstants.PROPERTY_SERVICE_URL_IDM_ROA_PWDMGT) + asUserDN + "/password")).openConnection();
			loPasswordURL.setRequestMethod("POST");
			loPasswordURL.setRequestProperty("RESTAuthorization",
					new String(Base64.encodeBase64((asUserDN + ":" + asCurrentpass).getBytes("UTF-8")), "UTF-8"));
			loPasswordURL.setDoOutput(true);
			loPasswordURL.connect();
			StringBuilder loPostParameters = new StringBuilder();
			loPostParameters.append("oldPassword=" + asCurrentpass);
			loPostParameters.append("&");
			loPostParameters.append("newPassword=" + asNewpass);
			loPostParameters.append("&");
			loPostParameters.append("retypeNewPassword=" + asNewpass);
			loPostBody = loPasswordURL.getOutputStream();
			loPostBody.write(loPostParameters.toString().getBytes("UTF-8"));
			loInpStream = loPasswordURL.getInputStream();
			loReader = new BufferedReader(new InputStreamReader(loInpStream));
			lsText = loReader.readLine();
			if (null != lsText){
				if (lsText.contains("success_message"))
				{
					lsReturnMessage = "Your password has been changed successfully.";
				}
				else
				{
					int liStartIndex = lsText.indexOf(":\"");
					int liEndIndex = lsText.indexOf("\"}]");
					lsReturnMessage = lsText.substring(liStartIndex+2, liEndIndex);
				}
			}
		}
		catch (IOException lsIOEx)
		{
			LOG_OBJECT.Error("IOException occurred while changing password", lsIOEx);
			throw lsIOEx;
		}
		finally{
			if(loPostBody != null){
				loPostBody.close();	
			}
			if(loInpStream != null){
				loInpStream.close();
			}
			if (loPasswordURL != null){
				loPasswordURL.disconnect();
			}
			if (loReader != null){
				loReader.close();
			}
		}
		return lsReturnMessage;
	}

	/**
	 * This method is used to update/change new security questions, when opted via update user profile 
	 * @param asUserDN UserDN of the User
	 * @param asCurPass current password
	 * @param aoQuestion List containing new questions
	 * @param aoAnswers List containing new answers
	 * @return String lsText returns corresponding message when questions are updated
	 * @throws ApplicationException
	 * @throws IOException
	 */ 	
	@SuppressWarnings("rawtypes")
	public static String setChallengeQuestions(String asUserDN, String asCurPass, List aoQuestion, List aoAnswers) 
		   throws ApplicationException,IOException
	{
		String lsText = "";
		OutputStream loPostOutputStream = null;
		InputStream loInpStream = null;
		HttpURLConnection loPasswordURL = null;
		BufferedReader loReader = null;
		try
		{
			String lsAuthString = asUserDN + ":" + asCurPass;
			String lsB64encodedAuthString = new sun.misc.BASE64Encoder().encode(lsAuthString.getBytes());
			URL loRestURL = new URL((PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE,
					ApplicationConstants.PROPERTY_SERVICE_URL_IDM_ROA_PWDMGT) + asUserDN + "/chares"));

			loPasswordURL = (HttpURLConnection) loRestURL.openConnection();
			loPasswordURL.setRequestMethod("POST");
			loPasswordURL.setRequestProperty("RESTAuthorization", lsB64encodedAuthString);
			loPasswordURL.setDoOutput(true);
			loPasswordURL.connect();

			StringBuilder loPostBody = new StringBuilder();
			String lsQuestion0 = aoQuestion.get(0).toString();
			String lsQuestion1 = aoQuestion.get(1).toString();
			String lsQuestion2 = aoQuestion.get(2).toString();
			String lsAnswer0 = aoAnswers.get(0).toString();
			String lsAnswer1 = aoAnswers.get(1).toString();
			String lsAnswer2 = aoAnswers.get(2).toString();

			loPostBody.append("_question0=" + lsQuestion0);
			loPostBody.append("&");
			loPostBody.append("_question1=" + lsQuestion1);
			loPostBody.append("&");
			loPostBody.append("_question2=" + lsQuestion2);
			loPostBody.append("&");
			loPostBody.append("_answer0=" + lsAnswer0);
			loPostBody.append("&");
			loPostBody.append("_answer1=" + lsAnswer1);
			loPostBody.append("&");
			loPostBody.append("_answer2=" + lsAnswer2);

			loPostOutputStream = loPasswordURL.getOutputStream();
			loPostOutputStream.write(loPostBody.toString().getBytes());
			loInpStream = loPasswordURL.getInputStream();
			loReader = new BufferedReader(new InputStreamReader(loInpStream));
			lsText = loReader.readLine();
			loPasswordURL.disconnect();
		}
		catch (IOException lsIOEx)
		{
			LOG_OBJECT.Error("IOException occurred while changing password", lsIOEx);
			throw lsIOEx;
		}
		finally{
			if(loPostOutputStream != null){
				loPostOutputStream.close();	
			}
			if(loInpStream != null){
				loInpStream.close();
		    }
			if(loPasswordURL != null){
				loPasswordURL.disconnect();
			}
			if(loReader != null){
				loReader.close();
		    }
		}
		return lsText;
	}
}
