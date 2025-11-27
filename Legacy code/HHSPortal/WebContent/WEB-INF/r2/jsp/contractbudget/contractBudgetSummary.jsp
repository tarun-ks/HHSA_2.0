<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page import="javax.portlet.PortletContext"%>
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="rule" uri="/WEB-INF/tld/rule-taglib.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="nav" uri="/WEB-INF/tld/navigationSM.tld"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<fmt:setBundle basename="com/nyc/hhs/properties/messages"/>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/contractBudgets.js"></script>

<script type="text/javascript">
//Release 3.6.0 Enhancement id 6484 
var maxSiteId = -1;
var keySeparator = "k3yv@lu3S3p@r@t0r";
var submitClicked = false;
// executes on page load
$(document).ready(function() {
	$(".budgetSummary #summaryAmount${subBudgetId}").each(function(e) {
		$(this).jqGridCurrency();
	});
	
	//Release 3.6.0 Enhancement id 6484 
	//closes site overlay
	$(".exit-panel-add-site, #cancelOverlay").click(function(){
		resetOverlay();
		$(".overlay").closeOverlay();
	});
	
	
	// client side validation on site overlay
	//Release 3.6.0 Enhancement id 6484 
	$("#addEditSiteForm").validate({
		rules: {
			siteNameOverlay:{required: true,
				maxlength:90,
				allowSpecialChar: ["A", " !@\\\#\\\$%^\\\&*()-_+=|\\\\{}\\\[\\\];:\\\"\\\'<>,.?\\\/`~\xA7"]},
			address1Overlay:{required: true,
				maxlength:60,
				allowSpecialChar: ["A", " \\\#\\\"\\\',.-"]},
			address2Overlay:{maxlength:60,
				allowSpecialChar: ["A", " \\\#\\\"\\\',.-"]},
			cityOverlay:{required: true,
				maxlength:40,
				allowSpecialChar: ["A", " \\\"\\\',.-"]},
			stateOverlay:{noneSelected: true},
			zipcodeOverlay:{required: true,
				maxlength:5,
				allowSpecialChar: ["N", ""]}
		},
		messages: {
			siteNameOverlay:{required: "! This field is required",
				maxlength: "! Input should be less then 90 characters",
				allowSpecialChar: "! Please enter valid text"},
			address1Overlay:{required: "! This field is required",
				maxlength: "! Input should be less then 60 characters",
				allowSpecialChar: "! Please enter valid text"},
			address2Overlay:{maxlength: "! Input should be less then 60 characters",
				allowSpecialChar: "! Please enter valid text"},
			cityOverlay:{required: "! This field is required",
				maxlength: "! Input should be less then 40 characters",
				allowSpecialChar: "! Please enter valid text"},
			stateOverlay:{noneSelected: "! This field is required"},
			zipcodeOverlay:{required: "! This field is required",
				maxlength: "! Input should be less then 5 characters",
				allowSpecialChar: "! Only numeric text allowed"}
		},
		submitHandler: function(form){
			pageGreyOut();
			jQuery.ajax({
			      type : "POST",
			      url : $("#contextPathSession").val() + "/AddressValidationServlet.jsp?" +
			      		"address1="+escape($("#address1Overlay").val())+
			      		"&city="+escape($("#cityOverlay").val())+
			      		"&state="+escape($("#stateOverlay").val())+
			      		"&zipcode="+escape($("#zipcodeOverlay").val()),
			      data : "",
			      success : function(e) {
			      	removePageGreyOut();
			      	$("#addressDiv").empty();
	                $("#addressDiv").html(e);
	                if(e.toString().indexOf("byPassValidation")>-1){
						 var selectedRadio = $(".rdoBtn:checked")
							.parent().parent();
						  var valueToSet = returnSpace(selectedRadio.find("input[type='hidden'][name='StatusDescriptionText']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='StatusReason']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='StreetNumberText']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='newAddress']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='newCity']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='newState']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='newZip']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='CongressionalDistrictName']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='Latitude']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='Longitude']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='XCoordinate']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='YCoordinate']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='CommunityDistrict']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='CivilCourtDistrict']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='SchoolDistrictName']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='HealthArea']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='BuildingIdNumber']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='TaxBlock']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='TaxLot']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='SenatorialDistrict']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='AssemblyDistrict']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='CouncilDistrict']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='LowEndStreetNumber']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='HighEndStreetNumber']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='LowEndStreetName']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='HighEndStreetName']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='NYCBorough']").val());
						  $("#addressRelatedData").val(valueToSet);
						  submitForm();
					}else {
						$(".overlay").closeOverlay();
		                 $(".overlay").launchOverlayNoClose($(".alert-box-address"));
		                 $(".alert-box-address").find('#selectaddress').click(function() {
	                       var selectedRadio = $(".rdoBtn:checked").parent().parent();
	                       $("#address1Overlay").val(selectedRadio.find("input[type='hidden'][name='newAddress']").val());
	                       $("#cityOverlay").val(selectedRadio.find("input[type='hidden'][name='newCity']").val());
	                       $("#zipcodeOverlay").val(selectedRadio.find("input[type='hidden'][name='newZip']").val());
	                       $("#stateOverlay>option[value='" + selectedRadio.find("input[type='hidden'][name='newState']").val() + "']").attr('selected', 'selected');
	                       var valueToSet = returnSpace(selectedRadio.find("input[type='hidden'][name='StatusDescriptionText']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='StatusReason']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='StreetNumberText']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='newAddress']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='newCity']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='newState']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='newZip']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='CongressionalDistrictName']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='Latitude']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='Longitude']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='XCoordinate']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='YCoordinate']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='CommunityDistrict']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='CivilCourtDistrict']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='SchoolDistrictName']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='HealthArea']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='BuildingIdNumber']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='TaxBlock']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='TaxLot']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='SenatorialDistrict']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='AssemblyDistrict']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='CouncilDistrict']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='LowEndStreetNumber']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='HighEndStreetNumber']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='LowEndStreetName']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='HighEndStreetName']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='NYCBorough']").val());
	                       $("#addressRelatedData").val(valueToSet);
	                       $(".overlay").closeOverlay();
	                       submitForm();
	                       return false;
		                 });
						$("#canceladdrvalidation, .address-exit-panel").unbind("click").click(function() {
							$(".overlay").closeOverlay();
							$(".overlay").launchOverlayNoClose($(".alertBoxAddSite"), "540px");
							return false;
						});
					}
			      }
			});
		}
	});
	

	//Submits the overlay form(client end only no server side hit)
	function submitForm(){
		setValuesForSite();
		$(".overlay").closeOverlay();
		resetOverlay();
	}

	//functions sets the value in hidden fields for site values
	function setValuesForSite(){
		var subBudgetId = $("#subBudgetIdForSite").val();
		var hdnTabId = $("#hdnTabIdForSite").val();
		var siteNameOverlay = $("#siteNameOverlay").val();
		var address1Overlay = $("#address1Overlay").val();
		var address2Overlay = $("#address2Overlay").val();
		var cityOverlay = $("#cityOverlay").val();
		var stateOverlay = $("#stateOverlay>option:selected").val();
		var zipcodeOverlay = $("#zipcodeOverlay").val();
		var addressRelatedData = $("#addressRelatedData").val();
		var indexOpened = $("#indexOpened").val();
		var actionTaken = "";
		if(indexOpened == "new"){
			actionTaken ="insert";
			maxSiteId++;
			var tableRow = '<tr id="trId'+subBudgetId+maxSiteId+'"><td>'
							+siteNameOverlay+'</td><td>'
							+address1Overlay+'</td><td>'
							+address2Overlay+'</td><td>'
							+cityOverlay+'</td><td>'
							+stateOverlay+'</td><td>'
							+zipcodeOverlay+'</td><td>'
							+'<select id="action'+maxSiteId+'" class="siteAction'+subBudgetId+maxSiteId+'"><option value="0">I need to... </option><option value="1">Edit Site</option><option value="2">Delete Site</option></select>'
							+'<input type="hidden" name="siteDetailsList['+maxSiteId+'].siteName" value="'+siteNameOverlay+'"/>'
							+'<input type="hidden" name="siteDetailsList['+maxSiteId+'].address1" value="'+address1Overlay+'"/>'
							+'<input type="hidden" name="siteDetailsList['+maxSiteId+'].address2" value="'+address2Overlay+'"/>'
							+'<input type="hidden" name="siteDetailsList['+maxSiteId+'].city" value="'+cityOverlay+'"/>'
							+'<input type="hidden" name="siteDetailsList['+maxSiteId+'].state" value="'+stateOverlay+'"/>'
							+'<input type="hidden" name="siteDetailsList['+maxSiteId+'].zipCode" value="'+zipcodeOverlay+'"/>'
							+'<input type="hidden" name="siteDetailsList['+maxSiteId+'].actionTaken" value="insert"/>'
							+'<input type="hidden" name="siteDetailsList['+maxSiteId+'].subBudgetSiteId"/>'
							+'<input type="hidden" name="siteDetailsList['+maxSiteId+'].subBudgetId" value="'+subBudgetId+'"/>'
							+'<input type="hidden" name="siteDetailsList['+maxSiteId+'].addressRelatedData" value="'+addressRelatedData+'"/></td>';
			$("#siteDetailTable"+subBudgetId).append(tableRow);
			if($("#noSite"+subBudgetId).size() > 0)
				($("#noSite"+subBudgetId)).hide();
			
			//siteAction(subBudgetId);
		}else{
			var $container = $("tr[id='trId"+subBudgetId+indexOpened+"']");
			actionTaken ="update";
			$container.find("td").eq(0).html(siteNameOverlay);
			$container.find("td").eq(1).html(address1Overlay);
			$container.find("td").eq(2).html(address2Overlay);
			$container.find("td").eq(3).html(cityOverlay);
			$container.find("td").eq(4).html(stateOverlay);
			$container.find("td").eq(5).html(zipcodeOverlay);
			$container.find("input[type='hidden'][name$='siteName']").val(siteNameOverlay);
			$container.find("input[type='hidden'][name$='address1']").val(address1Overlay);
			$container.find("input[type='hidden'][name$='address2']").val(address2Overlay);
			$container.find("input[type='hidden'][name$='city']").val(cityOverlay);
			$container.find("input[type='hidden'][name$='state']").val(stateOverlay);
			$container.find("input[type='hidden'][name$='zipCode']").val(zipcodeOverlay);
			$container.find("input[type='hidden'][name$='addressRelatedData']").val(addressRelatedData);
			if($container.find("input[type='hidden'][name$='actionTaken']").val() != "insert")
				$container.find("input[type='hidden'][name$='actionTaken']").val("update");
		}
		
	
		 var v_parameter = '&siteName='+  encodeURIComponent(siteNameOverlay)+
		 '&address1='+ address1Overlay+
		 '&address2='+ address2Overlay+
		 '&city='+ cityOverlay+
		 '&state='+ stateOverlay+
		 '&zipCode='+ zipcodeOverlay+
		 '&addressRelatedData='+ addressRelatedData+
		 '&actionTaken='+ actionTaken+
		 '&subBudgetId='+ subBudgetId;
		 
			var urlAppender = $("#saveSubBudgetDetails").val();
			jQuery.ajax({
				type : "POST",
				url : urlAppender,
				data : v_parameter,
				success : function(e) {
					  var errorArray = e.toString().split(":");
					   if(e.toString().indexOf("pageError")!=-1){
						    $("#errorGlobalMsg").html(errorArray[1]);
						    $("#errorGlobalMsg").show();
						    removePageGreyOut();
						   $(".overlay").closeOverlay();
					   }
					   else{
						   showCBGridTabsJSP('contractBudgetSummary',hdnTabId,subBudgetId); 
					   }
				},
				error : function(data, textStatus, errorThrown) {
					removePageGreyOut();
				}
			});
		
	}

	//resets overlay values
	function resetOverlay(){
		$("#siteNameOverlay").val("");
		$("#address1Overlay").val("");
		$("#address2Overlay").val("");
		$("#cityOverlay").val("");
		$("#stateOverlay option").eq(0).attr("selected", "selected");
		$("#zipcodeOverlay").val("");
		$("label.error").remove();
		$('.resetDropDown').find('option:first').attr('selected', 'selected');
	}

});


