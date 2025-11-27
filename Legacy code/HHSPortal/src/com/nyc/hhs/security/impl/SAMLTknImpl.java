package com.nyc.hhs.security.impl;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang.StringUtils;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.security.SecurityTkn;

public class SAMLTknImpl implements SecurityTkn{
    private String lmGUID = "";
    private String lmMail = "";
    private String lmGivenName = "";
    private String lmLastName = "";
    private String lmMiddleName = "";
    private String lmNycExtEmailValidationFlag = "";
    private String lmNycExtTOUVersion = "";
    private String lmUserType = "";

    private static  final SimpleDateFormat sdf = new SimpleDateFormat(HHSConstants.SECURITY_TIMESTAMP_FORMAT);

    private Timestamp logInTimestamp = null; 

    // [Start] QC 9205 R 8.0.0 SAML Internal
    private String lmEntryDN = "";
   	private String lmGroupMembership = "";
    // [End] QC 9205 R 8.0.0 SAML Internal
   	
    public SAMLTknImpl(){   super();    }

    public SAMLTknImpl(Map<String,String> map) {
        if(map != null && !map.isEmpty()){
            lmGUID                         = map.get(ApplicationConstants.SAML_ATTR_GUID_KEY             );
            lmMail                         = map.get(ApplicationConstants.SAML_ATTR_EMAIL_KEY            );
            lmGivenName                    = map.get(ApplicationConstants.SAML_ATTR_FIRST_NAME_KEY       );
            lmLastName                     = map.get(ApplicationConstants.SAML_ATTR_LAST_NAME_KEY        );
            String loMName = map.get(ApplicationConstants.SAML_ATTR_M_NAME_KEY        );
            if(loMName!= null && loMName.length() == 1 ){
                lmMiddleName = Character.toString(loMName.charAt(0)); 
            }
            lmNycExtEmailValidationFlag    = map.get(ApplicationConstants.SAML_ATTR_EMAIL_VALIDATION_KEY );
            lmNycExtTOUVersion             = map.get(ApplicationConstants.SAML_ATTR_VERSION_KEY          );
            lmUserType                     = map.get(ApplicationConstants.SAML_ATTR_USER_TYPE_KEY        );
            // [Start] QC 9205 R 8.0.0 SAML Internal
            // city user attributes
            lmEntryDN                      = map.get(ApplicationConstants.SAML_ATTR_ENTRY_DN );
            lmGroupMembership              = map.get(ApplicationConstants.SAML_ATTR_GROUP_MEMBERSHIP );
            
            /*  SAML attributes for Internal and External have the same names
            if(lmMail == null || lmMail.isEmpty() )
            {
            	lmMail = map.get(ApplicationConstants.SAML_ATTR_EMAIL) ; 
            }
            if(lmLastName == null || lmLastName.isEmpty() )
            {
            	lmLastName = map.get(ApplicationConstants.SAML_ATTR_LAST_NAME) ; 
            }
            if(lmGivenName == null || lmGivenName.isEmpty() )
            {
            	lmGivenName = map.get(ApplicationConstants.SAML_ATTR_FIRST_NAME) ; 
            }
            */
            if(lmEntryDN!= null && !lmEntryDN.isEmpty() )
            {
            	lmNycExtEmailValidationFlag = "true";
            }
         // [End] QC 9205 R 8.0.0 SAML Internal
        }

        logInTimestamp = new Timestamp(System.currentTimeMillis());
    }

    public String getLogInTimeStr(){
        if(logInTimestamp == null ){
            return "";
        }else{
            return logInTimestamp.toString();
        }
    }
    public long getLogInTimeSysMillis(){
        if(logInTimestamp == null ){ return 0; }
        else{ return logInTimestamp.getTime(); } 
    }

    public String getOrgType(){
        return HHSConstants.USER_AGENCY;
    }


    public String getLmGUID() {
        return lmGUID;
    }

    public String getLmMail() {
        return lmMail;
    }

    public String getLmGivenName() {
        return lmGivenName;
    }

    public String getLmLastName() {
        return lmLastName;
    }

