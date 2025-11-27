/**
 * This function is used to when user click on the save and save next button
 * @param pageToDirect redirect to the page 
 * @param bussAppId business application id
 * @param section section of the application
 * @param subSection sub section of the application
 */
function selectAllAndSubmit(pageToDirect,bussAppId,section,subSection) {
	var boxList = $('input:checkbox:checked');
	if (boxList.length == 0) {
		document.geographyform.next_action.value="nodirect";
		if($("#errorMessage").html()=='' || $("#errorMessage").html()==undefined){
			$("#errorMessage").append("You must select at least one geography");
		}
	}else {
		document.geographyform.next_action.value=pageToDirect;
		document.geographyform.action = document.geographyform.action+"&business_app_id="+bussAppId+"&section="+section+"&subsection="+subSection;
		document.geographyform.submit();
	}
}

/**
 * This function called user click on the other check box
 * @param id
 */
function EmptyListItem(id) {
	var mycheckbox = document.getElementById(id);
	var boxList = $('input:checkbox');
	if(mycheckbox.checked){
		boxList.each(function(i){
			var checkBoxId = $(this).attr("id");
			if(checkBoxId!="close"){
				this.checked = false;
				this.disabled=true;
			}
		});
		$("#errorMessage").html("");
		var accordionHeaderIds= $("div[id*='accordionHeaderId']");
		accordionHeaderIds.each(function(){
			$(this).css({
				'background':"url('../framework/skins/hhsa/images/arrowExpand.png') no-repeat scroll 99% center #4297E2",
				'cursor':'auto',
				'background-color':'#C1C1C1'});
			$(this).next().removeClass();
			$(this).next().addClass("close");
			$(this).nextAll().hide();
		});
	}
	else{
		boxList.each(function(i){
			var checkBoxId = $(this).attr("id");
			if(checkBoxId!="close"){
				this.checked = false;
				this.disabled=false;
			}
		});
		var accordionHeaderIds= $("div[id*='accordionHeaderId']");
		accordionHeaderIds.each(function(){
			$(this).css({
				'background':"url('../framework/skins/hhsa/images/arrowExpand.png') no-repeat scroll 99% center #4297E2",
				'cursor':'pointer',
				'background-color':'#4297E2'});
			$(this).next().removeClass();
			$(this).next().addClass("close");
			$(this).nextAll().hide();
		});
	}
}

/**
 * This function called when page is getting loaded
 */
var lastData = null;
$(function(){
	if($("#saveAndNextButtonId").size()>0){
		$("#buttonName").html("Save and Next");
	}else{
		$("#buttonName").html("Save");
	}
	
	if($("#readOnlyValueId").val()=='true'){
		var accordionHeaderIds= $("div[id*='accordionHeaderId']");
		accordionHeaderIds.each(function(){
			$(this).css({
				'background':"url('../framework/skins/hhsa/images/arrowCollapse.png') no-repeat scroll 99% center #4297E2",
				'cursor':'pointer',
				'background-color':'#4297E2'});
			$(this).nextAll().show();
			$(this).attr("onclick","");
		});
		
	}else{
		if($("#close").attr("checked")){
			var accordionHeaderIds= $("div[id*='accordionHeaderId']");
			accordionHeaderIds.each(function(){
				$(this).css({
					'background':"url('../framework/skins/hhsa/images/arrowExpand.png') no-repeat scroll 99% center #4297E2",
					'cursor':'auto',
					'background-color':'#C1C1C1'});
				$(this).nextAll().hide();
			});
		}else{
			var accordionHeaderIds= $("div[id*='accordionHeaderId']");
			accordionHeaderIds.each(function(){
				$(this).css({
					'background':"url('../framework/skins/hhsa/images/arrowExpand.png') no-repeat scroll 99% center #4297E2",
					'cursor':'pointer',
					'background-color':'#4297E2'});
				$(this).nextAll().hide();
			});
		}
	}
	// Tabs
	if($("#close").attr("checked")){
		var boxList = $('input:checkbox');
		boxList.each(function(i){
			var checkBoxId = $(this).attr("id");
			if(checkBoxId!="close"){
				this.checked = false;
				this.disabled=true;
			}
		});
	}
	$('#tabs').tabs();
	$('#newTabs').tabs();
	$('#dialog').dialog({
		autoOpen: false,
		width: 600,
		buttons: {
			"Ok": function() {
				$(this).dialog("close");
			},
			"Cancel": function() {
				$(this).dialog("close");
			}
		}
	});
	$(".accContainer").each(function(i){
		var isValid = true;
		var checkBoxId = $(this).find("input:checkbox");
		checkBoxId.each(function(i){
			if(i>0){
				if($(this).attr("checked")){
				}else{
					isValid = false;
				}
			}
		}); 
		if(isValid){
			var topId = $(checkBoxId)[0];
			$(topId).attr("checked",true);
		}
	});
	var $form = $("#Geography").closest('form');
	lastData = $form.serializeArray();
});

/**
 * This function check un check all the check boxes
 * @param obj 
 * @param key
 */
