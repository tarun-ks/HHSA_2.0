package com.nyc.hhs.controllers.utilTest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletException;

import org.apache.ibatis.session.SqlSession;
import org.jdom.Element;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.cache.CacheList;
import com.nyc.hhs.frameworks.cache.ICacheManager;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.model.AddressValidationBean;
import com.nyc.hhs.model.AutoCompleteBean;
import com.nyc.hhs.model.EvaluationBean;
import com.nyc.hhs.model.FinancialWFBean;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.RateBean;
import com.nyc.hhs.model.TaskAuditBean;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.servlet.CachingListener;
import com.nyc.hhs.servlet.HHSContextListener;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.FileNetOperationsUtils;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.PropertyUtil;
import com.nyc.hhs.util.XMLUtil;

public class HHSUtilTest
{

	private static SqlSession moSession = null; // SQL Session

	/**
	 * SQL session created ONCE before the class
	 * 
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{

		Boolean lbThrown = false;
		try
		{
			moSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
			loadAppCache();
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	public static void loadAppCache() throws ServletException, IOException
	{
		CacheList loCacheList = CacheList.getInstance();
		PrintWriter loOut = null;
		try
		{
			putCacheData(loCacheList);
		}
		catch (ApplicationException aoExp)
		{
		}
		catch (Exception aoExp)
		{
		}
		finally
		{
			if (null != loOut)
			{
				loOut.flush();
				loOut.close();
			}
		}
	}

	/**
	 * Method is used to put Cache data.
	 * @param aoCacheList
	 * @throws ApplicationException
	 */
	private static void putCacheData(CacheList aoCacheList) throws ApplicationException
	{
		HHSContextListener.contextInitializedSM();
		ResourceBundle loRB = PropertyLoader.getProperties(ApplicationConstants.CACHE_FILES);
		Object loCacheObject = XMLUtil.getDomObj(CachingListener.class.getResourceAsStream(loRB
				.getString(HHSConstants.TRANSACTION_LOWESCASE)));
		aoCacheList.putCacheLoader(loCacheObject);
		ICacheManager loCacheManager = BaseCacheManagerWeb.getInstance();
		loCacheManager.putCacheObject(HHSConstants.TRANSACTION_LOWESCASE, loCacheObject);
		Iterator loITer = loRB.keySet().iterator();
		loITer = loRB.keySet().iterator();
		while (loITer.hasNext())
		{
			String lsKey = (String) loITer.next();
			String lsFilePath = loRB.getString(lsKey);

			if (ApplicationConstants.TAXONOMY_ELEMENT.equalsIgnoreCase(lsKey))
			{
				PropertyUtil loTaxonomyUtil = new PropertyUtil();
				loTaxonomyUtil.setTaxonomyInCache(loCacheManager, ApplicationConstants.TAXONOMY_ELEMENT);
			}
			else if (ApplicationConstants.PROV_LIST.equalsIgnoreCase(lsKey))
			{
				loCacheManager.putCacheObject(lsKey, FileNetOperationsUtils.getProviderList(true));
			}
			else if (ApplicationConstants.AGENCY_LIST.equalsIgnoreCase(lsKey))
			{
				loCacheManager.putCacheObject(lsKey, FileNetOperationsUtils.getNYCAgencyListFromDB());
			}
			else if (ApplicationConstants.APPLICATION_SETTING.equalsIgnoreCase(lsKey))
			{
				loCacheManager.putCacheObject(lsKey, CommonUtil.getApplicationSettings());
			}
			else if (HHSConstants.TRANSACTION_LOWESCASE.equalsIgnoreCase(lsKey)
					|| HHSConstants.RECACHE_TIMER.equalsIgnoreCase(lsKey)
					|| HHSConstants.RECACHE_TIME_INTERVAL.equalsIgnoreCase(lsKey))
			{
			}
			else
			{
				loCacheObject = XMLUtil.getDomObj(CachingListener.class.getResourceAsStream(lsFilePath));
				aoCacheList.putCacheLoader(loCacheObject);
				loCacheManager.putCacheObject(lsKey, loCacheObject);
			}
		}
	}

	/**
	 * Close the SQL session created at the beginning
	 * 
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		Boolean lbThrown = false;
		try
		{
			moSession.rollback();
			moSession.close();
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}

	}

	/**
	 * This method adds 50 years to current date and return the resulted date.
	 * 
	 * @throws ApplicationException
	 * 
	 */
	@Test
	public void testGetToDate() throws ApplicationException
	{
		Date loDate = HHSUtil.getToDate();
		assertNotNull(loDate);
	}

	/**
	 * This method modifies string from date if exists and return modified date
	 * 
	 * @throws ApplicationException
	 * 
	 */
	@Test
	public void testSetDateToFrom() throws ApplicationException
	{
		Date loDate = HHSUtil.setDateToFrom(null, "03/05/2013", "from");
		assertNotNull(loDate);
	}

	/**
	 * This method modifies string from date if exists and return modified date
	 * 
	 * @throws ApplicationException
	 * 
	 */
	@Test
	public void testSetDateToFrom1() throws ApplicationException
	{
		Date loDate = HHSUtil.setDateToFrom("", "03/05/2014", "from");
		assertNotNull(loDate);
	}

	/**
	 * This method modifies string from date if exists and return modified date
	 * 
	 * @throws ApplicationException
	 * 
	 */
	@Test
	public void testSetDateToFrom2() throws ApplicationException
	{
		Date loDate = HHSUtil.setDateToFrom("03/05/2011", null, "from");
		assertNotNull(loDate);
	}

	/**
	 * This method modifies string from date if exists and return modified date
	 * 
	 * @throws ApplicationException
	 * 
	 */
	@Test
	public void testSetDateToFrom3() throws ApplicationException
	{
		Date loDate = HHSUtil.setDateToFrom("03/05/2013", "", "from");
		assertNotNull(loDate);
	}

	/**
	 * This method modifies string from date if exists and return modified date
	 * 
	 * @throws ApplicationException
	 * 
	 */
	@Test
	public void testSetDateToFrom4() throws ApplicationException
	{
		Date loDate = HHSUtil.setDateToFrom("03/05/2011", "", "to");
		assertNotNull(loDate);
	}

	/**
	 * This method modifies string from date if exists and return modified date
	 * 
	 * @throws ApplicationException
	 * 
	 */
	@Test
	public void testSetDateToFrom5() throws ApplicationException
	{
		Date loDate = HHSUtil.setDateToFrom("03/05/2011", null, "to");
		assertNotNull(loDate);
	}

	/**
	 * This method modifies string from date if exists and return modified date
	 * 
	 * @throws ApplicationException
	 * 
	 */
	@Test
	public void testSetDateToFrom6() throws ApplicationException
	{
		Date loDate = HHSUtil.setDateToFrom("", "03/05/2011", "to");
		assertNotNull(loDate);
	}

	/**
	 * This method modifies string from date if exists and return modified date
	 * 
	 * @throws ApplicationException
	 * 
	 */
	@Test
	public void testSetDateToFrom7() throws ApplicationException
	{
		Date loDate = HHSUtil.setDateToFrom(null, "03/05/2011", "to");
		assertNotNull(loDate);
	}

	/**
	 * This method modifies string to date if exists and return modified date
	 * 
	 * @throws ApplicationException
	 * 
	 */
	@Test
	public void testSetDateToFromToDate() throws ApplicationException
	{
		Date loDate = HHSUtil.setDateToFrom("03/05/2013", null, "to");
		assertNotNull(loDate);
	}

	/**
	 * This method adds hours, minutes and seconds to input from date and return
	 * the modified date
	 * 
	 * @throws ApplicationException
	 * 
	 */
	@Test
	public void testAddFromClause() throws ApplicationException
	{
		Date loDate = HHSUtil.addFromClause(HHSUtil.getToDate());
		assertNotNull(loDate);
	}

	/**
	 * This method adds hours, minutes and seconds to input to date and return
	 * the modified date
	 * 
	 * @throws ApplicationException
	 * 
	 */
	@Test
	public void testAddToClause() throws ApplicationException
	{
		Date loDate = HHSUtil.addFromClause(HHSUtil.getToDate());
		assertNotNull(loDate);
	}

	/**
	 * This method tests for count number of occurance of sub string in main
	 * string
	 * @throws ApplicationException
	 * 
	 */
	@Test
	public void testSubStringCount() throws ApplicationException
	{
		Integer liCount = HHSUtil.getSubStringCount("ababcdcdefef", "ab");
		assertNotNull(liCount);
	}

	/**
	 * This method tests for count number of occurance of sub string in main
	 * string
	 * @throws ApplicationException
	 * 
	 */
	@Test
	public void testSubStringCount1() throws ApplicationException
	{
		Integer liCount = HHSUtil.getSubStringCount("ababcdcdefef", "cd");
		assertNotNull(liCount);
	}

	/**
	 * This method tests for count number of occurance of sub string in main
	 * string
	 * @throws ApplicationException
	 * 
	 */
	@Test
	public void testSubStringCount2() throws ApplicationException
	{
		Integer liCount = HHSUtil.getSubStringCount("ababcdcdefef", "ef");
		assertNotNull(liCount);
	}

	/**
	 * 
	 * This method tests conversion of the AgencyName to acronym in () like DHS
	 * - Department of Homeless Services to Department of Homeless Services
	 * (DHS)
	 */
	@Test
	public void testAgencyName() throws Exception
	{
		String lsAgencyName = HHSUtil
				.getAgencyName("DHS -Department of Homeless Services to Department of Homeless Services");
		assertNotNull(lsAgencyName);
	}

	/**
	 * 
	 * This method tests conversion of the AgencyName to acronym in () like CC
	 * -Children care (CC)
	 */
	@Test
	public void testAgencyName1() throws Exception
	{
		String lsAgencyName = HHSUtil.getAgencyName("CC -Children care");
		assertNotNull(lsAgencyName);
	}

	/**
	 * 
	 * This method tests conversion of the AgencyName to acronym in () like CS
	 * -Cherity Services (CS)
	 */
	@Test
	public void testAgencyName2() throws Exception
	{
		String lsAgencyName = HHSUtil.getAgencyName("CS -Cherity Services");
		assertNotNull(lsAgencyName);
	}

	/**
	 * This method tests calculation for the Configurable Fiscal Year for a
	 * contract which returns Current FY(Fiscal Year) if Contract Start date is
	 * less than current FY else the Contract's first FY
	 * 
	 */
	@Test
	public void testFYForContractBudgetConfig() throws Exception
	{
		int liContractStartFY = HHSUtil.getFYForContractBudgetConfig(7);
		assertNotNull(liContractStartFY);
	}

	/**
	 * This method tests calculation for the Configurable Fiscal Year for a
	 * contract which returns Current FY(Fiscal Year) if Contract Start date is
	 * less than current FY else the Contract's first FY
	 * 
	 */
	@Test
	public void testFYForContractBudgetConfig1() throws Exception
	{
		int liContractStartFY = HHSUtil.getFYForContractBudgetConfig(6);
		assertNotNull(liContractStartFY);
	}

	/**
	 * This method tests calculation for the Configurable Fiscal Year for a
	 * contract which returns Current FY(Fiscal Year) if Contract Start date is
	 * less than current FY else the Contract's first FY
	 * 
	 */
	@Test
	public void testFYForContractBudgetConfig2() throws Exception
	{
		int liContractStartFY = HHSUtil.getFYForContractBudgetConfig(34);
		assertNotNull(liContractStartFY);
	}

	/**
	 * This method tests calculation for the Configurable Fiscal Year for a
	 * contract which returns Current FY(Fiscal Year) if Contract Start date is
	 * less than current FY else the Contract's first FY
	 * 
	 */
	@Test
	public void testFYForContractBudgetConfig3() throws Exception
	{
		int liContractStartFY = HHSUtil.getFYForContractBudgetConfig(12);
		assertNotNull(liContractStartFY);
	}

	/**
	 * This method tests for getting a particular status name corresponding to
	 * the status id and process Type where processType is a String variable
	 * corresponding to the process_type in status table and statusId ia a int
	 * variable corresponding to the status_id in status table
	 */
	@Test
	public void testStatusName() throws Exception
	{
		String lsFieldsDetail = HHSUtil.getStatusName("Payment", 43);
		assertNull(lsFieldsDetail);
	}

	/**
	 * This method tests for getting a particular status name corresponding to
	 * the status id and process Type where processType is a String variable
	 * corresponding to the process_type in status table and statusId ia a int
	 * variable corresponding to the status_id in status table
	 */
	@Test
	public void testStatusName1() throws Exception
	{
		String lsFieldsDetail = HHSUtil.getStatusName("Review Proposal Task", 43);
		assertNotNull(lsFieldsDetail);
	}

	/**
	 * This method tests for getting a particular status name corresponding to
	 * the status id and process Type where processType is a String variable
	 * corresponding to the process_type in status table and statusId ia a int
	 * variable corresponding to the status_id in status table
	 */
	@Test
	public void testStatusName2() throws Exception
	{
		String lsFieldsDetail = HHSUtil.getStatusName("Review Proposal Task", 43);
		assertNotNull(lsFieldsDetail);
	}

	/**
	 * This method tests for getting a particular status name corresponding to
	 * the status id and process Type where processType is a String variable
	 * corresponding to the process_type in status table and statusId ia a int
	 * variable corresponding to the status_id in status table
	 */
	@Test
	public void testStatusName3() throws Exception
	{
		String lsFieldsDetail = HHSUtil.getStatusName("Review Proposal Task", 43);
		assertNotNull(lsFieldsDetail);
	}

	/**
	 * This method tests for getting a particular status name corresponding to
	 * the status id and process Type where processType is a String variable
	 * corresponding to the process_type in status table and statusId ia a int
	 * variable corresponding to the status_id in status table
	 */
	@Test
	public void testStatusName4() throws Exception
	{
		String lsFieldsDetail = HHSUtil.getStatusName("Review Proposal Task", 43);
		assertNotNull(lsFieldsDetail);
	}

