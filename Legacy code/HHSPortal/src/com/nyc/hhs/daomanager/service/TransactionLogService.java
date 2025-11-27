package com.nyc.hhs.daomanager.service;

import java.util.HashMap;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.util.DAOUtil;

/**
 * 
 * TransactionLogService: This method inserts the entry in the database when the
 *                        transaction fails.
 * 
 */
public class TransactionLogService
{
	
	private static final LogInfo LOG_OBJECT = new LogInfo(TransactionLogService.class);
	
	/**
	 * This method inserts the entry in the database when the transaction fails.
	 * 
	 * @param aoTransationMap
	 *            Map containing transaction data
	 * @param aoMybatisSession
	 *            MyBatis Sql Session
	 * @throws ApplicationException
	 */
	public void logTransactionFailure(HashMap aoTransationMap, SqlSession aoMybatisSession) throws ApplicationException
	{
		try
		{
			DAOUtil.masterDAO(aoMybatisSession, aoTransationMap, ApplicationConstants.MAPPER_CLASS_TRANSACTION_LOG_MAPPER, "logTransactionFailure",
					"java.util.HashMap");
		}
		catch (ApplicationException aoAppEx)
		{
			throw aoAppEx;
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error occured while inserting the entry in the database when the transaction fails. " , aoExp);
		}
	}
}
