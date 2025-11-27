<%-- This jsp displays the read only version of provider response --%>
<%@ taglib prefix="nav" uri="/WEB-INF/tld/navigationSM.tld"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@page import="java.util.ArrayList, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/viewResponse.js"></script>
<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/framework/skins/hhsa/css/nycMain.css" media="all" type="text/css" />
<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/framework/skins/hhsa/css/nycMainR2.css" media="all" type="text/css" /> 
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/framework/skeletons/hhsa/js/jstree.min.js"></script> 
<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/framework/skins/hhsa/css/jstree.style.min.css" type="text/css"></link>
<portlet:defineObjects />

<portlet:actionURL var="viewDocuementInfo" escapeXml="false">
	<portlet:param name="action" value="rfpRelease" />
</portlet:actionURL>
<%-- Code updated for R4 Starts --%>
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
input{
font-size:1em !important;
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
<%-- Code updated for R4 Ends --%>
<%-- Form tag Starts --%>
<form:form id="viewResponseForm" name="viewResponseForm" action="${viewDocuementInfo}" method ="post" commandName="ProposalDetailsBean">
	<input type="hidden" id="hiddenDocumentId" value="" name="documentId"/>
	<input type="hidden" id="hiddenDocumentStatus" value="" name="docStatus"/>
	<input type="hidden" id="hiddenIsProcDocsVisible" value="" name="IsProcDocsVisible"/>
	<input type="hidden" value="" id="hiddendocRefSeqNo" name="hiddendocRefSeqNo"/>
	<input type="hidden" id="procurementId" value="${procurementId}" />
	<input type="hidden" id="jspPath" value="${jspPath}" />
	<%-- Code updated for R4 Starts --%>
	<input type="hidden" value="" id="uploadingDocumentType" name="uploadingDocumentType"/>
	<input type="hidden" value="${uploadBAFODocumentUrl}" id="uploadProposalDocumentAction"/>
	<input type="hidden" value="" id="docType" name="docType"/>	
	<input type="hidden" value="${ProposalDetailsBean.proposalId}" id="proposalId" name="proposalId"/>
	<input type="hidden" value="${ProposalDetailsBean.organizationId}" id="organizationId" name="organizationId"/>
	<input type="hidden" value="${ProposalDetailsBean.providerContactId}" id="staffId" name="staffId"/>
	<!--Start || Changes done for enhancement QC : 5688 for Release 3.2.0-->
	<input type="hidden" id="controller_action" name="controller_action"/>
	<!--End || Changes done for enhancement QC : 5688 for Release 3.2.0-->	
	<input type="hidden" value="${ProposalDetailsBean.providerName}" id="userName" name="userName"/>
	<input type="hidden" value="${topLevelFromRequest}" id="topLevelFromRequest" name="topLevelFromRequest"/>
	<input type="hidden" value="${midLevelFromRequest}" id="midLevelFromRequest" name="midLevelFromRequest"/>
	<%-- Code updated for R4 Ends --%>
	<%--View Response Header Begins --%>
	<c:if test="${IsProcDocsVisible eq true or IsProcDocsVisible eq 'true'}">
		<div class="hhs_header providerEvaluationHeader providerEvaluationHeader1">
      <table width="100%">
      <tr>
      <td width="160px;">
                    <div class='logoWrapper logoWrapper1'></div></td>
                    <td>

				<div class='headerFields'>
					<div class="print-td"><b>Procurement Title: </b> <label>${procurementTitle}</label></div>
					<div class="print-td"><b>Provider Name: </b> <label>${ProposalDetailsBean.organizationLegalName}</label></div>
					<%-- Code updated for R4 Starts --%>
					<%-- COMPETITION_POOL_TITLE and EVALUATION_GROUP_TITLE 
					are fetched as a part of R4 Open procurement change--%>
					<c:if test="${ProposalDetailsBean.isOpenEndedRFP eq '1'}">
						<div class="print-td"><b>Evaluation Group: </b> <label>${ProposalDetailsBean.evaluationGroupTitle}</label></div>
					</c:if>
					<div class="print-td"><b>Competition Pool: </b> <label>${ProposalDetailsBean.competitionPoolTitle}</label></div>
					<%-- Code updated for R4 Ends --%>
					<div class="print-td"><b>Proposal Title: </b> <label>${ProposalDetailsBean.proposalTitle}</label></div>
				</div>
			</td>
			</tr>
			</table>
		</div>
	</c:if>
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
	<!-- D:content replaced by scriptlet for Enhancement #5688 for Release 3.2.0 -->
	<%if(CommonUtil.getConditionalRoleDisplay(HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP, request.getSession())){%>
			<div id="helpIcon" class="iconQuestion"><a href="javascript:void(0);" title="Need Help?" onclick="smFinancePageSpecificHelp();"></a></div>
			<input type="hidden" id="screenName" value="View Proposal" name="screenName"/>
    <%} %>
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
		    <%-- Service Unit Start --%>
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
	    <%-- Questions Start --%>
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
			<%-- Site Information Start --%>
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
		<c:if test="${IsProcDocsVisible eq true}">
		<%-- Code updated for R4 Starts --%>
		<span><h3 style="width:50%;">Proposal Documents</h3></span><span><%-- Upload BAFO Document Button  --%>
		    <%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_ACCO_MANAGER, request.getSession())){%>
		    	<c:if test="${(aoStatusFlag eq true) and (showBafoButton eq 'true')}">
				    <div class="taskButtons floatRht">
				    <input type="button" value="Upload BAFO Document" title="Upload BAFO Document" id="upload" class="upload" onclick="uploadDocument1();"/>
				    </div>
			    </c:if>	
		    <%} %></span>
		    <%-- Code updated for R4 Ends --%>
	    
		<div id='ProposalDocuments'>
				<div class="tabularWrapper">
					<st:table objectName="proposalDocumentDetailList" cssClass="heading" alternateCss1="evenRows" alternateCss2="oddRows">
						<st:property headingName="Document Name" columnName="documentTitle" align="center" size="20%">
							<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.ProposalDocumentsExtension" />
						</st:property>
						<%--Release 3.6.0 Enhancement 6485, columnName changed to customLabelName from documentType--%> 
						<st:property headingName="Document Type" columnName="customLabelName" align="right" size="20%" />
						<st:property headingName="Required/Optional?" columnName="isRequiredDoc" align="right" size="20%" />
						<st:property headingName="Document Info" columnName="documentId" align="right" size="20%">
							<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.ProposalDocumentsExtension" />
						</st:property>
					</st:table>
					<c:if test="${proposalDocumentDetailList eq null or fn:length(proposalDocumentDetailList) eq 0}">
						<div class="messagedivNycMsg noRecord" id="messagedivNycMsg">No Documents found.</div>
					</c:if>
				</div>				
			</div>
		</c:if>
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
<div class="alert-box-help">
	<div class="tabularCustomHead toplevelheaderHelp"></div>
    <div id="helpPageDiv"></div>
    <a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
