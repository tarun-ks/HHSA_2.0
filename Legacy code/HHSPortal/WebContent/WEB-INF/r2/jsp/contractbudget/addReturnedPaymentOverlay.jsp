<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Iterator"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page errorPage="/error/errorpage.jsp"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<%-- Commenting below code as part of defect : 8627 fix --%>
<%-- <script type="text/javascript"
	src="${pageContext.servletContext.contextPath}/r2/js/contractBudget.js"></script> --%>
<script type="text/javascript" src="../resources/js/autoNumeric-1.7.5.js"></script>

<%--This JSP is for Add Return Payment overlay --%>
<style>

.sampleDiv{
border-width: 0px;
    position: absolute;
    left: 250px;
    top: 131px;
    width: 441px;
    height: 470px;
    font-family: 'Verdana';
    font-weight: 400;
    font-style: normal;
    font-size: 12px;
    text-align: left;
    background-color: palegoldenrod;
}
.sampleText{
  border-width:0px;
  position:absolute;
  left:10px;
  top:10px;
  width:433px;
  word-wrap:break-word;
}

.contractPopup span.error {
	width: 44% !important
}

.contractPopup .date {
	width: 5% !important
}

.tabularContainer {
	overflow-y: auto;
	height: 250px
}

.button {
	position: static !important
}

.tabularContainer .formcontainer .row span.error {
	margin-left: 70%
}
.formcontainer .row span.error{
	float: left;
	padding: 0px 0;
	text-align: left; 
	color:#D63301
}
.formfieldWidthClass{
width: 61% !important;
}
</style>


<style type="text/css">
.alert-box-amend-contract .ui-state-active .alert-box-bul-upload {
	background: #4297E2 !important
}

.alert-box-amend-contract {
	background: #FFF;
	display: none;
	z-index: 1001;
	position: fixed;
	width: 620px;
	height: 520px;
}
</style>
<portlet:defineObjects />
<portlet:resourceURL var="addReturnPaymentUrl" id="addReturnPaymentUrl"
	escapeXml="false"></portlet:resourceURL>
<%--  Overlay Popup Starts --%>
<%-- This Jsp contains required fields on Add return payment overlay --%>
<portlet:resourceURL var="getBulkUploadTemplatePage"
	id="getBulkUploadTemplatePage" escapeXml="false">
