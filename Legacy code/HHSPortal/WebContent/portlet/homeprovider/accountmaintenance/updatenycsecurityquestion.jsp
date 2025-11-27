<%@page import="java.util.ArrayList"%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="com.nyc.hhs.model.RegisterNycIdBean"%>
<%@page import="com.nyc.hhs.frameworks.grid.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant "%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<portlet:defineObjects/>

<script type="text/javascript">

//Below function brings user to provider home page
function goToHome(){
	var url= "<%=request.getContextPath()%>/portal/hhsweb.portal?_nfpb=true&_nfls=false&_pageLabel=portlet_hhsweb_portal_page_provider_home";
	location.href=url;
	//document.getElementById("homeHref").click();
}

//jquery ready function- executes the script after page loading
$(document).ready( function() {
	$('#answer1').alphanumeric( { allow: "-.' " , nchars:"_"});
	$('#answer2').alphanumeric( { allow: "-.' " , nchars:"_"});
	$('#answer3').alphanumeric( { allow: "-.' " , nchars:"_"});
//	$('#currentPassword').alphanumeric( { allow: "&*#!@% "});

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

	//Below function is called when user clicks on save button after changing security questions 
	$("#saveButton").click(function(){
		$("#updatenyssecurityquestion").validate({ 
        	onfocusout: false,
			focusCleanup: true,
			focusInvalid: false,
							 
	        	rules: {
	            	currentPassword:{required: true, passwd_policy:true },
	                securityQuestion1:{required: true},  
	                securityQuestion2:{required: true},
	                securityQuestion3:{required: true},  
	                answer1:{required:true, minlength:3, allowSpecialChar: ["A", ".\\\' -"], answerNotEqual: ["answer2", "answer3"] },
	                answer2:{required:true, minlength:3, allowSpecialChar: ["A", ".\\\' -"], answerNotEqual: ["answer1", "answer3"] },
	                answer3:{required:true, minlength:3, allowSpecialChar: ["A", ".\\\' -"], answerNotEqual: ["answer1", "answer2"] }
	                },
	            messages: {
	            	  currentPassword:{ required: "<fmt:message key='REQUIRED_FIELDS'/>",
        				  passwd_policy: "<fmt:message key='PASSWORD_CRITERIA'/>"	},
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
					document.updatenyssecurityquestion.submit();
					},errorPlacement: function(error, element) {
			         	error.appendTo(element.parent().parent().find("span.error"));
			  	  }
    	});
	});
});
</script>

