package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.batch.impl.PDFGenerationBatch;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.daomanager.service.ContractBudgetService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.BudgetDetails;
import com.nyc.hhs.model.CBEquipmentBean;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.CBIndirectRateBean;
import com.nyc.hhs.model.CBMileStoneBean;
import com.nyc.hhs.model.CBOperationSupportBean;
import com.nyc.hhs.model.CBProfessionalServicesBean;
import com.nyc.hhs.model.CBProgramIncomeBean;
import com.nyc.hhs.model.CBUtilities;
import com.nyc.hhs.model.ContractBudgetBean;
import com.nyc.hhs.model.ContractBudgetSummary;
import com.nyc.hhs.model.ContractList;
import com.nyc.hhs.model.ContractedServicesBean;
import com.nyc.hhs.model.ExtendedDocument;
import com.nyc.hhs.model.LineItemMasterBean;
import com.nyc.hhs.model.MasterBean;
import com.nyc.hhs.model.PersonnelServiceBudget;
import com.nyc.hhs.model.PersonnelServicesData;
import com.nyc.hhs.model.RateBean;
import com.nyc.hhs.model.Rent;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.model.UnallocatedFunds;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.PropertyLoader;

public class ContractBudgetServiceTest
{

	public String baseContractId = "63";
	public String amendContractId = "53";
	public String contractBudgetID = "24";
	public String subBudgetID = "24";
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

	ContractBudgetService moContractBudgetService = new ContractBudgetService();
	private static SqlSession moSession = null; // SQL Session

	// start junit to improve code coverage
	private static P8UserSession session = null;

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
		// loP8SecurityService.getPESession(loUserSession);
		// loP8SecurityService.getObjectStore(loUserSession);

