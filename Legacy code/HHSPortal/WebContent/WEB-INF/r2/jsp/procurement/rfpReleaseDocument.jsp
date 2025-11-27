<%@page import="java.util.ArrayList"%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page errorPage="/error/errorpage.jsp"%>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@ taglib prefix="nav" uri="/WEB-INF/tld/navigationSM.tld"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="com/nyc/hhs/properties/messages"/>
<portlet:defineObjects />
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/resources/js/calendar.js"></script>
<script type="text/javascript" src="../framework/skeletons/hhsa/js/jstree.min.js"></script> 
<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/framework/skins/hhsa/css/jstree.style.min.css" type="text/css"></link>
<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/css/style.css" type="text/css"></link>
<style>
.alert-box {
	background: #FFF;
	display: none;
	z-index: 1001;
}
.alert-box .sub-started a {
	color: #fff;
}
.alert-box .sub-notstarted {
	background-image: none;
	color: #fff;
}
#newTabs #main-wrapper {
	width: 880px;
	padding: 0;
}
#newTabs .bodycontainer {
	width: 880px;
	background: #fff;
	padding: 0;
}
</style>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/rfpReleaseDocument.js"></script>
<script type="text/javascript">
var contextPathVariable = "<%=request.getContextPath()%>";
$(document).ready(function() {
	
		if('null' != '<%=request.getAttribute("message")%>' && '<%=request.getAttribute("messageType")%>' != 'confirmation'
				&& ''!='<%=request.getAttribute("message")%>'){
		$(".messagediv").html('${message}'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\"  onclick=\"showMe('messagediv', this)\" />");
		$(".messagediv").addClass('<%= request.getAttribute("messageType")%>');
		$(".messagediv").show();
		<%request.removeAttribute("message");%>
		<%session.removeAttribute("message");%>
	}
		$('#deleteDoc').click(function() {
			pageGreyOut();
			$("#rfpReleaseForm").attr("action", $("#deleteRfpDocument").val());
			document.rfpReleaseForm.submit();
		});
		$('#deleteNo').click(function() {
			$("select").each(function() {
				document.getElementById($(this).attr("id")).selectedIndex = "";
			});
		});
	});
	// Code updated for R4 Starts 
	
