package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.daomanager.service.HhsAuditService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.HhsAuditBean;

public class HhsAuditServiceTest
{

	SqlSession moMyBatisSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
	HhsAuditService moHhsAuditService = new HhsAuditService();

	@Test
	public void testHhsauditInsertCase1 () throws ApplicationException
	{
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		loHhsAuditBean.setAuditTableIdentifier(HHSConstants.PROVIDER_AUDIT);
		loHhsAuditBean.setData("Task assigned to: Unassigned Level 1");
		loHhsAuditBean.setEventType(HHSConstants.PROPERTY_TASK_CREATION_EVENT);
		loHhsAuditBean.setEventName(HHSConstants.PROPERTY_TASK_CREATION_EVENT);
		loHhsAuditBean.setEntityId("61");
		loHhsAuditBean.setEntityType(HHSConstants.TASK_INVOICE_REVIEW);
		loHhsAuditBean.setUserId("803");
		Boolean lbUpdateStatus = moHhsAuditService.hhsauditInsert(moMyBatisSession, loHhsAuditBean, true);
		assertTrue(lbUpdateStatus);
		moMyBatisSession.close();
	}
	
	@Test
	public void testHhsauditInsertCase2 () throws ApplicationException
	{
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		loHhsAuditBean.setAuditTableIdentifier(HHSConstants.AGENCY_AUDIT);
		loHhsAuditBean.setData("Task Assigned To:first1 last2");
		loHhsAuditBean.setEventType(HHSConstants.PROPERTY_TASK_CREATION_EVENT_TYPE);
		loHhsAuditBean.setEventName(HHSConstants.TASK_ASSIGNMENT);
		loHhsAuditBean.setEntityId("61");
		loHhsAuditBean.setEntityType(HHSConstants.TASK_INVOICE_REVIEW);
		loHhsAuditBean.setUserId("agency_12");
		Boolean lbUpdateStatus = moHhsAuditService.hhsauditInsert(moMyBatisSession, loHhsAuditBean, true);
		assertTrue(lbUpdateStatus);
		moMyBatisSession.close();
	}
	
	@Test
	public void testHhsauditInsertCase3 () throws ApplicationException
	{
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		loHhsAuditBean.setAuditTableIdentifier(HHSConstants.ACCELERATOR_AUDIT);
		loHhsAuditBean.setData("Procurement Status has been changed to cancelled");
		loHhsAuditBean.setEventType(HHSConstants.CANCEL_PROC);
		loHhsAuditBean.setEventName(HHSConstants.CANCEL);
		loHhsAuditBean.setEntityId("975");
		loHhsAuditBean.setEntityType(HHSConstants.PROCUREMENT);
		loHhsAuditBean.setUserId("city_142");
		Boolean lbUpdateStatus = moHhsAuditService.hhsauditInsert(moMyBatisSession, loHhsAuditBean, true);
		assertTrue(lbUpdateStatus);
		moMyBatisSession.close();
	}
	
	@Test
	public void testHhsauditInsertCase4 () throws ApplicationException
	{
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		Boolean isTaskScreen=false;
		loHhsAuditBean.setAuditTableIdentifier(HHSConstants.NON_AUDIT_COMMENTS);
		loHhsAuditBean.setEntityType("Accept Proposal Task");
		loHhsAuditBean.setEntityId("341");
		loHhsAuditBean.setUserId("agency_14");
		loHhsAuditBean.setAgencyId("ACS");
		loHhsAuditBean.setProviderComments("verified");
		loHhsAuditBean.setInternalComments("submit for nxt evaluation");
		loHhsAuditBean.setIsTaskScreen(isTaskScreen);
		Boolean lbUpdateStatus = moHhsAuditService.hhsauditInsert(moMyBatisSession, loHhsAuditBean, true);
		assertTrue(lbUpdateStatus);
		moMyBatisSession.close();
	}
	
