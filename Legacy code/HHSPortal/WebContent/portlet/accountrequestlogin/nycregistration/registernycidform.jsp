<%@page import="java.util.*"%>
<%@page import="com.nyc.hhs.model.RegisterNycIdBean"%>
<%@page import="net.tanesha.recaptcha.ReCaptcha" %>
<%@page import="net.tanesha.recaptcha.ReCaptchaFactory" %> 
<%@page import="javax.portlet.PortletSession"%>
<%@page import="com.nyc.hhs.constants.ApplicationConstants" %>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="auth" uri="http://www.bea.com/servers/p13n/tags/auth" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<head>
<meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Expires" content="0">
</head>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>

<portlet:defineObjects/>
<style>
	.DMCError {
		color: red;
	}

	ul.errorClass {
		border: 1px solid #E80000;
		margin: 10px;
		padding: 8px;
		width: 78%;
		clear: both;
		background: #FFCCBA url(../framework/skins/hhsa/images/iconError.png)
			no-repeat 8px center;
		list-style-type: disc;
		list-style-position: inside;
		line-height: 31px;
	}
	.input{
	width: auto;
	border:1px solid #CCC
}
</style>

<div class="hhs_header">
	<h1>Human Health Services Accelerator</h1>
	    
	<!-- toolbar start -->
	<div class="toolbar">
		<div class="textresize">Text Size:
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

