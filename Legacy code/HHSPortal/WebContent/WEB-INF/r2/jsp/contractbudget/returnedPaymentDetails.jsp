<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">  
<%-- The jsp is added in Release 6 for return payment.
This jsp shows return payment details --%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<portlet:defineObjects />
<link
	href="${pageContext.servletContext.contextPath}/framework/skins/hhsa/css/nycMain.css"
	rel="stylesheet" type="text/css" />
<link
	href="${pageContext.servletContext.contextPath}/framework/skins/hhsa/css/nycMainR2.css"
	rel="stylesheet" type="text/css" />
<link rel="stylesheet" href="../css/documentlist.css" type="text/css"></link>
<link rel="stylesheet" href="../css/style.css" type="text/css"></link>
<script
	src="${pageContext.servletContext.contextPath}/framework/skeletons/hhsa/js/jquery-1.7.2.min.js"
	type="text/javascript"></script>
<script
	src="${pageContext.servletContext.contextPath}/framework/skeletons/hhsa/js/jquery.allJS.min.js"
	type="text/javascript"></script>
<script
	src="${pageContext.servletContext.contextPath}/framework/skeletons/hhsa/js/util.js"
	type="text/javascript"></script>
<script
	src="${pageContext.servletContext.contextPath}/framework/skeletons/hhsa/js/utilR2.js"
	type="text/javascript"></script>
<script
	src="${pageContext.servletContext.contextPath}/framework/skeletons/hhsa/js/jquery-ui-1.8.20.custom.min.js"
	type="text/javascript"></script>


<script type="text/javascript">


/**
 * Ready function
 */
$(document)
.ready(
		function() {
			var tmp = $("#returnedCheckAmount").html();
			$("#returnedCheckAmount").html(jqGridformatCurrency(isNaN(tmp)?'0.00':tmp));
		});
/**
 * This function will perform actions according to the option selected
 */
 function actionDropDownChanged(documentId, selectElement, documentName,isAddendumDoc,docStatus,docSeq, tableName,OrgType,docType) {
		var value = selectElement.selectedIndex;
		selectElement.selectedIndex = 0;
		if (value == 1) {
			viewRFPDocument(documentId, documentName);
			selectElement.selectedIndex = "";
		} else if (value == 2) {
			viewInformation(documentId,OrgType,docType)
		} 
	}
/**
 * This function downloads the document
 */
var contextPathVariablePath = "<%=request.getContextPath()%>";
function viewRFPDocument(docId, docName){
	window.open(contextPathVariablePath
			+ "/GetContent.jsp?action=displayDocument&documentId=" + docId
			+ "&documentName=" + docName);
}
/**
 * This function display file information for a particlar document
 */
function viewInformation(documentId,OrgType,docType){
	$("#docTypeHidden").val(docType);
	$("#hiddenDocumentId").val(documentId);
	$("#hdnOrgType").val(OrgType);
	$("#hdnEditable").val("false");
	$("#returnedPaymentForm").attr("action", $("#viewDocumentInfoResource").val());
	//document.returnedPaymentForm.action = $("#viewDocumentInfoResource").val();
	//Changes for defect 8614
	pageGreyOut();
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
			$(".overlay").launchOverlay($(".alert-box-viewDocumentProperties"), $(".exit-panel.upload-exit"), "650px", null, null);
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
	$(document.returnedPaymentForm).ajaxSubmit(options);
}

/**
 * This function is used for grey background during view information.
 */
function pageGreyOut(){
	$('#greyedBackground').show();
}
</script>

<style>
#greyedBackground {
            position: fixed;
            top: 0;
            right: 0;
            bottom: 0;
            left: 0;
            margin: auto;
            margin-top: 0px;
            width: 100%;
            height: 100%;
            background : none repeat scroll 0 0 #fff;
            z-index: 9999;
			opacity: 0.8;
			filter: alpha(opacity = 80);
			text-align:center;
			vertical-align:middle;
        } 
 #greyedBackground img{
	background-color:#fff;
	top:48%;
	position:relative;
	opacity: 0.8;
 }
 .tabularWrapper table th{
 font-size: 13px;}
 
 .tabularWrapper table td{
  font-size: 13px;}
</style>


<portlet:actionURL var="viewDocumentInfoResourceUrl" escapeXml="false">
	<portlet:param name="submit_action" value="viewFinancialDocumentInfo" />
