//This js is for create organization account page

// this function is for tabs, dialog link, date picker, slider,process bar and hover  
$(function() {

	// Accordion
	$("#accordion").accordion();

	// Tabs
	$('#tabs').tabs();
	$('#newTabs').tabs();
	// Dialog
	$('#dialog').dialog({
		autoOpen : false,
		width : 600,
		buttons : {
			"Ok" : function() {
				$(this).dialog("close");
			},
			"Cancel" : function() {
				$(this).dialog("close");
			}
		}
	});

	// Dialog Link
	$('#dialog_link').click(function() {
		$('#dialog').dialog('open');
		return false;
	});

	// Slider
	$('#slider').slider({
		range : true,
		values : [ 17, 67 ]
	});

	// Progressbar
	$("#progressbar").progressbar({
		value : 20
	});

	//hover states on the static widgets
	$('#dialog_link, ul#icons li').hover(function() {
		$(this).addClass('ui-state-hover');
	}, function() {
		$(this).removeClass('ui-state-hover');
	});

});

var jspValidationErrorMsg = "";

// this function provide navigation to account request submitted page
function navigateToRequestSubmit() {

	$("#errorUL").removeClass("errorMessages");
	$("#errorUL").html("");
	jspValidationErrorMsg = "";
	emailFlag = true;
	passwordFlag = true;
	minimumFlag = true;
	if (jsButtonSubmit('sbmtAcctReq', 'do-add')) { // if condition starts.
		var booleanFlag = true;
		document.getElementById("buttonHit").value = "sbmtAcctReq";
		document.createadminacct.action = document.createadminacct.action
				+ '&next_action=orgAcctRequestSubmitted';
		document.createadminacct.submit();
	} else {
		booleanFlag = false;
		document.getElementById("buttonHit").value = "sbmtAcctReq";
		document.createadminacct.action = document.createadminacct.action
				+ '&removeNavigator=true&accoutRequestmodule=true';
	}
	if (!booleanFlag) {
		var errorDivHtml = document.getElementById("errorUL").innerHTML;
		$("#errorUL").html(errorDivHtml + jspValidationErrorMsg);
		return booleanFlag;
	} else {
		document.getElementById("buttonHit").value = "sbmtAcctReq";
		document.forms[0].action = document.forms[0].action
				+ '&removeNavigator=true&accoutRequestmodule=true';
		document.forms[0].submit();
	}

}
//this function is for enabling entity type field if organization selected is for-profit
function disableEntityType(orgCorpStruc) {

	var selIndex = document.createadminacct.orgCorpStruc.selectedIndex;
	var comboValue = document.createadminacct.orgCorpStruc.options[selIndex].value;

	if (comboValue == "For Profit") {
		$("#entityTypeDiv").show();
		$("#entityType").show();
	} else {
		document.getElementById("entityTypeDiv").style.display = 'none';
		document.getElementById("entityType").style.display = 'none';
		document.getElementById("othersDiv").style.display = 'none';
		document.getElementById("others").style.display = 'none';
		document.getElementById("others").value = '';
		document.getElementById("entityType").selectedIndex = 0;
	}
}

// this function is for enabling other entity type field if entity type is selected as 'others'
function disableOthers(entityType) {
	var selIndex = document.createadminacct.entityType.selectedIndex;
	var comboValue = document.createadminacct.entityType.options[selIndex].value;
	if (comboValue == "Other") {
		document.getElementById("othersDiv").style.display = 'block';
		document.getElementById("others").style.display = 'block';
	} else {
		document.getElementById("othersDiv").style.display = 'none';
		document.getElementById("others").style.display = 'none';
	}
}

// this function is to enable CFO fields if "Yes" radio button is selected
function enableCFOFields() {
	document.getElementById("copyCFO").style.display = 'block';
	copyAdminInformationForCFO();
}
//this function is to disable CFO fields if "No" radio button is selected
function disableCFOFields() {

		document.getElementById("cfoFirstNameDiv").style.display = 'none';
		document.getElementById("cfoFirstName").style.display = 'none';
		document.getElementById("cfoMiddleNameDiv").style.display = 'none';
		document.getElementById("cfoMiddleName").style.display = 'none';
		document.getElementById("cfoLastNameDiv").style.display = 'none';
		document.getElementById("cfoLastName").style.display = 'none';
		document.getElementById("cfoPhNoDiv").style.display = 'none';
		document.getElementById("cfoPhNo").style.display = 'none';
		document.getElementById("cfoEmailDiv").style.display = 'none';
		document.getElementById("cfoEmail").style.display = 'none';
		document.getElementById("copyCFO").style.display = 'none';

		document.getElementById("cfoFirstName").value = '';
		document.getElementById("cfoMiddleName").value = '';
		document.getElementById("cfoLastName").value = '';
		document.getElementById("cfoPhNo").value = '';
		document.getElementById("cfoEmail").value = '';
		document.getElementById("copyAdminInfoForCfo").checked = false;
}

