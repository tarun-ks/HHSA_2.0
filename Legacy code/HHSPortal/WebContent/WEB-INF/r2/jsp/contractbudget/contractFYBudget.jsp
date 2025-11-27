<%-- This jsp implements the functionality of showing Contract Budget Fiscal Year Budget Information--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%-- The ContractFYBudget page gives an overview of the 
 fiscal year budget information--%>
 
<%-- Fiscal Year Budget Information Starts --%>
<h3>Fiscal Year Budget Information</h3>
<div class='tabularWrapper' id="contractFYBudget">
<table cellspacing="0" cellpadding="0" class="grid">
	<tr>
		<th>Start Date</th>
		<th>End Date</th>
		<th class='alignCenter'>FY Budget</th>
		<th class='alignCenter' title="Total of Submitted and Approved Invoices">YTD Invoiced Amount</th>
		<th class='alignCenter' title="FY Budget - YTD Invoiced Amount">Remaining Amount</th>
		<th class='alignCenter' title="Total of all Disbursed Payments">YTD Actual Paid Amount</th>
		<th class='alignCenter'
				title="(Advances Disbursed - Advance Recoupment Amounts). The Advance Recoupment Amount accounts for recoupments added to invoices in Pending Approval and Approved status. The amount may change as the Agency completes invoice reviews.">Unrecouped
				Advance Amount</th>
		</tr>
	<tr>
		<td><fmt:formatDate pattern="MM/dd/yyyy"
			value="${fiscalBudgetInfo.startDate}" /></td>
		<td><fmt:formatDate pattern="MM/dd/yyyy"
			value="${fiscalBudgetInfo.endDate}" /></td>
		<td class='alignRht'><fmt:formatNumber type="currency"
			value="${fiscalBudgetInfo.approvedBudget}" /></td>
		<td class='alignRht'><fmt:formatNumber type="currency"
			value="${fiscalBudgetInfo.invoicedAmount}" /></td>
		<td class='alignRht'><fmt:formatNumber type="currency"
			value="${fiscalBudgetInfo.remainingAmount}" /></td>
		<td class='alignRht'><fmt:formatNumber type="currency"
			value="${fiscalBudgetInfo.ytdActualPaid}" /></td>
		<td class='alignRht'><fmt:formatNumber type="currency"
			value="${fiscalBudgetInfo.unRecoupedAmount}" /></td>
	</tr>
</table>
</div>
<%-- Fiscal Year Budget Information Ends --%>
