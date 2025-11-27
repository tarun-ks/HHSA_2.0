package com.nyc.hhs.service.db.services.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nyc.hhs.model.ContractBudgetBean;
import com.nyc.hhs.model.FinancialSummaryBean;
import com.nyc.hhs.model.MasterStatusBean;
import com.nyc.hhs.model.ProcurementSummaryBean;
import com.nyc.hhs.model.QueueItemsDetailsBean;
/**
 * 
 * This is a common mapper class which has queries to get data
 */
public interface CommonMapper
{
	ArrayList<MasterStatusBean> getMasterStatus();

	ProcurementSummaryBean fetchAccProcurementPortletCount(HashMap<String, String> aoProcCountMap);

	ProcurementSummaryBean fetchProcurementCountForProvHomePage(Map<String, String> asUserOrg);

	FinancialSummaryBean fetchAccFinancialsPortletCount(HashMap<String, String> aoFincCountMap);

	FinancialSummaryBean fetchProviderFinancialCount(Map<String, String> asUserOrg);

	HashMap<Object, Object> getProcurementChangeControlWidget(Integer aiProcurementId);

	HashMap<Object, Object> getProcurementChangeControlWidgetDetailed(Map<String, Object> aoDataMap);

	public List<String> fetchEpinList(String asDataToSearch);

	public List<String> fetchProcurementEpinList(String asDataToSearch);

	public List<String> fetchContractEpinList(String asDataToSearch);

	public List<String> getPaymentCtId(String asDataToSearch);

	public List<String> getInvoiceCtId(String asDataToSearch);

	//Release 5 start change done for provider centric reports
	public List<String> fetchContractNoList(String asDataToSearch);
	
	public List<String> fetchContractTitleList(String asDataToSearch);
	
	public List<String> fetchProviderNameList(String asDataToSearch);
	//Release 5 end change done for provider centric reports
	//Enhancement id 6400 release 3.4.0
	public List<String> fetchAmendContractNoList(String asDataToSearch);

	public List<String> fetchBudgetEpinList(String asDataToSearch);

	public List<String> fetchBudgetNoList(String asDataToSearch);

	public Integer updateLastModifiedDetails(Map aoLastModifiedHashMap);

	public List<String> fetchRenewContractEpinList(String asDataToSearch);

	public List<String> fetchAmendContractEpinList(String asDataToSearch);
	
	public List<String> fetchAmendContractEpinListForListScreen(String asDataToSearch);
	
	public List<String> fetchAddContractEpinList(String asDataToSearch);

	Map<Object, Object> getProviderWidgetDetils(Map<String, String> loProviderDetails);

	public Integer checkDocumentExistsInAnyTable(String asDocumentId);

	public Integer removeLockedUser(String asSessionId);

	public Integer removeLockedUserById(String asUserId);

	public Map<String, String> checkLockFlagExist(Map<String, String> aoDataMap);

	public void addLock(Map<String, String> aoDataMap);

	public List<Map<String, String>> fetchProcuringAgenciesFromDB();
	
	public List<String> checkPdfCreated(String asEntityId);
	//Release 3.6.0 Enhancement id 6508.
	public List<QueueItemsDetailsBean> getQueueItemDelayConfig();
	
	// R7.2.0 QC 8914 Action Drop-down to be stripped from following Actions
	public List<String> getReadOnlyActionsToExclude();
	
	/** [Start] R8.4.0 qc9492 XML files for approved amendments and mods */
    public List<ContractBudgetBean> getBudgetForXMLRegen();
	@SuppressWarnings("rawtypes")
    public Integer updateBudgetXMLGenWithDocId(Map aoMap);

	/** [End] R8.4.0 qc9492 XML files for approved amendments and mods */
	
	// Start R8.6.0 QC_9499 Multi-Tab Browsing letting Invoice and Advance tasks to be Approved Multiple times causing Duplicate Payments
	public String getTokenFlagConfig();	
	// End R8.6.0 QC_9499 Multi-Tab Browsing letting Invoice and Advance tasks to be Approved Multiple times causing Duplicate Payments
	
}