<%@page import="java.util.ArrayList"%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page errorPage="/error/errorpage.jsp"%>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<portlet:defineObjects />
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/resources/js/calendar.js"></script>
<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/css/style.css" type="text/css"></link>
<style>
	.alert-box {
	background: #FFF;
	display: none;
	z-index: 1001;
}

.alert-box .sub-started a {
	color: #fff;
}

.alert-box .sub-notstarted {
	background-image: none;
	color: #fff;
}

#newTabs #main-wrapper {
	width: 880px;
	padding: 0;
}

#newTabs .bodycontainer {
	width: 880px;
	background: #fff;
	padding: 0;
}
.overlaycontent{
	padding:0 13px
}

</style>

<script type="text/javascript">
var tempFirstOverLay = "";
var contextPathVariablePath = "<%=request.getContextPath()%>";
$(document).ready(function() {
		if('null' != '<%=request.getAttribute("message")%>' && '<%=request.getAttribute("messageType")%>' != 'confirmation'){
		$(".messagediv").html('${message}'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
		$(".messagediv").addClass('<%= request.getAttribute("messageType")%>');
		$(".messagediv").show();
		<%request.removeAttribute("message");%>
		<%session.removeAttribute("message");%>
	}
	});
</script>	
	<portlet:actionURL var="rfpReleaseDocumentUrl" escapeXml="false">
		<portlet:param name="action" value="rfpRelease" />	
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}" />
		<portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}" />
	</portlet:actionURL>
	<portlet:resourceURL var="uploadRfpDocumentUrl" id="uploadDocument" escapeXml="false">
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="docCategory" value="Financials" />
	</portlet:resourceURL>
	<portlet:resourceURL var="addRfpDocumentResourceUrl" id="addFinanceDocumentResource" escapeXml="false">
		<portlet:param name="procurementId" value="${procurementId}" />
	</portlet:resourceURL>
	<portlet:renderURL var="rfpDocumentRenderUrl" escapeXml="false">
		<portlet:param name="action" value="rfpRelease" />
		<portlet:param name="render_action" value="displayRFPDocumentList" />	
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}" />
		<portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}" />
	</portlet:renderURL>
	<portlet:actionURL var="viewDocumentInfoResourceUrl" escapeXml="false">
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="submit_action" value="viewFinancialDocumentInfo" />
	</portlet:actionURL>
	<portlet:resourceURL var="deleteRfpDocument" id="removeDocumentFromList" escapeXml="false">
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}" />
		<portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}" />
	</portlet:resourceURL>
