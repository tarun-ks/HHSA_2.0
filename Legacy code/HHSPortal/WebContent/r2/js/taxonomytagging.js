//global variables
var currentId = null;
var idsGenerated = "0";
var suggestionVal = "";
var formValue = "";
var asProposalTitleGlobal, asProviderNameGlobal, asProcurementTitleGlobal, asProposalIdGlobal, asProcurementIdGlobal, asContractIdGlobal, asPreviousModifiers;
var ContractIdBulk = new Array();
var ProposalIdBulk = new Array();
var ProcurementIdBulk = new Array();
var keySeparator = "k3yv@lu3S3p@r@t0r";
/* onload method of the screen
Updated Method in R4*/
$(document)
		.ready(

				// for competition pool typehead 
				function() {
					if($("#competitionPoolTitle").val() == "" && $("#procurementId").val() != "P"){
						$("#competitionPoolTitle").attr("disabled", true);
					}else{
						$('#competitionPoolTitle').unbind().keyup(function(e){
							if(e.keyCode!=37 && e.keyCode!=38 && e.keyCode!=39 && e.keyCode!=40)
								replaceAllExceptAllowedChar(this);
						}).focusout(function(){
							replaceAllExceptAllowedChar(this);
						});
						typeHeadSearch($('#competitionPoolTitle'), $("#hiddenFetchTypeAheadNameList")
								.val()+ "&QueryId=fetchCompetitionPoolTitleList&key=COMPETITION_POOL_ID&value=COMPETITION_POOL_TITLE&procurementTitle="+$("#procurementContractTitle").val(), null, null, null, "competitionPoolId");
					}
					
					typeHeadSearch($('#procurementContractTitle'), $("#hiddenFetchTypeAheadNameList").val()+ "&QueryId=fetchProcurementContractTitleList&key=PROCUREMENT_TYPE&value=PROCUREMENT_TITLE", 
							"competitionPoolTitle", null, null, "procurementId", "callBackProcurementTitleOnSelect");
					$('#procurementContractTitle').keyup(function(evt) {
							if (suggestionVal.length > 0 && 3 <= $('#procurementContractTitle').val().length)
							var keyCode = evt ? (evt.which ? evt.which : evt.keyCode)
									: event.keyCode;
							if (keyCode != 13) {
								$("#procurementId").val("");
								$("#competitionPoolTitle").val("");
								$("#competitionPoolId").val("");
							}
						});
					

					
					formValue = $("#taxonomytaggingform").serializeArray();
					enableDisableDefaultFilter(true);
					$("#documentValuePop").find("input[type='text']").blur(
							function() {
								enableDisableDefaultFilter(false);
							});
					$("#documentValuePop").find("input[type='checkbox'],select")
							.change(function() {
								enableDisableDefaultFilter(false);
							});
					$("#documentValuePop").find(
							"input:radio[name=selectedTaxonomy]").change(
							function() {
								enableDisableDefaultFilter(false);
							});
					typeHeadSearch($("#providerName"), $("#contextPathSession").val() + '/AutoCompleteServlet.jsp');
					$("a[type]").click(
							//function for proposal and provider links and saving the selected in the hidden fields
							function() {
								var proposalId = $(this).parent().parent().find(
										"#hiddenProposalId").eq(0).val();
								var procurementId = $(this).parent().parent().find(
										"#hiddenProcurementId").eq(0).val();
								var orgId = $(this).parent().parent().find(
										"#hiddenOrganizationId").eq(0).val();
								var contractId = $(this).parent().parent().find(
										"#hiddenContractId").eq(0).val();
								var type = $(this).attr("type");
								if (type == "proposalLink") {
									var urlAppend = "&proposalId="+proposalId+"&procurementId="+procurementId+"&removeMenu=adas";
									window.open($("#hiddenViewProposalSummary").val()+urlAppend,
											'windowOpenTab',
									'scrollbars=1,resizable=1,width=1000,height=580,left=0,top=0');
								} else if (type == "providerLink") {
									var urlAppend = "&organizationId="+orgId;
									window.open($("#hiddenOrganizationSummary").val()+urlAppend,
											'windowOpenTab',
									'scrollbars=1,resizable=1,width=1000,height=580,left=0,top=0');
								}  else if (type == "editTag") {
									var proposalTitle = $(this).parent().find("#hiddenProposalTitle").eq(0).val();
									var providerName = $(this).parent().find("#hiddenProviderName").eq(0).val();
									var contractTitle = $(this).parent().find("#hiddenContractTitle").eq(0).val();
									editTaxonomyTagClicked(proposalTitle, providerName, contractTitle, proposalId, procurementId, contractId);
								}
							});
					//Opens Add tags in bulk 
					$("#addNewTagsBulk").click(function() {
						addNewTaxPopUpRender(false, "true");
					});//opens remove tags in bulk
					$("#removeAllTagsBulk").click(function() {
						removeAllTaxPopUpRender();
					});
					$("#tagAllSelectedProposals").click(function() {
						tagAllSelectedProposals();
					});
					
					/* BEGIN  QC 6523 Release 3.7.0 */
					disableProgramDropDown();
					//gets the  program Name if the agency is valid
					$("#agency").change(function() {
						agency = $(this).prop("selectedIndex");
						if (agency == 0) {
							$("#programName").val("");
							$("#programName").prop('disabled', true);
						} else {
							getProgramNameList();
						}
					});
					
					// type-ahead for Award EPIN
					typeHeadSearch($('#awardEpin'), $("#getEpinListResourceUrl").val() + "&epinQueryId=fetchContractEpinList",null,"typeHeadCallBackAward",null);
					/* END  QC 6523 Release 3.7.0 */
					

				});
