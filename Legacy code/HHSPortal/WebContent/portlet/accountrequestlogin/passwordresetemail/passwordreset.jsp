<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.nyc.hhs.model.FaqFormBean"%>
<%@page import="com.nyc.hhs.constants.ApplicationConstants"%>
<%@page import="javax.portlet.*"%>
<%@ page import="com.nyc.hhs.frameworks.grid.*"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="com.nyc.hhs.util.PropertyLoader"%>
<%@page import="com.nyc.hhs.service.filenetmanager.p8constants.P8Constants"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@page errorPage="/error/errorpage.jsp"%>
<head>
<meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Expires" content="0">
</head>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<portlet:defineObjects />
<style>
	.errorMessages{
		display:none;
	}
	.formcontainer .row span.label{
		width:36%;
	}
	.formcontainer .row span.formfield{
		width:29%;
	}	
</style>

<input type="hidden" name="checkedmethod" value="selectedContact"  id="hiddenChkEvidanceOld"/>
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

<form id="passwrdResetForm" action="<portlet:actionURL/>" method ="post" name="passwrdResetForm">
	<!-- Body Container Starts -->
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
				<p>Please enter the email address you use to log in to HHS Accelerator and select a method to reset your password:</p>
				<!-- Form Data Starts -->
				 <div class="row">
				      <span class="label">Email Address:</span>
				      <span class="formfield">
				      		<input id="emailAddress" name="emailAddress" type="text" class="input" value="" maxlength="128"/>
				      </span>
				      <span class="error"></span>
				 </div>
			 
	    		<div class="row">
				      <span class="label" style="height:40px;">Password Reset Method:</span>
				      <span class="formfield">      
					      	<input type="radio" id="passwordresetmethod" name="passwordresetmethod" value="viasecurityquestions"/>Verify identity via security questions <br/>
					      	<input type="radio"  id="passwordresetmethod" name="passwordresetmethod" value="viaemail"/>Verify identity via email
				      </span>
				      <span class="error"></span>
				</div>
			 
				<div class="buttonholder">
					<input type="button" class="graybtutton"  id = "cancelshare1" class="graybtutton" value="Cancel" / >
					<input  class="button" value="Continue" type="submit" id="Continue" />
				</div>
				<!-- Form Data Ends -->
			</div>
		</div>
	</div>
</form>

<script type=text/javascript>
	var contextPathVariable = "<%=request.getContextPath()%>";
	//jquery ready function- executes the script after page loading
	$(document).ready( function() {
	
		// performs logic/validations once user clicks on 'continue' button after selecting pwd reset method
		$("#Continue").click(function(){
			$("#passwrdResetForm").validate({  
                                   rules: {
                                           emailAddress:  {required: true, maxlength: 128, email :true },
                                           passwordresetmethod:{required: true }
                                          },
                                   messages: {
                                              emailAddress: { required: "<fmt:message key='REQUIRED_FIELDS'/>",
                                              				  maxlength: "<fmt:message key='EMAIL_LIMIT'/>", 
                                              				  email: "<fmt:message key='EMAIL_REQUIREMENT'/>" },
                                              passwordresetmethod:{ required: "<fmt:message key='REQUIRED_FIELDS'/>" }
                                		  },
            
				//submit form once all validations are passed
	            submitHandler: function(form){
	            	$('#Continue').attr("disabled", true);
	            	pageGreyOut();
		            var selectedContactArray = document.getElementsByName("passwordresetmethod");
			        var selectedContact = "";
	        		var i=0;
	         		for(i=0; i<selectedContactArray.length; i++){
	         			if(selectedContactArray[i].checked){
	         				selectedMethod = selectedContactArray[i].value;
				            break;
	         			}
	       			}   
					document.passwrdResetForm.action=document.passwrdResetForm.action+'&accoutRequestmodule=accoutRequestmodule&next_action='+selectedMethod;
		  			document.passwrdResetForm.submit();
					},errorPlacement: function(error, element) {
			         	error.appendTo(element.parent().parent().find("span.error"));
			  	  }
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
