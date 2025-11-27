/**
 * This Js file has functions for proposal list to fetch data
 * and perform actions on that data
 **/
suggestionVal = "";
$(document)
		.ready(
				function() {
					
					typeHeadSearch($('#procurementtitle'), $(
							"#hiddenGetProcurementListResourceUrl").val()
							+ "&QueryId=fetchProcurementTitleList", $(
							"#competitionPool").attr('id'), null, null,
							"procurementId", "getCompetitionData");
					/* This event will check for blank area.*/
					$(
							"#proposalTitle, #procurementtitle, #competitionPool, #activeProposalTo, #activeProposalFrom")
							.keyup(
									function() {
										var isbttn = true;
										if ($('#proposalTitle').val() != ''
												|| $('#procurementtitle').val() != ''
												|| $('#procurementtitle').val() != ''
												|| $('#activeProposalTo').val() != ''
												|| $('#activeProposalFrom')
														.val() != '') {
											isbttn = false;
										} else {
											$('input:checkbox').each(
													function() {
														if (!$(this).prop(
																'checked')) {
															isbttn = false;
															return -1;
														}
													});
										}
										$('#clearFilterBtn').prop('disabled',
												isbttn);
									});
										
					enableDisableDefaultFilter();
				});
/**
 * This Function will enable/disable the default filter button.
 **/

function enableDisableDefaultFilter() {
	var isbttn = true;
	if ($('#proposalTitle').val() != '' || $('#procurementtitle').val() != ''
			|| $('#competitionPool').val() != ''
			|| $('#activeProposalTo').val() != ''
			|| $('#activeProposalFrom').val() != '' || ($('#agency').val() != 'undefined' && $('#agency').val() != '')) {
		isbttn = false;
	} else {
		$('input:checkbox').each(function() {
			if (!$(this).prop('checked')) {
				isbttn = false;
				return -1;
			}
		});
	}
	$('#clearFilterBtn').prop('disabled', isbttn);
}
/**
 *  This Function will perform type Head search.
 **/
function getCompetitionData() {
	typeHeadSearch($('#competitionPool'), $("#hiddenGetCompetitionListResourceUrl").val()
			+ "&QueryId=fetchCompetitionPoolList&procurementId="
			+ $("#procurementId").val(), null, null, null, null, null);
}
/**
 * This Function will apply filter on selected fields
 * and will filter data when button is clicked.
 **/
function filtertask() {
	var startDate = new Date($("#activeProposalFrom").val());
	var endDate = new Date($("#activeProposalTo").val());
	if($('#proposalTitle').val()!='' && $('#proposalTitle').val().length<5)
		{
		$('#proposalTitle').parent().parent().find('.error').text("! You must enter 5 or more characters");
		$('#proposalTitle').parent().parent().find('.error').show();
		}
	else if (checkForDateProposalList()) {
		if (checkStartEndDatePlanned(startDate, endDate)) {
			$("#ProposalFilterForm").attr(
					"action",
					$("#ProposalFilterForm").attr("action")
							+ "&next_action=filterProposal");
			pageGreyOut();
			document.ProposalFilterForm.submit();
		} else {
			$("#activeProposalFrom").parent().next().html(
					"! End Date can not be less than Start Date.");
		}
	} 
}
/**
 *  This Function will clear filters selected.
 **/
function clearfilter() {
	if ($('#agency').html() != null) {
		$('#agency').val('All NYC Agencies');
	}
	$('#procurementtitle').val("");
	$('#competitionPool').val("");
	$('#competitionPool').prop('disabled', true);
	$('#proposalTitle').val("");
	$('#activeProposalFrom').val("");
	$('#activeProposalTo').val("");
	$('input:checkbox').each(function() {
		$(this).prop('checked', true);
	});
	$('#clearFilterBtn').prop('disabled', true);
	$("#proposalTitle").parent().next().html("");
}
/** 
 * This Function will navigate To Procurement .
 * */
function viewProcurement(procurementId) {
	$("#ProposalFilterForm").attr(
			"action",
			$("#navigateToProcurementURL").val() + "&procurementId="
					+ procurementId);
	document.ProposalFilterForm.submit();
}
/**
 *  This Function will navigate To Competition Pool URL.
 **/
function viewCompetitionPoolTitle(procurementId, evaluationGroupId,
		competitionPoolId, evalPoolMappingId) {
	$("#ProposalFilterForm").attr(
			"action",
			$("#navigateToCompetitionPoolURL").val() + "&procurementId="
					+ procurementId + "&evaluationGroupId=" + evaluationGroupId
					+ "&competitionPoolId=" + competitionPoolId
					+ "&evaluationPoolMappingId=" + evalPoolMappingId);
	document.ProposalFilterForm.submit();
}
/** 
 * This Function will show proposal details in the set format.
 **/
function viewProposalDetail(procurementId, proposalId) {
	var url = $("#hiddenViewResponse").val()
			+ "&removeMenu=asdas&jspPath=&procurementId=" + procurementId
			+ "&IsProcDocsVisible=" + true + "&proposalId=" + proposalId;
	window.open(url, 'windowOpenTab',
			'scrollbars=1,resizable=1,width=1000,height=580,left=0,top=0');
}
/**
 *  This Function will sort data when clicked on header.
 **/
function sort(columnName) {
	$("#ProposalFilterForm")
			.attr(
					"action",
					$("#ProposalFilterForm").attr("action")
							+ "&next_action=sortProposalList&sortGridName=proposalListMap"
							+ sortConfig(columnName));
	document.ProposalFilterForm.submit();
}
/** 
 * This Function is called to perform pagination
 **/
function paging(pageNumber) {
	document.ProposalFilterForm.reset();
	$("#ProposalFilterForm").attr(
			"action",
			$("#ProposalFilterForm").attr("action")
					+ "&next_action=fetchNextProposal&nextPage=" + pageNumber);
	document.ProposalFilterForm.submit();
}
/**
 * This Function will validate Date entered in filter alerts.
 **/
function checkForDateProposalList() {
	var isValid = true;
	var flag = false;
	$("input[type='text']").each(function() {
		if ($(this).attr("validate") == 'calender') {
			isValid = verifyDate(this);
			if (!isValid) {
				flag = true;
			}
		}
	});
	if (flag) {
		isValid = false;
	} else {
		isValid = true;
	}
	return isValid;
}
/**
 *  This Function will set the visibility of filter alerts.
 **/
function setVisibility(id, visibility) {
	callBackInWindow("closePopUp");
	if ($("#" + id).is(":visible")) {
		document.ProposalFilterForm.reset();
	}
	$("#" + id).toggle();
}