<%-- This jsp implements the functionality of showing Contract Budget Fiscal Year Budget Information--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@page import="com.nyc.hhs.util.HHSUtil"%>
<%@page
	import="com.nyc.hhs.constants.*"%>
<%@ page errorPage="/error/errorpage.jsp"%>
<portlet:defineObjects />

<%-- The ContractFYBudget page gives an overview of the 
 fiscal year budget information--%>
 
<%-- Payment Voucher Line Details --%>
<h6>Line Details</h6>
<div class='tabularWrapper' id="paymentLineItemId">
<table cellspacing="0" cellpadding="0" class="grid">
	<tr>
		<th class='alignCenter'>FY Budget</th>
		<th class='alignCenter'>Remaining Amount</th>
		<th class='alignCenter' title='Total all Paid Invoices'>YTD Actual Paid Amount</th>
		<th class='alignCenter' title='FY Budget - Total of all Payments, Including Advances'>Cash Balance</th>
		<th class='alignCenter'>Total Advance Amount</th>
		<th class='alignCenter'>Total Payment Amount</th>
	</tr>
	<tr>
		<td class='alignRht'><fmt:formatNumber type="currency"
			value="${paymentLineDetail.approvedBudget}" /></td>
		<td class='alignRht'><fmt:formatNumber type="currency"
			value="${paymentLineDetail.remainingAmount}" /></td>
		<td class='alignRht'><fmt:formatNumber type="currency"
			value="${paymentLineDetail.ytdActualPaid}" /></td>
		<td class='alignRht'><fmt:formatNumber type="currency"
			value="${paymentLineDetail.cashBalance}" /></td>
		<td class='alignRht'><fmt:formatNumber type="currency"
			value="${paymentLineDetail.advanceAmount}" /></td>
		<td class='alignRht'><fmt:formatNumber type="currency"
			value="${paymentLineDetail.paymentAmount}" /></td>
	</tr>
</table>
</div>

<%-- 
This jsp is used for payment Chart of allocation grid in Payment module
 for grids with static headers.
 --%>

<p>&nbsp;</p>

<%--below block is added  as part of build 3.1.0, enhancement 6023  to show hide COA grid based on payment status--%>
<c:if 
test="${paymentHeaderDetail.paymentStatus ne '159'}">
<H3>Chart of Accounts Allocation</H3>

<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow'escapeXml='false'>
	<portlet:param name="gridLabel" value="payment.paymentCOF.grid" />
</portlet:resourceURL>
<c:set var="readOnlyPageAttribute" value="true"></c:set>
<c:if
	test="${detailsBeanForTaskGrid.isTaskScreen && detailsBeanForTaskGrid.isTaskAssigned && detailsBeanForTaskGrid.level eq '1'  }">
	<c:set var="readOnlyPageAttribute" value="false"></c:set>
</c:if>



<c:set var="gridColNames"><%=HHSUtil.getHeader("payment.paymentCOF.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("payment.paymentCOF.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("payment.paymentCOF.grid")%></c:set>

<%-- loading the page  --%>
<portlet:resourceURL var='loadGridData' id='loadGridData' escapeXml='false'>
	<portlet:param name="transactionName" value="paymentCOF" />
	<portlet:param name="beanName" value="com.nyc.hhs.model.PaymentChartOfAllocation" />
	<portlet:param name="gridLabel" value="payment.paymentCOF.grid" />
	<portlet:param name="subBudgetId" value="${paymentId}"/>
</portlet:resourceURL>

<%-- operations for Edit/Update/Save --%>
<portlet:resourceURL var='paymentCOFOperationGrid' id='gridOperation' escapeXml='false'>
	<portlet:param name="transactionName" value="paymentCOF" />
	<portlet:param name="beanName" value="com.nyc.hhs.model.PaymentChartOfAllocation" />
	<portlet:param name="subBudgetId" value="${paymentId}"/>
</portlet:resourceURL>
<div class='accContainer'>
<%-- JGrid for adding dynamic table --%>
	<div class='gridFormField gridScroll'>
			<jq:grid id="paymentCOFGrid-${paymentId}" 
			 isReadOnly="${readOnlyPageAttribute}"
				gridColNames="${gridColNames}"
				gridColProp="${gridColProp}" 
				subGridColProp="${subGridColProp}"
				gridUrl="${SubGridHeaderRow}"
				subGridUrl="${loadGridData}"
				cellUrl="${paymentCOFOperationGrid}"
				editUrl="${paymentCOFOperationGrid}"
				dataType="json"
				methodType="POST" 
				columnTotalName="" 
				isPagination="true"
				rowsPerPage="5" isSubGrid="true"
				positiveCurrency="paymentAmount"  
				operations="del:false,edit:true,add:false,cancel:true,save:true" />
	</div>
	</div>
</c:if>	
