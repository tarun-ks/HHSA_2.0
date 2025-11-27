<%-- This JSP is for Accept Proposal Tasks --%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@page errorPage="/error/errorpage.jsp" %>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@taglib prefix="render" uri="http://www.bea.com/servers/portal/tags/netuix/render" %>
<style>
	.Column2{
	  /* fix for defect-6929 */
		height: 680px;
		min-height: 607px !important;
	}
.Column1{
		min-height: 570px;
	}
</style>
<portlet:defineObjects />
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/acceptProposalTask.js"></script>
<%--actionURL for events on Accept Proposal screen--%>
	<%--code updation for R4 starts--%>
<script>
var contextPathVariablePath = "<%=request.getContextPath()%>";
function viewRFPDocument(documentId, documentName) {
	removeTaskSelectedClass();
	document.getElementById(documentId).className = "taskSelected";
	window.open($("#contextPathSession").val()+"/GetContent.jsp?action=displayDocument&documentId=" + documentId+ "&documentName=" + documentName);

}
</script>
	<%--code updation for R4 ends--%>
<portlet:actionURL var="acceptProposalUrl" escapeXml="false">
</portlet:actionURL>
<%--actionURL to view Proposal Summary--%>
<portlet:actionURL var="viewProposalSummaryUrl" escapeXml="false">
	<portlet:param name="submit_action" value="viewProposalSummary" />
	<portlet:param name="procurementId" value="${taskDetailsBean.procurementId}" />
	<portlet:param name="proposalId" value="${taskDetailsBean.proposalId}" />
</portlet:actionURL>
<%--renderURL for rendering to Agency Task List screen--%>
<portlet:renderURL var="acceptProposalRenderUrl" escapeXml="false"></portlet:renderURL>
<form id="acceptProposalForm" name="acceptProposalForm" action="${acceptProposalUrl}" method ="post">
	<div class="skipElementsInCompare">
		<input type="hidden" id="hiddenDocumentId" value="" name="documentId"/>
		<input type="hidden" id="docTypeHidden" value="" name="docTypeHidden"/>
		<input type="hidden" id="orgType" value="" name="orgType"/>
		<input type="hidden" id="submit_action" value="" name="submit_action"/>
		<input type="hidden" value="${acceptProposalUrl}" id="acceptProposalUrl"/>
		<input type="hidden" value="${viewProposalSummaryUrl}" id="viewProposalSummaryUrl"/>
		<input type="hidden" name="procurementId" value="${taskDetailsBean.procurementId}" id="procurementId"/>
		<input type="hidden" name="proposalId" value="${taskDetailsBean.proposalId}" id="proposalId">
		<input type="hidden" name="workflowId" value="${workflowId}"/>
		<input type="hidden" name="taskId" value="${taskId}"/>
		<input type="hidden" id="reassignedToUserName" name="reassignedToUserName" value=""/>
		<input type="hidden" id="proposalTaskStatus" name="proposalTaskStatus" value='${proposalTaskStatus}'/>
		<!--Start || Changes done for enhancement QC : 5688 for Release 3.2.0-->
		<input type="hidden" id="controller_action" name="controller_action"/>
		<input type="hidden" value="${screenReadOnly}" id="screenReadOnly"/>
		<!--End || Changes done for enhancement QC : 5688 for Release 3.2.0-->
		<input type="hidden" id="acceptProposalRenderUrl" value="${acceptProposalRenderUrl}"/>
		<input type="hidden" id="procurementSummaryURL" value="<render:standalonePortletUrl portletUri='/r2/portlet/procurement/procurement.portlet'><render:param name='topLevelFromRequest' value='ProcurementInformation' /><render:param name='midLevelFromRequest' value='ProcurementSummary' /><render:param name='procurementId' value='${taskDetailsBean.procurementId}' /><render:param name='render_action' value='viewProcurement' /><render:param name='hideExitProcurement' value='true' /></render:standalonePortletUrl>"	/>
	</div>
		<h2>
			<label class='floatLft'>Task Details: 
				<label>${taskDetailsBean.taskType} - ${taskDetailsBean.procurementTitle}</label>
			</label>
			<span class="linkReturnVault floatRht"><a href="javascript:returnToAgencyTaskList('${workflowId}');">Return</a></span>
		</h2>
		<c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
		<d:content section="${helpIconProvider}">
			<div id="helpIcon" class="iconQuestion"><a href="javascript:void(0);" class="localTabs" title="Need Help?" onclick="smFinancePageSpecificHelp();"></a></div>
			<input type="hidden" id="screenName" value="Task Details - Accept Proposal" name="screenName"/>
		</d:content>
		<div class="complianceWrapper">
		<div class="failedShow" id="messagediv" style="display:none"></div>
		<c:if test="${message ne null}">
			<div class="${messageType}" id="errordiv" style="display:block">${message} <img
				src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
				class="message-close" onclick="showMe('errordiv', this)">
			</div>
		</c:if>
		<div id="ErrorDiv" class="failed breakAll"> </div>
		<%--Filter and Reassign section starts --%>
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
					<b>Status:</b> <label><c:out value="${proposalTaskStatus}"/></label>
					<input type="button" value="Finish Task" name="finish" id="finishButton" title="Before you can &quot;Finish Task,&quot; you must click the &quot;Save&quot; button below to update the task status and complete" onclick="finishTask()"/>
				</div>
			</div>
		<%--Filter and Reassign section ends --%> 
		<%-- Left Column Start --%>
			<div class="Column1">
				<h4>Procurement Details</h4>
		 		<label>Procurement Title:</label>
				<div style="word-break: break-all"><a href='javascript:viewProcurementSummary();' class='localTabs'>${taskDetailsBean.procurementTitle}</a></div>
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

