<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>


<portlet:defineObjects />
<%--Screen S407 - cancel  Amendment overlay starts--%>

<portlet:resourceURL var="cancelAmendment"
	id="cancelAmendment" escapeXml="false">
</portlet:resourceURL>
<input type='hidden' value='${cancelAmendment}' id='cancelAmendmentUrl' />

<div class="content">
<div id="newTabs">
<div class='tabularCustomHead'><span id="contractTypeId">Confirm Amendment Cancellation</span> <a href="javascript:void(0);" class="exit-panel">&nbsp;</a></div>

<div class="tabularContainer">
<%if(( null != request.getAttribute("Error") ) && !"".equalsIgnoreCase((String)request.getAttribute("Error"))){%>
	     <div id="transactionStatusDiv" class="failed breakAll" style="display:block" ><%=request.getAttribute("Error")%> </div>
	<%}%>
	 <div id="ErrorDiv" class="failed "> </div>
<h2>Cancel Amendment</h2>
<div class='hr'></div>

<div><span class="error" id="errorMsg"></span></div>

<p>Please enter the reason for cancelling the contract amendment in the comments section below and then click on the Cancel Amendment button.
</p>

<p><label class="required">*</label>Indicates a required field</p>
	<form:form id="cancelAmendmentForm" action="${cancelAmendment}" method="post" name="cancelAmendmentForm">
<div class="formcontainer">
	<div class="row">
		<span class="formfield autoWidth">
			<input type="checkbox" class='autoWidth' id='agreeCancel' onclick="enableSubmitButton(this);" />
			<label for='agreeCancel'>I agree to cancel this amendment.</label>
		</span>
			<span class="formfield">&nbsp;</span>
			<span class="error"></span>
	</div>
	<div id="inputDiv" style="display:none">
	<div class='row'>
		<textarea name="commentArea" id="commentArea" class='taskFullCommentsTxtarea floatLft' onkeyup="clearCommentError();setMaxLength(this,500);" onkeypress="setMaxLength(this,500);" 
		 cols="" rows="5"  ></textarea>
		<label class='required'>*</label>
		<span id="commentAreaErrorId" class="error"></span>
	</div>
	
	<div class="row"><span class="label"><label class="required">*</label>User Name:</span> 
	<span class="formfield"><input type="text" class='proposalConfigDrpdwn' name='userName' id="userName" maxlength="128"/></span> 
		  <span class="error"></span>
	</div>

	<div class="row" ><span class="label"><label class="required">*</label>Password:</span>
	<span class="formfield"><input type="password" class='proposalConfigDrpdwn' name='password' id="password"
		placeholder="Password" autocomplete="off" /></span><span class="error"></span>
	</div>
	</div>
</div>
<input type="hidden" id="contractId" name="contractId" value="${ContractId}"/>
	<%--code updation for R4 starts--%>
<input type="hidden" id="contractId" name="amendcontractid" value="${amendcontractid}"/>
	<%--code updation for R4 ends--%>
<div class="buttonholder"><input type="button" class="graybtutton" value="No, do NOT cancel this Amendment" id="btnNotCancelAmendment" onclick="clearAndCloseOverLay();"  /> 
<input type="submit" class="redbtutton" value="Yes, cancel this Amendment" id="btnCancelAmendment" disabled="disabled" /></div>
</form:form>
</div>
<%--cancel  Amendment overlay ends--%>
</div>
</div>
<script type="text/javascript">
var requiredKey= "<fmt:message key='REQUIRED_FIELDS'/>";
</script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/cancelAmendment.js"></script>