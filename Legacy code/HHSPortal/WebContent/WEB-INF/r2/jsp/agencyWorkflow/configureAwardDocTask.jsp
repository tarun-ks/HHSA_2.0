<%-- This jsp is used for configuring award documents--%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@page errorPage="/error/errorpage.jsp" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@taglib prefix="render" uri="http://www.bea.com/servers/portal/tags/netuix/render" %>
<style>
	.Column2{
		width:680px !important;
		min-height: 607px !important;
		overflow:hidden !important;
	}
.Column1{
		min-height: 570px;
	}
	.formcontainer .row span.label {
   		 width:38% !important;
	}
	.proposalConfigFormfield {
		width: 46% !important;
	}
	.blockMsg {
	position: absolute !important
}
</style>
<portlet:defineObjects />
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/awardDocumentTask.js"></script>
<!--Start Added in R5 -->
<script type="text/javascript" src="../framework/skeletons/hhsa/js/jstree.min.js"></script> 
<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/framework/skins/hhsa/css/jstree.style.min.css" type="text/css"></link>
<!--End Added in R5 -->
<%-- action URL for Award doc --%>
<script type="text/javascript">

var contextPathVariablePath = "<%=request.getContextPath()%>";
function viewRFPDocument(documentId, documentTitle) {
	var url = contextPathVariablePath
			+ "/GetContent.jsp?action=displayDocument&documentId=" + documentId
			+ "&documentName=" + documentTitle;
	window.open(url);
}
</script>
<portlet:actionURL var="configureAwardDocUrl" escapeXml="false">
</portlet:actionURL>
<%-- action URL for uploading award documents --%>
<portlet:actionURL var="uploadAwardDocUrl" escapeXml="false">
	<portlet:param name="procurementId" value="${taskDetailsBean.procurementId}" />
	<portlet:param name="evaluationPoolMappingId" value="${taskDetailsBean.evaluationPoolMappingId}" />
	<portlet:param name="workflowId" value="${workflowId}" />
	<portlet:param name="submit_action" value="uploadAwardDocument" />
	<portlet:param name="uploadingDocumentType" value="RFP" />
</portlet:actionURL>
<%-- action URL for deleting award documents --%>
<portlet:actionURL var="deleteDocumentUrl" escapeXml="false">
	<portlet:param name="submit_action" value="removeProposalDocumentFromList" />
	<portlet:param name="workflowId" value="${workflowId}" />
</portlet:actionURL>
<%--renderURL for rendering to Agency Task List screen--%>
<portlet:renderURL var="confAwardDocRenderUrl" escapeXml="false"></portlet:renderURL>

<%--resourceURL for rendering overlay on click of Add Document From Vault button--%>
<portlet:resourceURL var="addDocumentFromVaultUrl" id="addDocumentFromVaultUrl" escapeXml="false">
</portlet:resourceURL>
<input type = 'hidden' value='${addDocumentFromVaultUrl}' id='hiddenaddDocumentFromVaultUrl'/>
<%-- Added in R5 --%>
<input type = 'hidden' value='ConfigureAwardScreen' id='uploadingDocumentTypeAdd'/>

