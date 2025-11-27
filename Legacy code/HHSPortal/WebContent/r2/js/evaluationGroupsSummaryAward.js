//this method opens competition pool level summary screen for selected evaluation group
function openEvaluationGroupAward(evaluationGroupId) {
	$("#navigationForm").find("#forAction").eq(0).val("awardContract");
	$("#navigationForm").find("#topLevelFromRequest").eq(0).val(
			"AwardsandContracts");
	$("#navigationForm").find("#ES").eq(0).val("0");
	$("#navigationForm").find("#render_action").eq(0).val("awardsAndContracts");
	$("#navigationForm").find("#evaluationGroupId").eq(0)
			.val(evaluationGroupId);
	pageGreyOut();
	document.navigationForm.submit();
}

//This will execute when any Column header on grid is clicked for sorting
function sort(columnName) {
	$("#evaluationGroupsSummaryAwardForm")
			.attr(
					"action",
					$("#evaluationGroupsSummaryAwardForm").attr("action")
							+ "&submit_action=sortEvaluationGroupSummary&sortGridName=evaluationGroupSummary"
							+ sortConfig(columnName));
	pageGreyOut();
	document.evaluationGroupsSummaryAwardForm.submit();
}

//This will execute when Previous,Next.. is clicked for pagination
function paging(pageNumber) {
	$("#evaluationGroupsSummaryAwardForm").attr(
			"action",
			$("#evaluationGroupsSummaryAwardForm").attr("action")
					+ "&submit_action=paginateAwardContractsSummary&nextPage="
					+ pageNumber);
	pageGreyOut();
	document.evaluationGroupsSummaryAwardForm.submit();
	
}