function viewRFPDocument(documentId, documentName){
	  var jqxhr = $.ajax( {
			url : $("#contextPathSession").val()+"/GetContent.jsp?action=checkDocExits&documentId="+documentId+"&documentName="+documentName+"&documentType=file",
			type : 'POST',
			success : function(data) {
				if(data == 'FilenotFound'){
					var _message= "The selected file has been deleted.";
					$(".messagediv").html(_message+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
			  		$(".messagediv").addClass("failed");
			  		$(".messagediv").show();
					
				}else{
					window.open($("#contextPathSession").val()+"/GetContent.jsp?action=displayDocument&documentId="+documentId+"&documentName="+documentName);
				}
			},
			error : function(data, textStatus, errorThrown) {
			},
			complete : function() {
			}
		});
}  
//Code updated for R4 Ends
</script>	
<nav:navigationSM screenName="RFPDocuments,RFPDocumentsHeader">
	<portlet:actionURL var="rfpReleaseDocumentUrl" escapeXml="false">
		<portlet:param name="action" value="rfpRelease" />	
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}" />
		<portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}" />
	</portlet:actionURL>
	<portlet:actionURL var="uploadRfpDocumentUrl" escapeXml="false">
		<portlet:param name="action" value="rfpRelease"/>
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="submit_action" value="uploadDocument" />
		<portlet:param name="uploadingDocumentType" value="RFP" />
	</portlet:actionURL>
	<portlet:resourceURL var="addRfpDocumentResourceUrl" id="addRfpDocumentResource" escapeXml="false">
		<portlet:param name="action" value="rfpRelease"/>
		<portlet:param name="procurementId" value="${procurementId}" />
	</portlet:resourceURL>
	<portlet:renderURL var="rfpDocumentRenderUrl" escapeXml="false">
		<portlet:param name="action" value="rfpRelease" />
		<portlet:param name="render_action" value="displayRFPDocumentList" />	
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}" />
		<portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}" />
	</portlet:renderURL>
	<portlet:actionURL var="viewDocumentInfoResourceUrl" escapeXml="false">
		<portlet:param name="action" value="rfpRelease" />	
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="submit_action" value="viewDocumentInfo" />
	</portlet:actionURL>
	<portlet:actionURL var="deleteRfpDocument" escapeXml="false">
		<portlet:param name="action" value="rfpRelease" />	
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}" />
		<portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}" />
		<portlet:param name="submit_action" value="removeDocumentFromList" />
		<portlet:param name="deleteDocumentId" value="${deleteDocumentId}" />
	</portlet:actionURL>
	<portlet:actionURL var='backButton' escapeXml='false'>
   		<portlet:param name="submit_action" value="rfpDocumentsBackAction" />
		<portlet:param name="procurementId" value="${procurementId}" />
	</portlet:actionURL>
	<portlet:actionURL var='nextButton' escapeXml='false'>
		<portlet:param name="action" value="rfpRelease" />
   		<portlet:param name="submit_action" value="rfpDocumentsNextAction" />
		<portlet:param name="procurementId" value="${procurementId}" />
	</portlet:actionURL>
	
	<%-- Form Data Starts This is the main form which will be displayed for S212,S233--%>
	<form:form id="rfpReleaseForm" action="${rfpReleaseDocumentUrl}" method="post" commandName="rfpDocuments" name="rfpReleaseForm">
	<input type="hidden" value="${submit_action}" id="submit_action"/>
	<input type="hidden" value="${deleteRfpDocument}" id="deleteRfpDocument"/>
	<input type="hidden" value="${addRfpDocumentResourceUrl}" id="addRfpDocumentResource"/>
	<input type="hidden" value="${viewDocumentInfoResourceUrl}" id="viewDocumentInfoResource"/>
	<input type="hidden" value="${rfpDocumentRenderUrl}" id="rfpDocumentRender"/>
	<input type="hidden" value="${uploadRfpDocumentUrl}" id="uploadRfpDocumentAction"/>
	<input type="hidden" value="${procurementId}" id="currentProcurementId"/>
	<input type="hidden" value="" id="deleteDocumentId" name="deleteDocumentId"/>
	<input type="hidden" value="${topLevelFromRequest}" id="topLevelFromRequest" name="topLevelFromRequest"/>
	<input type="hidden" value="${midLevelFromRequest}" id="midLevelFromRequest" name="midLevelFromRequest"/>
	<input type="hidden" id="hiddenDocumentId" value="" name="documentId"/>
	<input type="hidden" id="hiddenDocumentStatus" value="" name="docStatus"/>
	<input type="hidden" id="hiddenAddendumType" value="" name="isAddendumType"/>
	<input type="hidden" id="hiddenDocReference" value="" name="hiddenDocReference"/>
	<%-- Code updated for R4 Starts --%>
	<input type="hidden" value="" id="docType" name="docType"/>
	<%-- Code changes for R5 Starts --%>
	<input type="hidden" value="" id="isFilter" name="isFilter"/>
	<input type="hidden" value="" id="uploadProcess" name="uploadProcess"/>
	<input type="hidden" value="" id="pageReadOnly" name="pageReadOnly"/>
	<input type="hidden" name="docTypeHidden" id="docTypeHidden" value='' />
	<input type="hidden" name="docCatHidden" id="docCatHidden" value='' />
	<%-- Code changes for R5 Ends --%>
	<%-- Code updated for R4 Ends --%>
	<c:if test="${org_type eq 'agency_org'}">
	<input type="hidden" id="isAgencyViewingRFPDoc" value="true" name="isAgencyViewingRFPDoc">
	</c:if>
		<div id='tabs-container'>
		<d:content isReadOnly="${((accessScreenEnable eq false) or hideExitProcurement) and (procurementBean.status ne 7 and procurementBean.status ne 8)}">
		<h2>RFP Documents</h2>
		<c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
		<d:content section="${helpIconProvider}">
			<div id="helpIcon" class="iconQuestion"><a href="javascript:void(0);" id="helpIconId" title='Need Help?'  onclick="smFinancePageSpecificHelp();"></a></div>
			<input type="hidden" id="screenName" value="RFP Documents" name="screenName"/>
		</d:content>	
		<div class='hr'></div>
		
		<c:set var="sectionUnpublishInformation"><%=HHSComponentMappingConstant.S212_UNPUBLISHED_INFORMATION%></c:set>
		<d:content section="${sectionUnpublishInformation}">
			<c:if test="${unPublishedDataMsg ne null}">
				<div class='infoMessage' style="display:block">${unPublishedDataMsg}</div>
			</c:if>
		</d:content>
		<c:if test="${(accessScreenEnable eq false) and (procurementBean.status ne 7 and procurementBean.status ne 8)}">
			<c:set var="lockAccess"><fmt:message key='SCREEN_LOCKED'/></c:set>
			<div class="lockMessage" style="display:block">${lockedByUser} ${lockAccess}</div>
		</c:if>
		<c:set var="sectionStaticText"><%=HHSComponentMappingConstant.S212_STATIC_TEXT%></c:set>
		<div class="messagediv" id="messagediv"></div>
		<d:content section="${sectionStaticText}">
			<div><p>Please upload the RFP and other relevant documents for Providers
					to refer to as they draft and submit Proposals.</p></div>
		</d:content>
		
		<div>&nbsp;</div>
		
		<c:set var="sectionButton"><%=HHSComponentMappingConstant.S212_BUTTON%></c:set>
			<d:content section="${sectionButton}">
				<c:if test="${showUploadDocument}">
					<div class="taskButtons floatRht">
						<input type="button" value="Add Document from Vault" class="addtoVault" onclick="javascript:void(0);" id="addDocument"> 
						<input type="button" value="Upload New Document" title="Upload document from desktop" class="upload" id="uploadDoc">
					</div>
				</c:if>
			</d:content>
			<c:set var="sectionProviderStaticText"><%=HHSComponentMappingConstant.S212_PROVIDER_STATIC_TEXT%></c:set>
			<d:content section="${sectionProviderStaticText}">
				<p>View RFP and other relevant documents by clicking links below.</p>
			</d:content>
		
		<%-- Grid table Starts from here and this grid will be generated by the grid tag controller--%>
		<div class="tabularWrapper clear"  style="min-height:420px">
		<div id="rfpDocreRenderId">
			
		<c:set var="sectionDocumentTable"><%=HHSComponentMappingConstant.S212_DOCUMENT_TABLE%></c:set>
		<d:content section="${sectionDocumentTable}">
			<st:table objectName="documentList"
					cssClass="heading" alternateCss1="evenRows" alternateCss2="oddRows">
				<st:property headingName="Document Name" columnName="documentTitle"
				align="center" size="15%">
				<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.RFPReleaseDocumentsExtension" />
				</st:property>
				<st:property headingName="Document Type" columnName="documentType"
				 align="right" size="15%" />
				<st:property headingName="Status" columnName="documentStatus"
				align="right" size="15%" />
				<st:property headingName="Modified" columnName="modifiedDate"
				 align="right" size="10%" />
				<st:property headingName="Modified By" columnName="lastModifiedByName"
				align="right" size="10%" />
				<st:property headingName="Actions" columnName="actions" align="right"
				size="20%">
				<st:extension
					decoratorClass="com.nyc.hhs.frameworks.grid.RFPReleaseDocumentsExtension" />
				</st:property>
			</st:table>
		</d:content>
		
		<%-- Below table will be generated when the logged in user belongs to provider organization--%>
		<c:set var="sectionProviderTable"><%=HHSComponentMappingConstant.S212_PROVIDER_TABLE%></c:set>
		<d:content section="${sectionProviderTable}">
			<st:table objectName="documentList"
						cssClass="heading" alternateCss1="evenRows" alternateCss2="oddRows">
					<st:property headingName="Document Name" columnName="documentTitle"
					align="center" size="15%">
					<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.RFPReleaseDocumentsExtension" />
					</st:property>
					<st:property headingName="Document Type" columnName="documentType"
					align="right" size="15%" />
					<st:property headingName="Last Modified Date" columnName="modifiedDate"
					align="right" size="10%" />
			</st:table>
		</d:content>
		
		<c:if test="${documentList eq null or (fn:length(documentList)) eq 0}">
		<div class="noRecordCityBudgetDiv noRecord">
			No documents have been uploaded yet...
		</div>
		</c:if>
			
		</div>	
		</div>
		<%-- grid div ends here--%>
		</d:content>	
		</div>
		
	</form:form>
	
