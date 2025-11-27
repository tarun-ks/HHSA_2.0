// This will execute when EditDocumentPRoperties link is clicked
function editDocument(){
	$("input[id^=editInput]").each(function(){
		$(this).show();
	});
	$("span[id^=hideWhenClicked]").each(function(){
		$(this).hide();
	});
	$("label[id^=requiredLabel]").each(function(){
		$(this).show();
	});
	$("img[id^=openWhenClicked]").show();
	$("#edit").hide();
	$("#buttonholder").show();
	
	pageGreyOut();
	var v_parameter = "";
	var urlAppender = $("#editDocumentPropertiesResource").val();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(e) {
			removePageGreyOut();
		},
		error : function(data, textStatus, errorThrown) {
			removePageGreyOut();
		}
	});
}

// This will execute when Cancel button is clicked from edit document properties screen
function cancelEdit(){
	$("input[id^=editInput]").each(function(){
		$(this).hide();
	});
	$("span[id^=hideWhenClicked]").each(function(){
		$(this).show();
	});
	$("img[id^=openWhenClicked]").each(function(){
		$(this).hide();
	});
	$("label[id^=requiredLabel]").each(function(){
		$(this).hide();
	});
	$("label[class^=error]").each(function(){
		$(this).hide();
	});
	$("#edit").show();
	$("#buttonholder").hide();
}
