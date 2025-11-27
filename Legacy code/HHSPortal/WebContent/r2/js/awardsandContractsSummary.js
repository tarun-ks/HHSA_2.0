//this method opens competition pool level summary screen for selected evaluation group
function openAwardAndContract(evaluationPoolMappingId) {
	$("#navigationForm").find("#forAction").eq(0).val("awardContract");
	$("#navigationForm").find("#topLevelFromRequest").eq(0).val(
			"AwardsandContracts");
	$("#navigationForm").find("#midLevelFromRequest").eq(0).val(
			"AwardsandContractsScreen");
	$("#navigationForm").find("#ES").eq(0).val("1");
	$("#navigationForm").find("#render_action").eq(0).val("awardsAndContracts");
	$("#navigationForm").find("#evaluationPoolMappingId").eq(0).val(
			evaluationPoolMappingId);
	pageGreyOut();
	document.navigationForm.submit();
}

// This will execute when any Column header on grid is clicked for sorting
function sort(columnName) {
	$("#awardsandContractsSummaryForm")
			.attr(
					"action",
					$("#awardsandContractsSummaryForm").attr("action")
							+ "&submit_action=sortAwardContractsSummary&sortGridName=awardContractsSummary"
							+ sortConfig(columnName));
	pageGreyOut();
	document.awardsandContractsSummaryForm.submit();
}

// This will execute when Previous,Next.. is clicked for pagination
function paging(pageNumber) {
	$("#awardsandContractsSummaryForm").attr(
			"action",
			$("#awardsandContractsSummaryForm").attr("action")
					+ "&submit_action=paginateAwardContractsSummary&nextPage="
					+ pageNumber);
	pageGreyOut();
	document.awardsandContractsSummaryForm.submit();
}

// this function is invoked on load of jsp and set default parameters
$(document).ready(
		function() {
			// on click of return to evaluation group button
			$("#returnEvaluationGroupsAward").click(
					function() {
						$("#navigationForm").find("#forAction").eq(0).val(
								"awardContract");
						$("#navigationForm").find("#topLevelFromRequest").eq(0)
								.val("AwardsandContracts");
						$("#navigationForm").find("#render_action").eq(0).val(
								"awardsAndContracts");
						pageGreyOut();
						document.navigationForm.submit();
					});

			// on changing group from drop down
			$("#evalGroupDropDown").change(
					function() {
						if ($(this).val() != -1) {
							$("#navigationForm").find("#forAction").eq(0).val(
									"awardContract");
							$("#navigationForm").find("#topLevelFromRequest")
									.eq(0).val("AwardsandContracts");
							$("#navigationForm").find("#render_action").eq(0)
									.val("awardsAndContracts");
							$("#navigationForm").find("#ES").eq(0).val("0");
							$("#navigationForm").find("#evaluationGroupId").eq(
									0).val($(this).val());
							pageGreyOut();
							document.navigationForm.submit();
						}
					});
		});