</nav:navigationSM>
<%-- Below divs are used to launch overlays for diffent functionalities--%>
<div class="overlay"></div>
<%-- this div is opened when the user click on upload document button--%>
<div class="alert-box alert-box-upload">
		<div class="content">
			<%-- Start : Changes in R5 --%>
			<div id="newTabs"  class='wizardTabs wizardUploadTabs-align'>
				<div class="tabularCustomHead">Upload Document</div> 
				<h2 class='padLft'>Upload Document</h2>
				<div class='hr'></div>
				<ul>
					<li id='step1' class='active'>Step 1: File Selection</li>
					<li id='step2' style="padding-left:25px;">Step 2: Document Information</li>
					<li id='step3' class="last">Step 3: Document Location</li>
				</ul>
		       	<div id="tab1"></div>
		        <div id="tab2"></div>
		        <div id="tabnew"></div>
			</div>
			<%-- End : Changes in R5 --%>
		</div>
		<a  href="javascript:void(0);" id="exitUpload" class="exit-panel upload-exit" title="Exit">&nbsp;</a>
	</div>
<%-- this div is opened when the user click on select document from vault button --%>
<div class="alert-box alert-box-addDocumentFromVault">
		<div class="content">
				<div class="tabularCustomHead">Select Existing Document from Document Vault</div>
				<div id="addDocumentFromVault"></div>
		</div>
		<a  href="javascript:void(0);" class="exit-panel upload-exit" >&nbsp;</a>
