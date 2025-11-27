<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.nyc.hhs.model.TaskQueue"%>
<%@page import="com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.nyc.hhs.util.PropertyLoader"%>
<%@page import="com.nyc.hhs.service.filenetmanager.p8constants.P8Constants"%>
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ page import="com.nyc.hhs.frameworks.sessiongrid.*"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib prefix="st" uri="/WEB-INF/tld/sessiongrid-taglib.tld"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript" src="../framework/skeletons/hhsa/js/inbox.js"></script>
<script type="text/javascript" src="../resources/js/calendar.js"></script>

<portlet:defineObjects/>
<%--resourceURL for typeheads Begin QC 5446 --%>
<portlet:resourceURL var="fetchTypeAheadNameList" id="fetchTypeAheadNameList" escapeXml="false">
	<portlet:param name="fetchTypeAhead" value="true" />
</portlet:resourceURL>
<%--End QC 5446 --%>

<%
	HashMap FilterDetails=new HashMap();
	String TaskTypeFilter="";
	String ProviderNameFilter="";
	/*****  Begin QC 5446 ****/
	String ProcurementTitleFilter="";
	String CompetitionPoolTitleFilter="";
	String AgencyNameFilter="";
	/*****  End QC 5446 ****/
	String StatusFilter="";
	String SubmittedFromFilter="";
	String SubmittedToFilter="";
	String DateAssignedFromFilter="";
	String DateAssignedToFilter="";
	String AssignedToFilter="";
	String lsLoginUserId=""; 
	String lsM39 = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE, "M39");
	String lsMPE1 = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE, "MPE1");
	String lsM29 = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE, "M29");
	if(request.getAttribute("loginUserID")!=null){
	lsLoginUserId=(String)request.getAttribute("loginUserID");
	}
	
	String displayblock=null;
	if(request.getAttribute("filterchecked")!=null)
	{
		displayblock=(String)request.getAttribute("filterchecked");
	}
	
	if(request.getAttribute("loFilterToBeRetained")!=null){
		FilterDetails=(HashMap)request.getAttribute("loFilterToBeRetained");
	}

	if(FilterDetails!=null && !FilterDetails.isEmpty()){
		 if(null!=(String)FilterDetails.get("TaskType"))
				TaskTypeFilter = (String)FilterDetails.get("TaskType");
			
			if(null!=(String)FilterDetails.get("ProviderName"))
			 ProviderNameFilter = (String)FilterDetails.get("ProviderName");
			
			/*****  Begin QC 5446 ****/
			if(null!=(String)FilterDetails.get("ProcurementTitle"))
				 ProcurementTitleFilter = (String)FilterDetails.get("ProcurementTitle");
			
			if(null!=(String)FilterDetails.get("CompetitionPoolTitle"))
				 CompetitionPoolTitleFilter = (String)FilterDetails.get("CompetitionPoolTitle");
			
			if(null!=(String)FilterDetails.get("agencyName"))
				 AgencyNameFilter = (String)FilterDetails.get("agencyName");
			/*****  End QC 5446 ****/
			
			if(null!=(String)FilterDetails.get("TaskStatus"))
			 StatusFilter = (String)FilterDetails.get("TaskStatus");
			
			if(null!=(String)FilterDetails.get("SubmittedFrom"))
			 SubmittedFromFilter = (String)FilterDetails.get("SubmittedFrom");
			
			if(null!=(String)FilterDetails.get("SubmittedTo"))
			 SubmittedToFilter = (String)FilterDetails.get("SubmittedTo");
			
			if(null!=(String)FilterDetails.get("AssignedFrom"))
			 DateAssignedFromFilter = (String)FilterDetails.get("AssignedFrom");
			
			if(null!=(String)FilterDetails.get("AssignedTo"))
			 DateAssignedToFilter = (String)FilterDetails.get("AssignedTo");
		
	}
%>

<%	
	String totalTasks=null;
	String PageSize=null;
	String appId=null;
	int sizeofpage=0;
	if(request.getAttribute("TotalTask")!=null){
		totalTasks =(String)request.getAttribute("TotalTask");
	}
	
	if(request.getAttribute("PageSize")!=null)
	{
	 	PageSize =(String)request.getAttribute("PageSize");
	}
	
	if(PageSize!=null)
	{
		sizeofpage=Integer.parseInt(PageSize);
	}
	
	if(request.getAttribute("appId")!=null){
		appId=(String)request.getAttribute("appId");
	}
	
