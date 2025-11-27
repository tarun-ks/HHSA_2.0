<%-- JSP added in R4 --%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<script type="text/javascript"
	src="${pageContext.servletContext.contextPath}/r2/js/amendmentList.js"></script>
<portlet:defineObjects />
<%--S439 – Download Amendment Documents Lightbox pop-up--%>

<portlet:resourceURL var="downloadAmendmentDocument"
	id="downloadAmendmentDocument" escapeXml="false">
		<portlet:param  name='contractId' value='${amendcontractid}'/>
		<portlet:param  name='contractTitle' value='${contractTitle}'/>
		<portlet:param name="provider" value="${provider}" />

</portlet:resourceURL>
<input type='hidden' value='${downloadAmendmentDocument}' id='downloadAmendmentDocumentUrl' />

<div class="content">
<div id="newTabs">
<div class='tabularCustomHead'><span id="">Download Amendment Documents</span> <a href="javascript:void(0);" class="exit-panel">&nbsp;</a></div>

<div class="tabularContainer">

<input type="hidden" name="contextPathSession" id="contextPathSession" value="${pageContext.servletContext.contextPath}" />
<div id="jsmessagediv" class="failed" style="display: none;">
				</div>


	 <div id="ErrorDiv" class="failed"> </div>
<h2>Download Amendment Documents for Registration</h2>
<div class='hr'></div>

<div><span class="error" id="errorMsg"></span></div>

<p>If you continue, the Amendment Certification of Funds and Amendment Budget Summary of all impacted budgets will be downloaded and the Amendment Status will change to 'Sent for Registration'. Do you want to continue with the download?

</p>


	<form:form id="downloadAmendmentDocumentForm" action="" method="post" name="downloadAmendmentDocumentFormName">

<div class="buttonholder"><input type="button" class="graybtutton" value="Cancel" id="btnNotDownloadAmendmentDocument" onclick="clearAndCloseOverLay();"  /> 
<input type="button" class="button" value="Download Documents" id="btnDownloadAmendmentDocument"  onclick="downloadAmendmentDocumentUrl()"/></div>
</form:form>
</div>
<%--cancel  Amendment overlay ends--%>
</div>
</div>
<script type="text/javascript">
var requiredKey= "<fmt:message key='REQUIRED_FIELDS'/>";
</script>