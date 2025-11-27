package com.nyc.hhs.service.db.dao;

import java.util.ArrayList;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.model.SecurityQuestionBean;
import com.nyc.hhs.service.db.services.application.NycRegisterMapper;

/**
 * This DAO class is for providing us with the functionality to register NYC process
 * Also it gives the functionality of fetching the Security Questions.
 * 
 */

public class NycRegisterDAO {
	
	/**
	 * This method fetch security questions for new user registration
	 * as well as updating security questions and answers.
	 * 
	 * @param aoMyBatisSession is an object of SqlSession
	 * @return ArrayList loQuestionList is the list of fetched questions
	 * @throws ApplicationException
	 */
	
	public ArrayList<SecurityQuestionBean> getSecurityQuestions(
		SqlSession aoMyBatisSession) throws ApplicationException
		{
	    ArrayList<SecurityQuestionBean> loQuestionList= null;
	    NycRegisterMapper loMapper = aoMyBatisSession.getMapper(NycRegisterMapper.class);
		loQuestionList = loMapper.getSecurityQuestions();			
 		return loQuestionList;
	}

}
