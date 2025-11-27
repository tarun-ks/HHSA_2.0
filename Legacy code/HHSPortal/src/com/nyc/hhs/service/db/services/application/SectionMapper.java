package com.nyc.hhs.service.db.services.application;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.nyc.hhs.model.ContactUsBean;
import com.nyc.hhs.model.OrgNameChangeBean;
import com.nyc.hhs.model.ProviderStatusBean;
import com.nyc.hhs.model.SectionBean;
import com.nyc.hhs.model.SubSectionBean;
import com.nyc.hhs.model.WithdrawalBean;
import com.nyc.hhs.model.WorkflowIDServiceBean;

/**
 * SectionMapper is an interface between the DAO and database layer to update ,
 * insert and fetch required data from different tables for task purpose.
 * 
 */
public interface SectionMapper
{

	List<SectionBean> fetchSection(HashMap aoHMSection);

	List<SectionBean> fetchBRProcAppStatus(HashMap aoHMSection);

	List<SubSectionBean> fetchSubSection(HashMap aoHMSubSection);

	List<ContactUsBean> fetchContactUsDetails(HashMap aoHMSubSection);

	List<WithdrawalBean> fetchServiceWithdrawalDetails(HashMap aoHMSubSection);

	List<WithdrawalBean> fetchBRWithdrawalDetails(HashMap aoHMSubSection);

	void updateSectionStatus(HashMap aoHMSection);

	void updateDocStatus(HashMap aoHMSection);

	void updateProcessSectionStatus(HashMap aoHMSection);

	void updateSectionStatusDB(HashMap aoHMSection);

	void updateProcessServiceSectionStatus(HashMap aoHMSection);

	void updateServiceSectionStatus(HashMap aoHMSection);

	// R5 Proposal Activity History and Char 500 history
	void lastApprovedBusinessApprovedCorporateStructure(HashMap aoHMBRApplication);

	// R5 Changes Ends
	void updateBRApplicationStatus(HashMap aoHMBRApplication);

	void updateBRProcAppStatus(HashMap aoHMBRApplication);

	void updateProcDocumentStatus(HashMap aoHMDocument);

	void updateDocumentStatus(HashMap aoHMDocument);

	void updateSubSectionStatus(HashMap aoHMSubSection);

	void updateProcSubSectionStatus(HashMap aoHMSubSection);

	void updateDocWithProcStatus(HashMap aoHMSection);

	Date fetchOrgExpirationDate(String loHMContextData);

	Date fetchproviderDueDate(String loHMContextData);

	void updateContactStatus(HashMap aoHMSection);

	void updateFilingDocStatus(HashMap aoHMSection);

	int updateDocLapsingMasterForFiling(HashMap aoHMSection);

	int approvedApplicationCount(HashMap aoHMSection);

	void updateSectionWithdrawalRequestStatusDB(HashMap aoHMReqdProp);

	void updateSectionWithdrawalStatusDB(HashMap aoHMReqdProp);

	void updateBusinessWithdrawalStatusDB(HashMap aoHMReqdProp);

	void updateBusinessWithdrawalRequestStatusDB(HashMap aoHMReqdProp);

	void insertAlertAndNotification(HashMap aoHMReqdProp);

	void updateOrgRequestStatus(HashMap aoHMSection);

	void updateUserStatus_DB(HashMap aoHMSection);

	void updateUserStatusActiveFlag(HashMap aoHMSection);

	void updateOrganizationActiveFlag(HashMap aoHMSection);

	List<OrgNameChangeBean> getSubsectionForLegalNameUpdateReqTask(HashMap aoHMProps);

	void updateLegalNameApprovedRequest(HashMap aoHMSection);

	void updateLegalNameRejectedRequest(HashMap aoHMSection);

	void updateLegalNameRequest(HashMap aoHMSection);

	List<WorkflowIDServiceBean> fetchServiceCapacityIds(HashMap aoHMSubSection);

	List<String> getCHAR500docFromDocument(HashMap aoHMSubSection);

	void insertDocLapsingMaster(HashMap aoHMSection);

	void updateDocLapsingMasterOrder(HashMap aoHMSection);

	void insertDocLapsingTrans(HashMap aoHMSection);

	void updateOrganizationStatus(HashMap aoHMSection);

	void deleteEntryFromSuperseding(HashMap aoHMSection);

	void updateSupersedingStatus(HashMap aoHMSection);

	List<ProviderStatusBean> getBusinessAndServiceStatus(HashMap aoHMSubSection);

	List<ProviderStatusBean> getBusinessAndServiceStatusBatch(HashMap aoHMSubSection);

	String getCurrentProviderStatus(HashMap aoHMSubSection);

	void insertSupersedingStatus(HashMap aoHMSection);

	List<WorkflowIDServiceBean> fetchNonSuspendedServiceCapacityIds(HashMap aoHMSubSection);

	void setBusinessExpirationDate(HashMap aoHMReqdProp);

	void setServiceDates(HashMap aoHMReqdProp);

	String fetchServiceElementID(HashMap aoHMSubSection);

	String fetchServiceAppStatus(HashMap aoHMSubSection);

	void insertInPrintView(HashMap aoHMSection);

	int UpdateprintView(HashMap aoHMSection);

	String GetEmail(HashMap aoHMSubSection);

	void updateSectionComments(HashMap aoHMSection);

	void updateServiceComments(HashMap aoHMSection);

	void updateBusinessComments(HashMap aoHMSection);

	String fetchWithdrawalID(String asServiceAppID);

	// method added as a part of release 3.4.0 defect 6478
	void updateUserDN_DB(HashMap aoHMSection);

	// Start of changed for release 3.10.0 enhancement 6573
	List<String> fetchBusinessAppIds_DB(HashMap aoHMSection);

	void deleteFromDocLapsingMaster(HashMap aoHMSection);

	void deleteFromDocLapsingTrans(HashMap aoHMSection);

	void deleteFromSupersedingStatus(HashMap aoHMSection);

	void deleteFromDueDateReminder(HashMap aoHMSection);

	String fetchCorporateStructure(HashMap aoHMSection);

	Integer checkDoclapsingEntry(HashMap aoHMSection);

	// End of changed for release 3.10.0 enhancement 6573

	// Start of changed for release 3.10.0 enhancement 6572
	Integer getSubSectionStatusForFilings(String asAppId);
	// End of changed for release 3.10.0 enhancement 6572

}
