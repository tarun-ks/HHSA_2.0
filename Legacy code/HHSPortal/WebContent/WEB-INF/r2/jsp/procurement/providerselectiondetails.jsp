<%--This jsp is used for Selection Details (S241) screen--%>
<%@page import="javax.portlet.PortletContext"%>
<%@page import="java.util.ArrayList,com.nyc.hhs.constants.*"%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@taglib prefix="nav" uri="/WEB-INF/tld/navigationSM.tld"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/providerselectiondetails.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/autoNumeric.js"></script>
<%-- Start : Changes in R5 --%>
<script type="text/javascript" src="../framework/skeletons/hhsa/js/jstree.min.js"></script> 
<%-- End : Changes in R5 --%>
<script type="text/javascript">
var contextPathVariablePath = "<%=request.getContextPath()%>";
$(document).ready(function() {
	if('null' != '<%=request.getAttribute("message")%>' && '<%=request.getAttribute("messageType")%>' != 'confirmation'){
	$(".messagediv").html('${message}'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\"  onclick=\"showMe('messagediv', this)\" />");
	$(".messagediv").addClass('<%= request.getAttribute("messageType")%>');
	$(".messagediv").show();
	<%request.removeAttribute("message");%>
	<%session.removeAttribute("message");%>
}
	$('#deleteDoc').click(function() {
		pageGreyOut();
		$("#awardDocform")
		.attr("action",$("#deleteAwardDocument").val());
		$("#awardDocform").submit();
	});
	$('#cancelDeleteDoc').click(function() {
		pageGreyOut();
		$("#awardDocform")
		.attr("action",
				$("#viewSelectionDetailsURL").val());
		$("#awardDocform").submit();
	});
});<%-- Code updated for R4 Starts --%>
function viewRFPDocument(documentId, documentName){
		window.open(contextPathVariablePath +"/GetContent.jsp?action=displayDocument&documentId="+documentId+"&documentName="+documentName);
}
<%-- Code updated for R4 Ends --%>
function viewRFPDocument(documentId, documentName){
		window.open(contextPathVariablePath +"/GetContent.jsp?action=displayDocument&documentId="+documentId+"&documentName="+documentName);
}
</script>
<style>
.blockMsg{
	top:25% !important;
	position:fixed !important
}
</style>
<nav:navigationSM screenName="SelectionDetailsScreen">
<portlet:defineObjects/>
<%--defining render url --%>
<portlet:actionURL var="selectionDetailUrl" escapeXml="false">
	<portlet:param name="action" value="rfpRelease"/>
	<portlet:param name="procurementId" value="${procurementId}" />
	<portlet:param name="awardId" value="${awardId}" />
	<portlet:param name="evaluationPoolMappingId" value="${evaluationPoolMappingId}" />
	<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}" />
	<portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}" />
</portlet:actionURL>
<%--defining resource url for add document --%>
<portlet:resourceURL var="addProposalDocumentResourceUrl" id="addProposalDocumentResource" escapeXml="false">
		<portlet:param name="action" value="rfpRelease"/>
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="proposalId" value="${proposalId}" />
		<portlet:param name="evaluationPoolMappingId" value="${evaluationPoolMappingId}" />
</portlet:resourceURL>
<%--defining resource url for view document info --%>
<portlet:actionURL var="viewDocumentInfoResourceUrl" escapeXml="false">
		<portlet:param name="action" value="rfpRelease" />	
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="submit_action" value="viewDocumentInfo" />
		<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}" />
		<portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}" />
</portlet:actionURL>
<%--defining action url for delete document --%>
	<portlet:actionURL var="deleteAwardDocument" escapeXml="false">
		<portlet:param name="action" value="rfpRelease" />	
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}" />
		<portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}" />
		<portlet:param name="awardId" value="${awardId}" />
		<portlet:param name="submit_action" value="removeAwardDocumentFromList" />
		<portlet:param name="deleteDocumentId" value="${deleteDocumentId}" />
		<portlet:param name="evaluationPoolMappingId" value="${evaluationPoolMappingId}" />
	</portlet:actionURL>
	<%--defining render url for fetching proposal summary --%>
	<portlet:renderURL var="proposalSummaryUrl" escapeXml="false">
		<portlet:param name="topLevelFromRequest" value="ProposalSummary" />						
		<portlet:param name="action" value="propEval" />
		<portlet:param name="render_action" value="proposalSummary" />
		<portlet:param name="procurementId" value="${procurementId}" />
	</portlet:renderURL>
	
	
	<portlet:renderURL var="viewSelectionDetailsURL" escapeXml="false">
		<portlet:param name="topLevelFromRequest" value="SelectionDetails" />
		<portlet:param name="midLevelFromRequest" value="SelectionDetailsScreen" />						
		<portlet:param name="action" value="selectionDetail" />
		<portlet:param name="render_action" value="viewSelectionDetails" />
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="evaluationPoolMappingId" value="${evaluationPoolMappingId}" />
	</portlet:renderURL>
	
	<input type="hidden" id="viewSelectionDetailsURL" value="${viewSelectionDetailsURL}"/>
	<input type="hidden" id="proposalSummaryUrl" value="${proposalSummaryUrl}"/>