//function performing action change task
function siteAction(subBudgetId,element,seqId,tabId)
{
	$("#subBudgetIdForSite").val(subBudgetId);
	$("#hdnTabIdForSite").val(tabId);
	
		var optionSelected =  $(element).find("option:selected").val();
		if(optionSelected == "1"){
			var selectedRow = $("#trId"+subBudgetId+seqId);
			$("#siteNameOverlay").val(selectedRow.find("td").eq(0).html().replace(/&amp;/g, '&'));
			$("#address1Overlay").val(selectedRow.find("td").eq(1).html());
			$("#address2Overlay").val(selectedRow.find("td").eq(2).html());
			$("#cityOverlay").val(selectedRow.find("td").eq(3).html());
			var stateValue = selectedRow.find("td").eq(4).html();
			$("#stateOverlay option[value='"+stateValue+"']").attr("selected", "selected");
			$("#zipcodeOverlay").val(selectedRow.find("td").eq(5).html());
			$("#indexOpened").val(seqId);
			$(".overlay").launchOverlayNoClose($(".alertBoxAddSite"), "540px");
		}else if(optionSelected == "2"){
			deleteSite(subBudgetId,tabId);
		}
}

//functions sets the value in hidden fields for site values
function deleteSite(subBudgetId,tabId){
	 var v_parameter = '&actionTaken='+ "delete"+
	 '&subBudgetId='+ subBudgetId;
	 
		var urlAppender = $("#saveSubBudgetDetails").val();
		jQuery.ajax({
			type : "POST",
			url : urlAppender,
			data : v_parameter,
			success : function(e) { 
				var errorArray = e.toString().split(":");
				if(e.toString().indexOf("pageError")!=-1){
			    $("#errorGlobalMsg").html(errorArray[1]);
			    $("#errorGlobalMsg").show();
			    removePageGreyOut();
			   $(".overlay").closeOverlay();
		   }
		   else{
				showCBGridTabsJSP('contractBudgetSummary',tabId,subBudgetId);
		   }
			},
			error : function(data, textStatus, errorThrown) {
				removePageGreyOut();
			}
		});
	
}
//returns space for blank or nulls
function returnSpace(str){
	var strVal = " ";
	if(str!=null && str!=""){
		strVal=str;
	}
	return strVal;
}

