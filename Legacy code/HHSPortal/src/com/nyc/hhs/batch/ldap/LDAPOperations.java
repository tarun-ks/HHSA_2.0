package com.nyc.hhs.batch.ldap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import com.nyc.hhs.batch.impl.LdapBatch;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.CityUserDetailsBeanForBatch;

public class LDAPOperations
{

	public static final int SUBTREE_SCOPE = SearchControls.SUBTREE_SCOPE;// OBJECT_SCOPE,
																			// ONELEVEL_SCOPE
	private static final LogInfo LOG_OBJECT = new LogInfo(LdapBatch.class);

	/**
	 * This method is used to search an entry in LDAP
	 * <ul>
	 * <li>
	 * Fetches User Atributes</li>
	 * </ul>
	 * @param aoDirContext - dir context
	 * @param asSearchFilter - search filter
	 * @param asSearchBase - search base
	 * @return Attributes
	 * @throws NamingException, ApplicationException Updated Method in R4
	 * 
	 * 
	 */
	private Attributes getUserAttributes(final DirContext aoDirContext, final String asSearchFilter,
			final String asSearchBase) throws NamingException, ApplicationException
	{

		SearchResult loSearchResult = null;
		final SearchControls loConstraints = new SearchControls();
		loConstraints.setSearchScope(SUBTREE_SCOPE);
		String loReturnedAttrs[] =
		{ LDAPConstants.GIVEN_NAME_ATTRIBUTE, LDAPConstants.SN_ATTRIBUTE, LDAPConstants.MAIL_ATTRIBUTE };
		loConstraints.setReturningAttributes(loReturnedAttrs);

		LOG_OBJECT.Debug("** Search Starts : " + System.currentTimeMillis());
		final NamingEnumeration<?> loSearchResults = aoDirContext.search(asSearchBase, asSearchFilter, loConstraints);
		LOG_OBJECT.Debug("** Search Ends : " + System.currentTimeMillis());
		if (loSearchResults != null)
		{
			while (loSearchResults.hasMore())
			{
				loSearchResult = (SearchResult) loSearchResults.next();
				LOG_OBJECT.Debug("fetching LDAP attributes for user :: " + loSearchResult.getName());
			}
		}
		else
		{
			LOG_OBJECT.Debug("user attributes are not present ..");
		}
		Attributes loAttribute = null;
		if (null != loSearchResult)
		{
			loAttribute = loSearchResult.getAttributes();
		}
		return loAttribute;
	}

