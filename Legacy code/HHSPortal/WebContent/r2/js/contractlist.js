/**
 * ============================================
 *** This file handle the task that will render
 *** Contract list and update the task.
 *This file is updated in R7.
 * ============================================
 */
var suggestionVal = "";
/**
 * On page laod Updated Method in R4
 */
// Start Added in R5
$( "#sharedList2" ).change(function() {
	$('#btnRemove').removeAttr('disabled');
	$('#btnAdd').attr('disabled', true);
		$('#sharedList1 option:selected').removeAttr('selected');
});
$( "#sharedList1" ).change(function() {
	$('#btnAdd').removeAttr('disabled');
	$('#btnRemove').attr('disabled', true);
		$('#sharedList2 option:selected').removeAttr('selected');	
});
// End Added in R5
$(document)
		.ready(
				// hides all messages
				function() {
					//added in R7 for firefox and IE
					$('.contractInfoTitle').parent().css('word-break','break-all');
					//r7 End
					$("select")
							.change(
									function() {
										$("#errorGlobalMsg").html('');
										$("#errorGlobalMsg").hide();
										$("#successGlobalMsg").html('');
										$("#successGlobalMsg").hide();
										if (document
												.getElementById("transactionStatusDiv") != null) {
											$("#transactionStatusDiv").html('');
											$("#transactionStatusDiv").hide();
										}
										if (document
												.getElementById("transactionStatusDivError") != null) {
											$("#transactionStatusDivError")
													.html('');
											$("#transactionStatusDivError")
													.hide();
										}
									});

					disableProgramDropDown();
					// gets the program Name if the agency is valid
					$("#agency").change(function() {
						agency = $(this).prop("selectedIndex");
						if (agency == 0) {
							$("#programName").val("");
							$("#programName").prop('disabled', true);
						} else {
							getProgramNameList();
						}
					});

					$('#contractValueFrom').autoNumeric('init', {
						vMax : '9999999999999999',
						vMin : '0.00'
					});
					$('#contractValueTo').autoNumeric('init', {
						vMax : '9999999999999999',
						vMin : '0.00'
					});
					$('#amendedContractValueFrom').autoNumeric('init', {
						vMax : '9999999999999999.99',
						vMin : '-9999999999999999.99'
					});
					$('#amendedContractValueTo').autoNumeric('init', {
						vMax : '9999999999999999.99',
						vMin : '-9999999999999999.99'
					});
					$(".tableContractValue").each(function(e) {
						$(this).autoNumeric('init', {
							vMax : '9999999999999999',
							vMin : '-9999999999999999.99'
						});
					});
					var pageW = $(document).width();
					var pageH = $(document).height();
					$(".alert-boxd").hide();
					// $("#contractsValue").validateCurrencyOnLoad();
					$(".tableContractValue").each(function(e) {

						$(this).autoNumeric('init', {
							vMax : '9999999999999999',
							vMin : '-9999999999999999'
						});
					});
					$(".upload").click(function() {
						$(".addContract").show();
						$(".overlay").show();
						$(".overlay").width(pageW);
						$(".overlay").height(pageH);
					});
					// typehead for EPin
					var orgType = $("#orgType").val();

					if (orgType == "agency_org" || orgType == "city_org") {
						typeHeadSearch($('#awardEpin'), $(
								"#getEpinListResourceUrl").val()
								+ "&epinQueryId=fetchContractEpinList", null,
								"typeHeadCallBackAward", null);
						typeHeadSearch($('#provider'), $(
								"#hiddengetProviderListResourceUrl").val(),
								null, "typeHeadCallBackProvider", null);
					}

					if (orgType == "provider_org" || orgType == "agency_org"
							|| orgType == "city_org") {
						typeHeadSearch(
								$('#awardEpinAmendment'),
								$("#getEpinListResourceUrl").val()
										+ "&epinQueryId=fetchAmendContractEpinListForListScreen",
								null, "typeHeadCallBackAwardAmendment", null);
					}

					// release 3.3.0 enhancement id 6248
					if (orgType == "city_org") {
						$(".contractListDiv tr>th:nth-child(5)").addClass(
								"alignRht nowrap");
					} else {
						$(".contractListDiv tr>th:nth-child(4)").addClass(
								"alignRht nowrap");
					}
					$(".contractListDiv tr>th:nth-child(1)").addClass("nowrap");

					if (orgType == "city_org") {
						$(".amendmentListDiv tr>th:nth-child(5)").addClass(
								"alignRht");
					} else if (orgType == "agency_org") {
						$(".amendmentListDiv tr>th:nth-child(4)").addClass(
								"alignRht");
					} else {
						$(".amendmentListDiv tr>th:nth-child(5)").addClass(
								"alignRht");
					}
					// typehead for contract number
					typeHeadSearch($('#ctId'), $(
							"#getContractNoListResourceUrl").val()
							+ "&contractNoQueryId=fetchContractNoList", null,
							"typeHeadCallBackCtId", null);

					// Start Enhancement id 6400 release 3.4.0
					typeHeadSearch($('#amendCtId'), $(
							"#getContractNoListResourceUrl").val()
							+ "&contractNoQueryId=fetchAmendContractNoList",
							null, "typeHeadCallBackCtIdAmend", null);
					typeHeadSearch($('#amendCtIdCity'), $(
							"#getContractNoListResourceUrl").val()
							+ "&contractNoQueryId=fetchAmendContractNoList",
							null, "typeHeadCallBackCtIdAmendCity", null);

					// End Enhancement id 6400 release 3.4.0

					$(".tabularWrapper select")
							.change(
									function(e) {

										var str = "";
										var title = "";
										var agencyType = "";
										var contractNo = "";
										var contractVal = "";

										var contractAction = $(this).val();
										/* Start : Added in R5 */
										var contractId = $(this).attr(
												"contractid");
										var amendcontractid = $(this).attr(
										"amendcontractid");
										/* End : Added in R5 */
										// opens view contract configuration
										if (contractAction.toLowerCase() == "view contract configuration") {
											$("#hdncontractAmt").val(
													$(this).attr(
															"contractAmount"));
											$("#hdncontractStartDt")
													.val(
															$(this)
																	.attr(
																			"contractStartDate"));
											$("#hdncontractEndDt").val(
													$(this).attr(
															"contractEndDate"));
											$("#hdncontractId").val(
													$(this).attr("contractid"));
											submitFormToShowConCnfigDet();
										}// opens view amendments
										else if (contractAction.toLowerCase() == "view amendments") {
											$("#hdncontractId").val(
													$(this).attr("contractid"));
											$("#hdnstatusId").val(
													$(this).attr("statusId"));
											submitFormToViewAmendments();
										}// opens view contract cof
										// Start || Added For Enhancement 6482
										// for Release 3.8.0
										else if (contractAction.toLowerCase() == "update contract information") {
											updateContractInfo($(this).attr(
													"contractid"));
										}// End || Added For Enhancement 6482
											// for Release 3.8.0
										else if (contractAction.toLowerCase() == "view contract cof"
												|| contractAction.toLowerCase() == "view cof") {
											$("#hdncontractStartDt")
													.val(
															$(this)
																	.attr(
																			"contractStartDate"));
											$("#hdncontractEndDt").val(
													$(this).attr(
															"contractEndDate"));
											$("#hdncontractId").val(
													$(this).attr("contractid"));
											$("#hdnAmendContractId").val(
													$(this).attr(
															"amendContractId"));
											$("#hdncontractTypeId").val(
													$(this).attr(
															"contracttypeid"));

											submitFormToShowContractCOF(
													$(this).attr("contractid"),
													$(this)
															.attr(
																	"contractStartDate"),
													$(this).attr(
															"contractEndDate"),
													$(this).attr(
															"amendContractId"),
													$(this).attr(
															"contracttypeid"));
										}
										// Start : R5 Added
										else if (contractAction.toLowerCase() == "view budget" || contractAction.toLowerCase() == "view budgets") {
											submitFormToViewBudgetList(contractId,null,null);
										} else if (contractAction.toLowerCase() == "view invoices") {
											submitFormToViewInvoiceList(contractId);
										} else if (contractAction.toLowerCase() == "view payments") {
											submitFormToViewPaymentList(contractId);
										} else if (contractAction.toLowerCase() == "view contract") {
											submitFormToViewContractList(contractId);
										// Start : Added in R7 Defect 8644 Issue 3
										} else if (contractAction.toLowerCase() == "add fiscal year") {
											submitFormToAddFiscalYear(contractId);
										}
										// End : Added in R7 Defect 8644 Issue 3
										else if (contractAction.toLowerCase() == "view contract amend") {
											submitFormToViewContractList($(this)
													.attr("contractid"));
										} else if (contractAction.toLowerCase() == "view budget amend") {
											submitFormToViewBudgetList($(this)
													.attr("contractid"), "Budget Amendment",amendcontractid);
										} 
										//R7 Start: Added for Flag/Unflag Action Handling
										else if (contractAction.toLowerCase() == "unflag contract") {
											 if ($("#orgType").val() == 'city_org' || ($("#orgType").val() == 'agency_org' && ($('#agencyRole').val()=='CFO' || $('#agencyRole').val()=='PROGRAM_MANAGER' || $('#agencyRole').val()=='FINANCE_MANAGER'))) {	
										
											if($("#orgType").val() == 'agency_org')	 {
												title = $(e.target).closest(
												"tr").find("td").eq(0)
												.text();
											 	providerName = $(e.target)
												.closest("tr").find(
														"td>a").html();
											 	agencyType=$('#agencyRole').val();
											}
											else{
											title = $(e.target).closest(
												"tr").find("td").eq(1)
												.text();
											
											agencyType = $(e.target)
												.closest("tr").find(
														"td").eq(0)
												.html();
											
											 	providerName = $(e.target)
												.closest("tr").find(
														"td>a").html();
											}
										contractid=$(e.target)
										.closest("tr").find(
												"td>select").attr('contractid');
										contractmessage=$(e.target)
										.closest("tr").find(
												"td>select").attr('contractmessage');
										
										launchUnflagSubmitMessageOverlay(title,agencyType,providerName,contractid,contractmessage); 
											 }
										} 
										 else if (contractAction.toLowerCase() == "flag contract") {
											 if ($("#orgType").val() == 'city_org' || ($("#orgType").val() == 'agency_org' && ($('#agencyRole').val()=='CFO' || $('#agencyRole').val()=='PROGRAM_MANAGER' || $('#agencyRole').val()=='FINANCE_MANAGER'))) {		
												 if($("#orgType").val() == 'agency_org')	 {
														title = $(e.target).closest(
														"tr").find("td").eq(0)
														.text();
													 	providerName = $(e.target)
														.closest("tr").find(
																"td>a").html();
													 	agencyType=$('#agencyRole').val();
													}
													else{
													title = $(e.target).closest(
														"tr").find("td").eq(1)
														.text();
													
													agencyType = $(e.target)
														.closest("tr").find(
																"td").eq(0)
														.html();
													
													 	providerName = $(e.target)
														.closest("tr").find(
																"td>a").html();
													}
													contractid=$(e.target)
													.closest("tr").find(
															"td>select").attr('contractid');
													contractmessage=$(e.target)
													.closest("tr").find(
															"td>select").attr('contractmessage');
													
													launchSubmitMessageOverlay(title,agencyType,providerName,contractid,contractmessage);
											 }
											 }
										//R7 End
										else if (contractAction.toLowerCase() == "user access") {
											if ($("#orgType").val() == 'provider_org') {
												title = $(e.target).closest(
														"tr").find("td").eq(0)
														.text();
												agencyType = $(e.target)
														.closest("tr").find(
																"td").eq(1)
														.html();
												contractNo = $(e.target)
														.closest("tr").find(
																"td").eq(2)
														.html();
												contractVal = $(
														$(e.target).closest(
																"tr")
																.find("td").eq(
																		3)
																.html()).text();
											} else {
												title = $(e.target).closest(
														"tr").find("td").eq(1)
														.text();
												agencyType = $(e.target)
														.closest("tr").find(
																"td").eq(0)
														.html();
												contractNo = $(e.target)
														.closest("tr").find(
																"td").eq(3)
														.html();
												contractVal = $(
														$(e.target).closest(
																"tr")
																.find("td").eq(
																		4)
																.html()).text();
											}
											getContractSharedList(contractId,
													title, agencyType,
													contractNo, contractVal);
										}
										// End : R5 Added
										else {
											$("#ct").val($(this).attr("ct"));
											$("#amendProvider").val(
													$(this).attr("provider"));
											$("#contractAmount").val(
													$(this).attr(
															"contractAmount"));
											$("#contractStartDate")
													.val(
															$(this)
																	.attr(
																			"contractStartDate"));
											$("#contractEndDate").val(
													$(this).attr(
															"contractEndDate"));
											$("#contractid").val(
													$(this).attr("contractid"));
											/* R6:contractagencyid added in method parameter start*/
											fillAndShowOverlay(
													contractAction,
													$(this).attr("contractid"),
													$(this).attr(
															"contractTypeId"),
													$(this)
															.attr(
																	"fyConfigFiscalYear"),
													$(this).attr(
															"amendcontractid"),
													$(this).attr(
															"contracttitle"),
													$(this).attr("provider"),
													/*R7: markAsRegistered added in method parameter ends*/
													$(this).attr("markAsRegistered"),
													$(this).attr(
															"contractagencyid"));
												/* R6:contractagencyid added in method parameter ends*/
										}
										$(this).prop('selectedIndex', 0);
										$(this).blur();
									});
					$('#SaveError').hide();
					if ($("#successGlobalMsg").html() != "")
						$("#successGlobalMsg").show();
					$("#contractsValue").jqGridCurrency();
				});
