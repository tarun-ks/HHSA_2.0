package com.nyc.hhs.service.db.services.application;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.nyc.hhs.model.PersonnelServiceBudget;
import com.nyc.hhs.model.RateBean;
import com.nyc.hhs.model.Rent;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.model.UnallocatedFunds;
/**
 * This is mapper class for contract budget amendment which has queries to get data
 */
public interface ContractBudgetAmendmentMapper
{
	public List<CBProgramIncomeBean> fetchProgramIncomeAmendment(CBGridBean aoCBGridBeanObj);

	public Integer updateProgramIncomeAmendment(CBProgramIncomeBean aoProgramIncomeBean);

	public Integer insertProgramIncomeAmendment(CBProgramIncomeBean aoCBCBProgramIncomeBeanObj);

	public List<CBProfessionalServicesBean> fetchProfServicesDetailsAmendment(CBGridBean aoProfService);

	public Integer addProfServicesAmendmentAmount(CBProfessionalServicesBean aoProfService);

	public Integer updateProfServicesAmendmentAmount(CBProfessionalServicesBean aoProfServicesDetails);

	public BigDecimal fetchRemainingAmnt(CBProfessionalServicesBean aoProfServicesDetails);

	public List<ContractedServicesBean> fetchContractedServicesAmendmentConsultants(CBGridBean aoCBGridBeanObj);

	public List<ContractedServicesBean> fetchContractedServicesNewAmendmentConsultants(CBGridBean aoCBGridBeanObj);

	public List<ContractedServicesBean> fetchContractedServicesAmendmentSubContractors(CBGridBean aoCBGridBeanObj);

	public List<ContractedServicesBean> fetchContractedServicesAmendmentVendors(CBGridBean aoCBGridBeanObj);

	public Integer addContractedServicesAmendment(ContractedServicesBean aoCBGridBeanObj);

	public Integer updateContractedServicesAmendment(ContractedServicesBean aoCBGridBeanObj);

	public Integer editContractedServicesAmendment(ContractedServicesBean aoCBGridBeanObj);

	public Integer delContractedServicesAmendment(ContractedServicesBean aoCBGridBeanObj);

	public ContractedServicesBean fetchInsertContractedServicesAmendment(ContractedServicesBean aoCBGridBeanObj);

	public ContractedServicesBean fetchNonGridContractedServices(CBGridBean aoCBGridBeanObj);

	public BigDecimal getRemainingAmountAmendmentContractedServices(ContractedServicesBean aoContractedServicesBeanObj);

	public BigDecimal getRemainingAmountContractedServicesInMultipleAmendments(
			ContractedServicesBean aoContractedServicesBeanObj);

	public CBGridBean getCbGridDataForSession(HashMap<String, String> aoHashmap);

	public List<UnallocatedFunds> fetchAmendmentUnallocatedFunds(CBGridBean aoCBGridBeanObj);

	public int updateAmendmentUnallocatedFunds(UnallocatedFunds aoUnallocatedFunds);

	public int updateAmendContractStatusToPendReg(TaskDetailsBean aoTaskDetailsBean);

	public int insertAmendmentUnallocatedFunds(CBGridBean aoCBGridBeanObj);

	public int insertUpdAmnUnallocatedFunds(CBGridBean aoCBGridBeanObj);

	public BigDecimal getAmendAmount(String asBudgetId);

	public List<PersonnelServiceBudget> fetchSalriedEmployeeForAmendment(CBGridBean asCBGridBean);

	public Integer updatePersonnelServicesForAmendment(PersonnelServiceBudget aoPersonnelServiceBudgetBean);

	public Integer insertFirstPersonnelServicesForAmendment(PersonnelServiceBudget aoPersonnelServiceBudgetBean);

	public Integer insertPersonnelServicesAmendment(PersonnelServiceBudget aoPersonnelServiceBudgetBean);

	public List<PersonnelServiceBudget> fetchHourlyEmployeeForAmendment(CBGridBean asCBGridBean);

	public List<PersonnelServiceBudget> fetchSeasonalEmployeeForAmendment(CBGridBean asCBGridBean);

	public Integer updateFringeBenifitsForAmendment(PersonnelServiceBudget aoPersonnelServiceBudgetBean);

	public Integer insertFirstFringeBenefitsForAmendment(PersonnelServiceBudget aoPersonnelServiceBudgetBean);

	public List<PersonnelServiceBudget> fetchFringeBenefitsForAmendment(CBGridBean aoPersonnelServiceBudget);

	public List<CBMileStoneBean> fetchMilestoneForAmendment(CBGridBean aoCBGridBeanObj);

	public List<CBMileStoneBean> fetchMilestoneBaseDetails(CBGridBean aoCBGridBeanObj);

	public CBMileStoneBean fetchMilestoneDetailsForValidation(String asId);

	public CBMileStoneBean fetchMilestoneDetailsForValidationInMultipleAmendments(CBMileStoneBean aoCBMileStoneBean);

	public Integer updateMilestoneDetails(CBMileStoneBean aoCBMilestoneBean);

	public Integer insertNewMilestoneForAmd(CBMileStoneBean aoCBMilestoneBean);

	public Integer insertMilestoneDetails(CBMileStoneBean aoCBMilestoneBean);

	public Integer getSeqForMilestone();

	public Integer deleteMilestoneDetails(CBMileStoneBean aoCBMilestoneBean);

	public List<CBOperationSupportBean> fetchOperationAndSupportAmendDetails(CBGridBean aoCBGridBeanObj);

