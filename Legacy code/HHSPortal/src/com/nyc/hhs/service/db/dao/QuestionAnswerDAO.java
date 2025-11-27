package com.nyc.hhs.service.db.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.service.db.services.application.QuestionAnswerMapper;
import com.nyc.hhs.util.DAOUtil;

/**
 * This DAO class provides us with the functionality related to Question and
 * Answers which includes the database operations like deleting, updating and
 * fetching information on the basis of input parameters.
 * 
 */

public class QuestionAnswerDAO
{
	private static final LogInfo LOG_OBJECT = new LogInfo(QuestionAnswerDAO.class);

	/**
	 * This method inserts question answers data in database.
	 * 
	 * @param aoInsertQueryList - map caontaining data to be inserted
	 * @param aoSession - Sql Session
	 * @param asTableName - name of the table
	 * @return - abInsertStatus
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public boolean insert(HashMap aoInsertQueryList, SqlSession aoSession, String asTableName)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Inside insert method of class QuestionAnswerDAO");
		boolean lbInsertStatus = false;
		try
		{
			aoInsertQueryList.put("TABLE", asTableName);
			DAOUtil.masterDAO(aoSession, aoInsertQueryList, ApplicationConstants.MAPPER_CLASS_QUESTION_ANSWER,
					"insertFormInformation", "java.util.HashMap");
			aoInsertQueryList.put("orgId", aoInsertQueryList.get("ORGANIZATION_ID"));
			aoInsertQueryList.put("formName", aoInsertQueryList.get("FORM_NAME"));
			aoInsertQueryList.put("formVersion", aoInsertQueryList.get("FORM_VERSION"));
			aoInsertQueryList.put("subSecId", aoInsertQueryList.get("SUB_SECTION_ID"));
			aoInsertQueryList.put("sectionId", aoInsertQueryList.get("SECTION_ID"));
			if (!asTableName.equalsIgnoreCase("ORGANIZATION"))
			{
				aoInsertQueryList.put("orgTable", "exist");
				aoInsertQueryList.put("bussAppId", aoInsertQueryList.get("BUSINESS_APPLICATION_ID"));
			}
			if (aoInsertQueryList.get("BUSINESS_APPLICATION_ID") != null)
			{
				aoInsertQueryList.put("asStatus", ApplicationConstants.COMPLETED_STATE);
				aoInsertQueryList.put("bappId", "exist");
				int liUpdatedRecords = (Integer) DAOUtil.masterDAO(aoSession, aoInsertQueryList,
						ApplicationConstants.MAPPER_CLASS_QUESTION_ANSWER, "updateSubSectionDetails",
						"java.util.HashMap");
				if (liUpdatedRecords <= 0)
				{
					DAOUtil.masterDAO(aoSession, aoInsertQueryList, ApplicationConstants.MAPPER_CLASS_QUESTION_ANSWER,
							"insertSubSectionDetails", "java.util.HashMap");
				}
				aoInsertQueryList.put("asBussAppId", aoInsertQueryList.get("BUSINESS_APPLICATION_ID"));
				aoInsertQueryList.put("asUserId", aoInsertQueryList.get("USER_ID"));
				DAOUtil.masterDAO(aoSession, aoInsertQueryList, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"updateBusiAppModifiedDate", "java.util.Map");
			}
			lbInsertStatus = true;
		}
		catch (Exception loEx)
		{
			lbInsertStatus = false;
			LOG_OBJECT.Error("Exception occured while inserting answer data in " + asTableName + " table " + loEx
					+ " for " + aoInsertQueryList, loEx);
			throw new ApplicationException("Exception occured while inserting answer data in table " + asTableName,
					loEx);
		}
		return lbInsertStatus;
	}

	/**
	 * This method updates question answers data in database.
	 * 
	 * @param aoDataToUpdate - map containing data to be inserted
	 * @param aoSession - Sql Session
	 * @param asTableName - name of the table
	 * @return - abInsertStatus
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public boolean update(HashMap aoDataToUpdate, SqlSession aoSession, String asTableName) throws ApplicationException
	{
		LOG_OBJECT.Debug("Inside update method of class QuestionAnswerDAO");
		boolean lbInsertStatus = false;
		try
		{
			aoDataToUpdate.put("TABLE", asTableName);
			StringBuffer loWhereClause = new StringBuffer();
			if (asTableName.equalsIgnoreCase("ORGANIZATION"))
			{
				loWhereClause.append("ORGANIZATION_ID = '");
				loWhereClause.append(aoDataToUpdate.get(ApplicationConstants.ORG_ID));
				loWhereClause.append("'");
			}
			else
			{
				loWhereClause.append("BUSINESS_APPLICATION_ID= '");
				loWhereClause.append(aoDataToUpdate.get(ApplicationConstants.BUIZ_APP_ID));
				loWhereClause.append("' AND ORGANIZATION_ID = '");
				loWhereClause.append(aoDataToUpdate.get(ApplicationConstants.ORG_ID));
				loWhereClause.append("'");
			}
			aoDataToUpdate.put("asStatus", ApplicationConstants.COMPLETED_STATE);
			aoDataToUpdate.put("asWhere", loWhereClause.toString());
			DAOUtil.masterDAO(aoSession, aoDataToUpdate, ApplicationConstants.MAPPER_CLASS_QUESTION_ANSWER,
					"updateFormInfo", "java.util.HashMap");
			aoDataToUpdate.put("orgId", aoDataToUpdate.get("ORGANIZATION_ID"));
			aoDataToUpdate.put("subSecId", aoDataToUpdate.get("SUB_SECTION_ID"));
			aoDataToUpdate.put("sectionId", aoDataToUpdate.get("SECTION_ID"));
			if (aoDataToUpdate.get(ApplicationConstants.BUIZ_APP_ID) != null)
			{
				aoDataToUpdate.put("orgTable", "exist");
				aoDataToUpdate.put("bussAppId", aoDataToUpdate.get(ApplicationConstants.BUIZ_APP_ID));
				int liUpdatedRecords = (Integer) DAOUtil.masterDAO(aoSession, aoDataToUpdate,
						ApplicationConstants.MAPPER_CLASS_QUESTION_ANSWER, "updateSubSectionDetails",
						"java.util.HashMap");
				if (liUpdatedRecords <= 0)
				{
					DAOUtil.masterDAO(aoSession, aoDataToUpdate, ApplicationConstants.MAPPER_CLASS_QUESTION_ANSWER,
							"insertSubSectionDetails", "java.util.HashMap");
				}
				aoDataToUpdate.put("asBussAppId", aoDataToUpdate.get("BUSINESS_APPLICATION_ID"));
				aoDataToUpdate.put("asUserId", aoDataToUpdate.get("USER_ID"));
				DAOUtil.masterDAO(aoSession, aoDataToUpdate, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"updateBusiAppModifiedDate", "java.util.Map");
			}
			
			lbInsertStatus = true;
		}
		catch (Exception loEx)
		{
			lbInsertStatus = false;
			LOG_OBJECT.Error("Exception occured while updating answer data in " + asTableName + " table " + loEx
					+ " for ", loEx);
			throw new ApplicationException("Exception occured while updating answer data in table " + loEx + " for ",
					loEx);
		}
		return lbInsertStatus;
	}

	/**
	 * This method fetch form information from database
	 * 
	 * @param aoMyBatisSession - sql session
	 * @param aoHMWhereClause - map containing where clause
	 * @return - loHMFormValues
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked" })
	public HashMap<String, Object> getFormInformation(SqlSession aoMyBatisSession, Map<String, String> aoHMWhereClause)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Inside getFormInformation method of class QuestionAnswerDAO");
		List<HashMap<String, Object>> loLQuestionAnswer;

		HashMap<String, Object> loHMFormValues = null;
		try
		{

			QuestionAnswerMapper loMapper = aoMyBatisSession.getMapper(QuestionAnswerMapper.class);
			StringBuffer loWhereClauseBuffer = new StringBuffer();
			if (aoHMWhereClause.get("TABLE").equalsIgnoreCase("ORGANIZATION"))
			{
				loWhereClauseBuffer.append("ORGANIZATION_ID = '");
				loWhereClauseBuffer.append(aoHMWhereClause.get(ApplicationConstants.ORG_ID));
				loWhereClauseBuffer.append("'");
			}
			else
			{
				loWhereClauseBuffer.append("BUSINESS_APPLICATION_ID= '");
				loWhereClauseBuffer.append(aoHMWhereClause.get(ApplicationConstants.BUIZ_APP_ID));
				loWhereClauseBuffer.append("' AND ORGANIZATION_ID = '");
				loWhereClauseBuffer.append(aoHMWhereClause.get(ApplicationConstants.ORG_ID));
				loWhereClauseBuffer.append("'");
			}
			aoHMWhereClause.put("asWhere", loWhereClauseBuffer.toString());
			loLQuestionAnswer = loMapper.getFormDetails(aoHMWhereClause);
			if (loLQuestionAnswer != null && !loLQuestionAnswer.isEmpty())
			{
				loHMFormValues = loLQuestionAnswer.get(0);
			}
			else
			{
				loHMFormValues = new HashMap<String, Object>();
			}
			if (aoHMWhereClause.get("TABLE").equalsIgnoreCase("ORGANIZATION"))
			{
				if (aoHMWhereClause.get(ApplicationConstants.BUIZ_APP_ID) != null)
				{
					aoHMWhereClause.put("section", ApplicationConstants.BUSINESS_APPLICATION_SECTION_BASICS);
					aoHMWhereClause.put("subSection", ApplicationConstants.BUZ_APP_SUB_SECTION_QUESTION);
					Map<String, String> loVal = loMapper.getFormDetailsOfOrg(aoHMWhereClause);
					if (loHMFormValues.keySet().size() > 0)
					{
						loHMFormValues.put("FORM_NAME", loVal.get("FORM_NAME"));
						loHMFormValues.put("FORM_VERSION", loVal.get("FORM_VERSION"));
						loHMFormValues.put("FORM_ID", loVal.get("FORM_ID"));
					}
				}
			}
			if (aoHMWhereClause.get("TABLE").equalsIgnoreCase("filing_form"))
			{
				HashMap<String, Object> loVal = loMapper.getCorpStructureValue(aoHMWhereClause);
				if (loVal != null)
				{
					loHMFormValues.put("basic_cs_value", loVal.get("CORPORATE_STRUCTURE_ID"));
				}
			}
		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Exception occured while selecting answer data from " + aoHMWhereClause.get("TABLE")
					+ " table " + loEx + " for ", loEx);
			throw new ApplicationException("Exception occured while selecting answer data from Basicform table " + loEx
					+ " for ", loEx);
		}

		return loHMFormValues;
	}

	/**
	 * This method fetch form information from database
	 * 
	 * @param aoMyBatisSession - sql session
	 * @param asBuisAppId - current business application id
	 * @param asOrgId - current organization id
	 * @param asTableName
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public HashMap<String, Object> getFormData(SqlSession aoMyBatisSession, String asBuisAppId, String asOrgId,
			String asTableName) throws ApplicationException
	{
		LOG_OBJECT.Debug("Inside getFormData method of class QuestionAnswerDAO");
		List<HashMap<String, Object>> loLQuestionAnswer;
		HashMap loHMWhereClause = new HashMap();
		HashMap<String, Object> loHMFormValues = null;
		StringBuffer loWhereClauseSB = new StringBuffer();
		try
		{
			loHMWhereClause.put(ApplicationConstants.ORG_ID, asOrgId);
			loHMWhereClause.put(ApplicationConstants.BUIZ_APP_ID, asBuisAppId);
			loHMWhereClause.put("TABLE", asTableName);
			if (asTableName.equalsIgnoreCase("ORGANIZATION"))
			{
				loWhereClauseSB.append("ORGANIZATION_ID = '");
				loWhereClauseSB.append(asOrgId);
				loWhereClauseSB.append("'");
			}
			else
			{
				loWhereClauseSB.append("BUSINESS_APPLICATION_ID= '");
				loWhereClauseSB.append(asBuisAppId);
				loWhereClauseSB.append("' AND ORGANIZATION_ID = '");
				loWhereClauseSB.append(asOrgId);
				loWhereClauseSB.append("'");
			}
			loHMWhereClause.put("asWhere", loWhereClauseSB.toString());
			QuestionAnswerMapper loMapper = aoMyBatisSession.getMapper(QuestionAnswerMapper.class);
			loLQuestionAnswer = loMapper.getFormDetails(loHMWhereClause);
			if (loLQuestionAnswer != null && !loLQuestionAnswer.isEmpty())
			{
				loHMFormValues = loLQuestionAnswer.get(0);
			}
			else
			{
				loHMFormValues = new HashMap<String, Object>();
			}
		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Exception occured while selecting answer data from " + asTableName + " table " + loEx
					+ " for ", loEx);
			throw new ApplicationException("Exception occured while selecting answer data from Basicform table " + loEx
					+ " for ", loEx);
		}

		return loHMFormValues;
	}
}