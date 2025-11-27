// this function is invoked on load of jsp and set default parameters
$(document).ready(
		function() {
			// on click of return to evaluation group button
			$("#returnEvaluationGroups").click(
					function() {
						$("#navigationForm").find("#forAction").eq(0).val(
								"propEval");
						$("#navigationForm").find("#topLevelFromRequest").eq(0)
								.val("ProposalsandEvaluations");
						pageGreyOut();
						document.navigationForm.submit();
					});

			// on changing group from drop down
			$("#evalGroupDropDown").change(
					function() {
						if ($(this).val() != -1) {
							$("#navigationForm").find("#forAction").eq(0).val(
									"propEval");
							$("#navigationForm").find("#topLevelFromRequest")
									.eq(0).val("ProposalsandEvaluations");
							$("#navigationForm").find("#ES").eq(0).val("0");
							$("#navigationForm").find("#evaluationGroupId").eq(
									0).val($(this).val());
							pageGreyOut();
							document.navigationForm.submit();
						}
					});
		});

// This function is used to view procurement summary for accelerator/agency
function editEvaluationSettings(competitionPoolId, evaluationGroupId,
		evaluationPoolMappingId) {
	// QC 9069 - Disable Evaluation Settings tab for RFPs in selections made, closed and cancelled statuses for read only users
	var role_observer = document.getElementById("role_current");
	
	//alert(role_current.value);
	
	if(role_observer.value == 'OBSERVER')
	{ 
		viewEvaluationStatus(competitionPoolId, evaluationGroupId,
				evaluationPoolMappingId);
	} else
	{
	
		$("#navigationForm").find("#forAction").eq(0).val("propEval");
		$("#navigationForm").find("#topLevelFromRequest").eq(0).val(
			"ProposalsandEvaluations");
		$("#navigationForm").find("#ES").eq(0).val("1");
		$("#navigationForm").find("#competitionPoolId").eq(0)
			.val(competitionPoolId);
		$("#navigationForm").find("#evaluationGroupId").eq(0)
			.val(evaluationGroupId);
		$("#navigationForm").find("#evaluationPoolMappingId").eq(0).val(
			evaluationPoolMappingId);
		pageGreyOut();
	
		document.navigationForm.submit();
	}
}
// This method is used to close submissions of proposals
function closeProposalSubmissions() {

	pageGreyOut();
	var urlAppender = $("#closeSubmissions").val() + "&closeGroup=false&buttonValue=closeSubmissions";
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		success : function(e) {
			removePageGreyOut();
			$("#submissionClose").html(e);
			$(".overlay").launchOverlay($(".alert-box-closeEvaluationTasks"),
					$(".close-submittion"), "616px", null);
		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	});
}

