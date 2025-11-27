<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@page import="com.nyc.hhs.constants.HHSConstants"%>
<%@page import="java.util.Calendar"%>
<%@page import="com.nyc.hhs.model.PaymentBean"%>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@page import="com.nyc.hhs.controllers.util.PaymentUtilities"%>
<%@ taglib prefix="fmtMessages" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@ page errorPage="/error/errorpage.jsp"%>
<%-- Added in R5 start--%>
<%@include file="/WEB-INF/r5/jsp/assigneeListMapping.jsp" %>
<portlet:resourceURL var="setDefaultUser" id="setDefaultUser"></portlet:resourceURL>
<input type="hidden" id="setDefaultUser" value="${setDefaultUser}"/>
<%-- Added in R5 ends--%>
<%-- An Advance Payment Review task is generated after the Advance Payment Request
 Task has been approved. The first reviewer will be able to assign dollar amounts 
 to Commodity and Accounting lines. Subsequent users will be able to review the
  payment and approve the task to the next level of review. Similar to the Payment 
  Review Task, a Payment may have multiple payment vouchers if all or a portion of 
  the advance amount was allocated to an Assignee. Users will be able to switch the 
  detailed view between the available payment vouchers by selecting the values in 
  S402.08  <Voucher No> dropdown. WF308  Advance Payment Review documents the
   triggers for S402  Advance Payment Review Task and the post-approval payment process. --%>
   
   <%-- setting show CB grid bean check --%>
<portlet:resourceURL var='showCBGridTabs' id='showCBGridTabs'
	escapeXml='false'>
</portlet:resourceURL>

<%-- setting refresh payment details check --%>
<portlet:resourceURL var="refreshPaymentDetailUrl" id="refreshPaymentDetail" escapeXml="false">
</portlet:resourceURL>

<input type="hidden" name="hdnRefreshPayment" id="hdnRefreshPayment" value="${refreshPaymentDetailUrl}" />

<%-- setting call back  check --%>
<portlet:resourceURL var='getCallBackContractBudgetData' id='getCallBackContractBudgetData' escapeXml='false'>
</portlet:resourceURL>
<input type = 'hidden' value='${getCallBackContractBudgetData}' id='getCallBackContractBudgetData'/>
<input type="hidden" name="submitContractBudgetOverlay" id="submitContractBudgetOverlay" value="${submitContractBudgetOverlay}"/>

<fmtMessages:setBundle basename="com/nyc/hhs/properties/messages"/>
<input type='hidden' value='${showCBGridTabs}' id='hiddenCBGridTagURL' />
<c:set var="subAmounttotal" value="0"></c:set>

<c:if test="${accessScreenEnable eq false}">
	<div class="failed" id="screenlockdiv" style="display:block">This screen is locked by ${lockedByUser}</div>
</c:if>

<%-- Included of Jquery CSS and JS files  --%>
<link rel="stylesheet" media="screen" href="${pageContext.servletContext.contextPath}/r2/css/ui.jqgrid.css" type="text/css"></link>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/grid.locale-en.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.formatCurrency-1.4.0.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/accordianFunctions.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/contractBudget.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jqgridDialogOveride.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/unsaveData.js"></script>
<script type="text/javascript" src="../resources/js/autoNumeric-1.7.5.js"></script>
<%-- Added in R5 start--%>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/defaultAssignee.js"></script>
<%-- Added in R5 ends--%>
<%-- [Start] 9.4.0 qc 9656 Invoice Review task may create same payment more than once due to multi-tab --%>

<%
		String pageName="";
		if(request.getRequestURI()!=null && request.getRequestURI().trim().length()>0){
			pageName = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/")+1);
			if(pageName!=null && pageName.trim().length()>0)
				request.setAttribute("multipletab_taskName", pageName);
		}		
%>
<jsp:include page="/WEB-INF/jsp/multipletab.jsp">
</jsp:include>
<%-- [End] 9.4.0 qc 9656 Invoice Review task may create same payment more than once due to multi-tab --%>
<style type="text/css">
/* Fixed for Contract information as per updated wireframes......page specific fix, DO NOT INCLUDE IN EXTERNAL CSS */
.paymentFormWrapper span.label{
	width:46% !important
}
.paymentFormWrapper span.formfield{
	width: 50% !important
}
</style>



<Script>
$(document)
.ready(
		function() {
			document.getElementById("paymentVoucherId").selectedIndex=0;			
		});
