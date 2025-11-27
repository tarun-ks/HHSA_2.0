<!--This page will display the summary information like status,submitted by etc. of application and its corresponding  services -->
<%@page import="com.nyc.hhs.util.DateUtil"%>
<%@page import="com.nyc.hhs.model.ApplicationSummary"%>
<%@page import="java.util.List"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Iterator"%>
<%@ page errorPage="/error/errorpage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="com.nyc.hhs.frameworks.grid.*"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="com.nyc.hhs.model.ApplicationSummary"%>
<%@page
	import="com.nyc.hhs.frameworks.grid.*,com.nyc.hhs.constants.*,com.nyc.hhs.util.CommonUtil,com.nyc.hhs.constants.ComponentMappingConstant"%>
<portlet:defineObjects />
<head>
<script type="text/javascript"
	src="../resources/js/applicationSummary.js"></script>
<style type="text/css">
.alert-box {
	top: 25%;
	width: 43%;
}

#displayshared .overlaycontent1 .tabularWrapper {
	height: 400px !important;
}
</style>
</head>
<%
	if (CommonUtil
			.getConditionalRoleDisplay(
					ComponentMappingConstant.BA_S046_PAGE,
					request.getSession())
			|| CommonUtil.getConditionalRoleDisplay(
					ComponentMappingConstant.BA_S048_PAGE,
					request.getSession())
		/*Start : Added in R5*/
			|| CommonUtil.getConditionalRoleDisplay(
					ComponentMappingConstant.HP_S100_SECTION,
					request.getSession())
		/* End : Added in R5*/
			){
%>
<h2>Summary of Applications</h2>
<c:if test="${appExistingStatus ne null and appExistingStatus eq 'yes'}">
	<div class="passed" style="display: block"><b>Application is
	already created.</b></div>

</c:if>
<c:if test="${unAuthorizedAccessError ne null}">
	<div class="failed" style="display: block"><b>${unAuthorizedAccessError
	}</b></div>

</c:if>
<div class='hr'>
</div>
<div>
HHS Prequalification is now hosted in PASSPort - it is streamlined and easier than ever to complete!  If your organization wishes to begin the HHS Prequalification application process or has a pending prequalification application in HHS Accelerator, you must submit a new application in PASSPort. To complete an HHS Prequalification Application in PASSPort, you must have a PASSPort account. Click <a class="link" title="PASSPort Login" target="_blank" href="https://passport.cityofnewyork.us/page.aspx/en/usr/login?blockSSORedirect=false&amp;%20ReturnUrl=/page.aspx/en/buy/homepage">here</a> to create a PASSPort account using your NYC.ID or to log into an existing PASSPort account. The <strong>same</strong> NYC.ID login credentials you use to access HHS Accelerator may be used to login or create an account in PASSPort.
</div>


<!-- Form Data Starts -->
<form name="applicationSummaryForm"
	action="<portlet:actionURL>
	<c:if test="${cityApplicationSummary}">
		<portlet:param name='action' value='businessSummary'/>
	</c:if></portlet:actionURL>"
	method="post"><c:choose>
	<c:when test="${printView ne null and printView eq 'no'}">
		<div class="tabularCustomHead">Current Application</div>
		<div class='tabularWrapper'>
        </table>
		<table cellspacing="0" cellpadding="0" style='margin: 10px 0;'>
			<tr>
				<th style='width: 37%'><b>Provider Status: </b>Not Applied</th>
				<th><b>Expiration: </b></th>
			</tr>
		</table>
		</div>

		<div class="tabularWrapper">
		<table cellspacing="0" cellpadding="0" class="grid">
			<tr>
				<th style='width: 37%'>Application</th>
				<th>Status</th>
				<th>Status Set By</th>
				<th>Effective Date</th>
				<th>Action</th>
			</tr>
			<tr class="">
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
			</tr>
		</table>
		</div>
		<div class="tabularCustomHead">Application History</div>
		<div class="tabularWrapper">
		<table cellspacing="0" cellpadding="0" class="grid"
			id="completeListId">
			<tr>
				<th style='width: 37%'>Application</th>
				<th>Status</th>
				<th>Submitted By</th>
				<th>Date Submitted</th>
				<th>Effective Start Date</th>
				<th>Expiration End Date</th>
			</tr>
			<tr class="">
				<td class='wordWrap'></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
			</tr>
		</table>
		</div>
	</c:when>
	<c:otherwise>
		<input type="hidden" value="${lbTermsAndCondFlag}"
			id="lbTermsAndCondFlag" name="lbTermsAndCondFlag" />
		<%
			String applicationUser = (String) request
								.getAttribute("applicationUser");
		%>

		<%
			ApplicationSummary loApplicationSummaryObj = (ApplicationSummary) request
								.getAttribute("loApplicationSummaryObj");
		%>
		<div class='tabularWrapper'>
		<table cellspacing="0" cellpadding="0" style='margin: 10px 0;'>
			<tr>
				<th style='width: 37%'><b>Provider Status: </b>${loApplicationSummaryObj.msProviderStatus}</th>

				<th><b>Expiration: </b><span id="expDate"> <c:if
					test="${loApplicationSummaryObj.msProviderStatus ne 'Rejected'}">
					<%=loApplicationSummaryObj
									.getMsExpirationDate() != null ? DateUtil
									.getDateByFormat("yyyy-MM-dd",
											"MM/dd/yyyy",
											loApplicationSummaryObj
													.getMsExpirationDate()
													.toString()) : ""%>
				</c:if> </span></th>
			</tr>
		</table>
		</div>
		<input type="hidden" id="workflowId" value="" name="workflowId" />
		<input type="hidden" id="applicationType" value=""
			name="applicationType" />
		<input type="hidden" id="newStatusValue" value=""
			name="newStatusValue" />
		<input type="hidden" id="oldStatusValue" value=""
			name="oldStatusValue" />
		<input type="hidden" id="serviceElementId" value=""
			name="serviceElementId" />
		<input type="hidden" id="viewHistory" value="" name="viewHistory" />
		<input type="hidden" id="historyType" value="" name="historyType" />
		<input type="hidden" id="serviceApplicationName" value=""
			name="serviceApplicationName" />
		<input type="hidden" id="providerExpirationDate"
			value='<%=loApplicationSummaryObj.getMsExpirationDate() != null ? DateUtil
								.getDateByFormat("yyyy-MM-dd", "dd/MM/yyyy",
										loApplicationSummaryObj
												.getMsExpirationDate()
												.toString()) : ""%>' />

		<div class="tabularCustomHead">Current Application</div>
		<c:if test="${param.workflow_success_key eq 'true'}">
			<div class="passed" style="display: block"><b> Your
			application has been successfully submitted for review. The HHS
			Accelerator team will now review your application. You will receive a
			notification of their decision via email and within the HHS
			Accelerator system.</b></div>
		</c:if>
		<c:if test="${param.workflow_success_key eq 'false'}">
			<div class="passed" style="display: block"><b>This
			application is already submitted.</b></div>
		</c:if>
		<c:if test="${param.workflow_success_key eq 'withdrawTrue'}">
			<div class="passed" style="display: block"><b>Your
			withdrawal request has been submitted.</b></div>
		</c:if>
		<c:choose>
			<c:when test="${currentItemList ne null and !empty currentItemList }">
				<input type="hidden" id="hiddenFieldId"
					value="<%=((List<ApplicationSummary>) request
										.getAttribute("currentItemList"))
										.get(0).getMsAppStatus()%>"
					name="applicationStatus" />
				<c:set var="overwriteStatus"
					value='<%=((List<ApplicationSummary>)request.getAttribute("currentItemList")).get(0).getMsSuperSedingStatus()%>'></c:set>
				<input type="hidden"
					value="<%=((List<ApplicationSummary>) request
										.getAttribute("currentItemList"))
										.get(0).getMsBusinessAppId()%>"
					id="addButtonBusinessId" name="appId" />
			</c:when>
			<c:otherwise>
				<input type="hidden" id="hiddenFieldId" value="" />
				<input type="hidden" value="" id="addButtonBusinessId"
					name="applicationId" />
			</c:otherwise>
		</c:choose>
		<div class="tabularWrapper">
		<table cellspacing="0" cellpadding="0" class="grid">
			<tr>
				<th style='width: 37%'>Application</th>
				<th>Status</th>
				<%
					if (CommonUtil.getConditionalRoleDisplay(
										ComponentMappingConstant.BA_S044__S133_SECTION,
										request.getSession())
										|| CommonUtil
												.getConditionalRoleDisplay(
														ComponentMappingConstant.BA_S047_SECTION,
														request.getSession())
										// Start : R5 Added 
										|| CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S027_PAGE_7, request.getSession()))
										// End : R5 Added
										 {
				%>
				<th>Status Set By</th>
				<th>Effective Date</th>
				<%--Start : R5 Added --%>
				<c:if test="${org_type ne 'agency_org'}">
					<th>Action</th>
				</c:if>
				<%--End : R5 Added --%>
				<%
					}
				%>

			</tr>
			<%
				List<ApplicationSummary> currentItemList = (List<ApplicationSummary>) request
									.getAttribute("currentItemList");
							Integer loCounter = 0;
							Iterator currentValueIterator = currentItemList
									.iterator();
							while (currentValueIterator.hasNext()) {
								ApplicationSummary serviceObj = (ApplicationSummary) currentValueIterator
										.next();
								++loCounter;
			%>
			<tr class="<%=loCounter % 2 == 0 ? "alternate" : ""%>">
				<td class='wordWrap'><%=serviceObj.getMsAppName()%></td>
				<td class='capitalize'><%=serviceObj.getMsSuperSedingStatus() != null ? serviceObj
									.getMsSuperSedingStatus()
									: serviceObj.getMsAppStatus() != null
											&& serviceObj
													.getMsAppStatus()
													.equalsIgnoreCase(
															ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS) ? serviceObj
											.getMsAppStatus() + "s"
											: serviceObj.getMsAppStatus()%></td>
				<%
					if (CommonUtil
											.getConditionalRoleDisplay(
													ComponentMappingConstant.BA_S044__S133_SECTION,
													request.getSession())
											|| CommonUtil
													.getConditionalRoleDisplay(
															ComponentMappingConstant.BA_S047_SECTION,
															request.getSession())
											//Start : R5 Added
											|| CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S027_PAGE_7, request.getSession())
											//End : R5 Added
											) {
				%>
				<td><%=serviceObj.getStatusSetBy() != null ? serviceObj
										.getStatusSetBy() : ""%></td>
				<td><%=serviceObj.getCityUserEffectiveDate() != null ? DateUtil
										.getDateByFormat(
												"yyyy-MM-dd",
												"MM/dd/yyyy",
												serviceObj
														.getCityUserEffectiveDate()
														.toString())
										: ""%></td>
				<%--Start : R5 Added --%>
				<c:if test="${org_type ne 'agency_org'}">
				<td><select name="select"
					onchange="selectValue(this,'<%=serviceObj.getMsServiceElementId()%>','<%=serviceObj.getMsAppStatus()%>','<%=serviceObj.getMsServiceAppId()%>','<%=serviceObj.getMsAppName()%>','<%=serviceObj.getMsWorkflowId()%>','')"
					id="selectBoxId<%=loCounter%>" >
					<option value="-1">I need to...</option>
					<%
						if (CommonUtil
													.getConditionalRoleDisplay(
															ComponentMappingConstant.BA_S044__S133_SECTION,
															request.getSession())) {
					%>
					<%
						if (serviceObj.getDisplaySuspendValue() != null
														&& !serviceObj
																.getDisplaySuspendValue()
																.equalsIgnoreCase("")) {
					%>
					<option value="1">Suspend</option>
					<%
						}
					%>
					<%
						if (serviceObj
														.getDisplayConditionallyValue() != null
														&& !serviceObj
																.getDisplayConditionallyValue()
																.equalsIgnoreCase("")) {
					%>
					<option value="2">Conditionally Approve</option>
					<%
						}
											}
					%>
					<%
						if (CommonUtil
													.getConditionalRoleDisplay(
															ComponentMappingConstant.BA_S047_SECTION,
															request.getSession())) {
					%>
					<option value="3">View History</option>
					<%
						}
					%>
				</select></td>
				</c:if>
				<%--End : R5 Added --%>
				<%
					}
				%>
			</tr>
			<%
				}
			%>
		</table>
		</div>
		<br />

		<input type="hidden" name="action_redirect" value=""
			id="termsCondition" />
		<input type="hidden" name="formName" value="HHSFile" />
		<input type="hidden" name="Hyperlink" value="" />
		<input type="hidden" name="newApplicationProcess" value=""
			id="newApplicationProcess" />
		<input type="hidden" name="serviceAppID" value="" id="serviceAppID" />