<form:form id="configureAwardDocForm" name="configureAwardDocForm" action="${configureAwardDocUrl}" method ="post" commandName="ProposalDetailsBean">
	<input type="hidden" id="documentId" value="" name="documentId"/>
	<input type="hidden" id="submit_action" value="" name="submit_action"/>
	<input type="hidden" value="${configureAwardDocUrl}" id="configureAwardDocUrl"/>
	<input type="hidden" value="${uploadAwardDocUrl}" id="uploadAwardDocumentAction"/>
	<input type="hidden" value="${deleteDocumentUrl}" id="deleteDocument"/>
	<input type="hidden" id="procurementId" name="procurementId" value="${taskDetailsBean.procurementId}"/>
	<input type="hidden" id="evaluationPoolMappingId" name="evaluationPoolMappingId" value="${taskDetailsBean.evaluationPoolMappingId}"/>
	<input type="hidden" name="workflowId" value="${workflowId}" id="workflowId"/>
	<input type="hidden" name="taskStatus" value="${taskDetailsBean.taskStatus}"/>
	<input type="hidden" name="IsNegotiationRequired" value="${taskDetailsBean.isNegotiationRequired}"/>
	<input type="hidden" name="orgType" id="orgType"/>
	<input type="hidden" value="${screenReadOnly}" id="screenReadOnly"/>
	<input type="hidden" id="reassignedToUserName" name="reassignedToUserName" value=""/>
	<input type="hidden" id="confAwardDocRenderUrl" value="${confAwardDocRenderUrl}"/>
	<input type="hidden" id="awardDocumentListSize" value="${(fn:length(awardDocumentList))}" />
	<input type="hidden" id="docSeqID" value="" name="docSeqID"/>
	<input type="hidden" name="docTypeHidden" id="docTypeHidden" value='' />
	<input type="hidden" id="procurementSummaryURL" value="<render:standalonePortletUrl portletUri='/r2/portlet/procurement/procurement.portlet'><render:param name='topLevelFromRequest' value='ProcurementInformation' /><render:param name='midLevelFromRequest' value='ProcurementSummary' /><render:param name='procurementId' value='${taskDetailsBean.procurementId}' /><render:param name='render_action' value='viewProcurement' /><render:param name='hideExitProcurement' value='true' /></render:standalonePortletUrl>"	/>
		<h2>
			<label class='floatLft'>Task Details: 
				<label>${taskDetailsBean.taskType} - ${taskDetailsBean.procurementTitle}</label>
			</label>
			<span class="linkReturnVault floatRht"><a href="javascript:returnToAgencyTaskList('${workflowId}');">Return</a></span>
		</h2>
		<c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
		<d:content section="${helpIconProvider}">
			<div id="helpIcon" class="iconQuestion"><a href="javascript:void(0);" title="Need Help?" onclick="smFinancePageSpecificHelp();"></a></div>
			<input type="hidden" id="screenName" value="Task Details - Configure Award Documents" name="screenName"/>
		</d:content>	
		<div class="complianceWrapper">
			<div class="failedShow" id="messagediv" style="display:none"></div>
			<c:if test="${message ne null} ">
				<div class="${messageType}" id="errordiv" style="display:block">${message} <img
					src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
					class="message-close" title="Close" alt="Close"
					onclick="showMe('errordiv', this)">
				</div>
				<%request.removeAttribute("message");%>
				<%session.removeAttribute("message");%>
			</c:if>
			<div class="passed" id="removeMessageDiv" style="display:none">Document Removed Successfully<img
					src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
					class="message-close" title="Close" alt="Close"
					onclick="showMe('removeMessageDiv', this)">
			</div>
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
						<input type="button" value="Finish Task" name="finish" id="finishButton" title="When you finish this task, notifications will be sent to all Providers who submitted Proposals." onclick="finishTask()"/>
					</div>
				</div>
			<%--Filter and Reassign section ends --%> 
			<%-- Left Column Start --%>
				<div class="Column1" id="procPortlet">
					<h4>Procurement Details</h4>
			 		<label>Procurement Title:</label>
					<div style="word-break: break-all"><a href='javascript:viewProcurementSummary();'>${taskDetailsBean.procurementTitle}</a></div>
					<label>Procurement E-PIN:</label>
					<div style="word-break: break-all">${taskDetailsBean.procurementEpin}</div>
					<c:if test="${(taskDetailsBean.isOpenEndedRfp ne null) and (taskDetailsBean.isOpenEndedRfp eq '1')}">
						<label>Evaluation Group:</label>
						<div style="word-break: break-all">${taskDetailsBean.evaluationGroupTitle}</div>
					</c:if>
			 		<label>Competition Pool:</label>
					<div style="word-break: break-all">${taskDetailsBean.competitionPoolTitle}</div>
					<label>Accelerator Contact 1:</label>
					<div style="word-break: break-all"><a href="mailto:${taskDetailsBean.accPrimaryContactId}">${taskDetailsBean.accPrimaryContact}</a></div>
					<label>Accelerator Contact 2:</label>
					<div style="word-break: break-all"><a href="mailto:${taskDetailsBean.accSecondaryContactId}">${taskDetailsBean.accSecondaryContact}</a></div>
					<div></div>
					<h4>Task Details</h4>
					<label>Task Name:</label>
					<div>${taskDetailsBean.taskType}</div>
					<label>Task Instructions:</label>
					<div>Indicate required and optional award documents. Also, upload checklists/instructions for selected Providers.</div>
					<label>Assigned To:</label>
					<div>${taskDetailsBean.assignedToUserName}</div>
					<label>Date Assigned:</label>
					<div>${taskDetailsBean.assignedDate}</div>
					<label>Last Modified:</label>
					<div>${taskDetailsBean.lastModifiedDate}</div>
				</div>
				<%-- Left Column End --%>
				
				<%-- Center Column Start --%>
				<div class='Column2'>
					<%-- Required Document Configuration Section Start --%>
					<h2 style="padding:0">Required Document Configuration</h2>
	    			<div class="hr"></div>  
					<p>From the drop-downs below please indicate which documents will be required for Providers to upload as part of their award submission. 
						Any fields left blank will not appear.
					</p>
					<div class='formcontainer'>
						<c:forEach var="i" begin="0" end="9">
							<c:set var="disabledValue" value="false"></c:set>
							<c:if test="${ProposalDetailsBean.requiredDocumentList[i].documentType ne null}">
								<c:set var="disabledValue" value="true"></c:set>
								<form:hidden path="requiredDocumentList[${i}].documentType"/>
							</c:if>
		   					<div class='row'>
								<span class='label'>Required Award Document Type ${i+1}:</span>
								<span class='formfield proposalConfigFormfield'>
									<form:select class='proposalConfigDrpdwn reqDocTypeDrpDwn' disabled="${disabledValue}" path="requiredDocumentList[${i}].documentType">
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
							</div>
						</c:forEach>
					</div>
					<%-- Required Document Configuration Section End --%>
					<%-- Optional Document Configuration Section Start --%>
					<h2>Optional Document Configuration</h2>
	    			<div class="hr"></div>   
					<p>From the drop-downs below please indicate which documents will be optional for Providers to upload as part of their award submission. 
					  Any fields left blank will not appear.
					</p>
					<div class='formcontainer'>
						<c:forEach var="i" begin="0" end="4">
							<c:set var="disabledValue" value="false"></c:set>
							<c:if test="${ProposalDetailsBean.optionalDocumentList[i].documentType ne null}">
								<form:hidden path="optionalDocumentList[${i}].documentType"/>
								<c:set var="disabledValue" value="true"></c:set>
							</c:if>
		   					<div class='row'>
								<span class='label'>Optional Award Document Type ${i+1}:</span>
								<span class='formfield proposalConfigFormfield'>
									<form:select class='proposalConfigDrpdwn' disabled="${disabledValue}" path="optionalDocumentList[${i}].documentType">
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
							</div>
						</c:forEach>
					<%-- Optional Document Configuration Section End --%>
				</div>
				<c:if test="${defaultConfigId eq null}">
					<input type="checkbox" id="chkForDefaultConfigurations" name="defaultConfigurationsChecked" 
						title="Check this box to use the attached, required, and optional documents as default options for all competition pools with pending Configure Award Documents Tasks open"/>
					<label for='chkForDefaultConfigurations'>Use the attached, required, and optional documents as defaults for all pending competition pools</label>
				</c:if>
			</div>
			<%-- Center Column End --%>
			<div>&nbsp;</div>
			<div class='clear'>&nbsp;</div>
		  	<div class="customtabs">
				<ul>
					<li class='selected'><a href='#attachDocuments'>Attach Documents</a></li>
				</ul>
			</div>	
			<div id='tabs-container' class='clearHeight'>
				<p>Upload documents for selected Providers to review as notification of their pending award & instructions for next steps.</p>
				<div id='attachDocuments'>
					<div class='taskButtons floatRht'><input type="button" value="Add Document from Vault" title="Add Document from Vault" class="addtoVault"  id="addDocument" onclick="addDocumentToVault('${taskDetailsBean.procurementId}','${workflowId}','${taskDetailsBean.evaluationPoolMappingId}');">
					<input value="Upload New Document" type="button" id="uploadDoc" class='upload' onclick="uploadDocument()"/></div>
					<div class="tabularWrapper" id="attachDocTable">
						<st:table objectName="awardDocumentList" cssClass="heading" alternateCss1="evenRows" alternateCss2="oddRows">
							<st:property headingName="Document Name" columnName="documentTitle" align="center" size="30%">
								<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.AwardDocumentExtention" />
							</st:property>
							<st:property headingName="Modified" columnName="modifiedDate" align="right" size="20%" />
							<st:property headingName="Modified By" columnName="lastModifiedByName" align="right" size="20%" />
							<st:property headingName="Actions" columnName="actions" align="right" size="30%">
								<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.AwardDocumentExtention" />
							</st:property>
						</st:table>
					</div>	
					<c:if test="${(fn:length(awardDocumentList)) <= 0}">
						<div class="noRecordCityBudgetDiv noRecord">
							No documents have been uploaded yet...
						</div>
					</c:if>
				</div>
			 </div>
