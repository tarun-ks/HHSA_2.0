<%@page import="java.util.ArrayList"%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="com.nyc.hhs.frameworks.grid.*,com.nyc.hhs.constants.*, com.nyc.hhs.model.ContactUsBean, java.util.ArrayList, java.util.Iterator"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@page import="javax.portlet.RenderRequest"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>

<div class="tabularCustomHead">Contact HHS Accelerator</div>
<% if (request.getAttribute("workflowfail") != null) { %>
	<script>
		$("#contactMessagediv").html(" This request could not be completed. Please try again in a few minutes.");
		$("#contactMessagediv").addClass("failed");
		$("#contactMessagediv").show();
	</script>
<%} %>
<form action="" method="post" name="contactusform" id="contactusform">   
<div class="tabularContainer"> 
	<div id="contactMessagediv"></div>
	<p>Use this form to contact the HHS Accelerator team with any questions or feedback.</p>  
	<p><label class='required'>*</label>Indicates required fields</p>
	<%
	String lsTransactionMsg = "";
	if (null!=request.getAttribute("transactionMessage")){
		lsTransactionMsg = (String)request.getAttribute("transactionMessage");
	}
	if(( null != request.getAttribute("transactionStatus") ) && "failed".equalsIgnoreCase((String)request.getAttribute("transactionStatus"))){%>
		<div id="transactionStatusDiv" class="failed" style="display:block" ><%=lsTransactionMsg%> </div>	
	<%}%>
	    
	<div class="formcontainer">
		<div class="row">            
			<span class="label"><label class='required'>*</label> Topic:</span> 
	        <span class="formfield">
	        	<select name="selecttopic" class='textarea'  id = "selecttopic" onchange="setChangeFlag();">
	            		<option value=""></option>
	                		<% ArrayList<ContactUsBean> topicList= (ArrayList<ContactUsBean>)request.getAttribute("loTopicList");
	                		if(null != topicList){
	                			Iterator<ContactUsBean> iter = topicList.iterator();
	                			String selected="";
    	                		String helpCategory = (String)request.getAttribute("helpCategory");
	                			while(iter.hasNext()){
	                				ContactUsBean contactUs = iter.next();
	                				String topic = contactUs.getMsTopic();
	                				String topicId = contactUs.getMsTopicID().toString();
	                				if(topic.equalsIgnoreCase(helpCategory)){
	                					selected = "selected";
	                				}
	                				else{
	                					selected = "";
	                				}
	                		%>	
	                	<option value="<%=topicId%>"<%=selected%>><c:out value="<%=topic%>"/></option> 	
                		<%	}
                			}
                		%>
				</select>
			</span> 
	        <span class="error"></span> 
		</div>
		<div class="row"> 
	    	<span class="label" style="height:100px;"><label class='required'>*</label>Question/Feedback:</span> 
	        <span class="formfield">
	        	<textarea name="commentbox" class="termsTextarea input proposalConfigDrpdwn" id = "commentbox" cols="25" rows="4" onchange="setChangeFlag();"></textarea>
			</span> 
			<span class="error"></span> 
		</div>
	   	<div class="row"> 
			<span class="label"><label class='required'>*</label>Preferred Method of Contact:</span>
	        <span class="formfield">
	        	<input name="contactmode" id="contactmodePhone" type="radio" value="phone" onclick="setChangeFlag();" />
	            	<label for='contactmodePhone'>Phone</label>
	            	 &nbsp; &nbsp;
	            <input name="contactmode" id= "contactmodeEmail" type="radio" value="email" onclick="setChangeFlag();" />
	            	<label for='contactmodeEmail'>Email</label>
	       </span>
	       <span class="error"></span> 
		</div>
	    <br>
	    <div class="buttonholder">
	    	<input type="button" class="graybtutton" id="helpButton" title="&lt&lt Back to Help" value="&lt&lt Back to Help" onclick = "displayAlertPopUp();"  />
	        <input type="button" class="graybtutton" id="cancelbutton" value="Cancel" title="Cancel" onclick = "closePopUp();"  />
	        <input type="submit"  id="contactsubmit" value="Submit" title="Submit" onclick="submitClick();"/>
		</div>
	</div>
	<a href="javascript:void(0);" class="close-panel" title='Close' onclick="displayAlertPopUp();"></a>	 
</div>
    
<div class="overlay"></div>
<div class="overlayYesNo"></div>
  	<!-- Overlay Popup Ends -->
</form>
<script type="text/javascript"><!--
var contextPathVariable = "<%=request.getContextPath()%>";
var changeFlag = false;
$(document).ready(function() {   	
   		$("a.exit-panel").click(function(){					
			$(".alert-box-contact").hide();
			$(".overlay").hide();
		});	
		$(".exitYesNo-panel").click(function(){		
			$(".overlayYesNo").hide();
		});
});

