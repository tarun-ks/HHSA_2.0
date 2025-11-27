package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.daomanager.service.ConfigurationService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.ContractBudgetBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;
import java.util.Date;

public class Release3140Test
{
	private static SqlSession moSession = null; // SQL Session
	private static P8UserSession session = null; // FileNet session

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		moSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
		session = getFileNetSession();
	}

	public P8UserSession getFileNetSession() throws ApplicationException
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
		loUserSession.setIsolatedRegionNumber("3");
		SqlSession loFilenetPEDBSession = HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory()
				.openSession();
		loUserSession.setFilenetPEDBSession(loFilenetPEDBSession);
		return loUserSession;
	}
	public String amendContractId = "3467";
	public String baseContractId = "3463";
	public String contractBudgetID = "11309";
	public String subBudgetID = "3009";
	public String parentSubBudgetID = "14";
	public String parentBudgetID = "13";
	public String invoiceId = "55";
	public String agency = "agency_12";
	public String provider = "803";

	// base line items id
	public String contractedServiceId = "13";
	public String rateId = "8";
	public String milestoneId = "7";
	public String personalServiceSalariedId = "11";
	public String programIncomeId = "84";
	public String unallocatedId = "12";
	public String equipmentId = "5";
	public String rentId = "8";
	public String IndRateId = "12";
	public String utilitiesId = "100";

	public String amendContractIdApproved = "60";
	public String contractBudgetIDApproved = "22";
	public String subBudgetIDApproved = "22";

	public String amendBudgetType = "1";
	public String baseBudgetType = "2";
	public String modBudgetType = "3";
	public String updaBudgetType = "4";

	public String fiscalYearId = "2015";

	public String procurementId = "163";

	public String amendmentContractIdForTask = "566";
	public String contractFinancialsId = "861";
	public String positive = "positive";
	public String negative = "negative";
	ConfigurationService moConfigurationService = new ConfigurationService();
	
	private CBGridBean getCBGridBeanParams() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setParentBudgetId(parentSubBudgetID);
		loCBGridBean.setParentSubBudgetId(parentSubBudgetID);
		loCBGridBean.setSubBudgetID(subBudgetID);
		loCBGridBean.setContractID(baseContractId);
		loCBGridBean.setModifyByAgency(agency);
		loCBGridBean.setModifyByProvider("803");
		loCBGridBean.setFiscalYearID(fiscalYearId);
		loCBGridBean.setProcurementID("136");
		loCBGridBean.setType("Approved");
		return loCBGridBean;
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{
		moSession.close();
		session.getFilenetPEDBSession().close();
		moSession.rollback();
	}
	
	@Test
	public void testGetNextNewFYBudgetDetails() throws ApplicationException
	{
		List<ContractBudgetBean> loContractBudgetBean = moConfigurationService.getNextNewFYBudgetDetails(moSession, baseContractId, fiscalYearId, getCBGridBeanParams());
		assertTrue(loContractBudgetBean!=null);
	}
	
	
	@Test(expected = ApplicationException.class)
	public void testGetNextNewFYBudgetDetailsNegative() throws ApplicationException
	{
		moConfigurationService.getNextNewFYBudgetDetails(moSession, baseContractId, null, getCBGridBeanParams());
	}
	
	@Test(expected = ApplicationException.class)
	public void testGetNextNewFYBudgetDetailsNegative2() throws ApplicationException
	{
		moConfigurationService.getNextNewFYBudgetDetails(moSession, baseContractId, fiscalYearId, null);
	}
	
	@Test(expected = ApplicationException.class)
	public void testGetNextNewFYBudgetDetailsNegative3() throws ApplicationException
	{
		moConfigurationService.getNextNewFYBudgetDetails(moSession, null, fiscalYearId, getCBGridBeanParams());
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testFetchContractConfUpdateSubBudgetDetails() throws ApplicationException
	{
			ConfigurationService loConfigurationService = new ConfigurationService();
			List loList = loConfigurationService.fetchContractConfUpdateSubBudgetDetails(moSession, baseContractId,
					fiscalYearId, "1", "2", amendContractId);
			assertNotNull(loList);
	}
	
	@Test(expected = ApplicationException.class)
	public void testAddContractConfUpdateBudgetDetails() throws ApplicationException
	{
			ContractBudgetBean loSubBudgetBean = contractBudgetBean();
			moConfigurationService.addContractConfUpdateBudgetDetails(moSession, loSubBudgetBean);
	}

	@Test
	public void testAddContractConfUpdateBudgetDetails2() throws ApplicationException
	{
			ContractBudgetBean loSubBudgetBean = contractBudgetBean();
			loSubBudgetBean.setSubbudgetName("bud2");
			moConfigurationService.addContractConfUpdateBudgetDetails(moSession, loSubBudgetBean);
	}
	
	@Test
	public void testEditContractConfUpdateSubBudgetDetails() throws ApplicationException
	{
			ContractBudgetBean loSubBudgetBean = contractBudgetBean();
			boolean lbUpdateStatus = moConfigurationService.editContractConfUpdateSubBudgetDetails(moSession, loSubBudgetBean,HHSConstants.TRUE);
			assertTrue(lbUpdateStatus);
	}
	
	@Test
	public void testEditContractConfUpdateSubBudgetDetails2() throws ApplicationException
	{
			ContractBudgetBean loSubBudgetBean = contractBudgetBean();
			loSubBudgetBean.setSubbudgetName("bud3");
			boolean lbUpdateStatus = moConfigurationService.editContractConfUpdateSubBudgetDetails(moSession, loSubBudgetBean,HHSConstants.TRUE);
			assertTrue(lbUpdateStatus);
	}
	
	@Test
	public void testUpdateBudgetForNewFYConfigurationTask() throws ApplicationException
	{
			ContractBudgetBean loSubBudgetBean = contractBudgetBean();
			boolean lbUpdateStatus = moConfigurationService.updateBudgetForNewFYConfigurationTask(moSession, loSubBudgetBean);
			assertTrue(lbUpdateStatus);
	}
	
	@Test
	public void testDelContractConfUpdateSubBudgetDetails() throws ApplicationException
	{
			ContractBudgetBean loSubBudgetBean = contractBudgetBean();
			boolean lbUpdateStatus = moConfigurationService.delContractConfUpdateSubBudgetDetails(moSession, loSubBudgetBean);
			assertTrue(lbUpdateStatus);
	}
	
	@Test
	public void testFetchContractConfAmendmentDetails1() throws ApplicationException, ParseException
	{
			ConfigurationService loConfigurationService = new ConfigurationService();
			CBGridBean aoCBGridBean = new CBGridBean();
			aoCBGridBean.setAmendmentContractID("6174");
			aoCBGridBean.setFiscalYearID("2014");
			loConfigurationService.fetchContractConfAmendmentDetails(aoCBGridBean, moSession);
	}

	
	/**
	 * @return
	 */
	private ContractBudgetBean contractBudgetBean() {
		ContractBudgetBean loSubBudgetBean = new ContractBudgetBean();
		loSubBudgetBean.setContractId("3465");
		loSubBudgetBean.setBudgetId("11312");
		loSubBudgetBean.setAmendmentContractId("3472");
		loSubBudgetBean.setBudgetTypeId(1);
		loSubBudgetBean.setContractValue("10000");
		loSubBudgetBean.setCreatedByUserId(agency);
		loSubBudgetBean.setPlannedAmount("3434");
		loSubBudgetBean.setSubbudgetName("bud1");
		loSubBudgetBean.setTotalbudgetAmount("200");
		loSubBudgetBean.setActiveFlag("1");
		loSubBudgetBean.setBudgetfiscalYear(fiscalYearId);
		loSubBudgetBean.setBudgetStartDate("2014/07/14");
		loSubBudgetBean.setBudgetEndDate("2014/07/14");
		loSubBudgetBean.setModifiedByUserId(agency);
		loSubBudgetBean.setStatusId("2");
		loSubBudgetBean.setSubbudgetAmount("1000");
		loSubBudgetBean.setBudgetfiscalYear("2014");
		loSubBudgetBean.setId("3016");
		loSubBudgetBean.setContractTypeId("2");
		return loSubBudgetBean;
	}
		
}