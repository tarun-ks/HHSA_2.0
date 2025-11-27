/**
 * 
 */
package com.nyc.hhs.service.db.services.application;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nyc.hhs.model.AssignmentsSummaryBean;
import com.nyc.hhs.model.BudgetDetails;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.ContractList;
import com.nyc.hhs.model.PaymentBean;
import com.nyc.hhs.model.PaymentChartOfAllocation;
import com.nyc.hhs.model.TaskDetailsBean;

/**
 * PaymentModuleMapper is an interface between the DAO and database layer for
 * business and service application,organization profile related transactions to
 * insert, update and select entries.
 * 
 */
public interface PaymentModuleMapper
{

	public ContractList fetchContractInfoForPayment(HashMap<String, String> aoHashmap);

	public BudgetDetails fetchFyBudgetSummary(HashMap<String, String> aoHashmap);

	public BigDecimal getInvoiceAmount(HashMap<String, String> aoHashmap);

	public BigDecimal fetchInvFyBudgetActualPaid(String asBudgetId);

	public CBGridBean getCbGridDataForSession(HashMap<String, String> aoHashmap);

	public List<AssignmentsSummaryBean> fetchAdvanceAssignmentAmount(CBGridBean aoCBGridBean);

	public Integer insertPaymentAssignmentDetails(HashMap aoQueryParam);

	public Integer updatePaymentAssignmentDetails(HashMap aoQueryParam);

	public Integer fetchCountPaymentAssignment(HashMap<String, String> aoHashmap);

	public Integer delAdvanceAssignment(AssignmentsSummaryBean aoAssignmentsSummaryBean);

	public Integer setAdvanceStatusForReviewTask(Map<String, Object> aoHashMap);

	public PaymentBean getPaymentHeaderDetails(HashMap<String, String> aoHashmap);

	public PaymentBean getPaymentReviewHeaderDetails(HashMap<String, String> aoHashmap);

	public List<PaymentBean> getPaymentVoucherNum(HashMap<String, String> aoHashmap);

	public BudgetDetails fetchPaymentFyBudgetSummary(HashMap<String, String> aoHashmap);

	public BigDecimal fetchPaymentFyBudgetActualPaid(HashMap<String, String> aoHashmap);

	public BigDecimal fetchTotalInvoiceAmount(HashMap<String, String> aoHashmap);

	public BigDecimal fetchTotalPaymentAmount(HashMap<String, String> aoHashmap);

	public Integer deleteBudgetAdvance(Map<String, Object> aoHashMap);

	public Integer setPaymentStatus(Map<String, Object> aoHashMap);

	public Integer setInvoiceStatus(Map<String, Object> aoHashMap);

	public Integer deletePaymentRecords(Map<String, Object> aoHashMap);

	public List<PaymentChartOfAllocation> paymentCOFFetch(HashMap<String, String> aoHashmap);

	public int paymentCOFEdit(PaymentChartOfAllocation aoPaymentChartOfAllocation);

	public BigDecimal paymentCOFReaminingAmountFetch(PaymentChartOfAllocation aoPaymentChartOfAllocation);

	public List<String> getPaymentVoucherList(TaskDetailsBean aoTaskDetailsBean);

	public BigDecimal getCoASumAmount(String asPaymentVoucherNo);

	public BigDecimal getTotalPaymentAmount(String asPaymentVoucherNo);

	public PaymentBean getAdvancePaymentHeaderDetails(HashMap<String, String> aoHashmap);

	public PaymentBean getAdvancePaymentReviewHeaderDetails(HashMap<String, String> aoHashmap);

	public List<PaymentBean> getAdvancePaymentVoucherList(HashMap<String, String> aoHashmap);

	public BigDecimal fetchTotalAdvanceAmount(HashMap<String, String> aoHashmap);

	public Map fetchAdvanceAndAssignment(TaskDetailsBean aoTaskDetailsBean);

	public Integer setPeriod(Map<String, Object> aoHashMap);

	public String fetchAdvanceDescForTaskHeader(String asBudgetAdvanceId);
	
	// below methods added as part of release 3.1.0 for enhancement 6023
	public Integer setAdvanceStatusForInterfaceReviewTask(Map<String, Object> aoHashMap);
	
	public Integer setInterfaceInvoiceStatus(Map<String, Object> aoHashMap);
	
	public Integer deleteInterfacePaymentRecords(Map<String, Object> aoHashMap);
	
	public String fetchCommaSeparatedVoucherList(Map<String, Object> aoHashMap);
	
	public Map fetchAgencyDetails(Map aoHashMap);
	
	//START || Changes for enhancement 6487 for Release 3.12.0
	public List<Map<Object, Object>> fetchPaymentAmount(Map<String, Object> aoHashMap);
	//END || Changes for enhancement 6487 for Release 3.12.0
	
	//method added as a part of release 3.12.0 enhancement 6578
	public String fetchBatchInProgressFlag(String asModBudId);

}
