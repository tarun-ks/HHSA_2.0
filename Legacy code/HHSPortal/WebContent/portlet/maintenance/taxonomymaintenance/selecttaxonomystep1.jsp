
<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@page import="com.nyc.hhs.constants.*"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<portlet:defineObjects/>

<style type="text/css">
	.paginationWrapper{
		display: none;
	}
	.container {
	    width:800px;
	    min-height: inherit !important;
	}
	.portlet3Col{
		margin-bottom: 1%;
	}
</style>
<script type="text/javascript">

//jquery ready function- executes the script after page loading
$(document).ready(function() {
	var pageW = $(document).width();
	var pageH = $(document).height();
	if(taxonomyTypeRadio!=""){
		selectTaxonomyType();
	}
	
	//Below function is called when user clicks on next button in add new taxonomy flow
	$('#nextshare1').click(function() { // bind click event to link
		var hasChecked =  saveTaxonomyTypevalue();
		if(hasChecked){
			shareScreen2(this.form);
	    	pageGreyOut();
    	var options = 
	   	{
	    	success: function(responseText, statusText, xhr ) 
			{
				var $response=$(responseText);
	            var data = $response.contents().find(".overlaycontent");
	            			
				$("#tab3").empty();
				$("#tab4").empty();
				$("#tab5").empty();
				$("#tab6").empty();
				
				if(null != data || data != ""){
					$("#tab4").html(data);
				}
				$("#sharelabel").html("");
				$("#overlayedJSPContent").html(responseText);
				$('#sharewiz').removeClass('wizardUlStep1').addClass('wizardUlStep2');
				$('#treemenu2').removeClass('wizardTabs wizardTabber');	
				$('step2confirmDoc').css("background-color", "#333333");
				ddtreemenu.createTree("treemenu2", true);
				ddtreemenu.openFirstLevel('treemenu2', 'expand');
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
	    }else {
	    	$("#selectTaxonomyMessagediv").html(" ! This field is required.");
			$("#selectTaxonomyMessagediv").addClass("failed");
			$("#selectTaxonomyMessagediv").show();
	    }
		return false;
	});
	
	//Below function is called when user clicks on cancel button	
	$('#cancelshare1').click(function() {
		deleteSaveValues();
	});
});

//Below function bring user to namenewitemstep3 screen once user clicks on next button
function shareScreen2(form){
	form.action = form.action+'&removeNavigator=true&taxonomyTypeRadio='+taxonomyTypeRadio+'&next_action=selectLocationstep2&removeMenu=true';
}

//Below function brings user to document vault step 1 screen
function backtoDocVault(){
	document.forms[0].action = document.forms[0].action+'&removeNavigator=true&next_action=backtoDocVaultFromStep1';
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

//Below function save taxonomytype
function saveTaxonomyTypevalue(){
	var chks = document.getElementsByName('taxonomyType');
    var hasChecked =  false;
	for (var i = 0; i <	 chks.length; i++){
		if (chks[i].checked){
	       taxonomyTypeRadio = chks[i].value;
	       hasChecked = true;
	    }
    }
    return hasChecked;
}
    
//Below function selects taxonomy type    
function selectTaxonomyType(){
	var chks = document.getElementsByName('taxonomyType');
	for (var i = 0; i <	 chks.length; i++){
		if (chks[i].value==taxonomyTypeRadio){
	    	chks[i].checked=true;
	      	break;
	    }
    }
}
</script>

<div class="overlaycontent">
	<form id="share2" action="<portlet:actionURL/>" method ="post" >
		<div class="wizardTabs">
			<div class=''  id="selectTaxonomyMessagediv"></div>
			<p><strong>Please select the type of taxonomy item that you would like to add:</strong></p>
				
					
			<div class="formcontainer">
				<div class="row"> 
					<span class="portlet3Col">
	               	 	<input name="taxonomyType" id="serviceArea" type="radio" value="Service Area"  />
	                	Service Area
	                </span>
	                <span class="portlet3Col">
		                 <input name="taxonomyType" id= "geography" type="radio" value="Geography"  />
		                Geography
	                </span>
	                </div>
	                <div class="row"> 
	                	<span class="portlet3Col">
			                <input name="taxonomyType" id="function" type="radio" value="Function"  />
			                Function 
	                	</span> 
	                	<span class="portlet3Col">
		                	<input name="taxonomyType" id= "languages" type="radio" value="Languages"  />
		                	Languages 
	                	</span> 
	                </div>
	                <div class="row"> 
	                	<span class="portlet3Col">
			                <input name="taxonomyType" id="serviceSetting" type="radio" value="Service Setting"  />
			                Service Setting
			             </span>
			             <span class="portlet3Col">   
			                <input name="taxonomyType" id= "populations" type="radio" value="Populations"  />
			                Populations </span> 
		            </div>
			</div>
			<div class="buttonholder">
					<input type="button" id="cancelshare1" value="Cancel"  title="Cancel" class="graybtutton"/>
					<input class='button' type="button" value="Next" title="Next" id="nextshare1"/>
			</div>
		</div>
	</form>
</div>
