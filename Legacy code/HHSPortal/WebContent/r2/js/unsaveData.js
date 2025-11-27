/**
 * This js file contains methods for data
 * that no longer need to be saved.
 */
var lastDataArray = new Array();
var commentsChange = false;
var formDataChange = false;
/** On page load
Updated Method in R4*/
$(document).ready(function() {
	/*Added Checks for R4 - TLC unsaved data comments.*/
	var isOkClick = false;
	var isSubmitOkClick = false;
	/*This function is called on click of budget tabs*/
	$(".procurementTabber li>a").click(function(e) {
		var itemClicked = $(this);
		var gridSubBudgetId= itemClicked.closest(".accContainer").find("#hdnGridSubBudgetId").val();
		if(isOkClick){
			isOkClick = false;
			clickOnGridArr = removeArrValue(clickOnGridArr, '-'+gridSubBudgetId+'_');
			changeOnGridCommentsArr = removeArrValue(changeOnGridCommentsArr, gridSubBudgetId);
			performTabTask(itemClicked);
			return false;
		}
			if((clickOnGridArr.length>0 && contains(clickOnGridArr,'-'+gridSubBudgetId+'_'))||(changeOnGridCommentsArr.length>0 && contains(changeOnGridCommentsArr,gridSubBudgetId)) ){
				var $self=$(this);
				e.preventDefault();
				e.stopPropagation();
				e.stopImmediatePropagation();
				/*displays dialogue box when screen is canceled  with unsaved data */
				$('<div id="dialogBox"></div>').appendTo('body')
				.html('<div><h6>You have unsaved data. <br />If you would like to leave this screen without saving your data, click <b>OK</b>. <br/>If you would like to save your data, click <b>Cancel</b> and save your data.</h6></div>')
				.dialog({
					modal: true, title: 'Unsaved Data', zIndex: 10000, autoOpen: true,
					width: 'auto', modal: true, resizable: false, draggable:false,
					dialogClass: 'dialogButtons',
					buttons: {
						OK: function () {
							/*Start R5: UX module, clean AutoSave Data*/
							deleteAutoSaveData();
							/*End R5: UX module, clean AutoSave Data*/
							isOkClick = true;
							$self.click();
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
				return false;
			}else {
				performTabTask(itemClicked);
			}
	});
	
	/**
	 * This function is called on click of return link
	Updated Method in R4
	*/
	$(".linkReturnValut>a,.linkReturnVault>a").click(function(e) {
		var itemClicked = $(this);
		var $self=$(this);
			if(clickOnGridArr.length>0 || commentsChange || changeOnGridCommentsArr.length>0 || formDataChange){
				e.preventDefault();
				e.stopPropagation();
				e.stopImmediatePropagation();
				$('<div id="dialogBox"></div>').appendTo('body')
				.html('<div><h6>You have unsaved data. <br />If you would like to leave this screen without saving your data, click <b>OK</b>. <br/>If you would like to save your data, click <b>Cancel</b> and save your data.</h6></div>')
				.dialog({
					modal: true, title: 'Unsaved Data', zIndex: 10000, autoOpen: true,
					width: 'auto', modal: true, resizable: false, draggable:false,
					dialogClass: 'dialogButtons',
					buttons: {
						OK: function () {
							pageGreyOut();
							/*Start R5: UX module, clean AutoSave Data*/
							deleteAutoSaveData();
							/*End R5: UX module, clean AutoSave Data*/
							document.location = $self.attr('href');
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
				return false;
			}else {
				pageGreyOut();
			}
	});
	
	/**
	 * This function is called on submit button on budget screens
	 Updated Method in R4
	 */
	$("#BudgetSubmitId").click(function(e) {
		var itemClicked = $(this);
		var $self=$(this);
		if(isSubmitOkClick){
			isSubmitOkClick = false;
			if(validateComment()){
			openOverlay();
			}else{
				$("#errorGlobalMsg").html(invalidResponseMsg);
				$("#errorGlobalMsg").show();
			}
			return false;
		}
			if(clickOnGridArr.length>0 || changeOnGridCommentsArr.length>0 || formDataChange){
				e.preventDefault();
				e.stopPropagation();
				e.stopImmediatePropagation();
				$('<div id="dialogBox"></div>').appendTo('body')
				.html('<div><h6>You have unsaved data. <br />If you would like to leave this screen without saving your data, click <b>OK</b>. <br/>If you would like to save your data, click <b>Cancel</b> and save your data.</h6></div>')
				.dialog({
					modal: true, title: 'Unsaved Data', zIndex: 10000, autoOpen: true,
					width: 'auto', modal: true, resizable: false, draggable:false,
					dialogClass: 'dialogButtons',
					buttons: {
						OK: function () {
							/*Start R5: UX module, clean AutoSave Data*/
							deleteAutoSaveData();
						/*	End R5: UX module, clean AutoSave Data*/
							isSubmitOkClick =true;
							$self.click();
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
				return false;
			}else {
				if(validateComment()){
					openOverlay();
					}else{
						$("#errorGlobalMsg").html(invalidResponseMsg);
						$("#errorGlobalMsg").show();
						return false;
					}
			}
	});
	
	/**This function is called on click of budget tabs*/
	$("#financeTabs li>a").click(function(e) {
		if( lbIsTAskAssign=="false" ||  lbIsScreenLocked=="false"){
			e.preventDefault();
			e.stopPropagation();
			e.stopImmediatePropagation();
			return false;
		}
		var itemClicked = $(this);
		var functionName = itemClicked.attr("clickFunction");
		var anothertabId = itemClicked.attr("anotherTabId");
		if(isOkClick){
			isOkClick = false;
			clickOnGridArr = removeArrValue(clickOnGridArr, anothertabId+'_');
			$("#"+functionName).click();
			return false;
		}
			if((clickOnGridArr.length>0  && contains(clickOnGridArr,anothertabId+'_') ) || formDataChange){
				var $self=$(this);
				e.preventDefault();
				e.stopPropagation();
				e.stopImmediatePropagation();
				$('<div id="dialogBox"></div>').appendTo('body')
				.html('<div><h6>You have unsaved data. <br />If you would like to leave this screen without saving your data, click <b>OK</b>. <br/>If you would like to save your data, click <b>Cancel</b> and save your data.</h6></div>')
				.dialog({
					modal: true, title: 'Unsaved Data', zIndex: 10000, autoOpen: true,
					width: 'auto', modal: true, resizable: false, draggable:false,
					dialogClass: 'dialogButtons',
					buttons: {
						OK: function () {
							/*Start R5: UX module, clean AutoSave Data*/
							deleteAutoSaveData();
							/*End R5: UX module, clean AutoSave Data*/
							isOkClick = true;
							$self.click();
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
				return false;
			}else {
				$("#"+functionName).click();
			}
	});	
	
});


/**This function is called on click budget Tabs*/
function performTabTask(element){
	showCBGridTabsJSP(element.attr("jspname"), element.closest(".accContainer").find("#hdnGridDivId").val(), element.closest(".accContainer").find("#hdnGridSubBudgetId").val(),element.closest(".accContainer").find("#hdnGridParentSubBudgetId").val());
}

/**This function checks if array contains passed value*/
function  contains(arr, findValue) {
    var i = arr.length;
     
    while (i--) {
        if (arr[i].indexOf(findValue)!=-1) return true;
    }
    return false;
}

/**This function removes particular value if array contains passed value*/
function removeArrValue(arr, value) {
    for ( var i = 0; i < arr.length; i++) {
     if (arr[i].indexOf(value)!=-1) {
       arr.splice(i,1);
      break;
     }
    }
    return arr;
   }

/**This function resets the flag*/
function resetFlag(){
	commentsChange = false;
	formDataChange = false;
}

/**change the status of fields*/
function setFieldChangeFlag(){
	formDataChange = true;
}

/** This function is called on click of return link
Updated Method in R4*/
$("#toolsIconUlID li a").click(function(e) {
	var itemClicked = $(this);
	var $self=$(this);
	if(!$(this).hasClass("active")){
		
		if(clickOnGridArr.length>0 || commentsChange || changeOnGridCommentsArr.length>0  || formDataChange){
			e.preventDefault();
			e.stopPropagation();
			e.stopImmediatePropagation();
			$('<div id="dialogBox"></div>').appendTo('body')
			.html('<div><h6>You have unsaved data. <br />If you would like to leave this screen without saving your data, click <b>OK</b>. <br/>If you would like to save your data, click <b>Cancel</b> and save your data.</h6></div>')
			.dialog({
				modal: true, title: 'Unsaved Data', zIndex: 10000, autoOpen: true,
				width: 'auto', modal: true, resizable: false, draggable:false,
				dialogClass: 'dialogButtons',
				buttons: {
					OK: function () {
						pageGreyOut();
						/*Start R5: UX module, clean AutoSave Data*/
						deleteAutoSaveData();
						/*End R5: UX module, clean AutoSave Data*/
						document.location = $self.attr('href');
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
			return false;
		}else {
			pageGreyOut();
			// Start QC 9404 R 8.0 Logout from auto-approval maintenance
			if( $self.attr('href').indexOf('logout=logout') != -1  )
				{
				 	console.log('IF Logout!!!');
				 	var newref = $self.attr('href') + '&app_menu_name=logout_icon';
				 	console.log(newref);
				 	document.location = $self.attr('href') + '&app_menu_name=logout_icon';
				}
			else
				{
					console.log('ELSE - not logout');
					document.location = $self.attr('href');
				}
			// End QC 9404 R 8.0 Logout from auto-approval maintenance
		 }
	}		
});

/**This function is called on click of return link
Updated Method in R4*/
$("#nyc_header_ul li a").click(function(e) {
	var itemClicked = $(this);
	var $self=$(this);
	if(!$(this).hasClass("active")){
		if(clickOnGridArr.length>0 || commentsChange ||changeOnGridCommentsArr.length>0 || formDataChange){
			e.preventDefault();
			e.stopPropagation();
			e.stopImmediatePropagation();
			$('<div id="dialogBox"></div>').appendTo('body')
			.html('<div><h6>You have unsaved data. <br />If you would like to leave this screen without saving your data, click <b>OK</b>. <br/>If you would like to save your data, click <b>Cancel</b> and save your data.</h6></div>')
			.dialog({
				modal: true, title: 'Unsaved Data', zIndex: 10000, autoOpen: true,
				width: 'auto', modal: true, resizable: false, draggable:false,
				dialogClass: 'dialogButtons',
				buttons: {
					OK: function () {
						pageGreyOut();
						/*Start R5: UX module, clean AutoSave Data*/
						deleteAutoSaveData();
						/*End R5: UX module, clean AutoSave Data*/
						document.location = $self.attr('href');
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
			return false;
		}else {
			pageGreyOut();
			document.location = $self.attr('href');
		}
	}
});

