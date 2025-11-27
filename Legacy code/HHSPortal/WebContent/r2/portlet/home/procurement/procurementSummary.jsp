<%-- This JSP is for Procurement Portlet Count--%>
<div>
	<div id="procurementPortlet">
		<%@page import="java.util.ArrayList, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
		<%@page language="java" contentType="text/html;charset=UTF-8"%>
		<%@page errorPage="/error/errorpage.jsp" %>
		<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
		<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
		<portlet:defineObjects/>
		<script type="text/javascript">
			var loadingBars = new Array();
			// This function is used to display procurement counts
			function onProcurementSummaryRefresh()
			{
				$("#procurementPortlet").loadingHome("Loading");
				hhsAjaxRender(null, document.getElementById("procurementSummary"), "procurementPortlet", document.getElementById("procurementSummary").action, "loadingCallBack");
			}
			// This function is used to remove waiting icon
			function loadingCallBack()
			{
				$("#procurementPortlet").loadingHomeClose();
			}
		</script>
		<%--resourceURL for events on Procurement Portlet--%>
		<portlet:resourceURL var='procurementSummaryPortlet' id='procurementSummaryPortlet' escapeXml='false'>
		</portlet:resourceURL>
	<!-- Body Wrapper Start -->
		<form id="procurementSummary" action="${procurementSummaryPortlet}" method ="post">
				<div class="tabularWrapper portlet1Col homepageHHS">
		  			<div class="tabularCustomHead">Procurements
		  				<a href="javascript:;" onclick="javascript:onProcurementSummaryRefresh()"><img src="../framework/skins/hhsa/images/refresh.png" class="tabularCustomHeadIcon" title="Refresh" alt="Refresh"/></a>
		  			</div>
					<table cellspacing="0" cellpadding="0"  class="grid">    
					  	<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S027_PAGE_7, request.getSession())) {%>            
							<tr>
								<c:choose>
									<c:when test="${procurementSummary.scheduledRFPWithIn10Days eq '0'}">
				                    	<td><c:if test="${visibilityFlag}"><span class="portletTextBold">${procurementSummary.scheduledRFPWithIn10Days}</span>&nbsp;&nbsp;</c:if>RFPs scheduled to be released within 10 days</td>
				                    </c:when>
				                    <c:otherwise>
				                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_procurement&_nfls=false&app_menu_name=header_procurement&fromHomePage=true&filterCriteria=rfpReleased10Days&filtered=filtered">${procurementSummary.scheduledRFPWithIn10Days}</a></span>&nbsp;&nbsp;RFPs scheduled to be released within 10 days</td>
				                    </c:otherwise>
			                    </c:choose>
							</tr>
			                <tr class="alternate">
			                	<c:choose>
			                		<c:when test="${procurementSummary.scheduledRFPWithIn60Days eq '0'}">
				                    	<td><c:if test="${visibilityFlag}"><span class="portletTextBold">${procurementSummary.scheduledRFPWithIn60Days}</span>&nbsp;&nbsp;</c:if>RFPs scheduled to be released within 60 days</td>
				                    </c:when>
				                    <c:otherwise>
				                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_procurement&_nfls=false&app_menu_name=header_procurement&fromHomePage=true&filterCriteria=rfpReleased60Days&filtered=filtered">${procurementSummary.scheduledRFPWithIn60Days}</a></span>&nbsp;&nbsp;RFPs scheduled to be released within 60 days</td>
				                    </c:otherwise>
			                 	</c:choose>
							</tr>
			                <tr>
			                	<c:choose>
			                		<c:when test="${procurementSummary.rfpInReleasedStatus eq '0'}">
				                    	 <td><c:if test="${visibilityFlag}"><span class="portletTextBold">${procurementSummary.rfpInReleasedStatus}</span>&nbsp;&nbsp;</c:if>RFPs in released status</td>
				                    </c:when>
				                    <c:otherwise>
				                    	 <td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_procurement&_nfls=false&app_menu_name=header_procurement&fromHomePage=true&filterCriteria=rfpReleasedStatus&filtered=filtered">${procurementSummary.rfpInReleasedStatus}</a></span>&nbsp;&nbsp;RFPs in released status</td>
				                    </c:otherwise>
			                 	</c:choose>
							</tr>
			                <tr class="alternate">
			                	<c:choose>
			                		<c:when test="${procurementSummary.proposalDueDateIn10Days eq '0'}">
				                    	 <td><c:if test="${visibilityFlag}"><span class="portletTextBold">${procurementSummary.proposalDueDateIn10Days}</span>&nbsp;&nbsp;</c:if>RFPs with proposal due dates within 10 days</td>
				                    </c:when>
				                    <c:otherwise>
				                    	 <td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_procurement&_nfls=false&app_menu_name=header_procurement&fromHomePage=true&filterCriteria=rfpProposalDueDate10Days&filtered=filtered">${procurementSummary.proposalDueDateIn10Days}</a></span>&nbsp;&nbsp;RFPs with proposal due dates within 10 days</td>
				                    </c:otherwise>
			                 	</c:choose>
							</tr>
							<tr>
								<c:choose>
			                		<c:when test="${procurementSummary.rfpWithProposalsReceived eq '0'}">
				                    	 <td><c:if test="${visibilityFlag}"><span class="portletTextBold">${procurementSummary.rfpWithProposalsReceived}</span>&nbsp;&nbsp;</c:if>RFPs with proposals received</td>
				                    </c:when>
				                    <c:otherwise>
				                    	 <td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_procurement&_nfls=false&app_menu_name=header_procurement&fromHomePage=true&filterCriteria=rfpProposalReceived&filtered=filtered">${procurementSummary.rfpWithProposalsReceived}</a></span>&nbsp;&nbsp;RFPs with proposals received</td>
				                    </c:otherwise>
			                 	</c:choose>
							</tr>
							<!-- Start || Text Changed as a part of Enhancement #5419 for Release 3.3.0 -->
			                <tr class="alternate">
			                	<c:choose>
			                		<c:when test="${procurementSummary.rfpWithEvaluationsComplete eq '0'}">
				                    	 <td><c:if test="${visibilityFlag}"><span class="portletTextBold">${procurementSummary.rfpWithEvaluationsComplete}</span>&nbsp;&nbsp;</c:if>RFPs with evaluations complete and submitted to Accelerator</td>
				                    </c:when>
				                    <c:otherwise>
				                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_procurement&_nfls=false&app_menu_name=header_procurement&fromHomePage=true&filterCriteria=rfpEvaluationComplete&filtered=filtered">${procurementSummary.rfpWithEvaluationsComplete}</a></span>&nbsp;&nbsp;RFPs with evaluations complete and submitted to Accelerator</td>
				                    </c:otherwise>
			                 	</c:choose>
							</tr>
			                <tr>
			                	<c:choose>
			                		<c:when test="${procurementSummary.approveAwardTaskCount eq '0'}">
				                    	<td><c:if test="${visibilityFlag}"><span class="portletTextBold">${procurementSummary.approveAwardTaskCount}</span>&nbsp;&nbsp;</c:if>RFPs with selections made</td>
				                    </c:when>
				                    <c:otherwise>
				                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_procurement&_nfls=false&app_menu_name=header_procurement&fromHomePage=true&filterCriteria=rfpSelectionMade&filtered=filtered">${procurementSummary.approveAwardTaskCount}</a></span>&nbsp;&nbsp;RFPs with selections made</td>
				                    </c:otherwise>
			                 	</c:choose>
							</tr>
							<!-- End || Text Changed as a part of Enhancement #5419 for Release 3.3.0 -->
						<%}else if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S027_PAGE_8, request.getSession())) {%>
								<c:set var="hideProcurementLinks"><%=CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HEADER_AGENCYP, request.getSession())%></c:set>
