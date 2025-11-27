<%-- This jsp renders the provider's awards documents. The user can view the document and document information associated with each document --%>
<%@page import="javax.portlet.PortletContext"%>
<%@page import="java.util.ArrayList,com.nyc.hhs.constants.*"%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="render" uri="http://www.bea.com/servers/portal/tags/netuix/render" %>
<%@ page import="com.bea.netuix.servlets.controls.page.PagePresentationContext,
	com.bea.netuix.servlets.controls.page.BookPresentationContext"%>
<style>
.providerEvaluationHeader{
	height:auto;
}
.logoWrapper{
	float:none !important;
}
  #greyedBackground {
            position: fixed;
            top: 0;
            right: 0;
            bottom: 0;
            left: 0;
            margin: auto;
            margin-top: 0px;
            width: 100%;
            height: 100%;
            background : none repeat scroll 0 0 #fff;
            z-index: 9999;
			opacity: 0.8;
			filter: alpha(opacity = 80);
			text-align:center;
			vertical-align:middle;
        } 
 #greyedBackground img{
	background-color:#fff;
	top:48%;
	position:relative;
	opacity: 0.8;
 }
 

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
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/viewAwardDocuments.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/autoNumeric.js"></script>
<%-- Start : Changes in R5 --%>
<script type="text/javascript" src="../framework/skeletons/hhsa/js/jstree.min.js"></script>
<%-- End : Changes in R5 --%>
<script type="text/javascript">
var contextPathVariablePath = "<%=request.getContextPath()%>";
$(document).ready(function() {
	if('null' != '<%=request.getAttribute("message")%>' && '<%=request.getAttribute("messageType")%>' != 'confirmation'){
	$(".messagediv").html('${message}'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"\" alt=\"\" onclick=\"showMe('messagediv', this)\" />");
	$(".messagediv").addClass('<%= request.getAttribute("messageType")%>');
	$(".messagediv").show();
	<%request.removeAttribute("message");%>
	<%session.removeAttribute("message");%>
}
});
//Code updated for R4 Starts 
function viewRFPDocument(documentId, documentName){
		window.open(contextPathVariablePath+"/GetContent.jsp?action=displayDocument&documentId="+documentId+"&documentName="+documentName);
}
// Code updated for R4 Ends 
</script>
<portlet:defineObjects/>

<%--View Document Information Url Begins --%>
<portlet:actionURL var="viewDocumentInfoResourceUrl" escapeXml="false">
		<portlet:param name="action" value="rfpRelease" />	
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="submit_action" value="viewDocumentInfo" />
</portlet:actionURL>
<%-- Code updated for R4 Starts --%>
<%--View Document Information Url Ends --%>
<%--View All Download Document Information Url Begins --%>
<portlet:resourceURL var="downloadAllDocument"
	id="downloadAllDocuments" escapeXml="false">
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="budgetId" value="${awardBean.budgetId}" />
		<portlet:param name="contractId" value="${awardBean.contractId}" />
		<portlet:param name="awardId" value="${awardId}" />
		<portlet:param name="providerOrgID" value="${providerOrgID}" />
		<portlet:param name="procurementTitle" value="${awardBean.procurementTitle}" />
		<portlet:param name="action" value="selectionDetail" />	
</portlet:resourceURL>
<%--View All Download Document Information Url Ends --%>
<%-- Code updated for R4 Ends --%>
<%--View Contract COF Url Begins --%>
<portlet:actionURL var="viewProcurementCOF" escapeXml="false">
		<portlet:param name="action" value="rfpRelease" />	
		<portlet:param name="submit_action" value="showProcCOF" />
</portlet:actionURL>
<%--View Contract COF Url Ends --%>

<portlet:resourceURL var= "requestZip" id="requestZip" escapeXml='false'>
	<portlet:param name="action" value="rfpRelease"/>
	<portlet:param name="fileName" value='${awardDocDetails.documentTitle}'/>
	<portlet:param name="providerOrgID" value="${providerOrgID}" />
	<portlet:param name="procurementId" value="${procurementId}" />
	<portlet:param name="evaluationPoolMappingId" value="${evaluationPoolMappingId}" />
