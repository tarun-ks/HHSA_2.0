<%@page import="javax.print.attribute.standard.DocumentName"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.nyc.hhs.model.WorkflowDetails"%>
<%@page import="com.nyc.hhs.model.OrgNameChangeBean"%>
<%@page import="com.nyc.hhs.model.ApplicationAuditBean"%>
<%@page import="com.nyc.hhs.constants.ApplicationConstants"%>
<%@page import="com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@page import="com.nyc.hhs.util.PropertyLoader"%>
<%@page import="com.nyc.hhs.service.filenetmanager.p8constants.P8Constants"%>
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="st" uri="/WEB-INF/tld/sessiongrid-taglib.tld"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page import="com.nyc.hhs.frameworks.sessiongrid.*"%>
<%@ page import="java.util.*"%>
<%@page import="com.nyc.hhs.daomanager.*"%>
<%@page import="java.net.*"%>
<%@page import="java.io.*"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib prefix="render" uri="http://www.bea.com/servers/portal/tags/netuix/render" %>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript" src="../framework/skeletons/hhsa/js/TaskDetailsOrganizationLegalNameChange.js"></script>

<portlet:defineObjects />

<% 
String lsLoginUserId=""; 
String lsTaskAssignedToUserId="";

if(request.getAttribute("loginUserID")!=null){
	lsLoginUserId=(String)request.getAttribute("loginUserID");
}
if(request.getAttribute("lsTaskAssignedToUserId")!=null){
	lsTaskAssignedToUserId=(String)request.getAttribute("lsTaskAssignedToUserId");
}
boolean lbSecurityCheck=CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.TA_S086_PAGE, request.getSession());
	boolean lbIsValidUser=true;
	String FilePath = "";
	if(request.getAttribute("jsptoInclude")!=null){
		FilePath=(String)request.getAttribute("jsptoInclude");
	}
	if(request.getAttribute("validUser")!=null)
	{
		lbIsValidUser=(Boolean)request.getAttribute("validUser");
	}
	String lsValidUser=new Boolean(lbIsValidUser).toString();
	
	boolean lbFinishTask = (Boolean)request.getAttribute("finishTaskStatus");
	String lsFinish= new Boolean(lbFinishTask).toString();
	boolean lbIsAllVerified = (Boolean)request.getAttribute("isAllVerified");
	String lsIsAllVerified= new Boolean(lbIsAllVerified).toString();
	String lsReadOnlyPage = (String)request.getParameter("isTaskLock");
	String lstaskStatus = (String)request.getParameter("taskStatus");
	String providerOldComments=(String)request.getAttribute("providerComments");
	String lsExceptionMsg=(String)request.getAttribute(ApplicationConstants.ERROR_MESSAGE);
	if(lsExceptionMsg==null)
	{
		lsExceptionMsg="null";
	}

	if(providerOldComments==null)
	{
		providerOldComments="";
	}
	if(lstaskStatus==null)
	{
		lstaskStatus="";
	}
	String lsIntialCommentValue=P8Constants.PROPERTY_PE_COMMENTS;
	String taskId = (String)request.getAttribute("taskId");
	String appId = (String)request.getAttribute("appId");
	String providerId = (String)request.getAttribute("providerId");
	
	String lsFromTaskManagementPage= (String)request.getParameter("fromTaskManagementPage");
	if(lsFromTaskManagementPage==null)
	{
		lsFromTaskManagementPage="";
	}
	
	String lsfromBRAppPage= (String)request.getParameter("fromBRAppPage");
	if(lsfromBRAppPage==null)
	{
		lsfromBRAppPage="";
	}
	
	String lsM39 = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE, "M39");
	String lsM40 = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE, "M40");
	String lsM41 = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE, "M41");
	String lsM57 = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE, "M57");
	String lsP05=PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE,"P05");
%>

<%String lsFilePath = "";
	if(renderRequest.getAttribute("fileToInclude") != null){
		lsFilePath = (String) renderRequest.getAttribute("fileToInclude");
	}
 %>

