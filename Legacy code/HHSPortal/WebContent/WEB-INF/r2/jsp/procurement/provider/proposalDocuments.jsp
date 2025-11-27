<%@ taglib prefix="nav" uri="/WEB-INF/tld/navigationSM.tld"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib prefix="rule" uri="/WEB-INF/tld/rule-taglib.tld"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<fmt:setBundle basename="com/nyc/hhs/properties/messages"/>
<%-- Start : Changes in R5 --%>
<script type="text/javascript" src="../framework/skeletons/hhsa/js/jstree.min.js"></script> 
<%-- End : Changes in R5 --%>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/proposalDocuments.js"></script>
<%-- Code updated for R4 Starts --%>
<script type="text/javascript">
var contextPathVariablePath = "<%=request.getContextPath()%>";
 $(document).ready(function() {
	if('null' != '<%=request.getAttribute("message")%>' && '<%=request.getAttribute("messageType")%>' != 'confirmation'){
	$(".messagediv").html('${message}'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" onclick=\"showMe('messagediv', this)\" />");
	$(".messagediv").addClass('<%= request.getAttribute("messageType")%>');
	$(".messagediv").show();
	<%request.removeAttribute("message");%>
	<%session.removeAttribute("message");%>
}
	$('#deleteDoc').click(function() {
		pageGreyOut();
		$("#proposalDocsForm")
		.attr("action",
				$("#deleteDocument").val());
		$("#proposalDocsForm").submit();
	});
	$('#cancelDeleteDoc').click(function() {
		pageGreyOut();
		$("#proposalDocsForm")
		.attr("action",
				$("#rfpDocumentRender").val());
		$("#proposalDocsForm").submit();
	});
});

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
</script>
<%-- Code updated for R4 Ends --%>
<nav:navigationSM screenName="ProposalDocuments">
<portlet:defineObjects />
<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/css/style.css" type="text/css"></link>
	<portlet:renderURL var="editProposal" escapeXml="false">
	<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}" />						
	<portlet:param name="midLevelFromRequest" value="ProposalDetails" />
	<portlet:param name="action" value="rfpRelease" />
	<portlet:param name="render_action" value="procurementProposalDetails" />
	<portlet:param name="procurementId" value="${procurementId}" />
</portlet:renderURL>
	<portlet:actionURL var="uploadProposalDocumentUrl" escapeXml="false">
		<portlet:param name="action" value="rfpRelease"/>
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="proposalId" value="${proposalId}" />
		<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}" />
		<portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}" />
	</portlet:actionURL>
	<portlet:resourceURL var="addProposalDocumentResourceUrl" id="addProposalDocumentResource" escapeXml="false">
		<portlet:param name="action" value="rfpRelease"/>
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="proposalId" value="${proposalId}" />
		<portlet:param name="procurementDocId" value="${procurementDocId}" />
	</portlet:resourceURL>
	<portlet:renderURL var="rfpDocumentRenderUrl" escapeXml="false">
		<portlet:param name="action" value="rfpRelease" />
		<portlet:param name="render_action" value="procurementProposalDocumentList" />	
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="proposalId" value="${proposalId}" />
		<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}" />
		<portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}" />
	</portlet:renderURL>
	<portlet:actionURL var="viewDocumentInfoResourceUrl" escapeXml="false">
		<portlet:param name="action" value="rfpRelease" />	
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="proposalId" value="${proposalId}" />
		<portlet:param name="submit_action" value="viewDocumentInfo" />
	</portlet:actionURL>
	<portlet:actionURL var="deleteDocumentUrl" escapeXml="false">
		<portlet:param name="action" value="rfpRelease" />	
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="proposalId" value="${proposalId}" />
		<portlet:param name="submit_action" value="removeProposalDocumentFromList" />
	</portlet:actionURL>
	<portlet:renderURL var="proposalSummaryUrl" escapeXml="false">
		<portlet:param name="topLevelFromRequest" value="ProposalSummary" />						
		<portlet:param name="action" value="propEval" />
		<portlet:param name="render_action" value="proposalSummary" />
		<portlet:param name="procurementId" value="${procurementId}" />
