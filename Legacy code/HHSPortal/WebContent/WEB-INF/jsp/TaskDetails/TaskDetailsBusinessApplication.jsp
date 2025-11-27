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
<portlet:defineObjects/>
<script type="text/javascript" src="../framework/skeletons/hhsa/js/TaskDetailsBusinessApplication.js"></script>

<jsp:useBean
	id="filenetWorkItemDetails" class="com.nyc.hhs.model.Task" scope="request"></jsp:useBean>
<% 

	String lsLoginUserId=""; 
	String lsTaskAssignedToUserId="";
	boolean lsManagerRole=false;
		if(request.getAttribute("managerRole")!=null){
		lsManagerRole=(Boolean)request.getAttribute("managerRole");
	}

	if(request.getAttribute("loginUserID")!=null){
		lsLoginUserId=(String)request.getAttribute("loginUserID");
	}
	if(request.getAttribute("lsTaskAssignedToUserId")!=null){
		lsTaskAssignedToUserId=(String)request.getAttribute("lsTaskAssignedToUserId");
	}
	boolean lbSecurityCheck=CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.TA_S079_PAGE, request.getSession());

	boolean lbIsValidUser=true;
	
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
	else if(lsStatus.equals(ApplicationConstants.STATUS_IN_REVIEW)||lsStatus.trim().equals("")){
		lsFinish="false";
	}
	String lsExceptionMsg=(String)request.getAttribute(ApplicationConstants.ERROR_MESSAGE);
	if(lsExceptionMsg==null)
	{
		lsExceptionMsg="null";
	}

	String lsReadOnlyPage = (String)request.getParameter("isTaskLock");
	boolean isTaskLocked = false;
	if(request.getAttribute("isTaskLocked")!=null){
	isTaskLocked=(Boolean)request.getAttribute("isTaskLocked");
	}
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
	String lsP05=PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE,"P05");
%>
<%
	String taskId = (String)request.getAttribute("taskId");
	String appId = (String)request.getAttribute("appId");