/**
 *  Start : Added in R5
 * Function : form submit on select of View Budget
 * */
function submitFormToViewBudgetList(contractId, budgetType,amendcontractid) {
	var $budgetType="";
	var $amendcontractid="";
	if(budgetType != null)
	{
		$budgetType = "&budgetType="+budgetType;
	}
	if(amendcontractid != null)
	{
		$amendcontractid = "&amendcontractid=" + amendcontractid;
	}
	var formId = $('form').attr('id');
	$("#" + formId).attr(
			"action",
			$("#hiddenNavigateListScreenUrl").val()
					+ "&listAction=budgetListAction" + $amendcontractid+"&contractId=" + contractId+$budgetType);
	$("#" + formId).submit();
}
/**
 *  Function : form submit on select of View Invoices*/
function submitFormToViewInvoiceList(contractId) {
	var formId = $('form').attr('id');
	$("#" + formId).attr(
			"action",
			$("#hiddenNavigateListScreenUrl").val()
					+ "&listAction=invoiceListAction&contractId=" + contractId);
	$("#" + formId).submit();
}
/**
 *  Function : form submit on select of View Payment
*/
function submitFormToViewPaymentList(contractId) {
	var formId = $('form').attr('id');
	$("#" + formId).attr(
			"action",
			$("#hiddenNavigateListScreenUrl").val()
					+ "&listAction=paymentListAction&contractId=" + contractId);
	$("#" + formId).submit();
}

