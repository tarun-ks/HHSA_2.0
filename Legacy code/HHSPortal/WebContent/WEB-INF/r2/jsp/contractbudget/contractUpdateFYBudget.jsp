<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%-- 
	Fiscal Year Budget Information : Contains general information about the Fiscal Year
		 Budget such as the fiscal year amount, available balance, etc
    --%>
<%-- Fiscal Year Budget Information Starts --%>
<h3>Fiscal Year Budget Information</h3>
<div class='tabularWrapper' id="contractUpdateFYBudget">
<table cellspacing="0" cellpadding="0" class="grid">
	<tr>
		<th>Start Date</th>
		<th>End Date</th>
		<th class='alignCenter'>Original FY Budget</th>
		<th class='alignCenter'>Updated FY Budget</th>
		<th class='alignCenter' title="Total of Submitted and Approved Invoices">YTD Invoiced Amount</th>
		<th class='alignCenter' title="FY Budget - YTD Invoiced Amount">Remaining Amount</th>
		<th class='alignCenter' title="Total of all Disbursed Payments">YTD Actual Paid Amount</th>
	</tr>
	<tr>
		<td><fmt:formatDate pattern="MM/dd/yyyy"
			value="${fiscalBudgetInfo.startDate}" /></td>
		<td><fmt:formatDate pattern="MM/dd/yyyy"
			value="${fiscalBudgetInfo.endDate}" /></td>
		<td class='alignRht'><fmt:formatNumber type="currency"
			value="${fiscalBudgetInfo.approvedBudget}" /></td>
		<td class='alignRht'><fmt:formatNumber type="currency"
			value="${fiscalBudgetInfo.updateAmount}" /></td>
		<td class='alignRht'><fmt:formatNumber type="currency"
			value="${fiscalBudgetInfo.invoicedAmount}" /></td>
		<td class='alignRht'><fmt:formatNumber type="currency"
			value="${fiscalBudgetInfo.remainingAmount}" /></td>
		<td class='alignRht'><fmt:formatNumber type="currency"
			value="${fiscalBudgetInfo.ytdActualPaid}" /></td>
	</tr>
</table>
</div>