<c:if test="${applicationUser ne 'accelerator'}">
			<div class='buttonholder'>
			<%-- [Start] R9.1.0 QC9608 		
			
			    <input id="add" type="submit"
				${loDisabledButtonMap['addButton']}  class='button'
				value="+ Add Service" title="+ Add Service"
				onclick="displayTermsAndCondition('serviceTermsAndCondition')" /> <input
				id="start" type="submit"
				${loDisabledButtonMap['startButton']}  class='button'
				value="Start New Accelerator Application"
				title="Start New Accelerator Application"
				onclick="displayTermsAndCondition('termsAndCondition', 'newApplicationProcess');" />
						[End] R9.1.0 QC9608 	--%>
			</div>
		</c:if> 

		<div class="tabularCustomHead">Application History</div>
		<div class="tabularWrapper">
		<table cellspacing="0" cellpadding="0" class="grid"
			id="completeListId">
			<tr>
				<th style='width: 37%'>Application</th>
				<th>Status</th>
				<th>Submitted By</th>
				<th>Date Submitted</th>
				<th>Effective Start Date</th>
				<th>Expiration End Date</th>
				<%
					if (CommonUtil.getConditionalRoleDisplay(
										ComponentMappingConstant.BA_S044__S133_SECTION,
										request.getSession())
										|| CommonUtil
												.getConditionalRoleDisplay(
														ComponentMappingConstant.BA_S047_SECTION,
														request.getSession())) {
				%>
				<c:if test="${lsDisplayDropDown}">
					<th id="historyAction">Action</th>
				</c:if>
				<%
					}
				%>
			</tr>
			<%
				Boolean appWithDrawn = (Boolean) request
									.getAttribute("withDrawnRequest");
			%>
			<%
				Map<ApplicationSummary, List<ApplicationSummary>> finalMap = (LinkedHashMap<ApplicationSummary, List<ApplicationSummary>>) request
									.getAttribute("completeItemMap");
							if (finalMap != null && !finalMap.isEmpty()) {
								Boolean isWithdrawl = false;
								Integer mapCounter = 0;
								Iterator iterator = finalMap.entrySet().iterator();
								while (iterator.hasNext()) {
									Map.Entry mapEntry = (Map.Entry) iterator
											.next();
									ApplicationSummary applicationSummary = (ApplicationSummary) mapEntry
											.getKey();
									List<ApplicationSummary> valueList = (List<ApplicationSummary>) mapEntry
											.getValue();
									++mapCounter;
			%>

			<tr class="<%=mapCounter % 2 == 0 ? "alternate"
										: ""%>">
				<%
					if (applicationSummary.getDisplayExclamanationBusiness() != null
						&& applicationSummary.getDisplayExclamanationBusiness()) {
				%>
				<td>
				<c:set var="isHyperlink" value="true" />
				<c:if test="${org_type eq 'agency_org'}">
				<%if(!applicationSummary.getMsAppStatus().equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED) && !applicationSummary.getMsAppStatus().equalsIgnoreCase(ApplicationConstants.STATUS_EXPIRED)){%>
					<c:set var="isHyperlink" value="false" />
				<%}%>
				 </c:if>
				 <c:choose>
					<c:when test="${isHyperlink}">
						<a style="float: left; width: 285px"
							href="<portlet:actionURL><portlet:param name='applicationStatus' value='<%=applicationSummary.getMsAppStatus()%>'/>
						<portlet:param name='bussAppId' value='<%=applicationSummary.getMsBusinessAppId()%>'/>
						<portlet:param name='applicationType' value='business'/>
						<portlet:param name='appId' value='<%=applicationSummary.getMsApplicationId()%>'/>
						<portlet:param name='overwriteStatus' value='${overwriteStatus}'/>
						<portlet:param name='headerPostSubmitionBusiness' value="businessapplication"/>
						<c:if test="${cityApplicationSummary}">
							<portlet:param name='action' value='businessSummary'/>
						</c:if>
						</portlet:actionURL>">
						<strong><%=applicationSummary.getMsAppName()%></strong>
							</a>
					</c:when>
					<c:otherwise>
						<strong><%=applicationSummary.getMsAppName()%></strong>
					</c:otherwise>
				</c:choose>
				<div class="withdrawal_info">

				<div
					id="<%=applicationSummary
											.getMsBusinessAppId()%>_<%=applicationSummary.getMsAppName()%>"
					class="withdrawal_info_tooltip">
				<div class="tooltip_content">
				<p><b>A request for withdrawal has been <br />submitted for this
				application</b></p>
				<b>Requested Date:</b><span><%=applicationSummary
											.getTimeStampStatus1() != null ? DateUtil
											.getDateByFormat(
													"yyyy-MM-dd",
													"MM/dd/yyyy",
													applicationSummary
															.getTimeStampStatus1()
															.toString())
											: " "%></span> <br />
				<b>Requester:</b><span><%=applicationSummary
											.getMsRequester()%></span></div>
				</div>

				<img style="float: left" class='exclamationIcon'
					src="../framework/skins/hhsa/images/exclamation.png"
					alt="Exclamation" title="Exclamation"
					onMouseOver="setVisibility('<%=applicationSummary
											.getMsBusinessAppId()%>_<%=applicationSummary.getMsAppName()%>', 'inline');"
					onMouseOut="setVisibility('<%=applicationSummary
											.getMsBusinessAppId()%>_<%=applicationSummary.getMsAppName()%>', 'none');" />
				</div>
				</td>
				<%
					isWithdrawl = true;
										} else {
											isWithdrawl = false;
				%>
				<td>
				<%
					if (applicationSummary
													.getFinalViewBusinessExclamanationSign() != null
													&& applicationSummary
															.getFinalViewBusinessExclamanationSign()) {
				%>
				<div class="withdrawal_info">
				<div
					id="<%=applicationSummary
												.getMsBusinessAppId()%>_<%=applicationSummary
												.getMsAppName()%>"
					class="withdrawal_info_tooltip">
				<div class="tooltip_content">
				<p><b>This</b><span style="text-transform: lowercase"><b>
				application has been <%=applicationSummary
												.getMsSuperSedingStatus()%>.</b></span></p>
				<%
					if (applicationSummary
														.getMsSuperSedingStatus() != null
														&& applicationSummary
																.getMsSuperSedingStatus()
																.equalsIgnoreCase(
																		ApplicationConstants.STATUS_SUSPEND)) {
				%> <b>Suspension Date:</b><span><%=applicationSummary
													.getTimeStampStatus1() != null ? DateUtil
													.getDateByFormat(
															"yyyy-MM-dd",
															"MM/dd/yyyy",
															applicationSummary
																	.getTimeStampStatus1()
																	.toString())
													: " "%></span> <%
 	} else if (applicationSummary
 										.getMsSuperSedingStatus() != null
 										&& applicationSummary
 												.getMsSuperSedingStatus()
 												.equalsIgnoreCase(
 														ApplicationConstants.STATUS_WITHDRAWN)) {
 %> <b>Withdrawn Date:</b><span><%=applicationSummary
													.getTimeStampStatus1() != null ? DateUtil
													.getDateByFormat(
															"yyyy-MM-dd",
															"MM/dd/yyyy",
															applicationSummary
																	.getTimeStampStatus1()
																	.toString())
													: " "%></span> <%
 	} else {
 %> <b><%=applicationSummary
													.getMsSuperSedingStatus()%> Date:</b><span><%=applicationSummary
													.getTimeStampStatus1() != null ? DateUtil
													.getDateByFormat(
															"yyyy-MM-dd",
															"MM/dd/yyyy",
															applicationSummary
																	.getTimeStampStatus1()
																	.toString())
													: " "%></span> <%
 	}
 %>
				</div>
				</div>

				<img style="float: left" class='exclamationIcon'
					src="../framework/skins/hhsa/images/exclamation.png"
					alt="Exclamation" title="Exclamation"
					onMouseOver="setVisibility('<%=applicationSummary
												.getMsBusinessAppId()%>_<%=applicationSummary
												.getMsAppName()%>', 'inline');"
					onMouseOut="setVisibility('<%=applicationSummary
												.getMsBusinessAppId()%>_<%=applicationSummary
												.getMsAppName()%>', 'none');" />
				</div>
				<%
					}
				%>
				 <c:set var="isHyperlink" value="true" />
				 <c:if test="${org_type eq 'agency_org'}">
				<%if(!applicationSummary.getMsAppStatus().equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED) && !applicationSummary.getMsAppStatus().equalsIgnoreCase(ApplicationConstants.STATUS_EXPIRED)){%>
					<c:set var="isHyperlink" value="false" />
				<%}%>
				 </c:if>
				 <c:choose>
					<c:when test="${isHyperlink}">
					<a style="float: left; width: 285px"
							href="<portlet:actionURL><portlet:param name='applicationStatus' value='<%=applicationSummary.getMsAppStatus()%>'/>
					<portlet:param name='bussAppId' value='<%=applicationSummary.getMsBusinessAppId()%>'/>
					<portlet:param name='applicationType' value='business'/>
					<portlet:param name='appId' value='<%=applicationSummary.getMsApplicationId()%>'/>
					<portlet:param name='overwriteStatus' value='${overwriteStatus}'/>
					<portlet:param name='headerPostSubmitionBusiness' value="businessapplication"/>
					<c:if test="${cityApplicationSummary}">
						<portlet:param name='action' value='businessSummary'/>
					</c:if>
					</portlet:actionURL>">
						<strong><%=applicationSummary.getMsAppName()%></strong>
					</a>
					</c:when>
					<c:otherwise>
						<strong><%=applicationSummary.getMsAppName()%></strong>
					</c:otherwise>
					</c:choose>
				</td>
				<%
					isWithdrawl = false;
										}
				%>
				<td class='capitalize'>
				<%
					if (applicationSummary.getMsAppStatus() != null
												&& applicationSummary
														.getMsAppStatus()
														.equalsIgnoreCase(
																ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS)) {
				%> <%=applicationSummary
											.getMsAppStatus() + "s"%> <%
 	} else {
 %> <%=applicationSummary
											.getMsAppStatus() != null ? applicationSummary
											.getMsAppStatus() : " "%> <%
 	}
 %>
				</td>
				<td><%=applicationSummary
										.getMsAppSubmittedBy() != null ? applicationSummary
										.getMsAppSubmittedBy() : " "%></td>
				<td><%=applicationSummary
										.getMdAppSubmissionDate() != null ? DateUtil
										.getDateByFormat(
												"yyyy-MM-dd",
												"MM/dd/yyyy",
												applicationSummary
														.getMdAppSubmissionDate()
														.toString())
										: " "%></td>
				<td>
				<%
					if (applicationSummary.getMsAppStatus() != null
												&& !(applicationSummary
														.getMsAppStatus()
														.equalsIgnoreCase(
																ApplicationConstants.STATUS_DRAFT) || applicationSummary
														.getMsAppStatus()
														.equalsIgnoreCase(ApplicationConstants.APP_STATUS_DRAFT))) {
				%> <%=applicationSummary
											.getMdAppStartDate() != null ? DateUtil
											.getDateByFormat(
													"yyyy-MM-dd",
													"MM/dd/yyyy",
													applicationSummary
															.getMdAppStartDate()
															.toString())
											: " "%></td>

				<%
					} else {
				%>
				<%=applicationSummary
											.getMdAppStartDate() != null ? DateUtil
											.getDateByFormat(
													"yyyy-MM-dd",
													"MM/dd/yyyy",
													applicationSummary
															.getMdAppStartDate()
															.toString())
											: ""%>
				<%
					}
				%>
				<td>
				<%
					if (applicationSummary.getMsAppStatus() != null
												&& !(applicationSummary
														.getMsAppStatus()
														.equalsIgnoreCase(
																ApplicationConstants.STATUS_DRAFT) || applicationSummary
														.getMsAppStatus()
														.equalsIgnoreCase(ApplicationConstants.APP_STATUS_DRAFT))) {
				%> <%=applicationSummary
											.getMdAppExpirationDate() != null ? DateUtil
											.getDateByFormat(
													"yyyy-MM-dd",
													"MM/dd/yyyy",
													applicationSummary
															.getMdAppExpirationDate()
															.toString())
											: ""%> <%
 	} else {
 %> <%=applicationSummary
											.getMdAppExpirationDate() != null ? DateUtil
											.getDateByFormat(
													"yyyy-MM-dd",
													"MM/dd/yyyy",
													applicationSummary
															.getMdAppExpirationDate()
															.toString())
											: ""%> <%
 	}
 %>
				</td>
				<%
					if (CommonUtil
												.getConditionalRoleDisplay(
														ComponentMappingConstant.BA_S044__S133_SECTION,
														request.getSession())
												|| CommonUtil
														.getConditionalRoleDisplay(
																ComponentMappingConstant.BA_S047_SECTION,
																request.getSession())) {
				%>
				<%
					if (applicationSummary
													.getDisplayHistoryDropDown()) {
				%>
				<td><select name="select"
					onchange="selectValue(this,'<%=applicationSummary
												.getMsServiceElementId()%>','<%=applicationSummary
												.getMsAppStatus()%>','<%=applicationSummary
												.getMsServiceAppId()%>','<%=applicationSummary
												.getMsAppName()%>','<%=applicationSummary
												.getMsWorkflowId()%>','<%=applicationSummary
												.getMsBusinessAppId()%>')"
					id="selectBoxId<%=loCounter%>">
					<option value="-1"></option>
					<option value="1">Suspend</option>
				</select></td>
				<%
					} else {
				%>
				<td>&nbsp;</td>
				<%
					}
										}
				%>

			</tr>
			<%
				if (valueList != null && !valueList.isEmpty()) {
										Integer counter = 0;
										Iterator valueIterator = valueList
												.iterator();
										while (valueIterator.hasNext()) {
											ApplicationSummary serviceObj = (ApplicationSummary) valueIterator
													.next();
											++counter;
			%>
			<tr class="<%=counter % 2 == 0 ? ""
												: "alternate"%>">
				<td class='wordWrap'><img src="../framework/skins/hhsa/images/childArrow.png" alt="subchild" style="float: left" width="27" height="20" title="subchild" /> 
				<% if (serviceObj.getMsAppStatus().equalsIgnoreCase(ApplicationConstants.STATUS_DRAFT)) {%> 
 					
 					<c:set var="draftLink" value="<%=serviceObj.getIsDraftServiceLink()%>" /> 
 					<c:choose>
						<c:when test="${draftLink || draftLink eq 'true'}">
							
							 <c:set var="isHyperlink" value="true" />
							 <c:if test="${org_type eq 'agency_org'}">
							<%if(!serviceObj.getMsAppStatus().equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED) && !serviceObj.getMsAppStatus().equalsIgnoreCase(ApplicationConstants.STATUS_EXPIRED)){%>
								<c:set var="isHyperlink" value="false" />
							<%}%>
							 </c:if>
							  <c:choose>
 								<c:when test="${isHyperlink}">
 									<a style="float: left; width: 285px"
										href="<portlet:actionURL><portlet:param name='applicationStatus' value='<%=serviceObj.getMsAppStatus()%>'/>
										<portlet:param name='bussAppId' value='<%=serviceObj.getMsServiceElementId()%>'/>
										<portlet:param name='applicationType' value='service'/>
										<portlet:param name='businessApplicationId' value='<%=applicationSummary.getMsBusinessAppId()%>'/>
										<portlet:param name='appId' value='<%=serviceObj.getMsApplicationId()%>'/>
										<portlet:param name='overwriteStatus' value='${overwriteStatus}'/>
										<portlet:param name='headerPostSubmitionService' value="service"/>
										<c:if test="${cityApplicationSummary}">
											<portlet:param name='action' value='businessSummary'/>
										</c:if>
										</portlet:actionURL>">
										<%=serviceObj.getMsAppName()%>
									</a>
 								</c:when>
 								<c:otherwise>
 									<%=serviceObj.getMsAppName()%>
 								</c:otherwise>
							</c:choose>
						<input type='hidden' name="newAppIdForServiceApplication"
							value="<%=serviceObj.getMsApplicationId()%>" />
					</c:when>
					<c:otherwise>
						<%=serviceObj.getMsAppName()%>
					</c:otherwise>
				</c:choose> <%
 	} else {
 %> <%
 	if (applicationSummary.getMsAppStatus().equalsIgnoreCase(ApplicationConstants.STATUS_DRAFT)
 		&& serviceObj.getMsAppStatus().equalsIgnoreCase(ApplicationConstants.DEACTIVATED)) {
 %> <%=serviceObj.getMsAppName()%> <%
 	} else {
 %> 
 <c:set var="isHyperlink" value="true" />
 <c:if test="${org_type eq 'agency_org'}">
<%if(!serviceObj.getMsAppStatus().equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED) && !serviceObj.getMsAppStatus().equalsIgnoreCase(ApplicationConstants.STATUS_EXPIRED)){%>
	<c:set var="isHyperlink" value="false" />
<%} %>
 </c:if>
 <c:choose>
 	<c:when test="${isHyperlink}">
 		<a style="float: left; width: 285px"
					href="<portlet:actionURL><portlet:param name='applicationStatus' value='<%=serviceObj.getMsAppStatus()%>'/>
				<portlet:param name='bussAppId' value='<%=serviceObj.getMsServiceElementId()%>'/>
				<portlet:param name='applicationType' value='service'/>
				<portlet:param name='serviceApplicationId' value='<%=serviceObj.getMsServiceAppId()%>'/>
				<portlet:param name='businessApplicationId' value='<%=applicationSummary.getMsBusinessAppId()%>'/>
				<portlet:param name='appId' value='<%=serviceObj.getMsApplicationId()%>'/>
				<portlet:param name='overwriteStatus' value='${overwriteStatus}'/>
				<portlet:param name='headerPostSubmitionService' value="service"/>
				<c:if test="${cityApplicationSummary}">
					<portlet:param name='action' value='businessSummary'/>
				</c:if>
				</portlet:actionURL>">
				<%=serviceObj.getMsAppName()%>
					</a> 
 	</c:when>
 	<c:otherwise>
 		<%=serviceObj.getMsAppName()%>
 	</c:otherwise>
 </c:choose>
 
				<%
 	}
 %> <%
 	}
 								if (isWithdrawl) {
 %>
				<div class="withdrawal_info">
				<div
					id="<%=applicationSummary
													.getMsBusinessAppId()%>_<%=serviceObj
													.getMsServiceElementId()%>"
					class="withdrawal_info_tooltip">
				<div class="tooltip_content">
				<p><b>A request for withdrawal has been <br />submitted for this
				application</b></p>
				<b>Requested Date:</b><span><%=applicationSummary
													.getTimeStampStatus1() != null ? DateUtil
													.getDateByFormat(
															"yyyy-MM-dd",
															"MM/dd/yyyy",
															applicationSummary
																	.getTimeStampStatus1()
																	.toString())
													: " "%></span> <br />
				<b>Requester:</b><span><%=applicationSummary
													.getMsRequester()%></span></div>
				</div>
				<img style="float: left" class='exclamationIcon'
					src="../framework/skins/hhsa/images/exclamation.png"
					alt="Exclamation" title="Exclamation"
					onMouseOver="setVisibility('<%=applicationSummary
													.getMsBusinessAppId()%>_<%=serviceObj
													.getMsServiceElementId()%>', 'inline');"
					onMouseOut="setVisibility('<%=applicationSummary
													.getMsBusinessAppId()%>_<%=serviceObj
													.getMsServiceElementId()%>', 'none');" />
				</div>


				<%
					} else if (!isWithdrawl
														&& serviceObj
																.getDisplayExclamanationService()) {
				%>
				<div class="withdrawal_info">
				<div
					id="<%=applicationSummary
													.getMsBusinessAppId()%>_<%=serviceObj
													.getMsServiceElementId()%>"
					class="withdrawal_info_tooltip">
				<div class="tooltip_content">
				<p><b>A request for withdrawal has been <br />submitted for this
				application</b></p>
				<b>Requested Date:</b><span><%=serviceObj
													.getTimeStampStatus1() != null ? DateUtil
													.getDateByFormat(
															"yyyy-MM-dd",
															"MM/dd/yyyy",
															serviceObj
																	.getTimeStampStatus1()
																	.toString())
													: " "%></span> <br />
				<b>Requester:</b><span><%=serviceObj
													.getMsRequester()%></span></div>
				</div>
				<img style="float: left" class='exclamationIcon'
					src="../framework/skins/hhsa/images/exclamation.png"
					alt="Exclamation" title="Exclamation"
					onMouseOver="setVisibility('<%=applicationSummary
													.getMsBusinessAppId()%>_<%=serviceObj
													.getMsServiceElementId()%>', 'inline');"
					onMouseOut="setVisibility('<%=applicationSummary
													.getMsBusinessAppId()%>_<%=serviceObj
													.getMsServiceElementId()%>', 'none');" />
				</div>

				<%
					} else if (!isWithdrawl
														&& (serviceObj.getMsAppStatus() != null && serviceObj
																.getMsAppStatus()
																.equalsIgnoreCase(
																		ApplicationConstants.DEACTIVATED))) {
				%>
				<div class="withdrawal_info">
				<div id="deActivated<%=counter%>"
					class="withdrawal_info_tooltip_Completed">
				<div class="tooltip_content_Completed">
				<p><b>Application no longer required for this service </b></p>
				</div>
				</div>
				<input type='hidden' name="newAppIdForServiceApplication"
							value="<%=serviceObj.getMsApplicationId()%>" />
				
				<img style="float: left" class='exclamationIcon'
					src="../framework/skins/hhsa/images/exclamation.png"
					alt="Exclamation" title="Exclamation"
					onMouseOver="setVisibility('deActivated<%=counter%>', 'inline');"
					onMouseout="setVisibility('deActivated<%=counter%>', 'none');" /></div>

				<%
					} else if (!isWithdrawl
														&& (serviceObj
																.getFinalViewServicdExclamanationSign() != null && serviceObj
																.getFinalViewServicdExclamanationSign())) {
				%>
				<div class="withdrawal_info">
				<div
					id="<%=applicationSummary
													.getMsBusinessAppId()%>_<%=serviceObj
													.getMsServiceElementId()%>"
					class="withdrawal_info_tooltip">
				<div class="tooltip_content">
				<p><b>This</b><span style="text-transform: lowercase"><b>
				service has been <%=serviceObj
													.getMsSuperSedingStatus()%>.</b></span></p>
				<%
					if (serviceObj
															.getMsSuperSedingStatus() != null
															&& serviceObj
																	.getMsSuperSedingStatus()
																	.equalsIgnoreCase(
																			ApplicationConstants.STATUS_SUSPEND)) {
				%> <b>Suspension Date:</b><span><%=serviceObj
														.getTimeStampStatus1() != null ? DateUtil
														.getDateByFormat(
																"yyyy-MM-dd",
																"MM/dd/yyyy",
																serviceObj
																		.getTimeStampStatus1()
																		.toString())
														: " "%></span> <%
 	} else if (serviceObj
 											.getMsSuperSedingStatus() != null
 											&& serviceObj
 													.getMsSuperSedingStatus()
 													.equalsIgnoreCase(
 															ApplicationConstants.STATUS_WITHDRAWN)) {
 %> <b>Withdrawn Date:</b><span><%=serviceObj
														.getTimeStampStatus1() != null ? DateUtil
														.getDateByFormat(
																"yyyy-MM-dd",
																"MM/dd/yyyy",
																serviceObj
																		.getTimeStampStatus1()
																		.toString())
														: " "%></span> <%
 	} else {
 %> <b><%=serviceObj
														.getMsSuperSedingStatus()%> Date:</b><span><%=serviceObj
														.getTimeStampStatus1() != null ? DateUtil
														.getDateByFormat(
																"yyyy-MM-dd",
																"MM/dd/yyyy",
																serviceObj
																		.getTimeStampStatus1()
																		.toString())
														: " "%></span> <%
 	}
 %>
				</div>
				</div>
				<img style="float: left" class='exclamationIcon'
					src="../framework/skins/hhsa/images/exclamation.png"
					alt="Exclamation" title="Exclamation"
					onMouseOver="setVisibility('<%=applicationSummary
													.getMsBusinessAppId()%>_<%=serviceObj
													.getMsServiceElementId()%>', 'inline');"
					onMouseOut="setVisibility('<%=applicationSummary
													.getMsBusinessAppId()%>_<%=serviceObj
													.getMsServiceElementId()%>', 'none');" />
				</div>
				<%
					}
				%>
				</td>
				<td class='capitalize'>
				<%
					if (serviceObj.getMsAppStatus() != null
														&& serviceObj
																.getMsAppStatus()
																.equalsIgnoreCase(
																		ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS)) {
				%> <%=serviceObj
													.getMsAppStatus() + "s"%> <%
 	} else {
 %> <%=serviceObj
													.getMsAppStatus() != null ? serviceObj
													.getMsAppStatus() : " "%> <%
 	}
 %>
				</td>
				<td><%=serviceObj
												.getMsAppSubmittedBy() != null ? serviceObj
												.getMsAppSubmittedBy() : " "%></td>
				<td><%=serviceObj
												.getMdAppSubmissionDate() != null ? DateUtil
												.getDateByFormat(
														"yyyy-MM-dd",
														"MM/dd/yyyy",
														serviceObj
																.getMdAppSubmissionDate()
																.toString())
												: " "%></td>
				<td>
				<%
					if (serviceObj.getMsAppStatus() != null
														&& !(serviceObj
																.getMsAppStatus()
																.equalsIgnoreCase(
																		ApplicationConstants.STATUS_DRAFT) || serviceObj
																.getMsAppStatus()
																.equalsIgnoreCase(
																		ApplicationConstants.APP_STATUS_DRAFT))) {
				%> <%=serviceObj
													.getMdAppStartDate() != null ? DateUtil
													.getDateByFormat(
															"yyyy-MM-dd",
															"MM/dd/yyyy",
															serviceObj
																	.getMdAppStartDate()
																	.toString())
													: " "%> <%
 	} else {
 %> <%=serviceObj
													.getMdAppStartDate() != null ? DateUtil
													.getDateByFormat(
															"yyyy-MM-dd",
															"MM/dd/yyyy",
															serviceObj
																	.getMdAppStartDate()
																	.toString())
													: " "%> <%
 	}
 %>
				</td>
				<td>
				<%
					if (serviceObj.getMsAppStatus() != null
														&& !(serviceObj
																.getMsAppStatus()
																.equalsIgnoreCase(
																		ApplicationConstants.STATUS_DRAFT) || serviceObj
																.getMsAppStatus()
																.equalsIgnoreCase(
																		ApplicationConstants.APP_STATUS_DRAFT))) {
				%> <%=serviceObj
													.getMdAppExpirationDate() != null ? DateUtil
													.getDateByFormat(
															"yyyy-MM-dd",
															"MM/dd/yyyy",
															serviceObj
																	.getMdAppExpirationDate()
																	.toString())
													: ""%> <%
 	} else {
 %> <%=serviceObj
													.getMdAppExpirationDate() != null ? DateUtil
													.getDateByFormat(
															"yyyy-MM-dd",
															"MM/dd/yyyy",
															serviceObj
																	.getMdAppExpirationDate()
																	.toString())
													: ""%> <%
 	}
 %>
				</td>
				<%
					if (CommonUtil
														.getConditionalRoleDisplay(
																ComponentMappingConstant.BA_S044__S133_SECTION,
																request.getSession())
														|| CommonUtil
																.getConditionalRoleDisplay(
																		ComponentMappingConstant.BA_S047_SECTION,
																		request.getSession())) {
				%>

				<%
					if (serviceObj
															.getDisplayHistoryDropDown()) {
				%>
				<td><select name="select"
					onchange="selectValue(this,'<%=serviceObj
														.getMsServiceElementId()%>','<%=serviceObj
														.getMsAppStatus()%>','<%=serviceObj
														.getMsServiceAppId()%>','<%=serviceObj
														.getMsAppName()%>','<%=serviceObj
														.getMsWorkflowId()%>','')"
					id="selectBoxId<%=loCounter%>">
					<option value="-1"></option>
					<option value="1">Suspend</option>
				</select></td>
				<%
					} else {
				%>
				<td>&nbsp;</td>
				<%
					}
												}
				%>
			</tr>
			<%
				}
									}
								}
							}
			%>
		</table>
		</div>
		<%
			if (CommonUtil.getConditionalRoleDisplay(
								ComponentMappingConstant.BA_S044__S133_SECTION,
								request.getSession())
								|| CommonUtil
										.getConditionalRoleDisplay(
												ComponentMappingConstant.BA_S047_SECTION,
												request.getSession())) {
		%>
		<div class="overlay"></div>
		<div class="alert-box">
		<div class="tabularCustomHead" id="popupHeader">Service -
		Withdrawal Request</div>

		<div class="tabularContainer" id="commentsId">
		<div class="formcontainer">
		<div class="row"><label class="required">*</label><span
			id="headerText">Please enter any comments associated with this
		request for withdrawal:</span></div>
		<div class="row"><textarea name="comments" cols="" rows=""
			class="input" style="width: 380px;" onkeyup="setMaxLength(this,500)"
			onkeypress="setMaxLength(this,500)" id="textBoxId"></textarea> <span
			id="errorMessage" class="individualError"></span></div>
		<div class="buttonholder"><input type="button" value="Cancel"
			title="Cancel" onclick="closePopup()" id="closeButtonId"
			class="graybtutton" /> <input type="button"
			value="Request Withdrawal" title="Suspend"
			onclick="return submitRequest(this)" id="submitButtonId" /></div>
		</div>
		</div>
		<div id="displayshared" style="display: none"></div>
		<a href="javascript:void(0);" class="exit-panel" id="closePopUp"
			onclick="closePopup()"></a></div>
		<%
			}
		%>
	</c:otherwise>
</c:choose></form>
<div>&nbsp;</div>
<%
	} else {
%>
<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
<%
	}
%>