<%@page import="java.util.ArrayList"%>
<%@page import="com.nyc.hhs.model.Document"%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="com.nyc.hhs.frameworks.grid.*,com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<portlet:defineObjects/>
<style type="text/css">
.alert-box{
        position:fixed !important;
        top:25% !important;
}
.alert-box-sharedoc{
	position:fixed !important;
}
</style>
<link rel="stylesheet" href="../css/documentlist.css" type="text/css"></link>
<script type="text/javascript" src="../js/accdocumentlist.js"></script>
<script type="text/javascript" src="../resources/js/calendar.js"></script>
<script type="text/javascript">
var deleteDocumentId = "";
var originalFormAction; 
// on load function to perform various checks on loading of jsp
$(document).ready(function() { 
	originalFormAction = document.acceleratorForm.action;
	 	if("null" != '<%=request.getAttribute("message")%>' && '<%=request.getAttribute("messageType")%>' !="confirmation"){
			$(".messagediv").html('<%=request.getAttribute("message")%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
			$(".messagediv").addClass('<%=request.getAttribute("messageType")%>');
			$(".messagediv").show();
			<%request.removeAttribute("message");%>
		}
	 	if('<%=request.getAttribute("filterStatus")%>' != "filtered" && "notfiltered" == '<%= request.getAttribute("filterStatus")%>')
	 		{
            clearFilter(this.form,'<%=((Document)request.getAttribute("document")).getDocSharedStatus()%>','<%=request.getAttribute("providerSet")%>','<%=request.getAttribute("agencySet")%>');
   		 }
		if ('<%=((Document)request.getAttribute("document")).getDocSharedStatus()%>' == "unshared") {
			$('#providerDiv').hide();
			$('#agencyDiv').hide();

		}
		if($('#filtersamplecategory').val() == ""){
			$("#filtersampletype").attr("disabled", "disabled");
		}
		else{
			$("#filtersampletype").removeAttr("disabled");
		}
	 	if('<%=request.getAttribute("category")%>' == "Sample Document"){
	 		$('#filterSampleCatDiv').show();
			$('#filterSampleTypeDiv').show();
	 	}
	 	else if('<%=request.getAttribute("category")%>' == "Solicitation"){
			$('#filterTypeDiv').show();
			$('#filterSampleCatDiv').hide();
			$('#filterSampleTypeDiv').hide();
	 	}
	 	else{
	 		$('#filterSampleCatDiv').hide();
			$('#filterSampleTypeDiv').hide();
	 	}
		if('<%=request.getAttribute("messageType")%>' == "confirmation")
		{
			$(".overlay").launchOverlay($(".alert-box-delete"), $(".exit-panel"), "350px", null, "onReady");
		}
		// This will execute when delete document option is selected from drop down
		$('#deleteDoc').click(function() {
			pageGreyOut();
			$("#next_action").val('deleteDocument');
			document.acceleratorForm.action = originalFormAction+'&documentId='+deleteDocumentId+'&removeNavigator=true';
			document.acceleratorForm.submit();
		});
		// This will execute when any option is selected for filter Document Category
		$('#filtercategory').change(function() {
			 getContent();
		});
		// This will execute when upload button is clicked
		$('#upload').click(function() {
    		uploadDocument(originalFormAction);
    		pageGreyOut();
			 var options = 
    			{	
				   	success: function(responseText, statusText, xhr ) 
					{
						var $response=$(responseText);
                            var data = $response.contents().find(".overlaycontent");
                            	$("#tab1").empty();
					 			$("#tab2").empty();
                            if(data != null || data != ''){
                                  $("#tab1").html(data.detach());
							}
						$("#overlayedJSPContent").html($response);
						$(".overlay").launchOverlay($(".alert-box"), $(".exit-panel.upload-exit"), "890px", null, "onReady");
						$('ul').removeClass('ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all ui-state-hover ui-tabs-nav');
						$('li').removeClass('ui-state-default ui-corner-top ui-state-hover');
						removePageGreyOut();
					},
					error:function (xhr, ajaxOptions, thrownError)
					{  
						showErrorMessagePopup();
						removePageGreyOut();
					}
				  };
				$(document.acceleratorForm).ajaxSubmit(options);
				return false;
					
		});	
		// This will execute when Upload New Version option is selected from action drop down
		$("select.terms").change(function() { 
			 var str = "";
			 var options = 
						{	
					    	success: function(responseText, statusText, xhr ) 
							{
								var $response=$(responseText);
		                        var data = $response.contents().find(".overlaycontent");
		                        	$("#tab1").empty();
						 			$("#tab2").empty();
		                        if(data != null || data != ''){
		                              $("#tab1").html(data.detach());
								}
								var overlayLaunchedTemp = overlayLaunched;
								var alertboxLaunchedTemp = alertboxLaunched;
								$("#overlayedJSPContent").html($response);
								overlayLaunched = overlayLaunchedTemp;
								alertboxLaunched = alertboxLaunchedTemp;
								$(".overlay").launchOverlay($(".alert-box"), $(".exit-panel"), "850px", null, "onReady");
								$('ul').removeClass('ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all ui-state-hover ui-tabs-nav');
								$('li').removeClass('ui-state-default ui-corner-top ui-state-hover');
								removePageGreyOut();
							},
							error:function (xhr, ajaxOptions, thrownError)
							{  
								showErrorMessagePopup();
								removePageGreyOut(); 
							}
					    };	
			   $(this).find("option:selected").each(function () {
				   str= $(this).text();
				   if(str == 'Upload New Version'){
				   	 $(this.form).ajaxSubmit(options);
				   	 $(this).parent().prop("selectedIndex",0);
					 pageGreyOut();
						return false;
					}
				});
		});	
		
// This will execute when any option is selected for filter Document Category
$('#filtersamplecategory').change(function() {
	if(samplefilterCategoryForCity()){
	getFilterSampleTypeForCity();
	}	
});	

//Start of R4 Document Vault changes:Jquery functions added for share/unsharing functionality for Accelerotor Document
//This will execute when Share button is clicked
$('#shareDoc').click(function() {
	pageGreyOut();
    shareDocument(originalFormAction);
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
                        if(data != null && data != ''){
                        	$("#tab3").html(data.detach());
						}
						$("#sharelabel").html("- Step 1");
						$("#overlayedJSPContent").html($response);
						$('#sharewiz').removeClass('wizardUlStep1')
						.removeClass('wizardUlStep2').removeClass('wizardUlStep3')
						.removeClass('wizardUlStep4').addClass('wizardUlStep1');
						$(".overlay").launchOverlay($(".alert-box-sharedoc"), $(".exit-panel"), "850px", null, "onReady");
						removePageGreyOut();
					},
					error:function (xhr, ajaxOptions, thrownError)
					{                     
						showErrorMessagePopup();
						removePageGreyOut();
					}
				  };
				$(document.acceleratorForm).ajaxSubmit(options);
				return false;
			});	
			
		// This will execute when unshareAll button is clicked
		$('#unshareAll').click(function() {
			pageGreyOut();
    		unshareAllDocument(originalFormAction);
			var options = 
    			{	
				   	success: function(responseText, statusText, xhr ) 
					{
						var $response=$(responseText);
                        var data = $response.contents().find(".overlaycontent");
                        	$("#unshareall").empty();
                        if(data != null || data != ''){
                        	$("#unshareall").html(data.detach());
						}
						$("#overlayedJSPContent").html($response);
						$(".overlay").launchOverlay($(".alert-box-unshareall"), $(".exit-panel"), "850px",null, "onReady");
						removePageGreyOut();
					},
					error:function (xhr, ajaxOptions, thrownError)
					{                     
						showErrorMessagePopup();
						removePageGreyOut();
					}
				  };
				$(document.acceleratorForm).ajaxSubmit(options);
				return false;		
		});	
		
		// This will execite when unshareByProvider button is clicked
		$('#unshareByProvider').click(function() {
			pageGreyOut();
    		unshareDocumentByProvider(originalFormAction);
    		$("#next_action").val("unsharedocumentbyprovider");
			var options = 
    			{	
				   	success: function(responseText, statusText, xhr ) 
					{
						var $response=$(responseText);
                        var data = $response.contents().find(".overlaycontent");
                        $("#unshareprovider").empty();
                        if(data != null || data != ''){
                        	$("#unshareprovider").html(data.detach());
						}
						$("#overlayedJSPContent").html($response);
						$(".overlay").launchOverlay($(".alert-box-unsharebyprovider"), $(".exit-panel"), "850px", "500px","onReady");
						removePageGreyOut();
					},
					error:function (xhr, ajaxOptions, thrownError)
					{                     
						showErrorMessagePopup();
						removePageGreyOut();
					}
				  };
				$(document.acceleratorForm).ajaxSubmit(options);
				return false;		
		});	
		
		// This will execute when Shared link is clicked
		$('.linkclass').click(function() {
			pageGreyOut();
			var linkId = $(this).attr('id'); 
			var name = $(this).attr('name');
    		displaySharedDocuments(linkId, name, originalFormAction);
    		var options = 
    			{	
				   	success: function(responseText, statusText, xhr ) 
					{
						var $response=$(responseText);
                        var data = $response.contents().find(".overlaycontent");
                        $("#displayshared").empty();
                        if(data != null || data != ''){
                        	$("#displayshared").html(data.detach());
						}
						$("#removeprovlabel").html(name);
						$("#overlayedJSPContent").html($response);
						$(".overlay").launchOverlay($(".alert-box-removeselectedprovs"), $(".exit-panel"), "890px", "400px", "onReady");
						removePageGreyOut();
					},
					error:function (xhr, ajaxOptions, thrownError)
					{                     
						showErrorMessagePopup();
						removePageGreyOut();
					}
				  };
				$(document.acceleratorForm).ajaxSubmit(options);
				return false;		
		});
		
		// This will execute when any value is selected for Shared - Unshared radio button in Filter Documents tab
		$("input[type='radio']").click(function() {
			if($(this).val()=="unshared")
			{
				$("#agencyDiv").hide();
				$("#providerDiv").hide();
				removeProviderAndAgency();
			}    
			else
			{
				$("#agencyDiv").show();
				$("#providerDiv").show();
				updateProviderAndAgency('<%=request.getAttribute("providerSet")%>','<%=request.getAttribute("agencySet")%>');    
			}
		});
		
		checkShareStatus('<%=request.getAttribute("sharedFlag")%>','<%=request.getAttribute("providerSet")%>','<%=request.getAttribute("agencySet")%>');
		updateProviderAndAgency("<%=request.getAttribute("providerSet")%>","<%=request.getAttribute("agencySet")%>","<%=((Document)request.getAttribute("document")).getFilterProviderId()%>",
		"<%=((Document)request.getAttribute("document")).getFilterNYCAgency()%>");
		
		// For Defect - 5998
		if($('#rdoShared').is(":checked") || $('#rdoUnshared').is(":checked")){
	    	 $('#filterbutton').val('Filtered');
	    	 $('#filterbutton').attr('title','Filtered');
	     }
});
// End of R4 Document Vault changes 

