package com.nyc.hhs.daomanager.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.EvaluationCriteriaBean;
import com.nyc.hhs.model.ExtendedDocument;
import com.nyc.hhs.model.Procurement;
import com.nyc.hhs.model.ProposalQuestionAnswerBean;
import com.nyc.hhs.model.RFPReleaseBean;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This service class will get the method calls from controller through
 * transaction layer. Execute queries by calling mapper and return query output
 * back to controller. If any error exists, wrap the exception into Application
 * Exception and throw it to controller.
 */
public class RFPReleaseService extends ServiceState
{
	/**
	 * This is a log object used to log any exception into log file.
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(ProcurementService.class);

	/**
	 * This method validates whether procurement has been linked with the EPIN
	 * <ul>
	 * <li>1. Retrieve procurement Id from the channel</li>
	 * <li>2. Execute query <b>fetchEpinValue</b> to fetch Epin corresponding to
	 * the procurement from the Procurement table</li>
	 * <li>3. If the fetched Epin value is "Pending" then put EpinStatusFlag as
	 * false in the HashMap else set it true</li>
	 * <li>4. Return HashMap.</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession - mybatis SQL session
	 * @param asProcId - procurement Id
	 * @param aoServiceData a map of services with evidence flag unchecked and
	 *            associated with procurements
	 * @return loValidateStatus boolean value indicating if epin exist
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public Boolean validateEPIN(SqlSession aoMyBatisSession, String asProcId, Map<String, Object> aoServiceData)
			throws ApplicationException
	{
		Boolean loValidateStatus = false;
		try
		{
			List<String> loServiceNameList = (List<String>) aoServiceData.get(HHSConstants.SER_NAME_LIST);
			List<String> loEvidenceIdList = (List<String>) aoServiceData.get(HHSConstants.ELEMENT_ID_LIST);
			if ((null == loServiceNameList || loServiceNameList.size() == HHSConstants.INT_ZERO)
					&& (null != loEvidenceIdList && loEvidenceIdList.size() > HHSConstants.INT_ZERO))
			{
				String lsValidationStatus = (String) DAOUtil.masterDAO(aoMyBatisSession, asProcId,
						HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER, HHSConstants.FETCH_EPIN_VALUE,
						HHSConstants.JAVA_LANG_STRING);
				if (null != lsValidationStatus && !lsValidationStatus.equals(HHSConstants.EMPTY_STRING))
				{
					loValidateStatus = true;
				}
				setMoState("EPIN fetched successfully for Procurement Id:" + asProcId);
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData(HHSConstants.PROCUREMENT_ID, asProcId);
			LOG_OBJECT.Error("Error occurred while fetching EPIN for Procurement Id", loExp);
			setMoState("Error occurred while fetching EPIN for Procurement Id:" + asProcId);
			throw loExp;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error occurred while fetching EPIN for Procurement Id", loExp);
			setMoState("Error occurred while fetching EPIN for Procurement Id:" + asProcId);
			throw new ApplicationException("Error occurred while fetching EPIN for Procurement Id", loExp);
		}
		return loValidateStatus;
	}

	/**
	 * This method validates the rfp requisites before releasing the rfp
	 * <ul>
	 * <li>1. Fetch procurement Id and EvidenceStatusFlag</li>
	 * <li>2. Execute query <b>fetchProcCertOfFunds</b> to fetch Procurement Cof
	 * status from database corresponding to the procurement Id</li>
	 * <li>3. If procurement cof stataus id "Approved" then proceed next else
	 * put RFPPreRequisitesFlag as false in the HashMap</li>
	 * <li>4. Execute query <b>fetchRfpPreRequisites</b> to fetch required
	 * document_type from rfp_document table corresponding to the procurement Id
	 * </li>
	 * <li>5. If required document_type is not-null then proceed next else put
	 * RFPPreRequisitesFlag as false in the HashMap</li>
	 * <li>6. Execute query <b>fetchRfpPreRequisites</b> to fetch
	 * rfp_document_id from rfp_document table corresponding to the procurement
	 * Id</li>
	 * <li>7. If rfp_document_id is not-null then proceed next else put
	 * RFPPreRequisitesFlag as false in the HashMap</li>
	 * <li>8. Execute query <b>fetchRfpPreRequisites</b> to fetch
	 * evaluation_criteria_id from evaluation_criteria table corresponding to
	 * the procurement Id</li>
	 * <li>9. If evaluation_criteria_id is not-null then put
	 * RFPPreRequisitesFlag as true else put RFPPreRequisitesFlag as false in
	 * the HashMap</li>
	 * <li>10. Return HashMap</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession - mybatis SQL session
	 * @param asProcId - procurement Id
	 * @param aoServiceData a map of services with evidence flag unchecked and
	 *            associated with procurements
	 * @return loRFPPreRequisites rfp bean object containing count of rfp
	 *         document, required document type and evaluation criteria for
	 *         given procurement Id
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings("unchecked")
	public RFPReleaseBean validateRfpPreRequisites(SqlSession aoMyBatisSession, String asProcId,
			Map<String, Object> aoServiceData) throws ApplicationException
	{

		RFPReleaseBean loRFPPreRequisites = null;
		try
		{
			List<String> loServiceNameList = (List<String>) aoServiceData.get(HHSConstants.SER_NAME_LIST);
			List<String> loEvidenceIdList = (List<String>) aoServiceData.get(HHSConstants.ELEMENT_ID_LIST);
			if ((null == loServiceNameList || loServiceNameList.size() == HHSConstants.INT_ZERO)
					&& (null != loEvidenceIdList && loEvidenceIdList.size() > HHSConstants.INT_ZERO))
			{
				loRFPPreRequisites = (RFPReleaseBean) DAOUtil.masterDAO(aoMyBatisSession, asProcId,
						HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER, HHSConstants.FETCH_RFP_PREREQ,
						HHSConstants.JAVA_LANG_STRING);
				setMoState("Last Modified details inserted successfully");
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData(HHSConstants.PROCUREMENT_ID, asProcId);
			LOG_OBJECT.Error("Error occurred while fetching rfp pre requisites", loExp);
			setMoState("Error occurred while fetching rfp pre requisites for ProcurementId:" + asProcId);
			throw loExp;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error occurred while fetching rfp pre requisites", loExp);
			setMoState("Error occurred while fetching rfp pre requisites for ProcurementId:" + asProcId);
			throw new ApplicationException("Error occurred while fetching rfp pre requisites", loExp);
		}
		return loRFPPreRequisites;
	}

	// changes start for R5
	/**
	 * This method validates whether procurement certification of funds status
	 * is approved or not
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. Get flag whether procurement is Open Ended RFP from
	 * checkIfOpenEndedZeroValue method of Competition Pool Service</li>
	 * <li>2. If open ended return true</li>
	 * <li>3. Else fetch the required information for the Cof validation</li>
	 * <li>4. If the retrieved authenticated user boolean flag is true then:</li>
	 * <li>5. Execute query <b>fetchProcCertOfFunds</b> to fetch procurement Cof
	 * status from the database</li>
	 * <li>6. If the procurement Cof status is "Approved"(loCertOfFundsStatus>0)
	 * then set loApproveFlag to true else false</li>
	 * <li>7. Return loApproveFlag</li>
	 * </ul>
	 * 
	 * Change: added check for open ended RFP and zero value procurement Changed
	 * By: Varun Change Date 10 March 2014
	 * 
	 * @param aoMyBatisSession - mybatis SQL session
	 * @param asProcId - procurement Id
	 * @param aoServiceData a map of services with evidence flag unchecked and
	 *            associated with procurements
	 * @return loApproveFlag indicating COF associated with Procurement -
	 *         boolean
	 * @throws ApplicationException If an Application Exception occurs
	 */

