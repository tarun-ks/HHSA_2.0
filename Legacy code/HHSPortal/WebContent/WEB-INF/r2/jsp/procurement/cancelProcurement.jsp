<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<portlet:defineObjects />
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/cancelProcurement.js"></script>
<%-- Overlay Popup Starts --%>
<portlet:actionURL var="cancelProcurementUrl" escapeXml="false">
	<portlet:param name="submit_action" value="cancelProcurement"/>
</portlet:actionURL>
<form:form id="cancelProcurementForm" action="${cancelProcurementUrl}" method="post"
		commandName="AuthenticationBean" name="cancelProcurementForm">
		
	<div class='tabularContainer'>
	<h2>Cancel Procurement</h2>
	<div class='hr'></div>
	<p>Are you sure you want to cancel this Procurement?</p>
	<p>Cancelling the Procurement will remove it from the default view of the Procurement Roadmap.
	Upon cancelling, you will no longer be able to edit the Procurement and can only view it by filtering the Procurement Roadmap by the &quot;Cancelled&quot; Procurement Status.
	</p>
	<div id="errorPlacementWrapper"> 
	<c:if test="${message ne null}">
				<div class="${messageType}" id="errorPlacement" style="display:block">${message} <img
					src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
					class="message-close" 
					onclick="showMe('errorPlacementWrapper', this)">
				</div>
			</c:if>
		</div>
	<div class="formcontainer" id="cancelProcurement">
		<p><input type="checkbox" id='chkCancelProcurement' onclick="hideUnhideUsername(this);"/>
		<label for='chkCancelProcurement'>Yes, I understand that after cancellation, this Procurement cannot be continued.</label></p>
    	<div>&nbsp;</div>
		<div id="authenticate">
			<div class="row" id="usernameDiv">
				<span class="label">User Name:</span>
				<span class="formfield">
					<form:input path="userName" id="userName" autocomplete="off"/>
				</span>
				<span class="formfield error">
				</span>
			</div>
			<div class="row" id="passwordDiv">
				<span class="label">Password:</span>
				<span class="formfield">
					<form:password path="password" id="password" autocomplete="off"/>
				</span>
				<span class="formfield error">
				</span>
			</div>
		</div>
	</div>
		
    <div class="buttonholder">
    	<input type="button" class="graybtutton" id="doNotCancelProcurement" value="No, do NOT cancel this Procurement" onclick="cancelOverLay();" />
    	<input type="submit" id="yesCancelProcurement" value="Yes, cancel this Procurement"/>
    </div>
    </div>
    <form:hidden path="procurementId" id="cancelProcurementId" value="${cancelProcurementId}"/>
 </form:form>
