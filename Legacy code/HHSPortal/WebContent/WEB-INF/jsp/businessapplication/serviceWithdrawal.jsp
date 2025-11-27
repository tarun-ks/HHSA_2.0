<!-- This page is displayed when manager click on  service withdrawal link on  the service history and comments screen .
Manager can will withdraw the service after adding comments -->
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="java.util.*"%>
<%@page import="com.nyc.hhs.util.DateUtil"%>    
<%@ page errorPage="/error/errorpage.jsp" %>   
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@page import="com.nyc.hhs.frameworks.grid.*,com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<portlet:defineObjects />
<style>
.errorMessages{
	display:none;
}
.individualErrorWithdrawl {
    color: red;
    float: right;
    font-size: 11px;
    width : 400px;
}
</style>
<script type="text/javascript">
function withdraw() {
	document.withdrawalForm.submit();
}
//updated in R5
function setMaxLength(obj,maxlimit){
	if(($(obj).val().length + $(obj).val().split("\n").length-1)  > maxlimit){
		$(obj).val($(obj).val().substring(0, maxlimit - $(obj).val().split("\n").length + 1));
		return false;
	}
}


 $(document).ready(function() {
	 $("a.terms").click(function(){ 
		 openWithdrawlWindow();
		    $(".overlay").launchOverlay($(".alert-box-withdraw-overlay"), $(".exit-panel-withdraw-overlay"));
	    });
 });

 /**
  * This function is used to validate the input before submitting the suspend and conditionally approve request
  * @param obj input object
  * @returns boolean true and false
  */
 function submitRequest(obj){
 	$("#errorMessage").html("");
 	if($.trim($("#comments").val())==''){
 		$("#errorMessage").html("! This field is required");
 		$("#comments").focus();
 		$("#errorMessage").removeClass().addClass("individualError");
 		return false;
 	}else{
 		convertSpecialCharactersHTMLGlobal('comments',true);
 		lsResult =  "^[0-9a-zA-Z !@\\\#\\\$%^\\\&*()-_+=|\\\\{}\\\[\\\];:\\\"\\\'<>,.?\\\/`~\xA7\n\r\t]+$";
 		var re = new RegExp(lsResult);
         if(!re.test($("#comments").val())){
             $("#errorMessage").html("! Your response contains invalid characters. Please re-type your response or visit Online Help for instructions on how to Copy and Paste as plain text.");
             $("#errorMessage").removeClass().addClass("individualErrorWithdrawl");
 			 $("#comments").focus();
 			$(window).resize();
 			return false;
 		}else{
 			$("#errorMessage").removeClass().addClass("individualError");
			$("#reqWithdraw").attr("disabled","disabled");
			document.withdrawalForm.submit();
 		}
 	}
 }
 
function cancel(){
	$(".overlay").closeOverlay();
} 

function openWithdrawlWindow(){
	$("#comments").val("");
	$("#errorMessage").html("");
	$("#errorMessage").removeClass().addClass("individualError");
	document.getElementById("reqWithdraw").disabled=false;
}
</script>
<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.SA_S067_S115_PAGE, request.getSession())
		// Start : R5 Added 
		|| CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S100_SECTION, request.getSession())
		// End : R5 Added
		){%>
	<form id="withdrawalForm" name="withdrawalForm" action="<portlet:actionURL/>" method ="post" >
	<h2>Service History &amp; Comments<label class="linkWithdraw">
		<c:if test="${lbWithdrawalVisibleFlag == 'visible' and (org_type ne 'city_org')}">
		<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.SA_S067_S115_SECTION, request.getSession())){%>
			<a href="javascript:;"  title="Withdraw Application" class="terms">Withdraw Application</a> 
		<%} %>
		</c:if>
		</label>
	</h2>
	
	<div class="hr"></div>
	<%
				if(request.getAttribute("successMsg") != null){
			%>
			<div class="passed" style="display:block"> 
				<b><%=request.getAttribute("successMsg")%></b>
			</div>
			<%
				} else if(request.getAttribute("errorMsg") != null){ 
			%>
			<div class="failed" style="display:block"> 
				<b><%=request.getAttribute("errorMsg")%></b>
			</div>
			<%
				}
				%>
	<p>Use this page to review the service application history and review comments by the HHS Accelerator team.
	<span class="positionOuter floatRht">
  
 	</span>    
	</p>
  
  
<!-- Container Starts -->     
 <c:choose>
