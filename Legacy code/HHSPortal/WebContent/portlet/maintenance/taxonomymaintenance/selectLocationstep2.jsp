
<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<title>Insert title here</title>
<style type="text/css">
	.bodycontainer{
		width:897px ;
	}
	.treeview ul{ 
		margin: 0 !important;
		padding: 0 !important;
	}
	.treeview li{ 
		list-style-type: none !important;
		padding-left: 16px !important;
		margin-bottom: 3px !important;
	}

	.treeview .submenu ul li{
		float: none !important;
		line-height: 16px !important;
		padding:0 0 0 16px !important;
		margin:0 0 3px 0 !important;
		width:auto !important;
		/*background: white url(../framework/skins/hhsa/images/iconExpand.gif) no-repeat left 5px;*/
	}
	.treeview li{
		line-height: 16px !important;
	
		margin:0 !important;
		width:auto !important;
	}

	.treeview li.submenu{ /* Style for LI that contains sub lists (other ULs). */
		background: white url(../framework/skins/hhsa/images/iconExpand.gif) no-repeat left 5px;
		cursor: hand !important;
		cursor: pointer !important;
	}
	.container {
    	width:800px;
	}

	.wizardTabs ul li, .wizardTabs ul li.default, .wizardTabs ul li.last, .wizardTabber ul li {
    	background: none;
	}

</style>

<div class="overlaycontent">
<%
	String taxonomyTree1 = (String)renderRequest.getAttribute("lsTaxonomyTypeTree");
%>

	<form id="share1" action="<portlet:actionURL/>" method ="post" >
		<div  >
			<div class=''  id="selectTaxonomyMessagediv"></div>
			<p><strong>From the tree structure below please select the intended Parent of your new taxonomy item. Your new taxonomy item will be added as a Child to item you select here.
			</strong></p>
			
			<DIV class="clear" style="height:250px; overflow-x: hidden;   overflow-y: auto;"><A class=link title="Collapse All" href="javascript:ddtreemenu.flatten('treemenu2', 'contact')">Collapse All</A> | <A class=link title="Expand All" href="javascript:ddtreemenu.flatten('treemenu2', 'expand')">Expand All</A> 
				<UL id=treemenu2 class="treeview">
				<%=taxonomyTree1%>
				</UL>
			</DIV>
			
			<div class="buttonholder">
				<input type="button" id="cancelshare2" value="Cancel" title="Cancel" class="graybtutton"/>
				<input type="button" id="backshare2" value="Back" title="Back" class="graybtutton"/>
				<input type="button" value="Next" title="Next" id="nextshare2"/>
			</div>
				
		 </div>
	</form>
</div>

<script type="text/javascript">

//jquery ready function- executes the script after page loading
$(document).ready(function() { 
	//creates tree structure
	ddtreemenu.createTree("treemenu2", true);
	
	//Below function is called when user clicks on next button in add new taxonomy flow
	$('#nextshare2').click(function() { // bind click event to link
		if(locationValue!=""){
			pageGreyOut();
			shareScreen2(this.form);
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
					
					if(null != data || data != ""){
						$("#tab5").html(data);
					}
					
					$('#sharewiz').removeClass('wizardUlStep1 wizardUlStep2').addClass('wizardUlStep3');
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
	    }else {
		    $("#selectTaxonomyMessagediv").html("! You must select a taxonomy item to continue.");
			$("#selectTaxonomyMessagediv").addClass("failed");
			$("#selectTaxonomyMessagediv").show();
	    }
		return false;
	});
	
	//Below function is called when user clicks on cancel button
	$('#cancelshare2').click(function() {
		pageGreyOut();
	   deleteSaveValues();
	});

	//Below function is called when user clicks on back button
	$('#backshare2').click(function() { // bind click event to link
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
						$("#tab3").html(data);
					}
				 	$("#sharelabel").html("");
					$('#sharewiz').removeClass('wizardUlStep2 wizardUlStep3 wizardUlStep4').addClass('wizardUlStep1');	
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

//Below function bring user to namenewitemstep3 screen once user clicks on next button
function shareScreen2(form){
	form.action = form.action+'&removeNavigator=true&locationValue='+locationValue+'&next_action=namenewitemstep3&removeMenu=true';
}

//Below function brings the user to step1 screen of the add new taxonomy flow
function backtoStep1(form){
	form.action = form.action+'&removeNavigator=true&removeMenu=true&next_action=selectTaxonomy';
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
</script>
