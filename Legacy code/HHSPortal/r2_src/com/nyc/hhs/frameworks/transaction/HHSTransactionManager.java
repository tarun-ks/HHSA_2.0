package com.nyc.hhs.frameworks.transaction;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;

/**
 * Transaction Manager is core class for executing services in a transaction. If
 * service fails complete is rolled back (automatic or manual through logs)
 * 
 */

public class HHSTransactionManager extends TransactionManager
{
	/**
	 * This method is used to execute one transaction with the name of the
	 * transaction ID passed to it
	 * 
	 * @param aoChannel channel object in which parameters are set and
	 *            communicated to transaction layer
	 * @param asTransactionId id of the transaction to be executed
	 * @return aoChannel channel object with the values manipulated by
	 *         transaction
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	public static Channel executeTransaction(Channel aoChannel, String asTransactionId) throws ApplicationException
	{
		return executeTransaction(aoChannel, asTransactionId, HHSConstants.TRANSACTION_ELEMENT);
	}

	/**
	 * This method is used to execute component transaction with the name of the
	 * transaction ID passed to it
	 * 
	 * @param aoChannel channel object in which parameters are set and
	 *            communicated to transaction layer
	 * @param asTransactionId id of the transaction to be executed
	 * @return aoChannel channel object with the values manipulated by
	 *         transaction
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	public static Channel executeTransactionComponent(Channel aoChannel, String asTransactionId)
			throws ApplicationException
	{
		return executeTransaction(aoChannel, asTransactionId, HHSConstants.TRANSACTION_ELEMENT_COMPONENT);
	}
}
