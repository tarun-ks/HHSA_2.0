package com.nyc.hhs.service.db.services.application;

import java.util.List;

import com.nyc.hhs.model.AutoSaveBean;
/**
 *This is a mapper class which has queries 
 *to perform functions and get data for 
 *Auto Save class
 */

public interface AutoSaveMapper
{
	public Integer insertAutoSave(AutoSaveBean bean);
	
	public Integer updateAutoSave(AutoSaveBean bean);
	
	public List<AutoSaveBean> getAutoSaveInfo(AutoSaveBean bean);
	
	public Integer deleteAutoSave(AutoSaveBean bean);
}
