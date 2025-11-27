package com.nyc.hhs.frameworks.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang.StringUtils;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.common.InformationMessageTag;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.ContractList;
import com.nyc.hhs.model.ExtendedDocument;
import com.nyc.hhs.model.StaffDetails;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.HHSTokenUtil;
import com.nyc.hhs.util.HHSUtil;

/**
 * This is a custom tag class which is executed by the JSTL to generate the Task
 * Header and Footer element.
 */

public class TaskHeaderFooter extends BodyTagSupport
{

	private static final LogInfo LOG_OBJECT = new LogInfo(TaskHeaderFooter.class);

	private static final long serialVersionUID = 1L;
	private String workFlowId;
	private String level;
	private String taskType;
	private String taskDetail;
	private String commentsSection;
	private String internalLastCommentQuery;
	private String providerLastCommentQuery;
	private String fetchHistoryQuery;
	private String isTaskScreen;
	private String showDocument;
	private String taskToBeLaunched;
	private String entityTypeForAgency;
	private String entityType;
	private String entityTypeTabLevel; // R4: Tab Level Comments

	// Added in R6
	private String textAreaSize;

	private static final String STR_JS_1 = "<script type='text/javascript'> $(document).ready(function () "
			+ "{$('#documentWrapper').html($('#documentSection').html());$('#documentSection').html('')});</script>";

	// Added in R6 : Returned Payment Task - Fix for View Document Information
	// on first time loading
	private static final String STR_JS_2 = "<script type='text/javascript'> $(document).ready(function () "
			+ "{$('#documentSection').appendTo('#documentWrapper');});</script>";

	// End in R6

	/**
	 * This method is invoked by the JSP page implementation object It process
	 * the end tag for this instance.It generate common Header and footer
	 * structures for all Financial task screens and footer sections for common
	 * screens <li>updated in R4</li>
	 * <ul>
	 * <li>For Task screens call both 'generateTaskHeader' and
	 * 'generateTaskFooter' method</li>
	 * <li>For Non task screens call 'generateTaskFooter' method</li>
	 * <li>Set TaskDetailsBean object in session</li>
	 * </ul>
	 * 
	 * @returns int SKIP_BODY is the valid return value for doEndTag and
	 *          signifies that tag does not wants to process the body.
	 */
	@Override
	public int doEndTag()
	{
		try
		{
			HttpSession loSession = pageContext.getSession();
			HttpServletRequest loRequest = (HttpServletRequest) pageContext.getRequest();
			String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID);
			String lsUserRole = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ROLE);
			JspWriter loOut = pageContext.getOut();
			TaskDetailsBean loTaskDetailsBean = null;
			StringBuffer loStringBuffer = new StringBuffer();

			loTaskDetailsBean = (TaskDetailsBean) loSession.getAttribute(HHSConstants.TASK_DETAIL_BEAN_SESSION);
			loTaskDetailsBean.setTaskType(taskType);
			loTaskDetailsBean.setUserId(lsUserId);
			loTaskDetailsBean.setUserRole(lsUserRole);
			if (isTaskScreen == null)
			{
				loTaskDetailsBean.setIsTaskScreen(false);
			}
			else
			{
				loTaskDetailsBean.setIsTaskScreen(true);
			}

