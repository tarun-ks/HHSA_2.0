<%-- This JSp is added in R7 for Cost Center.It is used for the content showing under the Contract Budgets Tab while Contract Configuration --%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<link rel="stylesheet" href="../css/style.css" type="text/css"></link> 
<style type="text/css">
input[disabled] {
    background-color: #d2d2d2 !important;
    color: #999 !important;
    border: 2px solid #d2d2d3 !important;
    cursor: default !important;
    }
    div[disabled], .disabledmultiselect{
    background-color: #d2d2d2 !important;
    color: #999 !important;
    cursor: default !important;
    }
            .multiselect{
                width:100%;
                height:92%;
                overflow: scroll;
                background-color:aliceblue;
                
            }
            .multiselect li {
               
                padding-left: 0em; 
                text-indent: -2em;
            }
            .multiselect ul{
                list-style-type: none;
                white-space: nowrap ;
            }
           .disabledmultiselect{
                background-color: rgba(204, 204, 204,1);
            }
        
</style>


<script type="text/javascript">

var addServiceArray = [];
var removeServiceArray = [];
var previousElmentClicked = -1;
var lastElementClicked = -1;
var previousEnableElmentClicked = -1;
var backgroudColor = "aliceblue";
var contractType = '${ContractType}';
//Below function will generate outer HTMl for new element
getNewRowHTML = function(elementId, elementText){
    return "<li id="+elementId+"><span>"+elementText+"</span></li>"
}

// below function will move element from one list to another
moveIds = function(fromList, toList){
    $("#"+fromList).find( "li" ).each(function (){

        if( $(this).find('span').css("background-color") == "rgb(0, 140, 255)"){
            var newRow = getNewRowHTML($(this).attr("id"),$(this).text());
            $("#"+toList+" ul").append(newRow);
            $(this).remove();
            if(toList == "enabledService" || toList == "updatedServices" ){
            	formDataChange = true;
                addServiceArray.push($(this).attr("id"));
                if(removeServiceArray.length >0 && $.inArray($(this).attr("id"), removeServiceArray) > -1)
                      removeServiceArray.splice($.inArray($(this).attr("id"), removeServiceArray),1);
                
            }else{
            	formDataChange = true;
                removeServiceArray.push($(this).attr("id"));
                if(addServiceArray.length >0 && $.inArray($(this).attr("id"), addServiceArray) > -1)
                    addServiceArray.splice($.inArray($(this).attr("id"), addServiceArray),1);
                
            }
            $("#"+toList+" ul li[id="+$(this).attr("id")+"]").click(function(ev){

            	if(!ev.ctrlKey)
         		   deSelectAllTheElement(toList);
                highlightService($(this));
                if(toList == "enabledService" || toList == "updatedServices"){
                    $("#selectbutton").attr("disabled","true");
                    $("#deselectbutton").removeAttr("disabled");
                  
                }else{
                    $("#deselectbutton").attr("disabled","true");
                    $("#selectbutton").removeAttr("disabled");
                }
                deSelectAllTheElement(fromList);
                var elementSelected = isAnyElementSelected(toList);
                if(!elementSelected){
                    disabledArrowButton();
                }
                if(toList == "enabledService" || toList == "updatedServices"){
                    if(previousEnableElmentClicked == -1 || (ev.shiftKey && previousEnableElmentClicked == -1)){
                        previousEnableElmentClicked = $(this).attr('index');
                    }else if(ev.shiftKey && previousEnableElmentClicked > -1){                      
                            lastElementClicked = $(this).attr('index');                          
                            selectElementWithShiftKey(toList,previousEnableElmentClicked,lastElementClicked );                     
                    }else{
                        previousEnableElmentClicked = $(this).attr('index');
                    }
                }else{
                    if(previousElmentClicked == -1 || (ev.shiftKey && previousElmentClicked == -1)){
                        previousElmentClicked = $(this).attr('index');
                    }else if(ev.shiftKey && previousElmentClicked > -1){                      
                            lastElementClicked = $(this).attr('index');                          
                            selectElementWithShiftKey("disabledService",previousElmentClicked,lastElementClicked );                     
                    }else{
                        previousElmentClicked = $(this).attr('index');
                    }
                }
            });
        }
        previousElmentClicked = -1;
        lastElementClicked = -1;
        previousEnableElmentClicked = -1;
    });
   
    var listForSort = $('#'+toList+" ul");
    var listitems = listForSort.children('li').get();
    listitems.sort(function(a, b) {
        return $(a).text().toUpperCase().localeCompare($(b).text().toUpperCase());
    })
    $.each(listitems, function(idx, itm) { 
    	$(itm).attr('index',idx);
    	listForSort.append(itm); });
    //
     var listForSort = $('#'+fromList+" ul");
    var listitems = listForSort.children('li').get();
    listitems.sort(function(a, b) {
        return $(a).text().toUpperCase().localeCompare($(b).text().toUpperCase());
    })
    $.each(listitems, function(idx, itm) { 
    	$(itm).attr('index',idx);
    	listForSort.append(itm); });
    disabledArrowButton();
}