<div id="documentSection" >
<form action="" method="post" name="documentForm" id="documentForm">
	<input type="hidden" value="${submit_action}" id="submit_action"/>
	<input type="hidden" value="${deleteRfpDocument}" id="deleteRfpDocumentUrl"/>
	<input type="hidden" value="${addRfpDocumentResourceUrl}" id="addRfpDocumentResource"/>
	<input type="hidden" value="${viewDocumentInfoResourceUrl}" id="viewDocumentInfoResource"/>
	<input type="hidden" value="${rfpDocumentRenderUrl}" id="rfpDocumentRender"/>
	<input type="hidden" value="${uploadRfpDocumentUrl}" id="uploadRfpDocumentAction"/>
	<input type="hidden" value="${procurementId}" id="currentProcurementId"/>
	<input type="hidden" value="" id="deleteDocumentId" name="deleteDocumentId"/>
	<input type="hidden" value="" id="deleteDocumentSequence" name="deleteDocumentSequence"/>
	<input type="hidden" value="" id="hdnTableName" name="hdnTableName"/>
	<input type="hidden" id="hiddenDocumentId" value="" name="documentId"/>
	<input type="hidden" id="hiddenDocumentStatus" value="" name="docStatus"/>
	<input type="hidden" id="hiddenAddendumType" value="" name="isAddendumType"/>
	<input type="hidden" value="" id="hdnOrgType" name="hdnOrgType"/>
	<input type="hidden" value="" id="hdnEditable" name="hdnEditable"/>
	<input type="hidden" name="docTypeHidden" id="docTypeHidden" value='' />


		<div id='tabs-container' class='clearHeight'>
				<div class="messagediv" id="messagediv"></div>
		<c:choose>
			<c:when test="${showUploadDocument}">
				<div class="taskButtons floatRht" id="taskButtonsId">
					<c:if test="${org_type eq 'provider_org'}">
					<input type="button" value="Add Document from Vault" title="Add Document from Vault"
					class="addtoVault"  id="addDocument" onclick="addDocumentToVault();"></c:if> <c:choose>
					<c:when test="${detailsBeanForTaskGrid ne null && detailsBeanForTaskGrid.isTaskScreen && !detailsBeanForTaskGrid.isTaskAssigned}">
					
					</c:when>
					<c:otherwise>
					<%-- Code updated for R4 Starts --%>
					<c:if test="${org_type eq 'agency_org'}">
					<input type="button" value="Add Document from Vault" title="Add Document from Vault"
					class="addtoVault"  id="addDocument" onclick="addDocumentToVault();"></c:if>
					<%-- Code updated for R4 Ends --%>
					<input
					type="button" value="Upload New Document" title="Upload New Document"
					class="upload" id="uploadDocument" onclick="uploadDocuments();">
					</c:otherwise>
					</c:choose>
				</div>
			</c:when>
		</c:choose>
		<%-- Grid Starts --%>
		<div class="tabularWrapper">
		<div id="rfpDocreRenderId">
		<d:content isReadOnly="${detailsBeanForTaskGrid ne null && detailsBeanForTaskGrid.isTaskScreen && !detailsBeanForTaskGrid.isTaskAssigned}">
			<!-- Change for Release 6:size of Attachment Date -->
			<st:table objectName="documentList"
				cssClass="heading" alternateCss1="evenRows" alternateCss2="oddRows">
		
								<st:property headingName="Document Name" columnName="documentTitle"
								align="center" size="20%">
								<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.TaskDocumentsExtension" />
								</st:property>
								<st:property headingName="Document Type" columnName="documentType"
								 align="right" size="20%">
								 <%-- Changes made in release 5 Starts--%>
								 <st:extension
									decoratorClass="com.nyc.hhs.frameworks.grid.TaskDocumentsExtension" />
								 </st:property>
								 <%-- Changes made in release 5 Ends--%>
								<st:property headingName="Attached By" columnName="createdBy"
								align="right" size="20%" />
								<st:property headingName="Attachment Date" columnName="createdDate"
								 align="right" size="20%" />
								<st:property headingName="Actions" columnName="actions" align="right"
								size="20%">
								<st:extension
									decoratorClass="com.nyc.hhs.frameworks.grid.TaskDocumentsExtension" />
								</st:property>
			</st:table>
			<!-- Change for Release 6:size of Attachment Date end-->
			</d:content>
			<c:if test="${(fn:length(documentList)) == 0}">
			<div class="noRecordCityBudgetDiv noRecord">
							No documents have been uploaded yet...
			</div>
			</c:if>
			
		</div>	
		</div>
		<%-- Grid Ends --%>
			
		</div>
		
</form>
<div class="overlay"></div>
<div class="alert-box alert-box-upload">
	<div class="content">
		<div id="uniqueTabs"  class='wizardTabs wizardUploadTabs-align'>
			<div class="tabularCustomHead">Upload Document</div> 
			<h2 class='padLft'>Upload Document</h2>
			<div class='hr'></div>
			<ul>
				<li id='step1' class='active'>Step 1: File Selection</li>
				<li id='step2' style="padding-left:25px;">Step 2: Document Information</li>
				<li id='step3' class="last">Step 3: Document Location</li>
			</ul>
	       	<div id="tab1"></div>
	        <div id="tab2"></div>
	        <div id="tabnew"></div>
		</div>
	</div>
	<a  href="javascript:void(0);" class="exit-panel upload-exit" title="Exit">&nbsp;</a>
</div>

<div class="alert-box alert-box-addDocumentFromVault">
		<div class="content">
				<div class="tabularCustomHead">Select Existing Document from Document Vault</div>
				<div id="addDocumentFromVault"></div>
		</div>
		<a  href="javascript:void(0);" class="exit-panel upload-exit" title="Exit">&nbsp;</a>
