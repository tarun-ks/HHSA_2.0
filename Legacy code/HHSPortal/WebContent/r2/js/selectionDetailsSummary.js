//This method sorts the column Name
function sort(columnName) {
	$("#selectionDetailsSummaryForm")
			.attr(
					"action",
					$("#selectionDetailsSummaryForm").attr("action")
							+ "&submit_action=sortSelectionDetailsSummary&sortGridName=selectionDetailsSummary"
							+ sortConfig(columnName));
	pageGreyOut();
	document.selectionDetailsSummaryForm.submit();
}

// This will execute when Previous,Next.. is clicked for pagination
function paging(pageNumber) {
	$("#selectionDetailsSummaryForm")
			.attr(
					"action",
					$("#selectionDetailsSummaryForm").attr("action")
							+ "&submit_action=paginateSelectionDetailsSummary&nextPage="
							+ pageNumber);
	pageGreyOut();
	document.selectionDetailsSummaryForm.submit();

}
//This method will display selection details 
function viewSelectionDetails(evaluationPoolMappingId, procurementId) {
	$("#navigationForm").find("#forAction").eq(0).val("selectionDetail");
	$("#navigationForm").find("#render_action").eq(0).val("viewSelectionDetails");
	$("#navigationForm").find("#topLevelFromRequest").eq(0).val("SelectionDetails");
	$("#navigationForm").find("#midLevelFromRequest").eq(0).val("SelectionDetailsScreen");
	$("#navigationForm").find("#evaluationPoolMappingId").eq(0).val(evaluationPoolMappingId);
	$("#navigationForm").find("#procurementId").eq(0).val(procurementId);
	pageGreyOut();
	document.navigationForm.submit();
}
// On ready of the Document
$(document).ready(function() {
	$('td:nth-child(4)').autoNumeric('init', {
		vMax : '9999999999999999',
		vMin : '0.00'
	});
});