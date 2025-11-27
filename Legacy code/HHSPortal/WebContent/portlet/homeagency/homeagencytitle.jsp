<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="com.nyc.hhs.constants.ApplicationConstants" %>
<%@ page import="com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant, org.apache.commons.lang.StringEscapeUtils;" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="fullAgencyName" value="<%=StringEscapeUtils.unescapeJava((String)session.getAttribute(ApplicationConstants.KEY_SESSION_ORG_NAME))%>"/>
<c:set var="agencyNameSplit" value="${fn:split(fullAgencyName, '-')}" />
<c:choose>
	<c:when test="${agencyNameSplit[1] != null}">
		<c:set var="agencyNameToDisplay" value="${agencyNameSplit[1]}" />
	</c:when>
	<c:otherwise>
		<c:set var="agencyNameToDisplay" value="${agencyNameSplit[0]}" />
	</c:otherwise>
</c:choose>
<title>${agencyNameToDisplay} Homepage</title>
<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S029_PAGE_5, request.getSession())) {%>
	<div class="portletTextBold">${agencyNameToDisplay} Homepage</div>
<%} %>