<!-- Body Wrapper Start -->
<form id="updatenyssecurityquestion" name="updatenyssecurityquestion" action="<portlet:actionURL/>&next_action=updateSecurityQuestions" method ="post" >

	<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S027_PAGE_1, request.getSession())) {%>
		<%
		String lsTransactionMsg = "";
		if (null!=request.getAttribute("transactionMessage")){
			lsTransactionMsg = (String)request.getAttribute("transactionMessage");
		}
		if(( null != request.getAttribute("transactionStatus") ) && "failed".equalsIgnoreCase((String)request.getAttribute("transactionStatus"))){%>
	    	<div id="transactionStatusDiv" class="failed" style="display:block" ><%=lsTransactionMsg%> </div>
		<%}%>
		<H2> Update NYC ID - Security Questions</H2>
		<p>Use this page to change your security questions and answers for your HHS Accelerator account. These will be used to verify your
	 	identity in the event that you forget your password. You may not answer two security questions with the same response. 
		Please note that changing the information below will change the security questions for HHS Accelerator and other NYC applications as well. 
		</p>
		<DIV class=hr></DIV>
		<c:if test="${error_to_display eq 'true'}">
			<div class="failed" style="display:block">
				<b> !! Failed to update user details</b>
			</div>
		</c:if>
		<a style="visibility:hidden;display:none" id="homeHref" href="<%=request.getContextPath()%>/portal/hhsweb.portal?_nfpb=true&_nfls=false&_pageLabel=portlet_hhsweb_portal_page_provider_home"></a>
		<c:set var="registerNycIdBean"
			value="${RegisterNycIdBean}">
		</c:set>
		<DIV class=formcontainer>
			<input id="ques1Text" name ="ques1Text" type="hidden" value="" />
			<input id="ques2Text" name ="ques2Text" type="hidden" value="" />
			<input id="ques3Text" name ="ques3Text" type="hidden" value="" />
			<P class=note><SPAN class=required>*</SPAN>Indicates required fields</P>
			<DIV class=clear></DIV>
			<!-- Form Data Starts -->
			<DIV class=row><SPAN class=label><SPAN class=required>*</SPAN>Current Password:</SPAN> <SPAN class=formfield><INPUT class=input type="password" placeholder="Password" id="currentPassword" name="currentPassword" autocomplete="off"> </SPAN><span class="error"></span></DIV>
		
			<DIV class=row>
				<SPAN class=label><SPAN class=required>*</SPAN>Security Question #1</SPAN>
			 	<SPAN class=formfield> <SELECT 
					id="securityQuestion1" name="securityQuestion1" 
					onchange="changeQuestion('securityQuestion1');"  >
					<OPTION value="">- Select One -</OPTION>
					<c:forEach var="beanItem" items="${registerNycIdBean.moSecurityQuestion1List}">
						<OPTION value="${beanItem.miquestionId}" title = "${beanItem.msQuestionText}" 
							<c:if test="${beanItem.miquestionId==registerNycIdBean.miSecurityQuestion1Id}"> selected</c:if>>${beanItem.msQuestionText}
						</OPTION>
					</c:forEach>
					</SELECT><label class="" id="securityQuestion1Div"></label>
				<span class="error" style="width:auto;"></span>
				</SPAN>
				
			</DIV>
			
			<DIV class=row><SPAN class=label>Your Answer:</SPAN>
				<SPAN class=formfield> <INPUT class=input type=text id="answer1"
					name="answer1" maxlength="255" value="${registerNycIdBean.msAnswer1}"><label
					class="DMCError" id="answer1Div"></label>
				</SPAN>
				<span class="error"></span>
			</DIV>
			
			<DIV class=row><SPAN class=label><SPAN class=required>*</SPAN>Security
			Question #2</SPAN>
			<SPAN class=formfield> <SELECT class="" 
				id="securityQuestion2" name="securityQuestion2" 
				onchange="changeQuestion('securityQuestion2');">
				<OPTION value="">- Select One -</OPTION>
				<c:forEach var="beanItem" items="${registerNycIdBean.moSecurityQuestion2List}">
					<OPTION value="${beanItem.miquestionId}" title = "${beanItem.msQuestionText}" 
					<c:if test="${beanItem.miquestionId==registerNycIdBean.miSecurityQuestion2Id}"> selected</c:if>>${beanItem.msQuestionText}</OPTION>
				</c:forEach>
				</SELECT><label class="DMCError" id="securityQuestion2Div"></label>
			<span class="error" style="width:auto;"></span>
			</SPAN>
			
			</DIV>
			
			<DIV class=row><SPAN class=label>Your Answer:</SPAN> <SPAN
				class=formfield> <INPUT class=input type=text id="answer2"
				name="answer2" maxlength="255" value="${registerNycIdBean.msAnswer2}"><label
				class="DMCError" id="answer2Div"></label></SPAN>
				<span class="error"></span>
			</DIV>
			
			<DIV class=row><SPAN class=label><SPAN class=required>*</SPAN>Security Question #3</SPAN> <SPAN class=formfield> 
				<SELECT class=""  
					id="securityQuestion3" name="securityQuestion3" onchange="changeQuestion3('securityQuestion3');">
					<OPTION value="" selected>- Select One -</OPTION>
					<c:forEach var="beanItem" items="${registerNycIdBean.moSecurityQuestion3List}">
						<OPTION value="${beanItem.miquestionId}" title = "${beanItem.msQuestionText}" 
						<c:if test="${beanItem.miquestionId==registerNycIdBean.miSecurityQuestion3Id}"> selected</c:if>>${beanItem.msQuestionText}</OPTION>
					</c:forEach>
					<c:if test=""></c:if>
				</SELECT><label class="DMCError" id="securityQuestion3Div"></label> 
				<span class="error" style="width:auto;"></span>
				</SPAN>
				
			</DIV>
			
			<DIV class=row><SPAN class=label>Your Answer:</SPAN>
				<SPAN
					class=formfield> <INPUT class=input type=text id="answer3"
					name="answer3" maxlength="255" value="${registerNycIdBean.msAnswer3}"><label
					class="DMCError" id="answer3Div"></label>
				</SPAN>
				<span class="error"></span>
			</DIV>
			
			<DIV class=buttonholder><INPUT class=graybtutton value=Cancel type=button title="Cancel" onclick="goToHome();"><INPUT class=button value="Save" id="saveButton" name="saveButton" title="Save" type="submit"></DIV>
			<!-- Form Data Ends -->
		</DIV>
		<!-- Body Container Ends -->
	<%}else{ %>
		<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
	<%} %>
</form>	

<script type="text/javascript">
var contextPathVariable = "<%=request.getContextPath()%>";


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

//Below function returns true if the field is blank/null and vice-versa
function isEmpty(fieldId){
	var returnFlag = true;
	if(document.getElementById(fieldId)!=null && trim(document.getElementById(fieldId).value)!=""){
		returnFlag = false;
	}
	return returnFlag;
}

//Below function removes left and right spaces from string	
function trim(stringToTrim){
	return stringToTrim.replace(/^\s+|\s+$/g,"");
}

/*function pageGreyOut(){
	$.blockUI({
    message: "<img src='../framework/skins/hhsa/images/loadingBlue.gif' />",
    overlayCSS: { opacity : 0.8}
    });
}*/
    
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
	}, "! No two security answers may be the same.Please re-enter a unique answer "); 
</Script>
