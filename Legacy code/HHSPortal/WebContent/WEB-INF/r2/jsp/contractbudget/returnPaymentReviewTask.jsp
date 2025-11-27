<%-- The jsp is added in Release 6 for return payment.
This jsp display for agency user for return payment review task --%>
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@ taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@ taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@ page errorPage="/error/errorpage.jsp"%>
<portlet:defineObjects />
<script>
/**
 * Ready function
 */
$(document).ready(function() {
	//Changes for Defect 8588
	$('#checkDate').on("input paste", function (e) {
		var data = "";
		$.each($(this).val().replace(/[^0-9]/g, ''), function(index, value){
			data = data + value;
			if(index == 1 || index == 3){
				data = data + "/";
			}
		});
		$('#checkDate').val(data);
    });
	
	$('#receivedDate').on("input paste", function (e) {
		var data = "";
		$.each($(this).val().replace(/[^0-9]/g, ''), function(index, value){
			data = data + value;
			if(index == 1 || index == 3){
				data = data + "/";
			}
		});
		$('#receivedDate').val(data);
    });
	//Changes for Defect 8588
	var today = new Date();
	var dd = today.getDate();
	var mm = today.getMonth()+1; //January is 0!

	var yyyy = today.getFullYear();
	if(dd<10){
	    dd='0'+dd;
	} 
	if(mm<10){
	    mm='0'+mm;
	} 
	var today = mm +'/' +  dd+'/' +yyyy;
	var readOnlyRet = ${returnedPaymentReadonly};
	if(readOnlyRet){
		$("#receiveDateImg").removeAttr("onclick");
		$("#checkDateImg").removeAttr("onclick");
	}
	$("#returnPaymentForm input[type=text]").on("keyup", function(){
		$('.error').text("");
		} );
	//This is a validator for returnpayment form
	// Changing maxlength value from 4 to 20 for enhancement 8652
	$("#returnPaymentForm").validate({
		rules: {
			checkNumber: {
				required: true,
				maxlength: 20
				},
			checkAmount: {
					required: true
					},
			checkDate: {
					required: true,
					DateRange : new Array("01/01/1800", "12/31/"+(parseInt(new Date().getFullYear())+50))
					},
			receivedDate:{
					required: true,
					calenderRestrictFutureDate : true,
					DateRange : new Array("01/01/1800", today)
						},
			descriptionInput:{
					required: true,
					maxlength : 200
						}
		},
		messages: {
			checkNumber: {
				required: "<fmt:message key='REQUIRED_FIELDS'/>",
				maxlength: "<fmt:message key='INPUT_MAX_4_CHAR'/>"
				    },
			checkAmount: {
				required: "<fmt:message key='REQUIRED_FIELDS'/>"
			},
			checkDate: {
				required: "<fmt:message key='REQUIRED_FIELDS'/>",
				DateRange: "<fmt:message key='REQUIRED_VALID_DATE2'/>"
			},
			receivedDate:{
				required: "<fmt:message key='REQUIRED_FIELDS'/>",
				calenderRestrictFutureDate: "<fmt:message key='FUTURE_DATE_RESTRICTION'/>",
					DateRange: "<fmt:message key='REQUIRED_VALID_DATE2'/>"
				},
			descriptionInput:{
				required: "<fmt:message key='REQUIRED_FIELDS'/>",
				maxlength: "!max length "
				}
		},
		submitHandler: function(form){
		var returnId = "${aoHashMap.returnPaymentDetailId}";
		document.returnPaymentForm.action = $("#saveDetailsUrl").val()+"&returnPaymentDetailId="+returnId;
		pageGreyOut();
		$(document.returnPaymentForm).ajaxSubmit(optionsReturnForm);
		},
		errorPlacement: function(error, element) {
			error.appendTo(element.parents("div.row").find("span.error"));
			
		}
	});
	
	var optionsReturnForm = 
	{
		success: function(responseText) 
		{ 
			if(responseText.error!=1)
			{
				resetFlag();
		        removePageGreyOut();
			}
			else{
				removePageGreyOut();
		    	  var message = responseText.message;
		          $(".messagedivforreturnpayment").html(message+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivforreturnpayment', this)\" />");
		          $(".messagedivforreturnpayment").removeClass("passed");
		          $(".messagedivforreturnpayment").addClass("failed");
		          $(".messagedivforreturnpayment").show();
			}
		},
		  error : function(data, textStatus, errorThrown) {
			  removePageGreyOut();
	    	  var message= "This request could not be completed. Please try again in a few minutes.";
	          $(".messagedivforreturnpayment").html(message+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivforreturnpayment', this)\" />");
	          $(".messagedivforreturnpayment").removeClass("passed");
	          $(".messagedivforreturnpayment").addClass("failed");
	          $(".messagedivforreturnpayment").show();
	      }
	};
	
 	$('#checkAmount').autoNumeric({
		vMax : '999999999999.99'
	});
	$('#checkAmount').validateCurrencyOnLoad();
	
	$('#checkNumber').validateOnlyNumber();
});
var returnedPaymentId = "${aoHashMap.returnPaymentDetailId}";
</script>

<portlet:resourceURL var='showCBGridTabs' id='showCBGridTabs' escapeXml='false'> </portlet:resourceURL>

<portlet:resourceURL var="submitContractBudgetOverlay" id="submitContractBudgetOverlay" escapeXml="false">
</portlet:resourceURL>

<input type="hidden" name="submitContractBudgetOverlay" id="submitContractBudgetOverlay" value="${submitContractBudgetOverlay}"/>

<portlet:resourceURL var='getCallBackContractBudgetData' id='getCallBackContractBudgetData' escapeXml='false'>
</portlet:resourceURL>

<portlet:resourceURL var="saveReturnPaymentDetails" id ="saveReturnPaymentDetails" escapeXml="false">
	</portlet:resourceURL>

<portlet:resourceURL var="getReassignListFinance" id="getReassignListFinanceInbox"/>
<input type="hidden" id="getReassignListFinanceHidden" value="${getReassignListFinance}" />
<input type = 'hidden' value='${getCallBackContractBudgetData}' id='getCallBackContractBudgetData'/>
<fmt:setBundle basename="com/nyc/hhs/properties/messages"/>
<input type='hidden' value='${showCBGridTabs}' id='hiddenCBGridTagURL' />
<c:set var="readOnlyPageAttribute" value="true"></c:set>
<input type="hidden" id="returnedPaymentReadonly" value="${returnedPaymentReadonly}" />
<input type="hidden" id="returnedPaymentId" value="${aoHashMap.returnPaymentDetailId}" />

<!-- Added below styles -->
<style type="text/css">
.alert-box-upload, .alert-box-delete, .alert-box-viewDocumentProperties, .alert-box-addDocumentFromVault {
	position:fixed !important;
	top:15% !important
}

.accContainer .ui-widget-content{
	background: none
}
.accContainer .ui-jqgrid .ui-jqgrid-pager .ui-pg-div span.ui-icon {
    margin: 0  2px !important;
}

.formcontainer .row span.error{
	float: right;
	padding: 0px 0;
	text-align: left; 
	color:#D63301
}
.accContainer .formcontainer span.label {
    width: 28% !important;
}
.saveButtonClass{
margin-bottom: -30px;
    position: relative;
    top: -25px;
    }
</style>
<%@include file="/WEB-INF/r5/jsp/assigneeListMapping.jsp" %>
<portlet:resourceURL var="setDefaultUser" id="setDefaultUser"></portlet:resourceURL>
<input type="hidden" id="setDefaultUser" value="${setDefaultUser}"/>
<link rel="stylesheet" media="screen" href="${pageContext.servletContext.contextPath}/r2/css/ui.jqgrid.css" type="text/css"></link>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/grid.locale-en.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.formatCurrency-1.4.0.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/accordianFunctions.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/returnedPaymentTask.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jqgridDialogOveride.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/unsaveData.js"></script>
<script type="text/javascript" src="../resources/js/autoNumeric-1.7.5.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/defaultAssignee.js"></script>
<!-- Main Div area starts-->
<div class='complianceWrapper'>
<div class='accContainer floatNone'>
	<c:set var="sectionName"><%=HHSComponentMappingConstant.AGENCY_S396_PAGE%></c:set>
	<d:content section="${sectionName}" authorize="" isReadOnly="${accessScreenEnable eq false}">
	<task:taskContent workFlowId="" showDocument="" taskType="taskReturnedPaymentReview" isTaskScreen="" level="header" taskDetail="" ></task:taskContent>
<div class='hr'></div>
	<% String lsErrorMessage = "";
		if(null != request.getAttribute("errorMessage")){
			lsErrorMessage = (String) request.getAttribute("errorMessage");
		%>
		<input type="text"/>
			<div class="failed breakAll" style="display:block" id="error"><%=lsErrorMessage%></div>			
	<%} %>	
<div class='clear'></div>
	<%-- Fiscal Year Budget Information Starts --%>
	<h3>Fiscal Year Budget Information</h3>
	<br>
	<div class='tabularWrapper'>
	<table cellspacing="0" cellpadding="0" class="grid">
		<tr>
			<th>Start Date</th>
			<th>End Date</th>
			<th>FY Budget</th>
			<th title="Total of Submitted and Approved Invoices">YTD Invoiced Amount</th>
			<th title="FY Budget - YTD Invoiced Amount">Remaining Amount</th>
			<th title="Total of all Paid Invoices">YTD Actual Paid Amount</th>
			<th 
				title="(Advances Disbursed - Advance Recoupment Amounts). The Advance Recoupment Amount accounts for recoupments added to invoices in Pending Approval and Approved status. The amount may change as the Agency completes invoice reviews">Unrecouped Advance Amount</th>
		</tr>
		<tr>
			<td><fmt:formatDate pattern="MM/dd/yyyy" value="${fiscalBudgetInfo.startDate}" /></td>
			<td><fmt:formatDate pattern="MM/dd/yyyy" value="${fiscalBudgetInfo.endDate}" /></td>
			<td><fmt:formatNumber type="currency" value="${fiscalBudgetInfo.approvedBudget}" /></td>
			<td><fmt:formatNumber type="currency" value="${fiscalBudgetInfo.invoicedAmount}" /></td>
			<td><fmt:formatNumber type="currency" value="${fiscalBudgetInfo.remainingAmount}" /></td>
			<td><fmt:formatNumber type="currency" value="${fiscalBudgetInfo.ytdActualPaid}" /></td>
			<td><fmt:formatNumber type="currency" value="${fiscalBudgetInfo.unRecoupedAmount}" /></td>
		</tr>
	</table>
	</div>
	<p class='clear'>&nbsp;</p>
	<%-- Form Data Starts --%>
	<d:content isReadOnly="${returnedPaymentReadonly}">
	<div class="messagedivforreturnpayment" id="messagedivforreturnpayment"></div>
	<form:form method="post" name="returnPaymentForm" id="returnPaymentForm"> 
	<input type="hidden" id="saveDetailsUrl" name ="saveDetailsUrl" value="${saveReturnPaymentDetails}"/>
				<div id="returnForm" class="formcontainer" >
				<h3>Returned Payment Information</h3>
				<div class="saveButtonClass buttonholder">
						<input type="submit" class="button" name="save" value="Save"
							title="Save" id="saveDetails" />
					</div>
					<div class="row">
						<span class="label">Fiscal Year Amount:</span> <span class="formfield"><fmt:formatNumber type="currency" value="${fiscalBudgetInfo.approvedBudget}" /></span>
					</div>
					<div class="row">
						<span class="label">Fiscal Year:</span> <span class="formfield">${returnPaymentDetails.budgetfiscalYear}</span>
					</div>
					<div class="row">
					<%-- Changing maxlength value from 4 to 20 for enhancement 8652 --%>
						<span class="label"><label class="required">*</label>Check #:</span> 
						<span><input type="text" name="checkNumber"
							id="checkNumber" value="${returnPaymentDetails.checkNumber}" maxlength="20" onchange="setFieldChangeFlag();"/>
						</span>
						<span class="error" id="checkNumberErrorSpan" style="width: 480px;"></span>	
				</div>
					<div class="row">
						<span class="label">Agency Tracking #:</span> 
						<span class="formfield">
							<input type="text" name="agencyTracking" id="agencyTracking" maxlength="20"
							value="${returnPaymentDetails.agencyTrackingNumber}" onchange="setFieldChangeFlag();" />
						</span>
					</div>
					<div class="row">
						<span class="label"><label class="required">*</label>Check Amount:</span>
						<span class="formfield" style="width: 180px !important;">
							<input type="text" name="checkAmount" id="checkAmount" value="${returnPaymentDetails.checkAmount}" onchange="setFieldChangeFlag();" />
						</span>
						<span class="error" id="checkAmountErrorSpan" style="width: 480px;"></span>
					</div>
					<div class="row">
					<%-- Changes for defect 8588 --%>
						<span class="label"><label class="required">*</label>Check Date:</span> 
						<span class="formfield" style="width: 180px !important;"><input
							type="text" name="checkDate" size="10" maxlength="10" id='checkDate'
							value="${returnPaymentDetails.checkDate}"  style="width: 138px;" validate="calenderFormat"/>
							<img id="checkDateImg" title="Check Date" alt="Check Date"
							src="../framework/skins/hhsa/images/calender.png"
							onclick="NewCssCal('checkDate',event,'mmddyyyy');return false;" onchange="setFieldChangeFlag();"/></span>
							<span class="error" id="checkDateErrorSpan" style="width: 480px;"></span>
					</div>
					<div class="row">
					<%-- Changes for defect 8588 --%>
						<span class="label"><label class="required">*</label>Received Date:</span> <span class="formfield" style="width: 180px !important;"><input
							type="text" name="receivedDate" size="10" maxlength="10" id='receivedDate'
							value="${returnPaymentDetails.receivedDate}"  style="width: 138px;" validate="calenderFormat"/>
							<img id="receiveDateImg" title="Check Date" alt="Check Date"
							src="../framework/skins/hhsa/images/calender.png"
							onclick="NewCssCal('receivedDate',event,'mmddyyyy');return false;" onchange="setFieldChangeFlag();" /></span>
							<span class="error" id="receivedDateErrorSpan" style="width: 480px;"></span>
					</div>
					<div class="row">
							<span class="label" style="height : 72px;"><label class="required">*</label>Description:</span> <span
								class="formfield" style="width: 180px !important;"> <textarea id="descriptionInput" onchange="setFieldChangeFlag();" onkeyup="setMaxLengthForEvaluatePropasal(this,200);"
									name="descriptionInput" style="width: 190px;resize: none;height:60px;">${returnPaymentDetails.description}</textarea>
							</span>
							<span id="descriptionInputErrorSpan" class="error" style="width: 480px; position: relative;top: 60px;"></span>
						</div>
					
				</div>
	</form:form>
	<%-- <div id="tempDocument" style="display:none"><jsp:include
			page="/WEB-INF/r2/jsp/tasks/document.jsp" /></div> --%>
	</d:content>
<div class='clear'></div>
<div class='gridFormField'>
	<task:taskContent workFlowId="" showDocument="" taskType="taskReturnedPaymentReview" isTaskScreen=""  level="footer"></task:taskContent>
	</div>
	</d:content>
	</div>
</div>
<!-- Main Div area ends-->
<%--  Overlay popup starts --%>
<div class='overlay'></div>
<div class="alert-box-help">
				<div class="tabularCustomHead toplevelheaderHelp"></div>
	            <div id="helpPageDiv"></div>
		  		<a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
			</div>
<div class="alert-box-submit-contract" id="overlayDivId"></div>
<jsp:include page="cancelReturnedPaymentOverlay.jsp"></jsp:include>