</portlet:actionURL>
<!-- This jsp is rendered to display returned payment descrpition -->
<div style="display:none;" id="greyedBackground"><img src='../framework/skins/hhsa/images/loadingBlue.gif' /></div>
<div id="borderDiv" style="border: 3px;border-style: solid;width: 900px;border-color: #f2f2f2;margin-bottom: 100px;">
<form action="" id="returnedPaymentForm" name="returnedPaymentForm"
	method="post" style="margin-left: 15px;margin-top: 10px;margin-bottom: 100px;">
	<input type="hidden" id="hiddenDocumentId" value="" name="documentId" />
	<input type="hidden" value="" id="hdnOrgType" name="hdnOrgType" /> <input
		type="hidden" name="docTypeHidden" id="docTypeHidden" value='' /> <input
		type="hidden" value="" id="hdnEditable" name="hdnEditable" /> <input
		type="hidden" value="${viewDocumentInfoResourceUrl}"
		id="viewDocumentInfoResource" />
	<h2>
		<label class='floatLft'>Returned Payment Details</label>
	</h2>
	<%--Css added for Firefox --%>
	<hr style="border-top: dotted 1px;width: 50%;clear: both;" align="left">

	<div class="formcontainer" style="margin-left: -12px;width: 730px;">
		<div class="row">
			<span class="label">Check #:</span> <span class="formfield">
				${returnedBean.checkNumber} </span>
		</div>
		<div class="row">
			<span class="label">Agency Tracking #:</span> <span class="formfield">
				${returnedBean.agencyTrackingNumber} </span>
		</div>
		<div class="row">
			<span class="label">Status:</span> <span class="formfield">
				${returnedBean.checkStatusName} </span>
		</div>
		<div class="row">
			<span class="label">Check Amount:</span> <span class="formfield" id="returnedCheckAmount">${returnedBean.checkAmount}</span>
		</div>
		<div class="row">
			<span class="label">Check Date:</span> <span class="formfield">${returnedBean.checkDate}
			</span>
		</div>
		<div class="row">
			<span class="label">Received Date:</span> <span class="formfield">${returnedBean.receivedDate}</span>
		</div>
		<div class="row">
			<span class="label">Approved Date:</span> <span class="formfield">${returnedBean.approvedDate}</span>
		</div>
		<div class="row">
			<span class="label">Approved By:</span> <span class="formfield">${returnedBean.approvedBy}</span>
		</div>
		<div class="row">
			<span class="label">Description:</span> <span style="word-wrap: break-word;" class="formfield">${returnedBean.description}</span>
		</div>
		<c:if test="${(188 == returnedBean.checkStatus)}">
		<div class="row">
			<span class="label">Cancelled Date:</span> <span style="word-wrap: break-word;" class="formfield">${returnedBean.updatedDate}</span>
		</div>
		<div class="row">
			<span class="label">Cancelled By:</span> <span style="word-wrap: break-word;" class="formfield">${returnedBean.updatedByUser}</span>
		</div>
		</c:if>
	</div>
	<br>
	<c:if test="${(187 == returnedBean.checkStatus) || (188 == returnedBean.checkStatus)}">
	<h2>
		<label class='floatLft'>Documents</label>
	</h2>
	<%--Css added for Firefox --%>
	<hr style="border-top: dotted 1px; width: 50%;clear: both;" align="left">
	<div class='tabularWrapper' style="width: 870px;">
		<st:table objectName="documentList"
				cssClass="heading" alternateCss1="evenRows" alternateCss2="oddRows">
		
								<st:property headingName="Document Name" columnName="documentTitle"
								align="center" size="20%">
								<%-- Changing extension file name for defect 8645 --%>
								<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.ReturnPaymentDetailDocExtension" />
								</st:property>
								<st:property headingName="Document Type" columnName="documentType"
								 align="right" size="20%">
								 <%-- Changes made in release 5 Starts--%>
								 <%-- Changing extension file name for defect 8645 --%>
								 <st:extension
									decoratorClass="com.nyc.hhs.frameworks.grid.ReturnPaymentDetailDocExtension" />
								 </st:property>
								 <%-- Changes made in release 5 Ends--%>
								<st:property headingName="Attached By" columnName="createdBy"
								align="right" size="20%" />
								<st:property headingName="Attachment Date" columnName="createdDate"
								 align="right" size="20%" />
								<st:property headingName="Actions" columnName="actions" align="right"
								size="25%">
								<%-- Changing extension file name for defect 8645 --%>
								<st:extension
									decoratorClass="com.nyc.hhs.frameworks.grid.ReturnPaymentDetailDocExtension" />
								</st:property>
			</st:table>
		<c:if test="${(fn:length(documentList)) == 0}">
			<div class="noRecordCityBudgetDiv noRecord" style="width: 867px;">
				No documents have been uploaded yet...</div>
		</c:if>
		<div style="clear:both;"></div>
	</div>
</c:if>
	<c:if test="${(org_type != 'provider_org')}">
	<h2>
		<label class='floatLft' style="margin-top: 50px;">Comments
			History</label>
	</h2>
	<%--Css added for Firefox --%>
	<hr style="border-top: dotted 1px; width: 50%;clear: both;" align="left">
	<div class='tabularWrapper' style="width:870px;">
		<table cellspacing='0' cellpadding='0' border='1'>
			<tbody>
				<tr style="height: 25px; font-size: 13px;">
					<th>Type</th>
					<th>Detail</th>
					<th>User</th>
					<th>Date/Time</th>
				</tr>
				<c:forEach var="viewHistoryBean" items="${commentsHistoryBean}"
					varStatus="rowCount">
					<c:choose>
						<c:when test="${rowCount.count mod 2 eq 0 }">
							<tr class="oddRows">
						</c:when>
						<c:otherwise>
							<tr class="evenRows">
						</c:otherwise>
					</c:choose>
					<td style="font-size: 13px;">${viewHistoryBean.action}</td>
					<td style="font-size: 13px;">${viewHistoryBean.detail}</td>
					<td style="font-size: 13px;">${viewHistoryBean.user}</td>
					<td style="font-size: 13px;">${viewHistoryBean.dateTime}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		<c:if test="${(fn:length(commentsHistoryBean)) == 0}">
			<div class="noRecordCityBudgetDiv noRecord" style="width: 867px;margin-bottom: 90px;">
				No Records Found...</div>
		</c:if>
		<div style="clear:both;"></div>
	</div>
	</c:if>
</form>
</div>
<!-- Document Information Overlay -->
<div class="alert-box alert-box-viewDocumentProperties">
	<div class="content">

		<div class="tabularCustomHead">View Document Information</div>
		<div id="viewDocumentProperties"></div>

	</div>
	<a href="javascript:void(0);" class="exit-panel upload-exit"
		title="Exit">&nbsp;</a>
</div>
<div id="overlayedJSPContent" style="display: none"></div>
<div class="overlay"></div>