</div>
<%-- this div will be opened when user select option to remove the document from list --%>
<div class="alert-box-delete">
		<div class="content">
		  	<div id="newTabs" class='wizardTabs'>
				<div class="tabularCustomHead">Remove Document from Procurement
					<a href="javascript:void(0);" class="exit-panel" ></a>
				</div>
				<div id="deleteDiv">
					<div class="pad6 clear promptActionMsg">Are you sure you want to remove this document? This will not delete the document from your vault.
					</div>
					<div class="buttonholder txtCenter">
						<input type="button"  class="graybtutton exit-panel"  id="deleteNo" value="No" />
						<input type="button"  class="button" id="deleteDoc" value="Yes" />
					</div>
				</div>
			</div>
		</div>
		<a  href="javascript:void(0);" class="exit-panel" >&nbsp;</a>
</div>
<div class="buttonholder txtCenter">
	<c:set var="sectionBackButton"><%=HHSComponentMappingConstant.S212_BACK_BUTTON%></c:set>
	<d:content section="${sectionBackButton}">
		<input type="button" title="Navigate to Services & Providers" class="button" id="backButton" value="Back" onclick="javascript:backButton();"/>
		<%-- Code updated for R4 Starts --%>
		<c:if test="${((procurementBean.status eq '3' ||
						procurementBean.status eq '4' ||
						procurementBean.status eq '5' ||
						procurementBean.status eq '6' ||
						procurementBean.status eq '7' ||
						procurementBean.status eq '8' 
						) &&
						(providerStatusId eq '9' ||
						providerStatusId eq '11' ||
						providerStatusId eq '12' ||
						providerStatusId eq '13' ||
						providerStatusId eq '14' 
						))}">
			<%-- Code updated for R4 Ends --%>
			<input type="button" value="Next"  id="nextButton" onclick="javascript:nextButton();"/>
		</c:if>
	</d:content>
</div>
<input type="hidden" value="${backButton}" id="backPageURL" name="backPageURL"/>
<input type="hidden" value="${nextButton}" id="nextPageURL" name="nextPageURL"/>
<%-- this div will be opened when user select view document properties from the dropdown option --%>		
<div class="alert-box alert-box-viewDocumentProperties">
		<div class="content">
		
				<div class="tabularCustomHead">View Document Information</div>
				<div id="viewDocumentProperties"></div>
		</div>
		<a  href="javascript:void(0);" class="exit-panel docinfo-exit" onclick="navigateToMain();">&nbsp;</a>
</div>

<div id="overlayedJSPContent" style="display:none"></div>