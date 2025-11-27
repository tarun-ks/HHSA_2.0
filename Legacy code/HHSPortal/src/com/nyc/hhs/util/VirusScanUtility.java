package com.nyc.hhs.util;

import org.apache.commons.lang.StringUtils;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;

/**
 * This utility class provides the functionality related to virus scan which
 * includes connecting socket to ICAP server, disconnecting socket, receiving
 * response from ICAP server, and entering result codes.
 * 
 */

public class VirusScanUtility
{
	private static final LogInfo LOG_OBJECT = new LogInfo(VirusScanUtility.class);
	private static final String ENVIRONMENT = System.getProperty("hhs.env");
	private static String lsEnvironment="";
	protected byte[] mbBytes;
	private final java.text.DateFormat moDateFormat = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
	private java.io.BufferedReader moInput = null;
	private java.io.DataOutputStream moOutput = null;
	private long mlPreviewBytes = 30;
	public String msProfileName = "";

	public String msServerAddress = "";
	public int miServerPort;
	public String msServiceName = "";
	private java.net.Socket moSocket = null;
	private String msUserOrgType="";

	// Start R9.6.1 qc_9692 Update document anti-virus scanning connection point
	//since lower environments are not in DMZ, so for lower environments will go to Citynet ICAP server too
	static{
		lsEnvironment = ENVIRONMENT;
		if (lsEnvironment != null)
		{
			if (lsEnvironment.indexOf("_") > 0)
			{
				String lsEnvArray[] = lsEnvironment.split("_");
				lsEnvironment = lsEnvArray[0].trim();
			}
		}
	}
	private VirusScanUtility(){	}
	public VirusScanUtility(String msUserOrgType)
	{
		this.msUserOrgType=msUserOrgType;
		try
		{			
			setParams();
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error occurred while setting parameters in virus scanning utility", aoExp);
		}
	}
	// End R9.6.1 qc_9692 Update document anti-virus scanning connection point
	/**
	 * This method will establish a connection with server using parameters
	 * server address and server port
	 * 
	 * @param serverAddress a string value of server address
	 * @param serverPort a string value of server port
	 * @return an integer value indicating if connection is established or not
	 * @throws ApplicationException
	 */
	private int connect(String asServerAddress, int aiServerPort) throws ApplicationException
	{
		int liRetValue = 0;
		if (this.moSocket == null)
		{
			try
			{
				java.net.InetAddress liIPAddress = java.net.InetAddress.getByName(asServerAddress);
				this.moSocket = new java.net.Socket(liIPAddress, aiServerPort);
				this.moOutput = new java.io.DataOutputStream(this.moSocket.getOutputStream());
				this.moInput = new java.io.BufferedReader(new java.io.InputStreamReader(this.moSocket.getInputStream()));
			}
			catch (Exception aoExp)
			{
				liRetValue = -1;
				StringBuffer loErrorBuffer = new StringBuffer();
				loErrorBuffer.append("Error:connect(");
				loErrorBuffer.append(asServerAddress);
				loErrorBuffer.append(":");
				loErrorBuffer.append(aiServerPort);
				loErrorBuffer.append("):");
				loErrorBuffer.append(aoExp.getMessage());
				LOG_OBJECT.Error(loErrorBuffer.toString(), aoExp);
				throw new ApplicationException("Error occurred while scanning the documents", aoExp);
			}
		}
		return liRetValue;
	}

	/**
	 * This method will disconnect the connection with server after all virus
	 * processing
	 * 
	 * @throws ApplicationException
	 */
	private void disconnect() throws ApplicationException
	{
		try
		{
			this.moInput.close();
			this.moOutput.close();
			this.moSocket.close();
			this.moSocket = null;
		}
		catch (Exception aoExp)
		{
			StringBuffer loErrorBuffer = new StringBuffer();
			loErrorBuffer.append("Error:disconnect():");
			loErrorBuffer.append(aoExp.getMessage());
			LOG_OBJECT.Error(loErrorBuffer.toString(), aoExp);
			throw new ApplicationException("Error occurred while scanning the documents", aoExp);
		}
	}

