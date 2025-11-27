<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.formatCurrency-1.4.0.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/contractConfigAmendment.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jqgridDialogOveride.js"></script> 
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/uploadfileFinancial.js"></script>
<div>
	<div id="treeStructure" style='width: 680px; padding-left: 15px;'>
	<div>Select the folder location to upload your document</div>
    <link rel="stylesheet" href="../css/documentlist.css" type="text/css"></link>
    <link rel="stylesheet" href="../css/style.css" type="text/css"></link>
	<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/framework/skins/hhsa/css/jstree.style.min.css" type="text/css"></link>
	<portlet:defineObjects/>
	<portlet:resourceURL var="openTreeAjax" id="openTreeAjax" escapeXml="false"></portlet:resourceURL>
	<portlet:renderURL var="rfpDocumentRenderUrl" escapeXml="false">
		<portlet:param name="action" value="rfpRelease" />
		<portlet:param name="render_action" value="displayRFPDocumentList" />	
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="proposalId" value="${proposalId}" />
		<portlet:param name="topLevelFromRequest" value="RFPDetails" />
		<portlet:param name="midLevelFromRequest" value="RFPDocuments" />
	</portlet:renderURL>
	<portlet:actionURL var="uploadDocumentUrl"  id='uploadDocumentUrl' escapeXml="false">
		<portlet:param name="submit_action" value="uploadFinancialFile" />
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="hiddendocRefSeqNo" value="${hiddendocRefSeqNo}" />
		<portlet:param name="currentProcurementId" value="${procurementId}" />
		<portlet:param name="uploadingDocumentType" value="${uploadingDocumentType}" />
	</portlet:actionURL>
	<portlet:resourceURL var="cancelUploadDocument" id="cancelUploadDocument" escapeXml="false">
	</portlet:resourceURL>
	<portlet:actionURL var='stepthreeback' id='stepthreeback' escapeXml='false'>
		<portlet:param name="submit_action" value="goBackActionFromStep3" />
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="evaluationPoolMappingId" value="${evaluationPoolMappingId}" />
		<portlet:param name="workflowId" value="${workflowId}" />
	</portlet:actionURL>
	<jsp:useBean id="document" class="com.nyc.hhs.model.Document" scope="request"></jsp:useBean>
	<input type="hidden" name="hdnopenTreeAjaxUpload" id ="hdnopenTreeAjaxUpload" value='${openTreeAjax}' />
	<input type="hidden" name="uploadDocumenturl" id="uploadDocumenturl" value='${uploadDocumentUrl}' /> 
	<input type="hidden" value="${cancelUploadDocument}" id="cancelUploadDocumentUrl"/>
	<form action="#" method="post" id='uploadDocumentLoc' name='uploadDocumentLoc'>
	<input type="hidden" name="customfolderid" id="customfolderid" value='' />
	<input type = 'hidden' value='${stepthreeback}' id='uploadbackStep3'/>
	<div id='overlaytree' class='leftTreeOverlay'></div>
	<div class='buttonholder buttonholder-align1'  id='btnholder'>
		<input type="button" value="Cancel" title="Cancel" name="cancel2" id="cancel2" class="graybtutton" /> 
		<input type="button" title="Back" name="back2" id="back2" value="Back" class="graybtutton" /> 
		<input type="button" value="Upload Document" title="Upload Document" name="uploadDocumentFinancial" id="uploadDocumentFinancial" /> 
		<input type="hidden" name="OldDocumentIdReq" id="OldDocumentIdReq" value='${OldDocumentIdReq}' />
	</div>
	</form>
	</div>
	
</div>