<!-- changes in R5 starts-->
							<tr>
								<c:choose>
									<c:when test="${procurementSummary.rfpReleaseIn30Days eq '0' or hideProcurementLinks eq false}">
				                    	<td width='50%'><c:if test="${visibilityFlag}"><span class="portletTextBold">${procurementSummary.rfpReleaseIn30Days}</span>&nbsp;&nbsp;</c:if>RFPs you're eligible for will be released within 30 days</td>
				                    </c:when>
				                    <c:otherwise>
				                    	<td width='50%'><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_procurement&_nfls=false&app_menu_name=header_procurement&fromHomePage=true&filterCriteria=rfpReleased30Days&filtered=filtered">${procurementSummary.rfpReleaseIn30Days}</a></span>&nbsp;&nbsp;RFPs you're eligible for will be released within 30 days</td>
				                    </c:otherwise>
			                    </c:choose>
			                    <c:choose>
			                		<c:when test="${procurementSummary.rfpWith1Draft eq '0' or hideProcurementLinks eq false}">
				                    	 <td width='50%'><c:if test="${visibilityFlag}"><span class="portletTextBold">${procurementSummary.rfpWith1Draft}</span>&nbsp;&nbsp;</c:if>RFPs with draft or submitted proposals</td>
				                    </c:when>
				                    <c:otherwise>
				                    	 <td width='50%'><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_procurement&_nfls=false&app_menu_name=header_procurement&fromHomePage=true&filterCriteria=proposalInDraftStatus&filtered=filtered">${procurementSummary.rfpWith1Draft}</a></span>&nbsp;&nbsp;RFPs with draft or submitted proposals</td>
				                    </c:otherwise>
			                 	</c:choose>
							</tr>
			                <tr class="alternate">
			              		<c:choose>
			                		<c:when test="${procurementSummary.rfpDueDateIn30Days eq '0' or hideProcurementLinks eq false}">
				                    	<td width='50%'><c:if test="${visibilityFlag}"><span class="portletTextBold">${procurementSummary.rfpDueDateIn30Days}</span>&nbsp;&nbsp;</c:if>RFPs you're eligible for have due dates within 30 days</td>
				                    </c:when>
				                    <c:otherwise>
				                    	<td width='50%'><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_procurement&_nfls=false&app_menu_name=header_procurement&fromHomePage=true&filterCriteria=rfpDueIn30Days&filtered=filtered">${procurementSummary.rfpDueDateIn30Days}</a></span>&nbsp;&nbsp;RFPs you're eligible for have due dates within 30 days</td>
				                    </c:otherwise>
			                 	</c:choose>
			                 	<c:choose>
			                 		<c:when test="${procurementSummary.rfpEligibleForAward eq '0' or hideProcurementLinks eq false}">
				                    	 <td width='50%'><c:if test="${visibilityFlag}"><span class="portletTextBold">${procurementSummary.rfpEligibleForAward}</span>&nbsp;&nbsp;</c:if>RFPs with proposals determined eligible for award</td>
				                    </c:when>
				                    <c:otherwise>
				                    	 <td width='50%'><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_procurement&_nfls=false&app_menu_name=header_procurement&fromHomePage=true&filterCriteria=rfpInSelectionsMadeStatus&filtered=filtered">${procurementSummary.rfpEligibleForAward}</a></span>&nbsp;&nbsp;RFPs with proposals determined eligible for award</td>
				                    </c:otherwise>
			                 	</c:choose>
							</tr>
							<tr>
							<c:if test="${procurementSummary.proposalReturnRevision > 0}">
							 	<td width='50%'><span class="red-ex-mark"/> <a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_procurement&_nfls=false&app_menu_name=header_procurement&fromHomePage=true&filterCriteria=submittedProposal&filtered=filtered">1 or more proposal has been returned back to you for revisions</a></td>
							 	<td width='50%'></td>
							</c:if>
							</tr>
<!-- changes in R5 ends-->			             
						<%}else{ %>
 							<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
 						<%} %>
					</table>
				</div>
		</form>	
	</div>
</div>