</portlet:resourceURL>
<%-- action URL for deleting agency award documents --%>
<%-- Code updated for Enhancement #6429 for Release 3.4.0 Starts--%>
<portlet:actionURL var="deleteDocumentUrl" escapeXml="false">
    <portlet:param name="action" value="rfpRelease"/>
	<portlet:param name="submit_action" value="removeAwardDocumentFromList" />
	<portlet:param name="deleteAgencyAward" value="1" />
	<portlet:param name="procurementId" value="${procurementId}" />
	<portlet:param name="evaluationPoolMappingId" value="${evaluationPoolMappingId}" />
	<portlet:param name="topLevelFromRequest" value="SelectionDetails" />
	<portlet:param name="evaluationPoolMappingId" value="${evaluationPoolMappingId}" />
	<portlet:param name="deleteDocumentId" value="${deleteDocumentId}" />
	<portlet:param name="docType" value="${docType}" />
	<portlet:param name="isFinancials" value="${isFinancials}" />
	<portlet:param name="organizationId" value="${providerOrgID}" />
	<portlet:param name="uploadingDocumentType" value="${uploadingDocumentType}" />
</portlet:actionURL>
<%-- Code updated for Enhancement #6429 for Release 3.4.0 Ends--%>

<%-- Add Award Document Action --%>
<%-- Code updated for Enhancement #6429 for Release 3.4.0 Starts--%>
	<portlet:actionURL var="addAwardDocumentUrl" escapeXml="false">
		<portlet:param name="action" value="rfpRelease"/>
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="evaluationPoolMappingId" value="${evaluationPoolMappingId}" />
		<portlet:param name="topLevelFromRequest" value="SelectionDetails" />
		<portlet:param name="isFinancials" value="${isFinancials}" />
		<portlet:param name="organizationId" value="${providerOrgID}" />
	</portlet:actionURL>
<%-- Code updated for Enhancement #6429 for Release 3.4.0 Starts--%>

<div style="display:none;" id="greyedBackground"><img src='../framework/skins/hhsa/images/loadingBlue.gif' /></div>
<div id="jsmessagediv" class="failed" style="display: none;">
				</div>

