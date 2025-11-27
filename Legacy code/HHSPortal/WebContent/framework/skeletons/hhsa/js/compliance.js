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
	function openInfo(documentId,documentName){
		var arrowDiv = document.getElementsByName("taskArrow");
		for (var i = 0; i <	 arrowDiv.length; i++)
		{
			arrowDiv[i].className = "taskNormal";
		}
		document.getElementById(documentId).className = "taskSelected";
		var url = $("#contextPathSession").val()+"/GetContent.jsp?documentID="+documentId;
		postRequest(url);
		}
	
	/**
	 * This method submit form and pass URL as parameter
	 * 
	 **/
	function submitUrl(finalurl){
		document.forms[0].action="<portlet:actionURL/>"+"&next_action=readUrl"+'&finalurl='+finalurl+'';
		document.forms[0].submit();
	}

	String.prototype.trim = function () {
	    return this.replace(/^\s*/, "").replace(/\s*$/, "");
	}
	
	/**
	 * This method open document 
	 * 
	 **/
	function viewDocumentTask(documentId,docName){
		previewUrl($("#contextPathSession").val()+"/GetContent.jsp?action=displayDocument&documentId="+documentId+"&documentName="+docName,'linkDiv');
	}
	
	/**
	 * This method display document or subsection form in a frame on page 
	 * 
	 **/
	function previewUrl(url,target){
	        clearTimeout(window.ht);
	        window.ht = setTimeout(function(){
	            var div = document.getElementById(target);
	            div.innerHTML = '<iframe style="width:100%; height:550px; display:block;"  src="' + url + '" />';
	        },20);      
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