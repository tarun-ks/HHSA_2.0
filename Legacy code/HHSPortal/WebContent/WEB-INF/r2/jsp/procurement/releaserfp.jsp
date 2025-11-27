<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="nav" uri="/WEB-INF/tld/navigationSM.tld"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@page errorPage="/error/errorpage.jsp" %>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="com/nyc/hhs/properties/messages"/>
<nav:navigationSM screenName="ReleaseRFP">
	<portlet:defineObjects />
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/releaserfp.js"></script> 
	<portlet:actionURL var="releaseRfpUrl" escapeXml="false">
		<portlet:param name="action" value="rfpRelease" />
		<portlet:param name="submit_action" value="releaseRfp" />
		<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}"/>
		<portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}"/>
	</portlet:actionURL>
	<form:form id="releaseRfpform" action="${releaseRfpUrl}" method="post" commandName="Authentication" name="releaseRfpform"> 
 		<form:hidden path="procurementId" value="${procurementId}"/>
		<div id="tabs-container">
			<d:content isReadOnly="${(accessScreenEnable eq false) or hideExitProcurement}">
			<%-- Container Starts --%>
			<div class="clear"></div>
			<%-- Form Data Starts --%>
				<h2>Release RFP</h2>
					
					<div class="hr"></div>
					
					<c:set var="sectionUnpublishInformation"><%=HHSComponentMappingConstant.S211_UNPUBLISHED_INFORMATION%></c:set>
					<d:content section="${sectionUnpublishInformation}">
						<c:if test="${unPublishedDataMsg ne null}">
							<div class='infoMessage' style="display:block">${unPublishedDataMsg}</div>
						</c:if>
					</d:content>
					<c:if test="${message ne null}">
						<c:choose>
							<c:when test="${serviceNameList ne null}">
								<div class="${messageType}" id="messagediv" style="display:block">
									<c:forEach var="serviceName" items="${serviceNameList}">
										${serviceName},
									</c:forEach>
										${message}
									<img
										src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
										class="message-close" 
										onclick="showMe('messagediv', this)">
								</div>
							</c:when>
							<c:when test="${missingInfoList ne null}">
								<div class="${messageType}" id="messagediv" style="display:block">
									${message}
									<ul class='errorBullets'>
										<c:forEach var="missingInfo" items="${missingInfoList}">
											<li>${missingInfo}</li>
										</c:forEach>
									</ul>
									<img
										src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
										class="message-close" 
										onclick="showMe('messagediv', this)">
								</div>
							</c:when>
							<c:otherwise>
								<div class="${messageType}" id="messagediv" style="display:block">${message} <img
									src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
									class="message-close" 
									onclick="showMe('messagediv', this)">
								</div>
							</c:otherwise>
						</c:choose>
					</c:if>
					<c:if test="${accessScreenEnable eq false}">
						<c:set var="lockAccess"><fmt:message key='SCREEN_LOCKED'/></c:set>
						<div class="lockMessage" style="display:block">${lockedByUser} ${lockAccess}</div>
					</c:if>
					
					<p>Enter your User Name and Password to release the RFP. 
						<br>The RFP will be displayed to providers whose Service Applications are approved for the selected services. </br>
					</p>
					
					<div class="formcontainer">
						<div class="row">
							<span class="label">User Name:</span>
							<span class="formfield">
								<form:input path="userName" cssClass="input" id="userName" onblur="clearErrorMessage(this);" autocomplete="off"/>
							</span>
							<span class="error">
								<form:errors path="userName" cssClass="ValidationError"></form:errors>
							</span>
						</div>
						<div class="row">
							<span class="label">Password:</span>
							<span class="formfield">
								<form:password path="password" cssClass="input" id="password" autocomplete="off" onblur="clearErrorMessage(this);"/>
							</span>
							<span class="error">
								<form:errors path="password" cssClass="ValidationError"></form:errors>
							</span>
						</div>
						<div class="row">
							<span class="label clear"></span>
							<span class="formfield">
								<div class="buttonholder">
									<input type="submit" value="Release RFP" title="Save changes and proceed to Release RFP" id="releaseRFPbutton"/>
								</div>
							</span>
						</div>
					</div>
				</d:content>
			</div>
		</form:form>
</nav:navigationSM>

<div class="overlay"></div>