</form:form>
<div class="overlay"></div>
<div class="alert-box alert-box-viewDocumentProperties">
	<div class="content">
		<div class="tabularCustomHead">View Document Information</div>
		<div id="viewDocumentProperties"></div>
	</div>
	<a  href="javascript:void(0);" class="exit-panel upload-exit">&nbsp;</a>
</div>
<div class="alert-box alert-box-upload">
	<div class="content">
	<!--Start Added in R5 -->
		<div id="newTabs"  class='wizardTabs wizardUploadTabs-align'>
			<div class="tabularCustomHead">Upload Document</div> 
			<h2 class='padLft'>Upload Document</h2>
			<div class='hr'></div>
			<ul>
				<li id='step1' class='active'>Step 1: File Selection</li>
				<li id='step2' class="" style='padding:0 30px;'>Step 2: Document Information</li>
				<li id='step3' class="last">Step 3: Document Location</li>
			</ul>
		</div>
	       	<div id="tab1"></div>
	        <div id="tab2"></div>
			<div id="tabnew"></div>
	</div>
	<!--End Added in R5 -->
	<a  href="javascript:void(0);" class="exit-panel upload-exit">&nbsp;</a>
</div>

<div class="alert-box alert-box-addDocumentFromVault">
		<div class="content">
		<!--Start Added in R5 -->
				<div class="tabularCustomHead">Select Existing Document from Document Vault</div>
				<div id="addDocumentFromVault"></div>
		</div>
		<!--End Added in R5 -->
		<a  href="javascript:void(0);" class="exit-panel upload-exit" title="Exit">&nbsp;</a>
</div>


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
<div id="overlayedJSPContent" style="display:none"></div>
<%--Help Overlay --%>
<div class="alert-box-help">
	<div class="tabularCustomHead">Procurement - Help Documents</div>
    <div id="helpPageDiv"></div>
 	<a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
</div>