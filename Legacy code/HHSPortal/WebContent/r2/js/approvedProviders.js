var lastDropDownValue;

//On page load
$(document).ready(function(){
	$("#saveChanges").click(function(){
		pageGreyOut();
	});
	$("#providerStatusDiv").hide();
	if($("#approvedProviderListSize").val() > 0){
		$("#approvedProvidersCount").show();
		if($("#selectionBoxDropDownHidden").val() == "1" && $("#numberOfServices").val() > 1 ){
			$("#approvedProvidersDropDown").show();
			bindSortFunction();
		}
		$(".providerCount").html($("#approvedProviderListSize").val());
	}
	
	
	$("a[id!='smallA'][id!='mediumA'][id!='largeA']").click(function(e) {
		if($("input[value^='Save'][type='button']:visible, input[type='submit']:visible").size() > 0 
				&& $("#tabs-container").size() > 0
				&& !$(this).hasClass("byPassLink") && !$(this).hasClass("exit-panel")
				&& ($(this).parents("#tabs-container").length == 0 || $(this).attr("id") == "returnSummaryPage")
				&& !$(this).parent().hasClass("tabDisabled")){
			var $self=$(this);
			var isSame = true;
			if(lastDropDownValue != null && lastDropDownValue != $("#selectionBoxDropDown").val()){
				isSame = false;
			}
			if(!isSame){
				e.preventDefault();
				$('<div id="dialogBox"></div>').appendTo('body')
				.html('<div><h6>You have unsaved data. <br />If you would like to leave this screen without saving your data, click <b>OK</b>. <br/>If you would like to save your data, click <b>Cancel</b> and save your data.</h6></div>')
				.dialog({
					modal: true, title: 'Unsaved Data', zIndex: 10000, autoOpen: true,
					width: 'auto', modal: true, resizable: false, draggable:false,
					dialogClass: 'dialogButtons',
					buttons: {
						OK: function () {
							if($self.hasClass("navigationR2Class")){
								navigateToTab($self);
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
			}else if($(this).hasClass("navigationR2Class")){
				navigateToTab($(this));
			}
		}else if($(this).hasClass("navigationR2Class")||  (typeof clickOnGridArr !='undefined' && clickOnGridArr.length>0)){
			if( typeof clickOnGridArr !='undefined'){
				if(clickOnGridArr.length>0){
					var $self=$(this);
					e.preventDefault();
					$('<div id="dialogBox"></div>').appendTo('body')
					.html('<div><h6>You have unsaved data. <br />If you would like to leave this screen without saving your data, click <b>OK</b>. <br/>If you would like to save your data, click <b>Cancel</b> and save your data.</h6></div>')
					.dialog({
						modal: true, title: 'Unsaved Data', zIndex: 10000, autoOpen: true,
						width: 'auto', modal: true, resizable: false, draggable:false,
						dialogClass: 'dialogButtons',
						buttons: {
							OK: function () {
								$(this).dialog("close");
								if($self.hasClass("navigationR2Class")){
									navigateToTab($self);
								}else{
									document.location = $self.attr('href');
									pageGreyOut();
								}
							},
							Cancel: function () {
								$(this).dialog("close");
								if(!$self.hasClass("navigationR2Class")){
									removePageGreyOut();
								}
							
							}
						},
						close: function (event, ui) {
							$(this).remove();
							if(!$self.hasClass("navigationR2Class")){
								removePageGreyOut();
							}
						}
					});
					$("div.dialogButtons div button:nth-child(2)").find("span").addClass("graybtutton");
				}else if($(this).hasClass("navigationR2Class")){
					navigateToTab($(this));
				}
			}else {
			  navigateToTab($(this));
			}
		}
	});
});
$(window).load(function(){
	lastDropDownValue = $("#selectionBoxDropDown").val();
});
// This function will display the organization read only screen corresponding to
// an organization_id
function viewOrganizationSummary(organizationId, organizationLegalName) {
	var url = $("#hiddenOpenOrganization").val() + "&organizationId=" + organizationId;
	window.open(url,
			'windowOpenTab',
	'scrollbars=1,resizable=1,width=1000,height=580,left=0,top=0');
}

// This function will be called on on-change event of the second drop-down
function fetchSelService() {
	ajaxCallForList(true, $("#hiddenApprovedProviders").val()+"&selectionBoxDropDown="+$("#selectionBoxDropDownHidden").val()+"&eltId="+$("#selServiceDropDown option:selected").val());
}

// This function will display the fields on click of "Generate List" button
// depending on the value selected in "Required Providers" drop down
function approvedProviderGenerate() {
	$("#selServiceDropDown option").eq(0).attr("selected", "selected");
	ajaxCallForList(false, $("#hiddenApprovedProviders").val()+"&selectionBoxDropDown="+$("#selectionBoxDropDown option:selected").val());
	$("#selectionBoxDropDownHidden").val($("#selectionBoxDropDown option:selected").val());
}

//fetches list of approved providers
function ajaxCallForList(isDropDownChange, url){
	pageGreyOut();
	var jqxhr = $.ajax({
		url : url+"&serviceElementIdList="+$("#serviceElementIdList").val(),
		type : 'POST',
		cache : false,
		success : function(data) {
			$("#approvedProvidersTableMain").html($(data).find("#approvedProvidersTableContainer"));
			removePageGreyOut();
			if($("#approvedProviderListSize").val() > 0){
				$("#approvedProvidersCount").show();
				if($("#selectionBoxDropDownHidden").val() == "1" && $("#numberOfServices").val() > 1){
					$("#approvedProvidersDropDown").show();
				}else{
					if(!isDropDownChange){
						$("#approvedProvidersDropDown").hide();
					}
				}
			}else{
				if(!isDropDownChange){
					$("#approvedProvidersDropDown").hide();
				}
				$("#approvedProvidersCount").hide();
			}
			$(".providerCount").html($("#approvedProviderListSize").val());
			bindSortFunction();
		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	});
}

//used for sorting approved provider table
function bindSortFunction(){
	$("#selServiceDropDown").unbind("change").change(function(){
		fetchSelService();
	});
}

//This will execute when any Column header on grid is clicked for sorting
function sort(columnName) {
	var url =  $("#hiddenApprovedProviders").val()+
				"&selectionBoxDropDown="+$("#selectionBoxDropDownHidden").val()+
				"&eltId="+$("#selServiceDropDown option:selected").val()+
				sortConfig(columnName);
	ajaxCallForList(true, url);
}
//This method is used to grey out the background
function setPageGreyOut(){
	pageGreyOut();
}