<!-- Body Wrapper Start -->
<form id="registernycid" action="<portlet:actionURL/>" method="post"
	  name="registernycid"><c:set var="registerNycIdBean"
	  value="${RegisterNycIdBean}"></c:set> <input type="hidden"
	  name="buttonHit" id="buttonHit" value="">
	  <DIV id="mymain">
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
	<H2>Register for NYC.ID</H2>
	<DIV class=hr></DIV>
		<DIV class=formcontainer>
			<P>Fill in the required fields to submit a request for an NYC.ID</P>
			<P class=note><SPAN class=required>*</SPAN>Indicates required fields</P>
			<BR>
			<H3>General Information</H3>
			
			<!-- Form Data Starts -->
			<input id="ques1Text" name ="ques1Text" type="hidden" value="" />
			<input id="ques2Text" name ="ques2Text" type="hidden" value="" />
			<input id="ques3Text" name ="ques3Text" type="hidden" value="" />
			
			<DIV class=row><SPAN class=label><SPAN class=required>*</SPAN>First
			Name:</SPAN>
			<SPAN class=formfield> <INPUT class=input type=text
				id="firstName" name="firstName" maxlength="32"
				value="${registerNycIdBean.msFirstName}"><label
				class="DMCError" id="firstNameDiv"></label>
			</SPAN>
			<span class="error"></span>
			</DIV>
			
			<DIV class=row><SPAN class=label>Middle Initial:</SPAN>
				<SPAN
					class=formfield> <INPUT style="WIDTH: 36px" class=input
					type=text id="middleName" name="middleName" maxlength="1"
					value="${registerNycIdBean.msMiddleName}"><label
					class="DMCError" id="middleNameDiv"></label>
				</SPAN>
			<span class="error"></span>
			</DIV>
			
			<DIV class=row><SPAN class=label><SPAN class=required>*</SPAN>Last
			Name:</SPAN>
			<SPAN class=formfield> <INPUT class=input type=text
				id="lastName" name="lastName" maxlength="64"
				value="${registerNycIdBean.msLastName}"><label class="DMCError"
				id="lastNameDiv"></label>
			</SPAN>
			<span class="error"></span>
			</DIV>
			
			<DIV class=row></DIV>
			
			<DIV class=row><SPAN class=label><SPAN class=required>*</SPAN>Email
			Address:</SPAN>
			<SPAN class=formfield> <INPUT class=input type=text
				id="email" name="email" maxlength="128"
				value="${registerNycIdBean.msEmailAddress}"><label
				class="DMCError" id="emailDiv"></label>
			</SPAN>
			<span class="error"></span>
			</DIV>
			
			<DIV class=row><SPAN class=label><SPAN class=required>*</SPAN>Confirm
			Email Address:</SPAN>
			<SPAN class=formfield> <INPUT class=input
				type=text id="confirmEmail" name="confirmEmail"
				value="${registerNycIdBean.msConfirmEmailAddress}"><label
				class="DMCError" id="confirmEmailDiv"></label>
			</SPAN>
			<span class="error"></span>
			</DIV>
			
			<DIV class="noteOverlapped note">Please note that a valid email address
			 is required to activate your NYC.ID
			</DIV>
			
			<DIV class=row><SPAN class=label><SPAN class=required>*</SPAN>Password:</SPAN>
			<SPAN class=formfield> <INPUT class=input type=password
				id="password" name="password" value="" maxlength="64" autocomplete="off"><label class="DMCError"
				id="passwordDiv"></label>
			</SPAN>
			<span class="error"></span>
			</DIV>
			
			<DIV class=row><SPAN class=label><SPAN class=required>*</SPAN>Confirm
			Password:</SPAN>
			<SPAN class=formfield> <INPUT class=input
				id="confirmPassword" name="confirmPassword" type=password value="" maxlength="64" autocomplete="off"><label
				class="DMCError" id="confirmPasswordDiv"></label>
			</SPAN>
			<span class="error"></span>
			</DIV>
			
			<DIV class="noteOverlapped note">Please note that your password must be
			eight characters or greater and must contain at least three of the
			following four characters: an upper case letter, a lower case letter, a
			number, and a symbol(&amp;,*, #, !,@, %).
			</DIV>
			<DIV class=row><SPAN class=label><SPAN class=required>*</SPAN>Security
			Question #1</SPAN> 
			<SPAN class=formfield>
				<SELECT class="" id="securityQuestion1" name="securityQuestion1"
					onchange="changeQuestion('securityQuestion1');">
					<OPTION value="">- Select One -</OPTION>
					<c:forEach var="beanItem" items="${registerNycIdBean.moSecurityQuestion1List}">
						<OPTION value="${beanItem.miquestionId}"
							<c:if test="${beanItem.miquestionId==registerNycIdBean.miSecurityQuestion1Id}"> selected</c:if>>${beanItem.msQuestionText}
						</OPTION>
					</c:forEach>
				</SELECT>
				<label class="" id="securityQuestion1Div"></label>
			<span class="error" style="width:auto;"></span>
			</SPAN>
			
			</DIV>
			
			<DIV class=row><SPAN class=label>Your Answer:</SPAN>
			<SPAN
				class=formfield> <INPUT class=input type=text id="answer1"
				name="answer1" maxlength="255" value="${registerNycIdBean.msAnswer1}"><label
				class="DMCError" id="answer1Div"></label>
			</SPAN>
			<span class="error"></span>
			</DIV>
			
			<DIV class=row><SPAN class=label><SPAN class=required>*</SPAN>Security
			Question #2</SPAN>
			<SPAN class=formfield>
				<SELECT class="" id="securityQuestion2" name="securityQuestion2"
				onchange="changeQuestion('securityQuestion2');">
					<OPTION value="">- Select One -</OPTION>
					<c:forEach var="beanItem" items="${registerNycIdBean.moSecurityQuestion2List}">
						<OPTION value="${beanItem.miquestionId}"
							<c:if test="${beanItem.miquestionId==registerNycIdBean.miSecurityQuestion2Id}"> selected</c:if>>${beanItem.msQuestionText}
						</OPTION>
					</c:forEach>
				</SELECT>
			<label class="DMCError" id="securityQuestion2Div"></label>
			<span class="error" style="width:auto;"></span></SPAN>
			
			</DIV>
			
			<DIV class=row><SPAN class=label>Your Answer:</SPAN>
			<SPAN
				class=formfield> <INPUT class=input type=text id="answer2"
				name="answer2" maxlength="255" value="${registerNycIdBean.msAnswer2}"><label
				class="DMCError" id="answer2Div"></label>
			</SPAN>
			<span class="error"></span>
			</DIV>
			
			<DIV class=row><SPAN class=label><SPAN class=required>*</SPAN>Security Question #3</SPAN>
			<SPAN class=formfield>
				<SELECT class="" id="securityQuestion3" name="securityQuestion3" onchange="changeQuestion3('securityQuestion3');">
					<OPTION value="" selected>- Select One -</OPTION>
					<c:forEach var="beanItem" items="${registerNycIdBean.moSecurityQuestion3List}">
							<OPTION value="${beanItem.miquestionId}"
								<c:if test="${beanItem.miquestionId==registerNycIdBean.miSecurityQuestion3Id}"> selected</c:if>>${beanItem.msQuestionText}
							</OPTION>
					</c:forEach>
					<c:if test=""></c:if>
				</SELECT>
				<label class="DMCError" id="securityQuestion3Div"></label>
			<span class="error" style="width:auto;"></span></SPAN>
			
			</DIV>
			
			<DIV class=row><SPAN class=label>Your Answer:</SPAN>
			<SPAN
				class=formfield> <INPUT class=input type=text id="answer3"
				name="answer3" maxlength="255" value="${registerNycIdBean.msAnswer3}"><label
				class="DMCError" id="answer3Div"></label>
			</SPAN>
			<span class="error"></span>
			</DIV>
			
			<DIV class="pad10 noteOverlapped">
			<%if(session.getAttribute(ApplicationConstants.CAPCHA_REQUIRED)!=null){ %>
				<SPAN class=required>*</SPAN>Please check the box below (If you do not see a box to check, please <a href="http://www1.nyc.gov/site/hhsaccelerator/help/contact.page" class=blueLink>contact HHS Accelerator</a>)
			<%}%>  
			</DIV>
			
			<div>
			   <%if(session.getAttribute(ApplicationConstants.CAPCHA_REQUIRED)!=null){ %>
			 	<script type="text/javascript" src="<%=session.getAttribute(ApplicationConstants.PROPERTY_DOITT_CAPTCHA_SERVICE_UI)%>"></script>
				<%}%>  
				<span id="recapchaSpan" class="error floatLft"></span>
				<%if(null!=request.getAttribute("captchaFailed")){%>
					<script type="text/javascript"></script>
			    	<span id="recapchaSpanId" class="error floatLft"><%=request.getAttribute("captchaFailed")%> </span>
				<%}%>
			</div>
			
		</div>
		
		<div class="buttonholder" style="text-align: center">
			<input class="graybtutton" value="Cancel" id="cancel" title='Cancel' name="cancel" type="button" onclick="redirectToLogin();" /> 
			<input class="button" value="Register" id="register" title='Register' name="register" type=submit />
		</div>
		
	</div>
</form>

<script type=text/javascript>
var contextPathVariable = "<%=request.getContextPath()%>";
	//Below function removes left and right spaces from string
	function trim(stringToTrim) {
		return stringToTrim.replace(/^\s+|\s+$/g,"");
	}
	
	//Below function logouts user and brings/redirects user to login page
	function redirectToLogin(){
	   var url =contextPathVariable+"/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_login_page&_nfls=false&logout=logout&app_menu_name=logout_icon";
   	   location.href= url;
	}

	//Below function is called once the user selects the question3 from dropdown
	function changeQuestion3(comboId){
		var selectedCombo = comboId;
  		var selectedQuestion = document.getElementById(comboId).value;  
		document.getElementById("answer3").value="";
  		if(selectedQuestion!=""){
       	document.getElementById("answer3").disabled=false;
		}else{
		document.getElementById("answer3").disabled=true;
		}
	}
	//Below function is called once the user selects the question from dropdown
	// further this function also enables/disables the selectability of 2nd and 3rd question
	function changeQuestion(comboId){
		var selectedCombo = comboId;
  		var selectedQuestion = document.getElementById(comboId).value;            
  		var v_parameter="";
  		if(selectedQuestion!=""){
			pageGreyOut();
  			var urlAppender = contextPathVariable+"/RetrieveQuestionsAjaxServlet.jsp?accoutRequestmodule=accoutRequestmodule&selectedCombo="+selectedCombo+"&selsetedQuetion="+selectedQuestion;
     		jQuery.ajax({
     			type: "POST",
     			url: urlAppender,
     			data: v_parameter,
     			success: function(e){
         			if("securityQuestion1"==selectedCombo){
        	 			removeAllOptions("securityQuestion2");
			        	removeAllOptions("securityQuestion3");
			        	addOption(document.getElementById('securityQuestion2'), "", "- Select One -" );
			        	addOption(document.getElementById('securityQuestion3'), "", "- Select One -" );
			        	parseOutputString("securityQuestion2",e,"");
			        	document.getElementById("securityQuestion2").disabled=false;
			        	document.getElementById("answer2").disabled=true;
			        	document.getElementById("securityQuestion3").disabled=true;
			        	document.getElementById("answer3").disabled=true;
						document.getElementById("answer2").value="";
						document.getElementById("answer3").value="";
						document.getElementById("answer1").value="";
						document.getElementById("answer1").disabled=false;
						//$.unblockUI();
						removePageGreyOut();
         			}else if("securityQuestion2"==selectedCombo){
						document.getElementById("answer2").disabled=false;
						document.getElementById("answer2").value="";
			        	removeAllOptions("securityQuestion3");
			        	addOption(document.getElementById('securityQuestion3'), "", "- Select One -" );
			        	parseOutputString("securityQuestion3",e,"");
			        	document.getElementById("securityQuestion3").disabled=false;
			        	document.getElementById("answer3").disabled=true;
						document.getElementById("answer3").value="";
						//$.unblockUI();
						removePageGreyOut();
			        }
        	    },
        	    beforeSend: function(){  //function for loading wheel
          							 }
            });
  	}else {
	        if("securityQuestion1"==selectedCombo){
	      	 			removeAllOptions("securityQuestion2");
			        	removeAllOptions("securityQuestion3");
			        	addOption(document.getElementById('securityQuestion2'), "", "- Select One -" );
			        	addOption(document.getElementById('securityQuestion3'), "", "- Select One -" );
						document.getElementById("answer1").value="";
			        	document.getElementById("answer2").value="";
			        	document.getElementById("answer3").value="";
			        	document.getElementById("securityQuestion2").disabled=true;
			        	document.getElementById("answer2").disabled=true;
			        	document.getElementById("securityQuestion3").disabled=true;
			        	document.getElementById("answer3").disabled=true;
						document.getElementById("answer1").disabled=true;
	       			}else if("securityQuestion2"==selectedCombo){
			        	removeAllOptions("securityQuestion3");
			        	addOption(document.getElementById('securityQuestion3'), "", "- Select One -" );
			        	document.getElementById("answer2").value="";
						document.getElementById("answer3").value="";
			        	document.getElementById("securityQuestion3").disabled=true;
			        	document.getElementById("answer3").disabled=true;
						document.getElementById("answer2").disabled=true;
			        }
	}
 }
	
	
	//Below function parse ajax response based upon delimiters	
	function parseOutputString(id,bsOutputString,msgFailure){
		var selectId=document.getElementById(id);
		isAjaxCallSuccess=false;
		bsOutput = bsOutputString.split ( '|' );
		for(var i=0;i < bsOutput.length;i++) {
			var myProperty = bsOutput[i];
			var bsOutput2 = myProperty.split ( ':' );
			if(bsOutput2[0] =="status"){
				if(bsOutput2[1] == "success"){
					isAjaxCallSuccess=true;
				}else{
					isAjaxCallSuccess=false;
					addOption(selectId,"", "");
				}
			}else{
				addOption(selectId,bsOutput2[0], bsOutput2[1]);
			}
		}

		if(!isAjaxCallSuccess){
			//displayError(msgFailure);
		}
	}

	//Below function remove values from question dropdown
	function removeAllOptions(id){
		selectbox=document.getElementById(id);
		var i;
		for(i=selectbox.options.length-1;i>=0;i--){
			selectbox.remove(i);
		}
	}

	//Below function add values to question dropdown
	function addOption(selectbox, value, text ){
		var optn = document.createElement("OPTION");
		optn.text = text;
		optn.value = value;
		selectbox.options.add(optn);
	}
	
	//Below function blocks further input from user and change background to grey
	/*function pageGreyOut(){
        $.blockUI({
        	message: "<img src='../framework/skins/hhsa/images/loadingBlue.gif' />",
        	overlayCSS: { opacity : 0.8}
        });
    }*/
	
	//Below function returns true if the field is blank/null and vice-versa
	function isEmpty(fieldId){
		var returnFlag = true;
		if(document.getElementById(fieldId)!=null && trim(document.getElementById(fieldId).value)!=""){
			returnFlag = false;
		}
		return returnFlag;
	}

	//adding new method 'passwd_policy' to jquery validate api, this method defines password validation rules	
	/*
	$.validator.addMethod("passwd_policy", function( value, element, param ) { 
        return this.optional(element) 
            || (value.length >= 8 
                && /.[&,*,#,!,@,%]/.test(value)  
                && /[0-9]/.test(value) 
                && /[a-z]/.test(value) 
                && /[A-Z]/.test(value)); 
    },"Your password must be at least 8 characters long, <br/>contain at least one number, <br/>" 
      +" at least one special character (&,*,#,!,@,%),<br/> at least one uppercase" 
      +" character  <br/>and at least one lowercase character."); 
      */

	//adding new method 'passwd_policy' to jquery validate api, this method defines password validation rules	

//this method checks password field if it is composed of first name, last name , email address or word "password", calls matchPasswordValues
$.validator.addMethod("passwd_values_check", function( value, element, param ) {
    return this.optional(element) 
        || (matchPasswordValues(value));
},"! Your password cannot contain your first name, last name, email address or the word 'password'. Please enter a new password." );

//this method checks password field if it is composed of first name, last name , email address or word "password"
function matchPasswordValues(lsPasswordEntered){
 var lbPasswordOK = true; // we assume password is in correct format
	if (null != document.getElementById("firstName").value  && trim(document.getElementById("firstName").value)!=""){
		if(lsPasswordEntered.toLowerCase().indexOf($('#firstName').val().toLowerCase()) != -1){
			lbPasswordOK = false;
		}
	}
	if (null != document.getElementById("lastName").value  && trim(document.getElementById("lastName").value)!=""){
		if(lsPasswordEntered.toLowerCase().indexOf($('#lastName').val().toLowerCase()) != -1){
			lbPasswordOK = false;
		}
	}
	if (null != document.getElementById("email").value  && trim(document.getElementById("email").value)!=""){
		if(lsPasswordEntered.toLowerCase().indexOf($('#email').val().toLowerCase()) != -1){
			lbPasswordOK = false;
		}
	}
	if(lsPasswordEntered.toLowerCase().indexOf("password") != -1){
			lbPasswordOK = false;
	}
	return lbPasswordOK;
}


$.validator.addMethod("passwd_policy", function( value, element, param ) {
    return this.optional(element) 
        || (value.length >= 8 
            && (/((?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]))/.test(value)  
            || /((?=.*[a-z])(?=.*[A-Z])(?=.*[&,*,#,!,@,%]))/.test(value)
            || /((?=.*[a-z])(?=.*[0-9])(?=.*[&,*,#,!,@,%]))/.test(value)
            || /((?=.*[A-Z])(?=.*[0-9])(?=.*[&,*,#,!,@,%]))/.test(value)
				));
},"Password not valid" );

//adding new method 'answerNotEqual' to jquery validate api, this method checks that no two answers can be same
	$.validator.addMethod("answerNotEqual", function(value, element, param) {
		var lsequalStatus= true;
		if(null != document.getElementById(param[0]).value  && trim(document.getElementById(param[0]).value)!="" ){
		   if(trim(document.getElementById(param[0]).value) == value){
			    lsequalStatus= false;		 
			    return false;
		   }
		   else{
				$("#"+param[0]).removeClass("error");
				$("#"+param[1]).removeClass("error");
				$("#"+param[0]).parent().find("label.error").remove();
				$("#"+param[1]).parent().find("label.error").remove();
	    		lsequalStatus= true;
	       }
		}
		else{
			$("#"+param[0]).removeClass("error");
			$("#"+param[1]).removeClass("error");
			$("#"+param[0]).parent().find("label.error").remove();
			$("#"+param[1]).parent().find("label.error").remove();
			lsequalStatus= true;
		}
	
		if(null != document.getElementById(param[1]).value  && trim(document.getElementById(param[1]).value)!="" ){
			if(trim(document.getElementById(param[1]).value) == value){
				lsequalStatus= false;
				return false;		 
			}
		 	else{
				$("#"+param[0]).removeClass("error");
				$("#"+param[1]).removeClass("error");
				$("#"+param[0]).parent().find("label.error").remove();
				$("#"+param[1]).parent().find("label.error").remove();
				lsequalStatus= true;
		 	}
		}
		else{
				$("#"+param[0]).removeClass("error");
				$("#"+param[1]).removeClass("error");
				$("#"+param[0]).parent().find("label.error").remove();
				$("#"+param[1]).parent().find("label.error").remove();
				lsequalStatus= true;
		}
		return lsequalStatus;

		}, "! No two security answers may be the same. "); 

	//jquery ready function- executes the script after page loading
	$(document).ready( function() {
		$('#firstName').alphanumeric( { allow: "-,.' \\\"" , nchars:"_0123456789"});
		$('#middleName').alphanumeric( { allow: "-,.' \\\"" , nchars:"_0123456789"});
		$('#lastName').alphanumeric( { allow: "-,.' \\\"" , nchars:"_0123456789"});
		$('#answer1').alphanumeric( { allow: "-.' " , nchars:"_"});
		$('#answer2').alphanumeric( { allow: "-.' " , nchars:"_"});
		$('#answer3').alphanumeric( { allow: "-.' " , nchars:"_"});
	//	$('#password').alphanumeric( { allow: "&*#!@% "});
	//	$('#confirmPassword').alphanumeric( { allow: "&*#!@% "});
	if(document.getElementById("securityQuestion1").value==""){
        document.getElementById("answer1").disabled=true;
	}
		if(document.getElementById("securityQuestion2").length<=1){
			 document.getElementById("securityQuestion2").disabled=true;
	    	 document.getElementById("answer2").disabled=true;
	    	 document.getElementById("securityQuestion3").disabled=true;
	    	 document.getElementById("answer3").disabled=true;
		 }else{
			 document.getElementById("securityQuestion2").disabled=false;
	    	 document.getElementById("answer2").disabled=false;
	    	 document.getElementById("securityQuestion3").disabled=false;
	    	 document.getElementById("answer3").disabled=false;
		 }

$("#recaptcha_response_field").keyup(function(){
  $('#recapchaSpan').html("");
  if(document.getElementById("recapchaSpanId")!=null){
  $('#recapchaSpanId').html("");
  }
 }); 
		//Below processing/validation is done once user click on register button
		$("#register").click(function(){
			$("#registernycid").validate({
							onfocusout: false,
						    focusCleanup: true,
							focusInvalid: false,
							 
                            rules: {
                                   firstName:{required: true, allowSpecialChar: ["T", ",.\\\"\\\'\\\" -"] },
                                   middleName:{allowSpecialChar: ["T", ",.\\\"\\\'\\\" -"] },
                                   lastName:{required: true, allowSpecialChar: ["T", ",.\\\"\\\'\\\" -"] },
                                   email:{required: true, email :true },
                                   confirmEmail:{required: true, email :true, equalTo: "#email" },
                                   password:{required: true, passwd_values_check:true, passwd_policy:true,allowSpecialChar: ["A", "\\\&\\\*\\\#\\\!\\\@\\\%"] },
                                   confirmPassword:{required: true, equalTo: "#password", passwd_values_check:true, passwd_policy:true },
                                     
                                   securityQuestion1:{required: true},  
                                   securityQuestion2:{required: true},
                                   securityQuestion3:{required: true},  
                                   answer1:{required:true, minlength:3, allowSpecialChar: ["A", ".\\\' -"], answerNotEqual: ["answer2", "answer3"] },
                                   answer2:{required:true, minlength:3, allowSpecialChar: ["A", ".\\\' -"], answerNotEqual: ["answer1", "answer3"] },
                                   answer3:{required:true, minlength:3, allowSpecialChar: ["A", ".\\\' -"], answerNotEqual: ["answer1", "answer2"] },
                                   recaptcha_response_field:{required:true}
                                },
                            messages: {
                                   firstName:{ required: "<fmt:message key='REQUIRED_FIELDS'/>",
                                   			   allowSpecialChar: "<fmt:message key='ALPHANUMERIC_ALLOWED_NAME'/>" },
								   middleName:{allowSpecialChar: "<fmt:message key='ALPHANUMERIC_ALLOWED_NAME'/>" },	                                   			   
	                               lastName:{ required: "<fmt:message key='REQUIRED_FIELDS'/>",
	                               			  allowSpecialChar: "<fmt:message key='ALPHANUMERIC_ALLOWED_NAME'/>" },
                                   email:{ required: "<fmt:message key='REQUIRED_FIELDS'/>",
                                           email : "<fmt:message key='EMAIL_REQUIREMENT'/>" },
                                   confirmEmail:{ required: "<fmt:message key='REQUIRED_FIELDS'/>",
                                                  email : "<fmt:message key='EMAIL_REQUIREMENT'/>",
                                                  equalTo: "<fmt:message key='EMAIL_NOT_MATCHED'/>" },
                                   password:{ required: "<fmt:message key='REQUIRED_FIELDS'/>",
                                	   passwd_policy: "<fmt:message key='PASSWORD_CRITERIA'/>",
                                	   allowSpecialChar: "<fmt:message key='PASSWORD_CRITERIA'/>" },
                                   confirmPassword:{required: "<fmt:message key='PASSWORD_RE_ENTER'/>", 
                                   					equalTo: "<fmt:message key='PASSWORD_NOT_MATCH'/>",
                                   					passwd_policy: "<fmt:message key='PASSWORD_CRITERIA'/>"
                                                      },
                                   securityQuestion1:{ required: "<fmt:message key='REQUIRED_SECURITY_QUES'/>" },
                                   securityQuestion2:{ required: "<fmt:message key='REQUIRED_SECURITY_QUES'/>" },
                                   securityQuestion3:{ required: "<fmt:message key='REQUIRED_SECURITY_QUES'/>" },
                                   answer1:{ required: "<fmt:message key='REQUIRED_FIELDS'/>",
                                        	 minlength: "<fmt:message key='SEC_ANS_MIN_LEN'/>",
                                        	 allowSpecialChar: "<fmt:message key='ALPHANUMERIC_ALLOWED_ANSWER'/>" },
                                   answer2:{ required: "<fmt:message key='REQUIRED_FIELDS'/>",
                                        	 minlength: "<fmt:message key='SEC_ANS_MIN_LEN'/>",
                                        	 allowSpecialChar: "<fmt:message key='ALPHANUMERIC_ALLOWED_ANSWER'/>" },
                                   answer3:{ required: "<fmt:message key='REQUIRED_FIELDS'/>",
                                             minlength: "<fmt:message key='SEC_ANS_MIN_LEN'/>",
                                        	 allowSpecialChar: "<fmt:message key='ALPHANUMERIC_ALLOWED_ANSWER'/>" }
                                   
                                },
	
				//submit form once all validations are passed                                
	            submitHandler: function(form){
	            var quesId1 = document.getElementById("securityQuestion1");
		     	var question1 = quesId1.options[quesId1.selectedIndex].text;
		     	var quesId2 = document.getElementById("securityQuestion2");
		     	var question2 = quesId2.options[quesId2.selectedIndex].text;
		     	var quesId3 = document.getElementById("securityQuestion3");
		     	var question3 = quesId3.options[quesId3.selectedIndex].text;
		     	document.getElementById("ques1Text").value=question1;
		     	document.getElementById("ques2Text").value=question2;
		     	document.getElementById("ques3Text").value=question3;
		     	pageGreyOut();
				document.getElementById("buttonHit").value="register";
				document.registernycid.action=document.registernycid.action+'&removeNavigator=true&accoutRequestmodule=true';
				document.registernycid.submit();
				
				},errorPlacement: function(error, element) {
				 if(document.getElementById("recaptcha_response_field")!=null && trim(document.getElementById("recaptcha_response_field").value)==""){
				$('#recapchaSpan').html("<fmt:message key='REQUIRED_FIELDS'/>");
				  if(document.getElementById("recapchaSpanId")!=null){
                   $('#recapchaSpanId').html("");
                  }
				 }
			         	error.appendTo(element.parent().parent().find("span.error"));
			  	  }
            });
    	});
	});
</script>