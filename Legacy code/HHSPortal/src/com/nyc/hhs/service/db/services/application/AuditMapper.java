package com.nyc.hhs.service.db.services.application;

import java.util.Map;

/**
 *  AuditMapper is an interface between the DAO and database layer 
 *  to insert data in application and general audit table.
 *  
 */

public interface  AuditMapper {

	public void insertInGeneralAudit(Map aoGeneralAuditMap);
	public void insertInApplicationAudit(Map aoApplicationAuditMap);
	public void insertInApplicationAuditDocument(Map aoApplicationAuditMap);

}
