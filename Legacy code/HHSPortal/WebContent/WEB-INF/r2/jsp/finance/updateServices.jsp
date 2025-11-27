<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%-- Added in R7 for Cost Center.It opens Update Services Overlay screen on click of update Services on budget list --%>
<portlet:defineObjects />
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
            
            if(toList == "updatedServices" ){
            	
                addServiceArray.push($(this).attr("id"));
                if(removeServiceArray.length >0 && $.inArray($(this).attr("id"), removeServiceArray) > -1)
                    removeServiceArray.splice($.inArray($(this).attr("id"), removeServiceArray),1);
                
            }else{
                removeServiceArray.push($(this).attr("id"));
                if(addServiceArray.length >0 && $.inArray($(this).attr("id"), addServiceArray) > -1)
                    addServiceArray.splice($.inArray($(this).attr("id"), addServiceArray),1);
            }
            $("#"+toList+" ul li[id="+$(this).attr("id")+"]").click(function(ev){
            	if(!ev.ctrlKey)
         		   deSelectAllTheElement(toList);
                highlightService($(this));
                if(toList == "updatedServices"){
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
                if(toList == "updatedServices"){
                    if(previousEnableElmentClicked == -1 || (ev.shiftKey && previousEnableElmentClicked == -1)){
                        previousEnableElmentClicked = $(this).attr('index');
                    }else if(ev.shiftKey && previousEnableElmentClicked > -1){                      
                            lastElementClicked = $(this).attr('index');                          
                            selectElementWithShiftKey(toList,previousEnableElmentClicked,lastElementClicked );                     
                    }else{
                        previousEnableElmentClicked = $(this).attr('index');
                    }
                }
                else{
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
    //sorting from list
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
// this method will return coma seprated enabled service ids

   disabledArrowButton = function(enableButton, disableButton){
                $("#selectbutton").attr("disabled","true");
                $("#deselectbutton").attr("disabled","true");

            }
breakString = function(noOfChar, text){
    for(var count=0;count<text.length;count++){
    }
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
      $('.multiselect[disabled]').addClass('disabledmultiselect');
      $('.multiselect[disabled]').css('width','99.5%');
      $('.disabledmultiselect').removeAttr("disabled");
      $('.disabledmultiselect').css('width','99.5%');
}); 
function saveServices(){
	pageGreyOut();
	var selectArr = [];
	$('#sharedList1 option').each(function() {
		selectArr.push($(this).val());
	});
	var v_parameter = "selectedServicesList=" + addServiceArray +"&DeleteItemsList=";
	var urlAppender = $("#updateSelectedServices").val();

	pageGreyOut();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(e) {
			removePageGreyOut();
			$(".overlay").closeOverlay();
			window.location.href = $("#duplicateRender").val(); 
		},
		error : function(data, textStatus, errorThrown) {
			removePageGreyOut();
			$(".overlay").closeOverlay();
		}
	});
	}
</script>
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
                height:94%;
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
               background-color: #d2d2d2 !important;
            }
        
</style>
<div class="content" style="width:735px;">
	<div class='tabularCustomHead'><span id="contractTypeId">Update Services
		</span> <a href="javascript:void(0);" class="exit-panel">&nbsp;</a></div>
