<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.nyc.hhs.constants.ApplicationConstants"%>
<%@page import="javax.portlet.*"%>
<%@ page import="com.nyc.hhs.frameworks.grid.*"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<portlet:defineObjects />	

<% 
	String lsQuestion1;
	String lsQuestion2;
    lsQuestion1 = (String)renderRequest.getAttribute("Question1");
    lsQuestion2 = (String)renderRequest.getAttribute("Question2");
%>

<!--<script type="text/JavaScript" src="js/curvycorners.js"></script> -->
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

<form id="myPasswordResetSecurityForm" action="<portlet:actionURL/>" method ="post" name="myPasswordResetSecurityForm">
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
				<p>To Reset your password please correctly answer both security questions below to verify your identity.</p>
				
				<!-- Form Data Starts -->
				<div class="row">
					<span class="label">Security Question #1:</span>
				    <span class="formfield">
				    	<label id="securityQues1" ><%=lsQuestion1%></label>
				    </span>
				 </div>
				 
				 <div class="row">
				 	<span class="label">Your Answer:</span>
				    <span class="formfield">
				    	<input id="securityAns1" maxlength="255" name="securityAns1" type="text" class="input" />
				    </span>
				    <span class="error"></span>
				 </div>
				 
				 <div class="row">
				 	<span class="label">Security Question #2:</span>
				    <span class="formfield">
				    	<label id="securityQues2"><%=lsQuestion2%></label>
				    </span>
				 </div>
				 
				 <div class="row">
				 	<span class="label">Your Answer:</span>
				    <span class="formfield">
				    	<input id="securityAns2"  maxlength="255" name="securityAns2" type="text" class="input" />
				    </span>
				    <span class="error"></span>
				 </div>
				 
				 <div class="buttonholder"><input type="button" id = "cancelshare1" class="graybtutton" value="Cancel" />
				 	<input type="submit" class="button" value="Continue"/>
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
	$(document).ready( function() {
		
		$('#securityAns1').alphanumeric( { allow: "-.' " , nchars:"_"});
		$('#securityAns2').alphanumeric( { allow: "-.' " , nchars:"_"});
		
		//jquery validate method for front end validations defined in rules and corresponding messages when validation is violated 
		$("#myPasswordResetSecurityForm").validate({ 
	                                rules: {
	                                        securityAns1:{required: true, maxlength: 255, minlength:3, allowSpecialChar: ["A", ".\\\' -"]},
	                                        securityAns2:{required: true, maxlength: 255, minlength:3, allowSpecialChar: ["A", ".\\\' -"]}  
	                                },
	                                
	                                messages: {
	                                securityAns1:{required: "<fmt:message key='REQUIRED_FIELDS'/>",
	                                              maxlength:  "<fmt:message key='SECURITY_ANS_MAX_LEN'/>",
	                                              minlength:"<fmt:message key='SECURITY_ANS_MIN_LEN'/>",
	                                              allowSpecialChar: "<fmt:message key='ALPHANUMERIC_ALLOWED_ANSWER'/>"
	                                             },
	                              	securityAns2:{required: "<fmt:message key='REQUIRED_FIELDS'/>",
	                                              maxlength: "<fmt:message key='SECURITY_ANS_MAX_LEN'/>",
	                                              minlength:"<fmt:message key='SECURITY_ANS_MIN_LEN'/>",
	                                              allowSpecialChar: "<fmt:message key='ALPHANUMERIC_ALLOWED_ANSWER'/>"
	                                             }   
	                                },
				//submit form once all validations are passed	                                
	            submitHandler: function(form){
		  			document.myPasswordResetSecurityForm.action=document.myPasswordResetSecurityForm.action+'&next_action=navigateToResetPwd&accoutRequestmodule=accoutRequestmodule';
		  			document.myPasswordResetSecurityForm.submit();
				},errorPlacement: function(error, element) {
			      	error.appendTo(element.parent().parent().find("span.error"));
				  }
	    });
		
		//jquery click method call when user clicks cancel button that returns user to hhs login page                
	    $('#cancelshare1').click(function() {
		    var url =contextPathVariable+"/portal/hhsweb.portal";
		    location.href= url;
		    return false;	
		});
	});

</script>