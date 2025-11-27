<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page errorPage="/error/errorpage.jsp" %>
<%@page import="com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<portlet:defineObjects/>
<portlet:resourceURL var="fromStep1Sharing" id="fromStep1Sharing" escapeXml="false"></portlet:resourceURL>
<script type="text/javascript" src="../js/sharedocuments.js"></script>
<script type="text/javascript">
//on load function to perform various checks on loading of jsp
function onReady(){
		// This will execute when Next button is clicked from Share document Step1 screen

}
</script>
<div class="overlaycontent">
	<!--Start of R4 Document Vault changes: Component Mapping check added for Agency security matrix-->
	<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S039_PAGE, request.getSession()) || CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S033_AGENCY_PAGE, request.getSession())){%>
	<!--End of R4 Document Vault changes -->
	<!-- Sharing Overlay Step 4 -->
	<form id="share1" action="" method ="post" name="share1">
	<input type="hidden" id="fromStep1SharingForm" value="${fromStep1Sharing}"/>
	<input type="hidden" name="proNameString" value="" id="proNameString">
		<div class="wizardTabs">
				<p>You have selected the following folders and/or documents to grant "read-only" access to other NYC providers and/or NYC Agencies.</p>
				<p>- Click "Cancel" to return to the Document Vault page and change your folder and document selections</p>
				<p>- Click "Next" to continue with these folders and documents</p>
				<div class='hr'></div>
				<div class="tabularWrapper" id="tableContainerDiv" style="float:left;height: 250px !important;width:100%; overflow: auto;">
					<table>
                       <tr>
                             <th>Folder or Document Name</th>
                             <th>Document Type</th>
                       </tr>
                       <c:forEach items="${shareDocumentList}" var="shareDocumentList" varStatus="counter">
                             <tr class=${counter.index % 2 eq 0?'evenRows':'oddRows'}>
                             <c:choose>
                              <c:when test="${(shareDocumentList.folderCount eq 0) and (shareDocumentList.docType eq null)}">
                               <td>${shareDocumentList.docName} <font color="red">(empty)</font></td>
                               </c:when>
                               <c:otherwise>
                               			<td>${shareDocumentList.docName}</td>
                               		</c:otherwise>
                               </c:choose>
                                  <c:choose>
                                   <c:when test="${shareDocumentList.docType ne null}">
                               			<td>${shareDocumentList.docType}</td>
                               		</c:when>
                               		<c:otherwise>
                               			<td>Folder</td>
                               		</c:otherwise>	
                               			</c:choose>
                             </tr>
                       </c:forEach>
                    </table>
				</div>
				<div class="buttonholder">
						<input type="button" id="cancelshare1" value="Cancel" title="Cancel" class="graybtutton"/>
						<input type="button" value="Next" title="Next" id="nextshare1" style="margin-right: 20px;"/>
				</div>
		 </div>
	</form>
	<%}else{ %>
		<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
	<%} %>
</div>