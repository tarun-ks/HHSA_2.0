/**
 * 
 */
package com.nyc.hhs.service.db.services.application;

import java.util.HashMap;
import java.util.List;

import com.batch.bulkupload.BulkUploadContractInfo;
import com.nyc.hhs.model.FiscalYear;
import com.nyc.hhs.model.NycAgencyDetails;
import com.nyc.hhs.model.PaymentSortAndFilter;
import com.nyc.hhs.model.ProgramNameInfo;
import com.nyc.hhs.model.StatusR3;

/**
 * This is mapper class for payment which has queries to get data
 */
public interface PaymentMapper
{
	public List<PaymentSortAndFilter> fetchPaymentListSummaryProvider(PaymentSortAndFilter aoPaymentBean);

	public List<PaymentSortAndFilter> fetchPaymentListSummaryAgency(PaymentSortAndFilter aoPaymentBean);

	public List<PaymentSortAndFilter> fetchPaymentListSummaryCity(PaymentSortAndFilter aoPaymentBean);

	Integer getPaymentCountProvider(PaymentSortAndFilter aoPaymentBean);

	Integer getPaymentCountAgency(PaymentSortAndFilter aoPaymentBean);

	Integer getPaymentCountCity(PaymentSortAndFilter aoPaymentBean);

	public List<FiscalYear> getFiscalInformation(String asActiveFlag);

	public List<ProgramNameInfo> getProgramName(String asActiveFlag);

	public List<ProgramNameInfo> getProgramNameAgency(HashMap<String, String> loProgramMap);

	List<StatusR3> getStatusList(String asProcessType);

	List<NycAgencyDetails> getAgencyList(String asAgencyDetails);

	List<HashMap> fetchBudgetIdListFromContractId(HashMap<String, String> loInputParams);
	
	public String getProgramNameId(BulkUploadContractInfo aoContractDetails);
	
	// Start || changes done for enhancement 6495 for Release 3.12.0
	List<StatusR3> getPaymentStatusListProvider(String asProcessType);
	// End || changes done for enhancement 6495 for Release 3.12.0
}
