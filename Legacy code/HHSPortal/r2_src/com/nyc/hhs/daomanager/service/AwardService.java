package com.nyc.hhs.daomanager.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.component.operations.WorkflowOperations;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSP8Constants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.AcceptProposalTaskBean;
import com.nyc.hhs.model.AwardBean;
import com.nyc.hhs.model.EvaluationBean;
import com.nyc.hhs.model.ExtendedDocument;
import com.nyc.hhs.model.NotificationDataBean;
import com.nyc.hhs.model.ProposalDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8SecurityOperations;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.PropertyLoader;

import filenet.vw.api.VWSession;

/**
 * This service class will get the method calls from controller through
 * transaction layer. Execute queries by calling mapper and return query output
 * back to controller. If any error exists, wrap the exception into Application
 * Exception and throw it to controller.
 */

public class AwardService extends ServiceState
{
	/**
	 * This is a log object used to log any exception into log file.
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(AwardService.class);

	/**
	 * This method fetches Award Details for given Procurement and ProposalId
	 * 
	 * <ul>
	 * <li>Retrieve Map containing procurement Id and user org Id from the
	 * Channel object</li>
	 * <li>Create context data HashMap and populate the same with the parameter
	 * map</li>
	 * <li>If aoIsFromFinance is true then execute the query id <b>
	 * fetchAwardDetailsForFinance</b></li>
	 * <li>If the fetched parameter map is not null then execute query
	 * <b>fetchAwardDetails</b> to fetch the required award details</li>
	 * <li>Return the fetched award details</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis Sql Session
	 * @param aoParamMap - input map containing procurement Id and
	 *            organization_id
	 * @param aoIsFromFinance - boolean status flag stating whether or not
	 *            budget details are required
	 * @return loAwardBean - an AwardBean object containing award related
	 *         details
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public AwardBean fetchAwardDetails(SqlSession aoMybatisSession, Map<String, String> aoParamMap,
			Boolean aoIsFromFinance) throws ApplicationException
	{
		AwardBean loAwardBean = null;
		HashMap<String, Object> loHMContextData = new HashMap<String, Object>();
		loHMContextData.put(HHSConstants.PARAM_MAP_LOWERCASE, aoParamMap);
		
		//*** Start QC 9585 R 8.9 do not expose password for service account in logs
		String param = CommonUtil.maskPassword(loHMContextData);
		LOG_OBJECT.Debug("Entered into fetching award details:: " + param);
		//LOG_OBJECT.Debug("Entered into fetching award details::" + loHMContextData.toString());
		//*** End QC 9585 R 8.9 do not expose password for service account in logs

		// checking if the param map contains data or not
		if (aoParamMap != null)
		{
			try
			{
				// checking if the value of boolean flag "aoIsFromFinance" is
				// true then execute query
				// fetchAwardDetailsForFinance else execute fetchAwardDetails
				if (aoIsFromFinance)
				{
					loAwardBean = (AwardBean) DAOUtil.masterDAO(aoMybatisSession, aoParamMap,
							HHSConstants.MAPPER_CLASS_AWARDS_MAPPER, HHSConstants.FETCH_AWARD_DETAILS_FOR_FINANCE,
							HHSConstants.JAVA_UTIL_MAP);
				}
				else
				{
					loAwardBean = (AwardBean) DAOUtil.masterDAO(aoMybatisSession, aoParamMap,
							HHSConstants.MAPPER_CLASS_AWARDS_MAPPER, HHSConstants.FETCH_AWARD_DETAILS,
							HHSConstants.JAVA_UTIL_MAP);
				}
				setMoState("Award Details fetched successfully for proposal Id:"
						+ aoParamMap.get(HHSConstants.PROPOSAL_ID));
			}
			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handles over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */
			catch (ApplicationException aoExp)
			{
				setMoState("Transaction Failed:: EvaluationService:fetchAwardDetails method - Error while fetching Award Details for proposal Id:"
						+ aoParamMap.get(HHSConstants.PROPOSAL_ID));
				aoExp.setContextData(loHMContextData);
				LOG_OBJECT.Error("Error while fetching Award Details for proposal Id:", aoExp);
				throw aoExp;
			}
			// handling exception other than Application Exception
			catch (Exception aoExp)
			{
				ApplicationException loAppExp = new ApplicationException(
						"Error while fetching Award Details for proposal Id:", aoExp);
				LOG_OBJECT.Error("Error while fetching Award Details for proposal Id:", loAppExp);
				setMoState("Transaction Failed:: EvaluationService:fetchAwardDetails method - Error while fetching Award Details for proposal Id:"
						+ aoParamMap.get(HHSConstants.PROPOSAL_ID));
				throw loAppExp;
			}
		}
		return loAwardBean;
	}

	/**
	 * This method fetches Award Id corresponding to a Procurement Id
	 * 
	 * <ul>
	 * <li>1. Retrieve Map containing procurement Id and user org Id from the
	 * Channel object</li>
	 * <li>2. Create context data HashMap and populate the same with the
	 * parameter map</li>
	 * <li>3. If the fetched parameter map is not null then Execute query
	 * <b>fetchAwardId</b> to fetch the required award Id</li>
	 * <li>4. Return the fetched award Id</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoParamMap an award map object
	 * @return lsAwardId - string representation of award Id
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public String fetchAwardId(SqlSession aoMybatisSession, Map<String, String> aoParamMap) throws ApplicationException
	{
		String lsAwardId = null;
		HashMap<String, Object> loHMContextData = new HashMap<String, Object>();
		loHMContextData.put(HHSConstants.PARAM_MAP_LOWERCASE, aoParamMap);
	
		//*** Start QC 9585 R 8.9 do not expose password for service account in logs
		String param = CommonUtil.maskPassword(loHMContextData);
		LOG_OBJECT.Debug("Entered into fetching award Id:: " + param);
		//*** End QC 9585 R 8.9 do not expose password for service account in logs

		// checking if the param map contains data or not
		if (aoParamMap != null)
		{
			try
			{
				lsAwardId = (String) DAOUtil.masterDAO(aoMybatisSession, aoParamMap,
						HHSConstants.MAPPER_CLASS_AWARDS_MAPPER, HHSConstants.FETCH_AWARD_ID,
						HHSConstants.JAVA_UTIL_MAP);
				setMoState("Award Id fetched successfully for ProcurementId Id:"
						+ aoParamMap.get(HHSConstants.PROCUREMENT_ID));
			}
			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handles over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */
			catch (ApplicationException aoExp)
			{
				setMoState("Transaction Failed:: EvaluationService:fetchAwardId method - Error while fetching Award Id for ProcurementId Id:"
						+ aoParamMap.get(HHSConstants.PROCUREMENT_ID));
				aoExp.setContextData(loHMContextData);
				LOG_OBJECT.Error("Error while fetching Award Id :", aoExp);
				throw aoExp;
			}
			// handling exception other than Application Exception
			catch (Exception aoExp)
			{
				ApplicationException loAppExp = new ApplicationException(
						"Error while fetching Award Id for ProcurementId Id:", aoExp);
				LOG_OBJECT.Error("Error while fetching Award Id for ProcurementId Id:", loAppExp);
				setMoState("Transaction Failed:: EvaluationService:fetchAwardDetails method - Error while fetching Award Details for proposal Id:"
						+ aoParamMap.get(HHSConstants.PROPOSAL_ID));
				throw loAppExp;
			}
		}
		return lsAwardId;
	}

	/**
	 * This method fetches Award documents corresponding to the parameter map
	 * 
	 * <ul>
	 * <li>1. Retrieve Map containing procurement Id and user org Id from the
	 * Channel object</li>
	 * <li>2. Create context data HashMap and populate the same with the
	 * parameter map</li>
	 * <li>3. If the fetched parameter map is not null then execute query
	 * <b>fetchAwardDocuments</b> to fetch the required award Id</li>
	 * <li>4. Return the fetched list of award documents</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param loParamMap an award map object
	 * @return loAwardDocumentList - an object of type List<ExtendedDocument>
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<ExtendedDocument> fetchAwardDocuments(SqlSession aoMybatisSession, Map<String, String> loParamMap)
			throws ApplicationException
	{
		List<ExtendedDocument> loAwardDocumentList = null;
		HashMap<String, Object> loHMContextData = new HashMap<String, Object>();
		loHMContextData.put(HHSConstants.PARAM_MAP_LOWERCASE, loParamMap);
        
		//*** Start QC 9585 R 8.9 do not expose password for service account in logs
		String param = CommonUtil.maskPassword(loHMContextData);
		LOG_OBJECT.Debug("Entered into fetching award documements::" + param);
		//LOG_OBJECT.Debug("Entered into fetching award documements::" + loHMContextData.toString());
		//*** End QC 9585 R 8.9 do not expose password for service account in logs
		
		
		// checking if the param map contains data or not
		if (loParamMap != null)
		{
			try
			{
				loAwardDocumentList = (List<ExtendedDocument>) DAOUtil.masterDAO(aoMybatisSession, loParamMap,
						HHSConstants.MAPPER_CLASS_AWARDS_MAPPER, HHSConstants.FETCH_AWARD_DOCUMENTS,
						HHSConstants.JAVA_UTIL_MAP);
				setMoState("Award Documents List fetched successfully for proposal Id:"
						+ loParamMap.get(HHSConstants.PROPOSAL_ID));
			}
			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handles over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */
			catch (ApplicationException aoExp)
			{
				setMoState("Transaction Failed:: EvaluationService:fetchAwardDocuments method - Error while fetching Award Documents List for proposal Id:"
						+ loParamMap.get(HHSConstants.PROPOSAL_ID));
				aoExp.setContextData(loHMContextData);
				LOG_OBJECT.Error("Error while fetching Award Documents List:", aoExp);
				throw aoExp;
			}
			// handling exception other than Application Exception
			catch (Exception aoExp)
			{
				LOG_OBJECT.Error("Error while fetching Award Documents List for proposal Id:", aoExp);
				setMoState("Transaction Failed:: EvaluationService:fetchAwardDetails method - Error while fetching Award Details for proposal Id:"
						+ loParamMap.get(HHSConstants.PROPOSAL_ID));
				throw new ApplicationException("Error occurred while fetching award documents", aoExp);
			}
		}
		return loAwardDocumentList;
	}

	/**
	 * This method fetches the award review status from database for given
	 * procurement id and evaluation pool mapping id.
	 * <ul>
	 * <li>1. Create the loContextDataMap to hold the context data.</li>
	 * <li>2. Set the input data in the loContextDataMap.</li>
	 * <li>3. Execute query "fetchAwardReviewStatus" from awards mapper to fetch
	 * award review status</li>
	 * </ul>
	 * <ul>
	 * <li>Method updated for R4</li>
	 * </ul>
	 * Change: Updated query for Competition Pool
	 * 
	 * Changed By: Pallavi
	 * 
	 * Changed Date: 21 Jan 2014
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProcurementId - procurement id
	 * @param asEvalPoolMappingId - Evaluation Pool Mapping Id
	 * @return loAwardReviewStatus - an EvaluationBean object
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	public EvaluationBean fetchAwardReviewStatus(SqlSession aoMybatisSession, String asProcurementId,
			String asEvalPoolMappingId) throws ApplicationException
	{
		EvaluationBean loAwardReviewStatus = null;
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID, asProcurementId);
		loContextDataMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvalPoolMappingId);
		try
		{
			Map<String, Object> loDataMap = new HashMap<String, Object>();
			loDataMap.put(HHSConstants.PROCUREMENT_ID, asProcurementId);
			loDataMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvalPoolMappingId);
			loAwardReviewStatus = (EvaluationBean) DAOUtil.masterDAO(aoMybatisSession, loDataMap,
					HHSConstants.MAPPER_CLASS_AWARDS_MAPPER, HHSConstants.FETCH_AWARD_REVIEW_STATUS,
					HHSConstants.JAVA_UTIL_MAP);
			setMoState("Award Review Status fetched successfully for procurement Id:" + asProcurementId);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching Award Review Status", aoAppEx);
			setMoState("Error while fetching Award Review Status" + aoAppEx.getMessage());
			throw aoAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Error while fetching Award Review Status", aoAppEx);
			setMoState("Error while fetching Award Review Status" + aoAppEx.getMessage());
			throw new ApplicationException("Error while fetching Award Review Status", aoAppEx);
		}
		return loAwardReviewStatus;
	}

	/**
	 * This method remove Award Documents.
	 * 
	 * <ul>
	 * <li>1. Execute the query "removeAwardDocuments" with asDocumentId as a
	 * parameter.</li>
	 * <li>2. Return the remove status.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession sql session object
	 * @param aoParamMap parameter details map
	 * @return loRowsDeleted - number of rows deleted
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Integer removeAwardDocuments(SqlSession aoMybatisSession, Map<String, Object> aoParamMap)
			throws ApplicationException
	{
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.REMOVE_AWARD_DOCS, aoParamMap);
		Integer loRowsDeleted = null;
		try
		{
			loRowsDeleted = (Integer) DAOUtil
					.masterDAO(aoMybatisSession, aoParamMap, HHSConstants.MAPPER_CLASS_AWARDS_MAPPER,
							HHSConstants.REMOVE_AWARD_DOCS, HHSConstants.JAVA_UTIL_MAP);
		}
		// catch any application exception thrown from the code and throw it
		// forward
		catch (ApplicationException loExp)
		{
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Exception occured while removing Award Documents Details in db", loExp);
			setMoState("Error while remove Award document" + loExp.getMessage());
			throw new ApplicationException("Exception occured while removing Award Documents Details in db", loExp);
		}
		return loRowsDeleted;
	}

	/**
	 * This method is used to insert/update the document details when user add
	 * or upload a new document
	 * <ul>
	 * <li>Get the document details map from the argument</li>
	 * <li>Get the replacing document id from the argument</li>
	 * <li>If the replacing document id is either null or empty then insert a
	 * new record by executing <code>insertAwardDocumentDetails</code> query
	 * <li>
	 * <li>Else update the record with new document id value by executing
	 * <code>updateAwardDocDetails</code> Query</li>
	 * </ul>
	 * @param aoMybatisSession sql session
	 * @param aoParamMap Document details parameter map
	 * @param asReplacingDocId Replacing document id
	 * @return Number of records updated Integer
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Integer insertAwardDocumentDetails(SqlSession aoMybatisSession, Map<String, Object> aoParamMap,
			String asReplacingDocId) throws ApplicationException
	{
		Integer loRowsInserted = HHSConstants.INT_ZERO;
		try
		{
			if (null == asReplacingDocId || asReplacingDocId.isEmpty()
					|| HHSConstants.NULL.equalsIgnoreCase(asReplacingDocId))
			{
				loRowsInserted = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoParamMap,
						HHSConstants.MAPPER_CLASS_AWARDS_MAPPER, HHSConstants.INS_AWARD_DOC_DET,
						HHSConstants.JAVA_UTIL_MAP);
			}
			else
			{
				aoParamMap.put(HHSConstants.REPLACING_DOCUMENT_ID, asReplacingDocId);
				loRowsInserted = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoParamMap,
						HHSConstants.MAPPER_CLASS_AWARDS_MAPPER, HHSConstants.UPDATE_AWARD_DOC_DETAILS,
						HHSConstants.JAVA_UTIL_MAP);
				if (loRowsInserted == HHSConstants.INT_ZERO)
				{
					throw new ApplicationException("Error Occured while updating award documents details");
				}
			}
		}
		// Catch the application exception thrown from dao layer and log it
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Error while Inserting Award Document Details", aoAppExp);
			setMoState("Error while Inserting Award Document Details");
			throw aoAppExp;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error while Inserting Award Document Details", loExp);
			setMoState("Error while Inserting Award Document Details");
			throw new ApplicationException("Error while Inserting Award Document Details", loExp);
		}
		return loRowsInserted;
	}

	/**
	 * This Method will retrieve the Award Information from Database
	 * <ul>
	 * <li>1.Execute Query <b>fetchAwardsDetails</b> ,pass ContractId as input</li>
	 * <li>2.Obtain Provider Name,Award Amount,Status,Epin</li>
	 * <li>3.Return AwardBean which obtains above info</li>
	 * <ul>
	 * @param aoMybatisSession SqlSession
	 * @param asContractId String
	 * @return loAwardDetails - AwardBean Bean
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public AwardBean fetchAwardsDetails(SqlSession aoMybatisSession, String asContractId) throws ApplicationException
	{
		AwardBean loAwardDetails = null;
		try
		{ // fetching Award Details
			loAwardDetails = (AwardBean) DAOUtil.masterDAO(aoMybatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_AWARDS_MAPPER, HHSConstants.FETCH_AWARDS_DETAILS,
					HHSConstants.JAVA_LANG_STRING);
			setMoState("Award Details fetched  sucessfully:");
		}
		// Handling Application Exception
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error while fetching  Award Details:", aoExp);
			setMoState("Error while fetching  Award Details:");
			throw aoExp;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error while fetching  Award Details:", loExp);
			setMoState("Error while fetching  Award Details:");
			throw new ApplicationException("Error while fetching  Award Details:", loExp);
		}
		return loAwardDetails;
	}

	/**
	 * The Service updates the Award Status to canceled
	 * <ul>
	 * <li>1.Obtain the required information to update the status i.e
	 * ConractId,UserId ,StatusId</li>
	 * <li>2. Trigger query <b>updateAwardStatus</b> query</li>
	 * <li>3. Return update Award Status Flag</li>
	 * <li>Made changes for release 3.10.0 enhancement 5686</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession
	 * @param aoStatusInfo Map<String, String>
	 * @return loUpdateAwardStatusFlag - Boolean
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Boolean updateAwardStatus(SqlSession aoMybatisSession, Map<String, String> aoStatusInfo, String asReuseEpin)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into update Award Status");
		HashMap<String, Object> loHmReqExceProp = new HashMap<String, Object>();
		loHmReqExceProp.put(HHSConstants.STATUS_INFO_MAP_ARG, aoStatusInfo);
		Boolean loUpdateAwardStatusFlag = Boolean.FALSE;
		try
		{ // setting Award Status Id as canceled
			aoStatusInfo.put(HHSConstants.STATUS_ID, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_CONTRACT_CANCELLED));
			aoStatusInfo.put(HHSConstants.ADD_DELETE_STATUS, HHSConstants.D);
			// made changes for release 3.10.0 enhancement 5686
			if (asReuseEpin == null)
			{
				asReuseEpin = HHSConstants.NOT_REUSE;
			}
			aoStatusInfo.put(HHSConstants.REUSE_EPIN, asReuseEpin);
			// Calling updateAwardStatus for updating Award Status,passing
			// required using Map
			DAOUtil.masterDAO(aoMybatisSession, aoStatusInfo, HHSConstants.MAPPER_CLASS_AWARDS_MAPPER,
					HHSConstants.UPDATE_AWARD_STATUS, HHSConstants.JAVA_UTIL_MAP);
			loUpdateAwardStatusFlag = Boolean.TRUE;

			setMoState("Award Status sucessfully updated to cancelled for ContractId"
					+ aoStatusInfo.get(HHSConstants.CONTRACT_ID));
		}
		// handling Application exception
		catch (ApplicationException aoExp)
		{
			setMoState("Error while updating Award Status :");
			aoExp.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error while updating Award Status :", aoExp);
			throw aoExp;
		}
		// handling exception other than Application exception
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while updating Award Status :", aoExp);
			setMoState("Error while updating Award Status :");
			throw new ApplicationException("Exception while updating Award Status ", aoExp);
		}

		return loUpdateAwardStatusFlag;

	}

	/**
	 * for enahncement 6574 for Release 3.10.0
	 * @param aoMybatisSession
	 * @param aoContractIdList
	 * @return
	 * @throws ApplicationException
	 */
	public Boolean updateAwardStatusList(SqlSession aoMybatisSession, List<String> aoContractIdList)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into updateAwardStatusList");
		Map<String, String> loStatusInfo = new HashMap<String, String>();
		Boolean loUpdateAwardStatusFlag = Boolean.FALSE;
		try
		{ // setting Award Status Id as canceled
			loStatusInfo.put(HHSConstants.STATUS_ID, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_CONTRACT_CANCELLED));
			loStatusInfo.put(HHSConstants.ADD_DELETE_STATUS, HHSConstants.D);
			loStatusInfo.put(HHSConstants.REUSE_EPIN, HHSConstants.YES_UPPERCASE);
			for (String loContractId : aoContractIdList)
			{
				loStatusInfo.put(HHSConstants.CONTRACT_ID, loContractId);
				DAOUtil.masterDAO(aoMybatisSession, loStatusInfo, HHSConstants.MAPPER_CLASS_AWARDS_MAPPER,
						HHSConstants.UPDATE_AWARD_STATUS, HHSConstants.JAVA_UTIL_MAP);
				LOG_OBJECT.Debug("Award Status sucessfully updated to cancelled for ContractId" + loContractId);
			}
			loUpdateAwardStatusFlag = Boolean.TRUE;
		}
		// handling Application exception
		catch (ApplicationException aoExp)
		{
			setMoState("Error while updating Award Status List:");
			LOG_OBJECT.Error("Error while updating Award Status List:", aoExp);
			throw aoExp;
		}
		// handling exception other than Application exception
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while updating Award Status List:", aoExp);
			setMoState("Error while updating Award Status List:");
			throw new ApplicationException("Exception while updating Award Status List", aoExp);
		}

		return loUpdateAwardStatusFlag;

	}

	/**
	 * The Service updates the related proposals for that provider for that
	 * procurement to "Not Selected"
	 * <ul>
	 * <li>1.Obtain the required information to update the status i.e
	 * ContractId,UserId ,StatusId</li>
	 * <li>2. Trigger query
	 * <b>updateRelatedProposal</b>,<b>updateModifiedFlagEvalResults</b> query</li>
	 * <li>3. Return update Award Status Flag</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession
	 * @param aoStatusInfo Map<String, String>
	 * @return Boolean
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Boolean updateRelatedProposal(SqlSession aoMybatisSession, Map<String, String> aoStatusInfo)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into Related Award Proposal");
		HashMap<String, Object> loHmReqExceProp = new HashMap<String, Object>();
		loHmReqExceProp.put(HHSConstants.STATUS_INFO_MAP_ARG, aoStatusInfo);
		Boolean loUpdateRelatedProposalFlag = Boolean.FALSE;
		try
		{ // setting Proposal Status as Not Selected

			aoStatusInfo.put(HHSConstants.PROPOSAL_IN_DRAFT_STATUS, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_DRAFT));
			aoStatusInfo.put(HHSConstants.UPDATE_PROPOSAL_STATUS_NON_RESPONSIVE, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_NON_RESPONSIVE));

			aoStatusInfo.put(HHSConstants.STATUS_ID, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_NOT_SELECTED));
			aoStatusInfo.put(HHSConstants.MODIFIED_FLAG, HHSConstants.ONE);
			DAOUtil.masterDAO(aoMybatisSession, aoStatusInfo, HHSConstants.MAPPER_CLASS_AWARDS_MAPPER,
					HHSConstants.UPDATED_MODIFIED_FLAG_EVAL_RESULTS, HHSConstants.JAVA_UTIL_MAP);
			// Calling updateRelatedProposal for updating Proposal
			// Status,passing required using Map
			DAOUtil.masterDAO(aoMybatisSession, aoStatusInfo, HHSConstants.MAPPER_CLASS_AWARDS_MAPPER,
					HHSConstants.UPDATED_RELATED_PROPOSAL, HHSConstants.JAVA_UTIL_MAP);

			loUpdateRelatedProposalFlag = Boolean.TRUE;

			setMoState("Updated Related Proposal Status to Not selected for ContractID"
					+ aoStatusInfo.get(HHSConstants.CONTRACT_ID));
		}
		// handling Application exception
		catch (ApplicationException aoExp)
		{
			setMoState("Error while updating Related Proposal Status to Not selected  :");
			aoExp.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error while updating Related Proposal Status to Not selected  :", aoExp);
			throw aoExp;
		}
		// handling exceptions other than Application exception
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while updating Related Proposal Status to Not selected s :", aoExp);
			setMoState("Error while updating Related Proposal Status to Not selected :");
			throw new ApplicationException("Exception while updating Related Proposal Status to Not selected", aoExp);
		}

		return loUpdateRelatedProposalFlag;

	}

	/**
	 * The Service the Award Review Status to "Update in Progress"
	 * <ul>
	 * <li>Obtain the required information to update the status i.e
	 * ContractId,UserId ,StatusId</li>
	 * <li>Trigger query <b>updateAwardReviewStatus query</b></li>
	 * <li>Return update Award Review Status Flag</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession
	 * @param aoStatusInfo Map<String, String>
	 * @return loUpdateAwardReviewStatusFlag - Boolean
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Boolean updateAwardReviewStatus(SqlSession aoMybatisSession, Map<String, String> aoStatusInfo)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into update Award Review Status ");
		HashMap<String, Object> loHmReqExceProp = new HashMap<String, Object>();
		loHmReqExceProp.put(HHSConstants.STATUS_INFO_MAP_ARG, aoStatusInfo);
		Boolean loUpdateAwardReviewStatusFlag = Boolean.FALSE;
		try
		{ // setting Award Status Id as cancelled
			aoStatusInfo.put(HHSConstants.STATUS_ID, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_AWARD_REVIEW_UPDATE_IN_PROGRESS));
			// Calling updateAwardStatus for updating Award Status,passing
			// required using Map
			DAOUtil.masterDAO(aoMybatisSession, aoStatusInfo, HHSConstants.MAPPER_CLASS_AWARDS_MAPPER,
					HHSConstants.UPDATE_AWARD_REVIEW_STATUS, HHSConstants.JAVA_UTIL_MAP);
			loUpdateAwardReviewStatusFlag = Boolean.TRUE;

			setMoState("Award Review Status sucessfully updated to Update in Progress"
					+ aoStatusInfo.get(HHSConstants.CONTRACT_ID));
		}
		// handling Applicaton exception
		catch (ApplicationException aoExp)
		{
			setMoState("Error while updating Award Review Status :");
			aoExp.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error while updating Award Review Status :", aoExp);
			throw aoExp;
		}
		// handling exceptions other than Application exception
		catch (Exception aoExp)
		{
			ApplicationException loAppExp = new ApplicationException("Error while updating Award Review Status :",
					aoExp);
			LOG_OBJECT.Error("Error while updating Award Review Status :", loAppExp);
			setMoState("Error while updating Award  Review Status :");
			throw loAppExp;

		}

		return loUpdateAwardReviewStatusFlag;

	}

	/**
	 * This method fetches Award Details for given procurementId and provider
	 * status
	 * <ul>
	 * <li>Execute query Id <b>awardAndContracts</b>for mapper AwardsMapper</li>
	 * </ul>
	 * @param aoMybatisSession mybatis SqlSession
	 * @param aoAwardMap Map<String, Object>
	 * @return List<AwardBean> loawardAndContractsList returns the award
	 *         details.
	 * @throws ApplicationException If an Application Exception occurs
	 */

	@SuppressWarnings("unchecked")
	public List<AwardBean> actionGetawardAndContractsList(SqlSession aoMybatisSession, Map<String, Object> aoAwardMap)
			throws ApplicationException
	{
		List<AwardBean> loAwardAndContractsList = null;
		try
		{
			// Calling awardAndContracts to get the details and passing through
			// map.
			loAwardAndContractsList = (List<AwardBean>) DAOUtil.masterDAO(aoMybatisSession, aoAwardMap,
					HHSConstants.MAPPER_CLASS_AWARDS_MAPPER, HHSConstants.AWARD_AND_CONTRACTS,
					HHSConstants.JAVA_UTIL_MAP);
			for (AwardBean loAwardBean : loAwardAndContractsList)
			{
				if (loAwardBean.getContractTypeId().equalsIgnoreCase(HHSConstants.FIVE))
				{
					loAwardBean.setIsFinancial(HHSConstants.EMPTY_STRING);
				}
				else if (loAwardBean.getContractTypeId().equalsIgnoreCase(HHSConstants.ONE))
				{
					loAwardBean.setIsFinancial(HHSConstants.TRUE);
				}
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handled over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		// Handling Application Exception
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Error while Fetching award and contracts details:", loAppEx);
			setMoState("Error while Fetching award and contracts details:");
			throw loAppEx;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error while Fetching award and contracts details:", loExp);
			setMoState("Error while Fetching award and contracts details:");
			throw new ApplicationException("Error while Fetching award and contracts details:", loExp);
		}
		return loAwardAndContractsList;
	}

	/**
	 * This method fetches Award Document Task Details for given procurement Id
	 * and evaluation pool mapping Id from task details map
	 * 
	 * <ul>
	 * <li>1. Get the procurementId and evaluation pool mapping Id from task
	 * details map</li>
	 * <li>2. If evaluation pool mapping id is null, get evaluation pool mapping
	 * Id using <b>fetchEvalPoolMappingId</b> from awards mapper</li>
	 * <li>3. Execute select query with id <b>fetchDocumentsForAwardDocTask</b>
	 * from the awards mapper</li>
	 * <li>Get list of Award Document Details and return to controller</li>
	 * </ul>
	 * <ul>
	 * <li>Method updated in R4</li>
	 * </ul>
	 * 
	 * Change: Updated query for competition pool
	 * 
	 * Changed By: Pallavi
	 * 
	 * Change Date: 24 Mar 2014
	 * 
	 * @param aoMybatisSession mybatis SQL session
	 * @param aoTaskMap a task detail map containing procurement and task
	 *            details
	 * @param asWobNumber a string value of wob number
	 * @return loAwardDocumentList - a list of award document details
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public List<ExtendedDocument> fetchDocumentsForAwardDocTask(SqlSession aoMybatisSession,
			HashMap<String, Object> aoTaskMap, String asWobNumber) throws ApplicationException
	{
		List<ExtendedDocument> loAwardDocumentList = null;
		try
		{
			// check for the null value of task detail map
			if (null != aoTaskMap)
			{
				HashMap loProcurementMap = (HashMap) aoTaskMap.get(asWobNumber);
				if (null != loProcurementMap)
				{
					// Get the procurement Id
					String lsProcurementId = (String) loProcurementMap.get(P8Constants.PE_WORKFLOW_PROCUREMENT_ID);
					String lsEvalPoolMappingId = (String) loProcurementMap.get(P8Constants.EVALUATION_POOL_MAPPING_ID);
					if (null == lsEvalPoolMappingId || lsEvalPoolMappingId.equalsIgnoreCase(HHSConstants.EMPTY_STRING))
					{
						lsEvalPoolMappingId = (String) DAOUtil.masterDAO(aoMybatisSession, lsProcurementId,
								HHSConstants.MAPPER_CLASS_AWARDS_MAPPER, HHSConstants.FETCH_EVAL_POOL_MAPPING_ID,
								HHSConstants.JAVA_LANG_STRING);
						loProcurementMap.put(P8Constants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolMappingId);
					}
					HashMap<String, String> loEvalMap = new HashMap<String, String>();
					loEvalMap.put(HHSConstants.PROCUREMENT_ID, lsProcurementId);
					loEvalMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolMappingId);
					// Get the award document list
					loAwardDocumentList = (List<ExtendedDocument>) DAOUtil.masterDAO(aoMybatisSession, loEvalMap,
							HHSConstants.MAPPER_CLASS_AWARDS_MAPPER, HHSConstants.FETCH_DOC_FOR_AWARD_TASK,
							HHSConstants.JAVA_UTIL_HASH_MAP);
					setMoState("Award Documents List fetched successfully for procurement Id:" + lsProcurementId);
				}
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoExp)
		{
			aoExp.setContextData(aoTaskMap);
			LOG_OBJECT.Error("Error while fetching Award Documents List", aoExp);
			setMoState("Error while fetching Award Documents List" + aoTaskMap);
			throw aoExp;
		}
		// handling exception other than Application Exception
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetching Award Documents List", aoExp);
			setMoState("Error while fetching Award Documents List" + aoTaskMap);
			throw new ApplicationException("Error while fetching Award Documents List", aoExp);
		}
		return loAwardDocumentList;
	}

	/**
	 * This method fetches Apt Progress View details based on the specified
	 * e-pin
	 * <ul>
	 * <li>Execute the query <b>viewAptProgress</b> from <code>awards</code>
	 * mapper.</li>
	 * <li>Returns all the required details for the specified e-pin</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoViewProgressMap Map<String, String> map containing award e-pin
	 * @return loViewAptDetails - AwardBean a bean containing Apt Progress View
	 *         details
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public AwardBean aptProgressView(SqlSession aoMybatisSession, Map<String, String> aoViewProgressMap)
			throws ApplicationException
	{
		HashMap<String, Object> loHmReqExceProp = new HashMap<String, Object>();
		loHmReqExceProp.put(HHSConstants.AO_INPUT_PARAMS, aoViewProgressMap);
		AwardBean loViewAptDetails = null;
		try
		{
			// Calling viewAptProgress to get the details and passing through
			// map.
			loViewAptDetails = (AwardBean) DAOUtil
					.masterDAO(aoMybatisSession, aoViewProgressMap, HHSConstants.MAPPER_CLASS_AWARDS_MAPPER,
							HHSConstants.VIEW_APT_PROGRESS, HHSConstants.JAVA_UTIL_MAP);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handled over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		// Handling Application Exception
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(loHmReqExceProp);
			setMoState("Error while Fetching view APT progress details:");
			LOG_OBJECT.Error("Error while Fetching view APT progress details:", aoAppEx);
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			ApplicationException loAppExp = new ApplicationException("Error while Fetching view APT progress details:",
					aoExp);
			LOG_OBJECT.Error("Error while Fetching view APT progress details:", loAppExp);
			setMoState("Error while Fetching view APT progress details:");
			throw loAppExp;
		}

		return loViewAptDetails;
	}

	/**
	 * This method deletes award task document details based on document Id
	 * 
	 * <ul>
	 * <li>Get the document Id from input</li>
	 * <li>Execute query with id <b>removeAwardTaskDocs</b> from awards mapper</li>
	 * <li>Return status flag to controller</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession mybatis SQL session
	 * @param asDocumentId a string value of document Id
	 * @param asDocSeqID a string value
	 * @return loRemoveStatus - a boolean value indicating document remove
	 *         status
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Boolean removeAwardTaskDocs(SqlSession aoMybatisSession, String asDocumentId, String asDocSeqID)
			throws ApplicationException
	{
		Boolean loRemoveStatus = false;
		try
		{
			HashMap<String, String> loAwardMap = new HashMap<String, String>();
			loAwardMap.put(HHSConstants.DOC_ID, asDocumentId);
			loAwardMap.put(HHSConstants.DOC_SEQ_ID, asDocSeqID);
			DAOUtil.masterDAO(aoMybatisSession, loAwardMap, HHSConstants.MAPPER_CLASS_AWARDS_MAPPER,
					HHSConstants.REMOVE_AWARD_TASK_DOCS, HHSConstants.JAVA_UTIL_HASH_MAP);
			loRemoveStatus = true;
			setMoState("Award Documents deleted successfully for document Id:" + asDocumentId);
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured while removing award document by executing removeAwardTaskDocs()",
					aoAppEx);
			setMoState("Transaction Failed:: removeAwardTaskDocs method - failed to remove award document for documentId="
					+ asDocumentId + " \n");
			throw aoAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception occured while removing award document by executing removeAwardTaskDocs()",
					aoExp);
			setMoState("Transaction Failed:: removeAwardTaskDocs method - failed to remove award document for documentId="
					+ asDocumentId + " \n");
			throw new ApplicationException(
					"Exception occured while removing award document by executing removeAwardTaskDocs()", aoExp);
		}
		return loRemoveStatus;
	}

	/**
	 * This method saves award document configuration details from Award
	 * Configuration
	 * 
	 * <ul>
	 * <li>Get the proposal details bean from input</li>
	 * <li>Get the Required Document list from input proposal details bean</li>
	 * <li>Iterate through the list and check if document type exists for that
	 * object</li>
	 * <li>If document type exists, set mandatory parameters in bean</li>
	 * <li>Execute query with id <b>saveAwardDocumentTypes</b> from awards
	 * mapper to save document configuration details</li>
	 * <li>Repeat above steps for Optional Document list from input proposal
	 * details bean</li>
	 * <li>Return status flag to controller</li>
	 * </ul>
	 * <ul>
	 * <li>Method updated in R4</li>
	 * </ul>
	 * Change: Added evaluation pool mapping id to save details for particular
	 * competition pool of a procurement
	 * 
	 * Changed By: Pallavi
	 * 
	 * Change Date: 24 Mar 2014
	 * 
	 * @param aoMybatisSession mybatis SQL session
	 * @param aoProposalDetailsBean a bean containing required and optional
	 *            document type list
	 * @return loSaveStatus - a boolean value indicating save status
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Boolean saveAwardDocumentConfig(SqlSession aoMybatisSession, ProposalDetailsBean aoProposalDetailsBean)
			throws ApplicationException
	{
		Boolean loSaveStatus = false;
		try
		{
			if (null != aoProposalDetailsBean)
			{
				Integer loCounter = HHSConstants.INT_ONE;
				// get the required document list from input
				// Iterate through the list and process each document bean
				// object
				for (ExtendedDocument loDocTypeBean : aoProposalDetailsBean.getRequiredDocumentList())
				{
					loDocTypeBean.setCreatedBy(aoProposalDetailsBean.getCreatedBy());
					loDocTypeBean.setLastModifiedById(aoProposalDetailsBean.getModifiedBy());
					loDocTypeBean.setProcurementId(aoProposalDetailsBean.getProcurementId());
					loDocTypeBean.setEvaluationPoolMappingId(aoProposalDetailsBean.getEvaluationPoolMappingId());
					// set required flag 1 for required document types
					loDocTypeBean.setRequiredFlag(HHSConstants.ONE);
					if (loDocTypeBean.getDocumentType() != null
							&& !loDocTypeBean.getDocumentType().equals(HHSConstants.EMPTY_STRING))
					{
						loDocTypeBean.setDocumentSeqNumber(loCounter.toString());
						DAOUtil.masterDAO(aoMybatisSession, loDocTypeBean, HHSConstants.MAPPER_CLASS_AWARDS_MAPPER,
								HHSConstants.SAVE_AWARD_DOC_TYPE, HHSConstants.COM_NYC_HHS_MODEL_EXTENDED_DOC);
						loCounter++;
					}
				}
				loCounter = HHSConstants.INT_ONE;
				// get the optional document list from input
				// Iterate through the list and process each document bean
				// object
				for (ExtendedDocument loDocTypeBean : aoProposalDetailsBean.getOptionalDocumentList())
				{
					loDocTypeBean.setCreatedBy(aoProposalDetailsBean.getCreatedBy());
					loDocTypeBean.setLastModifiedById(aoProposalDetailsBean.getModifiedBy());
					loDocTypeBean.setProcurementId(aoProposalDetailsBean.getProcurementId());
					loDocTypeBean.setEvaluationPoolMappingId(aoProposalDetailsBean.getEvaluationPoolMappingId());
					// set required flag 0 for optional document types
					loDocTypeBean.setRequiredFlag(HHSConstants.STRING_ZERO);
					if (loDocTypeBean.getDocumentType() != null
							&& !loDocTypeBean.getDocumentType().equals(HHSConstants.EMPTY_STRING))
					{

						loDocTypeBean.setDocumentSeqNumber(loCounter.toString());
						DAOUtil.masterDAO(aoMybatisSession, loDocTypeBean, HHSConstants.MAPPER_CLASS_AWARDS_MAPPER,
								HHSConstants.SAVE_AWARD_DOC_TYPE, HHSConstants.COM_NYC_HHS_MODEL_EXTENDED_DOC);
						loCounter++;
					}
				}
			}
			loSaveStatus = true;
			if (null != aoProposalDetailsBean && null != aoProposalDetailsBean.getEvaluationPoolMappingId())
			{
				setMoState("Award Document Type Details inserted successfully for evaluation pool mapping Id:"
						+ aoProposalDetailsBean.getEvaluationPoolMappingId());
			}
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured while saving award configuration data", aoAppEx);
			setMoState("Transaction Failed:: saveAwardDocumentTypes method - failed to save award configuration data"
					+ " \n");
			throw aoAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception occured while saving award configuration data", aoExp);
			setMoState("Transaction Failed:: saveAwardDocumentTypes method - failed to save award configuration data"
					+ " \n");
			throw new ApplicationException("Exception occured while saving award configuration data", aoExp);
		}
		return loSaveStatus;
	}

	/**
	 * This method inserts award task document details based on input document
	 * properties map
	 * 
	 * <ul>
	 * <li>Get document properties map from input</li>
	 * <li>Execute query with id <b>insertAwardTaskDocDetails</b> from awards
	 * mapper</li>
	 * <li>Return status flag to controller</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession mybatis SQL session
	 * @param aoDocPropsMap a document properties map
	 * @return loInsertStatus - a boolean value indicating insert status
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Boolean insertAwardTaskDocDetails(SqlSession aoMybatisSession, HashMap<String, String> aoDocPropsMap)
			throws ApplicationException
	{
		Boolean loInsertStatus = false;
		try
		{
			DAOUtil.masterDAO(aoMybatisSession, aoDocPropsMap, HHSConstants.MAPPER_CLASS_AWARDS_MAPPER,
					HHSConstants.INSERT_AWARD_TASK_DOC_DETAILS, HHSConstants.JAVA_UTIL_HASH_MAP);
			loInsertStatus = true;
			setMoState("award Document Details inserted successfully for input:" + aoDocPropsMap.toString());
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while inserting document details in insertAwardTaskDocDetails()",
					loAppEx);
			setMoState("Transaction Failed:: insertAwardTaskDocDetails method - failed to insert document details"
					+ " \n");
			throw loAppEx;
		}
		// handling exception other than Application Exception
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while inserting document details in insertAwardTaskDocDetails()",
					loAppEx);
			setMoState("Transaction Failed:: insertAwardTaskDocDetails method - failed to insert document details"
					+ " \n");
			throw new ApplicationException(
					"Exception occured while inserting document details in insertAwardTaskDocDetails()", loAppEx);
		}
		return loInsertStatus;
	}

	/**
	 * This method inserts agency award task document details based on input
	 * document properties map
	 * 
	 * Added for Enhancement #6429 for Release 3.4.0
	 * <ul>
	 * <li>Get document properties map from input</li>
	 * <li>Execute query with id <b>insertAgencyAwardDocsDetails</b> from awards
	 * mapper</li>
	 * <li>Return status flag to controller</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession mybatis SQL session
	 * @param aoDocPropsMap a document properties map
	 * @return loInsertStatus - a boolean value indicating insert status
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Boolean insertAgencyAwardDocsDetails(SqlSession aoMybatisSession, HashMap<String, String> aoDocPropsMap)
			throws ApplicationException
	{
		Boolean loInsertStatus = false;
		try
		{
			DAOUtil.masterDAO(aoMybatisSession, aoDocPropsMap, HHSConstants.MAPPER_CLASS_AWARDS_MAPPER,
					HHSConstants.INSERT_AGENCY_AWARD_DOCS_DETAILS, HHSConstants.JAVA_UTIL_HASH_MAP);
			loInsertStatus = true;
			setMoState("agency award Document Details inserted successfully for input:" + aoDocPropsMap.toString());
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while inserting document details in insertAgencyAwardDocsDetails()",
					loAppEx);
			setMoState("Transaction Failed:: insertAgencyAwardDocsDetails method - failed to insert document details"
					+ " \n");
			throw loAppEx;
		}
		// handling exception other than Application Exception
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while inserting document details in insertAgencyAwardDocsDetails()",
					loAppEx);
			setMoState("Transaction Failed:: insertAgencyAwardDocsDetails method - failed to insert document details"
					+ " \n");
			throw new ApplicationException(
					"Exception occured while inserting document details in insertAgencyAwardDocsDetails()", loAppEx);
		}
		return loInsertStatus;
	}

	/**
	 * This method removes agency award task document details based on input
	 * document properties map
	 * 
	 * Added for Enhancement #6429 for Release 3.4.0
	 * <ul>
	 * <li>Get document properties map from input</li>
	 * <li>Execute query with id <b>removeAgencyAwardDocsDetails</b> from awards
	 * mapper</li>
	 * <li>Return status flag to controller</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession mybatis SQL session
	 * @param aoDocPropsMap a document properties map
	 * @return loInsertStatus - a boolean value indicating insert status
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Boolean removeAgencyAwardDocs(SqlSession aoMybatisSession, Map<String, String> aoDocPropsMap)
			throws ApplicationException
	{
		Boolean loRemoveStatus = false;
		try
		{
			DAOUtil.masterDAO(aoMybatisSession, aoDocPropsMap, HHSConstants.MAPPER_CLASS_AWARDS_MAPPER,
					HHSConstants.REMOVE_AGENCY_AWARD_DOCS, HHSConstants.JAVA_UTIL_MAP);
			loRemoveStatus = true;
			setMoState("Agency award Document Details removed successfully for input:" + aoDocPropsMap.toString());
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while removing document details in removeAgencyAwardDocs()", loAppEx);
			setMoState("Transaction Failed:: removeAgencyAwardDocs method - failed to remove document details" + " \n");
			throw loAppEx;
		}
		// handling exception other than Application Exception
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while removing document details in removeAgencyAwardDocs()", loAppEx);
			setMoState("Transaction Failed:: removeAgencyAwardDocs method - failed to remove document details" + " \n");
			throw new ApplicationException(
					"Exception occured while removing document details in removeAgencyAwardDocs()", loAppEx);
		}
		return loRemoveStatus;
	}

	/**
	 * This method fetches Awards count
	 * <ul>
	 * <li>Execute query<b>awardAndContractsCount</b></li>
	 * </ul>
	 * @param aoMybatisSession mybatis SQL session
	 * @param loAwardMap map containing
	 * @return loAwardCount - Integer
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Integer awardAndContractsCount(SqlSession aoMybatisSession, Map<String, Object> loAwardMap)
			throws ApplicationException
	{
		Integer loAwardCount = HHSConstants.INT_ZERO;
		try
		{
			loAwardCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loAwardMap,
					HHSConstants.MAPPER_CLASS_AWARDS_MAPPER, HHSConstants.AWARD_CONTRACT_COUNT,
					HHSConstants.JAVA_UTIL_MAP);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Error while Fetching award and contracts records count:", loAppEx);
			setMoState("Error while Fetching award and contracts records count:");
			throw loAppEx;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error while Fetching award and contracts records count:", loExp);
			setMoState("Error while Fetching award and contracts records count:");
			throw new ApplicationException("Error while Fetching award and contracts records count:", loExp);
		}
		return loAwardCount;
	}

	/**
	 * This method will fetch Award Epin Details from <b>REF_APT_PIN</b> Table
	 * <ul>
	 * <li>Get the Procurement Id from the Argument</li>
	 * <li>Execute query <b>fetchAwardEPinDetails</b> from
	 * <b>EvaluationMapper</b>mapper</li>
	 * <li>return the list of bean to the channel object</li>
	 * </ul>
	 * @param aoMybatisSession Sql Session Object
	 * @param asProcurementId Procurement Id
	 * @return loAwardBeanList - List of beans with all award details
	 * @throws ApplicationException Application Exception
	 */
	@SuppressWarnings("unchecked")
	public List<AwardBean> fetchAwardEPinDetails(SqlSession aoMybatisSession, String asProcurementId)
			throws ApplicationException
	{
		List<AwardBean> loAwardBeanList = null;
		HashMap<String, Object> loHmReqExceProp = new HashMap<String, Object>();
		loHmReqExceProp.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		LOG_OBJECT.Debug("Entered into fetching Award Epin Details" + loHmReqExceProp.toString());
		try
		{
			loAwardBeanList = (List<AwardBean>) DAOUtil.masterDAO(aoMybatisSession, asProcurementId,
					HHSConstants.MAPPER_CLASS_AWARDS_MAPPER, HHSConstants.FETCH_AWARD_EPIN_DETAILS,
					HHSConstants.JAVA_LANG_STRING);
			setMoState("Award Epin details fetched successfully");
		}
		/*
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoExp)
		{
			setMoState("Error while fetching Award Epin Details");
			aoExp.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error while fetching Award Epin Details", aoExp);
			throw aoExp;
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetching Award Epin Details", aoExp);
			setMoState("Error while fetching Award Epin Details");
			throw new ApplicationException("Error while fetching Award Epin Details", aoExp);

		}
		return loAwardBeanList;
	}

	/**
	 * This method will fetch Provider and amount Details from DB
	 * <ul>
	 * <li>Get the Contract Id from the Argument</li>
	 * <li>Execute query <b>fetchAmountProviderDetails</b> from
	 * <b>AwardsMapper</b></li>
	 * <li>return the bean to the channel object</li>
	 * </ul>
	 * <ul>
	 * <li>Method updated in R4</li>
	 * </ul>
	 * Change: updated query for open ended RFP
	 * 
	 * Changed By: Varun Change Date: 25 Mar 2014
	 * 
	 * @param aoMybatisSession - mybatis Sql Session
	 * @param asContractId - Contract Id
	 * @return loAmountProviderDetails - AwardBean bean containing provider
	 *         amount details
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public AwardBean fetchAmountProviderDetails(SqlSession aoMybatisSession, String asContractId)
			throws ApplicationException
	{
		AwardBean loAmountProviderDetails = null;
		HashMap<String, Object> loHmReqExceProp = new HashMap<String, Object>();
		loHmReqExceProp.put(HHSConstants.CONTRACT_ID_KEY, asContractId);
		LOG_OBJECT.Debug("Entered into fetching Provider and amount Details" + loHmReqExceProp.toString());
		try
		{
			loAmountProviderDetails = (AwardBean) DAOUtil.masterDAO(aoMybatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_AWARDS_MAPPER, HHSConstants.FETCH_AMOUNT_PROVIDER_DETAILS,
					HHSConstants.JAVA_LANG_STRING);
			setMoState("Provider and amount Details List fetched successfully");
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			setMoState("Error while fetching Provider and amount Details");
			aoExp.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error while fetching Provider and amount Details", aoExp);
			throw aoExp;
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error while fetching Provider and amount Details", aoEx);
			setMoState("Error while fetching Provider and amount Details");
			throw new ApplicationException("Error while fetching Provider and amount Details", aoEx);

		}
		return loAmountProviderDetails;
	}

	/**
	 * This method will update the assigned Award E pin for the Award
	 * <ul>
	 * <li>Get the parameters set in Channel Object</li>
	 * <li>Execute query <b>assignAwardEPin</b> of Evaluation mapper with
	 * required parameters</li>
	 * <li>Return the number of rows updated on successful updation of the table
	 * </li>
	 * </ul>
	 * @param aoMybatisSession sql session object
	 * @param aoAwardEpinMap parameter map object
	 * @return loAwardEPinStatus status as boolean
	 * @throws ApplicationException Application Exception
	 */
	public Boolean assignAwardEpin(SqlSession aoMybatisSession, Map<String, String> aoAwardEpinMap)
			throws ApplicationException
	{
		Boolean loAwardEPinStatus = false;
		// preparing context data map for exception handling.
		HashMap<String, Object> loHmReqExceProp = new HashMap<String, Object>();
		loHmReqExceProp.put(HHSConstants.LO_AWARD_EPIN_MAP, aoAwardEpinMap);
		LOG_OBJECT.Debug("Entered into fetching Award Epin Details" + loHmReqExceProp.toString());
		try
		{
			// calling DAO layer to execute the transaction assignAwardEpin.
			DAOUtil.masterDAO(aoMybatisSession, aoAwardEpinMap, HHSConstants.MAPPER_CLASS_AWARDS_MAPPER,
					HHSConstants.ASSIGN_AWARD_EPIN, HHSConstants.JAVA_UTIL_MAP);
			// update status to not generated for pdf details batch.
			DAOUtil.masterDAO(aoMybatisSession, aoAwardEpinMap, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSConstants.UPDATE_PDF_STATUS_NOT_STARTED, HHSConstants.JAVA_UTIL_HASH_MAP);
			loAwardEPinStatus = true;
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			setMoState("Error while assigning Award Epin");
			// setting the context data.
			aoExp.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error while assigning Award Epin", aoExp);
			throw aoExp;
		}
		// handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while assigning Award Epin", aoExp);
			setMoState("Error while assigning Award Epin");
			throw new ApplicationException("Error while assigning Award Epin", aoExp);
		}
		return loAwardEPinStatus;
	}

	/**
	 * This method will update award approval date and award review status from
	 * award task
	 * 
	 * <ul>
	 * <li>Get the award hashmap from input params</li>
	 * <li>Check if Award workflow is first launched or not</li>
	 * <li>Execute query with id "updateAwardDetailsFromTask" from awards mapper
	 * </li>
	 * <li>Return updated status to controller</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession sql session object
	 * @param aoAwardMap a hashmap containing award details
	 * @return loUpdateStatus - a boolean value of award update status
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Boolean updateAwardDetailsFromTask(SqlSession aoMybatisSession, HashMap<String, String> aoAwardMap)
			throws ApplicationException
	{
		Boolean loUpdateStatus = false;
		try
		{
			if (null != aoAwardMap)
			{
				String lsAwardStatusId = aoAwardMap.get(ApplicationConstants.AWARD_STATUS_ID);
				if (null != lsAwardStatusId
						&& !StringUtils.isEmpty(lsAwardStatusId)
						&& !lsAwardStatusId.equals(PropertyLoader.getProperty(
								ApplicationConstants.PROPERTIES_STATUS_CONSTANT,
								ApplicationConstants.AWARD_REVIEW_RETURNED)))
				{
					aoAwardMap.put(ApplicationConstants.INCLUDE_DATE, ApplicationConstants.TRUE);
				}
				DAOUtil.masterDAO(aoMybatisSession, aoAwardMap, HHSConstants.MAPPER_CLASS_AWARDS_MAPPER,
						HHSConstants.UPDATE_AWARD_DETAILS_FROM_TASK, HHSConstants.JAVA_UTIL_HASH_MAP);
				loUpdateStatus = true;
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException loExp)
		{
			setMoState("Error while updating award details");
			loExp.setContextData(aoAwardMap);
			LOG_OBJECT.Error("Error while updating award details", loExp);
			throw loExp;
		}
		// handling exception other than Application Exception.
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Error while updating award details", loEx);
			setMoState("Error while updating award details");
			throw new ApplicationException("Error while updating award details", loEx);
		}
		return loUpdateStatus;
	}

	// Commented for enhancement 6448 for Release 3.8.0
	/*	*//**
	 * This method will fetch no of contracts which are not canceled,
	 * closed or registered and return true if the no of contracts fetched is
	 * greater than 0.
	 * <ul>
	 * <li>Get the Procurement Id from the Argument</li>
	 * <li>Execute query <b>ctNotCancelledClosedRegistered</b> from
	 * <b>AwardsMapper</b> mapper</li>
	 * <li>The query return the no of contracts</li>
	 * <li>If no of contracts is greater than 0 the method returns true else
	 * false.</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession
	 * @param asProcurementId String
	 * @return loCount - Boolean
	 * @throws ApplicationException
	 */

	/**
	 * Modifed by - Tanuj Mudgal
	 * 
	 * This Method is modified to fix 5523 as part of build 2.6.0 A new
	 * parameter "aoIsRequiredDoc" is been added to check if this is a required
	 * document.
	 * 
	 * This method will check whether all the Award documents have been uploaded
	 * or not
	 * <ul>
	 * <li>Get the Award document count from the db by Executing query
	 * <b>checkIfAllReqAwardDocsUploaded</b> from <b>AwardsMapper</b></li>
	 * <li>The query return the no of Award doc</li>
	 * <li>If no of award document is greater than 0 the method returns false
	 * else true.</li>
	 * </ul>
	 */
	/**
	 * @param aoMybatisSession session
	 * @param aoInsertDocCount document count
	 * @param asAwardId award id
	 * @param asReplacingDocId replacing document ID
	 * @return loDocumentCompleteStatus - document complete status - boolean
	 * @throws ApplicationException if exception occurs
	 */
	public Boolean checkIfAllReqAwardDocsUploaded(SqlSession aoMybatisSession, Integer aoInsertDocCount,
			String asAwardId, String asReplacingDocId, String asOrgId, Boolean aoIsRequiredDoc)
			throws ApplicationException
	{
		Boolean loDocumentCompleteStatus = Boolean.FALSE;
		try
		{
			if (null != aoInsertDocCount && aoInsertDocCount > 0)
			{
				// NT219 fix
				HashMap loHashMap = new HashMap();
				loHashMap.put(HHSConstants.ORGA_ID, asOrgId);
				loHashMap.put(HHSConstants.AWARD_ID, asAwardId);
				Integer loAwardDocumentCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loHashMap,
						HHSConstants.MAPPER_CLASS_AWARDS_MAPPER, HHSConstants.CHECK_IF_ALL_REQ_AWARD_DOCS_UPLOADED,
						HHSConstants.JAVA_UTIL_HASH_MAP);
				if (null != loAwardDocumentCount
						&& loAwardDocumentCount.equals(HHSConstants.INT_ZERO)
						&& (null == asReplacingDocId || asReplacingDocId.isEmpty() || HHSConstants.NULL
								.equals(asReplacingDocId)))
				{
					loDocumentCompleteStatus = Boolean.TRUE;
				}
			}
			// 5523 fix starts
			if (null != aoIsRequiredDoc && !aoIsRequiredDoc)
			{
				loDocumentCompleteStatus = Boolean.FALSE;
			}
			// 5523 fix ends
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error while fetching no of contracts which are not cancelled, closed or registered",
					aoExp);
			setMoState("Error while fetching no of contracts which are not cancelled, closed or registered");
			throw aoExp;
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception aoEx)
		{
			LOG_OBJECT
					.Error("Error while fetching no of contracts which are not cancelled, closed or registered", aoEx);
			setMoState("Error while fetching no of contracts which are not cancelled, closed or registered");
			throw new ApplicationException(
					"Error while fetching no of contracts which are not cancelled, closed or registered", aoEx);

		}
		return loDocumentCompleteStatus;
	}

	/**
	 * This method will get the agency id for the particular award id.
	 * <ul>
	 * <li>Get the agency id from the db by Executing query
	 * <b>getAgencyIdForAwardId</b> from <b>AwardsMapper</b></li>
	 * <li>The query return Agency id for the particular id we passed as
	 * parameter</li>
	 * </ul>
	 * <ul>
	 * <li>Method updated in R4</li>
	 * </ul>
	 * @param aoMybatisSession session
	 * @param aoNotificationMap Map
	 * @param asAwardId award id
	 * @param aoAwardDocFlag award doc flag
	 * @return aoNotificationMap - notification map
	 * @throws ApplicationException if exception occurs
	 */
	public HashMap<String, Object> getAgencyIdForAwardId(SqlSession aoMybatisSession,
			HashMap<String, Object> aoNotificationMap, String asAwardId, Boolean aoAwardDocFlag)
			throws ApplicationException
	{
		try
		{
			if (aoAwardDocFlag && null != aoNotificationMap)
			{
				String lsAgencyId = (String) DAOUtil.masterDAO(aoMybatisSession, asAwardId,
						HHSConstants.MAPPER_CLASS_AWARDS_MAPPER, HHSConstants.GET_AGENCY_ID_FOR_AWARD_ID,
						HHSConstants.JAVA_LANG_STRING);
				List<String> loAgencyIdList = new ArrayList<String>();
				loAgencyIdList.add(lsAgencyId);
				List<String> loNotificationList = (List<String>) aoNotificationMap
						.get(HHSConstants.NOTIFICATION_ALERT_ID);
				for (String loNotificationId : loNotificationList)
				{
					((NotificationDataBean) aoNotificationMap.get(loNotificationId)).setAgencyList(loAgencyIdList);

				}
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error while fetching no of contracts which are not cancelled, closed or registered",
					aoExp);
			setMoState("Error while fetching no of contracts which are not cancelled, closed or registered");
			throw aoExp;
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception aoEx)
		{
			LOG_OBJECT
					.Error("Error while fetching no of contracts which are not cancelled, closed or registered", aoEx);
			setMoState("Error while fetching no of contracts which are not cancelled, closed or registered");
			throw new ApplicationException(
					"Error while fetching no of contracts which are not cancelled, closed or registered", aoEx);
		}
		return aoNotificationMap;
	}

	/**
	 * This method is used to get the contract count against the procurement id
	 * <ul>
	 * <li>Execute query <b> getContractRegistered </b></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession ibatis session
	 * @param asProcurementId procurement id
	 * @return lbRegisteredContract - count as Integer
	 * @throws ApplicationException
	 */
	public Boolean getContractRegistered(SqlSession aoMybatisSession, final String asProcurementId)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into getContractRegistered method");
		HashMap<String, Object> loHmReqExceProp = new HashMap<String, Object>();
		loHmReqExceProp.put(HHSConstants.STATUS_INFO_MAP_ARG, asProcurementId);
		Integer loContractCount = 0;
		Boolean lbRegisteredContract = Boolean.FALSE;
		try
		{
			loContractCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, asProcurementId,
					HHSConstants.MAPPER_CLASS_AWARDS_MAPPER, HHSConstants.GET_CONTRACT_REGISTERED,
					HHSConstants.JAVA_LANG_STRING);
			if (loContractCount > 0)
			{
				lbRegisteredContract = Boolean.TRUE;
			}
		}
		// handling Application exception
		catch (ApplicationException aoExp)
		{
			setMoState("Error while getContractRegistered method in award service  :");
			aoExp.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error while getContractRegistered method  :", aoExp);
			throw aoExp;
		}
		// handling exceptions other than Application exception
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while getContractRegistered method in award service :", aoExp);
			setMoState("Error while getContractRegistered method in award service :");
			throw new ApplicationException(" Error while getContractRegistered method in award service ", aoExp);
		}
		return lbRegisteredContract;
	}

	/**
	 * The Service updates the contract budget status.
	 * <ul>
	 * <li>1.Obtain the required information to update the status i.e contractId
	 * and userId</li>
	 * <li>2. Trigger query <b>updateContractBudgetStatus</b></li>
	 * <li>3. Return update contract budget status Flag</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession
	 * @param aoStatusInfo HashMap<String, String>
	 * @return loUpdateFlag - Boolean
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Boolean updateContractBudgetStatus(SqlSession aoMybatisSession, HashMap<String, String> aoStatusInfo)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into update Contract Budget Status");
		HashMap<String, Object> loHmReqExceProp = new HashMap<String, Object>();
		loHmReqExceProp.put(HHSConstants.STATUS_INFO_MAP_ARG, aoStatusInfo);
		Boolean loUpdateFlag = Boolean.FALSE;
		try
		{
			// Calling updateContractBudgetStatus for updating Provider Status
			// Status,passing required using Map
			Integer loUpdatecount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoStatusInfo,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.UPDATE_CONTRACT_BUDGET_STATUS,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			if (loUpdatecount > 0)
			{
				loUpdateFlag = true;
			}
		}
		// handling Application exception
		catch (ApplicationException aoExp)
		{
			setMoState("Error while updating Contract Budget  :");
			aoExp.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error while updating Contract Budget  :", aoExp);
			throw aoExp;
		}
		// handling exceptions other than Application exception
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while updating Contract Budget :", aoExp);
			setMoState("Error while updating Contract Budget :");
			throw new ApplicationException("Exception while updatingContract Budget", aoExp);
		}

		return loUpdateFlag;

	}

	/**
	 * Made for enhancement 6574 for Release 3.10.0
	 * @param aoMybatisSession
	 * @param aoStatusInfo
	 * @param aoContractIdList
	 * @return
	 * @throws ApplicationException
	 */
	public Boolean updateContractBudgetStatusList(SqlSession aoMybatisSession, HashMap<String, String> aoStatusInfo,
			List<String> aoContractIdList) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into updateContractBudgetStatusList");
		HashMap<String, Object> loHmReqExceProp = new HashMap<String, Object>();
		loHmReqExceProp.put(HHSConstants.STATUS_INFO_MAP_ARG, aoStatusInfo);
		Boolean loUpdateFlag = Boolean.FALSE;
		try
		{
			// Calling updateContractBudgetStatus for updating Provider Status
			// Status,passing required using Map
			if (null != aoContractIdList && !aoContractIdList.isEmpty())
			{
				for (String loContractId : aoContractIdList)
				{
					LOG_OBJECT.Debug("Entered into updateContractBudgetStatusList" + loContractId);
					aoStatusInfo.put(HHSConstants.CONTRACT_ID1, loContractId);
					Integer loUpdatecount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoStatusInfo,
							HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.UPDATE_CONTRACT_BUDGET_STATUS,
							HHSConstants.JAVA_UTIL_HASH_MAP);
				}
				loUpdateFlag = true;
			}
		}
		// handling Application exception
		catch (ApplicationException aoExp)
		{
			setMoState("Error while updating Contract Budget  :");
			aoExp.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error while updating Contract Budget  :", aoExp);
			throw aoExp;
		}
		// handling exceptions other than Application exception
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while updating Contract Budget :", aoExp);
			setMoState("Error while updating Contract Budget :");
			throw new ApplicationException("Exception while updatingContract Budget", aoExp);
		}

		return loUpdateFlag;

	}

	/**
	 * This method is used to fetch the total amount awarded for all selected
	 * proposals for a particular competition pool of a procurement
	 * 
	 * <ul>
	 * <li>1. Add input parameters to map</li>
	 * <li>2. Execute query with query id
	 * <b>fetchAwardAmountForSelectedProposals</b> from <b>AwardsMapper</b> to
	 * fetch award amount for a given evaluation pool mapping id</li>
	 * </ul>
	 * <ul>
	 * <li>Method updated in R4</li>
	 * </ul>
	 * 
	 * Change: fetched Award Amount for input evaluation pool mapping Id
	 * 
	 * Changed By: Pallavi Change Date 12 Mar 2014
	 * 
	 * @param aoMybatisSession valid sql session object
	 * @param asEvaluationPoolMappingId Evaluation Pool Mapping Id
	 * @param aoHmReqProposMap required parameter properties map
	 * @return aoHmReqProposMap - updated required parameter map with award
	 *         amount property
	 * @throws ApplicationException when any exception occurs
	 */
	public HashMap<String, Object> fetchAwardAmountForSelectedProposals(SqlSession aoMybatisSession,
			String asEvaluationPoolMappingId, HashMap<String, Object> aoHmReqProposMap) throws ApplicationException
	{
		AcceptProposalTaskBean loProcurementDetailsBean = null;
		Map<String, Object> loDataMap = new HashMap<String, Object>();
		loDataMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
		try
		{
			loDataMap.put(HHSConstants.STATUS_PROPOSAL_SELECTED, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_SELECTED));
			loProcurementDetailsBean = (AcceptProposalTaskBean) DAOUtil.masterDAO(aoMybatisSession, loDataMap,
					HHSConstants.MAPPER_CLASS_AWARDS_MAPPER, HHSConstants.FETCH_AWARD_AMOUNT_FOR_SELECTED_PROPOSALS,
					HHSConstants.JAVA_UTIL_MAP);
			if (null != loProcurementDetailsBean)
			{
				aoHmReqProposMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_AWARD_AMOUNT,
						loProcurementDetailsBean.getAwardAmount());
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			setMoState("error while fetching Award Amount for selected proposals");
			aoExp.setContextData(loDataMap);
			LOG_OBJECT.Error("error while fetching Award Amount for selected proposals", aoExp);
			LOG_OBJECT.Error("error while fetching Award Amount for selected proposals", aoExp);
			LOG_OBJECT.Error("error while fetching Award Amount for selected proposals", aoExp);
			throw aoExp;
		}
		// handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("error while fetching Award Amount for selected proposals", aoExp);
			setMoState("error while fetching Award Amount for selected proposals");
			throw new ApplicationException("error while fetching Award Amount for selected proposals", aoExp);
		}
		return aoHmReqProposMap;
	}

	/**
	 * This method fetches organization legal name for a given contract Id
	 * 
	 * <ul>
	 * <li>1. Add input parameters to map</li>
	 * <li>2. Fetches organization legal name for input contract id using
	 * <b>fetchOrgNameFromContractId</b> from awards mapper</li>
	 * </ul>
	 * <ul<li>Method in R4</li></ul>>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asContractId - Contract Id
	 * @return organization name - string
	 * @throws ApplicationException If an Application Exception occurs Updated
	 * 
	 */
	public String fetchOrgNameFromContractId(SqlSession aoMybatisSession, String asContractId)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into update Contract Budget Status");
		HashMap<String, Object> loHmReqExceProp = new HashMap<String, Object>();
		loHmReqExceProp.put(HHSConstants.CONTRACT_ID, asContractId);
		String lsOrgName = null;
		try
		{
			lsOrgName = (String) DAOUtil.masterDAO(aoMybatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_AWARDS_MAPPER, HHSConstants.FETCH_ORG_NAME_FROM_CONTRACT_ID,
					HHSConstants.JAVA_LANG_STRING);
			setMoState("Successfully fetched organization legal name for contract Id:" + asContractId);
		}
		// handling Application exception
		catch (ApplicationException aoExp)
		{
			setMoState("Error while fetching organization legal name for contract Id:" + asContractId);
			aoExp.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error while fetching organization legal name for contract Id:" + asContractId, aoExp);
			throw aoExp;
		}
		// handling exceptions other than Application exception
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetching organization legal name for contract Id:" + asContractId, aoExp);
			setMoState("Error while fetching organization legal name for contract Id:" + asContractId);
			throw new ApplicationException("Exception while fetching organization legal name for contract Id:"
					+ asContractId, aoExp);
		}
		return lsOrgName;
	}

	/**
	 * This method fetches organization legal name and org id for a given
	 * contract Id
	 * 
	 * <ul>
	 * <li>1. Add input parameters to map</li>
	 * <li>2. Fetches organization legal name for input contract id using
	 * <b>fetchOrgNameFromContractId</b> from awards mapper</li>
	 * </ul>
	 * <ul<li>Method in R4</li></ul>>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asContractId - Contract Id
	 * @return organization name - string
	 * @throws ApplicationException If an Application Exception occurs Updated
	 * 
	 */
	public Map<String, String> fetchOrgDetailsFromContractId(SqlSession aoMybatisSession, String asContractId)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into update Contract Budget Status");
		HashMap<String, Object> loHmReqExceProp = new HashMap<String, Object>();
		loHmReqExceProp.put(HHSConstants.CONTRACT_ID, asContractId);
		Map<String, String> lsOrgDetailsMap = null;
		try
		{
			lsOrgDetailsMap = (Map<String, String>) DAOUtil.masterDAO(aoMybatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_AWARDS_MAPPER, HHSConstants.FETCH_ORG_DETAILS_FROM_CONTRACT_ID,
					HHSConstants.JAVA_LANG_STRING);
			setMoState("Successfully fetched organization legal name for contract Id:" + asContractId);
		}
		// handling Application exception
		catch (ApplicationException aoExp)
		{
			setMoState("Error while fetching organization legal name for contract Id:" + asContractId);
			aoExp.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error while fetching organization legal name for contract Id:" + asContractId, aoExp);
			throw aoExp;
		}
		// handling exceptions other than Application exception
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetching organization legal name for contract Id:" + asContractId, aoExp);
			setMoState("Error while fetching organization legal name for contract Id:" + asContractId);
			throw new ApplicationException("Exception while fetching organization legal name for contract Id:"
					+ asContractId, aoExp);
		}
		return lsOrgDetailsMap;
	}

	/**
	 * This method check if contract cof task has been approved.
	 * 
	 * <ul>
	 * <li>1. Add input parameters to map</li>
	 * <li>2. Fetches count of contract whose cof is not completed.Using
	 * <b>contractCofApproved</b> from awards mapper</li>
	 * </ul>
	 * Method added in R4
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asContractId - Contract Id
	 * @return organization name - string
	 * @throws ApplicationException If an Application Exception occurs Updated
	 * 
	 */
	public Integer contractCofApproved(SqlSession aoMybatisSession, String asContractId) throws ApplicationException
	{
		LOG_OBJECT.Debug("Fetch count of approved cof");
		HashMap<String, Object> loHmReqExceProp = new HashMap<String, Object>();
		loHmReqExceProp.put(HHSConstants.CONTRACT_ID, asContractId);
		List<String> aoContractStatusList = new ArrayList<String>();
		aoContractStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_CONTRACT_PENDING_CONFIGURATION));
		aoContractStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_CONTRACT_PENDING_COF));
		loHmReqExceProp.put(HHSConstants.STATUS_LIST, aoContractStatusList);
		Integer loContractCofApprovedCount = null;
		try
		{
			loContractCofApprovedCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loHmReqExceProp,
					HHSConstants.MAPPER_CLASS_AWARDS_MAPPER, HHSConstants.CONTRACT_COF_APPROVED,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			setMoState("Successfully fetched count of cof task for contract Id:" + asContractId);
		}
		// handling Application exception
		catch (ApplicationException aoExp)
		{
			setMoState("Error while fetching count of approved cof for contract Id:" + asContractId);
			aoExp.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error while while fetching count of approved cof for contract Id:" + asContractId, aoExp);
			throw aoExp;
		}
		// handling exceptions other than Application exception
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetching count of approved cof for contract Id:" + asContractId, aoExp);
			setMoState("Error while fetching count of approved cof for contract Id:" + asContractId);
			throw new ApplicationException("Exception while fetching count of approved cof for contract Id:"
					+ asContractId, aoExp);
		}
		return loContractCofApprovedCount;
	}

	/**
	 * This method check if contract budget task has been approved.
	 * 
	 * <ul>
	 * <li>1. Add input parameters to map</li>
	 * <li>2. Fetches count of contract whose budget review is not
	 * completed.Using <b>contractBudgetApproved</b> from awards mapper</li>
	 * </ul>
	 * <ul>
	 * <li>Method added in R4</li>
	 * </ul>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asContractId - Contract Id
	 * @return organization name - string
	 * @throws ApplicationException If an Application Exception occurs Updated
	 * 
	 */
	public Integer contractBudgetApproved(SqlSession aoMybatisSession, String asContractId) throws ApplicationException
	{
		LOG_OBJECT.Debug("Fetch count of budget approved");
		HashMap<String, Object> loHmReqExceProp = new HashMap<String, Object>();
		loHmReqExceProp.put(HHSConstants.CONTRACT_ID, asContractId);
		List<String> aoBudgetStatusList = new ArrayList<String>();
		aoBudgetStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_BUDGET_PENDING_APPROVAL));
		aoBudgetStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.BUDGET_PENDING_SUBMISSION));
		aoBudgetStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.BUDGET_RETURNED_FOR_REVISION));
		loHmReqExceProp.put(HHSConstants.STATUS_LIST, aoBudgetStatusList);
		Integer loContractBudgetApprovedCount = null;
		try
		{
			loContractBudgetApprovedCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loHmReqExceProp,
					HHSConstants.MAPPER_CLASS_AWARDS_MAPPER, HHSConstants.CONTRACT_BUDGET_APPROVED,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			setMoState("Successfully fetched count of budget approved for contract Id:" + asContractId);
		}
		// handling Application exception
		catch (ApplicationException aoExp)
		{
			setMoState("Error while fetching count of budget approved for contract Id:" + asContractId);
			aoExp.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error while while fetching count of budget approved for contract Id:" + asContractId,
					aoExp);
			throw aoExp;
		}
		// handling exceptions other than Application exception
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetching count of budget approved for contract Id:" + asContractId, aoExp);
			setMoState("Error while fetching count of budget approved for contract Id:" + asContractId);
			throw new ApplicationException("Exception while fetching count of budget approved for contract Id:"
					+ asContractId, aoExp);
		}
		return loContractBudgetApprovedCount;
	}

	/**
	 * This method is used to fetch default configuration id for a given
	 * evaluation Pool Mapping Id
	 * 
	 * <ul>
	 * <li>1. Get the procurement id from task properties map</li>
	 * <li>2. Fetches default configuration Id for input evaluation Pool Mapping
	 * Id using <b>fetchDefaultConfigId</b> from awards mapper</li>
	 * </ul>
	 * <ul>
	 * <Li>Method added in R4</li>
	 * </ul>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoTaskMap - Task Properties map
	 * @param asWobNumber - Workflow Id
	 * @return default configuration Id - string
	 * @throws ApplicationException If an Application Exception occurs New
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public String fetchDefaultDocumentConfigId(SqlSession aoMybatisSession, HashMap<String, Object> aoTaskMap,
			String asWobNumber) throws ApplicationException
	{
		String lsDefaultConfigId = null;
		try
		{
			// check for the null value of task detail map
			if (null != aoTaskMap)
			{
				HashMap loProcurementMap = (HashMap) aoTaskMap.get(asWobNumber);
				if (null != loProcurementMap)
				{
					// Get the evaluation Pool Mapping Id
					String lsEvalPoolMappingId = (String) loProcurementMap.get(P8Constants.EVALUATION_POOL_MAPPING_ID);
					lsDefaultConfigId = (String) DAOUtil.masterDAO(aoMybatisSession, lsEvalPoolMappingId,
							HHSConstants.MAPPER_CLASS_AWARDS_MAPPER, HHSConstants.FETCH_DEFAULT_CONFIG_ID,
							HHSConstants.JAVA_LANG_STRING);
					setMoState("Successfully fetched default configuration Id for evaluation Pool Mapping Id:"
							+ lsEvalPoolMappingId);
				}
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoExp)
		{
			aoExp.setContextData(aoTaskMap);
			LOG_OBJECT.Error("Error while fetching Default Configuration Id", aoExp);
			setMoState("Error while fetching Default Configuration Id" + aoTaskMap);
			throw aoExp;
		}
		// handling exception other than Application Exception
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetching Default Configuration Id", aoExp);
			setMoState("Error while fetching Default Configuration Id" + aoTaskMap);
			throw new ApplicationException("Error while fetching Default Configuration Id", aoExp);
		}
		return lsDefaultConfigId;
	}

	/**
	 * This method fetches list of award document types selected as default
	 * configurations for a given evaluation pool mapping id
	 * 
	 * <ul>
	 * <li>1. Get the evaluation pool mapping id from task properties map</li>
	 * <li>2. Fetches award document types default selected for given evaluation
	 * pool mapping id using <b>fetchAwardDocumentTypeForTask</b> from awards
	 * mapper</li>
	 * </ul>
	 * <ul>
	 * <li>Method added in R4</li>
	 * </ul>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoTaskMap - Task Properties map
	 * @param asWobNumber - Workflow Id
	 * @return loAwardDocumentList - list of award document type
	 * @throws ApplicationException If an Application Exception occurs New
	 * 
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public List<ExtendedDocument> fetchAwardDocumentTypeForTask(SqlSession aoMybatisSession,
			HashMap<String, Object> aoTaskMap, String asWobNumber) throws ApplicationException
	{
		List<ExtendedDocument> loAwardDocumentList = null;
		try
		{
			// check for the null value of task detail map
			if (null != aoTaskMap)
			{
				HashMap loProcurementMap = (HashMap) aoTaskMap.get(asWobNumber);
				if (null != loProcurementMap)
				{
					// Get the evaluation pool mapping Id
					String lsEvaluationPoolMappingId = (String) loProcurementMap
							.get(P8Constants.EVALUATION_POOL_MAPPING_ID);
					// Get the award document type list
					loAwardDocumentList = (List<ExtendedDocument>) DAOUtil.masterDAO(aoMybatisSession,
							lsEvaluationPoolMappingId, HHSConstants.MAPPER_CLASS_AWARDS_MAPPER,
							HHSConstants.FETCH_AWARD_DOCUMENT_TYPE_FOR_TASK, HHSConstants.JAVA_LANG_STRING);
					setMoState("Award Documents Type List fetched successfully for evaluation pool mapping Id:"
							+ lsEvaluationPoolMappingId);
				}
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoExp)
		{
			aoExp.setContextData(aoTaskMap);
			LOG_OBJECT.Error("Error while fetching Award Documents Type List", aoExp);
			setMoState("Error while fetching Award Documents Type List" + aoTaskMap);
			throw aoExp;
		}
		// handling exception other than Application Exception
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetching Award Documents Type List", aoExp);
			setMoState("Error while fetching Award Documents Type List" + aoTaskMap);
			throw new ApplicationException("Error while fetching Award Documents Type List", aoExp);
		}
		return loAwardDocumentList;
	}

	// Commented for enhancement 6448 for Release 3.8.0
	/*	*//**
	 * This method fetches the count of awards not approved for a
	 * procurement for a given procurement id
	 * 
	 * <ul>
	 * <li>1. Add input parameters to map</li>
	 * <li>2. Checks the count of awards which are not approved for a given
	 * procurement id using <b>checkAwardReviewStatus</b> from awards mapper</li>
	 * </ul>
	 * <ul>
	 * <li>Method updated in R4</li>
	 * </ul>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProcurementId - Procurement Id
	 * @return flag depicting award review status approved or not - boolean
	 * @throws ApplicationException If an Application Exception occurs New
	 * 
	 */
	/*
	 * public Boolean checkAwardReviewStatus(SqlSession aoMybatisSession, String
	 * asProcurementId) throws ApplicationException { Map<String, Object>
	 * loDataMap = new HashMap<String, Object>(); Boolean loAwardApproved =
	 * false; try { loDataMap.put(HHSConstants.PROCUREMENT_ID, asProcurementId);
	 * loDataMap.put(HHSConstants.STATUS_AWARD_REVIEW_APPROVED,
	 * PropertyLoader.getProperty( HHSConstants.PROPERTIES_STATUS_CONSTANT,
	 * HHSConstants.AWARD_REVIEW_APPROVED)); int liNonApprovedReviewStatus =
	 * (Integer) DAOUtil.masterDAO(aoMybatisSession, loDataMap,
	 * HHSConstants.MAPPER_CLASS_AWARDS_MAPPER,
	 * HHSConstants.CHECK_AWARD_REVIEW_STATUS, HHSConstants.JAVA_UTIL_MAP);
	 * loAwardApproved = (liNonApprovedReviewStatus == 0);
	 * setMoState("Award Review Status checked successfully for procurement Id:"
	 * + asProcurementId); } // Any Exception from DAO class will be thrown as
	 * Application Exception // which will be handles over here. It throws
	 * Application Exception back // to Controllers calling method through
	 * Transaction framework catch (ApplicationException aoAppEx) {
	 * aoAppEx.setContextData(loDataMap); LOG_OBJECT.Error(
	 * "Error while checking Award Review Status for procurement Id:" +
	 * asProcurementId, aoAppEx); setMoState(
	 * "Error while checking Award Review Status for close procurement Id:" +
	 * asProcurementId + aoAppEx.getMessage()); throw aoAppEx; } // handling
	 * exception other than ApplicationException catch (Exception aoAppEx) {
	 * LOG_OBJECT.Error("Error while checking Award Review Status for Id:" +
	 * asProcurementId, aoAppEx);
	 * setMoState("Error while checking Award Review Status for Id:" +
	 * asProcurementId + aoAppEx.getMessage()); throw new
	 * ApplicationException("Error while checking Award Review Status for Id:" +
	 * asProcurementId, aoAppEx); } return loAwardApproved; }
	 */

	/**
	 * This method updates contract start date and contract end date for a input
	 * contract id while assigning award epin for open ended RFPs
	 * 
	 * <ul>
	 * <li>1. Add input parameters to map</li>
	 * <li>2. If contract start date and end date are not null, Updates contract
	 * start state and end date for input contract id using
	 * <b>updateContractStartEndDate</b> from awards mapper</li>
	 * </ul>
	 * <ul>
	 * <li>Method added in R4</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL Session
	 * @param asEvaluationPoolMappingId - Evaluation Pool Mapping Id
	 * @param asContractId - Contract Id
	 * @param asContractStartDate - Contract Start Date
	 * @param asContractEndDate - Contract End Date
	 * @return flag depicting update status - boolean
	 * @throws ApplicationException If an Application Exception occurs New
	 * 
	 */
	public Boolean updateContractStartEndDates(SqlSession aoMybatisSession, String asEvaluationPoolMappingId,
			String asContractId, String asContractStartDate, String asContractEndDate) throws ApplicationException
	{
		Map<String, Object> loDataMap = new HashMap<String, Object>();
		loDataMap.put(HHSConstants.CONTRACT_ID, asContractId);
		loDataMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
		Boolean loContractDateUpdated = false;
		try
		{
			if (asContractStartDate != null && asContractEndDate != null && !asContractStartDate.isEmpty()
					&& !asContractEndDate.isEmpty())
			{
				loDataMap.put(HHSConstants.CONTRACT_START_DATE, DateUtil.getDate(asContractStartDate));
				loDataMap.put(HHSConstants.CONTRACT_END_DATE, DateUtil.getDate(asContractEndDate));
				int liRowsUpdated = (Integer) DAOUtil.masterDAO(aoMybatisSession, loDataMap,
						HHSConstants.MAPPER_CLASS_AWARDS_MAPPER, HHSConstants.UPDATE_CONTRACT_START_END_DATE,
						HHSConstants.JAVA_UTIL_MAP);
				loContractDateUpdated = (liRowsUpdated != 0);
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(loDataMap);
			LOG_OBJECT.Error("Error while updating contract state/end date for contract id:" + asContractId, aoAppEx);
			setMoState("Error while updating contract state/end date for contract id:" + asContractId
					+ aoAppEx.getMessage());
			throw aoAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Error while updating contract state/end date for contract id:" + asContractId, aoAppEx);
			setMoState("Error while updating contract state/end date for contract id:" + asContractId
					+ aoAppEx.getMessage());
			throw new ApplicationException("Error while updating contract state/end date for contract id:"
					+ asContractId, aoAppEx);
		}
		return loContractDateUpdated;
	}

	/**
	 * This method is used to insert award document details for given evaluation
	 * pool mapping id if default configuration has been selected for given
	 * procurement id
	 * 
	 * <ul>
	 * <li>1.Get the input parameter map</li>
	 * <li>2.Execute query with id insertDefaultAwardDocDetails from awards
	 * mapper</li>
	 * </ul>
	 * <ul>
	 * <li>Methid added in R4</li>
	 * </ul>
	 * @param aoMybatisSession SQL mybatis Session
	 * @param aoDataMap map containing award properties
	 * @return boolean value
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Boolean insertDefaultAwardDocDetails(SqlSession aoMybatisSession, Map<String, Object> aoDataMap)
			throws ApplicationException
	{
		Boolean loInsertStatus = false;
		try
		{
			DAOUtil.masterDAO(aoMybatisSession, aoDataMap, HHSConstants.MAPPER_CLASS_AWARDS_MAPPER,
					HHSConstants.INSERT_DEFAULT_AWARD_DOC_DETAILS, HHSConstants.JAVA_UTIL_MAP);
			loInsertStatus = true;
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(aoDataMap);
			LOG_OBJECT.Error("Error while inserting default award doc details:", aoAppEx);
			setMoState("Error while inserting default award doc details:" + aoAppEx.getMessage());
			throw aoAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Error while inserting default award doc details:", aoAppEx);
			setMoState("Error while inserting default award doc details" + aoAppEx.getMessage());
			throw new ApplicationException("Error while inserting default award doc details:", aoAppEx);
		}
		return loInsertStatus;
	}

	/**
	 * This method fetches Agency Award documents corresponding to the parameter
	 * map Added for Enhancement #6429 for Release 3.4.0
	 * <ul>
	 * <li>1. Retrieve Map containing procurement Id and user org Id from the
	 * Channel object</li>
	 * <li>2. Create context data HashMap and populate the same with the
	 * parameter map</li>
	 * <li>3. If the fetched parameter map is not null then execute query
	 * <b>fetchAgencyAwardDocuments</b> to fetch the required award Id</li>
	 * <li>4. Return the fetched list of award documents</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param loParamMap an award map object
	 * @return loAwardDocumentList - an object of type List<ExtendedDocument>
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<ExtendedDocument> fetchAgencyAwardDocuments(SqlSession aoMybatisSession,
			Map<String, String> loParamMap, String asAwardId) throws ApplicationException
	{
		List<ExtendedDocument> loAwardDocumentList = null;
		HashMap<String, Object> loHMContextData = new HashMap<String, Object>();
		loParamMap.put(HHSConstants.AWARD_ID, asAwardId);
		loHMContextData.put(HHSConstants.PARAM_MAP_LOWERCASE, loParamMap);

		LOG_OBJECT.Debug("Entered into fetching agency award documements::" + loHMContextData.toString());
		// checking if the param map contains data or not
		if (loParamMap != null)
		{
			try
			{
				loAwardDocumentList = (List<ExtendedDocument>) DAOUtil.masterDAO(aoMybatisSession, loParamMap,
						HHSConstants.MAPPER_CLASS_AWARDS_MAPPER, HHSConstants.FETCH_AGENCY_AWARD_DOCUMENTS,
						HHSConstants.JAVA_UTIL_MAP);
				setMoState("Agency Award Documents List fetched successfully for proposal Id:"
						+ loParamMap.get(HHSConstants.PROPOSAL_ID));
			}
			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handles over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */
			catch (ApplicationException aoExp)
			{
				setMoState("Transaction Failed:: AwardService:fetchAgencyAwardDocuments method - Error while fetching Agency Award Documents List for proposal Id:"
						+ loParamMap.get(HHSConstants.PROPOSAL_ID));
				aoExp.setContextData(loHMContextData);
				LOG_OBJECT.Error("Error while fetching Agency Award Documents List:", aoExp);
				throw aoExp;
			}
			// handling exception other than Application Exception
			catch (Exception aoExp)
			{
				LOG_OBJECT.Error("Error while fetching AgencyAward Documents List for proposal Id:", aoExp);
				setMoState("Transaction Failed:: AwardService:fetchAgencyAwardDocuments method - Error while fetching Agency Award Details for proposal Id:"
						+ loParamMap.get(HHSConstants.PROPOSAL_ID));
				throw new ApplicationException("Error occurred while fetching Agency award documents", aoExp);
			}
		}
		return loAwardDocumentList;
	}

	/**
	 * This method fetches Agency Award document ids corresponding to the
	 * parameter map Added for Enhancement #6429 for Release 3.4.0
	 * <ul>
	 * <li>1. Retrieve Map containing procurement Id and user org Id from the
	 * Channel object</li>
	 * <li>2. Create context data HashMap and populate the same with the
	 * parameter map</li>
	 * <li>3. If the fetched parameter map is not null then execute query
	 * <b>fetchAgencyAwardDocumentIds</b> to fetch the required award Id</li>
	 * <li>4. Return the fetched list of award documents</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param loParamMap an award map object
	 * @return loAwardDocumentidsList - an object of type List<String>
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<String> fetchAgencyAwardDocumentIds(SqlSession aoMybatisSession, String asAwardId,
			String asProviderOrgID, String asProcurementId, String asEvaluationPoolMappingId)
			throws ApplicationException
	{
		List<String> loAgencyAwardDocumentidsList = null;
		HashMap<String, Object> loHMContextData = new HashMap<String, Object>();
		Map<String, String> loParamMap = new HashMap<String, String>();
		loParamMap.put(HHSConstants.AWARD_ID, asAwardId);
		loParamMap.put(HHSConstants.AS_PROVIDER_ORG_ID, asProviderOrgID);
		loParamMap.put(HHSConstants.PROCUREMENT_ID, asProcurementId);
		loParamMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
		loHMContextData.put(HHSConstants.PARAM_MAP_LOWERCASE, loParamMap);

		LOG_OBJECT.Debug("Entered into fetching agency award documement ids::" + loHMContextData.toString());
		// checking if the param map contains data or not
		if (loParamMap != null)
		{
			try
			{
				loAgencyAwardDocumentidsList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, loParamMap,
						HHSConstants.MAPPER_CLASS_AWARDS_MAPPER, HHSConstants.FETCH_AGENCY_AWARD_DOCUMENT_IDs,
						HHSConstants.JAVA_UTIL_HASH_MAP);
				setMoState("Agency Award Document ids List fetched successfully for award Id:"
						+ loParamMap.get(HHSConstants.AWARD_ID));
			}
			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handles over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */
			catch (ApplicationException aoExp)
			{
				setMoState("Transaction Failed:: AwardService:fetchAgencyAwardDocumentIds method - Error while fetching Agency Award Document ids List for award Id:"
						+ loParamMap.get(HHSConstants.AWARD_ID));
				aoExp.setContextData(loHMContextData);
				LOG_OBJECT.Error("Error while fetching Agency Award Document ids List:", aoExp);
				throw aoExp;
			}
			// handling exception other than Application Exception
			catch (Exception aoExp)
			{
				LOG_OBJECT.Error("Error while fetching AgencyAward Documents List for proposal Id:", aoExp);
				setMoState("Transaction Failed:: AwardService:fetchAgencyAwardDocumentIds method - Error while fetching Agency Award ids for award Id:"
						+ loParamMap.get(HHSConstants.AWARD_ID));
				throw new ApplicationException("Error occurred while fetching Agency award document ids", aoExp);
			}
		}
		return loAgencyAwardDocumentidsList;
	}

	/**
	 * The Service the Award Review Status to "Update in Progress"
	 * <ul>
	 * 6574 for 3.10.0
	 * <li>Obtain the required information to update the status i.e
	 * ContractId,UserId ,StatusId</li>
	 * <li>Trigger query <b>updateAwardReviewStatus query</b></li>
	 * <li>Return update Award Review Status Flag</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession
	 * @param aoStatusInfo Map<String, String>
	 * @return loUpdateAwardReviewStatusFlag - Boolean
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Boolean updateAwardReviewStatusToUpdate(SqlSession aoMybatisSession, Map<String, String> aoStatusInfo)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into update Award Review Status ");
		HashMap<String, Object> loHmReqExceProp = new HashMap<String, Object>();
		loHmReqExceProp.put(HHSConstants.STATUS_INFO_MAP_ARG, aoStatusInfo);
		Boolean loUpdateAwardReviewStatusFlag = Boolean.FALSE;
		try
		{ // setting Award Status Id as cancelled
			aoStatusInfo.put(HHSConstants.STATUS_ID, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_AWARD_REVIEW_UPDATE_IN_PROGRESS_TEMP));
			aoStatusInfo.put(HHSConstants.UPDATE_AFTER_AWARD_APPROVAL, HHSConstants.TRUE);
			// Calling updateAwardStatus for updating Award Status,passing
			// required using Map
			DAOUtil.masterDAO(aoMybatisSession, aoStatusInfo, HHSConstants.MAPPER_CLASS_AWARDS_MAPPER,
					HHSConstants.UPDATE_AWARD_REVIEW_STATUS, HHSConstants.JAVA_UTIL_MAP);
			loUpdateAwardReviewStatusFlag = Boolean.TRUE;

			setMoState("Award Review Status sucessfully updated to Update in Progress"
					+ aoStatusInfo.get(HHSConstants.CONTRACT_ID));
		}
		// handling Applicaton exception
		catch (ApplicationException aoExp)
		{
			setMoState("Error while updating Award Review Status :");
			aoExp.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error while updating Award Review Status :", aoExp);
			throw aoExp;
		}
		// handling exceptions other than Application exception
		catch (Exception aoExp)
		{
			ApplicationException loAppExp = new ApplicationException("Error while updating Award Review Status :",
					aoExp);
			LOG_OBJECT.Error("Error while updating Award Review Status :", loAppExp);
			setMoState("Error while updating Award  Review Status :");
			throw loAppExp;

		}

		return loUpdateAwardReviewStatusFlag;

	}

	/**
	 * This method will give the provider and Award detials to show it on
	 * Evalution summary listing screen
	 * @param aoMyBatisSession
	 * @param providerId
	 * @return
	 */
	public HashMap<Object, Object> getProviderAndAwardDetails(SqlSession aoMyBatisSession, String providerId)
	{
		return new HashMap();
	}

	// Added below methods for Release 5
	/**
	 * This method will give the estimated amount of award after analysying form
	 * all proposal submitted by a Organization
	 * @param aoMyBatisSession
	 * @param aoproposalAwards
	 * @return list of AwardBean
	 */
	public List<AwardBean> getEstimatedAmountOfAward(SqlSession aoMyBatisSession, List aoProposalAwards)
	{
		return new ArrayList<AwardBean>();
	}

	/**
	 * This method will save the finalized amount in DB after clicking on
	 * Confirm button
	 * @param aoMyBatisSession
	 * @param negotiatedAmount
	 * @return
	 */
	public Boolean saveFinalizedAmountinDB(SqlSession aoMyBatisSession, Map aoHashMap) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into saveFinalizedAmountinDB ");
		Boolean loUpdateAmountFlag = Boolean.FALSE;
		try
		{
			if (null != aoHashMap)
			{
				DAOUtil.masterDAO(aoMyBatisSession, aoHashMap, HHSConstants.MAPPER_CLASS_AWARDS_MAPPER,
						HHSR5Constants.UPDATE_FINALIZED_AMOUNT, HHSConstants.JAVA_UTIL_MAP);
				loUpdateAmountFlag = Boolean.TRUE;

				setMoState("Finalized Amount sucessfully:");
			}
		}
		// handling Applicaton exception
		catch (ApplicationException aoExp)
		{
			setMoState("Error while updating Finalized Amount:");
			aoExp.setContextData(aoHashMap);
			LOG_OBJECT.Error("Error while updating Finalized Amount:", aoExp);
			throw aoExp;
		}
		// handling exceptions other than Application exception
		catch (Exception aoExp)
		{
			ApplicationException loAppExp = new ApplicationException("Error while updating Finalized Amount:", aoExp);
			LOG_OBJECT.Error("Error while updating Finalized Amount:", loAppExp);
			setMoState("Error while updating Finalized Amount:");
			throw loAppExp;
		}
		LOG_OBJECT.Debug("Exited saveFinalizedAmountinDB ");
		return loUpdateAmountFlag;
	}

	/**
	 * This method is part of Release 5 Award Negotiation Workflow. This method
	 * will update the negotiation flag for Approve Award.
	 * 
	 * <ul>
	 * <li>Get the award hashmap from input params</li>
	 * <li>Execute query with id "updateAwardNegotiationDetails" from awards
	 * mapper</li>
	 * <li>Return updated status to controller</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession sql session object
	 * @param aoAwardMap a hashmap containing award details
	 * @return loUpdateStatus - a boolean value of award update status
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Boolean updateAwardNegotiationDetails(SqlSession aoMybatisSession, Map<String, String> aoAwardMap)
			throws ApplicationException
	{
		Boolean loUpdateStatus = false;
		String lsUpdateFlag = aoAwardMap.get(HHSR5Constants.IS_NEGOTIATION_REQUIRED);
		try
		{
			if (null != lsUpdateFlag)
			{
				DAOUtil.masterDAO(aoMybatisSession, aoAwardMap, HHSConstants.MAPPER_CLASS_AWARDS_MAPPER,
						HHSR5Constants.UPDATE_AWARD_NEGOTIATION_FLAG, HHSConstants.JAVA_UTIL_HASH_MAP);
				loUpdateStatus = true;
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException loExp)
		{
			setMoState("Error while updating award Negotiation details");
			loExp.setContextData(aoAwardMap);
			LOG_OBJECT.Error("Error while updating award Negotiation details", loExp);
			throw loExp;
		}
		// handling exception other than Application Exception.
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Error while updating award Negotiation details", loEx);
			setMoState("Error while updating award Negotiation details");
			throw new ApplicationException("Error while updating award Negotiation details", loEx);
		}
		return loUpdateStatus;
	}

	/**
	 * This method is part of Release 5 Award Negotiation Workflow. 
	 * This Methos executes the workflow: "WF204 - Negotiation Award"
	 * 
	 * @param aoMybatisSession sql session object
	 * @param P8UserSession Filenet session
	 * @return aoRequiredProps - Containing the evaluationPoolId and procurementId
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Boolean cancelAllAwardWorkflows(SqlSession aoMyBatisSession, P8UserSession aoUserSession, Map aoRequiredProps)
			throws ApplicationException
	{
		Boolean lbTerminationFlag = Boolean.FALSE;
		LOG_OBJECT.Debug("Entered into cancelAllAwardWorkflows:: " + aoRequiredProps.toString());
		WorkflowOperations loWorkflowOperations = new WorkflowOperations(LOG_OBJECT);
		String lsEvaluationPoolMappingId = (String) aoRequiredProps.get(HHSConstants.EVALUATION_POOL_MAPPING_ID);
		String lsProcurementId = (String) aoRequiredProps.get(HHSConstants.PROCUREMENT_ID);
		// Prepare the filter to get the
		String lsFilter = P8Constants.EVALUATION_POOL_MAPPING_ID + HHSR5Constants.STRING_EQUAL
				+ lsEvaluationPoolMappingId + HHSR5Constants.STR + HHSR5Constants.AND_SMALL_CAPS
				+ P8Constants.PE_WORKFLOW_PROCUREMENT_ID + HHSR5Constants.STRING_EQUAL + lsProcurementId
				+ HHSR5Constants.STR;
		String[] loWorkflowNames =
		{ P8Constants.PE_AWARD_WORK_FLOW_SUBJECT, HHSP8Constants.WORKFLOW_NAME_204_NEGOTIATION_AWARD,
				P8Constants.PE_EVALUATE_AWARD_TASK_NAME, HHSP8Constants.WF_CONTRACT_CERTIFICATION_FUND,
				HHSP8Constants.WF_CONTRACT_CONFIGURATION };
		try
		{
			P8SecurityOperations loFilenetConnection = new P8SecurityOperations();
			VWSession moPeSession = loFilenetConnection.getPESession(aoUserSession);
			// Call the method that will cancel the award workflows.
			loWorkflowOperations.cancelWorkflows(lsFilter, loWorkflowNames, moPeSession);
			lbTerminationFlag = true;
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured on executing the cancelAllAwardWorkflows with filter:" + lsFilter, aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Error occured on executing the cancelAllAwardWorkflows with filter:" + lsFilter, aoAppEx);
			throw new ApplicationException("Error occured during executing the cancelAllAwardWorkflows method", aoAppEx);
		}
		LOG_OBJECT.Debug("Exited into cancelAllAwardWorkflows");
		return lbTerminationFlag;
	}

	/**
	 * This method is part of Release 5 Award Negotiation Workflow. This method
	 * will update the Contract status for selected provider.
	 * 
	 * <ul>
	 * <li>Get the award hashmap from input params</li>
	 * <li>Execute query with id "updateContractForNegotiationSeleted" from
	 * awards mapper</li>
	 * <li>Return updated status to controller</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession sql session object
	 * @param aoAwardMap a hashmap containing award details
	 * @return loUpdateStatus - a boolean value of award update status
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Boolean updateContractForNegotiationSeleted(SqlSession aoMybatisSession, Map<String, String> aoAwardMap)
			throws ApplicationException
	{
		Boolean loUpdateStatus = false;
		try
		{
			if (null != aoAwardMap)
			{
				DAOUtil.masterDAO(aoMybatisSession, aoAwardMap, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
						HHSR5Constants.UPDATE_CONTRACT_NEGOTIATION_STATUS, HHSConstants.JAVA_UTIL_HASH_MAP);
				loUpdateStatus = true;
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException loExp)
		{
			setMoState("Error while updating award Negotiation details");
			loExp.setContextData(aoAwardMap);
			LOG_OBJECT.Error("Error while updating award Negotiation details", loExp);
			throw loExp;
		}
		// handling exception other than Application Exception.
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Error while updating award Negotiation details", loEx);
			setMoState("Error while updating award Negotiation details");
			throw new ApplicationException("Error while updating award Negotiation details", loEx);
		}
		return loUpdateStatus;
	}

	/**
	 * This method is part of Release 5 Award Negotiation Workflow. This method
	 * will update the Contract status for selected provider.
	 * 
	 * <ul>
	 * <li>Get the award hashmap from input params</li>
	 * <li>Execute query with id "updateContractAmountAfterNegotiation" from
	 * awards mapper</li>
	 * <li>Return updated status to controller</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession sql session object
	 * @param aoAwardMap a hashmap containing award details
	 * @return loUpdateStatus - a boolean value of award update status
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Boolean updateContractAmountAfterNegotiation(SqlSession aoMybatisSession, Map<String, String> aoAwardMap)
			throws ApplicationException
	{
		Boolean loUpdateStatus = false;
		try
		{
			String lsUpdateFlag = (String) aoAwardMap.get(HHSConstants.AWARD_STATUS_ID);
			if ((HHSR5Constants.STATUS_UPDATE_CONTRACT_AFTER_NEGOTIATION.contains(lsUpdateFlag)))
			{
				DAOUtil.masterDAO(aoMybatisSession, aoAwardMap, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
						HHSR5Constants.UPDATE_CONTRACT_AMOUNT_AFTER_NEGOTIATION, HHSConstants.JAVA_UTIL_HASH_MAP);
				DAOUtil.masterDAO(aoMybatisSession, aoAwardMap, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
						HHSR5Constants.UPDATE_EVALUATION_AMOUNT_AFTER_NEGOTIATIONS, HHSConstants.JAVA_UTIL_HASH_MAP);
				loUpdateStatus = true;
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException loExp)
		{
			setMoState("Error while updating award Negotiation details");
			loExp.setContextData(aoAwardMap);
			LOG_OBJECT.Error("Error while updating award Negotiation details", loExp);
			throw loExp;
		}
		// handling exception other than Application Exception.
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Error while updating award Negotiation details", loEx);
			setMoState("Error while updating award Negotiation details");
			throw new ApplicationException("Error while updating award Negotiation details", loEx);
		}
		return loUpdateStatus;
	}

	/**
	 * This method fetches counts for enable/disable 'Cancel All Awards' Button.
	 * 
	 * <ul>
	 * <li>1. Add input parameters to map</li>
	 * <li>2. Fetches organization legal name for input evaluation id using
	 * <b>fetchEnableDisableCancelAllCount</b> from Contracts mapper</li>
	 * </ul>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asEvaluationPoolMappingId - Evaluation Pool Id
	 * @return lsContractCount - string
	 * @throws ApplicationException If an Application Exception occurs Updated
	 * 
	 */
	public Integer fetchEnableDisableCancelAllCount(SqlSession aoMybatisSession, String asEvaluationPoolMappingId)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into fetchEnableDisableCancelAllCount");
		HashMap<String, Object> loHmReqExceProp = new HashMap<String, Object>();
		loHmReqExceProp.put(HHSConstants.CONTRACT_ID, asEvaluationPoolMappingId);
		Integer lsContractCount = null;
		try
		{
			lsContractCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, asEvaluationPoolMappingId,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSR5Constants.FETCH_CANCEL_ALL_AWARD_COUNTS,
					HHSConstants.JAVA_LANG_STRING);
			setMoState("Successfully fetched enable/disable counts:" + asEvaluationPoolMappingId);
		}
		// handling Application exception
		catch (ApplicationException aoExp)
		{
			setMoState("Error while fetchEnableDisableCancelAllCount for Evaluation Id:" + asEvaluationPoolMappingId);
			aoExp.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error while fetchEnableDisableCancelAllCount for Evaluation Id:"
					+ asEvaluationPoolMappingId, aoExp);
			throw aoExp;
		}
		// handling exceptions other than Application exception
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetchEnableDisableCancelAllCount for Evaluation Id:"
					+ asEvaluationPoolMappingId, aoExp);
			setMoState("Error while fetchEnableDisableCancelAllCount for Evaluation Id:" + asEvaluationPoolMappingId);
			throw new ApplicationException("Error while fetchEnableDisableCancelAllCount for Evaluation Id:"
					+ asEvaluationPoolMappingId, aoExp);
		}
		return lsContractCount;
	}
	
	/** This is method is added for Defect 7149. It is used to cancel 
	 *  the award and Negotation workflows.
	 * @param aoUserSession - Filenet Session
	 * @param aoRequiredProps - Required Values
	 * @return
	 * @throws ApplicationException
	 */
	public Boolean cancelAwardWorkflow(P8UserSession aoUserSession, Map aoRequiredProps)
			throws ApplicationException
	{
		Boolean lbTerminationFlag = Boolean.FALSE;
		LOG_OBJECT.Debug("Entered into cancelAwardWorkflow:: " + aoRequiredProps.toString());
		WorkflowOperations loWorkflowOperations = new WorkflowOperations(LOG_OBJECT);
		String lsContractId = (String) aoRequiredProps.get(HHSConstants.CONTRACT_ID);
		// Prepare the filter to get the
		String lsFilter = HHSP8Constants.CONTRACT_ID + "='" + lsContractId + "'";
		String[] loWorkflowNames =
		{ HHSP8Constants.WORKFLOW_NAME_302_CONTRACT_CONFIGURATION,
				HHSP8Constants.WORKFLOW_NAME_303_CONTRACT_CERTIFICATION_FUNDS,
				HHSP8Constants.WORKFLOW_NAME_304_CONTRACT_BUDGET, HHSP8Constants.WORKFLOW_NAME_204_NEGOTIATION_AWARD };

		try
		{
			P8SecurityOperations loFilenetConnection = new P8SecurityOperations();
			VWSession moPeSession = loFilenetConnection.getPESession(aoUserSession);
			// Call the method that will cancel the award workflows.
			loWorkflowOperations.cancelWorkflows(lsFilter, loWorkflowNames, moPeSession);
			lbTerminationFlag = true;
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured on executing the cancelAwardWorkflow with filter:" + lsFilter, aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Error occured on executing the cancelAwardWorkflow with filter:" + lsFilter, aoAppEx);
			throw new ApplicationException("Error occured during executing the cancelAllAwardWorkflows method", aoAppEx);
		}
		LOG_OBJECT.Debug("Exited into cancelAwardWorkflow");
		return lbTerminationFlag;
	}
	
	/** This is method is added for Defect 7149.
	 *  It is used to set Cancel Paramter into Award WF.
	 * @param aoUserSession
	 * @param asRfpReleaseBeforeR4Flag
	 * @param aoRequiredProps
	 * @return
	 * @throws ApplicationException
	 */
	public Boolean setCancelParameterOnAwardWorkflow(P8UserSession aoUserSession, String asRfpReleaseBeforeR4Flag,
			Map aoRequiredProps) throws ApplicationException
	{
		Boolean lbCancelFlag = Boolean.FALSE;
		LOG_OBJECT.Debug("Entered into setCancelParameterOnAwardWorkflow:: " + asRfpReleaseBeforeR4Flag);
		WorkflowOperations loWorkflowOperations = new WorkflowOperations(LOG_OBJECT);
		String lsEvaluationPoolMappingId = (String) aoRequiredProps.get(HHSConstants.EVALUATION_POOL_MAPPING_ID);
		String lsProcurementId = (String) aoRequiredProps.get(HHSConstants.PROCUREMENT_ID);
		try
		{
			P8SecurityOperations loFilenetConnection = new P8SecurityOperations();
			VWSession moPeSession = loFilenetConnection.getPESession(aoUserSession);
			// Set the cancelflag in the award workflow to true, to make sure it
			// will not go to the award doc step.
			Boolean loCancelFlag = true;
			StringBuffer lsSetCancelAwardFilter = new StringBuffer(HHSP8Constants.PE_WORKFLOW_PROCUREMENT_ID)
					.append("='").append(lsProcurementId).append("'");
			if (null == asRfpReleaseBeforeR4Flag
					|| asRfpReleaseBeforeR4Flag.equalsIgnoreCase(HHSP8Constants.EMPTY_STRING))
			{
				lsSetCancelAwardFilter.append(" and ");
				lsSetCancelAwardFilter.append(HHSP8Constants.PROPERTY_PE_EVAL_POOL_MAPPING_ID);
				lsSetCancelAwardFilter.append("='");
				lsSetCancelAwardFilter.append(lsEvaluationPoolMappingId);
				lsSetCancelAwardFilter.append("'");
			}
			loWorkflowOperations.setCancelParameterOnAwardWorkflow(moPeSession, lsSetCancelAwardFilter.toString(),
					loCancelFlag);
			lbCancelFlag = Boolean.TRUE;
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured on executing the setCancelParameterOnAwardWorkflow" , aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Error occured on executing the setCancelParameterOnAwardWorkflow", aoAppEx);
			throw new ApplicationException("Error occured during executing the setCancelParameterOnAwardWorkflow method", aoAppEx);
		}
		LOG_OBJECT.Debug("Exited into setCancelParameterOnAwardWorkflow");
		return lbCancelFlag;
	}
}