// This function copy admin information to organization member fields.
function copyAdminInformationForCEO(){
	if(document.getElementById("copyAdminInfoForCeo").checked){
		
		document.getElementById("ceoFirstName").value=document.getElementById("adminFirstName").value;
		document.getElementById("ceoMiddleName").value=document.getElementById("adminMiddleName").value;
		document.getElementById("ceoLastName").value=document.getElementById("adminLastName").value;
		document.getElementById("ceoPhNo").value=document.getElementById("adminPhNo").value;
		document.getElementById("ceoEmail").value=document.getElementById("adminEmail").value;
		
		document.getElementById("ceoFirstNameDiv").style.display = 'none';
		document.getElementById("ceoFirstName").style.display = 'none';
		document.getElementById("ceoMiddleNameDiv").style.display = 'none';
		document.getElementById("ceoMiddleName").style.display = 'none';
		document.getElementById("ceoLastNameDiv").style.display = 'none';
		document.getElementById("ceoLastName").style.display = 'none';
		document.getElementById("ceoPhNoDiv").style.display = 'none';
		document.getElementById("ceoPhNo").style.display = 'none';
		document.getElementById("ceoEmailDiv").style.display = 'none';
		document.getElementById("ceoEmail").style.display = 'none';
		
	}else if(document.getElementById("copyAdminInfoForCeo").checked == false){
		
		document.getElementById("ceoFirstName").value='';
		document.getElementById("ceoMiddleName").value='';
		document.getElementById("ceoLastName").value='';
		document.getElementById("ceoPhNo").value='';
		document.getElementById("ceoEmail").value='';
		
		document.getElementById("ceoFirstNameDiv").style.display = 'block';
		document.getElementById("ceoFirstName").style.display = 'block';
		document.getElementById("ceoMiddleNameDiv").style.display = 'block';
		document.getElementById("ceoMiddleName").style.display = 'block';
		document.getElementById("ceoLastNameDiv").style.display = 'block';
		document.getElementById("ceoLastName").style.display = 'block';
		document.getElementById("ceoPhNoDiv").style.display = 'block';
		document.getElementById("ceoPhNo").style.display = 'block';
		document.getElementById("ceoEmailDiv").style.display = 'block';
		document.getElementById("ceoEmail").style.display = 'block';
	}

}

//This function copy admin information to organization member fields.
function copyAdminInformationForCFO(){
	
	if(document.getElementById("copyAdminInfoForCfo").checked){
		
		document.getElementById("cfoFirstName").value=document.getElementById("adminFirstName").value;
		document.getElementById("cfoMiddleName").value=document.getElementById("adminMiddleName").value;
		document.getElementById("cfoLastName").value=document.getElementById("adminLastName").value;
		document.getElementById("cfoPhNo").value=document.getElementById("adminPhNo").value;
		document.getElementById("cfoEmail").value=document.getElementById("adminEmail").value;
		
		document.getElementById("cfoFirstNameDiv").style.display = 'none';
		document.getElementById("cfoFirstName").style.display = 'none';
		document.getElementById("cfoMiddleNameDiv").style.display = 'none';
		document.getElementById("cfoMiddleName").style.display = 'none';
		document.getElementById("cfoLastNameDiv").style.display = 'none';
		document.getElementById("cfoLastName").style.display = 'none';
		document.getElementById("cfoPhNoDiv").style.display = 'none';
		document.getElementById("cfoPhNo").style.display = 'none';
		document.getElementById("cfoEmailDiv").style.display = 'none';
		document.getElementById("cfoEmail").style.display = 'none';
		
	}else if(document.getElementById("copyAdminInfoForCfo").checked == false){
		
		document.getElementById("cfoFirstName").value='';
		document.getElementById("cfoMiddleName").value='';
		document.getElementById("cfoLastName").value='';
		document.getElementById("cfoPhNo").value='';
		document.getElementById("cfoEmail").value='';
		
		document.getElementById("cfoFirstNameDiv").style.display = 'block';
		document.getElementById("cfoFirstName").style.display = 'block';
		document.getElementById("cfoMiddleNameDiv").style.display = 'block';
		document.getElementById("cfoMiddleName").style.display = 'block';
		document.getElementById("cfoLastNameDiv").style.display = 'block';
		document.getElementById("cfoLastName").style.display = 'block';
		document.getElementById("cfoPhNoDiv").style.display = 'block';
		document.getElementById("cfoPhNo").style.display = 'block';
		document.getElementById("cfoEmailDiv").style.display = 'block';
		document.getElementById("cfoEmail").style.display = 'block';
	}

}

