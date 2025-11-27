package com.nyc.hhs.batch.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.xml.DOMConfigurator;

import com.nyc.hhs.batch.IBatchQueue;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.AgencyTaskBean;
import com.nyc.hhs.model.CityUserDetailsBeanForBatch;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8services.P8HelperServices;
import com.nyc.hhs.service.filenetmanager.p8services.P8ProcessServiceForSolicitationFinancials;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This class is being used for synchronizing LDAP and database records for
 * internal users
 */

public class DataConversionAmendmentStatusBatch extends P8HelperServices implements IBatchQueue
{

	private static final LogInfo LOG_OBJECT = new LogInfo(DataConversionAmendmentStatusBatch.class);

	/**
	 * Blank implementation of getQueue method of interface
	 * 
	 * @param aoMParameters HashMap of Parameter
	 * @return null
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public List<CityUserDetailsBeanForBatch> getQueue(Map aoMParameters)
	{
		return null;
	}

	/**
	 * Implementation of the executeQueue method . This method will generate All
	 * the PDF's and Save into Filenet.
	 * @param aoLQueue List of Queue
	 * @throws ApplicationException ApplicationException Object
	 */
	@Override
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public void executeQueue(List aoLQueue) throws ApplicationException
	{
		String lsLog4jPath = "C:\\hhsLogs\\log4j.xml";

		DOMConfigurator.configure(lsLog4jPath);
		Channel loChannel = null;
		LOG_OBJECT.Info("Executing DataConversionAmendmentStatusBatch Batch .. ");
		SqlSession loFilenetPEDBSession = null;
		try
		{
			P8UserSession loFilenetSession = filenetConnection.setP8SessionVariables();
			loFilenetPEDBSession = HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory().openSession();
			loFilenetSession.setFilenetPEDBSession(loFilenetPEDBSession);
			setAmendmentStatus(loFilenetSession, HHSConstants.TASK_AMENDMENT_CONFIGURATION, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_CONTRACT_PENDING_CONFIGURATION));
			setAmendmentStatus(loFilenetSession, HHSConstants.TASK_AMENDMENT_COF, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_CONTRACT_PENDING_COF));

		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in amendment.executeQueue()", aoAppEx);
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error while executing amendment.executeQueue() ..", aoEx);
		}
	}

	/**
	 * This method fetch all the task by taking input as task type and update
	 * its contract status in DB.
	 * 
	 * @param loFilenetSession Filenet session
	 * @param asTaskType task type
	 * @param asStatusId status id
	 * @throws ApplicationException
	 */
	private void setAmendmentStatus(P8UserSession loFilenetSession, String asTaskType, String asStatusId)
			throws ApplicationException
	{
		Channel loChannel;
		loChannel = new Channel();
		String lsWobNum = null;
		String lsContractId = null;
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();

		HashMap<String, String> loFilterDetails = new HashMap<String, String>();
		AgencyTaskBean loAgencyTaskBean = new AgencyTaskBean();
		loFilterDetails.put(HHSConstants.PROPERTY_PE_TASK_TYPE, asTaskType);
		loAgencyTaskBean.setFilterProp(loFilterDetails);
		P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();

		List<AgencyTaskBean> loTaskList = loP8ProcessServiceForSolicitationFinancials.fetchAgencyTask(loFilenetSession,
				loAgencyTaskBean);
		for (AgencyTaskBean loTaskBean : loTaskList)
		{
			lsWobNum = loTaskBean.getWobNumber();
			lsContractId = loTaskBean.getEntityId();

			try
			{
				loTaskDetailsBean.setContractId(lsContractId);
				loChannel.setData(HHSConstants.AS_STATUS_ID, asStatusId);
				loChannel.setData(HHSConstants.LB_FLAG, true);
				loChannel.setData(HHSConstants.AO_TASK_DETAILS_BEAN, loTaskDetailsBean);
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.SET_CONTRACT_STATUS);
				LOG_OBJECT.Info("For Amendment Id-" + lsContractId + " status Id -" + asStatusId
						+ " updated successfully !!!!");
			}
			catch (ApplicationException aoAppEx)
			{
				LOG_OBJECT.Error("Exception in setAmendmentStatus()", aoAppEx);
			}
			catch (Exception aoEx)
			{
				LOG_OBJECT.Error("Error while executing setAmendmentStatus() ..", aoEx);
			}

		}
	}

}
