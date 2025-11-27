<%@ page errorPage="/error/errorpage.jsp"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.nyc.hhs.constants.ApplicationConstants"%>
<%@page import="javax.portlet.*"%>
<%@ page import="com.nyc.hhs.frameworks.grid.*"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<portlet:defineObjects />           

<div class="hhs_header">
	<h1>Human Health Services Accelerator</h1>
	<!-- toolbar start -->
	<div class="toolbar">
		<div class="textresize" style="font-size:12px;">Text Size:
   			 <ul>
		        <li><a onclick="changemysize(this, 10);" href="javascript:void(0);"  title="Small Text Size" id="smallA">A</a></li>
		        <li><a onclick="changemysize(this, 12);" href="javascript:void(0);"  title="Medium Text Size" id="mediumA" class="textmedium">A</a></li>
		        <li><a onclick="changemysize(this, 14);" href="javascript:void(0);"  title="Large Text Size" id="largeA" class="textbig">A</a></li>
		    </ul>
		    <input type='hidden' name='aaaValueToSet' id='aaaValueToSet' value='${aaaValueToSet}' />
		    <input type='hidden' name='urlForAAA' id='urlForAAA' value='${pageContext.servletContext.contextPath}/saveAAASize?next_action=aaaValueToSet' />
		</div>
    </div>
	<!-- toolbar ends -->
</div>

<form id="myPwdResetNewPwd" action="<portlet:actionURL/>" method ="post" name="myPwdResetNewPwd">
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
	<div class="" id="mymain">
	<h2>Reset Password</h2>
	<div class='hr'></div>
	<div class="">
		<div class='formcontainer'>
			<p>Enter your new password below.</p>
			<!-- Form Data Starts -->
			<div class="row">
	      		<span class="label">New NYC.ID Password:</span>
	      		<span class="formfield">
	      			<input  id="newNycIdPwd" maxlength="64" name="newNycIdPwd" type="password" class="input" value="" autocomplete="off"/>
	      		</span>
	      		<span class="error"></span>
			</div>
			
			<div class="row">
	      		<span class="label">Confirm New NYC.ID Password:</span>
	      		<span class="formfield">
	      			<input id="confirmNewPwd" maxlength="64" name="confirmNewPwd" type="password" class="input" value="" autocomplete="off"/>
	      		</span>
	      		<span class="error"></span>
			</div>
			
	  		<div class="row note">
	      		Your password must be eight characters or greater and must contain at least three of the following four characters: an upper case letter, a lower case letter, a number, and a symbol(&,*, #, !,@, %).
			</div>
			
			<div class="buttonholder"><input type="button"   id = "cancelshare1" class="graybtutton" value="Cancel" />
				<input type="submit" class="button" id="ResetId" title="Reset" value="Reset" />
			</div>
			<!-- Form Data Ends -->
		</div>
	</div>
</div>
<!-- Body Container Ends -->
</form>

<script type=text/javascript>
	var contextPathVariable = "<%=request.getContextPath()%>";
	//jquery ready function- executes the script after page loading
	$(document).ready( function(){

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
	
		//jquery validate method for front end validations defined in rules and corresponding messages when validation is violated
			$("#ResetId").click(function(){
		$("#myPwdResetNewPwd").validate({  
	                             rules: {
	                                     newNycIdPwd: {required: true, maxlength:64, passwd_policy:true, allowSpecialChar: ["A", "\\\&\\\*\\\#\\\!\\\@\\\%"] },
	                                     confirmNewPwd: {required: true, maxlength:64, equalTo: "#newNycIdPwd", passwd_policy:true }
	                                	},
	                             messages: {
	                                     newNycIdPwd: {required: "<fmt:message key='REQUIRED_FIELDS'/>",
	                                     passwd_policy: "<fmt:message key='PASSWORD_CRITERIA'/>", 
                                         allowSpecialChar: "<fmt:message key='PASSWORD_CRITERIA'/>" 	                                     
	                                     },
	                                     confirmNewPwd:{ required: "<fmt:message key='PASSWORD_RE_ENTER'/>" ,
	                                                     equalTo: "<fmt:message key='PASSWORD_NOT_MATCHED'/>",
	                                                     passwd_policy: "<fmt:message key='PASSWORD_CRITERIA'/>"
	                                                     }
	                                    },
		    //submit form once all validations are passed
		    submitHandler: function(form){
				document.myPwdResetNewPwd.action=document.myPwdResetNewPwd.action+'&accoutRequestmodule=accoutRequestmodule&next_action=navigateToLoginScreen';                
		        document.myPwdResetNewPwd.action.submit();
		    },errorPlacement: function(error, element) {
			         				error.appendTo(element.parent().parent().find("span.error"));}
		    
		    
		});
		});
		//jquery click method call when user clicks cancel button that returns user to hhs login page                
	    $('#cancelshare1').click(function() {
			var url =contextPathVariable+"/portal/hhsweb.portal";
			location.href= url;
		    return false; 
	    });            
	});
</script>