	@Test
	public void testHhsauditInsertCase5 () throws ApplicationException
	{
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		loHhsAuditBean.setAuditTableIdentifier(HHSConstants.PROVIDER_AUDIT);
		loHhsAuditBean.setData("Save");
		loHhsAuditBean.setEventType(HHSConstants.PROVIDER_COMMENTS_DATA);
		loHhsAuditBean.setEventName(HHSConstants.PROVIDER_COMMENTS_DATA);
		loHhsAuditBean.setEntityId("61");
		loHhsAuditBean.setEntityType(HHSConstants.AUDIT_INVOICES);
		loHhsAuditBean.setUserId("803");
		Boolean lbUpdateStatus = moHhsAuditService.hhsauditInsert(moMyBatisSession, loHhsAuditBean, true);
		assertTrue(lbUpdateStatus);
		moMyBatisSession.close();
	}
	
	@Test
	public void testHhsauditInsertCase6 () throws ApplicationException
	{
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		loHhsAuditBean.setAuditTableIdentifier(HHSConstants.AGENCY_AUDIT);
		loHhsAuditBean.setData("Status Changed to Approved");
		loHhsAuditBean.setEventType("Status Changed");
		loHhsAuditBean.setEventName("Status Changed");
		loHhsAuditBean.setEntityId("786");
		loHhsAuditBean.setEntityType("Contract Configuration");
		loHhsAuditBean.setUserId("agency_21");
		Boolean lbUpdateStatus = moHhsAuditService.hhsauditInsert(moMyBatisSession, loHhsAuditBean, true);
		assertTrue(lbUpdateStatus);
		moMyBatisSession.close();
	}
	
	@Test
	public void testHhsauditInsertCase7 () throws ApplicationException
	{
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		loHhsAuditBean.setAuditTableIdentifier(HHSConstants.ACCELERATOR_AUDIT);
		loHhsAuditBean.setData("Procurement Status has been changed to cancelled");
		loHhsAuditBean.setEventType(HHSConstants.CANCEL_PROC);
		loHhsAuditBean.setEventName(HHSConstants.CANCEL);
		loHhsAuditBean.setEntityId("723");
		loHhsAuditBean.setEntityType(HHSConstants.PROCUREMENT);
		loHhsAuditBean.setUserId("city_142");
		Boolean lbUpdateStatus = moHhsAuditService.hhsauditInsert(moMyBatisSession, loHhsAuditBean, true);
		assertTrue(lbUpdateStatus);
		moMyBatisSession.close();
	}
	
	@Test
	public void testHhsauditInsertCase8 () throws ApplicationException
	{
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		Boolean isTaskScreen=false;
		loHhsAuditBean.setAuditTableIdentifier(HHSConstants.NON_AUDIT_COMMENTS);
		loHhsAuditBean.setEntityType("Invoice Review");
		loHhsAuditBean.setEntityId("61");
		loHhsAuditBean.setUserId("agency_12");
		loHhsAuditBean.setAgencyId("ACS");
		loHhsAuditBean.setProviderComments("Approving second Time-Public.");
		loHhsAuditBean.setInternalComments("Approving second Time-Internal");
		loHhsAuditBean.setIsTaskScreen(isTaskScreen);
		Boolean lbUpdateStatus = moHhsAuditService.hhsauditInsert(moMyBatisSession, loHhsAuditBean, true);
		assertTrue(lbUpdateStatus);
		moMyBatisSession.close();
	}
	
	@Test
	public void testHhsauditInsertCase9 () throws ApplicationException
	{
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		loHhsAuditBean.setAuditTableIdentifier(HHSConstants.PROVIDER_AUDIT);
		loHhsAuditBean.setData("Task assigned to: Unassigned Level 1");
		loHhsAuditBean.setEventType(HHSConstants.PROPERTY_TASK_CREATION_EVENT);
		loHhsAuditBean.setEventName(HHSConstants.PROPERTY_TASK_CREATION_EVENT);
		loHhsAuditBean.setEntityId("60");
		loHhsAuditBean.setEntityType(HHSConstants.TASK_INVOICE_REVIEW);
		loHhsAuditBean.setUserId("803");
		Boolean lbUpdateStatus = moHhsAuditService.hhsauditInsert(moMyBatisSession, loHhsAuditBean, false);
		assertFalse(lbUpdateStatus);
		moMyBatisSession.close();
	}
	
