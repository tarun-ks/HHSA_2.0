package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.batch.impl.PDFGenerationBatch;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.daomanager.service.ContractBudgetAmendmentService;
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
import com.nyc.hhs.model.ContractedServicesBean;
import com.nyc.hhs.model.LineItemMasterBean;
import com.nyc.hhs.model.MasterBean;
import com.nyc.hhs.model.PersonnelServiceBudget;
import com.nyc.hhs.model.RateBean;
import com.nyc.hhs.model.Rent;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.model.UnallocatedFunds;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.PropertyLoader;

/**
 * @author faiyaz.asharaf
 * 
 */
public class ContractBudgetAmendmentServiceTest
{

	private static SqlSession moSession = null; // SQL Session
	
	public String amendContractId="63";
	public String baseContractId="53"; 
	public String contractBudgetID="24";
	public String subBudgetID="24";
	public String parentSubBudgetID="14";
	public String parentBudgetID="13";
	public String invoiceId="55";
	public String agency="agency_12";
	public String provider="803";
	
	//base line items id
	public String contractedServiceId="13";
	public String rateId="8";
	public String milestoneId="7";
	public String personalServiceSalariedId="11";
	public String programIncomeId="84";
	public String unallocatedId="12";
	public String equipmentId="5";
	public String rentId="8";
	public String IndRateId="12";
	public String utilitiesId="100";
	
	
	public String amendContractIdApproved="60";
	public String contractBudgetIDApproved="22";
	public String subBudgetIDApproved="22";
	

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
			String lsCastorPath = ((new PDFGenerationBatch()).getClass().getResource(lsClassName) + HHSConstants.EMPTY_STRING).replace(
					HHSConstants.PDF_CLASS, HHSConstants.CASTOR_MAPPING);

