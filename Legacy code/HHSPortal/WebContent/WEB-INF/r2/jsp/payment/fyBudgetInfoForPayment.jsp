<%-- This jsp is used for Fiscal Year Information in Advance Payment Request Task Screen--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<h5>Fiscal Year Budget Information</h5>
	<%-- Grid Starts --%>
	<div class="tabularWrapper">
		<table width="100%" cellspacing='0' cellpadding='0' border="1">				
			<tr>
				<th class='centerAlign'>Start Date</th>
				<th class='centerAlign'>End Date</th>
				<th class='centerAlign'>FY Budget</th>
				<th class='centerAlign' title='Total of Submitted and Approved Invoices'>YTD Invoiced Amount</th>
				<th class='centerAlign' title='FY Budget - YTD Invoiced Amount'>Remaining Amount</th>
				<th class='centerAlign' title='Total of All Payments Including Advances'>YTD Actual Paid Amount</th>
				<th class='centerAlign' title='FY Budget -€ Total of All Payments including Advances'>Cash Balance</th>
			</tr>
			<tr>
				<td><label><fmt:formatDate pattern="MM/dd/yyyy"
				value="${fiscalBudgetInfo.startDate}" /></label></td>
				<td><label><fmt:formatDate pattern="MM/dd/yyyy"
				value="${fiscalBudgetInfo.endDate}" /></label></td>
				<td><label><fmt:formatNumber type="currency"
				value="${fiscalBudgetInfo.approvedBudget}" /></label></td>
				<td><label><fmt:formatNumber type="currency"
				value="${fiscalBudgetInfo.invoicedAmount}" /></label></td>
				<td><label><fmt:formatNumber type="currency"
				value="${fiscalBudgetInfo.remainingAmount}" /></label></td>
				<td><label><fmt:formatNumber type="currency"
				value="${fiscalBudgetInfo.ytdActualPaid}" /></label></td>
				<td><label><fmt:formatNumber type="currency"
				value="${fiscalBudgetInfo.cashBalance}" /></label></td>
			</tr>		
		</table>
	</div>
<%-- Fiscal Year Budget Information Grid Ends --%>	

<div>&nbsp;</div>