<input type="hidden" name="viewBudgetDocument" id="viewBudgetDocument" value="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_contract_budget_page&removeMenu=adas&_nfls=true&_urlType=render&_windowLabel=portletInstance_13"/>
<input type="hidden" name="viewContractCOF" id="viewContractCOF"  value="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_financials&removeMenu=adas&submit_action=showContractCOF&_nfls=true&_urlType=action&_windowLabel=portletInstance_37"/>		
<input type="hidden" id="proposalSummaryUrl" value="${proposalSummaryUrl}"/>
<input type="hidden" id="fileName" value="${awardDocDetails.documentTitle}"/>
<input type="hidden" id="providerOrgID" value="${providerOrgID}"/>
<input type="hidden" id="procurementId" value="${procurementId}"/>
<input type="hidden" id="evaluationPoolMappingId" value="${evaluationPoolMappingId}"/>
<input type="hidden" value="${viewDocumentInfoResourceUrl}" id="viewDocumentInfoResource"/>
<input type = 'hidden'  value="${requestZip}" id="hiddenRequestZipUrl" />
<input type = 'hidden'  value="${downloadZip}" id="hiddenDownloadZipUrl" />
<%--Form Tag Begins --%>
<form:form id="awardDocform" name="awardDocform" action="" method ="post" commandName="Procurement">
	<%-- Code updated for Enhancement #6429 for Release 3.4.0 Starts--%>
	<input type="hidden" value="${deleteDocumentUrl}" id="deleteDocument"/>
	<input type="hidden" value="" id="deleteDocumentId" name="deleteDocumentId"/>
	<input type="hidden" value="${addAwardDocumentUrl}" id="addAwardDocumentAction"/>
	<%-- Code updated for Enhancement #6429 for Release 3.4.0 Ends--%>
	<input type="hidden" value="" id="docType" name="docType"/>
	<input type="hidden" value="" id="hiddendocRefSeqNo" name="hiddendocRefSeqNo"/>
	<input type="hidden" value="${procurementId}" id="currentProcurementId" name="procurementId"/>
	<input type="hidden" id="hiddenDocumentId" value="" name="documentId"/>
	<input type="hidden" id="hiddenDocumentStatus" value="" name="docStatus"/>
	<input type="hidden" id="awardId" value="${awardId}" name="awardId"/>
	<input type="hidden" value="" id="uploadingDocumentType" name="uploadingDocumentType"/>
	<input type = 'hidden' value='${awardBean.contractId}' id='hdncontractId' name='hdncontractId' />
	<input type = 'hidden' value='${awardBean.contractStartDate}' id='hdncontractStartDt' name='hdncontractStartDt'/>
	<input type = 'hidden' value='${awardBean.contractEndDate}' id='hdncontractEndDt' name='hdncontractEndDt'/>
	<input type = 'hidden' value='${awardBean.budgetId}' id='hdnbudgetId' name='hdnbudgetId'/>
	<input type="hidden" id="asProcStatus" value="${asProcStatus}" name="asProcStatus"/>
	<%-- Start : Changes in R5 --%>
	<input type="hidden" name="docTypeHidden" id="docTypeHidden" value='' />
	<%-- End : Changes in R5 --%>
	<%-- Code updated for R4 Starts --%>
	<input type='hidden' value='${downloadAllDocument}' id='downloadDocumentUrl' />
	<input type='hidden' value='${awardBean.procurementTitle}' id='procurementTitle' />
	<input type='hidden' value='${isFinancials}' id='isFinancials' />
	<%-- Code updated for Enhancement #6429 for Release 3.4.0 Starts--%>
	<input type='hidden' value='${message}' id='message' />
	<%-- Code updated for Enhancement #6429 for Release 3.4.0 Ends--%>
	<input type='hidden' value='${downloadAllDocument}' id='downloadDocumentUrl' />
	<%--  Error Message shown below --%>
