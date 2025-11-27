<%@page import="org.apache.taglibs.standard.tag.el.core.ForEachTag"%>
<%@ page errorPage="/error/errorpage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages" />
<script type="text/javascript">
$(document).ready(function() {
		if('null' != '<%=request.getAttribute("message")%>' && '<%=request.getAttribute("messageType")%>' != 'confirmation'){
		$(".addDocmessagediv").html('${message}'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('addDocmessagediv', this)\" />");
		$(".addDocmessagediv").addClass('<%= request.getAttribute("messageType")%>');
		$(".addDocmessagediv").show();
		$("#selectdoc").hide();
		<%request.removeAttribute("message");%>
		<%session.removeAttribute("message");%>
		}
});
</script>
<portlet:defineObjects />
<portlet:actionURL var="addRfpDocumentUrl" escapeXml="false">
	<portlet:param name="action" value="rfpRelease" />
	<portlet:param name="procurementId" value="${procurementId}" />
	<portlet:param name="procurementDocId" value="${procurementDocId}" />
	<portlet:param name="proposalId" value="${proposalId}" />
	<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}" />
	<portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}" />
</portlet:actionURL>
<%-- Start : Changes in R5 --%>
<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/css/style.css" type="text/css"></link>
<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/css/addVault.css" type="text/css"></link>
<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/framework/skins/hhsa/css/jstree.style.min.css" type="text/css"></link>
<portlet:resourceURL var="openTreeAjax" id="openTreeAjax" escapeXml="false"></portlet:resourceURL>
<portlet:actionURL var="openFolder"  id="openFolder" escapeXml="false"></portlet:actionURL>
<portlet:actionURL var="addDocumentFromVault"  id="addDocumentFromVault" escapeXml="false">
</portlet:actionURL>
<portlet:actionURL var="getFolderStructure"  id="getFolderStructure" escapeXml="false"></portlet:actionURL>
<%-- End : Changes in R5 --%>
<div class="overlaycontent"><script type="text/javascript"
	src="${pageContext.servletContext.contextPath}/r2/js/addDocumentFromVault.js"></script>

	<%-- Code updated for R4 Starts --%>
	<c:choose>
		<c:when test="${workflowId ne null}">
			<portlet:resourceURL var="addRfpDocumentResource" id="addDocumentFromVaultUrl" escapeXml="false">
				<portlet:param name="action" value="agencyWorkflow"/>
				<portlet:param name="procurementId" value="${procurementId}" />
			</portlet:resourceURL>
		</c:when>
		<c:otherwise>
			<portlet:resourceURL var="addRfpDocumentResource" id="addRfpDocumentResource" escapeXml="false">
				<%-- <portlet:param name="action" value="rfpRelease"/> --%>
				<portlet:param name="procurementId" value="${procurementId}" />
			</portlet:resourceURL>
		</c:otherwise>
	</c:choose>
	<%-- Code updated for R4 Ends --%>