</portlet:renderURL>
<portlet:renderURL var="proposalSubmitUrl" escapeXml="false">
		<portlet:param name="topLevelFromRequest" value="ProposalSummary" />						
		<portlet:param name="action" value="rfpRelease" />
		<portlet:param name="render_action" value="renderProviderProposal" />
		<portlet:param name="midLevelFromRequest" value="SubmitProposal" />
		<portlet:param name="procurementId" value="${procurementId}" />
</portlet:renderURL>
<portlet:resourceURL var="showProposalComments" id="showProposalComments" escapeXml="false">
	<portlet:param name="action" value="propEval" />	
	<portlet:param name="proposalId" value="${proposalId}"/>
</portlet:resourceURL>
<input type="hidden" name="editProposal" id="editProposal" value="${editProposal}"/>
<input type="hidden" value="${proposalSubmitUrl}" id="proposalSubmitUrl"/>
<input type="hidden" value="${uploadProposalDocumentUrl}" id="uploadProposalDocumentAction"/>
<input type="hidden" id="proposalSummaryUrl" value="${proposalSummaryUrl}"/>
<input type="hidden" value="${rfpDocumentRenderUrl}" id="rfpDocumentRender"/>
<input type="hidden" value="${deleteDocumentUrl}" id="deleteDocument"/>
<input type="hidden" value="${addProposalDocumentResourceUrl}" id="addProposalDocumentResource"/>
<%-- Form Data Starts This is the main form which will be displayed for S238--%>

<div id='tabs-container' class='clearHeight'>
<form action="${uploadProposalDocumentUrl}" method="post" name="proposalDocsForm" id="proposalDocsForm">
<d:content isReadOnly="${(accessScreenEnable eq false) and (procurementBean.status ne 7 and procurementBean.status ne 8)}">
<input type="hidden" value="${topLevelFromRequest}" id="topLevelFromRequest" name="topLevelFromRequest"/>
<input type="hidden" value="${midLevelFromRequest}" id="midLevelFromRequest" name="midLevelFromRequest"/>
<input type="hidden" value="${procurementId}" id="currentProcurementId" name="procurementId"/>
<input type="hidden" value="${viewDocumentInfoResourceUrl}" id="viewDocumentInfoResource"/>
<input type="hidden" value="${proposalId}" id="proposalId" name="proposalId"/>
<input type="hidden" value="" id="docType" name="docType"/>
<input type="hidden" value="" id="hiddendocRefSeqNo" name="hiddendocRefSeqNo"/>
<input type="hidden" value="" id="deleteDocumentId" name="deleteDocumentId"/>
<input type="hidden" value="" id="uploadingDocumentType" name="uploadingDocumentType"/>
<input type="hidden" id="hiddenDocumentId" value="" name="documentId"/>
<input type="hidden" id="hiddenDocumentStatus" value="" name="docStatus"/>
<%-- Start : Changes in R5 --%>
<input type="hidden" name="docTypeHidden" id="docTypeHidden" value='' />
<%-- End : Changes in R5 --%>
<input type="hidden" value="${proposalId}" name="proposalId"/>
<jsp:useBean id="document" class="com.nyc.hhs.model.ExtendedDocument" scope="request"></jsp:useBean>
<h2>
	<label class='floatLft'>
		Proposal Documents: <label>${proposalTitle}</label>
	</label>
	<a id="returnProposalSummaryPageDocuments"  class="floatRht returnButton" href="javascript:;">Proposal Summary</a>
</h2>
<c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
		<d:content section="${helpIconProvider}">
			<div id="helpIcon" class="iconQuestion"><a href="javascript:void(0);" title="Need Help?" onclick="smFinancePageSpecificHelp();"></a></div>
			<input type="hidden" id="screenName" value="Proposal Documents" name="screenName"/>
		</d:content>
	<%-- Below div will be displayed if the rule returns true --%>
	<rule:Rule ruleId="showProposalCommentLinkPropDetalis" requestAttName="statusChannel">
		<c:set var="showCommentLink" value="true"></c:set>
	</rule:Rule>
<div class='hr'></div>
<input type = 'hidden' value='${showProposalComments}' id='showProposalCommentsResourceUrl'/>
<c:if test="${(accessScreenEnable eq false) and (procurementBean.status ne 7 and procurementBean.status ne 8)}">
	<c:set var="lockAccess"><fmt:message key='SCREEN_LOCKED'/></c:set>
	<div class="lockMessage" style="display:block">${lockedByUser} ${lockAccess}</div>
