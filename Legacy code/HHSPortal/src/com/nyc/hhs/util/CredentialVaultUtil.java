package com.nyc.hhs.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.bea.p13n.security.management.credentials.CredentialEntry;
import com.bea.p13n.security.management.credentials.CredentialEntry.EntryType;
import com.bea.p13n.security.management.credentials.CredentialVaultService;
import com.bea.p13n.security.management.credentials.ResourceKey;
import com.bea.p13n.security.management.credentials.Scope;
import com.bea.p13n.security.management.credentials.UserPasswordCredential;
import com.nyc.hhs.exception.ApplicationException;

/**
 * This class is used to get the credentials.
 * 
 */
public class CredentialVaultUtil {

	/**
	 * This method gets the credential.
	 * 
	 * @param asCredentialKey - Credential Key
	 * @return - loLCredential
	 * @throws ApplicationException
	 */
	public static List<String> getCredential(String asCredentialKey) throws ApplicationException{

		List<String> loLCredential = null;	
		try {

			CredentialVaultService loCVS = com.bea.wlp.services.Services.getService(com.bea.p13n.security.management.credentials.CredentialVaultService.class);
			String lsDummyId = "dummy";
			// this case use enterprise scope, so all portal web can see this entry, but not another enterpise app target to external users.
			ResourceKey loResourceKey = new ResourceKey(Scope.getApplicationScope(), lsDummyId);
			CredentialEntry loCredentialEntry = loCVS.fetchCredentialEntry(asCredentialKey, EntryType.SYSTEM_TYPE, loResourceKey);
			UserPasswordCredential loCredential = (UserPasswordCredential)loCredentialEntry.getCredential();

			String lsUsername = loCredential.getPrincipalName();
			String lsPassword = new String(new String(loCredential.getPrincipalPassword()).getBytes(), "UTF-8");
			loLCredential = new ArrayList<String>();
			loLCredential.add(lsUsername);
			loLCredential.add(lsPassword);
		} catch (NullPointerException aoException) {
			throw new ApplicationException("Not able to retrive user name and password from Credential vault for "+asCredentialKey, aoException);
		}
		catch (UnsupportedEncodingException aoUnsupportedExp) {
			throw new ApplicationException("Not able to retrive user name and password from Credential vault for "+asCredentialKey, aoUnsupportedExp);
		}

		return loLCredential;
	}
}
