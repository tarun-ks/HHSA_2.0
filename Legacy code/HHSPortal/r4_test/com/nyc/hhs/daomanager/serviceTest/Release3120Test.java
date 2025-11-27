package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertTrue;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.daomanager.service.ConfigurationService;
import com.nyc.hhs.daomanager.service.ContractBudgetModificationService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.ContractBudgetBean;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.PropertyLoader;


public class Release3120Test
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
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	/**
	 * @return
	 * @throws ApplicationException
	 */
	public P8UserSession getFileNetSession() throws ApplicationException
	{
		System.setProperty(P8Constants.PROP_FILE_JAVA_SECURITY_AUTH_LOGIN_CONFIG, PropertyLoader.getProperty(
				P8Constants.PROPERTY_FILE, P8Constants.PROP_FILE_JAVA_SECURITY_AUTH_LOGIN_CONFIG));
		System.setProperty(P8Constants.PROP_FILE_JAVA_NAMING_FACTORY_INITIAL, PropertyLoader.getProperty(
				P8Constants.PROPERTY_FILE, P8Constants.PROP_FILE_JAVA_NAMING_FACTORY_INITIAL));
		System.setProperty(P8Constants.PROP_FILE_FILENET_PE_BOOTSTRAP_CEURI,
				PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, HHSConstants.FILENET_URI));

		P8UserSession loUserSession = new P8UserSession();
		loUserSession.setContentEngineUri(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
				HHSConstants.FILENET_URI));
		loUserSession.setObjectStoreName(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
				HHSConstants.OBJECT_STORE_NAME));
		loUserSession.setIsolatedRegionName(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
				HHSConstants.CONNECTION_POINT_NAME));
		loUserSession.setIsolatedRegionNumber(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
				HHSConstants.CONNECTION_POINT_NUMBER));
		loUserSession.setUserId(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, HHSConstants.CE_USER_ID));
		loUserSession.setPassword(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, HHSConstants.CE_PASSWORD));
		SqlSession loFilenetPEDBSession = HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory()
				.openSession();
		loUserSession.setFilenetPEDBSession(loFilenetPEDBSession);

		return loUserSession;

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
			moSession.close();
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	ContractBudgetModificationService moContractBudgetModificationService = new ContractBudgetModificationService();
	public String modsubBudgetID="20";
	public String modBudgetID="20";
	public String amendContractId = "63";
	public String baseContractId = "53";
	public String contractBudgetID = "24";
	public String subBudgetID = "24";
	public String parentSubBudgetID = "14";
	public String parentBudgetID = "13";
	public String invoiceId = "55";
	public String agency = "agency_12";
	public String provider = "803";
	
	ConfigurationService moConfigurationService = new ConfigurationService();
	
	@Test
	public void testmergeBudgetLineItemsForAmendment() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId("3441");
		aoTaskDetailsBean.setBudgetId("11298");
		aoTaskDetailsBean.setUserId(agency);
		aoTaskDetailsBean.setTaskName("");
		moContractBudgetModificationService.mergeBudgetLineItemsForModification(moSession, aoTaskDetailsBean, "86");
	}
	
	//need to test this
	@Test
	public void testmergeBudgetLineItemsForUpdate() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId("11313");
		aoTaskDetailsBean.setBudgetId("3468");
		aoTaskDetailsBean.setUserId(agency);
		aoTaskDetailsBean.setTaskName("");
		moContractBudgetModificationService.mergeBudgetLineItemsForUpdate(moSession, aoTaskDetailsBean, "86");
	}
	
	@Test
	public void testmergeBudgetLineItemsForAmendment1() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId("4637");
		aoTaskDetailsBean.setBudgetId("12205");
		aoTaskDetailsBean.setUserId(agency);
		aoTaskDetailsBean.setTaskName("");
		aoTaskDetailsBean.setP8UserSession(getFileNetSession());
		moContractBudgetModificationService.mergeBudgetLineItemsForAmendment(moSession, aoTaskDetailsBean, "86",0);
		moSession.commit();
	}

	@Test
	public void testmergeBudgetLineItemsForAmendment2() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId("3680");
		aoTaskDetailsBean.setBudgetId("11369");
		aoTaskDetailsBean.setUserId(agency);
		aoTaskDetailsBean.setTaskName("");
		moContractBudgetModificationService.mergeBudgetLineItemsForAmendment(moSession, aoTaskDetailsBean, "86",1);
	}
	
	@Test
	public void testDeleteForCancelConfigureNewFY() throws ApplicationException
	{
		ContractBudgetBean aoContractBudgetBean = new ContractBudgetBean();
		aoContractBudgetBean.setContractId("121");
		aoContractBudgetBean.setBudgetfiscalYear("2016");
		moConfigurationService.deleteForCancelConfigureNewFY(moSession, aoContractBudgetBean);
	}
	
	@Test(expected = ApplicationException.class)
	public void testDeleteForCancelConfigureNewFYExp() throws ApplicationException
	{
		ContractBudgetBean aoContractBudgetBean = new ContractBudgetBean();
		aoContractBudgetBean.setContractId("121");
		aoContractBudgetBean.setBudgetfiscalYear("2016");
		moConfigurationService.deleteForCancelConfigureNewFY(null, aoContractBudgetBean);
	}
	
	@Test
	public void testCopyFYAmountToPreviousAmountForFinancials() throws ApplicationException
	{
		String lsContractId = "6086";
		String lsUserId = "agency_21";
		moConfigurationService.copyFYAmountToPreviousAmountForFinancials(moSession,lsContractId,lsUserId);
	}
	
	@Test(expected = ApplicationException.class)
	public void testCopyFYAmountToPreviousAmountForFinancialsExp() throws ApplicationException
	{
		String lsContractId = "6086";
		String lsUserId = "agency_21";
		moConfigurationService.copyFYAmountToPreviousAmountForFinancials(null,lsContractId,lsUserId);
	}
	
	@Test
	public void testIsAlreadyLaunchedFYTask() throws ApplicationException
	{
		String lsContractId = "6086";
		moConfigurationService.isAlreadyLaunchedFYTask(moSession,lsContractId);
	}
	
	@Test(expected = ApplicationException.class)
	public void testIsAlreadyLaunchedFYTaskExp() throws ApplicationException
	{
		String lsContractId = "6086";
		moConfigurationService.isAlreadyLaunchedFYTask(null,lsContractId);
	}
	
	@Test
	public void testFetcNewFYContractDocs() throws ApplicationException
	{
		ContractBudgetBean aoContractBudgetBean = new ContractBudgetBean();
		aoContractBudgetBean.setContractId("6086");
		aoContractBudgetBean.setBudgetfiscalYear("2016");
		moConfigurationService.fetcNewFYContractDocs(moSession,aoContractBudgetBean);
	}
	
	@Test(expected = ApplicationException.class)
	public void testFetcNewFYContractDocsExp() throws ApplicationException
	{
		ContractBudgetBean aoContractBudgetBean = new ContractBudgetBean();
		aoContractBudgetBean.setContractId("6086");
		aoContractBudgetBean.setBudgetfiscalYear("2016");
		moConfigurationService.fetcNewFYContractDocs(null,aoContractBudgetBean);
	}
	
	@Test
	public void testFetchContractSourceId() throws ApplicationException
	{
		String lsContractId = "6086";
		moConfigurationService.fetchContractSourceId(moSession,lsContractId);
	}
	
	@Test
	public void testFetchContractSourceIdExp() throws ApplicationException
	{
		String lsContractId = "6086";
		moConfigurationService.fetchContractSourceId(null,lsContractId);
	}

}