</div>

<div class="alert-box-delete">
		<div class="content">
		  	<div id="newTabs" class='wizardTabs'>
				<div class="tabularCustomHead">Remove Document
					<a href="javascript:void(0);" class="exit-panel" title="Exit"></a>
				</div>
				<div id="deleteDiv">
				<!-- Fix for Defect 8260 -->
					<div class="pad6 clear promptActionMsg">Are you sure you want to remove this document? This will not delete the document from your vault.</div>
				<!-- Fix for Defect 8260 end-->	
					<div class="buttonholder txtCenter">
						<input type="button" title="No" class="graybtutton exit-panel"  id="deleteNo"  value="No" />
						<input type="button" title="Yes" class="button exit-panel" id="deleteDoc" onclick="removeDocuments();" value="Yes" />
					</div>
				</div>
			</div>
		</div>
		<a  href="javascript:void(0);" class="exit-panel" title="Exit">&nbsp;</a>
</div>
		
<div class="alert-box alert-box-viewDocumentProperties">
		<div class="content">
		
				<div class="tabularCustomHead">View Document Information</div>
				<div id="viewDocumentProperties"></div>
		
		</div>
		<a  href="javascript:void(0);" class="exit-panel upload-exit" title="Exit">&nbsp;</a>
</div>		
	<div id="overlayedJSPContent" style="display:none"></div>
