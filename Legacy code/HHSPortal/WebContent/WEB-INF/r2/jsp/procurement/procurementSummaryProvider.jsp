<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="nav" uri="/WEB-INF/tld/navigationSM.tld"%>
<%@ taglib prefix="comSM" uri="/WEB-INF/tld/commonSolicitation.tld"%>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<fmt:setBundle basename="com/nyc/hhs/properties/messages"/>
<portlet:defineObjects />
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/procurementSummaryProvider.js"></script>
<style>
h2{width:82%}
.formcontainer span.formfield{
	width:57% !important
}
</style>
<portlet:actionURL var="openServiceSelection" escapeXml="false">
	<portlet:param name="topLevelFromRequest" value="ServicesAndProviders" />						
	<portlet:param name="submit_action" value="openServiceSelection"/>
	<portlet:param name="procurementId" value="${procurementId}"/>
</portlet:actionURL>
<nav:navigationSM screenName="ProcurementSummaryHeader">
	<form:form id="procurementSummaryform" name="procurementSummaryform" action="${openServiceSelection}" method ="post">
	
		<div class="floatRht">
<comSM:commonSolicitation topLevelStatus="true" procurementId="${procurementId}" 
providerId="${sessionScope.user_organization}"></comSM:commonSolicitation>
</div>
		<div class='clear'></div>
		
		<%-- Form Data Starts --%>		
		<div id='tabs-container'>
			<h2>Procurement Summary</h2>
			<c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
							<d:content section="${helpIconProvider}">
								<div id="helpIcon" class="iconQuestion"><a href="javascript:void(0);" id="helpIconId" title='Need Help?' onclick="smFinancePageSpecificHelp();"></a></div>
								<input type="hidden" id="screenName" value="Procurement Summary" name="screenName"/>
							</d:content>
			<div class='hr'></div>
			<c:if test="${message ne null}">
				<div class="${messageType}" id="messagediv" style="display:block">${message} <img
					src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
					class="message-close" 
					onclick="showMe('messagediv', this)">
				</div>
			</c:if>
			<h3>Basic Information</h3>
			<div class="formcontainer">
				<c:set var="sectionBasicInformation"><%=HHSComponentMappingConstant.S231_BASIC_INFORMATION%></c:set>
				<d:content section="${sectionBasicInformation}">
					<div class="row">
						<span class="label">E-PIN:</span>
						<span class="formfield">
							${procurementBean.procurementEpin} 
						</span>
					</div>
				</d:content>
				<div class="row">
					 <span class="label">Procurement Title:</span>
					<span class="formfield">
						${procurementBean.procurementTitle} 
					</span>
				</div>
				<%--make a check for the city users  --%>
				<c:set var="sectionBasicInformation"><%=HHSComponentMappingConstant.S231_BASIC_INFORMATION%></c:set>
				<d:content section="${sectionBasicInformation}">
					<div class="row">
						 <span class="label">Procurement Status:</span>
						<span class="formfield">
							${procurementBean.procurementStatus}
						</span>
					</div>
				</d:content>
					<div class="row">
						 <span class="label">Agency:</span>
						<span class="formfield">
							${procurementBean.agencyName}
						</span>
					</div>
				<div class="row">
					<span class="label">Program Name:</span>
					<span class="formfield">
						${procurementBean.programName}
					</span>
				</div>
				<%--make a check for the city users  --%>
				<c:set var="sectionBasicInformation"><%=HHSComponentMappingConstant.S231_BASIC_INFORMATION%></c:set>
				<input type="hidden" id="hiddenProcDesc" value="${fn:escapeXml(procurementBean.procurementDescription)}"/>
				<d:content section="${sectionBasicInformation}">
					<div class="row">
						 <span class="label">Accelerator Primary Contact:</span>
						 <span class="formfield">
							${procurementBean.accPrimaryContact}
						</span>
					</div>
					<div class="row">
						 <span class="label">Accelerator Secondary Contact:</span>
						<span class="formfield">
							${procurementBean.accSecondaryContact}
						</span>
					</div>
					<div class="row">
						<span class="label">Agency Primary Contact:</span>
						<span class="formfield">
							${procurementBean.agecncyPrimaryContact}
						</span>
					</div>
					<div class="row">
						<span class="label">Agency Secondary Contact:</span>
						<span class="formfield">
							${procurementBean.agecncySecondaryContact}
						</span>
					</div>
				</d:content>
				<input type="hidden" id="mail" value="${procurementBean.email}">
				<div class="row">
					<span class="label">Agency Email Contact:</span>
					<span class="formfield">
						<a href='javascript:;' id="email">${procurementBean.email}</a>
					</span>
				</div>
				<div class="row">
					<span class="label" style='height:400px'>Procurement Description:</span>
					<span class="formfield">
						<div id="procDesc" class="descriptionField"></div>
					</span>
				</div>
				<div class="row">
				<%-- Code updated for R4 Starts --%>
					 <span class="label"><label class='required'>&#42;</label>Is this an open-ended RFP?:</span>
					 <span class="formfield">
						<c:choose>
							<c:when test="${procurementBean.isOpenEndedRFP eq '1'}">Yes</c:when>
							<c:otherwise>No</c:otherwise>
						</c:choose>
					 </span>
					 <input type="hidden" id="isOpenEndedRFP" value="${procurementBean.isOpenEndedRFP}"/>
				
				</div>
				
				<c:if test="${procurementBean.isOpenEndedRFP eq '0'}">
					<div class="row">
						<%-- Code updated for R4 Ends --%>
						<span class="label">Estimated No. of Contracts:</span>
						<span class="formfield">
							${procurementBean.estNumberOfContracts}
						</span>
					</div>
				
				</c:if>
				<c:if test="${procurementBean.isOpenEndedRFP eq '0'}">
				<%-- Code updated for R4 Ends --%>
					<div class="row">
						<span class="label">Estimated Procurement Value ($):</span>
						<span class="formfield" id="procValueSpan">
							 ${procurementBean.estProcurementValue}
						</span>
					</div>
				</c:if>
				
				<input type="hidden" id="link" value="${procurementBean.linkToConceptReport}">
				<div class="row">
					<span class="label">Link to Concept Report:</span>
					<span class="formfield">
						<a href='javascript:;' id="openLink">${procurementBean.linkToConceptReport}</a>
					</span>
				</div>
			</div>
			<%--Formatting the date in mm/dd/yyyy format  --%>
			<fmt:parseDate value="${procurementBean.rfpReleaseDateUpdated}" pattern="yyyy-MM-dd" var="formatedDate"/> 
		<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy" var="rfpReleaseDateUpdated"/>
		
		
			<h3 title="Procurement dates may change. Check Procurement Roadmap frequently to track up-to-date info.">Procurement Dates</h3>
			<div class="formcontainer">
				<div class="row">
					<span class="label">RFP Release Date:</span>
					<span class="formfield">
						${rfpReleaseDateUpdated}
					</span>
				</div>
				<fmt:parseDate value="${procurementBean.preProposalConferenceDateUpdated}" pattern="yyyy-MM-dd" var="formatedDate"/> 
				<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy" var="preProposalConferenceDateUpdated"/>
				<%-- Code updated for R4 Starts --%>
				<c:if test="${procurementBean.isOpenEndedRFP eq '0'}">
					<div class="row openEndedHide">
						<span class="label">Pre-Proposal Conference Date:</span>
						<span class="formfield">
							${preProposalConferenceDateUpdated }
						</span>
					</div>
				</c:if>
			<%-- Code updated for R4 Ends --%>	
				<fmt:parseDate value="${procurementBean.proposalDueDateUpdated}" pattern="yyyy-MM-dd" var="formatedDate"/> 
				<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy" var="proposalDueDateUpdated"/>
				<%-- Code updated for R4 Starts --%>
				<c:if test="${procurementBean.isOpenEndedRFP eq '0'}">
					<div class="row openEndedHide">
						<span class="label">Proposal Due Date:</span>
						<span class="formfield">
							${proposalDueDateUpdated} ${releaseTime}<br/>
						</span>
					</div>
				</c:if>
				<%-- Code updated for R4 Ends --%>	
			</div>
			<%--make a check for the city users  --%>
			<c:set var="sectionEvaluationDates"><%=HHSComponentMappingConstant.S231_EVALUATION_DATES%></c:set>
			<d:content section="${sectionEvaluationDates}">
				<%--Formatting the date in mm/dd/yyyy format  --%>
				<fmt:parseDate value="${Procurement.firstRFPEvalDateUpdated}" pattern="yyyy-MM-dd" var="formatedDate"/> 
				<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy" var="firstRFPEvalDateUpdated"/>
				<%-- Code updated for R4 Starts --%>
				<c:if test="${procurementBean.isOpenEndedRFP eq '0'}">
					<h3 class="openEndedHide">Evaluation Dates:</h3>
					<div class="formcontainer openEndedHide">
				<%-- Code updated for R4 Ends --%>
						<div class="row">
							<span class="label">First Draft of RFP & Evaluation Criteria Date:</span>
							<span class="formfield">
								${firstRFPEvalDateUpdated}
							</span>
						</div>
						<fmt:parseDate value="${procurementBean.finalRFPEvalDateUpdated}" pattern="yyyy-MM-dd" var="formatedDate"/> 
						<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy" var="finalRFPEvalDateUpdated"/>
						<div class="row">
							<span class="label">Finalize RFP & Evaluation Criteria Date:</span>
							<span class="formfield">
								${finalRFPEvalDateUpdated }
							</span>
						</div>
						<fmt:parseDate value="${procurementBean.evaluatorTrainingDateUpdated}" pattern="yyyy-MM-dd" var="formatedDate"/> 
					<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy" var="evaluatorTrainingDateUpdated"/>
						<div class="row">
							<span class="label">Evaluator Training Date:</span>
							<span class="formfield">
								${evaluatorTrainingDateUpdated} 
							</span>
						</div>
						<fmt:parseDate value="${procurementBean.firstEvalCompletionDateUpdated}" pattern="yyyy-MM-dd" var="formatedDate"/> 
						<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy" var="firstEvalCompletionDateUpdated"/>
						<div class="row">
							<span class="label">First Round of Evaluation Completion Date:</span>
							<span class="formfield">
								${firstEvalCompletionDateUpdated}
							</span>
						</div>
						<fmt:parseDate value="${procurementBean.finalEvalCompletionDateUpdated}" pattern="yyyy-MM-dd" var="formatedDate"/> 
					<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy" var="finalEvalCompletionDateUpdated"/>
					
						<div class="row">
							<span class="label">Finalize Evaluation Date:</span>
							<span class="formfield">
								${finalEvalCompletionDateUpdated }
							</span>
						</div>
						<fmt:parseDate value="${procurementBean.awardSelectionDateUpdated}" pattern="yyyy-MM-dd" var="formatedDate"/> 
					<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy" var="awardSelectionDateUpdated"/>
						<div class="row">
							<span class="label">Award Selection Date:</span>
							<span class="formfield">
								${awardSelectionDateUpdated} 
							</span>
						</div>
					</div>
				</c:if>
			</d:content>
			<%--Formatting the date in mm/dd/yyyy format  --%>
			<fmt:parseDate value="${procurementBean.contractStartDateUpdated}" pattern="yyyy-MM-dd" var="formatedDate"/> 
				<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy" var="contractStartDateUpdated"/>
			<%-- Code updated for R4 Starts --%>
			<c:if test="${procurementBean.isOpenEndedRFP eq '0'}">
				<h3 class="openEndedHide">Contract Dates</h3>
					<div class="formcontainer openEndedHide">
						<%-- Code updated for R4 Ends --%>
						<div class="row">
							<span class="label">Contract Start Date:</span>
							<span class="formfield">
								${contractStartDateUpdated}
							</span>
						</div>
						<fmt:parseDate value="${procurementBean.contractEndDateUpdated}" pattern="yyyy-MM-dd" var="formatedDate"/> 
						<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy" var="contractEndDateUpdated"/>
						<div class="row">
							<span class="label">Contract End Date:</span>
							<span class="formfield">
								${contractEndDateUpdated}
							</span>
						</div>
					</div>
			</c:if>
			<c:set var="sectionNextButton"><%=HHSComponentMappingConstant.S231_NEXT_BUTTON%></c:set>
			<d:content section="${sectionNextButton}">
				<div class="buttonholder">
					<input type="button" id ="next" value="Next" onclick="setPageGreyOut()" />
				</div>
			</d:content>
		</div>
	</form:form>
	<div class="overlay"></div>
</nav:navigationSM>