/**
 * On page load
 */
// This function is modified as part of defect 5629 for release 2.6.0
$(document).ready(function(){
	$("#procurementValue").autoNumeric('init', {vMax: '9999999999999999',vMin:'0.00'});	
	// The below if else condition are modified as per defect 5629 for release 2.6.0 
	$("#viewCoF").attr("disabled","disabled");
	$("#submitCoF").attr("disabled","disabled");
	$("#submitCoF").hide();
	if(status == "Not Submitted"){
		$("#submitCoF").removeAttr("disabled");
		$("#submitCoF").show();
	}else if(status == "In Review"){
		$("#viewCoF").removeAttr("disabled");
	}else if(status == "Approved"){
		$("#viewCoF").removeAttr("disabled");
	}});
//This function open the COF
function OpenCOF(){
	document.myform.action = $("#viewProcCOF").val();
	window.open($("#myform").attr("action")); // opens procurement COF doc in new window
}

//This function is called on click of submit Cof button 
function submit1(){
	// Validation for total and Proc value
if(clickOnGridArr.length>0){
	var $self=$(this);
	$('<div id="dialogBox"></div>').appendTo('body')
	.html('<div><h6>You have unsaved data. <br />If you would like to leave this screen without saving your data, click <b>OK</b>. <br/>If you would like to save your data, click <b>Cancel</b> and save your data.</h6></div>')
	.dialog({
		modal: true, title: 'Unsaved Data', zIndex: 10000, autoOpen: true,
		width: 'auto', modal: true, resizable: false, draggable:false,
		dialogClass: 'dialogButtons',
		buttons: {
			OK: function () {
				finalSubmit();
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
	
}else {
	finalSubmit();
}
}

// This function is called on click of submit Cof button after Submit1 function
function finalSubmit(){
	pageGreyOut();
	var totalProcAmount = $('#table_ProcCoAAllocation tbody tr:eq(1) td:last').html().replace('$', '').replaceAll(',', '');
	
	var procAmount = $("#procurementValue").html().replace('$', '').replaceAll(',', '');
	
	if($.trim(new Big(totalProcAmount)) != $.trim(new Big(procAmount))){
		$("#errorGlobalMsg").show();
		$("#errorGlobalMsg").html(totalNotEqual);
		removePageGreyOut();
	}else{		
		document.myform.action = $("#launchWFproc").val();
		document.myform.submit();		
	}
}


