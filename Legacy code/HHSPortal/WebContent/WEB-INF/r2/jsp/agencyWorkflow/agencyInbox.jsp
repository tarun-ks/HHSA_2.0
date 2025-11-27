<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.nyc.hhs.model.TaskQueue"%>
<%@page import="com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.nyc.hhs.util.PropertyLoader"%>
<%@page import="com.nyc.hhs.service.filenetmanager.p8constants.P8Constants"%>
<%@page import="com.nyc.hhs.constants.HHSConstants"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript"
	src="${pageContext.servletContext.contextPath}/r2/js/agencyInbox.js"></script>
<script type="text/javascript" src="../resources/js/calendar.js"></script>

<%-- 
This jsp is used for Agency Inbox service.
--%>
<portlet:defineObjects/>
<%-- Body Wrapper Start --%>
<%--resourceURL for typeheads--%>
<portlet:resourceURL var="fetchTypeAheadNameList" id="fetchTypeAheadNameList" escapeXml="false"/>

<%-- This portlet action is for fetch inbox task mapping --%>
<portlet:actionURL var="agencyInboxAction" id="agencyInboxAction"/> 
	<input type = "hidden" value='${fetchTypeAheadNameList}' id="hiddenFetchTypeAheadNameList" />
	<form name="myinboxform" id="myinboxform" action="<portlet:actionURL/>" method ="post" >
	<%-- R5 added for Default user assignment --%>
<portlet:resourceURL var="UpdateDefaultAssignee" id="UpdateDefaultAssignee"></portlet:resourceURL>
<input type="hidden" id="UpdateDefaultAssignee" value="${UpdateDefaultAssignee}"/>

<portlet:resourceURL var="setDefaultUser" id="setDefaultUser"></portlet:resourceURL>
<input type="hidden" id="setDefaultUser" value="${setDefaultUser}"/>

