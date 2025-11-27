<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<script type="text/javascript" src="../js/enhanceduploadfile.js"></script>
<script>
var locationformAction;
var formAction;
var uploadForm;
function onReadyStep3(){
	formAction = document.uploadform.action;
	uploadForm = document.uploadform;
	var preselectedDiv = '';
	if($js("#leftTree").size()>0){
		preselectedDiv = $js("#leftTree").jstree("get_selected")[0];
	    tree($('#hdnopenTreeAjaxUpload').val()+"&action=enhanceddocumentvault", 'overlaytree','customfolderid', preselectedDiv ,'');
	}
	<%--Commenting below lines for R5: not showing error message of previous screen --%>
	 if("null" != '<%= request.getAttribute("message")%>'){
		$(".messagedivover").html('<%= request.getAttribute("message")%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivoverloc', this)\" />");
		$(".messagedivover").addClass('<%= request.getAttribute("messageType")%>');
		$(".messagedivover").show();
		
	}
}
</script>
<div style="">
<div id="treeStructure" style='width:680px;padding-left:15px;'>
<div>Select the folder location to upload your document</div>
    <link rel="stylesheet" href="../css/documentlist.css" type="text/css"></link>
    <link rel="stylesheet" href="../css/style.css" type="text/css"></link>
	<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/framework/skins/hhsa/css/jstree.style.min.css" type="text/css"></link>
	<portlet:defineObjects/>
	<portlet:resourceURL var="openTreeAjax" id="openTreeAjax" escapeXml="false"></portlet:resourceURL>
	<jsp:useBean id="document" class="com.nyc.hhs.model.Document" scope="request"></jsp:useBean>
	<form action="<portlet:actionURL/>" method="post" name="uploadform" id="uploadform">
		<input type="hidden" value="${portletSessionScope.uploadFormData['returnUrl']}" name="fromUrl" id="fromUrl"/>
		<input type="hidden" value="backInstance" name="backInstance"/>
		<input type="hidden" value="<%=document.getFilePath()%>" name="filepath"/>
		<input type="hidden" value="<%=document.getDocCategory()%>" name="documentCategory"/>
		<input type="hidden" value="<%=document.getDocType()%>" name="documentType"/>
		<input type="hidden" value="<%=document.getSampleCategory()%>" name="sampledoccategory">
		<input type="hidden" value="<%=document.getHelpCategory()%>" name="helpcat">
		<input type="hidden" value="<%=document.getHelpRadioButton()%>" name="help">
		<input type="hidden" value="<%=document.getHelpDocDesc()%>" name="docDesc">
		<input type="hidden" id="message" name ="message"/>
		<input type="hidden" id="messageType" name ="messageType"/>
		<input type="hidden" value="<%=document.getSampleType()%>" name="sampledoctype">
		<input type="hidden" value="documentlocation" name="jspName"/>
		<input type="hidden" id="message" name ="message"/>
		<input type="hidden" id="messageType" name ="messageType"/>
		<input type="hidden" id="next_action" name ="next_action" value=''/>
		<input type="hidden" name="customfolderid" id ="customfolderid" value='' />
		<input type="hidden" name="hdnopenTreeAjaxUpload" id ="hdnopenTreeAjaxUpload" value='${openTreeAjax}' />	
		<div class="messagedivover" id="messagedivoverloc"> </div>	
		<div id='overlaytree' class='leftTreeOverlay formcontainer formcontainernopad'></div>
		<div class='buttonholder buttonholder-align1'>
			<input type="button" value="Cancel" title="Cancel" name="cancel3" id="cancel3"  class="graybtutton"/>
			<input type="button" title="Back" name ="back3" id="back3" value="Back" class="graybtutton"/> 
			<input type="button" value="Upload Document" title="Upload Document" name="uploadDocument" id="uploadDocument" />
			<input type="hidden" name="OldDocumentIdReq" id ="OldDocumentIdReq" value='${OldDocumentIdReq}' />	
		</div> 
	</form>
</div>
</div>