/**
 *  Function : form submit on select of View Contract
*/
function submitFormToViewContractList(contractId) {
	var formId = $('form').attr('id');
	$("#" + formId)
			.attr(
					"action",
					$("#hiddenNavigateListScreenUrl").val()
							+ "&listAction=contractListAction&contractId="
							+ contractId);
	$("#" + formId).submit();
}

// get contract list overlay
$('#SaveButton').on("click", function(e) {
	$("#sharedList1 option").attr("selected", "selected");
});
$('#btnAddAll').on("click", function(e) {
	$('#sharedList1 > option').appendTo('#sharedList2');
	$("#sharedList2 option").removeAttr('selected');
	checkEntries();
	e.preventDefault();
});

$('#btnRemoveAll').on("click", function(e) {
	$('#sharedList2 > option').appendTo('#sharedList1');
	$("#sharedList1 option").removeAttr('selected');
	checkEntries();
	e.preventDefault();
});
/**
 * This method checks when button should be enabled or disabled
 * */
function checkEntries() {
			if ($('#sharedList2 option').length != 0) {
		$('#btnRemoveAll').removeAttr('disabled');
	}
	if ($('#sharedList1 option').length == 0) {
		$('#btnAdd,#btnAddAll,#btnRemove').attr('disabled', 'disabled');
	}
	if ($('#sharedList1 option').length != 0) {
		$('#btnAddAll').removeAttr('disabled');
	}
	if ($('#sharedList2 option').length == 0) {
		$('#btnRemove,#btnRemoveAll,#btnAdd').attr('disabled', 'disabled');
	}
}
$('#btnAdd').click(function(e) {
	$('#sharedList1 > option:selected').appendTo('#sharedList2');
	$("#sharedList2 option").removeAttr('selected');
	$('#btnAdd').attr('disabled', 'disabled');
	checkEntries();
	e.preventDefault();
});

$('#btnRemove').click(function(e) {
	$('#sharedList2 > option:selected').appendTo('#sharedList1');
	$("#sharedList1 option").removeAttr('selected');
	$('#btnRemove').attr('disabled', 'disabled');
	checkEntries();
	e.preventDefault();
});
$('#cancelButtonUserAccess,#closeButtonUserAccess').on('click', function() {
	$(".overlay").closeOverlay();
	removePageGreyOut();
	return false;
});
/**
 *  End : Added in R5
 This method Submit the Contract Form
 */
