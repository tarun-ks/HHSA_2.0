package com.nyc.hhs.service.db.services.application;

import java.util.List;

import com.nyc.hhs.model.FaqFormBean;
import com.nyc.hhs.model.FaqFormDetailBean;
import com.nyc.hhs.model.FaqFormMasterBean;

/**
 *  FAQMapper is an interface between the DAO and database layer 
 *  for the FAQ maintenance screens so as to insert, update and
 *  delete entries.
 *  
 */
public interface FAQMapper {
	
	@SuppressWarnings("rawtypes")
	List selectFAQ(FaqFormBean aoFaqFormBean);
	@SuppressWarnings({"rawtypes" })
	List selectFromFAQForHelp(FaqFormBean aoFaqFormBean);
	List<FaqFormMasterBean> selectAllTopic();
	List<FaqFormDetailBean> selectQuestionsByTopic(FaqFormDetailBean asDetailBean);
	void insertTofaqHelpMaster(FaqFormBean aoFaqFormBean);
	void insertTofaqHelpDetail(FaqFormBean aoFaqFormBean);
	void insertTopicDetails(FaqFormDetailBean aoFaqDetailBean);
	void insertNewTopic(FaqFormMasterBean asButtonState);
	void updateTopicName(FaqFormMasterBean aoFaqMasterBean);
	void updateQuesAns(FaqFormDetailBean aoFaqDetailBean);
    void updateTopicNameInMaster(FaqFormMasterBean aoFaqMasterBean);
	void deleteFromfaqHelpDetail(FaqFormBean aoFaqFormBean);
	void deleteFromfaqHelpMaster(FaqFormBean aoFaqFormBean);
    void deleteTopicByTopicId(FaqFormMasterBean aoFaqMasterBean);
    void deleteTopicByTopicIdFromDetail(FaqFormDetailBean aoFaqDetailBean);
    void deleteQuestionByQuestionId(FaqFormDetailBean aoFaqDetailBean);	
    String getTopicName(String asTopisId);
}
