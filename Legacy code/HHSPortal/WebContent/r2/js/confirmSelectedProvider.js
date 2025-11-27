//this ready function implements the functionality of validating whether or not
// the award amount has been entered
$(document).ready(function() {
	$('#txtAwardAmount').autoNumeric('init', {vMax: '9999999999999999',vMin:'0.00'});
	
	$("#Confirm").click(function(){
		$('#txtAwardAmount').parent().next().html("");
	});
	$("#confirmSelectedForm").validate({
		rules : {
			awardAmount : {
				required : true,
				minlength : 1,
				maxStrict : 9999999999999999.99
			}
		},
		messages : {
			awardAmount : {
				required : "! This field is required.",
				maxStrict : "! Please enter a value less than $10,000,000,000,000,000.00"
			}
		},
		submitHandler : function(form) {
			pageGreyOut();
			var txtAwardAmountValue = $("#txtAwardAmount").val();
			if(txtAwardAmountValue != '') {
				$("#txtAwardAmount").val(txtAwardAmountValue.replaceAll(",",""));
			}
			//if award amount is 0 then display message.
			if(txtAwardAmountValue<=0){
				$('#txtAwardAmount').parent().next().html(
				"! Award amount should be greater than 0." );
				removePageGreyOut();
			}else{
				if(validateTextArea("txtEnterComments")){
					document.confirmSelectedForm.submit();
				} else {
					removePageGreyOut();
					$("#ErrorDiv").html(invalidResponseMsg);
        			$("#ErrorDiv").show();
				}
				
			}
			
		},
		errorPlacement : function(error, element) {
			error.appendTo(element.parent().parent().find("span.error"));
		}
	});
	if($("#txtAwardAmount").val()==0){
		$("#txtAwardAmount").val('');
	}
});

//this function will close the confirm selected provider overlay 
function cancelOverlay(){
	$(".overlay").closeOverlay($(".alert-box-markSelected"), $(".exit-panel.mark-Selected"), "850px", null, "onReady");
}

//this function will close the confirm not selected provider overlay 
function cancelNotSelectedOverlay(){
	$(".overlay").closeOverlay($(".alert-box-markNotSelected"), $(".exit-panel.mark-Not-Selected"), "850px", null, "onReady");
}

//this function implements the functionality of page grey out
function nonSelected(value){
	pageGreyOut();
	if(validateTextArea("txtEnterComments")){
		document.confirmNotSelectedForm.submit();
	} else {
		removePageGreyOut();
		$("#ErrorDiv").html(invalidResponseMsg);
		$("#ErrorDiv").show();
	}
}

//this function sets the max limit
//updated in R5
function setMaxLength(obj,maxlimit){
	if(($(obj).val().length + $(obj).val().split("\n").length-1)  > maxlimit){
		$(obj).val($(obj).val().substring(0, maxlimit - $(obj).val().split("\n").length + 1));
		return false;
	}
}