package com.nyc.hhs.service.db.services.application;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nyc.hhs.model.AdvanceSummaryBean;
import com.nyc.hhs.model.AssignmentsSummaryBean;
import com.nyc.hhs.model.AutoCompleteBean;
import com.nyc.hhs.model.BudgetDetails;
import com.nyc.hhs.model.BudgetList;
import com.nyc.hhs.model.CBEquipmentBean;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.CBIndirectRateBean;
import com.nyc.hhs.model.CBMileStoneBean;
import com.nyc.hhs.model.CBOperationSupportBean;
import com.nyc.hhs.model.CBProfessionalServicesBean;
import com.nyc.hhs.model.CBProgramIncomeBean;
import com.nyc.hhs.model.CBServicesBean;
import com.nyc.hhs.model.CBUtilities;
import com.nyc.hhs.model.ContractList;
import com.nyc.hhs.model.ContractedServicesBean;
import com.nyc.hhs.model.InvoiceList;
import com.nyc.hhs.model.PersonnelServiceBudget;
import com.nyc.hhs.model.PersonnelServicesData;
import com.nyc.hhs.model.RateBean;
import com.nyc.hhs.model.Rent;
import com.nyc.hhs.model.UnallocatedFunds;
/**
 * This is mapper class for invoice which has queries to get data
 */
public interface InvoiceMapper
{
	public ArrayList<InvoiceList> fetchInvoiceListProvider(InvoiceList aoInvoiceFilter);

	public ArrayList<InvoiceList> fetchInvoiceListAgency(InvoiceList aoInvoiceFilter);

	public ArrayList<InvoiceList> fetchInvoiceListAccelerator(InvoiceList aoInvoiceFilter);

	public int fetchInvoiceCountProvider(InvoiceList aoInvoiceFilter);

	public int fetchInvoiceCountAgency(InvoiceList aoInvoiceFilter);

	public int fetchInvoiceCountAccelerator(InvoiceList aoInvoiceFilter);

	void updateInvoiceWithdrawn(HashMap aoHMReqProp);

	public void deleteInvoice(String asInvoiceId);

	public void deleteInvoiceDetails(String asInvoiceId);
	
	//Made changes for defect id 6445 release 3.3.0 
	public void deleteInvoiceDocument(String asInvoiceId);
	
	//Made changes for defect id 6445 release 3.3.0 
	public List<HashMap<String,String>> fetchInvoiceDocumentIds(String asInvoiceId);

	public String selectWithdrawInvoiceWorkFlowDetails(String asInvoiceNumber);

	public void updateWithdrawInvoiceWorkFlowId(HashMap aoWithdrawInvoiceMap);

	public List<CBEquipmentBean> fetchInvoiceOperationSupport(CBGridBean aoCBGridBean);

	public List<CBOperationSupportBean> fetchInvoiceOperationSupportDetails(CBGridBean aoCBGridBean);

	public String fetchBudgetAllocatedForAnOpSupport(String aoOpSupportId);

	public String fetchInvAmountForAnOpSupportLineItem(CBOperationSupportBean aoCBOperationSupportBean);

	public Integer editInvoiceOperationSupportDetails(CBOperationSupportBean aoCBOperationSupportBean);

	public Integer getSeqForInvoiceDetail();

	public Integer getSeqForInvoiceAdvanceNumber();

	public Integer insertInvoiceOperationSupportDetails(CBOperationSupportBean aoCBOperationSupportBean);

	public List<CBEquipmentBean> fetchEquipmentDetails(CBGridBean aoCBGridBean);

	public List<CBEquipmentBean> fetchEquipmentInvoiceDetails(CBGridBean aoCBGridBean);

	public String fetchBudgetAllocatedForAnEquipment(String aoEquipmentId);

	public String fetchInvAmountForAnEquipment(CBEquipmentBean aoCBEquipmentBean);

	public List<BudgetDetails> fetchInvoiceSummary(HashMap<String, String> loInvSumInputParam);

	public boolean addEquipmentDetails(CBEquipmentBean aoCBEquipmentBean);

	public Integer editEquipmentDetails(CBEquipmentBean aoCBEquipmentBean);

	public Integer insertEquipmentDetails(CBEquipmentBean aoCBEquipmentBean);

	public boolean deleteEquipmentDetails(CBEquipmentBean aoCBEquipmentBean);

	public String fetchInvoiceStatus(String asInvoiceId);

	public String fetchInvoiceTotalForOTPS(CBGridBean aoCBGridBean);

	public String fetchInvoiceTotalForContractedServices(CBGridBean aoCBGridBean);

	public String fetchYTDInvoiced(String asSubBudgetId);

	public String fetchCSYTDInvoiced(String asSubBudgetId);

