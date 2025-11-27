package com.nyc.hhs.contractsbatch.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.batch.IBatchQueue;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSP8Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.cache.ICacheManager;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.service.filenetmanager.p8services.P8HelperServices;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.XMLUtil;

import filenet.vw.api.VWSession;

/**
 * This class will be used to perform all major actions in R3 batch process for
 * Registering the Base, Renew and Amendment type Contracts
 * 
 */
public class PaymentUpdateBatch extends P8HelperServices implements IBatchQueue
{

	private static final LogInfo LOG_OBJECT = new LogInfo(PaymentUpdateBatch.class);
	private Channel moChannelObj;
	private Boolean moBatchExecutedSuccessfuly = Boolean.TRUE;
	VWSession moPeSession = null;

	/**
	 * Blank implementation of getQueue method of interface
	 * 
	 * @param aoMParameters HashMap of Parameter
	 * @return null
	 */
	@SuppressWarnings("rawtypes")
	public List<PaymentUpdateBatch> getQueue(Map aoMParameters)
	{
		return null;
	}

	/**
	 * Default constructor Implementation
	 * 
	 * Setting ICacheManager object
	 */
	public PaymentUpdateBatch()
	{
		try
		{
			LOG_OBJECT.Debug("Constructor initialized");
			// Load property file for log4j

			LOG_OBJECT.Debug("Creating Filenet Connection");
			// Load transaction xmlsl
			ICacheManager loCacheManager = BaseCacheManagerWeb.getInstance();
			Object loCacheObject = XMLUtil.getDomObj(this.getClass().getResourceAsStream(
					HHSConstants.TRANSACTION_CONTRACTS_ELEMENT_PATH));
			loCacheManager.putCacheObject(HHSP8Constants.TRANSACTION_LOWESCASE, loCacheObject);

			Object loCacheNotificationObject = XMLUtil.getDomObj(this.getClass().getResourceAsStream(
					HHSP8Constants.EVENT_TYPE_TEMPLATE));
			loCacheManager.putCacheObject(HHSP8Constants.NOTIFICATION_CONTENT, loCacheNotificationObject);

			LOG_OBJECT.Debug("CacheManager initialized");
			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured during initializing the HHSComponentOperations constructor", aoAppEx);

		}
	}

