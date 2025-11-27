<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.nyc.hhs.model.MissingNameBean"%>
<%@page import="com.nyc.hhs.constants.ApplicationConstants"%>
<%@page import="javax.portlet.*"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ page import="com.nyc.hhs.frameworks.grid.*"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>

 
<portlet:defineObjects />
<form id="missingprofileinfo" action="<portlet:actionURL/>" method ="post" name="missingprofileinfo">
	<div id="mymain">
		<%
		String lsTransactionMsg = "";
		if (null!=request.getAttribute("transactionMessage")){
			lsTransactionMsg = (String)request.getAttribute("transactionMessage");
		}
		if(( null != request.getAttribute("transactionStatus") ) && "failed".equalsIgnoreCase((String)request.getAttribute("transactionStatus"))){%>
			<div id="transactionStatusDiv" class="failed" style="display:block" ><%=lsTransactionMsg%> </div>
		<%}%>
		<h2>Missing Profile Information</h2>
		<div class='hr'></div>
		
		<div class='formcontainer'>
			<p>Our records indicate that your profile is missing information. Please fill out this information to continue.</p>
			<p class='note'><span class='required'>*</span>Indicates required fields</p>
			<!-- Form Data Starts -->
			  <div class="row">
			      <span class="label"><span class='required'>*</span>First Name:</span>
			      <span class="formfield">
			      	<input id="firstName" name="firstName" type="text" class="input" value=""  maxlength= "32" />
			      </span>
			 </div>
			 <div class="row">
			 	<span class="label">Middle Name:</span>
			    <span class="formfield"><input id="middleName"  name="middleName" class="input" type="text" maxlength= "1" style='width:36px;' /></span>
			  </div>
			  <div class="row">
			  	<span class="label"><span class='required'>*</span>Last Name:</span>
			    <span class="formfield"><input id="lastName" name="lastName" class="input" maxlength= "64" type="text"/></span>
			 </div>
			 
			<!-- Form Data Ends -->
			 
			<div class="buttonholder"><input type="button" id = "cancelshare1" class="graybtutton" title="Cancel" value="Cancel" />
				<!-- <input type="button" class="button" value="Continue" onClick="javascript:navigateToTermsConditions();"/></div> -->
				<input type="submit" class="button" title="Continue" value="Continue"/>
			</div>
		
		</div>
	
	</div>
</form>

<script type=text/javascript>
var contextPathVariable = "<%=request.getContextPath()%>";
$(document).ready( function() {
$('#firstName').alphanumeric( { allow: "-,.' \\\"" , nchars:"_0123456789"});
$('#middleName').alphanumeric( { allow: "-,.' \\\"" , nchars:"_0123456789"});
$('#lastName').alphanumeric( { allow: "-,.' \\\"" , nchars:"_0123456789"});
$("#missingprofileinfo").validate({
	rules: {
    	firstName:  {
        	required: true, allowSpecialChar: ["T", ",.\\\"\\\'\\\" -"]
        },

    	middleName:  {
        	allowSpecialChar: ["T", ",.\\\"\\\'\\\" -"]
        },
                                        
        lastName:  {
        	required: true, allowSpecialChar: ["T", ",.\\\"\\\'\\\" -"]
        }
	},
	messages: {
    	firstName: { required: "<fmt:message key='REQUIRED_FIELDS'/>",
					 allowSpecialChar: "<fmt:message key='ALPHANUMERIC_ALLOWED_NAME'/>"    				
        	},
		middleName: {allowSpecialChar: "<fmt:message key='ALPHANUMERIC_ALLOWED_NAME'/>"
		    },        	
        lastName: { required: "<fmt:message key='REQUIRED_FIELDS'/>",
					allowSpecialChar: "<fmt:message key='ALPHANUMERIC_ALLOWED_NAME'/>"        
        	}
		},
		submitHandler: function(form){
			document.missingprofileinfo.action=document.missingprofileinfo.action+'&next_action=missingScreen&accoutRequestmodule=accoutRequestmodule';
			document.missingprofileinfo.submit();
            }
        });
$('#cancelshare1').click(function() {
	//pageGreyOut();
	deleteSaveValues();
	});
                
                
});
//this function is for navigation to login page
function deleteSaveValues(){
	returnSuccess();
	return false;	
}
//this function is for navigation to login page
function returnSuccess(){
	var url =contextPathVariable+"/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_login_page&_nfls=false&logout=logout&app_menu_name=logout_icon" ;
	location.href= url;
}

</script>

