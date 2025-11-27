package com.nyc.hhs.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.UnavailableException;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.FaqFormDetailBean;
import com.nyc.hhs.model.FaqFormMasterBean;
import com.nyc.hhs.model.UserThreadLocal;
import com.nyc.hhs.util.ApplicationSession;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.PortalUtil;

/**
 * This controller is for the maintenance of FAQ with major operations like-
 * add, update, delete topic and question answers
 * 
 */

public class MaintenanceFaqController extends GenericPortlet
{

	private static final LogInfo LOG_OBJECT = new LogInfo(MaintenanceFaqController.class);

	private static final String NEW_UNTITLED_TOPIC = "New Untitled Topic";
	private static final String PROVIDERADDCLIKD = "provideraddclikd";
	private static final String AGENCY = "agency";
	private static final String PROVIDER = "provider";
	private static final String QUESTIONS_LIST = "questionsList";
	private static final String FORMSUBMIT = "formsubmit";
	private static final String ONLY_TOPIC_UPDATE = "onlyTopicUpdate";
	private static final String SELECTED_ANSWER = "selectedAnswer";
	private static final String SELECTED_QUESTION = "selectedQuestion";
	private static final String FAQ_MAINTENANCE_QA_UPDATE = "faqMaintenanceQAUpdate";
	private static final String FAQ_MAINTENANCE_QA_INSERT = "faqMaintenanceQAInsert";
	private static final String FAQ_MAINTENANCE_QA = "faqMaintenanceQA";
	private static final String LINK_VALUE = "linkValue";
	private static final String TOPIC_LIST = "topicList";
	private static final String GET_TOPIC_LIST = "getTopicList";
	private static final String MAINTENANCE_FAQ = "maintenanceFaq";
	private static final String LANDING = "landing";
	private static final String LI_TOPIC_ID = "liTopicId";
	private static final String LS_MASTER_BEAN = "lsMasterBean";
	private static final String FAQ_MAINTENANCE_QA_DELETE = "faqMaintenanceQADelete";
	private static final String LS_DETAIL_BEAN = "lsDetailBean";
	private static final String MASTER_TOPIC_LIST = "masterTopicList";
	private static final String FILE_PATH = "filePath";
	private static final String REQUEST_COULD_NOT_BE_COMPLETED = "This request could not be completed. Please try again in a few minutes.";
	private static final String TOPIC_SUCCESSFULLY_ADDED = "Topic has been added successfully.";
	private static final String QUESTION_ANSWER_SUCCESSFULLY_ADDED = "Question/Answer has been added successfully.";
	private static final String QUESTION_ANSWER_SUCCESSFULLY_UPDATED = "Topic and Question/Answer has been updated successfully.";