// This will execute when any option is selected for filter Document Category
function samplefilterCategoryForCity() {
	var e = document.getElementById('filtersamplecategory');
	var category = e.options[e.selectedIndex].value;
	if (category == null || category == "") {
		document.getElementById("filtersampletype").value = "";
		document.getElementById("filtersampletype").disabled = true;
		return false;
	} else {
		document.getElementById("filtersampletype").disabled = false;
		return true;
	}

}
//This method will get the filter document category and 
//will return the list of document types through servlet call
function getContent() {
	var selectedInput = $("#filtercategory").val();
	selectedInput = selectedInput.replace(/&/g, "$");
	if("Sample Document" == selectedInput){
		getFilterSampleCategory();
		$('#filterSampleCatDiv').show();
		$('#filterSampleTypeDiv').show();
		$('#filterTypeDiv').hide();
		$("#filtersampletype").removeAttr("disabled");
		removePageGreyOut();
		return false;
	}if("Solicitation" == selectedInput){
		getDocumentTypeList(selectedInput, "city_org");
		$('#filterTypeDiv').show();
		$('#filterSampleCatDiv').hide();
		$('#filterSampleTypeDiv').hide();
		document.getElementById("filtertype").disabled = false;
		document.getElementById("filtersamplecategory").value = "";
		document.getElementById("filtersampletype").value = "";
		removePageGreyOut();
		return false;
	}
	else{
		document.getElementById("filtersamplecategory").value = "";
		document.getElementById("filtersampletype").value = "";
		$('#filterSampleCatDiv').hide();
		$('#filterSampleTypeDiv').hide();
		$('#filterTypeDiv').hide();
	}
}
// This method will get the filter document category and 
//will return the list of document types through servlet call
function getFilterSampleTypeForCity() {
	pageGreyOut();
	var selectedInput = document.getElementById("filtersamplecategory").value;
	var url = $("#contextPathSession").val()+"/GetContent.jsp?selectedInput=" + selectedInput+"&organizationId=<%=ApplicationConstants.PROVIDER_ORG%>";
	postRequestFetchDocType(url);
	removePageGreyOut();
}
</script>

