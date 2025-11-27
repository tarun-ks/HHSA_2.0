<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page import="com.nyc.hhs.model.WorkflowDetails"%>
<%@page import="com.nyc.hhs.service.filenetmanager.p8constants.P8Constants"%>
<%@page import="com.nyc.hhs.constants.ApplicationConstants"%>
<%@page import="com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@page import="com.nyc.hhs.util.PropertyLoader"%>
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="st" uri="/WEB-INF/tld/sessiongrid-taglib.tld"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="com.nyc.hhs.frameworks.sessiongrid.*"%>
<%@ page import="java.util.*"%>
<%@page import="com.nyc.hhs.daomanager.*"%>
<%@page import="com.nyc.hhs.model.TaskQueue"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib prefix="render" uri="http://www.bea.com/servers/portal/tags/netuix/render" %>
<portlet:defineObjects/>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript" src="../framework/skeletons/hhsa/js/TaskDetailsOrganizationAccountRequest.js"></script>

<portlet:defineObjects/>
<jsp:useBean
	id="filenetWorkItemDetails" class="com.nyc.hhs.model.Task" scope="request"></jsp:useBean>

<% 

	String lsLoginUserId=""; 
	String lsTaskAssignedToUserId="";
	
	if(request.getAttribute("loginUserID")!=null){
		lsLoginUserId=(String)request.getAttribute("loginUserID");
	}
	if(request.getAttribute("lsTaskAssignedToUserId")!=null){
		lsTaskAssignedToUserId=(String)request.getAttribute("lsTaskAssignedToUserId");
	}
	boolean lbSecurityCheck=CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.TA_S085_PAGE, request.getSession());
	boolean lbIsAllVerified = (Boolean)request.getAttribute("isAllVerified");
	String lsIsAllVerified= new Boolean(lbIsAllVerified).toString();
	boolean lbIsValidUser=true;
	String appId = (String)request.getAttribute("appId");
	String providerId = (String)request.getAttribute("providerId");
	if(null==providerId)
	{
		providerId="";
	}
	
	
	if(request.getAttribute("validUser")!=null)
	{
		lbIsValidUser=(Boolean)request.getAttribute("validUser");
	}
	String lsValidUser=new Boolean(lbIsValidUser).toString();

	String lsFinish="true";
	String lsStatus= filenetWorkItemDetails.getMsProcessStatus();
	if(lsStatus==null){
		lsFinish="false";
	}
	else if(lsStatus.equals("In Review")||lsStatus.trim().equals("")){
		lsFinish="false";
	}
	String lsExceptionMsg=(String)request.getAttribute(ApplicationConstants.ERROR_MESSAGE);
	if(lsExceptionMsg==null)
	{
		lsExceptionMsg="null";
	}

	String lsReadOnlyPage = (String)request.getParameter("isTaskLock");
	
	String lsIntialCommentValue=P8Constants.PROPERTY_PE_COMMENTS;
	
	String providerOldComments=(String)request.getAttribute("providerComments");
	if(providerOldComments==null)
	{
		providerOldComments="";
	}
	String lsChildSize="";
	int childSize=0;
	if(request.getAttribute("taskItemChildListSize")!=null){
		lsChildSize=(String)request.getAttribute("taskItemChildListSize");
		childSize= Integer.parseInt(lsChildSize);
	
	}
	String lsFromTaskManagementPage= (String)request.getParameter("fromTaskManagementPage");
	if(lsFromTaskManagementPage==null)
	{
		lsFromTaskManagementPage="false";
	}
	
	String lsM39 = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE, "M39");
	String lsM40 = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE, "M40");
	String lsM57 = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE, "M57");
	String lsM41 = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE, "M41");
	String lsP05=PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE,"P05");
%>




<%
	String taskId = (String)request.getAttribute("taskId");

