package com.nyc.hhs.util;

import java.math.BigDecimal;
import java.security.Security;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import javax.portlet.PortletSession;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.ComponentRoleMappingBean;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.MasterStatusBean;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.model.UserBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;

/**
 * This utility class has common methods for creating a HashMap containing
 * component role mapping, checking whether component should display or not
 * based on role component mapping and making entry in task History Audit
 * 
 */

public class CommonUtil
{
	private static final LogInfo LOG_OBJECT = new LogInfo(CommonUtil.class);
	public static String asBuildNo = "";

	/**
	 * This method converts the bean to String
	 * 
	 * @param aoBeanObject the bean object thet is to be converted
	 * @return lsBeanToString is the string representation of the bean object
	 */
	public static String convertBeanToString(Object aoBeanObject)
	{
		String lsBeanToString = "";
		try
		{
			ObjectWriter loWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
			if (TaskDetailsBean.class.isInstance(aoBeanObject))
			{
				TaskDetailsBean loTaskDetailsBean = ((TaskDetailsBean) BeanUtils.cloneBean(aoBeanObject));
				loTaskDetailsBean.setP8UserSession(null);
				lsBeanToString = loWriter.writeValueAsString(loTaskDetailsBean);
			}
			else
			{
				lsBeanToString = loWriter.writeValueAsString(aoBeanObject);
			}
		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Exception occured while converting Bean to String in CommonUtil ", loEx);
		}
		return lsBeanToString;
	}

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	/**
	 * This method creates the component role map
	 * <ul><li>Updated for R4 - Added Permission Type in the Component Role Mapping Map for new Financial.Procurement Roles of Provider</li></ul>
	 * @param aoList containing the user details and component details
	 * @returns loComponentMap the component containing role, component and organization
	 */
	public static Map getComponentRoleMap(List aoList)
	{
		Iterator loIterator = aoList.iterator();
		String lsComponentId = null;
		String lsOrgType = null;
		String lsRole = null;
		// Added for R4
		String lsPermissionType = null;
		Map loComponentMap = new HashMap();
		Map loOrgTypeMap = null;
		Map loRoleDisplayMap = null;
		while (loIterator.hasNext())
		{
			ComponentRoleMappingBean loValue = (ComponentRoleMappingBean) loIterator.next();
			lsComponentId = loValue.getMsComponentId().toLowerCase();
			lsOrgType = loValue.getMsOrgType().toLowerCase();
			lsRole = loValue.getMsRole().toLowerCase();
			lsPermissionType = loValue.getMsPermissionType();
			if (loComponentMap.containsKey(lsComponentId))
			{
				Map loNewOrgTypeMap = (Map) loComponentMap.get(lsComponentId);
				Map loNewRoleDisplayMap = new HashMap();
				if (loNewOrgTypeMap.containsKey(lsOrgType))
				{

					loNewRoleDisplayMap = (Map) loNewOrgTypeMap.get(lsOrgType);
					loNewRoleDisplayMap.put(lsRole, lsPermissionType);
					loNewOrgTypeMap.put(lsOrgType, loNewRoleDisplayMap);
				}
				else
				{
					loNewRoleDisplayMap.put(lsRole, lsPermissionType);
					loNewOrgTypeMap.put(lsOrgType, loNewRoleDisplayMap);
				}
				loComponentMap.put(lsComponentId, loNewOrgTypeMap);
			}
			else
			{
				loOrgTypeMap = new HashMap();
				loRoleDisplayMap = new HashMap<String, String>();
				loRoleDisplayMap.put(lsRole, lsPermissionType);
				loOrgTypeMap.put(lsOrgType, loRoleDisplayMap);
				loComponentMap.put(lsComponentId, loOrgTypeMap);
			}
		}
		return loComponentMap;
	}

	/**
	 * <ul>
	 * <li>This method will determine whether or not the particular component
	 * will be visible to the user</li>
	 * <li>Updated for R4: Adding additional check for the new provider role -
	 * Financial/Procurement and Financial Procurement</li>
	 * </ul>
	 * 
	 * @param asComponentId is the string representation of the component Id
	 * @param aoSession is the HttpSession session
	 * @return the boolean flags whether or not the particular component will be
	 *         visible to the user
	 */
	// Changed in R5
	@SuppressWarnings("rawtypes")
	public static boolean getConditionalRoleDisplay(String asComponentId, HttpSession aoSession)
	{
		return getConditionalRoleDisplayMap(asComponentId, aoSession).get("authorizeFlag");
	}

