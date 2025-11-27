<%-- This JSP is for Assigned tasks portlet Count--%>
<div>
	<div id="taskAssignedPortlet">
		<%@page import="java.util.ArrayList, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant, com.nyc.hhs.constants.HHSConstants"%>
		<%@page language="java" contentType="text/html;charset=UTF-8"%>
		<%@page import="com.nyc.hhs.service.filenetmanager.p8constants.P8Constants"%>
		<%@ page errorPage="/error/errorpage.jsp" %>
		<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
		<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
		<%@taglib prefix="st" uri="/WEB-INF/tld/formbuilder-taglib.tld"%>
		<portlet:defineObjects/>
		<script type="text/javascript">
			var loadingBars = new Array();
			// This function is used to display error message 
			$(document).ready(function() {
			    if( 'null' !='<%=request.getAttribute("message2")%>')
				{
					$(".error2").html('<%=request.getAttribute("message2")%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('error2', this)\" />");
				 	$(".error2").addClass('failed');
					$(".error2").show();
				 }
			    applyCssToHomePagesTable("#assignedTaskTable");
			});
			// This function is used to display procurement counts
			function onAssignedCountRefresh()
			{
				$("#taskAssignedPortlet").loadingHome("Loading...");
				hhsAjaxRender(null, document.getElementById("homeassign"), "taskAssignedPortlet", document.getElementById("homeassign").action, "loadingCallBackFromAssigned");
			}
			// This function is used to remove waiting icon
			function loadingCallBackFromAssigned()
			{
				$("#taskAssignedPortlet").loadingHomeClose();
				applyCssToHomePagesTable("#assignedTaskTable");
			}
		</script>
		<%--resourceURL for events on Assigned Tasks Portlet--%>
		<portlet:resourceURL var='homeTaskAssignPortlet' id='homeTaskAssignPortlet' escapeXml='false'>
		</portlet:resourceURL>
		<!-- Body Wrapper Start -->
		<form id="homeassign" name="homeassign" action="${homeTaskAssignPortlet}" method ="post" >
			<div id="error2" class="clear error2" ></div>
				<jsp:useBean
					id="taskCountAssignedBean" class="com.nyc.hhs.model.TaskCount" scope="request">
				</jsp:useBean>
				
			<!-- Begin QC8914 R7.2.0 Oversight Role Hide Task Inbox-->
			<% 
			if(! CommonUtil.hideForOversightRole(request.getSession()))
			{%>	
				
				<div class="tabularWrapper portlet1Col homepageHHS">
					<div class="tabularCustomHead">Tasks assigned to Staff
				   		<a href="javascript:;" onclick="javascript:onAssignedCountRefresh()"><img src="../framework/skins/hhsa/images/refresh.png" class="tabularCustomHeadIcon" title="Refresh" alt="Refresh"/></a>
				   	</div>
					<table cellspacing="0" cellpadding="0"  class="grid" id="assignedTaskTable">    
						<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S028_PAGE, request.getSession())) {%>
			                <!-- added for R5  module Manage Organization-->
			                <tr>
								<c:choose>
									<c:when test="${taskCountAssignedBean.approvedPsrCount eq 0}">
				                    	<td><c:if test="${taskCountAssignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountAssignedBean.approvedPsrCount}</span>&nbsp;&nbsp;</c:if>Approve PSR Tasks</td>
				                    </c:when>
				                    <c:otherwise>
				                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_task&_nfls=false&app_menu_name=header_application&taskScreen=taskmanager&taskType=<%=P8Constants.PROPERTY_PE_TASK_TYPE_APPROVE_PSR%>&action=assign">${taskCountAssignedBean.approvedPsrCount}</a></span>&nbsp;&nbsp;Approve PSR Tasks</td>
				                    </c:otherwise>
					            </c:choose>
							</tr>  
			                
			                 <tr>
								<c:choose>
									<c:when test="${taskCountAssignedBean.awardApprovalAmountTaskCount eq 0}">
				                    	<td><c:if test="${taskCountAssignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountAssignedBean.awardApprovalAmountTaskCount}</span>&nbsp;&nbsp;</c:if>Approve Award Amount Tasks</td>
				                    </c:when>
				                    <c:otherwise>
				                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_task&_nfls=false&app_menu_name=header_application&taskScreen=taskmanager&taskType=<%=P8Constants.PROPERTY_PE_TASK_TYPE_AWARD_APPROVAL_AMOUNT%>&action=assign">${taskCountAssignedBean.awardApprovalAmountTaskCount}</a></span>&nbsp;&nbsp;Approve Award Amount Tasks</td>
				                    </c:otherwise>
					            </c:choose>
							<!-- added for R5  module Manage Organization-->
							</tr>  
			                
			                <tr>
								<c:choose>
									<c:when test="${taskCountAssignedBean.awardApprovalTaskCount eq 0}">
				                    	<td><c:if test="${taskCountAssignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountAssignedBean.awardApprovalTaskCount}</span>&nbsp;&nbsp;</c:if>Approve Award(s) Tasks</td>
				                    </c:when>
				                    <c:otherwise>
				                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_task&_nfls=false&app_menu_name=header_application&taskScreen=taskmanager&taskType=<%=P8Constants.PROPERTY_PE_TASK_TYPE_AWARD_APPROVAL%>&action=assign">${taskCountAssignedBean.awardApprovalTaskCount}</a></span>&nbsp;&nbsp;Approve Award(s) Tasks</td>
				                    </c:otherwise>
					            </c:choose>
							</tr>            
							<tr>
								<c:choose>
									<c:when test="${taskCountAssignedBean.msBRAppCount eq 0}">
										<td><c:if test="${taskCountAssignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountAssignedBean.msBRAppCount}</span>&nbsp;&nbsp;</c:if>Business Application Tasks</td>
									</c:when>
									 <c:otherwise>
				                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_task&_nfls=false&app_menu_name=header_application&taskScreen=taskmanager&taskType=<%=P8Constants.PROPERTY_PE_TASK_TYPE_BR_APPLICATION%>&action=assign">${taskCountAssignedBean.msBRAppCount}</a></span>&nbsp;&nbsp;Business Application Tasks</td>
				                    </c:otherwise>
			                    </c:choose>
			                </tr>
			                <tr>
			                	<c:choose>
									<c:when test="${taskCountAssignedBean.msSRAppCount eq 0}">
										<td><c:if test="${taskCountAssignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountAssignedBean.msSRAppCount}</span>&nbsp;&nbsp;</c:if>Service Application Tasks</td>
									</c:when>
									 <c:otherwise>
				                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_task&_nfls=false&app_menu_name=header_application&taskScreen=taskmanager&taskType=<%=P8Constants.PROPERTY_PE_TASK_TYPE_SERVICE_APPLICATION%>&action=assign">${taskCountAssignedBean.msSRAppCount}</a></span>&nbsp;&nbsp;Service Application Tasks</td>
				                    </c:otherwise>
			                    </c:choose>
			                </tr>
			                <tr>
			                	<c:choose>
									<c:when test="${taskCountAssignedBean.newFilingCount eq 0}">
										<td><c:if test="${taskCountAssignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountAssignedBean.newFilingCount}</span>&nbsp;&nbsp;</c:if>New Filings</td>
									</c:when>
									 <c:otherwise>
				                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_task&_nfls=false&app_menu_name=header_application&taskScreen=taskmanager&taskType=<%=P8Constants.PROPERTY_PE_TASK_TYPE_NEW_FILING%>&action=assign">${taskCountAssignedBean.newFilingCount}</a></span>&nbsp;&nbsp;New Filings</td>
				                    </c:otherwise>
			                    </c:choose>
							</tr>
							<!-- Start QC 9587 R 8.10.0 Remove Contact Us task -->
							<%--
			                <tr>
			                	<c:choose>
									<c:when test="${taskCountAssignedBean.msContactCount eq 0}">
										<td><c:if test="${taskCountAssignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountAssignedBean.msContactCount}</span>&nbsp;&nbsp;</c:if>Contact Us Tasks</td>
									</c:when>
									 <c:otherwise>
				                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_task&_nfls=false&app_menu_name=header_application&taskScreen=taskmanager&taskType=<%=P8Constants.PROPERTY_PE_TASK_TYPE_CONTACT_US%>&action=assign">${taskCountAssignedBean.msContactCount}</a></span>&nbsp;&nbsp;Contact Us Tasks</td>
				                    </c:otherwise>
			                    </c:choose>
							</tr>
							--%>
							<!-- End QC 9587 R 8.10.0 Remove Contact Us task -->
							<tr>
			                	<c:choose>
									<c:when test="${taskCountAssignedBean.provAccountReqCount eq 0}">
										<td><c:if test="${taskCountAssignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountAssignedBean.provAccountReqCount}</span>&nbsp;&nbsp;</c:if>Provider Account Request Tasks</td>
									</c:when>
									 <c:otherwise>
				                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_task&_nfls=false&app_menu_name=header_application&taskScreen=taskmanager&taskType=<%=P8Constants.PROPERTY_PE_TASK_TYPE_PROVIDER_ACCOUNT_REQUEST%>&action=assign">${taskCountAssignedBean.provAccountReqCount}</a></span>&nbsp;&nbsp;Provider Account Request Tasks</td>
				                    </c:otherwise>
			                    </c:choose>
							</tr>
							<tr>
			                	<c:choose>
									<c:when test="${taskCountAssignedBean.orgLegalNameTasksCount eq 0}">
										<td><c:if test="${taskCountAssignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountAssignedBean.orgLegalNameTasksCount}</span>&nbsp;&nbsp;</c:if>Organization Legal Name Update Tasks</td>
									</c:when>
									 <c:otherwise>
				                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_task&_nfls=false&app_menu_name=header_application&taskScreen=taskmanager&taskType=<%=P8Constants.PROPERTY_PE_TASK_TYPE_ORGANIZATION_LEGAL_NAME_UPDATE_REQUEST%>&action=assign">${taskCountAssignedBean.orgLegalNameTasksCount}</a></span>&nbsp;&nbsp;Organization Legal Name Update Tasks</td>
				                    </c:otherwise>
			                    </c:choose>
							</tr>
			                <tr>
			                	<c:choose>
									<c:when test="${taskCountAssignedBean.msBRAppWithdrawalCount eq 0}">
										<td><c:if test="${taskCountAssignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountAssignedBean.msBRAppWithdrawalCount}</span>&nbsp;&nbsp;</c:if>Withdrawal Request - Business Application Tasks</td>
									</c:when>
									 <c:otherwise>
				                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_task&_nfls=false&app_menu_name=header_application&taskScreen=taskmanager&taskType=<%=P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_BUSINESS_REVIEW_APPLICATION%>&action=assign">${taskCountAssignedBean.msBRAppWithdrawalCount}</a></span>&nbsp;&nbsp;Withdrawal Request - Business Application Tasks</td>
				                    </c:otherwise>
			                    </c:choose>
							</tr>
							<tr>
			                	<c:choose>
									<c:when test="${taskCountAssignedBean.msSRWithdrawalAppCount eq 0}">
										<td><c:if test="${taskCountAssignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountAssignedBean.msSRWithdrawalAppCount}</span>&nbsp;&nbsp;</c:if>Withdrawal Request - Service Application Tasks</td>
									</c:when>
									 <c:otherwise>
				                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_task&_nfls=false&app_menu_name=header_application&taskScreen=taskmanager&taskType=<%=P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_SERVICE_APPLICATION%>&action=assign">${taskCountAssignedBean.msSRWithdrawalAppCount}</a></span>&nbsp;&nbsp;Withdrawal Request - Service Application Tasks</td>
				                    </c:otherwise>
			                    </c:choose>
							</tr>
						<%}else if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S029_PAGE_6, request.getSession())) {%>
							<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_ACCO_MANAGER, request.getSession())) {%>
								<tr>
									<c:choose>
										<c:when test="${taskCountAssignedBean.acceptPropTaskCount eq 0}">
					                    	<td><c:if test="${taskCountAssignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountAssignedBean.acceptPropTaskCount}</span></c:if>&nbsp;&nbsp;Accept Proposal Tasks</td>
					                    </c:when>
					                    <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=P8Constants.TASK_ACCEPT_PROPOSAL%>&taskAction=assign">${taskCountAssignedBean.acceptPropTaskCount}</a></span>&nbsp;&nbsp;Accept Proposal Tasks</td>
					                    </c:otherwise>
						            </c:choose>
								</tr>
							<%} %>
							<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_ACCO_AGENCY, request.getSession())) {%>
								<c:if test="${role ne 'CFO'}">
									<tr>
										<c:choose>
											<c:when test="${taskCountAssignedBean.reviewScoresTaskCount eq 0}">
						                    	<td><c:if test="${taskCountAssignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountAssignedBean.reviewScoresTaskCount}</span></c:if>&nbsp;&nbsp;Review  Scores Tasks</td>
						                    </c:when>
						                    <c:otherwise>
						                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=P8Constants.TASK_REVIEW_SCORES%>&taskAction=assign">${taskCountAssignedBean.reviewScoresTaskCount}</a></span>&nbsp;&nbsp;Review  Scores Tasks</td>
						                    </c:otherwise>
							            </c:choose>
									</tr>
								</c:if>
							<%} %>
							<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_ACCO_MANAGER, request.getSession())) {%>
								<tr>
									<c:choose>
										<c:when test="${taskCountAssignedBean.awardDocTaskCount eq 0}">
					                    	<td><c:if test="${taskCountAssignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountAssignedBean.awardDocTaskCount}</span></c:if>&nbsp;&nbsp;Configure Award Documents Tasks</td>
					                    </c:when>
					                    <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=P8Constants.TASK_CONFIGURE_AWARDS_DOCUMENTS%>&taskAction=assign">${taskCountAssignedBean.awardDocTaskCount}</a></span>&nbsp;&nbsp;Configure Award Documents Tasks</td>
					                    </c:otherwise>
						            </c:choose>
								</tr>
							<%} %>
							<!-- added for R5  module Manage Organization-->
							<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.ACCO_AGENCY, request.getSession())) {%>
							<tr>
									<c:choose>
										<c:when test="${taskCountAssignedBean.completePsrCount eq 0}">
					                    	<td><c:if test="${taskCountAssignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountAssignedBean.completePsrCount}</span></c:if>&nbsp;&nbsp;Complete PSR Tasks</td>
					                    </c:when>
					                    <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=P8Constants.PROPERTY_PE_TASK_TYPE_COMPLETE_PSR%>&taskAction=assign">${taskCountAssignedBean.completePsrCount}</a></span>&nbsp;&nbsp;Complete PSR Tasks</td>
					                    </c:otherwise>
						            </c:choose>
				     		</tr>
							<tr>
									<c:choose>
										<c:when test="${taskCountAssignedBean.finalizeAwardAmountCount eq 0}">
					                    	<td><c:if test="${taskCountAssignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountAssignedBean.finalizeAwardAmountCount}</span></c:if>&nbsp;&nbsp;Finalize Award Amount Tasks</td>
					                    </c:when>
					                    <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=P8Constants.PROPERTY_PE_TASK_TYPE_FINALIZE_AWARD_AMOUNT%>&taskAction=assign">${taskCountAssignedBean.finalizeAwardAmountCount}</a></span>&nbsp;&nbsp;Finalize Award Amount Tasks</td>
					                    </c:otherwise>
						            </c:choose>
							</tr>
							<%} %>
						    <!-- added for R5  module Manage Organization-->
							<%-- Task added and order change as per release 2.7.0 enhancement :5678 --%> 
							<tr>
								<c:choose>
									<c:when test="${taskCountAssignedBean.procurementCofTaskCount eq 0}">
				                    	<td><c:if test="${taskCountAssignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountAssignedBean.procurementCofTaskCount}</span></c:if>&nbsp;&nbsp;Procurement Certification of Funds</td>
				                    </c:when>
				                    <c:otherwise>
				                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=HHSConstants.TASK_PROCUREMENT_COF%>&taskAction=assign">${taskCountAssignedBean.procurementCofTaskCount}</a></span>&nbsp;&nbsp;Procurement Certification of Funds</td>
				                    </c:otherwise>
					            </c:choose>
							</tr>
							<tr>
								<c:choose>
									<c:when test="${taskCountAssignedBean.contConfigTaskCount eq 0}">
				                    	<td><c:if test="${taskCountAssignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountAssignedBean.contConfigTaskCount}</span></c:if>&nbsp;&nbsp;Contract Configuration Tasks</td>
				                    </c:when>
				                    <c:otherwise>
				                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=P8Constants.TASK_CONTRACT_CONFIGURATION%>&taskAction=assign">${taskCountAssignedBean.contConfigTaskCount}</a></span>&nbsp;&nbsp;Contract Configuration Tasks</td>
				                    </c:otherwise>
					            </c:choose>
							</tr>
							<tr>
								<c:choose>
									<c:when test="${taskCountAssignedBean.certOfFundsTaskCount eq 0}">
				                    	<td><c:if test="${taskCountAssignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountAssignedBean.certOfFundsTaskCount}</span></c:if>&nbsp;&nbsp;Certification of Funds Tasks</td>
				                    </c:when>
				                    <c:otherwise>
				                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=P8Constants.TASK_CONTRACT_COF%>&taskAction=assign">${taskCountAssignedBean.certOfFundsTaskCount}</a></span>&nbsp;&nbsp;Certification of Funds Tasks</td>
				                    </c:otherwise>
					            </c:choose>
							</tr>
							
							<tr>
								<c:choose>
									<c:when test="${taskCountAssignedBean.contractConfigurationUpdateTaskCount eq 0}">
				                    	<td><c:if test="${taskCountAssignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountAssignedBean.contractConfigurationUpdateTaskCount}</span></c:if>&nbsp;&nbsp;Contract Configuration Update</td>
				                    </c:when>
				                    <c:otherwise>
				                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=HHSConstants.TASK_CONTRACT_UPDATE%>&taskAction=assign">${taskCountAssignedBean.contractConfigurationUpdateTaskCount}</a></span>&nbsp;&nbsp;Contract Configuration Update</td>
				                    </c:otherwise>
					            </c:choose>
							</tr>
							
							<tr>
								<c:choose>
									<c:when test="${taskCountAssignedBean.amendmentCofTaskCount eq 0}">
				                    	<td><c:if test="${taskCountAssignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountAssignedBean.amendmentCofTaskCount}</span></c:if>&nbsp;&nbsp;Amendment Certification of Funds</td>
				                    </c:when>
				                    <c:otherwise>
				                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=HHSConstants.TASK_AMENDMENT_COF%>&taskAction=assign">${taskCountAssignedBean.amendmentCofTaskCount}</a></span>&nbsp;&nbsp;Amendment Certification of Funds</td>
				                    </c:otherwise>
					            </c:choose>
							</tr>
							
							<tr>
								<c:choose>
									<c:when test="${taskCountAssignedBean.contractConfigurationAmendmentTaskCount eq 0}">
				                    	<td><c:if test="${taskCountAssignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountAssignedBean.contractConfigurationAmendmentTaskCount}</span></c:if>&nbsp;&nbsp;Contract Configuration Amendment</td>
				                    </c:when>
				                    <c:otherwise>
				                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=HHSConstants.TASK_AMENDMENT_CONFIGURATION%>&taskAction=assign">${taskCountAssignedBean.contractConfigurationAmendmentTaskCount}</a></span>&nbsp;&nbsp;Contract Configuration Amendment</td>
				                    </c:otherwise>
					            </c:choose>
							</tr>
							
							<tr>
								<c:choose>
									<c:when test="${taskCountAssignedBean.newFyConfigurationTaskCount eq 0}">
				                    	<td><c:if test="${taskCountAssignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountAssignedBean.newFyConfigurationTaskCount}</span></c:if>&nbsp;&nbsp;New Fiscal Year Configuration</td>
				                    </c:when>
				                    <c:otherwise>
				                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=HHSConstants.TASK_NEW_FY_CONFIGURATION%>&taskAction=assign">${taskCountAssignedBean.newFyConfigurationTaskCount}</a></span>&nbsp;&nbsp;New Fiscal Year Configuration</td>
				                    </c:otherwise>
					            </c:choose>
							</tr>
							
							<tr>
								<c:choose>
									<c:when test="${taskCountAssignedBean.budgetReviewTaskCount eq 0}">
				                    	<td><c:if test="${taskCountAssignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountAssignedBean.budgetReviewTaskCount}</span></c:if>&nbsp;&nbsp;Budget Review Tasks</td>
				                    </c:when>
				                    <c:otherwise>
				                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=P8Constants.TASK_BUDGET_REVIEW%>&taskAction=assign">${taskCountAssignedBean.budgetReviewTaskCount}</a></span>&nbsp;&nbsp;Budget Review Tasks</td>
				                    </c:otherwise>
					            </c:choose>
							</tr>
							
							<tr>
								<c:choose>
									<c:when test="${taskCountAssignedBean.contractBudgetModificationTaskCount eq 0}">
				                    	<td><c:if test="${taskCountAssignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountAssignedBean.contractBudgetModificationTaskCount}</span></c:if>&nbsp;&nbsp;Contract Budget Modification Review</td>
				                    </c:when>
				                    <c:otherwise>
				                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=HHSConstants.TASK_BUDGET_MODIFICATION%>&taskAction=assign">${taskCountAssignedBean.contractBudgetModificationTaskCount}</a></span>&nbsp;&nbsp;Contract Budget Modification Review</td>
				                    </c:otherwise>
					            </c:choose>
							</tr>
							
							<tr>
								<c:choose>
									<c:when test="${taskCountAssignedBean.contractBudgetAmendmentTaskCount eq 0}">
				                    	<td><c:if test="${taskCountAssignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountAssignedBean.contractBudgetAmendmentTaskCount}</span></c:if>&nbsp;&nbsp;Contract Budget Amendment Review</td>
				                    </c:when>
				                    <c:otherwise>
				                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=HHSConstants.TASK_BUDGET_AMENDMENT%>&taskAction=assign">${taskCountAssignedBean.contractBudgetAmendmentTaskCount}</a></span>&nbsp;&nbsp;Contract Budget Amendment Review</td>
				                    </c:otherwise>
					            </c:choose>
							</tr>
							
							<tr>
								<c:choose>
									<c:when test="${taskCountAssignedBean.contractBudgetUpdateTaskCount eq 0}">
				                    	<td><c:if test="${taskCountAssignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountAssignedBean.contractBudgetUpdateTaskCount}</span></c:if>&nbsp;&nbsp;Contract Budget Update Review</td>
				                    </c:when>
				                    <c:otherwise>
				                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=HHSConstants.TASK_BUDGET_UPDATE%>&taskAction=assign">${taskCountAssignedBean.contractBudgetUpdateTaskCount}</a></span>&nbsp;&nbsp;Contract Budget Update Review</td>
				                    </c:otherwise>
					            </c:choose>
							</tr>
						
							<tr>
								<c:choose>
									<c:when test="${taskCountAssignedBean.invoiceReviewTaskCount eq 0}">
				                    	<td><c:if test="${taskCountAssignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountAssignedBean.invoiceReviewTaskCount}</span></c:if>&nbsp;&nbsp;Invoice Review Tasks</td>
				                    </c:when>
				                    <c:otherwise>
				                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=P8Constants.TASK_INVOICE_REVIEW%>&taskAction=assign">${taskCountAssignedBean.invoiceReviewTaskCount}</a></span>&nbsp;&nbsp;Invoice Review Tasks</td>
				                    </c:otherwise>
					            </c:choose>
							</tr>
							
							<tr>
								<c:choose>
									<c:when test="${taskCountAssignedBean.paymentReviewTaskCount eq 0}">
				                    	<td><c:if test="${taskCountAssignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountAssignedBean.paymentReviewTaskCount}</span></c:if>&nbsp;&nbsp;Payment Review Tasks</td>
				                    </c:when>
				                    <c:otherwise>
				                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=P8Constants.TASK_PAYMENT_REVIEW%>&taskAction=assign">${taskCountAssignedBean.paymentReviewTaskCount}</a></span>&nbsp;&nbsp;Payment Review Tasks</td>
				                    </c:otherwise>
					            </c:choose>
							</tr>
							
							<tr>
								<c:choose>
									<c:when test="${taskCountAssignedBean.advancePaymentRequestTaskCount eq 0}">
				                    	<td><c:if test="${taskCountAssignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountAssignedBean.advancePaymentRequestTaskCount}</span></c:if>&nbsp;&nbsp;Advance Request Review</td>
				                    </c:when>
				                    <c:otherwise>
				                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=HHSConstants.TASK_ADVANCE_REVIEW%>&taskAction=assign">${taskCountAssignedBean.advancePaymentRequestTaskCount}</a></span>&nbsp;&nbsp;Advance Request Review</td>
				                    </c:otherwise>
					            </c:choose>
							</tr>
							<tr>
								<c:choose>
									<c:when test="${taskCountAssignedBean.advancePaymentReviewTaskCount eq 0}">
				                    	<td><c:if test="${taskCountAssignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountAssignedBean.advancePaymentReviewTaskCount}</span></c:if>&nbsp;&nbsp;Advance Payment Review</td>
				                    </c:when>
				                    <c:otherwise>
				                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=HHSConstants.TASK_ADVANCE_PAYMENT_REVIEW%>&taskAction=assign">${taskCountAssignedBean.advancePaymentReviewTaskCount}</a></span>&nbsp;&nbsp;Advance Payment Review</td>
				                    </c:otherwise>
					            </c:choose>
							</tr>
							<!-- Added for R6- Return Payment Review Task -->
							<tr>
								
								<c:choose>
									<c:when test="${taskCountAssignedBean.returnPaymentReviewTaskCount eq 0}">
				                    	<td><c:if test="${taskCountAssignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountAssignedBean.returnPaymentReviewTaskCount}</span></c:if>&nbsp;&nbsp;Returned Payment Review</td>
				                    </c:when>
				                    <c:otherwise>
				                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=HHSConstants.TASK_RETURN_PAYMENT_REVIEW%>&taskAction=assign">${taskCountAssignedBean.returnPaymentReviewTaskCount}</a></span>&nbsp;&nbsp;Returned Payment Review</td>
				                    </c:otherwise>
					            </c:choose>
							</tr>
							<!-- Added for R6- Return Payment Review Task end-->
						<%}else{ %>
 							<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
 						<%} %>
					</table>
				</div>
				
			<%} %>
			<!-- End QC8914 R7.2.0 Oversight Hide Task Inbox-->
		</form>	
	</div>
</div>	