<!--[Start]R7.12.0 QC9312 add proposal id  -->
		        <label >Proposal ID:</label>
				<div style="word-break: break-all">${taskDetailsBean.proposalId}</div>
<!--[End]R7.12.0 QC9312 add proposal id  -->

		        <label >Proposal Title:</label>
				<div style="word-break: break-all">${taskDetailsBean.proposalTitle}</div>
					<%--code updation for R4 starts--%>
				<label >Proposal Submitted:</label>
				<div style="word-break: break-all">${taskDetailsBean.submittedDate}</div>
					<%--code updation for R4 ends--%>
				<label>Procurement E-PIN:</label>
				<div style="word-break: break-all">${taskDetailsBean.procurementEpin}</div>
				<div></div>
				<h4>Task Details</h4>
				<label>Task Name:</label>
				<div>${taskDetailsBean.taskType}</div>
				<label>Task Instructions:</label>
				<div>Review Proposal documents for responsiveness and mark as Verified or Returned. For Returned, give instructions/deadline in Comments tab.</div>
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
			<div>&nbsp;</div>
			<div class='clear'>&nbsp;</div>
		  	<%-- Contract(s) Section Starts Here --%>
			<div id='contractTabs'>
				<div class="customtabs">
					<ul>
						<li><a href='#ProposalDocuments' class="showButton localTabs">Proposal Documents</a></li>
						<li><a href='#RFPDocuments' class="hideButton localTabs">RFP Documents</a></li>				
						<li><a href='#Comments' class="showButton localTabs">Comments</a></li>
						<li><a href='#ViewTaskHistory' class="hideButton localTabs">View Task History</a></li>
						<li class='liBorderNone'></li>
						<div class='floatRht' id="saveDiv"><input type="button" id="saveButton" class="graybtutton" value="Save" onclick="saveAcceptProposalTaskDetails()"/></div>
					</ul>
				</div>	
				<div id='ProposalDocuments'>
					<div class="tabularWrapper">
						<st:table objectName="proposalDocumentDetailList" cssClass="heading" alternateCss1="evenRows" alternateCss2="oddRows">
							<st:property headingName="Details/Document Name" columnName="documentTitle" align="center" size="20%">
								<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.ProposalDocumentsExtension" />
							</st:property>
							<%--Release 3.6.0 Enhancement 6485, columnName changed to customLabelName from documentType--%> 
							<st:property headingName="Document Type" columnName="customLabelName" align="right" size="20%" />
							<st:property headingName="Required/Optional?" columnName="isRequiredDoc" align="right" size="10%" />
							<st:property headingName="Document Info" columnName="documentId" align="right" size="10%">
								<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.ProposalDocumentsExtension" />
							</st:property>
							<st:property headingName="Current Status" columnName="documentStatus" align="right" size="10%" />
							<st:property headingName="Modified" columnName="modifiedDate" align="right" size="10%" />
							<st:property headingName="Assign Status" columnName="assignStatus" align="right" size="20%">
								<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.ProposalDocumentsExtension" />
							</st:property>
						</st:table>
					</div>				
				</div>
				<div id='RFPDocuments'>
					<div class="tabularWrapper">
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
				<!--Start Added in R5 -->
				<!-- Defect 7273 Change starts -->
				<d:content isReadOnly="${screenReadOnly}">
				<!--End Added in R5 -->
				<div id='Comments'>
					<div class="taskComments taskComments2">
						<h3>Enter any public provider comments:</h3>
						<p class='commentsHeight'>Comments should be concise and are considered public information. Specify which Proposal Details or Proposal Documents your comments refer to. Click the &quot;Save&quot; button above to save your comments.</p>
						<textarea rows="5" class='textarea' id="providerComments" onkeyup="setMaxLengthForEvaluatePropasal(this,1000);" onkeypress="setMaxLengthForEvaluatePropasal(this,1000);" onblur="setMaxLengthForEvaluatePropasal(this,1000);" name="providerComments">${providerComments}</textarea>
					</div>
					<div class="taskComments  commentNoborder taskCommentsRight">
						<h3>Enter any internal Comments:</h3>
						<p class='commentsHeight'>
							Click the &quot;Save&quot; button above to save your comments.Internal comments will only be viewable by the Agency users
						</p>
						<textarea id="internalComments" rows="5" class='textarea' onkeyup="setMaxLength(this,250)" onkeypress="setMaxLength(this,250)" name="internalComments">${internalComments}</textarea>
					</div>
				</div>
				</d:content>
				<!--Start Added in R5 -->
				<!-- Defect 7273 Change ends -->
				<div id='ViewTaskHistory'>
					<div class="tabularWrapper">
						<st:table objectName="taskHistoryList" cssClass="heading" alternateCss1="evenRows" alternateCss2="oddRows">
							<st:property headingName="Task" columnName="taskName" align="center" size="25%" />
							<st:property headingName="Action" columnName="action" align="right" size="20%" />
							<st:property headingName="Detail" columnName="detail" align="right" size="25%" />
							<st:property headingName="User" columnName="user" align="right" size="10%" />
							<st:property headingName="Date/Time" columnName="dateTime" align="right" size="20%" />
						</st:table>	
						<!--End Added in R5 -->
					</div>						
				</div>
			</div>
	</div>