	@Test
	public void testHhsauditInsertCase10 () throws ApplicationException
	{
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		loHhsAuditBean.setAuditTableIdentifier(HHSConstants.AGENCY_AUDIT);
		loHhsAuditBean.setData("Task Assigned To:first1 last2");
		loHhsAuditBean.setEventType(HHSConstants.PROPERTY_TASK_CREATION_EVENT_TYPE);
		loHhsAuditBean.setEventName(HHSConstants.TASK_ASSIGNMENT);
		loHhsAuditBean.setEntityId("61");
		loHhsAuditBean.setEntityType(HHSConstants.TASK_INVOICE_REVIEW);
		loHhsAuditBean.setUserId("agency_15");
		Boolean lbUpdateStatus = moHhsAuditService.hhsauditInsert(moMyBatisSession, loHhsAuditBean, false);
		assertFalse(lbUpdateStatus);
		moMyBatisSession.close();
	}
	
	@Test
	public void testHhsauditInsertCase11 () throws ApplicationException
	{
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		loHhsAuditBean.setAuditTableIdentifier(HHSConstants.ACCELERATOR_AUDIT);
		loHhsAuditBean.setData("Procurement Status has been changed to cancelled");
		loHhsAuditBean.setEventType(HHSConstants.CANCEL_PROC);
		loHhsAuditBean.setEventName(HHSConstants.CANCEL);
		loHhsAuditBean.setEntityId("975");
		loHhsAuditBean.setEntityType(HHSConstants.PROCUREMENT);
		loHhsAuditBean.setUserId("city_12");
		Boolean lbUpdateStatus = moHhsAuditService.hhsauditInsert(moMyBatisSession, loHhsAuditBean, false);
		assertFalse(lbUpdateStatus);
		moMyBatisSession.close();
	}
	
	@Test
	public void testHhsauditInsertCase12 () throws ApplicationException
	{
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		loHhsAuditBean.setAuditTableIdentifier(HHSConstants.PROVIDER_AUDIT);
		loHhsAuditBean.setData("Sav");
		loHhsAuditBean.setEventType(HHSConstants.PROVIDER_COMMENTS_DATA);
		loHhsAuditBean.setEventName("Submit");
		loHhsAuditBean.setEntityId("60");
		loHhsAuditBean.setEntityType(HHSConstants.AUDIT_INVOICES);
		loHhsAuditBean.setUserId("801");
		Boolean lbUpdateStatus = moHhsAuditService.hhsauditInsert(moMyBatisSession, loHhsAuditBean, false);
		assertFalse(lbUpdateStatus);
		moMyBatisSession.close();
	}
	
	@Test
	public void testHhsauditInsertCase13 () throws ApplicationException
	{
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		loHhsAuditBean.setAuditTableIdentifier(HHSConstants.AGENCY_AUDIT);
		loHhsAuditBean.setData("Status Changed to Approved");
		loHhsAuditBean.setEventType("Status Changed");
		loHhsAuditBean.setEventName("Status Changed");
		loHhsAuditBean.setEntityId("786");
		loHhsAuditBean.setEntityType("Contract Configuration");
		loHhsAuditBean.setUserId("agency_12");
		Boolean lbUpdateStatus = moHhsAuditService.hhsauditInsert(moMyBatisSession, loHhsAuditBean, false);
		assertFalse(lbUpdateStatus);
		moMyBatisSession.close();
	}
	
	@Test
	public void testHhsauditInsertCase14 () throws ApplicationException
	{
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		loHhsAuditBean.setAuditTableIdentifier(HHSConstants.ACCELERATOR_AUDIT);
		loHhsAuditBean.setData("Procurement Status has been changed to cancelled");
		loHhsAuditBean.setEventType(HHSConstants.CANCEL_PROC);
		loHhsAuditBean.setEventName(HHSConstants.CANCEL);
		loHhsAuditBean.setEntityId("723");
		loHhsAuditBean.setEntityType(HHSConstants.PROCUREMENT);
		loHhsAuditBean.setUserId("city_14");
		Boolean lbUpdateStatus = moHhsAuditService.hhsauditInsert(moMyBatisSession, loHhsAuditBean, false);
		assertFalse(lbUpdateStatus);
		moMyBatisSession.close();
	}
	