//This function sets the maxlength of the obj passed.
//updated in R5
function setMaxLength(obj,maxlimit){
	if(($(obj).val().length + $(obj).val().split("\n").length-1)  > maxlimit){
		$(obj).val($(obj).val().substring(0, maxlimit - $(obj).val().split("\n").length + 1));
		return false;
	}
}

//This function hides the overlay page
function overlayHide()
{				
		$(".alert-box-contact").hide();
		$(".alert-box-help").hide();
		$(".overlay").hide();
}
//This function calls the ajax in case of submitting contact us page
function submitClick(){
	$("#contactusform").validate({
		rules: {
			selecttopic: {required: true},
			commentbox: {required: true, 
				maxlength: 300},
			contactmode: {required: true}
		},
		messages: {
			selecttopic: {required:"<fmt:message key='REQUIRED_FIELDS'/>"},
			commentbox: {required: "<fmt:message key='REQUIRED_FIELDS'/>", 
				maxlength: "<fmt:message key='INPUT_300_CHAR'/>"} ,
			contactmode:{required: "<fmt:message key='REQUIRED_FIELDS'/>"}
		},
		submitHandler: function(form){
			$("#contactMessagediv").html("");
			$("#contactMessagediv").addClass("");
			$("#contactMessagediv").hide();	
		    var  topicTextId = document.getElementById("selecttopic");
	        var topicName = topicTextId.options[topicTextId.selectedIndex].text;
	        var topicValue = document.getElementById("selecttopic").value;		    
	        var selectedInputfromquestion = document.getElementById("commentbox").value;	
	        var selectedContactArray = document.getElementsByName("contactmode");
	        var selectedContact = "";
	        for(var i=0; i<selectedContactArray.length; i++){
		        if(selectedContactArray[i].checked){
			        selectedContact = selectedContactArray[i].value;
			        break;
				}
	        } 
			pageGreyOut();	
	        var v_parameter="";
	    	var urlAppender = contextPathVariable+"/ContactUsServlet.jsp?action=insert&topicId="+topicValue+"&topicName="+topicName+"&selectedInputfromquestion="+selectedInputfromquestion+"&selectedContact="+selectedContact;
	        jQuery.ajax({
	        type: "POST",
	        url: urlAppender,
	        data: v_parameter,
	        success: function(e){
	        $("#contactDiv").empty();
			$("#contactDiv").html(e);
			//$.unblockUI();
			removePageGreyOut();
	           },
	           beforeSend: function(){  //function for loading wheel
	           }          
	           });
		},
		errorPlacement: function(error, element) {
		      error.appendTo(element.parent().parent().find("span.error"));
		}
	});
}
//This function trims the passed input
function trim(stringToTrim) {
	return stringToTrim.replace(/^\s+|\s+$/g,"");
}

//This function shows the msg in the contactMessagediv
function showMessage(msg){
	$("#contactMessagediv").html(msg);
	$("#contactMessagediv").addClass("failed");
	$("#contactMessagediv").show();
}

//This function set the boolean flag if any field change take place
function setChangeFlag(){
	changeFlag = true;
}

//This function displays the confirmation popup for data loss
function displayAlertPopUp(){
	if(changeFlag){
	 	displayConfirmation();
	}else {
	    proceed();
	}
}

//This function closes the popup
function closePopUp(){
if(changeFlag){
	displayConfirmation();
}else {
	overlayHide();
	}
}

//This function displays the confirmation popup for data loss
function displayConfirmation (){
    $('<div id="dialogBox"></div>').appendTo('body')
	.html('<div><h6>You have unsaved data.<div class="clear">If you would like to leave this screen without saving your data, click <b>OK</b>.</div><div class="clear">If you would like to save your data, click <b>Cancel</b> and save your data.</div></h6></div>')
	.dialog({
		modal: true, title: 'Unsaved Data', zIndex: 10000, autoOpen: true,
		width: 'auto', modal: true, resizable: false, draggable:false,
	    dialogClass: 'dialogButtons',
		buttons: {
			OK: function () {
				//Start R5: UX module, clean AutoSave Data
				deleteAutoSaveData();
				//End R5: UX module, clean AutoSave Data
				proceed();
				$(".overlayYesNo").hide();
				changeFlag = false;
				$(this).dialog("close");
			},
			Cancel: function () {
				$(this).dialog("close");
			}
		},
		close: function (event, ui) {
			$(this).remove();
		}
	});
    $("div.dialogButtons div button:nth-child(2)").find("span").addClass("graybtutton");
}

 
//This function launches the help pop up
function proceed(){
    $(".alert-box-contact").hide();
    $(".overlay").launchOverlay($(".alert-box-help"), $(".exit-panel"));
    if(document.getElementById("helpPopup")==null){
		overlayHide();
	}
}
</script>