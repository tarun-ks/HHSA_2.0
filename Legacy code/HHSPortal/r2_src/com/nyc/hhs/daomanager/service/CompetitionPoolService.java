package com.nyc.hhs.daomanager.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.AwardsContractSummaryBean;
import com.nyc.hhs.model.EvaluationGroupAwardBean;
import com.nyc.hhs.model.EvaluationGroupsProposalBean;
import com.nyc.hhs.model.EvaluationSummaryBean;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.Procurement;
import com.nyc.hhs.model.RFPReleaseBean;
import com.nyc.hhs.model.SelectionDetailsSummaryBean;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * <ul>
 * <li><b>This Class has been added in R4</b></li>
 * </ul>
 * 
 */
public class CompetitionPoolService extends ServiceState
{
	/**
	 * This is a log object used to log any exception into log file.
	 * 
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(CompetitionPoolService.class);

	/**
	 * <p>
	 * This method saves competition pool corresponding to a procurement id and
	 * user id
	 * <ul>
	 * <li>1. Add multiple input parameters to a Map</li>
	 * <li>2. Deletes competition pool not available in map corresponding to a
	 * procurement id using <b>deleteRemoveCompetitionPool</b> from procurement
	 * mapper</li>
	 * <li>3. Iterate through the selected competition pool list and check if it
	 * is already added or not using <b>checkCompetitionPoolExists</b> from
	 * procurement mapper</li>
	 * <li>4. If competition pool doesan't exist, Saves competition pool
	 * corresponding to a procurement id and user id using
	 * <b>insertNewCompetitionPool</b> from procurement mapper</li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProcurementId - Procurement Id
	 * @param asUserId - User Id
	 * @param aoSelectedPool - Selected Pool
	 * @return loSaveSuccessfull - flag depicting save was successful
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean saveCompetitionPool(SqlSession aoMybatisSession, String asProcurementId, String asUserId,
			List<String> aoSelectedPool) throws ApplicationException
	{
		Boolean loSaveSuccessfull = false;
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		loContextDataMap.put(HHSConstants.SELECTED_POOL, aoSelectedPool);
		loContextDataMap.put(HHSConstants.AS_USER_ID, asUserId);
		LOG_OBJECT.Debug("Entered into saveCompetitionPool for Procurement Id:" + asProcurementId);
		try
		{
			if (asProcurementId != null && aoSelectedPool != null && !aoSelectedPool.isEmpty())
			{				
				/*[Start] R6.3 QC 6627 commented out
				DAOUtil.masterDAO(aoMybatisSession, loContextDataMap, HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
						HHSConstants.DELETE_REMOVE_COMP_POOL, HHSConstants.JAVA_UTIL_MAP);
						[End] R6.3 QC 6627 commented out */
				for (String lsSelectedPool : aoSelectedPool)
				{
					loContextDataMap.put(HHSConstants.LS_SELECTED_POOL, lsSelectedPool);
					DAOUtil.masterDAO(aoMybatisSession, loContextDataMap, HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
							HHSConstants.INSERT_NEW_COMP_POOL_NOT_EXIST, HHSConstants.JAVA_UTIL_MAP);
				}
				//[Start] R6.3 QC 6627 added Deleting Empty Competition pool
				DAOUtil.masterDAO(aoMybatisSession, loContextDataMap, HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
						HHSConstants.DELETE_COMP_POOLS_ADDENDUM, HHSConstants.JAVA_UTIL_MAP);
				//[End] R6.3 QC 6627 added  Deleting Empty Competition pool