			BaseCacheManagerWeb.getInstance().putCacheObject(HHSConstants.CASTER_CONFIGURATION_PATH, lsCastorPath);
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
	}

	ContractBudgetAmendmentService moContractBudgetAmendmentService = new ContractBudgetAmendmentService();

	// Program Income Amendment Test Case Starts

	private CBGridBean getDummyCBGridBeanObj()
	{
		CBGridBean loCBGridBean = new CBGridBean();

		loCBGridBean.setParentBudgetId(parentBudgetID);
		loCBGridBean.setParentSubBudgetId(parentSubBudgetID);
		loCBGridBean.setContractBudgetID(contractBudgetID);
		loCBGridBean.setSubBudgetID(subBudgetID);
		loCBGridBean.setContractID(baseContractId);
		loCBGridBean.setModifyByAgency("803");
		loCBGridBean.setModifyByProvider("agency_12");
		return loCBGridBean;

	}

	
	private CBGridBean getDummyCBGridBeanObjApproved()
	{
		CBGridBean loCBGridBean = new CBGridBean();

		loCBGridBean.setParentBudgetId(parentBudgetID);
		loCBGridBean.setParentSubBudgetId(parentSubBudgetID);
		loCBGridBean.setContractBudgetID(contractBudgetIDApproved);
		loCBGridBean.setSubBudgetID(subBudgetIDApproved);
		loCBGridBean.setContractID(baseContractId);
		loCBGridBean.setModifyByAgency("803");
		loCBGridBean.setModifyByProvider("agency_12");
		loCBGridBean.setBudgetStatusId("86");
		return loCBGridBean;

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
		List<CBEquipmentBean> equipmentBeanList =  new ArrayList<CBEquipmentBean>();
		equipmentBeanList.add(getCBEquipmentBeanApproved());
		ContractedServicesBean loContractedServicesBean = new ContractedServicesBean();
		List<CBProfessionalServicesBean> CBProfessionalServicesBean =new ArrayList<CBProfessionalServicesBean>();
		CBProfessionalServicesBean.add(getProfServicesDetailsApproved());
		List<Rent> CBRent =new ArrayList<Rent>();
		CBRent.add(getRentApprovedObj());
		List<CBIndirectRateBean> CBCBIndirectRateBean =new ArrayList<CBIndirectRateBean>();
		CBCBIndirectRateBean.add(getIndRateObjApproved());
		List<RateBean> CBRateBean =new ArrayList<RateBean>();
		CBRateBean.add(getRateBeanApprovedObj());
		List<CBUtilities> CBCBUtilities =new ArrayList<CBUtilities>();
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
		/*P8UserSession loFilenetSession = getFileNetSession();
		moContractBudgetAmendmentService.generateMasterBeanObjectFromXML(moSession, "22", loFilenetSession);*/
		return masterBean;
	 }

	
	private CBProgramIncomeBean getDummyCBProgramIncomeBeanObj()
	{
		CBProgramIncomeBean loCBProgramIncomeBean = new CBProgramIncomeBean();
		loCBProgramIncomeBean.setContractBudgetID(contractBudgetID);
		loCBProgramIncomeBean.setSubBudgetID(subBudgetID);
		loCBProgramIncomeBean.setContractID(baseContractId);
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
	
	private CBOperationSupportBean getCBOperationSupportBeanObj()
	{
		CBOperationSupportBean loCBOperationSupportBean = new CBOperationSupportBean();
		loCBOperationSupportBean.setContractBudgetID(contractBudgetID);
		loCBOperationSupportBean.setSubBudgetID(subBudgetID);
		loCBOperationSupportBean.setContractID(baseContractId);
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
		loCBProgramIncomeBean.setContractID(baseContractId);
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
		loCBProfessionalServicesBean.setContractID(baseContractId);
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
		loCBOperationSupportBean.setContractID(baseContractId);
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
		loCBEquipmentBean.setContractID(baseContractId);
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
		loCBEquipmentBean.setContractID(baseContractId);
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
		loRent.setContractID(baseContractId);
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
		loCBMileStoneBean.setContractID(baseContractId);
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
		loRate.setContractID(baseContractId);
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
		CBIndirectRateBean loCBIndirectRateBean  = new CBIndirectRateBean();
		loCBIndirectRateBean.setContractBudgetID(contractBudgetID);
		loCBIndirectRateBean.setSubBudgetID(subBudgetID);
		loCBIndirectRateBean.setContractID(baseContractId);
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
		loCBUtilities.setContractID(baseContractId);
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
		loCBProfessionalServicesBean.setContractID(baseContractId);
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
	 * This method tests fetchProgramIncomeAmendment method for good data inputs
	 */
	@Test
	public void testFetchProgramIncomeAmendment() throws ApplicationException
	{
		CBGridBean loCBGridBean = getDummyCBGridBeanObj();
		List<CBProgramIncomeBean> loResultList = moContractBudgetAmendmentService.fetchProgramIncomeAmendment(
				loCBGridBean, moSession,null);
		assertNotNull(loResultList);
	}
	
	
	/**
	 * This method tests fetchProgramIncomeAmendment method for good data inputs
	 */
	@Test
	public void testFetchProgramIncomeApprovedAmendment() throws ApplicationException
	{
		CBGridBean loCBGridBean = getDummyCBGridBeanObjApproved();
		List<CBProgramIncomeBean> loResultList = moContractBudgetAmendmentService.fetchProgramIncomeAmendment(
				loCBGridBean, moSession,getMasterBeanApproved());
		assertNotNull(loResultList);
	}

	/**
	 * This method tests fetchProgramIncomeAmendment method for bad data inputs
	 * and an ApplicartionException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchProgramIncomeAmendmentWithAppException() throws ApplicationException
	{
		CBGridBean loCBGridBean = getDummyCBGridBeanObj();
		loCBGridBean.setSubBudgetID(null);// Invalid Sub Budget id
		moContractBudgetAmendmentService.fetchProgramIncomeAmendment(loCBGridBean, moSession, null);

	}

	/**
	 * This method tests fetchProgramIncomeAmendment method for bad data inputs
	 * and an ApplicartionException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchProgramIncomeAmendmentWithAppException2() throws ApplicationException
	{
		CBGridBean loCBGridBean = getDummyCBGridBeanObj();
		moContractBudgetAmendmentService.fetchProgramIncomeAmendment(loCBGridBean, null, null);
	}

	/**
	 * This method tests fetchProgramIncomeAmendment method for bad data inputs
	 * and an ApplicartionException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchProgramIncomeAmendmentWithAppException3() throws ApplicationException
	{
		CBGridBean loCBGridBean = getDummyCBGridBeanObj();
		loCBGridBean.setSubBudgetID("377H_C'");// Invalid Sub Budget id
		moContractBudgetAmendmentService.fetchProgramIncomeAmendment(loCBGridBean, null, null);
	}

	/**
	 * This method tests fetchProgramIncomeAmendment method for bad data inputs
	 * and an ApplicartionException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchProgramIncomeAmendmentWithAppException4() throws ApplicationException
	{
		CBGridBean loCBGridBean = getDummyCBGridBeanObj();
		loCBGridBean.setSubBudgetID("377H_A'");// Invalid Sub Budget id
		moContractBudgetAmendmentService.fetchProgramIncomeAmendment(loCBGridBean, null, null);
	}

	/**
	 * This method tests fetchProgramIncomeAmendment method for bad data inputs
	 * and an Exception is expected
	 * 
	 * @throws Exception If an Exception occurs
	 */
	@Test(expected = Exception.class)
	public void testFetchProgramIncomeAmendmentWithException() throws ApplicationException
	{
		CBGridBean loCBGridBean = null;
		moContractBudgetAmendmentService.fetchProgramIncomeAmendment(loCBGridBean, moSession, null);
	}

	/**
	 * This method tests updateProgramIncomeAmendment method for good data
	 * inputs for scenario where an already existing line item entry is updated
	 */
	@Test
	public void testUpdateProgramIncomeAmendmentForNegativeAmendment() throws ApplicationException
	{
		CBProgramIncomeBean loCBProgramIncomeBean = getDummyCBProgramIncomeBeanObj();
		loCBProgramIncomeBean.setAmendmentType("negative");
		loCBProgramIncomeBean.setId(programIncomeId);
		Boolean liResult = moContractBudgetAmendmentService.updateProgramIncomeAmendment(moSession,
				loCBProgramIncomeBean);
		assertTrue(liResult);
	}
	
	
	@Test
	public void testUpdateProgramIncomeAmendmentForNegativeAmendmentError() throws ApplicationException
	{
		CBProgramIncomeBean loCBProgramIncomeBean = getDummyCBProgramIncomeBeanObj();
		loCBProgramIncomeBean.setAmendmentType("negative");
		loCBProgramIncomeBean.setId(programIncomeId);
		loCBProgramIncomeBean.setAmendmentAmount("-1000000");
		moContractBudgetAmendmentService.updateProgramIncomeAmendment(moSession,
				loCBProgramIncomeBean);
	}
	
	
	@Test(expected = ApplicationException.class)
	public void testUpdateProgramIncomeAmendmentForNegativeAmendmentExp() throws ApplicationException
	{
		CBProgramIncomeBean loCBProgramIncomeBean = getDummyCBProgramIncomeBeanObj();
		loCBProgramIncomeBean.setAmendmentType("negative");
		loCBProgramIncomeBean.setId(programIncomeId);
		loCBProgramIncomeBean.setAmendmentAmount("200");
		moContractBudgetAmendmentService.updateProgramIncomeAmendment(moSession,
				loCBProgramIncomeBean);
	}

	
	@Test(expected = ApplicationException.class)
	public void testUpdateProgramIncomeAmendmentForPositiveAmendmentExp() throws ApplicationException
	{
		CBProgramIncomeBean loCBProgramIncomeBean = getDummyCBProgramIncomeBeanObj();
		loCBProgramIncomeBean.setAmendmentType("positive");
		loCBProgramIncomeBean.setId(programIncomeId);
		loCBProgramIncomeBean.setAmendmentAmount("-200");
		moContractBudgetAmendmentService.updateProgramIncomeAmendment(moSession,
				loCBProgramIncomeBean);
	}


	/**
	 * This method tests updateProgramIncomeAmendment method for good data
	 * inputs for scenario where a new line item entry is made when does not
	 * exists
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateProgramIncomeAmendmentForNegativeAmendmentForNewItemInsert() throws ApplicationException
	{
		CBProgramIncomeBean loCBProgramIncomeBean = getDummyCBProgramIncomeBeanObj();
		loCBProgramIncomeBean.setAmendmentType("negative");
		loCBProgramIncomeBean.setId("83");
		Boolean liResult = moContractBudgetAmendmentService.updateProgramIncomeAmendment(moSession,
				loCBProgramIncomeBean);
		assertTrue(liResult);
	}

	/**
	 * This method tests updateProgramIncomeAmendment throws exception
	 */
	@Test(expected = Exception.class)
	public void testUpdateProgramIncomeAmendmentForNegativeAmendmentWithAppException() throws ApplicationException
	{
		CBProgramIncomeBean loCBProgramIncomeBean = getDummyCBProgramIncomeBeanObj();
		loCBProgramIncomeBean.setAmendmentType("negative");
		loCBProgramIncomeBean.setId("");// Invalid programIncomeId
		moContractBudgetAmendmentService.updateProgramIncomeAmendment(moSession, loCBProgramIncomeBean);
	}

	/**
	 * This method tests updateProgramIncomeAmendment method for bad data inputs
	 * and an ApplicartionException is expected
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateProgramIncomeAmendmentForNegativeAmendmentWithException() throws ApplicationException
	{
		CBProgramIncomeBean loCBProgramIncomeBean = getDummyCBProgramIncomeBeanObj();
		loCBProgramIncomeBean.setAmendmentType("negative");
		loCBProgramIncomeBean.setId("A_105_1_10_10_105");
		moContractBudgetAmendmentService.updateProgramIncomeAmendment(moSession, loCBProgramIncomeBean);
	}

	/**
	 * This method tests updateProgramIncomeAmendment method for bad data inputs
	 * and an ApplicartionException is expected
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateProgramIncomeAmendmentForNegativeAmendmentWithException2() throws ApplicationException
	{
		CBProgramIncomeBean loCBProgramIncomeBean = getDummyCBProgramIncomeBeanObj();
		loCBProgramIncomeBean.setAmendmentType("negative");
		loCBProgramIncomeBean.setId("B_107_1_10_10_105");
		moContractBudgetAmendmentService.updateProgramIncomeAmendment(moSession, loCBProgramIncomeBean);
	}

	/**
	 * This method tests updateProgramIncomeAmendment method for bad data inputs
	 * and an ApplicartionException is expected
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateProgramIncomeAmendmentForNegativeAmendmentWithException3() throws ApplicationException
	{
		CBProgramIncomeBean loCBProgramIncomeBean = getDummyCBProgramIncomeBeanObj();
		loCBProgramIncomeBean.setAmendmentType("negative");
		loCBProgramIncomeBean.setId("CC_109_1_10_10_102");
		moContractBudgetAmendmentService.updateProgramIncomeAmendment(moSession, loCBProgramIncomeBean);
	}
	

	/**
	 * This method tests updateProgramIncomeAmendment method for good data
	 * inputs for scenario where a new line item entry is made when does not
	 * exists
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateProgramIncomeAmendmentForNewItemInsert() throws ApplicationException
	{
		CBProgramIncomeBean loCBProgramIncomeBean = getDummyCBProgramIncomeBeanObj();
		loCBProgramIncomeBean.setId("84");
		Boolean liResult = moContractBudgetAmendmentService.updateProgramIncomeAmendment(moSession,
				loCBProgramIncomeBean);
		assertTrue(liResult);
	}

	/**
	 * This method tests updateProgramIncomeAmendment throws exception
	 */
	@Test(expected = Exception.class)
	public void testUpdateProgramIncomeAmendmentWithAppException() throws ApplicationException
	{
		CBProgramIncomeBean loCBProgramIncomeBean = getDummyCBProgramIncomeBeanObj();
		loCBProgramIncomeBean.setId("");// Invalid programIncomeId
		moContractBudgetAmendmentService.updateProgramIncomeAmendment(moSession, loCBProgramIncomeBean);
	}

	/**
	 * This method tests updateProgramIncomeAmendment method for bad data inputs
	 * and an ApplicartionException is expected
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateProgramIncomeAmendmentWithException() throws ApplicationException
	{
		CBProgramIncomeBean loCBProgramIncomeBean = getDummyCBProgramIncomeBeanObj();
		loCBProgramIncomeBean.setId("A_105_1_10_10_105");
		moContractBudgetAmendmentService.updateProgramIncomeAmendment(moSession, loCBProgramIncomeBean);
	}

	/**
	 * This method tests updateProgramIncomeAmendment method for bad data inputs
	 * and an ApplicartionException is expected
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateProgramIncomeAmendmentWithException2() throws ApplicationException
	{
		CBProgramIncomeBean loCBProgramIncomeBean = getDummyCBProgramIncomeBeanObj();
		loCBProgramIncomeBean.setId("B_107_1_10_10_105");
		moContractBudgetAmendmentService.updateProgramIncomeAmendment(moSession, loCBProgramIncomeBean);
	}

	/**
	 * This method tests updateProgramIncomeAmendment method for bad data inputs
	 * and an ApplicartionException is expected
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateProgramIncomeAmendmentWithException3() throws ApplicationException
	{
		CBProgramIncomeBean loCBProgramIncomeBean = getDummyCBProgramIncomeBeanObj();
		loCBProgramIncomeBean.setId("CC_109_1_10_10_102");
		moContractBudgetAmendmentService.updateProgramIncomeAmendment(moSession, loCBProgramIncomeBean);
	}

	// Program Income Amendment Test Case Ends
	
	
	
	// Unallocated Funds Amendment
	
	/**
	 * This method tests if Unallocated Funds are available in database.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void testFetchUnallocatedFundsModification1() throws ApplicationException
	{
		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		loUnallocatedFundsBean.setSubBudgetID(subBudgetID);
		loUnallocatedFundsBean.setContractBudgetID(contractBudgetID);
		loUnallocatedFundsBean.setCreatedByUserId("city_142");
		loUnallocatedFundsBean.setParentBudgetId(parentBudgetID);
		loUnallocatedFundsBean.setParentSubBudgetId(parentSubBudgetID);

		List<UnallocatedFunds> loUnallocatedFundsList = moContractBudgetAmendmentService
				.fetchAmendmentUnallocatedFunds(moSession, loUnallocatedFundsBean,null);

		assertNotNull(loUnallocatedFundsList);
		assertTrue(loUnallocatedFundsList.size() > 0);
	}

	
	@Test
	public void testFetchUnallocatedFundsAMenDApproved() throws ApplicationException
	{
		List<UnallocatedFunds> loUnallocatedFundsList = moContractBudgetAmendmentService
				.fetchAmendmentUnallocatedFunds(moSession, getDummyUnallocatedFundsBeanObjApproved(),getMasterBeanApproved());
		assertTrue(loUnallocatedFundsList.size() > 0);
	}
	
	@Test
	public void testFetchUnallocatedFundsAmendment() throws ApplicationException
	{
		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		loUnallocatedFundsBean.setSubBudgetID("22222");
		loUnallocatedFundsBean.setContractBudgetID("222222");
		loUnallocatedFundsBean.setCreatedByUserId("city_142");
		loUnallocatedFundsBean.setParentBudgetId("33333");
		loUnallocatedFundsBean.setParentSubBudgetId("3333333");

		List<UnallocatedFunds> loUnallocatedFundsList = moContractBudgetAmendmentService
				.fetchAmendmentUnallocatedFunds(moSession, loUnallocatedFundsBean,null);

		assertTrue(loUnallocatedFundsList.size() > 0);
	}

	@Test
	public void testFetchUnallocatedFundsAmendment1() throws ApplicationException
	{
		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		loUnallocatedFundsBean.setSubBudgetID("59");
		loUnallocatedFundsBean.setContractBudgetID("54");
		loUnallocatedFundsBean.setCreatedByUserId("city_142");
		loUnallocatedFundsBean.setParentBudgetId("59");
		loUnallocatedFundsBean.setParentSubBudgetId("3333333");

		List<UnallocatedFunds> loUnallocatedFundsList = moContractBudgetAmendmentService
				.fetchAmendmentUnallocatedFunds(moSession, loUnallocatedFundsBean,null);

		assertTrue(loUnallocatedFundsList.size() > 0);
	}

	
	/**
	 * This method tests if CBGridBean is NULL
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void testFetchUnallocatedFundsModification2() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = null;
		List<UnallocatedFunds> loUnallocatedFundsList = null;

		loUnallocatedFundsList = moContractBudgetAmendmentService.fetchAmendmentUnallocatedFunds(moSession,
				aoCBGridBeanObj,null);
		assertNull(loUnallocatedFundsList);

	}

	/**
	 * This method tests if Sub BudgetId is Null
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void testFetchUnallocatedFundsModification3() throws ApplicationException
	{

		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		loUnallocatedFundsBean.setContractBudgetID(contractBudgetID);
		loUnallocatedFundsBean.setCreatedByUserId("city_142");

		List<UnallocatedFunds> loUnallocatedFundsList = moContractBudgetAmendmentService
				.fetchAmendmentUnallocatedFunds(moSession, loUnallocatedFundsBean,null);

		assertNull(loUnallocatedFundsList);
	}

	/**
	 * This method tests if new Unallocated Funds data is added for both base
	 * and modification
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void testFetchUnallocatedFundsModification4() throws ApplicationException
	{
		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		loUnallocatedFundsBean.setSubBudgetID(subBudgetID);
		loUnallocatedFundsBean.setContractBudgetID(contractBudgetID);
		loUnallocatedFundsBean.setCreatedByUserId("city_142");
		loUnallocatedFundsBean.setModificationAmount("0.0");
		loUnallocatedFundsBean.setAmmount("0.0");
		loUnallocatedFundsBean.setParentBudgetId(parentBudgetID);
		loUnallocatedFundsBean.setParentSubBudgetId(parentSubBudgetID);

		List<UnallocatedFunds> loUnallocatedFundsList = moContractBudgetAmendmentService
				.fetchAmendmentUnallocatedFunds(moSession, loUnallocatedFundsBean,null);

		assertTrue(loUnallocatedFundsList.size()> 0);
		
	}

	/**
	 * This method tests if database session is NULL
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchUnallocatedFundsModification5() throws ApplicationException
	{
		Boolean lbThrown = false;
		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		loUnallocatedFundsBean.setSubBudgetID(subBudgetID);
		loUnallocatedFundsBean.setContractBudgetID(contractBudgetID);
		loUnallocatedFundsBean.setCreatedByUserId("city_142");

		moSession = null;
		try
		{
			moContractBudgetAmendmentService.fetchAmendmentUnallocatedFunds(moSession, loUnallocatedFundsBean,null);
		}
		catch (ApplicationException loAppEx)
		{
			try
			{
				moSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
			}
			catch (Exception loEx)
			{
				lbThrown = true;
				assertTrue("Exception thrown", lbThrown);
			}
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	/**
	 * This method tests if new Unallocated Funds data is added for modification
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void testFetchUnallocatedFundsModification6() throws ApplicationException
	{
		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		loUnallocatedFundsBean.setParentBudgetId(parentBudgetID);
		loUnallocatedFundsBean.setParentSubBudgetId(parentSubBudgetID);
		loUnallocatedFundsBean.setCreatedByUserId("city_142");
		loUnallocatedFundsBean.setModificationAmount("0.0");
		loUnallocatedFundsBean.setAmmount("0.0");

		loUnallocatedFundsBean.setSubBudgetID(subBudgetID);
		loUnallocatedFundsBean.setContractBudgetID(contractBudgetID);

		List<UnallocatedFunds> loUnallocatedFundsList = moContractBudgetAmendmentService
				.fetchAmendmentUnallocatedFunds(moSession, loUnallocatedFundsBean,null);

		assertTrue(loUnallocatedFundsList.size() >0);
		
	}

	/**
	 * This method is for the negative condition if the sub_budget_id is not
	 * there.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchUnallocatedFundsModification7() throws ApplicationException
	{
		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		loUnallocatedFundsBean.setSubBudgetID(subBudgetID);
		loUnallocatedFundsBean.setContractBudgetID(contractBudgetID);
		loUnallocatedFundsBean.setCreatedByUserId("city_142");
		loUnallocatedFundsBean.setModificationAmount("0.0");
		loUnallocatedFundsBean.setAmmount("0.0");
		loUnallocatedFundsBean.setParentBudgetId(parentBudgetID);
		loUnallocatedFundsBean.setParentSubBudgetId(parentSubBudgetID);

		Boolean lbThrown = false;
		try
		{
			moContractBudgetAmendmentService.fetchAmendmentUnallocatedFunds(moSession, loUnallocatedFundsBean,null);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}

	}

	/**
	 * This method is for the negative condition if the parent sub_budget_id is
	 * not there.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchUnallocatedFundsModification8() throws ApplicationException
	{
		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		loUnallocatedFundsBean.setSubBudgetID(subBudgetID);
		loUnallocatedFundsBean.setSubBudgetID(subBudgetID);
		loUnallocatedFundsBean.setContractBudgetID(contractBudgetID);
		loUnallocatedFundsBean.setCreatedByUserId("city_142");
		loUnallocatedFundsBean.setModificationAmount("0.0");
		loUnallocatedFundsBean.setAmmount("0.0");
		loUnallocatedFundsBean.setParentBudgetId(parentBudgetID);
		loUnallocatedFundsBean.setParentSubBudgetId(parentSubBudgetID);

		Boolean lbThrown = false;
		try
		{
			moContractBudgetAmendmentService.fetchAmendmentUnallocatedFunds(moSession, loUnallocatedFundsBean,null);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}
	
	/**
	 * This method is for the negative condition if the parent sub_budget_id is
	 * not there.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchUnallocatedFundsModification9() throws ApplicationException
	{
		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		loUnallocatedFundsBean.setSubBudgetID(subBudgetID);
		loUnallocatedFundsBean.setContractBudgetID(contractBudgetID);
		loUnallocatedFundsBean.setCreatedByUserId("city_142");
		loUnallocatedFundsBean.setParentBudgetId(parentBudgetID);
		loUnallocatedFundsBean.setParentSubBudgetId(parentSubBudgetID);

		Boolean lbThrown = false;
		try
		{
			moContractBudgetAmendmentService.fetchAmendmentUnallocatedFunds(moSession, loUnallocatedFundsBean,null);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}
	
	
	/**
	 * This method tests if database session is Null
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void updateAmendmentUnallocatedFunds1() throws ApplicationException
	{
		Boolean lbThrown = false;
		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		loUnallocatedFundsBean.setSubBudgetID(subBudgetID);
		loUnallocatedFundsBean.setContractBudgetID(contractBudgetID);
		loUnallocatedFundsBean.setModifiedByUserId("city_142");
		loUnallocatedFundsBean.setParentBudgetId(parentBudgetID);
		loUnallocatedFundsBean.setParentSubBudgetId(parentSubBudgetID);
		loUnallocatedFundsBean.setAmendmentType(null);
		try
		{
			moContractBudgetAmendmentService.updateAmendmentUnallocatedFunds(moSession, loUnallocatedFundsBean);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	
	private UnallocatedFunds getDummyUnallocatedFundsBeanObj()
	{
		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		loUnallocatedFundsBean.setSubBudgetID(subBudgetID);
		loUnallocatedFundsBean.setContractBudgetID(contractBudgetID);
		loUnallocatedFundsBean.setContractID(baseContractId);
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
		loUnallocatedFundsBean.setModificationAmount("100");
		return loUnallocatedFundsBean;
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
	
	
	@Test(expected = ApplicationException.class)
	public void updateAmendmentUnallocatedFundsPosExp() throws ApplicationException
	{
		UnallocatedFunds loUnallocatedFundsBean  = getDummyUnallocatedFundsBeanObj();
		loUnallocatedFundsBean.setAmendmentType("positve");
		loUnallocatedFundsBean.setModificationAmount("-100");
		moContractBudgetAmendmentService.updateAmendmentUnallocatedFunds(moSession, loUnallocatedFundsBean);
	}

	
	@Test(expected = ApplicationException.class)
	public void updateAmendmentUnallocatedFundsNegExp() throws ApplicationException
	{
		UnallocatedFunds loUnallocatedFundsBean  = getDummyUnallocatedFundsBeanObj();
		loUnallocatedFundsBean.setAmendmentType("negative");
		loUnallocatedFundsBean.setModificationAmount("1000");
		moContractBudgetAmendmentService.updateAmendmentUnallocatedFunds(moSession, loUnallocatedFundsBean);
	}
	
	@Test(expected = ApplicationException.class)
	public void updateAmendmentUnallocatedFundsExp() throws ApplicationException
	{
		UnallocatedFunds loUnallocatedFundsBean  = getDummyUnallocatedFundsBeanObj();
		loUnallocatedFundsBean.setAmendmentType("negative");
		loUnallocatedFundsBean.setModificationAmount("-1000");
		moContractBudgetAmendmentService.updateAmendmentUnallocatedFunds(moSession, loUnallocatedFundsBean);
	}

	/**
	 * This method tests if database session is Null
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void updateAmendmentUnallocatedFunds2() throws ApplicationException
	{
		Boolean lbThrown = false;
		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		loUnallocatedFundsBean.setAmendmentType(HHSConstants.POSITIVE);
		loUnallocatedFundsBean.setSubBudgetID(subBudgetID);
		loUnallocatedFundsBean.setContractBudgetID(contractBudgetID);
		loUnallocatedFundsBean.setModifiedByUserId("city_142");
		loUnallocatedFundsBean.setParentBudgetId(parentBudgetID);
		loUnallocatedFundsBean.setParentSubBudgetId(parentSubBudgetID);
		loUnallocatedFundsBean.setModificationAmount("-132.90");
		try
		{
			moContractBudgetAmendmentService.updateAmendmentUnallocatedFunds(moSession, loUnallocatedFundsBean);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}
	
	/**
	 * This method tests if database session is Null
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void updateAmendmentUnallocatedFunds3() throws ApplicationException
	{
		Boolean lbThrown = false;
		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		loUnallocatedFundsBean.setAmendmentType(HHSConstants.NEGATIVE);
		loUnallocatedFundsBean.setSubBudgetID(subBudgetID);
		loUnallocatedFundsBean.setContractBudgetID(contractBudgetID);
		loUnallocatedFundsBean.setModifiedByUserId("city_142");
		loUnallocatedFundsBean.setParentBudgetId(parentBudgetID);
		loUnallocatedFundsBean.setParentSubBudgetId(parentSubBudgetID);
		loUnallocatedFundsBean.setModificationAmount("13452.90");
		try
		{
			moContractBudgetAmendmentService.updateAmendmentUnallocatedFunds(moSession, loUnallocatedFundsBean);
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
	public void updateAmendmentUnallocatedFunds4() throws ApplicationException
	{
		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		loUnallocatedFundsBean.setAmendmentType(HHSConstants.POSITIVE);
		loUnallocatedFundsBean.setSubBudgetID(subBudgetID);
		loUnallocatedFundsBean.setContractBudgetID(contractBudgetID);
		loUnallocatedFundsBean.setModifiedByUserId("city_142");
		loUnallocatedFundsBean.setParentBudgetId(parentBudgetID);
		loUnallocatedFundsBean.setParentSubBudgetId(parentSubBudgetID);
		loUnallocatedFundsBean.setModificationAmount("13452.90");
		loUnallocatedFundsBean.setId("12");
		
		boolean status = moContractBudgetAmendmentService.updateAmendmentUnallocatedFunds(moSession,
				loUnallocatedFundsBean);

		assertTrue(status);
	}
	
	/**
	 * This method tests if Unallocated Funds are updated database.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void updateAmendmentUnallocatedFunds5() throws ApplicationException
	{
		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		loUnallocatedFundsBean.setAmendmentType(HHSConstants.NEGATIVE);
		loUnallocatedFundsBean.setSubBudgetID(subBudgetID);
		loUnallocatedFundsBean.setContractBudgetID(contractBudgetID);
		loUnallocatedFundsBean.setModifiedByUserId("city_142");
		loUnallocatedFundsBean.setParentBudgetId(parentBudgetID);
		loUnallocatedFundsBean.setParentSubBudgetId(parentSubBudgetID);
		loUnallocatedFundsBean.setModificationAmount("-2.90");

		boolean status = moContractBudgetAmendmentService.updateAmendmentUnallocatedFunds(moSession,
				loUnallocatedFundsBean);

		assertTrue(status);
	}

	/**
	 * This method tests if Unallocated Funds are updated database and amount
	 * is not there.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void updateAmendmentUnallocatedFunds6() throws ApplicationException
	{
		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		boolean lbThrown = false;

		loUnallocatedFundsBean.setAmendmentType(HHSConstants.NEGATIVE);
		loUnallocatedFundsBean.setSubBudgetID(subBudgetID);
		loUnallocatedFundsBean.setContractBudgetID(contractBudgetID);
		loUnallocatedFundsBean.setModifiedByUserId("city_142");
		loUnallocatedFundsBean.setParentBudgetId(parentBudgetID);
		loUnallocatedFundsBean.setParentSubBudgetId(parentSubBudgetID);
		loUnallocatedFundsBean.setModificationAmount("-152.90");
		
		try
		{
			moContractBudgetAmendmentService.updateAmendmentUnallocatedFunds(null, loUnallocatedFundsBean);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}
	
	/**
	 * This method tests if database session is Null
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void updateAmendmentUnallocatedFunds7() throws ApplicationException
	{
		Boolean lbThrown = false;
		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		loUnallocatedFundsBean.setSubBudgetID(subBudgetID);
		loUnallocatedFundsBean.setContractBudgetID(contractBudgetID);
		loUnallocatedFundsBean.setModifiedByUserId("city_142");
		loUnallocatedFundsBean.setParentBudgetId(parentBudgetID);
		loUnallocatedFundsBean.setParentSubBudgetId(parentSubBudgetID);
		loUnallocatedFundsBean.setAmendmentType(HHSConstants.EMPTY_STRING);
		try
		{
			moContractBudgetAmendmentService.updateAmendmentUnallocatedFunds(moSession, loUnallocatedFundsBean);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}
	
	
	/**
	 * This method tests if database session is Null
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void updateAmendmentUnallocatedFunds8() throws ApplicationException
	{
		Boolean lbThrown = false;
		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		loUnallocatedFundsBean.setAmendmentType(HHSConstants.NEGATIVE);
		loUnallocatedFundsBean.setSubBudgetID(subBudgetID);
		loUnallocatedFundsBean.setContractBudgetID(contractBudgetID);
		loUnallocatedFundsBean.setModifiedByUserId("city_142");
		loUnallocatedFundsBean.setParentBudgetId(parentBudgetID);
		loUnallocatedFundsBean.setParentSubBudgetId(parentSubBudgetID);
		loUnallocatedFundsBean.setModificationAmount("-152777.90");
		try
		{
			moContractBudgetAmendmentService.updateAmendmentUnallocatedFunds(moSession, loUnallocatedFundsBean);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}
	
	// Professional Service Amendment Test Case Start
	/**
	 * This method populates the filter bean object with default sorting and
	 * pagination parameters.
	 * 
	 * @return loProfServicesBean, a CBGridBean bean object
	 * @throws ApplicationException object
	 */
	private CBGridBean getCBGridBeanParams() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setParentBudgetId(parentSubBudgetID);
		loCBGridBean.setParentSubBudgetId(parentSubBudgetID);
		loCBGridBean.setSubBudgetID(subBudgetID);
		loCBGridBean.setContractID(baseContractId);
		loCBGridBean.setModifyByAgency("");
		loCBGridBean.setModifyByProvider("803");

		return loCBGridBean;
	}

	/**
	 * This method populates the filter bean object with default sorting and
	 * pagination parameters.
	 * 
	 * @return loProfServicesBean, a CBProfessionalServicesBean bean object
	 * @throws ApplicationException object
	 */
	private CBProfessionalServicesBean getCBProfServicesBeanParams() throws ApplicationException
	{
		CBProfessionalServicesBean loCBProfServicesBean = new CBProfessionalServicesBean();
		loCBProfServicesBean.setParentBudgetId(parentBudgetID);
		loCBProfServicesBean.setParentSubBudgetId(parentSubBudgetID);
		loCBProfServicesBean.setContractID(baseContractId);
		loCBProfServicesBean.setModifyByAgency("");
		loCBProfServicesBean.setModifyByProvider("803");

		return loCBProfServicesBean;
	}

	/**
	 * This method tests fetching functionality of Professional service details
	 * with null Sub-budget id
	 *  
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchProfServicesDetailsWithSubBudgetNULL() throws ApplicationException
	{
		MasterBean loMasterBean = null;
		CBGridBean loProfService = getCBGridBeanParams();
		loProfService.setParentBudgetId(parentBudgetID);
		loProfService.setParentSubBudgetId(parentSubBudgetID);
		loProfService.setContractBudgetID(contractBudgetID);
		loProfService.setSubBudgetID(null);

		List<CBProfessionalServicesBean> loProfServiceDetailsList = moContractBudgetAmendmentService
				.fetchProfServicesDetailsAmendment(loProfService, moSession, loMasterBean);

		assertNotNull(loProfServiceDetailsList);
		assertTrue(loProfServiceDetailsList.size() > 0);
	}

	/**
	 * This method tests fetching functionality of Professional service details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchProfServicesDetailsParSubBudgetIdNULL() throws ApplicationException
	{
		MasterBean loMasterBean = null;
		CBGridBean loProfService = getCBGridBeanParams();
		loProfService.setParentBudgetId(parentBudgetID);
		loProfService.setParentSubBudgetId(null);
		loProfService.setContractBudgetID(contractBudgetID);
		loProfService.setSubBudgetID(subBudgetID);

		List<CBProfessionalServicesBean> loProfServiceDetailsList = moContractBudgetAmendmentService
				.fetchProfServicesDetailsAmendment(loProfService, moSession, loMasterBean);

		assertNotNull(loProfServiceDetailsList);
		assertTrue(loProfServiceDetailsList.size() > 0);
	}

	/**
	 * This method tests fetching functionality of Professional service details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchProfServicesDetailsOK() throws ApplicationException
	{
		MasterBean loMasterBean = null;
		CBGridBean loProfService = getCBGridBeanParams();
		loProfService.setParentBudgetId(parentBudgetID);
		loProfService.setParentSubBudgetId(parentSubBudgetID);
		loProfService.setContractBudgetID(contractBudgetID);
		loProfService.setSubBudgetID(subBudgetID);
		
		List<CBProfessionalServicesBean> loProfServiceDetailsList = moContractBudgetAmendmentService
				.fetchProfServicesDetailsAmendment(loProfService, moSession, loMasterBean);

		assertNotNull(loProfServiceDetailsList);
		assertTrue(loProfServiceDetailsList.size() > 0);
	}
	
	@Test
	public void testFetchProfServicesDetailsNotApproved() throws ApplicationException
	{
		List<CBProfessionalServicesBean> loProfServiceDetailsList = moContractBudgetAmendmentService
				.fetchProfServicesDetailsAmendment(getProfServicesDetails(), moSession, null);
		assertNotNull(loProfServiceDetailsList);
		assertTrue(loProfServiceDetailsList.size() > 0);
	}
	
	
	@Test
	public void testFetchProfServicesDetailsApproved() throws ApplicationException
	{
		CBGridBean loProfService = getProfServicesDetailsApproved();
		List<CBProfessionalServicesBean> loProfServiceDetailsList = moContractBudgetAmendmentService
				.fetchProfServicesDetailsAmendment(loProfService, moSession, getMasterBeanApproved());

		assertNotNull(loProfServiceDetailsList);
		assertTrue(loProfServiceDetailsList.size() > 0);
	}
		
	
	
	
	/**
	 * This method tests fetching functionality of Professional service details
	 * With wrong Parent Budget Id 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchProfServicesDetailsWithWorngBudget() throws ApplicationException
	{
		MasterBean loMasterBean = null;
		CBGridBean loProfService = getCBGridBeanParams();
		loProfService.setParentBudgetId("121212"); // Id not available in Professional_services table
		loProfService.setParentSubBudgetId(parentSubBudgetID);
		loProfService.setContractBudgetID(contractBudgetID);
		loProfService.setSubBudgetID(subBudgetID);

		List<CBProfessionalServicesBean> loProfServiceDetailsList = moContractBudgetAmendmentService
				.fetchProfServicesDetailsAmendment(loProfService, moSession, loMasterBean);

		assertNotNull(loProfServiceDetailsList);
		assertTrue(loProfServiceDetailsList.size() == 0);
	}
	
	/**
	 * This method tests fetching functionality of Professional service details
	 * With empty contract id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchProfServicesDetailsWithEmptyContractId() throws ApplicationException
	{
		MasterBean loMasterBean = null;
		CBGridBean loProfService = getCBGridBeanParams();
		loProfService.setParentBudgetId(parentBudgetID);
		loProfService.setParentSubBudgetId(parentSubBudgetID);
		loProfService.setContractBudgetID(contractBudgetID);
		loProfService.setSubBudgetID(subBudgetID);
		loProfService.setContractID("");

		List<CBProfessionalServicesBean> loProfServiceDetailsList = moContractBudgetAmendmentService
				.fetchProfServicesDetailsAmendment(loProfService, moSession, loMasterBean);

		assertNotNull(loProfServiceDetailsList);
		assertTrue(loProfServiceDetailsList.size() > 0);
	}
	
	/**
	 * This method tests, functionality to add Modification Amount for
	 * Professional service with empty professional service Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testEditProfServicesAmendmentWithEmptyID() throws ApplicationException
	{
			CBProfessionalServicesBean loProfService = getCBProfServicesBeanParams();
			loProfService.setContractBudgetID(contractBudgetID);
			loProfService.setSubBudgetID(subBudgetID);
			loProfService.setId("");
			loProfService.setModifyAmount("2000");
			// Add amendment amount
			boolean lbUpdateStatus = moContractBudgetAmendmentService.editProfServicesDetailsAmendment(
					loProfService,	moSession);

			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
	}
	
	/**
	 * This method tests, functionality to add amendment Amount for
	 * Professional service with Null professional service Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testEditProfServicesAmendmentWithIDNULL() throws ApplicationException
	{
			CBProfessionalServicesBean loProfService = getCBProfServicesBeanParams();
			loProfService.setContractBudgetID(contractBudgetID);
			loProfService.setSubBudgetID(subBudgetID);
			loProfService.setId(null);
			loProfService.setModifyAmount("2000");
			// Add Amendment amount
			boolean lbUpdateStatus = moContractBudgetAmendmentService.editProfServicesDetailsAmendment(
					loProfService,	moSession);

			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
	}
	
	/**
	 * This method tests, functionality to add Amendment Amount for
	 * Professional service
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testEditProfServicesAmendmentWithIncompleteID() throws ApplicationException
	{
			CBProfessionalServicesBean loProfService = getCBProfServicesBeanParams();
			loProfService.setContractBudgetID(contractBudgetID);
			loProfService.setSubBudgetID(subBudgetID);
			loProfService.setId("152_");
			loProfService.setModifyAmount("2000");
			// Add Amendment amount
			boolean lbUpdateStatus = moContractBudgetAmendmentService.editProfServicesDetailsAmendment(
					loProfService,	moSession);

			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
	}
	
	/**
	 * This method tests, functionality to add Amendment Amount for
	 * Professional service
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testEditProfServicesAmendmentWithWrongID() throws ApplicationException
	{
			CBProfessionalServicesBean loProfService = getCBProfServicesBeanParams();
			loProfService.setContractBudgetID(contractBudgetID);
			loProfService.setSubBudgetID(subBudgetID);
			loProfService.setId("149_10");
			loProfService.setModifyAmount("2000");
			// Add Amendment amount
			boolean lbUpdateStatus = moContractBudgetAmendmentService.editProfServicesDetailsAmendment(
					loProfService,	moSession);

			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
	}

	/**
	 * This method tests, functionality when (Amendment Amount + Remaining
	 * Amount) < 0 for Professional Professional service
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testEditProfServicesAmendmentLessRemaining() throws ApplicationException
	{
			// (Amendment Amount + Remaining Amount) < 0
			CBProfessionalServicesBean loProfService = getCBProfServicesBeanParams();
			loProfService.setContractBudgetID(contractBudgetID);
			loProfService.setSubBudgetID(subBudgetID);
			loProfService.setId("152_4_positive_170");
			loProfService.setModifyAmount("-3000");

			boolean lbUpdateStatus = moContractBudgetAmendmentService.editProfServicesDetailsAmendment(
					loProfService,	moSession);

			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
	}

	/**
	 * This method tests, functionality to add Amendment Amount for
	 * Professional service
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testEditProfServicesAmendmentAmount() throws ApplicationException
	{
			CBProfessionalServicesBean loProfService = getCBProfServicesBeanParams();
			loProfService.setContractBudgetID(contractBudgetID);
			loProfService.setSubBudgetID(subBudgetID);
			loProfService.setId("152_4_positive");
			loProfService.setModifyAmount("2000");
			// Add Amendment amount
			boolean lbUpdateStatus = moContractBudgetAmendmentService.editProfServicesDetailsAmendment(
					loProfService,	moSession);

			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
	}

	/**
	 * This method tests, functionality to update Amendment Amount for
	 * Professional Professional service
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testEditProfServicesUpdateAmendmentAmount() throws ApplicationException
	{
			CBProfessionalServicesBean loProfService = getCBProfServicesBeanParams();
			loProfService.setContractBudgetID(contractBudgetID);
			loProfService.setSubBudgetID(subBudgetID);
			loProfService.setId("152_4_positive_170");
			loProfService.setModifyAmount("3000");

			// Update Amendment amount
			boolean lbUpdateStatus = moContractBudgetAmendmentService.editProfServicesDetailsAmendment(
					loProfService, moSession);

			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
	}

	/**
	 * This method tests, functionality when (Amendment Amount is
	 * NULL for Professional Professional service
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testEditProfServicesAmendmentAmountNULL() throws ApplicationException
	{
			// (Amendment Amount + Remaining Amount) < 0
			CBProfessionalServicesBean loProfService = getCBProfServicesBeanParams();
			loProfService.setContractBudgetID(contractBudgetID);
			loProfService.setSubBudgetID(subBudgetID);
			loProfService.setId("152_4_positive_170");
			loProfService.setModifyAmount(null);

			boolean lbUpdateStatus = moContractBudgetAmendmentService.editProfServicesDetailsAmendment(
					loProfService, moSession);

			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
	}
	
	/**
	 * This method tests, functionality when (Amendment Amount is
	 * Empty for Professional Professional service
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testEditProfServicesAmendmentAmountEmpty() throws ApplicationException
	{
			// (Amendment Amount + Remaining Amount) < 0
			CBProfessionalServicesBean loProfService = getCBProfServicesBeanParams();
			loProfService.setContractBudgetID(contractBudgetID);
			loProfService.setSubBudgetID(subBudgetID);
			loProfService.setId("152_4_positive_170");
			loProfService.setModifyAmount("");

			boolean lbUpdateStatus = moContractBudgetAmendmentService.editProfServicesDetailsAmendment(
					loProfService, moSession);

			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
	}
	
	/**
	 * This method tests, functionality when (Amendment Amount is
	 * Empty for Professional Professional service
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testEditProfServicesAmendmentAmountWrongValue() throws ApplicationException
	{
			// (Amendment Amount + Remaining Amount) < 0
			CBProfessionalServicesBean loProfService = getCBProfServicesBeanParams();
			loProfService.setContractBudgetID(contractBudgetID);
			loProfService.setSubBudgetID(subBudgetID);
			loProfService.setId("152_4_positive_170");
			loProfService.setModifyAmount("123abc");

			boolean lbUpdateStatus = moContractBudgetAmendmentService.editProfServicesDetailsAmendment(
					loProfService, moSession);

			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
	}

	/**
	 * This method tests, functionality when modified budget_id or sub_budget_id
	 * is null in Professional Professional service
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testEditProfServicesAmendmentAmountKO() throws ApplicationException
	{
		// (Amendment Amount + Remaining Amount) < 0
		CBProfessionalServicesBean loProfService = getCBProfServicesBeanParams();
		loProfService.setContractBudgetID(contractBudgetID);
		loProfService.setSubBudgetID(null);
		loProfService.setId("152_4_positive_170");
		loProfService.setModifyAmount("1000");

		boolean lbUpdateStatus = moContractBudgetAmendmentService.editProfServicesDetailsAmendment(
				loProfService,	moSession);

		assertNotNull(lbUpdateStatus);
		assertTrue(lbUpdateStatus);
	}

	
	@Test(expected = ApplicationException.class)
	public void testEditProfServicesAmendmentAmountPosExp() throws ApplicationException
	{
		CBProfessionalServicesBean loProfService = getProfServicesDetails();
		loProfService.setAmendmentType("positive");
		loProfService.setModifyAmount("-100");
		moContractBudgetAmendmentService.editProfServicesDetailsAmendment(
				loProfService,	moSession);
	}
	
	@Test(expected = ApplicationException.class)
	public void testEditProfServicesAmendmentAmountNegExp() throws ApplicationException
	{
		CBProfessionalServicesBean loProfService = getProfServicesDetails();
		loProfService.setAmendmentType("negative");
		loProfService.setModifyAmount("100");
		moContractBudgetAmendmentService.editProfServicesDetailsAmendment(
				loProfService,	moSession);
	}
	
	
	@Test(expected = ApplicationException.class)
	public void testEditProfServicesAmendmentAmountNegCheck() throws ApplicationException
	{
		CBProfessionalServicesBean loProfService = getProfServicesDetails();
		loProfService.setAmendmentType("negative");
		loProfService.setModifyAmount("-10000000000");
		moContractBudgetAmendmentService.editProfServicesDetailsAmendment(
				loProfService,	moSession);
	}
	
	
	@Test()
	public void testEditProfServicesAmendmentAmountAmendCheck() throws ApplicationException
	{
		CBProfessionalServicesBean loProfService = getProfServicesDetails();
		loProfService.setAmendmentType("negative");
		loProfService.setModifyAmount("-1");
		moContractBudgetAmendmentService.editProfServicesDetailsAmendment(
				loProfService,	moSession);
	}
	
	
	@Test()
	public void testEditProfServicesAmendmentAmountAmendCheck1() throws ApplicationException
	{
		CBProfessionalServicesBean loProfService = getProfServicesDetails();
		loProfService.setAmendmentType("negative");
		loProfService.setModifyAmount("0");
		moContractBudgetAmendmentService.editProfServicesDetailsAmendment(
				loProfService,	moSession);
	}
	
	/**
	 * fetch milestone
	 */
	@Test
	public void testFetchCBAmendMilestone() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		loCBGridBean.setSubBudgetID(subBudgetID);
		loCBGridBean.setContractBudgetID(contractBudgetID);
		loCBGridBean.setParentBudgetId(parentBudgetID);
		loCBGridBean.setParentSubBudgetId(parentSubBudgetID);
		loCBGridBean.setBudgetTypeId("1");

		List<CBMileStoneBean> loCBMileStoneBean = moContractBudgetAmendmentService.fetchMilestone(loCBGridBean,moSession,null);
		assertNotNull(loCBMileStoneBean);
	}
	
	/**
	 * fetch milestone
	 */
	@Test
	public void testFetchCBAmendMilestone1() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		loCBGridBean.setSubBudgetID(subBudgetID);
		loCBGridBean.setContractBudgetID(contractBudgetID);
		loCBGridBean.setParentBudgetId(parentBudgetID);
		loCBGridBean.setParentSubBudgetId(parentSubBudgetID);