			if (HHSConstants.HEADER.equalsIgnoreCase(level))
			{
				loStringBuffer = generateTaskHeader(loRequest, loSession, loTaskDetailsBean, loStringBuffer);
				// set TaskDetailBean into session
				loSession.setAttribute(HHSConstants.TASK_DETAIL_BEAN_SESSION, loTaskDetailsBean);
			}
			else
			{
				if (isTaskScreen != null)
				{
					// get bean from session
					loTaskDetailsBean = (TaskDetailsBean) loSession.getAttribute(HHSConstants.TASK_DETAIL_BEAN_SESSION);
				}
				loStringBuffer = generateTaskFooter(loRequest, loSession, loTaskDetailsBean, loStringBuffer,
						pageContext);
				// Defect - 5768
				// Start Show View Comments History By Default
				if (null != entityTypeTabLevel && !entityTypeTabLevel.isEmpty())
				{
					loStringBuffer.append("<script>$(document).ready(function() {$('#commentsHistoryWrapperIdTabLevel"
							+ entityTypeTabLevel + "').trigger('click');});</script>");
				}
				// End (Defect - 5768)
				// set bean again in session with same attribute
				loSession.setAttribute(HHSConstants.TASK_DETAIL_BEAN_SESSION, loTaskDetailsBean);
			}
			loOut.print(loStringBuffer.toString());
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error in generating Task Header Footer", aoExp);
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error in generating Task Header Footer", aoExp);
		}

		return (EVAL_BODY_INCLUDE);
	}

	/**
	 * This method modifies as part of Release 3.12.0 enhancement 6602 This
	 * method create Task Header Section for all financial task screens
	 * <ul>
	 * <li>Call 'fetchTaskDetails' to get task details from Filenet</li>
	 * <li>Get all Agency users details to show in reassign dropdown by calling
	 * 'fetchAgencyDetails' method</li>
	 * <li>Generate Task Header Section(HTML tag) in java</li>
	 * <li>updated in R4</li>
	 * </ul>
	 * @param aoRequest to get request parameters and next page to be displayed
	 * @param aoResponse setting response parameter
	 * @param aoTaskDetailsBean TaskDetailsBean having task page related
	 *            attributes
	 * @param aoStringBuffer StringBuffer to construct html elements.
	 * @return aoStringBuffer StringBuffer
	 * @throws ApplicationException
	 * @throws IOException
	 * @throws ServletException
	 */
	@SuppressWarnings("rawtypes")
	private StringBuffer generateTaskHeader(HttpServletRequest aoRequest, HttpSession aoSession,
			TaskDetailsBean aoTaskDetailsBean, StringBuffer aoStringBuffer) throws ApplicationException,
			ServletException, IOException
	{
		// Start of changes for fixing defect for enhancement 5688 Release 3.2.0
		String lsUserOrg = (String) aoSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE);
		// End of changes for fixing defect for enhancement 5688 Release 3.2.0
		aoTaskDetailsBean = fetchTaskDetails(aoTaskDetailsBean);
		String lsAgencyId = aoTaskDetailsBean.getAgencyId();
		String lsTaskLevel = aoTaskDetailsBean.getLevel();
		if (aoTaskDetailsBean.getTaskName().equalsIgnoreCase(HHSConstants.TASK_NEW_FY_CONFIGURATION))
		{
			// isAlreadyLaunchedFYTask method called to set
			// isAlreadyLaunchedFYTask in TaskDetailsBean bean
			isAlreadyLaunchedFYTask(aoTaskDetailsBean);
		}
		Boolean loEnableDropdown = checkEnableDisableDropdown(aoTaskDetailsBean);
		String lsTaskName = aoTaskDetailsBean.getTaskName();
		HashMap<String, String> loUserMap = fetchAgencyDetails(aoTaskDetailsBean, lsAgencyId, lsTaskLevel, lsTaskName);
		Iterator loIterate = loUserMap.entrySet().iterator();
		String lsTaskInboxUrl = null;
		if (lsUserOrg.equalsIgnoreCase(HHSConstants.USER_CITY))
		{
			lsTaskInboxUrl = aoRequest.getContextPath()
					+ "/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_task&_nfls=false&returnToAgencyTask=true&controller_action=agencyWorkflowCity&controller_bean_id=propAgencyTaskHandler";

		}
		else
		{
			lsTaskInboxUrl = aoRequest.getContextPath()
					+ "/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&returnToAgencyTask=true";
		}

		aoStringBuffer.append("<div id='taskErrorDiv' class='failed' > </div>");

		// added in r7 for flag contract message
		if (!aoTaskDetailsBean.getTaskName().equalsIgnoreCase("")
				&& !aoTaskDetailsBean.getTaskName().equalsIgnoreCase(HHSR5Constants.PROCUREMENT_CERTIFICATION_FUNDS)
				&& !aoTaskDetailsBean.getTaskName().equalsIgnoreCase(HHSConstants.TASK_PROCUREMENT_COF))
		{
			ContractList loContractList = (ContractList) new InformationMessageTag()
					.fetchFlagContractMessage(aoTaskDetailsBean.getContractId());
			if (null != loContractList)
			{
				aoStringBuffer.append("<div  class='clear' > </div>");
                aoStringBuffer.append("<div id='contractMessage' class='flagInfoMessage' style='display:block;margin: 2px 0px'>");
                aoStringBuffer.append("<table style='width:100%'> <tr> <td class='flagIconR7' style='width:96%'>   </td>");
                
                aoStringBuffer.append("<td style='width:98%' >"
                            + "This contract has been flagged by " + loContractList.getModifyBy()
                            + " for the following reason(s): " + loContractList.getContractMessage() + "</td> </tr></table> </div>");
                aoStringBuffer.append("<div  class='clear' > </div>");


			}
		}
		// R7 end
		if (aoRequest.getAttribute(HHSConstants.ACCESS_SCREEN_ENABLE) != null
				&& !(Boolean) aoRequest.getAttribute(HHSConstants.ACCESS_SCREEN_ENABLE))
		{
			aoStringBuffer
					.append("<div class='lockMessage' style='display:block'>"
							+ aoRequest.getAttribute(HHSConstants.LOCKED_BY)
							+ "currently has edit access to this record. This record will be displayed in Read-only format.</div>");
		}

		aoStringBuffer
				.append("<form action='${submitAction}' method='post' name='taskHeaderForm' id='taskHeaderForm'>");
		/* Start QC 9499  Multi-Tab Browsing letting Invoice and Advance tasks to be Approved Multiple times causing Duplicate Payments*/
		aoStringBuffer.append(HHSTokenUtil.getHiddenTokenInput());
		/* End QC 9499  Multi-Tab Browsing letting Invoice and Advance tasks to be Approved Multiple times causing Duplicate Payments*/
		aoStringBuffer.append("<input type='hidden' name='askFlag' value='" + loUserMap.get("askFlag")
				+ "'id='askFlag' />");
		aoStringBuffer.append("<input type='hidden' name='reassigntouserText' value='' id='reassigntouserText' />");
		aoStringBuffer.append("<input type='hidden' name='hdnProviderComment' value='' id='hdnProviderComment' />");
		aoStringBuffer.append("<input type='hidden' name='hdnInternalComment' value='' id='hdnInternalComment' />");
		aoStringBuffer.append("<input type='hidden' name='reassignUserName' value='' id='reassignUserName' />");
		aoStringBuffer.append("<input type='hidden' id='hdnWorkFlowId' name='hdnWorkFlowId' value='")
				.append(aoTaskDetailsBean.getWorkFlowId()).append("' />");
		aoStringBuffer.append("<input type='hidden' id='hdnTaskType' name='hdnTaskType' value='")
				.append(aoTaskDetailsBean.getTaskType()).append("'  />");
		aoStringBuffer.append("<input type='hidden' id='hdnTasksubmitParameter' name='hdnTasksubmitParameter' value='")
				.append(aoTaskDetailsBean.getTaskType()).append("' />");
		aoStringBuffer
				.append("<input type='hidden' id='hdnTaskTypeDefaultAssignee' name='hdnTaskTypeDefaultAssignee' value='")
				.append(aoTaskDetailsBean.getTaskName()).append("' />");
		aoStringBuffer.append("<input type='hidden' id='hdnTaskLevel' name='hdnTaskLevel' value='")
				.append(aoTaskDetailsBean.getLevel()).append("' />");
		aoStringBuffer.append("<input type='hidden' id='hdnNewLaunch' name='hdnNewLaunch' value='")
				.append(aoTaskDetailsBean.getIsNewLaunch()).append("' />");
		aoStringBuffer.append("<h2> <label class='floatLft'>Task Details: ");
		if (HHSConstants.TASK_ADVANCE_REVIEW.equalsIgnoreCase(lsTaskName))
		{
			String advanceDesc = fetchAdvanceDescription(aoTaskDetailsBean);
			// below line is modified as per enhancement 5678 and release 2.7.0
			aoStringBuffer.append("Advance Request Review - ").append(advanceDesc);
		}
		
		else
		{
			aoStringBuffer.append(lsTaskName);
		}
		aoStringBuffer.append(" </label><span class=\"linkReturnVault floatRht\"><a href='").append(lsTaskInboxUrl)
				.append("' title=\"\">Return</a>");
		aoStringBuffer.append(" </span></h2>");
		
		// Condition added as a part of enhancement #5688 for Release 3.2.0
		if (null != lsUserOrg && !lsUserOrg.equalsIgnoreCase(HHSConstants.USER_CITY))
		{
			aoStringBuffer
					.append("<div id='helpIcon' class='iconQuestion'><a href='javascript:void(0);' title='Need Help?' onclick='smFinancePageSpecificHelp();'></a></div>");
		}
		aoStringBuffer.append("<input type='hidden' id='screenName' value='");
		aoStringBuffer.append(HHSConstants.HELP_ICON_MAP.get(aoTaskDetailsBean.getTaskName()));
		aoStringBuffer.append("' name='screenName'/>");
		aoStringBuffer.append("<div class='tasktopfilter taskButtons'>");
		aoStringBuffer.append("<div class='taskfilter'>");
		aoStringBuffer.append("<select id='assignUser' ");
		if (!aoTaskDetailsBean.getIsAssignableOperation())
		{
			aoStringBuffer.append(" disabled='disabled'");
		}
		aoStringBuffer.append(" name='assignUser' onchange='enableReassignButton()'>");
		aoStringBuffer.append("<option selected='selected'></option>");
		while (loIterate.hasNext())
		{
			Map.Entry loPairs = (Map.Entry) loIterate.next();
			if (loPairs.getKey() != null && !loPairs.getKey().equals(HHSR5Constants.ASKFLAG))
			{
				aoStringBuffer.append("<option value='").append(loPairs.getKey()).append("'>")
						.append(loPairs.getValue()).append("</option>");
			}
		}
		aoStringBuffer.append(
				"</select> <input type=\"button\" id='ReassignButton' disabled='disabled' class='graybtutton'").append(
				"value='Reassign Task' title=''  onclick='reAssignTask();' />");
		aoStringBuffer.append("</div>");
		aoStringBuffer.append("<div class='taskreassign'>");
		if (loEnableDropdown)
		{
			performEnableDropDownCheck(aoTaskDetailsBean, aoStringBuffer);
		}
		aoStringBuffer.append("<input type=\"button\" class='button'");
		if (loEnableDropdown || !aoTaskDetailsBean.getIsTaskAssigned())
		{
			aoStringBuffer.append(" disabled='disabled'");
		}
		// Added For R6- Finish button of Returned Payment task
		if (aoTaskDetailsBean.getTaskName().equalsIgnoreCase(HHSConstants.TASK_RETURN_PAYMENT_REVIEW))
		{
			aoStringBuffer
					.append(" value='Finish Task' id='finish' name='finish'")
					.append("title=''  onclick='if(finishTaskValidationForReturnedPayment())finishTaskForReturnedPayment();' />");
		}
		// Added For R6- Finish button of Returned Payment task end
		else
		{
			aoStringBuffer.append(" value='Finish Task' id='finish' name='finish'").append(
					"title=''  onclick='if(finishTaskValidation())finishTask();' />");
		}
		aoStringBuffer.append("</div>");
		aoStringBuffer.append("</div>");
		if (taskDetail != null)
		{
			generateTaskDetail(aoTaskDetailsBean, aoStringBuffer, taskType);
		}
		else
		{
			aoStringBuffer.append("</form>");
		}
		return aoStringBuffer;
	}

	/**
	 * New method added in R4
	 * @param aoTaskDetailsBean Task Details Bean
	 * @param aoStringBuffer StringBuffer
	 */
	private void performEnableDropDownCheck(TaskDetailsBean aoTaskDetailsBean, StringBuffer aoStringBuffer)
	{
		aoStringBuffer.append("<select id='finishtaskchild' ");
		if (!aoTaskDetailsBean.getIsTaskAssigned())
		{
			aoStringBuffer.append("disabled='disabled'");
		}
		aoStringBuffer.append(" name='finishtaskchild' onchange='enableFinishButton()' >");
		aoStringBuffer.append("<option value=''></option>");
		for (int liCounter = 0; liCounter < aoTaskDetailsBean.getTaskActions().size(); liCounter++)
		{
			String lsTaskAction = (String) aoTaskDetailsBean.getTaskActions().get(liCounter);
			aoStringBuffer.append("<option value='").append(lsTaskAction).append("'>").append(lsTaskAction)
					.append("</option>");
		}
		aoStringBuffer.append("</select> ");
	}

	/**
	 * This method fetched the advance details for a task
	 * 
	 * <li>The transaction used: budgetAdvanceDesc</li>
	 * 
	 * @param aoTaskDetailsBean Task Details Bean
	 * @return string
	 * @throws ApplicationException
	 */
	private String fetchAdvanceDescription(TaskDetailsBean aoTaskDetailsBean) throws ApplicationException
	{
		String lsAdvanceDescription = HHSConstants.EMPTY_STRING;
		// Retrieve Advance Description for Advance Payment Request Task header
		Channel loChannel = new Channel();
		loChannel.setData(HHSConstants.AO_TASK_DETAILS_BEAN, aoTaskDetailsBean);
		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_ADVNACE_DESC);
		lsAdvanceDescription = (String) loChannel.getData(HHSConstants.CHANNEL_KEY_BUDGET_ADVANCE_DESC);

		return lsAdvanceDescription;
	}

	/**
	 * This method fetches Agency user details on the basis of Agency User Id
	 * and task level Updated in R4
	 * <ul>
	 * <li>Set Agency Id and Task Level in Channel object</li>
	 * <li>Execute 'fetchAgencyDetails' transaction</li>
	 * <li>Create HashMap object for all user list and return it</li>
	 * </ul>
	 * @param aoTaskDetailsBean Task Details Bean
	 * @param asAgencyId AgencyId
	 * @param asTaskLevel TaskLevel
	 * @param asTaskName Task name
	 * @return User HashMap
	 * @throws ApplicationException
	 */
	private HashMap<String, String> fetchAgencyDetails(TaskDetailsBean aoTaskDetailsBean, String asAgencyId,
			String asTaskLevel, String asTaskName) throws ApplicationException
	{
		HashMap<String, String> loUserHashMap = new HashMap<String, String>();
		StaffDetails loUserDetails = null;
		List<StaffDetails> loAgencyUserDetails = null;
		List<String> loUserIdlist = new ArrayList();
		String lsProcessId = String.valueOf(HHSConstants.FINANCIAL_TASK_PROCESS_ID_MAP.get(asTaskName));
		String lsUserRole = aoTaskDetailsBean.getUserRole();
		Channel loChannel = new Channel();
		String lsAskflag = null;
		String lsEntityId = null;
		if (null == asTaskLevel)
		{
			asTaskLevel = HHSConstants.ONE;
		}
		lsEntityId = getAssigneeEntityId(aoTaskDetailsBean);
		loChannel.setData(HHSConstants.AS_AGENCY_ID, asAgencyId);
		loChannel.setData(HHSConstants.AS_TASK_LEVEL, asTaskLevel);
		loChannel.setData(HHSConstants.AS_PROCESS_ID, lsProcessId);
		loChannel.setData(HHSConstants.TASK_TYPE, asTaskName);
		loChannel.setData(HHSConstants.ENTITY_ID, lsEntityId);
		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_AGENCY_DETAILS);
		loAgencyUserDetails = (List<StaffDetails>) loChannel.getData(HHSConstants.AGENCY_USER_LIST);
		lsAskflag = (String) loChannel.getData(HHSR5Constants.ASKFLAG);
		Iterator loItr = loAgencyUserDetails.iterator();
		while (loItr.hasNext())
		{
			loUserDetails = (StaffDetails) loItr.next();
			loUserIdlist.add(loUserDetails.getMsStaffId());
			if (!loUserDetails.getMsStaffId().equalsIgnoreCase(aoTaskDetailsBean.getAssignedTo()))
			{
				loUserHashMap.put(loUserDetails.getMsStaffId(), loUserDetails.getMsStaffFirstName()
						+ HHSConstants.SPACE + loUserDetails.getMsStaffLastName());
			}
		}
		loUserHashMap.put(HHSR5Constants.ASKFLAG, lsAskflag);
		if (lsUserRole.equalsIgnoreCase(HHSConstants.ACCO_ADMIN_STAFF_ROLE)
				|| lsUserRole.equalsIgnoreCase(HHSConstants.ACCO_MANAGER_ROLE)
				|| lsUserRole.equalsIgnoreCase(HHSConstants.PROGRAM_ADMIN_STAFF_ROLE)
				|| lsUserRole.equalsIgnoreCase(HHSConstants.PROGRAM_MANAGER_ROLE)
				|| lsUserRole.equalsIgnoreCase(HHSConstants.FINANCE_ADMIN_STAFF_ROLE)
				|| lsUserRole.equalsIgnoreCase(HHSConstants.FINANCE_MANAGER_ROLE)
				|| lsUserRole.equalsIgnoreCase(HHSConstants.CFO_ROLE))
		{
			// Assign button will be enable for above roles
			aoTaskDetailsBean.setIsAssignableOperation(true);
		}
		else if (aoTaskDetailsBean.getAssignedTo().contains(HHSConstants.WF_INITIAL_REVIEWER)
				&& loUserIdlist.contains(aoTaskDetailsBean.getUserId()))
		{
			// Assign button will be enable for eligible user
			aoTaskDetailsBean.setIsAssignableOperation(true);
		}
		else if (aoTaskDetailsBean.getAssignedTo().equalsIgnoreCase(aoTaskDetailsBean.getUserId()))
		{
			// Assign button will be enable for assigned login user
			aoTaskDetailsBean.setIsAssignableOperation(true);
		}
		else
		{
			// Assign button will be disable
			aoTaskDetailsBean.setIsAssignableOperation(false);
		}
		if ((null != aoTaskDetailsBean.getCurrentTaskStatus() && aoTaskDetailsBean.getCurrentTaskStatus()
				.equalsIgnoreCase(HHSConstants.STATUS_SUSPENDED)))
		{
			// Assign button will be disable
			aoTaskDetailsBean.setIsAssignableOperation(false);
		}
		return loUserHashMap;
	}

	/**
	 * This method is to generate task details section and set dynamic values
	 * received from filenet
	 * @param aoTaskDetailsBean TaskDetailsBean having task page related
	 *            attributes
	 * @return aoTaskDetailsBean TaskDetailsBean populated with data from
	 *         database
	 * @throws ApplicationException
	 * @throws IOException
	 * @throws ServletException
	 */
	private void generateTaskDetail(TaskDetailsBean aoTaskDetailsBean, StringBuffer aoStringBuffer, String aoTaskType)
			throws ApplicationException, ServletException, IOException
	{
		aoStringBuffer.append("<div  class='tabularWrapper portlet1Col'>");
		aoStringBuffer.append("<div class='tabularCustomHead' align='center'>Task Details</div>");
		aoStringBuffer.append("<table cellspacing='0' cellpadding='0'>");
		aoStringBuffer.append("<tr>");
		aoStringBuffer.append("<td><div><b>Procurement/Contract Title:</b></div>");
		aoStringBuffer.append(aoTaskDetailsBean.getProcurementTitle());
		aoStringBuffer.append("</td>");
		aoStringBuffer.append("<td><div><b>Procurement EPIN:</b></div>");
		aoStringBuffer.append(aoTaskDetailsBean.getProcurementEpin());
		aoStringBuffer.append("</td>");
		aoStringBuffer.append("<td><div><b>Provider:</b></div>");
		aoStringBuffer.append(aoTaskDetailsBean.getProvider());
		aoStringBuffer.append("</td>");
		aoStringBuffer.append("<td><div><b>Award EPIN:</b></div>");
		aoStringBuffer.append(aoTaskDetailsBean.getAwardEpin());
		aoStringBuffer.append("</td>");
		aoStringBuffer.append("<td><div><b>CT#:</b></div>");
		aoStringBuffer.append(aoTaskDetailsBean.getCt());
		aoStringBuffer.append("</td>");
		aoStringBuffer.append("  </tr>");
		aoStringBuffer.append(" <tr>");
		aoStringBuffer.append("  <td><div><b>Task Name:</b></div>");
		// below condition is added as per enhancement 5678 and release 2.7.0
		if (HHSConstants.TASK_ADVANCE_REVIEW.equalsIgnoreCase(aoTaskDetailsBean.getTaskName()))
		{
			aoStringBuffer.append(HHSConstants.ADVANCE_REQUEST_REVIEW);
		}
		else
		{
			aoStringBuffer.append(aoTaskDetailsBean.getTaskName());
		}
		aoStringBuffer.append("</td>");
		aoStringBuffer.append("<td><div><b>Submitted By:</b></div>");
		aoStringBuffer.append(aoTaskDetailsBean.getSubmittedByName());
		aoStringBuffer.append("</td>");
		aoStringBuffer.append("<td><div><b>Date Submitted:</b></div>");
		aoStringBuffer.append(aoTaskDetailsBean.getSubmittedDate());
		aoStringBuffer.append("</td>");
		aoStringBuffer.append("<td><div><b>Assigned To:</b></div>");
		aoStringBuffer.append("<div>");
		aoStringBuffer.append(aoTaskDetailsBean.getAssignedToUserName());
		aoStringBuffer.append("</div>");
		if (!aoTaskDetailsBean.getTaskName().equalsIgnoreCase(HHSConstants.TASK_PROCUREMENT_COF))
		{
			aoStringBuffer.append("<div><a href = '#viewDefaultAssignee'>View Default Assignments</a></div>");
		}
		aoStringBuffer
				.append("<div> <input id='defaultAssignmentEntity' name ='defaultAssignmentEntity' type='hidden' value='"
						+ getAssigneeEntityId(aoTaskDetailsBean) + "' /></div>");
		aoStringBuffer.append("<div> <input id='taskAgencyId' name ='taskAgencyId' type='hidden' value='"
				+ aoTaskDetailsBean.getAgencyId() + "' /></div>");
		aoStringBuffer.append("</td>");
		aoStringBuffer.append("<td><div><b>Date Assigned:</b></div>");
		aoStringBuffer.append(aoTaskDetailsBean.getAssignedDate());
		aoStringBuffer.append("</td>");
		aoStringBuffer.append("</tr>");
		aoStringBuffer.append(" </table>");
		aoStringBuffer
				.append(" </div> <div class='overlay'></div><div class='alert-box alert-box-getDefaultAssignee'><a href='javascript:void(0);' class='exit-panel upload-exit' title='Exit'>&nbsp;</a><div id='getUserAssigneeList'></div></div>");
		if (null == aoTaskDetailsBean.getIsNewLaunch() || !aoTaskDetailsBean.getIsNewLaunch())
		{
			if (!aoTaskType.equalsIgnoreCase("taskProcurementCertificationFunds"))
			{
				aoStringBuffer
						.append("<div class=\"infoMessage\" style=\"display:block\">Default users cannot be assigned to this task because it was created prior to system enhancements. Please monitor your Agency’s Unassigned Task List to complete all levels of review.</div>");
			}
		}
		aoStringBuffer.append("</form>");

	}

	/**
	 * this method will fetch the entityId corresponding to the task's Id
	 * @param aoTaskDetailsBean
	 * @return entityId
	 */
	private String getAssigneeEntityId(TaskDetailsBean aoTaskDetailsBean)
	{
		String lsEntityId = "";
		if (HHSConstants.ADVANCE_REQUEST_REVIEW.equalsIgnoreCase(aoTaskDetailsBean.getTaskName())
				|| HHSConstants.TASK_ADVANCE_REVIEW.equalsIgnoreCase(aoTaskDetailsBean.getTaskName())
				|| HHSConstants.TASK_ADVANCE_PAYMENT_REVIEW.equalsIgnoreCase(aoTaskDetailsBean.getTaskName()))
		{
			lsEntityId = aoTaskDetailsBean.getBudgetAdvanceId();
		}
		else if (HHSConstants.TASK_AMENDMENT_CONFIGURATION.equalsIgnoreCase(aoTaskDetailsBean.getTaskName())
				|| HHSConstants.TASK_AMENDMENT_COF.equalsIgnoreCase(aoTaskDetailsBean.getTaskName())
				|| HHSConstants.TASK_CONTRACT_COF.equalsIgnoreCase(aoTaskDetailsBean.getTaskName())
				|| HHSConstants.TASK_CONTRACT_CONFIGURATION.equalsIgnoreCase(aoTaskDetailsBean.getTaskName())
				|| HHSConstants.TASK_CONTRACT_UPDATE.equalsIgnoreCase(aoTaskDetailsBean.getTaskName())
				|| HHSConstants.TASK_NEW_FY_CONFIGURATION.equalsIgnoreCase(aoTaskDetailsBean.getTaskName()))
		{
			lsEntityId = aoTaskDetailsBean.getContractId();
		}
		else if (HHSConstants.TASK_INVOICE_REVIEW.equalsIgnoreCase(aoTaskDetailsBean.getTaskName())
				|| HHSConstants.TASK_PAYMENT_REVIEW.equalsIgnoreCase(aoTaskDetailsBean.getTaskName()))
		{
			lsEntityId = aoTaskDetailsBean.getInvoiceId();
		}
		else if (HHSConstants.TASK_BUDGET_AMENDMENT.equalsIgnoreCase(aoTaskDetailsBean.getTaskName())
				|| HHSConstants.TASK_BUDGET_MODIFICATION.equalsIgnoreCase(aoTaskDetailsBean.getTaskName())
				|| HHSConstants.TASK_BUDGET_UPDATE.equalsIgnoreCase(aoTaskDetailsBean.getTaskName())
				|| HHSConstants.TASK_BUDGET_REVIEW.equalsIgnoreCase(aoTaskDetailsBean.getTaskName()))
		{
			lsEntityId = aoTaskDetailsBean.getBudgetId();
		}
		// Added for R6: setting entity id as return payment id
		else if (HHSConstants.TASK_RETURN_PAYMENT_REVIEW.equalsIgnoreCase(aoTaskDetailsBean.getTaskName()))
		{
			lsEntityId = aoTaskDetailsBean.getReturnPaymentDetailId();
		}
		// Added for R6: setting entity id as return payment id end
		return lsEntityId;
	}

	/**
	 * This method is to generate the Task Footer Section
	 * <ul>
	 * <li>Create comments and History Tab if 'showDocument' Tag is null else
	 * Show Documents Tab also</li>
	 * <li>In Comments tab show only Provider Text Box for Provider screen, for
	 * Agency it shows Internal Comment box</li>
	 * <li>Generate Task Header Footer(HTML tag) in java</li>
	 * </ul>
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * This method is modified as a part of Release 3.1.2 Defect 6420
	 * 
	 * <ul>
	 * <li>Comparing page level invoice id and session invoice id and returning
	 * null in case of discrepancy</li>
	 * </ul>
	 * @param aoRequest to get request parameters and next page to be displayed
	 * @param aoResponse setting response parameter
	 * @param aoTaskDetailsBean TaskDetailsBean having task page related
	 *            attributes
	 * @param aoStringBuffer StringBuffer to construct html elements.
	 * @param aoPageContext PageContext to include jsp.
	 * @return aoStringBuffer StringBuffer
	 * @throws ApplicationException
	 * @throws IOException
	 * @throws ServletException
	 */

	private StringBuffer generateTaskFooter(HttpServletRequest aoRequest, HttpSession aoSession,
			TaskDetailsBean aoTaskDetailsBean, StringBuffer aoStringBuffer, PageContext aoPageContext)
			throws ApplicationException, ServletException, IOException
	{
		String lsOrgType = (String) aoSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE);
		setEntityId(aoTaskDetailsBean);
		if (isTaskScreen == null)
		{
			if (null != entityType)
			{
				aoTaskDetailsBean.setEntityType(entityType);
			}
			if (null != entityTypeForAgency)
			{
				aoTaskDetailsBean.setEntityTypeForAgency(entityTypeForAgency);
			}
			// R4: Tab Level Comments
			aoTaskDetailsBean.setEntityTypeTabLevel(entityTypeTabLevel);
			if (null != entityTypeTabLevel && entityTypeTabLevel.length() > 0)
			{
				aoStringBuffer
						.append("<input type='hidden' id='hdnEntityTypeTabLevel" + entityTypeTabLevel
								+ "' name='hdnEntityTypeTabLevel" + entityTypeTabLevel + "' value='")
						.append(entityTypeTabLevel).append("' />");
			}
		}
		else
		{
			if (null != entityTypeTabLevel && entityTypeTabLevel.length() > 0)
			{
				aoStringBuffer
						.append("<input type='hidden' id='hdnEntityTypeTabLevel" + entityTypeTabLevel
								+ "' name='hdnEntityTypeTabLevel" + entityTypeTabLevel + "' value='")
						.append(entityTypeTabLevel).append("' />");
				if (null != entityType)
				{
					aoTaskDetailsBean.setEntityType(entityType);
				}
				// R4: Tab Level Comments
				aoTaskDetailsBean.setEntityTypeTabLevel(entityTypeTabLevel);
				if (null != entityTypeForAgency)
				{
					aoTaskDetailsBean.setEntityTypeForAgency(entityTypeForAgency);
				}

			}
			else
			{
				aoTaskDetailsBean.setEntityType(aoTaskDetailsBean.getTaskName());
			}
		}
		// R4: Tab Level Comments
		if (null != entityTypeTabLevel && entityTypeTabLevel.length() > 0
				&& null != aoTaskDetailsBean.getEntityTypeTabLevel()
				&& aoTaskDetailsBean.getEntityTypeTabLevel().length() > 0)
		{
			aoTaskDetailsBean.setIsEntityTypeTabLevel(true);
		}
		else
		{
			aoTaskDetailsBean.setIsEntityTypeTabLevel(false);
		}
		// R4: Tab Level Comments Ends
		aoTaskDetailsBean = fetchLastTaskComment(aoTaskDetailsBean);
		/* fetch documents starts */
		if (showDocument != null)
		{
			LOG_OBJECT.Info( "######[Teace]TaskHeaderFooter:  showDocument != null  "  );
			generateDocSection(aoRequest, aoSession, aoPageContext, aoTaskDetailsBean);
		}
        // Start QC9665 R9.3.2 Citywide cyber assessment program (CCAP) - vulnerabilities -- Users Can Access Other User's Uploaded Documents
		else{
			LOG_OBJECT.Info( "######[Teace]TaskHeaderFooter:  showDocument is null  "  );
			pullDocList(aoRequest, aoSession, aoTaskDetailsBean);
		}
		//End QC9665 R 9.3.2 Citywide cyber assessment program (CCAP) - vulnerabilities -- Users Can Access Other User's Uploaded Documents
		
		aoStringBuffer
				.append("<form action='${submitAction}' method='post' name='taskFooterForm' id='taskFooterForm'>");
		// R4: Tab Level Comments
		if (null != entityTypeTabLevel && entityTypeTabLevel.length() > 0)
		{
			aoStringBuffer.append("<div id='commentsTabsTabLevel" + entityTypeTabLevel + "'>");
		}
		else
		{
			aoStringBuffer.append("<div id='commentsTabs'>");
		}
		// R4: Tab Level Comments
		aoStringBuffer.append("<ul>");
		if (null != showDocument)
		{
			aoStringBuffer
					.append("<li><a id='documentWrapperId' href='#documentWrapper' onclick=\"showHideSave('documentWrapperId');\" title=''>Document Upload</a></li>");// For
		} // Tab
		if (null != entityTypeTabLevel && entityTypeTabLevel.length() > 0)
		{
			aoStringBuffer.append("<li><a id='commentWrapperIdTabLevel" + entityTypeTabLevel
					+ "' href='#commentWrapperTabLevel" + entityTypeTabLevel + "' onclick=\"showHideSaveTabLevel('"
					+ entityTypeTabLevel + "','commentWrapperIdTabLevel" + entityTypeTabLevel
					+ "');enableDictionary();\" title=''>Tab Comments</a></li>");
		}
		else
		{
			aoStringBuffer
					.append("<li><a id='commentWrapperId' href='#commentWrapper' onclick=\"showHideSave('commentWrapperId');\" title=''>Comments</a></li>");
		}
		if (isTaskScreen == null)
		{
			// R4: Tab Level Comments
			if (null != entityTypeTabLevel && entityTypeTabLevel.length() > 0)
			{
				aoStringBuffer.append("<li><a id='commentsHistoryWrapperIdTabLevel" + entityTypeTabLevel
						+ "' href='#commentsHistoryWrapperTabLevel" + entityTypeTabLevel
						+ "'  title='' onclick=\"fetchCommentsHistoryTabLevel(\'" + entityTypeTabLevel
						+ "\');showHideSaveTabLevel('" + entityTypeTabLevel + "','');"
						+ "\">View Comments History</a></li><li></li>");
			}
			else
			{
				aoStringBuffer
						.append("<li><a id='commentsHistoryWrapperId' href='#commentsHistoryWrapper'  title='' onclick=\"fetchCommentsHistory();showHideSave('');"
								+ "\">View Comments History</a></li><li></li>");
			}
			// R4: Tab Level Comments Ends
		}
		else
		{
			// R4: Tab Level Comments
			if (null != entityTypeTabLevel && entityTypeTabLevel.length() > 0)
			{
				aoStringBuffer.append("<li><a id='commentsHistoryWrapperIdTabLevel" + entityTypeTabLevel
						+ "' href='#commentsHistoryWrapperTabLevel" + entityTypeTabLevel
						+ "' title='' onclick=\"fetchCommentsHistoryTabLevel(\'" + entityTypeTabLevel
						+ "\');showHideSaveTabLevel('" + entityTypeTabLevel
						+ "','');\">View Comments History</a></li><li></li>");
			}
			else
			{
				aoStringBuffer
						.append("<li><a id='commentsHistoryWrapperId' href='#commentsHistoryWrapper' title='' onclick=\"fetchCommentsHistory();showHideSave('');\">View Task History</a></li><li></li>");
			}
			// R4: Tab Level Comments Ends
		}
		aoStringBuffer.append("<div class='floatRht'>");
		if (null != entityTypeTabLevel && entityTypeTabLevel.length() > 0)
		{
			aoStringBuffer.append("<input id='saveCommentTabLevel" + entityTypeTabLevel + "' name='saveCommentTabLevel"
					+ entityTypeTabLevel + "' type=\"button\" ");
		}
		else
		{
			aoStringBuffer.append("<input id='saveComment' name='saveComment' type=\"button\" ");
		}
		if (isTaskScreen != null && !aoTaskDetailsBean.getIsTaskAssigned())
		{
			aoStringBuffer.append(" disabled='disabled' ");
		}
		// R4: Tab Level Comments
		if (null != entityTypeTabLevel && entityTypeTabLevel.length() > 0)
		{
			aoStringBuffer.append(" class='graybtutton' value='Save' title='' onclick='saveCommentsTabLevel(\""
					+ entityTypeTabLevel + "\")'/>");
		}
		else
		{
			aoStringBuffer.append(" class='graybtutton' value='Save' title='' onclick='saveComments()'/>");
		}
		// R4: Tab Level Comments Ends
		aoStringBuffer.append("</div>");
		aoStringBuffer.append("</ul>");

		// Added in R6 : Returned Payment Task - Fix for View Document
		// Information on first time loading
		// and same for rest of the tasks.
		
		//[Start] R9.5.0 qc 9672 Screen Not Loading when selecting View Document Information in Configuration Tasks
		if (null != showDocument && aoTaskDetailsBean != null
				&&
				  ( HHSConstants.TASK_ADVANCE_PAYMENT_REVIEW.equalsIgnoreCase(aoTaskDetailsBean.getTaskName())
				   	|| HHSConstants.TASK_ADVANCE_REVIEW.equalsIgnoreCase(aoTaskDetailsBean.getTaskName())
					|| HHSConstants.TASK_AMENDMENT_CONFIGURATION.equalsIgnoreCase(aoTaskDetailsBean.getTaskName())
					|| HHSConstants.TASK_AMENDMENT_COF.equalsIgnoreCase(aoTaskDetailsBean.getTaskName())
					|| HHSConstants.TASK_RETURN_PAYMENT_REVIEW.equalsIgnoreCase(aoTaskDetailsBean.getTaskName())
					|| HHSConstants.RETURN_PAYMENT.equalsIgnoreCase(aoTaskDetailsBean.getTaskName())
					|| HHSConstants.TASK_BUDGET_AMENDMENT.equalsIgnoreCase(aoTaskDetailsBean.getTaskName())
					|| HHSConstants.TASK_BUDGET_MODIFICATION.equalsIgnoreCase(aoTaskDetailsBean.getTaskName())
					|| HHSConstants.TASK_BUDGET_UPDATE.equalsIgnoreCase(aoTaskDetailsBean.getTaskName())
					|| HHSConstants.TASK_BUDGET_REVIEW.equalsIgnoreCase(aoTaskDetailsBean.getTaskName())
					|| HHSConstants.TASK_CONTRACT_COF.equalsIgnoreCase(aoTaskDetailsBean.getTaskName())
					|| HHSConstants.TASK_CONTRACT_CONFIGURATION.equalsIgnoreCase(aoTaskDetailsBean.getTaskName())
					|| HHSConstants.TASK_CONTRACT_UPDATE.equalsIgnoreCase(aoTaskDetailsBean.getTaskName())
					|| HHSConstants.TASK_INVOICE_REVIEW.equalsIgnoreCase(aoTaskDetailsBean.getTaskName())
					|| HHSConstants.TASK_NEW_FY_CONFIGURATION.equalsIgnoreCase(aoTaskDetailsBean.getTaskName())
					|| HHSConstants.TASK_PAYMENT_REVIEW.equalsIgnoreCase(aoTaskDetailsBean.getTaskName())
					|| HHSConstants.TASK_PROCUREMENT_COF.equalsIgnoreCase(aoTaskDetailsBean.getTaskName())
					|| HHSConstants.ADVANCE_REQUEST_REVIEW.equalsIgnoreCase(aoTaskDetailsBean.getTaskName())				
				  )
				
			)
		//[End] R9.5.0 qc 9672 Screen Not Loading when selecting View Document Information in Configuration Tasks
		{
			aoStringBuffer.append("<div id='documentWrapper'>");
			aoStringBuffer.append(STR_JS_2);
			aoStringBuffer.append("</div>");
		}
		else if (null != showDocument)
		{ // Fix for defect 8619
			aoStringBuffer.append("<div id='documentWrapper'>");
			aoStringBuffer.append(STR_JS_1);
			aoStringBuffer.append("</div>");
		}

		if (null != entityTypeTabLevel && entityTypeTabLevel.length() > 0)
		{
			aoStringBuffer.append("<div id='commentWrapperTabLevel" + entityTypeTabLevel
					+ "' style='border: 1px solid #DDDDDD !important;'>");
		}
		else
		{
			aoStringBuffer.append("<div id='commentWrapper'>");
		}
		if (lsOrgType.equalsIgnoreCase(ApplicationConstants.PROVIDER_ORG) || null == isTaskScreen)
		{
			aoStringBuffer.append("<div class='taskCommentsFull'>");
			aoStringBuffer.append("<b>Enter any comments:</b>");
			aoStringBuffer.append("<div>Click the 'Save' button above to save your comments.</div>");
			// R4: Tab Level Comments
			if (null != entityTypeTabLevel && entityTypeTabLevel.length() > 0)
			{
				// fix done as a part of release 3.1.2 defect 6420 - start.
				aoStringBuffer.append("<div id='invoiceErrorMsg" + entityTypeTabLevel
						+ "'style='color: red; font-weight: bold; display: block;'></div>");
				// fix done as a part of release 3.1.2 defect 6420 - end.
				// Updating in R6
				aoStringBuffer.append("<textarea name='publicCommentAreaTabLevel" + entityTypeTabLevel
						+ "' id='publicCommentAreaTabLevel" + entityTypeTabLevel
						+ "' class='taskFullCommentsTxtarea' onkeyup='setMaxLength(this,"
						+ getTextAreaSize(textAreaSize, "1000") + ")' onkeypress='setMaxLength(this,"
						+ getTextAreaSize(textAreaSize, "1000") + ")'");
				aoStringBuffer.append(" onchange=\"setChangeFlagTabLevel('");
				aoStringBuffer.append(getSubBudgetIdFromTabLevelIdentifier(entityTypeTabLevel));
				aoStringBuffer.append("')\" cols='48' rows='5' >");
			}
			else
			{
				// fix done as a part of release 3.1.2 defect 6420 - start.
				aoStringBuffer
						.append("<div id='invoiceErrorMsg'style='color: red; font-weight: bold; display: block;'></div>");
				// fix done as a part of release 3.1.2 defect 6420 - end.
				// Updating in R6
				aoStringBuffer
						.append("<textarea name='publicCommentArea' id='publicCommentArea' class='taskFullCommentsTxtarea' onkeyup='setMaxLength(this,"
								+ getTextAreaSize(textAreaSize, "1000")
								+ ")' onkeypress='setMaxLength(this,"
								+ getTextAreaSize(textAreaSize, "1000") + ")'");
				aoStringBuffer.append(" onchange='setChangeFlag()' cols='48' rows='5' >");
			}
			// R4: Tab Level Comments Ends
			aoStringBuffer.append(aoTaskDetailsBean.getProviderComment());
			aoStringBuffer.append("</textarea></div>");
		}
		else
		{
			commentsSectionNonProvider(aoTaskDetailsBean, aoStringBuffer);

		}
		aoStringBuffer.append("</div>");
		// R4: Tab Level Comments
		if (null != entityTypeTabLevel && entityTypeTabLevel.length() > 0)
		{
			aoStringBuffer.append("<div id='commentsHistoryWrapperTabLevel" + entityTypeTabLevel
					+ "'  style='border: 1px solid #DDDDDD !important;'>");
		}
		else
		{
			aoStringBuffer.append("<div id='commentsHistoryWrapper'>");
		}
		aoStringBuffer.append("</div>");
		aoStringBuffer.append("</div>");
		aoStringBuffer.append("</form>");
		pageContext.include(HHSConstants.URL_TASK);
		return aoStringBuffer;
	}

	/**
	 * This method create comments size for textarea
	 * <ul>
	 * <li>Added in R6: Added size for Tab Level Comments</li>
	 * <ul>
	 * @param aoTextAreaSize
	 * @param aoExistingSize
	 * @param loTextAreaSize
	 */
	private String getTextAreaSize(String aoTextAreaSize, String aoExistingSize)
	{
		String loTextAreaSize = HHSConstants.EMPTY_STRING;
		if (StringUtils.isBlank(aoTextAreaSize))
		{
			loTextAreaSize = aoExistingSize;
		}
		else
		{
			loTextAreaSize = aoTextAreaSize;
		}
		return loTextAreaSize;
	}

	/**
	 * This method create comments section for Non provider screens Updated for
	 * <ul>
	 * <li>Added in R4: Added checks for Tab Level Comments</li>
	 * <ul>
	 * @param aoTaskDetailsBean Taskdetail bean
	 * @param aoStringBuffer Buffer object to create HTML tag
	 */
	private void commentsSectionNonProvider(TaskDetailsBean aoTaskDetailsBean, StringBuffer aoStringBuffer)
	{
		if (commentsSection != null && !HHSConstants.EMPTY_STRING.equalsIgnoreCase(commentsSection))
		{
			aoStringBuffer.append("<div class='taskComments'>");
			// fix done as a part of release 3.1.2 defect 6420 - start.
			aoStringBuffer
					.append("<div id='invoiceErrorMsg' style='color: red; font-weight: bold; display: block;'></div>");
			// fix done as a part of release 3.1.2 defect 6420 - end.
			aoStringBuffer.append("<b>Enter any public provider comments:</b>");
			aoStringBuffer.append("<div>Click the 'Save' button above to save your comments.</div>");
			// R4: Added Check for entityTypeTabLevel
			if (HHSConstants.ONE.equalsIgnoreCase(aoTaskDetailsBean.getLevel()) || null != entityTypeTabLevel)
			{
				showOrHideTab(aoTaskDetailsBean, aoStringBuffer);
			}
			else
			{
				getTabLevelComments(aoStringBuffer);
			}
			aoStringBuffer.append(aoTaskDetailsBean.getProviderComment());
			aoStringBuffer.append("</textarea></div>");
		}
		aoStringBuffer.append("<div ");
		if (commentsSection != null && !HHSConstants.EMPTY_STRING.equalsIgnoreCase(commentsSection))
		{
			aoStringBuffer.append("class='taskComments commentNoborder'");
		}
		else
		{
			aoStringBuffer.append("class='taskCommentsFull commentNoborder'");
		}
		aoStringBuffer.append("><b>Enter any internal Agency comments:</b>");
		aoStringBuffer.append("<div>Click the 'Save' button above to save your comments.</div>");
		aoStringBuffer.append("<textarea ");
		if (isTaskScreen != null && !aoTaskDetailsBean.getIsTaskAssigned())
		{
			aoStringBuffer.append(" disabled=\"true\" ");
		}
		// R4: Tab Level Comments
		if (null != entityTypeTabLevel && entityTypeTabLevel.length() > 0)
		{
			aoStringBuffer.append(" name='internalCommentAreaTabLevel" + entityTypeTabLevel
					+ "' id='internalCommentAreaTabLevel" + entityTypeTabLevel + "' ");
		}
		else
		{
			aoStringBuffer.append(" name='internalCommentArea' id='internalCommentArea' ");
		}
		// R4: Tab Level Comments Ends
		if (commentsSection != null && !HHSConstants.EMPTY_STRING.equalsIgnoreCase(commentsSection))
		{
			// R4: Tab Level Comments
			if (null != entityTypeTabLevel && entityTypeTabLevel.length() > 0)
			{
				aoStringBuffer.append("class='taskCommentsTxtareaTabLevel' ");
				aoStringBuffer.append(" onchange=\"setChangeFlagTabLevel('");
				aoStringBuffer.append(getSubBudgetIdFromTabLevelIdentifier(entityTypeTabLevel));
				aoStringBuffer.append("')\" cols='48' rows='5' ");
			}
			else
			{
				aoStringBuffer.append("class='taskCommentsTxtarea' ");
				aoStringBuffer.append("onchange='setChangeFlag()' cols='48' rows='5' ");
			}
			// R4: Tab Level Comments Ends
		}
		else
		{
			aoStringBuffer.append("class='taskFullCommentsTxtarea' ");
		}
		aoStringBuffer.append("onkeyup='setMaxLength(this,1000)' onkeypress='setMaxLength(this,1000)' >");
		aoStringBuffer.append(aoTaskDetailsBean.getInternalComment());
		aoStringBuffer.append("</textarea></div>");
	}

	/**
	 * <ul>
	 * <li>New method added in R4</li>
	 * </ul>
	 * This method is used to get the Tab level comments.
	 * @param aoStringBuffer
	 */
	private void getTabLevelComments(StringBuffer aoStringBuffer)
	{
		// R4: Tab Level Comments
		if (null != entityTypeTabLevel && entityTypeTabLevel.length() > 0)
		{
			aoStringBuffer
					.append("<textarea name='publicCommentAreaTabLevel"
							+ entityTypeTabLevel
							+ "' disabled=\"true\" id='publicCommentAreaTabLevel"
							+ entityTypeTabLevel
							+ "' class='taskCommentsTxtareaTabLevel' onkeyup='setMaxLength(this,1000)' onkeypress='setMaxLength(this,1000)'");
			aoStringBuffer.append(" onchange=\"setChangeFlagTabLevel('");
			aoStringBuffer.append(getSubBudgetIdFromTabLevelIdentifier(entityTypeTabLevel));
			aoStringBuffer.append("')\" cols='48' rows='5' >");
		}
		else
		{
			aoStringBuffer
					.append("<textarea name='publicCommentArea' disabled=\"true\" id='publicCommentArea' class='taskCommentsTxtarea' onkeyup='setMaxLength(this,1000)' onkeypress='setMaxLength(this,1000)'");
			aoStringBuffer.append(" onchange='setChangeFlag()' cols='48' rows='5' >");
		}
		// R4: Tab Level Comments Ends
	}

	/**
	 * This method is used to show or hide Tab.
	 * <ul>
	 * <li>New Method in R4</li>
	 * </ul>
	 * @param aoTaskDetailsBean Task Details Bean
	 * @param aoStringBuffer StringBuffer
	 */
	private void showOrHideTab(TaskDetailsBean aoTaskDetailsBean, StringBuffer aoStringBuffer)
	{
		aoStringBuffer.append("<textarea ");
		if (isTaskScreen != null && !aoTaskDetailsBean.getIsTaskAssigned())
		{
			aoStringBuffer.append(" disabled=\"true\" ");
		}
		aoStringBuffer.append("<textarea ");
		if (isTaskScreen != null && !aoTaskDetailsBean.getIsTaskAssigned())
		{
			aoStringBuffer.append(" disabled=\"true\" ");
		}
		if (null != entityTypeTabLevel && entityTypeTabLevel.length() > 0)
		{
			aoStringBuffer
					.append(" name='publicCommentAreaTabLevel"
							+ entityTypeTabLevel
							+ "' id='publicCommentAreaTabLevel"
							+ entityTypeTabLevel
							+ "' class='taskCommentsTxtareaTabLevel' onkeyup='setMaxLength(this,1000)' onkeypress='setMaxLength(this,1000)'");
			aoStringBuffer.append(" onchange=\"setChangeFlagTabLevel('");
			aoStringBuffer.append(getSubBudgetIdFromTabLevelIdentifier(entityTypeTabLevel));
			aoStringBuffer.append("')\" cols='48' rows='5' >");
		}
		else
		{
			aoStringBuffer
					.append(" name='publicCommentArea' id='publicCommentArea' class='taskCommentsTxtarea' onkeyup='setMaxLength(this,1000)' onkeypress='setMaxLength(this,1000)'");
			aoStringBuffer.append(" onchange='setChangeFlag()' cols='48' rows='5' >");
		}
	}

	/**
	 * This method generate the grid for the document section <li>The
	 * transaction used: getFinancialDocuments_db</li>
	 * 
	 * @param aoRequest to get request parameters and next page to be displayed
	 * @param aoSession to get the session objects
	 * @param aoPageContext PageContext to include jsp.
	 * @param aoTaskDetailsBean TaskDetailsBean .
	 * @throws ApplicationException
	 * @throws ServletException
	 * @throws IOException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private void generateDocSection(HttpServletRequest aoRequest, HttpSession aoSession, PageContext aoPageContext,
			TaskDetailsBean aoTaskDetailsBean) throws ApplicationException, ServletException, IOException
	{

		Channel loChannelObj = new Channel();
		boolean lbIsUploadAllowed = true;
		List<ExtendedDocument> loFinancialDocumentList = null;
		String lsContractId = HHSConstants.EMPTY_STRING;
		try
		{
			HashMap<String, String> loRequiredParamMap = new HashMap<String, String>();
			setRequiredParam(loRequiredParamMap);
			P8UserSession loUserSession = (P8UserSession) aoSession
					.getAttribute(ApplicationConstants.FILENET_SESSION_OBJECT);
			loChannelObj.setData(HHSConstants.HM_REQIRED_PROPERTY_MAP, loRequiredParamMap);
			loChannelObj.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
			String lsUserOrgType = (String) aoSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE);
			Map loMap = new HashMap();
			if (null != aoTaskDetailsBean && null != aoTaskDetailsBean.getContractId())
			{
				lsContractId = aoTaskDetailsBean.getContractId();
			}
			if (aoTaskDetailsBean != null
					&& (HHSConstants.TASK_AMENDMENT_CONFIGURATION.equalsIgnoreCase(aoTaskDetailsBean.getTaskName()) || HHSConstants.TASK_CONTRACT_UPDATE
							.equalsIgnoreCase(aoTaskDetailsBean.getTaskName())))
			{
				lsContractId = (String) aoSession.getAttribute(HHSConstants.TASK_CONTRACT_ID);
			}
			// Added for R6- Returned Payment Task
			if (aoTaskDetailsBean != null
					&& (HHSConstants.TASK_RETURN_PAYMENT_REVIEW.equalsIgnoreCase(aoTaskDetailsBean.getTaskName())))
			{
				String lsReturnedPaymentId = aoTaskDetailsBean.getReturnPaymentDetailId();
				loMap.put(HHSConstants.RETURN_PAYMENT_DETAIL_ID, lsReturnedPaymentId);
			}
			// Added for R6- Returned Payment Task
			loMap.put(HHSConstants.ORGANIZATION_TYPE, lsUserOrgType);
			loMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, lsContractId);
			if (null != aoTaskDetailsBean)
			{
				if (null != aoTaskDetailsBean.getBudgetId())
				{
					loMap.put(HHSConstants.BUDGET_ID_WORKFLOW, aoTaskDetailsBean.getBudgetId());
				}
				if (null != aoTaskDetailsBean.getInvoiceId())
				{
					loMap.put(HHSConstants.INVOICE_ID, aoTaskDetailsBean.getInvoiceId());
				}
			}
			loChannelObj.setData(HHSConstants.AO_PARAMETER_MAP, loMap);
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.GET_FINANCIAL_DOCUMENTS_DB);
			loFinancialDocumentList = (List<ExtendedDocument>) loChannelObj
					.getData(HHSConstants.AO_FINANCIAL_DOCUMENT_LIST);
			
            // Start QC9665 R 9.3.2 Citywide cyber assessment program (CCAP) - vulnerabilities -- Users Can Access Other User's Uploaded Documents
			aoSession.setAttribute(ApplicationConstants.SESSION_EXTENDED_DOCUMENT_LIST, loFinancialDocumentList);
			LOG_OBJECT.Info("save Document List in Session on Application scope in generateDocSection TaskHeaderFooter");
			//End QC9665 R 9.3.2 Citywide cyber assessment program (CCAP) - vulnerabilities -- Users Can Access Other User's Uploaded Documents

			// Added for R6- Returned Payment to hide Remove Document for Level
			// more than 1
			if (null != loFinancialDocumentList && !loFinancialDocumentList.isEmpty())
			{
				for (ExtendedDocument extendedDocument : loFinancialDocumentList)
				{
					if (null != aoTaskDetailsBean)
					{
						extendedDocument.setCurrentReviewLevel(aoTaskDetailsBean.getLevel());
						extendedDocument.setTaskName(aoTaskDetailsBean.getTaskName());
					}
				}
			}
			// Ended for R6- Returned Payment to hide Remove Document for Level
			// more than 1
			if (loFinancialDocumentList != null && !loFinancialDocumentList.isEmpty())
			{
				Collections.sort(loFinancialDocumentList, new Comparator<ExtendedDocument>()
				{
					@Override
					public int compare(ExtendedDocument c1, ExtendedDocument c2)
					{
						Date aoCreatedDate1 = HHSUtil.ConvertStringToDate(c1.getCreatedDate());
						Date aoCreatedDate2 = HHSUtil.ConvertStringToDate(c2.getCreatedDate());
						int liResult = aoCreatedDate1.compareTo(aoCreatedDate2);
						if (liResult != 0 && liResult > 0)
						{
							liResult = -1;
						}
						else if (liResult != 0 && liResult < 0)
						{
							liResult = 1;
						}
						if (liResult == 0)
						{
							liResult = c1.getDocumentTitle().toLowerCase()
									.compareTo(c2.getDocumentTitle().toLowerCase());
						}
						return liResult;
					}
				});
			}
			// Added for R6- check for readOnly criteria of addfromVault/Upload
			// doc in Returned Payment task
			if (HHSConstants.TASK_RETURN_PAYMENT_REVIEW.equalsIgnoreCase(aoTaskDetailsBean.getTaskName())
					&& !HHSR5Constants.ONE.equalsIgnoreCase(aoTaskDetailsBean.getLevel()))
			{
				lbIsUploadAllowed = HHSR5Constants.BOOLEAN_FALSE;
				aoRequest.setAttribute(HHSConstants.RETURN_PAYMENT_DETAIL_ID,
						aoTaskDetailsBean.getReturnPaymentDetailId());
			}
			// Added for R6- check for readOnly criteria of addfromVault/Upload
			// doc in Returned Payment task end
			aoRequest.setAttribute(ApplicationConstants.SESSION_DOCUMENT_LIST, loFinancialDocumentList);
			aoRequest.setAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE, lsUserOrgType);
			aoRequest.setAttribute(HHSConstants.SHOW_UPLOAD_DOC, lbIsUploadAllowed);
			aoRequest.setAttribute(HHSConstants.PROCUREMENT_ID, HHSConstants.EMPTY_STRING);
			if (null != aoSession.getAttribute(ApplicationConstants.ERROR_MESSAGE))
			{
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
						(String) aoSession.getAttribute(ApplicationConstants.ERROR_MESSAGE));
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
						(String) aoSession.getAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE));
			}
		}
		catch (ApplicationException aoExp)
		{
			String lsErrorMsg = aoExp.toString();
			lsErrorMsg = lsErrorMsg.substring(lsErrorMsg.lastIndexOf(HHSConstants.COLON) + 1, lsErrorMsg.length())
					.trim();
			if (lsErrorMsg.isEmpty())
			{
				lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			}
			LOG_OBJECT.Error("Application Exception in Document Vault", aoExp);
			aoSession.setAttribute(ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
					ApplicationConstants.DOCUMENT_EXCEPTION);
			aoSession.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoSession.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
			aoSession.setAttribute(ApplicationConstants.ERROR_MAP, aoExp.getContextData());

		}
		/* fetch documents ends */
		aoPageContext.include(HHSConstants.URL_TASKS_DOCUMENT);
	}
	