	public List<CBProgramIncomeBean> fetchProgramIncomeInvoice(CBGridBean aoCBGridBeanObj);

	public List<CBUtilities> fetchInvoicingUtilitiesDetails(CBGridBean aoCBGridBeanObj);

	public BigDecimal fetchIndirectRemainingAmount(CBIndirectRateBean aoCBIndirectRateBean);

	public List<CBIndirectRateBean> fetchInvoiceIndirectRate(CBGridBean aoCBGridBeanObj);

	public Integer insertInvoicingLineItemDetails(HashMap aoQueryParam);

	public Integer updateInvoicingDetails(HashMap aoQueryParam);

	public BigDecimal fetchUtilitiesRemainingAmount(CBUtilities aoCBUtilities);

	public List<UnallocatedFunds> fetchInvoiceUnallocatedFunds(UnallocatedFunds unallocatedFunds);

	public int insertInvoiceUnallocatedFunds(UnallocatedFunds aoUnallocatedFunds);

	public List<BudgetDetails> fetchInvoiceSummary(CBGridBean loCBGridBean);

	// For Professional Service
	public List<CBProfessionalServicesBean> fetchInvoiceProfServices(CBGridBean aoProfService);

	public Integer updateInvoiceForProfServices(CBProfessionalServicesBean aoProfServicesDetails);

	public Integer fetchCountInvoiceDetail(CBProfessionalServicesBean aoProfServicesDetails);

	public Integer addInvoiceForProfServices(CBProfessionalServicesBean aoProfService);

	public Integer fetchProfServicesForOther(Integer aoProfServiceId);

	public Integer addProfServicesForOther(CBProfessionalServicesBean aoProfService);

	public Integer updateProfServicesForOther(CBProfessionalServicesBean aoProfServicesDetails);

	public BigDecimal fetchTotalRemainingAmnt(CBProfessionalServicesBean aoProfServicesDetails);

	public BigDecimal fetchRemainingAmnt(CBProfessionalServicesBean aoProfServicesDetails);

	public String getInvoiceDetailId(CBProfessionalServicesBean aoProfServicesDetails);

	public List<CBMileStoneBean> fetchMilestoneInvoiceDetails(CBGridBean aoCBGridBeanObj);

	public BigDecimal fetchRemainingAmountMilestone(CBMileStoneBean aoCBMileStoneBean);

	public ContractList fetchContractInvoiceSummary(HashMap<String, String> aoHashmap);

	public InvoiceList fetchContractInvoiceInformation(HashMap<String, String> aoHashmap);
	
	//Added for R3.7.0 
	public List<InvoiceList> fetchContractInvoiceInfoList(HashMap<String, String> aoHashmap);

	public Integer updateInvoiceStatus(HashMap aoHmInvoiceRequiredProps);

	public List<Rent> fetchInvoicingRent(CBGridBean asCBGridBean);

	public BigDecimal fetchRentRemainingAmount(Rent aoRent);

	public String fetchCurrInvoiceStatus(String asInvoiceId);

	public List<PersonnelServiceBudget> fetchSalariedEmployee(CBGridBean aoCBGridBean);

	public List<PersonnelServiceBudget> fetchFringBenifits(CBGridBean aoCBGridBean);

	public BigDecimal fetchPersonnelAmount(PersonnelServiceBudget aoPersonnelServiceBudget);

	public PersonnelServicesData fetchInvoiceTotalForPersonnelServices(CBGridBean aoCBGridBean);

	public String fetchYTDTotalSalaryAndFringe(String aoBudgetId);

	public BigDecimal fetchPersonnelFringeAmount(PersonnelServiceBudget aoPersonnelServiceBudget);

	public String getAgencyIdByContractForWF(String asContractId);

	// rate start
	public List<RateBean> fetchInvoiceRateRemainingAmt(CBGridBean aoCBGridBeanObj);

	public List<RateBean> fetchInvoiceRateInvoiceAmt(CBGridBean aoCBGridBeanObj);

	public BigDecimal fetchRateValidationRemAmt(RateBean aoRateBean);

	public BigDecimal fetchRateValidationRemUnits(RateBean aoRateBean);

	// rate end

	public HashMap fetchInvoiceAmountAssignment(String asInvoiceId);

	public String fetchContractStatus(String asContractId);

	public Integer setStatusForInvoiceReviewTask(Map<String, Object> aoHashMap);
	
	/* QC9710 */
	public Integer UpdateInvoiceAdvanceModifiedDate(Map<String, Object> aoHashMap);
	
	/* QC9721 */
	public Integer UpdateInvoiceDetailsModifiedDate(Map<String, Object> aoHashMap);	
	
	public List<AssignmentsSummaryBean> contractInvAssignmentSummary(CBGridBean aoCBGridBean);

