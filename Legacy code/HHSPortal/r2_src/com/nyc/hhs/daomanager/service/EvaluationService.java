package com.nyc.hhs.daomanager.service;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSP8Constants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.constants.TransactionConstants;
import com.nyc.hhs.controllers.util.DownloadDBDDocsThread;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.model.AcceptProposalTaskBean;
import com.nyc.hhs.model.AutoCompleteBean;
import com.nyc.hhs.model.DocumentVisibility;
import com.nyc.hhs.model.EvaluationBean;
import com.nyc.hhs.model.EvaluationDetailBean;
import com.nyc.hhs.model.EvaluationFilterBean;
import com.nyc.hhs.model.Evaluator;
import com.nyc.hhs.model.ProposalDetailsBean;
import com.nyc.hhs.model.ProposalFilterBean;
import com.nyc.hhs.model.ProviderBean;
import com.nyc.hhs.model.ScoreDetailsBean;
import com.nyc.hhs.rule.Rule;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This service class will get the method calls from controller through
 * transaction layer. Execute queries by calling mapper and return query output
 * back to controller. If any error exists, wrap the exception into Application
 * Exception and throw it to controller.
 */
public class EvaluationService extends ServiceState
{
	/**
	 * This is a log object used to log any exception into log file.
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(EvaluationService.class);

	/**
	 * <p>
	 * This method fetches Evaluation score Details corresponding to a proposal
	 * Id and procurement id
	 * <ul>
	 * <li>1. Add multiple input parameters to a Map</li>
	 * <li>2. Fetch Evaluation score Details for the provided Proposal Id and
	 * procurement id using <b>fetchProviderEvaluationScores</b></li>
	 * <li>This method was added in R4</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param loProposalDetails - Proposal Details map
	 * @return loEvalutionBeanList - List list of evaluation scores
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<EvaluationBean> fetchEvaluationScores(SqlSession aoMybatisSession, Map<String, String> loProposalDetails)
			throws ApplicationException
	{
		List<EvaluationBean> loEvalutionBeanList = null;
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.ALL_EVALUATORS_MAP, loProposalDetails);
		try
		{
			loEvalutionBeanList = (List<EvaluationBean>) DAOUtil.masterDAO(aoMybatisSession, loProposalDetails,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_PROVIDER_EVAL_SCORE,
					HHSConstants.JAVA_UTIL_MAP);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoExp)
		{
			aoExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while finding the evaluation provider score", aoExp);
			setMoState("Error while getting Evaluation Scores");
			throw aoExp;
		}
		// handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while finding the evaluation provider score", aoExp);
			setMoState("Error while getting Evaluation Scores");
			throw new ApplicationException("Error while finding the evaluation provider score" + loProposalDetails,
					aoExp);
		}
		return loEvalutionBeanList;
	}

	/**
	 * <p>
	 * This method fetches Evaluation score Details corresponding to a proposal
	 * Id and procurement id
	 * <ul>
	 * <li>1. Add multiple input parameters to a Map</li>
	 * <li>2. Fetch Evaluation score Details for the provided Proposal Id and
	 * procurement id using <b>fetchProviderEvaluationScores</b></li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoHmRequiredProps - Map containing key values procurement
	 *            Id,Proposal Id, workFlow id
	 * @param asWobNumber - Wob Number
	 * @return loEvalutionBeanList - list of evaluation scores
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("rawtypes")
	public List<EvaluationBean> fetchEvaluationReviewScores(SqlSession aoMybatisSession, HashMap aoHmRequiredProps,
			String asWobNumber) throws ApplicationException
	{
		List<EvaluationBean> loEvalutionBeanList = null;
		Map<String, String> loProposalDetails = new HashMap<String, String>();
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.WOB_NUM, asWobNumber);

		if (aoHmRequiredProps != null)
		{
			try
			{
				HashMap loProcurementMap = (HashMap) aoHmRequiredProps.get(asWobNumber);
				if (null != loProcurementMap)
				{
					loProposalDetails.put(HHSConstants.PROCUREMENT_ID_KEY,
							(String) loProcurementMap.get(P8Constants.PE_WORKFLOW_PROCUREMENT_ID));
					loProposalDetails.put(HHSConstants.PROPOSAL_ID_KEY,
							(String) loProcurementMap.get(P8Constants.PE_WORKFLOW_PROPOSAL_ID));
					loEvalutionBeanList = fetchEvaluationScores(aoMybatisSession, loProposalDetails);
				}
			}

			// Any Exception from DAO class will be thrown as Application
			// Exception
			// which will be handles over here. It throws Application Exception
			// back
			catch (ApplicationException aoExp)
			{
				aoExp.setContextData(loContextDataMap);
				LOG_OBJECT.Error("Error while finding the evaluation provider score", aoExp);
				setMoState("Error while getting Evaluation Scores");
				throw aoExp;
			}
			// handling exception other than Application Exception.
			catch (Exception aoExp)
			{
				LOG_OBJECT.Error("Error while finding the evaluation provider score", aoExp);
				setMoState("Error while getting Evaluation Scores");
				throw new ApplicationException("Error while getting Evaluation Scores" + asWobNumber, aoExp);
			}
		}
		// Fix for Defect-6922
		setScoreCriteraToEscapeHtml(loEvalutionBeanList);
		return loEvalutionBeanList;
	}

	/**
	 * This method converts the special characters to view on UI, while fetching
	 * from DB
	 * @param loEvalutionBeanList
	 */
	private void setScoreCriteraToEscapeHtml(List<EvaluationBean> loEvalutionBeanList)
	{
		for (EvaluationBean loEvaluationBean : loEvalutionBeanList)
		{
			loEvaluationBean.setScoreCriteria(StringEscapeUtils.escapeHtml(loEvaluationBean.getScoreCriteria()));
		}
	}