</c:if>
<c:if test="${showAddendumDocMessage eq true and information eq null}">
	<div class="infoMessage" id="addendumMessageDiv" style="display:block">${errorMessage} <img
					src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
					class="message-close" onclick="showMe('addendumMessageDiv', this)">
				</div>
</c:if>
<c:if test="${information ne null}">
	<div class="infoMessage" id="infoMessageDiv" style="display:block">${information} <img
					src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
					class="message-close" onclick="showMe('infoMessageDiv', this)">
				</div>
</c:if>
<div class="messagediv" id="messagediv"></div>	

<p>Use this section to upload Proposal documents. You may select documents already stored in your Document Vault or upload new ones. 
   You will not be able to submit your Proposal unless all required documents are uploaded.
 </p>
 <%-- Below div will displayed if the showCommentLink in request is true --%>
	<c:if test="${showCommentLink eq 'true'}">
		<p><a href='javascript:;' id="showProposalComment" class='iconComments'>Show Proposal Comments</a></p>
	</c:if>
	
		<h3>Required Documents</h3>
		<%-- below is the tabular div which is generated by the grid tag handler depending upon the document list --%>
		<div class="tabularWrapper">
			<st:table objectName="requiredProposalDocumentList"
				cssClass="heading" alternateCss1="evenRows" alternateCss2="oddRows">
				<st:property headingName="Document Name" columnName="documentTitle"
				align="center" size="15%">
				<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.ProposalDocumentsExtensionForDocumentScreen" />
				</st:property>
				<st:property headingName="Document Type" columnName="customLabelName"
				 align="right" size="15%" />
				<st:property headingName="Status" columnName="documentStatus"
				align="right" size="15%" />
				<st:property headingName="Last<br> Modified" columnName="modifiedDate"
				 align="right" size="10%" />
				<st:property headingName="Last<br> Modified By" columnName="lastModifiedByName"
				align="right" size="10%"/>
				<st:property headingName="Actions" columnName="actions" align="right"
				size="20%">
				<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.ProposalDocumentsExtensionForDocumentScreen" />
				</st:property>
			</st:table>
			<%-- if the required document list is null or empty --%>
			<c:if test="${requiredProposalDocumentList eq null or fn:length(requiredProposalDocumentList) eq 0}">
				<div class="messagedivNycMsg noRecord" id="messagedivNycMsg">No Required Documents have been added.</div>
			</c:if>
		</div>	
		<div>&nbsp;</div>
		<h3>Optional Documents</h3>
		<%-- below is the tabular div which is generated by the grid tag handler depending upon the document list --%>
		<div class="tabularWrapper">
			<st:table objectName="optionalProposalDocumentList"
			cssClass="heading" alternateCss1="evenRows" alternateCss2="oddRows">
				<st:property headingName="Document Name" columnName="documentTitle"
				align="center" size="15%">
				<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.ProposalDocumentsExtensionForDocumentScreen" />
				</st:property>
				<%--Release 3.6.0 Enhancement 6485, columnName changed to customLabelName from documentType--%> 
				<st:property headingName="Document Type" columnName="customLabelName"
				 align="right" size="15%" />
				<st:property headingName="Status" columnName="documentStatus"
				align="right" size="15%" />
				<st:property headingName="Last<br> Modified" columnName="modifiedDate"
				 align="right" size="10%" />
				<st:property headingName=" Last<br> Modified By" columnName="lastModifiedByName"
				align="right" size="10%" />
				<st:property headingName="Actions" columnName="actions" align="right"
				size="20%">
				<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.ProposalDocumentsExtensionForDocumentScreen" />
				</st:property>
			</st:table>
			<%-- if the optional document list is null or empty --%>
			<c:if test="${optionalProposalDocumentList eq null or fn:length(optionalProposalDocumentList) eq 0}">
				<div class="messagedivNycMsg noRecord" id="messagedivNycMsg">No optional documents were selected for this procurement.</div>
			</c:if>
		</div>
		<div class="buttonholder txtCenter">
			<input type="button" class="button" id="backButton" value="Back" />
			<c:if test="${(procurementStatus eq '3' and proposalStatusId eq '17') or proposalStatusId eq '19'}">
			<c:set var="sectionBackButton"><%=HHSComponentMappingConstant.S212_BACK_BUTTON%></c:set>
			<d:content section="${sectionBackButton}">
				<input type="button" class="button" id="nextButton" value="Next" />
			</d:content>
		</div>
		</c:if>
	</d:content>