<form method="post" name="myform" action="<portlet:actionURL/>">
<%
	String lsManagerTask= (String)request.getParameter("isManagerTask");
%> 

<jsp:useBean
	id="filenetWorkItemDetails" class="com.nyc.hhs.model.Task"
	scope="request"></jsp:useBean>
<jsp:useBean
	id="orgDetails" class="com.nyc.hhs.model.OrgNameChangeBean"
	scope="request"></jsp:useBean>
	
	<%if(orgDetails.getLsCurrentOrgLegalName()==null){
		orgDetails.setLsCurrentOrgLegalName("");
	} 
	if(orgDetails.getLsModifiedBy()==null){
		orgDetails.setLsModifiedBy("");
	}
	if(orgDetails.getLsProposesOrgLegalName()==null){
		orgDetails.setLsProposesOrgLegalName("");
	}
	if(orgDetails.getLsReasonsComments()==null){
		orgDetails.setLsReasonsComments("");
	}
	if(orgDetails.getLsModifiedDate()==null){
		orgDetails.setLsModifiedDate("");
	}
	%>
<h2>Task Details: <span> <%=filenetWorkItemDetails.getTaskName()%></span></h2>

<div class="linkReturnValut" ><a href="javascript:;" title="Return" onclick="javascript:returnInbox('<%=taskId%>')">Return</a></div>