<table>
<tr>
<div class="tabularContainer" style="width:730px !important;border:0px !important">

	
		<div class="failed" id="errorMsg"></div>
		<p style="width:720px !important;">To update services, please select the services to be added and then click the &apos;Save&apos; button. 
		Please note that services cannot be removed once they are added. Only enabled services will be visible for this budget.</p>
		<br/>
		<div class="userAccessOverlay" id="userAccessleft" style="border:1px solid #ccc;height:430px;margin:0px 25px 0px 0px;">
						<p class="addInfouserAccess boldtextDefaultHeader">
							Available Services
						</p>
					
		 <div class="multiselect" id="disabledService" onselectstart="return false;">
            <ul style="padding-left:9%;">
            	<c:forEach items="${servicesMap}" var="entry">
								<c:if test="${entry.key eq 'enabledServices'}">
								<% int count1=0; %>
								<c:forEach var="userAccessListVar" items="${entry.value}"
									varStatus="loop">
								<li id="${userAccessListVar.costCenterServiceMappingId}" index="<%= count1++ %>"><span title="${userAccessListVar.enabledServiceName}">${userAccessListVar.enabledServiceName}</span></li>
				</c:forEach>
				</c:if>
			</c:forEach>
            </ul>
        </div>
		</div>
	<div class="muloptionsuserAccess" id="servicesNavigation" style="float: left;margin:150px 0px 0px -28px;">
	<input type="button" id="selectbutton" title="Add Service" name="select" value="&gt;"  style="font-weight: bold;width:35px;margin-top:3px;" 
	onclick="moveIds('disabledService','updatedServices');"/><br>
    <input type="button" id="deselectbutton" title="Remove Service" style="font-weight: bold;width:35px;margin: 20% -4% 0% -4%;" 
     name="deselect" value="&lt;" onclick="moveIds('updatedServices','disabledService');"/>
	</div>
				<%--Disabled services --%>
	<div class="userAccessOverlay" id="userAccessRight" style="float: right;width:310px;height: 190px;margin:0px 26px 0px 0px;">
						<p class="addInfouserAccess boldtextDefaultHeader">
							Enabled Services
						</p>
				<div class="multiselect" id="enabledService" disabled style="background-color: #d2d2d2 !important;"  >
					<ul style="padding-left: 9%;">
						<c:forEach items="${servicesMap}" var="entry">
							<c:if test="${entry.key eq 'selectedServices'}">
								<c:forEach var="userAccessListVar" items="${entry.value}"
									varStatus="loop">
									<li id="${userAccessListVar.costCenterServiceMappingId}"><span title="${userAccessListVar.enabledServiceName}">${userAccessListVar.enabledServiceName}</span></li>
								</c:forEach>
							</c:if>
						</c:forEach>
					</ul>
				</div>
					</div>
					<%--Updated services --%>
					<div class="userAccessOverlay" id="updateServices" style="border:1px solid #ccc;background-color:aliceblue;float: right;width:310px;height: 185px;margin:32px 26px 0px 0px;">
						<p class="addInfouserAccess boldtextDefaultHeader">
							Updated Services
						</p>
						  <div class="multiselect" id="updatedServices" onselectstart="return false;">
       					     <ul style="padding-left:9%;">    
								<c:forEach items="${servicesMap}" var="entry">
								<c:if test="${entry.key eq 'dataList'}">
								<% int count2=0; %>
								<c:forEach var="userAccessListVar" items="${entry.value}"
									varStatus="loop">
									<li id="${userAccessListVar.costCenterServiceMappingId}" index="<%= count2++ %>"><span title="${userAccessListVar.enabledServiceName}">${userAccessListVar.enabledServiceName}</span></li>
								</c:forEach>
								</c:if>
								</c:forEach>
							</ul>
					</div>
					</div>
</div>
</tr>
<tr>
<div class="buttonholder" style="width:710px;padding-top:12px;">
<input type="button" id="saveButton" value="Save" onclick="saveServices()" style="width:60px" />
</div>
</tr>
</table>

</div>
<portlet:resourceURL var="updateServicesPendingSubmission" id="updateServicesPendingSubmission" escapeXml="false">	
	<portlet:param name="contractId" value="${contractId}" />
	<portlet:param name="budgetId" value="${budgetId}" />
	<portlet:param name="fiscalYearId" value="${fiscalYearId}" />
	<portlet:param name="budgetType" value="${budgetType}" />
</portlet:resourceURL>
<input type="hidden" id="updateSelectedServices" value="${updateServicesPendingSubmission}"/>