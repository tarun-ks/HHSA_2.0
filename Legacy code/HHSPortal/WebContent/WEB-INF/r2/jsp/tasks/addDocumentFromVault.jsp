<%@page import="org.apache.taglibs.standard.tag.el.core.ForEachTag"%>
<%@ page errorPage="/error/errorpage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages" />
<script type="text/javascript">
$(document).ready(function() {
		if('null' != '<%=request.getAttribute("message")%>' && '<%=request.getAttribute("messageType")%>' != 'confirmation'){
	//update in release 5 starts
			$(".addDocmessagediv").html('${message}'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('addDocmessagediv', this)\" />");
			$(".addDocmessagediv").addClass('<%= request.getAttribute("messageType")%>');
			$(".addDocmessagediv").show();
			$("#selectdoc").hide();
			<%request.removeAttribute("message");%>
			<%session.removeAttribute("message");%>
		}
		var $tableObj = $('#reRenderId').find('table');
		if(typeof $tableObj != 'undefined'){
			$tableObj.css('width','100%');			
		}
		var $tableObjfolder = $('#folderStructure').find('table');
		if(typeof $tableObjfolder != 'undefined'){
			$tableObjfolder.css('width','100%');			
		}
		//update in release 5 ends
});
</script>
<style>
.paginationWrapper{
	float:right;	
	text-align:right;
	margin-top:-26px
}
</style>
<portlet:defineObjects />
<!-- Updated in Release 6: added returnedPaymentId as parameter -->
<portlet:actionURL var="addRfpDocumentUrl" escapeXml="false">
	<portlet:param name="procurementId" value="${procurementId}" />
	<portlet:param name="procurementDocId" value="${procurementDocId}" />
	<portlet:param name="proposalId" value="${proposalId}" />
	<portlet:param name="returnedPaymentId" value="${returnedPaymentHiddenId}" />
	<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}" />
	<portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}" />
</portlet:actionURL>
<!-- Updated in Release 6: added returnedPaymentId as parameter end-->
<!-- Updated in release 5 Starts -->
<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/css/style.css" type="text/css"></link>
<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/css/addVault.css" type="text/css"></link>
<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/framework/skins/hhsa/css/jstree.style.min.css" type="text/css"></link>
<portlet:actionURL var="getFolderStructure"  id="getFolderStructure" escapeXml="false"></portlet:actionURL>
<!--  Without adding, tree is not coming-->
<script type="text/javascript" src="../framework/skeletons/hhsa/js/jstree.min.js"></script> 
<!-- Updated in release 5 Ends -->
<div class="overlaycontent">
<portlet:resourceURL var="addRfpDocumentResource" id="addFinanceDocumentResource" escapeXml="false">
	<portlet:param name="procurementId" value="${procurementId}" />
	<portlet:param name="returnedPaymentId" value="${returnedPaymentId}" />