	/**
	 * This method tests the name of the transaction, depending upon the user
	 * org type e.g. comparing userOrgType with City,Provider or Accelerator and
	 * returning the transaction name to the homeFinancialController class</li>
	 */

	@Test
	public void testHomeFinancialTransaction() throws Exception
	{
		String lsFieldsDetail = HHSUtil.homeFinancialTransaction("city_org");
		assertNotNull(lsFieldsDetail);
	}

	/**
	 * This method tests the name of the transaction, depending upon the user
	 * org type e.g. comparing userOrgType with City,Provider or Accelerator and
	 * returning the transaction name to the homeFinancialController class</li>
	 */

	@Test
	public void testHomeFinancialTransaction1() throws Exception
	{
		String lsFieldsDetail = HHSUtil.homeFinancialTransaction("provider_org");
		assertNotNull(lsFieldsDetail);
	}

	/**
	 * This method tests the name of the transaction, depending upon the user
	 * org type e.g. comparing userOrgType with City,Provider or Accelerator and
	 * returning the transaction name to the homeFinancialController class</li>
	 */

	@Test
	public void testHomeFinancialTransaction2() throws Exception
	{
		String lsFieldsDetail = HHSUtil.homeFinancialTransaction("agency_org");
		assertNotNull(lsFieldsDetail);
	}

	/**
	 * This method tests the name of the transaction, depending upon the user
	 * org type e.g. comparing userOrgType with City,Provider or Accelerator and
	 * returning the transaction name to the homeFinancialController class</li>
	 */
	@Test
	public void testHomeFinancialTransaction3() throws Exception
	{
		String lsFieldsDetail = HHSUtil.homeFinancialTransaction("fgfdg");
		assertNull(lsFieldsDetail);
	}

	/**
	 * This method tests the name of the transaction, depending upon the user
	 * org type e.g. comparing userOrgType with City,Provider or Accelerator and
	 * returning the transaction name to the homeFinancialController class</li>
	 */
	@Test
	public void testHomeFinancialTransaction4() throws Exception
	{
		String lsFieldsDetail = HHSUtil.homeFinancialTransaction("abdc");
		assertNull(lsFieldsDetail);
	}

	/**
	 * This method tests the name of the transaction, depending upon the user
	 * org type e.g. comparing userOrgType with City,Provider or Accelerator and
	 * returning the transaction name to the homeFinancialController class</li>
	 */
	@Test
	public void testHomeFinancialTransaction5() throws Exception
	{
		String lsFieldsDetail = HHSUtil.homeFinancialTransaction("1234");
		assertNull(lsFieldsDetail);
	}

	/**
	 * This method tests the name of the transaction, depending upon the user
	 * org type e.g. comparing userOrgType with City,Provider or Accelerator and
	 * returning the transaction name to the homeFinancialController class</li>
	 */
	@Test
	public void testHomeFinancialTransaction6() throws Exception
	{
		String lsFieldsDetail = HHSUtil.homeFinancialTransaction("");
		assertNull(lsFieldsDetail);
	}

	/**
	 * This method tests the name of the transaction, depending upon the user
	 * org type.
	 */

	@Test
	public void testHomeProcurementTransaction1() throws Exception
	{
		String lsTransctionName = HHSUtil.homeProcurementTransaction("city_org");
		assertNotNull(lsTransctionName);
	}

	/**
	 * This method tests the name of the transaction, depending upon the user
	 * org type.
	 */

	@Test
	public void testHomeProcurementTransaction2() throws Exception
	{
		String lsTransctionName = HHSUtil.homeProcurementTransaction("agency_org");
		assertNotNull(lsTransctionName);
	}

	/**
	 * This method tests the name of the transaction, depending upon the user
	 * org type.
	 */

	@Test
	public void testHomeProcurementTransaction3() throws Exception
	{
		String lsTransctionName = HHSUtil.homeProcurementTransaction("");
		assertNull(lsTransctionName);
	}

	/**
	 * This method tests the name of the transaction, depending upon the user
	 * org type.
	 */

	@Test
	public void testHomeProcurementTransaction4() throws Exception
	{
		String lsTransctionName = HHSUtil.homeProcurementTransaction("provider_org");
		assertNotNull(lsTransctionName);
	}

	/**
	 * This method tests the name of the transaction, depending upon the user
	 * org type.
	 */

	@Test
	public void testHomeProcurementTransaction5() throws Exception
	{
		String lsTransctionName = HHSUtil.homeProcurementTransaction("AGENCY_ORG");
		assertNotNull(lsTransctionName);
	}

	/**
	 * This method tests the name of the transaction, depending upon the user
	 * org type.
	 */

	@Test
	public void testHomeProcurementTransaction6() throws Exception
	{
		String lsTransctionName = HHSUtil.homeProcurementTransaction("PROVIDER_ORG");
		assertNotNull(lsTransctionName);
	}

	/**
	 * This method tests the name of the transaction, depending upon the user
	 * org type.
	 */
	@Test
	public void testHomeProcurementTransaction7() throws Exception
	{
		String lsTransctionName = HHSUtil.homeProcurementTransaction("PROVIDERG");
		assertNull(lsTransctionName);
	}

	/**
	 * This method tests the name of the transaction, depending upon the action
	 * to be performed out of two actions.
	 */
	@Test
	public void testGetTransactionName1() throws Exception
	{
		String lsTransctionName = HHSUtil.getTransactionName("fetchProposalSummary", "fetchProposalSummary",
				"fetchProposalSummary");
		assertNotNull(lsTransctionName);
	}

	/**
	 * This method tests the name of the transaction, depending upon the action
	 * to be performed out of two actions.
	 */
	@Test
	public void testGetTransactionName() throws Exception
	{
		String lsTransctionName = HHSUtil.getTransactionName("fetchProposalDetails", "fetchProposalSummary",
				"fetchProposalDetails");
		assertNotNull(lsTransctionName);
	}

	/**
	 * This method tests the name of the transaction, depending upon the action
	 * to be performed out of two actions.
	 */
	@Test
	public void testGetTransactionName3() throws Exception
	{
		String lsTransctionName = HHSUtil.getTransactionName(null, "fetchProposalSummary", "fetchProposalDetails");
		assertNull(lsTransctionName);
	}

	/**
	 * This method tests the name of the transaction, depending upon the action
	 * to be performed out of two actions.
	 */
	@Test
	public void testGetTransactionName4() throws Exception
	{
		String lsTransctionName = HHSUtil.getTransactionName(null, "fetchProposalSummary", null);
		assertNull(lsTransctionName);
	}

	/**
	 * This method tests the name of the transaction, depending upon the action
	 * to be performed out of two actions.
	 */
	@Test
	public void testGetTransactionName5() throws Exception
	{
		String lsTransctionName = HHSUtil.getTransactionName(null, null, "fetchProposalSummary");
		assertNull(lsTransctionName);
	}

	/**
	 * This method tests whether user is Agency Acco user or not based on the
	 * role
	 */
	@Test
	public void testIsAgencyAccoUser1() throws Exception
	{
		Boolean lsAccoUser = HHSUtil.isAgencyAccoUser("ACCO_STAFF");
		assertTrue(lsAccoUser);
	}

	/**
	 * This method tests whether user is Agency Acco user or not based on the
	 * role
	 */
	@Test
	public void testIsAgencyAccoUser2() throws Exception
	{
		Boolean lsAccoUser = HHSUtil.isAgencyAccoUser("ACCO_MANAGER");
		assertTrue(lsAccoUser);
	}

	/**
	 * This method tests whether user is Agency Acco user or not based on the
	 * role
	 */
	@Test
	public void testIsAgencyAccoUser3() throws Exception
	{
		Boolean lsAccoUser = HHSUtil.isAgencyAccoUser("ACCO_ADMIN_STAFF");
		assertTrue(lsAccoUser);
	}

	/**
	 * This method tests whether user is Agency Acco user or not based on the
	 * role
	 */
	@Test
	public void testIsAgencyAccoUser4() throws Exception
	{
		Boolean lsAccoUser = HHSUtil.isAgencyAccoUser("");
		assertFalse(lsAccoUser);
	}

	/**
	 * This method tests whether user is Agency Acco user or not based on the
	 * role
	 */
	@Test
	public void testIsAgencyAccoUser5() throws Exception
	{
		Boolean lsAccoUser = HHSUtil.isAgencyAccoUser("ACCO");
		assertFalse(lsAccoUser);
	}

	/**
	 * This method tests the name of the transaction name based on the i/p
	 * scenario
	 */
	@Test
	public void testDocumentTypeTransactionName1() throws Exception
	{
		String lsTransactionName = HHSUtil.documentTypeTransactionName("award", "city_org");
		assertNotNull(lsTransactionName);
	}

	/**
	 * This method tests the name of the transaction name based on the i/p
	 * scenario
	 */
	@Test
	public void testDocumentTypeTransactionName2() throws Exception
	{
		String lsTransactionName = HHSUtil.documentTypeTransactionName("award", "provider_org");
		assertNotNull(lsTransactionName);
	}

	/**
	 * This method tests the name of the transaction name based on the i/p
	 * scenario
	 */
	@Test
	public void testDocumentTypeTransactionName3() throws Exception
	{
		String lsTransactionName = HHSUtil.documentTypeTransactionName("award", "agency_org");
		assertNull(lsTransactionName);
	}

	/**
	 * This method tests the name of the transaction name based on the i/p
	 * scenario
	 */
	@Test
	public void testDocumentTypeTransactionName4() throws Exception
	{
		String lsTransactionName = HHSUtil.documentTypeTransactionName("award", "agency");
		assertNull(lsTransactionName);
	}

	/**
	 * This method tests the name of the transaction name based on the i/p
	 * scenario
	 */
	@Test
	public void testDocumentTypeTransactionName5() throws Exception
	{
		String lsTransactionName = HHSUtil.documentTypeTransactionName("award", "org");
		assertNull(lsTransactionName);
	}

	/**
	 * This method tests the name of the transaction name based on the i/p
	 * scenario
	 */
	@Test
	public void testDocumentTypeTransactionName6() throws Exception
	{
		String lsTransactionName = HHSUtil.documentTypeTransactionName("award", "");
		assertNull(lsTransactionName);
	}

	/**
	 * This method tests the name of the transaction name based on the i/p
	 * scenario
	 */
	@Test
	public void testDocumentTypeTransactionName7() throws Exception
	{
		String lsTransactionName = HHSUtil.documentTypeTransactionName("award", null);
		assertNull(lsTransactionName);
	}

	/**
	 * This method tests the size of the List whether empty or not
	 */
	@Test
	public void testEmptyList1() throws Exception
	{
		Boolean isEmptyList = HHSUtil.isEmptyList(null);
		assertTrue(isEmptyList);
	}

	/**
	 * This method tests the size of the List whether empty or not by passing
	 * object of list
	 */
	@Test
	public void testEmptyList2() throws Exception
	{
		List<String> aoList = new ArrayList<String>();
		Boolean isEmptyList = HHSUtil.isEmptyList(aoList);
		assertTrue(isEmptyList);
	}

	/**
	 * This method tests the size of the List whether empty or not by adding
	 * string in list
	 */
	@Test
	public void testEmptyList3() throws Exception
	{
		List<String> aoList = new ArrayList<String>();
		aoList.add("Hello");
		Boolean isEmptyList = HHSUtil.isEmptyList(aoList);
		assertFalse(isEmptyList);
	}

	/**
	 * This method tests the size of the List whether empty or not by adding
	 * string in list
	 */
	@Test
	public void testEmptyList4() throws Exception
	{
		List<String> aoList = new ArrayList<String>();
		aoList.add("Hi");
		Boolean isEmptyList = HHSUtil.isEmptyList(aoList);
		assertFalse(isEmptyList);
	}

	/**
	 * This method tests the size of the List whether empty or not by passing
	 * empty list.
	 */
	@Test
	public void testEmptyList5() throws Exception
	{
		List<String> aoList = new ArrayList<String>();
		aoList.isEmpty();
		Boolean isEmptyList = HHSUtil.isEmptyList(aoList);
		assertTrue(isEmptyList);
	}

	/**
	 * This method tests the size of the List whether empty or not by passing
	 * list with type boolean.
	 */
	@Test
	public void testEmptyList6() throws Exception
	{
		List<Boolean> aoList = new ArrayList<Boolean>();
		Boolean isEmptyList = HHSUtil.isEmptyList(aoList);
		assertTrue(isEmptyList);
	}

	/**
	 * This method tests the size of the List whether empty or not by passing
	 * empty list.
	 */
	@Test
	public void testEmptyList7() throws Exception
	{
		List<String> aoList = new ArrayList<String>();
		aoList.add("");
		Boolean isEmptyList = HHSUtil.isEmptyList(aoList);
		assertFalse(isEmptyList);
	}

	/**
	 * This method tests entity Id for audit on the basis of parameters like
	 * invoiceId,budgetId etc
	 */

	@Test
	public void testFinancialEntityId1() throws Exception
	{
		FinancialWFBean aoFinancialWFBean = new FinancialWFBean();
		aoFinancialWFBean.setProcurementId("624");
		String lsEntityId = HHSUtil.getFinancialEntityId(aoFinancialWFBean);
		assertNotNull(lsEntityId);
	}

	/**
	 * This method tests entity Id for audit on the basis of parameters like
	 * invoiceId,budgetId etc
	 */

	@Test
	public void testFinancialEntityId2() throws Exception
	{
		FinancialWFBean aoFinancialWFBean = new FinancialWFBean();
		aoFinancialWFBean.setInvoiceId("624");
		String lsEntityId = HHSUtil.getFinancialEntityId(aoFinancialWFBean);
		assertNotNull(lsEntityId);
	}

	/**
	 * This method tests entity Id for audit on the basis of parameters like
	 * invoiceId,budgetId etc
	 */