function submitContractForm() {
	setVisibility('documentValuePop', 'none');
	pageGreyOut();
	document.contractFilterForm.submit();
}

/**
 *  This method set the visibility of pop up up whether it should be enable or
 disable.
 */
function setVisibility(id, visibility) {
	callBackInWindow("closePopUp");
	if ($("#" + id).is(":visible")) {
		document.contractFilterForm.reset();
	}
	$("#" + id).toggle();
	disableProgramDropDown();
}

/**
 * This will execute when Set To Default Filter button is clicked and will set
 * default values for filter Updated Method in R4
 */
function clearContractFilter() {
	$('select').find('option:first').attr('selected', 'selected');
	disableProgramDropDown();
	var actualDefaultAgency = "All NYC Agencies";
	var agencyOrCityUser = false;
	var orgType = $("#orgType").val();
	if (orgType == "agency_org" || orgType == "city_org") {
		agencyOrCityUser = true;
	}
	$("#baseContractTitle").val('');
	$("#contractTitle").val('');
	$("#provider").val('');
	$("#contractAgencyName").val(actualDefaultAgency);
	$("#programName").val('');
	$("#ctId").val('');
	$("amendCtId").val('');
	$("amendCtIdCity").val('');
	$("#awardEpin").val('');
	$("#contractValueFrom").val('');
	$("#contractValueTo").val('');
	$("#activeContractsFrom").val('');
	$("#activeContractsTo").val('');
	$("#chkPendingRegistration").attr('checked', true);
	$("#chkRegistered").attr('checked', true);
	$("#chkClosed").attr('checked', false);
	if (agencyOrCityUser) {
		$("#chkPendingCoF").attr('checked', true);
		$("#chkPendingConfig").attr('checked', true);
	} else {
		$("#chkPendingCoF").attr('checked', false);
		$("#chkPendingConfig").attr('checked', false);
	}
	$("#chkSuspended").attr('checked', false);
	$("#chkCancelled").attr('checked', false);

	$("#clearfilter").attr("disabled", true);
	$("span.error").empty();
}
/**
 *  This will execute when Previous,Next.. is clicked for pagination
 *  */
function paging(pageNumber) {
	document.contractFilterForm.reset();
	$("#contractFilterForm").attr(
			"action",
			$("#contractFilterForm").attr("action")
					+ "&next_action=fetchNextContracts&nextPage=" + pageNumber);
	document.contractFilterForm.submit();
}

/**
 *  This method submits the form on click of column header click and pass column
 *  name to sort.
 */
function sort(columnName) {
	document.contractFilterForm.reset();
	$("#contractFilterForm")
			.attr(
					"action",
					$("#contractFilterForm").attr("action")
							+ "&next_action=sortContractList&sortGridName=contractListMap"
							+ sortConfig(columnName));
	document.contractFilterForm.submit();
}

/**
 *  This will execute when Filter Button tab is clicked and displays filtered
 *	list
 */
function displayFilter() {
	var containtsNonRequiredCharacter = false;
	$('#contractFilterForm input:text').each(function() {
		if (removeNonRequiredCharacter(this)) {
			containtsNonRequiredCharacter = true;
		}

	});
	if (!containtsNonRequiredCharacter) {
		var startDate = new Date($("#activeContractsFrom").val());
		var endDate = new Date($("#activeContractsTo").val());
		if (checkForDateContractList()) {
			if (checkStartEndDatePlanned(startDate, endDate)) {
				$("#contractFilterForm").attr(
						"action",
						$("#contractFilterForm").attr("action")
								+ "&next_action=filterContracts");
				pageGreyOut();
				document.contractFilterForm.submit();

			} else {
				$("#activeContractsFrom").parent().next().html(
						"! End Date can not be less than Start Date.");
			}
		}
	}
}

/**
 *  Check for Date Format (mm-dd-yyyy)
 * */
function isDateFormat(txtDate) {
	var reg = /^(0[1-9]|1[012])([\/-])(0[1-9]|[12][0-9]|3[01])\2(\d{4})$/;
	return reg.test(txtDate);
}

/**
*  This will execute when Filter Button tab is clicked and displays filtered
*  amendment
*  list
**/
function displayFilterAmendment() {
	// Start Logic to validate Date Format
	var _DTfrom = true;
	var _DTto = true;
	if ($('#dateLastUpdateFrom').val() != ''
			&& isNaN(Date.parse($('#dateLastUpdateFrom').val())))
		_DTfrom = false;
	if ($('#dateLastUpdateTo').val() != ''
			&& isNaN(Date.parse($('#dateLastUpdateTo').val())))
		_DTto = false;
	// End Logic to validate Date Format
	// This condition accept Iff DateTo and DateFrom are valid date format
	if (_DTfrom && _DTto) {
		if (new Date($('#dateLastUpdateTo').val()) < new Date("1/1/1800")) {
			_DTto = false;
			$('#dateLastUpdateToError')
					.html(
							"! Invalid Date. Please enter a year equal to or after 1800");
		}
		if (new Date($('#dateLastUpdateFrom').val()) < new Date("1/1/1800")) {
			_DTfrom = false;
			$('#dateLastUpdateFromError')
					.html(
							"! Invalid Date. Please enter a year equal to or after 1800");
		}
		if ($('#dateLastUpdateFrom').val() != ''
				&& !isDateFormat($('#dateLastUpdateFrom').val())) {
			$('#dateLastUpdateFromError').html("! Please enter a valid date");
			_DTfrom = false;
		}
		if ($('#dateLastUpdateTo').val() != ''
				&& !isDateFormat($('#dateLastUpdateTo').val())) {
			$('#dateLastUpdateToError').html("! Please enter a valid date");
			_DTto = false;
		}
		if (_DTfrom && _DTto && checkForDate()) {
			$('#dateLastUpdateFromError').html('');
			$('#dateLastUpdateToError').html('');
			//Start 4.0.2:Updated for defect View Amendment Filter: session variable 'hdnIsViewAmendment' added for defect # 8405 and this change will fix the defect # 8406 also.
			$("#contractFilterFormAmendment")
					.attr(
							"action",
							$("#contractFilterFormAmendment").attr("action")
									+ "&next_action=filterAmendedContracts&hdnIsViewAmendment=false&hdncontractId="
									+ $("#hdncontractId").val()
									+ "&hdnstatusId=" + $("#hdnstatusId").val());
			//End 4.0.2
			pageGreyOut();
			document.contractFilterFormAmendment.submit();
		}
	}// Give error message on filter button using keyboard
	else {
		if (!_DTfrom && $('#dateLastUpdateFromError').html() == '')
			$('#dateLastUpdateFromError').html('! Please enter a valid date');

		if (!_DTto && $('#dateLastUpdateToError').html() == '')
			$('#dateLastUpdateToError').html('! Please enter a valid date');
	}
}
/**
 *  This method is changed as part of build 3.1.0, enhancement id 6020
 *	This function closes the Add Assignee overlay
 **/
