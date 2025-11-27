package com.nyc.hhs.service.db.services.application;

import java.util.HashMap;

/**
 *  TransactionLogMapper is an interface between the 
 *  DAO and database layer for Transaction Log
 *  
 */
public interface TransactionLogMapper {
	
	@SuppressWarnings("rawtypes")
	
	void logTransactionFailure(HashMap aoTransationMap);
	
}
