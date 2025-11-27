<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"><head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<portlet:defineObjects />
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/closeProcurement.js"></script>
<%-- Overlay Popup Starts --%>
<portlet:actionURL var="closeProcurementUrl" escapeXml="false">
	<portlet:param name="submit_action" value="closeProcurement"/>
</portlet:actionURL>
<form:form id="closeProcurementForm" action="${closeProcurementUrl}" method="post"
		commandName="AuthenticationBean" name="closeProcurementform">
		<div class='tabularContainer'>
		<h2>Close Procurement</h2>
	<div class='hr'></div>
	<c:if test="${message ne null}">
			<div class="${messageType}" id="messagediv" style="display:block">${message}<img src="../framework/skins/hhsa/images/iconClose.jpg" id="box" class="message-close"  onclick="showMe('messagediv', this)"></div>
	</c:if>
	<p>Are you sure you want to close this Procurement?</p>
	<p>Closing the Procurement will remove it from the default view of the Procurement Roadmap. 
	  Upon closing, you will no longer be able to edit this Procurement and can only view it by filtering the Procurement Roadmap by the &quot;Closed&quot; Procurement Status.
	</p>
	<div id="errorPlacementWrapper"> 
	<c:if test="${message ne null}">
		<div class="${messageType}" id="errorPlacement" style="display:block">${message} <img
			src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
			class="message-close" 
			onclick="showMe('errorPlacementWrapper', this)"/>
		</div>
	</c:if>
		</div>
	<div class="formcontainer" id="closeProcurement">
		<p><input type="checkbox" id='chkCloseProcurement' onclick="hideUnhideUsername(this);"/>
		<label for='chkCloseProcurement'>Yes, I understand that closing this procurement cannot be undone.</label></p>
    	<div>&nbsp;</div>
		<div id="authenticate">
			<div class="row" id="usernameDiv">
				<span class="label">User Name:</span>
				<span class="formfield">
					<form:input path="userName" cssClass="input" id="userName" autocomplete="off"/>
				</span>
				<span class="formfield error">
				</span>
			</div>
			<div class="row" id="passwordDiv">
				<span class="label">Password:</span>
				<span class="formfield">
					<form:password path="password" cssClass="input" id="password" autocomplete="off"/>
				</span>
				<span class="formfield error">
				</span>
			</div>
		</div>
	</div>
    <div class="buttonholder">
    	<input type="button" class="graybtutton" id="doNotCloseProcurement" value="No, do NOT close this Procurement" onclick="closeOverLay();" />
    	<input type="submit" id="yesCloseProcurement" value="Yes, close this Procurement"/>
    </div>
    </div>
    <form:hidden path="procurementId" id="closeProcurementId" value="${closeProcurementId}"/>
 </form:form>