//checks if element is available
function checkIfElementAvailable(element){
	if(element == null || $(element).attr("id") == null)
		return false;
	else
		return true;
}


/*checks if element is enable
New Method in R4*/
function checkIfElementEnable(element){
	if(element == null || $(element).attr('disabled') == undefined || $(element).attr('disabled') != "disabled"){
		return false;
	}
	else{
		return true;
	}
}

//client side validation on base(proposal details)
$("#saveButton, #saveNextButton").click(function(e){
	$("#saveType").val($(this).attr("id"));
	if(!($("#noSite"+subBudgetId)).is(":visible")){
		$("#noSiteMessage").removeClass("failedShow").html("");
		if(!$("#proposalDetailsForm1").valid())
			return false;
	}else{
		$("#noSiteMessage").addClass("failedShow").html("You must enter at least one site address where services will be provided.");
		return false;
	}
	
	if(!($("#noSite"+subBudgetId)).is(":visible")){
		pageGreyOut();
		form.submit();
	}else{
		return false;
	}
});

//Release 3.6.0 Enhancement id 6484 

//function add site button click action(opens blank overlay) 
function addSiteButtonMethod(subBudgetId,hdnTabId){
	$("#indexOpened").val("new");
	$("#subBudgetIdForSite").val(subBudgetId);
	$("#hdnTabIdForSite").val(hdnTabId);
	$(".overlay").launchOverlayNoClose($(".alertBoxAddSite"), "540px");
}
</script>
<style type="text/css">
	objHidden {display:none}
