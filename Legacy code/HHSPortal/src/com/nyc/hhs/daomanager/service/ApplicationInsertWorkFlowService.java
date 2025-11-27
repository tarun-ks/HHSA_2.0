package com.nyc.hhs.daomanager.service;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.model.ApplicationInsertWorkFlow;
import com.nyc.hhs.service.db.dao.ApplicationInsertWorkFlowDAO;


/**
 * ApplicationInsertWorkFlowService: This Class is the Outer service that
 *                                   creates the SQL session and make calls to
 *                                   DAO methods, on success of all DAO calls
 *                                   data is committed into database else all
 *                                   the changes are rolled back and exception
 *                                   is thrown.
 * */

@Service
public class ApplicationInsertWorkFlowService extends com.nyc.hhs.daomanager.service.ServiceState
{

	/**
	 * This method inserts application information in database with the workflow
	 * number obtained from workflow
	 * 
	 * @param asWobNumber
	 *            Workflow Id
	 * @param asApplicationID
	 *            Application Id
	 * @param asStatus
	 *            Application Status
	 * @param aoMybatisSession
	 *            MyBatis Sql Session
	 * @return lbInsertStatus Insertion Success/Failure session
	 * @throws ApplicationException
	 */
	public boolean insertApplicationDetails(String asWobNumber, String asApplicationID, String asStatus, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		boolean lbInsertStatus = false;

		ApplicationInsertWorkFlow loApplicationUpdateWorkFlow = new ApplicationInsertWorkFlow();
		loApplicationUpdateWorkFlow.setApplicationID(asApplicationID);
		loApplicationUpdateWorkFlow.setWobNumber(asWobNumber);
		loApplicationUpdateWorkFlow.setStatus(asStatus);
		ApplicationInsertWorkFlowDAO loApplicationDao = new ApplicationInsertWorkFlowDAO();
		lbInsertStatus = loApplicationDao.update(loApplicationUpdateWorkFlow, aoMybatisSession);

		return lbInsertStatus;

	}

}