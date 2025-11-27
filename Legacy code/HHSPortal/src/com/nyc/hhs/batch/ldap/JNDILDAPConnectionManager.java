package com.nyc.hhs.batch.ldap;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.util.PropertyLoader;

public class JNDILDAPConnectionManager
{

	private static final LogInfo LOG_OBJECT = new LogInfo(JNDILDAPConnectionManager.class);

	/**
	 * This method used to get the DirContext through the JNDI API
	 * @return DirContext
	 * @throws NamingException
	 * @throws ApplicationException
	 */
	public DirContext getLDAPDirContext() throws NamingException, ApplicationException
	{
		InitialDirContext loInitialDirContext = null;
		try
		{
			String lsProviderURL = PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "LDAP_HOST") + ":"
					+ PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "LDAP_CONNECTION_PORT");

			// Assign the JNDI environment values in Map
			final Hashtable<String, String> loEnvValues = new Hashtable();

			/* This variable contains the value of JNDI Initial context factory */
			loEnvValues.put(Context.INITIAL_CONTEXT_FACTORY,
					PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "INITIAL_CONTEXT_FACTORY"));
			/* This variable contains the value of JNDI LDAP URL */
			loEnvValues.put(Context.PROVIDER_URL, lsProviderURL);

			/*
			 * This variable contains the value of JNDI LDAP Security
			 * authentication
			 */
			loEnvValues.put(Context.SECURITY_AUTHENTICATION,
					PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "SECURITY_AUTHENTICATION"));
			/* This variable contains the value of LDAP login */
			loEnvValues.put(Context.SECURITY_PRINCIPAL,
					PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "LDAP_LOGIN_DN"));
			/* This variable contains the value of LDAP password */
			loEnvValues.put(Context.SECURITY_CREDENTIALS,
					PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "LDAP_ADMIN_PASSWORD"));
			loInitialDirContext = new InitialDirContext(loEnvValues);
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Application Exception occurred while executing JNDILDAPConnectionManager", aoExp);
			throw aoExp;

		}
		return loInitialDirContext;

	}

	public void disConnectLDAPConnection(DirContext dirCtx) throws NamingException
	{
		try
		{
			if (null != dirCtx)
			{
				dirCtx.close();
			}
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Application Exception occurred while executing JNDILDAPConnectionManager", aoExp);
		}
	}

}