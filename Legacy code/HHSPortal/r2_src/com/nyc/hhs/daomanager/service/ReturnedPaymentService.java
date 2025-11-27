package com.nyc.hhs.daomanager.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.BudgetList;
import com.nyc.hhs.model.BulkNotificationList;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.ProgramNameInfo;
import com.nyc.hhs.model.ReturnPaymentNotification;
import com.nyc.hhs.model.ReturnedPayment;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.HHSUtil;

/**
 * <p>
 * This service class will be used to fetch all the data for Returned Payment.
 * All render and action methods for Returned Payment Summary, Add returned
 * payment, notify provider, task etc screens will use this service to
 * fetch/insert/update data from/to database.
 * </p>
 * 
 */

public class ReturnedPaymentService extends ServiceState
{

	/**
	 * Logger Object Declared for ReturnedPaymentService
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(ReturnedPaymentService.class);

	/**
	 * Added in Release 6 as part of Return payment
	 * This method will cancel any Return payment in pending submission 
	 * or pending approval state.
	 * @param aoMyBatisSession
	 * @param aoReturnedPayment
	 * @return loUpdatePaymentStatusFlag
	 * @throws ApplicationException
	 */
	public Boolean cancelReturnedPayment(SqlSession aoMyBatisSession, ReturnedPayment aoReturnedPayment)
			throws ApplicationException
	{

		//*** Start QC 9585 R 8.9 do not expose password for service account in logs
		String param = CommonUtil.maskPassword(aoReturnedPayment);
		LOG_OBJECT.Debug("Entered into cancel Returned Payment Status method with paramters:: " + param);
		//*** End QC 9585 R 8.9 do not expose password for service account in logs

		Boolean loUpdatePaymentStatusFlag = Boolean.FALSE;
		int liUpdated = 0;
		try
		{
			liUpdated = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoReturnedPayment,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSR5Constants.UPDATE_RETURNED_PAYMENT_STATUS,
					HHSR5Constants.INPUT_PARAM_CLASS_RET_PAY_BEAN);
			if (liUpdated > 0)
			{
				loUpdatePaymentStatusFlag = Boolean.TRUE;
			}
			setMoState("Returned Payment Status sucessfully updated to cancelled for ReturnedPaymentID"
					+ aoReturnedPayment.getReturnedPaymentId());
		}
		// handling Application exception
		catch (ApplicationException loExp)
		{
			setMoState("Error while updating Returned Payment Status :");
			loExp.addContextData("Exception occured for returned payment cancel status update: cancelReturnedPayment ",
					loExp);
			LOG_OBJECT.Error("Error while updating Returned Payment Status :", loExp);
			throw loExp;
		}
		// handling exception other than Application exception
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error while updating Returned Payment Status :", loExp);
			setMoState("Error while updating Returned Payment Status :");
			throw new ApplicationException("Exception while updating Returned Payment Status ", loExp);
		}

		return loUpdatePaymentStatusFlag;
	}

	/**
	 * Added in Release 6 as part of Return payment
	 * This method will initiate any Return payment which is in pending submission state
	 * @param aoMyBatisSession
	 * @param aoReturnedPayment
	 * @return loUpdatePaymentStatusFlag
	 * @throws ApplicationException
	 */
	public Boolean initiateReturnedPayment(SqlSession aoMyBatisSession, ReturnedPayment aoReturnedPayment)
			throws ApplicationException
	{

		//*** Start QC 9585 R 8.9 do not expose password for service account in logs
		String param = CommonUtil.maskPassword(aoReturnedPayment);
		LOG_OBJECT.Debug("Entered into initiate Returned Payment with paramters:: " + param);
		//*** End QC 9585 R 8.9 do not expose password for service account in logs

		Boolean loUpdatePaymentStatusFlag = Boolean.FALSE;
		int liUpdated = 0;
		try
		{
			liUpdated = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoReturnedPayment,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSR5Constants.UPDATE_RETURNED_PAYMENT_STATUS,
					HHSR5Constants.INPUT_PARAM_CLASS_RET_PAY_BEAN);
			if (liUpdated > 0)
			{
				loUpdatePaymentStatusFlag = Boolean.TRUE;
			}
			setMoState("Returned Payment Status sucessfully updated to pending submission for ReturnedPaymentID"
					+ aoReturnedPayment);
		}
		// handling Application exception
		catch (ApplicationException loExp)
		{
			setMoState("Error while updating Returned Payment Status :");
			loExp.addContextData(
					"Exception occured for returned payment pending submission status update: initiateReturnedPayment ",
					loExp);
			LOG_OBJECT.Error("Error while updating Returned Payment Status :", loExp);
			throw loExp;
		}
		// handling exception other than Application exception
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error while updating Returned Payment Status :", loExp);
			setMoState("Error while updating Returned Payment Status :");
			throw new ApplicationException("Exception while updating Returned Payment Status ", loExp);
		}

		return loUpdatePaymentStatusFlag;
	}

	/**
	 * Added in Release 6 as part of Return payment 
	 * It is used to insert data in data base for add return payment
	 * <ul>
	 * <li>Execute query id <b> fetchInfoForReturnPayment</b></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoReturnedPayment ReturnedPayment
	 * @return aoReturnedPayment ReturnedPayment
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	public ReturnedPayment addReturnPaymentInfo(SqlSession aoMybatisSession, ReturnedPayment aoReturnedPayment)
			throws ApplicationException
	{
		//*** Start QC 9585 R 8.9 do not expose password for service account in logs
		String param = CommonUtil.maskPassword(aoReturnedPayment);
		LOG_OBJECT.Debug("Entered into addReturnPaymentInfo with paramters:: " + param);
		//*** End QC 9585 R 8.9 do not expose password for service account in logs

		try
		{
			DAOUtil.masterDAO(aoMybatisSession, aoReturnedPayment, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSR5Constants.INSERT_RETURN_PAYMENT, HHSR5Constants.RETURNED_PAYMENT_BEAN);
		}
		catch (ApplicationException loExp)
		{
			loExp.addContextData("ApplicationException occured for Add return Payment" + ": returnPaymentInfo ", loExp);
			LOG_OBJECT.Error("ApplicationException occured for Add return Payment : returnPaymentInfo ", loExp);
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured for Add return Payment : returnPaymentInfo ", loExp);
			loAppEx.addContextData("Exception occured for Add return Payment : returnPaymentInfo ", loExp);
			LOG_OBJECT.Error("Exception occured for Add return Payment : returnPaymentInfo", loExp);
			throw loAppEx;
		}

		return aoReturnedPayment;

	}

	/**
	 * Added in Release 6 as part of Return payment 
	 * It is used to fetch data in data base for add return payment
	 * <ul>
	 * <li>Execute query id <b> fetchInfoForReturnPayment</b></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoReturnedPayment ReturnedPayment
	 * @return loReturnedPayments 
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	@SuppressWarnings("unchecked")
	public List<ReturnedPayment> fetchReturnedPaymentDetails(SqlSession aoMybatisSession,
			ReturnedPayment aoReturnedPayment) throws ApplicationException
	{
		//*** Start QC 9585 R 8.9 do not expose password for service account in logs
		String param = CommonUtil.maskPassword(aoReturnedPayment);
		LOG_OBJECT.Debug("Entered into fetchReturnedPaymentDetails with paramters:: " + param);
		//*** End QC 9585 R 8.9 do not expose password for service account in logs


		List<ReturnedPayment> loReturnedPayments = new LinkedList<ReturnedPayment>();
		try
		{
			loReturnedPayments = (List<ReturnedPayment>) DAOUtil.masterDAO(aoMybatisSession, aoReturnedPayment,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSR5Constants.GET_RETURNED_PAYMENT_SUMMARY_DB,
					HHSR5Constants.RETURNED_PAYMENT_BEAN);
		}
		catch (ApplicationException loExp)
		{
			loExp.addContextData("ApplicationException occured for Add return Payment" + ": returnPaymentInfo ", loExp);
			LOG_OBJECT.Error("ApplicationException occured for Add return Payment : returnPaymentInfo ", loExp);
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured for Add return Payment : returnPaymentInfo ", loExp);
			loAppEx.addContextData("Exception occured for Add return Payment : returnPaymentInfo ", loExp);
			LOG_OBJECT.Error("Exception occured for Add return Payment : returnPaymentInfo", loExp);
			throw loAppEx;
		}
		return loReturnedPayments;
	}

	/**
	 * Added in Release 6 as part of Return payment
	 * This method will get last notified date on which provider was notified regarding the Return payment
	 * @param aoMybatisSession
	 * @param asBudgetId
	 * @return lsLastNotifiedDate
	 * @throws ApplicationException
	 */
	public String getLastNotifiedDate(SqlSession aoMybatisSession, String asBudgetId) throws ApplicationException
	{

		LOG_OBJECT.Info("Entered into fetchReturnedPaymentDetails with paramters::" + asBudgetId);
		String lsLastNotifiedDate = HHSR5Constants.EMPTY_STRING;
		try
		{
			if (null != asBudgetId && !asBudgetId.isEmpty())
			{
				lsLastNotifiedDate = (String) DAOUtil.masterDAO(aoMybatisSession, asBudgetId,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSR5Constants.GET_LAST_NOTIFIED_DATE,
						HHSConstants.JAVA_LANG_STRING);
			}
		}
		catch (ApplicationException loExp)
		{
			loExp.addContextData("ApplicationException occured for Add return Payment" + ": returnPaymentInfo ", loExp);
			LOG_OBJECT.Error("ApplicationException occured for Add return Payment : returnPaymentInfo ", loExp);
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured for Add return Payment : returnPaymentInfo ", loExp);
			loAppEx.addContextData("Exception occured for Add return Payment : returnPaymentInfo ", loExp);
			LOG_OBJECT.Error("Exception occured for Add return Payment : returnPaymentInfo", loExp);
			throw loAppEx;
		}
		return lsLastNotifiedDate;
	}

	/**
	 * Added in Release 6 as part of Return payment
	 * This method will get the unrecouped amount from database for a particular Contract
	 * @param aoMybatisSession
	 * @param asBudgetId
	 * @return lsUnRecoupedAmount
	 * @throws ApplicationException
	 */
	public String getUnRecoupedAmount(SqlSession aoMybatisSession, String asBudgetId) throws ApplicationException
	{

		LOG_OBJECT.Info("Entered into getUnRecoupedAmount with paramters::" + asBudgetId);
		String lsUnRecoupedAmount = HHSR5Constants.EMPTY_STRING;
		try
		{
			if (null != asBudgetId && !asBudgetId.isEmpty())
			{
				lsUnRecoupedAmount = (String) DAOUtil.masterDAO(aoMybatisSession, asBudgetId,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSR5Constants.GET_UNRECOUPED_AMOUNT,
						HHSConstants.JAVA_LANG_STRING);
			}
		}
		catch (ApplicationException loExp)
		{
			loExp.addContextData("ApplicationException occured for Add return Payment" + ": returnPaymentInfo ", loExp);
			LOG_OBJECT.Error("ApplicationException occured for Add return Payment : returnPaymentInfo ", loExp);
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured for Add return Payment : returnPaymentInfo ", loExp);
			loAppEx.addContextData("Exception occured for Add return Payment : returnPaymentInfo ", loExp);
			LOG_OBJECT.Error("Exception occured for Add return Payment : returnPaymentInfo", loExp);
			throw loAppEx;
		}
		return lsUnRecoupedAmount;
	}

	/**
	 * Added in Release 6 as part of Return payment
	 * This method will get the total returned payment amount approved by specified level agencies
	 * @param aoMybatisSession
	 * @param asBudgetId
	 * @return lsTotalApprovedRetAmount
	 * @throws ApplicationException
	 */
	public String getTotalApprovedRetPayAmount(SqlSession aoMybatisSession, String asBudgetId)
			throws ApplicationException
	{

		LOG_OBJECT.Info("Entered into getTotalApprovedRetPayAmount with paramters::" + asBudgetId);
		String lsTotalApprovedRetAmount = HHSR5Constants.EMPTY_STRING;
	
		try
		{
			if (null != asBudgetId && !asBudgetId.isEmpty())
			{
				lsTotalApprovedRetAmount = (String) DAOUtil.masterDAO(aoMybatisSession, asBudgetId,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
						HHSR5Constants.GET_TOTAL_APPROVED_RET_PAY_AMOUNT, HHSConstants.JAVA_LANG_STRING);
			}
		}
		catch (ApplicationException loExp)
		{
			loExp.addContextData("ApplicationException occured for Add return Payment" + ": returnPaymentInfo ", loExp);
			LOG_OBJECT.Error("ApplicationException occured for Add return Payment : returnPaymentInfo ", loExp);
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured for Add return Payment : returnPaymentInfo ", loExp);
			loAppEx.addContextData("Exception occured for Add return Payment : returnPaymentInfo ", loExp);
			LOG_OBJECT.Error("Exception occured for Add return Payment : returnPaymentInfo", loExp);
			throw loAppEx;
		}
		return lsTotalApprovedRetAmount;
	}

	/**
	 * Added in Release 6 as part of Return payment
	 * This method will set the Returned payment id for a particular task with respect to task audit
	 * @param aoHmRequiredProps
	 * @param aoReturnedPayment
	 * @param aoAuthFlag
	 * @return aoHmRequiredProps
	 */
	public HashMap<String, Object> setTaskReturnedPaymentId(HashMap<String, Object> aoHmRequiredProps,
			ReturnedPayment aoReturnedPayment, Boolean aoAuthFlag)
	{

		if (aoAuthFlag)
		{
			aoHmRequiredProps.put(HHSR5Constants.RETURNED_PAYMENT_DETAILS_ID, aoReturnedPayment.getReturnedPaymentId());
		}
		return aoHmRequiredProps;
	}

	/**
	 * Added in Release 6 as part of Return payment
	 * It will set the audit history for a particular returned payment.
	 * @param aoReturnedPayment
	 * @param abAuthFlag
	 * @return loAuditBeanList
	 */
	public List<HhsAuditBean> setAuditReturnedPaymentId(ReturnedPayment aoReturnedPayment, Boolean abAuthFlag)
	throws ApplicationException
	{
		List<HhsAuditBean> loAuditBeanList = new ArrayList<HhsAuditBean>();
		StringBuffer loDataSb = new StringBuffer();
		try
		{
			if (abAuthFlag)
			{
				String lsCheckReceived = aoReturnedPayment.getCheckReceived();
				if (null != lsCheckReceived && lsCheckReceived.equalsIgnoreCase(HHSConstants.YES_UPPERCASE))
				{
					loDataSb.append(HHSConstants.STATUS_CHANGED_TO).append(HHSConstants.DOUBLE_QUOTE)
							.append(HHSConstants.STATUS_PENDING_APPROVAL).append(HHSConstants.STR);
				}
			}
			else
			{
				loDataSb.append(HHSConstants.STATUS_CHANGED_TO).append(HHSConstants.DOUBLE_QUOTE)
						.append(HHSConstants.STATUS_PENDING_SUBMISSION).append(HHSConstants.STR);
			}
			loAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSConstants.STATUS_CHANGE, HHSR5Constants.STATUS_CHANGE,
					loDataSb.toString(), HHSR5Constants.RETURNED_PAYMENT_STRING,
					aoReturnedPayment.getReturnedPaymentId(), aoReturnedPayment.getCreatedByUserId(),
					HHSR5Constants.AGENCY_AUDIT));
			loAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSConstants.PROPERTY_TASK_CREATION_EVENT,
					HHSConstants.PROPERTY_TASK_CREATION_EVENT, HHSConstants.PROPERTY_TASK_CREATION_DATA,
					HHSR5Constants.TASK_RETURN_PAYMENT_REVIEW, aoReturnedPayment.getReturnedPaymentId(),
					aoReturnedPayment.getCreatedByUserId(), HHSR5Constants.AGENCY_AUDIT));
		}
		
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured for setting audit for return Payment : setAuditReturnedPaymentId ", loExp);
			loAppEx.addContextData("Exception occured for setting audit for return Payment : setAuditReturnedPaymentId ", loExp);
			LOG_OBJECT.Error("Exception occured for setting audit for return Payment : setAuditReturnedPaymentId ", loExp);
			throw loAppEx;
		}
		return loAuditBeanList;
	}

	/**
	 * Added in Release 6 as part of Return payment
	 * This method is used to get the complete history of notifications sent by the agency to the provider
	 * against the specified returned payment.
	 * Added in Release 6 for Returned Payment
	 * @param aoMybatisSession SqlSession object
	 * @param asBudgetId
	 * @return loListDoc
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<ReturnPaymentNotification> getNotificationHistory(SqlSession aoMybatisSession, String asBudgetId)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered into getNotificationHistory with paramters::" + asBudgetId);

		List<ReturnPaymentNotification> loListDoc = new ArrayList<ReturnPaymentNotification>();
		try
		{
			if (null != asBudgetId && !asBudgetId.isEmpty())
			{
				loListDoc = (List<ReturnPaymentNotification>) DAOUtil.masterDAO(aoMybatisSession, asBudgetId,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSR5Constants.GET_NOTIFICATION_HISTORY,
						HHSR5Constants.JAVA_LANG_STRING);
			}
		}
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("ApplicationException occured for notification history : getNotificationHistory ", loExp);
			throw loExp;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error while updating Returned Payment Status :", loExp);
			setMoState("Error while updating Returned Payment Status :");
			throw new ApplicationException("Exception while updating Returned Payment Status ", loExp);
		}
		return loListDoc;
	}

	/**
	 * Added in Release 6 as part of Return payment
	 * This method is used to get Returned payment summary from database 
	 * Added in Release 6 for Returned Payment
	 * @param aoMybatisSession
	 * @param asReturnedPaymentId
	 * @return loretPayment
	 * @throws ApplicationException
	 */
	public ReturnedPayment getReturnedPaymentSummaryDB(SqlSession aoMybatisSession, String asReturnedPaymentId)
			throws ApplicationException
	{

		LOG_OBJECT.Info("Entered into getReturnedPaymentSummaryDB with paramters::" + asReturnedPaymentId);
		ReturnedPayment loRetPayment = null;
		try
		{
			if (null != asReturnedPaymentId && !asReturnedPaymentId.isEmpty())
			{
				loRetPayment = (ReturnedPayment) DAOUtil.masterDAO(aoMybatisSession, asReturnedPaymentId,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSR5Constants.GET_RETURNED_PAYMENT_SUMMARY,
						HHSR5Constants.JAVA_LANG_STRING);
			}
		}
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("ApplicationException occured for payment summary from DB : getReturnedPaymentSumaryDB ",
					loExp);
			throw loExp;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error while updating Returned Payment Status :", loExp);
			setMoState("Error while updating Returned Payment Status :");
			throw new ApplicationException("Exception while updating Returned Payment Status ", loExp);
		}
		return loRetPayment;
	}

	/**
	 * Added in Release 6 as part of Return payment
	 * This method id used to get list of check type document id corresponding
	 * to particular Budget Id 
	 * 
	 * @param aoMybatisSession SQL session object
	 * @param asReturnPaymentId return payment id
	 * @return loDocIdList list of document ids
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<String> getDocumentForReturnedPaymentSumaryFN(SqlSession aoMybatisSession, String asReturnPaymentId)
			throws ApplicationException
	{

		LOG_OBJECT.Info("Entered into getDocumentForReturnedPaymentSumaryFN with paramters::" + asReturnPaymentId);
		List<String> loDocIdList = new ArrayList<String>();
		try
		{
			if (null != asReturnPaymentId && !asReturnPaymentId.isEmpty())
			{
				loDocIdList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, asReturnPaymentId,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
						HHSR5Constants.GET_DOC_ID_FOR_RETURNED_PAYMENT_SUMMARY_DB, HHSR5Constants.JAVA_LANG_STRING);
			}
		}
		catch (ApplicationException loExp)
		{
			LOG_OBJECT
					.Error("ApplicationException occured for document id corresponding to particular Budget Id : getDocumentForReturnedPaymentSumaryFN ",
							loExp);
			throw loExp;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error while updating Returned Payment Status :", loExp);
			setMoState("Error while updating Returned Payment Status :");
			throw new ApplicationException("Exception while updating Returned Payment Status ", loExp);
		}
		return loDocIdList;
	}

	/**
	 * Added in Release 6 as part of Return payment 
	 * It will save the return Payment details when clicked on 
	 * save button in task detail page.
	 * @param aoMybatiSession
	 * @param aoHashMap
	 * @return boolean lbUpdateStatus return payment saved
	 * @throws ApplicationException
	 */
	public boolean saveReturnPaymentDetails(SqlSession aoMybatiSession, HashMap<String, String> aoHashMap)
			throws ApplicationException
	{

		//*** Start QC 9585 R 8.9 do not expose password for service account in logs
		String param = CommonUtil.maskPassword(aoHashMap);
		LOG_OBJECT.Debug("Entered into saveReturnPaymentDetails with paramters:: " + param);
		//*** End QC 9585 R 8.9 do not expose password for service account in logs

		boolean lbUpdateStatus = false;
		Integer loRentCount = HHSConstants.INT_ZERO;
		try
		{
			if (null != aoHashMap.get(HHSConstants.RETURN_PAYMENT_DETAIL_ID))
			{
				loRentCount = (Integer) DAOUtil.masterDAO(aoMybatiSession, aoHashMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
						HHSConstants.CBY_UPDATE_RETURN_PAYMENT_DETAIL, HHSR5Constants.JAVA_UTIL_HASH_MAP);
				// Check, if the integer is updated as '1' after doing the
				// modification, else will return as false status update
				if (loRentCount > HHSConstants.INT_ZERO)
				{
					lbUpdateStatus = true;
				}
				// checking for edit status
				if (lbUpdateStatus)
				{
					setMoState("Return Payment Details: saveReturnPaymentDetails() edit successfully.");
				}
				else
				{
					setMoState("Return Payment Details: updateRate() failed to edit.");
					throw new ApplicationException(
							"Error occured while edit at ContractBudgetService: updateRate() for id:"
									+ aoHashMap.get(HHSConstants.RETURN_PAYMENT_DETAIL_ID));
				}
			}
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */

		catch (ApplicationException loExp)
		{
			setMoState("error occured while updating Return Payment Details for business type id "
					+ aoHashMap.get(HHSConstants.RETURN_PAYMENT_DETAIL_ID));
			loExp.addContextData("Exception occured while updating Rent ", loExp);
			LOG_OBJECT.Error("error occured while updating Rent ", loExp);
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while edit in ContractBudgetService:Return Payment Details ", loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: saveReturnPaymentDetails method - failed to edit"
					+ aoHashMap.get(HHSConstants.RETURN_PAYMENT_DETAIL_ID) + " \n");
			throw new ApplicationException("Exception occured while edit in saveReturnPaymentDetails ", loAppEx);
		}
		return lbUpdateStatus;
	}

	/**
	 * Added in Release 6 as part of Return payment
	 * This method is used to fetch return payment details for a particular return payment id
	 * @param aoMybatisSession
	 * @param aoHashMap
	 * @return loReturnedPayment return payment details
	 * @throws ApplicationException
	 */
	public ReturnedPayment fetchReturnPaymentDetails(SqlSession aoMybatisSession, HashMap<String, String> aoHashMap)
			throws ApplicationException
	{

		//*** Start QC 9585 R 8.9 do not expose password for service account in logs
		String param = CommonUtil.maskPassword(aoHashMap);
		LOG_OBJECT.Debug("Entered into fetchReturnPaymentDetails with paramters:: " + param);
		//*** End QC 9585 R 8.9 do not expose password for service account in logs

		ReturnedPayment loReturnedPayment = null;

		try
		{
			String lsReturnedId = (String) aoHashMap.get(HHSConstants.RETURN_PAYMENT_DETAIL_ID);

			if (null != lsReturnedId && !lsReturnedId.isEmpty())
			{
				loReturnedPayment = (ReturnedPayment) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
						HHSR5Constants.FETCH_RETURNED_PAYMENT_SUMMARY, HHSConstants.JAVA_UTIL_HASH_MAP);
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.RETURN_PAYMENT_DETAIL_ID, aoHashMap);
			LOG_OBJECT
					.Error("Exception occured while retrieveing Return Payment Information in ContractBudgetService ",
							loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: fetchReturnedPaymentSummary method - failed to fetch"
					+ aoHashMap + " \n");
			throw loAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception loAppEx)
		{
			LOG_OBJECT
					.Error("Exception occured while retrieveing Return Payment Information in ContractBudgetService ",
							loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: fetchReturnedPaymentSummary method - failed to fetch"
					+ aoHashMap + " \n");
			throw new ApplicationException("Error occured while retrieving Contract Summary", loAppEx);
		}
		return loReturnedPayment;
	}

	/**
	 * Added in Release 6 as part of Return payment
	 * This method is used to set payment record status for approval of payment
	 * review Task.
	 * <ul>
	 * <li>IUpdate the status of the advance</li>
	 * <li>This query used: setPeriod</li>
	 * <li>This query used: setPaymentStatus</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession Mybatis Session Object.
	 * @param aoFinalFinish FinalFinish parameter.
	 * @param aoTaskDetailsBean TaskDetailsBean parameter.
	 * @param asBudgetStatus BudgetStatus parameter.
	 * @return lbUpdateStatus
	 * @throws ApplicationException an exception object
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public boolean updateReturnedPaymentForApprovedStatus(SqlSession aoMyBatisSession, Boolean aoFinalFinish,
			TaskDetailsBean aoTaskDetailsBean, String asBudgetStatus) throws ApplicationException
	{
	
		//*** Start QC 9585 R 8.9 do not expose password for service account in logs
		String param = CommonUtil.maskPassword(aoTaskDetailsBean);
		LOG_OBJECT.Info("Entered into updateReturnedPaymentForApprovedStatus with paramters::" + aoFinalFinish
				+ " ::  "+ param +" ::  "+ asBudgetStatus);
		//*** End QC 9585 R 8.9 do not expose password for service account in logs

		boolean lbUpdateStatus = false;
		Integer loUpdateCount = HHSConstants.INT_ZERO;
		if (aoFinalFinish)
		{
			try
			{
				Map loHashMap = new HashMap<String, String>();

				loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, aoTaskDetailsBean.getContractId());
				loHashMap.put(HHSConstants.RETURN_PAYMENT_DETAIL_ID, aoTaskDetailsBean.getReturnPaymentDetailId());
				loHashMap.put(HHSConstants.STATUS_ID, asBudgetStatus);
				loHashMap.put(HHSConstants.MODIFY_BY, aoTaskDetailsBean.getUserId());

				ReturnedPayment loReturnedPayment = new ReturnedPayment();
				loReturnedPayment.setCheckStatus(asBudgetStatus);
				loReturnedPayment.setModifiedByUserId(aoTaskDetailsBean.getUserId());
				loReturnedPayment.setReturnedPaymentId(aoTaskDetailsBean.getReturnPaymentDetailId());
				loUpdateCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loReturnedPayment,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
						HHSR5Constants.UPDATE_RETURNED_PAYMENT_STATUS, HHSR5Constants.RETURNED_PAYMENT_BEAN);
				setMoState("Transaction Success:: PaymentModuleService:setPaymentStatus"
						+ " method - success to update record " + " \n");
				if (loUpdateCount > HHSConstants.INT_ZERO)
				{
					lbUpdateStatus = true;
				}

			}
			// catch any application exception thrown from the code due to
			// UPDATE
			// statement and throw it
			// forward
			catch (ApplicationException loAppEx)
			{
				LOG_OBJECT.Error("ApplicationException occured while executing query setPaymentStatus ", loAppEx);
				setMoState("Transaction Failed:: PaymentModuleService:setPaymentStatus "
						+ " method - failed to update record " + " \n");
				loAppEx.addContextData("BudgetId passed: ", aoTaskDetailsBean.getBudgetId());
				throw loAppEx;
			}
			// catch any exception thrown other than application Exception
			catch (Exception loAppEx)
			{
				LOG_OBJECT.Error("Exception occured while executing query in setPaymentStatus ", loAppEx);
				setMoState("Transaction Failed:: PaymentModuleService:setPaymentStatus method"
						+ " - failed to update record " + " \n");
				throw new ApplicationException("Exception occured while executing query in setPaymentStatus ", loAppEx);
			}
		}

		return lbUpdateStatus;
	}

	/**
	 * Added in Release 6 as part of Return payment
	 * It will insert the details of the
	 * returned payment documents uploaded or added by an agency
	 * <ul>
	 * <li>Get the parameter map from the channel</li>
	 * <li>Execute query with ID <b>insertReturnedPaymentDocumentDetails</b>
	 * from contract budget mapper.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession sql Session
	 * @param aoParamMap Parameter map Object
	 * @return liRowsUpdated number of rows inserted
	 * @throws ApplicationException throws application exception
	 */
	public Integer insertReturnedPaymentDocumentDetails(SqlSession aoMybatisSession, Map<String, Object> aoParamMap)
			throws ApplicationException
	{
		int liRowsUpdated = 0;
		try
		{
			//*** Start QC 9585 R 8.9 do not expose password for service account in logs
			String param = CommonUtil.maskPassword(aoParamMap);
			LOG_OBJECT.Info("Entered into insertReturnedPaymentDocumentDetails with paramters::" + param);
			//*** End QC 9585 R 8.9 do not expose password for service account in logs

			liRowsUpdated = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoParamMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSR5Constants.INSERT_RETURNED_PAYMENT_DOCUMENT_DETAILS, HHSConstants.JAVA_UTIL_MAP);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("Exception occured while inserting the returned payment document list : ", loExp);
			setMoState("Error Occured while inserting the returned payment document list :");
			loExp.addContextData("Parameter map passed for the insertion query : ", aoParamMap);
			throw loExp;
		}
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured for Add return Payment : returnPaymentInfo ", loExp);
			loAppEx.addContextData("Exception occured for Add return Payment : returnPaymentInfo ", loExp);
			LOG_OBJECT.Error("Exception occured for Add return Payment : returnPaymentInfo", loExp);
			throw loAppEx;
		}
		return liRowsUpdated;
	}

	/**
	 * Added in Release 6 as part of Return payment
	 * This method will get the current status of returned payment.
	 * @param aoMyBatisSession
	 * @param asReturnedPaymentId
	 * @return lsStatus
	 * @throws ApplicationException
	 */
	public String getReturnedPaymentStatus(SqlSession aoMyBatisSession, String asReturnedPaymentId)
			throws ApplicationException
	{

		LOG_OBJECT.Debug("Entered into getReturnedPaymentStatus with parameters::" + asReturnedPaymentId);
		String lsStatus = HHSConstants.EMPTY_STRING;
		try
		{
			if (null != asReturnedPaymentId && !asReturnedPaymentId.isEmpty())
			{
				lsStatus = (String) DAOUtil.masterDAO(aoMyBatisSession, asReturnedPaymentId,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSR5Constants.GET_RETURNED_PAYMENT_STATUS,
						HHSR5Constants.JAVA_LANG_STRING);
			}
			setMoState("Returned Payment Status for ReturnedPaymentID" + asReturnedPaymentId);
		}
		// handling Application exception
		catch (ApplicationException loExp)
		{
			setMoState("Error while updating Returned Payment Status :");
			loExp.addContextData("Exception occured for returned payment cancel status update: cancelReturnedPayment ",
					loExp);
			LOG_OBJECT.Error("Error while updating Returned Payment Status :", loExp);
			throw loExp;
		}
		// handling exception other than Application exception
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error while updating Returned Payment Status :", loExp);
			setMoState("Error while updating Returned Payment Status :");
			throw new ApplicationException("Exception while updating Returned Payment Status ", loExp);
		}

		return lsStatus;
	}

	/**
	 * Added in Release 6 as part of Bulk Notifications
	 * This method will fetch the date when the CSV file created. This method
	 * calls for Download CSV File Bulk Notification.
	 * @param aoMybatisSession
	 * @param asRequestId
	 * @return loTaskDetailsBean
	 * @throws ApplicationException
	 */
	public TaskDetailsBean getDateForExportNotification(SqlSession aoMybatisSession, String asRequestId)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered into getDateForExportNotification with parameters::" + asRequestId);
		TaskDetailsBean loTaskDetailsBean = null;
		try
		{
			if (null != asRequestId && !asRequestId.isEmpty())
			{
				loTaskDetailsBean = (TaskDetailsBean) DAOUtil.masterDAO(aoMybatisSession, asRequestId,
						HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS,
						HHSR5Constants.GET_CREATED_DATE_FOR_EXPORT_NOTIFICATIONS, HHSConstants.JAVA_LANG_STRING);
			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Error while updating Returned Payment Status :", loAppEx);
			setMoState("Error while updating Returned Payment Status :");
			throw loAppEx;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error while updating Returned Payment Status :", loExp);
			setMoState("Error while updating Returned Payment Status :");
			throw new ApplicationException("Exception while updating Returned Payment Status ", loExp);
		}
		return loTaskDetailsBean;
	}

	/**
	 * Added in Release 6 as part of Return payment
	 * This method will fetch the list of fiscal year
	 * for Active Budgets
	 * @param aoMybatisSession sql session
	 * @param asOrgId organization id
	 * @return loFiscalYearList list of fy years
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<String> fetchFiscalYearDetails(SqlSession aoMybatisSession, String asOrgId) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered into fetchFiscalYearDetails with parameters::" + asOrgId);
		List<String> loFiscalYearList = new ArrayList<String>();
		try
		{
			if (null != asOrgId && !asOrgId.isEmpty())
			{
				loFiscalYearList = (ArrayList<String>) DAOUtil.masterDAO(aoMybatisSession, asOrgId,
						HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS, HHSR5Constants.FETCH_FISCAL_YEAR_LIST,
						HHSConstants.JAVA_LANG_STRING);
			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Error while updating Returned Payment Status :", loAppEx);
			throw loAppEx;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error while updating Returned Payment Status :", loExp);
			setMoState("Error while updating Returned Payment Status :");
			throw new ApplicationException("Exception while updating Returned Payment Status ", loExp);
		}
		return loFiscalYearList;
	}

	/**
	 * Added in Release 6 as part of Return payment
	 * This method is added
	 * to fetch the list of provider Budget to whom bulk notifications will send
	 * on the basis of fiscal year and programId.
	 * 
	 * @param aoMybatisSession
	 * @param asFiscalYear
	 * @param asProgramName
	 * @param asBudgetId
	 * @param aoNotifyFlag
	 * @return loBudgetList
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked" })
	public List<BudgetList> fetchBudgetList(SqlSession aoMybatisSession, String asFiscalYear, String asProgramName,
			String asBudgetId, Boolean aoNotifyFlag) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered into fetchBudgetList with parameters::" + asFiscalYear + asProgramName + asBudgetId
				+ aoNotifyFlag);

		List<BudgetList> loBudgetList = new ArrayList<BudgetList>();
		Map<String, String> loRequestMap = new HashMap<String, String>();
		if (aoNotifyFlag)
		{
			try
			{
				loRequestMap.put(HHSConstants.FISCAL_YEAR, asFiscalYear);
				loRequestMap.put(HHSConstants.PROGRAM_ID, asProgramName);
				loRequestMap.put(HHSR5Constants.BUDGET_ID_WORKFLOW, asBudgetId);
				if (asBudgetId != null)
				{
					loBudgetList = (List<BudgetList>) DAOUtil
							.masterDAO(aoMybatisSession, loRequestMap, HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS,
									HHSR5Constants.FETCH_PROVIDER_NOTIFICATION_LIST_FOR_RECOUP_TASK,
									HHSConstants.JAVA_UTIL_MAP);
				}
				else
				{
					loBudgetList = (List<BudgetList>) DAOUtil.masterDAO(aoMybatisSession, loRequestMap,
							HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS, HHSR5Constants.FETCH_PROVIDER_NOTIFICATION_LIST,
							HHSConstants.JAVA_UTIL_MAP);
				}
			}
			catch (ApplicationException loAppEx)
			{
				LOG_OBJECT.Error("Error while updating Returned Payment Status :", loAppEx);
				throw loAppEx;
			}
			catch (Exception loExp)
			{
				LOG_OBJECT.Error("Error while updating Returned Payment Status :", loExp);
				setMoState("Error while updating Returned Payment Status :");
				throw new ApplicationException("Exception while updating Returned Payment Status ", loExp);
			}
		}
		return loBudgetList;
	}

	/**
	 * Added in Release 6 as part of Return payment
	 * This method fetch the data of Export File for
	 * Bulk Notification Screen.
	 * @param aoMybatisSession
	 * @param asFiscalYear - requested fiscal year
	 * @param asUserId - logged in user Id
	 * @param asProgramId
	 * @return loBulkNotificationList- details of CSV data
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<BulkNotificationList> fetchExportNotificationList(SqlSession aoMybatisSession, String asFiscalYear,
			String asUserId, String asProgramId) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered into fetchExportNotificationList with parameters::" + asFiscalYear + asUserId);
		List<BulkNotificationList> loBulkNotificationList = new ArrayList<BulkNotificationList>();
		Map<String, String> loHashMap = new HashMap<String, String>();
		try
		{
			loHashMap.put(HHSConstants.FISCAL_YEAR, asFiscalYear);
			loHashMap.put(HHSConstants.USER_ID, asUserId);
			loHashMap.put(HHSR5Constants.PROGRAM_ID, asProgramId);
			if (null != loHashMap && !loHashMap.isEmpty())
			{
				loBulkNotificationList = (List<BulkNotificationList>) DAOUtil.masterDAO(aoMybatisSession, loHashMap,
						HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS,
						HHSR5Constants.EXPORT_BULK_NOTIFICATIONS_DETAILS_QUERY, HHSConstants.JAVA_UTIL_MAP);
			}

		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Error while updating Returned Payment Status :", loAppEx);
			throw loAppEx;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error while updating Returned Payment Status :", loExp);
			setMoState("Error while updating Returned Payment Status :");
			throw new ApplicationException("Exception while updating Returned Payment Status ", loExp);
		}
		return loBulkNotificationList;
	}

	/**
	 * Added in Release 6 as part of Return payment
	 * This method is added to set the input filed in
	 * Request_bulknotification table. This method is called when Export bulk
	 * task option is selected on bulk notification screen. TX:
	 * insertPendingBulkNotificationStatus Controller: AgencySettingController
	 * @param aoMybatisSession
	 * @param aoExportRequestMap
	 * @return lbInsertStatus
	 * @throws ApplicationException
	 */
	public Boolean insertExportNotificationRequest(SqlSession aoMybatisSession,
			HashMap<String, String> aoExportRequestMap) throws ApplicationException
	{
		
		//*** Start QC 9585 R 8.9 do not expose password for service account in logs
		String param = CommonUtil.maskPassword(aoExportRequestMap);
		LOG_OBJECT.Info("Entered into insertExportNotificationRequest with parameters::" + param);
		//*** End QC 9585 R 8.9 do not expose password for service account in logs

		Integer liInserCount = null;
		Boolean lbInsertStatus = Boolean.FALSE;
		try
		{
			Map<String, String> loHashMap = new HashMap<String, String>();
			loHashMap.put(HHSConstants.FISCAL_YEAR, aoExportRequestMap.get(HHSConstants.FISCAL_YEAR));
			loHashMap.put(HHSConstants.USER_ID, aoExportRequestMap.get(HHSConstants.USER_ID));
			loHashMap.put(HHSConstants.STATUS_COLUMN, aoExportRequestMap.get(HHSConstants.STATUS_COLUMN));
			loHashMap.put(HHSConstants.PROGRAM_ID, aoExportRequestMap.get(HHSConstants.PROGRAM_ID));
			if (null != loHashMap && !loHashMap.isEmpty())
			{
				liInserCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loHashMap,
						HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS,
						HHSR5Constants.INSERT_BULK_NOTIFICATION_REQUEST_EXPORT, HHSConstants.JAVA_UTIL_MAP);
				if (null != liInserCount && liInserCount > 0)
				{
					lbInsertStatus = HHSConstants.BOOLEAN_TRUE;
					LOG_OBJECT.Info("Exiting from insertExportNotificationRequest with status::", lbInsertStatus);
				}
				else
				{
					lbInsertStatus = HHSConstants.BOOLEAN_FALSE;
					LOG_OBJECT.Info("Exiting from insertExportNotificationRequest with status::", lbInsertStatus);
				}
			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Error while updating Returned Payment Status :", loAppEx);
			throw loAppEx;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error while updating Returned Payment Status :", loExp);
			setMoState("Error while updating Returned Payment Status :");
			throw new ApplicationException("Exception while updating Returned Payment Status ", loExp);
		}
		return lbInsertStatus;
	}

	/**
	 * Added in Release 6 as part of Return payment
	 * This method will update the status of Export file in
	 * Export_notification_mapping and also contains the time-stamp when the file
	 * is down-loaded successfully.
	 * @param aoMybatisSession
	 * @param asNotificationId
	 * @param aoNotificationStatus
	 * @return lsUpdatedStatus
	 * @throws ApplicationException
	 */
	public String updateExportNotificationStatus(SqlSession aoMybatisSession, String asNotificationId,
			Map<String, String> aoNotificationStatus) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered into updateExportNotificationStatus with parameters::" + asNotificationId
				+ aoNotificationStatus);

		String lsUpdatedStatus = HHSConstants.STRING_FALSE;

		try
		{
			Map<String, Object> loDetailsMap = new HashMap<String, Object>();

			String lsNotificationStatus = aoNotificationStatus.get(HHSR5Constants.DOWNLOAD_STATUS);
			loDetailsMap.put(ApplicationConstants.ALERT_VIEW_NOTIFICATION_ID_PARAMETER, asNotificationId);
			loDetailsMap.put(HHSR5Constants.NOTIFICATION_STATUS, lsNotificationStatus);

			DAOUtil.masterDAO(aoMybatisSession, loDetailsMap, HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS,
					HHSR5Constants.UPDATE_EXPORT_NOTIFICATION_STATUS, HHSConstants.JAVA_UTIL_MAP);
			lsUpdatedStatus = HHSR5Constants.TRUE;
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Error while updating Returned Payment Status :", loAppEx);
			throw loAppEx;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error while updating Returned Payment Status :", loExp);
			setMoState("Error while updating Returned Payment Status :");
			throw new ApplicationException("Exception while updating Returned Payment Status ", loExp);
		}
		return lsUpdatedStatus;
	}

	/**
	 * Added in Release 6 as part of Return payment
	 * This method will update the status of Export file in
	 * Export_notification_mapping when Bulk notification Batch(R6) is Started.
	 * 
	 * @param aoMybatisSession SQL session
	 * @param asNotificationId notification id
	 * @param asNotificationStatus Notification status
	 * @return lsUpdatedStatus updte Status
	 * @throws ApplicationException
	 */
	public String updateExportNotificationStatus(SqlSession aoMybatisSession, String asNotificationId,
			String asNotificationStatus) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered into updateExportNotificationStatus with parameters::" + asNotificationId
				+ asNotificationStatus);
		String lsUpdatedStatus = HHSConstants.STRING_FALSE;

		try
		{
			Map<String, Object> loDetailsMap = new HashMap<String, Object>();

			loDetailsMap.put(ApplicationConstants.ALERT_VIEW_NOTIFICATION_ID_PARAMETER, asNotificationId);
			loDetailsMap.put(HHSR5Constants.NOTIFICATION_STATUS, asNotificationStatus);
			DAOUtil.masterDAO(aoMybatisSession, loDetailsMap, HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS,
					HHSR5Constants.UPDATE_EXPORT_NOTIFICATION_STATUS, HHSConstants.JAVA_UTIL_MAP);
			lsUpdatedStatus = HHSR5Constants.TRUE;
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Error while updating Returned Payment Status :", loAppEx);
			throw loAppEx;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error while updating Returned Payment Status :", loExp);
			setMoState("Error while updating Returned Payment Status :");
			throw new ApplicationException("Exception while updating Returned Payment Status ", loExp);
		}
		return lsUpdatedStatus;
	}

	/**
	 * Added in Release 6 as part of Return payment
	 * This method will fetch the list of program names for Bulk Notification
	 * Screen.
	 * @param aoMybatisSession
	 * @param asOrgType
	 * @param asOrgId
	 * @return loProgramNameList List of program names
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<ProgramNameInfo> getProgramName(SqlSession aoMybatisSession, String asOrgType, String asOrgId)
			throws ApplicationException
	{
		List<ProgramNameInfo> loProgramNameList = null;
		String lsActiveFlag = HHSConstants.ONE;
		HashMap<String, String> loProgramMap = new HashMap<String, String>();
		loProgramMap.put(HHSConstants.ACTIVE_FLAG, lsActiveFlag);
		loProgramMap.put(HHSConstants.ORG_ID, asOrgId);
		try
		{
			loProgramNameList = (List<ProgramNameInfo>) DAOUtil.masterDAO(aoMybatisSession, loProgramMap,
					HHSConstants.MAPPER_CLASS_AGENCY_SETTINGS, HHSR5Constants.FETCH_PROGRAM_NAME_FOR_NOTIFICATIONS,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			setMoState(HHSConstants.PL_PROGRAM_NAME_INFORMATION + asOrgType);
		}
		catch (ApplicationException loExp)
		{
			loExp.addContextData(HHSConstants.PL_ORGANIZATION_ID, asOrgId);
			LOG_OBJECT.Error(HHSConstants.PL_FETCHING_PROGRAM_NAME_LIST, loExp);
			setMoState(HHSConstants.PL_PROGRAM_NAME + asOrgId);
			throw loExp;
		}
		return loProgramNameList;
	}
}