	@SuppressWarnings("unchecked")
	public Boolean checkCofPSRApproval(SqlSession aoMyBatisSession, String asProcId, Map<String, Object> aoServiceData,
			Map<String, Boolean> aoApproveMap) throws ApplicationException
	{
		try
		{
			aoApproveMap.put(HHSR5Constants.APPROVE_PCOF_FLAG, false);
			aoApproveMap.put(HHSR5Constants.APPROVE_PCOF_AMOUNT_FLAG, true);
			aoApproveMap.put(HHSR5Constants.APPROVE_PCOF_DATE_FLAG, true);
			aoApproveMap.put(HHSR5Constants.APPROVE_PSR_FLAG, false);
			aoApproveMap.put(HHSR5Constants.APPROVE_PSR_AMOUNT_FLAG, true);
			aoApproveMap.put(HHSR5Constants.APPROVE_PSR_DATE_FLAG, true);
			List<String> loServiceNameList = (List<String>) aoServiceData.get(HHSConstants.SER_NAME_LIST);
			List<String> loEvidenceIdList = (List<String>) aoServiceData.get(HHSConstants.ELEMENT_ID_LIST);
			CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
			Boolean loIsOpenEndedZeroValue = loCompetitionPoolService.checkIfOpenEndedZeroValue(aoMyBatisSession,
					asProcId);
			if ((null == loServiceNameList || loServiceNameList.size() == HHSConstants.INT_ZERO)
					&& (null != loEvidenceIdList && loEvidenceIdList.size() > HHSConstants.INT_ZERO))
			{
				Boolean loPCOFApprovedFlag = true;
				if (!loIsOpenEndedZeroValue)
				{
					Integer loCertOfFundsStatus = (Integer) DAOUtil.masterDAO(aoMyBatisSession, asProcId,
							HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER, HHSConstants.FETCH_PROC_CERT_OF_FUNDS,
							HHSConstants.JAVA_LANG_STRING);
					if (loCertOfFundsStatus == HHSConstants.INT_ZERO)
					{
						loPCOFApprovedFlag = false;
					}
				}
				aoApproveMap.put(HHSR5Constants.APPROVE_PCOF_FLAG, loPCOFApprovedFlag);
				// if (loPCOFApprovedFlag)
				// {
				Integer loPSRCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, asProcId,
						HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSR5Constants.FETCH_PSR_APPROVED_COUNT,
						HHSConstants.JAVA_LANG_STRING);
				setMoState("Fetched PSR count successfully");
				if (loPSRCount > HHSConstants.INT_ZERO)
				{
					aoApproveMap.put(HHSR5Constants.APPROVE_PSR_FLAG, true);
				}
				// }
				if (aoApproveMap.get(HHSR5Constants.APPROVE_PSR_FLAG)
						|| aoApproveMap.get(HHSR5Constants.APPROVE_PCOF_FLAG))
				{
					Procurement loProcurementDetails = (Procurement) DAOUtil.masterDAO(aoMyBatisSession, asProcId,
							HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
							HHSR5Constants.VALIDATE_PROCUREMENT_AMOUNT_PCOF, HHSConstants.JAVA_LANG_STRING);
					if (aoApproveMap.get(HHSR5Constants.APPROVE_PSR_FLAG)
							&& null != loProcurementDetails.getPsrApprovedAmount()
							&& loProcurementDetails.getPsrApprovedAmount().compareTo(
									loProcurementDetails.getEstProcurementValue()) != 0)
					{
						aoApproveMap.put(HHSR5Constants.APPROVE_PSR_AMOUNT_FLAG, false);
					}
					if (aoApproveMap.get(HHSR5Constants.APPROVE_PCOF_FLAG)
							&& loProcurementDetails.getApprovedAmount().compareTo(
									loProcurementDetails.getEstProcurementValue()) != 0)
					{
						aoApproveMap.put(HHSR5Constants.APPROVE_PCOF_AMOUNT_FLAG, false);
					}
					if (!loIsOpenEndedZeroValue)
					{
						if (aoApproveMap.get(HHSR5Constants.APPROVE_PCOF_FLAG)
								&& !(loProcurementDetails.getContractStartDateUpdated().equalsIgnoreCase(
										loProcurementDetails.getPsrConctractStartDate()) && loProcurementDetails
										.getContractEndDateUpdated().equalsIgnoreCase(
												loProcurementDetails.getPsrConctractEndDate())))
						{
							aoApproveMap.put(HHSR5Constants.APPROVE_PSR_DATE_FLAG, false);
						}
						if (aoApproveMap.get(HHSR5Constants.APPROVE_PCOF_FLAG)
								&& !(loProcurementDetails.getContractStartDateUpdated().equalsIgnoreCase(
										loProcurementDetails.getPcofConctractStartDate()) && loProcurementDetails
										.getContractEndDateUpdated().equalsIgnoreCase(
												loProcurementDetails.getPcofConctractEndDate())))
						{
							aoApproveMap.put(HHSR5Constants.APPROVE_PCOF_DATE_FLAG, false);
						}
					}
				}
				setMoState("Fetched COF details successfully");
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData(HHSConstants.PROCUREMENT_ID, asProcId);
			LOG_OBJECT.Error("Error occurred while fetching COF Approval Flag", loExp);
			setMoState("Error occurred while fetching COF Approval Flag:" + asProcId);
			throw loExp;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error occurred while fetching COF Approval Flag", loExp);
			setMoState("Error occurred while fetching COF Approval Flag:" + asProcId);
			throw new ApplicationException("Error occurred while fetching COF Approval Flag", loExp);
		}
		return aoApproveMap.get(HHSR5Constants.APPROVE_PSR_FLAG)
				&& aoApproveMap.get(HHSR5Constants.APPROVE_PSR_AMOUNT_FLAG)
				&& aoApproveMap.get(HHSR5Constants.APPROVE_PCOF_FLAG)
				&& aoApproveMap.get(HHSR5Constants.APPROVE_PSR_DATE_FLAG);
	}

