package com.nyc.hhs.contractsbatch.servicetestR7;

public class FileNetOperationsUtilsTest
{
	import static org.junit.Assert.assertTrue;


	import java.util.ArrayList;
	import java.util.HashMap;
	import java.util.Iterator;
	import java.util.List;
	import java.util.TreeSet;

	import org.apache.commons.lang.StringEscapeUtils;
	import org.apache.ibatis.session.SqlSession;
	import org.junit.AfterClass;
	import org.junit.BeforeClass;
	import org.junit.Test;
	import org.springframework.util.CollectionUtils;

	import com.nyc.hhs.constants.ApplicationConstants;
	import com.nyc.hhs.constants.HHSConstants;
	import com.nyc.hhs.exception.ApplicationException;
	import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
	import com.nyc.hhs.frameworks.cache.ICacheManager;
	import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
	import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
	import com.nyc.hhs.frameworks.transaction.Channel;
	import com.nyc.hhs.frameworks.transaction.TransactionManager;
	import com.nyc.hhs.model.DocumentPropertiesBean;
	import com.nyc.hhs.model.ProviderBean;
	import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
	import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
	import com.nyc.hhs.util.FileNetOperationsUtils;
	import com.nyc.hhs.util.PropertyLoader;
	import com.nyc.hhs.util.XMLUtil;

	/**
	 * @author anand.g.singh
	 *
	 */
	public class FileNetOperationsUtilsTest {
		
		private static P8UserSession session = null;

		private static SqlSession moSession = null; // SQL Session
		
		/**
		 * SQL session created ONCE before the class
		 * 
		 * @throws Exception
		 */
		@BeforeClass
		public static void setUpBeforeClass() throws Exception
		{
			Boolean lbThrown = false;
			try
			{
				session = getFileNetSession();
				ICacheManager loCacheManager = BaseCacheManagerWeb.getInstance();

				Object loCacheNotificationObject1 = XMLUtil.getDomObj((new FileNetOperationsUtilsTest()).getClass()
						.getResourceAsStream("/com/nyc/hhs/config/ExtendedDocType.xml"));
				loCacheManager.putCacheObject(ApplicationConstants.FILENET_EXTENDED_DOC_TYPE, loCacheNotificationObject1);

				Object loCacheNotificationObject2 = XMLUtil.getDomObj((new FileNetOperationsUtilsTest()).getClass()
						.getResourceAsStream("/com/nyc/hhs/config/DocType.xml"));
				loCacheManager.putCacheObject(ApplicationConstants.FILENETDOCTYPE, loCacheNotificationObject2);

				Object loCacheNotificationObject3 = XMLUtil.getDomObj((new FileNetOperationsUtilsTest()).getClass()
						.getResourceAsStream("/com/nyc/hhs/config/TransactionConfig.xml"));
				loCacheManager.putCacheObject(ApplicationConstants.TRANSACTION_ELEMENT, loCacheNotificationObject3);
				
				loCacheManager.putCacheObject(ApplicationConstants.PROV_LIST, getProviderListForCache());
				
				loCacheManager.putCacheObject(ApplicationConstants.AGENCY_LIST, getAgencyListForCache());

				moSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
			}
			catch (Exception loEx)
			{
				lbThrown = true;
				assertTrue("Exception thrown", lbThrown);
			}
		}

		/**
		 * SQL session created after the class
		 * 
		 * @throws Exception
		 */

		@AfterClass
		public static void tearDownAfterClass() throws Exception
		{
			Boolean lbThrown = false;
			try
			{
				moSession.close();
			}
			catch (Exception loEx)
			{
				lbThrown = true;
				assertTrue("Exception thrown", lbThrown);
			}
		}