</div>	
	<script>
	function uploadDocuments(){
		pageGreyOut();
		var v_parameter = "&" + $("#documentForm").serialize();
		//Changes made in release 5
		var urlAppender = $("#uploadRfpDocumentAction").val();
		jQuery.ajax({
			type : "POST",
			url : urlAppender,
			data : v_parameter,
			success : function(responseText) {
			   	$("#tab1").empty();
				$("#tab2").empty();
				$("#tabnew").empty();
				if(responseText != null || responseText != ''){
			    	$("#tab1").html(responseText);
				}
				//changes made in release 5 starts
				$(".overlay").launchOverlay($(".alert-box-upload"), $(".exit-panel.upload-exit"), "850px", null, "onReadyForUploadStep");
				$('#uniqueTabs ul').removeClass('ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all ui-state-hover ui-tabs-nav');
				$('#uniqueTabs ul li').removeClass('ui-state-default ui-corner-top ui-state-hover');
				$('#step1').removeClass().addClass('active').css({'padding-left':'15px','margin-left':'0px'});
				$('#step2').removeClass().css({'padding-left':'25px','margin-left':'0px'});
				$('#step3').removeClass().addClass('last').css({'padding-left':'15px','margin-left':'0px'});
				//changes made in release 5 ends
				removePageGreyOut();
				},
			error : function(data, textStatus, errorThrown) {
				showErrorMessagePopup();
				 removePageGreyOut();
			}
		});
		
	}
	function removeDocuments(){
		pageGreyOut();
		var v_parameter = "&" + $("#documentForm").serialize();
		var urlAppender = $("#deleteRfpDocumentUrl").val() + "&deleteDocumentId=" +$("#deleteDocumentId").val() + "&deleteDocumentSequence=" +$("#deleteDocumentSequence").val() + "&hdnTableName=" +$("#hdnTableName").val()+ "&hdnOrgType=" +$("#hdnOrgType").val();
		jQuery.ajax({
			type : "POST",
			url : urlAppender,
			data : v_parameter,
			success : function(responseText) {
				if(responseText != null && responseText.indexOf("documentSection")>=0 ){
			    	$("#documentWrapper").html(responseText);
				} else {
					$(".messagediv").html('This request could not be completed. Please try again in a few minutes.'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
					$(".messagediv").addClass('failed');
					$(".messagediv").show();
				}  
				removePageGreyOut();
				},
			error : function(data, textStatus, errorThrown) {
				showErrorMessagePopup();
				 removePageGreyOut();
			}
		});
		
	}
	//This function is used to view document based upon document id and document name
	function viewRFPDocument(documentId, documentName){
		var jqxhr = $.ajax( {
			url : $("#contextPathSession").val()+"/GetContent.jsp?action=checkDocExits&documentId="+documentId+"&documentName="+documentName+"&documentType=file",
			type : 'POST',
			success : function(data) {
				if(data == 'FilenotFound'){
					var _message= "The selected file has been deleted.";
					$(".messagediv").html(_message+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
			  		$(".messagediv").addClass("failed");
			  		$(".messagediv").show();
					
				}else{
					window.open(contextPathVariablePath+"/GetContent.jsp?action=displayDocument&documentId="+documentId+"&documentName="+documentName);
				}
			},
			error : function(data, textStatus, errorThrown) {
			},
			complete : function() {
			}
		});
	}
	
	//This will be executed when any option is selected from the action drop down
	function actionDropDownChanged(documentId, selectElement, documentName,isAddendumDoc,docStatus,docSeq, tableName,OrgType,docType) {
		var value = selectElement.selectedIndex;
		if (value == 1) {
			viewRFPDocument(documentId, documentName);
			selectElement.selectedIndex = "";
		} else if (value == 2) {
			$("#docTypeHidden").val(docType);
			$("#hiddenDocumentId").val(documentId);
			$("#hiddenDocumentStatus").val(docStatus);
			$("#hiddenAddendumType").val(isAddendumDoc);
			$("#hdnOrgType").val(OrgType);
			var actionLength = selectElement.length;
			if(actionLength>3){
				$("#hdnEditable").val("true");
			}else {
				$("#hdnEditable").val("false");
			}
			$("#documentForm").attr("action", $("#viewDocumentInfoResource").val());
		
			var options = 
		    			{	
						   	success: function(responseText, statusText, xhr ) 
							{
						   		
						   		if(responseText.indexOf("<head><meta http-equiv")!=-1 && responseText.indexOf("<head><meta http-equiv")<300){
						    	 	var responseText1=responseText.slice(responseText.indexOf('<head><meta http-equiv'),(responseText.indexOf('</head>')+7));
							   		responseText = responseText.replace(responseText1,"");
						    	}
								var $response=$(responseText);
		                        var data = $response.contents().find(".overlaycontent");
		                        $("#viewDocumentProperties").html(data.detach());
		                        $("#overlayedJSPContent").html($response);
								$(".overlay").launchOverlay($(".alert-box-viewDocumentProperties"), $(".exit-panel.upload-exit"), "650px", null, "onReadyForViewInfo");
								var a=$('.documentLocationPath').text().trim();
								a=a.replace(/\\/g, "&#x200b;\\&#x200b;");
								b='<div style="width:50ch;" ></div>';
								$('.documentLocationPath').html(b);
								$('.documentLocationPath div').html(a);
								removePageGreyOut();
							},
							error:function (xhr, ajaxOptions, thrownError)
							{                     
								showErrorMessagePopup();
								removePageGreyOut();
							}
						};
			pageGreyOut();
			$("#documentForm").ajaxSubmit(options);
			selectElement.selectedIndex = "";
			return false;
		} else if (value == 3) {
			pageGreyOut();
			$("#deleteDocumentId").val(documentId);
			$("#deleteDocumentSequence").val(docSeq);
			$("#hdnTableName").val(tableName);
			$("#hdnOrgType").val(OrgType);
			$(".overlay").launchOverlay($(".alert-box-delete"), $(".exit-panel"), "350px", null, "onReady");
			removePageGreyOut();
			selectElement.selectedIndex = "";
			return false;
		}
	}

function addDocumentToVault() {
	pageGreyOut();
	//Changes made in release 5
	var url = $("#addRfpDocumentResource").val()+"&selectVault=true&selectAll=true";
	var jqxhr = $
	.ajax({
		url : url,
		type : 'POST',
		cache : false,
		success : function(response) {
			if(response != null || response != ''){
		    	$("#addDocumentFromVault").html(response);
			}
			$(".overlay").launchOverlay($(".alert-box-addDocumentFromVault"), $(".exit-panel.upload-exit"), "750px", "635px", "onReady");
			removePageGreyOut();
		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	});
}
</script>