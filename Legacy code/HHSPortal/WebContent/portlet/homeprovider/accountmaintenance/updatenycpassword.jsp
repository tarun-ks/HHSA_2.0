<%@page import="java.util.ArrayList"%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="com.nyc.hhs.frameworks.grid.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<portlet:defineObjects/>
<style type="text/css">
.row span.label{
	width: 42% !important;
}
.row span.formfield{
	width: 54% !important;
}
.row span.formfield .error{
	display: block;
}
</style>
<!-- Body Wrapper Start -->
<form id="updatenycpassword" name="updatenycpassword" action="<portlet:actionURL/>&next_action=updatenycpassword" method ="post" >

	<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S027_PAGE_1, request.getSession())) {%>
		<%
		String lsTransactionMsg = "";
		if (null!=request.getAttribute("transactionMessage")){
			lsTransactionMsg = (String)request.getAttribute("transactionMessage");
		}
		if(( null != request.getAttribute("transactionStatus") ) && "failed".equalsIgnoreCase((String)request.getAttribute("transactionStatus"))){%>
		     <div id="transactionStatusDiv" class="failed" style="display:block" ><%=lsTransactionMsg%> </div>
		<%}%>
		<H2>Update NYC.ID Password</H2>
		<p>Use this page to change the password that is used to log into HHS Accelerator. 
		Please note that changing this password will change the password to other NYC applications.
		</p>
		<DIV class=hr></DIV>
		<a style="visibility:hidden;display:none" id="homeHref" href="<%=request.getContextPath()%>/portal/hhsweb.portal?_nfpb=true&_nfls=false&_pageLabel=portlet_hhsweb_portal_page_provider_home"></a>
		<DIV class=formcontainer>
			<P class=note><SPAN class=required>*</SPAN>Indicates required fields</P>
			<DIV class=clear></DIV>
			<!-- Form Data Starts -->
			<DIV style="WIDTH: 53%" class=floatLft>
				<DIV class=row><SPAN class=label><SPAN class=required>*</SPAN>Old Password:</SPAN> <SPAN class=formfield><INPUT class=input type="password" placeholder="Password" id="oldPassword" maxlength="64" name="oldPassword" autocomplete="off"></SPAN> </DIV>
				<DIV class=row><SPAN class=label><SPAN class=required>*</SPAN>New Password:</SPAN> <SPAN class=formfield><INPUT class=input type="password" placeholder="Password" id="newPassword" maxlength="64" name="newPassword" autocomplete="off"></SPAN> </DIV>
				<DIV class=row><SPAN class=label><SPAN class=required>*</SPAN>Confirm New Password:</SPAN> <SPAN class=formfield><INPUT class=input type="password" placeholder="Password" id="confirmPassword" maxlength="64" name="confirmPassword" autocomplete="off"> </SPAN></DIV>
			</DIV>
			<DIV style="TEXT-ALIGN: left; WIDTH: 40%; line-height: 18px;" class="floatLft note">Please note that your password must be eight characters or greater and must contain at least three of the following four characters: an upper case letter, a lower case letter, a number, and a symbol (&amp;, *, #,!,@,%). </DIV>
			<DIV class=buttonholder><INPUT class=graybtutton value=Cancel title="Cancel" type=button onclick="goToHome();"><INPUT class=button value="Save" title="Save" type=submit id="SaveId"></DIV>
			<!-- Form Data Ends -->
		</DIV>
		<!-- Body Container Ends -->
	<% }else{ %>
		<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
	<%} %>
</form>	
 
<script type="text/javascript">

//jquery ready function- executes the script after page loading
$(document).ready( function() {

		//adding new method 'passwd_policy' to jquery validate api, this method defines password validation rules
$.validator.addMethod("passwd_policy", function( value, element, param ) {
    return this.optional(element) 
        || (value.length >= 8 
            && (/((?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]))/.test(value)  
            || /((?=.*[a-z])(?=.*[A-Z])(?=.*[&,*,#,!,@,%]))/.test(value)
            || /((?=.*[a-z])(?=.*[0-9])(?=.*[&,*,#,!,@,%]))/.test(value)
            || /((?=.*[A-Z])(?=.*[0-9])(?=.*[&,*,#,!,@,%]))/.test(value)
				));
},"Password not valid" );

	//Below function validates user details(old and new passwords)
		$("#SaveId").click(function(){
	$("#updatenycpassword").validate({  
						onfocusout: false,
					    focusCleanup: true,
						focusInvalid: false,
                         rules: {
                                  oldPassword: {required: true, maxlength:64, passwd_policy:true },
                                  newPassword: {required: true, maxlength:64, passwd_policy:true, allowSpecialChar: ["A", "\\\&\\\*\\\#\\\!\\\@\\\%"] },
                                  confirmPassword: {required: true, maxlength:64, passwd_policy:true, equalTo: "#newPassword"}
                          },
                         messages: {
                                  oldPassword: {
                                           	required: "<fmt:message key='REQUIRED_FIELDS'/>",
  	                                       	maxlength:"<fmt:message key='INPUT_64_CHAR'/>"
       		                                 },
                                  newPassword: {
                                           	required: "<fmt:message key='REQUIRED_FIELDS'/>",
                                          	maxlength:"<fmt:message key='INPUT_64_CHAR'/>",
                                          	allowSpecialChar: "<fmt:message key='PASSWORD_NOT_VALID'/>" 
              	                             },
                                  confirmPassword:{
                      	              		equalTo: "<fmt:message key='PASSWORD_NOT_MATCH'/>",
											required: "<fmt:message key='PASSWORD_RE_ENTER'/>",
                      	              
                      	                   	maxlength:"<fmt:message key='INPUT_64_CHAR'/>"
                          	                 }
                         		  }, invalidHandler: function(form, validator) {
											$(".failed").hide();
                        				}
       				});
		});
});

//Below function brings user to provider home page
function goToHome(){
	var url= "<%=request.getContextPath()%>/portal/hhsweb.portal?_nfpb=true&_nfls=false&_pageLabel=portlet_hhsweb_portal_page_provider_home";
	location.href=url;
	//document.getElementById("homeHref").click();
}

</script>
 