isAnyElementSelected = function(elemetIdToCheck){
	
    var elementSelected = false;
    $("#"+elemetIdToCheck).find( "li" ).each(function (){
        if( $(this).find('span').css("background-color") == "rgb(0, 140, 255)"){
            elementSelected = true;
            return;
        }
    });
    return elementSelected;
}

deSelectAllTheElement = function(elementIdToReset){
    $("#"+elementIdToReset).find( "li" ).each(function (){
        $(this).find('span').css("background-color",backgroudColor);
        $(this).find('span').css("color","#333");
        
    });
}
highlightService = function (obj){
    if( $(obj).find('span').css("background-color") == "rgb(0, 140, 255)"){
        
        $(obj).find('span').css("background-color",backgroudColor);
        $(obj).find('span').css("color","#333");
    }else{
        $(obj).find('span').css("background-color","rgb(0, 140, 255)");
        $(obj).find('span').css("color","white");
    }
}
disabledArrowButton = function(enableButton, disableButton){

                $("#selectbutton").attr("disabled","true");
                $("#deselectbutton").attr("disabled","true");

            }

selectElementWithShiftKey = function(divId,startIndex, endIndex){
    startIndex = parseInt(startIndex);
    var temp = startIndex;
    endIndex = parseInt(endIndex);
    if(startIndex > endIndex){
       startIndex = endIndex;
       endIndex = temp;
    }
    deSelectAllTheElement(divId);
    for(var count=startIndex; count<=endIndex;count++){
        highlightService($("#"+divId+" ul li[index="+count+"]"));
    }
}
 $(document)
