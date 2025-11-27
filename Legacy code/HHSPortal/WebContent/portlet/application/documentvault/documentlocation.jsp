<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<script type="text/javascript" src="../js/uploadfile.js"></script>
<script>
var locationformAction;
var formAction;
var uploadForm;
function onReady(){

	formAction = document.uploadform.action;
	uploadForm = document.uploadform;
	tree($('#hdnopenTreeAjaxVar').val(), 'overlaytree','customfolderid');
}
</script>

<div id="treeStructure" style='width:860px;padding-left:15px;'>
<div id="error"></div>
	<jsp:useBean id="document" class="com.nyc.hhs.model.Document" scope="request"></jsp:useBean>
	<portlet:defineObjects />
	<form action="<portlet:actionURL/>" method="post" name="uploadform" id="uploadform">
	<input type="hidden" value="<%=document.getFilePath()%>" name="filepath">
	<input type="hidden" value="<%=document.getDocCategory()%>" name="documentCategory">
	<input type="hidden" value="<%=document.getDocType()%>" name="documentType">
	<input type="hidden" value="documentlocation" name="jspName">
	<input type="hidden" id="message" name ="message"/>
	<input type="hidden" id="messageType" name ="messageType"/>
	<input type="hidden" id="next_action" name ="next_action" value=''/>
	<input type="hidden" name="customfolderid" id ="customfolderid" value='' />	
	

<div id='overlaytree' class='leftTreeOverlay formcontainer'></div>
<div class='buttonholder'>
							<input type="button" value="Cancel" title="Cancel" name="cancel3" id="cancel3"  class="graybtutton"/>
		                	<input type="button" title="Back" name ="back3" id="back3" value="Back" class="graybtutton"/> 
		                	<input type="button" value="Upload Document" title="Upload Document" name="uploadDocument" id="uploadDocument" />
		                	<input type="hidden" name="OldDocumentIdReq" id ="OldDocumentIdReq" value='${OldDocumentIdReq}' />	
						</div> 
						</form>
</div>