<div id="jsmessagediv" class="failed" style="display: none;">
				<div id="jsMessageContent"></div><img onclick="showMe('jsmessagediv', this)"  
					class="message-close" id="box"
					src="../framework/skins/hhsa/images/iconClose.jpg"></div>
					<%-- Code updated for R4 Ends --%>
	<%-- Form Data Starts --%>
	<div class='hr'></div>
	<div class="hhs_header providerEvaluationHeader providerEvaluationHeader1">
      <%-- Code updated for R4 Starts --%>
      <table width="100%">
      <tr>
      <td width="160px;">
                    <div class='logoWrapper logoWrapper1'></div>
        </td>
		<td style="width:400px;">
			<div class='headerFields'>
				<div class="print-td"><b>Procurement Title:</b> <label>${awardBean.procurementTitle}</label></div>
				<div class="print-td" class="content"><b>Provider Name: </b><label id="organisation_name">${organizaionName}</label></div>
				<%-- COMPETITION_POOL_TITLE and EVALUATION_GROUP_TITLE 
				are fetched as a part of R4 Open procurement change--%>
				<c:if test="${awardBean.isOpenEndedRFP eq '1'}">
					<div class="print-td"><b>Evaluation Group: </b> <label>${groupTitleMap['EVALUATION_GROUP_TITLE']}</label></div>
				</c:if>
				<div class="print-td"><b>Competition Pool: </b> <label>${groupTitleMap['COMPETITION_POOL_TITLE']}</label></div>
				<div class="print-td"><b>Award E-PIN:</b> <label>${awardBean.epin}</label></div>
			</div>
		</td>
		</tr>
	</table>
	<%-- Code updated for R4 Ends --%>	
	</div>
		
	<h2>
	<label class='floatLft'>View Award Documents</label>
	</h2>
	<div>	
	<c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
	<d:content section="${helpIconProvider}">
		<div id="helpIcon" class="iconQuestion"><a href="javascript:void(0);" title="Need Help?" onclick="smFinancePageSpecificHelp();"></a></div>
		<input type="hidden" id="screenName" value="View Award Documents" name="screenName"/>
	</d:content>	
	</div>
	<div class='hr'></div>
	<div class="formcontainer">
		<div class="row">
			  <span class="label">Contract ID:</span>
			   <span class="formfield">
			   		${awardBean.contractNumber}
			   </span>
		</div>
		<div class="row">
			  <span class="label">Award Amount ($):</span>
			  <span class="formfield" id="AwardAmountSpan">
					${awardBean.awardAmount}
			  </span>
		</div>
		<div class="row">
			  <span class="label">Status:</span>
			  <span class="formfield">
				  ${awardBean.contractStatus}
			  </span>
		</div>
	</div>
	<br/>
	
	<%-- Start of Changes done for Build 3.1.0 Enhancement 6025  : Table added for Download All Award Documents --%>
	<div id="reRenderDocId" style="display:block">
	<input type="hidden" id="docStatus" value="${awardDocDetails.statusId}"/>
	<c:if test="${failMsg ne null}">
		<div class="failed" id="failedMessageDiv" style="display: block">
			${failMsg} <img onclick="showMe('failedMessageDiv', this)"  
					class="message-close" id="box"
					src="../framework/skins/hhsa/images/iconClose.jpg">
		</div>
	</c:if>
	<c:if test="${successMsg ne null}">
		<div class="passed" id="passedMessageDiv" style="display: block">
			${successMsg} <img onclick="showMe('passedMessageDiv', this)"  
					class="message-close" id="box"
					src="../framework/skins/hhsa/images/iconClose.jpg">
		</div>
	</c:if>
	<%-- Code updated for Enhancement #6429 for Release 3.4.0 Starts--%>
	<div class="messagediv" id="messagediv"></div>
	<c:if test="${org_type ne 'city_org'}">
	<c:choose>
	<c:when test="${(awardBean.procurementStatus ne 7 and awardBean.procurementStatus ne 8)}">
	<div class="taskButtons floatRht">
		<input type="button" value="Add Award Document" title="Add Award Document" id="addAward" class="upload" onclick="addAwardDocument();"/>
	</div>
	</c:when>
	<c:otherwise>
	<div class="taskButtons floatRht">
	<input type="button" value="Add Award Document" title="Add Award Document" disabled="disabled" id="addAward" class="upload" onclick="addAwardDocument();"/>
	</div>
	</c:otherwise>
	</c:choose>
	</c:if>
	<%-- Code updated for Enhancement #6429 for Release 3.4.0 Ends--%>
	<h3>Download All Award Documents
	</h3>
	<div style="margin :4px;">Use the Actions drop down below to download a file containing all of the documents associated with this award.  
	If the file has not been requested or is expired, please select "Request New Zip File".  Please note: the zip file 
	may take a few minutes to generate before it is "Ready for Download".
	</div>
	<%-- Grid Starts --%>
		<div class="tabularWrapper">
			<table width="100%" cellspacing='0' cellpadding='0' border="1"  class="grid" id="financialDocTable">
				<thead>
					<tr>
						<th>File Name</th>
						<th>Date Generated</th>
						<th>Status</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td class="evenRows" align="center" width="30%">${awardDocDetails.documentTitle}</td>
						<c:choose>
							<c:when test="${awardDocDetails.statusId ne '162'}">
								<td width="30%">${awardDocDetails.modifiedDate}</td>
							</c:when>
							<c:otherwise>
								<td width="30%">----</td>
							</c:otherwise>
						</c:choose>
						<td width="30%">${awardDocDetails.status}</td>
						<td>
						<c:choose>
							<c:when test="${awardDocDetails.statusId ne '164'}">
								<select class=terms name=actions1 id=actions style='width: 200px' onChange="javascript: zipDocument(this)" >						 
									<c:if test="${awardDocDetails.statusId eq '161' or awardDocDetails.statusId eq '162'}">
										<option value="I need to..." >I need to...</option>
										<option>Request New Zip File</option>
									</c:if>
									<c:if test="${awardDocDetails.statusId eq '163'}"> 
										<option value="I need to..." >I need to...</option>
										<option>Request New Zip File</option>
										<option>Download Zip File</option>
									</c:if>
								</select> 
							</c:when>
							<c:otherwise>
								<select class=terms name=actions1 id=actions style='width: 200px' disabled="disabled">						 
										<option value="I need to..." >I need to...</option>
								</select> 
							</c:otherwise>
						</c:choose>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>
	
	<%-- End of Changes done for Build 3.1.0 Enhancement 6025 --%>
	
	<p><p>
	<%--View Award Documents Static Fields Ends --%>
	<%--Required Documentation Begins --%>
	<%-- Code updated for R4 Starts --%>
	<h3>Required Documentation
		<%--View Award Documents Static Fields Begins --%>
	</h3>
	<%-- Code updated for R4 Ends --%>	
	<%-- Grid Starts --%>
	<div class="tabularWrapper">
		<st:table objectName="awardConfigReqDocument"
			cssClass="heading" alternateCss1="evenRows" alternateCss2="oddRows">
			<st:property headingName="Document Name" columnName="documentTitle"
			align="center" size="30%">
			<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.AwardDocumentExtention" />
			</st:property>
			<st:property headingName="Document Type" columnName="documentType"
			 align="right" size="20%" />
			<st:property headingName="Last Modified" columnName="modifiedDate"
			 align="right" size="15%" />
			<st:property headingName="Last Modified By" columnName="lastModifiedByName"
			align="right" size="15%" />
			<st:property headingName="Actions" columnName="actions" align="right"
			size="20%">
			<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.ViewAwardDocumentExtention" />
			</st:property>
		</st:table>
	</div>
	<%--Required Documentation Ends --%>
	
	<p><p>
	
	<%--Optional Documentation Begins --%>
	<h3>Optional Documentation</h3>
	<div class="tabularWrapper">
	<c:choose>
            <c:when test="${fn:length(awardConfigReqDocument) gt 0 and fn:length(awardConfigOptDocument) eq 0}">
                  <st:table objectName="awardConfigOptDocument"
                        cssClass="heading" alternateCss1="evenRows" alternateCss2="oddRows">
                        <st:property headingName="Document Name" columnName="documentTitle"
                        align="center" size="30%">
                        </st:property>
                        <st:property headingName="Document Type" columnName="documentType"
                        align="right" size="20%" />
                        <st:property headingName="Last Modified" columnName="modifiedDate"
                        align="right" size="15%" />
                        <st:property headingName="Last Modified By" columnName="lastModifiedByName"
                        align="right" size="15%" />
                        <st:property headingName="Actions" columnName="actions" align="right"
                        size="20%">
                        </st:property>
                  </st:table>
                  <div class="messagedivNycMsg noRecord" id="messagedivNycMsg">No optional documents were selected for this procurement.</div>
            </c:when>
            <c:otherwise>
                  <st:table objectName="awardConfigOptDocument"
                        cssClass="heading" alternateCss1="evenRows" alternateCss2="oddRows">
                        <st:property headingName="Document Name" columnName="documentTitle"
                        align="center" size="30%">
                        <st:extension decoratorClass="com.nyc.hhs.frameworks.grid.AwardDocumentExtention" />
                        </st:property>
                        <st:property headingName="Document Type" columnName="documentType"
                        align="right" size="20%" />
                        <st:property headingName="Last Modified" columnName="modifiedDate"
                        align="right" size="15%" />
                        <st:property headingName="Last Modified By" columnName="lastModifiedByName"
                        align="right" size="15%" />
                        <st:property headingName="Actions" columnName="actions" align="right"
                        size="20%">
                        <st:extension decoratorClass="com.nyc.hhs.frameworks.grid.ViewAwardDocumentExtention" />
                        </st:property>
                  </st:table>
            </c:otherwise>
      </c:choose>

	</div>
	<%--Optional Documentation Ends --%>
	
	<p><p>
	<%--Financial Documentation Begins --%>	
	<c:if test="${isFinancials eq true}">
		<h3>Financial Documentation</h3>
		<%-- Grid Starts --%>
		<div class="tabularWrapper">
			<table width="100%" cellspacing='0' cellpadding='0' border="1"  class="grid" id="financialDocTable">
				<thead>
					<tr>
						<th>Document Name</th>
						<th>Document Type</th>
						<th>Last Modified</th>
						<th>Last Modified By</th>
						<th>Action</th>
					</tr>
				</thead>
				<tbody>
					<tr><%-- Code updated for R4 Starts --%>
						<td class="evenRows" align="center" width="30%">
							<c:if test="${contractBudgetApproved eq '0' and contractCofApproved eq '0'}">
								<a onclick="javascript: viewBudgetTypeDocument1();"  href="javascript:">Budget</a>
							</c:if>
						</td> <%-- Code updated for R4 Ends --%>
						<td>Budget</td>
						<td>${awardBean.budgetModifiedDate}</td>
						<td>${awardBean.budgetModifiedByStaffId}</td>
						<td class="floatRht">
							<select class=terms name=actions1 id=actions style='width: 200px' <c:if test="${not (contractBudgetApproved eq '0' and contractCofApproved eq '0')}"> disabled='disabled' </c:if> onChange="javascript: viewBudgetTypeDocument(this)">
								<option value="I need to..." >I need to...</option>
								<option>View Record</option>
							</select> 
						</td>
					</tr>
					<tr><%-- Code updated for R4 Starts --%>
						<td class="evenRows" align="center" width="30%">
							<c:if test="${contractCofApproved eq '0'}">
								<a onclick="javascript: viewFinancialContractDocument1();" href="javascript:">Certification of Funds</a>
							</c:if>
						</td> <%-- Code updated for R4 Ends --%>
						<td>Certification of Funds</td>
						<td>${awardBean.contractModifiedDate}</td>
						<td>${awardBean.contractModifiedBy}</td>
						<td class="floatRht">
							<select class=terms name=actions1 id=actions style='width: 200px' <c:if test="${contractCofApproved ne '0'}"> disabled='disabled' </c:if> onChange="javascript: viewFinancialContractDocument(this)">
								<option value="I need to..." >I need to...</option>
								<option>View Record</option>
							</select>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
	</c:if>
	<%-- Code updated for Enhancement #6429 for Release 3.4.0 Starts--%>
	<c:if test="${org_type ne 'city_org'}">
	<p><p>
	<%--Agency Documentation Begins --%>
	<h3>Agency Documentation</h3>
	•The documents below can only be viewed by Agency Users.
	<div class="tabularWrapper">
	<c:choose>
            <c:when test="${fn:length(agencyAwardDocList) eq 0}">
                  <st:table objectName="agencyAwardDocList"
                        cssClass="heading" alternateCss1="evenRows" alternateCss2="oddRows">
                        <st:property headingName="Document Name" columnName="documentTitle"
                        align="center" size="30%">
                        </st:property>
                        <st:property headingName="Document Type" columnName="documentType"
                        align="right" size="20%" />
                        <st:property headingName="Last Modified" columnName="modifiedDate"
                        align="right" size="15%" />
                        <st:property headingName="Last Modified By" columnName="lastModifiedByName"
                        align="right" size="15%" />
                        <st:property headingName="Actions" columnName="actions" align="right"
                        size="20%">
                        </st:property>
                  </st:table>
                  <div class="messagedivNycMsg noRecord" id="messagedivNycMsg">No agency documents were selected for this procurement.</div>
            </c:when>
            <c:otherwise>
                  <st:table objectName="agencyAwardDocList"
                        cssClass="heading" alternateCss1="evenRows" alternateCss2="oddRows">
                        <st:property headingName="Document Name" columnName="documentTitle"
                        align="center" size="30%">
                        <st:extension decoratorClass="com.nyc.hhs.frameworks.grid.AwardDocumentExtention" />
                        </st:property>
                        <st:property headingName="Document Type" columnName="documentType"
                        align="right" size="20%" />
                        <st:property headingName="Last Modified" columnName="modifiedDate"
                        align="right" size="15%" />
                        <st:property headingName="Last Modified By" columnName="lastModifiedByName"
                        align="right" size="15%" sortType="lastModifiedByName" sortValue="desc"/>
                        <st:property headingName="Actions" columnName="actions" align="right"
                        size="20%">
                        <st:extension decoratorClass="com.nyc.hhs.frameworks.grid.ViewAwardDocumentExtention" />
                        </st:property>
                  </st:table>
            </c:otherwise>
      </c:choose>

	</div>
	</c:if>
	<%--Agency Documentation Ends --%>
	<%-- Code updated for Enhancement #6429 for Release 3.4.0 Ends--%>
	<a style="visibility:hidden" id="providerHref" href="#" onclick="window.open('<render:standalonePortletUrl portletUri='/portlet/contractbudget/contractbudget.portlet'><render:param name='contractId' value='${awardBean.contractId}' /><render:param name='budgetId'  value='${awardBean.budgetId}'/><render:param name='viewBudgetDocument'  value='true'/></render:standalonePortletUrl>'); return false;"></a>
	<a style="visibility:hidden" id="CofHref" href="#" onclick="window.open('<render:standalonePortletUrl portletUri='/r2/portlet/finance/list/finance.portlet'><render:param name='hdncontractId' value='${awardBean.contractId}' /><render:param name='action'  value='contractListAction'/><render:param name='NAVIGATE_FROM_R2'  value='NAVIGATE_FROM_R2'/><render:param name='viewAwardDocument'  value='true'/><render:param name='hdncontractStartDt'  value='${awardBean.contractStartDate}'/><render:param name='hdncontractEndDt'  value='${awardBean.contractEndDate}'/><render:param name='removeMenu'  value='adas'/></render:standalonePortletUrl>'); return false;"></a>
	<%--Financial Documentation Ends --%>
