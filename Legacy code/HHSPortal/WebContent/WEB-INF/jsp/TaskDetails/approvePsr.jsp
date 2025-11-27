<%-- This JSP is for PSR complete details --%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>

<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@taglib prefix="render"
	uri="http://www.bea.com/servers/portal/tags/netuix/render"%>
<%@taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>

<portlet:defineObjects />

<script type="text/javascript"
	src="${pageContext.servletContext.contextPath}/resources/js/calendar.js"></script>
<%-- JQuery Grid links start--%>
<link rel="stylesheet" media="screen"
	href="${pageContext.servletContext.contextPath}/r2/css/ui.jqgrid.css"
	type="text/css"></link>
<script type="text/javascript"
	src="${pageContext.servletContext.contextPath}/r2/js/grid.locale-en.js"></script>
<script type="text/javascript"
	src="${pageContext.servletContext.contextPath}/r2/js/jquery.jqGrid.min.js"></script>
<script type="text/javascript"
	src="${pageContext.servletContext.contextPath}/r2/js/jquery.formatCurrency-1.4.0.js"></script>
<script type="text/javascript"
	src="${pageContext.servletContext.contextPath}/r2/js/jqgridDialogOveride.js"></script>
<script type="text/javascript"
	src="../resources/js/autoNumeric-1.7.5.js"></script>
<%-- JQuery Grid links end--%>
<%-- Accounts grid attributes starts --%>
<%-- Funds grid attributes starts --%>
	<portlet:resourceURL var='mainFundingGrid' id='mainFundingGrid' escapeXml='false'>
		<portlet:param name="screenName" value="financialsFundingGrid"/>
	</portlet:resourceURL>
	
	<portlet:resourceURL var='subFundingGrid' id='subFundingGrid' escapeXml='false'>
		<portlet:param name="screenName" value="financialsFundingGrid"/>
	</portlet:resourceURL>
	
	<portlet:resourceURL var='fundingOperationGrid' id='fundingOperationGrid' escapeXml='false'>
		<portlet:param name="screenName" value="financialsFundingGrid"/>
	</portlet:resourceURL>
<%-- Funds grid attributes ends --%>
<portlet:resourceURL var='mainAccountGrid' id='mainAccountGrid'
	escapeXml='false'>
</portlet:resourceURL>

<portlet:resourceURL var='subAccountGrid' id='subAccountGrid'
	escapeXml='false'>
	<portlet:param name="screenName" value="financialsAccountGrid" />
	<portlet:param name="procCerTaskScreen" value="true" />
</portlet:resourceURL>

<portlet:resourceURL var='accountOperationGrid'
	id='accountOperationGrid' escapeXml='false'>
	<portlet:param name="screenName" value="financialsAccountGrid" />
	<portlet:param name="procCerTaskScreen" value="true" />
</portlet:resourceURL>
<portlet:renderURL var="completePSRRenderUrl" escapeXml="false"></portlet:renderURL>
<portlet:resourceURL var="validateUser" id="validateUser"
	escapeXml="false">
</portlet:resourceURL>
<input type="hidden" name="validateUser" id="validateUser" value="${validateUser}" />
<input type="hidden" name="completePSRRenderUrl" id="completePSRRenderUrl" value="${completePSRRenderUrl}" />
<%-- Accounts grid attributes ends --%>
<style>
.Column2 {
	height: 607px;
	min-height: 207px !important;
}

.Column1 {
	min-height: 570px;
}

.popupPadding{
	padding: 0 10px 0 10px;
}
</style>
<portlet:defineObjects />
<portlet:resourceURL var='savePsrComments' id='savePsrComments'
	escapeXml='false'>
	<portlet:param name="controller_action" value="inboxControllerExtended" />
</portlet:resourceURL>
<input type='hidden' value='${savePsrComments}' id='savePsrComments' />
<portlet:resourceURL var='selectOverlayContent'
	id='selectOverlayContent' escapeXml='false'>
</portlet:resourceURL>
<input type='hidden' value='${selectOverlayContent}'
	id='hiddencancelProcurementOverayContentUrl' />

<script type="text/javascript"
	src="${pageContext.servletContext.contextPath}/r5/js/psrworkflow/approvePsr.js"></script>
<portlet:actionURL var="reassignApprovePsrTask" escapeXml="false">
	<portlet:param name="submit_action" value="reassignApprovePsrTask" />
	<portlet:param name="controller_action" value="inboxControllerExtended" />
</portlet:actionURL>
<portlet:actionURL var="returnApprovePSR" escapeXml="false">
	<portlet:param name="submit_action" value="returnApprovePSR" />
	<portlet:param name="controller_action" value="inboxControllerExtended" />
