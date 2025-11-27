<%-- This JSP is for Evaluate Proposal Tasks --%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!-- R5 starts-->
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!-- R5 ends-->
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@page errorPage="/error/errorpage.jsp" %>
<%@taglib prefix="render" uri="http://www.bea.com/servers/portal/tags/netuix/render" %>
<style>
	.Column2{
		width:680px !important;
		height: 607px;
		min-height: 607px !important;
	}
	.Column1{
		min-height: 570px;
	}
.scoreAndCommentRow{
	clear:both;
}
.scoreWrapper #currentRoundInfo span.criteriaCls{
	background-color:#F8F8F8 !important;
	float:left;
	padding: 7px 0 7px 0;
}

.scoreWrapper #currentRoundInfo span.criteriaScrCls{
	background-color:#EDEDED !important;
	float:left;
}

.criteriaScrCls h2{
	padding-left:2px;
}

.extraPadding{
	padding-bottom:4px;
}
</style>

<portlet:defineObjects />
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/evaluateProposalTask.js"></script>
<!-- R5 starts -->
<portlet:resourceURL var="getEvaluatorRoundDetails" id="getEvaluatorRoundDetails" escapeXml="false">
</portlet:resourceURL>

<input type="hidden" name="getEvaluatorRoundDetails" id="getEvaluatorRoundDetailsUrl" value="${getEvaluatorRoundDetails}" />
<!-- R5 Ends -->
<%--actionURL for events on Evaluate Proposal screen--%>
					<%--code updation for R4 starts--%>
<script>
var contextPathVariablePath = "<%=request.getContextPath()%>";
function viewRFPDocument(documentId, documentName) {
	removeTaskSelectedClass();
	document.getElementById(documentId).className = "taskSelected";
	window.open($("#contextPathSession").val()+"/GetContent.jsp?action=displayDocument&documentId=" + documentId+ "&documentName=" + documentName);
}
//R5 starts
function onChangeRound(obj){
	pageGreyOut();
	if(obj.value != ""){
		$('#rtnCrrScore').show();
		var _round = obj.value;
		if(isEvalRoundEmpty(_round)){
			var v_parameter = "&versionNumber=" + _round + "&evaluationStatusId=" + '${taskDetailsBean.evaluationStatusId}';
			var urlAppender = $("#getEvaluatorRoundDetailsUrl").val();
			jQuery.ajax({
				type : "POST",
				url : urlAppender,
				data : v_parameter,
				success : function(result) {
					hideNotRequiredRound(_round);
					$('#evaluationRoundDetail_'+_round).html(result);
					removePageGreyOut();
				},
				error : function(result) {
					showErrorMessagePopup();
					removePageGreyOut();
				}
			});
		}else{
			hideNotRequiredRound(_round);
			removePageGreyOut();
		}
	}else{
		$('#rtnCrrScore').hide();
		hideAllExistingRound();
		$('#currentRoundInfo').show();
		removePageGreyOut();
	}
}
	function hideNotRequiredRound(round){
		$('#currentRoundInfo').hide();
		hideAllExistingRound();
		$('#evaluationRoundDetail_'+round).show();
	}
	
	function hideAllExistingRound(){
		<c:forEach  var="evaluatorRoundDetailsVar" items="${evaluatorRoundDetails}">
			$('#evaluationRoundDetail_'+'${evaluatorRoundDetailsVar.versionNumber}').hide();
		</c:forEach>
	}
	function isEvalRoundEmpty(round){
		var isEmpty = true;
		var _evalHtml = $.trim($('#evaluationRoundDetail_'+round).html());
		if(_evalHtml != "" && _evalHtml != null){
			isEmpty = false;
		}
		return isEmpty;
	}
//R5 Ends
</script>
					<%--code updation for R4 ends--%>
<portlet:actionURL var="evaluateProposalUrl" escapeXml="false">
</portlet:actionURL>
<%--actionURL to view Proposal Summary--%>
<portlet:actionURL var="viewProposalSummaryUrl" escapeXml="false">
	<portlet:param name="submit_action" value="viewProposalSummary" />
	<portlet:param name="procurementId" value="${procurementId}" />
	<portlet:param name="proposalId" value="${proposalId}" />
