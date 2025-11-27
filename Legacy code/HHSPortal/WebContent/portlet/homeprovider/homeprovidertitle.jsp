<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ page import="com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant" %>
<%@ page errorPage="/error/errorpage.jsp" %>

<title>Provider Homepage</title>

<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S027_PAGE_5, request.getSession())) {%>
	<div class="portletTextBold">Provider Homepage</div>
<%} %>
