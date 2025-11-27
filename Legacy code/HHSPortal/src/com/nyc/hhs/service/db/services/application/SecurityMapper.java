package com.nyc.hhs.service.db.services.application;

import java.util.List;

import com.nyc.hhs.model.ComponentRoleMappingBean;
import com.nyc.hhs.model.UserBean;

/**
 *  SecurityMapper is an interface between the DAO and database 
 *  layer to map different roles for security related check.
 *  
 */

public interface SecurityMapper {
	List<ComponentRoleMappingBean> getroleComponentMapping(UserBean aoUserBean);
	
}
