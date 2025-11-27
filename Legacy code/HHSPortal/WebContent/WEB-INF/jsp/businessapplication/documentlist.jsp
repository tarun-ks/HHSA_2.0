<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ page import="com.nyc.hhs.frameworks.grid.*"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<portlet:defineObjects/>
<style type="text/css">
.tabularWrapper{
	height: 300px;
	} 
.ui-widget{
	font-family: Verdana !important;
	font-size: 12px !important;
}
.overlay
{
	background-color: #999;
	opacity: 0.5;
	filter: alpha(opacity = 70);
	position: fixed;
	top: 0px;
	left: 0px;
	z-index: 900;
	display: none;
}
.alert-box, .alert-box-link-to-vault
{
	background: none repeat scroll 0 0 #FFFFFF;
    display: none;
    position: fixed;
    top: 12%;
    left:14%;
    width: 72%;
    z-index: 1001;
}
 #newTabs{
	
}
#newTabs h5{
	background:#E4E4E4;
	color: #5077AA;
    font-size: 13px;
    font-weight: bold;
    padding: 6px 0px 6px 6px;
}
.alert-box .sub-started a, .alert-box-link-to-vault .sub-started a{
	color:#fff;
}
.alert-box .sub-notstarted, .alert-box-link-to-vault .sub-notstarted{
	background-image:none;
	color:#fff;
}
.formcontainer{
	position: relative;
}
</style>

<script type="text/javascript"> 
$(document).ready(function() {   	
		var pageW = $(document).width();
		var pageH = $(document).height();
    	$("select.terms").change(function() { 
			 var str = "";
			 var options = 
    					{	
					    	success: function(responseText, statusText, xhr ) 
							{
							 	$("#tab1").empty();
								$("#tab1").html(responseText);
							},
							error:function (xhr, ajaxOptions, thrownError)
							{                     
								showErrorMessagePopup();
								removePageGreyOut();
							}
					    };
				var options1 = 
    					{	
					    	success: function(responseText, statusText, xhr ) 
							{
							 	$("#linktovault").empty();
								$("#linktovault").html(responseText);
							},
							error:function (xhr, ajaxOptions, thrownError)
							{                     
								showErrorMessagePopup();
								removePageGreyOut();
							}
					    };	
			   $("select.terms option:selected").each(function () {
				   str= $(this).text();				   
				   if(str == 'Upload Document'){
				   	uploadDocument(str);
					$(".alert-box").show();
					 $(".overlay").show();
					 $(".overlay").width(pageW);
					 $(".overlay").height(pageH);
					 $(this.form).ajaxSubmit(options);
						return false;
					}
					if(str == 'Select document from Vault'){
						uploadDocument(str);
						$(".alert-box-link-to-vault").show();
					 	$(".overlay").show();
					 	$(".overlay").width(pageW);
					 	$(".overlay").height(pageH);
					 	$(this.form).ajaxSubmit(options1);
						return false;
					}
				});			
		});		
   		$("a.exit-panel").click(function(){					
		$(".alert-box").hide();
		$(".alert-box-link-to-vault").hide();
		$(".overlay").hide();
		});			
});

function openDocument(documentId, selectElement){
		var value = selectElement.selectedIndex;
				if(value == 1){
					var url = document.myform.action+'&documentId='+documentId+'&next_action=displayDocument';
					window.open(url);
				}
}

function displayDocument(documentId){
	var url = document.myform.action+'&documentId='+documentId+'&next_action=displayDocument';
		window.open(url);
}

function nextAction(nextaction){
	document.myform.action=document.myform.action+'&next_action='+nextaction;
	document.myform.submit();

}
function uploadDocument(str)
{
	if(str == "Upload Document"){
		document.myform.action = document.myform.action+'&removeNavigator=true&removeMenu=true&next_action=documentupload';
	}
	if(str == "Select document from Vault"){
		// changes in R5 Starts
		document.myform.action = document.myform.action+'&removeNavigator=true&removeMenu=true&next_action=selectDocFromVault&selectVault=true&selectAll=true';
		// changes in R5 ends
	}
}
</script>

<div class='tabularWrapper'>

<h3>Basic Documents</h3>
<div class='hr'></div>
Please upload most recent versions of the following required documents.
<form name="myform" action="<portlet:actionURL/>" method ="post" >
	<st:table objectName="taskItemList"  cssClass="heading"
		alternateCss1="evenRows" alternateCss2="oddRows">
		<st:property headingName="Document Name" columnName="docName" align="center"
			size="30%">
			<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.DocumentNameExtension" />
		</st:property>
		<st:property headingName="Document Type" columnName="docType"
			align="left" size="20%"  />
		<st:property headingName="Status" columnName="status"
			align="right" size="10%" />
		<st:property headingName="Modified" columnName="date"
			align="right" size="10%" />
		<st:property headingName="Last Modified By" columnName="lastModifiedBy"
			align="right" size="10%" />
		<st:property headingName="Actions" columnName="actions" 
			align="right" size="20%" >
			<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.DocumentActionApplicationExtension" />
		</st:property>
	</st:table>

	<div class="buttonholder">
		<input type="button" class="button" title="Back" value="Back" onclick="javascript: nextAction('previous');"/>
		<input type="button" class="button" title="Next" value="Next" onclick="javascript: nextAction('save_next');" />
	</div>
</form>
</div>
<div class="overlay"></div>
<div class="alert-box">
	<div class="content">
  		<div id="newTabs">
			<h5>Upload Document</h5>
			<ul>
				<li><a href="#tab1" title="Step 1: File Selection">Step 1: File Selection</a></li>
				<li><a href="#tab2" title="Step 2: Document Information">Step 2: Document Information</a></li>
			</ul>
            <div id="tab1"></div>
              <div id="tab2"></div>
		</div>
  	</div>
    <a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
</div>
<div class="alert-box-link-to-vault">
	<div class="content">
  		<div id="newTabs">
			<div id="newTabs">
			<!-- changes in R5 Starts --> 
				<h5>Select Existing Document from Document Vault</h5>
			<!-- changes in R5 ends --> 
		        <div id="linktovault"></div>
			</div>
		</div>
  	</div>
    <a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
</div>


