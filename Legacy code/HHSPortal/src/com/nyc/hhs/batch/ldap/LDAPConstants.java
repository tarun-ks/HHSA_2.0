package com.nyc.hhs.batch.ldap;

public class LDAPConstants {

	public static final String STRING_ZERO = "0";
	public static final int INTEGER_ZERO = 0;
	public static final String STRING_ONE = "1";
	public static final int INTEGER_ONE = 1;
	public static final String ACTIVE_FLAG = "activeFlag";
	public static final String HASH_MAP_VAR = "loHMap";

	public static final String CITY_USER_DETAILS = "cityUserDetails";
	public static final String COMMA = ",";
	public static final String CITY = "city";
	public static final String BLANK_STRING = "";
	public static final String USER_DN = "USER_DN";
	public static final String UPDATE_COUNT = "liupdateCount";
	
	public static final String YES = "yes";
	public static final String NO = "no";
	public static final String SKIP = "skip";
	public static final String GROUP_MEMBERSHIP = "groupMembership";
	public static final String USER_CN = "cn=";
	public static final String USER_OU = "ou=";
	public static final String FETCH_CITY_USERS_DETAILS_FOR_BATCH = "fetchCityUserDetailsForBatch";
	public static final String UPDATE_USERDN_IN_CITY_USER_DETAILS = "updateUserDNCityUserDetailsForBatch";
	public static final String FETCH_AGENCY_ID_FOR_LDAP_BATCH = "fetchAgencyIdforldapBatch";
	public static final String CITY_HHSA_I_MANAGER = "hhsa_i_manager";
	public static final String CITY_HHSA_I_EXEC_ADMIN = "hhsa_i_execAdmin";
	public static final String CITY_HHSA_I_STAFF = "hhsa_i_staff";
	public static final String AGENCY_NAME_PREFIX = "hhsa_i_agency_";
	public static final String AGENCY_CITY_LDAP_GROUPS = "allNYCAgencyDetailsIDOutput";
	public static final String GROUP_FILTER = "(&(objectClass=group)(cn=";
	public static final String DOUBLE_BRACKET ="))";
	public static final String MEMBER_ATTRIBUTE="member";
	public static final String MAIL_ATTRIBUTE="mail";
	public static final String GIVEN_NAME_ATTRIBUTE="givenname";
	public static final String SN_ATTRIBUTE="sn";

}