.ready(function() {
// R7 Cost center
                $("#disabledService ul li").click(function(ev){
                   //in case of disabled element blocking the click operation. 
                   if(!$(this).parents('div').hasClass('disabledmultiselect')){
                	   if(!ev.ctrlKey)
                		   deSelectAllTheElement('disabledService');
                        highlightService($(this));
                        $("#deselectbutton").attr("disabled","true");
                        $("#selectbutton").removeAttr("disabled");

                        if(!$('#enabledService').hasClass('disabledmultiselect'))
                        	deSelectAllTheElement('enabledService');
                        if(!$('#updatedServices').hasClass('disabledmultiselect'))
                        	deSelectAllTheElement('updatedServices'); 
                        previousEnableElmentClicked = -1;
                        var elementSelected = isAnyElementSelected('disabledService');
                        if(!elementSelected){
                            disabledArrowButton();
                        }
                        if(previousElmentClicked == -1 || (ev.shiftKey && previousElmentClicked == -1)){
                            previousElmentClicked = $(this).attr('index');
                    }else if(ev.shiftKey && previousElmentClicked > -1){                      
                        
                            lastElementClicked = $(this).attr('index');                          
                            selectElementWithShiftKey("disabledService",previousElmentClicked,lastElementClicked );                     
                    
                    }else{
                            previousElmentClicked = $(this).attr('index');
                    }
                   }
                });

                $("#enabledService ul li").click(function(ev){
                   //in case of disabled element blocking the click operation.
                   if(!$(this).parents('div').hasClass('disabledmultiselect')){
                	   if(!ev.ctrlKey)
                		   deSelectAllTheElement('enabledService');
                        highlightService($(this));
                        $("#selectbutton").attr("disabled","true");
                        $("#deselectbutton").removeAttr("disabled");
                        deSelectAllTheElement('disabledService');
                        previousElmentClicked = -1;
                        if(previousEnableElmentClicked == -1 || (ev.shiftKey && previousEnableElmentClicked == -1)){
                            previousEnableElmentClicked = $(this).attr('index');
                        }else if(ev.shiftKey && previousEnableElmentClicked > -1){                      
                                
                                lastElementClicked = $(this).attr('index');                          
                                selectElementWithShiftKey("enabledService",previousEnableElmentClicked,lastElementClicked );                     
                            
                        }else{
                                previousEnableElmentClicked = $(this).attr('index');
                        }
                   }
                    
                });	

                $("#updatedServices ul li").click(function(ev){
                   //in case of disabled element blocking the click operation.
                    if(!$(this).parents('div').hasClass('disabledmultiselect')){
                    	if(!ev.ctrlKey)
                 		   deSelectAllTheElement('updatedServices');
                        highlightService($(this));
                        $("#selectbutton").attr("disabled","true");
                        $("#deselectbutton").removeAttr("disabled");
                        deSelectAllTheElement('disabledService');
                        previousElmentClicked = -1;
                        if(previousEnableElmentClicked == -1 || (ev.shiftKey && previousEnableElmentClicked == -1)){
                            previousEnableElmentClicked = $(this).attr('index');
                        }else if(ev.shiftKey && previousEnableElmentClicked > -1){                      
                                
                                lastElementClicked = $(this).attr('index');                          
                                selectElementWithShiftKey("enabledService",previousEnableElmentClicked,lastElementClicked );                     
                            
                        }else{
                                previousEnableElmentClicked = $(this).attr('index');
                        }
                   }
               });
               disabledArrowButton();
     //R7 Cost center

	var agencyStatusFlag = '${selectionflag}';
	
	if(agencyStatusFlag != '2'){
	$('#servicesListTab').hide();
	}
	// For cost center enabled agencies, enabling program Income checkbox and setting to disabled
	if(agencyStatusFlag == '2' && (contractType == 1 || contractType == 2)){
		$('#budgetCustomized').find('table:visible').find('td:contains("Program Income")>input').prop('checked', true);
		$('#budgetCustomized').find('table:visible').find('td:contains("Program Income")>input').prop('disabled', true);
		}
	
$('#servCheckbx').change(function showHideButton(){
	if($('#servCheckbx').prop('checked')){
		onServiceCheckBoxClick(2);
		$('#budgetCustomized').find('table:visible').find('td:contains("Program Income")>input').prop('checked', true);
		$('#budgetCustomized').find('table:visible').find('td:contains("Program Income")>input').prop('disabled', true);
		$('#servicesListTab').show();
	}else{
		onServiceCheckBoxClick(1);
		$('#budgetCustomized').find('table:visible').find('td:contains("Program Income")>input').prop('disabled', false);
		$('#servicesListTab').hide();
	}
});
$('select[id*="sharedList"]').css('width','362.5px');
if(contractType == 1){
	$('#userAccessRight').css('height','362px');
    $('#servicesNavigation').css('margin','-27% 45% 0% 42%');
}
else{
 	$('#userAccessRight').css('height','160px');
	$('#saveButton').parent().css('margin','-5% 5% 0% 0%');
	$('#servicesNavigation').css('margin','-27% 44% 0% 42%');
	$('#enabledService').addClass('disabledmultiselect');
//	$('#updatedServicesAmendment').css('height','155x'); 
} 
$('.multiselect[disabled]').addClass('disabledmultiselect');
$('.multiselect[disabled]').css('width','99.5%');
$('.disabledmultiselect').removeAttr("disabled");
$('.disabledmultiselect').css('width','99.5%');
}); 