<div id="error" class='error' style="display:none"></div>
<%if(lbSecurityCheck){%>

	<div class="container">
	<div class="complianceWrapper">
	<!--Filter and Reassign section starts -->
		<input type="hidden" name="reassigntouserText" value="" id="reassigntouserText" />
	<div class="tasktopfilter taskButtons">
		<div class="taskfilter"><select name="userReassignTask"
			id="userList" onchange="checkUserTask('<%=lsManagerTask%>')">
			<option selected="selected"></option>
				<c:forEach items="${userMap}" var="user">
	       			<option value="${user.key}">${user.value}</option>
	    		</c:forEach>
		</select> <input type="button" class="graybtutton" id="reassign"
			value="Reassign Task" title="Reassign Task" onclick="javascript:reassignTask()" />
		</div>
		
		<div class="taskreassign"> <select id="finishtaskchild" name="finishtaskchild" onchange="enableFinishButton()">
			<c:forEach var="category" items='<%=request.getAttribute("withDrawalDetails") %>'>
				<option value="<c:out value="${category}"/>"><c:out value="${category}"/></option>
			</c:forEach>
		</select><input type="button" class="button" id="finish" value="Finish Task"
			name="finish" title="Finish Task" onclick='finishCall()' />
		</div>
	</div>
	<!--Filter and Reassign section ends --> 
	<%
		List<WorkflowDetails> assoDocList = (List) request.getAttribute("associatedDocumentsList");
	%> 

	<!-- Left Column Start -->
	<div class="Column1">
		<h4>Provider Details</h4>
		<label>Provider Name:</label>
		<div><%=filenetWorkItemDetails.getProviderName()%></div>
		
		<label>Submitted By:</label>
		
		<div><%=filenetWorkItemDetails.getSubmittedby()%></div>
		
		<label>Email Address:</label>
		<div class='breakAll'><a href= 'mailto:<%=filenetWorkItemDetails.getEmailAdd()%>'><%=filenetWorkItemDetails.getEmailAdd()%></a></div>
		
		<label>Phone #:</label>
		<div><span class="printerFriendlyPhoneTask"><%=filenetWorkItemDetails.getPhone()%></span></div>
		
		<label>Date Submitted:</label>
		<div><%=filenetWorkItemDetails.getDateSubmitted()%></div>
		
		
		<label>Provider Status:</label>
		<div><%=filenetWorkItemDetails.getCurrentProvStatus()%></div>
	
		<div></div>

		<h4>Task Details</h4>
		<label>Task Name:</label>
		<div><%=filenetWorkItemDetails.getTaskName()%></div>
	
		<label>Assigned To:</label>
		<div><%=filenetWorkItemDetails.getAssignedTo()%></div>
		
		<label>Date Assigned:</label>
		<div><%=filenetWorkItemDetails.getDateAssigned()%></div>
		
		<label>Last Modified:</label>
		<div><%=filenetWorkItemDetails.getMsDateLastModified()%></div>
	
	</div>
	<!-- Left Column End --> 

	<!-- Center Column Start -->


	<!-- Organization Details Section Start -->
	<div id="linkDiv" class="formcontainer newColumn" contentEditable='false' unselectable='true' >
		<div class="Taskslinks" id='Taskslinks' style='display:none'>	
			<h2 style="padding:0">Organization Legal Name Change: </h2>
		    <div class="hr"></div>
		    <div class="formcontainer">
			 	<div class="row">
			    	<span class="label">Current Organization Legal Name:</span>
			      	<span class="formfield"><%=orgDetails.getLsCurrentOrgLegalName() %></span>
			 	</div>
			 	<div class="row">
			    	<span class="label" >Proposed Organization Legal Name:</span>
			      	<span class="formfield"><%=orgDetails.getLsProposesOrgLegalName() %></span>
			    </div>
			    <div class="row">
			    	<span class="label" style="height:120px;"><br><br>Reason for updating your Organization Legal Name:</span>
			      	<span class="formfield"><%=orgDetails.getLsReasonsComments() %></span>
			 	</div>
			</div>
	   </div>
	   <div class="TasksForms" id='TasksForms' style='display:none'>	
	   </div>
	</div>

	<div>&nbsp;</div>

	<div class='clear'>&nbsp;</div>

	<!-- Last Column Start -->
	<div id="tabs">
		<ul>
			<li><a href="#tabs-1" title="Related Information">Related Information</a></li>
			<li><a href="#tabs-2" title="Add Section Comments">Add Comments</a></li>
			<li><a href="#tabs-3" title="View Task History">View Task History</a></li>
			 <li></li>
			 
		 	<div class='floatRht'> 	
				<input id="save" name="save" type="button" class="graybtutton" value="Save" title="Save" onclick="saveComments()"/>
			</div>
		</ul>


		<div id="tabs-1" >
		<div class="tabularWrapper">
 		<!-- Grid Starts -->
        <div>
        	<table cellspacing="0" cellpadding="0"  class="grid">
                <tr>
                  <th>Item Name</th>
                  <th>Modified</th>
                  <th>Last Modified By</th>
                  
                </tr>
                <tr>
                	<td><a name=taskArrow id='arrowreq' class=taskNormal href="#" OnClick="javascript: requestSubmit()" title="Organization Name Change Request" >Organization Name Change Request</a></td>
                    <td><%=orgDetails.getLsModifiedDate()%></td>
                    <td><%=orgDetails.getLsModifiedBy()%></td>
                   
                </tr>
                <tr class="alternate">
                	<td><a name=taskArrow id='arrowbasic' class=taskNormal href="#" onclick="previewUrl()" title="Organization Information - Basics">Organization Information - Basics</a></td>
                    <td><%=orgDetails.getLsModifiedDate()%></td>
                    <td><%=orgDetails.getLsModifiedBy()%></td>
                </tr>
                
            </table>
		</div>
        <!-- Grid Ends -->
		</div>
		</div>
		<!-- last Column End -->


		<div id="tabs-2">
		<label class="required"> * Do not save incomplete comments.<br>
					 Previously saved comments can be viewed in the 'View Task History' tab.</label>
			<div class="taskComments"><b>Enter any public provider comment</b>
				<div>Click the 'Save' button to save your comments</div>
				<% if((request.getAttribute("providerComments")==null) || (request.getAttribute("providerComments").equals(""))) {  %>
					<textarea name="publicCommentArea" id="publicCommentArea" class='taskCommentsTxtarea' onkeyup="setMaxLength(this,300)" onkeypress="setMaxLength(this,300)" onchange="setChangeFlag()" cols="48"
					rows="5"
					onFocus="if (this.value == this.defaultValue) {this.value = '';}"></textarea>
				<% }else{ %> <textarea name="publicCommentArea"  class='taskCommentsTxtarea'  id="publicCommentArea" onkeyup="setMaxLength(this,300)" onkeypress="setMaxLength(this,300)" onchange="setChangeFlag()"
					cols="46" rows="5"><%=request.getAttribute("providerComments")%></textarea>
				<%}%>
			</div>

			<div class="taskComments commentNoborder"><b>Enter any
			internal HHS Accelerator comment</b>
			<div>Click the 'Save' button to save your comments</div>
			<textarea name="internalCommentArea" id="internalCommentArea"  class='taskCommentsTxtarea' onkeyup="setMaxLength(this,300)" onkeypress="setMaxLength(this,300)"  onchange="setChangeFlag()" cols="48"
				rows="5"
				onFocus="if (this.value == this.defaultValue) {this.value = '';}"></textarea>
			</div>
		</div>

		<div id="tabs-3">
		<!-- Grid Starts -->
		<div class="tabularWrapper" id="relatedInfoGrid"><st:table
			objectName="taskHistoryList" gridName="relatedinfodocs" cssClass="heading"
			alternateCss1="evenRows" alternateCss2="oddRows">
		
			<st:property headingName="Task" columnName="msEntityIdentifier"
				align="center" size="25%" />
		
			<st:property headingName="Action" columnName="msEventname" align="center"
				size="15%" />
			<st:property headingName="Detail" columnName="msData"
							align="right" size="30%">
			<st:extension decoratorClass="com.nyc.hhs.frameworks.sessiongrid.TaskDetailExtension" />
			</st:property>
				<st:property headingName="User" columnName="msUserid" align="left"
				size="10%" />
			<st:property headingName="Date/Time" columnName="msDate"
				align="right" size="20%" />
		
		</st:table></div>
		<!-- Grid Ends -->
		</div>


	<input type="hidden" name="taskid" value="<%=taskId%>"> <input
		type="hidden" name="appId" value="<%=appId%>"> <input
		type="hidden" name="taskName"
		value="<%=filenetWorkItemDetails.getTaskName()%>"> 
		<textarea style="display:none;" name="providerCommentsOld" id="providerCommentsOld"><%=providerOldComments%></textarea>
		<input	type="hidden" name="isManagerTask" 	value="<%=lsManagerTask%>">	
		</div>
	
	<!-- 	
	<div class="buttonholder">
		<input id="save" name="save" type="button" class="graybtutton" value="Save" title="Save" onclick="saveComments()"/>
	</div>  -->
	
	</div>
	
	</div>
	<% } else {%>
		<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
	<%} %>