</portlet:resourceURL>
<!-- Updated in release 5 Starts -->
<portlet:resourceURL var="openTreeAjax" id="openTreeAjax" escapeXml="false"></portlet:resourceURL>
<!-- Updated in release 5 Ends -->
<form:form action="${addRfpDocumentUrl}" method="post"
	name="selectRFPDocForm" id="selectRFPDocForm"
	commandName="rfpDocuments">
	<!-- Updated in release 5 Starts -->
	<input type="hidden" value="${addRfpDocumentUrl}" name="addRfpDocumentUrl" id="addRfpDocumentUrl"/>
	<!-- Updated in release 5 Ends -->
	<input type="hidden" value="" name="addDocType" id="addDocType"/>
	<input type="hidden" value="" name="docCategory" id="docCategory"/>
	<input type="hidden" value="" name="lastModifiedBy" id="lastModifiedBy"/>
	<input type="hidden" value="" name="lastModifiedDate" id="lastModifiedDate"/>
	<input type="hidden" value="" name="creationDate" id="creationDate"/>
	<input type="hidden" value="" name="submissionBy" id="submissionBy"/>
	<input type="hidden" value="" name="docTitle" id="docTitle"/>
	<input type="hidden" value="" name="documentId" id="documentId"/>
	<!-- Updated in release 5 Starts -->
	<input type="hidden" id="getFolderStructureUrl" value="${getFolderStructure}"/>
	<input type="hidden" id="hdnopenTreeAjaxVar" value="${openTreeAjax}"/>
	<input type="hidden" id="hdnopenFolder" value="${openFolder}"/>
	<input type="hidden" name="customfolderid" id ="customfolderid" value='' />
	<input type="hidden" name="presentFolderId" id ="presentFolderId" value='${selectedfolderid}' />
	<input type="hidden" name="returnedPaymentHiddenId" id ="returnedPaymentHiddenId" value=""/>
	<div class="formcontainer">
	<c:choose>
	<c:when test="${docType ne null}">
	<div id="addVaultHeader">Below are all the documents under the Document Type of <b><i>${docType}</i></b> located in your vault. Select one of the documents and click "Select".</div>
    </c:when>
	<c:otherwise>
	<div id="addVaultHeader">Please select one of the documents below and click "Select".</div>
	</c:otherwise>
	</c:choose>
				<div id="dropDownSelection" class="vaultheader" onchange="dvTreeShow()">
				<img class='menuoptions-icon' src='../framework/skins/hhsa/images/menuoptions-icon.png'/>
				<select id="viewSelection">
				   <option value="View all documents">View all documents</option>
				   <option value="View with folder">View with folders</option></select>
				 </div>
	<!-- Updated in release 5 Ends -->
	<div class="addDocmessagediv" id="addDocmessagediv"></div>
	<div class="tWForLinkDocView" id="docView">
	<div id="checkBoxVal" style="color: red;"></div>
	<input type="hidden" value="${addRfpDocumentResource}" id="addRfpDocumentResourcePopup"/>
	<div id="reRenderId">
		<div>
		<div>&nbsp;</div>
		 <c:choose>
		 <c:when test="${(fn:length(documentList)) eq 0}">
			<div class="failed" id="addDocmessagediv" style="display:block">No documents exist in the Document Vault</div>
			</c:when>
			<c:otherwise>
					<st:table objectName="documentList" cssClass="heading"
						alternateCss1="evenRows" alternateCss2="oddRows"
						pageSize='${allowedObjectCount}'>
						<st:property headingName="Document Name" columnName="documentTitle"
							align="center" size="30%">
							<st:extension
								decoratorClass="com.nyc.hhs.frameworks.grid.AddDocumentFromVaultExtension" />
						</st:property>
						<st:property headingName="Document Type" columnName="documentType"
							align="right" size="30%" />
							<!-- Updated in release 5 Starts -->
							<st:property headingName="Folder"
							columnName="filePath" align="right" size="20%" >
							<st:extension
								decoratorClass="com.nyc.hhs.frameworks.grid.AddDocumentFromVaultExtension" />
						</st:property>
						<!-- Updated in release 5 Ends -->
						<st:property headingName="Modified" columnName="modifiedDate"
							align="right" size="20%" />
					</st:table>
					</c:otherwise>
					</c:choose>
		</div>
	</div>
	<!-- Added in release 5 Starts -->
	</div></div>
	<div class="formcontainer">
<div id="folderMain" style='display:none'>
<div class="addDocmessagedivFolder" id="addDocmessagedivFolder"></div>
<div>&nbsp;</div>
  <div class="leftTreeForLink" id="leftTreeVault"></div>
                <div class="tWForLink" id="folderStructure">
                <input type="hidden" value="${lbDocPresent}" id="isDocPresent" name="isDocPresent"/>
					<st:table objectName="documentList" cssClass="heading"
						alternateCss1="evenRows" alternateCss2="oddRows"
						pageSize='${allowedObjectCount}'>
						<st:property headingName="Document Name" columnName="documentTitle"
							align="right" size="20%">
							<st:extension
								decoratorClass="com.nyc.hhs.frameworks.grid.AddDocumentFromVaultExtension" />
						</st:property>
						<st:property headingName="Document Type" columnName="documentType"
							align="right" size="20%" />
						<st:property headingName="Modified" columnName="modifiedDate"
							align="right" size="10%" />
					</st:table>
					<c:if test="${(fn:length(documentList)) eq 0}">
			<!-- <div class="noRecordCityBudgetDiv noRecord">No documents of this type exist in the Document Vault.</div> -->
			<div class="noRecordCityBudgetDiv-align noRecord">
				<img src='../framework/skins/hhsa/images/file_recyclebin.png' style='width:30px;height:30px;' class='blankDocumentList'/>
				<c:choose>
				<c:when test="${docType ne null}">
					<p>No documents of document type<b><i>${docType}</i></b>were found in this folder.</p>
				</c:when>
				<c:otherwise>
					<p>No documents were found in this folder.</p>
				</c:otherwise>
				</c:choose>
				<br>
				<p>Please select another folder to locate your desired document.</p>
				</div>
			</c:if>
				</div>	
