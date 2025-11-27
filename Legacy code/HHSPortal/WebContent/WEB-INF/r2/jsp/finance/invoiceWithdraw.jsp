<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/invoiceWithdraw.js"></script>
<portlet:defineObjects />


<%-- Overlay Popup Starts --%>
<form:form  id="invoiceWithdrawSubmitForm" action=""  name="invoiceWithdrawSubmitForm" commandName="AuthenticationBean">
<div class="alert-boxd">
    <div class="tabularCustomHead">Confirm Invoice Withdrawal</div>
    <div class="tabularContainer"> 
    <div id="ErrorDiv" class="failed breakAll"  > </div>
	<h2>Withdraw Invoice</h2>
	<div class='hr'></div>
	<c:if
	test="${accessScreenEnable eq false}">
	<c:set var="readOnlyPageAttribute" value="true"></c:set>
	<div class="lockMessage" style="display:block">${lockedByUser} currently has edit access to this record. This record cannot be withdrawn.</div>
   </c:if>
	<p>Please enter the reason for withdrawing the Invoice in the comments section below and 
	then click the Withdraw Invoice button to continue or click the Cancel button to go back to the Invoice List. 
	</p>
	
	<p><label class="required">*</label>Indicates a required field</p>
    	<div>&nbsp;</div>
<d:content isReadOnly="${readOnlyPageAttribute}">	
	<div class="formcontainer">
		<div class="row">
			 <input name="" type="checkbox" id='chkWithdrawInvoice' onclick="enableSubmitButton(this);" />
			 <label for='chkWithdrawInvoice'>I agree to withdraw this invoice.</label>
		</div>
		<div id="displayDiv" style="display:none">
			<div class="row">
			<span class=" "></span>
		    <span class="">
				 <form:textarea path="reason" cols="" rows="6" class='input proposalConfigDrpdwn floatLft' id="textEnter" onkeyup="setMaxLength(this,300);" onkeypress="setMaxLength(this,300);"/><label class="required">*</label>
			     <form:errors path="reason"></form:errors>
			</span>
			<span class="error"></span>
			</div>
			<div class="row">
				  <span class="label equalForms"><label class="required">*</label><label for='txtUsername'>User Name:</label></span>
				  <span class="formfield equalForms"><form:input path="userName" cssClass='proposalConfigDrpdwn' id='txtUsername'/></span>
			<span class="error"></span>
			<form:errors path="userName"></form:errors>
			</div>
			<div class="row">
				  <span class="label equalForms"><label class="required">*</label><label for='txtPassword'>Password:</label></span>
				  <span class="formfield equalForms"><form:password path="password" cssClass='proposalConfigDrpdwn' id='txtPassword' autocomplete="off"/></span>
			<span class="error"></span>
			<form:errors path="password"></form:errors>
			</div>
		</div>
	</div>
		
    <div class="buttonholder">
    	<input type="button" class="graybtutton" title="Cancel" value="Cancel" onclick="clearAndCloseOverLay()" />
    	<input type="submit" id="withdrawButton" class="redbtutton" title="Withdraw Invoice" value="Withdraw Invoice" disabled="disabled"/>
    </div>
  </d:content>
    </div>
    <a href="javascript:void(0);" class="exit-panel"></a> 
	</div>
  <%-- Overlay Popup Ends --%>
  <form:input type = 'hidden' value='${invoiceId}' id='hdnInvoiceId' name="hdnInvoiceId" path="invoiceId"/>
</form:form>