	@Test
	public void testHhsauditInsertCase15 () throws ApplicationException
	{
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		Boolean isTaskScreen=false;
		loHhsAuditBean.setAuditTableIdentifier(HHSConstants.NON_AUDIT_COMMENTS);
		loHhsAuditBean.setEntityType("Invoice Review");
		loHhsAuditBean.setEntityId("60");
		loHhsAuditBean.setUserId("agency_12");
		loHhsAuditBean.setAgencyId("ACS");
		loHhsAuditBean.setProviderComments("Approving second Time-Public.");
		loHhsAuditBean.setInternalComments("Approving second Time-Internal");
		loHhsAuditBean.setIsTaskScreen(isTaskScreen);
		Boolean lbUpdateStatus = moHhsAuditService.hhsauditInsert(moMyBatisSession, loHhsAuditBean, false);
		assertFalse(lbUpdateStatus);
		moMyBatisSession.close();
	}
	
	@Test
	public void testHhsauditInsertCase16 () throws ApplicationException
	{
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		Boolean isTaskScreen=false;
		loHhsAuditBean.setAuditTableIdentifier("null");
		loHhsAuditBean.setEntityType("Invoice Review");
		loHhsAuditBean.setEntityId("60");
		loHhsAuditBean.setUserId("agency_12");
		loHhsAuditBean.setAgencyId("ACS");
		loHhsAuditBean.setProviderComments("Approving second Time-Public.");
		loHhsAuditBean.setInternalComments("Approving second Time-Internal");
		loHhsAuditBean.setIsTaskScreen(isTaskScreen);
		Boolean lbUpdateStatus = moHhsAuditService.hhsauditInsert(moMyBatisSession, loHhsAuditBean, true);
		assertFalse(lbUpdateStatus);
		moMyBatisSession.close();
	}
	
	
	@Test(expected = ApplicationException.class)
	public void testHhsauditInsertCase17() throws ApplicationException
	{
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		loHhsAuditBean.setAuditTableIdentifier(HHSConstants.PROVIDER_AUDIT);
		loHhsAuditBean.setData("Sav");
		loHhsAuditBean.setEventType(HHSConstants.PROVIDER_COMMENTS_DATA);
		loHhsAuditBean.setEventName("Submit");
		loHhsAuditBean.setEntityId("60");
		loHhsAuditBean.setEntityType(HHSConstants.AUDIT_INVOICES);
		loHhsAuditBean.setUserId("801");
		moHhsAuditService.hhsauditInsert(null, loHhsAuditBean, true);		
	}
	
