<%-- This JSP is for Finalize Award Tasks --%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@page errorPage="/error/errorpage.jsp" %>
<%@taglib prefix="render" uri="http://www.bea.com/servers/portal/tags/netuix/render" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<style>
.sumRow{
background-color: #d1d1d1;
border:1px solid black;}
	h2{width:82%}
	.Column2{
		width:680px !important;
		height: 607px;
		min-height: 607px !important;
	}
.Column1{
		min-height: 570px;
	}
.container .container {
    padding: 0 4px;
}
.popupPadding{
	padding: 0 10px 0 10px;
}
.linkPointer{
	cursor: pointer
}
.cancelbtn{
	font-weight: bold;
    color: #fff;
    text-align: center
}
/* .totalSumRow{
background: #E4E4E4;
text-align: right;
}
.totalAmt{
    margin-right: 120px;
} */
/* .finalAmtDiv{
width: 173px;
} */
</style>
<portlet:defineObjects />
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r5/js/awardfinalizetask/finalizeAwardAmount.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jqgridDialogOveride.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/autoNumeric.js"></script>
<portlet:resourceURL var='saveFinalizeAwardComments' id='saveFinalizeAwardComments'
	escapeXml='false'>
</portlet:resourceURL>
<portlet:resourceURL var='cancelAwardOverlay' id='cancelAwardOverlay' escapeXml='false'>
	<portlet:param name="render_action" value="awardsAndContracts" />
	<portlet:param name="action" value="awardContract" />
</portlet:resourceURL>
<input type='hidden' value='${saveFinalizeAwardComments}' id='saveFinalizeAwardComments' />
<portlet:resourceURL var='editFinalizeAmountTask' id='editFinalizeAmountTask'
	escapeXml='false'>
</portlet:resourceURL>
<input type='hidden' value='${editFinalizeAmountTask}' id='hiddenEditAmountOverlayUrl' />
<%--actionURL for events on Award Approval screen--%>
<portlet:actionURL var="finishFinalizeAmountUrl" escapeXml="false">
<portlet:param name="submit_action" value="finishFinalizeAmountTask" />
</portlet:actionURL>
<%--renderURL to view Evaluation Summary--%>
<portlet:renderURL var="viewEvaluationSummaryUrl" escapeXml="false">
	<portlet:param name="controller_action" value="propEval" />
	<portlet:param name="render_action" value="displayEvaluationSummary" />
</portlet:renderURL>
<portlet:actionURL var="viewProposalSummaryUrl" escapeXml="false">
	<portlet:param name="submit_action" value="viewProposalSummary" />
	<portlet:param name="procurementId" value="${procurementId}" />
</portlet:actionURL>
<%--renderURL for rendering to Finalize Award screen--%>
<portlet:renderURL var="finalizeAwardRenderUrl" escapeXml="false"></portlet:renderURL>
<portlet:actionURL var="finalizeAwardReassignURL" escapeXml="false"></portlet:actionURL>
<div class="alert-box alert-box-cancelAward">
<div class='tabularCustomHead'>Cancel Award</div>
<a href="javascript:void(0);" class="exit-panel cancel-Award">&nbsp;</a>
<div id="requestCancel"></div>
</div>
<%
	boolean lbFlag=false;
if (renderRequest.getParameter("controller_action") != null) {
	lbFlag=true;
	}