</style>

<portlet:resourceURL var='saveSubBudgetDetails' id='saveSubBudgetDetails'
	escapeXml='false'>
</portlet:resourceURL>
<input type="hidden" name="saveSubBudgetDetails" id="saveSubBudgetDetails"  value="${saveSubBudgetDetails}"/>
<div class="budgetSummary" style='padding:0' >	
<portlet:defineObjects />
<c:set var="changeId" value="${subBudgetId}"></c:set>
<c:set var="idVarTempFetchListInvoiceSummary" value="tabHighlightList${subBudgetId}" scope="session"/>
<input type="hidden" id="hdnTabHighlightList${subBudgetId}" name="hdnTabHighlightList${subBudgetId}" value="${sessionScope[idVarTempFetchListInvoiceSummary]}"/>
<h3>Budget Summary <span class='linkPrint'><a onclick="View('${subBudgetID}')" class='link' title='View Printer Friendly Version'>View Printer Friendly Version</a></span></h3>
<a style="display:none" href= "<portlet:renderURL><portlet:param name='render_action' value='printerView'/><portlet:param name='printerViewSubBudgetId' value="${subBudgetID}"/><portlet:param name='contractId' value="${contractId}"/><portlet:param name='budgetId' value="${budgetId}"/>
<portlet:param name='budgetType' value="${budgetType}"/>
<portlet:param name='fiscalYearId' value="${fiscalYearID}"/>
</portlet:renderURL>"  class='printerViewCB' id="printerViewCB${subBudgetID}"></a>