%>
<!-- Body Wrapper Start -->
	<form name="myinboxform" id="myinboxform" action="<portlet:actionURL/>" method ="post" >
	
	<!-- **** Begin QC5446 -->
	<input type = "hidden" value='${fetchTypeAheadNameList}' id="hiddenFetchTypeAheadNameList" />
	<!-- **** Begin QC5446 -->

	<!-- Body Container Starts -->
	<h2>Task Inbox </h2>	
	
	<div id="error" class="clear error" ></div>
	
	<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.TA_S077_PAGE, request.getSession())){%>

	<div id=unfiltered>Listed below are the tasks that are assigned to you with the applied filters.</div>
	
	<div class="hr"></div>
	<!-- Container Starts -->
	<div class="container">
		<!--Filter and Reassign section starts -->
		<div class="tasktopfilter">
			<div class="taskfilter taskButtons">
				<input type="button" title="Filter Tasks" value="Filter Tasks" class="filterDocument"  onclick="javascript:setVisibility('documentValuePop', 'inline');" />
			
				<!-- Popup for Filter Task Starts -->
				<div id="documentValuePop" class='formcontainer'>
					<div class='close'><a href="javascript:setVisibility('documentValuePop', 'none');" >X</a></div>
					
					<div class='row'>
						<span class='label'>Task Type:</span>
						<span class='formfield'>
							<select name="tasktype" id="tasktype" onchange="enableFilter();">
									<c:forEach var="category" items="${workItemInbox.taskTypeList}" >										
											<option value="<c:out value="${category}"/>"><c:out value="${category}"/></option>
									</c:forEach>
							  </select>
						</span>
					</div>
					<div class='row'>
						<span class='label'>Provider Name:</span>
						<span class='formfield'>
							<input type="text" id="providername" name="providername" onkeyup="setMaxLength(this,60)" onkeypress="setMaxLength(this,60)"/>
						</span>
					</div>
					
					<!-- **** Begin QC5446 ****-->
					<div class='row'>
						<span class='label'>Procurement Title:</span>
						<span class='formfield'>
							<input type="text" name="ProcurementTitle" class="widthFull" id="ProcurementTitle" maxlength="120" onkeyup="setMaxLength(this,120)" onkeypress="setMaxLength(this,120)" value="${loFilterToBeRetained.ProcurementTitle}"/>
							<input type="hidden" class="input" name="procurementId" id="procurementId" value="${loFilterToBeRetained.procurementId}"/>
						</span>
						<span class="error"></span>
					</div>
					<div class='row'>
			            <span class='label'>Competition Pool:</span>
			            <span class='formfield'>
							<input type="text" name="CompetitionPoolTitle" class="widthFull" id="CompetitionPoolTitle" maxlength="120" onkeyup="setMaxLength(this,120)" onkeypress="setMaxLength(this,120)" value="${loFilterToBeRetained.CompetitionPoolTitle}"/>
							<input type="hidden" class="input" name="competitionPoolId" id="competitionPoolId" value="${loFilterToBeRetained.competitionPoolId}"/>
			            </span>
			            <span class="error"></span>
			        </div>
			        <div class='row'>
						<span class='label'>Agency:</span>
						<span class='formfield'>
							<select id="agencyName" name="agencyName" class="widthFull">
									<option value="" title=""></option>
										<c:set var="loagencySettingsBean" value="${agencySettingsBean}"></c:set>
										<c:forEach var="listItems"
												items="${loagencySettingsBean.allAgencyDetailsBeanList}">
										<option value="${listItems.agencyId}"
											title="${listItems.agencyName}">${listItems.agencyName}</option>
										</c:forEach>
							</select>
						</span>
					</div>
					<!-- **** End QC5446 ****-->
					
					<div class='row'>
						<span class='label'>Status:</span>
						<span class='formfield'>
							<select name="status" id="status">
								<c:forEach var="category" items="${workItemInbox.statusList}" >										
											<option value="<c:out value="${category}"/>"><c:out value="${category}"/></option>
									</c:forEach>
							  </select>
						</span>
					</div>
					<div class='row'>
						<span class='label'>Date Submitted from:</span>
						<span>
							<input type="text" style='width:78px;' name="datefrom" id="datefrom" validate="calender" maxlength="10"/><img src="../framework/skins/hhsa/images/calender.png" title="Submitted From Date" onclick="NewCssCal('datefrom',event,'mmddyyyy');return false;"/> 
						</span>
						<span class="error2"></span>
						<span>
								&nbsp;&nbsp;to:<input type="text" style='width:78px;' name="dateto" id="dateto" validate="calender" maxlength="10"/><img src="../framework/skins/hhsa/images/calender.png" title="Submitted To Date" onclick="NewCssCal('dateto',event,'mmddyyyy');return false;"/>
						</span>
						<span class="error2"></span>
					</div>
					<div class='row'>
						<span class='label'>Last Assigned from:</span>
						<span>
							<input type="text" style='width:78px;' name="dateassignedfrom" id='dateassignedfrom' validate="calender" maxlength="10"/><img src="../framework/skins/hhsa/images/calender.png" title="Assigned From Date" onclick="NewCssCal('dateassignedfrom',event,'mmddyyyy');return false;"/>
						</span>
						<span class="error2"></span>
						<span>
							&nbsp;&nbsp;to:<input type="text" style='width:78px;' name="dateassignedto" id="dateassignedto" validate="calender" maxlength="10"/><img src="../framework/skins/hhsa/images/calender.png" title="Assigned To Date" onclick="NewCssCal('dateassignedto',event,'mmddyyyy');return false;"/>
						</span>
						<span class="error2"></span>
					</div>
					<div class="buttonholder">
						<input type="button" class="graybtutton" value="Clear Filters" title="Clear Filters" onclick="clearfilter();" /><input type="button" value="Filter" name="filter" id='filtersBtn' title="Filter" onclick='filtertask()'/>
					</div> 
				</div>
				<!-- Popup for Filter Task Ends -->
			
				<%if(displayblock == "display:block"){%>
					Tasks:<%=totalTasks%>
				<%}else{%>
					You must filter on a "Task Type" to view any tasks.
				<%}%>
			</div>
			<%if(displayblock == "display:block"){%>	
				<div class="taskreassign" id='reassigndiv' style='<%=displayblock%>'>
					<input type="hidden" name="reassigntouserText" value="" id="reassigntouserText" />
					Reassign Selected Tasks to: 
					<select name="reassigntouser" id="reassigntouser" class="input">
				        <option ></option>
						<c:forEach items="${userMap}" var="user">
				      			<option value="${user.key}">${user.value}</option>
				   		</c:forEach>
					</select>
					<input type="button" id="reassignId" value="Reassign" class="button" title="Reassign" onclick='ressignCall()'/>
				</div>
			<%}%>
		</div>
		<!--Filter and Reassign section ends -->
		<%if(null!=totalTasks && Integer.parseInt(totalTasks)>0){%>
			<div class="" id='griddiv' style='<%=displayblock%>'>
			 <!-- Grid Starts -->			 
			 <div class="tabularWrapper">
			 <st:table gridName="taskinbox" objectName="taskItemList" pageSize="<%=sizeofpage%>" cssClass="heading"
				alternateCss1="evenRows" alternateCss2="oddRows" defaultSort="moLastAssigned" sortType="DESC">
				
				<st:property headingName="Select" columnName="msWobNumber" align="center"
					size="3%" >
					<st:extension decoratorClass="com.nyc.hhs.frameworks.sessiongrid.TaskCheckBox" />
				</st:property>
				<st:property headingName="Task Name"  columnName="msTaskName" align="center" sort="true"
					size="32%" >		
				<st:extension decoratorClass="com.nyc.hhs.frameworks.sessiongrid.TaskIDExtension" />
				</st:property>
				
				<!-- Begin QC 5446 Change Column name for 'Approve Award' -->
				<!-- R5 change - added OR condition -->
				<!-- Changes done in column sizes to fix allignments in R7(Defect 8698) -->
				<%if ("Approve Award".equalsIgnoreCase(TaskTypeFilter) || "Approve PSR".equalsIgnoreCase(TaskTypeFilter)
						|| "Approve Award Amount".equalsIgnoreCase(TaskTypeFilter)) {%>
							<st:property headingName="Procurement Title" columnName="msProcurementTitle"
								align="left" sort="true" size="20%"  /> 
				<% } if (!("Approve Award".equalsIgnoreCase(TaskTypeFilter) || "Approve PSR".equalsIgnoreCase(TaskTypeFilter))) {%>
							<st:property headingName="Provider Name" columnName="msProviderName"
								align="left" sort="true" size="17%"  />
				<% } %>
				<!-- *** End QC 5446 *** -->
				<!-- R7 Change-8698(created a new Column "BA Status" on the for the "Tasks In My Inbox" and "Tasks assigned to Staff) -->
				<!--R7.3 QC 9003 : add sort for BA Status  -->
				<% if ("Service Application".equalsIgnoreCase(TaskTypeFilter)){%>
				<st:property headingName="BA Status" columnName="msBaStatus"
					align="right" sort="true" size="10%"  />
				<% } %>	
				<st:property headingName="Date Submitted" columnName="moDateCreated"
					align="right" sort="true" size="19%" />
				<st:property headingName="Last Assigned Date" columnName="moLastAssigned"
					align="right"  sort="true" size="21%" />
				<st:property headingName="Status" columnName="msStatus"
					align="right" sort="true" size="18%" />
			</st:table>
			</div>
            <!-- Grid Ends -->
			
			</div>
		<%}%>
		<!-- Container Ends -->
		</div>
		<input type="hidden" name="appId" value="<%=appId%>"> 
		<script>
			onload('<portlet:actionURL/>');
		</script>
		
	<% } else {%>
		<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
	<%} %>
</form>

<style type="text/css">
	<!--
	.wlp-bighorn-layout-flow,.wlp-bighorn-layout-border-wce-container {
		width: 100%;
	}
	.wlp-bighorn-window-content {
		min-height: 550px;
		width: 100%;
		overflow: hidden !important;
	}
	.bodycontainer{
		width: auto !important;
	}
	.containerpanel {
	    background: none repeat scroll 0 0 #FFFFFF;
	    border: 1px solid #CDCDCD;
	    display: block;
	    overflow:visible !important;
	    padding: 5px;
	}
	.alert-box-contact {
		background: none repeat scroll 0 0 #FFFFFF;
		display: none;	
		position: fixed;
		margin-left: 11%;
		top: 25%;
		width: 54%;
		z-index: 1001;
	}
	.paginationWrapper{
		margin-top: 0;
	}
	.error2{
	color: #D63301;
	width:100%; 
	float:right;
	text-align: right
}
	-->
</style>
<script type="text/javascript">

/**
 * This Method called on Page loading.It enable or disable buttons and display error or sucess message on based of conditions.
 **/    
$(document).ready(function() {
	
	document.getElementById('tasktype').value="<%=TaskTypeFilter%>";
	document.getElementById('providername').value="<%=ProviderNameFilter%>";
	/******  Begin QC 5446  *****/
	document.getElementById('ProcurementTitle').value="<%=ProcurementTitleFilter%>";
	document.getElementById('CompetitionPoolTitle').value="<%=CompetitionPoolTitleFilter%>";
	document.getElementById('agencyName').value="<%=AgencyNameFilter%>";
	/******  End QC 5446  *****/
	document.getElementById('datefrom').value="<%=SubmittedFromFilter%>";
	document.getElementById('dateto').value="<%=SubmittedToFilter%>";
	document.getElementById('dateassignedfrom').value="<%=DateAssignedFromFilter%>";
	document.getElementById('dateassignedto').value="<%=DateAssignedToFilter%>";
	document.getElementById('status').value="<%=StatusFilter%>";
	enableFilter();
	if( 'null' !='<%=request.getAttribute("message")%>')
	{
	 	$(".error").html('<%=request.getAttribute("message")%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('error', this)\" />");
	 	$(".error").addClass('failed');
		$(".error").show();
	}
	 
	<%if(displayblock == "display:block"){%>
		document.getElementById("reassignId").disabled= true;
	<%}%>	
});

/**
 * Check if current Task is manager task and user is manager or not.If user is not manager display error message and disable Reassign button else submit the form.
 **/
function ressignCall(){
	var chks = document.getElementsByName('check');
	var tasknames = "";
    var ManagerError = "";
	var  selectedTaskarray = new Array();
	for (var i = 0; i <	 chks.length; i++)
	{
		if (chks[i].checked)
		{ 
			selectedTaskarray = taskArray[i].split("_");
			if(selectedTaskarray[1]=="true")
			{
				tasknames=tasknames+selectedTaskarray[3]+" ";
			}
			if(selectedTaskarray[2]=="true")
			{
				if(document.getElementById("reassigntouser").value.split("|")[0]!="manager")
				{
					ManagerError=ManagerError+selectedTaskarray[3]+" ";
				}
			}
		}
    }
    if(tasknames!=""){
   			     			      
      	    $(".error").html('<%=lsMPE1%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('error', this)\" />");
		 	$(".error").addClass('failed');
			$(".error").show();
      	 									      
    } 
    else if(ManagerError!="")
    {
     	 $(".error").html('<%=lsM39%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('error', this)\" />");
		 	$(".error").addClass('failed');
			$(".error").show();
     	
    }
    else if('<%=lsLoginUserId%>'==document.getElementById("reassigntouser").value.split('|')[1])
    {
      	    $(".error").html('<%=lsM29%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('error', this)\" />");
		 	$(".error").addClass('failed');
			$(".error").show();
    }
    else{
   		pageGreyOut();
    	var selectvalue = document.getElementById("reassigntouser").value.split('|')[1];
	   	document.myinboxform.action=document.myinboxform.action+'&next_action=assignTask';
	   	document.getElementById('reassigntouserText').value=selectvalue;
	   	document.myinboxform.submit();
    }
}
/**
 * It shows message on page.
 **/              
function showMe (it, box) {
	if(box.id=='box'){
		vis = "none";
	}else{
		vis = "block";
	}
	document.getElementById(it).style.display = vis;
} 
</script>

<!-- Body Container Ends -->