/*This method is invoked to display the service selection pop up 
New Method in R4*/
function unHideServiceSelection() {
	$(".addNewTagInBulkPopUpDiv").hide();
	$("#serviceSelectionId").show();
	$(".serviceSelectionGrid").hide();
}
/*This method is invoked to display the add new tags pop up
New Method in R4*/
function hideServiceSelection() {
	$("#serviceSelectionId").hide();
	$(".addNewTagInBulkPopUpDiv").show();
	$(".serviceSelectionGrid").show();
}
/*This method stores the selected Ids in an array for individual tagging 
Updated Method in R4*/
function selectContractProposal(asProposalId, asProcurementId, asContractId) {
	pageGreyOut();
	asPreviousModifiers = "";
	ContractIdBulk = new Array();
	ProposalIdBulk = new Array();
	ProcurementIdBulk = new Array();
	ProposalIdBulk.push(asProposalId);
	ProcurementIdBulk.push(asProcurementId);
	ContractIdBulk.push(asContractId);
}
/*This method is stores the selected Ids in an array for bulk tagging
New Method in R4*/
function selectContractProposalInBulk() {
	pageGreyOut();
	asPreviousModifiers = "";
	ContractIdBulk = new Array();
	ProposalIdBulk = new Array();
	ProcurementIdBulk = new Array();
	$.each($('#taxonomyTaggingTable input[name="check"][type="checkbox"]:checked'), function(
			key, value) {
		var data = $(value).attr("value");
		var arr = data.split('_');
		ProposalIdBulk.push(arr[0]);
		ProcurementIdBulk.push(arr[1]);
		ContractIdBulk.push(arr[2]);
	});
}
/*This method is invoked by clicking on  Remove tag(s) in bulk and launches its overlay
New Method in R4*/
function removeAllTaxPopUpRender() {
	selectContractProposalInBulk();
	pageGreyOut();
	var url = $("#hiddenRemoveAllTaxonomyTagPopUpInBulk").val()
			+ "&proposalId=" + ProposalIdBulk + "&procurementId="
			+ ProcurementIdBulk + "&contractId=" + ContractIdBulk;
	var jqxhr = $.ajax({
		url : url,
		type : 'POST',
		cache : false,
		success : function(response) {
			removePageGreyOut();
			if (response != null || response != '') {
				$("#removeAllTaxonomyTaggingDiv").html(response);
			}
			$(".overlay").launchOverlay($(".alert-box-removeAllTaxonomyTag"), $(".exit-panel.removeall-exit"), "650px");
		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	});

}


/*This method is invoked on click of 'Yes, remove ALL tags from the selected proposals' button and removes the tags
New Method in R4*/
function removeAllTaxRender() {
	pageGreyOut();
	var sContractIdBulk="";
	var sProposalIdBulk ="";
	var sProcurementIdBulk ="";
	for(var i=0;i<ContractIdBulk.length;i++)
	{
		sContractIdBulk = sContractIdBulk + ContractIdBulk[i] + ",";
		sProposalIdBulk = sProposalIdBulk + ProposalIdBulk[i] + ","; 
		sProcurementIdBulk = sProcurementIdBulk +  ProcurementIdBulk[i] + ",";
	}
	$("#hiddenContractIdBulk").val(sContractIdBulk);
	$("#hiddenProposalIdBulk").val(sProposalIdBulk);
	$("#hiddenProcurementIdBulk").val(sProcurementIdBulk);
	$("#editTaxonomyTaggingForm").find("#submit_action").val("removeAllTaxonomyTagUrlBulk");
	$("#editTaxonomyTaggingForm").submit();
}
/*This method is invoked on click of 'Add tags in bulk' and launches its overlay
New Method in R4*/
function addNewTaxPopUpRender(showService, onFirstLoad) {
	$("#popup1MessageDiv").hide();
	selectContractProposalInBulk();
	editTaxonomyTagClickedInBulk(showService, null, null, null, onFirstLoad);
}
/* this method is used to apply required style to the table	
Updated Method in R4*/
function applyStyle(tableId)
{
	$("#"+tableId+" tr:visible:odd td").removeClass("oddRows").addClass('evenRows');
	$("#"+tableId+" tr:visible:even td").removeClass("evenRows").addClass('oddRows');
	$("#"+tableId+" tr:eq(0)").removeClass().addClass('heading');
	$("#"+tableId+" tr:eq(0) td").removeClass().css("font-weight","Bold");
}
/*This method displays the selected services on the screen
New Method in R4*/
function editTaxonomyTagClickedInBulk(showService, elementId, linkageBranchId,
		taxonomyTaggingId, onFirstLoad) 
{
	$("#popup1MessageDiv").hide();
	$("#columnCheckBox:checked").each(function(){
		var propTitle = $(this).parent().next().text();
		var provTitle = $(this).parent().next().next().text();
		var procTitle = $(this).parent().next().next().next().text();
		var e = $("<tr><td class='prop'></td><td class='prov'></td><td class='proc'></td></tr>");
		$("#headerTableTaxonomy").append(e);
		e.find(".prop").html(propTitle);
		e.find(".prov").html(provTitle);
		e.find(".proc").html(procTitle);
	});
	openEditPopup1();
}
/*This method launches the overlay for edit tags screen
New Method in R4*/
function openEditPopup1(){
	$(".overlay").launchOverlayNoClose($(".alert-box-editTaxonomyTag"), "850px");
	applyStyle("headerTableTaxonomy");
	applyStyle("serviceFunctionList");
	removePageGreyOut();
	cancelButtonFunctionality();
	$(".ttpopup-close").unbind("click").click(function(e){
		e.preventDefault();
		var $self=$(this);
		if(!portion2P05()){
			$('<div id="dialogBox"></div>').appendTo('body')
			.html('<div><h6>You have unsaved data. <br />If you would like to leave this screen without saving your data, click <b>OK</b>. <br/>If you would like to save your data, click <b>Cancel</b> and save your data.</h6></div>')
			.dialog({
				modal: true, title: 'Unsaved Data', zIndex: 10000, autoOpen: true,
				width: 'auto', modal: true, resizable: false, draggable:false,
				dialogClass: 'dialogButtons',
				buttons: {
					OK: function () {
						if($("#secondScreenPortion").is(":visible")){
							closeSecondPortion();
						}else{
							document.location = $self.attr('href');
						}
						$(this).dialog("close");
					},
					Cancel: function () {
						$(this).dialog("close");
					}
				},
				close: function (event, ui) {
					$(this).remove();
				}
			});
			$("div.dialogButtons div button:nth-child(2)").find("span").addClass("graybtutton");
		}else{
			if($("#secondScreenPortion").is(":visible")){
				closeSecondPortion();
			}else{
				document.location = $self.attr('href');
			}
		}
	});
}
/* this method is invoked on clicking edit tag link and launches it overlay
Updated Method in R4*/
function editTaxonomyTagClicked(proposalTitle, providerName, contractTitle, asProposalId, asProcurementId, asContractId,
		saveFlag, deleteFlag) {
	asProposalIdGlobal = asProposalId;
	asProcurementIdGlobal = asProcurementId;
	asContractIdGlobal = asContractId;
	pageGreyOut();
	if (saveFlag) {
		var url = $("#hiddenEditTaxonomyTagging").val() + "&proposalTitle="
				+ escape(asProposalTitle) + "&providerName="
				+ escape(asProviderName) + "&procurementContractTitle="
				+ escape(asProcurementTitle) + "&proposalId=" + asProposalId
				+ "&procurementId=" + asProcurementId + "&contractId="
				+ asContractId + "&success=success";
	} else if (deleteFlag) {
		var url = $("#hiddenEditTaxonomyTagging").val() + "&proposalTitle="
				+ escape(asProposalTitle) + "&providerName="
				+ escape(asProviderName) + "&procurementContractTitle="
				+ escape(asProcurementTitle) + "&proposalId=" + asProposalId
				+ "&procurementId=" + asProcurementId + "&contractId="
				+ asContractId + "&delete=delete";
	} else {
		selectContractProposal(asProposalId, asProcurementId, asContractId);
		var url = $("#hiddenEditTaxonomyTagging").val() + "&proposalId=" + asProposalId
				+ "&procurementId=" + asProcurementId + "&contractId=" + asContractId;
		var jqxhr = $.ajax({
			url : url,
			type : 'POST',
			cache : false,
			success : function(response) {
				if (response != null || response != '') {
					$("#popup1MessageDiv").hide();
					var e = $("<tr><td class='prop'></td><td class='prov'></td><td class='proc'></td></tr>");
					$("#headerTableTaxonomy").append(e);
					e.find(".prop").html(proposalTitle);
					e.find(".prov").html(providerName);
					e.find(".proc").html(contractTitle);
					$("#serviceFunctionList").append(response);
					showHideBlankRow();
					openEditPopup1();
					previousServiceSelection = $("#serviceFunctionList").html();
					var url = $("#hiddenEditTaxonomyTaggingInBulk").val();
					var jqxhr = $.ajax({
						url : url,
						type : 'POST',
						cache : false,
						success : function(response) {
							if (response != null || response != '') {
								$("#secondScreenPortion").html(response);
								ddtreemenu.createTree("treemenu", true);
								ddtreemenu.flatten('treemenu', 'contact');
								ddtreemenu.openFirstLevel('treemenu', 'expand');
								selectedServiceFunctionDropDown();
								$("#exisingLinkageItems").focus(function() {
									enableDisableRemoveButton();
								}).change(function() {
									enableDisableRemoveButton();
								});
								cancelButtonFunctionality();
							}
							removePageGreyOut();
						},
						error : function(data, textStatus, errorThrown) {
							showErrorMessagePopup();
							removePageGreyOut();
						}
					});
				}else{
					showErrorMessagePopup();
					removePageGreyOut();
				}
			},
			error : function(data, textStatus, errorThrown) {
				showErrorMessagePopup();
				removePageGreyOut();
			}
		});
	}
}

// this method generates the taxonomy tree
function taxonomyTreeGeneration() {
	pageGreyOut();
	var url = $("#hiddenAddNewTaxonomyTag").val();
	var jqxhr = $.ajax({
		url : url,
		type : 'POST',
		cache : false,
		success : function(response) {
			if (response != null || response != '') {
				if (response.indexOf("Failure|") >= 0) {
					var responseArray = response.split("|");
					response = responseArray[1];
					$("#ErrorDiv").html(response);
					$("#ErrorDiv").show();
				} else {
					$("#addTaxonomyTaggingDiv").html(response);
					ddtreemenu.createTree("treemenu7", true);
					ddtreemenu.flatten('treemenu7', 'contact');
					ddtreemenu.openFirstLevel('treemenu7', 'expand');
					$(".overlay").closeOverlay();
					$(".overlay").launchOverlayNoClose(
							$(".alert-box-addTaxonomyTag"), "850px", null,
							"selectedServiceFunctionDropDown");
					$("#exisingLinkageItems").focus(function() {
						enableDisableRemoveButton();
					}).change(function() {
						enableDisableRemoveButton();
					});
					$(".exit-panel.upload-exit").click(function() {
						returnToTagListScreen();
					});
				}
			}
			removePageGreyOut();
		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	});
}
// this method changes state of remove modifier buttong
function enableDisableRemoveButton() {
	if ($("#exisingLinkageItems option:selected").size() > 0) {
		$("#removeModifierButton").attr("disabled", false);
	} else {
		$("#removeModifierButton").attr("disabled", true);
	}
}

/* this method returns to tag list screen
Updated Method in R4*/
function returnToTagListScreen() {
	$(".overlay").closeOverlay();
	editTaxonomyTagClicked(asProposalTitleGlobal, asProviderNameGlobal,
			asProcurementTitleGlobal, asProposalIdGlobal,
			asProcurementIdGlobal, asContractIdGlobal);
}

/* this method returns to tag list screen
New Method in R4*/
function returnToTagListScreenInBulk() {
	$(".overlay").closeOverlay();
}
/* this method is invoked on click of save/save&next buttons
Updated Method in R4*/
function saveChanges(isContinue) {
	$("#transactionStatusDiv").hide();
	var selectedServiceElementId = document
			.getElementById('serviceFunctionDrpDwn').value;
	if (null != selectedServiceElementId && selectedServiceElementId == "") {
		showErrorStatusDiv("! This field is required");
	} else {
		hideErrorStatusDiv();
		pageGreyOut();
		var proposalId = document.getElementById('proposalId').value;
		var procurementId = document.getElementById('procurementId').value;
		var linkageValues = document.getElementById('linkageValues').value;
		var selectedServiceElementId = document
				.getElementById('serviceFunctionDrpDwn').value;
		var url = $("#hiddenSaveTaxonomyTag").val() + "&elementId="
				+ selectedServiceElementId + "&proposalId=" + proposalId
				+ "&procurementId=" + procurementId + "&allLinkages="
				+ linkageValues + "&modifiers=" + modifiers + "&contractId="
				+ $("#contractId").val() + "&taxonomyTaggingId="
				+ $("#taxonomyTaggingId").val() + "&previousIds="
				+ asPreviousModifiers;
		var jqxhr = $.ajax({
			url : url,
			type : 'POST',
			cache : false,
			success : function(response) {
				if (isContinue) {
					$("#overlay").closeOverlay();
					editTaxonomyTagClicked(asProposalTitleGlobal,
							asProviderNameGlobal, asProcurementTitleGlobal,
							asProposalIdGlobal, asProcurementIdGlobal,
							asContractIdGlobal, true, false);
				} else {
					if (response != null && response != "null"
							&& typeof response != undefined && response != "")
						$("#taxonomyTaggingId").val(response);
					removePageGreyOut();
				}
			},
			error : function(data, textStatus, errorThrown) {
				showErrorMessagePopup();
				removePageGreyOut();
			}
		});
	}
}


// Below function adds linkages separated by delimiter '|''
function getModifierList(lsTagId) {
	var lsSelectId = document.getElementById(lsTagId);
	var lsCounter;
	var lsLength;
	var lsValuesToAppend = "";
	var lsModifierValue = "";
	var lsTagList = "";
	if (lsSelectId.length > 0) {
		for (lsCounter = 0; lsCounter < lsSelectId.length; lsCounter++) {
			lsValuesToAppend = lsValuesToAppend
					+ lsSelectId.options[lsCounter].text;
			var lastIndex = lsValuesToAppend.lastIndexOf(">");
			lsModifierValue = lsValuesToAppend.substring(lastIndex + 1);
			lsTagList = lsTagList + lsModifierValue + "|";
		}
		lsLength = lsValuesToAppend.length;
		lsTagList = lsTagList.slice(0, (lsLength - 1));
	}
	return lsTagList;
}

// this method displays the tree structure on left hand side
function showList(elementId, branchid, type) {
	$("li>span").css("background", "white");
	$("li[id=" + elementId + "]>span").css("background", "#81B5DC");
	linkageElementId = elementId;
	linkageBranchValue = branchid;
	var lsSelectTag = document.getElementById('exisingLinkageItems');
	var lsDuplicateFlag = false;
	for ( var liCounter = 0; liCounter < lsSelectTag.length; liCounter++) {
		if (linkageElementId == lsSelectTag[liCounter].value) {
			lsDuplicateFlag = true;
			break;
		}
	}
	if (branchid.split(",").length <= 2) {
		lsDuplicateFlag = true;
	}
	if (trim(linkageElementId) != "") {
		if (lsDuplicateFlag == true) {
			$("#addModifierButton").attr("disabled", true);
		} else {
			$("#addModifierButton").attr("disabled", false);
		}
	}
}

// Below function adds linkage to the selected taxonomy
function addLinkages() {
	var lsNewElementId = linkageElementId;
	var lsNewBranchId = linkageBranchValue;
	
	addLinkageItem(lsNewElementId, lsNewBranchId);
}


// Below function adds new linkage selected from tree
function addLinkageItem(lsNewElementId, linkageBranchValue) { // function shud
	hideErrorStatusDiv();
	var path = "";
	var linkageBranchValueString = linkageBranchValue;
	var linkageBranchValueArray = linkageBranchValueString.split(",");
	var lsNewTagOption = "";
	var lsDuplicateValue = "";
	var lsDuplicateFlag = false;
	var lsErrorMsg = "";
	var lsSelectTag = document.getElementById('exisingLinkageItems');

	for ( var i = 0; i < linkageBranchValueArray.length - 1; i++) {
		path = path
				+ (document.getElementById(linkageBranchValueArray[i]).title)
				+ ">";
	}
	path = path.replace(/.$/, '');

	for ( var liCounter = 0; liCounter < lsSelectTag.length; liCounter++) {
		if (lsNewElementId == lsSelectTag[liCounter].value) {
			lsDuplicateFlag = true;
			lsDuplicateValue = lsNewElementId;
			break;
		}
	}

	if (trim(lsNewElementId) != "") {
		if (lsDuplicateFlag == true) {
			lsErrorMsg = "Linkage " + path + " already added";
			showErrorStatusDiv(lsErrorMsg);
		} else {
			lsNewTagOption = document.createElement('option');
			lsNewTagOption.value = lsNewElementId;
			lsNewTagOption.text = path;
			lsNewTagOption.title = path;
			lsSelectTag.options.add(lsNewTagOption);
			$("#addModifierButton").attr("disabled", true);
		}
	}
}

// this method populates the selected service drop down
function selectedServiceFunctionDropDown() {
	$("#procurementContractTitle.selectedservice").html(
			$("#procurementTitle.addnewtag").html());
	$("#proposalTitle.selectedservice").html(
			$("#proposalTitle.addnewtag").html());
	$("#providerName.selectedservice")
			.html($("#providerName.addnewtag").html());
	var branchIdHidden = $("#branchIdHidden").val();
	var elementIdHidden = $("#elementIdHidden").val();
	var branchIdArray = branchIdHidden.split("--");
	var elementIdArray = elementIdHidden.split("--");
	for ( var i = 1; i < branchIdArray.length - 1; i++) {
		getSelectedServiceFunctionDropDown(elementIdArray[i], branchIdArray[i]);
	}
	$('#serviceFunctionDrpDwn option').sort(sortServiceFunction).appendTo(
			'#serviceFunctionDrpDwn');
	$('#serviceFunctionDrpDwn option').eq(0).attr("selected", "selected");
}
// this method sorts the selected services 
function sortServiceFunction(a, b) {
	return (a.innerHTML > b.innerHTML) ? 1 : -1;
};

// Below function gets path for all the taxonomy whose evidence is not present
function getSelectedServiceFunctionDropDown(lsServiceElementId, lsBranchId) {
	var path = "";
	var arr = lsBranchId.split(",");
	for ( var i = 1; i < arr.length - 1; i++) {
		path = path + ($("#" + arr[i]).attr("title")) + ">";
	}
	path = path.replace(/.$/, '');
	var lsSelectTag = document.getElementById('serviceFunctionDrpDwn');
	var lsNewTagOption = document.createElement('option');
	lsNewTagOption.value = lsServiceElementId;
	lsNewTagOption.title = path;
	lsNewTagOption.text = path;
	lsSelectTag.options.add(lsNewTagOption);
}

/* Below function removes selected linkage under particular selected taxonomy
Updated Method in R4*/
function removeLinkageItem() {
	$("#exisingLinkageItems option:selected").remove();
	$("#removeModifierButton").attr("disabled", true);
}

// this method sets the data change flag
function setFlag() {
	changeFlag = true;
	hideErrorStatusDiv();
}

// this method is called on selecting edit/delete from the add taxonomy page
function editDeleteTags(procurementId, selectElement, proposalId, contractId,
		elementId, linkageBranchId, taxonomyTaggingId) {
	var value = selectElement.selectedIndex;
	$(selectElement).find("option").eq(0).attr("selected", "selected");
	if (value == 1) {
		editModifierDetails(elementId, taxonomyTaggingId, linkageBranchId);
	} else if (value == 2) {
		pageGreyOut();
		var url = $("#hiddenDeleteTaxonomyTag").val() + "&taxonomyTaggingId="
				+ taxonomyTaggingId + "&action=" + "deleteTag";
		var jqxhr = $.ajax({
			url : url,
			type : 'POST',
			cache : false,
			success : function(response) {
				removePageGreyOut();
				$("#overlay").closeOverlay();
				editTaxonomyTagClicked(asProposalTitleGlobal,
						asProviderNameGlobal, asProcurementTitleGlobal,
						asProposalIdGlobal, asProcurementIdGlobal,
						asContractIdGlobal, false, true);
			},
			error : function(data, textStatus, errorThrown) {
				showErrorMessagePopup();
				removePageGreyOut();
			}
		});
	}
}

/* this method is called on selecting edit/delete from the add taxonomy page
Updated Method in R4*/
function editDeleteTagsInBulk(procurementId, selectElement, proposalId,
		contractId, elementId, linkageBranchId, taxonomyTaggingId, fromSave) {
	if (selectElement != null) {
		var value = selectElement.selectedIndex;
		$(selectElement).find("option").eq(0).attr("selected", "selected");
	}
	if (value == 1 || fromSave) {
		editModifierDetailsInBulk(elementId, taxonomyTaggingId, linkageBranchId);
	} else if (value == 2) {
		deleteModifierDetailsInBulk(procurementId, selectElement, proposalId,
				contractId, elementId, linkageBranchId, taxonomyTaggingId)
	}
}


// this method will open edit modifier popup of taxonomy tag
function editModifierDetails(elementId, taxonomyTaggingId, linkageBranchId) {
	pageGreyOut();
	var url = $("#hiddenAddNewTaxonomyTag").val() + "&taxonomyTaggingId="
			+ taxonomyTaggingId;
	var jqxhr = $.ajax({
		url : url,
		type : 'POST',
		cache : false,
		success : function(response) {
			if (response != null && response != '') {
				$("#addTaxonomyTaggingDiv").html(response);
				ddtreemenu.createTree("treemenu", true);
				ddtreemenu.openFirstLevel('treemenu', 'expand');
			}
			$(".overlay").closeOverlay();
			$(".overlay").launchOverlayNoClose($(".alert-box-addTaxonomyTag"),
					"850px", null, "selectedServiceFunctionDropDown");
			$("#exisingLinkageItems").focus(function() {
				enableDisableRemoveButton();
			}).change(function() {
				enableDisableRemoveButton();
			});
			$(".exit-panel.upload-exit").click(function() {
				returnToTagListScreen();
			});
			var selectedFuction = document
					.getElementById('serviceFunctionDrpDwn');
			for ( var i = 0; i < selectedFuction.options.length; i++) {
				if (selectedFuction.options[i].value == elementId) {
					selectedFuction.selectedIndex = i;
					break;
				}
			}
			addTaxonomyModifiers(linkageBranchId);
			removePageGreyOut();
		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	});
}


// Below function adds new linkage selected from tree
function addTaxonomyModifiers(linkageBranchValue) {
	var path = "";
	var linkageBranchValueString = linkageBranchValue;
	var linkageBranchValueArray = linkageBranchValueString.split("|");
	var count = linkageBranchValueString.split("|").length;
	var arrcount = "";
	var lsNewTagOption = "";
	asPreviousModifiers = "";
	var lsSelectTag = document.getElementById('exisingLinkageItems');
	var j = 0;
	var elementId;
	for (j = 0; j < count; j++) {
		var temparr = linkageBranchValueArray[j].split(",,");
		for ( var i = 0; i < temparr.length; i++) {
			arr2 = temparr[i].split(",");
			for ( var k = 0; k < arr2.length - 1; k++) {
				path = path + (document.getElementById(arr2[k]).title) + ">";
				elementId = arr2[k];
			}
			path = path += "\r\n";
			path = path.replace(/.$/, '');
			var lastIndex = path.lastIndexOf(">");
			path = path.substring(0, lastIndex);
			lsNewTagOption = document.createElement('option');
			lsNewTagOption.text = path;
			lsNewTagOption.title = path;
			lsNewTagOption.value = elementId;
			asPreviousModifiers = elementId + "," + asPreviousModifiers;
			lsSelectTag.options.add(lsNewTagOption);
			path = "";
		}
	}

}

// Below function displays error div along with error message
function showErrorStatusDiv(lsErrorMsg) {
	$("#errorStatusDiv").html(lsErrorMsg);
	$("#errorStatusDiv").addClass('error');
	$("#errorStatusDiv").show();
}

// Below function removes spaces from left and right of the string
function trim(stringToTrim) {
	return stringToTrim.replace(/^\s+|\s+$/g, "");
}

// Below function bring the user to main screen, prompting if any unsaved
// changes are left
function returnToMain() {
	var url = $("#contextPathSession").val()
			+ "/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_maintenancelanding&_nfls=false&app_menu_name=header_maintenance";
	location.href = url;
}

// Below function hide error status div(this happens when user changes or takes
// another action)
function hideErrorStatusDiv() {
	$("#errorStatusDiv").hide();
	$("#errorStatusDiv").html("");
	$("#errorStatusDiv").removeClass('failed');
}

// This will execute when Previous,Next.. is clicked for pagination
function paging(pageNumber) {
	document.taxonomytaggingform.reset();
	$("#taxonomytaggingform").attr(
			"action",
			$("#taxonomytaggingform").attr("action")
					+ "&submit_action=fetchActiveProcurements&nextPage="
					+ pageNumber);
	pageGreyOut();
	$("#taxonomytaggingform").submit();
}

// This will execute when any Column header on grid is clicked for sorting
function sort(columnName) {
	document.taxonomytaggingform.reset();
	$("#taxonomytaggingform")
			.attr(
					"action",
					$("#taxonomytaggingform").attr("action")
							+ "&submit_action=sortTaxonomy&sortGridName=taxonomyTagging"
							+ sortConfig(columnName));
	$("#taxonomytaggingform").submit();
}
/*
This method is invoked when Selected Option is changed
New Method in R4
*/
function changesInSelectedOption() {
	$("#taxonomytaggingform")
			.attr(
					"action",
					$("#taxonomytaggingform").attr("action")
							+ "&submit_action=selectTaxonomy&sortGridName=taxonomyTagging");
	$("#taxonomytaggingform").submit();
}
/* This will execute when Filter Button tab is clicked, it validates the filter
 popup and filters the data
Updated Method in R4*/
function displayFilter() {
	$(".error").html("");
	var proposalTitle = $("#proposalTitle").val();
	var procurementTitle = $("#procurementContractTitle").val();
	var dateApprovedFrom = $("#dateApprovedFrom").val();
	var dateApprovedTo = $("#dateApprovedTo").val();
	var isValid = true;
	if (null != proposalTitle && proposalTitle != '') {
		var length = proposalTitle.length;
		if (length < 5) {
			$('#proposalTitle').parent().next().html(
					"! You must enter 5 or more characters");
			isValid = false;
		}
	}
	$("input[type='text']").each(function() {
		if ($(this).attr("validate") == 'calender') {
			var isValidLocal = verifyDate(this);
			if (!isValidLocal)
				isValid = false;
		}
	});
	if (dateApprovedTo != '' && dateApprovedFrom != '') {
		var endDate = new Date(dateApprovedTo);
		var startDate = new Date(dateApprovedFrom);
		if (dateApprovedTo != '' && dateApprovedFrom != ''
				&& startDate > endDate) {
			$("#dateApprovedTo")
					.parent()
					.next()
					.html(
							"! 'Date Approved to' must be after the 'Date Approved from'");
			isValid = false;
		}
	}
	if (isValid) {
		$("#taxonomytaggingform").attr(
				"action",
				$("#taxonomytaggingform").attr("action")
						+ "&submit_action=filterProcurement");
		pageGreyOut();
		$("#taxonomytaggingform").submit();
	}
}

/* This will execute when Clear Filter button is clicked and will set default
 values for filter
Updated Method in R4*/
function clearTaxonomyTaggingFilter() {
	$("#documentValuePop").find("input[type='text']").val("");
	$("#documentValuePop").find("input[type='checkbox']").attr("checked",
			"true");
	$("#clearfilter").attr("disabled", true);
	$("#competitionPoolTitle").attr("disabled", true);
	$("#procurementId").val("");
	
	/* BEGIN QC 6523 RELEASE 3.7.0     */
	$('select').find('option:first').attr('selected', 'selected');
	disableProgramDropDown();
	$("#contractAgencyName").val("All NYC Agencies");
	$("#programName").val('');
	$("#awardEpin").val('');
	/* END QC 6523 RELEASE 3.7.0     */
	
	$(".error").html("");
	$("input:radio[name=selectedTaxonomy]:first").attr('checked', true);
	enableDisableDefaultFilter(false);
}

// this method checks if date is future date
function checkForFutureDate(id) {
	var myDate1 = $(id).val();
	var isValid = true;
	if (myDate1 != undefined && myDate1 != '') {
		var month = myDate1.substring(0, 2);
		var date = myDate1.substring(3, 5);
		var year = myDate1.substring(6, 10);
		var currentDate = new Date(year, month - 1, date);
		var today = new Date();
		if (currentDate > today) {
			$(id).parent().next().html(
					"! Invalid Date. Please enter a date in the past");
			isValid = false;
		}
	}
	return isValid;
}

/* this method enables/disables default filter button
Updated Method in R4*/
function enableDisableDefaultFilter(isOnload) {
	var isSame = true;
	if (!$(formValue).compare($("#taxonomytaggingform").serializeArray())) {
		isSame = false;
	}
	if (isOnload) {
		if ($("#documentValuePop input[type='text'][value!='']").size() == 0
				&& $("#documentValuePop input[type='checkbox']:checked").size() == 2) {
			isSame = true;
		} else {
			isSame = false;
		}
	}
	if ($("#documentValuePop input[type='text'][value!='']").size() == 0
			&& $("#documentValuePop input[type='checkbox']:checked").size() == 2
			&& $("input:radio[name=selectedTaxonomy]:first").attr('checked') == 'checked' 
				/* BEGIN  QC 6523 Release 3.7.0 */				
			&& $("#agency").prop("selectedIndex")==0 )
	{
				/* END  QC 6523 Release 3.7.0 */
		$("#clearfilter").attr("disabled", true);
	} else {
		$("#clearfilter").attr("disabled", false);
	}
	if($("#competitionPoolTitle").val() == "" && $("#procurementId").val() != "P"){
		$("#competitionPoolTitle").attr("disabled", true);
		$("#procurementId").val("");
	}else{
		$("#competitionPoolTitle").attr("disabled", false);
		$('#competitionPoolTitle').unbind().keyup(function(e){
			if(e.keyCode!=37 && e.keyCode!=38 && e.keyCode!=39 && e.keyCode!=40)
				replaceAllExceptAllowedChar(this);
		}).focusout(function(){
			replaceAllExceptAllowedChar(this);
		});
		typeHeadSearch($('#competitionPoolTitle'), $("#hiddenFetchTypeAheadNameList")
				.val()+ "&QueryId=fetchCompetitionPoolTitleList&key=COMPETITION_POOL_ID&value=COMPETITION_POOL_TITLE&procurementTitle="+$("#procurementContractTitle").val(), null, null, null, "competitionPoolId");
	}
}

// This will execute when Filter Documents tab is clicked or closed
function setVisibility(id, visibility) {
	if ($("#" + id).is(":visible")) {
		document.taxonomytaggingform.reset();
		enableDisableDefaultFilter(true);
	}
	$("#" + id).toggle();
	$(".error").html("");
	callBackInWindow("closePopUp");
}

// This function is used to view procurement summary for agency/provider users
function viewProcurementSummary(procurementId) {
	var url = $("#procurementSummaryURL").val()
			+ "&action=procurementHandler&overlay=true&procurementId="
			+ procurementId;
	window.open(url, 'windowOpenTab',
			'scrollbars=1,resizable=1,width=1000,height=580,left=0,top=0');
}
/*This method is used to enable/disable the add and remove tags in bulk buttons
New Method in R4*/
function enableAddNewTagsBulk(selectedTaxonomy) {
	if (($("#taxonomyTaggingTable :checkbox:checked").length - $('[name="selectAll"]:checked').length) > 1) {
		$("#addNewTagsBulk").prop("disabled", "");
	} else {
		$("#addNewTagsBulk").prop("disabled", "disabled");
	}
	if (($("#taxonomyTaggingTable :checkbox:checked").length - $('[name="selectAll"]:checked').length) > 0) {
		$("#removeAllTagsBulk").prop("disabled", "");
	} else {
		$("#removeAllTagsBulk").prop("disabled", "disabled");
	}
	if ($("input[name=check]:checkbox:not(:checked)").size() > 0) {
		$("#selectAll").attr("checked", false);
	}else{
		$("#selectAll").attr("checked", true);
	}

}
/*This method is invoked  when u check or uncheck a check box on th eprocurement page
New Method in R4*/
function selectAllCheck() {
	if ($("#selectAll").attr("checked") == 'checked') {
		$('div#taxonomyTaggingTable input[type=checkbox]').each(function() {
			this.checked = true;
			enableAddNewTagsBulk();
		});
	} else {
		$('div#taxonomyTaggingTable input[type=checkbox]').each(function() {
			this.checked = false;
			enableAddNewTagsBulk();
		});
	}
}
/*This method removes the values in the filter pop up
New Method in R4*/
function callBackProcurementTitle(){
	$('#competitionPoolTitle').unbind().val("").keyup(function(e){
		if(e.keyCode!=37 && e.keyCode!=38 && e.keyCode!=39 && e.keyCode!=40)
			replaceAllExceptAllowedChar(this);
	}).focusout(function(){
		replaceAllExceptAllowedChar(this);
	});
	$('#competitionPoolId').val("");
	$('#procurementId').val("");
}
/* This method closes the remove all taxonomy overlay
New Method in R4*/
function closeRemoveAllTaxRender(){
	//$(".alert-box alert-box-removeAllTaxonomyTag").closeOverlay();
	document.location = $(".exit-panel.removeall-exit").attr("href");
}
/* This method is used to enable/diable the competetion pool field in the filter
New Method in R4*/
function callBackProcurementTitleOnSelect(){
	if($("#procurementId").val() == "P"){
		$('#competitionPoolTitle').unbind().keyup(function(e){
			if(e.keyCode!=37 && e.keyCode!=38 && e.keyCode!=39 && e.keyCode!=40)
				replaceAllExceptAllowedChar(this);
		}).focusout(function(){
			replaceAllExceptAllowedChar(this);
		});
		typeHeadSearch($('#competitionPoolTitle'), $("#hiddenFetchTypeAheadNameList")
				.val()+ "&QueryId=fetchCompetitionPoolTitleList&key=COMPETITION_POOL_ID&value=COMPETITION_POOL_TITLE&procurementTitle="+$("#procurementContractTitle").val(), null, null, null, "competitionPoolId");
	}else{
		$('#competitionPoolTitle').attr("disabled", true);
	}
}
/*This method is invoked on click 'Add new Tag(s)' and launches its overlay 
New Method in R4*/
function addNewTag(){
	$("#popup1MessageDiv").hide();
	pageGreyOut();
	currentId = null;
	var url = $("#hiddenEditTaxonomyTaggingInBulk").val();
	var jqxhr = $.ajax({
		url : url,
		type : 'POST',
		cache : false,
		success : function(response) {
			if (response != null || response != '') {
				$("#secondScreenPortion").html(response);
				ddtreemenu.createTree("treemenu", true);
				ddtreemenu.flatten('treemenu', 'contact');
				ddtreemenu.openFirstLevel('treemenu', 'expand');
				selectedServiceFunctionDropDown();
				$("#firstScreenPortion").hide();
				$("#secondScreenPortion").show();
				$(window).resize();
				$("#exisingLinkageItems").focus(function() {
					enableDisableRemoveButton();
				}).change(function() {
					enableDisableRemoveButton();
				});
				cancelButtonFunctionality();
			}
			removePageGreyOut();
		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	});
}
/*This methtod is used to save the taxonomy details in the hidden fields
New Method in R4*/
function saveChangesInBulk(isContinue) 
{
	$("#popup1MessageDiv").hide();
	$("#transactionStatusDiv").hide();
	var selectedServiceElementId = document.getElementById('serviceFunctionDrpDwn').value;
	if (null != selectedServiceElementId && selectedServiceElementId == "") {
		showErrorStatusDiv("! This field is required");
	} else {
		hideErrorStatusDiv();
		pageGreyOut();
		var linkageValues = document.getElementById('linkageValues').value;
		var e;
		if(currentId==null){
			currentId = ++idsGenerated;
			e = $("<tr id=\"current"+ currentId +"\"><td class='servicef'></td><td class='modifers'></td><td><select onchange=\"onchangeBulk(this, '"+currentId+"')\"><option value=I need to... >I need to...</option>"
						+ "<option>Edit Tag</option><option>Delete Tag</option></select>" +
								"<input type = \"hidden\" id=\"serviceId\" name=\"serviceId\"/>" +
								"<input type = \"hidden\" id=\"modifierIds\" name=\"modifierIds\"/>" +
								"<input type = \"hidden\" id=\"modifierListComplete\" name=\"modifierListComplete\"/>" +
								"<input type = \"hidden\" id=\"taxonomyTaggingId\" name=\"taxonomyTaggingId\" value=\"-\"/></td>" +
								"</tr>");
			$("#serviceFunctionList").append(e);
		}else{
			e = $("#current"+currentId);
		}
		var serviceName = $("#serviceFunctionDrpDwn option:selected").text();
		var modifierList = "--";
		var modifierListComplete = "";
		var modifierArray = new Array();
		var modifierIds = "";
		$("#exisingLinkageItems option").each(function(){
			var modifierName = $(this).text();
			modifierListComplete = modifierListComplete + modifierName + keySeparator;
			modifierName = modifierName.substring(modifierName.lastIndexOf(">")+1,modifierName.length);
			modifierArray[modifierArray.length] = modifierName;
			modifierIds = modifierIds + $(this).attr("value") + ",";
		});
		if($("#exisingLinkageItems option").size() > 0 ){
			modifierList = "";
			modifierArray.sort(
			  function(a, b) {
			    if (a.toLowerCase() < b.toLowerCase()) return -1;
			    if (a.toLowerCase() > b.toLowerCase()) return 1;
			    return 0;
			  }
			);
			$.each(modifierArray, function(index, value) { 
				if(modifierList!='')
					modifierList = modifierList + ", ";
				modifierList = modifierList + value;
			});
		}
		e.find("#serviceId").val($("#serviceFunctionDrpDwn option:selected").val());
		e.find("#modifierIds").val(modifierIds);
		e.find("#modifierListComplete").val(modifierListComplete);
		e.find(".servicef").html(serviceName.substring(serviceName.lastIndexOf(">")+1,serviceName.length));
		e.find(".modifers").html(modifierList);
		if(isContinue)
		{
			closeSecondPortion();
		}
		showHideBlankRow();
		cancelButtonFunctionality();
		removePageGreyOut();
	}
}
/*This method is invoked on selecting the action from the drop down in the edit tags pop up 
New Method in R4*/
function onchangeBulk(selectedElement, currentIdLocal){
	var value = selectedElement.selectedIndex;
	if (value == 1) {
		currentId = currentIdLocal;
		editTagBulk();
		cancelButtonFunctionality();
	} else if (value == 2) {
		$("#hiddenDeletedTags").val($("#hiddenDeletedTags").val()+$("#current"+currentIdLocal).find("#taxonomyTaggingId").val()+",");
		$("#current"+currentIdLocal).remove();
		$("#popup1MessageDiv div").text("The selected tag has been successfully deleted.").removeClass().addClass("passed").show();
		$("#popup1MessageDiv").show();
		showHideBlankRow();
	}
	selectedElement.selectedIndex = 0;
}
/* This method hide/show the default tag message on the tag all proposals pop up  
New Method in R4*/
function showHideBlankRow(){
	if($("tr[id^=current]").size()>0){
		$("#blankRow").hide();
		$("#tagAllDiv").show();
	}
	else{
		$("#blankRow").show();
		if($("#hiddenDeletedTags").val().replace(new RegExp("-,", 'g'), "").length == 0){
			$("#tagAllDiv").hide();
		}
	}
	applyStyle("serviceFunctionList");
}
/* This method is invoked when the tags are edited in bulk
New Method in R4*/
function editTagBulk(){
	$("#popup1MessageDiv").hide();
	var selectedServiceId = $("#current"+currentId).find("#serviceId").val();
	var modifierCompleteArray = $("#current"+currentId).find("#modifierListComplete").val().split(keySeparator);
	var modifierIdArray = $("#current"+currentId).find("#modifierIds").val().split(",");
	var selectedFuction = document.getElementById('serviceFunctionDrpDwn');
	
	for ( var i = 0; i < selectedFuction.options.length; i++) {
		if (selectedFuction.options[i].value == selectedServiceId) {
			selectedFuction.selectedIndex = i;
			break;
		}
	}
	var lsSelectTag = document.getElementById('exisingLinkageItems');
	$("#exisingLinkageItems option").remove();
	$("#removeModifierButton").attr("disabled", true);
	for(var i = 0 ; i < modifierCompleteArray.length - 1 ; i++){
		lsNewTagOption = document.createElement('option');
		lsNewTagOption.text = modifierCompleteArray[i];
		lsNewTagOption.title = modifierCompleteArray[i];
		lsNewTagOption.value = modifierIdArray[i];
		lsSelectTag.options.add(lsNewTagOption);
	}
	$("#firstScreenPortion").hide();
	ddtreemenu.flatten('treemenu', 'contact');
	ddtreemenu.openFirstLevel('treemenu', 'expand');
	$("#secondScreenPortion").show();
	$(window).resize();
}
/* This method is invoked on click of 'Tag All proposals' button and submits the form
New Method in R4*/
function tagAllSelectedProposals()
{
	$("#popup1MessageDiv").hide();
	var sContractIdBulk="";
	var sProposalIdBulk ="";
	var sProcurementIdBulk ="";
	for(var i=0;i<ContractIdBulk.length;i++)
	{
		sContractIdBulk = sContractIdBulk + ContractIdBulk[i] + ",";
		sProposalIdBulk = sProposalIdBulk + ProposalIdBulk[i] + ","; 
		sProcurementIdBulk = sProcurementIdBulk +  ProcurementIdBulk[i] + ",";
	}
	$("#hiddenContractIdBulk").val(sContractIdBulk);
	$("#hiddenProposalIdBulk").val(sProposalIdBulk);
	$("#hiddenProcurementIdBulk").val(sProcurementIdBulk);
	$("#editTaxonomyTaggingForm").submit();
}


var previousSelectedService, previousModifiers, previousServiceSelection;

/*This method is invoked on the click of cancel button and launches the alert box for exiting without saving 
New Method in R4*/
function cancelButtonFunctionality(){
	previousSelectedService = $("#serviceFunctionDrpDwn option:selected").val();
	previousModifiers = "";
	$("#exisingLinkageItems option").each(function(){
		previousModifiers = previousModifiers + $(this).attr("value") + ",";
	});
	$("#cancelAddNewTagInBulk").unbind("click").click(function(e){
		e.preventDefault();
		if(!portion2P05()){
			$('<div id="dialogBox"></div>').appendTo('body')
			.html('<div><h6>You have unsaved data. <br />If you would like to leave this screen without saving your data, click <b>OK</b>. <br/>If you would like to save your data, click <b>Cancel</b> and save your data.</h6></div>')
			.dialog({
				modal: true, title: 'Unsaved Data', zIndex: 10000, autoOpen: true,
				width: 'auto', modal: true, resizable: false, draggable:false,
				dialogClass: 'dialogButtons',
				buttons: {
					OK: function () {
						closeSecondPortion();
						$(this).dialog("close");
					},
					Cancel: function () {
						$(this).dialog("close");
					}
				},
				close: function (event, ui) {
					$(this).remove();
				}
			});
			$("div.dialogButtons div button:nth-child(2)").find("span").addClass("graybtutton");
		}else{
			closeSecondPortion();
		}
	});
}

/* This method checks whether there are unsaved changes in the tags.
New Method in R4*/
function portion2P05(){
	if($("#secondScreenPortion").is(":visible")){
		var currentSelectedService = $("#serviceFunctionDrpDwn option:selected").val();
		var currentModifiers = "";
		$("#exisingLinkageItems option").each(function(){
			currentModifiers = currentModifiers + $(this).attr("value") + ",";
		});
		if(currentSelectedService == previousSelectedService && previousModifiers == currentModifiers)
			return true;
		else
			return false;
	}else{
		if((ProcurementIdBulk.length > 1 && $("#serviceFunctionList tr").size() == 2) || (ProcurementIdBulk.length == 1 && $("#serviceFunctionList").html() == previousServiceSelection)){
			return true;
		}else{
			return false;
		}
	}
}


/* This method is used to close secondScreenPortion and open firstScreenPortion
New Method in R4*/
function closeSecondPortion(){
	$("#firstScreenPortion").show();
	$("#secondScreenPortion").hide();
	currentId=null;
	$(window).resize();
}

/* BEGIN  QC 6523 Release 3.7.0 */
//This funtion fetches program name list depending upon agency selected by user
function getProgramNameList() {
	var url = $("#getProgramListForAgency").val() + "&agencyId="
			+ $("#agency").val();
	
	var jqxhr = $.ajax({
		url : url,
		type : 'POST',
		cache : false,
		success : function(data) {
			if($("#agency").prop("selectedIndex")!=0)
				{
			$("#programName").html(data);
			$("#programName").prop('disabled', false);
			var myData = data;
			}
		},
		error : function(data, textStatus, errorThrown) {
			var myData = data;
		}
	});
}
//This funtion disables the drop down
function disableProgramDropDown() {
if($("#agency").prop("selectedIndex")==0)
	{
		$("#programName").prop('disabled', true);
	}
}

//This method calls typeHeadCallBackAward passing awardEpin as parameter
function typeHeadCallBackAward() {
	commonTypeHeadCallBack($('#awardEpin').val());
	}

/* END  QC 6523 Release 3.7.0 */