</portlet:actionURL>
<%--renderURL for rendering to Agency Task List screen--%>
<portlet:renderURL var="evaluateProposalRenderUrl" escapeXml="false"></portlet:renderURL>
<form:form commandName="ScoreDetailsBean" id="evaluateProposalForm" name="evaluateProposalForm" action="${evaluateProposalUrl}" method ="post">	
	<div class="skipElementsInCompare">
	<!-- R5 starts-->
	<input type="hidden" id="totalRound" name="totalRound" value="${(fn:length(evaluatorRoundDetails))}"/>
	<!-- R5 Ends-->
	<input type="hidden" id="fromSaveButton" name="fromSaveButton" value="${fromSaveButton}"/>
	<input type="hidden" id="confirmScoresTab" name="confirmScoresTab" value="${confirmScoresTab}"/>
	<input type="hidden" id="submit_action" name="submit_action" value=""/>
	<input type="hidden" id="hiddenDocumentId" value="" name="documentId"/>
	<input type="hidden" id="docTypeHidden" value="" name="docTypeHidden"/>
	<input type="hidden" id="orgType" value="" name="orgType"/>	
	<input type="hidden" name="procurementId" value="${procurementId}"/>	
	<input type="hidden" name="proposalId" value="${proposalId}"/>
	<input type="hidden" name="workflowId" value="${workflowId}"/>
	<input type="hidden" name="taskId" value="${taskId}"/>
	<!--Start || Changes done for enhancement QC : 5688 for Release 3.2.0-->
	<input type="hidden" id="controller_action" name="controller_action"/>
	<input type="hidden" value="${screenReadOnly}" id="screenReadOnly"/>
	<!--End || Changes done for enhancement QC : 5688 for Release 3.2.0-->
	<input type="hidden" name="evaluationStatusId" value="${taskDetailsBean.evaluationStatusId}"/>
	<input type="hidden" id="proposalTaskStatus" name="proposalTaskStatus" value='${proposalTaskStatus}'/>
	<input type="hidden" value="${viewProposalSummaryUrl}" id="viewProposalSummaryUrl"/>
	<input type="hidden" id="evaluateProposalRenderUrl" value="${evaluateProposalRenderUrl}"/>
	<input type="hidden" id="procurementSummaryURL" value="<render:standalonePortletUrl portletUri='/r2/portlet/procurement/procurement.portlet'><render:param name='topLevelFromRequest' value='ProcurementInformation' /><render:param name='midLevelFromRequest' value='ProcurementSummary' /><render:param name='procurementId' value='${procurementId}' /><render:param name='render_action' value='viewProcurement' /><render:param name='hideExitProcurement' value='true' /></render:standalonePortletUrl>"	/>
	</div>
	<%-- Body Container Starts --%>
		<h2>
			<label class='floatLft'>Task Details: 
				<label>${taskDetailsBean.taskType} <c:if test ='${evaluatorName ne null}'>(proxy)</c:if> - ${taskDetailsBean.procurementTitle}</label>
			</label>
			<span class="linkReturnVault floatRht"><a href="javascript:returnToAgencyTaskList('${workflowId}');">Return</a></span>
		</h2>
		<c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
		<d:content section="${helpIconProvider}">
			<div id="helpIcon" class="iconQuestion"><a id="helpIcn" href="javascript:void(0);" title="Need Help?" onclick="smFinancePageSpecificHelp();"></a></div>
			<input type="hidden" id="screenName" value="Task Details - Evaluate Proposal" name="screenName"/>
		</d:content>
		<%-- Messages --%>
		<div class="failed" id="failedmessagediv" style="display:none"></div>
		<c:if test="${message ne null}">
			<div class="${messageType}" id="errordiv" style="display:block">${message} <img
				src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
				class="message-close" 
				onclick="showMe('errordiv', this)">
			</div>
		</c:if>
		<div class="complianceWrapper">
			<%--Filter section starts --%>
				<div class="tasktopfilter taskButtons">				
					<div class="taskreassign">
						<b>Status:</b> <label><c:out value="${proposalTaskStatus}"/></label>
					</div>
				</div>
			<%--Filter section ends --%> 
		
		<%-- Left Column Start --%>
			<div class="Column1">
				<h4>Procurement Details</h4>
		 		<label>Procurement Title:</label>
				<div style="word-break: break-all"><a href='javascript:viewProcurementSummary(${taskDetailsBean.procurementId});' class='localTabs'>${taskDetailsBean.procurementTitle}</a></div>
								<%--code updation for R4 starts--%>
				<c:if test="${(taskDetailsBean.isOpenEndedRfp ne null) and (taskDetailsBean.isOpenEndedRfp eq '1')}">
					<label>Evaluation Group:</label>
					<div style="word-break: break-all">${taskDetailsBean.evaluationGroupTitle}</div>
				</c:if>
				<label>Competition Pool:</label>
				<div style="word-break: break-all">${taskDetailsBean.competitionPoolTitle}</div>
									<%--code updation for R4 ends--%>
				<label>Provider Name:</label>
				<div style="word-break: break-all">${taskDetailsBean.organizationName}</div>
		        <label>Proposal Title:</label>
				<div style="word-break: break-all">${taskDetailsBean.proposalTitle}</div>
				
				<%-- Start || Changes done for enahncement 6636 for Release 3.12.0 --%>
				<label>Proposal Id:</label>
				<div style="word-break: break-all">${taskDetailsBean.proposalId}</div>
				<%-- End || Changes done for enahncement 6636 for Release 3.12.0 --%>
				
				<label>Procurement E-PIN:</label>
				<div style="word-break: break-all">${taskDetailsBean.procurementEpin}</div>
									<%--code updation for R4 starts--%>
				<c:if test="${(taskDetailsBean.isOpenEndedRfp eq null) or (taskDetailsBean.isOpenEndedRfp eq '0')}">
					<label>First Round of Evaluation Completion Date:</label>
					<div>${taskDetailsBean.firstRoundEvalCompDate}</div>
				</c:if>
									<%--code updation for R4 ends--%>
				<div></div>
				
			<%-- Task Details started --%>			
				<h4>Task Details</h4>
				<label>Task Name:</label>
				<div>${taskDetailsBean.taskType} <c:if test ='${evaluatorName ne null}'>(proxy)</c:if></div>
				<label>Task Instructions:</label>
				<div>Enter scores and comments using the tabs below. Click on Proposal Document names to open and view the documents.</div>
				<c:if test ='${evaluatorName ne null}'>
					<label>External Evaluator:</label>
					<div>${evaluatorName}</div>
				</c:if>
				<label>Assigned To:</label>
				<div>${taskDetailsBean.assignedToUserName}</div>
				<label>Date Assigned:</label>
				<div>${taskDetailsBean.assignedDate}</div>
				<label>Last Modified:</label>
				<div>${taskDetailsBean.lastModifiedDate}</div>
			</div>
			<%-- Left Column End --%>
			
			<%-- Center Column Start --%>
			<div class='Column2' id="linkDiv"></div>
		    	<%-- Site Information End --%>
			<%-- Center Column End --%>
			
			<p class='clear'>&nbsp;</p>
			
		  	<%-- Contract(s) Section Starts Here --%>
			<div id='contractTabs'>
			
				<%-- Tabs Started --%>
				<div class="customtabs">
					<ul>
						<li><a href='#ProposalDocuments' class="hideButton localTabs">Proposal Documents</a></li>
						<li><a href='#RFPDocuments' class="hideButton localTabs">RFP Documents</a></li>				
						<li class="showButtonLi"><a href='#Scores' class="showButton localTabs">Scores & Comments</a></li>
						<%--Enhancement 5415, new tab added for confirm scores--%> 
						<li class="showButtonLi"><a href='#ConfirmScores' class="hideButton localTabs">Confirm Scores</a></li>
						<li class='liBorderNone'></li>
						<div class='floatRht' id="saveDiv"><input type="button" id="saveButton" class="graybtutton" value="Save" onclick="saveEvaluationTask()"/></div>										
					</ul>
				</div>	
				
				<div id='ProposalDocuments'>
					<div class="tabularWrapper">
						<st:table objectName="proposalDocumentDetailList" cssClass="heading" alternateCss1="evenRows" alternateCss2="oddRows">
							<st:property headingName="Details/Document Name" columnName="documentTitle" align="center" size="25%">
								<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.EvaluateProposalDocumentsExtension" />
							</st:property>
							<%--Release 3.6.0 Enhancement 6485, columnName changed to customLabelName from documentType--%> 
							<st:property headingName="Document Type" columnName="customLabelName" align="right" size="25%" />
							<st:property headingName="Required/Optional?" columnName="isRequiredDoc" align="right" size="20%" />
							<st:property headingName="Document Info" columnName="documentId" align="right" size="10%">
								<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.EvaluateProposalDocumentsExtension" />
							</st:property>							
							<st:property headingName="Modified" columnName="modifiedDate" align="right" size="20%" />							
						</st:table>
					</div>				
				</div>
				<div id='RFPDocuments'>
					<div class="tabularWrapper" id="refreshRFPDocuments">
						<st:table objectName="rfpDocumentsList" cssClass="heading" alternateCss1="evenRows" alternateCss2="oddRows">
							<st:property headingName="Document Name" columnName="documentTitle" align="center" size="25%">
								<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.ProposalDocumentsExtension" />
							</st:property>
							<st:property headingName="Document Type" columnName="documentType" align="right" size="20%" />
							<st:property headingName="Document Info" columnName="documentId" align="right" size="15%">
								<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.ProposalDocumentsExtension" />
							</st:property>
							<st:property headingName="Modified" columnName="modifiedDate" align="right" size="20%" />
							<st:property headingName="Last Modified By" columnName="lastModifiedByName" align="right" size="20%" />
						</st:table>		
					</div>						
				</div>
					<%--code updation for R4 starts--%>
				<%--Enhancement 5415, div restructured--%> 
				<div id='Scores' class="scoreWrapper">
				<div>Please enter comments and scores in the fields below.
				Comments should be concise and are considered public information.
				Scores must not exceed the maximum points for any Criteria. Click the
				&quot;Save&quot; button above to save your scores and comments.</div>
				<div>&nbsp;</div>
				<div style="color: red; margin-bottom:8px">In order to finish this task, please
				navigate to the 'Confirm Scores' tab once all fields are completed and
				submit your Evaluation.</div>
				<!-- R5 starts-->
				View Previous Iteration:	<select style="margin-bottom: 5px;" onchange="onChangeRound(this);">
														<option selected="selected" value="">Current Scores</option>
														<c:forEach  var="evaluatorRoundDetailsVar" items="${evaluatorRoundDetails}">
															        <option value="${evaluatorRoundDetailsVar.versionNumber}">${evaluatorRoundDetailsVar.roundInfo}</option>
														</c:forEach>
											</select> 
				<b style="display: none;" id="rtnCrrScore">Return to Current Scores to edit</b>
					
				<div id="currentRoundInfo">
				<!-- R5 Ends-->
					<c:set var="i" value="0" />
					<c:set var="screenReadOnly" value= "${screenReadOnly}"/>
					<c:choose>
						<c:when test="${screenReadOnly eq 'true' }">
							<c:set var="screenReadOnly" value="true" />
						</c:when>
						<c:otherwise>
							<c:set var="screenReadOnly" value="false" />
						</c:otherwise>
					</c:choose>
					<c:forEach var="scoreDetails"
						items="${ScoreDetailsBean.miEvaluationBeanList}">
						<input type="hidden" id="scoreHidden${i}"
							value="${ScoreDetailsBean.miEvaluationBeanList[i].score}" />
						<form:hidden path="miEvaluationBeanList[${i}].scoreAmended"
							id="scoreAmendedId${i}" />
						<form:hidden path="miEvaluationBeanList[${i}].scoreSeqNum" />
						<form:hidden path="miEvaluationBeanList[${i}].scoreCriteria" />
						<form:hidden path="miEvaluationBeanList[${i}].evaluationStatusId" />
						<form:hidden path="miEvaluationBeanList[${i}].evaluationCriteriaId" />
						<div class="scoreAndCommentRow">
							<span class='criteriaCls' style='width: 80%; word-wrap: break-word;'>
							<h2><label id="missingScoreOrCmnts" class='error floatLft'
								style="display: none;"><b>!</b></label><label >Criteria
							${i+1}: <label>${scoreDetails.scoreCriteria} </label></label></h2>
							</span>
							<span class='criteriaScrCls' style='width: 20%;'>
							<h2><label class='floatLft'>Score:<label id="maxScore">
							<form:input class="txt" validate="number" type="text" size="1" style='text-align: right;'
								path="miEvaluationBeanList[${i}].score" maxlength="3" 
								id="scoreSeqNum[${i}]" />/${scoreDetails.maximumScore}</label></label></h2>
							</span>
						</div>
						<div class="scoreAndCommentRow extraPadding">
							<span colspan='2'><form:textarea class="scorecmnts" readonly="${screenReadOnly}"
								path="miEvaluationBeanList[${i}].comments" rows="5"
								cssClass="textarea" id="comments_${i}" 
								onblur="setMaxLengthForEvaluatePropasal(this,1500);"
								onkeyup="setMaxLengthForEvaluatePropasal(this,1500);"
								onkeypress="setMaxLengthForEvaluatePropasal(this,1500);" /></span>
						</div>
						<c:set var="i" value="${i+1}" />
					</c:forEach>
					<div class="scoreAndCommentRow">
						<span class='criteriaCls' style='width: 80%; word-wrap: break-word;'>
						<h2><label id="internalCmnts" class='error floatLft'
							style="display: none"><b>!</b></label><label class='floatLft'>General
						Comments:</label></h2>
						</span>
			
						<span class='criteriaScrCls'  style='width: 20%;padding: 7px 0 7px 0;'>
						<h2><label class='floatLft'>Total Score: <label
							id="totalScore">0</label></label></h2>
						</span>
			
					</div>
					<div class="scoreAndCommentRow extraPadding">
						<span colspan='2'><form:textarea path="internalComments" rows="5" readonly="${screenReadOnly}"
							cssClass="textarea" id="internalComments" 
							onblur="setMaxLengthForEvaluatePropasal(this,4000);"
							onkeyup="setMaxLengthForEvaluatePropasal(this,4000);"
							onkeypress="setMaxLengthForEvaluatePropasal(this,4000);" /></span>
					</div>
				</div>
				<!-- R5 starts-->
				<c:forEach  var="evaluatorRoundDetailsVar" items="${evaluatorRoundDetails}">
				        <div id="evaluationRoundDetail_${evaluatorRoundDetailsVar.versionNumber}"> </div>
				</c:forEach>
				<!-- R5 Ends-->
				</div>

				<div id='ConfirmScores'>
									<%--code updation for R4 ends--%>
					
					<h3>Please review and confirm the following:</h3>
					<p text-align="justify">
					New York City Procurement rules require that all proposals be reviewed and rated based only on the criteria and other factors,<br>
					if any, prescribed in the RFP. Any personal knowledge about a proposer relevant to a prescribed criterion <br>
					&#40;or applicable subcriterion&#41; or factor &#40;e.g. prior performance&#41; must communicated with the Agency Chief Contracting Officer.<br>
					Such knowledge must not have any bearing on the scoring of the Proposal, which must be based solely on the content and quality.<br>
					Proposals must be independently read and awarded points according to the criteria prescribed in this task.</p>		
					
					<p>
						<input type="checkbox" name="accept" id="validateCheckbox">
						<label for='validateCheckbox'>Yes, I have read and accept the above conditions.</label>
					</p>
					
				<div>&nbsp;</div>
					<div id="authenticate" class="formcontainer skipElementsInCompare">
						<div class="row">
				      		<span class="label">User Name:</span>
				      		<span class="formfield"> 
				      			<input id="userName" size="5" type="text" name="userName" maxlength="64" class="input" autocomplete="off" >
				      		</span>
					      	<span class="formfield error"></span>
						</div>
						<div class="row">
				      		<span class="label">Password:</span>
				      		<span class="formfield"> 
				      			<input id="password" size="5" maxlength="64" name="password" type="password" class="input" autocomplete="off" />
				      		</span>
					      	<span class="formfield error"></span>
						</div>
						<%--Filter section starts --%>
						<div class="taskreassign">
							<input type="button" class="graybutton" value="Finish Task" name="finish" id="finishButton" onclick="finishEvaluationTask()"/>
						</div>
						<%--Filter section ends --%>
					</div>					
				</div>			
			</div>	
		</div>		
</form:form>

<%--  Overlay Popup --%>
<div class="overlay"></div>
<%--View Document Properties Overlay --%>
<div class="alert-box alert-box-viewDocumentProperties">
	<div class="content">
		<div class="tabularCustomHead">View Document Information</div>
		<div id="viewDocumentProperties"></div>
	</div>
	<a  href="javascript:void(0);" class="exit-panel upload-exit" onclick="closeOverLayInfo()">&nbsp;</a>
</div>
<%--Help Overlay --%>
<div class="alert-box-help">
	<div class="tabularCustomHead">Procurement - Help Documents</div>
    <div id="helpPageDiv"></div>
 	<a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
</div>