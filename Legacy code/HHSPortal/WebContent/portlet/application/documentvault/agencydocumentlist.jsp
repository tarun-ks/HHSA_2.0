<!-- Agency R4 Document Vault changes: Jsp added for Agency Document Vault screen -->

<%@page import="com.nyc.hhs.model.Document"%>
<%@page import="java.util.ArrayList"%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="com.nyc.hhs.frameworks.grid.*,com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant,org.apache.commons.lang.StringEscapeUtils"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<portlet:defineObjects/>
<style type="text/css">
.alert-box,.alert-box-sharedoc{
        position:fixed !important;
        top:25% !important
}
</style>
<link rel="stylesheet" href="../css/documentlist.css" type="text/css"></link>
<script type="text/javascript" src="../resources/js/calendar.js"></script>
<script type="text/javascript" src="../js/agencydocumentlist.js"></script>
<script type="text/javascript"> 
var agnFormAction;
var deleteDocumentId = "";
// on load function to perform various checks on loading of jsp
$(document).ready(function() {
	agnFormAction = document.agencyform.action;	
	if($("#isOrganizationSharesDoc").val()!= '' && $("#isOrganizationSharesDoc").val() == "true"){
		agnFormAction = agnFormAction +"&action=documentVault";
		$('select').each(function() {
			var selectBoxId = $(this).attr("id");
			if(selectBoxId.match("^actions")) {
		 		var lastValue = $('#'+selectBoxId+' option:last-child').val();
		 		if(lastValue=='Delete Document'){
					$('#'+selectBoxId).find('option:last').remove();//("selected","selected");
		 		}
		 	}
		});
	}
	if(document.getElementById("profiltercategory").value!="")
	{
		document.getElementById("filtertype").disabled=false;
	}else{
		document.getElementById("filtertype").disabled=true;
	}
 	if('null' != '<%=request.getAttribute("message")%>' && '<%=request.getAttribute("messageType")%>' != 'confirmation'){
		$(".messagediv").html('${message}'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
		$(".messagediv").addClass('<%= request.getAttribute("messageType")%>');
		$(".messagediv").show();
		<%request.removeAttribute("message");%>
	}
	if('<%=request.getAttribute("messageType")%>' == "confirmation")
	{
		$(".overlay").launchOverlay($(".alert-box-delete"), $(".nodelete"), "350px", null, "onReady");
		// Added for 1795 : This will execute when we click on the X button on the overlay 
		$(".nodelete").unbind("click").click(function(){
			document.agencyform.action = agnFormAction+'&documentId='+'<%=request.getAttribute("documentId")%>'+'&removeNavigator=true&next_action=<%=ApplicationConstants.CANCEL_DELETE%>';
			document.agencyform.submit();
		});
		
	}
		
checkShareStatus('<%=request.getAttribute("sharedFlag")%>','<%=request.getAttribute("providerSet")%>','<%=request.getAttribute("agencySet")%>');
updateProviderAndAgency('<%=request.getAttribute("providerSet")%>','<%=request.getAttribute("agencySet")%>','<%=((Document)request.getAttribute("document")).getFilterProviderId()%>',
'<%=((Document)request.getAttribute("document")).getFilterNYCAgency()%>');

// This will execute when delete document option is selected from drop down
$('#deleteDoc').click(function() {
	pageGreyOut();
	$("#next_action").val('deleteDocument');
	document.agencyform.action = agnFormAction+'&documentId='+deleteDocumentId+'&removeNavigator=true';
	document.agencyform.submit();
});


// Defect 1795 - This will execute when no delete document option is selected from drop down
$('#nodeleteDoc').click(function() {
	document.agencyform.action = agnFormAction+'&documentId='+deleteDocumentId+'&removeNavigator=true&next_action=<%=ApplicationConstants.CANCEL_DELETE%>';
	// On click of 'No' button on delete document overlay an ajax call is made rather than form submit so that page is not refreshed.
	var options = 
	{	
		success: function(responseText, statusText, xhr ) 
				{
			$(".overlay").closeOverlay();	
				},
				error:function (xhr, ajaxOptions, thrownError)
				{                     
					showErrorMessagePopup();
					removePageGreyOut();
				}
			  };
			$(document.agencyform).ajaxSubmit(options);
});


// This will execute when upload button is clicked
$('#uploadDoc').click(function() {
	pageGreyOut();
    uploadDocument(agnFormAction);
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
	//var overlayLaunchedTemp = overlayLaunched;
	//var alertboxLaunchedTemp = alertboxLaunched;
	$("#overlayedJSPContent").html($response);
	//overlayLaunched = overlayLaunchedTemp;
	//alertboxLaunched = alertboxLaunchedTemp;
	$(".overlay").launchOverlay($(".alert-box"), $(".exit-panel.upload-exit"), "890px", null, "onReady");
	$('ul').removeClass('ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all ui-state-hover ui-tabs-nav');
	$('li').removeClass('ui-state-default ui-corner-top ui-state-hover');
	//$.unblockUI();
	removePageGreyOut();
	},
	error:function (xhr, ajaxOptions, thrownError)
	{                     
		showErrorMessagePopup();
		removePageGreyOut();
	}
	};
	$(document.agencyform).ajaxSubmit(options);
	return false;
});

// This will execute when any option is selected for filter Document Category
$('#profiltercategory').change(function() {
	if(filterCategoryForProvider()){
	getContentForProvider();
	}	
});	

// This will execute when Share button is clicked
$('#shareDoc').click(function() {
	pageGreyOut();
    shareDocument(agnFormAction);
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
						//var overlayLaunchedTemp = overlayLaunched;
						//var alertboxLaunchedTemp = alertboxLaunched;
						$("#overlayedJSPContent").html($response);
						//overlayLaunched = overlayLaunchedTemp;
						//alertboxLaunched = alertboxLaunchedTemp;
						$('#sharewiz').removeClass('wizardUlStep1')
						.removeClass('wizardUlStep2').removeClass('wizardUlStep3')
						.removeClass('wizardUlStep4').addClass('wizardUlStep1');
						$(".overlay").launchOverlay($(".alert-box-sharedoc"), $(".exit-panel"), "850px", null, "onReady");
						//$.unblockUI();
						removePageGreyOut();
					},
					error:function (xhr, ajaxOptions, thrownError)
					{                     
						showErrorMessagePopup();
						removePageGreyOut();
					}
				  };
				$(document.agencyform).ajaxSubmit(options);
				return false;
			});	
			
		// This will execute when unshareAll button is clicked
		$('#unshareAll').click(function() {
			pageGreyOut();
    		unshareAllDocument(agnFormAction);
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
						//var overlayLaunchedTemp = overlayLaunched;
						//var alertboxLaunchedTemp = alertboxLaunched;
						$("#overlayedJSPContent").html($response);
						//overlayLaunched = overlayLaunchedTemp;
						//alertboxLaunched = alertboxLaunchedTemp;
						$(".overlay").launchOverlay($(".alert-box-unshareall"), $(".exit-panel"), "850px",null, "onReady");
						//$.unblockUI();
						removePageGreyOut();
					},
					error:function (xhr, ajaxOptions, thrownError)
					{                     
						showErrorMessagePopup();
						removePageGreyOut();
					}
				  };
				$(document.agencyform).ajaxSubmit(options);
				return false;		
		});	
		
		// This will execite when unshareByProvider button is clicked
		$('#unshareByProvider').click(function() {
			pageGreyOut();
    		unshareDocumentByProvider(agnFormAction);
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
						//var overlayLaunchedTemp = overlayLaunched;
						//var alertboxLaunchedTemp = alertboxLaunched;
						$("#overlayedJSPContent").html($response);
						//overlayLaunched = overlayLaunchedTemp;
						//alertboxLaunched = alertboxLaunchedTemp;
						$(".overlay").launchOverlay($(".alert-box-unsharebyprovider"), $(".exit-panel"), "850px", "500px","onReady");
						//$.unblockUI();
						removePageGreyOut();
					},
					error:function (xhr, ajaxOptions, thrownError)
					{                     
						showErrorMessagePopup();
						removePageGreyOut();
					}
				  };
				$(document.agencyform).ajaxSubmit(options);
				return false;		
		});	
		
		// This will execute when Shared link is clicked
		$('.linkclass').click(function() {
			pageGreyOut();
			var linkId = $(this).attr('id'); 
			var name = $(this).attr('name');
    		displaySharedDocuments(linkId, name, agnFormAction);
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
						//var overlayLaunchedTemp = overlayLaunched;
						//var alertboxLaunchedTemp = alertboxLaunched;
						$("#overlayedJSPContent").html($response);
						//overlayLaunched = overlayLaunchedTemp;
						//alertboxLaunched = alertboxLaunchedTemp;
						$(".overlay").launchOverlay($(".alert-box-removeselectedprovs"), $(".exit-panel"), "850px", "400px", "onReady");
						//$.unblockUI();
						removePageGreyOut();
					},
					error:function (xhr, ajaxOptions, thrownError)
					{                     
						showErrorMessagePopup();
						removePageGreyOut();
					}
				  };
				$(document.agencyform).ajaxSubmit(options);
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
		
		if("null" != '<%= request.getAttribute("filterStatus")%>' && "notfiltered" == '<%= request.getAttribute("filterStatus")%>'){
			clearFilter('<%=((Document)request.getAttribute("document")).getDocSharedStatus()%>','<%=request.getAttribute("providerSet")%>','<%=request.getAttribute("agencySet")%>');
		}
		
		if ('<%=((Document)request.getAttribute("document")).getDocSharedStatus()%>' == "unshared") {
			$('#providerDiv').hide();
			$('#agencyDiv').hide();

		}
		
		// For Defect - 5998
		if($('#rdoShared').is(":checked") || $('#rdoUnshared').is(":checked")){
	    	 $('#filterbutton').val('Filtered');
	    	 $('#filterbutton').attr('title','Filtered');
	     }
});