		/**
		 * This method creates the Filenet session object.
		 * 
		 * @return P8UserSession
		 * @throws ApplicationException
		 */
		public static P8UserSession getFileNetSession() throws ApplicationException
		{
			System.setProperty(P8Constants.PROP_FILE_JAVA_SECURITY_AUTH_LOGIN_CONFIG, PropertyLoader.getProperty(
					P8Constants.PROPERTY_FILE, P8Constants.PROP_FILE_JAVA_SECURITY_AUTH_LOGIN_CONFIG));
			System.setProperty(P8Constants.PROP_FILE_JAVA_NAMING_FACTORY_INITIAL, PropertyLoader.getProperty(
					P8Constants.PROPERTY_FILE, P8Constants.PROP_FILE_JAVA_NAMING_FACTORY_INITIAL));
			System.setProperty(P8Constants.PROP_FILE_FILENET_PE_BOOTSTRAP_CEURI,
					PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "FILENET_URI"));

			P8UserSession loUserSession = new P8UserSession();
			loUserSession.setContentEngineUri(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "FILENET_URI"));
			loUserSession.setObjectStoreName(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "OBJECT_STORE_NAME"));
			loUserSession.setIsolatedRegionName(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
					"CONNECTION_POINT_NAME"));
			loUserSession.setUserId("ceadmin");
			loUserSession.setPassword("Filenet1");

			SqlSession loFilenetPEDBSession = HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory()
					.openSession();
			loUserSession.setFilenetPEDBSession(loFilenetPEDBSession);

			return loUserSession;
		}
		
		/**
		 * This method fetches provider list from the DB.
		 * 
		 * @return P8UserSession
		 * @throws ApplicationException
		 */
		public static List<ProviderBean> getProviderListForCache() throws ApplicationException
		{
			List<ProviderBean> loProviderList = new ArrayList<ProviderBean>();
			Channel loChannel = new Channel();
				TransactionManager.executeTransaction(loChannel, "getProviderListForCache_DB");
				List<ProviderBean> loProviderTempList = (List<ProviderBean>) loChannel.getData("providerList");
				if (!CollectionUtils.isEmpty(loProviderTempList))
				{
					Iterator<ProviderBean> loIter = loProviderTempList.iterator();
					while (loIter.hasNext())
					{
						ProviderBean loProviderBean = loIter.next();
						String lsDisplayValue = loProviderBean.getDisplayValue();
						lsDisplayValue = StringEscapeUtils.escapeJavaScript(lsDisplayValue);
						loProviderBean.setDisplayValue(lsDisplayValue);
						loProviderList.add(loProviderBean);
					}
				}
			
			return loProviderList;

		}
		
		/**
		 * This method fetches agency list from the DB.
		 * 
		 * @return P8UserSession
		 * @throws ApplicationException
		 */
		public static TreeSet getAgencyListForCache() throws ApplicationException
		{
			Channel loChannel = new Channel();
			TransactionManager.executeTransaction(loChannel, "getAgencyListForCache_DB");
			List<ProviderBean> loAgencyList = (List) loChannel.getData("agencyList");
			TreeSet loAgencySet = new TreeSet<String>();
			if (!CollectionUtils.isEmpty(loAgencyList))
			{
				Iterator<ProviderBean> loIter = loAgencyList.iterator();
				while (loIter.hasNext())
				{
					ProviderBean loProviderBean = loIter.next();
					String lsDisplayValue = loProviderBean.getDisplayValue();
					lsDisplayValue = StringEscapeUtils.escapeJavaScript(lsDisplayValue);
					loAgencySet.add(loProviderBean.getHiddenValue() + "~" + lsDisplayValue);
				}
			}
			return loAgencySet;

		}
		
		@Test
		public void testGetDocumentProperties() throws ApplicationException
		{ 
			HashMap<String, String> aoFilterDetails= new HashMap<String, String>();
			aoFilterDetails.put(HHSConstants.PROPERTY_PE_TASK_TYPE, "Service Application");
			FileNetOperationsUtils.generateInboxAndManagementFilterDetails(aoFilterDetails,session,true, "inbox","city_459");
		}

}