	/**
	 * Implementation of the executeQueue method . This method will call all the
	 * other methods for executing the batch operations
	 * <ul>
	 * <li>call payment method</li>
	 * <li>call budgetAdvance method</li>
	 * </ul>
	 * @param aoLQueue List of Queue
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public void executeQueue(List aoLQueue) throws ApplicationException
	{
		try
		{
			// call the payment batch
			paymentBatch();

			// call the budget Advance batch
			budgetAdvance();
		}
		// Catch all ApplicationExceptions and throw to the caller after
		// logging error
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in PaymentUpdateBatch.executeQueue()", aoAppEx);
			throw aoAppEx;
		}
	} // end function executeQueue

	/**
	 * This method will call all the other methods for executing the payment
	 * batch operations and provides the required supporting environment for
	 * following calls
	 * <ul>
	 * <li>Transaction id : getInvoiceDetailsForBatch</li>
	 * <li>Transaction id : updateInvoiceIdForBatch</li>
	 * </ul>
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private void paymentBatch() throws ApplicationException
	{
		SqlSession loFilenetPEDBSession = null;
		try
		{
			moChannelObj = new Channel();

			TransactionManager.executeTransaction(moChannelObj, HHSConstants.GET_INVOICE_DETAILS_FOR_BATCH);
			List<HashMap> loInvoiceDetails = (List<HashMap>) moChannelObj.getData(HHSConstants.LO_INVOICE_DETAILS);
			for (int liCount = 0; liCount < loInvoiceDetails.size(); liCount++)
			{
				HashMap loHmRequiredProps = new HashMap();
				loHmRequiredProps.put(HHSConstants.CONTRACT_ID_WORKFLOW,
						String.valueOf(loInvoiceDetails.get(liCount).get(HHSConstants.CONTRACT_ID_UNDERSCORE)));
				loHmRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW,
						String.valueOf(loInvoiceDetails.get(liCount).get(HHSConstants.BUDGET_ID_HASH_KEY)));
				loHmRequiredProps.put(HHSConstants.INVOICE_ID,
						String.valueOf(loInvoiceDetails.get(liCount).get(HHSConstants.BATCH_INVOICE_ID)));
				loHmRequiredProps.put(HHSConstants.SUBMITTED_BY, HHSConstants.SYSTEM_USER);
				loHmRequiredProps.put(HHSConstants.AUDIT_PROVIDER_INITIATED_TASK, HHSConstants.BOOLEAN_TRUE);
				loHmRequiredProps.put(HHSConstants.WORKFLOW_NAME, HHSConstants.WF_PAYMENT_REVIEW);
				loHmRequiredProps.put(HHSConstants.PROPERTY_PE_TASK_SOURCE, HHSConstants.BATCH);

				P8UserSession loFilenetSession = filenetConnection.setP8SessionVariables();
				loFilenetPEDBSession = HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory().openSession();
				loFilenetSession.setFilenetPEDBSession(loFilenetPEDBSession);

				moChannelObj.setData(HHSConstants.AO_FILENET_SESSION, loFilenetSession);
				moChannelObj.setData(HHSConstants.LB_AUTH_STATUS_FLAG, HHSConstants.BOOLEAN_TRUE);
				moChannelObj.setData(HHSConstants.AO_HMWF_REQUIRED_PROPS, loHmRequiredProps);

				TransactionManager.executeTransaction(moChannelObj, HHSConstants.UPDATE_INVOICE_FOR_BATCH);
			}

			if (moBatchExecutedSuccessfuly)
			{
				LOG_OBJECT.Debug("All Payment have been processed successfully.\n");
			}
			else
			{
				LOG_OBJECT
						.Debug("Some Payment could not be processed successfully and thrown Exception. See Log file for more details.\n");
			}
		}
		// Catch all ApplicationExceptions and throw to the caller after
		// logging error
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in updatePayementStatus()", aoAppEx);
			throw aoAppEx;
		}
		finally
		{
			if (null != loFilenetPEDBSession)
			{
				loFilenetPEDBSession.close();
			}
		}
	}

	/**
	 * This method will call all the other methods for executing the
	 * budgetAdvance batch operations and provides the required supporting
	 * environment for following calls
	 * <ul>
	 * <li>Transaction id : getBudgetAdvanceForBatch</li>
	 * <li>Transaction id : updateBudgetAdvanceIdForBatch</li>
	 * </ul>
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private void budgetAdvance() throws ApplicationException
	{
		SqlSession loFilenetPEDBSession = null;
		try
		{
			moChannelObj = new Channel();
			TransactionManager.executeTransaction(moChannelObj, HHSConstants.GET_BUDGET_ADVANCE_FOR_BATCH);
			List<HashMap> loInvoiceDetails = (List<HashMap>) moChannelObj
					.getData(HHSConstants.LO_BUDGET_ADVANCE_DETAILS);
			for (int liCount = 0; liCount < loInvoiceDetails.size(); liCount++)
			{
				HashMap<String, Object> loHmRequiredProps = new HashMap<String, Object>();
				loHmRequiredProps.put(HHSConstants.BUDGET_ADVANCE_ID,
						String.valueOf(loInvoiceDetails.get(liCount).get(HHSConstants.BATCH_BUDGET_ADVANCE_ID)));
				loHmRequiredProps.put(HHSConstants.WORKFLOW_NAME, HHSConstants.WF_ADVANCE_PAYMENT_REVIEW);
				loHmRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW,
						String.valueOf(loInvoiceDetails.get(liCount).get(HHSConstants.BUDGET_ID_HASH_KEY)));
				loHmRequiredProps.put(HHSConstants.CONTRACT_ID_WORKFLOW,
						String.valueOf(loInvoiceDetails.get(liCount).get(HHSConstants.CONTRACT_ID_UNDERSCORE)));
				loHmRequiredProps.put(HHSConstants.SUBMITTED_BY, HHSConstants.SYSTEM_USER);
				loHmRequiredProps.put(HHSConstants.PROPERTY_PE_TASK_SOURCE, HHSConstants.BATCH);

				P8UserSession loFilenetSession = filenetConnection.setP8SessionVariables();
				loFilenetPEDBSession = HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory().openSession();
				loFilenetSession.setFilenetPEDBSession(loFilenetPEDBSession);

				moChannelObj.setData(HHSConstants.AO_FILENET_SESSION, loFilenetSession);
				moChannelObj.setData(HHSConstants.LB_AUTH_STATUS_FLAG, HHSConstants.BOOLEAN_TRUE);
				moChannelObj.setData(HHSConstants.AO_HMWF_REQUIRED_PROPS, loHmRequiredProps);
				TransactionManager.executeTransaction(moChannelObj, HHSConstants.UPDATE_BUDGET_ADVANCE_ID_FOR_BATCH);
			}
			if (moBatchExecutedSuccessfuly)
			{
				LOG_OBJECT.Debug("All Payment have been processed successfully.\n");
			}
			else
			{
				LOG_OBJECT
						.Debug("Some Payment could not be processed successfully and thrown Exception. See Log file for more details.\n");
			}
		}
		// Catch all ApplicationExceptions and throw to the caller after
		// logging error
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in updatePayementStatus()", aoAppEx);
			throw aoAppEx;
		}
		finally
		{
			if (null != loFilenetPEDBSession)
			{
				loFilenetPEDBSession.close();
			}

		}
	}
}