</form>

<style type="text/css">
	<!--
	.Column1{
		min-height: 570px;
	}
	.container .container{
		padding: 0 4px;
	}
	.container{
		padding: 0 0 0 8px;
	}
	.Column2{
		width: 690px;
		height: 551px;
		min-height: 551px !important;
	}
	h2{
		width: 90%;
	}
	.linkReturnValut{
		margin-top: 10px;
	}
	-->
</style>

<script type="text/javascript">	
var ischange = false;
var lastData = null;
/**
 * This Method called on Page loading.It enable or disable buttons and display error or sucess message on based of conditions.
 **/	
	$(document).ready(function() {
	document.getElementById("reassign").disabled= true;
	document.getElementById("finish").disabled= true;
	if("null" != '<%= request.getAttribute("message")%>'){
		$(".error").html('<%= request.getAttribute("message")%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('error', this)\" />");
		$(".error").addClass('passed');
		$(".error").show();
		<%request.removeAttribute("message");%>	
		}
	    document.getElementById("finishtaskchild").value = '<%=lstaskStatus%>';
/**
 *  Enable  finish button if finish flag is true else disable.
 **/		
	if('<%=lsFinish%>'=="true"){ 
		//document.getElementById("finish").disabled= false;
		document.getElementById("finishtaskchild").disabled= false;
	}
	else{
		//document.getElementById("finish").disabled= true;
		document.getElementById("finishtaskchild").disabled= true;
	 }
/**
 * If user is not valid display error message.
 **/	 
	 if('<%=lsValidUser%>'=="false")
	 {
	 	document.getElementById('error').innerHTML = '<%=lsM57%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('error', this)\" />";
	 	$(".error").addClass('failed');
		var element = document.getElementById('error');
		element.style.display ="block";
	 
	 }
/**
 * If Task is locked(Tasklock flag is true) make page readonly.
 **/	 
	if('<%=lsReadOnlyPage%>'=="true")
	 {
		document.getElementById("finishtaskchild").value = '<%=lstaskStatus%>';
		document.getElementById("finish").disabled= true;
		document.getElementById("reassign").disabled= true;
		document.getElementById("publicCommentArea").disabled= true;
		document.getElementById("internalCommentArea").disabled= true;
		document.getElementById("save").disabled= true;
		document.getElementById("userList").disabled= true;
		document.getElementById("finishtaskchild").disabled= true;
	  	document.getElementById("relatedInfoGrid").disabled= true;
	 }
/**
 * If page opened from TaskManagement and Task is not assigned to same user make page read only except Reassign action.
 **/
	 if('<%=lsFromTaskManagementPage%>' == "true"  && ('<%=lsLoginUserId%>'!='<%=lsTaskAssignedToUserId%>') )
	 {
		  document.getElementById("finishtaskchild").value = '<%=lstaskStatus%>';
		  document.getElementById("finish").disabled= true;
		  document.getElementById("publicCommentArea").disabled= true;
		  document.getElementById("internalCommentArea").disabled= true;
		  document.getElementById("save").disabled= true;
	   	  document.getElementById("finishtaskchild").disabled= true;
	 } 
	 
	 $(".emptyClass li a").click(function(e) {
	        
         var $self=$(this);
         var $form = $("#reassign").closest('form');
         
       /*  var isSame = false;
         data = $form.serializeArray();
         if(lastData != null){
               if($(lastData).compare($(data))){
                     isSame = true;
               }
         }*/
       
         if(ischange){
               e.preventDefault();
               $('<div id="dialogBox"></div>').appendTo('body')
					.html('<div><h6>You have unsaved data. <br />If you would like to leave this screen without saving your data, click <b>OK</b>. <br/>If you would like to save your data, click <b>Cancel</b> and save your data.</h6></div>')
               .dialog({
                     modal: true, title: 'Unsaved Data', zIndex: 10000, autoOpen: true,
                     width: 'auto', modal: true, resizable: false, draggable:false,
                     dialogClass: 'dialogButtons',
                     buttons: {
                           OK: function () {
                                 document.location = $self.attr('href');
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
               return false;
         }
   });
	
});

</script>
<script type="text/javascript">
/**
 * Check if current Task is manager task and user is manager or not.If user is not manager display error message and disable Reassign button
 **/
function checkUserTask(isManagerStep)
{
	var userName=document.getElementById("userList").value;
	if(userName!=null && userName!="")
	{
		document.getElementById("reassign").disabled= false;
	}
	else{
		document.getElementById("reassign").disabled= true;
	}
	if(isManagerStep=="true")
	{
		if(userName.split("|")[0]!="manager")
		{
			
		 document.getElementById("userList").value="";
		 document.getElementById('error').innerHTML = '<%=lsM39%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('error', this)\" />";
		 $(".error").addClass('failed');
		 var element = document.getElementById('error');
		 element.style.display ="block";
		 document.getElementById("reassign").disabled= true;
		 }
		 else
		 {
		 	document.getElementById("reassign").disabled= false;
		 	document.getElementById('managerTask').innerHTML='';
		}
	
	}

}
/**
 * Submit form on click on Save button.
 **/
function saveComments(){
	if('<%=lbSecurityCheck%>'){
		document.forms[0].action=document.forms[0].action+'&next_action=saveCommentsOrStatus';
		document.forms[0].submit();
	}
}

		/**
		 * Enable Finish Button on dropdown select.
		 **/
	function enableFinishButton()
	{
		if(document.getElementById("finishtaskchild").value ==''){
			document.getElementById("finish").disabled= true;
		}
		else
		{
			document.getElementById("finish").disabled= false;
		}
	}
/**
 * Submit form and redirect page to Inbox page on click on return link.
 **/
function returnInbox(taskId){
	var internalComment=document.getElementById('internalCommentArea').value;
	var publicComment= document.getElementById('publicCommentArea').value;
	if(ischange){
		 $('<div id="dialogBox"></div>').appendTo('body')
			.html('<div><h6>You have unsaved data. <br />If you would like to leave this screen without saving your data, click <b>OK</b>. <br/>If you would like to save your data, click <b>Cancel</b> and save your data.</h6></div>')
     .dialog({
           modal: true, title: 'Unsaved Data', zIndex: 10000, autoOpen: true,
           width: 'auto', modal: true, resizable: false, draggable:false,
           dialogClass: 'dialogButtons',
           buttons: {
                 OK: function () {
              	       
                	 document.forms[0].action="<portlet:renderURL/>"+"&returninbox=inbox"+'&taskUnlock='+taskId+'';
         			document.forms[0].submit();
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
	else{
		document.forms[0].action="<portlet:renderURL/>"+"&returninbox=inbox"+'&taskUnlock='+taskId+'';
		document.forms[0].submit();
	}
}


function submitUrl(finalurl){
	document.forms[0].action="<portlet:actionURL/>"+"&next_action=readUrl"+'&finalurl='+finalurl+'';
	document.forms[0].submit();
}
/**
 * Submit form on click on reassign button.
 **/
function reassignTask(){
	if('<%=lbSecurityCheck%>'){
		var selectvalue = document.getElementById("userList").value.split('|')[1];
		document.getElementById('reassigntouserText').value=selectvalue;
		if(selectvalue!="")
		{
			pageGreyOut();
			document.myform.action=document.myform.action+'&reassignTask=reassignChildTask'+'&next_action=assignTask';
			document.myform.submit();
		}
	}

}
String.prototype.trim = function () {
    return this.replace(/^\s*/, "").replace(/\s*$/, "");
}
/**
 * It Display Organization Legal name change form on this page
 **/
function previewUrl(){
	document.getElementById("arrowreq").className = "taskNormal";
	document.getElementById("arrowbasic").className = "taskSelected";
 	document.getElementById("TasksForms").style.display="block";
	document.getElementById("Taskslinks").style.display="none"
	var url="<render:standalonePortletUrl portletUri='/portlet/application/businessapplication/businessapplication.portlet'><render:param name='section' value='basics' /><render:param name='subsection'  value='questions' /><render:param name='next_action'  value='showquestion' /><render:param name='cityUserSearchProviderId'  value='<%=providerId%>' /><render:param name='removeNavigator'  value='true' /><render:param name='removeMenu'  value='true' /><render:param name='needPrintableView' value='true' /></render:standalonePortletUrl>";
	var target="TasksForms";       
    clearTimeout(window.ht);
    window.ht = setTimeout(function(){
        var div = document.getElementById(target);
        div.innerHTML = '<iframe style="width:100%; height:600px; display:block;"  src="' + url + '" />';
        },20);      
    }
/**
 * Submit form on click on finish button or display error message if no provider comments are entered.
 **/
function finishCall(){
	 document.getElementById("finish").disabled= true;
	if('<%=lbSecurityCheck%>'){
		 pageGreyOut();
		 document.myform.action=document.myform.action+'&next_action=finishchild';
		 document.myform.submit();
	}
}

function setChangeFlag(){
	ischange = true;
}
</script>