//This method is called to refresh the payment details
function refreshPaymentDetails(obj) {
	pageGreyOut();
	var paymentId = $("#paymentVoucherId").val();
	var reviewSelectId = document.getElementById("paymentVoucherId");
	var reviewProcessId = reviewSelectId.options[reviewSelectId.options.selectedIndex].value;
	var reviewProcessText = reviewSelectId.options[reviewSelectId.options.selectedIndex].text;
	var otherDetails = $("#paymentVoucherId option:selected").attr("id");
	var v_parameter = "paymentId=" + paymentId;
	var urlAppender = $("#hdnRefreshPayment").val();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(result) {
	        if(result.indexOf("paymentLineItemId")!=-1){
	        	$("#paymentLineAndChart").html(result);
	        	var paymentInfoArray = otherDetails.split("-");
	        	
				  if(trim(paymentInfoArray[1])==""){
					  $("#payeeNameId").html("--");
				  }else{
					  $("#payeeNameId").html(paymentInfoArray[1]);
				  }
				  
				  if(trim(paymentInfoArray[2])==""){
					  $("#payeeVendorCodeId").html("--");
				  }else{
					  $("#payeeVendorCodeId").html(paymentInfoArray[2]);
				  }
				  
				  if(trim(paymentInfoArray[3])==""){
					  $("#payeeAddrCodeId").html("--");
				  }else{
					  $("#payeeAddrCodeId").html(paymentInfoArray[3]);
				  }

	        }else {
	        	$("#taskErrorDiv").html(result);
				$("#taskErrorDiv").show();
	        }
	    	removePageGreyOut();
		}
	});
}

//This method is called for client side finish task validation
function finishTaskValidation(){
		var returnVal = true;
		var internalCommentVal = "";
		if(document.getElementById("internalCommentArea")!=null){
			internalCommentVal=trim(document.getElementById("internalCommentArea").value);
		}
		var taskStatus = $("#finishtaskchild").val();
	 if(taskLevel>0 && internalCommentVal=="" && taskStatus=="Returned for Revision"){
			$("#taskErrorDiv").html(internalAgencyCommentErrorMsg);
			$("#taskErrorDiv").show();
			returnVal = false;
		}
		return returnVal;
	}
	
	// this function trim the string
function trim(stringToTrim) {
	return stringToTrim.replace(/^\s+|\s+$/g,"");
}
</Script>
<c:set var="readOnlyPeriod" value="true"></c:set>
<c:if test="${detailsBeanForTaskGrid ne null && detailsBeanForTaskGrid.level eq '1' && detailsBeanForTaskGrid.isTaskAssigned}">
<c:set var="readOnlyPeriod" value="false"></c:set>
</c:if>
<c:set var="sectionName"><%=HHSComponentMappingConstant.AGENCY_S399_PAGE%></c:set>
<div class='complianceWrapper'>	
<d:content  section="${sectionName}" authorize="" >
<task:taskContent workFlowId="" taskType="taskAdvancePaymentReview" isTaskScreen="" level="header" taskDetail="" ></task:taskContent>
<d:content isReadOnly="false">

 <c:set var="periodDisable" value="true"></c:set>
 <c:if
	test="${detailsBeanForTaskGrid.isTaskAssigned && detailsBeanForTaskGrid.level eq '1'  }">
	<c:set var="periodDisable" value="false"></c:set>