	/**
	 * This method tests the execution of hhsMultiAuditInsert method and
	 * determines whether internal and provider comments are updated for the
	 * given workflow Id and task Id
	 * @throws ApplicationException
	 */
	@Test
	public void testHhsMultiAuditInsert() throws ApplicationException
	{
		List<HhsAuditBean> loUserCommentsList = new ArrayList<HhsAuditBean>();

		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		loHhsAuditBean.setAuditTableIdentifier(HHSConstants.ACCELERATOR_AUDIT);
		loHhsAuditBean.setData("Procurement Status has been changed to cancelled");
		loHhsAuditBean.setEventType(HHSConstants.CANCEL_PROC);
		loHhsAuditBean.setEventName(HHSConstants.CANCEL);
		loHhsAuditBean.setEntityId("723");
		loHhsAuditBean.setEntityType(HHSConstants.PROCUREMENT);
		loHhsAuditBean.setUserId("city_142");
		loUserCommentsList.add(loHhsAuditBean);
		Boolean lbUpdateStatus = moHhsAuditService.hhsMultiAuditInsert(moMyBatisSession, loUserCommentsList, true);
		assertTrue(lbUpdateStatus);
		moMyBatisSession.close();
	}
	
	
	/**
	 * This method tests the execution of hhsMultiAuditInsert method and
	 * determines whether internal and provider comments are updated for the
	 * given workflow Id and task Id
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testHhsMultiAuditInsertCase1() throws ApplicationException
	{
		List<HhsAuditBean> loUserCommentsList = new ArrayList<HhsAuditBean>();

		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		loHhsAuditBean.setAuditTableIdentifier(HHSConstants.NON_AUDIT_COMMENTS);
		loHhsAuditBean.setTaskId("1001");
		loHhsAuditBean.setWorkflowId("0399E833FB040D43A11E7C98AB62E8A4");
		loHhsAuditBean.setEntityType(HHSConstants.ACCEPT_PROPOSAL);
		loHhsAuditBean.setEntityId("0399E833FB040D43A11E7C98AB62E8A4");
		loHhsAuditBean.setUserId("agency_14");
		loHhsAuditBean.setAgencyId("DFTA");
		loHhsAuditBean.setProviderComments("This is a provider comment");
		loHhsAuditBean.setInternalComments("This is an internal comment by agency users");
		loUserCommentsList.add(loHhsAuditBean);
		moHhsAuditService.hhsMultiAuditInsert(null, loUserCommentsList, true);			
	}
	
	/**
	 * This method tests the execution of hhsMultiAuditInsert method and
	 * determines whether internal and provider comments are updated for the
	 * given workflow Id and task Id
	 * @throws ApplicationException
	 */
	@Test
	public void testHhsMultiAuditInsertCase2() throws ApplicationException
	{
		List<HhsAuditBean> loUserCommentsList = new ArrayList<HhsAuditBean>();

		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		loHhsAuditBean.setAuditTableIdentifier(HHSConstants.NON_AUDIT_COMMENTS);
		loHhsAuditBean.setTaskId("1001");
		loHhsAuditBean.setWorkflowId("0399E833FB040D43A11E7C98AB62E8A4");
		loHhsAuditBean.setEntityType(HHSConstants.ACCEPT_PROPOSAL);
		loHhsAuditBean.setEntityId("0399E833FB040D43A11E7C98AB62E8A4");
		loHhsAuditBean.setUserId("agency_14");
		loHhsAuditBean.setAgencyId("DFTA");
		loHhsAuditBean.setProviderComments("This is a provider comment");
		loHhsAuditBean.setInternalComments("This is an internal comment by agency users");
		loUserCommentsList.add(loHhsAuditBean);
		Boolean lbUpdateStatus = moHhsAuditService.hhsMultiAuditInsert(moMyBatisSession, loUserCommentsList, false);	
		assertFalse(lbUpdateStatus);
		moMyBatisSession.close();
	}
	
