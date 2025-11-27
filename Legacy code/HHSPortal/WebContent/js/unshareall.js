/**
 * ====================================================================================
 * This file contains the functionality of sharing screen.
 * 
 * It contains the methods that handle the event when entity sharing access is removed.
 * ====================================================================================
 */

/**
 * on load function to perform various checks on loading of jsp
 **/
function onReady(){
		$(".alert-box-unshareall").find('#removeaccess').click(function() { 
			
			removeaccess(this.form);
			// Start Updated in R5
			var options = {
					success : function(responseText, statusText, xhr) {
						var response = new String(responseText);
						var responses = response.split("|");
						if(!(responses[1] == "Error" || responses[1] == "Exception"))
						{
							$(".overlay").closeOverlay();
							//submitSuccess(responses[3], responses[4]);
							removePageGreyOut();
					        var data = $(responseText);
					        if(data.find(".tabularWrapper").size()>0)
					   		{
					        	$("#myform .tabularWrapper").replaceWith(data.find(".tabularWrapper"));
					 	        var _message = data.find("#message").val();
					 	        var _messageType = data.find("#messageType").val();
					 	       
					 	       
					 	        $(".messagediv").html(_message+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
					 	       $(".messagediv").removeClass("failed passed");
					 	        $(".messagediv").addClass(_messageType);
					 			$(".messagediv").show();
					 			 $(".overlay").closeOverlay();
					 	        removePageGreyOut();
					   		}    else
					        {$(".overlay").closeOverlay();
				 	        removePageGreyOut();
					        	
					        }
						}
					},
					// End Updated in R5
					error : function(xhr, ajaxOptions, thrownError) {
						showErrorMessagePopup();
						removePageGreyOut();
					}
				};
	    $(this.form).ajaxSubmit(options);
	    pageGreyOut();
		return false;
		});
		
		$(".alert-box-unshareall").find('#cancelunshare').click(function() {
			$(".overlay").closeOverlay();
			return false;
		});
}

/**
 *  This will execute when Remove Access button is clicked
 **/
function removeaccess(form){
		// Start Updated in R5
	var folderId = $js("#leftTree").jstree("get_selected");
	$('#parentFolderIdUnshare').val(folderId);
	    document.unshareform.action = $("#finalUnSharing").val()+'&unshareBy=all';
		// End Updated in R5
}
/**
 * This will execute when Remove Access button is clicked
 **/
function submitSuccess(str1, str2){
		document.getElementById("message").value= str1;
		document.getElementById("messageType").value = str2;
		document.getElementById("nextUnshareAllAction").value="showdocumentlist";
		document.unshareform.action = document.unshareform.action+"&removeNavigator=true";
		document.unshareform.submit();
}