    public String getLmMiddleName() {
        return lmMiddleName;
    }

    public String getLmNycExtEmailValidationFlag() {
        return lmNycExtEmailValidationFlag;
    }

    public String getLmNycExtTOUVersion() {
        return lmNycExtTOUVersion;
    }

    public String getLmUserType() {
        return lmUserType;
    }

    public Timestamp getLogInTimestamp() {
        return logInTimestamp;
    }

    public String getEntryDN() {
		return lmEntryDN;
	}
    
    public String getGroupMembership() {
		return lmGroupMembership;
	}

	

    public String toString(){

        return  "___________________________________________________"
                + "\n[GUID]" + lmGUID 
                + "\n[Email]" + lmMail                      
                + "\n[First Name]" + lmGivenName                 
                + "\n[Middle Name]" + lmMiddleName                
                + "\n[Last Name]" + lmLastName                  
                + "\n[Email Validation Flag]" + lmNycExtEmailValidationFlag 
                + "\n[lmNycExtTOUVersion]" + lmNycExtTOUVersion          
                + "\n[lmUserType]" + lmUserType 
                + "\n[lmEntryDN]" + lmEntryDN 
                + "\n[lmGroupMembership]" + lmGroupMembership
                ;         
    }


    @Override
    public String getUserDn() {
        return lmGUID;
    }

    @Override
    public String getNycId() {
        return lmMail;
    }

    @Override
    public String getFirstName() {
        return lmGivenName;
    }

    @Override
    public String getLastName() {
        return lmLastName ;
    }

    @Override
    public String getNycEmailValisationFlag() {
        return lmNycExtEmailValidationFlag;
    }

    @Override
    public String getMiddleName() {
        return lmMiddleName;
    }

    @Override
    public boolean isNycIdValid() {
        if (lmMail == null ) return false;

        try {
            InternetAddress emailAddr = new InternetAddress(lmMail);
            emailAddr.validate();
         } catch (AddressException ex) {
            return false;
         }
         //  QC 9528 R 8.5.0 Null Pointer Exception thrown when logging into Accelerator - add check for null
         //if( !lmNycExtEmailValidationFlag.equalsIgnoreCase(ApplicationConstants.TRUE) ){
         if( null == lmNycExtEmailValidationFlag || !ApplicationConstants.TRUE.equalsIgnoreCase(lmNycExtEmailValidationFlag) )
         {
             return false;
         }

         return true;
     }

    @Override
    public boolean isProfileComplete() {
        if( lmGivenName != null && lmGivenName.length() > 0
                && lmLastName != null && lmLastName.length() > 0  ){
            return true;
        }else {
            return false;
        }
    }
    // Start QC 9333 R 8.0.0 validate membership for City users only 
    @Override
    public HashMap<Boolean,Integer> isGroupMembershipComplete()
    {
    	HashMap<Boolean,Integer>  map = new HashMap<Boolean,Integer>();
    	// QC 9333 R 8.0.0 - validate only for City User, for Provider - always true 
    	if(lmUserType != null && (lmEntryDN == null || lmEntryDN.isEmpty()) )
    	{
        	map.put(true, 1); 
    		return map;
    	}
    	if(StringUtils.isBlank(lmGroupMembership))
    	{
    		map.put(false, 0);
    		return map;
    	}
    	if(StringUtils.isNotBlank(lmGroupMembership))
    	{
    		String[] arrOfStr = lmGroupMembership.split(",", -2); 
    		
    		int cnt = 0;
    	    for (String a : arrOfStr) 
    	    {	
    	    	System.out.println("====:: "+a);
    	    	if(a.contains("cn=hhsa_i"))
    	    		cnt++;
    	    }
    	    if(cnt==1)
    	    {
    	    	map.put(true, cnt);
    	    	return map;
    	    }
    	    else
    	    {
    	    	map.put(false, cnt);
    	    	return map;
    	    }
    	   
    	}
    	    	
		return map; 
    		
    }
 // End QC 9333 R 8.0.0 validate membership for City users only 

}