function clearAndCloseOverLay() {
	$("#overlayDivId").html("");
	$(".overlay").closeOverlay();

	// Build 3.1.0, enhancement 6020, added hide method when overlay is closed
	$(".overlay").hide();
}
/**
 *  this function launches the show comments overlay
 *  R6: Updated - added contractAgencyId as an extra method parameter
 *  */
function fillAndShowOverlay(contractType, contractId, contractTypeId,
		fyConfigFiscalYear, amendcontractid, contracttitle, provider, markAsRegistered,contractAgencyId) {

	var jspName = getJspName(contractType);
	if (jspName == "")
		return;
	if (contractType == "Download for registration") {
		provider = provider.replace(/[^a-zA-Z0-9 ]/g, '');
	}
	/* R6: contractAgencyId added in v_parameter to be sent to controller*/
	var v_parameter = "jspName=" + jspName + "&contractId=" + contractId
			+ "&contractTypeId=" + contractTypeId + "&fyConfigFiscalYear="
			+ fyConfigFiscalYear + "&amendcontractid=" + amendcontractid
			+ "&contractTitle=" + contracttitle + "&provider=" + provider
			+ "&contractAgencyId=" + contractAgencyId + "&markAsRegistered=" + markAsRegistered;
	var urlAppender = $("#hiddenContractTypeOverlayPageUrl").val();
	pageGreyOut();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(e) {
			$("#overlayDivId").html(e);
			$(".overlay").launchOverlayNoClose($(".alert-box-amend-contract"),
					"850px", null, "onReady");
			$("a.exit-panel").click(function() {
				clearAndCloseOverLay();
			});
			removePageGreyOut();

		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	});
}

/**
 *  This will execute when any file is selected to upload
 *  */
function displayDocName(filePath) {
	var fullPath = filePath.value;
	var fileNameIndex = fullPath.lastIndexOf("\\") + 1;
	var filename = fullPath.substr(fileNameIndex);
	var docName = document.getElementById("hidden");
	var ext = filename.lastIndexOf(".");
	filename = filename.substr(0, ext);
	$(".alert-box").find("#docName").val(filename);
	$(".alert-box").find("#hidden").show();
	$(".alert-box").find(".docnameError").hide();
}

/**
 *  This will execute when any file is selected to upload
 *  */
function bulkUploadReturn() {
	var fileName = $("#uploadfile").val();
	var fixErrors = document.getElementById('fileSelect');
	if (fileName == "") {
		fixErrors.innerHTML = "Please select a file";
		return (true);

	}
	var options = {
		success : function(responseText, statusText, xhr) {
			var responsesArr = responseText.split("|");
			if (responsesArr[1] == "error") {
				$(".alert-box-amend-contract").show();
				$("#OverlayErrorDiv")
						.html(
								responsesArr[3]
										+ "<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('OverlayErrorDiv', this)\" />");
				$("#OverlayErrorDiv").show();
				removePageGreyOut();
			} else {
				$(".overlay").closeOverlay();
				$("#bulkUploadSuccessDiv")
						.html(
								responsesArr[3]
										+ "<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('bulkUploadSuccessDiv', this)\" />");
				$("#bulkUploadSuccessDiv").removeClass().addClass(
						responsesArr[4]);
				$("#bulkUploadSuccessDiv").show();
				removePageGreyOut();
			}
		},
		error : function(xhr, ajaxOptions, thrownError) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	};
	pageGreyOut();
	$(UploadBulkContract).ajaxSubmit(options);
	return false;

}

/**
 *  This method is displays the overlay from where we upload the bulk upload data
 *  */
function bulkUploadConfirm(contractType, contractId, contractTypeId,
		fyConfigFiscalYear, amendcontractid) {
	var jspName = getJspName(contractType);
	if (jspName == "")
		return;
	var v_parameter = "jspName=" + jspName + "&contractId=" + contractId
			+ "&contractTypeId=" + contractTypeId + "&fyConfigFiscalYear="
			+ fyConfigFiscalYear + "&amendcontractid=" + amendcontractid;
	var urlAppender = $("#hiddenContractTypeOverlayPageUrl").val();
	pageGreyOut();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(e) {
			$("#overlayDivId").html("");
			$(".overlay").closeOverlay();
			$("#overlayDivId").html(e);
			$(".overlay").launchOverlayNoClose($(".alert-box-amend-contract"),
					"675px", null, "onReady");

			$("a.exit-panel").click(function() {
				clearAndCloseOverLay();
			});
			removePageGreyOut();

		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	});
}

/**
 *  This method is used to fetch bulk upload template file properties
 *  */
