<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="nav" uri="/WEB-INF/tld/navigationSM.tld"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="com/nyc/hhs/properties/messages"/>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/releaseAddendum.js"></script>
<portlet:defineObjects />
<portlet:actionURL var="relAddendum" escapeXml="false">
	<portlet:param name="action" value="rfpRelease" />
	<portlet:param name="submit_action" value="actionReleaseAddendum" />
	<portlet:param name="procurementId" value="${procurementId}" />
	<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}" />
	<portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}" />
</portlet:actionURL>
<nav:navigationSM screenName="ReleaseAddendum">
	<form:form id="releaseAddendumForm" action="${relAddendum}" method ="post" commandName="AuthenticationBean">
	<input type = "hidden" value="${relAddendum}" id="hiddenReleaseAddendum" />
	<input type = "hidden" value="${procurementId}" id="procurementId" name="procurementId"/>
		<div class='clear'></div>
		<%-- Form Data Starts --%>
		<div id='tabs-container'>
			<d:content isReadOnly="${(accessScreenEnable eq false) or hideExitProcurement}">
			<h2>Release Addendum</h2>
			<div class='hr'></div>
			<c:set var="sectionUnpublishInformation"><%=HHSComponentMappingConstant.S213_UNPUBLISHED_INFORMATION%></c:set>
			<d:content section="${sectionUnpublishInformation}">
				<c:if test="${unPublishedDataMsg ne null}">
					<div class='infoMessage' style="display:block">${unPublishedDataMsg}</div>
				</c:if>
			</d:content>
	
		 	<c:if test="${message ne null}">
				<div class="${messageType}" id="messagediv" style="display:block">${message} <img
					src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
					class="message-close" 
					onclick="showMe('messagediv', this)">
				</div>
			</c:if>
			<c:if test="${accessScreenEnable eq false}">
				<c:set var="lockAccess"><fmt:message key='SCREEN_LOCKED'/></c:set>
				<div class="lockMessage" style="display:block">${lockedByUser} ${lockAccess}</div>
			</c:if>
			<p>Please enter your User Name and Password to release the Addendum. 
			<div>The Addendum will be displayed to providers whose Service Applications are approved.</div>
			</p>
			
			<div class='formcontainer'>
				<div class='row'>
					<span class='label'>User Name:</span>
					<span class='formfield'>
						<form:input path="userName" class='input' autocomplete="off"/>
					</span>
					<span class="error">
						<form:errors path="userName" cssClass="ValidationError"></form:errors>
					</span>
				</div>
				<div class='row'>
					<span class='label'>Password:</span>
					<span class='formfield'>
						<form:password path="password" class='input' autocomplete="off"/>
					</span>
					<span class="error">
						<form:errors path="password" cssClass="ValidationError"></form:errors>
					</span>
				</div>
				<div class='row'>
					<span class='label clear'></span>
					<span class='formfield'>
					<p align="right">
					<input type="submit" class="button" title='Save changes and proceed to Release Addendum' value="Release Addendum" />
					</span>
				</div>
			</div>
			</d:content>
		</div>	
	</form:form>
</nav:navigationSM>
<div class="overlay"></div>
<div class="alert-box-help">
	<div class="tabularCustomHead">Procurement - Help Documents</div>
    <div id="helpPageDiv"></div>
 	<a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
</div>
<div class="alert-box-contact">
	<div id="contactDiv"></div>
</div>