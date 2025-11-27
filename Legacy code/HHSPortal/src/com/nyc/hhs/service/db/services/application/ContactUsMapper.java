package com.nyc.hhs.service.db.services.application;

import java.util.List;

import com.nyc.hhs.model.ContactUsBean;
/**
 *  ContactUsMapper is an interface between the DAO and database layer 
 *  to map the methods for the contact us screen. 
 *  
 */

public interface ContactUsMapper {
	
	int insertContactUsInformation(ContactUsBean aoContactUsBean);
	@SuppressWarnings({"rawtypes" })
	List getTopicList();
	Integer getSequenceFromTable();
	List<ContactUsBean> getContactIDList(Integer asContactus);
	
}