function getBulkUploadConfirmData(contractType, contractId, contractTypeId,
		fyConfigFiscalYear, amendcontractid) {
	var jspName = getJspName(contractType);
	if (jspName == "")
		return;
	var v_parameter = "jspName=" + jspName + "&contractId=" + contractId
			+ "&contractTypeId=" + contractTypeId + "&fyConfigFiscalYear="
			+ fyConfigFiscalYear + "&amendcontractid=" + amendcontractid;
	var urlAppender = $("#hiddenBulkContractUploadTemplateUrl").val();
	pageGreyOut();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(e) {
			if (e == "") {
				clearAndCloseOverLay();
				removePageGreyOut();
				window.location.reload(true);
			} else {
				$("#overlayDivId").html(e);
				$(".overlay").launchOverlayNoClose(
						$(".alert-box-amend-contract"), "700px", "285px", null,
						"onReady");
				$("a.exit-panel").click(function() {
					clearAndCloseOverLay();
				});
				removePageGreyOut();
			}
		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	});
}

/**
 *  This method is used to fetch bulk upload template file
 **/
function getBulkUploadTemp(docName, docId, contextPath) {
	window.open(contextPath
			+ "/GetContent.jsp?action=displayDocument&documentId=" + docId
			+ "&documentName=" + docName);
}
/**
 *  This method is used to fetch bulk upload template file on basis of
 *  #Contractid,
 *	#contractTypeId, #fyConfigFiscalYear and #amendcontractid
 **/
function getBulkUploadResponse(contractType, contractId, contractTypeId,
		fyConfigFiscalYear, amendcontractid) {
	var jspName = getJspName(contractType);
	if (jspName == "")
		return;
	var v_parameter = "jspName=" + jspName + "&contractId=" + contractId
			+ "&contractTypeId=" + contractTypeId + "&fyConfigFiscalYear="
			+ fyConfigFiscalYear + "&amendcontractid=" + amendcontractid;
	var urlAppender = $("#hiddenBulkContractUploadTemplateUrl").val();
	pageGreyOut();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(e) {
			$("#overlayDivId").html(e);
			$(".overlay").launchOverlayNoClose($(".alert-box-amend-contract"),
					"850px", null, "onReady");
			$("a.exit-panel").click(function() {
				clearAndCloseOverLay();
			});
			removePageGreyOut();

		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	});
}
/**
 * This method is used to the Jsp name on the basis of contractType Updated
 * Method in R4
 */
function getJspName(contractType) {
	var returnJspName = "";
	switch (contractType) {
	case "Add Contract":
		returnJspName = "addContract";
		break;
	case "Renew Contract":
		returnJspName = "renewContract";
		break;
	case "Suspend Contract":
		returnJspName = "suspendContract";
		break;
	case "Unsuspend Contract":
		returnJspName = "unSuspendContract";
		break;
	case "Close Contract":
		returnJspName = "closeContract";
		break;
	case "Update Contract Configuration":
		returnJspName = "updateContractConfig";
		break;
	case "Amend Contract":
		returnJspName = "amendContract";
		break;
	case "Cancel Amendment":
		returnJspName = "cancelAmendment";
		break;
	case "Cancel Contract":
		returnJspName = "cancelContract";
		break;
	// Start || Added For Enhancement 6000 for Release 3.8.0
	case "Delete Contract":
		returnJspName = "deleteContract";
		break;
	// End || Added For Enhancement 6000 for Release 3.8.0
	case "New FY Configuration Confirmation":
		returnJspName = "fYConfigConfirmation";
		break;
	case "Download for registration":
		returnJspName = "downloadAmendmentDocuments";
		break;
	case "Bulk Upload Confirm":
		returnJspName = "bulkUploadTemplateConfirm";
		break;
	case "Bulk Upload":
		returnJspName = "bulkUpload";
		break;
	// Start || For defect 8644 part 3.
	case "Mark Amendment As Registered Confirmation":
		returnJspName = "markAsRegistered";
		break;
	// End || For defect 8644 part 3.
	}
	return returnJspName;
}
/**
 *  This funtion submits the form.
 *  */
function submitFormToShowConCnfigDet() {
	document.contractFilterForm.action = $("#view1ContractConfigReadOnly")
			.val();
	document.contractFilterForm.submit();
}
/**
 * This funtion submits the form for Amnendment Updated Method in R4
 */
function submitFormToViewAmendments() {
	document.contractFilterForm.action = $("#viewAmendmentsList").val()
			+ "&hdnIsViewAmendment=true";
	document.contractFilterForm.submit();
}
/**
 * Restriction List is updated by this method
 */
function UpdateRestrictionList() {
	pageGreyOut();
	var selectArr = [];
	$('#sharedList2 option').each(function() {
		selectArr.push($(this).val());
	});
	var v_parameter = "sharedList2=" + selectArr + "&contractId="
			+ $("#contractIdUserAssign").val();
	var urlAppender = $("#userAccessUrl").val();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(responseText) {
			if (responseText != null && "" != responseText
					&& responseText != 'null') {
				$("#ErrorDiv").html(responseText);
				$("#ErrorDiv").show();
			} else {
				clearAndCloseOverLay();
				window.location.href = $("#duplicateRender").val();
			}
			removePageGreyOut();
		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	});
}

/**
 *  Added for enhancement 6482 for Release 3.8.0
 *  */
function updateContractInfo(contractId) {
	pageGreyOut();
	var urlAppender = $("#updateContractInforOverlay").val() + "&contractId="
			+ contractId;
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		success : function(e) {
			$("#updateContractInfo").html(e);
			$(".overlay").launchOverlayNoClose(
					$(".alert-box-updateContractInfo"), "850px", null,
					"onReady");
			$("a.exit-panel").click(function() {
				clearAndCloseOverLay();
			});
			removePageGreyOut();
		},
		error : function(data, textStatus, errorThrown) {
			removePageGreyOut();
		}
	});

}

/**
 *  Added for r5 contract shared list
 *  */
