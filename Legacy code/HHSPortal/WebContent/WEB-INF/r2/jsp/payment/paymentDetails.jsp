<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@page import="com.nyc.hhs.constants.HHSConstants"%>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@ taglib prefix="fmtMessages" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@ page errorPage="/error/errorpage.jsp"%>
<%--
This file is designed to display S344- Payment details page of payment module
 --%>
 
 <%-- Resource URL to display payment details  --%>
<portlet:resourceURL var='showCBGridTabs' id='showCBGridTabs'
	escapeXml='false'>
</portlet:resourceURL>

<fmtMessages:setBundle basename="com/nyc/hhs/properties/messages"/>

<%-- Included of Jquery CSS and JS files  --%>
<link rel="stylesheet" media="screen" href="${pageContext.servletContext.contextPath}/r2/css/ui.jqgrid.css" type="text/css"></link>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/grid.locale-en.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.formatCurrency-1.4.0.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/accordianFunctions.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/contractBudget.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jqgridDialogOveride.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/unsaveData.js"></script>
<script type="text/javascript" src="../resources/js/autoNumeric-1.7.5.js"></script>

<style type="text/css">
/* Fixed for Contract information as per updated wireframes......page specific fix, DO NOT INCLUDE IN EXTERNAL CSS */
.paymentFormWrapper span.label{
	width:46% !important
}
.paymentFormWrapper span.formfield{
	width: 50% !important
}
</style>

<c:set var="sectionName"><%=HHSComponentMappingConstant.SECTION_PAYMENT_DETAIL_344%></c:set>
<d:content  section="${sectionName}" authorize="" isReadOnly="true">
<div class='accContainer'>
	<h2><label class='floatLft'>Payment Details - ${paymentHeaderDetail.paymentVoucherNo} </label>
		<div class="linkReturnValut floatRht">
			<a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_financials&_nfls=true&_urlType=action&_windowLabel=portletInstance_37&toAction=paymentListAction">Return to Payments List</a>
	</div>
	</h2>

 
	<div class='hr'></div>
	<% String lsErrorMessage = HHSConstants.EMPTY_STRING;
		if(null != request.getAttribute(HHSConstants.ERROR_MESSAGE)){
			lsErrorMessage = (String) request.getAttribute(HHSConstants.ERROR_MESSAGE);
		%>
			<div class="failed breakAll" style="display:block" id="error"><%=lsErrorMessage%></div>			
	<%} %>	
	<div class="failed" id="errorGlobalMsg"></div>
	<div class="passed" id="successGlobalMsg"></div>
	
	
	<div class='clear'></div>
		
	<h2>Payment Voucher</h2>
	<div class='hr'></div>
	
	<h6>Header Details</h6>
	
	
	<div class="formcontainer paymentFormWrapper">
		<h3>General Information</h3>
		<div class="row"><span class="label">Agency Code:</span> <span
			class="formfield">${paymentHeaderDetail.agencyCode}</span></div>
		<div class="row"><span class="label">Payment Voucher No.:</span> <span
			class="formfield">${paymentHeaderDetail.paymentVoucherNo}</span></div>
		<div class="row"><span class="label">Budget FY:</span> <span
			class="formfield">${paymentHeaderDetail.budgetFYId}</span></div>
		<div class="row"><span class="label">Fiscal Year:</span> <span
			class="formfield">${paymentHeaderDetail.fiscalYearId}</span></div>
		<div class="row"><span class="label">Period:</span> <span
			class="formfield">${paymentHeaderDetail.period}</span></div>
	</div>
	
	<div class="formcontainer paymentFormWrapper">
		<h3>Invoice Information</h3>
		<div class="row"><span class="label">Invoice Number:</span>
		 <span	class="formfield">${paymentHeaderDetail.invoiceNo}</span></div>
		<div class="row"><span class="label">Invoice Submission Date:</span> <span
			class="formfield">${paymentHeaderDetail.invoiceSubmittedDate}</span></div>
