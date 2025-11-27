// AJAX Method	
$(function() {
		$("#accordion").accordion();
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
var url='';
function onload(url1){
	url = url1;
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
* Check if current Task is manager task and user is manager or not.
* If user is not manager display error message and disable Reassign button
* 
**/
function checkUserTask(isManagerStep)
{
var userName=document.getElementById("userList").value;
if(userName!=null && userName!="")
{
	document.getElementById("reassign").disabled= false;
}
else{
	document.getElementById("reassign").disabled= true;
}
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