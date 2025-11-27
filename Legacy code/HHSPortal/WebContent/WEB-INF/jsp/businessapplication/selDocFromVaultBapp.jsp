<!--This page is displayed when a user select document from vault on the document screen of business and service application-->
<!-- changes in R5 Starts --> 
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ page import="com.nyc.hhs.frameworks.grid.*"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@page import="com.nyc.hhs.frameworks.grid.*,com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<portlet:defineObjects/>
<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/css/style.css" type="text/css"></link>
<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/css/addVault.css" type="text/css"></link>
<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/framework/skins/hhsa/css/jstree.style.min.css" type="text/css"></link>
<script type="text/javascript">
//function called on onload
function onReady(){
	//This method Perfomr the check that the radio button is clicked
	$(".alert-box-link-to-vault").find('#selectdoc').click(function() { // bind click event to link
		$('#selectdoc').attr('disabled', 'disabled');
		pageGreyOut();
		if($(":radio:checked").size() > 0){
			nextActionSelDoc(this.form);
		   	this.form.submit();
		}
	    else{
	    	removePageGreyOut();
	    	$('#selectdoc').removeAttr('disabled');
	    	$("#checkBoxVal").html("At least one radio button should be checked"+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('checkBoxVal', this)\" />");
	    	$("#checkBoxVal").addClass('failed');
			$("#checkBoxVal").show();
	    }
	});
	//Cancel button functionality while selecting doc from vault
	$(".alert-box-link-to-vault").find('#cancellink').click(function() {
		$(".overlay").closeOverlay();
		$('.documentterms').find('option:first').attr('selected', 'selected');
		$('.viewOrRemDoc').find('option:first').attr('selected', 'selected');
	});
	
	//$("#tableContainerDiv table").eq(0).scrollableTable(175);
}
//This will generate the url for next action while submitting the form
function nextActionSelDoc(form){
	document.selDocForm.action=$("#getFolderStructureUrl").val()+"&section="+'<%=renderRequest.getAttribute("section")%>'+"&subsection="+'<%=renderRequest.getAttribute("subsection")%>'+'&next_action=submitDocId'+'&business_app_id='+'<%=renderRequest.getAttribute("business_app_id")%>'+"&elementId="+'<%=renderRequest.getAttribute("elementId")%>'+"&service_app_id="+'<%=renderRequest.getAttribute("service_app_id")%>';
}
//Added for Release 5- dropdown change method
function dvTreeShow() {
    var selectedVal = $('#viewSelection').val();
    var url;
	var _docType=$('#docType').val();
	var v_parameter = "&docType="+(_docType).replaceAll('+', '%2B');
    if (selectedVal == "View all documents") {
    	pageGreyOut();
    	url = $("#getFolderStructureUrl").val()+"&removeNavigator=true&removeMenu=true&next_action=selectDocFromVault&section="+'<%=renderRequest.getAttribute("section")%>'+"&subsection="+'<%=renderRequest.getAttribute("subsection")%>'+'&service_app_id='+'<%=renderRequest.getAttribute("service_app_id")%>'+'&business_app_id='+'<%=renderRequest.getAttribute("business_app_id")%>'+"&selectVault=true&selectAll=true&selectDocForRelease=BusinessApp";
    	var jqxhr = $.ajax({
			url : url,
			type : 'POST',
			cache : false,
			data : v_parameter.toString(),
			success : function(response) {
				if (response != null || response != '') {
					$('#folderMain').hide();
					$('#docView').show();
					var $response = $(response);
					var data = $response.contents();
					if (data != null || data != '') {
						$("#selDocForm .tWForLinkDocView").html(
								data.find(".tWForLinkDocView").html());
						removePageGreyOut();
					}
					$("#selectdoc").show();
				}
			},
			error : function(data, textStatus, errorThrown) {
				showErrorMessagePopup();
				removePageGreyOut();
			}
		});

	} else {
		pageGreyOut();
		var url = $("#getFolderStructureUrl").val()+ "&action=enhanceddocumentvault&render_jsp_name=addDocumentFromVault&selectVault=true&isFilter=false&selectDocForRelease=BusinessApp";
		var jqxhr = $.ajax({
			url : url,
			type : 'POST',
			cache : false,
			data : v_parameter.toString(),
			success : function(response) {
				$('#docView').hide();
				$('#folderMain').show();
				if (response != null || response != '') {
					var data = $(response);
					if (data != null || data != '') {
						$("#selDocForm .tWForLink").html(
								data.find(".tWForLink").html());
					$js('#leftTreeVault').jstree("destroy");
					tree($('#hdnopenTreeAjaxVar').val()+"&action=enhanceddocumentvault", 'leftTreeVault','customfolderid', "DocumentVault" ,'','');
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
					removePageGreyOut();
					}
				}
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
	var _docType=$('#docType').val();
	document.selDocForm.action = $('#hdnopenFolder').val()+ "&action=enhanceddocumentvault&submit_action=openFolder&render_jsp_name=addDocumentFromVault&folderName="+folderName
				+ "&folderId=" + folderId+"&isFilter=false&selectDocForRelease=BusinessApp";
	var options = {
		success : function(responseText, statusText, xhr) {
			removePageGreyOut();
			var data = $(responseText);
			$("#selDocForm .tWForLink").html(
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
	$(document.selDocForm).ajaxSubmit(options);
	return false;
}
//Release 5 end
// This method creates the url for the pagenumber
function paging(pageNumber) {
	// Start Added in R5
	var section =$("#section").val();
	var subsection = $("#subsection").val();
	var business_app_id = $("#business_app_id").val();
	var service_app_id = $("#service_app_id").val();
	var url,idToReplace;
	if(($('#leftTreeVault').is(':visible')))
		{
		url = $("#getFolderStructureUrl").val()+ "&action=enhanceddocumentvault&render_jsp_name=addDocumentFromVault&selectVault=true&isFilter=false&selectDocForRelease=BusinessApp&nextPage="+ pageNumber;
		idToReplace='folderStructure';
		}
	else
		{
			idToReplace='tableContainerDiv';
			url = $("#getFolderStructureUrl").val()+"&next_action=selectDocFromVault&nextPageParam="+ pageNumber;
			/* +"&section="+$("#section").val()+"&subsection="+$("#subsection").val()+"&next_action=selectDocFromVault&business_app_id="+$("#business_app_id").val()
			+"&elementId="$("#elementId").val()+"&service_app_id="$("#service_app_id").val()+"&nextPageParam="+ pageNumber; */
		}
	// End Added in R5
	hhsAjaxRender(null, document.selDocForm, idToReplace, url);
}
</script>

<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.BA_S043_PAGE, request.getSession())){%>
<div class="overlaycontent">
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
			<option value="View with folder">View with folders</option>
	</select>
</div>
<div id ="checkBoxVal" style="color:red;"></div>
<div class='hr'></div>
<form name="selDocForm" id="selDocForm" action="<portlet:actionURL/>" method ="post" >
		<div class='tWForLinkDocView' id="docView">
			<div id="tableContainerDiv">
			<c:choose>
				<c:when test="${selDocFromVaultItemList eq null or fn:length(selDocFromVaultItemList) eq 0}">
				<c:choose>
				<c:when test="${docType ne null}">
					<div class="failed" id="addDocmessagediv" style="display:block">No documents of document type <b><i>${docType}</i></b> exist in the Document Vault
					</div>
				</c:when>
				<c:otherwise>
					<div class="failed" id="addDocmessagediv" style="display:block">No documents exist in the Document Vault
					</div>
				</c:otherwise>
				</c:choose>
				</c:when>
				<c:otherwise>
						<st:table objectName="selDocFromVaultItemList" cssClass="heading"
							alternateCss1="evenRows" alternateCss2="oddRows" pageSize='${allowedObjectCount}'>
							<st:property headingName="Document Name"
								columnName="msDocumentName" align="center" size="30%">
								<st:extension
									decoratorClass="com.nyc.hhs.frameworks.grid.DocumentNameFromVaultExtension" />
							</st:property>
							<st:property headingName="Document Type"
								columnName="msDocumnetType" align="right" size="30%" />
							<st:property headingName="Folder" columnName="filePath"
								align="right" size="30%">
								<st:extension
									decoratorClass="com.nyc.hhs.frameworks.grid.DocumentNameFromVaultExtension" />
							</st:property>

							<st:property headingName="Modified"
								columnName="msLastModifiedDate" align="right" size="30%" />
						</st:table>
				</c:otherwise>
			</c:choose>
			</div>
		</div>
		<!-- Added for Release 5- selectVault for folder structure -->
	<portlet:actionURL var="getFolderStructure"  id="getFolderStructure" escapeXml="false"></portlet:actionURL>
	<portlet:resourceURL var="openTreeAjax" id="openTreeAjax" escapeXml="false"></portlet:resourceURL>
	<portlet:actionURL var="openFolder"  id="openFolder" escapeXml="false"></portlet:actionURL>
	<input type="hidden" id="hdnopenTreeAjaxVar" value="${openTreeAjax}"/>
	<input type="hidden" id="getFolderStructureUrl" value="${getFolderStructure}"/>
	<input type="hidden" name="customfolderid" id ="customfolderid" value='' />
	<input type="hidden" name="presentFolderId" id ="presentFolderId" value='${selectedfolderid}' />
	<input type="hidden" id="hdnopenFolder" value="${openFolder}"/>
    <input type="hidden" id="docType" value="${docType}" name="docType"/>
    <input type="hidden" id="entityId" value="${entityId}" name="entityId"/>
    <input type="hidden" id="docCategory" value="${docCategory}" name="docCategory"/>
    <input type="hidden" id="serviceAppID" value="${serviceAppID}" name="serviceAppID"/>
    <input type="hidden" id="sectionId" value="${sectionId}" name="sectionId"/>
    <input type="hidden" id="section" value="${section}" name="section"/>
    <input type="hidden" id="subsection" value="${subsection}" name="subsection"/>
    <input type="hidden" id="service_app_id" value="${service_app_id}" name="service_app_id"/>
    <input type="hidden" id="business_app_id" value="${business_app_id}" name="business_app_id"/>
    <input type="hidden" id="elementId" value="${elementId}" name="elementId"/>
    <div id="folderMain" style='display:none'>
    <div class="addDocmessagedivFolder" id="addDocmessagedivFolder"></div>
    <div>&nbsp;</div>
				 <div class="leftTreeForLink" id="leftTreeVault"></div>
				<div class="tWForLink" id="folderStructure">
				<input type="hidden" value="${lbDocPresent}" id="isDocPresent" name="isDocPresent"/>
					<st:table objectName="documentList" cssClass="heading"
						alternateCss1="evenRows" alternateCss2="oddRows" pageSize='${allowedObjectCount}'>
						<st:property headingName="Document Name" columnName="msDocumentName"
							align="right" size="20%">
							<st:extension
								decoratorClass="com.nyc.hhs.frameworks.grid.DocumentNameFromVaultExtension" />
						</st:property>
						<st:property headingName="Document Type" columnName="msDocumnetType"
							align="right" size="20%" />
						<st:property headingName="Modified" columnName="msLastModifiedDate"
							align="right" size="10%" />
					</st:table>
						<c:if test="${fn:length(documentList) eq 0}">
				<div class="noRecordCityBudgetDiv-align noRecord">
				<img src='../framework/skins/hhsa/images/file_recyclebin.png' style='width:30px;height:30px;' class='blankDocumentList'/>
				<c:choose>
				<c:when test="${DOC_TYPE ne null}">
				<p>No documents of document type <b><i>${DOC_TYPE}</i></b> were found in this folder.</p>
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
<!-- End Release 5- selectVault for folder structure -->   
<div class="buttonholder">
		<input type="button" class="graybtutton" title="Cancel" value="Cancel" id="cancellink" />
		<c:choose>
		<c:when test="${fn:length(selDocFromVaultItemList) > 0 or fn:length(documentList) > 0 }">
		<input type="button" class="button" title="Select" value="Select" id="selectdoc">
		</c:when>
		</c:choose>
	</div>
</form>
</div>
<% } else {%>
<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
<%} %>
<!-- changes in R5 ends --> 