	/**
	 * This method will determine whether or not the particular component will
	 * be visible to the user Updated for R4: Adding additional check for the
	 * new provider role - Financial/Procurement and Financial Procurement
	 * 
	 * @param asComponentId is the string representation of the component Id
	 * @param aoUserRoleMapping UserRole Mapping retrieved from session
	 * @param aoComponentMap Component Map retrieved from Session
	 * @return the boolean flag whether or not the particular component will be
	 *         visible to the user
	 */
	// Changed in R5
	private static Map<String, Boolean> getConditionalFlag(String asComponentId, UserBean aoUserRoleMapping,
			Map aoComponentMap)
	{
		String lsOrgType = aoUserRoleMapping.getMsOrgType();
		String lsOrgRole = aoUserRoleMapping.getMsRole();
		String lsPermissionType = aoUserRoleMapping.getMsPermissionType();
		String lsComponentId = asComponentId.toLowerCase();
		boolean lbAuthorizeFlag = false;
		boolean lbReadonlyFlag = true;
		String lsRoleDisplayKey = "";
		String lsRoleDisplayValue = "";
		Map<String, Boolean> loReturnMap = new HashMap<String, Boolean>();
		if (aoComponentMap.containsKey(lsComponentId))
		{
			Map loOrgTypeMap = (Map) aoComponentMap.get(lsComponentId);
			Set loOrgTypeEntrySet = loOrgTypeMap.entrySet();
			Iterator loOrgTypeItr = loOrgTypeEntrySet.iterator();
			while (loOrgTypeItr.hasNext())
			{
				Map.Entry loMapEntryObj = (Map.Entry) loOrgTypeItr.next();
				String lsOrgTypeKey = (String) loMapEntryObj.getKey();
				Map loRoleDisplayMap = (Map) loMapEntryObj.getValue();

				Set loRoleDisplayEntrySet = loRoleDisplayMap.entrySet();
				Iterator loRoleDisplayItr = loRoleDisplayEntrySet.iterator();
				while (loRoleDisplayItr.hasNext())
				{
					Map.Entry loRoleDisplayEntryObj = (Map.Entry) loRoleDisplayItr.next();
					lsRoleDisplayKey = (String) loRoleDisplayEntryObj.getKey();
					lsRoleDisplayValue = (String) loRoleDisplayEntryObj.getValue();
					if (lsOrgType.equalsIgnoreCase(lsOrgTypeKey)
							&& (lsRoleDisplayKey.equalsIgnoreCase(lsOrgRole)
									|| (ApplicationConstants.ROLE_STAFF.equalsIgnoreCase(lsRoleDisplayKey) && (ApplicationConstants.ROLE_MANAGER
											.equalsIgnoreCase(lsOrgRole)
											|| ApplicationConstants.ROLE_ADMINISTRATOR_PROV_STAFF
													.equalsIgnoreCase(lsOrgRole)
											|| ApplicationConstants.ROLE_ADMINISTRATOR_PROV_MANAGER
													.equalsIgnoreCase(lsOrgRole) || ApplicationConstants.ROLE_EXECUTIVE
												.equalsIgnoreCase(lsOrgRole))) || (ApplicationConstants.ROLE_MANAGER
									.equalsIgnoreCase(lsRoleDisplayKey)
									&& (ApplicationConstants.ROLE_ADMINISTRATOR_PROV_MANAGER
											.equalsIgnoreCase(lsOrgRole) || ApplicationConstants.ROLE_EXECUTIVE
											.equalsIgnoreCase(lsOrgRole))
									|| (ApplicationConstants.ACCO_STAFF_ROLE.equalsIgnoreCase(lsRoleDisplayKey) && (ApplicationConstants.ACCO_ADMIN_STAFF_ROLE
											.equalsIgnoreCase(lsOrgRole)
											|| ApplicationConstants.ACCO_MANAGER_ROLE.equalsIgnoreCase(lsOrgRole) || ApplicationConstants.CFO_ROLE
												.equalsIgnoreCase(lsOrgRole)))
									|| (ApplicationConstants.PROGRAM_STAFF_ROLE.equalsIgnoreCase(lsRoleDisplayKey) && (ApplicationConstants.PROGRAM_ADMIN_STAFF_ROLE
											.equalsIgnoreCase(lsOrgRole)
											|| ApplicationConstants.PROGRAM_MANAGER_ROLE.equalsIgnoreCase(lsOrgRole) || ApplicationConstants.CFO_ROLE
												.equalsIgnoreCase(lsOrgRole))) || (ApplicationConstants.FINANCE_STAFF_ROLE
									.equalsIgnoreCase(lsRoleDisplayKey) && (ApplicationConstants.FINANCE_ADMIN_STAFF_ROLE
									.equalsIgnoreCase(lsOrgRole)
									|| ApplicationConstants.FINANCE_MANAGER_ROLE.equalsIgnoreCase(lsOrgRole) 
									|| ApplicationConstants.CFO_ROLE.equalsIgnoreCase(lsOrgRole))))))
					{
						if (lsOrgType.equalsIgnoreCase(ApplicationConstants.PROVIDER_ORG)
								&& (lsRoleDisplayValue.equalsIgnoreCase(lsPermissionType)
										|| ApplicationConstants.ROLE_FINANCIALPROCUREMENT
												.equalsIgnoreCase(lsRoleDisplayValue) || (ApplicationConstants.ROLE_FINANCIALPROCUREMENT
											.equalsIgnoreCase(lsPermissionType))))
						{
							lbAuthorizeFlag = true;
							lbReadonlyFlag = false;
							break;
						}
						else if (lsOrgType.equalsIgnoreCase(ApplicationConstants.PROVIDER_ORG))
						{
							lbAuthorizeFlag = true;
							lbReadonlyFlag = true;
							break;
						}
						else if (!lsOrgType.equalsIgnoreCase(ApplicationConstants.PROVIDER_ORG))
						{
							lbAuthorizeFlag = true;
							lbReadonlyFlag = false;
							break;
						}
					}
				}
			}
		}
		else
		{
			lbAuthorizeFlag = false;
		}
		loReturnMap.put("authorizeFlag", lbAuthorizeFlag);
		loReturnMap.put("readonlyFlag", lbReadonlyFlag);
		return loReturnMap;
	}

	// R5 code end
	/**
	 * <ul>
	 * <li>This method will determine whether or not the particular component
	 * will be visible to the user</li>
	 * <li>Updated for R4: Adding additional check for the new provider role -
	 * Financial/Procurement and Financial Procurement</li>
	 * </ul>
	 * 
	 * @param asComponentId is the string representation of the component Id
	 * @param aoSession is the PortletSession object
	 * @return the boolean flag whether or not the particular component will be
	 *         visible to the user
	 */
	// changed in R5
	@SuppressWarnings("rawtypes")
	public static boolean getConditionalRoleDisplay(String asComponentId, PortletSession aoSession)
	{
		UserBean loUserRoleMapping = (UserBean) aoSession
				.getAttribute("getUserRoles", PortletSession.APPLICATION_SCOPE);
		Map loComponentMap = (Map) aoSession.getAttribute("roleMappingMap", PortletSession.APPLICATION_SCOPE);
		Map<String, Boolean> loBooleanFlagMap = getConditionalFlag(asComponentId, loUserRoleMapping, loComponentMap);

		return loBooleanFlagMap.get("authorizeFlag");
	}