	public List<AssignmentsSummaryBean> fetchInvoiceAssignmentDetail(CBGridBean aoCBGridBean);

	public Integer editAssignment(AssignmentsSummaryBean aoAssignmentsSummaryBean);

	public List<ContractedServicesBean> fetchContractedServicesInvoicingConsultants(CBGridBean aoCBGridBeanObj);

	public List<ContractedServicesBean> fetchContractedServicesInvoicingSubContractors(CBGridBean aoCBGridBeanObj);

	public List<ContractedServicesBean> fetchContractedServicesInvoicingVendors(CBGridBean aoCBGridBeanObj);

	public BigDecimal fetchContractedServicesRemainingAmount(ContractedServicesBean aoCBGridBeanObj);

	public BudgetDetails fetchInvFyBudgetSummary(HashMap<String, String> aoHashmap);

	public BigDecimal fetchInvFyBudgetActualPaid(String aoBudgetId);

	public Integer insertAgencyInvoiceNumber(Map aoInvoiceMap);

	public List<AutoCompleteBean> fetchVendorList(String aoVendorName);

	public List<AutoCompleteBean> fetchProviderList(String aoProviderName);

	public Integer validateAssignee(HashMap<String, String> aoHashmap);

	public Integer addAssignment(HashMap<String, String> aoHashmap);

	public Integer fetchContractInvoiceSeqNo(HashMap<String, String> aoHashmap);

	public Integer fetchBudgetInvoiceSeqNo(HashMap<String, String> aoHashmap);

	public BudgetList fetchBudgetStatus(HashMap<String, String> aoHashmap);

	public Integer fetchBudgetAmount(HashMap<String, String> aoHashmap);

	public String fetchDiscFlagAmount(HashMap<String, String> aoHashmap);

	public Integer updateInvoiceDetails(Map aoInvoiceMap);

	public Integer delAssignment(AssignmentsSummaryBean aoAssignmentsSummaryBean);

	public Integer fetchCountInvoiceDetails(HashMap<String, String> aoHashmap);

	public Integer delInvoiceDetails(HashMap<String, String> aoHashmap);

	public List<AdvanceSummaryBean> fetchInvoiceAdvancesDetails(CBGridBean aoCBGridBean);

	public Integer validateBudgetAdvanceStatus(String aoBudgetAdvanceId);

	public Map<String, String> validateInvAdvanceRecoupAmount(String aoBudgetAdvanceId);

	public String fetchInvoiceRecoupAmount(AdvanceSummaryBean aoAdvanceSummaryBean);

	public Integer editInvoiceAdvanceRecouped(AdvanceSummaryBean aoAdvanceSummaryBean);

	public Integer insertInvoiceAdvanceRecouped(AdvanceSummaryBean aoAdvanceSummaryBean);

	Integer removeInvoiceDocumentFromVault(String asdocumentId);

	public Integer invoiceNegativeAmendCheck(String asBudgetId);

	public BigDecimal invoiceTotal(String asInvoiceId);

	public BigDecimal assignmentAmount(String asInvoiceId);

	public BigDecimal assignmentAmountExceptCurrentLineItem(AssignmentsSummaryBean aoAssignmentsSummaryBean);

	public Integer fetchInvoiceSeqNo();

	public List<PersonnelServiceBudget> fetchSalariedEmployeeForRemainingAmt(CBGridBean aoCBGridBean);

	public List<PersonnelServiceBudget> fetchSalariedEmployeeForInvoicedAmt(CBGridBean aoCBGridBean);

	public List<PersonnelServiceBudget> fetchFringeForRemainingAmt(CBGridBean aoCBGridBean);

	public List<PersonnelServiceBudget> fetchFringeForInvoicedAmt(CBGridBean aoCBGridBean);

	public List<PersonnelServiceBudget> fetchHourlyEmployeeForRemainingAmt(CBGridBean aoCBGridBean);

	public List<PersonnelServiceBudget> fetchHourlyEmployeeForInvoicedAmt(CBGridBean aoCBGridBean);

	public List<PersonnelServiceBudget> fetchSeasonalEmployeeForRemainingAmt(CBGridBean aoCBGridBean);

	public List<PersonnelServiceBudget> fetchSeasonalEmployeeForInvoicedAmt(CBGridBean aoCBGridBean);

	public int updatePersonalServiceYtdAndRemainingAmount(String aoBaseBudgetId);

	public int updateContractedServiceYtdAndRemainingAmount(String aoBaseBudgetId);

	public int updateEquipmentYtdAndRemainingAmount(String aoBaseBudgetId);

	public int updateFringesYtdAndRemainingAmount(String aoBaseBudgetId);

	public int updateIndirectRateYtdAndRemainingAmount(String aoBaseBudgetId);

	public int updateMilestoneYtdAndRemainingAmount(String aoBaseBudgetId);