<p></p>
		<table width="100%" cellspacing='0' cellpadding='0'>				
				<tr>
					<th colspan='2' class='alignCenter'>Line Item</th>
					<th class='alignCenter'>FY Budget</th>
					<th class='alignCenter'>YTD Invoiced Amount</th>
					<th class='alignCenter'>Remaining Amount</th>
				</tr>
				<tr>
					<td width='3%' class='togglePlaceholder' id="togglerMain${changeId}" onclick="showme('taggingMain${changeId}', this.id);"><span>+</span></td>
					<td width='44%' class='bold'>Total City Funded Budget</td>
					<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalCityFundedBudget.approvedBudget}</label></label></td>
					<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalCityFundedBudget.ytdInvoicedAmount}</label></label></td>
					<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalCityFundedBudget.remainingAmount}</label></label></td>
				</tr>
		</table>
			
	
			
			<div id='taggingMain${changeId}' style='display:none'>
			
		<%-- "Total Direct Costs" table Starts --%>			
			<table width="92%" cellspacing='0' cellpadding='0'>				
				<tr>
					<td width='40%' class='noBdr'><h3>Total Direct Costs</h3></td>
					<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalDirectsCosts.approvedBudget}</label></label></td>
					<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalDirectsCosts.ytdInvoicedAmount}</label></label></td>
					<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalDirectsCosts.remainingAmount}</label></label></td>
				</tr>
			</table>
		<%-- "Total Direct Costs" table Ends Here --%>		
			
			
		<%-- "Total Salary and Fringe" Table Starts --%>
			<table width="92%" cellspacing='0' cellpadding='0'>					
				<tr>
					<td width='3%' class='togglePlaceholder' id="toggler${changeId}" onclick="showme('tagging${changeId}', this.id);"><span>+</span></td>
					<td width='37%' class='bold'>Total Salary and Fringe</td>
					<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalSalaryAndFringesAmount.approvedBudget}</label></label></td>
					<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalSalaryAndFringesAmount.ytdInvoicedAmount}</label></label></td>
					<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalSalaryAndFringesAmount.remainingAmount}</label></label></td>
				</tr>
					<tbody id="tagging${changeId}" style='display:none'>
					<tr >
						<td>&nbsp;</td>
						<td>Total Salary</td>
						<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalSalary.approvedBudget}</label></label></td>
						<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalSalary.ytdInvoicedAmount}</label></label></td>
						<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalSalary.remainingAmount}</label></label></td>
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td>Total Fringe</td>
						<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalFringes.approvedBudget}</label></label></td>
						<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalFringes.ytdInvoicedAmount}</label></label></td>
						<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalFringes.remainingAmount}</label></label></td>	
					</tr>
				</tbody>
			</table>
			<%-- "Total Salary and Fringe" Table Ends Here --%>
			
			
			<%-- "Total OTPS" Table starts --%>
			<table width="92%" cellspacing='0' cellpadding='0' class='summaryWrapper2'>				
					<tr>
						<td width='3%' class='togglePlaceholder' id="toggler2${changeId}" onclick="showme('tagging2${changeId}', this.id);"><span>+</span></td>
						<td width='37%' class='bold'>Total OTPS</td>
						<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalOTPSAmount.approvedBudget}</label></label></td>
						<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalOTPSAmount.ytdInvoicedAmount}</label></label></td>
						<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalOTPSAmount.remainingAmount}</label></label></td>
					</tr>
					<tbody id='tagging2${changeId}' style='display:none'>
						<tr>
							<td>&nbsp;</td>
							<td>Operations, Support and Equipment</td>
							<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.operationsSupportAndEquipmentAmount.approvedBudget}</label></label></td>
							<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.operationsSupportAndEquipmentAmount.ytdInvoicedAmount}</label></label></td>
							<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.operationsSupportAndEquipmentAmount.remainingAmount}</label></label></td>
						</tr>
						<tr>
							<td>&nbsp;</td>
							<td>Utilities</td>
							<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.utilitiesAmount.approvedBudget}</label></label></td>
							<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.utilitiesAmount.ytdInvoicedAmount}</label></label></td>
							<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.utilitiesAmount.remainingAmount}</label></label></td>
						</tr>
						<tr>
							<td>&nbsp;</td>
							<td>Professional Services</td>
							<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.professionalServicesAmount.approvedBudget}</label></label></td>
							<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.professionalServicesAmount.ytdInvoicedAmount}</label></label></td>
							<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.professionalServicesAmount.remainingAmount}</label></label></td>
						</tr>
						<tr>
							<td>&nbsp;</td>
							<td>Rent & Occupancy</td>
							<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.rentAndOccupancyAmount.approvedBudget}</label></label></td>
							<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.rentAndOccupancyAmount.ytdInvoicedAmount}</label></label></td>
							<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.rentAndOccupancyAmount.remainingAmount}</label></label></td>
						</tr>
						<tr>
							<td>&nbsp;</td>
							<td>Contracted Services</td>
							<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.contractedServicesAmount.approvedBudget}</label></label></td>
							<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.contractedServicesAmount.ytdInvoicedAmount}</label></label></td>
							<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.contractedServicesAmount.remainingAmount}</label></label></td>
						</tr>
					</tbody>
			</table>
			<%-- "Total OTPS" Table Ends here --%>
			
			
			<%-- "Total Rate Based" table Starts --%>			
			<table width="92%" cellspacing='0' cellpadding='0' class='summaryWrapper2'>				
				<tr>
					<td width='40%' class='bold'>Total Rate Based</td>
					<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalRateBasedAmount.approvedBudget}</label></label></td>
					<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalRateBasedAmount.ytdInvoicedAmount}</label></label></td>
					<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalRateBasedAmount.remainingAmount}</label></label></td>
				</tr>
			</table>
		<%-- "Total Rate Based" table Ends Here --%>	


		<%-- "Total Milestone Based" table Starts --%>			
			<table width="92%" cellspacing='0' cellpadding='0' class='summaryWrapper2'>				
				<tr>
					<td width='40%' class='bold'>Total Milestone Based</td>
					<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalMilestoneBasedAmount.approvedBudget}</label></label></td>
					<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalMilestoneBasedAmount.ytdInvoicedAmount}</label></label></td>
					<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalMilestoneBasedAmount.remainingAmount}</label></label></td>
				</tr>
			</table>
		<%-- "Total Milestone Based" table Ends Here --%>	


		<%-- "Unallocated Funds" table Starts --%>			
			<table width="92%" cellspacing='0' cellpadding='0' class=''>				
				<tr>
					<td width='40%' class='bold'>Unallocated Funds</td>
					<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.unallocatedFunds.approvedBudget}</label></label></td>
					<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.unallocatedFunds.ytdInvoicedAmount}</label></label></td>
					<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.unallocatedFunds.remainingAmount}</label></label></td>
				</tr>
			</table>
		<%-- "Unallocated Funds" table Ends Here --%>

		<h3 style='margin-left:8%;'>Total Indirect Costs</h3>
		<p style='margin-left:7%;'>
			&nbsp;&nbsp;<b>Indirect Rate - City Funded</b> &nbsp;${asIndirectRate} %
		</p>
		
		<%-- "Total Indirect Costs" table Starts --%>			
			<table width="92%" cellspacing='0' cellpadding='0'>				
				<tr>
					<td width='40%' class='bold'>Total Indirect Costs</td>
					<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalIndirectCosts.approvedBudget}</label></label></td>
					<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalIndirectCosts.ytdInvoicedAmount}</label></label></td>
					<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalIndirectCosts.remainingAmount}</label></label></td>
				</tr>
			</table>
		<%-- "Total Indirect Costs" table Ends Here --%>	
		
			</div>
			
			<hr>
			
			<table width="100%" cellspacing='0' cellpadding='0'>				
				<tr>
					<td width='3%'>&nbsp;</td>
					<td width='45%' class='bold'>Total Program Income
						<div>(Excluded from City Funded Budget; Not Invoiced)</div>
					</td>
					<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalProgramIncome.approvedBudget}</label></label></td>
					<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalProgramIncome.ytdInvoicedAmount}</label></label></td>
					<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalProgramIncome.remainingAmount}</label></label></td>
				</tr>
		    </table>
			
			
			<table width="100%" cellspacing='0' cellpadding='0'>				
				<tr>
					<td width='48%' class='bold'>Total Program Budget  
						<div>(City Funded Budget + Program Income)</div>
					</td>
					<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalProgramBudget.approvedBudget}</label></label></td>
					<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalProgramBudget.ytdInvoicedAmount}</label></label></td>
					<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalProgramBudget.remainingAmount}</label></label></td>
				</tr>
		    </table>
		    
		    
		    <%--Start changes for enhancement id 6484 release 3.6.0 --%>
		   <c:if test="${!recordBeforeRelease and hdnIsPrinterFriendly != 'true'}">
		    <h3>Service Site Information</h3>
			<p style="padding:0px!important;">Please enter an address for each site where your organization proposes to deliver services.</p>
			
			<c:if test="${(lsSubBudgetStatusId eq '84' or lsSubBudgetStatusId eq '83') and (org_type eq 'provider_org')}">
				<div class="buttonholder">
				<c:choose>
				<c:when test="${fn:length(SiteDetailsBean) gt 0}">
				<input type="button" ${readOnlyValue} value="+ Add Site" id='addSiteButton'  disabled="disabled"/>
				</c:when>
				<c:otherwise>
				<input type="button" ${readOnlyValue} value="+ Add Site" id='addSiteButton' onclick="addSiteButtonMethod(${subBudgetId},'${hdnTabId}')" />
				</c:otherwise>
				</c:choose>
					
				</div>
			</c:if>
			
			<div class="tabularWrapper">
					<table width="100%" cellspacing='0' cellpadding='0' border="1" id="siteDetailTable${subBudgetId}">
						<tr class="tableRow${subBudgetId}">
							<th>Site Name</th>
							<th>Address 1</th>
							<th>Address 2</th>
							<th>City</th>
							<th>State</th>
							<th>Zip Code</th>
							<c:if test="${(lsSubBudgetStatusId eq '84' or lsSubBudgetStatusId eq '83') and (org_type eq 'provider_org')}">
								<th>Action</th>
							</c:if>
						</tr>
						<c:choose>
							<c:when test="${empty SiteDetailsBean}">
								<tr id="noSite${subBudgetId}">
									<td colspan="7">No sites have been entered...</td>
								</tr>
							</c:when>
							<c:otherwise>
								<c:forEach var="siteIterator" items="${SiteDetailsBean}" varStatus="item">
									<tr id="trId${subBudgetId}${item.index}" class="trClass${subBudgetId}${item.index}">
										<td class="siteName">${siteIterator.siteName}</td>
										<td class="address1">${siteIterator.address1}</td>
										<td class="address2">${siteIterator.address2}</td>
										<td class="city">${siteIterator.city}</td>
										<td class="state">${siteIterator.state}</td>
										<td class="zipCode">${siteIterator.zipCode}</td>
										<input type="hidden" class="actionTaken" />
										
										<c:if test="${(lsSubBudgetStatusId eq '84' or lsSubBudgetStatusId eq '83') and (org_type eq 'provider_org')}">
											<td>
												<select id="action${subBudgetId}${item.index}" class="siteAction${subBudgetId}${item.index} resetDropDown" onchange="siteAction(${subBudgetId},this,${item.index},'${hdnTabId}')">
													<option value="0">I need to... </option>
													<option value="1">Edit Site</option>
													<option value="2">Delete Site</option>
												</select>
												
											</td>
									</tr>
									</c:if>
								</c:forEach>
							</c:otherwise>
						</c:choose>
					</table>
				</div>
				
			</c:if>	
	</div>

	

		    
		    <%-- End changes for enhancement id 6484 release 3.6.0 --%>
		
