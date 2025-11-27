<%-- This JSP is for Taxonomy tagging base screen --%>
<%@page import="java.util.ArrayList"%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="com.nyc.hhs.model.TaxonomyTree"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@taglib prefix="render" uri="http://www.bea.com/servers/portal/tags/netuix/render" %>

<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/taxonomytagging.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/js/accdocumentlist.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/resources/js/calendar.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/js/simpletreemenu.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/autoNumeric.js"></script>
<%-- Code updated for R4 Starts --%>

<style>
#headerTableProgram, #serviceFunctionList{
	width: 80%;
	margin-bottom: 10px;
	margin-top: 10px;
}
.tabularContainer{
	border: 0px;
}

.alert-box{
        top:25% !important;
}


</style><%-- Code updated for R4 Ends --%>
<portlet:defineObjects/>
<script type="text/javascript">
var PROGRAM_NAME_SORT =  ["ORDER BY AGENCY_ID ASC , PROGRAM_NAME ASC"
						,"ORDER BY AGENCY_ID ASC , PROGRAM_NAME ASC"      
						,"ORDER BY AGENCY_ID DESC  , PROGRAM_NAME ASC"    
						,"ORDER BY PROGRAM_NAME ASC , ACTIVE_FLAG ASC"    
						,"ORDER BY PROGRAM_NAME DESC , ACTIVE_FLAG ASC"   
						,"ORDER BY CREATED_DATE DESC , PROGRAM_NAME ASC"  
						,"ORDER BY CREATED_DATE ASC , PROGRAM_NAME ASC"   
						,"ORDER BY MODIFIED_DATE DESC , PROGRAM_NAME ASC" 
						,"ORDER BY MODIFIED_DATE ASC , PROGRAM_NAME ASC"  
						,"ORDER BY ACTIVE_FLAG ASC , MODIFIED_DATE DESC"  
						,"ORDER BY ACTIVE_FLAG DESC , MODIFIED_DATE DESC" ];
var SORT_ORDER =  [  "sort-ascending","sort-descending"];

