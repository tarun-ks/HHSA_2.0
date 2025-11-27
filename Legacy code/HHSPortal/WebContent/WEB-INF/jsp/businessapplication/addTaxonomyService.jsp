<%@page import="com.nyc.hhs.model.TaxonomyServiceBean"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.nyc.hhs.model.TaxonomyTree"%>
<%@page import="com.nyc.hhs.model.TaxonomyParentChild"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.nyc.hhs.model.TaxonomyTree"%>
<%@page import="java.util.ArrayList"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/resources/js/ddaccordion.js"></script>
<style>
	.accContainer{
	width:100% !important
	}
</style>
<script type="text/javascript">

//Initialize for Collapse and Expand Demo:
	ddaccordion.init({
	headerclass: "hdng", //Shared CSS class name of headers group
	contentclass: "accContainer", //Shared CSS class name of contents group
	revealtype: "click", //Reveal content when user clicks or onmouseover the header? Valid value: "click", "clickgo", or "mouseover"
	mouseoverdelay: 200, //if revealtype="mouseover", set delay in milliseconds before header expands onMouseover
	collapseprev: false, //Collapse previous content (so only one open at any time)? true/false 
	defaultexpanded: [], //index of content(s) open by default [index1, index2, etc]. [] denotes no content.
	onemustopen: false, //Specify whether at least one header should be open always (so never all headers closed)
	animatedefault: false, //Should contents open by default be animated into view?
	scrolltoheader: false, //scroll to header each time after it's been expanded by the user?
	persiststate: false, //persist state of opened contents within browser session?
	toggleclass: ["closedlanguage", "openlanguage"], //Two CSS classes to be applied to the header when it's collapsed and expanded, respectively ["class1", "class2"]
	//togglehtml: ["prefix", "<img src='http://i13.tinypic.com/80mxwlz.gif' style='width:13px; height:13px' /> ", "<img src='http://i18.tinypic.com/6tpc4td.gif' style='width:13px; height:13px' /> "], //Additional HTML added to the header when it's collapsed and expanded, respectively  ["position", "html1", "html2"] (see docs)
	animatespeed: "fast", //speed of animation: integer in milliseconds (ie: 200), or keywords "fast", "normal", or "slow"
	oninit:function(expandedindices){ //custom code to run when headers have initalized
		//do nothing
	},
	onopenclose:function(header, index, state, isuseractivated){ //custom code to run whenever a header is opened or closed
		//do nothing
	}
	})

	$(function(){

		// Accordion
		$(".accordion").accordion();
		$( ".accordion" ).accordion({
			collapsible: true,
			autoHeight: false
		});

		// Tabs
		$('#tabs').tabs();
		$('#newTabs').tabs();
		// Dialog
		$('#dialog').dialog({
			autoOpen: false,
			width: 600,
			buttons: {
				"Ok": function() {
					$(this).dialog("close");
				},
				"Cancel": function() {
					$(this).dialog("close");
				}
			}
		});
	});
			
		</script>
	<script type="text/javascript">

	// this code is used to add and remove the services 
	function addRemoveService(id, obj,addedServicesId) {
		if(obj.value == '+ Add'){
			//previous code by which the values were added
			var serviceSelected = document.getElementById('selected_Services');
			var element = document.createElement('li');
			element.appendChild(document.createTextNode(id)); 
			element.id = id;
			serviceSelected.appendChild(element);
			var functionScript1 = "addRemoveService('"+id+"','Remove','"+addedServicesId+"')";
			var functionScript = new Function(functionScript1);
			
			if ( typeof(element.attachEvent) != "undefined" ){    
			  	element.attachEvent("onclick", function(){addRemoveService(id,obj,addedServicesId)}) ;
			}else{ 
			  	element.addEventListener("click", function(){addRemoveService(id,obj,addedServicesId)}, false) ;
			} 
			obj.value = '- Remove';
			
			$(obj).attr("class","button redbtutton");
			
			if($("#addSelectedServices").val()!="" && $("#addSelectedServices").val()!=undefined){
				$("#addSelectedServices").val($("#addSelectedServices").val()+","+addedServicesId);
			}else{
				$("#addSelectedServices").val(addedServicesId);
			}
			}else if(obj.value == '- Remove'){
				var element = document.getElementById(id);
			
			if(element==null || element==undefined){
				setSelectedServices(addedServicesId);
			}else{
				element.parentNode.removeChild(element);
				obj.value = '+ Add';
				$(obj).attr("class","button");
			}
			
			
			var commaSeperatedString = $("#addSelectedServices").val().split(",");
			for(var i = 0; i <commaSeperatedString.length; i++) {
				if(commaSeperatedString[i]!=addedServicesId){
					$("#addSelectedServices").val(commaSeperatedString[i]+",");
				}
				if(commaSeperatedString.length==1){
					$("#addSelectedServices").val("");
				}
			}

			if($("#addSelectedServices").val()!=null && $("#addSelectedServices").val()!=""){
				var finalValue = $("#addSelectedServices").val();
				if(finalValue.charAt(finalValue.length-1)==","){
					$("#addSelectedServices").val(finalValue.substring(0,finalValue.length-1));
				}else if(finalValue.charAt(0)==","){
					$("#addSelectedServices").val(finalValue.substring(1,finalValue.length));
				}
			}
		}
	}