%>
<body>
<!-- Body Container Starts -->
<form name="myform" action="<portlet:actionURL/>" method ="post" >

	<h2>Task Details: <span><%=filenetWorkItemDetails.getTaskName()%></span></h2>
	
	<div class="linkReturnValut"><a href="javascript:;" title="Return" onclick="javascript:returnInbox('<%=taskId%>')">Return</a></div>

	<div id="error" class='error' style="display:none"></div>
	<%if(lbSecurityCheck){%>
		<div class="container">
		<div class="complianceWrapper">
		<%
		String lsManagerTask= (String)request.getParameter("isManagerTask");
		%>
		<!--Filter and Reassign section starts -->
		<div class="tasktopfilter">
	        <div class="taskfilter">
		        <input type="hidden" name="reassigntouserText" value="" id="reassigntouserText" />
			    <select id="userList" onchange="checkUserTask('<%=lsManagerTask%>')">
		            <option selected="selected"></option>
					<c:forEach items="${userMap}" var="user">
		       			<option value="${user.key}">${user.value}</option>
		    		</c:forEach>           
			    </select>
		        <input type="button" class="graybtutton" id="reassign" title="Reassign Task" value="Reassign Task" onclick="javascript:reassignTask()" />
			</div>
	        <div class="taskreassign">
	        
	        	<% if(lsFinish.equals("true")){%>
	       		<b> Business Application Status: </b><%=filenetWorkItemDetails.getMsProcessStatus()%> <%} %>
	          
	             <div class="taskreassign">
		             <select id="finishtaskchild" name="finishtaskchild" onchange="enableFinishButton()">
						<c:forEach var="category" items='<%=request.getAttribute("withDrawalDetails") %>'>
							<option value="<c:out value="${category}"/>"><c:out value="${category}"/></option>
						</c:forEach>
					 </select><input type="button" class="button" id="finish" title="Finish Task" value="Finish Task"
						name="finish" onclick='finishCall()' />
				</div>
	    	</div>
		</div>
    	<!--Filter and Reassign section ends -->

		<input type="hidden" name="taskid" value="<%=taskId%>">
		<input type="hidden" name="appId" value="<%=appId%>">


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
		<div class="formcontainer newColumn">
			<!-- Organization Details Section Start -->
			<div  id="linkDiv" class="formcontainer newColumn">
			</div>
			<script>
				onload('<portlet:actionURL/>');
			</script>

			<div>
			</div>
		</div>
		<!-- Last Column Start -->
		<div style="display:block; clear:both; overflow:hidden;">
		<div id="tabs">
			<ul>				
				<li><a href="#tabs-1" title="Add Comments">Add Comments</a></li>
                <li><a href="#tabs-2" title="View Task History">View Task History</a></li>
                 <li></li>
		               
               <div class='floatRht'> 
               		<input id="save" name="save" type="button" class="graybtutton" value="Save" title="Save" onclick="saveComments()"/>
               </div>
           	</ul>
  
			<div id="tabs-1">
			<label class="required"> * Do not save incomplete comments. The provider comments cannot be cleared - enter 'N/A' if necessary.<br>
					 Previously saved comments can be viewed in the 'View Task History' tab.</label>
				<div class="taskComments">
		            <b>Enter any public provider comment</b>
		            <div> Click the 'Save' button to save your comments</div>   
		            <% if((request.getAttribute("providerComments")==null) || (request.getAttribute("providerComments").equals(""))) {  %>
		             	<textarea id="publicCommentArea" name="publicCommentArea" class='taskCommentsTxtarea' onkeyup="setMaxLength(this,300)" onkeypress="setMaxLength(this,300)" onchange="setChangeFlag()" cols="48" rows="5" onFocus="if (this.value == this.defaultValue) {this.value = '';}" ></textarea>
		          	<% }else{ %>
		             	<textarea id="publicCommentArea" name="publicCommentArea" class='taskCommentsTxtarea' onkeyup="setMaxLength(this,300)" onkeypress="setMaxLength(this,300)" onchange="setChangeFlag()" cols="48" rows="5"><%=request.getAttribute("providerComments")%></textarea>
		         	<%}%>   
				</div>
       
       			<div class="taskComments commentNoborder">
            		<b>Enter any internal HHS Accelerator comment</b>
            		<div> Click the 'Save' button to save your comments</div>        
            		<textarea id="internalCommentArea" name="internalCommentArea" class='taskCommentsTxtarea'  onkeyup="setMaxLength(this,300)" onkeypress="setMaxLength(this,300)" onchange="setChangeFlag()" cols="48" rows="5" onFocus="if (this.value == this.defaultValue) {this.value = '';}" ></textarea>
       			</div>
			</div>

			<div id="tabs-2">
 				<!-- Grid Starts -->			 
			    <div  class="tabularWrapper">
				    <st:table objectName="taskHistoryList"  gridName="taskshistorygrid" cssClass="heading"
						alternateCss1="evenRows" alternateCss2="oddRows">
								
						<st:property headingName="Task"  columnName="msEntityIdentifier" align="center"
							size="15%" />
						
						<st:property headingName="Action"  columnName="msEventname" align="center"
							size="25%" />		
						<st:property headingName="Detail" columnName="msData"
							align="right" size="30%">
							<st:extension decoratorClass="com.nyc.hhs.frameworks.sessiongrid.TaskDetailExtension" />
						</st:property>
						<st:property headingName="User" columnName="msUserid"
							align="left" size="10%"  />
						<st:property headingName="Date/Time" columnName="msDate"
							align="right" size="20%" />
	
					</st:table>
				</div>
			    <!-- Grid Ends -->

			</div>
			<%
			 taskId = (String)request.getAttribute("taskId");
			 appId = (String)request.getAttribute("appId");
			%>
		
			<input type="hidden" name="taskid" value="<%=taskId%>">
			<input type="hidden" name="appId" value="<%=appId%>">
			<input type="hidden" name="taskName" value="<%=filenetWorkItemDetails.getTaskName()%>">
			<input	type="hidden" name="isManagerTask" 	value="<%=lsManagerTask%>">
		<textarea style="display:none;" name="providerCommentsOld" id="providerCommentsOld"><%=providerOldComments%></textarea>
			 
			
			</div>
   
		</div>
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
	previewUrl("<render:standalonePortletUrl portletUri='/portlet/accountrequestlogin/missingprofileinfo/missingprofileinformation.portlet'><render:param name='workflow' value='organizationAccountRequest'/><render:param name='orgId' value='<%=providerId%>'/><render:param name='accoutRequestmodule'  value='accoutRequestmodule#wlp_portlet_hhsweb_portal_page_missing_profile' /><render:param name='removeNavigator'  value='true' /><render:param name='removeMenu'  value='true' /></render:standalonePortletUrl>",'linkDiv');
	document.getElementById("reassign").disabled= true;
	document.getElementById("finish").disabled= true;
	if("null" != '<%= request.getAttribute("message")%>'){
		$(".error").html('<%= request.getAttribute("message")%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('error', this)\" />");
		$(".error").addClass('passed');
		$(".error").show();
		<%request.removeAttribute("message");%>	
	}
/**
 *  Enable  finish button if finish flag is true else disable.
 **/		
	if('<%=lsFinish%>'=="true"){ 
		document.getElementById("finish").disabled= false;
	}
/**
 * If user is not valid display error message.
 **/	
	 if('<%=lsValidUser%>'=="false")
	 {
	 	document.getElementById('error').innerHTML = '<%=lsM57%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('error', this)\" />";
		var element = document.getElementById('error');
	 	$(".error").addClass('failed');
		element.style.display ="block";
	 }
/**
 * If Task is locked(Tasklock flag is true) make page readonly.
 **/
	if('<%=lsReadOnlyPage%>'=="true")
	{
		document.getElementById("finish").disabled= true;
		document.getElementById("reassign").disabled= true;
		document.getElementById("publicCommentArea").disabled= true;
		document.getElementById("internalCommentArea").disabled= true;
		document.getElementById("save").disabled= true;
		document.getElementById("userList").disabled= true;
	  	  
	}
/**
 * If page opened from TaskManagement and Task is not assigned to same user make page read only except Reassign action.
 **/		 
	if('<%=lsFromTaskManagementPage%>'=="true" && ('<%=lsLoginUserId%>'!='<%=lsTaskAssignedToUserId%>') )
	{
		document.getElementById("finish").disabled= true;
		document.getElementById("finishtaskchild").disabled= true;
		document.getElementById("publicCommentArea").disabled= true;
		document.getElementById("internalCommentArea").disabled= true;
		document.getElementById("save").disabled= true;
	  	  
	}
/**
 * Display error message if any exception occured on page.
 **/	 
	if('<%=lsExceptionMsg%>'!= "null")
	{
		 document.getElementById('error').innerHTML = '<%=lsExceptionMsg%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('error', this)\" />";
		 var element = document.getElementById('error');
		 element.style.display ="block";
	 
	}
	$(".emptyClass li a").click(function(e) {
        
        var $self=$(this);
        var $form = $("#reassign").closest('form');
        
      
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
 *Submit the form and pass required values as parameter
 **/
	function submitForm(taskid,isTaskLock,isManagerTask,lsStatus){
		document.myform.action=document.myform.action+'&taskid='+taskid+'&isTaskLock='+isTaskLock+'&isManagerTask='+isManagerTask+'&taskStatus='+lsStatus+'&fromTaskManagementPage='+<%=lsFromTaskManagementPage%>+'&fromBRAppPage=true';
		document.myform.submit();
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
                	//Start R5: UX module, clean AutoSave Data
                	 deleteAutoSaveData();
                	 //End R5: UX module, clean AutoSave Data  
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
/**
 * Submit form on click on finish button or display error message if no provider comments are entered in some condition..
 **/
	function finishCall(){
	 document.getElementById("finish").disabled= true;
		if('<%=lbSecurityCheck%>'){
		var newProviderComments= document.getElementById('publicCommentArea').value;
		newProviderComments=newProviderComments.trim();
		if(document.getElementById('finishtaskchild').value=='Rejected'){
				if(newProviderComments=='' || newProviderComments=='<%=lsIntialCommentValue%>')
				{
						document.getElementById('error').innerHTML = '<%=lsM40%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('error', this)\" />";
						$(".error").addClass('failed');
						var element = document.getElementById('error');
						element.style.display ="block";
				}
				else
				{
					pageGreyOut();
					document.myform.action=document.myform.action+'&next_action=finishchild';
					document.myform.submit();
				}
				}
		else
		{
			pageGreyOut();
			document.myform.action=document.myform.action+'&next_action=finishchild';
			document.myform.submit();
		}
	}
	
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
				document.myform.action=document.myform.action+'&reassignTask=reassignParentTask'+'&next_action=assignTask';
				document.myform.submit();
			}
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

	function setChangeFlag(){
				ischange = true;
			}

</script>
</body>
</html>
