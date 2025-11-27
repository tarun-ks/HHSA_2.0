
<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>


<style type="text/css">
	.bodycontainer{
	width:897px ;
	}
	.noBorder{
	border:none ;
	}
	.container {
    width:800px;
}
</style>

<script type="text/javascript">

	//jquery ready function- executes the script after page loading
	$(document).ready(function() { 
		$("#taxonomyTypeId").html(taxonomyTypeRadio);
		$("#locationId").html(locationPath);
		$("#itemId").html(newItemValue);
	   
		//Below processing is done when user click on next button
		$('#nextshare4').click(function() { // bind click event to link
			pageGreyOut();
			complete(this.form);
	
		});

		//Below processing is done when user click on cancel button 
		$('#cancelshare4').click(function() {
			pageGreyOut();
		    deleteSaveValues();	
		});

		//Below processing is done when user click on back button
		$('#backshare4').click(function() { // bind click event to link
			pageGreyOut();
			backtoStep1(this.form);
			var options = 
   			{	
			   	success: function(responseText, statusText, xhr ) 
				{
					var $response = $(responseText);
					var data = $response.contents().find(".overlaycontent");
				 	
				 	$("#tab6").empty();
				 	$("#tab5").empty();
				 	$("#tab4").empty();
				 	$("#tab3").empty();

					if(null != data || data != ''){
						$("#tab5").html(data);
					}

				 	$("#sharelabel").html("");
					$("#overlayedJSPContent").html(responseText);
					$('#sharewiz').removeClass('wizardUlStep1 wizardUlStep2 wizardUlStep4').addClass('wizardUlStep3');
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

//Below function brings user to choose taxnomy type
function shareScreen2(form){
	form.action = form.action+'&removeNavigator=true&next_action=shareDocumentStep3&removeMenu=true';
}

//Below processing is done when user click on next button
function backtoStep1(form){
	form.action = form.action+'&removeNavigator=true&removeMenu=true&next_action=namenewitemstep3';
}

//Below function blocks any new input from user and turns screen to gray 
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

//Below function sets hidden values and submit form
function complete(form){
    document.getElementById("hdnTaxonomyType").value = taxonomyTypeRadio;
    document.getElementById("hdnLocation").value = locationValue;
    document.getElementById("hdnItemName").value = newItemValue;
    document.getElementById("hdnBranchId").value = branchValue;
	form.action = form.action+'&next_action=confirmselectionstep5&locationValue='+locationValue;
	form.submit();
}
</script>

<div class="overlaycontent">
	<form id="completeForm" action="<portlet:actionURL/>" method ="post" name="completeForm">
	<input type="hidden" id="hdnTaxonomyType" name ="hdnTaxonomyType"/>
	<input type="hidden" id="hdnLocation" name="hdnLocation"/>
	<input type="hidden" id="hdnItemName" name="hdnItemName"/>
	<input type="hidden" id="hdnBranchId" name="hdnBranchId"/>
	<input type="hidden" id="next_action" name="next_action"/>
		<div class="wizardTabs" >
			<p><strong>Please confirm that the following selections before clicking the "Complete" button.</strong></p>
				<div class="formcontainer">
					<div class='row'>
						<span class='label'>Taxonomy Type:</span>
						<span class='formfield'><label  id="taxonomyTypeId"></label></span>
					</div>
					<div class='row'>
						<span class='label'>Location of new taxonomy item:</span>
						<span class='formfield'><label  id="locationId"></label></span>
					</div>
					<div class='row'>
						<span class='label'>Name of new taxonomy item:</span>
						<span class='formfield breakAll' style='width:60%'><label id="itemId"></label></span>
					</div>
					
					<div class="buttonholder">
							<input type="button" id="cancelshare4" value="Cancel"  title="Cancel" class="graybtutton"/>
							<input type="button" id="backshare4" value="Back" title="Back" class="graybtutton"/>
							<input type="button" value="Complete" title="Next" id="nextshare4" />
					</div>
				</div>
		</div>
	</form>
</div>