</script>

<script>
function continueService(obj,hideBlock,showBlock,serviceName){
	var searchParam = "hide"+hideBlock;
	var hideBlockId =	$("div[id='"+searchParam+"']");
	var spanBlockId =	$("span[id='"+searchParam+"']");
	
	hideBlockId.each(function(i){
		$(this).attr("style","display:none");
	});

	spanBlockId.each(function(i){
		$(this).attr("style","display:none");
	});
	
	$(obj).attr("style","display:none");
	
	var showBlockId =	$("div[id='"+showBlock+"']");
	showBlockId.each(function(i){
		$(this).attr("style","display:block");
	});	

	var anchorTag =	$("a[id='"+hideBlock+"']")[0];
	$(anchorTag).attr("style","display:block;text-decoration:underline");
	
	var topSearch = "topLevel"+hideBlock;
	
	var topLevelAnchor =	$("a[id='"+topSearch+"']")[0];
	$(topLevelAnchor).text($(topLevelAnchor).text()+" > "+serviceName);
}

function backToServices(obj,keyValue){

	$(obj).attr("style","display:none");	
	
	var searchParam = "hide"+keyValue;
	var hideBlockId =	$("div[id='"+searchParam+"']");
	var spanBlockId =	$("span[id='"+searchParam+"']");
	
	var searchClass = "show"+keyValue;
	hideBlockId.each(function(i){
		$(this).attr("style","display:block");
	});
	
	spanBlockId.each(function(i){
		$(this).attr("style","display:block");
	});
	
	var searchString = "hideSecondLevel"+keyValue;
	var hideSecondLevel =  $("div[id*='"+searchString+"']");
	hideSecondLevel.each(function(i){
		$(this).attr("style","display:none");
	});
	
	var searchContinue = "continueButton"+keyValue;
	var continueButton =  $("input[id*='"+searchContinue+"']");
	continueButton.each(function(i){
		$(this).attr("style","display:block");
	});
	
	var topSearch = "topLevel"+keyValue;
	var topLevelAnchor =	$("a[id='"+topSearch+"']")[0];
	$(topLevelAnchor).text(keyValue);
}
function hideShowDisplayService(obj,elementId){
	$(obj).hide();
	var hideSection =  "myButton"+elementId;
	$("#"+hideSection).val("+ Add");
	$("#"+hideSection).attr("class","button");
	var commaSeperatedString = $("#addSelectedServices").val().split(",");
	var value ="";
	for(var i = 0; i <commaSeperatedString.length; i++) {
		if(commaSeperatedString[i]==elementId){
			if(commaSeperatedString.length==1){
				$("#addSelectedServices").val("");
			}
		}else{
			value = value + ',' + commaSeperatedString[i];
		}
	}
	$("#addSelectedServices").val(value);
	
	if($("#addSelectedServices").val()!=null && $("#addSelectedServices").val()!=""){
		var finalValue = $("#addSelectedServices").val();
		if(finalValue.charAt(finalValue.length-1)==","){
			$("#addSelectedServices").val(finalValue.substring(0,finalValue.length-1));
		}else if(finalValue.charAt(0)==","){
			$("#addSelectedServices").val(finalValue.substring(1,finalValue.length));
		}
	}
}

