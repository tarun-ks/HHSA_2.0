package com.nyc.hhs.daomanager.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.FaqFormBean;
import com.nyc.hhs.model.FaqFormDetailBean;
import com.nyc.hhs.model.FaqFormMasterBean;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DAOUtil;

/**
 * FAQService: This Class is used to perform operations to insert/update and
 * delete Topic and Question Answer details
 */
public class FAQService extends ServiceState
{
	private static final LogInfo LOG_OBJECT = new LogInfo(FAQService.class);

	/**
	 * This method gets FAQSummaryMap when clicked for Preview
	 * 
	 * @param aoFaqFormBean has fields related to faq (questions, answers
	 *            ...etc)
	 * @param aoMyBatisSession to connect to database
	 * @return Map<Integer,List<FaqFormBean>> with integer as key and list of
	 *         FaqFormBean has fields related to faq (questions, answers ...etc)
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes", "static-access" })
	public Map<Integer, List<FaqFormBean>> getFAQDisplayFromPreview(FaqFormBean aoFaqFormBean,
			SqlSession aoMyBatisSession) throws ApplicationException
	{
		List<FaqFormBean> loFAQSummaryListFromPreview = null;
		Map loFAQHashMap = new LinkedHashMap();
		Integer loCategoryId;
		try
		{
			loFAQSummaryListFromPreview = (List<FaqFormBean>) DAOUtil.masterDAO(aoMyBatisSession, aoFaqFormBean,
					ApplicationConstants.MAPPER_CLASS_FAQ, "selectFAQ", "com.nyc.hhs.model.FaqFormBean");
			Iterator loIterator = loFAQSummaryListFromPreview.iterator();
			List loFaqList = null;
			while (loIterator.hasNext())
			{
				FaqFormBean loValue = (FaqFormBean) loIterator.next();
				loCategoryId = loValue.getMsTopicId();
				if (loFAQHashMap.containsKey(loCategoryId))
				{
					List loNewList = (List) loFAQHashMap.get(loCategoryId);
					loNewList.add(loValue);
					loFAQHashMap.put(loCategoryId, loNewList);
				}
				else
				{
					loFaqList = new ArrayList();
					loFaqList.add(loValue);
					loFAQHashMap.put(loCategoryId, loFaqList);
				}
			}
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("FormFAQBean", CommonUtil.convertBeanToString(aoFaqFormBean));
			LOG_OBJECT.Error("Exception occured while getting data from getFAQDisplayFromPreview in FAQService ",
					loAppEx);
			setMoState("Transaction Failed:: FAQService:getFAQDisplayFromPreview method - failed to get FAQSummaryMap when clicked for Preview\n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: FAQService:getFAQDisplayFromPreview method - retrieved FAQSummaryMap when clicked for Preview\n ");
		return loFAQHashMap;
	}

	/**
	 * This method deletes from FAQ help master
	 * 
	 * @param aoFaqFormBean has fields related to faq (questions, answers
	 *            ...etc)
	 * @param aoMyBatisSession to connect to database
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "static-access" })
	public void deleteFromfaqHelpMaster(FaqFormBean aoFaqFormBean, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoFaqFormBean, ApplicationConstants.MAPPER_CLASS_FAQ,
					"deleteFromfaqHelpMaster", "com.nyc.hhs.model.FaqFormBean");
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while deleting data from deleteFromfaqHelpMaster in FAQService ",
					loAppEx);
			setMoState("Transaction Failed:: FAQService: deleteFromfaqHelpMaster method - failed to delete the whole table faqhelpmaster \n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: FAQService:deleteFromfaqHelpMaster method - deleted all entries from faqhelpmaster \n");
	}

	/**
	 * This method deletes from faq help detail
	 * 
	 * @param aoFaqFormBean has fields related to faq (questions, answers
	 *            ...etc)
	 * @param aoMyBatisSession to connect to database
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "static-access" })
	public void deleteFromfaqHelpDetail(FaqFormBean aoFaqFormBean, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoFaqFormBean, ApplicationConstants.MAPPER_CLASS_FAQ,
					"deleteFromfaqHelpDetail", "com.nyc.hhs.model.FaqFormBean");
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while deleting data from deleteFromfaqHelpDetail in FAQService ",
					loAppEx);
			setMoState("Transaction Failed:: FAQService: deleteFromfaqHelpDetail method - failed to delete the whole table faqhelpmaster \n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: FAQService: deleteFromfaqHelpDetail method - deleted all entries from faqhelpdetail \n");
	}

	/**
	 * This method inserts into faq help master
	 * 
	 * @param aoFaqFormBean has fields related to faq (questions, answers
	 *            ...etc)
	 * @param aoMyBatisSession to connect to database
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "static-access" })
	public void insertTofaqHelpMaster(FaqFormBean aoFaqFormBean, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoFaqFormBean, ApplicationConstants.MAPPER_CLASS_FAQ,
					"insertTofaqHelpMaster", "com.nyc.hhs.model.FaqFormBean");
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while inserting data from insertTofaqHelpMaster in FAQService ",
					loAppEx);
			setMoState("Transaction Failed:: FAQService: insertTofaqHelpMaster method - failed to insert all entries in MAINTENANCE_FAQ_MASTER "
					+ "to HELP_MAINTENANCE_FAQ_MASTER \n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: FAQService: insertTofaqHelpMaster method "
				+ "- inserted all entries in MAINTENANCE_FAQ_MASTER to HELP_MAINTENANCE_FAQ_MASTER \n");
	}

	/**
	 * This method insert into faq detail
	 * 
	 * @param aoFaqFormBean has fields related to faq (questions, answers
	 *            ...etc)
	 * @param aoMyBatisSession to connect to database
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "static-access" })
	public void insertTofaqHelpDetail(FaqFormBean aoFaqFormBean, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoFaqFormBean, ApplicationConstants.MAPPER_CLASS_FAQ,
					"insertTofaqHelpDetail", "com.nyc.hhs.model.FaqFormBean");
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while inserting data from insertTofaqHelpDetail in FAQService ",
					loAppEx);
			setMoState("Transaction Failed:: FAQService: insertTofaqHelpMaster method - failed to insert all "
					+ "entries in MAINTENANCE_FAQ_DETAILS to HELP_MAINTENANCE_FAQ_DETAILS \n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: FAQService: insertTofaqHelpMaster method - inserted all entries in "
				+ "MAINTENANCE_FAQ_DETAILS to HELP_MAINTENANCE_FAQ_DETAILS \n");
	}

	/**
	 * This method gets map of faq summary list that are published
	 * 
	 * @param aoFaqFormBean has fields related to faq (questions, answers
	 *            ...etc)
	 * @param aoMyBatisSession to connect to database
	 * @return Map<Integer,List<FaqFormBean>> with integer as key and list of
	 *         FaqFormBean has fields related to faq (questions, answers ...etc)
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes", "static-access" })
	public Map<Integer, List<FaqFormBean>> getFAQDisplayFromHelp(FaqFormBean aoFaqFormBean, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		List<FaqFormBean> loFAQSummaryListforPublish = null;
		Map loFAQHashMapForPublish = new LinkedHashMap();
		Integer loCategoryId;
		try
		{
			loFAQSummaryListforPublish = (List<FaqFormBean>) DAOUtil.masterDAO(aoMyBatisSession, aoFaqFormBean,
					ApplicationConstants.MAPPER_CLASS_FAQ, "selectFromFAQForHelp", "com.nyc.hhs.model.FaqFormBean");
			Iterator loItr = loFAQSummaryListforPublish.iterator();
			List loFaqList = null;
			while (loItr.hasNext())
			{
				FaqFormBean loValue = (FaqFormBean) loItr.next();
				loCategoryId = loValue.getMsTopicId();
				if (loFAQHashMapForPublish.containsKey(loCategoryId))
				{
					List loNewList = (List) loFAQHashMapForPublish.get(loCategoryId);
					loNewList.add(loValue);
					loFAQHashMapForPublish.put(loCategoryId, loNewList);
				}
				else
				{
					loFaqList = new ArrayList();
					loFaqList.add(loValue);
					loFAQHashMapForPublish.put(loCategoryId, loFaqList);
				}
			}

		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("FormFAQBean", CommonUtil.convertBeanToString(aoFaqFormBean));
			LOG_OBJECT.Error("Exception occured while getting data from getFAQDisplayFromHelp in FAQService ", loAppEx);
			setMoState("Transaction Failed:: FAQService: getFAQDisplayFromHelp method - failed to get faq summary list that are published \n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: FAQService: getFAQDisplayFromHelp method - retrieved faq summary list that are  published \n");
		return loFAQHashMapForPublish;
	}

	/**
	 * This method gets topic list for FaqFormMasterBean
	 * 
	 * @param aoMyBatisSession to connect to database
	 * @return List<FaqFormBean> list of FaqFormBean with fields related to faq
	 *         (questions, answers ...etc)
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "static-access", "unchecked" })
	public List<FaqFormMasterBean> getAllTopic(SqlSession aoMyBatisSession) throws ApplicationException
	{
		List<FaqFormMasterBean> loTopicList = null;
		try
		{
			loTopicList = (List<FaqFormMasterBean>) DAOUtil.masterDAO(aoMyBatisSession, null,
					ApplicationConstants.MAPPER_CLASS_FAQ, "selectAllTopic", null);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while getting data from getAllTopic in FAQService ", loAppEx);
			setMoState("Transaction Failed:: FAQService: getAllTopic method - failed to get all entries from MAINTENANCE_FAQ_MASTER \n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: FAQService: getAllTopic method - retrieved all entries from MAINTENANCE_FAQ_MASTER \n");
		return loTopicList;
	}

	/**
	 * This method adds new topic in Master table
	 * 
	 * @param aoFaqMasterBean has fields related to faq ( topic name, id, dates
	 *            etc...)
	 * @param aoMyBatisSession to connect to database
	 * @return boolean status
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "static-access" })
	public boolean addNewTopic(FaqFormMasterBean aoFaqMasterBean, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		Boolean lbStatus = false;
		try
		{
			lbStatus = (Boolean) DAOUtil.masterDAO(aoMyBatisSession, aoFaqMasterBean,
					ApplicationConstants.MAPPER_CLASS_FAQ, "insertNewTopic", "com.nyc.hhs.model.FaqFormMasterBean");
			lbStatus = true;
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while inserting data from addNewTopic in FAQService ", loAppEx);
			setMoState("Transaction Failed:: FAQService: addNewTopic method - failed to add a new untitled topic with sequence"
					+ "SEQ_FAQ_TOPIC_ID.NEXTVAL to MAINTENANCE_FAQ_MASTER \n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: FAQService: addNewTopic method - "
				+ "added a new untitled topic with sequence SEQ_FAQ_TOPIC_ID.NEXTVAL to MAINTENANCE_FAQ_MASTER \n");
		return lbStatus;
	}

	/**
	 * This method gets list of all questions that corresponds to given Topic id
	 * 
	 * @param aoFaqDetailBean has fields related to faq (questions, answers
	 *            ...etc)
	 * @param aoMyBatisSession to connect to database
	 * @return List<FaqFormDetailBean> list of FaqFormBean with fields related
	 *         to faq (questions, answers ...etc)
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "static-access", "unchecked" })
	public List<FaqFormDetailBean> getQuesByTopic(FaqFormDetailBean aoDetailBean, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		List<FaqFormDetailBean> loQuestionList = null;
		try
		{
			loQuestionList = (List<FaqFormDetailBean>) DAOUtil.masterDAO(aoMyBatisSession, aoDetailBean,
					ApplicationConstants.MAPPER_CLASS_FAQ, "selectQuestionsByTopic",
					"com.nyc.hhs.model.FaqFormDetailBean");
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("DetailBean", CommonUtil.convertBeanToString(aoDetailBean));
			LOG_OBJECT.Error("Exception occured while getting data from getQuesByTopic in FAQService ", loAppEx);
			setMoState("Transaction Failed:: FAQService: getQuesByTopic method - failed to get all questions where topicid="
					+ aoDetailBean.getMiTopicId() + " \n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: FAQService: getQuesByTopic method - retrived all questions where topicid="
				+ aoDetailBean.getMiTopicId() + " \n");
		return loQuestionList;
	}

	/**
	 * This method insert topic name, question and answer in detail faq table
	 * 
	 * @param aoFaqDetailBean has fields related to faq (questions, answers
	 *            ...etc)
	 * @param aoSession MyBatisSession to connect to database
	 * @return boolean status
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "static-access" })
	public boolean insertTopicDetails(FaqFormDetailBean aoFaqDetailBean, SqlSession aoSession)
			throws ApplicationException
	{
		Boolean lbStatus = false;
		try
		{
			DAOUtil.masterDAO(aoSession, aoFaqDetailBean, ApplicationConstants.MAPPER_CLASS_FAQ, "insertTopicDetails",
					"com.nyc.hhs.model.FaqFormDetailBean");
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("FaqDetailBean", CommonUtil.convertBeanToString(aoFaqDetailBean));
			LOG_OBJECT.Error("Exception occured while inserting data from insertTopicDetails in FAQService ", loAppEx);
			setMoState("Transaction Failed:: FAQService: insertTopicDetails method - failed to insert a new question in "
					+ "MAINTENANCE_FAQ_DETAILS with sequence SEQ_FAQ_QUESTION_ID.NEXTVAL \n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: FAQService: insertTopicDetails method - inserted a new record in "
				+ "MAINTENANCE_FAQ_DETAILS with sequence SEQ_FAQ_QUESTION_ID.NEXTVAL \n");
		return lbStatus;
	}

	/**
	 * This method updates question and answer based on topic id in detail faq
	 * table
	 * 
	 * @param aoFaqDetailBean has fields related to faq (questions, answers
	 *            ...etc)
	 * @param aoSession MyBatisSession to connect to database
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "static-access" })
	public void updateQuesAns(FaqFormDetailBean aoFaqDetailBean, SqlSession aoSession) throws ApplicationException
	{
		try
		{
			DAOUtil.masterDAO(aoSession, aoFaqDetailBean, ApplicationConstants.MAPPER_CLASS_FAQ, "updateQuesAns",
					"com.nyc.hhs.model.FaqFormDetailBean");
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("FaqDetailBean", CommonUtil.convertBeanToString(aoFaqDetailBean));
			LOG_OBJECT.Error("Exception occured while updating data from updateQuesAns in FAQService ", loAppEx);
			setMoState("Transaction Failed:: FAQService: updateQuesAns method - failed to update a record in"
					+ " MAINTENANCE_FAQ_DETAILS where questionid=" + aoFaqDetailBean.getMiQuestionId() + " \n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: FAQService: updateQuesAns method - updated a record in MAINTENANCE_FAQ_DETAILS where questionid="
				+ aoFaqDetailBean.getMiQuestionId() + " \n");
	}

	/**
	 * This method updates topic name
	 * 
	 * @param aoFaqMasterBean has fields related to faq ( topic name, id, dates
	 *            etc...)
	 * @param aoSession MyBatisSession to connect to database
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "static-access" })
	public void updateTopicName(FaqFormMasterBean aoFaqMasterBean, SqlSession aoSession) throws ApplicationException
	{
		try
		{
			DAOUtil.masterDAO(aoSession, aoFaqMasterBean, ApplicationConstants.MAPPER_CLASS_FAQ, "updateTopicName",
					"com.nyc.hhs.model.FaqFormMasterBean");
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("FaqMasterBean", CommonUtil.convertBeanToString(aoFaqMasterBean));
			LOG_OBJECT.Error("Exception occured while updating data from updateTopicName in FAQService ", loAppEx);
			setMoState("Transaction Failed:: FAQService: updateTopicName method -"
					+ " failed to update topic name in MAINTENANCE_FAQ_MASTER where topicic="
					+ aoFaqMasterBean.getMiTopicId() + " \n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: FAQService: updateTopicName method - updated topic name in MAINTENANCE_FAQ_MASTER where topicic="
				+ aoFaqMasterBean.getMiTopicId() + " \n");
	}

	/**
	 * This method delete topic name, question and answer based on topic id from
	 * master faq table
	 * 
	 * @param aoFaqMasterBean has fields related to faq ( topic name, id, dates
	 *            etc...)
	 * @param aoMyBatisSession to connect to database
	 * @return boolean status
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "static-access" })
	public boolean deleteQuesAnsByTopic(FaqFormMasterBean aoFaqMasterBean, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		boolean lbDeleteQAStatus = false;
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoFaqMasterBean, ApplicationConstants.MAPPER_CLASS_FAQ,
					"deleteTopicByTopicId", "com.nyc.hhs.model.FaqFormMasterBean");
			lbDeleteQAStatus = true;
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("FaqMasterBean", CommonUtil.convertBeanToString(aoFaqMasterBean));
			LOG_OBJECT.Error("Exception occured while deleting data from deleteQuesAnsByTopic in FAQService ", loAppEx);
			setMoState("Transaction Failed:: FAQService: deleteQuesAnsByTopic method - "
					+ "failed to delete a record from MAINTENANCE_FAQ_MASTER where topicid="
					+ aoFaqMasterBean.getMiTopicId() + " \n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: FAQService: deleteQuesAnsByTopic method - deleted a record from MAINTENANCE_FAQ_MASTER where topicid="
				+ aoFaqMasterBean.getMiTopicId() + " \n");
		return lbDeleteQAStatus;

	}

	/**
	 * This method delete topic name, question and answer based on topic id from
	 * detail faq table
	 * 
	 * @param aoFaqDetailBean has fields related to faq (questions, answers
	 *            ...etc)
	 * @param aoMyBatisSession to connect to database
	 * @return boolean status
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "static-access" })
	public boolean deleteQuesAnsByTopicFromDetails(FaqFormDetailBean aoFaqDetailBean, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		boolean lbQAStatus = false;
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoFaqDetailBean, ApplicationConstants.MAPPER_CLASS_FAQ,
					"deleteTopicByTopicIdFromDetail", "com.nyc.hhs.model.FaqFormDetailBean");
			lbQAStatus = true;
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("FaqDetailBean", CommonUtil.convertBeanToString(aoFaqDetailBean));
			LOG_OBJECT.Error(
					"Exception occured while deleting data from deleteQuesAnsByTopicFromDetails in FAQService ",
					loAppEx);
			setMoState("Transaction Failed:: FAQService: deleteQuesAnsByTopicFromDetails method - "
					+ "failed to delete all entries from MAINTENANCE_FAQ_DETAILS where topicid="
					+ aoFaqDetailBean.getMiTopicId() + " \n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: FAQService: deleteQuesAnsByTopicFromDetails method - "
				+ "deleted all entries from MAINTENANCE_FAQ_DETAILS where topicid=" + aoFaqDetailBean.getMiTopicId()
				+ " \n");
		return lbQAStatus;
	}

	/**
	 * This method delete question and linked answer based on question id from
	 * detail faq table
	 * 
	 * @param aoFaqDetailBean has fields related to faq (questions, answers
	 *            ...etc)
	 * @param aoMyBatisSession to connect to database
	 * @return boolean status
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "static-access" })
	public boolean deleteQuesAnsByQuestionId(FaqFormDetailBean aoFaqDetailBean, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		boolean lbDeleteStatus = false;
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoFaqDetailBean, ApplicationConstants.MAPPER_CLASS_FAQ,
					"deleteQuestionByQuestionId", "com.nyc.hhs.model.FaqFormDetailBean");
		}
		catch (ApplicationException loAppEx)
		{
			lbDeleteStatus = false;
			loAppEx.addContextData("FaqDetailBean", CommonUtil.convertBeanToString(aoFaqDetailBean));
			LOG_OBJECT.Error("Exception occured while deleting data from deleteQuesAnsByQuestionId in FAQService ",
					loAppEx);
			setMoState("Transaction Failed:: FAQService: deleteQuesAnsByQuestionId method - "
					+ "failed to delete a record from MAINTENANCE_FAQ_DETAILS where questionid="
					+ aoFaqDetailBean.getMiQuestionId() + " \n");
			throw loAppEx;
		}
		lbDeleteStatus = true;
		setMoState("Transaction Success:: FAQService: deleteQuesAnsByQuestionId method -"
				+ " deleted a record from MAINTENANCE_FAQ_DETAILS where questionid="
				+ aoFaqDetailBean.getMiQuestionId() + " \n");
		return lbDeleteStatus;
	}

	/**
	 * This method gets all topics from master faq table
	 * 
	 * @param aoFaqMasterBean has fields related to faq ( topic name, id, dates
	 *            etc...)
	 * @param aoMyBatisSession to connect to database
	 * @return boolean status
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "static-access" })
	public boolean updateTopicNameInMaster(FaqFormMasterBean aoFaqMasterBean, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		boolean lbDeleteQAStatus = false;
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoFaqMasterBean, ApplicationConstants.MAPPER_CLASS_FAQ,
					"updateTopicNameInMaster", "com.nyc.hhs.model.FaqFormMasterBean");
		}
		catch (ApplicationException loAppEx)
		{
			lbDeleteQAStatus = false;
			loAppEx.addContextData("FaqMasterBean", CommonUtil.convertBeanToString(aoFaqMasterBean));
			LOG_OBJECT.Error("Exception occured while updating data from updateTopicNameInMaster in FAQService ",
					loAppEx);
			setMoState("Transaction Failed:: FAQService:getFAQDisplayFromPreview method - failed to update"
					+ "topic name in MAINTENANCE_FAQ_MASTER with topic id=" + aoFaqMasterBean.getMiTopicId() + " \n");
			throw loAppEx;
		}
		lbDeleteQAStatus = true;
		setMoState("Transaction Success:: FAQService: updateTopicNameInMaster method - updated topic name in MAINTENANCE_FAQ_MASTER with topic id="
				+ aoFaqMasterBean.getMiTopicId() + " \n");
		return lbDeleteQAStatus;
	}

	/**
	 * This Method Fetches the topic name by topic id
	 * 
	 * @param aoMybatisSession Sql Session
	 * @param asTopicId Topic ID
	 * @return lsTopicName Topic Name
	 * @throws ApplicationException
	 */
	public String getTopicName(SqlSession aoMybatisSession, String asTopicId) throws ApplicationException
	{
		String lsTopicName = "";
		try
		{
			lsTopicName = (String) DAOUtil.masterDAO(aoMybatisSession, asTopicId,
					ApplicationConstants.MAPPER_CLASS_FAQ, "getTopicName", "java.lang.String");
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while fetching TopicName FAQ Master in FAQService ", loAppEx);
			setMoState("Transaction Success:: FAQService: getTopicName method - fetch topic name from MAINTENANCE_FAQ_MASTER with topic id="
					+ asTopicId + " \n");
			throw loAppEx;
		}
		return lsTopicName;
	}
}
