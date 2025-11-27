package com.nyc.hhs.service.db.services.application;

import java.util.ArrayList;
import java.util.HashMap;

import com.nyc.hhs.model.AlertInboxBean;

/**
 * AlertInboxMapper is an interface between the DAO and database layer for alert
 * inbox to insert , delete and select entries.
 * 
 */

public interface AlertInboxMapper
{
	public ArrayList<AlertInboxBean> selectAlertInboxList(HashMap<String, Object> aoQueryMap);

	public ArrayList<AlertInboxBean> selectAlertInboxListAll(HashMap<String, Object> aoQueryMap);

	public void deleteSelectedAlertItem(HashMap<String, String> aoQueryMap);

	public AlertInboxBean getSelectedAlertItem(HashMap<String, String> aoQueryMap);

	public void putNotificationStatus(HashMap<String, String> aoQueryMap);

	public int selectAlertInboxListCount(HashMap<String, Object> aoQueryMap);

	public int selectAlertInboxListCountAll(HashMap<String, Object> aoQueryMap);

	public int getUserAccountCount(String asUserId);

	// Added in R5
	public String getAlertBoxUnReadData(HashMap<String, Object> aoQueryMap);
	// R5 Ends
}