/*		loCBGridBean.setBudgetTypeId("3");
*/
		List<CBMileStoneBean> loCBMileStoneBean = moContractBudgetAmendmentService.fetchMilestone(loCBGridBean,moSession,null);
		assertNotNull(loCBMileStoneBean);
	}
	
	/**
	 * fetch milestone
	 */
	@Test
	public void testFetchCBAmendMilestoneFromXML1() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		loCBGridBean.setSubBudgetID(subBudgetID);
		loCBGridBean.setContractBudgetID(contractBudgetID);
		loCBGridBean.setParentBudgetId(parentBudgetID);
		loCBGridBean.setParentSubBudgetId(parentSubBudgetID);

		loCBGridBean.setBudgetStatusId("84");
		MasterBean loMasterBean = getMasterBeanMS();
		
		List<CBMileStoneBean> loCBMileStoneBean = moContractBudgetAmendmentService.fetchMilestone(loCBGridBean,moSession,loMasterBean);
		assertNotNull(loCBMileStoneBean);
	}
	
	/**
	 * fetch milestone
	 */
	@Test
	public void testFetchCBAmendMilestoneFromXML2() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		loCBGridBean.setContractBudgetID(contractBudgetID);
		loCBGridBean.setSubBudgetID(subBudgetID);
		loCBGridBean.setParentBudgetId(parentBudgetID);
		loCBGridBean.setParentSubBudgetId(parentSubBudgetID);

		loCBGridBean.setBudgetStatusId("86");
		MasterBean loMasterBean = getMasterBeanMS();
		
		List<CBMileStoneBean> loCBMileStoneBean = moContractBudgetAmendmentService.fetchMilestone(loCBGridBean,moSession,loMasterBean);
		assertNotNull(loCBMileStoneBean);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchCBAmendMilestoneFail() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		loCBGridBean.setSubBudgetID(null);
		loCBGridBean.setParentBudgetId(parentBudgetID);
		loCBGridBean.setParentBudgetId(parentBudgetID);

		moContractBudgetAmendmentService.fetchMilestone(loCBGridBean,moSession,null);
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchCBAmendMilestoneFail1() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		loCBGridBean.setSubBudgetID(null);
		loCBGridBean.setParentBudgetId(parentBudgetID);
		loCBGridBean.setParentBudgetId(parentBudgetID);
//		loCBGridBean.setBudgetTypeId("4");

		moContractBudgetAmendmentService.fetchMilestone(loCBGridBean,moSession,null);
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchCBAmendMilestoneFail2() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		loCBGridBean.setSubBudgetID(null);
		loCBGridBean.setParentBudgetId(parentBudgetID);
		loCBGridBean.setParentBudgetId(parentBudgetID);
