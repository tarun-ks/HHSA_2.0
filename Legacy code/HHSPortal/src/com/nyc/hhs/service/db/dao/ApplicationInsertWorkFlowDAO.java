package com.nyc.hhs.service.db.dao;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.model.ApplicationInsertWorkFlow;
import com.nyc.hhs.service.db.services.application.ApplicationInsertWorkFlowMapper;

/**
 * This DAO class contains method related application insert work flow
 * which provides the functionality for updating the Application details.
 * 
 */

public class ApplicationInsertWorkFlowDAO {

	/**
	 * This DAO class contains method related application insert work flow
	 * which provides the functionality for updating the Application details.
	 * 
	 * @param aoApplication	
	 * 				ApplicationInsertWorkFlow Bean
	 * @param aoSession
	 * 				SQL SESSION
	 * @return lbflag
	 * @throws ApplicationException
	 */
	public boolean update(ApplicationInsertWorkFlow aoApplication, SqlSession aoSession) throws ApplicationException {
		boolean lbFlag = false;
		try {
			
			ApplicationInsertWorkFlowMapper loMapper = aoSession.getMapper(ApplicationInsertWorkFlowMapper.class);
			loMapper.updateApplicationDetails(aoApplication);
			lbFlag = true;
		
		} catch (Exception loEx) {
			lbFlag = false;
			throw new ApplicationException("Exception occured while inserting data in taskinformation table "+loEx+" for "+aoApplication , loEx);
		}
		return lbFlag;
	}
}