	/**
	 * This method is used to get the attribute values in LDAP
	 * <ul>
	 * <li>Set UserBean</li>
	 * <li>Call the method to update User Bean With Role And Org</li>
	 * </ul>
	 * @param loGroupList - list of groups
	 * @return loUserHashMap - hashmap of userdn and cityuserdetailbean
	 * @throws ApplicationException Updated Method in R4
	 */
	@SuppressWarnings("rawtypes")
	public HashMap<String, CityUserDetailsBeanForBatch> getMemberListForLDAPGroup(List<String> loGroupList)
			throws ApplicationException
	{
		HashMap<String, CityUserDetailsBeanForBatch> loUserHashMap = new HashMap<String, CityUserDetailsBeanForBatch>();
		String lsSearchFilter = LDAPConstants.BLANK_STRING;
		Attribute loListOfUsers = null;
		try
		{
			JNDILDAPConnectionManager loJndiManager = new JNDILDAPConnectionManager();
			DirContext loDirContext = loJndiManager.getLDAPDirContext();

			SearchControls loSearchCtrl = new SearchControls();
			String loReturnedAttrs[] =
			{ LDAPConstants.MEMBER_ATTRIBUTE };
			loSearchCtrl.setSearchScope(SUBTREE_SCOPE);
			loSearchCtrl.setReturningAttributes(loReturnedAttrs);

			Iterator loListLtr = loGroupList.iterator();
			while (loListLtr.hasNext())
			{
				try
				{
					String lsGroupName = (String) loListLtr.next();
					LOG_OBJECT.Debug("Fetching member list corresponding to LDAP group ::  " + lsGroupName);
					lsSearchFilter = LDAPConstants.GROUP_FILTER + lsGroupName + LDAPConstants.DOUBLE_BRACKET;
					NamingEnumeration loAnswer = loDirContext.search("", lsSearchFilter, loSearchCtrl);
					while (null != loAnswer && loAnswer.hasMore())
					{
						SearchResult loEntry = (SearchResult) loAnswer.next();
						loListOfUsers = null != loEntry.getAttributes().get(LDAPConstants.MEMBER_ATTRIBUTE) ? loEntry
								.getAttributes().get(LDAPConstants.MEMBER_ATTRIBUTE) : null;
						if (null != loListOfUsers)
						{
							LOG_OBJECT.Debug("Member list corresponding to LDAP group ::  " + lsGroupName + " is "
									+ loListOfUsers.toString());
							for (int liCount = LDAPConstants.INTEGER_ZERO; liCount < loListOfUsers.size(); liCount++)
							{
								String lsUserDN = loListOfUsers.get(liCount).toString();
								LOG_OBJECT.Debug("USER DN retrieved from loListOfUsers is  ::  " + lsUserDN);
								try
								{
									if (null != lsUserDN && !lsUserDN.isEmpty())
									{
										String lsUserCN = lsUserDN.substring(lsUserDN.indexOf(LDAPConstants.USER_CN),
												lsUserDN.indexOf(LDAPConstants.USER_OU) - LDAPConstants.INTEGER_ONE);
										String lsBaseDN = lsUserDN.substring(lsUserDN.indexOf(LDAPConstants.USER_OU));
										Attributes aoUserAttributes = getUserAttributes(loDirContext, lsUserCN,
												lsBaseDN);
										CityUserDetailsBeanForBatch loUserBean = new CityUserDetailsBeanForBatch();
										loUserBean.setMsEmailId(aoUserAttributes.get(LDAPConstants.MAIL_ATTRIBUTE)
												.get().toString().trim());
										loUserBean.setMsFirstName(aoUserAttributes
												.get(LDAPConstants.GIVEN_NAME_ATTRIBUTE).get().toString().trim());
										loUserBean.setMsLastName(aoUserAttributes.get(LDAPConstants.SN_ATTRIBUTE).get()
												.toString().trim());
										loUserBean.setMsUserDn(lsUserDN);
										loUserBean.setMsActiveFlag(LDAPConstants.STRING_ONE);
										updateUserBeanWithRoleAndOrg(loUserBean, lsGroupName.trim());
										loUserHashMap.put(lsUserDN, loUserBean);
									}

								}
								catch (Exception aoEx)
								{
									LOG_OBJECT.Error(
											"execption occured while fetching user details from LDAP For User :::  "
													+ lsUserDN, aoEx);
									// R 7.8.0 do not stop collecting info for other users
									continue;
								}
							}
						}
					}
				}
				catch (NamingException aoNaEx)
				{
					LOG_OBJECT.Error("Error while fetching data from ldap lsSearchFilter: " + lsSearchFilter, aoNaEx);

				}
				catch (ApplicationException aoAppEx)
				{
					LOG_OBJECT.Error("Error while fetching data from ldap lsSearchFilter: " + lsSearchFilter, aoAppEx);
				}
				catch (Exception aoAppEx)
				{
					LOG_OBJECT.Error("Error while fetching data from ldap lsSearchFilter: " + lsSearchFilter, aoAppEx);
				}

			}
		}
		catch (NamingException aoNaEx)
		{
			LOG_OBJECT.Error("Error in getting LDAP connection  ", aoNaEx.getMessage());
			throw new ApplicationException("Error in getting LDAP connection  ", aoNaEx);
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Error while fetching data from ldap lsSearchFilter: " + lsSearchFilter, aoAppEx);
			throw new ApplicationException("Error in getting LDAP connection  ", aoAppEx);
		}

		return loUserHashMap;
	}