function getContractSharedList(contractId, title, agencyType, contractNo,
		contractVal) {
	pageGreyOut();
	var urlAppender = $("#getContractSharedListOverlay").val() + "&contractId="
			+ contractId;
	// alert(urlAppender)
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		success : function(e) {
			$("#getContractSharedList").html(e);
			$("#titleId").text(title);
			$("#agencyType").text(agencyType);
			$("#contractNo").text(contractNo);
			$("#contractVal1").text(contractVal);
			$(".overlay").launchOverlayNoClose(
					$(".alert-box-getContractSharedList"), "700px", null,
					"onReady");
			if($('#sharedList1 > option').length>0){
				$('#btnAddAll').attr('disabled',false);
			}
			if($('#sharedList2 > option').length>0){
				$('#btnRemoveAll').attr('disabled',false);
			} 
			$("a.exit-panel").click(function() {
				clearAndCloseOverLay();
			});
			removePageGreyOut();
		},
		error : function(data, textStatus, errorThrown) {
			removePageGreyOut();
		}
	});
}
/**
 * This funtion save New Contract from Contract Shared List.
 * */
function saveNewContractSharedList(contractId) {
	pageGreyOut();
	var urlAppender = $("#updateContractRestriction").val() + "&contractId="
			+ contractId;
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		success : function(e) {
			$("#getContractSharedList").html(e);
			$("#contractId").text(contractId);
			$(".overlay").launchOverlayNoClose(
					$(".alert-box-getContractSharedList"), "700px", null,
					"onReady");
			$("a.exit-panel").click(function() {
				clearAndCloseOverLay();
			});
			removePageGreyOut();
		},
		error : function(data, textStatus, errorThrown) {
			removePageGreyOut();
		}
	});
}
/**
 * This funtion submits the form for ContractCOF Updated Method in R4
 */
function submitFormToShowContractCOF(contractID, contractSD, contractED,
		amendContractId, contractTypeId) {
	var url = $("#viewContractCOF").val() + "&hdncontractId=" + contractID
			+ "&hdncontractStartDt=" + contractSD + "&hdncontractEndDt="
			+ contractED + "&hdnAmendContractId=" + amendContractId
			+ "&hdncontractTypeId=" + contractTypeId;
	window.open(url);
}

/**
 *  This funtion fetches program name list depending upon agency selected by user
 *  */
function getProgramNameList() {
	var url = $("#getProgramListForAgency").val() + "&agencyId="
			+ $("#agency").val();
	var jqxhr = $.ajax({
		url : url,
		type : 'POST',
		cache : false,
		success : function(data) {
			if ($("#agency").prop("selectedIndex") != 0) {
				$("#programName").html(data);
				$("#programName").prop('disabled', false);
			}
		},
		error : function(data, textStatus, errorThrown) {
		}
	});
}
/**
 *  This funtion disable the drop down
 *  */
function disableProgramDropDown() {
	if (($("#orgType").val()) != 'agency_org'
			&& $("#agency").prop("selectedIndex") == 0) {
		$("#programName").prop('disabled', true);
	}
}

/**
 *  This method calls commonTypeHeadCallBack passing providername as parameter
 *  */
function typeHeadCallBackProvider() {
	commonTypeHeadCallBack($('#provider').val());
}
/**
 *  This method calls typeHeadCallBackAward passing awardEpin as parameter
 *  */
function typeHeadCallBackAward() {
	commonTypeHeadCallBack($('#awardEpin').val());
}
/**
 *  This method calls typeHeadCallBackAwardAmendment passing awardEpinAmendment
*   as parameter
**/
function typeHeadCallBackAwardAmendment() {
	commonTypeHeadCallBack($('#awardEpinAmendment').val());
}
/**
 *  This method calls typeHeadCallBackCtId passing ctId as parameter
 * */
function typeHeadCallBackCtId() {
	commonTypeHeadCallBack($('#ctId').val());
}

// Start Enhancement id 6400 release 3.4.0
/**
 *  This method calls typeHeadCallBackCtId passing amendCtId as parameter
 * */
function typeHeadCallBackCtIdAmend() {
	commonTypeHeadCallBack($('#amendCtId').val());
}
/**
 *  This method calls typeHeadCallBackCtId passing amendCtIdCity as parameter
 * */
function typeHeadCallBackCtIdAmendCity() {
	commonTypeHeadCallBack($('#amendCtIdCity').val());
}
// End Enhancement id 6400 release 3.4.0
// Start : Added in R7 Defect 8644
/**
 *  Function : form submit on select of View Contract
*/
function submitFormToAddFiscalYear(contractId) {
	pageGreyOut();
	// Serializse the form to send the form data
	var v_parameter = "&contractId=" + contractId

	var urlAppender = $("#hiddenAddFiscalYearToContractUrl").val();
	// Define the Ajax call and events
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(result) {
			clearAllErrorMsgs();
			// Check for Success message (error-0)
			if (result["error"] == 0) {
				$("#successGlobalMsg").show();
				$("#successGlobalMsg").html(result["message"]);
				clearAndCloseOverLay();
				window.location.href = $("#duplicateRender").val();
			}
			// Check for Error message to be shown on same Overlay (error-1)
			else if (result["error"] == 1)// For authorization failure
			{
				$("#errorMsg").show();
				$("#errorMsg").html(result["message"]);
			}
			// Check for Error message to be shown on landing page (error-2)
			else if (result["error"] == 2) {
				$("#errorGlobalMsg").show();
				$("#errorGlobalMsg").html(result["message"]);
				clearAndCloseOverLay();
			}
			removePageGreyOut();
		},
		error : function(data, textStatus, errorThrown) {
			$("#errorGlobalMsg").show();
			$("#errorGlobalMsg").html(textStatus);
			clearAndCloseOverLay();
		}
	});
}
// End : Added in R7 Defect 8644
// End Enhancement id 6400 release 3.4.0

/**
 * This method is added in R7.
 * This method will launch the Submit Message overlay in R7
 */
