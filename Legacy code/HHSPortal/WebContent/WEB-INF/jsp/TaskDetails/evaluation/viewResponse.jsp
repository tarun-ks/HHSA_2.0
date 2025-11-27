<%@ taglib prefix="nav" uri="/WEB-INF/tld/navigationSM.tld"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@page import="java.util.ArrayList, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/viewResponse.js"></script>
<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/framework/skins/hhsa/css/nycMain.css" media="all" type="text/css" />
<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/framework/skins/hhsa/css/nycMainR2.css" media="all" type="text/css" /> 
<portlet:defineObjects />
<portlet:actionURL var="viewDocuementInfo" escapeXml="false">
	<portlet:param name="action" value="propEval" />
</portlet:actionURL>

<%-- BAFO Upload Document Action Starts --%>
	<portlet:actionURL var="uploadBAFODocumentUrl" escapeXml="false">
		<portlet:param name="action" value="rfpRelease"/>
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="proposalId" value="${proposalId}" />
		<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}" />
		<portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}" />
	</portlet:actionURL>
<%-- BAFO Upload Document Action Ends --%>

<style>
.providerEvaluationHeader1{
	height:auto !important;
}
.logoWrapper1{
	float:none !important;
}
</style>

<script>
var contextPathVariablePath = "<%=request.getContextPath()%>";
function viewRFPDocument(documentId, documentName) {
	window.open(contextPathVariablePath
			+ "/GetContent.jsp?action=displayDocument&documentId=" + documentId
			+ "&documentName=" + documentName);
}
</script>
<%-- Form tag Starts --%>
<form:form id="viewResponseForm" name="viewResponseForm" action="${viewDocuementInfo}" method ="post" commandName="ProposalDetailsBean">
	<input type="hidden" id="hiddenDocumentId" value="" name="documentId"/>
	<input type="hidden" id="hiddenDocumentStatus" value="" name="docStatus"/>
	<input type="hidden" id="hiddenIsProcDocsVisible" value="" name="IsProcDocsVisible"/>
	<input type="hidden" value="" id="hiddendocRefSeqNo" name="hiddendocRefSeqNo"/>
	<input type="hidden" id="procurementId" value="${procurementId}" />
	<input type="hidden" id="jspPath" value="evaluation/" />
	<input type="hidden" value="" id="uploadingDocumentType" name="uploadingDocumentType"/>
	<input type="hidden" value="${uploadBAFODocumentUrl}" id="uploadProposalDocumentAction"/>
	<input type="hidden" value="" id="docType" name="docType"/>	
	<input type="hidden" value="${ProposalDetailsBean.proposalId}" id="proposalId" name="proposalId"/>
	<input type="hidden" value="${ProposalDetailsBean.organizationId}" id="organizationId" name="organizationId"/>
	<input type="hidden" value="${ProposalDetailsBean.providerContactId}" id="staffId" name="staffId"/>
	<input type="hidden" value="${ProposalDetailsBean.providerName}" id="userName" name="userName"/>
	<input type="hidden" value="${topLevelFromRequest}" id="topLevelFromRequest" name="topLevelFromRequest"/>
	<input type="hidden" value="${midLevelFromRequest}" id="midLevelFromRequest" name="midLevelFromRequest"/>
	<input type="hidden" id="fromAwardTask" value="true" />
	<%--View Response Header Begins --%>
		<div class="hhs_header providerEvaluationHeader providerEvaluationHeader1">
			<div class="hhs_header providerEvaluationHeader providerEvaluationHeader1">
      <table width="100%">
      <tr>
      <td width="160px;">
                    <div class='logoWrapper logoWrapper1'></div></td>
                    <td>
				<div class='headerFields'>
					<div class="print-td"><b>Procurement Title: </b> <label>${procurementTitle}</label></div>
					<div class="print-td"><b>Provider Name: </b> <label>${ProposalDetailsBean.organizationLegalName}</label></div>
					<!-- COMPETITION_POOL_TITLE and EVALUATION_GROUP_TITLE 
					are fetched as a part of R4 Open procurement change-->
					<c:if test="${ProposalDetailsBean.isOpenEndedRFP eq '1'}">
						<div class="print-td"><b>Evaluation Group: </b> <label>${ProposalDetailsBean.evaluationGroupTitle}</label></div>
					</c:if>
					<div class="print-td"><b>Competition Pool: </b> <label>${ProposalDetailsBean.competitionPoolTitle}</label></div>
					<div class="print-td"><b>Proposal Title: </b> <label>${ProposalDetailsBean.proposalTitle}</label></div>
				</div>
			</td>
			</tr>
			</table>
		</div>
	<%--View Response Header Ends --%>
		
	<%--Proposal Details Header --%>
	<h2>Proposal Details: 
		<span>
			<c:choose>
				<c:when test="${ProposalDetailsBean.proposalTitle eq null or ProposalDetailsBean.proposalTitle eq ''}">
					Untitled Proposal
				</c:when>
				<c:otherwise>
					${ProposalDetailsBean.proposalTitle}
				</c:otherwise>
			</c:choose>
		</span>
	</h2>
	<%--Proposal Details Header Ends--%>
	
	<div class='hr'></div>
    <c:if test="${message ne null}">
			<div class="${messageType}" id="messagediv" style="display:block">${message}<img src="../framework/skins/hhsa/images/iconClose.jpg" id="box" class="message-close" title="Close" alt="Close" onclick="showMe('messagediv', this)"></div>
	</c:if>
	<%--Provider Contact Starts --%>
    <h3>Provider Contact</h3>
		<div class="formcontainer">
			<div class="row">
				<span class="label">Name:</span>
				<span class="formfield">${ProposalDetailsBean.providerName}</span>
			</div>	
			<div class="row">
				<span class="label">Office Title:</span>
				<span class="formfield">${ProposalDetailsBean.providerOfficeTitle}</span>
			</div>
			<div class="row">
				<span class="label">Email Address:</span>
				<span class="formfield">${ProposalDetailsBean.providerEmailId}</span>
			</div>
			<div class="row">
				<span class="label">Phone:</span>
				<span class="formfield">${ProposalDetailsBean.providerPhone}</span>
			</div>
		</div>
		<%--Provider Contact Ends --%>
			<%--Service Unit Starts --%>
			<div>&nbsp;</div> 
		    <h3>Service Unit</h3>
		    <!-- Service Unit Start -->
		    <div class="formcontainer">
			    <c:if test="${ProposalDetailsBean.serviceUnitFlag eq '1'}">
			        <div class="row">
			            <span class="label">Total Number of Service Units:</span>
			            <span class="formfield">${ProposalDetailsBean.totalNumberOfService}</span>
			        </div>  
		        </c:if>
		        <div class="row">
					<span class="label">Total Funding Request($):</span>
					<span class="formfield" id="fundingSpan">
						 ${ProposalDetailsBean.totalFundingRequest}
					</span>
				</div>
				<c:if test="${ProposalDetailsBean.serviceUnitFlag eq '1'}">
			        <div class="row">
			            <span class="label">Cost per Service Unit($/unit):</span>
		            	<span>
							<fmt:formatNumber maxFractionDigits="2" type="number" value="${ProposalDetailsBean.totalFundingRequest/ProposalDetailsBean.totalNumberOfService}" />
						</span>
					</div> 
				</c:if>
		   </div>
		   <%--Service Unit End --%>
	   <%--Questions Starts --%>
	   <div>&nbsp;</div>
	    <h3>Questions</h3>
	    <!-- Questions Start -->
	    <div class="formcontainer">
	    	<c:forEach var="questionAnswerList" items="${ProposalDetailsBean.questionAnswerBeanList}">
		    	
					<div class="row">
			            <span class="label">${questionAnswerList.questionText} :</span>
			            <span class="formfield">${questionAnswerList.answerText}</span>
			        </div> 
				
			</c:forEach>
		</div>
	   <%--Questions End --%>
	   
   		<%-- Site Information Starts --%>
	   <div>&nbsp;</div>
	    <h3>Site Information</h3>
			<!-- Site Information Start -->
			<div class="tabularWrapper">
					<table width="100%" cellspacing='0' cellpadding='0' border="1"  class="grid" id="siteDetailsTable">
						<thead>
							<tr>
								<th>Site Name</th>
								<th>Address 1</th>
								<th>Address 2</th>
								<th>City</th>
								<th>State</th>
								<th>Zip Code</th>
							</tr>
						</thead>
						<tbody>
						
							<c:forEach var="siteList" items="${ProposalDetailsBean.siteDetailsList}">
								<tr>
									<td>${siteList.siteName}</td>
									<td>${siteList.address1}</td>
									<td>${siteList.address2}</td>
									<td>${siteList.city}</td>
									<td>${siteList.state}</td>
									<td>${siteList.zipCode}</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
   		<%-- Site Information End --%>
		<div>&nbsp;</div>
		<%-- Proposal Document Starts --%>
		<h2>Proposal Documents</h2>
	    <div class="hr"></div>
		<div id='ProposalDocuments'>
			<div class="tabularWrapper">
				<st:table objectName="proposalDocumentDetailList" cssClass="heading" alternateCss1="evenRows" alternateCss2="oddRows">
					<st:property headingName="Document Name" columnName="documentTitle" align="center" size="20%">
						<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.ProposalDocumentsExtension" />
					</st:property>
					<st:property headingName="Document Type" columnName="documentType" align="right" size="20%" />
					<st:property headingName="Required/Optional?" columnName="isRequiredDoc" align="right" size="20%" />
					<st:property headingName="Document Info" columnName="documentId" align="right" size="20%">
						<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.ProposalDocumentsExtension" />
					</st:property>
				</st:table>
				<c:if test="${proposalDocumentDetailList eq null or fn:length(proposalDocumentDetailList) eq 0}">
					<div class="messagedivNycMsg" id="messagedivNycMsg" style='border:1px solid #ccc; border-top:0; padding:6px;' ><i>No Documents found.</i></div>
				</c:if>
			</div>				
		</div>
		<%-- Proposal Document End --%>
</form:form>	
<%-- Form tag Ends --%>
	
<%-- Overlay Starts --%>
<div class="overlay"></div>
<div class="alert-box alert-box-viewDocumentProperties">
	<div class="content">
			<div class="tabularCustomHead">View Document Information</div>
			<div id="viewDocumentProperties"></div>
	</div>
	<a  href="javascript:void(0);" class="exit-panel upload-exit" title="Exit">&nbsp;</a>
</div>
<%-- This div is opened when the user select upload BAFO document button--%>
<div class="alert-box alert-box-upload">
	<div class="content">
		<div id="newTabs"  class='wizardTabs'>
			<div class="tabularCustomHead">Upload Document</div> 
			<h2 class='padLft'>Upload Document</h2>
			<div class='hr'></div>
			<ul>
				<li id='step1' class='active'>Step 1: File Selection</li>
				<li id='step2' class="last">Step 2: Document Information</li>
			</ul>
	       	<div id="tab1"></div>
	        <div id="tab2"></div>
		</div>
	</div>
	<a  href="javascript:void(0);" class="exit-panel upload-exit" >&nbsp;</a>
</div>

<div id="overlayedJSPContent" style="display:none"></div>
<%-- Overlay Ends --%>