	/**
	 * This method is used to Synchronizing LDAP records with database
	 * <ul>
	 * <li>Execute transaction id <b>
	 * updateRoleInCityUserDetailsForLdapBatch</b></li>
	 * <li>Set the Channel object</li>
	 * <li>Fetch details from Ldap User Details Bean</li>
	 * </ul>
	 * @param loLDAPHashMap user details map obtained from LDAP
	 * @param loDBHashMap user details map obtained from database
	 * @throws ApplicationException , Exception Updated Method in R4
	 */
	@SuppressWarnings(
	{ "rawtypes" })
	public void identifyingListOfUsersToBeUpdated(HashMap loLDAPHashMap, HashMap loDBHashMap)
			throws ApplicationException, Exception
	{
		int liUserInsertedCount = LDAPConstants.INTEGER_ZERO;
		int liUserUpdatedCount = LDAPConstants.INTEGER_ZERO;
		int liUserActivatedCount = LDAPConstants.INTEGER_ZERO;
		Map loSiteMinderUserDetailUpdateMap = new HashMap();
		Map loSiteMinderUserDetailInsertMap = null;
		Channel loChannel = null;
		Iterator loLDAPLtr = loLDAPHashMap.entrySet().iterator();
		while (loLDAPLtr.hasNext())
		{
			try
			{
				CityUserDetailsBeanForBatch loLdapUserDetailsBean = null;
				Map.Entry loPairs = (Map.Entry) loLDAPLtr.next();
				String lsLDAPHashMapKey = (String) loPairs.getKey();
				loLdapUserDetailsBean = (CityUserDetailsBeanForBatch) loPairs.getValue();
				LOG_OBJECT.Debug("processing record for user from LDAP:::: " + lsLDAPHashMapKey);

				if (null != lsLDAPHashMapKey && loDBHashMap != null && !loDBHashMap.isEmpty()
						&& loDBHashMap.containsKey(lsLDAPHashMapKey))
				{
					CityUserDetailsBeanForBatch loDbUserDetailsBean = null;
					loDbUserDetailsBean = (CityUserDetailsBeanForBatch) loDBHashMap.get(lsLDAPHashMapKey);
					if (!(null != loLdapUserDetailsBean
							&& null != loDbUserDetailsBean
							&& loLdapUserDetailsBean.getMsFirstName().equalsIgnoreCase(
									loDbUserDetailsBean.getMsFirstName())
							&& loLdapUserDetailsBean.getMsLastName().equalsIgnoreCase(
									loDbUserDetailsBean.getMsLastName())
							&& loLdapUserDetailsBean.getMsEmailId()
									.equalsIgnoreCase(loDbUserDetailsBean.getMsEmailId())
							&& loLdapUserDetailsBean.getMsUserRole().equalsIgnoreCase(
									loDbUserDetailsBean.getMsUserRole())
							&& loLdapUserDetailsBean.getMsUserType().equalsIgnoreCase(
									loDbUserDetailsBean.getMsUserType()) && loLdapUserDetailsBean.getMsOrgName()
							.equalsIgnoreCase(loDbUserDetailsBean.getMsOrgName())))
					{
						// mismatch in LDAP and DB records .. hence updating
						// record in DB
						if (null != loLdapUserDetailsBean)
						{
							LOG_OBJECT.Debug("Updating database for user ::  " + null != loLdapUserDetailsBean
									.getMsEmailId() ? loLdapUserDetailsBean.getMsEmailId() : "");
							loChannel = new Channel();
							loSiteMinderUserDetailUpdateMap = setMapObject(loLdapUserDetailsBean);
							loChannel.setData("aoCmUserDetailMap", loSiteMinderUserDetailUpdateMap);
							TransactionManager.executeTransaction(loChannel, "updateRoleInCityUserDetailsForLdapBatch");
							LOG_OBJECT.Debug("Updating database for user ::  " + loLdapUserDetailsBean.getMsEmailId()
									+ " userDn== " + lsLDAPHashMapKey);
							liUserUpdatedCount++;
						}
					}
				}
				else
				{
					// search that USER DN in database, if found update else
					// insert
					loChannel = new Channel();
					HashMap<String, String> loMap = new HashMap<String, String>();
					loMap.put("asCmUserDN", lsLDAPHashMapKey);
					loChannel.setData("aoCmUserDetailMap", loMap);
					TransactionManager.executeTransaction(loChannel, "searchUserDnInUserDetailsForLdapBatch");
					loSiteMinderUserDetailInsertMap = (Map) loChannel.getData("aoSiteMinderUserDetailMap");
					if (null != loSiteMinderUserDetailInsertMap && !loSiteMinderUserDetailInsertMap.isEmpty())
					{
						LOG_OBJECT.Debug("Activating record for user ::  " + loLdapUserDetailsBean.getMsEmailId());
						loChannel = new Channel();
						loSiteMinderUserDetailUpdateMap = setMapObject(loLdapUserDetailsBean);
						loChannel.setData("aoCmUserDetailMap", loSiteMinderUserDetailUpdateMap);
						TransactionManager.executeTransaction(loChannel, "updateRoleInCityUserDetailsForLdapBatch");
						LOG_OBJECT.Debug("successfully Activated record for user ::  "
								+ loLdapUserDetailsBean.getMsEmailId() + " userDn== " + lsLDAPHashMapKey);
						liUserActivatedCount++;
					}
					else
					{
						LOG_OBJECT.Debug("inserting user details in city user details for user ::  "
								+ loLdapUserDetailsBean.getMsEmailId() + " userDn== " + lsLDAPHashMapKey);
						loChannel = new Channel();
						loSiteMinderUserDetailUpdateMap = setMapObject(loLdapUserDetailsBean);
						loChannel.setData("aoCmUserDetailMap", loSiteMinderUserDetailUpdateMap);
						TransactionManager.executeTransaction(loChannel, "insertIntoSiteMinderUserDetailsForLdapBatch");
						LOG_OBJECT.Debug("successfully inserted record for user ::  "
								+ loLdapUserDetailsBean.getMsEmailId() + " userDn== " + lsLDAPHashMapKey);
						liUserInsertedCount++;
					}

				}
			}
			catch (ApplicationException aoAppEx)
			{
				LOG_OBJECT.Error(
						"execption occured while executing identifyingListOfUsersToBeUpdated() method for user  ::  ",
						aoAppEx);
			}
			catch (Exception aoEx)
			{
				LOG_OBJECT.Error(
						"execption occured while executing identifyingListOfUsersToBeUpdated() method for user  ::  ",
						aoEx);
			}

		}
		LOG_OBJECT.Debug("Total inserted record ::  " + liUserInsertedCount + "/n Total updated records:: "
				+ liUserUpdatedCount + "/n Total activated records:: " + liUserActivatedCount);
	}