<form:form action="${addRfpDocumentUrl}" method="post"
	name="selectRFPDocForm" id="selectRFPDocForm"
	commandName="rfpDocuments">
	<%-- Start : Changes in R5 --%>
	<input type="hidden" value="${addRfpDocumentUrl}" name="addRfpDocumentUrl" id="addRfpDocumentUrl"/>
	<input type="hidden" value="${docType}" name="addDocType" id="addDocType"/>
	<%-- End : Changes in R5 --%>
	<input type="hidden" value="" name="docCategory" id="docCategory"/>
	<input type="hidden" value="" name="lastModifiedBy" id="lastModifiedBy"/>
	<input type="hidden" value="" name="lastModifiedDate" id="lastModifiedDate"/>
	<input type="hidden" value="" name="creationDate" id="creationDate"/>
	<input type="hidden" value="" name="submissionBy" id="submissionBy"/>
	<input type="hidden" value="" name="docTitle" id="docTitle"/>
	<input type="hidden" value="" name="docId" id="docId"/>
	<input type="hidden" value="${hiddendocRefSeqNo}" id="hiddendocRefSeqNo" name="hiddendocRefSeqNo"/>
	<input type="hidden" value="${uploadingDocumentType}" id="uploadingDocumentTypeAdd" name="uploadingDocumentTypeAdd"/>
	<input type="hidden" value="${awardId}" id="awardId" name="awardId"/>
	<input type="hidden" value="${evaluationPoolMappingId}" id="evaluationPoolMappingId" name="evaluationPoolMappingId"/>
	<input type="hidden" id="replacingDocumentId" value="${replacingDocumentId}" name="replacingDocumentId"/>
	<%-- Code updated for R4 Starts --%>
	<input type="hidden" id="hiddenAddendumType" value="${hiddenAddendumType}" name="hiddenAddendumType"/>
	<input type="hidden" id="procurementId" value="${procurementId}" name="procurementId"/>
	<input type="hidden" id="workflowId" value="${workflowId}" name="workflowId"/>
		<input type="hidden" id="hdnopenTreeAjaxVar" value="${openTreeAjax}"/>
		<input type="hidden" id="hdnopenFolder" value="${openFolder}"/>
		<input type="hidden" id="getFolderStructureUrl" value="${getFolderStructure}"/>
		<input type="hidden" id="submitFinal" value="${addDocumentFromVault}"/>
		<input type="hidden" name="customfolderid" id ="customfolderid" value='' />
			<input type="hidden" name="presentFolderId" id ="presentFolderId" value='${selectedfolderid}' />	
			<input type="hidden" name="DocumentType" id ="DocumentType" value="${docType}" />
	<%-- Code updated for R4 Ends --%>
	<div class="formcontainer">
	<%-- Start : Changes in R5 --%>
	<c:choose>
	<c:when test="${docType ne null}">
	<div id="addVaultHeader">Below are all the documents under the Document Type of <b><i>${docType}</i></b> located in your vault. Select one of the documents and click "Select".</div>
    </c:when>
	<c:otherwise>
	<div id="addVaultHeader">Please select one of the documents below and click "Select".</div>
	</c:otherwise>
	</c:choose>
				</br>
				<div id="dropDownSelection" class="vaultheader" onchange="dvTreeShow()">
				<img class='menuoptions-icon' src='../framework/skins/hhsa/images/menuoptions-icon.png'/>
				<select id="viewSelection">
				   <option value="View all documents">View all documents</option>
				   <option value="View with folder">View with folders</option></select>
				 </div>
	<div id="checkBoxVal" style="color: red;"></div>
	<div class="addDocmessagediv" id="addDocmessagediv"></div>
	<input type="hidden" value="${addRfpDocumentResource}" id="addRfpDocumentResourcePopup"/>
	<div class="tWForLinkDocView" id="docView">
	<div id="reRenderId" style="display:block">
		<div>
			<c:choose>
				<c:when test="${(fn:length(documentList)) > 0}">
				<div>&nbsp;</div>
					<st:table objectName="documentList" cssClass="heading"
						alternateCss1="evenRows" alternateCss2="oddRows"
						pageSize='${allowedObjectCount}'>
						<st:property headingName="Document Name" columnName="documentTitle"
							align="center" size="40%">
							<st:extension
								decoratorClass="com.nyc.hhs.frameworks.grid.AddDocumentFromVaultExtension" />
						</st:property>
						<st:property headingName="Document Type" columnName="documentType"
							align="right" size="30%" />
							<st:property headingName="Folder"
							columnName="filePath" align="right" size="20%" >
							<st:extension
								decoratorClass="com.nyc.hhs.frameworks.grid.AddDocumentFromVaultExtension" />
						</st:property>
						<st:property headingName="Modified" columnName="modifiedDate"
							align="right" size="20%" />
					</st:table>
		<%-- End : Changes in R5 --%>			
				</c:when>
				<c:otherwise>
				<c:choose>
				<c:when test="${docType ne null}">
					<div class="failed" id="addDocmessagediv" style="display:block">No documents of document type <b><i>${docType}</i></b> exist in the Document Vault
					</div>
				</c:when>
				<c:otherwise>
					<div class="failed" id="addDocmessagediv" style="display:block">No documents exist in the Document Vault
					</div>
				</c:otherwise>
				</c:choose>
			</c:otherwise>
			</c:choose>
			</div>
		</div>
	</div>
	</div>
	<%-- Start : Changes in R5 --%>
	<div class="formcontainer">
	<div id="folderMain" style='display:none'>
	<div class="addDocmessagedivFolder" id="addDocmessagedivFolder"></div>
               <div>&nbsp;</div>
				 <div class="leftTreeForLink" id="leftTreeVault"></div>
                <div class="tWForLink" id="folderStructure">
                 <input type="hidden" value="${lbDocPresent}" id="isDocPresent" name="isDocPresent"/>
					<st:table objectName="documentList" cssClass="heading"
						alternateCss1="evenRows" alternateCss2="oddRows"
						pageSize='${allowedObjectCount}'>
						<st:property headingName="Document Name" columnName="documentTitle"
							align="right" size="20%">
							<st:extension
								decoratorClass="com.nyc.hhs.frameworks.grid.AddDocumentFromVaultExtension" />
						</st:property>
						<st:property headingName="Document Type" columnName="documentType"
							align="right" size="20%" />
						<st:property headingName="Modified" columnName="modifiedDate"
							align="right" size="10%" />
					</st:table>
					<c:if test="${fn:length(documentList) eq 0}">
					<!-- <div class="noRecord" id="noDocumentDiv">No Records Found</div> -->
					<div class="noRecordCityBudgetDiv-align noRecord">
				<img src='../framework/skins/hhsa/images/file_recyclebin.png' style='width:30px;height:30px;' class='blankDocumentList'/>
				<c:choose>
				<c:when test="${DOC_TYPE eq null or empty DOC_TYPE }">
				<p>No documents were found in this folder.</p>
				</c:when>
				<c:otherwise>
				<p>No documents of document type <b><i>${DOC_TYPE}</i></b> were found in this folder.</p>
				</c:otherwise>
				</c:choose>
				<br>
				<p>Please select another folder to locate your desired document.</p>
				</div>
				</c:if>
				</div>
				
</div>
</div>

	<div class="buttonholder"><input type="button"
		class="graybtutton" title="Cancel" value="Cancel" id="cancelAddDoc" />
		<c:if test="${documentList ne null and (fn:length(documentList)) > 0}">
			<input type="button" class="button" title="Select" value="Select" id="selectdoc"/>
	</c:if></div>
	<div style="display:none" class="noRecord" id="noDocumentDivForNoList">No Records Found</div>
<%-- End : Changes in R5 --%>
</form:form></div>