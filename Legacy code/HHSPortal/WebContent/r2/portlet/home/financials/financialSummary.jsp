<%-- This JSP is for Financial Portlet Count--%>
<%@page import="java.util.ArrayList, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/financialsummary.js"></script>

		<portlet:defineObjects/>
		<%--resourceURL for events on Financial Portlet--%>
		<portlet:resourceURL var='financialSummaryPortlet' id='financialSummaryPortlet' escapeXml='false'>
		</portlet:resourceURL>
		<!-- Body Wrapper Start -->
		<form id="financialSummary" action="${financialSummaryPortlet}" method ="post">
		<div>
			<div id="financialPortlet">
				<div  class="tabularWrapper portlet1Col homepageHHS">
		  			<div class="tabularCustomHead">Financials
		  				<a href="javascript:;" onclick="javascript:onFinancialSummaryRefresh()"><img src="../framework/skins/hhsa/images/refresh.png" class="tabularCustomHeadIcon" title="Refresh" alt="Refresh"/></a>
		  			</div>
					<table cellspacing="0" cellpadding="0"  class="grid"> 
						<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S027_PAGE_7, request.getSession())) {%>                
							<tr>
								<c:choose>
									<c:when test="${financialSummary.pendingContractConfig eq '0'}">
				                    	 <td><span class="portletTextBold">${financialSummary.pendingContractConfig}</span>&nbsp;&nbsp;Contracts pending configuration</td>
				                    </c:when>
				                    <c:otherwise>
				                    	 <td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_financials&_nfls=true&_urlType=action&_windowLabel=portletInstance_37&toAction=contractListAction&fromHomePage=true&filterCriteria=contractsPendingConfiguration">${financialSummary.pendingContractConfig}</a></span>&nbsp;&nbsp;Contracts pending configuration</td>
				                    </c:otherwise>
			                    </c:choose>
							</tr>
			                <tr class="alternate">
			                	<c:choose>
									<c:when test="${financialSummary.pendingContractCOF eq '0'}">
				                    	 <td><span class="portletTextBold">${financialSummary.pendingContractCOF}</span>&nbsp;&nbsp;Contracts pending Certification of Funds </td>
				                    </c:when>
				                    <c:otherwise>
				                    	 <td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_financials&_nfls=true&_urlType=action&_windowLabel=portletInstance_37&toAction=contractListAction&fromHomePage=true&filterCriteria=contractsPendingCOF">${financialSummary.pendingContractCOF}</a></span>&nbsp;&nbsp;Contracts pending Certification of Funds </td>
				                    </c:otherwise>
			                    </c:choose>
							</tr>
			                <tr>
			                	<c:choose>
									<c:when test="${financialSummary.pendingContractRegistration eq '0'}">
				                    	 <td><span class="portletTextBold">${financialSummary.pendingContractRegistration}</span>&nbsp;&nbsp;Contracts pending registration </td>
				                    </c:when>
				                    <c:otherwise>
				                    	 <td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_financials&_nfls=true&_urlType=action&_windowLabel=portletInstance_37&toAction=contractListAction&fromHomePage=true&filterCriteria=contractsPendingRegistration">${financialSummary.pendingContractRegistration}</a></span>&nbsp;&nbsp;Contracts pending registration </td>
				                    </c:otherwise>
			                    </c:choose>
							</tr>
			                <tr class="alternate">
			                	<c:choose>
									<c:when test="${financialSummary.pendingApprovalBugtAmd eq '0'}">
				                    	 <td><span class="portletTextBold">${financialSummary.pendingApprovalBugtAmd}</span>&nbsp;&nbsp;Budgets and Amendment Budgets pending approval </td>
				                    </c:when>
				                    <c:otherwise>
				                    	 <td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_financials&_nfls=true&_urlType=action&_windowLabel=portletInstance_37&toAction=budgetListAction&fromHomePage=true&filterCriteria=budgetsAmendmentPendApproval">${financialSummary.pendingApprovalBugtAmd}</a></span>&nbsp;&nbsp;Budgets and Amendment Budgets pending approval </td>
				                    </c:otherwise>
			                    </c:choose>
							</tr>
							<tr>
								<c:choose>
									<c:when test="${financialSummary.pendingApprovalBugtMod eq '0'}">
				                    	 <td><span class="portletTextBold">${financialSummary.pendingApprovalBugtMod}</span>&nbsp;&nbsp;Budget Modifications and Updates pending approval </td>
				                    </c:when>
				                    <c:otherwise>
				                    	 <td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_financials&_nfls=true&_urlType=action&_windowLabel=portletInstance_37&toAction=budgetListAction&fromHomePage=true&filterCriteria=budgetsModUpdatesPendApproval">${financialSummary.pendingApprovalBugtMod}</a></span>&nbsp;&nbsp;Budget Modifications and Updates pending approval </td>
				                    </c:otherwise>
			                    </c:choose>
							</tr>
			                <tr class="alternate">
			                	<c:choose>
									<c:when test="${financialSummary.pendingApprovalInvoic eq '0'}">
				                    	<td><span class="portletTextBold">${financialSummary.pendingApprovalInvoic}</span>&nbsp;&nbsp;Invoices pending approval </td>
				                    </c:when>
				                    <c:otherwise>
				                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_financials&_nfls=true&_urlType=action&_windowLabel=portletInstance_37&toAction=invoiceListAction&fromHomePage=true&filterCriteria=invoicesPendingApproval">${financialSummary.pendingApprovalInvoic}</a></span>&nbsp;&nbsp;Invoices pending approval </td>
				                    </c:otherwise>
			                    </c:choose>
							</tr>
							<tr>
								<c:choose>
									<c:when test="${financialSummary.pendingApprovalPymets eq '0'}">
				                    	<td><span class="portletTextBold">${financialSummary.pendingApprovalPymets}</span>&nbsp;&nbsp;Payments pending approval </td>
				                    </c:when>
				                    <c:otherwise>
				                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_financials&_nfls=true&_urlType=action&_windowLabel=portletInstance_37&toAction=paymentListAction&fromHomePage=true&filterCriteria=paymentsPendApproval">${financialSummary.pendingApprovalPymets}</a></span>&nbsp;&nbsp;Payments pending approval </td>
				                    </c:otherwise>
			                    </c:choose>
							</tr>
			                <tr class="alternate">
			                	<c:choose>
									<c:when test="${financialSummary.paymentsFMSError eq '0'}">
				                    	<td><span class="portletTextBold">${financialSummary.paymentsFMSError}</span>&nbsp;&nbsp;Payments with FMS Error </td>
				                    </c:when>
				                    <c:otherwise>
				                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_financials&_nfls=true&_urlType=action&_windowLabel=portletInstance_37&toAction=paymentListAction&fromHomePage=true&filterCriteria=paymentsFMSError">${financialSummary.paymentsFMSError}</a></span>&nbsp;&nbsp;Payments with FMS Error </td>
				                    </c:otherwise>
			                    </c:choose>
							</tr>
						<%}else if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S027_PAGE_8, request.getSession())) {%>                
							<!-- Start : R5 updated structure-->
							<tr>
								<c:set var="hideContractLinks"><%=CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HEADER_AGENCYF, request.getSession())%></c:set>
								<c:choose>
									<c:when test="${financialSummary.budgetPendingSubmission eq '0' or hideContractLinks eq false}">
				                    	 <td><span class="portletTextBold">${financialSummary.budgetPendingSubmission}</span>&nbsp;&nbsp;Budgets pending submission </td>
				                    </c:when>
				                    <c:otherwise>
				                    	 <td><span class="portletTextBold"><a class='redcolor' href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_financials&_nfls=true&_urlType=action&_windowLabel=portletInstance_37&toAction=budgetListAction&fromHomePage=true&filterCriteria=budgetsPendSubmission">${financialSummary.budgetPendingSubmission}</a></span>&nbsp;&nbsp;Budgets pending submission </td>
				                    </c:otherwise>
			                    </c:choose>
			                    <c:choose>
									<c:when test="${financialSummary.activeBudgets eq '0' or hideContractLinks eq false}">
				                    	 <td><span class="portletTextBold">${financialSummary.activeBudgets}</span>&nbsp;&nbsp;Active Budgets</td>
				                    </c:when>
				                    <c:otherwise>
				                    	 <td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_financials&_nfls=true&_urlType=action&_windowLabel=portletInstance_37&toAction=budgetListAction&fromHomePage=true&filterCriteria=activeBudgets&filtered=filtered">${financialSummary.activeBudgets}</a></span>&nbsp;&nbsp;Active Budgets</td>
				                    </c:otherwise>
			                    </c:choose>
							</tr>
			                <tr class="alternate">
			                	<c:choose>
									<c:when test="${financialSummary.budgetReturnForRevision eq '0' or hideContractLinks eq false}">
				                    	 <td><span class="portletTextBold">${financialSummary.budgetReturnForRevision}</span>&nbsp;&nbsp;Budgets returned for revision</td>
				                    </c:when>
				                    <c:otherwise>
				                    	 <td><span class="portletTextBold"><a class='redcolor' href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_financials&_nfls=true&_urlType=action&_windowLabel=portletInstance_37&toAction=budgetListAction&fromHomePage=true&filterCriteria=budgetRetRevision">${financialSummary.budgetReturnForRevision}</a></span>&nbsp;&nbsp;Budgets returned for revision</td>
				                    </c:otherwise>
			                    </c:choose>
			                    <!--Start Added in R5 -->
			                    <c:choose>
									<c:when test="${financialSummary.budgetPendingApproval eq '0' or hideContractLinks eq false}">
										 <td><span class="portletTextBold">${financialSummary.budgetPendingApproval}</span>&nbsp;&nbsp;Budgets pending approval </td>
									</c:when>
									<c:otherwise>
										 <td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_financials&_nfls=true&_urlType=action&_windowLabel=portletInstance_37&toAction=budgetListAction&fromHomePage=true&filterCriteria=budgetsPendApproval">${financialSummary.budgetPendingApproval}</a></span>&nbsp;&nbsp;Budgets pending approval </td>
									</c:otherwise>
								</c:choose>
								<!--End Added in R5 -->
							</tr>
							<tr>
			                	<c:choose>
									<c:when test="${financialSummary.modificationPendingSubmission eq '0' or hideContractLinks eq false}">
				                    	<td width='50%'><span class="portletTextBold">${financialSummary.modificationPendingSubmission}</span>&nbsp;&nbsp;Modifications and Updates pending submission </td>
				                    </c:when>
				                    <c:otherwise>
				                    	<td width='50%'><span class="portletTextBold"><a class='redcolor' href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_financials&_nfls=true&_urlType=action&_windowLabel=portletInstance_37&toAction=budgetListAction&fromHomePage=true&filterCriteria=modPendSubmission">${financialSummary.modificationPendingSubmission}</a></span>&nbsp;&nbsp;Modifications and Updates pending submission </td>
				                    </c:otherwise>
			                    </c:choose>
			                    <c:choose>
									<c:when test="${financialSummary.modificationPendingApproval eq '0' or hideContractLinks eq false}">
				                    	<td><span class="portletTextBold">${financialSummary.modificationPendingApproval}</span>&nbsp;&nbsp;Modifications and Updates pending approval </td>
				                    </c:when>
				                    <c:otherwise>
				                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_financials&_nfls=true&_urlType=action&_windowLabel=portletInstance_37&toAction=budgetListAction&fromHomePage=true&filterCriteria=modPendApproval">${financialSummary.modificationPendingApproval}</a></span>&nbsp;&nbsp;Modifications and Updates pending approval </td>
				                    </c:otherwise>
			                    </c:choose>
							</tr>
			                <tr class="alternate">
			                	<c:choose>
									<c:when test="${financialSummary.modificationReturnForRevision eq '0' or hideContractLinks eq false}">
				                    	<td><span class="portletTextBold">${financialSummary.modificationReturnForRevision}</span>&nbsp;&nbsp;Modifications and Updates returned for revision </td>
				                    </c:when>
				                    <c:otherwise>
				                    	<td><span class="portletTextBold"><a class='redcolor' href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_financials&_nfls=true&_urlType=action&_windowLabel=portletInstance_37&toAction=budgetListAction&fromHomePage=true&filterCriteria=modUpdatesReturnedSubmission">${financialSummary.modificationReturnForRevision}</a></span>&nbsp;&nbsp;Modifications and Updates returned for revision </td>
				                    </c:otherwise>
			                    </c:choose>
			                    <c:choose>
									<c:when test="${financialSummary.invoicePendingApproval eq '0' or hideContractLinks eq false}">
				                    	<td><span class="portletTextBold">${financialSummary.invoicePendingApproval}</span>&nbsp;&nbsp;Invoices pending approval </td>
				                    </c:when>
				                    <c:otherwise>
				                    	<td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_financials&_nfls=true&_urlType=action&_windowLabel=portletInstance_37&toAction=invoiceListAction&fromHomePage=true&filterCriteria=invoicesPendingApproval">${financialSummary.invoicePendingApproval}</a></span>&nbsp;&nbsp;Invoices pending approval </td>
				                    </c:otherwise>
			                    </c:choose>
							</tr>
							<tr>
								<c:choose>
									<c:when test="${financialSummary.invoicePendingSubmission eq '0' or hideContractLinks eq false}">
				                    	<td><span class="portletTextBold">${financialSummary.invoicePendingSubmission}</span>&nbsp;&nbsp;Invoices pending submission </td>
				                    </c:when>
				                    <c:otherwise>
				                    	<td><span class="portletTextBold"><a class='redcolor' href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_financials&_nfls=true&_urlType=action&_windowLabel=portletInstance_37&toAction=invoiceListAction&fromHomePage=true&filterCriteria=invoicesPendingSubmission">${financialSummary.invoicePendingSubmission}</a></span>&nbsp;&nbsp;Invoices pending submission </td>
				                    </c:otherwise>
			                    </c:choose>
			                    <c:choose>
									<c:when test="${financialSummary.contractPendingRegistration eq '0' or hideContractLinks eq false}">
				                    	 <td><span class="portletTextBold">${financialSummary.contractPendingRegistration}</span>&nbsp;&nbsp;Contracts pending registration</td>
				                    </c:when>
				                    <c:otherwise>
				                    	 <td><span class="portletTextBold"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_financials&_nfls=true&_urlType=action&_windowLabel=portletInstance_37&toAction=contractListAction&fromHomePage=true&filterCriteria=contractsPendingRegistration">${financialSummary.contractPendingRegistration}</a></span>&nbsp;&nbsp;Contracts pending registration</td>
				                    </c:otherwise>
			                    </c:choose>
			                 </tr>
			                 <tr class="alternate">
			                 	<c:choose>
									<c:when test="${financialSummary.invoiceReturnForRevision eq '0' or hideContractLinks eq false}">
				                    	<td><span class="portletTextBold">${financialSummary.invoiceReturnForRevision}</span>&nbsp;&nbsp;Invoices returned for revision </td>
				                    </c:when>
				                    <c:otherwise>
				                    	<td><span class="portletTextBold"><a class='redcolor' href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_financials&_nfls=true&_urlType=action&_windowLabel=portletInstance_37&toAction=invoiceListAction&fromHomePage=true&filterCriteria=invoicesReturnedRevision">${financialSummary.invoiceReturnForRevision}</a></span>&nbsp;&nbsp;Invoices returned for revision </td>
				                    </c:otherwise>
			                    </c:choose>
			                 <td/>
							</tr>
							<!-- End : R5 updated structure-->
						<%}else{ %>
 							<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
 						<%} %>
					</table>
				</div>
		</div>
	</div>
</form>