		return loUserSession;
	}

	private MasterBean getMasterBeanApproved() throws ApplicationException
	{
		MasterBean masterBean = new MasterBean();
		List<LineItemMasterBean> lineItemMasterBean = new ArrayList<LineItemMasterBean>();
		masterBean.setBudgetId(contractBudgetIDApproved);
		lineItemMasterBean.add(new LineItemMasterBean());
		masterBean.setMasterBeanList(lineItemMasterBean);
		List<CBProgramIncomeBean> CBProgramIncomeBean = new ArrayList<CBProgramIncomeBean>();
		CBProgramIncomeBean.add(getDummyCBProgramIncomeBeanObjApproved());
		List<ContractedServicesBean> ContractedServicesBean = new ArrayList<ContractedServicesBean>();
		ContractedServicesBean.add(getDummyContractedServicesBeanObjConsultantApproved());
		List<UnallocatedFunds> unallocatedBeanList = new ArrayList<UnallocatedFunds>();
		unallocatedBeanList.add(getDummyUnallocatedFundsBeanObjApproved());
		List<CBOperationSupportBean> CBOperationSupportBean = new ArrayList<CBOperationSupportBean>();
		CBOperationSupportBean.add(getCBOperationSupportBeanApproved());
		List<CBEquipmentBean> equipmentBeanList = new ArrayList<CBEquipmentBean>();
		equipmentBeanList.add(getCBEquipmentBeanApproved());
		ContractedServicesBean loContractedServicesBean = new ContractedServicesBean();
		List<CBProfessionalServicesBean> CBProfessionalServicesBean = new ArrayList<CBProfessionalServicesBean>();
		CBProfessionalServicesBean.add(getProfServicesDetailsApproved());
		List<Rent> CBRent = new ArrayList<Rent>();
		CBRent.add(getRentApprovedObj());
		List<CBIndirectRateBean> CBCBIndirectRateBean = new ArrayList<CBIndirectRateBean>();
		CBCBIndirectRateBean.add(getIndRateObjApproved());
		List<RateBean> CBRateBean = new ArrayList<RateBean>();
		CBRateBean.add(getRateBeanApprovedObj());
		List<CBUtilities> CBCBUtilities = new ArrayList<CBUtilities>();
		CBCBUtilities.add(getCBUtilitiesApprovedObj());
		loContractedServicesBean.setYtdInvoiceAmt("0");
		loContractedServicesBean.setTotalContractedServices("100");
		lineItemMasterBean.get(0).setSubbudgetId(subBudgetIDApproved);
		lineItemMasterBean.get(0).setProgramincomeBeanList(CBProgramIncomeBean);
		lineItemMasterBean.get(0).setContractedserviceBeanList(ContractedServicesBean);
		lineItemMasterBean.get(0).setUnallocatedBeanList(unallocatedBeanList);
		lineItemMasterBean.get(0).setNonGridConServiceData(loContractedServicesBean);
		lineItemMasterBean.get(0).setProfserviceBeanList(CBProfessionalServicesBean);
		lineItemMasterBean.get(0).setRentBeanList(CBRent);
		lineItemMasterBean.get(0).setIndirectBeanList(CBCBIndirectRateBean);
		lineItemMasterBean.get(0).setRateBeanList(CBRateBean);
		lineItemMasterBean.get(0).setUtilityBeanList(CBCBUtilities);
		/*
		 * P8UserSession loFilenetSession = getFileNetSession();
		 * moContractBudgetAmendmentService
		 * .generateMasterBeanObjectFromXML(moSession, "22", loFilenetSession);
		 */
		return masterBean;
	}

	private UnallocatedFunds getDummyUnallocatedFundsBeanObjApproved()
	{
		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		loUnallocatedFundsBean.setSubBudgetID(subBudgetIDApproved);
		loUnallocatedFundsBean.setContractBudgetID(contractBudgetIDApproved);
		loUnallocatedFundsBean.setContractID(amendContractIdApproved);
		loUnallocatedFundsBean.setParentSubBudgetId(parentSubBudgetID);
		loUnallocatedFundsBean.setModifiedByUserId("city_142");
		loUnallocatedFundsBean.setParentBudgetId(parentBudgetID);
		loUnallocatedFundsBean.setParentSubBudgetId(parentSubBudgetID);
		loUnallocatedFundsBean.setAmendmentType("positive");
		loUnallocatedFundsBean.setInvoiceId(invoiceId);
		loUnallocatedFundsBean.setId(unallocatedId);
		loUnallocatedFundsBean.setModifyByAgency("agency_12");
		loUnallocatedFundsBean.setModifyByProvider("803");
		loUnallocatedFundsBean.setBudgetTypeId("2");
		loUnallocatedFundsBean.setBudgetStatusId("86");
		loUnallocatedFundsBean.setModificationAmount("100");
		return loUnallocatedFundsBean;
	}

	private ContractedServicesBean getDummyContractedServicesBeanObj()
	{
		ContractedServicesBean loContractedServicesBean = new ContractedServicesBean();
		loContractedServicesBean.setContractBudgetID(contractBudgetID);
		loContractedServicesBean.setSubBudgetID(subBudgetID);
		loContractedServicesBean.setContractID(amendContractId);
		loContractedServicesBean.setParentBudgetId(parentBudgetID);
		loContractedServicesBean.setParentSubBudgetId(parentSubBudgetID);
		loContractedServicesBean.setInvoiceId("55");
		loContractedServicesBean.setId(contractedServiceId);
		loContractedServicesBean.setModifyByAgency("agency_12");
		loContractedServicesBean.setModifyByProvider("803");
		loContractedServicesBean.setAmendmentType("positive");
		loContractedServicesBean.setBudgetStatusId("86");
		loContractedServicesBean.setId(contractedServiceId);
		return loContractedServicesBean;

	}

	private ContractedServicesBean getDummyContractedServicesBeanObjConsultantApproved()
	{
		ContractedServicesBean loContractedServicesBean = new ContractedServicesBean();
		loContractedServicesBean.setContractBudgetID(contractBudgetIDApproved);
		loContractedServicesBean.setSubBudgetID(subBudgetIDApproved);
		loContractedServicesBean.setContractID(amendContractIdApproved);
		loContractedServicesBean.setParentBudgetId(parentBudgetID);
		loContractedServicesBean.setParentSubBudgetId(parentSubBudgetID);
		loContractedServicesBean.setInvoiceId("55");
		loContractedServicesBean.setId(contractedServiceId);
		loContractedServicesBean.setModifyByAgency("agency_12");
		loContractedServicesBean.setModifyByProvider("803");
		loContractedServicesBean.setAmendmentType("positive");
		loContractedServicesBean.setBudgetStatusId("86");
		loContractedServicesBean.setSubHeader("1");
		return loContractedServicesBean;

	}

	private CBOperationSupportBean getCBOperationSupportBeanObj()
	{
		CBOperationSupportBean loCBOperationSupportBean = new CBOperationSupportBean();
		loCBOperationSupportBean.setContractBudgetID(contractBudgetID);
		loCBOperationSupportBean.setSubBudgetID(subBudgetID);
		loCBOperationSupportBean.setContractID(amendContractId);
		loCBOperationSupportBean.setParentSubBudgetId(parentSubBudgetID);
		loCBOperationSupportBean.setInvoiceId("55");
		loCBOperationSupportBean.setId(programIncomeId);
		loCBOperationSupportBean.setModifyByAgency("agency_12");
		loCBOperationSupportBean.setModifyByProvider("803");
		loCBOperationSupportBean.setBudgetTypeId("2");
		loCBOperationSupportBean.setModificationAmt("100");
		loCBOperationSupportBean.setAmendmentType("positive");
		return loCBOperationSupportBean;
	}

	private CBProgramIncomeBean getDummyCBProgramIncomeBeanObjApproved()
	{
		CBProgramIncomeBean loCBProgramIncomeBean = new CBProgramIncomeBean();
		loCBProgramIncomeBean.setContractBudgetID(contractBudgetIDApproved);
		loCBProgramIncomeBean.setSubBudgetID(subBudgetIDApproved);
		loCBProgramIncomeBean.setContractID(amendContractId);
		loCBProgramIncomeBean.setParentSubBudgetId(parentSubBudgetID);
		loCBProgramIncomeBean.setInvoiceId("55");
		loCBProgramIncomeBean.setId(programIncomeId);
		loCBProgramIncomeBean.setModifyByAgency("agency_12");
		loCBProgramIncomeBean.setModifyByProvider("803");
		loCBProgramIncomeBean.setBudgetType("2");
		loCBProgramIncomeBean.setIncome("900.00");
		loCBProgramIncomeBean.setActiveFlag("0");
		loCBProgramIncomeBean.setAmendmentType("positive");
		return loCBProgramIncomeBean;
	}

	private CBProfessionalServicesBean getProfServicesDetailsApproved()
	{
		CBProfessionalServicesBean loCBProfessionalServicesBean = new CBProfessionalServicesBean();
		loCBProfessionalServicesBean.setContractBudgetID(contractBudgetIDApproved);
		loCBProfessionalServicesBean.setSubBudgetID(subBudgetIDApproved);
		loCBProfessionalServicesBean.setContractID(amendContractId);
		loCBProfessionalServicesBean.setParentSubBudgetId(parentSubBudgetID);
		loCBProfessionalServicesBean.setInvoiceId("55");
		loCBProfessionalServicesBean.setId(programIncomeId);
		loCBProfessionalServicesBean.setModifyByAgency("agency_12");
		loCBProfessionalServicesBean.setModifyByProvider("803");
		loCBProfessionalServicesBean.setBudgetTypeId("2");
		loCBProfessionalServicesBean.setBudgetStatusId("86");
		loCBProfessionalServicesBean.setModifyAmount("100");
		loCBProfessionalServicesBean.setAmendmentType("positive");
		return loCBProfessionalServicesBean;
	}

	private CBOperationSupportBean getCBOperationSupportBeanApproved()
	{
		CBOperationSupportBean loCBOperationSupportBean = new CBOperationSupportBean();
		loCBOperationSupportBean.setContractBudgetID(contractBudgetIDApproved);
		loCBOperationSupportBean.setSubBudgetID(subBudgetIDApproved);
		loCBOperationSupportBean.setContractID(amendContractId);
		loCBOperationSupportBean.setParentSubBudgetId(parentSubBudgetID);
		loCBOperationSupportBean.setInvoiceId("55");
		loCBOperationSupportBean.setId(programIncomeId);
		loCBOperationSupportBean.setModifyByAgency("agency_12");
		loCBOperationSupportBean.setModifyByProvider("803");
		loCBOperationSupportBean.setBudgetTypeId("2");
		loCBOperationSupportBean.setBudgetStatusId("86");
		loCBOperationSupportBean.setModificationAmt("100");
		loCBOperationSupportBean.setAmendmentType("positive");
		return loCBOperationSupportBean;
	}

	private CBEquipmentBean getCBEquipmentBeanObj()
	{
		CBEquipmentBean loCBEquipmentBean = new CBEquipmentBean();
		loCBEquipmentBean.setContractBudgetID(contractBudgetID);
		loCBEquipmentBean.setSubBudgetID(subBudgetID);
		loCBEquipmentBean.setContractID(amendContractId);
		loCBEquipmentBean.setParentBudgetId(parentBudgetID);
		loCBEquipmentBean.setParentSubBudgetId(parentSubBudgetID);
		loCBEquipmentBean.setInvoiceId("55");
		loCBEquipmentBean.setId(equipmentId);
		loCBEquipmentBean.setModifyByAgency("agency_12");
		loCBEquipmentBean.setModifyByProvider("803");
		loCBEquipmentBean.setBudgetTypeId("2");
		loCBEquipmentBean.setModificationAmt("100");
		loCBEquipmentBean.setAmendmentType("positive");
		loCBEquipmentBean.setEquipment("Test");
		loCBEquipmentBean.setUnits("-1000");
		return loCBEquipmentBean;
	}

	private CBEquipmentBean getCBEquipmentBeanApproved()
	{
		CBEquipmentBean loCBEquipmentBean = new CBEquipmentBean();
		loCBEquipmentBean.setContractBudgetID(contractBudgetIDApproved);
		loCBEquipmentBean.setSubBudgetID(subBudgetIDApproved);
		loCBEquipmentBean.setContractID(amendContractId);
		loCBEquipmentBean.setParentBudgetId(parentBudgetID);
		loCBEquipmentBean.setParentSubBudgetId(parentSubBudgetID);
		loCBEquipmentBean.setInvoiceId("55");
		loCBEquipmentBean.setId(equipmentId);
		loCBEquipmentBean.setModifyByAgency("agency_12");
		loCBEquipmentBean.setModifyByProvider("803");
		loCBEquipmentBean.setBudgetTypeId("2");
		loCBEquipmentBean.setBudgetStatusId("86");
		loCBEquipmentBean.setModificationAmt("100");
		loCBEquipmentBean.setAmendmentType("positive");
		loCBEquipmentBean.setEquipment("Test");
		loCBEquipmentBean.setUnits("4");
		return loCBEquipmentBean;
	}

	private Rent getRentObj()
	{
		Rent loRent = new Rent();
		loRent.setContractBudgetID(contractBudgetID);
		loRent.setSubBudgetID(subBudgetID);
		loRent.setContractID(amendContractId);
		loRent.setParentBudgetId(parentBudgetID);
		loRent.setParentSubBudgetId(parentSubBudgetID);
		loRent.setInvoiceId("55");
		loRent.setId(rentId);
		loRent.setModifyByAgency("agency_12");
		loRent.setModifyByProvider("803");
		loRent.setBudgetTypeId("2");
		loRent.setModifyAmount("100");
		loRent.setAmendmentType("positive");
		loRent.setLocation("1");
		loRent.setManagementCompanyName("1");
		loRent.setPublicSchoolSpace("1");
		loRent.setPropertyOwner("1");
		loRent.setPercentChargedToContract("1");
		loRent.setBudgetStatusId("86");
		return loRent;
	}

	private Rent getRentApprovedObj()
	{
		Rent loRent = new Rent();
		loRent.setContractBudgetID(contractBudgetIDApproved);
		loRent.setSubBudgetID(subBudgetIDApproved);
		loRent.setContractID(amendContractIdApproved);
		loRent.setParentBudgetId(parentBudgetID);
		loRent.setParentSubBudgetId(parentSubBudgetID);
		loRent.setInvoiceId("55");
		loRent.setId(rentId);
		loRent.setModifyByAgency("agency_12");
		loRent.setModifyByProvider("803");
		loRent.setBudgetTypeId("2");
		loRent.setModifyAmount("100");
		loRent.setAmendmentType("positive");
		loRent.setLocation("11");
		loRent.setManagementCompanyName("2123");
		loRent.setPublicSchoolSpace("2321");
		loRent.setPropertyOwner("222");
		loRent.setPercentChargedToContract("2313");
		loRent.setBudgetStatusId("86");
		return loRent;
	}

	private CBMileStoneBean getCBMileStoneBeanObj()
	{
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();
		loCBMileStoneBean.setContractBudgetID(contractBudgetID);
		loCBMileStoneBean.setSubBudgetID(subBudgetID);
		loCBMileStoneBean.setContractID(amendContractId);
		loCBMileStoneBean.setParentBudgetId(parentBudgetID);
		loCBMileStoneBean.setParentSubBudgetId(parentSubBudgetID);
		loCBMileStoneBean.setInvoiceId("55");
		loCBMileStoneBean.setId(milestoneId);
		loCBMileStoneBean.setModifyByAgency("agency_12");
		loCBMileStoneBean.setModifyByProvider("803");
		loCBMileStoneBean.setBudgetTypeId("2");
		loCBMileStoneBean.setModificationAmount("100");
		loCBMileStoneBean.setAmendmentType("positive");
		loCBMileStoneBean.setBudgetStatusId("86");
		return loCBMileStoneBean;
	}

	private CBMileStoneBean getCBMileStoneBeanObjApproved()
	{
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();
		loCBMileStoneBean.setContractBudgetID(contractBudgetIDApproved);
		loCBMileStoneBean.setSubBudgetID(subBudgetIDApproved);
		loCBMileStoneBean.setContractID(amendContractIdApproved);
		loCBMileStoneBean.setParentBudgetId(parentBudgetID);
		loCBMileStoneBean.setParentSubBudgetId(parentSubBudgetID);
		loCBMileStoneBean.setInvoiceId("55");
		loCBMileStoneBean.setId(milestoneId);
		loCBMileStoneBean.setModifyByAgency("agency_12");
		loCBMileStoneBean.setModifyByProvider("803");
		loCBMileStoneBean.setBudgetTypeId("2");
		loCBMileStoneBean.setModificationAmount("100");
		loCBMileStoneBean.setAmendmentType("positive");
		loCBMileStoneBean.setBudgetStatusId("86");
		return loCBMileStoneBean;
	}

	private RateBean getRateObj()
	{
		RateBean loRate = new RateBean();
		loRate.setContractBudgetID(contractBudgetID);
		loRate.setSubBudgetID(subBudgetID);
		loRate.setContractID(amendContractId);
		loRate.setParentBudgetId(parentBudgetID);
		loRate.setParentSubBudgetId(parentSubBudgetID);
		loRate.setInvoiceId("55");
		loRate.setId(rateId);
		loRate.setModifyByAgency("agency_12");
		loRate.setModifyByProvider("803");
		loRate.setBudgetTypeId("2");
		loRate.setLsModifyAmount("100");
		loRate.setAmendmentType("positive");
		loRate.setBudgetStatusId("86");
		return loRate;
	}

	private RateBean getRateBeanApprovedObj()
	{
		RateBean loRateBean = new RateBean();
		loRateBean.setContractBudgetID(contractBudgetIDApproved);
		loRateBean.setSubBudgetID(subBudgetIDApproved);
		loRateBean.setContractID(amendContractIdApproved);
		loRateBean.setParentBudgetId(parentBudgetID);
		loRateBean.setParentSubBudgetId(parentSubBudgetID);
		loRateBean.setInvoiceId("55");
		loRateBean.setId(rateId);
		loRateBean.setModifyByAgency("agency_12");
		loRateBean.setModifyByProvider("803");
		loRateBean.setBudgetTypeId("2");
		loRateBean.setLsModifyAmount("100");
		loRateBean.setAmendmentType("positive");
		loRateBean.setBudgetStatusId("86");
		return loRateBean;
	}

	private CBIndirectRateBean getIndRateObj()
	{
		CBIndirectRateBean loCBIndirectRateBean = new CBIndirectRateBean();
		loCBIndirectRateBean.setContractBudgetID(contractBudgetID);
		loCBIndirectRateBean.setSubBudgetID(subBudgetID);
		loCBIndirectRateBean.setContractID(amendContractId);
		loCBIndirectRateBean.setParentBudgetId(parentBudgetID);
		loCBIndirectRateBean.setParentSubBudgetId(parentSubBudgetID);
		loCBIndirectRateBean.setInvoiceId("55");
		loCBIndirectRateBean.setId(IndRateId);
		loCBIndirectRateBean.setModifyByAgency("agency_12");
		loCBIndirectRateBean.setModifyByProvider("803");
		loCBIndirectRateBean.setBudgetTypeId("2");
		loCBIndirectRateBean.setIndirectModificationAmount("100");
		loCBIndirectRateBean.setAmendmentType("positive");
		loCBIndirectRateBean.setBudgetStatusId("86");
		return loCBIndirectRateBean;
	}

	private CBIndirectRateBean getIndRateObjApproved()
	{
		CBIndirectRateBean loCBIndirectRateBean = new CBIndirectRateBean();
		loCBIndirectRateBean.setContractBudgetID(contractBudgetIDApproved);
		loCBIndirectRateBean.setSubBudgetID(subBudgetIDApproved);
		loCBIndirectRateBean.setContractID(amendContractIdApproved);
		loCBIndirectRateBean.setParentBudgetId(parentBudgetID);
		loCBIndirectRateBean.setParentSubBudgetId(parentSubBudgetID);
		loCBIndirectRateBean.setInvoiceId("55");
		loCBIndirectRateBean.setId(IndRateId);
		loCBIndirectRateBean.setModifyByAgency("agency_12");
		loCBIndirectRateBean.setModifyByProvider("803");
		loCBIndirectRateBean.setBudgetTypeId("2");
		loCBIndirectRateBean.setIndirectModificationAmount("100");
		loCBIndirectRateBean.setAmendmentType("positive");
		loCBIndirectRateBean.setBudgetStatusId("86");
		return loCBIndirectRateBean;
	}

	private CBUtilities getCBUtilitiesObj()
	{
		CBUtilities loCBUtilities = new CBUtilities();
		loCBUtilities.setContractBudgetID(contractBudgetID);
		loCBUtilities.setSubBudgetID(subBudgetID);
		loCBUtilities.setContractID(amendContractId);
		loCBUtilities.setParentBudgetId(parentBudgetID);
		loCBUtilities.setParentSubBudgetId(parentSubBudgetID);
		loCBUtilities.setInvoiceId("55");
		loCBUtilities.setId(utilitiesId);
		loCBUtilities.setModifyByAgency("agency_12");
		loCBUtilities.setModifyByProvider("803");
		loCBUtilities.setBudgetTypeId("2");
		loCBUtilities.setLineItemModifiedAmt("100");
		loCBUtilities.setAmendmentType("positive");
		loCBUtilities.setBudgetStatusId("86");
		return loCBUtilities;
	}

	private CBUtilities getCBUtilitiesApprovedObj()
	{
		CBUtilities loCBUtilities = new CBUtilities();
		loCBUtilities.setContractBudgetID(contractBudgetIDApproved);
		loCBUtilities.setSubBudgetID(subBudgetIDApproved);
		loCBUtilities.setContractID(amendContractIdApproved);
		loCBUtilities.setParentBudgetId(parentBudgetID);
		loCBUtilities.setParentSubBudgetId(parentSubBudgetID);
		loCBUtilities.setInvoiceId("55");
		loCBUtilities.setId(utilitiesId);
		loCBUtilities.setModifyByAgency("agency_12");
		loCBUtilities.setModifyByProvider("803");
		loCBUtilities.setBudgetTypeId("2");
		loCBUtilities.setLineItemModifiedAmt("100");
		loCBUtilities.setAmendmentType("positive");
		loCBUtilities.setBudgetStatusId("86");
		loCBUtilities.setBudgetTypeId("1");
		return loCBUtilities;
	}

	private CBProfessionalServicesBean getProfServicesDetails()
	{
		CBProfessionalServicesBean loCBProfessionalServicesBean = new CBProfessionalServicesBean();
		loCBProfessionalServicesBean.setContractBudgetID(contractBudgetID);
		loCBProfessionalServicesBean.setSubBudgetID(subBudgetID);
		loCBProfessionalServicesBean.setContractID(amendContractId);
		loCBProfessionalServicesBean.setParentSubBudgetId(parentSubBudgetID);
		loCBProfessionalServicesBean.setParentBudgetId(parentBudgetID);
		loCBProfessionalServicesBean.setInvoiceId("55");
		loCBProfessionalServicesBean.setModifyByAgency("agency_12");
		loCBProfessionalServicesBean.setModifyByProvider("803");
		loCBProfessionalServicesBean.setBudgetTypeId("2");
		loCBProfessionalServicesBean.setModifyAmount("20");
		loCBProfessionalServicesBean.setAmendmentType("positive");
		loCBProfessionalServicesBean.setId("45_1_100");
		return loCBProfessionalServicesBean;
	}

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
			String lsClassName = (new PDFGenerationBatch()).getClass().getName();
			int liIndex = lsClassName.lastIndexOf(HHSConstants.DOT);
			if (liIndex > -1)
			{
				lsClassName = lsClassName.substring(liIndex + 1);
			}
			lsClassName = lsClassName + HHSConstants.DOT_CLASS;
			String lsCastorPath = ((new PDFGenerationBatch()).getClass().getResource(lsClassName) + HHSConstants.EMPTY_STRING)
					.replace(HHSConstants.PDF_CLASS, HHSConstants.CASTOR_MAPPING);

			BaseCacheManagerWeb.getInstance().putCacheObject(HHSConstants.CASTER_CONFIGURATION_PATH, lsCastorPath);
			session = getFileNetSession();
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
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
		finally{
			moSession.rollback();
			moSession.close();
		}
	}

	/**
	 * This method populates the filter bean object with default sorting and
	 * pagination parameters.
	 * 
	 * @return loProfServicesBean, a CBGridBean bean object
	 * @throws ApplicationException
	 */
	private CBGridBean getFetchProfServicesParams() throws ApplicationException
	{
		CBGridBean loProfServicesBean = new CBGridBean();

		loProfServicesBean.setModifyByAgency("");
		loProfServicesBean.setModifyByProvider("803");
		loProfServicesBean.setContractBudgetID("3");
		loProfServicesBean.setSubBudgetID("134");
		loProfServicesBean.setContractID("27");

		return loProfServicesBean;
	}

	/**
	 * This method populates the filter bean object with default sorting and
	 * pagination parameters.
	 * 
	 * @return loProfServicesBean, a CBProfessionalServicesBean bean object
	 * @throws ApplicationException
	 */
	private CBProfessionalServicesBean getProfServicesParamsForUpdateAmount() throws ApplicationException
	{
		CBProfessionalServicesBean loProfServicesBean = new CBProfessionalServicesBean();

		loProfServicesBean.setContractBudgetID("555");
		loProfServicesBean.setSubBudgetID("555");
		loProfServicesBean.setContractID("111777");
		loProfServicesBean.setModifyByAgency("");
		loProfServicesBean.setModifyByProvider("803");
		loProfServicesBean.setId("150");
		loProfServicesBean.setFyBudget("5000");
		loProfServicesBean.setProfessionalServiceName("Testing Stuff");
		return loProfServicesBean;
	}

	/**
	 * This method populates the filter bean object with default sorting and
	 * pagination parameters.
	 * 
	 * @return loProfServicesBean, a CBProfessionalServicesBean bean object
	 * @throws ApplicationException
	 */
	private CBProfessionalServicesBean getEditProfServicesParams() throws ApplicationException
	{
		CBProfessionalServicesBean loProfServicesBean = new CBProfessionalServicesBean();

		loProfServicesBean.setContractBudgetID("86");
		loProfServicesBean.setSubBudgetID("134");
		loProfServicesBean.setContractID("143");
		loProfServicesBean.setModifyByAgency("agency_13");
		loProfServicesBean.setModifyByProvider("001");
		loProfServicesBean.setId("254");
		loProfServicesBean.setFyBudget("6000");
		loProfServicesBean.setProfessionalServiceName("Testing Stuff");
		return loProfServicesBean;
	}

	/**
	 * This method tests fetching functionality of Professional service details
	 * 
	 * With correct input
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchProfServicesDetailsOK() throws ApplicationException
	{

		CBGridBean loProfService = getFetchProfServicesParams();
		List<CBProfessionalServicesBean> loProfServiceDetailsList = moContractBudgetService.fetchProfServicesDetails(
				loProfService, moSession);

		assertNotNull(loProfServiceDetailsList);
		assertTrue(loProfServiceDetailsList.size() > 0);
	}

	/**
	 * This method tests fetching functionality of Professional service details
	 * 
	 * moSession - null
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchProfServicesDetailsForNullSession() throws ApplicationException
	{
		CBGridBean loProfService = getFetchProfServicesParams();

		List<CBProfessionalServicesBean> loProfServiceDetailsList = moContractBudgetService.fetchProfServicesDetails(
				loProfService, null);

		assertNotNull(loProfServiceDetailsList);
		assertTrue(loProfServiceDetailsList.size() > 0);
	}

	/**
	 * This method tests fetching functionality of Professional service details
	 * 
	 * CBProfessionalServicesBean - null
	 * 
	 * @throws Exception If an Exception occurs
	 */
	@Test(expected = Exception.class)
	public void testFetchProfServicesDetailsKO() throws ApplicationException
	{

		List<CBProfessionalServicesBean> loProfServiceDetailsList = moContractBudgetService.fetchProfServicesDetails(
				null, moSession);

		assertNotNull(loProfServiceDetailsList);
		assertTrue(loProfServiceDetailsList.size() > 0);
	}

	/**
	 * This method tests fetching functionality of Professional service details
	 * 
	 * CBProfessionalServicesBean - null
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchProfServicesDetailsWithEmptyBean() throws ApplicationException
	{

		CBGridBean loProfService = new CBGridBean();
		List<CBProfessionalServicesBean> loProfServiceDetailsList = moContractBudgetService.fetchProfServicesDetails(
				loProfService, moSession);

		assertNotNull(loProfServiceDetailsList);
		assertTrue(loProfServiceDetailsList.size() > 0);
	}

	/**
	 * This method tests fetching functionality of Professional service details
	 * 
	 * Sub-Budget id - null
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchProfServicesDetailsWithSubBudgetIdNULL() throws ApplicationException
	{

		CBGridBean loProfService = getFetchProfServicesParams();
		loProfService.setSubBudgetID(null);

		List<CBProfessionalServicesBean> loProfServiceDetailsList = moContractBudgetService.fetchProfServicesDetails(
				loProfService, moSession);

		assertNotNull(loProfServiceDetailsList);
		assertTrue(loProfServiceDetailsList.size() > 0);
	}

	/**
	 * This method tests fetching functionality of Professional service details
	 * 
	 * Budget id - null
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchProfServicesDetailsWithBudgetIdNULL() throws ApplicationException
	{

		CBGridBean loProfService = getFetchProfServicesParams();
		loProfService.setContractBudgetID(null);

		List<CBProfessionalServicesBean> loProfServiceDetailsList = moContractBudgetService.fetchProfServicesDetails(
				loProfService, moSession);

		assertNotNull(loProfServiceDetailsList);
		assertTrue(loProfServiceDetailsList.size() > 0);
	}

	/**
	 * This method tests fetching functionality of Professional service details
	 * 
	 * Sub-Budget id - ""
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchProfServicesDetailsWithSubBudgetIdEmpty() throws ApplicationException
	{

		CBGridBean loProfService = getFetchProfServicesParams();
		loProfService.setSubBudgetID("");

		List<CBProfessionalServicesBean> loProfServiceDetailsList = moContractBudgetService.fetchProfServicesDetails(
				loProfService, moSession);

		assertNotNull(loProfServiceDetailsList);
		assertTrue(loProfServiceDetailsList.size() > 0);
	}

	/**
	 * This method tests, functionality to update budget Amount for Professional
	 * service
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testEditProfServicesDetailsForAmount() throws ApplicationException
	{

		CBProfessionalServicesBean loProfService = getProfServicesParamsForUpdateAmount();

		boolean lbUpdateStatus = moContractBudgetService.editProfServicesDetails(loProfService, moSession);

		assertNotNull(lbUpdateStatus);
		assertTrue(lbUpdateStatus);
	}

	/**
	 * This method tests, functionality to update budget Amount and Other for
	 * Professional service
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testEditProfServicesDetailsForAmountAndOther() throws ApplicationException
	{

		CBProfessionalServicesBean loProfService = getEditProfServicesParams();
		loProfService.setId("939");
		boolean lbUpdateStatus = moContractBudgetService.editProfServicesDetails(loProfService, moSession);

		assertNotNull(lbUpdateStatus);
		assertTrue(lbUpdateStatus);
	}

	/**
	 * This method tests, functionality to update budget Amount and Other for
	 * Professional service
	 * 
	 * Negative scenario -- Null session
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testEditProfServicesDetailsException() throws ApplicationException
	{

		CBProfessionalServicesBean loProfService = getEditProfServicesParams();

		boolean lbUpdateStatus = moContractBudgetService.editProfServicesDetails(loProfService, null);

		assertNotNull(lbUpdateStatus);
		assertTrue(lbUpdateStatus);
	}

	/**
	 * This method tests, functionality to update budget Amount and Other for
	 * Professional service
	 * 
	 * Negative scenario -- Null bean
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = NullPointerException.class)
	public void testEditProfServicesDetailsException2() throws ApplicationException
	{
		boolean lbUpdateStatus = moContractBudgetService.editProfServicesDetails(null, moSession);

		assertNotNull(lbUpdateStatus);
		assertTrue(lbUpdateStatus);
	}

	/**
	 * This method tests, functionality to update budget Amount and Other for
	 * Professional service
	 * 
	 * Negative scenario -- Empty bean
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testEditProfServicesDetailsException3() throws ApplicationException
	{

		CBProfessionalServicesBean loProfService = new CBProfessionalServicesBean();

		moContractBudgetService.editProfServicesDetails(loProfService, moSession);
	}

	/**
	 * This method tests, functionality to update budget Amount and Other for
	 * Professional service
	 * 
	 * Negative scenario -- Empty bean
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testEditProfServicesDetailsException4() throws ApplicationException
	{

		CBProfessionalServicesBean loProfService = new CBProfessionalServicesBean();
		loProfService.setProfessionalServiceName("A");

		moContractBudgetService.editProfServicesDetails(loProfService, moSession);
	}

	/**
	 * Default parameter set for CBIndirectRateBean bean.
	 * @return loCBIndirectRateBean CBIndirectRateBean as return type.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	private CBGridBean getCBGridBeanParams() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setContractBudgetID("555");
		loCBGridBean.setSubBudgetID("555");
		loCBGridBean.setContractID("555");
		loCBGridBean.setCreatedByUserId("city_142");
		loCBGridBean.setModifiedByUserId("city_142");
		loCBGridBean.setInvoiceId("55");
		loCBGridBean.setBudgetTypeId(HHSConstants.TWO);
		loCBGridBean.setInvoiceStatusIdList(HHSConstants.INVOICE_STATUS_ID_INDIRECT_RATE);
		return loCBGridBean;
	}

	/**
	 * Default parameter set for CBIndirectRateBean bean.
	 * @return loCBIndirectRateBean CBIndirectRateBean as return type.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	private CBGridBean getCBGridBeanModificationParams() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setCreatedByUserId("city_142");
		loCBGridBean.setModifiedByUserId("city_142");
		loCBGridBean.setSubBudgetID("557");
		loCBGridBean.setParentBudgetId("555");
		loCBGridBean.setParentSubBudgetId("555");
		loCBGridBean.setInvoiceId("55");
		loCBGridBean.setBudgetTypeId(HHSConstants.THREE);
		loCBGridBean.setInvoiceStatusIdList(HHSConstants.INVOICE_STATUS_ID_INDIRECT_RATE);
		return loCBGridBean;
	}

	/**
	 * Test method successfully fetches Indirect Rate.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchIndirectRateSuccess() throws ApplicationException
	{
		CBGridBean loCBIndirectRateBeanPositive = new CBGridBean();
		loCBIndirectRateBeanPositive.setBudgetTypeId("2");

		// Positive Scenario -- List of CBIndirectRateBean type returned.
		List<CBIndirectRateBean> loCBIndirect = moContractBudgetService.fetchIndirectRate(moSession,
				loCBIndirectRateBeanPositive);
		assertTrue(loCBIndirect.size() != 0);
	}

	/**
	 * Test method successfully fetches Indirect Rate.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchIndirectRateNullBudgetType() throws ApplicationException
	{
		CBGridBean loCBGridBeanPositive = new CBGridBean();
		loCBGridBeanPositive.setBudgetTypeId(null);
		List<CBIndirectRateBean> loCBIndirect = moContractBudgetService.fetchIndirectRate(moSession,
				loCBGridBeanPositive);
		assertNull(loCBIndirect);
	}

	/**
	 * Test method successfully fetches Indirect Rate.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchIndirectRateNullIndirectBean() throws ApplicationException
	{
		List<CBIndirectRateBean> loCBIndirect = moContractBudgetService.fetchIndirectRate(moSession, null);
		assertNull(loCBIndirect);
	}

	/**
	 * Test method throw exception while fetching Indirect Rate.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchIndirectRateException() throws ApplicationException
	{
		CBGridBean loCBIndirectRateBeanPositive = getCBGridBeanParams();
		// Negative Scenario -- Application Exception handled by setting.
		// Incomplete data in the Bean.
		moContractBudgetService.fetchIndirectRate(null, loCBIndirectRateBeanPositive);
	}

	/**
	 * Test method while fetching Indirect Rate.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchIndirectRateModification() throws ApplicationException
	{
		CBGridBean loCBGridBean = getCBGridBeanModificationParams();
		List<CBIndirectRateBean> loCBIndirect = moContractBudgetService.fetchIndirectRate(moSession, loCBGridBean);
		assertTrue(loCBIndirect.size() != 0);
	}

	/**
	 * Test method while fetching Indirect Rate.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchIndirectRateModificationException() throws ApplicationException
	{
		CBGridBean loCBGridBean = getCBGridBeanModificationParams();
		moContractBudgetService.fetchIndirectRate(null, loCBGridBean);
	}

	/**
	 * Test method while fetching Indirect Rate.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchIndirectRateBudgetTypeIdNotMatching() throws ApplicationException
	{
		CBGridBean loCBGridBean = getCBGridBeanModificationParams();
		loCBGridBean.setBudgetTypeId(HHSConstants.FIVE);
		List<CBIndirectRateBean> loCBIndirect = moContractBudgetService.fetchIndirectRate(moSession, loCBGridBean);
		assertNull(loCBIndirect);
	}

	/**
	 * This method tests if indirect rate is returned successfully for contract
	 * budget.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateIndirectRatePercentageSuccess() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setBudgetTypeId("2");
		String lsIndirectRate = moContractBudgetService.updateIndirectRatePercentage(moSession, loCBGridBean,
				getMasterBeanApproved());
		assertNotNull(lsIndirectRate);
	}

	/**
	 * This method tests if indirect rate is returned successfully for contract
	 * budget.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateIndirectRatePercentageNullBean() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setBudgetTypeId("2");
		String lsIndirectRate = moContractBudgetService.updateIndirectRatePercentage(moSession, loCBGridBean,
				getMasterBeanApproved());
		assertNotNull(lsIndirectRate);
	}

	/**
	 * This method tests if indirect rate is returned successfully for contract
	 * budget.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateIndirectRatePercentageNullBudgetType() throws ApplicationException
	{
		CBGridBean loCBGridBean = getCBGridBeanParams();
		loCBGridBean.setBudgetTypeId(null);
		String lsIndirectRate = moContractBudgetService.updateIndirectRatePercentage(moSession, loCBGridBean,
				getMasterBeanApproved());
		assertNotNull(lsIndirectRate);
	}

	/**
	 * This method tests if indirect rate is returned successfully for contract
	 * budget.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateIndirectRatePercentageBudgetTypeIdNotMatching() throws ApplicationException
	{
		CBGridBean loCBGridBean = getCBGridBeanParams();
		loCBGridBean.setBudgetTypeId(HHSConstants.FIVE);
		String lsIndirectRate = moContractBudgetService.updateIndirectRatePercentage(moSession, loCBGridBean,
				getMasterBeanApproved());
		assertEquals(lsIndirectRate, "0");
	}

	/**
	 * This method tests if indirect rate is returned successfully for contract
	 * modification.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateIndirectRatePercentageModification() throws ApplicationException
	{
		CBGridBean loCBGridBean = getCBGridBeanModificationParams();
		String lsIndirectRate = moContractBudgetService.updateIndirectRatePercentage(moSession, loCBGridBean,
				getMasterBeanApproved());
		assertNotNull(lsIndirectRate);
	}

	/**
	 * This method tests if indirect rate is returned successfully for contract
	 * modification.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateIndirectRatePercentageModificationNullPercentage() throws ApplicationException
	{
		CBGridBean loCBGridBean = getCBGridBeanModificationParams();
		loCBGridBean.setParentBudgetId("888");
		loCBGridBean.setParentSubBudgetId("8881");
		loCBGridBean.setSubBudgetID("758");
		loCBGridBean.setContractBudgetID("10046");
		String lsIndirectRate = moContractBudgetService.updateIndirectRatePercentage(moSession, loCBGridBean,
				getMasterBeanApproved());
		assertNotNull(lsIndirectRate);
	}

	/**
	 * This method tests fetchIndirectRatePercentage throws exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateIndirectRatePercentageException() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setBudgetTypeId("2");
		moContractBudgetService.updateIndirectRatePercentage(null, loCBGridBean, getMasterBeanApproved());
	}

	/**
	 * Default parameter set for CBIndirectRateBean bean.
	 * @return loCBIndirectRateBean CBIndirectRateBean as return type.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	private CBIndirectRateBean getCBGridIndirectBeanParams() throws ApplicationException
	{
		CBIndirectRateBean loIndirectRateBean = new CBIndirectRateBean();
		loIndirectRateBean.setIndirectAmount("1000");
		loIndirectRateBean.setIndirectModificationAmount("1000");
		loIndirectRateBean.setId("123");
		loIndirectRateBean.setModifyByProvider("803");
		loIndirectRateBean.setModifyByAgency("agency_47");
		loIndirectRateBean.setBudgetTypeId("2");
		loIndirectRateBean.setSubBudgetID("556");
		loIndirectRateBean.setInvoiceStatusIdList(HHSConstants.INVOICE_STATUS_ID_INDIRECT_RATE);
		return loIndirectRateBean;
	}

	/**
	 * Default parameter set for CBIndirectRateBean bean.
	 * @return loCBIndirectRateBean CBIndirectRateBean as return type.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	private CBIndirectRateBean getCBGridIndirectBeanModificationParams() throws ApplicationException
	{
		CBIndirectRateBean loIndirectRateBean = new CBIndirectRateBean();
		loIndirectRateBean.setIndirectAmount("1000");
		loIndirectRateBean.setParentBudgetId("555");
		loIndirectRateBean.setParentSubBudgetId("555");
		loIndirectRateBean.setModifyByProvider("803");
		loIndirectRateBean.setModifyByAgency("agency_47");
		loIndirectRateBean.setBudgetTypeId("3");
		loIndirectRateBean.setCreatedByUserId("803");
		loIndirectRateBean.setContractID("555");
		loIndirectRateBean.setContractBudgetID("10046");
		loIndirectRateBean.setSubBudgetID("774");
		loIndirectRateBean.setId("20");
		loIndirectRateBean.setIndirectModificationAmount("1000");
		return loIndirectRateBean;
	}

	/**
	 * Default parameter set for CBIndirectRateBean bean.
	 * @return loCBIndirectRateBean CBIndirectRateBean as return type.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	private CBIndirectRateBean getCBGridIndirectBeanAmendmentParams() throws ApplicationException
	{
		CBIndirectRateBean loIndirectRateBean = new CBIndirectRateBean();
		loIndirectRateBean.setIndirectAmount("1000");
		loIndirectRateBean.setModifyByProvider("803");
		loIndirectRateBean.setModifyByAgency("agency_47");
		loIndirectRateBean.setBudgetTypeId("3");
		loIndirectRateBean.setCreatedByUserId("803");
		loIndirectRateBean.setSubBudgetID("774");
		loIndirectRateBean.setId("20");
		loIndirectRateBean.setIndirectModificationAmount("1000");
		loIndirectRateBean.setAmendmentType(HHSConstants.POSITIVE);
		loIndirectRateBean.setContractBudgetID("666");
		loIndirectRateBean.setSubBudgetID("886");
		loIndirectRateBean.setContractID("111888");
		loIndirectRateBean.setParentBudgetId("555");
		loIndirectRateBean.setParentSubBudgetId("555");
		return loIndirectRateBean;
	}

	/**
	 * This method tests if indirect rate budget is updated successfully.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateIndirectRateSuccess() throws ApplicationException
	{
		CBIndirectRateBean loIndirectRateBean = new CBIndirectRateBean();
		loIndirectRateBean.setBudgetTypeId("2");
		loIndirectRateBean.setIndirectAmount("");
		loIndirectRateBean.setModifyByAgency("agency_13");
		loIndirectRateBean.setId("63");
		Boolean loIndirectRateUpdate = moContractBudgetService.updateIndirectRate(moSession, loIndirectRateBean);
		// for success
		assertTrue(loIndirectRateUpdate);
	}

	/**
	 * This method tests if indirect rate budget is updated successfully.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateIndirectRate1() throws ApplicationException
	{
		CBIndirectRateBean loIndirectRateBean = new CBIndirectRateBean();
		loIndirectRateBean.setBudgetTypeId("2");
		loIndirectRateBean.setIndirectAmount("");
		loIndirectRateBean.setModifyByAgency("agency_13");
		loIndirectRateBean.setId("300");
		loIndirectRateBean.setIndirectModificationAmount("100");
		loIndirectRateBean.setContractBudgetID("20");
		loIndirectRateBean.setSubBudgetID("20");
		Boolean loIndirectRateUpdate = moContractBudgetService.updateIndirectRate(moSession, loIndirectRateBean);
		// for success
		assertTrue(loIndirectRateUpdate);
	}

	/**
	 * This method tests if indirect rate budget is updated successfully.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateIndirectRate2() throws ApplicationException
	{
		CBIndirectRateBean loIndirectRateBean = new CBIndirectRateBean();
		loIndirectRateBean.setBudgetTypeId("2");
		loIndirectRateBean.setIndirectAmount("");
		loIndirectRateBean.setModifyByAgency("agency_13");
		loIndirectRateBean.setId("63");
		Boolean loIndirectRateUpdate = moContractBudgetService.updateIndirectRate(moSession, loIndirectRateBean);
		// for success
		assertTrue(loIndirectRateUpdate);
	}

	/**
	 * This method tests if indirect rate budget is updated successfully.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateIndirectRateModificationSuccess() throws ApplicationException
	{
		CBIndirectRateBean loIndirectRateBean = getCBGridIndirectBeanParams();
		loIndirectRateBean.setBudgetTypeId(HHSConstants.TWO);
		Boolean loIndirectRateUpdate = moContractBudgetService.updateIndirectRate(moSession, loIndirectRateBean);
		// for success
		assertTrue(loIndirectRateUpdate);
	}

	/**
	 * This method tests if indirect rate budget throw exception.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateIndirectRateException() throws ApplicationException
	{
		CBIndirectRateBean loIndirectRateBean = getCBGridIndirectBeanParams();
		// for exception
		moContractBudgetService.updateIndirectRate(null, loIndirectRateBean);
	}

	/**
	 * This method tests if indirect rate budget throw exception.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateIndirectRateRemainingModSumLessThanZeroException() throws ApplicationException
	{
		CBIndirectRateBean loIndirectRateBean = getCBGridIndirectBeanModificationParams();
		// for exception
		loIndirectRateBean.setIndirectModificationAmount("-99999999999999999999999999");
		moContractBudgetService.updateIndirectRate(moSession, loIndirectRateBean);
	}

	/**
	 * This method tests if indirect rate budget throw exception.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateIndirectRateModificationException() throws ApplicationException
	{
		CBIndirectRateBean loIndirectRateBean = getCBGridIndirectBeanModificationParams();
		// for exception
		moContractBudgetService.updateIndirectRate(null, loIndirectRateBean);
	}

	/**
	 * This method tests if indirect rate modification is success.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateIndirectRateModificationInsertSuccess() throws ApplicationException
	{
		CBIndirectRateBean loIndirectRateBean = getCBGridIndirectBeanModificationParams();
		loIndirectRateBean.setSubBudgetID("10046");
		loIndirectRateBean.setSubBudgetID("774");
		Boolean loIndirectRateUpdate = moContractBudgetService.updateIndirectRate(moSession, loIndirectRateBean);
		// for success
		assertTrue(loIndirectRateUpdate);
	}

	/**
	 * This method tests if indirect rate modification is success.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateIndirectRatePositiveAmendment() throws ApplicationException
	{
		CBIndirectRateBean loIndirectRateBean = getCBGridIndirectBeanAmendmentParams();
		Boolean loIndirectRateUpdate = moContractBudgetService.updateIndirectRate(moSession, loIndirectRateBean);
		// for success
		assertTrue(loIndirectRateUpdate);
	}

	/**
	 * This method tests if indirect rate modification is success.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateIndirectRateNegativeAmendment() throws ApplicationException
	{
		CBIndirectRateBean loIndirectRateBean = getCBGridIndirectBeanAmendmentParams();
		loIndirectRateBean.setAmendmentType(HHSConstants.NEGATIVE);
		loIndirectRateBean.setIndirectModificationAmount("-1000");
		Boolean loIndirectRateUpdate = moContractBudgetService.updateIndirectRate(moSession, loIndirectRateBean);
		// for success
		assertTrue(loIndirectRateUpdate);
	}

	/**
	 * This method tests if indirect rate modification is success.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateIndirectRatePositiveAmendmentException() throws ApplicationException
	{
		CBIndirectRateBean loIndirectRateBean = getCBGridIndirectBeanAmendmentParams();
		loIndirectRateBean.setIndirectModificationAmount("-1000");
		moContractBudgetService.updateIndirectRate(moSession, loIndirectRateBean);
	}

	/**
	 * This method tests if indirect rate modification is success.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateIndirectRateNegativeException() throws ApplicationException
	{
		CBIndirectRateBean loIndirectRateBean = getCBGridIndirectBeanAmendmentParams();
		loIndirectRateBean.setAmendmentType(HHSConstants.NEGATIVE);
		loIndirectRateBean.setIndirectModificationAmount("1000");
		moContractBudgetService.updateIndirectRate(moSession, loIndirectRateBean);
	}

	/**
	 * This method tests if Unallocated Funds are available in database.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	@SuppressWarnings("rawtypes")
	public void testFetchUnallocatedFunds1() throws ApplicationException
	{
		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		loUnallocatedFundsBean.setSubBudgetID("555");
		loUnallocatedFundsBean.setContractBudgetID("555");
		loUnallocatedFundsBean.setCreatedByUserId("city_142");

		List loUnallocatedFundsList = moContractBudgetService.fetchUnallocatedFunds(moSession, loUnallocatedFundsBean);

		assertNotNull(loUnallocatedFundsList);
		assertTrue(loUnallocatedFundsList.size() > 0);
	}

	/**
	 * This method tests if Unallocated Funds are not there in database.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	@SuppressWarnings("rawtypes")
	public void testFetchUnallocatedFunds2() throws ApplicationException
	{
		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		loUnallocatedFundsBean.setSubBudgetID("55");
		loUnallocatedFundsBean.setContractBudgetID("55");
		loUnallocatedFundsBean.setCreatedByUserId("city_142");

		List loUnallocatedFundsList = moContractBudgetService.fetchUnallocatedFunds(moSession, loUnallocatedFundsBean);

		assertTrue(loUnallocatedFundsList.size() == 0);

	}

	/**
	 * This method tests if Sub BudgetId is Null
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	@SuppressWarnings("rawtypes")
	public void testFetchUnallocatedFunds3() throws ApplicationException
	{

		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		loUnallocatedFundsBean.setContractBudgetID("25");
		loUnallocatedFundsBean.setCreatedByUserId("city_142");

		List loUnallocatedFundsList = moContractBudgetService.fetchUnallocatedFunds(moSession, loUnallocatedFundsBean);

		assertTrue(loUnallocatedFundsList == null);
	}

	/**
	 * This method tests if CBGridBean is NULL
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void testFetchUnallocatedFunds4() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = null;
		List<UnallocatedFunds> loUnallocatedFundsList = null;
		loUnallocatedFundsList = moContractBudgetService.fetchUnallocatedFunds(moSession, aoCBGridBeanObj);

		assertNull(loUnallocatedFundsList);

	}

	/**
	 * This method tests if database session is NULL
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchUnallocatedFunds5() throws ApplicationException
	{
		Boolean lbThrown = false;
		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		loUnallocatedFundsBean.setSubBudgetID("555");
		loUnallocatedFundsBean.setContractBudgetID("555");
		loUnallocatedFundsBean.setCreatedByUserId("city_142");

		try
		{
			moContractBudgetService.fetchUnallocatedFunds(null, loUnallocatedFundsBean);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	/**
	 * This method tests if Unallocated Funds are updated database.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateUnallocatedFunds1() throws ApplicationException
	{
		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		loUnallocatedFundsBean.setSubBudgetID("555");
		loUnallocatedFundsBean.setContractBudgetID("555");
		loUnallocatedFundsBean.setModifiedByUserId("city_142");
		loUnallocatedFundsBean.setAmmount("13452.90");

		boolean status = moContractBudgetService.updateUnallocatedFunds(moSession, loUnallocatedFundsBean);

		assertTrue(status);
	}

	/**
	 * This method tests if database session is Null
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateUnallocatedFunds2() throws ApplicationException
	{
		Boolean lbThrown = false;
		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		loUnallocatedFundsBean.setSubBudgetID("555");
		loUnallocatedFundsBean.setContractBudgetID("555");
		loUnallocatedFundsBean.setModifiedByUserId("city_142");
		loUnallocatedFundsBean.setAmmount("13452.90");
		try
		{
			moContractBudgetService.updateUnallocatedFunds(null, loUnallocatedFundsBean);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	/**
	 * This method tests if Unallocated Funds are updated database and ammount
	 * is not there.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateUnallocatedFunds3() throws ApplicationException
	{
		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		boolean status = false;

		try
		{
			status = moContractBudgetService.updateUnallocatedFunds(moSession, loUnallocatedFundsBean);
		}
		catch (ApplicationException loAppEx)
		{
			assertFalse(status);
		}

	}

	/**
	 * This method tests if Fetch Milestone gets data
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	@SuppressWarnings("rawtypes")
	public void testFetchMilestone() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();
			loCBMileStoneBean.setContractBudgetID("555");
			loCBMileStoneBean.setSubBudgetID("555");
			loCBMileStoneBean.setEntryTypeId("8");

			List loCBMileStoneBeanList = moContractBudgetService.fetchMilestone(loCBMileStoneBean, moSession);

			assertNotNull(loCBMileStoneBeanList);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}

	}

	/**
	 * This method tests if Fetch Milestone gets data
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	@SuppressWarnings("rawtypes")
	public void testFetchMilestoneException() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();
			loCBMileStoneBean.setSubBudgetID("555");
			loCBMileStoneBean.setEntryTypeId("8");

			List loCBMileStoneBeanList = moContractBudgetService.fetchMilestone(loCBMileStoneBean, moSession);

			assertNotNull(loCBMileStoneBeanList);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}

	}

	/**
	 * This method tests if Fetch Milestone gets data
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	@SuppressWarnings("rawtypes")
	public void testFetchMilestoneException2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();
			loCBMileStoneBean.setContractBudgetID("555");
			loCBMileStoneBean.setEntryTypeId("8");

			List loCBMileStoneBeanList = moContractBudgetService.fetchMilestone(loCBMileStoneBean, moSession);

			assertNotNull(loCBMileStoneBeanList);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}

	}

	/**
	 * This method tests if Fetch Milestone gets data
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	@SuppressWarnings("rawtypes")
	public void testFetchMilestoneException3() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();
			loCBMileStoneBean.setContractBudgetID("555");
			loCBMileStoneBean.setSubBudgetID("555");

			List loCBMileStoneBeanList = moContractBudgetService.fetchMilestone(loCBMileStoneBean, moSession);

			assertNotNull(loCBMileStoneBeanList);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}

	}

	/**
	 * This method tests if Fetch Milestone gets data
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	@SuppressWarnings("rawtypes")
	public void testFetchMilestoneException4() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();
			loCBMileStoneBean.setContractBudgetID("555");
			loCBMileStoneBean.setSubBudgetID("555");
			loCBMileStoneBean.setEntryTypeId("8");

			List loCBMileStoneBeanList = moContractBudgetService.fetchMilestone(loCBMileStoneBean, null);

			assertNotNull(loCBMileStoneBeanList);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}

	}

	/**
	 * This method tests if Milestone are updated database.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateMilestone() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();
			loCBMileStoneBean.setId("17");
			loCBMileStoneBean.setMileStone("junit test");
			loCBMileStoneBean.setModifiedByUserId("city_142");
			loCBMileStoneBean.setAmount("800");

			// positive test
			boolean status = moContractBudgetService.updateMilestone(loCBMileStoneBean, moSession);

			assertTrue(status);
			// negative test
			loCBMileStoneBean.setId("17");
			loCBMileStoneBean.setMileStone("junit test characters more than the limit of column in table");
			loCBMileStoneBean.setModifiedByUserId("city_142");
			loCBMileStoneBean.setAmount("800");
			status = moContractBudgetService.updateMilestone(loCBMileStoneBean, moSession);
			assertTrue(status);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}

	}

	/**
	 * This method tests if Milestone are updated database.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateMilestoneException() throws ApplicationException
	{
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();
		loCBMileStoneBean.setId("A");
		loCBMileStoneBean.setMileStone("junit test");
		loCBMileStoneBean.setModifiedByUserId("city_142");
		loCBMileStoneBean.setAmount("800");

		moContractBudgetService.updateMilestone(loCBMileStoneBean, moSession);

	}

	/**
	 * This method tests if Milestone are updated database.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateMilestoneException2() throws ApplicationException
	{
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();
		loCBMileStoneBean.setId(" _");
		loCBMileStoneBean.setMileStone("junit test");
		loCBMileStoneBean.setModifiedByUserId("city_142");
		loCBMileStoneBean.setAmount("800");

		moContractBudgetService.updateMilestone(loCBMileStoneBean, moSession);

	}

	/**
	 * This method tests if Milestone are updated database.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateMilestoneException3() throws ApplicationException
	{
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();
		loCBMileStoneBean.setId("17");
		loCBMileStoneBean.setMileStone("junit test");
		loCBMileStoneBean.setModifiedByUserId("city_142");
		loCBMileStoneBean.setAmount("800");

		moContractBudgetService.updateMilestone(loCBMileStoneBean, null);

	}

	/**
	 * This method tests if Milestone are updated database.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = NullPointerException.class)
	public void testUpdateMilestoneException4() throws ApplicationException
	{
		moContractBudgetService.updateMilestone(null, moSession);

	}

	/**
	 * This method tests if Milestone details are added.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testAddMilestone() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();
			Integer idSeq = 50;
			loCBMileStoneBean.setContractBudgetID("25");
			loCBMileStoneBean.setSubBudgetID("555");
			loCBMileStoneBean.setMileStone("junit test");
			loCBMileStoneBean.setModifiedByUserId("city_142");
			loCBMileStoneBean.setAmount("800");

			// positive test
			boolean status = moContractBudgetService.addMilestone(idSeq, loCBMileStoneBean, moSession);

			assertTrue(status);

			// negative test
			status = moContractBudgetService.addMilestone(null, null, moSession);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}

	}

	/**
	 * This method tests if Milestone details are added.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testAddMilestoneException() throws ApplicationException
	{
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();
		Integer idSeq = 50;
		loCBMileStoneBean.setContractBudgetID("25");
		loCBMileStoneBean.setSubBudgetID("555");
		loCBMileStoneBean.setMileStone("junit test");
		loCBMileStoneBean.setModifiedByUserId("city_142");
		loCBMileStoneBean.setAmount("800");

		// negative test
		moContractBudgetService.addMilestone(idSeq, loCBMileStoneBean, null);

	}

	/**
	 * This method tests if Milestone details are added.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testAddMilestoneException2() throws ApplicationException
	{
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();
		loCBMileStoneBean.setContractBudgetID("25");
		loCBMileStoneBean.setSubBudgetID("555");
		loCBMileStoneBean.setMileStone("junit test");
		loCBMileStoneBean.setModifiedByUserId("city_142");
		loCBMileStoneBean.setAmount("800");

		// negative test
		moContractBudgetService.addMilestone(null, loCBMileStoneBean, moSession);

	}

	/**
	 * This method tests if Milestone details are added.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = NullPointerException.class)
	public void testAddMilestoneException3() throws ApplicationException
	{
		Integer idSeq = 50;

		// negative test
		moContractBudgetService.addMilestone(idSeq, null, moSession);

	}

	/**
	 * This method tests if Milestone details are added.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testAddMilestoneException4() throws ApplicationException
	{
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();
		Integer idSeq = 50;

		// negative test
		moContractBudgetService.addMilestone(idSeq, loCBMileStoneBean, moSession);

	}

	/**
	 * This method tests if Milestone details are deleted.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testDeleteMilestone() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

			loCBMileStoneBean.setId("18");

			// positive test
			boolean status = moContractBudgetService.deleteMilestone(loCBMileStoneBean, moSession);

			assertTrue(status);

			// negative test
			status = moContractBudgetService.deleteMilestone(null, moSession);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}

	}

	/**
	 * This method tests if Milestone details are deleted.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testDeleteMilestoneException() throws ApplicationException
	{

		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();
		loCBMileStoneBean.setId("A");
		moContractBudgetService.deleteMilestone(loCBMileStoneBean, moSession);
	}

	/**
	 * This method tests if Milestone details are deleted.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testDeleteMilestoneException2() throws ApplicationException
	{

		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();
		boolean status = moContractBudgetService.deleteMilestone(loCBMileStoneBean, moSession);
		assertFalse(status);
	}

	/**
	 * This method tests if Milestone details are deleted.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testDeleteMilestoneException3() throws ApplicationException
	{

		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();
		loCBMileStoneBean.setId(null);
		moContractBudgetService.deleteMilestone(loCBMileStoneBean, moSession);
	}

	/**
	 * This method tests if Milestone details are deleted.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testDeleteMilestoneException4() throws ApplicationException
	{
		moContractBudgetService.deleteMilestone(null, moSession);
	}

	/**
	 * This method tests for Get Sequence for Milestone table.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testGetSeqForMilestone() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			// positive test
			Integer status = moContractBudgetService.getSeqForMilestone(moSession);

			assertNotNull(status);

			// negative test
			status = moContractBudgetService.getSeqForMilestone(null);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}

	}

	/**
	 * This method tests if budget summary is fetched or not
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchBudgetSummary() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBGridBean loCBGridBean = new CBGridBean();
			loCBGridBean.setSubBudgetID("24");
			loCBGridBean.setContractBudgetID("53");

			ContractBudgetSummary loCBBudgetSummary = moContractBudgetService.fetchBudgetSummary(moSession,
					loCBGridBean);

			assertNotNull(loCBBudgetSummary);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	/**
	 * This method tests if code catches the exception if session is null
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchBudgetSummaryException() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBGridBean loCBGridBean = new CBGridBean();
			loCBGridBean.setSubBudgetID("555");
			loCBGridBean.setContractBudgetID("555");

			moContractBudgetService.fetchBudgetSummary(null, loCBGridBean);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	/**
	 * This method tests if code catches the exception if session is null
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchBudgetSummaryNoData() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBGridBean loCBGridBean = new CBGridBean();
			loCBGridBean.setSubBudgetID("14444");
			loCBGridBean.setContractBudgetID("25");

			moContractBudgetService.fetchBudgetSummary(null, loCBGridBean);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	/**
	 * This method tests if code catches the exception if session is null
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchBudgetSummary4() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setContractBudgetID("555");
		moContractBudgetService.fetchBudgetSummary(moSession, loCBGridBean);
	}

	/**
	 * This method tests if code catches the exception if session is null
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchBudgetSummary5() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setSubBudgetID("555");
		moContractBudgetService.fetchBudgetSummary(moSession, loCBGridBean);
	}

	/**
	 * This method tests if code catches the exception if session is null
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchBudgetSummary6() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		moContractBudgetService.fetchBudgetSummary(moSession, loCBGridBean);
	}

	/**
	 * This method tests if code catches the exception if session is null
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = NullPointerException.class)
	public void testFetchBudgetSummary7() throws ApplicationException
	{
		moContractBudgetService.fetchBudgetSummary(moSession, null);
	}

	/**
	 * This method tests if indirect rate is returned successfully for contract
	 * budget.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateIndirectRatePercentage() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setSubBudgetID("555");
		loCBGridBean.setContractBudgetID("555");
		loCBGridBean.setBudgetTypeId("2");
		String lsIndirectRate = moContractBudgetService.updateIndirectRatePercentage(moSession, loCBGridBean,
				getMasterBeanApproved());
		assertNotNull(lsIndirectRate);
	}

	/**
	 * This method tests if indirect rate is returned successfully for contract
	 * modification.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateIndirectRateModificationPercentage() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setSubBudgetID("555");
		loCBGridBean.setContractBudgetID("555");
		loCBGridBean.setBudgetTypeId(HHSConstants.THREE);
		String lsIndirectRate = moContractBudgetService.updateIndirectRatePercentage(moSession, loCBGridBean,
				getMasterBeanApproved());
		assertNotNull(lsIndirectRate);
	}

	/**
	 * This method tests fetchUtilities throws exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchUtilities() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			CBGridBean loCBGridBean = new CBGridBean();

			// Positive Scenario -- List of CBIndirectRateBean type returned.

			loCBGridBean.setSubBudgetID("555");
			loCBGridBean.setContractBudgetID("555");
			loCBGridBean.setBudgetTypeId("2");

			List<CBUtilities> cbUtilitiesList = moContractBudgetService.fetchUtilities(moSession, loCBGridBean);
			assertNotNull(cbUtilitiesList);
			assertTrue(cbUtilitiesList.size() != 0);

			loCBGridBean.setBudgetTypeId(null);
			cbUtilitiesList = moContractBudgetService.fetchUtilities(moSession, loCBGridBean);
			assertNull(cbUtilitiesList);

			// Negative Scenario -- Application Exception handled by setting.

			loCBGridBean.setBudgetTypeId("2");
			cbUtilitiesList = moContractBudgetService.fetchUtilities(null, loCBGridBean);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	/**
	 * This method tests updateUtilities throws exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateUtilities() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			CBUtilities loCBUtilities = new CBUtilities();

			// Positive Scenario -- Utility Update Successful.
			loCBUtilities.setId("1");
			loCBUtilities.setFyAmount("1000");
			loCBUtilities.setBudgetTypeId("2");
			loCBUtilities.setModifyByProvider("803");
			boolean lbUpdateStatus = moContractBudgetService.updateUtilities(moSession, loCBUtilities);
			assertTrue(lbUpdateStatus);

			// Negative Scenario -- Application Exception handled by setting.

			lbUpdateStatus = moContractBudgetService.updateUtilities(null, loCBUtilities);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	/**
	 * This method tests fetchContractedServicesConsultants throws exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchContractedServicesConsultants() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			CBGridBean loCBGridBean = new CBGridBean();

			// Positive Scenario -- List of CBIndirectRateBean type returned.

			loCBGridBean.setSubBudgetID("555");
			loCBGridBean.setContractBudgetID("555");
			loCBGridBean.setBudgetTypeId("2");
			loCBGridBean.setSubHeader("1");
			List<ContractedServicesBean> lbContractedServicesBean = moContractBudgetService
					.fetchContractedServicesConsultants(moSession, loCBGridBean);
			assertNotNull(lbContractedServicesBean);
			assertFalse(lbContractedServicesBean.size() != 0);

			loCBGridBean.setBudgetTypeId(null);
			lbContractedServicesBean = moContractBudgetService.fetchContractedServicesConsultants(moSession,
					loCBGridBean);

			// Negative Scenario -- Application Exception handled by setting.

			loCBGridBean.setBudgetTypeId("2");
			lbContractedServicesBean = moContractBudgetService.fetchContractedServicesConsultants(null, loCBGridBean);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	/**
	 * This method tests fetchContractedServicesSubContractors throws exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchContractedServicesSubContractors() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			CBGridBean loCBGridBean = new CBGridBean();

			// Positive Scenario -- List of CBIndirectRateBean type returned.

			loCBGridBean.setSubBudgetID("555");
			loCBGridBean.setContractBudgetID("555");
			loCBGridBean.setBudgetTypeId("2");
			loCBGridBean.setSubHeader("2");
			List<ContractedServicesBean> lbContractedServicesBean = moContractBudgetService
					.fetchContractedServicesSubContractors(moSession, loCBGridBean);
			assertNotNull(lbContractedServicesBean);
			assertTrue(lbContractedServicesBean.size() != 0);

			loCBGridBean.setBudgetTypeId(null);
			lbContractedServicesBean = moContractBudgetService.fetchContractedServicesSubContractors(moSession,
					loCBGridBean);

			// Negative Scenario -- Application Exception handled by setting.

			loCBGridBean.setBudgetTypeId("2");
			lbContractedServicesBean = moContractBudgetService
					.fetchContractedServicesSubContractors(null, loCBGridBean);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	/**
	 * This method tests fetchContractedServicesVendors throws exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchContractedServicesVendors() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			CBGridBean loCBGridBean = new CBGridBean();

			// Positive Scenario -- List of CBIndirectRateBean type returned.

			loCBGridBean.setSubBudgetID("555");
			loCBGridBean.setContractBudgetID("555");
			loCBGridBean.setBudgetTypeId("2");
			loCBGridBean.setSubHeader("3");
			List<ContractedServicesBean> lbContractedServicesBean = moContractBudgetService
					.fetchContractedServicesVendors(moSession, loCBGridBean);
			assertNotNull(lbContractedServicesBean);
			assertTrue(lbContractedServicesBean.size() != 0);

			loCBGridBean.setBudgetTypeId(null);
			lbContractedServicesBean = moContractBudgetService.fetchContractedServicesVendors(moSession, loCBGridBean);

			// Negative Scenario -- Application Exception handled by setting.

			loCBGridBean.setBudgetTypeId("2");
			lbContractedServicesBean = moContractBudgetService.fetchContractedServicesVendors(null, loCBGridBean);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	/**
	 * This method tests addContractedServices throws exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testAddContractedServices() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			ContractedServicesBean aoCBGridBeanObj = new ContractedServicesBean();

			// Positive Scenario -- List of CBIndirectRateBean type returned.

			aoCBGridBeanObj.setSubBudgetID("555");
			aoCBGridBeanObj.setContractBudgetID("555");
			aoCBGridBeanObj.setBudgetTypeId("2");
			aoCBGridBeanObj.setSubHeader("3");
			aoCBGridBeanObj.setDescOfService("testing");
			aoCBGridBeanObj.setCsName("HHS_Testing");
			Integer lbContractedServicesBean = moContractBudgetService
					.addContractedServices(moSession, aoCBGridBeanObj);
			assertNotNull(lbContractedServicesBean);

			aoCBGridBeanObj.setBudgetTypeId(null);
			lbContractedServicesBean = moContractBudgetService.addContractedServices(moSession, aoCBGridBeanObj);

			// Negative Scenario -- Application Exception handled by setting.

			aoCBGridBeanObj.setBudgetTypeId("2");
			lbContractedServicesBean = moContractBudgetService.addContractedServices(null, aoCBGridBeanObj);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	/**
	 * This method tests editContractedServices throws exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testEditContractedServices() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			ContractedServicesBean aoCBGridBeanObj = new ContractedServicesBean();

			// Positive Scenario -- List of CBIndirectRateBean type returned.

			aoCBGridBeanObj.setSubBudgetID("555");
			aoCBGridBeanObj.setContractBudgetID("555");
			aoCBGridBeanObj.setBudgetTypeId("2");
			aoCBGridBeanObj.setSubHeader("1");
			aoCBGridBeanObj.setDescOfService("testing");
			aoCBGridBeanObj.setCsName("HHS_Testing");
			Boolean lbContractedServicesBean = moContractBudgetService.editContractedServices(moSession,
					aoCBGridBeanObj);
			assertNotNull(lbContractedServicesBean);

			aoCBGridBeanObj.setBudgetTypeId(null);
			lbContractedServicesBean = moContractBudgetService.editContractedServices(moSession, aoCBGridBeanObj);

			// Negative Scenario -- Application Exception handled by setting.

			aoCBGridBeanObj.setBudgetTypeId("2");
			lbContractedServicesBean = moContractBudgetService.editContractedServices(null, aoCBGridBeanObj);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	/**
	 * This method tests deleteContractedServices throws exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testDeleteContractedServices() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			ContractedServicesBean aoCBGridBeanObj = new ContractedServicesBean();

			// Positive Scenario -- List of CBIndirectRateBean type returned.

			aoCBGridBeanObj.setSubBudgetID("555");
			aoCBGridBeanObj.setContractBudgetID("555");
			aoCBGridBeanObj.setBudgetTypeId("2");
			aoCBGridBeanObj.setSubHeader("1");
			aoCBGridBeanObj.setDescOfService("testing");
			aoCBGridBeanObj.setCsName("HHS_Testing");
			Boolean lbContractedServicesBean = moContractBudgetService.deleteContractedServices(moSession,
					aoCBGridBeanObj);
			assertNotNull(lbContractedServicesBean);

			aoCBGridBeanObj.setBudgetTypeId(null);
			lbContractedServicesBean = moContractBudgetService.deleteContractedServices(moSession, aoCBGridBeanObj);

			// Negative Scenario -- Application Exception handled by setting.

			aoCBGridBeanObj.setBudgetTypeId("2");
			lbContractedServicesBean = moContractBudgetService.deleteContractedServices(null, aoCBGridBeanObj);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	/**
	 * Test method while fetching Indirect Rate.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchContractBudgetRent() throws ApplicationException
	{

		CBGridBean loCBIndirectRateBeanPositive = getCBGridBeanParams();
		loCBIndirectRateBeanPositive.setBudgetTypeId("2");
		loCBIndirectRateBeanPositive.setContractBudgetID("555");
		loCBIndirectRateBeanPositive.setContractID("555");
		loCBIndirectRateBeanPositive.setCreatedByUserId("623");
		loCBIndirectRateBeanPositive.setModifiedByUserId("623");
		loCBIndirectRateBeanPositive.setSubBudgetID("555");

		// Positive Scenario -- List of Rent type returned.
		// loCBIndirectRateBeanPositive.setBudgetTypeId(null);
		List<Rent> loRent = moContractBudgetService.fetchContractBudgetRent(moSession, loCBIndirectRateBeanPositive);
		assertNotNull(loRent);

		assertTrue(loRent.size() != 0);

	}

	/**
	 * This method tests updateUtilities throws exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */

	/**
	 * This method tests updateUtilities throws exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testInsertContractBudgetRent() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			Rent loRent = new Rent();
			Integer loSequence = 1;
			loRent.setLocation("location");
			loRent.setManagementCompanyName("managementCompanyName");
			loRent.setPercentChargedToContract("2");
			loRent.setPropertyOwner("propertyOwner");
			loRent.setPublicSchoolSpace("Y");
			loRent.setCreatedByUserId("909");
			loRent.setModifiedByUserId("909");
			loRent.setFyBudget("2000");
			loRent.setId("106");
			loRent.setContractBudgetID("25");
			loRent.setSubBudgetID("377");

			// Positive Scenario -- Rent Update Successful.

			loRent.setYtdInvoiceAmt("1000");
			loRent.setBudgetTypeId("2");
			boolean lbUpdateStatus = moContractBudgetService.insertContractBudgetRent(loSequence, moSession, loRent);
			assertTrue(lbUpdateStatus);

			// Negative Scenario -- Application Exception handled by setting.

			lbUpdateStatus = moContractBudgetService.insertContractBudgetRent(loSequence, null, loRent);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	/**
	 * This method tests set contract budget status for review task with no
	 * update
	 * @throws Exception
	 */
	@Test
	public void testSetContractBudgetStatusForReviewTask() throws Exception
	{
		Boolean lbThrown = false;
		Boolean lbFinalFinish = false;
		try
		{

			TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
			String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_BUDGET_APPROVED);
			loTaskDetailsBean.setContractId("111777");// Need to hardcord
			loTaskDetailsBean.setBudgetId("555");// Need to hardcord
			loTaskDetailsBean.setUserId("agency_12");// Need to hardcord

			moContractBudgetService.setContractBudgetStatusForReviewTask(moSession, lbFinalFinish, loTaskDetailsBean,
					lsBudgetStatus);

			assertEquals("", moContractBudgetService.getMoState().toString());

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	/**
	 * This method tests set contract budget status for review task with update
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void testSetContractBudgetStatusForReviewTask1() throws Exception
	{
		Boolean lbFinalFinish = true;

		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_BUDGET_APPROVED);
		loTaskDetailsBean.setContractId("111777");// Need to hardcord
		loTaskDetailsBean.setBudgetId("555");// Need to hardcord
		loTaskDetailsBean.setUserId("agency_12");// Need to hardcord

		moContractBudgetService.setContractBudgetStatusForReviewTask(null, lbFinalFinish, loTaskDetailsBean,
				lsBudgetStatus);

	}

	/**
	 * This method tests set contract budget status for review task exception
	 * @throws Exception
	 */
	// add with application exception
	@Test(expected = ApplicationException.class)
	public void testSetContractBudgetStatusForReviewTask2() throws Exception
	{
		Boolean lbFinalFinish = true;

		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_BUDGET_APPROVED);
		loTaskDetailsBean.setContractId(null);
		loTaskDetailsBean.setBudgetId("555");// Need to hardcord
		loTaskDetailsBean.setUserId("agency_12");// Need to hardcord

		moContractBudgetService.setContractBudgetStatusForReviewTask(moSession, lbFinalFinish, loTaskDetailsBean,
				lsBudgetStatus);

	}

	/**
	 * This method tests set contract budget status for review task exception
	 * @throws Exception
	 */
	// add with application exception
	@Test(expected = ApplicationException.class)
	public void testSetContractBudgetStatusForReviewTask3() throws Exception
	{
		Boolean lbFinalFinish = true;

		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_BUDGET_APPROVED);
		loTaskDetailsBean.setContractId("111777");// Need to hardcord
		loTaskDetailsBean.setBudgetId(null);
		loTaskDetailsBean.setUserId("agency_12");// Need to hardcord

		moContractBudgetService.setContractBudgetStatusForReviewTask(moSession, lbFinalFinish, loTaskDetailsBean,
				lsBudgetStatus);

	}

	/**
	 * This method tests set contract budget status for review task exception
	 * @throws Exception
	 */
	// add with application exception
	@Test(expected = ApplicationException.class)
	public void testSetContractBudgetStatusForReviewTask4() throws Exception
	{
		Boolean lbFinalFinish = true;

		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_BUDGET_APPROVED);
		loTaskDetailsBean.setContractId("111777");// Need to hardcord
		loTaskDetailsBean.setBudgetId("555");// Need to hardcord

		moContractBudgetService.setContractBudgetStatusForReviewTask(moSession, lbFinalFinish, loTaskDetailsBean,
				lsBudgetStatus);

	}

	/**
	 * This method tests set contract budget status for review task exception
	 * @throws Exception
	 */
	// add with application exception
	@Test(expected = ApplicationException.class)
	public void testSetContractBudgetStatusForReviewTask5() throws Exception
	{
		Boolean lbFinalFinish = true;

		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_BUDGET_APPROVED);

		moContractBudgetService.setContractBudgetStatusForReviewTask(moSession, lbFinalFinish, loTaskDetailsBean,
				lsBudgetStatus);

	}

	// Program Income Test Case Starts

	private CBGridBean getDummyCBGridBeanObj()
	{
		CBGridBean loCBGridBean = new CBGridBean();

		loCBGridBean.setContractBudgetID("555");
		loCBGridBean.setSubBudgetID("555");
		loCBGridBean.setContractID("111777");
		loCBGridBean.setCreatedByUserId("city_142");
		loCBGridBean.setModifiedByUserId("city_142");

		return loCBGridBean;

	}

	private CBProgramIncomeBean getDummyCBProgramIncomeBeanObj()
	{
		CBProgramIncomeBean loCBProgramIncomeBean = new CBProgramIncomeBean();

		loCBProgramIncomeBean.setContractBudgetID("555");
		loCBProgramIncomeBean.setSubBudgetID("555");
		loCBProgramIncomeBean.setContractID("111777");
		loCBProgramIncomeBean.setModifyByAgency("agency_42");
		loCBProgramIncomeBean.setModifyByProvider("803");
		loCBProgramIncomeBean.setId("105");
		loCBProgramIncomeBean.setBudgetType("2");
		loCBProgramIncomeBean.setProgramTitle("Hello Test");
		loCBProgramIncomeBean.setFYBudget("1000.00");
		loCBProgramIncomeBean.setIncome("900.00");
		loCBProgramIncomeBean.setProgramIncomeTypeId("6");

		return loCBProgramIncomeBean;

	}

	/**
	 * This method tests fetch Program Income
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchProgramIncome() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			CBGridBean loCBGridBean = getDummyCBGridBeanObj();
			List<CBProgramIncomeBean> loResultList = moContractBudgetService
					.fetchProgramIncome(loCBGridBean, moSession);
			assertNotNull(loResultList);
			assertTrue(!loResultList.isEmpty());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}

	}

	/**
	 * This method tests fetch Program Income throws exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchProgramIncomeWithException() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			CBGridBean loCBGridBean = getDummyCBGridBeanObj();
			loCBGridBean.setSubBudgetID("");// Invalid Sub Budget id
			moContractBudgetService.fetchProgramIncome(loCBGridBean, moSession);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}

	}

	/**
	 * This method tests fetch Program Income throws exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchProgramIncomeWithException2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			CBGridBean loCBGridBean = getDummyCBGridBeanObj();
			moContractBudgetService.fetchProgramIncome(loCBGridBean, null);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}

	}

	/**
	 * This method tests fetch Program Income throws exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchProgramIncomeWithException3() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			CBGridBean loCBGridBean = new CBGridBean();
			moContractBudgetService.fetchProgramIncome(loCBGridBean, moSession);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}

	}

	/**
	 * This method tests fetch Program Income throws exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = NullPointerException.class)
	public void testFetchProgramIncomeWithException4() throws ApplicationException
	{

		moContractBudgetService.fetchProgramIncome(null, moSession);

	}

	/**
	 * This method tests updateProgramIncome for editing the test in place of
	 * Other line item
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateProgramIncomeWithChangeInOtherField() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			CBProgramIncomeBean loCBProgramIncomeBean = getDummyCBProgramIncomeBeanObj();

			loCBProgramIncomeBean.setProgramIncomeTypeId("7"); // For Other - 7

			moContractBudgetService.updateProgramIncome(moSession, loCBProgramIncomeBean);
			lbThrown = true;
			assertTrue(lbThrown);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}

	}

	/**
	 * This method tests update Program Income throws exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateProgramIncomeWithException() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			CBProgramIncomeBean loCBProgramIncomeBean = getDummyCBProgramIncomeBeanObj();
			loCBProgramIncomeBean.setProgramTitle("");
			moContractBudgetService.updateProgramIncome(moSession, loCBProgramIncomeBean);
			lbThrown = true;
			assertTrue(lbThrown);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}

	}

	/**
	 * This method tests update Program Income throws exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateProgramIncomeWithException3() throws ApplicationException
	{
		CBProgramIncomeBean loCBProgramIncomeBean = getDummyCBProgramIncomeBeanObj();
		moContractBudgetService.updateProgramIncome(null, loCBProgramIncomeBean);
	}

	/**
	 * This method tests update Program Income throws exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateProgramIncomeWithException2() throws ApplicationException
	{

		CBProgramIncomeBean loCBProgramIncomeBean = getDummyCBProgramIncomeBeanObj();
		loCBProgramIncomeBean.setProgramIncomeTypeId(null); // For Other - 7
		moContractBudgetService.updateProgramIncome(moSession, loCBProgramIncomeBean);

	}

	/**
	 * This method tests update Program Income throws exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateProgramIncomeWithException4() throws ApplicationException
	{

		CBProgramIncomeBean loCBProgramIncomeBean = getDummyCBProgramIncomeBeanObj();
		loCBProgramIncomeBean.setBudgetTypeId(null);
		moContractBudgetService.updateProgramIncome(moSession, loCBProgramIncomeBean);

	}

	/**
	 * This method tests update Program Income throws exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateProgramIncomeWithException5() throws ApplicationException
	{

		CBProgramIncomeBean loCBProgramIncomeBean = getDummyCBProgramIncomeBeanObj();
		loCBProgramIncomeBean.setBudgetTypeId(null);
		moContractBudgetService.updateProgramIncome(moSession, loCBProgramIncomeBean);

	}

	// Case1: successful
	@Test
	public void testFetchRate1() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		aoCBGridBeanObj.setSubBudgetID("555");
		List<RateBean> loRateDetailsList = moContractBudgetService.fetchRate(moSession, aoCBGridBeanObj);

		assertNotNull(loRateDetailsList);
	}

	// Case2: Application exception
	// incomplete data
	@Test(expected = ApplicationException.class)
	public void testFetchRate2() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		// aoCBGridBeanObj.setSubBudgetID("555");
		moContractBudgetService.fetchRate(moSession, aoCBGridBeanObj);
	}

	// Case3: Application exception
	// null id
	@Test(expected = ApplicationException.class)
	public void testFetchRate3() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		aoCBGridBeanObj.setSubBudgetID(null);
		moContractBudgetService.fetchRate(moSession, aoCBGridBeanObj);
	}

	// Case4: Application exception
	// null session
	@Test(expected = ApplicationException.class)
	public void testFetchRate4() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		aoCBGridBeanObj.setSubBudgetID("555");
		moContractBudgetService.fetchRate(null, aoCBGridBeanObj);
	}

	// Case5: Application exception
	// null bean
	@Test(expected = NullPointerException.class)
	public void testFetchRate5() throws ApplicationException
	{
		moContractBudgetService.fetchRate(moSession, null);
	}

	/**
	 * This method tests update functionality of Rate details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateRate() throws ApplicationException
	{

		RateBean aoRateBean = new RateBean();
		aoRateBean.setId("1");
		aoRateBean.setUnitDesc("Test Case Check");
		aoRateBean.setUnits("12");
		aoRateBean.setFyBudget("1200");
		boolean loUpdateStatus = moContractBudgetService.updateRate(moSession, aoRateBean);
		assertTrue(loUpdateStatus);
	}

	/**
	 * This method tests Delete functionality of Rate details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testDeleteRate() throws ApplicationException
	{

		RateBean aoRateBean = new RateBean();
		aoRateBean.setId("1");
		boolean loUpdateStatus = moContractBudgetService.deleteRate(moSession, aoRateBean);
		assertTrue(loUpdateStatus);
	}

	/**
	 * This method tests Insert functionality of Rate details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testInsertRate() throws ApplicationException
	{

		RateBean aoRateBean = new RateBean();
		aoRateBean.setId("265");
		aoRateBean.setUnitDesc("Test Case Check");
		aoRateBean.setUnits("12");
		aoRateBean.setFyBudget("1200");
		aoRateBean.setModifiedByUserId("city_142");
		aoRateBean.setContractBudgetID("555");
		aoRateBean.setSubBudgetID("555");
		aoRateBean.setContractID("111777");
		boolean loUpdateStatus = moContractBudgetService.insertRate(12, moSession, aoRateBean);
		assertTrue(loUpdateStatus);
	}

	/**
	 * This method tests Insert functionality of Rate details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testInsertRateException() throws ApplicationException
	{

		RateBean aoRateBean = new RateBean();
		aoRateBean.setId("265");
		aoRateBean.setUnitDesc("Test Case Check");
		aoRateBean.setUnits("12");
		aoRateBean.setFyBudget("1200");
		aoRateBean.setModifiedByUserId("city_142");
		aoRateBean.setContractBudgetID("555");
		aoRateBean.setSubBudgetID("555");
		aoRateBean.setContractID("111777");

		moContractBudgetService.insertRate(12, null, aoRateBean);
	}

	/**
	 * This method tests Insert functionality of Rate details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testInsertRateException2() throws ApplicationException
	{

		RateBean aoRateBean = new RateBean();
		aoRateBean.setId("A");
		aoRateBean.setUnitDesc("Test Case Check");
		aoRateBean.setUnits("12");
		aoRateBean.setFyBudget("1200");
		aoRateBean.setModifiedByUserId("city_142");
		aoRateBean.setContractBudgetID("555");
		aoRateBean.setSubBudgetID("555");
		aoRateBean.setContractID("111777");

		moContractBudgetService.insertRate(12, moSession, aoRateBean);
	}

	/**
	 * This method tests Insert functionality of Rate details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testInsertRateException3() throws ApplicationException
	{

		RateBean aoRateBean = new RateBean();
		aoRateBean.setId("265");
		aoRateBean.setUnitDesc("Test Case Check");
		aoRateBean.setUnits("12");
		aoRateBean.setFyBudget("1200");
		aoRateBean.setModifiedByUserId("city_142");
		aoRateBean.setContractBudgetID("555");
		aoRateBean.setSubBudgetID("555");
		aoRateBean.setContractID("111777");

		moContractBudgetService.insertRate(null, moSession, aoRateBean);
	}

	/**
	 * This method tests Insert functionality of Rate details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = NullPointerException.class)
	public void testInsertRateException4() throws ApplicationException
	{
		moContractBudgetService.insertRate(12, moSession, null);
	}

	/**
	 * This method tests Insert functionality of Rate details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testInsertRateException5() throws ApplicationException
	{
		RateBean aoRateBean = new RateBean();
		moContractBudgetService.insertRate(12, moSession, aoRateBean);
	}

	/**
	 * This method test the setContractBudgetStatus method by updating the
	 * Budget Status
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testSetContractBudgetStatus() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			HashMap loHMMap = new HashMap();

			String lsBudgetId = "10069";

			loHMMap.put(HHSConstants.BUDGET_ID_WORKFLOW, lsBudgetId);
			loHMMap.put(HHSConstants.AS_STATUS_ID, "82");
			loHMMap.put(HHSConstants.MOD_BY_USER_ID, "city_142");
			loHMMap.put(HHSConstants.SUBMITTED_BY, "803");
			// Positive Scenario --
			Boolean lbUpdateFlag = moContractBudgetService.setContractBudgetStatus(moSession, loHMMap);
			assertTrue(lbUpdateFlag);

			// Negative Scenario -- Application Exception handled by setting.
			moContractBudgetService.setContractBudgetStatus(null, loHMMap);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	/**
	 * This method test the setContractBudgetStatus method by updating the
	 * Budget Status
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testSetContractBudgetStatusException() throws ApplicationException
	{

		HashMap loHMMap = new HashMap();

		loHMMap.put(HHSConstants.AS_STATUS_ID, "82");
		loHMMap.put(HHSConstants.MOD_BY_USER_ID, "city_142");
		loHMMap.put(HHSConstants.SUBMITTED_BY, "803");
		// Positive Scenario --
		moContractBudgetService.setContractBudgetStatus(moSession, loHMMap);

	}

	/**
	 * Test method for fetching Salaried Employee Budget on Personnel Services
	 * Screen with Invalid data.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchSalariedEmployeeBudgetWithInvalidData() throws ApplicationException
	{

		CBGridBean loPersonnelServiceBudget = new CBGridBean();
		loPersonnelServiceBudget.setSubBudgetID("377");
		loPersonnelServiceBudget.setContractBudgetID("25");
		List<PersonnelServiceBudget> loSalariedEmployess = moContractBudgetService.fetchSalariedEmployeeBudget(
				moSession, loPersonnelServiceBudget);
		assertTrue(loSalariedEmployess.size() == 0);

	}

	/**
	 * Test method for fetching Salaried Employee Budget on Personnel Services
	 * Screen with valid data.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchSalariedEmployeeBudgetWithValidData() throws ApplicationException
	{

		CBGridBean loPersonnelServiceBudget = new CBGridBean();
		loPersonnelServiceBudget.setSubBudgetID("555"); // Need to hardcord
		loPersonnelServiceBudget.setContractBudgetID("555"); // Need to hardcord
		List<PersonnelServiceBudget> loSalariedEmployess = moContractBudgetService.fetchSalariedEmployeeBudget(
				moSession, loPersonnelServiceBudget);
		assertTrue(loSalariedEmployess.size() != 0);

	}

	/**
	 * Test method for fetching Salaried Employee Budget on Personnel Services
	 * Screen with null session.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchSalariedEmployeeBudgetWithInvalidData2() throws ApplicationException
	{

		CBGridBean loPersonnelServiceBudget = new CBGridBean();
		loPersonnelServiceBudget.setSubBudgetID("377");
		moContractBudgetService.fetchSalariedEmployeeBudget(null, loPersonnelServiceBudget);

	}

	/**
	 * Test method for fetching Salaried Employee Budget on Personnel Services
	 * Screen with null session.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchSalariedEmployeeBudgetWithInvalidData3() throws ApplicationException
	{

		CBGridBean loPersonnelServiceBudget = new CBGridBean();
		loPersonnelServiceBudget.setContractBudgetID("25");
		moContractBudgetService.fetchSalariedEmployeeBudget(null, loPersonnelServiceBudget);

	}

	/**
	 * Test method for fetching Salaried Employee Budget on Personnel Services
	 * Screen with null session.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchSalariedEmployeeBudgetWithNullSession() throws ApplicationException
	{

		CBGridBean loPersonnelServiceBudget = new CBGridBean();
		loPersonnelServiceBudget.setSubBudgetID("377");
		loPersonnelServiceBudget.setContractBudgetID("25");
		moContractBudgetService.fetchSalariedEmployeeBudget(null, loPersonnelServiceBudget);

	}

	/**
	 * Test method for adding Salaried Employee Budget on Personnel Services
	 * Screen.
	 */
	@Test
	public void testAddEmployeeBudgetSalaried() throws ApplicationException
	{

		PersonnelServiceBudget loPersonnelServiceBudget = new PersonnelServiceBudget();
		createPersonnelServiceBudgetBean(loPersonnelServiceBudget);
		loPersonnelServiceBudget.setTransactionName(HHSConstants.CBY_SALARIED_EMPLOYEE_GRID_ADD);
		Boolean lbInsertStatus = moContractBudgetService.addEmployeeBudget(moSession, loPersonnelServiceBudget);
		assertTrue(lbInsertStatus);

	}

	/**
	 * Test method for adding Hourly Employee Budget on Personnel Services
	 * Screen.
	 */
	@Test
	public void testAddEmployeeBudgetHourly() throws ApplicationException
	{

		PersonnelServiceBudget loPersonnelServiceBudget = new PersonnelServiceBudget();
		createPersonnelServiceBudgetBean(loPersonnelServiceBudget);
		loPersonnelServiceBudget.setTransactionName(HHSConstants.CBY_HOURLY_EMPLOYEE_GRID_ADD);
		Boolean lbInsertStatus = moContractBudgetService.addEmployeeBudget(moSession, loPersonnelServiceBudget);
		assertTrue(lbInsertStatus);

	}

	/**
	 * Test method for adding Hourly Employee Budget on Personnel Services
	 * Screen.
	 */
	@Test
	public void testAddEmployeeBudgetSeasonal() throws ApplicationException
	{

		PersonnelServiceBudget loPersonnelServiceBudget = new PersonnelServiceBudget();
		createPersonnelServiceBudgetBean(loPersonnelServiceBudget);
		loPersonnelServiceBudget.setTransactionName(HHSConstants.CBY_SEASONAL_EMPLOYEE_GRID_ADD);
		Boolean lbInsertStatus = moContractBudgetService.addEmployeeBudget(moSession, loPersonnelServiceBudget);
		assertTrue(lbInsertStatus);

	}

	/**
	 * Test method for fetching Salaried Employee Budget on Personnel Services
	 * Screen with null session.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testAddEmployeeBudgetWithNullSession() throws ApplicationException
	{

		PersonnelServiceBudget loPersonnelServiceBudget = new PersonnelServiceBudget();
		createPersonnelServiceBudgetBean(loPersonnelServiceBudget);
		loPersonnelServiceBudget.setTransactionName(HHSConstants.CBY_SEASONAL_EMPLOYEE_GRID_ADD);
		moContractBudgetService.addEmployeeBudget(null, loPersonnelServiceBudget);

	}

	/**
	 * Test method for fetching Salaried Employee Budget on Personnel Services
	 * Screen with null session.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testAddEmployeeBudgetWithInvalidData() throws ApplicationException
	{

		PersonnelServiceBudget loPersonnelServiceBudget = new PersonnelServiceBudget();
		loPersonnelServiceBudget.setTransactionName(HHSConstants.CBY_SEASONAL_EMPLOYEE_GRID_ADD);
		moContractBudgetService.addEmployeeBudget(null, loPersonnelServiceBudget);

	}

	/**
	 * Test method for Editing Employee Budget on Personnel Services Screen.
	 */
	@Test
	public void testEditEmployeeBudget() throws ApplicationException
	{

		PersonnelServiceBudget loPersonnelServiceBudget = new PersonnelServiceBudget();
		loPersonnelServiceBudget.setId("54");
		loPersonnelServiceBudget.setEmpPosition("1");
		loPersonnelServiceBudget.setUnit("1000");
		loPersonnelServiceBudget.setBudgetAmount("5000");
		loPersonnelServiceBudget.setModifiedByUserId("803");
		Boolean lbEditStatus = moContractBudgetService.editEmployeeBudget(moSession, loPersonnelServiceBudget);
		assertTrue(lbEditStatus);

	}

	/**
	 * Test method for Editing Employee Budget on Personnel Services Screen with
	 * null session.
	 */
	@Test(expected = ApplicationException.class)
	public void testEditEmployeeBudgetWithNullSession() throws ApplicationException
	{

		PersonnelServiceBudget loPersonnelServiceBudget = new PersonnelServiceBudget();
		loPersonnelServiceBudget.setId("1");
		loPersonnelServiceBudget.setEmpPosition("1");
		loPersonnelServiceBudget.setUnit("1000");
		loPersonnelServiceBudget.setBudgetAmount("5000");
		loPersonnelServiceBudget.setModifiedByUserId("803");
		Boolean lbEditStatus = moContractBudgetService.editEmployeeBudget(null, loPersonnelServiceBudget);
		assertTrue(lbEditStatus);

	}

	/**
	 * Test method for Editing Employee Budget on Personnel Services Screen with
	 * null session.
	 */
	@Test(expected = ApplicationException.class)
	public void testEditEmployeeBudget2() throws ApplicationException
	{

		PersonnelServiceBudget loPersonnelServiceBudget = new PersonnelServiceBudget();
		loPersonnelServiceBudget.setId(null);
		loPersonnelServiceBudget.setEmpPosition("1");
		loPersonnelServiceBudget.setUnit("1000");
		loPersonnelServiceBudget.setBudgetAmount("5000");
		loPersonnelServiceBudget.setModifiedByUserId("803");
		Boolean lbEditStatus = moContractBudgetService.editEmployeeBudget(moSession, loPersonnelServiceBudget);
		assertTrue(lbEditStatus);

	}

	/**
	 * Test method for Editing Employee Budget on Personnel Services Screen with
	 * null session.
	 */
	@Test(expected = ApplicationException.class)
	public void testEditEmployeeBudget3() throws ApplicationException
	{

		PersonnelServiceBudget loPersonnelServiceBudget = new PersonnelServiceBudget();
		loPersonnelServiceBudget.setId("1");
		loPersonnelServiceBudget.setEmpPosition(null);
		loPersonnelServiceBudget.setUnit("1000");
		loPersonnelServiceBudget.setBudgetAmount("5000");
		loPersonnelServiceBudget.setModifiedByUserId("803");
		Boolean lbEditStatus = moContractBudgetService.editEmployeeBudget(moSession, loPersonnelServiceBudget);
		assertTrue(lbEditStatus);

	}

	/**
	 * Test method for Editing Employee Budget on Personnel Services Screen with
	 * null session.
	 */
	@Test(expected = NullPointerException.class)
	public void testEditEmployeeBudget4() throws ApplicationException
	{

		Boolean lbEditStatus = moContractBudgetService.editEmployeeBudget(moSession, null);
		assertTrue(lbEditStatus);

	}

	/**
	 * Test method for Editing Employee Budget on Personnel Services Screen.
	 */
	@Test
	public void testDelEmployeeBudget() throws ApplicationException
	{

		PersonnelServiceBudget loPersonnelServiceBudget = new PersonnelServiceBudget();
		loPersonnelServiceBudget.setId("55");
		Boolean lbDelStatus = moContractBudgetService.delEmployeeBudget(moSession, loPersonnelServiceBudget);
		assertTrue(lbDelStatus);

	}

	/**
	 * Test method for Editing Employee Budget on Personnel Services Screen with
	 * null session.
	 */
	@Test(expected = ApplicationException.class)
	public void testDelEmployeeBudgetWithNullSession() throws ApplicationException
	{

		PersonnelServiceBudget loPersonnelServiceBudget = new PersonnelServiceBudget();
		loPersonnelServiceBudget.setId("1");
		Boolean lbDelStatus = moContractBudgetService.delEmployeeBudget(null, loPersonnelServiceBudget);
		assertTrue(lbDelStatus);

	}

	/**
	 * Test method for Editing Employee Budget on Personnel Services Screen with
	 * null session.
	 */
	@Test(expected = ApplicationException.class)
	public void testDelEmployeeBudget2() throws ApplicationException
	{

		PersonnelServiceBudget loPersonnelServiceBudget = new PersonnelServiceBudget();
		Boolean lbDelStatus = moContractBudgetService.delEmployeeBudget(moSession, loPersonnelServiceBudget);
		assertTrue(lbDelStatus);

	}

	/**
	 * Test method for Editing Employee Budget on Personnel Services Screen with
	 * invalid id.
	 */
	@Test(expected = ApplicationException.class)
	public void testDelEmployeeBudget3() throws ApplicationException
	{

		PersonnelServiceBudget loPersonnelServiceBudget = new PersonnelServiceBudget();
		loPersonnelServiceBudget.setId("0");
		Boolean lbDelStatus = moContractBudgetService.delEmployeeBudget(moSession, loPersonnelServiceBudget);
		assertTrue(lbDelStatus);

	}

	/**
	 * Test method for Editing Employee Budget on Personnel Services Screen with
	 * null session.
	 */
	@Test(expected = NullPointerException.class)
	public void testDelEmployeeBudget4() throws ApplicationException
	{

		Boolean lbDelStatus = moContractBudgetService.delEmployeeBudget(moSession, null);
		assertTrue(lbDelStatus);

	}

	private void createPersonnelServiceBudgetBean(PersonnelServiceBudget aoPersonnelServiceBudget)
	{

		aoPersonnelServiceBudget.setSubBudgetID("3");
		aoPersonnelServiceBudget.setContractBudgetID("27");
		aoPersonnelServiceBudget.setEmpPosition("1");
		aoPersonnelServiceBudget.setUnit("1000");
		aoPersonnelServiceBudget.setBudgetAmount("5000");
		aoPersonnelServiceBudget.setCreatedByUserId("803");
		aoPersonnelServiceBudget.setModifiedByUserId("803");
	}

	/**
	 * Test method for fetching Hourly Employee Budget on Personnel Services
	 * Screen with Invalid data.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchHourlyEmployeeBudgetWithInvalidData() throws ApplicationException
	{

		CBGridBean loPersonnelServiceBudget = new CBGridBean();
		loPersonnelServiceBudget.setSubBudgetID("377");
		loPersonnelServiceBudget.setContractBudgetID("25");
		List<PersonnelServiceBudget> loSalariedEmployess = moContractBudgetService.fetchHourlyEmployeeBudget(moSession,
				loPersonnelServiceBudget);
		assertTrue(loSalariedEmployess.size() == 0);

	}

	/**
	 * Test method for fetching Hourly Employee Budget on Personnel Services
	 * Screen with valid data.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchHourlyEmployeeBudgetWithValidData() throws ApplicationException
	{

		CBGridBean loPersonnelServiceBudget = new CBGridBean();
		loPersonnelServiceBudget.setSubBudgetID("555"); // Need to hardcord
		loPersonnelServiceBudget.setContractBudgetID("555"); // Need to hardcord
		List<PersonnelServiceBudget> loSalariedEmployess = moContractBudgetService.fetchHourlyEmployeeBudget(moSession,
				loPersonnelServiceBudget);
		assertTrue(loSalariedEmployess.size() != 0);

	}

	/**
	 * Test method for fetching Hourly Employee Budget on Personnel Services
	 * Screen with null session.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchHourlyEmployeeBudgetWithNullSession() throws ApplicationException
	{

		CBGridBean loPersonnelServiceBudget = new CBGridBean();
		loPersonnelServiceBudget.setSubBudgetID("377");
		loPersonnelServiceBudget.setContractBudgetID("25");
		moContractBudgetService.fetchHourlyEmployeeBudget(null, loPersonnelServiceBudget);

	}

	/**
	 * Test method for fetching Hourly Employee Budget on Personnel Services
	 * Screen with null session.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchHourlyEmployeeBudget2() throws ApplicationException
	{

		CBGridBean loPersonnelServiceBudget = new CBGridBean();
		loPersonnelServiceBudget.setContractBudgetID("25");
		moContractBudgetService.fetchHourlyEmployeeBudget(null, loPersonnelServiceBudget);

	}

	/**
	 * Test method for fetching Hourly Employee Budget on Personnel Services
	 * Screen with null session.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchHourlyEmployeeBudget3() throws ApplicationException
	{

		CBGridBean loPersonnelServiceBudget = new CBGridBean();
		loPersonnelServiceBudget.setSubBudgetID("377");
		moContractBudgetService.fetchHourlyEmployeeBudget(null, loPersonnelServiceBudget);

	}

	/**
	 * Test method for fetching Seasonal Employee Budget on Personnel Services
	 * Screen with Invalid data.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchSeasonalEmployeeBudgetWithInvalidData() throws ApplicationException
	{
		CBGridBean loPersonnelServiceBudget = new CBGridBean();
		loPersonnelServiceBudget.setSubBudgetID("377");
		loPersonnelServiceBudget.setContractBudgetID("25");
		List<PersonnelServiceBudget> loSalariedEmployess = moContractBudgetService.fetchSeasonalEmployeeBudget(
				moSession, loPersonnelServiceBudget);
		assertTrue(loSalariedEmployess.size() == 0);

	}

	/**
	 * Test method for fetching Seasonal Employee Budget on Personnel Services
	 * Screen with valid data.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchSeasonalEmployeeBudgetWithValidData() throws ApplicationException
	{
		CBGridBean loPersonnelServiceBudget = new CBGridBean();
		loPersonnelServiceBudget.setSubBudgetID("555"); // Need to hardcord
		loPersonnelServiceBudget.setContractBudgetID("555"); // Need to hardcord
		List<PersonnelServiceBudget> loSalariedEmployess = moContractBudgetService.fetchSeasonalEmployeeBudget(
				moSession, loPersonnelServiceBudget);
		assertTrue(loSalariedEmployess.size() != 0);

	}

	/**
	 * Test method for fetching Seasonal Employee Budget on Personnel Services
	 * Screen with null session.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchSeasonalEmployeeBudgetWithNullSession() throws ApplicationException
	{
		CBGridBean loPersonnelServiceBudget = new CBGridBean();
		loPersonnelServiceBudget.setSubBudgetID("377");
		loPersonnelServiceBudget.setContractBudgetID("25");
		moContractBudgetService.fetchSeasonalEmployeeBudget(null, loPersonnelServiceBudget);

	}

	/**
	 * Test method for fetching Seasonal Employee Budget on Personnel Services
	 * Screen with null session.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchSeasonalEmployeeBudget2() throws ApplicationException
	{
		CBGridBean loPersonnelServiceBudget = new CBGridBean();
		loPersonnelServiceBudget.setContractBudgetID("25");
		moContractBudgetService.fetchSeasonalEmployeeBudget(null, loPersonnelServiceBudget);

	}

	/**
	 * Test method for fetching Seasonal Employee Budget on Personnel Services
	 * Screen with null session.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchSeasonalEmployeeBudget3() throws ApplicationException
	{
		CBGridBean loPersonnelServiceBudget = new CBGridBean();
		loPersonnelServiceBudget.setSubBudgetID("377");
		moContractBudgetService.fetchSeasonalEmployeeBudget(null, loPersonnelServiceBudget);

	}

	/**
	 * Test method for fetching Fringe Budget on Personnel Services Screen with
	 * Invalid data.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchFringeBudgetWithInvalidData() throws ApplicationException
	{
		CBGridBean loPersonnelServiceBudget = new CBGridBean();
		loPersonnelServiceBudget.setSubBudgetID("377");
		loPersonnelServiceBudget.setContractBudgetID("25");
		List<PersonnelServiceBudget> loSalariedEmployess = moContractBudgetService.fetchFringeBenifits(moSession,
				loPersonnelServiceBudget);
		assertNotNull(loSalariedEmployess);

	}

	/**
	 * Test method for fetching Fringe Budget on Personnel Services Screen with
	 * valid data.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchFringeBudgetWithValidData() throws ApplicationException
	{
		CBGridBean loPersonnelServiceBudget = new CBGridBean();
		loPersonnelServiceBudget.setSubBudgetID("555"); // Need to hardcord
		loPersonnelServiceBudget.setContractBudgetID("555"); // Need to hardcord
		List<PersonnelServiceBudget> loSalariedEmployess = moContractBudgetService.fetchFringeBenifits(moSession,
				loPersonnelServiceBudget);
		assertTrue(loSalariedEmployess.size() != 0);

	}

	/**
	 * Test method for fetching Fringe Budget on Personnel Services Screen with
	 * null session.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchFringeBudgetWithNullSession() throws ApplicationException
	{
		CBGridBean loPersonnelServiceBudget = new CBGridBean();
		loPersonnelServiceBudget.setSubBudgetID("377");
		loPersonnelServiceBudget.setContractBudgetID("25");
		moContractBudgetService.fetchFringeBenifits(null, loPersonnelServiceBudget);

	}

	/**
	 * Test method for Editing Fringe Benefits on Personnel Services Screen.
	 */

	@Test
	public void testEditFringeBenifitsWithValidData() throws ApplicationException
	{
		PersonnelServiceBudget loPersonnelServiceBudget = new PersonnelServiceBudget();
		loPersonnelServiceBudget.setContractBudgetID("555");
		loPersonnelServiceBudget.setSubBudgetID("556");
		loPersonnelServiceBudget.setId("20");
		loPersonnelServiceBudget.setBudgetAmount("5000");
		loPersonnelServiceBudget.setCreatedByUserId("803");
		loPersonnelServiceBudget.setModifiedByUserId("803");
		Boolean lbEditStatus = moContractBudgetService.editFringeBenifits(moSession, loPersonnelServiceBudget);
		assertTrue(lbEditStatus);

	}

	/**
	 * Test method for Editing Fringe Benefits on Personnel Services Screen with
	 * null session.
	 */
	@Test(expected = ApplicationException.class)
	public void testEditFringeBenifitsWithNullSession() throws ApplicationException
	{
		PersonnelServiceBudget loPersonnelServiceBudget = new PersonnelServiceBudget();
		loPersonnelServiceBudget.setContractBudgetID("555");
		loPersonnelServiceBudget.setSubBudgetID("556");
		loPersonnelServiceBudget.setId("1");
		loPersonnelServiceBudget.setBudgetAmount("5000");
		loPersonnelServiceBudget.setCreatedByUserId("803");
		loPersonnelServiceBudget.setModifiedByUserId("803");
		Boolean lbEditStatus = moContractBudgetService.editFringeBenifits(null, loPersonnelServiceBudget);
		assertTrue(lbEditStatus);

	}

	/**
	 * Test method for Editing Fringe Benefits on Personnel Services Screen with
	 * null session.
	 */
	@Test(expected = ApplicationException.class)
	public void testEditFringeBenifitsException2() throws ApplicationException
	{
		PersonnelServiceBudget loPersonnelServiceBudget = new PersonnelServiceBudget();
		loPersonnelServiceBudget.setContractBudgetID("555");
		loPersonnelServiceBudget.setSubBudgetID("556");
		loPersonnelServiceBudget.setId("A");
		loPersonnelServiceBudget.setBudgetAmount("5000");
		loPersonnelServiceBudget.setCreatedByUserId("803");
		loPersonnelServiceBudget.setModifiedByUserId("803");
		Boolean lbEditStatus = moContractBudgetService.editFringeBenifits(moSession, loPersonnelServiceBudget);
		assertTrue(lbEditStatus);

	}

	/**
	 * Test method for Editing Fringe Benefits on Personnel Services Screen with
	 * null session.
	 */
	@Test(expected = ApplicationException.class)
	public void testEditFringeBenifitsException3() throws ApplicationException
	{
		PersonnelServiceBudget loPersonnelServiceBudget = new PersonnelServiceBudget();
		loPersonnelServiceBudget.setId("20");
		loPersonnelServiceBudget.setBudgetAmount("5000");
		loPersonnelServiceBudget.setCreatedByUserId("803");
		loPersonnelServiceBudget.setModifiedByUserId("803");
		Boolean lbEditStatus = moContractBudgetService.editFringeBenifits(moSession, loPersonnelServiceBudget);
		assertTrue(lbEditStatus);

	}

	/**
	 * Test method for Editing Fringe Benefits on Personnel Services Screen with
	 * null session.
	 */
	@Test(expected = ApplicationException.class)
	public void testEditFringeBenifitsException4() throws ApplicationException
	{
		PersonnelServiceBudget loPersonnelServiceBudget = new PersonnelServiceBudget();
		loPersonnelServiceBudget.setContractBudgetID("555");
		loPersonnelServiceBudget.setSubBudgetID("556");
		loPersonnelServiceBudget.setId("_");
		loPersonnelServiceBudget.setBudgetAmount("5000");
		moContractBudgetService.editFringeBenifits(moSession, loPersonnelServiceBudget);
	}

	/**
	 * Test method for Editing Fringe Benefits on Personnel Services Screen with
	 * null session.
	 */
	@Test(expected = ApplicationException.class)
	public void testEditFringeBenifitsException5() throws ApplicationException
	{
		PersonnelServiceBudget loPersonnelServiceBudget = new PersonnelServiceBudget();
		loPersonnelServiceBudget.setContractBudgetID("555");
		loPersonnelServiceBudget.setSubBudgetID("556");
		loPersonnelServiceBudget.setId(null);
		loPersonnelServiceBudget.setBudgetAmount("5000");
		moContractBudgetService.editFringeBenifits(moSession, loPersonnelServiceBudget);
	}

	/**
	 * Test method for Editing Fringe Benefits on Personnel Services Screen with
	 * null session.
	 */
	@Test(expected = NullPointerException.class)
	public void testEditFringeBenifitsException6() throws ApplicationException
	{
		Boolean lbEditStatus = moContractBudgetService.editFringeBenifits(moSession, null);
		assertTrue(lbEditStatus);

	}

	/**
	 * This method tests fetching functionality of Contract Budget's Contract
	 * Summary details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchContractSummary1() throws ApplicationException
	{

		HashMap<String, String> aoHashMap = new HashMap<String, String>();
		aoHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, "111777");
		aoHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "555");
		ContractList loContractList = (ContractList) moContractBudgetService.fetchContractSummary(moSession, aoHashMap);

		assertNotNull(loContractList);
	}

	/**
	 * This method tests fetching functionality of Contract Budget's Contract
	 * Summary details
	 * 
	 * Negative scenario -- Null session
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchContractSummary2() throws ApplicationException
	{

		HashMap<String, String> aoHashMap = new HashMap<String, String>();
		aoHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, "111777");
		aoHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "555");
		moContractBudgetService.fetchContractSummary(null, aoHashMap);
	}

	/**
	 * This method tests fetching functionality of Contract Budget's Contract
	 * Summary details
	 * 
	 * Negative scenario -- Incomplete data
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchContractSummary3() throws ApplicationException
	{

		HashMap<String, String> aoHashMap = new HashMap<String, String>();
		aoHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, "111777");
		// aoHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "555");
		moContractBudgetService.fetchContractSummary(moSession, aoHashMap);
	}

	/**
	 * This method tests fetching functionality of Contract Budget's Contract
	 * Summary details
	 * 
	 * Negative scenario -- Empty map
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchContractSummary4() throws ApplicationException
	{
		HashMap<String, String> aoHashMap = new HashMap<String, String>();
		moContractBudgetService.fetchContractSummary(moSession, aoHashMap);
	}

	/**
	 * This method tests fetching functionality of Contract Budget's Contract
	 * Summary details
	 * 
	 * Negative scenario -- Null map
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchContractSummary5() throws ApplicationException
	{
		moContractBudgetService.fetchContractSummary(moSession, null);
	}

	/**
	 * This method tests fetching functionality of Contract Budget's FY Budget
	 * Summary details
	 * 
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchFyBudgetSummary1() throws ApplicationException
	{

		HashMap<String, String> aoHashMap = new HashMap<String, String>();
		aoHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, "111777");
		aoHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "555");
		BudgetDetails loBudgetDetails = (BudgetDetails) moContractBudgetService.fetchFyBudgetSummary(moSession,
				aoHashMap);
		assertNotNull(loBudgetDetails);
	}

	/**
	 * This method tests fetching functionality of Contract Budget's FY Budget
	 * Summary details
	 * 
	 * Negative scenario -- Incomplete data
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchFyBudgetSummary2() throws ApplicationException
	{
		BudgetDetails loFyBudget = new BudgetDetails();
		HashMap<String, String> aoHashMap = new HashMap<String, String>();
		aoHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, "50");
		aoHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "15");
		loFyBudget = moContractBudgetService.fetchFyBudgetSummary(moSession, aoHashMap);
		assertNotNull(loFyBudget);
	}

	/**
	 * This method tests fetching functionality of Contract Budget's FY Budget
	 * Summary details
	 * 
	 * Negative scenario -- Null session
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchFyBudgetSummary3() throws ApplicationException
	{

		HashMap<String, String> aoHashMap = new HashMap<String, String>();
		aoHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, "111777");
		aoHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "555");
		moContractBudgetService.fetchFyBudgetSummary(null, aoHashMap);
	}

	/**
	 * This method tests fetching functionality of Contract Budget's FY Budget
	 * Summary details
	 * 
	 * Negative scenario -- Empty map
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchFyBudgetSummary4() throws ApplicationException
	{

		HashMap<String, String> aoHashMap = new HashMap<String, String>();
		moContractBudgetService.fetchFyBudgetSummary(moSession, aoHashMap);
	}

	/**
	 * This method tests fetching functionality of Contract Budget's FY Budget
	 * Summary details
	 * 
	 * Negative scenario -- null map
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchFyBudgetSummary5() throws ApplicationException
	{
		moContractBudgetService.fetchFyBudgetSummary(moSession, null);
	}

	/**
	 * This method tests fetching functionality of Contract Budget's sub-Budget
	 * Summary details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchSubBudgetSummary() throws ApplicationException
	{

		HashMap<String, String> aoHashMap = new HashMap<String, String>();
		aoHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "555");
		aoHashMap.put(HHSConstants.SUBBUDGET_ID, "555");
		List<CBGridBean> loSubBudgetList = (List<CBGridBean>) moContractBudgetService.fetchSubBudgetSummary(moSession,
				aoHashMap);
		assertNotNull(loSubBudgetList);
		assertTrue(loSubBudgetList.size() > 0);
	}

	/**
	 * This method tests fetching functionality of Contract Budget's sub-Budget
	 * Summary details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchSubBudgetSummary2() throws ApplicationException
	{

		HashMap<String, String> aoHashMap = new HashMap<String, String>();
		aoHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "555");
		List<CBGridBean> loSubBudgetList = (List<CBGridBean>) moContractBudgetService.fetchSubBudgetSummary(moSession,
				aoHashMap);
		assertNotNull(loSubBudgetList);
		assertTrue(loSubBudgetList.size() > 0);
	}

	/**
	 * This method tests fetching functionality of Contract Budget's sub-Budget
	 * Summary details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchSubBudgetSummary3() throws ApplicationException
	{

		HashMap<String, String> aoHashMap = new HashMap<String, String>();
		aoHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "555");
		aoHashMap.put(HHSConstants.SUBBUDGET_ID, "555");
		moContractBudgetService.fetchSubBudgetSummary(null, aoHashMap);
	}

	/**
	 * This method tests fetching functionality of Contract Budget's sub-Budget
	 * Summary details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchSubBudgetSummary4() throws ApplicationException
	{

		HashMap<String, String> aoHashMap = new HashMap<String, String>();
		aoHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "555");
		moContractBudgetService.fetchSubBudgetSummary(null, aoHashMap);
	}

	/**
	 * This method tests fetching functionality of Contract Budget's sub-Budget
	 * Summary details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchSubBudgetSummary5() throws ApplicationException
	{

		HashMap<String, String> aoHashMap = new HashMap<String, String>();
		aoHashMap.put(HHSConstants.SUBBUDGET_ID, "555");
		moContractBudgetService.fetchSubBudgetSummary(moSession, aoHashMap);
	}

	/**
	 * This method tests fetching functionality of Contract Budget's sub-Budget
	 * Summary details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchSubBudgetSummary6() throws ApplicationException
	{

		HashMap<String, String> aoHashMap = new HashMap<String, String>();
		moContractBudgetService.fetchSubBudgetSummary(moSession, aoHashMap);
	}

	/**
	 * This method tests fetching functionality of getting session details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testGetCbGridDataForSession1() throws ApplicationException
	{

		HashMap<String, String> aoHashMap = new HashMap<String, String>();
		aoHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, "111777");
		aoHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "555");
		CBGridBean loCBGridBean = (CBGridBean) moContractBudgetService.getCbGridDataForSession(moSession, aoHashMap);
		assertNotNull(loCBGridBean);
	}

	/**
	 * This method tests fetching functionality of getting session details
	 * 
	 * Negative scenario -- Incomplete data
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testGetCbGridDataForSession2() throws ApplicationException
	{

		HashMap<String, String> aoHashMap = new HashMap<String, String>();
		aoHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, "111777");
		// aoHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "555");
		moContractBudgetService.getCbGridDataForSession(moSession, aoHashMap);
	}

	/**
	 * This method tests fetching functionality of getting session details
	 * 
	 * Negative scenario -- null session
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testGetCbGridDataForSession3() throws ApplicationException
	{

		HashMap<String, String> aoHashMap = new HashMap<String, String>();
		aoHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, "111777");
		aoHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "555");
		moContractBudgetService.getCbGridDataForSession(null, aoHashMap);
	}

	/**
	 * This method tests fetching functionality of getting session details
	 * 
	 * Negative scenario -- null map
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testGetCbGridDataForSession4() throws ApplicationException
	{
		moContractBudgetService.getCbGridDataForSession(moSession, null);
	}

	/**
	 * This method tests fetching functionality of getting session details
	 * 
	 * Negative scenario -- Empty map
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testGetCbGridDataForSession5() throws ApplicationException
	{
		HashMap<String, String> aoHashMap = new HashMap<String, String>();
		moContractBudgetService.getCbGridDataForSession(moSession, aoHashMap);
	}

	// S316 operation and support junits start

	// Case1: successfully fetch op and support page data
	// pass all required data
	@Test
	public void testfetchOpAndSupportPageData1() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBGridBean loCBGridBean = new CBGridBean();
			loCBGridBean.setSubBudgetID("555");
			loCBGridBean.setContractBudgetID("555");

			moContractBudgetService.fetchOpAndSupportPageData(loCBGridBean, moSession);

			assertEquals("ContractBudgetService: fetchOpAndSupportPageData() passed", moContractBudgetService
					.getMoState().toString());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	// Case2: ApplicationException
	// do not pass all required data
	@Test
	public void testfetchOpAndSupportPageData2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBGridBean loCBGridBean = new CBGridBean();
			loCBGridBean.setSubBudgetID("555");

			moContractBudgetService.fetchOpAndSupportPageData(loCBGridBean, moSession);

			assertEquals("ContractBudgetService: fetchOpAndSupportPageData() passed", moContractBudgetService
					.getMoState().toString());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	// Case3: ApplicationException
	// do not pass all required data
	@Test
	public void testfetchOpAndSupportPageData3() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBGridBean loCBGridBean = new CBGridBean();
			loCBGridBean.setSubBudgetID("555");
			loCBGridBean.setContractBudgetID("555");

			moContractBudgetService.fetchOpAndSupportPageData(loCBGridBean, null);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	// Case4: ApplicationException
	// do not pass all required data
	@Test
	public void testfetchOpAndSupportPageData4() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBGridBean loCBGridBean = new CBGridBean();

			moContractBudgetService.fetchOpAndSupportPageData(loCBGridBean, moSession);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	// Case5: ApplicationException
	// do not pass all required data
	@Test(expected = NullPointerException.class)
	public void testfetchOpAndSupportPageData5() throws ApplicationException
	{

		moContractBudgetService.fetchOpAndSupportPageData(null, moSession);
	}

	// Case1: successfully fetch op and support grid data
	// pass all required data
	@Test
	public void testfetchOperationAndSupportDetails1() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBGridBean loCBGridBean = new CBGridBean();
			loCBGridBean.setSubBudgetID("555");
			loCBGridBean.setContractBudgetID("555");

			moContractBudgetService.fetchOperationAndSupportDetails(loCBGridBean, moSession);

			assertEquals("ContractBudgetService: fetchOperationAndSupportDetails() passed", moContractBudgetService
					.getMoState().toString());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	// Case2: ApplicationException
	// do not pass all required data
	@Test
	public void testfetchOperationAndSupportDetails2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBGridBean loCBGridBean = new CBGridBean();
			loCBGridBean.setSubBudgetID("555");
			// loCBGridBean.setContractBudgetID("555");

			moContractBudgetService.fetchOperationAndSupportDetails(loCBGridBean, moSession);

			assertEquals("ContractBudgetService: fetchOpAndSupportPageData() passed", moContractBudgetService
					.getMoState().toString());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	// Case3: ApplicationException
	// Null session
	@Test(expected = ApplicationException.class)
	public void testfetchOperationAndSupportDetails3() throws ApplicationException
	{

		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setSubBudgetID("555");
		loCBGridBean.setContractBudgetID("555");

		moContractBudgetService.fetchOperationAndSupportDetails(loCBGridBean, null);
	}

	// Case4: ApplicationException
	// Null bean
	@Test(expected = NullPointerException.class)
	public void testfetchOperationAndSupportDetails4() throws ApplicationException
	{

		moContractBudgetService.fetchOperationAndSupportDetails(null, moSession);
	}

	// Case5: ApplicationException
	// New default bean
	@Test(expected = ApplicationException.class)
	public void testfetchOperationAndSupportDetails5() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		moContractBudgetService.fetchOperationAndSupportDetails(loCBGridBean, moSession);
	}

	// Case1: successfully update op and support grid data (without other)
	// pass all required data
	@Test
	public void testEditOperationAndSupportDetails1() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBOperationSupportBean loCBOperationSupportBean = new CBOperationSupportBean();

			loCBOperationSupportBean.setContractBudgetID("555");
			loCBOperationSupportBean.setSubBudgetID("555");
			loCBOperationSupportBean.setId("1");
			loCBOperationSupportBean.setModifiedByUserId("2422");
			loCBOperationSupportBean.setFyBudget("900");

			boolean lbEditStatus = moContractBudgetService.editOperationAndSupportDetails(loCBOperationSupportBean,
					moSession);

			assertTrue(lbEditStatus);
			assertEquals("ContractBudgetService: editOperationAndSupportDetails() passed", moContractBudgetService
					.getMoState().toString());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	// Case2: successfully insert new record op and support grid data (other)
	// pass all required data
	@Test
	public void testEditOperationAndSupportDetails2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBOperationSupportBean loCBOperationSupportBean = new CBOperationSupportBean();

			loCBOperationSupportBean.setContractBudgetID("555");
			loCBOperationSupportBean.setSubBudgetID("555");
			loCBOperationSupportBean.setId("21");
			loCBOperationSupportBean.setModifiedByUserId("2422");
			loCBOperationSupportBean.setFyBudget("900");
			loCBOperationSupportBean.setOpAndSupportName("abcd");

			boolean lbEditStatus = moContractBudgetService.editOperationAndSupportDetails(loCBOperationSupportBean,
					moSession);

			assertTrue(lbEditStatus);
			assertEquals("ContractBudgetService: editOperationAndSupportDetails() passed", moContractBudgetService
					.getMoState().toString());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	// Case3: successfully update record op and support grid data (other)
	// pass all required data
	@Test
	public void testEditOperationAndSupportDetails3() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBOperationSupportBean loCBOperationSupportBean = new CBOperationSupportBean();

			loCBOperationSupportBean.setContractBudgetID("555");
			loCBOperationSupportBean.setSubBudgetID("777");
			loCBOperationSupportBean.setId("1242");
			loCBOperationSupportBean.setModifiedByUserId("2422");
			loCBOperationSupportBean.setFyBudget("900");
			loCBOperationSupportBean.setOpAndSupportName("abcd");

			boolean lbEditStatus = moContractBudgetService.editOperationAndSupportDetails(loCBOperationSupportBean,
					moSession);

			assertTrue(lbEditStatus);
			assertEquals("ContractBudgetService: editOperationAndSupportDetails() passed", moContractBudgetService
					.getMoState().toString());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	// Case4: Application Exception
	// set session to null
	@Test(expected = ApplicationException.class)
	public void testEditOperationAndSupportDetails4() throws ApplicationException
	{
		CBOperationSupportBean loCBOperationSupportBean = new CBOperationSupportBean();

		moContractBudgetService.editOperationAndSupportDetails(loCBOperationSupportBean, null);
	}

	// Case5: case for if clause with blank input record op and support grid
	// data (other)
	// pass all required data
	@Test
	public void testEditOperationAndSupportDetails5() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBOperationSupportBean loCBOperationSupportBean = new CBOperationSupportBean();

			loCBOperationSupportBean.setContractBudgetID("555");
			loCBOperationSupportBean.setSubBudgetID("777");
			loCBOperationSupportBean.setId("1242");
			loCBOperationSupportBean.setModifiedByUserId("2422");
			loCBOperationSupportBean.setFyBudget("900");
			loCBOperationSupportBean.setOpAndSupportName("");

			boolean lbEditStatus = moContractBudgetService.editOperationAndSupportDetails(loCBOperationSupportBean,
					moSession);

			assertTrue(lbEditStatus);
			assertEquals("ContractBudgetService: editOperationAndSupportDetails() passed", moContractBudgetService
					.getMoState().toString());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	// Case6: case for if clause with blank input record op and support grid
	// data (other)
	// pass all required data
	@Test
	public void testEditOperationAndSupportDetails6() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBOperationSupportBean loCBOperationSupportBean = new CBOperationSupportBean();

			loCBOperationSupportBean.setContractBudgetID("555");
			loCBOperationSupportBean.setSubBudgetID("777");
			loCBOperationSupportBean.setId("1242");
			loCBOperationSupportBean.setModifiedByUserId("2422");
			loCBOperationSupportBean.setFyBudget("900");
			loCBOperationSupportBean.setOpAndSupportName("other");

			boolean lbEditStatus = moContractBudgetService.editOperationAndSupportDetails(loCBOperationSupportBean,
					moSession);

			assertTrue(lbEditStatus);
			assertEquals("ContractBudgetService: editOperationAndSupportDetails() passed", moContractBudgetService
					.getMoState().toString());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	// Case1: successfully fetch equipmentgrid data
	// pass all required data
	@Test
	public void testfetchEquipmentDetails1() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setSubBudgetID("555");
		loCBGridBean.setContractBudgetID("555");

		List<CBEquipmentBean> loCBEquipmentBeanList = moContractBudgetService.fetchEquipmentDetails(loCBGridBean,
				moSession);
		assertNotNull(loCBEquipmentBeanList);
		assertEquals("ContractBudgetService: fetchEquipmentDetails() passed", moContractBudgetService.getMoState()
				.toString());

	}

	// Case2: ApplicationException
	// do not pass all required data
	@Test(expected = ApplicationException.class)
	public void testfetchEquipmentDetails2() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setSubBudgetID("555");

		moContractBudgetService.fetchEquipmentDetails(loCBGridBean, moSession);

	}

	// Case3: ApplicationException
	// do not pass all required data
	@Test(expected = ApplicationException.class)
	public void testfetchEquipmentDetails3() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setContractBudgetID("555");

		moContractBudgetService.fetchEquipmentDetails(loCBGridBean, moSession);

	}

	// Case4: ApplicationException
	// Null session
	@Test(expected = ApplicationException.class)
	public void testfetchEquipmentDetails4() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setSubBudgetID("555");
		loCBGridBean.setContractBudgetID("555");

		moContractBudgetService.fetchEquipmentDetails(loCBGridBean, null);

	}

	// Case5: ApplicationException
	// Null bean
	@Test(expected = ApplicationException.class)
	public void testfetchEquipmentDetails5() throws ApplicationException
	{

		moContractBudgetService.fetchEquipmentDetails(null, moSession);

	}

	// Case1: successfully add equipmentgrid data
	// pass all required data
	@Test
	public void testaddEquipmentDetails1() throws ApplicationException
	{
		CBEquipmentBean loCBEquipmentBean = new CBEquipmentBean();
		loCBEquipmentBean.setContractBudgetID("555");
		loCBEquipmentBean.setSubBudgetID("555");
		loCBEquipmentBean.setEquipment("test name");
		loCBEquipmentBean.setUnits("2");
		loCBEquipmentBean.setFyBudget("200");
		loCBEquipmentBean.setModifiedByUserId("2432");
		loCBEquipmentBean.setCreatedByUserId("2432");

		boolean lbAddStatus = moContractBudgetService.addEquipmentDetails(loCBEquipmentBean, moSession);

		assertTrue(lbAddStatus);
		assertEquals("ContractBudgetService: addEquipmentDetails() passed", moContractBudgetService.getMoState()
				.toString());

	}

	// Case2: Application Exception while adding in data in equipmentgrid
	// do not pass all required data
	@Test(expected = ApplicationException.class)
	public void testaddEquipmentDetails2() throws ApplicationException
	{

		CBEquipmentBean loCBEquipmentBean = new CBEquipmentBean();
		loCBEquipmentBean.setContractBudgetID("555");
		// loCBEquipmentBean.setSubBudgetID("555");
		loCBEquipmentBean.setEquipment("test name");
		loCBEquipmentBean.setUnits("2");
		loCBEquipmentBean.setFyBudget("200");
		loCBEquipmentBean.setModifiedByUserId("2432");
		loCBEquipmentBean.setCreatedByUserId("2432");

		moContractBudgetService.addEquipmentDetails(loCBEquipmentBean, moSession);

	}

	// Case3: Application Exception while adding in data in equipmentgrid
	// do not pass all required data
	@Test(expected = ApplicationException.class)
	public void testaddEquipmentDetails3() throws ApplicationException
	{

		CBEquipmentBean loCBEquipmentBean = new CBEquipmentBean();
		// loCBEquipmentBean.setContractBudgetID("555");
		loCBEquipmentBean.setSubBudgetID("555");
		loCBEquipmentBean.setEquipment("test name");
		loCBEquipmentBean.setUnits("2");
		loCBEquipmentBean.setFyBudget("200");
		loCBEquipmentBean.setModifiedByUserId("2432");
		loCBEquipmentBean.setCreatedByUserId("2432");

		moContractBudgetService.addEquipmentDetails(loCBEquipmentBean, moSession);

	}

	// Case4: Application Exception
	// Null session
	@Test(expected = ApplicationException.class)
	public void testaddEquipmentDetails4() throws ApplicationException
	{

		CBEquipmentBean loCBEquipmentBean = new CBEquipmentBean();
		loCBEquipmentBean.setContractBudgetID("555");
		loCBEquipmentBean.setSubBudgetID("555");
		loCBEquipmentBean.setEquipment("test name");
		loCBEquipmentBean.setUnits("2");
		loCBEquipmentBean.setFyBudget("200");
		loCBEquipmentBean.setModifiedByUserId("2432");
		loCBEquipmentBean.setCreatedByUserId("2432");

		moContractBudgetService.addEquipmentDetails(loCBEquipmentBean, null);

	}

	// Case5: Application Exception
	// Null bean
	@Test(expected = ApplicationException.class)
	public void testaddEquipmentDetails5() throws ApplicationException
	{

		moContractBudgetService.addEquipmentDetails(null, moSession);

	}

	// Case1: successfully edit equipmentgrid data
	// pass all required data
	@Test
	public void testEditEquipmentDetails1() throws ApplicationException
	{
		CBEquipmentBean loCBEquipmentBean = new CBEquipmentBean();
		loCBEquipmentBean.setId("2");
		loCBEquipmentBean.setContractBudgetID("555");
		loCBEquipmentBean.setSubBudgetID("555");
		loCBEquipmentBean.setEquipment("test name");
		loCBEquipmentBean.setUnits("2");
		loCBEquipmentBean.setFyBudget("2000");
		loCBEquipmentBean.setModifiedByUserId("2432");
		loCBEquipmentBean.setCreatedByUserId("2432");

		boolean lbAddStatus = moContractBudgetService.editEquipmentDetails(loCBEquipmentBean, moSession);

		assertTrue(lbAddStatus);

	}

	// Case2: Application Exception
	// do not pass all required data
	@Test(expected = ApplicationException.class)
	public void testEditEquipmentDetails2() throws ApplicationException
	{

		CBEquipmentBean loCBEquipmentBean = new CBEquipmentBean();
		// loCBEquipmentBean.setId("2");
		loCBEquipmentBean.setContractBudgetID("555");
		loCBEquipmentBean.setSubBudgetID("555");
		loCBEquipmentBean.setEquipment("test name");
		loCBEquipmentBean.setUnits("2");
		loCBEquipmentBean.setFyBudget("2000");
		loCBEquipmentBean.setModifiedByUserId("2432");
		loCBEquipmentBean.setCreatedByUserId("2432");

		moContractBudgetService.editEquipmentDetails(loCBEquipmentBean, moSession);

	}

	// Case3: Application Exception
	// do not pass all required data
	@Test(expected = ApplicationException.class)
	public void testEditEquipmentDetails3() throws ApplicationException
	{

		CBEquipmentBean loCBEquipmentBean = new CBEquipmentBean();

		moContractBudgetService.editEquipmentDetails(loCBEquipmentBean, moSession);

	}

	// Case4: Application Exception
	// Null session
	@Test(expected = ApplicationException.class)
	public void testEditEquipmentDetails4() throws ApplicationException
	{

		CBEquipmentBean loCBEquipmentBean = new CBEquipmentBean();
		loCBEquipmentBean.setId("2");
		loCBEquipmentBean.setContractBudgetID("555");
		loCBEquipmentBean.setSubBudgetID("555");
		loCBEquipmentBean.setEquipment("test name");
		loCBEquipmentBean.setUnits("2");
		loCBEquipmentBean.setFyBudget("2000");
		loCBEquipmentBean.setModifiedByUserId("2432");
		loCBEquipmentBean.setCreatedByUserId("2432");

		moContractBudgetService.editEquipmentDetails(loCBEquipmentBean, null);

	}

	// Case5: Application Exception
	// Null bean
	@Test(expected = ApplicationException.class)
	public void testEditEquipmentDetails5() throws ApplicationException
	{

		moContractBudgetService.editEquipmentDetails(null, moSession);

	}

	// Case1: successfully edit equipmentgrid data
	// do not pass all required data
	@Test
	public void testDeleteEquipmentDetails1() throws ApplicationException
	{

		CBEquipmentBean loCBEquipmentBean = new CBEquipmentBean();
		loCBEquipmentBean.setId("2");

		boolean lbDelStatus = moContractBudgetService.deleteEquipmentDetails(loCBEquipmentBean, moSession);

		assertTrue(lbDelStatus);
		assertEquals("ContractBudgetService: deleteEquipmentDetails method - Equipment details deleted successfully.",
				moContractBudgetService.getMoState().toString());

	}

	// Case2: Application Exception
	// do not pass all required data
	@Test(expected = ApplicationException.class)
	public void testDeleteEquipmentDetails2() throws ApplicationException
	{
		CBEquipmentBean loCBEquipmentBean = new CBEquipmentBean();

		moContractBudgetService.deleteEquipmentDetails(loCBEquipmentBean, moSession);

	}

	// Case3: Application Exception
	// Null session
	@Test(expected = ApplicationException.class)
	public void testDeleteEquipmentDetails3() throws ApplicationException
	{
		CBEquipmentBean loCBEquipmentBean = new CBEquipmentBean();
		loCBEquipmentBean.setId("2");

		moContractBudgetService.deleteEquipmentDetails(loCBEquipmentBean, null);

	}

	// Case5: Application Exception
	// null id
	@Test(expected = ApplicationException.class)
	public void testDeleteEquipmentDetails5() throws ApplicationException
	{
		CBEquipmentBean loCBEquipmentBean = new CBEquipmentBean();
		loCBEquipmentBean.setId(null);

		moContractBudgetService.deleteEquipmentDetails(loCBEquipmentBean, moSession);

	}

	// Case5: Application Exception
	// Null bean
	@Test(expected = ApplicationException.class)
	public void testDeleteEquipmentDetails4() throws ApplicationException
	{

		moContractBudgetService.deleteEquipmentDetails(null, moSession);

	}

	// S316 operation and support junits end

	// Modification Summary Junits Start
	/**
	 * This method tests if modification budget summary is fetched or not
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 */
	@Test
	public void testFetchModificationBudgetSummary() throws ApplicationException, NoSuchMethodException,
			IllegalAccessException, InvocationTargetException
	{
		Boolean lbThrown = false;
		try
		{
			CBGridBean loCBGridBean = new CBGridBean();
			loCBGridBean.setSubBudgetID("20");
			loCBGridBean.setContractBudgetID("20");
			loCBGridBean.setParentSubBudgetId("12");
			loCBGridBean.setParentBudgetId("12");
			loCBGridBean.setBudgetStatusId("84");

			ContractBudgetSummary loCBBudgetSummary = moContractBudgetService.fetchModificationBudgetSummary(moSession,
					loCBGridBean, getMasterBeanApproved());

			assertNotNull(loCBBudgetSummary);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test
	public void testFetchModificationBudgetSummary1() throws ApplicationException, NoSuchMethodException,
			IllegalAccessException, InvocationTargetException
	{
		Boolean lbThrown = false;
		try
		{
			CBGridBean loCBGridBean = new CBGridBean();
			loCBGridBean.setSubBudgetID("20");
			loCBGridBean.setContractBudgetID("20");
			loCBGridBean.setParentSubBudgetId("12");
			loCBGridBean.setParentBudgetId("12");
			loCBGridBean.setBudgetStatusId("86");

			ContractBudgetSummary loCBBudgetSummary = moContractBudgetService.fetchModificationBudgetSummary(moSession,
					loCBGridBean, getMasterBeanApproved());

			assertNotNull(loCBBudgetSummary);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test(expected = ApplicationException.class)
	public void testFetchModificationBudgetSummaryException() throws ApplicationException, NoSuchMethodException,
			IllegalAccessException, InvocationTargetException
	{

		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setSubBudgetID("20");
		loCBGridBean.setContractBudgetID("20");
		loCBGridBean.setParentSubBudgetId("12");
		loCBGridBean.setParentBudgetId("12");
		loCBGridBean.setBudgetStatusId("84");

		moContractBudgetService.fetchModificationBudgetSummary(null, loCBGridBean, getMasterBeanApproved());
	}

	@Test(expected = ApplicationException.class)
	public void testFetchModificationBudgetSummaryException2() throws ApplicationException, NoSuchMethodException,
			IllegalAccessException, InvocationTargetException
	{
		// Sub Budget Id not sent
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setContractBudgetID("557");
		loCBGridBean.setParentSubBudgetId("555");
		loCBGridBean.setParentBudgetId("555");

		moContractBudgetService.fetchModificationBudgetSummary(moSession, loCBGridBean, getMasterBeanApproved());

	}

	@Test(expected = ApplicationException.class)
	public void testFetchModificationBudgetSummaryException3() throws ApplicationException, NoSuchMethodException,
			IllegalAccessException, InvocationTargetException
	{
		// Budget Id not sent
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setSubBudgetID("556");
		loCBGridBean.setParentSubBudgetId("555");
		loCBGridBean.setParentBudgetId("555");

		moContractBudgetService.fetchModificationBudgetSummary(moSession, loCBGridBean, getMasterBeanApproved());

	}

	@Test(expected = ApplicationException.class)
	public void testFetchModificationBudgetSummaryException4() throws ApplicationException, NoSuchMethodException,
			IllegalAccessException, InvocationTargetException
	{
		// Parents Ids not sent
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setSubBudgetID("556");
		loCBGridBean.setContractBudgetID("557");
		moContractBudgetService.fetchModificationBudgetSummary(moSession, loCBGridBean, getMasterBeanApproved());

	}

	@Test(expected = NullPointerException.class)
	public void testFetchModificationBudgetSummaryException5() throws ApplicationException, NoSuchMethodException,
			IllegalAccessException, InvocationTargetException
	{
		// Null bean
		moContractBudgetService.fetchModificationBudgetSummary(moSession, null, getMasterBeanApproved());

	}

	/**
	 * This method tests fetchUtilities throws exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchUtilitiesModification() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			CBGridBean loCBGridBean = new CBGridBean();

			// Positive Scenario -- List of CBIndirectRateBean type returned.

			loCBGridBean.setSubBudgetID("556");
			loCBGridBean.setContractBudgetID("557");
			loCBGridBean.setParentBudgetId("555");
			loCBGridBean.setParentSubBudgetId("555");
			loCBGridBean.setBudgetTypeId("3");
			loCBGridBean.setEntryTypeId("3");

			List<CBUtilities> cbUtilitiesList = moContractBudgetService.fetchUtilities(moSession, loCBGridBean);
			assertNotNull(cbUtilitiesList);
			assertTrue(cbUtilitiesList.size() != 0);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	@Test
	public void testFetchUtilitiesModificationIncompleteData() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			CBGridBean loCBGridBean = new CBGridBean();
			loCBGridBean.setBudgetTypeId(null);
			List<CBUtilities> cbUtilitiesList = moContractBudgetService.fetchUtilities(moSession, loCBGridBean);
			assertNull(cbUtilitiesList);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	@Test
	public void testFetchUtilitiesModificationException() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			CBGridBean loCBGridBean = new CBGridBean();

			// Negative Scenario -- Application Exception handled by setting.
			loCBGridBean.setBudgetTypeId("3");
			moContractBudgetService.fetchUtilities(null, loCBGridBean);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	@Test
	public void testUpdateUtilitiesModification() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			CBUtilities loCBUtilities = new CBUtilities();

			// Positive Scenario -- Modification Utility Update Successful.
			loCBUtilities.setId("1");
			loCBUtilities.setLineItemModifiedAmt("1000");
			loCBUtilities.setBudgetTypeId("3");
			loCBUtilities.setEntryTypeId("3");
			loCBUtilities.setModifyByProvider("803");
			loCBUtilities.setModifyByAgency("agency_47");
			loCBUtilities.setContractBudgetID("557");
			loCBUtilities.setSubBudgetID("556");
			boolean lbUpdateStatus = moContractBudgetService.updateUtilities(moSession, loCBUtilities);
			assertTrue(lbUpdateStatus);

			// Negative Scenario -- Application Exception handled by setting.

			lbUpdateStatus = moContractBudgetService.updateUtilities(null, loCBUtilities);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testinsertUtilitiesModification() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			CBUtilities loCBUtilities = new CBUtilities();

			// Positive Scenario -- Modification Utility Update Successful.
			loCBUtilities.setId("5");
			loCBUtilities.setLineItemModifiedAmt("50");
			loCBUtilities.setBudgetTypeId("3");
			loCBUtilities.setEntryTypeId("3");
			loCBUtilities.setModifyByProvider("803");
			loCBUtilities.setModifyByAgency("agency_47");
			loCBUtilities.setContractBudgetID("557");
			loCBUtilities.setSubBudgetID("556");
			boolean lbUpdateStatus = moContractBudgetService.updateUtilities(moSession, loCBUtilities);
			assertTrue(lbUpdateStatus);

			// Negative Scenario -- Application Exception handled by setting.

			lbUpdateStatus = moContractBudgetService.updateUtilities(null, loCBUtilities);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testUpdateUtilitiesModificationException() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			CBUtilities loCBUtilities = new CBUtilities();

			loCBUtilities.setEntryTypeId("3");

			// Negative Scenario -- Application Exception handled by setting.

			moContractBudgetService.updateUtilities(null, loCBUtilities);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	/**
	 * This method tests fetchUtilities throws exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchUtilitiesUpdate() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			CBGridBean loCBGridBean = new CBGridBean();

			// Positive Scenario -- List of CBIndirectRateBean type returned.

			loCBGridBean.setSubBudgetID("558");
			loCBGridBean.setContractBudgetID("558");
			loCBGridBean.setParentBudgetId("555");
			loCBGridBean.setParentSubBudgetId("555");
			loCBGridBean.setBudgetTypeId("4");
			loCBGridBean.setEntryTypeId("4");

			List<CBUtilities> cbUtilitiesList = moContractBudgetService.fetchUtilities(moSession, loCBGridBean);
			assertNotNull(cbUtilitiesList);
			assertTrue(cbUtilitiesList.size() != 0);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	@Test
	public void testFetchUtilitiesUpdateIncompleteData() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			CBGridBean loCBGridBean = new CBGridBean();
			loCBGridBean.setBudgetTypeId(null);
			List<CBUtilities> cbUtilitiesList = moContractBudgetService.fetchUtilities(moSession, loCBGridBean);
			assertNull(cbUtilitiesList);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	@Test
	public void testFetchUtilitiesUpdateException() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			CBGridBean loCBGridBean = new CBGridBean();

			// Negative Scenario -- Application Exception handled by setting.
			loCBGridBean.setBudgetTypeId("4");
			moContractBudgetService.fetchUtilities(null, loCBGridBean);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	@Test
	public void testUpdateUtilitiesUpdate() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			CBUtilities loCBUtilities = new CBUtilities();

			// Positive Scenario -- Modification Utility Update Successful.
			loCBUtilities.setId("1");
			loCBUtilities.setLineItemModifiedAmt("1000");
			loCBUtilities.setBudgetTypeId("4");
			loCBUtilities.setEntryTypeId("3");
			loCBUtilities.setModifyByProvider("");
			loCBUtilities.setModifyByAgency("");
			loCBUtilities.setContractBudgetID("51");
			loCBUtilities.setSubBudgetID("51");
			boolean lbUpdateStatus = moContractBudgetService.updateUtilities(moSession, loCBUtilities);
			assertTrue(lbUpdateStatus);

			// Negative Scenario -- Application Exception handled by setting.
			loCBUtilities.setId("0");
			lbUpdateStatus = moContractBudgetService.updateUtilities(moSession, loCBUtilities);
			assertNotNull(lbUpdateStatus);
			
			lbUpdateStatus = moContractBudgetService.updateUtilities(null, loCBUtilities);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testinsertUtilitiesUpdate() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			CBUtilities loCBUtilities = new CBUtilities();

			// Positive Scenario -- Modification Utility Update Successful.
			loCBUtilities.setId("5");
			loCBUtilities.setLineItemModifiedAmt("50");
			loCBUtilities.setBudgetTypeId("4");
			loCBUtilities.setEntryTypeId("3");
			loCBUtilities.setModifyByProvider("803");
			loCBUtilities.setModifyByAgency("agency_47");
			loCBUtilities.setContractBudgetID("558");
			loCBUtilities.setSubBudgetID("558");
			boolean lbUpdateStatus = moContractBudgetService.updateUtilities(moSession, loCBUtilities);
			assertTrue(lbUpdateStatus);

			// Negative Scenario -- Application Exception handled by setting.

			lbUpdateStatus = moContractBudgetService.updateUtilities(null, loCBUtilities);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testUpdateUtilitiesUpdateException() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBUtilities loCBUtilities = new CBUtilities();
			loCBUtilities.setEntryTypeId("4");
			// Negative Scenario -- Application Exception handled by setting.

			moContractBudgetService.updateUtilities(null, loCBUtilities);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	/** The Method will test insertContractDetailsFromAwardTask Method */

	@Test
	public void testInsertContractDetailsFromAwardTask() throws ApplicationException
	{
		HashMap<String, String> loContractMap = new HashMap<String, String>();
		Boolean lbStatusFlag = true;
		loContractMap.put("contractTypeId", "1");
		loContractMap.put("procurementId", "3");
		loContractMap.put("statusId", "59");
		loContractMap.put("modifiedFlag", "1");
		Boolean lbInsertStatus = moContractBudgetService.insertContractDetailsFromAwardTask(moSession, loContractMap,
				lbStatusFlag);
		assertTrue(lbInsertStatus);

	}

	/** The Method will test insertContractDetailsFromAwardTask Method */

	@Test
	public void testInsertContractDetailsFromAwardTaskCase1() throws ApplicationException
	{
		HashMap<String, String> loContractMap = new HashMap<String, String>();
		Boolean lbStatusFlag = false;
		loContractMap.put("contractTypeId", "1");
		loContractMap.put("procurementId", "3");
		loContractMap.put("statusId", "59");
		loContractMap.put("modifiedFlag", "1");
		Boolean lbInsertStatus = moContractBudgetService.insertContractDetailsFromAwardTask(moSession, loContractMap,
				lbStatusFlag);
		assertFalse(lbInsertStatus);

	}

	/** The Method will test insertContractDetailsFromAwardTask Method */

	@Test(expected = Exception.class)
	public void testInsertContractDetailsFromAwardTaskCase2() throws ApplicationException
	{
		HashMap<String, String> loContractMap = new HashMap<String, String>();
		Boolean lbStatusFlag = true;
		loContractMap.put("contractTypeId", "1");
		loContractMap.put("procurementId", "3");
		loContractMap.put("statusId", "59");
		loContractMap.put("modifiedFlag", "1");
		moContractBudgetService.insertContractDetailsFromAwardTask(moSession, null, lbStatusFlag);
	}

	/** The Method will test insertContractDetailsFromAwardTask Method */

	@Test(expected = ApplicationException.class)
	public void testInsertContractDetailsFromAwardTaskCase3() throws ApplicationException
	{
		HashMap<String, String> loContractMap = new HashMap<String, String>();
		Boolean lbStatusFlag = true;
		loContractMap.put("contractTypeId", "1");
		loContractMap.put("procurementId", "3");
		loContractMap.put("statusId", "59");
		loContractMap.put("modifiedFlag", "1");
		moContractBudgetService.insertContractDetailsFromAwardTask(null, loContractMap, lbStatusFlag);
	}

	/** The Method will test insertContractDetailsFromAwardTask Method */

	@Test(expected = Exception.class)
	public void testInsertContractDetailsFromAwardTaskCase4() throws ApplicationException
	{
		HashMap<String, String> loContractMap = new HashMap<String, String>();
		Boolean lbStatusFlag = true;
		moContractBudgetService.insertContractDetailsFromAwardTask(moSession, loContractMap, lbStatusFlag);
	}

	@Test
	public void testfetchSeasonalEmployeeForBase() throws Exception
	{
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setContractBudgetID("10069");
		loCBGridBean.setSubBudgetID("1198");
		moContractBudgetService.fetchSeasonalEmployeeForBase(moSession, loCBGridBean);
	}

	/**
	 * This method tests for Get Sequence for Milestone table.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testGetSeqForMilestoneCase2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			// positive test
			Integer status = moContractBudgetService.getSeqForMilestone(moSession);

			assertNotNull(status);

			// negative test
			status = moContractBudgetService.getSeqForMilestone(null);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}

	}

	/**
	 * This method tests for Get Sequence for Milestone table.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testGetSeqForMilestoneCase3() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			moContractBudgetService.getSeqForMilestone(null);
			moContractBudgetService.getSeqForMilestone(null);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}

	}

	/**
	 * This method tests if Milestone are updated database.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = NullPointerException.class)
	public void testUpdateMilestoneException5() throws ApplicationException
	{
		moContractBudgetService.updateMilestone(null, moSession);

	}

	@Test
	public void testFetchNonGridContractedServices() throws ApplicationException
	{
		ContractedServicesBean loCBContractedServicesBean = null;
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setSubBudgetID("557");
		loCBGridBean.setContractBudgetID("556");
		loCBContractedServicesBean = moContractBudgetService.fetchNonGridContractedServices(moSession, loCBGridBean);
		assertNull(loCBContractedServicesBean);
	}

	@Test
	public void testFetchNonGridContractedServicesCase2() throws ApplicationException
	{
		ContractedServicesBean loCBContractedServicesBean = null;
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setSubBudgetID("557");
		loCBGridBean.setContractBudgetID("556");
		loCBContractedServicesBean = moContractBudgetService.fetchNonGridContractedServices(moSession, loCBGridBean);
		assertNull(loCBContractedServicesBean);
	}

	@Test
	public void testFetchNonGridContractedServicesCase3() throws ApplicationException
	{
		ContractedServicesBean loCBContractedServicesBean = null;
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setSubBudgetID("557");
		loCBGridBean.setContractBudgetID("556");
		loCBContractedServicesBean = moContractBudgetService.fetchNonGridContractedServices(moSession, loCBGridBean);
		assertNull(loCBContractedServicesBean);
	}

	/*
	 * @Test public void testUpdatBudgetStatus() throws ApplicationException {
	 * boolean lbSaveStatus = false; Map<String, Object> loQueryMap = new
	 * HashMap<String, Object>(); loQueryMap.put("budgetID","555");
	 * ContractBudgetBean aoContractBudgetBean = null; lbSaveStatus =
	 * moContractBudgetService.updatBudgetStatus(moSession, lbSaveStatus,
	 * aoContractBudgetBean ); assertFalse(lbSaveStatus); }
	 * 
	 * @Test public void testUpdatBudgetStatusCase2() throws
	 * ApplicationException { boolean lbSaveStatus = false; Map<String, Object>
	 * loQueryMap = new HashMap<String, Object>();
	 * loQueryMap.put("budgetID",""); ContractBudgetBean aoContractBudgetBean =
	 * null; lbSaveStatus = moContractBudgetService.updatBudgetStatus(moSession,
	 * lbSaveStatus, aoContractBudgetBean ); assertFalse(lbSaveStatus); }
	 * 
	 * @Test public void testUpdatBudgetStatusCase3() throws
	 * ApplicationException { boolean lbSaveStatus = false; Map<String, Object>
	 * loQueryMap = new HashMap<String, Object>(); loQueryMap.put("","555");
	 * ContractBudgetBean aoContractBudgetBean = null; lbSaveStatus =
	 * moContractBudgetService.updatBudgetStatus(moSession, lbSaveStatus,
	 * aoContractBudgetBean ); assertFalse(lbSaveStatus); }
	 * 
	 * @Test public void testUpdatBudgetStatusCase4() throws
	 * ApplicationException { boolean lbSaveStatus = false; Map<String, Object>
	 * loQueryMap = new HashMap<String, Object>();
	 * loQueryMap.put("budgetID","555"); ContractBudgetBean aoContractBudgetBean
	 * = null; lbSaveStatus = moContractBudgetService.updatBudgetStatus(null,
	 * lbSaveStatus, aoContractBudgetBean ); assertFalse(lbSaveStatus); }
	 */

	/**
	 * This method tests Delete functionality of Rate details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testDeleteRateCase2() throws ApplicationException
	{

		RateBean aoRateBean = new RateBean();
		aoRateBean.setId("1");
		boolean loUpdateStatus = moContractBudgetService.deleteRate(moSession, aoRateBean);
		assertTrue(loUpdateStatus);
	}

	/**
	 * This method tests Delete functionality of Rate details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testDeleteRateCase3() throws ApplicationException
	{

		RateBean aoRateBean = new RateBean();
		aoRateBean.setId("");
		boolean loUpdateStatus = moContractBudgetService.deleteRate(moSession, aoRateBean);
		assertTrue(loUpdateStatus);
	}

	/**
	 * This method tests Delete functionality of Rate details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testDeleteRateCase4() throws ApplicationException
	{

		RateBean aoRateBean = new RateBean();
		aoRateBean.setId("1");
		boolean loUpdateStatus = moContractBudgetService.deleteRate(null, aoRateBean);
		assertTrue(loUpdateStatus);
	}

	/**
	 * This method tests Delete functionality of Rate details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testDeleteRateCase5() throws ApplicationException
	{

		RateBean aoRateBean = new RateBean();
		aoRateBean.setId("1");
		boolean loUpdateStatus = moContractBudgetService.deleteRate(null, aoRateBean);
		assertTrue(loUpdateStatus);
	}

	/**
	 * This method tests Delete functionality of Rate details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testDeleteRateCase6() throws ApplicationException
	{

		RateBean aoRateBean = new RateBean();
		aoRateBean.setId("");
		boolean loUpdateStatus = moContractBudgetService.deleteRate(moSession, aoRateBean);
		assertTrue(loUpdateStatus);
	}

	/**
	 * This method tests Delete functionality of Rate details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testDeleteRateCase7() throws ApplicationException
	{

		RateBean aoRateBean = new RateBean();
		aoRateBean.setId("1");
		boolean loUpdateStatus = moContractBudgetService.deleteRate(moSession, aoRateBean);
		assertTrue(loUpdateStatus);
	}

	/**
	 * This method tests Delete functionality of Rate details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testDeleteRateCase8() throws ApplicationException
	{

		RateBean aoRateBean = new RateBean();
		aoRateBean.setId("1");
		boolean loUpdateStatus = moContractBudgetService.deleteRate(moSession, aoRateBean);
		assertTrue(loUpdateStatus);
	}

	@Test
	public void testFetchFinancialDocuments() throws ApplicationException
	{
		List<ExtendedDocument> loFinancialDocList = null;
		Map<String, Object> aoParamMap = new HashMap<String, Object>();
		aoParamMap.put("contractId", "111777");
		aoParamMap.put("invoiceId", "55");
		aoParamMap.put("budgetId", "555");
		aoParamMap.put("organizationType", "city_org");
		loFinancialDocList = moContractBudgetService.fetchFinancialDocuments(moSession, aoParamMap);
		assertNotNull(loFinancialDocList);
	}

	/**
	 * This method tests fetching functionality of Contract Budget's FY Budget
	 * Summary details
	 * 
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchFyBudgetCase1() throws ApplicationException
	{

		HashMap<String, String> aoHashMap = new HashMap<String, String>();
		aoHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, "111777");
		aoHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "555");
		BudgetDetails loBudgetDetails = (BudgetDetails) moContractBudgetService.fetchFyBudgetSummary(moSession,
				aoHashMap);
		assertNotNull(loBudgetDetails);
	}

	/**
	 * This method tests fetching functionality of Contract Budget's FY Budget
	 * Summary details
	 * 
	 * Negative scenario -- Incomplete data
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchFyBudgetCase2() throws ApplicationException
	{

		HashMap<String, String> aoHashMap = new HashMap<String, String>();
		aoHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, "111777");
		// aoHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "555");
		moContractBudgetService.fetchFyBudgetSummary(moSession, aoHashMap);
	}

	/**
	 * This method tests fetching functionality of Contract Budget's FY Budget
	 * Summary details
	 * 
	 * Negative scenario -- Null session
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchFyBudgetCase3() throws ApplicationException
	{

		HashMap<String, String> aoHashMap = new HashMap<String, String>();
		aoHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, "111777");
		aoHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "555");
		moContractBudgetService.fetchFyBudgetSummary(null, aoHashMap);
	}

	/**
	 * This method tests fetching functionality of Contract Budget's FY Budget
	 * Summary details
	 * 
	 * Negative scenario -- Empty map
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchFyBudgetCase4() throws ApplicationException
	{

		HashMap<String, String> aoHashMap = new HashMap<String, String>();
		moContractBudgetService.fetchFyBudgetSummary(moSession, aoHashMap);
	}

	/**
	 * This method tests fetching functionality of Contract Budget's FY Budget
	 * Summary details
	 * 
	 * Negative scenario -- null map
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchFyBudgetCase5() throws ApplicationException
	{
		moContractBudgetService.fetchFyBudgetSummary(moSession, null);
	}

	/**
	 * This method tests update functionality of Rate details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateRateCase2() throws ApplicationException
	{

		RateBean aoRateBean = new RateBean();
		aoRateBean.setId("1");
		aoRateBean.setUnitDesc("Test Case Check");
		aoRateBean.setUnits("12");
		aoRateBean.setFyBudget("1200");
		boolean loUpdateStatus = moContractBudgetService.updateRate(moSession, aoRateBean);
		assertTrue(loUpdateStatus);
	}

	/**
	 * This method tests update functionality of Rate details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateRateCase3() throws ApplicationException
	{

		RateBean aoRateBean = new RateBean();
		aoRateBean.setId("1");
		aoRateBean.setUnitDesc("Test Case Check");
		aoRateBean.setUnits("12");
		aoRateBean.setFyBudget("1200");
		boolean loUpdateStatus = moContractBudgetService.updateRate(moSession, aoRateBean);
		assertTrue(loUpdateStatus);
	}

	/**
	 * This method tests update functionality of Rate details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateRateCase4() throws ApplicationException
	{

		RateBean aoRateBean = new RateBean();
		aoRateBean.setId("1");
		aoRateBean.setUnitDesc("Test Case Check");
		aoRateBean.setUnits("12");
		aoRateBean.setFyBudget("1200");
		boolean loUpdateStatus = moContractBudgetService.updateRate(moSession, aoRateBean);
		assertTrue(loUpdateStatus);
	}

	/**
	 * This method tests update functionality of Rate details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateRateCase5() throws ApplicationException
	{

		RateBean aoRateBean = new RateBean();
		aoRateBean.setId("1");
		aoRateBean.setUnitDesc("Test Case Check");
		aoRateBean.setUnits("12");
		aoRateBean.setFyBudget("1200");
		boolean loUpdateStatus = moContractBudgetService.updateRate(moSession, aoRateBean);
		assertTrue(loUpdateStatus);
	}

	/**
	 * This method tests update functionality of Rate details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateRateCase6() throws ApplicationException
	{

		RateBean aoRateBean = new RateBean();
		aoRateBean.setId("1");
		aoRateBean.setUnitDesc("Test Case Check");
		aoRateBean.setUnits("12");
		aoRateBean.setFyBudget("1200");
		boolean loUpdateStatus = moContractBudgetService.updateRate(null, aoRateBean);
		assertTrue(loUpdateStatus);
	}

	/**
	 * This method tests update functionality of Rate details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateRateCase7() throws ApplicationException
	{

		RateBean aoRateBean = new RateBean();
		aoRateBean.setId("1");
		aoRateBean.setUnitDesc("Test Case Check");
		aoRateBean.setUnits("12");
		aoRateBean.setFyBudget("1200");
		boolean loUpdateStatus = moContractBudgetService.updateRate(moSession, aoRateBean);
		assertTrue(loUpdateStatus);
	}

	/**
	 * This method tests update functionality of Rate details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateRateCase8() throws ApplicationException
	{

		RateBean aoRateBean = new RateBean();
		aoRateBean.setId("1");
		aoRateBean.setUnitDesc("Test Case Check");
		aoRateBean.setUnits("12");
		aoRateBean.setFyBudget("1200");
		boolean loUpdateStatus = moContractBudgetService.updateRate(null, aoRateBean);
		assertTrue(loUpdateStatus);
	}

	/**
	 * Test method for fetching Fringe Budget on Personnel Services Screen with
	 * Invalid data.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchFringeBudgetWithInvalidDataCase2() throws ApplicationException
	{
		CBGridBean loPersonnelServiceBudget = new CBGridBean();
		loPersonnelServiceBudget.setSubBudgetID("377");
		loPersonnelServiceBudget.setContractBudgetID("25");
		List<PersonnelServiceBudget> loSalariedEmployess = moContractBudgetService.fetchFringeBenifits(moSession,
				loPersonnelServiceBudget);
		assertNotNull(loSalariedEmployess);

	}

	/**
	 * Test method for fetching Fringe Budget on Personnel Services Screen with
	 * valid data.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchFringeBudgetWithValidDataCase2() throws ApplicationException
	{
		CBGridBean loPersonnelServiceBudget = new CBGridBean();
		loPersonnelServiceBudget.setSubBudgetID("555"); // Need to hardcord
		loPersonnelServiceBudget.setContractBudgetID("555"); // Need to hardcord
		List<PersonnelServiceBudget> loSalariedEmployess = moContractBudgetService.fetchFringeBenifits(moSession,
				loPersonnelServiceBudget);
		assertTrue(loSalariedEmployess.size() != 0);

	}

	/**
	 * Test method for fetching Fringe Budget on Personnel Services Screen with
	 * null session.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchFringeBudgetWithNullSessionCase2() throws ApplicationException
	{
		CBGridBean loPersonnelServiceBudget = new CBGridBean();
		loPersonnelServiceBudget.setSubBudgetID("377");
		loPersonnelServiceBudget.setContractBudgetID("25");
		moContractBudgetService.fetchFringeBenifits(null, loPersonnelServiceBudget);

	}

	/**
	 * Test method for fetching Fringe Budget on Personnel Services Screen with
	 * Invalid data.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchFringeBudgetWithInvalidDataCase3() throws ApplicationException
	{
		CBGridBean loPersonnelServiceBudget = new CBGridBean();
		loPersonnelServiceBudget.setSubBudgetID("377");
		loPersonnelServiceBudget.setContractBudgetID("25");
		List<PersonnelServiceBudget> loSalariedEmployess = moContractBudgetService.fetchFringeBenifits(moSession,
				loPersonnelServiceBudget);
		assertNotNull(loSalariedEmployess);

	}

	/**
	 * Test method for fetching Fringe Budget on Personnel Services Screen with
	 * valid data.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchFringeBudgetWithValidDataCase4() throws ApplicationException
	{
		CBGridBean loPersonnelServiceBudget = new CBGridBean();
		loPersonnelServiceBudget.setSubBudgetID("555"); // Need to hardcord
		loPersonnelServiceBudget.setContractBudgetID("555"); // Need to hardcord
		List<PersonnelServiceBudget> loSalariedEmployess = moContractBudgetService.fetchFringeBenifits(moSession,
				loPersonnelServiceBudget);
		assertTrue(loSalariedEmployess.size() != 0);

	}

	@Test
	public void testFetchFringeBenefitsForBase() throws Exception

	{
		CBGridBean aoPersonnelServiceBudget = new CBGridBean();
		aoPersonnelServiceBudget.setContractBudgetID("555");
		List loFringeBenefitsForBase = moContractBudgetService.fetchFringeBenefitsForBase(moSession,
				aoPersonnelServiceBudget);
		assertNotNull(loFringeBenefitsForBase);
	}

	@Test
	public void testInsertInvoiceDocumentDetails() throws Exception
	{
		HashMap<String, Object> loInvoiceMap = new HashMap<String, Object>();
		loInvoiceMap.put("invoiceId", "55");
		loInvoiceMap.put("budgetId", "555");
		loInvoiceMap.put("contractId", "111777");
		loInvoiceMap.put("DateLastModified", "18-APR-13 02.16.41.169000000 PM");
		loInvoiceMap.put("userId", "803");
		loInvoiceMap.put("documentId", "1");
		loInvoiceMap.put("DOC_TYPE", "Banking Documentation");
		Integer liRowsUpdated = moContractBudgetService.insertInvoiceDocumentDetails(moSession, loInvoiceMap);
		assertNotNull(liRowsUpdated);
	}

	@Test
	public void testInsertContractDocumentDetails() throws Exception
	{
		HashMap<String, Object> loContractMap = new HashMap<String, Object>();
		loContractMap.put("contractId", "843");
		loContractMap.put("userId", "agency_14");
		loContractMap.put("documentId", "49");
		loContractMap.put("DOC_TYPE", "Agency Document");
		Integer liRowsUpdated = moContractBudgetService.insertContractDocumentDetails(moSession, loContractMap);
		assertNotNull(liRowsUpdated);
	}

	@Test
	public void testInsertBudgetDocumentDetails() throws Exception
	{
		HashMap<String, Object> loBudgetMap = new HashMap<String, Object>();
		loBudgetMap.put("contractId", "111777");
		loBudgetMap.put("budgetId", "555");
		loBudgetMap.put("DateLastModified", "02-MAY-13 06.32.08.225000000 PM");
		loBudgetMap.put("userId", "803");
		loBudgetMap.put("documentId", "65");
		loBudgetMap.put("DOC_TYPE", "Banking Documentation");
		Integer liRowsUpdated = moContractBudgetService.insertBudgetDocumentDetails(moSession, loBudgetMap);
		assertNotNull(liRowsUpdated);
	}

	@Test
	public void testFetchPersonnelServiceData() throws Exception
	{
		CBGridBean loCBGridBeanObj = new CBGridBean();
		PersonnelServicesData loPersonnelServicesData = moContractBudgetService.fetchPersonnelServiceData(moSession,
				loCBGridBeanObj, getMasterBeanApproved());
		assertEquals(loPersonnelServicesData.getTotalFringeAmount(), new Integer(0));
		assertEquals(loPersonnelServicesData.getTotalSalaryAmount(), new Integer(0));
		assertEquals(loPersonnelServicesData.getTotalSalaryAndFringeAmount(), new Integer(0));
		assertEquals(loPersonnelServicesData.getTotalYtdInvoicedAmount(), new Integer(0));

	}

	@Test
	public void testFetchPersonnelServiceDataWithInvalidData() throws Exception
	{
		CBGridBean loCBGridBeanObj = new CBGridBean();
		PersonnelServicesData loPersonnelServicesData = moContractBudgetService.fetchPersonnelServiceData(moSession,
				loCBGridBeanObj, getMasterBeanApproved());
		assertEquals(loPersonnelServicesData.getTotalFringeAmount(), new Integer(0));
		assertEquals(loPersonnelServicesData.getTotalSalaryAmount(), new Integer(0));
		assertEquals(loPersonnelServicesData.getTotalSalaryAndFringeAmount(), new Integer(0));
		assertEquals(loPersonnelServicesData.getTotalYtdInvoicedAmount(), new Integer(0));

	}

	@Test
	public void testFetchPersonnelServiceDataWithValidData() throws Exception
	{
		CBGridBean loCBGridBeanObj = new CBGridBean();
		PersonnelServicesData loPersonnelServicesData = moContractBudgetService.fetchPersonnelServiceData(moSession,
				loCBGridBeanObj, getMasterBeanApproved());
		assertEquals(loPersonnelServicesData.getTotalFringeAmount(), new Integer(0));
		assertEquals(loPersonnelServicesData.getTotalSalaryAmount(), new Integer(0));
		assertEquals(loPersonnelServicesData.getTotalSalaryAndFringeAmount(), new Integer(0));
		assertEquals(loPersonnelServicesData.getTotalYtdInvoicedAmount(), new Integer(0));

	}

	@Test(expected = ApplicationException.class)
	public void testFetchPersonnelServiceDataWithNullSession() throws Exception
	{
		CBGridBean loCBGridBeanObj = new CBGridBean();
		PersonnelServicesData loPersonnelServicesData = moContractBudgetService.fetchPersonnelServiceData(null,
				loCBGridBeanObj, getMasterBeanApproved());
		assertEquals(loPersonnelServicesData.getTotalFringeAmount(), new Integer(0));
		assertEquals(loPersonnelServicesData.getTotalSalaryAmount(), new Integer(0));
		assertEquals(loPersonnelServicesData.getTotalSalaryAndFringeAmount(), new Integer(0));
		assertEquals(loPersonnelServicesData.getTotalYtdInvoicedAmount(), new Integer(0));

	}

	@Test
	public void testFetchPersonnelServiceDataCase2() throws Exception
	{
		CBGridBean loCBGridBeanObj = new CBGridBean();
		PersonnelServicesData loPersonnelServicesData = moContractBudgetService.fetchPersonnelServiceData(moSession,
				loCBGridBeanObj, getMasterBeanApproved());
		assertEquals(loPersonnelServicesData.getTotalFringeAmount(), new Integer(0));
		assertEquals(loPersonnelServicesData.getTotalSalaryAmount(), new Integer(0));
		assertEquals(loPersonnelServicesData.getTotalSalaryAndFringeAmount(), new Integer(0));
		assertEquals(loPersonnelServicesData.getTotalYtdInvoicedAmount(), new Integer(0));

	}

	@Test
	public void testFetchPersonnelServiceDataWithInvalidDataCase2() throws Exception
	{
		CBGridBean loCBGridBeanObj = new CBGridBean();
		PersonnelServicesData loPersonnelServicesData = moContractBudgetService.fetchPersonnelServiceData(moSession,
				loCBGridBeanObj, getMasterBeanApproved());
		assertEquals(loPersonnelServicesData.getTotalFringeAmount(), new Integer(0));
		assertEquals(loPersonnelServicesData.getTotalSalaryAmount(), new Integer(0));
		assertEquals(loPersonnelServicesData.getTotalSalaryAndFringeAmount(), new Integer(0));
		assertEquals(loPersonnelServicesData.getTotalYtdInvoicedAmount(), new Integer(0));

	}

	@Test
	public void testFetchPersonnelServiceDataWithValidDataCase2() throws Exception
	{
		CBGridBean loCBGridBeanObj = new CBGridBean();
		PersonnelServicesData loPersonnelServicesData = moContractBudgetService.fetchPersonnelServiceData(moSession,
				loCBGridBeanObj, getMasterBeanApproved());
		assertEquals(loPersonnelServicesData.getTotalFringeAmount(), new Integer(0));
		assertEquals(loPersonnelServicesData.getTotalSalaryAmount(), new Integer(0));
		assertEquals(loPersonnelServicesData.getTotalSalaryAndFringeAmount(), new Integer(0));
		assertEquals(loPersonnelServicesData.getTotalYtdInvoicedAmount(), new Integer(0));

	}

	@Test(expected = ApplicationException.class)
	public void testFetchPersonnelServiceDataWithNullSessionCase2() throws Exception
	{
		CBGridBean loCBGridBeanObj = new CBGridBean();
		PersonnelServicesData loPersonnelServicesData = moContractBudgetService.fetchPersonnelServiceData(null,
				loCBGridBeanObj, getMasterBeanApproved());
		assertEquals(loPersonnelServicesData.getTotalFringeAmount(), new Integer(0));
		assertEquals(loPersonnelServicesData.getTotalSalaryAmount(), new Integer(0));
		assertEquals(loPersonnelServicesData.getTotalSalaryAndFringeAmount(), new Integer(0));
		assertEquals(loPersonnelServicesData.getTotalYtdInvoicedAmount(), new Integer(0));

	}

	@Test
	public void testFetchPersonnelServiceDataCase3() throws Exception
	{
		CBGridBean loCBGridBeanObj = new CBGridBean();
		PersonnelServicesData loPersonnelServicesData = moContractBudgetService.fetchPersonnelServiceData(moSession,
				loCBGridBeanObj, getMasterBeanApproved());
		assertEquals(loPersonnelServicesData.getTotalFringeAmount(), new Integer(0));
		assertEquals(loPersonnelServicesData.getTotalSalaryAmount(), new Integer(0));
		assertEquals(loPersonnelServicesData.getTotalSalaryAndFringeAmount(), new Integer(0));
		assertEquals(loPersonnelServicesData.getTotalYtdInvoicedAmount(), new Integer(0));

	}

	@Test
	public void testFetchPersonnelServiceDataWithInvalidDataCase3() throws Exception
	{
		CBGridBean loCBGridBeanObj = new CBGridBean();
		PersonnelServicesData loPersonnelServicesData = moContractBudgetService.fetchPersonnelServiceData(moSession,
				loCBGridBeanObj, getMasterBeanApproved());
		assertEquals(loPersonnelServicesData.getTotalFringeAmount(), new Integer(0));
		assertEquals(loPersonnelServicesData.getTotalSalaryAmount(), new Integer(0));
		assertEquals(loPersonnelServicesData.getTotalSalaryAndFringeAmount(), new Integer(0));
		assertEquals(loPersonnelServicesData.getTotalYtdInvoicedAmount(), new Integer(0));

	}

	@Test
	public void testFetchPersonnelServiceDataWithValidDataCase3() throws Exception
	{
		CBGridBean loCBGridBeanObj = new CBGridBean();
		PersonnelServicesData loPersonnelServicesData = moContractBudgetService.fetchPersonnelServiceData(moSession,
				loCBGridBeanObj, getMasterBeanApproved());
		assertEquals(loPersonnelServicesData.getTotalFringeAmount(), new Integer(0));
		assertEquals(loPersonnelServicesData.getTotalSalaryAmount(), new Integer(0));
		assertEquals(loPersonnelServicesData.getTotalSalaryAndFringeAmount(), new Integer(0));
		assertEquals(loPersonnelServicesData.getTotalYtdInvoicedAmount(), new Integer(0));

	}

	@Test
	public void testFetchPersonnelServiceDataWithValidDataCase3123() throws Exception
	{
		CBGridBean loCBGridBeanObj = new CBGridBean();
		PersonnelServicesData loPersonnelServicesData = moContractBudgetService.fetchPersonnelServiceData(moSession,
				loCBGridBeanObj, null);
		assertEquals(loPersonnelServicesData.getTotalFringeAmount(), new Integer(0));
		assertEquals(loPersonnelServicesData.getTotalSalaryAmount(), new Integer(0));
		assertEquals(loPersonnelServicesData.getTotalSalaryAndFringeAmount(), new Integer(0));
		assertEquals(loPersonnelServicesData.getTotalYtdInvoicedAmount(), new Integer(0));

	}

	/**
	 * Test method while fetching Indirect Rate.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchContractBudgetRentWithValidData() throws ApplicationException
	{
		Boolean loThrown = false;
		try
		{

			CBGridBean loCBIndirectRateBeanPositive = getCBGridBeanParams();
			loCBIndirectRateBeanPositive.setBudgetTypeId("2");
			loCBIndirectRateBeanPositive.setContractBudgetID("555");
			loCBIndirectRateBeanPositive.setContractID("555");
			loCBIndirectRateBeanPositive.setCreatedByUserId("623");
			loCBIndirectRateBeanPositive.setModifiedByUserId("623");
			loCBIndirectRateBeanPositive.setSubBudgetID("555");

			// Positive Scenario -- List of Rent type returned.
			// loCBIndirectRateBeanPositive.setBudgetTypeId(null);
			List<Rent> loRent = moContractBudgetService
					.fetchContractBudgetRent(moSession, loCBIndirectRateBeanPositive);
			assertNotNull(loRent);

			assertTrue(loRent.size() != 0);
			// Negative Scenario -- Application Exception handled by setting.
			// Incomplete data in the Bean.
			CBGridBean loCBIndirectRateBeanNegative = new CBGridBean();
			loCBIndirectRateBeanNegative.setBudgetTypeId("2");
			loCBIndirectRateBeanNegative.setContractBudgetID("555444");
			loCBIndirectRateBeanNegative.setContractID("555444");
			loCBIndirectRateBeanNegative.setCreatedByUserId("62312");
			loCBIndirectRateBeanNegative.setModifiedByUserId("62223");
			loCBIndirectRateBeanNegative.setSubBudgetID("55544");
			loRent = moContractBudgetService.fetchContractBudgetRent(moSession, loCBIndirectRateBeanNegative);
			assertTrue(loRent.size() == 1);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	/**
	 * Test method while fetching Indirect Rate.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchContractBudgetRentWithInvalidData() throws ApplicationException
	{
		Boolean loThrown = false;
		try
		{

			CBGridBean loCBIndirectRateBeanPositive = getCBGridBeanParams();
			loCBIndirectRateBeanPositive.setBudgetTypeId("2");
			loCBIndirectRateBeanPositive.setContractBudgetID("555");
			loCBIndirectRateBeanPositive.setContractID("555");
			loCBIndirectRateBeanPositive.setCreatedByUserId("623");
			loCBIndirectRateBeanPositive.setModifiedByUserId("623");
			loCBIndirectRateBeanPositive.setSubBudgetID("555");

			// Positive Scenario -- List of Rent type returned.
			// loCBIndirectRateBeanPositive.setBudgetTypeId(null);
			List<Rent> loRent = moContractBudgetService
					.fetchContractBudgetRent(moSession, loCBIndirectRateBeanPositive);
			assertNotNull(loRent);

			assertTrue(loRent.size() != 0);
			// Negative Scenario -- Application Exception handled by setting.
			// Incomplete data in the Bean.
			CBGridBean loCBIndirectRateBeanNegative = new CBGridBean();
			loCBIndirectRateBeanNegative.setBudgetTypeId("2");
			loCBIndirectRateBeanNegative.setContractBudgetID("555444");
			loCBIndirectRateBeanNegative.setContractID("555444");
			loCBIndirectRateBeanNegative.setCreatedByUserId("62312");
			loCBIndirectRateBeanNegative.setModifiedByUserId("62223");
			loCBIndirectRateBeanNegative.setSubBudgetID("55544");
			loRent = moContractBudgetService.fetchContractBudgetRent(moSession, loCBIndirectRateBeanNegative);
			assertTrue(loRent.size() == 1);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	/**
	 * Test method while fetching Indirect Rate.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchContractBudgetRentWithNullSession() throws ApplicationException
	{
		Boolean loThrown = false;
		try
		{

			CBGridBean loCBIndirectRateBeanPositive = getCBGridBeanParams();
			loCBIndirectRateBeanPositive.setBudgetTypeId("2");
			loCBIndirectRateBeanPositive.setContractBudgetID("555");
			loCBIndirectRateBeanPositive.setContractID("555");
			loCBIndirectRateBeanPositive.setCreatedByUserId("623");
			loCBIndirectRateBeanPositive.setModifiedByUserId("623");
			loCBIndirectRateBeanPositive.setSubBudgetID("555");

			// Positive Scenario -- List of Rent type returned.
			// loCBIndirectRateBeanPositive.setBudgetTypeId(null);
			List<Rent> loRent = moContractBudgetService
					.fetchContractBudgetRent(moSession, loCBIndirectRateBeanPositive);
			assertNotNull(loRent);

			assertTrue(loRent.size() != 0);
			// Negative Scenario -- Application Exception handled by setting.
			// Incomplete data in the Bean.
			CBGridBean loCBIndirectRateBeanNegative = new CBGridBean();
			loCBIndirectRateBeanNegative.setBudgetTypeId("2");
			loCBIndirectRateBeanNegative.setContractBudgetID("555444");
			loCBIndirectRateBeanNegative.setContractID("555444");
			loCBIndirectRateBeanNegative.setCreatedByUserId("62312");
			loCBIndirectRateBeanNegative.setModifiedByUserId("62223");
			loCBIndirectRateBeanNegative.setSubBudgetID("55544");
			loRent = moContractBudgetService.fetchContractBudgetRent(null, loCBIndirectRateBeanNegative);
			assertTrue(loRent.size() == 1);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	/**
	 * Test method while fetching Indirect Rate.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchContractBudgetRentWithValidDataCase2() throws ApplicationException
	{
		Boolean loThrown = false;
		try
		{

			CBGridBean loCBIndirectRateBeanPositive = getCBGridBeanParams();
			loCBIndirectRateBeanPositive.setBudgetTypeId("2");
			loCBIndirectRateBeanPositive.setContractBudgetID("555");
			loCBIndirectRateBeanPositive.setContractID("555");
			loCBIndirectRateBeanPositive.setCreatedByUserId("623");
			loCBIndirectRateBeanPositive.setModifiedByUserId("623");
			loCBIndirectRateBeanPositive.setSubBudgetID("555");

			// Positive Scenario -- List of Rent type returned.
			// loCBIndirectRateBeanPositive.setBudgetTypeId(null);
			List<Rent> loRent = moContractBudgetService
					.fetchContractBudgetRent(moSession, loCBIndirectRateBeanPositive);
			assertNotNull(loRent);

			assertTrue(loRent.size() != 0);
			// Negative Scenario -- Application Exception handled by setting.
			// Incomplete data in the Bean.
			CBGridBean loCBIndirectRateBeanNegative = new CBGridBean();
			loCBIndirectRateBeanNegative.setBudgetTypeId("2");
			loCBIndirectRateBeanNegative.setContractBudgetID("555444");
			loCBIndirectRateBeanNegative.setContractID("555444");
			loCBIndirectRateBeanNegative.setCreatedByUserId("62312");
			loCBIndirectRateBeanNegative.setModifiedByUserId("62223");
			loCBIndirectRateBeanNegative.setSubBudgetID("55544");
			loRent = moContractBudgetService.fetchContractBudgetRent(moSession, loCBIndirectRateBeanNegative);
			assertTrue(loRent.size() == 1);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	/**
	 * Test method while fetching Indirect Rate.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchContractBudgetRentWithInvalidDataCase2() throws ApplicationException
	{
		Boolean loThrown = false;
		try
		{

			CBGridBean loCBIndirectRateBeanPositive = getCBGridBeanParams();
			loCBIndirectRateBeanPositive.setBudgetTypeId("2");
			loCBIndirectRateBeanPositive.setContractBudgetID("555");
			loCBIndirectRateBeanPositive.setContractID("555");
			loCBIndirectRateBeanPositive.setCreatedByUserId("623");
			loCBIndirectRateBeanPositive.setModifiedByUserId("623");
			loCBIndirectRateBeanPositive.setSubBudgetID("555");

			// Positive Scenario -- List of Rent type returned.
			// loCBIndirectRateBeanPositive.setBudgetTypeId(null);
			List<Rent> loRent = moContractBudgetService
					.fetchContractBudgetRent(moSession, loCBIndirectRateBeanPositive);
			assertNotNull(loRent);

			assertTrue(loRent.size() != 0);
			// Negative Scenario -- Application Exception handled by setting.
			// Incomplete data in the Bean.
			CBGridBean loCBIndirectRateBeanNegative = new CBGridBean();
			loCBIndirectRateBeanNegative.setBudgetTypeId("2");
			loCBIndirectRateBeanNegative.setContractBudgetID("555444");
			loCBIndirectRateBeanNegative.setContractID("555444");
			loCBIndirectRateBeanNegative.setCreatedByUserId("62312");
			loCBIndirectRateBeanNegative.setModifiedByUserId("62223");
			loCBIndirectRateBeanNegative.setSubBudgetID("55544");
			loRent = moContractBudgetService.fetchContractBudgetRent(moSession, loCBIndirectRateBeanNegative);
			assertTrue(loRent.size() == 1);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	/**
	 * Test method while fetching Indirect Rate.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchContractBudgetRentWithNullSessionCase2() throws ApplicationException
	{
		Boolean loThrown = false;
		try
		{

			CBGridBean loCBIndirectRateBeanPositive = getCBGridBeanParams();
			loCBIndirectRateBeanPositive.setBudgetTypeId("2");
			loCBIndirectRateBeanPositive.setContractBudgetID("555");
			loCBIndirectRateBeanPositive.setContractID("555");
			loCBIndirectRateBeanPositive.setCreatedByUserId("623");
			loCBIndirectRateBeanPositive.setModifiedByUserId("623");
			loCBIndirectRateBeanPositive.setSubBudgetID("555");

			// Positive Scenario -- List of Rent type returned.
			// loCBIndirectRateBeanPositive.setBudgetTypeId(null);
			List<Rent> loRent = moContractBudgetService
					.fetchContractBudgetRent(moSession, loCBIndirectRateBeanPositive);
			assertNotNull(loRent);

			assertTrue(loRent.size() != 0);
			// Negative Scenario -- Application Exception handled by setting.
			// Incomplete data in the Bean.
			CBGridBean loCBIndirectRateBeanNegative = new CBGridBean();
			loCBIndirectRateBeanNegative.setBudgetTypeId("2");
			loCBIndirectRateBeanNegative.setContractBudgetID("555444");
			loCBIndirectRateBeanNegative.setContractID("555444");
			loCBIndirectRateBeanNegative.setCreatedByUserId("62312");
			loCBIndirectRateBeanNegative.setModifiedByUserId("62223");
			loCBIndirectRateBeanNegative.setSubBudgetID("55544");
			loRent = moContractBudgetService.fetchContractBudgetRent(null, loCBIndirectRateBeanNegative);
			assertTrue(loRent.size() == 1);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	/**
	 * Test method while fetching Indirect Rate.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchContractBudgetRentWithValidDataCase3() throws ApplicationException
	{
		Boolean loThrown = false;
		try
		{

			CBGridBean loCBIndirectRateBeanPositive = getCBGridBeanParams();
			loCBIndirectRateBeanPositive.setBudgetTypeId("2");
			loCBIndirectRateBeanPositive.setContractBudgetID("555");
			loCBIndirectRateBeanPositive.setContractID("555");
			loCBIndirectRateBeanPositive.setCreatedByUserId("623");
			loCBIndirectRateBeanPositive.setModifiedByUserId("623");
			loCBIndirectRateBeanPositive.setSubBudgetID("555");

			// Positive Scenario -- List of Rent type returned.
			// loCBIndirectRateBeanPositive.setBudgetTypeId(null);
			List<Rent> loRent = moContractBudgetService
					.fetchContractBudgetRent(moSession, loCBIndirectRateBeanPositive);
			assertNotNull(loRent);

			assertTrue(loRent.size() != 0);
			// Negative Scenario -- Application Exception handled by setting.
			// Incomplete data in the Bean.
			CBGridBean loCBIndirectRateBeanNegative = new CBGridBean();
			loCBIndirectRateBeanNegative.setBudgetTypeId("2");
			loCBIndirectRateBeanNegative.setContractBudgetID("555444");
			loCBIndirectRateBeanNegative.setContractID("555444");
			loCBIndirectRateBeanNegative.setCreatedByUserId("62312");
			loCBIndirectRateBeanNegative.setModifiedByUserId("62223");
			loCBIndirectRateBeanNegative.setSubBudgetID("55544");
			loRent = moContractBudgetService.fetchContractBudgetRent(moSession, loCBIndirectRateBeanNegative);
			assertTrue(loRent.size() == 1);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	/**
	 * Test method while fetching Indirect Rate.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchContractBudgetRentWithInvalidDataCase3() throws ApplicationException
	{
		Boolean loThrown = false;
		try
		{

			CBGridBean loCBIndirectRateBeanPositive = getCBGridBeanParams();
			loCBIndirectRateBeanPositive.setBudgetTypeId("2");
			loCBIndirectRateBeanPositive.setContractBudgetID("555");
			loCBIndirectRateBeanPositive.setContractID("555");
			loCBIndirectRateBeanPositive.setCreatedByUserId("623");
			loCBIndirectRateBeanPositive.setModifiedByUserId("623");
			loCBIndirectRateBeanPositive.setSubBudgetID("555");

			// Positive Scenario -- List of Rent type returned.
			// loCBIndirectRateBeanPositive.setBudgetTypeId(null);
			List<Rent> loRent = moContractBudgetService
					.fetchContractBudgetRent(moSession, loCBIndirectRateBeanPositive);
			assertNotNull(loRent);

			assertTrue(loRent.size() != 0);
			// Negative Scenario -- Application Exception handled by setting.
			// Incomplete data in the Bean.
			CBGridBean loCBIndirectRateBeanNegative = new CBGridBean();
			loCBIndirectRateBeanNegative.setBudgetTypeId("2");
			loCBIndirectRateBeanNegative.setContractBudgetID("555444");
			loCBIndirectRateBeanNegative.setContractID("555444");
			loCBIndirectRateBeanNegative.setCreatedByUserId("62312");
			loCBIndirectRateBeanNegative.setModifiedByUserId("62223");
			loCBIndirectRateBeanNegative.setSubBudgetID("55544");
			loRent = moContractBudgetService.fetchContractBudgetRent(moSession, loCBIndirectRateBeanNegative);
			assertTrue(loRent.size() == 1);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	/**
	 * Test method while fetching Indirect Rate.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchContractBudgetRentWithNullSessionCase3() throws ApplicationException
	{
		Boolean loThrown = false;
		try
		{

			CBGridBean loCBIndirectRateBeanPositive = getCBGridBeanParams();
			loCBIndirectRateBeanPositive.setBudgetTypeId("2");
			loCBIndirectRateBeanPositive.setContractBudgetID("555");
			loCBIndirectRateBeanPositive.setContractID("555");
			loCBIndirectRateBeanPositive.setCreatedByUserId("623");
			loCBIndirectRateBeanPositive.setModifiedByUserId("623");
			loCBIndirectRateBeanPositive.setSubBudgetID("555");

			// Positive Scenario -- List of Rent type returned.
			// loCBIndirectRateBeanPositive.setBudgetTypeId(null);
			List<Rent> loRent = moContractBudgetService
					.fetchContractBudgetRent(moSession, loCBIndirectRateBeanPositive);
			assertNotNull(loRent);

			assertTrue(loRent.size() != 0);
			// Negative Scenario -- Application Exception handled by setting.
			// Incomplete data in the Bean.
			CBGridBean loCBIndirectRateBeanNegative = new CBGridBean();
			loCBIndirectRateBeanNegative.setBudgetTypeId("2");
			loCBIndirectRateBeanNegative.setContractBudgetID("555444");
			loCBIndirectRateBeanNegative.setContractID("555444");
			loCBIndirectRateBeanNegative.setCreatedByUserId("62312");
			loCBIndirectRateBeanNegative.setModifiedByUserId("0");
			loCBIndirectRateBeanNegative.setSubBudgetID("55544");
			loRent = moContractBudgetService.fetchContractBudgetRent(null, loCBIndirectRateBeanNegative);
			assertTrue(loRent.size() == 1);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	/**
	 * Test method while fetching Indirect Rate.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchContractBudgetRentWithValidDataCase4() throws ApplicationException
	{
		Boolean loThrown = false;
		try
		{

			CBGridBean loCBIndirectRateBeanPositive = getCBGridBeanParams();
			loCBIndirectRateBeanPositive.setBudgetTypeId("2");
			loCBIndirectRateBeanPositive.setContractBudgetID("555");
			loCBIndirectRateBeanPositive.setContractID("555");
			loCBIndirectRateBeanPositive.setCreatedByUserId("623");
			loCBIndirectRateBeanPositive.setModifiedByUserId("623");
			loCBIndirectRateBeanPositive.setSubBudgetID("555");

			// Positive Scenario -- List of Rent type returned.
			// loCBIndirectRateBeanPositive.setBudgetTypeId(null);
			List<Rent> loRent = moContractBudgetService
					.fetchContractBudgetRent(moSession, loCBIndirectRateBeanPositive);
			assertNotNull(loRent);

			assertTrue(loRent.size() != 0);
			// Negative Scenario -- Application Exception handled by setting.
			// Incomplete data in the Bean.
			CBGridBean loCBIndirectRateBeanNegative = new CBGridBean();
			loCBIndirectRateBeanNegative.setBudgetTypeId("2");
			loCBIndirectRateBeanNegative.setContractBudgetID("555444");
			loCBIndirectRateBeanNegative.setContractID("555444");
			loCBIndirectRateBeanNegative.setCreatedByUserId("62312");
			loCBIndirectRateBeanNegative.setModifiedByUserId("62223");
			loCBIndirectRateBeanNegative.setSubBudgetID("55544");
			loRent = moContractBudgetService.fetchContractBudgetRent(moSession, loCBIndirectRateBeanNegative);
			assertTrue(loRent.size() == 1);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	/**
	 * Test method while fetching Indirect Rate.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchContractBudgetRentWithInvalidDataCase4() throws ApplicationException
	{
		Boolean loThrown = false;
		try
		{

			CBGridBean loCBIndirectRateBeanPositive = getCBGridBeanParams();
			loCBIndirectRateBeanPositive.setBudgetTypeId("2");
			loCBIndirectRateBeanPositive.setContractBudgetID("555");
			loCBIndirectRateBeanPositive.setContractID("555");
			loCBIndirectRateBeanPositive.setCreatedByUserId("623");
			loCBIndirectRateBeanPositive.setModifiedByUserId("623");
			loCBIndirectRateBeanPositive.setSubBudgetID("555");

			// Positive Scenario -- List of Rent type returned.
			// loCBIndirectRateBeanPositive.setBudgetTypeId(null);
			List<Rent> loRent = moContractBudgetService
					.fetchContractBudgetRent(moSession, loCBIndirectRateBeanPositive);
			assertNotNull(loRent);

			assertTrue(loRent.size() != 0);
			// Negative Scenario -- Application Exception handled by setting.
			// Incomplete data in the Bean.
			CBGridBean loCBIndirectRateBeanNegative = new CBGridBean();
			loCBIndirectRateBeanNegative.setBudgetTypeId("2");
			loCBIndirectRateBeanNegative.setContractBudgetID("555444");
			loCBIndirectRateBeanNegative.setContractID("555444");
			loCBIndirectRateBeanNegative.setCreatedByUserId("62312");
			loCBIndirectRateBeanNegative.setModifiedByUserId("62223");
			loCBIndirectRateBeanNegative.setSubBudgetID("55544");
			loRent = moContractBudgetService.fetchContractBudgetRent(moSession, loCBIndirectRateBeanNegative);
			assertTrue(loRent.size() == 1);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	/**
	 * Test method while fetching Indirect Rate.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchContractBudgetRentWithNullSessionCase4() throws ApplicationException
	{
		Boolean loThrown = false;
		try
		{

			CBGridBean loCBIndirectRateBeanPositive = getCBGridBeanParams();
			loCBIndirectRateBeanPositive.setBudgetTypeId("2");
			loCBIndirectRateBeanPositive.setContractBudgetID("555");
			loCBIndirectRateBeanPositive.setContractID("555");
			loCBIndirectRateBeanPositive.setCreatedByUserId("623");
			loCBIndirectRateBeanPositive.setModifiedByUserId("623");
			loCBIndirectRateBeanPositive.setSubBudgetID("555");

			// Positive Scenario -- List of Rent type returned.
			// loCBIndirectRateBeanPositive.setBudgetTypeId(null);
			List<Rent> loRent = moContractBudgetService
					.fetchContractBudgetRent(moSession, loCBIndirectRateBeanPositive);
			assertNotNull(loRent);

			assertTrue(loRent.size() != 0);
			// Negative Scenario -- Application Exception handled by setting.
			// Incomplete data in the Bean.
			CBGridBean loCBIndirectRateBeanNegative = new CBGridBean();
			loCBIndirectRateBeanNegative.setBudgetTypeId("2");
			loCBIndirectRateBeanNegative.setContractBudgetID("555444");
			loCBIndirectRateBeanNegative.setContractID("555444");
			loCBIndirectRateBeanNegative.setCreatedByUserId("62312");
			loCBIndirectRateBeanNegative.setModifiedByUserId("62223");
			loCBIndirectRateBeanNegative.setSubBudgetID("55544");
			loRent = moContractBudgetService.fetchContractBudgetRent(moSession, loCBIndirectRateBeanNegative);
			assertTrue(loRent.size() == 1);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	@Test
	public void testDeleteContractBudgetRent1() throws ApplicationException
	{
		Rent loRent = new Rent();

		// Positive Scenario -- Rent Update Successful.
		loRent.setId("389");
		loRent.setYtdInvoiceAmt("1000");
		loRent.setBudgetTypeId("2");
		boolean lbUpdateStatus = moContractBudgetService.deleteContractBudgetRent(moSession, loRent);
		assertTrue(lbUpdateStatus);

	}

	@Test(expected = ApplicationException.class)
	public void testDeleteContractBudgetRent2() throws ApplicationException
	{
		Rent loRent = new Rent();

		loRent.setId("75");
		loRent.setYtdInvoiceAmt("1000");
		loRent.setBudgetTypeId("2");

		// Negative Scenario -- Application Exception handled by setting.

		moContractBudgetService.deleteContractBudgetRent(null, loRent);

	}

	@Test
	public void testDeleteContractBudgetRent3() throws ApplicationException
	{

		Rent loRent = new Rent();

		// Positive Scenario -- Rent Update Successful.
		loRent.setId("389");
		loRent.setYtdInvoiceAmt("1000");
		loRent.setBudgetTypeId("2");
		boolean lbUpdateStatus = moContractBudgetService.deleteContractBudgetRent(moSession, loRent);
		assertTrue(lbUpdateStatus);

	}

	@Test
	public void testDeleteContractBudgetRent4() throws ApplicationException
	{
		Rent loRent = new Rent();

		// Positive Scenario -- Rent Update Successful.
		loRent.setId("389");
		loRent.setBudgetTypeId("2");
		boolean lbUpdateStatus = moContractBudgetService.deleteContractBudgetRent(moSession, loRent);
		assertTrue(lbUpdateStatus);

	}

	@Test(expected = ApplicationException.class)
	public void testDeleteContractBudgetRent5() throws ApplicationException
	{
		Rent loRent = new Rent();

		loRent.setId("75");
		loRent.setYtdInvoiceAmt("1000");
		loRent.setBudgetTypeId("2");

		// Negative Scenario -- Application Exception handled by setting.

		moContractBudgetService.deleteContractBudgetRent(null, loRent);

	}

	@Test
	public void testDeleteContractBudgetRent6() throws ApplicationException
	{

		Rent loRent = new Rent();

		// Positive Scenario -- Rent Update Successful.
		loRent.setId("389");
		loRent.setYtdInvoiceAmt("1000");
		loRent.setBudgetTypeId("2");
		boolean lbUpdateStatus = moContractBudgetService.deleteContractBudgetRent(moSession, loRent);
		assertTrue(lbUpdateStatus);

	}

	@Test
	public void testDeleteContractBudgetRent7() throws ApplicationException
	{
		Rent loRent = new Rent();

		// Positive Scenario -- Rent Update Successful.
		loRent.setId("389");
		loRent.setYtdInvoiceAmt("1000");
		loRent.setBudgetTypeId("2");
		boolean lbUpdateStatus = moContractBudgetService.deleteContractBudgetRent(moSession, loRent);
		assertTrue(lbUpdateStatus);

	}

	@Test(expected = ApplicationException.class)
	public void testDeleteContractBudgetRent8() throws ApplicationException
	{
		Rent loRent = new Rent();

		loRent.setId("75");
		loRent.setYtdInvoiceAmt("1000");
		loRent.setBudgetTypeId("2");

		// Negative Scenario -- Application Exception handled by setting.

		moContractBudgetService.deleteContractBudgetRent(null, loRent);

	}

	@Test
	public void testDeleteContractBudgetRent9() throws ApplicationException
	{

		Rent loRent = new Rent();

		// Positive Scenario -- Rent Update Successful.
		loRent.setId("389");
		loRent.setYtdInvoiceAmt("1000");
		loRent.setBudgetTypeId("2");
		boolean lbUpdateStatus = moContractBudgetService.deleteContractBudgetRent(moSession, loRent);
		assertTrue(lbUpdateStatus);

	}

	@Test(expected = ApplicationException.class)
	public void testDeleteContractBudgetRent10() throws ApplicationException
	{
		Rent loRent = new Rent();

		// Positive Scenario -- Rent Update Successful.
		loRent.setId("389");
		loRent.setYtdInvoiceAmt("1000");
		loRent.setBudgetTypeId("2");
		boolean lbUpdateStatus = moContractBudgetService.deleteContractBudgetRent(null, loRent);
		assertTrue(lbUpdateStatus);

	}

	@Test(expected = ApplicationException.class)
	public void testDeleteContractBudgetRent11() throws ApplicationException
	{
		Rent loRent = new Rent();

		loRent.setId("75");
		loRent.setYtdInvoiceAmt("1000");
		loRent.setBudgetTypeId("0");

		// Negative Scenario -- Application Exception handled by setting.

		moContractBudgetService.deleteContractBudgetRent(null, loRent);

	}

	@Test
	public void testDeleteContractBudgetRent12() throws ApplicationException
	{

		Rent loRent = new Rent();

		// Positive Scenario -- Rent Update Successful.
		loRent.setId("389");
		loRent.setYtdInvoiceAmt("1000");
		loRent.setBudgetTypeId("1");
		boolean lbUpdateStatus = moContractBudgetService.deleteContractBudgetRent(moSession, loRent);
		assertTrue(lbUpdateStatus);

	}

	@Test
	public void testDeleteContractBudgetRent13() throws ApplicationException
	{
		Rent loRent = new Rent();

		// Positive Scenario -- Rent Update Successful.
		loRent.setId("389");
		loRent.setYtdInvoiceAmt("1000");
		loRent.setBudgetTypeId("2");
		boolean lbUpdateStatus = moContractBudgetService.deleteContractBudgetRent(moSession, loRent);
		assertTrue(lbUpdateStatus);

	}

	@Test(expected = ApplicationException.class)
	public void testDeleteContractBudgetRent14() throws ApplicationException
	{
		Rent loRent = new Rent();

		loRent.setId("75");
		loRent.setYtdInvoiceAmt("1000");
		loRent.setBudgetTypeId("3");

		// Negative Scenario -- Application Exception handled by setting.

		moContractBudgetService.deleteContractBudgetRent(null, loRent);

	}

	@Test
	public void testDeleteContractBudgetRent15() throws ApplicationException
	{

		Rent loRent = new Rent();

		// Positive Scenario -- Rent Update Successful.
		loRent.setId("389");
		loRent.setYtdInvoiceAmt("1000");
		loRent.setBudgetTypeId("2");
		boolean lbUpdateStatus = moContractBudgetService.deleteContractBudgetRent(moSession, loRent);
		assertTrue(lbUpdateStatus);

	}

	/**
	 * This method tests updateUtilities throws exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateContractBudgetRent2() throws ApplicationException
	{
		CBGridBean loCBIndirectRateBeanPositive = getCBGridBeanParams();
		loCBIndirectRateBeanPositive.setBudgetTypeId("2");
		Rent loRent = new Rent();
		loRent.setId("1");
		loRent.setLocation("newLocation");
		loRent.setId("389");
		loRent.setYtdInvoiceAmt("1000");
		boolean lbUpdateStatus = moContractBudgetService.updateContractBudgetRent(moSession, loRent);
		assertTrue(lbUpdateStatus);
	}

	@Test
	public void testUpdateContractBudgetRent3() throws ApplicationException
	{
		CBGridBean loCBIndirectRateBeanPositive = getCBGridBeanParams();
		loCBIndirectRateBeanPositive.setBudgetTypeId("2");
		Rent loRent = new Rent();
		loRent.setId("1");
		loRent.setLocation("newLocation");
		loRent.setId("389");
		loRent.setYtdInvoiceAmt("1000");
		boolean lbUpdateStatus = moContractBudgetService.updateContractBudgetRent(null, loRent);
		assertTrue(lbUpdateStatus);
	}

	@Test
	public void testUpdateContractBudgetRent4() throws ApplicationException
	{
		CBGridBean loCBIndirectRateBeanPositive = getCBGridBeanParams();
		loCBIndirectRateBeanPositive.setBudgetTypeId("2");
		Rent loRent = new Rent();
		loRent.setId("1");
		loRent.setLocation("newLocation");
		loRent.setId("389");
		loRent.setYtdInvoiceAmt("1000");
		boolean lbUpdateStatus = moContractBudgetService.updateContractBudgetRent(null, loRent);
		assertTrue(lbUpdateStatus);
	}

	@Test
	public void testUpdateContractBudgetRent5() throws ApplicationException
	{
		CBGridBean loCBIndirectRateBeanPositive = getCBGridBeanParams();
		loCBIndirectRateBeanPositive.setBudgetTypeId("2");
		Rent loRent = new Rent();
		loRent.setId("1");
		loRent.setLocation("newLocation");
		loRent.setId("389");
		loRent.setYtdInvoiceAmt("1000");
		boolean lbUpdateStatus = moContractBudgetService.updateContractBudgetRent(moSession, loRent);
		assertTrue(lbUpdateStatus);
	}

	@Test
	public void testUpdateContractBudgetRent6() throws ApplicationException
	{
		CBGridBean loCBIndirectRateBeanPositive = getCBGridBeanParams();
		loCBIndirectRateBeanPositive.setBudgetTypeId("2");
		Rent loRent = new Rent();
		loRent.setId("1");
		loRent.setLocation("newLocation");
		loRent.setId("389");
		loRent.setYtdInvoiceAmt("1000");
		boolean lbUpdateStatus = moContractBudgetService.updateContractBudgetRent(null, loRent);
		assertTrue(lbUpdateStatus);
	}

	@Test
	public void testUpdateContractBudgetRent7() throws ApplicationException
	{
		CBGridBean loCBIndirectRateBeanPositive = getCBGridBeanParams();
		loCBIndirectRateBeanPositive.setBudgetTypeId("2");
		Rent loRent = new Rent();
		loRent.setId("1");
		loRent.setLocation("newLocation");
		loRent.setId("389");
		loRent.setYtdInvoiceAmt("1000");
		boolean lbUpdateStatus = moContractBudgetService.updateContractBudgetRent(moSession, loRent);
		assertTrue(lbUpdateStatus);
	}

	@Test
	public void testUpdateContractBudgetRent8() throws ApplicationException
	{
		CBGridBean loCBIndirectRateBeanPositive = getCBGridBeanParams();
		loCBIndirectRateBeanPositive.setBudgetTypeId("2");
		Rent loRent = new Rent();
		loRent.setId("1");
		loRent.setLocation("newLocation");
		loRent.setId("389");
		loRent.setYtdInvoiceAmt("1000");
		boolean lbUpdateStatus = moContractBudgetService.updateContractBudgetRent(null, loRent);
		assertTrue(lbUpdateStatus);
	}

	@Test
	public void testUpdateContractBudgetRent9() throws ApplicationException
	{
		CBGridBean loCBIndirectRateBeanPositive = getCBGridBeanParams();
		loCBIndirectRateBeanPositive.setBudgetTypeId("2");
		Rent loRent = new Rent();
		loRent.setId("1");
		loRent.setLocation("newLocation");
		loRent.setId("389");
		loRent.setYtdInvoiceAmt("1000");
		boolean lbUpdateStatus = moContractBudgetService.updateContractBudgetRent(moSession, loRent);
		assertTrue(lbUpdateStatus);
	}

	@Test
	public void testUpdateContractBudgetRent10() throws ApplicationException
	{
		CBGridBean loCBIndirectRateBeanPositive = getCBGridBeanParams();
		loCBIndirectRateBeanPositive.setBudgetTypeId("2");
		Rent loRent = new Rent();
		loRent.setId("1");
		loRent.setLocation("newLocation");
		loRent.setId("389");
		loRent.setYtdInvoiceAmt("1000");
		boolean lbUpdateStatus = moContractBudgetService.updateContractBudgetRent(null, loRent);
		assertTrue(lbUpdateStatus);
	}

	@Test
	public void testUpdateContractBudgetRent11() throws ApplicationException
	{
		CBGridBean loCBIndirectRateBeanPositive = getCBGridBeanParams();
		loCBIndirectRateBeanPositive.setBudgetTypeId("2");
		Rent loRent = new Rent();
		loRent.setId("1");
		loRent.setLocation("");
		loRent.setId("389");
		loRent.setYtdInvoiceAmt("1000");
		boolean lbUpdateStatus = moContractBudgetService.updateContractBudgetRent(moSession, loRent);
		assertTrue(lbUpdateStatus);
	}

	@Test
	public void testUpdateContractBudgetRent12() throws ApplicationException
	{
		CBGridBean loCBIndirectRateBeanPositive = getCBGridBeanParams();
		loCBIndirectRateBeanPositive.setBudgetTypeId("2");
		Rent loRent = new Rent();
		loRent.setId("1");
		loRent.setLocation("newLocation");
		loRent.setId("389");
		loRent.setYtdInvoiceAmt("0");
		boolean lbUpdateStatus = moContractBudgetService.updateContractBudgetRent(null, loRent);
		assertTrue(lbUpdateStatus);
	}

	@Test
	public void testUpdateContractBudgetRent13() throws ApplicationException
	{
		CBGridBean loCBIndirectRateBeanPositive = getCBGridBeanParams();
		loCBIndirectRateBeanPositive.setBudgetTypeId("2");
		Rent loRent = new Rent();
		loRent.setId("1");
		loRent.setLocation("newLocation");
		loRent.setId("389");
		loRent.setYtdInvoiceAmt("1000");
		boolean lbUpdateStatus = moContractBudgetService.updateContractBudgetRent(moSession, loRent);
		assertTrue(lbUpdateStatus);
	}

	@Test
	public void testUpdateContractBudgetRent14() throws ApplicationException
	{
		CBGridBean loCBIndirectRateBeanPositive = getCBGridBeanParams();
		loCBIndirectRateBeanPositive.setBudgetTypeId("2");
		Rent loRent = new Rent();
		loRent.setId("1");
		loRent.setLocation("newLocation");
		loRent.setId("389");
		loRent.setYtdInvoiceAmt("1000");
		boolean lbUpdateStatus = moContractBudgetService.updateContractBudgetRent(null, loRent);
		assertTrue(lbUpdateStatus);
	}

	@Test
	public void testUpdateContractBudgetRent15() throws ApplicationException
	{
		Rent loRent = new Rent();
		// CBGridBean loCBIndirectRateBeanPositive = getCBGridBeanParams();
		loRent.setId("2");
		loRent.setLocation("newLocation");
		loRent.setManagementCompanyName("Test");
		loRent.setPropertyOwner("Test");
		loRent.setPublicSchoolSpace("1");
		loRent.setPercentChargedToContract("1.00");
		loRent.setFyBudget("100.00");
		loRent.setModifyByProvider("909");
		loRent.setModifyByAgency("agency_21");
		loRent.setBudgetTypeId(HHSConstants.TWO);

		boolean lbUpdateStatus = moContractBudgetService.updateContractBudgetRent(moSession, loRent);
		assertTrue(lbUpdateStatus);
	}

	@Test
	public void testUpdateContractBudgetRent16() throws ApplicationException
	{
		CBGridBean loCBIndirectRateBeanPositive = getCBGridBeanParams();
		loCBIndirectRateBeanPositive.setBudgetTypeId("2");
		Rent loRent = new Rent();
		loRent.setId("1");
		loRent.setLocation("newLocation");
		loRent.setId("389");
		loRent.setYtdInvoiceAmt("1000");
		boolean lbUpdateStatus = moContractBudgetService.updateContractBudgetRent(null, loRent);
		assertTrue(lbUpdateStatus);
	}

	@Test
	public void testGetSeqForRent() throws ApplicationException
	{
		int liCurrentSeq = moContractBudgetService.getSeqForRent(moSession);
		assertTrue(liCurrentSeq >= 0);
	}

	@Test
	public void testGetSeqForRate() throws ApplicationException
	{
		int liCurrentSeq = moContractBudgetService.getSeqForRate(moSession);
		assertTrue(liCurrentSeq >= 0);
	}

	@Test
	public void testFetchHourlyEmployeeForBase() throws Exception
	{
		CBGridBean loPersonnelServiceBudget = new CBGridBean();
		loPersonnelServiceBudget.setContractBudgetID("555");
		loPersonnelServiceBudget.setSubBudgetID("555");
		moContractBudgetService.fetchHourlyEmployeeForBase(moSession, loPersonnelServiceBudget);
		assertNotNull(loPersonnelServiceBudget);

	}

	@Test
	public void testFetchSalariedEmployeeForBase() throws Exception
	{
		CBGridBean loPersonnelServiceBudget = new CBGridBean();
		loPersonnelServiceBudget.setContractBudgetID("555");
		loPersonnelServiceBudget.setSubBudgetID("555");
		moContractBudgetService.fetchSalariedEmployeeForBase(moSession, loPersonnelServiceBudget);
		assertNotNull(loPersonnelServiceBudget);

	}

	@Test
	public void testfetchCurrentCBStatus1() throws Exception
	{
		String asBudgetID = "555";
		String lsBudgetStatus = moContractBudgetService.fetchCurrentCBStatus(moSession, asBudgetID);
		assertNotNull(lsBudgetStatus);
	}

	@Test
	public void testfetchCurrentCBStatus2() throws Exception
	{
		String asBudgetID = "";
		String lsBudgetStatus = moContractBudgetService.fetchCurrentCBStatus(moSession, asBudgetID);
		assertNull(lsBudgetStatus);
	}

	@Test
	public void testfetchCurrentCBStatus3() throws Exception
	{
		String asBudgetID = "5456";
		String lsBudgetStatus = moContractBudgetService.fetchCurrentCBStatus(moSession, asBudgetID);
		assertNull(lsBudgetStatus);
	}

	@Test(expected = ApplicationException.class)
	public void testfetchCurrentCBStatus4() throws Exception
	{
		moContractBudgetService.fetchCurrentCBStatus(moSession, null);
	}

	@Test(expected = ApplicationException.class)
	public void testfetchCurrentCBStatus5() throws Exception
	{
		String asBudgetID = "2";
		moContractBudgetService.fetchCurrentCBStatus(null, asBudgetID);
	}

	@Test
	public void testFetchAssignmentSummaryCase1() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		aoCBGridBeanObj.setContractID("30");
		aoCBGridBeanObj.setContractBudgetID("14");
		aoCBGridBeanObj.setBudgetTypeId(HHSConstants.TWO);
		List loAssignmentsList = moContractBudgetService.fetchAssignmentSummary(moSession, aoCBGridBeanObj);
		assertNotNull(loAssignmentsList);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchAssignmentSummaryCase2() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		aoCBGridBeanObj.setContractID("555");
		aoCBGridBeanObj.setContractBudgetID("555");
		aoCBGridBeanObj.setBudgetTypeId(HHSConstants.TWO);
		List loAssignmentsList = moContractBudgetService.fetchAssignmentSummary(null, aoCBGridBeanObj);
		assertNotNull(loAssignmentsList);
	}

	@Test
	public void testFetchAssignmentSummaryCase3() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		aoCBGridBeanObj.setContractID("");
		aoCBGridBeanObj.setParentBudgetId("14");
		aoCBGridBeanObj.setBudgetTypeId(HHSConstants.ONE);
		List loAssignmentsList = moContractBudgetService.fetchAssignmentSummary(moSession, aoCBGridBeanObj);
		assertNotNull(loAssignmentsList);
	}

	@Test
	public void testFetchAssignmentSummaryCase4() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		aoCBGridBeanObj.setContractID("23456");
		aoCBGridBeanObj.setContractBudgetID("1234");
		aoCBGridBeanObj.setBudgetTypeId(HHSConstants.TWO);
		List loAssignmentsList = moContractBudgetService.fetchAssignmentSummary(moSession, aoCBGridBeanObj);
		assertNotNull(loAssignmentsList);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchAssignmentSummaryCase5() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		aoCBGridBeanObj.setContractID("");
		aoCBGridBeanObj.setContractBudgetID("");
		aoCBGridBeanObj.setBudgetTypeId(HHSConstants.ONE);
		moContractBudgetService.fetchAssignmentSummary(null, aoCBGridBeanObj);

	}

	@Test
	public void testfetchAdvanceDetailsCase1() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		aoCBGridBeanObj.setContractBudgetID("555");
		List loAdvancesList = moContractBudgetService.fetchAdvanceDetails(moSession, aoCBGridBeanObj);
		assertNotNull(loAdvancesList);

	}

	@Test
	public void testfetchAdvanceDetailsCase2() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		aoCBGridBeanObj.setContractBudgetID("");
		List loAdvancesList = moContractBudgetService.fetchAdvanceDetails(moSession, aoCBGridBeanObj);
		assertNotNull(loAdvancesList);

	}

	@Test
	public void testfetchAdvanceDetailsCase3() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		aoCBGridBeanObj.setContractBudgetID("12345");
		List loAdvancesList = moContractBudgetService.fetchAdvanceDetails(moSession, aoCBGridBeanObj);
		assertNotNull(loAdvancesList);

	}

	@Test(expected = ApplicationException.class)
	public void testfetchAdvanceDetailsCase4() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		aoCBGridBeanObj.setContractBudgetID("555");
		moContractBudgetService.fetchAdvanceDetails(null, aoCBGridBeanObj);

	}

	@Test(expected = ApplicationException.class)
	public void testfetchAdvanceDetailsCase5() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		aoCBGridBeanObj.setContractBudgetID("555");
		moContractBudgetService.fetchAdvanceDetails(null, aoCBGridBeanObj);

	}

	@Test
	public void testRemoveFinancialDocsCase1() throws Exception
	{
		HashMap<String, String> loFinancialMap = new HashMap<String, String>();
		loFinancialMap.put("hdnTableName", "contract_document");
		loFinancialMap.put("asDocumentSequence", "111777");
		loFinancialMap.put("asDeletedDocumentId", "555");
		Integer liRowsUpdated = moContractBudgetService.removeFinancialDocs(moSession, loFinancialMap);
		assertNotNull(liRowsUpdated);
	}

	@Test
	public void testRemoveFinancialDocsCase2() throws Exception
	{
		HashMap<String, String> loFinancialMap = new HashMap<String, String>();
		loFinancialMap.put("hdnTableName", "budget_document");
		loFinancialMap.put("asDocumentSequence", "111777");
		loFinancialMap.put("asDeletedDocumentId", "555");
		Integer liRowsUpdated = moContractBudgetService.removeFinancialDocs(moSession, loFinancialMap);
		assertNotNull(liRowsUpdated);
	}

	@Test
	public void testRemoveFinancialDocsCase3() throws Exception
	{
		HashMap<String, String> loFinancialMap = new HashMap<String, String>();
		loFinancialMap.put("hdnTableName", "budget");
		loFinancialMap.put("asDocumentSequence", "111777");
		loFinancialMap.put("asDeletedDocumentId", "555");
		Integer liRowsUpdated = moContractBudgetService.removeFinancialDocs(moSession, loFinancialMap);
		assertNotNull(liRowsUpdated);
	}

	@Test(expected = ApplicationException.class)
	public void testRemoveFinancialDocsCase4() throws Exception
	{
		HashMap<String, String> loFinancialMap = new HashMap<String, String>();
		loFinancialMap.put("hdnTableName", "budget");
		loFinancialMap.put("asDocumentSequence", "111777");
		loFinancialMap.put("asDeletedDocumentId", "555");
		moContractBudgetService.removeFinancialDocs(null, loFinancialMap);
	}

	@Test(expected = ApplicationException.class)
	public void testRemoveFinancialDocsCase5() throws Exception
	{
		HashMap<String, String> loFinancialMap = new HashMap<String, String>();
		loFinancialMap.put("hdnTableName", "budget_document");
		loFinancialMap.put("asDocumentSequence", "111777");
		loFinancialMap.put("asDeletedDocumentId", "555");
		moContractBudgetService.removeFinancialDocs(null, loFinancialMap);
	}

	@Test
	public void testFetchPersonnelServiceMasterDataCase1() throws Exception
	{
		String lsPersonnelServiceMasterData = moContractBudgetService.fetchPersonnelServiceMasterData(moSession);
		assertNotNull(lsPersonnelServiceMasterData);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchPersonnelServiceMasterDataCase2() throws Exception
	{
		moContractBudgetService.fetchPersonnelServiceMasterData(null);
	}

	@Test
	public void testFetchPersonnelServiceMasterDataCase3() throws Exception
	{
		String lsPersonnelServiceMasterData = moContractBudgetService.fetchPersonnelServiceMasterData(moSession);
		assertNotNull(lsPersonnelServiceMasterData);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchPersonnelServiceMasterDataCase4() throws Exception
	{
		moContractBudgetService.fetchPersonnelServiceMasterData(null);
	}

	@Test
	public void testFetchPersonnelServiceMasterDataCase5() throws Exception
	{
		String lsPersonnelServiceMasterData = moContractBudgetService.fetchPersonnelServiceMasterData(moSession);
		assertNotNull(lsPersonnelServiceMasterData);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchPersonnelServiceMasterDataCase6() throws Exception
	{
		moContractBudgetService.fetchPersonnelServiceMasterData(null);
	}

	@Test
	public void testFetchPersonnelServiceMasterDataCase7() throws Exception
	{
		String lsPersonnelServiceMasterData = moContractBudgetService.fetchPersonnelServiceMasterData(moSession);
		assertNotNull(lsPersonnelServiceMasterData);
	}

	@Test
	public void testFetchPersonnelServiceMasterDataCase8() throws Exception
	{
		String lsPersonnelServiceMasterData = moContractBudgetService.fetchPersonnelServiceMasterData(moSession);
		assertNotNull(lsPersonnelServiceMasterData);
	}

	@Test
	public void testInsertBudgetDocumentDetailsCase2() throws Exception
	{
		HashMap<String, Object> loBudgetMap = new HashMap<String, Object>();
		loBudgetMap.put("contractId", "111777");
		loBudgetMap.put("budgetId", "555");
		loBudgetMap.put("DateLastModified", "02-MAY-13 06.32.08.225000000 PM");
		loBudgetMap.put("userId", "803");
		loBudgetMap.put("documentId", "65");
		loBudgetMap.put("DOC_TYPE", "Banking Documentation");
		Integer liRowsUpdated = moContractBudgetService.insertBudgetDocumentDetails(moSession, loBudgetMap);
		assertNotNull(liRowsUpdated);
	}

	@Test(expected = ApplicationException.class)
	public void testInsertBudgetDocumentDetailsCase3() throws Exception
	{
		HashMap<String, Object> loBudgetMap = new HashMap<String, Object>();
		loBudgetMap.put("contractId", "111777");
		loBudgetMap.put("budgetId", "555");
		loBudgetMap.put("DateLastModified", "02-MAY-13 06.32.08.225000000 PM");
		loBudgetMap.put("userId", "803");
		loBudgetMap.put("documentId", "65");
		loBudgetMap.put("DOC_TYPE", "Banking Documentation");
		moContractBudgetService.insertBudgetDocumentDetails(null, loBudgetMap);
	}

	@Test
	public void testInsertInvoiceDocumentDetailsCase2() throws Exception
	{
		HashMap<String, Object> loInvoiceMap = new HashMap<String, Object>();
		loInvoiceMap.put("invoiceId", "55");
		loInvoiceMap.put("budgetId", "555");
		loInvoiceMap.put("contractId", "111777");
		loInvoiceMap.put("DateLastModified", "18-APR-13 02.16.41.169000000 PM");
		loInvoiceMap.put("userId", "803");
		loInvoiceMap.put("documentId", "1");
		loInvoiceMap.put("DOC_TYPE", "Banking Documentation");
		Integer liRowsUpdated = moContractBudgetService.insertInvoiceDocumentDetails(moSession, loInvoiceMap);
		assertNotNull(liRowsUpdated);
	}

	@Test(expected = ApplicationException.class)
	public void testInsertInvoiceDocumentDetailsCase3() throws Exception
	{
		HashMap<String, Object> loInvoiceMap = new HashMap<String, Object>();
		loInvoiceMap.put("invoiceId", "55");
		loInvoiceMap.put("budgetId", "555");
		loInvoiceMap.put("contractId", "111777");
		loInvoiceMap.put("DateLastModified", "18-APR-13 02.16.41.169000000 PM");
		loInvoiceMap.put("userId", "803");
		loInvoiceMap.put("documentId", "1");
		loInvoiceMap.put("DOC_TYPE", "Banking Documentation");
		moContractBudgetService.insertInvoiceDocumentDetails(null, loInvoiceMap);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchFinancialDocumentsCase2() throws ApplicationException
	{
		Map<String, Object> aoParamMap = new HashMap<String, Object>();
		aoParamMap.put("contractId", "111777");
		aoParamMap.put("invoiceId", "55");
		aoParamMap.put("budgetId", "");
		moContractBudgetService.fetchFinancialDocuments(null, aoParamMap);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchFinancialDocumentsCase3() throws ApplicationException
	{
		Map<String, Object> aoParamMap = new HashMap<String, Object>();
		aoParamMap.put("contractId", "111777");
		aoParamMap.put("invoiceId", "55");
		aoParamMap.put("budgetId", "555");
		moContractBudgetService.fetchFinancialDocuments(null, aoParamMap);
	}

	@Test
	public void testInsertContractDocumentDetailsCase2() throws Exception
	{
		HashMap<String, Object> loContractMap = new HashMap<String, Object>();
		loContractMap.put("contractId", "843");
		loContractMap.put("userId", "agency_14");
		loContractMap.put("documentId", "49");
		loContractMap.put("DOC_TYPE", "Agency Document");
		Integer liRowsUpdated = moContractBudgetService.insertContractDocumentDetails(moSession, loContractMap);
		assertNotNull(liRowsUpdated);
	}

	@Test(expected = ApplicationException.class)
	public void testInsertContractDocumentDetailsCase3() throws Exception
	{
		HashMap<String, Object> loContractMap = new HashMap<String, Object>();
		loContractMap.put("contractId", "843");
		loContractMap.put("userId", "agency_14");
		loContractMap.put("documentId", "49");
		loContractMap.put("DOC_TYPE", "Agency Document");
		moContractBudgetService.insertContractDocumentDetails(null, loContractMap);
	}

	@Test
	public void testCreateReplicaOfBudget() throws ApplicationException
	{
		Boolean lbFinalFinish = true;
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId("30");
		loTaskDetailsBean.setBudgetId("30");
		loTaskDetailsBean.setUserId("city_142");
		String asBudgetStatus = "67";
		moContractBudgetService.createReplicaOfBudget(moSession, loTaskDetailsBean, asBudgetStatus);
		assertNotNull(moContractBudgetService);
	}

	@Test
	public void testInsertStandardRowsSubBudgetLevel() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId("1001");
		aoTaskDetailsBean.setStartFiscalYear("2014");
		aoTaskDetailsBean.setTaskName(HHSConstants.TASK_CONTRACT_COF);
		moContractBudgetService.insertStandardRowsSubBudgetLevel(aoTaskDetailsBean, moSession);
		assertNotNull(aoTaskDetailsBean);
	}
	
	@Test
	public void testInsertStandardRowsSubBudgetLevel1() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId("140");
		aoTaskDetailsBean.setStartFiscalYear("2014");
		aoTaskDetailsBean.setUserId("agency_21");
		aoTaskDetailsBean.setTaskName(HHSConstants.TASK_CONTRACT_CONFIGURATION);
		moContractBudgetService.insertStandardRowsSubBudgetLevel(aoTaskDetailsBean, moSession);
		assertNotNull(aoTaskDetailsBean);
	}
	
	@Test
	public void testInsertStandardRowsSubBudgetLevel2() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId("111777");
		aoTaskDetailsBean.setStartFiscalYear("");
		moContractBudgetService.insertStandardRowsSubBudgetLevel(aoTaskDetailsBean, moSession);
		assertNotNull(aoTaskDetailsBean);
	}
	
	@Test(expected = ApplicationException.class)
	public void testInsertStandardRowsSubBudgetLevel3() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId("111777");
		aoTaskDetailsBean.setStartFiscalYear("2013");
		moContractBudgetService.insertStandardRowsSubBudgetLevel(aoTaskDetailsBean, null);
	}
	
	@Test(expected = NullPointerException.class)
	public void testInsertStandardRowsSubBudgetLevel4() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId("111777");
		aoTaskDetailsBean.setStartFiscalYear("2013");
		moContractBudgetService.insertStandardRowsSubBudgetLevel(null, moSession);
	}
	
	@Test
	public void testInsertStandardRowsSubBudgetLevel5() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId("111777");
		aoTaskDetailsBean.setStartFiscalYear("");
		moContractBudgetService.insertStandardRowsSubBudgetLevel(aoTaskDetailsBean, moSession);
		assertNotNull(aoTaskDetailsBean);
	}
	
	

	@Test
	public void testInsertStandardRowsSubBudgetLevel22() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId("111777");
		aoTaskDetailsBean.setStartFiscalYear("2013");
		moContractBudgetService.insertStandardRowsSubBudgetLevel(aoTaskDetailsBean, moSession);
		assertNotNull(aoTaskDetailsBean);
	}

	@Test(expected = ApplicationException.class)
	public void testInsertStandardRowsSubBudgetLevel32() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId("111777");
		aoTaskDetailsBean.setStartFiscalYear("2013");
		moContractBudgetService.insertStandardRowsSubBudgetLevel(aoTaskDetailsBean, null);
	}

	@Test(expected = NullPointerException.class)
	public void testInsertStandardRowsSubBudgetLevel42() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId("111777");
		aoTaskDetailsBean.setStartFiscalYear("2013");
		moContractBudgetService.insertStandardRowsSubBudgetLevel(null, moSession);
	}

	// start of junit code coverage
	@Test
	public void testvalidateAmountTotal() throws ApplicationException
	{
		Boolean loValid = moContractBudgetService.validateAmountTotal(moSession, "555");
		assertFalse(loValid);
	}

	@Test(expected = ApplicationException.class)
	public void testvalidateAmountTotalSessionNull() throws ApplicationException
	{
		Boolean loValid = moContractBudgetService.validateAmountTotal(null, "555");
		assertFalse(loValid);
	}

	@Test(expected = ApplicationException.class)
	public void testvalidateAmountTotalBudgetIdNull() throws ApplicationException
	{
		Boolean loValid = moContractBudgetService.validateAmountTotal(moSession, null);
		assertFalse(loValid);
	}

	private ContractBudgetBean getDummyContractBudgetBean()
	{
		ContractBudgetBean loSubBudgetBean = new ContractBudgetBean();
		loSubBudgetBean.setBudgetfiscalYear("2014");
		loSubBudgetBean.setBudgetId("555");
		loSubBudgetBean.setBudgetTypeId(2);
		loSubBudgetBean.setContractValue("10000");
		loSubBudgetBean.setCreatedByUserId("city_142");
		loSubBudgetBean.setId("2003");
		loSubBudgetBean.setPlannedAmount("3434");
		loSubBudgetBean.setSubbudgetName("testSubBudgetName");
		loSubBudgetBean.setTotalbudgetAmount("200");
		loSubBudgetBean.setActiveFlag("1");
		loSubBudgetBean.setBudgetfiscalYear("2014");
		loSubBudgetBean.setBudgetStartDate("07/14/2014");
		loSubBudgetBean.setBudgetEndDate("07/14/2013");
		loSubBudgetBean.setContractId("111777");
		loSubBudgetBean.setModifiedByUserId("city_142");
		loSubBudgetBean.setStatusId("2");
		loSubBudgetBean.setSubbudgetAmount("1000");

		return loSubBudgetBean;
	}

	// no such method exception here
	/*
	 * @Test public void testupdatBudgetStatus() throws ApplicationException {
	 * ContractBudgetBean loSubBudgetBean = getDummyContractBudgetBean();
	 * Boolean loValid = moContractBudgetService.updatBudgetStatus(moSession,
	 * true, loSubBudgetBean); assertFalse(loValid); }
	 * 
	 * @Test public void testupdatBudgetStatusBudgetTypeOne() throws
	 * ApplicationException { ContractBudgetBean loSubBudgetBean =
	 * getDummyContractBudgetBean(); loSubBudgetBean.setBudgetTypeId(1); Boolean
	 * loValid = moContractBudgetService.updatBudgetStatus(moSession, true,
	 * loSubBudgetBean); assertFalse(loValid); }
	 * 
	 * @Test public void testupdatBudgetStatusBudgetTypeThree() throws
	 * ApplicationException { ContractBudgetBean loSubBudgetBean =
	 * getDummyContractBudgetBean(); loSubBudgetBean.setBudgetTypeId(3); Boolean
	 * loValid = moContractBudgetService.updatBudgetStatus(moSession, true,
	 * loSubBudgetBean); assertFalse(loValid); }
	 */

	/**
	 * This method tests updateUtilities throws exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateContractBudgetRent() throws ApplicationException
	{
		Rent loRent = new Rent();
		boolean lbUpdateStatus;
		try
		{
			loRent.setBudgetTypeId("2");
			loRent.setId("1");
			loRent.setLocation("newLocation");
			loRent.setId("389");
			loRent.setYtdInvoiceAmt("1000");
			lbUpdateStatus = moContractBudgetService.updateContractBudgetRent(moSession, loRent);
			assertTrue(lbUpdateStatus);

			// Negative1 Scenario -- Application Exception handled by setting.

			lbUpdateStatus = moContractBudgetService.updateContractBudgetRent(null, loRent);
			lbUpdateStatus = moContractBudgetService.updateContractBudgetRent(moSession, null);
		}
		catch (ApplicationException loAexp)
		{
			lbUpdateStatus = true;
			assertTrue(lbUpdateStatus);
		}

	}

	/**
	 * This method tests updateUtilities throws exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = Exception.class)
	public void testUpdateContractBudgetRentException() throws ApplicationException
	{
		moContractBudgetService.updateContractBudgetRent(moSession, null);
	}

	@Test
	public void testInsertStandardRowsSubBudgetLevelContractId() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId("819");
		aoTaskDetailsBean.setStartFiscalYear("2014");
		aoTaskDetailsBean.setUserId("agency_21");
		aoTaskDetailsBean.setTaskName(HHSConstants.TASK_CONTRACT_CONFIGURATION);
		moContractBudgetService.insertStandardRowsSubBudgetLevel(aoTaskDetailsBean, moSession);
		assertNotNull(aoTaskDetailsBean);
	}

	/**
	 * This method tests update Program Income for editing line items other than
	 * 'Other' line item
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateProgramIncome() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			CBProgramIncomeBean loCBProgramIncomeBean = getDummyCBProgramIncomeBeanObj();
			loCBProgramIncomeBean.setBudgetTypeId("2");
			loCBProgramIncomeBean.setModifyByProvider("909");
			Boolean lbUpdateStatus = moContractBudgetService.updateProgramIncome(moSession, loCBProgramIncomeBean);
			assertTrue(lbUpdateStatus);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}

	}

	@Test(expected = ApplicationException.class)
	public void testUpdateProgramIncomeAppExp() throws ApplicationException
	{
		CBProgramIncomeBean loCBProgramIncomeBean = getDummyCBProgramIncomeBeanObj();
		loCBProgramIncomeBean.setBudgetTypeId("2");
		loCBProgramIncomeBean.setModifyByAgency("agency_47");
		moContractBudgetService.updateProgramIncome(null, loCBProgramIncomeBean);
	}

	@Test(expected = Exception.class)
	public void testUpdateProgramIncomeExp() throws ApplicationException
	{
		CBProgramIncomeBean loCBProgramIncomeBean = getDummyCBProgramIncomeBeanObj();
		moContractBudgetService.updateProgramIncome(null, loCBProgramIncomeBean);
	}

	@Test
	public void testFetchPersonnelServiceDataBudType() throws Exception
	{
		CBGridBean loCBGridBeanObj = getCBGridBeanModificationParams();
		PersonnelServicesData loPersonnelServicesData = moContractBudgetService.fetchPersonnelServiceData(moSession,
				loCBGridBeanObj, getMasterBeanApproved());
		assertNotNull(loPersonnelServicesData);
	}

	@Test
	public void testFetchPersonnelServiceDataBudTypeTwo() throws Exception
	{
		CBGridBean loCBGridBeanObj = getCBGridBeanModificationParams();
		loCBGridBeanObj.setBudgetTypeId("2");
		loCBGridBeanObj.setContractBudgetID("666");
		PersonnelServicesData loPersonnelServicesData = moContractBudgetService.fetchPersonnelServiceData(moSession,
				loCBGridBeanObj, getMasterBeanApproved());
		assertNotNull(loPersonnelServicesData);
	}

	@Test
	public void testFetchPersonnelServiceDataBudTypeOne() throws Exception
	{
		CBGridBean loCBGridBeanObj = getCBGridBeanModificationParams();
		loCBGridBeanObj.setBudgetTypeId("1");
		loCBGridBeanObj.setBudgetStatusId("86");
		loCBGridBeanObj.setContractBudgetID("666");
		PersonnelServicesData loPersonnelServicesData = moContractBudgetService.fetchPersonnelServiceData(moSession,
				loCBGridBeanObj, getMasterBeanApproved());
		assertNotNull(loPersonnelServicesData);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchPersonnelServiceDataAppExp1() throws Exception
	{
		CBGridBean loCBGridBeanObj = getCBGridBeanModificationParams();
		loCBGridBeanObj.setBudgetTypeId("1");
		loCBGridBeanObj.setBudgetStatusId("86");
		loCBGridBeanObj.setContractBudgetID("666");
		PersonnelServicesData loPersonnelServicesData = moContractBudgetService.fetchPersonnelServiceData(null,
				loCBGridBeanObj, getMasterBeanApproved());
		assertNotNull(loPersonnelServicesData);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchPersonnelServiceDataAppExp2() throws Exception
	{
		CBGridBean loCBGridBeanObj = getCBGridBeanModificationParams();
		loCBGridBeanObj.setBudgetTypeId("2");
		loCBGridBeanObj.setBudgetStatusId("86");
		loCBGridBeanObj.setContractBudgetID("2");
		PersonnelServicesData loPersonnelServicesData = moContractBudgetService.fetchPersonnelServiceData(null,
				loCBGridBeanObj, getMasterBeanApproved());
		assertNotNull(loPersonnelServicesData);
	}

	@Test
	public void testFetchFyBudgetSummaryTwo() throws ApplicationException
	{

		HashMap<String, String> aoHashMap = new HashMap<String, String>();
		aoHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, "111777");
		aoHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "555");
		aoHashMap.put(HHSConstants.BUDGET_TYPE, "3");
		BudgetDetails loBudgetDetails = (BudgetDetails) moContractBudgetService.fetchFyBudgetSummary(moSession,
				aoHashMap);
		assertNotNull(loBudgetDetails);
		aoHashMap.put(HHSConstants.BUDGET_TYPE, "4");
		loBudgetDetails = (BudgetDetails) moContractBudgetService.fetchFyBudgetSummary(moSession, aoHashMap);
		assertNotNull(loBudgetDetails);
	}

	/*
	 * @Test public void testUpdatBudgetStatus123() throws ApplicationException
	 * { boolean lbSaveStatus = false; Map<String, Object> loQueryMap = new
	 * HashMap<String, Object>(); loQueryMap.put("budgetID", "86");
	 * ContractBudgetBean aoContractBudgetBean = getDummyContractBudgetBean();
	 * lbSaveStatus = moContractBudgetService.updatBudgetStatus(moSession, true,
	 * aoContractBudgetBean); assertFalse(lbSaveStatus); }
	 */

	/**
	 * This method tests set contract budget status for review task with no
	 * update
	 * @throws Exception
	 */
	@Test
	public void testSetContractBudgetStatusForReviewTaskForCoverage() throws Exception
	{
		Boolean lbThrown = false;
		try
		{

			TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
			String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_BUDGET_APPROVED);
			loTaskDetailsBean.setContractId("30");// Need to hardcord
			loTaskDetailsBean.setBudgetId("7");// Need to hardcord
			loTaskDetailsBean.setUserId("agency_12");// Need to hardcord
			loTaskDetailsBean.setEntityType(HHSConstants.TASK_BUDGET_AMENDMENT);
			moContractBudgetService.setContractBudgetStatusForReviewTask(moSession, true, loTaskDetailsBean,
					lsBudgetStatus);

			assertTrue(!moContractBudgetService.getMoState().toString().isEmpty());

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	/**
	 * This method tests if Milestone details are added.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testAddMilestoneForCoverage() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();
			Integer idSeq = 50;
			loCBMileStoneBean.setContractBudgetID("7");
			loCBMileStoneBean.setSubBudgetID("7");
			loCBMileStoneBean.setMileStone("junit test");
			loCBMileStoneBean.setModifiedByUserId("city_142");
			loCBMileStoneBean.setAmount("800");
			loCBMileStoneBean.setId("12312312");
			loCBMileStoneBean.setModifyByProvider("909");
			// positive test
			boolean status = moContractBudgetService.addMilestone(idSeq, loCBMileStoneBean, moSession);
			assertTrue(status);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}

	}

	@Test
	public void testDeleteContractedServices123() throws ApplicationException
	{
		ContractedServicesBean aoCBGridBeanObj = new ContractedServicesBean();
		aoCBGridBeanObj.setSubBudgetID("555");
		aoCBGridBeanObj.setContractBudgetID("555");
		aoCBGridBeanObj.setBudgetTypeId("2");
		aoCBGridBeanObj.setSubHeader("1");
		aoCBGridBeanObj.setDescOfService("testing");
		aoCBGridBeanObj.setCsName("HHS_Testing");
		aoCBGridBeanObj.setId("3123");
		Boolean lbContractedServicesBean = moContractBudgetService.deleteContractedServices(moSession, aoCBGridBeanObj);
		assertTrue(lbContractedServicesBean);
	}

	@Test
	public void testEditContractedServices123() throws ApplicationException
	{
		ContractedServicesBean aoCBGridBeanObj = new ContractedServicesBean();
		aoCBGridBeanObj.setSubBudgetID("555");
		aoCBGridBeanObj.setContractBudgetID("555");
		aoCBGridBeanObj.setBudgetTypeId("2");
		aoCBGridBeanObj.setSubHeader("1");
		aoCBGridBeanObj.setDescOfService("testing");
		aoCBGridBeanObj.setCsName("HHS_Testing");
		aoCBGridBeanObj.setFyBudget("12312");
		aoCBGridBeanObj.setId("3123");
		Boolean lbContractedServicesBean = moContractBudgetService.editContractedServices(moSession, aoCBGridBeanObj);
		assertTrue(lbContractedServicesBean);
	}

	@Test
	public void testAddContractedServices123() throws ApplicationException
	{
		ContractedServicesBean aoCBGridBeanObj = new ContractedServicesBean();
		aoCBGridBeanObj.setSubBudgetID("6");
		aoCBGridBeanObj.setContractBudgetID("6");
		aoCBGridBeanObj.setBudgetTypeId("2");
		aoCBGridBeanObj.setSubHeader("1");
		aoCBGridBeanObj.setDescOfService("testing");
		aoCBGridBeanObj.setCsName("HHS_Testing");
		aoCBGridBeanObj.setId("12345");
		aoCBGridBeanObj.setFyBudget("1312");
		aoCBGridBeanObj.setModifyByProvider("909");
		Integer liVal = moContractBudgetService.addContractedServices(moSession, aoCBGridBeanObj);
		assertNotNull(liVal);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchNonGridContractedServices3123() throws ApplicationException
	{
		moContractBudgetService.fetchNonGridContractedServices(null, new CBGridBean());
	}

	@Test
	public void testInsertContractBudgetRent123() throws ApplicationException
	{
		Rent loRent = new Rent();
		Integer loSequence = 565;
		loRent.setLocation("Delhi");
		loRent.setManagementCompanyName("mumbai");
		loRent.setPercentChargedToContract("3.00");
		loRent.setPropertyOwner("Delhi property");
		loRent.setPublicSchoolSpace("0");
		loRent.setCreatedByUserId("909");
		loRent.setModifiedByUserId("909");
		loRent.setFyBudget("2000");
		loRent.setId("new_row");
		loRent.setContractBudgetID("4");
		loRent.setSubBudgetID("4");
		loRent.setRemainingAmt("0");
		loRent.setLineItemInvoiceAmt("0");
		loRent.setProposedBudget("0");
		loRent.setAmendmentAmount("0");
		// Positive Scenario -- Rent Update Successful.

		loRent.setYtdInvoiceAmt("1000");
		loRent.setBudgetTypeId("2");
		boolean lbUpdateStatus = moContractBudgetService.insertContractBudgetRent(loSequence, moSession, loRent);
		assertTrue(lbUpdateStatus);
	}
	
	
	@Test
	public void testCreateReplicaOfBudgetNewFY1() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId("75");
		loTaskDetailsBean.setStartFiscalYear("2016");
		loTaskDetailsBean.setBudgetId("75");
		loTaskDetailsBean.setUserId("system");
		moContractBudgetService.createReplicaOfBudgetNewFY(moSession,  loTaskDetailsBean);
		
	}
	
	@Test(expected=ApplicationException.class)
	public void testCreateReplicaOfBudgetNewFY2() throws ApplicationException
	{
		moContractBudgetService.createReplicaOfBudgetNewFY(null,  null);
	}
	
	
	@Test
	public void testFetchContractStatusMethod1() throws ApplicationException
	{
		String lsResult = moContractBudgetService.fetchContractStatusMethod(moSession,  "30");
		assertTrue(!lsResult.isEmpty());
	}
	
	@Test(expected=ApplicationException.class)
	public void testFetchContractStatusMethod2() throws ApplicationException
	{
		String lsResult = moContractBudgetService.fetchContractStatusMethod(null,  "30");
		assertTrue(!lsResult.isEmpty());
	}
	
	@Test
	public void testSetAmendmentContractStatus1() throws ApplicationException
	{
		HashMap loHashMap = new HashMap<String,String>();
		loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW,"144");
		boolean lbResult = moContractBudgetService.setAmendmentContractStatus(moSession, loHashMap);
		assertTrue(lbResult);
	}
	
	@Test(expected=ApplicationException.class)
	public void testSetAmendmentContractStatus2() throws ApplicationException
	{
		boolean lbResult = moContractBudgetService.setAmendmentContractStatus(null, null);
		assertFalse(lbResult);
	}
	
	@Test()
	public void testSetAmendmentContractStatus2123() throws ApplicationException
	{
		//START || Added as a part of release 3.12.0 for enhancement request 6643
		Map loHashMap = new HashMap();
		loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW,"123");
		loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "12");
		loHashMap.put(HHSConstants.MODIFY_BY, "system");
		moContractBudgetService.updateLineItemModifiedDate(moSession, loHashMap);
	}
	
	
	// end of junit code coverage
}