$(document).ready(// for competition pool typehead 
		function() {
			restoreFilter();
			
			$('input[name="filterStatus"]').click(function(){
				if($('input[name="filterStatus"]:checked').length == 1 ){
					$("#searchStatus").val($("[name=filterStatus]:checked").val());
				}else{
					$("#searchStatus").val("");
				}
        	});
			
			$("#filter").click(function() {
				filter();
			});
			$("#statusActive").click(function() {
				$("#statusInactive").attr('checked', false);
			});
			$("#statusInactive").click(function() {
				$("#statusActive").attr('checked', false);
			});
			
			$("#clearfilter").click(function() {
				clearFilter();
			});
 			$('#addNewProgram').click(function() {
 	    		pageGreyOut();
 				 var options = 
	    			{	
					   	success: function(responseText, statusText, xhr ) 
						{
							var $response=$(responseText);
                            var data = $response.contents().find(".overlaycontent");
                            	$("#newProgramTab1").empty();
					 			$("#newProgramTab2").empty();
                            if(data != null || data != ''){
                            	$("#newProgramTab1").html(data.detach());
							}
							$("#overlayedJSPContent").html($response);
							$(".overlay").launchOverlay($(".alert-box"), $(".exit-panel"), "890px", null, "onReady");
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
 					 document.programnamesform.action = $('#addNewProgramUrl').val();
				  	$(document.programnamesform).ajaxSubmit(options);
				  return false;  
			});	

			typeHeadSearch($('#filterProgramName'), $("#programNameListUrl")
					.val() + "&programNameQueryId=getListByTypeHead", null,
					"typeHeadCallBackProgram", $("#programNameTypeHeadError"));
		});


//This function is called as call back function when program name type head search is performed
var suggestionVal = "";
var isValid = false;
function typeHeadCallBackProgram() {
	if (!$("#documentValuePop").is(":visible")){
		$(".autocomplete").html("").hide();
	}

	var a = $('#filterProgramName').val();
	isValid = isAutoSuggestValid(a, suggestionVal);
	if (!isValid && $('#filterProgramName').val() != ''
			&& $('#filterProgramName').val().length >= 3) {
		$(".autocomplete").html("").hide();
		suggestionVal = "";
		$('#filterProgramName').parent().next().html("! There is no such Program Name");
	} else if(isValid || $('#filterProgramName').val().length < 3){
		$('#filterProgramName').parent().next().html("");
	}
}


function restoreFilter(){
	var stat = $("#searchStatus").val() ; 
	if( stat  == "1" ){
		$("#inactiveStatus").prop('checked', false);
	}else if( stat == "0" ){
		$("#activeStatus").prop('checked', false);
	}

	$("#agency").val($("#searchAgencyId").val());
	$("#createdfrom").val($("#searchCreatedfrom").val());
	$("#createdto").val($("#searchCreatedto").val());
	$("#modifiedfrom").val($("#searchModifiedfrom").val());
	$("#modifiedto").val($("#searchModifiedto").val());

	setHeaderClass();
}

//Show & Hide Filter tab
function clearFilter() {
	$("#filterProgramName").val("");
	$("#programNameTypeHeadError").empty("");
	$("#searchWord").val("");
	
	$("#agency").val("");
	$("#createdfrom").val('');
	$("#createdto").val('');
	$("#modifiedfrom").val('');
	$("#modifiedto").val('');
	 
	$("#inactiveStatus").attr('checked', true);
	$("#activeStatus").attr('checked', true);
}
 
//Show & Hide Filter tab
function setVisibility(id, visibility) {
	if ($("#" + id).is(":visible")) {
		document.programnamesform.reset();
	}
	$("#" + id).toggle();
	$(".error").html("");
	callBackInWindow("closePopUp");
}

//This will execute when Filter Button tab is clicked  
function filter( ) {
	//$("#searchStatus").val($("[name=filterStatus]:checked").val());
	
	$("#searchAgencyId").val($("#agency").val());
	$("#searchCreatedfrom").val($("#createdfrom").val());
	$("#searchCreatedto").val($("#createdto").val());
	$("#searchModifiedfrom").val($("#modifiedfrom").val());
	$("#searchModifiedto").val($("#modifiedto").val());
	
	$("#searchWord").val($("#filterProgramName").val());
	$("#searchAgencyId").val($("#agency").val());

	$("#currentPage").val(1);
	setVisibility('documentValuePop', 'none');
 
 	document.programnamesform.action = $('#programNamePageUrl').val();
	document.programnamesform.submit(); 

}


//This will execute when clicking page number.
function paging( pageNo) {
	$("#programnamesform").find("#currentPage").val(pageNo);
	$("#searchWord").val($("#filterProgramName").val());
	document.programnamesform.action = $('#programNamePageUrl').val();
	document.programnamesform.submit();
}

function actionFunction(programId, agencyId, objSel ){
	var progName =  $( objSel ).parent().prev().prev().prev().prev().html();
	if( objSel.value == "Inactivate"){
		$("#targetProgramId").val( programId );
		$("#targetProgramName").val( progName );
		$("#targetProgramAgency").val( agencyId );
		inactivateProgram( );
	}else if(objSel.value == "Modify_Name" ){
		//Set parameter for Program Name change
		$("#targetProgramAgency").val( agencyId );
		$("#oldProgramNameChange").val( progName );
		$("#newProgramNameChange").val( "" );
		$("#programIdChange").val( programId );
		modifyName();
	}else if(objSel.value == "Activate" ){
		$("#targetProgramId").val( programId );
		$("#targetProgramName").val( progName );
		$("#targetProgramAgency").val( agencyId );
		activateProgram( );
	}
	objSel.value = "";
}

function modifyName(){
		pageGreyOut();
		 var options = 
			{	
			   	success: function(responseText, statusText, xhr ) 
				{
					var $response=$(responseText);
					var data = $response.contents().find(".overlaycontent");
					$("#changeNameTab1").empty();
			 		$("#changeNameTab2").empty();
					if(data != null || data != ''){
                   		$("#changeNameTab1").html(data.detach());
					}
					$("#overlayedJSPContent").html($response);
					$(".overlay").launchOverlay($(".alert-box-help"), $(".exit-panel"), "890px", null, "onReady");
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
			 document.programnamesform.action = $("#modifyProgramNameUrl").val();
		  	$(document.programnamesform).ajaxSubmit(options);
}

function inactivateProgram( ){
	 var options = 
		{	
		   	success: function(responseText, statusText, xhr ) 
			{
				var $response=$(responseText);
				var data = $response.contents().find(".overlaycontent");
				$("#inactivateProgramTab1").empty();
				if(data != null || data != ''){
              		$("#inactivateProgramTab1").html(data.detach());
				}
				$("#overlayedJSPContent").html($response);
				$(".overlay").launchOverlay($(".alert-box-delete"), $(".exit-panel"), "890px", null, "onReady");
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
		 document.programnamesform.action = $("#inactivateProgramUrl").val();
	  	$(document.programnamesform).ajaxSubmit(options);
}

function activateProgram( ){
	pageGreyOut();
	 var options = 
		{	
		   	success: function(responseText, statusText, xhr ) 
			{
				var $response=$(responseText);
				var data = $response.contents().find(".overlaycontent");
				$("#activateProgramTab1").empty();
		 		$("#activateProgramTab2").empty();
				if(data != null || data != ''){
              		$("#activateProgramTab1").html(data.detach());
				}
				$("#overlayedJSPContent").html($response);
				$(".overlay").launchOverlay($(".alert-box-contact"), $(".exit-panel"), "890px", null, "onReady");
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
		 document.programnamesform.action = $("#activateProgramUrl").val();
	  	$(document.programnamesform).ajaxSubmit(options);
};

function setHeaderClass(){
	var sortData = $('#searchSortOrder').val();
	$.each( PROGRAM_NAME_SORT, function( key, value ) {
		  if( key != 0 && value == sortData ){
				 switch (key){
				 case 1: 
						$("#agency_sort").removeClass("sort-default").addClass("sort-ascending");
						//$("#program_name_sort").removeClass("sort-default").addClass("sort-ascending");
						break;
				 case 2: 
						$("#agency_sort").removeClass("sort-default").addClass("sort-descending");
						//$("#program_name_sort").removeClass("sort-default").addClass("sort-ascending");
						break;
				 case 3: 
						$("#program_name_sort").removeClass("sort-default").addClass("sort-ascending");
						//$("#status_sort").removeClass("sort-default").addClass("sort-ascending");
						break;
				 case 4: 
						$("#program_name_sort").removeClass("sort-default").addClass("sort-descending");
						//$("#status_sort").removeClass("sort-default").addClass("sort-ascending");
						break;
				 case 5: 
						$("#created_date_sort").removeClass("sort-default").addClass("sort-descending");
						//$("#program_name_sort").removeClass("sort-default").addClass("sort-ascending");
						break;
				 case 6: 
						$("#created_date_sort").removeClass("sort-default").addClass("sort-ascending");
						//$("#program_name_sort").removeClass("sort-default").addClass("sort-ascending");
						break;
				 case 7: 
						$("#modified_date_sort").removeClass("sort-default").addClass("sort-descending");
						//$("#program_name_sort").removeClass("sort-default").addClass("sort-ascending");
						break;
				 case 8: 
						$("#modified_date_sort").removeClass("sort-default").addClass("sort-ascending");
						//$("#program_name_sort").removeClass("sort-default").addClass("sort-ascending");
						break;
				 case 9: 
						$("#status_sort").removeClass("sort-default").addClass("sort-ascending");
						//$("#modified_date_sort").removeClass("sort-default").addClass("sort-ascending");
						break;
				 case 10: 
						$("#status_sort").removeClass("sort-default").addClass("sort-descending");
						//$("#modified_date_sort").removeClass("sort-default").addClass("sort-ascending");
						break;
				}
		  }
		});
	
};




function setSortOrder( inx ) {
	var sortData = $('#searchSortOrder').val();
	if( PROGRAM_NAME_SORT[inx] == sortData ){
		$('#searchSortOrder').val(PROGRAM_NAME_SORT[inx+1]);
	} else{
		$('#searchSortOrder').val(PROGRAM_NAME_SORT[inx]);
	}

	return;
}

function sortList(inx) {
	
	setSortOrder(inx);
	
	document.programnamesform.action = $('#programNamePageUrl').val();
	document.programnamesform.submit();
};

</script>


<portlet:resourceURL var="programNameListUrl" id="programNameListUrl" escapeXml='false'></portlet:resourceURL>
<portlet:resourceURL  var="deleteProgramNameUrl" escapeXml="false">
	<portlet:param name="submit_action" value="delete" />
</portlet:resourceURL >

<portlet:actionURL var="addNewProgramUrl" id="addNewProgramUrl" escapeXml='false'>
	<portlet:param name="submit_action" value="addNewProgramStep1" />
</portlet:actionURL>

<portlet:actionURL var="modifyProgramNameUrl" id="modifyProgramNameUrl" escapeXml='false'>
	<portlet:param name="submit_action" value="modifyProgramNameStep1" />
</portlet:actionURL>

<portlet:actionURL var="inactivateProgramUrl" id="inactivateProgramUrl" escapeXml='false'>
	<portlet:param name="submit_action" value="inactivateProgramStep" />
</portlet:actionURL>

<portlet:actionURL var="activateProgramUrl" id="activateProgramUrl" escapeXml='false'>
	<portlet:param name="submit_action" value="activateProgramStep" />
</portlet:actionURL>

<portlet:actionURL  var="programNameUrl" escapeXml="false">
	<portlet:param name="submit_action" value="paging" />
</portlet:actionURL >

<form:form id="programnamesform" name="programnamesform" action="${programNameUrl}" method ="post" commandName="paginationBean">
<!-- 	<input type="hidden" id="submit_action" name="submit_action" value=""/> -->
   	<input type="hidden" name="searchWord"		id="searchWord" value="${paginationBean.searchWord}"	/>
	<input type="hidden" name="currentPage"   	id="currentPage" value="${paginationBean.currentPage}"	/>
	<input type="hidden" name="rowsInPage"		id="rowsInPage" value="${paginationBean.rowsInPage}"	/>
	
	<input type="hidden" name="searchStatus"		    id="searchStatus" value="${paginationBean.searchStatus}"	/>
   	<input type="hidden" name="searchAgencyId"		    id="searchAgencyId" value="${paginationBean.searchAgencyId}"	/>	
	<input type="hidden" name="searchCreatedfrom"   	id="searchCreatedfrom" value="${paginationBean.searchCreatedFrom}"	/>
	<input type="hidden" name="searchCreatedto"		    id="searchCreatedto" value="${paginationBean.searchCreatedTo}"	/>
	<input type="hidden" name="searchModifiedfrom"   	id="searchModifiedfrom" value="${paginationBean.searchModifiedFrom}"	/>
	<input type="hidden" name="searchModifiedto"		id="searchModifiedto" value="${paginationBean.searchModifiedTo}"	/>
	<input type="hidden" name="searchSortOrder"			id="searchSortOrder" value="${paginationBean.searchSortOrder}"	/>

	<input type="hidden" name="oldProgramNameChange"		id="oldProgramNameChange" value=""	/>
	<input type="hidden" name="newProgramNameChange"		id="newProgramNameChange" value=""	/>
	<input type="hidden" name="programIdChange"				id="programIdChange" value=""	/>
	<input type="hidden" name="newProgramNameTemp"		    id="newProgramNameTemp" value="${newProgramNameChange}"	/>
	<input type="hidden" name="newProgramNameConfirm"		id="newProgramNameConfirm" value=""	/>

	<input type="hidden" name="targetProgramId"				id="targetProgramId" value=""	/>
	<input type="hidden" name="targetProgramName"			id="targetProgramName" value=""	/>
	<input type="hidden" name="targetProgramAgency"			id="targetProgramAgency" value=""	/>

	<input type="hidden" name="programNamePageUrl"			id="programNamePageUrl" value="${programNameUrl}"	/>
	<input type="hidden" name="programNameListUrl"			id="programNameListUrl" value="${programNameListUrl}"	/>
	<input type="hidden" name="addNewProgramUrl"			id="addNewProgramUrl" value="${addNewProgramUrl}"	/>

	<input type="hidden" name="modifyProgramNameUrl"		id="modifyProgramNameUrl" value="${modifyProgramNameUrl}"	/>

	<input type="hidden" name="inactivateProgramUrl"		id="inactivateProgramUrl" value="${inactivateProgramUrl}"	/>
	<input type="hidden" name="activateProgramUrl"			id="activateProgramUrl" value="${activateProgramUrl}"	/>

	<input type="hidden" name="restoredInput"               id="restoredInput"   value="" />


	<h2 class='autoWidth'>Program Maintenance</h2>
	<div class='linkReturnValut' style='margin-top:8px; margin-right:8px'>
		<a href="javascript:void(0);" onclick="returnToMain();">Return to Maintenance Homepage</a>
	</div>

	<div class='hr'></div>

	<div>&nbsp;</div>
	<div class="taskButtons">
	    <span>
		    <input type="button" value="Filter Items" class="filterDocument" onclick="setVisibility('documentValuePop', 'inline');">
		</span>
	    <span class="">&nbsp;
			<input type="button" value="Add New Program"  id="addNewProgram" name="addNewProgram" class="add marginReset" >
		</span>
		<span>Total Programs are  <label>${paginationBean.totalDataCount}</label></span>

<!-- [Start] Search Filter pop up  -->
	    <div id="documentValuePop" class="formcontainer providerFilter" style='width:460px;'>
	        <div class="close"><a href="javascript:setVisibility('documentValuePop', 'none');">X</a></div>
		        <div class='row' id="agencyDiv">
					<span class='label'>Agency:</span>
					<span class='formfield'>
						<select id="agency" name="agency" class="input">
							<option value="" > -- SELECT -- </option>
							<c:forEach items="${agencyList}" var="agencyItem" >
								<option value="${agencyItem.agencyId}"> ${agencyItem.agencyName}</option>
							</c:forEach>
						</select>
					</span>
				</div>
	        
		        <div class="row">
		            <span class="label">Program Name:</span>
		            <span class="formfield">
		            	<input id="filterProgramName" id="filterProgramName" name="filterProgramName" class="proposalConfigDrpdwn"  type="text" value="${paginationBean.searchWord}" maxlength="120" >
		            </span>
		            <span class="error" id="programNameTypeHeadError"></span>
		        </div>

				<div class='row'>
					<span class='label'>Created from:</span>
					<span class='formfield'>
						<span class='floatLft'>							
							<input type="text" style='width:78px;' name="createdfrom" id="createdfrom" value="${paginationBean.searchCreatedFrom}" validate="calender" maxlength="10"/> 
					   	<img title="Created From Date" src="../framework/skins/hhsa/images/calender.png" onclick="NewCssCal('createdfrom',event,'mmddyyyy');return false;"/>
					   	&nbsp;&nbsp;&nbsp;
					   	</span>
						<span class="error clear"></span>					   	
					   	to:
					   	<span>
						<input type="text" style='width:78px;' name="createdto" id='createdto' value="${paginationBean.searchCreatedTo}" validate="calender" maxlength="10"/>
						<img title="Created To Date" src="../framework/skins/hhsa/images/calender.png" onclick="NewCssCal('createdto',event,'mmddyyyy');return false;"/>
					   	</span>
					</span>
						<span class="error clear"></span>
				</div>

				<div class='row'>
					<span class='label'>Last Modified from:</span>
					<span class='formfield'>
						<span class='floatLft' >						
						<input startEnd="true" type="text" style='width:78px;' name="modifiedfrom" id='modifiedfrom' value="${paginationBean.searchModifiedFrom}" validate="calender" maxlength="10"/>
					   	<img title="Modified From Date" src="../framework/skins/hhsa/images/calender.png" onclick="NewCssCal('modifiedfrom',event,'mmddyyyy');return false;"/>
					   	&nbsp;&nbsp;&nbsp;						   	
					   	</span>
						<span class="error clear"></span>						   	
					   	to:
					   	<span>
						<input type="text" style='width:78px;' name="modifiedto" id='modifiedto' value="${paginationBean.searchModifiedTo}" validate="calender" maxlength="10"/>
						<img title="Modified To Date" src="../framework/skins/hhsa/images/calender.png" onclick="NewCssCal('modifiedto',event,'mmddyyyy');return false;"/>
					   	</span>
					</span>
						<span class="error clear"></span>
				</div>

				<div class='row'>
					<span class='label'>Status:</span>
 					<span >
						<input type="checkbox" style='width:20px;' name="filterStatus" id="activeStatus" value="1" checked="checked">Active
					</span>
					<span >
						<input type="checkbox" style='width:20px;' name="filterStatus" id="inactiveStatus" value="0" checked="checked">Inactive
					</span>
				</div>

			<div class="buttonholder">
				<input type="button" id="clearfilter" value="Clear" class="graybtutton" >
				<input type="button" id="filter"     value="Filter"    class="button" >
			</div>  
	    </div>
<!-- [End] Search Filter pop up  -->
	 </div>


 	<div class="clear"></div>	
<!-- [Start] Main table  -->
		<div class="tabularWrapper programNameDiv" id="programNameTable">
		 	<div class="paginationWrapper">
 				<ul><li>
	 				<c:choose>
	 						<c:when test="${paginationBean.currentPage > 1}">
	 							<a href="javascript:void(0)" onclick="paging(${paginationBean.currentPage}-1)">Previous</a>&nbsp;&nbsp;
	 						</c:when>
	 				</c:choose> 				
					<c:choose>
	 						<c:when test="${paginationBean.startPage > 1}">
	 							<a href="javascript:void(0)" onclick="paging(${paginationBean.startPage}-1)"><Strong> &#60 </Strong></a>&nbsp;&nbsp;
	 						</c:when>
	 				</c:choose>
	 				<c:forEach var="inx" begin="${paginationBean.startPage}" end="${paginationBean.endPage}">
	 					<c:choose>
	 						<c:when test="${paginationBean.currentPage == inx}">
	 							${inx}&nbsp;&nbsp;
	 						</c:when>
	 						<c:otherwise>
	 							<a href="javascript:void(0)" onclick="paging(${inx})">${inx}</a>&nbsp;&nbsp;
	 						</c:otherwise>
	 					</c:choose>
					</c:forEach>
					<c:choose>
	 						<c:when test="${paginationBean.totalPageCount > paginationBean.endPage}">
	 							<a href="javascript:void(0)" onclick="paging(${paginationBean.endPage}+1)"><Strong>&#62</Strong></a>&nbsp;&nbsp;
	 						</c:when>
	 				</c:choose>
	 				<c:choose>
	 						<c:when test="${paginationBean.totalPageCount > paginationBean.currentPage}">
	 							<a href="javascript:void(0)" onclick="paging(${paginationBean.currentPage}+1)">Next</a>&nbsp;&nbsp;
	 						</c:when>
	 				</c:choose>
<!--
 					<a href="javascript:void(0)" onclick="paging('2')">2</a>&nbsp;&nbsp;
 					<a href="javascript:void(0)" onclick="paging('3')">3</a>&nbsp;&nbsp;
 					<a href="javascript:void(0)" onclick="paging('4')">4</a>&nbsp;&nbsp;
 					<a href="javascript:void(0)" onclick="paging('5')">5</a>&nbsp;&nbsp;
 					<a href="javascript:void(0)" onclick="paging('6')">&gt;</a>&nbsp;&nbsp;
 					<a href="javascript:void(0)" onclick="paging('2')">Next</a>&nbsp;&nbsp; -->
 					</li>
 				</ul>
 			</div> 
			<div  class="tabularWrapper procurementList clear" style='min-height:405px !important'>
			<table>
				<tr>
					<th class="heading" align="center" width="10%">
						<b><a id="agency_sort" href="javascript:" title="Agency" class="sort-default" onclick="sortList(1)" >Agency</a></b></th>
					<th class="heading" align="center" width="30%">
						<b><a id="program_name_sort" href="javascript:" title="Program Name"  class="sort-default" onclick="sortList(3)">Program Name</a></b></th>
					<th class="heading" align="center" width="10%">
						<b><a id="created_date_sort" href="javascript:" title="Create Date"  class="sort-default" onclick="sortList(5)">Create Date</a></b></th>
					<th class="heading" align="center" width="10%">
						<b><a id="modified_date_sort"  href="javascript:" title="Last Modified Date"  class="sort-default" onclick="sortList(7)"  >Last Modified Date</a></b></th>
					<th class="heading" align="center" width="9%">
						<b><a  id="status_sort"  href="javascript:" title="Status"  class="sort-default"  onclick="sortList(9)" >Status</a></b></th>
					<th class="heading" align="center" width="9%">
						<b>Action</b></th>
				</tr>
				<c:forEach items="${programNameLst}" var="programItem" varStatus="loopIndex">
					<c:choose>
						<c:when test="${loopIndex.count % 2 == 0}">
					 		<tr>
								<td class="evenRows" align="center" >${programItem.agencyId}
									<input type="hidden" id="programId" name="programId" value="${programItem.programId}">
								</td>
								<td class="evenRows" align="center" >${programItem.programName}</td>
								<td class="evenRows" align="center" >${programItem.createdDate}</td>
								<td class="evenRows" align="center" >${programItem.modifiedDate}</td>
								<td class="evenRows" align="center" >
									<c:choose>
										<c:when test="${programItem.activeFlag == 1}">Active</c:when>
										<c:otherwise>Inactive</c:otherwise>
									</c:choose>
								</td>
								<td class="evenRows" align="center" >
									<c:choose>
										<c:when test="${programItem.activeFlag == 1 }">
											<select name="action${loopIndex.count}" class="contractAmend" id="action${loopIndex.count}" style="width: 150px" 
											onchange="actionFunction('${programItem.programId}','${programItem.agencyId}', this)">
												<option title="I need to..." value="" to...="">I need to...</option>
												<c:choose>
													<c:when test="${programItem.ref_cnt < 1}">
														<option title="Inactivate" value="Inactivate">Inactivate Program</option>
													</c:when>
												</c:choose>
												<option title="Name Change" value="Modify_Name">Modify Name</option>
											</select>
										</c:when>
										<c:otherwise>
											<select name="action${loopIndex.count}" class="contractAmend" id="action${loopIndex.count}" style="width: 150px" 
											onchange="actionFunction('${programItem.programId}','${programItem.agencyId}',  this)">
												<option title="I need to..." value="" need="" to...="">I need to...</option>
												<option title="Activate" value="Activate">Activate Program</option>
											</select>
										</c:otherwise>
									</c:choose>
<%--
									<a href='#' onclick='inactivateProgram("${programItem.programId}")'>remove</a>								
 									<input type="button" value="Update Program" id="updateProgram" onclick="updateProgram('${programItem.programId}')" class="add marginReset">
									<input type="button" value="Inactivate" id="addNewProgram" onclick="inactivate('${programItem.programId}')" class="remove marginReset">
--%>							</td>
							</tr>
						</c:when>
						<c:otherwise>
					 		<tr >
								<td class="oddRows" align="center" >${programItem.agencyId}
									<input type="hidden" id="programId" name="programId" value="${programItem.programId}">
								</td>
								<td class="oddRows" align="center" >${programItem.programName}</td>
								<td class="oddRows" align="center" >${programItem.createdDate}</td>
								<td class="oddRows" align="center" >${programItem.modifiedDate}</td>
								<td class="oddRows" align="center" >
									<c:choose>
										<c:when test="${programItem.activeFlag == 1}">Active</c:when>
										<c:otherwise>Inactive</c:otherwise>
									</c:choose>
								</td>
								<td class="oddRows" align="center" >
									<c:choose>
										<c:when test="${programItem.activeFlag == 1}">
											<select name="action${loopIndex.count}" class="contractAmend" id="action${loopIndex.count}" style="width: 150px" 
											onchange="actionFunction('${programItem.programId}','${programItem.agencyId}',  this)">
												<option title="I need to..." value="" need="" to...="">I need to...</option>
												<c:choose>
													<c:when test="${programItem.ref_cnt < 1}">
														<option title="Inactivate" value="Inactivate">Inactivate Program</option>
													</c:when>
												</c:choose>
												<option title="Name Change" value="Modify_Name">Modify Name</option>
											</select>
										</c:when>
										<c:otherwise>
											<select name="action${loopIndex.count}" class="contractAmend" id="action${loopIndex.count}" style="width: 150px" 
											onchange="actionFunction('${programItem.programId}', '${programItem.agencyId}', this)">
												<option title="I need to..." value="" need="" to...="">I need to...</option>
												<option title="Activate" value="Activate">Activate Program</option>
											</select>
										</c:otherwise>
									</c:choose>
								</td>
							</tr>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</table>
			</div>

			<c:if test="${programNameLst eq null or fn:length(programNameLst) eq 0}">
				<div class="messagedivNycMsg noRecord" id="messagedivNycMsg">No Data found.</div>
			</c:if>
			
			<div class="paginationWrapperBottom">
 				<ul><li>
	 				<c:choose>
	 						<c:when test="${paginationBean.currentPage > 1}">
	 							<a href="javascript:void(0)" onclick="paging(${paginationBean.currentPage}-1)">Previous</a>&nbsp;&nbsp;
	 						</c:when>
	 				</c:choose> 				
					<c:choose>
	 						<c:when test="${paginationBean.startPage > 1}">
	 							<a href="javascript:void(0)" onclick="paging(${paginationBean.startPage}-1)"><Strong> &#60 </Strong></a>&nbsp;&nbsp;
	 						</c:when>
	 				</c:choose>
	 				<c:forEach var="inx" begin="${paginationBean.startPage}" end="${paginationBean.endPage}">
	 					<c:choose>
	 						<c:when test="${paginationBean.currentPage == inx}">
	 							${inx}&nbsp;&nbsp;
	 						</c:when>
	 						<c:otherwise>
	 							<a href="javascript:void(0)" onclick="paging(${inx})">${inx}</a>&nbsp;&nbsp;
	 						</c:otherwise>
	 					</c:choose>
					</c:forEach>
					<c:choose>
	 						<c:when test="${paginationBean.totalPageCount > paginationBean.endPage}">
	 							<a href="javascript:void(0)" onclick="paging(${paginationBean.endPage}+1)"><Strong>&#62</Strong></a>&nbsp;&nbsp;
	 						</c:when>
	 				</c:choose>
	 				<c:choose>
	 						<c:when test="${paginationBean.totalPageCount > paginationBean.currentPage}">
	 							<a href="javascript:void(0)" onclick="paging(${paginationBean.currentPage}+1)">Next</a>&nbsp;&nbsp;
	 						</c:when>
	 				</c:choose>
			
		</div>

<!--
 					<a href="javascript:void(0)" onclick="paging('2')">2</a>&nbsp;&nbsp;
 					<a href="javascript:void(0)" onclick="paging('3')">3</a>&nbsp;&nbsp;
 					<a href="javascript:void(0)" onclick="paging('4')">4</a>&nbsp;&nbsp;
 					<a href="javascript:void(0)" onclick="paging('5')">5</a>&nbsp;&nbsp;
 					<a href="javascript:void(0)" onclick="paging('6')">&gt;</a>&nbsp;&nbsp;
 					<a href="javascript:void(0)" onclick="paging('2')">Next</a>&nbsp;&nbsp; -->
 					</li>
 				</ul>
 			</div> 					
<!-- [End] Main table  -->
		
<div>&nbsp;</div>	
</form:form>
<!-- [Start] Add New Program pop up  -->
		<!-- Overlay Divs Start -->
			<div class="overlay"></div>
			<div class="alert-box">
				<div class="content">
			  		<div id="newTabs" class='wizardTabs'>
						<div class="tabularCustomHead">Add New Program</div> 
						<h2 class='padLft'>Add New Program</h2>
						<div class='hr'></div>
						<ul>
							<li id='newProgramStep1' class="active">Step 1: New Program </li>
							<li id='newProgramStep2' class="last">Step 2: Confirmation</li>
						</ul>
			            <div id="newProgramTab1"></div>
			            <div id="newProgramTab2" ></div>
					</div>
			  	</div>
		  	<a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
 			</div>
<!-- [End] Add New Program pop up  -->

<!-- [Start] Modify Program Name pop up  -->
		<!-- Overlay Divs Start -->
			<div class="alert-box-help">
				<div class="content">
			  		<div id="changeNameTabs" class='wizardTabs'>
						<div class="tabularCustomHead">Modify Program Name</div> 
						<h2 class='padLft'>Modify Program Name</h2>
						<div class='hr'></div>
						<ul>
							<li id='changeNameStep1' class="active">Step 1: Name Change </li>
							<li id='changeNameStep2' class="last">Step 2: Confirmation</li>
						</ul>
			            <div id="changeNameTab1"></div>
			            <div id="changeNameTab2" ></div>
					</div>
			  	</div>
		  	<a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
 			</div> 
<!-- [End] Modify Program Name pop up  -->

<!-- [Start] Inactivate Program Name pop up  -->
		<!-- Overlay Divs Start -->
			<div class="alert-box-delete">
				<div class="content">
			  		<div id="inactivateProgramTabs" class='wizardTabs'>
						<div class="tabularCustomHead">Inactivate Program</div> 
						<h2 class='padLft'>Inactivate Program</h2>
						<div class='hr'></div>
						<ul>
							<li id='inactivateProgramStep1' class="active">Step 1: Confirmation</li>
						</ul>
			            <div id="inactivateProgramTab1"></div>
					</div>
			  	</div>
		  	<a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
 			</div> 
<!-- [End] Inactivate Program Name pop up  -->

<!-- [Start] Activate Program Name pop up  -->
		<!-- Overlay Divs Start -->
			<div class="alert-box-contact">
				<div class="content">
			  		<div id="activateProgramTabs" class='wizardTabs'>
						<div class="tabularCustomHead">Activate Program</div> 
						<h2 class='padLft'>Activate Program</h2>
						<div class='hr'></div>
						<ul>
							<li id='activateProgramStep1' class="active">Step 1: Confirmation</li>
						</ul>
			            <div id="activateProgramTab1"></div>
					</div>
			  	</div>
		  	<a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
 			</div> 
<!-- [End] Activate Program Name pop up  -->

<%-- Overlay starts --%> 

<div id="overlayedJSPContent" style="display:none"></div>

<%-- Code updated for R4 Ends --%>