</form>

<div class="overlay"></div>
<%--View Document Properties Overlay --%>
<div class="alert-box alert-box-viewDocumentProperties">
	<div class="content">
		<div class="tabularCustomHead">View Document Information</div>
		<div id="viewDocumentProperties"></div>
	</div>
	<a  href="javascript:void(0);" class="exit-panel upload-exit" onclick="closeOverLayInfo()">&nbsp;</a>
</div>
<%--Mark Non-Responsive Overlay --%>
<div class="alert-box alert-box-markNonResponsive">
     <div class="content">
           <div class="tabularCustomHead">Confirm Action</div> 
           <div class='tabularContainer'>
               <h2>Mark Non-Responsive</h2>
               <div class='hr'></div>
               <p>Are you sure you want to mark the following Proposal as Non-Responsive?</p>
               <p>All Proposals marked Non-Responsive will not be sent to evaluators for review and will be disqualified from 
               competing in this Procurement's competition pool.</p>
               <div class="buttonholder">
                    <input type="button" class="graybtutton" value="Cancel" onclick="cancelOverLay();"/> 
                    <input type="submit" class="redbtutton" id="markNonResponsive" value="Yes, Mark Non-Responsive" onclick="markNonResponsive();"/>
                    <input type="hidden" name="proposalNonResponsiveId" value="${taskDetailsBean.proposalId}"/>
               </div>
           </div>                  
     </div>
     <a  href="javascript:void(0);" class="exit-panel upload-exit">&nbsp;</a>
</div>
<%--Help Overlay --%>
<div class="alert-box-help">
	<div class="tabularCustomHead">Procurement - Help Documents</div>
    <div id="helpPageDiv"></div>
 	<a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
</div>

