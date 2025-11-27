/**
 * This file contains the methods that will handle 
 * the headers rendering on application page.
 */
var lastDataArray = new Array();
/**
 *  on page load
 * */
$(document).ready(function() {
	$("#returnProposalSummaryPage").click(function(){
		window.location.href = $("#ProposalSummaryAnchor").attr("href");
	});
	/* below line is modified as per defect 5598 as part of release 2.7.0*/
	$("a[id!='smallA'][id!='mediumA'][id!='largeA'][id!='helpIconId']").click(function(e) {
		if($("#appProvform").size() == 0)
		if($("input[value^='Save'][type='button']:visible, input[type='submit']:visible").size() > 0 
				&& $("#tabs-container").size() > 0
				&& !$(this).hasClass("byPassLink") && !$(this).hasClass("exit-panel")
				&& ($(this).parents("#tabs-container").length == 0 || $(this).attr("id") == "returnSummaryPage")
				&& !$(this).parent().hasClass("tabDisabled")){
			var $self=$(this);
			var isSame = true;
			if(lastDataArray != null && lastDataArray.length > 0){
				$.each(lastDataArray, function(i) {
					selectDeselectAllMultiSelect($("form[name='"+lastDataArray[i][0]+"']"), true);
					if(!$(lastDataArray[i][1]).compare($("form[name='"+lastDataArray[i][0]+"']").serializeArray())){
						isSame = false;
					}
					selectDeselectAllMultiSelect($("form[name='"+lastDataArray[i][0]+"']"), false);
				});
			}
			if(!isSame && lastDataArray != null & lastDataArray.length > 0){
				e.preventDefault();
				/* displays  a dialogue box when the screen is canceled with unsaved data*/
				$('<div id="dialogBox"></div>').appendTo('body')
				.html('<div><h6>You have unsaved data. <br />If you would like to leave this screen without saving your data, click <b>OK</b>. <br/>If you would like to save your data, click <b>Cancel</b> and save your data.</h6></div>')
				.dialog({
					modal: true, title: 'Unsaved Data', zIndex: 10000, autoOpen: true,
					width: 'auto', modal: true, resizable: false, draggable:false,
					dialogClass: 'dialogButtons',
					buttons: {
						OK: function () {
						/*	Start R5: UX module, clean AutoSave Data*/
							deleteAutoSaveData();
						/*	End R5: UX module, clean AutoSave Data*/
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
					/* displays  a dialogue box when the screen is canceled with unsaved data*/
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
	document.getElementById('greyedBackgroundR2').style.display='none';
});
$(window).load(function(){
	var ignoreForms = ["publishProcurementform", "releaseRfpform", "navigationForm", "appProvform", "rfpReleaseForm", "proposalDocsForm"];
	$("form").each(function(){
		if(typeof($(this).attr("name")) != "undefined" && $.inArray($(this).attr("name"), ignoreForms) < 0){
			selectDeselectAllMultiSelect($(this), true);
			lastDataArray[lastDataArray.length] = new Array($(this).attr("name"), $(this).serializeArray());
			selectDeselectAllMultiSelect($(this), false);
		}
	});
});

/**
 * function performing data change check on form
 * */
$.fn.backNextButton = function(callBackMethod){
	$(this).click(function(){
		var isSame = true;
		if(lastDataArray != null && lastDataArray.length > 0){
			$.each(lastDataArray, function(i) {
				if(!$(lastDataArray[i][1]).compare($("form[name='"+lastDataArray[i][0]+"']").serializeArray())){
					isSame = false;
				}
			});
		}
		if(!isSame && lastDataArray != null & lastDataArray.length > 0){
			$('<div id="dialogBox"></div>').appendTo('body')
			.html('<div><h6>You have unsaved data. <br />If you would like to leave this screen without saving your data, click <b>OK</b>. <br/>If you would like to save your data, click <b>Cancel</b> and save your data.</h6></div>')
			.dialog({
				modal: true, title: 'Unsaved Data', zIndex: 10000, autoOpen: true,
				width: 'auto', modal: true, resizable: false, draggable:false,
				dialogClass: 'dialogButtons',
				buttons: {
					OK: function () {
						callBackInWindow(callBackMethod);
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
			callBackInWindow(callBackMethod);
		}
	});
};

/**
 * navigate to tab clicked
Updated Method in R4
*/
function navigateToTab(elt){
	if(elt.find("input[type='hidden']").size() > 0){
		$("#navigationForm").find("#forAction").eq(0).val(elt.find("#forAction").val());
		$("#navigationForm").find("#render_action").eq(0).val(elt.find("#render_action").val());
		$("#navigationForm").find("#topLevelFromRequest").eq(0).val(elt.find("#topLevelFromRequest").val());
		$("#navigationForm").find("#midLevelFromRequest").eq(0).val(elt.find("#midLevelFromRequest").val());
		$("#navigationForm").find("#ES").eq(0).val(elt.find("#ES").val());
		pageGreyOut();
		document.navigationForm.submit();
	}
}

/** 
 * method to pre select all multiselect options
 * */
function selectDeselectAllMultiSelect(formObj, type){
	if(type){
		$(formObj).find("select[multiple='multiple']").each(function(){
			var selectedIndexes = "";
			$(this).find("option").each(function(i){
				if($(this).is(":selected")){
					selectedIndexes = i + "," + selectedIndexes
				}
			});
			var scrollPos = $(this).scrollTop();
			$(this).attr("scrollPos", scrollPos);
			$(this).attr("prevSelected", selectedIndexes);
		});
	}
	$(formObj).find("select[multiple='multiple'] option").attr("selected", type);
	if(!type){
		$(formObj).find("select[multiple='multiple']").each(function(){
			if($(this).attr("scrollPos")!=null && typeof $(this).attr("scrollPos") !='undefined'
				&& $(this).attr("prevSelected")!=null && typeof $(this).attr("prevSelected") !='undefined'){
				$(this).scrollTop($(this).attr("scrollPos"));
				var selectedIndexes = $(this).attr("prevSelected");
				var selectedIndexesArray = selectedIndexes.split(",");
				$(this).find("option").each(function(i){
					if($.inArray(i+"", selectedIndexesArray) != -1){
						$(this).attr("selected", true);
					}
				});
			}
		});
	}
}