//This function copy admin information to organization member fields.
function copyAdminInformationForPresident(){
	if(document.getElementById("copyAdminInfoForPres").checked){
		
		document.getElementById("presFirstName").value=document.getElementById("adminFirstName").value;
		document.getElementById("presMiddleName").value=document.getElementById("adminMiddleName").value;
		document.getElementById("presLastName").value=document.getElementById("adminLastName").value;
		document.getElementById("presPhNo").value=document.getElementById("adminPhNo").value;
		document.getElementById("presEmail").value=document.getElementById("adminEmail").value;
		
		document.getElementById("presFirstNameDiv").style.display = 'none';
		document.getElementById("presFirstName").style.display = 'none';
		document.getElementById("presMiddleNameDiv").style.display = 'none';
		document.getElementById("presMiddleName").style.display = 'none';
		document.getElementById("presLastNameDiv").style.display = 'none';
		document.getElementById("presLastName").style.display = 'none';
		document.getElementById("presPhNoDiv").style.display = 'none';
		document.getElementById("presPhNo").style.display = 'none';
		document.getElementById("presEmailDiv").style.display = 'none';
		document.getElementById("presEmail").style.display = 'none';
		
	}else if(document.getElementById("copyAdminInfoForPres").checked == false){
		
		document.getElementById("presFirstName").value='';
		document.getElementById("presMiddleName").value='';
		document.getElementById("presLastName").value='';
		document.getElementById("presPhNo").value='';
		document.getElementById("presEmail").value='';
		
		document.getElementById("presFirstNameDiv").style.display = 'block';
		document.getElementById("presFirstName").style.display = 'block';
		document.getElementById("presMiddleNameDiv").style.display = 'block';
		document.getElementById("presMiddleName").style.display = 'block';
		document.getElementById("presLastNameDiv").style.display = 'block';
		document.getElementById("presLastName").style.display = 'block';
		document.getElementById("presPhNoDiv").style.display = 'block';
		document.getElementById("presPhNo").style.display = 'block';
		document.getElementById("presEmailDiv").style.display = 'block';
		document.getElementById("presEmail").style.display = 'block';
	}
}
//jquery ready function- executes the script after page loading
$(document).ready(function() {
	$('#orgLegalName').alphanumeric({
		allow : "-,.' "
	});
	$('#orgCorpStruc').alphanumeric({
		allow : "-,.' "
	});
	$('#others').alphanumeric({
		allow : "-,.' "
	});
	$('#entityType').alphanumeric({
		allow : "-,.' "
	});
	$('#acctPeriodFrom').alphanumeric({
		allow : "-,.' "
	});
	$('#acctPeriodTo').alphanumeric({
		allow : "-,.' "
	});
	$('#execAddLine1').alphanumeric({
		allow : "-,.#' \\\"",nchars:"_"
	});
	$('#execAddLine2').alphanumeric({
		allow : "-,.#' \\\"",nchars:"_"
	});
	$('#execCity').alphanumeric({
		allow : "-,.' \\\"",nchars:"_0123456789"
	});
	$('#execState').alphanumeric({
		allow : "-,.' "
	});
	$('#execZipCode').alphanumeric({
		allow : "-,.' "
	});
	$('#adminOfficeTitle').alphanumeric({
		allow : "-,.' "
	});
	$('#presPhNo').alphanumeric({
		allow : "-,.' "
	});
	$('#orgAltName').alphanumeric({
		allow : "-,.' "
	});
	$('#orgDunNo').numeric();
	$("input[name='execZipCode']").fieldFormatter("XXXXX");
	$("input[name='execPhNo']").fieldFormatter("XXX-XXX-XXXX");
	$("input[name='ceoPhNo']").fieldFormatter("XXX-XXX-XXXX");
	$("input[name='cfoPhNo']").fieldFormatter("XXX-XXX-XXXX");
	$("input[name='presPhNo']").fieldFormatter("XXX-XXX-XXXX");
	$("input[name='adminPhNo']").fieldFormatter("XXX-XXX-XXXX");
	$("input[name='execFaxNo']").fieldFormatter("XXX-XXX-XXXX");

	$('#isCFO').dblclick(function() {
		$('#isCFO').prop('checked', false);
		enableCFOFields();
	});

	var pageW = $(document).width();
	var pageH = $(document).height();

	$("select.terms").change(function() {
		var str = "";
		$("select.terms option:selected").each(function() {
			str = $(this).text();

			if (str == 'Select file to upload') {
				$(".alert-box").show();
				$(".overlay").show();
				$(".overlay").width(pageW);
				$(".overlay").height(pageH);
			} else if (str == 'View uploaded document') {
				$(".alert-box").show();
				$(".overlay").show();
				$(".overlay").width(pageW);
				$(".overlay").height(pageH);
			}
		});

	});

	$("a.exit-panel").click(function() {
		$(".alert-box").hide();
		$(".overlay").hide();
	});

	$("#errorUL").removeClass("errorMessages");
	$("#errorUL").html("");


});