<c:when test="${applicationHistoryInfo ne null and ! empty applicationHistoryInfo}">      
	<div class="accordion">
		<div class="clear">
	        <!-- Grid Starts -->
	        <div  class="tabularWrapper clear">
            	<table cellspacing="0" cellpadding="0" class="grid">
	                <tr>
	                    <th>Service Application</th>
	                    <th>Status</th>
	                    <th>Updated By</th>
	                    <th>Date</th>
	                    <th>Comment</th>
                    </tr>
	               
	                		 <c:forEach var="loopItems" items="${applicationHistoryInfo}">
	                		 	<tr>
		                		 <c:choose>
			              	 		<c:when test="${loopItems['EVENT_NAME'] eq 'Provider Comments'}">
			              	 			<td>${loopItems['ENTITY_IDENTIFIER']}</td>
					              	 	<td>&nbsp;</td>
					              	 	<td>${loopItems['USER_ID']}</td>
					              	 	<td>${loopItems['AUDIT_DATE']}</td>
					              	 	<td>${loopItems['DATA']}</td>
			              	 		</c:when>
			              	 		<c:when test="${loopItems['EVENT_NAME'] eq 'Status Changed'}">
			              	 		 	<c:set var="myVar" value='${loopItems["DATA"]}'/>
										<c:set var="search" value="Status Changed to " />
										<c:set var="replace" value="" />
										<c:set var="status" value="${fn:replace(myVar, search, replace)}"/>
			              	 			<td>${loopItems['ENTITY_IDENTIFIER']}</td>
					              	 	<td>${status}</td>
					              	 	<td>${loopItems['USER_ID']}</td>
					              	 	<td>${loopItems['AUDIT_DATE']}</td>
					              	 	<td>N/A</td>
			              	 		</c:when>
			              	 		<c:when test="${loopItems['EVENT_NAME'] eq 'Service Application Conditionally Approved' or loopItems['EVENT_NAME'] eq 'Service Application Suspended'}">
			              	 			<td>${loopItems['ENTITY_IDENTIFIER']}</td>
					              	 	<td>${loopItems['EVENT_NAME']}</td>
					              	 	<td>${loopItems['USER_ID']}</td>
					              	 	<td>${loopItems['AUDIT_DATE']}</td>
					              	 	<td>N/A</td>
			              	 		</c:when>
			              	 		<c:otherwise>
			              	 			<td>${loopItems['ENTITY_IDENTIFIER']}</td>
					              	 	<td>${loopItems['EVENT_NAME']}</td>
					              	 	<td>${loopItems['USER_ID']}</td>
					              	 	<td>${loopItems['AUDIT_DATE']}</td>
					              	 	<td>${loopItems['DATA']}</td>
			              	 		</c:otherwise>
			              	 	</c:choose>
				         		</tr>
			                 </c:forEach>
	            </table>
			</div>
            <!-- Grid Ends -->
		</div>
	   
	</div>
</c:when>
<c:otherwise>
<h2>No Record Found</h2>
</c:otherwise>
</c:choose>
 <!-- Overlay Popup Starts -->
	<div class="overlay"></div>

	<div class="alert-box alert-box-withdraw-overlay">
    	<div class="tabularCustomHead">Services - Withdraw Request<a href="javascript:void(0);" class="exit-panel exit-panel-withdraw-overlay"></a></div>
	    <div class="tabularContainer">
	        <div class="formcontainer">
	          <div class="row"><label class="required">*</label>Please enter any comments associated with this request for withdrawal:</div>
	          <div class="row"> 
	             <textarea name="comments" id="comments" cols="" rows=""  style="width:96%; height:100px" title="Please enter the reasoning for requesting a withdrawal, Accelerator will use this to determine whether or not to approve this request." onkeyup="setMaxLength(this,500)" onkeypress="setMaxLength(this,500)"></textarea>
	             <br>
            	<span id="errorMessage" class="individualError" style='float: left;'></span>
	          </div>           
	            
	          <div class="buttonholder">
	          	<input type="button" class="graybtutton" value="Cancel" title="Cancel" onclick="cancel()"/>
                <input type="hidden" name="next_action" value="servicewithdraw" />
                <input type="hidden" name="subsection" value="servicehistory" />
                <input type="hidden" name="section" value="servicessummary" />
                <input type="hidden" name="business_app_id" value="<%=request.getAttribute("business_app_id")%>"/>
                <input type="hidden" name="service_app_id" value="<%=request.getAttribute("service_app_id")%>"/>
                <input type="hidden" name="elementId" value="<%=request.getAttribute("elementId")%>"/>
                <input type="button" id="reqWithdraw" title="Request Withdrawal" value="Request Withdrawal" onclick="submitRequest(this)"/>
	          </div>
	        </div>
	    </div>
   </div>
   
  <!-- Overlay Popup Ends -->
</form>
 
<% } else {%>
	<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
<%} %>