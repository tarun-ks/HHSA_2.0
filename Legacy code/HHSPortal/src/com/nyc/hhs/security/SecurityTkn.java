package com.nyc.hhs.security;

import java.util.HashMap;

public interface SecurityTkn {

    public String getOrgType();

    public String getUserDn();
    public String getNycId();
    public String getFirstName();
    public String getLastName();
    public String getMiddleName();
    public String getNycEmailValisationFlag();
   // [Start] QC 9205 R 8.0.0 SAML Internal
    public String getEntryDN();
    public String getGroupMembership();
    // [End] QC 9205 R 8.0.0 SAML Internal
    
    public boolean isNycIdValid();
    public boolean isProfileComplete();
    // R 8.0.0 QC 9205 SAML Internal
    public HashMap<Boolean,Integer> isGroupMembershipComplete();
    
    public String toString();
}
