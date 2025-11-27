package com.nyc.hhs.service.filenetmanager.p8services;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;

/**
 * 
 * This class is used to get the P8 session which later used to perform any kind
 * of P8 operations It extends P8HelperServices classes
 * 
 */

public class P8SecurityService extends P8HelperServices
{
	private static final LogInfo LOG_OBJECT = new LogInfo(P8SecurityService.class);
	
	/**
	 * This method is used to establish the filenet connection using an
	 * authenticated user details This calls getFileNetConnection method of
	 * P8SecurityOperations to establish the connection
	 * 
	 * @param aoP8UserSession
	 *            P8UserSession object
	 * @return P8UserSession P8UserSession object
	 * @throws ApplicationException
	 *             If an application exception occurred
	 */
	public P8UserSession getFileNetConnection(P8UserSession aoP8UserSession) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered P8SecurityService.getFileNetConnection()");
		try
		{
			filenetConnection.getFileNetConnection(aoP8UserSession);
		}
		catch (ApplicationException aoAppex)
		{
			LOG_OBJECT.Error("Error in  P8SecurityService.getFileNetConnection()", aoAppex);
			throw aoAppex;
		}

		LOG_OBJECT.Debug("Exiting P8SecurityService.getFileNetConnection()");
		return aoP8UserSession;
	}

	/**
	 * This method is used to set subject in P8UserSession
	 * 
	 * @param aoP8UserSession
	 *            P8UserSession object
	 * @throws ApplicationException
	 *             If an application exception occurred
	 */
	public void popSubject(P8UserSession aoP8UserSession) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered P8SecurityService.popSubject()");
		
		filenetConnection.popSubject(aoP8UserSession);
		
		LOG_OBJECT.Debug("Exited P8SecurityService.popSubject()");
	}
}