	/**
	 * This method populate CityUserDetailsBean for internal users
	 * <ul>
	 * <li>Fill Site Minder User Detail Update Map</li>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * @param aoLdapUserDetailsBean CityUserDetailsBean fetched from LDAP
	 * @return loSiteMinderUserDetailUpdateMap CityUserDetailsBean object
	 * @throws ApplicationException
	 */
	public Map<String, String> setMapObject(CityUserDetailsBeanForBatch aoLdapUserDetailsBean)
			throws ApplicationException
	{
		Map<String, String> loSiteMinderUserDetailUpdateMap = new HashMap<String, String>();
		loSiteMinderUserDetailUpdateMap.put("FirstName", aoLdapUserDetailsBean.getMsFirstName());
		loSiteMinderUserDetailUpdateMap.put("LastName", aoLdapUserDetailsBean.getMsLastName());
		loSiteMinderUserDetailUpdateMap.put("EmailAddress", aoLdapUserDetailsBean.getMsEmailId());
		loSiteMinderUserDetailUpdateMap.put("UserRole", aoLdapUserDetailsBean.getMsUserRole());
		loSiteMinderUserDetailUpdateMap.put("UserType", aoLdapUserDetailsBean.getMsUserType());
		loSiteMinderUserDetailUpdateMap.put("OrgName", aoLdapUserDetailsBean.getMsOrgName());
		loSiteMinderUserDetailUpdateMap.put("UserDN", aoLdapUserDetailsBean.getMsUserDn());
		return loSiteMinderUserDetailUpdateMap;
	}

	/**
	 * This method update the UsderBean for city and agency users
	 * 
	 * @param aoUserBean Bean containing user details
	 * @param asGroup internal user group
	 * @return void
	 * 
	 * @throws ApplicationException
	 */
	public void updateUserBeanWithRoleAndOrg(CityUserDetailsBeanForBatch loUserBean, String asGroup)
			throws ApplicationException
	{
		if (asGroup.toLowerCase().contains(ApplicationConstants.AGENCY))
		{
			updateUserBeanAgency(asGroup, loUserBean);
		}
		else
		{
			updateUserbeanCity(asGroup, loUserBean);
		}
	}

