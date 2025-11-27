package com.nyc.hhs.service.db.services.application;


import java.util.List;
import java.util.Map;

import com.nyc.hhs.model.BusinessApplicationSummary;

/**
 * BusinessApplicationSummaryMapper is an interface between the DAO and database 
 * layer for business and service application related transactions to retrieve 
 * entries. 
 * 
 */

public interface BusinessApplicationSummaryMapper {

	List<BusinessApplicationSummary> getBusinessApplicationSummary(Map<String, String> aoMap);
}