	// changes for R5
	/**
	 * This method inserts the approved and conditionally approved providers
	 * list in procurement_provider table
	 * <ul>
	 * <li>1. Fetch all approved and conditionally approved providers
	 * corresponding to the selected services</li>
	 * <li>2. Execute query <b>saveApprovedProviders</b> to insert the fetched
	 * list in the database in procurement_provider table</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProcurementId - a string value of procurement Id
	 * @param asUserId - a string value of user Id
	 * @param aoAuthStatusFlag - a boolean value indicating previous service
	 *            executed successfully
	 * @return loInsertStatus - boolean
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean insertAppProviderList(SqlSession aoMybatisSession, String asProcurementId, String asUserId,
			Boolean aoAuthStatusFlag) throws ApplicationException
	{
		Boolean loInsertStatus = false;
		try
		{
			if (aoAuthStatusFlag)
			{
				HashMap<String, String> loReqMap = new HashMap<String, String>();
				loReqMap.put(HHSConstants.PROCUREMENT_ID, asProcurementId);
				loReqMap.put(HHSConstants.USER_ID, asUserId);
				loReqMap.put(HHSConstants.PROPOSAL_STATUS_ID_KEY, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROVIDER_ELIGIBLE_TO_PROPOSE));
				loReqMap.put(HHSConstants.ACTIVE_FLAG, HHSConstants.ONE);
				String lsProcStatus = (String) DAOUtil.masterDAO(aoMybatisSession, asProcurementId,
						HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.GET_PROC_STATUS,
						HHSConstants.JAVA_LANG_STRING);
				if (lsProcStatus.equalsIgnoreCase(HHSConstants.DRAFT)
						|| lsProcStatus.equalsIgnoreCase(HHSConstants.PROCUREMENT_STATUS_PLANNED)
						|| lsProcStatus.equalsIgnoreCase(HHSConstants.PROCUREMENT_STATUS_RELEASED))
				{
					if (lsProcStatus.equalsIgnoreCase(HHSConstants.DRAFT)
							|| lsProcStatus.equalsIgnoreCase(HHSConstants.PROCUREMENT_STATUS_PLANNED))
					{
						loReqMap.put(HHSConstants.ACTIVE_FLAG, HHSConstants.ZERO);
						DAOUtil.masterDAO(aoMybatisSession, asProcurementId,
								HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
								HHSConstants.DELETE_PROCUREMENT_PROVIDER_DATA, HHSConstants.JAVA_LANG_STRING);
					}
					DAOUtil.masterDAO(aoMybatisSession, loReqMap, HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER,
							HHSConstants.SAVE_APPROVED_PROVIDERS, HHSConstants.JAVA_UTIL_HASH_MAP);
					if (lsProcStatus.equalsIgnoreCase(HHSConstants.PROCUREMENT_STATUS_RELEASED))
					{
						DAOUtil.masterDAO(aoMybatisSession, loReqMap, HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER,
								HHSConstants.SAVE_APPROVED_PROVIDERS_SERVICES, HHSConstants.JAVA_UTIL_HASH_MAP);
					}
				}
				loInsertStatus = true;
				setMoState("Approved Provider List saved successfully for Procurement Id:" + asProcurementId);
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("Error occurred while inserting Approved providers list", loExp);
			setMoState("Error occurred while inserting Approved providers list for Procurement Id:" + asProcurementId);
			throw loExp;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error occurred while inserting Approved providers list", loExp);
			setMoState("Error occurred while inserting Approved providers list for Procurement Id:" + asProcurementId);
			throw new ApplicationException("Error occurred while inserting Approved providers list", loExp);
		}
		return loInsertStatus;
	}

	/**
	 * Modified as a part of release 3.1.0 for enhancement request 6024
	 * 
	 * This method retrieves last updated details by city user on evaluation
	 * criteria screen for particular procurement ID. If no information is
	 * updated by city then no data will be populated on the screen.
	 * <ul>
	 * <li>1. Retrieves the required information i.e. procurement id, user name</li>
	 * <li>2. Execute query <b>getProcurementPrevStatus</b></li>
	 * <li>3. for some other condition execute query
	 * <b>fetchEvaluationCriteria</b></li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession - mybatis SQL session
	 * @param asProcurementId - string representation of procurement id
	 * @return - loRFPReleaseBean - Bean
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public RFPReleaseBean fetchEvaluationCriteria(SqlSession aoMyBatisSession, String asProcurementId,
			String asProcurementStatus) throws ApplicationException
	{
		RFPReleaseBean loRFPReleaseBean = null;
		try
		{
			if (PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_PROCUREMENT_CANCELLED).equalsIgnoreCase(asProcurementStatus))
			{
				asProcurementStatus = (String) DAOUtil.masterDAO(aoMyBatisSession, asProcurementId,
						HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER, HHSConstants.GET_PROC_PREV_STATUS,
						HHSConstants.JAVA_LANG_STRING);
			}
			if (PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_PROCUREMENT_RELEASED).equalsIgnoreCase(asProcurementStatus))
			{
				loRFPReleaseBean = (RFPReleaseBean) DAOUtil.masterDAO(aoMyBatisSession, asProcurementId,
						HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER, HHSConstants.FETCH_ADD_EVAL_CRITERIA,
						HHSConstants.JAVA_LANG_STRING);
			}
			if (loRFPReleaseBean == null)
			{
				loRFPReleaseBean = (RFPReleaseBean) DAOUtil.masterDAO(aoMyBatisSession, asProcurementId,
						HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER, HHSConstants.FETCH_EVAL_CRIERIA,
						HHSConstants.JAVA_LANG_STRING);
			}
		}
		// handling exception while processing evaluation criteria details.
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.DETAIL_BEAN, CommonUtil.convertBeanToString(asProcurementId));
			LOG_OBJECT.Error("Exception occured while getting evaluation data from database ", loAppEx);
			setMoState("Transaction Failed:: RFPReleaseService: fetchEvaluationCriteria method - failed to get evaluation data"
					+ asProcurementId);
			throw loAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Exception occured while getting evaluation data from database ", loEx);
			setMoState("Transaction Failed:: RFPReleaseService: fetchEvaluationCriteria method - failed to get evaluation data"
					+ asProcurementId);
			throw new ApplicationException("Exception occured while getting evaluation data from database ", loEx);
		}
		return loRFPReleaseBean;
	}

	/**
	 * This method updates data from procurement_addendum to procurement table
	 * corresponding to the procurement Id
	 * <ul>
	 * <li>1. Retrieve "lbAddendumDoc" and procurement Id</li>
	 * <li>2. If the retrieved "lbAddendumDoc" message from HashMap is true then
	 * update data from procurement_addendum to procurement table by executing
	 * query <b>updateProcurementData</b> corresponding to the procurement Id</li>
	 * <li>3. If the number of rows inserted is greater then 0 then delete the
	 * data from procurement_addendum_document by executing query
	 * <b>deleteProcAddendunData</b> corresponding to that procurement Id</li>
	 * <li>4. If number of rows deleted is greater than 0 then put
	 * "lbUpdateSuccessful" as true else put it as false</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProcId - string representation of procurement Id
	 * @param asUserId asUserId
	 * @param aoAddendumDoc - boolean status flag
	 * @return loUpdateSuccessful - boolean status flag
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean updateProcurementData(SqlSession aoMybatisSession, String asProcId, String asUserId,
			Boolean aoAddendumDoc) throws ApplicationException
	{
		Boolean loUpdateSuccessful = false;
		HashMap<String, String> loMap = new HashMap<String, String>();
		loMap.put(HHSConstants.PROCUREMENT_ID, asProcId);
		loMap.put(HHSConstants.USER_ID, asUserId);
		if (aoAddendumDoc)
		{
			try
			{
				DAOUtil.masterDAO(aoMybatisSession, loMap, HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
						HHSConstants.UPDATE_PROC_DATA, HHSConstants.JAVA_UTIL_MAP);
				loUpdateSuccessful = true;
				setMoState("Addendum details copied from procurement_addendum to procurement table corresponding to the procurement Id");
			}
			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handles over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */
			catch (ApplicationException loAppEx)
			{
				LOG_OBJECT
						.Error("Exception occured while coping details from procurement_addendum to procurement table corresponding to the procurement Id ",
								loAppEx);
				setMoState("Transaction Failed:: RFPReleaseService:updateProcurementData method - while coping details from procurement_addendum to procurement table corresponding to the procurement Id ");
				throw loAppEx;
			}
			catch (Exception loExp)
			{
				LOG_OBJECT
						.Error("Exception occured while coping details from procurement_addendum to procurement table corresponding to the procurement Id ",
								loExp);
				setMoState("Transaction Failed:: RFPReleaseService:updateProcurementData method - while coping details from procurement_addendum to procurement table corresponding to the procurement Id ");
				throw new ApplicationException(
						"Exception occured while coping details from procurement_addendum to procurement table corresponding to the procurement Id",
						loExp);
			}
		}
		return loUpdateSuccessful;
	}

	/**
	 * Changed method - By: Siddharth Bhola Reason: Build: 2.6.0 Enhancement id:
	 * 5667, When addendum is released then all 0 entries in DOC_DELETE_FLAG,
	 * corresponding to Procurement are hard deleted
	 * 
	 * This method copies the data from rfp_addendum_document to rfp_document
	 * table corresponding to the procurement Id
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. Retrieve authentication flag and procurement_id and create one
	 * HashMap<String, Boolean></li>
	 * <li>2. If retrieved authentication flag is true then put "authFlag" as
	 * true in the HashMap and insert the data from rfp_addendum_document to
	 * rfp_document table corresponding to the procurement_id via
	 * <b>insertRfpDocumentData</b></li>
	 * <li>3. If the number of rows updated is greater then 0 then delete the
	 * rows from rfp_addendum_document corresponding to that procurement Id by
	 * executing query <b>deleteRfpDocAddendunData</b> and set "lbAddendumDoc"
	 * as true else set it as false</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProcId - string representation of procurement Id
	 * @return loAddendumDoc - Addendum Document boolean status flag
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public Boolean updateRfpDocument(SqlSession aoMybatisSession, String asProcId, Boolean aoEvalProgress)
			throws ApplicationException
	{
		Boolean loAddendumDoc = false;
		HashMap<String, String> loMap = new HashMap<String, String>();
		String lsDocSubmittedStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.DOCUMENT_SUBMITTED);
		loMap.put(HHSConstants.PROCUREMENT_ID, asProcId);
		loMap.put(HHSConstants.DOC_SUBMITTED_STATUS, lsDocSubmittedStatus);
		if (!aoEvalProgress)
		{
			try
			{
				List<ExtendedDocument> loRfpDocumentTypeList = (List<ExtendedDocument>) DAOUtil.masterDAO(
						aoMybatisSession, asProcId, HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER,
						HHSConstants.CHECK_RFP_DOC_TYPE, HHSConstants.JAVA_LANG_STRING);

				if (loRfpDocumentTypeList != null)
				{
					for (ExtendedDocument loDocType : loRfpDocumentTypeList)
					{
						String lsDocType = loDocType.getDocumentType();
						String lsDocStatusId = loDocType.getStatusId();
						if (lsDocType.equalsIgnoreCase(HHSConstants.ADDENDA)
								&& lsDocStatusId.equalsIgnoreCase(PropertyLoader.getProperty(
										HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.DOCUMENT_DRAFT)))
						{
							DAOUtil.masterDAO(aoMybatisSession, loMap, HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER,
									HHSConstants.INS_RFP_DOC_DATA, HHSConstants.JAVA_UTIL_HASH_MAP);
							loAddendumDoc = true;
							setMoState("Addendum details copied from rfp_addendum_document to rfp_document table corresponding to the procurement Id");
							LOG_OBJECT
									.Debug("Addendum details copied from rfp_addendum_document to rfp_document table corresponding to the procurement Id");
							break;
						}
					}
				}
				// build 2.6.0, defect id 5667.
				if (loAddendumDoc)
				{
					DAOUtil.masterDAO(aoMybatisSession, asProcId, HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER,
							HHSConstants.HARD_DELETE_RFP_DOC_DETAILS, HHSConstants.JAVA_LANG_STRING);
				}
			}
			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handles over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */
			catch (ApplicationException loAppEx)
			{
				LOG_OBJECT
						.Error("Exception occured while coping details from rfp_addendum_document to rfp_document table corresponding to the procurement Id ",
								loAppEx);
				setMoState("Transaction Failed:: RFPReleaseService:updateRfpDocument method - while coping details from rfp_addendum_document to rfp_document table corresponding to the procurement Id "
						+ asProcId);
				throw loAppEx;
			}
			catch (Exception loExp)
			{
				LOG_OBJECT
						.Error("Exception occured while coping details from rfp_addendum_document to rfp_document table corresponding to the procurement Id ",
								loExp);
				setMoState("Transaction Failed:: RFPReleaseService:updateRfpDocument method - while coping details from rfp_addendum_document to rfp_document table corresponding to the procurement Id "
						+ asProcId);
				throw new ApplicationException(
						"Exception occured while coping details from rfp_addendum_document to rfp_document table corresponding to the procurement Id",
						loExp);
			}
		}
		return loAddendumDoc;
	}

	/**
	 * Modified as a part of release 3.1.0 for enhancement request 6024
	 * 
	 * This method checks for evaluations in progress corresponding to the
	 * procurement Id
	 * <ul>
	 * <li>1. Retrieve procurement_id and create one HashMap<String, String></li>
	 * <li>2. Executes query <b>checkEvaluationInProgress</b> and set
	 * "loEvalProgress" as true else set it to true if gets the result</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProcId - string representation of procurement Id
	 * @return loEvalProgress - evaluation in progress boolean status flag
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public Boolean checkEvaluationInProgress(SqlSession aoMybatisSession, String asProcId) throws ApplicationException
	{
		Boolean loEvalProgress = Boolean.FALSE;
		Map<String, String> loMap = new HashMap<String, String>();
		String lsprocStatusId = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.EVALUATE_PROPOSAL_TASK_SCORES_COMPLETED);
		loMap.put(HHSConstants.PROCUREMENT_ID, asProcId);
		loMap.put(HHSConstants.PROC_STA_ID, lsprocStatusId);
		try
		{
			Integer loEvalProgressCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loMap,
					HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER, HHSConstants.CHECK_EVAL_IN_PROGRESS,
					HHSConstants.JAVA_UTIL_MAP);

			if (null != loEvalProgressCount && loEvalProgressCount > HHSConstants.INT_ZERO)
			{
				loEvalProgress = Boolean.TRUE;
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error(
					"Exception occured while checking evaluation in progress corresponding to the procurement Id "
							+ asProcId, loAppEx);
			setMoState("Transaction Failed:: RFPReleaseService:checkEvaluationInProgress method - while checking evaluation in progress corresponding to the procurement Id "
					+ asProcId);
			throw loAppEx;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error(
					"Exception occured while checking evaluation in progress corresponding to the procurement Id "
							+ asProcId, loExp);
			setMoState("Transaction Failed:: RFPReleaseService:checkEvaluationInProgress method - while checking evaluation in progress corresponding to the procurement Id "
					+ asProcId);
			throw new ApplicationException(
					"Exception occured while checking evaluation in progress corresponding to the procurement Id "
							+ asProcId, loExp);
		}
		return loEvalProgress;
	}

	/**
	 * Modified as a part of release 3.1.0 for enhancement request 6024
	 * 
	 * This method copies the data from procurement_addendum_document to
	 * procurement_document_config table corresponding to the procurement Id
	 * <ul>
	 * <li>1. Retrieve "lbUpdateSuccessful" and procurement Id</li>
	 * <li>2. If the retrieved "lbUpdateSuccessful" is true then insert data
	 * from procurement_addendum_document to procurement_document_config table
	 * by executing query <b>insertProcDocumentConfig</b> corresponding to the
	 * procurement Id</li>
	 * <li>3. If the number of rows inserted is greater then 0 then delete the
	 * data from procurement_addendum_document by executing query
	 * <b>deleteProcAddendumDocument</b> corresponding to that procurement Id</li>
	 * <li>4. If number of rows deleted is greater than 0 then put
	 * "lbInsertSuccessful" as true else set it as false</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProcId - string representation of procurement Id
	 * @param loUpdateSuccessful - boolean status flag
	 * @return loInsertSuccessful - boolean status flag
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean updateProcDocumentConfig(SqlSession aoMybatisSession, String asProcId, Boolean loUpdateSuccessful)
			throws ApplicationException
	{
		Boolean loInsertSuccessful = false;
		if (loUpdateSuccessful)
		{
			try
			{
				HashMap<String, String> loInputMap = new HashMap<String, String>();
				Integer loNewDocsCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, asProcId,
						HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.FETCH_NEW_DOCS_COUNT,
						HHSConstants.JAVA_LANG_STRING);
				if (loNewDocsCount > HHSConstants.INT_ZERO)
				{
					loInputMap.put(HHSConstants.PROCUREMENT_ID, asProcId);
					List<ExtendedDocument> loExtendedDocumentList = (List<ExtendedDocument>) DAOUtil.masterDAO(
							aoMybatisSession, loInputMap, HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
							HHSConstants.FETCH_PROP_DOC_FOR_RELEASED, HHSConstants.JAVA_UTIL_HASH_MAP);
					if (loExtendedDocumentList != null && !loExtendedDocumentList.isEmpty())
					{
						Integer loVersionNumber = (Integer) DAOUtil.masterDAO(aoMybatisSession, asProcId,
								HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER,
								HHSConstants.GET_MAX_VERSION_PROC_DOCUMENTS, HHSConstants.JAVA_LANG_STRING);
						loVersionNumber++;
						for (ExtendedDocument loExtendedDocument : loExtendedDocumentList)
						{
							loExtendedDocument.setVersionNo(String.valueOf(loVersionNumber));
							if (loExtendedDocument != null && loExtendedDocument.getProcurementDocumentId() == null)
							{
								DAOUtil.masterDAO(aoMybatisSession, loExtendedDocument,
										HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER, HHSConstants.INS_PROC_DOC_CONFIG,
										HHSConstants.COM_NYC_HHS_MODEL_EXTENDED_DOC);
							}
							DAOUtil.masterDAO(aoMybatisSession, loExtendedDocument,
									HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER,
									HHSConstants.INS_PROC_DOC_CONFIG_MAPPING,
									HHSConstants.COM_NYC_HHS_MODEL_EXTENDED_DOC);
						}
					}
					loInsertSuccessful = true;
					setMoState("Addendum details copied from procurement_addendum_document to procurement_document_config table corresponding to the procurement Id");
				}
			}
			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handles over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */
			catch (ApplicationException loAppEx)
			{
				LOG_OBJECT
						.Error("Exception occured while coping details from procurement_addendum_document to procurement_document_config table corresponding to the procurement Id ",
								loAppEx);
				setMoState("Transaction Failed:: RFPReleaseService:updateProcDocumentConfig method - while coping details from procurement_addendum_document to procurement_document_config table corresponding to the procurement Id "
						+ asProcId);
				throw loAppEx;
			}
			catch (Exception loExp)
			{
				LOG_OBJECT
						.Error("Exception occured while coping details from procurement_addendum_document to procurement_document_config table corresponding to the procurement Id ",
								loExp);
				setMoState("Transaction Failed:: RFPReleaseService:updateProcDocumentConfig method - while coping details from procurement_addendum_document to procurement_document_config table corresponding to the procurement Id "
						+ asProcId);
				throw new ApplicationException(
						"Exception occured while coping details from procurement_addendum_document to procurement_document_config table corresponding to the procurement Id",
						loExp);
			}
		}
		return loInsertSuccessful;
	}

	/**
	 * Modified as a part of release 3.1.0 for enhancement request 6024
	 * 
	 * This method copies the data from addendum_question_config to
	 * procurement_question_config table corresponding to the procurement Id
	 * <ul>
	 * <li>1. Retrieve "lbInsertSuccessful" and procurement Id</li>
	 * <li>2. If the retrieved "lbInsertSuccessful" true then insert data from
	 * addendum_question_config to procurement_question_config table by
	 * executing query <b>insertProcQuestionConfig</b> corresponding to the
	 * procurement Id</li>
	 * <li>3. If the number of rows inserted is greater then 0 then delete the
	 * data from procurement_addendum_document by executing query
	 * <b>deleteAddendumQuestionConfig</b> corresponding to that procurement Id</li>
	 * <li>4. If number of rows deleted is greater than 0 then set
	 * "lbInsertProcQuestion" as true else put it as false</li>
	 * </ul>
	 * Defect #6024 changes Release 3.1.0
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProcId - string representation of procurement Id
	 * @param aoInsertSuccessful - boolean status flag
	 * @param asUserId - user id of current user
	 * @return loInsertProcQuestion - boolean status flag
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean updateProcQuestionConfig(SqlSession aoMybatisSession, String asProcId, Boolean aoInsertSuccessful,
			String asUserId) throws ApplicationException
	{
		Boolean loInsertProcQuestion = false;
		if (aoInsertSuccessful)
		{
			try
			{
				Integer loDataInserted = (Integer) DAOUtil.masterDAO(aoMybatisSession, asProcId,
						HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER, HHSConstants.INS_PROC_QUE_CONFIG,
						HHSConstants.JAVA_LANG_STRING);
				if (loDataInserted > 0)
				{
					HashMap loMap = new HashMap();
					loMap.put(HHSConstants.PROCUREMENT_ID, asProcId);
					List<ProposalQuestionAnswerBean> loProposalQuestionAnswerList = (List<ProposalQuestionAnswerBean>) DAOUtil
							.masterDAO(aoMybatisSession, loMap, HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
									HHSConstants.FETCH_PROP_QUES_FOR_RELEASED, HHSConstants.JAVA_UTIL_HASH_MAP);
					if (loProposalQuestionAnswerList != null && !loProposalQuestionAnswerList.isEmpty())
					{
						Integer loVersionNumber = (Integer) DAOUtil.masterDAO(aoMybatisSession, asProcId,
								HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER,
								HHSConstants.GET_MAX_VERSION_PROC_QUESTIONS, HHSConstants.JAVA_LANG_STRING);
						loVersionNumber++;
						for (ProposalQuestionAnswerBean loProposalQuestionAnswerBean : loProposalQuestionAnswerList)
						{
							loProposalQuestionAnswerBean.setVersionNo(String.valueOf(loVersionNumber));
							loProposalQuestionAnswerBean.setCreatedBy(asUserId);
							loProposalQuestionAnswerBean.setModifiedBy(asUserId);
							DAOUtil.masterDAO(aoMybatisSession, loProposalQuestionAnswerBean,
									HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER,
									HHSConstants.INSERT_QUESTION_MAPPING_RELEASE_ADDENDUM,
									HHSConstants.COM_NYC_MODEL_PROP_QUE_ANS_BEAN);
						}
					}
				}
				loInsertProcQuestion = true;
				setMoState("Addendum details copied from addendum_question_config to procurement_question_config table corresponding to the procurement Id");
			}
			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handles over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */
			catch (ApplicationException loAppEx)
			{
				LOG_OBJECT
						.Error("Exception occured while coping details from addendum_question_config to procurement_question_config table corresponding to the procurement Id",
								loAppEx);
				setMoState("Transaction Failed:: RFPReleaseService:updateProcQuestionConfig method -  while coping details from addendum_question_config to procurement_question_config table corresponding to the procurement Id"
						+ asProcId);
				throw loAppEx;
			}
			catch (Exception loExp)
			{
				LOG_OBJECT
						.Error("Exception occured while coping details from addendum_question_config to procurement_question_config table corresponding to the procurement Id",
								loExp);
				setMoState("Transaction Failed:: RFPReleaseService:updateProcQuestionConfig method -  while coping details from addendum_question_config to procurement_question_config table corresponding to the procurement Id"
						+ asProcId);
				throw new ApplicationException(
						"Exception occured while coping details from addendum_question_config to procurement_question_config table corresponding to the procurement Id",
						loExp);
			}
		}
		return loInsertProcQuestion;
	}

	/**
	 * Modified as a part of release 3.1.0 for enhancement request 6024
	 * 
	 * This method copies the data from addendum_evaluation_criteria to
	 * evaluation_criteria table corresponding to the procurement Id
	 * <ul>
	 * <li>1. Retrieve boolean flag and procurement Id</li>
	 * <li>2. If the retrieved "lbInsertProcQuestion" is true then insert data
	 * from addendum_evaluation_criteria to evaluation_criteria table by
	 * executing query <b>insertEvaluationCriteria</b> corresponding to the
	 * procurement Id</li>
	 * <li>3. If the number of rows inserted is greater then 0 then delete the
	 * data from procurement_addendum_document by executing query
	 * <b>deleteAddendumEvalCriteria</b> corresponding to that procurement Id</li>
	 * <li>4. If number of rows deleted is greater than 0 then set
	 * "lbInsertEvalCrireia" as true else put it as false</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProcId - string representation of procurement Id
	 * @param aoInsertProcQuestion - boolean status flag
	 * @return loInsertEvalCrireia - boolean status flag
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean updateEvaluationCriteria(SqlSession aoMybatisSession, String asProcId, Boolean aoInsertProcQuestion)
			throws ApplicationException
	{
		Boolean loInsertEvalCrireia = false;
		Map<String, Object> loData = new HashMap<String, Object>();
		if (aoInsertProcQuestion)
		{
			try
			{
				Integer loMaxVersion = (Integer) DAOUtil.masterDAO(aoMybatisSession, asProcId,
						HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER, HHSConstants.GET_MAX_VERSION_EVAL_CRITERIA,
						HHSConstants.JAVA_LANG_STRING);
				loMaxVersion++;
				loData.put(HHSConstants.PROCUREMENT_ID, asProcId);
				loData.put(HHSConstants.NEXT_VERSION, loMaxVersion);
				DAOUtil.masterDAO(aoMybatisSession, loData, HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER,
						HHSConstants.INS_EVAL_CRITERIA, HHSConstants.JAVA_UTIL_MAP);
				loInsertEvalCrireia = true;
				setMoState("Addendum details copied from addendum_evaluation_criteria to evaluation_criteria table corresponding to the procurement Id");
			}
			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handles over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */
			catch (ApplicationException loAppEx)
			{
				LOG_OBJECT
						.Error("Exception occured while coping details from addendum_evaluation_criteria to evaluation_criteria table corresponding to the procurement Id",
								loAppEx);
				setMoState("Transaction Failed:: RFPReleaseService:updateEvaluationCriteria method - while coping details from addendum_evaluation_criteria to evaluation_criteria table corresponding to the procurement Id"
						+ asProcId);
				throw loAppEx;
			}
			catch (Exception loExp)
			{
				LOG_OBJECT
						.Error("Exception occured while coping details from addendum_evaluation_criteria to evaluation_criteria table corresponding to the procurement Id",
								loExp);
				setMoState("Transaction Failed:: RFPReleaseService:updateEvaluationCriteria method - while coping details from addendum_evaluation_criteria to evaluation_criteria table corresponding to the procurement Id"
						+ asProcId);
				throw new ApplicationException(
						"Exception occured while coping details from addendum_evaluation_criteria to evaluation_criteria table corresponding to the procurement Id",
						loExp);
			}
		}
		return loInsertEvalCrireia;
	}

	/**
	 * This method deletes the data from the Addendum tables corresponding to
	 * the procurement Id
	 * <ul>
	 * <li>1. Retrieve boolean flag and procurement Id from the channel</li>
	 * <li>2. If the fetched boolean flag value is "true" then execute query
	 * <b>deleteRfpDocAddendunData</b>, <b>deleteProcAddendumDocument</b>,
	 * <b>deleteProcAddendumDocument</b>, <b>deleteAddendumQuestionConfig</b>,
	 * <b>deleteAddendumEvalCriteria</b> and <b>deleteProcAddendumService</b> to
	 * delete the data from addendum tables corresponding to the procurement Id</li>
	 * <li>3. If no. of rows deleted is greater than "0" then set "lbDeleteFlag"
	 * as true else set it as false</li>
	 * <li>4. Return the boolean flag</li>
	 * If any exception is thrown it will be handled here in the catch block
	 * with an error messege and it will be thrown again.
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProcId - string representation of procurement Id
	 * @param aoStatusFlag - boolean status flag
	 * @return loDeleteFlag - boolean status flag
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean deleteAddendumData(SqlSession aoMybatisSession, String asProcId, Boolean aoStatusFlag)
			throws ApplicationException
	{
		Boolean loDeleteFlag = false;
		if (aoStatusFlag)
		{
			try
			{
				Integer loDelStatus = (Integer) DAOUtil.masterDAO(aoMybatisSession, asProcId,
						HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER, HHSConstants.DEL_RFP_ADD_DATA,
						HHSConstants.JAVA_LANG_STRING);

				loDelStatus = (Integer) DAOUtil.masterDAO(aoMybatisSession, asProcId,
						HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER, HHSConstants.DEL_PROC_ADD_DOC,
						HHSConstants.JAVA_LANG_STRING);

				loDelStatus = (Integer) DAOUtil.masterDAO(aoMybatisSession, asProcId,
						HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER, HHSConstants.DEL_PROC_ADD_DATA,
						HHSConstants.JAVA_LANG_STRING);

				loDelStatus = (Integer) DAOUtil.masterDAO(aoMybatisSession, asProcId,
						HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER, HHSConstants.DEL_ADD_QUE_CONFIG,
						HHSConstants.JAVA_LANG_STRING);

				loDelStatus = (Integer) DAOUtil.masterDAO(aoMybatisSession, asProcId,
						HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER, HHSConstants.DEL_ADD_EVAL_CRITERIA,
						HHSConstants.JAVA_LANG_STRING);

				loDelStatus = (Integer) DAOUtil.masterDAO(aoMybatisSession, asProcId,
						HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER, HHSConstants.DEL_PROC_ADD_SERVICE,
						HHSConstants.JAVA_LANG_STRING);

				if (loDelStatus > HHSConstants.INT_ZERO)
				{
					loDeleteFlag = true;
				}
				else
				{
					loDeleteFlag = false;
				}

				setMoState("Addendum table data deleted from RFP_ADDENDUM_DOCUMENT, PROCUREMENT_ADDENDUM, ADDENDUM_QUESTION_CONFIG, PROCUREMENT_ADDENDUM_DOCUMENT, ADDENDUM_EVALUATION_CRITERIA and "
						+ "ADDENDUM_EVALUATION_CRITERIA corresponding to the procurement Id");
			}
			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handles over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */
			catch (ApplicationException loAppEx)
			{
				LOG_OBJECT
						.Error("Exception occured while deleting data from addendum tables corresponding to the procurement Id",
								loAppEx);
				setMoState("Transaction Failed:: RFPReleaseService:deleteAddendumTableData method - while deleting data from addendum tables corresponding to the procurement Id"
						+ asProcId);
				throw loAppEx;
			}
			catch (Exception loExp)
			{
				LOG_OBJECT
						.Error("Exception occured while deleting data from addendum tables corresponding to the procurement Id",
								loExp);
				setMoState("Transaction Failed:: RFPReleaseService:deleteAddendumTableData method - while deleting data from addendum tables corresponding to the procurement Id"
						+ asProcId);
				throw new ApplicationException(
						"Exception occured while deleting data from addendum tables corresponding to the procurement Id",
						loExp);
			}
		}
		return loDeleteFlag;
	}

	/**
	 * This method deletes the data from the Addendum tables corresponding to
	 * the procurement Id
	 * <ul>
	 * <li>1. Retrieve boolean flag and procurement Id from the channel</li>
	 * <li>2. If the fetched boolean flag value is "true" then execute query
	 * <b>deleteProcAddendumDocument</b> and <b>deleteProcAddendumService</b> to
	 * delete the data from addendum tables corresponding to the procurement Id</li>
	 * <li>3. If no. of rows deleted is greater than "0" then set "lbDeleteFlag"
	 * as true else set it as false</li>
	 * <li>4. Return the boolean flag</li>
	 * If any exception is thrown it will be handled here in the catch block
	 * with an error messege and it will be thrown again.
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProcurementId - string representation of procurement Id
	 * @param aoStatusFlag - boolean status flag
	 * @return loDeleteFlag - boolean status flag
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean deletePlannedAddendumData(SqlSession aoMybatisSession, String asProcurementId, Boolean aoStatusFlag)
			throws ApplicationException
	{
		Boolean loDelFlag = false;
		if (aoStatusFlag)
		{
			try
			{
				Integer loDeleteStatus = (Integer) DAOUtil.masterDAO(aoMybatisSession, asProcurementId,
						HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER, HHSConstants.DEL_PROC_ADD_DATA,
						HHSConstants.JAVA_LANG_STRING);

				loDeleteStatus = (Integer) DAOUtil.masterDAO(aoMybatisSession, asProcurementId,
						HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER, HHSConstants.DEL_PROC_ADD_SERVICE,
						HHSConstants.JAVA_LANG_STRING);

				if (loDeleteStatus > HHSConstants.INT_ZERO)
				{
					loDelFlag = true;
				}
				else
				{
					loDelFlag = false;
				}

				setMoState("Addendum table data deleted from PROCUREMENT_ADDENDUM ADDENDUM_EVALUATION_CRITERIA and "
						+ "PRCRMNT_ADDM_SERVICES procurement Id");
			}
			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handles over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */
			catch (ApplicationException loAppEx)
			{
				LOG_OBJECT.Error("Exception occured while deleting data from addendum tables procurement Id", loAppEx);
				setMoState("Transaction Failed:: RFPReleaseService:deletePlannedAddendumData method - while deleting data from addendum tables procurement Id"
						+ asProcurementId);
				throw loAppEx;
			}
			catch (Exception loExp)
			{
				LOG_OBJECT
						.Error("Exception occured while deleting data from addendum tables corresponding to the procurement Id",
								loExp);
				setMoState("Transaction Failed:: RFPReleaseService:deletePlannedAddendumData method - while deleting data from addendum tables procurement Id"
						+ asProcurementId);
				throw new ApplicationException(
						"Exception occured while deleting data from addendum tables procurement Id", loExp);
			}
		}
		return loDelFlag;
	}

	/**
	 * This method updates the Approved Providers in the procurement_provider
	 * table corresponding to the procurement_id (changes active flag to 1)
	 * <ul>
	 * <li>1. Retrieve user_Id, procurement Id and the boolean flag value</li>
	 * <li>2. If the retrieved boolean flag value is true then create a HashMap
	 * and populate it with user Id, procurement Id and provider status
	 * (eligible to propose)</li>
	 * <li>3. Execute query <b>updateAppProviders</b> to update the approved
	 * providers corresponding to that procurement Id</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asUserId - string representation of user Id
	 * @param asProcId - string representation of procurement Id
	 * @param aoInsertEvalCrireia - boolean status flag
	 * @return loValue - number of rows inserted
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Integer updateApprovedProviders(SqlSession aoMybatisSession, String asUserId, String asProcId,
			Boolean aoInsertEvalCrireia) throws ApplicationException
	{
		Integer loValue = HHSConstants.INT_ZERO;
		if (aoInsertEvalCrireia)
		{
			try
			{
				HashMap<String, Object> loHashMap = new HashMap<String, Object>();
				Integer liEligibleToPropose = Integer.parseInt(PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROVIDER_ELIGIBLE_TO_PROPOSE));
				loHashMap.put(HHSConstants.USER_ID, asUserId);
				loHashMap.put(HHSConstants.PROCID_LOWERCASE, asProcId);
				loHashMap.put(HHSConstants.ELIGIBLE_TO_PROPOSE, liEligibleToPropose);
				loValue = (Integer) DAOUtil.masterDAO(aoMybatisSession, loHashMap,
						HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER, HHSConstants.UPDATE_APP_PROVIDERS,
						HHSConstants.JAVA_UTIL_HASH_MAP);
				setMoState("Approved Providers get inserted in procurement_provider table corresponding to the procurement Id");
			}
			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handles over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */
			catch (ApplicationException loAppEx)
			{
				LOG_OBJECT.Error(
						"Exception occured while inserting approved providers corresponding to the procurement Id",
						loAppEx);
				setMoState("Transaction Failed:: RFPReleaseService:insertNewApprovedProviders method - while inserting approved providers corresponding to the procurement Id"
						+ asProcId);
				throw loAppEx;
			}
			catch (Exception loExp)
			{
				LOG_OBJECT.Error(
						"Exception occured while inserting approved providers corresponding to the procurement Id",
						loExp);
				setMoState("Transaction Failed:: RFPReleaseService:insertNewApprovedProviders method - while inserting approved providers corresponding to the procurement Id"
						+ asProcId);
				throw new ApplicationException(
						"Exception occured while inserting approved providers corresponding to the procurement Id",
						loExp);
			}
		}
		return loValue;
	}

	/**
	 * Modified as a part of release 3.1.0 for enhancement request 6024
	 * 
	 * This method save details entered by city user on evaluation criteria
	 * screen for particular procurement ID.
	 * <ul>
	 * <li>1. Retrieves the required information i.e. procurement id, user name</li>
	 * <li>2. Execute query <b>saveEvaluationCriteria</b> to update details in
	 * evaluation_criteria table</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession - mybatis SQL session
	 * @param asProcurementStatus - Current procurement status
	 * @param aoRfpReleaseBean - aoRfpReleaseBean
	 * @return Boolean -- return true if Evaluation Criteria is saved
	 *         Successfully
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean saveEvaluationCriteria(SqlSession aoMyBatisSession, String asProcurementStatus,
			RFPReleaseBean aoRfpReleaseBean) throws ApplicationException
	{
		Boolean loSaveStatus = Boolean.TRUE;
		Boolean loUpdateCheck = Boolean.TRUE;
		try
		{
			saveEvalCriteriaInDB(aoMyBatisSession, aoRfpReleaseBean, loUpdateCheck, asProcurementStatus);
		}
		// handling exception while processing evaluation criteria details.
		catch (ApplicationException loExp)
		{
			loSaveStatus = Boolean.FALSE;
			LOG_OBJECT.Error("Error occurred while processing evaluation data" + loExp);
			setMoState("Error occurred while processing evaluation data:" + loExp.getMessage());
			throw loExp;
		}
		// handling exception other than Application Exception.
		catch (Exception loEx)
		{
			loSaveStatus = Boolean.FALSE;
			LOG_OBJECT.Error("Error occurred while processing evaluation data", loEx);
			setMoState("Error occurred while processing evaluation data:" + loEx.getMessage());
			throw new ApplicationException("Error occurred while processing evaluation data:", loEx);
		}
		return loSaveStatus;
	}

	/**
	 * This method save details entered by city user on evaluation criteria
	 * screen for particular procurement ID.
	 * <ul>
	 * <li>Retrieves the required information i.e. procurement id, user name</li>
	 * <li>Execute query to delete Addendum Evaluation
	 * Criteria<b>deleteAddendumEvaluationCriteria</b></li>
	 * <li>Execute query to delete Evaluation
	 * Criteria<b>deleteEvaluationCriteria </b></li>
	 * <li>Execute query to save<b>saveEvaluationCriteria</b></li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession aoMyBatisSession
	 * @param aoRfpReleaseBean aoRfpReleaseBean
	 * @param aoUpdateCheck abUpdateCheck
	 * @param asProcurementStatus asProcurementStatus
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	private void saveEvalCriteriaInDB(SqlSession aoMyBatisSession, RFPReleaseBean aoRfpReleaseBean,
			Boolean aoUpdateCheck, String asProcurementStatus) throws ApplicationException
	{
		Integer loRowsUpdated = 0;
		List<EvaluationCriteriaBean> loEvaluationCriteriaBeanList = aoRfpReleaseBean.getLoEvaluationCriteriaBeanList();
		for (EvaluationCriteriaBean loEvaluationCriteriaBean : loEvaluationCriteriaBeanList)
		{
			loEvaluationCriteriaBean.setCreatedByUserId(aoRfpReleaseBean.getCreatedByUserId());
			loEvaluationCriteriaBean.setModifiedByUserId(aoRfpReleaseBean.getModifiedByUserId());
			loEvaluationCriteriaBean.setProcurementId(aoRfpReleaseBean.getProcurementId());
			if ((loEvaluationCriteriaBean.getScoreCriteria() == null || loEvaluationCriteriaBean.getScoreCriteria()
					.isEmpty()))
			{
				if ((loEvaluationCriteriaBean.getScoreSeqNumber() == null || loEvaluationCriteriaBean
						.getScoreSeqNumber() == HHSConstants.INT_ZERO))
				{
					continue;
				}
				else
				{
					if (PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
							HHSConstants.STATUS_PROCUREMENT_RELEASED).equalsIgnoreCase(asProcurementStatus))
					{
						DAOUtil.masterDAO(aoMyBatisSession, loEvaluationCriteriaBean,
								HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER, HHSConstants.DEL_ADD_EVALUATION_CRITERIA,
								HHSConstants.COM_NYC_HHSMODEL_EVAL_CRITERIA_BEAN);
					}
					else
					{
						DAOUtil.masterDAO(aoMyBatisSession, loEvaluationCriteriaBean,
								HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER, HHSConstants.DEL_EVAL_CRITERIA,
								HHSConstants.COM_NYC_HHSMODEL_EVAL_CRITERIA_BEAN);
					}
				}
			}
			else
			{
				loEvaluationCriteriaBean.setScoreFlag(HHSConstants.ONE);
				if (PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_PROCUREMENT_RELEASED).equalsIgnoreCase(asProcurementStatus))
				{
					if (aoUpdateCheck)
					{
						loRowsUpdated = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loEvaluationCriteriaBean,
								HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER, HHSConstants.UPDATE_ADD_EVAL_CRITERIA,
								HHSConstants.COM_NYC_HHSMODEL_EVAL_CRITERIA_BEAN);
						if (loRowsUpdated == null || loRowsUpdated == HHSConstants.INT_ZERO)
						{
							aoUpdateCheck = Boolean.FALSE;
						}
					}
					if (!aoUpdateCheck
							&& (aoRfpReleaseBean.getNullCheck() == null || aoRfpReleaseBean.getNullCheck().isEmpty()))
					{
						DAOUtil.masterDAO(aoMyBatisSession, loEvaluationCriteriaBean,
								HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER, HHSConstants.SAVE_ADD_EVAL_CRITERIA,
								HHSConstants.COM_NYC_HHSMODEL_EVAL_CRITERIA_BEAN);
					}
					else if (!aoUpdateCheck
							&& (aoRfpReleaseBean.getNullCheck() != null && aoRfpReleaseBean.getNullCheck()
									.equalsIgnoreCase(HHSConstants.YES_LOWERCASE)))
					{
						break;
					}
					aoUpdateCheck = Boolean.TRUE;
				}
				else if (PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_PROCUREMENT_DRAFT).equalsIgnoreCase(asProcurementStatus)
						|| PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_PROCUREMENT_PLANNED).equalsIgnoreCase(asProcurementStatus))
				{
					if (aoUpdateCheck)
					{
						loRowsUpdated = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loEvaluationCriteriaBean,
								HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER, HHSConstants.UPDATE_EVAL_CRITERIA,
								HHSConstants.COM_NYC_HHSMODEL_EVAL_CRITERIA_BEAN);
						if (loRowsUpdated == null || loRowsUpdated == HHSConstants.INT_ZERO)
						{
							aoUpdateCheck = Boolean.FALSE;
						}
					}
					if (!aoUpdateCheck
							&& (aoRfpReleaseBean.getNullCheck() == null || aoRfpReleaseBean.getNullCheck().isEmpty()))
					{
						DAOUtil.masterDAO(aoMyBatisSession, loEvaluationCriteriaBean,
								HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER, HHSConstants.SAVE_EVAL_CRITERIA,
								HHSConstants.COM_NYC_HHSMODEL_EVAL_CRITERIA_BEAN);
					}
					else if (!aoUpdateCheck
							&& (aoRfpReleaseBean.getNullCheck() != null && aoRfpReleaseBean.getNullCheck()
									.equalsIgnoreCase(HHSConstants.YES_LOWERCASE)))
					{
						break;
					}
					aoUpdateCheck = Boolean.TRUE;
				}
			}
		}
	}

	/**
	 * This method fetches procurement Status for particular procurement ID.
	 * <ul>
	 * <li>1. Create the map to hold the context data.</li>
	 * <li>2. Set procurement id in the map.</li>
	 * <li>3. Execute query <b>getProcurementStatus</b> to get Procurement
	 * Status from database</li>
	 * <li>4. Return the procurement status fetched.</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession - mybatis SQL session
	 * @param asProcurementId - string representation of procurement id
	 * @return - String - string representation of procurement status
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public String getProcurementStatus(SqlSession aoMyBatisSession, String asProcurementId) throws ApplicationException
	{
		String lsProcurementStatus = null;
		HashMap<String, String> loExceptionParamMap = new HashMap<String, String>();
		loExceptionParamMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		try
		{
			// Fetching Procurement Status code ,passing ProcurementId as Input
			lsProcurementStatus = (String) DAOUtil.masterDAO(aoMyBatisSession, asProcurementId,
					HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER, HHSConstants.GET_PROC_STATUS,
					HHSConstants.JAVA_LANG_STRING);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException loExp)
		{
			setMoState("Error occurred while fetching Procurement Status for ProcurementId:" + asProcurementId);
			loExp.setContextData(loExceptionParamMap);
			LOG_OBJECT.Error("Error occurred while fetching Procurement Status", loExp);
			throw loExp;
		}
		catch (Exception loExp)
		{
			setMoState("Error occurred while fetching Procurement Status for ProcurementId:" + asProcurementId);
			LOG_OBJECT.Error("Error occurred while fetching Procurement Status", loExp);
			throw new ApplicationException("Error occurred while fetching Procurement Status for ProcurementId:"
					+ asProcurementId, loExp);
		}
		return lsProcurementStatus;
	}

	/**
	 * This method is used to update the rfp document status once the rfp is
	 * released
	 * <ul>
	 * <li>Get all the parameter values from the Request Parameters</li>
	 * <li>Execute Query with Id <code>updateRFPdocumentStatus</code> of
	 * <code>RFPReleaseMapper</code> mapper</li>
	 * </ul>
	 * @param aoMybatisSession aoMybatisSession
	 * @param asProcId asProcId
	 * @param asUserId asUserId
	 * @param aoValidateStatus lbValidateStatus
	 * @return loUpdateStatus boolean
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean updateRfpDocumentStatus(SqlSession aoMybatisSession, String asProcId, String asUserId,
			Boolean aoValidateStatus) throws ApplicationException
	{
		Boolean loUpdateStatus = false;
		if (aoValidateStatus)
		{
			try
			{
				HashMap<String, Object> loHashMap = new HashMap<String, Object>();
				loHashMap.put(HHSConstants.USER_ID, asUserId);
				loHashMap.put(HHSConstants.PROCID_LOWERCASE, asProcId);
				loHashMap.put(HHSConstants.DOCUMENT_STATUS, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.DOCUMENT_SUBMITTED));
				DAOUtil.masterDAO(aoMybatisSession, loHashMap, HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER,
						HHSConstants.UPDATE_RFP_DOCUMENT_STATUS, HHSConstants.JAVA_UTIL_HASH_MAP);
				loUpdateStatus = true;
				setMoState("RFP Document status updated successfully for ProcurementId:" + asProcId);
			}
			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handles over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */
			catch (ApplicationException loAppEx)
			{
				LOG_OBJECT.Error("Error occurred while updating  document status for procurement Id:" + asProcId,
						loAppEx);
				setMoState("Transaction Failed:: RFPReleaseService:updateRfpDocumentStatus method - while updating  document status for procurement Id"
						+ asProcId);
				throw loAppEx;
			}
			catch (Exception loExp)
			{
				LOG_OBJECT
						.Error("Error occurred while updating  document status for procurement Id:" + asProcId, loExp);
				setMoState("Transaction Failed:: RFPReleaseService:updateRfpDocumentStatus method - while updating  document status for procurement Id"
						+ asProcId);
				throw new ApplicationException("Error occurred while updating  document status for procurement Id:"
						+ asProcId, loExp);
			}
		}
		return loUpdateStatus;
	}

}