	// R5 code ends
	/**
	 * This method will determine whether or not the particular component will
	 * be visible to the user Updated for R4: Adding additional check for the
	 * new provider role - Financial/Procurement and Financial Procurement
	 * <ul>
	 * <li>Updated for R4: Adding additional check for the new provider role -
	 * Financial/Procurement and Financial Procurement</li>
	 * </ul>
	 * 
	 * @param asComponentId is the string representation of the component Id
	 * @param aoSession is the PortletSession object
	 * @return the boolean flag whether or not the particular component will be
	 *         visible to the user
	 */
	// R5 code start
	@SuppressWarnings("rawtypes")
	public static boolean getConditionalRoleWithoutCFODisplay(String asComponentId, PortletSession aoSession)
	{
		UserBean loUserRoleBean = (UserBean) aoSession.getAttribute("getUserRoles", PortletSession.APPLICATION_SCOPE);
		String lsOrganizationRole = loUserRoleBean.getMsRole();
		String lsOrgPermissionType = loUserRoleBean.getMsPermissionType();
		Map loComponentRoleMap = (Map) aoSession.getAttribute("roleMappingMap", PortletSession.APPLICATION_SCOPE);
		String lsOrganizationType = loUserRoleBean.getMsOrgType();
		String lsComponentId = asComponentId.toLowerCase();
		String lsRoleKey = "";
		String lsRoleValue = "";
		boolean lbComponentFlag = false;
		if (loComponentRoleMap.containsKey(lsComponentId))
		{
			Map loOrgTypeMap = (Map) loComponentRoleMap.get(lsComponentId);
			Set loOrgTypeEntrySet = loOrgTypeMap.entrySet();
			Iterator loOrgTypeItr = loOrgTypeEntrySet.iterator();
			while (loOrgTypeItr.hasNext())
			{
				Map.Entry loMapEntryObj = (Map.Entry) loOrgTypeItr.next();
				String lsOrgTypeKey = (String) loMapEntryObj.getKey();
				Map loRoleDisplayMap = (Map) loMapEntryObj.getValue();

				Set loRoleDisplayEntrySet = loRoleDisplayMap.entrySet();
				Iterator loRoleDisplayItr = loRoleDisplayEntrySet.iterator();
				while (loRoleDisplayItr.hasNext())
				{
					Map.Entry loRoleDisplayEntryObj = (Map.Entry) loRoleDisplayItr.next();
					lsRoleKey = (String) loRoleDisplayEntryObj.getKey();
					lsRoleValue = (String) loRoleDisplayEntryObj.getValue();
					if (lsOrganizationType.equalsIgnoreCase(lsOrgTypeKey)
							&& (lsRoleKey.equalsIgnoreCase(lsOrganizationRole)
									|| (ApplicationConstants.ROLE_STAFF.equalsIgnoreCase(lsRoleKey) && (ApplicationConstants.ROLE_MANAGER
											.equalsIgnoreCase(lsOrganizationRole)
											|| ApplicationConstants.ROLE_ADMINISTRATOR_PROV_STAFF
													.equalsIgnoreCase(lsOrganizationRole)
											|| ApplicationConstants.ROLE_ADMINISTRATOR_PROV_MANAGER
													.equalsIgnoreCase(lsOrganizationRole) || ApplicationConstants.ROLE_EXECUTIVE
												.equalsIgnoreCase(lsOrganizationRole))) || (ApplicationConstants.ROLE_MANAGER
									.equalsIgnoreCase(lsRoleKey)
									&& (ApplicationConstants.ROLE_ADMINISTRATOR_PROV_MANAGER
											.equalsIgnoreCase(lsOrganizationRole) || ApplicationConstants.ROLE_EXECUTIVE
											.equalsIgnoreCase(lsOrganizationRole))
									|| (ApplicationConstants.ACCO_STAFF_ROLE.equalsIgnoreCase(lsRoleKey) && (ApplicationConstants.ACCO_ADMIN_STAFF_ROLE
											.equalsIgnoreCase(lsOrganizationRole) || ApplicationConstants.ACCO_MANAGER_ROLE
											.equalsIgnoreCase(lsOrganizationRole)))
									|| (ApplicationConstants.PROGRAM_STAFF_ROLE.equalsIgnoreCase(lsRoleKey) && (ApplicationConstants.PROGRAM_ADMIN_STAFF_ROLE
											.equalsIgnoreCase(lsOrganizationRole) || ApplicationConstants.PROGRAM_MANAGER_ROLE
											.equalsIgnoreCase(lsOrganizationRole))) || (ApplicationConstants.FINANCE_STAFF_ROLE
									.equalsIgnoreCase(lsRoleKey) && (ApplicationConstants.FINANCE_ADMIN_STAFF_ROLE
									.equalsIgnoreCase(lsOrganizationRole)
									|| ApplicationConstants.FINANCE_MANAGER_ROLE.equalsIgnoreCase(lsOrganizationRole) 
									|| ApplicationConstants.CFO_ROLE.equalsIgnoreCase(lsOrganizationRole))))))
					{
						if (lsOrganizationType.equalsIgnoreCase(ApplicationConstants.PROVIDER_ORG)
								&& (lsRoleValue.equalsIgnoreCase(lsOrgPermissionType)
										|| ApplicationConstants.ROLE_FINANCIALPROCUREMENT.equalsIgnoreCase(lsRoleValue) || (ApplicationConstants.ROLE_FINANCIALPROCUREMENT
											.equalsIgnoreCase(lsOrgPermissionType))))
						{
							lbComponentFlag = true;
							break;
						}
						else if (lsOrganizationType.equalsIgnoreCase(ApplicationConstants.PROVIDER_ORG))
						{
							lbComponentFlag = true;
							break;
						}
						else if (!lsOrganizationType.equalsIgnoreCase(ApplicationConstants.PROVIDER_ORG))
						{
							lbComponentFlag = true;
							break;
						}
					}
				}
			}
		}
		else
		{
			lbComponentFlag = false;
		}

		return lbComponentFlag;
	}

	// R5 code ends
	/**
	 * This method is for inserting audit data into audit table
	 * 
	 * @param aoChannel channel object to execute transaction
	 * @param asOrgId organization id for auditing
	 * @param asEventName event name for auditing
	 * @param asEventType event type for auditing
	 * @param aoDAuditDate audit type for auditing
	 * @param asData data description for auditing
	 * @param asEntityType entity type for auditing
	 * @param asEntityId entity id for auditing
	 * @param asProviderFlag provider flag indication for auditing
	 * @param asSectionId section id for auditing
	 * @param asAuditType audit type for auditing
	 * 
	 * @throws ApplicationException
	 */

	public static void addAuditDataToChannel(Channel aoChannel, String asOrgId, String asEventName, String asEventType,
			Date aoDAuditDate, String asUserId, String asData, String asEntityType, String asEntityId,
			String asProviderFlag, String asAppId, String asSectionId, String asAuditType) throws ApplicationException
	{
		// call the transaction AuditInformation
		aoChannel.setData("orgId", asOrgId);
		aoChannel.setData("eventName", asEventName);
		aoChannel.setData("eventType", asEventType);
		aoChannel.setData("auditDate", DateUtil.getFormattedDated("dd/MM/yyyy HH:mm:ss", aoDAuditDate));
		aoChannel.setData("userId", asUserId);
		aoChannel.setData("data", asData);
		aoChannel.setData("entityType", asEntityType);
		aoChannel.setData("entityId", asEntityId);
		aoChannel.setData("providerFlag", asProviderFlag);
		aoChannel.setData("appId", asAppId);
		aoChannel.setData("sectionId", asSectionId);
		aoChannel.setData("asAuditType", asAuditType);
	}