<input type="hidden" value="${addProposalDocumentResourceUrl}" id="addAwardDocumentResource"/>
<input type="hidden" value="${selectionDetailUrl}" id="uploadAwardDocumentAction"/>
<input type="hidden" value="${deleteAwardDocument}" id="deleteAwardDocument"/>
<%-- Start : Changes in R5 --%>
<input type="hidden" value="${viewDocumentInfoResourceUrl}" id="viewDocumentInfoResource"/>
<%-- End : Changes in R5 --%>
<form:form id="awardDocform" name="awardDocform" action="${selectionDetailUrl}" method ="post" commandName="Procurement">
<input type="hidden" value="" id="docType" name="docType"/>
<input type="hidden" value="" id="deleteDocumentId" name="deleteDocumentId"/>
<input type="hidden" value="" id="uploadingDocumentType" name="uploadingDocumentType"/>
<input type="hidden" value="" id="hiddendocRefSeqNo" name="hiddendocRefSeqNo"/>
<input type="hidden" value="" id="hiddenIsDocRequired" name="hiddenIsDocRequired"/>
<input type="hidden" value="${procurementId}" id="currentProcurementId" name="procurementId"/>
<input type="hidden" id="hiddenDocumentId" value="" name="documentId"/>
<input type="hidden" id="hiddenDocumentStatus" value="" name="docStatus"/>
<input type="hidden" id="awardId" value="${awardId}" name="awardId"/>
<%-- Start : Changes in R5 --%>
<input type="hidden" name="docTypeHidden" id="docTypeHidden" value='' />
<%-- End : Changes in R5 --%>
	<%-- Form Data Starts --%>
	<h2>Selection Details
		<a id="returnSelectionDetailsSummary" class="floatRht returnButton" href="#">Selection Details Summary</a>
	</h2>
	<%-- below div is used to display help icon on the page --%>
	<c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
		<d:content section="${helpIconProvider}">
			<div id="helpIcon" class="iconQuestion"><a href="javascript:void(0);" id="helpIconId" title='Need Help?' onclick="smFinancePageSpecificHelp();"></a></div>
			<input type="hidden" id="screenName" value="Selection Details" name="screenName"/>
		</d:content>	
	<div class='hr'></div>
	<%-- below div is used to display user friendly message to the user --%>
	<div class="messagediv" id="messagediv"></div>
	<h3>Proposal Selected</h3>
	<p>
		Congratulations! One or more of your Proposals have been determined eligible for award. 
		Awards are subject to timely completion of contract negotiation, responsibility determination, and contract registration.	
	</p>
	<p>
	<portlet:renderURL var="selectionSummary" escapeXml="false">
		<portlet:param name="action" value="propEval"/>
		<portlet:param name="topLevelFromRequest" value="SelectionDetails"/>
		<portlet:param name="midLevelFromRequest" value="ProposalSummary"/>
		<portlet:param name="render_action" value="proposalSummary"/>
		<portlet:param name="procurementId" value="${procurementId}"/>
	</portlet:renderURL>
		You may visit the <a class="link" href="#" id="proposalSummaryLink">Proposal Summary</a> page to see your full list of Proposals with scoring and rank information.
	</p>
	<p>Please carefully review the information below and follow instructions to continue the award selection process and upload required documentation.</p>
	<h3>Award Details</h3>
	<%-- below div will display all the details regarding award selected --%>
	<div class="formcontainer">
		<div class="row">
		<%-- Code updated for R4 Starts --%>
			  <span class="label">Competition Pool:</span>
			  <span class="formfield">${groupTitleMap['COMPETITION_POOL_TITLE']}</span>
		</div>
		<%-- Added in R5 --%>
		<c:if test="${!(awardBean.status eq '180' || awardBean.status eq '181'|| awardBean.status eq '182')}">
		<%-- Added in R5 --%>
		<div class="row">
			  <span class="label">Award Amount ($):</span>
			  <span class="formfield awardAmt" >${awardBean.awardAmount}</span>
		<%-- Code updated for R4 Ends --%>
		</div>
		</c:if>
		<div class="row">
			  <span class="label">Award EPIN:</span>
			  <span class='formfield'>${awardBean.epin}</span>
		</div>
		<div class="row">
			  <span class="label">CT #:</span>
			  <span class='formfield'>${awardBean.contractNumber}</span>
		</div>
		<!-- Updated for R5 -->
		<div class="row">
			  <span class="label">Contract Status:</span>
			  <span class='formfield'><c:choose>
								<c:when test="${(awardBean.status eq '180' || awardBean.status eq '181'|| awardBean.status eq '182')}">Pending Final Award Amount</c:when>
								<c:otherwise>${awardBean.contractStatus}</c:otherwise>
							</c:choose></span>
		</div>
		<!-- Updated for R5 -->
	</div>
	<h3>Award Documents</h3>
	<p>Please click the links below to view the documents the Agency has attached to guide you through the next steps in the award selection process.</p>
	<%-- Grid Starts --%>
	<%-- below div is generated when  the award document list is not null or not empty --%>
				<div class="tabularWrapper">
			        <st:table objectName="awardDocumentList"  cssClass="heading"
						alternateCss1="evenRows" alternateCss2="oddRows">
						<st:property headingName="Document Name" columnName="documentTitle" align="center">
						<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.ProposalDocumentsExtensionForDocumentScreen" />
						</st:property>
							<st:property headingName="Document Type" columnName="documentType" 
								align="right" >
							</st:property>
						<st:property headingName="Last Modified" columnName="modifiedDate" 
							align="left"  >
						</st:property>
					</st:table>
				</div>
		<h3>Required Documentation</h3>
		<p>Please upload the following required documents. <b>As soon as you upload a document below, it will be submitted to the Agency.<b></p>	
		<%-- Grid Starts --%>
		<%-- below div is generated when  the required document list is not null or not empty --%>
			<div class="tabularWrapper">
		<st:table objectName="awardConfigReqDocument"
			cssClass="heading" alternateCss1="evenRows" alternateCss2="oddRows">
			<st:property headingName="Document Name" columnName="documentTitle"
			align="center" size="15%">
			<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.AwardDocumentExtention" />
			</st:property>
			<st:property headingName="Document Type" columnName="documentType"
			 align="right" size="15%" />
			<st:property headingName="Status" columnName="documentStatus"
			align="right" size="15%" />
			<st:property headingName="Last Modified" columnName="modifiedDate"
			 align="right" size="10%" />
			<st:property headingName="Last Modified By" columnName="lastModifiedByName"
			align="right" size="15%" />
			<st:property headingName="Actions" columnName="actions" align="right"
			size="20%">
			<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.AwardDocumentExtention" />
			</st:property>
			</st:table>
		</div>
		<p><p>
		<h3>Optional Documentation</h3>
		<p>Please upload the following optional documents. <b>As soon as you upload a document below, it will be submitted to the Agency.<b></p>
		
		<%-- Grid Starts --%>
		<%-- below div is generated when  the optional document list is not null or not empty --%>
		<div class="tabularWrapper">
		<c:choose>
		<c:when test="${fn:length(awardConfigReqDocument) gt 0 and fn:length(awardConfigOptDocument) eq 0}">
		<st:table objectName="awardConfigOptDocument"
			cssClass="heading" alternateCss1="evenRows" alternateCss2="oddRows">
			<st:property headingName="Document Name" columnName="documentTitle"
			align="center" size="15%">
			<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.AwardDocumentExtention" />
			</st:property>
			<st:property headingName="Document Type" columnName="documentType"
			 align="right" size="15%" />
			<st:property headingName="Status" columnName="documentStatus"
			align="right" size="15%" />
			<st:property headingName="Last Modified" columnName="modifiedDate"
			 align="right" size="10%" />
			<st:property headingName="Last Modified By" columnName="lastModifiedByName"
			align="right" size="15%" />
			<st:property headingName="Actions" columnName="actions" align="right"
			size="20%">
			<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.AwardDocumentExtention" />
			</st:property>
			</st:table>
		 <div class="messagedivNycMsg noRecord" id="messagedivNycMsg">No optional documents were selected for this award.</div>
        </c:when>
        <c:otherwise>
                  <st:table objectName="awardConfigOptDocument"
			cssClass="heading" alternateCss1="evenRows" alternateCss2="oddRows">
			<st:property headingName="Document Name" columnName="documentTitle"
			align="center" size="15%">
			<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.AwardDocumentExtention" />
			</st:property>
			<st:property headingName="Document Type" columnName="documentType"
			 align="right" size="15%" />
			<st:property headingName="Status" columnName="documentStatus"
			align="right" size="15%" />
			<st:property headingName="Last Modified" columnName="modifiedDate"
			 align="right" size="10%" />
			<st:property headingName="Last Modified By" columnName="lastModifiedByName"
			align="right" size="15%" />
			<st:property headingName="Actions" columnName="actions" align="right"
			size="20%">
			<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.AwardDocumentExtention" />
			</st:property>
			</st:table>
            </c:otherwise>
	   </c:choose>
		
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
		</div>
		<div id="tab1"></div>
	    <div id="tab2"></div>
	    <div id="tabnew"></div>
	    <%-- End : Changes in R5 --%>
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
						<input type="button"  class="graybtutton" id="cancelDeleteDoc" value="No" />
						<input type="button"  class="button" id="deleteDoc" value="Yes" />
					</div>
				</div>
			</div>
		</div>
		<a  href="javascript:void(0);" class="exit-panel" >&nbsp;</a>
</div>
<%-- this div is opened when the user click on select document from vault  --%>
<div class="alert-box alert-box-addDocumentFromVault">
		<div class="content">
				<div class="tabularCustomHead">Select Existing Document from Document Vault</div>
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
<%-- below div is used to content js content and access them from overlay --%>
<div id="overlayedJSPContent" style="display:none"></div>