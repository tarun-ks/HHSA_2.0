
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<portlet:defineObjects />
<c:set var="headerTitle" value="Task History:"></c:set>
<c:if test="${(org_type eq 'provider_org') || (detailsBeanForTaskGrid ne null && !detailsBeanForTaskGrid.isTaskScreen)|| detailsBeanForTaskGrid.isEntityTypeTabLevel}">
<c:set var="headerTitle" value="Comment History:"></c:set>
</c:if>
 	<div  class='tabularWrapper'>
 	<p>
 	<b>${headerTitle}</b><p>
	<table width='100%' cellspacing='0' cellpadding='0' border='1'>
	<tbody>
	<tr>
	<c:choose>
	<c:when test="${(org_type eq 'provider_org') || (detailsBeanForTaskGrid ne null && !detailsBeanForTaskGrid.isTaskScreen) || detailsBeanForTaskGrid.isEntityTypeTabLevel}">
			<th>Type</th>
	</c:when>
	<c:otherwise>
			<th>Action</th>
	</c:otherwise>
	</c:choose>
	<th>Detail</th>
	<th>User</th>
	<th>Date/Time</th>
	</tr>
		  <c:forEach var="viewHistoryBean" items="${commentsHistoryBean}" varStatus="rowCount">
		 <c:choose>
		   <c:when test="${rowCount.count mod 2 eq 0 }">
			<tr class="oddRows">
		   </c:when>
		   <c:otherwise>
		   <tr class="evenRows">
		   </c:otherwise>
		 </c:choose>
		<td>${viewHistoryBean.action}</td>
		<td>${viewHistoryBean.detail}</td>
		<td>${viewHistoryBean.user}</td>
		<td>${viewHistoryBean.dateTime}</td>
		</tr>
		</c:forEach>
	</tbody>
	</table>
		<c:if test="${(fn:length(commentsHistoryBean)) == 0}">
			<div class="noRecordCityBudgetDiv noRecord">
							No Records Found...
			</div>
			</c:if>
	</div>