	/**
	 * This method will generate 6 digit random number
	 * 
	 * @param liLength random number liLength
	 * @return six digit random number
	 */
	//[Start]R9.3.0 qc 9638 Vuln 5: CWE 331 - Insufficient Entropy
	/* public static String getRandomString(int liLength)
	{
		String lsCaptchaChars = "abcdefghjijkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ123456789";
		Random loRandom = new Random();
		char[] loBuf = new char[liLength];
		for (int liCntr = 0; liCntr < liLength; liCntr++)
		{
			loBuf[liCntr] = lsCaptchaChars.charAt(loRandom.nextInt(lsCaptchaChars.length()));
		}
		return new String(loBuf);
	}*/
	//[End]R9.3.0 qc 9638 Vuln 5: CWE 331 - Insufficient Entropy
	/**
	 * This method will import security certificates required for authentication
	 */
	public static void importCertif()
	{
		String lsOKeyStore;
		try
		{
			lsOKeyStore = PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE,
					ApplicationConstants.PROPERTY_KEY_STORE_PATH);
			System.setProperty(ApplicationConstants.SSL_TRUSTSTORE, lsOKeyStore);
			System.setProperty(ApplicationConstants.SSL_TRUSTSTORE_PASSWORD, "changeit");
			System.setProperty(ApplicationConstants.SSL_KEYSTORE, lsOKeyStore);
			System.setProperty(ApplicationConstants.SSL_KEYSTORE_TYPE, "JKS");
			System.setProperty(ApplicationConstants.SSL_TRUSTSTORE_TYPE, "JKS");
			Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
			System.setProperty(ApplicationConstants.PROTOCOL_HANDLER_PKGS,
					ApplicationConstants.PROTOCOL_HANDLER_PKG_NAME);

		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occurred while importing certificates", loAppEx);
		}
	}

	/**
	 * This method is to fetch the build constant from property file
	 * 
	 * @return a string value of build constant
	 */
	public static String buildConstant()
	{
		try
		{
			asBuildNo = PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE,
					ApplicationConstants.PROPERTY_BUILD_NO);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Error occurred while fetching build constant", loAppEx);
		}
		return asBuildNo;
	}

	/**
	 * This method is used to load the application settings attributes into
	 * cache
	 * 
	 * @return loApplicationSettingMap map containing settings values
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public static HashMap<String, String> getApplicationSettings() throws ApplicationException
	{
		HashMap<String, String> loApplicationSettingMap = new HashMap<String, String>();
		Channel loChannel = new Channel();
		TransactionManager.executeTransaction(loChannel, "applicationSettingDB");
		List<HashMap<String, String>> loApplicationSettingMapList = (List<HashMap<String, String>>) loChannel
				.getData("loAppliocationSettingMap");
		for (Iterator loIterator = loApplicationSettingMapList.iterator(); loIterator.hasNext();)
		{
			HashMap<String, String> loMap = (HashMap<String, String>) loIterator.next();
			loApplicationSettingMap.put(loMap.get("COMPONENT_NAME").concat("_").concat(loMap.get("SETTINGS_NAME")),
					loMap.get("SETTINGS_VALUE"));

		}
		return loApplicationSettingMap;
	}

	public static String getActionMenuAvailable() throws ApplicationException
	{
		HashMap<String, String>  loApplicationSettingMap  =  getApplicationSettings();
		return loApplicationSettingMap.get(HHSConstants.GET_ACTION_MENU_CTRL);
	}

	/*[Start] QC9744 */
	public static String getDocVaultActionCityAvailable()  throws ApplicationException
	{
		HashMap<String, String>  loApplicationSettingMap  =  getApplicationSettings();

			if( loApplicationSettingMap.get(HHSConstants.GET_DOC_VAULT_MENU_CITY_CTRL) != null) {
				return loApplicationSettingMap.get(HHSConstants.GET_DOC_VAULT_MENU_CITY_CTRL);
			} else {
				return HHSConstants.STRING_TRUE;
			}
	}
	public static String getDocVaultActionProviderAvailable()  throws ApplicationException
	{
		HashMap<String, String>  loApplicationSettingMap  =   getApplicationSettings();

			if( loApplicationSettingMap.get(HHSConstants.GET_DOC_VAULT_MENU_PROVIDER_CTRL) != null) {
				return loApplicationSettingMap.get(HHSConstants.GET_DOC_VAULT_MENU_PROVIDER_CTRL);
			} else {
				return HHSConstants.STRING_TRUE;
			}
	}
	/*[End] QC9744 */

	public static String getOrgAccountRequestDiableNote() throws ApplicationException
	{
		String lsDefaultRtn = "Your request can not be processed now. Please contact Administrator.";
		HashMap<String, String>  loApplicationSettingMap  =  getApplicationSettings();
		String loNote = loApplicationSettingMap.get(HHSConstants.GET_ORG_ACCOUNT_CTRL);
		if(loNote != null && loNote.length() > 0 ) {
			return loNote;
		}else {
			return lsDefaultRtn ;
		}
	}

	/**
	 * This method is used to load the application settings for batch attributes
	 * into cache Created for R4
	 * 
	 * @return loApplicationSettingMapForBatch map containing settings values
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public static HashMap<String, String> getApplicationSettingsForBatch(String asTransactionCacheKey)
			throws ApplicationException
	{
		HashMap<String, String> loApplicationSettingMapForBatch = new HashMap<String, String>();
		Channel loChannel = new Channel();
		TransactionManager.executeTransaction(loChannel, "applicationSettingDB_batch", asTransactionCacheKey);
		List<HashMap<String, String>> loApplicationSettingMapList = (List<HashMap<String, String>>) loChannel
				.getData("loAppliocationSettingMap");
		for (Iterator loIterator = loApplicationSettingMapList.iterator(); loIterator.hasNext();)
		{
			HashMap<String, String> loMap = (HashMap<String, String>) loIterator.next();
			loApplicationSettingMapForBatch.put(
					loMap.get("COMPONENT_NAME").concat("_").concat(loMap.get("SETTINGS_NAME")),
					loMap.get("SETTINGS_VALUE"));

		}
		return loApplicationSettingMapForBatch;
	}

	/**
	 * This method is to get Date in MM/dd/yyyy HH:mm:ss format
	 * 
	 * @param asDate input date
	 * @throws Date
	 */
	public static Date getItemDate(final String asDate)
	{
		final Calendar loCal = Calendar.getInstance();
		final SimpleDateFormat loFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		loFormat.setCalendar(loCal);

		try
		{
			return loFormat.parse(asDate);
		}
		catch (Exception loExc)
		{
			return null;
		}
	}

	/**
	 * This method is to get time difference in minutes
	 * 
	 * @param asDate loEarlierDate
	 * @param asDate loLaterDate
	 * @return integer minutes
	 */
	public static int minutesDiff(Date loEarlierDate, Date loLaterDate)
	{
		if (loEarlierDate == null || loLaterDate == null)
		{
			return 0;
		}

		return (int) ((loLaterDate.getTime() / 60000) - (loEarlierDate.getTime() / 60000));
	}

	/**
	 * This method is to get Current time
	 * 
	 * @return lsCurrentdate
	 */
	public static String getCurrentTime()
	{
		Date loCurrentdate = new Date(System.currentTimeMillis());
		DateFormat loDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		String lsCurrentdate = loDateFormat.format(loCurrentdate);
		return lsCurrentdate;
	}

	/**
	 * This method is to get Date in MM/dd/yyyy HH:mm:ss:SSS format
	 * 
	 * @param asDate input date
	 * @throws Date
	 */
	public static Date getItemDateInMIlisec(final String asDate)
	{
		final Calendar loCal = Calendar.getInstance();
		final SimpleDateFormat loFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss:SSS");
		loFormat.setCalendar(loCal);

		try
		{
			return loFormat.parse(asDate);
		}
		catch (Exception loExc)
		{
			return null;
		}
	}

	/**
	 * This method is to get time difference in miliseconds
	 * 
	 * @param asDate loEarlierDate
	 * @param asDate loLaterDate
	 * @return integer minutes
	 */
	public static float timeDiff(Date loEarlierDate, Date loLaterDate)
	{
		if (loEarlierDate == null || loLaterDate == null)
		{
			return 0;
		}

		return (float) (((loLaterDate.getTime()) - (loEarlierDate.getTime()))) / 1000;
	}

	/**
	 * This method is to get Current time in milisecond
	 * 
	 * @return lsCurrentdate
	 */
	public static String getCurrentTimeInMilliSec()
	{
		Date loCurrentdate = new Date(System.currentTimeMillis());
		DateFormat loFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss:SSS");
		String lsCurrentdate = loFormat.format(loCurrentdate);
		return lsCurrentdate;
	}

	/**
	 * This method is used to create a hashmap of task and procurement
	 * properties to be fetched from workflow
	 * 
	 * @return a hashmap of task and procurement properties to be fetched from
	 *         workflow Updated Method in R4
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public static HashMap getTaskPropertiesHashMap()
	{
		HashMap loHmReqProposMap = new HashMap();
		loHmReqProposMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_TITLE, "");
		loHmReqProposMap.put(P8Constants.PE_WORKFLOW_PROVIDER_ID, "");
		loHmReqProposMap.put(P8Constants.PE_WORKFLOW_PROVIDER_NAME, "");
		loHmReqProposMap.put(P8Constants.PE_WORKFLOW_PROPOSAL_TITLE, "");
		loHmReqProposMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_EPIN, "");
		loHmReqProposMap.put(P8Constants.PE_WORKFLOW_FIRST_ROUND_EVAL_DATE, "");
		loHmReqProposMap.put(P8Constants.PROPERTY_PE_TASK_NAME, "");
		loHmReqProposMap.put(P8Constants.PE_WORKFLOW_TASK_TYPE, "");
		loHmReqProposMap.put(P8Constants.PE_WORKFLOW_ASSIGNED_TO_NAME, "");
		loHmReqProposMap.put(P8Constants.PE_WORKFLOW_ASSIGNED_TO, "");
		loHmReqProposMap.put(P8Constants.PE_WORKFLOW_DATE_ASSIGNED, "");
		loHmReqProposMap.put(P8Constants.PE_WORKFLOW_LAST_MODIFIED_DATE, "");
		loHmReqProposMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, "");
		loHmReqProposMap.put(P8Constants.PE_WORKFLOW_PROPOSAL_ID, "");
		loHmReqProposMap.put(P8Constants.PE_WORKFLOW_TASK_ID, "");
		loHmReqProposMap.put(P8Constants.PE_WORKFLOW_AGENCY_ID, "");
		loHmReqProposMap.put(P8Constants.PE_WORKFLOW_AGENCY_NAME, "");
		loHmReqProposMap.put(P8Constants.PE_WORKFLOW_TASK_STATUS, "");
		loHmReqProposMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_PRIMARY_AGENCY_ID, "");
		loHmReqProposMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_SECONDARY_AGENCY_ID, "");
		loHmReqProposMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_PRIMARY_AGENCY, "");
		loHmReqProposMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_SECONDARY_AGENCY, "");
		loHmReqProposMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_PRIMARY_ACC_ID, "");
		loHmReqProposMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_SECONDARY_ACC_ID, "");
		loHmReqProposMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_PRIMARY_ACC, "");
		loHmReqProposMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_SECONDARY_ACC, "");
		loHmReqProposMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_AWARD_AMOUNT, "");
		loHmReqProposMap.put(P8Constants.PE_WORKFLOW_EVALUATION_STATUS_ID, "");
		loHmReqProposMap.put(P8Constants.PE_WORKFLOW_IS_FIRST_LAUNCH, "");
		loHmReqProposMap.put(P8Constants.PE_WORKFLOW_TASK_STATUS_PREVIOUS, "");
		loHmReqProposMap.put(P8Constants.FINALIZE_EVALUATION_DATE, "");
		loHmReqProposMap.put(P8Constants.AWARD_SELECTION_DATE, "");
		loHmReqProposMap.put(P8Constants.PE_WORKFLOW_IS_FIRST_REACHED, "");
		loHmReqProposMap.put(P8Constants.PE_WORKFLOW_COMPETITION_POOL_TITLE, "");
		loHmReqProposMap.put(P8Constants.EVALUATION_POOL_MAPPING_ID, "");
		loHmReqProposMap.put(P8Constants.PROPERTY_PE_EVAL_GRP_TITLE, "");
		loHmReqProposMap.put(P8Constants.IS_OPEN_ENDED_RFP, "");
		return loHmReqProposMap;
	}

	/**
	 * This method get the procurement and task details from hashmap and set
	 * into the loTaskDetailsBean object
	 * 
	 * <ul>
	 * <li>Get the task detail map and wob number from input</li>
	 * <li>Get the task and procurement properties from map and set in task
	 * details bean</li>
	 * <li>Return Task details bean to the calling method</li>
	 * </ul>
	 * 
	 * @param aoTaskMap
	 * @param asWobNumber a string value of wob number
	 * @return loTaskDetailsBean containing procurement and task details
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings("unchecked")
	public static TaskDetailsBean getTaskDetailsBeanFromMap(HashMap<String, Object> aoTaskMap, String asWobNumber)
			throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		if (null != aoTaskMap)
		{
			HashMap<String, Object> aoTaskDetailMap = (HashMap<String, Object>) aoTaskMap.get(asWobNumber);
			if (null != aoTaskDetailMap)
			{
				// Changes made for QC Defect 6530
				loTaskDetailsBean.setProcurementTitle(StringEscapeUtils.escapeHtml((String) aoTaskDetailMap
						.get(P8Constants.PE_WORKFLOW_PROCUREMENT_TITLE)));
				loTaskDetailsBean.setOrganizationId((String) aoTaskDetailMap.get(P8Constants.PE_WORKFLOW_PROVIDER_ID));
				loTaskDetailsBean.setOrganizationName((String) aoTaskDetailMap
						.get(P8Constants.PE_WORKFLOW_PROVIDER_NAME));
				loTaskDetailsBean
						.setProposalTitle((String) aoTaskDetailMap.get(P8Constants.PE_WORKFLOW_PROPOSAL_TITLE));
				loTaskDetailsBean.setProcurementEpin((String) aoTaskDetailMap
						.get(P8Constants.PE_WORKFLOW_PROCUREMENT_EPIN));
				loTaskDetailsBean.setFirstRoundEvalCompDate(DateUtil.getDateMMddYYYYFormat((Date) aoTaskDetailMap
						.get(P8Constants.PE_WORKFLOW_FIRST_ROUND_EVAL_DATE)));
				loTaskDetailsBean.setTaskName((String) aoTaskDetailMap.get(P8Constants.PROPERTY_PE_TASK_NAME));
				loTaskDetailsBean.setTaskType((String) aoTaskDetailMap.get(P8Constants.PE_WORKFLOW_TASK_TYPE));
				loTaskDetailsBean.setAssignedToUserName((String) aoTaskDetailMap
						.get(P8Constants.PE_WORKFLOW_ASSIGNED_TO_NAME));
				loTaskDetailsBean.setAssignedTo((String) aoTaskDetailMap.get(P8Constants.PE_WORKFLOW_ASSIGNED_TO));
				loTaskDetailsBean.setAssignedDate(DateUtil.getDateMMddYYYYFormat((Date) aoTaskDetailMap
						.get(P8Constants.PE_WORKFLOW_DATE_ASSIGNED)));
				loTaskDetailsBean.setLastModifiedDate(DateUtil.getDateMMddYYYYFormat((Date) aoTaskDetailMap
						.get(P8Constants.PE_WORKFLOW_LAST_MODIFIED_DATE)));
				loTaskDetailsBean
						.setProcurementId((String) aoTaskDetailMap.get(P8Constants.PE_WORKFLOW_PROCUREMENT_ID));
				loTaskDetailsBean.setProposalId((String) aoTaskDetailMap.get(P8Constants.PE_WORKFLOW_PROPOSAL_ID));
				loTaskDetailsBean.setTaskId(String.valueOf(aoTaskDetailMap.get(P8Constants.PE_WORKFLOW_TASK_ID)));
				loTaskDetailsBean.setAgencyId((String) aoTaskDetailMap.get(P8Constants.PE_WORKFLOW_AGENCY_ID));
				loTaskDetailsBean.setAgencyName((String) aoTaskDetailMap.get(P8Constants.PE_WORKFLOW_AGENCY_NAME));
				loTaskDetailsBean.setTaskStatus((String) aoTaskDetailMap.get(P8Constants.PE_WORKFLOW_TASK_STATUS));
				loTaskDetailsBean.setAgencyPrimaryContactId((String) aoTaskDetailMap
						.get(P8Constants.PE_WORKFLOW_PROCUREMENT_PRIMARY_AGENCY_ID));
				loTaskDetailsBean.setAgencySecondaryContactId((String) aoTaskDetailMap
						.get(P8Constants.PE_WORKFLOW_PROCUREMENT_SECONDARY_AGENCY_ID));
				loTaskDetailsBean.setAwardAmount((String) aoTaskDetailMap
						.get(P8Constants.PE_WORKFLOW_PROCUREMENT_AWARD_AMOUNT));
				loTaskDetailsBean.setEvaluationStatusId(String.valueOf(aoTaskDetailMap
						.get(P8Constants.PE_WORKFLOW_EVALUATION_STATUS_ID)));
				loTaskDetailsBean.setIsFirstLaunch((Boolean) aoTaskDetailMap
						.get(P8Constants.PE_WORKFLOW_IS_FIRST_LAUNCH));
				loTaskDetailsBean.setIsFirstReached((Boolean) aoTaskDetailMap
						.get(P8Constants.PE_WORKFLOW_IS_FIRST_REACHED));
				loTaskDetailsBean.setPreviousTaskStatus((String) aoTaskDetailMap
						.get(P8Constants.PE_WORKFLOW_TASK_STATUS_PREVIOUS));
				loTaskDetailsBean.setFinalizeEvaluationDate(DateUtil.getDateMMddYYYYFormat((Date) aoTaskDetailMap
						.get(P8Constants.FINALIZE_EVALUATION_DATE)));
				loTaskDetailsBean.setAwardSelectionDate(DateUtil.getDateMMddYYYYFormat((Date) aoTaskDetailMap
						.get(P8Constants.AWARD_SELECTION_DATE)));
				loTaskDetailsBean.setAgencyPrimaryContact((String) aoTaskDetailMap
						.get(P8Constants.PE_WORKFLOW_PROCUREMENT_PRIMARY_AGENCY));
				loTaskDetailsBean.setAgencySecondaryContact((String) aoTaskDetailMap
						.get(P8Constants.PE_WORKFLOW_PROCUREMENT_SECONDARY_AGENCY));
				loTaskDetailsBean.setAccPrimaryContactId((String) aoTaskDetailMap
						.get(P8Constants.PE_WORKFLOW_PROCUREMENT_PRIMARY_ACC_ID));
				loTaskDetailsBean.setAccSecondaryContactId((String) aoTaskDetailMap
						.get(P8Constants.PE_WORKFLOW_PROCUREMENT_SECONDARY_ACC_ID));
				loTaskDetailsBean.setAccPrimaryContact((String) aoTaskDetailMap
						.get(P8Constants.PE_WORKFLOW_PROCUREMENT_PRIMARY_ACC));
				loTaskDetailsBean.setAccSecondaryContact((String) aoTaskDetailMap
						.get(P8Constants.PE_WORKFLOW_PROCUREMENT_SECONDARY_ACC));
				loTaskDetailsBean
						.setCompetitionPoolTitle((aoTaskDetailMap.get(P8Constants.PE_WORKFLOW_COMPETITION_POOL_TITLE) == null || aoTaskDetailMap
								.get(P8Constants.PE_WORKFLOW_COMPETITION_POOL_TITLE).equals(HHSConstants.EMPTY_STRING)) ? (String) aoTaskDetailMap
								.get(P8Constants.PE_WORKFLOW_PROCUREMENT_TITLE) : (String) aoTaskDetailMap
								.get(P8Constants.PE_WORKFLOW_COMPETITION_POOL_TITLE));
				loTaskDetailsBean.setEvaluationPoolMappingId(aoTaskDetailMap
						.get(P8Constants.EVALUATION_POOL_MAPPING_ID) == null ? loTaskDetailsBean.getProcurementId()
						: (String) aoTaskDetailMap.get(P8Constants.EVALUATION_POOL_MAPPING_ID));
				loTaskDetailsBean.setEvaluationGroupTitle((String) aoTaskDetailMap
						.get(P8Constants.PROPERTY_PE_EVAL_GRP_TITLE));
				loTaskDetailsBean.setIsOpenEndedRfp((String) aoTaskDetailMap.get(P8Constants.IS_OPEN_ENDED_RFP));
				// R5 Change starts
				loTaskDetailsBean.setIsNegotiationRequired((Boolean) aoTaskDetailMap
						.get(P8Constants.PROPERTY_WORKFLOW_IS_NEGOTIATION_REQUIRED));
				//

			}
		}
		return loTaskDetailsBean;
	}

	/**
	 * This method generates audit bean object for saving provider and internal
	 * comments in user comments table
	 * 
	 * @param asTaskId a string value of taskId
	 * @param asWorkflowId a string value of workflow Id
	 * @param asEntityType a string value of entity type
	 * @param asEntityId a string value of entity id
	 * @param asUserId a string value of user id
	 * @param asUserOrg a string value of user org
	 * @param asProviderComments a string value of provider comments
	 * @param asInternalComments a string value of internal comments
	 * @return audit bean object
	 */
	public static HhsAuditBean getBeanForSavingUserComments(String asTaskId, String asWorkflowId, String asEntityType,
			String asEntityId, String asUserId, String asUserOrg, String asProviderComments, String asInternalComments)
	{
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		loHhsAuditBean.setAuditTableIdentifier(ApplicationConstants.NON_AUDIT_COMMENTS);
		loHhsAuditBean.setTaskId(asTaskId);
		loHhsAuditBean.setWorkflowId(asWorkflowId);
		loHhsAuditBean.setEntityType(asEntityType);
		loHhsAuditBean.setEntityId(asEntityId);
		loHhsAuditBean.setUserId(asUserId);
		loHhsAuditBean.setAgencyId(asUserOrg);
		loHhsAuditBean.setProviderComments(asProviderComments);
		loHhsAuditBean.setInternalComments(asInternalComments);
		loHhsAuditBean.setIsTaskScreen(false);
		return loHhsAuditBean;
	}	
	/**
	 * This method is used to get reassign user map
	 * @param loUserList list of UsreBean
	 * @return loAgencyMap map of string
	 * @throws ApplicationException
	 */
	public static Map<String, String> getReassignUserMap(List<UserBean> loUserList) throws ApplicationException
	{
		Map<String, String> loAgencyMap = null;
		if (null != loUserList && !loUserList.isEmpty())
		{
			loAgencyMap = new TreeMap<String, String>();
			for (UserBean loUserBean : loUserList)
			{
				loAgencyMap.put(loUserBean.getMsUserId(), loUserBean.getMsUserName());
			}
		}
		return loAgencyMap;
	}

	/**
	 * This Methods return Audit bean
	 * 
	 * @param aoChannel Channel Object
	 * @param asEventName Name Of the Event
	 * @param asEventType Type OF the Event
	 * @param asData Data to be inserted
	 * @param asEntityType Type of Entity
	 * @param asEntityId Id of Entity
	 * @param asUserID USer ID
	 * @param asTableIdentifier Table Identifier
	 * @return aoAudit
	 */
	public static HhsAuditBean addAuditDataToChannel(String asEventName, String asEventType, String asData,
			String asEntityType, String asEntityId, String asUserID, String asTableIdentifier)
	{
		HhsAuditBean aoAudit = new HhsAuditBean();
		aoAudit.setEventName(asEventName);
		aoAudit.setEventType(asEventType);
		aoAudit.setData(asData);
		aoAudit.setEntityType(asEntityType);
		aoAudit.setEntityId(asEntityId);
		aoAudit.setUserId(asUserID);
		aoAudit.setAuditTableIdentifier(asTableIdentifier);
		return aoAudit;
	}

	/**
	 * Converts string currency format to bigdecimal and normal number format to
	 * long
	 * 
	 * @param asCurrencyToConvert String to be converted to long or bigdecimal
	 * @return Converted object
	 * @throws ApplicationException Application Exception
	 */
	public static Object convertCurrencyFormatToNumber(String asCurrencyToConvert) throws ApplicationException
	{
		try
		{
			if (asCurrencyToConvert != null)
			{
				asCurrencyToConvert = asCurrencyToConvert.replaceAll(ApplicationConstants.COMMA,
						ApplicationConstants.EMPTY_STRING);
				if (asCurrencyToConvert.contains(ApplicationConstants.DOT))
				{
					return new BigDecimal(asCurrencyToConvert);
				}
				else
				{
					return Long.parseLong(asCurrencyToConvert);
				}
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While converting string currency format to bigdecimal and normal number format to long",
					aoEx);
			throw loAppEx;
		}
		return Long.valueOf(ApplicationConstants.INT_ZERO);
	}

	/**
	 * This method gets the cache region for cache key
	 * 
	 * @param aoCacheKey - cache key for which key to be searched
	 * @return cache region for the key
	 */
	public static String getRegionName(Object aoCacheKey)
	{
		String lsRegionName = null;
		if (aoCacheKey != null)
		{
			lsRegionName = ApplicationConstants.CACHE_REGION_MAPPING.get((String) aoCacheKey);
			if (lsRegionName == null)
			{
				if (((String) aoCacheKey).startsWith(ApplicationConstants.LOCK_ID_START)
						|| ((String) aoCacheKey).startsWith(HHSR5Constants.DV_LOCK_ID_START))
				{
					lsRegionName = ApplicationConstants.HHS_CONFIG_SYNC;
				}
				else if (((String) aoCacheKey).equalsIgnoreCase("editDocumentMap"))
				{
					lsRegionName = ApplicationConstants.HHS_LOCK_IDS;
				}
				else
				{
					lsRegionName = ApplicationConstants.HHS_CONFIG;
				}
			}
		}
		return lsRegionName;
	}

	/**
	 * This method set all the contract type into the map
	 * 
	 * @return Map contract type map
	 * @throws ApplicationException Application Exception Updated Method in R4
	 */
	public static Map<String, String> getContractType() throws ApplicationException
	{

		Map<String, String> loContractType = new LinkedHashMap<String, String>();
		Document loXMLDoc = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				HHSConstants.CONTRACT_TYPE_MAPPING_CONFIG_KEY);
		int liContractTypeCount = 1;
		if (null == loXMLDoc || loXMLDoc.toString().equals(HHSConstants.EMPTY_STRING))
		{
			throw new ApplicationException("Contract type configuration not loaded into memory for transaction key: "
					+ HHSConstants.CONTRACT_TYPE_MAPPING_CONFIG_KEY);
		}
		String lsXPath = HHSConstants.CONTRACT_TYPE_CONFIG_NODE_XPATH;
		Element loEle = XMLUtil.getElement(lsXPath, loXMLDoc);
		if (null != loEle)
		{
			List<Element> loChildrenElementList = loEle.getChildren(HHSConstants.CONTRACT_TYPE_CONFIG_NODE_NAME);
			for (Element loElement : loChildrenElementList)
			{
				loContractType.put(String.valueOf(liContractTypeCount), loElement.getAttributeValue(HHSConstants.TYPE));
				liContractTypeCount++;
			}
		}
		return loContractType;
	}

	/**
	 * This method gives the full sequential call of the method with its
	 * references.
	 * 
	 * @return a string containing stack trace of method call sequence for
	 *         current thread.
	 */
	public static String getCallingFunctionInformation()
	{
		StackTraceElement[] loStackTraceElements = Thread.currentThread().getStackTrace();
		StringBuffer loTrace = new StringBuffer();
		String lsClassname;

		if (loStackTraceElements != null)
		{
			for (StackTraceElement loStack : loStackTraceElements)
			{
				lsClassname = loStack.getClassName().substring(loStack.getClassName().lastIndexOf(".") + 1);
				if (loStack.getClassName().contains("com.nyc.hhs."))
				{

					loTrace.append(lsClassname);
					loTrace.append(" -> ");
					loTrace.append(loStack.getMethodName());
					loTrace.append("-->>");
				}

			}
		}
		return loTrace.toString();
	}
	/**
	 * This method is used to get condition role for display map
	 * @param asComponentId 
	 * @param aoSession a HttpSession object
	 * @return loReturnMap on input of specific component id
	 */
	// Added in R5
	public static Map<String, Boolean> getConditionalRoleDisplayMap(String asComponentId, HttpSession aoSession)
	{
		UserBean loUserRoleMapping = (UserBean) aoSession.getAttribute("getUserRoles");
		Map loComponentMap = (Map) aoSession.getAttribute("roleMappingMap");
		return getConditionalFlag(asComponentId, loUserRoleMapping, loComponentMap);
	}
	/**
	 * This method is used to set session for auto save data
	 * @param aoPortletSession a PortletSession object 
	 * @param asEntityId 
	 * @param asEntityName
	 */
	public static void setSessionForAutoSaveData(PortletSession aoPortletSession, String asEntityId, String asEntityName)
	{
		if (StringUtils.isNotBlank(asEntityId) && StringUtils.isNotBlank(asEntityName))
		{
			aoPortletSession.setAttribute(HHSR5Constants.AUTO_SAVE_ENTITY_ID, asEntityId,
					PortletSession.APPLICATION_SCOPE);
			aoPortletSession.setAttribute(HHSR5Constants.AUTO_SAVE_ENTITY_NAME, asEntityName,
					PortletSession.APPLICATION_SCOPE);
		}
	}
	/**
	 * This method is used to set channel for auto save data
	 * @param aoChannel a Channel object
	 * @param asEntityId
	 * @param aoEntityName
	 */
	public static void setChannelForAutoSaveData(Channel aoChannel, String asEntityId, String aoEntityName)
	{
		aoChannel.setData(HHSR5Constants.AUTO_SAVE_ENTITY_ID, asEntityId);
		aoChannel.setData(HHSR5Constants.AUTO_SAVE_ENTITY_NAME, aoEntityName);
	}
	
	/**
	 * BEGIN R 7.2.0  QC 8419 methods
	 */

	 /** 
	 * Check the value of Oversight flag for this User
	 * @param HttpSession
	 * @return boolean value to indicate if something should be hidden for Oversight role
	 */
	public static boolean hideForOversightRole(HttpSession session) {
		boolean hide = false;
		//"1".equalsIgnoreCase((String)session.getAttribute(ApplicationConstants.KEY_SESSION_OVERSIGHT_FLAG)) 
		if( ApplicationConstants.ROLE_OBSERVER.equalsIgnoreCase((String)session.getAttribute(ApplicationConstants.KEY_SESSION_ROLE_CURRENT))) 	
			hide = true;
		
		return hide;
	}
	
	/**
	 * This method will strp certain Options from dropdown, for example to have read-only Options, and not have write Options
	 * @param optionsList which contains various Options for Action DropDown
	 */
	public static void keepReadOnlyActions(StringBuilder optionsList) throws ApplicationException {
		
		LOG_OBJECT.Info("optionsList = " + optionsList);
		LOG_OBJECT.Info("length of optionsList = " + optionsList.length());
		
//		List<String> tokenList = new ArrayList<String>();
		// the following Options will be stripped out. Add or delete from the List to retain less, or more Options
//		tokenList.add("title='Cancel");
//		tokenList.add("title='Delete");
//		tokenList.add("title='Update");
//		tokenList.add("title='Flag");
//		tokenList.add("title='Download");
//		tokenList.add("title='Unflag");
//		tokenList.add("title='Unsuspend");
//		tokenList.add("title= 'New FY");
//		tokenList.add("title='Suspend");
//		tokenList.add("title='Amend");
//		tokenList.add("title='Renew");
//		tokenList.add("title='Close");
//		tokenList.add("title='Edit");
//		tokenList.add("title='Assign");
		
		Channel loChannel = new Channel();
		try
		{
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.GET_READ_ONLY_ACTIONS_TO_EXCLUDE);
			@SuppressWarnings("unchecked")
			List<String> actionsExcludeList = (List<String>) loChannel.getData(HHSConstants.READ_ONLY_ACTIONS_EXCLUDE_LIST);
		
			for (String token : actionsExcludeList) {
				while (StringUtils.contains(optionsList.toString(), token)) { // using while loop to handle duplicate tokens, just in case.
					int tokenIndex = StringUtils.indexOf(optionsList.toString(), token);
					int beginOptionIndex = StringUtils.lastIndexOf(optionsList.substring(0, tokenIndex), "<option"); // lastIndex will get '<option' just before token
					int endOptionIndex = StringUtils.indexOf(optionsList.toString(), "</option>", tokenIndex) + 9; // added 9 for </option>
					LOG_OBJECT.Info("before stripping = " + optionsList);
					LOG_OBJECT.Info("token = " + token);
					LOG_OBJECT.Info("tokenIndex = "+tokenIndex + " beginIndex = "+beginOptionIndex + " endIndex = "+endOptionIndex);
					optionsList.replace(0, optionsList.length(), StringUtils.overlay(optionsList.toString(), "", beginOptionIndex, endOptionIndex)); // purge option
					LOG_OBJECT.Info("after stripping = " + optionsList);
				}
			}
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception Occured while retrieving ReadOnlyActionsExcludeList", aoEx);
			LOG_OBJECT.Error("Exception Occured while retrieving ReadOnlyActionsExcludeList", loAppEx);
			throw loAppEx;
		}
		
	}
	
	/** End R 7.2.0  QC 8419 methods*/
	
	//*** Start QC 8998 R 8.8  remove password from logs
	/*
	 * This method is used to mask password in logs
	 * search password keyword and mask password value with *******
	 * returns String
	 */
	public static String maskPassword(Object aoParameter)
	{
		// QC9585 R 9.3.0 Service account password are being shown in plaintext in application Portal logs
		// find and replace with * all occurrences of password values in passing parameter
		String param = " ";
		String mySearch ="assword=";
		if(null!=aoParameter)
		{
			param = aoParameter.toString();
		}
		
		if(param.contains(mySearch))
		{
			String[] testarr = param.split(mySearch);
			String maskParam = "";
			int cnt = 0;
			for (String chunk : testarr)
			{	
				if(cnt == 0)
				{ 
					maskParam = chunk;
				}
				else
				{	
					int t = chunk.indexOf(",", 0);
					maskParam = maskParam + mySearch + "********" + chunk.substring(t);
				}
				cnt++;
			}
			
			param = maskParam;
		}
		
		return param;
	}
	//*** End QC9585 R 9.3.0  remove password from logs
}
