
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--   BElpw styles added to remove status images from tabs --%>
<%-- Release 5 changes starts  --%>
<%@include file="/WEB-INF/r5/jsp/assigneeListMapping.jsp" %>
<portlet:resourceURL var="setDefaultUser" id="setDefaultUser"></portlet:resourceURL>
<input type="hidden" id="setDefaultUser" value="${setDefaultUser}"/>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/defaultAssignee.js"></script>
<%-- Release 5 changes Ends  --%>
<script type="text/javascript">




</script>
<portlet:defineObjects />

<div>

<div class="clear"></div>
<portlet:actionURL var="submitAction" escapeXml="false">
	<portlet:param  name="taskcontrollerAction" value="save"/>
</portlet:actionURL>
<task:taskContent workFlowId="ab123456ab" taskType="taskBudgetAmendment" level="header" taskDetail="" ></task:taskContent>

<form:form action="${submitAction}" method="post" name="taskForm1" id="taskForm1">

<input type = "hidden" value="${submitAction1}" id="hiddenURL"/>
<div style="height:200px">
<h2>My JSP</h2>
	<div class="customtabs">
			Middle JSP 
	</div>
	</div>
		<input type="submit" class="graybtutton" id="save" name="save"	value="save" onclick="formSubmit();"   />
		<br><br><br><br><br>
	<div>
	</div>
<div class='clear'>	</div>
</form:form>
<task:taskContent workFlowId="abhi" taskType="taskBudgetAmendment"  level="footer" commentsSection=" "></task:taskContent>




</div>
<script type="text/javascript">
function formSubmit(){
	$("#hiddenURL").val("${submitAction}");
	$("#"+document.forms[0].id).attr("action",$("#hiddenURL").val());
	document.taskFooterForm.submit();
}
</script>