</c:if>
	<div class='hr'></div>
	<% String lsErrorMessage = HHSConstants.EMPTY_STRING;
		if(null != request.getAttribute(HHSConstants.ERROR_MESSAGE)){
			lsErrorMessage = (String) request.getAttribute(HHSConstants.ERROR_MESSAGE);
		%>
			<div class="failed breakAll" style="display:block" id="error"><%=lsErrorMessage%></div>			
	<%} %>	
	<div class="failed" id="errorGlobalMsg"></div>
	<div class="passed" id="successGlobalMsg"></div>
	
	<div class='clear'></div>
	
	<form:form action="" method="post" name="contractBudgetForm"
	id="contractBudgetForm">
	
	<h2>Payment Voucher</h2>
	<div class='hr'></div>
	
	<h6>Header Details</h6>
	<c:if test="${fn:length(paymentVoucherList) gt 1}">
	<p>NOTE: This advance has multiple payments </p>
	</c:if>
	
	<div class="formcontainer paymentFormWrapper">
		<h3>General Information</h3>
		<div class="row"><span class="label">Agency Code:</span> <span
			class="formfield">${paymentHeaderDetail.agencyCode}</span></div>
			
		<div class="row"><span class="label">Payment Voucher No.:</span> <span
			class="formfield">
			<select id="paymentVoucherId" name="paymentVoucherId"
				onchange="refreshPaymentDetails(this)">
				<c:forEach var="listItems" 
					items="${paymentVoucherList}" varStatus="counter">
					<option value="${listItems.paymentId}" 
						id="${listItems.period}-${listItems.payeeName}-${listItems.payeeVendorCode}-${listItems.payeeVendorAddrCode}"
						>${listItems.paymentVoucherNo}</option>
				</c:forEach>
			</select> 
				
			</span></div>
			
			
		<div class="row"><span class="label">Budget FY:</span> <span
			class="formfield">${paymentHeaderDetail.budgetFYId}</span></div>
		<div class="row"><span class="label">Fiscal Year:</span> <span
			class="formfield">${paymentHeaderDetail.fiscalYearId}</span></div>
		
		<div class="row"><span  class="label">Period:</span> 			
			<%
			PaymentBean loPaymentHeaderDetail = (PaymentBean)request.getAttribute(HHSConstants.PAYMENT_HEADER_DETAIL);
			String lsPeriodDropDownList = (String)PaymentUtilities.getPeriodDropDownList(loPaymentHeaderDetail);
			%>
			
			<select id="periodId" name="periodId" 	<c:if test="${readOnlyPeriod}"> disabled='${readOnlyPeriod}' </c:if>
				class=''>
			<c:choose>
			<c:when test="${readOnlyPeriod}">
			<option value="${paymentHeaderDetail.period}" selected>${paymentHeaderDetail.period}</option>
			</c:when>
			<c:otherwise>
			<%=lsPeriodDropDownList %>
			</c:otherwise>
			</c:choose>	
			</select>  
			
		</div>
	</div>
	

	<div class="formcontainer paymentFormWrapper">
		<h3>Advance Information</h3>
		<div class="row"><span class="label">Advance Description:</span> <span
			class="formfield">${paymentHeaderDetail.advanceDescription}</span></div>
		<div class="row"><span class="label">Advance Number:</span> <span
			class="formfield">${paymentHeaderDetail.budgetAdvanceNumber}</span></div>
		<div class="row"><span class="label">Request Submission Date:</span> <span
			class="formfield">${paymentHeaderDetail.advanceRequestDate}</span></div>
		<div class="row"><span class="label">Request Approval Date:</span> <span
			class="formfield">${paymentHeaderDetail.advanceApprovedDate}</span></div>
	</div>
	
	<div class='clear'>&nbsp;</div>
	
	<div class="formcontainer paymentFormWrapper">
		<h3>Vendor Information</h3>
		<div class="row">
			<span class="label">Vendor Code:</span> 
			<span class="formfield">${paymentHeaderDetail.vendorCode}</span>
		</div>
		<div class="row">
			<span class="label">Vendor Address Code:</span> 
			<span class="formfield">${paymentHeaderDetail.vendorAddrCode}</span>
		</div>
		<div class="row">
			<span  class="label">Payee Name:</span> 
			<span id="payeeNameId" class="formfield">
				<c:choose>
				    <c:when test="${paymentHeaderDetail.payeeName ne null}">
				       ${paymentHeaderDetail.payeeName}
				    </c:when>
				    <c:otherwise>
				        --
				    </c:otherwise>
				</c:choose>
			</span>
		</div>
		<div class="row">
			<span class="label">Payee Vendor Code:</span> 
			<span id="payeeVendorCodeId" class="formfield">			
				<c:choose>
				    <c:when test="${paymentHeaderDetail.payeeVendorCode ne null}">
				       ${paymentHeaderDetail.payeeVendorCode}
				    </c:when>
				    <c:otherwise>
				        --
				    </c:otherwise>
				</c:choose>
			</span>
			</div>
		<div class="row">
			<span class="label">Payee Vendor Address Code:</span> 
			<span id="payeeAddrCodeId" class="formfield">
				<c:choose>
				    <c:when test="${paymentHeaderDetail.payeeVendorAddrCode ne null}">
				       ${paymentHeaderDetail.payeeVendorAddrCode}
				    </c:when>
				    <c:otherwise>
				        --
				    </c:otherwise>
				</c:choose>
			</span>
		</div>
	</div>
	


	
	<p>&nbsp;</p>

	<%-- Payment Voucher Line Details --%>
	<div id="paymentLineAndChart">
		<jsp:include page="/WEB-INF/r2/jsp/payment/advancePaymentLineAndChartDetails.jsp" />
	</div>



</form:form>
</d:content>
<p class='clear'>&nbsp;</p>
<div class='gridFormField'>
	<task:taskContent workFlowId=""  taskType="taskAdvancePaymentReview"  isTaskScreen=""  level="footer"></task:taskContent>
	</div>
</d:content>
</div>
<%--  Overlay popup starts --%>
<div class="overlay"></div>
<div class="alert-box-help">
				<div class="tabularCustomHead toplevelheaderHelp"></div>
	            <div id="helpPageDiv"></div>
		  		<a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
</div>

