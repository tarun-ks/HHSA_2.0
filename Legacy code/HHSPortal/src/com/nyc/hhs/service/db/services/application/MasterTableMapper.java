package com.nyc.hhs.service.db.services.application;

import java.util.List;

import com.nyc.hhs.model.ActionStatusBean;
import com.nyc.hhs.model.StaffDetails;

/**
 *  MasterTableMapper is an interface between the DAO and database layer 
 *  to fetch data from different database tables for task .
 *  
 */

public interface MasterTableMapper {

	List<String> fetchStatusMasterData(String asStatusType);
	List<StaffDetails> fetchCityUserDetails();
	// Start: QC 8914 ReadOnlyRole R7.2.0  
	public String fetchCityAgencyUserOversightFlag(String asUserId);
	// End: QC 8914 ReadOnlyRole R7.2.0  
	
	
	//<!-- [Start] R9.6.4 QC9701 -->
	public Integer updateActionStatus(ActionStatusBean aoActionStat);

	public ActionStatusBean getActionStatusByAgency(String aoAgencyId);
	
	public List<ActionStatusBean> getActionStatusAll();
	
	public Integer updateActionStatusProvider(ActionStatusBean aoActionStat);

	public ActionStatusBean getActionStatusByProvider(String aoProviderId);
	
	public List<ActionStatusBean> getActionStatusProviderAll();
	//<!-- [End] R9.6.4 QC9701 -->
	
}