</form:form>
<%--Form Tag Ends --%>

<%-- Code updated for Enhancement #6429 for Release 3.4.0 Starts--%>
<%-- This div is opened when the user select add Award Document button--%>
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
		   <%-- End : Changes in R5 --%>
		</div>
	</div>
	<a  href="javascript:void(0);" class="exit-panel upload-exit" >&nbsp;</a>
</div>
<%-- Code updated for Enhancement #6429 for Release 3.4.0 Ends--%>

<%--View Document Properties Overlay Begins --%>
<div class="overlay"></div>
<div class="alert-box alert-box-viewDocumentProperties">
	<div class="content">
		<div class="tabularCustomHead">View Document Information</div>
		<div id="viewDocumentProperties"></div>
	</div>
	<a  href="javascript:void(0);" class="exit-panel upload-exit" >&nbsp;</a>
</div>
<%--View Document Properties Overlay Ends --%>
<%--Procurement Help Documents Overlay Begins --%>
<div class="alert-box-help">
				<div class="tabularCustomHead toplevelheaderHelp"></div>
	            <div id="helpPageDiv"></div>
		  		<a  href="javascript:void(0);" class="exit-panel" >&nbsp;</a>
</div>
<%--Procurement Help Documents Overlay Ends --%>
<%-- Code updated for Enhancement #6429 for Release 3.4.0 Starts--%>
<%--Delete Agency Award Documents Overlay Begins --%>
<div class="alert-box-delete">
		<div class="content">
		  	<div id="newTabs" class='wizardTabs'>
				<div class="tabularCustomHead">Remove Document from Procurement
					<a href="javascript:void(0);" class="exit-panel"></a>
				</div>
				<div id="deleteDiv">
					<div class="pad6 clear promptActionMsg">Are you sure you want to remove this document? This will not delete the document from your vault.
					</div>
					<div class="buttonholder txtCenter">
						<input type="button" class="graybtutton exit-panel" value="No" />
						<input type="button" class="button" id="deleteDoc" value="Yes" />
					</div>
				</div>
			</div>
		</div>
		<a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
</div>
<%--Delete Agency Award Documents Overlay Ends --%>
<%-- Code updated for Enhancement #6429 for Release 3.4.0 Starts--%>
<div id="overlayedJSPContent" style="display:none"></div>
