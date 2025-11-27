
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<script type="text/javascript">
$(document).ready(function() {
	if($("#isForCityTask").val() == "true")
		{
		//document.getElementById("reassignDropDown").disabled = true;
		}
});
</script>
<portlet:defineObjects />
<%
	boolean lbFlag=false;
if (renderRequest.getParameter("controller_action") != null) {
	lbFlag=true;
	}
%>
	<jsp:include page="/WEB-INF/r2/jsp/agencyWorkflow/evaluateProposalTask.jsp"></jsp:include>
	<input type="hidden" id="isForCityTask" value="<%=lbFlag%>"/>
	
