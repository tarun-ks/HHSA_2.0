<%-- This JSP is for Award Approval Tasks --%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@page errorPage="/error/errorpage.jsp" %>
<%@taglib prefix="render" uri="http://www.bea.com/servers/portal/tags/netuix/render" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<style>
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
.taskComments2 p{
	line-height:16px
}
</style>
<portlet:defineObjects />
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/framework/skeletons/hhsa/js/awardApprovalTask.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/autoNumeric.js"></script>
<%--actionURL for events on Award Approval screen--%>
<portlet:actionURL var="approveAwardUrl" escapeXml="false"></portlet:actionURL>
<%--resourceURL to view Selection comments--%>
<portlet:resourceURL var="viewSelectionComments" id="viewSelectionComments"></portlet:resourceURL>
<%--renderURL to view Evaluation Summary--%>
<%--Changes done for Defect #6460 for Release 3.3.0--%>
<portlet:renderURL var="viewEvaluationSummaryUrl" escapeXml="false">
	<portlet:param name="controller_action" value="propEval" />
	<portlet:param name="render_action" value="displayEvaluationSummary" />
</portlet:renderURL>
<%--actionURL to view Proposal Summary--%>
<%--Changes done for Defect #6460 for Release 3.3.0--%>
<portlet:actionURL var="viewProposalSummaryUrl" escapeXml="false">
	<portlet:param name="controller_action" value="propEval" />
	<portlet:param name="submit_action" value="viewProposalSummary" />
	<portlet:param name="procurementId" value="${procurementId}" />