	/**
	 * This method is getting called when user is trying to find the Evaluation
	 * Review Scores
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. Add input parameters to map</li>
	 * <li>2. Get evaluation review score count using
	 * <b>getEvaluationReviewScore</b> from evaluation mapper</li>
	 * <li>3. Get review score count using <b>fetchReviewScoreCount</b> from
	 * evaluation mapper</li>
	 * <li>4. Fetches evaluator count using <b>fetchEvaluatorCount</b> from
	 * evaluation mapper</li>
	 * <li>5. Evaluate Flag based on evaluator count and review score count</li>
	 * </ul>
	 * 
	 * Change: Added check for competition pool
	 * 
	 * Changed By: Pallavi
	 * 
	 * Change Date: 31 Dec 2013
	 * 
	 * @param aoMybatisSession mybatis session to connect with the database
	 * @param asProcurementId procurement id for which evaluation settings
	 *            getting saved
	 * @param asEvaluationPoolMappingId Evaluation Pool Mapping Id
	 * @param asEvaluationSent Evaluation Sent
	 * @return loIsEvaluationReviewSend - the boolean value whether evaluation
	 *         review has been sent or not
	 * @throws ApplicationException when errors occurs
	 */
	public Boolean getEvaluationReviewScore(SqlSession aoMybatisSession, String asProcurementId,
			String asEvaluationPoolMappingId, String asEvaluationSent) throws ApplicationException
	{
		Boolean loIsEvaluationReviewSend = Boolean.FALSE;
		Map<String, String> loDataMap = new HashMap<String, String>();
		loDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		loDataMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
		try
		{
			// find the evaluation task has been send or not
			Integer loEvaluationReviewCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loDataMap,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.GET_EVALUATION_REVIEW_SCORE,
					HHSConstants.JAVA_UTIL_MAP);
			if (asEvaluationSent != null && asEvaluationSent.equalsIgnoreCase(HHSConstants.ONE))
			{
				Integer loReviewScoreCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loDataMap,
						HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_REVIEW_SCORE_COUNT,
						HHSConstants.JAVA_UTIL_MAP);
				// Review Scores has not been generated
				Integer loEvaluatorCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loDataMap,
						HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_EVALUATOR_COUNT,
						HHSConstants.JAVA_UTIL_MAP);
				if (loEvaluatorCount > 0)
				{
					if (loReviewScoreCount == HHSConstants.INT_ZERO)
					{
						loIsEvaluationReviewSend = Boolean.TRUE;
					}
				}
				else
				{
					loIsEvaluationReviewSend = Boolean.FALSE;
				}
			}
			else
			// if count is greater then 0 i.e. evaluation has been sent
			if (loEvaluationReviewCount > HHSConstants.INT_ZERO)
			{
				loIsEvaluationReviewSend = Boolean.TRUE;
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoException)
		{
			setMoState("Error while finding the evaluation review task has been send  for procurement id"
					+ asProcurementId);
			aoException.setContextData(loDataMap);
			LOG_OBJECT.Error("Error while finding the evaluation review task has been send for procurement id"
					+ asProcurementId, aoException);
			throw aoException;
		}
		/**
		 * Any Exception from DAO class will be handles over here. It throws
		 * Application Exception back to Controllers calling method through
		 * Transaction framework
		 */
		catch (Exception aoExp)
		{
			setMoState("Error while finding the evaluation review task has been send  for procurement id"
					+ asProcurementId);
			LOG_OBJECT.Error("Error while finding the evaluation review task has been send for procurement id"
					+ asProcurementId, aoExp);
			throw new ApplicationException(
					"Error while finding the evaluation review task has been send for procurement id" + asProcurementId,
					aoExp);
		}
		return loIsEvaluationReviewSend;
	}

	/**
	 * This method is getting called when user trying to find whether evaluation
	 * task has been send or not.
	 * 
	 * <ul>
	 * <li>1.Add input parameters to map</li>
	 * <li>2.The Query <b>findEvaluationTaskSent</b> is executed from evaluation
	 * mapper to fetch evaluation task sent flag</li>
	 * <li>3.If count is greater than 0, return true</li>
	 * </ul>
	 * 
	 * Change: Updated query for competition pool
	 * 
	 * Changed By: Pallavi
	 * 
	 * Change Date: 2 Jan 2014
	 * 
	 * @param aoMybatisSession my batis session to connect with the database
	 * @param asProcurementId - procurement id
	 * @param asEvaluationPoolMappingId - evaluation group mapping id
	 * @return loIsEvaluationSend - the boolean value whether evaluation has
	 *         been sent or not
	 * @throws ApplicationException when ApplicationException occurs
	 */
	public Boolean findEvaluationTaskSent(SqlSession aoMybatisSession, final String asProcurementId,
			final String asEvaluationPoolMappingId) throws ApplicationException
	{
		Boolean loIsEvaluationSend = Boolean.FALSE;
		Map<String, String> loDataMap = new HashMap<String, String>();
		loDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		loDataMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
		try
		{
			// find the evaluation task has been send or not
			Integer loEvaluationCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loDataMap,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FIND_EVAL_TASK_SEND,
					HHSConstants.JAVA_UTIL_MAP);
			// if count is greater then 0 i.e. evaluation has been sent
			if (loEvaluationCount > HHSConstants.INT_ZERO)
			{
				loIsEvaluationSend = Boolean.TRUE;
			}
			setMoState("Successfully fetched evaluation task sent flag for evaluation pool mapping id:"
					+ asEvaluationPoolMappingId);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoException)
		{
			setMoState("Error while finding the evaluation task has been send  for evaluation pool mapping id:"
					+ asEvaluationPoolMappingId);
			aoException.setContextData(loDataMap);
			LOG_OBJECT.Error("Error while finding the evaluation task has been send evaluation pool mapping id:"
					+ asEvaluationPoolMappingId, aoException);
			throw aoException;
		}
		/**
		 * Any Exception from DAO class will be handles over here. It throws
		 * Application Exception back to Controllers calling method through
		 * Transaction framework
		 */
		catch (Exception aoExp)
		{
			setMoState("Error while finding the evaluation task has been send  for evaluation pool mapping id:"
					+ asEvaluationPoolMappingId);
			LOG_OBJECT.Error("Error while finding the evaluation task has been send for evaluation pool mapping id:"
					+ asEvaluationPoolMappingId, aoExp);
			throw new ApplicationException(
					"Error while finding the evaluation task has been send  for evaluation pool mapping id:"
							+ asEvaluationPoolMappingId, aoExp);
		}
		return loIsEvaluationSend;
	}

	/**
	 * This method is used to find the number of evaluators for a given
	 * evaluation pool mapping id
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Add input parameters to map</li>
	 * <li>The Query <b>getEvaluationCount</b> is executed from evaluation
	 * mapper to fetch evaluator count</li>
	 * </ul>
	 * 
	 * Change: Updated query for competition pool
	 * 
	 * Changed By: Pallavi
	 * 
	 * Change Date: 2 Jan 2014
	 * 
	 * @param aoMybatisSession my batis session to connect with the database
	 * @param asEvaluationPoolMappingId - evaluation pool mapping id
	 * @param aoIsEvaluationSend boolean variable when task already sent or not
	 * @return count of evaluator - integer
	 * @throws ApplicationException when ApplicationException occurs
	 */
	public Integer getEvaluationCount(SqlSession aoMybatisSession, final String asEvaluationPoolMappingId,
			final Boolean aoIsEvaluationSend) throws ApplicationException
	{
		Integer loEvaluatorCountOld = HHSConstants.INT_ZERO;
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
		try
		{
			// If evaluation tasks have already been sent
			if (aoIsEvaluationSend)
			{
				loEvaluatorCountOld = (Integer) DAOUtil.masterDAO(aoMybatisSession, asEvaluationPoolMappingId,
						HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.GET_EVAL_COUNT,
						HHSConstants.JAVA_LANG_STRING);
				setMoState("Sucessfully fetched evaluator count for evaluation pool mapping id:"
						+ asEvaluationPoolMappingId);
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoExp)
		{
			setMoState("Error while getting all the evaluators count for Evaluation Pool Mapping Id: "
					+ asEvaluationPoolMappingId);
			aoExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while getting all the evaluators count for Evaluation Pool Mapping Id: ", aoExp);
			throw aoExp;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (Exception aoExp)
		{
			setMoState("Error while getting all the evaluators count for Evaluation Pool Mapping Id: "
					+ asEvaluationPoolMappingId);
			LOG_OBJECT.Error("Error while getting all the evaluators count for Evaluation Pool Mapping Id: ", aoExp);
			throw new ApplicationException(
					"Error while getting all the evaluators count for Evaluation Pool Mapping Id: "
							+ asEvaluationPoolMappingId, aoExp);
		}
		return loEvaluatorCountOld;
	}

	/**
	 * This method is used to get all the internal evaluator from the database
	 * for given evaluation pool mapping id and procurement id
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. Add input parameters to map</li>
	 * <li>2. Get internal evaluator list for given evaluation pool mapping id
	 * and procurement id using <b>getInternalEvaluationsList</b> from
	 * evaluation mapper</li>
	 * </ul>
	 * 
	 * Change: Updated query for competition pool
	 * 
	 * Changed BY: Pallavi
	 * 
	 * Change Date: 2 Jan 2014
	 * 
	 * @param aoMybatisSession my batis session to connect with the database
	 * @param asProcurementId - procurement id
	 * @param asEvaluationPoolMappingId - evaluation pool mapping id
	 * @param aoIsEvaluationSend boolean variable when task already sent or not
	 * @return loInternalEvaluatorList - the list of all the internal evaluators
	 * @throws ApplicationException when ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<Evaluator> getInternalEvaluationsList(SqlSession aoMybatisSession, final String asProcurementId,
			String asEvaluationPoolMappingId, final Boolean aoIsEvaluationSend) throws ApplicationException
	{
		List<Evaluator> loInternalEvaluatorList = null;
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		loContextDataMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
		try
		{
			// If evaluation tasks have already been sent then find the internal
			// evaluation list
			if (aoIsEvaluationSend)
			{
				Map<String, String> loParameterMap = new HashMap<String, String>();
				loParameterMap.put(HHSConstants.PROCUMENET_ID, asProcurementId);
				loParameterMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
				loInternalEvaluatorList = (List<Evaluator>) DAOUtil.masterDAO(aoMybatisSession, loParameterMap,
						HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.GET_INT_EVAL_LIST,
						HHSConstants.JAVA_UTIL_MAP);
				setMoState("Successfully fetched Internal Evaluator list for Evaluation Pool Mapping Id: "
						+ asEvaluationPoolMappingId);
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoException)
		{
			setMoState("Error while getting all the internal evaluators for Evaluation Pool Mapping Id: "
					+ asEvaluationPoolMappingId);
			aoException.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while getting all the internal evaluators for Evaluation Pool Mapping Id: "
					+ asEvaluationPoolMappingId, aoException);
			throw aoException;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (Exception aoException)
		{
			setMoState("Error while getting all the internal evaluators for Evaluation Pool Mapping Id: "
					+ asEvaluationPoolMappingId);
			LOG_OBJECT.Error("Error while getting all the internal evaluators for Evaluation Pool Mapping Id: "
					+ asEvaluationPoolMappingId, aoException);
			throw new ApplicationException(
					"Error while getting all the internal evaluators for Evaluation Pool Mapping Id: "
							+ asEvaluationPoolMappingId, aoException);
		}
		return loInternalEvaluatorList;
	}

	/**
	 * This method is used to get all the external evaluator from the database
	 * for given evaluation pool mapping id and procurement id
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. Add input parameters to map</li>
	 * <li>2. Get external evaluator list for given evaluation pool mapping id
	 * and procurement id using <b>getExternalEvaluationsList</b> from
	 * evaluation mapper</li>
	 * </ul>
	 * 
	 * Change: Updated query for competition pool
	 * 
	 * Changed BY: Pallavi
	 * 
	 * Change Date: 2 Jan 2014
	 * 
	 * @param aoMybatisSession my batis session to connect with the database
	 * @param asProcurementId - procurement id
	 * @param asEvaluationPoolMappingId - evaluation pool mapping id
	 * @param aoIsEvaluationSend boolean variable whether task already sent or
	 *            not
	 * @return the list of all the external evaluator
	 * @throws ApplicationException when ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<Evaluator> getExternalEvaluationsList(SqlSession aoMybatisSession, final String asProcurementId,
			String asEvaluationPoolMappingId, final Boolean aoIsEvaluationSend) throws ApplicationException
	{
		List<Evaluator> loExternalEvaluatorList = null;
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		try
		{
			// If evaluation tasks have already been sent then find the external
			// evaluation list
			if (aoIsEvaluationSend)
			{
				Map<String, String> loProcurementMap = new HashMap<String, String>();
				loProcurementMap.put(HHSConstants.PROCUMENET_ID, asProcurementId);
				loProcurementMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
				loExternalEvaluatorList = (List<Evaluator>) DAOUtil.masterDAO(aoMybatisSession, loProcurementMap,
						HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.GET_EXT_EVAL_LIST,
						HHSConstants.JAVA_UTIL_MAP);
				setMoState("Successfully fetched External Evaluator list for Evaluation Pool Mapping Id: "
						+ asEvaluationPoolMappingId);
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoException)
		{
			setMoState("Error while getting all the external evaluators for Evaluation Pool Mapping Id: "
					+ asEvaluationPoolMappingId);
			aoException.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while getting all the external evaluators for Evaluation Pool Mapping Id: "
					+ asEvaluationPoolMappingId, aoException);
			throw aoException;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (Exception aoException)
		{
			setMoState("Error while getting all the external evaluators for Evaluation Pool Mapping Id: "
					+ asEvaluationPoolMappingId);
			LOG_OBJECT.Error("Error while getting all the external evaluators for Evaluation Pool Mapping Id: "
					+ asEvaluationPoolMappingId, aoException);
			throw new ApplicationException(
					"Error while getting all the external evaluators for Evaluation Pool Mapping Id: "
							+ asEvaluationPoolMappingId, aoException);
		}
		return loExternalEvaluatorList;
	}

	/**
	 * This method is used to update the evaluator count against Evaluation Pool
	 * Mapping Id
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. Add input parameters to map</li>
	 * <li>2. Updates evaluator count for given evaluation pool mapping id using
	 * <b>updateEvaluatorCount</b> from evaluation mapper</li>
	 * </ul>
	 * 
	 * Change: Updated query for competition pool Changes as a part of release
	 * 3.6.0 for enhancement request 5905 Changed By: Pallavi
	 * 
	 * Change Date: 2 Jan 2014
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asEvaluationPoolMappingId - string representation of Evaluation
	 *            Pool Mapping Id
	 * @param aoEvaluatorsCountNew - evaluator count needs to save
	 * @param aoIsEvaluationSend - boolean status flag
	 * @param aoInputMap - an object of type Map<String, String>
	 * @return loUpdateSuccess success flag
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean updateEvaluatorCount(SqlSession aoMybatisSession, final String asEvaluationPoolMappingId,
			final Integer aoEvaluatorsCountNew, final Boolean aoIsEvaluationSend, Map<String, String> aoInputMap)
			throws ApplicationException
	{
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
		Map<String, String> loInputParam = new LinkedHashMap<String, String>();
		Boolean loUpdate = Boolean.TRUE;
		Boolean loUpdateSuccess = Boolean.FALSE;
		try
		{
			loInputParam.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
			loInputParam.put(HHSConstants.EVAL_COUNT_NEW, aoEvaluatorsCountNew.toString());
			if (aoInputMap != null && aoInputMap.containsKey(HHSConstants.MORE_EVALUATOR_ERROR_MESSAGE))
			{
				loUpdate = Boolean.FALSE;
			}
			if (loUpdate)
			{
				DAOUtil.masterDAO(aoMybatisSession, loInputParam, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
						HHSConstants.UPDATE_EVALUATOR_COUNT, HHSConstants.JAVA_UTIL_MAP);
				// Start || Changes as a part of release 3.6.0 for enhancement
				// request 5905
				loUpdateSuccess = Boolean.TRUE;
				// End || Changes as a part of release 3.6.0 for enhancement
				// request 5905
			}
			setMoState("Successfully updated evaluator count for evaluation pool mapping Id:"
					+ asEvaluationPoolMappingId);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoExp)
		{
			setMoState("Error while updating evaluator count based on evaluation pool mapping id: "
					+ asEvaluationPoolMappingId);
			aoExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while updating evaluator count based on evaluation pool mapping id", aoExp);
			throw aoExp;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (Exception aoExp)
		{
			setMoState("Error while updating evaluator count based on evaluation pool mapping id: "
					+ asEvaluationPoolMappingId);
			LOG_OBJECT.Error("Error while updating evaluator count based on evaluation pool mapping id", aoExp);
			throw new ApplicationException("Error while updating evaluator count based on evaluation pool mapping id: "
					+ asEvaluationPoolMappingId, aoExp);
		}
		// Start || Changes as a part of release 3.6.0 for enhancement request
		// 5905
		return loUpdateSuccess;
		// End || Changes as a part of release 3.6.0 for enhancement request
		// 5905
	}

	/**
	 * This method is getting called when user trying to find whether evaluation
	 * task has been send or not.Also gives evalauation progress flag
	 * 
	 * <ul>
	 * <li>1.Add input parameters to map</li>
	 * <li>2.The Query <b>getEvalProgressStatus</b> is executed from evaluation
	 * mapper to fetch evaluation task sent and evaluation progress flag</li>
	 * </ul>
	 * 
	 * This method is added as a part of Release 3.6.0 for enhancement 5905
	 * 
	 * @param aoMybatisSession my batis session to connect with the database
	 * @param asProcurementId - procurement id
	 * @param asEvaluationPoolMappingId - evaluation group mapping id
	 * @return loEvalSendFlag - the boolean value whether evaluation has been
	 *         sent or not
	 * @throws ApplicationException when ApplicationException occurs
	 */
	public Boolean getEvalProgressStatus(SqlSession aoMybatisSession, final String asProcurementId,
			final String asEvaluationPoolMappingId) throws ApplicationException
	{
		// Boolean loIsEvaluationSend = Boolean.FALSE;
		Map<String, String> loDataMap = new HashMap<String, String>();
		loDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		loDataMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
		Boolean loEvalSendFlag = Boolean.FALSE;
		String loEvaluationSent;
		String loEvaluationProgress;
		try
		{
			// find the evaluation task has been send or not
			Map<String, String> loEvaluationProgressMap = (Map<String, String>) DAOUtil.masterDAO(aoMybatisSession,
					loDataMap, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.GET_EVAL_PROGRESS_STATUS,
					HHSConstants.JAVA_UTIL_MAP);
			// if count is greater then 0 i.e. evaluation has been sent
			if (null != loEvaluationProgressMap)
			{
				loEvaluationSent = loEvaluationProgressMap.get(HHSConstants.EVALUATION_SENT_VAL);
				loEvaluationProgress = loEvaluationProgressMap.get(HHSConstants.EVALUATION_PROGRESS);
				if (null != loEvaluationSent && loEvaluationSent.equals(HHSConstants.ONE))
				{
					if (null != loEvaluationProgress && loEvaluationProgress.equals(HHSConstants.ONE))
					{
						throw new ApplicationException(
								"This information is currently being saved. Please allow a few minutes for this to be processed.");
					}
					else
					{
						loDataMap.put(HHSConstants.EVAL_PROGRESS_FLAG, HHSConstants.ONE);
						DAOUtil.masterDAO(aoMybatisSession, loDataMap, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
								HHSConstants.UPDATE_EVAL_PROGRESS_STATUS, HHSConstants.JAVA_UTIL_MAP);
					}
					loEvalSendFlag = Boolean.TRUE;
				}
			}
			setMoState("Successfully fetched evaluation progress status for evaluation pool mapping id:"
					+ asEvaluationPoolMappingId);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoException)
		{
			setMoState("Error while fetching the evaluation progress status for evaluation pool mapping id:"
					+ asEvaluationPoolMappingId);
			aoException.setContextData(loDataMap);
			LOG_OBJECT.Error("Error while fetching the evaluation progress status for evaluation pool mapping id:"
					+ asEvaluationPoolMappingId, aoException);
			throw aoException;
		}
		/**
		 * Any Exception from DAO class will be handles over here. It throws
		 * Application Exception back to Controllers calling method through
		 * Transaction framework
		 */
		catch (Exception aoExp)
		{
			setMoState("Error while fetching the evaluation progress status for evaluation pool mapping id:"
					+ asEvaluationPoolMappingId);
			LOG_OBJECT.Error("Error while fetching the evaluation progress status for evaluation pool mapping id:"
					+ asEvaluationPoolMappingId, aoExp);
			throw new ApplicationException(
					"Error while fetching the evaluation progress status for evaluation pool mapping id:"
							+ asEvaluationPoolMappingId, aoExp);
		}
		return loEvalSendFlag;
	}

	/**
	 * This method is used to update the evaluation progress flag against
	 * Evaluation Pool Mapping Id
	 * 
	 * <ul>
	 * <li>1. Add input parameters to map</li>
	 * <li>2. Updates evaluation progress flag for given evaluation pool mapping
	 * id using <b>updateEvalProgressStatus</b> from evaluation mapper</li>
	 * </ul>
	 * 
	 * This method is added as a part of Release 3.6.0 for enhancement 5905
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asEvaluationPoolMappingId - string representation of Evaluation
	 *            Pool Mapping Id
	 * @param aoEvaluatorsCountNew - evaluator count needs to save
	 * @param aoUpdateFlag - represents evaluation progress status
	 * @param aoIsEvaluationSend - Evaluation sent flag
	 * @return loUpdate boolean update flag
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean updateEvalProgressStatus(SqlSession aoMybatisSession, final String asEvaluationPoolMappingId)
			throws ApplicationException
	{
		LOG_OBJECT
				.Debug("Entering updateEvalProgressStatus::Updating evaluation progress status for evaluation pool mapping id::: "
						+ asEvaluationPoolMappingId);
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
		Map<String, String> loInputParam = new LinkedHashMap<String, String>();
		Boolean loUpdate = Boolean.FALSE;
		try
		{
			loInputParam.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
			loInputParam.put(HHSConstants.EVAL_PROGRESS_FLAG, HHSConstants.STRING_ZERO);
			DAOUtil.masterDAO(aoMybatisSession, loInputParam, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
					HHSConstants.UPDATE_EVAL_PROGRESS_STATUS, HHSConstants.JAVA_UTIL_MAP);
			loUpdate = Boolean.TRUE;
			setMoState("Successfully updated evaluation progress status for evaluation pool mapping Id:"
					+ asEvaluationPoolMappingId);
			LOG_OBJECT
					.Debug("Exiting updateEvalProgressStatus::Successfully updated evaluation progress status for evaluation pool mapping id::: "
							+ asEvaluationPoolMappingId);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoExp)
		{
			setMoState("Error while updating evaluation progress status based on evaluation pool mapping id: "
					+ asEvaluationPoolMappingId);
			aoExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while updating evaluation progress status based on evaluation pool mapping id",
					aoExp);
			throw aoExp;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (Exception aoExp)
		{
			setMoState("Error while updating evaluation progress status based on evaluation pool mapping id: "
					+ asEvaluationPoolMappingId);
			LOG_OBJECT.Error("Error while updating evaluation progress status based on evaluation pool mapping id",
					aoExp);
			throw new ApplicationException(
					"Error while updating evaluation progress status based on evaluation pool mapping id: "
							+ asEvaluationPoolMappingId, aoExp);
		}
		return loUpdate;
	}

	/**
	 * This method is used to determine whether all evlauations for a Evaluation
	 * Pool Mapping Id are finished or not
	 * 
	 * <ul>
	 * <li>1. Add input parameters to map</li>
	 * <li>2. FEtches evaluation progress status flag for given evaluation pool
	 * mapping id using <b>getAllEvalProgressStatusFlag</b> from evaluation
	 * mapper</li>
	 * </ul>
	 * 
	 * This method is added as a part of Release 3.6.0 for enhancement 5905
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asEvaluationPoolMappingId - string representation of Evaluation
	 *            Pool Mapping Id
	 * @return loEvalFlag boolean update flag
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean getAllEvalProgressStatusFlag(SqlSession aoMybatisSession, final String asEvaluationPoolMappingId)
			throws ApplicationException
	{
		LOG_OBJECT
				.Debug("Entering getAllEvalProgressStatusFlag::fetching evaluation progress status flag for evaluation pool mapping id::: "
						+ asEvaluationPoolMappingId);
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
		Map<String, String> loInputParam = new LinkedHashMap<String, String>();
		Boolean loEvalFlag = Boolean.FALSE;
		try
		{
			loInputParam.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
			Integer loEvalCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loInputParam,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSP8Constants.GET_EVAL_PROGRESS_STATUS_FLAG,
					HHSConstants.JAVA_UTIL_MAP);
			if (loEvalCount > HHSConstants.INT_ZERO)
			{
				loEvalFlag = Boolean.TRUE;
			}
			setMoState("Successfully fetched evaluation progress status flag for evaluation pool mapping Id:"
					+ asEvaluationPoolMappingId);
			LOG_OBJECT
					.Debug("Exiting getAllEvalProgressStatusFlag::Successfully fetched evaluation progress status flag for evaluation pool mapping id::: "
							+ asEvaluationPoolMappingId);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoExp)
		{
			setMoState("Error while fetching evaluation progress status flag based on evaluation pool mapping id: "
					+ asEvaluationPoolMappingId);
			aoExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error(
					"Error while fetching evaluation progress status flag based on evaluation pool mapping id", aoExp);
			throw aoExp;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (Exception aoExp)
		{
			setMoState("Error while fetching evaluation progress status flag based on evaluation pool mapping id: "
					+ asEvaluationPoolMappingId);
			LOG_OBJECT.Error(
					"Error while fetching evaluation progress status flag based on evaluation pool mapping id", aoExp);
			throw new ApplicationException(
					"Error while fetching evaluation progress status flag based on evaluation pool mapping id: "
							+ asEvaluationPoolMappingId, aoExp);
		}
		return loEvalFlag;
	}

	/**
	 * This method used when user tries to save the evaluation setting from the
	 * 214 screen when
	 * <ul>
	 * <li>
	 * If at least 3 evaluators have been chosen</li>
	 * <li>
	 * If evaluation tasks have already been sent, If the number of evaluators
	 * is more than the number of evaluators who were sent tasks</li>
	 * <li>
	 * If evaluation tasks have already been sent, If the number of evaluators
	 * is equal to the number of evaluators who were sent tasks</li>
	 * <li>
	 * If evaluation tasks have already been sent, If the number of evaluators
	 * is less than the number of evaluators who were sent tasks</li>
	 * </ul>
	 * 
	 * Change: updated query for competition pool
	 * 
	 * Changed By: Pallavi
	 * 
	 * Change Date: 2 Jan 2014
	 * 
	 * @param aoMybatisSession my batis session to connect with the database
	 * @param aoParamMap input parameters contains all the input parameters
	 *            coming from the front end
	 * @param asProcurementId procurement id for which evaluation settings
	 *            getting saved
	 * @param aoEvaluatorsCountNew count of the new evaluators coming from the
	 *            front end
	 * @param aoIsEvaluationSend boolean variable when task already sent or not
	 * @param aoEvaluatorsCountOld count of the old evaluators coming from the
	 *            database
	 * @param aoInternalEvaluationsList internal evaluation list coming from
	 *            database
	 * @param aoExternalEvaluationsList external evaluation list coming from
	 *            database
	 * @param aoAllEvaluatorsMap all evaluator map id coming from front end
	 * @param asEvaluationPoolMappingId - evaluation pool mapping id
	 * @return the map of all output parameters after saving the evaluators
	 * @throws ApplicationException if any Exception occurred
	 */
	public Map<String, String> saveEvaluationDetails(SqlSession aoMybatisSession,
			final Map<String, List<Evaluator>> aoParamMap, final String asProcurementId,
			final Integer aoEvaluatorsCountNew, final Boolean aoIsEvaluationSend, final Integer aoEvaluatorsCountOld,
			final List<Evaluator> aoInternalEvaluationsList, final List<Evaluator> aoExternalEvaluationsList,
			final Map<String, Evaluator> aoAllEvaluatorsMap, final String asEvaluationPoolMappingId)
			throws ApplicationException
	{
		Boolean loMoreEvaluator = Boolean.FALSE;
		Map<String, String> loReturnMap = new LinkedHashMap<String, String>();
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		loContextDataMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
		try
		{
			if (aoParamMap != null && !aoParamMap.isEmpty())
			{
				// check if evaluation send
				LOG_OBJECT.Debug("aoIsEvaluationSend:::: " + aoIsEvaluationSend);
				LOG_OBJECT.Debug("aoEvaluatorsCountNew:::: " + aoEvaluatorsCountNew);
				LOG_OBJECT.Debug("aoEvaluatorsCountOld:::: " + aoEvaluatorsCountOld);
				if (aoIsEvaluationSend)
				{
					// If the number of evaluators is more than the number of
					// evaluators who were sent tasks
					if (aoEvaluatorsCountNew > aoEvaluatorsCountOld)
					{
						loReturnMap.put(HHSConstants.OLD_EVALUATOR_COUNT, aoEvaluatorsCountOld.toString());
						loReturnMap.put(HHSConstants.NEW_EVALUATOR_COUNT, aoEvaluatorsCountNew.toString());
						loReturnMap.put(HHSConstants.MORE_EVALUATOR_ERROR_MESSAGE, HHSConstants.TRUE);
						loMoreEvaluator = Boolean.TRUE;
					}
					// If the number of evaluators is equal to the number of
					// evaluators who were sent tasks
					else if (aoEvaluatorsCountNew.equals(aoEvaluatorsCountOld))
					{
						// replaced evaluator map
						isEvaluatorsReplaced(aoMybatisSession, aoInternalEvaluationsList, aoExternalEvaluationsList,
								aoAllEvaluatorsMap, aoParamMap, asProcurementId, asEvaluationPoolMappingId);
					}
					// If the number of evaluators is less than the number of
					// evaluators who were sent tasks
					else if (aoEvaluatorsCountNew < aoEvaluatorsCountOld)
					{
						// removed evaluator list
						isEvaluatorsReplaced(aoMybatisSession, aoInternalEvaluationsList, aoExternalEvaluationsList,
								aoAllEvaluatorsMap, aoParamMap, asProcurementId, asEvaluationPoolMappingId);
						loReturnMap.put(HHSConstants.AFTER_EVAL_LESS_COUNT, HHSConstants.TRUE);
					}
				}
				if (!loMoreEvaluator && !aoIsEvaluationSend)
				{
					insertEvaluatorDetails(aoMybatisSession, aoParamMap, asProcurementId, asEvaluationPoolMappingId);
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
			setMoState("Error while saving the evaluation setting for procurment id: " + asProcurementId);
			aoExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while saving the evaluation setting ", aoExp);
			throw aoExp;
		}
		/**
		 * Any Exception from DAO class will be handles over here. It throws
		 * Application Exception back to Controllers calling method through
		 * Transaction framework
		 */
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Error while saving the evaluation setting", aoAppEx);
			setMoState("Transaction Failed:: saveEvaluationDetails method - saving the evaluation setting");
			throw new ApplicationException("Error while saving the evaluation setting", aoAppEx);
		}
		return loReturnMap;
	}

	/**
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * This method is used to insert and delete the evaluator information from
	 * the internal and external tables before the evaluation not started. When
	 * InternalEvaluatorList is not null and empty deleteEvaluationInternal
	 * query is executed otherwise deleteEvaluationSettingsInternal is executed.
	 * When ExternalEvaluatorList is not null and empty deleteEvaluationExternal
	 * is executed otherwise deleteEvaluationSettingsExternal is executed. On
	 * the save of internal evaluator <b>saveInternalEvaluationDetails</b> is
	 * executed. On the save of External
	 * evaluator<b>saveExternalEvaluationDetails</b> is executed.
	 * 
	 * Change: updated queries for competition pool
	 * 
	 * Changed By: Pallavi
	 * 
	 * Change Date: 2 Jan 2014
	 * 
	 * @param aoMybatisSession - mybatis session
	 * @param aoParamMap - input parameter map
	 * @param asProcurementId - procurement id
	 * @param asEvaluationPoolMappingId - evaluation pool mapping id
	 * @throws ApplicationException If ApplicationException occurs
	 */
	private void insertEvaluatorDetails(SqlSession aoMybatisSession, final Map<String, List<Evaluator>> aoParamMap,
			final String asProcurementId, final String asEvaluationPoolMappingId) throws ApplicationException
	{
		Map<String, Object> loDeleteEvaluatorMap = new HashMap<String, Object>();
		List<Evaluator> loInternalEvaluatorList = aoParamMap.get(HHSConstants.INTERNAL_LIST);
		List<Evaluator> loExternalEvaluatorList = aoParamMap.get(HHSConstants.EXTERNAL_LIST);
		loDeleteEvaluatorMap.put(HHSConstants.INT_EVAL_LIST, loInternalEvaluatorList);
		loDeleteEvaluatorMap.put(HHSConstants.EXT_EVAL_LIST, loExternalEvaluatorList);
		loDeleteEvaluatorMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		loDeleteEvaluatorMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
		try
		{
			// delete the evaluators
			if (loInternalEvaluatorList != null && !loInternalEvaluatorList.isEmpty())
			{
				DAOUtil.masterDAO(aoMybatisSession, loDeleteEvaluatorMap, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
						HHSConstants.DEL_EVAL_INT, HHSConstants.JAVA_UTIL_MAP);
			}
			else
			{
				DAOUtil.masterDAO(aoMybatisSession, loDeleteEvaluatorMap, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
						HHSConstants.DELETE_EVALUATION_SETTINGS_INTERNAL, HHSConstants.JAVA_UTIL_MAP);
			}
			if (loExternalEvaluatorList != null && !loExternalEvaluatorList.isEmpty())
			{
				DAOUtil.masterDAO(aoMybatisSession, loDeleteEvaluatorMap, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
						HHSConstants.DEL_EVAL_EXT, HHSConstants.JAVA_UTIL_MAP);
			}
			else
			{
				DAOUtil.masterDAO(aoMybatisSession, loDeleteEvaluatorMap, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
						HHSConstants.DELETE_EVALUATION_SETTINGS_EXTERNAL, HHSConstants.JAVA_UTIL_MAP);
			}
			insertEvaluators(aoMybatisSession, aoParamMap);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoExp)
		{
			aoExp.setContextData(loDeleteEvaluatorMap);
			setMoState("Error while saving the evaluation setting for Evaluation Pool Mapping Id: "
					+ asEvaluationPoolMappingId);
			LOG_OBJECT.Error("Error while saving the evaluation setting for Evaluation Pool Mapping Id: "
					+ asEvaluationPoolMappingId, aoExp);
			throw aoExp;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (Exception aoExp)
		{
			setMoState("Error while saving the evaluation setting for Evaluation Pool Mapping Id: "
					+ asEvaluationPoolMappingId);
			LOG_OBJECT.Error("Error while saving the evaluation setting for Evaluation Pool Mapping Id: "
					+ asEvaluationPoolMappingId, aoExp);
			throw new ApplicationException("Error while saving the evaluation setting for Evaluation Pool Mapping Id: "
					+ asEvaluationPoolMappingId, aoExp);
		}
	}

	/**
	 * This method is used to called when user are trying to save the evaluator
	 * settings
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>
	 * This method getting called when evaluation tasks have already been sent</li>
	 * <li>
	 * If the number of evaluators is equal to the number of evaluators who were
	 * sent tasks</li>
	 * <li>Execute queries <b> saveInternalEvaluationDetails </b> and
	 * <b>updateEvaluatorExternalAfterEvaluation </b></li>
	 * <li>Execute queries to save <b> saveInternalEvaluationDetails </b> and
	 * <b> updateEvaluatorExternalAfterEvaluation</b></li>
	 * </ul>
	 * 
	 * Change: updated query for competition pool
	 * 
	 * Changed By: Pallavi
	 * 
	 * Change Date: 2 Jan 2014
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoInternalEvaluationsList list of evaluator
	 * @param aoExternalEvaluationsList list of evaluator
	 * @param aoAllEvaluatorsMap map of all evaluators
	 * @param aoParamMap parameters map
	 * @param asProcurementId - procurement id
	 * @param asEvaluationPoolMappingId - evaluation pool mapping id
	 */
	private void isEvaluatorsReplaced(SqlSession aoMybatisSession, List<Evaluator> aoInternalEvaluationsList,
			List<Evaluator> aoExternalEvaluationsList, Map<String, Evaluator> aoAllEvaluatorsMap,
			Map<String, List<Evaluator>> aoParamMap, String asProcurementId, String asEvaluationPoolMappingId)
			throws ApplicationException
	{
		Map<String, Object> loEvaluatorReplacedMap = new LinkedHashMap<String, Object>();
		List<Evaluator> loInternalEvaluatorList = new ArrayList<Evaluator>();
		List<Evaluator> loExternalEvaluatorList = new ArrayList<Evaluator>();
		Map<String, Object> loDeleteEvaluatorMap = new HashMap<String, Object>();
		loDeleteEvaluatorMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		loDeleteEvaluatorMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
		LOG_OBJECT.Debug("aoAllEvaluatorsMap Values:::: " + aoAllEvaluatorsMap);
		LOG_OBJECT.Debug("aoInternalEvaluationsList Values:::: " + aoInternalEvaluationsList.toString());
		LOG_OBJECT.Debug("aoExternalEvaluationsList Values:::: " + aoExternalEvaluationsList.toString());
		try
		{
			if (aoInternalEvaluationsList != null && !aoInternalEvaluationsList.isEmpty())
			{
				for (Evaluator loInternalEvaluators : aoInternalEvaluationsList)
				{
					if (!aoAllEvaluatorsMap.containsKey(loInternalEvaluators.getAgencyId()
							.concat(HHSConstants.DELIMETER_SIGN)
							.concat(loInternalEvaluators.getInternalEvaluatorName())))
					{
						loInternalEvaluatorList.add(loInternalEvaluators);
					}
				}
			}
			if (aoExternalEvaluationsList != null && !aoExternalEvaluationsList.isEmpty())
			{
				for (Evaluator loExternalEvaluators : aoExternalEvaluationsList)
				{
					if (!aoAllEvaluatorsMap.containsKey(loExternalEvaluators.getExtEvaluatorName()
							.concat(HHSConstants.DELIMETER_SIGN).concat(loExternalEvaluators.getAgencyUserId())))
					{
						loExternalEvaluatorList.add(loExternalEvaluators);
					}
				}
			}
			LOG_OBJECT.Debug("loInternalEvaluatorList Values:::: " + loInternalEvaluatorList.toString());
			LOG_OBJECT.Debug("loExternalEvaluatorList Values:::: " + loExternalEvaluatorList.toString());
			if (loInternalEvaluatorList != null && !loInternalEvaluatorList.isEmpty())
			{
				loEvaluatorReplacedMap.put(HHSConstants.LO_EVALUATOR_INTERNAL_LIST, loInternalEvaluatorList);
				loEvaluatorReplacedMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
				loEvaluatorReplacedMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
				for (Evaluator loInternalEvaluator : loInternalEvaluatorList)
				{
					loInternalEvaluator.setProcurementId(asProcurementId);
					loInternalEvaluator.setEvaluationPoolMappingId(asEvaluationPoolMappingId);
					loInternalEvaluator.setStatus(HHSConstants.D);
					DAOUtil.masterDAO(aoMybatisSession, loInternalEvaluator,
							HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.UPDATE_EVAL_INT_AFTER_EVAL,
							HHSConstants.EVALUATOR_BEAN);
				}
			}
			if (loExternalEvaluatorList != null && !loExternalEvaluatorList.isEmpty())
			{
				loEvaluatorReplacedMap.put(HHSConstants.LO_EVALUATOR_EXTERNAL_LIST, loExternalEvaluatorList);
				loEvaluatorReplacedMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
				loEvaluatorReplacedMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
				for (Evaluator loExternalEvaluator : loExternalEvaluatorList)
				{
					loExternalEvaluator.setProcurementId(asProcurementId);
					loExternalEvaluator.setEvaluationPoolMappingId(asEvaluationPoolMappingId);
					loExternalEvaluator.setStatus(HHSConstants.D);
					DAOUtil.masterDAO(aoMybatisSession, loExternalEvaluator,
							HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.UPDATE_EVAL_EXT_AFTER_EVAL,
							HHSConstants.EVALUATOR_BEAN);
				}
			}
			// insert the evaluators
			insertEvaluators(aoMybatisSession, aoParamMap);
			setMoState("Evaluators Replaced successfully for evaluation pool mapping id:" + asEvaluationPoolMappingId);
		}
		// Any Exception from DAO class will be thrown as Application
		// Exception
		// which will be handles over here.
		catch (ApplicationException aoAppEx)
		{
			setMoState("Transaction Failed:: EvaluationService:isEvaluatorsReplaced method - while replacing the evaluator");
			aoAppEx.setContextData(loEvaluatorReplacedMap);
			LOG_OBJECT.Error("Error was occurred while replacing the evaluator", aoAppEx);
			LOG_OBJECT.Error("Error was occurred while replacing the evaluator", aoAppEx);
			throw aoAppEx;
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error was occurred while replacing the evaluator", aoEx);
			setMoState("Transaction Failed:: EvaluationService:isEvaluatorsReplaced method - replacing the evaluator");
			throw new ApplicationException("Evaluators Replaced successfully for evaluation pool mapping id:"
					+ asEvaluationPoolMappingId, aoEx);
		}
	}

	/**
	 * This method is used to insert evaluators
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Execute query <b> saveInternalEvaluationDetails </b> to save internal
	 * evaluators
	 * <li>Execute query <b> saveExternalEvaluationDetails </b> to save external
	 * evaluators
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoParamMap input param map
	 * @throws ApplicationException If an exception occurs
	 */
	private void insertEvaluators(SqlSession aoMybatisSession, Map<String, List<Evaluator>> aoParamMap)
			throws ApplicationException
	{
		if (aoParamMap.containsKey(HHSConstants.INTERNAL_LIST))
		{
			// save the internal evaluator
			List<Evaluator> loInternalList = aoParamMap.get(HHSConstants.INTERNAL_LIST);
			for (Evaluator loInternalEvaluator : loInternalList)
			{
				LOG_OBJECT.Debug("loInternalEvaluator::::::::::" + loInternalEvaluator);
				loInternalEvaluator.setStatus(HHSConstants.EVALUATION_SETTINGS_INSERT);
				DAOUtil.masterDAO(aoMybatisSession, loInternalEvaluator, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
						HHSConstants.SAVE_EVAL_INT_DETAIL, HHSConstants.EVALUATOR_BEAN);
			}
		}
		if (aoParamMap.containsKey(HHSConstants.EXTERNAL_LIST))
		{
			// save the external evaluators
			List<Evaluator> loExternalList = aoParamMap.get(HHSConstants.EXTERNAL_LIST);
			for (Evaluator loExternalEvaluator : loExternalList)
			{
				LOG_OBJECT.Debug("loExternalEvaluator::::::::::" + loExternalEvaluator);
				loExternalEvaluator.setStatus(HHSConstants.EVALUATION_SETTINGS_INSERT);
				DAOUtil.masterDAO(aoMybatisSession, loExternalEvaluator, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
						HHSConstants.SAVE_EVALUATOR_EXT_DET, HHSConstants.EVALUATOR_BEAN);
			}
		}
	}

	/**
	 * This method is used to get the agency id based on the procurement id from
	 * the procurement table
	 * <ul>
	 * <li>1. Create one Context data HashMap and populate it with procurement
	 * Id</li>
	 * <li>2. Execute query <b>getProcurementAgencyId</b> to fetch the agency Id
	 * and return the same</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProcurementId - string representation of procurement Id
	 * @return loAgencyStatusMap - a map of Agency status
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Map<String, Object> getProcurementAgencyId(SqlSession aoMybatisSession, String asProcurementId)
			throws ApplicationException
	{
		Map<String, Object> loAgencyStatusMap = null;
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		try
		{
			loAgencyStatusMap = (Map<String, Object>) DAOUtil.masterDAO(aoMybatisSession, asProcurementId,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.GET_PROCUREMENT_AGENCY_ID,
					HHSConstants.JAVA_LANG_STRING);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoExp)
		{
			setMoState("Error while getting agency id based on procurement id " + asProcurementId);
			aoExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while getting agency id based on procurement ", aoExp);
			throw aoExp;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error while getting agency id based on procurement ", loExp);
			setMoState("Error while getting agency id based on procurement id " + asProcurementId);
			throw new ApplicationException("Error while getting agency id based on procurement", loExp);
		}
		return loAgencyStatusMap;
	}

	/**
	 * <p>
	 * This method fetches Evaluation score Details corresponding to a proposal
	 * Id and procurement id
	 * <ul>
	 * <li>1. Add multiple input parameters to a Map</li>
	 * <li>2. Fetch header Details for the provided Proposal Id and procurement
	 * id using <b>fetchProviderProposalHeader</b></li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProposalId - string representation of Proposal Id
	 * @return loHeaderMap - a map of Header details
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> fetchProposalDetails(SqlSession aoMybatisSession, String asProposalId)
			throws ApplicationException
	{
		Map<String, String> loHeaderMap = null;
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROPOSAL_ID_KEY, asProposalId);
		try
		{
			loHeaderMap = (Map<String, String>) DAOUtil.masterDAO(aoMybatisSession, asProposalId,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_PROVIER_PROP_HEADER,
					HHSConstants.JAVA_LANG_STRING);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoExp)
		{
			aoExp.setContextData(loContextDataMap);
			setMoState("Error while getting Proposal Header Details");
			LOG_OBJECT.Error("Error while fetching the proposal detail  ", aoExp);
			throw aoExp;
		}
		/**
		 * Any Exception from DAO class will be handles over here. It throws
		 * Application Exception back to Controllers calling method through
		 * Transaction framework
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetching the proposal detail  ", aoExp);
			setMoState("Error while getting Proposal Header Details");
			throw new ApplicationException("Error while fetching the proposal detail  ", aoExp);
		}
		return loHeaderMap;
	}

	/**
	 * <p>
	 * The Method will fetch internal comments ,Audit Date and Agency Name from
	 * database against proposalId
	 * <ul>
	 * <li>1.Connect to database using SqlSession</li>
	 * <li>2.Call the Query [fetchProposalComments]</li>
	 * <li>3. Get List of ProposalFilterBean which contains Date,comments and
	 * Agency Name</li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession SqlSession
	 * @param asProposalId ProposalId
	 * @return loProposalFilterBeanList - List
	 * @throws ApplicationException ,If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<ProposalFilterBean> fetchProposalComments(SqlSession aoMybatisSession, String asProposalId)
			throws ApplicationException
	{
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROPOSAL_ID_KEY, asProposalId);
		List<ProposalFilterBean> loProposalFilterBeanList = null;
		try
		{
			loProposalFilterBeanList = (List<ProposalFilterBean>) DAOUtil.masterDAO(aoMybatisSession, asProposalId,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_PROPOSAL_COMMENTS,
					HHSConstants.JAVA_LANG_STRING);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoExp)
		{
			aoExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while getting proposal comments :", aoExp);
			setMoState("Error while getting proposal comments, proposal id:" + asProposalId);
			throw aoExp;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (Exception aoExp)
		{
			ApplicationException aoAppEx = new ApplicationException("Error while getting proposal comments :", aoExp);
			aoAppEx.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while getting proposal comments :", aoAppEx);
			setMoState("Error while getting proposal comments, proposal id:" + asProposalId);
			throw aoAppEx;
		}
		return loProposalFilterBeanList;
	}

	/**
	 * This method fetches the sorted proposal data and display it on the page.
	 * 
	 * <ul>
	 * <li>1. Execute the query "getEvaluationScores" with asProposalId as a
	 * parameter.</li>
	 * <li>2. Return the EvaluationBean.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession mbais sql session
	 * @param loProposalDetailsBeanList List of Proposal Details Bean
	 * @return loEvalScoreBeanList - a List
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<EvaluationBean> getEvaluationScores(SqlSession aoMybatisSession,
			List<ProposalDetailsBean> loProposalDetailsBeanList) throws ApplicationException
	{
		List<EvaluationBean> loEvalScoreBeanList = null;
		Map<String, List<ProposalDetailsBean>> loProposalIdMap = new HashMap<String, List<ProposalDetailsBean>>();
		loProposalIdMap.put(HHSConstants.BEAN_PROP_DET_LIST, loProposalDetailsBeanList);
		try
		{
			loEvalScoreBeanList = (List<EvaluationBean>) DAOUtil.masterDAO(aoMybatisSession, loProposalIdMap,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.EVAL_SCORE, HHSConstants.JAVA_UTIL_MAP);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error while getting proposal details", aoExp);
			setMoState("Error while getting proposal details");
			throw aoExp;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error while getting proposal details", loExp);
			setMoState("Error while getting proposal details");
			throw new ApplicationException("Error while getting proposal details", loExp);
		}
		return loEvalScoreBeanList;
	}

	/**
	 * This method fetches total evaluations processed for a procurement
	 * 
	 * <ul>
	 * <li>Get evaluation Bean as input</li>
	 * <li>Create the loContextDataMap to hold the context data.</li>
	 * <li>Set the input data in the loContextDataMap.</li>
	 * <li>Call fetchEvaluationResultData() method to get the data from the
	 * Database.</li>
	 * <li>Throw the exception if any occur.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession a SQL session
	 * @param aoEvalBean EvaluationFilterBean holding sorting and filtering data
	 * @param aoEvalAwardReviewStatusBean - EvaluationBean reference
	 * @return a list of EvaluationFilterBean fetched from database
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	public List<EvaluationFilterBean> fetchEvaluationResultsSelections(SqlSession aoMybatisSession,
			EvaluationFilterBean aoEvalBean, EvaluationBean aoEvalAwardReviewStatusBean, Integer aoProcurementStatus)
			throws ApplicationException
	{
		List<EvaluationFilterBean> loEvalResults = null;
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.AO_EVAL_BEAN, aoEvalBean);
		loContextDataMap.put(HHSConstants.ARG_EVAL_AWARD_REVIEW_STATUS_BEAN, aoEvalAwardReviewStatusBean);
		try
		{
			loEvalResults = fetchEvaluationResultData(aoMybatisSession, aoEvalBean, aoEvalAwardReviewStatusBean,
					loEvalResults, aoProcurementStatus);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while getting proposal comments :", aoAppEx);
			setMoState("Error occurred while fetching evaluation Results and Settings:" + aoAppEx.getMessage());
			throw aoAppEx;
		}
		/**
		 * Any Exception from DAO class will be handles over here. It throws
		 * Application Exception back to Controllers calling method through
		 * Transaction framework
		 */
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Error while getting proposal comments :", aoAppEx);
			setMoState("Error occurred while fetching evaluation Results and Settings:" + aoAppEx.getMessage());
			throw new ApplicationException("Error while getting proposal comments :", aoAppEx);
		}
		return loEvalResults;
	}

	/**
	 * This method fetches total evaluations processed for a procurement.
	 * 
	 * <ul>
	 * <li>1. make not null Check for Acco User.</li>
	 * <li>2. if step 1 is true then Check for filter check.</li>
	 * <li>3. if step 2 is true then set validUser true in bean.</li>
	 * <li>4. if check in step 2 is false then make not null Check for
	 * statusIdList.</li>
	 * <li>5. if check in step 3 is false then set required status in the list.</li>
	 * <li>6. if step 1 is false then make not null Check for filter check and
	 * make Check for statusIdList.</li>
	 * <li>7. if check in step 3 is false then set required status in the list.</li>
	 * <li>8. If user is an valid user then call fetchEvalDataForValidUsers()
	 * method to fetch the data.</li>
	 * 
	 * 
	 * <li>Throw the exception if any occur.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - SqlSession
	 * @param aoEvalBean - EvaluationFilterBean holding the sorting and the
	 *            filtering criteria
	 * @param aoEvalAwardReviewStatusBean - EvaluationBean reference
	 * @param aoEvalResults - List of EvaluationFilterBean use to data to be
	 *            fetched from database
	 * @return List - List of EvaluationFilterBean holding the data fetched from
	 *         database
	 * @throws ApplicationException - throws ApplicationException
	 */
	private List<EvaluationFilterBean> fetchEvaluationResultData(SqlSession aoMybatisSession,
			EvaluationFilterBean aoEvalBean, EvaluationBean aoEvalAwardReviewStatusBean,
			List<EvaluationFilterBean> aoEvalResults, Integer aoProcurementStatus) throws ApplicationException
	{
		// If Acco User then fetch the records with Evaluated,Scores
		// Returned,Selected,Not Selected Status

		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.AO_EVAL_BEAN, aoEvalBean);
		try
		{
			if (aoEvalBean.getIsAccoUser() != null)
			{
				if (null != aoEvalBean.getIsFiltered())
				{
					aoEvalBean.setIsValidUser(Boolean.TRUE);
				}
				else
				{
					if (aoEvalBean.getProposalStatusIdList() == null && !aoEvalBean.getFilteredCheck())
					{
						List<String> loProposalStatusList = new ArrayList<String>();
						loProposalStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_PROPOSAL_EVALUATED));
						loProposalStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_PROPOSAL_SCORES_RETURNED));
						loProposalStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_PROPOSAL_SELECTED));
						loProposalStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_PROPOSAL_NOT_SELECTED));
						aoEvalBean.setProposalStatusList(loProposalStatusList);
					}
					aoEvalBean.setIsValidUser(Boolean.TRUE);
				}
			}
			// If Non Acco User then fetch the records with Selected Status
			else if (aoEvalBean.getIsAgencyAccoUser() != null)
			{
				List<String> loProposalStatusList = new ArrayList<String>();
				loProposalStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_PROPOSAL_SELECTED));
				aoEvalBean.setProposalStatusList(loProposalStatusList);
				aoEvalBean.setIsValidUser(Boolean.TRUE);
			}
			else
			{
				aoEvalBean.setIsValidUser(Boolean.FALSE);
			}
			// Execute transaction if its Acco or Agency Non Acco User
			if (aoEvalBean.getIsValidUser())
			{
				aoEvalResults = fetchEvalDataForValidUsers(aoMybatisSession, aoEvalBean, aoEvalAwardReviewStatusBean,
						aoProcurementStatus);
			}
		}
		// Any Exception from DAO class will be thrown as Application
		// Exception
		// which will be handles over here. It throws Application Exception
		// back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			aoExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while getting Evaluation Result Data :", aoExp);
			setMoState("Error occurred while fetching evaluation Results Data:" + aoExp.getMessage());
			throw aoExp;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while getting proposal comments :", aoExp);
			setMoState("Error occurred while fetching evaluation Results Data:" + aoExp.getMessage());
			throw new ApplicationException("Error occurred while fetching evaluation Results Data:" + loContextDataMap,
					aoExp);
		}
		return aoEvalResults;
	}

	/**
	 * This method fetches total evaluations processed for a procurement.
	 * 
	 * <ul>
	 * <li>1. make not null Check for Proposal Title.</li>
	 * <li>2. if check in step 1 is true then add % sign in the start and the
	 * end.</li>
	 * <li>3. make not null Check for Organization Name.</li>
	 * <li>4. if check in step 1 is true then add % sign in the start and the
	 * end of Organization Name.</li>
	 * <li>5. Execute select query <b>fetchEvaluationResultsSelections</b> from
	 * the EvaluationMapper</li>
	 * <li>6. Query will return list of evaluations</li>
	 * <li>7. Remove the % sign appended with field</li>
	 * <li>8. Throw the exception if any occur.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - SqlSession
	 * @param aoEvalBean - EvaluationFilterBean holding the sorting and the
	 *            filtering criteria
	 * @param aoEvalAwardReviewStatusBean - EvaluationBean reference
	 * @return List - List of EvaluationFilterBean holding the data fetched from
	 *         database
	 * @throws ApplicationException - throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	private List<EvaluationFilterBean> fetchEvalDataForValidUsers(SqlSession aoMybatisSession,
			EvaluationFilterBean aoEvalBean, EvaluationBean aoEvalAwardReviewStatusBean, Integer aoProcurementStatus)
			throws ApplicationException

	{
		List<EvaluationFilterBean> loEvalResults = null;
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.AO_EVAL_BEAN, aoEvalBean);
		try
		{

			if (aoEvalBean.getProposalTitle() != null)
			{
				// Appending Proposal Title with % for Filter Purpose
				StringBuffer loProposalTitleStrBfr = new StringBuffer(HHSConstants.PERCENT).append(
						aoEvalBean.getProposalTitle()).append(HHSConstants.PERCENT);
				aoEvalBean.setProposalTitle(loProposalTitleStrBfr.toString());
			}

			if (aoEvalBean.getOrganizationName() != null)
			{
				// Appending Proposal Title with % for Filter Purpose
				StringBuffer loOrganizationSb = new StringBuffer(HHSConstants.PERCENT).append(
						aoEvalBean.getOrganizationName()).append(HHSConstants.PERCENT);
				aoEvalBean.setOrganizationName(loOrganizationSb.toString());
			}
			if (null != aoEvalAwardReviewStatusBean)
			{
				aoEvalBean.setAwardReviewStatus(aoEvalAwardReviewStatusBean.getAwardReviewStatus());
				// Start || Changes made for Enhancement 6574 for Release 3.10.0
				if (null != aoEvalAwardReviewStatusBean.getAwardReviewStatusId()
						&& aoEvalAwardReviewStatusBean.getAwardReviewStatusId().equalsIgnoreCase(
								PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
										HHSConstants.STATUS_AWARD_REVIEW_APPROVED)))
				{
					if (null != aoEvalBean && null != aoEvalBean.getEvaluationPoolMappingId()
							&& !aoEvalBean.getEvaluationPoolMappingId().isEmpty())
					{
						String loEvalStatus = (String) DAOUtil.masterDAO(aoMybatisSession,
								aoEvalBean.getEvaluationPoolMappingId(), HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
								HHSConstants.FETCH_EVAL_POOL_MAPPING_STATUS, HHSConstants.JAVA_LANG_STRING);
						if (null != loEvalStatus && !loEvalStatus.isEmpty() && loEvalStatus.equalsIgnoreCase("139"))
						{
							aoEvalBean.setAwardReviewStatusId(HHSConstants.AWARD_APPROVAL_TERMINATED);
						}
						else
						{
							aoEvalBean.setAwardReviewStatusId(aoEvalAwardReviewStatusBean.getAwardReviewStatusId());
						}
					}
					else if (null != aoProcurementStatus && aoProcurementStatus == 6)
					{
						aoEvalBean.setAwardReviewStatusId(HHSConstants.AWARD_APPROVAL_TERMINATED);
					}
					else
					{
						aoEvalBean.setAwardReviewStatusId(aoEvalAwardReviewStatusBean.getAwardReviewStatusId());
					}
				}
				else
				{
					aoEvalBean.setAwardReviewStatusId(aoEvalAwardReviewStatusBean.getAwardReviewStatusId());
				}
				// End || Changes made for Enhancement 6574 for Release 3.10.0
			}
			if (aoEvalBean.getUserRole() != null
					&& (aoEvalBean.getUserRole().equalsIgnoreCase(HHSConstants.ACCO_ADMIN_STAFF_ROLE)
							|| aoEvalBean.getUserRole().equalsIgnoreCase(HHSConstants.ACCO_MANAGER_ROLE)
							|| aoEvalBean.getUserRole().equalsIgnoreCase(HHSConstants.ACCO_STAFF_ROLE) || (aoEvalBean
							.getOrgType() != null && aoEvalBean.getOrgType().equalsIgnoreCase(HHSConstants.USER_CITY))))
			{
				loEvalResults = (List<EvaluationFilterBean>) DAOUtil.masterDAO(aoMybatisSession, aoEvalBean,
						HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_EVAL_RESULTS_SEL,
						HHSConstants.COM_NYC_HHS_MODEL_EVALFILTERBEAN);
			}
			else
			{
				List<String> loProposalStatusList = new ArrayList<String>();
				loProposalStatusList.add("23");
				aoEvalBean.setProposalStatusList(loProposalStatusList);
				loEvalResults = (List<EvaluationFilterBean>) DAOUtil.masterDAO(aoMybatisSession, aoEvalBean,
						HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_EVAL_RESULTS_SEL,
						HHSConstants.COM_NYC_HHS_MODEL_EVALFILTERBEAN);
			}

			String lsProposalTitle = aoEvalBean.getProposalTitle();
			if (null != lsProposalTitle)
			{ // Removing % sign i.e added previously
				lsProposalTitle = lsProposalTitle.substring(HHSConstants.INT_ONE);
				lsProposalTitle = lsProposalTitle.substring(HHSConstants.INT_ZERO, lsProposalTitle.length()
						- HHSConstants.INT_ONE);
				aoEvalBean.setProposalTitle(lsProposalTitle);
			}
			String lsOrganizationName = aoEvalBean.getOrganizationName();
			if (null != lsOrganizationName)
			{ // Removing % sign i.e added previously
				lsOrganizationName = lsOrganizationName.substring(HHSConstants.INT_ONE);
				lsOrganizationName = lsOrganizationName.substring(HHSConstants.INT_ZERO, lsOrganizationName.length()
						- HHSConstants.INT_ONE);
				aoEvalBean.setOrganizationName(lsOrganizationName);
			}
			setMoState("Evaluation Results and Settings fetched successfully for ProcurementId :"
					+ aoEvalBean.getProcurementId());
		}
		// Any Exception from DAO class will be thrown as Application
		// Exception
		// which will be handles over here. It throws Application Exception
		// back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoAppExp)
		{
			aoAppExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while Evaluation Result Data for valid user :", aoAppExp);
			setMoState("Error occurred while fetching evaluation Results Data:" + aoAppExp.getMessage());
			throw aoAppExp;
		}
		/**
		 * Any Exception from DAO class will be handles over here. It throws
		 * Application Exception back to Controllers calling method through
		 * Transaction framework
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error occurred while fetching evaluation Results Data for valid user :", aoExp);
			setMoState("Error occurred while fetching evaluation Results Data for valid user:" + aoExp.getMessage());
			throw new ApplicationException("Error occurred while fetching evaluation Results Data for valid user:"
					+ loContextDataMap, aoExp);
		}
		return loEvalResults;
	}

	/**
	 * This method fetches count of evaluations processed for a procurement
	 * 
	 * <ul>
	 * <li>1. Get evaluation Bean as input</li>
	 * <li>2. Create the loContextDataMap to hold the context data.</li>
	 * <li>3. Set the input data in the loContextDataMap.</li>
	 * <li>4. make not null Check for Proposal Title.</li>
	 * <li>5. if check in step 1 is true then add % sign in the start and the
	 * end.</li>
	 * <li>6. make not null Check for Organization Name.</li>
	 * <li>7. if check in step 1 is true then add % sign in the start and the
	 * end of Organization Name.</li>
	 * <li>8. Execute select query fetchEvaluationResultsCount from the
	 * EvaluationMapper</li>
	 * <li>9. Query will return count of evaluations</li>
	 * <li>10. Remove the % sign appended with field</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession a SQL session
	 * @param aoEvalBean EvaluationFilterBean reference holding the criteria
	 *            based on which data to be fetched
	 * @return an integer value of evaluation results count
	 * @throws ApplicationException - throws ApplicationException
	 */
	public Integer fetchEvaluationResultsCount(SqlSession aoMybatisSession, EvaluationFilterBean aoEvalBean)
			throws ApplicationException
	{
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.AO_EVAL_BEAN, aoEvalBean);
		Integer loEvalCount = HHSConstants.INT_ZERO;
		if (aoEvalBean.getIsValidUser())
		{
			if (aoEvalBean.getProposalTitle() != null)
			{
				// Appending Proposal Title with % for Filter Purpose
				StringBuffer loProposalTitleStrBfr = new StringBuffer(HHSConstants.PERCENT).append(
						aoEvalBean.getProposalTitle()).append(HHSConstants.PERCENT);
				aoEvalBean.setProposalTitle(loProposalTitleStrBfr.toString());
			}

			if (aoEvalBean.getOrganizationName() != null)
			{
				// Appending Proposal Title with % for Filter Purpose
				StringBuffer loOrganizationSb = new StringBuffer(HHSConstants.PERCENT).append(
						aoEvalBean.getOrganizationName()).append(HHSConstants.PERCENT);
				aoEvalBean.setOrganizationName(loOrganizationSb.toString());
			}
			try
			{
				loEvalCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoEvalBean,
						HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_EVAL_RESULT_COUNT,
						HHSConstants.COM_NYC_HHS_MODEL_EVALFILTERBEAN);
				setMoState("Evaluation Results and Settings count fetched successfully for ProcurementId :"
						+ aoEvalBean.getProcurementId());
				String lsProposalTitle = aoEvalBean.getProposalTitle();
				if (null != lsProposalTitle)
				{ // Removing % sign i.e added previously
					lsProposalTitle = lsProposalTitle.substring(HHSConstants.INT_ONE);
					lsProposalTitle = lsProposalTitle.substring(HHSConstants.INT_ZERO, lsProposalTitle.length()
							- HHSConstants.INT_ONE);
					aoEvalBean.setProposalTitle(lsProposalTitle);
				}
				String lsOrganizationName = aoEvalBean.getOrganizationName();
				if (null != lsOrganizationName)
				{ // Removing % sign i.e added previously
					lsOrganizationName = lsOrganizationName.substring(HHSConstants.INT_ONE);
					lsOrganizationName = lsOrganizationName.substring(HHSConstants.INT_ZERO,
							lsOrganizationName.length() - HHSConstants.INT_ONE);
					aoEvalBean.setOrganizationName(lsOrganizationName);
				}
			}
			// Any Exception from DAO class will be thrown as Application
			// Exception
			// which will be handles over here. It throws Application Exception
			// back
			// to Controllers calling method through Transaction framework
			catch (ApplicationException aoAppEx)
			{
				aoAppEx.setContextData(loContextDataMap);
				LOG_OBJECT.Error("Error while getting proposal comments :", aoAppEx);
				setMoState("Error occurred while fetching evaluation Results and Settings:" + aoAppEx.getMessage());
				throw aoAppEx;
			}
			/**
			 * Any Exception from DAO class will be handles over here. It throws
			 * Application Exception back to Controllers calling method through
			 * Transaction framework
			 */
			catch (Exception aoAppEx)
			{
				LOG_OBJECT.Error("Error while getting proposal comments :", aoAppEx);
				setMoState("Error occurred while fetching evaluation Results and Settings:" + aoAppEx.getMessage());
				throw new ApplicationException("Error while getting proposal comments :", aoAppEx);
			}
		}
		return loEvalCount;
	}

	/**
	 * The Method will return the Visibility Status of Finalize Result Button*
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. Create the loContextDataMap to hold the context data.</li>
	 * <li>2. Set the input data in the loContextDataMap.</li>
	 * <li>3.Check If user id an Agency-Acco user</li>
	 * <li>4.Check if All Proposal statuses are either "Non-Responsive",
	 * "Selected" or "Not Selected"</li>
	 * <li>5.In Addition to 2 check if Award status is Null the return true i.e
	 * button will be active</li>
	 * <li>6. Execute queries "getProposalStatusIdCount" and
	 * "getAwardReviewStatusId".</li>
	 * <li>7. Set the flag based on the query result.</li>
	 * </ul>
	 * 
	 * Change: updated query for competition pool
	 * 
	 * Changed By: Pallavi
	 * 
	 * Change Date: 21 Jan 2014
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoEvalBean EvaluationFilterBean
	 * @return map containing button display flag properties
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Boolean> fetchFinalizeResultsVisibiltyStatus(SqlSession aoMybatisSession,
			EvaluationFilterBean aoEvalBean) throws ApplicationException
	{
		Map<String, Boolean> loFinalizeResultStatusMap = new HashMap<String, Boolean>();
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.AO_EVAL_BEAN, aoEvalBean);
		try
		{
			List<String> loProposalStatusList = new ArrayList<String>();
			// Setting status as Non Responsive,Selected,Not Selected
			loProposalStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_PROPOSAL_DRAFT));
			loProposalStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_PROPOSAL_NON_RESPONSIVE));
			loProposalStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_PROPOSAL_SELECTED));
			loProposalStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_PROPOSAL_NOT_SELECTED));
			aoEvalBean.setProposalStatusIdList(loProposalStatusList);
			// Fetching the no of status other than above
			List<Integer> loProposaReviewStatusList = (List<Integer>) DAOUtil.masterDAO(aoMybatisSession, aoEvalBean,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.PROPOSAL_STATUS_RESULT_COUNT,
					HHSConstants.COM_NYC_HHS_MODEL_EVALFILTERBEAN);
			// Getting Award Review Status
			String lsAwardReviewStatusId = (String) DAOUtil.masterDAO(aoMybatisSession, aoEvalBean,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.GET_AWARD_REVIEW_STATUS_ID,
					HHSConstants.COM_NYC_HHS_MODEL_EVALFILTERBEAN);
			Boolean loButtonDisplayFlagForFinalize = Boolean.FALSE;
			if (loProposaReviewStatusList != null && !loProposaReviewStatusList.isEmpty())
			{
				for (Integer loProposalStatus : loProposaReviewStatusList)
				{
					if (loProposalStatus != null && (loProposalStatus == 23 || loProposalStatus == 24))
					{
						loButtonDisplayFlagForFinalize = Boolean.TRUE;
					}
					else
					{
						loButtonDisplayFlagForFinalize = Boolean.FALSE;
						break;
					}
				}
			}
			// if no status exists other than above and Award Review Status
			// null
			Boolean loFinalizeResultStatus = Boolean.FALSE;
			if (loButtonDisplayFlagForFinalize && lsAwardReviewStatusId == null)
			{
				loFinalizeResultStatus = Boolean.TRUE;
			}
			if (loFinalizeResultStatus && aoEvalBean.getUserRole() != null
					&& (aoEvalBean.getUserRole().equalsIgnoreCase(HHSConstants.ACCO_MANAGER_ROLE)))
			{
				loFinalizeResultStatusMap.put(HHSConstants.SHOW_FINALIZE_BUTTON, Boolean.TRUE);
				loFinalizeResultStatusMap.put(HHSConstants.FINALIZE_BUTTON_ACTIVE, Boolean.TRUE);
			}
			else if ((!loButtonDisplayFlagForFinalize) && lsAwardReviewStatusId == null
					&& aoEvalBean.getUserRole() != null
					&& (aoEvalBean.getUserRole().equalsIgnoreCase(HHSConstants.ACCO_MANAGER_ROLE)))
			{
				loFinalizeResultStatusMap.put(HHSConstants.SHOW_FINALIZE_BUTTON, Boolean.TRUE);
				loFinalizeResultStatusMap.put(HHSConstants.FINALIZE_BUTTON_ACTIVE, Boolean.FALSE);
			}
			else
			{
				loFinalizeResultStatusMap.put(HHSConstants.SHOW_FINALIZE_BUTTON, Boolean.FALSE);
			}
			setMoState("Successfully fetched finalize button visibility flag for procurement id:"
					+ aoEvalBean.getProcurementId());
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while getting Finalize Result Button Status :", aoAppEx);
			setMoState("Error while getting Finalize Result Button Status" + aoAppEx.getMessage());
			throw aoAppEx;
		}
		/**
		 * Any Exception from DAO class will be handles over here. It throws
		 * Application Exception back to Controllers calling method through
		 * Transaction framework
		 */
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error while getting Finalize Result Button Status :", aoEx);
			setMoState("Error while getting Finalize Result Button Status" + aoEx.getMessage());
			throw new ApplicationException("Error while getting Finalize Result Button Status :", aoEx);
		}
		return loFinalizeResultStatusMap;
	}

	/**
	 * The Method will return the Visibility Status of Update Result Button
	 * after the awards have been approved
	 * <ul>
	 * <li>Changes done for Enhancement #6574 for Release 3.10.0</li>
	 * <li>1. Create the loContextDataMap to hold the context data.</li>
	 * <li>2. Set the input data in the loContextDataMap.</li>
	 * <li>3.Check If user id an Agency-Acco user</li>
	 * <li>4.Check if All Proposal statuses are either "Non-Responsive",
	 * "Selected" or "Not Selected"</li>
	 * <li>5.In Addition to 2 check if Award status is Null the return true i.e
	 * button will be active</li>
	 * <li>6. Execute queries "getProposalStatusIdCount" and
	 * "getAwardReviewStatusId".</li>
	 * <li>7. Set the flag based on the query result.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoEvalBean EvaluationFilterBean
	 * @return map containing button display flag properties
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public String fetchUpdateAfterApprovalStatus(SqlSession aoMybatisSession, EvaluationFilterBean aoEvalBean)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered fetchUpdateAfterApprovalStatus for procurement id: " + aoEvalBean.getProcurementId());
		String lsShowUpdateAfterApprove = null;
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.AO_EVAL_BEAN, aoEvalBean);
		try
		{
			String lsAwardReviewStatusId = (String) DAOUtil.masterDAO(aoMybatisSession, aoEvalBean,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.GET_AWARD_REVIEW_STATUS_ID,
					HHSConstants.COM_NYC_HHS_MODEL_EVALFILTERBEAN);

			// if no status exists other than above and Approve Award Task has
			// been completed
			// and configure award task has not been completed
			Boolean loUpdateAfterApprove = Boolean.FALSE;
			// for 6574 for Release 3.10.0
			if (lsAwardReviewStatusId != null
					&& lsAwardReviewStatusId.equalsIgnoreCase(PropertyLoader.getProperty(
							HHSConstants.PROPERTIES_STATUS_CONSTANT,
							HHSConstants.STATUS_AWARD_REVIEW_UPDATE_IN_PROGRESS_TEMP)))
			{
				loUpdateAfterApprove = Boolean.TRUE;
				if (loUpdateAfterApprove && aoEvalBean.getUserRole() != null
						&& (aoEvalBean.getUserRole().equalsIgnoreCase(HHSConstants.ACCO_MANAGER_ROLE)))
				{
					lsShowUpdateAfterApprove = HHSConstants.ENABLE;
				}
				else
				{
					lsShowUpdateAfterApprove = HHSConstants.DISABLE;
				}
			}

			LOG_OBJECT.Debug("Entered fetchUpdateAfterApprovalStatus for procurement id: "
					+ aoEvalBean.getProcurementId());
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching update after approve flag :", aoAppEx);
			setMoState("Error while fetching update after approve flag" + aoAppEx.getMessage());
			throw aoAppEx;
		}
		/**
		 * Any Exception from DAO class will be handles over here. It throws
		 * Application Exception back to Controllers calling method through
		 * Transaction framework
		 */
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error while fetching update after approve flag :", aoEx);
			setMoState("Error while fetching update after approve flag" + aoEx.getMessage());
			throw new ApplicationException("Error while fetching update after approve flag :", aoEx);
		}
		return lsShowUpdateAfterApprove;
	}

	/**
	 * This Method will Update Results Button Visibilty Status
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. Create the loContextDataMap to hold the context data.</li>
	 * <li>2. Set the input data in the loContextDataMap.</li>
	 * <li>3.If user is an Agency ACCO user</li>
	 * <li>4. Execute querie "getAwardReviewStatusId".</li>
	 * <li>5.If the Award Review Status is "Returned", this button is visible
	 * and active.</li>
	 * <li>6.If the Award Review Status is "In Review" the button is visible and
	 * inactive.</li>
	 * <li>7.Else not visible</li>
	 * </ul>
	 * 
	 * Change: updated query for competition pool
	 * 
	 * Changed By: Pallavi
	 * 
	 * Change Date: 21 Jan 2014
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoEvalBeanForUpdateResults EvaluationFilterBean
	 * @return lsUpdateResultStatus - update result status
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public String fetchUpdateResultsVisibiltyStatus(SqlSession aoMybatisSession,
			EvaluationFilterBean aoEvalBeanForUpdateResults) throws ApplicationException
	{
		String lsUpdateResultStatus = null;
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.AO_EVAL_BEAN, aoEvalBeanForUpdateResults);
		try
		{
			List<String> loProposalStatusListForUpdateResults = new ArrayList<String>();

			// Setting status as Non Responsive,Selected,Not Selected
			loProposalStatusListForUpdateResults.add(PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_DRAFT));
			loProposalStatusListForUpdateResults.add(PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_NON_RESPONSIVE));
			loProposalStatusListForUpdateResults.add(PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_SELECTED));
			loProposalStatusListForUpdateResults.add(PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_NOT_SELECTED));
			aoEvalBeanForUpdateResults.setProposalStatusIdList(loProposalStatusListForUpdateResults);

			// Fetching the no of status other than above
			List<Integer> loProposaReviewStatusList = (List<Integer>) DAOUtil.masterDAO(aoMybatisSession,
					aoEvalBeanForUpdateResults, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
					HHSConstants.PROPOSAL_STATUS_RESULT_COUNT, HHSConstants.COM_NYC_HHS_MODEL_EVALFILTERBEAN);
			String lsAwardReviewStatusId = (String) DAOUtil.masterDAO(aoMybatisSession, aoEvalBeanForUpdateResults,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.GET_AWARD_REVIEW_STATUS_ID,
					HHSConstants.COM_NYC_HHS_MODEL_EVALFILTERBEAN);
			Boolean loButtonDisplayFlagForUpdateResult = Boolean.FALSE;
			if (loProposaReviewStatusList != null && !loProposaReviewStatusList.isEmpty())
			{
				for (Integer loProposalStatus : loProposaReviewStatusList)
				{
					if (loProposalStatus != null && (loProposalStatus == 23 || loProposalStatus == 24))
					{
						loButtonDisplayFlagForUpdateResult = Boolean.TRUE;
					}
					else
					{
						loButtonDisplayFlagForUpdateResult = Boolean.FALSE;
						break;
					}
				}
			}
			if (!loButtonDisplayFlagForUpdateResult)
			{
				lsUpdateResultStatus = HHSConstants.NOT_VISIBLE;
			}
			else if (null != lsAwardReviewStatusId
					&& (lsAwardReviewStatusId.equals(PropertyLoader.getProperty(
							HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_AWARD_REVIEW_RETURNED)) || lsAwardReviewStatusId
							.equals(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
									HHSConstants.STATUS_AWARD_REVIEW_UPDATE_IN_PROGRESS))))
			{
				lsUpdateResultStatus = HHSConstants.ENABLE;
			}
			else if (null != lsAwardReviewStatusId
					&& (lsAwardReviewStatusId.equals(PropertyLoader.getProperty(
							HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_AWARD_REVIEW_IN_REVIEW)) || lsAwardReviewStatusId
							.equals(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
									HHSConstants.STATUS_AWARD_REVIEW_APPROVED))))
			{
				lsUpdateResultStatus = HHSConstants.DISABLE;
			}
			else
			{
				lsUpdateResultStatus = HHSConstants.NOT_VISIBLE;
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while getting Update Result Button Status :", aoAppEx);
			setMoState("Error while getting update Result Button Status" + aoAppEx.getMessage());
			throw aoAppEx;
		}
		/**
		 * Any Exception from DAO class will be handles over here. It throws
		 * Application Exception back to Controllers calling method through
		 * Transaction framework
		 */
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error while getting Update Result Button Status :", aoEx);
			setMoState("Error while getting update Result Button Status" + aoEx.getMessage());
			throw new ApplicationException("Error while getting Update Result Button Status :", aoEx);
		}
		return lsUpdateResultStatus;
	}

	/**
	 * This method fetches the internal evaluation users .
	 * 
	 * <ul>
	 * <li>1. Fetch agency id from the channel object</li>
	 * <li>2. Execute the query "fetchInternalEvaluatorUsers" with asAgencyId as
	 * a parameter.</li>
	 * <li>3. Return the list of ProviderBean.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asAgencyId - string representation of agency Id
	 * @param asInputParam - a string
	 * @return loInternalEvaluatorList - an object of type
	 *         List<AutoCompleteBean>
	 * @throws ApplicationException If an ApplicationException occurs
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<AutoCompleteBean> fetchInternalEvaluatorUsers(SqlSession aoMybatisSession, String asAgencyId,
			String asInputParam) throws ApplicationException
	{
		List<AutoCompleteBean> loInternalEvaluatorList = null;
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.AGENCY_ID, asAgencyId);
		// create a input map
		Map<String, Object> loInputParamMap = new HashMap<String, Object>();
		loInputParamMap.put(HHSConstants.AS_AGENCY_ID, asAgencyId);
		asInputParam = HHSConstants.PERCENT + asInputParam.toUpperCase() + HHSConstants.PERCENT;
		loInputParamMap.put(HHSConstants.INPUT_PARAM_MAP, asInputParam);
		try
		{
			loInternalEvaluatorList = (List<AutoCompleteBean>) DAOUtil.masterDAO(aoMybatisSession, loInputParamMap,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_INTERNAL_EVALUATOR_USERS,
					HHSConstants.JAVA_UTIL_MAP);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			aoExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Exception occured while getting internal evaluation user setting from db", aoExp);
			throw new ApplicationException("Exception occured while getting internal evaluation user setting from db"
					+ aoExp);
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception occured while getting internal evaluation user setting from db", loExp);
			throw new ApplicationException("Exception occured while getting internal evaluation user setting from db"
					+ loExp);
		}
		return loInternalEvaluatorList;
	}

	/**
	 * This method fetches the evaluation users external.
	 * 
	 * <ul>
	 * <li>1. Fetch agency id from the channel object</li>
	 * <li>2. Execute the query "fetchExternalEvaluatorUsers" with asAgencyId as
	 * a parameter.</li>
	 * <li>3. Return the list of ProviderBean.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asAgencyId - string representation of agency Id
	 * @param asInputParam - string
	 * @return loExternalEvaluatorList - an object of type List<ProviderBean>
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<ProviderBean> fetchExternalEvaluatorUsers(SqlSession aoMybatisSession, String asAgencyId,
			String asInputParam) throws ApplicationException
	{
		List<ProviderBean> loExternalEvaluatorList = null;
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.AGENCY_ID, asAgencyId);
		// create a input map
		Map<String, Object> loInputParamMap = new HashMap<String, Object>();
		loInputParamMap.put(HHSConstants.AS_AGENCY_ID, asAgencyId);
		asInputParam = HHSConstants.PERCENT + asInputParam.toUpperCase() + HHSConstants.PERCENT;
		loInputParamMap.put(HHSConstants.INPUT_PARAM_MAP, asInputParam);
		try
		{
			loExternalEvaluatorList = (List<ProviderBean>) DAOUtil.masterDAO(aoMybatisSession, loInputParamMap,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_EXTERNAL_EVALUATOR_USERS,
					HHSConstants.JAVA_UTIL_MAP);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			aoExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Exception occured while getting external evaluation user setting from db", aoExp);
			throw new ApplicationException("Exception occured while getting external evaluation user setting from db"
					+ aoExp);
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception occured while getting external evaluation user setting from db", loExp);
			throw new ApplicationException("Exception occured while getting external evaluation user setting from db"
					+ loExp);
		}
		return loExternalEvaluatorList;
	}

	/**
	 * This method will fetch the required proposal data corresponding to the
	 * proposal Id.
	 * <ul>
	 * <li>1. Retrieve Proposal Id from the channel</li>
	 * <li>2. Execute query <b>fetchReqProposalDetails</b> to fetch proposal
	 * details(Organization Name, Proposal Title, Evaluation Score, Award
	 * Amount, Comment) corresponding to the proposal Id</li>
	 * <li>3. Return fetched list</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProposalId - string representation of Proposal Id
	 * @return loProposalDetails - return proposal Detail
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public EvaluationBean fetchReqProposalDetails(SqlSession aoMybatisSession, String asProposalId)
			throws ApplicationException
	{
		EvaluationBean loProposalDetails = null;
		HashMap<String, Object> loHMContextData = new HashMap<String, Object>();
		loHMContextData.put(HHSConstants.PROPOSAL_ID, asProposalId);
		LOG_OBJECT.Debug("Entered into fetching required proposal details::" + loHMContextData.toString());
		if (asProposalId != null)
		{
			try
			{
				loProposalDetails = (EvaluationBean) DAOUtil.masterDAO(aoMybatisSession, asProposalId,
						HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_REQ_PROPOSALS_DETAILS,
						HHSConstants.JAVA_LANG_STRING);

				setMoState("Proposal Details List fetched successfully corresponding to Proposal Id:" + asProposalId);
			}
			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handles over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */
			catch (ApplicationException aoAppEx)
			{
				aoAppEx.setContextData(loHMContextData);
				LOG_OBJECT.Error(HHSConstants.ERROR_WHILE_FETCHING_PROPOSAL_DETAILS, aoAppEx);
				setMoState("Transaction Failed:: EvaluationService:fetchReqProposalDetails method -  while fetching list of proposal details corresponding to the proposal Id:"
						+ asProposalId);
				throw aoAppEx;
			}
			catch (Exception loExp)
			{
				LOG_OBJECT.Error(HHSConstants.ERROR_WHILE_FETCHING_PROPOSAL_DETAILS, loExp);
				setMoState("Transaction Failed:: EvaluationService:fetchReqProposalDetails method -  while fetching list of proposal details corresponding to the proposal Id:"
						+ asProposalId);
				throw new ApplicationException("Error occured while fetching required proposal details" + loExp);
			}
		}
		return loProposalDetails;

	}

	/**
	 * This method will fetch the award status Id corresponding to the
	 * procurement Id.
	 * <ul>
	 * <li>1. Retrieve Evaluation bean from the channel</li>
	 * <li>2. If the retrieved Evaluation bean is not null then execute query
	 * <b>fetchAwardStatusId</b> to fetch award status Id corresponding to the
	 * procurement Id in award table</li>
	 * <li>3. Return the fetched award status Id</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoEvaluationBean - an EvaluationBean object
	 * @return lsAwardStatusId - string representation of award status Id
	 * @throws ApplicationException If an ApplicationException occurs
	 */

	public Map<String, Object> fetchAwardStatusId(SqlSession aoMybatisSession, EvaluationBean aoEvaluationBean)
			throws ApplicationException
	{
		Map<String, Object> lsAwardStatusId = null;
		HashMap<String, Object> loHMContextData = new HashMap<String, Object>();
		loHMContextData.put(HHSConstants.EVAL_BEAN, aoEvaluationBean);
		LOG_OBJECT.Debug("Entered into fetching award Status Id::" + loHMContextData.toString());
		// checking if evaluation bean is not null
		if (aoEvaluationBean != null)
		{
			try
			{

				lsAwardStatusId = (Map<String, Object>) DAOUtil.masterDAO(aoMybatisSession, aoEvaluationBean,
						HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_AWARD_STATUS_ID,
						HHSConstants.COM_NYC_HHS_MODEL_EVALUATION_BEAN);
				setMoState("Award Status Id fetched successfully corresponding to Procurement Id");
			}
			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handles over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */
			catch (ApplicationException aoAppEx)
			{
				LOG_OBJECT
						.Error("Exception occured while fetching award status Id corresponding to the procurement Id",
								aoAppEx);
				setMoState("Transaction Failed:: EvaluationService:fetchAwardStatusId method -  while fetching award status Id corresponding to the procurement Id:");
				aoAppEx.setContextData(loHMContextData);
				throw aoAppEx;
			}
			catch (Exception loExp)
			{
				LOG_OBJECT.Error(
						"Exception occured while fetching award status Id corresponding to the procurement Id", loExp);
				setMoState("Transaction Failed:: EvaluationService:fetchAwardStatusId method -  while fetching award status Id corresponding to the procurement Id:");
				throw new ApplicationException(
						"Exception occured while fetching award status Id corresponding to the procurement Id" + loExp);
			}
		}
		return lsAwardStatusId;
	}

	/**
	 * This method will update the modified flag in the evaluation_result table
	 * corresponding to the proposal Id.
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Retrieve Evaluation bean and award status Id from the channel</li>
	 * <li>If the retrieved Evaluation bean and award status Id is not null then
	 * execute query <b>fetchAwardStatusId</b> to update the modified flag
	 * corresponding to the proposal Id in Evaluation_Result table</li>
	 * <li>Return the number of rows updated</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoAwardStatusId - string representation of award status Id
	 * @param aoEvaluationBean - an EvaluationBean object
	 * @return loCount - number of rows updated
	 * @throws ApplicationException If an ApplicationException occurs
	 */

	public Integer updateModifiedFlag(SqlSession aoMybatisSession, Map<String, Object> aoAwardStatusId,
			EvaluationBean aoEvaluationBean) throws ApplicationException
	{
		Integer loCount = null;
		HashMap<String, Object> loHMContextData = new HashMap<String, Object>();
		loHMContextData.put(HHSConstants.AWARD_STATUS_ID, aoAwardStatusId);
		LOG_OBJECT.Debug("Entered into updating mofified flag in evaluation result table::"
				+ loHMContextData.toString());
		// checking if award Status Id and evaluation bean is not null
		if (aoAwardStatusId != null && aoEvaluationBean != null)
		{
			try
			{
				if (aoAwardStatusId.get(HHSConstants.AWARD_REVIEW_STATUS_ID_KEY) != null)
				{
					String lsAwardStatusId = ((BigDecimal) aoAwardStatusId.get(HHSConstants.AWARD_REVIEW_STATUS_ID_KEY))
							.toString();
					// checking if the retrieved award review status is equal to
					// "In Progress"
					// Start || Changes done for enhancement 6574 for Release
					// 3.10.0
					if (lsAwardStatusId != null && aoAwardStatusId.get(HHSConstants.AWARD_APPROVAL_DATE) != null)
					{
						loCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoEvaluationBean,
								HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
								HHSConstants.UPDATED_MODIFIED_FLAG_EVAL_RESULTS,
								HHSConstants.COM_NYC_HHS_MODEL_EVALUATION_BEAN);
					}
					// End || Changes done for enhancement 6574 for Release
					// 3.10.0
					setMoState("Modified flag updated successfully when the award Review Status is In Progress");
				}
			}
			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handles over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */
			catch (ApplicationException aoAppEx)
			{
				aoAppEx.setContextData(loHMContextData);
				LOG_OBJECT.Error(
						"Exception occured while updating modified flag when the award review status is In Progress",
						aoAppEx);
				setMoState("Transaction Failed:: EvaluationService:updateModifiedFlag method -  updating modified flag when the award review status is In Progress");
				throw aoAppEx;
			}
			catch (Exception loExp)
			{
				LOG_OBJECT.Error(
						"Exception occured while updating modified flag when the award review status is In Progress",
						loExp);
				setMoState("Transaction Failed:: EvaluationService:updateModifiedFlag method -  updating modified flag when the award review status is In Progress");
				throw new ApplicationException(
						"Exception occured while updating modified flag when the award review status is In Progress"
								+ loExp);
			}
		}
		return loCount;
	}

	/**
	 * This method will update the Status of the proposal to "Selected" on click
	 * of "Confirm" button.
	 * <ul>
	 * <li>1. Retrieve Evaluation bean from the channel</li>
	 * <li>2. Execute query <b>updateSelectedProposalAwardAmount</b> and
	 * <b>updateProposalReviewStatus</b>, <b>updateSelectedProposalComments</b>
	 * and <b>updateSelectedProposalStatus</b> to modify proposal status to
	 * "Selected" corresponding to the proposal Id in proposal_config table,
	 * update the award_amount in award table and internal comments in
	 * user_comment table</li>
	 * <li>3. Return count of the number of rows updated</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoEvaluationBean - an EvaluationBean object
	 * @return loCount - number of rows updated
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Integer updateSelectedProposalDetails(SqlSession aoMybatisSession, EvaluationBean aoEvaluationBean)
			throws ApplicationException
	{
		Integer loCount = null;
		HashMap<String, Object> loHMContextData = new HashMap<String, Object>();
		loHMContextData.put(HHSConstants.EVAL_BEAN, aoEvaluationBean);
		LOG_OBJECT.Debug("Entered into updating selected proposal details::" + loHMContextData.toString());
		// checking if evaluation bean is not null
		if (aoEvaluationBean != null)
		{
			try
			{

				Map<String, String> loDataMap = new HashMap<String, String>();
				// updating award amount in evaluation result table
				loCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoEvaluationBean,
						HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
						HHSConstants.UPDATE_SELECTED_PROPOSAL_AWARD_AMOUNT,
						HHSConstants.COM_NYC_HHS_MODEL_EVALUATION_BEAN);

				// updating proposal status to selected
				loDataMap.put(HHSConstants.PROPOSAL_STATUS_ID_KEY, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_SELECTED));
				loDataMap.put(HHSConstants.PROPOSAL_ID, aoEvaluationBean.getProposalId());
				DAOUtil.masterDAO(aoMybatisSession, loDataMap, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
						HHSConstants.UPDATE_PROPOSAL_REVIEW_STATUS, HHSConstants.JAVA_UTIL_MAP);

				// updating proposal comments in user_comment table
				loCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoEvaluationBean,
						HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.UPDATE_SELECTED_PROPOSAL_COMMENTS,
						HHSConstants.COM_NYC_HHS_MODEL_EVALUATION_BEAN);
				// checking if the count is zero
				if (loCount == HHSConstants.INT_ZERO)
				{
					loCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoEvaluationBean,
							HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
							HHSConstants.INSERT_SELECTED_PROPOSAL_COMMENTS,
							HHSConstants.COM_NYC_HHS_MODEL_EVALUATION_BEAN);
				}

				setMoState("Proposal Details updated successfully corresponding to Proposal Id");
			}
			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handles over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */
			catch (ApplicationException aoAppEx)
			{
				aoAppEx.setContextData(loHMContextData);
				LOG_OBJECT.Error(HHSConstants.ERROR_WHILE_CONFIRMING_PROPOSAL_SELECTED, aoAppEx);
				setMoState("Transaction Failed:: EvaluationService:updateSelectedProposalDetails method -  while updating proposal details corresponding to the proposal Id:");
				throw aoAppEx;
			}
			catch (Exception loExp)
			{
				LOG_OBJECT.Error(HHSConstants.ERROR_WHILE_CONFIRMING_PROPOSAL_SELECTED, loExp);
				setMoState("Transaction Failed:: EvaluationService:updateSelectedProposalDetails method -  while updating proposal details corresponding to the proposal Id:");
				throw new ApplicationException("Exception Occured while confirming a proposal selected" + loExp);
			}
		}
		return loCount;
	}

	/**
	 * s This method will update the Status of the proposal to "Not Selected" on
	 * click of "Confirm" button.
	 * <ul>
	 * <li>1. Retrieve Evaluation bean from the channel</li>
	 * <li>2. Execute query <b>updateNotSelectedProposalAwardAmount</b>,
	 * <b>updateNotSelectedProposalComments</b> and
	 * <b>updateNotSelectedProposalStatus</b> to modify proposal status to
	 * "Not Selected" corresponding to the proposal Id in proposal_config table,
	 * update award_amount in award table and internal comments column in
	 * user_comment table.
	 * <li>3. Return count of the number of rows updated</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoEvaluationBean - an EvaluationBean object
	 * @return loCount - number of rows updated
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Integer updateNotSelectedProposalDetails(SqlSession aoMybatisSession, EvaluationBean aoEvaluationBean)
			throws ApplicationException
	{
		Integer loCount = null;
		HashMap<String, Object> loHMContextData = new HashMap<String, Object>();
		loHMContextData.put(HHSConstants.EVAL_BEAN, aoEvaluationBean);
		LOG_OBJECT.Debug("Entered into updating not selected proposal details::" + loHMContextData.toString());
		// checking if evaluation bean is not null
		if (aoEvaluationBean != null)
		{
			try
			{

				Map<String, String> loDataMap = new HashMap<String, String>();

				// updating award amount in evaluation result table
				loCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoEvaluationBean,
						HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
						HHSConstants.UPDATE_NOT_SELECTED_PROPOSAL_AWARD_AMOUNT,
						HHSConstants.COM_NYC_HHS_MODEL_EVALUATION_BEAN);

				// updating proposal status to not-selected
				loDataMap.put(HHSConstants.PROPOSAL_STATUS_ID_KEY, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_NOT_SELECTED));
				loDataMap.put(HHSConstants.PROPOSAL_ID, aoEvaluationBean.getProposalId());
				DAOUtil.masterDAO(aoMybatisSession, loDataMap, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
						HHSConstants.UPDATE_PROPOSAL_REVIEW_STATUS, HHSConstants.JAVA_UTIL_MAP);

				// updating proposal comments in user_comment table
				loCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoEvaluationBean,
						HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
						HHSConstants.UPDATE_NOT_SELECTED_PROPOSAL_COMMENTS,
						HHSConstants.COM_NYC_HHS_MODEL_EVALUATION_BEAN);

				// checking if the count is zero
				if (loCount == HHSConstants.INT_ZERO)
				{
					loCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoEvaluationBean,
							HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
							HHSConstants.INSERT_SELECTED_PROPOSAL_COMMENTS,
							HHSConstants.COM_NYC_HHS_MODEL_EVALUATION_BEAN);
				}

				setMoState("Proposal Details updated successfully corresponding to Proposal Id");
			}
			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handles over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */
			catch (ApplicationException aoAppEx)
			{
				aoAppEx.setContextData(loHMContextData);
				LOG_OBJECT.Error(HHSConstants.ERROR_WHILE_CONFIRMING_PROPOSAL_NOT_SELECTED, aoAppEx);
				setMoState("Transaction Failed:: EvaluationService:updateNotSelectedProposalDetails method -  while updating proposal details corresponding to the proposal Id:");
				throw aoAppEx;
			}
			catch (Exception loExp)
			{
				LOG_OBJECT.Error(HHSConstants.ERROR_WHILE_CONFIRMING_PROPOSAL_NOT_SELECTED, loExp);
				setMoState("Transaction Failed:: EvaluationService:updateNotSelectedProposalDetails method -  while updating proposal details corresponding to the proposal Id:");
				throw new ApplicationException("Exception Occured while confirming a proposal not-selected" + loExp);
			}
		}
		return loCount;
	}

	/**
	 * This method will get the details bean of the Evaluations for the specific
	 * ProcurementId
	 * <ul>
	 * <li>1. Create the map to hold the context data.</li>
	 * <li>2. Create the EvaluationBean list to be returned to hold the result
	 * data.</li>
	 * <li>3. Put the input data in the context map.</li>
	 * <li>4. Check for proposal Title, if not null then add % sign in start and
	 * end of it and set in the bean again</li>
	 * <li>5. Check for Organization Name, if not null then add % sign in start
	 * and end of it and set in the bean again</li>
	 * <li>6. Execute select query <b>fetchEvaluationDetails</b></li>
	 * <li>7. Apply Filter Criteria</li>
	 * <li>8. Get the List of Evaluation Details bean and return it to the
	 * channel Object</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession Object
	 * @param aoEvaluationBean an object of type EvaluationBean
	 * @return list of Evaluation Details
	 * @throws ApplicationException application Exception Object
	 */
	@SuppressWarnings("unchecked")
	public List<EvaluationBean> fetchEvaluationDetails(SqlSession aoMybatisSession, EvaluationBean aoEvaluationBean)
			throws ApplicationException
	{
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.EVAL_BEAN, aoEvaluationBean);
		List<EvaluationBean> loEvalutionDetailsList = null;
		try
		{
			if (null != aoEvaluationBean)
			{
				if (aoEvaluationBean.getProposalTitle() != null)
				{
					// Appending Proposal Title with % for Filter Purpose
					StringBuffer loProposalTitleStrBfr = new StringBuffer(HHSConstants.PERCENT).append(
							aoEvaluationBean.getProposalTitle()).append(HHSConstants.PERCENT);
					aoEvaluationBean.setProposalTitle(loProposalTitleStrBfr.toString());
				}
				if (aoEvaluationBean.getOrganizationName() != null)
				{
					// Appending Provider Name with % for Filter Purpose
					StringBuffer loOrgNameSb = new StringBuffer(HHSConstants.PERCENT).append(
							aoEvaluationBean.getOrganizationName()).append(HHSConstants.PERCENT);
					aoEvaluationBean.setOrganizationName(loOrgNameSb.toString());
				}
				if (aoEvaluationBean.getEvaluationStatusList() != null
						&& aoEvaluationBean.getEvaluationStatusList().size() > 0
						&& (aoEvaluationBean.getEvaluationStatusList().contains(HHSConstants.EMPTY_STRING)))
				{
					aoEvaluationBean.setNotStartedStatus(HHSConstants.YES);
				}
				if (aoEvaluationBean.getEvaluationStatusList() != null
						&& aoEvaluationBean.getEvaluationStatusList().size() > 0
						&& (aoEvaluationBean.getEvaluationStatusList().contains(PropertyLoader.getProperty(
								HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_EVALUATE_PROPOSAL_TASK_IN_REVIEW))))
				{
					aoEvaluationBean.setInProgressStatus(HHSConstants.YES);
				}
				if (aoEvaluationBean.getEvaluationStatusList() != null
						&& aoEvaluationBean.getEvaluationStatusList().size() > 0
						&& (aoEvaluationBean.getEvaluationStatusList().contains(PropertyLoader.getProperty(
								HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.EVALUATE_PROPOSAL_TASK_SCORES_COMPLETED))))
				{
					aoEvaluationBean.setCompletedStatus(HHSConstants.YES);
				}
				if (aoEvaluationBean.getProposalStatusList() != null
						&& aoEvaluationBean.getProposalStatusList().size() > 0)
				{
					List<String> loStatusList = aoEvaluationBean.getProposalStatusList();
					if (loStatusList.contains(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
							HHSConstants.PROPOSAL_ACCEPTED_FOR_EVALUATION_KEY)))
					{
						loStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_PROPOSAL_EVALUATED));
						loStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_PROPOSAL_SCORES_RETURNED));
						loStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_PROPOSAL_SELECTED));
						loStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_PROPOSAL_NOT_SELECTED));
						aoEvaluationBean.setProposalStatusList(loStatusList);
					}
				}
				loEvalutionDetailsList = (List<EvaluationBean>) DAOUtil.masterDAO(aoMybatisSession, aoEvaluationBean,
						HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_EVALUATION_DETAILS,
						HHSConstants.COM_NYC_HHS_MODEL_EVALUATION_BEAN);
				String lsProposalTitle = aoEvaluationBean.getProposalTitle();
				if (null != lsProposalTitle)
				{ // Removing % sign i.e added previously
					lsProposalTitle = lsProposalTitle.substring(HHSConstants.INT_ONE);
					lsProposalTitle = lsProposalTitle.substring(HHSConstants.INT_ZERO, lsProposalTitle.length()
							- HHSConstants.INT_ONE);
					aoEvaluationBean.setProposalTitle(lsProposalTitle);
				}
				String lsOrganizationName = aoEvaluationBean.getOrganizationName();
				if (null != lsOrganizationName)
				{
					// Removing % sign i.e added previously
					lsOrganizationName = lsOrganizationName.substring(HHSConstants.INT_ONE);
					lsOrganizationName = lsOrganizationName.substring(HHSConstants.INT_ZERO,
							lsOrganizationName.length() - HHSConstants.INT_ONE);
					aoEvaluationBean.setOrganizationName(lsOrganizationName);
				}
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Exception occured while fetchEvaluationDetails from db ", aoAppEx);
			setMoState("Exception occured while Fetching evaluation details from db");
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured while fetchEvaluationDetails from db ", aoAppEx);
			setMoState("Exception occured while Fetching evaluation details from db");
			throw new ApplicationException("Exception occured while fetchEvaluationDetails from db", aoAppEx);
		}
		return loEvalutionDetailsList;
	}

	/**
	 * This method will fetch ACCO Comments corresponding to the Procurement Id
	 * and proposal Id
	 * <ul>
	 * <li>1. Retrieve procurement Id and proposal Id from the Channel object</li>
	 * <li>2. Create context data HashMap and populate the same with the
	 * procurement Id and proposal Id</li>
	 * <li>3. If the fetched procurement Id and proposal Id are not null then
	 * execute query <b>fetchAccoComments</b> to fetch the ACCO comments from
	 * the user_comments table corresponding to the procurement Id and proposal
	 * Id</li>
	 * <li>4. Return the fetched result</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession - mybatis SQL Session Object
	 * @param asProcurementId - string representation of Procurement Id
	 * @param asProposalId - string representation of ProposalId Id
	 * @return loAccoComments - Map<String,String> containing ACCO Comments
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, String>> fetchAccoComments(SqlSession aoMyBatisSession, String asProcurementId,
			String asProposalId) throws ApplicationException
	{
		// R5 starts : updated to List
		List<Map<String, String>> loAccoComments = null;
		// R5 ends : updated to List
		Map<String, String> loACCOMap = new HashMap<String, String>();
		loACCOMap.put(HHSConstants.PROPOSAL_ID, asProposalId);
		loACCOMap.put(HHSConstants.PROCUREMENT_ID, asProcurementId);

		LOG_OBJECT.Debug("Entered into fetching ACCO review comments::" + loACCOMap.toString());

		if (asProcurementId != null && asProposalId != null)
		{
			try
			{
				// R5 starts : updated to List
				loAccoComments = (List<Map<String, String>>) DAOUtil.masterDAO(aoMyBatisSession, loACCOMap,
						HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_ACCO_COMMENTS,
						HHSConstants.JAVA_UTIL_MAP);
				// R5 ends : updated to List
				setMoState("ACCO Review Comments fetched successfully corresponding to procurement Id and proposal Id");
			}
			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handles over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */
			catch (ApplicationException aoAppEx)
			{
				LOG_OBJECT
						.Error("Exception occured while fetching ACCO Review Comments corresponding to procurement Id and proposal Id",
								aoAppEx);
				setMoState("Transaction Failed:: EvaluationService:fetchAccoComments method - while fetching ACCO Review Comments corresponding to procurement Id and proposal Id");
				aoAppEx.setContextData(loACCOMap);
				throw aoAppEx;
			}
			// Handling Exception other than ApplicationException
			catch (Exception aoAppEx)
			{
				LOG_OBJECT
						.Error("Exception occured while fetching ACCO Review Comments corresponding to procurement Id and proposal Id",
								aoAppEx);
				setMoState("Transaction Failed:: EvaluationService:fetchAccoComments method - while fetching ACCO Review Comments corresponding to procurement Id and proposal Id");
				throw new ApplicationException(
						"Exception occured while fetching ACCO Review Comments corresponding to procurement Id and proposal Id",
						aoAppEx);
			}
		}
		return loAccoComments;
	}

	/**
	 * <p>
	 * This method fetches Evaluation score Details corresponding to a proposal
	 * Id and procurement id
	 * <ul>
	 * <li>1. Add multiple input parameters to a Map</li>
	 * <li>2. Execute query <b> displayEvaluationScoresDetails</b></li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMyBatisSession - mybatis SQL session
	 * @param asProposalId - Proposal Id
	 * @param asProcurementId - Procurement Id
	 * @return list of EvaluationFilter Bean
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<EvaluationBean> fetchEvaluationScoresDetails(SqlSession aoMyBatisSession, String asProcurementId,
			String asProposalId) throws ApplicationException
	{
		List<EvaluationBean> loEvaluationScoreList = null;
		// R5: updated Map<String, Object>
		Map<String, Object> loEvalCommentsMap = new HashMap<String, Object>();
		loEvalCommentsMap.put(HHSConstants.PROCUREMENT_ID, asProcurementId);
		loEvalCommentsMap.put(HHSConstants.PROPOSAL_ID, asProposalId);

		LOG_OBJECT.Debug("Entered into fetching evaluation score details::" + loEvalCommentsMap.toString());

		if (asProcurementId != null && asProposalId != null)
		{
			try
			{
				// Start : R5 Added
				List<String> loEvalStatusList = (List<String>) DAOUtil.masterDAO(aoMyBatisSession,
						loEvalCommentsMap.get(HHSConstants.PROPOSAL_ID), HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
						HHSR5Constants.GET_EVAL_STATUS_LIST, HHSConstants.JAVA_LANG_STRING);
				loEvalCommentsMap.put(HHSConstants.EVAL_STATUS_LIST, loEvalStatusList);
				// End : R5 Added
				loEvaluationScoreList = (List<EvaluationBean>) DAOUtil.masterDAO(aoMyBatisSession, loEvalCommentsMap,
						HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.DISPLAY_EVAL_SCORES_DETAILS,
						HHSConstants.JAVA_UTIL_MAP);
				setMoState("Evaluation Score details list fetched successfully corresponding to the procurement Id and Proposal Id");
			}
			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handles over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */
			catch (ApplicationException aoAppEx)
			{
				LOG_OBJECT
						.Error("Exception occured while fetching evaluation score details list corresponding to the procurement Id and Proposal Id",
								aoAppEx);
				setMoState("Transaction Failed:: EvaluationService:fetchEvaluationScoresDetails method - while fetching evaluation score details list corresponding to the procurement Id and Proposal Id");
				aoAppEx.setContextData(loEvalCommentsMap);
				throw aoAppEx;
			}
			// Handling Exception other than ApplicationException
			catch (Exception aoAppEx)
			{
				LOG_OBJECT
						.Error("Exception occured while fetching evaluation score details list corresponding to the procurement Id and Proposal Id",
								aoAppEx);
				setMoState("Transaction Failed:: EvaluationService:fetchEvaluationScoresDetails method - while fetching evaluation score details list corresponding to the procurement Id and Proposal Id");
				throw new ApplicationException(
						"Exception occured while fetching evaluation score details list corresponding to the procurement Id and Proposal Id",
						aoAppEx);
			}
		}
		return loEvaluationScoreList;
	}

	/**
	 * <p>
	 * This method fetches Evaluation score Details corresponding to a proposal
	 * Id and procurement id
	 * <ul>
	 * <li>1. Add multiple input parameters to a Map</li>
	 * <li>2. Fetch Evaluation score Details for the provided Proposal Id and
	 * procurement id using <b>fetchEvaluationScoreDetails</b></li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMyBatisSession - mybatis SQL session
	 * @param aoHmRequiredProps - required properties map
	 * @param asWobNumber work flow id
	 * @return list of EvaluationBean Bean
	 * @throws ApplicationException If an Exception occurs
	 */
	public List<EvaluationBean> fetchEvaluationScoresDetails(SqlSession aoMyBatisSession, HashMap aoHmRequiredProps,
			String asWobNumber) throws ApplicationException
	{
		List<EvaluationBean> loEvaluationScoreList = null;
		String lsProcurementId = null;
		String lsProposalId = null;

		if (aoHmRequiredProps != null)
		{
			try
			{
				HashMap loProcurementMap = (HashMap) aoHmRequiredProps.get(asWobNumber);
				if (null != loProcurementMap)
				{
					lsProcurementId = (String) loProcurementMap.get(P8Constants.PE_WORKFLOW_PROCUREMENT_ID);
					lsProposalId = (String) loProcurementMap.get(P8Constants.PE_WORKFLOW_PROPOSAL_ID);
					loEvaluationScoreList = fetchEvaluationScoresDetails(aoMyBatisSession, lsProcurementId,
							lsProposalId);
				}
			}
			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handles over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */
			catch (ApplicationException aoAppEx)
			{
				LOG_OBJECT
						.Error("Exception occured while fetching evaluation score details list corresponding to the procurement Id and Proposal Id",
								aoAppEx);
				setMoState("Transaction Failed:: EvaluationService:fetchEvaluationScoresDetails method - while fetching evaluation score details list corresponding to the procurement Id and Proposal Id");
				throw aoAppEx;
			}
			/**
			 * Any Exception from DAO class will be handles over here. It throws
			 * Application Exception back to Controllers calling method through
			 * Transaction framework
			 */
			catch (Exception aoAppEx)
			{
				LOG_OBJECT
						.Error("Exception occured while fetching evaluation score details list corresponding to the procurement Id and Proposal Id",
								aoAppEx);
				setMoState("Transaction Failed:: EvaluationService:fetchEvaluationScoresDetails method - while fetching evaluation score details list corresponding to the procurement Id and Proposal Id");
				throw new ApplicationException(
						"Exception occured while fetching evaluation score details list corresponding to the procurement Id and Proposal Id",
						aoAppEx);
			}
		}
		return loEvaluationScoreList;
	}

	/**
	 * <p>
	 * Below method query has been changed as per enhancement 5415 to fetch
	 * evaluation status Id. This method fetches Evaluation score Details
	 * corresponding to a proposal Id and procurement id
	 * <ul>
	 * <li>1. Add multiple input parameters to a Map</li>
	 * <li>2. Fetch Evaluation score Details and evaluation status Id for the
	 * provided Proposal Id and procurement id using
	 * <b>fetchEvaluatorCommentsDetails</b></li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMyBatisSession - mybatis SQL session
	 * @param asProposalId - Proposal Id
	 * @param asProcurementId - Procurement Id
	 * @return list of EvaluationFilter Bean
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<EvaluationBean> fetchEvaluatorCommentsDetails(SqlSession aoMyBatisSession, String asProcurementId,
			String asProposalId) throws ApplicationException
	{
		List<EvaluationBean> loEvalCommentsList = null;

		Map<String, String> loEvalCommentsMap = new HashMap<String, String>();
		loEvalCommentsMap.put(HHSConstants.PROCUREMENT_ID, asProcurementId);
		loEvalCommentsMap.put(HHSConstants.PROPOSAL_ID, asProposalId);

		LOG_OBJECT.Debug("Entered into fetching evaluator's comments details::" + loEvalCommentsMap.toString());

		if (asProcurementId != null && asProposalId != null)
		{
			try
			{
				// Below query has been changed as per enhancement 5415 to fetch
				// evaluation status Id.
				loEvalCommentsList = (List<EvaluationBean>) DAOUtil.masterDAO(aoMyBatisSession, loEvalCommentsMap,
						HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_EVALUATOR_COMMENTS_DETAILS,
						HHSConstants.JAVA_UTIL_MAP);
				setMoState("Evaluator's Comments List fetched successfully corresponding to the procurement Id and Proposal Id");
			}
			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handles over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */
			catch (ApplicationException aoAppEx)
			{
				LOG_OBJECT
						.Error("Exception occured while fetching evaluator's comments list corresponding to the procurement Id and Proposal Id",
								aoAppEx);
				setMoState("Transaction Failed:: EvaluationService:fetchEvaluatorCommentsDetails method - while fetching evaluator's comments list corresponding to the procurement Id and Proposal Id");
				aoAppEx.setContextData(loEvalCommentsMap);
				throw aoAppEx;
			}
			// Handling Exception other than ApplicationException
			catch (Exception aoAppEx)
			{
				LOG_OBJECT
						.Error("Exception occured while fetching evaluator's comments list corresponding to the procurement Id and Proposal Id",
								aoAppEx);
				setMoState("Transaction Failed:: EvaluationService:fetchEvaluatorCommentsDetails method - while fetching evaluator's comments list corresponding to the procurement Id and Proposal Id");
				throw new ApplicationException(
						"Exception occured while fetching evaluator's comments list corresponding to the procurement Id and Proposal Id",
						aoAppEx);
			}
		}
		return loEvalCommentsList;
	}

	/**
	 * This method fetches Evaluation comments Details corresponding to a
	 * proposal Id and procurement id from task detail map
	 * 
	 * <ul>
	 * <li>Get proposal Id and procurement Id from task details map</li>
	 * <li>
	 * Execute query with id <b>fetchEvaluatorCommentsDetails</b> from
	 * evaluation mapper</li>
	 * <li>Return evaluation bean list containing evaluator's comments to
	 * controller</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession - mybatis SQL session
	 * @param aoHmRequiredProps Properties Map
	 * @param asWobNumber Wob Number
	 * @return list of EvaluationFilter Bean
	 * @throws ApplicationException If any Exception occurs
	 */
	@SuppressWarnings("rawtypes")
	public List<EvaluationBean> fetchEvaluatorCommentsDetails(SqlSession aoMyBatisSession, HashMap aoHmRequiredProps,
			String asWobNumber) throws ApplicationException
	{
		List<EvaluationBean> loEvalCommentsList = null;
		String lsProcurementId = null;
		String lsProposalId = null;
		if (aoHmRequiredProps != null)
		{
			try
			{
				HashMap loProcurementMap = (HashMap) aoHmRequiredProps.get(asWobNumber);
				if (null != loProcurementMap)
				{
					lsProcurementId = (String) loProcurementMap.get(P8Constants.PE_WORKFLOW_PROCUREMENT_ID);
					lsProposalId = (String) loProcurementMap.get(P8Constants.PE_WORKFLOW_PROPOSAL_ID);
					loEvalCommentsList = fetchEvaluatorCommentsDetails(aoMyBatisSession, lsProcurementId, lsProposalId);
				}
			}
			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handles over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */
			catch (ApplicationException aoAppEx)
			{
				LOG_OBJECT
						.Error("Exception occured while fetching evaluator's comments list corresponding to the procurement Id and Proposal Id",
								aoAppEx);
				setMoState("Transaction Failed:: EvaluationService:fetchEvaluatorCommentsDetails method - while fetching evaluator's comments list corresponding to the procurement Id and Proposal Id");
				throw aoAppEx;
			}
			/**
			 * Any Exception from DAO class will be handles over here. It throws
			 * Application Exception back to Controllers calling method through
			 * Transaction framework
			 */
			catch (Exception aoAppEx)
			{
				LOG_OBJECT
						.Error("Exception occured while fetching evaluator's comments list corresponding to the procurement Id and Proposal Id",
								aoAppEx);
				setMoState("Transaction Failed:: EvaluationService:fetchEvaluatorCommentsDetails method - while fetching evaluator's comments list corresponding to the procurement Id and Proposal Id");
				throw new ApplicationException(
						"Exception occured while fetching evaluator's comments list corresponding to the procurement Id and Proposal Id",
						aoAppEx);
			}
		}
		return loEvalCommentsList;
	}

	/**
	 * This method will fetch estimated procurement value corresponding to the
	 * procurement Id.
	 * <ul>
	 * <li>1. Retrieve procurement Id from the channel</li>
	 * <li>2. Execute query <b>fetchProcurementValue</b></li>
	 * <li>3. Return fetched value for estimated procurement value</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProcurementId - string representation of procurement Id
	 * @return lsProcurementValue - string representation of estimated
	 *         procurement value
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public String fetchProcurementValue(SqlSession aoMybatisSession, String asProcurementId)
			throws ApplicationException
	{
		String lsProcurementValue = null;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		try
		{
			lsProcurementValue = (String) DAOUtil.masterDAO(aoMybatisSession, asProcurementId,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_PROCUREMENT_VALUE,
					HHSConstants.JAVA_LANG_STRING);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			setMoState("Error while fetching estimated procurement value");
			aoExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching estimated procurement value:", aoExp);
			throw aoExp;
		}
		// handling exception other than application exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error while fetching estimated procurement value", aoEx);
			setMoState("Error while fetching estimated procurement value" + aoEx.getMessage());
			throw new ApplicationException("Error while fetching estimated procurement value", aoEx);
		}
		return lsProcurementValue;
	}

	/**
	 * This method will fetch Award Amount corresponding to the Evaluation Pool
	 * Mapping Id.
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. Retrieve Evaluation Pool Mapping Id from the channel</li>
	 * <li>2. Execute query <b>fetchAwardAmount</b></li>
	 * <li>3. Return fetched value for Award Amount</li>
	 * </ul>
	 * 
	 * Change: updated query for competition pool
	 * 
	 * Changed By: Pallavi
	 * 
	 * Change Date: 11 Mar 2014
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param asEvalPoolMappingId Evaluation Pool Mapping id
	 * @return map containing award amount details
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> fetchAwardAmount(SqlSession aoMybatisSession, String asEvalPoolMappingId)
			throws ApplicationException
	{
		Map<String, String> loAwardAmount = null;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvalPoolMappingId);
		try
		{
			Map<String, String> loInputParam = new HashMap<String, String>();
			loInputParam.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvalPoolMappingId);
			loInputParam.put(HHSConstants.STATUS, PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_PROPOSAL_SELECTED));
			loAwardAmount = (Map<String, String>) DAOUtil.masterDAO(aoMybatisSession, loInputParam,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_AWARD_AMOUNT,
					HHSConstants.JAVA_UTIL_MAP);
			setMoState("Successfully fetched award amount for evaluation pool mapping Id:" + asEvalPoolMappingId);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			setMoState("Error while fetching Award Amount for evaluation pool mapping Id:" + asEvalPoolMappingId);
			aoExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching Award Amount for evaluation pool mapping Id:" + asEvalPoolMappingId,
					aoExp);
			throw aoExp;
		}
		// handling exception other than application exception.
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetching Award Amount for evaluation pool mapping Id:" + asEvalPoolMappingId,
					aoExp);
			setMoState("Error while fetching Award Amount for evaluation pool mapping Id:" + asEvalPoolMappingId
					+ aoExp.getMessage());
			throw new ApplicationException("Error while fetching Award Amount for evaluation pool mapping Id:"
					+ asEvalPoolMappingId, aoExp);
		}
		return loAwardAmount;
	}

	/**
	 * This method will fetch count for providers corresponding to the
	 * evaluation pool mapping Id.
	 * <ul>
	 * <li>1. Retrieve evaluation pool mapping Id from the channel</li>
	 * <li>2. Execute query <b>fetchFinalizeProcurementCount</b></li>
	 * <li>3. Return count for no. of providers and awards</li>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * Change: Updated query for competition pool
	 * 
	 * Changed By: Pallavi
	 * 
	 * Change Date: 11 Mar 2014
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asEvalPoolMappingId - string representation of evaluation pool
	 *            mapping Id
	 * @return loNoOfProvidersAndAmount - AwardAmount
	 * @throws ApplicationException If an Exception occurs
	 */
	public Integer countFinalizeProcurementDetails(SqlSession aoMybatisSession, String asEvalPoolMappingId)
			throws ApplicationException
	{
		Integer loNoOfProvidersAndAmount = null;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvalPoolMappingId);
		try
		{
			loNoOfProvidersAndAmount = (Integer) DAOUtil.masterDAO(aoMybatisSession, asEvalPoolMappingId,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_FINALIZE_PROCUREMENT_COUNT,
					HHSConstants.JAVA_LANG_STRING);
			setMoState("Successfully fetched provider count for evaluation pool mapping id:" + asEvalPoolMappingId);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			setMoState("Error while getting provider count for evaluation pool mapping id:" + asEvalPoolMappingId);
			aoExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error(
					"Error while getting provider count for evaluation pool mapping id:" + asEvalPoolMappingId, aoExp);
			throw aoExp;
		}
		// handling exception other than application exception.
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error(
					"Error while getting provider count for evaluation pool mapping id:" + asEvalPoolMappingId, aoExp);
			setMoState("Error while getting provider count for evaluation pool mapping id:" + asEvalPoolMappingId
					+ aoExp.getMessage());
			throw new ApplicationException("Error while getting provider count for evaluation pool mapping id:"
					+ asEvalPoolMappingId, aoExp);
		}
		return loNoOfProvidersAndAmount;
	}

	/**
	 * This method will update the status of the procurement corresponding to
	 * the procurement Id
	 * <ul>
	 * <li>1. Retrieve procurement Id from the request</li>
	 * <li>2. Execute query <b>getProcurementStatus</b> to retrieve
	 * procurement_status corresponding to the procurement Id in the procurement
	 * table</li>
	 * <li>3. Execute query <b>updateProcurementStatus</b> if procurement status
	 * is proposal recieved</li>
	 * <li>4. Execute query <b>preserveOldStatus</b> if procurement status is
	 * evaluation complete or selection made</li>
	 * <li>5. Return boolean flag value</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession - mybatis SQL session
	 * @param asProcurementId - string representation of procurement Id
	 * @return loSuccessStatus - Boolean
	 * @throws ApplicationException If an ApplicationException occurs
	 */

	public Boolean updateProcurementStatus(SqlSession aoMyBatisSession, String asProcurementId)
			throws ApplicationException
	{
		Boolean loSuccessStatus = Boolean.FALSE;
		Map<String, String> loParamMap = new HashMap<String, String>();
		loParamMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		loParamMap.put(HHSConstants.AS_STATUS_ID, PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_PROCUREMENT_EVALUATIONS_COMPLETE));
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		try
		{
			String lsProcStatus = (String) DAOUtil.masterDAO(aoMyBatisSession, asProcurementId,
					HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER, HHSConstants.GET_PROC_STATUS,
					HHSConstants.JAVA_LANG_STRING);
			if (lsProcStatus != null
					&& !lsProcStatus.isEmpty()
					&& lsProcStatus.equalsIgnoreCase(PropertyLoader
							.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
									HHSConstants.STATUS_PROCUREMENT_PROPOSALS_RECEIVED)))
			{
				DAOUtil.masterDAO(aoMyBatisSession, loParamMap, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
						HHSConstants.UPDATE_PROC_STATUS, HHSConstants.JAVA_UTIL_MAP);
				loSuccessStatus = true;
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoExp)
		{
			setMoState("Error while updating the status of the procurement" + asProcurementId);
			aoExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while updating the status of the procurement", aoExp);
			throw aoExp;

		}
		// handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while updating the status of the procurement", aoExp);
			setMoState("Error while updating the status of the procurement");
			throw new ApplicationException("Error while updating the status of the procurement", aoExp);

		}
		return loSuccessStatus;
	}

	/**
	 * This method updates the proposal status as non responsive corresponding
	 * to the proposal Id
	 * <ul>
	 * <li>1. Fetch proposal Id from the channel object</li>
	 * <li>2. Create one HashMap for context data and populate it with proposal
	 * Id</li>
	 * <li>3. Execute query <b>updateproposalstatusnonresponsive</b>
	 * corresponding to the proposal Id and set the boolean status flag value as
	 * true</li>
	 * <li>4. Return boolean status flag</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession - mybatis SQL session
	 * @param asProposalId - string representation of proposal Id
	 * @return loSuccessStatus - boolean status flag
	 * @throws ApplicationException - IF an ApplicationException occurs
	 */
	public Boolean markProposalNonResponsive(SqlSession aoMyBatisSession, String asProposalId)
			throws ApplicationException
	{
		Boolean loSuccessStatus = Boolean.FALSE;
		Map<String, Object> loMarkProposalNonResponsiveMap = new HashMap<String, Object>();
		loMarkProposalNonResponsiveMap.put(HHSConstants.PROPOSAL_ID_KEY, asProposalId);
		try
		{
			String lsProposalStatusId = (String) PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_PROPOSAL_NON_RESPONSIVE);
			loMarkProposalNonResponsiveMap.put(HHSConstants.PROPOSAL_STATUS_ID_KEY, lsProposalStatusId);
			DAOUtil.masterDAO(aoMyBatisSession, loMarkProposalNonResponsiveMap,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.UPDATE_PROPOSAL_STATUS_NON_RESPONSIVE,
					HHSConstants.JAVA_UTIL_MAP);
			loSuccessStatus = true;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoExp)
		{
			setMoState("Error while updating the status of the Proposal" + asProposalId);
			aoExp.setContextData(loMarkProposalNonResponsiveMap);
			LOG_OBJECT.Error("Error while updating the status of the Proposal", aoExp);
			throw aoExp;
		}
		// handling exception other than ApplicationException
		catch (Exception aoExp)
		{
			setMoState("Error while updating the status of the Proposal" + asProposalId);
			LOG_OBJECT.Error("Error while updating the status of the Proposal", aoExp);
			throw new ApplicationException("Error while updating the status of the Proposal", aoExp);
		}
		return loSuccessStatus;
	}

	/**
	 * This method will update award review status corresponding to the
	 * procurement Id and evaluation pool mapping Id
	 * 
	 * <ul>
	 * <li>1. Retrieve procurement Id and evaluation pool mapping Id from input
	 * parameter map</li>
	 * <li>2. Execute query<b>updateAwardReviewStatus</b> from evaluation mapper
	 * corresponding to procurement Id and evaluation pool mapping Id</li>
	 * <li>3. If no rows updated, Inserts award review status by executing
	 * <b>insertAwardReviewStatus</b> from evaluation mapper</li>
	 * <li>4. Return boolean flag value</li>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * 
	 * Change: removed unused method parameter
	 * 
	 * Changed By: Pallavi
	 * 
	 * Change Date: 12 Mar 2014
	 * 
	 * @param aoMyBatisSession SqlSession
	 * @param aoProcurementInfoMap procurement information map
	 * @return Boolean loSuccessStatus
	 * @throws ApplicationException If an ApplicationException occurs
	 */

	public Boolean updateAwardReviewStatus(SqlSession aoMyBatisSession, Map<String, Object> aoProcurementInfoMap)
			throws ApplicationException
	{
		Boolean loSuccessStatus = Boolean.FALSE;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap
				.put(HHSConstants.PROCUREMENT_ID_KEY, aoProcurementInfoMap.get(HHSConstants.PROCUREMENT_ID_KEY));
		loContextDataMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID,
				aoProcurementInfoMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID));
		try
		{
			Integer loUpdatedRowsCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoProcurementInfoMap,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.UPDATE_AWARD_REVIEW_STATUS,
					HHSConstants.JAVA_UTIL_MAP);
			if (loUpdatedRowsCount == null || loUpdatedRowsCount == HHSConstants.INT_ZERO)
			{
				DAOUtil.masterDAO(aoMyBatisSession, aoProcurementInfoMap, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
						HHSConstants.INSERT_AWARD_REVIEW_STATUS, HHSConstants.JAVA_UTIL_MAP);
			}
			loSuccessStatus = true;
			setMoState("Successfully updated award review status for evaluation pool mapping Id:"
					+ aoProcurementInfoMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID));
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoExp)
		{
			setMoState("Error while updating the status of the award for evaluation pool mapping Id:"
					+ aoProcurementInfoMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID));
			aoExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while updating the status of the award for evaluation pool mapping Id:"
					+ aoProcurementInfoMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID), aoExp);
			throw aoExp;
		}
		// handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while updating the status of the award for evaluation pool mapping Id:"
					+ aoProcurementInfoMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID), aoExp);
			setMoState("Error while updating the status of the award for evaluation pool mapping Id:"
					+ aoProcurementInfoMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID));
			throw new ApplicationException(
					"Error while updating the status of the award for evaluation pool mapping Id:"
							+ aoProcurementInfoMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID), aoExp);
		}
		return loSuccessStatus;
	}

	/**
	 * Changes done for enhancement 6574 for Release 3.10.0
	 * @param aoMyBatisSession
	 * @param aoProcurementInfoMap
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<String> fetchUpdatedContracts(SqlSession aoMyBatisSession, Map<String, Object> aoProcurementInfoMap)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered fetchUpdatedContracts for evaluation pool mapping Id:"
				+ aoProcurementInfoMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID));
		List<String> loContractIdsList = null;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap
				.put(HHSConstants.PROCUREMENT_ID_KEY, aoProcurementInfoMap.get(HHSConstants.PROCUREMENT_ID_KEY));
		loContextDataMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID,
				aoProcurementInfoMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID));
		try
		{
			loContractIdsList = (List<String>) DAOUtil.masterDAO(aoMyBatisSession, aoProcurementInfoMap,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_UPDATED_CONTRACTS,
					HHSConstants.JAVA_UTIL_MAP);
			setMoState("Successfully updated award review status for evaluation pool mapping Id:"
					+ aoProcurementInfoMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID));
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoExp)
		{
			setMoState("Error while fetchUpdatedContracts for evaluation pool mapping Id:"
					+ aoProcurementInfoMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID));
			aoExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error(
					"Error while fetchUpdatedContracts for evaluation pool mapping Id:"
							+ aoProcurementInfoMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID), aoExp);
			throw aoExp;
		}
		// handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error(
					"Error while fetchUpdatedContracts for evaluation pool mapping Id:"
							+ aoProcurementInfoMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID), aoExp);
			setMoState("Error while fetchUpdatedContracts for evaluation pool mapping Id:"
					+ aoProcurementInfoMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID));
			throw new ApplicationException("Error whilefetchUpdatedContracts for evaluation pool mapping Id:"
					+ aoProcurementInfoMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID), aoExp);
		}
		return loContractIdsList;
	}

	/**
	 * Changes done for enhancement 6577 for Release 3.10.0
	 * @param aoMyBatisSession
	 * @param aoProcurementInfoMap
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<String> fetchContractsForCancellingTasks(SqlSession aoMyBatisSession,
			HashMap<String, String> aoProcurementInfoMap, String aoCancelFlag) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered fetchContractsForCancellingTasks for evaluation pool mapping Id:"
				+ aoProcurementInfoMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID));
		List<String> loContractIdsList = null;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, aoProcurementInfoMap.get(HHSConstants.PROCUREMENT_ID));
		loContextDataMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID,
				aoProcurementInfoMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID));
		try
		{
			if (null != aoCancelFlag && aoCancelFlag.equalsIgnoreCase(HHSConstants.STRING_TRUE))
			{
				loContractIdsList = (List<String>) DAOUtil.masterDAO(aoMyBatisSession, aoProcurementInfoMap,
						HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_CONTRACTS_FOR_CANCEL_TASKS,
						HHSConstants.JAVA_UTIL_HASH_MAP);
				setMoState("Successfully updated award review status for evaluation pool mapping Id:"
						+ aoProcurementInfoMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID));
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoExp)
		{
			setMoState("Error while fetchContractsForCancellingTasks for evaluation pool mapping Id:"
					+ aoProcurementInfoMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID));
			aoExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetchContractsForCancellingTasks for evaluation pool mapping Id:"
					+ aoProcurementInfoMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID), aoExp);
			throw aoExp;
		}
		// handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetchContractsForCancellingTasks for evaluation pool mapping Id:"
					+ aoProcurementInfoMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID), aoExp);
			setMoState("Error while fetchContractsForCancellingTasks for evaluation pool mapping Id:"
					+ aoProcurementInfoMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID));
			throw new ApplicationException(
					"Error while fetchContractsForCancellingTasks for evaluation pool mapping Id:"
							+ aoProcurementInfoMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID), aoExp);
		}
		if (null != loContractIdsList)
		{
			LOG_OBJECT.Debug("EXITED fetchContractsForCancellingTasks with list size ::: " + loContractIdsList.size());
		}
		return loContractIdsList;
	}

	/**
	 * Changes done for enhancement 6577 for Release 3.10.0
	 * @param aoMyBatisSession
	 * @param aoProcurementInfoMap
	 * @return
	 * @throws ApplicationException
	 */
	public String checkCancelCompetitionRule(SqlSession aoMyBatisSession, HashMap<String, String> aoProcurementInfoMap)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered checkCancelCompetitionRule for evaluation pool mapping Id:"
				+ aoProcurementInfoMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID));
		String loCancelStatusFlag = HHSConstants.STRING_TRUE;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID,
				aoProcurementInfoMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID));
		try
		{
			Integer loContractsCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoProcurementInfoMap,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_REGISTERED_CLOSD_CONTRACTS_COUNT,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			if (null != loContractsCount && loContractsCount > 0)
			{
				loCancelStatusFlag = HHSConstants.FALSE;
			}
			setMoState("Successfully updated award review status for evaluation pool mapping Id:"
					+ aoProcurementInfoMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID));
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoExp)
		{
			setMoState("Error while checkCancelCompetitionRule for evaluation pool mapping Id:"
					+ aoProcurementInfoMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID));
			aoExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while checkCancelCompetitionRule for evaluation pool mapping Id:"
					+ aoProcurementInfoMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID), aoExp);
			throw aoExp;
		}
		// handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while checkCancelCompetitionRule for evaluation pool mapping Id:"
					+ aoProcurementInfoMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID), aoExp);
			setMoState("Error while checkCancelCompetitionRule for evaluation pool mapping Id:"
					+ aoProcurementInfoMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID));
			throw new ApplicationException("Error while checkCancelCompetitionRule for evaluation pool mapping Id:"
					+ aoProcurementInfoMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID), aoExp);
		}
		LOG_OBJECT.Debug("Exited checkCancelCompetitionRule for evaluation pool mapping Id:"
				+ aoProcurementInfoMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID) + "---CancelStatus--- "
				+ loCancelStatusFlag);
		return loCancelStatusFlag;
	}

	/**
	 * Added for enhancement 6574 for Release 3.10.0
	 * @param aoContractIdList
	 * @param aoProvSelectionMap
	 */
	@SuppressWarnings("unchecked")
	public HashMap fetchContractIdsForUtilityWorkflow(List<String> aoContractIdList, HashMap aoHMWFRequiredProps,
			String aoAwardAmount) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered fetchContractIdsForUtilityWorkflow for Contractids ::: "
				+ aoContractIdList.toString() + " and awardAmount::: " + aoAwardAmount);
		String[] loContractIdArray = null;
		try
		{
			if (null != aoAwardAmount && !aoAwardAmount.isEmpty())
			{
				aoHMWFRequiredProps.put(P8Constants.PE_WORKFLOW_PROCUREMENT_AWARD_AMOUNT, aoAwardAmount);
			}
			if (null != aoContractIdList && !aoContractIdList.isEmpty())
			{
				loContractIdArray = new String[aoContractIdList.size()];
				int liInteger = 0;
				for (String lsContractId : aoContractIdList)
				{
					loContractIdArray[liInteger++] = lsContractId;
				}
				aoHMWFRequiredProps.put(P8Constants.PE_WORKFLOW_LIST_OF_SELECTED_CONTRACTS, loContractIdArray);
				LOG_OBJECT.Debug("Exited fetchContractIdsForUtilityWorkflow for Contractids ::: "
						+ loContractIdArray.toString());
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception occured while fetchContractIdsForUtilityWorkflow for Contractids ::: "
					+ aoContractIdList.toString(), aoEx);
			setMoState("Transaction Failed:: EvaluationService:fetchContractIdsForUtilityWorkflow for Contractids ::: "
					+ aoContractIdList.toString());
			throw new ApplicationException(
					"Exception occured while fetchContractIdsForUtilityWorkflow for Contractids ::: "
							+ aoContractIdList.toString(), aoEx);
		}
		return aoHMWFRequiredProps;
	}

	/**
	 * Added for enhancement 6577 for Release 3.10.0
	 * @param aoContractIdList
	 * @param aoProvSelectionMap
	 */
	@SuppressWarnings("unchecked")
	public HashMap insertContractIdsForUtilityWorkflow(List<String> aoContractIdList, HashMap aoHMWFRequiredProps)
			throws ApplicationException
	{
		String[] loContractIdArray = null;
		try
		{
			if (null != aoContractIdList && !aoContractIdList.isEmpty())
			{
				LOG_OBJECT.Debug("Entered insertContractIdsForUtilityWorkflow for Contractids ::: "
						+ aoContractIdList.toString());

				loContractIdArray = new String[aoContractIdList.size()];
				int liInteger = 0;
				for (String lsContractId : aoContractIdList)
				{
					loContractIdArray[liInteger++] = lsContractId;
				}
				aoHMWFRequiredProps.put(P8Constants.PE_WORKFLOW_LIST_OF_SELECTED_CONTRACTS, loContractIdArray);
				LOG_OBJECT.Debug("Exited insertContractIdsForUtilityWorkflow for Contractids ::: "
						+ loContractIdArray.toString());
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception occured while insertContractIdsForUtilityWorkflow for Contractids ::: ", aoEx);
			setMoState("Transaction Failed:: EvaluationService:insertContractIdsForUtilityWorkflow for Contractids ::: ");
			throw new ApplicationException(
					"Exception occured while insertContractIdsForUtilityWorkflow for Contractids ::: ", aoEx);
		}
		return aoHMWFRequiredProps;
	}

	/**
	 * The method will fetch the award review comments corresponding to the
	 * evaluation pool mapping Id
	 * <ul>
	 * <li>1. Retrieve evaluation pool mapping Id from the channel object</li>
	 * <li>2. Execute query <b>fetchAwardReviewComments</b> to fetch the award
	 * review comments from the user_comments table corresponding to the
	 * evaluation pool mapping Id</li>
	 * <li>3. Return the fetched review comments</li>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * Change: Updated query for competition pool
	 * 
	 * Changed By: Pallavi
	 * 
	 * Change Date: 11 Mar 2014
	 * 
	 * @param aoMyBatisSession - mybatis SQL session
	 * @param asEvalPoolMappingId - string representation of evaluation pool
	 *            mapping Id
	 * @param asProcurementId - string representation of procurement mapping Id
	 * @param asRfpReleasedBeforeR4Flag - string representation of rfp release
	 *            flag
	 * @return loViewAwardComment - map of Award Review Comments
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Map fetchReviewAwardComments(SqlSession aoMyBatisSession, String asEvalPoolMappingId,
			String asProcurementId, String asRfpReleasedBeforeR4Flag) throws ApplicationException
	{
		Map<String, String> loViewAwardComment = null;
		Map<String, String> loAwardMap = new HashMap<String, String>();
		loAwardMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvalPoolMappingId);
		LOG_OBJECT.Debug("Entered into fetching award review comments::" + loAwardMap.toString());
		try
		{
			if (null == asRfpReleasedBeforeR4Flag)
			{
				loViewAwardComment = (Map<String, String>) DAOUtil.masterDAO(aoMyBatisSession, asEvalPoolMappingId,
						HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_AWARD_REVIEW_COMMENTS,
						HHSConstants.JAVA_LANG_STRING);
				setMoState("Award Review Comments fetched successfully corresponding to evalPoolMappingId:"
						+ asEvalPoolMappingId);
			}
			else
			{
				loViewAwardComment = (Map<String, String>) DAOUtil.masterDAO(aoMyBatisSession, asProcurementId,
						HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_AWARD_REVIEW_COMMENTS,
						HHSConstants.JAVA_LANG_STRING);
				setMoState("Award Review Comments fetched successfully corresponding to asProcurementId:"
						+ asProcurementId);
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			setMoState("Transaction Failed:: EvaluationService:fetchAwardReviewComment method -  while fetching Award Review Comments corresponding to the evalPoolMappingId:"
					+ asEvalPoolMappingId);
			aoAppEx.setContextData(loAwardMap);
			LOG_OBJECT.Error(
					"Exception occured while fetching Award Review Comments corresponding to the evaluation pool mapping Id:"
							+ asEvalPoolMappingId, aoAppEx);
			throw aoAppEx;
		}
		// Handling Exception other than ApplicationException
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error(
					"Exception occured while fetching Award Review Comments corresponding to the evaluation pool mapping Id:"
							+ asEvalPoolMappingId, aoEx);
			setMoState("Transaction Failed:: EvaluationService:fetchAwardReviewComment method -  while fetching Award Review Comments corresponding to the evalPoolMappingId:"
					+ asEvalPoolMappingId);
			throw new ApplicationException(
					"Exception occured while fetching Award Review Comments corresponding to the evaluation pool mapping Id: "
							+ asEvalPoolMappingId, aoEx);
		}
		return loViewAwardComment;
	}

	/**
	 * The Method will fetch details for Internal/External evaluator details on
	 * basis of procurement id and evaluation pool mapping id
	 * <ul>
	 * <li>1.run query id fetchIntExtProposalDetails</li>
	 * <li>2.fetch the details</li>
	 * <li>3.fetch all the parameter in EvaluationDetailBean and passed in the
	 * list</li>
	 * <li>4.return the list</li>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * Change: updated query for competition pool
	 * 
	 * Changed by: Pallavi
	 * 
	 * Change date: 7 Jan 2014
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param asProcurementId procurement id
	 * @param asEvalPoolMappingId - evaluation pool mapping id
	 * @return loIntExtProposalDetails - List of evaluation details
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<EvaluationDetailBean> fetchIntExtProposalDetails(SqlSession aoMybatisSession, String asProcurementId,
			String asEvalPoolMappingId) throws ApplicationException
	{
		List<EvaluationDetailBean> loIntExtProposalDetails = null;
		Map<String, String> loInputParam = new HashMap<String, String>();
		loInputParam.put(HHSConstants.LO_PROCUREMENT_ID, asProcurementId);
		loInputParam
				.put(HHSConstants.LS_DRAFT_ID, PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_PROPOSAL_DRAFT));
		loInputParam.put(HHSConstants.MARK_PROPOSAL_NON_RESPONSIVE, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_NON_RESPONSIVE));
		loInputParam.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvalPoolMappingId);
		try
		{
			loIntExtProposalDetails = (List<EvaluationDetailBean>) DAOUtil.masterDAO(aoMybatisSession, loInputParam,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_INT_EXT_PROPOSAL_DETAILS,
					HHSConstants.JAVA_UTIL_MAP);
			setMoState("Successfully fetched Internal/External evaluator details for evaluation pool mapping id:"
					+ asEvalPoolMappingId);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoExp)
		{
			setMoState("Error while fetching details for Internal/External evaluator for evaluation pool mapping id:"
					+ asEvalPoolMappingId);
			aoExp.setContextData(loInputParam);
			LOG_OBJECT.Error(
					"Error while fetching details for Internal/External evaluator for evaluation pool mapping id:"
							+ asEvalPoolMappingId, aoExp);
			LOG_OBJECT.Error("Error while fetching details for Internal/External evaluator:", aoExp);
			throw aoExp;
		}
		// handling exception other than ApplicationException
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error(
					"Error while fetching details for Internal/External evaluator for evaluation pool mapping id:"
							+ asEvalPoolMappingId, aoExp);
			setMoState("Error while fetching details for Internal/External evaluator for evaluation pool mapping id:"
					+ asEvalPoolMappingId);
			throw new ApplicationException(
					"Error while fetching details for Internal/External evaluator for evaluation pool mapping id:"
							+ asEvalPoolMappingId, aoExp);
		}
		return loIntExtProposalDetails;
	}

	/**
	 * The Method will insert details in Evaluation_STATUS table against
	 * procurement id
	 * <ul>
	 * <li>1.run query id <b>updateEvaluationStatus</b></li>
	 * <li>2.insert the details</li>
	 * <li>3.return boolean as true when details are inserted in db</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession
	 * @param aoProcMap procurement details map
	 * @param aoIntExtProposalDetails internal external evaluator details bean
	 * @return loUpdateEvaluationStatus - Boolean
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean insertEvaluationStatus(SqlSession aoMybatisSession, HashMap<String, String> aoProcMap,
			List<EvaluationDetailBean> aoIntExtProposalDetails) throws ApplicationException
	{
		Boolean loUpdateEvaluationStatus = false;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		try
		{
			for (EvaluationDetailBean loIntExtDetails : aoIntExtProposalDetails)
			{
				loIntExtDetails.setProcurementId(aoProcMap.get(HHSConstants.PROCUREMENT_ID_KEY));
				loIntExtDetails.setCreatedByUserId(aoProcMap.get(HHSConstants.LS_USER_ID));
				loIntExtDetails.setModifiedByUserId(aoProcMap.get(HHSConstants.LS_USER_ID));
				loIntExtDetails.setStatusId(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_EVALUATE_PROPOSAL_TASK_IN_REVIEW));
				// inserting Evaluation Status data against procurementId
				DAOUtil.masterDAO(aoMybatisSession, loIntExtDetails, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
						HHSConstants.INSERT_EVALUATION_STATUS, HHSConstants.EVALUATION_DETAIL_BEAN);
				loUpdateEvaluationStatus = true;
			}
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoExp)
		{
			setMoState("Error while inserting Evaluation  data :");
			aoExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while inserting Evaluation  data :", aoExp);
			throw aoExp;
		}
		// handling exception other than ApplicationException
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while inserting Evaluation  data :", aoExp);
			setMoState("Error while inserting Evaluation  data :");
			throw new ApplicationException("Error while inserting Evaluation  data :", aoExp);
		}
		return loUpdateEvaluationStatus;
	}

	/**
	 * The Method will remove Evaluation Settings Internal and External Data,
	 * evaluation results data, evaluation scores data, award data when cancel
	 * evaluation task buttin is click on evaluation status screen
	 * 
	 * <ul>
	 * 
	 * <li>1. Add input parameters to map</li>
	 * <li>2.Delete Evaluation Results data by executing query
	 * <code>deleteEvaluationResults</code> from <code>evaluation</code> mapper.
	 * </li>
	 * <li>3.Delete Evaluation Score data by executing query
	 * <code>deleteEvaluationScore</code> from <code>evaluation</code> mapper.</li>
	 * <li>4.Delete Evaluation Status data by executing query
	 * <code>deleteEvaluationStatus</code> from <code>evaluation</code> mapper.</li>
	 * <li>5.Delete Internal Evaluation Settings data by executing query
	 * <code>deleteEvaluationSettingsInternal</code> from
	 * <code>evaluation</code> mapper.</li>
	 * <li>6.Delete External Evaluation Settings data by executing query
	 * <code>deleteEvaluationSettingsExternal</code> from
	 * <code>evaluation</code> mapper.</li>
	 * <li>7.Delete Award data by executing query <code>deleteAwardData</code>
	 * from <code>evaluation</code> mapper.</li>
	 * <li>8.Update evaluator count by executing query
	 * <b>updateEvaluatorCount</b> from <code>evaluation</code> mapper.</li>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * Change: added queries for competition pool
	 * 
	 * Changed By: Pallavi
	 * 
	 * Change Date: 15 Jan 2014
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param asProcurementId procurement id
	 * @param asEvalPoolMappingId evaluation pool mapping Id
	 * @return delete status - boolean
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean deleteEvaluationSettingData(SqlSession aoMybatisSession, String asProcurementId,
			String asEvalPoolMappingId) throws ApplicationException
	{
		Boolean loDeleteEvaluationFlag = false;
		LOG_OBJECT.Debug("Entered into delete Evaluation Setting Data");
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID, asProcurementId);
		loContextDataMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvalPoolMappingId);
		try
		{
			Map<String, Object> loDataMap = new HashMap<String, Object>();
			loDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
			loDataMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvalPoolMappingId);
			loDataMap.put(HHSConstants.EVAL_COUNT_NEW, "0");
			loDataMap.put(HHSConstants.CLEAR_EVAL_SENT_FLAG, true);
			loDataMap.put(HHSConstants.STATUS_COMPETITION_POOL_PROPOSALS_RECEIVED, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_COMPETITION_POOL_PROPOSALS_RECEIVED));
			// if All evaluation task workflows will be terminated
			// Deleting Evaluation Results data against procurementId and
			// evaluation pool mapping id
			DAOUtil.masterDAO(aoMybatisSession, loDataMap, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
					HHSConstants.DELETE_EVALUATION_RESULTS, HHSConstants.JAVA_UTIL_MAP);

			// Deleting Evaluation Score data against procurementId and
			// evaluation pool mapping id
			DAOUtil.masterDAO(aoMybatisSession, loDataMap, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
					HHSConstants.DELETE_EVALUATION_SCORE, HHSConstants.JAVA_UTIL_MAP);

			// Start Added in R5
			// Update cancel status against procurementId and
			DAOUtil.masterDAO(aoMybatisSession, loDataMap, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
					HHSR5Constants.UPDATE_EVALUATION_VERSION_ARCHIVE, HHSConstants.JAVA_UTIL_MAP);
			// End Added in R5

			// Deleting Evaluation Status data against procurementId and
			// evaluation pool mapping id
			DAOUtil.masterDAO(aoMybatisSession, loDataMap, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
					HHSConstants.DELETE_EVALUATION_STATUS, HHSConstants.JAVA_UTIL_MAP);

			// Deleting Evaluation Settings Internal data against procurementId
			// and evaluation pool mapping id
			DAOUtil.masterDAO(aoMybatisSession, loDataMap, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
					HHSConstants.DELETE_EVALUATION_SETTINGS_INTERNAL, HHSConstants.JAVA_UTIL_MAP);

			// Deleting Evaluation Settings External data against procurementId
			// and evaluation pool mapping id
			DAOUtil.masterDAO(aoMybatisSession, loDataMap, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
					HHSConstants.DELETE_EVALUATION_SETTINGS_EXTERNAL, HHSConstants.JAVA_UTIL_MAP);

			// Deleting award data against procurementId and evaluation pool
			// mapping id
			DAOUtil.masterDAO(aoMybatisSession, loDataMap, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
					HHSConstants.DELETE_AWARD_DATA, HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMybatisSession, loDataMap, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
					HHSConstants.UPDATE_EVALUATOR_COUNT, HHSConstants.JAVA_UTIL_MAP);

			loDeleteEvaluationFlag = Boolean.TRUE;
			setMoState("Evaluation Result,Status,Score ,Settings Internal and External data is deleted for evaluation pool mapping id:"
					+ asEvalPoolMappingId);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			setMoState("Error while deleting Evaluation data for evaluation pool mapping id:" + asEvalPoolMappingId);
			aoExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while deleting Evaluation data for evaluation pool mapping id:"
					+ asEvalPoolMappingId, aoExp);
			throw aoExp;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error while deleting Evaluation data for evaluation pool mapping id:"
					+ asEvalPoolMappingId, aoEx);
			setMoState("Error while deleting Evaluation data for evaluation pool mapping id:" + asEvalPoolMappingId);
			throw new ApplicationException("Error while deleting Evaluation data for evaluation pool mapping id:"
					+ asEvalPoolMappingId, aoEx);
		}
		return loDeleteEvaluationFlag;
	}

	/**
	 * The Method is used for setting all the proposal Status to Accepted for
	 * Evaluation,except the proposal in draft state
	 * 
	 * <ul>
	 * <li>1. Capture all the required parameter and set into Map</li>
	 * <li>2. Execute Query <b>updateProposalStatusAccForEval</b> from
	 * evaluation mapper for input procurement ID and evaluation pool mapping Id
	 * </li>
	 * <li>3. Return the update status</li>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * Change: added check for competition pool
	 * 
	 * Changed By: Pallavi
	 * 
	 * Change Date: 15 Jan 2014
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param asProcurementId Procurement Id
	 * @param asEvalPoolMappingId Evaluation Pool Mapping Id
	 * @param asUserId User Id
	 * @return Boolean proposalStatus
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean updateProposalStatus(SqlSession aoMybatisSession, String asProcurementId, String asUserId,
			String asEvalPoolMappingId) throws ApplicationException
	{
		Boolean loUpdateEvaluationFlag = false;
		LOG_OBJECT.Debug("Entered into update Proposal Status");
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID, asProcurementId);
		loContextDataMap.put(HHSConstants.USER_ID, asUserId);

		Map<String, String> loStatusInfo = new HashMap<String, String>();
		loStatusInfo.put(HHSConstants.PROCUREMENT_ID, asProcurementId);
		loStatusInfo.put(HHSConstants.USER_ID, asUserId);
		loStatusInfo.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvalPoolMappingId);
		loStatusInfo
				.put(HHSConstants.STATUS_PROPOSAL_DRAFT, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_DRAFT));
		loStatusInfo.put(HHSConstants.STATUS_PROPOSAL_NON_RESPONSIVE, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_NON_RESPONSIVE));
		loStatusInfo.put(HHSConstants.PROPOSAL_ACCEPTED_FOR_EVALUATION_KEY, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.PROPOSAL_ACCEPTED_FOR_EVALUATION_KEY));
		try
		{
			// Updating Proposal Status to Accepted for Evaluated except
			// proposal in draft
			DAOUtil.masterDAO(aoMybatisSession, loStatusInfo, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
					HHSConstants.UPDATE_PROPOSAL_STATUS_QUERY_ID, HHSConstants.JAVA_UTIL_MAP);
			loUpdateEvaluationFlag = Boolean.TRUE;
			setMoState("Successfully updated proposal status for evaluation pool mapping Id:" + asEvalPoolMappingId);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			setMoState("Error while updating Proposal Status  for evaluation pool mapping Id:" + asEvalPoolMappingId);
			aoExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while updating Proposal Status for evaluation pool mapping Id:"
					+ asEvalPoolMappingId, aoExp);
			throw aoExp;
		}
		// handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while updating Proposal Status for evaluation pool mapping Id:"
					+ asEvalPoolMappingId, aoExp);
			setMoState("Error while updating Proposal Status for evaluation pool mapping Id:" + asEvalPoolMappingId);
			throw new ApplicationException("Error while updating Proposal Status for evaluation pool mapping Id:"
					+ asEvalPoolMappingId, aoExp);
		}
		return loUpdateEvaluationFlag;
	}

	/**
	 * This method fetches the evaluation criteria details(score sequence no.,
	 * score criteria and maximum score) corresponding to the procurement Id
	 * <ul>
	 * <li>1. Retrieve procurement Id from the Channel object</li>
	 * <li>2. If the retrieved procurement Id is not null then execute query
	 * <b>fetchEvaluationCriteriaDetails</b> to fetch the evaluation criteria
	 * details from evaluation_criteria table corresponding to the procurement
	 * Id</li>
	 * <li>3. Return the retrieved result</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProcurementId - string representation of procurement Id
	 * @return loEvalCriteriaList - list of fetched evaluation criteria details
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<EvaluationBean> fetchEvaluationCriteriaDetails(SqlSession aoMybatisSession, String asProcurementId,
			String asEvaluationPoolMappingId) throws ApplicationException
	{
		List<EvaluationBean> loEvalCriteriaList = null;
		HashMap<String, Object> loHMContextData = new HashMap<String, Object>();
		loHMContextData.put(HHSConstants.PROCUREMENT_ID, asProcurementId);
		Map<String, String> loContextMap = new HashMap<String, String>();
		loContextMap.put(HHSConstants.PROCUREMENT_ID, asProcurementId);
		loContextMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
		LOG_OBJECT.Debug("Entered into fetching evaluation criteria details::" + loHMContextData.toString());
		if (asProcurementId != null)
		{
			try
			{
				loEvalCriteriaList = (List<EvaluationBean>) DAOUtil.masterDAO(aoMybatisSession, loContextMap,
						HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_EVALUATION_CRITERIA_DETAIL,
						HHSConstants.JAVA_UTIL_MAP);
				// Fix for Defect-6922
				setScoreCriteraToEscapeHtml(loEvalCriteriaList);
				setMoState("Evaluation Criteria Details fetched successfully corresponding to the procurement Id");
			}
			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handles over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */
			catch (ApplicationException aoAppEx)
			{
				LOG_OBJECT
						.Error("Exception occured while fetching evaluation criteria details corresponding to the procurement Id ",
								aoAppEx);
				setMoState("Transaction Failed:: EvaluationService:fetchEvaluationCriteriaDetails method - while fetching evaluation criteria details corresponding to the procurement Id");
				aoAppEx.setContextData(loHMContextData);
				throw aoAppEx;
			}
			// Handling Exception other than ApplicationException
			catch (Exception aoAppEx)
			{
				LOG_OBJECT
						.Error("Exception occured while fetching evaluation criteria details corresponding to the procurement Id ",
								aoAppEx);
				setMoState("Transaction Failed:: EvaluationService:fetchEvaluationCriteriaDetails method - while fetching evaluation criteria details corresponding to the procurement Id");
				throw new ApplicationException(
						"Exception occured while fetching evaluation criteria details corresponding to the procurement Id ",
						aoAppEx);
			}
		}
		return loEvalCriteriaList;
	}

	/**
	 * This method gets list of documents that needs to be downloaded for input
	 * procurement id and evaluation pool mapping Id
	 * 
	 * <ul>
	 * <li>1. Add multiple input parameters to map</li>
	 * <li>2. Get the list of downloadable documents corresponding to
	 * procurement Id and evaluation pool mapping Id using <b>getDBDDocsList</b>
	 * query from evaluation mapper</li>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * Change: updated query for competition Pool
	 * 
	 * Changed By: Varun
	 * 
	 * Change Date: 8 Jan 2014
	 * 
	 * @param aoMybatisSession - SQL mybatis session
	 * @param asProcurementId - Procurement id of current procurement
	 * @param asEvaluationPoolMappingId - Evaluation Pool mapping id
	 * @return loDBDDocList - list of documents to download
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, String>> getDBDDocsList(SqlSession aoMybatisSession, String asProcurementId,
			String asEvaluationPoolMappingId) throws ApplicationException
	{
		List<Map<String, String>> loDBDDocList = null;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID, asProcurementId);
		loContextDataMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
		try
		{
			loContextDataMap.put(HHSConstants.DBD, HHSConstants.DOING_BUSINESS_DATA_FORM);
			loContextDataMap.put(HHSConstants.STATUS_PROPOSAL_NON_RESPONSIVE, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_NON_RESPONSIVE));
			loDBDDocList = (List<Map<String, String>>) DAOUtil.masterDAO(aoMybatisSession, loContextDataMap,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.DB_DOC_LIST, HHSConstants.JAVA_UTIL_MAP);
			setMoState("Successfully fetched DBD document list for evaluation pool mapping Id:"
					+ asEvaluationPoolMappingId);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoExp)
		{
			setMoState("Error while fetching dbd documnet list for evaluation pool mapping Id:"
					+ asEvaluationPoolMappingId);
			aoExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching dbd documnet list for evaluation pool mapping Id:"
					+ asEvaluationPoolMappingId, aoExp);
			throw aoExp;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (Exception aoExp)
		{
			setMoState("Error while fetching dbd documnet list for evaluation pool mapping Id:"
					+ asEvaluationPoolMappingId);
			LOG_OBJECT.Error("Error while fetching dbd documnet list for evaluation pool mapping Id:"
					+ asEvaluationPoolMappingId, aoExp);
			throw new ApplicationException("Error while fetching dbd documnet list for evaluation pool mapping Id:"
					+ asEvaluationPoolMappingId, aoExp);
		}
		return loDBDDocList;
	}

	/**
	 * This method downloads the document and zip them.
	 * <ul>
	 * <li>1. Iterate over list of files to be downloaded</li>
	 * <li>2. Create threads to download the content from filenet</li>
	 * <li>3. Execute the threads and wait for all the threads to complete</li>
	 * <li>4. Zip the output of all downloads using "zipFolder" method</li>
	 * <li>5. Clean the temp folder using "deleteAllDownloadedTemplates" method</li>
	 * 
	 * @param aoUserSession - filenet session
	 * @param aoDBDDocList - list of documents to be zipped and downloaded
	 * @param asProcurementId - Procurement id of current procurement
	 * @param asUserId - user id of current user
	 * @param asContextPath - context path of temp folder
	 * @return lsZipFilePath - path of zipped file
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public String downloadDBDDocumentsAndZip(P8UserSession aoUserSession, List<Map<String, String>> aoDBDDocList,
			String asProcurementId, String asUserId, String asContextPath, String asFolderName,
			Boolean lsIsFinacialDocRequired) throws ApplicationException
	{
		String lsZipFilePath = null;
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		try
		{
			loContextDataMap.put(HHSConstants.PROCUREMENT_ID, asProcurementId);
			loContextDataMap.put(HHSConstants.AS_USER_ID, asUserId);
			loContextDataMap.put(HHSConstants.CONTEXT_PATH, asContextPath);
			loContextDataMap.put(HHSConstants.AO_DB_DOC_LIST, aoDBDDocList);
			loContextDataMap.put(HHSConstants.IS_FINANCIAL, lsIsFinacialDocRequired);
			if (aoDBDDocList != null && aoDBDDocList.size() > HHSConstants.INT_ZERO)
			{

				CountDownLatch loStartSignal = new CountDownLatch(1);
				CountDownLatch loDoneSignal = new CountDownLatch(aoDBDDocList.size());
				long llCurrentTime = System.currentTimeMillis();
				String lsPath = asContextPath + HHSConstants.FORWARD_SLASH + llCurrentTime + HHSConstants.UNDERSCORE
						+ asUserId + asFolderName;
				lsZipFilePath = lsPath + HHSConstants.ZIP;
				for (Map<String, String> loDBDDocDetails : aoDBDDocList)
				{
					if ((loDBDDocDetails != null && lsIsFinacialDocRequired.TRUE)
							|| (loDBDDocDetails != null && lsIsFinacialDocRequired.FALSE))
					{
						new Thread(new DownloadDBDDocsThread(loStartSignal, loDoneSignal, aoUserSession,
								loDBDDocDetails, lsPath, lsIsFinacialDocRequired)).start();
					}

				}
				loStartSignal.countDown(); // let all threads proceed
				loDoneSignal.await();
				File loFolder = new File(lsPath);
				if (loFolder.exists())
				{
					HHSUtil.zipFolder(lsPath, lsPath + HHSConstants.ZIP);
				}
				HHSUtil.cleanup(new File(asContextPath));
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (InterruptedException aoExp)
		{
			setMoState("Error while downloading dbd documents :");
			ApplicationException aoAppExp = new ApplicationException("Error while downloading dbd documents", aoExp);
			aoAppExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while downloading dbd documents :", aoExp);
			throw aoAppExp;
		}
		catch (Exception loExp)
		{
			setMoState("Error while downloading dbd documents :");
			LOG_OBJECT.Error("Error while downloading dbd documents :", loExp);
			throw new ApplicationException("Error while downloading dbd documents :" + loExp);
		}
		return lsZipFilePath;
	}

	/**
	 * This method fetches the List of Provider Name
	 * 
	 * <ul>
	 * <li>1. Get the Provider Name initials and Execute the query
	 * "fetchProviderNameList"</li>
	 * <li>2. Return the list of Providers</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param asProviderName Provider Name Initials
	 * @return List<String> Provider Name List
	 * @throws ApplicationException - throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<String> fetchProviderNameList(SqlSession aoMybatisSession, String asProviderName)
			throws ApplicationException
	{
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROVIDER_NAME_PARAM, asProviderName);
		List<String> loProviderNameList = null;
		try
		{
			if (asProviderName != null)
			{
				// Appending provider name with % for LIKE clause in query
				// fetchProviderNameList
				StringBuffer loProviderName = new StringBuffer(HHSConstants.PERCENT).append(
						asProviderName.toLowerCase()).append(HHSConstants.PERCENT);

				loProviderNameList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, loProviderName.toString(),
						HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_PROVIDER_NAME_LIST_QUERY_ID,
						HHSConstants.JAVA_LANG_STRING);
				setMoState("Provider name list fetched sucessfully:");
			}

		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoAppExp)
		{
			aoAppExp.setContextData(loContextDataMap);
			setMoState("Error while fetching provider name list :");
			LOG_OBJECT.Error("Error while fetching provider name list :", aoAppExp);
			throw aoAppExp;
		}
		// handling exception other than ApplicationException
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetching provider name list :", aoExp);
			setMoState("Error while fetching provider name list :");
			throw new ApplicationException("Exception occured while fetching provider name list" + aoExp);
		}
		return loProviderNameList;
	}

	/**
	 * This method is used to get all the external and internal evaluator for
	 * the proposal
	 * <ul>
	 * <li>Get the procurement id from the request parameter</li>
	 * <li>Execute the query <code>fetchExtAndIntEvaluator</code> from
	 * <code>evaluation</code> mapper</li>
	 * <li>return the list of all evaluators configured for the procurement</li>
	 * </ul>
	 * @param aoMybatisSession sql session object
	 * @param asProcurementId procurement id
	 * @return list of evaluator
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<String> fetchExtAndIntEvaluator(SqlSession aoMybatisSession, String asProcurementId)
			throws ApplicationException
	{
		List<HashMap<String, String>> loEvaluatorsList = null;
		List<String> loEvaluatorEmailList = new ArrayList<String>();
		String lsEvaluatorEmailId = null;
		try
		{
			loEvaluatorsList = (List<HashMap<String, String>>) DAOUtil.masterDAO(aoMybatisSession, asProcurementId,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_EXT_AND_INT_EVALUATOR,
					HHSConstants.JAVA_LANG_STRING);
			if (null != loEvaluatorsList && !loEvaluatorsList.isEmpty())
			{
				for (Iterator<HashMap<String, String>> loEvaluatorItr = loEvaluatorsList.iterator(); loEvaluatorItr
						.hasNext();)
				{
					lsEvaluatorEmailId = loEvaluatorItr.next().get(HHSConstants.NAME);
					loEvaluatorEmailList.add(lsEvaluatorEmailId);
				}
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			setMoState("Error while launching workflow :");
			HashMap<String, Object> aoHMContextData = new HashMap<String, Object>();
			aoHMContextData.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
			aoExp.setContextData(aoHMContextData);
			LOG_OBJECT.Error("Error while launching workflow :", aoExp);
			throw aoExp;
		}
		// Handling exception other than ApplicationException
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while launching workflow :", aoExp);
			setMoState("Error while launching workflow :");
			throw new ApplicationException("Error while launching workflow :", aoExp);
		}
		return loEvaluatorEmailList;
	}

	/**
	 * The Method will retrieve the Close Button Visibitly Status
	 * <ul>
	 * <li>1. Create the map to hold the context data.</li>
	 * <li>2. Create the map to be returned to hold the result data.</li>
	 * <li>3. Set procurement id and evaluation group Id in the context map.</li>
	 * <li>4. Fetches proposal due date and submission close date using
	 * <b>fetchProcurementDates</b> from evaluation mapper</li>
	 * <li>5. If it is before the Proposal Due Date and Time, this button is
	 * disabled,</li>
	 * <li>6.Else, if it is after the Proposal Due Date and Time, this button is
	 * enabled</li>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * Change: updated code for competition pool and open ended RFP
	 * 
	 * Changed By: Pallavi
	 * 
	 * Change Date: 2 Jan 2014
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProcurementId - Procurement Id
	 * @param asEvaluationGroupId - Evaluation Group Id
	 * @return loCloseButtonVisibleStatus - a Map<String, String> map
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> getCloseButtonVisibiltyStatus(SqlSession aoMybatisSession, String asProcurementId,
			String asEvaluationGroupId) throws ApplicationException
	{
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		Map<String, String> loCloseButtonVisibleStatus = new HashMap<String, String>();
		loContextDataMap.put(HHSConstants.PROCUMENET_ID, asProcurementId);
		loContextDataMap.put(HHSConstants.EVALUATION_GROUP_ID, asEvaluationGroupId);
		loCloseButtonVisibleStatus.put(HHSConstants.CLOSE_BUTTON_VISIBILTY_FLAG, HHSConstants.NO_UPPERCASE);
		try
		{
			EvaluationBean loEvaluationBean = (EvaluationBean) DAOUtil.masterDAO(aoMybatisSession, loContextDataMap,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_PROCUREMENT_DATES,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			if (loEvaluationBean != null && loEvaluationBean.getSubmissionCloseDate() == null)
			{
				loCloseButtonVisibleStatus.put(HHSConstants.CLOSE_BUTTON_VISIBILTY_FLAG, HHSConstants.YES_UPPERCASE);
				if (loEvaluationBean.getIsOpenEndedRFP() == null
						|| loEvaluationBean.getIsOpenEndedRFP().equalsIgnoreCase(HHSConstants.ZERO))
				{
					if (new Date(System.currentTimeMillis()).after(loEvaluationBean.getUpdProposalDueDate()))
					{
						HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb
								.getInstance().getCacheObject(ApplicationConstants.APPLICATION_SETTING);
						String loReleaseTime = (String) loApplicationSettingMap
								.get(HHSConstants.PROPOSAL_RELEASE_TIME_KEY);
						long loHours = 0;
						long loMinutes = 0;
						if (loReleaseTime.toLowerCase().indexOf(HHSConstants.AM) > 0)
						{
							loHours = Long.parseLong(loReleaseTime.substring(0,
									loReleaseTime.indexOf(HHSConstants.COLON)));
						}
						else
						{
							loHours = Long.parseLong(loReleaseTime.substring(0,
									loReleaseTime.indexOf(HHSConstants.COLON))) + 12;
						}
						loMinutes = Long.parseLong(loReleaseTime.substring(
								loReleaseTime.indexOf(HHSConstants.COLON) + 1, loReleaseTime.indexOf(' ')));
						if (new Date(System.currentTimeMillis()).getTime() > (loEvaluationBean.getUpdProposalDueDate()
								.getTime() + (loHours * 60 * 60 * 1000) + (loMinutes * 60 * 1000)))
						{
							loCloseButtonVisibleStatus.put(HHSConstants.CLOSE_BUTTON_ENABLE_FLAG, HHSConstants.ENABLE);
						}
						else
						{
							loCloseButtonVisibleStatus.put(HHSConstants.CLOSE_BUTTON_ENABLE_FLAG, HHSConstants.DISABLE);
						}
					}
					else if (new Date(System.currentTimeMillis()).before(loEvaluationBean.getUpdProposalDueDate()))
					{
						loCloseButtonVisibleStatus.put(HHSConstants.CLOSE_BUTTON_ENABLE_FLAG, HHSConstants.DISABLE);
					}
				}
			}
			setMoState("Successfully fetched close button visibility status for procurement ID:" + asProcurementId);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoAppEx)
		{
			setMoState("Error while getting Close Button Visibilty Status for procurement ID:" + asProcurementId);
			aoAppEx.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while  Close Button Visibilty Status for procurement ID:" + asProcurementId,
					aoAppEx);
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoAppEx)
		{
			setMoState("Error while getting Close Button Visibilty Status for procurement ID:" + asProcurementId);
			LOG_OBJECT.Error("Error while  Close Button Visibilty Status for procurement ID:" + asProcurementId,
					aoAppEx);
			throw new ApplicationException("Error while  Close Button Visibilty Status for procurement ID:"
					+ asProcurementId, aoAppEx);
		}
		return loCloseButtonVisibleStatus;
	}

	/**
	 * The Method will return the visibility status of Download DBD button
	 * <ul>
	 * <li>1. Check whether tasks are sent out only for Agency ACCO Users and
	 * Evaluation Task Has been sent</li>
	 * <li>2. Return "true" if the above clause is true, else return false</li>
	 * <li>3. Button will be enabled displayed after evaluations tasks are sent
	 * out only for Agency ACCO Users. Prior to evaluation tasks being sent out
	 * or when evaluation tasks are cancelled, this button is hidden.</li>
	 * <li>Execute query id <b> etchReqDocCoun </b></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - Mybatis Session
	 * @param asProcurementId - Procurement Id
	 * @param aoEvalutionDetailsList - an object of type List<EvaluationBean>
	 * @return loDownloadDBDStatusFlag - boolean status flag
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean getDownloadDBDDocsVisibiltyStatus(SqlSession aoMybatisSession, String asProcurementId,
			List<EvaluationBean> aoEvalutionDetailsList) throws ApplicationException
	{
		Boolean loDownloadDBDStatusFlag = false;
		Map<String, String> loProcDocInfo = new HashMap<String, String>();
		loProcDocInfo.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		loProcDocInfo.put(HHSConstants.FETCH_REQ_DOC_NAME_KEY, HHSConstants.DOING_BUSINESS_DATA_FORM);
		loProcDocInfo.put(HHSConstants.DOC_REQUIRED_FLAG, HHSConstants.ONE);
		try
		{
			if (!HHSUtil.isEmptyList(aoEvalutionDetailsList))
			{
				Integer liRequiredIndex = 0;
				for (EvaluationBean loEvaluationBean : aoEvalutionDetailsList)
				{
					if (loEvaluationBean.getSendEvaluationStatus().equalsIgnoreCase(HHSConstants.YES))
					{
						break;
					}
					liRequiredIndex++;
				}
				if (liRequiredIndex >= aoEvalutionDetailsList.size())
				{
					liRequiredIndex = 0;
				}
				EvaluationBean loEvaluationBean = (EvaluationBean) aoEvalutionDetailsList.get(liRequiredIndex);
				String lsEvaluationSent = loEvaluationBean.getSendEvaluationStatus();
				if (lsEvaluationSent.equalsIgnoreCase(HHSConstants.YES))
				{
					Integer liRequiredDocId = (Integer) DAOUtil.masterDAO(aoMybatisSession, loProcDocInfo,
							HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_REQ_DOC_COUNT,
							HHSConstants.JAVA_UTIL_MAP);
					if (liRequiredDocId != null && liRequiredDocId > 0)
					{
						loDownloadDBDStatusFlag = true;
					}
				}
			}
		}
		// handling exception other than Application Exception.
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Error while getting Download DBD Button Visibilty Status", aoAppEx);
			setMoState("Error while getting Download DBD Button Visibilty Status");
			throw new ApplicationException("Error while getting Download DBD Button Visibilty Status", aoAppEx);
		}
		return loDownloadDBDStatusFlag;

	}

	/**
	 * The Method will check the visibility of Cancel Evaluation Tasks button on
	 * Evaluation Status screen for input evaluation pool mapping Id
	 * 
	 * <ul>
	 * <li>1. Create the map to hold the context data.</li>
	 * <li>2. Add input parameters to the map.</li>
	 * <li>3. Check cancel evaluation tasks button visibility status
	 * corresponding to evaluation pool mapping Id using
	 * <b>checkCancelEvalVisibilityStatus</b> from evaluation mapper</li>
	 * <li>4. Set cancel flag based on query result and input evaluation sent
	 * flag</li>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * Change: added check for competition pool
	 * 
	 * Changed By: Pallavi
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asEvalPoolMappingId - Evaluation Pool Mapping Id
	 * @param asEvaluationSentFlag - evaluation sent flag
	 * @param aoNotNonResponsiveCount - proposal count not in non responsive
	 *            status
	 * @return loCancelEvalTaskStatusFlag - boolean status flag
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean getCancelEvalTaskVisibiltyStatus(SqlSession aoMybatisSession, String asEvalPoolMappingId,
			String asEvaluationSentFlag, Integer aoNotNonResponsiveCount) throws ApplicationException
	{
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvalPoolMappingId);
		Boolean loCancelEvalTaskStatusFlag = false;
		try
		{
			Integer loPropCountWithApproveAwards = (Integer) DAOUtil.masterDAO(aoMybatisSession, asEvalPoolMappingId,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.CHECK_CANCEL_EVAL_VISIBLITY_STATUS,
					HHSConstants.JAVA_LANG_STRING);
			if (null != asEvaluationSentFlag && asEvaluationSentFlag.equalsIgnoreCase(HHSConstants.ONE)
					&& null != loPropCountWithApproveAwards && loPropCountWithApproveAwards == 0
					&& null != aoNotNonResponsiveCount && aoNotNonResponsiveCount != 0)
			{
				loCancelEvalTaskStatusFlag = true;
			}
			setMoState("Successfully processed cancel evaluation tasks visibility status for evaluation pool mapping Id:"
					+ asEvalPoolMappingId);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException loAppEx)
		{
			setMoState("Error while getting Cancel Evaluation Tasks Visibilty Status for evaluation pool mapping Id:"
					+ asEvalPoolMappingId);
			loAppEx.setContextData(loContextDataMap);
			LOG_OBJECT.Error(
					"Error while getting Cancel Evaluation Tasks Visibilty Status for evaluation pool mapping Id:"
							+ asEvalPoolMappingId, loAppEx);
			throw loAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error(
					"Error while getting Cancel Evaluation Tasks Visibilty Status for evaluation pool mapping Id:"
							+ asEvalPoolMappingId, aoEx);
			setMoState("Error while getting Cancel Evaluation Tasks Visibilty Status for evaluation pool mapping Id:"
					+ asEvalPoolMappingId);
			throw new ApplicationException(
					"Error while getting Cancel Evaluation Tasks Visibilty Status for evaluation pool mapping Id:"
							+ asEvalPoolMappingId, aoEx);
		}
		return loCancelEvalTaskStatusFlag;
	}

	/**
	 * The Method will check the visibility for Send Evaluation Task Button on
	 * Evaluation Status screen
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. Add input parameters to map.</li>
	 * <li>2. Get the total no of evaluators for input evaluation pool mapping
	 * Id using <b>countEvaluationUsers</b> from evaluation mapper</li>
	 * <li>3. Get total proposal count map for input evaluation pool mapping Id
	 * using <b>checkSendEvalVisiblityStatus</b> from evaluation mapper</li>
	 * <li>4. Return false if list is empty.</li>
	 * <li>5. Add input evaluation sent flag and total evaluator count in result
	 * map from above query</li>
	 * <li>6. Execute rule <b>sendEvalButtonStatus</b> to get the send
	 * evaluation task button visibility status</li>
	 * </ul>
	 * 
	 * Change: added check for competition pool
	 * 
	 * Changed By: Pallavi
	 * 
	 * Change Date: 10 Mar 2014
	 * 
	 * @param aoMybatisSession - mybatis SQL Session
	 * @param asEvaluationSentFlag - evaluation sent flag
	 * @param aoEvaluationBean - an EvaluationBean object
	 * @return loSendEvalTaskButtonFlagMap - a Map<String, Boolean> object
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Boolean> getSendEvaluationTasksVisibiltyStatus(SqlSession aoMybatisSession,
			String asEvaluationSentFlag, EvaluationBean aoEvaluationBean) throws ApplicationException
	{
		Map<String, Boolean> loSendEvalTaskButtonFlagMap = new HashMap<String, Boolean>();
		Integer loNoOfUsers = HHSConstants.INT_ZERO;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.ARG_EVALUATION_BEAN, aoEvaluationBean);
		try
		{
			loSendEvalTaskButtonFlagMap.put(HHSConstants.SHOW_SEND_EVAL_TASK_BUTTON, true);
			loNoOfUsers = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoEvaluationBean.getEvaluationPoolMappingId(),
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.TOTAL_EVALUATION_USERS,
					HHSConstants.JAVA_LANG_STRING);
			Map<String, String> loParamMap = new HashMap<String, String>();
			loParamMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, aoEvaluationBean.getEvaluationPoolMappingId());
			loParamMap.put(HHSConstants.LS_DRAFT_ID, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_DRAFT));
			loParamMap.put(HHSConstants.PROPOSAL_NON_RESPONSIVE, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.PROPOSAL_ACCEPTED_FOR_EVALUATION_KEY));
			loParamMap.put(HHSConstants.PROPOSAL_ACCEPTED_FOR_EVAL, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_NON_RESPONSIVE));
			HashMap<String, Object> loResultMap = (HashMap<String, Object>) DAOUtil.masterDAO(aoMybatisSession,
					loParamMap, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
					HHSConstants.CHECK_SEND_EVAL_VISIBILITY_STATUS, HHSConstants.JAVA_UTIL_MAP);
			loResultMap.put(HHSConstants.EVALUATOR_COUNT, loNoOfUsers == null ? HHSConstants.ZERO : loNoOfUsers);
			loResultMap.put(HHSConstants.EVALUATION_SENT, asEvaluationSentFlag == null ? HHSConstants.ZERO
					: asEvaluationSentFlag);
			// successfully saved evaluators on S214- Evaluation Settings
			// and evaluations have been sent and all proposals are in
			// Accepted for Evaluation or non - responsive
			Channel loChannel = new Channel();
			loChannel.setData(loResultMap);
			Boolean lbButtonVisibilityStatus = Boolean.valueOf((String) Rule.evaluateRule(
					HHSConstants.SEND_EVAL_BUTTON_STATUS, loChannel));
			if (lbButtonVisibilityStatus)
			{
				loSendEvalTaskButtonFlagMap.put(HHSConstants.ENABLE_SEND_EVAL_TASK_BUTTON, Boolean.TRUE);
			}
			else if (null != asEvaluationSentFlag && asEvaluationSentFlag.equalsIgnoreCase(HHSConstants.ONE))
			{
				loSendEvalTaskButtonFlagMap.put(HHSConstants.SHOW_SEND_EVAL_TASK_BUTTON, false);
			}
			else
			{
				loSendEvalTaskButtonFlagMap.put(HHSConstants.ENABLE_SEND_EVAL_TASK_BUTTON, Boolean.FALSE);
			}
			setMoState("Successfully processed send evaluation tasks visibility status for evaluation pool mapping Id:"
					+ aoEvaluationBean.getEvaluationPoolMappingId());
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoAppEx)
		{
			setMoState("Error while getting Send Evaluation Tasks Visibilty Status for evaluation pool mapping Id:"
					+ aoEvaluationBean.getEvaluationPoolMappingId());
			aoAppEx.setContextData(loContextDataMap);
			LOG_OBJECT.Error(
					"Error while getting Send Evaluation Tasks Visibilty Status for evaluation pool mapping Id:"
							+ aoEvaluationBean.getEvaluationPoolMappingId(), aoAppEx);
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error(
					"Error while getting Send Evaluation Tasks Visibilty Status for evaluation pool mapping Id:"
							+ aoEvaluationBean.getEvaluationPoolMappingId(), aoEx);
			setMoState("Error while getting Send Evaluation Tasks Visibilty Status for evaluation pool mapping Id:"
					+ aoEvaluationBean.getEvaluationPoolMappingId());
			throw new ApplicationException(
					"Error while getting Send Evaluation Tasks Visibilty Status for evaluation pool mapping Id:"
							+ aoEvaluationBean.getEvaluationPoolMappingId(), aoEx);
		}
		return loSendEvalTaskButtonFlagMap;
	}

	/**
	 * The Method will calculate the Total Evaluation Data i.e In
	 * progress,Completed
	 * <ul>
	 * <li>1. Create the map to hold the context data.</li>
	 * <li>2. Create the EvaluationBean object to be returned to hold the result
	 * data.</li>
	 * <li>3. Put the input data in the context map.</li>
	 * <li>4.check whether user has sent evaluation tasks</li>
	 * <li>5.If 1 is true,Get TotalEvaluationComplete and Total Evaluation in
	 * Progress.QueryId will be
	 * fetchTotalEvaluationComplete,fetchTotalEvaluationInProgess respectively</li>
	 * <li>6.Calculate the percentage on the basis of above data and set in
	 * Evaluation Bean</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession
	 * @param aoEvaluationBean EvaluationBean
	 * @param aoEvalList Evaluation list
	 * @return EvaluationBean
	 * @throws ApplicationException If an ApplicationException occurs
	 */

	public EvaluationBean getTotalEvaluationData(SqlSession aoMybatisSession, EvaluationBean aoEvaluationBean,
			List<EvaluationBean> aoEvalList) throws ApplicationException
	{
		EvaluationBean loEvaluationBean = null;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.ARG_EVALUATION_BEAN, aoEvaluationBean);
		loContextDataMap.put(HHSConstants.ARG_EVALUATION_LIST, aoEvalList);
		try
		{

			if (!HHSUtil.isEmptyList(aoEvalList))
			{
				Integer liRequiredIndex = 0;
				for (EvaluationBean loEvalBean : aoEvalList)
				{
					if (loEvalBean.getSendEvaluationStatus().equalsIgnoreCase(HHSConstants.YES))
					{
						break;
					}
					liRequiredIndex++;
				}
				if (liRequiredIndex >= aoEvalList.size())
				{
					liRequiredIndex = 0;
				}
				EvaluationBean loEvalBean = (EvaluationBean) aoEvalList.get(liRequiredIndex);
				String lsEvaluationSent = loEvalBean.getSendEvaluationStatus();
				if (lsEvaluationSent.equalsIgnoreCase(HHSConstants.YES))
				{
					Integer loTotalEvaluationComplete = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoEvaluationBean,
							HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_TOTAL_EVALUATION_COMPLETE,
							HHSConstants.COM_NYC_HHS_MODEL_EVALUATION_BEAN);

					Integer loTotalEvaluationInProgess = (Integer) DAOUtil.masterDAO(aoMybatisSession,
							aoEvaluationBean, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
							HHSConstants.FETCH_TOTAL_EVALUATION_INPROGRESS,
							HHSConstants.COM_NYC_HHS_MODEL_EVALUATION_BEAN);
					Integer loTotalEvaluation = loTotalEvaluationInProgess + loTotalEvaluationComplete;
					Float loPercInProgress = HHSConstants.FLOAT_ZERO;
					Float loPercCompleted = HHSConstants.FLOAT_ZERO;
					if (loTotalEvaluation > HHSConstants.INT_ZERO)
					{

						loPercInProgress = (float) (((float) loTotalEvaluationInProgess * HHSConstants.HUNDRED) / (loTotalEvaluation));
						loPercCompleted = HHSConstants.HUNDRED - loPercInProgress;
					}
					loEvaluationBean = new EvaluationBean();
					loEvaluationBean.setTotalEvaluationCompleted(loTotalEvaluationComplete);
					loEvaluationBean.setTotalEvaluationInProgess(loTotalEvaluationInProgess);
					loEvaluationBean.setPercCompleted(loPercCompleted);
					loEvaluationBean.setPercInProgress(loPercInProgress);
					setMoState(" Total Evaluation data fetched sucessfully");
				}

			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoAppEx)
		{
			setMoState("Error while fetching  Total Evaluation data");
			aoAppEx.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching  Total Evaluation data ", aoAppEx);
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error while fetching  Total Evaluation data ", aoEx);
			setMoState("Error while fetching  Total Evaluation data");
			throw new ApplicationException("Error while fetching  Total Evaluation data", aoEx);
		}
		return loEvaluationBean;
	}

	/**
	 * Changed method - By: Siddharth Bhola Reason: Enhancement id: 5415 added
	 * comments in query to update/insert score level comments in column
	 * comments and temp_comments, table evaluation_score
	 * 
	 * This method saves or updates the Evaluation score Details corresponding
	 * to a evaluation criteria Id and evaluation status Id
	 * <ul>
	 * <li>check for boolean evaluation update status flag</li>
	 * <li>If true, get evaluation bean and execute query with id
	 * "updateScoreDetails"</li>
	 * <li>If result is 0, execute query with id "insertScoreDetails" to insert
	 * evaluation score details</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoScoreBean - ScoreDetailsBean
	 * @param aoEvalUpdateStatus - boolean value of evaluation update status
	 * @param asProposalReviewStatus - a string value of proposal Review status
	 * @return boolean flag indicating save status
	 * @throws ApplicationException If an ApplicationException occurs
	 */

	public Boolean saveEvaluationScoreDetails(SqlSession aoMybatisSession, ScoreDetailsBean aoScoreBean,
			Boolean aoEvalUpdateStatus, String asProposalReviewStatus) throws ApplicationException
	{
		Boolean loSaveFlag = false;
		HashMap<String, String> loContextDataMap = new HashMap<String, String>();
		Integer loCount = null;
		try
		{
			if (aoEvalUpdateStatus)
			{
				boolean lbUpdateFlag = true;
				for (EvaluationBean loEvalBean : aoScoreBean.getMiEvaluationBeanList())
				{
					// set created by and modified by in evaluation bean
					loEvalBean.setCreatedByUserId(aoScoreBean.getCreatedBy());
					loEvalBean.setModifiedByUserId(aoScoreBean.getModifiedBy());
					loEvalBean.setActionPerformed(aoScoreBean.getAction());
					// check for proposal task status and set modified flag
					// accordingly
					if (null != asProposalReviewStatus
							&& asProposalReviewStatus.equalsIgnoreCase(HHSConstants.SCORES_RETURNED)
							&& null != aoScoreBean.getAction()
							&& aoScoreBean.getAction().equalsIgnoreCase(HHSConstants.TASK_FINISHED))
					{
						loEvalBean.setModifiedFlag(HHSConstants.ONE);
					}
					else
					{
						loEvalBean.setModifiedFlag(HHSConstants.STRING_ZERO);
					}
					if (lbUpdateFlag)
					{
						// Execute update query to update evaluaion details
						loCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loEvalBean,
								HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.UPDATE_SCORE_DET,
								HHSConstants.COM_NYC_HHS_MODEL_EVALUATION_BEAN);
						// check for updated count
						if (loCount > HHSConstants.INT_ZERO)
						{
							lbUpdateFlag = true;
							loSaveFlag = true;
						}
						else
						{
							lbUpdateFlag = false;
						}
					}
					// If 0 rows are updated, execute insert query
					if (!lbUpdateFlag)
					{
						DAOUtil.masterDAO(aoMybatisSession, loEvalBean, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
								HHSConstants.INSERT_SCORE_DETAIL, HHSConstants.COM_NYC_HHS_MODEL_EVALUATION_BEAN);
						loSaveFlag = true;
					}
				}
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			setMoState("Error while saving Score Details");
			aoExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while saving Score Details :", aoExp);
			loSaveFlag = false;
			throw aoExp;
		}
		// handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while saving Score Details", aoExp);
			setMoState("Error while saving Score Details");
			throw new ApplicationException("Error while saving Score Details", aoExp);
		}
		return loSaveFlag;
	}

	/**
	 * This method is used to get no of providers for the proposal
	 * <ul>
	 * <li>Execute the query <code>fetchNoOfProviders</code> from
	 * <code>evaluation</code> mapper</li>
	 * <li>Return the no of providers for the proposal for the given procurement
	 * id.</li>
	 * </ul>
	 * @param aoMybatisSession- sql session object
	 * @param aoInputParam - Input Paramater
	 * @return liNoOfProviders - No of providers
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public int fetchNoOfProviders(SqlSession aoMybatisSession, Map<String, Object> aoInputParam)
			throws ApplicationException
	{
		int liNoOfProviders = HHSConstants.INT_ZERO;
		try
		{
			// executing query fetchNoOfProviders of EvaluationMapper.xml
			liNoOfProviders = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoInputParam,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_NO_OF_PROVIDERS,
					HHSConstants.JAVA_UTIL_MAP);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			// populating context data map for exceptional handling
			HashMap<String, Object> loHMContextData = new HashMap<String, Object>();
			loHMContextData.put(HHSConstants.PROCUREMENT_ID_KEY, aoInputParam);
			aoExp.setContextData(loHMContextData);
			LOG_OBJECT.Error("Error while fetching no of providers:", aoExp);
			setMoState("Error while fetching no of providers:");
			throw aoExp;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error while fetching no of providers ", aoEx);
			setMoState("Error while fetching no of providers");
			throw new ApplicationException("Error while fetching no of providers", aoEx);
		}
		return liNoOfProviders;
	}

	/**
	 * This method is used to get the no of proposals
	 * <ul>
	 * <li>Execute the query <code>fetchNoOfProposals</code> from
	 * <code>evaluation</code> mapper.</li>
	 * <li>Return the number of proposals for the given procurement id and
	 * status.</li>
	 * </ul>
	 * @param aoMybatisSession sql session object
	 * @param aoInputParam map containing asProcurementId and
	 *            asProcurementStatus
	 * @return loNoOfProviders No of proposals
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Integer fetchNoOfProposals(SqlSession aoMybatisSession, Map<String, Object> aoInputParam)
			throws ApplicationException
	{
		Integer loNoOfProposals = HHSConstants.INT_ZERO;
		try
		{
			// executing query fetchNoOfProposals of EvaluationMapper.xml
			loNoOfProposals = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoInputParam,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_NO_OF_PROPOSALS,
					HHSConstants.JAVA_UTIL_MAP);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			// populating context data map for exceptional handling
			HashMap<String, Object> loHMContextData = new HashMap<String, Object>();
			loHMContextData.put(HHSConstants.INPUT_PARAM_MAP, aoInputParam);
			aoExp.setContextData(loHMContextData);
			setMoState("Error while fetching no of proposals:");
			LOG_OBJECT.Error("Error while fetching no of proposals:", aoExp);
			throw aoExp;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error while fetching no of proposals ", aoEx);
			setMoState("Error while fetching no of proposals");
			throw new ApplicationException("Error while fetching no of proposals", aoEx);
		}
		return loNoOfProposals;
	}

	/**
	 * This method will get the proposal count for Evaluations status screen for
	 * the specific ProcurementId
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1.Execute select query <b>fetchProposalCount</b></li>
	 * <li>3.Get the proposal count and return it to the channel Object</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession Object
	 * @param aoEvaluationBeanForProposal - Evaluation Bean reference
	 * @return number of proposals
	 * @throws ApplicationException application Exception Object
	 */
	public Integer fetchProposalCount(SqlSession aoMybatisSession, EvaluationBean aoEvaluationBeanForProposal)
			throws ApplicationException
	{
		Integer loProposalCount = null;
		try
		{
			if (aoEvaluationBeanForProposal.getProposalTitle() != null)
			{
				// Appending Proposal Title with % for Filter Purpose
				StringBuffer loProposalTitleStrBfr = new StringBuffer(HHSConstants.PERCENT).append(
						aoEvaluationBeanForProposal.getProposalTitle()).append(HHSConstants.PERCENT);
				aoEvaluationBeanForProposal.setProposalTitle(loProposalTitleStrBfr.toString());
			}
			if (aoEvaluationBeanForProposal.getOrganizationName() != null)
			{
				// Appending Provider Name with % for Filter Purpose
				StringBuffer loOrgNameSb = new StringBuffer(HHSConstants.PERCENT).append(
						aoEvaluationBeanForProposal.getOrganizationName()).append(HHSConstants.PERCENT);
				aoEvaluationBeanForProposal.setOrganizationName(loOrgNameSb.toString());
			}
			if (aoEvaluationBeanForProposal.getEvaluationStatusList() != null
					&& aoEvaluationBeanForProposal.getEvaluationStatusList().size() > 0
					&& (aoEvaluationBeanForProposal.getEvaluationStatusList().contains(HHSConstants.EMPTY_STRING)))
			{
				aoEvaluationBeanForProposal.setNotStartedStatus(HHSConstants.YES);
			}
			if (aoEvaluationBeanForProposal.getEvaluationStatusList() != null
					&& aoEvaluationBeanForProposal.getEvaluationStatusList().size() > 0
					&& (aoEvaluationBeanForProposal.getEvaluationStatusList().contains(PropertyLoader.getProperty(
							HHSConstants.PROPERTIES_STATUS_CONSTANT,
							HHSConstants.STATUS_EVALUATE_PROPOSAL_TASK_IN_REVIEW))))
			{
				aoEvaluationBeanForProposal.setInProgressStatus(HHSConstants.YES);
			}
			if (aoEvaluationBeanForProposal.getEvaluationStatusList() != null
					&& aoEvaluationBeanForProposal.getEvaluationStatusList().size() > 0
					&& (aoEvaluationBeanForProposal.getEvaluationStatusList().contains(PropertyLoader.getProperty(
							HHSConstants.PROPERTIES_STATUS_CONSTANT,
							HHSConstants.EVALUATE_PROPOSAL_TASK_SCORES_COMPLETED))))
			{
				aoEvaluationBeanForProposal.setCompletedStatus(HHSConstants.YES);
			}
			if (aoEvaluationBeanForProposal.getProposalStatusList() != null
					&& aoEvaluationBeanForProposal.getProposalStatusList().size() > 0)
			{
				List<String> loProposalStatusList = aoEvaluationBeanForProposal.getProposalStatusList();
				if (loProposalStatusList.contains(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.PROPOSAL_ACCEPTED_FOR_EVALUATION_KEY)))
				{
					loProposalStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
							HHSConstants.STATUS_PROPOSAL_EVALUATED));
					loProposalStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
							HHSConstants.STATUS_PROPOSAL_SCORES_RETURNED));
					loProposalStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
							HHSConstants.STATUS_PROPOSAL_SELECTED));
					loProposalStatusList.add(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
							HHSConstants.STATUS_PROPOSAL_NOT_SELECTED));
					aoEvaluationBeanForProposal.setProposalStatusList(loProposalStatusList);
				}
			}
			loProposalCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoEvaluationBeanForProposal,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_PROPOSAL_COUNT,
					HHSConstants.COM_NYC_HHS_MODEL_EVALUATION_BEAN);
			String lsProposalTitle = aoEvaluationBeanForProposal.getProposalTitle();
			if (null != lsProposalTitle)
			{ // Removing % sign i.e added previously
				lsProposalTitle = lsProposalTitle.substring(HHSConstants.INT_ONE);
				lsProposalTitle = lsProposalTitle.substring(HHSConstants.INT_ZERO, lsProposalTitle.length()
						- HHSConstants.INT_ONE);
				aoEvaluationBeanForProposal.setProposalTitle(lsProposalTitle);
			}
			String lsOrganizationName = aoEvaluationBeanForProposal.getOrganizationName();
			if (null != lsOrganizationName)
			{
				// Removing % sign i.e added previously
				lsOrganizationName = lsOrganizationName.substring(HHSConstants.INT_ONE);
				lsOrganizationName = lsOrganizationName.substring(HHSConstants.INT_ZERO, lsOrganizationName.length()
						- HHSConstants.INT_ONE);
				aoEvaluationBeanForProposal.setOrganizationName(lsOrganizationName);
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured while Fetching evaluation proposal count ", aoAppEx);
			setMoState("Exception occured while Fetching evaluation proposal count ");
			throw aoAppEx;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception occured while Fetching evaluation proposal count ", loExp);
			setMoState("Exception occured while Fetching evaluation proposal count ");
			throw new ApplicationException("Exception occured while Fetching evaluation proposal count", loExp);
		}
		return loProposalCount;
	}

	/**
	 * This method updates Proposal Status to "Returned for Revision"
	 * corresponding to the proposal Id and procurement Id
	 * <ul>
	 * <li>1. Retrieve status map from the channel object</li>
	 * <li>2. If the retrieved map is not null then fetch the stataus Id
	 * corresponding to proposal selected and put it in the map</li>
	 * <li>3. Execute query <b>updateReturnForRevision</b> corresonding to
	 * proposal Id to update the proposal status to "Returned for Revision"</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession sql session object
	 * @param aoStatusMap - input map object
	 * @return loUpdateConfirmReturnForAction boolean status flag
	 * @throws ApplicationException If any Exception occurs
	 */

	public Boolean confirmReturnForAction(SqlSession aoMybatisSession, Map<String, String> aoStatusMap)
			throws ApplicationException
	{
		Boolean loUpdateConfirmReturnForAction = Boolean.FALSE;
		HashMap<String, Object> loHMContextData = new HashMap<String, Object>();
		loHMContextData.put(HHSConstants.PROCUREMENT_ID, aoStatusMap);
		LOG_OBJECT.Debug("Entered into updating proposal Status::" + loHMContextData.toString());
		if (aoStatusMap != null)
		{
			try
			{
				DAOUtil.masterDAO(aoMybatisSession, aoStatusMap, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
						HHSConstants.UPDATE_RETURN_FOR_REVISION, HHSConstants.JAVA_UTIL_MAP);
				aoStatusMap.put(HHSConstants.PREV_STATUS, HHSConstants.THIRTY_TWO);
				aoStatusMap.put(HHSConstants.DOCUMENT_STATUS, HHSConstants.THIRTY);
				DAOUtil.masterDAO(aoMybatisSession, aoStatusMap, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
						HHSConstants.UPDATE_DOC_RETURN_FOR_REVISION, HHSConstants.JAVA_UTIL_MAP);
				loUpdateConfirmReturnForAction = Boolean.TRUE;
				setMoState("Proposal Status updated successfully corresponding to proposal Id");
			}
			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handles over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */
			catch (ApplicationException aoExp)
			{
				LOG_OBJECT
						.Error("Exception occured while updating proposal status corresponding to procurement Id and proposal Id",
								aoExp);
				setMoState("Transaction Failed:: EvaluationService:confirmReturnForAction method - while updating proposal status corresponding to procurement Id and proposal Id");
				aoExp.setContextData(loHMContextData);
				throw aoExp;
			}
			// handling Exception other than Application Exception
			catch (Exception aoExp)
			{
				LOG_OBJECT
						.Error("Exception occured while updating proposal status corresponding to procurement Id and proposal Id",
								aoExp);
				setMoState("Transaction Failed:: EvaluationService:confirmReturnForAction method - while updating proposal status corresponding to procurement Id and proposal Id");
				throw new ApplicationException(
						"Exception occured while updating proposal status corresponding to procurement Id and proposal Id",
						aoExp);
			}
		}
		return loUpdateConfirmReturnForAction;
	}

	/**
	 * This method will handle the functionality of generating reviews score
	 * task
	 * 
	 * <ul>
	 * <li>1. Retrieve Proposal Id from the channel</li>
	 * <li>2. Execute query <b>fetchReqProposalDetails</b> to fetch proposal
	 * details(Organization Name, Proposal Title, Evaluation Score, Award
	 * Amount, Comment) corresponding to the proposal Id</li>
	 * <li>3. Return fetched list</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProposalId - string representation of Proposal Id
	 * @return loUpdateProposalFlag - return boolean value
	 * @throws ApplicationException If any Exception occurs
	 */
	public Boolean modifyProposalStatus(SqlSession aoMybatisSession, String asProposalId) throws ApplicationException
	{
		Boolean loUpdateProposalFlag = false;

		Map<String, String> loModifyProposalStatusMap = new HashMap<String, String>();
		loModifyProposalStatusMap.put(HHSConstants.PROPOSAL_ID, asProposalId);
		LOG_OBJECT.Debug("Entered into modifing proposal status::" + loModifyProposalStatusMap.toString());

		if (asProposalId != null)
		{
			try
			{
				HashMap<String, String> loDataMap = new HashMap<String, String>();
				loDataMap.put(HHSConstants.PROPOSAL_STATUS_ID_KEY, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_SCORES_RETURNED));
				loDataMap.put(HHSConstants.PROPOSAL_ID, asProposalId);
				DAOUtil.masterDAO(aoMybatisSession, loDataMap, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
						HHSConstants.UPDATE_PROPOSAL_REVIEW_STATUS, HHSConstants.JAVA_UTIL_MAP);
				loUpdateProposalFlag = true;
				setMoState("Proposal Status updated successfully corresponding to proposal Id");
			}
			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handles over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */
			catch (ApplicationException aoAppEx)
			{
				aoAppEx.setContextData(loModifyProposalStatusMap);
				LOG_OBJECT.Error("Exception occured while updating successfully corresponding to proposal Id", aoAppEx);
				setMoState("Transaction Failed:: EvaluationService:modifyProposalStatus method - while updating successfully corresponding to proposal Id");
				throw aoAppEx;
			}
			// Handling Exception other than ApplicationException
			catch (Exception aoAppEx)
			{
				LOG_OBJECT.Error("Exception occured while updating successfully corresponding to proposal Id", aoAppEx);
				setMoState("Transaction Failed:: EvaluationService:modifyProposalStatus method - while updating successfully corresponding to proposal Id");
				throw new ApplicationException(
						"Exception occured while updating successfully corresponding to proposal Id", aoAppEx);
			}
		}
		return loUpdateProposalFlag;

	}

	/**
	 * This method will get the proposal count for Evaluations status screen for
	 * the specific ProcurementId
	 * <ul>
	 * <li>1.Execute select query <b>updateEvaluationStatus</b></li>
	 * <li>3.Get the proposal count and return it to the channel Object</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession Object
	 * @param aoEvalMap - Evaluation Map reference
	 * @return loUpdateEvalStatus - number of proposals
	 * @throws ApplicationException application Exception Object
	 */
	public Boolean updateEvaluationStatus(SqlSession aoMybatisSession, HashMap<String, Object> aoEvalMap)
			throws ApplicationException
	{
		Boolean loUpdateEvalStatus = false;
		try
		{
			DAOUtil.masterDAO(aoMybatisSession, aoEvalMap, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
					HHSConstants.UPDATE_EVALUATION_STATUS, HHSConstants.JAVA_UTIL_HASH_MAP);
			loUpdateEvalStatus = true;
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured while Fetching evaluation proposal count ", aoAppEx);
			setMoState("Exception occured while Fetching evaluation proposal count ");
			throw aoAppEx;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception occured while Fetching evaluation proposal count ", loExp);
			setMoState("Exception occured while Fetching evaluation proposal count ");
			throw new ApplicationException("Exception occured while Fetching evaluation proposal count", loExp);
		}
		return loUpdateEvalStatus;
	}

	/**
	 * This method is used to get the details of the procurement required to
	 * launch award work flow
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. Fetches procurement details required for launching award workflow
	 * using <b>fetchProcurementDetailsForAwardWF</b> from evaluation mapper</li>
	 * <li>2. Set properties in input required properties hashmap</li>
	 * <li>3. Return required properties to calling method</li>
	 * </ul>
	 * 
	 * Change: updated query for competition pool and open ended RFP
	 * 
	 * Changed By: Pallavi
	 * 
	 * Change Date: 12 Mar 2014
	 * 
	 * @param aoMybatisSession MybatisSession
	 * @param aoDataMap Input Properties map
	 * @param aoHmReqProposMap Required Properties map
	 * @return aoHmReqProposMap - Required Properties map with updated values
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public HashMap<String, Object> fetchProcurementDetailsForAwardWF(SqlSession aoMybatisSession,
			Map<String, Object> aoDataMap, HashMap<String, Object> aoHmReqProposMap) throws ApplicationException
	{
		AcceptProposalTaskBean loProcurementDetailsBean = null;
		try
		{
			loProcurementDetailsBean = (AcceptProposalTaskBean) DAOUtil.masterDAO(aoMybatisSession, aoDataMap,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_PROC_DETAILS_FOR_AWARD_WF,
					HHSConstants.JAVA_UTIL_MAP);
			if (null != loProcurementDetailsBean)
			{
				aoHmReqProposMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_PRIMARY_AGENCY,
						loProcurementDetailsBean.getAgencyPrimaryContact());
				aoHmReqProposMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_SECONDARY_AGENCY,
						loProcurementDetailsBean.getAgencySecondaryContact());
				aoHmReqProposMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_EPIN,
						loProcurementDetailsBean.getProcurementEpin());
				aoHmReqProposMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_TITLE,
						loProcurementDetailsBean.getProcurementTitle());
				aoHmReqProposMap.put(P8Constants.PE_WORKFLOW_AGENCY_ID, loProcurementDetailsBean.getAgencyId());
				aoHmReqProposMap.put(P8Constants.PE_WORKFLOW_AGENCY_NAME, loProcurementDetailsBean.getAgencyName());
				aoHmReqProposMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_PRIMARY_AGENCY_ID,
						loProcurementDetailsBean.getAgencyPrimaryContactId());
				aoHmReqProposMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_SECONDARY_AGENCY_ID,
						loProcurementDetailsBean.getAgencySecondaryContactId());
				aoHmReqProposMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_PRIMARY_ACC_ID,
						loProcurementDetailsBean.getAccPrimaryContactId());
				aoHmReqProposMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_SECONDARY_ACC_ID,
						loProcurementDetailsBean.getAccSecondaryContactId());
				aoHmReqProposMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_PRIMARY_ACC,
						loProcurementDetailsBean.getAccPrimaryContact());
				aoHmReqProposMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_SECONDARY_ACC,
						loProcurementDetailsBean.getAccSecondaryContact());
				aoHmReqProposMap.put(P8Constants.PE_WORKFLOW_COMPETITION_POOL_TITLE,
						loProcurementDetailsBean.getCompetitionPoolTitle());
				aoHmReqProposMap.put(P8Constants.PROPERTY_PE_EVAL_GRP_TITLE,
						loProcurementDetailsBean.getEvaluationGroupTitle());
				aoHmReqProposMap.put(P8Constants.IS_OPEN_ENDED_RFP, loProcurementDetailsBean.getIsOpenEndedRfp());
				aoHmReqProposMap.put(P8Constants.EVALUATION_POOL_MAPPING_ID,
						aoDataMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID));
				setMoState("Successfully fetched procurement details for award workflow for evaluation pool mapping Id:"
						+ aoDataMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID));
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			setMoState("error while fetching procurement details for award workflow");
			aoExp.setContextData(aoDataMap);
			LOG_OBJECT.Error("error while fetching procurement details for award workflow", aoExp);
			LOG_OBJECT.Error("error while fetching accepted for evaluation proposal details", aoExp);
			throw aoExp;
		}
		// handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("error while fetching procurement details for award workflow", aoExp);
			setMoState("error while fetching procurement details for award workflow");
			throw new ApplicationException("error while fetching procurement details for award workflow", aoExp);
		}
		return aoHmReqProposMap;
	}

	/**
	 * <p>
	 * This method fetches External Evaluator Name corresponding to particular
	 * Evaluation Status Id from task details map
	 * <ul>
	 * <li>Get the task details map from input</li>
	 * <li>Get the evaluation status Id from task detail map and Check if
	 * evaluation id is null</li>
	 * <li>Fetch evaluator details from DB using query id
	 * <b>fetchEvaluatorDetails</b></li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoTaskMap - Task Details Map
	 * @param asWobNumber a string value of wob number
	 * @return lsEvaluatorDetail - string of evaluator name
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("rawtypes")
	public String fetchEvaluatorDetails(SqlSession aoMybatisSession, HashMap<String, Object> aoTaskMap,
			String asWobNumber) throws ApplicationException
	{
		String lsEvaluatorDetail = null;
		try
		{
			if (null != aoTaskMap)
			{
				HashMap loProcurementMap = (HashMap) aoTaskMap.get(asWobNumber);
				if (null != loProcurementMap)
				{
					String lsEvaluationStatusId = (String) loProcurementMap
							.get(P8Constants.PE_WORKFLOW_EVALUATION_STATUS_ID);
					if (lsEvaluationStatusId != null)
					{
						lsEvaluatorDetail = (String) DAOUtil.masterDAO(aoMybatisSession, lsEvaluationStatusId,
								HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.FETCH_EVALUATOR_DETAILS,
								HHSConstants.JAVA_LANG_STRING);
					}
					setMoState("Successfully fetched evaluator details :" + aoTaskMap);
				}
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while fetching evaluator details", aoAppEx);
			setMoState("Error while fetching evaluator details :" + aoTaskMap);
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error while fetching evaluator details", aoEx);
			setMoState("Error while fetching evaluator details");
			throw new ApplicationException("Error while fetching evaluator details", aoEx);
		}
		return lsEvaluatorDetail;
	}

	/**
	 * This method updates Evaluation review Details.
	 * 
	 * <ul>
	 * <li>1. Check if list is not null</li>
	 * <li>2. Iterate Over the list and update the data by executing the query
	 * "updateEvaluationReviewDetails" with EvaluationBean as a parameter.</li>
	 * </ul>
	 * @param aoMybatisSession - My batis session
	 * @param aoEvaluationBeanList - Evaluation bean list
	 * @return boolean flag return true when update successfully
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean updateEvaluationReviewsStatus(SqlSession aoMybatisSession, List<EvaluationBean> aoEvaluationBeanList)
			throws ApplicationException
	{
		Boolean loUpdateStatus = false;
		try
		{
			if (aoEvaluationBeanList != null)
			{
				for (EvaluationBean loEvaluationBean : aoEvaluationBeanList)
				{
					DAOUtil.masterDAO(aoMybatisSession, loEvaluationBean, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
							HHSConstants.UPDATE_EVALUATION_REVIEW_STATUS,
							HHSConstants.COM_NYC_HHS_MODEL_EVALUATION_BEAN);
				}
				loUpdateStatus = true;
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured while UPDATING evaluation review status in db", aoAppEx);
			setMoState("Exception occured while UPDATING evaluation review status in db");
			throw aoAppEx;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception occured while UPDATING evaluation review status in db", loExp);
			setMoState("Exception occured while UPDATING evaluation review status in db");
			throw new ApplicationException("Exception occured while UPDATING evaluation review status in db", loExp);
		}
		setMoState("Successfully updated evaluation review status in db");
		return loUpdateStatus;
	}

	/**
	 * This method finalizes Evaluation review Details.
	 * 
	 * <ul>
	 * <li>1. Check if list is not null</li>
	 * <li>2. Iterate Over the list and fetch list that needs to be updated
	 * <li>3. update the data by executing the query
	 * "finishEvaluationReviewsStatus" with EvaluationBean as a parameter.</li>
	 * <li>Execute query <b>finishEvaluationReviewsStatusCompleted </b></li>
	 * </ul>
	 * @param aoMybatisSession - My batis session
	 * @param aoEvaluationBeanList - Evaluation bean list
	 * @return flag depecting save is successfull
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public List<String> finishEvaluationReviewsStatus(SqlSession aoMybatisSession,
			List<EvaluationBean> aoEvaluationBeanList) throws ApplicationException
	{
		List<String> loEvalStatusIdList = null;

		try
		{
			if (aoEvaluationBeanList != null && aoEvaluationBeanList.size() > HHSConstants.INT_ZERO)
			{
				String lsProposalId = null;
				String lsProcStatusId = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_EVALUATE_PROPOSAL_TASK_SCORES_RETURNED);

				Map<String, Object> loDataMap = new HashMap<String, Object>();
				for (EvaluationBean loEvaluationBean : aoEvaluationBeanList)
				{
					lsProposalId = loEvaluationBean.getProposalId();
					if (loEvaluationBean.getProcStatusId() != null
							&& loEvaluationBean.getProcStatusId().equalsIgnoreCase(lsProcStatusId))
					{
						if (loEvalStatusIdList == null)
						{
							loEvalStatusIdList = new ArrayList<String>();
						}
						loEvalStatusIdList.add(loEvaluationBean.getEvaluationStatusId());
					}
				}
				loDataMap.put(HHSConstants.PROPOSAL_ID, lsProposalId);
				loDataMap.put(HHSConstants.EVAL_STATUS_LIST, loEvalStatusIdList);
				loDataMap.put(HHSConstants.AS_PROC_STATUS, lsProcStatusId);
				if (loEvalStatusIdList != null && loEvalStatusIdList.size() > HHSConstants.INT_ZERO)
				{
					DAOUtil.masterDAO(aoMybatisSession, loDataMap, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
							HHSConstants.FINISH_EVALUATION_REVIEW_STATUS, HHSConstants.JAVA_UTIL_MAP);
				}
				DAOUtil.masterDAO(aoMybatisSession, loDataMap, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
						HHSConstants.FINISH_EVALUATION_REVIEW_STATUS_COMPLETED, HHSConstants.JAVA_UTIL_MAP);

			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Exception occured while UPDATING evaluation review status in db", aoExp);
			setMoState("Exception occured while UPDATING evaluation review status in db");
			throw aoExp;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception occured while UPDATING evaluation review status in db", loExp);
			setMoState("Exception occured while UPDATING evaluation review status in db");
			throw new ApplicationException("Exception occured while UPDATING evaluation review status in db", loExp);
		}
		setMoState("Successfully updated evaluation review status in db");
		return loEvalStatusIdList;
	}

	/**
	 * This method inserts evaluation results, updates propsoal status once the
	 * task is finished i.e. all evaluator scores are accepted
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. CHeck if scores are accepted</li>
	 * <li>2. If yes add the required data to map</li>
	 * <li>3. Invoke query "insertEvaluationResult" to insert evaluation results
	 * </li>
	 * <li>4. Invoke query "updateSelectedProposalStatus" to update proposal
	 * config status</li>
	 * </ul>
	 * 
	 * Change: updated query for competition pool
	 * 
	 * Changed By: Pallavi
	 * 
	 * Change Date: 14 Jan 2014
	 * 
	 * @param aoMybatisSession - My batis session
	 * @param asStatus - Workflow status
	 * @param asUserId - User id of user
	 * @param asScore - Average Score
	 * @param asProposalId - Proposal id
	 * @param asEvaluationPoolMappingId - Evaluation Pool Mapping ID
	 * @return - flag depecting save is successfull
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean updateEvaluationResult(SqlSession aoMybatisSession, String asStatus, String asUserId,
			String asScore, String asProposalId, String asEvaluationPoolMappingId) throws ApplicationException
	{
		Boolean loUpdateStatus = false;
		try
		{
			if (asStatus != null && asStatus.equalsIgnoreCase(HHSConstants.SCORES_ACCEPTED))
			{
				Map<String, String> loDataMap = new HashMap<String, String>();
				loDataMap.put(HHSConstants.USER_ID, asUserId);
				loDataMap.put(HHSConstants.SCORE, asScore);
				loDataMap.put(HHSConstants.PROPOSAL_STATUS_ID_KEY, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_EVALUATED));
				loDataMap.put(HHSConstants.PROPOSAL_ID, asProposalId);
				loDataMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
				Integer loRecoredUpdated = (Integer) DAOUtil.masterDAO(aoMybatisSession, loDataMap,
						HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.UPDATE_EVALUATION_RESULT,
						HHSConstants.JAVA_UTIL_MAP);
				if (loRecoredUpdated == HHSConstants.INT_ZERO)
				{
					DAOUtil.masterDAO(aoMybatisSession, loDataMap, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
							HHSConstants.INSERT_EVALUATION_RESULT, HHSConstants.JAVA_UTIL_MAP);
				}
				DAOUtil.masterDAO(aoMybatisSession, loDataMap, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
						HHSConstants.UPDATE_PROPOSAL_REVIEW_STATUS, HHSConstants.JAVA_UTIL_MAP);
				loUpdateStatus = true;
				setMoState("Successfully updated evaluation results for evaluation pool mapping Id:"
						+ asEvaluationPoolMappingId);
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error(
					"Exception occured while UPDATING evaluation results in db for evaluation pool mapping Id:"
							+ asEvaluationPoolMappingId, aoAppEx);
			setMoState("Exception occured while UPDATING evaluation results in db for evaluation pool mapping Id:"
					+ asEvaluationPoolMappingId);
			throw aoAppEx;
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error(
					"Exception occured while UPDATING evaluation results in db for evaluation pool mapping Id:"
							+ asEvaluationPoolMappingId, aoEx);
			setMoState("Exception occured while UPDATING evaluation results in db for evaluation pool mapping Id:"
					+ asEvaluationPoolMappingId);
			throw new ApplicationException(
					"Exception occured while UPDATING evaluation results in db for evaluation pool mapping Id:"
							+ asEvaluationPoolMappingId, aoEx);
		}
		setMoState("Successfully updated evaluation results in db for evaluation pool mapping Id:"
				+ asEvaluationPoolMappingId);
		return loUpdateStatus;
	}

	/**
	 * This method will get the details bean of the Evaluations for the specific
	 * ProcurementId and evaluation pool mapping Id
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Get the task map from the argument</li>
	 * <li>Get the procurement Id and evaluation pool mapping Id from map</li>
	 * <li>If evaluation pool mapping Id is null, fetch evaluation pool mapping
	 * Id corresponding to procurement Id</li>
	 * <li>4.Execute select query <b>fetchEvaluationResultsScores</b> from
	 * evaluation mapper to get evaluation results and scores details</li>
	 * </ul>
	 * 
	 * Change: updated query for evaluation pool mapping Id
	 * 
	 * Changed By: Pallavi
	 * 
	 * Change Date: 14 Mar 2014
	 * 
	 * @param aoMybatisSession SqlSession Object
	 * @param aoTaskDetailMap a map containing procurement and task details
	 * @param asWobNumber a string value of wob number
	 * @return list of EvaluationBean
	 * @throws ApplicationException application Exception Object
	 */
	@SuppressWarnings("unchecked")
	public List<EvaluationBean> fetchEvaluationResultsScores(SqlSession aoMybatisSession,
			HashMap<String, Object> aoTaskDetailMap, String asWobNumber) throws ApplicationException
	{
		List<EvaluationBean> loEvaluationDetailsList = null;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.WOB_NUMBER, asWobNumber);
		LOG_OBJECT.Debug("Entered into fetchEvaluationResultsScores for wob number:" + loContextDataMap.toString());
		try
		{
			if (null != aoTaskDetailMap)
			{
				HashMap<String, Object> loTaskMap = (HashMap<String, Object>) aoTaskDetailMap.get(asWobNumber);
				if (null != loTaskMap)
				{
					String lsProcurementId = (String) loTaskMap.get(P8Constants.PE_WORKFLOW_PROCUREMENT_ID);
					String lsEvalPoolMappingId = (String) loTaskMap.get(P8Constants.EVALUATION_POOL_MAPPING_ID);
					if (null == lsEvalPoolMappingId || lsEvalPoolMappingId.equalsIgnoreCase(HHSConstants.EMPTY_STRING))
					{
						lsEvalPoolMappingId = (String) DAOUtil.masterDAO(aoMybatisSession, lsProcurementId,
								HHSConstants.MAPPER_CLASS_AWARDS_MAPPER, HHSConstants.FETCH_EVAL_POOL_MAPPING_ID,
								HHSConstants.JAVA_LANG_STRING);
						loTaskMap.put(P8Constants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolMappingId);
					}
					loEvaluationDetailsList = (List<EvaluationBean>) DAOUtil.masterDAO(aoMybatisSession, loTaskMap,
							HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_EVAL_RESULT_SCORE,
							HHSConstants.JAVA_UTIL_HASH_MAP);
					setMoState("Evaluation Results & Scores Fetched successfully for ProcurementId:"
							+ loTaskMap.get(P8Constants.PE_WORKFLOW_PROCUREMENT_ID));
				}
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(aoTaskDetailMap);
			LOG_OBJECT.Error("Error occurred while fething evaluation Results & Scores for input", aoAppEx);
			setMoState("Error occurred while fething evaluation Results & Scores");
			throw aoAppEx;
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occurred while fething evaluation Results & Scores for input", aoEx);
			setMoState("Error occurred while fething evaluation Results & Scores");
			throw new ApplicationException("Error occurred while fething evaluation Results & Scores for input", aoEx);
		}
		LOG_OBJECT.Debug("Exited fetchEvaluationResultsScores for wob number:" + loContextDataMap.toString());
		return loEvaluationDetailsList;
	}

	/**
	 * This method will fetch selection comments for proposal id selected from
	 * comments map
	 * 
	 * <ul>
	 * <li>Get comments map containing proposal id from input</li>
	 * <li>Execute query with id "fetchSelectionCommentsForAwardTask" from
	 * evaluation mapper</li>
	 * <li>Return evaluation bean object to controller</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession Object
	 * @param aoCommentsMap comments map
	 * @return evaluation bean object
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public EvaluationBean fetchSelectionCommentsForAwardTask(SqlSession aoMybatisSession,
			HashMap<String, String> aoCommentsMap) throws ApplicationException
	{
		EvaluationBean loEvaluationBean = null;
		LOG_OBJECT.Debug("Entered into fetchSelectionCommentsForAwardTask for input:" + aoCommentsMap.toString());
		try
		{
			loEvaluationBean = (EvaluationBean) DAOUtil.masterDAO(aoMybatisSession, aoCommentsMap,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_SEL_COMMENT_FOR_AWARD_TASK,
					HHSConstants.JAVA_UTIL_HASH_MAP);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occurred while fetcing selection comments");
			setMoState("Error occurred while fetcing selection comments");
			throw aoAppEx;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error occurred while fetcing selection comments");
			setMoState("Error occurred while fetcing selection comments");
			throw new ApplicationException("Error occurred while fetcing selection comments", loExp);
		}
		LOG_OBJECT.Debug("Exited fetchSelectionCommentsForAwardTask for input:" + aoCommentsMap.toString());
		return loEvaluationBean;
	}

	/**
	 * This method is used for fetching award approval date from database for
	 * given procurement Id and evaluation pool mapping ID
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. Fetches award approval date for input procurement Id and
	 * evaluation pool mapping ID using <b>fetchAwardAppDate</b> from evaluation
	 * mapper</li>
	 * <li>2. If Award approval date is not null, iterate through the evaluation
	 * results list and set award approval date</li>
	 * <li>3. Else return input evaluation results list</li>
	 * </ul>
	 * 
	 * Change: updated query for Evaluation Pool Mapping Id
	 * 
	 * Changed By: Pallavi
	 * 
	 * Change Date: 21 Jan 2014
	 * 
	 * @param aoMybatisSession MybatisSession
	 * @param aoEvalFilterBean Eval Filter Bean
	 * @param aoEvalResults EvalResults
	 * @return EvalResults - a List
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public List<EvaluationBean> fetchAwardAppDate(SqlSession aoMybatisSession, EvaluationFilterBean aoEvalFilterBean,
			List<EvaluationBean> aoEvalResults) throws ApplicationException
	{
		String loAwardAppDate = null;
		LOG_OBJECT.Debug("Entered into fetchAwardAppDate for input:" + aoEvalFilterBean.getProcurementId());
		try
		{
			loAwardAppDate = (String) DAOUtil.masterDAO(aoMybatisSession, aoEvalFilterBean,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_AWARD_APPROVAL_DATE,
					HHSConstants.COM_NYC_HHS_MODEL_EVALFILTERBEAN);
			if (loAwardAppDate != null && null != aoEvalResults)
			{
				for (EvaluationBean loEvaluationBean : aoEvalResults)
				{
					loEvaluationBean.setAwardApprovalDate(DateUtil.getDate(DateUtil.getCurrentDate()));
				}
			}
			else
			{
				return aoEvalResults;
			}
			setMoState("Successfully fetched Award Approval Date for evaluation pool mapping Id:"
					+ aoEvalFilterBean.getEvaluationPoolMappingId());
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occurred while fetcing award approval date for evaluation pool mapping Id:"
					+ aoEvalFilterBean.getEvaluationPoolMappingId());
			setMoState("Error occurred while fetcing award approval date for evaluation pool mapping Id:"
					+ aoEvalFilterBean.getEvaluationPoolMappingId());
			throw aoAppEx;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occurred while fetcing award approval date for evaluation pool mapping Id:"
					+ aoEvalFilterBean.getEvaluationPoolMappingId());
			setMoState("Error occurred while fetcing award approval date for evaluation pool mapping Id:"
					+ aoEvalFilterBean.getEvaluationPoolMappingId());
			throw new ApplicationException(
					"Error occurred while fetcing award approval date for evaluation pool mapping Id:"
							+ aoEvalFilterBean.getEvaluationPoolMappingId(), aoEx);
		}
		LOG_OBJECT.Debug("Exited fetchAwardAppDate for input:" + aoEvalFilterBean.getProcurementId());
		return aoEvalResults;
	}

	/**
	 * This method will fetch evaluators list corresponding to the Procurement
	 * Id and evaluation pool mapping Id
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. Retrieve procurement Id and evaluation pool mapping Id from the
	 * Channel object</li>
	 * <li>2. Create context data HashMap and populate the same with the
	 * procurement Id and evaluation pool mapping Id</li>
	 * <li>3. Executes query <b>fetchEvaluatorsList</b> to fetch the Evaluators
	 * list from the data base tables corresponding to the procurement Id and
	 * evaluation pool mapping Id</li>
	 * <li>4. Return the fetched result</li>
	 * </ul>
	 * 
	 * Change: updated query for competition pool
	 * 
	 * Changed By: Pallavi
	 * 
	 * Change Date: 11 Mar 2014
	 * 
	 * @param aoMyBatisSession - mybatis SQL Session Object
	 * @param asProcurementId - string representation of Procurement Id
	 * @param asEvaluationPoolMappingId - Evaluation Pool Mapping Id
	 * @return loEvaluatorsList - list of evaluators
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<EvaluationDetailBean> fetchEvaluatorsList(SqlSession aoMyBatisSession, String asProcurementId,
			String asEvaluationPoolMappingId) throws ApplicationException
	{
		List<EvaluationDetailBean> loEvaluatorsList = null;
		Map<String, String> loContextMap = new HashMap<String, String>();
		loContextMap.put(HHSConstants.PROCUREMENT_ID, asProcurementId);
		loContextMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
		LOG_OBJECT.Debug("Entered into fetching Evaluators list::" + loContextMap.toString());
		try
		{
			loEvaluatorsList = (List<EvaluationDetailBean>) DAOUtil.masterDAO(aoMyBatisSession, loContextMap,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_EVALUATORS_LIST,
					HHSConstants.JAVA_UTIL_MAP);
			setMoState("Succuessfully fetched evaluators list for evaluation pool mapping Id:"
					+ asEvaluationPoolMappingId);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(loContextMap);
			LOG_OBJECT.Error(
					"Exception occured while fetching Evaluators list corresponding for evaluation pool mapping Id:"
							+ asEvaluationPoolMappingId, aoAppEx);
			setMoState("Transaction Failed:: EvaluationService:fetchEvaluatorsList method - while fetching Evaluators list for evaluation pool mapping Id:"
					+ asEvaluationPoolMappingId);
			throw aoAppEx;
		}
		// Handling Exception other than ApplicationException
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured while fetching Evaluators list for evaluation pool mapping Id:"
					+ asEvaluationPoolMappingId, aoAppEx);
			setMoState("Transaction Failed:: EvaluationService:fetchEvaluatorsList method - while fetching Evaluators list for evaluation pool mapping Id:"
					+ asEvaluationPoolMappingId);
			throw new ApplicationException(
					"Exception occured while fetching Evaluators list for evaluation pool mapping Id:"
							+ asEvaluationPoolMappingId, aoAppEx);
		}
		return loEvaluatorsList;
	}

	/**
	 * This method will fetch user email Id list corresponding to the proposal
	 * Id task
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. Retrieve Proposal Id from the channel</li>
	 * <li>2. Create one context map and populate the same with the proposal Id</li>
	 * <li>3. Retrieve the notification map and fetch request map from it</li>
	 * <li>4. Retrieve procurement Title and put it in the request map</li>
	 * <li>5. If the fetched proposal Id is not null then execute query
	 * <b>fetchUserEmailIds</b> to fetch user email Id list</li>
	 * <li>6. If the fetched user email Id list is not null and notification map
	 * is not null then populate the notification map with the retrieved email
	 * Id list</li>
	 * <li>7. Return the notification map</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProposalId - string representation of Proposal Id
	 * @param asProcurementTitle - string representation of Procurement Title
	 * @param aoNotificationMap - notification Map object
	 * @return aoNotificationMap - notification Map object
	 * @throws ApplicationException If any Exception occurs
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> fetchUserEmailIds(SqlSession aoMybatisSession, String asProposalId,
			String asProcurementTitle, HashMap<String, Object> aoNotificationMap) throws ApplicationException
	{

		List<String> loUserEmailIdList = null;

		Map loContextMap = new HashMap();
		loContextMap.put(HHSConstants.PROPOSAL_ID, asProposalId);
		LOG_OBJECT.Debug("Entered into fetching user email email ids::" + asProposalId);
		// checking if the proposal Id is not null
		if (asProposalId != null)
		{
			try
			{
				HashMap<String, String> loRequestMap = (HashMap<String, String>) aoNotificationMap
						.get(TransactionConstants.REQUEST_MAP_PARAMETER_NAME);
				loRequestMap.put(HHSConstants.PROC_TITLE, asProcurementTitle);

				loUserEmailIdList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, asProposalId,
						HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_USER_EMAIL_IDS,
						HHSConstants.JAVA_LANG_STRING);
				// checking if the fetched user email Id is neither null nor its
				// size is zero
				if (null != loUserEmailIdList && !loUserEmailIdList.isEmpty())
				{
					aoNotificationMap.put(TransactionConstants.USER_ID, loUserEmailIdList);
				}
				setMoState("User Email Ids fetched successfully corresponding to proposal Id");
			}
			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handles over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */
			catch (ApplicationException aoAppEx)
			{
				LOG_OBJECT
						.Error("Exception occured while fetching user email Id corresponding to proposal Id", aoAppEx);
				setMoState("Transaction Failed:: EvaluationService:modifyProposalStatus method - while fetching user email Id corresponding to proposal Id");
				aoAppEx.setContextData(loContextMap);
				throw aoAppEx;
			}
			// Handling Exception other than ApplicationException
			catch (Exception aoAppEx)
			{
				LOG_OBJECT.Error("Exception occured while updating successfully corresponding to proposal Id", aoAppEx);
				setMoState("Transaction Failed:: EvaluationService:modifyProposalStatus method - while fetching user email Id corresponding to proposal Id");
				throw new ApplicationException(
						"Exception occured while fetching user email Ids corresponding to proposal Id", aoAppEx);
			}
		}
		return aoNotificationMap;

	}

	/**
	 * This method will fetch user email Id list corresponding to the evaluation
	 * status Id task
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. Retrieve Evaluation Status Id List from the channel</li>
	 * <li>2. Create one context map and populate the same with the Evaluation
	 * Status Id List</li>
	 * <li>3. Retrieve the notification map and fetch request map from it</li>
	 * <li>4. Retrieve procurement Title and put it in the request map</li>
	 * <li>5. If the fetched Evaluation Status Id List is not null then execute
	 * query <b>fetchReturnedScoresUserEmailIds</b> to fetch user email Id list</li>
	 * <li>6. If the fetched user email Id list is not null and notification map
	 * is not null then populate the notification map with the retrieved email
	 * Id list</li>
	 * <li>7. Return the notification map</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProposalId - string representation of Proposal Id
	 * @param asProcurementTitle - string representation of Procurement Title
	 * @param aoNotificationMap - notification Map object
	 * @return aoNotificationMap - notification Map object
	 * @throws ApplicationException If any Exception occurs
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> fetchReturnedUserEmailIds(SqlSession aoMybatisSession,
			List<String> aoEvalStatusIdList, String asProcurementTitle, HashMap<String, Object> aoNotificationMap)
			throws ApplicationException
	{

		List<String> loUserEmailIdList = new ArrayList<String>();

		Map loContextMap = new HashMap();
		loContextMap.put(HHSConstants.PROPOSAL_ID, aoEvalStatusIdList);
		LOG_OBJECT.Debug("Entered into fetching user email email ids for score returned evaluators::"
				+ aoEvalStatusIdList);
		// checking if the list of evaluationStatusId is not null
		if (aoEvalStatusIdList != null && aoEvalStatusIdList.size() > HHSConstants.INT_ZERO)
		{
			try
			{
				HashMap<String, String> loRequestMap = (HashMap<String, String>) aoNotificationMap
						.get(TransactionConstants.REQUEST_MAP_PARAMETER_NAME);
				loRequestMap.put(HHSConstants.PROC_TITLE, asProcurementTitle);
				String lsUserEmailId = null;
				// iterating over the list of evaluationStatusId
				for (String lsEvaluationStatusId : aoEvalStatusIdList)
				{
					lsUserEmailId = (String) DAOUtil.masterDAO(aoMybatisSession, lsEvaluationStatusId,
							HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
							HHSConstants.FETCH_RETURN_SCORES_USER_EMAIL_IDS, HHSConstants.JAVA_LANG_STRING);

					loUserEmailIdList.add(lsUserEmailId);
				}

				// checking if the fetched user email Id is neither null nor its
				// size is zero
				if (null != loUserEmailIdList && !loUserEmailIdList.isEmpty())
				{
					aoNotificationMap.put(TransactionConstants.USER_ID, loUserEmailIdList);
				}
				setMoState("User Email Ids fetched successfully corresponding to proposal Id");
			}
			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handles over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */
			catch (ApplicationException aoAppEx)
			{
				LOG_OBJECT
						.Error("Exception occured while fetching user email Id corresponding to proposal Id", aoAppEx);
				setMoState("Transaction Failed:: EvaluationService:modifyProposalStatus method - while fetching user email Id corresponding to proposal Id");
				aoAppEx.setContextData(loContextMap);
				throw aoAppEx;
			}
			// Handling Exception other than ApplicationException
			catch (Exception aoAppEx)
			{
				LOG_OBJECT.Error("Exception occured while updating successfully corresponding to proposal Id", aoAppEx);
				setMoState("Transaction Failed:: EvaluationService:modifyProposalStatus method - while fetching user email Id corresponding to proposal Id");
				throw new ApplicationException(
						"Exception occured while fetching user email Ids corresponding to proposal Id", aoAppEx);
			}
		}
		return aoNotificationMap;

	}

	/**
	 * The Service clears the add delete flag for the contract for input
	 * procurement id and evaluation pool mapping id.
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. Add input parameters to map</li>
	 * <li>2. Trigger query <code>updateAddDelFlag</code> query from evaluation
	 * mapper to update add delete flag</li>
	 * <li>3. If the transaction is sucessful returns true, else returns false.</li>
	 * </ul>
	 * 
	 * Change: updated query for evaluation pool mapping Id
	 * 
	 * Changed By: Pallavi
	 * 
	 * Change Date: 14 Mar 2014
	 * 
	 * @param aoMybatisSession - SqlSession
	 * @param asProcurementId - Procurement Id
	 * @param asEvaluationPoolMappingId - Evaluation Pool Mapping Id
	 * @return update status
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Boolean updateAddDelFlag(SqlSession aoMybatisSession, String asProcurementId,
			String asEvaluationPoolMappingId) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into update Add Delete Flag");
		Map<String, Object> loParamMap = new HashMap<String, Object>();
		loParamMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		loParamMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
		Boolean loUpdateFlag = Boolean.FALSE;
		try
		{
			loParamMap.put(HHSConstants.ADD_DELETE_STATUS, HHSConstants.EMPTY_STRING);
			// Calling updateAddDelFlag for clearing add delete flag, passing
			// required parameters using Map
			DAOUtil.masterDAO(aoMybatisSession, loParamMap, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
					HHSConstants.UPDATE_ADD_DELETE_FLAG, HHSConstants.JAVA_UTIL_MAP);
			loUpdateFlag = Boolean.TRUE;
			setMoState("Successfully updated add delete flag for evaluation pool mapping id:"
					+ asEvaluationPoolMappingId);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error while updating Add Delete Flag for evaluation pool mapping id:"
					+ asEvaluationPoolMappingId, aoExp);
			setMoState("Error while updating Add Delete Flag for evaluation pool mapping id:"
					+ asEvaluationPoolMappingId);
			throw aoExp;
		}
		// handling exception other than Application exception
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while updating Add Delete Flag for evaluation pool mapping id:"
					+ asEvaluationPoolMappingId, aoExp);
			setMoState("Error while updating Add Delete Flag for evaluation pool mapping id:"
					+ asEvaluationPoolMappingId);
			throw new ApplicationException("Exception while updating Add Delete Flag for evaluation pool mapping id:"
					+ asEvaluationPoolMappingId, aoExp);
		}
		return loUpdateFlag;
	}

	/**
	 * This method is used to get the updated proposal due date for the
	 * procurement
	 * <ul>
	 * <li>Execute the query <code>getUpdatedProposalDueDate</code> from
	 * <code>evaluation</code> mapper</li>
	 * <li>Return the updated proposal due date for the given procurement id.</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession sql session object
	 * @param asProcurementId String procurement id
	 * @return lsUpdatedPropDueDate Timestamp
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Timestamp getUpdatedProposalDueDate(SqlSession aoMybatisSession, String asProcurementId)
			throws ApplicationException
	{
		Timestamp lsUpdatedPropDueDate = null;

		try
		{
			// executing query getUpdatedProposalDueDate of EvaluationMapper.xml
			lsUpdatedPropDueDate = (Timestamp) DAOUtil.masterDAO(aoMybatisSession, asProcurementId,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.GET_UPDATED_PROPOSAL_DUE_DATE,
					HHSConstants.JAVA_LANG_STRING);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			// populating context data map for exceptional handling
			HashMap<String, Object> loHMContextData = new HashMap<String, Object>();
			loHMContextData.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
			aoExp.setContextData(loHMContextData);
			setMoState("Error while fetching updated proposal due date:");
			LOG_OBJECT.Error("Error while fetching updated proposal due date:", aoExp);
			throw aoExp;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error while fetching updated proposal due date ", aoEx);
			setMoState("Error while fetching updated proposal due date");
			throw new ApplicationException("Error while fetching updated proposal due date", aoEx);
		}
		return lsUpdatedPropDueDate;
	}

	/**
	 * This method will fetch count of records in evaluation status table
	 * corresponding to the Procurement Id and evaluation pool mapping id
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. Retrieve procurement Id and evaluation pool mapping id from the
	 * Channel object</li>
	 * <li>2. Add input parameters to map</li>
	 * <li>3. If the fetched procurement Id and evaluation pool mapping id is
	 * not null then execute query <b>fetchEvaluationStatusCount</b> to fetch
	 * the record count from the evaluation_status table corresponding to the
	 * procurement Id and evaluation pool mapping id</li>
	 * <li>4. Return the fetched result</li>
	 * </ul>
	 * 
	 * Change: updated query for competition pool
	 * 
	 * Changed By: Pallavi
	 * 
	 * Change Date: 2 Jan 2014
	 * 
	 * @param aoMyBatisSession - mybatis SQL Session Object
	 * @param asProcurementId - string representation of Procurement Id
	 * @param asEvaluationPoolMappingId - evaluation pool mapping id
	 * @return loLaunchWorkflow - boolean status flag
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean fetchEvaluationStatusCount(SqlSession aoMyBatisSession, String asProcurementId,
			String asEvaluationPoolMappingId) throws ApplicationException
	{

		Boolean loLaunchWorkflow = false;
		Map<String, String> loContextDataMap = new HashMap<String, String>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID, asProcurementId);
		loContextDataMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
		LOG_OBJECT.Debug("Entered into fetching evaluation status table record count::" + loContextDataMap.toString());
		// checking if the procurement Id is not null
		if (asProcurementId != null && asEvaluationPoolMappingId != null)
		{
			try
			{
				Map<String, String> loDataMap = new HashMap<String, String>();
				loDataMap.put(HHSConstants.PROCUREMENT_ID, asProcurementId);
				loDataMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
				Integer loCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loDataMap,
						HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_EVALUATION_STATUS_COUNT,
						HHSConstants.JAVA_UTIL_MAP);
				if (loCount > HHSConstants.INT_ZERO)
				{
					loLaunchWorkflow = true;
				}
				setMoState("Evaluation Status count fetched successfully corresponding to procurement Id:"
						+ asProcurementId);
			}
			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handles over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */
			catch (ApplicationException aoAppEx)
			{
				aoAppEx.setContextData(loContextDataMap);
				LOG_OBJECT.Error(
						"Exception occured while fetching Evaluation Status count corresponding to procurement Id",
						aoAppEx);
				setMoState("Transaction Failed:: EvaluationService:fetchEvaluationStatusCount method - while fetching Evaluation Status count corresponding to procurement Id");
				throw aoAppEx;
			}
			// Handling Exception other than ApplicationException
			catch (Exception aoAppEx)
			{
				LOG_OBJECT.Error(
						"Exception occured while fetching Evaluation Status count corresponding to procurement Id:"
								+ asProcurementId, aoAppEx);
				setMoState("Transaction Failed:: EvaluationService:fetchEvaluationStatusCount method - while fetching Evaluation Status count corresponding to procurement Id:"
						+ asProcurementId);
				throw new ApplicationException(
						"Exception occured while fetching Evaluation Status count corresponding to procurement Id",
						aoAppEx);
			}
		}
		return loLaunchWorkflow;
	}

	/**
	 * This method is used to get the document id list from document bean list
	 * @param aoDocIdsList bean list
	 * @return list of document ids
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public List<String> fetchDBDDocIdsList(List<Map<String, String>> aoDocIdsList) throws ApplicationException
	{
		List<String> loDocumentIdList = new ArrayList<String>();
		try
		{
			if (null != aoDocIdsList)
			{
				for (Iterator<Map<String, String>> loDocumentItr = aoDocIdsList.iterator(); loDocumentItr.hasNext();)
				{
					Map<String, String> loDocMap = (Map<String, String>) loDocumentItr.next();
					if (null != loDocMap && null != loDocMap.get(HHSConstants.DOCUMENT_IDENTIFIER_ID))
					{
						loDocumentIdList.add(loDocMap.get(HHSConstants.DOCUMENT_IDENTIFIER_ID));
					}
				}
			}
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Error while fetching document ids from document bean ", aoAppEx);
			throw new ApplicationException("Error while fetching document ids from document bean ", aoAppEx);
		}
		return loDocumentIdList;
	}

	/**
	 * This method will add all the properties values from filenet in the
	 * document list
	 * 
	 * @param aoDocumentPropHM document properties map from filenet
	 * @param aoDocDetailList Doc Detail List
	 * @return aoDocDetailList - list of extended document bean
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, String>> consolidateAllDocsProperties(HashMap<String, Object> aoDocumentPropHM,
			List<Map<String, String>> aoDocDetailList) throws ApplicationException
	{
		String lsDocumentId = null;
		Map<String, String> loDocMap = null;
		HashMap<String, Object> loDocPropsBean = null;
		try
		{
			if (null != aoDocDetailList && aoDocDetailList.size() > HHSConstants.INT_ZERO)
			{
				for (Iterator<Map<String, String>> loDocIterator = aoDocDetailList.iterator(); loDocIterator.hasNext();)
				{
					loDocMap = (Map<String, String>) loDocIterator.next();
					lsDocumentId = loDocMap.get(HHSConstants.DOCUMENT_IDENTIFIER_ID);
					if (null != lsDocumentId && null != aoDocumentPropHM)
					{
						loDocPropsBean = (HashMap<String, Object>) aoDocumentPropHM.get(lsDocumentId);
						if (null != loDocPropsBean && loDocPropsBean.size() > HHSConstants.INT_ZERO)
						{
							loDocMap.put(HHSConstants.FILE_DOCUMENT_TITLE,
									(String) loDocPropsBean.get(P8Constants.PROPERTY_CE_DOCUMENT_TITLE));
						}
					}
				}
			}
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error while fetching document ids from document bean ", aoEx);
			throw new ApplicationException("Error while fetching document ids from document bean ", aoEx);
		}
		return aoDocDetailList;
	}

	/**
	 * This method is used to get the count of proposals whose status is not
	 * nonresponsive
	 * <ul>
	 * <li>Execute the query <code>fetchNotNonResponsiveCount</code> from
	 * <code>evaluation</code> mapper</li>
	 * <li>Return the count of proposals</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession
	 * @param aoParamMap Map
	 * @return Integer loProposalCount
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Integer fetchNotNonResponsiveCount(SqlSession aoMybatisSession, Map<String, String> aoParamMap)
			throws ApplicationException
	{
		int liProposalCount = 0;
		try
		{
			aoParamMap.put(HHSConstants.STATUS_ID, PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_PROPOSAL_NON_RESPONSIVE));
			aoParamMap.put(HHSConstants.LS_DRAFT_ID, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_DRAFT));
			// executing query fetchNotNonResponsiveCount of
			// EvaluationMapper.xml
			liProposalCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoParamMap,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_NOT_NON_RESPONSIVE_COUNT,
					HHSConstants.JAVA_UTIL_MAP);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			aoExp.setContextData(aoParamMap);
			LOG_OBJECT.Error("Error while fetching proposal count which are not non responsive: ", aoExp);
			// populating context data map for exceptional handling
			setMoState("Error while fetching proposal count which are not non responsive");
			throw aoExp;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error while fetching proposal count which are not non responsive: ", aoEx);
			setMoState("Error while fetching proposal count which are not non responsive");
			throw new ApplicationException("Error while fetching proposal count which are not non responsive", aoEx);
		}
		return liProposalCount;
	}

	/**
	 * This method is used to update the evaluation sent flag for input
	 * evaluation pool mapping id
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. Add input parameters to map</li>
	 * <li>2. Execute the query <code>updateEvaluationSentFlag</code> from
	 * <code>evaluation</code> mapper</li>
	 * <li>3. Return the boolean flag ture if the update is sucessful else
	 * return false.</li>
	 * </ul>
	 * 
	 * Change: updated query for evaluation pool mapping id
	 * 
	 * Changed by: Pallavi
	 * 
	 * Change Date: 7 Jan 2014
	 * 
	 * @param aoMybatisSession SqlSession sql session object
	 * @param asEvalPoolMappingId Evaluation Pool Mapping Id
	 * @return loUpdateFlag Boolean
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean updateEvaluationSentFlag(SqlSession aoMybatisSession, String asEvalPoolMappingId)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into update evaluation sent flag");
		Map<String, Object> loParamMap = new HashMap<String, Object>();
		loParamMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvalPoolMappingId);
		Boolean loUpdateFlag = Boolean.FALSE;
		try
		{
			loParamMap.put(HHSConstants.EVALUATION_SENT, HHSConstants.ONE);
			// Calling updateEvaluationSentFlag for clearing add delete flag,
			// passing
			// required parameters using Map
			Integer loUpdateCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loParamMap,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.UPDATE_EVALUATION_SENT_FLAG,
					HHSConstants.JAVA_UTIL_MAP);
			if (loUpdateCount > 0)
			{
				loUpdateFlag = Boolean.TRUE;
			}
			setMoState("Successfully updates evaluation sent flag for evaluation pool mapping Id:"
					+ asEvalPoolMappingId);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error while updating evaluation sent flag for evaluation pool mapping Id:"
					+ asEvalPoolMappingId, aoExp);
			setMoState("Error while updating evaluation sent flag for evaluation pool mapping Id:"
					+ asEvalPoolMappingId);
			throw aoExp;
		}
		// handling exception other than Application exception
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while updating evaluation sent flag for evaluation pool mapping Id:"
					+ asEvalPoolMappingId, aoExp);
			setMoState("Error while updating evaluation sent flag for evaluation pool mapping Id:"
					+ asEvalPoolMappingId);
			throw new ApplicationException(
					"Exception while updating evaluation sent flag for evaluation pool mapping Id:"
							+ asEvalPoolMappingId, aoExp);
		}
		return loUpdateFlag;
	}

	/**
	 * This method is used to get the evaluation sent flag which indicates that
	 * the evaluation task has already been sent for input evaluation pool
	 * mapping id
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. Add input parameters to map</li>
	 * <li>2. Execute the query <code>fetchEvaluationSentFlag</code> from
	 * <code>evaluation</code> mapper</li>
	 * <li>3. Return the evaluation sent flag which indicates that the
	 * evaluation task has already been sent.</li>
	 * </ul>
	 * 
	 * Change: updated query for competition pool
	 * 
	 * Changed By: Pallavi
	 * 
	 * Change Date: 31 Dec 2013
	 * 
	 * @param aoMybatisSession SqlSession sql session object
	 * @param asEvaluationPoolMappingId String Evaluation Pool Mapping Id
	 * @return lsEvaluationSent String
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public String getEvaluationSentFlag(SqlSession aoMybatisSession, String asEvaluationPoolMappingId)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into get evaluation sent flag");
		String lsEvaluationSent = null;
		Map<String, Object> loParamMap = new HashMap<String, Object>();
		loParamMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
		try
		{
			// Calling fetchEvaluationSentFlag for clearing add delete flag,
			// passing
			// required parameters using Map
			lsEvaluationSent = (String) DAOUtil.masterDAO(aoMybatisSession, asEvaluationPoolMappingId,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_EVALUATION_SENT_FLAG,
					HHSConstants.JAVA_LANG_STRING);
			setMoState("Fetches evaluation sent flag for evaluation pool mapping id:" + asEvaluationPoolMappingId);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error while getting evaluation sent flag for evaluation pool mapping id:"
					+ asEvaluationPoolMappingId, aoExp);
			setMoState("Error while getting evaluation sent flag for evaluation pool mapping id:"
					+ asEvaluationPoolMappingId);
			throw aoExp;
		}
		// handling exception other than Application exception
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while getting evaluation sent flag for evaluation pool mapping id:"
					+ asEvaluationPoolMappingId, aoExp);
			setMoState("Error while getting evaluation sent flag for evaluation pool mapping id:"
					+ asEvaluationPoolMappingId);
			throw new ApplicationException(
					"Exception while getting evaluation sent flag for evaluation pool mapping id:"
							+ asEvaluationPoolMappingId, aoExp);
		}
		return lsEvaluationSent;
	}

	/**
	 * This method checks the workfow status and sends boolean status flag
	 * corresponding to it.
	 * 
	 * <li>1. Retrieve workflow status and boolean status flag from the channel</li>
	 * <li>2. If the retrieved status is not null and is equal to Score Returned
	 * and notification map is not null then set the value of output boolean
	 * flag as true</li> <li>3. Return output boolean flag</li>
	 * 
	 * @param asStatus - Workflow status
	 * @param aoDocDetailList - Doc Detail List
	 * @return aoNotificationMap - notification map
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	public Boolean checkScoresReturnedStatus(String asStatus, HashMap<String, Object> aoNotificationMap)
			throws ApplicationException
	{
		Boolean loScoreReturnedStatus = false;
		// checking if status Id is not null and is equal to score returned and
		// notification map is not null
		if (asStatus != null && asStatus.equalsIgnoreCase(HHSConstants.SCORES_RETURNED) && aoNotificationMap != null)
		{
			loScoreReturnedStatus = true;
		}
		return loScoreReturnedStatus;
	}

	/**
	 * This method is used to get the total award amount for input procurement
	 * id and evaluation pool mapping id
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. Fetches updated award amount for input procurement id and
	 * evaluation pool mapping id using <b>fetchUpdatedAwardAmount</b> from
	 * evaluation mapper</li>
	 * <li>2. If total award amount is null, set it as 0.00</li>
	 * </ul>
	 * 
	 * Change: updated query for competition pool
	 * 
	 * Changed By: Pallavi
	 * 
	 * Change Date: 14 Mar 2014
	 * 
	 * @param aoMybatisSession MybatisSession
	 * @param aoDataMap Input parameter map
	 * @return lsTotalAwardAmount - string
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public String fetchUpdatedAwardAmount(SqlSession aoMybatisSession, Map<String, Object> aoDataMap)
			throws ApplicationException
	{
		String lsTotalAwardAmount = "0.00";
		try
		{
			lsTotalAwardAmount = (String) DAOUtil.masterDAO(aoMybatisSession, aoDataMap,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_UPDATED_AWARD_AMOUNT,
					HHSConstants.JAVA_UTIL_MAP);
			if (null == lsTotalAwardAmount)
			{
				lsTotalAwardAmount = "0.00";
			}
			setMoState("Fetches updated award amount for evaluation pool mapping id:"
					+ aoDataMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID));
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			setMoState("error while fetching accepted for evaluation proposal details");
			aoExp.setContextData(aoDataMap);
			LOG_OBJECT.Error("error while fetching accepted for evaluation proposal details", aoExp);
			LOG_OBJECT.Error("error while fetching accepted for evaluation proposal details", aoExp);
			throw aoExp;
		}
		// handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("error while fetching accepted for evaluation proposal details", aoExp);
			setMoState("error while fetching accepted for evaluation proposal details");
			throw new ApplicationException("error while fetching accepted for evaluation proposal details", aoExp);
		}
		return lsTotalAwardAmount;
	}

	/**
	 * This method is used to fetch proposal and organization name <li>Execute
	 * query id <b> fetchProposalAndOrgNam </b></li>
	 * @param aoMybatisSession MybatisSession
	 * @param asProposalId proposal id
	 * @return loHeaderMap map
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> fetchProposalAndOrgName(SqlSession aoMybatisSession, String asProposalId)
			throws ApplicationException
	{
		Map<String, String> loHeaderMap = null;
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROPOSAL_ID_KEY, asProposalId);
		try
		{
			loHeaderMap = (Map<String, String>) DAOUtil.masterDAO(aoMybatisSession, asProposalId,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_PROPOSAL_AND_ORG_NAME,
					HHSConstants.JAVA_LANG_STRING);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoExp)
		{
			aoExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching the proposal detail  ", aoExp);
			setMoState("Error while getting Proposal Header Details");
			throw aoExp;
		}
		/**
		 * Any Exception from DAO class will be handles over here. It throws
		 * Application Exception back to Controllers calling method through
		 * Transaction framework
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetching the proposal detail  ", aoExp);
			setMoState("Error while getting Proposal Header Details");
		}
		return loHeaderMap;
	}

	/**
	 * 
	 * Below method is added as per enhancement 5415. This method fetches
	 * Evaluation score, criteria and comments Details corresponding to a
	 * evaluator
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Updated Method in R4</li>
	 * <li>1. Fetch Evaluation score Details which includes comment and criteria
	 * for the evaluator based on evaluationStatusId using
	 * <b>fetchEvaluationScoreDetailsForEvaluator</b></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoQueryMap - Evaluation Status Map
	 * @return loEvaluationBeanList - List of evaluation Bean
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<EvaluationBean> fetchEvaluationScoreDetailsForEvaluator(SqlSession aoMybatisSession,
			Map<String, Object> aoQueryMap) throws ApplicationException
	{
		List<EvaluationBean> loEvaluationBeanList = null;
		try
		{
			List<String> loEvalStatusList = (List<String>) DAOUtil.masterDAO(aoMybatisSession,
					aoQueryMap.get(HHSConstants.PROPOSAL_ID), HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
					HHSR5Constants.GET_EVAL_STATUS_LIST, HHSConstants.JAVA_LANG_STRING);
			aoQueryMap.put(HHSConstants.EVAL_STATUS_LIST, loEvalStatusList);
			loEvaluationBeanList = (List<EvaluationBean>) DAOUtil.masterDAO(aoMybatisSession, aoQueryMap,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
					HHSConstants.FETCH_EVALUATION_SCORE_DETAILS_FOR_EVALUATOR, HHSConstants.JAVA_UTIL_MAP);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoExp)
		{
			aoExp.setContextData(aoQueryMap);
			LOG_OBJECT
					.Error("ApplicationException while fetching score and criteria details in fetchEvaluationScoreDetailsForEvaluator:: ",
							aoExp);
			setMoState("ApplicationException while fetching score and criteria details in fetchEvaluationScoreDetailsForEvaluator:: ");
			throw aoExp;
		}
		// handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			LOG_OBJECT
					.Error("Exception while fetching score and criteria details in fetchEvaluationScoreDetailsForEvaluator:: ",
							aoExp);
			setMoState("Exception while fetching score and criteria details in fetchEvaluationScoreDetailsForEvaluator:: ");
			throw new ApplicationException(
					"Exception while fetching score and criteria details in fetchEvaluationScoreDetailsForEvaluator:: ",
					aoExp);

		}
		return loEvaluationBeanList;
	}

	/**
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>New method - Added By: Siddharth Bhola</li>
	 * <li>Reason: Enhancement id: 5436</li>
	 * <li>This method is added as part of Production Support release (2.5.0)</li>
	 * </ul>
	 * <ul>
	 * <li>It is enhancement request to maintain version history when review
	 * score task is finished/(scores returned)</li>
	 * <li>This service will copy data based upon proposal whose review score
	 * task is either a.) finished (completed) b.) finished with scores returned
	 * </li>
	 * <li>In both of the cases for each evualuator, we copy data from current
	 * tables to archive tables Tables: EVALUATION_VERSION_ARCHIVE,
	 * EVALUATION_SCORE_ARCHIVE, EVALUATION_GEN_COMMENT_ARCHIVE</li>
	 * </ul>
	 * @param aoMybatisSession - SqlSession object
	 * @param asProposalId - Proposal id for which review score task is
	 *            finished/scores returned
	 * @param asUserId - user who is doing action on finish button on review
	 *            score task screen
	 * @throws ApplicationException ApplicationException object
	 */
	public void reviewScoreVersionInsert(SqlSession aoMybatisSession, String asProposalId, String asUserId,
			String asStatus) throws ApplicationException
	{
		Map<String, Object> loQueryMap = new HashMap<String, Object>();
		loQueryMap.put(HHSConstants.PROPOSAL_ID_KEY, asProposalId);
		loQueryMap.put(HHSConstants.AS_USER_ID, asUserId);
		try
		{
			String loRequestAmendment = (String) DAOUtil.masterDAO(aoMybatisSession, loQueryMap,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSR5Constants.GET_REQ_AMEND_FLAG_FROM_EVAL_RES,
					HHSConstants.JAVA_UTIL_MAP);
			if (loRequestAmendment == null || loRequestAmendment.equals(HHSConstants.ZERO))
			{
				Integer loVersionRowCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loQueryMap,
						HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.EVALUATION_VERSION_ARCHIVE,
						HHSConstants.JAVA_UTIL_MAP);

				if (null != loVersionRowCount && loVersionRowCount == HHSConstants.INT_ONE)
				{
					DAOUtil.masterDAO(aoMybatisSession, loQueryMap, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
							HHSConstants.EVALUATION_SCORE_ARCHIVE, HHSConstants.JAVA_UTIL_MAP);
					// Start Added in R5
					loQueryMap.put(HHSR5Constants.AS_RETURN_STATUS, (asStatus
							.equalsIgnoreCase(HHSConstants.SCORES_RETURNED)) ? HHSR5Constants.INT_ONE
							: HHSR5Constants.INT_ZERO);
					// End Added in R5
					DAOUtil.masterDAO(aoMybatisSession, loQueryMap, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
							HHSConstants.EVALUATION_GEN_COMMENT_ARCHIVE, HHSConstants.JAVA_UTIL_MAP);
					// Starts R5 : Added to update SCORE_CHANGE_TYPE,
					// COMMENT_CHANGE_FLAG to 0(default)from EVALUATION_SCORE
					if (asStatus != null && asStatus.equalsIgnoreCase(HHSConstants.SCORES_RETURNED))
					{
						DAOUtil.masterDAO(aoMybatisSession, loQueryMap, HHSR5Constants.MAPPER_CLASS_EVALUATION_MAPPER,
								HHSR5Constants.UPDATE_SCORE_CHANGE_TO_DEFAULT, HHSR5Constants.JAVA_UTIL_MAP);
					}
					// ends R5 : Added to update SCORE_CHANGE_TYPE,
					// COMMENT_CHANGE_FLAG to 0(default)from EVALUATION_SCORE
				}
				else
				{
					throw new ApplicationException("Error in creating version History on finish click of Review Task");
				}
			}
			else
			{
				loQueryMap.put(HHSR5Constants.AS_FLAG, HHSConstants.ZERO);
				DAOUtil.masterDAO(aoMybatisSession, loQueryMap, HHSR5Constants.MAPPER_CLASS_EVALUATION_MAPPER,
						HHSR5Constants.SET_REQ_AMEND_FLAG, HHSR5Constants.JAVA_UTIL_MAP);

				DAOUtil.masterDAO(aoMybatisSession, loQueryMap, HHSR5Constants.MAPPER_CLASS_EVALUATION_MAPPER,
						HHSR5Constants.UPDATE_RET_STATUS_FOR_EVAL_GEN_COMMENT_ARCHIVE, HHSR5Constants.JAVA_UTIL_MAP);
			}
			setMoState("reviewScoreVersionInsert passed successfully.");
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoExp)
		{
			setMoState("Error in method reviewScoreVersionInsert for Proposal id::" + asProposalId + " userid::"
					+ asUserId);
			LOG_OBJECT.Error("Error in creating version History on finish click of Review Task", aoExp);
			throw aoExp;
		}
		catch (Exception aoExp)
		{
			setMoState("Error in method reviewScoreVersionInsert for Proposal id::" + asProposalId + " userid::"
					+ asUserId);
			LOG_OBJECT.Error("Error in creating version History on finish click of Review Task", aoExp);
			throw new ApplicationException("Error in creating version History on finish click of Review Task ", aoExp);

		}
	}

	/**
	 * This method will update the proposal status when user selects the unlock
	 * proposal option from Evaluation Status screen
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. Updates proposal status for input parameter using
	 * <b>updateProposalStatus</b> from evaluation mapper</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession Object
	 * @param aoParamMap - Input Parameter Map
	 * @return update status
	 * @throws ApplicationException application Exception Object
	 */
	public Boolean updateProposalStatus(SqlSession aoMybatisSession, Map<String, String> aoParamMap,
			Boolean abStatusFlag) throws ApplicationException
	{
		Boolean loUpdateProposalStatus = false;
		try
		{
			if (abStatusFlag)
			{
				DAOUtil.masterDAO(aoMybatisSession, aoParamMap, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
						HHSConstants.UPDATE_PROPOSAL_STATUS, HHSConstants.JAVA_UTIL_MAP);
				loUpdateProposalStatus = true;
				setMoState("Successfully updates proposal status for proposal id:"
						+ aoParamMap.get(HHSConstants.PROPOSAL_ID));
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(aoParamMap);
			LOG_OBJECT.Error("Exception occured while updating proposal status", aoAppEx);
			setMoState("Exception occured while updating proposal status");
			throw aoAppEx;
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception occured while updating proposal status :", loExp);
			setMoState("Exception occured while updating proposal status :");
			throw new ApplicationException("Exception occured while updating proposal status :", loExp);
		}
		return loUpdateProposalStatus;
	}

	/**
	 * This method is added as a part of enhancement 6577 for Release 3.10.0
	 * This is used to fetch competition title and procureemnt title for the
	 * notification map which is created during cancel competition
	 * @param aoMybatisSession SqlSession
	 * @param aoStatusInfoMap Map<String, String>
	 * @return loModifiedInfoMap HashMap<String, String>
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, String> fetchCompTitleAndProcTitle(SqlSession aoMybatisSession,
			Map<String, String> aoStatusInfoMap) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entering fetchCompTitleAndProcTitle for evaluation pool mapping id::: "
				+ aoStatusInfoMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID));
		HashMap<String, String> loModifiedInfoMap = null;
		try
		{
			loModifiedInfoMap = (HashMap<String, String>) DAOUtil.masterDAO(aoMybatisSession, aoStatusInfoMap,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_COMP_TITLE_AND_PROC_TITLE,
					HHSConstants.JAVA_UTIL_MAP);
			LOG_OBJECT.Debug("Exiting fetchCompTitleAndProcTitle");
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error while fetchCompTitleAndProcTitle", aoExp);
			setMoState("Error while fetchCompTitleAndProcTitle");
			throw aoExp;
		}
		// handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetchCompTitleAndProcTitle", aoExp);
			setMoState("Error while fetchCompTitleAndProcTitle");
			throw new ApplicationException("Error while fetchCompTitleAndProcTitle", aoExp);
		}
		return loModifiedInfoMap;
	}

	/**
	 * This method is added as a part of enhancement 6577 for Release 3.10.0
	 * This is used to fetch provider list for the notification map which is
	 * created during cancel competition
	 * @param aoMybatisSession SqlSession
	 * @param aoStatusInfoMap Map<String, String>
	 * @return loProviderIdList List<String>
	 * @throws ApplicationException
	 */

	@SuppressWarnings("unchecked")
	public List<String> fetchProviderIdList(SqlSession aoMybatisSession, Map<String, String> aoStatusInfoMap)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entering fetchProviderIdList for evaluation pool mapping id::: "
				+ aoStatusInfoMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID));
		List<String> loProviderIdList = null;
		try
		{
			loProviderIdList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, aoStatusInfoMap,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_PROVIDERS_IN_COMPETITION,
					HHSConstants.JAVA_UTIL_MAP);
			LOG_OBJECT.Debug("Exiting fetchProviderIdList");
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error while fetchProviderIdList", aoExp);
			setMoState("Error while fetchProviderIdList");
			throw aoExp;
		}
		// handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetchProviderIdList", aoExp);
			setMoState("Error while fetchProviderIdList");
			throw new ApplicationException("Error while fetchProviderIdList", aoExp);
		}
		return loProviderIdList;
	}

	/**
	 * This method is added as a part of Release 5 This is used to fetch
	 * Document Configuration for Evaluation Setting Screen
	 * @param aoMybatisSession SqlSession
	 * @param asProcurementId String
	 * @param asEvaluationPoolMappingId String
	 * @return List<DocumentVisibility> loDocumentVisibilityList
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<DocumentVisibility> fetchEvaluationDocumentConfiguration(SqlSession aoMybatisSession,
			String asProcurementId, String asEvaluationPoolMappingId) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entering fetchEvaluationDocumentConfiguration");
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSR5Constants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
		loContextDataMap.put(HHSR5Constants.PROCUREMENT_ID, asProcurementId);
		List<DocumentVisibility> loDocumentVisibilityList = null;
		try
		{
			String lsEvalPoolCurrentStatus = (String) DAOUtil.masterDAO(aoMybatisSession, asEvaluationPoolMappingId,
					HHSR5Constants.MAPPER_CLASS_EVALUATION_MAPPER, HHSR5Constants.FETCH_EVAL_POOL_MAPPING_STATUS,
					HHSR5Constants.JAVA_LANG_STRING);
			String lsEvalPoolReleasedStatus = PropertyLoader.getProperty(HHSR5Constants.PROPERTIES_STATUS_CONSTANT,
					HHSR5Constants.STATUS_COMPETITION_POOL_RELEASED);
			HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb
					.getInstance().getCacheObject(ApplicationConstants.APPLICATION_SETTING);
			String lsHiddenDocTypes = loApplicationSettingMap.get(HHSR5Constants.HIDDEN_DOC_TYPE_KEY
					+ HHSR5Constants.UNDERSCORE + HHSR5Constants.HIDDEN_DOC_TYPES);
			if (lsHiddenDocTypes != null)
			{
				String[] loHiddenDocTypes = lsHiddenDocTypes.split(HHSR5Constants.DOUBLE_HHSUTIL_DELIM_PIPE);
				loContextDataMap.put(HHSR5Constants.HIDDEN_DOC_TYPES, Arrays.asList(loHiddenDocTypes));
			}
			if (lsEvalPoolCurrentStatus != null && lsEvalPoolCurrentStatus.equalsIgnoreCase(lsEvalPoolReleasedStatus))
			{
				loDocumentVisibilityList = (List<DocumentVisibility>) DAOUtil.masterDAO(aoMybatisSession,
						loContextDataMap, HHSR5Constants.MAPPER_CLASS_EVALUATION_MAPPER,
						HHSR5Constants.FETCH_DOCUMENT_VISIBILITY_DETAILS, HHSR5Constants.JAVA_UTIL_MAP);
			}
			else
			{
				loDocumentVisibilityList = (List<DocumentVisibility>) DAOUtil.masterDAO(aoMybatisSession,
						loContextDataMap, HHSR5Constants.MAPPER_CLASS_EVALUATION_MAPPER,
						HHSR5Constants.FETCH_DOCUMENT_VISIBILITY_DETAILS_AFTER_RELEASE, HHSR5Constants.JAVA_UTIL_MAP);
			}

			LOG_OBJECT.Debug("Exiting fetchEvaluationDocumentConfiguration");
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error while fetchEvaluationDocumentConfiguration", aoExp);
			setMoState("Error while fetchEvaluationDocumentConfiguration");
			throw aoExp;
		}
		// handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetchEvaluationDocumentConfiguration", aoExp);
			setMoState("Error while fetchEvaluationDocumentConfiguration");
			throw new ApplicationException("Error while fetchEvaluationDocumentConfiguration", aoExp);
		}
		return loDocumentVisibilityList;
	}

	/**
	 * This method is added as a part of Release 5 This is used to save Document
	 * Configuration at Evaluation Setting screen
	 * @param aoMybatisSession SqlSession
	 * @param aoDocumentVisibilityList List<DocumentVisibility>
	 * @param asUserId String
	 * @return boolean lbDocumentVisibilitySaveStatus
	 * @throws ApplicationException
	 */
	public boolean saveEvaluationDocumentConfiguration(SqlSession aoMybatisSession,
			List<DocumentVisibility> aoDocumentVisibilityList, String asUserId) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entering saveEvaluationDocumentConfiguration");
		boolean lbDocumentVisibilitySaveStatus = false;
		try
		{
			if (aoDocumentVisibilityList != null && !aoDocumentVisibilityList.isEmpty())
			{
				for (DocumentVisibility loDocumentVisibility : aoDocumentVisibilityList)
				{
					loDocumentVisibility.setUserId(asUserId);
					if (loDocumentVisibility.getDocumentVisibilityId() != null
							&& !loDocumentVisibility.getDocumentVisibilityId().isEmpty())
					{
						DAOUtil.masterDAO(aoMybatisSession, loDocumentVisibility,
								HHSR5Constants.MAPPER_CLASS_EVALUATION_MAPPER,
								HHSR5Constants.UPDATE_DOCUMENT_VISIBILITY, HHSR5Constants.DOCUMENT_VISIBILITY_BEAN);
					}
					else
					{
						DAOUtil.masterDAO(aoMybatisSession, loDocumentVisibility,
								HHSR5Constants.MAPPER_CLASS_EVALUATION_MAPPER,
								HHSR5Constants.INSERT_DOCUMENT_VISIBILITY, HHSR5Constants.DOCUMENT_VISIBILITY_BEAN);
					}
				}
				lbDocumentVisibilitySaveStatus = true;
			}
			LOG_OBJECT.Debug("Exiting saveEvaluationDocumentConfiguration");
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error while saveEvaluationDocumentConfiguration", aoExp);
			setMoState("Error while saveEvaluationDocumentConfiguration");
			throw aoExp;
		}
		// handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while saveEvaluationDocumentConfiguration", aoExp);
			setMoState("Error while saveEvaluationDocumentConfiguration");
			throw new ApplicationException("Error while saveEvaluationDocumentConfiguration", aoExp);
		}
		return lbDocumentVisibilitySaveStatus;
	}

	/**
	 * This method is added as a part of Release 5 This is used to fetch
	 * Evaluation Round Score Details using evaluation status Id only
	 * @param aoMybatisSession SqlSession
	 * @param aoTaskMap HashMap<String, Object>
	 * @param asWobNumber String
	 * @return loEvaluationScoreList List<EvaluationBean>
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked" })
	public List<EvaluationBean> fetchEvaluationRoundScoreDetails(SqlSession aoMyBatisSession,
			HashMap<String, Object> aoTaskMap, String asWobNumber) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entering fetchEvaluationRoundScoreDetails");
		List<EvaluationBean> loEvaluationScoreList = null;
		try
		{
			Map<String, Object> loProcurementMap = (Map<String, Object>) aoTaskMap.get(asWobNumber);
			if (null != loProcurementMap)
			{
				loEvaluationScoreList = (List<EvaluationBean>) DAOUtil.masterDAO(aoMyBatisSession, loProcurementMap,
						HHSR5Constants.MAPPER_CLASS_EVALUATION_MAPPER,
						HHSR5Constants.FETCH_EVALUATION_ROUND_SCORE_DETAILS, HHSR5Constants.JAVA_UTIL_MAP);
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while fetchEvaluationRoundScoreDetails", aoAppEx);
			setMoState("Error while fetchEvaluationRoundScoreDetails");
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetchEvaluationRoundScoreDetails", aoExp);
			setMoState("Error while fetchEvaluationRoundScoreDetails");
			throw new ApplicationException("Error while fetchEvaluationRoundScoreDetails", aoExp);
		}
		return loEvaluationScoreList;
	}

	/**
	 * This method is added as a part of Release 5 This is used to fetch Round
	 * Drop down Details using evaluationStatusId Only
	 * @param aoMybatisSession SqlSession
	 * @param aoTaskMap HashMap<String, Object>
	 * @param String asWobNumber
	 * @return loEvaluationScoreList List<EvaluationBean>
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public List<EvaluationBean> fetchRoundDropdownDetails(SqlSession aoMyBatisSession,
			HashMap<String, Object> aoTaskMap, String asWobNumber) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entering fetchRoundDropdownDetails");
		List<EvaluationBean> loEvaluationScoreList = null;
		try
		{
			Map loProcurementMap = (Map<String, Object>) aoTaskMap.get(asWobNumber);
			if (null != loProcurementMap)
			{
				loEvaluationScoreList = (List<EvaluationBean>) DAOUtil.masterDAO(aoMyBatisSession, loProcurementMap,
						HHSR5Constants.MAPPER_CLASS_EVALUATION_MAPPER, HHSR5Constants.FETCH_ROUND_DROPDOWN_DETAILS,
						HHSR5Constants.JAVA_UTIL_MAP);
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while fetchRoundDropdownDetails", aoAppEx);
			setMoState("Error while fetchRoundDropdownDetails");
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetchRoundDropdownDetails", aoExp);
			setMoState("Error while fetchRoundDropdownDetails");
			throw new ApplicationException("Error while fetchRoundDropdownDetails", aoExp);
		}
		return loEvaluationScoreList;
	}

	/**
	 * This method is added as a part of Release 5 This is used to fetch
	 * Evaluation Score on Select a Round from drop down
	 * @param aoMybatisSession SqlSession
	 * @param aoQueryMap Map<String, String>
	 * @return loEvaluationScoreList List<EvaluationBean>
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<EvaluationBean> fetchEvalScoreOfSelectRound(SqlSession aoMyBatisSession, Map<String, String> aoQueryMap)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entering fetchEvalScoreOfSelectRound");
		List<EvaluationBean> loEvaluationScoreList = null;
		try
		{
			loEvaluationScoreList = (List<EvaluationBean>) DAOUtil.masterDAO(aoMyBatisSession, aoQueryMap,
					HHSR5Constants.MAPPER_CLASS_EVALUATION_MAPPER, HHSR5Constants.FETCH_EVAL_SCORE_OF_SELECT_ROUND,
					HHSR5Constants.JAVA_UTIL_MAP);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while fetchEvalScoreOfSelectRound", aoAppEx);
			setMoState("Error while fetchEvalScoreOfSelectRound");
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetchEvalScoreOfSelectRound", aoExp);
			setMoState("Error while fetchEvalScoreOfSelectRound");
			throw new ApplicationException("Error while fetchEvalScoreOfSelectRound", aoExp);
		}
		return loEvaluationScoreList;
	}

	/**
	 * This method is added as a part of Release 5 This is used to fetch
	 * Evaluation Round(dopdownlist) Details
	 * @param aoMybatisSession SqlSession
	 * @param aoTaskMap Map<String, Object>
	 * @return loEvaluationScoreList List<EvaluationBean>
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked" })
	public List<EvaluationBean> fetchEvalRoundDetails(SqlSession aoMyBatisSession, Map<String, Object> aoTaskMap)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entering fetchEvalRoundDetails");
		List<EvaluationBean> loEvaluationScoreList = null;
		try
		{
			loEvaluationScoreList = (List<EvaluationBean>) DAOUtil.masterDAO(aoMyBatisSession, aoTaskMap,
					HHSR5Constants.MAPPER_CLASS_EVALUATION_MAPPER, HHSR5Constants.FETCH_ALL_ROUND_DROP_DOWN_DETAILS,
					HHSR5Constants.JAVA_UTIL_MAP);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while fetchEvalRoundDetails", aoAppEx);
			setMoState("Error while fetchEvalRoundDetails");
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetchEvalRoundDetails", aoExp);
			setMoState("Error while fetchEvalRoundDetails");
			throw new ApplicationException("Error while fetchEvalRoundDetails", aoExp);
		}
		return loEvaluationScoreList;
	}

	/**
	 * This method is added as a part of Release 5 This is used to fetch
	 * Evaluation Score Details
	 * @param aoMybatisSession SqlSession
	 * @param aoQueryMap Map<String, String>
	 * @param aoRoundList List<EvaluationBean>
	 * @return loEvaluationScoreList List<EvaluationBean>
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<EvaluationBean> fetchEvaluationScoreDetails(SqlSession aoMyBatisSession,
			Map<String, String> aoQueryMap, List<EvaluationBean> aoRoundList) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entering fetchEvaluationScoreDetails");
		List<EvaluationBean> loEvaluationScoreList = null;
		try
		{
			if (!aoRoundList.isEmpty())
			{
				aoQueryMap.put(HHSR5Constants.VERSION_NUMBER, aoRoundList.get(HHSR5Constants.INT_ZERO)
						.getVersionNumber());
			}
			else
			{
				aoQueryMap.put(HHSR5Constants.VERSION_NUMBER, HHSR5Constants.ONE);
			}

			Integer loCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoQueryMap,
					HHSR5Constants.MAPPER_CLASS_EVALUATION_MAPPER, HHSR5Constants.FETCH_COUNT_EVALUATION_SCORE_ARCHIVE,
					HHSR5Constants.JAVA_UTIL_MAP);
			if (loCount > HHSR5Constants.INT_ONE)
			{
				loEvaluationScoreList = (List<EvaluationBean>) DAOUtil.masterDAO(aoMyBatisSession, aoQueryMap,
						HHSR5Constants.MAPPER_CLASS_EVALUATION_MAPPER, HHSR5Constants.FETCH_EVAL_SCORE_OF_SELECT_ROUND,
						HHSR5Constants.JAVA_UTIL_MAP);
			}
			else
			{
				loEvaluationScoreList = (List<EvaluationBean>) DAOUtil.masterDAO(aoMyBatisSession, aoQueryMap,
						HHSR5Constants.MAPPER_CLASS_EVALUATION_MAPPER, HHSR5Constants.FETCH_LATEST_EVALUATION_SCORE,
						HHSR5Constants.JAVA_UTIL_MAP);
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while fetchEvaluationScoreDetails", aoAppEx);
			setMoState("Error while fetchEvaluationScoreDetails");
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetchEvaluationScoreDetails", aoExp);
			setMoState("Error while fetchEvaluationScoreDetails");
			throw new ApplicationException("Error while fetchEvaluationScoreDetails", aoExp);
		}
		return loEvaluationScoreList;
	}

	/**
	 * This method is added as a part of Release 5 This is used to fetch
	 * AWARD_REVIEW_STATUS_ID w.r.t. procourementId for Evaluation Setting
	 * Screen. If AWARD_REVIEW_STATUS_ID is null or returned or in_review then
	 * return true
	 * @param aoMybatisSession SqlSession
	 * @param asProcurementId String
	 * @param asEvaluationPoolMappingId String
	 * @return loAwardReviewStatus Boolean
	 * @throws ApplicationException
	 */
	public Boolean checkAwardStatusForEvaluationSetting(SqlSession aoMyBatisSession, String asProcurementId,
			String asEvaluationPoolMappingId) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entering checkAwardStatusForEvaluationSetting");
		Boolean lsAwardReviewStatus = HHSR5Constants.BOOLEAN_FALSE;
		try
		{
			HashMap<String, String> loParam = new HashMap<String, String>();
			loParam.put(HHSR5Constants.PROCUREMENT_ID, asProcurementId);
			loParam.put(HHSR5Constants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
			String loAwardReviewStatusId = (String) DAOUtil.masterDAO(aoMyBatisSession, loParam,
					HHSR5Constants.MAPPER_CLASS_EVALUATION_MAPPER,
					HHSR5Constants.GET_AWARD_STATUS_ID_FOR_EVALUATION_SETTING, HHSR5Constants.JAVA_UTIL_HASH_MAP);
			if (null == loAwardReviewStatusId || loAwardReviewStatusId.equals(HHSR5Constants.STATUS_THIRTY_THREE)
					|| loAwardReviewStatusId.equals(HHSR5Constants.STATUS_THIRTY_FOUR))
			{
				lsAwardReviewStatus = HHSR5Constants.BOOLEAN_TRUE;
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while checkAwardStatusForEvaluationSetting", aoAppEx);
			setMoState("Error while checkAwardStatusForEvaluationSetting");
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while checkAwardStatusForEvaluationSetting", aoExp);
			setMoState("Error while checkAwardStatusForEvaluationSetting");
			throw new ApplicationException("Error while checkAwardStatusForEvaluationSetting", aoExp);
		}
		return lsAwardReviewStatus;
	}

	/**
	 * This method added as a part of release 5 for Award Negotiation Module.
	 * This method will get the details bean of the Evaluations for the specific
	 * ProcurementId and evaluation pool mapping Id for Finalize Award Screen
	 * <ul>
	 * <li>Get the procurement Id and evaluation pool mapping Id from map</li>
	 * <li>Execute select query <b>fetchFinalizeAwardScores</b> from proposal
	 * mapper to get evaluation results and scores details</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession Object
	 * @param aoTaskDetailMap a map containing procurement and task details
	 * @param asWobNumber a string value of wob number
	 * @return list of EvaluationBean
	 * @throws ApplicationException application Exception Object
	 */
	@SuppressWarnings("unchecked")
	public Map fetchFinalizeAwardResultsScores(SqlSession aoMybatisSession, HashMap<String, Object> aoTaskDetailMap,
			String asWobNumber) throws ApplicationException
	{
		List<EvaluationBean> loEvaluationDetailsList = null;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		Map loResultMap = new HashMap();
		loContextDataMap.put(HHSConstants.WOB_NUMBER, asWobNumber);
		LOG_OBJECT.Debug("Entered into fetchFinalizeAwardResultsScores for wob number:" + loContextDataMap.toString());
		try
		{
			if (null != aoTaskDetailMap)
			{
				HashMap<String, Object> loTaskMap = (HashMap<String, Object>) aoTaskDetailMap.get(asWobNumber);

				if (null != loTaskMap)
				{
					loEvaluationDetailsList = (List<EvaluationBean>) DAOUtil.masterDAO(aoMybatisSession, loTaskMap,
							HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSR5Constants.FETCH_FINALIZE_AWARD_SCORES,
							HHSConstants.JAVA_UTIL_HASH_MAP);
					loResultMap.put(HHSR5Constants.FINALIZE_SAME_PROVIDER_LIST, loEvaluationDetailsList);
					loEvaluationDetailsList = (List<EvaluationBean>) DAOUtil.masterDAO(aoMybatisSession, loTaskMap,
							HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSR5Constants.FETCH_FINALIZE_OTHER_SCORES,
							HHSConstants.JAVA_UTIL_HASH_MAP);
					loResultMap.put(HHSR5Constants.FINALIZE_OTHER_PROVIDER_LIST, loEvaluationDetailsList);
					setMoState("Evaluation Results & Scores Fetched successfully for ProcurementId:"
							+ loTaskMap.get(P8Constants.PE_WORKFLOW_PROCUREMENT_ID));
				}
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(aoTaskDetailMap);
			LOG_OBJECT.Error("Error occurred while fething evaluation Results & Scores for input", aoAppEx);
			setMoState("Error occurred while fething evaluation Results & Scores");
			throw aoAppEx;
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occurred while fething evaluation Results & Scores for input", aoEx);
			setMoState("Error occurred while fething evaluation Results & Scores");
			throw new ApplicationException("Error occurred while fething evaluation Results & Scores for input", aoEx);
		}
		LOG_OBJECT.Debug("Exited fetchFinalizeAwardResultsScores for wob number:" + loContextDataMap.toString());
		return loResultMap;
	}

	/**
	 * This method added as a part of release 5 for Award Negotiation Module.
	 * 
	 * <ul>
	 * <li>Get the award hashmap from input params</li>
	 * <li>Execute query with id "updateEvaluationNegotiationStatus" from awards
	 * mapper</li>
	 * <li>Return updated status to controller</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession sql session object
	 * @param aoAwardMap a hashmap containing award details
	 * @return loUpdateStatus - a boolean value of award update status
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Boolean updateEvaluationNegotiationStatus(SqlSession aoMybatisSession, Map<String, String> aoEvalMap)
			throws ApplicationException
	{
		Boolean loUpdateStatus = false;
		try
		{
			if (null != aoEvalMap)
			{
				DAOUtil.masterDAO(aoMybatisSession, aoEvalMap, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
						HHSR5Constants.UPDATE_NEGOTIATION_STATUS, HHSConstants.JAVA_UTIL_HASH_MAP);
				loUpdateStatus = true;
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException loExp)
		{
			setMoState("Error while updating Evaluation details");
			loExp.setContextData(aoEvalMap);
			LOG_OBJECT.Error("Error while updating Evaluation details", loExp);
			throw loExp;
		}
		// handling exception other than Application Exception.
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Error while updating Evaluation details", loEx);
			setMoState("Error while updating Evaluation details");
			throw new ApplicationException("Error while updating Evaluation details", loEx);
		}
		return loUpdateStatus;
	}

	/**
	 * This method added as a part of release 5 for Award Negotiation Module
	 * 
	 * <ul>
	 * This method set the column : PRESERVED_AMOUNT of EvaluationResults table
	 * </ul>
	 * @param aoMybatisSession SqlSession
	 * @param aoMap Map containing data
	 * @return lbUpdateStatus boolean
	 * @throws ApplicationException
	 */
	public Boolean setNegotiationEvaluationAmount(SqlSession aoMybatisSession, Map aoMap) throws ApplicationException
	{
		LOG_OBJECT.Info("Entering setNegotiationEvaluationAmount");
		Boolean loUpdateStatus = Boolean.FALSE;
		String lsEvaluationPoolMappingId = (String) aoMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID);
		String lsNegotiationFlag = (String) aoMap.get(HHSR5Constants.IS_NEGOTIATION_REQUIRED);
		try
		{
			if (null != lsNegotiationFlag && lsNegotiationFlag.equalsIgnoreCase(HHSConstants.TRUE))
			{
				Integer loUpdateCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, lsEvaluationPoolMappingId,
						HHSR5Constants.MAPPER_CLASS_EVALUATION_MAPPER,
						HHSR5Constants.SET_NEGOTIATION_EVALUATION_AMOUNT, HHSR5Constants.JAVA_LANG_STRING);
				if (loUpdateCount > HHSR5Constants.INT_ZERO)
				{
					loUpdateStatus = Boolean.TRUE;
				}
			}
		}
		// catch any application exception thrown from the code and throw it
		// forward
		catch (ApplicationException loExp)
		{
			setMoState("Error occurred while setNegotiationEvaluationAmount");
			LOG_OBJECT.Error("Error occurred setNegotiationEvaluationAmount", loExp);
			throw loExp;
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception loAppEx)
		{
			setMoState("Error occurred while setNegotiationEvaluationAmount");
			LOG_OBJECT.Error("Error occurred while setNegotiationEvaluationAmount", loAppEx);
			throw new ApplicationException("Error occurred while setNegotiationEvaluationAmount", loAppEx);

		}
		LOG_OBJECT.Info("Exited setNegotiationEvaluationAmount");
		return loUpdateStatus;
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
	public Boolean clearPreviousFinalizedAmount(SqlSession aoMybatisSession, Map<String, String> aoAwardMap)
			throws ApplicationException
	{
		Boolean loClearStatus = false;
		try
		{
			DAOUtil.masterDAO(aoMybatisSession, aoAwardMap, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
					HHSR5Constants.CLEAR_NEGOTIATION_DATA, HHSConstants.JAVA_UTIL_HASH_MAP);
			loClearStatus = true;

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
		return loClearStatus;
	}

	/**
	 * This method is part of Release 5 Request Amend. This method will update
	 * the REQUEST_AMENDMENT_FLAG flag for EVALUATION_RESULTS.
	 * 
	 * <ul>
	 * <li>Get the proposalId from input params</li>
	 * <li>Execute query with id "setRequestAmendmentFlag" from Evaluation
	 * mapper</li>
	 * <li>RequestAmendmentFlag status to controller</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession sql session object
	 * @param asProposalId a String containing proposal Id
	 * @return loRequestAmendmentFlag - a boolean value of RequestAmendmentFlag
	 *         status
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings("unchecked")
	public Boolean setRequestAmendmentFlag(SqlSession aoMybatisSession, String asProposalId)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into method: setRequestAmendmentFlag");
		Boolean loRequestAmendmentFlag = false;
		if (asProposalId != null)
		{
			try
			{
				Map<String, Object> loQueryMap = new HashMap<String, Object>();
				loQueryMap.put(HHSConstants.PROPOSAL_ID_KEY, asProposalId);
				loQueryMap.put(HHSR5Constants.AS_FLAG, HHSConstants.ONE);
				Integer loUpdateCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loQueryMap,
						HHSR5Constants.MAPPER_CLASS_EVALUATION_MAPPER, HHSR5Constants.SET_REQ_AMEND_FLAG,
						HHSR5Constants.JAVA_UTIL_MAP);
				List<String> loEvalStatusList = (List<String>) DAOUtil.masterDAO(aoMybatisSession,
						loQueryMap.get(HHSConstants.PROPOSAL_ID_KEY), HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
						HHSR5Constants.GET_EVAL_STATUS_LIST, HHSConstants.JAVA_LANG_STRING);
				loQueryMap.put(HHSConstants.EVAL_STATUS_LIST, loEvalStatusList);
				loQueryMap.put(HHSR5Constants.AS_FLAG, HHSConstants.ZERO);
				DAOUtil.masterDAO(aoMybatisSession, loQueryMap, HHSR5Constants.MAPPER_CLASS_EVALUATION_MAPPER,
						HHSR5Constants.SET_RETURN_STATUS_EVAL_GEN_ARCHIVE, HHSR5Constants.JAVA_UTIL_MAP);
				if (loUpdateCount > HHSR5Constants.INT_ZERO)
				{
					loRequestAmendmentFlag = Boolean.TRUE;
				}
				setMoState("RequestAmendmentFlag updated to one, successfully corresponding to proposal Id");
			}
			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handles over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */
			catch (ApplicationException aoAppEx)
			{
				LOG_OBJECT.Error("Exception occured while updating RequestAmendmentFlag corresponding to proposal Id",
						aoAppEx);
				setMoState("Transaction Failed:: EvaluationService:setRequestAmendmentFlag method - while updating RequestAmendmentFlag corresponding to proposal Id");
				throw aoAppEx;
			}
			// Handling Exception other than ApplicationException
			catch (Exception aoAppEx)
			{
				LOG_OBJECT.Error("Exception occured while updating RequestAmendmentFlag corresponding to proposal Id",
						aoAppEx);
				setMoState("Transaction Failed:: EvaluationService:setRequestAmendmentFlag method - while updating RequestAmendmentFlag corresponding to proposal Id");
				throw new ApplicationException(
						"Exception occured while updating RequestAmendmentFlag corresponding to proposal Id", aoAppEx);
			}
		}
		return loRequestAmendmentFlag;
	}

	/**
	 * This method is part of Release 5, fetch the Request Amend Flag on Review
	 * Score
	 * 
	 * <ul>
	 * <li>1. Check if loProcurementMap is not null</li>
	 * <li>2. extract lsProposalId from loProcurementMap using asWobNumber and
	 * fetch using id "getRequestAmendmentFlagFromEvaluationResult" with
	 * EvaluationBean as a parameter.</li>
	 * </ul>
	 * @param aoMybatisSession - My batis session
	 * @param aoHmRequiredProps - HashMap
	 * @param asWobNumber - wobnumber
	 * @return String String
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("rawtypes")
	public String fetchRequestAmendFlag(SqlSession aoMyBatisSession, HashMap aoHmRequiredProps, String asWobNumber)
			throws ApplicationException
	{
		String loRequestAmendFlag = null;
		String lsProposalId = null;
		if (aoHmRequiredProps != null)
		{
			try
			{
				HashMap loProcurementMap = (HashMap) aoHmRequiredProps.get(asWobNumber);
				if (null != loProcurementMap)
				{
					lsProposalId = (String) loProcurementMap.get(P8Constants.PE_WORKFLOW_PROPOSAL_ID);
					Map<String, Object> loQueryMap = new HashMap<String, Object>();
					loQueryMap.put(HHSConstants.PROPOSAL_ID_KEY, lsProposalId);
					loRequestAmendFlag = (String) DAOUtil.masterDAO(aoMyBatisSession, loQueryMap,
							HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
							HHSR5Constants.GET_REQ_AMEND_FLAG_FROM_EVAL_RES, HHSConstants.JAVA_UTIL_MAP);
					setMoState("Fetch Amend Request Flag using Proposal Id");
				}
			}
			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handles over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */
			catch (ApplicationException aoAppEx)
			{
				LOG_OBJECT.Error("Exception occured while Fetch Amend Request Flag corresponding to the Proposal Id",
						aoAppEx);
				setMoState("Transaction Failed:: EvaluationService:fetchRequestAmendFlag method - Fetch Amend Request Flag corresponding to the Proposal Id");
				throw aoAppEx;
			}
			/**
			 * Any Exception from DAO class will be handles over here. It throws
			 * Application Exception back to Controllers calling method through
			 * Transaction framework
			 */
			catch (Exception aoAppEx)
			{
				LOG_OBJECT.Error("Exception occured while Fetch Amend Request Flag corresponding to the Proposal Id",
						aoAppEx);
				setMoState("Transaction Failed:: EvaluationService:fetchRequestAmendFlag method - Fetch Amend Request Flag corresponding to the Proposal Id");
				throw new ApplicationException(
						"Exception occured while Fetch Amend Request Flag corresponding to the Proposal Id", aoAppEx);
			}
		}
		return loRequestAmendFlag;
	}
	/**
	 * This method is added in R5 for defect 8363.It checks 
	 * if the award is approved with/without financials.
	 * <ul>
	 * <li>1.Get the EvaluationPoolMAppingId</li>
	 * <li>2.Execute query <code>checkApprovalForFinance</code></li>
	 * <li>Return the count</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession
	 * @param asEvaluationPoolMappingId procurement id
	 * @return liCount Integer Count
	 * @throws ApplicationException if application occurs
	 */
	public Integer checkApprovalWithFinancials(SqlSession aoMybatisSession, String asEvaluationPoolMappingId)
			throws ApplicationException
	{
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
		Integer liCount = 0;
		try
		{
			liCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, asEvaluationPoolMappingId,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSR5Constants.CHECK_APPROVAL_FINANCE,
					HHSConstants.JAVA_LANG_STRING);
			if (liCount > 0)
			{
				liCount = 1;
			}
		}

		// Catch application exception thrown from the code
		// and throw it forward
		catch (ApplicationException aoEx)
		{
			setMoState("Error while checkApprovalWithFinancials");
			aoEx.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while checkApprovalWithFinancials", aoEx);
			throw aoEx;
		}
		catch (Exception loEx)
		{
			setMoState("Error while checkApprovalWithFinancials");
			LOG_OBJECT.Error("Error while checkApprovalWithFinancials", loEx);
			throw new ApplicationException("Error while checkApprovalWithFinancials", loEx);
		}
		return liCount;
	}
}