</form>
</div>		
</nav:navigationSM>


<div class="overlay"></div>
<%-- this div is opened when the user select upload document button--%>
<div class="alert-box alert-box-upload">
	<div class="content">
	<%-- Start : Changes in R5 --%>
		<div id="newTabs"  class='wizardTabs wizardUploadTabs-align'>
			<div class="tabularCustomHead">Upload Document</div> 
			<h2 class='padLft'>Upload Document</h2>
			<div class='hr'></div>
			<c:if test="${message ne null}">
				<div class="${messageType}" id="messagediv" style="display:block">${message}<img src="../framework/skins/hhsa/images/iconClose.jpg" id="box" class="message-close"  onclick="showMe('messagediv', this)"></div>
			</c:if>
			      <ul id='proposaluploadDoc'>
					<li id='step1' class='active'>Step 1: File Selection</li>
					<li id='step2' style="padding-left:25px;">Step 2: Document Information</li>
					<li id='step3' class="last">Step 3: Document Location</li>
				</ul>
	       	<div id="tab1"></div>
		    <div id="tab2"></div>
		    <div id="tabnew"></div>
		</div>
	</div>
	<a  href="javascript:void(0);" class="exit-panel upload-exit" >&nbsp;</a>
</div>
<%-- this div is opened when the user select upload document button--%>
<div class="alert-box alert-box-upload">
	<div class="content">
	<%-- Start : Changes in R5 --%>
		<div id="newTabs"  class='wizardTabs'>
			<div class="tabularCustomHead">Upload Document</div> 
			<h2 class='padLft'>Upload Document</h2>
			<div class='hr'></div>
			<c:if test="${message ne null}">
				<div class="${messageType}" id="messagediv" style="display:block">${message}<img src="../framework/skins/hhsa/images/iconClose.jpg" id="box" class="message-close"  onclick="showMe('messagediv', this)"></div>
			</c:if>
		</div>
		<div id="tab1"></div>
	    <div id="tab2"></div>
	    <div id="tabnew"></div>
	    <%-- End : Changes in R5 --%>
	</div>
	<a  href="javascript:void(0);" class="exit-panel upload-exit" >&nbsp;</a>
</div>
<%-- this div is opened when the user click on select document from vault  --%>
<div class="alert-box alert-box-addDocumentFromVault">
		<div class="content">
				<%-- Start : Changes in R5 --%>
				<div class="tabularCustomHead">Select Existing Document from Document Vault</div>
				<%-- End : Changes in R5 --%>
				<div id="addDocumentFromVault"></div>
		</div>
		<a  href="javascript:void(0);" class="exit-panel upload-exit" >&nbsp;</a>
</div>
<%-- this div will be opened when user select view document properties from the dropdown option --%>	
<div class="alert-box alert-box-viewDocumentProperties">
		<div class="content">
		
				<div class="tabularCustomHead">View Document Information</div>
				<div id="viewDocumentProperties"></div>
		
		</div>
		<a  href="javascript:void(0);" class="exit-panel docinfo-exit" onclick="navigateToMain();">&nbsp;</a>
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
						<input type="button"  class="graybtutton" id="cancelDeleteDoc" value="No" />
						<input type="button"  class="button" id="deleteDoc" value="Yes" />
					</div>
				</div>
			</div>
		</div>
		<a  href="javascript:void(0);" class="exit-panel" >&nbsp;</a>
</div>
<%-- this div will be opened when user click on help icon on the screen --%>	

<div class="alert-box-contact">
				<div id="contactDiv"></div>
</div>
<div id="overlayedJSPContent" style="display:none"></div>
<%-- Pop up starts for Comments--%>
<div class="alert-box alert-box-proposal-comments" id="overlayDivId">
</div>
<%-- Pop up ends for Comments--%>