</div>
</div>
<!-- Added in release 5 Ends -->
	<div class="buttonholder" style="margin-top:20px !important"><input type="button"
		class="graybtutton" title="Cancel" value="Cancel" id="cancelAddDoc" />
	<c:if test="${(fn:length(documentList)) > 0}">
		<input type="button" class="button" title="Select" value="Select"
			id="selectdoc" />
	</c:if></div>
</form:form></div>
<Script>
function onReady(){
	// This will execute when Cancel button is clicked during file upload
	$(".alert-box-addDocumentFromVault").find('#cancelAddDoc').unbind("click").click(function() {
		$(".overlay").closeOverlay();
		return false;
	});
	//This method Perform the check that the radio button is clicked
	$(".alert-box-addDocumentFromVault").find('#selectdoc').click(function() { // bind click event to link
		if($(":radio:checked").size() > 0){
			pageGreyOut();
			addSelectedDocument();
		}
	// added else condition in release 5 	
	    else{
	    	$(".addDocmessagediv")
			.html('At least one radio button should be checked'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('addDocmessagediv', this)\" />");
	$(".addDocmessagediv").addClass('failed');
	$(".addDocmessagediv").show();
	    }
	});
}
// Updated in release 5  for pagination
function paging(pageNumber) {
	var url,idToReplace;
	if(($('#leftTreeVault').is(':visible')))
		{
		url = $("#getFolderStructureUrl").val()+"&action=enhanceddocumentvault&render_jsp_name=addDocumentFromVault&selectVault=true&isFilter=false&selectDocForRelease=Financial&nextPage="+ pageNumber;
		idToReplace='folderStructure';
		}
	else
		{
			idToReplace='reRenderId';
			url = $("#addRfpDocumentResourcePopup").val()+"&nextPageParam="+ pageNumber;
		}
	hhsAjaxRender( null, document.selectRFPDocForm,idToReplace,url);
}
//This method will be called when user select any of the radio button on add document screen
function setHiddenParams(docType,docCategory,lastModifiedBy,modifiedDate,createdDate,createdBy,docTitle,docId)
{
	$("#addDocType").val(docType);
	$("#docCategory").val(docCategory);
	$("#lastModifiedBy").val(lastModifiedBy);
	$("#lastModifiedDate").val(modifiedDate);
	$("#creationDate").val(createdDate);
	$("#submissionBy").val(createdBy);
	$("#docTitle").val(docTitle);
	$("#documentId").val(docId);
	}