	/**
	 * This method tests the execution of hhsMultiAuditInsert method and
	 * determines whether internal and provider comments are updated for the
	 * given workflow Id and task Id
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testHhsMultiAuditInsertCase3() throws ApplicationException
	{
		List<HhsAuditBean> loUserCommentsList = new ArrayList<HhsAuditBean>();
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		loHhsAuditBean.setAuditTableIdentifier(HHSConstants.NON_AUDIT_COMMENTS);
		loHhsAuditBean.setTaskId("1001");
		loHhsAuditBean.setWorkflowId("0399E833FB040D43A11E7C98AB62E8A4");
		loHhsAuditBean.setEntityType(HHSConstants.ACCEPT_PROPOSAL);
		loHhsAuditBean.setEntityId("0399E833FB040D43A11E7C98AB62E8A4");
		loHhsAuditBean.setUserId("agency_14");
		loHhsAuditBean.setAgencyId("DFTA");
		loHhsAuditBean.setProviderComments("This is a provider comment");
		loHhsAuditBean.setInternalComments("This is an internal comment by agency users");
		loUserCommentsList.add(null);
		moHhsAuditService.hhsMultiAuditInsert(moMyBatisSession, loUserCommentsList, true);			
		moMyBatisSession.close();
	}
	
	/**
	 * This method tests the execution of hhsMultiAuditInsert method and
	 * determines whether internal and provider comments are updated for the
	 * given workflow Id and task Id
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testHhsMultiAuditInsertCase4() throws ApplicationException
	{
		List<HhsAuditBean> loUserCommentsList = new ArrayList<HhsAuditBean>();

		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		loHhsAuditBean.setAuditTableIdentifier(HHSConstants.NON_AUDIT_COMMENTS);
		loHhsAuditBean.setTaskId("1001");
		loHhsAuditBean.setWorkflowId(null);
		loHhsAuditBean.setEntityType(HHSConstants.ACCEPT_PROPOSAL);
		loHhsAuditBean.setEntityId("0399E833FB040D43A11E7C98AB62E8A4");
		loHhsAuditBean.setUserId("agency_14");
		loHhsAuditBean.setAgencyId("DFTA");
		loHhsAuditBean.setProviderComments("This is a provider comment");
		loHhsAuditBean.setInternalComments("This is an internal comment by agency users");
		loUserCommentsList.add(loHhsAuditBean);
		moHhsAuditService.hhsMultiAuditInsert(moMyBatisSession, loUserCommentsList, true);			
		moMyBatisSession.close();
	}
	
	/**
	 * This method tests the execution of hhsMultiAuditInsert method and
	 * determines whether internal and provider comments are updated for the
	 * given workflow Id and task Id
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testHhsMultiAuditInsertCase5() throws ApplicationException
	{
		List<HhsAuditBean> loUserCommentsList = new ArrayList<HhsAuditBean>();

		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		loHhsAuditBean.setAuditTableIdentifier(HHSConstants.NON_AUDIT_COMMENTS);
		loHhsAuditBean.setTaskId("1001");
		loHhsAuditBean.setWorkflowId("0399E833FB040D43A11E7C98AB62E8A4");
		loHhsAuditBean.setEntityType(HHSConstants.ACCEPT_PROPOSAL);
		loHhsAuditBean.setEntityId("0399E833FB040D43A11E7C98AB62E8A4");
		loHhsAuditBean.setUserId(null);
		loHhsAuditBean.setAgencyId("DFTA");
		loHhsAuditBean.setProviderComments("This is a provider comment");
		loHhsAuditBean.setInternalComments("This is an internal comment by agency users");
		loUserCommentsList.add(loHhsAuditBean);
		moHhsAuditService.hhsMultiAuditInsert(moMyBatisSession, loUserCommentsList, true);			
		moMyBatisSession.close();
	}
	
	/**
	 * This method tests the execution of hhsMultiAuditInsert method and
	 * determines whether internal and provider comments are updated for the
	 * given workflow Id and task Id
	 * @throws ApplicationException
	 */
	@Test(expected = Exception.class)
	public void testHhsMultiAuditInsertCase6() throws ApplicationException
	{
		List<HhsAuditBean> loUserCommentsList = new ArrayList<HhsAuditBean>();

		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		loHhsAuditBean.setAuditTableIdentifier(HHSConstants.NON_AUDIT_COMMENTS);
		loHhsAuditBean.setTaskId("1001");
		loHhsAuditBean.setWorkflowId("0399E833FB040D43A11E7C98AB62E8A4");
		loHhsAuditBean.setEntityType(HHSConstants.ACCEPT_PROPOSAL);
		loHhsAuditBean.setEntityId("0399E833FB040D43A11E7C98AB62E8A4");
		loHhsAuditBean.setUserId(null);
		loHhsAuditBean.setAgencyId("DFTA");
		loHhsAuditBean.setProviderComments("This is a provider comment");
		loHhsAuditBean.setInternalComments("This is an internal comment by agency users");
		loUserCommentsList.add(loHhsAuditBean);
		moHhsAuditService.hhsMultiAuditInsert(moMyBatisSession, loUserCommentsList, true);			
		moMyBatisSession.close();
	}
	