%> 
<input type="hidden" id="isForCityTask" value="<%=lbFlag%>"/> 
<%-- Cancel Award --%>
<form id="finalizeAwardForm" name="finalizeAwardForm" action="${finishFinalizeAmountUrl}" method ="post">
	<input type="hidden" id="orgType" value="" name="orgType"/>
	<input type="hidden" id="ApprovedAmountFlag" value="${ApprovedAmountFlag}" name="ApprovedAmountFlag"/>
	<input type="hidden" id="cancelAwardOverlay" value="${cancelAwardOverlay}" name="cancelAwardOverlay"/>
	<input type="hidden" id="organizationName" value="${taskDetailsBean.organizationName}" name="organizationName"/>
	<input type="hidden" id="providerList" value="${providerList}" name="providerList"/>
	<input type="hidden" value="${finishFinalizeAmountUrl}" id="finishFinalizeAmountUrl"/>
	<input type="hidden" name="finalizeAwardRenderUrl" id="finalizeAwardRenderUrl" value="${finalizeAwardRenderUrl}" />
	<input type="hidden" name="comments" value="${comments}" id="previousComments"/>
	<input type="hidden" id="viewEvaluationSummaryUrl" value="${viewEvaluationSummaryUrl}"/>
	<input type="hidden" id="viewProposalSummaryUrl" value="${viewProposalSummaryUrl}"/>
	<input type="hidden" id="finalizeAwardReassignURL" value="${finalizeAwardReassignURL}"/>
	<input type="hidden" name="procurementId" id="procurementId" value="${procurementId}"/>
	<input type="hidden" id="workflowId" name="workflowId" value="${workflowId}"/>
	<input type="hidden" name="taskId" value="${taskId}"/>
	<input type="hidden" name="entityId" id="entityId" value="${entityId}"/>
	<input type="hidden" name="agencyId" value="${taskDetailsBean.agencyId}"/>
	<input type="hidden" name="isFirstLaunch" value="${taskDetailsBean.isFirstLaunch}"/>
	<input type="hidden" id="providerId" name="providerId" value="${taskDetailsBean.organizationId}"/>
	<input type="hidden" id="reassignedToUserName" name="reassignedToUserName" value=""/>
	<input type="hidden" id="previousStatus" name="previousStatus" value='${previousStatus}'/>
	<input type="hidden" value="${screenReadOnly}" id="screenReadOnly"/>
	<input type="hidden" value="${taskDetailsBean.procurementTitle}" name="procurementTitle"/>
	<input type="hidden" value="${taskDetailsBean.evaluationPoolMappingId}" id="evaluationPoolMappingId" name="evaluationPoolMappingId"/>
	<input type="hidden" value="${evaluationResultsList[0].statusId}" id="contractID" name="contractID"/>
	<input type="hidden" value="${evaluationResultsList[0].awardReviewStatus}" name="contractTypeId"/>
	<input type="hidden" id="procurementSummaryURL" value="<render:standalonePortletUrl portletUri='/r2/portlet/procurement/procurement.portlet'><render:param name='topLevelFromRequest' value='ProcurementInformation' /><render:param name='midLevelFromRequest' value='ProcurementSummary' /><render:param name='procurementId' value='${procurementId}' /><render:param name='render_action' value='viewProcurement' /><render:param name='hideExitProcurement' value='true' /></render:standalonePortletUrl>"	/>
		<h2>Task Details: <span>${taskDetailsBean.taskName} - ${taskDetailsBean.procurementTitle}</span></h2>
		<div class="linkReturnValut"><a href="javascript:returnToTaskInbox('${workflowId}');">Return</a></div>
		<div class='container'>
		<div class="complianceWrapper">
		<div class="failedShow" id="messagediv" style="display:none"></div>
		<c:if test="${message ne null}">
			<div class="${messageType}" id="errordiv" style="display:block">${message} <img
				src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
				class="message-close" title="Close" alt="Close"
				onclick="showMe('errordiv', this)">
			</div>
		</c:if>
		<div id="ErrorDiv" class="failed breakAll"> </div>
		<!--Filter and Reassign section starts -->
			<div class="tasktopfilter taskButtons">
				<div class="taskfilter">
						<select id="reassignDropDown" name="reassignedTo" onchange="enableDisableReassignButton()">
							<option value=""></option>
							<c:forEach var="reAssignUser" items="${reassignUserMap}">
						    	<option value="${reAssignUser.key}">${reAssignUser.value}</option>
							</c:forEach>					
						</select> 
					<input type="button" id="reassignButton" value="Reassign Task" onclick="reassignTask()" />			 
				</div>
				<div class="taskreassign">
					<input type="button" class="redbtutton cancelbtn" value="Cancel Award"  id="cancelAwardButton"/>
					<input type="button" value="Finish Task" name="finish" id="finishButton" onclick="finishTask()"/>
				</div>
			</div>
		<!--Filter and Reassign section ends --> 
		<c:set var="splitAgencyName" value="${fn:split(taskDetailsBean.agencyName, '-')}" />
		<c:choose>
			<c:when test="${splitAgencyName[1] != null}">
				<c:set var="displayAgencyName" value="${splitAgencyName[1]}" />
			</c:when>
			<c:otherwise>
				<c:set var="displayAgencyName" value="${splitAgencyName[0]}" />
			</c:otherwise>
		</c:choose>
		<!-- Left Column Start -->
			<div class="Column1">
				<h4>Procurement Details</h4>
		 		<label>Procurement Title:</label>
				<div style="word-break: break-all"><a href='javascript:viewProcurementSummary();' class='localTabs'>${taskDetailsBean.procurementTitle}</a></div>
				<label>Agency:</label>
				<div>${procurementBean.agencyName}</div>
				<label>Procurement E-PIN:</label>
				<div style="word-break: break-all">${procurementBean.procurementEpin}</div>
		 		<label>Competition Pool:</label>
				<div style="word-break: break-all">${taskDetailsBean.competitionPoolTitle}</div>
				<label>Agency Contact 1:</label>
				<div style="word-break: break-all"><a href="mailto:${procurementBean.agencyPrimaryEmail}" class='localTabs'>${procurementBean.agencyPrimaryContactName}</a></div>
				<label>Agency Contact 2:</label>
				<div style="word-break: break-all"><a href="mailto:${procurementBean.agencySecondaryEmail}" class='localTabs'>${procurementBean.agencySecondaryContactName}</a></div>
				<label>Provider Name:</label>
				<div><p class="tableAwardAmount">${taskDetailsBean.organizationName}</p></div>
				<div></div>
				<h4>Task Details</h4>
				<label>Task Name:</label>
				<div>${taskDetailsBean.taskName}</div>
				<label>Task Instructions:</label>
				<div>Input final award amount for each proposal listed.</div>
				<label>Assigned To:</label>
				<div>${taskDetailsBean.assignedToUserName}</div>
				<label>Date Assigned:</label>
				<div>${taskDetailsBean.assignedDate}</div>
				<label>Last Modified:</label>
				<div>${taskDetailsBean.lastModifiedDate}</div>
			</div>
			<!-- Left Column End -->
			
			<!-- Center Column Start -->
			<div class='Column2' id="linkDiv">
				<jsp:include page="/WEB-INF/jsp/TaskDetails/procurementSummaryProvider.jsp"></jsp:include>
			</div>
		    	<!-- Site Information End -->
			<!-- Center Column End -->
			<div>&nbsp;</div>
			<div class='clear'>&nbsp;</div>
		  	<!-- Contract(s) Section Starts Here -->
			<div id='contractTabs'>
				<div class="customtabs">
					<ul>
						<li><a href='#EvaluationResults' class="hideButton localTabs" title="Input Award Amount">Input Award Amount</a></li>
						<li><a href='#OtherAwards' title="OtherAwards" class="hideButton localTabs">Other Awards</a></li>
						<li><a href='#Comments' title="Comments" class="showButton localTabs">Comments</a></li>
						<li><a href='#ViewTaskHistory' title="View Task History" class="hideButton localTabs">View Task History</a></li>
						<div class='floatRht' id="saveDiv"><input type="button" id="saveButton" class="graybtutton" value="Save" onclick="saveFinalizeAwardComments()"/></div>
					</ul>
				</div>	
				<div id='OtherAwards'>
					<div class="tabularWrapper">
						<st:table objectName="EvaluationList" cssClass="heading" alternateCss1="evenRows" alternateCss2="oddRows">
							<st:property headingName="Provider Name" columnName="organizationName" align="center" size="20%" />
							<st:property headingName="Award E-PIN" columnName="extEpin" align="right" size="20%" >
								<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.FinalizeAwardExtension" />
							</st:property>
							<st:property headingName="CT#" columnName="extCtNumber" align="right" size="22%" >
							<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.FinalizeAwardExtension" />
							</st:property>
							<st:property headingName="Estimated Amount ($)" columnName="awardAmount" align="right" size="18%" >
							<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.EvaluationResultsExtension" />
							</st:property>
						    <st:property headingName="Final Amount ($)" columnName="negotiatedAmount" align="right" size="20%" >
						    <st:extension decoratorClass="com.nyc.hhs.frameworks.grid.FinalizeAwardExtension" />
							</st:property>
						</st:table>	
						<c:if test="${EvaluationList eq null or fn:length(EvaluationList) eq 0}">
							<div class="noRecord">There are currently no other approved awards in this competition pool.</div>
						</c:if>
					</div>						
				</div>
				<d:content isReadOnly="${screenReadOnly}">
				<div id='EvaluationResults'>
					<div class="tabularWrapper evaluationResultDiv clear" id="inputAwardAmountTableDiv">
						<st:table objectName="evaluationResultsList" cssClass="heading" alternateCss1="evenRows" alternateCss2="oddRows">
							<st:property headingName="Proposal ID" columnName="proposalId" align="center" size="10%" />
							<st:property headingName="Provider Name" columnName="organizationName" align="right" size="20%" />
							<st:property headingName="Proposal Title" columnName="proposalTitle" align="center" size="15%">
								<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.EvaluationResultsExtension" />
							</st:property>
							<st:property headingName="Evaluation Score" columnName="evaluationScore" align="right" size="7%" />
							<st:property headingName="Estimated Amount ($)" columnName="awardAmount" align="center" size="24%">
								<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.EvaluationResultsExtension" />
							</st:property>						
							<st:property headingName="Final Amount ($)" columnName="negotiatedAmount" align="right" size="24%">
								<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.EvaluationResultsExtension" />
							</st:property>
						</st:table>
						<c:if test="${evaluationResultsList eq null or fn:length(evaluationResultsList) eq 0}">
							<div class="noRecord">No record found.</div>
						</c:if>
					</div>
				</div>
				
				
				<div id='Comments'>
					<div>
						<h3>Enter any internal comments:</h3>
						<p>Enter any comments. These comments will be available to the Accelerator. Click the &quot;Save&quot; button above to save your comments.</p>
						<div style='height:40px'>&nbsp;</div>
						<textarea rows="5" class='textarea' id="internalComments" name="internalComments" onkeyup="setMaxLength(this,1000)" onkeypress="setMaxLength(this,1000)">${internalComments}</textarea>
					</div>
				</div>
				</d:content>
				<div id='ViewTaskHistory'>
					<div class="tabularWrapper">
						<st:table objectName="taskHistoryList" cssClass="heading" alternateCss1="evenRows" alternateCss2="oddRows">
							<st:property headingName="Task" columnName="taskName" align="center" size="25%" />
							<st:property headingName="Action" columnName="action" align="right" size="20%" />
							<st:property headingName="Detail" columnName="detail" align="right" size="25%" />
							<st:property headingName="User" columnName="user" align="right" size="10%" />
							<st:property headingName="Date/Time" columnName="dateTime" align="right" size="20%" />
						</st:table>	
					</div>						
				</div>
			</div>
	</div>
	</div>
		<div class="alert-box alert-box-editAward skipElementsInCompare">
			<div class='tabularCustomHead'>Input Final Amount</div>
			<a href="javascript:void(0);" class="exit-panel">&nbsp;</a>
			<div id="requestEditAmmount" class="popupPadding">
				<div class='hr'></div>		
				<div class="failed" id="errorMsg"></div>
				<p>Please review the information below and enter the final proposal amount following negotiations with the provider.</p>
				<p><label class='required'>&#42;</label>Indicates required fields</p>
				<input type='hidden' id='selectProposalId' name='selectProposalId' value='${selectProposalId}'/>
			<div class="formcontainer">
				<div class="row">
					<span class="label" >Provider Name:</span> <span class="formfield" id="providerName"></span>
				</div>
				<div class="row">
					<span class="label" >Proposal Title:</span> <span class="formfield" id="propsalTitle"></span>
				</div>
				<div class="row">
					<span class="label" >Evaluation Score:</span> <span
						class="formfield" id="score"></span>
				</div>
				<div class="row">
					<span class="label" >Estimated Amount ($):</span> <span
						class="formfield" id="amount"></span>
				</div>
				<div class="row">
					<span class="label">Previous Selection Comments:</span> <span
						class="formfield awardAmt" id="proposalComments"></span>
				</div>
				<div class="row">
					<span class="label"><label class='required'>&#42;</label>Final Amount($):</span> <span class="formfield">
						<input type="text" rows="1" cols="45" name="AMOUNT" style="width:124px;" id="finalAmount"/> </span>
						<span class="error">
				</div>
			</div>
			<div class="buttonholder">
				<input type="button" class="graybtutton" id="cancelAward"
					value="Cancel" onclick="cancelOverLay();" />
				<input type="submit"
					class="button" id="confirmAward" value="Confirm"/>
			</div>
		</div>
		</div>
</form>