</portlet:actionURL>
<%--renderURL for rendering to Award Approval screen--%>
<portlet:renderURL var="approveAwardRenderUrl" escapeXml="false"></portlet:renderURL>
<form id="approveAwardForm" name="approveAwardForm" action="${approveAwardUrl}" method ="post">
	<input type="hidden" id="orgType" value="" name="orgType"/>
	<input type="hidden" id="submit_action" value="" name="submit_action"/>
	<input type="hidden" value="${approveAwardUrl}" id="approveAwardUrl"/>
	<input type="hidden" id="viewSelectionComments" value="${viewSelectionComments}"/>
	<input type="hidden" id="viewEvaluationSummaryUrl" value="${viewEvaluationSummaryUrl}"/>
	<input type="hidden" id="viewProposalSummaryUrl" value="${viewProposalSummaryUrl}"/>
	<input type="hidden" id="approveAwardRenderUrl" value="${approveAwardRenderUrl}"/>
	<input type="hidden" name="procurementId" id="procurementId" value="${procurementId}"/>
	<input type="hidden" id="workflowId" name="workflowId" value="${workflowId}"/>
	<input type="hidden" name="taskId" value="${taskId}"/>
	<input type="hidden" name="awardTaskName" id="taskName" value="${taskName}"/>
	<input type="hidden" name="agencyId" value="${taskDetailsBean.agencyId}"/>
	<input type="hidden" name="isFirstLaunch" value="${taskDetailsBean.isFirstLaunch}"/>
	<input type="hidden" name="isFirstReached" value="${taskDetailsBean.isFirstReached}"/>
	<input type="hidden" id="reassignedToUserName" name="reassignedToUserName" value=""/>
	<input type="hidden" id="previousStatus" name="previousStatus" value='${previousStatus}'/>
	<input type="hidden" value="${screenReadOnly}" id="screenReadOnly"/>
	<input type="hidden" value="${taskDetailsBean.procurementTitle}" name="procurementTitle"/>
	<input type="hidden" value="${taskDetailsBean.isNegotiationRequired}" id="negotiationField"/>
	<input type="hidden" value="${taskDetailsBean.evaluationPoolMappingId}" id="evaluationPoolMappingId" name="evaluationPoolMappingId"/>
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
					<select id="finishDropDown" name="finishStatus" onchange="enableDisableNegotiateDropDown()">
						<option value=""></option>
						<c:forEach var="finishStatus" items="${finishStatusMap}">
							<option value="${finishStatus.key}">${finishStatus.value}</option>
						</c:forEach>
					</select>
					<!-- R5 change starts -->
					<select id="negotiateDropDown" name="IsNegotiationRequired"
						onchange="enableDisableFinishButton()">
						<option></option>
						<!-- Changes for 7325 starts -->
						<c:choose>
							<c:when test="${taskDetailsBean.isNegotiationRequired eq 'true'}">
								<option value="true">Negotiations Required</option>
							</c:when>
							<c:when
								test="${(taskDetailsBean.isNegotiationRequired eq 'false') and (taskDetailsBean.isFirstLaunch eq 'false')}">
								<option value="false">Negotiations Not Required</option>
							</c:when>
							<c:otherwise>
								<option value="true">Negotiations Required</option>
								<option value="false">Negotiations Not Required</option>
							</c:otherwise>
						</c:choose>
						<!-- Changes for 7325 ends -->
					</select>
					<!-- R5 change ends -->
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
				<div>${displayAgencyName}</div>
				<label>Procurement E-PIN:</label>
				<div style="word-break: break-all">${taskDetailsBean.procurementEpin}</div>
				<c:if test="${(taskDetailsBean.isOpenEndedRfp ne null) and (taskDetailsBean.isOpenEndedRfp eq '1')}">
					<label>Evaluation Group:</label>
					<div style="word-break: break-all">${taskDetailsBean.evaluationGroupTitle}</div>
				</c:if>
		 		<label>Competition Pool:</label>
				<div style="word-break: break-all">${taskDetailsBean.competitionPoolTitle}</div>
				<label>Agency Contact 1:</label>
				<div style="word-break: break-all"><a href="mailto:${taskDetailsBean.agencyPrimaryContactId}" class='localTabs'>${taskDetailsBean.agencyPrimaryContact}</a></div>
				<label>Agency Contact 2:</label>
				<div style="word-break: break-all"><a href="mailto:${taskDetailsBean.agencySecondaryContactId}" class='localTabs'>${taskDetailsBean.agencySecondaryContact}</a></div>
				<label>Total Award Amount:</label>
				<div><p class="tableAwardAmount">${taskDetailsBean.awardAmount}</p></div>
				<div></div>
				<h4>Task Details</h4>
				<label>Task Name:</label>
				<div>${taskDetailsBean.taskName}</div>
				<label>Task Instructions:</label>
				<div>Review Agency award selections below.</div>
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
						<li><a href='#EvaluationResults' class="hideButton localTabs" title="Evaluation Results & Scores">Evaluation Results &amp; Scores</a></li>
						<li><a href='#Comments' title="Comments" class="showButton localTabs">Comments</a></li>
						<li><a href='#ViewTaskHistory' title="View Task History" class="hideButton localTabs">View Task History</a></li>
						<div class='floatRht' id="saveDiv"><input type="button" id="saveButton" class="graybtutton" value="Save" onclick="saveApproveAwardTaskDetails()"/></div>
					</ul>
				</div>	
				<div id='EvaluationResults'>
					<div class="tabularWrapper evaluationResultDiv clear">
						<st:table objectName="evaluationResultsList" cssClass="heading" alternateCss1="evenRows" alternateCss2="oddRows">
							<st:property headingName="Proposal Title" columnName="proposalTitle" align="center" size="20%">
								<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.EvaluationResultsExtension" />
							</st:property>
							<st:property headingName="Provider Name" columnName="organizationName" align="right" size="15%" />
							<st:property headingName="Evaluation Score" columnName="evaluationScore" align="right" size="10%" />
							<st:property headingName="Proposal Status" columnName="proposalStatus" align="right" size="10%" />
							<st:property headingName="Award Amount($)" columnName="awardAmount" align="right" size="15%">
								<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.EvaluationResultsExtension" />
							</st:property>
							<st:property headingName="Evaluation Summary" columnName="evaluationSummary" align="right" size="20%">
								<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.EvaluationResultsExtension" />
							</st:property>
							<st:property headingName="Comments" columnName="comments" align="right" size="10%">
								<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.EvaluationResultsExtension" />
							</st:property>
						</st:table>
					</div>				
				</div>
				<div id='Comments'>
					<div>
						<h3>Enter any internal Comments:</h3>
						<p>Enter any review comments. These comments will be available to the ACCO. Click the &quot;Save&quot; button above to save your comments.</p>
						<div style='height:40px'>&nbsp;</div>
						<textarea rows="5" class='textarea' id="internalComments" name="internalComments" onkeyup="setMaxLength(this,250)" onkeypress="setMaxLength(this,250)">${internalComments}</textarea>
					</div>
				</div>
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
</form>
<div class="overlay"></div>
<%--View Comments Overlay --%>
<div class="alert-box alert-box-viewComments">
     <div class="content">
          <div class="tabularCustomHead">View Comments</div> 
          <div class='tabularContainer'>
              <p>Below are the comments for the following Proposal:</p>
              <div class="formcontainer">
	              <div class="row">
					  <span class="label">Provider Name:</span> 
				      <span class="formfield commentclass1"></span>
			  	  </div>
			  	  <div class="row">
					  <span class="label">Proposal Title:</span>
					  <span class="formfield commentclass2"></span>
			      </div>
			      <div class="row">
					  <span class="label">Comments:</span> 
					  <span class="formfield commentclass3"></span>
			      </div>
	          </div> 
	      </div>                 
     </div>
     <a  href="javascript:void(0);" class="exit-panel upload-exit" title="Exit">&nbsp;</a>
</div>
<%--Confirm Override Overlay --%>
<div class="alert-box alert-box-confirmOverride">
     <div class="content">
           <div class="tabularCustomHead">Confirm Override</div> 
           <div class='tabularContainer'>
               <h2>Confirm Override</h2>
               <div class='hr'></div>
               <p>Are you sure you want to approve the selected providers and not use the HHS Accelerator system for Financial related functions for the resulting contracts?</p>
               <br/>
               <p>Once confirmed, the resulting awards will not be automatically converted for Financial processing.</p>
               <br/>
               <p>
				   <input type="checkbox" name="accept" id="validateCheckbox">
				   <label for='validateCheckbox'>Yes, do not convert this award for Financial processing. I understand this action cannot be reversed.</label>
			   </p>
               <div class="buttonholder">
                    <input type="button" class="graybtutton" value="Cancel" onclick="cancelOverLay();"/> 
                    <input type="submit" id="confirmOverride" value="Yes, I confirm that this will not use Financials" onclick="confirmOverride();"/>
               </div>
           </div>                  
     </div>
     <a  href="javascript:void(0);" class="exit-panel upload-exit" title="Exit">&nbsp;</a>
</div>

