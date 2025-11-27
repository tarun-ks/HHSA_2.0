package com.nyc.hhs.frameworks.filenetmanager;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This class is used for creating Filenet P8 User Session bean. It fetches
 * system required properties from the hhs.properties file.
 * 
 */

public class FilenetSessionFactory
{

	/**
	 * This method will set the user details to the session objects and return
	 * the same
	 * 
	 * @return loFilenetSession A bean which will have details about user
	 * @throws ApplicationException
	 */
	public P8UserSession getFilenetSession() throws ApplicationException
	{

		P8UserSession loFilenetSession = new P8UserSession();

		System.setProperty(P8Constants.PROP_FILE_JAVA_SECURITY_AUTH_LOGIN_CONFIG,
				PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, P8Constants.PROP_FILE_JAVA_SECURITY_AUTH_LOGIN_CONFIG));
		System.setProperty("filenet.pe.bootstrap.ceuri", PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "FILENET_URI"));
		System.setProperty(P8Constants.PROP_FILE_JAVA_NAMING_FACTORY_INITIAL,
				PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, P8Constants.PROP_FILE_JAVA_NAMING_FACTORY_INITIAL));
		loFilenetSession.setContentEngineUri(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "FILENET_URI"));
		loFilenetSession.setUserId(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "CE_USER_ID"));
		loFilenetSession.setPassword(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "CE_PASSWORD"));
		loFilenetSession.setObjectStoreName(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "OBJECT_STORE_NAME"));
		loFilenetSession.setIsolatedRegionName(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "CONNECTION_POINT_NAME"));

		return loFilenetSession;
	}
}
