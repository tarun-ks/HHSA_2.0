<%-- This JSP is for Unassigned tasks portlet Count--%>
<div>
	<div id="taskUnassignedPortlet">
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
				if( 'null' !='<%=request.getAttribute("message1")%>')
				{
				 	$(".error1").html('<%=request.getAttribute("message1")%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('error1', this)\" />");
				 	$(".error1").addClass('failed');
					$(".error1").show();
				}
				applyCssToHomePagesTable("#unassignedTaskTable");
			});
			// This function is used to display procurement counts
			function onUnassignedCountRefresh()
			{
				$("#taskUnassignedPortlet").loadingHome("Loading...");
				hhsAjaxRender(null, document.getElementById("homeunassign"), "taskUnassignedPortlet", document.getElementById("homeunassign").action, "loadingCallBackFromUnassigned");
			}
			// This function is used to remove waiting icon
			function loadingCallBackFromUnassigned()
			{
				$("#taskUnassignedPortlet").loadingHomeClose();
				applyCssToHomePagesTable("#unassignedTaskTable");
			}
		</script>
		<%--resourceURL for events on Unassigned Tasks Portlet--%>
		<portlet:resourceURL var='homeTaskUnassignPortlet' id='homeTaskUnassignPortlet' escapeXml='false'>
		</portlet:resourceURL>
		<!-- Body Wrapper Start -->
		<form name ="homeunassign" id="homeunassign" action="${homeTaskUnassignPortlet}" method ="post">
		<div id="error1" class="clear error1"></div>
			<jsp:useBean id="taskCountUnsassignedBean" class="com.nyc.hhs.model.TaskCount" scope="request"></jsp:useBean>
				<!-- Begin QC8914 R7.2.0Oversight Role Hide Task Inbox-->
				<% 
				if(! CommonUtil.hideForOversightRole(request.getSession()))
				{%>
			
					<div class="tabularWrapper portlet1Col homepageHHS">
					  	<div class="tabularCustomHead">Unassigned Tasks
					   		<a href="javascript:;" onclick="javascript:onUnassignedCountRefresh()"><img src="../framework/skins/hhsa/images/refresh.png" class="tabularCustomHeadIcon" title="Refresh" alt="Refresh"/></a>
					   	</div>
					    <table cellspacing="0" cellpadding="0"  class="grid" id="unassignedTaskTable">
					    	<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S028_PAGE, request.getSession())) {%>
					    		<!-- added for R5  module Manage Organization-->
					    		<tr>
									<c:choose>
										<c:when test="${taskCountUnsassignedBean.approvedPsrCount eq 0}">
					                    	<td><c:if test="${taskCountUnsassignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountUnsassignedBean.approvedPsrCount}</span>&nbsp;&nbsp;</c:if>Approve PSR Tasks</td>
					                    </c:when>
					                    <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_task&_nfls=false&app_menu_name=header_application&taskScreen=taskmanager&taskType=<%=P8Constants.PROPERTY_PE_TASK_TYPE_APPROVE_PSR%>&action=Unassigned">${taskCountUnsassignedBean.approvedPsrCount}</a></span>&nbsp;&nbsp;Approve PSR Tasks</td>
					                    </c:otherwise>
						            </c:choose>
								</tr>   
								
								<tr>
									<c:choose>
										<c:when test="${taskCountUnsassignedBean.awardApprovalAmountTaskCount eq 0}">
					                    	<td><c:if test="${taskCountUnsassignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountUnsassignedBean.awardApprovalAmountTaskCount}</span>&nbsp;&nbsp;</c:if>Approve Award Amount Tasks</td>
					                    </c:when>
					                    <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_task&_nfls=false&app_menu_name=header_application&taskScreen=taskmanager&taskType=<%=P8Constants.PROPERTY_PE_TASK_TYPE_AWARD_APPROVAL_AMOUNT%>&action=Unassigned">${taskCountUnsassignedBean.awardApprovalAmountTaskCount}</a></span>&nbsp;&nbsp;Approve Award Amount Tasks</td>
					                    </c:otherwise>
						            </c:choose>
								</tr>   
					    		<!-- added for R5  module Manage Organization--> 
					                
					    		<tr>
									<c:choose>
										<c:when test="${taskCountUnsassignedBean.awardApprovalTaskCount eq 0}">
					                    	<td><c:if test="${taskCountUnsassignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountUnsassignedBean.awardApprovalTaskCount}</span>&nbsp;&nbsp;</c:if>Approve Award(s) Tasks</td>
					                    </c:when>
					                    <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_task&_nfls=false&app_menu_name=header_application&taskScreen=taskmanager&taskType=<%=P8Constants.PROPERTY_PE_TASK_TYPE_AWARD_APPROVAL%>&action=unassign">${taskCountUnsassignedBean.awardApprovalTaskCount}</a></span>&nbsp;&nbsp;Approve Award(s) Tasks</td>
					                    </c:otherwise>
						            </c:choose>
								</tr>            
								<tr>
									<c:choose>
										<c:when test="${taskCountUnsassignedBean.msBRAppCount eq 0}">
											<td><c:if test="${taskCountUnsassignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountUnsassignedBean.msBRAppCount}</span>&nbsp;&nbsp;</c:if>Business Application Tasks (Unassigned Staff)</td>
										</c:when>
										 <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_task&_nfls=false&app_menu_name=header_application&taskScreen=taskmanager&taskType=<%=P8Constants.PROPERTY_PE_TASK_TYPE_BR_APPLICATION%>&action=Unassigned">${taskCountUnsassignedBean.msBRAppCount}</a></span>&nbsp;&nbsp;Business Application Tasks (Unassigned Staff)</td>
					                    </c:otherwise>
				                    </c:choose>
				                </tr>              
								<tr>
									<c:choose>
										<c:when test="${taskCountUnsassignedBean.brAppMgrTaskCount eq 0}">
											<td><c:if test="${taskCountUnsassignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountUnsassignedBean.brAppMgrTaskCount}</span>&nbsp;&nbsp;</c:if>Business Application Tasks (Unassigned Manager)</td>
										</c:when>
										 <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_task&_nfls=false&app_menu_name=header_application&taskScreen=taskmanager&taskType=<%=P8Constants.PROPERTY_PE_TASK_TYPE_BR_APPLICATION%>&action=unassign">${taskCountUnsassignedBean.brAppMgrTaskCount}</a></span>&nbsp;&nbsp;Business Application Tasks (Unassigned Manager)</td>
					                    </c:otherwise>
				                    </c:choose>
								</tr>
						        <tr>
						        	<c:choose>
										<c:when test="${taskCountUnsassignedBean.msSRAppCount eq 0}">
											<td><c:if test="${taskCountUnsassignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountUnsassignedBean.msSRAppCount}</span>&nbsp;&nbsp;</c:if>Service Application Tasks (Unassigned Staff)</td>
										</c:when>
										 <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_task&_nfls=false&app_menu_name=header_application&taskScreen=taskmanager&taskType=<%=P8Constants.PROPERTY_PE_TASK_TYPE_SERVICE_APPLICATION%>&action=Unassigned">${taskCountUnsassignedBean.msSRAppCount}</a></span>&nbsp;&nbsp;Service Application Tasks (Unassigned Staff)</td>
					                    </c:otherwise>
				                    </c:choose>
								</tr>
								<tr>
									<c:choose>
										<c:when test="${taskCountUnsassignedBean.srAppMgrTaskCount eq 0}">
											<td><c:if test="${taskCountUnsassignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountUnsassignedBean.srAppMgrTaskCount}</span>&nbsp;&nbsp;</c:if>Service Application Tasks (Unassigned Manager)</td>
										</c:when>
										 <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_task&_nfls=false&app_menu_name=header_application&taskScreen=taskmanager&taskType=<%=P8Constants.PROPERTY_PE_TASK_TYPE_SERVICE_APPLICATION%>&action=unassign">${taskCountUnsassignedBean.srAppMgrTaskCount}</a></span>&nbsp;&nbsp;Service Application Tasks (Unassigned Manager)</td>
					                    </c:otherwise>
				                    </c:choose>
								</tr>
								<tr>
						        	<c:choose>
										<c:when test="${taskCountUnsassignedBean.newFilingCount eq 0}">
											<td><c:if test="${taskCountUnsassignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountUnsassignedBean.newFilingCount}</span>&nbsp;&nbsp;</c:if>New Filings</td>
										</c:when>
										 <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_task&_nfls=false&app_menu_name=header_application&taskScreen=taskmanager&taskType=<%=P8Constants.PROPERTY_PE_TASK_TYPE_NEW_FILING%>&action=Unassigned">${taskCountUnsassignedBean.newFilingCount}</a></span>&nbsp;&nbsp;New Filings</td>
					                    </c:otherwise>
				                    </c:choose>
								</tr>
							<!-- Start QC 9587 R 8.10.0 Remove Contact Us task --> 
							<%--
						        <tr>
						        	<c:choose>
										<c:when test="${taskCountUnsassignedBean.msContactCount eq 0}">
											<td><c:if test="${taskCountUnsassignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountUnsassignedBean.msContactCount}</span>&nbsp;&nbsp;</c:if>Contact Us Tasks</td>
										</c:when>
										 <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_task&_nfls=false&app_menu_name=header_application&taskScreen=taskmanager&taskType=<%=P8Constants.PROPERTY_PE_TASK_TYPE_CONTACT_US%>&action=Unassigned">${taskCountUnsassignedBean.msContactCount}</a></span>&nbsp;&nbsp;Contact Us Tasks</td>
					                    </c:otherwise>
				                    </c:choose>
								</tr>
								--%>
								<!-- End QC 9587 R 8.10.0 Remove Contact Us task --> 
								<tr>
						        	<c:choose>
										<c:when test="${taskCountUnsassignedBean.provAccountReqCount eq 0}">
											<td><c:if test="${taskCountUnsassignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountUnsassignedBean.provAccountReqCount}</span>&nbsp;&nbsp;</c:if>Provider Account Request Tasks</td>
										</c:when>
										 <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_task&_nfls=false&app_menu_name=header_application&taskScreen=taskmanager&taskType=<%=P8Constants.PROPERTY_PE_TASK_TYPE_PROVIDER_ACCOUNT_REQUEST%>&action=Unassigned">${taskCountUnsassignedBean.provAccountReqCount}</a></span>&nbsp;&nbsp;Provider Account Request Tasks</td>
					                    </c:otherwise>
				                    </c:choose>
								</tr>
								<tr>
						        	<c:choose>
										<c:when test="${taskCountUnsassignedBean.orgLegalNameTasksCount eq 0}">
											<td><c:if test="${taskCountUnsassignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountUnsassignedBean.orgLegalNameTasksCount}</span>&nbsp;&nbsp;</c:if>Organization Legal Name Update Tasks</td>
										</c:when>
										 <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_task&_nfls=false&app_menu_name=header_application&taskScreen=taskmanager&taskType=<%=P8Constants.PROPERTY_PE_TASK_TYPE_ORGANIZATION_LEGAL_NAME_UPDATE_REQUEST%>&action=Unassigned">${taskCountUnsassignedBean.orgLegalNameTasksCount}</a></span>&nbsp;&nbsp;Organization Legal Name Update Tasks</td>
					                    </c:otherwise>
				                    </c:choose>
								</tr>
								<tr>
				                	<c:choose>
										<c:when test="${taskCountUnsassignedBean.msBRAppWithdrawalCount eq 0}">
											<td><c:if test="${taskCountUnsassignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountUnsassignedBean.msBRAppWithdrawalCount}</span>&nbsp;&nbsp;</c:if>Withdrawal Request - Business Application Tasks</td>
										</c:when>
										 <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_task&_nfls=false&app_menu_name=header_application&taskScreen=taskmanager&taskType=<%=P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_BUSINESS_REVIEW_APPLICATION%>&action=Unassigned">${taskCountUnsassignedBean.msBRAppWithdrawalCount}</a></span>&nbsp;&nbsp;Withdrawal Request - Business Application Tasks</td>
					                    </c:otherwise>
				                    </c:choose>
								</tr>
								<tr>
				                	<c:choose>
										<c:when test="${taskCountUnsassignedBean.msSRWithdrawalAppCount eq 0}">
											<td><c:if test="${taskCountUnsassignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountUnsassignedBean.msSRWithdrawalAppCount}</span>&nbsp;&nbsp;</c:if>Withdrawal Request - Service Application Tasks</td>
										</c:when>
										 <c:otherwise>
					                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_task&_nfls=false&app_menu_name=header_application&taskScreen=taskmanager&taskType=<%=P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_SERVICE_APPLICATION%>&action=Unassigned">${taskCountUnsassignedBean.msSRWithdrawalAppCount}</a></span>&nbsp;&nbsp;Withdrawal Request - Service Application Tasks</td>
					                    </c:otherwise>
				                    </c:choose>
								</tr>
							<%}else if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S029_PAGE_6, request.getSession())) {%>
									<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_ACCO_MANAGER, request.getSession())) {%>
										<tr>
											<c:choose>
												<c:when test="${taskCountUnsassignedBean.acceptPropTaskCount eq 0}">
							                    	<td><c:if test="${taskCountUnsassignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountUnsassignedBean.acceptPropTaskCount}</span></c:if>&nbsp;&nbsp;Accept Proposal Tasks</td>
							                    </c:when>
							                    <c:otherwise>
							                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=P8Constants.TASK_ACCEPT_PROPOSAL%>&taskAction=unassign">${taskCountUnsassignedBean.acceptPropTaskCount}</a></span>&nbsp;&nbsp;Accept Proposal Tasks</td>
							                    </c:otherwise>
								            </c:choose>
										</tr>
									<%} %>
									<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_ACCO_AGENCY, request.getSession())) {%>
										<c:if test="${role ne 'CFO'}">
											<tr>
												<c:choose>
													<c:when test="${taskCountUnsassignedBean.reviewScoresTaskCount eq 0}">
								                    	<td><c:if test="${taskCountUnsassignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountUnsassignedBean.reviewScoresTaskCount}</span></c:if>&nbsp;&nbsp;Review  Scores Tasks</td>
								                    </c:when>
								                    <c:otherwise>
								                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=P8Constants.TASK_REVIEW_SCORES%>&taskAction=unassign">${taskCountUnsassignedBean.reviewScoresTaskCount}</a></span>&nbsp;&nbsp;Review  Scores Tasks</td>
								                    </c:otherwise>
									            </c:choose>
											</tr>
										</c:if>
									<%} %>
									<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_ACCO_MANAGER, request.getSession())) {%>
										<tr>
											<c:choose>
												<c:when test="${taskCountUnsassignedBean.awardDocTaskCount eq 0}">
							                    	<td><c:if test="${taskCountUnsassignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountUnsassignedBean.awardDocTaskCount}</span></c:if>&nbsp;&nbsp;Configure Award Documents Tasks</td>
							                    </c:when>
							                    <c:otherwise>
							                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=P8Constants.TASK_CONFIGURE_AWARDS_DOCUMENTS%>&taskAction=unassign">${taskCountUnsassignedBean.awardDocTaskCount}</a></span>&nbsp;&nbsp;Configure Award Documents Tasks</td>
							                    </c:otherwise>
								            </c:choose>
										</tr>
									<%} %>
									<!-- added for R5  module Manage Organization-->
									<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.ACCO_AGENCY, request.getSession())) {%>
									<tr>
											<c:choose>
												<c:when test="${taskCountUnsassignedBean.completePsrCount eq 0}">
							                    	<td><c:if test="${taskCountUnsassignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountUnsassignedBean.completePsrCount}</span></c:if>&nbsp;&nbsp;Complete PSR Tasks</td>
							                    </c:when>
							                    <c:otherwise>
							                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=P8Constants.PROPERTY_PE_TASK_TYPE_COMPLETE_PSR%>&taskAction=unassign">${taskCountUnsassignedBean.completePsrCount}</a></span>&nbsp;&nbsp;Complete PSR Tasks</td>
							                    </c:otherwise>
								            </c:choose>
						     		</tr>
									<tr>
											<c:choose>
												<c:when test="${taskCountUnsassignedBean.finalizeAwardAmountCount eq 0}">
							                    	<td><c:if test="${taskCountUnsassignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountUnsassignedBean.finalizeAwardAmountCount}</span></c:if>&nbsp;&nbsp;Finalize Award Amount Tasks</td>
							                    </c:when>
							                    <c:otherwise>
							                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=P8Constants.PROPERTY_PE_TASK_TYPE_FINALIZE_AWARD_AMOUNT%>&taskAction=unassign">${taskCountUnsassignedBean.finalizeAwardAmountCount}</a></span>&nbsp;&nbsp;Finalize Award Amount Tasks</td>
							                    </c:otherwise>
								            </c:choose>
									</tr>
									<%}%>
									<!-- added for R5  module Manage Organization-->
								    <%-- Task added and order change as per release 2.7.0 enhancement :5678 --%> 
									<tr>
										<c:choose>
											<c:when test="${taskCountUnsassignedBean.procurementCofTaskCount eq 0}">
						                    	<td><c:if test="${taskCountUnsassignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountUnsassignedBean.procurementCofTaskCount}</span></c:if>&nbsp;&nbsp;Procurement Certification of Funds</td>
						                    </c:when>
						                    <c:otherwise>
						                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=HHSConstants.TASK_PROCUREMENT_COF%>&taskAction=unassign">${taskCountUnsassignedBean.procurementCofTaskCount}</a></span>&nbsp;&nbsp;Procurement Certification of Funds</td>
						                    </c:otherwise>
							            </c:choose>
									</tr>
									<tr>
										<c:choose>
											<c:when test="${taskCountUnsassignedBean.contConfigTaskCount eq 0}">
						                    	<td><c:if test="${taskCountUnsassignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountUnsassignedBean.contConfigTaskCount}</span></c:if>&nbsp;&nbsp;Contract Configuration Tasks</td>
						                    </c:when>
						                    <c:otherwise>
						                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=P8Constants.TASK_CONTRACT_CONFIGURATION%>&taskAction=unassign">${taskCountUnsassignedBean.contConfigTaskCount}</a></span>&nbsp;&nbsp;Contract Configuration Tasks</td>
						                    </c:otherwise>
							            </c:choose>
									</tr>
									<tr>
										<c:choose>
											<c:when test="${taskCountUnsassignedBean.certOfFundsTaskCount eq 0}">
						                    	<td><c:if test="${taskCountUnsassignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountUnsassignedBean.certOfFundsTaskCount}</span></c:if>&nbsp;&nbsp;Certification of Funds Tasks</td>
						                    </c:when>
						                    <c:otherwise>
						                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=P8Constants.TASK_CONTRACT_COF%>&taskAction=unassign">${taskCountUnsassignedBean.certOfFundsTaskCount}</a></span>&nbsp;&nbsp;Certification of Funds Tasks</td>
						                    </c:otherwise>
							            </c:choose>
									</tr>
									
									<tr>
										<c:choose>
											<c:when test="${taskCountUnsassignedBean.contractConfigurationUpdateTaskCount eq 0}">
						                    	<td><c:if test="${taskCountUnsassignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountUnsassignedBean.contractConfigurationUpdateTaskCount}</span></c:if>&nbsp;&nbsp;Contract Configuration Update</td>
						                    </c:when>
						                    <c:otherwise>
						                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=HHSConstants.TASK_CONTRACT_UPDATE%>&taskAction=unassign">${taskCountUnsassignedBean.contractConfigurationUpdateTaskCount}</a></span>&nbsp;&nbsp;Contract Configuration Update</td>
						                    </c:otherwise>
							            </c:choose>
									</tr>
									
									<tr>
										<c:choose>
											<c:when test="${taskCountUnsassignedBean.amendmentCofTaskCount eq 0}">
						                    	<td><c:if test="${taskCountUnsassignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountUnsassignedBean.amendmentCofTaskCount}</span></c:if>&nbsp;&nbsp;Amendment Certification of Funds</td>
						                    </c:when>
						                    <c:otherwise>
						                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=HHSConstants.TASK_AMENDMENT_COF%>&taskAction=unassign">${taskCountUnsassignedBean.amendmentCofTaskCount}</a></span>&nbsp;&nbsp;Amendment Certification of Funds</td>
						                    </c:otherwise>
							            </c:choose>
									</tr>
									
									<tr>
										<c:choose>
											<c:when test="${taskCountUnsassignedBean.contractConfigurationAmendmentTaskCount eq 0}">
						                    	<td><c:if test="${taskCountUnsassignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountUnsassignedBean.contractConfigurationAmendmentTaskCount}</span></c:if>&nbsp;&nbsp;Contract Configuration Amendment</td>
						                    </c:when>
						                    <c:otherwise>
						                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=HHSConstants.TASK_AMENDMENT_CONFIGURATION%>&taskAction=unassign">${taskCountUnsassignedBean.contractConfigurationAmendmentTaskCount}</a></span>&nbsp;&nbsp;Contract Configuration Amendment</td>
						                    </c:otherwise>
							            </c:choose>
									</tr>
									
									<tr>
										<c:choose>
											<c:when test="${taskCountUnsassignedBean.newFyConfigurationTaskCount eq 0}">
						                    	<td><c:if test="${taskCountUnsassignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountUnsassignedBean.newFyConfigurationTaskCount}</span></c:if>&nbsp;&nbsp;New Fiscal Year Configuration</td>
						                    </c:when>
						                    <c:otherwise>
						                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=HHSConstants.TASK_NEW_FY_CONFIGURATION%>&taskAction=unassign">${taskCountUnsassignedBean.newFyConfigurationTaskCount}</a></span>&nbsp;&nbsp;New Fiscal Year Configuration</td>
						                    </c:otherwise>
							            </c:choose>
									</tr>
									
									<tr>
										<c:choose>
											<c:when test="${taskCountUnsassignedBean.budgetReviewTaskCount eq 0}">
						                    	<td><c:if test="${taskCountUnsassignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountUnsassignedBean.budgetReviewTaskCount}</span></c:if>&nbsp;&nbsp;Budget Review Tasks</td>
						                    </c:when>
						                    <c:otherwise>
						                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=P8Constants.TASK_BUDGET_REVIEW%>&taskAction=unassign">${taskCountUnsassignedBean.budgetReviewTaskCount}</a></span>&nbsp;&nbsp;Budget Review Tasks</td>
						                    </c:otherwise>
							            </c:choose>
									</tr>
									
									<tr>
										<c:choose>
											<c:when test="${taskCountUnsassignedBean.contractBudgetModificationTaskCount eq 0}">
						                    	<td><c:if test="${taskCountUnsassignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountUnsassignedBean.contractBudgetModificationTaskCount}</span></c:if>&nbsp;&nbsp;Contract Budget Modification Review</td>
						                    </c:when>
						                    <c:otherwise>
						                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=HHSConstants.TASK_BUDGET_MODIFICATION%>&taskAction=unassign">${taskCountUnsassignedBean.contractBudgetModificationTaskCount}</a></span>&nbsp;&nbsp;Contract Budget Modification Review</td>
						                    </c:otherwise>
							            </c:choose>
									</tr>
									
									<tr>
										<c:choose>
											<c:when test="${taskCountUnsassignedBean.contractBudgetAmendmentTaskCount eq 0}">
						                    	<td><c:if test="${taskCountUnsassignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountUnsassignedBean.contractBudgetAmendmentTaskCount}</span></c:if>&nbsp;&nbsp;Contract Budget Amendment Review</td>
						                    </c:when>
						                    <c:otherwise>
						                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=HHSConstants.TASK_BUDGET_AMENDMENT%>&taskAction=unassign">${taskCountUnsassignedBean.contractBudgetAmendmentTaskCount}</a></span>&nbsp;&nbsp;Contract Budget Amendment Review</td>
						                    </c:otherwise>
							            </c:choose>
									</tr>
									
									<tr>
										<c:choose>
											<c:when test="${taskCountUnsassignedBean.contractBudgetUpdateTaskCount eq 0}">
						                    	<td><c:if test="${taskCountUnsassignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountUnsassignedBean.contractBudgetUpdateTaskCount}</span></c:if>&nbsp;&nbsp;Contract Budget Update Review</td>
						                    </c:when>
						                    <c:otherwise>
						                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=HHSConstants.TASK_BUDGET_UPDATE%>&taskAction=unassign">${taskCountUnsassignedBean.contractBudgetUpdateTaskCount}</a></span>&nbsp;&nbsp;Contract Budget Update Review</td>
						                    </c:otherwise>
							            </c:choose>
									</tr>
								
									<tr>
										<c:choose>
											<c:when test="${taskCountUnsassignedBean.invoiceReviewTaskCount eq 0}">
						                    	<td><c:if test="${taskCountUnsassignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountUnsassignedBean.invoiceReviewTaskCount}</span></c:if>&nbsp;&nbsp;Invoice Review Tasks</td>
						                    </c:when>
						                    <c:otherwise>
						                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=P8Constants.TASK_INVOICE_REVIEW%>&taskAction=unassign">${taskCountUnsassignedBean.invoiceReviewTaskCount}</a></span>&nbsp;&nbsp;Invoice Review Tasks</td>
						                    </c:otherwise>
							            </c:choose>
									</tr>
									
									<tr>
										<c:choose>
											<c:when test="${taskCountUnsassignedBean.paymentReviewTaskCount eq 0}">
						                    	<td><c:if test="${taskCountUnsassignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountUnsassignedBean.paymentReviewTaskCount}</span></c:if>&nbsp;&nbsp;Payment Review Tasks</td>
						                    </c:when>
						                    <c:otherwise>
						                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=P8Constants.TASK_PAYMENT_REVIEW%>&taskAction=unassign">${taskCountUnsassignedBean.paymentReviewTaskCount}</a></span>&nbsp;&nbsp;Payment Review Tasks</td>
						                    </c:otherwise>
							            </c:choose>
									</tr>
									
									<tr>
										<c:choose>
											<c:when test="${taskCountUnsassignedBean.advancePaymentRequestTaskCount eq 0}">
						                    	<td><c:if test="${taskCountUnsassignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountUnsassignedBean.advancePaymentRequestTaskCount}</span></c:if>&nbsp;&nbsp;Advance Request Review</td>
						                    </c:when>
						                    <c:otherwise>
						                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=HHSConstants.TASK_ADVANCE_REVIEW%>&taskAction=unassign">${taskCountUnsassignedBean.advancePaymentRequestTaskCount}</a></span>&nbsp;&nbsp;Advance Request Review</td>
						                    </c:otherwise>
							            </c:choose>
									</tr>
									<tr>
										<c:choose>
											<c:when test="${taskCountUnsassignedBean.advancePaymentReviewTaskCount eq 0}">
						                    	<td><c:if test="${taskCountUnsassignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountUnsassignedBean.advancePaymentReviewTaskCount}</span></c:if>&nbsp;&nbsp;Advance Payment Review</td>
						                    </c:when>
						                    <c:otherwise>
						                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=HHSConstants.TASK_ADVANCE_PAYMENT_REVIEW%>&taskAction=unassign">${taskCountUnsassignedBean.advancePaymentReviewTaskCount}</a></span>&nbsp;&nbsp;Advance Payment Review</td>
						                    </c:otherwise>
							            </c:choose>
									</tr>
									<!-- Added for R6- Return Payment Review Task -->
									<tr>
										<c:choose>
											<c:when test="${taskCountUnsassignedBean.returnPaymentReviewTaskCount eq 0}">
						                    	<td><c:if test="${taskCountUnsassignedBean.lbVisibilityFlag}"><span class="portletTextBold">${taskCountUnsassignedBean.returnPaymentReviewTaskCount}</span></c:if>&nbsp;&nbsp;Returned Payment Review</td>
						                    </c:when>
						                    <c:otherwise>
						                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=<%=HHSConstants.TASK_RETURN_PAYMENT_REVIEW%>&taskAction=unassign">${taskCountUnsassignedBean.returnPaymentReviewTaskCount}</a></span>&nbsp;&nbsp;Returned Payment Review</td>
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
		