%>
<body>
<!-- Body Container Starts -->
<form name="myform" action="<portlet:actionURL/>" method ="post" >

	<h2>Task Details: <span><%=filenetWorkItemDetails.getTaskName()%></span></h2>
	
	<div class="linkReturnVault"><a href="javascript:;" title="Return" onclick="javascript:returnInbox('<%=taskId%>')">Return</a></div>

	<div id="error" class='error' style="display:none"></div>
	<%if(lbSecurityCheck){%>
		<div class="container">
		<div class="complianceWrapper">
		<%
		String lsManagerTask= (String)request.getParameter("isManagerTask");
		 %>
		<!--Filter and Reassign section starts -->
	    <div class="tasktopfilter">
	        <div class="taskfilter tasksize">
		        <input type="hidden" name="reassigntouserText" value="" id="reassigntouserText" />
		        <select id="userList" style="min-width: 136px;max-width: 136px" onchange="checkUserTask('<%=lsManagerTask%>')">
			          <option selected="selected"></option>
					<c:forEach items="${userMap}" var="user">
		       			<option value="${user.key}">${user.value}</option>
		    		</c:forEach>           
		        </select>
		        <input type="button" class="graybtutton" id="reassign" value="Reassign Task" title="Reassign Task" onclick="javascript:reassignTask()" />
		        
		        <% if(!(filenetWorkItemDetails.getAssignedTo().equalsIgnoreCase("Unassigned")||filenetWorkItemDetails.getAssignedTo().equalsIgnoreCase("Unassigned - Manager")) &&lsManagerRole){%>
		        <input type="button" class="graybtutton" id="suspend" value="Suspend Application" title="Suspend Application" />
		        <%}%>
		        
			</div>
	        <div class="taskreassign">
	        	<% if((lsFinish.equals("true")||lsStatus.equals(ApplicationConstants.STATUS_IN_REVIEW))&&childSize>0){%>
	       		<b> Business Application Status: </b><%=filenetWorkItemDetails.getMsProcessStatus()%> <%} %>
	          	<input type="button" class="button" id="finish" value="Finish Task" title="Finish Task" onclick="javascript:finishTask()"/>
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
			<h4>Application Details</h4>
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
			
			<label>Number of Services:</label>
			<div><%=filenetWorkItemDetails.getNoOfServices()%></div>
			
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
		<div class='Column2'>
		
		<div class="hr"></div>
		
		
		<!-- Organization Details Section Start -->
		<div class="formcontainer">
			<!-- Grid Starts -->			 
			<div  class="tabularWrapper">
				<% if(childSize>0){%>
					 <st:table objectName="taskItemChildList" gridName="childtasksgrid" cssClass="heading"
						alternateCss1="evenRows" alternateCss2="oddRows">
						<st:property headingName="Task"  columnName="msTaskName" align="center"
							size="35%" >		
							<st:extension decoratorClass="com.nyc.hhs.frameworks.sessiongrid.TaskIDExtension" />
						</st:property>
						<st:property headingName="Assigned To" columnName="msAssignedTo"
							align="right" size="20%" />
						<st:property headingName="Last Assigned" columnName="moLastAssigned"
							align="right" size="30%" />
						<st:property headingName="Status" columnName="msStatus"
							align="right" size="15%" />
					</st:table>
				<%} %>
			</div>
			<!-- Grid Ends -->


		</div>
		<script>
			onload('<portlet:actionURL/>');
		</script>

		<div>&nbsp;</div>
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
						size="25%" />
					
					<st:property headingName="Action"  columnName="msEventname" align="center"
						size="15%" />		
					<st:property headingName="Detail" columnName="msData"
						align="right" size="30%" />
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
	<div id="linkDiv">
		<script type="text/javascript"></script>
	</div>
</form>

<style type="text/css">
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
	.alert-box-suspend{
	background: none repeat scroll 0 0 #FFFFFF;
    display: none;
    z-index: 1001;
}
</style>

<script type="text/javascript">
var ischange = false;
var taskdetailAction = document.myform.action;
/**
 * This Method called on Page loading.It enable or disable buttons and display error or sucess message on based of conditions.
 **/	
	$(document).ready(function() {
		var lastData = null;
		document.getElementById("reassign").disabled= true;
		 if("null" != '<%= request.getAttribute("messagecom")%>'){
			 $(".error").html('<%= request.getAttribute("messagecom")%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('error', this)\" />");
			 $(".error").addClass('passed');
			 $(".error").show();
			 <%request.removeAttribute("messagecom");%>	
			}
		$("a.exit-panel").click(function(){
		 		document.myform.action = taskdetailAction;
		});

			$('#suspend').click(function() {
				pageGreyOut();
			    suspendCall(document.myform);
			 	var options = 
		    	{	
					success: function(responseText, statusText, xhr ) 
				{
				var $response=$(responseText);
			    var data = $response.contents().find(".overlaycontent");
			   		$("#suspendTask").empty();
				if(data != null || data != ''){
			    	$("#suspendTask").html(data.detach());
				}
				var overlayLaunchedTemp = overlayLaunched;
				var alertboxLaunchedTemp = alertboxLaunched;
				$("#overlayedJSPContent").html($response);
				overlayLaunched = overlayLaunchedTemp;
				alertboxLaunched = alertboxLaunchedTemp;
				$(".overlay").launchOverlay($(".alert-box-suspend"), $(".exit-panel"), "600px", null, "onReady");
				$('ul').removeClass('ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all ui-state-hover ui-tabs-nav');
				$('li').removeClass('ui-state-default ui-corner-top ui-state-hover');
				removePageGreyOut();
				},
				error:function (xhr, ajaxOptions, thrownError)
				{                     
					showErrorMessagePopup();
					removePageGreyOut();
				}
				};
				$(document.myform).ajaxSubmit(options);
				return false;
			});
		
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
			
	/**
	 *  Enable  finish button if finish flag is true else disable.
	 **/
		if('<%=lsFinish%>'=="true"){ 
				document.getElementById("finish").disabled= false;
		}
		else{
			document.getElementById("finish").disabled= true;
		}
/**
 * If user is not valid user display error message.
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
		 if('<%=lsReadOnlyPage%>'=="true" || '<%=isTaskLocked%>'=="true")
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
		    document.getElementById("publicCommentArea").disabled= true;
		    document.getElementById("internalCommentArea").disabled= true;
		    document.getElementById("save").disabled= true;
		    <% if(!(filenetWorkItemDetails.getAssignedTo().equalsIgnoreCase("Unassigned")||filenetWorkItemDetails.getAssignedTo().equalsIgnoreCase("Unassigned - Manager")) &&lsManagerRole){%>
		    document.getElementById("suspend").disabled= true;
	        <%}%>
		 }
/**
 * Display error message if any exception occured on page.
 **/
		 if('<%=lsExceptionMsg%>'!= "null")
		 {
			 document.getElementById('error').innerHTML = '<%=lsExceptionMsg%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('error', this)\" />";
			 $(".error").addClass('failed');
			 var element = document.getElementById('error');
			 element.style.display ="block";
		 }
		});

</script>
<script type="text/javascript">
/**
 *Submit the form and pass required values as parameter
 **/
	function submitForm(taskid,isTaskLock,isManagerTask,lsStatus){
		if(ischange){
			 $('<div id="dialogBox"></div>').appendTo('body')
				.html('<div><h6>You have unsaved data. <br />If you would like to leave this screen without saving your data, click <b>OK</b>. <br/>If you would like to save your data, click <b>Cancel</b> and save your data.</h6></div>')
           .dialog({
                 modal: true, title: 'Unsaved Data', zIndex: 10000, autoOpen: true,
                 width: 'auto', modal: true, resizable: false,draggable:false,
                 dialogClass: 'dialogButtons',
                 buttons: {
                       OK: function () {
                    	       
                    		document.myform.action=document.myform.action+'&taskid='+taskid+'&isTaskLock='+isTaskLock+'&isManagerTask='+isManagerTask+'&taskStatus='+lsStatus+'&fromTaskManagementPage='+'<%=lsFromTaskManagementPage%>'+'&fromBRAppPage=true';
                			document.myform.submit();
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
		}else{
			document.myform.action=document.myform.action+'&taskid='+taskid+'&isTaskLock='+isTaskLock+'&isManagerTask='+isManagerTask+'&taskStatus='+lsStatus+'&fromTaskManagementPage='+'<%=lsFromTaskManagementPage%>'+'&fromBRAppPage=true';
			document.myform.submit();
		}
	
		}
		
		function setChangeFlag(){
			ischange = true;
		}
/**
 * Submit form on click on finish button or display error message if no provider comments are entered.
 **/
	function finishTask(){
	 var currentStatus='<%=filenetWorkItemDetails.getMsProcessStatus()%>';
		if((<%=lbSecurityCheck%>) && (currentStatus!='In Review') && !(<%=isTaskLocked%>) ){
			var newProviderComments= document.getElementById('publicCommentArea').value;
			newProviderComments=newProviderComments.trim();
			
			
			if(currentStatus=='<%=ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS%>' || currentStatus=='<%=ApplicationConstants.STATUS_REJECTED%>'||currentStatus=='<%=ApplicationConstants.STATUS_DEFFERED%>'){
			
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
						document.myform.action=document.myform.action+'&next_action=taskFinishedParent';
						document.myform.submit();
					}
					}
			else
			{
				pageGreyOut();
				document.myform.action=document.myform.action+'&next_action=taskFinishedParent';
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
	/**
	 * Submit form on click on Suspend button.
	 **/
	function suspendCall(form){
		
		if('<%=lbSecurityCheck%>'){
				pageGreyOut();
				form.action="<portlet:renderURL/>"+'&SuspendAction=Suspended'+'&next_action=forcefullySuspend';
		}
	};
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
</script>
<div class="overlay"></div>
	<div class="alert-box-suspend">
		<div class="content">
			<div id="newTabs" class='wizardTabs'>
				<div class="tabularCustomHead">Suspend</div>
		        <div id="suspendTask"></div>
			</div>
		</div>
		<a  href="javascript:void(0);" class="exit-panel" title="Exit">&nbsp;</a>
	</div>
	<div id="overlayedJSPContent" style="display:none"></div>
</body>