	public int updateOperationAndSupportYtdAndRemainingAmount(String aoBaseBudgetId);

	public int updateProfessionalServicesYtdAndRemainingAmount(String aoBaseBudgetId);

	public int updateProgramIncomeYtdAndRemainingAmount(String aoBaseBudgetId);

	public int updateRateYtdAndRemainingAmount(String aoBaseBudgetId);

	public int updateRentYtdAndRemainingAmount(String aoBaseBudgetId);

	public int updateUtilitiesYtdAndRemainingAmount(String aoBaseBudgetId);

	public int updateLineItemsYTDAndRemainingForAgencyInterface(HashMap<String, String> aoHashMap);
	
	public int updateLineItemsYTDAndRemainingUnitsForRate(HashMap<String, String> aoHashMap);

	public int updateSubBudgetsYTDAndRemainingForAgencyInterface(HashMap<String, String> aoHashMap);

	public int updateBudgetsYTDAndRemainingForAgencyInterface(HashMap<String, String> aoHashMap);

	public String getBaseBudgetIdFromInvoiceId(String aoInvoiceId);

	@SuppressWarnings("rawtypes")
	public List<HashMap> fetchApproveInvoiceFromETL();

	@SuppressWarnings("rawtypes")
	public void updateInvoiceIdForBatch(HashMap aoHashMap);

	//Start Enhancement 6535 Release 3.8.0
	public BigDecimal fetchRateRemainingPaymentDisbursed(CBGridBean aoRateBean);
	public BigDecimal fetchRateRemainingPaymentDisbursedUnits(CBGridBean aoRateBean);
	public BigDecimal fetchFyBudgetLineItem(CBGridBean aoRateBean);
	public BigDecimal fetchFyBudgetLineItemUnits(CBGridBean aoRateBean);
	public BigDecimal fetchRateRemainingPendingInvoiceNegative(RateBean aoRateBean);
	public List<RateBean> fetchInvoiceRateRemainingAmtNegative(CBGridBean aoCBGridBeanObj);
	//End Enhancement 6535 Release 3.8.0
	
	//method added as a part of Enhancement 6576 Release 3.10.0
	public String preventPaymentOverBudgetTotal(Map asMap);
	// Added for R5
	public List<String> getInvoiceDetails(HashMap<String, Object> aoHashMap);
	//Added in R7
	public String fetchModificationUrlDetail(String asBudgetId);
	//Start:Added in R7 for Cost-Center
	public int updateLineItemsYtdAndRemainingAmountUnitsForServices(HashMap<String, String> aoHashMap);
	public int updateLineItemsYtdAndRemainingAmountForCostCenter(HashMap<String, String> aoHashMap);
	//End:Added in R7 for Cost-Center
	
	//Start: Added in R7 for cost center
	public BigDecimal fetchFyBudgetUnitsLineItem(CBServicesBean aoCBServices);

	public List<CBServicesBean> fetchInvoiceServiceData(CBGridBean aoCBGridBeanObj);

	public List<CBServicesBean> fetchInvoiceCostData(CBGridBean aoCBGridBeanObj);

	public List<CBServicesBean> fetchCostData(CBGridBean aoCBGridBeanObj);

	public Integer updateServicesList(CBServicesBean servicesDetailId);

	public Integer updateCostCenterList(CBServicesBean servicesDetailId);

	public Integer editInvoiceService(CBServicesBean aoCBServices);

	public Integer insertInvoiceService(CBServicesBean aoCBServices);

	public BigDecimal fetchInvAmountForCostCenterLineItem(CBServicesBean aoCBServices);

	public Integer editInvoiceCostCenter(CBServicesBean aoCBServices);

	public Integer insertInvoiceCostCenter(CBServicesBean aoCBServices);

	public BigDecimal invoiceServicesTotal(String asInvoiceId);

	public BigDecimal assignmentAmountServices(String asInvoiceId);

	public BigDecimal fetchCostCenterAmountTotal(String asInvoiceId);

	public BigDecimal assignmentAmountCostCenter(String asInvoiceId);
	
	public void deleteCostCenterInvoiceDetails(String asInvoiceId);
	public void deleteServicesInvoiceDetails(String asInvoiceId);
	
	public BigDecimal fetchCCRemainingPaymentDisbursed(CBGridBean aoCBGridBeanObj);
	//End: Added in R7 for cost-center
	
	//Start QC 8774 R 7.5.0  update InvoiceAdvance with 0 RecoupedAmnt if Invoice has been Withdrawn 
	public void updateInvoiceAdvanceWith0RecoupedAmntForInvoiceWithdrawn(String  asInvoiceNumber);
	//End QC 8774 R 7.5.0  update InvoiceAdvance with 0 RecoupedAmnt if Invoice has been Withdrawn 

}