<%--below block is added  as part of build 3.1.0, enhancement 6023  to show invoice rejection date conditionally based on payment status--%>		
		<span class="formfield">
				<c:choose>
				    <c:when test="${paymentHeaderDetail.paymentStatus ne null && paymentHeaderDetail.paymentStatus eq '159'}">
				       		<div class="row"><span class="label">Invoice Rejected Date:</span> <span
							class="formfield">${paymentHeaderDetail.rejectedDate}</span></div>
				    </c:when>
				    <c:otherwise>
				       		<div class="row"><span class="label">Invoice Approved Date:</span> <span
							class="formfield">${paymentHeaderDetail.invoiceApprovedDate}</span></div>
				    </c:otherwise>
				</c:choose>
		</span>
		<div class="row"><span class="label">Service Date From:</span> <span
			class="formfield">${paymentHeaderDetail.invoiceStartDate}</span></div>
		<div class="row"><span class="label">Service Date To:</span> <span
			class="formfield">${paymentHeaderDetail.invoiceEndDate}</span></div>
	
	</div>
	

	<div class='clear'>&nbsp;</div>
	
	<div class="formcontainer paymentFormWrapper">
		<h3>Vendor Information</h3>
		<div class="row"><span class="label">Vendor Code:</span> <span
			class="formfield">${paymentHeaderDetail.vendorCode}</span></div>
		<div class="row"><span class="label">Vendor Address Code:</span> <span
			class="formfield">${paymentHeaderDetail.vendorAddrCode}</span></div>
		<div class="row"><span class="label">Payee Name:</span> 
			<span class="formfield">
				<c:choose>
				    <c:when test="${paymentHeaderDetail.payeeName ne null}">
				       ${paymentHeaderDetail.payeeName}
				    </c:when>
				    <c:otherwise>
				        --
				    </c:otherwise>
				</c:choose>
			</span>
		</div>
		<div class="row"><span class="label">Payee Vendor Code:</span> 
			<span class="formfield">
				<c:choose>
				    <c:when test="${paymentHeaderDetail.payeeVendorCode ne null}">
				       ${paymentHeaderDetail.payeeVendorCode}
				    </c:when>
				    <c:otherwise>
				        --
				    </c:otherwise>
				</c:choose>
			</span>
		</div>
		<div class="row"><span class="label">Payee Vendor Address Code:</span> 
			<span class="formfield">
				<c:choose>
				    <c:when test="${paymentHeaderDetail.payeeVendorAddrCode ne null}">
				       ${paymentHeaderDetail.payeeVendorAddrCode}
				    </c:when>
				    <c:otherwise>
				        --
				    </c:otherwise>
				</c:choose>
			</span>
		</div>
	</div>
	

	<div class="formcontainer paymentFormWrapper">
		<h3>Disbursement Information</h3>
		<div class="row"><span class="label">Disbursement Number:</span> <span
			class="formfield">${paymentHeaderDetail.checkNum}</span></div>
		<div class="row"><span class="label">Disbursement Date:</span> <span
			class="formfield">${paymentHeaderDetail.disbursementDate}</span></div>
	</div>
	
	<p>&nbsp;</p>
	
<%--Start : Added in R5 --%>
<portlet:actionURL var='navigateListScreenUrl' escapeXml='false'>
	<portlet:param  name='controllerAction' value='redirectFromLanding'/>
</portlet:actionURL>
<input type="hidden" name="hiddenNavigateListScreenUrl" id="hiddenNavigateListScreenUrl"  value="${navigateListScreenUrl}"/>

	<form:form id="paymentDetail" name="paymentDetail">
	
	View Related:&nbsp;&nbsp;<a class="activelink" href="javascript:void(0);" onclick="submitFormToViewContractList('${contractId}');">Contract</a>&nbsp;&nbsp;|&nbsp;&nbsp;  
		  	<a class="activelink" href="javascript:void(0);" onclick="submitFormToViewBudgetList('${contractId}','${budgetId}');">Budget</a>&nbsp;&nbsp;|&nbsp;&nbsp; 
		    <c:if test="${not empty paymentHeaderDetail.invoiceNo && (paymentHeaderDetail.paymentStatus ne null && paymentHeaderDetail.paymentStatus ne '159')}">
		   		<a class="activelink" href="javascript:void(0);" onclick="submitFormToViewInvoiceList('${contractId}',null,'${paymentHeaderDetail.invoiceId}');">
		    </c:if>
		   		Invoice
		    <c:if test="${not empty paymentHeaderDetail.invoiceNo && (paymentHeaderDetail.paymentStatus ne null && paymentHeaderDetail.paymentStatus ne '159')}">
		   		</a>
		   	</c:if>
	</form:form>
<%--End : Added in R5 --%>
	<%-- Payment Voucher Line Details --%>
	<div id="assignAdvanceId">
		<jsp:include page="/WEB-INF/r2/jsp/payment/paymentVoucherLineDetails.jsp" />
	</div>

	<p>&nbsp;</p>

</div>
</d:content>

