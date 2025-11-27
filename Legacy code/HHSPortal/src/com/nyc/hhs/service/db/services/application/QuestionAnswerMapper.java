package com.nyc.hhs.service.db.services.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  QuestionAnswerMapper is an interface between the DAO and database 
 *  layer for Question and Answers to insert, delete and update entries.
 *  
 */

public interface QuestionAnswerMapper
{
	void insertFormInformation(HashMap aoHashmap);
	void deleteFormInfo(HashMap aoHashmap);
	void updateFormInfo(HashMap aoHashmap);
	void insertSubSectionDetails(HashMap aoHashmap);
	int updateSubSectionDetails(HashMap aoHashmap);
	List<HashMap<String,Object>> getFormDetails(Map<String, String> aoMap);
	HashMap getCorpStructureValue(Map<String, String> aoHashmap);
	Map<String, String> getFormDetailsOfOrg(Map<String, String> aoHashmap);
}