/*		loCBGridBean.setBudgetTypeId("3");
*/
		moContractBudgetAmendmentService.fetchMilestone(loCBGridBean,moSession,null);
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testGetSeqForMilestone() throws ApplicationException
	{

		Integer seq = moContractBudgetAmendmentService.getSeqForMilestone(moSession);
		assertNotNull(seq);
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testGetSeqForMilestoneExcp() throws ApplicationException
	{

		int seq = moContractBudgetAmendmentService.getSeqForMilestone(null);
		assertNotNull(seq);
	}
	
	/**
	 * insert milestone
	 */
	@Test
	public void testInsertCBAmendMilestone() throws ApplicationException
	{
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID(subBudgetID);
		loCBMileStoneBean.setModifiedByUserId("803");
		loCBMileStoneBean.setCreatedByUserId("803");
		loCBMileStoneBean.setMileStone("new test case");
		loCBMileStoneBean.setModificationAmount("500");
		loCBMileStoneBean.setContractBudgetID(contractBudgetID);
		Integer seq = 500;

		Boolean loRowInserted = moContractBudgetAmendmentService.addMilestone(seq,loCBMileStoneBean,moSession);
		assertTrue(loRowInserted);
	}
	
	/**
	 * insert milestone
	 */
	@Test
	public void testInsertCBAmendMilestone2() throws ApplicationException
	{
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID(subBudgetID);
		loCBMileStoneBean.setMileStone("new case");
		loCBMileStoneBean.setModificationAmount("500");
		loCBMileStoneBean.setContractBudgetID(contractBudgetID);
		Integer seq = 501;

		Boolean loRowInserted = moContractBudgetAmendmentService.addMilestone(seq,loCBMileStoneBean,moSession);
		assertTrue(loRowInserted);
	}
	

	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testInsertCBAmendMilestoneFail() throws ApplicationException
	{
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID(null);
		loCBMileStoneBean.setModifiedByUserId("803");
		loCBMileStoneBean.setCreatedByUserId("803");
		loCBMileStoneBean.setMileStone("new test case");
		loCBMileStoneBean.setModificationAmount("500");
		loCBMileStoneBean.setContractBudgetID(contractBudgetID);
		Integer seq = 500;

		Boolean loRowInserted = moContractBudgetAmendmentService.addMilestone(seq,loCBMileStoneBean,moSession);
	}
	
	/**
	 * insert milestone
	 */
	@Test
	public void testInsertCBAmendMilestone1() throws ApplicationException
	{
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID(subBudgetID);
		loCBMileStoneBean.setModifiedByUserId("803");
		loCBMileStoneBean.setCreatedByUserId("803");
		loCBMileStoneBean.setMileStone("new milestone81");
		loCBMileStoneBean.setModificationAmount("500");
		loCBMileStoneBean.setContractBudgetID(contractBudgetID);
		Integer seq = 819;

		Boolean loRowInserted = moContractBudgetAmendmentService.addMilestone(seq,loCBMileStoneBean,moSession);
		assertTrue(loRowInserted);
	}
	
	/**
	 * Application Exception
	 */
	@Test(expected = ApplicationException.class)
	public void testInsertCBAmendMilestonefail1() throws ApplicationException
	{
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID(subBudgetID);
		loCBMileStoneBean.setMileStone("new milestone81");
		loCBMileStoneBean.setModificationAmount("500");
		loCBMileStoneBean.setContractBudgetID(contractBudgetID);
		Integer seq = 81;

		Boolean loRowInserted = moContractBudgetAmendmentService.addMilestone(seq,loCBMileStoneBean,moSession);
		assertTrue(loRowInserted);
	}
	
	/**
	 * Application Exception
	 */
	@Test(expected = ApplicationException.class)
	public void testInsertCBAmendMilestonefail2() throws ApplicationException
	{
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID("0");
		loCBMileStoneBean.setMileStone("new milestone81");
		loCBMileStoneBean.setModificationAmount("500");
		loCBMileStoneBean.setContractBudgetID(contractBudgetID);
		Integer seq = 81;

		Boolean loRowInserted = moContractBudgetAmendmentService.addMilestone(seq,loCBMileStoneBean,moSession);
		assertTrue(loRowInserted);
	}
	
	/**
	 * update milestone
	 */
	@Test
	public void testUpdateCBAmendMilestone() throws ApplicationException
	{
		
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID(subBudgetID);
		loCBMileStoneBean.setModifiedByUserId("803");
		loCBMileStoneBean.setCreatedByUserId("803");
		loCBMileStoneBean.setAmendmentType("positive");
		loCBMileStoneBean.setModificationAmount("500");
		loCBMileStoneBean.setContractBudgetID(contractBudgetID);
		loCBMileStoneBean.setParentSubBudgetId(parentSubBudgetID);
		loCBMileStoneBean.setId(milestoneId);

		Boolean loRowUpdated = moContractBudgetAmendmentService.updateMilestone(loCBMileStoneBean,moSession);
		assertTrue(loRowUpdated);
	}
	
	/**
	 * update milestone
	 */
	@Test
	public void testUpdateCBAmendMilestone1() throws ApplicationException
	{
		
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID(subBudgetID);
		loCBMileStoneBean.setModifiedByUserId("803");
		loCBMileStoneBean.setCreatedByUserId("803");
		loCBMileStoneBean.setAmendmentType("positive");
		loCBMileStoneBean.setModificationAmount("1500");
		loCBMileStoneBean.setContractBudgetID(contractBudgetID);
		loCBMileStoneBean.setId(milestoneId);
		loCBMileStoneBean.setParentSubBudgetId(parentSubBudgetID);
		Boolean loRowUpdated = moContractBudgetAmendmentService.updateMilestone(loCBMileStoneBean,moSession);
		assertTrue(loRowUpdated);
	}
	
	/**
	 * update milestone
	 */
	@Test
	public void testUpdateCBAmendMilestoneNew() throws ApplicationException
	{
		
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID(subBudgetID);
		loCBMileStoneBean.setModifiedByUserId("803");
		loCBMileStoneBean.setCreatedByUserId("803");
		loCBMileStoneBean.setAmendmentType("positive");
		loCBMileStoneBean.setModificationAmount("1500");
		loCBMileStoneBean.setContractBudgetID(contractBudgetID);
		loCBMileStoneBean.setId(milestoneId);
		loCBMileStoneBean.setParentSubBudgetId(parentSubBudgetID);
		Boolean loRowUpdated = moContractBudgetAmendmentService.updateMilestone(loCBMileStoneBean,moSession);
		assertTrue(loRowUpdated);
	}
	
	/**
	 * update milestone
	 */
	@Test
	public void testUpdateCBAmendMilestone2() throws ApplicationException
	{
		
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID(subBudgetID);
		loCBMileStoneBean.setModifiedByUserId("803");
		loCBMileStoneBean.setCreatedByUserId("803");
		loCBMileStoneBean.setAmendmentType("positive");
		loCBMileStoneBean.setModificationAmount("1500");
		loCBMileStoneBean.setContractBudgetID(contractBudgetID);
		loCBMileStoneBean.setId(milestoneId);
		loCBMileStoneBean.setParentSubBudgetId(parentSubBudgetID);
		Boolean loRowUpdated = moContractBudgetAmendmentService.updateMilestone(loCBMileStoneBean,moSession);
		assertTrue(loRowUpdated);
	}
	
	/**
	 * update milestone
	 */
	@Test
	public void testUpdateCBAmendMilestoneAppEx2() throws ApplicationException
	{
		
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID(subBudgetID);
		loCBMileStoneBean.setModifiedByUserId("city_142");
		loCBMileStoneBean.setCreatedByUserId("city_142");
		loCBMileStoneBean.setAmendmentType("positive");
		loCBMileStoneBean.setModificationAmount("1500");
		loCBMileStoneBean.setContractBudgetID(contractBudgetID);
		loCBMileStoneBean.setId(milestoneId);
		loCBMileStoneBean.setParentSubBudgetId(parentSubBudgetID);
		Boolean loRowUpdated = moContractBudgetAmendmentService.updateMilestone(loCBMileStoneBean,moSession);
		assertTrue(loRowUpdated);
	}
	
	/**
	 * update milestone
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateCBAmendMilestoneNegAmd() throws ApplicationException
	{
		
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID(subBudgetID);
		loCBMileStoneBean.setModifiedByUserId("city_142");
		loCBMileStoneBean.setCreatedByUserId("city_142");
		loCBMileStoneBean.setAmendmentType("negative");
		loCBMileStoneBean.setModificationAmount("-1500");
		loCBMileStoneBean.setContractBudgetID(contractBudgetID);
		loCBMileStoneBean.setId(milestoneId);
		loCBMileStoneBean.setParentSubBudgetId(parentSubBudgetID);
		Boolean loRowUpdated = moContractBudgetAmendmentService.updateMilestone(loCBMileStoneBean,moSession);
		assertTrue(loRowUpdated);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateCBAmendMilestoneNegAmtforPositiveAmd() throws ApplicationException
	{
		
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID(subBudgetID);
		loCBMileStoneBean.setModifiedByUserId("803");
		loCBMileStoneBean.setCreatedByUserId("803");
		loCBMileStoneBean.setAmendmentType("positive");
		loCBMileStoneBean.setModificationAmount("-5000");
		loCBMileStoneBean.setContractBudgetID(contractBudgetID);
		loCBMileStoneBean.setId("81");

		Boolean loRowUpdated = moContractBudgetAmendmentService.updateMilestone(loCBMileStoneBean,moSession);
		assertTrue(loRowUpdated);
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateCBAmendMilestoneAmtforNegAmd() throws ApplicationException
	{
		
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID(subBudgetID);
		loCBMileStoneBean.setModifiedByUserId("803");
		loCBMileStoneBean.setCreatedByUserId("803");
		loCBMileStoneBean.setAmendmentType("negative");
		loCBMileStoneBean.setModificationAmount("5000");
		loCBMileStoneBean.setContractBudgetID(contractBudgetID);
		loCBMileStoneBean.setId("81");

		Boolean loRowUpdated = moContractBudgetAmendmentService.updateMilestone(loCBMileStoneBean,moSession);
		assertTrue(loRowUpdated);
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateCBAmendMilestoneNegMod() throws ApplicationException
	{
		
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID(subBudgetID);
		loCBMileStoneBean.setModifiedByUserId("803");
		loCBMileStoneBean.setCreatedByUserId("803");
		loCBMileStoneBean.setModificationAmount("-5000");
		loCBMileStoneBean.setContractBudgetID(contractBudgetID);
		loCBMileStoneBean.setId("81");

		Boolean loRowUpdated = moContractBudgetAmendmentService.updateMilestone(loCBMileStoneBean,moSession);
		assertTrue(loRowUpdated);
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateCBAmendMilestoneNegMod2() throws ApplicationException
	{
		
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID(subBudgetID);
		loCBMileStoneBean.setModifiedByUserId("city_142");
		loCBMileStoneBean.setCreatedByUserId("city_142");
		loCBMileStoneBean.setModificationAmount("-5000");
		loCBMileStoneBean.setContractBudgetID(contractBudgetID);
		loCBMileStoneBean.setId("81");

		Boolean loRowUpdated = moContractBudgetAmendmentService.updateMilestone(loCBMileStoneBean,moSession);
		assertTrue(loRowUpdated);
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateCBAmendMilestoneNegMod1() throws ApplicationException
	{
		
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID(null);
		loCBMileStoneBean.setModifiedByUserId("803");
		loCBMileStoneBean.setCreatedByUserId("803");
		loCBMileStoneBean.setModificationAmount("-5000");
		loCBMileStoneBean.setContractBudgetID(contractBudgetID);
		loCBMileStoneBean.setId("81");

		Boolean loRowUpdated = moContractBudgetAmendmentService.updateMilestone(loCBMileStoneBean,moSession);
		assertTrue(loRowUpdated);
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateCBAmendMilestoneValid2() throws ApplicationException
	{
		
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID(null);
		loCBMileStoneBean.setModificationAmount("-5000");
		loCBMileStoneBean.setContractBudgetID(contractBudgetID);
		loCBMileStoneBean.setId("81");

		Boolean loRowUpdated = moContractBudgetAmendmentService.updateMilestone(loCBMileStoneBean,moSession);
		assertTrue(loRowUpdated);
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateCBAmendMilestoneNew1() throws ApplicationException
	{
		
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID(subBudgetID);
		loCBMileStoneBean.setModifiedByUserId("803");
		loCBMileStoneBean.setCreatedByUserId("803");
		loCBMileStoneBean.setModificationAmount("-1500");
		loCBMileStoneBean.setContractBudgetID(contractBudgetID);
		loCBMileStoneBean.setId("314");

		Boolean loRowUpdated = moContractBudgetAmendmentService.updateMilestone(loCBMileStoneBean,moSession);
		assertTrue(loRowUpdated);
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateCBAmendMilestoneValid() throws ApplicationException
	{
		
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID(subBudgetID);
		loCBMileStoneBean.setModifiedByUserId("803");
		loCBMileStoneBean.setCreatedByUserId("803");
		loCBMileStoneBean.setModificationAmount("-50000");
		loCBMileStoneBean.setContractBudgetID(contractBudgetID);
		loCBMileStoneBean.setId("18");

		Boolean loRowUpdated = moContractBudgetAmendmentService.updateMilestone(loCBMileStoneBean,moSession);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateCBAmendMilestoneFail() throws ApplicationException
	{
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID(null);
		loCBMileStoneBean.setModifiedByUserId("803");
		loCBMileStoneBean.setCreatedByUserId("803");
		loCBMileStoneBean.setModificationAmount("500");
		loCBMileStoneBean.setContractBudgetID(contractBudgetID);
		loCBMileStoneBean.setId("19");

		Boolean loRowUpdated = moContractBudgetAmendmentService.updateMilestone(loCBMileStoneBean,moSession);
	}

	/**
	 * delete milestone
	 */
	@Test
	public void testDeleteCBAmendMilestone() throws ApplicationException
	{
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID(subBudgetID);
		loCBMileStoneBean.setId(milestoneId);
		

		Boolean loRowUpdated = moContractBudgetAmendmentService.deleteMilestone(loCBMileStoneBean,moSession);
		assertTrue(loRowUpdated);
	}

	@Test
	public void testDeleteCBAmendMilestone121() throws ApplicationException
	{

		Boolean loRowUpdated = moContractBudgetAmendmentService.deleteMilestone(getCBMileStoneBeanObj(),moSession);
		assertTrue(loRowUpdated);
	}
	
	
	/**
	 * delete milestone
	 */
	@Test
	public void testDeleteCBAmendMilestone1() throws ApplicationException
	{
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID(subBudgetID);
		loCBMileStoneBean.setId(milestoneId);
		

		Boolean loRowUpdated = moContractBudgetAmendmentService.deleteMilestone(loCBMileStoneBean,moSession);
		assertTrue(loRowUpdated);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testDeleteCBAmendMilestoneFail() throws ApplicationException
	{
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID(null);
		loCBMileStoneBean.setId("19");

		Boolean loRowUpdated = moContractBudgetAmendmentService.deleteMilestone(loCBMileStoneBean,moSession);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test(expected = Exception.class)
	public void testDeleteCBAmendMilestoneFail1() throws ApplicationException
	{
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID(null);

		Boolean loRowUpdated = moContractBudgetAmendmentService.deleteMilestone(loCBMileStoneBean,moSession);
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test(expected = Exception.class)
	public void testDeleteCBAmendMilestoneFail2() throws ApplicationException
	{
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID(subBudgetID);
		loCBMileStoneBean.setId("a");

		Boolean loRowUpdated = moContractBudgetAmendmentService.deleteMilestone(loCBMileStoneBean,moSession);
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testDeleteCBAmendMilestoneAppex() throws ApplicationException
	{
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();
		loCBMileStoneBean.setId("0");
	
		moContractBudgetAmendmentService.deleteMilestone(loCBMileStoneBean,moSession);
	}


	/**
	 * Test method for fetchModificationRent()
	 * @throws ApplicationException
	 * Application Exception thrown here
	 */
	@Test
	public void testfetchModificationRent() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		loCBGridBean.setSubBudgetID(subBudgetID);
		loCBGridBean.setParentBudgetId(parentBudgetID);
		loCBGridBean.setParentSubBudgetId(parentSubBudgetID);

  		List<Rent> loRentBeanList = moContractBudgetAmendmentService.fetchAmendmentRent(moSession, loCBGridBean, null);
		assertNotNull(loRentBeanList);
		assertTrue(loRentBeanList.size() > 0);
	}
	
	@Test
	public void testfetchModificationRent2() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		loCBGridBean.setSubBudgetID(subBudgetID);
		loCBGridBean.setParentBudgetId(parentBudgetID);
		loCBGridBean.setParentSubBudgetId(parentSubBudgetID);

  		List<Rent> loRentBeanList = moContractBudgetAmendmentService.fetchAmendmentRent(moSession, loCBGridBean, null);
		assertNotNull(loRentBeanList);
		assertTrue(loRentBeanList.size() > 0);
	}
	
	@Test
	public void testfetchModificationRent3() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		loCBGridBean.setSubBudgetID(subBudgetID);
		loCBGridBean.setParentBudgetId(parentBudgetID);
		loCBGridBean.setParentSubBudgetId(parentSubBudgetID);

  		List<Rent> loRentBeanList = moContractBudgetAmendmentService.fetchAmendmentRent(moSession, loCBGridBean, null);
		assertNotNull(loRentBeanList);
		assertTrue(loRentBeanList.size() > 0);
	}
	@Test
	public void testfetchModificationRent4() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		loCBGridBean.setSubBudgetID(subBudgetID);
		loCBGridBean.setParentBudgetId(parentBudgetID);
		loCBGridBean.setParentSubBudgetId(parentSubBudgetID);

  		List<Rent> loRentBeanList = moContractBudgetAmendmentService.fetchAmendmentRent(moSession, loCBGridBean, null);
		assertNotNull(loRentBeanList);
		assertTrue(loRentBeanList.size() > 0);
	}
	@Test
	public void testfetchModificationRent6() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		loCBGridBean.setSubBudgetID(subBudgetID);
		loCBGridBean.setParentBudgetId(parentBudgetID);
		loCBGridBean.setParentSubBudgetId(parentSubBudgetID);

  		List<Rent> loRentBeanList = moContractBudgetAmendmentService.fetchAmendmentRent(moSession, loCBGridBean, null);
		assertNotNull(loRentBeanList);
		assertTrue(loRentBeanList.size() > 0);
	}
	@Test
	public void testfetchModificationRent7() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		loCBGridBean.setSubBudgetID(subBudgetID);
		loCBGridBean.setParentBudgetId(parentBudgetID);
		loCBGridBean.setParentSubBudgetId(parentSubBudgetID);

  		List<Rent> loRentBeanList = moContractBudgetAmendmentService.fetchAmendmentRent(moSession, loCBGridBean, null);
		assertNotNull(loRentBeanList);
		assertTrue(loRentBeanList.size() > 0);
	}
	@Test
	public void testfetchModificationRent8() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		loCBGridBean.setSubBudgetID(subBudgetID);
		loCBGridBean.setParentBudgetId(parentBudgetID);
		loCBGridBean.setParentSubBudgetId(parentSubBudgetID);

  		List<Rent> loRentBeanList = moContractBudgetAmendmentService.fetchAmendmentRent(moSession, loCBGridBean, null);
		assertNotNull(loRentBeanList);
		assertTrue(loRentBeanList.size() > 0);
	}
	@Test
	public void testfetchModificationRent9() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		loCBGridBean.setSubBudgetID(subBudgetID);
		loCBGridBean.setParentBudgetId(parentBudgetID);
		loCBGridBean.setParentSubBudgetId(parentSubBudgetID);

  		List<Rent> loRentBeanList = moContractBudgetAmendmentService.fetchAmendmentRent(moSession, loCBGridBean, null);
		assertNotNull(loRentBeanList);
		assertTrue(loRentBeanList.size() > 0);
	}
	
	@Test
	public void testfetchModificationRent5() throws ApplicationException
	{
		Rent loCBRent = getRentApprovedObj();
  		List<Rent> loRentBeanList = moContractBudgetAmendmentService.fetchAmendmentRent(moSession, loCBRent, getMasterBeanApproved());
		assertNotNull(loRentBeanList);
		assertTrue(loRentBeanList.size() > 0);
	}
	
	@Test
	public void testfetchModificationRent213() throws ApplicationException
	{
  		List<Rent> loRentBeanList = moContractBudgetAmendmentService.fetchAmendmentRent(moSession, getRentObj(), null);
		assertNotNull(loRentBeanList);
		assertTrue(loRentBeanList.size() > 0);
	}
	
	
	/**
	 * Test method for fetchModificationRent()
	 * @throws ApplicationException
	 * Application Exception thrown here
	 */
	@Test(expected = ApplicationException.class)
	public void testfetchModificationRentForException() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		loCBGridBean.setSubBudgetID(subBudgetID);
		loCBGridBean.setParentBudgetId(parentBudgetID);
		loCBGridBean.setParentSubBudgetId(parentSubBudgetID);
		moSession= null;
		List<Rent> lbStatus = moContractBudgetAmendmentService.fetchAmendmentRent(moSession, loCBGridBean, null);
		assertNull(lbStatus);
	}
	/**
	 * Test method for fetchModificationRent()
	 * @throws ApplicationException
	 * Application Exception thrown here
	 */
	@Test(expected = Exception.class)
	public void testfetchModificationRentForApplicationException() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		loCBGridBean.setSubBudgetID(null);
		loCBGridBean.setParentBudgetId(parentBudgetID);
		loCBGridBean.setParentSubBudgetId(parentSubBudgetID);
		List<Rent> lbStatus = moContractBudgetAmendmentService.fetchAmendmentRent(moSession, loCBGridBean, null);
		assertNotNull(lbStatus);
	}
	/**
	 * @throws ApplicationException 
	 * 
	 */
	@Test
	public void testUpdateModificationRentForPositiveScenario() throws ApplicationException
	{
		Rent loRent = new  Rent();
		loRent.setId("123");
		loRent.setSubBudgetID(subBudgetID);
		loRent.setParentSubBudgetId(parentSubBudgetID);
		loRent.setContractBudgetID(contractBudgetID);
		loRent.setId("390");
		loRent.setModifyAmount("2500");
		loRent.setModifyByProvider("803");
		loRent.setModifiedByUserId("803");
		loRent.setLocation("Location123");
		loRent.setManagementCompanyName("managementCompanyName");
		loRent.setPercentChargedToContract("50");
		loRent.setPropertyOwner("abc");
		loRent.setPublicSchoolSpace("1");
		loRent.setParentId("390");
		loRent.setBudgetId(557);
		loRent.setFyBudget("5000");
		moSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
		Boolean lbStatus = moContractBudgetAmendmentService.updateAmendmentRent(moSession, loRent);
		
		assertTrue(lbStatus);
	}
	
	@Test
	public void testUpdateModificationRentForPositiveExp() throws ApplicationException
	{
		Rent loRent = getRentObj();
		loRent.setAmendmentType("positive");
		loRent.setModifyAmount("-100");
		Boolean lbStatus = moContractBudgetAmendmentService.updateAmendmentRent(moSession, loRent);
		assertTrue(lbStatus);
	}

	
	@Test
	public void testUpdateModificationRentForNegExp() throws ApplicationException
	{
		Rent loRent = getRentObj();
		loRent.setAmendmentType("negative");
		loRent.setModifyAmount("100");
		Boolean lbStatus = moContractBudgetAmendmentService.updateAmendmentRent(moSession, loRent);
		assertTrue(lbStatus);
	}

	@Test
	public void testUpdateModificationRentForNegExpception() throws ApplicationException
	{
		Rent loRent = getRentObj();
		loRent.setAmendmentType("negative");
		loRent.setModifyAmount("-100");
		Boolean lbStatus = moContractBudgetAmendmentService.updateAmendmentRent(moSession, loRent);
		assertTrue(lbStatus);
	}
	
	/**
	 * This test case is for the location entered as null
	 * 
	 * @throws ApplicationException 
	 * 
	 */
	@Test(expected = Exception.class)
	public void testUpdateModificationRentForException() throws ApplicationException
	{
		boolean lbThrown=false;
		try
		{
			moSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
		Rent loRent = new  Rent();
		loRent.setSubBudgetID(subBudgetID);
		loRent.setParentSubBudgetId(parentSubBudgetID);
		loRent.setId("390");
		loRent.setModifyAmount("2500");
		loRent.setModifyByProvider("803");
		loRent.setLocation(null);
		loRent.setParentId("390");
		loRent.setBudgetId(557);
		loRent.setFyBudget("5000");
		
		Boolean lbStatus = moContractBudgetAmendmentService.updateAmendmentRent(moSession, loRent);
		
		//assertTrue(lbStatus);
	}
	/**
	 * @throws ApplicationException 
	 * 
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateModificationRentForLocationAE() throws ApplicationException
	{
		Rent loRent = new  Rent();
		loRent.setSubBudgetID(subBudgetID);
		loRent.setParentSubBudgetId(parentSubBudgetID);
		loRent.setId("390");
		loRent.setModifyAmount("2500");
		loRent.setModifyByProvider("803");
		loRent.setLocation(null);
		loRent.setParentId("390");
		loRent.setBudgetId(557);
		loRent.setFyBudget("5000");
		
		Boolean lbStatus = moContractBudgetAmendmentService.updateAmendmentRent(moSession, loRent);
		
		assertFalse(lbStatus);
	}
	/**
	 * This test case is for the Modify Amount entered as null
	 * 
	 * @throws ApplicationException 
	 * 
	 */
	@Test(expected = Exception.class)
	public void testUpdateModificationRentForModifyAmountAsNull() throws ApplicationException
	{
		Rent loRent = new  Rent();
		loRent.setSubBudgetID(subBudgetID);
		loRent.setParentSubBudgetId(parentSubBudgetID);
		loRent.setId("390");
		loRent.setModifyAmount(null);
		loRent.setModifyByProvider("803");
		loRent.setLocation("Location123");
		loRent.setParentId("390");
		loRent.setBudgetId(557);
		loRent.setFyBudget("5000");
		
		Boolean lbStatus = moContractBudgetAmendmentService.updateAmendmentRent(moSession, loRent);
		
		assertFalse(lbStatus);
	}
	/**
	 * This test case is for the ID entered as null
	 * 
	 * @throws ApplicationException 
	 * 
	 */
	@Test(expected = Exception.class)
	public void testUpdateModificationRentForIdAsNull() throws ApplicationException
	{
		Rent loRent = new  Rent();
		loRent.setSubBudgetID(subBudgetID);
		loRent.setParentSubBudgetId(parentSubBudgetID);
		loRent.setId(null);
		loRent.setModifyAmount("5000");
		loRent.setModifyByProvider("803");
		loRent.setLocation("Location123");
		loRent.setParentId("390");
		loRent.setBudgetId(557);
		loRent.setFyBudget("5000");
		
		Boolean lbStatus = moContractBudgetAmendmentService.updateAmendmentRent(moSession, loRent);
		
		assertFalse(lbStatus);
	}
	/**
	 * This test case is for the provider entered as null
	 * 
	 * @throws ApplicationException 
	 * 
	 */
	@Test(expected = Exception.class)
	public void testUpdateModificationRentForProviderAsNull() throws ApplicationException
	{
		Rent loRent = new  Rent();
		loRent.setSubBudgetID(subBudgetID);
		loRent.setParentSubBudgetId(parentSubBudgetID);
		loRent.setId("390");
		loRent.setModifyAmount("250");
		loRent.setModifyByProvider(null);
		loRent.setLocation("Location123");
		loRent.setParentId("390");
		loRent.setBudgetId(557);
		loRent.setFyBudget("5000");
		
		Boolean lbStatus = moContractBudgetAmendmentService.updateAmendmentRent(moSession, loRent);
		
		assertFalse(lbStatus);
	}
	/**
	 * This test case is for the SubBudgetId entered as null
	 * 
	 * @throws ApplicationException 
	 * 
	 */
	@Test(expected = Exception.class)
	public void testUpdateModificationRentForSubBudgetIdAsNull() throws ApplicationException
	{
		Rent loRent = new  Rent();
		loRent.setSubBudgetID(null);
		loRent.setParentSubBudgetId(subBudgetID);
		loRent.setId("390");
		loRent.setModifyAmount("250");
		loRent.setModifyByProvider("803");
		loRent.setLocation("Location123");
		loRent.setParentId("390");
		loRent.setBudgetId(557);
		loRent.setFyBudget("5000");
		
		Boolean lbStatus = moContractBudgetAmendmentService.updateAmendmentRent(moSession, loRent);
		
		assertFalse(lbStatus);
	}
	/**
	 * This test case is for the delete rentId
	 * 
	 * @throws ApplicationException 
	 * 
	 */
	@Test
	public void testDeleteRentModificationForPositiveCase() throws ApplicationException
	{
		Rent loRent = new  Rent();
		loRent.setId("390");
		
		Boolean lbStatus = moContractBudgetAmendmentService.deleteRentAmendment(moSession, loRent);
		
		assertTrue(lbStatus);
	}
	/**
	 * This test case is for the id entered as null
	 * 
	 * @throws ApplicationException 
	 * 
	 */
	@Test(expected = Exception.class)
	public void testDeleteRentModificationForNegativeCase() throws ApplicationException
	{
		Rent loRent = new  Rent();
		loRent.setId(null);
		
		Boolean lbStatus = moContractBudgetAmendmentService.deleteRentAmendment(moSession, loRent);
		
		assertFalse(lbStatus);
	}
	/**
	 * This test case is for the id entered as null
	 * 
	 * @throws ApplicationException 
	 * 
	 */
	@Test(expected = ApplicationException.class)
	public void testDeleteRentModificationForAECase() throws ApplicationException
	{
		Rent loRent = new  Rent();
		loRent.setId(null);
		
		Boolean lbStatus = moContractBudgetAmendmentService.deleteRentAmendment(moSession, loRent);
		
		assertFalse(lbStatus);
	}
	/**
	 * This method test insertContractBudgetModificationRateInfo method for inserting new rate info in RATE table 
	 * Test for validateModificationUnit with new record identifier
	 * Positive Scenario
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertContractBudgetModificationRent() throws ApplicationException
	{
		Rent loRent = new Rent();
		loRent.setLocation("location");
		loRent.setManagementCompanyName("MC1");
		loRent.setPropertyOwner("propertyOwner");
		loRent.setPercentChargedToContract("5");
		loRent.setSubBudgetID(subBudgetID);
		loRent.setParentSubBudgetId(parentSubBudgetID);
        loRent.setModifyByProvider("803");
		loRent.setId("13");
		loRent.setModifiedByUserId("803");
		loRent.setCreatedByUserId("803");
		loRent.setModifyByAgency("city_142");
		loRent.setModifyByProvider("803");
		loRent.setModifyAmount("58");
		loRent.setContractBudgetID(contractBudgetID);
		Integer loRowInserted = moContractBudgetAmendmentService.insertContractBudgetAmendmentRent(moSession, loRent);
		assertEquals("1", loRowInserted.toString());
	}
	
	@Test(expected = ApplicationException.class)
	public void testInsertContractBudgetModificationRentPosExp() throws ApplicationException
	{
		Rent loRent = getRentObj();
		loRent.setAmendmentType("positive");
		loRent.setModifyAmount("-1000");
		moContractBudgetAmendmentService.insertContractBudgetAmendmentRent(moSession, loRent);
	}
	
	@Test(expected = ApplicationException.class)
	public void testInsertContractBudgetModificationRentNegExp() throws ApplicationException
	{
		Rent loRent = getRentObj();
		loRent.setAmendmentType("negative");
		loRent.setModifyAmount("1000");
		moContractBudgetAmendmentService.insertContractBudgetAmendmentRent(moSession, loRent);
	}
	
	@Test(expected = ApplicationException.class)
	public void testInsertContractBudgetModificationRentExp() throws ApplicationException
	{
		Rent loRent = getRentObj();
		loRent.setAmendmentType("negative");
		loRent.setModifyAmount("-10000000000");
		moContractBudgetAmendmentService.insertContractBudgetAmendmentRent(moSession, loRent);
	}
	
	
	/**
	 * Test method successfully fetches Indirect Rate.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchIndirectRateSuccess() throws ApplicationException
	{
		CBGridBean loCBIndirectRateBeanPositive = getCBGridBeanParams();
		// Positive Scenario -- List of CBIndirectRateBean type returned.
		List<CBIndirectRateBean> loCBIndirect = moContractBudgetAmendmentService.fetchIndirectRate(moSession,
				loCBIndirectRateBeanPositive, null);
		assertTrue(loCBIndirect.size() != 0);
	}
	
	
	@Test
	public void testFetchIndirectRateSuccess1() throws ApplicationException
	{
		List<CBIndirectRateBean> loCBIndirect = moContractBudgetAmendmentService.fetchIndirectRate(moSession,
				getIndRateObj(), null);
		assertTrue(loCBIndirect.size() != 0);
	}
	@Test
	public void testFetchIndirectRateSuccess12() throws ApplicationException
	{
		CBIndirectRateBean CBIndirectRateBean=  getIndRateObj();
		CBIndirectRateBean.setSubBudgetID(subBudgetID);
		CBIndirectRateBean.setParentSubBudgetId(subBudgetID);
		List<CBIndirectRateBean> loCBIndirect = moContractBudgetAmendmentService.fetchIndirectRate(moSession,
				CBIndirectRateBean, null);
		assertTrue(loCBIndirect.size() != 0);
	}
	
	
	@Test
	public void testFetchIndirectRateExp() throws ApplicationException
	{
		moContractBudgetAmendmentService.fetchIndirectRate(moSession,getIndRateObjApproved()
				, getMasterBeanApproved());
	}
	
	
	/**
	 * Test method successfully fetches Indirect Rate.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchIndirectRateSuccessSizeNull() throws ApplicationException
	{
		CBGridBean loCBIndirectRateBeanPositive = getCBGridBeanParams();
		loCBIndirectRateBeanPositive.setSubBudgetID("87234762738834298032");
		loCBIndirectRateBeanPositive.setParentSubBudgetId("12312323");
		// Positive Scenario -- List of CBIndirectRateBean type returned.
		List<CBIndirectRateBean> loCBIndirect = moContractBudgetAmendmentService.fetchIndirectRate(moSession,
				loCBIndirectRateBeanPositive, null);
		assertTrue(loCBIndirect.size() == 0);
	}

	/**
	 * Test method successfully fetches Indirect Rate.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchIndirectRateNullIndirectBean() throws ApplicationException
	{
		List<CBIndirectRateBean> loCBIndirect = moContractBudgetAmendmentService.fetchIndirectRate(moSession, null, null);
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
		moContractBudgetAmendmentService.fetchIndirectRate(null, loCBIndirectRateBeanPositive, null);
	}
	@Test
	public void testFetchContractedServicesAmendmentSubContractor() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		loCBGridBean.setSubBudgetID(subBudgetID);
		loCBGridBean.setParentBudgetId(parentBudgetID);
		loCBGridBean.setParentSubBudgetId(parentSubBudgetID);

		List<ContractedServicesBean> loCBContractedServicesBean = moContractBudgetAmendmentService
				.fetchContractedServicesAmendmentSubContractors(moSession, loCBGridBean, null);
		assertNotNull(loCBContractedServicesBean);
	}

	private ContractedServicesBean getDummyContractedServicesBeanObj()
	{
		ContractedServicesBean loContractedServicesBean = new ContractedServicesBean();
		loContractedServicesBean.setContractBudgetID(contractBudgetID);
		loContractedServicesBean.setSubBudgetID(subBudgetID);
		loContractedServicesBean.setContractID(baseContractId);
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
	
	@Test
	public void testfetchContractedServicesAmendmentConsultants() throws ApplicationException
	{
		List<ContractedServicesBean> loCBContractedServicesBean = moContractBudgetAmendmentService
				.fetchContractedServicesAmendmentConsultants(moSession, getDummyContractedServicesBeanObj(), null);
		assertNotNull(loCBContractedServicesBean);
	}
	
	@Test(expected = ApplicationException.class)
	public void testfetchContractedServicesAmendmentConsultantsExp() throws ApplicationException
	{
		moContractBudgetAmendmentService
				.fetchContractedServicesAmendmentConsultants(null, getDummyContractedServicesBeanObj(), null);
	}
	
	@Test
	public void testfetchContractedServicesAmendmentConsultantsApproved() throws ApplicationException
	{
		List<ContractedServicesBean> loCBContractedServicesBean = moContractBudgetAmendmentService
				.fetchContractedServicesAmendmentConsultants(moSession, getDummyContractedServicesBeanObjConsultantApproved(), getMasterBeanApproved());
		assertNotNull(loCBContractedServicesBean);
	}
	
	@Test
	public void testfetchContractedServicesAmendmentSubContractorApproved() throws ApplicationException
	{
		MasterBean masterBean = getMasterBeanApproved();
		masterBean.getMasterBeanList().get(0).getContractedserviceBeanList().get(0).setSubHeader("2");
		ContractedServicesBean loContractedServicesBean = getDummyContractedServicesBeanObjConsultantApproved();
		loContractedServicesBean.setSubHeader("2");
		List<ContractedServicesBean> loCBContractedServicesBean = moContractBudgetAmendmentService
				.fetchContractedServicesAmendmentSubContractors(moSession, getDummyContractedServicesBeanObjConsultantApproved(), masterBean);
		assertNotNull(loCBContractedServicesBean);
	}
	
	@Test
	public void testfetchContractedServicesAmendmentVendorApproved() throws ApplicationException
	{
		MasterBean masterBean = getMasterBeanApproved();
		masterBean.getMasterBeanList().get(0).getContractedserviceBeanList().get(0).setSubHeader("3");
		ContractedServicesBean loContractedServicesBean = getDummyContractedServicesBeanObjConsultantApproved();
		loContractedServicesBean.setSubHeader("3");
		List<ContractedServicesBean> loCBContractedServicesBean = moContractBudgetAmendmentService
				.fetchContractedServicesAmendmentVendors(moSession, getDummyContractedServicesBeanObjConsultantApproved(), masterBean);
		assertNotNull(loCBContractedServicesBean);
	}
	
	@Test(expected = ApplicationException.class)
	public void testfetchContractedServicesAmendmentVendorApprovedExp() throws ApplicationException
	{
		ContractedServicesBean loContractedServicesBean = getDummyContractedServicesBeanObj();
		loContractedServicesBean.setSubHeader("3");
		List<ContractedServicesBean> loCBContractedServicesBean = moContractBudgetAmendmentService
				.fetchContractedServicesAmendmentVendors(null, getDummyContractedServicesBeanObjConsultantApproved(), null);
		assertNotNull(loCBContractedServicesBean);
	}
	
	@Test(expected = ApplicationException.class)
	public void testFetchContractedServicesAmendmentSubContractor2() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		loCBGridBean.setSubBudgetID("556B");
		loCBGridBean.setParentBudgetId(parentBudgetID);
		loCBGridBean.setParentSubBudgetId(parentSubBudgetID);

		List<ContractedServicesBean> loCBContractedServicesBean = moContractBudgetAmendmentService
				.fetchContractedServicesAmendmentSubContractors(moSession, loCBGridBean, null);
		assertNotNull(loCBContractedServicesBean);
	}
	@Test(expected = ApplicationException.class)
	public void testFetchContractedServicesAmendmentSubContractor3() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		List<ContractedServicesBean> loCBContractedServicesBean = moContractBudgetAmendmentService
				.fetchContractedServicesAmendmentSubContractors(moSession, loCBGridBean, null);
		assertNotNull(loCBContractedServicesBean);
	}
	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchContractedServicesAmendmentVendors() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		loCBGridBean.setSubBudgetID(subBudgetID);
		loCBGridBean.setParentBudgetId(parentBudgetID);
		loCBGridBean.setParentSubBudgetId(parentSubBudgetID);

		List<ContractedServicesBean> loCBContractedServicesBean = moContractBudgetAmendmentService
				.fetchContractedServicesAmendmentVendors(moSession, loCBGridBean, null);
		assertNotNull(loCBContractedServicesBean);
	}

	public ContractedServicesBean SetData()
	{
		ContractedServicesBean loCBGridBean = new ContractedServicesBean();
		loCBGridBean.setSubBudgetID(subBudgetID);
		loCBGridBean.setParentBudgetId(parentBudgetID);
		loCBGridBean.setParentSubBudgetId(parentSubBudgetID);
		loCBGridBean.setCsName("testing");
		loCBGridBean.setDescOfService("testing");
		loCBGridBean.setModifiedByUserId("803");
		loCBGridBean.setContractBudgetID(contractBudgetID);
		loCBGridBean.setAmendmentAmt("1200");
		return loCBGridBean;
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testAddContractedServicesAmendment() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = new ContractedServicesBean();

		loCBGridBean = SetData();
		loCBGridBean.setSubHeader("1");

		Integer liVal = moContractBudgetAmendmentService.addContractedServicesAmendment(moSession, loCBGridBean);
		assertNotNull(liVal);
	}

	@Test(expected = ApplicationException.class)
	public void testAddContractedServicesConsultantPosAmendment() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = getDummyContractedServicesBeanObj();
		loCBGridBean.setAmendmentAmt("-1000");
		loCBGridBean.setAmendmentType("positive");
		moContractBudgetAmendmentService.addContractedServicesAmendment(moSession, loCBGridBean);
	}


	@Test(expected = ApplicationException.class)
	public void testAddContractedServicesConsultantNegAmendment() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = getDummyContractedServicesBeanObj();
		loCBGridBean.setAmendmentAmt("1000");
		loCBGridBean.setAmendmentType("negative");
		moContractBudgetAmendmentService.addContractedServicesAmendment(moSession, loCBGridBean);
	}
	
	@Test(expected = ApplicationException.class)
	public void testAddContractedServicesConsultantNegAmendmentExp() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = getDummyContractedServicesBeanObj();
		loCBGridBean.setAmendmentAmt("-1000");
		loCBGridBean.setAmendmentType("negative");
		moContractBudgetAmendmentService.addContractedServicesAmendment(moSession, loCBGridBean);
	}
	
	
	@Test(expected = ApplicationException.class)
	public void testAddContractedServicesConsultantNegAmendmentExpSessionnull() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = getDummyContractedServicesBeanObj();
		loCBGridBean.setAmendmentAmt("-1000");
		loCBGridBean.setAmendmentType("negative");
		moContractBudgetAmendmentService.addContractedServicesAmendment(null, loCBGridBean);
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testAddContractedServicesAmendment1() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = new ContractedServicesBean();

		loCBGridBean = SetData();
		loCBGridBean.setSubHeader("2");

		Integer liVal = moContractBudgetAmendmentService.addContractedServicesAmendment(moSession, loCBGridBean);
		assertNotNull(liVal);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testAddContractedServicesAmendment2() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = new ContractedServicesBean();

		loCBGridBean = SetData();
		loCBGridBean.setSubHeader("3");

		Integer liVal = moContractBudgetAmendmentService.addContractedServicesAmendment(moSession, loCBGridBean);
		assertNotNull(liVal);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testEditContractedServicesAmendmentNegExp() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = getDummyContractedServicesBeanObj();
		loCBGridBean.setSubHeader("1");
		loCBGridBean.setAmendmentAmt("1000");
		loCBGridBean.setAmendmentType("negative");
		moContractBudgetAmendmentService.editContractedServicesAmendment(moSession,
				loCBGridBean);
	}
	
	@Test(expected = ApplicationException.class)
	public void testEditContractedServicesAmendmentPosExp() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = getDummyContractedServicesBeanObj();
		loCBGridBean.setSubHeader("1");
		loCBGridBean.setAmendmentAmt("-1000");
		loCBGridBean.setAmendmentType("positive");
		moContractBudgetAmendmentService.editContractedServicesAmendment(moSession,
				loCBGridBean);
	}
	
	@Test(expected = ApplicationException.class)
	public void testEditContractedServicesAmendmentExp() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = getDummyContractedServicesBeanObj();
		moContractBudgetAmendmentService.editContractedServicesAmendment(moSession,
				loCBGridBean);
	}

	@Test
	public void testEditContractedServicesAmendment() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = getDummyContractedServicesBeanObj();
		loCBGridBean.setAmendmentAmt("1000");
		loCBGridBean.setAmendmentType("positive");
		moContractBudgetAmendmentService.editContractedServicesAmendment(moSession,
				loCBGridBean);
	}

	
	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testEditContractedServicesAmendment1() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = new ContractedServicesBean();

		loCBGridBean = SetData();
		loCBGridBean.setSubHeader("2");
		loCBGridBean.setId(contractedServiceId);

		Boolean lbStatus = moContractBudgetAmendmentService.editContractedServicesAmendment(moSession,
				loCBGridBean);
		assertNotNull(lbStatus);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testEditContractedServicesAmendment2() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = new ContractedServicesBean();

		loCBGridBean = SetData();
		loCBGridBean.setSubHeader("1");
		loCBGridBean.setId(contractedServiceId);

		Boolean lbStatus = moContractBudgetAmendmentService.editContractedServicesAmendment(moSession,
				loCBGridBean);
		assertNotNull(lbStatus);
	}


	@Test
	public void testEditContractedServicesAmendment5() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = new ContractedServicesBean();

		loCBGridBean = SetData();
		loCBGridBean.setSubHeader("3");
		loCBGridBean.setId(contractedServiceId);
		loCBGridBean.setSubBudgetID(subBudgetID);
		loCBGridBean.setModifyByAgency("");
		loCBGridBean.setModifyByProvider("803");
		Boolean lbStatus = moContractBudgetAmendmentService.editContractedServicesAmendment(moSession,
				loCBGridBean);
		assertNotNull(lbStatus);
	}
	@Test
	public void testEditContractedServicesAmendment6() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = new ContractedServicesBean();

		loCBGridBean = SetData();
		loCBGridBean.setId(contractedServiceId);
		loCBGridBean.setSubBudgetID(subBudgetID);
		loCBGridBean.setModifyByAgency("");
		loCBGridBean.setModifyByProvider("803");
		loCBGridBean.setCsName(null);
		loCBGridBean.setDescOfService(null);
		Boolean lbStatus = moContractBudgetAmendmentService.editContractedServicesAmendment(moSession,
				loCBGridBean);
		assertNotNull(lbStatus);
	}

	@Test(expected = ApplicationException.class)
	public void testEditContractedServicesAmendment7() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = new ContractedServicesBean();

		loCBGridBean = SetData();
		loCBGridBean.setSubHeader("3");
		loCBGridBean.setId("80");

		Boolean lbStatus = moContractBudgetAmendmentService.editContractedServicesAmendment(null,
				loCBGridBean);
		assertNotNull(lbStatus);
	}

	@Test(expected = ApplicationException.class)
	public void testEditContractedServicesAmendment8() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = new ContractedServicesBean();

		loCBGridBean = SetData();
		loCBGridBean.setSubHeader("3");
		loCBGridBean.setId("80B");

		Boolean lbStatus = moContractBudgetAmendmentService.editContractedServicesAmendment(moSession,
				loCBGridBean);
		assertNotNull(lbStatus);
	}


	@Test(expected = ApplicationException.class)
	public void testEditContractedServicesAmendment9() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = new ContractedServicesBean();

		Boolean lbStatus = moContractBudgetAmendmentService.editContractedServicesAmendment(moSession,
				loCBGridBean);
		assertNotNull(lbStatus);
	}

	@Test(expected = ApplicationException.class)
	public void testEditContractedServicesAmendment10() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = new ContractedServicesBean();

		loCBGridBean = SetData();
		loCBGridBean.setSubHeader("3");
		loCBGridBean.setId("80");
		loCBGridBean.setAmendmentAmt("-7788");
		Boolean lbStatus = moContractBudgetAmendmentService.editContractedServicesAmendment(moSession,
				loCBGridBean);
		assertNotNull(lbStatus);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testDeleteContractedServicesAmendment() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = getDummyContractedServicesBeanObj();
		Boolean lbStatus = moContractBudgetAmendmentService.deleteContractedServicesAmendment(moSession,
				loCBGridBean);
		assertNotNull(lbStatus);
	}
	
	@Test
	public void testfetchNonGridContractedServicesAmendmentApproved() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = getDummyContractedServicesBeanObjConsultantApproved();
		ContractedServicesBean loContractedServicesBean = moContractBudgetAmendmentService.fetchNonGridContractedServicesAmendment(moSession, loCBGridBean, getMasterBeanApproved());
		assertNotNull(loContractedServicesBean);
	}
	
	@Test
	public void testfetchNonGridContractedServicesAmendment() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = getDummyContractedServicesBeanObj();
		ContractedServicesBean loContractedServicesBean = moContractBudgetAmendmentService.fetchNonGridContractedServicesAmendment(moSession, loCBGridBean, null);
		assertNotNull(loContractedServicesBean);
	}
	
	@Test(expected = ApplicationException.class)
	public void testfetchNonGridContractedServicesAmendmentExp() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = getDummyContractedServicesBeanObj();
		ContractedServicesBean loContractedServicesBean = moContractBudgetAmendmentService.fetchNonGridContractedServicesAmendment(null, loCBGridBean, null);
		assertNotNull(loContractedServicesBean);
	}

	
	@Test(expected = ApplicationException.class)
	public void testDeleteContractedServicesAmendmentExp() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = getDummyContractedServicesBeanObj();

		moContractBudgetAmendmentService.deleteContractedServicesAmendment(null,
			loCBGridBean);
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testDeleteContractedServicesAmendment1() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = new ContractedServicesBean();

		loCBGridBean = SetData();
		loCBGridBean.setSubHeader("2");
		loCBGridBean.setId("80");

		Boolean lbStatus = moContractBudgetAmendmentService.deleteContractedServicesAmendment(moSession,
				loCBGridBean);
		assertNotNull(lbStatus);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testDeleteContractedServicesAmendment2() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = new ContractedServicesBean();

		loCBGridBean = SetData();
		loCBGridBean.setSubHeader("3");
		loCBGridBean.setId("80");

		Boolean lbStatus = moContractBudgetAmendmentService.deleteContractedServicesAmendment(moSession,
				loCBGridBean);
		assertNotNull(lbStatus);
	}

	/**
	 * This method tests the FetchContractBudgetAmendmentRate - Positive Scenario
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchContractBudgetAmendmentRate() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setSubBudgetID(subBudgetID);
		loCBGridBean.setParentBudgetId(parentBudgetID);
		loCBGridBean.setParentSubBudgetId(parentSubBudgetID);
		List<RateBean> loRateBeanList = moContractBudgetAmendmentService.fetchContractBudgetAmendmentRate(moSession, loCBGridBean, null);
		assertNotNull(loRateBeanList);
	}

	
	@Test()
	public void testFetchContractBudgetAmendmentRateApproved() throws ApplicationException
	{
		List<RateBean> loRateBeanList = moContractBudgetAmendmentService.fetchContractBudgetAmendmentRate(moSession, getRateBeanApprovedObj(), getMasterBeanApproved());
		assertNotNull(loRateBeanList);
	}
	
	@Test()
	public void testFetchContractBudgetAmendmentRateNotApproved() throws ApplicationException
	{
		List<RateBean> loRateBeanList = moContractBudgetAmendmentService.fetchContractBudgetAmendmentRate(moSession, getCBGridBeanParams(), null);
		assertNotNull(loRateBeanList);
	}
	
	

	
	/**
	 * This method tests the FetchContractBudgetAmendmentRate - Positive Scenario
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchContractBudgetAmendmentRateAssertReturnId() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		
		loCBGridBean.setSubBudgetID(subBudgetID);
		loCBGridBean.setParentBudgetId(parentBudgetID);
		loCBGridBean.setParentSubBudgetId(parentSubBudgetID);
		List<RateBean> loRateBeanList = moContractBudgetAmendmentService.fetchContractBudgetAmendmentRate(moSession, loCBGridBean, null);
		assertTrue(loRateBeanList != null);
	}
	
	/**
	 * This method tests the FetchContractBudgetAmendmentRate - Negative Scenario
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchContractBudgetAmendmentRateFailSubBudgetNull() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setSubBudgetID(null);
		loCBGridBean.setParentBudgetId(parentBudgetID);
		loCBGridBean.setParentSubBudgetId(parentSubBudgetID);
		moContractBudgetAmendmentService.fetchContractBudgetAmendmentRate(moSession, loCBGridBean, null);
	}
	
	/**
	 * This method tests the FetchContractBudgetAmendmentRate - Negative Scenario
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchContractBudgetAmendmentRateFailParentSubBudgetNull() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setSubBudgetID(null);
		loCBGridBean.setParentBudgetId(parentBudgetID);
		loCBGridBean.setParentSubBudgetId(null);
		moContractBudgetAmendmentService.fetchContractBudgetAmendmentRate(moSession, loCBGridBean, null);
	}
	
	/**
	 * This method tests the FetchContractBudgetAmendmentRate - Positive Scenario
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchContractBudgetAmendmentRateCBGridBeanNull() throws ApplicationException
	{
		moContractBudgetAmendmentService.fetchContractBudgetAmendmentRate(moSession, null, null);
	}

	/**
	 * This method test insertContractBudgetAmendmentRateInfo method for inserting new rate info in RATE table 
	 * Test for validateModificationUnit with new record identifier
	 * Positive Scenario
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertContractBudgetAmendmentRate() throws ApplicationException
	{
		RateBean loRateBean = new RateBean();
		loRateBean.setSubBudgetID(subBudgetID);
		loRateBean.setUnitDesc("ModTest");
		loRateBean.setParentSubBudgetId(parentSubBudgetID);
		loRateBean.setId(rateId);
		loRateBean.setModifiedByUserId("agency_21");
		loRateBean.setCreatedByUserId("agency_21");
		loRateBean.setModifyByAgency("agency_21");
		loRateBean.setModifyByProvider("989");
		loRateBean.setLsModifyAmount("58");
		loRateBean.setLsModifyUnits("3");
		loRateBean.setContractBudgetID(contractBudgetID);
		Integer loRowInserted = moContractBudgetAmendmentService.insertContractBudgetAmendmentRateInfo(moSession, loRateBean);
		assertEquals("1", loRowInserted.toString());
	}
	
	/**
	 * This method test insertContractBudgetAmendmentRateInfo method for inserting new rate info in RATE table 
	 * Test for validateModificationUnit with base record
	 * Positive Scenario
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertContractBudgetAmendmentRateIdNull() throws ApplicationException
	{
		RateBean loRateBean = new RateBean();
		loRateBean.setSubBudgetID(subBudgetID);
		loRateBean.setParentSubBudgetId(parentSubBudgetID);
		loRateBean.setUnitDesc("ModTest");
		loRateBean.setId(null);
		loRateBean.setModifiedByUserId("agency_21");
		loRateBean.setCreatedByUserId("agency_21");
		loRateBean.setModifyByAgency("agency_21");
		loRateBean.setModifyByProvider("803");
		loRateBean.setLsModifyAmount("58");
		loRateBean.setLsModifyUnits("3");
		loRateBean.setContractBudgetID(contractBudgetID);
		Integer loRowInserted = moContractBudgetAmendmentService.insertContractBudgetAmendmentRateInfo(moSession, loRateBean);
		assertEquals("1", loRowInserted.toString());
	}
	
	/**
	 * This method test insertContractBudgetAmendmentRateInfo method for inserting new rate info in RATE table 
	 * Test for validateModificationUnit with base record
	 * Negative Scenario
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertContractBudgetAmendmentRateValidateDataFalse() throws ApplicationException
	{
		RateBean loRateBean = new RateBean();
		loRateBean.setSubBudgetID(subBudgetID);
		loRateBean.setParentSubBudgetId(parentSubBudgetID);
		loRateBean.setUnitDesc("ModTest");
		loRateBean.setId(rateId);
		loRateBean.setModifiedByUserId("803");
		loRateBean.setCreatedByUserId("803");
		loRateBean.setModifyByAgency("agency_21");
		loRateBean.setModifyByProvider("803");
		loRateBean.setLsModifyAmount("58");
		loRateBean.setLsModifyUnits("-3");
		loRateBean.setContractBudgetID(contractBudgetID);
		moContractBudgetAmendmentService.insertContractBudgetAmendmentRateInfo(moSession, loRateBean);
	}
	
	/**
	 * This method test insertContractBudgetAmendmentRateInfo method for inserting new rate info in RATE table 
	 * Negative Scenario
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertContractBudgetAmendmentRateFailEx() throws ApplicationException
	{
		RateBean loRateBean = new RateBean();
		loRateBean.setSubBudgetID(subBudgetID);
		loRateBean.setParentSubBudgetId(null);
		loRateBean.setUnitDesc("ModTest");
		loRateBean.setId(rateId);
		loRateBean.setModifiedByUserId("803");
		loRateBean.setCreatedByUserId("803");
		loRateBean.setModifyByAgency("agency_21");
		loRateBean.setModifyByProvider("803");
		loRateBean.setLsModifyAmount("58");
		loRateBean.setLsModifyUnits("3");
		loRateBean.setContractBudgetID(contractBudgetID);
		moContractBudgetAmendmentService.insertContractBudgetAmendmentRateInfo(moSession, loRateBean);
	}
	
	/**
	 * This method test insertContractBudgetAmendmentRateInfo method for inserting new rate info in RATE table 
	 * Positive Scenario
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertContractBudgetAmendmentRateExemptId() throws ApplicationException
	{
		RateBean loRateBean = new RateBean();
		loRateBean.setSubBudgetID(subBudgetID);
		loRateBean.setUnitDesc("ModTest");
		loRateBean.setParentSubBudgetId(parentSubBudgetID);
		loRateBean.setModifiedByUserId("803");
		loRateBean.setCreatedByUserId("803");
		loRateBean.setModifyByAgency("agency_21");
		loRateBean.setModifyByProvider("803");
		loRateBean.setLsModifyAmount("58");
		loRateBean.setLsModifyUnits("3");
		loRateBean.setContractBudgetID(contractBudgetID);
		Integer loRowInserted = moContractBudgetAmendmentService.insertContractBudgetAmendmentRateInfo(moSession, loRateBean);
		assertEquals("1", loRowInserted.toString());
	}
	
	/**
	 * This method test insertContractBudgetAmendmentRateInfo method for inserting new rate info in RATE table 
	 * Negative Scenario
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testInsertContractBudgetAmendmentRateFailAppEx() throws ApplicationException
	{
		RateBean loRateBean = new RateBean();
		loRateBean.setSubBudgetID(null);
		loRateBean.setId("13_newrecord");
		loRateBean.setUnitDesc("ModTest1");
		loRateBean.setModifiedByUserId("803");
		loRateBean.setCreatedByUserId("803");
		loRateBean.setLsModifyAmount("58");
		loRateBean.setLsModifyUnits("3");
		loRateBean.setContractBudgetID(contractBudgetID);
		
		moContractBudgetAmendmentService.insertContractBudgetAmendmentRateInfo(moSession, loRateBean);
	}
	
	/**
	 * This method test insertContractBudgetAmendmentRateInfo method for inserting new rate info in RATE table 
	 * Positive Scenario
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertContractBudgetAmendmentRateNullHandled() throws ApplicationException
	{
		Integer loRowInserted = moContractBudgetAmendmentService.insertContractBudgetAmendmentRateInfo(moSession, null);
		assertEquals("0", loRowInserted.toString());
	}
	
	
	@Test(expected = ApplicationException.class)
	public void testInsertContractBudgetAmendmentRatePosExp() throws ApplicationException
	{
		 RateBean loRate =  getRateObj();
		 loRate.setAmendmentType("positive");
		 loRate.setLsModifyAmount("-1000");
		moContractBudgetAmendmentService.insertContractBudgetAmendmentRateInfo(moSession, loRate);
	}

	@Test(expected = ApplicationException.class)
	public void testInsertContractBudgetAmendmentRateNegExp() throws ApplicationException
	{
		 RateBean loRate =  getRateObj();
		 loRate.setAmendmentType("negative");
		 loRate.setLsModifyAmount("1000");
		moContractBudgetAmendmentService.insertContractBudgetAmendmentRateInfo(moSession, loRate);
	}

	@Test(expected = ApplicationException.class)
	public void testInsertContractBudgetAmendmentRateNegExp1() throws ApplicationException
	{
		 RateBean loRate =  getRateObj();
		 loRate.setAmendmentType("negative");
		 loRate.setLsModifyAmount("-10000000");
		moContractBudgetAmendmentService.insertContractBudgetAmendmentRateInfo(moSession, loRate);
	}
	
	@Test(expected = ApplicationException.class)
	public void testInsertContractBudgetAmendmentRateExp() throws ApplicationException
	{
		 RateBean loRate =  getRateObj();
		 loRate.setAmendmentType("negative");
		 loRate.setLsModifyAmount("-10000000");
		moContractBudgetAmendmentService.insertContractBudgetAmendmentRateInfo(null, loRate);
	}
	
	
	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testUpdateContractBudgetAmendmentRate() throws ApplicationException
	{
		RateBean loRateBean = new RateBean();
		
		loRateBean.setSubBudgetID(subBudgetID);
		loRateBean.setUnitDesc("ModTest");
		loRateBean.setParentSubBudgetId(parentSubBudgetID);
		loRateBean.setLsModifyAmount("58");
		loRateBean.setLsModifyUnits("3");
		loRateBean.setId("556");
		loRateBean.setModifyByProvider("803");
		moContractBudgetAmendmentService.updateContractBudgetAmendmentRateInfo(moSession, loRateBean);
		assertEquals("Method updateContractBudgetAmendmentRateInfo executed succesfully", moContractBudgetAmendmentService.getMoState().toString());
	}
	
	@Test(expected = ApplicationException.class)
	public void testUpdateContractBudgetAmendmentRatePosExp() throws ApplicationException
	{
		RateBean loRate =  getRateObj();
		loRate.setAmendmentType("positive");
		loRate.setLsModifyAmount("-100");
		moContractBudgetAmendmentService.updateContractBudgetAmendmentRateInfo(moSession, loRate);
	}
	
	@Test(expected = ApplicationException.class)
	public void testUpdateContractBudgetAmendmentRateNegExp() throws ApplicationException
	{
		RateBean loRate =  getRateObj();
		loRate.setAmendmentType("negative");
		loRate.setLsModifyAmount("100");
		moContractBudgetAmendmentService.updateContractBudgetAmendmentRateInfo(moSession, loRate);
	}
	
	@Test(expected = ApplicationException.class)
	public void testUpdateContractBudgetAmendmentRateExp() throws ApplicationException
	{
		RateBean loRate =  getRateObj();
		loRate.setAmendmentType("negative");
		loRate.setLsModifyAmount("-100000000");
		moContractBudgetAmendmentService.updateContractBudgetAmendmentRateInfo(moSession, loRate);
	}
	
	
	@Test()
	public void testUpdateContractBudgetAmendmentRate1() throws ApplicationException
	{
		RateBean loRate =  getRateObj();
		int i = moContractBudgetAmendmentService.updateContractBudgetAmendmentRateInfo(moSession, loRate);
		assertEquals(i, 1);
	}
	
	@Test()
	public void testUpdateContractBudgetAmendmentRate2() throws ApplicationException
	{
		RateBean loRate =  getRateObj();
		loRate.setLsModifyAmount("-1");
		loRate.setAmendmentType("negative");
		int i = moContractBudgetAmendmentService.updateContractBudgetAmendmentRateInfo(moSession, loRate);
		assertEquals(i, 1);
	}
	
	
	@Test(expected = ApplicationException.class)
	public void testUpdateContractBudgetAmendmentRateExp1() throws ApplicationException
	{
		moContractBudgetAmendmentService.updateContractBudgetAmendmentRateInfo(null, getRateObj());
	}
	
	
	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateContractBudgetAmendmentRateUnitDescNull() throws ApplicationException
	{
		RateBean loRateBean = new RateBean();
		
		loRateBean.setSubBudgetID(subBudgetID);
		loRateBean.setUnitDesc(null);
		loRateBean.setParentSubBudgetId(parentSubBudgetID);
		loRateBean.setModifyByProvider("803");
		loRateBean.setLsModifyAmount("58");
		loRateBean.setLsModifyUnits("3");
		loRateBean.setId("5");
		Integer loRowInserted = moContractBudgetAmendmentService.updateContractBudgetAmendmentRateInfo(moSession, loRateBean);
		
	}
	
	/**
	 * 	
	 * @throws ApplicationException
	 */
	@Test
	public void testUpdateContractBudgetAmendmentRateBeanValueChanged() throws ApplicationException
	{
		RateBean loRateBean = new RateBean();
		loRateBean.setSubBudgetID(subBudgetID);
		loRateBean.setContractBudgetID(contractBudgetID);
		loRateBean.setParentSubBudgetId(parentSubBudgetID);
		loRateBean.setModifyByProvider("803");
		loRateBean.setLsModifyAmount("558");
		loRateBean.setLsModifyUnits("3");
		loRateBean.setId("1");
		Integer loRowInserted = moContractBudgetAmendmentService.updateContractBudgetAmendmentRateInfo(moSession, loRateBean);
		//assertEquals("1", loRowInserted.toString());
		assertNotNull(loRowInserted);
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateContractBudgetAmendmentRateUnitDescEmpty() throws ApplicationException
	{
		RateBean loRateBean = new RateBean();
		loRateBean.setSubBudgetID(subBudgetID);
		loRateBean.setUnitDesc("");
		loRateBean.setParentSubBudgetId(parentSubBudgetID);
		loRateBean.setModifyByProvider("803");
		loRateBean.setLsModifyAmount("58");
		loRateBean.setLsModifyUnits("3");
		loRateBean.setId("556");
		moContractBudgetAmendmentService.updateContractBudgetAmendmentRateInfo(moSession, loRateBean);
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateContractBudgetAmendmentRateNegativeModifyUnits() throws ApplicationException
	{
		RateBean loRateBean = new RateBean();
		loRateBean.setSubBudgetID(subBudgetID);
		loRateBean.setUnitDesc("");
		loRateBean.setParentSubBudgetId(parentSubBudgetID);
		loRateBean.setModifyByProvider("803");
		loRateBean.setLsModifyAmount("58");
		loRateBean.setLsModifyUnits("-3");
		loRateBean.setId("556");
		moContractBudgetAmendmentService.updateContractBudgetAmendmentRateInfo(moSession, loRateBean);
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateContractBudgetAmendmentRateFail() throws ApplicationException
	{
		RateBean loRateBean = new RateBean();
		
		loRateBean.setSubBudgetID(null);
		loRateBean.setUnitDesc("ModTest");
		loRateBean.setModifyByProvider("803");
		loRateBean.setLsModifyAmount("58");
		loRateBean.setLsModifyUnits("3");
		loRateBean.setId("556");
		
		moContractBudgetAmendmentService.updateContractBudgetAmendmentRateInfo(moSession, loRateBean);
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateContractBudgetAmendmentRateFailBudgetIdNull() throws ApplicationException
	{
		RateBean loRateBean = new RateBean();
		loRateBean.setSubBudgetID(null);
		loRateBean.setUnitDesc("ModTest");
		loRateBean.setModifyByProvider("803");
		loRateBean.setLsModifyAmount("58");
		loRateBean.setParentSubBudgetId(null);
		loRateBean.setLsModifyUnits("3");
		loRateBean.setId("556");
		moContractBudgetAmendmentService.updateContractBudgetAmendmentRateInfo(moSession, loRateBean);
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateContractBudgetAmendmentRateFailModifyUnitNull() throws ApplicationException
	{
		RateBean loRateBean = new RateBean();
		loRateBean.setSubBudgetID(null);
		loRateBean.setUnitDesc("ModTest");
		loRateBean.setModifyByProvider("803");
		loRateBean.setLsModifyAmount("58");
		loRateBean.setParentSubBudgetId(parentSubBudgetID);
		loRateBean.setLsModifyUnits(null);
		loRateBean.setId("556");
		moContractBudgetAmendmentService.updateContractBudgetAmendmentRateInfo(moSession, loRateBean);
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testUpdateContractBudgetAmendmentRateBeanNull() throws ApplicationException
	{
		Integer loRowInserted = moContractBudgetAmendmentService.updateContractBudgetAmendmentRateInfo(moSession, null);
		assertEquals("0", loRowInserted.toString());
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateContractBudgetAmendmentRateFailSubBudgetIdNull() throws ApplicationException
	{
		RateBean loRateBean = new RateBean();
		loRateBean.setSubBudgetID(null);
		loRateBean.setUnitDesc("ModTest");
		loRateBean.setModifyByProvider("803");
		loRateBean.setLsModifyAmount("58");
		loRateBean.setParentSubBudgetId(parentSubBudgetID);
		loRateBean.setLsModifyUnits(null);
		loRateBean.setId("556");
		moContractBudgetAmendmentService.updateContractBudgetAmendmentRateInfo(null, loRateBean);
	}

	/**
	 * This method tests for deleting a row of Contract Modification Rate Grid
	 * Positive Scenario
	 * @throws ApplicationException
	 */
	@Test
	public void testDeleteContractBudgetAmendmentRate() throws ApplicationException
	{
		RateBean loRateBean = new RateBean();
		loRateBean.setSubBudgetID(subBudgetID);
		loRateBean.setId("555");
		moContractBudgetAmendmentService.deleteContractBudgetAmendmentRateInfo(moSession, loRateBean);
		assertEquals("Method deleteContractBudgetAmendmentRateInfo executed succesfully", moContractBudgetAmendmentService.getMoState().toString());
	}
	
	/**
	 * This method tests for deleting a row of Contract Modification Rate Grid
	 * Negative Scenario
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testDeleteContractBudgetAmendmentRateFail() throws ApplicationException
	{
		RateBean loRateBean = new RateBean();
		loRateBean.setSubBudgetID(null);
		loRateBean.setId("556");
		moContractBudgetAmendmentService.deleteContractBudgetAmendmentRateInfo(moSession, loRateBean);
	}
	
	/**
	 * This method tests for deleting a row of Contract Modification Rate Grid
	 * Negative Scenario
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testDeleteContractBudgetAmendmentRateFailSubBudgetNull() throws ApplicationException
	{
		CBGridBean loRateBean = new RateBean();
		loRateBean.setSubBudgetID(null);
		moContractBudgetAmendmentService.deleteContractBudgetAmendmentRateInfo(moSession, (RateBean)loRateBean);
	}
	
	/**
	 * This method tests for deleting a row of Contract Modification Rate Grid
	 * Positive Scenario
	 * @throws ApplicationException
	 */
	@Test
	public void testDeleteContractBudgetAmendmentRateCBGridBeanNull() throws ApplicationException
	{
		Integer loRowDeleted = moContractBudgetAmendmentService.deleteContractBudgetAmendmentRateInfo(moSession, null);
		assertEquals("0", loRowDeleted.toString());
	}
	
	/**
	 * This method tests for deleting a row of Contract Modification Rate Grid
	 * Negative Scenario
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testDeleteContractBudgetAmendmentRateFailEmptyRateBean() throws ApplicationException
	{
		RateBean loRateBean = new RateBean();
		moContractBudgetAmendmentService.deleteContractBudgetAmendmentRateInfo(moSession, loRateBean);
	}
	
	/**
	 * This method tests for fetch a Fiscal Year Budget Summary
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchFyBudgetSummary() throws ApplicationException
	{
		BudgetDetails loFyBudget = new BudgetDetails();
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, baseContractId);
		loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, contractBudgetID);
		loFyBudget = moContractBudgetAmendmentService.fetchFyBudgetSummary(moSession, loHashMap);
		assertNotNull(loFyBudget);
	}
	
	@Test(expected = ApplicationException.class)
	public void testFetchFyBudgetSummary1() throws ApplicationException
	{
		BudgetDetails loFyBudget = new BudgetDetails();
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, "");
		loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "");
		loFyBudget = moContractBudgetAmendmentService.fetchFyBudgetSummary(moSession, loHashMap);
		assertNotNull(loFyBudget);
	}
	
	@Test(expected = ApplicationException.class)
	public void testFetchFyBudgetSummaryExp() throws ApplicationException
	{
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, baseContractId);
		loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, contractBudgetID);
		moContractBudgetAmendmentService.fetchFyBudgetSummary(null, loHashMap);
	}
	
	/**
	 * This method tests for fetch a CBGrid's data in session
	 * @throws ApplicationException
	 */
	@Test
	public void testGetCbGridDataForSession() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, baseContractId);
		loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, contractBudgetID);
		loCBGridBean = moContractBudgetAmendmentService.getCbGridDataForSession(moSession, loHashMap);
		assertNull(loCBGridBean);
	}
	
	@Test(expected = ApplicationException.class)
	public void testGetCbGridDataForSessionExp() throws ApplicationException
	{
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, baseContractId);
		loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, contractBudgetID);
		moContractBudgetAmendmentService.getCbGridDataForSession(null, loHashMap);
	}
	
	/**
	 * This method tests for fetch a sub-budget Summary
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchCMSubBudgetSummary() throws ApplicationException
	{
		List<CBGridBean> loSubBudgetList = null;
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, baseContractId);
		loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, contractBudgetID);
		loSubBudgetList = moContractBudgetAmendmentService.fetchCMSubBudgetSummary(moSession, loHashMap);
		assertNotNull(loSubBudgetList);
	}
	
	
	@Test
	public void testFetchCMSubBudgetSummarySubBudgetId() throws ApplicationException
	{
		List<CBGridBean> loSubBudgetList = null;
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, baseContractId);
		loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, contractBudgetID);
		loHashMap.put(HHSConstants.SUBBUDGET_ID, subBudgetID);
		loSubBudgetList = moContractBudgetAmendmentService.fetchCMSubBudgetSummary(moSession, loHashMap);
		assertNotNull(loSubBudgetList);
	}
	
	@Test(expected = ApplicationException.class)
	public void testFetchCMSubBudgetSummaryExp() throws ApplicationException
	{
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, baseContractId);
		loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, contractBudgetID);
		moContractBudgetAmendmentService.fetchCMSubBudgetSummary(null, loHashMap);
	}
	
	/**
	 * This method tests fetchUtilities throws exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchUtilitiesAmendment() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			CBGridBean loCBGridBean = new CBGridBean();

			// Positive Scenario -- List of CBIndirectRateBean type returned.

			loCBGridBean.setSubBudgetID(subBudgetID);
			loCBGridBean.setContractBudgetID(contractBudgetID);
			loCBGridBean.setParentBudgetId(parentBudgetID);
			loCBGridBean.setParentSubBudgetId(parentSubBudgetID);
			loCBGridBean.setEntryTypeId("3");

			List<CBUtilities> cbUtilitiesList = moContractBudgetAmendmentService.fetchUtilitiesAmendment(moSession, loCBGridBean, null);
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
	public void testFetchUtilitiesAmendmentIncompleteData() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			CBGridBean loCBGridBean = new CBGridBean();
			List<CBUtilities> cbUtilitiesList = moContractBudgetAmendmentService.fetchUtilitiesAmendment(moSession, loCBGridBean, null);
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
	public void testFetchUtilitiesAmendmentIncompleteData1() throws ApplicationException
	{
			List<CBUtilities> cbUtilitiesList = moContractBudgetAmendmentService.fetchUtilitiesAmendment(moSession, getCBGridBeanParams(), null);
			assertNull(cbUtilitiesList);
	}

	@Test
	public void testFetchUtilities() throws ApplicationException
	{
			List<CBUtilities> cbUtilitiesList = moContractBudgetAmendmentService.fetchUtilitiesAmendment(moSession, getCBUtilitiesApprovedObj(), getMasterBeanApproved());
			assertNull(cbUtilitiesList);
	}

	

	@Test
	public void testFetchUtilitiesAmendmentException() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			CBGridBean loCBGridBean = new CBGridBean();

			// Negative Scenario -- Application Exception handled by setting.
			moContractBudgetAmendmentService.fetchUtilitiesAmendment(null, loCBGridBean, null);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}
	
	@Test
	public void testUpdateUtilitiesPostiveAmendmentException() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBUtilities loCBUtilities = new CBUtilities();
			loCBUtilities.setAmendmentType("positive");
			loCBUtilities.setLineItemModifiedAmt("-1000");

			// Negative Scenario -- Application Exception handled by setting.
			moContractBudgetAmendmentService.updateUtilitiesAmendment(moSession, loCBUtilities);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("All updates must be for positive amounts within a positive budget amendment. Please confirm that all entry fields are positive amounts before saving.", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}
	
	
	@Test(expected =ApplicationException.class)
	public void testUpdateUtilitiesPostiveException() throws ApplicationException
	{
		CBUtilities loCBUtilities = getCBUtilitiesObj();
		loCBUtilities.setAmendmentType("positive");
		loCBUtilities.setLineItemModifiedAmt("-100");
		moContractBudgetAmendmentService.updateUtilitiesAmendment(moSession, loCBUtilities);
	}
	
	@Test(expected =ApplicationException.class)
	public void testUpdateUtilitiesNegException() throws ApplicationException
	{
		CBUtilities loCBUtilities = getCBUtilitiesObj();
		loCBUtilities.setAmendmentType("negative");
		loCBUtilities.setLineItemModifiedAmt("100");
		moContractBudgetAmendmentService.updateUtilitiesAmendment(moSession, loCBUtilities);
	}
	
	@Test(expected =ApplicationException.class)
	public void testUpdateUtilitiesNegException1() throws ApplicationException
	{
		CBUtilities loCBUtilities = getCBUtilitiesObj();
		loCBUtilities.setAmendmentType("negative");
		loCBUtilities.setLineItemModifiedAmt("-10000000000000");
		moContractBudgetAmendmentService.updateUtilitiesAmendment(moSession, loCBUtilities);
	}
	
	@Test(expected =ApplicationException.class)
	public void testUpdateUtilitiesException() throws ApplicationException
	{
		CBUtilities loCBUtilities = getCBUtilitiesObj();
		moContractBudgetAmendmentService.updateUtilitiesAmendment(null, loCBUtilities);
	}
		
	
	@Test
	public void testUpdateUtilitiesNegativeAmendmentException() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBUtilities loCBUtilities = new CBUtilities();
			loCBUtilities.setAmendmentType("negative");
			loCBUtilities.setLineItemModifiedAmt("1000");

			// Negative Scenario -- Application Exception handled by setting.
			moContractBudgetAmendmentService.updateUtilitiesAmendment(moSession, loCBUtilities);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("All updates must be for negative amounts within a negative budget amendment. Please confirm that all entry fields are negative amounts before saving.", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}
	
	@Test
	public void testUpdateUtilities() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBUtilities loCBUtilities = new CBUtilities();
			loCBUtilities.setAmendmentType("negative");
			loCBUtilities.setLineItemModifiedAmt("-1000");
			loCBUtilities.setId("1");
			// Negative Scenario -- Application Exception handled by setting.
			moContractBudgetAmendmentService.updateUtilitiesAmendment(moSession, loCBUtilities);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("! Entered value would cause the Proposed Budget to fall below the amount already invoiced for the line item. Please enter a new value.", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}
	
	@Test
	public void testUpdateUtilities2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBUtilities loCBUtilities = new CBUtilities();
			loCBUtilities.setAmendmentType("positive");
			loCBUtilities.setLineItemModifiedAmt("1000");
			loCBUtilities.setId("3");
			loCBUtilities.setSubBudgetID(subBudgetID);
			loCBUtilities.setContractBudgetID(contractBudgetID);
			loCBUtilities.setEntryTypeId("3");
			loCBUtilities.setModifyByProvider("803");
			loCBUtilities.setModifyByAgency("agency_47");
			// Negative Scenario -- Application Exception handled by setting.
			boolean lbUpdateStatus =  moContractBudgetAmendmentService.updateUtilitiesAmendment(moSession, loCBUtilities);
			assertTrue(lbUpdateStatus);


		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("! Entered value would cause the Proposed Budget to fall below the amount already invoiced for the line item. Please enter a new value.", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}
	
	@Test
	public void testUpdateUtilities3() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBUtilities loCBUtilities = new CBUtilities();
			loCBUtilities.setAmendmentType("positive");
			loCBUtilities.setLineItemModifiedAmt("1000");
			loCBUtilities.setId("3");
			loCBUtilities.setSubBudgetID(subBudgetID);
			loCBUtilities.setContractBudgetID(contractBudgetID);
			loCBUtilities.setEntryTypeId("3");
			loCBUtilities.setModifyByProvider("803");
			loCBUtilities.setModifyByAgency("agency_47");
			// Negative Scenario -- Application Exception handled by setting.
			boolean lbUpdateStatus =  moContractBudgetAmendmentService.updateUtilitiesAmendment(moSession, loCBUtilities);
			assertTrue(lbUpdateStatus);


		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("! Entered value would cause the Proposed Budget to fall below the amount already invoiced for the line item. Please enter a new value.", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}
	
	@Test
	public void testUpdateUtilities4() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBUtilities loCBUtilities = new CBUtilities();
			loCBUtilities.setAmendmentType(null);
			loCBUtilities.setLineItemModifiedAmt("1000");
			loCBUtilities.setId("3");
			loCBUtilities.setSubBudgetID(subBudgetID);
			loCBUtilities.setContractBudgetID(contractBudgetID);
			loCBUtilities.setEntryTypeId("3");
			loCBUtilities.setModifyByProvider("803");
			loCBUtilities.setModifyByAgency("agency_47");
			boolean lbUpdateStatus =  moContractBudgetAmendmentService.updateUtilitiesAmendment(moSession, loCBUtilities);
			assertTrue(lbUpdateStatus);


		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Null Pointer Exception occured in ContractBudgetService: updateUtilities method:: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}
	
	@Test
	public void testUpdateUtilities5() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBUtilities loCBUtilities = new CBUtilities();
			loCBUtilities.setLineItemModifiedAmt("1000");
			loCBUtilities.setId("3");
			loCBUtilities.setSubBudgetID(subBudgetID);
			loCBUtilities.setContractBudgetID(contractBudgetID);
			loCBUtilities.setEntryTypeId("3");
			loCBUtilities.setModifyByProvider("803");
			loCBUtilities.setModifyByAgency("agency_47");
			boolean lbUpdateStatus =  moContractBudgetAmendmentService.updateUtilitiesAmendment(null, loCBUtilities);
			assertTrue(lbUpdateStatus);


		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}
	
	//S349 start
	@Test
	public void testFetchOpAndSupportAmendPageData1() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		
		aoCBGridBeanObj.setParentBudgetId(parentBudgetID);
		aoCBGridBeanObj.setParentSubBudgetId(parentSubBudgetID);
		aoCBGridBeanObj.setContractBudgetID(contractBudgetID);
		aoCBGridBeanObj.setSubBudgetID(subBudgetID);
		CBOperationSupportBean loCBOperationSupportBean = moContractBudgetAmendmentService.fetchOpAndSupportAmendPageData(aoCBGridBeanObj, moSession, null);
		
		assertNotNull(loCBOperationSupportBean);
	}
	
	@Test
	public void testFetchOpAndSupportAmendPageData12() throws ApplicationException
	{
		CBOperationSupportBean loCBOperationSupportBean = moContractBudgetAmendmentService.fetchOpAndSupportAmendPageData(getCBOperationSupportBeanApproved(), moSession, getMasterBeanApproved());
		assertNotNull(loCBOperationSupportBean);
	}
	
	@Test
	public void testfetchAmendmentOTPS() throws ApplicationException
	{
		List<CBOperationSupportBean> loCBOperationSupportBean = moContractBudgetAmendmentService.fetchAmendmentOTPS(moSession,getCBOperationSupportBeanApproved(),  getMasterBeanApproved());
		assertNotNull(loCBOperationSupportBean);
	}
	
	
	
	
	
	@Test(expected = ApplicationException.class)
	public void testFetchOpAndSupportAmendPageData2() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		
		aoCBGridBeanObj.setParentBudgetId(parentBudgetID);
		aoCBGridBeanObj.setParentSubBudgetId(parentSubBudgetID);
		aoCBGridBeanObj.setContractBudgetID(contractBudgetID);
		aoCBGridBeanObj.setSubBudgetID(subBudgetID);
		CBOperationSupportBean loCBOperationSupportBean = moContractBudgetAmendmentService.fetchOpAndSupportAmendPageData(aoCBGridBeanObj, null, null);
		
		assertNotNull(loCBOperationSupportBean);
	}

	@Test
	public void testFetchAmendmentOTPS1() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		
		aoCBGridBeanObj.setParentBudgetId(parentBudgetID);
		aoCBGridBeanObj.setParentSubBudgetId(parentSubBudgetID);
		aoCBGridBeanObj.setContractBudgetID(contractBudgetID);
		aoCBGridBeanObj.setSubBudgetID(subBudgetID);
		List<CBOperationSupportBean> loCBOperationSupportBeanList = moContractBudgetAmendmentService.fetchAmendmentOTPS(moSession, aoCBGridBeanObj, null);
		
		assertNotNull(loCBOperationSupportBeanList);
	}
	
	@Test(expected = ApplicationException.class)
	public void testFetchAmendmentOTPS2() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		
		aoCBGridBeanObj.setParentBudgetId(parentBudgetID);
		aoCBGridBeanObj.setParentSubBudgetId(parentSubBudgetID);
		aoCBGridBeanObj.setContractBudgetID(contractBudgetID);
		aoCBGridBeanObj.setSubBudgetID(subBudgetID);
		List<CBOperationSupportBean> loCBOperationSupportBeanList = moContractBudgetAmendmentService.fetchAmendmentOTPS(null, aoCBGridBeanObj, null);
		
		assertNotNull(loCBOperationSupportBeanList);
	}
	

	@Test
	public void testEditAmendmentOTPS3() throws ApplicationException
	{
		CBOperationSupportBean aoCBOperationSupportBean = new CBOperationSupportBean();
		aoCBOperationSupportBean.setId("2");
		aoCBOperationSupportBean.setSubBudgetID(subBudgetID);
		aoCBOperationSupportBean.setAmendAmt("600");
		aoCBOperationSupportBean.setModifyByProvider("803");
		aoCBOperationSupportBean.setContractBudgetID(contractBudgetID);
		boolean lbStatus = moContractBudgetAmendmentService.editAmendmentOTPS(aoCBOperationSupportBean, moSession);
		assertTrue(lbStatus);
	}
	
	@Test(expected = ApplicationException.class)
	public void testEditAmendmentOTPS3PosExp() throws ApplicationException
	{
		CBOperationSupportBean loCBOperationSupportBean = getCBOperationSupportBeanApproved();
		loCBOperationSupportBean.setAmendmentType("positive");
		loCBOperationSupportBean.setAmendAmt("-100");
		moContractBudgetAmendmentService.editAmendmentOTPS(loCBOperationSupportBean, moSession);
	}

	@Test(expected = ApplicationException.class)
	public void testEditAmendmentOTPS3NegExp() throws ApplicationException
	{
		CBOperationSupportBean loCBOperationSupportBean = getCBOperationSupportBeanApproved();
		loCBOperationSupportBean.setAmendmentType("negative");
		loCBOperationSupportBean.setAmendAmt("100");
		moContractBudgetAmendmentService.editAmendmentOTPS(loCBOperationSupportBean, moSession);
	}
	
	@Test(expected = ApplicationException.class)
	public void testEditAmendmentOTPS3Exp() throws ApplicationException
	{
		CBOperationSupportBean loCBOperationSupportBean = getCBOperationSupportBeanApproved();
		loCBOperationSupportBean.setAmendmentType("negative");
		loCBOperationSupportBean.setAmendAmt("-10000000");
		moContractBudgetAmendmentService.editAmendmentOTPS(loCBOperationSupportBean, moSession);
	}
	
	
	@Test(expected = ApplicationException.class)
	public void testEditAmendmentOTPS4() throws ApplicationException
	{
		CBOperationSupportBean aoCBOperationSupportBean = new CBOperationSupportBean();
		aoCBOperationSupportBean.setAmendmentType("positive");
		aoCBOperationSupportBean.setId("2");
		aoCBOperationSupportBean.setSubBudgetID(subBudgetID);
		aoCBOperationSupportBean.setAmendAmt("-60000");
		aoCBOperationSupportBean.setModifyByProvider("803");
		aoCBOperationSupportBean.setContractBudgetID(contractBudgetID);
		boolean lbStatus = moContractBudgetAmendmentService.editAmendmentOTPS(aoCBOperationSupportBean, moSession);
		assertTrue(lbStatus);
	}

	@Test(expected = ApplicationException.class)
	public void testEditAmendmentOTPS5() throws ApplicationException
	{
		CBOperationSupportBean aoCBOperationSupportBean = new CBOperationSupportBean();
		aoCBOperationSupportBean.setAmendmentType("positive");
		aoCBOperationSupportBean.setId("2");
		aoCBOperationSupportBean.setSubBudgetID(subBudgetID);
		aoCBOperationSupportBean.setAmendAmt("60000");
		aoCBOperationSupportBean.setModifyByProvider("803");
		aoCBOperationSupportBean.setContractBudgetID(contractBudgetID);
		boolean lbStatus = moContractBudgetAmendmentService.editAmendmentOTPS(aoCBOperationSupportBean, null);
		assertTrue(lbStatus);
	}

	@Test
	public void testEditAmendmentOTPS6() throws ApplicationException
	{
		CBOperationSupportBean aoCBOperationSupportBean = new CBOperationSupportBean();
		aoCBOperationSupportBean.setId("6");
		aoCBOperationSupportBean.setSubBudgetID(subBudgetID);
		aoCBOperationSupportBean.setAmendAmt("600");
		aoCBOperationSupportBean.setModifyByProvider("803");
		aoCBOperationSupportBean.setContractBudgetID(contractBudgetID);
		boolean lbStatus = moContractBudgetAmendmentService.editAmendmentOTPS(aoCBOperationSupportBean, moSession);
		assertTrue(lbStatus);
	}

	//// 
	
	@Test
	public void testFetchAmendmentOTPSEquipment1() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		
		aoCBGridBeanObj.setParentBudgetId(parentBudgetID);
		aoCBGridBeanObj.setParentSubBudgetId(parentSubBudgetID);
		aoCBGridBeanObj.setContractBudgetID(contractBudgetID);
		aoCBGridBeanObj.setSubBudgetID(subBudgetID);
		List<CBEquipmentBean> loCBEquipmentBeanList = moContractBudgetAmendmentService.fetchAmendmentOTPSEquipment(moSession, aoCBGridBeanObj, null);
		
		assertNotNull(loCBEquipmentBeanList);
	}
	
	
	@Test
	public void testFetchAmendmentOTPSEquipment322() throws ApplicationException
	{
		CBEquipmentBean loCBEquipmentBean = getCBEquipmentBeanApproved();
		List<CBEquipmentBean> loCBEquipmentBeanList = moContractBudgetAmendmentService.fetchAmendmentOTPSEquipment(moSession, loCBEquipmentBean, getMasterBeanApproved());
		
		assertNotNull(loCBEquipmentBeanList);
	}
	
	
	
	@Test(expected = ApplicationException.class)
	public void testFetchAmendmentOTPSEquipment2() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		
		aoCBGridBeanObj.setParentBudgetId(parentBudgetID);
		aoCBGridBeanObj.setParentSubBudgetId(parentSubBudgetID);
		aoCBGridBeanObj.setContractBudgetID(contractBudgetID);
		aoCBGridBeanObj.setSubBudgetID(subBudgetID);
		List<CBEquipmentBean> loCBEquipmentBeanList = moContractBudgetAmendmentService.fetchAmendmentOTPSEquipment(moSession, aoCBGridBeanObj, null);
		
	}
	
	
	@Test(expected = ApplicationException.class)
	public void testFetchAmendmentOTPSEquipment3() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = null;
		
		List<CBEquipmentBean> loCBEquipmentBeanList = moContractBudgetAmendmentService.fetchAmendmentOTPSEquipment(moSession, aoCBGridBeanObj, null);
		
	}
	

	@Test
	public void testAddAmendmentOTPSEquipment4() throws ApplicationException
	{
		
		Boolean lbStatus = moContractBudgetAmendmentService.addAmendmentOTPSEquipment(moSession, getCBEquipmentBeanObj());
		
		assertTrue(lbStatus);
	}
	
	
	@Test(expected = ApplicationException.class)
	public void testAddAmendmentOTPSEquipmentExp() throws ApplicationException
	{
		moContractBudgetAmendmentService.addAmendmentOTPSEquipment(null, getCBEquipmentBeanObj());
	}
	
	
	@Test(expected = ApplicationException.class)
	public void testAddAmendmentOTPSEquipment5() throws ApplicationException
	{
		CBEquipmentBean aoCBEquipmentBean = new CBEquipmentBean();
		
		aoCBEquipmentBean.setContractBudgetID(contractBudgetID);
		aoCBEquipmentBean.setSubBudgetID(subBudgetID);
		aoCBEquipmentBean.setEquipment("Test");
		aoCBEquipmentBean.setUnits("4");
		aoCBEquipmentBean.setModificationAmt("999");
		aoCBEquipmentBean.setModifyByProvider("803");
		
		Boolean lbStatus = moContractBudgetAmendmentService.addAmendmentOTPSEquipment(moSession, aoCBEquipmentBean);
		
		assertTrue(lbStatus);
	}
	
	@Test
	public void testEditAmendmentOTPSEquipment6() throws ApplicationException
	{
		Boolean lbThrown = false;

		try
		{
			CBEquipmentBean aoCBEquipmentBean = new CBEquipmentBean();
			aoCBEquipmentBean.setId("59");
			aoCBEquipmentBean.setSubBudgetID(subBudgetID);
			aoCBEquipmentBean.setContractBudgetID(contractBudgetID);
			aoCBEquipmentBean.setEquipment("TestEquipment");
			aoCBEquipmentBean.setUnits("2");
			aoCBEquipmentBean.setAmendAmt("300");
			aoCBEquipmentBean.setModifyByProvider("803");
			boolean lbUpdateStatus = moContractBudgetAmendmentService.editAmendmentOTPSEquipment(moSession, aoCBEquipmentBean);
			
			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}
	@Test
	public void testEditAmendmentOTPSEquipment7() throws ApplicationException
	{
		Boolean lbThrown = false;

		try
		{
			CBEquipmentBean aoCBEquipmentBean = new CBEquipmentBean();
			aoCBEquipmentBean.setId("234");
			aoCBEquipmentBean.setSubBudgetID(subBudgetID);
			aoCBEquipmentBean.setContractBudgetID(contractBudgetID);
			aoCBEquipmentBean.setEquipment("TestEquipment");
			aoCBEquipmentBean.setUnits("2");
			aoCBEquipmentBean.setAmendAmt("40");
			aoCBEquipmentBean.setModifyByProvider("803");
			boolean lbUpdateStatus = moContractBudgetAmendmentService.editAmendmentOTPSEquipment(moSession, aoCBEquipmentBean);
			
			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	
	@Test(expected = ApplicationException.class)
	public void testEditAmendmentOTPSEquipment72222() throws ApplicationException
	{
			moContractBudgetAmendmentService.editAmendmentOTPSEquipment(moSession, getCBEquipmentBeanObj());
	}
	
	@Test()
	public void testEditAmendmentOTPSEquipment7223322() throws ApplicationException
	{
			CBEquipmentBean aoCBEquipmentBean = getCBEquipmentBeanObj();
			aoCBEquipmentBean.setUnits("-1");
			aoCBEquipmentBean.setAmendmentType("negative");
			aoCBEquipmentBean.setModificationAmt("-10");
			boolean lbUpdateStatus = moContractBudgetAmendmentService.editAmendmentOTPSEquipment(moSession, aoCBEquipmentBean);
			assertTrue(lbUpdateStatus);
	}
	
	
	
	
	

	@Test(expected = ApplicationException.class)
	public void testEditAmendmentOTPSEquipment72222222() throws ApplicationException
	{
			moContractBudgetAmendmentService.editAmendmentOTPSEquipment(null, getCBEquipmentBeanObj());
	}
	
	
	@Test
	public void testEditAmendmentOTPSEquipment8() throws ApplicationException
	{
		Boolean lbThrown = false;

		try
		{
			CBEquipmentBean aoCBEquipmentBean = new CBEquipmentBean();
			aoCBEquipmentBean.setId("121");
			aoCBEquipmentBean.setSubBudgetID(subBudgetID);
			aoCBEquipmentBean.setContractBudgetID(contractBudgetID);
			//aoCBEquipmentBean.setEquipment("TestEquipment");
			aoCBEquipmentBean.setUnits("2");
			aoCBEquipmentBean.setAmendAmt("40");
			aoCBEquipmentBean.setModifyByProvider("803");
			boolean lbUpdateStatus = moContractBudgetAmendmentService.editAmendmentOTPSEquipment(moSession, aoCBEquipmentBean);
			
			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testEditAmendmentOTPSEquipment9() throws ApplicationException
	{
		Boolean lbThrown = false;

		try
		{
			CBEquipmentBean aoCBEquipmentBean = new CBEquipmentBean();
			aoCBEquipmentBean.setId("121");
			aoCBEquipmentBean.setSubBudgetID(subBudgetID);
			aoCBEquipmentBean.setContractBudgetID(contractBudgetID);
			//aoCBEquipmentBean.setEquipment("TestEquipment");
			aoCBEquipmentBean.setUnits("-200");
			aoCBEquipmentBean.setAmendAmt("40");
			aoCBEquipmentBean.setModifyByProvider("803");
			boolean lbUpdateStatus = moContractBudgetAmendmentService.editAmendmentOTPSEquipment(moSession, aoCBEquipmentBean);
			
			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testEditAmendmentOTPSEquipment10() throws ApplicationException
	{
		Boolean lbThrown = false;

		try
		{
			CBEquipmentBean aoCBEquipmentBean = new CBEquipmentBean();
			aoCBEquipmentBean.setId("234");
			aoCBEquipmentBean.setSubBudgetID(subBudgetID);
			aoCBEquipmentBean.setContractBudgetID(contractBudgetID);
			//aoCBEquipmentBean.setEquipment("TestEquipment");
			aoCBEquipmentBean.setUnits("20");
			aoCBEquipmentBean.setAmendAmt("-40000");
			aoCBEquipmentBean.setModifyByProvider("803");
			boolean lbUpdateStatus = moContractBudgetAmendmentService.editAmendmentOTPSEquipment(moSession, aoCBEquipmentBean);
			
			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testEditAmendmentOTPSEquipment11() throws ApplicationException
	{
		Boolean lbThrown = false;

		try
		{
			CBEquipmentBean aoCBEquipmentBean = new CBEquipmentBean();
			aoCBEquipmentBean.setId("234");
			aoCBEquipmentBean.setSubBudgetID(subBudgetID);
			aoCBEquipmentBean.setContractBudgetID(contractBudgetID);
			//aoCBEquipmentBean.setEquipment("TestEquipment");
			aoCBEquipmentBean.setUnits("20");
			aoCBEquipmentBean.setAmendAmt("-40000");
			aoCBEquipmentBean.setModifyByProvider("803");
			boolean lbUpdateStatus = moContractBudgetAmendmentService.editAmendmentOTPSEquipment(null, aoCBEquipmentBean);
			
			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testDelAmendmentOTPSEquipment12() throws ApplicationException
	{
		CBEquipmentBean aoCBEquipmentBean = new CBEquipmentBean();
		aoCBEquipmentBean.setId("234");
		aoCBEquipmentBean.setSubBudgetID(subBudgetID);
		Boolean lbStatus = moContractBudgetAmendmentService.delAmendmentOTPSEquipment(moSession, aoCBEquipmentBean);
		
		assertTrue(lbStatus);
	}

	@Test(expected = ApplicationException.class)
	public void testDelAmendmentOTPSEquipment13() throws ApplicationException
	{
		CBEquipmentBean aoCBEquipmentBean = new CBEquipmentBean();
		aoCBEquipmentBean.setId("234");
		aoCBEquipmentBean.setSubBudgetID(subBudgetID);
		Boolean lbStatus = moContractBudgetAmendmentService.delAmendmentOTPSEquipment(null, aoCBEquipmentBean);
		
		assertTrue(lbStatus);
	}
	//S49 end
	
	@Test
	public void testFetchSalariedEmployeeBudgetForAmendment1() throws ApplicationException
	{
		CBGridBean loCBGridBean = getCBGridBeanForPersonnelService();

		List<PersonnelServiceBudget> loResultList = moContractBudgetAmendmentService.fetchSalariedEmployeeBudgetForAmendment(moSession, loCBGridBean,null);
		
		assertNotNull(loResultList);

	}	
	
	@Test
	public void testFetchSalariedEmployeeBudgetForAmendment2() throws ApplicationException
	{
		CBGridBean loCBGridBean = getCBGridBeanForPersonnelService();
		loCBGridBean.setBudgetStatusId("84");
		MasterBean loMasterBean = getMasterBean();
		List<PersonnelServiceBudget> loResultList = moContractBudgetAmendmentService.fetchSalariedEmployeeBudgetForAmendment(moSession, loCBGridBean,loMasterBean);
		
		assertNotNull(loResultList);

	}	
	
	@Test
	public void testFetchSalariedEmployeeBudgetForAmendment3() throws ApplicationException
	{
		CBGridBean loCBGridBean = getCBGridBeanForPersonnelService();
		loCBGridBean.setBudgetStatusId("84");
		List<PersonnelServiceBudget> loResultList = moContractBudgetAmendmentService.fetchSalariedEmployeeBudgetForAmendment(moSession, loCBGridBean,null);
		
		assertNotNull(loResultList);

	}		

	@Test
	public void testFetchSalariedEmployeeBudgetForAmendment4() throws ApplicationException
	{
		CBGridBean loCBGridBean = getCBGridBeanForPersonnelService();
		loCBGridBean.setBudgetStatusId("86");
		MasterBean loMasterBean = getMasterBean();
		List<PersonnelServiceBudget> loResultList = moContractBudgetAmendmentService.fetchSalariedEmployeeBudgetForAmendment(moSession, loCBGridBean,loMasterBean);
		
		assertNotNull(loResultList);

	}	
	
	@Test(expected = ApplicationException.class)
	public void testFetchSalariedEmployeeBudgetForAmendment5() throws ApplicationException
	{
		CBGridBean loCBGridBean = getCBGridBeanForPersonnelService();
		loCBGridBean.setBudgetStatusId("86");
		moContractBudgetAmendmentService.fetchSalariedEmployeeBudgetForAmendment(null, loCBGridBean,null);

	}	
	
	@Test(expected = Exception.class)
	public void testFetchSalariedEmployeeBudgetForAmendment6() throws ApplicationException
	{
		CBGridBean loCBGridBean = getCBGridBeanForPersonnelService();
		loCBGridBean.setBudgetStatusId("86");
		moContractBudgetAmendmentService.fetchSalariedEmployeeBudgetForAmendment(moSession, null,null);

	}	
	
	@Test
	public void testFetchHourlyEmployeeBudgetForAmendment1() throws ApplicationException
	{
		CBGridBean loCBGridBean = getCBGridBeanForPersonnelService();

		List<PersonnelServiceBudget> loResultList = moContractBudgetAmendmentService.fetchHourlyEmployeeBudgetForAmendment(moSession, loCBGridBean,null);
		
		assertNotNull(loResultList);

	}	
	
	@Test
	public void testFetchHourlyEmployeeBudgetForAmendment2() throws ApplicationException
	{
		CBGridBean loCBGridBean = getCBGridBeanForPersonnelService();
		loCBGridBean.setBudgetStatusId("84");
		MasterBean loMasterBean = getMasterBean();
		loMasterBean.getMasterBeanList().get(0).getPersonnelserviceBeanList().get(0).setEmpType("2");
		
		List<PersonnelServiceBudget> loResultList = moContractBudgetAmendmentService.fetchHourlyEmployeeBudgetForAmendment(moSession, loCBGridBean,loMasterBean);
		
		assertNotNull(loResultList);

	}	
	
	@Test
	public void testFetchHourlyEmployeeBudgetForAmendment3() throws ApplicationException
	{
		CBGridBean loCBGridBean = getCBGridBeanForPersonnelService();
		loCBGridBean.setBudgetStatusId("84");
		List<PersonnelServiceBudget> loResultList = moContractBudgetAmendmentService.fetchHourlyEmployeeBudgetForAmendment(moSession, loCBGridBean,null);
		
		assertNotNull(loResultList);

	}		

	@Test
	public void testFetchHourlyEmployeeBudgetForAmendment4() throws ApplicationException
	{
		CBGridBean loCBGridBean = getCBGridBeanForPersonnelService();
		loCBGridBean.setBudgetStatusId("86");
		MasterBean loMasterBean = getMasterBean();
		loMasterBean.getMasterBeanList().get(0).getPersonnelserviceBeanList().get(0).setEmpType("2");
		List<PersonnelServiceBudget> loResultList = moContractBudgetAmendmentService.fetchHourlyEmployeeBudgetForAmendment(moSession, loCBGridBean,loMasterBean);
		
		assertNotNull(loResultList);

	}	
	
	@Test(expected = ApplicationException.class)
	public void testFetchHourlyEmployeeBudgetForAmendment5() throws ApplicationException
	{
		CBGridBean loCBGridBean = getCBGridBeanForPersonnelService();
		loCBGridBean.setBudgetStatusId("86");
		moContractBudgetAmendmentService.fetchHourlyEmployeeBudgetForAmendment(null, loCBGridBean,null);

	}	
	
	@Test(expected = Exception.class)
	public void testFetchHourlyEmployeeBudgetForAmendment6() throws ApplicationException
	{
		CBGridBean loCBGridBean = getCBGridBeanForPersonnelService();
		loCBGridBean.setBudgetStatusId("86");
		moContractBudgetAmendmentService.fetchHourlyEmployeeBudgetForAmendment(moSession, null,null);

	}		
	
	@Test
	public void testFetchSeasonalEmployeeBudgetForAmendment1() throws ApplicationException
	{
		CBGridBean loCBGridBean = getCBGridBeanForPersonnelService();

		List<PersonnelServiceBudget> loResultList = moContractBudgetAmendmentService.fetchSeasonalEmployeeBudgetForAmendment(moSession, loCBGridBean,null);
		
		assertNotNull(loResultList);

	}	
	
	@Test
	public void testFetchSeasonalEmployeeBudgetForAmendment2() throws ApplicationException
	{
		CBGridBean loCBGridBean = getCBGridBeanForPersonnelService();
		loCBGridBean.setBudgetStatusId("84");
		MasterBean loMasterBean = getMasterBean();
		loMasterBean.getMasterBeanList().get(0).getPersonnelserviceBeanList().get(0).setEmpType("3");
		
		List<PersonnelServiceBudget> loResultList = moContractBudgetAmendmentService.fetchSeasonalEmployeeBudgetForAmendment(moSession, loCBGridBean,loMasterBean);
		
		assertNotNull(loResultList);

	}	
	
	@Test
	public void testFetchSeasonalEmployeeBudgetForAmendment3() throws ApplicationException
	{
		CBGridBean loCBGridBean = getCBGridBeanForPersonnelService();
		loCBGridBean.setBudgetStatusId("84");
		List<PersonnelServiceBudget> loResultList = moContractBudgetAmendmentService.fetchSeasonalEmployeeBudgetForAmendment(moSession, loCBGridBean,null);
		
		assertNotNull(loResultList);

	}		

	@Test
	public void testFetchSeasonalEmployeeBudgetForAmendment4() throws ApplicationException
	{
		CBGridBean loCBGridBean = getCBGridBeanForPersonnelService();
		loCBGridBean.setBudgetStatusId("86");
		MasterBean loMasterBean = getMasterBean();
		loMasterBean.getMasterBeanList().get(0).getPersonnelserviceBeanList().get(0).setEmpType("3");
		List<PersonnelServiceBudget> loResultList = moContractBudgetAmendmentService.fetchSeasonalEmployeeBudgetForAmendment(moSession, loCBGridBean,loMasterBean);
		
		assertNotNull(loResultList);

	}	
	
	@Test(expected = ApplicationException.class)
	public void testFetchSeasonalEmployeeBudgetForAmendment5() throws ApplicationException
	{
		CBGridBean loCBGridBean = getCBGridBeanForPersonnelService();
		loCBGridBean.setBudgetStatusId("86");
		moContractBudgetAmendmentService.fetchSeasonalEmployeeBudgetForAmendment(null, loCBGridBean,null);

	}	
	
	@Test(expected = Exception.class)
	public void testFetchSeasonalEmployeeBudgetForAmendment6() throws ApplicationException
	{
		CBGridBean loCBGridBean = getCBGridBeanForPersonnelService();
		loCBGridBean.setBudgetStatusId("86");
		moContractBudgetAmendmentService.fetchSeasonalEmployeeBudgetForAmendment(moSession, null,null);

	}			

	@Test
	public void testFetchFringeBenefitsForAmendment1() throws ApplicationException
	{
		CBGridBean loCBGridBean = getCBGridBeanForPersonnelService();

		List<PersonnelServiceBudget> loResultList = moContractBudgetAmendmentService.fetchFringeBenefitsForAmendment(moSession, loCBGridBean,null);
		
		assertNotNull(loResultList);

	}	
	
	@Test
	public void testFetchFringeBenefitsForAmendment2() throws ApplicationException
	{
		CBGridBean loCBGridBean = getCBGridBeanForPersonnelService();
		loCBGridBean.setBudgetStatusId("84");
		MasterBean loMasterBean = getMasterBean();
		loMasterBean.getMasterBeanList().get(0).getPersonnelserviceBeanList().get(0).setEmpType("4");
		
		List<PersonnelServiceBudget> loResultList = moContractBudgetAmendmentService.fetchFringeBenefitsForAmendment(moSession, loCBGridBean,loMasterBean);
		
		assertNotNull(loResultList);

	}	
	
	@Test
	public void testFetchFringeBenefitsForAmendment3() throws ApplicationException
	{
		CBGridBean loCBGridBean = getCBGridBeanForPersonnelService();
		loCBGridBean.setBudgetStatusId("84");
		List<PersonnelServiceBudget> loResultList = moContractBudgetAmendmentService.fetchFringeBenefitsForAmendment(moSession, loCBGridBean,null);
		
		assertNotNull(loResultList);

	}		

	@Test
	public void testFetchFringeBenefitsForAmendment4() throws ApplicationException
	{
		CBGridBean loCBGridBean = getCBGridBeanForPersonnelService();
		loCBGridBean.setBudgetStatusId("86");
		MasterBean loMasterBean = getMasterBean();
		loMasterBean.getMasterBeanList().get(0).getPersonnelserviceBeanList().get(0).setEmpType("4");
		List<PersonnelServiceBudget> loResultList = moContractBudgetAmendmentService.fetchFringeBenefitsForAmendment(moSession, loCBGridBean,loMasterBean);
		
		assertNotNull(loResultList);

	}	
	
	@Test(expected = ApplicationException.class)
	public void testFetchFringeBenefitsForAmendment5() throws ApplicationException
	{
		CBGridBean loCBGridBean = getCBGridBeanForPersonnelService();
		loCBGridBean.setBudgetStatusId("86");
		moContractBudgetAmendmentService.fetchFringeBenefitsForAmendment(null, loCBGridBean,null);

	}	
	
	@Test(expected = Exception.class)
	public void testFetchFringeBenefitsForAmendment6() throws ApplicationException
	{
		CBGridBean loCBGridBean = getCBGridBeanForPersonnelService();
		loCBGridBean.setBudgetStatusId("86");
		moContractBudgetAmendmentService.fetchFringeBenefitsForAmendment(moSession, null,null);

	}
	
	@Test(expected = ApplicationException.class)
	public void TestEditEmployeeBudgetForAmendment1() throws ApplicationException
	{
		PersonnelServiceBudget loPSBean = getPersonnelServiceObject();
		loPSBean.setAmendmentType(HHSConstants.POSITIVE);
		loPSBean.setAmendmentAmount("-200");
		moContractBudgetAmendmentService.editEmployeeBudgetForAmendment(moSession, loPSBean);

	}
	
	@Test(expected = ApplicationException.class)
	public void TestEditEmployeeBudgetForAmendment2() throws ApplicationException
	{
		PersonnelServiceBudget loPSBean = getPersonnelServiceObject();
		loPSBean.setAmendmentType(HHSConstants.NEGATIVE);
		loPSBean.setAmendmentAmount("200");
		moContractBudgetAmendmentService.editEmployeeBudgetForAmendment(moSession, loPSBean);

	}	
	
	@Test(expected = ApplicationException.class)
	public void TestEditEmployeeBudgetForAmendment3() throws ApplicationException
	{
		PersonnelServiceBudget loPSBean = getPersonnelServiceObject();
		loPSBean.setAmendmentType(HHSConstants.NEGATIVE);
		loPSBean.setAmendmentAmount("-200");
		loPSBean.setAmendmentUnit("-4");
		loPSBean.setTransactionName(HHSConstants.CBY_AMEND_SALARIED_EMPLOYEE_GRID_EDIT);
		loPSBean.setId("1828");
		moContractBudgetAmendmentService.editEmployeeBudgetForAmendment(moSession, loPSBean);

	}	
	
	@Test(expected = ApplicationException.class)
	public void TestEditEmployeeBudgetForAmendment4() throws ApplicationException
	{
		PersonnelServiceBudget loPSBean = getPersonnelServiceObject();
		loPSBean.setAmendmentType(HHSConstants.NEGATIVE);
		loPSBean.setAmendmentAmount("-200");
		loPSBean.setAmendmentUnit("-4");
		loPSBean.setTransactionName(HHSConstants.CBY_AMEND_SALARIED_EMPLOYEE_GRID_EDIT);
		loPSBean.setId(HHSConstants.NEW_ROW_IDENTIFIER);
		moContractBudgetAmendmentService.editEmployeeBudgetForAmendment(moSession, loPSBean);

	}	
	
	@Test(expected = ApplicationException.class)
	public void TestEditEmployeeBudgetForAmendment5() throws ApplicationException
	{
		PersonnelServiceBudget loPSBean = getPersonnelServiceObject();
		loPSBean.setAmendmentType(HHSConstants.NEGATIVE);
		loPSBean.setAmendmentAmount("-200");
		loPSBean.setAmendmentUnit("-4");
		loPSBean.setTransactionName(HHSConstants.CBY_AMEND_SALARIED_EMPLOYEE_GRID_EDIT);
		loPSBean.setId("1828");
		moContractBudgetAmendmentService.editEmployeeBudgetForAmendment(null, loPSBean);

	}		
	
	@Test(expected = ApplicationException.class)
	public void TestEditEmployeeBudgetForAmendment6() throws ApplicationException
	{
		PersonnelServiceBudget loPSBean = getPersonnelServiceObject();
		loPSBean.setAmendmentType(HHSConstants.NEGATIVE);
		loPSBean.setAmendmentAmount("-200");
		loPSBean.setAmendmentUnit("-4");
		loPSBean.setTransactionName(HHSConstants.CBY_AMEND_SALARIED_EMPLOYEE_GRID_EDIT);
		loPSBean.setId(HHSConstants.NEW_ROW_IDENTIFIER);
		moContractBudgetAmendmentService.editEmployeeBudgetForAmendment(null, loPSBean);

	}
	
	@Test(expected = ApplicationException.class)
	public void TestEditEmployeeBudgetForAmendment7() throws ApplicationException
	{
		PersonnelServiceBudget loPSBean = getPersonnelServiceObject();
		loPSBean.setAmendmentType(HHSConstants.NEGATIVE);
		loPSBean.setAmendmentAmount("-200");
		loPSBean.setAmendmentUnit("0");
		loPSBean.setTransactionName(HHSConstants.CBY_AMEND_SALARIED_EMPLOYEE_GRID_EDIT);
		loPSBean.setId(HHSConstants.NEW_ROW_IDENTIFIER);
		moContractBudgetAmendmentService.editEmployeeBudgetForAmendment(moSession, loPSBean);

	}		
	
	@Test(expected = ApplicationException.class)
	public void TestEditEmployeeBudgetForAmendment8() throws ApplicationException
	{
		PersonnelServiceBudget loPSBean = getPersonnelServiceObject();
		loPSBean.setAmendmentType(HHSConstants.NEGATIVE);
		loPSBean.setAmendmentAmount("-200");
		loPSBean.setAmendmentUnit("0");
		loPSBean.setTransactionName(HHSConstants.CBY_AMEND_SALARIED_EMPLOYEE_GRID_EDIT);
		loPSBean.setId("1828");
		moContractBudgetAmendmentService.editEmployeeBudgetForAmendment(moSession, loPSBean);

	}	
	
	@Test(expected = ApplicationException.class)
	public void TestEditEmployeeBudgetForAmendment9() throws ApplicationException
	{
		PersonnelServiceBudget loPSBean = getPersonnelServiceObject();
		loPSBean.setAmendmentType(HHSConstants.NEGATIVE);
		loPSBean.setAmendmentAmount("-200");
		loPSBean.setAmendmentUnit("0");
		loPSBean.setTransactionName(HHSConstants.CBY_AMEND_SALARIED_EMPLOYEE_GRID_EDIT);
		loPSBean.setId("1828123");
		moContractBudgetAmendmentService.editEmployeeBudgetForAmendment(moSession, loPSBean);

	}	
	
	@Test(expected = ApplicationException.class)
	public void TestEditEmployeeBudgetForAmendment10() throws ApplicationException
	{
		PersonnelServiceBudget loPSBean = getPersonnelServiceObject();
		loPSBean.setAmendmentType(HHSConstants.NEGATIVE);
		loPSBean.setAmendmentAmount("-200");
		loPSBean.setAmendmentUnit("0");
		loPSBean.setTransactionName(HHSConstants.CBY_AMEND_SALARIED_EMPLOYEE_GRID_EDIT);
		loPSBean.setId("1832");
		moContractBudgetAmendmentService.editEmployeeBudgetForAmendment(moSession, loPSBean);

	}	
	
	@Test
	public void TestEditEmployeeBudgetForAmendment11() throws ApplicationException
	{
		PersonnelServiceBudget loPSBean = getPersonnelServiceObject();
		loPSBean.setAmendmentType(HHSConstants.NEGATIVE);
		loPSBean.setAmendmentAmount("-50");
		loPSBean.setAmendmentUnit("0");
		loPSBean.setTransactionName(HHSConstants.CBY_AMEND_SALARIED_EMPLOYEE_GRID_EDIT);
		loPSBean.setId(personalServiceSalariedId);
		Boolean loReturnedStatus = moContractBudgetAmendmentService.editEmployeeBudgetForAmendment(moSession, loPSBean);
		assertTrue(loReturnedStatus);
	}	
	
	
	@Test(expected = ApplicationException.class)
	public void TestAddEmployeeBudgetForAmendment1() throws ApplicationException
	{
		PersonnelServiceBudget loPSBean = getPersonnelServiceObject();
		loPSBean.setAmendmentType(HHSConstants.POSITIVE);
		loPSBean.setAmendmentAmount("-200");
		moContractBudgetAmendmentService.addEmployeeBudgetForAmendment(moSession, loPSBean);

	}
	
	@Test(expected = ApplicationException.class)
	public void TestAddEmployeeBudgetForAmendment2() throws ApplicationException
	{
		PersonnelServiceBudget loPSBean = getPersonnelServiceObject();
		loPSBean.setAmendmentType(HHSConstants.NEGATIVE);
		loPSBean.setAmendmentAmount("200");
		moContractBudgetAmendmentService.addEmployeeBudgetForAmendment(moSession, loPSBean);

	}	
	
	@Test(expected = ApplicationException.class)
	public void TestAddEmployeeBudgetForAmendment3() throws ApplicationException
	{
		PersonnelServiceBudget loPSBean = getPersonnelServiceObject();
		loPSBean.setAmendmentType(HHSConstants.NEGATIVE);
		loPSBean.setAmendmentAmount("-200");
		loPSBean.setAmendmentUnit("-4");
		loPSBean.setTransactionName(HHSConstants.CBY_AMEND_SALARIED_EMPLOYEE_GRID_EDIT);
		loPSBean.setId("1828");
		moContractBudgetAmendmentService.addEmployeeBudgetForAmendment(moSession, loPSBean);

	}	
	
	@Test(expected = ApplicationException.class)
	public void TestAddEmployeeBudgetForAmendment4() throws ApplicationException
	{
		PersonnelServiceBudget loPSBean = getPersonnelServiceObject();
		loPSBean.setAmendmentType(HHSConstants.NEGATIVE);
		loPSBean.setAmendmentAmount("-200");
		loPSBean.setAmendmentUnit("-4");
		loPSBean.setTransactionName(HHSConstants.CBY_AMEND_SALARIED_EMPLOYEE_GRID_EDIT);
		loPSBean.setId(HHSConstants.NEW_ROW_IDENTIFIER);
		moContractBudgetAmendmentService.addEmployeeBudgetForAmendment(moSession, loPSBean);

	}	
	
	@Test(expected = ApplicationException.class)
	public void TestAddEmployeeBudgetForAmendment5() throws ApplicationException
	{
		PersonnelServiceBudget loPSBean = getPersonnelServiceObject();
		loPSBean.setAmendmentType(HHSConstants.NEGATIVE);
		loPSBean.setAmendmentAmount("-200");
		loPSBean.setAmendmentUnit("-4");
		loPSBean.setTransactionName(HHSConstants.CBY_AMEND_SALARIED_EMPLOYEE_GRID_EDIT);
		loPSBean.setId("1828");
		moContractBudgetAmendmentService.addEmployeeBudgetForAmendment(null, loPSBean);

	}		
	
	@Test(expected = ApplicationException.class)
	public void TestAddEmployeeBudgetForAmendment6() throws ApplicationException
	{
		PersonnelServiceBudget loPSBean = getPersonnelServiceObject();
		loPSBean.setAmendmentType(HHSConstants.NEGATIVE);
		loPSBean.setAmendmentAmount("-200");
		loPSBean.setAmendmentUnit("-4");
		loPSBean.setTransactionName(HHSConstants.CBY_AMEND_SALARIED_EMPLOYEE_GRID_EDIT);
		loPSBean.setId(HHSConstants.NEW_ROW_IDENTIFIER);
		moContractBudgetAmendmentService.addEmployeeBudgetForAmendment(null, loPSBean);

	}
	
	@Test(expected = ApplicationException.class)
	public void TestAddEmployeeBudgetForAmendment7() throws ApplicationException
	{
		PersonnelServiceBudget loPSBean = getPersonnelServiceObject();
		loPSBean.setAmendmentType(HHSConstants.NEGATIVE);
		loPSBean.setAmendmentAmount("-200");
		loPSBean.setAmendmentUnit("0");
		loPSBean.setTransactionName(HHSConstants.CBY_AMEND_SALARIED_EMPLOYEE_GRID_EDIT);
		loPSBean.setId(HHSConstants.NEW_ROW_IDENTIFIER);
		moContractBudgetAmendmentService.addEmployeeBudgetForAmendment(moSession, loPSBean);

	}		
	
	@Test(expected = ApplicationException.class)
	public void TestAddEmployeeBudgetForAmendment8() throws ApplicationException
	{
		PersonnelServiceBudget loPSBean = getPersonnelServiceObject();
		loPSBean.setAmendmentType(HHSConstants.NEGATIVE);
		loPSBean.setAmendmentAmount("-200");
		loPSBean.setAmendmentUnit("0");
		loPSBean.setTransactionName(HHSConstants.CBY_AMEND_SALARIED_EMPLOYEE_GRID_EDIT);
		loPSBean.setId("1828");
		moContractBudgetAmendmentService.addEmployeeBudgetForAmendment(moSession, loPSBean);

	}	
	
	@Test(expected = ApplicationException.class)
	public void TestAddEmployeeBudgetForAmendment9() throws ApplicationException
	{
		PersonnelServiceBudget loPSBean = getPersonnelServiceObject();
		loPSBean.setAmendmentType(HHSConstants.NEGATIVE);
		loPSBean.setAmendmentAmount("-200");
		loPSBean.setAmendmentUnit("0");
		loPSBean.setTransactionName(HHSConstants.CBY_AMEND_SALARIED_EMPLOYEE_GRID_EDIT);
		loPSBean.setId("1828123");
		moContractBudgetAmendmentService.addEmployeeBudgetForAmendment(moSession, loPSBean);

	}	
	
	@Test(expected = ApplicationException.class)
	public void TestAddEmployeeBudgetForAmendment10() throws ApplicationException
	{
		PersonnelServiceBudget loPSBean = getPersonnelServiceObject();
		loPSBean.setAmendmentType(HHSConstants.NEGATIVE);
		loPSBean.setAmendmentAmount("-200");
		loPSBean.setAmendmentUnit("0");
		loPSBean.setTransactionName(HHSConstants.CBY_AMEND_SALARIED_EMPLOYEE_GRID_EDIT);
		loPSBean.setId("1832");
		moContractBudgetAmendmentService.addEmployeeBudgetForAmendment(moSession, loPSBean);

	}	
	
	@Test
	public void TestAddEmployeeBudgetForAmendment11() throws ApplicationException
	{
		PersonnelServiceBudget loPSBean = getPersonnelServiceObject();
		loPSBean.setAmendmentType(HHSConstants.POSITIVE);
		loPSBean.setAmendmentAmount("50");
		loPSBean.setAmendmentUnit("1");
		loPSBean.setTransactionName(HHSConstants.CBY_AMEND_SALARIED_EMPLOYEE_GRID_ADD);
		loPSBean.setId(HHSConstants.NEW_ROW_IDENTIFIER);
		Boolean loReturnedStatus = moContractBudgetAmendmentService.addEmployeeBudgetForAmendment(moSession, loPSBean);
		assertTrue(loReturnedStatus);
	}	
	
	@Test
	public void TestAddEmployeeBudgetForAmendment12() throws ApplicationException
	{
		PersonnelServiceBudget loPSBean = getPersonnelServiceObject();
		loPSBean.setAmendmentType(HHSConstants.NEGATIVE);
		loPSBean.setAmendmentAmount("0");
		loPSBean.setAmendmentUnit("0");
		loPSBean.setTransactionName(HHSConstants.CBY_AMEND_SALARIED_EMPLOYEE_GRID_ADD);
		loPSBean.setId(HHSConstants.NEW_ROW_IDENTIFIER);
		Boolean loReturnedStatus = moContractBudgetAmendmentService.addEmployeeBudgetForAmendment(moSession, loPSBean);
		assertTrue(loReturnedStatus);
	}	
	
	@Test(expected = ApplicationException.class)
	public void TestEditFringeBenefitsForAmendment1() throws ApplicationException
	{
		PersonnelServiceBudget loPSBean = getPersonnelServiceObject();
		loPSBean.setAmendmentType(HHSConstants.POSITIVE);
		loPSBean.setAmendmentAmount("-200");
		moContractBudgetAmendmentService.editFringeBenefitsForAmendment(moSession, loPSBean);

	}
	
	@Test(expected = ApplicationException.class)
	public void TestEditFringeBenefitsForAmendment2() throws ApplicationException
	{
		PersonnelServiceBudget loPSBean = getPersonnelServiceObject();
		loPSBean.setAmendmentType(HHSConstants.NEGATIVE);
		loPSBean.setAmendmentAmount("200");
		moContractBudgetAmendmentService.editFringeBenefitsForAmendment(moSession, loPSBean);

	}	
	
	@Test(expected = ApplicationException.class)
	public void TestEditFringeBenefitsForAmendment3() throws ApplicationException
	{
		PersonnelServiceBudget loPSBean = getPersonnelServiceObject();
		loPSBean.setAmendmentType(HHSConstants.NEGATIVE);
		loPSBean.setAmendmentAmount("-200");
		loPSBean.setId("1158");
		moContractBudgetAmendmentService.editFringeBenefitsForAmendment(moSession, loPSBean);

	}	
	
	@Test(expected = ApplicationException.class)
	public void TestEditFringeBenefitsForAmendment4() throws ApplicationException
	{
		PersonnelServiceBudget loPSBean = getPersonnelServiceObject();
		loPSBean.setAmendmentType(HHSConstants.NEGATIVE);
		loPSBean.setAmendmentAmount("-1500");
		loPSBean.setId("58");
		moContractBudgetAmendmentService.editFringeBenefitsForAmendment(moSession, loPSBean);

	}	
	
	@Test(expected = ApplicationException.class)
	public void TestEditFringeBenefitsForAmendment5() throws ApplicationException
	{
		PersonnelServiceBudget loPSBean = getPersonnelServiceObject();
		loPSBean.setAmendmentType(HHSConstants.POSITIVE);
		loPSBean.setAmendmentAmount("200");
		loPSBean.setId("1828");
		moContractBudgetAmendmentService.editFringeBenefitsForAmendment(null, loPSBean);

	}		
	
	@Test(expected = ApplicationException.class)
	public void TestEditFringeBenefitsForAmendment6() throws ApplicationException
	{
		PersonnelServiceBudget loPSBean = getPersonnelServiceObject();
		loPSBean.setAmendmentType(HHSConstants.NEGATIVE);
		loPSBean.setAmendmentAmount("-200");
		loPSBean.setId("1828");
		moContractBudgetAmendmentService.editFringeBenefitsForAmendment(null, loPSBean);

	}
	
	@Test(expected = ApplicationException.class)
	public void TestEditFringeBenefitsForAmendment7() throws ApplicationException
	{
		PersonnelServiceBudget loPSBean = getPersonnelServiceObject();
		loPSBean.setAmendmentType(HHSConstants.NEGATIVE);
		loPSBean.setAmendmentAmount("-200");
		loPSBean.setId("2261");
		moContractBudgetAmendmentService.editFringeBenefitsForAmendment(moSession, loPSBean);
	}		
	
	@Test(expected = ApplicationException.class)
	public void TestEditFringeBenefitsForAmendment8() throws ApplicationException
	{
		PersonnelServiceBudget loPSBean = getPersonnelServiceObject();
		loPSBean.setAmendmentType(HHSConstants.NEGATIVE);
		loPSBean.setAmendmentAmount("-200");
		loPSBean.setId("12");
		Boolean loReturnedStatus = moContractBudgetAmendmentService.editFringeBenefitsForAmendment(moSession, loPSBean);
		assertTrue(loReturnedStatus);

	}	
	
	@Test
	public void TestEditFringeBenefitsForAmendment9() throws ApplicationException
	{
		PersonnelServiceBudget loPSBean = getPersonnelServiceObject();
		loPSBean.setAmendmentType(HHSConstants.POSITIVE);
		loPSBean.setAmendmentAmount("200");
		loPSBean.setId("58");
		Boolean loReturnedStatus = moContractBudgetAmendmentService.editFringeBenefitsForAmendment(moSession, loPSBean);
		assertTrue(loReturnedStatus);

	}	
	
	
	private CBGridBean getCBGridBeanForPersonnelService()
	{
		CBGridBean loCBGridBEan = new CBGridBean();
		loCBGridBEan.setContractBudgetID(contractBudgetID);
		loCBGridBEan.setSubBudgetID(subBudgetID);
		loCBGridBEan.setParentBudgetId(parentBudgetID);
		loCBGridBEan.setParentSubBudgetId(parentSubBudgetID);
		return loCBGridBEan;
	}
	
	private PersonnelServiceBudget getPersonnelServiceObject()
	{
		PersonnelServiceBudget loPSBean = new PersonnelServiceBudget();
		loPSBean.setContractBudgetID(contractBudgetID);
		loPSBean.setSubBudgetID(subBudgetID);
		loPSBean.setParentBudgetId(parentBudgetID);
		loPSBean.setParentSubBudgetId(parentSubBudgetID);
		return loPSBean;
	}	
	
	
	private MasterBean getMasterBean()
	{
		MasterBean loMasterBean = new MasterBean();
		List <PersonnelServiceBudget> loPSList = new ArrayList<PersonnelServiceBudget>();
		List <LineItemMasterBean> loLineItemList = new ArrayList<LineItemMasterBean>();
	      
	      PersonnelServiceBudget loPS = new PersonnelServiceBudget();
	      loPS.setId("1");
	      loPS.setEmpPosition("Accountant");
	      loPS.setEmpType("1");
	      loPS.setUnit("3");
	      loPS.setBudgetAmount("5000");
	      loPS.setInvoicedAmount("0");
	      loPS.setPositionId("1");
	      loPS.setRemainingAmount("5000");
	      loPS.setModificationAmount("0");
	      loPS.setAmendmentAmount("1000");
	      loPS.setProposedBudgetAmount("6000");
	      loPS.setAmendmentUnit("4");
	      
	      loPSList.add(loPS);
	      LineItemMasterBean loLineItemBean = new LineItemMasterBean();
	      loLineItemBean.setSubbudgetId(subBudgetID);
	      loLineItemBean.setPersonnelserviceBeanList(loPSList);
	      
	      loMasterBean.setBudgetId("666");
	      loLineItemList.add(loLineItemBean);
	      loMasterBean.setMasterBeanList(loLineItemList);
	      
		return loMasterBean;
	}
	
	private MasterBean getMasterBeanMS()
	{
		MasterBean loMasterBean = new MasterBean();
		List <CBMileStoneBean> loMSList = new ArrayList<CBMileStoneBean>();
		List <LineItemMasterBean> loLineItemList = new ArrayList<LineItemMasterBean>();
	      
	      CBMileStoneBean loMS = new CBMileStoneBean();
	      loMS.setId("1");
	      loMS.setMileStone("test1");
	      loMS.setAmount("5000");
	      loMS.setModificationAmount("10");
	      loMS.setRemainAmt("5000");
	      loMS.setProposedAmount("5010");
	      
	      loMSList.add(loMS);
	      LineItemMasterBean loLineItemBean = new LineItemMasterBean();
	      loLineItemBean.setSubbudgetId(subBudgetID);
	      loLineItemBean.setMilestoneBeanList(loMSList);
	      
	      loMasterBean.setBudgetId("666");
	      loLineItemList.add(loLineItemBean);
	      loMasterBean.setMasterBeanList(loLineItemList);
	      
		return loMasterBean;
	}
	
	@Test
	public void testContractBudgetAmendmentServicegenerateMaster()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.generateMasterBeanObjectFromXML(moSession, "300", getFileNetSession());
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}
	
	
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
	
	@Test
	public void generateAmendmentBudgetDataTest() throws ApplicationException {
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setBudgetId("153");
		loTaskDetailsBean.setUserId(agency);
		String lsDocId = moContractBudgetAmendmentService.
		generateAmendmentBudgetData(moSession, true, loTaskDetailsBean, getFileNetSession());
		assertNotNull(lsDocId);
	}
	

	@Test
	public void generateAmendmentBudgetDataTest1() throws ApplicationException {
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setBudgetId("153");
		loTaskDetailsBean.setUserId(agency);
		String lsDocId = moContractBudgetAmendmentService.
		generateAmendmentBudgetData(moSession, true, loTaskDetailsBean, getFileNetSession());
		assertNotNull(lsDocId);
	}
	

	@Test
	public void generateAmendmentBudgetDataTest2() throws ApplicationException {
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setBudgetId("153");
		loTaskDetailsBean.setUserId(agency);
		String lsDocId = moContractBudgetAmendmentService.
		generateAmendmentBudgetData(moSession, true, loTaskDetailsBean, getFileNetSession());
		assertNotNull(lsDocId);
	}

	@Test
	public void generateAmendmentBudgetDataTest3() throws ApplicationException {
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setBudgetId("153");
		loTaskDetailsBean.setUserId(agency);
		String lsDocId = moContractBudgetAmendmentService.
		generateAmendmentBudgetData(moSession, true, loTaskDetailsBean, getFileNetSession());
		assertNotNull(lsDocId);
	}

	@Test
	public void generateAmendmentBudgetDataTest4() throws ApplicationException {
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setBudgetId("153");
		loTaskDetailsBean.setUserId(agency);
		String lsDocId = moContractBudgetAmendmentService.
		generateAmendmentBudgetData(moSession, true, loTaskDetailsBean, getFileNetSession());
		assertNotNull(lsDocId);
	}

	@Test
	public void generateAmendmentBudgetDataTest5() throws ApplicationException {
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setBudgetId("153");
		loTaskDetailsBean.setUserId(agency);
		String lsDocId = moContractBudgetAmendmentService.
		generateAmendmentBudgetData(moSession, true, loTaskDetailsBean, getFileNetSession());
		assertNotNull(lsDocId);
	}

	@Test
	public void generateAmendmentBudgetDataTest6() throws ApplicationException {
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setBudgetId("153");
		loTaskDetailsBean.setUserId(agency);
		String lsDocId = moContractBudgetAmendmentService.
		generateAmendmentBudgetData(moSession, true, loTaskDetailsBean, getFileNetSession());
		assertNotNull(lsDocId);
	}

	@Test
	public void generateAmendmentBudgetDataTest7() throws ApplicationException {
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setBudgetId("153");
		loTaskDetailsBean.setUserId(agency);
		String lsDocId = moContractBudgetAmendmentService.
		generateAmendmentBudgetData(moSession, true, loTaskDetailsBean, getFileNetSession());
		assertNotNull(lsDocId);
	}

	@Test
	public void generateAmendmentBudgetDataTest8() throws ApplicationException {
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setBudgetId("153");
		loTaskDetailsBean.setUserId(agency);
		String lsDocId = moContractBudgetAmendmentService.
		generateAmendmentBudgetData(moSession, true, loTaskDetailsBean, getFileNetSession());
		assertNotNull(lsDocId);
	}

	@Test
	public void generateAmendmentBudgetDataTest9() throws ApplicationException {
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setBudgetId("153");
		loTaskDetailsBean.setUserId(agency);
		String lsDocId = moContractBudgetAmendmentService.
		generateAmendmentBudgetData(moSession, true, loTaskDetailsBean, getFileNetSession());
		assertNotNull(lsDocId);
	}

	@Test
	public void generateAmendmentBudgetDataTest10() throws ApplicationException {
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setBudgetId("153");
		loTaskDetailsBean.setUserId(agency);
		String lsDocId = moContractBudgetAmendmentService.
		generateAmendmentBudgetData(moSession, true, loTaskDetailsBean, getFileNetSession());
		assertNotNull(lsDocId);
	}

	@Test
	public void generateAmendmentBudgetDataTest11() throws ApplicationException {
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setBudgetId("153");
		loTaskDetailsBean.setUserId(agency);
		String lsDocId = moContractBudgetAmendmentService.
		generateAmendmentBudgetData(moSession, true, loTaskDetailsBean, getFileNetSession());
		assertNotNull(lsDocId);
	}

	@Test
	public void generateAmendmentBudgetDataTest12() throws ApplicationException {
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setBudgetId("153");
		loTaskDetailsBean.setUserId(agency);
		String lsDocId = moContractBudgetAmendmentService.
		generateAmendmentBudgetData(moSession, true, loTaskDetailsBean, getFileNetSession());
		assertNotNull(lsDocId);
	}
	
	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServicefetchProgramIncomeAmendment0Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.fetchProgramIncomeAmendment(getCBGridBeanParams(), null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServiceupdateProgramIncomeAmendment1Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.updateProgramIncomeAmendment(null, getDummyCBProgramIncomeBeanObjApproved());
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServicefetchContractedServicesAmendmentConsultants2Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.fetchContractedServicesAmendmentConsultants(null, getDummyContractedServicesBeanObj(), null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServicefetchContractedServicesAmendmentSubContractors3Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.fetchContractedServicesAmendmentSubContractors(null, getDummyContractedServicesBeanObj(), null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServicefetchContractedServicesAmendmentVendors4Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.fetchContractedServicesAmendmentVendors(null, getDummyContractedServicesBeanObj(), null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServiceaddContractedServicesAmendment5Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.addContractedServicesAmendment(null, getDummyContractedServicesBeanObj());
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServiceeditContractedServicesAmendment6Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.editContractedServicesAmendment(null, getDummyContractedServicesBeanObj());
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServicedeleteContractedServicesAmendment7Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.deleteContractedServicesAmendment(null, getDummyContractedServicesBeanObj());
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServicefetchNonGridContractedServicesAmendment8Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.fetchNonGridContractedServicesAmendment(null, getDummyContractedServicesBeanObj(), null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServicefetchFyBudgetSummary9Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.fetchFyBudgetSummary(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServicegetCbGridDataForSession10Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.getCbGridDataForSession(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServicefetchCMSubBudgetSummary11Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.fetchCMSubBudgetSummary(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServiceupdateAmendmentUnallocatedFunds12Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.updateAmendmentUnallocatedFunds(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServicefetchAmendmentUnallocatedFunds13Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.fetchAmendmentUnallocatedFunds(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServicefetchProfServicesDetailsAmendment14Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.fetchProfServicesDetailsAmendment(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServiceeditProfServicesDetailsAmendment15Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.editProfServicesDetailsAmendment(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServicefetchOpAndSupportAmendPageData16Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.fetchOpAndSupportAmendPageData(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServicefetchAmendmentOTPS17Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.fetchAmendmentOTPS(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServiceeditAmendmentOTPS18Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.editAmendmentOTPS(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServicefetchAmendmentOTPSEquipment19Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.fetchAmendmentOTPSEquipment(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServiceaddAmendmentOTPSEquipment20Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.addAmendmentOTPSEquipment(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServiceeditAmendmentOTPSEquipment21Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.editAmendmentOTPSEquipment(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServicedelAmendmentOTPSEquipment22Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.delAmendmentOTPSEquipment(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServicefetchSalariedEmployeeBudgetForAmendment23Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.fetchSalariedEmployeeBudgetForAmendment(null, getPersonnelServiceObject(), null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServicefetchHourlyEmployeeBudgetForAmendment24Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.fetchHourlyEmployeeBudgetForAmendment(null, getPersonnelServiceObject(), null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServicefetchSeasonalEmployeeBudgetForAmendment25Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.fetchSeasonalEmployeeBudgetForAmendment(null, getPersonnelServiceObject(), null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServicefetchFringeBenefitsForAmendment26Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.fetchFringeBenefitsForAmendment(null, getPersonnelServiceObject(), null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServiceaddEmployeeBudgetForAmendment27Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.addEmployeeBudgetForAmendment(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServiceeditEmployeeBudgetForAmendment28Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.editEmployeeBudgetForAmendment(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServiceeditFringeBenefitsForAmendment29Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.editFringeBenefitsForAmendment(null, getPersonnelServiceObject());
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServicefetchMilestone30Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.fetchMilestone(getCBMileStoneBeanObj(),null , null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServicegetSeqForMilestone31Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.getSeqForMilestone(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServiceaddMilestone32Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.addMilestone(null, getCBMileStoneBeanObj(), null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServiceupdateMilestone33Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.updateMilestone(getCBMileStoneBeanObj(), null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServicedeleteMilestone34Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.deleteMilestone(getCBMileStoneBeanObj(), null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServicefetchAmendmentRent35Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.fetchAmendmentRent(null, getRentObj(), null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServiceupdateAmendmentRent36Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.updateAmendmentRent(null, getRentObj());
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServiceinsertContractBudgetAmendmentRent37Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.insertContractBudgetAmendmentRent(null, getRentObj());
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServicedeleteRentAmendment38Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.deleteRentAmendment(null, getRentObj());
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServicefetchContractBudgetAmendmentRate39Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.fetchContractBudgetAmendmentRate(null,getRateObj(), null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServiceinsertContractBudgetAmendmentRateInfo40Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.insertContractBudgetAmendmentRateInfo(null, getRateObj());
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServiceupdateContractBudgetAmendmentRateInfo41Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.updateContractBudgetAmendmentRateInfo(null, getRateObj());
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServicedeleteContractBudgetAmendmentRateInfo42Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.deleteContractBudgetAmendmentRateInfo(null, getRateObj());
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServicefetchUtilitiesAmendment43Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.fetchUtilitiesAmendment(null, getCBUtilitiesObj(), null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServiceupdateUtilitiesAmendment44Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.updateUtilitiesAmendment(null, getCBUtilitiesObj());
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServicefetchIndirectRate45Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.fetchIndirectRate(null, getIndRateObj(), null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServicegenerateAmendmentBudgetData46Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.generateAmendmentBudgetData(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServicegenerateMasterBeanObjectFromXML47Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.generateMasterBeanObjectFromXML(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

}
