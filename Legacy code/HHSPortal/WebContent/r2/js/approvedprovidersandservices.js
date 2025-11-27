var isDropDownChange=false;
//This method called when page is getting loaded and set the values

$(document).ready(function(){
	$("#backButton").backNextButton("backButtonAction");
	$("#providerStatusDiv").hide();
	if($("#approvedProviderListSize").val() > 0){
		$("#approvedProvidersCount").show();
		if($("#selectionBoxDropDown").val() == "1" && $("#selectedServiceListListSize").val() >= 1){
			$("#approvedProvidersDropDown").show();
			$("#approvedProvidersTableMain").show();
		}else if($("#selectionBoxDropDown").val() == "0" && $("#selectedServiceListListSize").val() >= 1){
			$("#approvedProvidersTableMain").show();
		}
		bindSortFunction();
		$(".providerCount").html($("#approvedProviderListSize").val());
	}
	
	$("#myTable").tablesorter({  cssHeader: "sort-ascending-new",
        cssDesc: "sort-ascending-new",
        cssAsc: "sort-descending-new",
        sortInitialOrder: "desc"}); 

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
function fetchSelService(obj) {
	var changeDropDownValueProvider = null;
	
	if($(obj).val()!=-1){
		changeDropDownValueProvider = "changeDropDownValueProvider";
	}
	ajaxCallForList(true, $("#hiddenApprovedProviders").val()+"&selectionBoxDropDown="+$("#selectionBoxDropDown").val()+
			"&eltId="+$("#selServiceDropDown option:selected").val()+"&changeDropDownValue="+changeDropDownValueProvider);
}

// This method will handle the action on click of Next button
function nextButtonAction() {
	pageGreyOut();
	window.location.href = $("#nextPageURL").val();
}

//This method will handle the action on click of Back button
function backButtonAction() {
	pageGreyOut();
	window.location.href = $("#backPageURL").val();
}

//used for sorting approved provider table
function bindSortFunction(){
	$("#selServiceDropDown").unbind("change").change(function(){
		fetchSelService(this);
	});
}

//This will execute when any Column header on grid is clicked for sorting
function sort(columnName) {
	var url =  $("#hiddenApprovedProviders").val()+
				"&selectionBoxDropDown="+$("#selectionBoxDropDownHidden").val()+
				"&eltId="+$("#selServiceDropDown option:selected").val()+
				sortConfig(columnName);
	//calling ajaxCallForList method to perform the sorting
	ajaxCallForList(true, url);
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
			
			$("th").each(function(i){
				if(i!=0){
					$(this).hide();
				}else{
					$(this).find("a").remove();
					$(this).addClass("header").html("Provider Name");
				}
			});
			$("tr").each(function(){
				$(this).find("td").each(function(i){
					if(i!=0){
						$(this).hide();
					}
				});
			});
			
			$(".sort-descending").removeAttr("class").removeAttr('onclick').attr("href","javascript:");
			$(".sort-ascending").removeAttr("class").removeAttr('onclick').attr("href","javascript:");
			
			removePageGreyOut();
			if($("#approvedProviderListSize").val() > 0){
				$("#approvedProvidersCount").show();
			}else{
				
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