	/**
	 * This method will return a string value of input stream after reading it
	 * till the end
	 * 
	 * @return a string value of input read line
	 * @throws ApplicationException
	 */
	private String receive() throws ApplicationException
	{
		String lsValue = "  ";
		StringBuffer loSbReturnString = new StringBuffer();
//		String[] lsResults = new String[1024];
//		int liArrayLocation = 0;
		try
		{
			while (null != lsValue && lsValue.length() != 0)
			{
				lsValue = this.moInput.readLine();
				if (null != lsValue)
				{
					loSbReturnString.append(lsValue);
					loSbReturnString.append("\n");
//					lsResults[liArrayLocation] = lsValue;
//					liArrayLocation++;
				}
			}
		}
		catch (Exception aoExp)
		{
			loSbReturnString.append("Error:receive():");
			loSbReturnString.append(aoExp.getMessage());
			LOG_OBJECT.Error(loSbReturnString.toString(), aoExp);
			throw new ApplicationException("Error occurred while scanning the documents", aoExp);
		}
		return loSbReturnString.toString();
	}

	/**
	 * This method will accept result string and return the result code based on
	 * input
	 * 
	 * @param returnString a string value of result String
	 * @return an integer value of result code
	 * @throws ApplicationException
	 */
	public int resultCode(String asReturnString) throws ApplicationException
	{
		int liRetValue = 0;
		if (asReturnString.startsWith("Error") || asReturnString.startsWith("Undefined") || "".equals(asReturnString))
		{
			liRetValue = -1;
		}

		if (asReturnString.startsWith("Infected") || asReturnString.startsWith("Blocked")
				&& (asReturnString.contains(":")))
		{

			try
			{
				String[] loResponses = asReturnString.split(":", 3);
				liRetValue = Integer.parseInt(loResponses[1]);
			}
			catch (Exception aoExp)
			{
				StringBuffer loReturnString = new StringBuffer();
				loReturnString.append("Error:scanfiletext:parsereturnString:");
				loReturnString.append(asReturnString);
				loReturnString.append(":");
				loReturnString.append(aoExp.getMessage());
				LOG_OBJECT.Error(loReturnString.toString(), aoExp);
				liRetValue = 1;
				throw new ApplicationException("Error occurred while scanning the documents", aoExp);
			}

		}

		if (asReturnString.startsWith("Clean"))
		{
			liRetValue = 0;
		}
		return liRetValue;
	}

	/**
	 * This method will get input string and write it on Output Stream
	 * 
	 * @param str a string value of input bytes
	 * @return an integer value indicating if writing to output stream is
	 *         successful
	 * @throws ApplicationException
	 */
	private int send(String asStr) throws ApplicationException
	{
		int liRetValue = 0;
		try
		{
			this.moOutput.writeBytes(asStr);
			this.moOutput.flush();
		}
		catch (Exception aoExp)
		{
			StringBuffer loSBReturnString = new StringBuffer();
			loSBReturnString.append("Error:send(");
			loSBReturnString.append(asStr);
			loSBReturnString.append("):");
			loSBReturnString.append(aoExp.getMessage());
			LOG_OBJECT.Error(loSBReturnString.toString(), aoExp);
			liRetValue = -1;
			throw new ApplicationException("Error occurred while scanning the documents", aoExp);
		}
		return liRetValue;
	}

