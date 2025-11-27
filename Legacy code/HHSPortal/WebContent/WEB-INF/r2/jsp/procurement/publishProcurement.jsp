<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="nav" uri="/WEB-INF/tld/navigationSM.tld"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>  
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<fmt:setBundle basename="com/nyc/hhs/properties/messages"/>
<portlet:defineObjects />
<nav:navigationSM screenName="PublishProcurement"> 
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/publishProcurement.js"></script> 
<portlet:actionURL var="publishProcurementUrl" escapeXml="false">
	<portlet:param name="submitAction" value="publishProcurement" />
	<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}"/>
	<portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}"/>
</portlet:actionURL>
<form:form id="publishProcurementform" action="${publishProcurementUrl}" method="post"
	commandName="AuthenticationBean" name="publishProcurementform"> 
 <form:hidden path="procurementId" value="${procurementId}"/>
<div id="tabs-container">

<div class="clear"></div>

<d:content isReadOnly="${(accessScreenEnable eq false) or hideExitProcurement}">
<h2>Publish Procurement</h2>
<div class="hr"></div>
	
	<c:set var="sectionUnpublishInformation"><%=HHSComponentMappingConstant.S208_UNPUBLISHED_INFORMATION%></c:set>
		<d:content section="${sectionUnpublishInformation}">
			<c:if test="${unPublishedDataMsg ne null}">
				<div class='infoMessage' style="display:block">${unPublishedDataMsg}</div>
			</c:if>
		</d:content>
			<c:if test="${message ne null}">
				<c:choose>
					<c:when test="${serviceNameList ne null}">
						<div class="${messageType}" id="messagediv" style="display:block">
							<c:set var="listSize" value="${fn:length(serviceNameList)}"></c:set>
							<c:set var="ctr" value="0"></c:set>
							<c:forEach var="serviceName" items="${serviceNameList}">
							<c:set var="ctr" value="${ctr + 1}"></c:set>
							<c:choose>
								<c:when test="${ctr eq  listSize}">
									${serviceName}
								</c:when>
								<c:otherwise>
									${serviceName},
								</c:otherwise>
							</c:choose>
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
		
	<p>Enter your User Name and Password to publish this Procurement. 
		<br>The Procurement will appear as an entry in the Procurement Roadmap to all HHS Accelerator users in the system.
	</p>

<%-- Form Data Starts --%>		
<div class="formcontainer">
		<div class="row">
			<span class="label">User Name:</span>
			<span class="formfield">
			<form:input path="userName" cssClass="input" id="userName" autocomplete="off"/>
		</span>
		<span class="formfield error">
			<form:errors path="userName" ></form:errors>
		</span>
		</div>
		<div class="row">
			<span class="label">Password:</span>
			<span class="formfield">
				<form:password path="password" cssClass="input" id="password" autocomplete="off"/>
			</span>
			<span class="formfield error">
			<form:errors path="password" ></form:errors>
		</span>
		</div>
		<div class="row">
			<span class="label clear"></span>
			<span class="formfield">
			<div class="buttonholder">
				<input type="submit" value="Publish Procurement" id="publishprocurementbutton"/>
				</div>
			</span>
		</div>
	</div>
	</d:content>
</div>
</form:form>
</nav:navigationSM> 

<div class="overlay"></div>
