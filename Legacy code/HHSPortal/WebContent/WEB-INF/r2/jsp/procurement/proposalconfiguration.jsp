<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="nav" uri="/WEB-INF/tld/navigationSM.tld"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="comSM" uri="/WEB-INF/tld/commonSolicitation.tld"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<style>
.formcontainer .row span.error{
	padding:0 !important;
	background:transparent;
	line-height:auto;
	font-size: 0px;
	margin-left:36%
}
.formcontainer .row span.error label{
	font-size:12px;
	line-height:auto
}

.labelErrorDiv {
	color: red;
}

.labelInput{
	width: 47.5%;
}
</style>
<fmt:setBundle basename="com/nyc/hhs/properties/statusproperties"/>
<nav:navigationSM screenName="ProposalConfiguration">
	<portlet:defineObjects />
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/proposalconfiguration.js"></script>
	<portlet:actionURL var="proposalConfigurationUrl" escapeXml="false">
		<portlet:param name="action" value="rfpRelease" />
		<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}"/>
		<portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}"/>
	</portlet:actionURL>
	<script type="text/javascript">
		//on load function to perform various checks on loading of jsp
		$(document).ready(function(){
			for(i=0; i<=14; i++){
				if("" != document.getElementById("requiredDocumentList"+i+".customLabelName").value){
					$("#customLabelDivRequired"+i+"").show();
				}
			}
			
			for(i=0; i<=4; i++){
				if("" != document.getElementById("optionalDocumentList"+i+".customLabelName").value){
					$("#customLabelDivOptional"+i+"").show();
				}
			}
		});
		</script>
	<form:form id="proposalconfigform" name="proposalconfigform" action="${proposalConfigurationUrl}" method ="post" commandName="ProposalDetailsBean">
		<div id='tabs-container' class='clearHeight'>
			<d:content isReadOnly="${((accessScreenEnable eq false) or hideExitProcurement) and (procurementBean.status ne 7 and procurementBean.status ne 8)}">
			<c:set var="released"><fmt:message key='PROCUREMENT_RELEASED'/></c:set>
			<c:set var="proposalReceived"><fmt:message key='PROCUREMENT_PROPOSALS_RECEIVED'/></c:set>
			<c:set var="evaluationComplete"><fmt:message key='PROCUREMENT_EVALUATIONS_COMPLETE'/></c:set>
			<c:set var="selectionMade"><fmt:message key='PROCUREMENT_SELECTIONS_MADE'/></c:set>
			<c:set var="cancelled"><fmt:message key='PROCUREMENT_CANCELLED'/></c:set>
			<c:set var="closed"><fmt:message key='PROCUREMENT_CLOSED'/></c:set>
			<d:content readonlyStatuses="${proposalReceived}, ${evaluationComplete}, ${selectionMade}, ${closed}, ${cancelled}" isReadOnly="${isReadOnly}">
			<h2>Proposal Configuration</h2>
			<c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
				<d:content section="${helpIconProvider}">
					<div id="helpIcon" class="iconQuestion"><a href="javascript:void(0);" id="helpIconId" title='Need Help?' onclick="smFinancePageSpecificHelp();"></a></div>
					<input type="hidden" id="screenName" value="Proposal Configuration" name="screenName"/>
				</d:content>
			<div class='hr'></div>
			
			<c:set var="sectionUnpublishInformation"><%=HHSComponentMappingConstant.S209_UNPUBLISHED_INFORMATION%></c:set>
			<d:content section="${sectionUnpublishInformation}">
				<c:if test="${unPublishedDataMsg ne null}">
					<div class='infoMessage' style="display:block">${unPublishedDataMsg}</div>
				</c:if>
			</d:content>
			<c:if test="${message ne null}">
				<div class="${messageType}" id="messagediv" style="display:block">${message} <img
					src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
					class="message-close" 
					onclick="showMe('messagediv', this)">
				</div>
			</c:if>
			<c:if test="${(accessScreenEnable eq false) and (procurementBean.status ne 7 and procurementBean.status ne 8)}">
				<div class="lockMessage" style="display:block">${lockedByUser} currently has edit access to this record. This record will be displayed in Read-only format.</div>
			</c:if>
		<form:hidden path="procurementStatus" />
				<p>Please make selections below to indicate information required for a Provider's proposal.</p>
					<%-- Question Configurations Starts --%>
					<h3>Question Configurations</h3>
					<p>
						In the fields below, enter any questions that are applicable. 
						Answers to these questions will be entered as free text and will not be validated. 
						Only questions with corresponding checkboxes marked will appear to Providers.
					</p>
					<div class='formcontainer' id="customQuestion">
					<!-- Loop modified for additional 10 custom questions for Enhancement #6411 for Release 3.2.0 -->
					<!-- Loop modified for additional 5 custom questions for QC9181 for Release 7.7.0 -->
						<c:forEach var="i" begin="0" end="19">
							<c:set var="checked" value=""></c:set>
							<c:set var="checkedValue" value=""></c:set>
							<c:set var="disabledValue" value="false"></c:set>
							<c:if test="${ProposalDetailsBean.questionAnswerBeanList[i].questionFlag=='1'}">
								<c:set var="checked" value="checked"></c:set>
								<c:set var="checkedValue" value="1"></c:set>
							</c:if>
							<c:if test="${procurementBean.status eq '3'}">
								<c:choose>
									<c:when test="${(ProposalDetailsBean.questionAnswerBeanList[i].isAddendum == 'false') or (ProposalDetailsBean.questionAnswerBeanList[i].isAddendum == 'true' and ProposalDetailsBean.questionAnswerBeanList[i].procurementQnId ne null)}">
										<c:set var="disabledValue" value="true"></c:set>
									</c:when>
									<c:otherwise>
										<c:set var="disabledValue" value="false"></c:set>
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${ProposalDetailsBean.questionAnswerBeanList[i].isAddendum eq null}">
										<form:hidden path="questionAnswerBeanList[${i}].isAddendum" value="false"/>
									</c:when>
									<c:otherwise>
										<form:hidden path="questionAnswerBeanList[${i}].isAddendum"/>
									</c:otherwise>
								</c:choose>
							</c:if>
		   					<div class='row'>
								<span class='label'>Custom Question ${i+1} Text:</span>
								<span class='formfield proposalConfigFormfield'>
									<form:checkbox path="questionAnswerBeanList[${i}].questionFlag" id="questionFlag${i}"
									checked='${checked}' value="${checkedValue}" disabled="${disabledValue}" onclick="enableTextBox(this,'${i}')" />
									<form:input path="questionAnswerBeanList[${i}].questionText" class='inputStretched' id="questionText${i}" maxlength="150"/>
									<form:hidden path="questionAnswerBeanList[${i}].procurementQnId" />
									<form:hidden path="questionAnswerBeanList[${i}].addendumId"/>
								</span>
								<span class="error">
									<label  class="error"  id="questionFlagError${i}">
										<form:errors path="questionAnswerBeanList[${i}].questionText" cssClass="ValidationError"></form:errors>
									</label>
								</span>
							</div>
						</c:forEach>
					</div>
			<%-- Question Configurations Ends --%>	
				<div>&nbsp;</div>
			<%-- Required Document Section Starts --%>
				<h3>Required Documents</h3>
				<p>
					From the drop-downs below, please indicate which documents Providers will be required to upload as part of their Proposal. 
					Blank fields will not appear to Providers.
				</p>
				<div class='formcontainer'>
					<c:forEach var="i" begin="0" end="14">
						<c:if test="${procurementBean.status eq '3'}">
							<c:choose>
								<c:when test="${(ProposalDetailsBean.requiredDocumentList[i].isAddendum == 'false') or (ProposalDetailsBean.requiredDocumentList[i].isAddendum == 'true' and ProposalDetailsBean.requiredDocumentList[i].procurementDocumentId ne null)}">
									<c:set var="disabledValue" value="true"></c:set>
								</c:when>
								<c:otherwise>
									<c:set var="disabledValue" value="false"></c:set>
								</c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${ProposalDetailsBean.requiredDocumentList[i].isAddendum eq null}">
									<form:hidden path="requiredDocumentList[${i}].isAddendum" value="false"/>
								</c:when>
								<c:otherwise>
									<form:hidden path="requiredDocumentList[${i}].isAddendum"/>
								</c:otherwise>
							</c:choose>
							<form:hidden path="requiredDocumentList[${i}].addendumDocumentId"/>
						</c:if>
	   					<div class='row'>
							<span class='label'>Required Proposal Document Type ${i+1}:</span>
							<span class='formfield proposalConfigFormfield'>
								<form:select class='proposalConfigDrpdwn' disabled="${disabledValue}" path="requiredDocumentList[${i}].documentType" onchange="checkOthersDocType(this.value,${i})">
									<form:option value=""></form:option>
									<c:forEach var="docType" items="${documentTypeList}">
											<c:choose>
												<c:when test="${ProposalDetailsBean.requiredDocumentList[i].documentType eq docType}">
													<option selected="selected" value="${docType}">${docType}</option>
												</c:when>
												<c:otherwise>
									                <option value="${docType}">${docType}</option>
									           	</c:otherwise>
											</c:choose>
									</c:forEach>
								</form:select>
							</span>
							<c:if test="${disabledValue eq true}">
							<form:hidden path="requiredDocumentList[${i}].documentType" />
							</c:if>
							<form:hidden path="requiredDocumentList[${i}].documentSeqNumber" />
							<form:hidden path="requiredDocumentList[${i}].procurementDocumentId" />
							<form:hidden path="requiredDocumentList[${i}].requiredFlag" />
						</div>
						<%-- Custom Label input box added for required documents Release 3.6.0 enhancement #6485 --%>	
						<div id="customLabelDivRequired${i}" style="display:none">
							<div class='row'>
								<span class='label'>* Required Document Name:</span>
								<span class='customLabelNameDiv${i}'">
								<c:choose>
									<c:when test="${disabledValue eq true}">
										<form:input type="text" path="requiredDocumentList[${i}].customLabelName" class="readonly labelInput" onkeyup="setMaxLength(this,350)" onkeypress="setMaxLength(this,350)" readonly="true"/>
									</c:when>
									<c:otherwise>
										<form:input type="text" path="requiredDocumentList[${i}].customLabelName" class="labelInput" onkeyup="setMaxLength(this,350)" onkeypress="setMaxLength(this,350)"/>
									</c:otherwise>
								</c:choose>
								</span>
								<div id="customLabelReqError${i}" class="labelErrorDiv">
								</div>
							</div>
						</div>
					</c:forEach>
				</div>
					
			<%-- //Required Document Section Ends --%>
			
			<div>&nbsp;</div>
			
			<%-- Optional Document Section Starts --%>
			<h3>Optional Documents</h3>
				<p>	
					From the drop-downs below, please indicate which documents will be optional for the Providers to upload as part of their Proposal. 
					Blank fields will not appear to Providers.
				</p>
				<div class='formcontainer'>
					<c:forEach var="i" begin="0" end="4">
						<c:if test="${procurementBean.status eq '3'}">
							<c:choose>
								<c:when test="${(ProposalDetailsBean.optionalDocumentList[i].isAddendum == 'false') or (ProposalDetailsBean.optionalDocumentList[i].isAddendum == 'true' and ProposalDetailsBean.optionalDocumentList[i].procurementDocumentId ne null)}">
									<c:set var="disabledValue" value="true"></c:set>
								</c:when>
								<c:otherwise>
									<c:set var="disabledValue" value="false"></c:set>
								</c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${ProposalDetailsBean.optionalDocumentList[i].isAddendum eq null}">
									<form:hidden path="optionalDocumentList[${i}].isAddendum" value="false"/>
								</c:when>
								<c:otherwise>
									<form:hidden path="optionalDocumentList[${i}].isAddendum"/>
								</c:otherwise>
							</c:choose>
							<form:hidden path="optionalDocumentList[${i}].addendumDocumentId"/>
						</c:if>
						<div class='row'>
							<span class='label'>Optional Proposal Document Type ${i+1}:</span>
							<span class='formfield proposalConfigFormfield'>
								<form:select class='proposalConfigDrpdwn' disabled="${disabledValue}" path="optionalDocumentList[${i}].documentType" onchange="checkOthersOptionalDocType(this.value,${i})">
									<form:option value=""></form:option>
									<c:forEach var="docType" items="${documentTypeList}">
										<c:choose>
											<c:when test="${ProposalDetailsBean.optionalDocumentList[i].documentType eq docType}">
												<option selected="selected" value="${docType}">${docType}</option>
											</c:when>
											<c:otherwise>
								                <option value="${docType}">${docType}</option>
								           	</c:otherwise>
										</c:choose>
									</c:forEach>
								</form:select>
							</span>
							<c:if test="${disabledValue eq true}">
							<form:hidden path="optionalDocumentList[${i}].documentType" />
							</c:if>
							<form:hidden path="optionalDocumentList[${i}].documentSeqNumber" />
							<form:hidden path="optionalDocumentList[${i}].procurementDocumentId" />
							<form:hidden path="optionalDocumentList[${i}].requiredFlag" />
						</div>
						<%-- Custom Label input box added for optional documents Release 3.6.0 enhancement #6485 --%>	
						<div id="customLabelDivOptional${i}" style="display:none">
							<div class='row'>
								<span class='label'>* Optional Document Name:</span>
								<span class='customLabelNameDiv${i}'>
									<c:choose>
										<c:when test="${disabledValue eq true}">
											<form:input type="text" path="optionalDocumentList[${i}].customLabelName" readonly="true" class="readonly labelInput" onkeyup="setMaxLength(this,350)" onkeypress="setMaxLength(this,350)"/>
										</c:when>
										<c:otherwise>
											<form:input type="text" path="optionalDocumentList[${i}].customLabelName" class="labelInput" onkeyup="setMaxLength(this,350)" onkeypress="setMaxLength(this,350)"/>
										</c:otherwise>
									</c:choose>
								</span>
								<div id="customLabelOptError${i}" class="labelErrorDiv">
								</div>
							</div>
						</div>
					</c:forEach>
				</div>
			</d:content>
			<%-- //Optional Document Section Ends --%>	
			<div class="buttonholder clearHeight">
				<div class='floatLft'>
					<comSM:commonSolicitation screenName="ProposalConfiguration" level="ProcurementWidget" procurementId="${procurementId}" procurementStatus="${procurementBean.procurementStatus}"></comSM:commonSolicitation>
				</div>
				<c:set var="sectionSaveButton"><%=HHSComponentMappingConstant.S209_SAVE_BUTTON%></c:set>
				<d:content section="${sectionSaveButton}">
				<%-- Code updated for R4 Starts --%>
					<c:if test="${saveButtonStatus and (not isReadOnly)}">
				<%-- Code updated for R4 Ends --%>
						<input type="button" value="Save" title='Save changes' onclick="saveProposalConfiguration();"/>
					</c:if>
				</d:content>
			</div>
			<input type="hidden" name="procurementId" value="${procurementId}"/>
			</d:content>
	</div>
	</form:form>
</nav:navigationSM>
<div class="overlay"></div>