</portlet:actionURL>
<portlet:actionURL var="finishApprovePSR" escapeXml="false">
	<portlet:param name="submit_action" value="finishApprovePSR" />
	<portlet:param name="controller_action" value="inboxControllerExtended" />
</portlet:actionURL>
<%--resource URL to comments starts--%>
<portlet:resourceURL var="viewEvaluatorCommentsUrl"
	id="viewEvaluatorCommentsForReviewScore" escapeXml="false">
	<portlet:param name="procurementId" value="${procurementId}" />
	<portlet:param name="proposalId" value="${proposalId}" />
</portlet:resourceURL>

<%--actionURL to view Proposal Summary--%>
<portlet:actionURL var="viewProposalSummaryUrl" escapeXml="false">
	<portlet:param name="submit_action" value="viewProposalSummary" />
	<portlet:param name="procurementId"
		value="${taskDetailsBean.procurementId}" />
	<portlet:param name="proposalId" value="${taskDetailsBean.proposalId}" />
</portlet:actionURL>
<%--renderURL for rendering to Agency Task List screen--%>
<div>
	<form:form id="psrform" name="psrform" action="${finishApprovePSR}" method="post" >
		<div class="skipElementsInCompare">
			<input type="hidden" id="hiddenDocumentId" value="" name="documentId" />
			<input type="hidden" id="orgType" value="" name="orgType" /> 
			<input type="hidden" value="${reassignApprovePsrTask}" id="reassignApprovePsrTask" /> 
			<input type="hidden" value="${finishApprovePSR}" id="finishApprovePSR" />
			<input type="hidden" value="${returnApprovePSR}" id="returnApprovePSR" /> 
			<input type="hidden" value="${viewProposalSummaryUrl}" id="viewProposalSummaryUrl" /> 
			<input type="hidden" name="procurementId" value="${taskDetailsBean.procurementId}" id="procurementId" /> 
			<input type="hidden" name="comments" value="${comments}" id="comments"/> 
			<input type="hidden" name="workflowId" value="${workflowId}" /> 
			<input type="hidden" name="taskId" value="${taskId}" /> 
			<input type="hidden" id="reassignedToUserName" name="reassignedToUserName" value="" /> 
			<input type="hidden" id="proposalTaskStatus" name="proposalTaskStatus" value='${proposalTaskStatus}' />
			<input type="hidden" value="${screenReadOnly}" id="screenReadOnly" />
			<input type="hidden" value="${viewEvaluatorCommentsUrl}" id="evaluatorCommentsUrlId" />
			<input type="hidden" id="procurementSummaryURL"
				value="<render:standalonePortletUrl portletUri='/r2/portlet/procurement/procurement.portlet'><render:param name='topLevelFromRequest' value='ProcurementInformation' /><render:param name='midLevelFromRequest' value='ProcurementSummary' /><render:param name='procurementId' value='${taskDetailsBean.procurementId}' /><render:param name='render_action' value='viewProcurement' /><render:param name='hideExitProcurement' value='true' /></render:standalonePortletUrl>" />
		</div>
		<h2>
			<label class='floatLft'>Task Details: <label>${taskDetailsBean.taskType}
					- ${taskDetailsBean.procurementTitle}</label>
			</label> <span class="linkReturnVault floatRht"><a
				href="javascript:returnToAgencyTaskList('${workflowId}');">Return</a></span>
		</h2>
		<c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
		<d:content section="${helpIconProvider}">
			<div id="helpIcon" class="iconQuestion">
				<a href="javascript:void(0);" class="localTabs" title="Need Help?"
					onclick="smFinancePageSpecificHelp();"></a>
			</div>
		</d:content>
		<div class="complianceWrapper">
			<div id="ErrorDiv" class="failed"></div> 
			<c:if test="${errorMessage ne null}">
				<div class="failedShow" id="messagediverrorjs">
					<div style="color: #d63301;">${errorMessage}</div>
					<img src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/iconClose.jpg" id="box"
					class="message-close" 
						onclick="showMe('messagediverrorjs', this)">
				</div>
			</c:if>
			<%--Filter and Reassign section starts --%>
			<div class="tasktopfilter taskButtons skipElementsInCompare">
				<div class="taskfilter">
					<select id="reassignDropDown" name="reassignedTo"
						onchange="enableDisableReassignButton()">
						<option value=""></option>
						<c:forEach var="reAssignUser" items="${reassignUserMap}">
							<option value="${reAssignUser.key}">${reAssignUser.value}</option>
						</c:forEach>
					</select> <input type="button" id="reassignButton" value="Reassign Task" disabled="disabled" onclick="reassignTask()" />
				</div>
				<div class="taskreassign">
					<select id="finishtaskchild" name="finishtaskchild"
						onchange="enableFinishButton()">
						<option></option>
						<option id="Approved" value="Approved"> Approved </option>
						<option id="Returned" value="Returned"> Returned </option>
					</select> <input 
					class="button" type="submit" value="Finish Task" name="finish"
						id="approvePsrButton" onclick="checkFinishTask()" />
				</div>
			</div>
			<%--Filter and Reassign section ends --%>
			<%-- Left Column Start --%>
			<div class="Column1">
				<h4>Procurement Details</h4>
				<label>Procurement Title:</label>
				<div style="word-break: break-all">
					<a href='javascript:viewProcurementSummary();' class='localTabs'>${taskDetailsBean.procurementTitle}</a>
				</div>
				<c:if
					test="${(taskDetailsBean.isOpenEndedRfp ne null) and (taskDetailsBean.isOpenEndedRfp eq '1')}">
					<label>Evaluation Group:</label>
					<div style="word-break: break-all">${taskDetailsBean.evaluationGroupTitle}</div>
				</c:if>
				<label>Agency:</label>
				<div >${PsrDataBean.agencyName}</div>
				<label>Procurement E-PIN:</label>
				<div style="word-break: break-all">
					<c:choose>
							<c:when test="${PsrDataBean.procurementEpin ne null}">
								${PsrDataBean.procurementEpin}	
							</c:when>
							<c:otherwise>Pending</c:otherwise>
					</c:choose>
				</div>
				<%--code updation for R4 ends--%>
				<c:set var="string1"
					value="${PsrDataBean.agecncyPrimaryContact}" />
				<c:set var="string2" value="${fn:split(string1,'||')}" />
				<label>Agency Contact 1:</label>
				<div style="word-break: break-all">
					<a href="mailto:${string2[1]}" class='localTabs'>${string2[0]}</a>
				</div>
				<c:set var="string1"
					value="${PsrDataBean.agecncySecondaryContact}" />
				<c:set var="string2" value="${fn:split(string1,'||')}" />
				<label>Agency Contact 2:</label>
				<div style="word-break: break-all">
					<a href="mailto:${string2[1]}" class='localTabs'>${string2[0]}</a>
				</div>
				<c:if test="${(PsrDataBean.isOpenEndedRFP eq '0')}">
					<label>Total Award Amount:</label>
					<div style="word-break: break-all">
						<fmt:formatNumber type="currency"
							value="${PsrDataBean.estProcurementValue}" />
					</div>
				</c:if>
				<div></div>
				<h4>Task Details</h4>
				<label>Task Name:</label>
				<div>${taskDetailsBean.taskType}</div>
				<label>Task Instructions:</label>
				<div>Review PSR form</div>
				<label>Assigned To:</label>
				<div>${taskDetailsBean.assignedToUserName}</div>
				<label>Date Assigned:</label>
				<div>${taskDetailsBean.assignedDate}</div>
				<label>Last Modified:</label>
				<div>${taskDetailsBean.lastModifiedDate}</div>
			</div>
			<%-- Left Column End --%>
			<%-- Center Column Start --%>
		</div>

		<div class='Column2'>
			<h2>Pre-Solicitation Review</h2>
			<%-- New --%>
			<input type="hidden" id="psrPcofVersionNumber" name="psrPcofVersionNumber"
				value="${PsrDataBean.psrPcofVersionNumber}" />
			<input type="hidden" id="wobNumber" name="wobNumber"
				value="${wobNumber}" />
			<%-- Form Data Starts --%>
			<h3>Basic Information</h3>
			<div class="formcontainer">
				<div class="row">
					<span class="label">E-Pin:</span> <span class="formfield"> 
						<c:choose>
							<c:when test="${PsrDataBean.procurementEpin ne null}">
					${PsrDataBean.procurementEpin}	
							</c:when>
							<c:otherwise>Pending</c:otherwise>
						</c:choose>
					</span>
				</div>
				<div class="formcontainer">
					<div class="row">
						<span class="label">Procurement Title:</span> <span
							class="formfield"> ${PsrDataBean.procurementTitle} </span>
					</div>
					<%--make a check for the city users  --%>
					<div class="row">
						<span class="label" style="height : 30px;">Agency:</span> <span class="formfield">
							${PsrDataBean.agencyName} </span>
					</div>
					<div class="row">
						<span class="label">Program Name:</span> <span class="formfield">
							${PsrDataBean.programName} </span>
					</div>
					<c:set var="string1" value="${PsrDataBean.accPrimaryContact}" />
					<c:set var="string2" value="${fn:split(string1,'||')}" />

					<div class="row">
						<span class="label">Accelerator Primary Contact:</span> <span
							class="formfield"> <a href="mailto:${string2[1]}"
							class='localTabs'>${string2[0]}</a>
						</span>
					</div>
					<c:set var="string1" value="${PsrDataBean.accSecondaryContact}" />
					<c:set var="string2" value="${fn:split(string1,'||')}" />
					<div class="row">
						<span class="label">Accelerator Secondary Contact:</span> <span
							class="formfield"> <a href="mailto:${string2[1]}"
							class='localTabs'>${string2[0]}</a>
						</span>
					</div>
					<c:set var="string1"
						value="${PsrDataBean.agecncyPrimaryContact}" />
					<c:set var="string2" value="${fn:split(string1,'||')}" />
					<div class="row">
						<span class="label">Agency Primary Contact:</span> <span
							class="formfield"> <a href="mailto:${string2[1]}"
							class='localTabs'>${string2[0]}</a>
						</span>
					</div>
					<c:set var="string1"
						value="${PsrDataBean.agecncySecondaryContact}" />
					<c:set var="string2" value="${fn:split(string1,'||')}" />
					<div class="row">
						<span class="label">Agency Secondary Contact:</span> <span
							class="formfield"> <a href="mailto:${string2[1]}"
							class='localTabs'>${string2[0]}</a>
						</span>
					</div>
					<input type="hidden" id="mail" value="${PsrDataBean.email}">
					<div class="row">
						<span class="label">Agency Email Contact:</span> <span
							class="formfield"><a class="localTabs"href="mailto:${PsrDataBean.email}" id="email">${PsrDataBean.email}</a>
						</span>
					</div>
					<div class="row">
						<span class="label" style="height : 225px">Procurement Description:</span> <span
							class="formfield"><div class='Column2'
								style="height: 100px; width: 324px;">
								${PsrDataBean.procurementDescription}</div> </span>
					</div>
					<d:content isReadOnly="true">
						<div class="row">
							<span class="label" style="height : 150px">Basis for Contracting Out:</span> <span class="formfield"
								style="width: 324px;">
								<table>
									<tr>
										<td><c:set var="basisContractOut1" value="" /> <c:if
												test="${fn:contains(PsrDataBean.basisContractOut, '0')}">
												<c:set var="basisContractOut1" value="checked" />
											</c:if> <input type="checkbox" name="basisContractOut" value="0"
											${basisContractOut1} /></td>
										<td>Develop/maintain/strengthen relationship between
											non-profits/charities & communities served</td>
									</tr>
									<tr>
										<td><c:set var="basisContractOut2" value="" /> <c:if
												test="${fn:contains(PsrDataBean.basisContractOut, '1')}">
												<c:set var="basisContractOut2" value="checked" />
											</c:if> <input type="checkbox" name="basisContractOut" value="1"
											${basisContractOut2} /></td>
										<td>Obtain cost effective services</td>
									</tr>
									<tr>
										<td><c:set var="basisContractOut3" value="" /> <c:if
												test="${fn:contains(PsrDataBean.basisContractOut, '2')}">
												<c:set var="basisContractOut3" value="checked" />
											</c:if> <input type="checkbox" name="basisContractOut" value="2"
											${basisContractOut3} /></td>
										<td>Obtain special expertise</td>
									</tr>
									<tr>
										<td><c:set var="basisContractOut4" value="" /> <c:if
												test="${fn:contains(PsrDataBean.basisContractOut, '3')}">
												<c:set var="basisContractOut4" value="checked" />
											</c:if> <input type="checkbox" name="basisContractOut" value="3"
											${basisContractOut4} /></td>
										<td>Obtain personnel or expertise not available in the
											agency</td>
									</tr>
									<tr>
										<td><c:set var="basisContractOut5" value="" /> <c:if
												test="${fn:contains(PsrDataBean.basisContractOut, '4')}">
												<c:set var="basisContractOut5" value="checked" />
											</c:if> <input type="checkbox" name="basisContractOut" value="4"
											${basisContractOut5} /></td>
										<td>Provide services not needed on a long term bases</td>
									</tr>
									<tr>
										<td><c:set var="basisContractOut6" value="" /> <c:if
												test="${fn:contains(PsrDataBean.basisContractOut, '5')}">
												<c:set var="basisContractOut6" value="checked" />
											</c:if> <input type="checkbox" name="basisContractOut" value="5"
											${basisContractOut6} /></td>
										<td>Accomplish work within a limited amount of time</td>
									</tr>
								</table>
							</span>
							</div>
						<div class="row">
							<span class="label"></label>Anticipated Level of Competition:</span> <span
								class="formfield"> <select id="anticipateLevelComp"
								name="anticipateLevelComp">
									<c:set var="anticipateLevelCompH" value="" />
									<c:if test="${PsrDataBean.anticipateLevelComp eq 'H'}">
										<c:set var="anticipateLevelCompH" value="selected" />
									</c:if>
									<c:set var="anticipateLevelCompM" value="" />
									<c:if test="${PsrDataBean.anticipateLevelComp eq 'M'}">
										<c:set var="anticipateLevelCompM" value="selected" />
									</c:if>
									<c:set var="anticipateLevelCompL" value="" />
									<c:if test="${PsrDataBean.anticipateLevelComp eq 'L'}">
										<c:set var="anticipateLevelCompL" value="selected" />
									</c:if>
									<option value=""></option>
									<option value="H" ${anticipateLevelCompH}>High</option>
									<option value="M" ${anticipateLevelCompM}>Medium</option>
									<option value="L" ${anticipateLevelCompL}>Low</option>
							</select>
							</span>
						</div>

						<div class="row">
							<span class="label" style="height : 60px;">Consideration of Price:</span> <span
								class="formfield"> <textarea id="considerationPrice"
									name="considerationPrice" style="width: 324px;resize: none;height:60px;">${PsrDataBean.considerationPrice}</textarea>
							</span>
						</div>

						<c:if test="${(PsrDataBean.isOpenEndedRFP eq '0')}">
							<div class="row">
								<span class="label">Estimated No. of Contracts:</span> <span
									class="formfield">
									${PsrDataBean.estNumberOfContracts} </span>
							</div>

							<div class="row">
								<span class="label">Estimated Procurement Value ($):</span> <span
									class="formfield"> <fmt:formatNumber type="number"
										value="${PsrDataBean.estProcurementValue}" />
								</span>
							</div>
						</c:if>
						<c:if test="${(PsrDataBean.linkToConceptReport ne null)}">
						<div class="row">
							<span class="label">Link to Concept Report:</span> <span
								class="formfield"> <a href='${PsrDataBean.linkToConceptReport}' id="openLink" target="_blank" >${PsrDataBean.linkToConceptReport}</a>
							</span>
						</div>
						</c:if>
						<div class="row">
							<span class="label" style="height : 90px;">HHS Accelerator Service Applications
								Required:</span><span class="formfield" style="width: 39%;">
								To receive this RFP in the HHS Accelerator system, you must have
								an approved Service Application for<c:if test="${(PsrDataBean.serviceFilter eq '0')}"> all</c:if>
								<c:if test="${(PsrDataBean.serviceFilter eq '1')}"> at least one</c:if> of the
								following: <br><c:forEach
									items="${elementNameList}" var="element">
									<table>
									<tr>
										<td><li></li></td>
										<td>${element.serviceName}</td>
									</tr>
									</table>
								</c:forEach></span>
						</div>
						<h3>Anticipated Procurement Dates</h3>
						<c:if test="${(PsrDataBean.linkToConceptReport ne null)}">
						<%--Formatting the date in mm/dd/yyyy format  --%>
						<fmt:parseDate value="${PsrDataBean.conceptReportReleaseDt}"
							pattern="yyyy-MM-dd" var="formatedDate" />
						<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy"
							var="conceptReportReleaseDt" />
						<div class="row">
							<span class="label">Concept Report Release Date:</span> <span class="formfield"> <input type="text"
								name="conceptReportReleaseDt" id="conceptReportReleaseDt"
								value="${conceptReportReleaseDt}" maxlength="10"
								validate="calenderFormat" futureDate="true"
								cssStyle="width:78px;" id="conceptReportReleaseDt"
								cssClass="StatusOtherThanDraft agencyClass" /> <img
								src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/calender.png"
								class="imgclassPlanned"
								onclick="NewCssCal('conceptReportReleaseDt',event,'mmddyyyy');return false;">
								<br />
							</span>
						</div>
						</c:if>
						<fmt:parseDate value="${PsrDataBean.rfpReleaseDate}"
							pattern="EEE MMM dd mm:ss:SS zzz yyyy" var="formatedDate" />
						<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy"
							var="rfpReleaseDate" />
						<div class="row">
							<span class="label">RFP Release Date:</span> <span
								class="formfield"> ${rfpReleaseDate}</span>
						</div>
						<c:if test="${(PsrDataBean.isOpenEndedRFP eq '0')}">
							<fmt:parseDate value="${PsrDataBean.proposalDueDateUpdated}"
								pattern="yyyy-MM-dd" var="formatedDate" />
							<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy"
								var="proposalDueDateUpdated" />
							<div class="row">
								<span class="label">Proposal Due Date:</span> <span
									class="formfield"> ${proposalDueDateUpdated} &nbsp;2:00 PM </span>
							</div>

							<h3>Anticipated Evaluation Dates</h3>
							<fmt:parseDate value="${PsrDataBean.firstRFPEvalDateUpdated}"
								pattern="yyyy-MM-dd" var="formatedDate" />
							<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy"
								var="firstRFPEvalDateUpdated" />
							<div class="row">
								<span class="label">First Draft of RFP & Evaluation
									Criteria Date:</span> <span class="formfield">
									${firstRFPEvalDateUpdated} </span>
							</div>
							<!-- Changed Bean mapping for Defect 8401 -->
							<div class="row">
								<fmt:parseDate
									value="${PsrDataBean.finalRFPEvalDateUpdated}"
									pattern="yyyy-MM-dd" var="formatedDate" />
								<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy"
									var="finalEvalCompletionDateUpdated" />
								<span class="label">Finalize RFP & Evaluation Criteria
									Date:</span> <span class="formfield">
									${finalEvalCompletionDateUpdated} </span>
							</div>
							<!-- Changed Bean mapping for Defect 8401 -->
							<div class="row">
								<fmt:parseDate
									value="${PsrDataBean.evaluatorTrainingDateUpdated}"
									pattern="yyyy-MM-dd" var="formatedDate" />
								<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy"
									var="evaluatorTrainingDateUpdated" />
								<span class="label">Evaluator Training Date:</span> <span
									class="formfield"> ${evaluatorTrainingDateUpdated} </span>
							</div>
							<div class="row">
								<fmt:parseDate
									value="${PsrDataBean.firstEvalCompletionDateUpdated}"
									pattern="yyyy-MM-dd" var="formatedDate" />
								<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy"
									var="firstEvalCompletionDateUpdated" />
								<span class="label">First Round of Evaluation Completion
									Date:</span> <span class="formfield">
									${firstEvalCompletionDateUpdated} </span>
							</div>
							<div class="row">
								<fmt:parseDate
									value="${PsrDataBean.finalEvalCompletionDateUpdated}"
									pattern="yyyy-MM-dd" var="formatedDate" />
								<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy"
									var="proposalDueDateUpdated" />
								<span class="label">Finalize Evaluation Date:</span> <span
									class="formfield"> ${proposalDueDateUpdated} </span>
							</div>
							<div class="row">
								<fmt:parseDate
									value="${PsrDataBean.awardSelectionDateUpdated}"
									pattern="yyyy-MM-dd" var="formatedDate" />
								<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy"
									var="awardSelectionDateUpdated" />
								<span class="label">Award Selection Date:</span> <span
									class="formfield"> ${awardSelectionDateUpdated} </span>
							</div>
							<h3>Anticipated Contract Dates</h3>
							<div class="row">
								<fmt:parseDate
									value="${PsrDataBean.contractStartDatePlanned}"
									pattern="yyyy-MM-dd" var="formatedDate" />
								<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy"
									var="contractStartDatePlanned" />
								<span class="label">Contract Start Date:</span> <span
									class="formfield"> ${contractStartDatePlanned} </span>
							</div>
							<div class="row">
								<fmt:parseDate value="${PsrDataBean.contractEndDateUpdated}"
									pattern="yyyy-MM-dd" var="formatedDate" />
								<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy"
									var="contractEndDateUpdated" />
								<span class="label">Contract End Date:</span> <span
									class="formfield"> ${contractEndDateUpdated} </span>
							</div>
							<div class="row">
								<span class="label">Renewal Options:</label></span> <span
									class="formfield"><input type="text" rows="1" cols="35"
									name="renewalOption" style="width:324px;" id="renewalOption"
									value="${PsrDataBean.renewalOption}" /> </span>
							</div>
						</c:if>
						<c:if test="${(PsrDataBean.isOpenEndedRFP eq '1')}">
						<div class="row">
								<span class="label">Renewal Options:
									</span>
										<span class="formfield"><input class="ignoreValidate" type="text" rows="1"
												cols="35" name="renewalOption" id="renewalOption" value="${PsrDataBean.renewalOption}"/>
									   </span>									 
							</div>
						</c:if>
						<h3>Multi-Year Human Services Contract</h3>
						<div class="row">
							<span class="label">The ACCO has determined that the needs
								of the population will continue beyond one year and that a
								multi-term contract will serve the best interests of the City by
								encouraging effective competition/prompting economies, pursuant
								to § 2-04(d)(2) of the PPB rules, because:</label>
							</span> <span class="formfield"><textarea id="multiYearHumanServContract" rows="10"  style="width:324px;resize: none;"
									name="multiYearHumanServContract">${PsrDataBean.multiYearHumanServContract}</textarea>
							</span>
						</div>
						<div class="row">
							<span class="label">The ACCO has determined, that pursuant
								to § 2-04(e) of the PPB Rules that the contract(s) awarded from
								this procurement will fall within the parameters of:</label>
							</span> <span class="formfield">
								<div id="checkbox_div">
									<c:set var="contractTermInfo1" value="" /> 
									<c:if test="${PsrDataBean.contractTermInfo eq '6'}">
										<c:set var="contractTermInfo1" value="checked" />
									</c:if>
									<c:set var="contractTermInfo2" value="" /> 
									<c:if test="${PsrDataBean.contractTermInfo eq '9'}">
										<c:set var="contractTermInfo2" value="checked" />
									</c:if>
									<c:set var="contractTermInfo3" value="" /> 
									<c:if test="${PsrDataBean.contractTermInfo eq '0'}">
										<c:set var="contractTermInfo3" value="checked" />
									</c:if>
									<input type="radio" name="contractTermInfo" id="contractTermInfo1" value="6" ${contractTermInfo1}/>Six-year contract term<br>
									<input type="radio" name="contractTermInfo" id="contractTermInfo2" value="9" ${contractTermInfo2}/>Nine-year contract term<br>
									<input type="radio" name="contractTermInfo" id="contractTermInfo3" value="0" ${contractTermInfo3}/>Not Applicable<br>
								</div>
							</span>
						</div>
						<div class="row">
							<span class="label">The ACCO has determined that although
								the contract(s) awarded from this procurement require a contract
								term beyond nine years. Pursuant to § 2-04(e)(3), this is an
								extraordinary case and there are compelling circumstances
								warranting an award for a total term in excess of 9 years,
								because:
							</span>
							<span class="formfield"><textarea id="multiYearHumanServOpt" rows="11" style="width:324px;resize: none;"
									name="multiYearHumanServOpt">${PsrDataBean.multiYearHumanServOpt}</textarea>							
							</span>
						</div>
					</d:content>
				</div>
			</div>
			<jsp:useBean id="procurementCOFDetails"
				class="com.nyc.hhs.model.ProcurementCOF" scope="request"></jsp:useBean>
			<c:if test="${(taskDetailsBean.isOpenEndedRfp eq 'NO') &&(PsrDataBean.estProcurementValue > 0)}">
				<c:if test="${not empty GridColNames && not empty MainHeaderProp && not empty SubHeaderProp && not empty mainAccountGrid && not empty subAccountGrid && not empty columnsForTotal}">
				<div class='hr'></div>
				<h3>Procurement Certification of Funds</h3>
				<div class="formcontainer">
					<div class="row">
						<span class="label">Agency Code:</span> <span class="formfield">${ProcurementCOF.agencyCode}</span>
					</div>
					<div class="row">
					<fmt:parseDate value="${ProcurementCOF.createdDate}" pattern="yyyy-MM-dd" var="formatedDate" />
					<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy" var="createdDate" />
						<span class="label">Submitted By:</span> <span
							class="formfield">${ProcurementCOF.createdByUserId}</span>
					</div>
					<div class="row">
						<span class="label">Date Submitted:</span> <span
							class="formfield"><label id="aoProcValue">${createdDate}</label></span>
					</div>
					<div class="row">
						<span class="label">Approved By:</span> <span
							class="formfield">${ProcurementCOF.approverFirstName}</span>
					</div>
					<div class="row">
					<fmt:parseDate value="${ProcurementCOF.approvedDate}" pattern="yyyy-MM-dd" var="formatedDate" />
					<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy" var="approvedDate" />
						<span class="label">Date Approved:</span> <span
							class="formfield">${approvedDate}</span>
					</div>
				</div>

				<p>&nbsp;</p>

				<h3>Chart of Accounts Allocation</h3>
				<div class='gridFormField gridScroll'>
					<jq:grid id="ProcCoAAllocation" gridColNames="${GridColNames}"
						gridColProp="${MainHeaderProp}" subGridColProp="${SubHeaderProp}"
						gridUrl="${mainAccountGrid}" subGridUrl="${subAccountGrid}"
						cellUrl="${accountOperationGrid}"
						editUrl="${accountOperationGrid}"
						positiveCurrency="${columnsForTotal}" dataType="json"
						methodType="POST" columnTotalName="${columnsForTotal}"
						notAllowDuplicateColumn="uobc,subOC,rc" isPagination="true"
						rowsPerPage="5" isSubGrid="true" isReadOnly="true"
						operations="del:false,edit:false,add:false,cancel:false,save:false"
						autoWidth="false" isCOAScreen="true" />
				</div>
				<c:if test="${showPsrFundingSubGrid eq true}">
			<P>&nbsp;</P>
			<H3>Funding Source Allocation</H3>
				<div class='gridFormField gridScroll'> 
					<jq:grid id="fundingSource" 
						gridColNames="${FundingGridColNames}"
						gridColProp="${FundingMainHeaderProp}"
						subGridColProp="${FundingSubHeaderProp}"
						gridUrl="${mainFundingGrid}" 
						positiveCurrency="${columnsForTotal}" 
						nonEditColumnName="total" autoWidth="false" isCOAScreen="true"
						subGridUrl="${subFundingGrid}" cellUrl="${fundingOperationGrid}" editUrl="${fundingOperationGrid}"
						dataType="json" methodType="POST" columnTotalName="${columnsForTotal}"
						isPagination="true" rowsPerPage="5" isSubGrid="true" isReadOnly="true"
						operations="del:false,edit:false,add:false,cancel:false,save:false"
						 />
				</div>
				</c:if>
				</c:if>
			</c:if>
			<p>&nbsp;</p>
		</div>

		<%-- PCOF end --%>
		<%-- Site Information End --%>
		<%-- Center Column End --%>

		<div>&nbsp;</div>
		<div class='clear'>&nbsp;</div>
		<%-- Contract(s) Section Starts Here --%>
		<d:content isReadOnly="${screenReadOnly}">
			<div id='contractTabs'>
				<div class="customtabs">
					<ul style="background-color:transparent;">
						<li><a href='#Comments' title="Comments"
							class="showButton localTabs">Comments</a></li>
						<li><a href='#ViewTaskHistory' title="View Task History"
							class="hideButton localTabs">View Task History</a></li>
						<div class='floatRht' id="saveDiv">
							<input type="button" id="saveButton" class="graybtutton"
								value="Save" onclick="savePsrComments()" />
						</div>
					</ul>
				</div>
				<div id='Comments'>
					<div>
						<h3>Enter any internal Comments:</h3>
						<p>Enter any review comments. These comments will be
							available to the ACCO. Click the &quot;Save&quot;
							button above to save your comments.</p>
						<div style='height: 40px'>&nbsp;</div>
						<textarea rows="5" class='textarea' id="internalComments"
							name="internalComments" onkeyup="setMaxLength(this,1000)"
							onkeypress="setMaxLength(this,1000)">${internalComments}</textarea>
					</div>
				</div>
				<div id='ViewTaskHistory'>
					<div class="tabularWrapper">
						<st:table objectName="taskHistoryList" cssClass="heading"
							alternateCss1="evenRows" alternateCss2="oddRows">
							<st:property headingName="Task" columnName="taskName"
								align="center" size="25%" />
							<st:property headingName="Action" columnName="action"
								align="right" size="20%" />
							<st:property headingName="Detail" columnName="detail"
								align="right" size="25%" />
							<st:property headingName="User" columnName="user" align="right"
								size="10%" />
							<st:property headingName="Date/Time" columnName="dateTime"
								align="right" size="20%" />
						</st:table>
					</div>
				</div>
			</div>
		</d:content>
		
		<%--PCOF Popup--%>
		<div class="alert-box alert-box-approvepsr skipElementsInCompare">
			<div class='tabularCustomHead'>Approve PSR</div>
			<a href="javascript:void(0);" class="exit-panel">&nbsp;</a>
			<div id="requestCancel" class="popupPadding">
				<h2>Approve PSR</h2>
				<div class='hr'></div>
		
				<div class="failed" id="errorMsg"></div>
				<p>Are you sure you want to Approve the PSR form?</p>
				<p>
					After submission of the form, the PSR can no longer be edited or modified.<br>
				</p>
				<div class="formcontainer">
					<div class="row">
						<span class='label clearLabel autoWidth'> <input name=""
							type="checkbox" id='chkSubmitCBForm' /> <label
							for='chkSubmitCBForm'>Yes, I confirm the accuracy of this form.</label>
						</span> <span class="error"></span>
					</div>
					<div class="row" id="usernameDiv">
						<span class="label"> <label class="required">*</label><label
							for='txtSubmitCBUserName'>User Name:</label>
						</span> <span class="formfield"> <input type="text"
							class='proposalConfigDrpdwn' id="txtSubmitCBUserName"
							name='userName' maxlength="128" value=""/>
						</span> <span class="error" style="padding-left: 5px;" id="usernamespan"></span>
					</div>
					<div class="row" id="passwordDiv">
						<span class="label"> <label class="required">*</label><label
							for='txtSubmitCBPassword'>Password:</label>
						</span> <span class="formfield"> <input type="password"
							class='proposalConfigDrpdwn' name='password'
							id="txtSubmitCBPassword" autocomplete="off" />
						</span> <span class="error" style="padding-left: 5px;" id="passwordspan"></span>
						</div>
				</div>
				<div class="buttonholder">
					<input type="button" class="graybtutton" title=""
						value="No, do not approve form" id="btnNotSubmitCB" onclick="cancelOverLay()"/> <input
						type="button" class="button" title="" value="Yes, approve form"
						id="btnSubmitCB" onclick="finishTask()" />
				</div>
			</div>
		</div>
	</form:form>
</div>