	/**
	 * This method is to render the next page depending on the action, FAQ
	 * maintenance process
	 * 
	 * @param aoRequest to get screen parameters and next page to be displayed
	 * @param aoResponse setting response parameter for JSP variables
	 * @throws PortletException
	 * @throws IOException
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void doView(RenderRequest aoRequest, RenderResponse aoResponse) throws PortletException, IOException
	{
		long loStartTime = System.currentTimeMillis();
		PortletSession loPortletSessionThread = aoRequest.getPortletSession();
		String lsUserIdThreadLocal = (String) loPortletSessionThread.getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
		UserThreadLocal.setUser(lsUserIdThreadLocal);
		String lsFilePath = ApplicationConstants.MAINTENANCE_MANAGE_FAQ;
		List<FaqFormMasterBean> loTempList = null;
		// navigation to Manage FAQ page
		if (null != aoRequest.getPortletSession().getAttribute("publish", PortletSession.APPLICATION_SCOPE)
				|| MaintenanceFaqController.LANDING.equalsIgnoreCase(PortalUtil.parseQueryString(aoRequest,
						"navigatefrom")))
		{
			Channel loChannelObj = new Channel();
			try
			{
				navigateToManageFaq(aoRequest, aoResponse, loChannelObj);
			}
			catch (ApplicationException aoFbAppEx)
			{
				LOG_OBJECT.Error("Error occured in getting Form Data ", aoFbAppEx);
			}
		}
		loTempList = (List<FaqFormMasterBean>) ApplicationSession.getAttribute(aoRequest, false,
				MaintenanceFaqController.MASTER_TOPIC_LIST);
		aoRequest.setAttribute("topicListMaster", loTempList);
		aoRequest.setAttribute(MaintenanceFaqController.TOPIC_LIST, (List<FaqFormDetailBean>) ApplicationSession
				.getAttribute(aoRequest, true, MaintenanceFaqController.TOPIC_LIST));
		aoRequest.setAttribute(MaintenanceFaqController.LINK_VALUE,
				(String) ApplicationSession.getAttribute(aoRequest, true, MaintenanceFaqController.LINK_VALUE));
		// setting File path
		if (aoRequest.getParameter(MaintenanceFaqController.FILE_PATH) != null)
		{
			lsFilePath = (String) aoRequest.getParameter(MaintenanceFaqController.FILE_PATH);
		}
		/**
		 * This condition sets the top level status message of the transaction
		 * on screen
		 */
		if (aoRequest.getParameter("transactionMessage") != null
				&& !"".equalsIgnoreCase(aoRequest.getParameter("transactionMessage")))
		{
			aoRequest.setAttribute("transactionStatus", aoRequest.getParameter("transactionStatus"));
			aoRequest.setAttribute("transactionMessage", aoRequest.getParameter("transactionMessage"));
		}
		long loEndTimeTime = System.currentTimeMillis();
		try
		{
			LOG_OBJECT.Debug("TIME TAKEN for execution of render Method in MaintenanceFaqController = "
					+ (loEndTimeTime - loStartTime));
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Error while execution of render Method in MaintenanceFaqController", aoEx);
		}
		UserThreadLocal.unSet();
		PortletRequestDispatcher loObjDispatcher = getPortletContext().getRequestDispatcher(lsFilePath);
		loObjDispatcher.include(aoRequest, aoResponse);
	}

	/**
	 * This method decide the execution flow for FAQ maintenance process
	 * 
	 * @param aoRequest to get screen parameters and next action to be performed
	 * @param aoResponse decides the next execution flow
	 * @throws PortletException
	 * @throws IOException
	 * @throws UnavailableException
	 */
	@Override
	public void processAction(ActionRequest aoRequest, ActionResponse aoResponse) throws PortletException, IOException,
			UnavailableException
	{
		long loStartTime = System.currentTimeMillis();
		PortletSession loPortletSessionThread = aoRequest.getPortletSession();
		String lsUserIdThreadLocal = (String) loPortletSessionThread.getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
		UserThreadLocal.setUser(lsUserIdThreadLocal);
		if (aoRequest.getPortletSession().getAttribute("publish", PortletSession.APPLICATION_SCOPE) == null)
		{
			String lsNextAction = aoRequest.getParameter("next_action");
			String lsSelectedValue = aoRequest.getParameter("selectedValue");
			String lsButtonValue = (String) aoRequest.getParameter("submitButtonValue");
			String lsTopicName = aoRequest.getParameter("topicName");
			String lsTopicId = aoRequest.getParameter("topicId");
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			Channel loChannelObj = new Channel();
			String lsTransactionStatusMsg = "", lsTransactionStatus = "";

			try
			{
				// adds new topic to provider or agency depending which add
				// button (provider/agency) is clicked
				if (lsButtonValue != null
						&& (lsButtonValue.equalsIgnoreCase(MaintenanceFaqController.PROVIDERADDCLIKD) || lsButtonValue
								.equalsIgnoreCase("agencyaddclikd")))
				{
					lsTransactionStatus = "failed";
					lsTransactionStatusMsg = REQUEST_COULD_NOT_BE_COMPLETED;
					addNewTopic(aoRequest, aoResponse, lsButtonValue, lsUserId);
					lsTransactionStatus = "passed";
					lsTransactionStatusMsg = MaintenanceFaqController.TOPIC_SUCCESSFULLY_ADDED;
				}

				// directs to FAQ update page
				
				else if (lsTopicId != null)
				{

					loChannelObj.setData("asTopicId", lsTopicId);
					TransactionManager.executeTransaction(loChannelObj, "getTopicName");
					lsTopicName = (String) loChannelObj.getData("asTopicName");
					navigateToManageFaq(aoRequest, aoResponse, loChannelObj);
					int liTopicId = Integer.valueOf(lsTopicId);
					ApplicationSession.setAttribute(liTopicId, aoRequest, MaintenanceFaqController.LI_TOPIC_ID);
					loadFaq(aoRequest, aoResponse, lsTopicName, liTopicId, loChannelObj);

				}

				// deletes the topic based on topic id from FAQ Detail and
				// Master table
				else if (null != lsNextAction && lsNextAction.equals("deleteRequest"))
				{
					int liTopicId = (Integer) ApplicationSession.getAttribute(aoRequest, true,
							MaintenanceFaqController.LI_TOPIC_ID);

					FaqFormDetailBean lsFaqDetailBean = new FaqFormDetailBean();
					lsFaqDetailBean.setMiTopicId(liTopicId);
					loChannelObj.setData(MaintenanceFaqController.LS_DETAIL_BEAN, lsFaqDetailBean);

					FaqFormMasterBean lsFaqMasterBean = new FaqFormMasterBean();
					lsFaqMasterBean.setMiTopicId(liTopicId);
					loChannelObj.setData(MaintenanceFaqController.LS_MASTER_BEAN, lsFaqMasterBean);

					TransactionManager.executeTransaction(loChannelObj,
							MaintenanceFaqController.FAQ_MAINTENANCE_QA_DELETE);
					navigateToManageFaq(aoRequest, aoResponse, loChannelObj);
				}
				// deletes the question based on question id(question selected
				// from drop down) from FAQ Detail table
				else if (null != lsNextAction && lsNextAction.equals("deleteQuestion"))
				{
					deleteQuestion(aoRequest, aoResponse, lsSelectedValue, loChannelObj);
				}
				// deletes topic from FAQ Master table
				else if (null != lsNextAction && lsNextAction.equals(MaintenanceFaqController.FORMSUBMIT)
						&& null != lsSelectedValue)
				{
					if (!"".equalsIgnoreCase(lsSelectedValue)
							&& MaintenanceFaqController.ONLY_TOPIC_UPDATE.equalsIgnoreCase(lsSelectedValue))
					{
						deleteTopic(aoRequest, aoResponse, lsUserId, loChannelObj);
						// update question and answer in Detail table and
						// corresponding topic id,topic name in Master table
					}
					else if (!"".equalsIgnoreCase(lsSelectedValue))
					{
						lsTransactionStatus = "failed";
						lsTransactionStatusMsg = REQUEST_COULD_NOT_BE_COMPLETED;
						updateQuestionAnswer(aoRequest, aoResponse, lsSelectedValue, lsUserId, loChannelObj);
						lsTransactionStatus = "passed";
						lsTransactionStatusMsg = MaintenanceFaqController.QUESTION_ANSWER_SUCCESSFULLY_UPDATED;
					}
					// insert question and answer in Detail table and
					// corresponding topic id in Master table
					else
					{
						lsTransactionStatus = "failed";
						lsTransactionStatusMsg = REQUEST_COULD_NOT_BE_COMPLETED;
						insertQuestionAnswer(aoRequest, aoResponse, lsUserId, loChannelObj);
						lsTransactionStatus = "passed";
						lsTransactionStatusMsg = MaintenanceFaqController.QUESTION_ANSWER_SUCCESSFULLY_ADDED;
					}
				}
			}
			catch (ApplicationException aoFbAppEx)
			{
				LOG_OBJECT.Error("Error occured in MaintenanceFaqController Controller ", aoFbAppEx);
			}
			catch (Exception aoExp)
			{
				LOG_OBJECT.Error("Error occured in MaintenanceFaqController Controller ", aoExp);
			}
			aoResponse.setRenderParameter("transactionStatus", lsTransactionStatus);
			aoResponse.setRenderParameter("transactionMessage", lsTransactionStatusMsg);
		}

		long loEndTimeTime = System.currentTimeMillis();
		try
		{
			LOG_OBJECT.Debug("TIME TAKEN for execution of action Method in MaintenanceFaqController = "
					+ (loEndTimeTime - loStartTime));
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Error while execution of action Method in MaintenanceFaqController", aoEx);
		}
		UserThreadLocal.unSet();
	}

	/**
	 * This method is to delete question and its linked answer
	 * 
	 * @param aoRequest to get the session and screen parameters
	 * @param aoResponse to set response parameter that is used in JSP
	 * @param asSelectedValue selected question to delete it
	 * @param aoChannelObj Channel Object that needs to be set for to and fro of
	 *            values
	 * @throws NumberFormatException
	 * @throws ApplicationException
	 */
	private void deleteQuestion(ActionRequest aoRequest, ActionResponse aoResponse, String asSelectedValue,
			Channel aoChannelObj) throws NumberFormatException, ApplicationException
	{
		int liQuestionId = Integer.valueOf(asSelectedValue);

		FaqFormDetailBean lsFaqDetailBean = new FaqFormDetailBean();
		lsFaqDetailBean.setMiQuestionId(liQuestionId);
		aoChannelObj.setData(MaintenanceFaqController.LS_DETAIL_BEAN, lsFaqDetailBean);

		TransactionManager.executeTransaction(aoChannelObj, "faqMaintenanceDeleteQuestion");
		navigateToManageFaq(aoRequest, aoResponse, aoChannelObj);
	}

	/**
	 * This method is to add new topic for provider or agency
	 * 
	 * @param aoRequest to get the session and screen parameters
	 * @param aoResponse to set response parameter that is used in JSP
	 * @param asButtonValue to check whether topic is to be added under provider
	 *            or agency
	 * @param asUserId User Id
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private void addNewTopic(ActionRequest aoRequest, ActionResponse aoResponse, String asButtonValue, String asUserId)
			throws ApplicationException
	{
		List<FaqFormMasterBean> loMasterTopicList;
		FaqFormMasterBean lsFaqMasterBean = new FaqFormMasterBean();
		Channel loChannelObj2 = new Channel();
		if (asButtonValue.equalsIgnoreCase(MaintenanceFaqController.PROVIDERADDCLIKD))
		{
			lsFaqMasterBean.setMsType(MaintenanceFaqController.PROVIDER);
		}
		else if (asButtonValue.equalsIgnoreCase("agencyaddclikd"))
		{
			lsFaqMasterBean.setMsType(MaintenanceFaqController.AGENCY);
		}

		lsFaqMasterBean.setMsTopicName(MaintenanceFaqController.NEW_UNTITLED_TOPIC);
		lsFaqMasterBean.setMoModifiedDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
		lsFaqMasterBean.setMsCreatedBy(asUserId);
		lsFaqMasterBean.setMsModifiedBy(asUserId);

		loChannelObj2.setData(MaintenanceFaqController.LS_MASTER_BEAN, lsFaqMasterBean);
		TransactionManager.executeTransaction(loChannelObj2, "maintenance_add_topic");

		loMasterTopicList = (ArrayList) loChannelObj2.getData(MaintenanceFaqController.GET_TOPIC_LIST);
		ApplicationSession.setAttribute(loMasterTopicList, aoRequest, MaintenanceFaqController.MASTER_TOPIC_LIST);
		aoResponse.setRenderParameter(MaintenanceFaqController.FILE_PATH, ApplicationConstants.MAINTENANCE_MANAGE_FAQ);
	}

	/**
	 * This method is to delete topic and its linked questions and answers
	 * 
	 * @param aoRequest to get the session and screen parameters
	 * @param aoResponse to set response parameter that is used in JSP
	 * @param lsUserId User Id
	 * @param aoChannelObj channel object required to execute transaction
	 * @throws ApplicationException
	 */
	private void deleteTopic(ActionRequest aoRequest, ActionResponse aoResponse, String lsUserId, Channel aoChannelObj)
			throws ApplicationException
	{
		String lsTopicName;
		int liTopicId = (Integer) ApplicationSession
				.getAttribute(aoRequest, true, MaintenanceFaqController.LI_TOPIC_ID);
		lsTopicName = (String) aoRequest.getParameter(MaintenanceFaqController.LINK_VALUE);

		FaqFormMasterBean lsFaqMasterBean = new FaqFormMasterBean();
		lsFaqMasterBean.setMiTopicId(liTopicId);
		lsFaqMasterBean.setMsTopicName(lsTopicName);
		lsFaqMasterBean.setMsModifiedBy(lsUserId);
		lsFaqMasterBean.setMoModifiedDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
		aoChannelObj.setData(MaintenanceFaqController.LS_MASTER_BEAN, lsFaqMasterBean);

		TransactionManager.executeTransaction(aoChannelObj, "faqMaintenanceQAUpdateTopic");
		loadFaq(aoRequest, aoResponse, lsTopicName, liTopicId, aoChannelObj);
	}

	/**
	 * This method is to insert question and answer for a particular topic
	 * 
	 * @param aoRequest to get the session and screen parameters
	 * @param aoResponse to set response parameter that is used in JSP
	 * @param lsUserId User Id
	 * @param aoChannelObj channel object required to execute transaction
	 * @throws ApplicationException
	 */
	private void insertQuestionAnswer(ActionRequest aoRequest, ActionResponse aoResponse, String lsUserId,
			Channel aoChannelObj) throws ApplicationException
	{
		String lsTopicName;
		String lsQuestion = (String) aoRequest.getParameter(MaintenanceFaqController.SELECTED_QUESTION);
		String lsAnswer = (String) aoRequest.getParameter(MaintenanceFaqController.SELECTED_ANSWER);
		lsTopicName = (String) aoRequest.getParameter(MaintenanceFaqController.LINK_VALUE);
		int liTopicId = (Integer) ApplicationSession
				.getAttribute(aoRequest, true, MaintenanceFaqController.LI_TOPIC_ID);

		FaqFormMasterBean lsFaqMasterBean = new FaqFormMasterBean();
		lsFaqMasterBean.setMiTopicId(liTopicId);
		lsFaqMasterBean.setMsTopicName(lsTopicName);
		lsFaqMasterBean.setMsModifiedBy(lsUserId);
		lsFaqMasterBean.setMoModifiedDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
		aoChannelObj.setData(MaintenanceFaqController.LS_MASTER_BEAN, lsFaqMasterBean);

		FaqFormDetailBean lsFaqDetailBean = new FaqFormDetailBean();
		lsFaqDetailBean.setMsQuestion(lsQuestion);
		lsFaqDetailBean.setMsAnswer(lsAnswer);
		lsFaqDetailBean.setMiTopicId(liTopicId);
		lsFaqDetailBean.setMsCreatedBy(lsUserId);
		lsFaqDetailBean.setMsModifiedBy(lsUserId);
		lsFaqDetailBean.setMoModifiedDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
		aoChannelObj.setData(MaintenanceFaqController.LS_DETAIL_BEAN, lsFaqDetailBean);

		TransactionManager.executeTransaction(aoChannelObj, MaintenanceFaqController.FAQ_MAINTENANCE_QA_INSERT);
		loadFaq(aoRequest, aoResponse, lsTopicName, liTopicId, aoChannelObj);
	}

	/**
	 * This method is to update question and answer linked to topic
	 * 
	 * @param aoRequest to get the session and screen parameters
	 * @param aoResponse to set response parameter that is used in JSP
	 * @param asSelectedValue selected question to delete it
	 * @param asUserId User Id
	 * @param aoChannelObj channel object required to execute transaction
	 * @throws NumberFormatException
	 * @throws ApplicationException
	 */

	private void updateQuestionAnswer(ActionRequest aoRequest, ActionResponse aoResponse, String asSelectedValue,
			String asUserId, Channel aoChannelObj) throws NumberFormatException, ApplicationException
	{
		String lsTopicName;
		int liQuestionId = Integer.valueOf(asSelectedValue);
		int liTopicId = (Integer) ApplicationSession
				.getAttribute(aoRequest, true, MaintenanceFaqController.LI_TOPIC_ID);
		String lsQuestion = (String) aoRequest.getParameter(MaintenanceFaqController.SELECTED_QUESTION);
		String lsAnswer = (String) aoRequest.getParameter(MaintenanceFaqController.SELECTED_ANSWER);
		lsTopicName = (String) aoRequest.getParameter(MaintenanceFaqController.LINK_VALUE);

		FaqFormMasterBean lsFaqMasterBean = new FaqFormMasterBean();
		lsFaqMasterBean.setMiTopicId(liTopicId);
		lsFaqMasterBean.setMsTopicName(lsTopicName);
		lsFaqMasterBean.setMsModifiedBy(asUserId);
		lsFaqMasterBean.setMoModifiedDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
		aoChannelObj.setData(MaintenanceFaqController.LS_MASTER_BEAN, lsFaqMasterBean);

		FaqFormDetailBean lsFaqDetailBean = new FaqFormDetailBean();
		lsFaqDetailBean.setMiQuestionId(liQuestionId);
		lsFaqDetailBean.setMsQuestion(lsQuestion);
		lsFaqDetailBean.setMsAnswer(lsAnswer);
		lsFaqDetailBean.setMsModifiedBy(asUserId);
		lsFaqDetailBean.setMoModifiedDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
		aoChannelObj.setData(MaintenanceFaqController.LS_DETAIL_BEAN, lsFaqDetailBean);

		TransactionManager.executeTransaction(aoChannelObj, MaintenanceFaqController.FAQ_MAINTENANCE_QA_UPDATE);
		
		loadFaq(aoRequest, aoResponse, lsTopicName, liTopicId, aoChannelObj);
	}

	/**
	 * This method populates the FAQ detail bean
	 * 
	 * @param aoRequest to get the session and screen parameters
	 * @param aoResponse to set response parameter that is used in jsp
	 * @param asTopicName topic name
	 * @param aiTopicId Topic id linked to topic
	 * @param aoChannelObj channel object required to execute transaction
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked" })
	private void loadFaq(ActionRequest aoRequest, ActionResponse aoResponse, String asTopicName, int aiTopicId,
			Channel aoChannelObj) throws ApplicationException
	{
		List<FaqFormDetailBean> loDetailTopicList;
		FaqFormDetailBean lsFaqDetailBean = new FaqFormDetailBean();
		lsFaqDetailBean.setMiTopicId(aiTopicId);
		aoChannelObj.setData(MaintenanceFaqController.LS_DETAIL_BEAN, lsFaqDetailBean);

		TransactionManager.executeTransaction(aoChannelObj, MaintenanceFaqController.FAQ_MAINTENANCE_QA);

		loDetailTopicList = (List<FaqFormDetailBean>) aoChannelObj.getData(MaintenanceFaqController.QUESTIONS_LIST);
		ApplicationSession.setAttribute(loDetailTopicList, aoRequest, MaintenanceFaqController.TOPIC_LIST);
		ApplicationSession.setAttribute(asTopicName, aoRequest, MaintenanceFaqController.LINK_VALUE); // just
																										// now
																										// changed
		aoResponse.setRenderParameter(MaintenanceFaqController.FILE_PATH, ApplicationConstants.MAINTENANCE_UPDATE_FAQ);
	}

	/**
	 * This method provide navigation to manage FAQ page for adding topic under
	 * provider or agency (called from action view)
	 * 
	 * @param aoRequest to get the session and screen parameters
	 * @param aoResponse to set response parameter that is used in jsp
	 * @param aoChannelObj channel object required to execute transaction
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private void navigateToManageFaq(ActionRequest aoRequest, ActionResponse aoResponse, Channel aoChannelObj)
			throws ApplicationException
	{
		List<FaqFormMasterBean> loMasterTopicList;
		TransactionManager.executeTransaction(aoChannelObj, MaintenanceFaqController.MAINTENANCE_FAQ);
		loMasterTopicList = (ArrayList) aoChannelObj.getData(MaintenanceFaqController.GET_TOPIC_LIST);

		ApplicationSession.setAttribute(loMasterTopicList, aoRequest, MaintenanceFaqController.MASTER_TOPIC_LIST);
		aoResponse.setRenderParameter(MaintenanceFaqController.FILE_PATH, ApplicationConstants.MAINTENANCE_MANAGE_FAQ);
	}

	/**
	 * This method provide navigation to manage FAQ page for adding topic under
	 * provider or agency (called from render view)
	 * 
	 * @param aoRequest to get the session and screen parameters
	 * @param aoResponse to set response parameter that is used in jsp
	 * @param aoChannelObj channel object required to execute transaction
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private void navigateToManageFaq(RenderRequest aoRequest, RenderResponse aoResponse, Channel aoChannelObj)
			throws ApplicationException
	{
		List<FaqFormMasterBean> loMasterTopicList;
		TransactionManager.executeTransaction(aoChannelObj, MaintenanceFaqController.MAINTENANCE_FAQ);
		loMasterTopicList = (ArrayList) aoChannelObj.getData(MaintenanceFaqController.GET_TOPIC_LIST);
		ApplicationSession.setAttribute(loMasterTopicList, aoRequest, MaintenanceFaqController.MASTER_TOPIC_LIST);
	}
}
