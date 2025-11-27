package com.nyc.hhs.service.filenetmanager.p8dataprovider;

import java.util.HashMap;

import javax.security.auth.Subject;

import com.filenet.api.core.Connection;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.util.UserContext;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8services.P8HelperServices;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;

import filenet.vw.api.VWSession;

/**
 * This call is generally used to validate the user in P8 server and establish
 * connection with P8 engine to carry out the operations
 * 
 */

public class P8SecurityOperations extends P8HelperServices
{

	private static final LogInfo LOG_OBJECT = new LogInfo(P8SecurityOperations.class);

	/**
	 * This Method is used for fetching objectStore.
	 * 
	 * @param aoUserSession P8 user session object
	 * @return ObjectStore active filenet object-store object
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public ObjectStore getObjectStore(P8UserSession aoUserSession) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered P8SecurityOperations.getObjectStore()");

		ObjectStore loObjectStore = null;
		HashMap loHmRequiredProp = new HashMap();
		String lsObjectStoreName = aoUserSession.getObjectStoreName();
		loHmRequiredProp.put("aoUserSession", aoUserSession);

        if(aoUserSession != null) {
            LOG_OBJECT.Debug("##########################TRACE[getObjectStore]" );
        }else{
            LOG_OBJECT.Debug("##########################TRACE[getObjectStore]loUserSession is NULL" );
        }
		
		if (lsObjectStoreName == null || lsObjectStoreName.equalsIgnoreCase(""))
		{
			ApplicationException loAppex = new ApplicationException(
					"Runtime Error in Fetching Filenet CE Connection.Error in getObjectStore Method. FileNet ObjectStore name is missing");
			loAppex.setContextData(loHmRequiredProp);
			LOG_OBJECT
					.Error("Runtime Error in Fetching Filenet CE Connection.Error in getObjectStore Method. FileNet ObjectStore name is missing");
			throw loAppex;
		}

		try
		{
			
           // Start QC 8998 R 8.8  remove password from logs
			String param = CommonUtil.maskPassword(aoUserSession);
			            	            
			LOG_OBJECT.Info("Start Getting getDomain(aoUserSession); "+ param); // aoUserSession);
			// Creating FileNet Connection and retrieving domain
			Domain loDomain = getDomain(aoUserSession);
			LOG_OBJECT.Info("Connexted after getDomain(aoUserSession); "+ param); // aoUserSession);
			// End QC 8998 R 8.8  remove password from logs

			// Not able to find object store from name. Fetching the new
			// instance from Factory class.
			if (loObjectStore == null)
			{
				loObjectStore = Factory.ObjectStore.fetchInstance(loDomain, lsObjectStoreName, null);
			}

		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmRequiredProp);
			LOG_OBJECT.Error("Exception in P8SecurityOperations.getObjectStore", aoAppex);
			throw aoAppex;
		}
		catch (EngineRuntimeException aoE)
		{
			ApplicationException loAppex = new ApplicationException(
					"Runtime Error in Fetching Filenet CE Connection.Filenet CE Service Might not be running.Please contact Admin.",
					aoE);
			loAppex.setContextData(loHmRequiredProp);
			LOG_OBJECT.Error("Exception in P8SecurityOperations.getObjectStore", aoE);
			throw loAppex;

		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Runtime Error in Fetching Filenet CE Connection.Filenet CE Service Might not be running.Please contact Admin. :",
					aoEx);
			loAppex.setContextData(loHmRequiredProp);
			LOG_OBJECT.Error("Exception in P8SecurityOperations.getObjectStore", aoEx);
		}

		LOG_OBJECT.Debug("Exited P8SecurityOperations.getObjectStore()");
		return loObjectStore;
	}

	/**
	 * This internal method is used for creating FileNet connection and
	 * retrieving Domain object.
	 * 
	 * @param aoUserSession P8 user session object
	 * @return Domain an object of type Domain
	 * @throws ApplicationException
	 */
	private Domain getDomain(P8UserSession aoUserSession) throws ApplicationException
	{

		LOG_OBJECT.Debug("Entered P8SecurityOperations.getDomain()");

		Domain loDomain = null;

		HashMap<String, String> loHmReqExceProp = new HashMap<String, String>();
		loHmReqExceProp.put("ContentEngineUri", aoUserSession.getContentEngineUri());
		loHmReqExceProp.put("userName", aoUserSession.getUserId());

		Subject loSubject = null;
		UserContext loUC = UserContext.get();
		Connection loConn = null;

		
		
        if(aoUserSession != null) {
            LOG_OBJECT.Debug("##########################TRACE[getSharedDocumentsOwnerList]" );
        }else{
            LOG_OBJECT.Debug("##########################TRACE[getSharedDocumentsOwnerList]loUserSession is NULL" );
        }



		try
		{
			if (aoUserSession.getContentEngineUri().equalsIgnoreCase(""))
			{
				ApplicationException loAppex = new ApplicationException(
						"Runtime Error in Fetching Filenet CE Connection.FileNet Content Engine URI is missing");
				loAppex.setContextData(loHmReqExceProp);
				LOG_OBJECT
						.Error("Runtime Error in Fetching Filenet CE Connection.Error in getObjectStore Method. FileNet Content Engine URI is missing");
				throw loAppex;
			}

			if (aoUserSession.getConnection() == null)
			{
				loConn = Factory.Connection.getConnection(aoUserSession.getContentEngineUri());
				aoUserSession.setConnection(loConn);
			}

			if (aoUserSession.getSubject() == null)
			{

				if (aoUserSession.getUserId().equalsIgnoreCase("") || aoUserSession.getPassword().equalsIgnoreCase(""))
				{
					ApplicationException loAppex = new ApplicationException(
							"Runtime Error in Fetching Filenet CE Connection.FileNet Credentials are missing");
					LOG_OBJECT.Debug("Runtime Error in Fetching Filenet CE Connection.FileNet Credentials are missing");
					loAppex.setContextData(loHmReqExceProp);
					throw loAppex;
				}

				// Creating new Subject for the user using FileNet connection
				// object
				loSubject = UserContext.createSubject(loConn, aoUserSession.getUserId(), aoUserSession.getPassword(),
						null);

				// Pushing the subject in UserContext
				loUC.pushSubject(loSubject);
				aoUserSession.setSubject(loSubject);

			}
			else
			{
				loUC.pushSubject(aoUserSession.getSubject());
			}

			loDomain = Factory.Domain.getInstance(aoUserSession.getConnection(), null);// entireNetwork.get_LocalDomain();

		}
		catch (ApplicationException aoAppex)
		{

			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8SecurityOperations.getDomain()", aoAppex);
			throw aoAppex;
		}
		catch (EngineRuntimeException aoE)
		{
			ApplicationException loAppex = new ApplicationException(
					"Runtime Error in Fetching Filenet CE Connection.Not able to Generate Connection Object", aoE);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8SecurityOperations.getDomain()", aoE);
			throw loAppex;

		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Runtime Error in Fetching Filenet CE Connection.Filenet CE Service Might not be running.Please contact Admin. :",
					aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8SecurityOperations.getDomain()", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Debug("Exited P8SecurityOperations.getDomain()");
		return loDomain;
	}

	/**
	 * This method is used for retrieving FileNet PE Session based on user
	 * credentials provided in user bean
	 * 
	 * @param aoUserSession P8 user session object
	 * @return a valid VWSession object
	 * @throws ApplicationException
	 */
	/*
	 * 
	 * Original code
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public VWSession getPESession(P8UserSession aoUserSession) throws ApplicationException
	{

		LOG_OBJECT.Debug("Entered P8SecurityOperations.getPESession()");

		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("ContentEngineUri", aoUserSession.getContentEngineUri());
		loHmReqExceProp.put("userID", aoUserSession.getUserId());
		loHmReqExceProp.put("IsolatedRegionName", aoUserSession.getIsolatedRegionName());

		try
		{
			getDomain(aoUserSession);

			if (null != aoUserSession.getVwsession())
			{
				VWSession loVWSession = aoUserSession.getVwsession();
				if (!loVWSession.isLoggedOn())
				{
					loVWSession.logon(aoUserSession.getIsolatedRegionName());
				}
				return loVWSession;
			}
			else
			{
				// Fetching Content URL from user bean and setting the value
				VWSession loVWSession = new VWSession(aoUserSession.getIsolatedRegionName());
				aoUserSession.setVwsession(loVWSession);
				LOG_OBJECT.Debug("Exited P8SecurityOperations.getPESession()");
				return loVWSession;
			}

		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8SecurityOperations.getPESession()", aoAppex);
			throw aoAppex;
		}

		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Runtime Error in Fetching Filenet PE Connection.Filenet PE Service Might not be running.Please contact Admin.",
					aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8SecurityOperations.getPESession()", aoEx);
			throw loAppex;
		}
	}

	/**
	 * This method is used to establish the filenet connection using an
	 * authenticated user details.
	 * 
	 * @param aoUserSession P8 user session object
	 * @return P8UserSession P8 user session object
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public P8UserSession getFileNetConnection(P8UserSession aoUserSession) throws ApplicationException
	{

		LOG_OBJECT.Debug("Entered P8SecurityOperations.getFileNetConnection()");

		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("usersesion", aoUserSession.toString());

		try
		{
			if (aoUserSession.getContentEngineUri().equalsIgnoreCase(""))
			{
				ApplicationException loAppex = new ApplicationException(
						"Runtime Error in Fetching Filenet CE Connection.FileNet Content Engine URI is missing");
				loAppex.setContextData(loHmReqExceProp);
				LOG_OBJECT
						.Error("Runtime Error in Fetching Filenet CE Connection.FileNet Content Engine URI is missing");
				throw loAppex;
			}

			getDomain(aoUserSession);
		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8SecurityOperations.getFileNetConnection()", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Runtime Error in Fetching Filenet CE Connection.Filenet PE Service Might not be running.Please contact Admin.",
					aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8SecurityOperations.getFileNetConnection()", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Debug("Exited P8SecurityOperations.getFileNetConnection()");
		return aoUserSession;
	}

	/**
	 * This method is used to set subject in P8UserSession
	 * 
	 * @param aoUserSession P8 user session object
	 * @throws ApplicationException
	 */
	public void popSubject(P8UserSession aoUserSession) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered P8SecurityOperations.popSubject()");

		UserContext loUC = UserContext.get();
		Subject loSubject = loUC.popSubject();
		aoUserSession.setSubject(loSubject);

		LOG_OBJECT.Debug("Exited P8SecurityOperations.popSubject()");
	}
	/**
	 * This method is used to set P8 Session variables
	 * @return loUserSession
	 * @throws ApplicationException
	 */
	public P8UserSession setP8SessionVariables() throws ApplicationException
	{
		P8UserSession loUserSession = new P8UserSession();
		loUserSession.setContentEngineUri(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "FILENET_URI"));
		loUserSession.setObjectStoreName(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "OBJECT_STORE_NAME"));
		loUserSession.setIsolatedRegionName(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
				"CONNECTION_POINT_NAME"));
		loUserSession.setIsolatedRegionNumber(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
				"CONNECTION_POINT_NUMBER"));
		loUserSession.setUserId(HHSUtil.decryptASEString(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
				"CE_USER_ID")));
		loUserSession.setPassword(HHSUtil.decryptASEString(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
				"CE_PASSWORD")));
		return loUserSession;
	}

}
