
<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<portlet:defineObjects/>

<style type="text/css">
	.bodycontainer{
	width:897px ;
	}
	.container {
    width:800px;
}
</style>
<% 
String lsDuplicate = "";
if(null !=renderRequest.getParameter("duplicateElementName")){
	lsDuplicate = (String)renderRequest.getParameter("duplicateElementName");
}

%>

<div class="overlaycontent">

	<form id="share1" action="<portlet:actionURL/>" method ="post" >
		<div class="wizardTabs">
		
	
		<div class=''  id="itemNameMessagediv"></div>
				<p><strong>Please enter the name/title of the new taxonomy item and confirm.</strong>
				<div><label class='required'>*</label>Indicates Required Fields</div>
				</p>
				
				<div class="formcontainer">
					<div class="row">
						<span class='label'><label class='required'>*</label>Name:</span>
						<span class='formfield'><input type="text" name="newItem" maxlength="200" style="width:350px;" id="newItem" /></span>
					</div>
					<br>
					<br>
					<br>
					<div class="buttonholder">
							<input type="button" id="cancelshare3" value="Cancel" title="Cancel" class="graybtutton"/>
							<input type="button" id="backshare3" value="Back" title="Back" class="graybtutton"/>
							<input type="button" value="Next" title="Next" id="nextshare3"/>
					</div>
			 </div>
		 </div>
	</form>
</div>
<script type="text/javascript">
suggestionVal ="";
isValid = false;

//jquery ready function- executes the script after page loading
$(document).ready(function() { 
	if(newItemValue!=""){
		document.getElementById("newItem").value=newItemValue;
	}
	var duplicate = '<%=lsDuplicate%>';
	if(""!=duplicate){
		$("#itemNameMessagediv").html(duplicate);
	    $("#itemNameMessagediv").addClass("failed");
	    $("#itemNameMessagediv").show();
	    $('#sharewiz').removeClass('wizardUlStep1 wizardUlStep2 wizardUlStep3 wizardUlStep4').addClass('wizardUlStep3');
	}

	//Below function is called when user clicks on next button
	$('#nextshare3').click(function() { // bind click event to link
		if(trim(document.getElementById("newItem").value)==""){
	   	    $("#itemNameMessagediv").html(" ! This field is required.");
           	$("#itemNameMessagediv").addClass("failed");
        	$("#itemNameMessagediv").show();
        	return false;
        }
		var lsReturnStatus = validateText('newItem');
	    if(!lsReturnStatus){
	    	var lsErrorMsg = "! Only ! @ # $ % ^ & * () - _ + = | \ { } [ ] ; : \" \' < > , . ? / ` ~ \xA7 and \n spaces are allowed as a special character.";
			$("#itemNameMessagediv").html(lsErrorMsg);
		    $("#itemNameMessagediv").addClass("failed");
		    $("#itemNameMessagediv").show();
		    return false;
	    }   
	    newItemValue =  $('#newItem').val();
	 	pageGreyOut();
		shareScreen2(this.form);
	   	var options = 
	    	{
		    	success: function(responseText, statusText, xhr ) 
				{
					//$tabs.tabs('select', 2); // switch to third tab
					var $response = $(responseText);
					var data = $response.contents().find(".overlaycontent");
					
					$("#tab3").empty();
				    $("#tab4").empty();
				    $("#tab5").empty();
				    $("#tab6").empty();
		
					if(null != data || data !=""){
					$("#tab6").html(data);
					}
	
					$('#sharewiz').removeClass('wizardUlStep1 wizardUlStep2 wizardUlStep3').addClass('wizardUlStep4');
					$("#sharelabel").html("");
					$("#overlayedJSPContent").html(responseText);
					
					//$.unblockUI();
					removePageGreyOut();
				},
				error:function (xhr, ajaxOptions, thrownError)
				{                     
					showErrorMessagePopup();
					removePageGreyOut();
				}
	        };

	    $(this.form).ajaxSubmit(options);
		return false;
	});
	
	//Below function is called when user clicks on cancel button
	$('#cancelshare3').click(function() {
	   pageGreyOut();
	   deleteSaveValues();
	});
	
	//Below function is called when user clicks on back button
	$('#backshare3').click(function() { // bind click event to link
		newItemValue =  $('#newItem').val();
		pageGreyOut();
		backtoStep1(this.form);
		var options = 
    			{	
				   	success: function(responseText, statusText, xhr ) 
					{
						var $response = $(responseText);
						var data = $response.contents().find(".overlaycontent");

					 	$("#tab3").empty();
						$("#tab4").empty();
					 	$("#tab5").empty();
					 	$("#tab6").empty();
		
						if(null != data || data != ''){
							$("#tab4").html(data);
						}
					 	$("#sharelabel").html("");
						$('#sharewiz').removeClass('wizardUlStep1 wizardUlStep3 wizardUlStep4').addClass('wizardUlStep2');
						ddtreemenu.createTree("treemenu2", true);
						ddtreemenu.openFirstLevel('treemenu2', 'expand');
	  					$("#overlayedJSPContent").html(responseText);
						//$.unblockUI();
						removePageGreyOut();
					},
					error:function (xhr, ajaxOptions, thrownError)
					{                     
						showErrorMessagePopup();
						removePageGreyOut();
					}
			   };
				$(this.form).ajaxSubmit(options);
				return false;
		});
});

//Below function is called when user click on add new taxonomy
function shareScreen2(form){
		form.action = form.action+'&removeNavigator=true&taxonomyTypeRadio='+taxonomyTypeRadio+'&locationValue='+locationValue+'&next_action=confirmselectionstep4&removeMenu=true'+'&newItem='+newItemValue;
}

//Below processing is done when user click on back button
function backtoStep1(form){
	form.action = form.action+'&removeNavigator=true&removeMenu=true&next_action=selectLocationstep2&taxonomyTypeRadio='+taxonomyTypeRadio;
}

//Below function blocks any new input from user and turns screen to grey
/*function pageGreyOut(){
	$.blockUI({
	    message: "<img src='../framework/skins/hhsa/images/loadingBlue.gif' />",
	    overlayCSS: { opacity : 0.8}
	});
}*/

//Below function removes page grey out and allows user to make actions
/*function removePageGreyOut(){
	$.unblockUI();
}*/

String.prototype.beginsWith = function (string) {
    return(this.indexOf(string) == 0);
};

//Below function removes spaces from left and right of string 
function trim(stringToTrim) {
	return stringToTrim.replace(/^\s+|\s+$/g,"");
}

function validateText(id) {
	convertSpecialCharactersHTMLGlobal(id,true);
	var lsDescription = document.getElementById(id).value;
	lsResult = "^[0-9a-zA-Z !@\\\#\\\$%^\\\&*()-_+=|\\\\{}\\\[\\\];:\\\"\\\'<>,.?\\\/`~\xA7]+$";
	var re = new RegExp(lsResult);
	if(null!=lsDescription && lsDescription!=""){
		return re.test(lsDescription);
	}else{
		return true;
	}
}

</script>