function saveServices(){
	//console.log("-------saveServices----------"); // qc 9654
	pageGreyOut();
	
	var _budgetYear = _tmpSelectedYear;
	var v_parameter = "selectedServicesList=" + addServiceArray + "&DeleteItemsList="+ removeServiceArray + "&budgetYear=" + _budgetYear;
	var urlAppender = $("#updateSelectedServices").val();
	
	pageGreyOut();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(e) {
			// Start QC 9654 R 9.4
			//console.log("-------e :: "+e); 
			var errorArray = e.split(":");
			if (e.indexOf("pageError") != -1) 
			{
				$("#taskErrorDiv").html(errorArray[1]);
				$("#taskErrorDiv").show();
			}	
			//End QC 9654 R 9.4
			removeServiceArray = [];
			addServiceArray = [];
			removePageGreyOut();
			formDataChange = false;
		},
		error : function(data, textStatus, errorThrown) 
		{   
			removePageGreyOut();
		}
	});
	}
	

 function onServiceCheckBoxClick(Ops) {
	 //console.log("------onServiceCheckBoxClick------"); //qc9654
	 //console.log("------onServiceCheckBoxClick---Ops :: "+Ops); 
	 pageGreyOut();
	var urlAppender = $("#updateCostCenterEnabled").val()
	+ "&OPERATION=" + Ops ;
	
	pageGreyOut();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data :"",          
		success : function(e) {
			// Start QC 9654 R 9.4
			//console.log("-------e :: "+e); 
			var errorArray = e.split(":");
			if (e.indexOf("pageError") != -1) 
			{
				$("#taskErrorDiv").html(errorArray[1]);
				$("#taskErrorDiv").show();
			}	
			//End QC 9654 R 9.4
			removePageGreyOut();
		}
	});
}
</script>
        
        <c:if test="${screenReadOnly eq 'true'}">
	<c:set var="isReadOnlyTask" value="disabled"></c:set>
</c:if>
<!-- Checking if Cost Center is selected in Base Contract -->
<c:set var="isSelected" value="true"></c:set>
<c:if test="${((ContractType eq 4) || (ContractType eq 2) || (checkForNewFY eq 'true')) and (selectionflag ne 2)}">
<c:set var="isSelected" value="false"></c:set>
</c:if>
<!-- Checking if Cost Center enabled for agency -->
<c:set var="isCostCenterChosen" value="true"></c:set>
<c:if test="${selectionflag eq 0}">
<c:set var="isCostCenterChosen" value="false"></c:set>
</c:if>
<c:if test="${(isSelected) and (isCostCenterChosen)}">
<br>
<B id="servicesTemplate">Services</B>
<div>
	Click the checkbox to enable "Services" for this contract. If selected, the "Program Income" budget category will be added by default.<br/>
	 Services functionality cannot be enabled/disabled via Updates, Amendments, or New FY Configurations. <br/>Note: Only enabled services will be visible for this budget.
</div>
<br>
<c:if
	test="${selectionflag eq 2}">
	<c:set var="isChecked" value="checked"></c:set>
</c:if>
<!-- Disabling  Services Checkbox and Enabled Services for Amendment & Update-->
<c:if test="${((ContractType eq 4) || (ContractType eq 2)) and (selectionflag eq 2)}">
	<c:set var="readOnlyValue" value="disabled"></c:set>
</c:if>
<d:content isReadOnly="${screenReadOnly}" >
<c:if test="${checkForNewFY eq 'true'}">
	<c:set var="newFyDisabled" value="disabled"></c:set>
</c:if>
<div id="servicesCustomizeTab2">
<input type="checkbox" id="servCheckbx" name="servCheckbx" ${isChecked} ${readOnlyValue} ${newFyDisabled}/>Enable Services for this contract
</div>

<br>
<div class='clear'>&nbsp;</div>
<div id="servicesListTab">
<div class="userAccessOverlay" id="userAccessleft" style="border:1px solid #ccc;height:362px;position: initial;float: left;margin: 0%;padding: 0%;">
						<p class="addInfouserAccess boldtextDefaultHeader">
							Available Services 
						</p>

        <div class="multiselect" onselectstart="return false;" id="disabledService" ${isReadOnlyTask}>
            <ul style="padding-left:8%;">
            	<c:forEach items="${servicesMap}" var="entry">
								<c:if test="${entry.key eq 'enabledServices'}">
								<% int count=0; %> 
								<c:forEach var="userAccessListVar" items="${entry.value}"
									varStatus="loop">
								<li id="${userAccessListVar.costCenterServiceMappingId}" index="<%= count++ %>"><span>${userAccessListVar.enabledServiceName}</span></li>
				</c:forEach>
				</c:if>
			</c:forEach>
            </ul>
        </div>