<!-- Body Wrapper Start -->
	<form id="acceleratorForm" action="<portlet:actionURL/>" method ="post" name="acceleratorForm" >
	<input type="hidden" id="agencySet" value="${agencySet}"/>
	<input type="hidden" id="providerSet" value="${providerSet}"/>
	<input type="hidden" id="filterStatus" value="${filterStatus}"/>
	<input type="hidden" name="next_action" value="" id="next_action">
		<jsp:useBean id="document" class="com.nyc.hhs.model.Document" scope="request"></jsp:useBean>
		<!-- Body Container Starts -->
			<h2 id="pageHeader">Document Vault</h2><!--
			<div id="helpIcon" class="iconQuestion"><a href="javascript:void(0);" title="Need Help?" onclick="pageSpecificHelp('Document Vault');"></a></div>
			--><span id="descriptionMsg">Manage documents loaded by your organization.</span>
			<br/>
			<div class="messagediv" id="messagediv"></div>
			
			<!--Start of R4 Document Vault changes-->
			<% Document loDocBean = (Document)request.getAttribute("document"); %>
			<!--End of R4 Document Vault changes -->
			
			<%if(((null != request.getAttribute("isOrganizationSharesDoc") && ((String) request.getAttribute("isOrganizationSharesDoc")).equalsIgnoreCase("true"))) || CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S031_PAGE, request.getSession())){%>
				<!-- Container Starts -->
				<!--Filter and Reassign section starts -->
				<div class="taskButtons">
				<%if(request.getAttribute("filterStatus") == "filtered"){%>
					 <input type="button" value="Filtered" title="Filtered" id= "filterbutton" title="Filter Document" class="filterDocument" onclick="setVisibility('documentValuePop', 'inline');"/>	
				<% } else{%>
					<input type="button" value="Filter Documents" title="Filter Documents" id= "filterbutton" title="Filter Document" class="filterDocument" onclick="setVisibility('documentValuePop', 'inline');"/>
				<%}%>
				
				<%if(!(null != request.getAttribute("isOrganizationSharesDoc") && ((String) request.getAttribute("isOrganizationSharesDoc")).equalsIgnoreCase("true"))){%>
					<input type="button" value="Upload" title="Upload" id="upload" class="upload" />
				<%}%>
				<!--Start of R4 Document Vault changes: Adding of share/unshare buton -->
				 <input type="button" value="Share" id="shareDoc" title="Share" class="share disable" disabled="disabled"/>	
				 <input type="button" value="Un-Share All" title="Un-Share All" id="unshareAll" class="unShare disable" disabled="disabled"/>
				 <input type="button" value="Un-Share by Organization" title="Un-Share by Organization" id="unshareByProvider" class="unShare disable"/>
				 <!--End of R4 Document Vault changes -->
				 
				<!-- Popup for Filter Task Starts -->
				<div id="documentValuePop" class='formcontainer providerFilter'>
					
					<div class='close'><a href="javascript:setVisibility('documentValuePop', 'none');" title="Close">X</a></div>
					<div class='row'>
						<span class='label'>Document Category:</span>
						<span class='formfield'>
							<select id = "filtercategory" name="filtercategory" class="terms">
								<c:forEach var="category" items="${document.categoryList}" >
									<%String selected = "";%>
									<c:if test="${category==document.filterDocCategory}">
										<%selected = "selected";%>										
									</c:if>
									<option value="<c:out value="${category}"/>" <%=selected%> title="${category}"><c:out value="${category}"/></option>
								</c:forEach>
							</select>
						</span>
					</div>
					<div class='row' id="filterTypeDiv" style="display: none;">
						<span class='label'>Document Type:</span>
						<span class='formfield'>
							<select id = "filtertype" name="filtertype" class="input">
								<c:forEach var="type" items="${document.typeList}" >
									<%String selected = "";%>
									<c:if test="${type==document.filterDocType}">
										<%selected = "selected";%>										
									</c:if>
									<option value="<c:out value="${type}"/>" <%=selected%> title="${type}"><c:out value="${type}"/></option>
								</c:forEach>
							</select>
						</span>
					</div>
					<div class="row" id="filterSampleCatDiv">
						<span class="label wrapText">Sample Document Category:</span> 
						<span class="formfield">
							<select id="filtersamplecategory" name="filtersamplecategory" onchange="javascript:selectFilterSampleCategory();">
							 <option value="" > </option>
								<c:forEach var="category" items="${document.sampleCategoryList}">
									<c:set var="selected" value=""></c:set>
									<c:if test="${category eq document.sampleCategory}">
										<c:set var="selected" value="selected"></c:set>
									</c:if>
									<option value="<c:out value="${category}"/>" ${selected} title='${category}'>
									<c:out value="${category}" /></option>
								</c:forEach>
							</select> 
						</span>
						<span class="error"></span>
					</div>
					<div class="row" id="filterSampleTypeDiv">
						<span class="label wrapText">Sample Document Type:</span> 
						<span class="formfield">
							<select id="filtersampletype" name="filtersampletype">
								 <option value="" > </option>
								<c:forEach var="type" items="${document.sampleTypeList}">
									<%String selected = "";%>
										<c:if test="${type==document.sampleType}">
											<%selected = "selected";%>
										</c:if>
									<option value="<c:out value="${type}"/>" <%=selected%> title="${type}">
									<c:out value="${type}" /></option>
								</c:forEach>
							</select> 
						</span>
						<span class="error"></span>
					</div>
					
					<!--Start of R4 Document Vault changes: Adding sharing div for city filter -->
					<div class='row' id="sharedDiv">
						<span class='label'>Shared:</span>
						<span class='formfield rdoLabels'>
							<% if(loDocBean.getDocSharedStatus()!=null && loDocBean.getDocSharedStatus().equalsIgnoreCase("shared")){ %>
								<input type="radio" name="shared" value="both" id="rdoBoth" class='rdoBtn' ></input> <label for="rdoBoth">Both Shared & Un-Shared</label>
								<input type="radio" name="shared" value="shared" id='rdoShared' class='rdoBtn' checked="checked"></input> <label for='rdoShared'>Only Shared</label>
								<input type="radio" name="shared" value="unshared" id='rdoUnshared' class='rdoBtn'></input> <label for='rdoUnshared'>Only Un-Shared</label>
							<%} else if(loDocBean.getDocSharedStatus()!=null && loDocBean.getDocSharedStatus().equalsIgnoreCase("unshared")){%>
								<input type="radio" name="shared" value="both" id="rdoBoth" class='rdoBtn'></input> <label for="rdoBoth">Both Shared & Un-Shared</label>
								<input type="radio" name="shared" value="shared" id='rdoShared' class='rdoBtn'></input> <label for='rdoShared' >Only Shared</label>
								<input type="radio" name="shared" value="unshared" id='rdoUnshared' class='rdoBtn' checked="checked"></input> <label for='rdoUnshared'>Only Un-Shared</label>
							<%} else{ %>
								<input type="radio" name="shared" value="both" id="rdoBoth" class='rdoBtn' checked="checked"></input> <label for="rdoBoth">Both Shared & Un-Shared</label>
								<input type="radio" name="shared" value="shared" id='rdoShared' class='rdoBtn'></input> <label for='rdoShared' >Only Shared</label>
								<input type="radio" name="shared" value="unshared" id='rdoUnshared' class='rdoBtn'></input> <label for='rdoUnshared'>Only Un-Shared</label>
							<%} %>
						</span>
					</div>
					
					<div class='row' id="providerDiv">
						<span class='label'>Provider:</span>
						<span class='formfield'>
							<select id="provider" name="provider" class="input">
							</select>
						</span>
					</div>
					<div class='row' id="agencyDiv">
						<span class='label'>NYC Agency:</span>
						<span class='formfield'>
							<select id="agency" name="agency" class="input">
							</select>
						</span>
					</div>
					<!--End of R4 Document Vault changes -->
					
					<div class='row'>
						<span class='label'>Modified from:</span>
						<span class='formfield'>
							<input type="text" style='width:78px;' name="modifiedfrom" id='modifiedfrom' value="${document.filterModifiedFrom}" validate="calender" maxlength="10"/>
						   	<img title="Modified From Date" src="../framework/skins/hhsa/images/calender.png" onclick="NewCssCal('modifiedfrom',event,'mmddyyyy');return false;"/>
						</span>
						<span class="error" id="datevalidate"></span>
					</div>
					
					<div class='row'>
						<span class='label'>Modified To:</span>
						<span class='formfield'>
							<input type="text" style='width:78px;' name="modifiedto" id='modifiedto' value="${document.filterModifiedTo}" validate="calender" maxlength="10"/>
							<img title="Modified To Date" src="../framework/skins/hhsa/images/calender.png" onclick="NewCssCal('modifiedto',event,'mmddyyyy');return false;"/>
						</span>
						<span class="error" id="dateRange"></span>
					</div>
					
					<div class="buttonholder">
						<input type="button" id="clearfilter" title="Clear Filters" value="Clear Filters" onclick="clearFilter(this.form,'null','<%=request.getAttribute("providerSet")%>','<%=request.getAttribute("agencySet")%>')" class="graybtutton"/>
						<input type="button" value="Filter" title="Filter" id="filter" onclick="displayFilter(originalFormAction)"/>
					</div> 
				
				</div>
				<!-- Popup for Filter Task Ends -->
			</div>
			<div class='clear'></div>
	
			<!--Filter and Reassign section ends -->
		
			<!-- Form Data Starts -->
			<div id="mymain">
			 	<!-- Grid Starts -->
			 	<!-- Changes done for defect 6252 -->
			    <div  class="tabularWrapper" style="height: 400px;">
			        <st:table objectName="documentList"  cssClass="heading"
						alternateCss1="evenRows" alternateCss2="oddRows" pageSize='<%=(Integer)session.getAttribute("allowedObjectCount")%>'>
						<!--Start of R4 Document Vault changes-->
						<st:property headingName="" columnName="documentId" align="center" size="5%" >
							<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.DocumentNameExtension" />
						</st:property>
						<!--End of R4 Document Vault changes -->
						<st:property headingName="Document Name" columnName="docName" align="center" sortType="docName" sortValue="asc"
							size="15%">
							<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.DocumentNameExtension" />
						</st:property>
							<st:property headingName="Document Category" columnName="docCategory" sortType="docCategory" sortValue="asc"
								align="right" size="15%" />
							<st:property headingName="Sample Document Type" columnName="sampleType" sortType="sampleType" sortValue="asc"
								align="left" size="15%" />
						<st:property headingName="Modified" columnName="date"
							sortType="date" sortValue="asc" align="right" size="10%" />
						<st:property headingName="Last Modified By" columnName="lastModifiedBy" sortType="lastModifiedBy" sortValue="asc"
							 align="right" size="10%" />
						<!--Start of R4 Document Vault changes-->	 
						<st:property headingName="Shared" columnName="shareStatus" sortType="shareStatus" sortValue="asc"
						align="center" size="15%">
							<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.DocumentNameExtension" />
						</st:property>
						<!--End of R4 Document Vault changes -->
						<st:property headingName="Actions" columnName="actions" 
							align="right" size="20%" >
							<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.DocumentActionExtension" />
						</st:property>
					</st:table>
				</div>
			    <!-- Grid Ends -->
			</div>
			
			<input type="hidden" name="hiddenDocCategory" value='<%=document.getFilterDocCategory()%>' />	
			<input type="hidden" name="hiddenDocType" value='<%=document.getFilterDocType()%>'/>
			<input type="hidden" name="hiddenFilterModifiedFrom" value='<%=document.getFilterModifiedFrom()%>' />
			<input type="hidden" name="hiddenFilterModifiedTo" value='<%=document.getFilterModifiedTo()%>' />
			<input type="hidden" name="hiddenFilterProviderId" value='<%=document.getFilterProviderId()%>' />
			<input type="hidden" name="hiddenFilterNYCAgency" value='<%=document.getFilterNYCAgency()%>'/>
			<input type="hidden" name="hiddenDocShareStatus" value="<%=document.getDocSharedStatus()%>" />	
			<input type="hidden" name="hiddenSampleCategory" value='<%=document.getFilterSampleCategory()%>'/>	
			<input type="hidden" name="hiddenSampleType" value='<%=document.getFilterSampleType()%>'/>
			<!-- Form Data Ends -->
			<input type="hidden" id="isOrganizationSharesDoc" name="isOrganizationSharesDoc" value="${isOrganizationSharesDoc}" />
			<input type="hidden" name="provider" id="ownerProviderId" value="${ownerProviderId}" />
			<input type="hidden" name="providerId" id="providerId" value="${providerId}" />
			<input type="hidden" name="section" id="section" value="${section}" />
			<input type="hidden" name="subsection" id="subsection" value="${subsection}" />
		</form>
		<!-- Overlay Divs Start -->
			<div class="overlay"></div>
			<div class="alert-box">
				<div class="content">
			  		<div id="newTabs" class='wizardTabs'>
						<div class="tabularCustomHead">Upload Document</div> 
						<h2 class='padLft'>Upload Document</h2>
						<div class='hr'></div>
						<ul>
							<li id='step1' class='active'>Step 1: File Selection</li>
							<li id='step2' class="last">Step 2: Document Information</li>
						</ul>
			            <div id="tab1"></div>
			            <div id="tab2"></div>
					</div>
			  	</div>
		  	<a  href="javascript:void(0);" class="exit-panel upload-exit">&nbsp;</a>
			</div>
		
		<!--Start of R4 Document Vault changes: adding alert box divs for share/unshare overlays-->	
		<div class="alert-box-sharedoc">
		<div class="content">
			<div class='wizardTabber'>
				<div class="tabularCustomHead">Share Document(s)<label id="sharelabel" class="overlay-subtitle"></label></div>
				<ul id="sharewiz" class='wizardUlStep1' >
					<li id='step1confirmDoc' >Step 1: Confirm Documents</li>
					<li id='step2selectOrg'>Step 2: Select Providers</li>
					<li id='step3selectNycAgency'>Step 3: Select NYC Agencies</li>
					<li id='step4confirmSel'>Step 4: Confirm Selections</li>
				</ul>
		        <div id="tab3"></div>
		        <div id="tab4"></div>
		        <div id="tab5"></div>
		        <div id="tab6"></div>
			</div>
		</div>
		<a  href="javascript:void(0);" class="exit-panel" title="Exit">&nbsp;</a>
	</div>
	<div class="alert-box-unshareall">
		<div class="content">
			<div id="newTabs">
				<div class="tabularCustomHead">Remove All Access</div>
				<div id="unshareall"></div>
			</div>
		</div>
		<a  href="javascript:void(0);" class="exit-panel" title="Exit">&nbsp;</a>
	</div>
	<div class="alert-box-unsharebyprovider">
		<div class="content">
			<div id="newTabs">
				<div class="tabularCustomHead">Remove Access By Organization</div>
				<div id="unshareprovider"></div>
			</div>
		</div>
		<a  href="javascript:void(0);" class="exit-panel" title="Exit">&nbsp;</a>
	</div>
	<div class="alert-box-removeselectedprovs">
		<div class="content">
		  	<div id="newTabs">
				<div class="tabularCustomHead"><label id="removeprovlabel" class="overlay-subtitle"></label></div>
		        <div id="displayshared"></div>
			</div>
		</div>
		<a  href="javascript:void(0);" class="exit-panel" title="Exit">&nbsp;</a>
	</div>
	<!--End of R4 Document Vault changes -->		
			
			<div class="alert-box-help">
				<div class="content">
			  		<div id="newTabs" class='wizardTabs'>
						<div class="tabularCustomHead">Document Vault - Help Documents</div>
			            <div id="helpPageDiv"></div>
					</div>
		  		</div>
		  		<a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
			</div>
			<div class="alert-box-delete">
				<div class="content">
			  		<div id="newTabs" class='wizardTabs'>
						<div class="tabularCustomHead">Remove Document from Document Vault</div>
						<div id="deleteDiv">
						    <div class="pad6 clear promptActionMsg">Are you sure you want to delete this document?</div>
						    <div class="buttonholder txtCenter">
						        <input type="button" title="No" class="graybtutton exit-panel" value="No" />
						        <input type="button" title="Yes" class="button" id="deleteDoc" value="Yes" />
						    </div>
						</div>
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
			<!-- Overlay Divs End -->	
		<div id="overlayedJSPContent" style="display:none"></div>
		<% } else {%>
	   		<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
		<%} %>