<%@page import="org.apache.taglibs.standard.tag.el.core.ForEachTag"%>
<%@ page errorPage="/error/errorpage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages" />
<portlet:defineObjects />
<portlet:actionURL var="uploadRfpDocumentUrl" escapeXml="false">
	<portlet:param name="action" value="rfpRelease" />
	<portlet:param name="submit_action" value="displayFileInformation" />
</portlet:actionURL>
<portlet:resourceURL var="rfpReleaseResource" escapeXml="false">
	<portlet:param name="action" value="rfpRelease"/>
</portlet:resourceURL>
<input type="hidden" value="${rfpReleaseResource}" id="uploadDocUrlHidden"/>
<div class="overlaycontent">
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/uploadRfpDocument.js"></script>
	<form:form action="${uploadRfpDocumentUrl}" enctype="multipart/form-data" 
		method="post" name="uploadRFPform" id="uploadRFPform"
		commandName="rfpDocuments">
		<div class="formcontainer">
		<div class="messagedivover" id="messagedivover"></div>
		<div class="pad10">Select a document category and document type,
		then browse your computer for the file to upload.</div>
		<div id="reqiredDiv" class="reqiredDiv"><label class="required">*</label>Indicates
		a Required Field</div>
		<%-- Start : Changes in R5 --%>
		<div class="row" id="typeDiv"><span class="label"><label
			class="required">*</label>Document Type:</span> 
			<span class="formfield"> 
				<c:choose>
				    <c:when test="${document.docType eq null}">
				    	<input type="text" path="doctype" value="" name="doctype" id="doctype" class="input" onkeypress="if (this.value.length > 60) { return false; }" />
				    </c:when>
				    <c:otherwise>
				    	${document.docType}
				    	<input type="hidden" path="doctype" value="${document.docType}" name="doctype" id="doctype" class="input" onkeypress="if (this.value.length > 60) { return false; }" />
				    </c:otherwise>
			    </c:choose>
				</span>
			 <span class="error"></span></div>
		<div class="row">
			<span class="label"><label class="required">*</label>Select the file to upload:</span> 
			<span class="formfield">
					<input type="file" id="uploadfile" name="uploadfile" />
			</span></div>
		<div class="row" id="hidden" style="display: none">
			<span class="label"><label class="required">*</label>Document Name:</span> 
			<span class="formfield"><form:input path="docName" id="docName" maxlength= "50"/> </span> 
			<%-- End : Changes in R5 --%>
			<span class="error"></span>
		</div>
		<div class='buttonholder'><input type="button" value="Cancel"
			title="Cancel" name="cancel" id="cancel" class="graybtutton" /> <input
			type="button" value="Next" title="Next" name="next1" id="next1" /></div>
	 </div>
	</form:form>
</div>