</div>
					<div class='clear'>&nbsp;</div>
	<c:if test="${ContractType eq 1}">				
	<div class="muloptionsuserAccess" id="servicesNavigation" style="float: left;margin: -20% 44% 0% 42%;" >
	<input type="button" id="selectbutton" title="Add Service" name="select" value="&gt;"  style="font-weight: bold;width:35px;" 
	onclick="moveIds('disabledService','enabledService');"/><br>
    <input type="button" id="deselectbutton" title="Remove Service" style="font-weight: bold;width:35px;margin: 3% -4% 0% -4%;" 
     name="deselect" value="&lt;" onclick="moveIds('enabledService','disabledService');"/>
	</div>
	</c:if>
	<c:if test="${(ContractType eq 4) || (ContractType eq 2)}">
	<div class="muloptionsuserAccess" id="servicesNavigation" style="float: left;margin: -20% 44% 0% 42%;">
	<input type="button" id="selectbutton" title="Add Service" name="select" value="&gt;"  style="font-weight: bold;width:35px;" 
	onclick="moveIds('disabledService','updatedServices');"/><br>
    <input type="button" id="deselectbutton" title="Remove Service" style="font-weight: bold;width:35px;margin: 3% -4% 0% -4%;" 
     name="deselect" value="&lt;" onclick="moveIds('updatedServices','disabledService');"/>
	</div>
	</c:if>
	
	<div class='clear'>&nbsp;</div>
		<div class="userAccessOverlay" id="userAccessRight" style="border:1px solid #ccc;float: right;margin: -44% 6% 0% 0%;" >
						<p class="addInfouserAccess boldtextDefaultHeader">Enabled Services</p>
				<div class="multiselect" onselectstart="return false;" id="enabledService" ${readOnlyValue} ${isReadOnlyTask}>
					<ul style="padding-left: 8%;">
						<c:forEach items="${servicesMap}" var="entry">
							<c:if test="${entry.key eq 'selectedServices'}">
							<% int count1=0; %> 
								<c:forEach var="userAccessListVar" items="${entry.value}"
									varStatus="loop">
									<li id="${userAccessListVar.costCenterServiceMappingId}" index="<%= count1++ %>"><span>${userAccessListVar.enabledServiceName}</span></li>
								</c:forEach>
							</c:if>
						</c:forEach>
					</ul>
				</div>
			</div>
							
							
					<c:if test="${ContractType eq 2}">
					<c:set var="UpdateListBoxName" value="Amended Services"></c:set>
					</c:if>
					<c:if test="${ContractType eq 4}">
					<c:set var="UpdateListBoxName" value="Updated Services"></c:set>
					</c:if>
					<c:if test="${(ContractType eq 4) || (ContractType eq 2)}">
					<br>
					<div class="userAccessOverlay" id="updatedServicesAmendment" style="border:1px solid #ccc;height:160px;float: right;margin: -24% 6% 0% 0%;">
						<p class="addInfouserAccess boldtextDefaultHeader">
							${UpdateListBoxName}
						</p>
						  <div class="multiselect" onselectstart="return false;" id="updatedServices" ${isReadOnlyTask}>
       					     <ul style="padding-left:8%;">    
								<c:forEach items="${servicesMap}" var="entry">
								<c:if test="${entry.key eq 'dataList'}">
								<% int count2=0; %>
								<c:forEach var="userAccessListVar" items="${entry.value}"
									varStatus="loop">
									<li id="${userAccessListVar.costCenterServiceMappingId}" index="<%= count2++ %>"><span>${userAccessListVar.enabledServiceName}</span></li>
								</c:forEach>
								</c:if>
								</c:forEach>
							</ul>
					</div>
					</c:if>
<br>
<div class='clear'>&nbsp;</div>
<div class="buttonholder"  style="float: right;margin: -2% 5% 0% 0%;">
<c:if test="${screenReadOnly eq null}"> 
<input type="button" id="saveButton" class="graybtutton"
								value="Save Services" onclick="saveServices()" />
</c:if>
</div>
</div>
<br> 
</d:content>
</c:if>
