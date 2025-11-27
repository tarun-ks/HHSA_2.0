<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.nyc.hhs.model.FaqFormBean"%>
<%@page import="com.nyc.hhs.constants.ApplicationConstants,org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="javax.portlet.*"%>
<%@ page import="com.nyc.hhs.frameworks.grid.*"%>

<%@ page errorPage="/error/errorpage.jsp" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="com.nyc.hhs.frameworks.grid.*,com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<portlet:defineObjects />

<script type="text/javascript" src="../js/faq.js"></script>
<script type="text/javascript" src="../js/richtext.js"></script>
<script type="text/javascript">
	var action = "";
	var url="";
	var isDeleteQuestion = false;
</script>
<style type="text/css">
	#header_maintenance{
	   background-color: #1569B2!important;
	   border-color: #1569B2!important;;
	   color: #ffffff!important; 
	}
	.input{
		font-family: Verdana;
		color: #333333;
		font-size: 12px;
	}
	#header_maintenance{
	   background-color: #1569B2!important;
	   border-color: #1569B2!important;;
	   color: #ffffff!important; 
	}
	.alert-box-delete {
		background: none repeat scroll 0 0 #FFFFFF;
		display: none;
		position: fixed;
		margin-left: 20%;
		top: 25%;
		width: 30%;
		z-index: 1001;
	}
	#myform table{
		border: none;
	}
	.faq-unsaved-box {
		background: none repeat scroll 0 0 #FFFFFF;
		display: none;
		/*height: 268px;*/
		position: fixed;
		margin-left: 20%;
		top: 25%;
		width: 30%;
		z-index: 1001;
	}
</style>

<script>
initRTE("../framework/skins/hhsa/images/richtextimage/", "../framework/skins/hhsa/css/", "rte");
</script>

<form id="myform" action="<portlet:actionURL/>" method ="post" name="myform" >
	<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.FQ_S131_PAGE, request.getSession())) {%>
	  	<div class=''  id="errorDiv"></div>
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
	   	<table cellspacing="0" width="100%" cellpadding="0">
	    	<tr>
		        <td>
		        <h2>Help Topic: <input id="helpTopic" name="linkValue" maxlength ="60" style='width:525px; font-size:16px; color:#5077AA;'  value="<%=StringEscapeUtils.escapeHtml((String)request.getAttribute("linkValue"))%>" onchange="setChangeFlag()" /></h2>
		       
		        </td>
		        <td class='floatLft'>
		        	 <div class="taskButtons">
		         		<input name="deleteButton" type="button" class='remove' title="Delete Topic" value="Delete Topic"  onclick="javascript:deleteRequest()"/>
		            </div>
		        </td>
		        <td class="linkReturnVault alignRht">
		        <td class="linkReturnVault alignRht"><a href="javascript:void(0);" onclick = "displayAlertPopUp();">Return</a></td>
	      </tr>
	    </table>
	    <div id="topicreqd" class='row'><span class="error"></span></div>        
		<div class='row'>
			<h3>Select a Question:</h3>   
			<div>
				<select style='width: 99%;' id = "qusList" name="quesList" onchange="fillQuestions();">
					<option value="onlyTopicUpdate">None</option>
					<option value="">ADD NEW QUESTION</option>
					<c:forEach var="topic" items="${topicList}">
		            	<option value="<c:out value="${topic.miQuestionId}"/>-<c:out value="${topic.msAnswer}"/>"><c:out value="${topic.msQuestion}"/></option>
					</c:forEach>
				</select>
				 	</div> 
		</div>
				
		<div class='row'>&nbsp;</div>
	    <div class="row">
	    	<h3>Question:</h3>
	        <input id="selectedQuestion" name="selectedQuestion" type="text" value="" style='width: 99%;' maxlength="90" onchange="setChangeFlag()" />
	    </div>
	            
		<div id="questionreqd" class='row'><span class="error"></span></div>
	    <div class="row" >
		    <h3>Answer:</h3>
                <div style="width:930px" >
                    <script language="JavaScript" type="text/javascript">
                    	setChangeFlag();
		              	writeRichText('rte1','', 925, 200, true, false);
					</script>
				</div>	
		    <input type="hidden" id="selectedAnswer" name="selectedAnswer" value="" />

<!--  	        <textarea id="selectedAnswer" name="selectedAnswer" cols="" rows="" style='width: 99%;' class="input" value="" maxlength="250" onchange="setChangeFlag()" ></textarea> -->
	    </div>
	    <div id="answerreqd" class='row'><span class="error"></span></div>
	    <div class="buttonholder">
	    	<input  id="deletebutton" class="graybtutton" name="deletebutton" type="button" style="display:none;" value="Delete Question" onclick="javascript:deletesQuestion();"/>
	        <input name="savebutton" type="button" value="Save" title="Save" onclick="javascript:formsubmit();"/>
	    </div>
	        
		<div class="overlay"></div>
		<div class="alert-box-delete">
			<div class="content">
				<div id="newTabs" class='wizardTabs'>
					<div id = "DeleteConfirmationTitle" class="tabularCustomHead">
					</div>
					<div id="deleteDiv">
						<div id = "ConfirmMessagediv" class="pad6 clear promptActionMsg">
						</div>
						<div class="buttonholder txtCenter">
							<input type="button" class="graybtutton exit-panel" title="No" value="No" />
							<input type="button" class="button" id="deleteDoc" title="Yes" onclick='proceed();' value="Yes" />
						</div>
					</div>
				</div>
			</div>
			<a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
		</div>
		<div class="faq-unsaved-box">
			<div class="content">
		  		<div id="newTabs" class='wizardTabs'>
					<div id = "UnsaveConfirmationTitle" class="tabularCustomHead"></div>
					<div id="deleteDiv">
					    <div id = "UnsaveMessagediv" class="pad6 clear promptActionMsg"></div>
					    <div class="buttonholder txtCenter" style="padding-right: 4px;">
					     <input type="button" class="graybtutton exit-panel"  title="Cancel" value="Cancel" onclick="closePopUp();" />
					    <input type="button" class="button" id="deleteDoc" title="Ok" onclick="returnToMain();" value="OK" />
				    </div>
				    </div>
				</div>
		  	</div>
		  	<a  href="javascript:void(0);" class="exit-panel" title='Close'>&nbsp;</a>
		</div> 
	
		<script>
	 		onload('<portlet:actionURL/>');
		</script>

	<%}else{ %>
  		<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
  	<%} %>