	/**
	 * This method will set parameters for constructor
	 * 
	 * @param serverAddress a string value of server address
	 * @param serverPort an integer value of server port
	 * @param serviceName a string value of service name
	 * @param profileName a string value of profile name
	 * @param clientIP a string value of client IP
	 * @throws ApplicationException If an Application Exception occurs
	 */
	// Start R9.6.1 qc_9692 Update document anti-virus scanning connection point
	public void setParams() throws ApplicationException
	{
		LOG_OBJECT.Debug("msUserOrgType : "+ this.msUserOrgType + ",lsEnvironment:"+ lsEnvironment);
		
		 //since lower environments are not in DMZ, only prd provider site is in DMZ, so for lower environments will go to Citynet ICAP server too
		if ( "prd".equalsIgnoreCase(lsEnvironment) && StringUtils.isNotBlank(this.msUserOrgType) && 
				HHSConstants.PROVIDER_ORG.equalsIgnoreCase(this.msUserOrgType)
		){		
			this.msServerAddress = PropertyLoader.getProperty("com.nyc.hhs.properties.hhsservices", "SERVER_ADDRESS");
			this.miServerPort = Integer.valueOf(PropertyLoader.getProperty("com.nyc.hhs.properties.hhsservices",
					"SERVER_PORT"));
			this.msServiceName = PropertyLoader.getProperty("com.nyc.hhs.properties.hhsservices", "SERVICE_NAME");
			this.msProfileName = PropertyLoader.getProperty("com.nyc.hhs.properties.hhsservices", "PROFILE_NAME");
		}else{ // for internal site (or lower env) to use new Citynet server,add the prefix with "CITYNET." in the properties file
			this.msServerAddress = PropertyLoader.getProperty("com.nyc.hhs.properties.hhsservices", "CITYNET.SERVER_ADDRESS");
			this.miServerPort = Integer.valueOf(PropertyLoader.getProperty("com.nyc.hhs.properties.hhsservices",
					"CITYNET.SERVER_PORT"));
			this.msServiceName = PropertyLoader.getProperty("com.nyc.hhs.properties.hhsservices", "CITYNET.SERVICE_NAME");
			this.msProfileName = PropertyLoader.getProperty("com.nyc.hhs.properties.hhsservices", "CITYNET.PROFILE_NAME");
		}
	}
	// End R9.6.1 qc_9692 Update document anti-virus scanning connection point
	/**
	 * This method will scan the file and return the output result
	 * 
	 * @param aoIs an Input Stream object
	 * @param fileLen length of file
	 * @return a string value indication result
	 */
	public String scanFile(byte[] abBytes, long alFileLen, String asFileName, String asClientIp)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Inside Virus Scan Utility for Document scanning");
		int liRetValue = 0;
		String lsServerResponse = "";
		if (this.mlPreviewBytes > abBytes.length)
		{
			this.mlPreviewBytes = abBytes.length;
		}
		try
		{
			LOG_OBJECT.Debug("this.msServerAddress:" +this.msServerAddress +",this.miServerPort:"+this.miServerPort + ",this.msServiceName:" + this.msServiceName +",this.msProfileName:" +this.msProfileName);
			liRetValue = connect(this.msServerAddress, this.miServerPort);
			VirusScanResponse loIcapResponse = new VirusScanResponse();
			while (liRetValue == 0)
			{
				int liReqHeader = 0;
				int liResHeader;
				int liResBody;
				StringBuffer loRequestHeader = new StringBuffer();
				loRequestHeader.append("GET http://");
				loRequestHeader.append(asClientIp);
				loRequestHeader.append("/");
				loRequestHeader.append(moDateFormat.format(new java.util.Date()));
				loRequestHeader.append("/");
				loRequestHeader.append(asFileName);
				loRequestHeader.append(" HTTP/1.1\r\n");
				loRequestHeader.append("Host: ");
				loRequestHeader.append(asClientIp);
				loRequestHeader.append("\r\n\r\n");
				String lsResponseHeader = "HTTP/1.1 200 OK\r\n" + "Transfer-Encoding: chunked\r\n\r\n";
				liResHeader = loRequestHeader.length();
				liResBody = liResHeader + (lsResponseHeader.length());
				LOG_OBJECT.Debug(lsResponseHeader);
				String lsProfile = "";
				if (!"".equals(this.msProfileName))
				{
					lsProfile = "?profile=" + this.msProfileName;
				}
				StringBuffer loIcapRequest = new StringBuffer();
				createICAPRequest(liReqHeader, liResHeader, liResBody, lsProfile, loIcapRequest, asClientIp);
				liRetValue = send(loIcapRequest.toString());
				if (liRetValue == -1)
				{
					break;
				}
				liRetValue = send(loRequestHeader.toString());
				if (liRetValue == -1)
				{
					break;
				}
				liRetValue = send(lsResponseHeader);
				if (liRetValue == -1)
				{
					break;
				}
				loIcapRequest = new StringBuffer();
				loIcapRequest.append(Long.toHexString(this.mlPreviewBytes));
				loIcapRequest.append("\r\n");
				liRetValue = send(loIcapRequest.toString());
				if (liRetValue == -1)
				{
					break;
				}
				loIcapRequest = new StringBuffer();
				loIcapRequest.append("\r\n0\r\n\r\n");
				this.moOutput.write(abBytes, 0, (int) this.mlPreviewBytes);
				liRetValue = send(loIcapRequest.toString());
				if (liRetValue == -1)
				{
					break;
				}
				loIcapRequest = new StringBuffer();
				loIcapRequest.append(receive());
				LOG_OBJECT.Debug(loIcapRequest.toString());
				if ((loIcapResponse.continue_check(loIcapRequest.toString())) == true)
				{
					loIcapRequest = new StringBuffer();
					alFileLen = alFileLen - this.mlPreviewBytes;
					loIcapRequest.append(Long.toHexString(alFileLen));
					loIcapRequest.append("\r\n");

					liRetValue = send(loIcapRequest.toString());
					if (liRetValue == -1)
					{
						break;
					}
					this.moOutput.write(abBytes, (int) this.mlPreviewBytes, (int) alFileLen);
					liRetValue = send("\r\n0\r\n\r\n");
					this.moOutput.flush();
					LOG_OBJECT.Debug(this.moOutput.toString());
					if (liRetValue == -1)
					{
						break;
					}
					loIcapRequest = new StringBuffer();
					loIcapRequest.append(receive());
					lsServerResponse = loIcapResponse.request(loIcapRequest.toString());
				}
				else
				{
					lsServerResponse = loIcapResponse.request(loIcapRequest.toString());
				}
				liRetValue = 1;
			}
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error:scanfiletext:sending_preview:", aoExp);
			liRetValue = -1;
			throw new ApplicationException("Exception occured in VirusScanUtility: scanFile method:: ", aoExp);
		}
		finally
		{
			disconnect();
		}
		LOG_OBJECT.Debug("scanFile method" + ",asFileName:" +asFileName + ",lsServerResponse:"+ lsServerResponse);
		return lsServerResponse;
	}

	/**
	 * This method will create ICAP request
	 * 
	 * @param aiReqHeader an integer value of Request Header
	 * @param aiResHeader an integer value of Response Header
	 * @param aiResBody an integer value of Response body
	 * @param asProfile a string value Profile name
	 * @param sbIcapRequest a string buffer request object
	 */
	private void createICAPRequest(int aiReqHeader, int aiResHeader, int aiResBody, String asProfile,
			StringBuffer asbIcapRequest, String asClientIp)
	{
		asbIcapRequest.append("RESPMOD icap://");
		asbIcapRequest.append(this.msServerAddress);
		asbIcapRequest.append(":");
		asbIcapRequest.append(this.miServerPort);
		asbIcapRequest.append("/");
		asbIcapRequest.append(this.msServiceName);
		asbIcapRequest.append(asProfile);
		asbIcapRequest.append(" ICAP/1.0\r\n");
		asbIcapRequest.append("Allow: 204\r\n");
		asbIcapRequest.append("Encapsulated: req-hdr=");
		asbIcapRequest.append(aiReqHeader);
		asbIcapRequest.append(" res-hdr=");
		asbIcapRequest.append(aiResHeader);
		asbIcapRequest.append(" res-body=");
		asbIcapRequest.append(aiResBody);
		asbIcapRequest.append("\r\n");
		asbIcapRequest.append("Host: ");
		asbIcapRequest.append(this.msServerAddress);
		asbIcapRequest.append("\r\n");
		asbIcapRequest.append("Preview: ");
		asbIcapRequest.append(Long.toString(this.mlPreviewBytes));
		asbIcapRequest.append("\r\n");
		asbIcapRequest.append("User-Agent: JavaICAPClient\r\n");
		asbIcapRequest.append("X-Client-IP: ");
		asbIcapRequest.append(asClientIp);
		asbIcapRequest.append("\r\n\r\n");
	}
}