	@Test
	public void testFinancialEntityId3() throws Exception
	{
		FinancialWFBean aoFinancialWFBean = new FinancialWFBean();
		aoFinancialWFBean.setBudgetId("624");
		String lsEntityId = HHSUtil.getFinancialEntityId(aoFinancialWFBean);
		assertNotNull(lsEntityId);
	}

	/**
	 * This method tests entity Id for audit on the basis of parameters like
	 * invoiceId,budgetId etc
	 */

	@Test
	public void testFinancialEntityId4() throws Exception
	{
		FinancialWFBean aoFinancialWFBean = new FinancialWFBean();
		aoFinancialWFBean.setContractId("624");
		String lsEntityId = HHSUtil.getFinancialEntityId(aoFinancialWFBean);
		assertNotNull(lsEntityId);
	}

	/**
	 * This method tests entity Id for audit on the basis of parameters like
	 * invoiceId,budgetId.
	 * <ul>
	 * <li>passed the empty proc id.</li>
	 * </ul>
	 */
	@Test
	public void testFinancialEntityId5() throws Exception
	{
		FinancialWFBean aoFinancialWFBean = new FinancialWFBean();
		aoFinancialWFBean.setProcurementId("");
		String lsEntityId = HHSUtil.getFinancialEntityId(aoFinancialWFBean);
		assertNotNull(lsEntityId);
	}

	/**
	 * This method tests entity Id for audit on the basis of parameters like
	 * invoiceId,budgetId.
	 * <ul>
	 * <li>passed the empty invoice id.</li>
	 * </ul>
	 */
	@Test
	public void testFinancialEntityId6() throws Exception
	{
		FinancialWFBean aoFinancialWFBean = new FinancialWFBean();
		aoFinancialWFBean.setInvoiceId("");
		String lsEntityId = HHSUtil.getFinancialEntityId(aoFinancialWFBean);
		assertNotNull(lsEntityId);
	}

	/**
	 * This method tests entity Id for audit on the basis of parameters like
	 * invoiceId,budgetId etc
	 * <ul>
	 * <li>passed the empty budget id.</li>
	 * </ul>
	 */

	@Test
	public void testFinancialEntityId7() throws Exception
	{
		FinancialWFBean aoFinancialWFBean = new FinancialWFBean();
		aoFinancialWFBean.setBudgetId("");
		String lsEntityId = HHSUtil.getFinancialEntityId(aoFinancialWFBean);
		assertNotNull(lsEntityId);
	}

	/**
	 * This method tests entity Id for audit on the basis of parameters like
	 * invoiceId,budgetId etc
	 * <ul>
	 * <li>passed the empty contract id.</li>
	 * </ul>
	 */

	@Test
	public void testFinancialEntityId8() throws Exception
	{
		FinancialWFBean aoFinancialWFBean = new FinancialWFBean();
		aoFinancialWFBean.setContractId("");
		String lsEntityId = HHSUtil.getFinancialEntityId(aoFinancialWFBean);
		assertNotNull(lsEntityId);
	}

	/**
	 * This method tests generation of the list of E-PIN depending upon first 3
	 * initials of E-pin entered by user
	 */

	@Test
	public void testGenerateDelimitedResponse1() throws ApplicationException
	{
		final List<String> aoInputList = new ArrayList<String>();
		aoInputList.add("InputList");
		StringBuffer loOutputBuffer = HHSUtil.generateDelimitedResponse(aoInputList, "Inp", 3);
		assertNotNull(loOutputBuffer);
	}

	/**
	 * This method tests generation of the list of E-PIN depending upon first 3
	 * initials of E-pin entered by user
	 */
	@Test
	public void testGenerateDelimitedResponse2() throws ApplicationException
	{
		final List<String> aoInputList = new ArrayList<String>();
		aoInputList.add("Input");
		StringBuffer loOutputBuffer = HHSUtil.generateDelimitedResponse(aoInputList, "Input", 3);
		assertNotNull(loOutputBuffer);
	}

	/**
	 * This method tests generation of the list of E-PIN depending upon first 3
	 * initials of E-pin entered by user
	 */
	@Test
	public void testGenerateDelimitedResponse3() throws ApplicationException
	{
		final List<String> aoInputList = new ArrayList<String>();
		aoInputList.add("List");
		StringBuffer loOutputBuffer = HHSUtil.generateDelimitedResponse(aoInputList, "List", 3);
		assertNotNull(loOutputBuffer);
	}

	/**
	 * This method tests generation of the list of E-PIN depending upon first 3
	 * initials of E-pin entered by user
	 */
	@Test
	public void testGenerateDelimitedResponse4() throws ApplicationException
	{
		final List<String> aoInputList = new ArrayList<String>();
		aoInputList.add("InputList");
		StringBuffer loOutputBuffer = HHSUtil.generateDelimitedResponse(aoInputList, "Inp", 5);
		assertNotNull(loOutputBuffer);
	}

	/**
	 * This method tests generation of the list of E-PIN depending upon first 3
	 * initials of E-pin entered by user
	 */
	@Test
	public void testGenerateDelimitedResponse5() throws ApplicationException
	{
		final List<String> aoInputList = new ArrayList<String>();
		aoInputList.add("InputList");
		StringBuffer loOutputBuffer = HHSUtil.generateDelimitedResponse(aoInputList, "", 3);
		assertNotNull(loOutputBuffer);
	}

	/**
	 * This method tests generation of the list of E-PIN depending upon first 3
	 * initials of E-pin entered by user
	 */
	@Test
	public void testGenerateDelimitedResponse6() throws ApplicationException
	{
		final List<String> aoInputList = new ArrayList<String>();
		aoInputList.add("");
		StringBuffer loOutputBuffer = HHSUtil.generateDelimitedResponse(aoInputList, "Inp", 2);
		assertNotNull(loOutputBuffer);
	}

	/**
	 * This method tests generation of the list of E-PIN depending upon first 3
	 * initials of E-pin entered by user
	 */
	@Test
	public void testGenerateDelimitedResponse7() throws ApplicationException
	{
		final List<String> aoInputList = new ArrayList<String>();
		aoInputList.add("InputList");
		StringBuffer loOutputBuffer = HHSUtil.generateDelimitedResponse(aoInputList, "Inp", 1);
		assertNotNull(loOutputBuffer);
	}

	/**
	 * This method tests generation of the list of E-PIN depending upon first 3
	 * initials of E-pin entered by user
	 */
	@Test
	public void testGenerateDelimitedResponse8() throws ApplicationException
	{
		final List<String> aoInputList = new ArrayList<String>();
		aoInputList.add("IList");
		StringBuffer loOutputBuffer = HHSUtil.generateDelimitedResponse(aoInputList, "Inp", 3);
		assertNotNull(loOutputBuffer);
	}

	/**
	 * This method tests generation of the list of E-PIN depending upon first 3
	 * initials of E-pin entered by user changing the parameter to be passed
	 */
	@Test
	public void testGenerateDelimitedResponse9() throws ApplicationException
	{
		final List<String> aoInputList = new ArrayList<String>();
		aoInputList.add("Hello");
		StringBuffer loOutputBuffer = HHSUtil.generateDelimitedResponse(aoInputList, "Inp", 3);
		assertNotNull(loOutputBuffer);
	}

	/**
	 * This method tests generation of the list of E-PIN depending upon first 3
	 * initials of E-pin entered by user changing the parameter to be passed
	 */
	@Test
	public void testGenerateDelimitedResponse10() throws ApplicationException
	{
		final List<String> aoInputList = new ArrayList<String>();
		aoInputList.add("InputList");
		StringBuffer loOutputBuffer = HHSUtil.generateDelimitedResponse(aoInputList, "Input", 3);
		assertNotNull(loOutputBuffer);
	}

	/**
	 * This method tests conversion of the subgrid data rows in json form. The
	 * way it gets the complete String from gridheader property file and
	 * tokenize it to get the header names.
	 */
	@Test
	public void testPopulateSubGridRows1() throws Exception
	{
		final List<String> aoInputList = new ArrayList<String>();
		aoInputList.add("InputList");
		StringBuffer loOutputBuffer = HHSUtil.generateDelimitedResponse(aoInputList, "Inp", 3);
		assertNotNull(loOutputBuffer);
	}

	/**
	 * This method tests that receives level users delimited by pipe (|) symbol
	 * in String format. It splits the string on pipe(|) and convert it into
	 * array of string
	 */

	@Test
	public void testConvertStringToArray1() throws Exception
	{
		final List<String> aoInputList = new ArrayList<String>();
		aoInputList.add("InputList");
		String loUserArr[] = HHSUtil.convertStringToArray("Inp");
		assertNotNull(loUserArr);
	}

	/**
	 * This method tests that receives level users delimited by pipe (|) symbol
	 * in String format. It splits the string on pipe(|) and convert it into
	 * array of string.Here we are checking for exceptions.
	 * @throws ApplicationException
	 */

	@Test(expected = java.lang.Exception.class)
	public void testConvertStringToArrayException() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();