/*[Start] QC9665 R 9.3.2 Citywide cyber assessment program (CCAP) - vulnerabilities */	
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private void pullDocList(HttpServletRequest aoRequest, HttpSession aoSession, 
			TaskDetailsBean aoTaskDetailsBean) throws ApplicationException, ServletException, IOException
	{
		Channel loChannelObj = new Channel();
		boolean lbIsUploadAllowed = true;
		List<ExtendedDocument> loFinancialDocumentList = null;
		String lsContractId = HHSConstants.EMPTY_STRING;
		try
		{
			HashMap<String, String> loRequiredParamMap = new HashMap<String, String>();
			setRequiredParam(loRequiredParamMap);
			P8UserSession loUserSession = (P8UserSession) aoSession
					.getAttribute(ApplicationConstants.FILENET_SESSION_OBJECT);
			loChannelObj.setData(HHSConstants.HM_REQIRED_PROPERTY_MAP, loRequiredParamMap);
			loChannelObj.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
			String lsUserOrgType = (String) aoSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE);
			Map loMap = new HashMap();
			if (null != aoTaskDetailsBean && null != aoTaskDetailsBean.getContractId())
			{
				lsContractId = aoTaskDetailsBean.getContractId();
			}
			if (aoTaskDetailsBean != null
					&& (HHSConstants.TASK_AMENDMENT_CONFIGURATION.equalsIgnoreCase(aoTaskDetailsBean.getTaskName()) || HHSConstants.TASK_CONTRACT_UPDATE
							.equalsIgnoreCase(aoTaskDetailsBean.getTaskName())))
			{
				lsContractId = (String) aoSession.getAttribute(HHSConstants.TASK_CONTRACT_ID);
			}
			// Added for R6- Returned Payment Task
			if (aoTaskDetailsBean != null
					&& (HHSConstants.TASK_RETURN_PAYMENT_REVIEW.equalsIgnoreCase(aoTaskDetailsBean.getTaskName())))
			{
				String lsReturnedPaymentId = aoTaskDetailsBean.getReturnPaymentDetailId();
				loMap.put(HHSConstants.RETURN_PAYMENT_DETAIL_ID, lsReturnedPaymentId);
			}
			// Added for R6- Returned Payment Task
			loMap.put(HHSConstants.ORGANIZATION_TYPE, lsUserOrgType);
			loMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, lsContractId);
			if (null != aoTaskDetailsBean)
			{
				if (null != aoTaskDetailsBean.getBudgetId())
				{
					loMap.put(HHSConstants.BUDGET_ID_WORKFLOW, aoTaskDetailsBean.getBudgetId());
				}
				if (null != aoTaskDetailsBean.getInvoiceId())
				{
					loMap.put(HHSConstants.INVOICE_ID, aoTaskDetailsBean.getInvoiceId());
				}
			}
			loChannelObj.setData(HHSConstants.AO_PARAMETER_MAP, loMap);
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.GET_FINANCIAL_DOCUMENTS_DB);
			loFinancialDocumentList = (List<ExtendedDocument>) loChannelObj
					.getData(HHSConstants.AO_FINANCIAL_DOCUMENT_LIST);
            // Start QC9665 R 9.3.2 Citywide cyber assessment program (CCAP) - vulnerabilities -- Users Can Access Other User's Uploaded Documents
			aoSession.setAttribute(ApplicationConstants.SESSION_EXTENDED_DOCUMENT_LIST, loFinancialDocumentList);
			LOG_OBJECT.Info("save Document List in Session on Application scope in generateDocSection TaskHeaderFooter");
			//End QC9665 R 9.3.2 Citywide cyber assessment program (CCAP) - vulnerabilities -- Users Can Access Other User's Uploaded Documents
		}
		catch (ApplicationException aoExp)
		{
			String lsErrorMsg = aoExp.toString();
			lsErrorMsg = lsErrorMsg.substring(lsErrorMsg.lastIndexOf(HHSConstants.COLON) + 1, lsErrorMsg.length())
					.trim();
			if (lsErrorMsg.isEmpty())
			{
				lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			}
			LOG_OBJECT.Error("Application Exception in Document Vault", aoExp);
			aoSession.setAttribute(ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
					ApplicationConstants.DOCUMENT_EXCEPTION);
			aoSession.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoSession.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
			aoSession.setAttribute(ApplicationConstants.ERROR_MAP, aoExp.getContextData());

		}
		/* fetch documents ends */
	}
	/*[End] QC9665 R 9.3.2 Citywide cyber assessment program (CCAP) - vulnerabilities */	

	
	/**
	 * This method is updated for Release 3.8.0 #6483. This method is updated
	 * for Release 3.14.0 #6602. Gets task header section details from database
	 * <li>This method set finish task status to show in dropdown</li> <li>
	 * Execute 'fetchTaskDetails' transaction to get task details like task name
	 * ,submitted by,submitted date etc from filenet</li>
	 * @param aoTaskDetailsBean TaskDetailsBean having task page related
	 *            attributes
	 * @return aoTaskDetailsBean TaskDetailsBean populated with data from
	 *         database
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private TaskDetailsBean fetchTaskDetails(TaskDetailsBean aoTaskDetailsBean) throws ApplicationException
	{
		List loTaskActions = new ArrayList();
		// If the task is contract configuration update or Configure New FY,
		// Cancel option is added
		// to the Drop Down.
		// Added for R6: Return payment review task level 1
		if (aoTaskDetailsBean.getTaskName().equalsIgnoreCase(HHSConstants.TASK_CONTRACT_UPDATE)
				|| aoTaskDetailsBean.getTaskName().equalsIgnoreCase(HHSConstants.TASK_NEW_FY_CONFIGURATION)
				|| (aoTaskDetailsBean.getLevel().equalsIgnoreCase(HHSR5Constants.ONE) && aoTaskDetailsBean
						.getTaskName().equalsIgnoreCase(HHSConstants.TASK_RETURN_PAYMENT_REVIEW)))
		{
			loTaskActions.add(ApplicationConstants.STATUS_APPROVED);
			loTaskActions.add(HHSConstants.CANCEL);
		}
		// Added for R6: Return payment review task level 1 end
		else
		{
			loTaskActions.add(ApplicationConstants.STATUS_APPROVED);
			loTaskActions.add(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS);
		}
		aoTaskDetailsBean.setTaskActions(loTaskActions);
		return aoTaskDetailsBean;
	}

	/**
	 * This method is to fetch Last Agency Comment from database by calling
	 * 'fetchLastTaskComment' transaction
	 * 
	 * @param aoTaskDetailsBean TaskDetailsBean having task page related
	 *            attributes
	 * @return aoTaskDetailsBean TaskDetailsBean populated with data from
	 *         database
	 * @throws ApplicationException
	 */
	private TaskDetailsBean fetchLastTaskComment(TaskDetailsBean aoTaskDetailsBean) throws ApplicationException
	{
		Channel loChannel = new Channel();
		loChannel.setData(ApplicationConstants.TASK_DETAILS_BEAN_KEY, aoTaskDetailsBean);

		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_LAST_TASK_COMMENT);
		aoTaskDetailsBean = (TaskDetailsBean) loChannel.getData(HHSConstants.AS_TASK_COMMENT);
		return aoTaskDetailsBean;
	}

	/**
	 * @param aoTaskDetailsBean
	 * @throws ApplicationException
	 */
	private void setEntityId(TaskDetailsBean aoTaskDetailsBean) throws ApplicationException
	{
		String lsEntityId = HHSConstants.EMPTY_STRING;
		if (null != aoTaskDetailsBean.getBudgetAdvanceId() && !(aoTaskDetailsBean.getBudgetAdvanceId()).isEmpty())
		{
			lsEntityId = aoTaskDetailsBean.getBudgetAdvanceId();
		}
		else if (null != aoTaskDetailsBean.getInvoiceId() && !(aoTaskDetailsBean.getInvoiceId()).isEmpty())
		{
			lsEntityId = aoTaskDetailsBean.getInvoiceId();
		}
		// Added for R6: Return payment review task
		else if (null != aoTaskDetailsBean && null != aoTaskDetailsBean.getBudgetId()
				&& !(aoTaskDetailsBean.getBudgetId()).isEmpty())
		{
			// Added for defect 8618
			if (null != aoTaskDetailsBean.getReturnPaymentDetailId()
					&& !(aoTaskDetailsBean.getReturnPaymentDetailId()).isEmpty())
			{
				lsEntityId = aoTaskDetailsBean.getReturnPaymentDetailId();
			}
			else
			{
				lsEntityId = aoTaskDetailsBean.getBudgetId();
			}
		}
		// Added for R6: Return payment review task end
		else if (null != aoTaskDetailsBean.getContractId() && !(aoTaskDetailsBean.getContractId()).isEmpty())
		{
			lsEntityId = aoTaskDetailsBean.getContractId();
		}
		else if (null != aoTaskDetailsBean.getProcurementId() && !(aoTaskDetailsBean.getProcurementId()).isEmpty())
		{
			lsEntityId = aoTaskDetailsBean.getProcurementId();
		}
		aoTaskDetailsBean.setEntityId(lsEntityId);

	}

	/**
	 * Below method will set the required parameter into the map
	 * @param aoParamMap HashMap
	 */
	private void setRequiredParam(HashMap<String, String> aoParamMap)
	{
		aoParamMap.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, ApplicationConstants.EMPTY_STRING);
	}

	/**
	 * This method is updated for Release 3.8.0 #6483 This method is updated for
	 * Release 3.12.0 #6602 This method check if we need to populate status
	 * dropdown or not
	 * @param aoTaskDetailsBean TaskDetail bean object
	 * @return boolean
	 */
	private boolean checkEnableDisableDropdown(TaskDetailsBean aoTaskDetailsBean)
	{
		String lsTaskLevel = aoTaskDetailsBean.getLevel();
		String lsTaskName = aoTaskDetailsBean.getTaskName();
		// Added condition for R6: Return payment review task
		if (lsTaskLevel.equalsIgnoreCase(HHSConstants.ONE)
				&& !(lsTaskName.equalsIgnoreCase(HHSConstants.TASK_BUDGET_REVIEW)
						|| lsTaskName.equalsIgnoreCase(HHSConstants.TASK_CONTRACT_UPDATE)
						|| lsTaskName.equalsIgnoreCase(HHSConstants.TASK_INVOICE_REVIEW)
						|| lsTaskName.equalsIgnoreCase(HHSConstants.TASK_BUDGET_UPDATE)
						|| lsTaskName.equalsIgnoreCase(HHSConstants.TASK_BUDGET_MODIFICATION)
						|| lsTaskName.equalsIgnoreCase(HHSConstants.TASK_BUDGET_AMENDMENT)
						|| lsTaskName.equalsIgnoreCase(HHSConstants.TASK_PAYMENT_REVIEW)
						|| lsTaskName.equalsIgnoreCase(HHSConstants.TASK_ADVANCE_REVIEW)
						|| lsTaskName.equalsIgnoreCase(HHSConstants.TASK_ADVANCE_PAYMENT_REVIEW)
						|| lsTaskName.equalsIgnoreCase(HHSConstants.TASK_NEW_FY_CONFIGURATION) || lsTaskName
							.equalsIgnoreCase(HHSConstants.TASK_RETURN_PAYMENT_REVIEW)))
		// Added condition for R6: Return payment review task
		{
			return false;
		}
		else if (lsTaskName.equalsIgnoreCase(HHSConstants.TASK_CONTRACT_CONFIGURATION)
				|| (lsTaskName.equalsIgnoreCase(HHSConstants.TASK_CONTRACT_UPDATE) && aoTaskDetailsBean
						.getDiscFlagForUpdate())
				|| (lsTaskName.equalsIgnoreCase(HHSConstants.TASK_NEW_FY_CONFIGURATION) && aoTaskDetailsBean
						.getIsAlreadyLaunchedFYTask()))
		{
			return false;
		}

		return true;
	}

	/**
	 * Added as part of Release 3.12.0 enhancement 6602 This method checks
	 * whether the New FY task is already launched task and sets
	 * IsAlreadyLaunchedFYTask property in TaskDetailsBean
	 * @param aoTaskDetailsBean
	 * @throws ApplicationException
	 */
	private void isAlreadyLaunchedFYTask(TaskDetailsBean aoTaskDetailsBean) throws ApplicationException
	{
		Channel loChannel = new Channel();
		loChannel.setData(HHSConstants.CONTRACT_ID_KEY, aoTaskDetailsBean.getContractId());
		HHSTransactionManager.executeTransaction(loChannel, "isAlreadyLaunchedFYTask");
		Boolean loIsAlreadyLaunchedFYTask = (Boolean) loChannel.getData("aoIsAlreadyLaunchedFYTask");
		aoTaskDetailsBean.setIsAlreadyLaunchedFYTask(loIsAlreadyLaunchedFYTask);
	}

	/**
	 * @return the level
	 */
	public String getLevel()
	{
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(String level)
	{
		this.level = level;
	}

	/**
	 * @return the workFlowId
	 */
	public String getWorkFlowId()
	{
		return workFlowId;
	}

	/**
	 * @param workFlowId the workFlowId to set
	 */
	public void setWorkFlowId(String workFlowId)
	{
		this.workFlowId = workFlowId;
	}

	/**
	 * @return the taskType
	 */
	public String getTaskType()
	{
		return taskType;
	}

	/**
	 * @param taskType the taskType to set
	 */
	public void setTaskType(String taskType)
	{
		this.taskType = taskType;
	}

	/**
	 * @return the taskDetail
	 */
	public String getTaskDetail()
	{
		return taskDetail;
	}

	/**
	 * @param taskDetail the taskDetail to set
	 */
	public void setTaskDetail(String taskDetail)
	{
		this.taskDetail = taskDetail;
	}

	/**
	 * @return the commentsSection
	 */
	public final String getCommentsSection()
	{
		return commentsSection;
	}

	/**
	 * @param commentsSection the commentsSection to set
	 */
	public final void setCommentsSection(String commentsSection)
	{
		this.commentsSection = commentsSection;
	}

	/**
	 * @return the internalLastCommentQuery
	 */
	public String getInternalLastCommentQuery()
	{
		return internalLastCommentQuery;
	}

	/**
	 * @param internalLastCommentQuery the internalLastCommentQuery to set
	 */
	public void setInternalLastCommentQuery(String internalLastCommentQuery)
	{
		this.internalLastCommentQuery = internalLastCommentQuery;
	}

	/**
	 * @return the providerLastCommentQuery
	 */
	public String getProviderLastCommentQuery()
	{
		return providerLastCommentQuery;
	}

	/**
	 * @param providerLastCommentQuery the providerLastCommentQuery to set
	 */
	public void setProviderLastCommentQuery(String providerLastCommentQuery)
	{
		this.providerLastCommentQuery = providerLastCommentQuery;
	}

	/**
	 * @return the fetchHistoryQuery
	 */
	public String getFetchHistoryQuery()
	{
		return fetchHistoryQuery;
	}

	/**
	 * @param fetchHistoryQuery the fetchHistoryQuery to set
	 */
	public void setFetchHistoryQuery(String fetchHistoryQuery)
	{
		this.fetchHistoryQuery = fetchHistoryQuery;
	}

	/**
	 * @return the isTaskScreen
	 */
	public String getIsTaskScreen()
	{
		return isTaskScreen;
	}

	/**
	 * @param isTaskScreen the isTaskScreen to set
	 */
	public void setIsTaskScreen(String isTaskScreen)
	{
		this.isTaskScreen = isTaskScreen;
	}

	/**
	 * @return the showDocument
	 */
	public String getShowDocument()
	{
		return showDocument;
	}

	/**
	 * @param showDocument the showDocument to set
	 */
	public void setShowDocument(String showDocument)
	{
		this.showDocument = showDocument;
	}

	/**
	 * @return the taskToBeLaunched
	 */
	public String getTaskToBeLaunched()
	{
		return taskToBeLaunched;
	}

	/**
	 * @param taskToBeLaunched the taskToBeLaunched to set
	 */
	public void setTaskToBeLaunched(String taskToBeLaunched)
	{
		this.taskToBeLaunched = taskToBeLaunched;
	}

	/**
	 * @return the entityTypeForAgency
	 */
	public String getEntityTypeForAgency()
	{
		return entityTypeForAgency;
	}

	/**
	 * @param entityTypeForAgency the entityTypeForAgency to set
	 */
	public void setEntityTypeForAgency(String entityTypeForAgency)
	{
		this.entityTypeForAgency = entityTypeForAgency;
	}

	/**
	 * @return the entityType
	 */
	public String getEntityType()
	{
		return entityType;
	}

	/**
	 * @param entityType the entityType to set
	 */
	public void setEntityType(String entityType)
	{
		this.entityType = entityType;
	}

	// R4: Tab Level Comments
	public String getEntityTypeTabLevel()
	{
		return entityTypeTabLevel;
	}

	public void setEntityTypeTabLevel(String entityTypeTabLevel)
	{
		this.entityTypeTabLevel = entityTypeTabLevel;
	}

	// R4: Tab Level Comments Ends

	private String getSubBudgetIdFromTabLevelIdentifier(String asTabIdentifierId)
	{
		String[] lsTabNameTempSpliter = asTabIdentifierId.split(HHSConstants.UNDERSCORE);
		String lsSubBudgetIdKey = lsTabNameTempSpliter[2];

		return lsSubBudgetIdKey;
	}

	/* Start : Added in R6 */
	public String getTextAreaSize()
	{
		return textAreaSize;
	}

	public void setTextAreaSize(String textAreaSize)
	{
		this.textAreaSize = textAreaSize;
	}
	/* End : Added in R6 */
}