// This method will get the filter document category and 
//will return the list of document types through servlet call
function getContentForProvider() {
	pageGreyOut();
	var selectedInput = document.getElementById("profiltercategory").value;
	var url = $("#contextPathSession").val()+"/GetContent.jsp?selectedInput=" + selectedInput+"&organizationId=<%=ApplicationConstants.AGENCY_ORG%>";
	postRequestFetchDocType(url);
	//$.unblockUI();
	removePageGreyOut();
}


</script>

<!-- Body Wrapper Start -->
	<form id="myform" action="<portlet:actionURL/>" method ="post" name='agencyform'>
	    <input type="hidden" name="next_action" value="" id="next_action">
	    <input type="hidden" id="agencySet" value="${agencySet}"/>
		<input type="hidden" id="providerSet" value="${providerSet}"/>
		<input type="hidden" id="filterStatus" value="${filterStatus}"/>
		<!-- Body Container Starts -->
			<h2>Document Vault</h2> 
			<c:if test="${org_type ne 'city_org'}">
				<div class="iconQuestion"><a href="javascript:void(0);" title="Need Help?" onclick="pageSpecificHelp('Document Vault');"></a></div>
			</c:if>
			<div class='hr'></div>
			<div>Manage documents loaded by your organization.</div>
			<div class="messagediv" id="messagediv"></div>
			
			<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S030_PAGE, request.getSession())){
				
				Document loDocBean = (Document)request.getAttribute("document"); %>
				<!-- Container Starts -->
				<!--Filter and Reassign section starts -->
				<div class="taskButtons">
				<%if(request.getAttribute("filterStatus") == "filtered"){%>
					<input type="button" value="Filtered" title="Filtered" id= "filterbutton" class="filterDocument" onclick="setVisibility('documentValuePop', 'inline');"/>	
				<% } else{%>
				 	<input type="button" value="Filter Documents" title="Filter Documents" id= "filterbutton" class="filterDocument" onclick="setVisibility('documentValuePop', 'inline');"/>
				<%}%>
				<c:if test="${org_type ne 'city_org'}">
					<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S032_PAGE, request.getSession())){%>
					<input type="button" value="Upload" title="Upload" id="uploadDoc" class="upload" />
					<%}%>
				</c:if>
			<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S033_AGENCY_PAGE, request.getSession())){%>
				 <input type="button" value="Share" id="shareDoc" title="Share" class="share disable" disabled="disabled"/>	
				 <input type="button" value="Un-Share All" title="Un-Share All" id="unshareAll" class="unShare disable" disabled="disabled"/>
				 <input type="button" value="Un-Share by Organization" title="Un-Share by Organization" id="unshareByProvider" class="unShare disable"/>
			<%}%>
			<!-- Popup for Filter Task Starts -->
				<div id="documentValuePop" class='formcontainer providerFilter'>
					<div class='close'><a href="javascript:setVisibility('documentValuePop', 'none');" title="Close">X</a></div>
					<div class='row'>
						<span class='label'>Document Category:</span>
						<span class='formfield'>
							<select id = "profiltercategory" name="filtercategory" class="terms">
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
					<div class='row'>
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
					<div class='row' id="sharedDiv">
						<span class='label'>Shared:</span>
						<span class='formfield rdoLabels'>
							<% if(loDocBean.getDocSharedStatus()!=null && loDocBean.getDocSharedStatus().equalsIgnoreCase("shared")){ %>
								<input type="radio" name="shared" value="both" id="rdoBoth" class='rdoBtn'></input> <label for="rdoBoth">Both Shared & Un-Shared</label>
								<input type="radio" name="shared" value="shared" id='rdoShared' class='rdoBtn' checked="checked"></input> <label for='rdoShared'>Only Shared</label>
								<input type="radio" name="shared" value="unshared" id='rdoUnshared' class='rdoBtn'></input> <label for='rdoUnshared'>Only Un-Shared</label>
							<%} else if(loDocBean.getDocSharedStatus()!=null && loDocBean.getDocSharedStatus().equalsIgnoreCase("unshared")){%>
								<input type="radio" name="shared" value="both" id="rdoBoth" class='rdoBtn'></input> <label for="rdoBoth">Both Shared & Un-Shared</label>
								<input type="radio" name="shared" value="shared" id='rdoShared' class='rdoBtn'></input> <label for='rdoShared' >Only Shared</label>
								<input type="radio" name="shared" value="unshared" id='rdoUnshared' class='rdoBtn' checked="checked" ></input> <label for='rdoUnshared'>Only Un-Shared</label>
							<%} else{ %>
								<input type="radio" name="shared" value="both" id="rdoBoth" class='rdoBtn' checked="checked" ></input> <label for="rdoBoth">Both Shared & Un-Shared</label>
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
					<div class='row'>
						<span class='label'>Modified from:</span>
						<span class='formfield' style='width: 30% !important'>
							<input type="text" style='width:78px;' name="modifiedfrom" id='modifiedfrom' 
								value="${document.filterModifiedFrom}" maxlength="10" validate="calender"/>
					      	<img title="Modified From Date" alt="Modified From Date" src="../framework/skins/hhsa/images/calender.png" onclick="NewCssCal('modifiedfrom',event,'mmddyyyy');return false;"/>
						</span>
						<span class="error" id="datevalidate"></span>
					</div>
					<div class='row'>
						<span class='label'>Modified To:</span>
						<span class='formfield' style="width: 30% !important">
							<input type="text" style='width:78px;' name="modifiedto" id='modifiedto' 
								value="${document.filterModifiedTo}"  validate="calender" maxlength="10"/>
					      	<img title="Modified To Date" alt="Modified To Date" src="../framework/skins/hhsa/images/calender.png" onclick="NewCssCal('modifiedto',event,'mmddyyyy');return false;"/>
						</span>
						<span class="error" id="dateRange"></span>
					</div>
					<div class="buttonholder">
						<input type="button" id="clearfilter" title="Clear Filters" value="Clear Filters" onclick="clearFilter('null','<%=request.getAttribute("providerSet")%>','<%=request.getAttribute("agencySet")%>')" class="graybtutton"/>
						<input type="button" value="Filter" title="Filter" id="filter" onclick="return displayFilter(agnFormAction)"/>
					</div> 
				</div>
				<!-- Popup for Filter Task Ends -->
				</div>
			
				  <div class='clear'></div>			
		
			<!--Filter and Reassign section ends -->
	
			<!-- Form Data Starts -->
			<div id="mymain">
			<!-- Grid Starts -->
			<div  class="tabularWrapper">
				<st:table objectName="documentList"  cssClass="heading"
					alternateCss1="evenRows" alternateCss2="oddRows" pageSize='<%=(Integer)session.getAttribute("allowedObjectCount")%>'>
					<st:property headingName="" columnName="documentId" align="center" size="5%" >
						<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.DocumentNameExtension" />
					</st:property>
					<st:property headingName="Document Name" columnName="docName" align="center" sortType="docName" sortValue="asc" size="30%">
						<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.DocumentNameExtension" />
					</st:property>
					<st:property headingName="Document Type" columnName="docType" sortType="docType" sortValue="asc" align="center" size="20%"  />
					<st:property headingName="Modified" columnName="date"
						align="right" size="10%" sortType="date" sortValue="asc"/>
					<st:property headingName="Shared" columnName="shareStatus" sortType="shareStatus" sortValue="asc"
						align="center" size="15%">
						<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.DocumentNameExtension" />
					</st:property>
					<st:property headingName="Actions" columnName="actions"  align="center" size="20%" >
						<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.DocumentActionExtension" />
					</st:property>
				</st:table>
			</div>
			<!-- Grid Ends -->
			</div>
			
			<input type="hidden" name="hiddenDocCategory" value='<%=loDocBean.getFilterDocCategory()%>' />	
			<input type="hidden" name="hiddenDocType" value='<%=loDocBean.getFilterDocType()%>'/>
			<input type="hidden" name="hiddenFilterModifiedFrom" value='<%=loDocBean.getFilterModifiedFrom()%>' />
			<input type="hidden" name="hiddenFilterModifiedTo" value='<%=loDocBean.getFilterModifiedTo()%>' />
			<input type="hidden" name="hiddenFilterProviderId" value='<%=loDocBean.getFilterProviderId()%>'/>
			<input type="hidden" name="hiddenFilterNYCAgency" value='<%=loDocBean.getFilterNYCAgency()%>'/>
			<input type="hidden" name="hiddenDocShareStatus" value='<%=loDocBean.getDocSharedStatus()%>' />				
			<!-- Form Data Ends -->
	</form>			
	<!-- Container Ends -->
		
	<div class="overlay"></div>
	<div class="alert-box">
		<div class="content">
			<div id="newTabs"  class='wizardTabs'>
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
		<a  href="javascript:void(0);" class="exit-panel upload-exit" title="Exit">&nbsp;</a>
	</div>
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
	<div class="alert-box-help">
		<div class="content">
			<div id="newTabs" class='wizardTabs'>
				<div class="tabularCustomHead">Document Vault - Help Documents</div>
		        <div id="helpPageDiv"></div>
			</div>
		</div>
		<a  href="javascript:void(0);" class="exit-panel" title="Exit">&nbsp;</a>
	</div>
	<div class="alert-box-delete">
		<div class="content">
		  	<div id="newTabs" class='wizardTabs'>
				<div class="tabularCustomHead">Remove Document from Document Vault
					<a href="javascript:void(0);" class="exit-panel nodelete" title="Exit"></a>
				</div>
				<div id="deleteDiv">
					<div class="pad6 clear promptActionMsg">Are you sure you want to delete this document?
					</div>
					<div class="buttonholder txtCenter">
						<input type="button" title="No" class="graybtutton exit-panel" id="nodeleteDoc" value="No" />
						<input type="button" title="Yes" class="button" id="deleteDoc" value="Yes" />
					</div>
				</div>
			</div>
		</div>
		<a  href="javascript:void(0);" class="exit-panel nodelete" title="Exit">&nbsp;</a>
		</div>
		<div class="alert-box-contact">
			<div class="content">
				<div id="newTabs">
				<div id="contactDiv"></div>
				</div>
			</div>
		</div>
		<c:if test="${param.next_action eq 'openProviderView' or lsAction eq  'openProviderView'}">
			<input type="hidden" name="action" value="documentVault" id="searchDocumentVaultId"/>
		</c:if>
		<input type="hidden" id="isOrganizationSharesDoc" name="isOrganizationSharesDoc" value="${isOrganizationSharesDoc}" />
		<div id="overlayedJSPContent" style="display:none"></div>
		<% } else {%>
	   		<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
	   	<%} %>