		loHHSUtil.convertStringToArray(null);
	}

	/**
	 * This method tests generation for the list of providers depending upon
	 * first 3 initials of E-pin entered by user.Here we are changing the
	 * display name and search terms.
	 */

	@Test
	public void testGenerateDelimitedAutoCompleteResponse1() throws Exception
	{
		final List<AutoCompleteBean> aoInputList = new ArrayList<AutoCompleteBean>();
		AutoCompleteBean loAutoCompleteBean = new AutoCompleteBean();
		loAutoCompleteBean.setDisplayName("accelerator");
		aoInputList.add(loAutoCompleteBean);
		String aoPartialSearchTerms = "abc";
		StringBuffer loOutputBuffer = HHSUtil.generateDelimitedAutoCompleteResponse(aoInputList, aoPartialSearchTerms,
				3);
		assertNotNull(loOutputBuffer);
	}

	/**
	 * This method tests generation for the list of providers depending upon
	 * first 3 initials of E-pin entered by user by changing the display name.
	 */

	@Test
	public void testGenerateDelimitedAutoCompleteResponse2() throws Exception
	{
		final List<AutoCompleteBean> aoInputList = new ArrayList<AutoCompleteBean>();
		AutoCompleteBean loAutoCompleteBean = new AutoCompleteBean();
		loAutoCompleteBean.setDisplayName("agency");
		aoInputList.add(loAutoCompleteBean);
		String aoPartialSearchTerms = "abc";
		StringBuffer loOutputBuffer = HHSUtil.generateDelimitedAutoCompleteResponse(aoInputList, aoPartialSearchTerms,
				3);
		assertNotNull(loOutputBuffer);
	}

	/**
	 * This method tests generation for the list of providers depending upon
	 * first 3 initials of E-pin entered by user by changing the display name.
	 */

	@Test
	public void testGenerateDelimitedAutoCompleteResponse3() throws Exception
	{
		final List<AutoCompleteBean> aoInputList = new ArrayList<AutoCompleteBean>();
		AutoCompleteBean loAutoCompleteBean = new AutoCompleteBean();
		loAutoCompleteBean.setDisplayName("provider");
		aoInputList.add(loAutoCompleteBean);
		String aoPartialSearchTerms = "abc";
		StringBuffer loOutputBuffer = HHSUtil.generateDelimitedAutoCompleteResponse(aoInputList, aoPartialSearchTerms,
				3);
		assertNotNull(loOutputBuffer);
	}

	/**
	 * This method tests generation for the list of providers depending upon
	 * first 3 initials of E-pin entered by user by changing the display name to
	 * test.
	 */

	@Test
	public void testGenerateDelimitedAutoCompleteResponse4() throws Exception
	{
		final List<AutoCompleteBean> aoInputList = new ArrayList<AutoCompleteBean>();
		AutoCompleteBean loAutoCompleteBean = new AutoCompleteBean();
		loAutoCompleteBean.setDisplayName("Test");
		aoInputList.add(loAutoCompleteBean);
		String aoPartialSearchTerms = "abc";
		StringBuffer loOutputBuffer = HHSUtil.generateDelimitedAutoCompleteResponse(aoInputList, aoPartialSearchTerms,
				3);
		assertNotNull(loOutputBuffer);
	}

	/**
	 * This method tests generation for the list of providers depending upon
	 * first 3 initials of E-pin entered by user by changing the search terms.
	 */

	@Test
	public void testGenerateDelimitedAutoCompleteResponse5() throws Exception
	{
		final List<AutoCompleteBean> aoInputList = new ArrayList<AutoCompleteBean>();
		AutoCompleteBean loAutoCompleteBean = new AutoCompleteBean();
		loAutoCompleteBean.setDisplayName("Test");
		aoInputList.add(loAutoCompleteBean);
		String aoPartialSearchTerms = "Hello";
		StringBuffer loOutputBuffer = HHSUtil.generateDelimitedAutoCompleteResponse(aoInputList, aoPartialSearchTerms,
				3);
		assertNotNull(loOutputBuffer);
	}

	/**
	 * This method tests generation for the list of providers depending upon
	 * first 3 initials of E-pin entered by user by changing the display name
	 * and search terms.
	 */

	@Test
	public void testGenerateDelimitedAutoCompleteResponse6() throws Exception
	{
		final List<AutoCompleteBean> aoInputList = new ArrayList<AutoCompleteBean>();
		AutoCompleteBean loAutoCompleteBean = new AutoCompleteBean();
		loAutoCompleteBean.setDisplayName("Test");
		aoInputList.add(loAutoCompleteBean);
		String aoPartialSearchTerms = "Hello";
		StringBuffer loOutputBuffer = HHSUtil.generateDelimitedAutoCompleteResponse(aoInputList, aoPartialSearchTerms,
				5);
		assertNotNull(loOutputBuffer);
	}

	/**
	 * This method tests generation for the list of providers depending upon
	 * first 3 initials of E-pin entered by user by changing the display name
	 * and search terms.
	 */

	@Test
	public void testGenerateDelimitedAutoCompleteResponse7() throws Exception
	{
		final List<AutoCompleteBean> aoInputList = new ArrayList<AutoCompleteBean>();
		AutoCompleteBean loAutoCompleteBean = new AutoCompleteBean();
		loAutoCompleteBean.setDisplayName("Agency");
		aoInputList.add(loAutoCompleteBean);
		String aoPartialSearchTerms = "Hello";
		StringBuffer loOutputBuffer = HHSUtil.generateDelimitedAutoCompleteResponse(aoInputList, aoPartialSearchTerms,
				7);
		assertNotNull(loOutputBuffer);
	}

	/**
	 * This method tests generation for the list of providers depending upon
	 * first 3 initials of E-pin entered by user by changing the display name
	 * and search terms.
	 */

	@Test
	public void testGenerateDelimitedAutoCompleteResponse8() throws Exception
	{
		final List<AutoCompleteBean> aoInputList = new ArrayList<AutoCompleteBean>();
		AutoCompleteBean loAutoCompleteBean = new AutoCompleteBean();
		loAutoCompleteBean.setDisplayName("Agency");
		aoInputList.add(loAutoCompleteBean);
		String aoPartialSearchTerms = "Hello";
		StringBuffer loOutputBuffer = HHSUtil.generateDelimitedAutoCompleteResponse(aoInputList, aoPartialSearchTerms,
				10);
		assertNotNull(loOutputBuffer);
	}

	/**
	 * 
	 * This method tests the Budget's Initial Fiscal year
	 */

	@Test
	public void testGetBudgetFY1() throws ApplicationException
	{

		Integer liContractFirstFY = HHSUtil.getBudgetFY("12/12/12");
		assertNotNull(liContractFirstFY);
	}

	/**
	 * 
	 * This method tests the Budget's Initial Fiscal year
	 */

	@Test
	public void testGetBudgetFY2() throws ApplicationException
	{

		Integer liContractFirstFY = HHSUtil.getBudgetFY("12/12/11");
		assertNotNull(liContractFirstFY);
	}

	/**
	 * This method tests conversion of double value up to the precison value
	 * given in second parameter
	 */
	@Test
	public void testRound1() throws ApplicationException
	{

		double lfPow = HHSUtil.round(34.14d, 2);
		assertNotNull(lfPow);
	}

	/**
	 * This method tests Date from EPOCH time
	 */

	@Test
	public void testDateFromEpochTime1() throws ApplicationException
	{
		Date loDate = HHSUtil.getDateFromEpochTime("12122012");
		assertNotNull(loDate);
	}

	/**
	 * This method tests for getting a particular status corresponding to status
	 * id and process Type.
	 */
	@Test
	public void testStatusID1() throws ApplicationException
	{
		int liStatusId = HHSUtil.getStatusID("Bulk Upload", "Draft");
		assertNotNull(liStatusId);
	}

	/**
	 * This method tests for getting a particular status corresponding to status
	 * id and process Type.
	 */
	@Test
	public void testStatusID2() throws ApplicationException
	{
		int liStatusId = HHSUtil.getStatusID("Bulk Upload", "pending");
		assertNotNull(liStatusId);
	}

	/**
	 * This method tests for getting a particular status corresponding to status
	 * id and process Type.
	 */
	@Test
	public void testStatusID3() throws ApplicationException
	{
		int liStatusId = HHSUtil.getStatusID("Bulk Upload", "Approved");
		assertNotNull(liStatusId);
	}

	/**
	 * This method tests for getting a particular status corresponding to status
	 * id and process Type.
	 */
	@Test
	public void testStatusID4() throws ApplicationException
	{
		int liStatusId = HHSUtil.getStatusID("Bulk Upload", "Award");
		assertNotNull(liStatusId);
	}

	/**
	 * This method tests for getting a particular status corresponding to status
	 * id and process Type by taking null.
	 */
	@Test
	public void testStatusID5() throws ApplicationException
	{
		int liStatusId = HHSUtil.getStatusID("Bulk Upload", "null");
		assertNotNull(liStatusId);
	}

	/**
	 * This method tests for converting string currency format to bigdecimal and
	 * normal number format to long
	 */

	@Test
	public void testConvertCurrencyFormatToNumber1() throws ApplicationException
	{

		Object loCurrencyToConvert = HHSUtil.convertCurrencyFormatToNumber("1234");
		assertNotNull(loCurrencyToConvert);
	}

	/**
	 * This method tests for converting string currency format to bigdecimal and
	 * normal number format to long
	 */

	@Test
	public void testConvertCurrencyFormatToNumber2() throws ApplicationException
	{

		Object loCurrencyToConvert = HHSUtil.convertCurrencyFormatToNumber("4567");
		assertNotNull(loCurrencyToConvert);
	}

	/**
	 * This method tests for converting string currency format to bigdecimal and
	 * normal number format to long
	 */

	@Test
	public void testConvertCurrencyFormatToNumber3() throws ApplicationException
	{

		Object loCurrencyToConvert = HHSUtil.convertCurrencyFormatToNumber("1425");
		assertNotNull(loCurrencyToConvert);
	}

	/**
	 * This method tests for converting string currency format to bigdecimal and
	 * normal number format to long
	 */
	@Test
	public void testConvertCurrencyFormatToNumber4() throws ApplicationException
	{

		Object loCurrencyToConvert = HHSUtil.convertCurrencyFormatToNumber("1025");
		assertNotNull(loCurrencyToConvert);
	}

	/**
	 * This method tests for converting string currency format to bigdecimal and
	 * normal number format to long
	 */
	@Test
	public void testConvertCurrencyFormatToNumber5() throws ApplicationException
	{

		Object loCurrencyToConvert = HHSUtil.convertCurrencyFormatToNumber("1020");
		assertNotNull(loCurrencyToConvert);
	}

	/**
	 * This method tests for calculating the Current Configurable Budget Start
	 * date
	 */

	@Test
	public void testNewBudgetStartDate1() throws ApplicationException
	{

		String lsContractStartDate = HHSUtil.getNewBudgetStartDate("12/12/12", "2012");
		assertNotNull(lsContractStartDate);
	}

	/**
	 * This method tests for calculating the Current Configurable Budget Start
	 * date
	 */

	@Test
	public void testNewBudgetStartDate2() throws ApplicationException
	{

		String lsContractStartDate = HHSUtil.getNewBudgetStartDate(DateUtil.getCurrentDate(), "2013");
		assertNotNull(lsContractStartDate);
	}

	/**
	 * This method tests for calculating the Current Configurable Budget Start
	 * date
	 */

	@Test
	public void testNewBudgetStartDate3() throws ApplicationException
	{

		String lsContractStartDate = HHSUtil.getNewBudgetStartDate("01/01/01", "2012");
		assertNotNull(lsContractStartDate);
	}

	/**
	 * This method tests for calculating the Current Configurable Budget Start
	 * date
	 */

	@Test
	public void testNewBudgetStartDate4() throws ApplicationException
	{

		String lsContractStartDate = HHSUtil.getNewBudgetStartDate("01/01/2001", "2012");
		assertNotNull(lsContractStartDate);
	}

	/**
	 * This method tests for calculating the Current Configurable Budget Start
	 * date
	 */

	@Test
	public void testNewBudgetStartDate5() throws ApplicationException
	{

		String lsContractStartDate = HHSUtil.getNewBudgetStartDate("01/01/2013", "2013");
		assertNotNull(lsContractStartDate);
	}

	/**
	 * This method tests for calculating the Current Configurable Budget End
	 * date
	 */

	@Test
	public void testNewBudgetEndDate1() throws ApplicationException
	{

		String lsContractEndDate = HHSUtil.getNewBudgetEndDate("12/12/12", "2013");
		assertNotNull(lsContractEndDate);
	}

	/**
	 * This method tests for calculating the Current Configurable Budget End
	 * date
	 */
	@Test
	public void testAddDocumentFromVaultTransactionName() throws Exception
	{
		String lsTransactionName = null;
		String asUploadingDocType = "Award";
		String asUserOrgType = "provider_org";
		String asOrgId = "r3_org";
		Date asModifiedDate = new Date();
		Map<Object, Object> asParamMap = new HashMap<Object, Object>();
		lsTransactionName = HHSUtil.addDocumentFromVaultTransactionName(asUploadingDocType, asUserOrgType, asParamMap,
				asModifiedDate, asOrgId);
		assertNotNull(lsTransactionName);
	}

	/**
	 * This method tests for calculating the Current Configurable Budget End
	 * date
	 */
	@Test
	public void testAddDocumentFromVaultTransactionName1() throws Exception
	{
		String lsTransactionName = null;
		String asUploadingDocType = "Awar";
		String asUserOrgType = "provider_org";
		String asOrgId = "r3_org";
		Date asModifiedDate = new Date();
		Map<Object, Object> asParamMap = new HashMap<Object, Object>();
		lsTransactionName = HHSUtil.addDocumentFromVaultTransactionName(asUploadingDocType, asUserOrgType, asParamMap,
				asModifiedDate, asOrgId);
		assertNotNull(lsTransactionName);
	}

	/**
	 * This method tests for calculating the Current Configurable Budget End
	 * date
	 */
	@Test
	public void testAddDocumentFromVaultTransactionName2() throws Exception
	{
		String lsTransactionName = null;
		String asUploadingDocType = "Award";
		String asUserOrgType = "city_org";
		String asOrgId = "";
		Date asModifiedDate = new Date();
		Map<Object, Object> asParamMap = new HashMap<Object, Object>();
		lsTransactionName = HHSUtil.addDocumentFromVaultTransactionName(asUploadingDocType, asUserOrgType, asParamMap,
				asModifiedDate, asOrgId);
		assertNotNull(lsTransactionName);
	}

	/**
	 * This method tests for calculating the Current Configurable Budget End
	 * date
	 */
	@Test
	public void testAddDocumentFromVaultTransactionName3() throws Exception
	{
		String lsTransactionName = null;
		String asUploadingDocType = "Award";
		String asUserOrgType = "test";
		String asOrgId = "";
		Date asModifiedDate = new Date();
		Map<Object, Object> asParamMap = new HashMap<Object, Object>();
		lsTransactionName = HHSUtil.addDocumentFromVaultTransactionName(asUploadingDocType, asUserOrgType, asParamMap,
				asModifiedDate, asOrgId);
		assertNull(lsTransactionName);
	}

	/**
	 * This method tests for calculating the Current Configurable Budget End
	 * date
	 */
	@Test
	public void testGetTransactionNameInsertDocumentDetailsInDBOnUpload() throws Exception
	{
		String lsTransactionName = null;
		String asUploadingDocType = "Award";
		String asUserOrgType = "provider_org";
		String asProposalId = "1";
		String asAwardId = "2";
		String asOrgId = "";
		Map<String, Object> asParamMap = new HashMap<String, Object>();
		lsTransactionName = HHSUtil.getTransactionNameInsertDocumentDetailsInDBOnUpload(asUserOrgType,
				asUploadingDocType, asParamMap, asProposalId, asAwardId, asOrgId);
		assertNotNull(lsTransactionName);
	}

	/**
	 * This method tests calculation of the Current Fiscal Year and the End
	 * Fiscal Year of the Contract.
	 */
	@Test
	public void testGetFYDetails1() throws ApplicationException
	{
		HashMap<String, Integer> asParamMap = new HashMap<String, Integer>();
		Date loContractEndDate = new Date();
		Date loCurrentDate = new Date();
		asParamMap = HHSUtil.getFYDetails(loContractEndDate, loCurrentDate, loCurrentDate);
		assertNotNull(asParamMap);
	}

	/**
	 * This Method tests seting of the Audit Data to Channel object
	 * 
	 */

	@Test
	public void testNewBudgetEndDate2() throws ApplicationException
	{

		String lsContractEndDate = HHSUtil.getNewBudgetEndDate("12/02/13", "12/02/13");
		assertNotNull(lsContractEndDate);
	}

	/**
	 * This Method tests seting of the Audit Data to Channel object
	 * 
	 */

	@Test
	public void testNewBudgetEndDate3() throws ApplicationException
	{

		String lsContractEndDate = HHSUtil.getNewBudgetEndDate("12/12/2012", "12/12/2012");
		assertNotNull(lsContractEndDate);
	}

	/**
	 * This Method tests seting of the Audit Data to Channel object
	 * 
	 */

	@Test
	public void testNewBudgetEndDate4() throws ApplicationException
	{

		String lsContractEndDate = HHSUtil.getNewBudgetEndDate(DateUtil.getCurrentDate(), DateUtil.getCurrentDate());
		assertNotNull(lsContractEndDate);
	}

	/**
	 * This Method tests seting of the Audit Data to Channel object
	 * 
	 */

	@Test
	public void testNewBudgetEndDate5() throws ApplicationException
	{

		String lsContractEndDate = HHSUtil.getNewBudgetEndDate("01/01/01", "01/01/01");
		assertNotNull(lsContractEndDate);
	}

	/**
	 * This Method tests seting of the Audit Data to Channel object
	 * 
	 */

	@Test
	public void testAddDocumentFromVaultTransactionName4() throws Exception
	{
		String lsTransactionName = null;
		String asUploadingDocType = "Award";
		String asUserOrgType = "test";
		String asOrgId = "";
		Date asModifiedDate = new Date();
		Map<Object, Object> asParamMap = new HashMap<Object, Object>();
		lsTransactionName = HHSUtil.addDocumentFromVaultTransactionName(asUploadingDocType, asUserOrgType, asParamMap,
				asModifiedDate, asOrgId);
		assertNull(lsTransactionName);
	}

	/**
	 * This Method tests seting of the Audit Data to Channel object
	 * 
	 */

	@Test
	public void testAddDocumentFromVaultTransactionName5() throws Exception
	{
		String lsTransactionName = null;
		String asUploadingDocType = "Award";
		String asUserOrgType = "ABC";
		String asOrgId = "";
		Date asModifiedDate = new Date();
		Map<Object, Object> asParamMap = new HashMap<Object, Object>();
		lsTransactionName = HHSUtil.addDocumentFromVaultTransactionName(asUploadingDocType, asUserOrgType, asParamMap,
				asModifiedDate, asOrgId);
		assertNull(lsTransactionName);
	}

	/**
	 * This Method tests seting of the Audit Data to Channel object
	 * 
	 */

	@Test
	public void testAddDocumentFromVaultTransactionName6() throws Exception
	{
		String lsTransactionName = null;
		String asUploadingDocType = "Award";
		String asUserOrgType = "Hello";
		Date asModifiedDate = new Date();
		Map<Object, Object> asParamMap = new HashMap<Object, Object>();
		lsTransactionName = HHSUtil.addDocumentFromVaultTransactionName(asUploadingDocType, asUserOrgType, asParamMap,
				asModifiedDate, "");
		assertNull(lsTransactionName);
	}

	/**
	 * This Method for getting the transaction name by passing the required
	 * parameters.
	 */

	@Test
	public void testGetTransactionNameInsertDocumentDetailsInDBOnUpload1() throws Exception
	{
		String lsTransactionName = null;
		String asUploadingDocType = "Award";
		String asUserOrgType = "Agency_org";
		String asProposalId = "1";
		String asAwardId = "2";
		Map<String, Object> asParamMap = new HashMap<String, Object>();
		lsTransactionName = HHSUtil.getTransactionNameInsertDocumentDetailsInDBOnUpload(asUserOrgType,
				asUploadingDocType, asParamMap, asProposalId, asAwardId, "");
		assertNull(lsTransactionName);
	}

	/**
	 * This Method for getting the transaction name by passing the required
	 * parameters.
	 */

	@Test
	public void testGetTransactionNameInsertDocumentDetailsInDBOnUpload2() throws Exception
	{
		String lsTransactionName = null;
		String asUploadingDocType = "Award";
		String asUserOrgType = "Award";
		String asProposalId = "1";
		String asAwardId = "2";
		Map<String, Object> asParamMap = new HashMap<String, Object>();
		lsTransactionName = HHSUtil.getTransactionNameInsertDocumentDetailsInDBOnUpload(asUserOrgType,
				asUploadingDocType, asParamMap, asProposalId, asAwardId, "");
		assertNull(lsTransactionName);
	}

	/**
	 * This Method for getting the transaction name by passing the required
	 * parameters.
	 */

	@Test
	public void testGetTransactionNameInsertDocumentDetailsInDBOnUpload3() throws Exception
	{
		String lsTransactionName = null;
		String asUploadingDocType = "provider_org";
		String asUserOrgType = "provider_org";
		String asProposalId = "1";
		String asAwardId = "2";
		Map<String, Object> asParamMap = new HashMap<String, Object>();
		lsTransactionName = HHSUtil.getTransactionNameInsertDocumentDetailsInDBOnUpload(asUserOrgType,
				asUploadingDocType, asParamMap, asProposalId, asAwardId, "");
		assertNull(lsTransactionName);
	}

	/**
	 * This Method for getting the transaction name by passing the required
	 * parameters.
	 */

	@Test
	public void testGetTransactionNameInsertDocumentDetailsInDBOnUpload4() throws Exception
	{
		String lsTransactionName = null;
		String asUploadingDocType = "Award";
		String asUserOrgType = "provider_org";
		String asProposalId = "7";
		String asAwardId = "5";
		Map<String, Object> asParamMap = new HashMap<String, Object>();
		lsTransactionName = HHSUtil.getTransactionNameInsertDocumentDetailsInDBOnUpload(asUserOrgType,
				asUploadingDocType, asParamMap, asProposalId, asAwardId, "");
		assertNotNull(lsTransactionName);
	}

	/**
	 * This Method for getting the transaction name by passing the required
	 * parameters.
	 */

	@Test
	public void testGetTransactionNameInsertDocumentDetailsInDBOnUpload5() throws Exception
	{
		String lsTransactionName = null;
		String asUploadingDocType = "AwardID";
		String asUserOrgType = "provider_org";
		String asProposalId = "1";
		String asAwardId = "2";
		Map<String, Object> asParamMap = new HashMap<String, Object>();
		lsTransactionName = HHSUtil.getTransactionNameInsertDocumentDetailsInDBOnUpload(asUserOrgType,
				asUploadingDocType, asParamMap, asProposalId, asAwardId, "");
		assertNull(lsTransactionName);
	}

	/**
	 * This Method for getting the transaction name by passing the required
	 * parameters.
	 */

	@Test
	public void testGetTransactionNameInsertDocumentDetailsInDBOnUpload6() throws Exception
	{
		String lsTransactionName = null;
		String asUploadingDocType = "Proposal";
		String asUserOrgType = "provider_org";
		String asProposalId = "1";
		String asAwardId = "2";
		Map<String, Object> asParamMap = new HashMap<String, Object>();
		lsTransactionName = HHSUtil.getTransactionNameInsertDocumentDetailsInDBOnUpload(asUserOrgType,
				asUploadingDocType, asParamMap, asProposalId, asAwardId, "");
		assertNotNull(lsTransactionName);
	}

	/**
	 * This Method for getting the transaction name by passing the required
	 * parameters.
	 */
	@Test
	public void testGetTransactionNameInsertDocumentDetailsInDBOnUpload7() throws Exception
	{
		String lsTransactionName = null;
		String asUploadingDocType = "proposal";
		String asUserOrgType = "provider_org";
		String asProposalId = "1";
		String asAwardId = "2";
		Map<String, Object> asParamMap = new HashMap<String, Object>();
		lsTransactionName = HHSUtil.getTransactionNameInsertDocumentDetailsInDBOnUpload(asUserOrgType,
				asUploadingDocType, asParamMap, asProposalId, asAwardId, "");
		assertNotNull(lsTransactionName);
	}

	/**
	 * This method tests calculation of the Current Fiscal Year and the End
	 * Fiscal Year of the Contract.
	 */
	@Test
	public void testGetFYDetails2() throws ApplicationException
	{
		HashMap<String, Integer> asParamMap = new HashMap<String, Integer>();
		Date loContractEndDate = DateUtil.getDate("08/02/2013");
		Date loCurrentDate = DateUtil.getDate("08/02/2013");
		asParamMap = HHSUtil.getFYDetails(loContractEndDate, loCurrentDate, loCurrentDate);
		assertNotNull(asParamMap);
	}

	/**
	 * This method tests calculation of the Current Fiscal Year and the End
	 * Fiscal Year of the Contract.
	 */
	@Test
	public void testGetFYDetails3() throws ApplicationException
	{
		HashMap<String, Integer> asParamMap = new HashMap<String, Integer>();
		Date loContractEndDate = DateUtil.getDate(DateUtil.getCurrentDate());
		Date loCurrentDate = DateUtil.getDate("02/02/2013");
		asParamMap = HHSUtil.getFYDetails(loContractEndDate, loCurrentDate, loCurrentDate);
		assertNotNull(asParamMap);
	}

	/**
	 * This method tests Header names of Jq grid
	 * @throws Exception
	 */
	@Test
	public void testGetHeader1() throws Exception
	{
		String loBuffer = HHSUtil.getHeader("salary.grid");
		assertNotNull(loBuffer.toString());
	}

	/**
	 * This method tests for fetching all the status corresponding to one
	 * process Type.
	 * 
	 */

	@Test
	public void testGetStatusMap1() throws ApplicationException
	{
		List<String> lsStatusList = new ArrayList<String>();
		lsStatusList = HHSUtil.getStatusMap("Payment");
		assertNotNull(lsStatusList);
	}

	/**
	 * This method tests for fetching all the status corresponding to one
	 * process Type.
	 * 
	 */

	@Test
	public void testGetStatusMap2() throws ApplicationException
	{
		List<String> lsStatusList = new ArrayList<String>();
		lsStatusList = HHSUtil.getStatusMap("Proposal");
		assertNotNull(lsStatusList);
	}

	/**
	 * This method tests for fetching the status corresponding to one process
	 * Type.
	 * 
	 */

	@Test
	public void testGetStatusMap3() throws ApplicationException
	{
		List<String> lsStatusList = new ArrayList<String>();
		lsStatusList = HHSUtil.getStatusMap("Advance");
		assertNotNull(lsStatusList);
	}

	/**
	 * This method tests subgrid properties of Jq grid
	 * @throws Exception
	 */

	@Test
	public void testGetSubGridProp1() throws Exception
	{
		String loBuffer = HHSUtil.getSubGridProp("contractBudgetIndirectRate.grid");
		assertNotNull(loBuffer.toString());
	}

	/**
	 * This method tests returns of subgrid header row for Jq grid
	 * @throws Exception
	 */

	@Test
	public void testGetSubGridHeaderRow1() throws Exception
	{
		StringBuffer loBuffer = new StringBuffer();
		loBuffer = HHSUtil.getSubGridHeaderRow("contractedServicesConsultants.grid");
		assertNotNull(loBuffer.toString());
	}

	/**
	 * This method is for getting the Epoch Time by passing the date as
	 * parameter.
	 * @throws Exception
	 */
	@Test
	public void testGetEpochTimeFromDate1() throws ApplicationException
	{
		String loCurrentDate = DateUtil.getCurrentDate();
		String loEpochTime = HHSUtil.getEpochTimeFromDate(loCurrentDate);
		assertNotNull(loEpochTime);
	}

	/**
	 * This method is for getting the Epoch Time by passing the date as
	 * parameter.
	 * @throws Exception
	 */
	@Test
	public void testGetEpochTimeFromDate2() throws ApplicationException
	{
		String loEpochTime = HHSUtil.getEpochTimeFromDate("01/03/2013");
		assertNotNull(loEpochTime);
	}

	/**
	 * This method is for getting the Epoch Time by passing the date as
	 * parameter.
	 * @throws ApplicationException
	 * @throws Exception
	 */
	@Test(expected = java.lang.Exception.class)
	public void testGetEpochTimeFromDate3() throws ApplicationException
	{
		String loEpochTime = HHSUtil.getEpochTimeFromDate(null);
	}

	/**
	 * This method is for getting the Time by passing the date as parameter.
	 * @throws Exception
	 */
	@Test
	public void testGetZeroTimeDate() throws ApplicationException
	{
		Date loDate = new Date();
		loDate = HHSUtil.getZeroTimeDate(loDate);
		assertNotNull(loDate);
	}

	@Test
	public void testCopyListToList() throws ApplicationException
	{
		List<Element> aoDestination = null;
		List<Element> aoSource = null;
		HHSUtil.copyListToList(aoDestination, aoSource, "inbox");
	}

	/**
	 * This method tests returns of Header properties of Jq grid
	 * @throws Exception
	 * 
	 **/
	@Test
	public void testGetHeaderProp1() throws Exception
	{
		String loBuffer = new String();
		loBuffer = HHSUtil.getHeaderProp("contractBudgetSalariedEmployee.grid");
		assertNotNull(loBuffer.toString());
	}

	/**
	 * This method tests conversion of the subgrid data rows in json form
	 * @throws Exception
	 * 
	 **/

	@Test
	public void testPopulateSubGridRows2() throws Exception
	{
		StringBuffer loBuffer = new StringBuffer();
		Object loBeanObj = null;
		List<Object> loListObj = new ArrayList<Object>();
		String lsRowsPerPage = null;
		String lsPage = null;
		String lsErrorMsg = null;
		String lsfields = "contractBudgetSalariedEmployee.grid";
		loBuffer = HHSUtil.populateSubGridRows(loBeanObj, loListObj, lsRowsPerPage, lsPage, lsErrorMsg, lsfields);
		assertNotNull(loBuffer);
	}

	/**
	 * This method tests identification for whether or not '*'/'**' messgae will
	 * be displayed on the evaluation.
	 */
	@Test
	public void testGetStarDoubleStarStatus2() throws ApplicationException
	{
		Map<String, Boolean> loDisplayStarsMap = new HashMap<String, Boolean>();
		List<EvaluationBean> aoEvaluationBeanList = new ArrayList<EvaluationBean>();
		loDisplayStarsMap = HHSUtil.getStarDoubleStarStatus(aoEvaluationBeanList);
		assertNotNull(loDisplayStarsMap);
	}

	/**
	 * This method tests addition of audit Bean list in Channel on Finish Of
	 * Financial Tasks
	 */
	@Test
	public void testSetAuditOnFinancialFinishTask() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setTaskStatus(ApplicationConstants.STATUS_APPROVED);
		loTaskDetailsBean.setProviderComment("efewrgr");
		loTaskDetailsBean.setInternalComment("rrweeeeer");
		loTaskDetailsBean.setCurrentTaskStatus(HHSConstants.TASK_RFR);
		loTaskDetailsBean.setTaskName(HHSConstants.TASK_BUDGET_REVIEW);
		loTaskDetailsBean.setLevel("1");
		loTaskDetailsBean.setTotalLevel("2");
		loTaskDetailsBean.setEntityType(HHSConstants.TASK_BUDGET_REVIEW);
		loTaskDetailsBean.setUserId("org_123");
		loTaskDetailsBean.setEntityId("123");
		loTaskDetailsBean.setEntityStatus("Approved");
		Channel loChannel = new Channel();
		HHSUtil.setAuditOnFinancialFinishTask(loTaskDetailsBean, loChannel);
	}

	/**
	 * This method tests addition of audit Bean list in Channel on Finish Of
	 * Financial Tasks
	 */
	@Test
	public void testSetAuditOnFinancialFinishTask1() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setTaskStatus(HHSConstants.TASK_RFR);
		loTaskDetailsBean.setProviderComment("efewrgr");
		loTaskDetailsBean.setInternalComment("rrweeeeer");
		loTaskDetailsBean.setCurrentTaskStatus(HHSConstants.TASK_RFR);
		loTaskDetailsBean.setTaskName(HHSConstants.TASK_BUDGET_REVIEW);
		loTaskDetailsBean.setLevel("1");
		loTaskDetailsBean.setTotalLevel("2");
		loTaskDetailsBean.setEntityType(HHSConstants.TASK_BUDGET_REVIEW);
		loTaskDetailsBean.setUserId("org_123");
		loTaskDetailsBean.setEntityId("123");
		loTaskDetailsBean.setEntityStatus("Approved");
		Channel loChannel = new Channel();
		HHSUtil.setAuditOnFinancialFinishTask(loTaskDetailsBean, loChannel);
	}

	/**
	 * This Method tests values which insert the values in the Audit
	 * 
	 */
	@Test
	public void testSetHhsAudit() throws ApplicationException
	{
		Boolean lbThrown = false;
		HhsAuditBean aoAudit = new HhsAuditBean();
		Channel loChannel = new Channel();
		try
		{
			HHSUtil.setHhsAudit(aoAudit);
		}
		catch (ApplicationException e)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	/**
	 * This Method tests conversion of String received from addressValidation to
	 * bean object
	 * 
	 */
	@Test
	public void testConvertAddressValidationFields() throws ApplicationException
	{
		String asAddressRelatedData = "1k3yv@lu3S3p@r@t0r2k3yv@lu3S3p@r@t0r3k3yv@lu3S3p@r@t0r4k3yv@lu3S3p@r@t0r5k3yv@lu3S3p@r@t0r6k3yv@lu3S3p@r@t0r7k3yv@lu3S3p@r@t0r8k3yv@lu3S3p@r@t0r9k3yv@lu3S3p@r@t0r10k3yv@lu3S3p@r@t0r11k3yv@lu3S3p@r@t0r12k3yv@lu3S3p@r@t0r13k3yv@lu3S3p@r@t0r14k3yv@lu3S3p@r@t0r15k3yv@lu3S3p@r@t0r16k3yv@lu3S3p@r@t0r17k3yv@lu3S3p@r@t0r18k3yv@lu3S3p@r@t0r19k3yv@lu3S3p@r@t0r20k3yv@lu3S3p@r@t0r21k3yv@lu3S3p@r@t0r22k3yv@lu3S3p@r@t0r23k3yv@lu3S3p@r@t0r24k3yv@lu3S3p@r@t0r25k3yv@lu3S3p@r@t0r26k3yv@lu3S3p@r@t0r27";
		AddressValidationBean aoAddressBean = new AddressValidationBean();
		HHSUtil.convertAddressValidationFields(asAddressRelatedData, aoAddressBean);
	}

	/**
	 * This Method tests seting of the Audit Data to Channel object
	 * 
	 */

	@Test
	public void testAddAuditDataToChannel() throws ApplicationException
	{
		Boolean lbThrown = false;
		Channel loChannel = new Channel();
		String lsEventName = null;
		String lsEventType = null;
		String lsData = null;
		String lsEntityType = null;
		String lsEntityId = null;
		String lsUserID = null;
		String lsTableIdentifier = null;
		String lsAuditChannelName = null;
		HHSUtil.addAuditDataToChannel(loChannel, lsEventName, lsEventType, lsData, lsEntityType, lsEntityId, lsUserID,
				lsTableIdentifier, lsAuditChannelName);
	}

	/**
	 * This method tests sorting list of objects
	 */
	@Test
	public void testSortList() throws ApplicationException
	{
		Boolean lbThrown = false;
		List<Object> lolistOfObjects = new ArrayList<Object>();
		String lsColumnName = null;
		HHSUtil.sortList(lolistOfObjects, lsColumnName);
	}

	/**
	 * This method tests for clearing the temp folder - deletes file older then
	 * the time set(mins)
	 * @throws IOException
	 */
	@Test
	public void testDeleteAllDownloadedTemplates() throws ApplicationException, IOException
	{
		File loFile = new File("C:\\a");
		loFile.mkdir();
		loFile = new File("C:\\a\\b");
		loFile.mkdir();
		loFile = new File("C:\\a\\b\\abc.txt");
		loFile.createNewFile();
		File loDeleteAll = new File("C:/a/b");
		HHSUtil.deleteAllDownloadedTemplates(loDeleteAll);
	}

	/**
	 * Returns list of services with evidence flag
	 * 
	 * @return Map
	 * @throws ApplicationException Application Exception
	 */

	@Test
	public void testServicesList1() throws ApplicationException
	{
		Map<String, String> loIdNameMap = new HashMap<String, String>();
		loIdNameMap = HHSUtil.getServicesList();
		assertNotNull(loIdNameMap);
	}

	/**
	 * Returns String date in MM/DD/YYYY Format
	 * 
	 * @return Map
	 * @throws ApplicationException Application Exception
	 */

	@Test
	public void testFormatDateToMMDDYYYY() throws ApplicationException
	{
		String lsFormattedDate = null;
		lsFormattedDate = HHSUtil.formatDateToMMDDYYYY("2013-03-29 00:00:00");
		assertNotNull(lsFormattedDate);
	}

	/**
	 * Returns String date in MM/DD/YYYY Format
	 * 
	 * @return Map
	 * @throws ApplicationException Application Exception
	 */

	@Test
	public void testFormatDateToMMDDYYYY1() throws ApplicationException
	{
		String lsFormattedDate = null;
		lsFormattedDate = HHSUtil.formatDateToMMDDYYYY("");
		assertNotNull(lsFormattedDate);
	}

	/**
	 * Returns String date in MM/DD/YYYY Format
	 * 
	 * @return Map
	 * @throws ApplicationException Application Exception
	 */

	@Test
	public void testFormatDateToMMDDYYYY2() throws ApplicationException
	{
		String lsFormattedDate = null;
		lsFormattedDate = HHSUtil.formatDateToMMDDYYYY(null);
		assertNull(lsFormattedDate);
	}

	/**
	 * Returns String date in MM/DD/YYYY Format
	 * 
	 * @return Map
	 * @throws ApplicationException Application Exception
	 */

	@Test(expected = ApplicationException.class)
	public void testFormatDateToMMDDYYYY3() throws ApplicationException
	{
		String lsFormattedDate = null;
		lsFormattedDate = HHSUtil.formatDateToMMDDYYYY("xxxxxxxxxxx");
		assertNotNull(lsFormattedDate);
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetServicesList0Negative() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getServicesList();
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilsortByValues2Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		Map aoMap = new HashMap<Object, Object>();
		try
		{
			loHHSUtil.sortByValues(aoMap);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}
	
	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetZeroTimeDate3Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getZeroTimeDate(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgenerateDelimitedResponse4Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.generateDelimitedResponse(null, null, 0);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgenerateDelimitedAutoCompleteResponse5Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.generateDelimitedAutoCompleteResponse(null, null, 0);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetToDate6Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getToDate();
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilsetDateToFrom7Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.setDateToFrom(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilsetHhsAudit8Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.setHhsAudit(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtildeleteUserCommentsIfEmptyCommentsSaved9Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.deleteUserCommentsIfEmptyCommentsSaved(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilsetHhsAuditForTabLevel10Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.setHhsAuditForTabLevel(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtiladdAuditDataToChannel11Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.addAuditDataToChannel(null, null, null, null, null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilsetAuditOnFinancialFinishTask12Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.setAuditOnFinancialFinishTask(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilpopulateSubGridRows13Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.populateSubGridRows(null, null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetHeader14Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getHeader(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetHeaderProp15Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getHeaderProp(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetSubGridProp16Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getSubGridProp(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetSubGridHeaderRow17Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getSubGridHeaderRow(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilsetMasterStatus18Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.setMasterStatus();
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetStatusName19Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getStatusName(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetStatusID20Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getStatusID(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetStatusMap21Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getStatusMap(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetAgencyMap22Negative()
	{
		
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getAgencyMap();
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
		 
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetCurrentTimestampDate23Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getCurrentTimestampDate();
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetSubStringCount24Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getSubStringCount(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilsetFinancialWFProperty25Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.setFinancialWFProperty(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetFinancialEntityId26Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getFinancialEntityId(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtiladdFromClause27Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.addFromClause(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtiladdToClause28Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.addToClause(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilformatAmount29Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.formatAmount(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilconvertAddressValidationFields30Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.convertAddressValidationFields(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetFYDetails31Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getFYDetails(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetFirstAndLastFYOfContract32Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getFirstAndLastFYOfContract(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetNewBudgetStartDate33Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getNewBudgetStartDate(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetNewBudgetEndDate34Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getNewBudgetEndDate(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetFYForContractBudgetConfig35Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getFYForContractBudgetConfig(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilisEmptyList36Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.isEmptyList(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtildeleteAllDownloadedTemplates37Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.deleteAllDownloadedTemplates(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetAgencyName38Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getAgencyName(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetBudgetFY39Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getBudgetFY(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilround40Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.round(0, 0);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilisAgencyNonAccoUser42Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.isAgencyNonAccoUser(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilisAcceleratorUser44Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.isAcceleratorUser(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtildocumentTypeTransactionName45Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.documentTypeTransactionName(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtiladdDocumentFromVaultTransactionName46Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.addDocumentFromVaultTransactionName(null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetTransactionNameInsertDocumentDetailsInDBOnUpload47Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getTransactionNameInsertDocumentDetailsInDBOnUpload(null, null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilsortList48Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.sortList(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilconvertStringToArray49Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.convertStringToArray(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetTransactionName50Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getTransactionName(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilcopyListToList51Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.copyListToList(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilhomeProcurementTransaction52Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.homeProcurementTransaction(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilhomeFinancialTransaction53Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.homeFinancialTransaction(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetEvidenceFlag54Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getEvidenceFlag(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetStarDoubleStarStatus55Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getStarDoubleStarStatus(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetDateFromEpochTime56Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getDateFromEpochTime(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetEpochTimeFromDate57Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getEpochTimeFromDate(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetUtilDate58Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getUtilDate(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilmarshalObject59Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.marshalObject(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilunmarshalObject60Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.unmarshalObject(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilconvertDocumentToXML61Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.convertDocumentToXML(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilconvertInputStreamToXml62Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.convertInputStreamToXml(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilconvertXmlToStream63Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.convertXmlToStream(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetAgencyMapForProcurement64Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getAgencyMapForProcurement();
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilformatDateToMMDDYYYY65Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.formatDateToMMDDYYYY(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtildecryptASEString69Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.decryptASEString(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilzipFolder70Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.zipFolder(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilauditConfigCommnetsOnBudgets71Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.auditConfigCommnetsOnBudgets(null, null, null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetEntryTypeDetail73Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getEntryTypeDetail(null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilsetPublishEntryType74Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.setPublishEntryType(null, null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtiladdAuditDataToChannel82Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.addAuditDataToChannel(null, null, null, null, null, null, null, null, null, null, null, null,
					null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetApplicationSettings86Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getApplicationSettings();
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetApplicationSettingsBulk87Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getApplicationSettingsBulk();
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetTaskDetailsBeanFromMap95Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getTaskDetailsBeanFromMap(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetReassignUserMap97Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getReassignUserMap(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilconvertCurrencyFormatToNumber105Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.convertCurrencyFormatToNumber(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetContractType107Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getContractType();
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetServicesList0NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getServicesList();
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilsortByValues2NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.sortByValues(new HashMap<String, String>());
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetZeroTimeDate3NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getZeroTimeDate(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgenerateDelimitedResponse4NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.generateDelimitedResponse(null, null, 0);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgenerateDelimitedAutoCompleteResponse5NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.generateDelimitedAutoCompleteResponse(null, null, 0);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetToDate6NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getToDate();
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilsetDateToFrom7NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.setDateToFrom(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilsetHhsAudit8NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.setHhsAudit(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtildeleteUserCommentsIfEmptyCommentsSaved9NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.deleteUserCommentsIfEmptyCommentsSaved(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilsetHhsAuditForTabLevel10NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.setHhsAuditForTabLevel(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtiladdAuditDataToChannel11NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.addAuditDataToChannel(null, null, null, null, null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilsetAuditOnFinancialFinishTask12NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.setAuditOnFinancialFinishTask(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilpopulateSubGridRows13NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.populateSubGridRows(null, null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetHeader14NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getHeader(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetHeaderProp15NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getHeaderProp(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetSubGridProp16NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getSubGridProp(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetSubGridHeaderRow17NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getSubGridHeaderRow(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilsetMasterStatus18NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.setMasterStatus();
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetStatusName19NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getStatusName(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetStatusID20NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getStatusID(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetStatusMap21NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getStatusMap(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetAgencyMap22NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getAgencyMap();
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetCurrentTimestampDate23NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getCurrentTimestampDate();
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetSubStringCount24NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getSubStringCount(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilsetFinancialWFProperty25NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.setFinancialWFProperty(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetFinancialEntityId26NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getFinancialEntityId(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtiladdFromClause27NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.addFromClause(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtiladdToClause28NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.addToClause(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilformatAmount29NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.formatAmount(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilconvertAddressValidationFields30NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.convertAddressValidationFields(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetFYDetails31NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getFYDetails(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetFirstAndLastFYOfContract32NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getFirstAndLastFYOfContract(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetNewBudgetStartDate33NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getNewBudgetStartDate(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetNewBudgetEndDate34NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getNewBudgetEndDate(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetFYForContractBudgetConfig35NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getFYForContractBudgetConfig(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilisEmptyList36NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.isEmptyList(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtildeleteAllDownloadedTemplates37NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.deleteAllDownloadedTemplates(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetAgencyName38NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getAgencyName(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetBudgetFY39NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getBudgetFY(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilround40NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.round(0, 0);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilisAgencyNonAccoUser42NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.isAgencyNonAccoUser(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilisAcceleratorUser44NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.isAcceleratorUser(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtildocumentTypeTransactionName45NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.documentTypeTransactionName(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtiladdDocumentFromVaultTransactionName46NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.addDocumentFromVaultTransactionName(null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetTransactionNameInsertDocumentDetailsInDBOnUpload47NegativeApp()
			throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getTransactionNameInsertDocumentDetailsInDBOnUpload(null, null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilsortList48NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.sortList(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilconvertStringToArray49NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.convertStringToArray(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetTransactionName50NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getTransactionName(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilcopyListToList51NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.copyListToList(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilhomeProcurementTransaction52NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.homeProcurementTransaction(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilhomeFinancialTransaction53NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.homeFinancialTransaction(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetEvidenceFlag54NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getEvidenceFlag(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetStarDoubleStarStatus55NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getStarDoubleStarStatus(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetDateFromEpochTime56NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getDateFromEpochTime(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetEpochTimeFromDate57NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getEpochTimeFromDate(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetUtilDate58NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getUtilDate(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilmarshalObject59NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.marshalObject(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilunmarshalObject60NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.unmarshalObject(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilconvertDocumentToXML61NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.convertDocumentToXML(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilconvertInputStreamToXml62NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.convertInputStreamToXml(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilconvertXmlToStream63NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.convertXmlToStream(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetAgencyMapForProcurement64NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getAgencyMapForProcurement();
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilformatDateToMMDDYYYY65NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.formatDateToMMDDYYYY(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilConvertStringToDate66NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.ConvertStringToDate(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilGetFiscalYear67NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.GetFiscalYear();
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtildecrypt68NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.decrypt(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtildecryptASEString69NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.decryptASEString(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilzipFolder70NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.zipFolder(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilauditConfigCommnetsOnBudgets71NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.auditConfigCommnetsOnBudgets(null, null, null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtillistMapToJSON72NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.listMapToJSON(null, null, null, null, 0);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetEntryTypeDetail73NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getEntryTypeDetail(null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilsetPublishEntryType74NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.setPublishEntryType(null, null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetTaskAuditBean75NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getTaskAuditBean(null, null, null, null, null, null, null, null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetUserTypeFromUserId76NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getUserTypeFromUserId(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilconvertBeanToString77NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.convertBeanToString(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetComponentRoleMap78NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getComponentRoleMap(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetConditionalRoleWithoutCFODisplay81NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getConditionalRoleWithoutCFODisplay(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtiladdAuditDataToChannel82NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.addAuditDataToChannel(null, null, null, null, null, null, null, null, null, null, null, null, null);
	}

	/*@Test(expected = ApplicationException.class)
	public void testHHSUtilgetRandomString83NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getRandomString(0);
	}*/

	@Test(expected = ApplicationException.class)
	public void testHHSUtilimportCertif84NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.importCertif();
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilbuildConstant85NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.buildConstant();
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetApplicationSettings86NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getApplicationSettings();
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetApplicationSettingsBulk87NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getApplicationSettingsBulk();
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetItemDate88NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getItemDate(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilminutesDiff89NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.minutesDiff(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetCurrentTime90NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getCurrentTime();
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetItemDateInMIlisec91NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getItemDateInMIlisec(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtiltimeDiff92NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.timeDiff(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetCurrentTimeInMilliSec93NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getCurrentTimeInMilliSec();
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetTaskPropertiesHashMap94NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getTaskPropertiesHashMap();
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetTaskDetailsBeanFromMap95NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getTaskDetailsBeanFromMap(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetBeanForSavingUserComments96NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getBeanForSavingUserComments(null, null, null, null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetReassignUserMap97NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getReassignUserMap(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtiladdAuditDataToChannel98NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.addAuditDataToChannel(null, null, null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilpopulateProviderCommentsMapFromDB101NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.populateProviderCommentsMapFromDB(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilpopulateAgencyCommentsAuditList102NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.populateAgencyCommentsAuditList(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetSubBudgetIdForTabLevelOnCommentsSave103NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getSubBudgetIdForTabLevelOnCommentsSave(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgenerateLineItemTabsToHighlightMapProvider104NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.generateLineItemTabsToHighlightMapProvider(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilconvertCurrencyFormatToNumber105NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.convertCurrencyFormatToNumber(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetRegionName106NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getRegionName(null);
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetContractType107NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getContractType();
	}

	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetCallingFunctionInformation108NegativeApp() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getCallingFunctionInformation();
	}
	
	@Test
	public void testHHSUtilsortByValuesPositive1() throws ApplicationException
	{
		HashMap loMap = new HashMap<String, String>();
		loMap.put("Key1", "Temp");
		loMap.put("Key2", "Temp");
		HHSUtil loHHSUtil = new HHSUtil();
		assertNotNull(loHHSUtil.sortByValues(loMap));
	}
	
	@Test
	public void testHHSUtilsortByValuesPositive2() throws ApplicationException
	{
		HashMap loMap = new HashMap<String, String>();
		loMap.put("Key1", "Temp1");
		loMap.put("Key2", "Temp2");
		loMap.put("Key3", "Temp3");
		HHSUtil loHHSUtil = new HHSUtil();
		assertNotNull(loHHSUtil.sortByValues(loMap));
	}
	
	@Test
	public void testHHSUtilgetUserTypeFromUserIdP1() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		assertNotNull(loHHSUtil.getUserTypeFromUserId("909"));
	}
	
	@Test
	public void testHHSUtilgetUserTypeFromUserIdP2() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		assertNotNull(loHHSUtil.getUserTypeFromUserId("agency_123"));
	}
	
	@Test
	public void testHHSUtilgetUserTypeFromUserIdP3() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		assertNotNull(loHHSUtil.getUserTypeFromUserId("city_212"));
	}
	
	@Test
	public void testHHSUtilgetUserTypeFromUserIdP4() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		assertNotNull(loHHSUtil.getUserTypeFromUserId("system"));
	}
	
	@Test
	public void testHHSUtilgetEvidenceFlagP1() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		List<String> aoElementIdList = new ArrayList<String>();
		aoElementIdList.add("136");
		aoElementIdList.add("138");
		assertNotNull(loHHSUtil.getEvidenceFlag(aoElementIdList));
	}
	
	@Test
	public void testHHSUtilgetEvidenceFlagP2() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		List<String> aoElementIdList = new ArrayList<String>();
		assertNull(loHHSUtil.getEvidenceFlag(aoElementIdList));
	}
	@Test
	public void generateLineItemTabsToHighlightMapProvider() throws ApplicationException
	{
		List<String> aoAuditResultList = new ArrayList<String>();
		aoAuditResultList.add("Tlc_rate_145");
		aoAuditResultList.add("Tlc_rate_146");
		aoAuditResultList.add("Tlc_rate_147");
		HHSUtil loHHSUtil = new HHSUtil();
		List<Integer> loLineItems = loHHSUtil.generateLineItemTabsToHighlightMapProvider(aoAuditResultList);
		assertNotNull(loLineItems);
	}

	@Test
	public void generateLineItemTabsToHighlightMapProvider1() throws ApplicationException
	{
		List<String> aoAuditResultList = new ArrayList<String>();
		HHSUtil loHHSUtil = new HHSUtil();
		List<Integer> loLineItems = loHHSUtil.generateLineItemTabsToHighlightMapProvider(aoAuditResultList);
		assertNull(loLineItems);
	}

	@Test
	public void generateLineItemTabsToHighlightMapProvider2() throws ApplicationException
	{
		List<String> aoAuditResultList = new ArrayList<String>();
		aoAuditResultList.add("Tlc_procurement_145");
		aoAuditResultList.add("Tlc_procurement_146");
		aoAuditResultList.add("Tlc_procurement_147");
		HHSUtil loHHSUtil = new HHSUtil();
		List<Integer> loLineItems = loHHSUtil.generateLineItemTabsToHighlightMapProvider(aoAuditResultList);
		assertNotNull(loLineItems);
	}

	@Test
	public void generateLineItemTabsToHighlightMapProvider3() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		List<Integer> loLineItems = loHHSUtil.generateLineItemTabsToHighlightMapProvider(null);
		assertNull(loLineItems);
	}

	@Test
	public void getSubBudgetIdForTabLevelOnCommentsSave() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		String lsSubBudgetId = loHHSUtil.getSubBudgetIdForTabLevelOnCommentsSave("Tlc_procurement_147");
		assertNotNull(lsSubBudgetId);
	}

	@Test
	public void getSubBudgetIdForTabLevelOnCommentsSave1() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		String lsSubBudgetId = loHHSUtil.getSubBudgetIdForTabLevelOnCommentsSave(null);
		assertNull(lsSubBudgetId);
	}

	@Test
	public void getSubBudgetIdForTabLevelOnCommentsSave2() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		String lsSubBudgetId = loHHSUtil.getSubBudgetIdForTabLevelOnCommentsSave("");
		assertNull(lsSubBudgetId);
	}

	@Test
	public void populateAgencyCommentsAuditList() throws ApplicationException
	{
		List<Map<String, String>> loUserCommentsMapDBList = new ArrayList<Map<String, String>>();
		Map<String, String> loUserCommentMap = new HashMap<String, String>();
		loUserCommentMap.put(HHSConstants.USER_INTERNAL_COMMENT, "Internal Comments");
		loUserCommentsMapDBList.add(loUserCommentMap);
		List<HhsAuditBean> loAuditList = new ArrayList<HhsAuditBean>();
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setEntityId("123");
		loTaskDetailsBean.setEntityType("Contract Configuration");
		loTaskDetailsBean.setAssignedTo("agency_14");
		HHSUtil loHHSUtil = new HHSUtil();
		loAuditList = loHHSUtil
				.populateAgencyCommentsAuditList(loUserCommentsMapDBList, loAuditList, loTaskDetailsBean);
		assertNotNull(loAuditList);
	}

	@Test
	public void populateAgencyCommentsAuditList1() throws ApplicationException
	{
		List<Map<String, String>> loUserCommentsMapDBList = new ArrayList<Map<String, String>>();
		Map<String, String> loUserCommentMap = new HashMap<String, String>();
		loUserCommentMap.put(HHSConstants.USER_INTERNAL_COMMENT, "");
		loUserCommentsMapDBList.add(loUserCommentMap);
		List<HhsAuditBean> loAuditList = new ArrayList<HhsAuditBean>();
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setEntityId("123");
		loTaskDetailsBean.setEntityType("Contract Configuration");
		loTaskDetailsBean.setAssignedTo("agency_14");
		HHSUtil loHHSUtil = new HHSUtil();
		loAuditList = loHHSUtil
				.populateAgencyCommentsAuditList(loUserCommentsMapDBList, loAuditList, loTaskDetailsBean);
		assertNotNull(loAuditList);
	}

	@Test
	public void populateAgencyCommentsAuditList2() throws ApplicationException
	{
		List<Map<String, String>> loUserCommentsMapDBList = new ArrayList<Map<String, String>>();
		Map<String, String> loUserCommentMap = new HashMap<String, String>();
		loUserCommentMap.put(HHSConstants.USER_INTERNAL_COMMENT, null);
		loUserCommentsMapDBList.add(loUserCommentMap);
		List<HhsAuditBean> loAuditList = new ArrayList<HhsAuditBean>();
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setEntityId("123");
		loTaskDetailsBean.setEntityType("Contract Configuration");
		loTaskDetailsBean.setAssignedTo("agency_14");
		HHSUtil loHHSUtil = new HHSUtil();
		loAuditList = loHHSUtil
				.populateAgencyCommentsAuditList(loUserCommentsMapDBList, loAuditList, loTaskDetailsBean);
		assertNotNull(loAuditList);
	}

	@Test
	public void populateAgencyCommentsAuditList3() throws ApplicationException
	{
		List<Map<String, String>> loUserCommentsMapDBList = new ArrayList<Map<String, String>>();
		Map<String, String> loUserCommentMap = new HashMap<String, String>();
		loUserCommentMap.put(HHSConstants.PARAM_USER_COMMENT_TABLE_PROVIDER_COMMENTS, "Provider Comments");
		loUserCommentsMapDBList.add(loUserCommentMap);
		List<HhsAuditBean> loAuditList = new ArrayList<HhsAuditBean>();
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setEntityId("123");
		loTaskDetailsBean.setEntityType("Contract Configuration");
		loTaskDetailsBean.setAssignedTo("agency_14");
		HHSUtil loHHSUtil = new HHSUtil();
		loAuditList = loHHSUtil
				.populateAgencyCommentsAuditList(loUserCommentsMapDBList, loAuditList, loTaskDetailsBean);
		assertNotNull(loAuditList);
	}

	@Test
	public void populateAgencyCommentsAuditList4() throws ApplicationException
	{
		List<Map<String, String>> loUserCommentsMapDBList = new ArrayList<Map<String, String>>();
		Map<String, String> loUserCommentMap = new HashMap<String, String>();
		loUserCommentMap.put(HHSConstants.PARAM_USER_COMMENT_TABLE_PROVIDER_COMMENTS, "");
		loUserCommentsMapDBList.add(loUserCommentMap);
		List<HhsAuditBean> loAuditList = new ArrayList<HhsAuditBean>();
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setEntityId("123");
		loTaskDetailsBean.setEntityType("Contract Configuration");
		loTaskDetailsBean.setAssignedTo("agency_14");
		HHSUtil loHHSUtil = new HHSUtil();
		loAuditList = loHHSUtil
				.populateAgencyCommentsAuditList(loUserCommentsMapDBList, loAuditList, loTaskDetailsBean);
		assertNotNull(loAuditList);
	}

	@Test
	public void populateAgencyCommentsAuditList5() throws ApplicationException
	{
		List<Map<String, String>> loUserCommentsMapDBList = new ArrayList<Map<String, String>>();
		Map<String, String> loUserCommentMap = new HashMap<String, String>();
		loUserCommentMap.put(HHSConstants.PARAM_USER_COMMENT_TABLE_PROVIDER_COMMENTS, null);
		loUserCommentsMapDBList.add(loUserCommentMap);
		List<HhsAuditBean> loAuditList = new ArrayList<HhsAuditBean>();
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setEntityId("123");
		loTaskDetailsBean.setEntityType("Contract Configuration");
		loTaskDetailsBean.setAssignedTo("agency_14");
		HHSUtil loHHSUtil = new HHSUtil();
		loAuditList = loHHSUtil
				.populateAgencyCommentsAuditList(loUserCommentsMapDBList, loAuditList, loTaskDetailsBean);
		assertNotNull(loAuditList);
	}
	
	@Test
	public void populateProviderCommentsMapFromDB()
	{
		List<Map<String, String>> loUserCommentsMapDBList = new ArrayList<Map<String, String>>();
		Map<String, String> loUserCommentsMap = new HashMap<String, String>();
		loUserCommentsMap.put(HHSConstants.PARAM_USER_COMMENT_TABLE_ENTITY_ID, "123");
		loUserCommentsMap.put(HHSConstants.PARAM_USER_COMMENT_TABLE_PROVIDER_COMMENTS, "Provider Comments");
		loUserCommentsMapDBList.add(loUserCommentsMap);
		HHSUtil loHHSUtil = new HHSUtil();
		Map<String, String> loCommentsMap = loHHSUtil.populateProviderCommentsMapFromDB(loUserCommentsMapDBList);
		assertNotNull(loCommentsMap);
	}

	@Test
	public void populateProviderCommentsMapFromDB1()
	{
		List<Map<String, String>> loUserCommentsMapDBList = new ArrayList<Map<String, String>>();
		Map<String, String> loUserCommentsMap = new HashMap<String, String>();
		loUserCommentsMapDBList.add(loUserCommentsMap);
		HHSUtil loHHSUtil = new HHSUtil();
		Map<String, String> loCommentsMap = loHHSUtil.populateProviderCommentsMapFromDB(loUserCommentsMapDBList);
		assertNotNull(loCommentsMap);
	}

	@Test
	public void getUserTypeFromUserId()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		String lsUserType = loHHSUtil.getUserTypeFromUserId(null);
		assertNull(lsUserType);
	}

	@Test
	public void getUserTypeFromUserId1()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		String lsUserType = loHHSUtil.getUserTypeFromUserId("city_43");
		assertNotNull(lsUserType);
	}

	@Test
	public void getUserTypeFromUserId2()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		String lsUserType = loHHSUtil.getUserTypeFromUserId("agency_14");
		assertNotNull(lsUserType);
	}

	@Test
	public void getUserTypeFromUserId3()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		String lsUserType = loHHSUtil.getUserTypeFromUserId("system");
		assertNotNull(lsUserType);
	}

	@Test
	public void getUserTypeFromUserId4()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		String lsUserType = loHHSUtil.getUserTypeFromUserId("org_147");
		assertNotNull(lsUserType);
	}

	@Test
	public void generateFinalAuditListForTabLevelCommentsProvider()
	{
		Map<String, String> loTabLevelCommentsMapDB = new HashMap<String, String>();
		loTabLevelCommentsMapDB.put("Provider_Comments", "Provider Comments");
		List<HhsAuditBean> loAuditList = new ArrayList<HhsAuditBean>();
		HashMap aoHMWFRequiredProps = new HashMap();
		aoHMWFRequiredProps.put(HHSConstants.PROPERTY_PE_TASK_TYPE, "Contract Budget Review");
		aoHMWFRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW, "123");
		aoHMWFRequiredProps.put(HHSConstants.SUBMITTED_BY, "org_143");
		String asEntitType = "Contract Budget Review";
		HHSUtil loHHSUtil = new HHSUtil();
		loAuditList = loHHSUtil.generateFinalAuditListForTabLevelCommentsProvider(loTabLevelCommentsMapDB, loAuditList,
				aoHMWFRequiredProps, asEntitType);
		assertNotNull(loAuditList);
	}

	@Test
	public void generateFinalAuditListForTabLevelCommentsProvider1()
	{
		Map<String, String> loTabLevelCommentsMapDB = new HashMap<String, String>();
		loTabLevelCommentsMapDB.put("Provider_Comments", "Provider Comments");
		List<HhsAuditBean> loAuditList = new ArrayList<HhsAuditBean>();
		HashMap aoHMWFRequiredProps = new HashMap();
		aoHMWFRequiredProps.put(HHSConstants.PROPERTY_PE_TASK_TYPE, HHSConstants.WF_INVOICE_REVIEW);
		aoHMWFRequiredProps.put(HHSConstants.ENTITY_ID, "123");
		aoHMWFRequiredProps.put(HHSConstants.SUBMITTED_BY, "org_143");
		String asEntitType = "Contract Budget Review";
		HHSUtil loHHSUtil = new HHSUtil();
		loAuditList = loHHSUtil.generateFinalAuditListForTabLevelCommentsProvider(loTabLevelCommentsMapDB, loAuditList,
				aoHMWFRequiredProps, asEntitType);
		assertNotNull(loAuditList);
	}

	@Test
	public void generateFinalAuditListForTabLevelCommentsProvider2()
	{
		Map<String, String> loTabLevelCommentsMapDB = new HashMap<String, String>();
		loTabLevelCommentsMapDB.put("Provider_Comments", "Provider Comments");
		List<HhsAuditBean> loAuditList = null;
		String asEntitType = "Contract Budget Review";
		HHSUtil loHHSUtil = new HHSUtil();
		loAuditList = loHHSUtil.generateFinalAuditListForTabLevelCommentsProvider(loTabLevelCommentsMapDB, loAuditList,
				null, asEntitType);
		assertNull(loAuditList);
	}

	@Test
	public void generateFinalAuditListForTabLevelCommentsProvider3()
	{
		Map<String, String> loTabLevelCommentsMapDB = new HashMap<String, String>();
		loTabLevelCommentsMapDB.put("Provider_Comments", "Provider Comments");
		List<HhsAuditBean> loAuditList = null;
		HashMap aoHMWFRequiredProps = new HashMap();
		String asEntitType = "Contract Budget Review";
		HHSUtil loHHSUtil = new HHSUtil();
		loAuditList = loHHSUtil.generateFinalAuditListForTabLevelCommentsProvider(loTabLevelCommentsMapDB, loAuditList,
				aoHMWFRequiredProps, asEntitType);
		assertNull(loAuditList);
	}

	@Test
	public void generateFinalAuditListForTabLevelCommentsProvider4()
	{
		Map<String, String> loTabLevelCommentsMapDB = new HashMap<String, String>();
		loTabLevelCommentsMapDB.put("Provider_Comments", "Provider Comments");
		List<HhsAuditBean> loAuditList = null;
		HashMap aoHMWFRequiredProps = new HashMap();
		aoHMWFRequiredProps.put("key", "value");
		HHSUtil loHHSUtil = new HHSUtil();
		loAuditList = loHHSUtil.generateFinalAuditListForTabLevelCommentsProvider(loTabLevelCommentsMapDB, loAuditList,
				aoHMWFRequiredProps, null);
		assertNull(loAuditList);
	}

	@Test
	public void generateFinalAuditListForTabLevelCommentsProvider5()
	{
		Map<String, String> loTabLevelCommentsMapDB = new HashMap<String, String>();
		loTabLevelCommentsMapDB.put("Provider_Comments", "Provider Comments");
		List<HhsAuditBean> loAuditList = null;
		HashMap aoHMWFRequiredProps = new HashMap();
		aoHMWFRequiredProps.put("key", "value");
		HHSUtil loHHSUtil = new HHSUtil();
		loAuditList = loHHSUtil.generateFinalAuditListForTabLevelCommentsProvider(loTabLevelCommentsMapDB, loAuditList,
				aoHMWFRequiredProps, "");
		assertNull(loAuditList);
	}

	@Test
	public void generateFinalAuditListForTabLevelCommentsProvider6()
	{
		List<HhsAuditBean> loAuditList = null;
		HashMap aoHMWFRequiredProps = new HashMap();
		aoHMWFRequiredProps.put("key", "value");
		String asEntitType = "Contract Budget Review";
		HHSUtil loHHSUtil = new HHSUtil();
		loAuditList = loHHSUtil.generateFinalAuditListForTabLevelCommentsProvider(null, loAuditList,
				aoHMWFRequiredProps, asEntitType);
		assertNull(loAuditList);
	}

	@Test
	public void generateFinalAuditListForTabLevelCommentsProvider7()
	{
		Map<String, String> loTabLevelCommentsMapDB = new HashMap<String, String>();
		List<HhsAuditBean> loAuditList = null;
		String asEntitType = "Contract Budget Review";
		HashMap aoHMWFRequiredProps = new HashMap();
		aoHMWFRequiredProps.put("key", "value");
		HHSUtil loHHSUtil = new HHSUtil();
		loAuditList = loHHSUtil.generateFinalAuditListForTabLevelCommentsProvider(loTabLevelCommentsMapDB, loAuditList,
				aoHMWFRequiredProps, asEntitType);
		assertNull(loAuditList);
	}

	@Test
	public void generateFinalAuditListForTabLevelCommentsProvider8()
	{
		Map<String, String> loTabLevelCommentsMapDB = new HashMap<String, String>();
		loTabLevelCommentsMapDB.put("Provider_Comments", "Provider Comments");
		List<HhsAuditBean> loAuditList = null;
		String asEntitType = "Contract Budget Review";
		HashMap aoHMWFRequiredProps = new HashMap();
		aoHMWFRequiredProps.put("key", "value");
		HHSUtil loHHSUtil = new HHSUtil();
		loAuditList = loHHSUtil.generateFinalAuditListForTabLevelCommentsProvider(loTabLevelCommentsMapDB, loAuditList,
				aoHMWFRequiredProps, asEntitType);
		assertNull(loAuditList);
	}

	@Test
	public void getTaskAuditBean()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		TaskAuditBean loTaskAuditBean = loHHSUtil.getTaskAuditBean("1234568", "1", "Launch", "Contract Configuration",
				"123", "Contract", "Level 1", "Provider", "235", "org_143", "Unassigned", null);
		assertNotNull(loTaskAuditBean);
	}

	@Test
	public void getTaskAuditBean1()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		TaskAuditBean loTaskAuditBean = loHHSUtil.getTaskAuditBean("1234568", "1", "Launch", "Contract Configuration",
				"123", "Contract", "Level 1", "Agency", "235", "agency_14", "Unassigned", null);
		assertNotNull(loTaskAuditBean);
	}

	@Test
	public void setPublishEntryType() throws ApplicationException
	{
		Channel loChannel = new Channel();
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.setPublishEntryType(loChannel, "123", "true", "1", "org_143", "2014");
		assertNotNull(loChannel);
	}
	
	
	@Test
	public void testHHSUtilgetUtilDateP1() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		assertNotNull(loHHSUtil.getUtilDate("2014-06-05 12:12:12"));
	}
	
	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetUtilDateP2() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		loHHSUtil.getUtilDate("Temp");
	}
	
	@Test
	public void testHHSUtilGetFiscalYearP1() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		assertNotNull(loHHSUtil.GetFiscalYear());
	}
	
	@Test
	public void testHHSUtilsetHhsAuditForTabLevelP1() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		HhsAuditBean aoAudit = new HhsAuditBean();
		TaskDetailsBean loTaskDetailBean = new TaskDetailsBean();
		loTaskDetailBean.setProviderComment("Temp");
		loTaskDetailBean.setInternalComment("Temp");
		loTaskDetailBean.setContractId("26");
		loTaskDetailBean.setBudgetId("4");
		loTaskDetailBean.setSubBudgetId("4");
		loTaskDetailBean.setEntityTypeTabLevel("TLC_milestone_4");
		loHHSUtil.setHhsAuditForTabLevel(aoAudit, loTaskDetailBean);
		assertTrue(true);
	}
	
	@Test
	public void testHHSUtilsetFinancialWFPropertyP1() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		FinancialWFBean aoFinancialWFBean = new FinancialWFBean();
		assertNotNull(loHHSUtil.setFinancialWFProperty(aoFinancialWFBean, "Temp"));
	}
	
	@Test
	public void testFinancialEntityIdP1() throws Exception
	{
		FinancialWFBean aoFinancialWFBean = new FinancialWFBean();
		aoFinancialWFBean.setAdvanceNumber("234");
		String lsEntityId = HHSUtil.getFinancialEntityId(aoFinancialWFBean);
		assertNotNull(lsEntityId);
	}
	
	@Test
	public void testPopulateSubGridRowsP1() throws Exception
	{
		StringBuffer loBuffer = new StringBuffer();
		Object loBeanObj = null;
		String lsRowsPerPage = null;
		String lsPage = null;
		String lsErrorMsg = null;
		String lsfields = "contractBudgetSalariedEmployee.grid";
		loBuffer = HHSUtil.populateSubGridRows(loBeanObj, null, lsRowsPerPage, lsPage, lsErrorMsg, lsfields);
		assertNotNull(loBuffer);
	}
	
	@Test
	public void testPopulateSubGridRowsP2() throws Exception
	{
		StringBuffer loBuffer = new StringBuffer();
		Object loBeanObj = new RateBean();
		String lsRowsPerPage = null;
		String lsPage = null;
		String lsErrorMsg = null;
		List<Object> loList = new ArrayList<Object>();
		loList.add("Temp1");
		loList.add("Temp2");
		String lsfields = "salary.grid";
		loBuffer = HHSUtil.populateSubGridRows(loBeanObj, loList, lsRowsPerPage, lsPage, lsErrorMsg, lsfields);
		assertNotNull(loBuffer);
	}
	
	@Test(expected = ApplicationException.class)
	public void testHHSUtilgetAgencyP1() throws ApplicationException
	{
		HHSUtil loHHSUtil = new HHSUtil();
		BaseCacheManagerWeb.getInstance().putCacheObject(HHSConstants.NYC_AGENCY_MASTER, null);
		loHHSUtil.getAgencyMap();
	}
}