// This function is used to select the document from the list of the documents
function addSelectedDocument() {
	document.selectRFPDocForm.action=$("#addRfpDocumentUrl").val()+"&submit_action=addFinanceDocumentFromVault&isAjaxCall=true";
	$(document.selectRFPDocForm).ajaxSubmit(options);
		pageGreyOut();
}
var options = 
{
	success: function(responseText, statusText, xhr ) 
	{	
		if(responseText.indexOf("<head><meta http-equiv")!=-1 && responseText.indexOf("<head><meta http-equiv")<300){
    	 	var responseText1=responseText.slice(responseText.indexOf('<head><meta http-equiv'),(responseText.indexOf('</head>')+7));
	   		responseText = responseText.replace(responseText1,"");
    	}
		var responseString = new String(responseText);
		var responsesArr = responseString.split("|");
		if(responsesArr[1] == "Error")
		{
			$( "#formcontainer1" ).show();
		}
		else if(responsesArr[1] == "Exception")
		{
			$(".addDocmessagediv").html(responsesArr[3]+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('addDocmessagediv', this)\" />");
			$(".addDocmessagediv").addClass(responsesArr[4]);
			$(".addDocmessagediv").show();
		}
		else
		{
			var $response=$(responseText);
            var data = $response.contents().find("#documentSection");
            if(data!=null && data!=""){
            $("#documentWrapper").html(data.detach());
        	$(".messagediv").html('Document added successfully'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
			$(".messagediv").addClass('passed');
			$(".messagediv").show();
            }
            $(".overlay").closeOverlay();
		}
		removePageGreyOut();
	},
	error:function (xhr, ajaxOptions, thrownError)
	{   
		showErrorMessagePopup();
		removePageGreyOut();
	}
};
// Added in release 5 for document vault tree structure.
function dvTreeShow() {
	pageGreyOut();
    var selectedVal = $('#viewSelection').val();
    var url;
	var _uploadingDocType=$('#uploadingDocumentTypeAdd').val();

    if (selectedVal == "View all documents") {
       var url =$("#addRfpDocumentResource").val()+"&selectVault=true&selectAll=true";
       var jqxhr = $.ajax({
			url : url,
			type : 'POST',
			cache : false,
			success : function(response) {
				if (response != null || response != '') {
					$('#folderMain').hide();
					$('#docView').show();
					var $response = $(response);
					var data = $response.contents();
					if (data != null || data != '') {
						$("#selectRFPDocForm .tabularWrapper").html(
								data.find(".tabularWrapper").html());
					}
					$("#selectdoc").show();
					
				}
				removePageGreyOut();
			},
			error : function(data, textStatus, errorThrown) {
				showErrorMessagePopup();
				removePageGreyOut();
			}
		});

	} else {
		pageGreyOut();
		var url = $("#getFolderStructureUrl").val()+ "&action=enhanceddocumentvault&render_jsp_name=addDocumentFromVault&selectVault=true&isFilter=false&uploadingDocumentType="+_uploadingDocType+"&docType="+$('#addDocType').val()+"&selectDocForRelease=Financial";
		var jqxhr = $.ajax({
			url : url,
			type : 'POST',
			cache : false,
			success : function(response) {
				$('#docView').hide();
				$('#folderMain').show();
				if (response != null || response != '') {
					var data = $(response);
					if (data != null || data != '') {
						$("#selectRFPDocForm .tWForLink").html(
								data.find(".tWForLink").html());
					$js('#leftTreeVault').jstree("destroy");
					tree($('#hdnopenTreeAjaxVar').val()+"&action=enhanceddocumentvault", 'leftTreeVault','customfolderid', "DocumentVault" ,'');
					}
					if($(".blankDocumentList").size()>0){
						$("#selectdoc").hide();
					}else{
						$("#selectdoc").show();
					}
					if($("#isDocPresent").val() == "false"){
						$(".addDocmessagedivFolder").html('No documents exist in the Document Vault');
						$(".addDocmessagedivFolder").addClass('failed');
						$(".addDocmessagedivFolder").show();
					}
				}
				removePageGreyOut();
			},
			error : function(data, textStatus, errorThrown) {
				showErrorMessagePopup();
				removePageGreyOut();
			}
		});
	}
}
function openFolder(folderId, folderName,divId) {
	pageGreyOut();
	var _uploadingDocType=$('#uploadingDocumentTypeAdd').val();
	document.selectRFPDocForm.action = $("#addRfpDocumentUrl").val()+ "&action=enhanceddocumentvault&submit_action=openFolder&render_jsp_name=addDocumentFromVault&folderName="+folderName
				+ "&folderId=" + folderId+"&isFilter=false&uploadingDocumentType="+_uploadingDocType+"&docType="+$('#addDocType').val()+"&selectDocForRelease=Financial";
    var options = {
		success : function(responseText, statusText, xhr) {
			removePageGreyOut();
			var data = $(responseText);
			$("#selectRFPDocForm .tWForLink").html(
					data.find(".tWForLink").html());
				$js('#leftTreeVault').jstree("select_node", folderId);
				if($(".blankDocumentList").size()>0){
					$("#selectdoc").hide();
				}else{
					$("#selectdoc").show();
				}
				removePageGreyOut();
		}
	};
	$(document.selectRFPDocForm).ajaxSubmit(options);
	return false;
}
</Script>