				loSaveSuccessfull = true;
			}
			else if ((null == aoSelectedPool || aoSelectedPool.isEmpty()) && asProcurementId != null)
			{
				loContextDataMap.put(HHSConstants.SELECTED_POOL, null);
				DAOUtil.masterDAO(aoMybatisSession, loContextDataMap, HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
						HHSConstants.DELETE_COMP_POOLS_ADDENDUM, HHSConstants.JAVA_UTIL_MAP);
			}
			setMoState("Successfully Saves Competition Pool for Procurement Id:" + asProcurementId);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error while saving connection pool data for Procurement Id:" + asProcurementId);
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while saving connection pool data  for Procurement Id:" + asProcurementId);
			throw loExp;
		}
		catch (Exception loExp)
		{
			setMoState("Error while saving connection pool data for Procurement Id:" + asProcurementId);
			LOG_OBJECT.Error("Error while saving connection pool data  for Procurement Id:" + asProcurementId);
			throw new ApplicationException("Error while saving connection pool data for Procurement Id:"
					+ asProcurementId, loExp);
		}
		return loSaveSuccessfull;
	}

	/**
	 * <p>
	 * This method fetches competition pools corresponding to a procurement id
	 * <ul>
	 * <li>1. Add input parameters to a Map</li>
	 * <li>2. fetches competition pools corresponding to a procurement id using
	 * <b>fetchCompetitionPoolData</b> from Procurement mapper</li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProcurementId - Procurement Id
	 * @return loSelectedPool - list of competition pools
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<String> getCompetitionPoolData(SqlSession aoMybatisSession, String asProcurementId)
			throws ApplicationException
	{
		List<String> loSelectedPool = null;
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		LOG_OBJECT.Debug("Entered into getCompetitionPoolData for Procurement Id:" + asProcurementId);
		try
		{
			if (asProcurementId != null)
			{
				loSelectedPool = (List<String>) DAOUtil.masterDAO(aoMybatisSession, asProcurementId,
						HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.FETCH_COMP_POOL_DATA,
						HHSConstants.JAVA_LANG_STRING);
			}
			setMoState("Successfully fetch Competition Pool list for Procurement Id:" + asProcurementId);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error while fetching connection pool data for Procurement Id:" + asProcurementId);
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching connection pool data  for Procurement Id:" + asProcurementId);
			throw loExp;
		}
		catch (Exception loExp)
		{
			setMoState("Error while fetching connection pool data for Procurement Id:" + asProcurementId);
			LOG_OBJECT.Error("Error while fetching connection pool data  for Procurement Id:" + asProcurementId);
			throw new ApplicationException("Error while fetching connection pool data for Procurement Id:"
					+ asProcurementId, loExp);
		}
		return loSelectedPool;
	}

	/**
	 * <p>
	 * This method fetches list of all competition pools along with their ids
	 * associated with a procurement
	 * <ul>
	 * <li>1. Add input parameters to a Map</li>
	 * <li>2. Fetches list of competition pools associated with the procurement
	 * for a given procurement id by executing query
	 * <b>fetchAllCompetitionPool</b> from ProposalMapper</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProcurementId - Procurement Id
	 * @return loCompetitionPoolList - list of map of Competition Pools
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, String>> fetchAllCompetitionPools(SqlSession aoMybatisSession, String asProcurementId)
			throws ApplicationException
	{
		List<Map<String, String>> loCompetitionPoolList = null;
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID, asProcurementId);
		LOG_OBJECT.Debug("Entered into fetchAllCompetitionPools for Procurement Id:" + asProcurementId);
		try
		{
			if (asProcurementId != null)
			{
				loCompetitionPoolList = (List<Map<String, String>>) DAOUtil.masterDAO(aoMybatisSession,
						asProcurementId, HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER,
						HHSConstants.FETCH_ALL_COMPETITION_POOL, HHSConstants.JAVA_LANG_STRING);
				setMoState("Successfully fetched all Competition Pool for procurement id :" + asProcurementId);
			}
			else
			{
				throw new ApplicationException("Procurement Id cannot be null while fetching Competition Pool");
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error while fetching all Competition Pool for procurement id :" + asProcurementId);
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching all Competition Pool for procurement id :", loExp);
			throw loExp;
		}
		catch (Exception loExp)
		{
			setMoState("Error while fetching all Competition Pool for procurement id :" + asProcurementId);
			LOG_OBJECT.Error("Error while fetching all Competition Pool for procurement id :", loExp);
			throw new ApplicationException("Error while fetching all Competition Pool for procurement id :"
					+ asProcurementId, loExp);
		}
		return loCompetitionPoolList;
	}

	
    /**
     * <p>
     * This method fetches list of all competition pools along with their ids
     * associated with a procurement
     * <ul>
     * <li>1. Add input parameters to a Map</li>
     * <li>2. Fetches list of competition pools associated with the procurement
     * for a given procurement id by executing query
     * <b>fetchAllCompetitionPool</b> from ProposalMapper</li>
     * </ul>
     * </p>
     * 
     * @param aoMybatisSession - mybatis SQL session
     * @param asProcurementId - Procurement Id
     * @return loCompetitionPoolList - list of map of Competition Pools
     * @throws ApplicationException If an ApplicationException occurs
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, String>> fetchAllCompetitionPoolWithProposalId(SqlSession aoMybatisSession, String asProcurementId, String asProposalId)
            throws ApplicationException
    {
        List<Map<String, String>> loCompetitionPoolList = null;
        Map<String, Object> loContextDataMap = new HashMap<String, Object>();
        loContextDataMap.put(HHSConstants.PROCUREMENT_ID, asProcurementId);
        
        Map<String, Object> loParamMap = new HashMap<String, Object>();
        loParamMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId); 
        loParamMap.put(HHSConstants.PROPOSAL_ID_KEY, asProposalId);
        LOG_OBJECT.Debug("Entered into fetchAllCompetitionPools for Procurement Id & Proposal Id:" + asProcurementId + " :: " + asProposalId);
        try
        {
            if (asProcurementId != null)
            {
                loCompetitionPoolList = (List<Map<String, String>>) DAOUtil.masterDAO(aoMybatisSession,
                        loParamMap, HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER,
                        HHSConstants.FETCH_ALL_COMPETITION_POOL_FLAG, HHSConstants.JAVA_UTIL_HASH_MAP);
                setMoState("Successfully fetched all Competition Pool for Procurement Id & Proposal Id:" + asProcurementId + " :: " + asProposalId);
            }
            else
            {
                throw new ApplicationException("Procurement Id cannot be null while fetching Competition Pool");
            }
        }
        /**
         * Any Exception from DAO class will be thrown as Application Exception
         * which will be handles over here. It throws Application Exception back
         * to Controllers calling method through Transaction framework
         */
        catch (ApplicationException loExp)
        {
            setMoState("Error while fetching all Competition Pool for procurement id :" + asProcurementId);
            loExp.setContextData(loContextDataMap);
            LOG_OBJECT.Error("Error while fetching all Competition Pool for procurement id :", loExp);
            throw loExp;
        }
        catch (Exception loExp)
        {
            setMoState("Error while fetching all Competition Pool for procurement id :" + asProcurementId);
            LOG_OBJECT.Error("Error while fetching all Competition Pool for procurement id :", loExp);
            throw new ApplicationException("Error while fetching all Competition Pool for procurement id :"
                    + asProcurementId, loExp);
        }
        return loCompetitionPoolList;
    }
	
	
	/**
	 * <p>
	 * This method fetches proposals and evaluations summary for a given
	 * procurement id or evaluation group id
	 * <ul>
	 * <li>1. Add multiple input parameters to a Map</li>
	 * <li>2. Fetches evaluation summary corresponding to a procurement id and
	 * evaluation group id using <b>fetchEvaluationSummary</b> from evaluation
	 * mapper</li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoEvaluationSummaryBean - EvaluationSummaryBean properties
	 * @return loEvaluationSummaryList - list of evaluation summary
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<EvaluationSummaryBean> fetchEvaluationSummary(SqlSession aoMybatisSession,
			EvaluationSummaryBean aoEvaluationSummaryBean) throws ApplicationException
	{
		List<EvaluationSummaryBean> loEvaluationSummaryList = null;
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, aoEvaluationSummaryBean.getProcurementId());
		loContextDataMap.put(HHSConstants.EVALUATION_GROUP_ID, aoEvaluationSummaryBean.getEvaluationGroupId());
		try
		{
			loEvaluationSummaryList = (List<EvaluationSummaryBean>) DAOUtil.masterDAO(aoMybatisSession,
					aoEvaluationSummaryBean, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
					HHSConstants.FETCH_EVALUATION_SUMMARY, HHSConstants.COM_NYC_HHS_MODEL_EVAL_SUMMARY);
			setMoState("Successfully fetched evaluation summary for procurement Id:"
					+ aoEvaluationSummaryBean.getProcurementId());
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoExp)
		{
			aoExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching the evaluation summary for procurement Id:"
					+ aoEvaluationSummaryBean.getProcurementId(), aoExp);
			setMoState("Error while fetching the evaluation summary for procurement Id:"
					+ aoEvaluationSummaryBean.getProcurementId());
			throw aoExp;
		}
		/**
		 * Any Exception from DAO class will be handles over here. It throws
		 * Application Exception back to Controllers calling method through
		 * Transaction framework
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetching the evaluation summary for procurement Id:"
					+ aoEvaluationSummaryBean.getProcurementId(), aoExp);
			setMoState("Error while fetching the evaluation summary for procurement Id:"
					+ aoEvaluationSummaryBean.getProcurementId());
			throw new ApplicationException("Error while fetching the evaluation summary for procurement Id:"
					+ aoEvaluationSummaryBean.getProcurementId(), aoExp);
		}
		return loEvaluationSummaryList;
	}

	/**
	 * <p>
	 * This method fetches evaluation group proposal list for a given
	 * procurement id
	 * <ul>
	 * <li>1. Add multiple input parameters to a Map</li>
	 * <li>2. Fetches evaluation group proposals summary corresponding to a
	 * procurement id using <b>fetchEvaluationGroupProposal</b> from evaluation
	 * mapper</li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param loEvalGroupProposalBean - EvaluationGroupsProposalBean properties
	 * @return loEvaluationGroupProposalList - list of evaluation group
	 *         proposals
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<EvaluationGroupsProposalBean> fetchEvaluationGroupProposal(SqlSession aoMybatisSession,
			EvaluationGroupsProposalBean loEvalGroupProposalBean) throws ApplicationException
	{
		List<EvaluationGroupsProposalBean> loEvaluationGroupProposalList = null;
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, loEvalGroupProposalBean.getProcurementId());
		try
		{
			loEvaluationGroupProposalList = (List<EvaluationGroupsProposalBean>) DAOUtil.masterDAO(aoMybatisSession,
					loEvalGroupProposalBean, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
					HHSConstants.FETCH_EVALUATION_GROUP_PROPOSAL, HHSConstants.COM_NYC_HHS_MODEL_EVAL_GRP_PRP);
			setMoState("Successfully fetched evaluation group proposal summary for procurement Id:"
					+ loEvalGroupProposalBean.getProcurementId());
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoExp)
		{
			aoExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching the evaluation group proposal summary for procurement Id:"
					+ loEvalGroupProposalBean.getProcurementId(), aoExp);
			setMoState("Error while fetching the evaluation group proposal summary for procurement Id:"
					+ loEvalGroupProposalBean.getProcurementId());
			throw aoExp;
		}
		/**
		 * Any Exception from DAO class will be handles over here. It throws
		 * Application Exception back to Controllers calling method through
		 * Transaction framework
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetching the evaluation group proposal summary for procurement Id:"
					+ loEvalGroupProposalBean.getProcurementId(), aoExp);
			setMoState("Error while fetching the evaluation group proposal summary for procurement Id:"
					+ loEvalGroupProposalBean.getProcurementId());
			throw new ApplicationException(
					"Error while fetching the evaluation group proposal summary for procurement Id:"
							+ loEvalGroupProposalBean.getProcurementId(), aoExp);
		}
		return loEvaluationGroupProposalList;
	}

	/**
	 * <p>
	 * This method fetches all evaluation groups corresponding to a procurement
	 * id
	 * <ul>
	 * <li>1. Fetches evaluation groups corresponding to a procurement id using
	 * <b>fetchEvaluationGroupsProcurement</b> from evaluation mapper</li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProcurementId - Procurement Id
	 * @return list of all evaluation groups
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, String>> fetchAllEvaluationGroups(SqlSession aoMybatisSession, String asProcurementId)
			throws ApplicationException
	{
		List<Map<String, String>> loEvaluationGroups = null;
		Map<String, String> loDataMap = new HashMap<String, String>();
		loDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		loDataMap.put(HHSConstants.STATUS_EVALUATION_GROUP_NO_PROPOSALS, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_EVALUATION_GROUP_NO_PROPOSALS));
		try
		{
			loEvaluationGroups = (List<Map<String, String>>) DAOUtil.masterDAO(aoMybatisSession, loDataMap,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_EVALUATION_GROUPS_PROCUREMENT,
					HHSConstants.JAVA_UTIL_MAP);
			setMoState("Successfully fetched all evaluation groups for procurement id:" + asProcurementId);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error while fetching the evaluation groups for procurement Id:" + asProcurementId, aoExp);
			setMoState("Error while fetching the evaluation groups for procurement Id:" + asProcurementId);
			throw aoExp;
		}
		/**
		 * Any Exception from DAO class will be handles over here. It throws
		 * Application Exception back to Controllers calling method through
		 * Transaction framework
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetching the evaluation groups for procurement Id:" + asProcurementId, aoExp);
			setMoState("Error while fetching the evaluation groups for procurement Id:" + asProcurementId);
			throw new ApplicationException("Error while fetching the evaluation groups for procurement Id:"
					+ asProcurementId, aoExp);
		}
		return loEvaluationGroups;
	}

	/**
	 * <p>
	 * This method fetches competition pool titles corresponding to a
	 * procurement id and evaluation group id
	 * <ul>
	 * <li>1. Add multiple input parameters to a Map</li>
	 * <li>2. If evaluation group Id is not null, Fetches competition pool
	 * titles corresponding to a procurement id and evaluation group id using
	 * <b>fetchCompetitionPoolTitle</b> from evaluation mapper</li>
	 * <li>3. Else Fetches competition pool titles corresponding to a
	 * procurement id using <b>fetchCompetitionPoolTitleProc</b> from evaluation
	 * mapper</li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProcurementId - Procurement Id
	 * @param asEvaluationGroupId - Evaluation Group Id
	 * @return loCompetitionPoolList - list of competition pool titles map
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, String>> fetchCompetitionPoolTitles(SqlSession aoMybatisSession, String asProcurementId,
			String asEvaluationGroupId) throws ApplicationException
	{
		List<Map<String, String>> loCompetitionPoolList = null;
		try
		{
			Map<String, String> loDataMap = new HashMap<String, String>();
			loDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
			loDataMap.put(HHSConstants.EVALUATION_GROUP_ID_KEY, asEvaluationGroupId);
			loDataMap.put(HHSConstants.STATUS_COMPETITION_POOL_NO_PROPOSALS, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_COMPETITION_POOL_NO_PROPOSALS));
			loCompetitionPoolList = (List<Map<String, String>>) DAOUtil.masterDAO(aoMybatisSession, loDataMap,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_COMPETITION_POOL_TITLE,
					HHSConstants.JAVA_UTIL_MAP);
			setMoState("Successfully fetched competition pool titles for procurement Id:" + asProcurementId);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error while fetching the competition pool title for procurement Id:" + asProcurementId,
					aoExp);
			setMoState("Error while fetching the competition pool title for procurement Id:" + asProcurementId);
			throw aoExp;
		}
		/**
		 * Any Exception from DAO class will be handles over here. It throws
		 * Application Exception back to Controllers calling method through
		 * Transaction framework
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetching the competition pool title for procurement Id:" + asProcurementId,
					aoExp);
			setMoState("Error while fetching the competition pool title for procurement Id:" + asProcurementId);
			throw new ApplicationException("Error while fetching the competition pool title for procurement Id:"
					+ asProcurementId, aoExp);
		}
		return loCompetitionPoolList;
	}

	/**
	 * <p>
	 * This method fetches evaluation group title and evaluation group closing
	 * date corresponding to evaluationPoolMappingId
	 * <ul>
	 * <li>1. Fetches evaluation group title and group closing date for
	 * corresponding evaluationPoolMappingId using <b>fetchGroupTitleAndDate</b>
	 * from evaluation mapper</li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asEvaluationPoolMappingId - EvaluationPoolMappingId
	 * @return Map of evaluation titles and dates
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> fetchGroupTitleAndDate(SqlSession aoMybatisSession, String asEvaluationPoolMappingId)
			throws ApplicationException
	{
		Map<String, String> loGroupTitleList = null;
		try
		{
			loGroupTitleList = (Map<String, String>) DAOUtil.masterDAO(aoMybatisSession, asEvaluationPoolMappingId,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_GROUP_TITLE_DATE,
					HHSConstants.JAVA_LANG_STRING);
			setMoState("Successfully fetched evaluation group title and closing date for evaluation Pool Mapping Id:"
					+ asEvaluationPoolMappingId);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error(
					"Error while fetching the group pool title and closing date for evaluation Pool Mapping Id:"
							+ asEvaluationPoolMappingId, aoExp);
			setMoState("Error while fetching the group pool title for evaluation Pool Mapping Id:"
					+ asEvaluationPoolMappingId);
			throw aoExp;
		}
		/**
		 * Any Exception from DAO class will be handles over here. It throws
		 * Application Exception back to Controllers calling method through
		 * Transaction framework
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error(
					"Error while fetching the group pool title and closing date for evaluation Pool Mapping Id:"
							+ asEvaluationPoolMappingId, aoExp);
			setMoState("Error while fetching the group pool title and closing date for evaluation Pool Mapping Id:"
					+ asEvaluationPoolMappingId);
			throw new ApplicationException(
					"Error while fetching the group pool title and closing date for evaluation Pool Mapping Id:"
							+ asEvaluationPoolMappingId, aoExp);
		}
		return loGroupTitleList;
	}

	/**
	 * <p>
	 * This method fetches evaluation group title and submission close date for
	 * corresponding procurement id and evaluation group id
	 * <ul>
	 * <li>1. Add multiple input parameters to a Map</li>
	 * <li>2. Fetches evaluation group title and submission close date for
	 * corresponding procurement id and evaluation group id using
	 * <b>fetchGroupTitleAndDateGroup</b> from evaluation mapper</li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProcurementId - Procurement Id
	 * @param asEvaluationGroupId - Evaluation Group Id
	 * @return loGroupTitleAndDate - Map of evaluation titles and submission
	 *         close dates
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> fetchGroupTitleAndDateGroupId(SqlSession aoMybatisSession, String asProcurementId,
			String asEvaluationGroupId) throws ApplicationException
	{
		Map<String, String> loGroupTitleAndDate = null;
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		loContextDataMap.put(HHSConstants.EVALUATION_GROUP_ID, asEvaluationGroupId);
		try
		{
			loGroupTitleAndDate = (Map<String, String>) DAOUtil.masterDAO(aoMybatisSession, loContextDataMap,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_GROUP_TITLE_DATE_GROUP,
					HHSConstants.JAVA_UTIL_MAP);
			setMoState("Successfully fetched evaluation group title and closing date for evaluation group Id:"
					+ asEvaluationGroupId);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoExp)
		{
			aoExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching the group pool title and closing date for evaluation group Id:"
					+ asEvaluationGroupId, aoExp);
			setMoState("Error while fetching the group pool title and closing date for evaluation group Id:"
					+ asEvaluationGroupId);
			throw aoExp;
		}
		/**
		 * Any Exception from DAO class will be handles over here. It throws
		 * Application Exception back to Controllers calling method through
		 * Transaction framework
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetching the group pool title and closing date for evaluation group Id:"
					+ asEvaluationGroupId, aoExp);
			setMoState("Error while fetching the group pool title and closing date for evaluation group Id:"
					+ asEvaluationGroupId);
			throw new ApplicationException(
					"Error while fetching the group pool title and closing date for evaluation group Id:"
							+ asEvaluationGroupId, aoExp);
		}
		return loGroupTitleAndDate;
	}

	/**
	 * <p>
	 * This method assigns evaluation group to a proposal when submitting a
	 * proposal to a procurement for a given procurement id and proposal id
	 * <ul>
	 * <li>1. Add multiple input parameters to a Map</li>
	 * <li>2. Assigns evaluation group to a proposal using
	 * <b>updateEvaluationGroupForProposal</b> from proposal mapper</li>
	 * </ul>
	 * </p>
	 * @param aoMyBatisSession mybatis SQL session
	 * @param asProposalId a string value of proposal Id
	 * @param asProcurementId a string value of procurement Id
	 * @param aoValidateStatus status depicting success of previous services in
	 *            a transaction
	 * @return loAssignGroupStatus - boolean value indicating Assign Group
	 *         Status
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Boolean assignEvaluationGroup(SqlSession aoMyBatisSession, String asProposalId, String asProcurementId,
			Boolean aoValidateStatus) throws ApplicationException
	{
		Boolean loAssignGroupStatus = false;
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		loContextDataMap.put(HHSConstants.PROPOSAL_ID_KEY, asProposalId);
		try
		{
			if (null != aoValidateStatus && aoValidateStatus)
			{
				DAOUtil.masterDAO(aoMyBatisSession, loContextDataMap, HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER,
						HHSConstants.UPDATE_EVALUATION_GROUP_FOR_PROPOSAL, HHSConstants.JAVA_UTIL_MAP);
				loAssignGroupStatus = true;
				setMoState("Successfully assigned evaluation group to a proposal for proposal id:" + asProposalId);
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while updating evaluation group for a proposal corresponding to proposal id:"
					+ asProposalId, aoAppEx);
			setMoState("Error while updating evaluation group for a proposal corresponding to proposal id:"
					+ asProposalId);
			throw aoAppEx;
		}
		/**
		 * Any Exception from DAO class will be handles over here. It throws
		 * Application Exception back to Controllers calling method through
		 * Transaction framework
		 */
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error while updating evaluation group for a proposal corresponding to proposal id:"
					+ asProposalId, aoEx);
			setMoState("Error while updating evaluation group for a proposal corresponding to proposal id:"
					+ asProposalId);
			throw new ApplicationException(
					"Error while updating evaluation group for a proposal corresponding to proposal id:" + asProposalId,
					aoEx);
		}
		return loAssignGroupStatus;
	}

	/**
	 * <p>
	 * This method inserts evaluation pool mapping details for a given
	 * procurement id and current evaluation group
	 * <ul>
	 * <li>1. Inserts evaluation pool mapping details for all competition pools
	 * of a procurement using <b>insertGroupCompetitionMapping</b> from RFP
	 * Release mapper</li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoRFPReleaseBean - Release Bean properties
	 * @param aoUpdateStatusFlag - update status flag
	 * @return loUpdateStatus - flag depicting insert status
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean insertGroupCompetitionMapping(SqlSession aoMybatisSession, RFPReleaseBean aoRFPReleaseBean,
			Boolean aoUpdateStatusFlag) throws ApplicationException
	{
		Boolean loUpdateStatus = false;
		try
		{
			if (aoUpdateStatusFlag)
			{
				DAOUtil.masterDAO(aoMybatisSession, aoRFPReleaseBean, HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER,
						HHSConstants.INSERT_GROUP_COMP_MAPPING, HHSConstants.COM_NYC_HHS_MODEL_RFP_RELEASE_BEAN);
				loUpdateStatus = true;
				setMoState("Evaluation Pool Mapping Details inserted successfully for ProcurementId:"
						+ aoRFPReleaseBean.getProcurementId());
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Error occurred while inserting evaluation pool mapping details for procurement Id:"
					+ aoRFPReleaseBean.getProcurementId(), loAppEx);
			setMoState("Transaction Failed:: RFPReleaseService:insertGroupCompetitionMapping method - inserting evaluation group for procurement Id"
					+ aoRFPReleaseBean.getProcurementId());
			throw loAppEx;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Error occurred while inserting evaluation pool mapping details for procurement Id:"
					+ aoRFPReleaseBean.getProcurementId(), loEx);
			setMoState("Transaction Failed:: RFPReleaseService:insertGroupCompetitionMapping method - inserting evaluation group for procurement Id"
					+ aoRFPReleaseBean.getProcurementId());
			throw new ApplicationException(
					"Error occurred while inserting evaluation pool mapping details for procurement Id:"
							+ aoRFPReleaseBean.getProcurementId(), loEx);
		}
		return loUpdateStatus;
	}
	/**
	 * <p>
	 * This method inserts evaluation pool mapping details for a given
	 * procurement id and current evaluation group
	 * <ul>
	 * <li>1. Inserts evaluation pool mapping details for all competition pools
	 * of a procurement using <b>insertGroupCompetitionAddendumMapping</b> from RFP
	 * Release mapper during Addendum</li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProcurementId - Procurement Id
	 * @param asUserId - User Id
	 * @param aoUpdateStatusFlag - Previous 
	 * @return loUpdateStatus - flag depicting insert status
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean insertGroupCompetitionAddendumMapping(SqlSession aoMybatisSession, String asProcurementId,String asUserId , 
			Boolean aoUpdateStatusFlag) throws ApplicationException
	{
		Boolean loUpdateStatus = false;

		try
		{
			if (aoUpdateStatusFlag)
			{
				RFPReleaseBean  loRFPReleaseBean = new RFPReleaseBean();
				loRFPReleaseBean.setProcurementId(asProcurementId);
				loRFPReleaseBean.setCreatedByUserId(asUserId);
				loRFPReleaseBean.setModifiedByUserId(asUserId);
				loRFPReleaseBean.setEvalGroupStatus(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_COMPETITION_POOL_RELEASED));
				
				DAOUtil.masterDAO(aoMybatisSession, loRFPReleaseBean, HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER,
						HHSConstants.INSERT_GROUP_COMP_ADDENDUM_MAPPING, HHSConstants.COM_NYC_HHS_MODEL_RFP_RELEASE_BEAN);
				loUpdateStatus = true;
				setMoState("Evaluation Pool Mapping Details inserted successfully for ProcurementId:"
						+ loRFPReleaseBean.getProcurementId());
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Error occurred while inserting evaluation pool mapping details for procurement Id during addendum:"
					+ asProcurementId, loAppEx);
			setMoState("Transaction Failed:: RFPReleaseService:insertGroupCompetitionAddendumMapping method - inserting evaluation group for procurement Id during addendum"
					+ asProcurementId);
			throw loAppEx;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Error occurred while inserting evaluation pool mapping details for procurement Id during addendum:"
					+ asProcurementId, loEx);
			setMoState("Transaction Failed:: RFPReleaseService:insertGroupCompetitionAddendumMapping method - inserting evaluation group for procurement Id during addendum"
					+ asProcurementId);
			throw new ApplicationException(
					"Error occurred while inserting evaluation pool mapping details for procurement Id during addendum:"
							+ asProcurementId, loEx);
		}
		return loUpdateStatus;
	}
	
	/**
	 * <p>
	/**
	 * <p>
	 * This method updates evaluation group status whenever competition pool
	 * status is changed
	 * <ul>
	 * <li>1. Get minimum of evaluation pool mapping status for a given
	 * evaluation id or evaluation pool mapping id using
	 * <b>getMinPoolMappingStatusId</b>from evaluation mapper</li>
	 * <li>2. Add Evaluation Group status and Competition pool status to input
	 * paramter map</li>
	 * <li>3. updates evaluation group status for for a given evaluation id or
	 * evaluation pool mapping id using <b>updateEvalGroupStatus</b> from
	 * evaluation mapper</li>
	 * <li>4. insert into ACCELERATOR_AUDIT
	 * using<b>hhsauditInsertForEvalGroup</b>from hhsAudit mapper</li>
	 * </ul>
	 * </p>
	 * @param aoMyBatisSession - mybatis SQL session
	 * @param aoInputParam - Input Parameters Map
	 * @return loUpdateEvalGroupStatus - flag depicting update evaluation group
	 *         status
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean updateEvalGroupStatus(SqlSession aoMyBatisSession, Map<String, Object> aoInputParam)
			throws ApplicationException
	{
		Boolean loUpdateEvalGroupStatus = Boolean.FALSE;
		try
		{
			Integer loStatusId = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoInputParam,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.GET_MIN_POOL_MAPPING_STATUS_ID,
					HHSConstants.JAVA_UTIL_MAP);
			setInputParamForEvalGrpUpdate(aoInputParam, loStatusId);

			// executing query updateEvalGroupStatus of
			// EvaluationMapper.xml
			Integer loUpdateCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoInputParam,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.UPDATE_EVAL_GRP_STATUS,
					HHSConstants.JAVA_UTIL_MAP);
			// returns whether the execution is successful or not
			if (loUpdateCount > HHSConstants.INT_ZERO)
			{
				loUpdateEvalGroupStatus = Boolean.TRUE;
			}
			// Condition added for audit entry change
			HhsAuditBean aoAudit = new HhsAuditBean();
			aoAudit.setEvalGrp((String) aoInputParam.get(HHSConstants.IS_EVAL_GRP));
			if (null != (String) aoInputParam.get(HHSConstants.IS_EVAL_GRP)
					&& ((String) aoInputParam.get(HHSConstants.IS_EVAL_GRP)).equalsIgnoreCase(HHSConstants.ONE))
			{
				aoAudit.setEntityId((String) aoInputParam.get(HHSConstants.EVALUATION_GROUP_ID));
			}
			else
			{
				aoAudit.setEntityId((String) aoInputParam.get(HHSConstants.EVALUATION_POOL_MAPPING_ID));
			}
			aoAudit.setEventName((String) aoInputParam.get(HHSConstants.EVENT_NAME));
			aoAudit.setEventType((String) aoInputParam.get(HHSConstants.EVENT_TYPE));
			aoAudit.setUserId((String) aoInputParam.get(HHSConstants.USER_ID));
			DAOUtil.masterDAO(aoMyBatisSession, aoAudit, HHSConstants.MAPPER_CLASS_HHS_AUDIT_MAPPER,
					HHSConstants.HHSAUDIT_INSERT_FOR_EVAL_GROUP, HHSConstants.HHS_AUDIT_BEAN_PATH);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			aoExp.setContextData(aoInputParam);
			setMoState("Exception occured while updating evaluation group status for evaluation group Id:"
					+ aoInputParam.get(HHSConstants.EVALUATION_GROUP_ID));
			LOG_OBJECT.Error("Exception occured while updating evaluation group status for evaluation group Id:"
					+ aoInputParam.get(HHSConstants.EVALUATION_GROUP_ID), aoExp);
			throw aoExp;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			ApplicationException aoAppExp = new ApplicationException(
					"Exception occured while updating evaluation group status for evaluation group Id:"
							+ aoInputParam.get(HHSConstants.EVALUATION_GROUP_ID), aoEx);
			LOG_OBJECT.Error("Exception occured while updating evaluation group status for evaluation group Id:"
					+ aoInputParam.get(HHSConstants.EVALUATION_GROUP_ID), aoAppExp);
			setMoState("Exception occured while updating evaluation group status for evaluation group Id:"
					+ aoInputParam.get(HHSConstants.EVALUATION_GROUP_ID));
			throw aoAppExp;
		}
		return loUpdateEvalGroupStatus;
	}

	/**
	 * This method is used to set input parameters for
	 * <b>updateEvalGroupStatus</b> query in evaluation mapper
	 * @param aoInputParam - Input Parameters Map
	 * @param loStatusId - status id
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	private void setInputParamForEvalGrpUpdate(Map<String, Object> aoInputParam, Integer loStatusId)
			throws ApplicationException
	{
		aoInputParam.put(HHSConstants.MIN_STATUS_ID, loStatusId);
		aoInputParam.put(HHSConstants.STATUS_EVALUATION_GROUP_RELEASED, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_EVALUATION_GROUP_RELEASED));
		aoInputParam.put(HHSConstants.STATUS_EVALUATION_GROUP_PROPOSALS_RECEIVED, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_EVALUATION_GROUP_PROPOSALS_RECEIVED));
		aoInputParam.put(HHSConstants.STATUS_EVALUATION_GROUP_EVALUATIONS_COMPLETE, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_EVALUATION_GROUP_EVALUATIONS_COMPLETE));
		aoInputParam.put(HHSConstants.STATUS_EVALUATION_GROUP_SELECTIONS_MADE, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_EVALUATION_GROUP_SELECTIONS_MADE));
		aoInputParam.put(HHSConstants.STATUS_EVALUATION_GROUP_NO_PROPOSALS, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_EVALUATION_GROUP_NO_PROPOSALS));
		aoInputParam.put(HHSConstants.STATUS_COMPETITION_POOL_RELEASED, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_COMPETITION_POOL_RELEASED));
		aoInputParam.put(HHSConstants.STATUS_COMPETITION_POOL_PROPOSALS_RECEIVED, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_COMPETITION_POOL_PROPOSALS_RECEIVED));
		aoInputParam.put(HHSConstants.STATUS_COMPETITION_POOL_EVALUATIONS_COMPLETE, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_COMPETITION_POOL_EVALUATIONS_COMPLETE));
		aoInputParam.put(HHSConstants.STATUS_COMPETITION_POOL_SELECTIONS_MADE, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_COMPETITION_POOL_SELECTIONS_MADE));
		aoInputParam.put(HHSConstants.STATUS_COMPETITION_POOL_NO_PROPOSALS, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_COMPETITION_POOL_NO_PROPOSALS));
		aoInputParam.put(HHSConstants.STATUS_COMPETITION_POOL_NON_RESPONSIVE, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_COMPETITION_POOL_NON_RESPONSIVE));
		aoInputParam.put(HHSConstants.STATUS_EVALUATION_GROUP_NON_RESPONSIVE, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_EVALUATION_GROUP_NON_RESPONSIVE));
		// Start || Changes done for enhancement 6577 for Release 3.10.0
		aoInputParam.put(HHSConstants.STATUS_COMPETITION_POOL_CANCELLED, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_COMPETITION_POOL_CANCELLED));
		aoInputParam.put(HHSConstants.STATUS_EVALUATION_GROUP_CANCELLED, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_EVALUATION_GROUP_CANCELLED));
		// End || Changes done for enhancement 6577 for Release 3.10.0
	}

	/**
	 * <p>
	 * This method creates new evaluation group whenever a procurement is
	 * released or active evaluation group is closed
	 * <ul>
	 * <li>1. Inserts evaluation group for procurement using
	 * <b>insertEvaluationGroup</b> from procurement mapper</li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoProcBean - Procurement properties
	 * @param asCloseGroupFlag - flag denoting open or close procurement
	 * @return loUpdateStatus - flag depicting insert status
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean insertEvaluationGroup(SqlSession aoMybatisSession, Procurement aoProcBean, Boolean aoCloseGroupFlag)
			throws ApplicationException
	{
		Boolean loUpdateStatus = false;
		try
		{
			if (null != aoCloseGroupFlag && aoCloseGroupFlag)
			{
				DAOUtil.masterDAO(aoMybatisSession, aoProcBean, HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
						HHSConstants.INSERT_EVALUATION_GROUP, HHSConstants.COM_NYC_HHS_MODEL_PROC);
				loUpdateStatus = true;
				setMoState("Evaluation Group inserted successfully for ProcurementId:" + aoProcBean.getProcurementId());
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
					"Error occurred while inserting evaluation group for procurement Id:"
							+ aoProcBean.getProcurementId(), loAppEx);
			setMoState("Transaction Failed:: CompetitionPoolService:insertEvaluationGroup method - inserting evaluation group for procurement Id"
					+ aoProcBean.getProcurementId());
			throw loAppEx;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (Exception loEx)
		{
			LOG_OBJECT.Error(
					"Error occurred while inserting evaluation group for procurement Id:"
							+ aoProcBean.getProcurementId(), loEx);
			setMoState("Transaction Failed:: CompetitionPoolService:insertEvaluationGroup method - inserting evaluation group for procurement Id"
					+ aoProcBean.getProcurementId());
			throw new ApplicationException("Error occurred while inserting evaluation group for procurement Id:"
					+ aoProcBean.getProcurementId(), loEx);
		}
		return loUpdateStatus;
	}

	/**
	 * <p>
	 * This method updates competition pool status for particular evaluation
	 * group of a procurement
	 * <ul>
	 * <li>1. Fetches list of submitted proposal count against competition pool
	 * id with the query id <b>fetchProposalCountAndCompId</b></li>
	 * <li>2. Iterate through the list of proposal count against competition
	 * pool id</li>
	 * <li>3. Check if proposal count is 0 for a competition pool, set
	 * competition pool status as "No Proposals" else set as
	 * "Proposals Received"</li>
	 * <li>4. Updates competition pool status using <b>updateCompPoolStatus</b>
	 * from evaluation mnapper</li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoDataMap - Input parameters Map
	 * @return loUpdateStatus - boolean
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings(
	{ "unchecked" })
	public Boolean updateCompPoolStatus(SqlSession aoMybatisSession, Map<String, Object> aoDataMap)
			throws ApplicationException
	{
		Boolean loUpdateStatus = false;
		List<Map<String, Object>> loCompetitionPoolMap = null;
		Map<String, Object> aoStatusValMap = new HashMap<String, Object>();
		try
		{
			loCompetitionPoolMap = (List<Map<String, Object>>) DAOUtil.masterDAO(aoMybatisSession, aoDataMap,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_PROPOSAL_COUNT_AND_COMP_ID,
					HHSConstants.JAVA_UTIL_MAP);
			if (null != loCompetitionPoolMap)
			{
				for (Map<String, Object> loResultMap : loCompetitionPoolMap)
				{
					if (loResultMap.get(HHSConstants.PROPOSAL_COUNT_TABLE_COL) != null
							&& ((BigDecimal) loResultMap.get(HHSConstants.PROPOSAL_COUNT_TABLE_COL)).intValue() == 0)
					{
						aoStatusValMap.put(HHSConstants.COMP_POOL_STATUS,
								aoDataMap.get(HHSConstants.COMPETITION_POOL_NO_PROPOSALS));
					}
					else
					{
						aoStatusValMap.put(HHSConstants.COMP_POOL_STATUS,
								aoDataMap.get(HHSConstants.COMPETITION_POOL_PROPOSAL_RECEIVED));
					}
					aoStatusValMap
							.put(HHSConstants.COMP_CONF_ID, loResultMap.get(HHSConstants.COMPETITION_POOL_ID_COL));
					aoStatusValMap.put(HHSConstants.EVALUATION_GROUP_ID,
							aoDataMap.get(HHSConstants.EVALUATION_GROUP_ID));
					Integer loCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoStatusValMap,
							HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.UPDATE_COMP_POOL_STATUS,
							HHSConstants.JAVA_UTIL_MAP);
					if (loCount > 0)
					{
						addAuditForCompetitionPool(aoMybatisSession, aoDataMap, loResultMap);
					}
				}
				loUpdateStatus = true;
				setMoState("Competition Pool Status updated sucessfully for evaluation group id:"
						+ aoDataMap.get(HHSConstants.EVALUATION_GROUP_ID));
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.setContextData(aoDataMap);
			setMoState("Error while updating comp pool status for evaluation group id :"
					+ aoDataMap.get(HHSConstants.EVALUATION_GROUP_ID));
			LOG_OBJECT.Error(
					"Error while updating comp pool status for evaluation group id :"
							+ aoDataMap.get(HHSConstants.EVALUATION_GROUP_ID), loExp);
			throw loExp;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (Exception loExp)
		{
			ApplicationException aoAppExp = new ApplicationException(
					"Error while updating comp pool status for evaluation group id :"
							+ aoDataMap.get(HHSConstants.EVALUATION_GROUP_ID), loExp);
			setMoState("Error while updating comp pool status for evaluation group id :"
					+ aoDataMap.get(HHSConstants.EVALUATION_GROUP_ID));
			LOG_OBJECT.Error(
					"Error while updating comp pool status for evaluation id :"
							+ aoDataMap.get(HHSConstants.EVALUATION_GROUP_ID), aoAppExp);
			throw aoAppExp;
		}
		return loUpdateStatus;
	}

	/**
	 * This method adds audit for competition pool status update
	 * <ul>
	 * <li>Get data from datamap and set into Audit Bean</li>
	 * <li>Invoke service <b>hhsauditInsertForCompPool</b> to add audit entry
	 * into database</li>
	 * </ul>
	 * @param aoMybatisSession - Mybatis session
	 * @param aoDataMap - data map from transaction
	 * @param aoResultMap - Comepetition pool related data
	 * @throws ApplicationException
	 */
	private void addAuditForCompetitionPool(SqlSession aoMybatisSession, Map<String, Object> aoDataMap,
			Map<String, Object> aoResultMap) throws ApplicationException
	{
		// Condition added for audit entry change
		HhsAuditBean aoAudit = new HhsAuditBean();
		aoAudit.setEvalGrp((String) aoDataMap.get(HHSConstants.IS_EVAL_GRP));
		if (null != (String) aoDataMap.get(HHSConstants.IS_EVAL_GRP)
				&& ((String) aoDataMap.get(HHSConstants.IS_EVAL_GRP)).equalsIgnoreCase(HHSConstants.ONE))
		{
			aoAudit.setEntityId((String) aoDataMap.get(HHSConstants.EVALUATION_GROUP_ID));
			aoAudit.setCompConfId(aoResultMap.get(HHSConstants.COMPETITION_POOL_ID_COL).toString());
		}
		else
		{
			aoAudit.setEntityId((String) aoDataMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID));
		}
		aoAudit.setEventName((String) aoDataMap.get(HHSConstants.EVENT_NAME));
		aoAudit.setEventType((String) aoDataMap.get(HHSConstants.EVENT_TYPE));
		aoAudit.setUserId((String) aoDataMap.get(HHSConstants.USER_ID));
		DAOUtil.masterDAO(aoMybatisSession, aoAudit, HHSConstants.MAPPER_CLASS_HHS_AUDIT_MAPPER,
				HHSConstants.HHSAUDIT_INSERT_FOR_COMP_POOL, HHSConstants.HHS_AUDIT_BEAN_PATH);
	}

	/**
	 * <p>
	 * This method fetches evaluation summary count for a given procurement id
	 * or evaluation group id
	 * <ul>
	 * <li>1. Add input parameters to a Map</li>
	 * <li>2. Fetches evaluation summary count using
	 * <b>fetchEvaluationSummaryCount</b> from evaluation mapper</li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoEvaluationSummaryBean - EvaluationSummaryBean properties
	 * @return loEvaluationSummaryCount - integer
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Integer fetchEvaluationSummaryCount(SqlSession aoMybatisSession,
			EvaluationSummaryBean aoEvaluationSummaryBean) throws ApplicationException
	{
		Integer loEvaluationSummaryCount = null;
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, aoEvaluationSummaryBean.getProcurementId());
		loContextDataMap.put(HHSConstants.EVALUATION_GROUP_ID, aoEvaluationSummaryBean.getEvaluationGroupId());
		try
		{
			loEvaluationSummaryCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoEvaluationSummaryBean,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_EVALUATION_SUMMARY_COUNT,
					HHSConstants.COM_NYC_HHS_MODEL_EVAL_SUMMARY);
			setMoState("Successfully fetched evaluation summary count for procurement id:"
					+ aoEvaluationSummaryBean.getProcurementId());
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoExp)
		{
			aoExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching the evaluation summary count for procurement id:"
					+ aoEvaluationSummaryBean.getProcurementId(), aoExp);
			setMoState("Error while fetching the evaluation summary count for procurement id:"
					+ aoEvaluationSummaryBean.getProcurementId());
			throw aoExp;
		}
		/**
		 * Any Exception from DAO class will be handles over here. It throws
		 * Application Exception back to Controllers calling method through
		 * Transaction framework
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetching the evaluation summary count for procurement id:"
					+ aoEvaluationSummaryBean.getProcurementId(), aoExp);
			setMoState("Error while fetching the evaluation summary count for procurement id:"
					+ aoEvaluationSummaryBean.getProcurementId());
			throw new ApplicationException("Error while fetching the evaluation summary count for procurement id:"
					+ aoEvaluationSummaryBean.getProcurementId(), aoExp);
		}
		return loEvaluationSummaryCount;
	}

	/**
	 * <p>
	 * This method fetches count of proposals corresponding to a evaluation
	 * group of a procurement
	 * <ul>
	 * <li>1. Add input parameters to a Map</li>
	 * <li>2. Fetches count of proposals corresponding to a evaluation group
	 * using <b>fetchEvalGroupProposalCount</b> from evaluation mapper</li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoEvalGroupsProposalBean - EvalGroupsProposalBean properties
	 * @return loEvalGroupProposalCount - integer
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Integer fetchEvalGroupProposalCount(SqlSession aoMybatisSession,
			EvaluationGroupsProposalBean aoEvalGroupsProposalBean) throws ApplicationException
	{
		Integer loEvalGroupProposalCount = null;
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, aoEvalGroupsProposalBean.getProcurementId());
		try
		{
			loEvalGroupProposalCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoEvalGroupsProposalBean,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_EVAL_GRP_PROPOSAL_COUNT,
					HHSConstants.COM_NYC_HHS_MODEL_EVAL_GRP_PRP);
			setMoState("Successfully fetched proposal count for evaluation group of a procurement id:"
					+ aoEvalGroupsProposalBean.getProcurementId());
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoExp)
		{
			aoExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching proposal count for evaluation group of a procurement id:"
					+ aoEvalGroupsProposalBean.getProcurementId(), aoExp);
			setMoState("Error while fetching proposal count for evaluation group of a procurement id:"
					+ aoEvalGroupsProposalBean.getProcurementId());
			throw aoExp;
		}
		/**
		 * Any Exception from DAO class will be handles over here. It throws
		 * Application Exception back to Controllers calling method through
		 * Transaction framework
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetching proposal count for evaluation group of a procurement id:"
					+ aoEvalGroupsProposalBean.getProcurementId(), aoExp);
			setMoState("Error while fetching proposal count for evaluation group of a procurement id:"
					+ aoEvalGroupsProposalBean.getProcurementId());
			throw new ApplicationException(
					"Error while fetching proposal count for evaluation group of a procurement id:"
							+ aoEvalGroupsProposalBean.getProcurementId(), aoExp);
		}
		return loEvalGroupProposalCount;
	}

	/**
	 * <p>
	 * This method fetches evaluation group id corresponding to a procurement id
	 * <ul>
	 * <li>1. Add input parameters to a Map</li>
	 * <li>2. Fetches evaluation group id corresponding to a procurement id
	 * using <b>fetchEvaluationGroupId</b> from evaluation mapper</li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProcurementId - Procurement Id
	 * @return evaluation group id - string
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public String fetchEvaluationGroupId(SqlSession aoMybatisSession, String asProcurementId)
			throws ApplicationException
	{
		String lsEvaluationGroupId = null;
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		try
		{
			lsEvaluationGroupId = (String) DAOUtil.masterDAO(aoMybatisSession, asProcurementId,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_EVALUATION_GROUP_ID,
					HHSConstants.JAVA_LANG_STRING);
			setMoState("Successfully fetched evaluation group id for a given procurement id:" + asProcurementId);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoExp)
		{
			aoExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching evaluation group id for a given procurement id:" + asProcurementId,
					aoExp);
			setMoState("Error while fetching evaluation group id for a given procurement id:" + asProcurementId);
			throw aoExp;
		}
		/**
		 * Any Exception from DAO class will be handles over here. It throws
		 * Application Exception back to Controllers calling method through
		 * Transaction framework
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetching evaluation group id for a given procurement id:" + asProcurementId,
					aoExp);
			setMoState("Error while fetching evaluation group id for a given procurement id:" + asProcurementId);
			throw new ApplicationException("Error while fetching evaluation group id for a given procurement id:"
					+ asProcurementId, aoExp);
		}
		return lsEvaluationGroupId;
	}

	/**
	 * This method is used to fetch proposal and organization name for a given
	 * proposal id
	 * 
	 * <ul>
	 * <li>1. Add input parameters to a Map</li>
	 * <li>2. Fetches Proposal Title and Organization name for a proposal id
	 * using <b>fetchProposalAndOrgName</b> from evaluation mapper</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProposalId - proposal id
	 * @return loHeaderMap - map containing proposal title and org name
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
			setMoState("Successfully fetched Proposal Title and Organization name for a proposal id:" + asProposalId);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoExp)
		{
			aoExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching Proposal Title and Organization name for a proposal id:"
					+ asProposalId, aoExp);
			setMoState("Error while fetching Proposal Title and Organization name for a proposal id:" + asProposalId);
			throw aoExp;
		}
		/**
		 * Any Exception from DAO class will be handles over here. It throws
		 * Application Exception back to Controllers calling method through
		 * Transaction framework
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetching Proposal Title and Organization name for a proposal id:"
					+ asProposalId, aoExp);
			setMoState("Error while fetching Proposal Title and Organization name for a proposal id:" + asProposalId);
			throw new ApplicationException(
					"Error while fetching Proposal Title and Organization name for a proposal id:" + asProposalId,
					aoExp);
		}
		return loHeaderMap;
	}

	/**
	 * This method is used to fetch flag depicting RFP is released before R4 for
	 * a given procurement id
	 * 
	 * <ul>
	 * <li>1. Add input parameters to a Map</li>
	 * <li>2. Fetches RFP Release Before flag for a given procurement id using
	 * <b>fetchRfpReleasedBeforeR4Flag</b> from evaluation mapper</li>
	 * </ul>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProcurementId - procurement id
	 * @return loRfpBeforeR4Flag - a string value having value 1 or null
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public String fetchRfpReleasedBeforeR4Flag(SqlSession aoMybatisSession, String asProcurementId)
			throws ApplicationException
	{
		String loRfpBeforeR4Flag = null;
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID, asProcurementId);
		try
		{
			loRfpBeforeR4Flag = (String) DAOUtil.masterDAO(aoMybatisSession, asProcurementId,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_RFP_BEFORE_R4_FLAG,
					HHSConstants.JAVA_LANG_STRING);
			setMoState("Successfully fetched RFP Released before flag for procrement id:" + asProcurementId);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoExp)
		{
			aoExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching RFP Released before flag for procrement id:" + asProcurementId);
			setMoState("Error while fetching RFP Released before flag for procrement id:" + asProcurementId);
			throw aoExp;
		}
		/**
		 * Any Exception from DAO class will be handles over here. It throws
		 * Application Exception back to Controllers calling method through
		 * Transaction framework
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetching RFP Released before flag for procrement id:" + asProcurementId,
					aoExp);
			setMoState("Error while fetching RFP Released before flag for procrement id:" + asProcurementId);
			throw new ApplicationException("Error while fetching RFP Released before flag for procrement id:"
					+ asProcurementId, aoExp);
		}
		return loRfpBeforeR4Flag;
	}

	/**
	 * This method updates procurement status based on evaluation group status
	 * for a procurement id
	 * 
	 * <ul>
	 * <li>1. Get minimum of evaluation group status for a procurement id using
	 * <b>getMinGroupStatusId</b> from evaluation mapper</li>
	 * <li>2. Add Evaluation Group status and procurement status to input
	 * parameter map</li>
	 * <li>3. Updates procurement status based on group id using
	 * <b>updateProcurementStatusBasedOnGroup</b> from evaluation mapper</li>
	 * <li>4. Insert procurement status based on HhsAuditBean using
	 * <b>hhsauditInsertForProcurement</b> from evaluation mapper</li>
	 * <li>5. Updates default configuration based on aoInputParam using
	 * <b>updateDefaultConfigurationId</b> from evaluation mapper</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoInputParam - Input properties
	 * @return flag depicting update procurement status - boolean
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean updateProcurementStatusBasedOnGroup(SqlSession aoMybatisSession, Map<String, Object> aoInputParam)
			throws ApplicationException
	{
		Boolean loUpdateStatus = false;
		try
		{
			Integer loStatusId = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoInputParam,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.GET_MIN_GROUP_STATUS_ID,
					HHSConstants.JAVA_UTIL_MAP);
			setInputParamForProcStatusUpdate(aoInputParam, loStatusId);

			Integer loCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoInputParam,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.UPDATE_PROC_STATUS_GROUP_BASIS,
					HHSConstants.JAVA_UTIL_MAP);
			// Condition added for audit entry change
			if (loCount > 0)
			{
				HhsAuditBean aoAudit = new HhsAuditBean();
				aoAudit.setEntityId((String) aoInputParam.get(HHSConstants.PROCUREMENT_ID_KEY));
				aoAudit.setEventName((String) aoInputParam.get(HHSConstants.EVENT_NAME));
				aoAudit.setEventType((String) aoInputParam.get(HHSConstants.EVENT_TYPE));
				aoAudit.setUserId((String) aoInputParam.get(HHSConstants.USER_ID));
				DAOUtil.masterDAO(aoMybatisSession, aoAudit, HHSConstants.MAPPER_CLASS_HHS_AUDIT_MAPPER,
						HHSConstants.HHSAUDIT_INSERT_FOR_PROC, HHSConstants.HHS_AUDIT_BEAN_PATH);
			}
			if (null != (String) aoInputParam.get(HHSConstants.DEFAULT_CONFIG_ID)
					&& !((String) aoInputParam.get(HHSConstants.DEFAULT_CONFIG_ID)).isEmpty())
			{
				DAOUtil.masterDAO(aoMybatisSession, aoInputParam, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
						HHSConstants.UPDATE_DEFAULT_CONF_ID, HHSConstants.JAVA_UTIL_MAP);
			}
			loUpdateStatus = true;
			setMoState("Successfully updates procurement status for procurement id:"
					+ aoInputParam.get(HHSConstants.PROCUREMENT_ID_KEY));
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.setContextData(aoInputParam);
			setMoState("Error while updating procurement status for procurement id :"
					+ aoInputParam.get(HHSConstants.PROCUREMENT_ID_KEY));
			LOG_OBJECT.Error(
					"Error while updating procurement status for procurement id :"
							+ aoInputParam.get(HHSConstants.PROCUREMENT_ID_KEY), loExp);
			throw loExp;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (Exception loExp)
		{
			ApplicationException aoAppEx = new ApplicationException(
					"Error while updating procurement status for procurement id :"
							+ aoInputParam.get(HHSConstants.PROCUREMENT_ID_KEY), loExp);
			setMoState("Error while updating procurement status for procurement id :"
					+ aoInputParam.get(HHSConstants.PROCUREMENT_ID_KEY));
			LOG_OBJECT.Error(
					"Error while updating procurement status for procurement id :"
							+ aoInputParam.get(HHSConstants.PROCUREMENT_ID_KEY), aoAppEx);
			throw aoAppEx;
		}
		return loUpdateStatus;
	}

	/**
	 * This method is used to set input parameters for
	 * <b>updateProcurementStatusBasedOnGroup</b> query in evaluation mapper
	 * @param aoInputParam - input parameters map
	 * @param loStatusId - status id
	 * @throws ApplicationException If an exception Occurs
	 */
	private void setInputParamForProcStatusUpdate(Map<String, Object> aoInputParam, Integer loStatusId)
			throws ApplicationException
	{
		aoInputParam.put(HHSConstants.MIN_STATUS_ID, loStatusId);
		aoInputParam.put(HHSConstants.STATUS_EVALUATION_GROUP_RELEASED, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_EVALUATION_GROUP_RELEASED));
		aoInputParam.put(HHSConstants.STATUS_EVALUATION_GROUP_PROPOSALS_RECEIVED, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_EVALUATION_GROUP_PROPOSALS_RECEIVED));
		aoInputParam.put(HHSConstants.STATUS_EVALUATION_GROUP_EVALUATIONS_COMPLETE, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_EVALUATION_GROUP_EVALUATIONS_COMPLETE));
		aoInputParam.put(HHSConstants.STATUS_EVALUATION_GROUP_SELECTIONS_MADE, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_EVALUATION_GROUP_SELECTIONS_MADE));
		aoInputParam.put(HHSConstants.STATUS_EVALUATION_GROUP_NO_PROPOSALS, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_EVALUATION_GROUP_NO_PROPOSALS));

		aoInputParam.put(HHSConstants.STATUS_PROCUREMENT_RELEASED, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_RELEASED));
		aoInputParam.put(HHSConstants.STATUS_PROCUREMENT_PROPOSALS_RECEIVED, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_PROPOSALS_RECEIVED));
		aoInputParam.put(HHSConstants.STATUS_PROCUREMENT_EVALUATIONS_COMPLETE, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_EVALUATIONS_COMPLETE));
		aoInputParam.put(HHSConstants.STATUS_PROCUREMENT_SELECTIONS_MADE, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_SELECTIONS_MADE));
		aoInputParam.put(HHSConstants.STATUS_EVALUATION_GROUP_NON_RESPONSIVE, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_EVALUATION_GROUP_NON_RESPONSIVE));
	}

	/**
	 * This method is used to check if procurement is open ended or zero value
	 * for a given procurement id
	 * 
	 * <ul>
	 * <li>1. Add input parameters to a Map</li>
	 * <li>2. Fetches count of open ended or zero value procurement for a given
	 * procurement id using <b>isOpenEndedOrZeroValue</b> from procurement
	 * mapper</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProcurementId - procurement id
	 * @return flag depicting procurement is open ended or zero value - boolean
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public boolean checkIfOpenEndedZeroValue(SqlSession aoMybatisSession, String asProcurementId)
			throws ApplicationException
	{
		Map<String, String> loContextDataMap = new HashMap<String, String>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		LOG_OBJECT.Debug("Entered into CompetitionPoolService:checkIfOpenEndedZeroValue method with procurement id:"
				+ asProcurementId);
		int liCount = HHSConstants.INT_ZERO;
		try
		{
			liCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, asProcurementId,
					HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.IS_OPEN_ENDED_OR_ZERO_VALUE,
					HHSConstants.JAVA_LANG_STRING);
			setMoState("Transaction passed:: CompetitionPoolService:checkIfOpenEndedZeroValue method for procurement id:"
					+ asProcurementId);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handled over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			// Catch the exception thrown by masterDao method and pass the
			// caught exception with input params to controller
			loAppEx.setContextData(loContextDataMap);
			LOG_OBJECT.Error(
					"Transaction Failed:: CompetitionPoolService:checkIfOpenEndedZeroValue method for procurement id:"
							+ asProcurementId, loAppEx);
			setMoState("Transaction Failed:: CompetitionPoolService:checkIfOpenEndedZeroValue method for procurement id:"
					+ asProcurementId);
			throw loAppEx;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handled over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (Exception loEx)
		{
			// Catch the exception thrown by masterDao method and pass the
			// caught exception with input params to controller
			ApplicationException aoAppEx = new ApplicationException(
					"Error while ferching flag for open ended or zero value for procurement id:" + asProcurementId,
					loEx);
			LOG_OBJECT.Error(
					"Transaction Failed:: CompetitionPoolService:checkIfOpenEndedZeroValue method for procurement id:"
							+ asProcurementId, aoAppEx);
			setMoState("Transaction Failed:: CompetitionPoolService:checkIfOpenEndedZeroValue method for procurement id:"
					+ asProcurementId);
			throw aoAppEx;
		}
		return (liCount != HHSConstants.INT_ZERO);
	}

	/**
	 * <p>
	 * This method updates competition pool status for a particular evaluation
	 * group
	 * <ul>
	 * <li>1. Updates competition pool status for a given evaluation pool
	 * mapping id using <b>updateEvalPoolMappingStatus</b> from evaluation
	 * mapper</li>
	 * <li>2. Insert into competetion pool for a given evaluation pool mapping
	 * id using <b>hhsauditInsertForCompPool</b> from evaluation mapper</li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoDataMap - Input parameters Map
	 * @return loUpdateStatus - flag depicting update status
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean updateEvalPoolMappingStatus(SqlSession aoMybatisSession, Map<String, Object> aoDataMap)
			throws ApplicationException
	{
		Boolean loUpdateStatus = false;
		try
		{
			Integer loCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoDataMap,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.UPDATE_EVAL_POOL_MAPPING_STATUS,
					HHSConstants.JAVA_UTIL_MAP);
			if (loCount > 0)
			{
				loUpdateStatus = true;
				// Condition added for audit entry change
				HhsAuditBean aoAudit = new HhsAuditBean();
				aoAudit.setEntityId((String) aoDataMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID));
				aoAudit.setEventName((String) aoDataMap.get(HHSConstants.EVENT_NAME));
				aoAudit.setEventType((String) aoDataMap.get(HHSConstants.EVENT_TYPE));
				aoAudit.setUserId((String) aoDataMap.get(HHSConstants.USER_ID));
				DAOUtil.masterDAO(aoMybatisSession, aoAudit, HHSConstants.MAPPER_CLASS_HHS_AUDIT_MAPPER,
						HHSConstants.HHSAUDIT_INSERT_FOR_COMP_POOL, HHSConstants.HHS_AUDIT_BEAN_PATH);
				setMoState("Successfully updated competition pool status for a given evaluation pool mapping id:"
						+ aoDataMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID));
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.setContextData(aoDataMap);
			setMoState("Error while updating competition pool status for a given evaluation pool mapping id:"
					+ aoDataMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID));
			LOG_OBJECT.Error("Error while updating competition pool status for a given evaluation pool mapping id:"
					+ aoDataMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID), loExp);
			throw loExp;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (Exception loExp)
		{
			setMoState("Error while updating competition pool status for a given evaluation pool mapping id:"
					+ aoDataMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID));
			LOG_OBJECT.Error("Error while updating competition pool status for a given evaluation pool mapping id:"
					+ aoDataMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID), loExp);
			throw new ApplicationException(
					"Error while updating competition pool status for a given evaluation pool mapping id:"
							+ aoDataMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID), loExp);
		}
		return loUpdateStatus;
	}

	/**
	 * <p>
	 * This method updates competition pool status for a particular evaluation
	 * group based on Auth status
	 * <ul>
	 * <li>1. Updates competition pool status for a given evaluation pool
	 * mapping id by calling method <b>updateEvalPoolMappingStatus</b></li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoDataMap - Input parameters Map
	 * @param aoAuthStatusFlag - flag depicting execution of previous services
	 * @return flag depicting update status
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean updateEvalPoolMappingStatus(SqlSession aoMybatisSession, Map<String, Object> aoDataMap,
			Boolean aoAuthStatusFlag) throws ApplicationException
	{
		Boolean loUpdateStatus = false;
		if (aoAuthStatusFlag)
		{
			loUpdateStatus = updateEvalPoolMappingStatus(aoMybatisSession, aoDataMap);
		}
		return loUpdateStatus;
	}

	/**
	 * <p>
	 * This method updates evaluation group status for a particular procurement
	 * based on Auth status
	 * <ul>
	 * <li>1. Updates evaluation group status for a given procurement id by
	 * calling method <b>updateEvalGroupStatus</b></li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoInputParam - Input parameters Map
	 * @param aoAuthStatusFlag - flag depicting execution of previous services
	 * @return loUpdateStatus - flag depicting update status
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean updateEvalGroupStatus(SqlSession aoMyBatisSession, Map<String, Object> aoInputParam,
			Boolean aoAuthStatusFlag) throws ApplicationException
	{
		Boolean loUpdateStatus = false;
		if (aoAuthStatusFlag)
		{
			loUpdateStatus = updateEvalGroupStatus(aoMyBatisSession, aoInputParam);
		}
		return loUpdateStatus;
	}

	/**
	 * <p>
	 * This method updates procurement status for a particular procurement based
	 * on Auth status
	 * <ul>
	 * <li>1. Updates procurement status for a given procurement id by calling
	 * method <b>updateProcurementStatusBasedOnGroup</b></li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoDataMap - Input parameters Map
	 * @param aoAuthStatusFlag - flag depicting execution of previous services
	 * @return loUpdateStatus - flag depicting update status
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean updateProcurementStatusBasedOnGroup(SqlSession aoMybatisSession, Map<String, Object> aoInputParam,
			Boolean aoAuthStatusFlag) throws ApplicationException
	{
		Boolean loUpdateStatus = false;
		if (aoAuthStatusFlag)
		{
			loUpdateStatus = updateProcurementStatusBasedOnGroup(aoMybatisSession, aoInputParam);
		}
		return loUpdateStatus;
	}

	/**
	 * This method fetches the list of evaluation group award summary for a
	 * given procurement id
	 * 
	 * <ul>
	 * <li>1. Fetches evaluation group awards summary for a given procurement id
	 * using <b>fetchEvaluationGroupAwardsList</b> from awards mapper</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoEvaluationGroupAwardBean - EvaluationGroupAwardBean properties
	 * @return loEvaluationGroupAwardList - list of evaluation group awards
	 *         summary
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<EvaluationGroupAwardBean> fetchEvaluationGroupAwards(SqlSession aoMybatisSession,
			EvaluationGroupAwardBean aoEvaluationGroupAwardBean) throws ApplicationException
	{
		List<EvaluationGroupAwardBean> loEvaluationGroupAwardList = null;
		try
		{
			loEvaluationGroupAwardList = (List<EvaluationGroupAwardBean>) DAOUtil.masterDAO(aoMybatisSession,
					aoEvaluationGroupAwardBean, HHSConstants.MAPPER_CLASS_AWARDS_MAPPER,
					HHSConstants.FETCH_EVALUATION_GROUP_AWARD_LIST, HHSConstants.COM_NYC_HHS_MODEL_EVAL_GROUP_AWARD);
			setMoState("Successfully fetched evaluation group awards summary for procurement Id:"
					+ aoEvaluationGroupAwardBean.getProcurementId());
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error occurred while fetching Evaluation Group Awards summary for procurement id :"
					+ aoEvaluationGroupAwardBean.getProcurementId());
			LOG_OBJECT.Error("Error while fetching Evaluation Group Awards summary for procurement id :"
					+ aoEvaluationGroupAwardBean.getProcurementId(), loExp);
			throw loExp;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (Exception loExp)
		{
			setMoState("Error occurred while fetching Evaluation Group Awards summary for procurement id :"
					+ aoEvaluationGroupAwardBean.getProcurementId());
			LOG_OBJECT.Error("Error while fetching Evaluation Group Awards summary for procurement id :"
					+ aoEvaluationGroupAwardBean.getProcurementId(), loExp);
			throw new ApplicationException("Error while fetching Evaluation Group Awards summary for procurement id :"
					+ aoEvaluationGroupAwardBean.getProcurementId(), loExp);
		}
		return loEvaluationGroupAwardList;
	}

	/**
	 * This method returns evaluation group awards count for a input procurement
	 * id
	 * 
	 * <ul>
	 * <li>1. Fetches count of evaluation group awards for a procurement id
	 * using <b>fetchEvaluationGroupAwardsListCount</b> from awards mapper</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProcurementId - Procurement Id
	 * @return count of evaluation group awards
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Integer fetchEvaluationGroupAwardsCount(SqlSession aoMybatisSession, String asProcurementId)
			throws ApplicationException
	{
		Integer loEvaluationGroupAwardListCount = null;
		try
		{
			loEvaluationGroupAwardListCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, asProcurementId,
					HHSConstants.MAPPER_CLASS_AWARDS_MAPPER, HHSConstants.FETCH_EVALUATION_GROUP_AWARD_LIST_COUNT,
					HHSConstants.JAVA_LANG_STRING);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error(
					"Error while fetching Evaluation Group Award count for procurement id :" + asProcurementId, loExp);
			setMoState("Error occurred while fetching Evaluation Group Award count for procurement id :"
					+ asProcurementId);
			throw loExp;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (Exception loExp)
		{
			LOG_OBJECT.Error(
					"Error while fetching Evaluation Group Award count for procurement id :" + asProcurementId, loExp);
			setMoState("Error occurred while fetching Evaluation Group Award count for procurement id :"
					+ asProcurementId);
			throw new ApplicationException("Error while fetching Evaluation Group Award count for procurement id :"
					+ asProcurementId, loExp);
		}
		return loEvaluationGroupAwardListCount;
	}

	/**
	 * 
	 * This method is used to fetch Awards and Contracts Summary for a
	 * particular group of a procurement
	 * 
	 * <ul>
	 * <li>1. Add input parameters to map</li>
	 * <li>2. Fetches Awards and Contracts Summary for a particular group of a
	 * procurement using <b>fetchGroupAwardsContracts</b> from evaluation mapper
	 * </li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoAwardsContractSummaryBean - AwardsContractSummaryBean properties
	 * @return loAwardsContractSummaryList - list of awards and contracts
	 *         summary
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<AwardsContractSummaryBean> fetchGroupAwardsContracts(SqlSession aoMybatisSession,
			AwardsContractSummaryBean aoAwardsContractSummaryBean) throws ApplicationException
	{
		List<AwardsContractSummaryBean> loAwardsContractSummaryList = null;
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, aoAwardsContractSummaryBean.getProcurementId());
		loContextDataMap.put(HHSConstants.EVALUATION_GROUP_ID, aoAwardsContractSummaryBean.getEvaluationGroupId());
		try
		{
			loAwardsContractSummaryList = (List<AwardsContractSummaryBean>) DAOUtil.masterDAO(aoMybatisSession,
					aoAwardsContractSummaryBean, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
					HHSConstants.FETCH_GROUP_AWARDS_CONTRACTS, HHSConstants.COM_NYC_HHS_MODEL_AWARD_CONTRACT_SUMMARY);
			setMoState("Successfully fetched Awards and Contracts Summary for evaluation group id:"
					+ aoAwardsContractSummaryBean.getEvaluationGroupId());
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoExp)
		{
			aoExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching Awards and Contracts Summary for evaluation group id:"
					+ aoAwardsContractSummaryBean.getEvaluationGroupId(), aoExp);
			setMoState("Error while fetching Awards and Contracts Summary for evaluation group id:"
					+ aoAwardsContractSummaryBean.getEvaluationGroupId());
			throw aoExp;
		}
		/**
		 * Any Exception from DAO class will be handles over here. It throws
		 * Application Exception back to Controllers calling method through
		 * Transaction framework
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetching Awards and Contracts Summary for evaluation group id:"
					+ aoAwardsContractSummaryBean.getEvaluationGroupId(), aoExp);
			setMoState("Error while fetching Awards and Contracts Summary for evaluation group id:"
					+ aoAwardsContractSummaryBean.getEvaluationGroupId());
			throw new ApplicationException("Error while fetching Awards and Contracts Summary for evaluation group id:"
					+ aoAwardsContractSummaryBean.getEvaluationGroupId(), aoExp);
		}
		return loAwardsContractSummaryList;
	}

	/**
	 * This method is used to fetch Awards and contracts count or a particular
	 * group of a procurement
	 * 
	 * <ul>
	 * <li>1. Add input parameters to map</li>
	 * <li>2. Fetches Awards and Contracts count for a particular group of a
	 * procurement using <b>fetchGroupAwardsContractsCount</b> from evaluation
	 * mapper</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoAwardsContractSummaryBean - AwardsContractSummaryBean properties
	 * @return count of Awards and Contracts for a particular evaluation group
	 *         id Integer
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Integer fetchGroupAwardsContractsCount(SqlSession aoMybatisSession,
			AwardsContractSummaryBean aoAwardsContractSummaryBean) throws ApplicationException
	{
		Integer loEvaluationSummaryCount = null;
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, aoAwardsContractSummaryBean.getProcurementId());
		loContextDataMap.put(HHSConstants.EVALUATION_GROUP_ID, aoAwardsContractSummaryBean.getEvaluationGroupId());
		try
		{
			loEvaluationSummaryCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoAwardsContractSummaryBean,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_GROUP_AWARDS_CONTRACTS_COUNT,
					HHSConstants.COM_NYC_HHS_MODEL_AWARD_CONTRACT_SUMMARY);
			setMoState("Successfully fetched Awards and Contracts count for evaluation group id:"
					+ aoAwardsContractSummaryBean.getEvaluationGroupId());
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoExp)
		{
			aoExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching Awards and Contracts count for evaluation group id:"
					+ aoAwardsContractSummaryBean.getEvaluationGroupId(), aoExp);
			setMoState("Error while fetching Awards and Contracts count for evaluation group id:"
					+ aoAwardsContractSummaryBean.getEvaluationGroupId());
			throw aoExp;
		}
		/**
		 * Any Exception from DAO class will be handles over here. It throws
		 * Application Exception back to Controllers calling method through
		 * Transaction framework
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetching Awards and Contracts count for evaluation group id:"
					+ aoAwardsContractSummaryBean.getEvaluationGroupId(), aoExp);
			setMoState("Error while fetching Awards and Contracts count for evaluation group id:"
					+ aoAwardsContractSummaryBean.getEvaluationGroupId());
			throw new ApplicationException("Error while fetching Awards and Contracts count for evaluation group id:"
					+ aoAwardsContractSummaryBean.getEvaluationGroupId(), aoExp);
		}
		return loEvaluationSummaryCount;
	}

	/**
	 * This method fetches the List of Selected Details Summary for all selected
	 * proposals of a procurement
	 * 
	 * <ul>
	 * <li>1. Fetches list of Selection Details for competition pools of all
	 * groups of a procurement using <b>fetchSelectionDetailsSummaryList</b>
	 * from Awards mapper</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoSelectionDetailsSummaryBean - SelectionDetailsSummaryBean
	 *            properties
	 * @return list of selection details summary
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<SelectionDetailsSummaryBean> fetchGroupSelectionDetails(SqlSession aoMybatisSession,
			SelectionDetailsSummaryBean aoSelectionDetailsSummaryBean) throws ApplicationException
	{
		List<SelectionDetailsSummaryBean> loSelectionDetailsSummaryBeanList = null;
		try
		{
			loSelectionDetailsSummaryBeanList = (List<SelectionDetailsSummaryBean>) DAOUtil.masterDAO(aoMybatisSession,
					aoSelectionDetailsSummaryBean, HHSConstants.MAPPER_CLASS_AWARDS_MAPPER,
					HHSConstants.FETCH_SELECTION_DETAILS_SUMMARY_LIST,
					HHSConstants.COM_NYC_HHS_MODEL_SELECTION_DETAIL_SUMMARY);
			setMoState("Successfully  fetched Selected Details Summary for procurement id:"
					+ aoSelectionDetailsSummaryBean.getProcurementId());
		}
		/**
		 * Any Exception from DAO class will be handles over here. It throws
		 * Application Exception back to Controllers calling method through
		 * Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("Error while fetching Selection Details Summary for procurement id :"
					+ aoSelectionDetailsSummaryBean.getProcurementId(), loExp);
			setMoState("Error while fetching Selection Details Summary for procurement id :"
					+ aoSelectionDetailsSummaryBean.getProcurementId());
			throw loExp;
		}
		/**
		 * Any Exception from DAO class will be handles over here. It throws
		 * Application Exception back to Controllers calling method through
		 * Transaction framework
		 */
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error while fetching Selection Details Summary for procurement id :"
					+ aoSelectionDetailsSummaryBean.getProcurementId(), loExp);
			setMoState("Error while fetching Selection Details Summary for procurement id :"
					+ aoSelectionDetailsSummaryBean.getProcurementId());
			throw new ApplicationException("Error while fetching Selection Details Summary for procurement id :"
					+ aoSelectionDetailsSummaryBean.getProcurementId(), loExp);
		}
		return loSelectionDetailsSummaryBeanList;
	}

	/**
	 * This method fetches the total count of selection details list Summary for
	 * a given procurement id
	 * 
	 * <ul>
	 * <li>1. Fetches total count of selection details using
	 * <b>fetchSelectionDetailsSummaryListCount</b> from awards mapper</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProcurementId - Procurement Id
	 * @return count of selection details - loGroupSelectionDetails List Count
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Integer fetchGroupSelectionDetailsCount(SqlSession aoMybatisSession, String asProcurementId)
			throws ApplicationException
	{
		Integer loGroupSelectionDetailsListCount = null;
		try
		{
			loGroupSelectionDetailsListCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, asProcurementId,
					HHSConstants.MAPPER_CLASS_AWARDS_MAPPER, HHSConstants.FETCH_SELECTION_DETAILS_SUMMARY_LIST_COUNT,
					HHSConstants.JAVA_LANG_STRING);
			setMoState("Successfully fetched selection details summary count for procurement id:" + asProcurementId);
		}
		/**
		 * Any Exception from DAO class will be handles over here. It throws
		 * Application Exception back to Controllers calling method through
		 * Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("Error while fetching the total of Selection Details Summary count for procurement id :"
					+ asProcurementId, loExp);
			setMoState("Error while fetching the total of Selection Details Summary count for procurement id :"
					+ asProcurementId);
			throw loExp;
		}
		/**
		 * Any Exception from DAO class will be handles over here. It throws
		 * Application Exception back to Controllers calling method through
		 * Transaction framework
		 */
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error while fetching the total of Selection Details Summary count for procurement id :"
					+ asProcurementId, loExp);
			setMoState("Error while fetching the total of Selection Details Summary count for procurement id :"
					+ asProcurementId);
			throw new ApplicationException(
					"Error while fetching the total of Selection Details Summary List for procurement id :"
							+ asProcurementId, loExp);
		}
		return loGroupSelectionDetailsListCount;
	}

	/**
	 * This method is used to fetch procurement title details or competition
	 * pool title details based on query Id
	 * 
	 * <ul>
	 * <li>1. Fetches procurement title details or competition pool title
	 * details based on query Id from procurement mapper</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asQueryId - Query Id
	 * @param asInputParam - Input parameters map
	 * @param asProcurementTitle - Procurement Title
	 * @return list of map of competition pool details
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, String>> fetchTypeAheadNameList(SqlSession aoMybatisSession, String asQueryId,
			String asInputParam, String asProcurementTitle) throws ApplicationException
	{
		List<Map<String, String>> loFetchedList = null;
		Map<String, String> loDataMap = new HashMap<String, String>();
		loDataMap.put(HHSConstants.PROCUREMENT_TITLE, asProcurementTitle);
		try
		{
			// check whether the query string is null or not
			if (null != asQueryId)
			{
				loDataMap.put(HHSConstants.INPUT_PARAM_MAP, HHSConstants.PERCENT + asInputParam.toLowerCase()
						+ HHSConstants.PERCENT);
				loFetchedList = (List<Map<String, String>>) DAOUtil.masterDAO(aoMybatisSession, loDataMap,
						HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, asQueryId, HHSConstants.JAVA_UTIL_MAP);
			}
			else
			{
				throw new ApplicationException("Query id can not be null to fetch typehead data");
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(loDataMap);
			LOG_OBJECT.Error(
					"Exception occured while fetchin data in fetchTypeAheadNameList method in CompetitionPoolService ",
					aoAppEx);
			setMoState("Transaction Failed:: Exception occured while fetchin data in fetchTypeAheadNameList method in CompetitionPoolService queryid: \n"
					+ asQueryId);
			throw aoAppEx;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (Exception aoEx)
		{
			LOG_OBJECT
					.Error("Exception occured while fetching data in fetchTypeAheadNameList method in CompetitionPoolService ",
							aoEx);
			setMoState("Transaction Failed:: Exception occured while fetching data in fetchTypeAheadNameList method in CompetitionPoolService queryid: \n"
					+ asQueryId);
			throw new ApplicationException(
					"Exception occured while fetching data in fetchTypeAheadNameList method in CompetitionPoolService ",
					aoEx);
		}
		return loFetchedList;
	}

	/**
	 * <p>
	 * This method updates competition pool status for particular evaluation
	 * group of a procurement
	 * <ul>
	 * <li>Fetches list of submitted proposal count against competition pool id
	 * by executing query id <b>fetchEvaluationGroupStatus</b></li>
	 * <li>Execute query id <b> getProposalCountAndCompId </b></li>
	 * <li>Iterate through the list of proposal count against competition pool
	 * id</li>
	 * <li>Check if proposal count is 0 for a competition pool, set competition
	 * pool status as "No Proposals" else set as "Proposals Received"</li>
	 * <li>Updates competition pool status using
	 * <b>updateCompPoolStatusInPropResubmit</b> from evaluation mapper</li>
	 * <li>Insert into competetion pool using<b>hhsauditInsertForCompPool</b></li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoDataMap - Input parameters Map
	 * @param aoUpdateStatus - boolean
	 * @return flag depicting update status
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings(
	{ "unchecked" })
	public Boolean updateCompPoolStatusInPropResubmit(SqlSession aoMybatisSession, Map<String, Object> aoDataMap,
			Boolean aoUpdateStatus) throws ApplicationException
	{
		Boolean loUpdateStatus = false;
		List<Map<String, Object>> loCompetitionPoolMap = null;
		Map<String, Object> aoStatusValMap = new HashMap<String, Object>();
		try
		{
			if (null != aoUpdateStatus && aoUpdateStatus)
			{
				String lsEvalGroupStatus = (String) DAOUtil.masterDAO(aoMybatisSession, aoDataMap,
						HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_EVALUATION_GROUP_STATUS,
						HHSConstants.JAVA_UTIL_MAP);
				if (null != lsEvalGroupStatus
						&& lsEvalGroupStatus.equalsIgnoreCase(PropertyLoader.getProperty(
								HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_EVALUATION_GROUP_PROPOSALS_RECEIVED)))
				{
					loCompetitionPoolMap = (List<Map<String, Object>>) DAOUtil.masterDAO(aoMybatisSession, aoDataMap,
							HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.GET_PROPOSAL_COUNT_AND_COMP_ID,
							HHSConstants.JAVA_UTIL_MAP);
					if (null != loCompetitionPoolMap)
					{
						loUpdateStatus = updateCompPoolFinal(aoMybatisSession, aoDataMap, loCompetitionPoolMap,
								aoStatusValMap);
					}
				}
			}
			setMoState("Competition Pool Status updated sucessfully for proposal id:"
					+ aoDataMap.get(HHSConstants.PROPOSAL_ID));
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.setContextData(aoDataMap);
			setMoState("Error while updating comp pool status for proposal id:"
					+ aoDataMap.get(HHSConstants.PROPOSAL_ID));
			LOG_OBJECT.Error(
					"Error while updating comp pool status for proposal id:" + aoDataMap.get(HHSConstants.PROPOSAL_ID),
					loExp);
			throw loExp;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (Exception loExp)
		{
			setMoState("Error while updating comp pool status for proposal id:"
					+ aoDataMap.get(HHSConstants.PROPOSAL_ID));
			LOG_OBJECT.Error(
					"Error while updating comp pool status for proposal id:" + aoDataMap.get(HHSConstants.PROPOSAL_ID),
					loExp);
			throw new ApplicationException("Error while updating comp pool status for proposal id:"
					+ aoDataMap.get(HHSConstants.PROPOSAL_ID), loExp);
		}
		return loUpdateStatus;
	}

	/**
	 * This method is used to update comp pool status executing
	 * <b>updateCompPoolStatusInPropResubmit</b> query in evaluation mapper
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoDataMap - Input parameters Map
	 * @param loCompetitionPoolMap - Input parameters Map
	 * @param aoStatusValMap - Input parameters Map
	 * @return flag depicting update status
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	private Boolean updateCompPoolFinal(SqlSession aoMybatisSession, Map<String, Object> aoDataMap,
			List<Map<String, Object>> loCompetitionPoolMap, Map<String, Object> aoStatusValMap)
			throws ApplicationException
	{
		Boolean loUpdateStatus;
		for (Map<String, Object> loResultMap : loCompetitionPoolMap)
		{
			int liBigDecimalInitializer = 0;
			if (loResultMap != null)
			{
				if (loResultMap.get(HHSConstants.PROPOSAL_COUNT_TABLE_COL).equals(
						new BigDecimal(liBigDecimalInitializer)))
				{
					aoStatusValMap.put(HHSConstants.COMP_POOL_STATUS,
							aoDataMap.get(HHSConstants.STATUS_COMPETITION_POOL_NO_PROPOSALS));
				}
				else
				{
					aoStatusValMap.put(HHSConstants.COMP_POOL_STATUS,
							aoDataMap.get(HHSConstants.STATUS_COMPETITION_POOL_PROPOSALS_RECEIVED));
				}
				aoStatusValMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID,
						loResultMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID_COL));
				Integer loCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoStatusValMap,
						HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
						HHSConstants.UPDATE_COMP_POOL_STATUS_IN_PROP_RESUBMIT, HHSConstants.JAVA_UTIL_MAP);
				if (loCount > 0)
				{
					// Condition added for audit entry change
					HhsAuditBean aoAudit = new HhsAuditBean();
					aoAudit.setEvalGrp((String) aoDataMap.get(HHSConstants.IS_EVAL_GRP));
					aoAudit.setEntityId(loResultMap.get(HHSConstants.EVALUATION_POOL_MAPPING_ID_COL).toString());
					aoAudit.setEventName(HHSConstants.SUBMIT);
					aoAudit.setEventType(HHSConstants.PROPOSAL_SUBMIT);
					aoAudit.setUserId((String) aoDataMap.get(HHSConstants.USER_ID));
					DAOUtil.masterDAO(aoMybatisSession, aoAudit, HHSConstants.MAPPER_CLASS_HHS_AUDIT_MAPPER,
							HHSConstants.HHSAUDIT_INSERT_FOR_COMP_POOL, HHSConstants.HHS_AUDIT_BEAN_PATH);
				}
			}
		}
		loUpdateStatus = true;
		return loUpdateStatus;
	}

	/**
	 * This method fetches Competition Pool Status
	 * <ul>
	 * <li>Execute query <b>getCompetitionPoolStatus </b></li>
	 * </ul>
	 * @param aoMybatisSession - SqlSession
	 * @param asEvaluationPoolMappingId - string
	 * @return lsPoolStatus - string
	 * @throws ApplicationException
	 */
	public String getCompetitionPoolStatus(SqlSession aoMybatisSession, String asEvaluationPoolMappingId)
			throws ApplicationException
	{
		String lsPoolStatus = null;
		Map<String, String> loDataMap = new HashMap<String, String>();
		loDataMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
		try
		{
			if (null != asEvaluationPoolMappingId)
			{
				lsPoolStatus = (String) DAOUtil.masterDAO(aoMybatisSession, loDataMap,
						HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.GET_COMPETITION_POOL_STATUS,
						HHSConstants.JAVA_UTIL_MAP);
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(loDataMap);
			LOG_OBJECT
					.Error("Exception occured while fetchin data in getCompetitionPoolStatus method in CompetitionPoolService ",
							aoAppEx);
			setMoState("Transaction Failed:: Exception occured while fetchin data in getCompetitionPoolStatus method in CompetitionPoolService queryid");
			throw aoAppEx;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (Exception aoEx)
		{
			LOG_OBJECT
					.Error("Exception occured while fetching data in getCompetitionPoolStatus method in CompetitionPoolService",
							aoEx);
			setMoState("Transaction Failed:: Exception occured while fetching data in getCompetitionPoolStatus method in CompetitionPoolService");
			throw new ApplicationException(
					"Exception occured while fetching data in getCompetitionPoolStatus method in CompetitionPoolService ",
					aoEx);
		}
		return lsPoolStatus;
	}

	/**
	 * Modified as a part of release 3.1.0 for enhancement request 6024 This
	 * method fetches Evaluation Group Count for which submissions has been
	 * closed corresponding to a procurement
	 * <ul>
	 * <li>Execute query <b>checkIfPublishedReleased </b></li>
	 * </ul>
	 * @param aoMybatisSession - SqlSession
	 * @param asProcurementId - string
	 * @return lsPoolStatus - string
	 * @throws ApplicationException
	 */
	public Integer checkIfPublishedReleased(SqlSession aoMybatisSession, String asProcurementId)
			throws ApplicationException
	{
		Integer loEvalGroupCount = null;
		Map<String, String> loDataMap = new HashMap<String, String>();
		loDataMap.put(HHSConstants.PROCUREMENT_ID, asProcurementId);
		try
		{
			loEvalGroupCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, asProcurementId,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.CHECK_IF_PUBLISHED_RELEASED,
					HHSConstants.JAVA_LANG_STRING);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(loDataMap);
			LOG_OBJECT
					.Error("Exception occured while fetchin data in checkIfPublishedReleased method in CompetitionPoolService ",
							aoAppEx);
			setMoState("Transaction Failed:: Exception occured while fetchin data in checkIfPublishedReleased method in CompetitionPoolService queryid");
			throw aoAppEx;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (Exception aoEx)
		{
			LOG_OBJECT
					.Error("Exception occured while fetching data in checkIfPublishedReleased method in CompetitionPoolService",
							aoEx);
			setMoState("Transaction Failed:: Exception occured while fetching data in checkIfPublishedReleased method in CompetitionPoolService");
			throw new ApplicationException(
					"Exception occured while fetching data in checkIfPublishedReleased method in CompetitionPoolService ",
					aoEx);
		}
		return loEvalGroupCount;
	}

	/**
	 * This method checks if procurements award has been approved i.e rule EA8
	 * <ul>
	 * <li>1. Retrieve Evaluation pool mapping Id and populate them in the
	 * HashMap</li>
	 * <li>2. Execute query <b>checkIfAwardApprovedForEvalPool</b> to fetch the
	 * count</li>
	 * <li>3. If count is 0 then return false else return true</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - SqlSession
	 * @param asEvaluationPoolMappingId - Evaluation pool mapping id
	 * @return flag if procurement has approved award
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public boolean checkIfAwardApprovedForEvalPool(SqlSession aoMybatisSession, String asEvaluationPoolMappingId)
			throws ApplicationException
	{
		Map<String, String> loContextDataMap = new HashMap<String, String>();
		loContextDataMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
		loContextDataMap.put(HHSConstants.STATUS_AWARD_REVIEW_APPROVED, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_AWARD_REVIEW_APPROVED));
		LOG_OBJECT.Debug("Entered into CompetitionPoolService:checkIfAwardApprovedForEvalPool method::"
				+ loContextDataMap.toString());
		int liCount = HHSConstants.INT_ZERO;
		try
		{
			if (asEvaluationPoolMappingId != null)
			{
				liCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loContextDataMap,
						HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
						HHSConstants.CHECK_IF_AWARD_APPROVED_FOR_EVAL_POOL, HHSConstants.JAVA_UTIL_MAP);
			}
			setMoState("Transaction passed:: CompetitionPoolService:checkIfAwardApprovedForEvalPool method");
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handled over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			// Catch the exception thrown by masterDao method and pass the
			// caught exception with input params to controller
			loAppEx.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Transaction Failed:: CompetitionPoolService:checkIfAwardApprovedForEvalPool method",
					loAppEx);
			setMoState("Transaction Failed:: CompetitionPoolService:checkIfAwardApprovedForEvalPool method");
			throw loAppEx;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Transaction Failed:: CompetitionPoolService:checkIfAwardApprovedForEvalPool method",
					loExp);
			setMoState("Transaction Failed:: CompetitionPoolService:checkIfAwardApprovedForEvalPool method");
			throw new ApplicationException(
					"Transaction Failed:: CompetitionPoolService:checkIfAwardApprovedForEvalPool method", loExp);
		}
		return (liCount != HHSConstants.INT_ZERO);
	}

	/**
	 * <p>
	 * This method assigns main competition pool id to the proposal when
	 * submitting a proposal to a procurement for a given procurement id and
	 * proposal id
	 * <ul>
	 * <li>1. Add multiple input parameters to a Map</li>
	 * <li>2. Move comp pool id from temp to main pool id for the proposal using
	 * <b>moveCompetitionPoolIdFromTemp</b> from proposal mapper</li>
	 * </ul>
	 * </p>
	 * @param aoMyBatisSession mybatis SQL session
	 * @param asProposalId a string value of proposal Id
	 * @param asProcurementId a string value of procurement Id
	 * @param aoValidateStatus status depicting success of previous services in
	 *            a transaction
	 * @return loAssignGroupStatus - boolean value indicating Assign Group
	 *         Status
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Boolean moveCompetitionPoolIdFromTemp(SqlSession aoMyBatisSession, String asProposalId,
			String asProcurementId, Boolean aoValidateStatus) throws ApplicationException
	{
		Boolean loAssignGroupStatus = false;
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		loContextDataMap.put(HHSConstants.PROPOSAL_ID_KEY, asProposalId);
		try
		{
			if (null != aoValidateStatus && aoValidateStatus)
			{
				DAOUtil.masterDAO(aoMyBatisSession, loContextDataMap, HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER,
						HHSConstants.MOVE_COMPETITION_POOL_FROM_TEMP, HHSConstants.JAVA_UTIL_MAP);
				loAssignGroupStatus = true;
				setMoState("Successfully moved competition pool id from temp to main column for proposal id:"
						+ asProposalId);
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(loContextDataMap);
			LOG_OBJECT
					.Error("Error moved competition pool id from temp to main column for proposal id:" + asProposalId,
							aoAppEx);
			setMoState("Error moved competition pool id from temp to main column for proposal id:" + asProposalId);
			throw aoAppEx;
		}
		/**
		 * Any Exception from DAO class will be handles over here. It throws
		 * Application Exception back to Controllers calling method through
		 * Transaction framework
		 */
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error(
					"Error moved competition pool id from temp to main column for proposal id:" + asProposalId, aoEx);
			setMoState("Error moved competition pool id from temp to main column for proposal id:" + asProposalId);
			throw new ApplicationException("Error moved competition pool id from temp to main column for proposal id:"
					+ asProposalId, aoEx);
		}
		return loAssignGroupStatus;
	}

	/**
	 * <p>
	 * This method fetches all evaluation groups corresponding to a procurement
	 * id
	 * <ul>
	 * <li>1. Fetches evaluation groups corresponding to a procurement id using
	 * <b>fetchEvaluationGroupsWithAwards</b> from evaluation mapper</li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProcurementId - Procurement Id
	 * @return list of all evaluation groups
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, String>> fetchEvaluationGroupsWithAwards(SqlSession aoMybatisSession, String asProcurementId)
			throws ApplicationException
	{
		List<Map<String, String>> loEvaluationGroups = null;
		Map<String, String> loDataMap = new HashMap<String, String>();
		loDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		try
		{
			loEvaluationGroups = (List<Map<String, String>>) DAOUtil.masterDAO(aoMybatisSession, loDataMap,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_EVALUATION_GROUPS_WITH_AWARDS,
					HHSConstants.JAVA_UTIL_MAP);
			setMoState("Successfully fetched all evaluation groups for procurement id:" + asProcurementId);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error while fetching the evaluation groups for procurement Id:" + asProcurementId, aoExp);
			setMoState("Error while fetching the evaluation groups for procurement Id:" + asProcurementId);
			throw aoExp;
		}
		/**
		 * Any Exception from DAO class will be handles over here. It throws
		 * Application Exception back to Controllers calling method through
		 * Transaction framework
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetching the evaluation groups for procurement Id:" + asProcurementId, aoExp);
			setMoState("Error while fetching the evaluation groups for procurement Id:" + asProcurementId);
			throw new ApplicationException("Error while fetching the evaluation groups for procurement Id:"
					+ asProcurementId, aoExp);
		}
		return loEvaluationGroups;
	}

	/**
	 * <p>
	 * This method fetches evaluation group id corresponding to a procurement id
	 * <ul>
	 * <li>1. Add input parameters to a Map</li>
	 * <li>2. Fetches evaluation group id corresponding to a procurement id
	 * using <b>fetchEvaluationGroupId</b> from evaluation mapper</li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asEvaluationGroupId - Evaluation group Id
	 * @param asEvaluationPoolMappingId - Evaluation Pool Mapping Id
	 * @return evaluation group id - string
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public String fetchEvalGroupIdFromPoolMappingId(SqlSession aoMybatisSession, String asEvaluationGroupId,
			String asEvaluationPoolMappingId) throws ApplicationException
	{
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.EVALUATION_GROUP_ID, asEvaluationGroupId);
		loContextDataMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
		try
		{
			if (null == asEvaluationGroupId || asEvaluationGroupId.isEmpty())
			{
				asEvaluationGroupId = (String) DAOUtil.masterDAO(aoMybatisSession, asEvaluationPoolMappingId,
						HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_EVAL_GROUP_ID_FROM_POOL_ID,
						HHSConstants.JAVA_LANG_STRING);
			}
			setMoState("Successfully fetched evaluation group id for a given Evaluation Pool Mapping id:"
					+ asEvaluationPoolMappingId);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoExp)
		{
			aoExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching evaluation group id for a given Evaluation Pool Mapping id:"
					+ asEvaluationPoolMappingId, aoExp);
			setMoState("Error while fetching evaluation group id for a given Evaluation Pool Mapping id:"
					+ asEvaluationPoolMappingId);
			throw aoExp;
		}
		/**
		 * Any Exception from DAO class will be handles over here. It throws
		 * Application Exception back to Controllers calling method through
		 * Transaction framework
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetching evaluation group id for a given Evaluation Pool Mapping id:"
					+ asEvaluationPoolMappingId, aoExp);
			setMoState("Error while fetching evaluation group id for a given Evaluation Pool Mapping id:"
					+ asEvaluationPoolMappingId);
			throw new ApplicationException(
					"Error while fetching evaluation group id for a given Evaluation Pool Mapping id:"
							+ asEvaluationPoolMappingId, aoExp);
		}
		return asEvaluationGroupId;
	}

	/**
	 * This method is used to update competiton pool and evaluation group status
	 * to non responsive for input evaluation pool mapping id
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. Execute the query <code>fetchNotNonResponsivePropCount</code> from
	 * <code>evaluation</code> mapper</li>
	 * <li>2. If all proposals in competition pool are non responsive Execute
	 * the query <code>updateEvalPoolMappingStatus</code> from
	 * <code>evaluation</code> mapper to update competition pool status to non
	 * responsive</li>
	 * <li>3. When Competition pool status is updated, Execute the query
	 * <code>fetchNonResponsiveCompPoolCount</code> from <code>evaluation</code>
	 * mapper</li>
	 * <li>4. If all competition pool are either non responsive or proposals
	 * received and count of non responsive is atleast one, Execute the query
	 * <code>updateNonResponsiveEvalGroup</code> from <code>evaluation</code>
	 * mapper to update evaluation group status to non responsive</li>
	 * <li>5. Return the boolean flag true if the update is successful else
	 * return false.</li>
	 * </ul>
	 * 
	 * Created by: Sadhna
	 * 
	 * Change Date: 27 May 2014
	 * 
	 * @param aoMybatisSession SqlSession sql session object
	 * @param asEvalPoolMappingId Evaluation Pool Mapping Id
	 * @return loUpdateFlag Boolean
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean updateNonResponsiveStatus(SqlSession aoMybatisSession, Map<String, Object> aoInputParamMap)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into updateNonResponsiveStatus");
		Boolean loUpdateFlag = Boolean.FALSE;
		Integer loUpdatedCompPoolCount = null;
		Integer loNonResponsivePoolCount = null;
		Integer loUpdatedEvalGroupCount = null;
		Boolean loUpdatedGrpFlag = Boolean.FALSE;
		try
		{
			aoInputParamMap.put(HHSConstants.STATUS_PROPOSAL_DRAFT, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_DRAFT));
			aoInputParamMap.put(HHSConstants.STATUS_PROPOSAL_NON_RESPONSIVE, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_NON_RESPONSIVE));
			aoInputParamMap.put(HHSConstants.COMPETITION_POOL_STATUS, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_COMPETITION_POOL_NON_RESPONSIVE));
			aoInputParamMap.put(HHSConstants.STATUS_COMPETITION_POOL_NON_RESPONSIVE, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_COMPETITION_POOL_NON_RESPONSIVE));
			aoInputParamMap.put(HHSConstants.STATUS_COMPETITION_POOL_NO_PROPOSALS, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_COMPETITION_POOL_NO_PROPOSALS));
			aoInputParamMap.put(HHSConstants.STATUS_EVALUATION_GROUP_NON_RESPONSIVE, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_EVALUATION_GROUP_NON_RESPONSIVE));
			Integer loPropCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoInputParamMap,
					HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_NOT_NON_RESPONSIVE_PROP_COUNT,
					HHSConstants.JAVA_UTIL_MAP);
			if (null != loPropCount && loPropCount == 0)
			{
				loUpdatedCompPoolCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoInputParamMap,
						HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.UPDATE_EVAL_POOL_MAPPING_STATUS,
						HHSConstants.JAVA_UTIL_MAP);
			}
			if (null != loUpdatedCompPoolCount && loUpdatedCompPoolCount > 0)
			{
				loNonResponsivePoolCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoInputParamMap,
						HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_NON_RESPONSIVE_COMP_POOL_COUNT,
						HHSConstants.JAVA_UTIL_MAP);
				if (null != loNonResponsivePoolCount && loNonResponsivePoolCount > 0)
				{
					loUpdatedEvalGroupCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoInputParamMap,
							HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.UPDATE_NON_RESPONSIVE_EVAL_GROUP,
							HHSConstants.JAVA_UTIL_MAP);
				}
				else
				{
					loUpdatedGrpFlag = updateEvalGroupStatus(aoMybatisSession, aoInputParamMap);
				}
				if ((null != loUpdatedEvalGroupCount && loUpdatedEvalGroupCount > 0)
						|| (null != loUpdatedGrpFlag && loUpdatedGrpFlag))
				{
					updateProcurementStatusBasedOnGroup(aoMybatisSession, aoInputParamMap);
					loUpdateFlag = Boolean.TRUE;
				}
			}
			setMoState("Successful NonResponsiveStatus Update for evaluation pool mapping Id:" + aoInputParamMap);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error while updating NonResponsiveStatus for evaluation pool mapping Id:"
					+ aoInputParamMap, aoExp);
			setMoState("Error while updating NonResponsiveStatus for evaluation pool mapping Id:" + aoInputParamMap);
			throw aoExp;
		}
		// handling exception other than Application exception
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while updating NonResponsiveStatus for evaluation pool mapping Id:"
					+ aoInputParamMap, aoExp);
			setMoState("Error while updating NonResponsiveStatus for evaluation pool mapping Id:" + aoInputParamMap);
			throw new ApplicationException(
					"Exception while updating NonResponsiveStatus for evaluation pool mapping Id:" + aoInputParamMap,
					aoExp);
		}
		return loUpdateFlag;
	}

	/**
	 * Modified as a part of release 3.1.0 for enhancement request 6024
	 * 
	 * This method is used to update version no in eval group
	 * 
	 * <ul>
	 * <li>1. Execute the query <code>updateEvalGroupWithVersionInfo</code> from
	 * <code>evaluation</code> mapper</li>
	 * <li>2. Return the boolean flag true if the update is successful else
	 * return false.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession sql session object
	 * @param aoDataMap input parameters map
	 * @return loUpdateFlag Boolean
	 * @throws ApplicationException If an ApplicationException occurs
	 */

	public Boolean updateEvalGroupWithVersionInfo(SqlSession aoMybatisSession, Map<String, Object> aoDataMap)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into updateEvalGroupWithVersionInfo");
		Boolean loUpdateFlag = Boolean.FALSE;
		try
		{
			DAOUtil.masterDAO(aoMybatisSession, aoDataMap, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
					HHSR5Constants.UPDATE_EVAL_GROUP_WITH_VERSION_INFO, HHSConstants.JAVA_UTIL_MAP);
			loUpdateFlag = Boolean.TRUE;
			setMoState("Successfully updated Version info for evaluation group on close submission:" + aoDataMap);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error while updating Version info for evaluation group on close submission:" + aoDataMap,
					aoExp);
			setMoState("Error while updating Version info for evaluation group on close submission:" + aoDataMap);
			throw aoExp;
		}
		// handling exception other than Application exception
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while updating Version info for evaluation group on close submission:" + aoDataMap,
					aoExp);
			setMoState("Error while updating Version info for evaluation group on close submission:" + aoDataMap);
			throw new ApplicationException(
					"Error while updating Version info for evaluation group on close submission:" + aoDataMap, aoExp);
		}
		return loUpdateFlag;
	}

	/**
	 * This method is used to insert audit entry for Submit Proposal Changes
	 * done for Defect #6431 for Release 3.1.3 <b>hhsauditProviderInsert</b>
	 * query in HhsAuditMapper
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProposalId - Input parameters Map
	 * @param asUserId - Input parameters Map
	 * @param aoUpdateStatus - Input parameters Map
	 * @return flag depicting insert audit status
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean insertAuditForSubmitProp(SqlSession aoMybatisSession, String asProposalId, String asUserId,
			Boolean aoUpdateStatus) throws ApplicationException
	{
		Boolean loinsertAuditStatus = Boolean.FALSE;
		try
		{
			if (null != aoUpdateStatus && aoUpdateStatus)
			{
				HhsAuditBean aoAudit = new HhsAuditBean();
				aoAudit.setEntityId(asProposalId);
				aoAudit.setEntityType(HHSConstants.PROPOSAL);
				aoAudit.setData(HHSConstants.PROPOSAL_STATUS_CHANGED_TO_SUBMITTED);
				aoAudit.setEventName(HHSConstants.SUBMIT);
				aoAudit.setEventType(HHSConstants.PROPOSAL_SUBMIT);
				aoAudit.setUserId(asUserId);
				DAOUtil.masterDAO(aoMybatisSession, aoAudit, HHSConstants.MAPPER_CLASS_HHS_AUDIT_MAPPER,
						HHSConstants.HHSAUDIT_PROVIDER_INSERT, HHSConstants.HHS_AUDIT_BEAN_PATH);
				loinsertAuditStatus = true;
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error while inserting Audit For Proposal Submit:" + asProposalId, aoExp);
			setMoState("Error while inserting Audit For Proposal Submit:" + asProposalId);
			throw aoExp;
		}
		// handling exception other than Application exception
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while inserting Audit For Proposal Submit:" + asProposalId, aoExp);
			setMoState("Error while inserting Audit For Proposal Submit:" + asProposalId);
			throw new ApplicationException("Error while inserting Audit For Proposal Submit:" + asProposalId, aoExp);
		}
		return loinsertAuditStatus;
	}

	// R5 code starts
	/**
	 * This method is added as a part of Release 5 .This is used to save
	 * Document Configuration.
	 * <ul>
	 * <li>Execute query with Id "insertDefaultDocumentVisibility" from
	 * EvaluationMapper</li>
	 * </ul>
	 * @param aoMybatisSession SQLSession
	 * @param aoInputParam Required Prop Map
	 * @return loInsertStatus Boolean
	 * @throws ApplicationException
	 */
	public Boolean insertDefaultDocumentVisibility(SqlSession aoMybatisSession, Map<String, Object> aoInputParam)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into insertDefaultDocumentVisibility");
		Boolean loInsertStatus = Boolean.FALSE;
		String lsEvaluationGroupId = (String) aoInputParam.get(HHSConstants.EVALUATION_GROUP_ID);
		try
		{
			HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb
					.getInstance().getCacheObject(ApplicationConstants.APPLICATION_SETTING);
			String lsHiddenDocTypes = loApplicationSettingMap.get(HHSR5Constants.HIDDEN_DOC_TYPE_KEY
					+ HHSR5Constants.UNDERSCORE + HHSR5Constants.HIDDEN_DOC_TYPES);
			if (lsHiddenDocTypes != null)
			{
				String[] loHiddenDocTypes = lsHiddenDocTypes.split(HHSR5Constants.DOUBLE_HHSUTIL_DELIM_PIPE);
				aoInputParam.put(HHSR5Constants.HIDDEN_DOC_TYPES, Arrays.asList(loHiddenDocTypes));
			}
			DAOUtil.masterDAO(aoMybatisSession, aoInputParam, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
					HHSR5Constants.INSERT_DEFAULT_DOCUMENT_VISIBILITY, HHSConstants.JAVA_UTIL_MAP);
			loInsertStatus = true;
			setMoState("Successfully inserted Default document visibility :" + aoInputParam);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error while inserting Default document visibility status:" + lsEvaluationGroupId, aoExp);
			setMoState("Error while inserting Default document visibility status:" + lsEvaluationGroupId);
			throw aoExp;
		}
		// handling exception other than Application exception
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while inserting Default document visibility status:" + lsEvaluationGroupId, aoExp);
			setMoState("Error while inserting Default document visibility status:" + lsEvaluationGroupId);
			throw new ApplicationException("Error while inserting Default document visibility status:"
					+ lsEvaluationGroupId, aoExp);
		}
		return loInsertStatus;
	}
	// R5 code ends

}