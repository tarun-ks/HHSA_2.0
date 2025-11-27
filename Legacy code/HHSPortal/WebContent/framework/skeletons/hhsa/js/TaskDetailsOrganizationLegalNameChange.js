// AJAX Method	
$(function() {
		$("#accordion").accordion();
		$('#tabs li').removeClass('ui-state-default ui-corner-top ui-tabs-selected ui-state-active');
		$('#tabs').tabs();
		$('#newTabs').tabs();
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
		$('#dialog_link, ul#icons li').hover(function() {
			$(this).addClass('ui-state-hover');
		}, function() {
			$(this).removeClass('ui-state-hover');
		});
	});

/**
 * This method call AJAX Method to get document or form Content 
 * 
 **/
function getContent(url) {
	postRequest(url);
}

/**
 * AJAX Method 
 * 
 **/
function postRequest(strURL) {
		var xmlHttp;
		if (window.XMLHttpRequest) { 
			var xmlHttp = new XMLHttpRequest();
		} else if (window.ActiveXObject) { 
			var xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
		}
		xmlHttp.open('POST', strURL, true);
		xmlHttp.setRequestHeader('Content-Type',
				'application/x-www-form-urlencoded');
		xmlHttp.onreadystatechange = function() {
			if (xmlHttp.readyState == 4) {
			
				updatepage(xmlHttp.responseText);
			}
		};
		xmlHttp.send(strURL);
	}
function updatepage(str) {
	$("#linkDiv").html(str);
}

/**
 * This method display document information
 * 
 **/
function openInfo(documentId){
		var url = $("#contextPathSession").val()+"GetContent.jsp?documentID="+documentId;
		postRequest(url);
		}

/**
 * This method set CSS class and Style
 * 
 **/
function requestSubmit(){
	 document.getElementById("arrowreq").className = "taskSelected";
	 document.getElementById("arrowbasic").className = "taskNormal";
	 document.getElementById("TasksForms").style.display="none";
	 document.getElementById("Taskslinks").style.display="block";
}

/**
 * Method to display error or success Message 
 * 
 **/
function showMe (it, box) {
	if(box.id=='box'){
		vis = "none";
	}else{
		vis = "block";
	}
document.getElementById(it).style.display = vis;
} 

/**
 * Function sets the maximum length of Textarea.
 * updated in R5
 */
function setMaxLength(obj,maxlimit){
	if(($(obj).val().length + $(obj).val().split("\n").length-1)  > maxlimit){
		$(obj).val($(obj).val().substring(0, maxlimit - $(obj).val().split("\n").length + 1));
		return false;
	}
}