</portlet:resourceURL>
<div class="overlay"></div>
<div class="alert-box-amend-contract alert-box">
	<form id="addReturnedPaymentForm" method="post"
		name="addReturnedPaymentForm">
		<input type="hidden" id="addReturnPaymentUrl"
			value="${addReturnPaymentUrl}" />
		<input type="hidden" id="agencyAction" value="confirmNotification" name="actionSelected" />	
		<div id="newTabs">
			<div class='tabularCustomHead'>
				<span id="contractTypeId">Add Returned Payment</span> <a
					href="javascript:void(0);" class="exit-panel">&nbsp;</a>
			</div>

			<h2 class="boldclass textAlignLeft paymentMargin">
				Add Returned Payment
				<div class='hr' style="width: 588px;"></div>
			</h2>
			<!-- Change for defect 8608-->
			<div class="mymaindiv" style="margin-left: 20px;">
				<p style="line-height: 17px;">
					To indicate the amount still owed by the provider, or to initiate review of a check that your Agency has received, please complete the fields below.</br> <label
						class="required">*</label> Indicates a required field.
				</p>
			</div>
			<div class="formcontainer contractPopup" style="margin-left: 20px;">
			<!--  emergency build 6.0.1 - INC000001386100/INC000001385777
					Fix for Budget page not loading due to single quote in contract title (changing name of span)-->
			<div id="addReturnedPaymentformData">
			<input type="hidden" id="programName" value="${contractInfo.programName}"/>
				<div class="row">
						<span class="label equalForms">Procurement/Contract Title:</span> 
						<span class="formfield equalForms" id="contractTitleLabel">${contractInfo.contractTitle}</span>
						 <span class="error"></span>
				</div>
				
				<div class="row">
					<span class="label equalForms">CT#:</span>
					 <span class="formfield equalForms" id="ct_number">${contractInfo.extCT}</span>
				</div>

				<div class="row">
					<span class="label equalForms">Provider:</span>
					 <span class="formfield equalForms" id="providersName">${contractInfo.provider}</span>
				</div>

				<div class="row">
					<span class="label equalForms">Fiscal Year:</span>
					<span class="formfield equalForms" id="fiscalYear"></span>
				</div>
				
				<div class="row">
					<span class="label equalForms">Agency Tracking #:</span>
					 <span class='formfield equalForms'>
					 <!-- Changed name for R6:Fix for Defect 8552 -->
					 <input type = "text" class='formfieldWidthClass' name="agencyTrackingNumber" id="agencyTrackingNumber" maxlength="20" />
					<!-- Changed name for R6:Fix for Defect 8552 end-->
					<span class="error"></span>
					</span> 
				</div>
				
				<div class="row">
					<span class="label equalForms"><label class="required">*</label>Check Amount ($):</span>
					 <span class='formfield equalForms'>
					 <input type="text" class='formfieldWidthClass' name="checkAmountVal" id="checkAmountVal" onchange="clearErrorSpan()"/>
					</span> <span class="error"></span>
				</div>
				
				<div class="row">
					<span class="label equalForms" style="height: 70px;"><label class="required">*</label>Description:</span> 
					<span class="formfield equalForms">
					<textarea class='formfieldWidthClass' maxlength="200" onkeyup="setMaxLengthForAddContract(this,200)"onkeypress="setMaxLengthForAddContract(this,200)"
							id="descriptionInput" name="descriptionInput" style="resize: none;height:60px;width:72% !important;">
					</textarea> </span>
					<span class="error" style="margin-left: 323px;"></span>
				</div>

				<div id='radioButton'>
					<div class='row'>
						<span class="label equalForms"><label class="required">*</label>Has your Agency received the check?:</span>
						<span><input style="margin-top:10px;" type="radio" name='checkReceivedRadio' id='checkReceivedRadio'
							class='checkDefaultTask' value='Y' onchange="showProviderRadio(this);">Yes
							<input type='radio' name='checkReceivedRadio' id='checkReceivedRadio' class='checkDefaultTask' value='N'
							onchange="showProviderRadio(this);">No
						</span>
						<span class="error" id="errorSpanForRadio"></span>
				</div>

				<div class='row hiddenBlock' id="notifyProvider">
						<span class="label equalForms"><label class="required">*</label>
							Notify provider of pending payment?:</span> 
							<span><input type="radio" name='notifyProviderVal' id='notifyProviderVal'
							class='checkDefaultTask' value='Y' onchange="showProviderList(this);">Yes 
							<input type='radio' name='notifyProviderVal' id='notifyProviderVal'
							class='checkDefaultTask' value='N' onchange="showProviderList(this);">No </span>
							<span style="margin-left: 20px;">
							<a style="text-decoration: underline;" href="#" onclick="viewNotificationSample()">View Sample Notification</a></span>
							<span class="error"></span>
				</div>
				<div class='row hiddenBlock' id="notifyProviderList">
						<span class="label"><label class="required">*</label>Send To:</span>
						<span>
						<select name="notProvider" id="notProvider" onchange="disableButton()" style="height: 22px; width: 214px;">
							<option value=""></option>
							<c:forEach items="${NotificationProviderList}" var="option">
								<option value="${option.key}">
									<c:out value="${option.value}"></c:out>
								</option>
							</c:forEach>
						</select> 
						</span>
						<span class="error"></span>
					</div>
				</div>
<!-- Removing closing div and adding it after buttonholder div, to fix sample notification issue induces in Release 6.0.1 Defect#8655-->
				<div id="SampleNotificationDiv" class="sampleDiv" data-label="Notification flyout detailed" style="visibility: hidden; z-index: 1074; display: none;" >
					<div id="SampleNotificationText" class="sampleText">
					<!--  Fix for QC defect : 8611, to show 'X' on sample notification pop up -->
					<a onclick="closeSampleNotificationDiv();" style="cursor: pointer;float: right;margin-right: 10px;" title="Close">[X]</a>
						${sampleNotification}
					</div>
				</div>

				<div class="buttonholder" id="actionButtons">
					<input type="button" class="graybtutton" name="cancelButton" title="Cancel" value="   Cancel   " id="cancelButton"
						onclick="clearAndCloseOverLayReturnPayment()" />
					 <input type="submit" class="button buttonGap" name="returnedPayment" value="Add Returned Payment" title="Add Returned Payment" id="AddReturnedPayment"  />
				</div>
</div>
			</div>
		</div>
	</form>
</div>

<script>
function loadOnReady(){
			$('#checkAmountVal').autoNumeric({
				vMax : '999999999999.99'
			});
		};
</script>