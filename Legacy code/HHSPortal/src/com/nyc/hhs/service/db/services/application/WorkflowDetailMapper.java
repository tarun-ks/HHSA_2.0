package com.nyc.hhs.service.db.services.application;

import java.util.HashMap;
import java.util.List;

import com.nyc.hhs.model.DocumentBean;
import com.nyc.hhs.model.WorkflowDetails;

/**
 *  WorkflowDetailMapper is an interface between the DAO and database layer to
 *  fetch provider details, sub section details and documents details from 
 *  database for task.
 *  
 */
public interface WorkflowDetailMapper {

	List<WorkflowDetails> select(HashMap asAppId);
	
	List<WorkflowDetails> providerDetails(HashMap aoProviderName);
	
	List<WorkflowDetails> noOfServices(HashMap aoAppId);
	
	List<WorkflowDetails> questionDB(HashMap asAppId);
	
	List<WorkflowDetails> documentDetails(HashMap asAppId);
	
	List<WorkflowDetails> subSectionDetails(HashMap asAppId);
	
	List<WorkflowDetails> subSectionServiceDetails(HashMap asAppId);
	
	List<WorkflowDetails> documentServiceDetails(HashMap asAppId);
	
	List<DocumentBean> getDocList(HashMap aoRequdFields);
}
