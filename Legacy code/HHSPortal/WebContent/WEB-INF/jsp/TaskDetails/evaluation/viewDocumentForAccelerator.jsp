<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />
<%
	boolean lbFlag=false;
if (renderRequest.getParameter("controller_action") != null) {
	lbFlag=true;
	}
%>
	<jsp:include page="/WEB-INF/r2/jsp/procurement/viewdocumentinfo.jsp"></jsp:include>
	<input type="hidden" id="isForCityTask" value="<%=lbFlag%>"/>