</div>
<div class="alert-box-contact">
	<div id="contactDiv"></div>
</div>
<%-- Code updated for R4 Starts --%>
<%-- This div is opened when the user select upload BAFO document button--%>
<div class="alert-box alert-box-upload">
	<div class="content">
		<div id="newTabs"  class='wizardTabs wizardUploadTabs-align'>
				<div class="tabularCustomHead">Upload Document</div> 
				<h2 class='padLft'>Upload Document</h2>
				<div class='hr'></div>
				<ul>
					<li id='step1' class='active' style='margin-left:10px;'>Step 1: File Selection</li>
					<li id='step2' style="padding:0 16px;">Step 2: Document Information</li>
					<li id='step3' class="last" style="padding:0 10px;">Step 3: Document Location</li>
				</ul>
		       	<div id="tab1"></div>
		        <div id="tab2"></div>
		        <div id="tabnew"></div>
			</div>
	</div>
	<a  href="javascript:void(0);" class="exit-panel upload-exit" >&nbsp;</a>
</div>
<!-- Release 5 -->
<div>&nbsp;</div>
<span><h3 style="width:100%;">Proposal Activity History</h3></span>
<div class="tabularWrapper">
<st:table
              objectName="ProposalAuditList" cssClass="heading"
              alternateCss1="evenRows" alternateCss2="oddRows" >
              <st:property headingName="Task" columnName="taskName"
                                  size="20%"/>
              <st:property headingName="Action" columnName="action"
                                  size="20%"/>
              <st:property headingName="Detail" columnName="detail"
                                  size="20%"/>
              <st:property headingName="User" columnName="user"
                                  size="20%"/>
              <st:property headingName="Date/Time" columnName="dateTime"
                                  size="20%"/>
              
              </st:table>
</div>
 <!-- Release 5 -->
             <tbody>
             </tbody> 
<div id="overlayedJSPContent" style="display:none"></div>
<%-- Code updated for R4 End --%>