	/**
	 * This method update the UsderBean for city
	 * <ul>
	 * <li>Set User Bean</li>
	 * </ul>
	 * @param asRole Principle of the subject
	 * @param aoUserBean Bean containing user details
	 * @throws ApplicationException
	 */
	private void updateUserbeanCity(String asRole, CityUserDetailsBeanForBatch aoUserBean) throws ApplicationException
	{
		aoUserBean.setMsUserType(ApplicationConstants.CITY_ORG);
		aoUserBean.setMsOrgName(ApplicationConstants.CITY);
		aoUserBean.setMsUserRole(ApplicationConstants.ROLE_STAFF);
		if (asRole.toLowerCase().contains("staff"))
		{
			aoUserBean.setMsUserRole(ApplicationConstants.ROLE_STAFF);
		}
		else if (asRole.toLowerCase().contains("manager"))
		{
			aoUserBean.setMsUserRole(ApplicationConstants.ROLE_MANAGER);
		}
		else
		{
			aoUserBean.setMsUserRole(ApplicationConstants.ROLE_EXECUTIVE);
		}
	}

	/**
	 * This method update the UsderBean for Agency
	 * <ul>
	 * <li>Set User Bean</li>
	 * </ul>
	 * 
	 * @param asRole Principle of the subject
	 * @param aoUserBean Bean containing user details
	 * @throws ApplicationException
	 */
	private void updateUserBeanAgency(String asRole, CityUserDetailsBeanForBatch aoUserBean)
			throws ApplicationException
	{
		String lsSplitName[] = asRole.split("_");
		aoUserBean.setMsUserRole(ApplicationConstants.ROLE_STAFF);
		if (null != lsSplitName && lsSplitName.length > 3)
		{
			aoUserBean.setMsOrgName(lsSplitName[3].trim().toUpperCase());
			if (lsSplitName.length > 4)
			{
				String lsRoleString = lsSplitName[4].toLowerCase().trim();
				if (lsRoleString.indexOf(".") != -1)
				{
					lsRoleString = lsRoleString.substring(0, lsRoleString.indexOf("."));
				}
				String loAgencyRole = ApplicationConstants.ROLE_AGENCY.get(lsRoleString.toLowerCase().trim());
				if (null != loAgencyRole)
				{
					aoUserBean.setMsUserRole(loAgencyRole);
				}
			}
		}
		else
		{
			aoUserBean.setMsOrgName(lsSplitName[2].trim());
		}

		aoUserBean.setMsUserType(ApplicationConstants.AGENCY_ORG);
	}

	/**
	 * This method used to de-activate internal users
	 * <ul>
	 * <li>Execute transaction id <b> deactivateInternalUser</b></li>
	 * <li>Set the Channel object</li>
	 * </ul>
	 * @param loLDAPHashMap LDAP hash map
	 * @param loDBHashMap DB hash map
	 * @throws ApplicationException
	 */

	public void deactivateRemainingUsersFromDatabase(HashMap loLDAPHashMap, HashMap loDBHashMap)
			throws ApplicationException
	{

		Channel loChannel = null;
		Iterator loDBItr = loDBHashMap.entrySet().iterator();
		while (loDBItr.hasNext())
		{
			try
			{
				Map.Entry loPairs = (Map.Entry) loDBItr.next();
				String lsDBHashMapKey = (String) loPairs.getKey();
				LOG_OBJECT.Debug("processing record for user from LDAP:::: " + lsDBHashMapKey);

				if (null != lsDBHashMapKey && !loLDAPHashMap.isEmpty() && !loLDAPHashMap.containsKey(lsDBHashMapKey))
				{
					loChannel = new Channel();
					HashMap loHashMap = new HashMap();
					loHashMap.put("UserDN", lsDBHashMapKey);
					loChannel.setData("aoCmUserDetailMap", loHashMap);
					TransactionManager.executeTransaction(loChannel, "deactivateInternalUser");

				}
			}
			catch (ApplicationException aoAppEx)
			{
				LOG_OBJECT
						.Error("execption occured while executing deactivateRemainingUsersFromDatabase() method for user  ::  ",
								aoAppEx);
			}
			catch (Exception aoEx)
			{
				LOG_OBJECT
						.Error("execption occured while executing deactivateRemainingUsersFromDatabase() method for user  ::  ",
								aoEx);
			}
		}

	}

}