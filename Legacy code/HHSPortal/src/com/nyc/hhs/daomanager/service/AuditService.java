package com.nyc.hhs.daomanager.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.util.DAOUtil;

/**
 * 
 * AuditService: This class provides operations to log events from different
 *               modules of the application.
 * 
 */
public class AuditService extends ServiceState
{
	/**
	 * This method logs the Audit information available in aoAuditInfoMap into
	 * Audit tables.
	 * 
	 * @param aoMyBatisSession
	 *            MyBatis Sql session
	 * @param aoAuditInfoMap
	 *            Map containing information to be inserted in Audit table
	 * @return Boolean lbAuditStatus Insertion success/failure status
	 * @throws ApplicationException
	 */
	public Boolean logAuditInfo(SqlSession aoMyBatisSession, HashMap aoAuditInfoMap) throws ApplicationException
	{

		Boolean lbAuditStatus = false;
		
		String lsAuditType = (String) aoAuditInfoMap.get("asAuditType");
		//Defect #6201 Fix
		if(null != aoAuditInfoMap && null != aoAuditInfoMap.get("eventType"))
		{
			if ("application".equalsIgnoreCase(lsAuditType))
			{
				if (aoAuditInfoMap.containsKey("entityId"))
				{
					DAOUtil.masterDAO(aoMyBatisSession, aoAuditInfoMap, "com.nyc.hhs.service.db.services.application.AuditMapper",
							"insertInApplicationAudit", "java.util.Map");
				}
				else if (aoAuditInfoMap.containsKey("tncentityId"))
				{
					DAOUtil.masterDAO(aoMyBatisSession, aoAuditInfoMap, "com.nyc.hhs.service.db.services.application.AuditMapper",
							"insertInApplicationAuditDocument", "java.util.Map");
				}
				else
				{
					return lbAuditStatus;
				}

			}
			else
			{
				DAOUtil.masterDAO(aoMyBatisSession, aoAuditInfoMap, "com.nyc.hhs.service.db.services.application.AuditMapper", "insertInGeneralAudit",
						"java.util.Map");
			}
		}
		return lbAuditStatus;

	}
	
	@SuppressWarnings("unchecked")
	public Boolean logAuditInfoForBatch(SqlSession aoMyBatisSession, HashMap aoAuditInfoMap,Map aoProviderStatusMap) throws ApplicationException
	{

		Boolean lbAuditStatus = false;
		
		if(null!=aoProviderStatusMap.get("orgStatus")){
			aoAuditInfoMap.put("data", "Provider status changed to ".concat((String)(aoProviderStatusMap.get("orgStatus"))));
		}
		if(null!=aoProviderStatusMap.get("asProviderStatus")){
			aoAuditInfoMap.put("data", "Provider status changed to ".concat((String)(aoProviderStatusMap.get("asProviderStatus"))));
		}
		String lsAuditType = (String) aoAuditInfoMap.get("eventType");
		if ("Provider status changed by Batch".equalsIgnoreCase(lsAuditType))
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoAuditInfoMap, "com.nyc.hhs.service.db.services.application.AuditMapper", "insertInGeneralAudit",
			"java.util.Map");
		}
		return lbAuditStatus;

	}
}