function  enableAllTextbox(obj,key){
	var boxList = $('input:checkbox');
	if($(obj).attr("checked")){
		boxList.each(function(i){
			var checkBoxId = $(this).attr("id");
			if(checkBoxId==key){
				this.checked = true;
			}
		});
	}
	else{
		boxList.each(function(i){
			var checkBoxId = $(this).attr("id");
			if(checkBoxId==key){
				this.checked = false;
			}
		}); 
	}	
	var checkList = $('input:checkbox:checked');
	if (checkList.length == 0) {
		$("#close").removeAttr("checked");
		$("#close").removeAttr("disabled");
	}
}
//This method remove all selected box value when a checkbox is checked
function removeSelectAll(obj,key,topCheckBoxId){
	var boxList = $('input:checkbox');

	if($(obj).attr("checked")==undefined || $(obj).attr("checked")==false){
		boxList.each(function(i){
			var checkBoxId = $(this).attr("id");
			if(checkBoxId==topCheckBoxId){
				this.checked = false;
			}
		}); 
	}
	$(".accContainer").each(function(i){
		var isValid = true;
		var checkBoxId = $(this).find("input:checkbox");
		checkBoxId.each(function(i){
			if(i>0){
				if($(this).attr("checked")){
				}else{
					isValid = false;
				}
			}
		}); 
		if(isValid){
			var topId = $(checkBoxId)[0];
			$(topId).attr("checked",true);
		}
	});  
	var checkList = $('input:checkbox:checked');
	if (checkList.length == 0) {
		$("#close").removeAttr("checked");
		$("#close").removeAttr("disabled");
	}
}

/**
 * This function is used to expand and collapse all the accordion  
 * @param expandCollapse
 */
function collapseExpandAll(expandCollapse){
	if($("#close").attr("checked")!='checked'){
		var topHeaderIds= $("div[id*='accordionHeaderId']");
		topHeaderIds.each(function(){
			if(expandCollapse=='collapseAll'){
				$(this).next().removeClass();
				$(this).next().addClass("close");
				$(this).nextAll().hide();
				$(this).attr("style","background:url('../framework/skins/hhsa/images/arrowExpand.png') no-repeat scroll 99% center #4297E2");
			}else{
				$(this).next().removeClass();
				$(this).next().addClass("custDataRowHead1");
				$(this).nextAll().show();
				$(this).attr("style","background:url('../framework/skins/hhsa/images/arrowCollapse.png') no-repeat scroll 99% center #4297E2");
			}
		});
	}
}

/**
 * This function is used when user click on the accordion
 * @param obj object of the accordion
 */
function displayAccortion(obj){
	if($("#close").attr("checked")!='checked'){
		var openShow = $(obj).next().attr("class");
		if(openShow=='custDataRowHead1'){
			$(obj).next().removeAttr("class");
			$(obj).next().addClass("close");
			$(obj).attr("style","background:url('../framework/skins/hhsa/images/arrowExpand.png') no-repeat scroll 99% center #4297E2");
			$(obj).nextAll().hide();
		}else{
			$(obj).next().removeAttr("class");
			$(obj).attr("style","background:url('../framework/skins/hhsa/images/arrowCollapse.png') no-repeat scroll 99% center #4297E2");
			$(obj).next().addClass("custDataRowHead1");
			$(obj).nextAll().show();
		}
	}
}

/**
 * This function called when user click on the back button
 * @param pageToDirect page to redirect
 * @param bussAppId business application id
 * @param section section of the page
 * @param subSection sub section of the page
 */
function GoToPreviousPage(pageToDirect,bussAppId,section,subSection) {
	var $form = $("#Geography").closest('form');
	var isSame = false;
	data = $form.serializeArray();
	if(lastData != null){
		if($(lastData).compare($(data))){
			isSame = true;
		}
	}
	if(!isSame && lastData != null){
		$('<div id="dialogBox"></div>').appendTo('body')
		.html('<div><h6>You have unsaved data. <br />If you would like to leave this screen without saving your data, click <b>OK</b>. <br/>If you would like to save your data, click <b>Cancel</b> and save your data.</h6></div>')
		.dialog({
			modal: true, title: 'Unsaved Data', zIndex: 10000, autoOpen: true,
			width: 'auto', modal: true, resizable: false, draggable:false,
			dialogClass: 'dialogButtons',
			buttons: {
				OK: function () {
					if(pageToDirect != 'refresh')
					{
					document.geographyform.next_action.value=pageToDirect;
					document.geographyform.action = document.geographyform.action+"&business_app_id="+bussAppId+"&section="+section+"&subsection="+subSection;
					document.geographyform.submit();
					$(this).dialog("close");
					}
					else{
						location.href=$("#contextPathSession").val()+"/portal/hhsweb.portal?_nfpb=true&_st=&_windowLabel=portletInstance_21&_urlType=render&wlpportletInstance_21_next_action=open&wlpportletInstance_21_app_menu_name=header_organization_information&wlpportletInstance_21_subsection=geography&wlpportletInstance_21_action=orgBasicInformation&wlpportletInstance_21_section=basics";
					}
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
		if(pageToDirect != 'refresh')
			{
		document.geographyform.next_action.value=pageToDirect;
		document.geographyform.action = document.geographyform.action+"&business_app_id="+bussAppId+"&section="+section+"&subsection="+subSection;
		document.geographyform.submit();
			}
		else{
			location.href=$("#contextPathSession").val()+"/portal/hhsweb.portal?_nfpb=true&_st=&_windowLabel=portletInstance_21&_urlType=render&wlpportletInstance_21_next_action=open&wlpportletInstance_21_app_menu_name=header_organization_information&wlpportletInstance_21_subsection=geography&wlpportletInstance_21_action=orgBasicInformation&wlpportletInstance_21_section=basics";
		}
	}
}

