package com.nyc.hhs.service.db.services.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.TaskDetailsBean;
/**
 *  This is mapper class for audit which has queries to get data
 */
public interface HhsAuditMapper
{
	public void hhsauditProviderInsert(HhsAuditBean aoAuditBean);

	public void hhsauditAgencyInsert(HhsAuditBean aoAuditBean);

	public void hhsauditAcceleratorInsert(HhsAuditBean aoAuditBean);

	public void saveCommentNonAudit(HhsAuditBean aoAuditBean);

	public int updateCommentNonAudit(HhsAuditBean aoAuditBean);

	public int updateCommentNonAuditForProvider(HhsAuditBean aoAuditBean);

	public void deleteFromUserComment(HhsAuditBean aoAuditBean);

	public int copyAgencyTaskCommentHistory(HashMap aoHMReqdProp);

	public List<Map<String, String>> fetchUserCommentsForTabLevelAuditProvider (HhsAuditBean aoAuditBean);

	public List<Map<String, String>> fetchUserCommentsForTabLevelAuditAgency (TaskDetailsBean aoAuditBean);

	public void deleteFromUserCommentForTabLevelProvider(HhsAuditBean aoAuditBean);

	public void deleteFromUserCommentForTabLevelAgency(TaskDetailsBean aoAuditBean);

	public void deleteTabHighlightTabLevelFromAgencyAuditOnSubmit(HashMap aoHMWFRequiredProps);

	// Methods for Inserts for audit entry for Procurement, Eval gp and Comp
	// pool status update
	public void hhsauditInsertForProcurement(HhsAuditBean aoAuditBean);

	public void hhsauditInsertForEvalGroup(HhsAuditBean aoAuditBean);

	public void hhsauditInsertForCompPool(HhsAuditBean aoAuditBean);
	
	public void releaseAddendumAuditInsert(HhsAuditBean aoAuditBean);

	/* [Start] R8.9.0 QC9531  cleaning Dup user_dn     */
	public void hhsauditInsertForDupUserDN(HhsAuditBean aoAuditBean);
	/* [End] R8.9.0 QC9531  cleaning Dup user_dn     */
}
