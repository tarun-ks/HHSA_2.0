//this method opens competition pool level details of the chosen group
function openEvaluationGroup(evaluationGroupId) {
	$("#navigationForm").find("#forAction").eq(0).val("propEval");
	$("#navigationForm").find("#topLevelFromRequest").eq(0).val(
			"ProposalsandEvaluations");
	$("#navigationForm").find("#ES").eq(0).val("0");
	$("#navigationForm").find("#evaluationGroupId").eq(0)
			.val(evaluationGroupId);
	pageGreyOut();
	document.navigationForm.submit();
}

// This will execute when any Column header on grid is clicked for sorting
function sort(columnName) {
	$("#groupSummaryForm")
			.attr(
					"action",
					$("#groupSummaryForm").attr("action")
							+ "&submit_action=sortGroupProposalSummary&sortGridName=groupProposalSummary"
							+ sortConfig(columnName));
	pageGreyOut();
	document.groupSummaryForm.submit();
}

//This will execute when Previous,Next.. is clicked for pagination
function paging(pageNumber) {
	$("#groupSummaryForm").attr(
			"action",
			$("#groupSummaryForm").attr("action")
					+ "&submit_action=paginateGroupProposalSummary&nextPage="
					+ pageNumber);
	pageGreyOut();
	document.groupSummaryForm.submit();
}