	public List<CBEquipmentBean> fetchEquipmentAmendDetails(CBGridBean aoCBGridBeanObj);

	public Integer editOperationAndSupportAmendDetails(CBOperationSupportBean aoCBOperationSupportBean);

	public void insertOperationAndSupportAmendDetails(CBOperationSupportBean aoCBOperationSupportBean);

	@SuppressWarnings("rawtypes")
	public List<HashMap<String, Object>> fetchSubBudgetDetails(Map aoMap);

	public void addEquipmentAmendDetails(CBEquipmentBean aoCBEquipmentBean);

	public Integer delEquipmentAmendDetails(CBEquipmentBean aoCBEquipmentBean);

	public Integer editEquipmentAmendDetails(CBEquipmentBean aoCBEquipmentBean);

	public void editInsertEquipmentAmendDetails(CBEquipmentBean aoCBEquipmentBean);

	public List<CBEquipmentBean> fetchEquipmentAmendAmtDetails(CBGridBean aoCBGridBeanObj);

	public String fetchDocIdForBudget(String asBudgetId);

	@SuppressWarnings("rawtypes")
	public String fetchAmendmentBudgetStatus(Map aoMap);

	@SuppressWarnings("rawtypes")
	public Integer updateBudgetWithDocId(Map aoMap);

	public BudgetDetails fetchFyBudgetSummary(HashMap<String, String> aoHashmap);

	public List<CBUtilities> fetchUtilitiesAmendmentDetails(CBGridBean aoCBGridBeanObj);

	public Integer fetchOtherEntryIfExists(CBOperationSupportBean aoCBOperationSupportBean);

	public void insertAmendRecInOtherDetails(CBOperationSupportBean aoCBOperationSupportBean);

	public Integer fetchOtherEntryIfExistsProgInc(CBProgramIncomeBean aoCBProgramIncomeBean);

	public void insertAmendRecInOtherDetailsProgInc(CBProgramIncomeBean aoCBProgramIncomeBean);

	public Double getPendingNegAmendmentUnitsPS(PersonnelServiceBudget aoPersonnelServiceBudgetBean);

	public Integer getPendingNegAmendmentUnitsEqp(CBEquipmentBean aoCBEquipmentBean);

	public Map fetchPSDetailsForValidationInMultipleAmendments(PersonnelServiceBudget aoPersonnelServiceBudget);

	public BigDecimal fetchFringeAmountForValidationInMultipleAmendments(PersonnelServiceBudget aoPersonnelServiceBudget);

	public BigDecimal fetchOTPSDetailsForValidationInMultipleAmendments(CBOperationSupportBean aoCBOperationSupportBean);

	public CBEquipmentBean fetchEquipmentDetailsForValidationInMultipleAmendments(CBEquipmentBean aoCBEquipmentBean);

	public CBUtilities fetchUtilitiesDetailsForValidationInMultipleAmendments(CBUtilities aoCBUtilities);

	public BigDecimal fetchProfServiceForValidationInMultipleAmendments(CBProfessionalServicesBean aoProfServicesDetails);

	public BigDecimal getRemainingAmountRentInMultipleAmendments(Rent aoRent);

	public BigDecimal getRemainingAmountRateInMultipleAmendments(RateBean aoRateBeanObj);

	public BigDecimal getRemainingAmountUnAllocatedInMultipleAmendments(UnallocatedFunds aoUnallocatedFunds);

	public Integer getBaseUnitContractBudgetRateInMultipleAmendments(RateBean aoRateBeanObj);

	public BigDecimal getRemainingAmountIndirectRate(CBIndirectRateBean aoIndirectRate);

	public BigDecimal fetchProgIncomeDetailsForValidationInMultipleAmendments(CBProgramIncomeBean aoCBProgramIncomeBean);
	
	public Integer fetchRateValidationRemngUnitsInMultipleAmendments(RateBean aoRateBean);
	//Added in R7 for Program income
	public Integer insertProgramIncomeAmendmentGrid(CBProgramIncomeBean aoCBCBProgramIncomeBeanObj);

	public CBProgramIncomeBean fetchAmendmentProgramIncomeDetails(CBProgramIncomeBean aoCBCBProgramIncomeBeanObj);
	//R7 changes end
	

    // [Start] R7.5.0 QC9146 Professional Service Grid issue for Amendment
    public Integer mergeProfServicesAmendmentAmount(CBProfessionalServicesBean aoProfService);
    // [End] R7.5.0 QC9146 Professional Service Grid issue for Amendment
	
    // Start   QC 8394 R 7.9.0 add Add/Delete action for Unallocated Fund 
    public int addAmendmentUnallocatedFunds(UnallocatedFunds aoUnallocatedFunds);
    
    public int deleteAmendmentUnallocatedFunds(UnallocatedFunds aoUnallocatedFunds);
         
    public int insertNewUnallocatedFundsForAmendment(UnallocatedFunds aoUnallocatedFunds);
    
    public List<UnallocatedFunds> fetchAmendmentUnallocatedFundsToBaseLine(CBGridBean aoCBGridBeanObj);
    
    // End   QC 8394 R 7.9.0 add Add/Delete action for Unallocated Fund 
	
    //[Start] R9.4.0 QC8522
	public ContractedServicesBean fetchNonGridContractedServices_AMND(CBGridBean aoCBGridBeanObj);
    //[End] R9.4.0 QC8522

    
}