function  launchSubmitMessageOverlay(title,agencyType,providerName,contractid,contractmessage) {
	$('#messagediv').hide();
	$('#textAreaError').text('');
	$('#flaggedDescription').show();
	$('#contractSubMessageHeader').text('Flag Contract');
	$('#contractMessageHeader').text('Flag Contract');
	$('#publicCommentArea').prop('readonly','');
	$("#providerName").text(providerName);
	$('#selectedContractId').val(contractid);
	$('#submitUnflagBulk').hide();
	$('#submitBulk').show();
	$('.unflagOverlayDiv').hide();
	$('#publicCommentArea').val('');
	$('#reqiredDiv').show();
	$('div.heading>label.required').show();
	$('#unFlaggedConfirmationMessage').hide();
	$('#publicCommentArea_count').parent().show();
	var v_parameter = "contractId=" + $('#selectedContractId').val()+"&agencyType="+agencyType+"&actionSelected=flagContract";
	var urlAppender = $("#fetchMessageOverlayDetails").val();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(data,statusText, xhr) {
			if(null!=data)
			{		
				$("#agencyType").text(data.ContractList.agencyId);
				$('#publicCommentArea').val('');
				$("#titleId").text(data.ContractList.contractTitle);
				$("#ctNumber").text(data.ContractList.extCT);
				removePageGreyOut();
				//enableDictionary();
				addHighlightOnTextarea($('#publicCommentArea'));
				$('#publicCommentArea').attr('placeholder','Comments');
				$(".overlay").launchOverlay($(".alert_bulk_notification"), $(".exit-panel"), "572px", null, "onReady");
		}
		},
		error : function(data,statusText, xhr) {
			$(".overlay").closeOverlay();
			removePageGreyOut();
		}
	});

}

/**
 * This method added in R7.
 * This method will launch unflag contract overlay.
 */
function  launchUnflagSubmitMessageOverlay(title,agencyType,providerName,contractid,contractmessage) {
	$('#messagediv').hide();
	$('#reqiredDiv').hide();
	$('#textAreaError').text('');
	$('#flaggedDescription').hide();
	$('div.heading>label.required').hide();
	$('#contractSubMessageHeader').text('Unflag Contract');
	$('#contractMessageHeader').text('Unflag Contract');
	$("#providerName").text(providerName);
	$('#selectedContractId').val(contractid);
	$('#submitBulk').hide();
	$('#submitUnflagBulk').show();
	$('#publicCommentArea').prop('readonly','true');
	$('.unflagOverlayDiv').show();
	$('#unFlaggedConfirmationMessage').show();
	$('#publicCommentArea_count').parent().hide();
	var v_parameter = "contractId=" + $('#selectedContractId').val()+"&agencyType="+agencyType+"&actionSelected=unflagContract";
	var urlAppender = $("#fetchMessageOverlayDetails").val();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(data,statusText, xhr) {
			if(null!=data)
			{	
				$("#agencyType").text(data.ContractList.agencyId);
				$('#publicCommentArea').val(data.ContractList.contractMessage);
				$('#flaggedBy').text(data.ContractList.modifyBy);
				$('#flaggedDate').text(data.ContractList.dateLastUpdateTo);
				$("#titleId").text(data.ContractList.contractTitle);
				$("#ctNumber").text(data.ContractList.extCT);
				removePageGreyOut();
			$(".overlay").launchOverlay($(".alert_bulk_notification"), $(".exit-panel"), "572px", null, "onReady");
			}
		},
		error : function(data,statusText, xhr) {
			$(".overlay").closeOverlay();
			removePageGreyOut();
		}
	});
}

/**
 * This method is added in R7.
 * This method will handle flag contract action.
 */
function submitMessageoverlay(){
	if($('#publicCommentArea').val()=='' || validateComment()==false){
		if(validateComment()==false){
			$('#textAreaError').text("! Only (A-Z)(a-z), numbers (0-9), spaces and the following special characters are allowed ~ ` ! @ # $ % ^ & * ( ) _ + = -  ; : \" ' < > ? , .  /  ] [ { } ");
		}
		else{
			$('#messagediv').show();
		}
	}
	else{
	$('#messagediv').hide();	
	pageGreyOut();
	var urlAppender = $("#hiddenlaunchSubmitMessageOverlay").val();
	var commentArea=convertSpecialCharFlagContract($('#publicCommentArea').val());
	var v_parameter= "contractId="	+$('#selectedContractId').val()+"&contractMessage="+commentArea;
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(e) {
			clearAndCloseOverLay();
			removePageGreyOut();
			window.location.href = $("#duplicateRender").val();
			
		},
		error : function(data, textStatus, errorThrown) {
			clearAndCloseOverLay();
			removePageGreyOut();
		}
	});
	}
}
/**
 * This method is added in R7.
 * This method will handle the Unflag Contract Action.
 */
function  UnflagContractMessage(){
	pageGreyOut();
	var v_parameter = "contractId=" + $('#selectedContractId').val();
	var urlAppender = $("#unflagContractMessage").val();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(e) {
			clearAndCloseOverLay();
			removePageGreyOut();
			window.location.href = $("#duplicateRender").val();
		},
		error : function() {
			$(".overlay").closeOverlay();
			removePageGreyOut();
		}
	});
}

/**
 * This method set the boolean flag if any field change take place in R7
 */
function setChangeFlag(){
	isFieldchange = true;
}
/**
 * Method added in R7 to display Contracts only which are flagged.
 */
function displayFilteredContracts(){
	$("#contractFilterForm")
	.attr(
			"action",
			$("#contractFilterForm").attr("action")
					+ "&next_action=filterflaggedContracts");
	pageGreyOut();
	document.contractFilterForm.submit();
	$('#displayAll').show();
}
/**
 * Method added in R7 to display all the Contract.
 */
function displayAll(){
	//clearContractFilter();
	displayFilter();
}

/***
 * This method added in R7.
 * This method will convert special characer Hash and Ampersand
 */
function convertSpecialCharFlagContract(str){
	if(typeof(str)!="undefined" && trim(str)!=""){
	str= str.replace(/\&/g,'%26');
	str= str.replace(/\#/g,'%23');
	}
	return str;
}

