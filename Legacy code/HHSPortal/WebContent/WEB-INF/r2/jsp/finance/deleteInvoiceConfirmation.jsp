<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<%-- Overlay Popup Starts --%>
<div class="content">
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/invoiceDelete.js"></script>

<%-- Overlay Popup Starts --%>

<form:form action="" id="invoiceDeleteForm" name="invoiceDeleteForm"  commandName="AuthenticationBean">
<div class="tabularCustomHead">
    
    <span id="InvoiceTypeId">Confirm Invoice Deletion</span> <a href="javascript:void(0);" class="exit-panel">&nbsp;</a></div>
    <div class="tabularContainer"> 
     <div id="ErrorDiv" class="failed breakAll"  > </div>
	<h2>Delete Invoice</h2>
	<div class='hr'></div>
  <c:if
	test="${accessScreenEnable eq false}">
	<c:set var="readOnlyPageAttribute" value="true"></c:set>
	<div class="lockMessage" style="display:block">${lockedByUser} currently has edit access to this record. This record cannot be deleted.</div>
   </c:if>
<p>Are you sure you want to delete this invoice? Enter required fields and click on the ‘Yes, delete 
this Invoice’ button to continue deletion or click the ‘No, do NOT delete this Invoice’ button to return 
to the Invoice List.
 </p>

<p><label class="required">*</label>Indicates a required field.</p>
    	<div>&nbsp;</div>
	<d:content isReadOnly="${readOnlyPageAttribute}">		
	<div class="formcontainer">
		<div class="row">
			 <input name="" type="checkbox" id='chkDeleteInvoice' onclick="enableSubmitButton(this);" />
			 <label for='chkDeleteInvoice'>I agree to delete this invoice.</label>
		</div>
		<div id="displayDiv" style="display:none">
			<div class="row">
				  <span class="label"><label class="required">*</label><label for='txtUsername'>User Name:</label></span>
				  <span class="formfield equalForms"><form:input path="userName" name="txtUsernameDelete" type="text" class='proposalConfigDrpdwn' id='txtUsernameDelete' /></span>
			<span class="error"></span>
                        <form:errors path="userName"></form:errors>
			</div>
			<div class="row">
				  <span class="label"><label class="required">*</label><label for='txtPassword'>Password:</label></span>
				  <span class="formfield equalForms"><form:password path="password" name="txtPasswordDelete"  cssClass='proposalConfigDrpdwn' id='txtPasswordDelete' autocomplete="off" /></span>
			<span class="error"></span>
            <form:errors path="password"></form:errors>
			</div>
		</div>
    <div class="buttonholder" id="buttonHolderDelete">
    	<input type="button" class="graybtutton" value="No, do NOT delete this Invoice" onclick="clearAndCloseOverLay();" />
    	<input type="submit" class="redbtutton" value="Yes, delete this Invoice" disabled="disabled" id="deleteInvoiceButton"/>
    </div>
  </div>
  </d:content>
    </div>
     <form:input type = 'hidden' value='${invoiceId}' id='hdnInvoiceId' name="hdnInvoiceId" path="invoiceId"/>
     </form:form>
   </div>

  <%-- Overlay Popup Ends --%>