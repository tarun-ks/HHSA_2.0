<%-- This JSP is for Tasks in my Inbox portlet Count--%>
<div>
	<div id="inboxPortlet">
		<%@page language="java" contentType="text/html;charset=UTF-8"%>
		<%@page import="com.nyc.hhs.service.filenetmanager.p8constants.P8Constants"%>
		<%@ page errorPage="/error/errorpage.jsp" %>
		<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
		<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
		<%@taglib prefix="st" uri="/WEB-INF/tld/formbuilder-taglib.tld"%>
		<%@page import="java.util.ArrayList, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant, com.nyc.hhs.constants.HHSConstants"%>
		<portlet:defineObjects/>
		<script type="text/javascript">
			var loadingBars = new Array();
			// This function is used to display error message 
			$(document).ready(function() {
			    if( 'null' !='<%=request.getAttribute("message")%>')
				{
					$(".error").html('<%=request.getAttribute("message")%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('error', this)\" />");
				 	$(".error").addClass('failed');
					$(".error").show();
				}
			    applyCssToHomePagesTable("#inboxTaskTable");
			});
			// This function is used to display procurement counts
			function onCountRefresh()
			{
				$("#inboxPortlet").loadingHome("Loading...");
				hhsAjaxRender(null, document.getElementById("homeinbox"), "inboxPortlet", document.getElementById("homeinbox").action, "loadingCallBackFromInbox");
			}
			// This function is used to remove waiting icon
			function loadingCallBackFromInbox()
			{
				$("#inboxPortlet").loadingHomeClose();
				applyCssToHomePagesTable("#inboxTaskTable");
			}
		</script>
		<%--resourceURL for events on Task Inbox Portlet--%>
		<portlet:resourceURL var='homeInboxPortlet' id='homeInboxPortlet' escapeXml='false'>
		</portlet:resourceURL>
		<!-- Body Wrapper Start -->
		<form id="homeinbox" name="homeinbox" action="${homeInboxPortlet}" method ="post" >
			<div id="error" class="clear error" ></div>
				<jsp:useBean
				id="taskCountBean" class="com.nyc.hhs.model.TaskCount" scope="request">
				</jsp:useBean>
				
				<!-- Begin QC8914 R7.2.0 Oversight Role Hide Task Inbox-->
				<% 
				if(! CommonUtil.hideForOversightRole(request.getSession()))
				{%>
					<div class="tabularWrapper portlet1Col homepageHHS">
			  			<div class="tabularCustomHead">Tasks in My Inbox
			   			<a href="javascript:;" onclick="javascript:onCountRefresh()"><img src="../framework/skins/hhsa/images/refresh.png" class="tabularCustomHeadIcon" title="Refresh" alt="Refresh"/></a></div>
						<table cellspacing="0" cellpadding="0"  class="grid" id="inboxTaskTable">  
							<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S028_PAGE, request.getSession()) ) {%>
				             <!-- added for R5  module Manage Organization-->
				             <tr>
									<c:choose>
										<c:when test="${taskCountBean.approvedPsrCount eq 0}">
					                    	<td><c:if test="${taskCountBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountBean.approvedPsrCount}</span>&nbsp;&nbsp;</c:if>Approve PSR Tasks</td>
					                    </c:when>
					                    <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_task&_nfls=false&app_menu_name=header_application&taskScreen=inbox&taskType=<%=P8Constants.PROPERTY_PE_TASK_TYPE_APPROVE_PSR%>&action=taskhome">${taskCountBean.approvedPsrCount}</a></span>&nbsp;&nbsp;Approve PSR Tasks</td>
					                    </c:otherwise>
						            </c:choose>
								</tr>  
				             
				                <tr>
									<c:choose>
										<c:when test="${taskCountBean.awardApprovalAmountTaskCount eq 0}">
					                    	<td><c:if test="${taskCountBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountBean.awardApprovalAmountTaskCount}</span>&nbsp;&nbsp;</c:if>Approve Award Amount Tasks</td>
					                    </c:when>
					                    <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_task&_nfls=false&app_menu_name=header_application&taskScreen=inbox&taskType=<%=P8Constants.PROPERTY_PE_TASK_TYPE_AWARD_APPROVAL_AMOUNT%>&action=taskhome">${taskCountBean.awardApprovalAmountTaskCount}</a></span>&nbsp;&nbsp;Approve Award Amount Tasks</td>
					                    </c:otherwise>
						            </c:choose>
								</tr>  
								<!-- added for R5  module Manage Organization-->
				                 <tr>
									<c:choose>
										<c:when test="${taskCountBean.awardApprovalTaskCount eq 0}">
					                    	<td><c:if test="${taskCountBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountBean.awardApprovalTaskCount}</span>&nbsp;&nbsp;</c:if>Approve Award(s) Tasks</td>
					                    </c:when>
					                    <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_task&_nfls=false&app_menu_name=header_application&taskScreen=inbox&taskType=<%=P8Constants.PROPERTY_PE_TASK_TYPE_AWARD_APPROVAL%>&action=taskhome">${taskCountBean.awardApprovalTaskCount}</a></span>&nbsp;&nbsp;Approve Award(s) Tasks</td>
					                    </c:otherwise>
						            </c:choose>
								</tr>            
								<tr>
									<c:choose>
										<c:when test="${taskCountBean.msBRAppCount eq 0}">
											<td><c:if test="${taskCountBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountBean.msBRAppCount}</span>&nbsp;&nbsp;</c:if>Business Application Tasks</td>
										</c:when>
										 <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_task&_nfls=false&app_menu_name=header_application&taskScreen=inbox&taskType=<%=P8Constants.PROPERTY_PE_TASK_TYPE_BR_APPLICATION%>&action=taskhome">${taskCountBean.msBRAppCount}</a></span>&nbsp;&nbsp;Business Application Tasks</td>
					                    </c:otherwise>
				                    </c:choose>
				                </tr>
				                <tr>
				                	<c:choose>
										<c:when test="${taskCountBean.msSRAppCount eq 0}">
											<td><c:if test="${taskCountBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountBean.msSRAppCount}</span>&nbsp;&nbsp;</c:if>Service Application Tasks</td>
										</c:when>
										 <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_task&_nfls=false&app_menu_name=header_application&taskScreen=inbox&taskType=<%=P8Constants.PROPERTY_PE_TASK_TYPE_SERVICE_APPLICATION%>&action=taskhome">${taskCountBean.msSRAppCount}</a></span>&nbsp;&nbsp;Service Application Tasks</td>
					                    </c:otherwise>
				                    </c:choose>
				                </tr>
				                <tr>
				                	<c:choose>
										<c:when test="${taskCountBean.newFilingCount eq 0}">
											<td><c:if test="${taskCountBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountBean.newFilingCount}</span>&nbsp;&nbsp;</c:if>New Filings</td>
										</c:when>
										 <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_task&_nfls=false&app_menu_name=header_application&taskScreen=inbox&taskType=<%=P8Constants.PROPERTY_PE_TASK_TYPE_NEW_FILING%>&action=taskhome">${taskCountBean.newFilingCount}</a></span>&nbsp;&nbsp;New Filings</td>
					                    </c:otherwise>
				                    </c:choose>
								</tr>
							<!-- Start QC 9587 R 8.10.0 Remove Contact Us task -->
							<%--
								<tr>
				                	<c:choose>
										<c:when test="${taskCountBean.msContactCount eq 0}">
											<td><c:if test="${taskCountBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountBean.msContactCount}</span>&nbsp;&nbsp;</c:if>Contact Us Tasks</td>
										</c:when>
										 <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_task&_nfls=false&app_menu_name=header_application&taskScreen=inbox&taskType=<%=P8Constants.PROPERTY_PE_TASK_TYPE_CONTACT_US%>&action=taskhome">${taskCountBean.msContactCount}</a></span>&nbsp;&nbsp;Contact Us Tasks</td>
					                    </c:otherwise>
				                    </c:choose>
								</tr>
								--%>
								<!-- End QC 9587 R 8.10.0 Remove Contact Us task -->
								<tr>
				                	<c:choose>
										<c:when test="${taskCountBean.provAccountReqCount eq 0}">
											<td><c:if test="${taskCountBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountBean.provAccountReqCount}</span>&nbsp;&nbsp;</c:if>Provider Account Request Tasks</td>
										</c:when>
										 <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_task&_nfls=false&app_menu_name=header_application&taskScreen=inbox&taskType=<%=P8Constants.PROPERTY_PE_TASK_TYPE_PROVIDER_ACCOUNT_REQUEST%>&action=taskhome">${taskCountBean.provAccountReqCount}</a></span>&nbsp;&nbsp;Provider Account Request Tasks</td>
					                    </c:otherwise>
				                    </c:choose>
								</tr>
								<tr>
				                	<c:choose>
										<c:when test="${taskCountBean.orgLegalNameTasksCount eq 0}">
											<td><c:if test="${taskCountBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountBean.orgLegalNameTasksCount}</span>&nbsp;&nbsp;</c:if>Organization Legal Name Update Tasks</td>
										</c:when>
										 <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_task&_nfls=false&app_menu_name=header_application&taskScreen=inbox&taskType=<%=P8Constants.PROPERTY_PE_TASK_TYPE_ORGANIZATION_LEGAL_NAME_UPDATE_REQUEST%>&action=taskhome">${taskCountBean.orgLegalNameTasksCount}</a></span>&nbsp;&nbsp;Organization Legal Name Update Tasks</td>
					                    </c:otherwise>
				                    </c:choose>
								</tr>
				                <tr>
				                	<c:choose>
										<c:when test="${taskCountBean.msBRAppWithdrawalCount eq 0}">
											<td><c:if test="${taskCountBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountBean.msBRAppWithdrawalCount}</span>&nbsp;&nbsp;</c:if>Withdrawal Request - Business Application Tasks</td>
										</c:when>
										 <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_task&_nfls=false&app_menu_name=header_application&taskScreen=inbox&taskType=<%=P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_BUSINESS_REVIEW_APPLICATION%>&action=taskhome">${taskCountBean.msBRAppWithdrawalCount}</a></span>&nbsp;&nbsp;Withdrawal Request - Business Application Tasks</td>
					                    </c:otherwise>
				                    </c:choose>
								</tr>
								<tr>
				                	<c:choose>
										<c:when test="${taskCountBean.msSRWithdrawalAppCount eq 0}">
											<td><c:if test="${taskCountBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountBean.msSRWithdrawalAppCount}</span>&nbsp;&nbsp;</c:if>Withdrawal Request - Service Application Tasks</td>
										</c:when>
										 <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_task&_nfls=false&app_menu_name=header_application&taskScreen=inbox&taskType=<%=P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_SERVICE_APPLICATION%>&action=taskhome">${taskCountBean.msSRWithdrawalAppCount}</a></span>&nbsp;&nbsp;Withdrawal Request - Service Application Tasks</td>
					                    </c:otherwise>
				                    </c:choose>
								</tr>
							<%}else if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S029_PAGE_6, request.getSession()) ) {%>
								<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_ACCO_MANAGER, request.getSession())) {%>
									<tr>
										<c:choose>
											<c:when test="${taskCountBean.acceptPropTaskCount eq 0}">
						                    	<td><c:if test="${taskCountBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountBean.acceptPropTaskCount}</span></c:if>&nbsp;&nbsp;Accept Proposal Tasks</td>
						                    </c:when>
						                    <c:otherwise>
						                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=inbox&taskType=<%=P8Constants.TASK_ACCEPT_PROPOSAL%>&taskAction=taskhome">${taskCountBean.acceptPropTaskCount}</a></span>&nbsp;&nbsp;Accept Proposal Tasks</td>
						                    </c:otherwise>
							            </c:choose>
									</tr>
								<%} %>
								<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_ACCO_AGENCY, request.getSession())) {%>
									<c:if test="${role ne 'CFO'}">
										<tr>
											<c:choose>
												<c:when test="${taskCountBean.reviewScoresTaskCount eq 0}">
							                    	<td><c:if test="${taskCountBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountBean.reviewScoresTaskCount}</span></c:if>&nbsp;&nbsp;Review  Scores Tasks</td>
							                    </c:when>
							                    <c:otherwise>
							                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=inbox&taskType=<%=P8Constants.TASK_REVIEW_SCORES%>&taskAction=taskhome">${taskCountBean.reviewScoresTaskCount}</a></span>&nbsp;&nbsp;Review  Scores Tasks</td>
							                    </c:otherwise>
								            </c:choose>
										</tr>
									</c:if>
								<%} %>
								<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_ACCO_MANAGER, request.getSession())) {%>
									<tr>
										<c:choose>
											<c:when test="${taskCountBean.awardDocTaskCount eq 0}">
						                    	<td><c:if test="${taskCountBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountBean.awardDocTaskCount}</span></c:if>&nbsp;&nbsp;Configure Award Documents Tasks</td>
						                    </c:when>
						                    <c:otherwise>
						                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=inbox&taskType=<%=P8Constants.TASK_CONFIGURE_AWARDS_DOCUMENTS%>&taskAction=taskhome">${taskCountBean.awardDocTaskCount}</a></span>&nbsp;&nbsp;Configure Award Documents Tasks</td>
						                    </c:otherwise>
							            </c:choose>
									</tr>
								<%} %>
							   <!-- added for R5  module Manage Organization-->
							   <%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.ACCO_AGENCY, request.getSession())) {%>
								<tr>
										<c:choose>
											<c:when test="${taskCountBean.completePsrCount eq 0}">
						                    	<td><c:if test="${taskCountBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountBean.completePsrCount}</span></c:if>&nbsp;&nbsp;Complete PSR Tasks</td>
						                    </c:when>
						                    <c:otherwise>
						                     	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=inbox&taskType=<%=P8Constants.PROPERTY_PE_TASK_TYPE_COMPLETE_PSR%>&taskAction=taskhome">${taskCountBean.completePsrCount}</a></span>&nbsp;&nbsp;Complete PSR Tasks</td>
						                    </c:otherwise>
							            </c:choose>
					     		</tr>
								<tr>
										<c:choose>
											<c:when test="${taskCountBean.finalizeAwardAmountCount eq 0}">
						                    	<td><c:if test="${taskCountBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountBean.finalizeAwardAmountCount}</span></c:if>&nbsp;&nbsp;Finalize Award Amount Tasks</td>
						                    </c:when>
						                    <c:otherwise>
						                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=inbox&taskType=<%=P8Constants.PROPERTY_PE_TASK_TYPE_FINALIZE_AWARD_AMOUNT%>&taskAction=taskhome">${taskCountBean.finalizeAwardAmountCount}</a></span>&nbsp;&nbsp;Finalize Award Amount Tasks</td>
						                    </c:otherwise>
							            </c:choose>
								</tr>
								<%}%>
							    <!--  added for R5  module Manage Organization-->
								<tr>
									<c:choose>
										<c:when test="${taskCountBean.evaluatePropTaskCount eq 0}">
					                    	<td><c:if test="${taskCountBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountBean.evaluatePropTaskCount}</span></c:if>&nbsp;&nbsp;Evaluate Proposal Tasks</td>
					                    </c:when>
					                    <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=inbox&taskType=<%=P8Constants.TASK_EVALUATE_PROPOSAL%>&taskAction=taskhome">${taskCountBean.evaluatePropTaskCount}</a></span>&nbsp;&nbsp;Evaluate Proposal Tasks</td>
					                    </c:otherwise>
						            </c:choose>
								</tr>
								<%-- Task added and order change as per release 2.7.0 enhancement :5678 --%> 
							
								<tr>
									<c:choose>
										<c:when test="${taskCountBean.procurementCofTaskCount eq 0}">
					                    	<td><c:if test="${taskCountBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountBean.procurementCofTaskCount}</span></c:if>&nbsp;&nbsp;Procurement Certification of Funds</td>
					                    </c:when>
					                    <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=inbox&taskType=<%=HHSConstants.TASK_PROCUREMENT_COF%>&taskAction=taskhome">${taskCountBean.procurementCofTaskCount}</a></span>&nbsp;&nbsp;Procurement Certification of Funds</td>
					                    </c:otherwise>
						            </c:choose>
								</tr>
								<tr>
									<c:choose>
										<c:when test="${taskCountBean.contConfigTaskCount eq 0}">
					                    	<td><c:if test="${taskCountBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountBean.contConfigTaskCount}</span></c:if>&nbsp;&nbsp;Contract Configuration Tasks</td>
					                    </c:when>
					                    <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=inbox&taskType=<%=P8Constants.TASK_CONTRACT_CONFIGURATION%>&taskAction=taskhome">${taskCountBean.contConfigTaskCount}</a></span>&nbsp;&nbsp;Contract Configuration Tasks</td>
					                    </c:otherwise>
						            </c:choose>
								</tr>
								<tr>
									<c:choose>
										<c:when test="${taskCountBean.certOfFundsTaskCount eq 0}">
					                    	<td><c:if test="${taskCountBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountBean.certOfFundsTaskCount}</span></c:if>&nbsp;&nbsp;Certification of Funds Tasks</td>
					                    </c:when>
					                    <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=inbox&taskType=<%=P8Constants.TASK_CONTRACT_COF%>&taskAction=taskhome">${taskCountBean.certOfFundsTaskCount}</a></span>&nbsp;&nbsp;Certification of Funds Tasks</td>
					                    </c:otherwise>
						            </c:choose>
								</tr>
								
								<tr>
									<c:choose>
										<c:when test="${taskCountBean.contractConfigurationUpdateTaskCount eq 0}">
					                    	<td><c:if test="${taskCountBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountBean.contractConfigurationUpdateTaskCount}</span></c:if>&nbsp;&nbsp;Contract Configuration Update</td>
					                    </c:when>
					                    <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=inbox&taskType=<%=HHSConstants.TASK_CONTRACT_UPDATE%>&taskAction=taskhome">${taskCountBean.contractConfigurationUpdateTaskCount}</a></span>&nbsp;&nbsp;Contract Configuration Update</td>
					                    </c:otherwise>
						            </c:choose>
								</tr>
								
								<tr>
									<c:choose>
										<c:when test="${taskCountBean.amendmentCofTaskCount eq 0}">
					                    	<td><c:if test="${taskCountBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountBean.amendmentCofTaskCount}</span></c:if>&nbsp;&nbsp;Amendment Certification of Funds</td>
					                    </c:when>
					                    <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=inbox&taskType=<%=HHSConstants.TASK_AMENDMENT_COF%>&taskAction=taskhome">${taskCountBean.amendmentCofTaskCount}</a></span>&nbsp;&nbsp;Amendment Certification of Funds</td>
					                    </c:otherwise>
						            </c:choose>
								</tr>
								
								<tr>
									<c:choose>
										<c:when test="${taskCountBean.contractConfigurationAmendmentTaskCount eq 0}">
					                    	<td><c:if test="${taskCountBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountBean.contractConfigurationAmendmentTaskCount}</span></c:if>&nbsp;&nbsp;Contract Configuration Amendment</td>
					                    </c:when>
					                    <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=inbox&taskType=<%=HHSConstants.TASK_AMENDMENT_CONFIGURATION%>&taskAction=taskhome">${taskCountBean.contractConfigurationAmendmentTaskCount}</a></span>&nbsp;&nbsp;Contract Configuration Amendment</td>
					                    </c:otherwise>
						            </c:choose>
								</tr>
								
								<tr>
									<c:choose>
										<c:when test="${taskCountBean.newFyConfigurationTaskCount eq 0}">
					                    	<td><c:if test="${taskCountBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountBean.newFyConfigurationTaskCount}</span></c:if>&nbsp;&nbsp;New Fiscal Year Configuration</td>
					                    </c:when>
					                    <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=inbox&taskType=<%=HHSConstants.TASK_NEW_FY_CONFIGURATION%>&taskAction=taskhome">${taskCountBean.newFyConfigurationTaskCount}</a></span>&nbsp;&nbsp;New Fiscal Year Configuration</td>
					                    </c:otherwise>
						            </c:choose>
								</tr>
								
								<tr>
									<c:choose>
										<c:when test="${taskCountBean.budgetReviewTaskCount eq 0}">
					                    	<td><c:if test="${taskCountBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountBean.budgetReviewTaskCount}</span></c:if>&nbsp;&nbsp;Budget Review Tasks</td>
					                    </c:when>
					                    <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=inbox&taskType=<%=P8Constants.TASK_BUDGET_REVIEW%>&taskAction=taskhome">${taskCountBean.budgetReviewTaskCount}</a></span>&nbsp;&nbsp;Budget Review Tasks</td>
					                    </c:otherwise>
						            </c:choose>
								</tr>
								
								<tr>
									<c:choose>
										<c:when test="${taskCountBean.contractBudgetModificationTaskCount eq 0}">
					                    	<td><c:if test="${taskCountBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountBean.contractBudgetModificationTaskCount}</span></c:if>&nbsp;&nbsp;Contract Budget Modification Review</td>
					                    </c:when>
					                    <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=inbox&taskType=<%=HHSConstants.TASK_BUDGET_MODIFICATION%>&taskAction=taskhome">${taskCountBean.contractBudgetModificationTaskCount}</a></span>&nbsp;&nbsp;Contract Budget Modification Review</td>
					                    </c:otherwise>
						            </c:choose>
								</tr>
								
								<tr>
									<c:choose>
										<c:when test="${taskCountBean.contractBudgetAmendmentTaskCount eq 0}">
					                    	<td><c:if test="${taskCountBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountBean.contractBudgetAmendmentTaskCount}</span></c:if>&nbsp;&nbsp;Contract Budget Amendment Review</td>
					                    </c:when>
					                    <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=inbox&taskType=<%=HHSConstants.TASK_BUDGET_AMENDMENT%>&taskAction=taskhome">${taskCountBean.contractBudgetAmendmentTaskCount}</a></span>&nbsp;&nbsp;Contract Budget Amendment Review</td>
					                    </c:otherwise>
						            </c:choose>
								</tr>
								
								<tr>
									<c:choose>
										<c:when test="${taskCountBean.contractBudgetUpdateTaskCount eq 0}">
					                    	<td><c:if test="${taskCountBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountBean.contractBudgetUpdateTaskCount}</span></c:if>&nbsp;&nbsp;Contract Budget Update Review</td>
					                    </c:when>
					                    <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=inbox&taskType=<%=HHSConstants.TASK_BUDGET_UPDATE%>&taskAction=taskhome">${taskCountBean.contractBudgetUpdateTaskCount}</a></span>&nbsp;&nbsp;Contract Budget Update Review</td>
					                    </c:otherwise>
						            </c:choose>
								</tr>
							
								<tr>
									<c:choose>
										<c:when test="${taskCountBean.invoiceReviewTaskCount eq 0}">
					                    	<td><c:if test="${taskCountBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountBean.invoiceReviewTaskCount}</span></c:if>&nbsp;&nbsp;Invoice Review Tasks</td>
					                    </c:when>
					                    <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=inbox&taskType=<%=P8Constants.TASK_INVOICE_REVIEW%>&taskAction=taskhome">${taskCountBean.invoiceReviewTaskCount}</a></span>&nbsp;&nbsp;Invoice Review Tasks</td>
					                    </c:otherwise>
						            </c:choose>
								</tr>
								
								<tr>
									<c:choose>
										<c:when test="${taskCountBean.paymentReviewTaskCount eq 0}">
					                    	<td><c:if test="${taskCountBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountBean.paymentReviewTaskCount}</span></c:if>&nbsp;&nbsp;Payment Review Tasks</td>
					                    </c:when>
					                    <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=inbox&taskType=<%=P8Constants.TASK_PAYMENT_REVIEW%>&taskAction=taskhome">${taskCountBean.paymentReviewTaskCount}</a></span>&nbsp;&nbsp;Payment Review Tasks</td>
					                    </c:otherwise>
						            </c:choose>
								</tr>
								
								<tr>
									<c:choose>
										<c:when test="${taskCountBean.advancePaymentRequestTaskCount eq 0}">
					                    	<td><c:if test="${taskCountBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountBean.advancePaymentRequestTaskCount}</span></c:if>&nbsp;&nbsp;Advance Request Review</td>
					                    </c:when>
					                    <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=inbox&taskType=<%=HHSConstants.TASK_ADVANCE_REVIEW%>&taskAction=taskhome">${taskCountBean.advancePaymentRequestTaskCount}</a></span>&nbsp;&nbsp;Advance Request Review</td>
					                    </c:otherwise>
						            </c:choose>
								</tr>
								<tr>
									<c:choose>
										<c:when test="${taskCountBean.advancePaymentReviewTaskCount eq 0}">
					                    	<td><c:if test="${taskCountBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountBean.advancePaymentReviewTaskCount}</span></c:if>&nbsp;&nbsp;Advance Payment Review</td>
					                    </c:when>
					                    <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=inbox&taskType=<%=HHSConstants.TASK_ADVANCE_PAYMENT_REVIEW%>&taskAction=taskhome">${taskCountBean.advancePaymentReviewTaskCount}</a></span>&nbsp;&nbsp;Advance Payment Review</td>
					                    </c:otherwise>
						            </c:choose>
								</tr>
								<!-- Added for R6- Return Payment Review Task -->
								<tr>
									<c:choose>
										<c:when test="${taskCountBean.returnPaymentReviewTaskCount eq 0}">
					                    	<td><c:if test="${taskCountBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountBean.returnPaymentReviewTaskCount}</span></c:if>&nbsp;&nbsp;Returned Payment Review</td>
					                    </c:when>
					                    <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=inbox&taskType=<%=HHSConstants.TASK_RETURN_PAYMENT_REVIEW%>&taskAction=taskhome">${taskCountBean.returnPaymentReviewTaskCount}</a></span>&nbsp;&nbsp;Returned Payment Review</td>
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