	/**
	 * This method tests the execution of hhsMultiAuditInsert method and
	 * determines whether internal and provider comments are updated for the
	 * given workflow Id and task Id
	 * @throws ApplicationException
	 */
	@Test(expected = Exception.class)
	public void testHhsMultiAuditInsertCase7() throws ApplicationException
	{
		List<HhsAuditBean> loUserCommentsList = new ArrayList<HhsAuditBean>();

		HhsAuditBean loHhsAuditBean = new HhsAuditBean();
		loHhsAuditBean.setAuditTableIdentifier(HHSConstants.NON_AUDIT_COMMENTS);
		loHhsAuditBean.setTaskId("1001");
		loHhsAuditBean.setWorkflowId("0399E833FB040D43A11E7C98AB62E8A4");
		loHhsAuditBean.setEntityType(HHSConstants.ACCEPT_PROPOSAL);
		loHhsAuditBean.setEntityId("0399E833FB040D43A11E7C98AB62E8A4");
		loHhsAuditBean.setUserId("agency_14");
		loHhsAuditBean.setAgencyId("DFTA");
		loHhsAuditBean.setProviderComments("This is a provider comment");
		loHhsAuditBean.setInternalComments("This is an internal comment by agency users");
		loUserCommentsList.add(loHhsAuditBean);
		moHhsAuditService.hhsMultiAuditInsert(null, loUserCommentsList, true);			
	}
	
	
	/**
	 * This method tests the execution of hhsMultiAuditInsert method and
	 * determines whether internal and provider comments are updated for the
	 * given workflow Id and task Id
	 * @throws ApplicationException
	 */
	@Test
	public void testdeleteFromUserComment() throws ApplicationException
	{
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();

		boolean lbStatus=moHhsAuditService.deleteFromUserComment(moMyBatisSession,loHhsAuditBean,true)	;	
		assertTrue(lbStatus);
	}
	
	/**
	 * This method tests the execution of hhsMultiAuditInsert method and
	 * determines whether internal and provider comments are updated for the
	 * given workflow Id and task Id
	 * @throws ApplicationException
	 */
	@Test
	public void testdeleteFromUserComment1() throws ApplicationException
	{
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();

		boolean lbStatus=moHhsAuditService.deleteFromUserComment(moMyBatisSession,loHhsAuditBean,false)	;	
		assertTrue(lbStatus);
	}
	
	/**
	 * This method tests the execution of hhsMultiAuditInsert method and
	 * determines whether internal and provider comments are updated for the
	 * given workflow Id and task Id
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testdeleteFromUserComment2() throws ApplicationException
	{
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();

		boolean lbStatus=moHhsAuditService.deleteFromUserComment(null,loHhsAuditBean,true)	;	
	}
	
	/**
	 * This method tests the execution of hhsMultiAuditInsert method and
	 * determines whether internal and provider comments are updated for the
	 * given workflow Id and task Id
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testdeleteFromUserComment3() throws ApplicationException
	{
		HhsAuditBean loHhsAuditBean = new HhsAuditBean();

		boolean lbStatus=moHhsAuditService.deleteFromUserComment(null,null,true)	;	
	}
	
	@Test
	public void testcopyCommentHistory() throws ApplicationException
	{
		HashMap<String,String> aoHMReqdProp = new HashMap<String, String>();
		
		aoHMReqdProp.put("newEntityType", "Accept Proposal Task");
        aoHMReqdProp.put("newEntityId", "341");
        aoHMReqdProp.put("entityType", "Invoice Review");
        aoHMReqdProp.put("entityId", "61");

		boolean lbStatus=moHhsAuditService.copyCommentHistory(moMyBatisSession,aoHMReqdProp,true)	;	
		assertTrue(lbStatus);
		
	}
	
	@Test
	public void testcopyCommentHistory1() throws ApplicationException
	{
		HashMap<String, String> aoHMReqdProp = new HashMap<String, String>();

		aoHMReqdProp.put("newEntityType", "Accept Proposal Task");
		aoHMReqdProp.put("newEntityId", "341");
		aoHMReqdProp.put("entityType", "Invoice Review");
		aoHMReqdProp.put("entityId", "61");

		boolean lbStatus = moHhsAuditService.copyCommentHistory(moMyBatisSession, aoHMReqdProp, false);
		assertTrue(lbStatus);
	}
	
	
}
