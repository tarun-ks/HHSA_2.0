<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects/>
<script type="text/javascript" src="../js/uploadfileBapp.js"></script>
<script>


</script>
<div id="treeStructure" style='width:680px;padding-left:15px;'>
	<portlet:actionURL var='uploadDocument' escapeXml='false'>
	</portlet:actionURL>
	<jsp:useBean id="document" class="com.nyc.hhs.model.Document" scope="request"></jsp:useBean>
		<input type="hidden" value="<%=document.getFilePath()%>" name="filepath"/>
		<input type="hidden" value="<%=document.getDocCategory()%>" name="documentCategory"/>
		<input type="hidden" value="<%=document.getDocType()%>" name="documentType"/>
		<input type="hidden" value="documentlocation" name="jspName"/>
		<input type="hidden" id="message" name ="message"/>
		<input type="hidden" id="messageType" name ="messageType"/>
		<input type="hidden" id="next_action" name ="next_action" value=''/>
		<input type="hidden" name="customfolderid" id ="customfolderid" value='' />
		<input type="hidden" name="uploadDocumenturl" id ="uploadDocumenturl" value='${uploadDocument}' />	
		<input type="hidden" name="isAjaxCall" value="true"/>	
		<div id='overlaytree' class='leftTreeOverlay formcontainer'></div>
		<div class='buttonholder' style='margin-right:-40px;'>
			<input type="button" value="Cancel" title="Cancel" name="cancel3" id="cancel3"  class="graybtutton"/>
			<input type="button" title="Back" name ="back3" id="back3" value="Back" class="graybtutton"/> 
			<input type="button" value="Upload Document" title="Upload Document" name="uploadDocument" id="uploadDocument" />
			<input type="hidden" name="OldDocumentIdReq" id ="OldDocumentIdReq" value='${OldDocumentIdReq}' />	
		</div> 
</div>

