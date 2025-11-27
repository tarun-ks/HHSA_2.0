package com.nyc.hhs.service.db.services.application;

import com.nyc.hhs.model.ApplicationInsertWorkFlow;

/**
 *  ApplicationInsertWorkFlowMapper is a mapper class 
 *  for Application insert workflow to insert and update entries.
 *  
 */

public interface ApplicationInsertWorkFlowMapper
{
	void insertApplicationDetails(ApplicationInsertWorkFlow aoApplicationDetails);
	void updateApplicationDetails(ApplicationInsertWorkFlow aoUpdateApplicationDetails);
}
