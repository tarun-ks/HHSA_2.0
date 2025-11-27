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
				<th class='centerAlign' title='FY Budget - Total of All Payments including Advances'>Cash Balance</th>
				<th class='centerAlign' title='(Advances Disbursed - Advance Recoupment Amounts). The Advance Recoupment Amount accounts for recoupments added to invoices in Pending Approval and Approved status. The amount may change as the Agency completes invoice reviews.'>Unrecouped Advance Amount</th>
			</tr>
			<tr>
				<td><label><fmt:formatDate pattern="MM/dd/yyyy"
				value="${fiscalBudgetInfo.startDate}" /></label></td>
				<td><label><fmt:formatDate pattern="MM/dd/yyyy"
				value="${fiscalBudgetInfo.endDate}" /></label></td>
				<td><label><fmt:formatNumber type="currency"
				value="${fiscalBudgetInfo.approvedBudget}" /></label></td>
				<td><label><fmt:formatNumber type="currency"
				value="${fiscalBudgetInfo.ytdInvoicedAmount}" /></label></td>
				<td><label><fmt:formatNumber type="currency"
				value="${fiscalBudgetInfo.remainingAmount}" /></label></td>
				<td><label><fmt:formatNumber type="currency"
				value="${fiscalBudgetInfo.ytdActualPaid}" /></label></td>
				<td><label><fmt:formatNumber type="currency"
				value="${fiscalBudgetInfo.cashBalance}" /></label></td>
				<td><label><fmt:formatNumber type="currency"
				value="${fiscalBudgetInfo.unRecoupedAmount}" /></label></td>
			</tr>		
		</table>
	</div>
<%-- Fiscal Year Budget Information Grid Ends --%>	

<div>&nbsp;</div>

<%-- Invoice Information Starts --%>		
<h3>Invoice Information</h3>
	<div class="formcontainer paymentFormWrapper">
		<div class="row">
		  <span class="label">Invoice Number:</span>
		  <span class="formfield">${invoiceInfo.invoiceNumber}</span>
		</div>
		<div class="row">
		  <span class="label" title="This field is available for Providers to tag this invoice with their own internal invoice number. This field is optional.">Provider Invoice Number:</span>
		 <span class="formfield">
	  			<input name="prvInvNum" id="prvInvNum" maxlength="20" type="text" disabled="disabled" value="${invoiceInfo.invoiceProvider}" title="This field is available for Providers to tag this invoice with their own internal invoice number. This field is optional." />
		  </span>
		</div>
		<div class="row">
		  <span class="label">Service Date From:</span>
		  <span class="formfield"><input id="invStartDate" name="invStartDate" disabled="disabled" type="text" class='date' value="${invoiceInfo.invoiceStartDate}"/>
		  <img src="../framework/skins/hhsa/images/calender.png"
		class="imgclassPlanned" title="Planned RFP Release Date" 
		onclick="return false;">
		  </span>
		</div>
		<div class="row">
		  <span class="label">Invoice Submission Date:</span>
		  <span class="formfield">
	  			<c:if test="${contractInfo.budgetStatus ne 'Pending for submission'}">
		  			${invoiceInfo.invoiceDateSubmitted}
	  		  	</c:if>
				<c:if test="${contractInfo.budgetStatus eq 'Pending for submission'}">
				  	N/A
				</c:if>
		  </span>
		</div>
	</div>

	<div class="formcontainer paymentFormWrapper">
	<div class="row">
		  <span class="label clearLabel">&nbsp;</span>
		  <span class="formfield"></span>
		</div>
		<div class="row">
		  <span class="label" title="This field is available for Agencies to tag this invoice with their own internal invoice number.  This field is optional.">Agency Invoice Number:</span>
		  <span class="formfield" title="This field is available for Agencies to tag this invoice with their own internal invoice number.  This field is optional.">
		   <input id="invoiceNumber" 
	        name="invoiceNumber" maxlength="20" type="text" value="${invoiceInfo.agency}"  onchange="setFieldChangeFlag();" />
	        </span><span id="invoiceNumberSpan" class="error"></span>
		</div>
		<div class="row">
		  <span class="label">Service Date To:</span>
		  <span class="formfield"><input id="invEndDate" name="invEndDate" disabled="disabled" type="text" class='date' value="${invoiceInfo.invoiceEndDate}" />
		  <img src="../framework/skins/hhsa/images/calender.png"
		class="imgclassPlanned" title="Planned RFP Release Date"
		onclick="return false;"></span>
		</div>
		<div class="row">
		  <span class="label">Invoice Approved Date:</span>
		  <span class="formfield">
		  		<c:choose>
		  		<c:when test="${contractInfo.budgetStatus eq 'Approved'}">
		  			${invoiceInfo.invoiceDateApproved}
		  		</c:when>
		  		<c:otherwise>
		  			N/A
		  		</c:otherwise>
		  </c:choose>
		  </span>
		</div>
	</div>

<p>&nbsp;</p>	
	<div class="tabularWrapper" > 
	<table id="assignAdvanceTable" cellspacing="0" cellpadding="0" border='1' style='width:50%; margin:auto; float:none'> 
		   <tr>
				<th class='right'>Description</th>
				<th class='right'>Amount</th>
		  </tr>                
		  <tr>
			<td><label><b>Invoice Total</b></label></td>
			 <td><label id="invoiceTotal"><b>${invoiceInfo.invoiceValue}</b></label></td>
		  </tr>
		 <tr>
			<td><label>Assignment Total</label></td>
			<td><label id="assignmentTotal">${invoiceInfo.assignmentValue}</label></td>
		  </tr>
		  <tr>
			<td><label>Advance Recoupment Total</label></td>
			<td><label id="advanceRecoup">${invoiceInfo.advanceValue}</label></td>
		  </tr>
		   <tr>
			<td><label><b>Total Proposed Payment to Vendor</b></label></td>
			 <td><label id="totalPayment">${invoiceInfo.totalValue}</label></td>
		  </tr>
		</table>

	 </div>
<script>
$(document)
.ready(
		function() {
			$("#invoiceTotal>b").jqGridCurrency();
			$("#assignmentTotal").jqGridCurrency();
			$("#advanceRecoup").jqGridCurrency();
			$("#totalPayment").jqGridCurrency();
		});

</script>