// This method is used to select the action dropdown in the evaluation pagae
function viewEvaluationActions(competitionPoolId, evaluationGroupId,
		evaluationPoolMappingId, selectElement) {
		
	var value = $(selectElement).val();
	if (value == "Edit Evaluation Settings") {
		editEvaluationSettings(competitionPoolId, evaluationGroupId,
				evaluationPoolMappingId);
	} else if (value == "View Evaluation Status") {
		viewEvaluationStatus(competitionPoolId, evaluationGroupId,
				evaluationPoolMappingId);
	} else if (value == "View Evaluation Results and Selections") {
		viewEvaluationResults(competitionPoolId, evaluationGroupId,
				evaluationPoolMappingId);
	}
	// Start || Changes done for Enhancement #6577 for Release 3.10.0 
	else if (value == "Cancel Competition") {
		cancelCompetition(competitionPoolId, evaluationGroupId,
				evaluationPoolMappingId, selectElement);
	}
	// End || Changes done for Enhancement #6577 for Release 3.10.0 
}
//This method is invoked on choosing view evaluation Status in the action dropdown
function viewEvaluationStatus(competitionPoolId, evaluationGroupId,
		evaluationPoolMappingId) {
	
	//alert("---viewEvaluationStatus!!");
	
	$("#navigationForm").find("#forAction").eq(0).val("propEval");
	
	$("#navigationForm").find("#render_action").eq(0).val("getEvaluationStatus");
	$("#navigationForm").find("#topLevelFromRequest").eq(0).val(
			"ProposalsandEvaluations");
	$("#navigationForm").find("#midLevelFromRequest").eq(0).val(
			"EvaluationStatus");
	$("#navigationForm").find("#ES").eq(0).val("0");
	$("#navigationForm").find("#competitionPoolId").eq(0)
			.val(competitionPoolId);
	$("#navigationForm").find("#evaluationGroupId").eq(0)
			.val(evaluationGroupId);
	$("#navigationForm").find("#evaluationPoolMappingId").eq(0).val(
			evaluationPoolMappingId);
	pageGreyOut();
	document.navigationForm.submit();
}
//This method is invoked on choosing view evaluation Results and selection in the action dropdown
function viewEvaluationResults(competitionPoolId, evaluationGroupId,
		evaluationPoolMappingId) {
	
	//alert("==viewEvaluationResults!!");
		
	$("#navigationForm").find("#forAction").eq(0).val("propEval");
	$("#navigationForm").find("#render_action").eq(0).val("fetchEvaluationResults");
	$("#navigationForm").find("#topLevelFromRequest").eq(0).val(
			"ProposalsandEvaluations");
	$("#navigationForm").find("#midLevelFromRequest").eq(0).val(
			"EvaluationResultsandSelections");
	$("#navigationForm").find("#ES").eq(0).val("0");
	$("#navigationForm").find("#competitionPoolId").eq(0)
			.val(competitionPoolId);
	$("#navigationForm").find("#evaluationGroupId").eq(0)
			.val(evaluationGroupId);
	$("#navigationForm").find("#evaluationPoolMappingId").eq(0).val(
			evaluationPoolMappingId);
	pageGreyOut();
	document.navigationForm.submit();
}

//This method is used to cancel Competition
// Added for Enhancement #6577 for Release 3.10.0 
function cancelCompetition(competitionPoolId, evaluationGroupId,
		evaluationPoolMappingId, selectElement) {
	pageGreyOut();
	var urlAppender = $("#cancelCompetition").val()+ "&competitionPoolId=" + competitionPoolId + "&evaluationGroupId=" + evaluationGroupId 
	+ "&evaluationPoolMappingId=" + evaluationPoolMappingId;
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		success : function(e) {
			removePageGreyOut();
			$(selectElement).prop('selectedIndex', 0);
			$("#cancelCompetitionOverlay").html(e);
			$(".overlay").launchOverlay($(".alert-box-cancelCompetition"),
					$(".cancel-competition"), "616px", null);
		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	});
}

// This method is used to close group submissions
function closeGroupSubmissions() {
	pageGreyOut();
	var urlAppender = $("#closeGroup").val() + "&closeGroup=true&buttonValue=closeGroupSubmissions";
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		success : function(e) {
			removePageGreyOut();
			$("#groupSubmissionClose").html(e);
			$(".overlay").launchOverlay($(".alert-box-closeGroupSubmission"),
					$(".close-group-submittion"), "616px", null);
		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	});
}

//This method is used to close all submissions
function closeAllSubmissions() {
	pageGreyOut();
	var urlAppender = $("#closeAllSubmission").val() + "&closeGroup=false&buttonValue=closeAllSubmissions";
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		success : function(e) {
			removePageGreyOut();
			$("#allSubmissionClose").html(e);
			$(".overlay").launchOverlay($(".alert-box-closeAllSubmissions"),
					$(".close-all-submittion"), "616px", null);
		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	});
}

//This will execute when any Column header on grid is clicked for sorting
function sort(columnName) {
	$("#evaluationSummaryForm")
			.attr(
					"action",
					$("#evaluationSummaryForm").attr("action")
							+ "&submit_action=sortEvaluationSummary&sortGridName=proposalEvaluationSummary"
							+ sortConfig(columnName));
	pageGreyOut();
	document.evaluationSummaryForm.submit();
}

//This will execute when Previous,Next.. is clicked for pagination
function paging(pageNumber) {
	$("#evaluationSummaryForm").attr(
			"action",
			$("#evaluationSummaryForm").attr("action")
					+ "&submit_action=paginateEvaluationSummary&nextPage="
					+ pageNumber);
	pageGreyOut();
	document.evaluationSummaryForm.submit();
}