function setSelectedServices(selectedId){
	var inputList = $("input[id='hiddenSelectedServices'][type='hidden']");
	var selectedServiceId;
	inputList.each(function(i){
		var serviceId = $(this).attr("value");
		if(selectedId==null){
			serviceId = "myButton"+serviceId;
			$("#"+serviceId).val("- Remove");
			$("#"+serviceId).attr("class","button redbtutton");
		}
		else if(selectedId!=null && selectedId==serviceId){
				serviceId = "myButton"+selectedId;
				$("#"+serviceId).val("+ Add");
				$("#"+serviceId).attr("class","button");
				$("#displayService"+selectedId).hide();
			}
			if(i>0){
				selectedServiceId = $("#addSelectedServices").val()+","+$(this).attr("value");
			}else{
				selectedServiceId = $(this).attr("value");
			}
			$("#addSelectedServices").val(selectedServiceId);
		});
}

function setValue(saveServices){
	$("#saveServices").val(saveServices);
}
</script>
<div>
	<form method="post" action="<portlet:actionURL/>" name="addservice">
	<!-- Selected Container start -->
	<%
		List<String> errorList = (List<String>)request.getAttribute("errorToDisplay");
		if(errorList!=null && !errorList.isEmpty()){
		Iterator errorListIterator = errorList.iterator();  
		while(errorListIterator.hasNext()){
			String errorMsg = (String)errorListIterator.next();
	%>
       	<div class="individualError"><%=errorMsg%></div>
	<% 
	 	}
		}
	%>
	<div class="iconQuestion"><a href="javascript:void(0);" title="Need Help?" onclick="pageSpecificHelp('Applications');"></a></div>
	<div class="overlay"></div>
	<div class="alert-box-help">
       <div class="content">
             <div id="newTabs" class='wizardTabs'>
                   <div class="tabularCustomHead">Application Information - Help Documents</div>
             <div id="helpPageDiv"></div>
             </div>
       </div>
       <a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
	</div>
	<div class="alert-box-contact">
		<div class="content">
			<div id="newTabs">
				<div id="contactDiv"></div>
			</div>
		</div>
	</div>
	<h3 style="display:inline">Add Services</h3>
	
	<p>Below is a full list of Services for which your organization may apply.   Services are grouped by category.  To add a Service, click the "Add" button or the "Continue" button to view more Services.</p>
	<p>You must add at least one Service to complete your HHS Accelerator Application. For each Service selected, you will be required to add supporting information. A Service search is also available at the bottom of the page.  Once you have finished your Service selection, click the "Complete Selections" button on the bottom of the page. </p>
	             		
	<div class="selectedContainer">
	
		<h4>Selected Services</h4>
		
		<ul id="selected_Services" >
			<%
			List<TaxonomyServiceBean> saveServicesList = (List<TaxonomyServiceBean>)request.getAttribute("selectedServiceList");
			if(saveServicesList!=null && !saveServicesList.isEmpty()){
			Iterator serviceIterator = saveServicesList.iterator();  
			while(serviceIterator.hasNext()){
				TaxonomyServiceBean serviceObj = (TaxonomyServiceBean)serviceIterator.next();
			%>
			<input type="hidden" id="hiddenSelectedServices" value="<%=serviceObj.getServiceElementId()%>" />
			<li onclick="hideShowDisplayService(this,'<%=serviceObj.getServiceElementId()%>')" id="displayService<%=serviceObj.getServiceElementId()%>">
				<%=serviceObj.getServiceElementName()%></li>
			<% }
			}
			%>
		</ul>
	</div>
	
	<div class="hr"></div>
	
	<!-- Selected Container end -->
	
	<h3 style="display: inline;">Select from full list</h3>
	
	<div class="expandCollapseLink"><a href="#"
		title="Collapse All" onClick="ddaccordion.collapseall('hdng'); return false">Collapse
	all</a> | <a href="#" title="Expand All" onClick="ddaccordion.expandall('hdng'); return false">Expand
	all</a>
	</div>
	
	<div class="accordion">
	<%
		Boolean isSecondLevel = false;
		Boolean isThirdLevel = false;
		Map finalMap = (LinkedHashMap) request.getAttribute("finalMap");
		if(finalMap!=null && !finalMap.isEmpty()){
			Iterator iterator=finalMap.entrySet().iterator();
			 while(iterator.hasNext()){
				Map.Entry mapEntry=(Map.Entry)iterator.next();
				String key = (String)mapEntry.getKey();
				List<TaxonomyParentChild> valueList = (List<TaxonomyParentChild>)mapEntry.getValue();
	%>
	<h3 class="hdng"><a href="#" id="topLevel<%=key%>"><%=key%></a></h3>
		<div class="accContainer">
		    <div class="accDataRowHead">
		    	<span class="col1">Services</span>
		    	<span class="col2">Definition</span>
		    	<span class="col3"><a href="#" title="<<< Back to" style="display:none;text-decoration: underline;" id="<%=key%>" onclick="backToServices(this,'<%=key%>')"> <<< Back to <%=key%></a></span>
		    </div>
			<div class="testing">
			<div class="accDataRow" id="<%=key%>">
			<%
			 	if(valueList!=null && !valueList.isEmpty()){
				 	Iterator valueIterator = valueList.iterator();                
					 while(valueIterator.hasNext()){
					 	isSecondLevel = false;
						TaxonomyParentChild childValue = (TaxonomyParentChild)valueIterator.next();
						List<TaxonomyParentChild> secondLevelList = (List<TaxonomyParentChild>)childValue.getChildList();
			%>
			<div style="display:block; clear:both;">
				<div style="display:block" id="hide<%=key%>">
		    		<span class="col1"><%=childValue.getMsElementName()%></span>
		     		<span class="col2"><%=childValue.getMsElementDescription()%></span>
	     		</div>
		        <%
			    if(secondLevelList!=null){
		      		if(secondLevelList!=null && !secondLevelList.isEmpty()){
			      		isSecondLevel = true;
				 		Iterator secondLevelIterator = secondLevelList.iterator();  
				 		//out.println(secondLevelList.size()+"find the list size");              
						while(secondLevelIterator.hasNext()){
							TaxonomyParentChild secondLevelValue = (TaxonomyParentChild)secondLevelIterator.next();
				  			%>
				  			<div id="hideSecondLevel<%=key%><%=childValue.getMsElamentId()%>" style="display:none" class="show<%=key%>">
				  				<span class="col1"><%=secondLevelValue.getMsElementName()%></span>
			     				<span class="col2"><%=secondLevelValue.getMsElementDescription()%></span>
			     				<span class="col3">
			     					<input id="myButton<%=secondLevelValue.getMsElamentId()%>" type="button" class="button" value="+ Add" title="+ Add" onclick="addRemoveService('<%=secondLevelValue.getMsElementName()%>',this,'<%=secondLevelValue.getMsElamentId()%>'); "/>
			     				</span>
			     			</div>
	
							<% if(isSecondLevel){ %>
			      				<span class="col3">
			      					<input id="continueButton<%=key%>" style="display:block" type="button" class="button" title="Continue" value="Continue" 
			      					onclick="continueService(this,'<%=key%>','hideSecondLevel<%=key%><%=childValue.getMsElamentId()%>','<%=childValue.getMsElementName()%>');"/>
			      				</span>
			      			<%
			      			isSecondLevel = false;
			      			}
			      		}
			      			isSecondLevel = true;
						}
				}
				if(!isSecondLevel){
 			 	%>
	 			 	<span class="col3" id="hide<%=key%>">
	 			 		<input id="myButton<%=childValue.getMsElamentId()%>" type="button" class="button" value="+ Add" title="+ Add" onclick="addRemoveService('<%=childValue.getMsElementName()%>',this,'<%=childValue.getMsElamentId()%>'); "/>
	 			 	</span>
	 			<%
				}
				%>
			</div>
			<br/>		
			<%
		     }	
		     }
	 		%>
	 			     
			</div>
			</div>
		</div>
		<%
		}
		}
		%>
	
	</div>
	
	<!-- Search Container Start -->
	<div class="searchContainer">
		<h5>Search</h5>
		<input type="text" size="44" /><input type="button" value="Clear" title="Clear" class="button" />
		
		<input type="submit" value="Search" title="Search" class="button" />
	</div>
	<!-- Search Container End -->
	<div class="hr"></div>
	
	<div class="buttonholder">
		<input type="button" class="button" title="Cancel" value="Cancel" /><input name="saveService" type="submit" class="button" value="Complete Selection" title="Complete Selection" onclick="setValue('saveServices')"/>
		<input type="hidden" name="selectedService" value="" id="addSelectedServices">
		<input type="hidden" name="submitButtonValue" value="" id="saveServices">
		<input type="hidden" name="subsection" value="" id="addservice">
	</div>
	</form>
</div>
<script type="text/javascript">
	setSelectedServices(null);
</script>
<!-- End accordion -->
