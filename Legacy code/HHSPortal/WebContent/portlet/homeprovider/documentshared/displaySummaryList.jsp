<!--This page is displayed when an accelerator select view history from the drop down. 
It will display history for business and services-->
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@page import="com.nyc.hhs.frameworks.grid.*,com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<style type="text/css">
	.bodycontainer #main-wrapper .bodycontainer{
		width:890px;
		padding:0px;
	}
	.grid td{
		padding: 4px;
	}
</style>

<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.BA_S047_SECTION, request.getSession())){%>
	<div class="overlaycontent1">           	
		<div  class="tabularWrapper" style='height:400px; overflow:auto; padding:4px;'>
			<c:choose>
	        	<c:when test="${historyList ne null and fn:length(historyList)>0}">
			    	<table cellspacing="0" cellpadding="0"  class="grid">
				        <tr>
				        	<th>Application/Section</th>
		                    <th>Status</th>
		                    <th>Updated By</th>
		                    <th>Date</th>
		                    <th>Comment</th>
	                	</tr>
						<c:forEach var="historyListInfo" items="${historyList}" varStatus="counter">
							<tr class="${counter.count%2 eq 0? 'alternate' : ''}">
								<td>${historyListInfo.msSection}</td>
								<td>${historyListInfo.msAppStatus}</td>
								<td nowrap>${historyListInfo.msAppSubmittedBy}</td>
								<td><fmt:formatDate pattern="MM/dd/yyyy" value="${historyListInfo.mdAppSubmissionDate}" /></td>
								<td>${historyListInfo.msComments}</td>
							</tr>	
						</c:forEach>					 
			    	</table>
				</c:when>
			<c:otherwise>
				<div>No record found</div>
			</c:otherwise>
			</c:choose>   
		</div>
	</div>			
<br/>

<% } else {%>
	<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
<%} %>
