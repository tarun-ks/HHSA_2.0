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
	String.prototype.trim = function () {
	    return this.replace(/^\s*/, "").replace(/\s*$/, "");
	}
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
	 * This method display form in a frame on page 
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
	 * udpated in R5
	 */
	function setMaxLength(obj,maxlimit){
		if(($(obj).val().length + $(obj).val().split("\n").length-1)  > maxlimit){
			$(obj).val($(obj).val().substring(0, maxlimit - $(obj).val().split("\n").length + 1));
			return false;
		}
	}