</form>

<script type="text/javascript">
var isFieldchange = false;
var contextPathVariable = "<%=request.getContextPath()%>";
	// ready function executes the script after page loading
$(document).ready(function(){ 
	action = document.myform.action;
	var pageW = $(document).width();
	var pageH = $(document).height();
	if(document.getElementById("qusList").value=="onlyTopicUpdate"){
	document.getElementById("selectedQuestion").disabled=true;
	    disableLink = true;
		if (navigator.userAgent.indexOf("MSIE")==-1 ) 
	   { // FF
		document.getElementById('rte1').contentDocument.getElementsByTagName('body')[0].style.display="none";
	   }
	   else 
	   { // IE
		    document.getElementById('rte1').contentWindow.document.getElementsByTagName('body').disabled=true;
	 
	   }
	//document.getElementById('rte1').contentWindow.document.getElementsByTagName('body')[0].style.display="none";
}else{
    disableLink = false;
	document.getElementById("selectedQuestion").disabled=false;
		if (navigator.userAgent.indexOf("MSIE")==-1) 
	   { // FF
		document.getElementById('rte1').contentDocument.getElementsByTagName('body')[0].style.display="block";
	   }
	   else 
	   { // IE
		    document.getElementById('rte1').contentWindow.document.getElementsByTagName('body')[0].disabled=false;
	   }
}
$('textarea[maxlength]').keyup(function(){  
//get the limit from maxlength attribute  
var limit = parseInt($(this).attr('maxlength'));  
//get the current text inside the textarea  
var text = $(this).val();  
//count the number of characters in the text  
var chars = text.length;  
//check if there are more characters then allowed  
if(chars > limit){  
    //and if there are use substr to get the text before the limit  
  	var new_text = text.substr(0, limit);  
    //and change the current text with the new text  
    $(this).val(new_text);  
 	}    
 }); 

		
$(".exit-panel").click(function(){					
	$(".alert-box-delete").hide();
	$(".faq-unsaved-box").hide();
	$(".overlay").hide();
	$("#transactionStatusDiv").hide();
	
});
});	
	
</script>