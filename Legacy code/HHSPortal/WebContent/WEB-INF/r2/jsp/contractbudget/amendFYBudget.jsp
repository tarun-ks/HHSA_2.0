<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<h3>Fiscal Year Budget Information</h3>
	<%-- Grid Starts --%>
	<div class='tabularWrapper' id="amendFYBudget">
	<table cellspacing="0" cellpadding="0" class="grid">
		<tr>
			<th>Start Date</th>
			<th>End Date</th>
			<th class='alignCenter'>Approved FY Budget</th>
			<th class='alignCenter'>YTD Invoiced Amount</th>
			<th class='alignCenter'>Remaining Amount</th>
			<th class='alignCenter'>Amendment Amount</th>
			<th class='alignCenter'>Proposed Budget</th>
		</tr>
		<tr>
			<td><fmt:formatDate pattern="MM/dd/yyyy"
				value="${fiscalBudgetInfo.startDate}" /></td>
			<td><fmt:formatDate pattern="MM/dd/yyyy"
				value="${fiscalBudgetInfo.endDate}" /></td>
			<td class='alignRht'><fmt:formatNumber type="currency"
				value="${fiscalBudgetInfo.approvedBudget}" /></td>
			<td class='alignRht'><fmt:formatNumber type="currency"
				value="${fiscalBudgetInfo.ytdInvoicedAmount}" /></td>
			<td class='alignRht'><fmt:formatNumber type="currency"
				value="${fiscalBudgetInfo.remainingAmount}" /></td>
			<td class='alignRht'><fmt:formatNumber type="currency"
				value="${fiscalBudgetInfo.amendmentAmount}" /></td>
			<td class='alignRht'><fmt:formatNumber type="currency"
				value="${fiscalBudgetInfo.proposedBudget}" /></td>
		</tr>
	</table>
	</div>
	<%-- Fiscal Year Budget Information Grid Ends --%>