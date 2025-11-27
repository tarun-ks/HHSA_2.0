<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<portlet:defineObjects />
<style>
.alert-box-proposal-comment{
	background: #FFF;
    z-index: 1001;
    position: fixed
}
</style>

<div class="" style="width:600px">
    <div class="tabularCustomHead">Proposal Comments</div>
    <div class="tabularContainer" style='overflow:hidden'> 
    <c:choose>
	    <c:when test="${proposalCommentsList ne null}">
	    	<c:forEach items="${proposalCommentsList}" var="proposalCommentsList">
		    	<c:set var="agencyName" value="${fn:split(proposalCommentsList.agencyName, '-')}"></c:set>
		    	<div class="row">
		    		<div class="content wordWrap" style="width:400px">
		    			<b>${agencyName[1]} - <fmt:formatDate pattern='MM/dd/yyyy' value='${proposalCommentsList.auditDate}'/></b>
	    			</div>
		    	</div>
		    	<div class="row">
					<div class="content wordWrap">
		    			${proposalCommentsList.userComment}<br/>
		    		</div>
		    	</div>
	    	</c:forEach>
	    </c:when>
	    <c:otherwise>
	    	No comments found
	    </c:otherwise>
    </c:choose>
    </div>
    <a href="javascript:void(0);" class="exit-panel"></a> 
    </div>