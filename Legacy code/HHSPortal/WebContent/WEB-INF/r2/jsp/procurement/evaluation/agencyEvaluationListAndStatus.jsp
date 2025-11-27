<%@page import="javax.portlet.PortletContext"%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />
<portlet:actionURL var="evaluationListsUrl" escapeXml="false">
	<portlet:param name="submit_action" value="evaluationListsUrl" />
</portlet:actionURL>
<form:form id="evolutionListForm" action="${evaluationListsUrl}" method="post"
	commandName="EvaluationBean">
</form:form>