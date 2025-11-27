<%@page import="java.util.ArrayList"%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="com.nyc.hhs.frameworks.grid.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<portlet:defineObjects/>

<!-- Body Wrapper Start -->
<form id="updatenycnameandemailform" name="updatenycnameandemailform" action="<portlet:actionURL/>&next_action=updatenycnameandemail" method ="post" >
	<%
	String lsTransactionMsg = "";
	if (null!=request.getAttribute("transactionMessage")){
		lsTransactionMsg = (String)request.getAttribute("transactionMessage");
	}
	if(null!=request.getAttribute("transactionStatus") && "passed".equalsIgnoreCase((String)request.getAttribute("transactionStatus"))){%>
		<div id="transactionStatusDiv" class="passed" style="display:block" ><%=lsTransactionMsg%> </div>
	<%}else if(( null != request.getAttribute("transactionStatus") ) && "failed".equalsIgnoreCase((String)request.getAttribute("transactionStatus"))){%>
	     <div id="transactionStatusDiv" class="failed" style="display:block" ><%=lsTransactionMsg%> </div>
	<%}%>
	<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S027_PAGE_1, request.getSession())) {%>
	
		<H2>Update NYC.ID Name and Email Address</H2>
		<p>Use this page to change the name or email address for your HHS Accelerator account. Please note that changing the information
		 below will change your login and account information for HHS Accelerator and other NYC applications that use NYC.ID.
		</p>
		<DIV class=hr></DIV>
		<a style="visibility:hidden;display:none" id="homeHref" href="<%=request.getContextPath()%>/portal/hhsweb.portal?_nfpb=true&_nfls=false&_pageLabel=portlet_hhsweb_portal_page_provider_home"></a>  
		<DIV class=formcontainer>
			<c:if test="${error_to_display eq 'true'}">
				<div class="failed" style="display:block">
					<b> !! Failed to update user details</b>
				</div>
			</c:if>
			<P class=note><SPAN class=required>*</SPAN>Indicates required fields</P>
			<DIV class=clear></DIV>
			<!-- Form Data Starts -->
			<DIV class=row>
				<SPAN class=label><SPAN class=required>*</SPAN>First Name:</SPAN> <SPAN class=formfield><INPUT class=input type=text maxlength="32" name="fName" id="fName" value="${userDataMap['FIRST_NAME']}"></SPAN> 
				<span class="error"></span>
			</DIV>
			<DIV class=row><SPAN class=label>Middle Initial:</SPAN> <SPAN class=formfield>
				<INPUT style="width:5%" class=input type=text maxlength="1" name="mName" id="mName" value="${userDataMap['MIDDLE_INITIAL']}"></SPAN><span class="error"></span>
			</DIV>
			<DIV class=row><SPAN class=label><SPAN class=required>*</SPAN>Last Name:</SPAN> <SPAN class=formfield><INPUT class=input type=text maxlength="64" name="lName" id="lName" value="${userDataMap['LAST_NAME']}"></SPAN>
				<span class="error"></span>
			</DIV>
			<DIV class=row> </DIV>
			<DIV class=row><SPAN class=label><SPAN class=required>*</SPAN>Email Address:</SPAN> <SPAN class=formfield><INPUT class=input type=text name="emailAdd" maxlength="128" id="emailAdd" value="${userDataMap['EMAIL']}"></SPAN>
				<span class="error"></span>
			</DIV>
			<DIV class=row><SPAN class=label><SPAN class=required>*</SPAN>Confirm Email Address:</SPAN> <SPAN class=formfield><INPUT class=input type=text name="confirmEmailAdd" maxlength="128" id="confirmEmailAdd" value="${userDataMap['EMAIL']}"></SPAN><span class="error"></span> </DIV>
			<DIV class=buttonholder><INPUT class=graybtutton value=Cancel title="Cancel" type=button onclick="goToHome();"><INPUT class=button value="Save" title="Save" type="submit"></DIV>
			<!-- Form Data Ends -->
		</DIV>
		<!-- Body Container Ends -->
	 
	<%}else{ %>
		<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
	<%} %>
</form>	

<script type="text/javascript">

//Below function bring user to provider home page
function goToHome(){
	var url= "<%=request.getContextPath()%>/portal/hhsweb.portal?_nfpb=true&_nfls=false&_pageLabel=portlet_hhsweb_portal_page_provider_home";
	location.href=url;
	//document.getElementById("homeHref").click();
}

//jquery ready function- executes the script after page loading
$(document).ready( function() {
	$('#fName').alphanumeric( { allow: "-,.' \\\"" , nchars:"_0123456789"});
   	$('#mName').alphanumeric( { allow: "-,.' \\\"" , nchars:"_0123456789"});
   	$('#lName').alphanumeric( { allow: "-,.' \\\"" , nchars:"_0123456789"});
	
	//Below function validates user details
	$("#updatenycnameandemailform").validate({  
                                rules: {
                                          fName:{required:true, maxlength:32, allowSpecialChar: ["T", ",.\\\"\\\'\\\" -"] },
                                          mName:{maxlength:1, allowSpecialChar: ["T", ",.\\\"\\\'\\\" -"]},
                                          lName:{required:true, maxlength:64, allowSpecialChar: ["T", ",.\\\"\\\'\\\" -"] },
                                          emailAdd:{required:true, email:true, maxlength:128 },
                                          confirmEmailAdd:{required:true, equalTo: "#emailAdd", maxlength:128 }
                                	   },                
                                messages: {
                                          fName: {
	                                           required: "<fmt:message key='REQUIRED_FIELDS'/>",
	                                           maxlength:"<fmt:message key='INPUT_32_CHAR'/>",
	                                           allowSpecialChar: "<fmt:message key='ALPHANUMERIC_ALLOWED_NAME'/>"
	                                             },
                                          mName: {
                                               maxlength:"<fmt:message key='MIDDLE_1_CHAR'/>",
                                               allowSpecialChar: "<fmt:message key='ALPHANUMERIC_ALLOWED_NAME'/>"
                                                 },
                                          lName: {
                                               required: "<fmt:message key='REQUIRED_FIELDS'/>",
                                               maxlength:"<fmt:message key='INPUT_64_CHAR'/>",
                                               allowSpecialChar: "<fmt:message key='ALPHANUMERIC_ALLOWED_NAME'/>"
                                                },
                                          emailAdd: {
                                               required: "<fmt:message key='REQUIRED_FIELDS'/>",
                                                email : "<fmt:message key='EMAIL_REQUIREMENT'/>",
                                               maxlength:"<fmt:message key='INPUT_128_CHAR'/>"
                                                },
                                          confirmEmailAdd: {
                                               required: "<fmt:message key='REQUIRED_FIELDS'/>",
                                               maxlength:"<fmt:message key='INPUT_128_CHAR'/>",
                                               equalTo: "<fmt:message key='EMAIL_NOT_MATCHED'/>" 
                                                }
                                	  }, 
                                invalidHandler: function(form, validator) {
      								$(".failed").hide();
                                },errorPlacement: function(error, element) {
			         				error.appendTo(element.parent().parent().find("span.error"));
                }
                });
});
</script>
	