<portlet:resourceURL var="getReassignListFinance" id="getReassignListFinanceInbox"/>
<input type="hidden" id="getReassignListFinanceHidden" value="${getReassignListFinance}" />
<%-- added for r5 getContractSharedList --%>
<div class="overlay"></div>
<div class="alert-box alert-box-getDefaultAssignee">
<a href="javascript:void(0);" class="exit-panel upload-exit" title="Exit">&nbsp;</a>
<%@include file="/WEB-INF/r2/defaultTaskAssignment.jsp" %>
</div>
	<%-- Body Container Starts --%>
	<h2>Task Inbox </h2>	
	<c:if test="${message ne null}">
		<div class="${messageType}" id="messagediv" style="display:block">${message} <img
			src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
			class="message-close" onclick="showMe('messagediv', this)">
		</div>
	</c:if>
	<c:choose>
		<c:when test="${filterchecked eq 'display:block'}">
			<div id=unfiltered>Below is the list of selected tasks currently assigned to you. Use the filter to view other task types.</div>
		</c:when>
	</c:choose>
	
	<div class="hr"></div>
	
		<%--Filter and Reassign section starts --%>
		<div class="tasktopfilter">
			<div class="taskfilter taskButtons">
				<span>
					<input type="button" value="Filter Tasks" class="filterDocument marginReset"  onclick="javascript:setVisibility('documentValuePop', 'inline');" />
				</span>
					<input type="hidden" id="dropDownValuePrevtasktype" value="${loFilterToBeRetained.taskName}"/>
					<input type="hidden" id="dropDownValuePrevstatus" value="${loFilterToBeRetained.status}"/>
					<input type="hidden" id="dropDownValuePrevprogramname" value="${loFilterToBeRetained.programName}"/>
				
					<c:choose>
						<c:when test="${filterchecked eq 'display:block'}">
							Tasks: ${TotalTask}
						</c:when>
						<c:otherwise>
							You must filter on a "Task Type" to view any tasks.
						</c:otherwise>
					</c:choose>
				
				<%-- Popup for Filter Task Starts --%>
				<div id="documentValuePop" class='formcontainerFinance'>
					<div class='close'><a href="javascript:setVisibility('documentValuePop', 'none');" >X</a></div>
					
					<div class='row'>
						<span class='label'>Task Type:</span>
						<span class='formfield'>
							<select name="tasktype" class="widthFull" id="tasktype" onchange="enableFilter();">
								<option value=" "> </option>
								${taskTypes}
						  	</select>
						</span>
					</div>
					<div style="display:none">
						<c:forEach var="entry" items="${statusMap}">
						  <input type="hidden" class="hiddenStatusMap" key="${entry.key}" value="${entry.value}"/>
						</c:forEach>
					</div>
					<div class='row'>
						<span class='label'>Status:</span>
						<span class='formfield'>	
							<select disabled="disabled" name="status" class="widthFull" id="status">
							  </select>
						</span>
					</div>
					<div class='row'>
						<span class='label'>Program Name:</span>
						<span class='formfield'>
							<select name="programname" class="widthFull" id="programname" >
							<option value=""> </option>
									<c:forEach var="category" items="${programList}" >										
											<option title="<c:out value="${category.programName}"/>" value="<c:out value="${category.programName}"/>"><c:out value="${category.programName}"/></option>
									</c:forEach>
							  </select>
						</span>
					</div>
				
					<div class='row'>
						<span class='label'>Procurement/<br />Contract Title:</span>
						<span class='formfield'>
							<input type="text" name="procurementTitle" class="widthFull" id="procurementTitle" maxlength="120" onkeyup="setMaxLength(this,120)" onkeypress="setMaxLength(this,120)" value="${loFilterToBeRetained.procurementTitle}"/>
							<input type="hidden" class="input" name="procurementId" id="procurementId" value="${loFilterToBeRetained.procurementId}"/>
						</span>
						<span class="error"></span>
					</div>
					<%--code updation for R4 starts--%>
			        <div class='row'>
			            <span class='label'>Competition Pool:</span>
			            <span class='formfield'>
							<input type="text" name="competitionPoolTitle" class="widthFull" id="competitionPoolTitle" maxlength="120" onkeyup="setMaxLength(this,120)" onkeypress="setMaxLength(this,120)" value="${loFilterToBeRetained.competitionPoolTitle}"/>
							<input type="hidden" class="input" name="competitionPoolId" id="competitionPoolId" value="${loFilterToBeRetained.competitionPoolId}"/>
			            </span>
			            <span class="error"></span>
			        </div>
			        					<%--code updation for R4 ends--%>
					<div class='row'>
						<span class='label'>Provider Name:</span>
						<span class='formfield'>
						<!-- [Start] R7.3.0 QC9016: Type Ahead increase provider name size in drop-down-->
							<input type="text" class="proposalConfigDrpdwn" id="providername" name="providername" onkeyup="setMaxLength(this,200)" onkeypress="setMaxLength(this,200)" value="${loFilterToBeRetained.providerName}"/>
						<!-- [End] R7.3.0 QC9016: Type Ahead increase provider name size in drop-down-->
						</span>
					</div>
				
					<div class='row'>
						<span class='label'>Date Submitted from:</span>
						<span class='formfield'>
							<span class='floatLft'>
								<input type="text" class='datepicker' name="datefrom" id="datefrom" validate="calender" maxlength="10" value="${loFilterToBeRetained.submittedFromDate}"/><img src="../framework/skins/hhsa/images/calender.png" onclick="NewCssCal('datefrom',event,'mmddyyyy');return false;"/> 
							</span>
							<span class="error clear ieError7"></span>
								<b> &nbsp;&nbsp;to:&nbsp; </b>
							<span>
									<input type="text" class='datepicker' name="dateto" id="dateto" validate="calender" maxlength="10" value="${loFilterToBeRetained.submittedToDate}"/><img src="../framework/skins/hhsa/images/calender.png" onclick="NewCssCal('dateto',event,'mmddyyyy');return false;"/>
							</span>
							<span class="error clear ieError7"></span>
						</span>
					</div>
					<div class='row'>
						<span class='label'>Last Assigned Date from:</span>
						<span class='formfield'>
							<span class='floatLft'>
								<input type="text" class='datepicker' name="dateassignedfrom" id='dateassignedfrom' validate="calender" maxlength="10" value="${loFilterToBeRetained.assignedFromDate}"/><img src="../framework/skins/hhsa/images/calender.png" onclick="NewCssCal('dateassignedfrom',event,'mmddyyyy');return false;"/>
							</span>
							<span class="error clear ieError7"></span>
							<b> &nbsp;&nbsp;to:&nbsp; </b>
							<span>
								<input type="text" class='datepicker' name="dateassignedto" id="dateassignedto" validate="calender" maxlength="10" value="${loFilterToBeRetained.assignedToDate}"/><img src="../framework/skins/hhsa/images/calender.png" onclick="NewCssCal('dateassignedto',event,'mmddyyyy');return false;"/>
							</span>
							<span class="error clear ieError7"></span>
						</span>
					</div>
					<div class="buttonholder">
						<input type="button" class="graybtutton" value="Clear Filters" onclick="clearfilter();" />
						<input type="button" value="Filter" name="filter" id='filtersBtn' disabled="disabled" onclick='filtertask()'/>
					</div> 
				</div>
				<%-- Popup for Filter Task Ends --%>
			</div>
			<c:choose>
					<c:when test="${filterchecked eq 'display:block'}">
				<div class="taskreassign" id='reassigndiv' style='${filterchecked}'>
					<input type="hidden" name="reassigntouserText" value="" id="reassigntouserText" />
					Reassign Selected Tasks to: 
					<select name="reassigntouser" id="reassigntouser" disabled="disabled" class="input" onchange="enableSubmit();">
				        <option ></option>
						<c:forEach items="${userMap}" var="user">
				      			<option value="${user.key}">${user.value}</option>
				   		</c:forEach>
					</select>
					<input type="button" id="reassignId" value="Assign" disabled class="button" onclick='ressignCall()'/>
				</div>
				</c:when>
			</c:choose>
		</div>
		<%--Filter and Reassign section ends --%>
		<c:if test="${TotalTask > 0}">
			<div class="" id='griddiv' style='${filterchecked}'>
				 <%-- Grid Starts --%>
				 <div class="tabularWrapper">
				 <st:table objectName="agencyTaskItemList" pageSize="${allowedObjectCount}" cssClass="heading"
					alternateCss1="evenRows" alternateCss2="oddRows" >
					<st:property headingName="Select" columnName="wobNumber" align="center"
						size="3%" >
						<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.TaskCheckBox" />
					</st:property>
					<st:property headingName="Task Name"  columnName="taskName" sortType="taskName" sortValue="asc" align="center"
						size="22%" >		
						<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.AgencyTaskExtention" />
					</st:property>
					<st:property headingName="Procurement/Contract Title" columnName="procurementTitle" sortType="procurementTitle" sortValue="asc"
						align="left" size="15%"  />
					
					<%-- Start || Changes done for enahncement 6636 for Release 3.12.0 --%>	 
				    <c:choose>
					<c:when test="${(loFilterToBeRetained.taskName eq 'Evaluate Proposal') or (loFilterToBeRetained.taskName eq 'Review Scores')}">  
 						<st:property  headingName="Proposal Id" columnName="proposalId" sortType="proposalId" sortValue="asc" align="right" size="9%" />
					</c:when>
				    </c:choose> 
			        <%-- End || Changes done for enahncement 6636 for Release 3.12.0 --%>
					
					<st:property headingName="Provider Name" columnName="providerName" sortType="providerName" sortValue="asc"
						align="left" size="15%"  />

                    <%-- [Start: Add column only for Invoice Review -- R6.1.0 enhancement #8665 and QC6522] --%>			 
	 				<c:choose>
						<c:when test="${loFilterToBeRetained.taskName eq 'Invoice Review'}">   
	 						<st:property  headingName="Service Start Date" columnName="serviceStartDate" sortValue="asc" align="right" size="9%" />
							<st:property  headingName="Service End Date"   columnName="serviceEndDate"   align="right" size="9%" />
						</c:when>
					</c:choose> 
                    <%-- [End: Add column only for Invoice Review -- R6.1.0 enhancement #8665 and QC6522 ] --%>

					<st:property headingName="Date Submitted" columnName="dateCreated" sortType="dateCreated" sortValue="asc"
						align="right" size="15%" />
					<st:property headingName="Last Assigned Date" columnName="lastAssigned" sortType="lastAssigned" sortValue="desc"
						align="right"  size="15%" />
					<st:property headingName="Status" columnName="status" sortType="status" sortValue="asc"
						align="right" size="10%" />
				</st:table>
				</div>
	            <%-- Grid Ends --%>
			</div>
		</c:if>
		<%-- Container Ends --%>
		<%-- added for defaultassignee --%>
				<input type="hidden" id="taskLevel" name="taskLevel"/>
		<input type="hidden" value="" id="assigneeUserId" name="assigneeUserId"/>
		<input type="hidden" value="" id="assigneeTaskType" name="assigneeTaskType"/>
		<input type="hidden" value="" id="assigneeTaskLevel" name="assigneeTaskLevel"/>
</form>

<style type="text/css">
	.alert-box-contact {
		background:#FFF;
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
</style>


